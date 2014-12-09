package my.ep.web.backingbean.sm.virtualcert;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.revision.ISmApplRevisionSO;
import my.ep.biz.service.sm.search.ISupplierCategorySO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.virtualcert.IVirtualCertSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionResDTO;
import my.ep.dto.sm.search.SupplierCategoryReqDTO;
import my.ep.dto.sm.search.SupplierCategoryResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.virtualcert.VirtualCertReqDTO;
import my.ep.dto.sm.virtualcert.VirtualCertResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.questionnaire.QuestionnaireBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.sm.virtualcert.VirtualCertManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
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
		SmSupplier supp = getSmSupplier(null);

		String xhtml = "/xhtml/sm/virtualcert/virtualCertDet.xhtml";

		log.info("viewVirtualCert appl ID>>>>" + supp.getSmAppl().getApplId());
		log.info("isQuestionnaire>>>>" + supp.getSmAppl().isQuestionnaire());
		log.info("Appl Type>>>>" + supp.getSmAppl().getApplType());
		
		if(!supp.getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)){
			if(!supp.getSmAppl().isQuestionnaire()){
				QuestionnaireBackingBean quesBackingBean = new QuestionnaireBackingBean();
				quesBackingBean.getManagedBean().setTargetUrl(xhtml);
				quesBackingBean.getManagedBean().setSmSupp(supp);
				quesBackingBean.getManagedBean().setSmAppl(supp.getSmAppl());
				quesBackingBean.retrieveQuestionnaire();	
				retrieveCertInfo(supp.getSupplierId());


				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/questionnaire/questionnaireView.xhtml");
			}else{
				retrieveCertInfo(supp.getSupplierId());

				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, xhtml);
			}
		}else{
			retrieveCertInfo(supp.getSupplierId());

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, xhtml);
		}
	}
	
	public SmAppl getSerializedAppl(String serPath) {
		
		System.out.println("serPath: " + serPath);
		ISmApplRevisionSO applRevisionSO = null;

		try {
			applRevisionSO = (ISmApplRevisionSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
							ISmApplRevisionSO.class);

			SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();

			reqDTO.setDeSerFileName(serPath);
			SmApplRevisionResDTO resDTO = applRevisionSO.readApplicationHist(reqDTO);

//		} catch (NullPointerException ne) {
//			getManagedBean().setReviewApplicationExist(false);
			return resDTO.getSmAppl();
		} finally {
			applRevisionSO = null;
		}
//		return null;
	}
	
	public void generatePdf(SmMofAccount mofAcc) throws Exception {
		
		SmSupplier smSupplier = getSmSupplier(mofAcc.getMofNo());
		
//		Long applId = getSmSupplier(mofAcc.getMofNo()).getSmAppl().getApplId();
		
//		log.info("******generatePdf>>>" + applId);
		IMofRegistrationSO mofRegistrationSO = null;
//		SmAppl smAppl = null;
//		tmpPath = System.getProperty("java.io.tmpdir");

		String mofNo = mofAcc.getMofNo();

		tmpPath = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		tmpPath = tmpPath + "/" + Calendar.getInstance().get(Calendar.YEAR) + "/" + "SAP/cert/" + mofNo + "/";
		
		File dir = new File(tmpPath);
		if(!dir.exists()) dir.mkdirs();
		
		log.info("************tmp>>>" + tmpPath);
//		SmSupplier smSupplier = null;
/*		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
	
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			MofRegistrationResDTO resDto = new MofRegistrationResDTO();
			
			reqDto.setApplId(applId);
			resDto = mofRegistrationSO.retrieveSupplierProfileByApplId(reqDto);
			log.info("*********Supplier ID>" + resDto.getSmAppl().getApplId());
			smSupplier = resDto.getSmAppl().getSmSupplier();
			
			System.out.println("supplier.getSmAppl()::::::::::::::::000 " + resDto.getSmAppl().getApplId());
			System.out.println("supplier.getSmAppl()::::::::::::::::000 " + resDto.getSmAppl().getSmSupplierCategories().size());
			
		} finally {
			mofRegistrationSO = null;
		}*/
		
		// instantiate velocity template
		// replace value in velocity template
		String coverHtml = getCoverHtmlFromVelocity(smSupplier, mofAcc, false);
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
	
	
	public void generateBumiPdf(SmMofAccount mofAcc) throws Exception {
		
		// retrieve supplier by mofAccountNo, userId
		
		Long applId = getSmSupplier(mofAcc.getMofNo()).getSmAppl().getApplId();
		
		log.info("******generatePdf>>>" + applId);
		IMofRegistrationSO mofRegistrationSO = null;

		String mofNo = mofAcc.getMofNo();

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
			
			reqDto.setApplId(applId);
			resDto = mofRegistrationSO.retrieveSupplierProfileByApplId(reqDto);
			log.info("*********Supplier ID>" + resDto.getSmAppl().getApplId());
			smSupplier = resDto.getSmAppl().getSmSupplier();
		} finally {
			mofRegistrationSO = null;
		}
		// instantiate velocity template
		// replace value in velocity template
		String coverHtml = getCoverHtmlFromVelocity(smSupplier, mofAcc, true);
		String lampiranBHtml = getLampiranBFromVelocity();
		
		String timeStamp = String.valueOf((new Date()).getTime());
		String suppId = smSupplier.getSupplierId().toString();
		
		// generate pdf from html string
		tmpCover = tmpPath + "cover_" + suppId + "_" + timeStamp + ".pdf";
		log.info("-------tmpCover-------" + tmpCover);
		OutputStream os1 = new FileOutputStream(tmpCover);
        generatePdf(os1, coverHtml);
        os1.flush();
        os1.close();
		
        File fCover = new File(tmpCover);
              
		// combine all pdf into 1
        List<InputStream> list = new ArrayList<InputStream>();        
        list.add(new FileInputStream(fCover));
        list.add(new FileInputStream(new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/templates/lampiranB.pdf"))));
		
        String latestFile = tmpPath + "Mof_" + suppId + "_" + timeStamp + ".pdf";
        OutputStream combined = new FileOutputStream(new File(latestFile));
        doMerge(list, combined);
        combined.flush();
        combined.close();
        fCover.delete();
        downloadFile(latestFile);
	}
	
	private List<SmSupplierCategory> retrieveSupplierCategories(Long supplierId) {
		
		SupplierCategoryReqDTO supplierSearchReqDTO = new SupplierCategoryReqDTO();
		supplierSearchReqDTO.setSupplierId(supplierId);
		
//		List<SmSupplierCategory> resultList = new ArrayList<SmSupplierCategory>();
		List resultList = new ArrayList();
		ISupplierCategorySO supplierCategorySO = null;
		
		try {
			supplierCategorySO = (ISupplierCategorySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierCategorySO.class);
			SupplierCategoryResDTO supplierCategoryResDTO = supplierCategorySO.retriveSupplierCategoriesByApplId(supplierSearchReqDTO);
			
			if(supplierCategoryResDTO.isSuccessful()) {
//				resultList = supplierCategoryResDTO.getSupplierCategoryList();
				resultList = supplierCategoryResDTO.getResultList();
			}
		} finally {
			supplierCategorySO = null;
		}
		
		return resultList;
	}
	
	private SmSupplier getSmSupplier(String mofAccountNo) {
		
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		log.info("******getSmSupplier******" + pmUser.getUserId());
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		supplierSearchReqDTO.setUserId(pmUser.getUserId());
		
		if(mofAccountNo != null) {
			supplierSearchReqDTO.setMofAccountNo(mofAccountNo);	
		}
		

		ISupplierSearchSO supplierSearchSO= null;
		SmSupplier smSupp = null;
		try {
			supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
//			SupplierSearchResDTO smResDTO = supplierSearchSO.retrieveSupplierForVirtualCertification(supplierSearchReqDTO);
			SupplierSearchResDTO smResDTO = supplierSearchSO.retriveSupplierProfileForVirtualCert(supplierSearchReqDTO);
//			smSupp = (SmSupplier)smResDTO.getResultBean().getResultList().get(0);
			smSupp = smResDTO.getSmSupplier();
		} finally {
			supplierSearchSO = null;
		}
		
		return smSupp;
	}
	
	
    public String getCoverHtmlFromVelocity(SmSupplier supplier, SmMofAccount mofAcc, boolean isBumi) throws Exception {
    	String myTemplatePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/templates");

    	Properties p = new Properties();
    	p.setProperty( "file.resource.loader.path", myTemplatePath);
    	Velocity.init(p);

    	String templateName = null;
    	
    	if(isBumi) {
    		templateName = "vc-bumi.vm";
    	} else {
    		templateName = "vc-cover.vm";
    	}
    	
    	Template template = Velocity.getTemplate( templateName );
    	
    	log.info("template: " + template);
    	
    	VelocityContext context = new VelocityContext();
    	log.info("*********Supplier Appl**********" + supplier.getSmAppl().getApplId());
    	//String supplierType = supplier.getSmAppl().getSupplierType();
    	
    	//Date now = new Date();
    	
    	SmMofCert mofCert = null;
    	//SmMofAccount smMofAccount = null;
    	
    	IVirtualCertSO virtualCertSO = (IVirtualCertSO) BusinessDelegate.getInstance().getServiceObject("sm", IVirtualCertSO.class);
		VirtualCertReqDTO reqDTO = new VirtualCertReqDTO();
		reqDTO.setMofAccountId(mofAcc.getMofAccountId());
		
		VirtualCertResDTO resDTO = virtualCertSO.retrieveLatestCertByAccountId(reqDTO);
		
		mofCert = resDTO.getSmMofCert();
    	SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yyyy");
    	log.info(">>>>>mofCertSerialNo>>>" + mofCert.getCertSerialNo());
    	log.info(">>>>>mofAccountNo>>>" +  mofAcc.getMofNo());
    	String mofCertSerialNo = mofCert.getCertSerialNo();
    	String mofAccountNo = mofAcc.getMofNo();
    	String effDate = ft.format(mofAcc.getEffDate());
    	String expDate = ft.format(mofAcc.getExpDate());
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
    	String address2 = (StringUtils.isBlank(smAddress.getAddress2())) ? "" : smAddress.getAddress2();
    	String address3 = (StringUtils.isBlank(smAddress.getAddress3())) ? "" : smAddress.getAddress3();
    	String address4 = (StringUtils.isBlank(smAddress.getAddress4())) ? "" : smAddress.getAddress4();
    	String postCode = smAddress.getPostcode();
    	String cityName = smAddress.getPmCity().getCityName();
    	String stateName = smAddress.getPmState().getStateName();
    	String countryName = smAddress.getPmCountry().getCountryName();
//    	String regDate = supplier.getCreatedDate().toString();
    	
    	FacesScopes fs = new FacesScopes();
    	String hostUrl = fs.getRequest().getServerName() +":" + fs.getRequest().getServerPort();
    	
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
    	context.put("regDate", toUtilDate(supplier.getCreatedDate()));
    	
    	context.put("companyName", escapeXhtml(companyName));
    	context.put("bizRegNo", bizRegNo);
    	context.put("address1", escapeXhtml(address1));
    	context.put("address2", escapeXhtml(address2));
    	context.put("address3", escapeXhtml(address3));
    	context.put("address4", escapeXhtml(address4));
    	context.put("postCode", postCode);
    	context.put("cityName", cityName);
    	context.put("stateName", stateName);
    	context.put("countryName", countryName);
    	context.put("personnelList", personnelList);
    	context.put("hostUrl", hostUrl);
    	
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
    	
    	/*List<SmSupplierCategory> categoryList = new ArrayList<SmSupplierCategory>();
    	for(SmSupplierCategory supplierCategory : supplier.getSmAppl().getSmSupplierCategories()) {
    		if(supplierCategory.isRecordStatus() && supplierCategory.isApprovedByAp() && supplierCategory.isApprovedByPo()) {
    			categoryList.add(supplierCategory);
    		}
    	} */   	

//    	List<SmSupplierCategory> supplierCategories = retrieveSupplierCategories(supplier.getSmAppl().getApplId());
    	
    	System.out.println("supplier.getSupplierId(): " + supplier.getSupplierId());
    	
    	List supplierCategories = retrieveSupplierCategories(supplier.getSupplierId());
    	
/*    	List categories = new ArrayList();
    	for(SmSupplierCategory supplierCategory : supplierCategories) {
    		
    		log.info("supplierCategory: " + supplierCategory.isRecordStatus() + " || " +  supplierCategory.isApprovedByAp() + " || " + supplierCategory.isApprovedByPo());
    		
    		if(supplierCategory.isRecordStatus() && supplierCategory.isApprovedByAp() && supplierCategory.isApprovedByPo()) {
    			List category = new ArrayList();
    			
    			category.add(toUtilDate(supplierCategory.getCreatedDate()));
    			category.add(supplierCategory.getPmCategoryL1().getCategoryL1Code() + supplierCategory.getPmCategoryL2().getCategoryL2Code() + supplierCategory.getPmCategoryL3().getCategoryL3Code());
    			category.add(supplierCategory.getPmCategoryL3().getCategoryName());
    			
    			categories.add(category);
    		}
    	}*/
    	
    	VelocityContext context = new VelocityContext();
    	
    	context.put("mofCertSerialNo", this.getManagedBean().getMofCertSerialNo());
    	context.put("mofAccountNo", this.getManagedBean().getMofAccountNo());
    	context.put("effDate", this.getManagedBean().getEffDate());
    	context.put("expDate", this.getManagedBean().getExpDate());
    	context.put("regDate", toUtilDate(supplier.getCreatedDate()));
//    	context.put("categoryList", categoryList);
    	context.put("categories", supplierCategories);
    	context.put("addDateAndCount", (supplier.getSmAppl().getApplType() == ParameterConst.NEW_SM_APPLN_FOR_MOF) ? "" : getAddCategoriesDate(supplierCategories, supplier.getSmAppl().getCreatedDate()));
    	context.put("deleteDateAndCount", (supplier.getSmAppl().getApplType() == ParameterConst.NEW_SM_APPLN_FOR_MOF) ? "" : getDeleteCategoriesDate(supplierCategories, supplier.getSmAppl().getCreatedDate()));
    	
    	StringWriter output = new StringWriter();
    	    	
    	template.merge(context, output);
    	log.debug("html output: " + output);
    	
    	return output.toString();
    }
    
    private String getAddCategoriesDate(List supplierCategories, java.sql.Timestamp applDate) {
    	Calendar applUtilDate = Calendar.getInstance();
    	applUtilDate.setTimeInMillis(applDate.getTime());
    	applUtilDate.add(Calendar.SECOND, 30);
    	
    	Calendar catDate = Calendar.getInstance();
    	String addDateInString = null; 
    	int count = 0;
    	
    	/*for(SmSupplierCategory supplierCategory : supplierCategories) {
    		catDate.setTimeInMillis(supplierCategory.getChangedDate().getTime());
    		if(catDate.after(applUtilDate)) {
    			addDateInString = toUtilDate(supplierCategory.getChangedDate());
    			count++;
    		}
    	}*/
    	
    	for(Object obj : supplierCategories) {
    		Object[] supplierCategory = (Object[]) obj;
    		catDate.setTimeInMillis(((java.sql.Timestamp)supplierCategory[1]).getTime());
    		if(((BigDecimal)supplierCategory[5]).intValue() == 1) {
        		if(catDate.after(applUtilDate)) {
        			addDateInString = toUtilDate((java.sql.Timestamp)supplierCategory[1]);
        			count++;
        		}    			
    		}
    	}
    	
    	String dateAndCount = "";
    	if(count > 0) {
    		dateAndCount = addDateInString + " : " + count;
    	}
    	
    	return dateAndCount;
    }
    
    private String getDeleteCategoriesDate(List<SmSupplierCategory> supplierCategories, java.sql.Timestamp applDate) {
    	Calendar applUtilDate = Calendar.getInstance();
    	applUtilDate.setTimeInMillis(applDate.getTime());
    	applUtilDate.add(Calendar.SECOND, 30);
    	
    	Calendar catDate = Calendar.getInstance();
    	String addDateInString = null; 
    	int count = 0;
    	
    	for(Object obj : supplierCategories) {
    		Object[] supplierCategory = (Object[]) obj;
    		catDate.setTimeInMillis(((java.sql.Timestamp)supplierCategory[1]).getTime());
    		if(((BigDecimal)supplierCategory[5]).intValue() == 9) {
	    		if(catDate.after(applUtilDate)) {
	    			addDateInString = toUtilDate((java.sql.Timestamp)supplierCategory[1]);
	    			count++;
	    		}
    		}
    	}
    	
    	String dateAndCount = "";
    	if(count > 0) {
    		dateAndCount = addDateInString + " : " + count;
    	}
    	
    	return dateAndCount;
    }
    
    private String toUtilDate(java.sql.Timestamp sqlDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Calendar createdDate = Calendar.getInstance();
		createdDate.setTimeInMillis(sqlDate.getTime());
		return sdf.format(createdDate.getTime());
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
		
        ITextRenderer render = new ITextRenderer();
        if (html != null) {
        	html = html.replaceAll("&amp;nbsp;", "");
        	html = html.replaceAll("&amp;", "&amp;amp;");
            render.setDocumentFromString(html);
            render.layout();
            render.createPDF(baos);
        }
	}
	
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
	
	private void retrieveCertInfo(Long supplierId){
		IVirtualCertSO virtualCertSO = (IVirtualCertSO) BusinessDelegate.getInstance().getServiceObject("sm", IVirtualCertSO.class);
		VirtualCertReqDTO reqDTO = new VirtualCertReqDTO();
		reqDTO.setSupplierId(supplierId);
		VirtualCertResDTO resDTO = virtualCertSO.retrieveMOFCertBySupplierId(reqDTO);
		this.getManagedBean().setMofCertLs(new ArrayList<SmMofCert>());
		if(resDTO.getSmMofCerts().size() > 0){
			this.getManagedBean().setMofCertLs(resDTO.getSmMofCerts());
		}
	}
	
	public void printMofCertificate(String certNo) throws IOException{

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("PCERTSERIALNO", certNo);
		
		generateReport(ReportConst.SM_VIRTUAL_VERIFICATION_jsp, paramMap);
	}
	
	public void printBumiCertificate(String certNo) throws IOException{

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("PCERTSERIALNO", certNo);
		
		generateReport(ReportConst.SM_VIRTUAL_VERIFICATION_BUMI_jsp, paramMap);
	}
}

