/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.bean.sc.quatationtender;

import java.util.List;

import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;

import org.primefaces.model.DualListModel;

/**
 * The Class TabBean.
 *
 * @author Viswanath
 */
public class TabBean {
	
	/** The title name. */
	private String titleName;
	
	/** The sc qt approval. */
	private ScQtApproval scQtApproval;
	
	/** The sc committee. */
	private ScCommittee scCommittee;
	
	/** The selected sc committee. */
	private ScCommittee selectedScCommittee;
	
	/** The budget approval users. */
	private DualListModel<ScQtApproverSettings> budgetApprovalUsers;
	
	/** The selected approver list. */
	private List<ScQtApproverSettings> selectedApproverList;
	
	/** The sc qt approval list. */
	private List<ScQtApproval> scQtApprovalList;
	
	/** The sc qt committee. */
	private ScQtCommittee scQtCommittee;

	/** The approver committes. */
	private List<PmParameter> approverCommittes;

	/** The members. */
	private List<ScCommitteeMember> members;

	/**
	 * Instantiates a new tab bean.
	 *
	 * @param titleName the title name
	 */
	public TabBean(String titleName) {
		this.titleName = titleName;
	}

	/**
	 * Instantiates a new tab bean.
	 *
	 * @param titleName the title name
	 * @param scQtApproval the sc qt approval
	 * @param budgetApprovalUsers the budget approval users
	 */
	public TabBean(String titleName, ScQtApproval scQtApproval,
			DualListModel<ScQtApproverSettings> budgetApprovalUsers) {
		this.titleName = titleName;
		this.scQtApproval = scQtApproval;
		this.budgetApprovalUsers = budgetApprovalUsers;

	}

	/**
	 * Instantiates a new tab bean.
	 *
	 * @param titleName the title name
	 * @param scQtApproval the sc qt approval
	 */
	public TabBean(String titleName, ScQtApproval scQtApproval) {
		this.titleName = titleName;
		this.scQtApproval = scQtApproval;
	}

	/**
	 * Instantiates a new tab bean.
	 *
	 * @param titleName the title name
	 * @param scCommittee the sc committee
	 */
	public TabBean(String titleName, ScCommittee scCommittee) {
		this.titleName = titleName;
		this.scCommittee = scCommittee;

	}

	/**
	 * Instantiates a new tab bean.
	 *
	 * @param titleName the title name
	 * @param scQtApprovalList the sc qt approval list
	 */
	public TabBean(String titleName, List<ScQtApproval> scQtApprovalList) {
		this.titleName = titleName;
		this.scQtApprovalList = scQtApprovalList;
	}

	/**
	 * Instantiates a new tab bean.
	 *
	 * @param titleName the title name
	 * @param scQtCommittee the sc qt committee
	 */
	public TabBean(String titleName, ScQtCommittee scQtCommittee) {
		this.titleName = titleName;
		this.scQtCommittee = scQtCommittee;
	}

	/**
	 * Instantiates a new tab bean.
	 *
	 * @param titleName the title name
	 * @param scQtApproval the sc qt approval
	 * @param approverSetting the approver setting
	 */
	public TabBean(String titleName, ScQtApproval scQtApproval,
			List<PmParameter> approverSetting) {
		this.titleName = titleName;
		this.scQtApproval = scQtApproval;
		this.approverCommittes = approverSetting;

	}

	/**
	 * Gets the title name.
	 *
	 * @return the title name
	 */
	public String getTitleName() {
		return titleName;
	}

	/**
	 * Sets the title name.
	 *
	 * @param titleName the new title name
	 */
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	/**
	 * Gets the sc qt approval.
	 *
	 * @return the sc qt approval
	 */
	public ScQtApproval getScQtApproval() {
		return scQtApproval;
	}

	/**
	 * Sets the sc qt approval.
	 *
	 * @param scQtApproval the new sc qt approval
	 */
	public void setScQtApproval(ScQtApproval scQtApproval) {
		this.scQtApproval = scQtApproval;
	}

	/**
	 * Gets the sc committee.
	 *
	 * @return the sc committee
	 */
	public ScCommittee getScCommittee() {
		return scCommittee;
	}

	/**
	 * Sets the sc committee.
	 *
	 * @param scCommittee the new sc committee
	 */
	public void setScCommittee(ScCommittee scCommittee) {
		this.scCommittee = scCommittee;
	}

	/**
	 * Gets the selected sc committee.
	 *
	 * @return the selected sc committee
	 */
	public ScCommittee getSelectedScCommittee() {
		return selectedScCommittee;
	}

	/**
	 * Sets the selected sc committee.
	 *
	 * @param selectedScCommittee the new selected sc committee
	 */
	public void setSelectedScCommittee(ScCommittee selectedScCommittee) {
		this.selectedScCommittee = selectedScCommittee;
	}

	/**
	 * Gets the budget approval users.
	 *
	 * @return the budget approval users
	 */
	public DualListModel<ScQtApproverSettings> getBudgetApprovalUsers() {
		return budgetApprovalUsers;
	}

	/**
	 * Sets the budget approval users.
	 *
	 * @param budgetApprovalUsers the new budget approval users
	 */
	public void setBudgetApprovalUsers(
			DualListModel<ScQtApproverSettings> budgetApprovalUsers) {
		this.budgetApprovalUsers = budgetApprovalUsers;
	}

	/**
	 * Gets the selected approver list.
	 *
	 * @return the selected approver list
	 */
	public List<ScQtApproverSettings> getSelectedApproverList() {
		return selectedApproverList;
	}

	/**
	 * Sets the selected approver list.
	 *
	 * @param selectedApproverList the new selected approver list
	 */
	public void setSelectedApproverList(
			List<ScQtApproverSettings> selectedApproverList) {
		this.selectedApproverList = selectedApproverList;
	}

	/**
	 * Gets the sc qt approval list.
	 *
	 * @return the sc qt approval list
	 */
	public List<ScQtApproval> getScQtApprovalList() {
		return scQtApprovalList;
	}

	/**
	 * Sets the sc qt approval list.
	 *
	 * @param scQtApprovalList the new sc qt approval list
	 */
	public void setScQtApprovalList(List<ScQtApproval> scQtApprovalList) {
		this.scQtApprovalList = scQtApprovalList;
	}

	/**
	 * Gets the sc qt committee.
	 *
	 * @return the sc qt committee
	 */
	public ScQtCommittee getScQtCommittee() {
		return scQtCommittee;
	}

	/**
	 * Sets the sc qt committee.
	 *
	 * @param scQtCommittee the new sc qt committee
	 */
	public void setScQtCommittee(ScQtCommittee scQtCommittee) {
		this.scQtCommittee = scQtCommittee;
	}

	/**
	 * Gets the members.
	 *
	 * @return the members
	 */
	public List<ScCommitteeMember> getMembers() {
		return members;
	}

	/**
	 * Sets the members.
	 *
	 * @param members the new members
	 */
	public void setMembers(List<ScCommitteeMember> members) {
		this.members = members;
	}

	/**
	 * Gets the approver committes.
	 *
	 * @return the approver committes
	 */
	public List<PmParameter> getApproverCommittes() {
		return approverCommittes;
	}

	/**
	 * Sets the approver committes.
	 *
	 * @param approverCommittes the new approver committes
	 */
	public void setApproverCommittes(List<PmParameter> approverCommittes) {
		this.approverCommittes = approverCommittes;
	}

}
