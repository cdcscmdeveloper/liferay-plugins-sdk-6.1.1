/*
 * Copyright © 2013 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pp.procurementapproval;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pp.PpRemark;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpPlanDtl;
import my.ep.web.managedbean.BaseManagedBean;


@ManagedBean(name = "procurmentApprovalManagedBean")
@SessionScoped
public class ProcurementApprovalManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	
	private PmOrgProfile currentUser;
	
	private List<PpPlan> pendingApprovalPlans;
	
//	private PpPlanMaster selectedPlanMaster;

	private PpPlan selectedPlan;
	
	private PpPlan selectedPlanItem;

	private PpPlanDtl selectedItem;
	
	private PpRemark selectedItemRemark;
	
	/**
	 * @return the pendingApprovalPlans
	 */
	public List<PpPlan> getPendingApprovalPlans() {
		return pendingApprovalPlans;
	}

	/**
	 * @param pendingApprovalPlans the pendingApprovalPlans to set
	 */
	public void setPendingApprovalPlans(List<PpPlan> pendingApprovalPlans) {
		this.pendingApprovalPlans = pendingApprovalPlans;
	}

	/**
	 * @return the currentUser
	 */
	public PmOrgProfile getCurrentUser() {
		return currentUser;
	}

	/**
	 * @param currentUser the currentUser to set
	 */
	public void setCurrentUser(PmOrgProfile currentUser) {
		this.currentUser = currentUser;
	}
	
//	/**
//	 * @return the selectedPlanMaster
//	 */
//	public PpPlanMaster getSelectedPlanMaster() {
//		return selectedPlanMaster;
//	}
//
//	/**
//	 * @param selectedPlanMaster the selectedPlanMaster to set
//	 */
//	public void setSelectedPlanMaster(PpPlanMaster selectedPlanMaster) {
//		this.selectedPlanMaster = selectedPlanMaster;
//	}

	/**
	 * @return the selectedPlan
	 */
	public PpPlan getSelectedPlan() {
		return selectedPlan;
	}

	/**
	 * @param selectedPlan the selectedPlan to set
	 */
	public void setSelectedPlan(PpPlan selectedPlan) {
		this.selectedPlan = selectedPlan;
	}

	/**
	 * @return the selectedPlanItem
	 */
	public PpPlan getSelectedPlanItem() {
		return selectedPlanItem;
	}

	/**
	 * @param selectedPlanItem the selectedPlanItem to set
	 */
	public void setSelectedPlanItem(PpPlan selectedPlanItem) {
		this.selectedPlanItem = selectedPlanItem;
	}

	/**
	 * @return the selectedItem
	 */
	public PpPlanDtl getSelectedItem() {
		return selectedItem;
	}

	/**
	 * @param selectedItem the selectedItem to set
	 */
	public void setSelectedItem(PpPlanDtl selectedItem) {
		this.selectedItem = selectedItem;
	}

	/**
	 * @return the selectedItemRemark
	 */
	public PpRemark getSelectedItemRemark() {
		return selectedItemRemark;
	}

	/**
	 * @param selectedItemRemark the selectedItemRemark to set
	 */
	public void setSelectedItemRemark(PpRemark selectedItemRemark) {
		this.selectedItemRemark = selectedItemRemark;
	}
	


}
