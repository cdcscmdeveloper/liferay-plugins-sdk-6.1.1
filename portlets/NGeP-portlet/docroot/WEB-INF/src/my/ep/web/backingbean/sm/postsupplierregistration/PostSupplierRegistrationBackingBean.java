package my.ep.web.backingbean.sm.postsupplierregistration;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.upgraderole.IUpgradeRoleSO;
import my.ep.biz.service.sm.workFlowStatus.ISmWorkFlowStatusSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.osb.sm.SMOsbReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.payment.PaymentReqDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.py.PyBill;
import my.ep.persistence.entity.py.PyPaymentOrder;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.postsupplierregistration.PostSupplierRegistrationManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

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
	private static final String CONTENT_PAGE = "/xhtml/sm/postsupplierregistration/msgContent.xhtml";
	private static final String PROMO_PAGE = "/xhtml/sm/postsupplierregistration/supplierPromo.xhtml";
	private static final String ACKNOWLEDGEMENT_PAGE = "/xhtml/sm/postsupplierregistration/postRegCompleteAcknowlegement.xhtml";
	
	private static final String MOF_SOFTCERT_REQUEST = "request-soft-cert";
	//private static final String MOF_CODIFICATION_SEARCH = "codification-search";
	private static final String UPGRADE_TO_MOF = "upgrade-basic-to-mof";
	private static final String VIEW_VIRTUAL_CERT = "view-virtual-certification-temp";
	//private static final String VIEW_VIRTUAL_CERT = "virtual-cert";
	private static final String RESUBMIT_APPLICATION = "resubmit-application?apId=";
	private static final String CATALOGUE_MAINTENANCE_PAGE = "catalogue-maintenance-file";
	private static final String RENEW_MOF = "renew-mof-account";
	
	private int softCertStatus;
	private boolean changeRoleSection;
	private boolean roleChangeSuccess;
	
	private static final String CATALOGUE_PUBLISHED = "U";
	
	public static Logger log = Logger.getLogger(PostSupplierRegistrationBackingBean.class);
	
	public PostSupplierRegistrationBackingBean() {

	}
	
	public String currentStatusAndPath(){
		log.info("Here Enter Funtion currentStatusAndPath ===>");
		SmSupplier userSupplier = getSupplierProfileByUserId();
		String path = null;
		String desc = null;
		Boolean isRejApplExpired = false;

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

			SmWorkflowStatus rejectedWf = rejectedApplExist(supplier.getSupplierId());
			boolean isRejectedApplExist = (rejectedWf == null) ? false : true;
			//30713 - Start
			//Add checking for expired process fee KN
			SmWorkflowStatus currWorkflowStatus = isLatestActiveWorkFlow(supplier.getSupplierId());
			if(rejectedWf != null && currWorkflowStatus.getPmStatus().getStatusId().toString().equalsIgnoreCase(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM_PROCESSING_FEE) 
					&& my.ep.constant.sm.SupplierManagementConst.APPL_TYPE_MOF.equalsIgnoreCase(supplier.getSmAppl().getApplType())){
				Timestamp currentDate = new Timestamp(System.currentTimeMillis());
				long diff = currentDate.getTime()
						- rejectedWf.getCreatedDate().getTime();
				long diffDays = diff / (24 * 60 * 60 * 1000);
				//if expired
				if (diffDays >= SmRuleHelper.getInstance()
						.getApplicationResubmissionDays()) {
					getManagedBean().setRejectedApplExist(false);
					isRejectedApplExist = false;
					rejectedWf = null;
				}
			}
			//30713 - End
			boolean successPayment = checkSuccessPaymentBySupplierId(supplier.getSupplierId());
			
			//Self Request Supplier Admin
			if(supplier.getRecordStatusFlg()==SupplierManagementConst.SUPPLIER_SELF_REQUEST_ADMIN_STATUS){
				path = basicAccountFlow(supplier);
			}else{
				//Active Appl
				if(appl != null){
					log.info("*****In Progress*********" + isRejectedApplExist);			
					SmWorkflowStatus workFlowStatus = getSmWorkflowStatus(appl);
					this.getManagedBean().setStatusDesc(getStatusDescById(workFlowStatus.getPmStatus().getStatusId()));
					log.info("*****workFlowStatus*********" + getManagedBean().getStatusDesc() + " " + workFlowStatus.getPmStatus().getStatusId());
					
					//Enhancement #20467
					if(rejectedWf != null){
						log.info("************** currentStatusAndPath: rejectedWf.getDocId() = " + rejectedWf.getDocId());
						SmAppl rejAppl = new SmAppl();
						rejAppl.setApplId(rejectedWf.getDocId());
						
						//to check rejected appl expired or not
						isRejApplExpired = checkRejectedApplExpired(rejAppl);				
						log.info("************** currentStatusAndPath: isRejApplExpired = " + isRejApplExpired);
						
						//if rejected appl expired, treat as new appl
						if(isRejApplExpired) {
							isRejectedApplExist = false;
						}
					}

					if(successPayment){
						setSuccessPaymentFlag();
						
						//to check whether is resubmit
						if(appl.isResubmit()){
							getManagedBean().setRejectedApplExist(true);
						}else{
							//if refId for processing fee not equeal to current workflow doc id
							//treat is as rejectedAppl
							PyBill pyBill = getSuccessPaymentApplIdBySupplierId(supplier.getSupplierId());
							if(pyBill != null){
								if(pyBill.getBillRefId() == workFlowStatus.getDocId()){
									getManagedBean().setRejectedApplExist(false);
									successPayment = false;
								}else{
									if(rejectedPaymentExpired(supplier.getSupplierId()).size() > 0){
										// to avoid resubmit flow when supplier rejected by system during Registration fee(20119)
										getManagedBean().setRejectedApplExist(false);
									}
									//if rejected appl expired, user need to pay processing fee
									if(isRejApplExpired) {
										getManagedBean().setSuccessPayment(false);
									}
								}
							}else{
								getManagedBean().setRejectedApplExist(false);
								if(isRejApplExpired) {
									getManagedBean().setSuccessPayment(false);
								}
							}
						}
					}
					
					showHyperlink(workFlowStatus.getPmStatus().getStatusId(),isRejectedApplExist,successPayment);
					path = TASK_LIST_PATH;
					log.info("*****Flow Status*********" + getManagedBean().getFlowStatus());	
					
					if(workFlowStatus.getPmStatus().getStatusId().longValue() == SupplierManagementConst.PM_STATUS_PENDING_APPLICATION_DECLARATION_L){
						if(isRejApplExpired) {
							getManagedBean().setFlowStatus(1);
						} else if(rejectedWf != null){
							validPaymentForResubmission(supplier.getSupplierId(), rejectedWf.getDocId());
						}
					}
					
					if(isRejApplExpired) {
						setExpiredRejectedApplFlag();
					}

				}else{
					//MOF APPL
					if(supplier.getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)){
						SmWorkflowStatus workFlowStatus = getSmWorkflowStatus(supplier.getSmAppl());
						if(workFlowStatus != null && 
							(workFlowStatus.getPmStatus().getStatusId().longValue() == my.ep.constant.sm.SupplierManagementConst.PM_STATUS_REGISTERED || 
							workFlowStatus.getPmStatus().getStatusId().longValue() == my.ep.constant.sm.SupplierManagementConst.PM_STATUS_SV_ASSIGNMENT_AFTER_APPROVAL || 
							workFlowStatus.getPmStatus().getStatusId().longValue() == my.ep.constant.sm.SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT)){
							if(successPayment){
								getManagedBean().setSuccessPayment(true);
							}
							
							setRejectApplFlagByPayment(supplier,workFlowStatus);
							
							path = mofSupplierFlowAfterApproval(supplier,getManagedBean().isRejectedApplExist());
						}else{
							SmWorkflowStatus wfStatus = isLatestActiveWorkFlow(supplier.getSupplierId());
							if(wfStatus != null){
								PmStatus wfStat = wfStatus.getPmStatus();
								if(wfStat != null && 
									(wfStat.getStatusId().longValue() == SupplierManagementConst.PM_STATUS_PO_REJECTED_APPLICATION || 
									wfStat.getStatusId().longValue() == SupplierManagementConst.PM_STATUS_APPROVER_REJECTED_APPLICATION ||
									wfStat.getStatusId().longValue() == SupplierManagementConst.PM_STATUS_REJECTED_BY_SYS)){
									
									log.info("*******Resubmit********");
									//path = RESUBMIT_APPLICATION + wfStatus.getDocId();
									getManagedBean().setFlowStatus(3);
									path = validPaymentForResubmission(supplier.getSupplierId(), rejectedWf.getDocId());
									
									if(wfStat.getStatusId().longValue() == SupplierManagementConst.PM_STATUS_REJECTED_BY_SYS){
										log.info("*******PM_STATUS_REJECTED_BY_SYS********");
										if(!checkSuccessPaymentBySupplierId(supplier.getSupplierId())){
											log.info("*******No SUCCESS PAYMENT********");
											getManagedBean().setRejectedApplExist(false);
											path = MOF_REG_PATH;
											getManagedBean().setFlowStatus(1);
											getManagedBean().setDisplayRejectLabel(true);
										}
									}
								}else{
									getManagedBean().setRejectedApplExist(false);
									log.info("*******Not Resubmit********");
									path = MOF_REG_PATH;
									getManagedBean().setFlowStatus(1);
									
									if(rejectedWf != null){
										path = validPaymentForResubmission(supplier.getSupplierId(), rejectedWf.getDocId());
									}
								}
							}else{
								path = MOF_REG_PATH;
								getManagedBean().setFlowStatus(1);
							}
						}
						log.info("********FLOW STATUS**********" +  getManagedBean().getFlowStatus());
					}else if(supplier.getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.BASIC_ACCOUNT)){
						path = basicAccountFlow(supplier);
					}
				}
			}
			
		}

		return path;

	}
	
	public void preRender(){
		String currStatus = currentStatusAndPath();
		log.info("******PreRender****" + currStatus);
		//this.getManagedBean().setStatusDesc(currStatus);
		
		String pageRedirect = null;
		
		if(changeRoleSection){
			pageRedirect= ACKNOWLEDGEMENT_PAGE;
		}else{
			pageRedirect= CONTENT_PAGE;
		}

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, pageRedirect);
	}
	
	public void pageNavigate() throws Exception{
		SmSupplier supplier = getSupplierProfileByUserId();
		//START 27264
		boolean proceed = true;
		if(supplier != null){
			try{
				
				String path1 = currentStatusAndPath();
				//27214
				if(path1 != null && (MOF_REG_PATH.equalsIgnoreCase(path1) || path1.contains(RESUBMIT_APPLICATION) || path1.contains(CATALOGUE_UPLOAD_PATH) || path1.contains(SOFT_CERT_PATH) || path1.contains(MOF_SOFTCERT_REQUEST) || path1.contains(CATALOGUE_MAINT))){
					//28454
					if(path1.contains(RESUBMIT_APPLICATION)){
						this.getManagedBean().setResubmitAppl(true);
					}
					log.info("pageNavigate------------"+path1);
					checkSSMValid(supplier,this.getManagedBean());
					log.info("this.getManagedBean().getPersonnelsRole()------------"+this.getManagedBean().getPersonnelsRole());
					log.info("this.getManagedBean().isCheckRoleSSM()------------"+this.getManagedBean().isCheckRoleSSM());
					if(this.getManagedBean().getPersonnelsRole()!= null && this.getManagedBean().getPersonnelsRole().size() > 0 && !this.getManagedBean().isCheckRoleSSM()){
						proceed =false;
						//Start 27951
						if(this.getManagedBean().getSmSupplier().getSmAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)){
							this.getManagedBean().setPreRegistration(true);
						}else{
							this.getManagedBean().setPreRegistration(false);
						}
						//End 27951
						RequestContext.getCurrentInstance().execute("blockWidget.hide()");
						RequestContext.getCurrentInstance().execute("ownerCheckingWidgetVar.show()");
						SmCommonUtil.redirectToPage(
								CONTENT_PAGE,
								"",new String[] {""});
					}
				}				
			}catch(Exception w){
				log.info("Here done--"+w.getMessage());
				proceed =false;
				RequestContext.getCurrentInstance().execute("blockWidget.hide()");
				if(this.getManagedBean() != null && this.getManagedBean().getStatusDescFromSSM()!=null){
					SmCommonUtil.redirectToPage(
							CONTENT_PAGE,
							w.getMessage(),
							new String[] { SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null) });
				}else{
					SmCommonUtil.redirectToPage(
							CONTENT_PAGE,
							w.getMessage(),new String[] {""});
				}
								
			}
						
		}
		if(proceed){
			if(isHavePendingTaskList() && supplier.getRecordStatusFlg()!=SupplierManagementConst.SUPPLIER_SELF_REQUEST_ADMIN_STATUS){
				pageRedirect(TASK_LIST_PATH);
				
			}else{
				String path = currentStatusAndPath();
				log.info("******xhtml*******" + path);
				pageRedirect(path);
			}
		}
		//END 27264
		
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
			SmSupplier smSupp = SmApplicationUtil.getCurrentSupplier();
			if(smSupp != null){
				log.info("------getSupplierProfile--------" + smSupp.getCompanyName());
				return smSupp;
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
		if (getManagedBean().getToken() == null) {
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
	 * show/ Hide the hyperlink based on statusCode
	 * @param statusCode
	 */
	private void showHyperlink(Long statusCode, boolean isRejected, boolean isResubmit){
		Integer sc = statusCode.intValue();
		
		switch(sc){
		case 20100 : //Pending Application Declaration
			getManagedBean().setFlowStatus(1);
			getManagedBean().setHyperlink(true);
			
			if(isRejected){
				getManagedBean().setFlowStatus(3);
				getManagedBean().setHyperlink(true);
			}
			break;

		case 20114 : //Pending Payment (Processing fee)
			getManagedBean().setFlowStatus(2);
			getManagedBean().setHyperlink(true);
			break;

		case 20200 : //Pending Application Inquiry
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(true);
			
			if(isRejected){
				getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() +1);
				
				if(!isResubmit){
					getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() -1);
				}
			}
			break;

		case 20251 : //Pending Payment
			getManagedBean().setFlowStatus(4);
			getManagedBean().setHyperlink(true);
			
			if(isRejected){
				getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() +1);
				
				if(!isResubmit){
					getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() -1);
				}
			}
			break;
			
			
		case 20101 : //Pending Supporting Document Verification
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			
			if(isRejected){
				getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() +1);
				
				if(!isResubmit){
					getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() -1);
				}
			}
			break;
		
		case 20102 : //Pending Application Verification
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			
			if(isRejected){
				getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() +1);
				
				if(!isResubmit){
					getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() -1);
				}
			}
			break;
		
		case 20103 : //Pending Review Site Visit Report - Before Approval
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			if(isRejected){
				getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() +1);
				
				if(!isResubmit){
					getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() -1);
				}
			}
			break;

		case 20104 : //Pending Review Application
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			
			if(isRejected){
				getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() +1);
				
				if(!isResubmit){
					getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() -1);
				}
			}
			break;
			
		case 20105 : //Pending Site Visit
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			
			if(isRejected){
				getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() +1);
				
				if(!isResubmit){
					getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() -1);
				}
			}
			break;
		
		case 20106 : //Pending Re-Site Visit
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			
			if(isRejected){
				getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() +1);
				
				if(!isResubmit){
					getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() -1);
				}
			}
			break;
			
		case 20107 : //Pending Reply Inquiry by Processing Officer
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(true);
			
			if(isRejected){
				getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() +1);
				
				if(!isResubmit){
					getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() -1);
				}
			}
			break;
		
		case 20108 : //Pending Application Approval
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			
			if(isRejected){
				getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() +1);
				
				if(!isResubmit){
					getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() -1);
				}
			}
			break;
			
		case 20109 : //Pending Application Re-Approval
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			
			if(isRejected){
				getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() +1);
				
				if(!isResubmit){
					getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() -1);
				}
			}
			break;
		
		case 20110 : //Pending Site Visit Assignment - Before Approval
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			
			if(isRejected){
				getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() +1);
				
				if(!isResubmit){
					getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() -1);
				}
			}
			break;
		
		case 20111 : //Pending Site Visit Assignment - Before Approval
			getManagedBean().setFlowStatus(3);
			getManagedBean().setHyperlink(false);
			
			if(isRejected){
				getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() +1);
				
				if(!isResubmit){
					getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() -1);
				}
			}
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
			
			String catalogue = checkCatalogueUpload(supplier.getSupplierId()); 
			if(catalogue == null){
				//mofActionMap.put("CU", CATALOGUE_UPLOAD_PATH);
				mofActionMap.put("CU", CATALOGUE_MAINTENANCE_PAGE);
			}else{
				if(!catalogue.equalsIgnoreCase(CATALOGUE_PUBLISHED)){
					mofActionMap.put("CU",CATALOGUE_MAINTENANCE_PAGE);
				}
			}
		}
		//#30053 - Start
		if(supplier.getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)){
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			if(pmUser != null){
				MofAccountReqDTO mofReqDTO = new MofAccountReqDTO();
				mofReqDTO.setLoginId(pmUser.getLoginId());
				Integer exisitingBasicMOFFlg = SmServiceLookup.getMofAccountSO().checkExpiredDowngradedToBasic(mofReqDTO).getBasicMOFRenewFlg();
				log.info("This is the value of the integer" + exisitingBasicMOFFlg);
				if (exisitingBasicMOFFlg != my.ep.constant.sm.SupplierManagementConst.RECORD_STATUS_DELETED) {
					if (exisitingBasicMOFFlg == my.ep.constant.sm.SupplierManagementConst.MOF_CAN_RENEW) {
						mofActionMap.put("RNMOF", RENEW_MOF);
					}
				}
			}	
		}
		//#30053 - End
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
		FacesScopes fs = new FacesScopes();
		if(fs.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()) || fs.hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())){ 
			return true;
		}
		return false;
	}
	
	/**
	 * Action for basic supplier admin
	 */
	private void basicSupplierAction(Long supplierId){
		Map<String,String> basicActionMap = new HashMap<String, String>();
		
		//String msg = "Upgrade to MOF Account to enjoys more benefits.";
		//String msg = getLabelFromProperties("lbl.sm.mof.upgrade.image");
		String catalogue = checkCatalogueUpload(supplierId); 
		
		if(catalogue == null){
			basicActionMap.put("CU", CATALOGUE_MAINTENANCE_PAGE);
		}else{
			if(!catalogue.equalsIgnoreCase(CATALOGUE_PUBLISHED)){
				basicActionMap.put("CU",CATALOGUE_MAINTENANCE_PAGE);
			}
		}
		
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if(pmUser != null){
			MofAccountReqDTO mofReqDTO = new MofAccountReqDTO();
			mofReqDTO.setLoginId(pmUser.getLoginId());
			Integer exisitingBasicMOFFlg = SmServiceLookup.getMofAccountSO().checkExpiredDowngradedToBasic(mofReqDTO).getBasicMOFRenewFlg();
			
			//start 28564
			if (exisitingBasicMOFFlg != my.ep.constant.sm.SupplierManagementConst.RECORD_STATUS_DELETED) {
				//end 28564
				if (exisitingBasicMOFFlg == my.ep.constant.sm.SupplierManagementConst.MOF_CAN_RENEW) {
					basicActionMap.put("RNMOF", RENEW_MOF);
				} else {
					basicActionMap.put("UPMOF", UPGRADE_TO_MOF);
				}
			}

		}
		getManagedBean().setUserActionMap(basicActionMap);
	}
	
	
	/**
	 * Validate User is Supplier Temp
	 * @return boolean
	 */
	public boolean validSupplierTemp(){
		log.info("*******validBasicSupplierAdmin********");
		if(new FacesScopes().hasRoleUser(RoleCode.SUPPLIER_TEMP.value())){ 
			log.info("*check**this is supplier temp user********");

			return true;
		}
		return false;
	}
	
	/**
	 * Navigate to softcert Request
	 */
	public void naviToSoftCertRequest(){
		try {
			pageRedirect(SOFT_CERT_PATH);
		} catch (PortalException | SystemException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * preRender of supplier promotion banner
	 */
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
				setMofExpiryMsg(userSupplier);

			}else
				if(validBasicSupplier()){
				basicSupplierAction(userSupplier.getSupplierId());

			}
		}
		
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, PROMO_PAGE);
	}
	
	/**
	 * Set MOF Expiry Date notification message
	 * @param SmSupplier supp
	 */
	private void setMofExpiryMsg(SmSupplier supp) {
		SmMofAccount acc = SmCommonUtil.getMofAccountBySuppIdAndSuppType(
				supp.getSupplierId(), supp.getSmAppl().getSupplierType());
		if (acc != null
				&& acc.getExpDate() != null
				&& acc.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
			Date currDate = new Date();
			Calendar currDateAfterThreeMonths = Calendar.getInstance();
			currDateAfterThreeMonths.setTime(currDate);
			currDateAfterThreeMonths.add(Calendar.MONTH, 3);
			if (currDateAfterThreeMonths.getTime().after(acc.getExpDate())) {
				getManagedBean().setMofExpDateMsg(DateUtils.dateToString("dd-MM-yyyy",
						acc.getExpDate()));
			}
		}
	}
	
	/**
	 * check supplier's catalogue
	 * @param supplierId
	 * @return String
	 */
	private String checkCatalogueUpload(Long supplierId){
		List<CmSupplierItem> suppItems = getCmSupplierItems(supplierId);
		log.info("checkCatalogueUpload Supplier id>>>>>>" + supplierId);
		log.info("checkCatalogueUpload SupplierItems size:>>>>> " + suppItems.size());
		String catalogueStatus = null;
				
		
		if(suppItems.size() > 0){
			for(CmSupplierItem cmSuppItem : suppItems){
				if(cmSuppItem.getItemStatus().equalsIgnoreCase(CATALOGUE_PUBLISHED)){
					catalogueStatus = CATALOGUE_PUBLISHED;
					break;
				}else{
					catalogueStatus = cmSuppItem.getItemStatus();
				}
			}
		}
		
		return catalogueStatus;
	}
	
	/**
	 * retrieve current user soft cert status
	 * @param 
	 * @return SmPersonnel
	 */
	public SmPersonnel retrieveCurrentPersonnel(){
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		IPersonnelSO reqSO = SmServiceLookup.getPersonnelSO();
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setUserId(pmUser.getUserId());
		PersonnelResDTO resDTO = reqSO.retrievePersonnelByUserId(reqDTO);
		
		return resDTO.getSmPersonnel();
	}
	
	public int softCertProsseingStatus(){
		return my.ep.constant.sm.SupplierManagementConst.SOFT_CERT_PROCESSING;
	}
	
	public int softCertIsActiveStatus(){
		return my.ep.constant.sm.SupplierManagementConst.SOFT_CERT_ISSUED;
	}
	
	public int softCertRejectedStatus(){
		return my.ep.constant.sm.SupplierManagementConst.SOFT_CERT_REJECTED;
	}
	
	public int softCertPendingReqStatus(){
		return my.ep.constant.sm.SupplierManagementConst.SOFT_CERT_PENDING_REQ;
	}
	
	private void changeRoleForSupplierTemp(Long personnelId, SmSupplier supplier){
		log.info("*****changeRoleForSupplierTemp - start******");
		changeRoleSection = true;
		IPersonnelSO personnelSO = SmServiceLookup.getPersonnelSO();
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setPersonnelId(personnelId);
		reqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
		
		try{
			PersonnelResDTO resDTO =  personnelSO.supplierTempChangeRole(reqDTO);
			//PersonnelResDTO resDTO = new PersonnelResDTO();
			//resDTO.setSuccessful(true);
			if(resDTO.isSuccessful()){
				roleChangeSuccess = true;
				String msgId = null;
				
				if(supplier.getSmAppl().getApplType()
						.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.APPL_TYPE_MOF)){
					msgId = MessageConst.INFO_721035;
				}else{
					if(supplier.getBusinessType()
							.equals(my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_GOVT_SELLER)){
						msgId = MessageConst.INFO_721038;
					}else{
						msgId = MessageConst.INFO_721039;
					}
				}

				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(msgId,
						MessageUtils.BUNDLE_COMMON_MESSAGES,
						new FacesScopes().getUserLocale())));	

			}
		}catch(Exception ex){
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,null,ex.getMessage()));	
		}
		
	}
	
	public int retrieveCurrentUserSoftCertStatus(){
		return softCertStatus;
	}
	
	private String mofSupplierFlowAfterApproval(SmSupplier supplier, boolean isRejectedApplExist){
		String path = null;
		SmPersonnel currentPersonnel = retrieveCurrentPersonnel();
		log.info("-------mofSupplierFlowAfterApproval supplier type-------------" + supplier.getSmAppl().getSupplierType());
		if(!supplier.getSmAppl().isQuestionnaire()){
			getManagedBean().setFlowStatus(5);
			
			if(isRejectedApplExist){
				getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() +1);
			}
			
			log.info("**********flow Status*********" + getManagedBean().getFlowStatus());
			path =  VIEW_VIRTUAL_CERT;
		}else{
			if(supplier.getSmAppl().getSupplierType().equalsIgnoreCase(ParameterConst.SUPPLIER_TYPE_CONTRACTOR)){
				softCertStatus = currentPersonnel.getSoftcertFlg();
				if(supplier.getSmAppl().getSmCompanyBasic().isWithFederal()){
					if(!isPublishedCatalogue(supplier.getSupplierId())){
						if(getCmSupplierItems(supplier.getSupplierId()).size() > 0){
							path = CATALOGUE_MAINT;
						}else{
							path = CATALOGUE_UPLOAD_PATH;
						}
						getManagedBean().setFlowStatus(6);
						
						if(isRejectedApplExist){
							getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() +1);
						}
					}else{
						if(checkPendingSoftCertRequest(supplier.getSupplierId())){
							path = SOFT_CERT_PATH;
							//getManagedBean().setFlowStatus(7);
						}else{
							if(softCertStatus == SupplierManagementConst.SOFT_CERT_REJECTED){
								path = SOFT_CERT_PATH;
							}
							path = SOFT_CERT_PATH;
							//getManagedBean().setFlowStatus(7);
						}
						getManagedBean().setFlowStatus(7);
						if(isRejectedApplExist){
							getManagedBean().setFlowStatus(getManagedBean().getFlowStatus() +1);
						}
					}


					if(isPublishedCatalogue(supplier.getSupplierId()) && validSupplierTemp() && softCertStatus == SupplierManagementConst.SOFT_CERT_ISSUED){
						log.info(">>>CHange ROle for SUpplier Temp<<");
						changeRoleForSupplierTemp(currentPersonnel.getPersonnelId(),supplier);
					}
				}else{
					log.info(">>>Contractor<<<");
					if(validSupplierTemp()){
						log.info(">>>CHange ROle for SUpplier Temp<<");
						changeRoleForSupplierTemp(currentPersonnel.getPersonnelId(),supplier);

					}
				}
			}else{
				log.info(">>>Consultant<<");
				if(validSupplierTemp()){
					log.info(">>>CHange ROle for SUpplier Temp<<");
					changeRoleForSupplierTemp(currentPersonnel.getPersonnelId(),supplier);
				}
			}
		}

		return path;
	}
	
	private String basicAccountFlow(SmSupplier supplier){
		String path = null;
		
		//Basic Applcation
		//List<CmSupplierItem> cmSuppItemLs = getCmSupplierItems(supplier.getSupplierId());
		SmPersonnel currentPersonnel = retrieveCurrentPersonnel();
		softCertStatus = currentPersonnel.getSoftcertFlg();
		boolean isG2G = supplier.getBusinessType().
				equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_GOVT_SELLER) ? true : false;
		boolean isWithFederal = currentPersonnel.getSmAppl().getSmCompanyBasic().isWithFederal();
		log.info("isWithFederal --> " + isWithFederal );
		log.info("softCertStatus --> " + softCertStatus );
		//Start 26288 - DA - 29602
		if(!isWithFederal && !isG2G && !SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL.equalsIgnoreCase(supplier.getBusinessType())){ //assumption non-federal has no published catalog
			log.info("this is basic not g2g");
			if(!isPublishedCatalogue(supplier.getSupplierId())){
				if(getCmSupplierItems(supplier.getSupplierId()).size() > 0){
					path = CATALOGUE_MAINT;
				}else{
					path = CATALOGUE_UPLOAD_PATH;
				}
				getManagedBean().setFlowStatus(1);
			}else if(validSupplierTemp()){
				if(softCertStatus == SupplierManagementConst.SOFT_CERT_PENDING_REQ && !isWithFederal){
					log.info(">>>CHange ROle for SUpplier Temp<<");
					changeRoleForSupplierTemp(currentPersonnel.getPersonnelId(),supplier);
				}				
			}
		//26238 - Basic account skip upload catalogue
		}else if(!isG2G && (isWithFederal || SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL.equalsIgnoreCase(supplier.getBusinessType()))&& !isPublishedCatalogue(supplier.getSupplierId())){
			if(getCmSupplierItems(supplier.getSupplierId()).size() > 0){
				path = CATALOGUE_MAINT;
			}else{
				path = CATALOGUE_UPLOAD_PATH;
			}
			getManagedBean().setFlowStatus(1);
		}else{
			log.info("this is basic g2g");

			if(checkPendingSoftCertRequest(supplier.getSupplierId())){
				path = SOFT_CERT_PATH;
				getManagedBean().setFlowStatus(2);
			}else{
				if(softCertStatus == SupplierManagementConst.SOFT_CERT_REJECTED){
					path = SOFT_CERT_PATH;
				}
				// #25384: happen if the previous admin already request
				// softcert and replaced by this new admin which after request
				// need to do softcert payment.
				else if (softCertStatus == SupplierManagementConst.IS_SOFTCERT_REQ) {
					path = TASK_LIST_PATH;
				}
				
				getManagedBean().setFlowStatus(2);
			}
			//End 26288 - DA - 29602
			log.info("softCertStatus---->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+softCertStatus);
			if(validSupplierTemp() && softCertStatus == SupplierManagementConst.SOFT_CERT_ISSUED){
				log.info(">>>CHange ROle for SUpplier Temp<<");
				changeRoleForSupplierTemp(currentPersonnel.getPersonnelId(),supplier);
			}
		}
		
		return path;
	}
	
	/**
	 * Navigate to Dash Board
	 * @throws IOException
	 */
	public void naviToDashBoard() throws IOException{
		log.info("<<<<<< naviToDashBoard >>>>>");
		System.out.println("<<<<<< test >>>>>");
		String currentUrl = SmCommonUtil.getEpBaseUrl();
		log.info("CurrentUrl >>>>" + currentUrl);
		
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
	
		externalContext.redirect(currentUrl);

	}

	public boolean isChangeRoleSection() {
		return changeRoleSection;
	}

	public boolean isRoleChangeSuccess() {
		return roleChangeSuccess;
	}
	
	private boolean isPublishedCatalogue(Long supplierId){
		List<CmSupplierItem> suppItems = getCmSupplierItems(supplierId);
		boolean catalogueStatus = false;
		
		if(suppItems.size() > 0){
			for(CmSupplierItem cmSuppItem : suppItems){
				if(cmSuppItem.getItemStatus().equalsIgnoreCase(CATALOGUE_PUBLISHED)){
					catalogueStatus = true;
					break;
				}
			}
		}
		
		return catalogueStatus;
	}
	
	/**
	 * Get G2G Business Type
	 * @return String
	 */
	public String getG2GBusinessType(){
		return SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_GOVT_SELLER;
	}
	
	private SmWorkflowStatus rejectedApplExist(Long supplierId){
		//boolean isRejectedApplExist = false;
		SmWorkflowStatus wf = null;
		ISmWorkFlowStatusSO workFlowStatusSO = SmServiceLookup.getSmWorkFlowStatusSO();
		SmWorkFlowStatusReqDTO reqDTO = new SmWorkFlowStatusReqDTO();
		
		reqDTO.setStatusIds(new ArrayList<Long>());
		reqDTO.getStatusIds().add(SupplierManagementConst.PM_STATUS_PO_REJECTED_APPLICATION);
		reqDTO.getStatusIds().add(SupplierManagementConst.PM_STATUS_APPROVER_REJECTED_APPLICATION);
		reqDTO.getStatusIds().add(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYS);
		reqDTO.setSupplierId(supplierId);
		
		SmWorkFlowStatusResDTO resDTO = workFlowStatusSO.
				retrieveWorkFlowBySuppIdAndStatusIds(reqDTO);
		
		if(resDTO.getWorkFlowStatusLs().size() > 0){
			getManagedBean().setRejectedApplExist(true);
			wf = resDTO.getWorkFlowStatusLs().get(0);
		}else{
			getManagedBean().setRejectedApplExist(false);
		}
		
		return wf;
	}
	
	private List<SmWorkflowStatus> rejectedPaymentExpired(Long supplierId){
		//boolean isRejectedApplExist = false;
		ISmWorkFlowStatusSO workFlowStatusSO = SmServiceLookup.getSmWorkFlowStatusSO();
		SmWorkFlowStatusReqDTO reqDTO = new SmWorkFlowStatusReqDTO();
		
		reqDTO.setStatusIds(new ArrayList<Long>());
		reqDTO.getStatusIds().add(20119L);
		reqDTO.setSupplierId(supplierId);
		
		SmWorkFlowStatusResDTO resDTO = workFlowStatusSO.
				retrieveWorkFlowBySuppIdAndStatusIds(reqDTO);
		
		
		return resDTO.getWorkFlowStatusLs();
	}
	
	private SmWorkflowStatus isLatestActiveWorkFlow(Long supplierId){
		ISmWorkFlowStatusSO workFlowStatusSO = SmServiceLookup.getSmWorkFlowStatusSO();
		SmWorkFlowStatusReqDTO reqDTO = new SmWorkFlowStatusReqDTO();
		SmWorkflowStatus wfStatus = null;
		
		reqDTO.setSupplierId(supplierId);	
		SmWorkFlowStatusResDTO resDTO = workFlowStatusSO.retrieveAllIsCurrentBySupplierId(reqDTO);
		
		if(resDTO.getWorkFlowStatusLs().size() > 0){
			wfStatus = resDTO.getWorkFlowStatusLs().get(0);
		}
		
		return wfStatus;
	}
	
/*	private boolean checkSuccessPaymentWithApplId(Long applId){
		PaymentReqDTO reqDTO = new PaymentReqDTO();
		reqDTO.setApplId(applId);
		List<PyPaymentOrder> paymentOrders = SmServiceLookup.getPaymentSO().retrieveSuccPaymentByApplId(reqDTO).getPaymentOrders();
	
		if(paymentOrders.size() > 0){
			return true;
		}else{
			return false;
		}
	}*/
	
	private boolean checkSuccessPaymentBySupplierId(Long supplierId){
		PaymentReqDTO reqDTO = new PaymentReqDTO();
		//reqDTO.setApplId(applId);
		reqDTO.setSupplierId(supplierId);
		List<PyPaymentOrder> paymentOrders = SmServiceLookup.getPaymentSO().retrieveSuccPaymentBySupplierId(reqDTO).getPaymentOrders();
		log.info("checkSuccessPaymentBySupplierId>>>>>>>>>>" + paymentOrders.size());
		
		if(paymentOrders.size() > 0){
			for(PyPaymentOrder pOrder : paymentOrders){
				log.info("pOrder>>>>>>>>>>>" + pOrder.getPyPayment().getPyBill().getBillId());
				log.info("pOrder billType>>>>>>>>>>>" + pOrder.getPyPayment().getPyBill().getBillType());
			}
			return true;
		}else{
			return false;
		}
	}
	
	private PyBill getSuccessPaymentApplIdBySupplierId(Long supplierId){
		PaymentReqDTO reqDTO = new PaymentReqDTO();
		PyBill pyBill = null;
		//reqDTO.setApplId(applId);
		reqDTO.setSupplierId(supplierId);
		List<PyPaymentOrder> paymentOrders = SmServiceLookup.getPaymentSO().retrieveSuccPaymentBySupplierId(reqDTO).getPaymentOrders();
	
		if(paymentOrders.size() > 0){
			for(PyPaymentOrder pOrder : paymentOrders){
				if(pOrder.getPyPayment().getPyBill().getBillType().equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_PROCESS_FEE)){
					pyBill = pOrder.getPyPayment().getPyBill();
					break;
				}
			
			}
		}
		
		return pyBill;
	}
	
	private String validPaymentForResubmission(Long supplierId, Long rejectedWfId){
		boolean successPaymentDocId = checkSuccessPaymentBySupplierId(supplierId);
		if(!successPaymentDocId){
			log.info("*******No SUCCESS PAYMENT********");
			getManagedBean().setRejectedApplExist(true);
			getManagedBean().setDisplayRejectLabel(true);
			getManagedBean().setFlowStatus(1);

			return MOF_REG_PATH;
		}else{
			log.info("*******SUCCESS PAYMENT********" + RESUBMIT_APPLICATION + rejectedWfId);
			getManagedBean().setRejectedApplExist(true);
			getManagedBean().setDisplayRejectLabel(true);
			getManagedBean().setFlowStatus(3);
			getManagedBean().setSuccessPayment(true);
			
			

			return RESUBMIT_APPLICATION + rejectedWfId;
		}

	}
	
	private void setRejectApplFlagByPayment(SmSupplier supplier, SmWorkflowStatus workFlowStatus){
		//if refId for processing fee not equeal to current workflow doc id
		//treat is as rejectedAppl
		PyBill pyBill = getSuccessPaymentApplIdBySupplierId(supplier.getSupplierId());
		if(pyBill != null){
			if(pyBill.getBillRefId() == workFlowStatus.getDocId()){
				getManagedBean().setRejectedApplExist(false);
	
			}else{
				getManagedBean().setRejectedApplExist(true);
			}
		}else{
			getManagedBean().setRejectedApplExist(false);
		}
	}
	
	private void setSuccessPaymentFlag(){
		getManagedBean().setRejectedApplExist(true);
		//getManagedBean().setDisplayRejectLabel(true);
		getManagedBean().setSuccessPayment(true);
	}
	
	//Enhancement #20467
	/**
	 * Check whether the rejected application was expired
	 * compare to no. of days allowed to resubmit after rejected
	 * @param SmAppl rejAppl
	 * @return Boolean
	 */
	private Boolean checkRejectedApplExpired(SmAppl rejAppl){
		Boolean isExpired = false;

		if(rejAppl.getApplId() != null) {
			if(!SmApplicationUtil.isRejectedApplValidForResubmission(rejAppl)) {
				isExpired = true;
			}
		}		
		return isExpired;
	}
	
	private void setExpiredRejectedApplFlag(){
		getManagedBean().setRejectedApplExist(false);
		getManagedBean().setDisplayRejectLabel(true);
	}
	
	//START : LOG 12491

	/**
	 * renderWelcomeFirstTime
	 * 
	 * to show first time message after 
	 * regisration complete
	 * 
	 * 
	 */
	public void renderWelcomeFirstTime() {
		log.info("******* showWelcomeFirstTime ******");
		FacesScopes facesScopes = new FacesScopes();
		FacesContext fc = FacesContext.getCurrentInstance();
		try {
			PmUser sUsers = facesScopes.getUserSessionBean();
			if (sUsers == null) {
				throw new EpException(MessageConst.ERROR_770045);
			}
			if (new FacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
					|| new FacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
					|| new FacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
					|| new FacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())
					|| new FacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value())
					|| new FacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())) {
				PersonnelReqDTO reqDTO = new PersonnelReqDTO();
				reqDTO.setUserId(sUsers.getUserId());
				IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate.getInstance().getServiceObject("sm",
						IPersonnelSO.class);
				PersonnelResDTO resDTO = smPersonnelSO.retrievePersonnelByUserId(reqDTO);
				if (resDTO.getSmPersonnel() != null && resDTO.getSmPersonnel().isShowWelcome()) {
					String msgId = null;

					if (resDTO.getSmPersonnel().getSmAppl().getApplType()
							.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.APPL_TYPE_MOF)) {
						msgId = MessageConst.INFO_721035;
					} else {
						if (resDTO
								.getSmPersonnel()
								.getSmAppl()
								.getSmSupplier()
								.getBusinessType()
								.equals(my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_GOVT_SELLER)) {
							msgId = MessageConst.INFO_721038;
						} else {
							msgId = MessageConst.INFO_721039;
						}
					}

					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(msgId,
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

					// UPDATE FLAG TO 0
					resDTO.getSmPersonnel().setShowWelcome(false);
					reqDTO.setSmPersonnel(resDTO.getSmPersonnel());
					smPersonnelSO.updatePersonnelRecord(reqDTO);
					
					

				}

			}
			//callTestEjb();
		} catch (Exception e) {
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, MessageConst.ERROR_770045, PmCommonUtil
							.getMsgFromMsgUtils(MessageConst.ERROR_770045, null)));
		}
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ACKNOWLEDGEMENT_PAGE);

	}
	//Remove Function callUpgradeRole
	//END : LOG 12491
}
