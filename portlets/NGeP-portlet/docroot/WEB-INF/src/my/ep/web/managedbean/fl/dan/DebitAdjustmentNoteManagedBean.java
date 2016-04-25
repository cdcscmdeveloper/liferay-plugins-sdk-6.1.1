/**
 * Copyright © 2013 Archisoft. All rights reserved.

 */
package my.ep.web.managedbean.fl.dan;

import java.math.BigDecimal;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.persistence.entity.fl.adjust.FlAdjustmentItem;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.TreeNode;

// TODO: Auto-generated Javadoc
/**
 * Managed Bean for Debit Adjustment Note.
 *
 * @author KKLIM
 * @since 1/11/2013
 */
@ManagedBean(name = "danMB")
@SessionScoped
public class DebitAdjustmentNoteManagedBean extends BaseManagedBean{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The adjustment. */
	private FlAdjustment adjustment;
	
	/** The dan charge line tree. */
	private TreeNode danChargeLineTree;
	
	/** The selected nodes. */
	private TreeNode selectedNode;
	
	/** The is dan. */
	private boolean isDan = false;
	//tab menu
	/** The selected tab index. */
	private int selectedTabIndex;
	
	/** The selected dan item. */
	private FlAdjustmentItem selectedDanItem;
	
	//getSuplier address details
	/** The supplier address. */
	private String supplierAddress;
	
	/** The phone no. */
	private String phoneNo;
	
	/** The fax no. */
	private String faxNo;
	
	/** The contact email. */
	private String contactEmail;
	
	/** The contact person. */
	private String contactPerson;
	
	/** The contact phone no. */
	private String contactPhoneNo;
	
	/** The branch name. */
	private String branchName;
	
	// set chargeline for selected items
	/** The vot fund code. */
	private String votFundCode;
	
	/** The asset ind code. */
	private String assetIndCode;
	
	/** The gl account code. */
	private String glAccountCode;
	
	/** The project code. */
	private String projectCode;
	
	/** The setia code. */
	private String setiaCode;
	
	/** The sub setia code. */
	private String subSetiaCode;
	
	/** The prg activity code. */
	private String prgActivityCode;
	
	/** The vot fund type. */
	private String votFundType;
	
	/** The financing type code. */
	private String financingTypeCode;
	
	/** The ordered amt total. */
	private BigDecimal orderedAmtTotal ;
	
	/** The dan amt total. */
	private BigDecimal danAmtTotal ;
	
	/** The inv amt total. */
	private BigDecimal invAmtTotal ;
	
	/** The account no name. */
	private String accountNoName;
	
	/** The back to document. */
	private String backToDocument;
	private boolean autoUpdate=true;
	private boolean gfmasError=false;
	private String gfmasErrorContent;
	
	private boolean hyperLinkPage = false;
	
	private String buttonVal;
	
	private String backToPrevious;
	private List<FlRemark> previousRemarkList;
	private List<FlAttachment> previousAttachmentList;
	
	/** The text before sign. */
	private String textBeforeSign;
	
	//gpki signing
		/** The pm digi sign. */
	private PmDigiSign pmDigiSign;
	
	/** The button action. */
	private String buttonAction;
	
	/** The validated. */
	private boolean validated = false;
	
	
	/**
	 * Gets the adjustment.
	 *
	 * @return the adjustment
	 */
	public FlAdjustment getAdjustment() {
		return adjustment;
	}
	
	/**
	 * Sets the adjustment.
	 *
	 * @param adjustment the adjustment to set
	 */
	public void setAdjustment(FlAdjustment adjustment) {
		this.adjustment = adjustment;
	}
	
	/**
	 * Checks if is dan.
	 *
	 * @return the isDan
	 */
	public boolean isDan() {
		return isDan;
	}
	
	/**
	 * Sets the dan.
	 *
	 * @param isDan the isDan to set
	 */
	public void setDan(boolean isDan) {
		this.isDan = isDan;
	}
	
	/**
	 * Gets the dan charge line tree.
	 *
	 * @return the danChargeLineTree
	 */
	public TreeNode getDanChargeLineTree() {
		return danChargeLineTree;
	}
	
	/**
	 * Sets the dan charge line tree.
	 *
	 * @param danChargeLineTree the danChargeLineTree to set
	 */
	public void setDanChargeLineTree(TreeNode danChargeLineTree) {
		this.danChargeLineTree = danChargeLineTree;
	}

	
	/**
	 * Gets the selected tab index.
	 *
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}
	
	/**
	 * Sets the selected tab index.
	 *
	 * @param selectedTabIndex the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}
	
	/**
	 * Gets the supplier address.
	 *
	 * @return the supplierAddress
	 */
	public String getSupplierAddress() {
		return supplierAddress;
	}
	
	/**
	 * Sets the supplier address.
	 *
	 * @param supplierAddress the supplierAddress to set
	 */
	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}
	
	/**
	 * Gets the phone no.
	 *
	 * @return the phoneNo
	 */
	public String getPhoneNo() {
		return phoneNo;
	}
	
	/**
	 * Sets the phone no.
	 *
	 * @param phoneNo the phoneNo to set
	 */
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	
	/**
	 * Gets the fax no.
	 *
	 * @return the faxNo
	 */
	public String getFaxNo() {
		return faxNo;
	}
	
	/**
	 * Sets the fax no.
	 *
	 * @param faxNo the faxNo to set
	 */
	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}
	
	/**
	 * Gets the contact email.
	 *
	 * @return the contactEmail
	 */
	public String getContactEmail() {
		return contactEmail;
	}
	
	/**
	 * Sets the contact email.
	 *
	 * @param contactEmail the contactEmail to set
	 */
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	
	/**
	 * Gets the contact person.
	 *
	 * @return the contactPerson
	 */
	public String getContactPerson() {
		return contactPerson;
	}
	
	/**
	 * Sets the contact person.
	 *
	 * @param contactPerson the contactPerson to set
	 */
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	
	/**
	 * Gets the contact phone no.
	 *
	 * @return the contactPhoneNo
	 */
	public String getContactPhoneNo() {
		return contactPhoneNo;
	}
	
	/**
	 * Sets the contact phone no.
	 *
	 * @param contactPhoneNo the contactPhoneNo to set
	 */
	public void setContactPhoneNo(String contactPhoneNo) {
		this.contactPhoneNo = contactPhoneNo;
	}
	
	/**
	 * Gets the branch name.
	 *
	 * @return the branchName
	 */
	public String getBranchName() {
		return branchName;
	}
	
	/**
	 * Sets the branch name.
	 *
	 * @param branchName the branchName to set
	 */
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
	/**
	 * Gets the vot fund code.
	 *
	 * @return the votFundCode
	 */
	public String getVotFundCode() {
		return votFundCode;
	}
	
	/**
	 * Sets the vot fund code.
	 *
	 * @param votFundCode the votFundCode to set
	 */
	public void setVotFundCode(String votFundCode) {
		this.votFundCode = votFundCode;
	}
	
	/**
	 * Gets the asset ind code.
	 *
	 * @return the assetIndCode
	 */
	public String getAssetIndCode() {
		return assetIndCode;
	}
	
	/**
	 * Sets the asset ind code.
	 *
	 * @param assetIndCode the assetIndCode to set
	 */
	public void setAssetIndCode(String assetIndCode) {
		this.assetIndCode = assetIndCode;
	}
	
	/**
	 * Gets the gl account code.
	 *
	 * @return the glAccountCode
	 */
	public String getGlAccountCode() {
		return glAccountCode;
	}
	
	/**
	 * Sets the gl account code.
	 *
	 * @param glAccountCode the glAccountCode to set
	 */
	public void setGlAccountCode(String glAccountCode) {
		this.glAccountCode = glAccountCode;
	}
	
	/**
	 * Gets the project code.
	 *
	 * @return the projectCode
	 */
	public String getProjectCode() {
		return projectCode;
	}
	
	/**
	 * Sets the project code.
	 *
	 * @param projectCode the projectCode to set
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	
	/**
	 * Gets the setia code.
	 *
	 * @return the setiaCode
	 */
	public String getSetiaCode() {
		return setiaCode;
	}
	
	/**
	 * Sets the setia code.
	 *
	 * @param setiaCode the setiaCode to set
	 */
	public void setSetiaCode(String setiaCode) {
		this.setiaCode = setiaCode;
	}
	
	/**
	 * Gets the sub setia code.
	 *
	 * @return the subSetiaCode
	 */
	public String getSubSetiaCode() {
		return subSetiaCode;
	}
	
	/**
	 * Sets the sub setia code.
	 *
	 * @param subSetiaCode the subSetiaCode to set
	 */
	public void setSubSetiaCode(String subSetiaCode) {
		this.subSetiaCode = subSetiaCode;
	}
	
	/**
	 * Gets the prg activity code.
	 *
	 * @return the prgActivityCode
	 */
	public String getPrgActivityCode() {
		return prgActivityCode;
	}
	
	/**
	 * Sets the prg activity code.
	 *
	 * @param prgActivityCode the prgActivityCode to set
	 */
	public void setPrgActivityCode(String prgActivityCode) {
		this.prgActivityCode = prgActivityCode;
	}
	
	/**
	 * Gets the vot fund type.
	 *
	 * @return the votFundType
	 */
	public String getVotFundType() {
		return votFundType;
	}
	
	/**
	 * Sets the vot fund type.
	 *
	 * @param votFundType the votFundType to set
	 */
	public void setVotFundType(String votFundType) {
		this.votFundType = votFundType;
	}
	
	/**
	 * Gets the financing type code.
	 *
	 * @return the financingTypeCode
	 */
	public String getFinancingTypeCode() {
		return financingTypeCode;
	}
	
	/**
	 * Sets the financing type code.
	 *
	 * @param financingTypeCode the financingTypeCode to set
	 */
	public void setFinancingTypeCode(String financingTypeCode) {
		this.financingTypeCode = financingTypeCode;
	}
		
	/**
	 * Gets the selected dan item.
	 *
	 * @return the selectedDanItem
	 */
	public FlAdjustmentItem getSelectedDanItem() {
		return selectedDanItem;
	}
	
	/**
	 * Sets the selected dan item.
	 *
	 * @param selectedDanItem the selectedDanItem to set
	 */
	public void setSelectedDanItem(FlAdjustmentItem selectedDanItem) {
		this.selectedDanItem = selectedDanItem;
	}
	
	/**
	 * Gets the ordered amt total.
	 *
	 * @return the orderedAmtTotal
	 */
	public BigDecimal getOrderedAmtTotal() {
		return orderedAmtTotal;
	}
	
	/**
	 * Sets the ordered amt total.
	 *
	 * @param orderedAmtTotal the orderedAmtTotal to set
	 */
	public void setOrderedAmtTotal(BigDecimal orderedAmtTotal) {
		this.orderedAmtTotal = orderedAmtTotal;
	}
	
	/**
	 * Gets the dan amt total.
	 *
	 * @return the danAmtTotal
	 */
	public BigDecimal getDanAmtTotal() {
		return danAmtTotal;
	}
	
	/**
	 * Sets the dan amt total.
	 *
	 * @param danAmtTotal the danAmtTotal to set
	 */
	public void setDanAmtTotal(BigDecimal danAmtTotal) {
		this.danAmtTotal = danAmtTotal;
	}
	
	/**
	 * Gets the inv amt total.
	 *
	 * @return the invAmtTotal
	 */
	public BigDecimal getInvAmtTotal() {
		return invAmtTotal;
	}
	
	/**
	 * Sets the inv amt total.
	 *
	 * @param invAmtTotal the invAmtTotal to set
	 */
	public void setInvAmtTotal(BigDecimal invAmtTotal) {
		this.invAmtTotal = invAmtTotal;
	}
	
	/**
	 * Gets the account no name.
	 *
	 * @return the accountNoName
	 */
	public String getAccountNoName() {
		return accountNoName;
	}
	
	/**
	 * Sets the account no name.
	 *
	 * @param accountNoName the accountNoName to set
	 */
	public void setAccountNoName(String accountNoName) {
		this.accountNoName = accountNoName;
	}
	
	/**
	 * Gets the back to document.
	 *
	 * @return the backToDocument
	 */
	public String getBackToDocument() {
		return backToDocument;
	}
	
	/**
	 * Sets the back to document.
	 *
	 * @param backToDocument the backToDocument to set
	 */
	public void setBackToDocument(String backToDocument) {
		this.backToDocument = backToDocument;
	}

	/**
	 * @return the autoUpdate
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * @return the gfmasError
	 */
	public boolean isGfmasError() {
		return gfmasError;
	}

	/**
	 * @param gfmasError the gfmasError to set
	 */
	public void setGfmasError(boolean gfmasError) {
		this.gfmasError = gfmasError;
	}

	/**
	 * @return the gfmasErrorContent
	 */
	public String getGfmasErrorContent() {
		return gfmasErrorContent;
	}

	/**
	 * @param gfmasErrorContent the gfmasErrorContent to set
	 */
	public void setGfmasErrorContent(String gfmasErrorContent) {
		this.gfmasErrorContent = gfmasErrorContent;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	/**
	 * @return the hyperLinkPage
	 */
	public boolean isHyperLinkPage() {
		return hyperLinkPage;
	}

	/**
	 * @param hyperLinkPage the hyperLinkPage to set
	 */
	public void setHyperLinkPage(boolean hyperLinkPage) {
		this.hyperLinkPage = hyperLinkPage;
	}

	/**
	 * @return the buttonVal
	 */
	public String getButtonVal() {
		return buttonVal;
	}

	/**
	 * @param buttonVal the buttonVal to set
	 */
	public void setButtonVal(String buttonVal) {
		this.buttonVal = buttonVal;
	}

	/**
	 * @return the backToPrevious
	 */
	public String getBackToPrevious() {
		return backToPrevious;
	}

	/**
	 * @param backToPrevious the backToPrevious to set
	 */
	public void setBackToPrevious(String backToPrevious) {
		this.backToPrevious = backToPrevious;
	}

	/**
	 * @return the textBeforeSign
	 */
	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	/**
	 * @param textBeforeSign the textBeforeSign to set
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	/**
	 * @return the pmDigiSign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	/**
	 * @param pmDigiSign the pmDigiSign to set
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * @return the buttonAction
	 */
	public String getButtonAction() {
		return buttonAction;
	}

	/**
	 * @param buttonAction the buttonAction to set
	 */
	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
	}

	/**
	 * @return the validated
	 */
	public boolean isValidated() {
		return validated;
	}

	/**
	 * @param validated the validated to set
	 */
	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public List<FlRemark> getPreviousRemarkList() {
		return previousRemarkList;
	}

	public void setPreviousRemarkList(List<FlRemark> previousRemarkList) {
		this.previousRemarkList = previousRemarkList;
	}

	public List<FlAttachment> getPreviousAttachmentList() {
		return previousAttachmentList;
	}

	public void setPreviousAttachmentList(List<FlAttachment> previousAttachmentList) {
		this.previousAttachmentList = previousAttachmentList;
	}
	
}
