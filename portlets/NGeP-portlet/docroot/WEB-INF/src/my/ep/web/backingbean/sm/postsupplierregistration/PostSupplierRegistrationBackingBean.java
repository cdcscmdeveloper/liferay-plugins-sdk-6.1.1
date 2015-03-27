package my.ep.web.backingbean.sm.postsupplierregistration;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
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
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
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
	private static final String CATALOGUE_UPLOAD_PATH = "codification-search";
	private static final String SUPPLIER_TYPE_SELECTION_PATH = "/xhtml/sm/postsupplierregistration/supplierTypeSelection.xhtml";
	private static final String SOFT_CERT_PATH = "request-soft-cert";
	private static final String MOF_REG_PATH = "mof-registration";
	private static final String TASK_LIST_PATH = "task-list";
	
	public static Logger log = Logger.getLogger(PostSupplierRegistrationBackingBean.class);
	
	public PostSupplierRegistrationBackingBean() {

	}
	
	public String currentStatusAndPath(boolean isPath){
		SmSupplier supplier = getSupplierProfile();
		String path = null;
		String desc = null;

		if(supplier != null){
			log.info("Supplier is not null===>" + supplier.getSupplierId());
			this.getManagedBean().setSmSupplier(supplier);

			SmAppl appl = retrieveInProcessAppl(supplier);

			if(appl != null){
				log.info("*****In Progress*********");
				SmWorkflowStatus workFlowStatus = getSmWorkflowStatus(appl);
				desc = workFlowStatus.getPmStatus().getPmStatusDescs().get(0).getStatusName();
				desc = this.getStatusDescById(workFlowStatus.getPmStatus().getStatusId());
				path = TASK_LIST_PATH;
				
				
			}else{
				if(supplier.getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.TEMP_TYPE) || 
						supplier.getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.TEMP_TYPE)){
					path = SUPPLIER_TYPE_SELECTION_PATH;
					desc = "Supplier Type selection";
					desc = getLabelFromProperties("lbl.sm.basic.supplier.selection");
				}else{
					log.info("Supplier appl type===>" + supplier.getSmAppl().getApplType());
					if(supplier.getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)){
						path = MOF_REG_PATH;
						desc = getLabelFromProperties("lbl.sm.basic.mof.registration"); 
					}else{
						log.info("****Basic********");
						List<CmSupplierItem> suppItems = getCmSupplierItems(supplier.getSupplierId());
						log.info("*****SUPP ITEMS LIST******" + suppItems.size());

						if(suppItems.size() < 1){
							path = CATALOGUE_UPLOAD_PATH;
							desc = getLabelFromProperties("lbl.sm.basic.catalogue.upload");
						}else{
							/*
							 * check the supplier item status
							 */
							boolean publishedStatus = false;
							boolean pending = false;
							for(CmSupplierItem cmSuppItem : suppItems){
								log.info("****Supplier Item*****" + cmSuppItem.getItemStatus());
								if(cmSuppItem.getItemStatus().equalsIgnoreCase("U")){
									publishedStatus = true;
								}else{
									if(cmSuppItem.getItemStatus().equalsIgnoreCase("P")){
										pending = true;
									}
								}
							}

							if(!publishedStatus){
								log.info("********is Not Published******");
								if(pending){
									path = CATALOGUE_UPLOAD_PATH;
									desc = getLabelFromProperties("lbl.sm.basic.catalogue.pending.approval"); 
								}else{
									path = CATALOGUE_UPLOAD_PATH;
									desc = getLabelFromProperties("lbl.sm.basic.catalogue.upload"); 
								}
							}else{
								log.info("********Published Catalogue******");
								path = SOFT_CERT_PATH;
								desc = getLabelFromProperties("lbl.sm.basic.softcert.request"); 
							}
						}
					}
				}

			}
			if(isPath){
				return path;
			}else{
				return desc;
			}
		}

		return null;
	}
	
	public void preRender(){
		String currStatus = currentStatusAndPath(false);
		log.info("******PreRender****" + currStatus);
		this.getManagedBean().setStatusDesc(currStatus);
	}
	
	public void pageNavigate() throws Exception{
		if(isHavePendingTaskList()){
			pageRedirect(TASK_LIST_PATH);
			
		}else{
			String path = currentStatusAndPath(true);
			log.info("******xhtml*******" + path);
			
			if(path.equalsIgnoreCase(SUPPLIER_TYPE_SELECTION_PATH)){
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, path);
			}else{
				try{
					pageRedirect(path);
				}
				catch(Exception ex){
					throw new Exception(ex.getMessage());
				}
			}
		}
		
	}
	
	
	public String getStatusDesc(){
		return getManagedBean().getStatusDesc();
	}
	
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
			}
			
			RegistrationReqDTO reqDTO = new RegistrationReqDTO();
			reqDTO.setSmSupp(getManagedBean().getSmSupplier());
			IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IRegistrationSO.class);
			try{
				RegistrationResDTO resDTO = reqSO.updateOnlineAcc(reqDTO);
				log.info("--------Supplier------------" + this.getManagedBean().getSmSupplier().getBusinessType());
				
				if(resDTO.isSuccessful()){
					FacesContext fc = FacesContext.getCurrentInstance();
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
	private SmSupplier getSupplierProfile(){
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		log.info("***pmUser name***" + pmUser.getUserName());
		log.info("***pmUser id***" + pmUser.getUserId());
		
		if(pmUser != null){
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			supplierSearchReqDTO.setUserId(pmUser.getUserId());

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
			SupplierSearchResDTO smResDTO =supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
			SmSupplier smSupp = (SmSupplier)smResDTO.getResultBean().getResultList().get(0);
			
			log.info("------getSupplierProfile--------" + smSupp.getCompanyName());
			
			return smSupp;
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
	
	public void pageRedirect(String url)  throws IOException,PortalException, SystemException {
		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay) facesScope.getRequestScope(WebKeys.THEME_DISPLAY);
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String commonUrl = themeDisplay.getURLCurrent().substring(0,themeDisplay.getURLCurrent().indexOf(
						themeDisplay.getLayout().getGroup().getFriendlyURL())+ themeDisplay.getLayout().getGroup().getFriendlyURL().length());
		externalContext.redirect(commonUrl + "/" + url);
	}
	
	private SmAppl retrieveInProcessAppl(SmSupplier supplier){
		IMofRegistrationSO mofRegSO = SmServiceLookup.getMofRegistrationSO();
		MofRegistrationReqDTO mofRegistrationReqDTO = new MofRegistrationReqDTO();
		mofRegistrationReqDTO.setSmSupplier(supplier);
		mofRegistrationReqDTO.setAppType(ParameterConst.NEW_SM_APPLN_FOR_MOF);
		
		MofRegistrationResDTO mofRegistrationResDTO = mofRegSO.retrieveLatestSmApplicationDraft(mofRegistrationReqDTO);
		
		return mofRegistrationResDTO.getSmAppl();
	}
	
	private SmWorkflowStatus getSmWorkflowStatus(SmAppl appl){
		ISmWorkFlowStatusSO workFlowSO = SmServiceLookup.getSmWorkFlowStatusSO();
		SmWorkFlowStatusReqDTO reqDTO = new SmWorkFlowStatusReqDTO();
		reqDTO.setApplId(appl.getApplId());
		SmWorkFlowStatusResDTO resDTO = workFlowSO.retrieveCurrentWorkFlowStatusByDocId(reqDTO);
		
		return resDTO.getWorkflowStatus();
		
	}
	
	private String getLabelFromProperties(String labelStr){
		Locale locale = new FacesScopes().getUserLocale();
		String label = ResourceBundleUtils.getMessage(labelStr, ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		
		return label;
	}
	
/*	private void initCatalogueBackingBean(){
		CatalogueBackingBean cBB = new CatalogueBackingBean();
		cBB.preRenderCatalogue();
	}
	
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
