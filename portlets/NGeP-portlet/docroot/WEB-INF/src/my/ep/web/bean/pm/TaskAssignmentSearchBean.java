/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;

/**
 * Search Bean for Support Document 
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
public class TaskAssignmentSearchBean {
	
	private PmOrgProfile orgProfile;
	private PmUser user;
	private PmParameter orgType;
	
//	private PmOrgProfile ptj;
//	private Long ministryId;
//	private Long ptjId;
//	private String userId;
	

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

	
}
