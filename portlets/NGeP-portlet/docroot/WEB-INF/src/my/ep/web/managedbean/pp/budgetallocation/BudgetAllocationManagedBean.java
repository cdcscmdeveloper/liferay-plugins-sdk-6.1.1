/*
 * Copyright © 2013 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pp.budgetallocation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pp.budgetallocation.PpBudget;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAlloc;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtl;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocOrg;
import my.ep.web.bean.pp.BudgetAllocSearchBean;
import my.ep.web.bean.pp.ProcurementPlanSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "budgetAllocManagedBean")
@SessionScoped
public class BudgetAllocationManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	
	private Boolean amanahFlag;

	private Map<Long, BigDecimal> budgetAllocDtlMapper = new HashMap<Long, BigDecimal>();

	private List <PpBudget> budgetList; 

	private PpBudgetAlloc selectedBudgetAllocation;
	
	private PpBudgetAllocDtl selectedBudgetAllocDtl;
	
	private PpBudgetAllocOrg selectedBudgetAllocOrg;

	private List <PpBudgetAlloc> budgetAllocationList;

	private String viewMode;
	
	private String bpmTaskId; 
	
	private List <PmOrgProfile> currentProfiles;
	
//	private PpBudgetAlloc retrieveBudgetAlloc;
	
	private String loginUser;
	
	private boolean confirm;
	
	private BudgetAllocSearchBean budgetAllocSearchBean;
	
	/**
	 * @return the amanahFlag
	 */
	public Boolean getAmanahFlag() {
		return amanahFlag;
	}

	/**
	 * @param amanahFlag the amanahFlag to set
	 */
	public void setAmanahFlag(Boolean amanahFlag) {
		this.amanahFlag = amanahFlag;
	}

	/**
	 * @return the loginUser
	 */
	public String getLoginUser() {
		return loginUser;
	}

	/**
	 * @param loginUser the loginUser to set
	 */
	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}
	

	/**
	 * @return the confirm
	 */
	public boolean isConfirm() {
		return confirm;
	}

	/**
	 * @param confirm the confirm to set
	 */
	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}

	/**
	 * @return the budgetList
	 */
	public List<PpBudget> getBudgetList() {
		return budgetList;
	}

	/**
	 * @param budgetList the budgetList to set
	 */
	public void setBudgetList(List<PpBudget> budgetList) {
		this.budgetList = budgetList;
	}

	/**
	 * @return the bpmTaskId
	 */
	public String getBpmTaskId() {
		return bpmTaskId;
	}

	/**
	 * @param bpmTaskId the bpmTaskId to set
	 */
	public void setBpmTaskId(String bpmTaskId) {
		this.bpmTaskId = bpmTaskId;
	}
	
	/**
	 * @return the selectedBudgetAllocation
	 */
	public PpBudgetAlloc getSelectedBudgetAllocation() {
		if(selectedBudgetAllocation==null)
			selectedBudgetAllocation = new PpBudgetAlloc();
		return selectedBudgetAllocation;
	}

	/**
	 * @param selectedBudgetAllocation the selectedBudgetAllocation to set
	 */
	public void setSelectedBudgetAllocation(PpBudgetAlloc selectedBudgetAllocation) {
		this.selectedBudgetAllocation = selectedBudgetAllocation;
	}


	/**
	 * @return the selectedBudgetAllocDtl
	 */
	public PpBudgetAllocDtl getSelectedBudgetAllocDtl() {
		if(selectedBudgetAllocDtl==null)
			selectedBudgetAllocDtl = new PpBudgetAllocDtl();
		return selectedBudgetAllocDtl;
	}

	/**
	 * @param selectedBudgetAllocDtl the selectedBudgetAllocDtl to set
	 */
	public void setSelectedBudgetAllocDtl(PpBudgetAllocDtl selectedBudgetAllocDtl) {
		this.selectedBudgetAllocDtl = selectedBudgetAllocDtl;
	}

	
	/**
	 * @return the selectedBudgetAllocOrg
	 */
	public PpBudgetAllocOrg getSelectedBudgetAllocOrg() {
		if(selectedBudgetAllocOrg==null)
			selectedBudgetAllocOrg = new PpBudgetAllocOrg();
		return selectedBudgetAllocOrg;
	}

	/**
	 * @param selectedBudgetAllocOrg the selectedBudgetAllocOrg to set
	 */
	public void setSelectedBudgetAllocOrg(PpBudgetAllocOrg selectedBudgetAllocOrg) {
		this.selectedBudgetAllocOrg = selectedBudgetAllocOrg;
	}

	/**
	 * @return the budgetAllocationList
	 */
	public List<PpBudgetAlloc> getBudgetAllocationList() {
		return budgetAllocationList;
	}

	/**
	 * @param budgetAllocationList the budgetAllocationList to set
	 */
	public void setBudgetAllocationList(List<PpBudgetAlloc> budgetAllocationList) {
		this.budgetAllocationList = budgetAllocationList;
	}
	
	/**
	 * @return the viewMode
	 */
	public String getViewMode() {
		return viewMode;
	}

	/**
	 * @param viewMode the viewMode to set
	 */
	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}

	
	/**
	 * @return the budgetAllocDtlMapper
	 */
	public Map<Long, BigDecimal> getBudgetAllocDtlMapper() {
		return budgetAllocDtlMapper;
	}

	/**
	 * @param budgetAllocDtlMapper the budgetAllocDtlMapper to set
	 */
	public void setBudgetAllocDtlMapper(Map<Long, BigDecimal> budgetAllocDtlMapper) {
		this.budgetAllocDtlMapper = budgetAllocDtlMapper;
	}

	/**
	 * @return the currentProfiles
	 */
	public List<PmOrgProfile> getCurrentProfiles() {
		return currentProfiles;
	}

	/**
	 * @param currentProfiles the currentProfiles to set
	 */
	public void setCurrentProfiles(List<PmOrgProfile> currentProfiles) {
		this.currentProfiles = currentProfiles;
	}

	/**
	 * @return the budgetAllocSearchBean
	 */
	public BudgetAllocSearchBean getBudgetAllocSearchBean() {
		if(budgetAllocSearchBean==null)
			budgetAllocSearchBean = new BudgetAllocSearchBean();
		return budgetAllocSearchBean;
	}

	/**
	 * @param budgetAllocSearchBean the budgetAllocSearchBean to set
	 */
	public void setBudgetAllocSearchBean(BudgetAllocSearchBean budgetAllocSearchBean) {
		this.budgetAllocSearchBean = budgetAllocSearchBean;
	}



	
}
