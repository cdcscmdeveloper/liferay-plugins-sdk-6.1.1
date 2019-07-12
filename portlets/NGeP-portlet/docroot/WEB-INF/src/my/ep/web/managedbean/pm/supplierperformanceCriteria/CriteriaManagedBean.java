/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.supplierperformanceCriteria;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmCriteria;
import my.ep.web.bean.pm.CriteriaSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for SupplierCriteria
 * 
 * 
 */
@ManagedBean(name = "criteriaManagedBean")
@SessionScoped
public class CriteriaManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private CriteriaSearchBean criteriaSearchBean;
	private PmCriteria pmCriteria;
	private PmCriteria selectedCriteria;
	private boolean isLazy = false;
	
	

	public CriteriaSearchBean getCriteriaSearchBean() {
		if(criteriaSearchBean==null)
			criteriaSearchBean = new CriteriaSearchBean();
		return criteriaSearchBean;
	}

	
	public void setCriteriaSearchBean(CriteriaSearchBean criteriaSearchBean) {
		this.criteriaSearchBean = criteriaSearchBean;
	}

	
	public PmCriteria getSelectedCriteria() {
		if(selectedCriteria==null)
			selectedCriteria = new PmCriteria();
		return selectedCriteria;
	}

	/**
	 * @param selectedCriteria the selectedCriteria to set
	 */
	public void setSelectedCriteria(PmCriteria selectedCriteria) {
		this.selectedCriteria = selectedCriteria;
	}


	public PmCriteria getPmCriteria() {
		return pmCriteria;
	}


	public boolean isLazy() {
		return isLazy;
	}


	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

}