package my.ep.web.managedbean.pm.userprofile;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmUser;
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

	private PmUser pmUser;
	
    // user group search String
	private String userNames;
	
	private String roleNames;
	
	private String ptjCodes;
	
	private String ptjNames;

	// List of user group list
	private List<PmUser> userGroup;
	
	
	public PmUser getPmUser() {
		return pmUser;
	}

	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}

	public String getUserNames() {
		return userNames;
	}

	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}

	public String getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(String roleNames) {
		this.roleNames = roleNames;
	}	

	public String getPtjCodes() {
		return ptjCodes;
	}

	public void setPtjCodes(String ptjCodes) {
		this.ptjCodes = ptjCodes;
	}

	public String getPtjNames() {
		return ptjNames;
	}

	public void setPtjNames(String ptjNames) {
		this.ptjNames = ptjNames;
	}

	public List<PmUser> getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(List<PmUser> userGroup) {
		this.userGroup = userGroup;
	}
	

	
}
