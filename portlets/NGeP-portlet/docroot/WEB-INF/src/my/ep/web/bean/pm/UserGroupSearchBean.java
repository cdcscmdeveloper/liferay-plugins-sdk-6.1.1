/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

/**
 * Search Bean for User Group
 * 
 * @author DK
 * @since 15/10/2014
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
	
	public UserGroupSearchBean(){}

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
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the ptjCode
	 */
	public String getPtjCode() {
		return ptjCode;
	}

	/**
	 * @param ptjCode the ptjCode to set
	 */
	public void setPtjCode(String ptjCode) {
		this.ptjCode = ptjCode;
	}

	/**
	 * @return the ptjName
	 */
	public String getPtjName() {
		return ptjName;
	}

	/**
	 * @param ptjName the ptjName to set
	 */
	public void setPtjName(String ptjName) {
		this.ptjName = ptjName;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the groupCode
	 */
	public String getGroupCode() {
		return groupCode;
	}

	/**
	 * @param groupCode the groupCode to set
	 */
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
}
