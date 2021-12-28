package my.ep.web.backingbean.fl.yep;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.orgprofile.IPtjReorgSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.fee.settings.IFeeExemptionSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.orgprofile.PtjReorgReqDTO;
import my.ep.dto.pm.authorization.orgprofile.PtjReorgResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionReqDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmPtjReorg;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBaseBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.fl.yep.YearEndProcessManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.StatusHelper;

/**
 * Backing bean for year end process
 * 
 * @Author basri
 * @since 10/09/2019
 * */

@ManagedBean(name = "yearEndProcessBackingBean")
@ViewScoped
public class YearEndProcessBackingBean extends
		FulfilmentBaseBackingBean<YearEndProcessManagedBean> {
	private static Logger log = EpLogger.getLogger(YearEndProcessBackingBean.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String SEARCH_YEP = "/xhtml/fl/yep/search/searchYearEndProcess.xhtml?faces-redirect=true";
	
	/** The Constant NAV_TO_MAINTAIN_PO. */
	public static final String NAV_TO_MAINTAIN_PO = "MaintainPO";
	
	/** The Constant NAV_TO_MAINTAIN_CO. */
	public static final String NAV_TO_MAINTAIN_CO = "MaintainCO";

	/**
	 * The method used to clear the previous viewed information before rendering
	 * the contract search screen.
	 */
	public void preRenderSearch(boolean trackingDiary) {
		renewSesionObject();
		defaultPtjAndMinistryValues();
		resetSearchCriteria();
//		getManagedBean().setTrackingDiarySearch(trackingDiarySearch);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, SEARCH_YEP);
	}

	/**
	 * Selected order type.
	 * 
	 * @param e
	 *  the e
	 */
	public void selectedDocType(AjaxBehaviorEvent e) {
		String docType = (String) e.getComponent().getAttributes().get("value");

		// reset everything to false
		//#29036 removed for auto check by vinod Start
		//#29281 changed commented code for auto check Ap58 -- Vinod
		this.getManagedBean().setCheckDocType(false); 
		resetSearchCriteria();
		//this.getManagedBean().setSearchAp58(true);
		if (docType != null) {
			if (docType.equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)) {
				this.getManagedBean().setCheckDocType(true);
			}
		}
		getManagedBean().setSearchDocType(docType);
	}

	/**
	 * Reset search criteria.
	 */
	private void resetSearchCriteria() {
		getManagedBean().setSearchOrderNo(null);
		getManagedBean().setSearchTitle(null);
		getManagedBean().setSearchDocType(null);
		getManagedBean().setStatusDescId(null);
		getManagedBean().setSearchAp58(null);
		getManagedBean().setSearchCarryFoward(null);
		getManagedBean().setSearchCloseMnualPay(null);
		getManagedBean().setSearchNonAction(null);
		getManagedBean().setSearchStartDate(null);
		getManagedBean().setSearchToDate(null);
		getManagedBean().setSearchSuppName(null);
		getManagedBean().setSearchPhysLoaNo(null);
		
		getManagedBean().setSearchCentralPanelContractName(null);
		getManagedBean().setSearchCircularNo(null);
		getManagedBean().setSearchLoaNo(null);
		getManagedBean().setSearchPhysLoaNo(null);
		getManagedBean().setSearchContractNo(null);
		getManagedBean().setSearchPhysContractNo(null);
		getManagedBean().setSearchContractFromDuration(null);
		getManagedBean().setSearchContractToDuration(null);
	}

	/**
	 * retrieve financial year options for current year and past 7 years
	 * 
	 * @return financial year options
	 */
	public static List<SelectItem> getFinancialYearOpt() {
		int years = 0;
		List<SelectItem> financialYearOpt = new ArrayList<SelectItem>();
		for (Integer i = 0; i < 2; i++) {
			years = CommonMethod.getYear(-i);
			financialYearOpt
					.add(new SelectItem(years, Integer.toString(years)));
		}
		return financialYearOpt;
	}
	
	/**
	 * Get status select items by module and doc type for dropdown.
	 * 
	 * @param moduleCode
	 *            the module code
	 * @param docType
	 *            the document type
	 * @return array of SelectItem
	 */
	public SelectItem[] getStatusDropdownValues(String moduleCode,
			String docType) {
		
		
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			List<PmStatusDesc> statusDescs = StatusHelper.getInstance()
					.getStatusDescList(moduleCode, docType, null);
			
			List<PmStatusDesc> statusDescsYep = new ArrayList<PmStatusDesc>();
			
				for (PmStatusDesc statusDesc : statusDescs) {
				
				/*#28373 Vinod Added CO_PENDING_REVISION_INVOICE,PO_PENDING_REVISION_INVOICE,CO_PENDING_PAYMENT_QUERY and PO_PENDING_PAYMENT_QUERY*/
				if(statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_SUPPLIER_ACKNOWLEDGEMENT)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_FULFILMENT)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_PARTIAL_FL)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_INVOICE)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_DAN_APPROVAL)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_PAYMENT_MATCH)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_CANCELLED)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_CANCELLED_BY_YEP)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_SUPPLIER_ACKNOWLEDGEMENT)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_FULFILMENT)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_PARTIAL_FL)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_INVOICE)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_DAN_APPROVAL)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_PAYMENT_MATCH)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_CANCELLED)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_CANCELLED_BY_YEP)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_REVISION_INVOICE)
					//32387 start
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_REVISION_INVOICE2)
					//32387 end
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_PAYMENT_QUERY)
					||statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_PAYMENT_QUERY)){
					
					statusDescsYep.add(statusDesc);
				}
			}
				
				
			items = new SelectItem[statusDescsYep.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmStatusDesc statusDescYep : statusDescsYep) {
				
					items[i++] = new SelectItem(statusDescYep.getPmStatus()
							.getStatusId(), statusDescYep.getStatusName());
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}
	
	
	public void searchYep(){

		if(null ==getManagedBean().getSearchDocType()){ 
			throw new EpException(MessageConst.ERROR_760444);
		}
		
		if(null == getManagedBean().getSearchStartDate() && null != getManagedBean().getSearchToDate()){
			throw new EpException(MessageConst.ERROR_760494);
		}
		getManagedBean().getFlYepDataModel().reloadData();
		
	}
	
	/**
	 * Get the function type. PTJ or Supplier
	 * 
	 * @return the function type
	 */
	public SelectItem[] getFunctionType() {
		List<String> typeList = new ArrayList<String>();
		typeList.add(ParameterConst.ORG_TYPE_PTJ);
		typeList.add(ParameterConst.ORG_TYPE_SUPPLIER);

		SearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), typeList));

		return getRadioButtonCodeValuesBySearchBean(ParameterTypeConst.ORGANISATION_TYPES, searchBean);
	}
	
	/**
	 * Show function type if user have both ptj & supplier role
	 * @return boolean
	 */
	public boolean showFunctionType() {
		if(hasSupplierRole() && hasGovernmentUserRole()) {
			if(getManagedBean().getSearchFunctionType()==null) {
				getManagedBean().setSearchFunctionType(ParameterConst.ORG_TYPE_PTJ);
			}
			return true;
		} else {
			if(hasSupplierRole()) {
				getManagedBean().setSearchFunctionType(ParameterConst.ORG_TYPE_SUPPLIER);
			} else {
				getManagedBean().setSearchFunctionType(ParameterConst.ORG_TYPE_PTJ);
			}
			return false;
		}
	}
	
	/**
	 * Gets the default ptj and ministry search values based on user role.
	 *
	 */
	private void defaultPtjAndMinistryValues() {
		if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value()) ||
			getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()) || (hasSupplierRole() && !hasGovernmentUserRole())) {
		} else {
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			PmOrgProfile orgProfile = null;
			if (userResDTO.getUserOrgList() != null	&& userResDTO.getUserOrgList().size() > 0) {
				orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
			}
			if (getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.RECEIVING_OFFICER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.ACKNOWLEDGE_OFFICER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.PAY_OFFICER.value())) {
				this.getManagedBean().setPtj(orgProfile);
				this.getManagedBean().setMinistry(retrieveMinistryOrgProfile(orgProfile.getOrgProfileId()));
				this.getManagedBean().setPtjGroup(retrieveJabatanOrgProfile(orgProfile.getOrgProfileId()));
			} else if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {
				this.getManagedBean().setPtj(null);
				this.getManagedBean().setMinistry(orgProfile);
			}
		}
	}
	
	/**
	 * Get Ministry condition for construct the LOV listing
	 * @return String
	 */
	public String getMinistryCondition() {
		String condition = "";
		if(hasGovernmentUserRole()) {
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
			IPtjReorgSO ptjReorgSO = (IPtjReorgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IPtjReorgSO.class);
			
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			PmOrgProfile orgProfile = null;
			if (userResDTO.getUserOrgList() != null	&& userResDTO.getUserOrgList().size() > 0) {
				orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
			}
			condition = "orgProfileId^"+retrieveMinistryOrgProfile(orgProfile.getOrgProfileId()).getOrgProfileId();
			
			PtjReorgReqDTO ptjReqDTO = new PtjReorgReqDTO();
			ptjReqDTO.setDeepLevel(true);
			ptjReqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
			PtjReorgResDTO ptjResDTO = ptjReorgSO.retrieveOldPtjByNewPtjId(ptjReqDTO);
			PmOrgProfile ministry = null;
			for(PmPtjReorg pmPtjReorg : ptjResDTO.getPtjReorgList()) {
				ministry = retrieveMinistryOrgProfile(pmPtjReorg.getOldPtjId());
				if(ministry!=null) {
					condition = condition + "*" + "orgProfileId^" + ministry.getOrgProfileId();
				}
			}
		} 
		return condition;
	}
	
	/**
	 * Get PTJ condition for construct the LOV listing
	 * @return String
	 */
	public String getPTJCondition() {
		String condition = OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value())+
				"^"+this.getManagedBean().getPtjGroup().getOrgProfileId();
		if(hasGovernmentUserRole()) {
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
			IPtjReorgSO ptjReorgSO = (IPtjReorgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IPtjReorgSO.class);
			
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			PmOrgProfile orgProfile = null;
			if (userResDTO.getUserOrgList() != null	&& userResDTO.getUserOrgList().size() > 0) {
				orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
			}
			String ptjList = orgProfile.getOrgProfileId()+"";
			PtjReorgReqDTO ptjReqDTO = new PtjReorgReqDTO();
			ptjReqDTO.setDeepLevel(true);
			ptjReqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
			PtjReorgResDTO ptjResDTO = ptjReorgSO.retrieveOldPtjByNewPtjId(ptjReqDTO);
			for(PmPtjReorg pmPtjReorg : ptjResDTO.getPtjReorgList()) {
				ptjList = ptjList + "," + pmPtjReorg.getOldPtjId();
			}
			condition = condition + "|orgProfileId^"+ptjList+"^9";
		}
		return condition;
	}
		
	/**
	 * The method used reset PTJ/PTJ Group profile value once select the Ministry
	 */
	public void resetPtjGroupProfile() {
		getManagedBean().setPtj(null);
		getManagedBean().setPtjGroup(null);
	}
	
	/**
	 * The method used reset PTJ profile value once select the Ministry
	 */
	public void resetPtjProfile() {
		getManagedBean().setPtj(null);
	}
		
	/**
	  * The method used to disable the ministry Lov
	  * if login user is FL User
	  * or internal auditor.
	  * 
	  * @return true, if login user is FL User/internal auditor
	  *         false, if login user is not User/internal auditor
	*/	
	public boolean disableMinistryLov() {
		if(hasGovernmentUserRole() || getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {
			if(isPTJReOrg()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	/**
	  * The method used to disable the ptj Lov
	  * if login user is FL User
	  * or ministry is null.
	  * 
	  * @return true, if login user is FL User/ministry is null
	  *         false, if login user is not User/ministry is null
	*/	
	
	public boolean disablePtjLov() {
		if(getManagedBean().getPtjGroup() == null) {
			return true;
		} else if(hasGovernmentUserRole()) {
			if(isPTJReOrg()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	/**
	  * The method used to disable the ptj Lov
	  * if login user is FL User
	  * or ministry is null.
	  * 
	  * @return true, if login user is FL User/ministry is null
	  *         false, if login user is not User/ministry is null
	*/	
	
	public boolean disablePtjGroupLov() {
		if(getManagedBean().getMinistry() == null) {
			return true;
		} else if(hasGovernmentUserRole()) {
			if(isPTJReOrg()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	private boolean isPTJReOrg() {
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
		IPtjReorgSO ptjReorgSO = (IPtjReorgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IPtjReorgSO.class);
		
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
		UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
		PmOrgProfile orgProfile = null;
		if (userResDTO.getUserOrgList() != null	&& userResDTO.getUserOrgList().size() > 0) {
			orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
		}
		PtjReorgReqDTO ptjReqDTO = new PtjReorgReqDTO();
		ptjReqDTO.setDeepLevel(true);
		ptjReqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
		PtjReorgResDTO ptjResDTO = ptjReorgSO.retrieveOldPtjByNewPtjId(ptjReqDTO);
		if(ptjResDTO!=null && ptjResDTO.getPtjReorgList().size()>0)
			return true;
		else 
			return false;
	}
	
	/**
	 * Get PTJ group condition for construct the LOV listing
	 * @return String
	 */
	public String constuctConditionFieldsForPTJGroup() {	
		String condition = OrgProfileUtil.getFromToSearchBeanParentString(OrganizationType.DEPARTMENT.value(), OrganizationType.MINISTRY.value())+"^"+
				this.getManagedBean().getMinistry().getOrgProfileId();
		if(hasGovernmentUserRole()) {
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
			IPtjReorgSO ptjReorgSO = (IPtjReorgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IPtjReorgSO.class);
			
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			PmOrgProfile orgProfile = null;
			if (userResDTO.getUserOrgList() != null	&& userResDTO.getUserOrgList().size() > 0) {
				orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
			}
			String kumpulanPtjList = retrieveJabatanOrgProfile(orgProfile.getOrgProfileId()).getOrgProfileId()+"";
			PtjReorgReqDTO ptjReqDTO = new PtjReorgReqDTO();
			ptjReqDTO.setDeepLevel(true);
			ptjReqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
			PtjReorgResDTO ptjResDTO = ptjReorgSO.retrieveOldPtjByNewPtjId(ptjReqDTO);
			PmOrgProfile kumpulanPtj = null;
			for(PmPtjReorg pmPtjReorg : ptjResDTO.getPtjReorgList()) {
				kumpulanPtj = retrieveJabatanOrgProfile(pmPtjReorg.getOldPtjId());
				if(kumpulanPtj!=null) {
					kumpulanPtjList = kumpulanPtjList + "," + kumpulanPtj.getOrgProfileId();
				}
			}
			condition = condition + "|orgProfileId^"+kumpulanPtjList+"^9";
		}
		return condition;
	}
	
	/**
	 * Search reset.
	 */
	public void searchReset(){
		renewSesionObject();
	}
	
	public boolean getAllowClose(Long statusId){
		
		
		if(statusId.equals(FulfilmentStatusConst.PO_PENDING_INVOICE)
				||statusId.equals(FulfilmentStatusConst.PO_PENDING_DAN_APPROVAL)
				||statusId.equals(FulfilmentStatusConst.PO_PENDING_PAYMENT_MATCH)
				||statusId.equals(FulfilmentStatusConst.CO_PENDING_INVOICE)
				||statusId.equals(FulfilmentStatusConst.CO_PENDING_DAN_APPROVAL)
				||statusId.equals(FulfilmentStatusConst.CO_PENDING_PAYMENT_MATCH)
				||statusId.equals(FulfilmentStatusConst.PENDING_SUPPLIER_ACK)
				||statusId.equals(FulfilmentStatusConst.PO_PENDING_FULFILMENT)
				||statusId.equals(FulfilmentStatusConst.PO_PENDING_PAYMENT_QUERY)
				||statusId.equals(FulfilmentStatusConst.CO_PENDING_PAYMENT_QUERY)
				||statusId.equals(FulfilmentStatusConst.CO_PENDING_REVISION_INVOICE)
				//32387 start
				||statusId.equals(FulfilmentStatusConst.PO_PENDING_REVISION_INVOICE2)
				//32387 end
				||statusId.equals(FulfilmentStatusConst.CO_PENDING_FULFILMENT)){
				
				return true;
			}
		return false;
	}
	
	public boolean isCarryForward(Long statusId){
		
		if(statusId.equals(FulfilmentStatusConst.PO_PENDING_SUPPLIER_ACKNOWLEDGEMENT)
				||statusId.equals(FulfilmentStatusConst.PO_PENDING_FULFILMENT)
				||statusId.equals(FulfilmentStatusConst.CO_PENDING_SUPPLIER_ACKNOWLEDGEMENT)
				||statusId.equals(FulfilmentStatusConst.CO_PENDING_FULFILMENT)){				
				return true;
			}
		return false;
	}
	
	public String navigateToPage() throws ParseException {

		IFulfilmentRequestSO fulfilmentRequestSO = null;
		IFulfilmentOrderSO fulfilmentOrderSO = null;
		IFeeExemptionSO feeExemptionsSO = null;

		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FeeExemptionReqDTO reqFeeDTO = new FeeExemptionReqDTO();
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		FeeExemptionResDTO resFeeDTO = null;
		FulfilmentOrderBackingBean flOrderBB = new FulfilmentOrderBackingBean();

		String navgPage = "xhtml/fl/yep/search/searchYearEndProcess.xhtml";
		String docType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docType");
		String flDocId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docId");
		String buttonVal = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("buttonVal");
		String statusDesc = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("statusDesc");
		String taskId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("taskId");
		String orderN = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("orderN");
		String carryForward = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("carryForward");
		String progressStatus = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("progressStatus");
		String phase = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("phase");

		if (docType.equals(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
			feeExemptionsSO = (IFeeExemptionSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IFeeExemptionSO.class);
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
			reqDTO.setDocType(ParameterConst.DOCTYPE_PURCHASE_ORDER);
			reqDTO.setFlOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = fulfilmentOrderSO.retrieveFulfilmentOrderById(reqDTO);
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);

			flOrderBB.renewSesionObject();
			reqFeeDTO.setOrgProfileId((long) 1955);
			resFeeDTO = feeExemptionsSO.getFeeExemptionsYepProcessing(reqFeeDTO);

			if (resFeeDTO.getFeeExemptions().get(2).getEffDate() != null
					&& resFeeDTO.getFeeExemptions().get(2).getExpDate() != null) {

				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				Calendar currTime = Calendar.getInstance();
				Date d1 = df.parse(df.format(currTime.getTime()));
				Date d2 = df.parse(df.format(resFeeDTO.getFeeExemptions().get(2).getEffDate()));
				Date d3 = df.parse(df.format(resFeeDTO.getFeeExemptions().get(2).getExpDate()));
				System.out.println("d2 is : " + d2);
				System.out.println("d3 is : " + d3);
				if (d1.compareTo(d3) <= 0 && d1.compareTo(d2) >= 0) { //28375
					flOrderBB.getManagedBean().setPatCheck("true");

				} else {
					flOrderBB.getManagedBean().setPatCheck("false");
				}
			} else{
				flOrderBB.getManagedBean().setPatCheck("false"); //28375
			}

			System.out.println("flOrderBB.getManagedBean().getPatCheck() is : "
					+ flOrderBB.getManagedBean().getPatCheck());

			flOrderBB.getManagedBean().setButtonVal(buttonVal);
			if (BpmOutcomeConst.BPM_CARRY_FORWARD.equalsIgnoreCase(buttonVal) && phase == "3") {
				flOrderBB.getManagedBean().setDisable(false);
			}
			/* #28375 - Logan - Beginning */
			System.out.println("flOrderBB.getManagedBean().getPatCheck() is : "
					+ flOrderBB.getManagedBean().getPatCheck());
			flOrderBB.getManagedBean().setTaskId(taskId);
			/* Ending */
			// #28374 Vinod Start
			flOrderBB.getManagedBean().setProgressStatus(progressStatus);
			flOrderBB.getManagedBean().setIscarryForward(carryForward);
			// #28374 Vinod End

			String[] outcome = new String[1];
			if (BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(buttonVal)) {
				outcome[0] = BpmOutcomeConst.BPM_CANCEL;
				flOrderBB.getManagedBean().setOutComes(outcome);
			}
			flOrderBB.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			flOrderBB.getManagedBean().setSelectedTabIndex(0);
			flOrderBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			flOrderBB.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
			flOrderBB.getManagedBean().setRemoveFactoring(false);
			flOrderBB.getManagedBean().setDocMaintainRemarks(true);

			flOrderBB.getManagedBean().setBackToPrevious("searchYearEndProcess");
			if (resDTO.getFlOrder().isFactored()) {
				flOrderBB.getManagedBean().setFactoringCheck("true");
				flOrderBB.getFactoringCompanyDtl();
			}
			if (buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW) || buttonVal.equalsIgnoreCase("CLOSEMANUAL")) {
				//log.info("29442: remark:"+resDTO.getRemarkList().get(0).getRemark());
				flBB.initializeRemarkAndAttachement(null, resDTO.getRemarkList(), resDTO.getAttachmentList());
				flBB.getManagedBean().setHideRemarks(true);
			} else if (buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)
					|| buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				flBB.getManagedBean().setDocMaintainRemarks(true);
			} else {
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			}

			navgPage = NAV_TO_MAINTAIN_PO;
		} else if (docType.equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)) {
			feeExemptionsSO = (IFeeExemptionSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IFeeExemptionSO.class);
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_ORDER);
			reqDTO.setFlOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO coResDTO = fulfilmentOrderSO.retrieveFulfilmentOrderById(reqDTO);

			flOrderBB.renewSesionObject();

			reqFeeDTO.setOrgProfileId((long) 1955);
			resFeeDTO = feeExemptionsSO.getFeeExemptionsYepProcessing(reqFeeDTO);

			if (resFeeDTO.getFeeExemptions().get(2).getEffDate() != null
					&& resFeeDTO.getFeeExemptions().get(2).getExpDate() != null) {

				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				Calendar currTime = Calendar.getInstance();
				Date d1 = df.parse(df.format(currTime.getTime()));
				Date d2 = df.parse(df.format(resFeeDTO.getFeeExemptions().get(2).getEffDate()));
				Date d3 = df.parse(df.format(resFeeDTO.getFeeExemptions().get(2).getExpDate()));
				//System.out.println("d2 is : " + d2);
				//System.out.println("d3 is : " + d3);
				if (d1.compareTo(d3) <= 0 && d1.compareTo(d2) >= 0) { //28375
					flOrderBB.getManagedBean().setPatCheck("true");

				} else {
					flOrderBB.getManagedBean().setPatCheck("false");
				}
			} else{
				flOrderBB.getManagedBean().setPatCheck("false"); //28375
			}

			System.out.println("flOrderBB.getManagedBean().getPatCheck() is : "
					+ flOrderBB.getManagedBean().getPatCheck());

			flOrderBB.getManagedBean().setButtonVal(buttonVal);
			flOrderBB.getManagedBean().setSelectedTabIndex(0);
			flOrderBB.getManagedBean().setFlRequest(coResDTO.getFlOrder().getFlFulfilmentRequest());
			flOrderBB.getManagedBean().setFulfilmentOrder(coResDTO.getFlOrder());
			flOrderBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			/* #28375 - Logan - Beginning */
			flOrderBB.getManagedBean().setTaskId(taskId);
			/* Ending */
			// #28374 Vinod Start
			flOrderBB.getManagedBean().setProgressStatus(progressStatus);
			flOrderBB.getManagedBean().setIscarryForward(carryForward);
			// #28374 Vinod End
			flOrderBB.getManagedBean().setBackToPrevious("searchYearEndProcess");

			if (buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW) || buttonVal.equalsIgnoreCase("CLOSEMANUAL")) {
				//log.info("29442: remark:"+coResDTO.getRemarkList().get(0).getRemark());
				flBB.initializeRemarkAndAttachement(null, coResDTO.getRemarkList(), coResDTO.getAttachmentList());
				flBB.getManagedBean().setHideRemarks(true);
			} else if (buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)
					|| buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			} else {
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			}

			navgPage = NAV_TO_MAINTAIN_CO;
		}
		// 28377 razin
		reqDTO.setAttachment(new FlAttachment());
		reqDTO.getAttachment().setDocId(flOrderBB.getManagedBean().getFulfilmentOrder().getFulfilmentOrderId());
		// reqDTO.getAttachment().SecId(FulfilmentConst.AP58_AL);
		reqDTO.getAttachment().setDocType(flOrderBB.getManagedBean().getFulfilmentOrder().getDocType());
		FulfilmentResDTO resDTOattachment = fulfilmentOrderSO.retrieveAttachmentApprovalLetter(reqDTO);
		if (resDTOattachment.getAttachmentList().size() > 0) {
			flOrderBB.getManagedBean().setAttachment(resDTOattachment.getAttachmentList().get(0));
			flOrderBB.getManagedBean().getAttachment().setBrandNew(false);
		} else {
			flOrderBB.getManagedBean().setAttachment(new FlAttachment());
			flOrderBB.getManagedBean().getAttachment().setBrandNew(true);
		}

		// end - 28377 razin
		//29440 start
		flOrderBB.getManagedBean().setFlOrderId(Long.valueOf(flDocId));
		//29440 end
		flOrderBB.getManagedBean().setYep(true);
		return navgPage;
	}
	
//	28377 razin
	
	//check if ap58
    public boolean isAP58(Long statusId){
    	if(statusId.equals(FulfilmentStatusConst.PO_PENDING_INVOICE)
				||statusId.equals(FulfilmentStatusConst.PO_PENDING_PAYMENT_MATCH)
				||statusId.equals(FulfilmentStatusConst.PO_PENDING_DAN_APPROVAL)
				||statusId.equals(FulfilmentStatusConst.CO_PENDING_DAN_APPROVAL)
				||statusId.equals(FulfilmentStatusConst.CO_PENDING_INVOICE)
				||statusId.equals(FulfilmentStatusConst.PO_PENDING_PAYMENT_QUERY)
				||statusId.equals(FulfilmentStatusConst.CO_PENDING_PAYMENT_QUERY)
				||statusId.equals(FulfilmentStatusConst.CO_PENDING_REVISION_INVOICE)
				||statusId.equals(FulfilmentStatusConst.PO_PENDING_REVISION_INVOICE2)
				||statusId.equals(FulfilmentStatusConst.CO_PENDING_PAYMENT_MATCH)){
				
				return true;
			}
		return false;
	}
    // end 28377
    
    //29440 start
    public void generateReport(String docType, String docNo) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_DOC_NO, docNo);
		paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
		String reportName = ReportConst.FL_NGEP_PURCHASE_ORDER;
		if(reportName!=null) {
			try {
				generateReport(reportName, paramMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new EpException("Report is not available for this document.");
		}
	}
    //29440 end
}
