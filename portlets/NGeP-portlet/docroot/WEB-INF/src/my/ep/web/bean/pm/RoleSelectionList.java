/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

/**
 * Role Selection List
 * 
 * @author RAIN POH
 * @since 6/8/2014
 * 
 */
public class RoleSelectionList implements Serializable {

	private static final long serialVersionUID = 1L;
	private String RolesNames;
	private String ApproverEtc;

	public RoleSelectionList() {
	}

	/**
	 * @return the rolesNames
	 */
	public String getRolesNames() {
		return RolesNames;
	}

	/**
	 * @param rolesNames
	 *            the rolesNames to set
	 */
	public void setRolesNames(String rolesNames) {
		RolesNames = rolesNames;
	}

	/**
	 * @return the approverEtc
	 */
	public String getApproverEtc() {
		return ApproverEtc;
	}

	/**
	 * @param approverEtc
	 *            the approverEtc to set
	 */
	public void setApproverEtc(String approverEtc) {
		ApproverEtc = approverEtc;
	}

}
