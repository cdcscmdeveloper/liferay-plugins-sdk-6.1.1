/**
 * Copyright © 2013 Archisoft. All rights reserved.

 */
package my.ep.web.managedbean.fl.dan;

import java.math.BigDecimal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.persistence.entity.fl.adjust.FlAdjustmentItem;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

/**
 * Managed Bean for Debit Adjustment Note
 * @author KKLIM
 * @since 1/11/2013
 */
@ManagedBean(name = "danMB")
@SessionScoped
public class DebitAdjustmentNoteManagedBean extends BaseManagedBean{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(DebitAdjustmentNoteManagedBean.class);
	
	private FlAdjustment adjustment;
	private TreeNode danChargeLineTree;
	private TreeNode[] selectedNodes;
	//private TreeNode selectedNode;
	//private TreeNode chargeLineItemRoot ;
	
	
	private boolean isDan = false;
	//tab menu
	private int selectedTabIndex;
	
	private FlAdjustmentItem selectedDanItem;
	
	//getSuplier address details
	private String supplierAddress;
	private String phoneNo;
	private String faxNo;
	private String contactEmail;
	private String contactPerson;
	private String contactPhoneNo;
	private String branchName;
	
	// set chargeline for selected items
	private String votFundCode;
	private String assetIndCode;
	private String glAccountCode;
	private String projectCode;
	private String setiaCode;
	private String subSetiaCode;
	private String prgActivityCode;
	private String votFundType;
	private String financingTypeCode;
	
	private BigDecimal orderedAmtTotal ;
	private BigDecimal danAmtTotal ;
	private BigDecimal invAmtTotal ;
	private String accountNoName;
	
	
	/**
	 * @return the adjustment
	 */
	public FlAdjustment getAdjustment() {
		return adjustment;
	}
	/**
	 * @param adjustment the adjustment to set
	 */
	public void setAdjustment(FlAdjustment adjustment) {
		this.adjustment = adjustment;
	}
	/**
	 * @return the isDan
	 */
	public boolean isDan() {
		return isDan;
	}
	/**
	 * @param isDan the isDan to set
	 */
	public void setDan(boolean isDan) {
		this.isDan = isDan;
	}
	/**
	 * @return the danChargeLineTree
	 */
	public TreeNode getDanChargeLineTree() {
		return danChargeLineTree;
	}
	/**
	 * @param danChargeLineTree the danChargeLineTree to set
	 */
	public void setDanChargeLineTree(TreeNode danChargeLineTree) {
		this.danChargeLineTree = danChargeLineTree;
	}
	/**
	 * @return the selectedNodes
	 */
	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}
	/**
	 * @param selectedNodes the selectedNodes to set
	 */
	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}
	/**
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}
	/**
	 * @param selectedTabIndex the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}
	/**
	 * @return the supplierAddress
	 */
	public String getSupplierAddress() {
		return supplierAddress;
	}
	/**
	 * @param supplierAddress the supplierAddress to set
	 */
	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}
	/**
	 * @return the phoneNo
	 */
	public String getPhoneNo() {
		return phoneNo;
	}
	/**
	 * @param phoneNo the phoneNo to set
	 */
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	/**
	 * @return the faxNo
	 */
	public String getFaxNo() {
		return faxNo;
	}
	/**
	 * @param faxNo the faxNo to set
	 */
	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}
	/**
	 * @return the contactEmail
	 */
	public String getContactEmail() {
		return contactEmail;
	}
	/**
	 * @param contactEmail the contactEmail to set
	 */
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	/**
	 * @return the contactPerson
	 */
	public String getContactPerson() {
		return contactPerson;
	}
	/**
	 * @param contactPerson the contactPerson to set
	 */
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	/**
	 * @return the contactPhoneNo
	 */
	public String getContactPhoneNo() {
		return contactPhoneNo;
	}
	/**
	 * @param contactPhoneNo the contactPhoneNo to set
	 */
	public void setContactPhoneNo(String contactPhoneNo) {
		this.contactPhoneNo = contactPhoneNo;
	}
	/**
	 * @return the branchName
	 */
	public String getBranchName() {
		return branchName;
	}
	/**
	 * @param branchName the branchName to set
	 */
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
	/**
	 * @return the votFundCode
	 */
	public String getVotFundCode() {
		return votFundCode;
	}
	/**
	 * @param votFundCode the votFundCode to set
	 */
	public void setVotFundCode(String votFundCode) {
		this.votFundCode = votFundCode;
	}
	/**
	 * @return the assetIndCode
	 */
	public String getAssetIndCode() {
		return assetIndCode;
	}
	/**
	 * @param assetIndCode the assetIndCode to set
	 */
	public void setAssetIndCode(String assetIndCode) {
		this.assetIndCode = assetIndCode;
	}
	/**
	 * @return the glAccountCode
	 */
	public String getGlAccountCode() {
		return glAccountCode;
	}
	/**
	 * @param glAccountCode the glAccountCode to set
	 */
	public void setGlAccountCode(String glAccountCode) {
		this.glAccountCode = glAccountCode;
	}
	/**
	 * @return the projectCode
	 */
	public String getProjectCode() {
		return projectCode;
	}
	/**
	 * @param projectCode the projectCode to set
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	/**
	 * @return the setiaCode
	 */
	public String getSetiaCode() {
		return setiaCode;
	}
	/**
	 * @param setiaCode the setiaCode to set
	 */
	public void setSetiaCode(String setiaCode) {
		this.setiaCode = setiaCode;
	}
	/**
	 * @return the subSetiaCode
	 */
	public String getSubSetiaCode() {
		return subSetiaCode;
	}
	/**
	 * @param subSetiaCode the subSetiaCode to set
	 */
	public void setSubSetiaCode(String subSetiaCode) {
		this.subSetiaCode = subSetiaCode;
	}
	/**
	 * @return the prgActivityCode
	 */
	public String getPrgActivityCode() {
		return prgActivityCode;
	}
	/**
	 * @param prgActivityCode the prgActivityCode to set
	 */
	public void setPrgActivityCode(String prgActivityCode) {
		this.prgActivityCode = prgActivityCode;
	}
	/**
	 * @return the votFundType
	 */
	public String getVotFundType() {
		return votFundType;
	}
	/**
	 * @param votFundType the votFundType to set
	 */
	public void setVotFundType(String votFundType) {
		this.votFundType = votFundType;
	}
	/**
	 * @return the financingTypeCode
	 */
	public String getFinancingTypeCode() {
		return financingTypeCode;
	}
	/**
	 * @param financingTypeCode the financingTypeCode to set
	 */
	public void setFinancingTypeCode(String financingTypeCode) {
		this.financingTypeCode = financingTypeCode;
	}
		
	/**
	 * @return the selectedDanItem
	 */
	public FlAdjustmentItem getSelectedDanItem() {
		return selectedDanItem;
	}
	/**
	 * @param selectedDanItem the selectedDanItem to set
	 */
	public void setSelectedDanItem(FlAdjustmentItem selectedDanItem) {
		this.selectedDanItem = selectedDanItem;
	}
	/**
	 * @return the orderedAmtTotal
	 */
	public BigDecimal getOrderedAmtTotal() {
		return orderedAmtTotal;
	}
	/**
	 * @param orderedAmtTotal the orderedAmtTotal to set
	 */
	public void setOrderedAmtTotal(BigDecimal orderedAmtTotal) {
		this.orderedAmtTotal = orderedAmtTotal;
	}
	/**
	 * @return the danAmtTotal
	 */
	public BigDecimal getDanAmtTotal() {
		return danAmtTotal;
	}
	/**
	 * @param danAmtTotal the danAmtTotal to set
	 */
	public void setDanAmtTotal(BigDecimal danAmtTotal) {
		this.danAmtTotal = danAmtTotal;
	}
	/**
	 * @return the invAmtTotal
	 */
	public BigDecimal getInvAmtTotal() {
		return invAmtTotal;
	}
	/**
	 * @param invAmtTotal the invAmtTotal to set
	 */
	public void setInvAmtTotal(BigDecimal invAmtTotal) {
		this.invAmtTotal = invAmtTotal;
	}
	/**
	 * @return the accountNoName
	 */
	public String getAccountNoName() {
		return accountNoName;
	}
	/**
	 * @param accountNoName the accountNoName to set
	 */
	public void setAccountNoName(String accountNoName) {
		this.accountNoName = accountNoName;
	}
	
}
