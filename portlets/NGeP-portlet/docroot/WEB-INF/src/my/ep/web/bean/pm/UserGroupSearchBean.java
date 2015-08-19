/**
 * 
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

/**
 * @author DK
 *
 */
public class UserGroupSearchBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userName;
	
	private String role;
	
	private String ptjCode;
	
	private String ptjName;
	
	private String status;
	
	private String groupCode;
	
	private String groupName;
	
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
	
	public String getPtjCode() {
		return ptjCode;
	}
	
	public void setPtjCode(String ptjCode) {
		this.ptjCode = ptjCode;
	}
	
	public String getPtjName() {
		return ptjName;
	}
	
	public void setPtjName(String ptjName) {
		this.ptjName = ptjName;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
}
