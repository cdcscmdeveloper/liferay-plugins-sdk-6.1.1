package my.ep.web.backingbean.sm.questionnaire;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.questionnaire.IQuestionnaireSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.questionnaire.QuestionnaireReqDTO;
import my.ep.dto.sm.questionnaire.QuestionnaireResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAnswer;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmQuestion;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierAnswer;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.questionnaire.QuestionnaireManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.primefaces.model.StreamedContent;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.liferay.portal.util.PortalUtil;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

@ManagedBean(name = "questionnaireBackingBean")
@RequestScoped
public class QuestionnaireBackingBean extends
		BaseBackingBean<QuestionnaireManagedBean> {
	public static Logger log = Logger.getLogger(QuestionnaireBackingBean.class);

	public QuestionnaireBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
	}

	public void retrieveSupplierInfo() {
		log.info("***********retrieveSupplierInfo**************");
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		reqDTO.setSupplierId(27);
		IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IRegistrationSO.class);
		RegistrationResDTO resDTO = reqSO.retrieveSuppOnlineAcc(reqDTO);
		log.info("***********retrieveSupplierInfo**************"
				+ resDTO.getSmSupp().getRegNo());
		this.getManagedBean().setSmSupp(resDTO.getSmSupp());
		resDTO.getSmSupp().getSmAppl().getApplType();
	}

	public void retrieveQuestionnaire() {
		// this.test();
		log.info("***********retrieveQuestionnaire**************");
		QuestionnaireReqDTO reqDTO = new QuestionnaireReqDTO();
		IQuestionnaireSO reqSO = (IQuestionnaireSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IQuestionnaireSO.class);
		QuestionnaireResDTO resDTO = reqSO.retrieveActiveQuestion();

		// this.getManagedBean().setSmQuestionLs(resDTO.getSmQuestionList());

		List<SmSupplierAnswer> smSuppAnswerLs = new ArrayList<SmSupplierAnswer>();
		for (SmQuestion smQues : resDTO.getSmQuestionList()) {
			SmSupplierAnswer smSuppAns = new SmSupplierAnswer();
			//smSuppAns.setSmSupplier(this.getManagedBean().getSmSupp());
			smSuppAns.setSmQuestion(smQues);
			smSuppAns.setSmAnswer(new SmAnswer());
			smSuppAnswerLs.add(smSuppAns);
			// /smSuppAns.setSupplierAnswerId(supplierAnswerId)
		}

		this.getManagedBean().setSmSuppAnswerLs(smSuppAnswerLs);
	}

	public String submitQuestionnaire() {
		log.info("***********submitQuestionnaire Start**************");
		// this.getManagedBean().getSmSupp().setSmSupplierAnswers(this.getManagedBean().getSmSuppAnswerLs());
		QuestionnaireReqDTO reqDTO = new QuestionnaireReqDTO();
		// for(SmSupplierAnswer sa : this.getManagedBean().getSmSuppAnswerLs()){
		// log.info("****88888888888888888*****" +
		// sa.getSmAnswer().getAnswerId());
		// }
		log.info("****88888888888888888*****");
		reqDTO.setSmSuppAns(this.getManagedBean().getSmSuppAnswerLs());
		IQuestionnaireSO reqSO = (IQuestionnaireSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IQuestionnaireSO.class);
		QuestionnaireResDTO resDTO = reqSO.questionnaireSubmission(reqDTO);

		return "viewVirtualCert";

	}
	
	
	public void generatePdf() throws Exception {
		
		IMofRegistrationSO mofRegistrationSO = null;
//		SmAppl smAppl = null;
		SmSupplier smSupplier = null;
		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
	
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			MofRegistrationResDTO resDto = new MofRegistrationResDTO();
	
			reqDto.setSupplierId(getSmSupplier().getSupplierId());
			resDto = mofRegistrationSO.retrieveSupplierProfile(reqDto);
//			smAppl = resDto.getSmAppl();
			smSupplier = resDto.getSmSupplier();
		} finally {
			mofRegistrationSO = null;
		}
		// instantiate velocity template
		// replace value in velocity template
		String coverHtml = getCoverHtmlFromVelocity(smSupplier);
		String contentHtml = getContentHtmlFromVelocity(smSupplier);
		String lampiranBHtml = getLampiranBFromVelocity();
		
		// generate pdf from html string
		OutputStream os1 = new FileOutputStream("D:\\HKS\\cover.pdf");
        OutputStream os2 = new FileOutputStream("D:\\HKS\\content.pdf");
//        OutputStream os3 = new FileOutputStream(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/templates/lampiranB.pdf"));
        generatePdf(os1, coverHtml);
        generatePdf(os2, contentHtml);
//        generatePdf(os3, lampiranBHtml);
		
		// combine all pdf into 1
        List<InputStream> list = new ArrayList<InputStream>();        
        list.add(new FileInputStream(new File("D://HKS//cover.pdf")));
        list.add(new FileInputStream(new File("D://HKS//content.pdf")));
        list.add(new FileInputStream(new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/templates/lampiranB.pdf"))));
		
        OutputStream combined = new FileOutputStream(new File("D:\\HKS\\combined.pdf"));
        
        doMerge(list, combined);
	}
	
	
	
	
	
	
	private SmSupplier getSmSupplier() {
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		
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
	
    public String getCoverHtmlFromVelocity(SmSupplier supplier) throws Exception {
    	String myTemplatePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/templates");

    	Properties p = new Properties();
    	p.setProperty( "file.resource.loader.path", myTemplatePath);
    	Velocity.init(p);

    	Template template = Velocity.getTemplate( "vc-cover.vm" );
//    	Template template = Velocity.getTemplate( "vc-bumi.vm" );
    	
    	log.info("template: " + template);
    	
    	VelocityContext context = new VelocityContext();
    	
    	String supplierType = supplier.getSmAppl().getSupplierType();
    	Date now = new Date();
    	
    	SmMofCert mofCert = null;
    	SmMofAccount smMofAccount = null;
    	for(SmMofAccount mofAccount : supplier.getSmMofAccounts()) {
    		if(mofAccount.getEffDate().before(now) && mofAccount.getExpDate().after(now)) {
    			if(mofAccount.getSupplierType().equals(supplierType)) {
    				mofCert = mofAccount.getSmMofCerts().get(0);
    				for(SmMofCert cert : mofAccount.getSmMofCerts()) {
    					if(cert.isRecordStatus() && cert.getEffDate().before(now) && cert.getExpDate().after(now)) {
    						if(cert.getMofCertId() > mofCert.getMofCertId()) {
    							mofCert = cert;
    							smMofAccount = mofAccount;
    						}
    					}
    				}
    			}
    		}
    	}
    	
    	SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yyyy");
    	
    	String mofCertSerialNo = mofCert.getCertSerialNo();
    	String mofAccountNo = smMofAccount.getMofNo();
    	String effDate = ft.format(smMofAccount.getEffDate());
    	String expDate = ft.format(smMofAccount.getExpDate());
    	
    	this.getManagedBean().setMofCertSerialNo(mofCertSerialNo);
    	this.getManagedBean().setMofAccountNo(mofAccountNo);
    	this.getManagedBean().setEffDate(effDate);
    	this.getManagedBean().setExpDate(expDate);
    	
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
    	
    	String regDate = ft.format(supplier.getCreatedDate());
    	this.getManagedBean().setRegDate(regDate);
    	
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
    	context.put("regDate", this.getManagedBean().getRegDate());
    	
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
        }
        
        outputStream.flush();
        document.close();
        outputStream.close();
    }
	
	public void validateQuestionnaire(Long applId, String targetUrl){
		System.out.println("********QuestionnaireBackingBean Start******");
		if(applId != null && targetUrl != null){
			this.getManagedBean().setTargetUrl(targetUrl);
			try{
				SmSupplier supplier = getSmSupplier();
				if(!supplier.getSmAppl().isQuestionnaire()){
					this.getManagedBean().setSmSupp(supplier);
					System.out.println("********QuestionnaireBackingBean******" + applId);
					//System.out.println("********QuestionnaireBackingBean type******" + type);

					retrieveQuestionnaire();
					//this.getManagedBean().setTargetUrl(targetUrl);
					this.getManagedBean().setSmSupp(supplier);
					SmMofAccount mofAcc = retrieveLatestMofAccount();
					this.getManagedBean().setSmMofAccount(mofAcc);
					FacesContext fc = FacesContext.getCurrentInstance();
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/questionnaire/questionnaireView.xhtml");
					return;
				}
//				else{
//					FacesContext fc = FacesContext.getCurrentInstance();
//					fc.getApplication().getNavigationHandler().handleNavigation(fc, null,targetUrl);
//				}
			}catch(Exception ex){
				System.out.println("Exception>>>>" + ex.getMessage());
			}
		}
	}
	
	public SmMofAccount retrieveLatestMofAccount(){
		SmMofAccount smMofAcc = new SmMofAccount();
		try {
			IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
	
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			MofRegistrationResDTO resDto = new MofRegistrationResDTO();
			
			reqDto.setSupplierId(getSmSupplier().getSupplierId());
			System.out.println("*********Supplier ID>" + reqDto.getSupplierId());
			resDto = mofRegistrationSO.retrieveSupplierProfile(reqDto);
			SmSupplier supplier = resDto.getSmSupplier();
			Date now = new Date();
			
			for(SmMofAccount mofAcc : supplier.getSmMofAccounts()){
				System.out.println("11111111111111111" + mofAcc);
				if(mofAcc.getEffDate().before(now) && mofAcc.getExpDate().after(now)){
					System.out.println("***mofAcc.getSupplierType()****" + mofAcc.getSupplierType());
					if(mofAcc.getSupplierType().equalsIgnoreCase(supplier.getSmAppl().getSupplierType())){
						smMofAcc = mofAcc;
					}
				}
			}
			
		}catch(Exception ex){
			System.out.println("**********retrieveLatestMofAccount exception>>>>>>" + ex.getMessage());
		}
		
		return smMofAcc;
	}
}
