/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.supplierperformanceCriteria;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmSupplierCriteria;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for SupplierCriteria
 * 
 * 
 */
@ManagedBean(name = "supplierCriteriaManagedBean")
@SessionScoped
public class SupplierCriteriaManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private PmSupplierCriteria pmSupplierCriteria;

	private String supplierCriteriaCodes;

	private String supplierCriteriaNames;
	
	

	private Long status;

	private boolean isLazy;

	public PmSupplierCriteria getPmSupplierCriteria() {
		return pmSupplierCriteria;
	}

	public void setPmSupplierCriteria(PmSupplierCriteria pmSupplierCriteria) {
		this.pmSupplierCriteria = pmSupplierCriteria;
	}

	public String getSupplierCriteriaCodes() {
		return supplierCriteriaCodes;
	}

	public void setSupplierCriteriaCodes(String supplierCriteriaCodes) {
		this.supplierCriteriaCodes = supplierCriteriaCodes;
	}

	public String getSupplierCriteriaNames() {
		return supplierCriteriaNames;
	}

	public void setSupplierCriteriaNames(String supplierCriteriaNames) {
		this.supplierCriteriaNames = supplierCriteriaNames;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public boolean isLazy() {
		return isLazy;
	}

	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	
	
	
	
	
	
}