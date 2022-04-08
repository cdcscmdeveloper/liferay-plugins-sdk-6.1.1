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

import java.io.Serializable;
import java.util.List;

import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.common.PmParameter;
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
public class TabBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The title name. */
	private String titleName;
	
	/** The tab type */
	private String tabType;
	
	/** The sc qt approval. */
	private ScQtApproval scQtApproval;
	
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
	
	private SelectItem[] fcTypes;
	
	private String icon;
	
	private String tooltip;

	private boolean disabled;//#25037

	/**
	 * Instantiates a new tab bean.
	 *
	 * @param titleName the title name
	 * @param tabType	the tab type
	 * @param scQtApproval the sc qt approval
	 * @param budgetApprovalUsers the budget approval users
	 */
	public TabBean(String titleName, String tabType, ScQtApproval scQtApproval,
			DualListModel<ScQtApproverSettings> budgetApprovalUsers) {
		this.titleName = titleName;
		this.tabType = tabType;
		this.scQtApproval = scQtApproval;
		this.budgetApprovalUsers = budgetApprovalUsers;

	}

	/**
	 * Instantiates a new tab bean.
	 *
	 * @param titleName the title name
	 * @param tabType	the tab type
	 * @param scQtCommittee the sc qt committee
	 */
	public TabBean(String titleName, String tabType, ScQtCommittee scQtCommittee) {
		this.titleName = titleName;
		this.tabType = tabType;
		this.scQtCommittee = scQtCommittee;
	}

	/**
	 * Instantiates a new tab bean.
	 *
	 * @param titleName the title name
	 * @param tabType	the tab type
	 * @param scQtApproval the sc qt approval
	 * @param approverSetting the approver setting
	 */
	public TabBean(String titleName, String tabType, ScQtApproval scQtApproval,
			List<PmParameter> approverSetting) {
		this.titleName = titleName;
		this.tabType = tabType;
		this.scQtApproval = scQtApproval;
		this.approverCommittes = approverSetting;

	}
	
	/**
	 * Instantiates a new tab bean.
	 *
	 * @param titleName the title name
	 * @param tabType	the tab type
	 * @param scQtApproval the sc qt approval
	 * @param SelectItem the fcTypes
	 */
	public TabBean(String titleName, String tabType, ScQtApproval scQtApproval,
			SelectItem[] fcTypes) {
		this.titleName = titleName;
		this.tabType = tabType;
		this.scQtApproval = scQtApproval;
		this.fcTypes = fcTypes;

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
	 * @return the tabType
	 */
	public String getTabType() {
		return tabType;
	}

	/**
	 * @param tabType the tabType to set
	 */
	public void setTabType(String tabType) {
		this.tabType = tabType;
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

	/**
	 * @return the fcTypes
	 */
	public SelectItem[] getFcTypes() {
		return fcTypes;
	}

	/**
	 * @param fcTypes the fcTypes to set
	 */
	public void setFcTypes(SelectItem[] fcTypes) {
		this.fcTypes = fcTypes;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * @return the tooltip
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * @param tooltip the tooltip to set
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}
