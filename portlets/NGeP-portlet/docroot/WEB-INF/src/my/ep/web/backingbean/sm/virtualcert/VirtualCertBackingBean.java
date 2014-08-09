package my.ep.web.backingbean.sm.virtualcert;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.questionnaire.QuestionnaireBackingBean;
import my.ep.web.managedbean.sm.virtualcert.VirtualCertManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

@ManagedBean(name = "virtualCertBackingBean")
@RequestScoped
public class VirtualCertBackingBean extends BaseBackingBean<VirtualCertManagedBean> {
	public static Logger log = Logger.getLogger(VirtualCertBackingBean.class);
	private String tmpCover = null;
	private String tmpContent = null;
	//private String tmpCombined = null;
	private String tmpPath = null;
	
	public void viewVirtualCert(){
		SmSupplier supp = getSmSupplier();
		QuestionnaireBackingBean quesBackingBean = new QuestionnaireBackingBean();
		String xhtml = "/xhtml/sm/virtualcert/virtualCertDet.xhtml";
		
		//SmSupplier smSupplier = null;
		try {
			IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
	
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			MofRegistrationResDTO resDto = new MofRegistrationResDTO();
			
			reqDto.setSupplierId(getSmSupplier().getSupplierId());
			log.info("*********Supplier ID>" + reqDto.getSupplierId());
			resDto = mofRegistrationSO.retrieveSupplierProfile(reqDto);
			this.getManagedBean().setSmSupplier(resDto.getSmSupplier());
			Date now = new Date();
			this.getManagedBean().setMofAccountLs(new ArrayList<SmMofAccount>());
			for(SmMofAccount mofAcc : this.getManagedBean().getSmSupplier().getSmMofAccounts()){
				log.info("EFF>>>>>>>" + mofAcc.getEffDate());
				log.info("EXF>>>>>>>" + mofAcc.getExpDate());
				if(mofAcc.getEffDate().before(now) && mofAcc.getExpDate().after(now)){
					log.info("viewVirtualCert within the range>>>>");
					this.getManagedBean().getMofAccountLs().add(mofAcc);
				}
			}
//			FacesContext fc = FacesContext.getCurrentInstance();
//			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, xhtml);
			quesBackingBean.validateQuestionnaire(supp.getSmAppl().getApplId(), xhtml);
		} catch(Exception ex){
			log.error("viewVirtualCert ex:" + ex.getMessage());
		}
		
	}
	
	public void generatePdf(String mofNo) throws Exception {
		
//		log.info("mofNo: " + mofNo);
		
		Long applId = getSmSupplier().getSmAppl().getApplId();
		
		log.info("******generatePdf>>>" + applId);
		IMofRegistrationSO mofRegistrationSO = null;
//		SmAppl smAppl = null;
//		tmpPath = System.getProperty("java.io.tmpdir");
		
//		log.info("mofNo: " + mofNo);
		
		tmpPath = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		tmpPath = tmpPath + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/" + "SAP/cert/" + mofNo + "/";
		
		File dir = new File(tmpPath);
		if(!dir.exists()) dir.mkdirs();
		
		log.info("************tmp>>>" + tmpPath);
		SmSupplier smSupplier = null;
		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
	
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			MofRegistrationResDTO resDto = new MofRegistrationResDTO();
			
			//reqDto.setSupplierId(getSmSupplier().getSupplierId());
//			System.out.println("*********Supplier ID>" + reqDto.getSupplierId());
			reqDto.setApplId(applId);
			resDto = mofRegistrationSO.retrieveSupplierProfileByApplId(reqDto);
			log.info("*********Supplier ID>" + resDto.getSmAppl().getApplId());
			smSupplier = resDto.getSmAppl().getSmSupplier();
		} finally {
			mofRegistrationSO = null;
		}
		// instantiate velocity template
		// replace value in velocity template
		String coverHtml = getCoverHtmlFromVelocity(smSupplier, mofNo);
		String contentHtml = getContentHtmlFromVelocity(smSupplier);
		String lampiranBHtml = getLampiranBFromVelocity();
		
		String timeStamp = String.valueOf((new Date()).getTime());
		String suppId = smSupplier.getSupplierId().toString();
		
		// generate pdf from html string
		tmpCover = tmpPath + "cover_" + suppId + "_" + timeStamp + ".pdf";
		tmpContent = tmpPath + "content_" + suppId + "_" + timeStamp + ".pdf";
		log.info("-------tmpCover-------" + tmpCover);
		OutputStream os1 = new FileOutputStream(tmpCover);
        OutputStream os2 = new FileOutputStream(tmpContent);
        generatePdf(os1, coverHtml);
        generatePdf(os2, contentHtml);
        os1.flush();
        os2.flush();
        os1.close();
        os2.close();
		
        File fCover = new File(tmpCover);
        File fContent = new File(tmpContent);
              
		// combine all pdf into 1
        List<InputStream> list = new ArrayList<InputStream>();        
//        InputStream inputCover = new FileInputStream(fCover);
//        InputStream inputContent = new FileInputStream(fContent);
        list.add(new FileInputStream(fCover));
        list.add(new FileInputStream(fContent));
        list.add(new FileInputStream(new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/templates/lampiranB.pdf"))));
		
        String latestFile = tmpPath + "Mof_" + suppId + "_" + timeStamp + ".pdf";
        OutputStream combined = new FileOutputStream(new File(latestFile));
        doMerge(list, combined);
        combined.flush();
        combined.close();
        fCover.delete();
        fContent.delete();
        downloadFile(latestFile);
        
        /* temporary disabled
        try{
        	File afile =new File(latestFile);

        	String newPathFile = "E:\\HKS\\" + afile.getName();
        	if(afile.renameTo(new File(newPathFile))){
        		System.out.println("File is moved successful!");
        		downloadFile(newPathFile);

        	}else{
        		System.out.println("File is failed to move!");
        	}

        }catch(Exception e){
        	e.printStackTrace();
        }*/

        
	}
	
	
	private SmSupplier getSmSupplier() {
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		log.info("******getSmSupplier******" + pmUser.getUserId());
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		supplierSearchReqDTO.setUserId(pmUser.getUserId());

		ISupplierSearchSO supplierSearchSO= null;
		SmSupplier smSupp = null;
		try {
			supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
//			SupplierSearchResDTO smResDTO = supplierSearchSO.retrieveSupplierForVirtualCertification(supplierSearchReqDTO);
			SupplierSearchResDTO smResDTO = supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
			smSupp = (SmSupplier)smResDTO.getResultBean().getResultList().get(0);
		} finally {
			supplierSearchSO = null;
		}
		
		return smSupp;
	}
	
/*	public StreamedContent getPdfFile() throws Exception {
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = PortalUtil
				.getHttpServletResponse(portletResponse);
		res.setHeader("Content-Disposition", "attachment; filename=\""
				+ "test-editor-2-pdf.pdf" + "\"");//
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setContentType("application/pdf");

		res.flushBuffer();
		OutputStream out = res.getOutputStream();
		out.write(getPdfStream2());

		return null;
	}*/
	
/*	public byte[] getPdfStream2() throws Exception {
		
        byte[] pdf = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        OutputStream baos1 = new FileOutputStream("D:\\HKS\\cover.pdf");
        OutputStream baos2 = new FileOutputStream("D:\\HKS\\context.pdf");
//        String html = LEADING_HTML_FOR_PDF + this.getPdfText() + CLOSING_HTML_FOR_PDF;
        String coverhtml = getCoverHtmlFromVelocity();  
        String contentHtml = getContentHtmlFromVelocity();
    
        generatePdf(baos1, coverhtml);
        generatePdf(baos2, contentHtml);
        
        List<InputStream> list = new ArrayList<InputStream>();
        
        list.add(new FileInputStream(new File("D:\\HKS\\cover.pdf")));
        list.add(new FileInputStream(new File("D:\\HKS\\context.pdf")));
        
        OutputStream combined = new FileOutputStream(new File("D:\\HKS\\combined.pdf"));
        
        doMerge(list, combined);
        
        
        return null;
	}*/
	
    public String getCoverHtmlFromVelocity(SmSupplier supplier, String mofNo) throws Exception {
    	String myTemplatePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/templates");

    	Properties p = new Properties();
    	p.setProperty( "file.resource.loader.path", myTemplatePath);
    	Velocity.init(p);

    	Template template = Velocity.getTemplate( "vc-cover.vm" );
    	
    	log.info("template: " + template);
    	
    	VelocityContext context = new VelocityContext();
    	log.info("*********Supplier Appl**********" + supplier.getSmAppl());
    	String supplierType = supplier.getSmAppl().getSupplierType();
    	
    	Date now = new Date();
    	
    	SmMofCert mofCert = null;
    	SmMofAccount smMofAccount = null;
    	for(SmMofAccount mofAccount : supplier.getSmMofAccounts()) {
    		log.info("*********mofAccount**********" + mofAccount.getSmMofCerts().size());
    		if(mofAccount.getEffDate().before(now) && mofAccount.getExpDate().after(now)) {
    			if(mofAccount.getMofNo().equals(mofNo)) {
	    			if(mofAccount.getSupplierType().equals(supplierType)) {
	    				smMofAccount = mofAccount; //added for to make it temporary runaable
	    				log.info("mofAccount.getMofNo(): " + mofAccount.getMofNo());
	    				mofCert = mofAccount.getSmMofCerts().get(0);
	    				
	    				for(SmMofCert cert : mofAccount.getSmMofCerts()) {
	    					log.info("****CERT ID****" + cert.getMofCertId());
	    					log.info("****MOF CERT ID****" + mofCert.getMofCertId());
	    					if(cert.isRecordStatus() && cert.getEffDate().before(now) && cert.getExpDate().after(now)) {
	    						if(cert.getMofCertId() > mofCert.getMofCertId()) {
	    							log.info("****MOF Gretter****" + mofAccount);
	    							mofCert = cert;
	    							smMofAccount = mofAccount;
	    						}
	    					}
	    				}
	    			}
    			}
    		}
    		
    		log.info(">>>>>MOF Account No>>>" + mofAccount.getMofAccountId());
    	}
    	
    	SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yyyy");
    	log.info(">>>>>mofCertSerialNo>>>" + mofCert.getCertSerialNo());
    	log.info(">>>>>mofAccountNo>>>" +  smMofAccount.getMofNo());
    	String mofCertSerialNo = mofCert.getCertSerialNo();
    	String mofAccountNo = smMofAccount.getMofNo();
    	String effDate = ft.format(smMofAccount.getEffDate());
    	String expDate = ft.format(smMofAccount.getExpDate());
    	log.info("*****after expiry date****");
    	this.getManagedBean().setMofCertSerialNo(mofCertSerialNo);
    	this.getManagedBean().setMofAccountNo(mofAccountNo);
    	this.getManagedBean().setEffDate(effDate);
    	this.getManagedBean().setExpDate(expDate);
    	log.info("*****Before COmpany Basic****");
    	String companyName = supplier.getSmAppl().getSmCompanyBasic().getCompanyName();
    	String bizRegNo = supplier.getRegNo();
    	
    	SmAddress smAddress = supplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress();
    	String address1 = smAddress.getAddress1();
    	String address2 = (smAddress.getAddress2() == null) ? smAddress.getAddress2() : "";
    	String address3 = (smAddress.getAddress3() == null) ? smAddress.getAddress3() : "";
    	String address4 = (smAddress.getAddress4() == null) ? smAddress.getAddress4() : "";
    	String postCode = smAddress.getPostcode();
    	String cityName = smAddress.getPmCity().getCityName();
    	String stateName = smAddress.getPmState().getStateName();
    	String countryName = smAddress.getPmCountry().getCountryName();
    	
    	String regDate = supplier.getCreatedDate().toString();
    	
    	List<SmPersonnel> personnelList = new ArrayList<SmPersonnel>();
    	for(SmPersonnel personnel : supplier.getSmAppl().getSmPersonnels()) {
    		if(personnel.isAuthorized() && personnel.isRecordStatus()) {
    			personnelList.add(personnel);
    		}
    	}
    	
    	
    	context.put("mofCertSerialNo", mofCertSerialNo);
    	context.put("mofAccountNo", mofAccountNo);
    	context.put("effDate", effDate);
    	context.put("expDate", expDate);
    	
    	context.put("companyName", companyName);
    	context.put("bizRegNo", bizRegNo);
    	context.put("address1", address1);
    	context.put("address2", address2);
    	context.put("address3", address3);
    	context.put("address4", address4);
    	context.put("postCode", postCode);
    	context.put("cityName", cityName);
    	context.put("stateName", stateName);
    	context.put("countryName", countryName);
    	context.put("registrationDate", regDate);
    	
    	context.put("personnelList", personnelList);
    	
    	StringWriter output = new StringWriter();
    	
//    	Velocity.evaluate(context, output, "log or null", templateString);
    	template.merge(context, output);
    	log.debug("html output: " + output);
    	
    	return output.toString();
    }
    
    public String getContentHtmlFromVelocity(SmSupplier supplier) throws Exception {
    	String myTemplatePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/templates");

    	Properties p = new Properties();
    	p.setProperty( "file.resource.loader.path", myTemplatePath);
    	Velocity.init(p);

    	Template template = Velocity.getTemplate( "vc-category.vm" );
    	
    	log.info("template: " + template);
    	
    	List<SmSupplierCategory> categoryList = new ArrayList<SmSupplierCategory>();
    	for(SmSupplierCategory supplierCategory : supplier.getSmAppl().getSmSupplierCategories()) {
    		if(supplierCategory.isRecordStatus() && supplierCategory.isApprovedByAp() && supplierCategory.isApprovedByPo()) {
    			categoryList.add(supplierCategory);
    		}
    	}    	
    	
    	VelocityContext context = new VelocityContext();
    	
    	context.put("mofCertSerialNo", this.getManagedBean().getMofCertSerialNo());
    	context.put("mofAccountNo", this.getManagedBean().getMofAccountNo());
    	context.put("effDate", this.getManagedBean().getEffDate());
    	context.put("expDate", this.getManagedBean().getExpDate());
    	
    	context.put("categoryList", categoryList);
    	
    	StringWriter output = new StringWriter();
    	    	
    	template.merge(context, output);
    	log.debug("html output: " + output);
    	
    	return output.toString();
    }
    
	public String getLampiranBFromVelocity()  throws Exception {
		String myTemplatePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/templates");

		Properties p = new Properties();
		p.setProperty( "file.resource.loader.path", myTemplatePath);
		Velocity.init(p);

		Template template = Velocity.getTemplate( "vc-lampiran-b.vm" );

		log.info("template: " + template);

		VelocityContext context = new VelocityContext();

		StringWriter output = new StringWriter();

		template.merge(context, output);
		log.debug("html output: " + output);

		return output.toString();
	}
    
	public void generatePdf(OutputStream baos, String html) throws Exception {
		
/*		Document document = new Document(PageSize.A4);
		PdfWriter writer = PdfWriter.getInstance(document, baos);
		
		Font font = new Font(Font.TIMES_ROMAN, 11);
		HeaderFooter footer = new HeaderFooter(new Phrase("(Sijil ini adalah cetakan komputer dan tidak memerlukan tandatangan)", font), false);
		
		footer.setBorder(Rectangle.NO_BORDER);
        footer.setAlignment(Element.ALIGN_CENTER);
        
		document.setFooter(footer);
		
		document.open();
		
		HTMLWorker htmlWorker = new HTMLWorker(document);		
		htmlWorker.parse(new StringReader(html));
				
		document.close();*/
		
        ITextRenderer render = new ITextRenderer();
        if (html != null) {
        	html = html.replaceAll("&amp;nbsp;", "");
        	html = html.replaceAll("&amp;", "&amp;amp;");
            render.setDocumentFromString(html);
            render.layout();
            render.createPDF(baos);
        }
//		return baos.toByteArray();
	}
	
/*	public void convertHtmlToPdf(String htmlString, OutputStream os)
            throws Exception {
        ITextRenderer render = new ITextRenderer();
        if (htmlString != null) {
            htmlString = htmlString.replaceAll("&amp;nbsp;", "");
            htmlString = htmlString.replaceAll("&amp;", "&amp;amp;");
            render.setDocumentFromString(htmlString);
            render.layout();
            render.createPDF(os);
        }
    }*/
	
	public static void doMerge(List<InputStream> list, OutputStream outputStream)
            throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte cb = writer.getDirectContent();
        
        for (InputStream in : list) {
            PdfReader reader = new PdfReader(in);
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                document.newPage();
                //import the page from source pdf
                PdfImportedPage page = writer.getImportedPage(reader, i);
                //add the page to the destination pdf
                cb.addTemplate(page, 0, 0);
            }
            in.close();
        }
        
        outputStream.flush();
        document.close();
        outputStream.close();
    }
	
	/**
	 * Download file.
	 */
	public void downloadFile(String filePath) throws IOException{
		File file = new File(filePath);
		log.info("download file>>>>");
		try{
			PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
			HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
			res.setHeader( "Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" );//
			res.setHeader( "Content-Transfer-Encoding", "binary" );
			res.setContentType("application/octet-stream");
			res.flushBuffer();
			OutputStream out = res.getOutputStream();   
			if(file.getPath()!=null) {
				log.info("file path not null>>>>");
				InputStream input = null;
				try {
					URLConnection uc = new URL(file.getPath()).openConnection();
					input = new BufferedInputStream(uc.getInputStream()); 
				} catch (Exception e) {
					log.info("It's Local file>>>>");
					// Cannot open connection, maybe local file
					input = new FileInputStream(file.getPath());
				}
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = input.read( buffer )) != -1){ 
					out.write( buffer, 0, bytesRead ); buffer = new byte[4096]; 
				}
				input.close();			
			} else {
				//out.write(attachment.getFileByte());
			}
			out.flush();
			out.close(); 
			FacesContext.getCurrentInstance().responseComplete();
		}catch(Exception ex){
			log.error("download exception>>>>" + ex.getMessage());
		}
	}
	
}

