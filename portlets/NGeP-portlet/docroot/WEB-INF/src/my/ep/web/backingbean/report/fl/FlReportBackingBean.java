/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.report.fl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import my.ep.biz.service.pm.authorization.orgprofile.IPtjReorgSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.rpt.IRPTRequestDtlSO;
import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.orgprofile.PtjReorgReqDTO;
import my.ep.dto.pm.authorization.orgprofile.PtjReorgResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.report.RPTRequestDtlReqDTO;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmPtjReorg;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
import my.ep.web.constant.report.FlReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.fl.FlReportManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
/**
 * Backing Bean for FlReport.
 *
 * @author KK LIM
 * @since 26/4/2017
 */
public class FlReportBackingBean<T extends FlReportManagedBean> extends ReportBaseBackingBean<T>  {

	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(FlReportBackingBean.class);

	
	public FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * retrieve report list by category
	 */
	public void retrieveReportListByCategory(String reportCategory) {
		retrieveReportListByModuleCodeAndSubModule(
				ModuleCodeConst.ORDER_FULFILLMENT.toUpperCase(),
				reportCategory);
	}
	
	/**
	 * On change of report name
	 * 
	 */
	public void onChangeReportName(){

		log.info("onChangeReportName");	
		
		if (StringUtils.isNotBlank(getManagedBean().getReportCode())) {
			retrieveReportValueFromDb();
		}
	}
	
	/**
	 * generate FL report
	*/
	public void generateFlReport(LinkedHashMap<String, String> params){
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,IRPTRequestSO.class);		
		RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
		params.put(FlReportConst.PARAM_KEY_REPORT, getManagedBean().getReportName());
		params.put(FlReportConst.PARAM_FL_REPORT_ID_EQ, getManagedBean().getReportCode());
		params.put(FlReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId().toString());
		reqDTO.setDefaultOutputType(this.getManagedBean().getDefaultOutputType());
		reqDTO.setTempAccess(this.getManagedBean().isTempAccess());
		reqDTO.setParametersMap(params);
		reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
		RPTRequestResDTO resDTO=null;
		try {
			resDTO = rptRequestSO.generateReport(reqDTO);
			
			//R5 #14076 start
			RPTRequestDtlReqDTO restDtlDTO = new RPTRequestDtlReqDTO();
			restDtlDTO.getRptRequestDtl().setRequestId(resDTO.getRequestId());
			//temporary code for log data
			log.info("28379: url: "+resDTO.getReportUrl());
			
			//1 = Contract No 
			//2 = Contract Request No || Invoice No(Supplier)
			//3 = Purchase Order No ||  Contract Order No
			//4 = Purchase Request No || Delivery Order No || Fulfilment Received Note  No || Stop Delivery Instruction No || Invoice No
			
			if(params.get(FlReportConst.FL_RPT_CONTRACT_NO) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo01(params.get(FlReportConst.FL_RPT_CONTRACT_NO));
			}	
			
			if(params.get(FlReportConst.FL_RPT_CONTRACT_REQ_NO) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo02(params.get(FlReportConst.FL_RPT_CONTRACT_REQ_NO));
			} 
			if(params.get(FlReportConst.FL_RPT_SUPPL_INV_NO) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo02(params.get(FlReportConst.FL_RPT_SUPPL_INV_NO));
			} 
			
			if(params.get(FlReportConst.FL_RPT_PURCHASE_ORDER) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo03(params.get(FlReportConst.FL_RPT_PURCHASE_ORDER));
			}
			
			if(params.get(FlReportConst.FL_RPT_COTRACT_ORDER) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo03(params.get(FlReportConst.FL_RPT_COTRACT_ORDER));
			}
			
			if(params.get(FlReportConst.FL_RPT_DELI_ORD_NO) != null){
				restDtlDTO.getRptRequestDtl().setDocumentNo04(params.get(FlReportConst.FL_RPT_DELI_ORD_NO));
			}
			if(params.get(FlReportConst.FL_RPT_FRN_NO) != null){
				restDtlDTO.getRptRequestDtl().setDocumentNo04(params.get(FlReportConst.FL_RPT_FRN_NO));
			}
			if(params.get(FlReportConst.FL_RPT_STOP_INSTR_NO) != null){
				restDtlDTO.getRptRequestDtl().setDocumentNo04(params.get(FlReportConst.FL_RPT_STOP_INSTR_NO));
			}		
			if(params.get(FlReportConst.FL_RPT_INV_NO) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo04(params.get(FlReportConst.FL_RPT_INV_NO));
			}
			if(params.get(FlReportConst.FL_RPT_PURCHASE_REQUEST) != null){	
				restDtlDTO.getRptRequestDtl().setDocumentNo04(params.get(FlReportConst.FL_RPT_PURCHASE_REQUEST));
			}

			if(restDtlDTO.getRptRequestDtl().getDocumentNo01() != null || restDtlDTO.getRptRequestDtl().getDocumentNo02() != null || restDtlDTO.getRptRequestDtl().getDocumentNo03() != null || restDtlDTO.getRptRequestDtl().getDocumentNo04() != null ){
				addRptRequestDetail(restDtlDTO);	
			}
			//R5 #14076 end
				
		} catch (Exception e) {
			e.printStackTrace();
		}	
		getPopUpMsg(resDTO.getRequestId() != null ? resDTO.getRequestId().toString() : "");
		RequestContext.getCurrentInstance().execute("confirmationId.show()");
	}

	//R5 #14076 start
	public void addRptRequestDetail(RPTRequestDtlReqDTO restDtlDTO){
					
		IRPTRequestDtlSO rptRequestDtlSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,IRPTRequestDtlSO.class);
		if(restDtlDTO.getRptRequestDtl().getCreatedBy() == null){
			restDtlDTO.getRptRequestDtl().setCreatedBy(getFacesScopes()
				.getUserSessionBean().getUserId());
		}
		rptRequestDtlSO.saveRPTRequestDtlDTO(restDtlDTO);
	}
	//end
	/**
	 * reset search criteria values
	*/
	public void resetSearchCriteriaValues() {
		if(getManagedBean().isSupplier() || hasAuditGenUserRole()){
			getManagedBean().setMinistry(null);
			getManagedBean().setPtjGroup(null);
			getManagedBean().setPtj(null);
		}
		 getManagedBean().setSelectedYear(null); 
		 getManagedBean().setContractNo(null); 
		 getManagedBean().setPhysicalContractNo(null); 
		 getManagedBean().setContractRequestNo(null); 
		 getManagedBean().setPanelName(null); 
		 getManagedBean().setLoaNo(null); 
		 getManagedBean().setPhysicalLoaNo(null); 
		 getManagedBean().setPurchaseOrderNo(null); 
		 getManagedBean().setPkpNo(null); 
		 getManagedBean().setGroup(null); 
		 getManagedBean().setRequisitioner(null); 
		 getManagedBean().setApprover(null); 
		 getManagedBean().setReceivingOfficer(null); 
		 getManagedBean().setAcknowledgeOfficer(null); 
		 getManagedBean().setPayOfficer(null); 
		 getManagedBean().setStatusDescId(null); 
		 getManagedBean().setSelectedOrgProfile(null);//supplier name
		
	}
	
	/**
	 * The method used for generating a list of available display type of status
	 * as selection.
	 * 
	 * @return the selection list of display type
	 */
	public SelectItem[] getStatusOptions() {
		return PmCommonUtil.getRecordStatusOptions();
	}
	
	/**
	 * get the Pm zone list
	 * 
	 */
	public SelectItem[] getPmZoneList() {
		return PmCommonUtil.getPmZoneListOptions();
	}
	
	/**
	 * Get Ministry condition for construct the LOV listing
	 * @return String
	 */
	public String getMinistryCondition() {
		String condition = "";
		retrieveParameterCodeType();
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
	 * Get PTJ group condition for construct the LOV listing
	 * @return String
	 */
	public String constuctConditionFieldsForPTJGroup() {	
		String condition = "";
		if(getManagedBean().getMinistry() != null){
			condition = OrgProfileUtil.getFromToSearchBeanParentString(OrganizationType.DEPARTMENT.value(), OrganizationType.MINISTRY.value())+"^"+
					this.getManagedBean().getMinistry().getOrgProfileId();
		}
		
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
		}else if(hasAuditGenUserRole() && condition.trim().length() > 0){
			return condition;
		}
		return condition;
	}
	
	/**
	 * Get PTJ condition for construct the LOV listing
	 * @return String
	 */
	public String getPTJCondition() {
		String condition = "";
		if(getManagedBean().getPtjGroup() != null){
			condition = OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value())+
					"^"+this.getManagedBean().getPtjGroup().getOrgProfileId();
		}
		
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
		}else if(hasAuditGenUserRole() && condition.trim().length() > 0){
			return condition;
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
	 * Check is it login user is Government User
	 * 
	 * @return true, if user is Government User
	 */
	protected boolean hasGovernmentUserRole() {
		if (getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value()))
			return true;
		else if (getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()))
			return true;
		else if (getFacesScopes().hasRoleUser(RoleCode.RECEIVING_OFFICER.value()))
			return true;
		else if (getFacesScopes().hasRoleUser(RoleCode.ACKNOWLEDGE_OFFICER.value()))
			return true;		
		else if (getFacesScopes().hasRoleUser(RoleCode.PAY_OFFICER.value()))
			return true;
		else if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()))
			return true;
		else
			return false;		
	}
	
	/**
	 * Check is it login user is Government User
	 * 
	 * @return true, if user is Government User
	 */
	public boolean hasAuditGenUserRole() {
		if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value()))
			return true;
		else if (getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value()))
			return true;
		else
			return false;		
	}
	
	
	/**
	 * Checks if is supplier role.
	 *
	 * @return true, if is supplier role
	 */
	public boolean hasSupplierRole(){
		if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.FL_USER.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	
	/**
	 * Gets the default ptj and ministry search values based on user role.
	 *
	 */
	public void defaultPtjAndMinistryValues() {
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
	 * set the default date from and date to
	 *
	 */
	public void setDefaultDateFromAndDateTo() {
		if (getManagedBean().getFromDate() == null
				|| getManagedBean().getToDate() == null) {
			Calendar fromDate = Calendar.getInstance();
			fromDate.set(fromDate.get(Calendar.YEAR), 00, 01, 0, 00, 0);
			getManagedBean().setFromDate(fromDate.getTime());
			getManagedBean().setToDate(new Date());
		}
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
		return (hasGovernmentUserRole()	|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()) );
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
		return (hasGovernmentUserRole() || getManagedBean().getPtjGroup() == null );
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
		return (hasGovernmentUserRole() || getManagedBean().getMinistry() == null );
	}
	
	/**
	 * get the status drop down values
	*/
	public SelectItem[]  getStatusDropDownValues(String docType){
		return getStatusDropdownValues(ModuleCodeConst.ORDER_FULFILLMENT, docType);
	}
	
	/**
	 * get the requisitioner list
	 */
	public List<SelectItem> getRequisitionerList(Long userId,Long ptjId){
		if (null != userId) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,IPMReportSO.class);
			List<String> ptLst = reportSO.retrieveHighRoleCodeByUser(userId);
			if (CollectionUtils.isNotEmpty(ptLst)) {
				for (int i = 0; i < ptLst.size(); i++) {
					getManagedBean().getHighRoleCodeList().add(ptLst.get(i));
				}
			}
			
			if(getManagedBean().getRequisitionerList().isEmpty() || getManagedBean().getRequisitionerList() == null){
				getManagedBean().setRequisitionerList(new ArrayList<SelectItem>());
			}
			getManagedBean().getRequisitionerList().clear();
			if ((CollectionUtils.isNotEmpty(getManagedBean().getHighRoleCodeList()))
					&& (getManagedBean().getHighRoleCodeList().contains(RoleCode.REQUISITIONER.value()))) {
				List<Object[]> rlcdLst = reportSO.retrieveUserListByREQUISITIONER(userId,RoleCode.REQUISITIONER.value());
				if (CollectionUtils.isNotEmpty(rlcdLst)) {
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						getManagedBean().getRequisitionerList().add(selectItem);
					}
				}
			}else {
				List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(userId,RoleCode.REQUISITIONER.value(),ptjId);
				if (CollectionUtils.isNotEmpty(rlcdLst)) {
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						getManagedBean().getRequisitionerList().add(selectItem);
					}
				}
			}
		}
		return getManagedBean().getRequisitionerList();
	}
	
	/**
	 * get the approver list
	 */
	public List<SelectItem> getApproverList(Long userId,Long ptjId){
		if (null != userId) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IPMReportSO.class);
			List<String> ptLst = reportSO.retrieveHighRoleCodeByUser(userId);
			if (CollectionUtils.isNotEmpty(ptLst)) {
				for (int i = 0; i < ptLst.size(); i++) {
					getManagedBean().getHighRoleCodeList().add(ptLst.get(i));
				}
			}

			if(getManagedBean().getApproverList().isEmpty() || getManagedBean().getApproverList() == null){
				getManagedBean().setApproverList(new ArrayList<SelectItem>());
			}
			getManagedBean().getApproverList().clear();
			if (CollectionUtils.isNotEmpty(getManagedBean().getHighRoleCodeList())
					&& (getManagedBean().getHighRoleCodeList().contains(RoleCode.REQUISITIONER.value()))) {
				return null;
			}else {
				List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(userId,RoleCode.FL_APPROVER.value(), ptjId);
				if (CollectionUtils.isNotEmpty(rlcdLst)) {
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						getManagedBean().getApproverList().add(selectItem);
					}
				}
			}
		}
		return getManagedBean().getApproverList();
	}
	
	/**
	 * get the receiving officer list
	 */
	public List<SelectItem> getReceivingOfficerList(Long userId,Long ptjId){
		if (null != userId) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IPMReportSO.class);
			List<String> ptLst = reportSO.retrieveHighRoleCodeByUser(userId);
			if (CollectionUtils.isNotEmpty(ptLst)) {
				for (int i = 0; i < ptLst.size(); i++) {
					getManagedBean().getHighRoleCodeList().add(ptLst.get(i));
				}
			}

			if(getManagedBean().getReceivingOfficerList().isEmpty() || getManagedBean().getReceivingOfficerList() == null){
				getManagedBean().setReceivingOfficerList(new ArrayList<SelectItem>());
			}
			getManagedBean().getReceivingOfficerList().clear();
			if (CollectionUtils.isNotEmpty(getManagedBean().getHighRoleCodeList())
					&& (getManagedBean().getHighRoleCodeList().contains(RoleCode.REQUISITIONER.value()))) {
				return null;
			}else {
				List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(userId,RoleCode.RECEIVING_OFFICER.value(), ptjId);
				if (CollectionUtils.isNotEmpty(rlcdLst)) {
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						getManagedBean().getReceivingOfficerList().add(selectItem);
					}
				}
			}
		}
		return getManagedBean().getReceivingOfficerList();
	} 
	
	/**
	 * get the acknowledge officer list
	 */
	public List<SelectItem> getAcknowledgeOfficerList(Long userId,Long ptjId){
		if (null != userId) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IPMReportSO.class);
			List<String> ptLst = reportSO.retrieveHighRoleCodeByUser(userId);
			if (CollectionUtils.isNotEmpty(ptLst)) {
				for (int i = 0; i < ptLst.size(); i++) {
					getManagedBean().getHighRoleCodeList().add(ptLst.get(i));
				}
			}

			if(getManagedBean().getAcknowledgeOfficerList().isEmpty() || getManagedBean().getAcknowledgeOfficerList() == null){
				getManagedBean().setAcknowledgeOfficerList(new ArrayList<SelectItem>());
			}
			getManagedBean().getAcknowledgeOfficerList().clear();
			if (CollectionUtils.isNotEmpty(getManagedBean().getHighRoleCodeList())
					&& (getManagedBean().getHighRoleCodeList().contains(RoleCode.REQUISITIONER.value()))) {
				return null;
			}else {
				List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(userId,RoleCode.ACKNOWLEDGE_OFFICER.value(), ptjId);
				if (CollectionUtils.isNotEmpty(rlcdLst)) {
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						getManagedBean().getAcknowledgeOfficerList().add(selectItem);
					}
				}
			}
		}
		return getManagedBean().getAcknowledgeOfficerList();
	} 
	
	/**
	 * get the payOfficer list
	 *
	 */
	public List<SelectItem> getPayOfficerList(Long userId,Long ptjId){
		if (null != userId) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IPMReportSO.class);
			List<String> ptLst = reportSO.retrieveHighRoleCodeByUser(userId);
			if (CollectionUtils.isNotEmpty(ptLst)) {
				for (int i = 0; i < ptLst.size(); i++) {
					getManagedBean().getHighRoleCodeList().add(ptLst.get(i));
				}
			}

			if(getManagedBean().getPayOfficerList().isEmpty() || getManagedBean().getPayOfficerList() == null){
				getManagedBean().setPayOfficerList(new ArrayList<SelectItem>());
			}
			getManagedBean().getPayOfficerList().clear();
			if (CollectionUtils.isNotEmpty(getManagedBean().getHighRoleCodeList())
					&& (getManagedBean().getHighRoleCodeList().contains(RoleCode.REQUISITIONER.value()))) {
				return null;
			}else {
				List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(userId,RoleCode.PAY_OFFICER.value(), ptjId);
				if (CollectionUtils.isNotEmpty(rlcdLst)) {
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						getManagedBean().getPayOfficerList().add(selectItem);
					}
				}
			}
		}
		return getManagedBean().getPayOfficerList();
	} 
	
	/**
	 * retrieveParameterCodeType - Retrieve parameter code type.
	 */
	public void retrieveParameterCodeType() {
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			String parameterCodeType = getParameterCodeTypeString();
			if (StringUtils.isNotEmpty(parameterCodeType)
					&& (parameterCodeType.equals(ReportConst.PARAM_CODE_TYPE_FPJ)
							|| parameterCodeType.equals(ReportConst.PARAM_CODE_TYPE_FMI))) {
				getManagedBean().setSupplier(false);
			} else if (StringUtils.isNotEmpty(parameterCodeType)
					&& (parameterCodeType.equals(ReportConst.ROLE_CODE_SUP))) {
				getManagedBean().setSupplier(true);
				retrieveSupplierCompName();
			} 
		}
	}
	
	/**
	 * getParameterCodeTypeString - Gets the parameter code type string.
	 * 
	 * @return the parameterCodeType string
	 */
	public String getParameterCodeTypeString() {
		log.info("ReportBackingBean-getParameterCodeTypeString");
		Long userId = new FacesScopes().getUserSessionBean().getUserId();
		String parameterCodeType = null;
		if (null != userId) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IPMReportSO.class);
			List<Object[]> paramcodeLst = reportSO.retrieveParameterCodeByUserid(getFacesScopes().getUserSessionBean().getUserId());
			if (CollectionUtils.isNotEmpty(paramcodeLst)) {
				for (Object[] object : paramcodeLst) {
					parameterCodeType = object[0].toString();
				}
			}
		}
		return parameterCodeType;
	}
	
	/**
	 * retrieveSupplierCompName - Retrieve supplier company name.
	 */
	private void retrieveSupplierCompName() {
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IPMReportSO.class);
		List<Object[]> supplCompNameLst = reportSO.retrieveSupplierCompName(getFacesScopes().getUserSessionBean().getUserId());
		if (CollectionUtils.isNotEmpty(supplCompNameLst)) {
			for (Object[] object : supplCompNameLst) {
				getManagedBean().setSupplierCompName(object[0].toString());
				getManagedBean().setSupplierId(object[1].toString());
			}
		}
		
	}
	
}