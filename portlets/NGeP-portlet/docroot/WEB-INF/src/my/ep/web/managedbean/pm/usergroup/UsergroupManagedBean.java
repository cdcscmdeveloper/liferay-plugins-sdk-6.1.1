/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.usergroup;

import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.web.bean.pm.UserGroupSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for User Group
 * 
 * @author DK
 * @since 27/10/2014
 */
@ManagedBean(name = "usergroupManagedBean")
@SessionScoped
public class UsergroupManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private PmUserGroup userGroup;
	private List<PmUserGroupUser> userGroups;
	private List<PmUser> users;
	private List<PmUserGroupUser> selectedUsrGrps;
	private List<PmUserGroup> pmUserGroupList;
	private String role;
	private UserGroupSearchBean usergroupSearchBean = new UserGroupSearchBean();
	private PmOrgProfile pmOrgProfile;
	private String orgConditionField;
	private PmOrgProfile orgProfile;
	private PmUserGroupUser selectedDeleteGroupUser;
	private String orgId;
	private Long orgProfileId;
	private boolean isLazy = false;
	private String errorMessage;
	private boolean brandNew = false;
	private boolean selectGrp = false;
	private boolean disabledOrg;
	private HashMap<Long,Integer> hmGroupUserFlagStatDb = null;
	
	//#27629 start
	private String userName;
	private String identificationNo;
	private List<PmUserGroupUser> userGroupUserSearch;
	private boolean userSearch; //#29225 rename to avoid property not found in jsf
	private String recordSearchStatusFlg;
	//27629 end

	public UsergroupManagedBean() {
	}

	/**
	 * @return the userGroup
	 */
	public PmUserGroup getUserGroup() {
		return userGroup;
	}

	/**
	 * @param userGroup
	 *            the userGroup to set
	 */
	public void setUserGroup(PmUserGroup userGroup) {
		this.userGroup = userGroup;
	}

	/**
	 * @return the userGroups
	 */
	public List<PmUserGroupUser> getUserGroups() {
		return userGroups;
	}

	/**
	 * @param userGroups
	 *            the userGroups to set
	 */
	public void setUserGroups(List<PmUserGroupUser> userGroups) {
		this.userGroups = userGroups;
	}

	/**
	 * @return the users
	 */
	public List<PmUser> getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            the users to set
	 */
	public void setUsers(List<PmUser> users) {
		this.users = users;
	}

	/**
	 * @return the selectedUsrGrps
	 */
	public List<PmUserGroupUser> getSelectedUsrGrps() {
		return selectedUsrGrps;
	}

	/**
	 * @param selectedUsrGrps
	 *            the selectedUsrGrps to set
	 */
	public void setSelectedUsrGrps(List<PmUserGroupUser> selectedUsrGrps) {
		this.selectedUsrGrps = selectedUsrGrps;
	}

	/**
	 * @return the pmUserGroupList
	 */
	public List<PmUserGroup> getPmUserGroupList() {
		return pmUserGroupList;
	}

	/**
	 * @param pmUserGroupList
	 *            the pmUserGroupList to set
	 */
	public void setPmUserGroupList(List<PmUserGroup> pmUserGroupList) {
		this.pmUserGroupList = pmUserGroupList;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	

	/**
	 * @return the pmOrgProfile
	 */
	public PmOrgProfile getPmOrgProfile() {
		return pmOrgProfile;
	}

	/**
	 * @param pmOrgProfile
	 *            the pmOrgProfile to set
	 */
	public void setPmOrgProfile(PmOrgProfile pmOrgProfile) {
		this.pmOrgProfile = pmOrgProfile;
	}

	/**
	 * @return the orgConditionField
	 */
	public String getOrgConditionField() {
		return orgConditionField;
	}

	/**
	 * @param orgConditionField
	 *            the orgConditionField to set
	 */
	public void setOrgConditionField(String orgConditionField) {
		this.orgConditionField = orgConditionField;
	}

	/**
	 * @return the orgProfile
	 */
	public PmOrgProfile getOrgProfile() {
		return orgProfile;
	}

	/**
	 * @param orgProfile
	 *            the orgProfile to set
	 */
	public void setOrgProfile(PmOrgProfile orgProfile) {
		this.orgProfile = orgProfile;
	}

	/**
	 * @return the orgId
	 */
	public String getOrgId() {
		return orgId;
	}

	/**
	 * @param orgId
	 *            the orgId to set
	 */
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	/**
	 * @return the orgProfileId
	 */
	public Long getOrgProfileId() {
		return orgProfileId;
	}

	/**
	 * @param orgProfileId
	 *            the orgProfileId to set
	 */
	public void setOrgProfileId(Long orgProfileId) {
		this.orgProfileId = orgProfileId;
	}

	/**
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}

	/**
	 * @param isLazy
	 *            the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage
	 *            the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the brandNew
	 */
	public boolean isBrandNew() {
		return brandNew;
	}

	/**
	 * @param brandNew
	 *            the brandNew to set
	 */
	public void setBrandNew(boolean brandNew) {
		this.brandNew = brandNew;
	}

	/**
	 * @return the selectGrp
	 */
	public boolean isSelectGrp() {
		return selectGrp;
	}

	/**
	 * @param selectGrp
	 *            the selectGrp to set
	 */
	public void setSelectGrp(boolean selectGrp) {
		this.selectGrp = selectGrp;
	}

	/**
	 * @return the selectedDeleteGroupUser
	 */
	public PmUserGroupUser getSelectedDeleteGroupUser() {
		return selectedDeleteGroupUser;
	}

	/**
	 * @param selectedDeleteGroupUser
	 *            the selectedDeleteGroupUser to set
	 */
	public void setSelectedDeleteGroupUser(
			PmUserGroupUser selectedDeleteGroupUser) {
		this.selectedDeleteGroupUser = selectedDeleteGroupUser;
	}

	/**
	 * @return the disabledOrg
	 */
	public boolean isDisabledOrg() {
		return disabledOrg;
	}

	/**
	 * @param disabledOrg
	 *            the disabledOrg to set
	 */
	public void setDisabledOrg(boolean disabledOrg) {
		this.disabledOrg = disabledOrg;
	}

	/**
	 * @return the usergroupSearchBean
	 */
	public UserGroupSearchBean getUsergroupSearchBean() {
		return usergroupSearchBean;
	}

	/**
	 * @param usergroupSearchBean the usergroupSearchBean to set
	 */
	public void setUsergroupSearchBean(UserGroupSearchBean usergroupSearchBean) {
		this.usergroupSearchBean = usergroupSearchBean;
	}

	/**
	 * @return the hmGroupUserFlagStatDb
	 */
	public HashMap<Long, Integer> getHmGroupUserFlagStatDb() {
		return hmGroupUserFlagStatDb;
	}

	/**
	 * @param hmGroupUserFlagStatDb the hmGroupUserFlagStatDb to set
	 */
	public void setHmGroupUserFlagStatDb(
			HashMap<Long, Integer> hmGroupUserFlagStatDb) {
		this.hmGroupUserFlagStatDb = hmGroupUserFlagStatDb;
	}
	
	//#27628 start
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the identificationNo
	 */
	public String getIdentificationNo() {
		return identificationNo;
	}

	/**
	 * @param identificationNo the identificationNo to set
	 */
	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}
	
	/**
	 * @return the userGroupUserSearch
	 */
	public List<PmUserGroupUser> getUserGroupUserSearch() {
		return userGroupUserSearch;
	}

	/**
	 * @param userGroupUserSearch the userGroupUserSearch to set
	 */
	public void setUserGroupUserSearch(List<PmUserGroupUser> userGroupUserSearch) {
		this.userGroupUserSearch = userGroupUserSearch;
	}
	
	/**
	 * @return the isUserSearch
	 */
	public boolean isUserSearch() {
		return userSearch;
	}

	/**
	 * @param isUserSearch the isUserSearch to set
	 */
	public void setUserSearch(boolean userSearch) {
		this.userSearch = userSearch;
	}
	
	public String getRecordSearchStatusFlg() {
		return recordSearchStatusFlg;
	}

	public void setRecordSearchStatusFlg(String recordSearchStatusFlg) {
		this.recordSearchStatusFlg = recordSearchStatusFlg;
	}
	
	//#27629 end

}
