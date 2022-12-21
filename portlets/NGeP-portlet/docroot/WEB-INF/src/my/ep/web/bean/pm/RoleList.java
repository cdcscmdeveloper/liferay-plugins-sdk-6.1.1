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

	private static final long serialVersionUID = 1L;
	private String roleName;	
	private String roleTypes;	
	private String apprLimit;	
	private String roleStatus;
	
	public RoleList(){}

	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the roleTypes
	 */
	public String getRoleTypes() {
		return roleTypes;
	}

	/**
	 * @param roleTypes the roleTypes to set
	 */
	public void setRoleTypes(String roleTypes) {
		this.roleTypes = roleTypes;
	}

	/**
	 * @return the apprLimit
	 */
	public String getApprLimit() {
		return apprLimit;
	}

	/**
	 * @param apprLimit the apprLimit to set
	 */
	public void setApprLimit(String apprLimit) {
		this.apprLimit = apprLimit;
	}

	/**
	 * @return the roleStatus
	 */
	public String getRoleStatus() {
		return roleStatus;
	}

	/**
	 * @param roleStatus the roleStatus to set
	 */
	public void setRoleStatus(String roleStatus) {
		this.roleStatus = roleStatus;
	}
	
	
}
