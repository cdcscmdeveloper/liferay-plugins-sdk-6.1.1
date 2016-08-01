/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

/**
 * Role List
 * 
 * @author RAIN POH
 * @since 6/8/2014
 *
 */
public class RoleList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String roleName;
	
	private String roleTypes;	

	private String apprLimit;	
	
	private String roleStatus;
	
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleTypes() {
		return roleTypes;
	}

	public void setRoleTypes(String roleTypes) {
		this.roleTypes = roleTypes;
	}

	public String getApprLimit() {
		return apprLimit;
	}

	public void setApprLimit(String apprLimit) {
		this.apprLimit = apprLimit;
	}

	public String getRoleStatus() {
		return roleStatus;
	}

	public void setRoleStatus(String roleStatus) {
		this.roleStatus = roleStatus;
	}
	
	
}
