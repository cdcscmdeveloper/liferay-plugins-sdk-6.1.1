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
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.ct.IContractIntegrationSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.authorization.usergroup.IUserGroupSO;
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
import my.ep.dto.ct.ContractIntegrationReqDTO;
import my.ep.dto.ct.ContractIntegrationResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupReqDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupResDTO;
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
	private PagingDataModel<WorklistItemVO> taskListDataModel = new taskPagingDataModel();

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
		this.taskListDataModel.resetData();
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
			}
			PmParameter ptjParam = getParameterByTypeAndCode(
					ParameterTypeConst.ORGANISATION_TYPES,
					ParameterConst.ORG_TYPE_PTJ);
			this.getManagedBean().getTaskAssignmentSearchBean()
					.setOrgType(ptjParam);

		} else {
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
		this.taskListDataModel.reloadData();
		log.info("searchDocument - End");

	}

	private class taskPagingDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;

		public taskPagingDataModel() {
			super("dtTaskAssignResult");
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
			int taskListType = 0;

			try {
				taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
				loginId = taskSearchBean.getUser() != null ? taskSearchBean
						.getUser().getLoginId() : null;
				taskListType = taskSearchBean.getSelectedTaskListType();
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

						// User/Group/Others Task
						switch (taskListType) {
						case 0:
							reqDTO.setState(BpmConstants.STATE_ASSIGNED);
							reqDTO.setAssignment(BpmConstants.ASSIGNMENT_MY);
							responseDTO = bpmSO.getReassignedItems(reqDTO);
							break;
						case 1:
							reqDTO.setState(BpmConstants.STATE_ASSIGNED);
							reqDTO.setAssignment(BpmConstants.ASSIGNMENT_GROUP);
							reqDTO.setIsClaimed(false);
							responseDTO = bpmSO.getReassignedItems(reqDTO);
							break;
						case 2:
							reqDTO.setState(BpmConstants.STATE_ASSIGNED);
							reqDTO.setAssignment(BpmConstants.ASSIGNMENT_ALL);
							responseDTO = bpmSO.getGroupByOthersItems(reqDTO);
							break;

						default:
							break;
						}

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
			return desc.getStatusName() +" & ReassignType : " + desc.getPmStatus().getReasssignType();
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
			this.getManagedBean().setAvailableUsers(new ArrayList<PmUser>());
			this.getManagedBean().setSelectedUser(null);
			this.getManagedBean().setSelectedTask(item);
			this.getManagedBean().setAvailableUsers(
					generateAvailableUsers(item));
		} catch (Exception e) {
			e.printStackTrace();
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
	 * @return the taskListDataModel
	 */
	public PagingDataModel<WorklistItemVO> getTaskListDataModel() {
		return taskListDataModel;
	}

	/**
	 * @param taskListDataModel
	 *            the taskListDataModel to set
	 */
	public void setTaskListDataModel(
			PagingDataModel<WorklistItemVO> taskListDataModel) {
		this.taskListDataModel = taskListDataModel;
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
	 * retrieve and generate available users
	 * 
	 * @return List<PmUser>
	 */
	public List<PmUser> generateAvailableUsers(WorklistItemVO task) {
		log.info("generateAvailableUsers");
		boolean isApprover = false;
		List<PmUser> pmUsers = new ArrayList<PmUser>();
		try {
			PmStatus status = PmCommonUtil.getPmStatusById(task.getDocStatus()
					.longValue());
			if (status != null) {
				List<String> roleList = new ArrayList<String>();
				Long userId = this.getManagedBean()
						.getTaskAssignmentSearchBean().getUser().getUserId();
				Long orgProfileId = this.getManagedBean()
						.getTaskAssignmentSearchBean().getOrgProfile()
						.getOrgProfileId();
				Long orgTypeId = this.getManagedBean()
						.getTaskAssignmentSearchBean().getOrgType()
						.getParameterId();
				ISearchBean<PmUser> searchBean = new SearchBean<PmUser>();
				PmOrgProfile orgProfile = null;

				String moduleCode = status.getModuleCode();
				int reassignType = status.getReasssignType() != null ? status
						.getReasssignType() : 0;
				switch (reassignType) {
				//1 - Normal Reassignment
				case 1:
					if (ModuleCodeConst.CONTRACT_MANAGEMENT
							.equalsIgnoreCase(moduleCode)) {
						roleList = retrieveCtRoles(task);
					} else if (ModuleCodeConst.SUPPLIER_SELECTION
							.equalsIgnoreCase(moduleCode)) {
						roleList = retrieveSourcingRoles(task);
					}

					// same org profile id
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"pmUserOrgs.pmOrgProfile.orgProfileId",
									SearchOperator.OPERATOR_EQUALS.value(),
									orgProfileId));

					// same org type id
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									"pmUserOrgs.pmOrgProfile.orgTypeCode.parameterId",
									SearchOperator.OPERATOR_EQUALS.value(),
									orgTypeId));

					break;
				// case 2:
				//
				// break;
				case 3:
					//3 - Cross PTJ
					if (ModuleCodeConst.SUPPLIER_SELECTION
							.equalsIgnoreCase(moduleCode)) {
						orgProfile = retrieveSourcingOrgProfile(task);
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
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean(
										"pmUserOrgs.pmOrgProfile.orgProfileId",
										SearchOperator.OPERATOR_EQUALS.value(),
										0));
					}

					break;
				case 4:
					//4 - Include User under Ministry, Kumpulan PTJ & PTJ
					Long actualOrgProfileId = null;
					if (ModuleCodeConst.PROCUREMENT_PLAN
							.equalsIgnoreCase(moduleCode)) {
						actualOrgProfileId = retrievePpOrgProfile(task);
					}
					if(actualOrgProfileId != null){
						roleList = retrieveAvailableRoles(task.getDocStatus()
							.longValue(),reassignType);
						pmUsers = PmCommonUtil.retrieveUserListFromGroupOrgType(
								actualOrgProfileId,
								roleList,
								false,
								null,
								new ArrayList<Long>(Arrays.asList(userId,
										getFacesScopes().getUserSessionBean()
												.getUserId())),searchBean);
					}else{
						// not allow to search
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean(
										"pmUserOrgs.pmOrgProfile.orgProfileId",
										SearchOperator.OPERATOR_EQUALS.value(),
										0));
					}
					
					break;
				case 5:
					//5 - Include User for Naik Taraf & PTJ
					roleList = retrieveAvailableRoles(task.getDocStatus()
							.longValue(),reassignType);
					pmUsers = PmCommonUtil.retrieveUserListFromGroupOrgType(
							orgProfileId,
							roleList,
							true,
							ParameterConst.ORG_AUTH_TYPE_ORG_GROUP,
							new ArrayList<Long>(Arrays.asList(userId,
									getFacesScopes().getUserSessionBean()
											.getUserId())),searchBean);
					break;
				case 6:
					//6 - User Group
					Long userGroupId = null;
					if (ModuleCodeConst.SUPPLIER_SELECTION
							.equalsIgnoreCase(moduleCode)) {
						userGroupId = retrieveSourcingUserGroupId(task);
					}else if (ModuleCodeConst.ORDER_FULFILLMENT
							.equalsIgnoreCase(moduleCode)) {
						
					}
					
					if(userGroupId != null){
						pmUsers = retrieveUserGroupUserListByUserGroupId(userGroupId,null);
					}else{
						// not allow to search
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean(
										"pmUserOrgs.pmOrgProfile.orgProfileId",
										SearchOperator.OPERATOR_EQUALS.value(),
										0));
					}
					break;
				case 7:
					//7 - Delivery Address
					// not allow to search
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"pmUserOrgs.pmOrgProfile.orgProfileId",
									SearchOperator.OPERATOR_EQUALS.value(),
									0));
					break;

				default:
					throw new EpException(MessageConst.ERROR_770095,
							new String[] {"reassignType:"+ reassignType +" is not valid." });
				}

				if(pmUsers.isEmpty()){
					
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

					if (roleList.isEmpty()) {
						roleList = retrieveAvailableRoles(task.getDocStatus()
								.longValue(),reassignType);
					}

					// in applicable to roles
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"pmUserOrgs.pmUserRoles.pmRole.roleCode",
									SearchOperator.OPERATOR_IN.value(), roleList));

					pmUsers = PmCommonUtil.searchUsersBySearchBean(searchBean);
				}
				

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770095,
					new String[] { e.getMessage() });
		}
		return pmUsers;
	}

	private List<String> retrieveSourcingRoles(WorklistItemVO task) {
		log.info("retrieveSourcingRoles");
		List<String> roleList = new ArrayList<>();
		SourcingIntegrationResDTO resDTO = retrieveSourcingTaskReassignmentDetail(task);
		if (resDTO != null && StringUtils.isNotBlank(resDTO.getRoleCode())) {
			roleList.add(resDTO.getRoleCode());
		}
		return roleList;
	}

	private PmOrgProfile retrieveSourcingOrgProfile(WorklistItemVO task) {
		log.info("retrieveSourcingOrgProfile");
		PmOrgProfile orgProfile = null;
		SourcingIntegrationResDTO resDTO = retrieveSourcingTaskReassignmentDetail(task);
		if (resDTO != null && resDTO.getOrgProfileId() != null) {
			orgProfile = PmCommonUtil.retrievePmOrgProfileDtl(resDTO
					.getOrgProfileId());
		}
		return orgProfile;
	}
	
	private Long retrieveSourcingUserGroupId(WorklistItemVO task) {
		log.info("retrieveSourcingUserGroupId");
		Long userGroupId = null;
		SourcingIntegrationResDTO resDTO = retrieveSourcingTaskReassignmentDetail(task);
		if (resDTO != null && resDTO.getUserGroupId() != null) {
			userGroupId = resDTO.getUserGroupId();
		}
		return userGroupId;
	}

	private SourcingIntegrationResDTO retrieveSourcingTaskReassignmentDetail(
			WorklistItemVO task) {
		ISourcingIntegrationSO scIntegrationSO = null;
		SourcingIntegrationReqDTO reqDTO = null;
		SourcingIntegrationResDTO resDTO = null;
		try {
			reqDTO = new SourcingIntegrationReqDTO();
			scIntegrationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION,
					ISourcingIntegrationSO.class);
			reqDTO.setDocType(task.getDocType());
			reqDTO.setDocNo(task.getDocNumber());
			reqDTO.setStatusId(task.getDocStatus().longValue());
			reqDTO.setTaskName(task.getTaskName());
			resDTO = scIntegrationSO.retrieveTaskReassignmentDetail(reqDTO);

		} finally {
			scIntegrationSO = null;
		}
		return resDTO;
	}
	
	private Long retrievePpOrgProfile(WorklistItemVO task) {
		log.info("retrievePpOrgProfile");
		Long orgProfileId = null;
		ProcurementPlanResDTO resDTO = retrievePpTaskReassignmentDetail(task);
		if (resDTO != null && resDTO.getOrgProfileId() != null) {
			orgProfileId = resDTO.getOrgProfileId();
		}
		return orgProfileId;
	}
	
	private ProcurementPlanResDTO retrievePpTaskReassignmentDetail(
			WorklistItemVO task) {
		IProcurementPlanSO procurementSO= null;
		ProcurementPlanReqDTO reqDTO = null;
		ProcurementPlanResDTO resDTO = null;
		try {
			reqDTO = new ProcurementPlanReqDTO();
			procurementSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROCUREMENT_PLAN,
					IProcurementPlanSO.class);
			String docType = ParameterConst.DOCTYPE_PLAN_EXPENDITURE.equals(task.getDocType()) ? ParameterConst.PLAN_TYPE_EXPENDITURE: ParameterConst.PLAN_TYPE_PROCUREMENT;
			reqDTO.setDocType(docType);
			reqDTO.setDocNo(task.getDocNumber());
			reqDTO.setStatusId(task.getDocStatus().longValue());
			resDTO = procurementSO.retrieveTaskReassignmentDetail(reqDTO);

		} finally {
			procurementSO = null;
		}
		return resDTO;
	}

	private List<String> retrieveCtRoles(WorklistItemVO task) {
		log.info("retrieveCtRoles");
		List<String> roleList = new ArrayList<>();
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
				roleList = resDTO.getRoleCodeList();
			}
		} finally {
			ctIntegrationSO = null;
		}

		return roleList;
	}

	/**
	 * retrieve available roles by status id
	 * 
	 * @param statusId
	 * @return List<String>
	 */
	private List<String> retrieveAvailableRoles(Long statusId,Integer reassignType) {
		log.info("retrieveAvailableRoles");
		IStatusSO statusSO = null;
		List<String> statusRoles = new ArrayList<String>();
		try {
			StatusReqDTO reqDTO = new StatusReqDTO();
			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			reqDTO.setStatusId(statusId);
			reqDTO.setReassignType(reassignType);
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
	public void confirmReassignTask(String action) {
		log.info("confirmReassignTask");

		PmUser pmUser = this.getManagedBean().getSelectedUser();
		WorklistItemVO task = this.getManagedBean().getSelectedTask();
		ITaskAssignmentSO taskAssignmentSO = null;

		if (task == null) {
			throw new EpException(MessageConst.ERROR_770097);
		}
		List<String> assigneeUsers = task.getAssignees();
		if (!assigneeUsers.isEmpty()) {
			if (pmUser != null && assigneeUsers.contains(pmUser.getLoginId())) {
				throw new EpException("the task already exist under the user:"
						+ pmUser.getUserName());
			} else {
				// remove the current user.
				if (assigneeUsers.contains(getManagedBean()
						.getTaskAssignmentSearchBean().getUser().getLoginId())) {
					assigneeUsers.remove(getManagedBean()
							.getTaskAssignmentSearchBean().getUser()
							.getLoginId());
				}
			}
		}

		if (ProfileManagementConst.REASSIGN_ACTION_ASSIGN.equals(action)) {
			if (pmUser == null) {
				throw new EpException(MessageConst.ERROR_770096);
			}
			// replace with the new assignee.
			assigneeUsers.add(pmUser.getLoginId());
		}

		try {
			TaskAssignmentReqDTO reqDTO = new TaskAssignmentReqDTO();
			reqDTO.setTaskId(task.getTaskId());
			reqDTO.setUser(pmUser);
			reqDTO.setUserIdList(assigneeUsers);
			reqDTO.setDocNumber(task.getDocNumber());
			reqDTO.setDocId(task.getDocId());
			reqDTO.setDocType(task.getDocType());
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
				this.taskListDataModel.reloadData();
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
	
	private List<PmUser> retrieveUserGroupUserListByUserGroupId(Long userGroupId, BigDecimal approvalLimit) {
		log.info("retrieveUserGroupUserListByUserGroupId");
		List<PmUser> pmUserList = new ArrayList<PmUser>();
		IUserGroupSO userGroupSO = null;
		try {
			UserGroupReqDTO reqDTO = new UserGroupReqDTO();
			userGroupSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserGroupSO.class);
			reqDTO.setApprovalLimit(approvalLimit);
			reqDTO.setGroupId(userGroupId);
			UserGroupResDTO resDTO = userGroupSO.retrieveUserGroupUserListByGroupId(reqDTO);
			if (resDTO.isSuccessful() && !resDTO.getPmUserList().isEmpty()) {
				pmUserList = resDTO.getPmUserList();
			}
		} finally {
			userGroupSO = null;
		}

		return pmUserList;
	}
	
	

}