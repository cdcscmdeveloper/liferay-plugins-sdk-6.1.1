/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.taskassign;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections; //#38680
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.ct.IContractIntegrationSO;
import my.ep.biz.service.fl.IFulfilmentSO;
import my.ep.biz.service.pm.access.application.IAccessApplSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.taskassginment.ITaskAssignmentSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sm.supplierDataMaintenance.ISupplierDataMaintenanceSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constant.pm.ProfileManagementStatusConst;
import my.ep.constant.sc.QTStatusConst; //#38680
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.BpmConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.constants.SCProcessNameConst; //#38680
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.ct.ContractIntegrationReqDTO;
import my.ep.dto.ct.ContractIntegrationResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.access.application.AccessApplReqDTO;
import my.ep.dto.pm.access.application.AccessApplResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.taskassignment.TaskAssignmentReqDTO;
import my.ep.dto.pm.common.taskassignment.TaskAssignmentResDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.SourcingIntegrationResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmPendingRemoveTask;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmStatusRole;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.TaskAssignmentSearchBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.pm.taskassign.TaskReassignmentManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pm.helper.StatusHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.PmTaskReassignmentVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * Backing Bean for Task Reasssingment (new)
 * 
 * @author Ken
 * @since 07.09.2016
 * 
 */
@ManagedBean(name = "taskReassignmentBackingBean")
@ViewScoped
public class TaskReassignmentBackingBean extends
		BaseBackingBean<TaskReassignmentManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger
			.getLogger(TaskReassignmentBackingBean.class);

	// Navigation Details
	public static final String NAV_TO_SEARCH = "taskReassignment";

	// Mapper for DDL object
	private static Map<Long, PmParameter> orgTypeMapper;

	// Authorization
	private ISearchBean<PmParameterDesc> orgTypeSearchBean;

	// List for Paging
	// 20231012 #7465 - Start
	private PagingDataModel<WorklistItemVO> userTaskListDataModel = new UserTaskListDataModel();	
	private PagingDataModel<WorklistItemVO> groupTaskListDataModel = new GroupTaskListDataModel();
	private PagingDataModel<WorklistItemVO> othersTaskListDataModel = new OthersTaskListDataModel();
	// 20231012 #7465 - End
	private PagingDataModel<PmUser> userListDataModel = new userPagingDataModel();
	
	//[#25018] - start	
	private static final Map<String, String> adminRolesForBulkReassignment = new HashMap<>();
	private static final List<String> invalidRolesForBulkReassignmentLst = new ArrayList<>(); //Sonarlint fixing #38680
	private static final Map<Long, String> excludeRolesForBulkReassignment = new HashMap<>(); //Sonarlint fixing #38680
	
	//#38680 - Start
	private static final List<Long> excludeStatusIds = new ArrayList<>();
	
	private static final List<String> NOT_ALLOWED_TASKS = Arrays.asList(
			SCProcessNameConst.VIEW_SCORING,
			SCProcessNameConst.UPDATE_SCORING,
			SCProcessNameConst.VIEW_EC_EVALUATION,
			SCProcessNameConst.UPDATE_EC_EVALUATION,
			SCProcessNameConst.VIEW_OC_EVALUATION,
			SCProcessNameConst.UPDATE_OC_EVALUATION,
			SCProcessNameConst.VIEW_TEC_EVALUATION,
			SCProcessNameConst.UPDATE_TEC_EVALUATION,
			SCProcessNameConst.VIEW_FEC_EVALUATION,
			SCProcessNameConst.UPDATE_FEC_EVALUATION,
			SCProcessNameConst.VIEW_FC_SELECTION,
			SCProcessNameConst.UPDATE_FC_SELECTION,
			SCProcessNameConst.ENDORSE_SCORING,
			SCProcessNameConst.SIGNOFF_SCORING);
	
	private static final Set<String> COMMITTEE_TYPES;
	//#38680 - End

	static {		
		adminRolesForBulkReassignment.put(RoleCode.UPS_ADMIN.value(), RoleCode.UPS_ADMIN.value());
		adminRolesForBulkReassignment.put(RoleCode.PROCUREMENT_ADMIN.value(), RoleCode.PROCUREMENT_ADMIN.value());
		adminRolesForBulkReassignment.put(RoleCode.FACTORING_ADMIN.value(), RoleCode.FACTORING_ADMIN.value());
		adminRolesForBulkReassignment.put(RoleCode.BPK_ADMIN.value(), RoleCode.BPK_ADMIN.value());
		adminRolesForBulkReassignment.put(RoleCode.BPK_TEMPL_ADMIN.value(), RoleCode.BPK_TEMPL_ADMIN.value());
		adminRolesForBulkReassignment.put(RoleCode.CM_ADMIN.value(), RoleCode.CM_ADMIN.value());
		adminRolesForBulkReassignment.put(RoleCode.EP_ADMIN.value(), RoleCode.EP_ADMIN.value());
		adminRolesForBulkReassignment.put(RoleCode.EP_ADMIN_1.value(), RoleCode.EP_ADMIN_1.value());
		adminRolesForBulkReassignment.put(RoleCode.EP_ADMIN_2.value(), RoleCode.EP_ADMIN_2.value());
		adminRolesForBulkReassignment.put(RoleCode.JABATAN_ADMIN.value(), RoleCode.JABATAN_ADMIN.value());
		adminRolesForBulkReassignment.put(RoleCode.MINISTRY_ADMIN.value(), RoleCode.MINISTRY_ADMIN.value());
		adminRolesForBulkReassignment.put(RoleCode.MIN_CONTRACT_ADMIN.value(), RoleCode.MIN_CONTRACT_ADMIN.value());
		adminRolesForBulkReassignment.put(RoleCode.PTJ_ADMIN.value(), RoleCode.PTJ_ADMIN.value());
		adminRolesForBulkReassignment.put(RoleCode.SUPPORT_ADMIN.value(), RoleCode.SUPPORT_ADMIN.value());
		
		invalidRolesForBulkReassignmentLst.add(RoleCode.FEC_CHAIRPERSON.value());
		invalidRolesForBulkReassignmentLst.add(RoleCode.FEC_MEMBER.value());
		invalidRolesForBulkReassignmentLst.add(RoleCode.FEC_SECRETARY.value());
		invalidRolesForBulkReassignmentLst.add(RoleCode.TEC_CHAIRPERSON.value());
		invalidRolesForBulkReassignmentLst.add(RoleCode.TEC_MEMBER.value());
		invalidRolesForBulkReassignmentLst.add(RoleCode.TEC_SECRETARY.value());
		invalidRolesForBulkReassignmentLst.add(RoleCode.EC_CHAIRPERSON.value());
		invalidRolesForBulkReassignmentLst.add(RoleCode.EC_MEMBER.value());
		invalidRolesForBulkReassignmentLst.add(RoleCode.EC_SECRETARY.value());
		invalidRolesForBulkReassignmentLst.add(RoleCode.SC_CHAIRPERSON.value());
		invalidRolesForBulkReassignmentLst.add(RoleCode.SC_MEMBER.value());
		invalidRolesForBulkReassignmentLst.add(RoleCode.SC_SECRETARY.value());
		invalidRolesForBulkReassignmentLst.add(RoleCode.OC_CHAIRPERSON.value());
		invalidRolesForBulkReassignmentLst.add(RoleCode.OC_MEMBER.value());
		invalidRolesForBulkReassignmentLst.add(RoleCode.OC_SECRETARY.value());
		invalidRolesForBulkReassignmentLst.add(RoleCode.PBA_MEMBER.value());
		invalidRolesForBulkReassignmentLst.add(RoleCode.PPB_MEMBER.value());
		
		//#38680- Start
		excludeStatusIds.add(QTStatusConst.SE_PENDING_ENDORSMENT);
		excludeStatusIds.add(QTStatusConst.EC_PENDING_COMPLIANCE_PROPOSAL);
		excludeStatusIds.add(QTStatusConst.OC_PENDING_COMPLIANCE_PROPOSAL);
		excludeStatusIds.add(QTStatusConst.OC_PENDING_COMPLIANCE_VERIFICATION_REVISION);
		excludeStatusIds.add(QTStatusConst.FEC_PENDING_EVALUATION_PROPOSAL);
		excludeStatusIds.add(QTStatusConst.TEC_PENDING_EVALUATION_PROPOSAL);
		excludeStatusIds.add(QTStatusConst.QT_SUPPLIER_FINALIZATION);
		excludeStatusIds.add(QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING);
		excludeStatusIds.add(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT);
		excludeStatusIds.add(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF);
		excludeStatusIds.add(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION);
		excludeStatusIds.add(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF_WITH_REPUBLISH);
		excludeStatusIds.add(QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION);
		excludeStatusIds.add(QTStatusConst.SF_PENDING_BIDDING_FINALIZATION);
		excludeStatusIds.add(QTStatusConst.SF_PENDING_BIDDING_FINALIZATION_REVISION);
		excludeStatusIds.add(QTStatusConst.QT_SUPPLIER_FINALIZATION_REVISION);
		
		
		
		Set<String> s = new HashSet<>();
		s.add(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
		s.add(ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE);
		s.add(ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE);
		s.add(ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE);
		s.add(ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE);
		COMMITTEE_TYPES = Collections.unmodifiableSet(s);
		//#38680- End
	}
	//[#25018] - end
	
	public void preLoading() throws EpException {
		log.info("preLoading");
		resetTaskAssignSearchBean();
		retrieveOrgTypeMap();
		//[#25018] - start		
		this.getManagedBean().setShowParentMsg(true);
		this.getManagedBean().setShowChildMsg(false);
		setIsAdminUser();
		retrieveStatusIdsForInvalidBulkTask();
		if (this.getManagedBean().getSelectedTasksToBulkReassign() != null && 
				this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
			this.getManagedBean().getSelectedTasksToBulkReassign().clear();
		}
		//[#25018] - end
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_SEARCH);
		if (this.getManagedBean().isEnableDeleteGroupTasksButton()) {
			this.getManagedBean().setEnableDeleteGroupTasksButton(false);
		}
		if (this.getManagedBean().isEnableDeleteOthersTasksButton()) {
			this.getManagedBean().setEnableDeleteOthersTasksButton(false);
		}
	}

	/**
	 * retrieve and set org type mapper
	 */
	private void retrieveOrgTypeMap() {
		log.info("retrieveOrgTypeMap");
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescListBySearchBean(
						ParameterTypeConst.ORGANISATION_TYPES, null);
		setOrgTypeMapper(new HashMap<Long, PmParameter>());
		for (PmParameterDesc pmDesc : parameterDescs) {
			getOrgTypeMapper().put(pmDesc.getPmParameter().getParameterId(),
					pmDesc.getPmParameter());
		}
	}

	/**
	 * reset search bean fields
	 */
	public void resetTaskAssignSearchBean() {
		log.info("resetTaskAssignSearchBean");
		this.getManagedBean().setTaskAssignmentSearchBean(
				new TaskAssignmentSearchBean());
		this.getManagedBean().getTaskAssignmentSearchBean()
				.setExcludeUser(getFacesScopes().getUserSessionBean());
		this.setCurrentActiveOrg();
		this.getManagedBean().setTaskLazy(false);
		resetAllTaskList();
		resetReassigneeList();
		resetCheckAllTasks();
		this.getManagedBean().setShowParentMsg(true);	// [#25018]
		this.getManagedBean().setShowChildMsg(false);	// [#25018]
		
		//7465, 29822 - start
		this.getManagedBean().setEnableRemoveAllGroupTasksButton(false);
		this.getManagedBean().setEnableRemoveAllOthersTasksButton(false);
		this.getManagedBean().setShowRemoveProcessMessageGroup(false);
		this.getManagedBean().setShowRemoveProcessMessageOther(false);
		//7465, 29822 - end
		//7465 - start reendorse
		this.getManagedBean().setEnableDeleteGroupTasksButton(false);
		this.getManagedBean().setEnableDeleteOthersTasksButton(false);
		this.getManagedBean().setSelectedTasksToBulkReassign(null);
		//7465 - end reendorse

		// 20231012 #7465 - Start
		if (this.getManagedBean().isFromUserMaintenanceDetails()) {
			reinitRedirectedTaskReassignmentSearchBean();
		}
		// 20231012 #7465 - End
	}
	
	/**
	 * To Reset All Task List (BPM)
	 */
	private void resetAllTaskList(){
		this.userTaskListDataModel.resetData();
		this.groupTaskListDataModel.resetData();
		this.othersTaskListDataModel.resetData();
	}
	
	/**
	 * To Reload All Task List (BPM)
	 */
	private void reloadAllTaskList(){
		this.userTaskListDataModel.reloadData();
		this.groupTaskListDataModel.reloadData();
		this.othersTaskListDataModel.reloadData();
	}

	/**
	 * retrieve and set the current active org
	 */
	private void setCurrentActiveOrg() {
		log.info("setCurrentActiveOrg");
		if (getFacesScopes().getUserSessionBean().getUserId() == null) {
			throw new EpException(MessageConst.ERROR_770149);
		}

		List<PmUserOrg> userOrgList = PmCommonUtil
				.retrieveActiveOrgWithoutRole(getFacesScopes()
						.getUserSessionBean().getUserId(), false);

		if (userOrgList == null || userOrgList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_770105);
		}

		PmOrgProfile pmOrgProfile = userOrgList.get(0).getPmOrgProfile();
		this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);

		if (ParameterConst.ORG_TYPE_PTJ.equals(pmOrgProfile.getOrgTypeCode()
				.getParameterCode())
				|| ParameterConst.ORG_TYPE_MINISTRY.equals(pmOrgProfile
						.getOrgTypeCode().getParameterCode())
				|| ParameterConst.ORG_TYPE_JABATAN.equals(pmOrgProfile
						.getOrgTypeCode().getParameterCode())) {
			if (ParameterConst.ORG_TYPE_PTJ.equals(pmOrgProfile
					.getOrgTypeCode().getParameterCode())) {
				// default org profile
				this.getManagedBean().getTaskAssignmentSearchBean()
						.setOrgProfile(pmOrgProfile);
				this.getManagedBean().setOrgProfileRequired(false);
			}else{
				this.getManagedBean().setOrgProfileRequired(true);
			}
			
			PmParameter ptjParam = getParameterByTypeAndCode(
					ParameterTypeConst.ORGANISATION_TYPES,
					ParameterConst.ORG_TYPE_PTJ);
			this.getManagedBean().getTaskAssignmentSearchBean()
					.setOrgType(ptjParam);

		} else if(ParameterConst.ORG_TYPE_SUPPORT.equals(pmOrgProfile.getOrgTypeCode()
				.getParameterCode())){
			this.getManagedBean().getTaskAssignmentSearchBean()
			.setOrgProfile(pmOrgProfile);
			this.getManagedBean().setOrgProfileRequired(false);
			PmParameter supportParam = getParameterByTypeAndCode(
					ParameterTypeConst.ORGANISATION_TYPES,
					ParameterConst.ORG_TYPE_SUPPORT);
			this.getManagedBean().getTaskAssignmentSearchBean()
					.setOrgType(supportParam);
			
		}else if(ParameterConst.ORG_TYPE_BPK.equals(pmOrgProfile.getOrgTypeCode()
				.getParameterCode())){
			this.getManagedBean().getTaskAssignmentSearchBean()
			.setOrgProfile(pmOrgProfile);
			this.getManagedBean().setOrgProfileRequired(false);
			PmParameter bpkParam = getParameterByTypeAndCode(
					ParameterTypeConst.ORGANISATION_TYPES,
					ParameterConst.ORG_TYPE_BPK);
			this.getManagedBean().getTaskAssignmentSearchBean()
					.setOrgType(bpkParam);
			
		}else {
			this.getManagedBean().setOrgProfileRequired(true);
		}
	}

	/**
	 * change organization type
	 */
	public void changeOrgType() {
		log.info("changeOrgType");
		if (this.getManagedBean().getTaskAssignmentSearchBean() != null) {
			if (this.getManagedBean().getTaskAssignmentSearchBean()
					.getOrgProfile() == null) {
				this.getManagedBean().getTaskAssignmentSearchBean()
						.setOrgProfile(new PmOrgProfile());
			}
			if (this.getManagedBean().getTaskAssignmentSearchBean().getUser() == null) {
				this.getManagedBean().getTaskAssignmentSearchBean()
						.setUser(new PmUser());
			}
			this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile()
					.setOrgName("");
			this.getManagedBean().getTaskAssignmentSearchBean().getUser()
					.setUserName("");
			this.getManagedBean().getTaskAssignmentSearchBean().getUser()
					.setLoginId("");
			this.getManagedBean().getTaskAssignmentSearchBean()
					.setDocType(null);
			this.getManagedBean().getTaskAssignmentSearchBean()
				.setSearched(false);
			this.getManagedBean().getTaskAssignmentSearchBean().setDocNo("");
			
			if (this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile() != null && 
					this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile()
					.getOrgName() != null) {
				this.getManagedBean().getTaskAssignmentSearchBean().setOrgProfile(new PmOrgProfile());
				this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile()
				.setOrgName("");
			}
			

			if (this.getManagedBean().getTaskAssignmentSearchBean()
					.getOrgType().getParameterId() != null) {
				PmParameter pmParameter = getOrgTypeMapper().get(
						this.getManagedBean().getTaskAssignmentSearchBean()
								.getOrgType().getParameterId());
				this.getManagedBean().getTaskAssignmentSearchBean()
						.getOrgType()
						.setParameterCode(pmParameter.getParameterCode());
				this.getManagedBean().setOrgProfileRequired(true);

			}
			this.getManagedBean().setTaskLazy(false);
			resetAllTaskList();
			resetReassigneeList();
			resetCheckAllTasks();
		}
	}

	/**
	 * To Construct condition fields for lovMinistry
	 * 
	 * @return String
	 */
	public String constructConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterId^");
		sb.append(getManagedBean().getTaskAssignmentSearchBean().getOrgType()
				.getParameterId());
		sb.append("|recordStatus^1");
		if (OrganizationType.MINISTRY.value().equals(
				getManagedBean().getTaskAssignmentSearchBean().getOrgType()
						.getParameterCode())) {
			if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
				sb.append("|orgProfileId^"
						+ this.getManagedBean().getCurrentActiveOrg()
								.getOrgProfileId());
			}
		} else if (OrganizationType.DEPARTMENT.value().equals(
				getManagedBean().getTaskAssignmentSearchBean().getOrgType()
						.getParameterCode())) {
			if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
				sb.append("|");
				sb.append(OrgProfileUtil.getFromToSearchBeanParentString(
						OrganizationType.DEPARTMENT.value(),
						OrganizationType.MINISTRY.value()));
				sb.append("^");
				sb.append(this.getManagedBean().getCurrentActiveOrg()
						.getOrgProfileId());
			} else if (getFacesScopes().hasRoleUser(
					RoleCode.JABATAN_ADMIN.value())) {
				sb.append("|orgProfileId^"
						+ this.getManagedBean().getCurrentActiveOrg()
								.getOrgProfileId());
			}
		} else if (OrganizationType.DIVISION.value().equals(
				getManagedBean().getTaskAssignmentSearchBean().getOrgType()
						.getParameterCode())) {
			if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
				sb.append("|");
				sb.append(OrgProfileUtil
						.getSearchBeanParentString(OrganizationType.MINISTRY
								.value()));
				sb.append("^");
				sb.append(this.getManagedBean().getCurrentActiveOrg()
						.getOrgProfileId());
			} else if (getFacesScopes().hasRoleUser(
					RoleCode.JABATAN_ADMIN.value())) {
				sb.append("|");
				sb.append(OrgProfileUtil
						.getSearchBeanParentString(OrganizationType.DEPARTMENT
								.value()));
				sb.append("^");
				sb.append(this.getManagedBean().getCurrentActiveOrg()
						.getOrgProfileId());
			} else if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())) {
				sb.append("|orgProfileId^"
						+ this.getManagedBean().getCurrentActiveOrg()
								.getOrgProfileId());
			}
		}

		return sb.toString();
	}

	/**
	 * Reset Fields
	 * **/
	public void onChangeUser(BaseEntity entity) {
		if (getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles() != null
				&& !getManagedBean().getTaskAssignmentSearchBean()
						.getSelectedRoles().isEmpty()) {
			getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles()
					.clear();
		}
		this.getManagedBean().getTaskAssignmentSearchBean()
			.setSearched(false);
		this.getManagedBean().getTaskAssignmentSearchBean().setDocType(null);
		this.getManagedBean().setTaskLazy(false);
		resetAllTaskList();
		resetCheckAllTasks();
		//7465 - start  - reendorse
		this.getManagedBean().setShowRemoveProcessMessageGroup(false);
		this.getManagedBean().setShowRemoveProcessMessageOther(false);
		this.getManagedBean().setEnableRemoveAllGroupTasksButton(false);
		this.getManagedBean().setEnableRemoveAllOthersTasksButton(false);
		this.getManagedBean().setEnableDeleteGroupTasksButton(false);
		this.getManagedBean().setEnableDeleteOthersTasksButton(false);
		this.getManagedBean().setSelectedTasksToBulkReassign(null);
		//7465 - end  - reendorse
	}

	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public void selectPtjAction(PmOrgProfile orgProfile) {
		this.getManagedBean().getTaskAssignmentSearchBean()
				.setOrgProfile(orgProfile);
		onChangeOrg(orgProfile);
	}

	/**
	 * Reset Fields
	 * **/
	public void onChangeOrg(BaseEntity entity) {
		if (getManagedBean().getTaskAssignmentSearchBean().getUser() != null) {
			getManagedBean().getTaskAssignmentSearchBean().setUser(null);
		}

		if (getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles() != null
				&& !getManagedBean().getTaskAssignmentSearchBean()
						.getSelectedRoles().isEmpty()) {
			getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles()
					.clear();
		}
		this.getManagedBean().getTaskAssignmentSearchBean()
			.setSearched(false);
		this.getManagedBean().setTaskLazy(false);
		resetAllTaskList();
		resetCheckAllTasks();
	}

	/**
	 * To get the selected user role list
	 * 
	 * @return SelectItem[] 
	 */
	public SelectItem[] getRoleList() {
		IUserSO userSO = null;
		SelectItem[] items = null;
		try {
			if (getManagedBean().getTaskAssignmentSearchBean().getUser() != null
					&& getManagedBean().getTaskAssignmentSearchBean().getUser()
							.getLoginId() != null
					&& getManagedBean().getTaskAssignmentSearchBean().getUser()
							.getLoginId().length() > 0
					&& getManagedBean().getTaskAssignmentSearchBean().getUser()
							.getUserName() != null
					&& getManagedBean().getTaskAssignmentSearchBean().getUser()
							.getUserName().length() > 0) {
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				List<Long> users = new ArrayList<Long>();
				users.add(getManagedBean().getTaskAssignmentSearchBean()
						.getUser().getUserId());
				UserReqDTO userReqDTO = new UserReqDTO();
				userReqDTO.setUserIds(users);

				// get ups related roles only for UPS_ADMIN
				if (getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value())) {
					userReqDTO.setRoleCodes(new ArrayList<String>(Arrays
							.asList(ProfileManagementConst.UPS_ROLES)));
				}

				List<PmUserRole> userRoles = (userSO
						.retrieveUserRoleListByUserIdsAndRoleCodes(userReqDTO))
						.getUserRoleList();

				List<String> tempRoleCodeList = new ArrayList<String>();
				for (PmUserRole userRole : userRoles) {
					//Redmine Bug #31732 - START
					if(userRole.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE))
						tempRoleCodeList.add(userRole.getPmRole().getRoleCode());
					//Redmine Bug #31732 - END
				}
				// get unique role code
				Set<String> tempRoleCodeSet = new HashSet<String>(
						tempRoleCodeList);
				items = new SelectItem[tempRoleCodeSet.size()];
				String userRole = null;
				getManagedBean().setRoleCodes(
						new ArrayList<String>(tempRoleCodeSet));

				for (int i = 0; i < getManagedBean().getRoleCodes().size(); i++) {
					userRole = getManagedBean().getRoleCodes().get(i);
					items[i] = new SelectItem(userRole,
							PmCommonUtil.getRoleDescByRoleCode(userRole));
				}

			}
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		} finally {
			userSO = null;
		}
		return items;
	}
	
	// #20117
	public List<PmUser> searchReassignees(String docNo) {
		
		log.info("Searching reassignees..");
		
		List<PmUser> users = new ArrayList<PmUser>();
		PmUser user = new PmUser();
		String orgCode = null;
		Long profileId = 0L;
		
		if (getManagedBean().getTaskAssignmentSearchBean().getOrgProfile() != null) {
			profileId = getManagedBean().getTaskAssignmentSearchBean().getOrgProfile().getOrgProfileId();
			orgCode = getManagedBean().getTaskAssignmentSearchBean().getOrgProfile().getOrgCode();
		} else {
			profileId = getManagedBean().getCurrentActiveOrg().getOrgProfileId();
			orgCode = getManagedBean().getCurrentActiveOrg().getOrgCode();
		}
		
		/* Due to a possibility of having more than one profileId for the selected Org Name, 
		 * we need to safeguard the Org returned is always correct by evaluating the Org Code first */
		PmOrgProfile orgProfileDetail = PmCommonUtil.retrieveOrgProfileDtlByCode(orgCode);
		List <PmUser> userOrgList = null;
		
		if (orgProfileDetail != null) {
			userOrgList = PmCommonUtil.retrieveUserListByOrgProfileId(
					orgProfileDetail.getOrgProfileId());
		}
		
		BpmResponseDTO bpmResDTO = null;
		
		BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
		bpmReqDTO.setDocNumber(docNo);
		bpmReqDTO.setState(BpmConstants.STATE_ASSIGNED);
		
		if (SmServiceLookup.getBpmSO().getReassignedTasks(bpmReqDTO) == null) {
			throw new EpException(MessageConst.ERROR_770208);
		} else {
			bpmResDTO = SmServiceLookup.getBpmSO().getReassignedTasks(bpmReqDTO);
			
			if (!bpmResDTO.isSuccessful()) {
				//throw new EpException(MessageConst.ERROR_770100);
				throw new EpException(MessageConst.ERROR_770208);
			}
			
			if (bpmResDTO.getWorklistVO() != null) {
				for (WorklistItemVO task : bpmResDTO.getWorklistVO().getWorklistItem()) {
					//#38680- Start
					if (!(excludeStatusIds.contains(task.getDocStatus()
							.longValue()) && isTaskName(task.getTaskName()))) {
					for (String loginId : task.getAssignees()) {
						user = PmCommonUtil.retrieveUserByLoginId(loginId);
						
						// Ensures only users within the selected Organization will be listed
						if (userOrgList != null && userOrgList.size() > 0) {
							for (PmUser orgList : userOrgList) {
								if (orgList.getLoginId().equals(user.getLoginId())) {
									users.add(user);
								}
							}
						}
						log.info("USER.INFO= ["+user.getLoginId()+" "+user.getUserName()+"] "+
								profileId+" : "+orgCode+" : "+
								user.getRecordStatusFlg()+" : "+
								this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile().getRecordStatusFlg());
					}
				   }//#38680- End
				}
			}
		}
		log.info("users: "+users);
		return users;
	}
	
	public SelectItem[] setReassigneeRoleList(Long userId) {
		
		SelectItem[] items = null;
		IUserSO userSO = null;
		
		try {
			
			userSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			List<Long> users = new ArrayList<Long>();
			users.add(userId);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserIds(users);
			
			if (getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value())) {
				userReqDTO.setRoleCodes(new ArrayList<String>(Arrays
						.asList(ProfileManagementConst.UPS_ROLES)));
			}
			List<PmUserRole> userRoles = (userSO.retrieveUserRoleListByUserIdsAndRoleCodes(userReqDTO))
					.getUserRoleList();
			List<String> tempRoleCodeList = new ArrayList<String>();
			
			for (PmUserRole userRole : userRoles) {
				tempRoleCodeList.add(userRole.getPmRole().getRoleCode());
			}
			
			Set<String> tempRoleCodeSet = new HashSet<String>(tempRoleCodeList);
			items = new SelectItem[tempRoleCodeSet.size()];
			String userRole = null;
			getManagedBean().setRoleCodes(new ArrayList<String>(tempRoleCodeSet));
	
			for (int i = 0; i < getManagedBean().getRoleCodes().size(); i++) {
				userRole = getManagedBean().getRoleCodes().get(i);
				log.info("userRole is: "+userRole);
				items[i] = new SelectItem(userRole,
						PmCommonUtil.getRoleDescByRoleCode(userRole));
			}
			
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		} finally {
			userSO = null;
		}
		return items;
	}
	
	
	/**
	 * This method used for construct condition fields for LOV (Lov Id :
	 * PM_STATUS_ROLE)
	 **/
	public String constuctConditionFields() {
		IStatusSO statusSO = null;
		StringBuilder sb = new StringBuilder();
		try {
			StatusReqDTO reqDTO = null;
			if (getManagedBean().getTaskAssignmentSearchBean()
					.getSelectedRoles() != null
					&& !getManagedBean().getTaskAssignmentSearchBean()
							.getSelectedRoles().isEmpty()) {
				reqDTO = new StatusReqDTO();
				reqDTO.setRoleCodeList(getManagedBean()
						.getTaskAssignmentSearchBean().getSelectedRoles());
			} else if (getManagedBean().getRoleCodes() != null
					&& !getManagedBean().getRoleCodes().isEmpty()) {
				reqDTO = new StatusReqDTO();
				reqDTO.setRoleCodeList(getManagedBean().getRoleCodes());
			}

			if (reqDTO != null) {
				reqDTO.setReassignable(true);
				statusSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
				StatusResDTO resDTO = statusSO
						.retrieveStatusRoleListByRoleCode(reqDTO);

				if (resDTO.isSuccessful()) {
					List<String> tempDocTypeList = new ArrayList<String>();
					for (PmStatusRole statusRole : resDTO.getStatusRoleList()) {
						tempDocTypeList.add(statusRole.getPmStatus()
								.getDocType());
					}
					// get unique doc type
					Set<String> tempDocTypeSet = new HashSet<String>(
							tempDocTypeList);

					sb.append("pmParameter.parameterType^");
					sb.append(ParameterTypeConst.DOCUMENT_TYPES);
					sb.append("|pmParameter.parameterCode^");

					int count = 0;
					for (String docType : tempDocTypeSet) {
						if (count != 0) {
							sb.append(",");
						}
						sb.append(docType);
						count++;
					}

					sb.append("^9");
					sb.append("|languageCode^");
					sb.append(new FacesScopes().getUserLocale().getLanguage());
					sb.append("|pmParameter.recordStatusFlg^");
					sb.append(DbValueConst.RECORD_STATUS_ACTIVE);
				}
			}
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		} finally {
			statusSO = null;
		}

		return sb.toString();
	}

	/**
	 * To construct user condisiton fields for opUserLov
	 * 
	 * @return String
	 */
	public String constructUserConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("pmUserOrgs.pmOrgProfile.orgProfileId^");
		sb.append(getManagedBean().getTaskAssignmentSearchBean()
				.getOrgProfile().getOrgProfileId());
		sb.append("|userId^");
		sb.append(getManagedBean().getTaskAssignmentSearchBean()
				.getExcludeUser().getUserId());
		sb.append("^2");
		sb.append("|recordStatus^1");
		if (getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value())) {
			sb.append("|pmUserOrgs.pmUserRoles.pmRole.roleCode^");
			String roleCode = null;
			for (int i = 0; i < ProfileManagementConst.UPS_ROLES.length; i++) {
				roleCode = ProfileManagementConst.UPS_ROLES[i];
				sb.append(roleCode);
				if (i != ProfileManagementConst.UPS_ROLES.length - 1) {
					sb.append(",");
				}
			}
			sb.append("^9");
		}
		return sb.toString();
	}

	/**
	 * search task assignment
	 */
	
	// #20117
	public void searchTasks() {
		
		this.getManagedBean().setShowParentMsg(true);	// [#25018]
		this.getManagedBean().setShowChildMsg(false);	// [#25018]
		// [#25018] - start
		if (this.getManagedBean().getSelectedTasksToBulkReassign() != null && 
				this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
			this.getManagedBean().getSelectedTasksToBulkReassign().clear();
		}
		// [#25018] - end
		
		if (!(this.getManagedBean().getTaskAssignmentSearchBean()
				.getOrgProfile() != null && this.getManagedBean()
				.getTaskAssignmentSearchBean().getOrgProfile()
				.getOrgProfileId() != null)) {
			throw new EpException(MessageConst.ERROR_770091);
		}
		this.getManagedBean().setResultOrgProfile(
				this.getManagedBean().getTaskAssignmentSearchBean()
						.getOrgProfile());
		
		this.getManagedBean().getTaskAssignmentSearchBean().setSearched(true);
		this.getManagedBean().setTaskLazy(true);

		//Ensures at least User Name or DocNo is specified
		if (this.getManagedBean().getTaskAssignmentSearchBean().getDocNo().isEmpty() &&
			 !(this.getManagedBean().getTaskAssignmentSearchBean().getUser() != null
				&& this.getManagedBean().getTaskAssignmentSearchBean()
						.getUser().getUserId() != null && this.getManagedBean()
				.getTaskAssignmentSearchBean().getUser().getLoginId().length() > 0)) {
			throw new EpException(MessageConst.ERROR_770206);
		}
		if (this.getManagedBean().getTaskAssignmentSearchBean().getDocNo().isEmpty()) {
			resetReassigneeList(); //If the Reset button is not clicked when performing the next search
			resetCheckAllTasks();
			searchTaskAssign();
			//7465, 29822 - start
			checkPendingTaskRemove();
			//7465, 29822 - end
			log.info("[#22117] - isBpkAdminUser: " + this.getManagedBean().isBpkAdminUser());
		} else {
			resetReassigneeList();
			resetCheckAllTasks();
			//7465 - start
			if (this.getManagedBean().getTaskAssignmentSearchBean().getUser() != null && this.getManagedBean()
					.getTaskAssignmentSearchBean().getUser().getLoginId() != null) {
				searchUsers();
				checkPendingTaskRemove();
			} else {
				searchUsers();
			}
			//7465 - end
		}
	}
	
	public void searchTaskAssign() {
		log.info("searchDocument - Start");
		
		this.getManagedBean().setResultUser(this.getManagedBean().getTaskAssignmentSearchBean().getUser());
		this.getManagedBean().setStatusIds(retrieveAvailableStatusId());
		
		reloadAllTaskList();
		
		//[#22117 - start]
		this.getManagedBean().setBpkAdminUser(checkBpkAdminUser());
		if(this.getManagedBean().isBpkAdminUser()) {
			//disable check box for user task
			this.getManagedBean().setCheckAllUserTasksEnabled(false);
			//disable check box for group task
			this.getManagedBean().setCheckAllGroupTasksEnabled(false);
			//disable button for group task
			this.getManagedBean().setEnableDeleteGroupTasksButton(false);
			//disable check box for other task
			this.getManagedBean().setCheckAllOthersTasksEnabled(false);
			//disable button for other task
			this.getManagedBean().setEnableDeleteOthersTasksButton(false);
		}
		//[#22117 - end]
		log.info("searchDocument - End");

	}
	
	public void searchUsers() {
		
		log.info("Start querying for users associated with the specified docNo..");
		
		PmUser reassignee = null;
		List<PmUser> reassignees = searchReassignees(this.getManagedBean().
				getTaskAssignmentSearchBean().getDocNo());
		
		for (int r = 0; r < reassignees.size(); r++) {
			 try {
				  if (reassignees.get(r) != null) {
					  reassignee = reassignees.get(r);
					  
					  if (this.getManagedBean().getTaskAssignmentSearchBean().getUser() != null) {
							if (reassignee.getLoginId().equals(this.getManagedBean()
									.getTaskAssignmentSearchBean().getUser().getLoginId())) {
								this.getManagedBean().getReassigneeList()
								 .put(this.getManagedBean().getTaskAssignmentSearchBean().getUser().getLoginId(),
									  this.getManagedBean().getTaskAssignmentSearchBean().getUser().getUserName());
								break;
							}
						} else {
							this.getManagedBean().getReassigneeList()
								.put(reassignee.getLoginId(),reassignee.getUserName());
						}
				  }
	        } catch (NullPointerException e) {
		        	log.info("There is an empty assignee! "+e.getMessage());
		    }
		}
		
		if (this.getManagedBean().getReassigneeList().isEmpty()) {
			// #35325 Start
			// Workaround to reset already loaded data and error messages since  
			// existing handling is throwing exception before performing other actions
			reloadAllTaskList();
			checkPendingTaskRemove();
			// #35325 End
			throw new EpException(MessageConst.ERROR_770208);
		} else {
			if (this.getManagedBean().getTaskAssignmentSearchBean().getUser() != null) {
				this.getManagedBean().setResultUser(this.getManagedBean().getTaskAssignmentSearchBean().getUser());
			} else {
				this.getManagedBean().setResultUser(reassignees.get(0));
				setReassigneeRoleList(reassignees.get(0).getUserId());
			}
			this.getManagedBean().setStatusIds(retrieveAvailableStatusId());
			this.getManagedBean().setShowReassigneeList(true);
			
			if (this.getManagedBean().getTaskAssignmentSearchBean().getUser() != null) {
				reloadAllTaskList();
			}
		}
		log.info("Displaying users: "+this.getManagedBean().getReassigneeList());

	}
	
	public void selectReassignee() {
		
		PmUser selectedReassignee = PmCommonUtil.retrieveUserByLoginId(
				this.getManagedBean().getSelectedReassignee().get(0));
		
		if (selectedReassignee != null) {
			this.getManagedBean().setResultUser(selectedReassignee);			
			setReassigneeRoleList(selectedReassignee.getUserId());	//[#25018]
			this.getManagedBean().setStatusIds(retrieveAvailableStatusId());
			
			log.info("Selected user: "+this.getManagedBean().getSelectedReassignee().get(0));
		}
		
		if (this.getManagedBean().getSelectedReassignee().size() > 0) {
			this.getManagedBean().setShowReassigneeList(true);
		}
		reloadAllTaskList();
		checkPendingTaskRemoveByDocNo(this.getManagedBean().getSelectedReassignee().get(0)); //#7465
		
		this.getManagedBean().setBpkAdminUser(checkBpkAdminSameUser());
		if(this.getManagedBean().isBpkAdminUser()) {
			//disable check box for user task
			this.getManagedBean().setCheckAllUserTasksEnabled(false);
			//disable check box for group task
			this.getManagedBean().setCheckAllGroupTasksEnabled(false);
			//disable button for group task
			this.getManagedBean().setEnableDeleteGroupTasksButton(false);
			//disable check box for other task
			this.getManagedBean().setCheckAllOthersTasksEnabled(false);
			//disable button for other task
			this.getManagedBean().setEnableDeleteOthersTasksButton(false);
		}
		
		
	}
	
	public void resetReassigneeList() {
		
		if (this.getManagedBean().getReassigneeList().size() > 0) {
			this.getManagedBean().getReassigneeList().clear();
		}
		if (this.getManagedBean().isShowReassigneeList()) {
			this.getManagedBean().setShowReassigneeList(false);
		}
		this.getManagedBean().setSelectedReassignee(new ArrayList<String>());
		this.getManagedBean().getSelectedReassignee().clear();
	}
	
	public boolean verifiedDocNumber(String docNumber) {
		
	   Set<String> moduleList = new HashSet<String>();
	   boolean valid = true;
		  
	   if (Pattern.matches("[a-zA-Z]+",docNumber) || 
       		Pattern.matches("[0-9]+",docNumber)) {
	 		valid = false; 
	   }
	   
	   if (docNumber.length() < 10) {
		   valid = false;
	   } else {	   
		   Pattern pattern = Pattern.compile("[^a-z0-9 ]", 
			  		Pattern.CASE_INSENSITIVE);
		   Matcher matcher = pattern.matcher(docNumber);
		   int numOfInvalidChars = 0;
		   
		   if (!Character.isDigit(docNumber.charAt(docNumber.length()-1))) {
		 		valid = false; 
		   }
		   
		   while (matcher.find()) {
			 	if ( "-".indexOf(docNumber.charAt(matcher.start())) != -1 ) {
			 		 continue;
			 	}
			 	numOfInvalidChars = numOfInvalidChars + 1;
				log.info( "Invalid characters: " + docNumber.charAt(matcher.start()) );
		   }
		   
		   if (numOfInvalidChars > 0) {
			   valid = false;
		   }
		   if (docNumber.matches(".*\\s+.*")) {
	           valid = false;
	        }
	   }
	   
	   String module = null;
	   
    	//Load all recognized module constants
	    for (Field field : ParameterConst.class.getDeclaredFields()) {
	        int fieldModifiers = field.getModifiers();
	        
	        if (Modifier.isStatic(fieldModifiers) && 
	        		Modifier.isFinal(fieldModifiers) ) {
	        	try {
	        		module = field.get(null).toString();
	        		for (char chr : module.toCharArray()) {
	        	        if (!Character.isDigit(chr) && module.length() == 2) {
	        	        	moduleList.add(module);
	        	        }
	        	    }
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
	        }
	    }
	       	
    	List<String> docNoList = new ArrayList<String>();
        String value = "";
        char currKey;
        
        for (int i = 0; i < docNumber.length() + 1; i++) {
        	
            if (i == 0) {
            	currKey = docNumber.charAt(0);
            } else {
            	currKey = docNumber.charAt(--i);
            }
            
            //Splitting digits and letters off the docNumber
            if (currKey >= '0' && currKey <= '9') {
                while (i < docNumber.length() 
                		&& (docNumber.charAt(i) >= '0' 
                			&& docNumber.charAt(i) <= '9')) {
                    value += docNumber.charAt(i++);
                }
                docNoList.add(value);
                value = "";
            } else {
                while (i < docNumber.length() &&
                		!(docNumber.charAt(i) >= '0' 
                			&& docNumber.charAt(i) <= '9')) {
                	value += docNumber.charAt(i++);
                }
                docNoList.add(value);
                value = "";
            }
        }
        
        String docPrefix = docNoList.get(0).toString();
        log.info("docNoList= "+docNoList+ " docPrefix= "+docPrefix);
        
        if (!moduleList.contains(docPrefix)) {
        	if (docPrefix.contains("-")) {
        		docPrefix = docPrefix.replace("-", "");
	        }
        	if (!docPrefix.isEmpty() && docPrefix.matches("[a-zA-Z]+")) {
        		if (docPrefix.length() != 2 && docPrefix.length() < 2 ) {
	        		valid = false;
		    	} else {
		    		moduleList.add(docPrefix);
		    	}
        	}
        	if (!valid) {
        		Iterator<String> moduleIterator = moduleList.iterator(); 
    	       
    	        while (moduleIterator.hasNext()) {
    	        	if (moduleIterator.next().equals(docPrefix)) {
    	        		moduleList.remove(docPrefix);
    	        		break;
    	        	}
    	        }
        	}
        }
        log.info("moduleList size= "+moduleList.size()+" valid= "+valid);
        return valid;
	}
	
	
	/**
	 * User Task List Data Model
	 * 
	 * @author Ken
	 *
	 */
	private class UserTaskListDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;

		public UserTaskListDataModel() {
			super("dtTaskAssignResult",getManagedBean().isTaskLazy());
		}

		private ISearchResultBean<WorklistItemVO> getWorklistItems() {
			log.info("getWorklistItems");
			IBpmSO bpmSO = null;
			BpmRequestDTO reqDTO = null;
			BpmResponseDTO responseDTO = null;
			String loginId = null;
			List<Integer> statusIds = getManagedBean().getStatusIds(); //status_id list from pm_status
			TaskAssignmentSearchBean taskSearchBean = null;
			SearchResultBean<WorklistItemVO> resultBean = null;

			try {
				taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
				loginId = taskSearchBean.getUser() != null ? taskSearchBean
						.getUser().getLoginId() : null;
				// #20117		
				if (loginId == null) {
					if (getManagedBean().getResultUser() != null) {
						loginId = getManagedBean().getResultUser().getLoginId();
					}
				} 
				if (loginId != null && loginId.length() > 0) {
					if (statusIds != null && !statusIds.isEmpty()) {
						bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
								.getServiceObject(IBpmSO.class);
						reqDTO = new BpmRequestDTO();
						Object ctxToken = bpmSO.authenticate(loginId);

						log.info("BPM pageNo******" + getPageNo());
						log.info("BPM pageSize*****" + getPageSize());
						log.info("BPM loginId******" + loginId);
						log.info("BPM statusIds******" + statusIds.size());
				

						reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
						reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
						reqDTO.setStatusList(statusIds);
						reqDTO.setUserId(loginId);
						reqDTO.setCtxToken(ctxToken);
						List<String> docNumberList = new ArrayList<String>();
						if (StringUtils.isNotBlank(getManagedBean()
								.getTaskAssignmentSearchBean().getDocNo())) {
							docNumberList.add(getManagedBean()
									.getTaskAssignmentSearchBean().getDocNo());
							reqDTO.setDocNumberList(docNumberList);
						}

						if (getManagedBean().getTaskAssignmentSearchBean()
								.getDocType() != null
								&& getManagedBean()
										.getTaskAssignmentSearchBean()
										.getDocType().getPmParameter() != null) {
							reqDTO.setDocType(getManagedBean()
									.getTaskAssignmentSearchBean().getDocType()
									.getPmParameter().getParameterCode());
						}
						
						reqDTO.setState(BpmConstants.STATE_ASSIGNED);
						reqDTO.setAssignment(BpmConstants.ASSIGNMENT_MY);
						responseDTO = bpmSO.getReassignedItems(reqDTO);


						// Set resultBean for UI
						resultBean = new SearchResultBean<WorklistItemVO>();
						resultBean.setTotalRecord(0);
						resultBean.setResultList(null);

						if (responseDTO.getWorklistVO() != null) {
							log.info("BPM size******"
									+ responseDTO.getWorklistVO()
											.getWorklistItem().size());
							log.info("BPM total******"
									+ responseDTO.getWorklistVO().getTotal());

							resultBean.setTotalRecord(responseDTO
									.getWorklistVO().getTotal());
							resultBean.setResultList(responseDTO
									.getWorklistVO().getWorklistItem());
							
							//#38680- Start
							removeTaskForChairAndMemberExcludeStatus(resultBean, responseDTO);
							//#38680- End
							
							//[#25018] - start
							if (!responseDTO.getWorklistVO().getWorklistItem().isEmpty() && 
									responseDTO.getWorklistVO().getWorklistItem().size() > 0) {
								getManagedBean().setAllUserTasks(new ArrayList <WorklistItemVO>());
								getManagedBean().setCheckAllUserTasksEnabled(true);
								for (WorklistItemVO worklist : responseDTO.getWorklistVO().getWorklistItem()) {
									getManagedBean().getAllUserTasks().add(worklist);
								}
							} else {
								getManagedBean().setCheckAllUserTasksEnabled(false);
							}
							//[#25018] - end
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				bpmSO = null;
			}
			return resultBean;
		}

		/**
		 * Generate meaningful title
		 * 
		 * @param worklistItem
		 */
		private void markupWorklistItem(List<WorklistItemVO> worklistItem) {
			log.info("markupWorklistItem");
			for (WorklistItemVO worklistItemVO : worklistItem) {
				// fl module Purchase Request Initiation Submit PR
				if (worklistItemVO
						.getProcess()
						.equalsIgnoreCase(
								FLProcessNameConst.PURCHASE_REQ_INITIATION_PROCESS_NAME)
						&& worklistItemVO.getTaskName().equalsIgnoreCase(
								FLProcessNameConst.SUBMIT_PR_HT)) {
					if (!worklistItemVO.getTitle().equalsIgnoreCase(
							SourcingConstants.RN_TITLE)) {
						worklistItemVO.setTitle(ResourceBundleUtils.getMessage(
								"lbl.sc.request.note",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								getFacesScopes().getUserLocale())
								+ " - " + worklistItemVO.getTitle());
					}
				}
				// s1 module
				if (worklistItemVO.getTitle().equalsIgnoreCase(
						SourcingConstants.RN_TITLE)) {
					worklistItemVO.setTitle(ResourceBundleUtils.getMessage(
							"lbl.sc.request.note",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							getFacesScopes().getUserLocale()));
				}
				if (worklistItemVO.getTitle().equalsIgnoreCase(
						SourcingConstants.PI_TITLE)) {
					worklistItemVO.setTitle(ResourceBundleUtils.getMessage(
							"lbl.sc.pr.inquiry",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							getFacesScopes().getUserLocale()));
				}
				if (worklistItemVO.getTitle().equalsIgnoreCase(
						SourcingConstants.PD_TITLE)) {
					worklistItemVO.setTitle(ResourceBundleUtils.getMessage(
							"lbl.sc.purchase.in.default",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							getFacesScopes().getUserLocale()));
				}
			}
		}

		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(
				ISearchBean<WorklistItemVO> searchBean) {
			log.info("pageChangeListener");
			return getWorklistItems();
		}

		private int calculateFinalOffSet(int currentPage) {
			int offSet = (currentPage - 1) * PagingConst.ROWS_PER_PAGE;
			return offSet;
		}
	}
	
	/**
	 * Group Task List Data Model (available to claim in task list screen)
	 * 
	 * @author Ken
	 *
	 */
	private class GroupTaskListDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;

		public GroupTaskListDataModel() {
			super("dtGroupTaskAssignResult",getManagedBean().isTaskLazy());
		}

		private ISearchResultBean<WorklistItemVO> getWorklistItems() {
			log.info("groupTaskListDataModel - getWorklistItems");
			IBpmSO bpmSO = null;
			BpmRequestDTO reqDTO = null;
			BpmResponseDTO responseDTO = null;
			String loginId = null;
			//#7465 start - missing document number to search in bpm
			String documentNumber = null;
			List<Integer> statusIds = getManagedBean().getStatusIds();
			TaskAssignmentSearchBean taskSearchBean = null;
			SearchResultBean<WorklistItemVO> resultBean = null;

			try {
				taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
				loginId = taskSearchBean.getUser() != null ? taskSearchBean
						.getUser().getLoginId() : null;
				//#7465 start - missing document number to search in bpm
				documentNumber = taskSearchBean.getDocNo();
				// #20117		
				if (loginId == null) {
					if (getManagedBean().getResultUser() != null) {
						loginId = getManagedBean().getResultUser().getLoginId();
					}
				} 
				if (loginId != null && loginId.length() > 0) {
					
					if (statusIds != null && !statusIds.isEmpty()) {
						bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
								.getServiceObject(IBpmSO.class);
						reqDTO = new BpmRequestDTO();
						Object ctxToken = bpmSO.authenticate(loginId);

						log.info("BPM pageNo******" + getPageNo());
						log.info("BPM pageSize*****" + getPageSize());
						log.info("BPM loginId******" + loginId);
						log.info("BPM statusIds******" + statusIds.size());

						//#7465 start - missing document number to search in bpm
						reqDTO.setDocNumber(documentNumber);
						reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
						reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
						reqDTO.setStatusList(statusIds);
						reqDTO.setUserId(loginId);
						reqDTO.setCtxToken(ctxToken);
						List<String> docNumberList = new ArrayList<String>();
						if (StringUtils.isNotBlank(getManagedBean()
								.getTaskAssignmentSearchBean().getDocNo())) {
							docNumberList.add(getManagedBean()
									.getTaskAssignmentSearchBean().getDocNo());
							reqDTO.setDocNumberList(docNumberList);
						}

						if (getManagedBean().getTaskAssignmentSearchBean()
								.getDocType() != null
								&& getManagedBean()
										.getTaskAssignmentSearchBean()
										.getDocType().getPmParameter() != null) {
							reqDTO.setDocType(getManagedBean()
									.getTaskAssignmentSearchBean().getDocType()
									.getPmParameter().getParameterCode());
						}
						
						reqDTO.setState(BpmConstants.STATE_ASSIGNED);
						reqDTO.setAssignment(BpmConstants.ASSIGNMENT_GROUP);
						reqDTO.setIsClaimed(false);
						responseDTO = bpmSO.getReassignedItems(reqDTO);

						// Set resultBean for UI
						resultBean = new SearchResultBean<WorklistItemVO>();
						resultBean.setTotalRecord(0);
						resultBean.setResultList(null);

						if (responseDTO.getWorklistVO() != null) {
							log.info("BPM size******"
									+ responseDTO.getWorklistVO()
											.getWorklistItem().size());
							log.info("BPM total******"
									+ responseDTO.getWorklistVO().getTotal());
							/*
							 * if(responseDTO.getWorklistVO().getWorklistItem()!=
							 * null) { // markup data
							 * markupWorklistItem(responseDTO
							 * .getWorklistVO().getWorklistItem()); }
							 */
							resultBean.setTotalRecord(responseDTO
									.getWorklistVO().getTotal());
							resultBean.setResultList(responseDTO
									.getWorklistVO().getWorklistItem());
							
							// #7465 Re-open issues on 24/11/2023
							if (CollectionUtils.isNotEmpty(responseDTO.getWorklistVO().getWorklistItem())) {
								//#7465 start - missing document number to search in bpm
								List<WorklistItemVO> resetWorklist = new ArrayList<WorklistItemVO>();
								getManagedBean().setAllGroupTasks(resetWorklist);
								getManagedBean().setCheckAllGroupTasksEnabled(true);
								getManagedBean().setEnableRemoveAllGroupTasksButton(true); //#7465 reendorse
								for (WorklistItemVO worklist : responseDTO.getWorklistVO().getWorklistItem()) {
									getManagedBean().getAllGroupTasks().add(worklist);
								}
							} else {
								//#7465, #29822  - START - reendorse
								getManagedBean().setCheckAllGroupTasksEnabled(false);
								getManagedBean().setEnableDeleteGroupTasksButton(false); // #7465 Re-open issues on 24/11/2023
								getManagedBean().setEnableRemoveAllGroupTasksButton(false);
								//#7465, #29822  - end - reendorse
							}
						}
						//#7465 START - reendorse
						// #7465 Re-open issues on 24/11/2023
						if(getManagedBean().isShowRemoveProcessMessageGroup()) {
							//disable check box for group task
							getManagedBean().setCheckAllGroupTasksEnabled(false);
							//disable button for group task
							getManagedBean().setEnableRemoveAllGroupTasksButton(false);
						}
						//#7465 end - reendorse
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				bpmSO = null;
			}
			return resultBean;
		}

		/**
		 * Generate meaningful title
		 * 
		 * @param worklistItem
		 */
		private void markupWorklistItem(List<WorklistItemVO> worklistItem) {
			log.info("markupWorklistItem");
			for (WorklistItemVO worklistItemVO : worklistItem) {
				// fl module Purchase Request Initiation Submit PR
				if (worklistItemVO
						.getProcess()
						.equalsIgnoreCase(
								FLProcessNameConst.PURCHASE_REQ_INITIATION_PROCESS_NAME)
						&& worklistItemVO.getTaskName().equalsIgnoreCase(
								FLProcessNameConst.SUBMIT_PR_HT)) {
					if (!worklistItemVO.getTitle().equalsIgnoreCase(
							SourcingConstants.RN_TITLE)) {
						worklistItemVO.setTitle(ResourceBundleUtils.getMessage(
								"lbl.sc.request.note",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								getFacesScopes().getUserLocale())
								+ " - " + worklistItemVO.getTitle());
					}
				}
				// s1 module
				if (worklistItemVO.getTitle().equalsIgnoreCase(
						SourcingConstants.RN_TITLE)) {
					worklistItemVO.setTitle(ResourceBundleUtils.getMessage(
							"lbl.sc.request.note",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							getFacesScopes().getUserLocale()));
				}
				if (worklistItemVO.getTitle().equalsIgnoreCase(
						SourcingConstants.PI_TITLE)) {
					worklistItemVO.setTitle(ResourceBundleUtils.getMessage(
							"lbl.sc.pr.inquiry",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							getFacesScopes().getUserLocale()));
				}
				if (worklistItemVO.getTitle().equalsIgnoreCase(
						SourcingConstants.PD_TITLE)) {
					worklistItemVO.setTitle(ResourceBundleUtils.getMessage(
							"lbl.sc.purchase.in.default",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							getFacesScopes().getUserLocale()));
				}
			}
		}

		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(
				ISearchBean<WorklistItemVO> searchBean) {
			log.info("pageChangeListener");
			return getWorklistItems();
		}

		private int calculateFinalOffSet(int currentPage) {
			int offSet = (currentPage - 1) * PagingConst.ROWS_PER_PAGE;
			return offSet;
		}
	}
	
	/**
	 * Others Task List Data Model (group task has been claimed by other user)
	 * 
	 * @author Ken
	 *
	 */
	private class OthersTaskListDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;

		public OthersTaskListDataModel() {
			super("dtOthersTaskAssignResult",getManagedBean().isTaskLazy());
		}

		private ISearchResultBean<WorklistItemVO> getWorklistItems() {
			log.info("othersTaskListDataModel - getWorklistItems");
			IBpmSO bpmSO = null;
			BpmRequestDTO reqDTO = null;
			BpmResponseDTO responseDTO = null;
			String loginId = null;
			//#7465 start - missing document number to search in bpm
			String documentNumber = null;
			List<Integer> statusIds = getManagedBean().getStatusIds();
			TaskAssignmentSearchBean taskSearchBean = null;
			SearchResultBean<WorklistItemVO> resultBean = null;

			try {
				taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
				loginId = taskSearchBean.getUser() != null ? taskSearchBean
						.getUser().getLoginId() : null;
				//#7465 start - missing document number to search in bpm
				documentNumber = taskSearchBean.getDocNo();
				// #20117		
				if (loginId == null) {
					if (getManagedBean().getResultUser() != null) {
						loginId = getManagedBean().getResultUser().getLoginId();
					}
				}
				if (loginId != null && loginId.length() > 0) {
					if (statusIds != null && !statusIds.isEmpty()) {
						bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
								.getServiceObject(IBpmSO.class);
						reqDTO = new BpmRequestDTO();
						Object ctxToken = bpmSO.authenticate(loginId);

						log.info("BPM pageNo******" + getPageNo());
						log.info("BPM pageSize*****" + getPageSize());
						log.info("BPM loginId******" + loginId);
						log.info("BPM statusIds******" + statusIds.size());

						//#7465 start - missing document number to search in bpm
						reqDTO.setDocNumber(documentNumber);
						reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
						reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
						reqDTO.setStatusList(statusIds);
						reqDTO.setUserId(loginId);
						reqDTO.setCtxToken(ctxToken);
						List<String> docNumberList = new ArrayList<String>();
						if (StringUtils.isNotBlank(getManagedBean()
								.getTaskAssignmentSearchBean().getDocNo())) {
							docNumberList.add(getManagedBean()
									.getTaskAssignmentSearchBean().getDocNo());
							reqDTO.setDocNumberList(docNumberList);
						}

						if (getManagedBean().getTaskAssignmentSearchBean()
								.getDocType() != null
								&& getManagedBean()
										.getTaskAssignmentSearchBean()
										.getDocType().getPmParameter() != null) {
							reqDTO.setDocType(getManagedBean()
									.getTaskAssignmentSearchBean().getDocType()
									.getPmParameter().getParameterCode());
						}
						
						reqDTO.setState(BpmConstants.STATE_ASSIGNED);
						reqDTO.setAssignment(BpmConstants.ASSIGNMENT_ALL);
						responseDTO = bpmSO.getGroupByOthersItems(reqDTO);
						
						// Set resultBean for UI
						resultBean = new SearchResultBean<WorklistItemVO>();
						resultBean.setTotalRecord(0);
						resultBean.setResultList(null);

						if (responseDTO.getWorklistVO() != null) {
							log.info("BPM size******"
									+ responseDTO.getWorklistVO()
											.getWorklistItem().size());
							log.info("BPM total******"
									+ responseDTO.getWorklistVO().getTotal());
							/*
							 * if(responseDTO.getWorklistVO().getWorklistItem()!=
							 * null) { // markup data
							 * markupWorklistItem(responseDTO
							 * .getWorklistVO().getWorklistItem()); }
							 */
							resultBean.setTotalRecord(responseDTO
									.getWorklistVO().getTotal());
							resultBean.setResultList(responseDTO
									.getWorklistVO().getWorklistItem());
							
							// #7465 Re-open issues on 24/11/2023
							if (CollectionUtils.isNotEmpty(responseDTO.getWorklistVO().getWorklistItem())) {
								//#7465 start - missing document number to search in bpm
								List<WorklistItemVO> resetWorklist = new ArrayList<WorklistItemVO>();
								getManagedBean().setAllOthersTasks(resetWorklist); //7465 - wrong reset value for others
								getManagedBean().setCheckAllOthersTasksEnabled(true);
								getManagedBean().setEnableRemoveAllOthersTasksButton(true); //#7465 reendorse
								for (WorklistItemVO worklist : responseDTO.getWorklistVO().getWorklistItem()) {
									getManagedBean().getAllOthersTasks().add(worklist);
								}
							} else {
								//#7465, #29822  - START - reendorse
								getManagedBean().setCheckAllOthersTasksEnabled(false); 
								getManagedBean().setEnableDeleteOthersTasksButton(false); // #7465 Re-open issues on 24/11/2023
								getManagedBean().setEnableRemoveAllOthersTasksButton(false);
//								//#7465, #29822  - end - reendorse
							}
						}
						//#7465 START - reendorse
						// #7465 Re-open issues on 24/11/2023
						if(getManagedBean().isShowRemoveProcessMessageOther()) {
							//disable check box for group task
							getManagedBean().setCheckAllOthersTasksEnabled(false);
							//disable button for group task
							getManagedBean().setEnableRemoveAllOthersTasksButton(false);
						}
						//#7465 end - reendorse
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				bpmSO = null;
			}
			return resultBean;
		}

		/**
		 * Generate meaningful title
		 * 
		 * @param worklistItem
		 */
		private void markupWorklistItem(List<WorklistItemVO> worklistItem) {
			log.info("markupWorklistItem");
			for (WorklistItemVO worklistItemVO : worklistItem) {
				// fl module Purchase Request Initiation Submit PR
				if (worklistItemVO
						.getProcess()
						.equalsIgnoreCase(
								FLProcessNameConst.PURCHASE_REQ_INITIATION_PROCESS_NAME)
						&& worklistItemVO.getTaskName().equalsIgnoreCase(
								FLProcessNameConst.SUBMIT_PR_HT)) {
					if (!worklistItemVO.getTitle().equalsIgnoreCase(
							SourcingConstants.RN_TITLE)) {
						worklistItemVO.setTitle(ResourceBundleUtils.getMessage(
								"lbl.sc.request.note",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								getFacesScopes().getUserLocale())
								+ " - " + worklistItemVO.getTitle());
					}
				}
				// s1 module
				if (worklistItemVO.getTitle().equalsIgnoreCase(
						SourcingConstants.RN_TITLE)) {
					worklistItemVO.setTitle(ResourceBundleUtils.getMessage(
							"lbl.sc.request.note",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							getFacesScopes().getUserLocale()));
				}
				if (worklistItemVO.getTitle().equalsIgnoreCase(
						SourcingConstants.PI_TITLE)) {
					worklistItemVO.setTitle(ResourceBundleUtils.getMessage(
							"lbl.sc.pr.inquiry",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							getFacesScopes().getUserLocale()));
				}
				if (worklistItemVO.getTitle().equalsIgnoreCase(
						SourcingConstants.PD_TITLE)) {
					worklistItemVO.setTitle(ResourceBundleUtils.getMessage(
							"lbl.sc.purchase.in.default",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							getFacesScopes().getUserLocale()));
				}
			}
		}

		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(
				ISearchBean<WorklistItemVO> searchBean) {
			log.info("pageChangeListener");
			return getWorklistItems();
		}

		private int calculateFinalOffSet(int currentPage) {
			int offSet = (currentPage - 1) * PagingConst.ROWS_PER_PAGE;
			return offSet;
		}
	}
	
	/**
	 * retrieve status id list for BPM
	 * 
	 * @return List<Integer>
	 */
	private List<Integer> retrieveAvailableStatusId() {
		log.info("retrieveAvailableStatusId");
		List<Integer> statusIds = new ArrayList<Integer>();
		try {
			StatusReqDTO reqDTO = new StatusReqDTO();
			if (getManagedBean().getTaskAssignmentSearchBean()
					.getSelectedRoles() != null
					&& !getManagedBean().getTaskAssignmentSearchBean()
							.getSelectedRoles().isEmpty()) {
				reqDTO = new StatusReqDTO();
				reqDTO.setRoleCodeList(getManagedBean()
						.getTaskAssignmentSearchBean().getSelectedRoles());
			} else if (getManagedBean().getRoleCodes() != null
					&& !getManagedBean().getRoleCodes().isEmpty()) {
				reqDTO = new StatusReqDTO();
				reqDTO.setRoleCodeList(getManagedBean().getRoleCodes());
			}

			if (reqDTO.getRoleCodeList() != null
					&& !reqDTO.getRoleCodeList().isEmpty()) {
				reqDTO.setReassignable(true);
				IStatusSO statusSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IStatusSO.class);
				StatusResDTO resDTO = statusSO
						.retrieveStatusRoleListByRoleCode(reqDTO);
				if (resDTO.isSuccessful()) {
					for (int i = 0; i < resDTO.getStatusRoleList().size(); i++) {
						statusIds.add(resDTO.getStatusRoleList().get(i)
								.getPmStatus().getStatusId().intValue());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770094,
					new String[] { e.getMessage() });
		}
		return statusIds;
	}

	/**
	 * Get meaningful application status message
	 * 
	 * @param docInt
	 * @return String
	 */
	public String applicationStatus(Integer docInt) {
		log.info("applicationStatus");
		log.info("docInt:" + docInt);
		if (docInt != null) {
			PmStatusDesc desc = StatusHelper.getInstance().getStatusDescById(docInt.longValue());
			return desc.getStatusName();
//			return desc.getStatusName() +" & ReassignType : " + desc.getPmStatus().getReasssignType();
			//return getStatusDescById(docInt.longValue());
		} else {
			return "";
		}
	}

	/**
	 * Redirect to Task Re-assignment Details
	 * 
	 * @param item
	 *            WorklistItemVO
	 * @return xhtml
	 */
	public void redirectToTaskReassign(WorklistItemVO item) {
		log.info("redirectToTaskReassign - Start");
		try {
			this.getManagedBean().setSelectedUser(null);
			this.getManagedBean().setSelectedTask(item);
			resetAvailableUser();
			this.getManagedBean().setTaskReassignmentVO(null);
			this.getManagedBean().setCommitteeUpdate(false);
			this.getManagedBean().setRemarks(null);
			if(ProfileManagementConst.TASK_REASSIGN_REMARKS_CT.contains(item.getDocStatus().longValue())
					|| ProfileManagementConst.TASK_REASSIGN_REMARKS_S2.contains(item.getDocStatus().longValue())){
				getManagedBean().setRemarkRequired(true);
			}else{
				getManagedBean().setRemarkRequired(false);
			}
			
			this.getManagedBean().setBulkReassign(false);	//[#25018]
			this.getManagedBean().setSingleReassign(true);	//[#25018]
//			this.getManagedBean().setAvailableUsers(
//					generateAvailableUsers(item));
		} catch (Exception e) {
			throw e;
		}
		log.info("redirectToTaskReassign - End");
		RequestContext.getCurrentInstance()
				.execute("dlTaskReassignment.show()");
	}
	
	//[#25018]
	/**
	 * Redirect to Bulk Task Re-assignment Details
	 * 
	 * @return xhtml
	 */	
	public void redirectToBulkTasksReassign() {
		log.info("redirectToBulkTasksReassign - Start");		
		
		try {
			this.getManagedBean().setSelectedUser(null);
			this.getManagedBean().setSelectedTask(null);
			resetAvailableUser();
			this.getManagedBean().setTaskReassignmentVO(null);
			this.getManagedBean().setCommitteeUpdate(false);
			this.getManagedBean().setRemarks(null);		
			this.getManagedBean().setBulkReassign(true);
			this.getManagedBean().setSingleReassign(false);
			
			if(this.getManagedBean().getSelectedTasksToBulkReassign() != null 
					&& this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
				WorklistItemVO task = this.getManagedBean().getSelectedTasksToBulkReassign().get(0);
				//set value for text input document type
				this.getManagedBean().setDocType(task.getDocType());
			}
			
			//set value for text area document number
			this.getManagedBean().setSelectedTasksTA(getSelectedTasks());
			
			if(ProfileManagementConst.TASK_REASSIGN_REMARKS_CT.contains(this.getManagedBean().getDocStatus())
					|| ProfileManagementConst.TASK_REASSIGN_REMARKS_S2.contains(this.getManagedBean().getDocStatus())){
				getManagedBean().setRemarkRequired(true);
			}else{
				getManagedBean().setRemarkRequired(false);
			}			
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		}
		log.info("redirectToBulkTasksReassign - End");
		
		RequestContext.getCurrentInstance()
				.execute("dlTaskReassignment.show()");		
	}

	/**
	 * @return the orgTypeMapper
	 */
	public static Map<Long, PmParameter> getOrgTypeMapper() {
		return orgTypeMapper;
	}

	/**
	 * @param orgTypeMapper
	 *            the orgTypeMapper to set
	 */
	public static void setOrgTypeMapper(Map<Long, PmParameter> orgTypeMapper) {
		TaskReassignmentBackingBean.orgTypeMapper = orgTypeMapper;
	}

	/**
	 * @param orgTypeSearchBean
	 *            the orgTypeSearchBean to set
	 */
	public void setOrgTypeSearchBean(
			ISearchBean<PmParameterDesc> orgTypeSearchBean) {
		this.orgTypeSearchBean = orgTypeSearchBean;
	}

	/**
	 * getOrgTypeSearchBean
	 * 
	 * @return ISearchBean<PmParameterDesc>
	 */
	public ISearchBean<PmParameterDesc> getOrgTypeSearchBean() {
		log.info("getOrgTypeSearchBean");
		if (this.orgTypeSearchBean == null) {
			setOrgTypeSearchBean(super.createSearchBean(PmParameterDesc.class,
					"somOrgType"));

		}

		return this.orgTypeSearchBean;
	}

	/**
	 * To Retrieve Sourcing Module Task Reassignment Details
	 * 
	 * @param task
	 * @return PmTaskReassignmentVO
	 */
	private PmTaskReassignmentVO retrieveSourcingTaskReassignmentDetail(
			WorklistItemVO task) {
		log.info("retrieveSourcingTaskReassignmentDetail");
		ISourcingIntegrationSO scIntegrationSO = null;
		PmTaskReassignmentVO vo = new PmTaskReassignmentVO();
		try {
			SourcingIntegrationReqDTO reqDTO = new SourcingIntegrationReqDTO();
			scIntegrationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION,
					ISourcingIntegrationSO.class);
			reqDTO.setDocType(task.getDocType());
			reqDTO.setDocNo(task.getDocNumber());
			reqDTO.setStatusId(task.getDocStatus().longValue());
			reqDTO.setTaskName(task.getTaskName());
			SourcingIntegrationResDTO resDTO = scIntegrationSO.retrieveTaskReassignmentDetail(reqDTO);
			if(resDTO != null){
				vo.setGroupDocId(resDTO.getGroupDocId());
				vo.setGroupDocType(resDTO.getGroupDocType());
				vo.setDocId(resDTO.getDocId());
				vo.setDocType(resDTO.getDocType());
				vo.setOrgProfileId(resDTO.getOrgProfileId());
				vo.setRoleList(StringUtils.isNotBlank(resDTO.getRoleCode()) ? new ArrayList<String>(Arrays.asList(resDTO.getRoleCode())) : new ArrayList<String>());
				vo.setUserGroupId(resDTO.getUserGroupId());
				//Redmine Bug #31732 - START
				if(!resDTO.getRoleCodeList().isEmpty())
					vo.setRoleList(resDTO.getRoleCodeList());
				//Redmine Bug #31732 - END
				
			}
		}catch(Exception e){
			throw new EpException("unable to get task reassignment details from Sourcing Module.");
		} finally {
			scIntegrationSO = null;
		}
		return vo;
	}

	/**
	 * To Retrieve Procurement Plan Task Reassignment Details
	 * 
	 * @param task
	 * @return PmTaskReassignmentVO
	 */
	private PmTaskReassignmentVO retrievePpTaskReassignmentDetail(
			WorklistItemVO task) {
		log.info("retrievePpTaskReassignmentDetail");
		IProcurementPlanSO procurementSO= null;
		PmTaskReassignmentVO vo = new PmTaskReassignmentVO();
		try {
			ProcurementPlanReqDTO reqDTO  = new ProcurementPlanReqDTO();
			procurementSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROCUREMENT_PLAN,
					IProcurementPlanSO.class);
			String docType = ParameterConst.DOCTYPE_PLAN_EXPENDITURE.equals(task.getDocType()) ? ParameterConst.PLAN_TYPE_EXPENDITURE: ParameterConst.PLAN_TYPE_PROCUREMENT;
			reqDTO.setDocType(docType);
			reqDTO.setDocNo(task.getDocNumber());
			reqDTO.setStatusId(task.getDocStatus().longValue());
			ProcurementPlanResDTO resDTO = procurementSO.retrieveTaskReassignmentDetail(reqDTO);
			if (resDTO != null && resDTO.getOrgProfileId() != null) {
				vo.setOrgProfileId(resDTO.getOrgProfileId());
				vo.setDocId(resDTO.getDocId());
				if(StringUtils.isNotBlank(resDTO.getRoleCode())){
				   vo.setRoleList(new ArrayList<String>(Arrays.asList(resDTO.getRoleCode())));
				}
			}
		}catch(Exception e){
			throw new EpException("unable to get task reassignment details from Procurement Plan Module.");
		} finally {
			procurementSO = null;
		}
		return vo;
	}
	
	/**
	 * To Retrieve Contract Management Task Reassignment Details
	 * 
	 * @param task
	 * @return PmTaskReassignmentVO
	 */
	private PmTaskReassignmentVO retrieveCtTaskReassignmentDetails(WorklistItemVO task,ISearchBean<PmUser> userSearchBean) {
		log.info("retrieveCtTaskReassignmentDetails");
		PmTaskReassignmentVO vo = new PmTaskReassignmentVO();
		IContractIntegrationSO ctIntegrationSO = null;
		try {
			ContractIntegrationReqDTO reqDTO = new ContractIntegrationReqDTO();
			ctIntegrationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT,
					IContractIntegrationSO.class);
			if(getManagedBean()
					.getTaskAssignmentSearchBean().getUser() != null){
				reqDTO.setUserId(getManagedBean()
						.getTaskAssignmentSearchBean().getUser().getUserId());
			}
			reqDTO.setDocNo(task.getDocNumber());
			reqDTO.setDocType(task.getDocType());
			reqDTO.setStatusId(task.getDocStatus().longValue());
			reqDTO.setUserSearchBean(userSearchBean);
			ContractIntegrationResDTO resDTO = ctIntegrationSO
					.retrieveTaskAssignmentRole(reqDTO);
			if (resDTO.isSuccessful()) {
				vo.setRoleList(resDTO.getRoleCodeList());
				vo.setGroupDocId(resDTO.getGroupDocId());
				vo.setGroupDocType(resDTO.getGroupDocType());
				vo.setDocId(resDTO.getDocId());
				vo.setDocNo(resDTO.getDocNo());
				vo.setDocType(resDTO.getDocType());
				//For Tracking Diary 
				if(resDTO.getCircularList() != null && !resDTO.getCircularList().isEmpty()){
					vo.setTrackingDiaryList(resDTO.getCircularList());
				}				
//				if(resDTO.getRoleCodeList() != null){
//					for(String roleCode : vo.getRoleList()){
//						if(roleCode.endsWith("_MEMBER") || roleCode.endsWith("_CHAIRPERSON")){
//							vo.setRoleUpdate(true);
//							break;
//						}
//					}	
//				}
				
			}
		}catch(Exception e){
			throw new EpException("unable to get task reassignment details from Contract Management Module.");
		} finally {
			ctIntegrationSO = null;
		}
		return vo;
	}

	/**
	 * To Retrieve Order Fulfilment Task Reassignment Details
	 * 
	 * @param task
	 * @return PmTaskReassignmentVO
	 */
	private PmTaskReassignmentVO retrieveFLTaskReassignmentDetail(
			WorklistItemVO task) {
		log.info("retrieveFLTaskReassignmentDetail");
		IFulfilmentSO fulfilmentSO = null;
		PmTaskReassignmentVO vo = new PmTaskReassignmentVO();
		try {
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			fulfilmentSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.ORDER_FULFILLMENT,
					IFulfilmentSO.class);
			reqDTO.setDocType(task.getDocType());
			reqDTO.setFlDocNo(task.getDocNumber());
			reqDTO.setStatusId(task.getDocStatus().longValue());
			reqDTO.setTaskName(task.getTaskName());
			reqDTO.setTaskId(task.getTaskId());
			if(getManagedBean().getTaskAssignmentSearchBean() != null &&
					getManagedBean().getTaskAssignmentSearchBean().getUser() != null &&
					getManagedBean().getTaskAssignmentSearchBean().getUser().getLoginId() != null){
				reqDTO.setUserLoginId(getManagedBean().getTaskAssignmentSearchBean().getUser().getLoginId());
			}else{
				//28843@azza09092020 - Start
				reqDTO.setUserLoginId(this.getManagedBean().getSelectedReassignee().get(0));
				//28843@azza09092020 - End
			}

			FulfilmentResDTO resDTO = fulfilmentSO.retrieveTaskReassignmentDetail(reqDTO);
			if(resDTO != null){
				vo.setOrderAmount(resDTO.getOrderAmount());
				vo.setApprovalLimit(resDTO.getOrderAmount());
				vo.setOrgProfileId(resDTO.getOrgProfileId());
				vo.setDocId(resDTO.getDocId());
				vo.setDocNo(resDTO.getDocNo());
				vo.setDocType(resDTO.getDocType());
				vo.setUserGroupId(resDTO.getUserGroupId());
				vo.setAddressId(resDTO.getAddressId());
				vo.setSupplier(resDTO.isSupplier());
				vo.setGroupDocId(resDTO.getGroupDocId());
				vo.setGroupDocType(resDTO.getGroupDocType());
			}
		}catch(Exception e){
			throw new EpException("unable to get task reassignment details from Fulfilment Module.");
		} finally {
			fulfilmentSO = null;
		}
		return vo;
	}
	
	/**
	 * To Retrieve Catalogue Management Task Reassignment Details
	 * 
	 * @param task
	 * @return PmTaskReassignmentVO
	 */
	private PmTaskReassignmentVO retrieveCMTaskReassignmentDetail(
			WorklistItemVO task) {
		log.info("retrieveCMTaskReassignmentDetail");
		ICodificationSO codificationSO = null;
		PmTaskReassignmentVO vo = new PmTaskReassignmentVO();
		try {
			CodificationReqDTO reqDTO = new CodificationReqDTO();
			codificationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CATALOGUE_MANAGEMENT,
					ICodificationSO.class);
			reqDTO.setDocNo(task.getDocNumber());
			reqDTO.setDocType(task.getDocType());
			
			CodificationResDTO resDTO = codificationSO.findDocIdByDocNoAndDocType(reqDTO);
			if(resDTO != null){
				vo.setDocId(resDTO.getDocId());
			}
		}catch(Exception e){
			throw new EpException("unable to get task reassignment details from Catalogue Management Module.");
		} finally {
			codificationSO = null;
		}
		return vo;
	}
	
	/**
	 * To Retrieve Profile Management Task Reassignment Details
	 * 
	 * @param task
	 * @return PmTaskReassignmentVO
	 */
	private PmTaskReassignmentVO retrievePMTaskReassignmentDetail(
			WorklistItemVO task) {
		log.info("retrievePMTaskReassignmentDetail");
		PmTaskReassignmentVO vo = new PmTaskReassignmentVO();
		//it is for access application only
		if(task.getDocType() != null && 
				(ParameterConst.DOCTYPE_COMPANY_PROFILE.equals(task.getDocType()) || ParameterConst.DOCTYPE_MARKET_RESEARCH.equals(task.getDocType()))){
			IAccessApplSO accessApplSO = null;
			try{
				accessApplSO = (IAccessApplSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IAccessApplSO.class);
				
				AccessApplReqDTO reqDTO = new AccessApplReqDTO();
				reqDTO.setDocNo(task.getDocNumber());
				AccessApplResDTO resDTO = accessApplSO.retrieveAccessApplByApplNo(reqDTO);
				if(resDTO.isSuccessful() && resDTO.getAccessAppl() != null){
					vo.setDocId(resDTO.getAccessAppl().getAccessApplId());
					
				}
			}catch(Exception e){
				throw new EpException(e.getMessage());
			}finally{
				accessApplSO = null;
			}
		}
		
		return vo;
	}
	
	private PmTaskReassignmentVO retrieveSMTaskReassignmentDetail(
			WorklistItemVO task) {
		log.info("retrieveSMTaskReassignmentDetail");
		PmTaskReassignmentVO vo = new PmTaskReassignmentVO();
		if(ProfileManagementConst.TASK_REASSIGN_SM_PO.contains(task.getDocStatus().longValue())){
			vo.setRoleList(new ArrayList<String>(Arrays.asList(RoleCode.UPS_PO.value())));
		}
		
		return vo;
	}


	/**
	 * retrieve available roles by status id
	 * 
	 * @param statusId
	 * @return List<String>
	 */
	private List<String> retrieveAvailableRoles(Long statusId,Integer reassignType,Integer subReassignType) {
		log.info("retrieveAvailableRoles");
		IStatusSO statusSO = null;
		List<String> statusRoles = new ArrayList<String>();
		try {
			StatusReqDTO reqDTO = new StatusReqDTO();
			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			reqDTO.setStatusId(statusId);
			reqDTO.setReassignType(reassignType);
			reqDTO.setSubReassignType(subReassignType);
			StatusResDTO resDTO = statusSO.retrieveStatusRoleByStatusId(reqDTO);
			if (resDTO.isSuccessful()) {
				for (PmStatusRole statusRole : resDTO.getStatusRoleList()) {
					statusRoles.add(statusRole.getPmRole().getRoleCode());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770093,
					new String[] { e.getMessage() });
		} finally {
			statusSO = null;
		}
		statusRoles.add(RoleCode.BPK_ADMIN.value()); // # 10927
		return statusRoles;
	}

	/**
	 * Reassign task to selected user
	 */
	public void confirmReassignTask() {
		log.info("confirmReassignTask");

		log.info("[#25018] isBulkReassign? " + getManagedBean().isBulkReassign());
		
		List<WorklistItemVO> tasks = null;	//[#25018]		
		PmUser pmUser = this.getManagedBean().getSelectedUser();
		//WorklistItemVO task = this.getManagedBean().getSelectedTask();
		ITaskAssignmentSO taskAssignmentSO = null;
		//PmTaskReassignmentVO vo = getManagedBean().getTaskReassignmentVO();
		PmTaskReassignmentVO vo = null;		//[#25018]
		TaskAssignmentReqDTO reqDTO = null; //[#25018]
		TaskAssignmentResDTO resDTO = null; //[#25018]
		StringBuilder stb = null;			//[#25018]		
		
		//[#25018]
		if(getManagedBean().isSingleReassign()){
			tasks = new ArrayList<WorklistItemVO>();
			tasks.add(getManagedBean().getSelectedTask());
		}
		//[#25018]
		else if(getManagedBean().isBulkReassign()) {
			tasks = new ArrayList<WorklistItemVO>();
			tasks.addAll(getManagedBean().getSelectedTasksToBulkReassign());					
		}
		
		//if (task == null) {
		if (tasks == null) { //[#25018]
			throw new EpException(MessageConst.ERROR_770097);
		}
		
		if(getManagedBean().isRemarkRequired() && StringUtils.isBlank(getManagedBean().getRemarks())){
			throw new EpException(MessageConst.ERROR_100022,new String[]{
					ResourceBundleUtils.getMessage(
					"lbl.txt.remarks",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale())});
		}
		List<String> assigneeUsers = new ArrayList<String>();
//		if (!assigneeUsers.isEmpty()) {
////			if (pmUser != null && assigneeUsers.contains(pmUser.getLoginId())) {
////				throw new EpException("the task already exist under the user:"
////						+ pmUser.getUserName());
////			} else {
//				// remove the current user.
//				if (assigneeUsers.contains(getManagedBean()
//						.getTaskAssignmentSearchBean().getUser().getLoginId())) {
//					assigneeUsers.remove(getManagedBean()
//							.getTaskAssignmentSearchBean().getUser()
//							.getLoginId());
////				}
//			}
//		}

		if (pmUser == null) {
			throw new EpException(MessageConst.ERROR_770096);
		}
		
		//[#22117] - start
		//validate task reassignment to BPK_ADMIN
		if (pmUser != null) {
			log.info("[#22117] - Validate user if has role BPK_ADMIN");
			List<PmUserOrg> pmUserOrgs = PmCommonUtil.retrieveActiveOrg(pmUser.getUserId());
			if (pmUserOrgs != null && pmUserOrgs.size() > 0) {
				PmOrgProfile orgProfile = pmUserOrgs.get(0).getPmOrgProfile();
				log.info("[#22117] - Selected user org type is: " + orgProfile.getOrgTypeCode().getParameterCode());
				if(orgProfile.getOrgTypeCode().getParameterCode().equals(ParameterConst.ORG_TYPE_BPK)) {
					log.info("[#22117] - Selected user org type is: BPK");
					for (PmUserRole userRole : pmUserOrgs.get(0).getPmUserRoles()) {
						if (DbValueConst.RECORD_STATUS_ACTIVE == userRole.getRecordStatusFlg()
								&& userRole.getPmRole().getRoleCode().equals(RoleCode.BPK_ADMIN.value())) {
							log.info("[#22117] - Selected user has role: BPK_ADMIN");
							//to restrict only role EP_ADMIN or EP_ADMIN_2 can perform reassign task
							//to BPK_ADMIN
							if (!(getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) 
									|| getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_2.value()))) {
								String errMsg = pmUser.getUserName();
								throw new EpException(MessageConst.ERROR_770209,  new String[] { errMsg });
							} else{							
								log.info("[#22117] - Proceed reassign task...");
							}
						}
					}
				}
			}
			//throw new EpException(MessageConst.ERROR_770096);
		}
		
		//validate task reassignment from BPK_ADMIN
		String orgType = this.getManagedBean().getTaskAssignmentSearchBean().getOrgType().getParameterCode();
		log.info("[#22117] Org type: " + orgType);
		if (pmUser != null && orgType.equals(ParameterConst.ORG_TYPE_BPK)) {
			log.info("[#22117] - Validate user if has role BPK_ADMIN");			
			List<String> userRoles = this.getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles();
			if(userRoles != null && userRoles.contains(RoleCode.BPK_ADMIN.value())) {
				log.info("[#22117] - Selected user has role: BPK_ADMIN");
				//to restrict only role EP_ADMIN or EP_ADMIN_2 can perform reassign task
				//from BPK_ADMIN
				if (!(getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) 
						|| getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_2.value()))) {
					String errMsg = this.getManagedBean().getTaskAssignmentSearchBean().getUser().getUserName();
					throw new EpException(MessageConst.ERROR_770229,  new String[] { errMsg });
				} else{							
					log.info("[#22117] - Proceed reassign task...");
				}
			}			
		} 
		//[#22117] - end
		
		// replace with the new assignee.
		assigneeUsers.add(pmUser.getLoginId());
		

		try {			
			for(WorklistItemVO task : tasks) { //[#25018]
				//TaskAssignmentReqDTO reqDTO = new TaskAssignmentReqDTO();
				reqDTO = new TaskAssignmentReqDTO();
				
				PmUser fromUser = this.getManagedBean().getSelectedReassignee() != null && !this.getManagedBean().getSelectedReassignee().isEmpty() ?
						PmCommonUtil.retrieveUserByLoginId(
								this.getManagedBean().getSelectedReassignee().get(0)) :
							getManagedBean().getTaskAssignmentSearchBean().getUser();
				log.info("[#25018] WorklistItemVO DocNo: " + task.getDocNumber());				
				//[#25018] -start
				if(getManagedBean().isSingleReassign()){					
					vo = getManagedBean().getTaskReassignmentVO();
				}				
				else if(getManagedBean().isBulkReassign()) {
					for(PmTaskReassignmentVO pmtaskvo : getManagedBean().getTasksReassignmentVO()) {
						log.info("[#25018] PmTaskReassignmentVO DocNo: " + pmtaskvo.getDocNo());
						if(task.getDocNumber().equals(pmtaskvo.getDocNo())) {
							vo = pmtaskvo;
							break;
						}						
					}
				}
				//[#25018] -end
				
				reqDTO.setTaskId(task.getTaskId());
				reqDTO.setUser(pmUser);
				reqDTO.setUserIdList(assigneeUsers);
				reqDTO.setDocNumber(StringUtils.isNotBlank(vo.getDocNo())? vo.getDocNo() : task.getDocNumber());
				reqDTO.setDocId(vo.getDocId() != null ? vo.getDocId().intValue() : task.getDocId());		
				reqDTO.setDocType(vo.getDocType() != null ? vo.getDocType() : task.getDocType());
				reqDTO.setTaskDocType(task.getDocType());
				reqDTO.setGroupDocId(vo.getGroupDocId() != null ? vo.getGroupDocId() : null);
				reqDTO.setGroupDocType(StringUtils.isNotBlank(vo.getGroupDocType()) ? vo.getGroupDocType() : null);
				reqDTO.setDocStatus(task.getDocStatus());
				reqDTO.setRoleCode(PmCommonUtil.getUserRoleValue());
				reqDTO.setActionedByUser(getUserIdSessionFromLoginId()); //#31849 add log key
				reqDTO.setFromUser(fromUser);
				//log.info("[#25018] isCommitteeUpdate():" + getManagedBean().isCommitteeUpdate());
				reqDTO.setCommitteeUpdate(getManagedBean().isCommitteeUpdate());	
				reqDTO.setUserLocale(getFacesScopes().getUserLocale());
				reqDTO.setRemark(getManagedBean().getRemarks());
				reqDTO.setTaskRoleCode(vo.getRoleList() !=null && !vo.getRoleList().isEmpty() ? vo.getRoleList().get(0) : null);
				//log.info("[#25018] TaskRoleCode: " + reqDTO.getTaskRoleCode());
				//For Tracking Diary List (CT)
				if(vo.getTrackingDiaryList() != null && !vo.getTrackingDiaryList().isEmpty()){
					reqDTO.setTrackingDiaryList(vo.getTrackingDiaryList());
				}
			
				//For Role Update (CT)
				if(vo.getStatus() != null 
						&& ModuleCodeConst.CONTRACT_MANAGEMENT.equalsIgnoreCase(vo.getStatus().getModuleCode()) 
						&& vo.getRoleList() != null){
					for(String roleCode : vo.getRoleList()){
						if(roleCode.endsWith("_MEMBER") || roleCode.endsWith("_CHAIRPERSON")){
							reqDTO.setRoleUpdate(true);
							break;
						}
					}	
				}
	
				taskAssignmentSO = (ITaskAssignmentSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								ITaskAssignmentSO.class);
				log.info("[#25018] Saving reassign task: " + task.getDocNumber());
				//TaskAssignmentResDTO resDTO = taskAssignmentSO.reassignTask(reqDTO);
				resDTO = taskAssignmentSO.reassignTask(reqDTO);
				
	
				// isSuccess = reassignTask(pmUser, task);
//				if (resDTO != null && resDTO.isSuccessful()) {
//					RequestContext.getCurrentInstance()
//						.execute("dlTaskReassignment.hide()");
//					reloadAllTaskList();
//					PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), 
//							FacesMessage.SEVERITY_INFO, MessageConst.INFO_770104, 
//							new String[] { task.getDocNumber() });				
//				}
				
				if (resDTO != null && resDTO.isSuccessful()) {
					if(stb != null) {
						stb.append(", ");
						stb.append(task.getDocNumber());
					} else {
						stb = new StringBuilder(task.getDocNumber());
					}
				}
			}
			log.info("[#25018] Successful reassign task: " + stb.toString());			
			
			if (resDTO != null && resDTO.isSuccessful()) {
				RequestContext.getCurrentInstance()
					.execute("dlTaskReassignment.hide()");
				reloadAllTaskList();
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), 
						FacesMessage.SEVERITY_INFO, MessageConst.INFO_770104, 
						new String[] { stb.toString() });				
			}
		} finally {
			taskAssignmentSO = null;
		}

	}
	
	/**
	 * To Retieve USer Group User List By User Group Id
	 * 
	 * @param userGroupId
	 * @param approvalLimit
	 * @param excludeUserIds
	 * @return ISearchResultBean<PmUser>
	 */
	private ISearchResultBean<PmUser> retrieveUserGroupUserListByUserGroupId(Long userGroupId, BigDecimal approvalLimit, List<Long> excludeUserIds,ISearchBean<PmUser> searchBean) {
		log.info("retrieveUserGroupUserListByUserGroupId");
		IUserSO userSO = null;
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserSO.class);
			reqDTO.setUserSearchBean(searchBean);
			reqDTO.setApprovalLimit(approvalLimit);
			reqDTO.setGroupId(userGroupId);
			reqDTO.setExcludeUserIds(excludeUserIds);
			UserResDTO resDTO = userSO.retrieveUserListByUserGroupId(reqDTO);
			if (resDTO.isSuccessful() && resDTO.getUserResultBean() != null) {
				return resDTO.getUserResultBean();
			}
		} finally {
			userSO = null;
		}

		return null;
	}
	
	/**
	 * @return the userListDataModel
	 */
	public PagingDataModel<PmUser> getUserListDataModel() {
		return userListDataModel;
	}

	/**
	 * @param userListDataModel the userListDataModel to set
	 */
	public void setUserListDataModel(PagingDataModel<PmUser> userListDataModel) {
		this.userListDataModel = userListDataModel;
	}

	/**
	 * Available user data model (to confirm which user to be assignee)
	 * 
	 * @author Ken
	 *
	 */
	private class userPagingDataModel extends PagingDataModel<PmUser> {

		private static final long serialVersionUID = 1L;

		public userPagingDataModel() {
			super("dtTaskReassign");
		}

		@Override
		protected ISearchResultBean<PmUser> pageChangeListener(
				ISearchBean<PmUser> searchBean) {
			log.info("generateAvailableUsers");
			
			//[#25018]
			List<WorklistItemVO> tasks = new ArrayList<WorklistItemVO>();
			ISearchResultBean<PmUser> resultBean = null;
			HashMap<String, PmUser> users = new HashMap<String, PmUser>();
			String capitalizeUserName;
			PmTaskReassignmentVO vo = null;
			List<String> roleList = new ArrayList<String>();
			
			try {
				log.info("[#25018] isBulkReassign? " + getManagedBean().isBulkReassign());
				//[#25018]
				if(getManagedBean().isSingleReassign()){
				//WorklistItemVO task = getManagedBean().getSelectedTask();
					tasks.add(getManagedBean().getSelectedTask());
					getManagedBean().setTasksReassignmentVO(null); //[#25018] newly added
				}
				//[#25018]
				else if(getManagedBean().isBulkReassign()) {
					tasks.addAll(getManagedBean().getSelectedTasksToBulkReassign());
					getManagedBean().setTasksReassignmentVO(null); //[#25018] newly added
				}
				//[#25018]
				for(WorklistItemVO task : tasks) {	
					//[#25018]
					if(getManagedBean().isBulkReassign()) {
						vo = null;					
					}
					
						PmStatus status = PmCommonUtil.getPmStatusById(task.getDocStatus()
								.longValue());
						
						if (status != null) {
							//ISearchResultBean<PmUser> resultBean = null;
							//PmTaskReassignmentVO vo = getManagedBean().getTaskReassignmentVO();
							
							//List<String> roleList = new ArrayList<String>();
							int reassignType = status.getPmStatusTask() != null 
									&& status.getPmStatusTask().getReasssignType() != null 
									? status.getPmStatusTask().getReasssignType() : 0;
							String moduleCode = status.getModuleCode();
							
							log.info(":::pageChangeListener() reassignType: "+ reassignType +", moduleCode: "+ moduleCode +".");		
							log.info("[#25018] docType: "+ task.getDocType() +", docNo: "+ task.getDocNumber() + ", docStatus: " + task.getDocStatus().longValue() + ", taskName: " + task.getTaskName());		
							if(vo == null ){
								if (ModuleCodeConst.CONTRACT_MANAGEMENT
										.equalsIgnoreCase(moduleCode)) {
									vo = retrieveCtTaskReassignmentDetails(task,searchBean);
								} else if (ModuleCodeConst.SUPPLIER_SELECTION
										.equalsIgnoreCase(moduleCode)) {
									vo = retrieveSourcingTaskReassignmentDetail(task);
								} else if (ModuleCodeConst.ORDER_FULFILLMENT
										.equalsIgnoreCase(moduleCode)) {
									vo = retrieveFLTaskReassignmentDetail(task);
								}else if (ModuleCodeConst.PROCUREMENT_PLAN
										.equalsIgnoreCase(moduleCode)) {
									vo = retrievePpTaskReassignmentDetail(task);
								}else if(ModuleCodeConst.CATALOGUE_MANAGEMENT
										.equalsIgnoreCase(moduleCode)) {
									vo = retrieveCMTaskReassignmentDetail(task);
								}else if(ModuleCodeConst.PROFILE_MANAGEMENT
										.equalsIgnoreCase(moduleCode)) {
									vo = retrievePMTaskReassignmentDetail(task);
								}else if(ModuleCodeConst.SUPPLIER_MANAGEMENT
										.equalsIgnoreCase(moduleCode)) {
									vo = retrieveSMTaskReassignmentDetail(task);
									
								}
							}
							
							
							if(vo != null){
								vo.setStatus(status);
								if(getManagedBean().isSingleReassign()){ //[#25018]
									getManagedBean().setTaskReassignmentVO(vo);
								}
								//[#25018] -start
								else if(getManagedBean().isBulkReassign()){
									vo.setDocNo(task.getDocNumber()); //[#25018] newly added
									if(getManagedBean().getTasksReassignmentVO() != null) {
										log.info("[#25018] add vo...");
										getManagedBean().getTasksReassignmentVO().add(vo);
									} else {
										log.info("[#25018] declare new list and add vo...");
										getManagedBean().setTasksReassignmentVO(new ArrayList<PmTaskReassignmentVO>());
										getManagedBean().getTasksReassignmentVO().add(vo);
									}
								}
								//[#25018] -end
								log.info("[#25018] Role list [" + StringUtils.join(vo.getRoleList(), "|")+ "]");
								// 99 - MIXTURE(Sub Reassign Type)
								if(reassignType == ProfileManagementConst.REASSIGN_TYPE_MIXTURE){
									log.info("[#25018] reassignType:" + reassignType);
									roleList = vo.getRoleList();
									if(!roleList.isEmpty()){
										PmStatusRole statusRole = PmCommonUtil.retrieveStatusRoleByStatusIdAndRoleCode(task.getDocStatus().longValue(), roleList.get(0));
										if(statusRole != null && statusRole.getReasssignType() !=null){
											reassignType = statusRole.getReasssignType();
											log.info("[#25018] generateUserResultBean for reassignType:" + reassignType);
											resultBean = generateUserResultBean(searchBean, task,reassignType,roleList,vo);
										}
									}
								}else{
									//Start Fix - #34699
									if (moduleCode.equalsIgnoreCase(ModuleCodeConst.CONTRACT_MANAGEMENT)) {
										log.info("#34699 generateUserResultBean for reassignType:" + reassignType+ " for CT only ::: ");
										resultBean = generateUserResultBeanForCT(searchBean, task, reassignType, roleList,vo);
									} else {
										log.info("[#25018] generateUserResultBean for reassignType:" + reassignType);
										resultBean = generateUserResultBean(searchBean, task, reassignType, roleList, vo);
									}
									//End Fix - #34699
								}
								
								if(resultBean != null && resultBean.getResultList() != null){
									//String capitalizeUserName;
									//HashMap<String, PmUser> users = new HashMap<String, PmUser>();
									
									String currentUser = getManagedBean().getTaskAssignmentSearchBean().getUser() != null ?
											  getManagedBean().getTaskAssignmentSearchBean().getUser().getLoginId() : 
											  getManagedBean().getSelectedReassignee().get(0);
									log.info("[#25018] currentUser is:" + currentUser);		  
//								    for (PmUser getResultList : resultBean.getResultList()) {
//								    	if (!getResultList.getLoginId().equals(currentUser)) {
//								    		users.put(getResultList.getLoginId(), getResultList);
//								    	}
//								    }
								    //[#25018] -start
								    for (PmUser getResultList : resultBean.getResultList()) {
								    	if (!getResultList.getLoginId().equals(currentUser)) {
								    		if(users != null && users.size() > 0) {
								    			if(!users.containsKey(getResultList.getLoginId()))
								    				log.info("[#25018] add next user: " + getResultList.getLoginId());
								    				users.put(getResultList.getLoginId(), getResultList);
								    		} else {
								    			log.info("[#25018] add 1st user: " + getResultList.getLoginId());
							    				users.put(getResultList.getLoginId(), getResultList);
								    		}
								    	}
								    }					    
								  //[#25018] -end
//								    resultBean.getResultList().clear();
//									resultBean.getResultList().addAll(new ArrayList<PmUser>(users.values()));
//									
//									for(PmUser user : resultBean.getResultList()){
//										capitalizeUserName = WordUtils.capitalizeFully(user.getUserName());
//										user.setUserName(capitalizeUserName);
//									}
								    //[#22117]
								    //remove from list if login user has role BPK administrator
								    if (getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())) {
								    	log.info("[#22117] BPK admin login id: " + getFacesScopes().getUserSessionBean().getLoginId());
								    	if(users.containsKey(getFacesScopes().getUserSessionBean().getLoginId())) {
								    		users.remove(getFacesScopes().getUserSessionBean().getLoginId());
								    	}
								    }
								}
								//return resultBean;
							}				
							
						}
			} //end for statement
			
			if(resultBean != null && resultBean.getResultList() != null) {
				log.info("[#25018] add new list of user...");
			    resultBean.getResultList().clear();
				resultBean.getResultList().addAll(new ArrayList<PmUser>(users.values()));
				
				for(PmUser user : resultBean.getResultList()){
					log.info("[#25018] capitalize user name "+user.getIdentificationNo()+
							"("+user.getUserName()+")");
					capitalizeUserName = WordUtils.capitalizeFully(user.getUserName());
					user.setUserName(capitalizeUserName);
				}
				log.info("getTotalRecord:: "+resultBean.getTotalRecord());
				return resultBean;
			}
			
		}catch(Exception e){
			throw e;
		}
			return null;
		}
	}
	
	/**
	 * To search available user to be assignee
	 */
	public void searchAvailableUser(){
		userListDataModel.reloadData();
	}
	
	/**
	 * To reset user list data model 
	 */
	public void resetAvailableUser(){
		getManagedBean().setSearchUserName(null);
		getManagedBean().setTaskReassignmentVO(null);
		userListDataModel.resetData();
	}
	
	/**
	 * Action after docType has been selected
	 * 
	 * @param entity
	 */
	public void onChangeDocType(BaseEntity entity){
		this.getManagedBean().getTaskAssignmentSearchBean()
			.setSearched(false);
		this.getManagedBean().setTaskLazy(false);
		resetAllTaskList();
	}
	
	/**
	 * To generate user result bean based on reassignType
	 * 
	 * @param status
	 * @param searchBean
	 * @param task
	 * @param reassignType
	 * @param roleList
	 * @param vo
	 * @return ISearchResultBean<PmUser>
	 */
	private ISearchResultBean<PmUser> generateUserResultBean(
			 ISearchBean<PmUser> searchBean,WorklistItemVO task ,int reassignType,List<String> roleList,PmTaskReassignmentVO vo){
		PmOrgProfile orgProfile = null;
		ISearchResultBean<PmUser> resultBean = null;
		boolean isSearched = false;
		Long orgProfileId = getManagedBean()
						.getTaskAssignmentSearchBean().getOrgProfile()
						.getOrgProfileId();
		Long userId = getManagedBean().getTaskAssignmentSearchBean().getUser() != null ?
					  getManagedBean().getTaskAssignmentSearchBean().getUser().getUserId() : 
					  Long.valueOf(this.getManagedBean().getSelectedReassignee().get(0)).longValue();
		Long actualOrgProfileId = null;
		Long userGroupId = null;
		Long addressId = null;
		Integer subReassignType = null;
		
		log.info(":::generateUserResultBean() userId: "+ userId +", orgProfileId: "+ orgProfileId +", reassignType: "+ reassignType +".");
		
		// filter by user name
		if(StringUtils.isNotBlank(getManagedBean().getSearchUserName())){
			searchBean.getDataFilterBeans().add(
				new SearchFilterBean(
						"userName",
						SearchOperator.OPERATOR_LIKE.value(),
						getManagedBean().getSearchUserName(),true));
		}
			//admin list to be filtered from task reassignment
			List<String> filteredAdminRoles = new ArrayList<String>();
			filteredAdminRoles.add("JABATAN_ADMIN");
			filteredAdminRoles.add("MINISTRY_ADMIN");
			filteredAdminRoles.add("PTJ_ADMIN");
			/* 27827 - START */
			filteredAdminRoles.add("BPK_ADMIN");
			/* 27827 - END */
			

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserOrgs.pmUserRoles.pmRole.roleCode",
							SearchOperator.OPERATOR_NOT_IN.value(), filteredAdminRoles));
			
		if(roleList.isEmpty()){
			roleList = vo.getRoleList();
		}	
		
		switch (reassignType) {
		case ProfileManagementConst.REASSIGN_TYPE_COMMON:
			//1 - Normal Reassignment
			
			//check because to allow cross org profile for market research #10927
			if(task.getDocStatus().longValue() != ProfileManagementStatusConst.PENDING_MARKET_RESEARCH_ACCESS_APPROVAL.longValue()) {
				
				// same org profile id
				searchBean.getDataFilterBeans().add(
				new SearchFilterBean(
						"pmUserOrgs.pmOrgProfile.orgProfileId",
						SearchOperator.OPERATOR_EQUALS.value(),
						orgProfileId));
			}
			break;
		case ProfileManagementConst.REASSIGN_TYPE_S2:
			// 2: S2/CT Committee Task
			
			// not current selected user and not current login user
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("userId",
							SearchOperator.OPERATOR_NOT_IN.value(),
								new ArrayList<Long>(Arrays.asList(userId,
									getFacesScopes().getUserSessionBean()
										.getUserId()))));
			
			//#38680- Start
			String role = roleList.toString().replace("[", "").replace("]", "")
					.split(",")[0].trim();
			String committeeType = role.split("_")[0];
			if (excludeStatusIds.contains(vo.getStatus().getStatusId())
					&& isCommitteeType(committeeType)) {
				// Filter By - roleCode
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmUserOrgs.pmUserRoles.pmRole.roleCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								roleList));

				// Filter By - role status
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmUserOrgs.pmUserRoles.recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));
			}
			//#38680- End
			
			// Order by user name
			searchBean.getSortColumns().add(new SearchOrderBean("userName"));
			
			//Bug #25021 Task Reassignment Sabah & Sarawak - Finalization Comm
			log.info(":::generateUserResultBean() userId: "+ userId +", vo.getDocId(): "+ vo.getDocId() 
					+", vo.getDocType(): "+ vo.getDocType() +", vo.getRoleList(): "+ vo.getRoleList() +".");	
			
			resultBean = generateCommitteAssignees(searchBean, vo.getDocId(), vo.getDocType(), roleList, orgProfileId);
			//Bug #25021
			
			getManagedBean().setCommitteeUpdate(true);		
					
			isSearched = true;
			break;
		case ProfileManagementConst.REASSIGN_TYPE_CROSS_PTJ:
			//3 - Cross PTJ
			//#35396 - Start
			if(orgProfileId != null){
				orgProfile	= PmCommonUtil.retrievePmOrgProfileDtl(orgProfileId);
			}
			//#35396 - End
			
			if (orgProfile != null) {
				if (OrganizationType.MINISTRY.value().equals(
						orgProfile.getOrgTypeCode().getParameterCode())) {
					// PTJ under same ministry
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									"pmUserOrgs.pmOrgProfile." + OrgProfileUtil
											.getSearchBeanParentString(OrganizationType.MINISTRY
													.value()),
									SearchOperator.OPERATOR_EQUALS
											.value(), orgProfile
											.getOrgProfileId()));
				} else if (OrganizationType.DIVISION.value().equals(
						orgProfile.getOrgTypeCode().getParameterCode())) {
					// same org profile id
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									"pmUserOrgs.pmOrgProfile.orgProfileId",
									SearchOperator.OPERATOR_EQUALS
											.value(), orgProfile
											.getOrgProfileId()));
				}
			} else {
				// not allow to search
				isSearched = true;
			}
			break;
		case ProfileManagementConst.REASSIGN_TYPE_USER_MIN_KPTJ_PTJ:
			//4 - Include User under Ministry, Kumpulan PTJ & PTJ
			actualOrgProfileId = vo.getOrgProfileId();
			if(actualOrgProfileId != null){
				if(roleList == null || roleList.isEmpty()){
					roleList = retrieveAvailableRoles(task.getDocStatus()
						.longValue(),reassignType,subReassignType);
				}
				resultBean = PmCommonUtil.retrieveUserListFromGroupOrgType(
						actualOrgProfileId,
						roleList,
						false,
						null,
						new ArrayList<Long>(Arrays.asList(userId,
								getFacesScopes().getUserSessionBean()
										.getUserId())),searchBean);
				
			}
			isSearched = true;
			
			break;
		case ProfileManagementConst.REASSIGN_TYPE_NAIK_TARAF_PTJ:
			//5 - Include User for Naik Taraf & PTJ
			
			actualOrgProfileId = vo.getOrgProfileId();
			if(actualOrgProfileId != null){
				if(roleList == null || roleList.isEmpty()){
					roleList = retrieveAvailableRoles(task.getDocStatus()
						.longValue(),reassignType,subReassignType);
				}
				
				resultBean = PmCommonUtil.retrieveUserListFromGroupOrgType(
						actualOrgProfileId,
						roleList,
						true,
						ParameterConst.ORG_AUTH_TYPE_ORG_GROUP,
						new ArrayList<Long>(Arrays.asList(userId,
								getFacesScopes().getUserSessionBean()
										.getUserId())),searchBean);
			}
			isSearched = true;
			break;
		case ProfileManagementConst.REASSIGN_TYPE_USER_GROUP:
			//6 - User Group
			BigDecimal approvalLimit = vo.getApprovalLimit();
			userGroupId = vo.getUserGroupId();
						
			if(userGroupId != null){
				resultBean = retrieveUserGroupUserListByUserGroupId(
						userGroupId,
						approvalLimit,
						new ArrayList<Long>(Arrays.asList(userId,
						getFacesScopes().getUserSessionBean()
						.getUserId())),searchBean);
			}
			isSearched = true;
			break;
		case ProfileManagementConst.REASSIGN_TYPE_DEL_ADDRESS:
			//7 - Delivery Address
			addressId = vo.getAddressId();
			
			if(addressId != null){
				// same address id
				searchBean
						.getDataFilterBeans()
						.add(new SearchFilterBean(
								"pmAddressTypes.pmAddress.addressId",
								SearchOperator.OPERATOR_EQUALS
										.value(), addressId));
				
				searchBean
				.getDataFilterBeans()
				.add(new SearchFilterBean(
						"pmAddressTypes.recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS
								.value(), DbValueConst.RECORD_STATUS_ACTIVE));
				
				searchBean
				.getDataFilterBeans()
				.add(new SearchFilterBean(
						"pmAddressTypes.pmAddress.recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS
								.value(), DbValueConst.RECORD_STATUS_ACTIVE));
				
			}else{
				// not allow to search
				isSearched = true;
			}
			
			break;
		case ProfileManagementConst.REASSIGN_TYPE_USER_MIN_KPTJ_PTJ_NAIK_TARAF:
			//8 - Include user for Ministry, Kumpulan PTJ, PTJ & Naik Taraf
		if(vo.getOrgProfileId() != null){
			if(roleList == null || roleList.isEmpty()){
					roleList = retrieveAvailableRoles(task.getDocStatus()
						.longValue(),reassignType,subReassignType);
				}
				resultBean = PmCommonUtil.retrieveUserListFromGroupOrgTypeForSourcing(
						vo.getOrgProfileId(),
						roleList,						
						new ArrayList<Long>(Arrays.asList(userId,
								getFacesScopes().getUserSessionBean()
										.getUserId())),searchBean);
				
			}
		
			//#37794 - Start
			if (!Arrays.toString(roleList.toArray()).replace("[", "")
					.replace("]", "")
					.equalsIgnoreCase(RoleCode.DESK_OFFICER.value())) {
				getManagedBean().setCommitteeUpdate(true);
			}
			//#37794 - End
			
			isSearched = true;
			break;
		case ProfileManagementConst.REASSIGN_TYPE_SUPPLIER:
			//9 GOVT_SELLER
			// same org profile id
			searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
							"pmUserOrgs.pmOrgProfile.orgProfileId",
								SearchOperator.OPERATOR_EQUALS.value(),
								orgProfileId));
			break;
		case ProfileManagementConst.REASSIGN_TYPE_SUPPLIER_OR_PTJ:
			//10 - Supplier(multiple roles) / PTJ(single)
		
			// if isSupplier then PmStatusRole.reassignType = 9 else PmStatusRole.reassignType = 1
				subReassignType = vo.isSupplier() ? 9 : 1;

			// same org profile id
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
							"pmUserOrgs.pmOrgProfile.orgProfileId",
							SearchOperator.OPERATOR_EQUALS.value(),
							orgProfileId));
				
			
			break;
		/* 27827 - START */
		case ProfileManagementConst.REASSIGN_TYPE_PTJ_AND_BPK:
			//11 - Include PTJ & BPK

			if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value()) ||
				getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value()) ||
				getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
				log.info("----- User PTJ, JABATAN OR MINISTRY -----");
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmUserOrgs.pmOrgProfile.orgProfileId",
						SearchOperator.OPERATOR_EQUALS.value(),
						orgProfileId));
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmUserOrgs.pmOrgProfile.bpk",
						SearchOperator.OPERATOR_EQUALS.value(),
						true));
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmUserOrgs.pmOrgProfile.pmZone.zoneId",
						SearchOperator.OPERATOR_EQUALS.value(),
						1L));
			} else if (getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())) {
				log.info("----- User BPK -----");
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmUserOrgs.pmOrgProfile.orgProfileId",
						SearchOperator.OPERATOR_EQUALS.value(),
						orgProfileId));
			} else {
				log.info("----- User EPADMIN, EPADMIN2 -----");
				List<String> paramcodes=new ArrayList<String>();
				paramcodes.add(ParameterConst.ORG_TYPE_PTJ);
				paramcodes.add(ParameterConst.ORG_TYPE_BPK);
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmUserOrgs.pmOrgProfile.orgTypeCode.parameterCode",
						SearchOperator.OPERATOR_IN.value(),
						paramcodes));
			}

			break;
		/* 27827 - END */
		default:
			// not allow to search
			isSearched = true;
		}
		
		if(!isSearched){
			// Filter By user status
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE));
			
			// Filter By user org status
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmUserOrgs.recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));		

			// Filter By role status
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmUserOrgs.pmUserRoles.recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE));	

			// not current selected user and not current login user
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("userId",
							SearchOperator.OPERATOR_NOT_IN.value(),
							new ArrayList<Long>(Arrays.asList(userId,
									getFacesScopes().getUserSessionBean()
											.getUserId()))));

			if (roleList == null || roleList.isEmpty()) {
				roleList = retrieveAvailableRoles(task.getDocStatus()
						.longValue(),reassignType,subReassignType);
				//--10927
				if (getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())){
					for(Iterator<String> role=roleList.listIterator();
							role.hasNext();){
						String a=role.next();
						if (a.equals(RoleCode.EP_ADMIN_2.value())){
							role.remove();
							log.info("roleList1="+roleList.size());
						}
						if (a.equals(RoleCode.BPK_ADMIN.value())){
							role.remove();
							log.info("roleList2="+roleList.size());
						}
						
					}
				}
				
				if (getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())){
					for(Iterator<String> role=roleList.listIterator();
							role.hasNext();){
						String a=role.next();
						if (a.equals(RoleCode.BPK_ADMIN.value())){
							role.remove();
							log.info("roleList_SYS="+roleList.size());
						}
						
					}
				}
				
				//--10927
				
				/*//--10927
				if (getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())){
					roleList.remove(RoleCode.EP_ADMIN_2.value());
					System.out.println("Remove EpAdmin2::::::::::::::::::");
				}
				
				//--10927*/
			}

			if (!roleList.isEmpty()) {
				// in applicable to roles
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmUserOrgs.pmUserRoles.pmRole.roleCode",
								SearchOperator.OPERATOR_IN.value(), roleList));
			}
		
			
			// Order by user name
			searchBean.getSortColumns().add(new SearchOrderBean("userName"));
			
			resultBean = PmCommonUtil.searchUsersResultBeanBySearchBean(searchBean);
		}
		log.info("isSearched:: "+isSearched);
		return resultBean;
		
	}
	
	/**
	 * Remove group task/other task from user.
	 */
	public void removeReassignTask() {
		log.info("removeReassignTask");

		String claimedBy = null;
		WorklistItemVO task = this.getManagedBean().getSelectedTask();
		ITaskAssignmentSO taskAssignmentSO = null;
		int reassignWorkListType = getManagedBean().getTaskType();
		
		if (task == null) {
			throw new EpException(MessageConst.ERROR_770097);
		}
		List<String> assigneeUsers = task.getAssignees();
		if (assigneeUsers !=null && !assigneeUsers.isEmpty()) {
			// remove the current user.
			String currentUser = getManagedBean().getTaskAssignmentSearchBean().getUser() != null ?
					  getManagedBean().getTaskAssignmentSearchBean().getUser().getLoginId() : 
					  this.getManagedBean().getSelectedReassignee().get(0);
			
			if (assigneeUsers.contains(currentUser)) {
				assigneeUsers.remove(currentUser);
			}
			if(assigneeUsers != null && assigneeUsers.size() > 1 && ProfileManagementConst.REASSIGN_WORKLIST_TYPE_OTHERS == reassignWorkListType){
				claimedBy = task.getAcquiredBy();
			}
		}

		try {
			TaskAssignmentReqDTO reqDTO = new TaskAssignmentReqDTO();
			// #7465 Re-open issues on 24/11/2023
			String taskId = task.getTaskId();
			reqDTO.setTaskId(taskId);
			reqDTO.setUserIdList(assigneeUsers);
			reqDTO.setClaimedBy(claimedBy);

			taskAssignmentSO = (ITaskAssignmentSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							ITaskAssignmentSO.class);
			TaskAssignmentResDTO resDTO = taskAssignmentSO.removeGroupOrOthersTask(reqDTO);

			// isSuccess = reassignTask(pmUser, task);
			if (resDTO != null && resDTO.isSuccessful()) {
				resetForRemoveReassignTask(taskId); // #7465 Re-open issues on 14/12/2023
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), 
						FacesMessage.SEVERITY_INFO, MessageConst.INFO_770105, null);
			}
		} finally {
			taskAssignmentSO = null;
		}

	}

	// #7465 Re-open issues on 24/11/2023 & 14/12/2023 Start
	private void resetForRemoveReassignTask(String taskId) {
		boolean removedFromSelectedTask = false;
		resetCheckAllTasks();
		if (getManagedBean().getSelectedGroupTasks().containsKey(taskId)) {
			getManagedBean().getAllGroupTasks().clear();
			getManagedBean().getSelectedGroupTasks().clear();
			getManagedBean().getSelectedGroupTasks().remove(taskId);
			getManagedBean().setEnableDeleteGroupTasksButton(false);
			removedFromSelectedTask = true;
		} else if (getManagedBean().getSelectedOthersTasks().containsKey(taskId)) {
			getManagedBean().getAllOthersTasks().clear();
			getManagedBean().getSelectedOthersTasks().clear();
			getManagedBean().getSelectedOthersTasks().remove(taskId);
			getManagedBean().setEnableDeleteOthersTasksButton(false);
			removedFromSelectedTask = true;
		}

		if (!removedFromSelectedTask) {
			getManagedBean().setEnableDeleteGroupTasksButton(false);
			getManagedBean().setEnableDeleteOthersTasksButton(false);
		}
		reloadAllTaskList();
	}
	// #7465 Re-open issues on 24/11/2023 & 14/12/2023 End
	
	/**
	 * @return the userTaskListDataModel
	 */
	public PagingDataModel<WorklistItemVO> getUserTaskListDataModel() {
		return userTaskListDataModel;
	}

	/**
	 * @param userTaskListDataModel the userTaskListDataModel to set
	 */
	public void setUserTaskListDataModel(
			PagingDataModel<WorklistItemVO> userTaskListDataModel) {
		this.userTaskListDataModel = userTaskListDataModel;
	}

	/**
	 * @return the groupTaskListDataModel
	 */
	public PagingDataModel<WorklistItemVO> getGroupTaskListDataModel() {
		return groupTaskListDataModel;
	}

	/**
	 * @param groupTaskListDataModel the groupTaskListDataModel to set
	 */
	public void setGroupTaskListDataModel(
			PagingDataModel<WorklistItemVO> groupTaskListDataModel) {
		this.groupTaskListDataModel = groupTaskListDataModel;
	}

	/**
	 * @return the othersTaskListDataModel
	 */
	public PagingDataModel<WorklistItemVO> getOthersTaskListDataModel() {
		return othersTaskListDataModel;
	}

	/**
	 * @param othersTaskListDataModel the othersTaskListDataModel to set
	 */
	public void setOthersTaskListDataModel(
			PagingDataModel<WorklistItemVO> othersTaskListDataModel) {
		this.othersTaskListDataModel = othersTaskListDataModel;
	}
	
	public void redirectToTaskReassignment(PmOrgProfile orgProfile, PmUser user) {		
		log.info("redirectToTaskReassignment");
		// 20231012 #7465 - Start
		this.getManagedBean().setPmOrgProfile(orgProfile);
		this.getManagedBean().setPmUser(user);
		// 20231012 #7465 - End
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackURL(fc.getViewRoot().getViewId());		
		resetTaskAssignSearchBean();
		retrieveOrgTypeMap();
		//[#25018] - start		
		this.getManagedBean().setShowParentMsg(true);
		this.getManagedBean().setShowChildMsg(false);
		setIsAdminUser();
		retrieveStatusIdsForInvalidBulkTask();
		if (this.getManagedBean().getSelectedTasksToBulkReassign() != null && 
				this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
			this.getManagedBean().getSelectedTasksToBulkReassign().clear();
		}
		//[#25018] - end
		
		// 20231012 #7465 - Start
		this.getManagedBean().setFromUserMaintenanceDetails(true);
		reinitRedirectedTaskReassignmentSearchBean();
		// 20231012 #7465 - End
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "/xhtml/pm/taskReassignment/taskReassignment.xhtml");
	}
	
	// 20231012 #7465 - Start
	private void reinitRedirectedTaskReassignmentSearchBean() {
		this.getManagedBean().getTaskAssignmentSearchBean().setOrgProfile(this.getManagedBean().getPmOrgProfile());
		this.getManagedBean().getTaskAssignmentSearchBean().setOrgType(this.getManagedBean().getPmOrgProfile().getOrgTypeCode());
		this.getManagedBean().getTaskAssignmentSearchBean().setUser(this.getManagedBean().getPmUser());
		this.getManagedBean().setOrgProfileRequired(false);
		this.getManagedBean().setDisableForTaskAssignment(true);
	}
	// 20231012 #7465 - End
	
	public void redirectToPmUserPage(){
		log.info("this.getManagedBean().getBackURL()"+this.getManagedBean().getBackURL());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, this.getManagedBean().getBackURL()); 

	}
	
	private ISearchResultBean<PmUser> generateCommitteAssignees(ISearchBean<PmUser> searchBean,
			Long docId, String docType, List<String> roleCodeList, Long orgProfileId){
		ICommitteSO committeSO;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			CommitteReqDTO committeReqDTO = new CommitteReqDTO();
			committeReqDTO.setDocId(docId);
			committeReqDTO.setDocType(docType);
			committeReqDTO.setRoleCode(roleCodeList != null && !roleCodeList.isEmpty() ? roleCodeList.get(0) : "");
			committeReqDTO.setUserSearchBean(searchBean);
			//Bug #25021
			committeReqDTO.setOrgProfileId(orgProfileId);
			//end Bug #25021
			
			return committeSO.retrieveUserListByCommType(committeReqDTO).getUserResultBean();
		} finally {
			committeSO = null;
		}
	}
	
	private PmStatusDesc retrieveStatusDesc(Long statusId){
		IStatusSO statusSO;
		PmStatusDesc statusDesc;
		try {
			statusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(statusId);
			reqDTO.setLanguageCode("en");
			
			// Get status description from db.
			StatusResDTO resDTO = statusSO.retrieveStatusDescById(reqDTO);
			
			statusDesc = resDTO.getPmStatusDesc();		    
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			statusSO = null;
		}

		return statusDesc;
	}
	
	// Enhancement #25013					
	public void checkAllGroupTasks() {
		
		log.info("Total number of GROUP tasks= "+this.getManagedBean().getAllGroupTasks().size());
		
		if (this.getManagedBean().getSelectedGroupTasks() != null && 
				this.getManagedBean().getSelectedGroupTasks().size() > 0) {
			this.getManagedBean().getSelectedGroupTasks().clear();
		}
    	for ( WorklistItemVO task : this.getManagedBean().getAllGroupTasks()) {
    		if (this.getManagedBean().isCheckAllGroupTasks()) {
    			task.setTaskSelect(true);
    			this.getManagedBean().getSelectedGroupTasks().put(task.getTaskId(),task);
    		} else {
    			task.setTaskSelect(false);
    			if (this.getManagedBean().getSelectedGroupTasks().containsKey(task.getTaskId())) {
	    			this.getManagedBean().getSelectedGroupTasks().remove(task.getTaskId());
	    		}
    		}
		}
    	this.getManagedBean().setEnableDeleteGroupTasksButton(this.getManagedBean().isCheckAllGroupTasks());
    	log.info("getSelectedGroupTasks= "+this.getManagedBean().getSelectedGroupTasks().size());
    	log.info("enableDeleteGroupTasksButton= "+this.getManagedBean().isEnableDeleteGroupTasksButton());
	}
	
	public void checkGroupTask(AjaxBehaviorEvent event) {
		
		if ((boolean) event.getComponent().getAttributes().get("value")) {
			for (WorklistItemVO checkedTask : this.getManagedBean().getAllGroupTasks()) {
		    	if (checkedTask.getTaskSelect()) {
		    		log.info("task= "+checkedTask.getTaskId());
		    		this.getManagedBean().getSelectedGroupTasks().put(checkedTask.getTaskId(),checkedTask);
		    	}
		    }
		} else {
			for (WorklistItemVO uncheckedTask : this.getManagedBean().getAllGroupTasks()) {
				if (!uncheckedTask.getTaskSelect()) {
					uncheckedTask.setTaskSelect(false);
		    		this.getManagedBean().getSelectedGroupTasks().remove(uncheckedTask.getTaskId());
		    	}
			}
		}
		//#7465 start - single checkbox not enable remove button
		if ( (this.getManagedBean().getSelectedGroupTasks() != null && 
				!this.getManagedBean().getSelectedGroupTasks().isEmpty()) &&
				(this.getManagedBean().getSelectedGroupTasks().size() >= 1) ) {
			this.getManagedBean().setEnableDeleteGroupTasksButton(true);
    	} else {
    		this.getManagedBean().setEnableDeleteGroupTasksButton(false);
    	}
		log.info("getSelectedGroupTasks= "+this.getManagedBean().getSelectedGroupTasks().size());
		log.info("enableDeleteGroupTasksButton= "+this.getManagedBean().isEnableDeleteGroupTasksButton());
	}
	
	public void checkAllOthersTasks() {
		
		log.info("Total number of OTHERS tasks= "+this.getManagedBean().getAllOthersTasks().size());
		
		if (this.getManagedBean().getSelectedOthersTasks() != null && 
				this.getManagedBean().getSelectedOthersTasks().size() > 0) {
			this.getManagedBean().getSelectedOthersTasks().clear();
		}
    	for ( WorklistItemVO task : this.getManagedBean().getAllOthersTasks()) {
    		if (this.getManagedBean().isCheckAllOthersTasks()) {
    			task.setTaskSelect(true);
    			this.getManagedBean().getSelectedOthersTasks().put(task.getTaskId(),task);
    		} else {
    			task.setTaskSelect(false);
    			if (this.getManagedBean().getSelectedOthersTasks().containsKey(task.getTaskId())) {
	    			this.getManagedBean().getSelectedOthersTasks().remove(task.getTaskId());
	    		}
    		}
		}
    	this.getManagedBean().setEnableDeleteOthersTasksButton(this.getManagedBean().isCheckAllOthersTasks());
    	log.info("getSelectedOthersTasks= "+this.getManagedBean().getSelectedOthersTasks().size());
    	log.info("enableDeleteOthersTasksButton= "+this.getManagedBean().isEnableDeleteOthersTasksButton());
	}
	
	public void checkOthersTask(AjaxBehaviorEvent event) {
		
		if ((boolean) event.getComponent().getAttributes().get("value")) {
			for (WorklistItemVO checkedTask : this.getManagedBean().getAllOthersTasks()) {
		    	if (checkedTask.getTaskSelect()) {
		    		log.info("task= "+checkedTask.getTaskId());
		    		this.getManagedBean().getSelectedOthersTasks().put(checkedTask.getTaskId(),checkedTask);
		    	}
		    }
		} else {
			for (WorklistItemVO uncheckedTask : this.getManagedBean().getAllOthersTasks()) {
				if (!uncheckedTask.getTaskSelect()) {
					uncheckedTask.setTaskSelect(false);
					this.getManagedBean().getSelectedOthersTasks().remove(uncheckedTask.getTaskId());
		    	}
			}
		}
		//#7465 start - single checkbox not enable remove button
		if ( (this.getManagedBean().getSelectedOthersTasks() != null && 
				!this.getManagedBean().getSelectedOthersTasks().isEmpty()) &&
				(this.getManagedBean().getSelectedOthersTasks().size() >= 1) ) {
			this.getManagedBean().setEnableDeleteOthersTasksButton(true);
    	} else {
    		this.getManagedBean().setEnableDeleteOthersTasksButton(false);
    	}
		log.info("getSelectedOthersTasks= "+this.getManagedBean().getSelectedOthersTasks().size());
		log.info("enableDeleteOthersTasksButton= "+this.getManagedBean().isEnableDeleteOthersTasksButton());
	}
	
	public void removeReassignGroupTasks() {
		
		log.info("removeReassignGroupTasks");

		String claimedBy = null;
		ITaskAssignmentSO taskAssignmentSO = null;
		int reassignWorkListType = getManagedBean().getTaskType();
		List<String> assigneeUsers = new ArrayList<String>();
		
		TaskAssignmentResDTO resDTO = new TaskAssignmentResDTO();
		
		if (this.getManagedBean().getSelectedGroupTasks() != null && 
				!this.getManagedBean().getSelectedGroupTasks().isEmpty()) {
			
			log.info("Number of task(s) to be deleted= "+this.getManagedBean().getSelectedGroupTasks().size());
			
			for (Entry<String, WorklistItemVO> selectedTask : this.getManagedBean().getSelectedGroupTasks().entrySet()) {
				
				String taskId = selectedTask.getKey();
				WorklistItemVO task = selectedTask.getValue();
				log.info("Task(s) to be deleted: "+task.getDocNumber()+" - "+taskId);
				
				if (!task.getAssignees().isEmpty()) {
					assigneeUsers = task.getAssignees();
				}
				if (assigneeUsers != null && !assigneeUsers.isEmpty()) {
					String currentUser = getManagedBean().getTaskAssignmentSearchBean().getUser() != null ?
							  getManagedBean().getTaskAssignmentSearchBean().getUser().getLoginId() : 
							  this.getManagedBean().getSelectedReassignee().get(0);
					
					if (assigneeUsers.contains(currentUser)) {
						assigneeUsers.remove(currentUser);
					}
					if (assigneeUsers != null && assigneeUsers.size() > 1 && 
							ProfileManagementConst.REASSIGN_WORKLIST_TYPE_OTHERS == reassignWorkListType) {
						claimedBy = task.getAcquiredBy();
					}
				}
				
				try {
					TaskAssignmentReqDTO reqDTO = new TaskAssignmentReqDTO();
					reqDTO.setTaskId(task.getTaskId());
					reqDTO.setUserIdList(assigneeUsers);
					reqDTO.setClaimedBy(claimedBy);

					taskAssignmentSO = (ITaskAssignmentSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									ITaskAssignmentSO.class);
					resDTO = taskAssignmentSO.removeGroupOrOthersTask(reqDTO);

				} finally {
					taskAssignmentSO = null;
				}
			}
		}
		if (resDTO != null && resDTO.isSuccessful()) {
			resetCheckAllTasks();
			this.getManagedBean().getAllGroupTasks().clear();
			this.getManagedBean().getSelectedGroupTasks().clear();
			this.getManagedBean().setEnableDeleteGroupTasksButton(false);
			reloadAllTaskList();
			PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), 
			FacesMessage.SEVERITY_INFO, MessageConst.INFO_770105, null);
		}
	}
	
	//#7465, #29822 - start
	/**
	 * To remove all group task
	 * 
	 */	
	public void removeReassignAllGroupTasks() {
		String loginId = null;
		
		TaskAssignmentResDTO resDTO = new TaskAssignmentResDTO();
		TaskAssignmentReqDTO reqDTO = new TaskAssignmentReqDTO();
		
		TaskAssignmentSearchBean taskSearchBean = null;
		
		ISupplierDataMaintenanceSO taskAssignmentSO = null;
		try {
			
			log.info("==============start insert pm_pending_remove task to DB ==============");
			
			taskAssignmentSO = (ISupplierDataMaintenanceSO) BusinessDelegate
					.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierDataMaintenanceSO.class);
			
			taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
			loginId = taskSearchBean.getUser() != null ? taskSearchBean
					.getUser().getLoginId() : null;

			if (loginId == null) {
				if (getManagedBean().getResultUser() != null) {
					loginId = getManagedBean().getResultUser().getLoginId();
				}
			} 
			if (loginId != null && loginId.length() > 0) {

				if (getManagedBean().getTaskAssignmentSearchBean()
						.getSelectedRoles() != null
						&& !getManagedBean().getTaskAssignmentSearchBean()
								.getSelectedRoles().isEmpty()) {
					reqDTO.setParamRole(new ArrayList<String>());
					reqDTO.getParamRole().addAll(getManagedBean()
							.getTaskAssignmentSearchBean().getSelectedRoles());
				} else if (getManagedBean().getRoleCodes() != null
						&& !getManagedBean().getRoleCodes().isEmpty()) {
					reqDTO.setParamRole(new ArrayList<String>());
					reqDTO.getParamRole().addAll(getManagedBean().getRoleCodes());
				}
				
				//user_id
				reqDTO.setUserId(loginId);
				//param_task
				reqDTO.setParamTask("GROUP");
				//module
				reqDTO.setModule("PM");
				//created by
				reqDTO.setCreatedBy(getUserByLoginId());
				//#7465 START - reendorse
				String docNumber =getManagedBean().getTaskAssignmentSearchBean().getDocNo() ;
				if (StringUtils.isNotBlank(docNumber)) {
					// param_doc_no
					reqDTO.setParamDocNo(docNumber);
				}
				//#7465 end - reendorse
				if (getManagedBean().getTaskAssignmentSearchBean().getDocType() != null
						&& getManagedBean().getTaskAssignmentSearchBean().getDocType() != null) {
					//param_doc_type
					reqDTO.setParamDocType(getManagedBean()
							.getTaskAssignmentSearchBean().getDocType()
							.getPmParameter().getParameterCode());
				}
				resDTO = taskAssignmentSO.savePmPendingRemoveTask(reqDTO);
				
			}
		} catch (Exception e) {
			log.info("==============error pm_pending_remove task to DB ==============");
			e.printStackTrace();
		} finally {
			log.info("==============finsh pm_pending_remove task to DB ==============");
		}
		
		
		if (resDTO != null && resDTO.isSuccessful()) {
			PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), 
			FacesMessage.SEVERITY_INFO, MessageConst.INFO_780071, null);
			
			//#7465, #29822 - START - reendorse
			// 20231012 #7465
			this.getManagedBean().setEnableDeleteGroupTasksButton(false);
			this.getManagedBean().setEnableRemoveAllGroupTasksButton(false);
			this.getManagedBean().setCheckAllGroupTasksEnabled(false);
			this.getManagedBean().setShowRemoveProcessMessageGroup(true);
			//#7465, #29822  - end - reendorse
			log.info("==============dialog is completed closed==============");

		}
	}
	/**
	 * To remove all others task
	 * 
	 */	
	public void removeReassignAllOthersTasks() {
		String loginId = null;

		TaskAssignmentResDTO resDTO = new TaskAssignmentResDTO();
		TaskAssignmentReqDTO reqDTO = new TaskAssignmentReqDTO();

		TaskAssignmentSearchBean taskSearchBean = null;

		ISupplierDataMaintenanceSO taskAssignmentSO = null;
		try {
			
			log.info("==============start insert pm_pending_remove task to DB ==============");
			
			taskAssignmentSO = (ISupplierDataMaintenanceSO) BusinessDelegate
					.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierDataMaintenanceSO.class);

			taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
			loginId = taskSearchBean.getUser() != null ? taskSearchBean.getUser().getLoginId() : null;

			if (loginId == null) {
				if (getManagedBean().getResultUser() != null) {
					loginId = getManagedBean().getResultUser().getLoginId();
				}
			}
			if (loginId != null && loginId.length() > 0) {

				if (getManagedBean().getTaskAssignmentSearchBean()
						.getSelectedRoles() != null
						&& !getManagedBean().getTaskAssignmentSearchBean()
								.getSelectedRoles().isEmpty()) {
					reqDTO.setParamRole(new ArrayList<String>());
					reqDTO.getParamRole().addAll(getManagedBean()
							.getTaskAssignmentSearchBean().getSelectedRoles());
				} else if (getManagedBean().getRoleCodes() != null
						&& !getManagedBean().getRoleCodes().isEmpty()) {
					reqDTO.setParamRole(new ArrayList<String>());
					reqDTO.getParamRole().addAll(getManagedBean().getRoleCodes());
				}

				// user_id
				reqDTO.setUserId(loginId);
				// param_task
				reqDTO.setParamTask("OTHERS");
				//module
				reqDTO.setModule("PM");
				//created by
				reqDTO.setCreatedBy(getUserByLoginId());

				//#7465 - START - reendorse
				String docNumber =getManagedBean().getTaskAssignmentSearchBean().getDocNo() ;
				if (StringUtils.isNotBlank(docNumber)) {
					// param_doc_no
					reqDTO.setParamDocNo(docNumber);
				}
				//#7465 - end - reendorse

				if (getManagedBean().getTaskAssignmentSearchBean().getDocType() != null
						&& getManagedBean().getTaskAssignmentSearchBean().getDocType() != null) {
					// param_doc_type
					reqDTO.setParamDocType(getManagedBean()
							.getTaskAssignmentSearchBean().getDocType()
							.getPmParameter().getParameterCode());
				}

				resDTO = taskAssignmentSO.savePmPendingRemoveTask(reqDTO);

			}
		} catch (Exception e) {
			log.info("==============error pm_pending_remove task to DB ==============");
			e.printStackTrace();
		} finally {
			log.info("==============finsh pm_pending_remove task to DB ==============");
		}

		if (resDTO != null && resDTO.isSuccessful()) {
			PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), FacesMessage.SEVERITY_INFO,
					MessageConst.INFO_780071, null);
			
			//#7465, #29822  - START - reendorse
			// 20231012 #7465
			this.getManagedBean().setEnableDeleteOthersTasksButton(false);
			this.getManagedBean().setEnableRemoveAllOthersTasksButton(false);
			this.getManagedBean().setCheckAllOthersTasksEnabled(false); 
			this.getManagedBean().setShowRemoveProcessMessageOther(true);
			//#7465, #29822  - end - reendorse

			log.info("==============dialog is completed closed==============");

		}
	}
	//#7465, #29822 - end

	public void removeReassignOthersTasks() {
		
		log.info("removeReassignOthersTasks");

		String claimedBy = null;
		ITaskAssignmentSO taskAssignmentSO = null;
		int reassignWorkListType = getManagedBean().getTaskType();
		List<String> assigneeUsers = new ArrayList<String>();
		
		TaskAssignmentResDTO resDTO = new TaskAssignmentResDTO();
		
		if (this.getManagedBean().getSelectedOthersTasks() != null && 
				!this.getManagedBean().getSelectedOthersTasks().isEmpty()) {
			
			log.info("Number of task(s) to be deleted= "+this.getManagedBean().getSelectedOthersTasks().size());
			
			for (Entry<String, WorklistItemVO> selectedTask : this.getManagedBean().getSelectedOthersTasks().entrySet()) {
				
				String taskId = selectedTask.getKey();
				WorklistItemVO task = selectedTask.getValue();
				log.info("Task(s) to be deleted: "+taskId);
				
				if (!task.getAssignees().isEmpty()) {
					assigneeUsers = task.getAssignees();
				}
				
				if (assigneeUsers != null && !assigneeUsers.isEmpty()) {
					String currentUser = getManagedBean().getTaskAssignmentSearchBean().getUser() != null ?
							  getManagedBean().getTaskAssignmentSearchBean().getUser().getLoginId() : 
							  this.getManagedBean().getSelectedReassignee().get(0);
					
					if (assigneeUsers.contains(currentUser)) {
						assigneeUsers.remove(currentUser);
					}
					if (assigneeUsers != null && assigneeUsers.size() > 1 && 
							ProfileManagementConst.REASSIGN_WORKLIST_TYPE_OTHERS == reassignWorkListType) {
						claimedBy = task.getAcquiredBy();
					}
				}
				
				try {
					TaskAssignmentReqDTO reqDTO = new TaskAssignmentReqDTO();
					reqDTO.setTaskId(task.getTaskId());
					reqDTO.setUserIdList(assigneeUsers);
					reqDTO.setClaimedBy(claimedBy);

					taskAssignmentSO = (ITaskAssignmentSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									ITaskAssignmentSO.class);
					resDTO = taskAssignmentSO.removeGroupOrOthersTask(reqDTO);

				} finally {
					taskAssignmentSO = null;
				}
			}
		} else {
			throw new EpException(MessageConst.ERROR_770098);
		}
		if (resDTO != null && resDTO.isSuccessful()) {
			resetCheckAllTasks();
			this.getManagedBean().getAllOthersTasks().clear();
			this.getManagedBean().getSelectedOthersTasks().clear();
			this.getManagedBean().setEnableDeleteOthersTasksButton(false);
			reloadAllTaskList();
			PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), 
			FacesMessage.SEVERITY_INFO, MessageConst.INFO_770105, null);
		}
	}
	
	public void resetCheckAllTasks() {
		log.info("resetCheckAllTasks");
		if (getManagedBean().isCheckAllGroupTasksEnabled()) {
			getManagedBean().setCheckAllGroupTasksEnabled(false);
		}
		if (getManagedBean().isCheckAllGroupTasks()) {
			getManagedBean().setCheckAllGroupTasks(false);
		}
		if (getManagedBean().isCheckAllOthersTasksEnabled()) {
			getManagedBean().setCheckAllOthersTasksEnabled(false);
		}
		if (getManagedBean().isCheckAllOthersTasks()) {
			getManagedBean().setCheckAllOthersTasks(false);
		}
		log.info("isCheckAllGroupTasksEnabled= "+getManagedBean().isCheckAllGroupTasksEnabled());
		log.info("isCheckAllOthersTasksEnabled= "+getManagedBean().isCheckAllOthersTasksEnabled());		
		
		getManagedBean().setCheckAllUserTasks(false);			//[#25018]
		getManagedBean().setCheckAllUserTasksEnabled(false);	//[#25018]		
	}	
	
	//[#25018]
	private void setIsAdminUser() {
		log.info("setIsAdminUser - Start");		
		log.info("[#25018] User Id: " + getFacesScopes().getUserSessionBean().getUserId());
		//PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelByUserId(reqDTO);		
		log.info("[#25018] User role code: " + PmCommonUtil.getUserRoleValue());
		//getManagedBean().setAdmin(adminRolesForBulkReassignment.containsValue(PmCommonUtil.getUserRoleValue()));
		if(adminRolesForBulkReassignment.containsValue(PmCommonUtil.getUserRoleValue())) {
			getManagedBean().setAdmin(true);
		} else if(getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())) {
			getManagedBean().setAdmin(true);
		}
		log.info("[#25018] IsAdmin: " + getManagedBean().isAdmin());
		log.info("[#25018] setIsAdminUser - End");
	}
	
	//[#25018]					
	public void checkAllUserTasks() {
		log.info("checkAllUserTasks - Start");
		
		//log.info("[#25018] Total number of user task: "+ this.getManagedBean().getAllUserTasks().size());
		
		if (this.getManagedBean().getSelectedTasksToBulkReassign() != null && 
				this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
			this.getManagedBean().getSelectedTasksToBulkReassign().clear();
		}
		
		if (this.getManagedBean().getSelectedTasksToBulkReassign() == null) {
			this.getManagedBean().setSelectedTasksToBulkReassign(new ArrayList <WorklistItemVO>());
		}
		
    	for ( WorklistItemVO task : this.getManagedBean().getAllUserTasks()) {
    		if (this.getManagedBean().isCheckAllUserTasks()) {
    			task.setTaskSelect(true);
    			this.getManagedBean().getSelectedTasksToBulkReassign().add(task);
    		} else {
    			task.setTaskSelect(false);

    			if (this.getManagedBean().getSelectedTasksToBulkReassign() != null && 
    					this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
    				this.getManagedBean().getSelectedTasksToBulkReassign().clear();
    			}
    		}
		}
    	
    	if (this.getManagedBean().getSelectedTasksToBulkReassign() != null && 
				this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
    		this.getManagedBean().setShowParentMsg(false);
			this.getManagedBean().setShowChildMsg(true);
			boolean isValid = false;
	    	//validate selected task list - same doc type and same doc status
			//log.info("[#25018] - call validateDocTypeAndStatus");
			isValid = validateDocTypeAndStatus();
	    	
			if(isValid) {
				//validate selected task list - applicable for valid roles
				//log.info("[#25018] - call validateTaskForRole");
				isValid = validateTaskForRole();
			}
    	
    	} else {
    		this.getManagedBean().setShowParentMsg(true);
			this.getManagedBean().setShowChildMsg(false);    		
    	}
    	//log.info("[#25018] Total number of all task: "+ this.getManagedBean().getSelectedTasksToBulkReassign().size());
    	//log.info("[#25018] Total number of selected task: "+ this.getManagedBean().getSelectedTasksToBulkReassign().size());
    	log.info("checkAllUserTasks - End");
	}
	
	//[#25018]
	public void checkUserTask(AjaxBehaviorEvent event) {
		log.info("checkUserTask - Start");			
		
		if (this.getManagedBean().getSelectedTasksToBulkReassign() != null && 
				this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
			this.getManagedBean().getSelectedTasksToBulkReassign().clear();
		}
		
		if (this.getManagedBean().getSelectedTasksToBulkReassign() == null) {
			this.getManagedBean().setSelectedTasksToBulkReassign(new ArrayList <WorklistItemVO>());
		}
		//checkbox checked
		if ((boolean) event.getComponent().getAttributes().get("value")) {
			for (WorklistItemVO task : this.getManagedBean().getAllUserTasks()) {
		    	if (task.getTaskSelect()) {
		    		//log.info("[#25018] task: "+task.getTaskId());
		    		task.setTaskSelect(true);
		    		this.getManagedBean().getSelectedTasksToBulkReassign().add(task);		    		
		    	}
		    }
		} 
		//checkbox unchecked
		else {
			for (WorklistItemVO task : this.getManagedBean().getAllUserTasks()) {
				if (task.getTaskSelect()) {
		    		//log.info("[#25018] task: "+task.getTaskId());
					task.setTaskSelect(true);
		    		this.getManagedBean().getSelectedTasksToBulkReassign().add(task);
		    	} else {
		    		task.setTaskSelect(false);
		    	}
			}
		}
		
		if (this.getManagedBean().getSelectedTasksToBulkReassign() != null && 
				this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
			this.getManagedBean().setShowParentMsg(false);
			this.getManagedBean().setShowChildMsg(true);
			boolean isValid = false;
	    	if(this.getManagedBean().getSelectedTasksToBulkReassign().size() > 1) {
	    		//validate selected task list - same doc type and same doc status
	    		//log.info("[#25018] - call validateDocTypeAndStatus");
	    		isValid = validateDocTypeAndStatus();
	    	} else {
	    		isValid = true;
	    	}
	    	
	    	if(isValid) {
				//validate selected task list - applicable for valid roles
	    		//log.info("[#25018] - call validateTaskForRole");
				isValid = validateTaskForRole();
			}
	    	
    	} else {
    		this.getManagedBean().setShowParentMsg(true);
			this.getManagedBean().setShowChildMsg(false);    		
    	}
		
		if (this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0 &&
				(this.getManagedBean().getAllUserTasks().size() == this.getManagedBean().getSelectedTasksToBulkReassign().size())) {			
			this.getManagedBean().setCheckAllUserTasks(true);
			this.getManagedBean().setCheckAllUserTasksEnabled(true);
		} else {
			this.getManagedBean().setCheckAllUserTasks(false);
			this.getManagedBean().setCheckAllUserTasksEnabled(true);
		}
		//log.info("[#25018] Total number of selected task: "+ this.getManagedBean().getSelectedTasksToBulkReassign().size());
		log.info("checkUserTask - End");
	}
	
	//[#25018]
	public String getSelectedTasks() {		
		log.info("getSelectedTasks - Start");		
		WorklistItemVO task = null;
		StringBuilder documents = new StringBuilder();
		
		if(this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {			 
			for (int i = 0; i < this.getManagedBean().getSelectedTasksToBulkReassign().size(); i++) {
				task = this.getManagedBean().getSelectedTasksToBulkReassign().get(i);				
				documents.append(task.getDocNumber());
				documents.append(", ");				
			}
		}
		//log.info("[#25018] List of selected documents: " + documents.toString());
		log.info("getSelectedTasks - End");
		return documents.toString();
		
	}
	
	//[#25018]
	private void retrieveStatusIdsForInvalidBulkTask() {
		log.info("retrieveStatusIdsForInvalidBulkTask - Start");
		
		try {
			StatusReqDTO reqDTO = new StatusReqDTO();			
			//log.info("[#25018] invalidRolesForBulkReassignmentLst:[" + StringUtils.join(invalidRolesForBulkReassignmentLst,'|') + "]");					
			reqDTO = new StatusReqDTO();
			reqDTO.setRoleCodeList(invalidRolesForBulkReassignmentLst);				
			reqDTO.setReassignable(false);
			reqDTO.setReassignType(null);
			
			IStatusSO statusSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IStatusSO.class);
			//log.info("[#25018] calling retrieveStatusRoleListByRoleCode...");
			StatusResDTO resDTO = statusSO
					.retrieveStatusRoleListByRoleCode(reqDTO);
			if (resDTO != null && resDTO.isSuccessful()) {
				//statIdMap = new HashMap<Long,String>();
				for (PmStatusRole psr : resDTO.getStatusRoleList()) {
					excludeRolesForBulkReassignment.put(psr.getPmStatus().getStatusId(), psr.getPmRole().getRoleCode());
				}
				/*
				//sort list
				Map<Long, String> treeMap = new TreeMap<Long, String>(excludeRolesForBulkReassignment);
				//Traversing result Map  
			    Set set=treeMap.entrySet();//Converting to Set so that we can traverse  
			    Iterator itr=set.iterator();  
			    while(itr.hasNext()){  
			        //Converting to Map.Entry so that we can get key and value separately  
			        Map.Entry entry=(Map.Entry)itr.next();  
			        log.info("[#25018] Key:" +entry.getKey()+" Value:" +entry.getValue());  
			    }
			    */  
			}

		} catch (Exception e) {
			throw new EpException(e.getMessage());
		}
		log.info("retrieveStatusIdsForInvalidBulkTask - End");
	}
	
	//[#25018]
	private boolean validateTaskForRole() {
		log.info("validateTaskForRole - Start");
		
		StringBuilder stb = null;
		PmTaskReassignmentVO vo = null;
		ISearchBean<PmUser> searchBean = null;
		
		try {
			if(this.getManagedBean().getSelectedTasksToBulkReassign() != null 
					&& this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
				for(WorklistItemVO task : this.getManagedBean().getSelectedTasksToBulkReassign()) {
					log.info("[#25018] docType: "+ task.getDocType() +", docNo: "+ task.getDocNumber() + ", docStatus: " + task.getDocStatus().longValue() + ", taskName: " + task.getTaskName());
					PmStatus status = PmCommonUtil.getPmStatusById(task.getDocStatus().longValue());

					if (status != null) {
						if (ModuleCodeConst.CONTRACT_MANAGEMENT
								.equalsIgnoreCase(status.getModuleCode())) {
							vo = retrieveCtTaskReassignmentDetails(task,searchBean);
						} else if (ModuleCodeConst.SUPPLIER_SELECTION
								.equalsIgnoreCase(status.getModuleCode())) {
							vo = retrieveSourcingTaskReassignmentDetail(task);
						} else if (ModuleCodeConst.ORDER_FULFILLMENT
								.equalsIgnoreCase(status.getModuleCode())) {
							vo = retrieveFLTaskReassignmentDetail(task);
						}else if (ModuleCodeConst.PROCUREMENT_PLAN
								.equalsIgnoreCase(status.getModuleCode())) {
							vo = retrievePpTaskReassignmentDetail(task);
						}else if(ModuleCodeConst.CATALOGUE_MANAGEMENT
								.equalsIgnoreCase(status.getModuleCode())) {
							vo = retrieveCMTaskReassignmentDetail(task);
						}else if(ModuleCodeConst.PROFILE_MANAGEMENT
								.equalsIgnoreCase(status.getModuleCode())) {
							vo = retrievePMTaskReassignmentDetail(task);
						}else if(ModuleCodeConst.SUPPLIER_MANAGEMENT
								.equalsIgnoreCase(status.getModuleCode())) {
							vo = retrieveSMTaskReassignmentDetail(task);							
						}
					}					
					
					log.info("[#25018] Doc status id: " + task.getDocStatus().longValue());
					if(excludeRolesForBulkReassignment.containsKey(new Long(task.getDocStatus().longValue()))) {
						log.info("[#25018] Found match status id...");
						boolean invalid = false;
						if(vo != null && vo.getRoleList() != null && vo.getRoleList().size() > 0){
							for(String role : vo.getRoleList()) {
								log.info("[#25018] Role code: " + role);
								if(invalidRolesForBulkReassignmentLst.contains(role)) {
									log.info("[#25018] Has invalid role for bulk task reassingment");
									invalid = true;
									break;
								}
							}						
						}
						
						if(invalid) {
							if(stb != null) {
								stb.append(", ");
								stb.append(task.getDocNumber());
							} else {
								stb = new StringBuilder(task.getDocNumber());
							}
						}
					}
				}
				//log.info("[#25018] Invalid doc number: [" + (stb != null ? stb.toString():' ') + "]");
				if(stb != null && stb.length() > 0) {					
					this.getManagedBean().getSelectedTasksToBulkReassign().clear();					
					//throw new EpException(ResourceBundleUtils.getMessage("lbl.pm.ta.user.task.invalidrole", new String[] { stb.toString() }, ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));

					FacesContext fc = FacesContext.getCurrentInstance();
					fc.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									ResourceBundleUtils.getMessage("lbl.pm.ta.user.task.invalidrole", new String[] { stb.toString() }, ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale())));
					return false;
				
				} else {
					return true;
				}
			} else {
				return false;
			}			
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		}
		//log.info("validateTaskForRole - End");
	}
	
	//[#25018]
	/**
	 * Validate doc type and doc status 
	 * 
	 * @return void
	 */	
	public boolean validateDocTypeAndStatus() {
		log.info("validateDocTypeAndStatus - Start");
		
		String docType = null;
		long docStat = 0L;
		WorklistItemVO task = null;
		boolean isSameTypeAndStat = false;
		
		try {			
			if(this.getManagedBean().getSelectedTasksToBulkReassign() != null 
					&& this.getManagedBean().getSelectedTasksToBulkReassign().size() > 0) {
				//log.info("[#25018] Selected task(s) size: " + this.getManagedBean().getSelectedTasksToBulkReassign().size());
	
				//get document type & status from first task from bulk reassign list
				task = this.getManagedBean().getSelectedTasksToBulkReassign().get(0);
				docType = task.getDocType();
				docStat = task.getDocStatus().longValue();
				this.getManagedBean().setDocType(docType);
				this.getManagedBean().setDocStatus(new Long(docStat));
				//log.info("[#25018] First task from list - DocType:" + task.getDocType() + " || " + "DocStat:" +  task.getDocStatus().longValue() + " || " + "DocNo:" + task.getDocNumber() + " || " + "DocId:" + task.getDocId() + " || " + "TaskName:" + task.getTaskName());
				
				if(this.getManagedBean().getSelectedTasksToBulkReassign().size() > 1) {
					//check if next task from list have same doc type & status from first task
					for(int i = 1; i < this.getManagedBean().getSelectedTasksToBulkReassign().size(); i++) {
						task = this.getManagedBean().getSelectedTasksToBulkReassign().get(i);
						//log.info("[#25018] Next task from list - DocType:" + task.getDocType() + " || " + "DocStat:" +  task.getDocStatus().longValue() + " || " + "DocNo:" + task.getDocNumber() + " || " + "DocId:" + task.getDocId() + " || " + "TaskName:" + task.getTaskName());				
						if(docType.equals(task.getDocType()) && (docStat == task.getDocStatus().longValue())) {							
							isSameTypeAndStat = true;					
						} else {							
							isSameTypeAndStat = false;
							break;
						}
					}
				} else {
					isSameTypeAndStat = true;
				}
			}		
			
			if(isSameTypeAndStat) {				
				//set value for text area
				//this.getManagedBean().setSelectedTasksTA(getSelectedTasks());
				return true;
			} else {				
				this.getManagedBean().getSelectedTasksToBulkReassign().clear();				
				//throw new EpException(ResourceBundleUtils.getMessage("lbl.pm.ta.user.task.invalidtypeandstatus", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
				
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								ResourceBundleUtils.getMessage("lbl.pm.ta.user.task.invalidtypeandstatus", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale())));
				return false;
			}
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		}
		//log.info("validateDocTypeAndStatus - End");		
	}
	
	//[#22117]
	public boolean checkBpkAdminUser() {
		log.info("checkBpkAdminUser - Start");
		boolean isBpkAdmin = false;
		
		if (getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())) {			
			log.info("[#22117] - login user is BPK_ADMIN");
			log.info("[#22117] - login user login Id: " + getFacesScopes().getUserSessionBean().getLoginId());
			log.info("[#22117] - search user login Id: " + this.getManagedBean().getTaskAssignmentSearchBean().getUser().getLoginId());
			if(getFacesScopes().getUserSessionBean().getLoginId().equals(this.getManagedBean().getTaskAssignmentSearchBean().getUser().getLoginId())) {
				isBpkAdmin = true;
				//check if BPK administrator has other roles
				log.info("[#22117] - retrieve BPK_ADMIN role");
				List<PmUserOrg> userOrgList = PmCommonUtil.retrieveActiveOrg(getFacesScopes().getUserSessionBean().getUserId());
				log.info("[#22117] - BPK_ADMIN org size: " + userOrgList.size());
				if(userOrgList != null && userOrgList.size() > 0) {
					PmUserOrg userOrg = userOrgList.get(0);
					if(userOrg != null && userOrg.getPmUserRoles() != null && userOrg.getPmUserRoles().size() > 0) {
						for (PmUserRole userRole : userOrg.getPmUserRoles()) {
							log.info("[#22117] - BPK_ADMIN role: " + userRole.getPmRole().getRoleCode());
							if (DbValueConst.RECORD_STATUS_ACTIVE == userRole.getRecordStatusFlg() 
									&& !(userRole.getPmRole().getRoleCode().equals(RoleCode.BPK_ADMIN.value()))) {								
								log.info("[#22117] - BPK_ADMIN has other role!!");
								isBpkAdmin = true;							
								break;
							}						
						}
					}				
				}
			}
		}		
		return isBpkAdmin;			
	}
	
	public boolean checkBpkAdminSameUser() {
		log.info("checkBpkAdminSameUser - Start");
		boolean isBpkAdmin = false;
		
		if (getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())) {			
			if(getFacesScopes().getUserSessionBean().getLoginId().equals(this.getManagedBean().getSelectedReassignee().get(0))) {
				isBpkAdmin = true;
				log.info("retrieve BPK_ADMIN role----");
				List<PmUserOrg> userOrgList = PmCommonUtil.retrieveActiveOrg(getFacesScopes().getUserSessionBean().getUserId());
				log.info("BPK_ADMIN org size: ----" + userOrgList.size());
				if(userOrgList != null && userOrgList.size() > 0) {
					PmUserOrg userOrg = userOrgList.get(0);
					if(userOrg != null && userOrg.getPmUserRoles() != null && userOrg.getPmUserRoles().size() > 0) {
						for (PmUserRole userRole : userOrg.getPmUserRoles()) {
							log.info(" BPK_ADMIN role: --" + userRole.getPmRole().getRoleCode());
							if (DbValueConst.RECORD_STATUS_ACTIVE == userRole.getRecordStatusFlg() 
									&& !(userRole.getPmRole().getRoleCode().equals(RoleCode.BPK_ADMIN.value()))) {								
								log.info(" BPK_ADMIN has other role!!---");
								isBpkAdmin = true;							
								break;
							}						
						}
					}				
				}
			}
		}		
		return isBpkAdmin;			
	}
	
	//7465, 29822 - start
	/**
	 * To check task in progress remove or not
	 * 
	 */	
	public void checkPendingTaskRemove() {
		PmUser pmUser = this.getManagedBean().getTaskAssignmentSearchBean().getUser();
		ISupplierDataMaintenanceSO iSupplierDataMaintenanceSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierDataMaintenanceSO.class);
		List<PmPendingRemoveTask> hasActivePendingList = iSupplierDataMaintenanceSO.checkActivePendingTaskRemove(pmUser
				.getLoginId());
		
		if(hasActivePendingList != null){
			for(PmPendingRemoveTask hasActivePending : hasActivePendingList){
				if (hasActivePending != null) {
					if (hasActivePending.getParamTask().equalsIgnoreCase("GROUP")) {
						this.getManagedBean().setEnableRemoveAllGroupTasksButton(false);
						this.getManagedBean().setShowRemoveProcessMessageGroup(true);
					} else {
						this.getManagedBean().setEnableRemoveAllOthersTasksButton(false);
						this.getManagedBean().setShowRemoveProcessMessageOther(true);
					}
				}
			}
		}
		
		if(hasActivePendingList == null || hasActivePendingList.size() == 0){
			//no pending
			this.getManagedBean().setEnableRemoveAllGroupTasksButton(true);
			this.getManagedBean().setEnableRemoveAllOthersTasksButton(true);
			this.getManagedBean().setShowRemoveProcessMessageGroup(false);
			this.getManagedBean().setShowRemoveProcessMessageOther(false);
		}
	}
	/**
	 * To get user id
	 * 
	 */	
	public Long getUserByLoginId(){
		Long loginSessionUserId = 1L;
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
	
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setLoginId(getFacesScopes().getUserScreenName());
		PmUser pmUser = userSO.retrieveUserByLoginId(userReqDTO).getPmUser();
		if (pmUser == null) {
			loginSessionUserId = getFacesScopes().getUserSessionBean().getUserId();
		}
		else{
			loginSessionUserId = pmUser.getUserId();
		}
		
		new LogKey(loginSessionUserId);
		
		return loginSessionUserId;
	}
	/**
	 * To check task in progress remove or not based on DocNo searching only
	 * 
	 */
	public void checkPendingTaskRemoveByDocNo(String loginId) {
		ISupplierDataMaintenanceSO iSupplierDataMaintenanceSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierDataMaintenanceSO.class);
		List<PmPendingRemoveTask> hasActivePendingList = iSupplierDataMaintenanceSO.checkActivePendingTaskRemove(loginId);
		
		if(hasActivePendingList != null){
			for(PmPendingRemoveTask hasActivePending : hasActivePendingList){
				if (hasActivePending != null) {
					if (hasActivePending.getParamTask().equalsIgnoreCase("GROUP")) {
						this.getManagedBean().setEnableRemoveAllGroupTasksButton(false);
						this.getManagedBean().setShowRemoveProcessMessageGroup(true);
					} else {
						this.getManagedBean().setEnableRemoveAllOthersTasksButton(false);
						this.getManagedBean().setShowRemoveProcessMessageOther(true);
					}
				}
			}
		}
		
		if(hasActivePendingList == null || hasActivePendingList.size() == 0){
			//no pending
			this.getManagedBean().setEnableRemoveAllGroupTasksButton(true);
			this.getManagedBean().setEnableRemoveAllOthersTasksButton(true);
			this.getManagedBean().setShowRemoveProcessMessageGroup(false);
			this.getManagedBean().setShowRemoveProcessMessageOther(false);
		}
	}
	//7465, 29822 - end
	
	//Start Fix - #34699
		/**
		 * To generate user result bean based on reassignType
		 * 
		 * @param status
		 * @param searchBean
		 * @param task
		 * @param reassignType
		 * @param roleList
		 * @param vo
		 * @return ISearchResultBean<PmUser>
		 */
		private ISearchResultBean<PmUser> generateUserResultBeanForCT(
				 ISearchBean<PmUser> searchBean,WorklistItemVO task ,int reassignType,List<String> roleList,PmTaskReassignmentVO vo){
			PmOrgProfile orgProfile = null;
			ISearchResultBean<PmUser> resultBean = null;
			boolean isSearched = false;
			Long orgProfileId = getManagedBean()
							.getTaskAssignmentSearchBean().getOrgProfile()
							.getOrgProfileId();
			Long userId = getManagedBean().getTaskAssignmentSearchBean().getUser() != null ?
						  getManagedBean().getTaskAssignmentSearchBean().getUser().getUserId() : 
						  Long.valueOf(this.getManagedBean().getSelectedReassignee().get(0)).longValue();
			Long actualOrgProfileId = null;
			Long userGroupId = null;
			Long addressId = null;
			Integer subReassignType = null;
			
			log.info(":::generateUserResultBean() userId: "+ userId +", orgProfileId: "+ orgProfileId +", reassignType: "+ reassignType +".");
			
			// filter by user name
			if(StringUtils.isNotBlank(getManagedBean().getSearchUserName())){
				searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"userName",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getSearchUserName(),true));
			}
				//admin list to be filtered from task reassignment
				List<String> filteredAdminRoles = new ArrayList<String>();
				filteredAdminRoles.add("JABATAN_ADMIN");
				filteredAdminRoles.add("MINISTRY_ADMIN");
				filteredAdminRoles.add("PTJ_ADMIN");
				/* 27827 - START */
				filteredAdminRoles.add("BPK_ADMIN");
				/* 27827 - END */
				

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmUserOrgs.pmUserRoles.pmRole.roleCode",
								SearchOperator.OPERATOR_NOT_IN.value(), filteredAdminRoles));
				
			if(roleList.isEmpty()){
				roleList = vo.getRoleList();
			}	
			
			switch (reassignType) {
			case ProfileManagementConst.REASSIGN_TYPE_COMMON:
				//1 - Normal Reassignment
				
				//check because to allow cross org profile for market research #10927
				if(task.getDocStatus().longValue() != ProfileManagementStatusConst.PENDING_MARKET_RESEARCH_ACCESS_APPROVAL.longValue()) {
					
					// same org profile id
					searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserOrgs.pmOrgProfile.orgProfileId",
							SearchOperator.OPERATOR_EQUALS.value(),
							orgProfileId));
				}
				break;
			case ProfileManagementConst.REASSIGN_TYPE_S2:
				// 2: S2/CT Committee Task
				
				// not current selected user and not current login user
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("userId",
								SearchOperator.OPERATOR_NOT_IN.value(),
									new ArrayList<Long>(Arrays.asList(userId,
										getFacesScopes().getUserSessionBean()
											.getUserId()))));			
				// Order by user name
				searchBean.getSortColumns().add(new SearchOrderBean("userName"));
				
				//Bug #25021 Task Reassignment Sabah & Sarawak - Finalization Comm
				log.info(":::generateUserResultBean() userId: "+ userId +", vo.getDocId(): "+ vo.getDocId() 
						+", vo.getDocType(): "+ vo.getDocType() +", vo.getRoleList(): "+ vo.getRoleList() +".");	
				
				resultBean = generateCommitteAssigneesForCT(searchBean, vo.getDocId(), vo.getDocType(), roleList, orgProfileId);
				//Bug #25021
				
				getManagedBean().setCommitteeUpdate(true);		
						
				isSearched = true;
				break;
			case ProfileManagementConst.REASSIGN_TYPE_CROSS_PTJ:
				//3 - Cross PTJ
				if(vo.getOrgProfileId() != null){
					orgProfile	= PmCommonUtil.retrievePmOrgProfileDtl(vo.getOrgProfileId());
				}
				
				if (orgProfile != null) {
					if (OrganizationType.MINISTRY.value().equals(
							orgProfile.getOrgTypeCode().getParameterCode())) {
						// PTJ under same ministry
						searchBean
								.getDataFilterBeans()
								.add(new SearchFilterBean(
										"pmUserOrgs.pmOrgProfile." + OrgProfileUtil
												.getSearchBeanParentString(OrganizationType.MINISTRY
														.value()),
										SearchOperator.OPERATOR_EQUALS
												.value(), orgProfile
												.getOrgProfileId()));
					} else if (OrganizationType.DIVISION.value().equals(
							orgProfile.getOrgTypeCode().getParameterCode())) {
						// same org profile id
						searchBean
								.getDataFilterBeans()
								.add(new SearchFilterBean(
										"pmUserOrgs.pmOrgProfile.orgProfileId",
										SearchOperator.OPERATOR_EQUALS
												.value(), orgProfile
												.getOrgProfileId()));
					}
				} else {
					// not allow to search
					isSearched = true;
				}
				break;
			case ProfileManagementConst.REASSIGN_TYPE_USER_MIN_KPTJ_PTJ:
				//4 - Include User under Ministry, Kumpulan PTJ & PTJ
				actualOrgProfileId = vo.getOrgProfileId();
				if(actualOrgProfileId != null){
					if(roleList == null || roleList.isEmpty()){
						roleList = retrieveAvailableRoles(task.getDocStatus()
							.longValue(),reassignType,subReassignType);
					}
					resultBean = PmCommonUtil.retrieveUserListFromGroupOrgType(
							actualOrgProfileId,
							roleList,
							false,
							null,
							new ArrayList<Long>(Arrays.asList(userId,
									getFacesScopes().getUserSessionBean()
											.getUserId())),searchBean);
					
				}
				isSearched = true;
				
				break;
			case ProfileManagementConst.REASSIGN_TYPE_NAIK_TARAF_PTJ:
				//5 - Include User for Naik Taraf & PTJ
				
				actualOrgProfileId = vo.getOrgProfileId();
				if(actualOrgProfileId != null){
					if(roleList == null || roleList.isEmpty()){
						roleList = retrieveAvailableRoles(task.getDocStatus()
							.longValue(),reassignType,subReassignType);
					}
					
					resultBean = PmCommonUtil.retrieveUserListFromGroupOrgType(
							actualOrgProfileId,
							roleList,
							true,
							ParameterConst.ORG_AUTH_TYPE_ORG_GROUP,
							new ArrayList<Long>(Arrays.asList(userId,
									getFacesScopes().getUserSessionBean()
											.getUserId())),searchBean);
				}
				isSearched = true;
				break;
			case ProfileManagementConst.REASSIGN_TYPE_USER_GROUP:
				//6 - User Group
				BigDecimal approvalLimit = vo.getApprovalLimit();
				userGroupId = vo.getUserGroupId();
							
				if(userGroupId != null){
					resultBean = retrieveUserGroupUserListByUserGroupId(
							userGroupId,
							approvalLimit,
							new ArrayList<Long>(Arrays.asList(userId,
							getFacesScopes().getUserSessionBean()
							.getUserId())),searchBean);
				}
				isSearched = true;
				break;
			case ProfileManagementConst.REASSIGN_TYPE_DEL_ADDRESS:
				//7 - Delivery Address
				addressId = vo.getAddressId();
				
				if(addressId != null){
					// same address id
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									"pmAddressTypes.pmAddress.addressId",
									SearchOperator.OPERATOR_EQUALS
											.value(), addressId));
					
					searchBean
					.getDataFilterBeans()
					.add(new SearchFilterBean(
							"pmAddressTypes.recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS
									.value(), DbValueConst.RECORD_STATUS_ACTIVE));
					
					searchBean
					.getDataFilterBeans()
					.add(new SearchFilterBean(
							"pmAddressTypes.pmAddress.recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS
									.value(), DbValueConst.RECORD_STATUS_ACTIVE));
					
				}else{
					// not allow to search
					isSearched = true;
				}
				
				break;
			case ProfileManagementConst.REASSIGN_TYPE_USER_MIN_KPTJ_PTJ_NAIK_TARAF:
				//8 - Include user for Ministry, Kumpulan PTJ, PTJ & Naik Taraf
			if(vo.getOrgProfileId() != null){
				if(roleList == null || roleList.isEmpty()){
						roleList = retrieveAvailableRoles(task.getDocStatus()
							.longValue(),reassignType,subReassignType);
					}
					resultBean = PmCommonUtil.retrieveUserListFromGroupOrgTypeForSourcing(
							vo.getOrgProfileId(),
							roleList,						
							new ArrayList<Long>(Arrays.asList(userId,
									getFacesScopes().getUserSessionBean()
											.getUserId())),searchBean);
					
				}
				
				isSearched = true;
				break;
			case ProfileManagementConst.REASSIGN_TYPE_SUPPLIER:
				//9 GOVT_SELLER
				// same org profile id
				searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
								"pmUserOrgs.pmOrgProfile.orgProfileId",
									SearchOperator.OPERATOR_EQUALS.value(),
									orgProfileId));
				break;
			case ProfileManagementConst.REASSIGN_TYPE_SUPPLIER_OR_PTJ:
				//10 - Supplier(multiple roles) / PTJ(single)
			
				// if isSupplier then PmStatusRole.reassignType = 9 else PmStatusRole.reassignType = 1
					subReassignType = vo.isSupplier() ? 9 : 1;

				// same org profile id
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
								"pmUserOrgs.pmOrgProfile.orgProfileId",
								SearchOperator.OPERATOR_EQUALS.value(),
								orgProfileId));
					
				
				break;
			/* 27827 - START */
			case ProfileManagementConst.REASSIGN_TYPE_PTJ_AND_BPK:
				//11 - Include PTJ & BPK

				if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value()) ||
					getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value()) ||
					getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
					log.info("----- User PTJ, JABATAN OR MINISTRY -----");
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmUserOrgs.pmOrgProfile.orgProfileId",
							SearchOperator.OPERATOR_EQUALS.value(),
							orgProfileId));
					
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmUserOrgs.pmOrgProfile.bpk",
							SearchOperator.OPERATOR_EQUALS.value(),
							true));
					
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmUserOrgs.pmOrgProfile.pmZone.zoneId",
							SearchOperator.OPERATOR_EQUALS.value(),
							1L));
				} else if (getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())) {
					log.info("----- User BPK -----");
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmUserOrgs.pmOrgProfile.orgProfileId",
							SearchOperator.OPERATOR_EQUALS.value(),
							orgProfileId));
				} else {
					log.info("----- User EPADMIN, EPADMIN2 -----");
					List<String> paramcodes=new ArrayList<String>();
					paramcodes.add(ParameterConst.ORG_TYPE_PTJ);
					paramcodes.add(ParameterConst.ORG_TYPE_BPK);
					
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmUserOrgs.pmOrgProfile.orgTypeCode.parameterCode",
							SearchOperator.OPERATOR_IN.value(),
							paramcodes));
				}

				break;
			/* 27827 - END */
			default:
				// not allow to search
				isSearched = true;
			}
			
			if(!isSearched){
				// Filter By user status
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));
				
				// Filter By user org status
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmUserOrgs.recordStatusFlg",
									SearchOperator.OPERATOR_EQUALS.value(),
									DbValueConst.RECORD_STATUS_ACTIVE));		

				// Filter By role status
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmUserOrgs.pmUserRoles.recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));	

				// not current selected user and not current login user
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("userId",
								SearchOperator.OPERATOR_NOT_IN.value(),
								new ArrayList<Long>(Arrays.asList(userId,
										getFacesScopes().getUserSessionBean()
												.getUserId()))));

				if (roleList == null || roleList.isEmpty()) {
					roleList = retrieveAvailableRoles(task.getDocStatus()
							.longValue(),reassignType,subReassignType);
					//--10927
					if (getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())){
						for(Iterator<String> role=roleList.listIterator();
								role.hasNext();){
							String a=role.next();
							if (a.equals(RoleCode.EP_ADMIN_2.value())){
								role.remove();
								log.info("roleList1="+roleList.size());
							}
							if (a.equals(RoleCode.BPK_ADMIN.value())){
								role.remove();
								log.info("roleList2="+roleList.size());
							}
							
						}
					}
					
					if (getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())){
						for(Iterator<String> role=roleList.listIterator();
								role.hasNext();){
							String a=role.next();
							if (a.equals(RoleCode.BPK_ADMIN.value())){
								role.remove();
								log.info("roleList_SYS="+roleList.size());
							}
							
						}
					}
					
					//--10927
					
					/*//--10927
					if (getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())){
						roleList.remove(RoleCode.EP_ADMIN_2.value());
						System.out.println("Remove EpAdmin2::::::::::::::::::");
					}
					
					//--10927*/
				}

				if (!roleList.isEmpty()) {
					// in applicable to roles
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"pmUserOrgs.pmUserRoles.pmRole.roleCode",
									SearchOperator.OPERATOR_IN.value(), roleList));
				}
			
				
				// Order by user name
				searchBean.getSortColumns().add(new SearchOrderBean("userName"));
				
				resultBean = PmCommonUtil.searchUsersResultBeanBySearchBean(searchBean);
			}
			log.info("isSearched:: "+isSearched);
			return resultBean;
			
		}
		
		
		private ISearchResultBean<PmUser> generateCommitteAssigneesForCT(ISearchBean<PmUser> searchBean,
				Long docId, String docType, List<String> roleCodeList, Long orgProfileId){
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();
				committeReqDTO.setDocId(docId);
				committeReqDTO.setDocType(docType);
				committeReqDTO.setRoleCode(roleCodeList != null && !roleCodeList.isEmpty() ? roleCodeList.get(0) : "");
				committeReqDTO.setUserSearchBean(searchBean);
				//Bug #25021
				committeReqDTO.setOrgProfileId(orgProfileId);
				//end Bug #25021
				committeReqDTO.setSearchValue(getManagedBean().getSearchUserName()); //Fix - #34699
				return committeSO.retrieveUserListByCommTypeForTaskReassignment(committeReqDTO).getUserResultBean(); //Fix - #34699
			} finally {
				committeSO = null;
			}
		}
		
		//End Fix - #34699
		
	//#38680- Start
	/**
	 * Check Activity Name
	 * 
	 * @param taskName
	 * @return the indicator taskName.
	 */
	public static boolean isTaskName(String taskName) {
		if (taskName == null) {
			return false;
		}
		String t = taskName.trim();
		for (String notAllowed : NOT_ALLOWED_TASKS) {
			if (notAllowed.equalsIgnoreCase(t)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check Committee Type
	 * 
	 * @param committeeType
	 *            the committee type
	 * @return the indicator committee type.
	 */
	public static boolean isCommitteeType(String committeeType) {
	    if (committeeType == null){
	    	return false;
	    }
	    for (String t : COMMITTEE_TYPES) {
	        if (t.equalsIgnoreCase(committeeType)){
	        	return true;
	        }
	    }
	    return false;
	}
	
	
	/**
	 * remove task for role chairperson and member to do Task Reasgnment.
	 * 
	 * @param resultBean
	 * @param responseDTO
	 * 
	 */
	public void removeTaskForChairAndMemberExcludeStatus(
			SearchResultBean<WorklistItemVO> resultBean,
			BpmResponseDTO responseDTO) {
		List<WorklistItemVO> src = resultBean.getResultList();
		if (src != null && !src.isEmpty()) {
			List<WorklistItemVO> filtered = new java.util.ArrayList<>(
					src.size());
			for (WorklistItemVO w : src) {
				boolean exclude = false;
				Integer status = w.getDocStatus();
				if (excludeStatusIds.contains(status.longValue())
						&& isTaskName(w.getTaskName())) {
					exclude = true;
				}

				if (!exclude) {
					filtered.add(w);
				}
			}

			int removed = src.size() - filtered.size();
			resultBean.setResultList(filtered);

			int total = (responseDTO.getWorklistVO() == null) ? 0 : responseDTO
					.getWorklistVO().getTotal();
			resultBean.setTotalRecord(Math.max(0, total - removed));
		}
	}
	//#38680- End
}
