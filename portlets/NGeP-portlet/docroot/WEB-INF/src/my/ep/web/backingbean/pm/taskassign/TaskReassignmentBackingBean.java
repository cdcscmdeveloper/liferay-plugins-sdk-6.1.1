/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.taskassign;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.ct.IContractIntegrationSO;
import my.ep.biz.service.fl.IFulfilmentSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.taskassginment.ITaskAssignmentSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.BpmConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.ct.ContractIntegrationReqDTO;
import my.ep.dto.ct.ContractIntegrationResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
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
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmStatusRole;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
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
import my.ep.web.vo.PmTaskReassignmentVO;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "taskReassignmentBackingBean")
@ViewScoped
public class TaskReassignmentBackingBean extends
		BaseBackingBean<TaskReassignmentManagedBean> {
	private static Logger log = Logger
			.getLogger(TaskReassignmentBackingBean.class);

	// Navigation Details
	public static final String NAV_TO_SEARCH = "taskReassignment";

	// Mapper for DDL object
	private static Map<Long, PmParameter> orgTypeMapper;

	// Authorization
	private ISearchBean<PmParameterDesc> orgTypeSearchBean;

	// List for Paging
	private PagingDataModel<WorklistItemVO> userTaskListDataModel = new userTaskListDataModel();	
	private PagingDataModel<WorklistItemVO> groupTaskListDataModel = new groupTaskListDataModel();
	private PagingDataModel<WorklistItemVO> othersTaskListDataModel = new othersTaskListDataModel();
	private PagingDataModel<PmUser> userListDataModel = new userPagingDataModel();

	public void preLoading() throws EpException {
		log.info("preLoading");
		resetTaskAssignSearchBean();
		retrieveOrgTypeMap();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_SEARCH);
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
	}
	
	private void resetAllTaskList(){
		this.userTaskListDataModel.resetData();
		this.groupTaskListDataModel.resetData();
		this.othersTaskListDataModel.resetData();
	}
	
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
		}
	}

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
	}

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
					tempRoleCodeList.add(userRole.getPmRole().getRoleCode());
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
	public void searchTaskAssign() {
		log.info("searchDocument - Start");
		if (!(this.getManagedBean().getTaskAssignmentSearchBean()
				.getOrgProfile() != null && this.getManagedBean()
				.getTaskAssignmentSearchBean().getOrgProfile()
				.getOrgProfileId() != null)) {
			throw new EpException(MessageConst.ERROR_770091);
		}
		if (!(this.getManagedBean().getTaskAssignmentSearchBean().getUser() != null
				&& this.getManagedBean().getTaskAssignmentSearchBean()
						.getUser().getUserId() != null && this.getManagedBean()
				.getTaskAssignmentSearchBean().getUser().getLoginId().length() > 0)) {
			throw new EpException(MessageConst.ERROR_770092);
		}
		this.getManagedBean().setResultUser(
				this.getManagedBean().getTaskAssignmentSearchBean().getUser());
		this.getManagedBean().setResultOrgProfile(
				this.getManagedBean().getTaskAssignmentSearchBean()
						.getOrgProfile());
		this.getManagedBean().getTaskAssignmentSearchBean().setSearched(true);
		this.getManagedBean().setTaskLazy(true);
		reloadAllTaskList();
		log.info("searchDocument - End");

	}

	private class userTaskListDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;

		public userTaskListDataModel() {
			super("dtTaskAssignResult",getManagedBean().isTaskLazy());
		}

		private ISearchResultBean<WorklistItemVO> getWorklistItems() {
			log.info("getWorklistItems");
			IBpmSO bpmSO = null;
			BpmRequestDTO reqDTO = null;
			BpmResponseDTO responseDTO = null;
			String loginId = null;
			List<Integer> statusIds = null;
			TaskAssignmentSearchBean taskSearchBean = null;
			SearchResultBean<WorklistItemVO> resultBean = null;

			try {
				taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
				loginId = taskSearchBean.getUser() != null ? taskSearchBean
						.getUser().getLoginId() : null;
				if (loginId != null && loginId.length() > 0) {
					statusIds = retrieveAvailableStatusId();
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

//						// User/Group/Others Task
//						switch (taskListType) {
//						case 0:
//							reqDTO.setState(BpmConstants.STATE_ASSIGNED);
//							reqDTO.setAssignment(BpmConstants.ASSIGNMENT_MY);
//							responseDTO = bpmSO.getReassignedItems(reqDTO);
//							break;
//						case 1:
//							reqDTO.setState(BpmConstants.STATE_ASSIGNED);
//							reqDTO.setAssignment(BpmConstants.ASSIGNMENT_GROUP);
//							reqDTO.setIsClaimed(false);
//							responseDTO = bpmSO.getReassignedItems(reqDTO);
//							break;
//						case 2:
//							reqDTO.setState(BpmConstants.STATE_ASSIGNED);
//							reqDTO.setAssignment(BpmConstants.ASSIGNMENT_ALL);
//							responseDTO = bpmSO.getGroupByOthersItems(reqDTO);
//							break;
//
//						default:
//							break;
//						}

						// Invoke BPM
						// responseDTO = bpmSO.getReassignedItems(reqDTO);

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
	
	private class groupTaskListDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;

		public groupTaskListDataModel() {
			super("dtGroupTaskAssignResult",getManagedBean().isTaskLazy());
		}

		private ISearchResultBean<WorklistItemVO> getWorklistItems() {
			log.info("groupTaskListDataModel - getWorklistItems");
			IBpmSO bpmSO = null;
			BpmRequestDTO reqDTO = null;
			BpmResponseDTO responseDTO = null;
			String loginId = null;
			List<Integer> statusIds = null;
			TaskAssignmentSearchBean taskSearchBean = null;
			SearchResultBean<WorklistItemVO> resultBean = null;

			try {
				taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
				loginId = taskSearchBean.getUser() != null ? taskSearchBean
						.getUser().getLoginId() : null;
				if (loginId != null && loginId.length() > 0) {
					statusIds = retrieveAvailableStatusId();
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
	
	private class othersTaskListDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;

		public othersTaskListDataModel() {
			super("dtOthersTaskAssignResult",getManagedBean().isTaskLazy());
		}

		private ISearchResultBean<WorklistItemVO> getWorklistItems() {
			log.info("othersTaskListDataModel - getWorklistItems");
			IBpmSO bpmSO = null;
			BpmRequestDTO reqDTO = null;
			BpmResponseDTO responseDTO = null;
			String loginId = null;
			List<Integer> statusIds = null;
			TaskAssignmentSearchBean taskSearchBean = null;
			SearchResultBean<WorklistItemVO> resultBean = null;

			try {
				taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
				loginId = taskSearchBean.getUser() != null ? taskSearchBean
						.getUser().getLoginId() : null;
				if (loginId != null && loginId.length() > 0) {
					statusIds = retrieveAvailableStatusId();
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
	 * retrieve status id list for BPM
	 * 
	 * @return
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
//			this.getManagedBean().setAvailableUsers(
//					generateAvailableUsers(item));
		} catch (Exception e) {
			throw e;
		}
		log.info("redirectToTaskReassign - End");
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
//				vo.setGroupDocId(resDTO.getGroupDocId());
//				vo.setGroupDocType(resDTO.getGroupDocType());
				vo.setOrgProfileId(resDTO.getOrgProfileId());
				vo.setRoleList(new ArrayList<String>(Arrays.asList(resDTO.getRoleCode())));
				vo.setUserGroupId(resDTO.getUserGroupId());
				
			}
		}catch(Exception e){
			throw new EpException("unable to get task reassignment details from Sourcing Module.");
		} finally {
			scIntegrationSO = null;
		}
		return vo;
	}

	
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
			}
		}catch(Exception e){
			throw new EpException("unable to get task reassignment details from Procurement Plan Module.");
		} finally {
			procurementSO = null;
		}
		return vo;
	}
	
	private PmTaskReassignmentVO retrieveCtTaskReassignmentDetails(WorklistItemVO task) {
		log.info("retrieveCtTaskReassignmentDetails");
		PmTaskReassignmentVO vo = new PmTaskReassignmentVO();
		IContractIntegrationSO ctIntegrationSO = null;
		try {
			ContractIntegrationReqDTO reqDTO = new ContractIntegrationReqDTO();
			ctIntegrationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT,
					IContractIntegrationSO.class);
			reqDTO.setDocNo(task.getDocNumber());
			reqDTO.setDocType(task.getDocType());
			ContractIntegrationResDTO resDTO = ctIntegrationSO
					.retrieveTaskAssignmentRole(reqDTO);
			if (resDTO.isSuccessful() && !resDTO.getRoleCodeList().isEmpty()) {
				vo.setRoleList(resDTO.getRoleCodeList());
				vo.setGroupDocId(resDTO.getGroupDocId());
				vo.setGroupDocType(resDTO.getGroupDocType());
				vo.setDocId(resDTO.getDocId());
				vo.setDocNo(resDTO.getDocNo());
				vo.setDocType(resDTO.getDocType());
			}
		}catch(Exception e){
			throw new EpException("unable to get task reassignment details from Contract Management Module.");
		} finally {
			ctIntegrationSO = null;
		}
		return vo;
	}

		
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
			}
			
			FulfilmentResDTO resDTO = fulfilmentSO.retrieveTaskReassignmentDetail(reqDTO);
			if(resDTO != null){
				vo.setOrderAmount(resDTO.getOrderAmount());
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
		return statusRoles;
	}

	/**
	 * Reassign task to selected user
	 */
	public void confirmReassignTask() {
		log.info("confirmReassignTask");

		PmUser pmUser = this.getManagedBean().getSelectedUser();
		WorklistItemVO task = this.getManagedBean().getSelectedTask();
		ITaskAssignmentSO taskAssignmentSO = null;
		PmTaskReassignmentVO vo = getManagedBean().getTaskReassignmentVO();
		if (task == null) {
			throw new EpException(MessageConst.ERROR_770097);
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
		// replace with the new assignee.
		assigneeUsers.add(pmUser.getLoginId());
		

		try {
			TaskAssignmentReqDTO reqDTO = new TaskAssignmentReqDTO();
			reqDTO.setTaskId(task.getTaskId());
			reqDTO.setUser(pmUser);
			reqDTO.setUserIdList(assigneeUsers);
			reqDTO.setDocNumber(StringUtils.isNotBlank(vo.getDocNo())? vo.getDocNo() : task.getDocNumber());
			reqDTO.setDocId(vo.getDocId() != null ? vo.getDocId().intValue() : task.getDocId());		
			reqDTO.setDocType(vo.getDocType() != null ? vo.getDocType() : task.getDocType());
			reqDTO.setGroupDocId(vo.getGroupDocId() != null ? vo.getGroupDocId() : null);
			reqDTO.setGroupDocType(StringUtils.isNotBlank(vo.getGroupDocType()) ? vo.getGroupDocType() : null);
			reqDTO.setDocStatus(task.getDocStatus());
			reqDTO.setRoleCode(PmCommonUtil.getUserRoleValue());
			reqDTO.setActionedByUser(getFacesScopes().getUserSessionBean()
					.getUserId());
			reqDTO.setFromUser(getManagedBean().getTaskAssignmentSearchBean()
					.getUser());

			taskAssignmentSO = (ITaskAssignmentSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							ITaskAssignmentSO.class);
			TaskAssignmentResDTO resDTO = taskAssignmentSO.reassignTask(reqDTO);

			// isSuccess = reassignTask(pmUser, task);
			if (resDTO != null && resDTO.isSuccessful()) {
				reloadAllTaskList();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770104,
										new String[] { task.getDocNumber() },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
		} finally {
			taskAssignmentSO = null;
		}

	}
	
	private ISearchResultBean<PmUser> retrieveUserGroupUserListByUserGroupId(Long userGroupId, BigDecimal approvalLimit, List<Long> excludeUserIds) {
		log.info("retrieveUserGroupUserListByUserGroupId");
		IUserSO userSO = null;
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserSO.class);
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

	private class userPagingDataModel extends PagingDataModel<PmUser> {

		private static final long serialVersionUID = 1L;

		public userPagingDataModel() {
			super("dtTaskReassign");
		}

		@Override
		protected ISearchResultBean<PmUser> pageChangeListener(
				ISearchBean<PmUser> searchBean) {
			log.info("generateAvailableUsers");
			try {
				WorklistItemVO task = getManagedBean().getSelectedTask();
				PmStatus status = PmCommonUtil.getPmStatusById(task.getDocStatus()
						.longValue());
				if (status != null) {
					ISearchResultBean<PmUser> resultBean = null;
					PmTaskReassignmentVO vo = null;
					List<String> roleList = new ArrayList<String>();
					int reassignType = status.getReasssignType() != null ? status
							.getReasssignType() : 0;
							
					String moduleCode = status.getModuleCode();
					if (ModuleCodeConst.CONTRACT_MANAGEMENT
							.equalsIgnoreCase(moduleCode)) {
						vo = retrieveCtTaskReassignmentDetails(task);
					} else if (ModuleCodeConst.SUPPLIER_SELECTION
							.equalsIgnoreCase(moduleCode)) {
						vo = retrieveSourcingTaskReassignmentDetail(task);
					} else if (ModuleCodeConst.ORDER_FULFILLMENT
							.equalsIgnoreCase(moduleCode)) {
						vo = retrieveFLTaskReassignmentDetail(task);
					}else if (ModuleCodeConst.PROCUREMENT_PLAN
							.equalsIgnoreCase(moduleCode)) {
						vo = retrievePpTaskReassignmentDetail(task);
					}else if(ModuleCodeConst.CONTRACT_MANAGEMENT
							.equalsIgnoreCase(moduleCode)) {
						vo = retrieveCMTaskReassignmentDetail(task);
					}else if(ModuleCodeConst.SUPPLIER_MANAGEMENT
							.equalsIgnoreCase(moduleCode)) {
						vo = new PmTaskReassignmentVO();
					}
					
					if(vo != null){
						getManagedBean().setTaskReassignmentVO(vo);
						// 99 - MIXTURE(Sub Reassign Type)
						if(reassignType == ProfileManagementConst.REASSIGN_TYPE_MIXTURE){
							roleList = vo.getRoleList();
							if(!roleList.isEmpty()){
								PmStatusRole statusRole = PmCommonUtil.retrieveStatusRoleByStatusIdAndRoleCode(task.getDocStatus().longValue(), roleList.get(0));
								if(statusRole != null && statusRole.getReasssignType() !=null){
									reassignType = statusRole.getReasssignType();
									resultBean = generateUserResultBean(status , searchBean, task,reassignType,roleList,vo);
								}
							}
						}else{
							resultBean = generateUserResultBean(status, searchBean, task,reassignType,roleList,vo);
						}
						
						if(resultBean != null && resultBean.getResultList() != null){
							String capitalizeUserName;
							for(PmUser user : resultBean.getResultList()){
								capitalizeUserName = WordUtils.capitalizeFully(user.getUserName());
								user.setUserName(capitalizeUserName);
							}
						}
						return resultBean;
					}				
					
				}
			
		}catch(Exception e){
			throw e;
		}
			return null;
		}
	}
	
	public void searchAvailableUser(){
		userListDataModel.reloadData();
	}
	
	public void resetAvailableUser(){
		getManagedBean().setSearchUserName(null);
		getManagedBean().setTaskReassignmentVO(null);
		userListDataModel.resetData();
	}
	
	public void onChangeDocType(BaseEntity entity){
		this.getManagedBean().getTaskAssignmentSearchBean()
			.setSearched(false);
		this.getManagedBean().setTaskLazy(false);
		resetAllTaskList();
	}
	
	private ISearchResultBean<PmUser> generateUserResultBean(
			PmStatus status , ISearchBean<PmUser> searchBean,WorklistItemVO task ,int reassignType,List<String> roleList,PmTaskReassignmentVO vo){
		PmOrgProfile orgProfile = null;
		ISearchResultBean<PmUser> resultBean = null;
		boolean isSearched = false;
		Long orgProfileId = getManagedBean()
						.getTaskAssignmentSearchBean().getOrgProfile()
						.getOrgProfileId();
		Long userId = getManagedBean()
				.getTaskAssignmentSearchBean().getUser().getUserId();
		Long actualOrgProfileId = null;
		Long userGroupId = null;
		Long addressId = null;
		Integer subReassignType = null;
		
		// filter by user name
		if(StringUtils.isNotBlank(getManagedBean().getSearchUserName())){
			searchBean.getDataFilterBeans().add(
				new SearchFilterBean(
						"userName",
						SearchOperator.OPERATOR_LIKE.value(),
						getManagedBean().getSearchUserName(),true));
		}
		if(roleList.isEmpty()){
			roleList = vo.getRoleList();
		}	
		
		switch (reassignType) {
		case ProfileManagementConst.REASSIGN_TYPE_COMMON:
			//1 - Normal Reassignment

			// same org profile id
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserOrgs.pmOrgProfile.orgProfileId",
							SearchOperator.OPERATOR_EQUALS.value(),
							orgProfileId));
			break;
		// case 2:
		//
		// break;
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
									OrgProfileUtil
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
			if(roleList == null || roleList.isEmpty()){
				roleList = retrieveAvailableRoles(task.getDocStatus()
					.longValue(),reassignType,subReassignType);
			}
			
			resultBean = PmCommonUtil.retrieveUserListFromGroupOrgType(
					orgProfileId,
					roleList,
					true,
					ParameterConst.ORG_AUTH_TYPE_ORG_GROUP,
					new ArrayList<Long>(Arrays.asList(userId,
							getFacesScopes().getUserSessionBean()
									.getUserId())),searchBean);
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
						.getUserId())));
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
				
			}else{
				// not allow to search
				isSearched = true;
			}
			
			break;
		case ProfileManagementConst.REASSIGN_TYPE_USER_MIN_KPTJ_PTJ_NAIK_TARAF:
			//8 - Include user for Ministry, Kumpulan PTJ, PTJ & Naik Taraf
			if(vo.getOrgProfileId() != null){
				orgProfile	= PmCommonUtil.retrievePmOrgProfileDtl(vo.getOrgProfileId());
			}
			if(orgProfile != null){
				if(roleList == null || roleList.isEmpty()){
					roleList = retrieveAvailableRoles(task.getDocStatus()
						.longValue(),reassignType,subReassignType);
				}
				resultBean = PmCommonUtil.retrieveUserListFromGroupOrgType(
						orgProfile.getOrgProfileId(),
						roleList,
						true,
						null,
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
		if (!assigneeUsers.isEmpty()) {
			// remove the current user.
			if (assigneeUsers.contains(getManagedBean()
					.getTaskAssignmentSearchBean().getUser().getLoginId())) {
				assigneeUsers.remove(getManagedBean()
							.getTaskAssignmentSearchBean().getUser()
							.getLoginId());
			
			}
			if(assigneeUsers != null && assigneeUsers.size() > 1 && ProfileManagementConst.REASSIGN_WORKLIST_TYPE_OTHERS == reassignWorkListType){
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
			TaskAssignmentResDTO resDTO = taskAssignmentSO.removeGroupOrOthersTask(reqDTO);

			// isSuccess = reassignTask(pmUser, task);
			if (resDTO != null && resDTO.isSuccessful()) {
				reloadAllTaskList();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770105,
										null,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
		} finally {
			taskAssignmentSO = null;
		}

	}

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
	
	

}