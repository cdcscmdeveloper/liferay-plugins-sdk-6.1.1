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
	private String role;
	private UsergroupSearchBean usergroupSearchBean;

	private String orgConditionField;
	private PmOrgProfile orgProfile;

	private String orgId;
	
	private Long orgProfileId;

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
		return usergroupSearchBean.getPtjCode();
	}

	public void setPtjCode(String ptjCode) {
		this.usergroupSearchBean.setPtjCode(ptjCode);
	}

	public String getUserName() {
		return usergroupSearchBean.getUserName();
	}

	public void setUserName(String userName) {
		this.usergroupSearchBean.setUserName(userName);
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPtjName() {
		return usergroupSearchBean.getPtjName();
	}

	public void setPtjName(String ptjName) {
		this.usergroupSearchBean.setPtjName(ptjName);
	}

	public String getGroupName() {
		return usergroupSearchBean.getGroupName();
	}

	public void setGroupName(String groupName) {
		this.usergroupSearchBean.setGroupName(groupName);
	}

	public String getGroupCode() {
		return usergroupSearchBean.getGroupCode();
	}

	public void setGroupCode(String groupCode) {
		this.usergroupSearchBean.setGroupCode(groupCode);
	}

	public Long getStatus() {
		return usergroupSearchBean.getStatus();
	}

	public void setStatus(Long status) {
		this.usergroupSearchBean.setStatus(status);
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

	public UsergroupSearchBean getUsergroupSearchBean() {
		return usergroupSearchBean;
	}

	public void setUsergroupSearchBean(UsergroupSearchBean usergroupSearchBean) {
		this.usergroupSearchBean = usergroupSearchBean;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public Long getOrgProfileId() {
		return orgProfileId;
	}

	public void setOrgProfileId(Long orgProfileId) {
		this.orgProfileId = orgProfileId;
	}

}
