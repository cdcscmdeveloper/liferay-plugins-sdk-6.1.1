/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import my.ep.persistence.entity.pm.common.PmParameter;
/**
 * Search Bean for Rejection Reason
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
public class ReasonSearchBean {
	private PmParameter reasonCategoryParam;
	private Long reasonCategory;
	private String reasonCode;
	private String reasonDesc;
	private Long applicationType;
	private Long companyType;
	private Long supplierType;
	private Long restrType;
	private Long status;
	

	
	
	/**
	 * @return the reasonCategoryParam
	 */
	public PmParameter getReasonCategoryParam() {
		if(reasonCategoryParam==null)
			reasonCategoryParam = new PmParameter();
		return reasonCategoryParam;
	}
	/**
	 * @param reasonCategoryParam the reasonCategoryParam to set
	 */
	public void setReasonCategoryParam(PmParameter reasonCategoryParam) {
		this.reasonCategoryParam = reasonCategoryParam;
	}
	/**
	 * @return the reasonCode
	 */
	public String getReasonCode() {
		return reasonCode;
	}
	/**
	 * @return the reasonCategory
	 */
	public Long getReasonCategory() {
		return reasonCategory;
	}
	/**
	 * @param reasonCategory the reasonCategory to set
	 */
	public void setReasonCategory(Long reasonCategory) {
		this.reasonCategory = reasonCategory;
	}
	/**
	 * @param reasonCode the reasonCode to set
	 */
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}
	/**
	 * @return the reasonDesc
	 */
	public String getReasonDesc() {
		return reasonDesc;
	}
	/**
	 * @param reasonDesc the reasonDesc to set
	 */
	public void setReasonDesc(String reasonDesc) {
		this.reasonDesc = reasonDesc;
	}
	/**
	 * @return the applicationType
	 */
	public Long getApplicationType() {
		return applicationType;
	}
	/**
	 * @param applicationType the applicationType to set
	 */
	public void setApplicationType(Long applicationType) {
		this.applicationType = applicationType;
	}
	/**
	 * @return the companyType
	 */
	public Long getCompanyType() {
		return companyType;
	}
	/**
	 * @param companyType the companyType to set
	 */
	public void setCompanyType(Long companyType) {
		this.companyType = companyType;
	}
	/**
	 * @return the supplierType
	 */
	public Long getSupplierType() {
		return supplierType;
	}
	/**
	 * @param supplierType the supplierType to set
	 */
	public void setSupplierType(Long supplierType) {
		this.supplierType = supplierType;
	}
	/**
	 * @return the status
	 */
	public Long getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Long status) {
		this.status = status;
	}
	/**
	 * @return the restrType
	 */
	public Long getRestrType() {
		return restrType;
	}
	/**
	 * @param restrType the restrType to set
	 */
	public void setRestrType(Long restrType) {
		this.restrType = restrType;
	}
	
	
}
