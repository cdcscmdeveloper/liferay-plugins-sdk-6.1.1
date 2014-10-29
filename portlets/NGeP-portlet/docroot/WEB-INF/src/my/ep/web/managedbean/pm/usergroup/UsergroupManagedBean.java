package my.ep.web.managedbean.pm.usergroup;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "usergroupManagedBean")
@SessionScoped
public class UsergroupManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PmUserGroup userGroup;
	
	private List<PmUserGroupUser> userGroups;
	private List<PmUser> users;
	private List<PmUserGroupUser> selectedUsrGrps;
	private String userName;	
	private String role;	
	private String ptjCode;	
	private String ptjName;	
	private String groupName;
	private String groupCode;
	private String status;
	
	private String orgConditionField;	
	private PmOrgProfile orgProfile;

	public PmUserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(PmUserGroup userGroup) {
		this.userGroup = userGroup;
	}

	public List<PmUserGroupUser> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<PmUserGroupUser> userGroups) {
		this.userGroups = userGroups;
	}

	public List<PmUser> getUsers() {
		return users;
	}

	public void setUsers(List<PmUser> users) {
		this.users = users;
	}

	public List<PmUserGroupUser> getSelectedUsrGrps() {
		return selectedUsrGrps;
	}

	public void setSelectedUsrGrps(List<PmUserGroupUser> selectedUsrGrps) {
		this.selectedUsrGrps = selectedUsrGrps;
	}

	public String getPtjCode() {
		return ptjCode;
	}

	public void setPtjCode(String ptjCode) {
		this.ptjCode = ptjCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPtjName() {
		return ptjName;
	}

	public void setPtjName(String ptjName) {
		this.ptjName = ptjName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrgConditionField() {
		return orgConditionField;
	}

	public void setOrgConditionField(String orgConditionField) {
		this.orgConditionField = orgConditionField;
	}

	public PmOrgProfile getOrgProfile() {
		return orgProfile;
	}

	public void setOrgProfile(PmOrgProfile orgProfile) {
		this.orgProfile = orgProfile;
	}
		
		
}

