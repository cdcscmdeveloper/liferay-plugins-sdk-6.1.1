/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmZone;

/**
 * Search Bean for Support Document 
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
public class TaskAssignmentSearchBean {
	private Long ministryId;
	private Long ptjId;
	private String userId;
	/**
	 * @return the ministryId
	 */
	public Long getMinistryId() {
		return ministryId;
	}
	/**
	 * @param ministryId the ministryId to set
	 */
	public void setMinistryId(Long ministryId) {
		this.ministryId = ministryId;
	}
	/**
	 * @return the ptjId
	 */
	public Long getPtjId() {
		return ptjId;
	}
	/**
	 * @param ptjId the ptjId to set
	 */
	public void setPtjId(Long ptjId) {
		this.ptjId = ptjId;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	
}
