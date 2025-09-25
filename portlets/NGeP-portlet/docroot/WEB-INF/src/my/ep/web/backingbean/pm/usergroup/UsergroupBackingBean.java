package my.ep.web.backingbean.pm.usergroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.fl.IFulfilmentSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.role.IRoleSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.authorization.usergroup.IUserGroupSO;
import my.ep.biz.service.pm.common.audit.IAuditPMSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.role.RoleReqDTO;
import my.ep.dto.pm.authorization.role.RoleResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupReqDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupResDTO;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.SourcingIntegrationResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.mapbeans.AuditPMUserGroupBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.UserGroupSearchBean;
import my.ep.web.managedbean.pm.usergroup.UsergroupManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "usergroupBackingBean")
@ViewScoped
public class UsergroupBackingBean extends BaseBackingBean<UsergroupManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = EpLogger.getLogger(UsergroupBackingBean.class);

	private String NAV_TO_MAIN = "userGroupSearchMain";
	public static final String NAV_TO_MAINTAIN = "/xhtml/pm/usergroupsearch/userGroupMaintenance.xhtml"; //#34445

	private IPagingDataModel<PmUserGroup> userGroupList = new UserGroupProfilePagingDataModel();

	private Long orgProfileId = null;

	public UsergroupBackingBean() {
	}

	/**
	 * reset search group detail
	 */
	public void reset() {
		logger.info("inside reset");
		this.getManagedBean().setUsergroupSearchBean(new UserGroupSearchBean());
		this.getManagedBean().getUsergroupSearchBean().setStatus(null);
		this.getManagedBean().setLazy(false);
		userGroupList = new UserGroupProfilePagingDataModel();
	}

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}

	/**
	 * get user group status record list
	 * 
	 */
	public SelectItem[] getUserGroupStatus() {

		return PmCommonUtil.getCommonStatusOptions();
	}
	
	//#27629 start
	/**
	 * get search user group status record list
	 * 
	 */
	public SelectItem[] getSearchUserGroupStatus() {

		SelectItem[] items = new SelectItem[3];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.all", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[1] = new SelectItem(DbValueConst.RECORD_STATUS_ACTIVE,
				ResourceBundleUtils.getMessage("lbl.pm.active",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items[2] = new SelectItem(DbValueConst.RECORD_STATUS_SUSPENDED,
				ResourceBundleUtils.getMessage("lbl.pm.inactive",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		return items;
	}
	//#27629 end

	/**
	 * pagination for user search list
	 */
	private class UserGroupProfilePagingDataModel extends PagingDataModel<PmUserGroup> {
		private static final long serialVersionUID = 1L;

		public UserGroupProfilePagingDataModel() {
			super("searchGroupProfile", getManagedBean().isLazy());
		}

		@Override
		protected ISearchResultBean<PmUserGroup> pageChangeListener(ISearchBean<PmUserGroup> searchBean) {
			populateSearchBean(searchBean);
			UserGroupReqDTO reqDTO = new UserGroupReqDTO();
			reqDTO.setUserGroupSearchBean(searchBean);
			IUserGroupSO groupSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserGroupSO.class);
			UserGroupResDTO resDTO = groupSO.retrieveUserGroups(reqDTO);
			ISearchResultBean<PmUserGroup> iSearchResultBean = resDTO.getUserGroupResultBean();
			return iSearchResultBean;

		}

		/**
		 * @param userGrpBean
		 * @param searchBean
		 */
		private void populateSearchBean(ISearchBean<PmUserGroup> searchBean) {
			SearchFilterBean codesSearchBean = null;
			if (StringUtils.isNotBlank(getManagedBean().getUsergroupSearchBean().getUserName())) {
				codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean.setSearchField("pmUserGroupUsers.pmUser.userName");
				codesSearchBean.setSearchValue(getManagedBean().getUsergroupSearchBean().getUserName());
				codesSearchBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}

			if (StringUtils.isNotBlank(getManagedBean().getUsergroupSearchBean().getRole())) {
				codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
				codesSearchBean.setSearchField("roleCode");
				codesSearchBean.setSearchValue(getManagedBean().getUsergroupSearchBean().getRole());
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}

			if (StringUtils.isNotBlank(getManagedBean().getUsergroupSearchBean().getGroupCode())) {
				codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean.setSearchField("groupCode");
				codesSearchBean.setSearchValue(getManagedBean().getUsergroupSearchBean().getGroupCode());
				codesSearchBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}

			if (StringUtils.isNotBlank(getManagedBean().getUsergroupSearchBean().getGroupName())) {
				codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean.setSearchField("groupName");
				codesSearchBean.setSearchValue(getManagedBean().getUsergroupSearchBean().getGroupName());
				codesSearchBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}

			if (StringUtils.isNotBlank(getManagedBean().getUsergroupSearchBean().getPtjCode())) {
				codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean.setSearchField("pmOrgProfile.pmOrgValidity.orgCode");
				codesSearchBean.setSearchValue(getManagedBean().getUsergroupSearchBean().getPtjCode());
				codesSearchBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}

			if (StringUtils.isNotBlank(getManagedBean().getUsergroupSearchBean().getPtjName())) {
				codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean.setSearchField("pmOrgProfile.pmOrgValidity.orgName");
				codesSearchBean.setCaseInsensitive(true);
				codesSearchBean.setSearchValue(getManagedBean().getUsergroupSearchBean().getPtjName());
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}

			if (getManagedBean().getUsergroupSearchBean().getStatus() != null) {
				codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
				codesSearchBean.setSearchField("recordStatusFlg");
				codesSearchBean.setSearchValue(getManagedBean().getUsergroupSearchBean().getStatus());
				searchBean.getDataFilterBeans().add(codesSearchBean);
			} else {
				codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
				codesSearchBean.setSearchField("recordStatusFlg");
				codesSearchBean.setSearchValue(new ArrayList<String>(
						Arrays.asList(ParameterConst.RECORD_STATUS_INACTIVE, ParameterConst.RECORD_STATUS_ACTIVE)));
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}
			List<PmUserOrg> userOrgList = PmCommonUtil
					.retrieveActiveOrg(getFacesScopes().getUserSessionBean().getUserId());
			PmOrgProfile orgProfile = null;
			if (userOrgList != null && !userOrgList.isEmpty()) {
				orgProfile = userOrgList.get(0).getPmOrgProfile();
			}
			if (orgProfile != null) {
				if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean(
							"pmOrgProfile."
									+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
							SearchOperator.OPERATOR_EQUALS.value(), orgProfile.getOrgProfileId()));

				} else if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean(
							"pmOrgProfile."
									+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()),
							SearchOperator.OPERATOR_EQUALS.value(), orgProfile.getOrgProfileId()));

				} else if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("pmOrgProfile.orgProfileId",
							SearchOperator.OPERATOR_EQUALS.value(), orgProfile.getOrgProfileId()));
				}
			}

			// if (null!=new FacesScopes().getUserSessionBean()) {
			//
			// UserReqDTO reqDTO = new UserReqDTO();
			// IUserSO reqSO = (IUserSO) BusinessDelegate.getInstance()
			// .getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
			// IUserSO.class);
			// PmUser pmUser = new FacesScopes().getUserSessionBean();
			//
			// UserReqDTO reqDTO1 = new UserReqDTO();
			// reqDTO1.setLoginId(pmUser.getLoginId());
			// UserResDTO resDTO = reqSO.retrieveOrgprofilebyUserId(reqDTO1);
			// if(resDTO.isSuccessful()){
			// SearchFilterBean orgProfileSearchBean = new SearchFilterBean(
			// SearchOperator.OPERATOR_EQUALS.value());
			// orgProfileSearchBean.setSearchField("pmOrgProfile.orgProfileId");
			// orgProfileSearchBean.setSearchValue(resDTO.getPmOrgProfile().getOrgProfileId().toString());
			// orgProfileSearchBean.setCaseInsensitive(true);
			// searchBean.getDataFilterBeans().add(orgProfileSearchBean);
			// }
			//
			//// }
			// codesSearchBean = new SearchFilterBean(
			// SearchOperator.OPERATOR_EQUALS.value());
			// codesSearchBean.setSearchField("roleCode");
			// codesSearchBean.setSearchValue("FL_APPROVER");
			// codesSearchBean.setCaseInsensitive(true);
			// searchBean.getDataFilterBeans().add(codesSearchBean);
		}
	}

	public IPagingDataModel<PmUserGroup> getUserGroupList() {
		return userGroupList;
	}

	/**
	 * reload to user group creation page xhtml
	 */
	public String createUserGroupProfiles() {
		PmUserGroup userGroup = new PmUserGroup();
		List<PmUserGroupUser> userGroupUser = new ArrayList<PmUserGroupUser>();
		this.getManagedBean().setUserGroup(userGroup);
		this.getManagedBean().setUserGroups(userGroupUser);
		this.getManagedBean().setOrgProfileId(null);

		List<PmUserOrg> userOrgList = PmCommonUtil.retrieveActiveOrg(getFacesScopes().getUserSessionBean().getUserId());
		PmOrgProfile orgProfile = null;
		if (userOrgList != null && !userOrgList.isEmpty()) {
			orgProfile = userOrgList.get(0).getPmOrgProfile();
		}
		if (orgProfile != null) {
			if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())) {
				getManagedBean().getUserGroup().setPmOrgProfile(orgProfile);
				getManagedBean().setOrgProfileId(orgProfile.getOrgProfileId());
				getManagedBean().setDisabledOrg(true);
				this.getManagedBean().setSelectGrp(true);
			}
		}
		return "/xhtml/pm/usergroupsearch/userGroupMaintenance.xhtml";

	}

	/**
	 * reload back to user group search page
	 */
	public String backToMainPages() {
		// this.getManagedBean().setLazy(true);
		this.getManagedBean().setSelectGrp(false);
		this.getManagedBean().setOrgProfileId(null);
		debugLogger("backToMainPages");
		return NAV_TO_MAIN;
	}

	/**
	 * pagination for user search list
	 */
	public String goToMaintenanceAction(PmUserGroup userGroup) {
		this.getManagedBean().setUserGroup(userGroup);
		logger.info("inside  maintanace...");
		return "/xhtml/pm/usergroupsearch/userGroupMaintenance.xhtml";
	}

	public void searchUserGroupAction() {
		this.getManagedBean().setLazy(true);
		getUserGroupList().reloadData();
	}

	public void setUserGroupList(IPagingDataModel<PmUserGroup> userGroupList) {
		this.userGroupList = userGroupList;
	}

	public void userGroupListner(List<BaseEntity> users) {
		List<PmUserGroupUser> groupUsers = this.getManagedBean().getUserGroups();
		if (null != users) {
			// getManagedBean().setBrandNew(true);
			PmUserGroupUser groupUser = null;
			for (int i = 0; i < users.size(); i++) {
				PmUser pmUser = (PmUser) users.get(i);
				groupUser = new PmUserGroupUser();
				for (PmUserGroupUser userGroupUser : groupUsers) {
					if (userGroupUser.getPmUser().getIdentificationNo().equalsIgnoreCase(pmUser.getIdentificationNo()))
						throw new EpException(MessageConst.ERR_900002);
				}

				groupUser.setPmUser(pmUser);
				groupUser.setPmUserGroup(this.getManagedBean().getUserGroup());
				// retrieve pm user roles
				groupUser.setPmUserRole(retrieveUserGroup(pmUser.getUserId()));
				groupUsers.add(groupUser);
			}
		}
		this.getManagedBean().setUserGroups(groupUsers);
	}

	private PmUserRole retrieveUserGroup(Long userId) {
		logger.info("userId: " + userId);
		IUserSO reqSO = null;
		PmUserRole pmUserRole = null;
		Date currentDate = new Date();
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			reqSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserSO.class);

			SearchBean<PmUserRole> searchBean = new SearchBean<PmUserRole>();

			// Filter By - user org info
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmUserOrg.effDate", SearchOperator.OPERATOR_LESS_EQUAL.value(), currentDate));

			searchBean.getDataFilterBeans()
					.add(new SearchMultiFilterBean(
							new SearchFilterBean("pmUserOrg.expDate", SearchOperator.OPERATOR_GREATER_EQUAL.value(),
									currentDate),
							new SearchFilterBean("pmUserOrg.expDate", SearchOperator.OPERATOR_IS_NULL.value(), null,
									SearchCondition.CONDITION_OR.value())));

			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmUserOrg.pmOrgProfile.orgProfileId",
					SearchOperator.OPERATOR_EQUALS.value(), this.getManagedBean().getOrgProfileId()));

			searchBean.getDataFilterBeans().add(new SearchFilterBean("recordStatusFlg",
					SearchOperator.OPERATOR_EQUALS.value(), DbValueConst.RECORD_STATUS_ACTIVE));

			// User id
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmUserOrg.pmUser.userId", SearchOperator.OPERATOR_EQUALS.value(), userId));

			// Role Code
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmRole.roleCode",
					SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getUserGroup().getRoleCode()));

			reqDTO.setUserRoleSearchBean(searchBean);
			UserResDTO resDTO = reqSO.retrieveUserRoleBySearchBean(reqDTO);
			if (resDTO.isSuccessful() && resDTO.getUserRoleList() != null && !resDTO.getUserRoleList().isEmpty()) {
				logger.info("not null");
				logger.info("pmUserOrg" + resDTO.getUserRoleList().size());
				pmUserRole = resDTO.getUserRoleList().get(0);
			}

		} finally {
			reqSO = null;
		}
		return pmUserRole;
	}

	public void userListner(PmOrgProfile orgProfile) {
		if (orgProfile != null) {
			orgProfileId = orgProfile.getOrgProfileId();
			this.getManagedBean().setOrgProfileId(orgProfileId);
			this.getManagedBean().setSelectGrp(true);
			getManagedBean().getUserGroup().setPmOrgProfile(orgProfile);

			RequestContext.getCurrentInstance().update("usergrouping");
		}

	}

	// public void deleteUserGroup(int rowIndex) {
	//
	// List<PmUserGroupUser> grpUsers = this.getManagedBean().getUserGroups();
	// PmUserGroupUser removeGroupUser = grpUsers.get(rowIndex);
	//// List<PmUserGroupUser> selectedUsrGrp = this.getManagedBean()
	//// .getSelectedUsrGrps();
	////
	//// if (selectedUsrGrp.isEmpty()) {
	//// throw new EpException(MessageConst.ERR_900003);
	//// }
	//// if (grpUsers.size() == selectedUsrGrp.size()) {
	//// throw new EpException(MessageConst.ERR_900004);
	//// }
	// grpUsers.remove(removeGroupUser);
	//// for (PmUserGroupUser pmUserGroupUser : selectedUsrGrp) {
	//// grpUsers.remove(pmUserGroupUser);
	//// }
	//
	// this.getManagedBean().setUserGroups(grpUsers);
	// }

	public void deleteUserGroup() {

		List<PmUserGroupUser> grpUsers = this.getManagedBean().getUserGroups();
		PmUserGroupUser removeGroupUser = getManagedBean().getSelectedDeleteGroupUser();
		// List<PmUserGroupUser> selectedUsrGrp = this.getManagedBean()
		// .getSelectedUsrGrps();
		//
		// if (selectedUsrGrp.isEmpty()) {
		// throw new EpException(MessageConst.ERR_900003);
		// }
		// if (grpUsers.size() == selectedUsrGrp.size()) {
		// throw new EpException(MessageConst.ERR_900004);
		// }
		grpUsers.remove(removeGroupUser);
		// for (PmUserGroupUser pmUserGroupUser : selectedUsrGrp) {
		// grpUsers.remove(pmUserGroupUser);
		// }

		this.getManagedBean().setUserGroups(grpUsers);
	}

	/**
	 * pass object into user maintenance page
	 */
	public String gotoMaintanance(PmUserGroup userGroup) {
		logger.info("gotoMaintanance");
		this.getManagedBean().setSelectGrp(true);
		// getManagedBean().setBrandNew(false);
		orgProfileId = userGroup.getPmOrgProfile().getOrgProfileId();
//		List<PmUserGroupUser> groupUserList = new ArrayList<PmUserGroupUser>();
//		for (PmUserGroupUser userGroupUser : userGroup.getPmUserGroupUsers()) {
//			for (PmUserOrg userOrg : userGroupUser.getPmUser().getPmUserOrgs()) {
//				if (orgProfileId.equals(userOrg.getPmOrgProfile().getOrgProfileId())
//						&& DbValueConst.RECORD_STATUS_ACTIVE == userOrg.getRecordStatusFlg()) {
//					for (PmUserRole userRole : userOrg.getPmUserRoles()) {
//						if (userGroup.getRoleCode().equals(userRole.getPmRole().getRoleCode())) {
//							if ((RoleCode.FL_APPROVER.value().equals(userRole.getPmRole().getRoleCode())
//									|| RoleCode.RN_APPROVER.value().equals(userRole.getPmRole().getRoleCode()))
//									&& null != userRole.getApprovalLimit()) {
//								if (!groupUserList.contains(userGroupUser)) {
//									userGroupUser.setPmUserRole(userRole);
//									groupUserList.add(userGroupUser);
//								}
//							}
//						}
//					}
//				}
//
//			}
//		}
//		logger.info("groupUserList  :" + groupUserList.size());		
		
		this.getManagedBean().setOrgProfileId(orgProfileId);
		this.getManagedBean().setUserGroup(userGroup);
		this.getManagedBean().setUserGroups(userGroup.getPmUserGroupUsers());
		
		//[#26037] - start		
		if(this.getManagedBean().getHmGroupUserFlagStatDb() == null) {
			this.getManagedBean().setHmGroupUserFlagStatDb(new HashMap<Long,Integer>());		
		}
		
		HashMap<Long,Integer> hmGrpUsrStat = this.getManagedBean().getHmGroupUserFlagStatDb();
		if(!hmGrpUsrStat.isEmpty()) {
			this.getManagedBean().getHmGroupUserFlagStatDb().clear();
		}
		
		//logger.info("[#26037] Set value to hashmap...");
		if(this.getManagedBean().getUserGroups() != null) {
			for (PmUserGroupUser groupUser : this.getManagedBean().getUserGroups()) {
				hmGrpUsrStat.put(groupUser.getPmUser().getUserId(), groupUser.getRecordStatusFlg());
			}
		}
		//logger.info("[#26037] Hashmap size: " + hmGrpUsrStat.size());
		this.getManagedBean().setHmGroupUserFlagStatDb(hmGrpUsrStat);		
		//[#26037] - end		
		
		//#27629 start
		this.getManagedBean().setUserSearch(false);
		this.getManagedBean().setUserName(null);
		this.getManagedBean().setIdentificationNo(null);
		this.getManagedBean().setRecordSearchStatusFlg(null);
		//#27629 end
		
		return "/xhtml/pm/usergroupsearch/userGroupMaintenance.xhtml";
	}

	/**
	 * Validation message for user group details
	 * 
	 */
	private void userGroupValidationMsg(PmUserGroup userGroup) {
		debugLogger("userGroupValidationMsg");

		if (StringUtils.isBlank(userGroup.getGroupCode())) {
			throw new EpException(MessageConst.ERR_900006);
		}

		if (StringUtils.isBlank(userGroup.getGroupName())) {
			throw new EpException(MessageConst.ERR_900007);
		}

		if (userGroup.getPmOrgProfile() == null) {
			throw new EpException(MessageConst.ERR_900008);
		}

		if (this.getManagedBean().getUserGroups().size() == 0) {
			throw new EpException(MessageConst.ERR_900005);
		}

		if (null == userGroup.getRecordStatusFlg()) {
			throw new EpException(MessageConst.ERR_900009);
		}

		if (DbValueConst.RECORD_STATUS_ACTIVE == userGroup.getRecordStatusFlg()) {
			boolean isValid = false;
			for (PmUserGroupUser groupUser : getManagedBean().getUserGroups()) {
				if (DbValueConst.RECORD_STATUS_ACTIVE == groupUser.getRecordStatusFlg()) {
					isValid = true;
					break;
				}
			}
			if (!isValid) {
				throw new EpException(MessageConst.ERR_900030);
			}
			if (!userGroup.isBrandNew() && !validateOrgProfileStatus(userGroup)) {
				throw new EpException(MessageConst.ERR_900033, new String[] { userGroup.getPmOrgProfile().getOrgCode(),
						userGroup.getPmOrgProfile().getOrgName() });
			}

			// validate duplicate group name (Role and PTJ)
			findDuplicateGroupName();
		}

	}

	/**
	 * save or create user group details
	 */
	public String saveUserGroup() {
		debugLogger("saveUserGroup");
		
		//#34445 Start
		try {
			PmUserGroup userGroup = this.getManagedBean().getUserGroup();
			// userGroup.setRoleCode("FL_APPROVER");

			// #29225 get changes
			if (this.getManagedBean().isUserSearch()) {
				for (PmUserGroupUser searchList : this.getManagedBean().getUserGroupUserSearch()) {
					for (PmUserGroupUser originList : userGroup.getPmUserGroupUsers()) {
						if (searchList.getUserGroupUserId() == originList.getUserGroupUserId()) {
							originList.setRecordStatusFlg(searchList.getRecordStatusFlg());
						}
					}
				}
			}

			IUserGroupSO userGrpSo = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserGroupSO.class);

			// userGroup.setGroupCode(userGroup.getGroupCode().toUpperCase());
			if (userGroup.isBrandNew()) {

				UserGroupReqDTO seqReqDto = new UserGroupReqDTO();
				seqReqDto.setGroupCode("GR");
				UserGroupResDTO seqResDto = userGrpSo.generateGroupCode(seqReqDto);
				userGroup.setGroupCode(seqResDto.getGroupCode());
			}

			userGroupValidationMsg(userGroup);

			List<PmUserGroupUser> pmUserGroupUsers = this.getManagedBean().getUserGroups();

			// logger.info("[#26037] - calling setUserDetails...start");
			HashMap<Long, Integer> hMap = this.getManagedBean().getHmGroupUserFlagStatDb();
			for (PmUserGroupUser groupUser : pmUserGroupUsers) {
				// logger.info("[#26423] User [" +
				// groupUser.getPmUser().getUserId() + "]");
				if (hMap != null && !(hMap.isEmpty())) {
					// [#26423] - start
					if (hMap.containsKey(groupUser.getPmUser().getUserId())) {
						// logger.info("[#26423] Existing user [" +
						// groupUser.getPmUser().getUserId() + "]");
						if (groupUser.getRecordStatusFlg().intValue() != hMap.get(groupUser.getPmUser().getUserId())
								.intValue()) {
							// logger.info("[#26037] User [" +
							// groupUser.getPmUser().getUserId() +
							// "] status has been changed from " +
							// hMap.get(groupUser.getPmUser().getUserId()) +
							// " to " + groupUser.getRecordStatusFlg());
							setUserDetails(userGroup, groupUser);
						}
					} else {
						// logger.info("[#26423] New user added [" +
						// groupUser.getPmUser().getUserId() + "]");
						setUserDetails(userGroup, groupUser);
					}
					// [#26423] - end
				} else {
					setUserDetails(userGroup, groupUser);
				}
			}
			// logger.info("[#26037] - calling setUserDetails...end");

			changeUserGroupStatus(pmUserGroupUsers);
			userGroup.setPmUserGroupUsers(pmUserGroupUsers);
			if (userGroup.isBrandNew()) {
				findDuplicateGroup(userGroup);
			}

			UserGroupReqDTO reqDto = new UserGroupReqDTO();
			reqDto.setUserGroup(userGroup);
			reqDto.setRoleCode(PmCommonUtil.getUserRoleValue());
			reqDto.setPmUser(getFacesScopes().getUserSessionBean());

			/** AUDIT 28895_35509 START **/
			if (reqDto.getUserGroup() != null && reqDto.getUserGroup().getUserGroupId() != null
					&& !userGroup.isBrandNew()) {
				System.out.println("userGroup.isBrandNew() :::: " + userGroup.isBrandNew());
				UserGroupResDTO resDTOOld = userGrpSo.retrieveUserGroupByUserGroupId(reqDto);
				this.getManagedBean().setUserGroupOld(resDTOOld.getUserGroup());
				/** AUDIT 28895_35509 END **/
			}

			UserGroupResDTO resDTO = userGrpSo.saveOrUpdateUserGroup(reqDto);
			/** AUDIT 28895_35509 START **/
			this.getManagedBean().setUserGroupUpdate(resDTO.getUserGroupUpdate());
			this.getManagedBean().setUserGroupNew(resDTO.getUserGroupNew());
			if (null != resDTO.getUserGroupNew()) {
				this.getManagedBean().setRoleDesc(
						PmCommonUtil.getRoleDescByRoleCodeAndLanguage(resDTO.getUserGroupNew().getRoleCode(), "ms"));
			}

			/** AUDIT 28895_35509 END **/
			this.getManagedBean().setUserGroup(resDTO.getUserGroup());
			if (resDTO.isSuccessful()) {

				logger.info("resDTO.getUserGroup().getGroupCode() :" + resDTO.getUserGroup().getGroupCode());
				if (userGroup.isBrandNew()) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
									MessageConst.ERR_900022, new String[] { resDTO.getUserGroup().getGroupName() },
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				} else {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
									MessageConst.ERR_900023, new String[] { resDTO.getUserGroup().getGroupName() },
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				}
				/************ AUDIT #28895_35509 *************/
				IAuditPMSO pmauditSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);
				AuditPMBackingBeanTemplate auTemplate = new AuditPMUserGroupBackingBean();
				if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_USERGROUP)) {
					// getManagedBean().setSelectedCriteria(resDTO.getPmCriteria());
					auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_ORGUSERGROUPCFG);
				}
				/************ END AUDIT #28895_35509 *************/
			}
			if (userGroup.isBrandNew()) {
				return NAV_TO_MAIN;
			} else {
				return gotoMaintanance(resDTO.getUserGroup());
				// return "userGroupMaintenance"; //#29225 duplicate return
			}
		} catch (EpException e) {
			handleException(e);
			return NAV_TO_MAINTAIN;
		}
		//#34445 End
	}

	public String constuctConditionFields() {
		StringBuilder sb = new StringBuilder();
		orgProfileId = this.getManagedBean().getOrgProfileId();
		if (null != orgProfileId) {
			sb.append("pmUserOrgs.pmOrgProfile.orgProfileId^");
			sb.append(orgProfileId);
			// return "pmUserOrgs.pmOrgProfile.orgProfileId^" + orgProfileId +
			// "|"
			// + "pmUserOrgs.pmUserRoles.pmRole.roleCode^" +
			// RoleCode.FL_APPROVER.value();
		}

		if (StringUtils.isNotBlank(getManagedBean().getUserGroup().getRoleCode())) {
			sb.append("|pmUserOrgs.pmUserRoles.pmRole.roleCode^");
			sb.append(getManagedBean().getUserGroup().getRoleCode());
		}

		// Filter by record status
		sb.append("|pmUserOrgs.pmUserRoles.recordStatusFlg^");
		sb.append(DbValueConst.RECORD_STATUS_ACTIVE);
		// sb.append("|pmUserOrgs.recordStatusFlg^");
		// sb.append(DbValueConst.RECORD_STATUS_ACTIVE);

		StringBuilder selectedUsersIds = new StringBuilder();
		if (getManagedBean().getUserGroups() != null && !getManagedBean().getUserGroups().isEmpty()) {
			PmUserGroupUser groupUser = null;
			for (int i = 0; i < getManagedBean().getUserGroups().size(); i++) {
				groupUser = getManagedBean().getUserGroups().get(i);
				if (groupUser.getPmUser() != null && groupUser.getPmUser().getUserId() != null) {
					if (i != 0) {
						selectedUsersIds.append(",");
					}
					selectedUsersIds.append(groupUser.getPmUser().getUserId());
				}
			}
		}
		if (selectedUsersIds.length() > 0) {
			sb.append("|userId^");
			sb.append(selectedUsersIds.toString());
			sb.append("^");
			sb.append(SearchOperator.OPERATOR_NOT_IN.value());
		}
		return sb.toString();
	}

	/*
	 * Verifying duplicate record
	 * 
	 * @Param PmUserGroup
	 * 
	 * @return boolean
	 */
	private void findDuplicateGroup(PmUserGroup userGroup) {
		logger.info("inside findDuplicateGroup");

		UserGroupReqDTO reqDTO = new UserGroupReqDTO();
		reqDTO.setUserGroup(userGroup);
		IUserGroupSO groupSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IUserGroupSO.class);
		boolean response = groupSO.checkDuplicateGroup(reqDTO);
		if (response) {
			throw new EpException(MessageConst.ERR_900001, new String[] { userGroup.getGroupCode() });
		}
	}

	public void preLoadgroupSearch() {
		logger.info("preLoadgroupSearch ....");
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_MAIN);
	}

	/*
	 * Setting update user
	 */
	private void setUserDetails(PmUserGroup userGroup, PmUserGroupUser groupUser) {
		logger.info("setUserDetails");
		if (!userGroup.isBrandNew() && DbValueConst.RECORD_STATUS_SUSPENDED == userGroup.getRecordStatusFlg()) {
			groupUser.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
		} else if (!userGroup.isBrandNew() && DbValueConst.RECORD_STATUS_ACTIVE == userGroup.getRecordStatusFlg()
				&& DbValueConst.RECORD_STATUS_ACTIVE == groupUser.getRecordStatusFlg()) {
			boolean isValidOrgProfile = false;
			boolean isValidRoleStatus = false;
			if (validateUserOrgProfileStatus(userGroup, groupUser.getPmUser())) {
				isValidOrgProfile = true;
			}

			if (validateUserRoleStatus(userGroup, groupUser.getPmUser())) {
				isValidRoleStatus = true;
			}

			if (!isValidOrgProfile) {
				throw new EpException(MessageConst.ERR_900031, new String[] { groupUser.getPmUser().getUserName(),
						userGroup.getPmOrgProfile().getOrgCode(), userGroup.getPmOrgProfile().getOrgName() });
			}

			if (!isValidRoleStatus) {
				throw new EpException(MessageConst.ERR_900032, new String[] {
						PmCommonUtil.getRoleDescByRoleCodeAndLanguage(getManagedBean().getUserGroup().getRoleCode(),
								getFacesScopes().getUserLocale().getLanguage()),
						groupUser.getPmUser().getUserName() });
			}

			if (isValidOrgProfile && isValidRoleStatus) {
				groupUser.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
			}
		} else if (!userGroup.isBrandNew() 
				&& DbValueConst.RECORD_STATUS_ACTIVE == userGroup.getRecordStatusFlg().intValue()
				&& DbValueConst.RECORD_STATUS_SUSPENDED == groupUser.getRecordStatusFlg().intValue()) {
			//logger.info("[#26037] - check any pending task for user: " + groupUser.getPmUser().getUserName());
			//logger.info("[#26037] - group id: " + userGroup.getUserGroupId());
			//logger.info("[#26037] - group role: " + userGroup.getRoleCode());
			//logger.info("[#26037] - check any pending task for user if status set to 0");
			if (userGroup.getUserGroupId() != null 
					&& groupUser.getRecordStatusFlg().intValue() == 0) {
				//logger.info("[#26037] - user set to inactive [0]");
				FulfilmentReqDTO fulfilmentReqDTO = null;
				SourcingIntegrationReqDTO sourcingReqDTO = null;
				
				if (RoleCode.FL_APPROVER.value().equals(getManagedBean().getUserGroup().getRoleCode())) {
					if (groupUser.getPmUser().getLoginId() != null) {
						
						fulfilmentReqDTO = new FulfilmentReqDTO();						
							
						fulfilmentReqDTO.setGroupId(userGroup.getUserGroupId());						
						//logger.info("[#26037] - calling isAnyPendingTaskForUser...");
						if(isAnyPendingTaskForUser(groupUser.getPmUser().getLoginId(), fulfilmentReqDTO, null)){
							//logger.info("[#26037] - user still has pending task in hand...");
							// revert it to active
							userGroup.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
							
							// hit error and exit
							throw new EpException(MessageConst.ERROR_770228, new String[] { groupUser.getPmUser().getUserName() });
						}						
					}
					
				} else if (RoleCode.RN_APPROVER.value().equals(getManagedBean().getUserGroup().getRoleCode())) {
					if (groupUser.getPmUser().getLoginId() != null) {									
						
						sourcingReqDTO = new SourcingIntegrationReqDTO();
						
						sourcingReqDTO.setUserGroupId(userGroup.getUserGroupId());						
						//logger.info("[#26037] - calling isAnyPendingTaskForUser...");
						if(isAnyPendingTaskForUser(groupUser.getPmUser().getLoginId(), null, sourcingReqDTO)){
							//logger.info("[#26037] - user still has pending task in hand...");
							// revert it to active
							userGroup.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
							
							// hit error and exit
							throw new EpException(MessageConst.ERROR_770228, new String[] { groupUser.getPmUser().getUserName() });
						}						
					}
				}
			}
		}
	}

	public void changeUserGroupStatus(List<PmUserGroupUser> pmUserGroupUsers) {
		PmUserGroup userGroup = getManagedBean().getUserGroup();
		if (!userGroup.isBrandNew() && DbValueConst.RECORD_STATUS_SUSPENDED == userGroup.getRecordStatusFlg()) {
			for (PmUserGroupUser groupUser : getManagedBean().getUserGroups()) {
				groupUser.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
			}
		}
	}

	// public void changeUserStatus(PmUserGroupUser groupUser){
	// PmUserGroup userGroup = getManagedBean().getUserGroup();
	// if(groupUser.getRecordStatusFlg() != null &&
	// DbValueConst.RECORD_STATUS_ACTIVE == groupUser.getRecordStatusFlg()){
	// if(validateUserStatus(userGroup, groupUser.getPmUser())){
	// groupUser.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
	// }else{
	// throw new EpException(groupUser.getPmUser().getLoginId() + " , " +
	// groupUser.getPmUser().getUserName() + " is not valid.");
	// }
	// }
	// }

	/**
	 * This method used to retrieve login user information.
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public String constructConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("recordStatus^1");
		List<PmUserOrg> userOrgList = PmCommonUtil.retrieveActiveOrg(getFacesScopes().getUserSessionBean().getUserId());
		PmOrgProfile orgProfile = null;
		if (userOrgList != null && !userOrgList.isEmpty()) {
			orgProfile = userOrgList.get(0).getPmOrgProfile();
		}
		if (orgProfile != null) {
			if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
				sb.append("|");
				sb.append(OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()));
				sb.append("^");
				sb.append(orgProfile.getOrgProfileId());

			} else if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())) {
				sb.append("|");
				sb.append(OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()));
				sb.append("^");
				sb.append(orgProfile.getOrgProfileId());

			} else if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())) {
				sb.append("|orgProfileId^");
				sb.append(orgProfile.getOrgProfileId());
			}
		}

		return sb.toString();
	}

	public SelectItem[] getUserGroupRoles() {
		ISearchBean<PmRoleDesc> rolesSearchBean = super.createSearchBean(PmRoleDesc.class, "somusergrouproles");
		SearchFilterBean bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
		bean.setSearchField("pmRole.roleCode");
		bean.setSearchValue(new ArrayList<String>(Arrays.asList(ProfileManagementConst.UG_ROLES_LIST)));
		rolesSearchBean.getDataFilterBeans().add(bean);

		SelectItem[] items = null;
		IRoleSO roleSO = null;
		try {
			if (rolesSearchBean != null) {
				rolesSearchBean.getSortColumns();
				RoleReqDTO roleReqDTO = new RoleReqDTO();
				roleReqDTO.setSearchRoleBean(rolesSearchBean);
				roleReqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
				roleSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IRoleSO.class);
				RoleResDTO resDto = roleSO.retrieveRoleDecsBySearchBean(roleReqDTO);
				if (resDto.isSuccessful() && resDto.getSearchRoleResultBean() != null
						&& !resDto.getSearchRoleResultBean().getResultList().isEmpty()) {
					items = new SelectItem[resDto.getSearchRoleResultBean().getResultList().size() + 1];
					items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));

					int i = 1;
					for (PmRoleDesc roleDesc : resDto.getSearchRoleResultBean().getResultList()) {
						items[i++] = new SelectItem(roleDesc.getPmRole().getRoleCode(), roleDesc.getRoleName());
					}
				}
			}
		} finally {
			roleSO = null;
		}

		return items;
	}

	private void findDuplicateGroupName() {
		PmUserGroup userGroup = getManagedBean().getUserGroup();
		UserGroupReqDTO reqDTO = new UserGroupReqDTO();
		reqDTO.setUserGroup(userGroup);
		IUserGroupSO groupSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IUserGroupSO.class);
		boolean response = groupSO.checkDuplicateGroupName(reqDTO);
		if (response) {
			if (userGroup.isBrandNew()) {
				throw new EpException(MessageConst.ERR_900029, new String[] {
						PmCommonUtil.getRoleDescByRoleCodeAndLanguage(getManagedBean().getUserGroup().getRoleCode(),
								getFacesScopes().getUserLocale().getLanguage()),
						getManagedBean().getUserGroup().getPmOrgProfile().getOrgCode() });
			} else {
				throw new EpException(MessageConst.ERROR_770220,
						new String[] { PmCommonUtil.getResourceBundleLabels("lbl.pm.ug.result.groupname") });
			}

		}
	}

	private boolean validateUserOrgProfileStatus(PmUserGroup userGroup, PmUser user) {

		boolean isValid = false;
		List<PmUser> pmUsers = new ArrayList<PmUser>();

		ISearchBean<PmUser> searchBean = new SearchBean<PmUser>();

		/* Search Bean Filter - Start */
		// only active user
		SearchFilterBean bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("recordStatusFlg");
		bean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
		searchBean.getDataFilterBeans().add(bean);

		// only active org group
		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("pmUserOrgs.recordStatusFlg");
		bean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
		searchBean.getDataFilterBeans().add(bean);

		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("pmUserOrgs.pmOrgProfile.orgProfileId");
		bean.setSearchValue(userGroup.getPmOrgProfile().getOrgProfileId());
		searchBean.getDataFilterBeans().add(bean);

		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("pmUserOrgs.pmOrgProfile.recordStatusFlg");
		bean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
		searchBean.getDataFilterBeans().add(bean);

		Date currentDate = new Date();
		// Filter By - Org effDate
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmUserOrgs.pmOrgProfile.pmOrgValidity.effDate",
				SearchOperator.OPERATOR_LESS_EQUAL.value(), currentDate));

		// Filter By - Org expDate
		searchBean.getDataFilterBeans()
				.add(new SearchMultiFilterBean(
						new SearchFilterBean("pmUserOrgs.pmOrgProfile.pmOrgValidity.expDate",
								SearchOperator.OPERATOR_GREATER_EQUAL.value(), currentDate),
						new SearchFilterBean("pmUserOrgs.pmOrgProfile.pmOrgValidity.expDate",
								SearchOperator.OPERATOR_IS_NULL.value(), null, SearchCondition.CONDITION_OR.value())));

		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("userId");
		bean.setSearchValue(user.getUserId());
		searchBean.getDataFilterBeans().add(bean);

		// search users
		pmUsers = PmCommonUtil.searchUsersBySearchBean(searchBean);
		if (pmUsers != null && !pmUsers.isEmpty()) {
			isValid = true;
		}

		return isValid;
	}

	private boolean validateUserRoleStatus(PmUserGroup userGroup, PmUser user) {

		boolean isValid = false;
		List<PmUser> pmUsers = new ArrayList<PmUser>();

		ISearchBean<PmUser> searchBean = new SearchBean<PmUser>();

		/* Search Bean Filter - Start */

		// only active user
		SearchFilterBean bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("recordStatusFlg");
		bean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
		searchBean.getDataFilterBeans().add(bean);

		// only active user role
		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("pmUserOrgs.pmUserRoles.recordStatusFlg");
		bean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
		searchBean.getDataFilterBeans().add(bean);

		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("userId");
		bean.setSearchValue(user.getUserId());
		searchBean.getDataFilterBeans().add(bean);

		// in applicable role code
		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("pmUserOrgs.pmUserRoles.pmRole.roleCode");
		bean.setSearchValue(userGroup.getRoleCode());
		searchBean.getDataFilterBeans().add(bean);

		// search users
		pmUsers = PmCommonUtil.searchUsersBySearchBean(searchBean);
		if (pmUsers != null && !pmUsers.isEmpty()) {
			isValid = true;
		}

		return isValid;
	}

	private boolean validateOrgProfileStatus(PmUserGroup userGroup) {
		boolean isValid = false;
		IOrgProfileSO orgSO = null;
		try {
			PmOrgProfile orgProfile = userGroup.getPmOrgProfile();
			ISearchBean<PmOrgProfile> searchBean = new SearchBean<PmOrgProfile>();
			Date currentDate = new Date();

			// Filter By - Active Status Only
			searchBean.getDataFilterBeans().add(new SearchFilterBean("recordStatusFlg",
					SearchOperator.OPERATOR_EQUALS.value(), DbValueConst.RECORD_STATUS_ACTIVE));

			// Filter By - Org effDate
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmOrgValidity.effDate",
					SearchOperator.OPERATOR_LESS_EQUAL.value(), currentDate));

			// Filter By - Org expDate
			searchBean.getDataFilterBeans()
					.add(new SearchMultiFilterBean(
							new SearchFilterBean("pmOrgValidity.expDate", SearchOperator.OPERATOR_GREATER_EQUAL.value(),
									currentDate),
							new SearchFilterBean("pmOrgValidity.expDate", SearchOperator.OPERATOR_IS_NULL.value(), null,
									SearchCondition.CONDITION_OR.value())));

			// Filter By - Org Profile Id
			searchBean.getDataFilterBeans().add(new SearchFilterBean("orgProfileId",
					SearchOperator.OPERATOR_EQUALS.value(), orgProfile.getOrgProfileId()));

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileSearchBean(searchBean);
			orgSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgSO.searchOrgProfile(reqDTO);
			if (resDTO.getOrgProfileResultBean() != null && resDTO.getOrgProfileResultBean().getResultList() != null
					&& !resDTO.getOrgProfileResultBean().getResultList().isEmpty()) {
				isValid = true;
			}
		} finally {
			orgSO = null;
		}
		return isValid;
	}

	public void resetGroupUsers() {
		getManagedBean().setUserGroups(new ArrayList<PmUserGroupUser>());
	}

	public void userGroupStatusChange() {
		logger.info("userGroupStatusChange");
		if (this.getManagedBean().getUserGroup() != null) {
			int status = this.getManagedBean().getUserGroup().getRecordStatusFlg();
			if (DbValueConst.RECORD_STATUS_SUSPENDED == status
					&& isAnyPendingTaskForGroup(getManagedBean().getUserGroup().getUserGroupId())) {
				getManagedBean().getUserGroup().setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
				throw new EpException("this usergroup has active task");
			}
		}
	}

	private boolean isAnyPendingTaskForGroup(Long groupId) {
		logger.info("isAnyPendingTaskForGroup");
		boolean anyPendingTask = false;

		IFulfilmentSO fulfilmentSO = null;
		FulfilmentReqDTO reqDTO = null;
		FulfilmentResDTO resDTO = null;
		ISourcingIntegrationSO sourcingIntegrationSO = null;
		SourcingIntegrationReqDTO sourcingReqDTO = null;
		SourcingIntegrationResDTO sourcingResDTO = null;

		try {
			if (groupId != null) {
				if (RoleCode.FL_APPROVER.value().equals(getManagedBean().getUserGroup().getRoleCode())) {
					fulfilmentSO = (IFulfilmentSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentSO.class);

					reqDTO = new FulfilmentReqDTO();
					reqDTO.setGroupId(groupId);
					// check user group active in fl
					resDTO = fulfilmentSO.isUserGroupActiveInFulfilment(reqDTO);
					if (resDTO.isHasActiveTransaction()) {
						anyPendingTask = true;
					} else {
						// check user group user active in fl
						resDTO = fulfilmentSO.isUserGroupUserActiveInFulfilment(reqDTO);
						if (resDTO.isHasActiveTransaction()) {
							anyPendingTask = true;
						}
					}

				} else if (RoleCode.RN_APPROVER.value().equals(getManagedBean().getUserGroup().getRoleCode())) {
					sourcingIntegrationSO = (ISourcingIntegrationSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
					sourcingReqDTO = new SourcingIntegrationReqDTO();
					sourcingReqDTO.setUserGroupId(groupId);
					sourcingResDTO = sourcingIntegrationSO.isUserGroupActiveInDP(sourcingReqDTO);
					if (sourcingResDTO.isUserGroupActive()) {
						anyPendingTask = true;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fulfilmentSO = null;
		}
		return anyPendingTask;
	}
	
	/*private boolean isAnyPendingTaskForUser(String loginId) {		
		logger.info("isAnyPendingTaskForUser");
		return PmCommonUtil.isAnyPendingTask(loginId);
	}*/
	
	private boolean isAnyPendingTaskForUser(String loginId, FulfilmentReqDTO fulfilmentReqDTO, SourcingIntegrationReqDTO sourcingReqDTO) {
		logger.info("isAnyPendingTaskForUser");
		boolean anyPendingTask = false;
		IBpmSO bpmSO = null;
		BpmRequestDTO reqDTO = null;
		BpmResponseDTO responseDTO = null;
		IFulfilmentSO fulfilmentSO = null;
		ISourcingIntegrationSO sourcingIntegrationSO = null;
		FulfilmentResDTO fulfilmentResDTO = null;
		SourcingIntegrationResDTO sourcingResDTO = null;
		
		try {			
			if (loginId != null && loginId.length() > 0) {
				
				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
						.getServiceObject(IBpmSO.class);
				
				reqDTO = new BpmRequestDTO();
				logger.info("BPM loginId: " + loginId);				
				reqDTO.setUserId(loginId);
				
				if (RoleCode.RN_APPROVER.value().equals(getManagedBean().getUserGroup().getRoleCode())) {
					reqDTO.setDocType(ParameterConst.DOCTYPE_REQUEST_NOTE );
				}
				
				// Invoke BPM
				logger.info("Invoke BPM...");
				responseDTO = bpmSO.getReassignedItems(reqDTO);
				
				if (responseDTO.getWorklistVO() != null) {				
					logger.info("BPM total: " + responseDTO.getWorklistVO().getTotal());
					if (responseDTO.getWorklistVO().getTotal() > 0) {						
						
						if(responseDTO.getWorklistVO().getWorklistItem() != null 
								&& responseDTO.getWorklistVO().getWorklistItem().size() > 0) {							
							
							if(fulfilmentReqDTO != null) {
								fulfilmentReqDTO.setDocNumberList(new ArrayList<String>());
							} else if(sourcingReqDTO != null) {
								sourcingReqDTO.setDocNumberList(new ArrayList<String>());
							}
							
							for(WorklistItemVO item : responseDTO.getWorklistVO().getWorklistItem()){
							/*	logger.info("#26037 - ******Doc Items From BPM******");
								logger.info("#26037 - TaskId******" + item.getTaskId());
								logger.info("#26037 - TaskNo******" + item.getTaskNumber());
								logger.info("#26037 - TaskName******" + item.getTaskName());
								logger.info("#26037 - DocId******" + item.getDocId());
								logger.info("#26037 - DocStatus******" + item.getDocStatus());
								logger.info("#26037 - DocType******" + item.getDocType());
								logger.info("#26037 - DocNumber******" + item.getDocNumber());							
								logger.info("#26037 - TaskType******" + item.getTaskType());
								logger.info("\n");
								logger.info("adding to list..."); */								
								if(fulfilmentReqDTO != null) {
									fulfilmentReqDTO.getDocNumberList().add(item.getDocNumber());
								} else if(sourcingReqDTO != null) {
									sourcingReqDTO.getDocNumberList().add(item.getDocNumber());
								}
							}							
						
							if (RoleCode.FL_APPROVER.value().equals(getManagedBean().getUserGroup().getRoleCode())) {
								fulfilmentSO = (IFulfilmentSO) BusinessDelegate.getInstance()
										.getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentSO.class);
								
								//logger.info("#26037 - call retrieveDocNumberByGroupId to compare list of Doc Number in BPM");
								//logger.info("#26037 - against list of Doc Number in FL_FULFILMENT_REQUEST");
								logger.info("#26037 - calling retrieveDocNumberByGroupId...");
								fulfilmentResDTO = fulfilmentSO.retrieveDocNumberByGroupId(fulfilmentReqDTO);
								//logger.info("#26037 - Total record match for Doc Number:" + fulfilmentResDTO.getDocNos().size());
								if(fulfilmentResDTO.getDocNos() != null 
										&& fulfilmentResDTO.getDocNos().size() > 0) {
									anyPendingTask = true;
								}
							} else if (RoleCode.RN_APPROVER.value().equals(getManagedBean().getUserGroup().getRoleCode())) {
								sourcingIntegrationSO = (ISourcingIntegrationSO) BusinessDelegate.getInstance()
										.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
								
								//logger.info("#26037 - call retrieveDocNumberByGroupId to compare list of Doc Number in BPM");
								//logger.info("#26037 - against list of Doc Number in SC_REQUEST_NOTE");
								logger.info("#26037 - calling retrieveDocNumberByGroupId...");
								sourcingResDTO = sourcingIntegrationSO.retrieveDocNumberByGroupId(sourcingReqDTO);
								//logger.info("#26037 - Total record match for Doc Number:" + sourcingResDTO.getDocNumberList().size());
								if(sourcingResDTO.getDocNumberList() != null 
										&& sourcingResDTO.getDocNumberList().size() > 0) {
									anyPendingTask = true;
								}
							}							
						}	
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bpmSO = null;
		}
		return anyPendingTask;
	}
	
	//#27629 start
	public void userGroupUserSearch() {
		logger.info("[#27629] userGroupSearch");
		String userName = getManagedBean().getUserName();
		String identificationNo = getManagedBean().getIdentificationNo();
		String statusFlag = getManagedBean().getRecordSearchStatusFlg();
		List<PmUserGroupUser> firstLoad = this.getManagedBean().getUserGroups(); //#29225 save origin list
		PmUserGroupUser groupUser = null;
		
		logger.info("userName :"+userName+ ", id :"+identificationNo+", status :" + statusFlag);
		
		List<PmUserGroupUser> groupUsers = new ArrayList<PmUserGroupUser>();
		if (!userName.isEmpty() || !identificationNo.isEmpty() || statusFlag!=null){
			
			//#29225 comment setter
			/*if (this.getManagedBean().isUserSearch()){
				this.getManagedBean().setUserGroups(this.getManagedBean().getUserGroupUserSearch());
			}else{
				this.getManagedBean().setUserGroupUserSearch(this.getManagedBean().getUserGroups());
			}*/
			
			for (PmUserGroupUser groupUserSearch : this.getManagedBean().getUserGroups()) {
				logger.info("Loop : " + groupUserSearch.getPmUser().getUserName() + ", " + groupUserSearch.getPmUser().getIdentificationNo()
						+ ", " + groupUserSearch.getApprovalLimit() + "," + groupUserSearch.getRecordStatusFlg()
						+ ", " + groupUserSearch.isBrandNew());
				groupUser = new PmUserGroupUser();

				if (groupUserSearch.getPmUser().getUserName() != null && !userName.isEmpty()
						&& groupUserSearch.getPmUser().getUserName().toLowerCase().contains(userName.toLowerCase())) {
					if (!identificationNo.isEmpty() && statusFlag != null) {
						if (groupUserSearch.getPmUser().getIdentificationNo().toLowerCase()
								.contains(identificationNo.toLowerCase())
								&& groupUserSearch.getRecordStatusFlg().toString().equals(statusFlag)) {
							groupUser.setPmUser(groupUserSearch.getPmUser());
						}
					} else if (!identificationNo.isEmpty() && statusFlag == null) {
						if (groupUserSearch.getPmUser().getIdentificationNo().toLowerCase()
								.contains(identificationNo.toLowerCase())) {
							groupUser.setPmUser(groupUserSearch.getPmUser());
						}
					} else if (identificationNo.isEmpty() && statusFlag != null) {
						if (groupUserSearch.getRecordStatusFlg().toString().equals(statusFlag)) {
							groupUser.setPmUser(groupUserSearch.getPmUser());
						}
					} else {
						groupUser.setPmUser(groupUserSearch.getPmUser());
					}
				}
				if (groupUserSearch.getPmUser().getIdentificationNo() != null && !identificationNo.isEmpty()
						&& userName.isEmpty()) {
					if (statusFlag != null) {
						if (groupUserSearch.getPmUser().getIdentificationNo().toLowerCase()
								.contains(identificationNo.toLowerCase())
								&& groupUserSearch.getRecordStatusFlg().toString().equals(statusFlag)) {
							groupUser.setPmUser(groupUserSearch.getPmUser());

						}
					} else {
						if (groupUserSearch.getPmUser().getIdentificationNo().toLowerCase()
								.contains(identificationNo.toLowerCase())) {
							groupUser.setPmUser(groupUserSearch.getPmUser());
						}
					}
				}
				if (groupUserSearch.getRecordStatusFlg().toString() != null && identificationNo.isEmpty()
						&& userName.isEmpty() && statusFlag != null) {
					if (groupUserSearch.getRecordStatusFlg().toString().equals(statusFlag.toLowerCase())) {
						groupUser.setPmUser(groupUserSearch.getPmUser());
					}
				}
				
				if (groupUser.getPmUser()!=null) {
					//groupUser.setPmUserGroup(this.getManagedBean().getUserGroup());
					//groupUser.setPmUserRole(groupUserSearch.getPmUserRole());
					groupUser.setRecordStatusFlg(groupUserSearch.getRecordStatusFlg());
					groupUser.setApprovalLimit(groupUserSearch.getApprovalLimit());
					groupUser.setBrandNew(groupUserSearch.isBrandNew());
					groupUser.setUserGroupUserId(groupUserSearch.getUserGroupUserId()); //#29225
					logger.info("insert : " + groupUser.getPmUser().getUserName() + ", " + groupUser.getPmUser().getIdentificationNo()
							+ ", " + groupUser.getApprovalLimit() + "," + groupUser.getRecordStatusFlg()
							+ ", " + groupUser.isBrandNew());
					groupUsers.add(groupUser);	
				}
			}
			this.getManagedBean().setUserSearch(true);
			this.getManagedBean().setUserGroupUserSearch(groupUsers); //#29225 display search result 
			//this.getManagedBean().setUserGroups(groupUsers);
		}
		/*else if (userName.isEmpty() && identificationNo.isEmpty() && statusFlag==null){
			if (this.getManagedBean().isUserSearch()){
				this.getManagedBean().setUserGroups(this.getManagedBean().getUserGroupUserSearch());
			}else{
				this.getManagedBean().setUserGroupUserSearch(this.getManagedBean().getUserGroups());
			}
		}*/
		else{
			this.getManagedBean().setUserSearch(false);
			this.getManagedBean().setUserGroups(firstLoad); //#29225 get origin
			this.getManagedBean().setUserGroupUserSearch(new ArrayList<PmUserGroupUser>()); //#29225 reset searchList
		}
	}
	public void userGroupUserReset() {
		
		//#29225 use isUserSearch as render property
		/*if(this.getManagedBean().isUserSearch()){
			this.getManagedBean().setUserGroups(this.getManagedBean().getUserGroupUserSearch());
		}*/	
		this.getManagedBean().setUserSearch(false);
		this.getManagedBean().setUserName(null);
		this.getManagedBean().setIdentificationNo(null);
		this.getManagedBean().setRecordSearchStatusFlg(null);
		
	}
	
	//#27629 end
}
