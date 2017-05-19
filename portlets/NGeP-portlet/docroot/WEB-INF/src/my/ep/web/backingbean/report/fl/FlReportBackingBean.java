/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.report.fl;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import my.ep.biz.service.pm.authorization.orgprofile.IPtjReorgSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.orgprofile.PtjReorgReqDTO;
import my.ep.dto.pm.authorization.orgprofile.PtjReorgResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
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

	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public void retrieveReportListByCategory(String reportCategory) {
		retrieveReportListByModuleCodeAndSubModule(
				ModuleCodeConst.ORDER_FULFILLMENT.toUpperCase(),
				reportCategory);
	}
	
	public void onChangeReportName(){

		log.info("onChangeReportName");	
		resetAction();
		
		if (StringUtils.isNotBlank(getManagedBean().getReportCode())) {
			retrieveReportValueFromDb();
		}
		
//		if (PmReportConst.PM_LIST_OF_PROFILES_RPT_VAL.equals(getManagedBean()
//				.getReportCode()) ||
//				PmReportConst.PM_LIST_OF_ORGANIZATION_RPT_VAL.equals(getManagedBean()
//						.getReportCode())) {	
//			setCurrentActiveOrg();
//			retrieveOrgTypeMap();
//			this.getManagedBean().setSearchOrgCategory(PmReportConst.ORG_CAT_FEDERAL_GOVT);
//			retrieveOrgTypeAndOrgName();
//		}
	}
	
	public void generateFlReport(LinkedHashMap<String, String> params){
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IRPTRequestSO.class);		
		RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
		params.put(FlReportConst.PARAM_KEY_REPORT, getManagedBean().getReportName());
		params.put(FlReportConst.PARAM_FL_REPORT_ID_EQ, getManagedBean().getReportCode());

		reqDTO.setDefaultOutputType(this.getManagedBean()
				.getDefaultOutputType());
		reqDTO.setTempAccess(this.getManagedBean().isTempAccess());
		reqDTO.setParametersMap(params);
		reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
		RPTRequestResDTO resDTO=null;
		try {
			resDTO = rptRequestSO.generateReport(reqDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		getPopUpMsg(resDTO.getRequestId() != null ? resDTO.getRequestId().toString() : "");
		RequestContext.getCurrentInstance().execute("confirmationId.show()");
		
	
	}
	
	public void resetAction() {

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
	
	public SelectItem[] getPmZoneList() {
		return PmCommonUtil.getPmZoneListOptions();
	}
	
	private String constructFlParamStrings(LinkedHashMap<String, String> map){
		
		map.put(FlReportConst.PARAM_KEY_REPORT, getManagedBean().getReportName());
		map.put(FlReportConst.PARAM_FL_REPORT_ID_EQ, getManagedBean().getReportCode());
		
		
		return constructParameterString(map);
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
	protected boolean hasAuditGenUserRole() {
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
	
	
}