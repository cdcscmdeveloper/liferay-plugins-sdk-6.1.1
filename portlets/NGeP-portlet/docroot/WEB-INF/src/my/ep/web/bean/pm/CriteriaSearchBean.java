/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

/**
 * Search Bean for Rejection Reason
 * 
 * @author RAIN POH
 * @since 17/7/2014
 * 
 */
public class CriteriaSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String criteriaCode;
	private String criteriaName;
	private String status;
	public CriteriaSearchBean() {
	}

	
	
	public String getCriteriaCode() {
		return criteriaCode;
	}
	public void setCriteriaCode(String criteriaCode) {
		this.criteriaCode = criteriaCode;
	}



	public String getCriteriaName() {
		return criteriaName;
	}



	public void setCriteriaName(String criteriaName) {
		this.criteriaName = criteriaName;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	
	
}