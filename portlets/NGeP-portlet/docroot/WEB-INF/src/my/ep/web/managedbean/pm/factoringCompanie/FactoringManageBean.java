/*
 * Copyright © 2015 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.factoringCompanie;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Factoring Companies
 * 
 * @author Zeinab
 * @since 09/02/2015
 * 
 */
@ManagedBean(name = "FactoringMB")
@SessionScoped
public class FactoringManageBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<PmFinancialOrg> factoringCompanies;

	public FactoringManageBean() {

	}

	/**
	 * @return the factoringCompanies
	 */
	public List<PmFinancialOrg> getFactoringCompanies() {
		return factoringCompanies;
	}

	/**
	 * @param factoringCompanies the factoringCompanies to set
	 */
	public void setFactoringCompanies(List<PmFinancialOrg> factoringCompanies) {
		this.factoringCompanies = factoringCompanies;
	}
	

}
