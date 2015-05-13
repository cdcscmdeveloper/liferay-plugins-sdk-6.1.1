package my.ep.web.backingbean.sm.postsupplierregistration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.workFlowStatus.ISmWorkFlowStatusSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.postsupplierregistration.PostSupplierRegistrationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

@ManagedBean (name = "postSupplierRegBB")
@RequestScoped
public class PostSupplierRegistrationBackingBean extends BaseBackingBean<PostSupplierRegistrationManagedBean>{
	//private static final String CATALOGUE_UPLOAD_PATH = "/xhtml/cm/codification/codificationView.xhtml";
	private static final String CATALOGUE_UPLOAD_PATH = "supptemp-codification-search";
	private static final String CATALOGUE_MAINT = "supptemp-catalogue-maint";
	//private static final String SUPPLIER_TYPE_SELECTION_PATH = "supptemp-registration-cont";
	private static final String SOFT_CERT_PATH = "supptemp-request-soft-cert";
	private static final String MOF_REG_PATH = "supptemp-mof-registration";
	private static final String TASK_LIST_PATH = "task-list";
	//private static final String DASHBOARD = "dashboard";
	//private static final String DASHBOARD_PATH = "welcome";
	private static final String SUPPLIER_TYPE_SELECTION_XHTML = "/xhtml/sm/supplierregistrationcont/supplierTypeSelection.xhtml";
	private static final String CONTENT_PAGE = "/xhtml/sm/postsupplierregistration/msgContent.xhtml";
	private static final String PROMO_PAGE = "/xhtml/sm/postsupplierregistration/supplierPromo.xhtml";
	private static final String MOF_SOFTCERT_REQUEST = "request-soft-cert";
	private static final String MOF_CODIFICATION_SEARCH = "codification-search";
	private static final String UPGRADE_TO_MOF = "upgrade-basic-to-mof";
	
	public static Logger log = Logger.getLogger(PostSupplierRegistrationBackingBean.class);
	
	public PostSupplierRegistrationBackingBean() {

	}
	
	public String currentStatusAndPath(){
		SmSupplier userSupplier = getSupplierProfileByUserId();
		String path = null;
		String desc = null;

		if(userSupplier != null){
			ISupplierSearchSO supplierSearchSO = SmServiceLookup.getSupplierSearchSO();
			SupplierSearchReqDTO suppSearchReqDTO = new SupplierSearchReqDTO();
			suppSearchReqDTO.setSupplierId(userSupplier.getSupplierId());
			SupplierSearchResDTO suppSearchResDTO = supplierSearchSO.retriveSupplierById(suppSearchReqDTO);

			SmSupplier supplier = suppSearchResDTO.getSmSupplier();

			log.info("Supplier is not null===>" + supplier.getSupplierId());
			this.getManagedBean().setSmSupplier(supplier);
			getManagedBean().setFlowStatus(0);

			SmAppl appl = retrieveInProcessAppl(supplier.getSupplierId());
			this.getManagedBean().setBannerImage(false);
			getManagedBean().setSmAppl(appl);

			if(appl != null){
				log.info("*****In Progress*********");			
				SmWorkflowStatus workFlowStatus = getSmWorkflowStatus(appl);
				//desc = this.getStatusDescById(workFlowStatus.getPmStatus().getStatusId());
				this.getManagedBean().setStatusDesc(getStatusDescById(workFlowStatus.getPmStatus().getStatusId()));
				showHyperlink(workFlowStatus.getPmStatus().getStatusId());
				path = TASK_LIST_PATH;

			}else{			
				//MOF APPL
				if(supplier.getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)){
					SmWorkflowStatus workFlowStatus = getSmWorkflowStatus(supplier.getSmAppl());
					//Transact with Federal and supplier type is CONTRACTOR
					if(supplier.getSmAppl().getSmCompanyBasic().isWithFederal() && 
							supplier.getSmAppl().getSupplierType().equalsIgnoreCase(ParameterConst.SUPPLIER_TYPE_CONTRACTOR)){
						if(workFlowStatus != null && workFlowStatus.getPmStatus().getStatusId() == 20199){
							if(checkPendingSoftCertRequest(supplier.getSupplierId())){
								path = SOFT_CERT_PATH;
								getManagedBean().setFlowStatus(5);
							}else{
								String catalogueStat = checkCatalogueUpload(supplier.getSupplierId());
								if(catalogueStat == null){
									path = CATALOGUE_UPLOAD_PATH;
									getManagedBean().setFlowStatus(6);
								}else{
									if(catalogueStat.equalsIgnoreCase("U")){
										getManagedBean().setFlowStatus(0);
									}else{
										path = CATALOGUE_MAINT;
										getManagedBean().setFlowStatus(6);
									}
								}
							}
						}else{
							path = MOF_REG_PATH;
							getManagedBean().setFlowStatus(1);
						}
					}else if(supplier.getSmAppl().getSupplierType().equalsIgnoreCase(ParameterConst.SUPPLIER_TYPE_CONSULTANT)){	
							path = MOF_REG_PATH;
							getManagedBean().setFlowStatus(1);
					}
					
					log.info("********FLOW STATUS**********" +  getManagedBean().getFlowStatus());
				}else if(supplier.getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.BASIC_ACCOUNT)){
					//Basic Applcation
					String catStatus = checkCatalogueUpload(supplier.getSupplierId());
					if(catStatus == null){
						path = CATALOGUE_UPLOAD_PATH;
						getManagedBean().setFlowStatus(1);
					}else{
						if(catStatus.equalsIgnoreCase("U")){
							path = SOFT_CERT_PATH;
							getManagedBean().setFlowStatus(2);
						}else{
							path = CATALOGUE_MAINT;
							getManagedBean().setFlowStatus(1);
						}
					}
				}
			}
		}

		/*			if(isPath){
				return path;
			}else{
				return desc;
			}*/

		return path;

	}
	
	public void preRender(){
		String currStatus = currentStatusAndPath();
		log.info("******PreRender****" + currStatus);
		//this.getManagedBean().setStatusDesc(currStatus);
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CONTENT_PAGE);
	}
	
	public void pageNavigate() throws Exception{
		if(isHavePendingTaskList()){
			pageRedirect(TASK_LIST_PATH);
			
		}else{
			String path = currentStatusAndPath();
			log.info("******xhtml*******" + path);
			
			/*if(path.equalsIgnoreCase(SUPPLIER_TYPE_SELECTION_PATH)){
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, path);
			}else{
				try{*/
					pageRedirect(path);
			/*	}
				catch(Exception ex){
					throw new Exception(ex.getMessage());
				}
			}*/
		}
		
	}
	
	
/*	public String getStatusDesc(){
		return getManagedBean().getStatusDesc();
	}*/
	
	/**
	 * Save supplier upon user selected supplier and account type
	 * @return String
	 */
	public String  saveSupplierAccountType(){
		log.info("***saveSupplierAccountType***" + this.getManagedBean().getSmSupplier().getSmAppl().getSupplierType());
		SmAppl appl = this.getManagedBean().getSmSupplier().getSmAppl();
		
		if(appl != null){
			if(appl.getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)){
				appl.setApplType(ParameterConst.NEW_SM_APPLN_FOR_MOF);

			}else{
				if(appl.getApplType().equalsIgnoreCase(ParameterConst.BASIC_ACCOUNT)){
					appl.setApplType(ParameterConst.BASIC_ACCOUNT);
				}else{
					appl.setApplType(ParameterConst.NEW_SM_APPLN_FOR_MOF);
				}
				
				appl.setSupplierType(SupplierManagementConst.STYPE_CONTRACTOR);
			}
			
			RegistrationReqDTO reqDTO = new RegistrationReqDTO();
			reqDTO.setSmSupp(getManagedBean().getSmSupplier());
			IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IRegistrationSO.class);
			try{
				RegistrationResDTO resDTO = reqSO.updateSupplier(reqDTO);
				log.info("--------Supplier------------" + this.getManagedBean().getSmSupplier().getBusinessType());
				
				if(resDTO.isSuccessful()){
					//FacesContext fc = FacesContext.getCurrentInstance();
					if(this.getManagedBean().getSmSupplier().getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)){
						//fc.getApplication().getNavigationHandler().handleNavigation(fc, null, MOF_REG_PATH);
						pageRedirect(MOF_REG_PATH);
					}else{
						pageRedirect(CATALOGUE_UPLOAD_PATH);
						//fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CATALOGUE_UPLOAD_PATH);
					}
				}
			}catch(Exception ex){
				throw new EpException(ex.getMessage());
			}
		}
		
		return null;
	}
	
	/**
	 * get user's supplier profile
	 * @return SmSupplier
	 */
	private SmSupplier getSupplierProfileByUserId(){
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		log.info("***pmUser name***" + pmUser.getUserName());
		log.info("***pmUser id***" + pmUser.getUserId());
		
		if(pmUser != null){
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			supplierSearchReqDTO.setUserId(pmUser.getUserId());

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
			SupplierSearchResDTO smResDTO =supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
			
			if(smResDTO.getResultBean() != null){
				if(smResDTO.getResultBean().getResultList().size() > 0){
					SmSupplier smSupp = (SmSupplier)smResDTO.getResultBean().getResultList().get(0);
					log.info("------getSupplierProfile--------" + smSupp.getCompanyName());
					return smSupp;
				}
			}
		}
		
		
		return null;
		
	}
	
	/**
	 * Get Supplier catalogue Item
	 */
	private List<CmSupplierItem> getCmSupplierItems(Long supplierId){
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		reqDTO.setSupplierId(supplierId);
		
		ICatalogueSO reqSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, ICatalogueSO.class);
		CatalogueResDTO resDTO = reqSO.retrieveSupplierItemBySupplierId(reqDTO);
		
		return resDTO.getSupplierItemList();
	}
	
	/**
	 * Check whether the login user have pending task in task list
	 * 
	 * @return boolean
	 */
	private boolean isHavePendingTaskList() {
		boolean retVal = false;
		if (getManagedBean().getToken() == null
				|| StringUtils.isBlank(getManagedBean().getToken())) {
			FacesScopes face = new FacesScopes();
			getManagedBean().setToken(face.getBPMAuthenticationCtxToken());
		}
		List<WorklistItemVO> taskList = SmCommonUtil.getBpmTaskListByAuthToken(getManagedBean().getToken());

		if (taskList == null || taskList.isEmpty()) {
			retVal = false;
		} else {
			retVal = true;
		}
		return retVal;
	}
	
	/**
	 * Page Redirect
	 * @param url
	 * @throws IOException
	 * @throws PortalException
	 * @throws SystemException
	 */
	public void pageRedirect(String url)  throws IOException,PortalException, SystemException {
		log.info("**********pageRedirect*********" + url);
		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay) facesScope.getRequestScope(WebKeys.THEME_DISPLAY);
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String commonUrl = themeDisplay.getURLCurrent().substring(0,themeDisplay.getURLCurrent().indexOf(
						themeDisplay.getLayout().getGroup().getFriendlyURL())+ themeDisplay.getLayout().getGroup().getFriendlyURL().length());
		
		log.info("Postregistration Common url>>>>>>>>>>>" + commonUrl);
		
		externalContext.redirect(commonUrl + "/" + url);
	}
	
	/**
	 * retrieve In Process Appl
	 * @param supplierId
	 * @return
	 */
	private SmAppl retrieveInProcessAppl(Long supplierId){
		IMofRegistrationSO mofRegSO = SmServiceLookup.getMofRegistrationSO();
		MofRegistrationReqDTO mofRegistrationReqDTO = new MofRegistrationReqDTO();
		/*mofRegistrationReqDTO.setSmSupplier(supplier);
		mofRegistrationReqDTO.setAppType(ParameterConst.NEW_SM_APPLN_FOR_MOF);*/
		mofRegistrationReqDTO.setSupplierId(supplierId);
		
		
		MofRegistrationResDTO mofRegistrationResDTO = mofRegSO.retrieveActiveSmApplBySupp(mofRegistrationReqDTO);
		
		return mofRegistrationResDTO.getSmAppl();
	}
	
	/**
	 * Retrieve SmWorkFlowStatus
	 * @param appl
	 * @return SmWorkflowStatus
	 */
	private SmWorkflowStatus getSmWorkflowStatus(SmAppl appl){
		ISmWorkFlowStatusSO workFlowSO = SmServiceLookup.getSmWorkFlowStatusSO();
		SmWorkFlowStatusReqDTO reqDTO = new SmWorkFlowStatusReqDTO();
		reqDTO.setApplId(appl.getApplId());
		SmWorkFlowStatusResDTO resDTO = workFlowSO.retrieveCurrentWorkFlowStatusByDocId(reqDTO);
		
		return resDTO.getWorkflowStatus();
		
	}
	
	/**
	 * get Labe From Properties
	 * @param labelStr
	 * @return String
	 */
	private String getLabelFromProperties(String labelStr){
		Locale locale = new FacesScopes().getUserLocale();
		String label = ResourceBundleUtils.getMessage(labelStr, ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		
		return label;
	}
	
	/**
	 * proceed To Supplier Selection
	 * @throws PortalException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void proceedToSupplierSelection() throws PortalException, SystemException, IOException{
		SmSupplier supplier = getSupplierProfileByUserId();
		if(supplier != null){
			this.getManagedBean().setSmSupplier(supplier);
			if(!supplier.getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.TEMP_TYPE) || 
					!supplier.getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.TEMP_TYPE)){
				//pageRedirect(DASHBOARD_PATH);
				/*FacesScopes facesScope = new FacesScopes();
				hemeDisplay themeDisplay = (ThemeDisplay)facesScope.getRequestScope().WebKeys.THEME_DISPLAY);
				PortletURL url = PortletURLFactoryUtil.create(request, this.getPortletName(), themeDisplay.getPlid(), PortletRequest.RENDER_PHASE);*/
				//BridgeContextImpl.getCurrentInstance().redirect(DASHBOARD_PATH);
				
				//FacesContext fc = FacesContext.getCurrentInstance();
				//fc.getApplication().getNavigationHandler().handleNavigation(fc, null, SUPPLIER_TYPE_SELECTION_XHTML);
			}else{
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, SUPPLIER_TYPE_SELECTION_XHTML);
			}
		}
	}
	
	/**
	 * show/ Hide the hyperlink based on statusCode
	 * @param statusCode
	 */
	private void showHyperlink(Long statusCode){
		Integer sc = statusCode.intValue();
		
		switch(sc){
		case 20100 : //Pending Application Declaration
			getManagedBean().setFlowStatus(1);
			getManagedBean().setHyperlink(true);
			break;

		case 20114 : //Pending Payment (Processing fee)
			getManagedBean().setFlowStatus(2);
			getManagedBean().setHyperlink(true);
			break;

		case 20200 : //Pending Application Inquiry
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(true);
			break;

		case 20251 : //Pending Payment
			getManagedBean().setFlowStatus(4);
			getManagedBean().setHyperlink(true);
			break;
			
			
		case 20101 : //Pending Supporting Document Verification
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			break;
		
		case 20102 : //Pending Application Verification
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			break;
		
		case 20103 : //Pending Review Site Visit Report - Before Approval
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			break;

		case 20104 : //Pending Review Application
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			break;
			
		case 20105 : //Pending Site Visit
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			break;
		
		case 20106 : //Pending Re-Site Visit
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			break;
			
		case 20107 : //Pending Reply Inquiry by Processing Officer
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(true);
			break;
		
		case 20108 : //Pending Application Approval
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			break;
			
		case 20109 : //Pending Application Re-Approval
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			break;
		
		case 20110 : //Pending Site Visit Assignment - Before Approval
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			break;
		
		case 20111 : //Pending Site Visit Assignment - Before Approval
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			break;
			
		default :
			getManagedBean().setHyperlink(false);
			break;
		}	
	}
	
	/**
	 * Construct the to do list for MOF
	 * @param supplier
	 */
	private void mofUserAction(SmSupplier supplier){
		Map<String,String> mofActionMap = new HashMap<String, String>();
		
		SmCompanyBasic companyBasic = retrieveSmCompanyBasic(supplier.getSupplierId());
		if(companyBasic.isWithFederal() && supplier.getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)){
			if(checkPendingSoftCertRequest(supplier.getSupplierId())){
				//String msg = getLabelFromProperties("lbl.sm.basic.softcert.request"); 
				//String softCertImg = getLabelFromProperties("lbl.sm.softcert.request.image");
				
				mofActionMap.put("SC", MOF_SOFTCERT_REQUEST);
			}
			
			List<CmSupplierItem> suppItems = getCmSupplierItems(supplier.getSupplierId());
			log.info("SupplierItems size:>>>>> " + suppItems);
			boolean publishedStatus = false;
			if(suppItems.size() > 0){
				for(CmSupplierItem cmSuppItem : suppItems){
					if(cmSuppItem.getItemStatus().equalsIgnoreCase("U")){
						publishedStatus = true;
					}
				}
			}
			
			if(!publishedStatus){
				//String msg = getLabelFromProperties("lbl.sm.basic.catalogue.upload"); 
				//String catUploadImg = getLabelFromProperties("lbl.sm.catalogue.upload.image");
				mofActionMap.put("CU", MOF_CODIFICATION_SEARCH);
			}
		}
		
		log.info("mofActionMap>>>>" + mofActionMap.size());
		getManagedBean().setUserActionMap(mofActionMap);
	}
	
	/**
	 * check Pending SoftCert Request
	 * @param supplierId
	 * @return boolean
	 */
	private boolean checkPendingSoftCertRequest(Long supplierId){
		IPersonnelSO personnelSO =  SmServiceLookup.getPersonnelSO();
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(supplierId);
		
		PersonnelResDTO resDTO = personnelSO.retrievePersonnelBySupplierId(reqDTO); 
		boolean softCertStatus = false;
		
		log.info("resDTO.getSmPersonnelList().size(): "+ resDTO.getSmPersonnelList().size());
		if(resDTO.getSmPersonnelList().size() > 0){
			for(SmPersonnel personnel : resDTO.getSmPersonnelList()){
				if(personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PENDING_REQ){
					softCertStatus = true;
					break;
				}
			}
		}
		
		return softCertStatus;
	}
	
	/**
	 * retrieve SmCompanyBasic by supplier Id
	 * @param supplierId
	 * @return SmCompanyBasic
	 */
	private SmCompanyBasic retrieveSmCompanyBasic(Long supplierId){
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setSupplierId(supplierId);
		SupplierSearchResDTO resDTO = SmServiceLookup.getSupplierSearchSO().retrieveSmCompanyBasicBySupplierId(reqDTO);
		
		return resDTO.getSmCompanyBasic();
	}
	
	/**
	 * Validate User is MOF Admin
	 * @return boolean
	 */
	public boolean validMofUser(){
		log.info("*******check validMofUser********");
		if(new FacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())){ 
			return true;
		}
		return false;
	}
	
	/**
	 * Validate User is MOF Admin
	 * @return boolean
	 */
	public boolean validBasicSupplier(){
		log.info("*******validBasicSupplierAdmin********");
		if(new FacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())){ 
			return true;
		}
		return false;
	}
	
	/**
	 * Action for basic supplier admin
	 */
	private void basicSupplierAction(){
		Map<String,String> basicActionMap = new HashMap<String, String>();
		
		//String msg = "Upgrade to MOF Account to enjoys more benefits.";
		//String msg = getLabelFromProperties("lbl.sm.mof.upgrade.image");
		basicActionMap.put("UPMOF", UPGRADE_TO_MOF);
		
		getManagedBean().setUserActionMap(basicActionMap);
	}
	
	
	/**
	 * Validate User is Supplier Temp
	 * @return boolean
	 */
	public boolean validSupplierTemp(){
		log.info("*******validBasicSupplierAdmin********");
		if(new FacesScopes().hasRoleUser(RoleCode.SUPPLIER_TEMP.value())){ 
			return true;
		}
		return false;
	}
	
	public void naviToSoftCertRequest(){
		try {
			pageRedirect(SOFT_CERT_PATH);
		} catch (PortalException | SystemException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void supplierPromotion(){
		SmSupplier userSupplier = getSupplierProfileByUserId();
		if(userSupplier != null){
			ISupplierSearchSO supplierSearchSO = SmServiceLookup.getSupplierSearchSO();
			SupplierSearchReqDTO suppSearchReqDTO = new SupplierSearchReqDTO();
			suppSearchReqDTO.setSupplierId(userSupplier.getSupplierId());
			SupplierSearchResDTO suppSearchResDTO = supplierSearchSO.retriveSupplierById(suppSearchReqDTO);
			
			if(validMofUser()){
				log.info("MOF_SUPPLIER_ADMIN>>>>");
				mofUserAction(suppSearchResDTO.getSmSupplier());

			}else if(validBasicSupplier()){
				basicSupplierAction();

			}
		}
		
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, PROMO_PAGE);
	}
	
	private String checkCatalogueUpload(Long supplierId){
		List<CmSupplierItem> suppItems = getCmSupplierItems(supplierId);
		log.info("SupplierItems size:>>>>> " + suppItems);
		String catalogueStatus = null;
				
		
		if(suppItems.size() > 0){
			for(CmSupplierItem cmSuppItem : suppItems){
				if(cmSuppItem.getItemStatus().equalsIgnoreCase("U")){
					catalogueStatus = "U";
					break;
				}
				
				if(cmSuppItem.getItemStatus().equalsIgnoreCase("P")){
					catalogueStatus = "P";
				}
			}
		}
		
		return catalogueStatus;
	}
	
/*	private void initCatalogueBackingBean(){
		CatalogueBackingBean cBB = new CatalogueBackingBean();
		cBB.preRenderCatalogue();
	}*/
	
/*	
	private void initSoftcertBackingBean(){
		SoftCertBackingBean scBB = new SoftCertBackingBean();
		scBB.preRenderSoftCert();
	}*/
	
/*	public void preRenderView(){
		//if(this.getManagedBean().getSmSupplier() == null){
			pageNavigation();
		//}
	}*/
}
