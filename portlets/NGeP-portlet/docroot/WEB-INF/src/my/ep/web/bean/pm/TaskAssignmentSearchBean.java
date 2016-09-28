/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;
import java.util.List;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;

/**
 * Search Bean for Task Assignment
 * 
 * @author RAIN POH
 * @since 15/9/2014
 *
 */
public class TaskAssignmentSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private PmOrgProfile orgProfile;
	private PmUser user;
	private PmParameter orgType;
	private PmParameterDesc docType;
	private String docNo;
	private List<String> selectedRoles;
	private PmUser excludeUser;
	private boolean isSearched;
	
//	private PmOrgProfile ptj;
//	private Long ministryId;
//	private Long ptjId;
//	private String userId;
	
	public TaskAssignmentSearchBean(){}
	
	/**
	 * @return the user
	 */
	public PmUser getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(PmUser user) {
		this.user = user;
	}
	/**
	 * @return the orgType
	 */
	public PmParameter getOrgType() {
		if(orgType==null)
			orgType = new PmParameter();
		return orgType;
	}
	/**
	 * @param orgType the orgType to set
	 */
	public void setOrgType(PmParameter orgType) {
		this.orgType = orgType;
	}
	/**
	 * @return the orgProfile
	 */
	public PmOrgProfile getOrgProfile() {
		return orgProfile;
	}
	/**
	 * @param orgProfile the orgProfile to set
	 */
	public void setOrgProfile(PmOrgProfile orgProfile) {
		this.orgProfile = orgProfile;
	}
	/**
	 * @return the docType
	 */
	public PmParameterDesc getDocType() {
		return docType;
	}
	/**
	 * @param docType the docType to set
	 */
	public void setDocType(PmParameterDesc docType) {
		this.docType = docType;
	}
	/**
	 * @return the docNo
	 */
	public String getDocNo() {
		return docNo;
	}
	/**
	 * @param docNo the docNo to set
	 */
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}
	/**
	 * @return the selectedRoles
	 */
	public List<String> getSelectedRoles() {
		return selectedRoles;
	}
	/**
	 * @param selectedRoles the selectedRoles to set
	 */
	public void setSelectedRoles(List<String> selectedRoles) {
		this.selectedRoles = selectedRoles;
	}
	/**
	 * @return the excludeUser
	 */
	public PmUser getExcludeUser() {
		return excludeUser;
	}
	/**
	 * @param excludeUser the excludeUser to set
	 */
	public void setExcludeUser(PmUser excludeUser) {
		this.excludeUser = excludeUser;
	}

	/**
	 * @return the isSearched
	 */
	public boolean isSearched() {
		return isSearched;
	}

	/**
	 * @param isSearched the isSearched to set
	 */
	public void setSearched(boolean isSearched) {
		this.isSearched = isSearched;
	}

	
}
