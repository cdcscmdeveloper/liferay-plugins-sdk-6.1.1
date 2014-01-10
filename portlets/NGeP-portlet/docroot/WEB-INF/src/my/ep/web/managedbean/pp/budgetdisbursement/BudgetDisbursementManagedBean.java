/**
 * Copyright © 2012 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pp.budgetdisbursement;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.Transient;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pp.budgetallocation.PpBudget;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtl;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetDtl;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetTran;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean For Budget Disbursement
 * 
 * @author RainPoh
 * @since 19/12/2013
 */
@ManagedBean(name = "budgetDisburseManagedBean")
@SessionScoped
public class BudgetDisbursementManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;

	private List <PpBudget> budgetList; 

//	private PmOrgProfile currentUser;
	
	private PpBudget selectedPpBudget;

	private String viewMode;
	
	private Map<Long, BigDecimal> selectedGoMapper = new HashMap<Long, BigDecimal>();
	
	private PpBudgetDtl selectedPpBudgetDtl;
	
//	private PpBudgetTran selectedPpBudgetTran;
	
//	private boolean enableChildDetail;
	


	private boolean verify;
	
	private boolean verifyRequired;
	
	private boolean isConfirm;
	

	
	/**
	 * @return the isConfirm
	 */
	public boolean isConfirm() {
		return isConfirm;
	}

	/**
	 * @param isConfirm the isConfirm to set
	 */
	public void setConfirm(boolean isConfirm) {
		this.isConfirm = isConfirm;
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

//	/**
//	 * @return the currentUser
//	 */
//	public PmOrgProfile getCurrentUser() {
//		return currentUser;
//	}
//
//	/**
//	 * @param currentUser the currentUser to set
//	 */
//	public void setCurrentUser(PmOrgProfile currentUser) {
//		this.currentUser = currentUser;
//	}
	
	/**
	 * @return the selectedPpBudget
	 */
	public PpBudget getSelectedPpBudget() {
		if(selectedPpBudget==null)
			selectedPpBudget = new PpBudget();
		return selectedPpBudget;
	}

	/**
	 * @param selectedPpBudget the selectedPpBudget to set
	 */
	public void setSelectedPpBudget(PpBudget selectedPpBudget) {
		this.selectedPpBudget = selectedPpBudget;
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
	 * @return the selectedGoMapper
	 */
	public Map<Long, BigDecimal> getSelectedGoMapper() {
		return selectedGoMapper;
	}

	/**
	 * @param selectedGoMapper the selectedGoMapper to set
	 */
	public void setSelectedGoMapper(Map<Long, BigDecimal> selectedGoMapper) {
		this.selectedGoMapper = selectedGoMapper;
	}

	/**
	 * @return the selectedPpBudgetDtl
	 */
	public PpBudgetDtl getSelectedPpBudgetDtl() {
		if (selectedPpBudgetDtl==null)
			selectedPpBudgetDtl = new PpBudgetDtl();
		return selectedPpBudgetDtl;
	}

	/**
	 * @param selectedPpBudgetDtl the selectedPpBudgetDtl to set
	 */
	public void setSelectedPpBudgetDtl(PpBudgetDtl selectedPpBudgetDtl) {
		this.selectedPpBudgetDtl = selectedPpBudgetDtl;
	}

	/**
	 * @return the verify
	 */
	public boolean isVerify() {
		return verify;
	}

	/**
	 * @param verify the verify to set
	 */
	public void setVerify(boolean verify) {
		this.verify = verify;
	}

	/**
	 * @return the verifyRequired
	 */
	public boolean isVerifyRequired() {
		return verifyRequired;
	}

	/**
	 * @param verifyRequired the verifyRequired to set
	 */
	public void setVerifyRequired(boolean verifyRequired) {
		this.verifyRequired = verifyRequired;
	}

	
	
	
}
