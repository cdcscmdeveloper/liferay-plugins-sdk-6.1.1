package my.ep.web.backingbean.pm.usergroup;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.authorization.usergroup.IUserGroupSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupReqDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmProfileGroup;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.usergroup.UsergroupManagedBean;
import my.ep.web.managedbean.pm.usergroup.UsergroupSearchBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "usergroupBackingBean")
@ViewScoped
public class UsergroupBackingBean extends BaseBackingBean<UsergroupManagedBean> {

	private static final Logger logger = EpLogger
			.getLogger(UsergroupBackingBean.class);

	private String NAV_TO_MAIN = "userGroupSearchMain";

	private IPagingDataModel<PmUserGroup> userGroupList = new UserGroupProfilePagingDataModel();

	private Long orgProfileId = null;

	public UsergroupBackingBean() {
		System.out.println("inside constructor");
	}

	/**
	 * reset search group detail
	 */
	public void reset() {
		System.out.println("inside reset");
		this.getManagedBean().setUsergroupSearchBean(new UsergroupSearchBean());
		this.getManagedBean().setStatus(null);
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

	/**
	 * pagination for user search list
	 */
	private class UserGroupProfilePagingDataModel extends
			PagingDataModel<PmUserGroup> {
		private static final long serialVersionUID = 1L;

		public UserGroupProfilePagingDataModel() {
			super("searchGroupProfile", getManagedBean().isLazy());
		}

		@Override
		protected ISearchResultBean<PmUserGroup> pageChangeListener(
				ISearchBean<PmUserGroup> searchBean) {
			populateSearchBean(searchBean);
			UserGroupReqDTO reqDTO = new UserGroupReqDTO();
			reqDTO.setUserGroupSearchBean(searchBean);
			IUserGroupSO groupSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IUserGroupSO.class);
			UserGroupResDTO resDTO = groupSO.retrieveUserGroups(reqDTO);
			ISearchResultBean<PmUserGroup> iSearchResultBean = resDTO
					.getUserGroupResultBean();
			return iSearchResultBean;

		}

		/**
		 * @param userGrpBean
		 * @param searchBean
		 */
		private void populateSearchBean(ISearchBean<PmUserGroup> searchBean) {
			
				if (getManagedBean().getUserName() != null
					&& !"".equalsIgnoreCase(getManagedBean().getUserName())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean
						.setSearchField("pmUserGroupUsers.pmUser.userName");
				codesSearchBean.setSearchValue(getManagedBean().getUserName());
				codesSearchBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}

			if (getManagedBean().getGroupCode() != null
					&& !"".equalsIgnoreCase(getManagedBean().getGroupCode())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean.setSearchField("groupCode");
				codesSearchBean.setSearchValue(getManagedBean().getGroupCode());
				codesSearchBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}

			if (getManagedBean().getGroupName() != null
					&& !"".equalsIgnoreCase(getManagedBean().getGroupName())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean.setSearchField("groupName");
				codesSearchBean.setSearchValue(getManagedBean().getGroupName());
				codesSearchBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}

			if (getManagedBean().getPtjCode() != null
					&& !"".equalsIgnoreCase(getManagedBean().getPtjCode())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean.setSearchField("pmOrgProfile.pmOrgValidity.orgCode");
				codesSearchBean.setSearchValue(getManagedBean().getPtjCode());
				codesSearchBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}

			if (getManagedBean().getPtjName() != null
					&& !"".equalsIgnoreCase(getManagedBean().getPtjName())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean
						.setSearchField("pmOrgProfile.pmOrgValidity.orgName");
				codesSearchBean.setCaseInsensitive(true);
				codesSearchBean.setSearchValue(getManagedBean().getPtjName());
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}

			if (getManagedBean().getStatus() != null
					&& !"".equals(getManagedBean().getStatus())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_EQUALS.value());
				codesSearchBean.setSearchField("recordStatusFlg");
				codesSearchBean.setSearchValue(getManagedBean().getStatus());
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}else{
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_IN.value());
				codesSearchBean.setSearchField("recordStatusFlg");
				codesSearchBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.RECORD_STATUS_INACTIVE, ParameterConst.RECORD_STATUS_ACTIVE)));
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}
			PmUser user = PmCommonUtil.retrieveUserById(getFacesScopes().getUserSessionBean().getUserId(), false);
			PmOrgProfile orgProfile = null;
			if(user != null){
				orgProfile = user.getPmUserOrgs().get(0).getPmOrgProfile();
			}
			if(orgProfile != null){
				Set<Long> orgProfileSet = null;
				if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
					orgProfileSet = new HashSet<Long>(); 
					for(PmOrgProfile childOrgProfile : orgProfile.getChildOrgProfiles()){
						orgProfileSet.add(childOrgProfile.getOrgProfileId());
					}
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmOrgProfile.parentOrgProfile.orgProfileId",
									SearchOperator.OPERATOR_IN.value(),new ArrayList<Long>(orgProfileSet)));
					
				}else if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmOrgProfile.parentOrgProfile.orgProfileId",
									SearchOperator.OPERATOR_EQUALS.value(),orgProfile.getOrgProfileId()));
					
				}else if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())) {
					searchBean.getDataFilterBeans().add(
									new SearchFilterBean("pmOrgProfile.orgProfileId",
											SearchOperator.OPERATOR_EQUALS.value(),orgProfile.getOrgProfileId()));
				}
			}
						
//		if (null!=new FacesScopes().getUserSessionBean()) {
//			
//			UserReqDTO reqDTO = new UserReqDTO();
//			IUserSO reqSO  =  (IUserSO) BusinessDelegate.getInstance()
//					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
//			PmUser pmUser = new FacesScopes().getUserSessionBean();
//			
//			UserReqDTO reqDTO1 = new UserReqDTO();
//			reqDTO1.setLoginId(pmUser.getLoginId());
//			UserResDTO resDTO = reqSO.retrieveOrgprofilebyUserId(reqDTO1);
//			if(resDTO.isSuccessful()){
//				SearchFilterBean orgProfileSearchBean = new SearchFilterBean(
//					SearchOperator.OPERATOR_EQUALS.value());
//				orgProfileSearchBean.setSearchField("pmOrgProfile.orgProfileId");
//				orgProfileSearchBean.setSearchValue(resDTO.getPmOrgProfile().getOrgProfileId().toString());
//				orgProfileSearchBean.setCaseInsensitive(true);
//				searchBean.getDataFilterBeans().add(orgProfileSearchBean);
//			}
//			
//			}
			SearchFilterBean codesSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			codesSearchBean.setSearchField("roleCode");
			codesSearchBean.setSearchValue("FL_APPROVER");
			codesSearchBean.setCaseInsensitive(true);
			searchBean.getDataFilterBeans().add(codesSearchBean);
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
		/*PmUserGroupUser pmUserGroupUser = new PmUserGroupUser();
		setUserDetails(pmUserGroupUser);
		userGroupUser.add(pmUserGroupUser);*/
		this.getManagedBean().setUserGroup(userGroup);
		this.getManagedBean().setUserGroups(userGroupUser);
		this.getManagedBean().setOrgProfileId(null);
		return "/xhtml/pm/usergroupsearch/userGroupMaintenance.xhtml";

	}

	/**
	 * reload back to user group search page
	 */
	public String backToMainPages() {
	//	this.getManagedBean().setLazy(true);
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
		System.out.println("inside  maintanace...");
		return "/xhtml/pm/usergroupsearch/userGroupMaintenance.xhtml";
	}

	public void searchUserGroupAction() {
		this.getManagedBean().setLazy(true);
		getUserGroupList().reloadData();
	}

	public void setUserGroupList(IPagingDataModel<PmUserGroup> userGroupList) {
		this.userGroupList = userGroupList;
	}

	public void userGroupListner(List<PmUser> users) {
		List<PmUserGroupUser> groupUsers = this.getManagedBean()
				.getUserGroups();
		if (null != users) {
			getManagedBean().setBrandNew(true);
			PmUserGroupUser groupUser = null;
			for (PmUser pmUser : users) {
				groupUser = new PmUserGroupUser();
				for (PmUserGroupUser userGroupUser : groupUsers) {
					if (userGroupUser.getPmUser().getIdentificationNo()
							.equalsIgnoreCase(pmUser.getIdentificationNo()))
						throw new EpException(MessageConst.ERR_900002);
				}

				groupUser.setPmUser(pmUser);
				groupUser.setPmUserGroup(this.getManagedBean().getUserGroup());
				//retrieve pm user roles
				groupUser.setPmUserRole(retrieveUserGroup(pmUser.getUserId()));
				groupUsers.add(groupUser);
			}
		}
		this.getManagedBean().setUserGroups(groupUsers);
	}

	private PmUserRole retrieveUserGroup(Long userId){
		System.out.println("userId: " + userId);
		IUserSO reqSO = null;
		PmUserRole pmUserRole = null;
		Date currentDate = new Date();
		try{
			UserReqDTO reqDTO = new UserReqDTO();
			reqSO = (IUserSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			
			SearchBean<PmUserRole> searchBean = new SearchBean<PmUserRole>();

			// Filter By - user org info
			searchBean
					.getDataFilterBeans()
					.add(new SearchFilterBean("pmUserOrg.effDate",
							SearchOperator.OPERATOR_LESS_EQUAL.value(), currentDate));

			searchBean.getDataFilterBeans().add(
					new SearchMultiFilterBean(
							new SearchFilterBean(
									"pmUserOrg.expDate", 
									SearchOperator.OPERATOR_GREATER_EQUAL.value(), 
									currentDate),
							new SearchFilterBean(
									"pmUserOrg.expDate", 
									SearchOperator.OPERATOR_IS_NULL.value(), 
									null, 
									SearchCondition.CONDITION_OR.value())));
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserOrg.pmOrgProfile.orgProfileId", 
							SearchOperator.OPERATOR_EQUALS.value(), 
							this.getManagedBean().getOrgProfileId()));

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"recordStatusFlg", 
							SearchOperator.OPERATOR_EQUALS.value(), 
							DbValueConst.RECORD_STATUS_ACTIVE));
			
			//User id
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserOrg.pmUser.userId", 
							SearchOperator.OPERATOR_EQUALS.value(), 
							userId));
			
			//Role Code
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmRole.roleCode", 
							SearchOperator.OPERATOR_EQUALS.value(), 
							RoleCode.FL_APPROVER.value()));

			
			reqDTO.setUserRoleSearchBean(searchBean);
			UserResDTO resDTO = reqSO.retrieveUserRoleBySearchBean(reqDTO);
			if(resDTO.isSuccessful() && resDTO.getUserRoleList()!=null && !resDTO.getUserRoleList().isEmpty()){
				System.out.println("not null");
				System.out.println("pmUserOrg" +resDTO.getUserRoleList().size());
				pmUserRole = resDTO.getUserRoleList().get(0);
			}
			
		}finally{
			reqSO = null;
		}
		return pmUserRole;
	}
	public void userListner() {
		orgProfileId = this.getManagedBean().getUserGroup().getPmOrgProfile()
				.getOrgProfileId();
		this.getManagedBean().setOrgProfileId(orgProfileId);
		this.getManagedBean().setSelectGrp(true);
		
		RequestContext.getCurrentInstance().update("usergrouping");
	}

	public void deleteUserGroup() {

		List<PmUserGroupUser> grpUsers = this.getManagedBean().getUserGroups();

		List<PmUserGroupUser> selectedUsrGrp = this.getManagedBean()
				.getSelectedUsrGrps();

		if (selectedUsrGrp.isEmpty()) {
			throw new EpException(MessageConst.ERR_900003);
		}
		if (grpUsers.size() == selectedUsrGrp.size()) {
			throw new EpException(MessageConst.ERR_900004);
		}

		for (PmUserGroupUser pmUserGroupUser : selectedUsrGrp) {
			grpUsers.remove(pmUserGroupUser);
		}

		this.getManagedBean().setUserGroups(grpUsers);
	}

	/**
	 * pass object into user maintenance page
	 */
	public String gotoMaintanance(PmUserGroup userGroup) {
		debugLogger("gotoMaintanance");
		this.getManagedBean().setSelectGrp(true);
		getManagedBean().setBrandNew(false);
		orgProfileId = userGroup.getPmOrgProfile().getOrgProfileId();
		List<PmUserGroupUser> groupUserList = new ArrayList<PmUserGroupUser>();
		for (PmUserGroupUser userGroupUser : userGroup.getPmUserGroupUsers()) {
			for (PmUserOrg userOrg : userGroupUser.getPmUser().getPmUserOrgs()) {
					for (PmUserRole userRole : userOrg.getPmUserRoles()) {
						if (null != userRole.getApprovalLimit()) {
							if(!groupUserList.contains(userGroupUser)){
								userGroupUser.setPmUserRole(userRole);
								groupUserList.add(userGroupUser);
							}
						}
				}
			}
		}
		System.out.println("groupUserList  :"+groupUserList.size());
		this.getManagedBean().setOrgProfileId(orgProfileId);
		this.getManagedBean().setUserGroup(userGroup);
		this.getManagedBean().setUserGroups(groupUserList);
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
		
	}

	/**
	 * save or create user group details
	 */
	public String saveUserGroup() {
		debugLogger("saveUserGroup");
		PmUserGroup userGroup = this.getManagedBean().getUserGroup();
		userGroup.setRoleCode("FL_APPROVER");
		
		IUserGroupSO userGrpSo = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserGroupSO.class);
		
//		userGroup.setGroupCode(userGroup.getGroupCode().toUpperCase());
		if (userGroup.isBrandNew()) {
			
			UserGroupReqDTO seqReqDto = new UserGroupReqDTO();
			seqReqDto.setGroupCode("GR");
			UserGroupResDTO seqResDto = userGrpSo.generateGroupCode(seqReqDto);
			userGroup.setGroupCode(seqResDto.getGroupCode());
		}

		userGroupValidationMsg(userGroup);

		List<PmUserGroupUser> pmUserGroupUsers = this.getManagedBean()
				.getUserGroups();
		
		for(PmUserGroupUser groupUser:pmUserGroupUsers){
			setUserDetails(groupUser);
		}

		userGroup.setPmUserGroupUsers(pmUserGroupUsers);
		if (userGroup.isBrandNew()) {
			setUserDetails(userGroup);
			findDuplicateGroup(userGroup);
		}

		UserGroupReqDTO reqDto = new UserGroupReqDTO();
		reqDto.setUserGroup(userGroup);

		

		UserGroupResDTO resDTO = userGrpSo.saveOrUpdateUserGroup(reqDto);

		this.getManagedBean().setUserGroup(resDTO.getUserGroup());
		if (resDTO.isSuccessful()) {
			
			System.out.println("resDTO.getUserGroup().getGroupCode() :"+resDTO.getUserGroup().getGroupCode());
			if (userGroup.isBrandNew()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.ERR_900022, new String[] {
												resDTO.getUserGroup()
												.getGroupName()},
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			} else {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.ERR_900023, new String[] {
												resDTO.getUserGroup()
												.getGroupName()},
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
		}
		if(userGroup.isBrandNew())
			return NAV_TO_MAIN;
		else
			return "userGroupMaintenance";
	}

	
	
	public String constuctConditionFields() {
		orgProfileId = this.getManagedBean().getOrgProfileId();
		if (null != orgProfileId) {
			return "pmUserOrgs.pmOrgProfile.orgProfileId^" + orgProfileId + "|"
					+ "pmUserOrgs.pmUserRoles.pmRole.roleCode^" + RoleCode.FL_APPROVER.value();
		}
		return "";
	}

	/*
	 * Verifying duplicate record 
	 * @Param PmUserGroup
	 * @return boolean
	*/
	private void findDuplicateGroup(PmUserGroup userGroup) {
		System.out.println("inside findDuplicateGroup");

		UserGroupReqDTO reqDTO = new UserGroupReqDTO();
		reqDTO.setUserGroup(userGroup);
		IUserGroupSO groupSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserGroupSO.class);
		boolean response = groupSO.checkDuplicateGroup(reqDTO);
		if (response) {
			throw new EpException(MessageConst.ERR_900001, new String[] {userGroup.getGroupCode()});
//			FacesContext.getCurrentInstance().addMessage(
//					null,
//					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
//							MessageUtils.getMessage(
//									MessageConst.ERR_900001, new String[] {
//											userGroup.getGroupCode()},
//									MessageUtils.BUNDLE_COMMON_MESSAGES,
//									new FacesScopes().getUserLocale())));
		}
	}

	public void preLoadgroupSearch() {
		System.out.println("preLoadgroupSearch ....");
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
	}

	/*
	 *  Setting update user and create user 
	 */
	public void setUserDetails(BaseEntity baseObject) {
		
//		PmUser user = new FacesScopes().getUserSessionBean();
		  if (baseObject.isBrandNew()) {
		   baseObject.setCreatedBy(1L);
		   baseObject
		     .setCreatedDate(new Timestamp(System.currentTimeMillis()));
		  }
		  baseObject.setChangedBy(1L);
	}
	
	/**
     * This method used to retrieve login user information.
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public String constructConditionFields(){
		StringBuilder sb = new StringBuilder();
		sb.append("recordStatus^1");
		PmUser user = PmCommonUtil.retrieveUserById(getFacesScopes().getUserSessionBean().getUserId(), false);
		PmOrgProfile orgProfile = null;
		if(user != null){
			orgProfile = user.getPmUserOrgs().get(0).getPmOrgProfile();
		}
		if(orgProfile != null){
			List<Long> orgProfileList = null;
			if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
				orgProfileList = new ArrayList<Long>(); 
				for(PmOrgProfile childOrgProfile : orgProfile.getChildOrgProfiles()){
					orgProfileList.add(childOrgProfile.getOrgProfileId());
				}
				sb.append("|parentOrgProfile.orgProfileId^");
				for(int i = 0; i<orgProfileList.size();i++){
					Long id = orgProfileList.get(i);
					if(i != 0){
						sb.append(",");
					}
					sb.append(id);				
				}
				sb.append("^9");
								
			}else if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())) {
				sb.append("|parentOrgProfile.orgProfileId^");
				sb.append(orgProfile.getOrgProfileId());
				
			}else if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())) {
				sb.append("|orgProfileId^");
				sb.append(orgProfile.getOrgProfileId());
			}
		}
		
		return sb.toString();
	}
}
