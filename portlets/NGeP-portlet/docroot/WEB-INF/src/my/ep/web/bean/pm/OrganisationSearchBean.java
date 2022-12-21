/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmParameter;

/**
 * Search Bean for Organisation
 * 
 * @author venugopal
 * @since 6/11/2014
 * 
 */
public class OrganisationSearchBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String orgTypeCode;
	private String orgCode;
	private String orgName;
	private String orgStatus;
	private PmOrgProfile orgProfile;
	private PmParameter orgType;
	
	public OrganisationSearchBean(){}
	
	/**
	 * @return the orgTypeCode
	 */
	public String getOrgTypeCode() {
		return orgTypeCode;
	}
	/**
	 * @param orgTypeCode the orgTypeCode to set
	 */
	public void setOrgTypeCode(String orgTypeCode) {
		this.orgTypeCode = orgTypeCode;
	}
	/**
	 * @return the orgCode
	 */
	public String getOrgCode() {
		return orgCode;
	}
	/**
	 * @param orgCode the orgCode to set
	 */
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	/**
	 * @return the orgName
	 */
	public String getOrgName() {
		return orgName;
	}
	/**
	 * @param orgName the orgName to set
	 */
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	/**
	 * @return the orgStatus
	 */
	public String getOrgStatus() {
		return orgStatus;
	}
	/**
	 * @param orgStatus the orgStatus to set
	 */
	public void setOrgStatus(String orgStatus) {
		this.orgStatus = orgStatus;
	}
	/**
	 * @return the orgType
	 */
	public PmParameter getOrgType() {
		if(orgType==null){
			orgType = new PmParameter();
		}
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
	

}
