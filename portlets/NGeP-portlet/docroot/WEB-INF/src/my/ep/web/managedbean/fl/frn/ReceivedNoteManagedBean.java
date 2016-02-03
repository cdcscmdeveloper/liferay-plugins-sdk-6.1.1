/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.frn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNote;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNoteItem;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCriteriaDesc;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.web.managedbean.BaseManagedBean;

// TODO: Auto-generated Javadoc
/**
 * Managed Bean for Create Received Note.
 *
 * @author Giribabu
 * @since 7/10/2013
 */
@ManagedBean(name = "frnMB")
@SessionScoped
public class ReceivedNoteManagedBean extends BaseManagedBean {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The fl remark. */
	private FlRemark flRemark = new FlRemark();
	
	/** The fl attachment. */
	private FlAttachment flAttachment = new FlAttachment();
	
	/** The fulfilment note. */
	private FlFulfilmentNote fulfilmentNote = new FlFulfilmentNote();
	
	/** The fulfilment note item. */
	private FlFulfilmentNoteItem fulfilmentNoteItem = new FlFulfilmentNoteItem();
	
	/** The fulfilment request. */
	private FlFulfilmentRequest fulfilmentRequest;
	
	/** The selected item. */
	private FlFulfilmentNoteItem selectedItem = new FlFulfilmentNoteItem();

	/** The selected addr row. */
	private FlDeliveryAddress selectedAddrRow;
	
	/** The selected del addr. */
	private FlDeliveryAddress selectedDelAddr;
	
	/** The check ptj flg. */
	private boolean checkPtjFlg = true;
	
	/** The fulfillment req id. */
	private String fulfillmentReqId;
	
	/** The button val. */
	private String buttonVal;
	
	/** The doc type. */
	private String docType;
	
	/** The supplier address. */
	private String supplierAddress;
	
	/** The frn cancellation process. */
	private boolean frnCancellationProcess;
	
	/** The selected tab index. */
	private int selectedTabIndex;
	
	/** The pm user. */
	private PmUser pmUser;
	
	/** The criteria desc list. */
	private List<PmCriteriaDesc> criteriaDescList;
	
	/** The doc maintain remarks. */
	private boolean docMaintainRemarks = false;
	
	/** Disable evaluation. */
	private boolean disableEvaluation = false;
	
	//gpki signing
	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;
	
	/** The auto update. */
	private boolean autoUpdate = true;
	
	/** The text before sign. */
	private String textBeforeSign;
	
	/** The button action. */
	private String buttonAction;
	
	/** The validated. */
	private boolean validated = false;
	
	
	private boolean gfmasError=false;
	private String gfmasErrorContent;
	private String bpmTaskName;
	private boolean lastFRNTaxInv=false;
	private List<PmUser> approverList;
	
	private boolean acknowledged;
	
	/** The from page url. */
	private String fromPageUrl;
	
	private BigDecimal orderedAmt;

	private BigDecimal orderedQty;

	private BigDecimal receivedAmt; 
	
	private BigDecimal receivedQty;
	
	private BigDecimal totalReceivedAmt; 
	
	private BigDecimal totalReceivedQty;	
	
	private BigDecimal deliveredAmt;

	private BigDecimal deliveredQty;
	
	private BigDecimal remainingQty;
	
	private boolean itemTypeProduct=false;
	
	/** The back to previous. */
	private String backToPrevious;
	
	private boolean hyperLinkPage = false;
	
	private Long userLoginId;
	
	private List<PmUser> pmUserList;
	
	private PmUser selectedAckOfficer;
	private boolean issuanceCogstrCheck;
	
	
	/**
	 * Gets the fl remark.
	 *
	 * @return the flRemark
	 */
	public FlRemark getFlRemark() {
		return flRemark;
	}

	/**
	 * Sets the fl remark.
	 *
	 * @param flRemark            the flRemark to set
	 */
	public void setFlRemark(FlRemark flRemark) {
		this.flRemark = flRemark;
	}

	/**
	 * Gets the fl attachment.
	 *
	 * @return the flAttachment
	 */
	public FlAttachment getFlAttachment() {
		return flAttachment;
	}

	/**
	 * Sets the fl attachment.
	 *
	 * @param flAttachment            the flAttachment to set
	 */
	public void setFlAttachment(FlAttachment flAttachment) {
		this.flAttachment = flAttachment;
	}

	//public CreateDOManagedBean() {}

	/**
	 * Gets the selected addr row.
	 *
	 * @return the selectedAddrRow
	 */
	public FlDeliveryAddress getSelectedAddrRow() {
		return selectedAddrRow;
	}

	/**
	 * Sets the selected addr row.
	 *
	 * @param selectedAddrRow the selectedAddrRow to set
	 */
	public void setSelectedAddrRow(FlDeliveryAddress selectedAddrRow) {
		this.selectedAddrRow = selectedAddrRow;
	}

	/**
	 * Gets the selected del addr.
	 *
	 * @return the selectedDelAddr
	 */
	public FlDeliveryAddress getSelectedDelAddr() {
		return selectedDelAddr;
	}

	/**
	 * Sets the selected del addr.
	 *
	 * @param selectedDelAddr the selectedDelAddr to set
	 */
	public void setSelectedDelAddr(FlDeliveryAddress selectedDelAddr) {
		this.selectedDelAddr = selectedDelAddr;
	}

	/**
	 * Gets the fulfillment req id.
	 *
	 * @return the fulfillmentReqId
	 */
	public String getFulfillmentReqId() {
		return fulfillmentReqId;
	}

	/**
	 * Sets the fulfillment req id.
	 *
	 * @param fulfillmentReqId the fulfillmentReqId to set
	 */
	public void setFulfillmentReqId(String fulfillmentReqId) {
		this.fulfillmentReqId = fulfillmentReqId;
	}

	/**
	 * Checks if is check ptj flg.
	 *
	 * @return the checkPtjFlg
	 */
	public boolean isCheckPtjFlg() {
		return checkPtjFlg;
	}

	/**
	 * Sets the check ptj flg.
	 *
	 * @param checkPtjFlg the checkPtjFlg to set
	 */
	public void setCheckPtjFlg(boolean checkPtjFlg) {
		this.checkPtjFlg = checkPtjFlg;
	}

	/**
	 * Gets the fulfilment note.
	 *
	 * @return the fulfilmentNote
	 */
	public FlFulfilmentNote getFulfilmentNote() {
		return fulfilmentNote;
	}

	/**
	 * Sets the fulfilment note.
	 *
	 * @param fulfilmentNote the fulfilmentNote to set
	 */
	public void setFulfilmentNote(FlFulfilmentNote fulfilmentNote) {
		this.fulfilmentNote = fulfilmentNote;
	}

	/**
	 * Gets the fulfilment note item.
	 *
	 * @return the fulfilmentNoteItem
	 */
	public FlFulfilmentNoteItem getFulfilmentNoteItem() {
		return fulfilmentNoteItem;
	}

	/**
	 * Sets the fulfilment note item.
	 *
	 * @param fulfilmentNoteItem the fulfilmentNoteItem to set
	 */
	public void setFulfilmentNoteItem(FlFulfilmentNoteItem fulfilmentNoteItem) {
		this.fulfilmentNoteItem = fulfilmentNoteItem;
	}

	/**
	 * Gets the button val.
	 *
	 * @return the buttonVal
	 */
	public String getButtonVal() {
		return buttonVal;
	}

	/**
	 * Sets the button val.
	 *
	 * @param buttonVal the buttonVal to set
	 */
	public void setButtonVal(String buttonVal) {
		this.buttonVal = buttonVal;
	}

	/**
	 * Gets the fulfilment request.
	 *
	 * @return the fulfilmentRequest
	 */
	public FlFulfilmentRequest getFulfilmentRequest() {
		return fulfilmentRequest;
	}

	/**
	 * Sets the fulfilment request.
	 *
	 * @param fulfilmentRequest the fulfilmentRequest to set
	 */
	public void setFulfilmentRequest(FlFulfilmentRequest fulfilmentRequest) {
		this.fulfilmentRequest = fulfilmentRequest;
	}

	/**
	 * Gets the doc type.
	 *
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * Sets the doc type.
	 *
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * Gets the selected item.
	 *
	 * @return the selectedItem
	 */
	public FlFulfilmentNoteItem getSelectedItem() {
		return selectedItem;
	}

	/**
	 * Sets the selected item.
	 *
	 * @param selectedItem the selectedItem to set
	 */
	public void setSelectedItem(FlFulfilmentNoteItem selectedItem) {
		this.selectedItem = selectedItem;
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
	 * Gets the pm user.
	 *
	 * @return the pmUser
	 */
	public PmUser getPmUser() {
		return pmUser;
	}

	/**
	 * Sets the pm user.
	 *
	 * @param pmUser the pmUser to set
	 */
	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}

	/**
	 * Checks if is frn cancellation process.
	 *
	 * @return the frnCancellationProcess
	 */
	public boolean isFrnCancellationProcess() {
		return frnCancellationProcess;
	}

	/**
	 * Sets the frn cancellation process.
	 *
	 * @param frnCancellationProcess the frnCancellationProcess to set
	 */
	public void setFrnCancellationProcess(boolean frnCancellationProcess) {
		this.frnCancellationProcess = frnCancellationProcess;
	}

	/**
	 * Gets the criteria desc list.
	 *
	 * @return the criteriaDescList
	 */
	public List<PmCriteriaDesc> getCriteriaDescList() {
		return criteriaDescList;
	}

	/**
	 * Sets the criteria desc list.
	 *
	 * @param criteriaDescList the criteriaDescList to set
	 */
	public void setCriteriaDescList(List<PmCriteriaDesc> criteriaDescList) {
		this.criteriaDescList = criteriaDescList;
	}

	/**
	 * Checks if is doc maintain remarks.
	 *
	 * @return the docMaintainRemarks
	 */
	public boolean isDocMaintainRemarks() {
		return docMaintainRemarks;
	}

	/**
	 * Sets the doc maintain remarks.
	 *
	 * @param docMaintainRemarks the docMaintainRemarks to set
	 */
	public void setDocMaintainRemarks(boolean docMaintainRemarks) {
		this.docMaintainRemarks = docMaintainRemarks;
	}

	/**
	 * Gets the pm digi sign.
	 *
	 * @return the pm digi sign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	/**
	 * Sets the pm digi sign.
	 *
	 * @param pmDigiSign the new pm digi sign
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * Checks if is auto update.
	 *
	 * @return true, if is auto update
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * Sets the auto update.
	 *
	 * @param autoUpdate the new auto update
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * Gets the text before sign.
	 *
	 * @return the text before sign
	 */
	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	/**
	 * Sets the text before sign.
	 *
	 * @param textBeforeSign the new text before sign
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	/**
	 * Gets the button action.
	 *
	 * @return the button action
	 */
	public String getButtonAction() {
		return buttonAction;
	}

	/**
	 * Sets the button action.
	 *
	 * @param buttonAction the new button action
	 */
	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
	}

	/**
	 * Checks if is validated.
	 *
	 * @return true, if is validated
	 */
	public boolean isValidated() {
		return validated;
	}

	/**
	 * Sets the validated.
	 *
	 * @param validated the new validated
	 */
	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	/**
	 * @return the disableEvaluation
	 */
	public boolean isDisableEvaluation() {
		return disableEvaluation;
	}

	/**
	 * @param disableEvaluation the disableEvaluation to set
	 */
	public void setDisableEvaluation(boolean disableEvaluation) {
		this.disableEvaluation = disableEvaluation;
	}

	/**
	 * @return the fromPageUrl
	 */
	public String getFromPageUrl() {
		return fromPageUrl;
	}

	/**
	 * @param fromPageUrl the fromPageUrl to set
	 */
	public void setFromPageUrl(String fromPageUrl) {
		this.fromPageUrl = fromPageUrl;
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

	/**
	 * @return the bpmTaskName
	 */
	public String getBpmTaskName() {
		return bpmTaskName;
	}

	/**
	 * @param bpmTaskName the bpmTaskName to set
	 */
	public void setBpmTaskName(String bpmTaskName) {
		this.bpmTaskName = bpmTaskName;
	}

	/**
	 * @return the acknowledged
	 */
	public boolean isAcknowledged() {
		return acknowledged;
	}

	/**
	 * @param acknowledged the acknowledged to set
	 */
	public void setAcknowledged(boolean acknowledged) {
		this.acknowledged = acknowledged;
	}

	/**
	 * @return the lastFRNTaxInv
	 */
	public boolean isLastFRNTaxInv() {
		return lastFRNTaxInv;
	}

	/**
	 * @param lastFRNTaxInv the lastFRNTaxInv to set
	 */
	public void setLastFRNTaxInv(boolean lastFRNTaxInv) {
		this.lastFRNTaxInv = lastFRNTaxInv;
	}

	/**
	 * @return the orderedAmt
	 */
	public BigDecimal getOrderedAmt() {
		return orderedAmt;
	}

	/**
	 * @param orderedAmt the orderedAmt to set
	 */
	public void setOrderedAmt(BigDecimal orderedAmt) {
		this.orderedAmt = orderedAmt;
	}

	/**
	 * @return the orderedQty
	 */
	public BigDecimal getOrderedQty() {
		return orderedQty;
	}

	/**
	 * @param orderedQty the orderedQty to set
	 */
	public void setOrderedQty(BigDecimal orderedQty) {
		this.orderedQty = orderedQty;
	}

	/**
	 * @return the receivedAmt
	 */
	public BigDecimal getReceivedAmt() {
		return receivedAmt;
	}

	/**
	 * @param receivedAmt the receivedAmt to set
	 */
	public void setReceivedAmt(BigDecimal receivedAmt) {
		this.receivedAmt = receivedAmt;
	}

	/**
	 * @return the receivedQty
	 */
	public BigDecimal getReceivedQty() {
		return receivedQty;
	}

	/**
	 * @param receivedQty the receivedQty to set
	 */
	public void setReceivedQty(BigDecimal receivedQty) {
		this.receivedQty = receivedQty;
	}

	/**
	 * @return the deliveredAmt
	 */
	public BigDecimal getDeliveredAmt() {
		return deliveredAmt;
	}

	/**
	 * @param deliveredAmt the deliveredAmt to set
	 */
	public void setDeliveredAmt(BigDecimal deliveredAmt) {
		this.deliveredAmt = deliveredAmt;
	}

	/**
	 * @return the deliveredQty
	 */
	public BigDecimal getDeliveredQty() {
		return deliveredQty;
	}

	/**
	 * @param deliveredQty the deliveredQty to set
	 */
	public void setDeliveredQty(BigDecimal deliveredQty) {
		this.deliveredQty = deliveredQty;
	}

	/**
	 * @return the remainingQty
	 */
	public BigDecimal getRemainingQty() {
		return remainingQty;
	}

	/**
	 * @param remainingQty the remainingQty to set
	 */
	public void setRemainingQty(BigDecimal remainingQty) {
		this.remainingQty = remainingQty;
	}

	/**
	 * @return the totalReceivedAmt
	 */
	public BigDecimal getTotalReceivedAmt() {
		return totalReceivedAmt;
	}

	/**
	 * @param totalReceivedAmt the totalReceivedAmt to set
	 */
	public void setTotalReceivedAmt(BigDecimal totalReceivedAmt) {
		this.totalReceivedAmt = totalReceivedAmt;
	}

	/**
	 * @return the totalReceivedQty
	 */
	public BigDecimal getTotalReceivedQty() {
		return totalReceivedQty;
	}

	/**
	 * @param totalReceivedQty the totalReceivedQty to set
	 */
	public void setTotalReceivedQty(BigDecimal totalReceivedQty) {
		this.totalReceivedQty = totalReceivedQty;
	}

	/**
	 * @return the itemTypeProduct
	 */
	public boolean isItemTypeProduct() {
		return itemTypeProduct;
	}

	/**
	 * @param itemTypeProduct the itemTypeProduct to set
	 */
	public void setItemTypeProduct(boolean itemTypeProduct) {
		this.itemTypeProduct = itemTypeProduct;
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
	 * @return the approverList
	 */
	public List<PmUser> getApproverList() {
		if(approverList == null){
			setApproverList(new ArrayList<PmUser>());
		}
		return approverList;
	}

	/**
	 * @param approverList the approverList to set
	 */
	public void setApproverList(List<PmUser> approverList) {
		this.approverList = approverList;
	}

	/**
	 * @return the pmUserList
	 */
	public List<PmUser> getPmUserList() {
		if(pmUserList == null){
			setPmUserList(new ArrayList<PmUser>());
		}
		return pmUserList;
	}

	/**
	 * @param pmUserList the pmUserList to set
	 */
	public void setPmUserList(List<PmUser> pmUserList) {
		this.pmUserList = pmUserList;
	}

	/**
	 * @param userLoginId the userLoginId to set
	 */
	public void setUserLoginId(Long userLoginId) {
		this.userLoginId = userLoginId;
	}

	/**
	 * @return the userLoginId
	 */
	public Long getUserLoginId() {
		return userLoginId;
	}

	/**
	 * @return the selectedAckOfficer
	 */
	public PmUser getSelectedAckOfficer() {
		return selectedAckOfficer;
	}

	/**
	 * @param selectedAckOfficer the selectedAckOfficer to set
	 */
	public void setSelectedAckOfficer(PmUser selectedAckOfficer) {
		this.selectedAckOfficer = selectedAckOfficer;
	}

	/**
	 * @return the issuanceCogstrCheck
	 */
	public boolean isIssuanceCogstrCheck() {
		return issuanceCogstrCheck;
	}

	/**
	 * @param issuanceCogstrCheck the issuanceCogstrCheck to set
	 */
	public void setIssuanceCogstrCheck(boolean issuanceCogstrCheck) {
		this.issuanceCogstrCheck = issuanceCogstrCheck;
	}

}
