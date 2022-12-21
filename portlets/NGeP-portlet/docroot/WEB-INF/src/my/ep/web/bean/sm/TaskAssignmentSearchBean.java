/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.sm;

import java.io.Serializable;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmPersonnel;

/**
 * Search Bean for Task Assignment
 * 
 * @author KK LIM
 * @since 13/12/2016
 *
 */
public class TaskAssignmentSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private SmPersonnel user;
	private PmParameter orgType;
	private String docType;
	private String docNo;
	private PmUser excludeUser;
	private boolean isSearched;
	private String companyName;
	
	public TaskAssignmentSearchBean(){}
	
	/**
	 * @return the user
	 */
	public SmPersonnel getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(SmPersonnel user) {
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
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}
	/**
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	
}
