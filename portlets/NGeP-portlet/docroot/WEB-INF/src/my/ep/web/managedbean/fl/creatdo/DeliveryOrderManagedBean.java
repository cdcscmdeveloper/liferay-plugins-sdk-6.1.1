/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.creatdo;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNoteItem;
import my.ep.persistence.entity.fl.order.FlDelOrderItem;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgAddress;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
//import my.ep.persistence.entity.pm.common.PmMaksud;

/**
 * Managed Bean for Create Purchase Order
 * 
 * @author Giribabu
 * @since 7/10/2013
 */
@ManagedBean(name = "doMB")
@SessionScoped
public class DeliveryOrderManagedBean extends BaseManagedBean {
	private static Logger log = Logger.getLogger(DeliveryOrderManagedBean.class);
	private static final long serialVersionUID = 1L;

	private FlRemark flRemark = new FlRemark();
	private FlAttachment flAttachment = new FlAttachment();
	private FlDeliveryOrder flDeliveryOrder;
	private FlDeliveryAddress deliveryAddress;
	private FlFulfilmentOrder fulfilmentOrder;
    private FlFulfilmentRequest fulfilmentRequest;
    private FlDelOrderItem delOrderItem = new FlDelOrderItem();
	//private List<DeliveryAddressPopup> daPopupList;
	//private List<DeliveryAddressPopup> selectedPopupAddress;
	
	private FlDeliveryAddress selectedAddrRow;
	private List<PmOrgAddress> selectedPmDelAddrList;
	private List<PmOrgAddress> delAddrList;
	private FlDeliveryAddress selectedDelAddr;
	//private List<PmMaksud> maksudCodeList;
	private boolean checkPtjFlg = true;
	
	private String fulfillmentReqId;
	private String fulfilmentOrderId;
	private String statusId;
	private List<FlFulfilmentOrder> pendingDoList;
	private List<FlDeliveryAddress>deliveryAddressList;
	private String docProcess;
	private String buttonVal;
	private String selectedLovDelAddr;
	private String supplierAddress;
	
	private String docType;
	private String statusDesc;
	
	private FlFulfilmentItemAddr selectedItemRow;
	private List <FlFulfilmentItemAddr>selectedItemRowList;
	private FlFulfilmentItemAddr selectedItemAmountSection;
	private List <FlFulfilmentItemAddr>selectedItemRowCalList;
	private List<FlFulfilmentNoteItem> rejectionList;
	
	private String itemSelectedForCal; 
	private int delQtyCal;
	private int delAmtCal;
	
	private List<FlRemark> remarkList;
	private List<FlAttachment> attachmentList;
	
	private PmUser pmUser;
	private String supplierDoRef;
	
	//tab menu
	private int selectedTabIndex;
	
	private boolean modificationCancel;
	private boolean docMaintainRemarks = false;
	
	private String fromPageUrl;

	/**
	 * @return the itemSelectedForCal
	 */
	public String getItemSelectedForCal() {
		return itemSelectedForCal;
	}

	/**
	 * @param itemSelectedForCal the itemSelectedForCal to set
	 */
	public void setItemSelectedForCal(String itemSelectedForCal) {
		this.itemSelectedForCal = itemSelectedForCal;
	}

	/**
	 * @return the flRemark
	 */
	public FlRemark getFlRemark() {
		return flRemark;
	}

	/**
	 * @param flRemark
	 *            the flRemark to set
	 */
	public void setFlRemark(FlRemark flRemark) {
		this.flRemark = flRemark;
	}

	/**
	 * @return the flAttachment
	 */
	public FlAttachment getFlAttachment() {
		return flAttachment;
	}

	/**
	 * @param flAttachment
	 *            the flAttachment to set
	 */
	public void setFlAttachment(FlAttachment flAttachment) {
		this.flAttachment = flAttachment;
	}

	//public CreateDOManagedBean() {}

	/**
	 * @return the selectedAddrRow
	 */
	public FlDeliveryAddress getSelectedAddrRow() {
		return selectedAddrRow;
	}

	/**
	 * @param selectedAddrRow the selectedAddrRow to set
	 */
	public void setSelectedAddrRow(FlDeliveryAddress selectedAddrRow) {
		this.selectedAddrRow = selectedAddrRow;
	}

	/**
	 * @return the selectedPmDelAddrList
	 */
	public List<PmOrgAddress> getSelectedPmDelAddrList() {
		return selectedPmDelAddrList;
	}

	/**
	 * @param selectedPmDelAddrList the selectedPmDelAddrList to set
	 */
	public void setSelectedPmDelAddrList(List<PmOrgAddress> selectedPmDelAddrList) {
		this.selectedPmDelAddrList = selectedPmDelAddrList;
	}

	/**
	 * @return the delAddrList
	 */
	public List<PmOrgAddress> getDelAddrList() {
		return delAddrList;
	}

	/**
	 * @param delAddrList the delAddrList to set
	 */
	public void setDelAddrList(List<PmOrgAddress> delAddrList) {
		this.delAddrList = delAddrList;
	}

	/**
	 * @return the fulfillmentReqId
	 */
	public String getFulfillmentReqId() {
		return fulfillmentReqId;
	}

	/**
	 * @param fulfillmentReqId the fulfillmentReqId to set
	 */
	public void setFulfillmentReqId(String fulfillmentReqId) {
		this.fulfillmentReqId = fulfillmentReqId;
	}

	/**
	 * @return the maksudCodeList
	 *//*
	public List<PmMaksud> getMaksudCodeList() {
		return maksudCodeList;
	}

	*//**
	 * @param maksudCodeList the maksudCodeList to set
	 *//*
	public void setMaksudCodeList(List<PmMaksud> maksudCodeList) {
		this.maksudCodeList = maksudCodeList;
	}*/

	/**
	 * @return the checkPtjFlg
	 */
	public boolean isCheckPtjFlg() {
		return checkPtjFlg;
	}

	/**
	 * @param checkPtjFlg the checkPtjFlg to set
	 */
	public void setCheckPtjFlg(boolean checkPtjFlg) {
		this.checkPtjFlg = checkPtjFlg;
	}

	/**
	 * @return the flDeliveryOrder
	 */
	public FlDeliveryOrder getFlDeliveryOrder() {
		return flDeliveryOrder;
	}

	/**
	 * @param flDeliveryOrder the flDeliveryOrder to set
	 */
	public void setFlDeliveryOrder(FlDeliveryOrder flDeliveryOrder) {
		this.flDeliveryOrder = flDeliveryOrder;
	}

	/**
	 * @return the fulfilmentOrderId
	 */
	public String getFulfilmentOrderId() {
		return fulfilmentOrderId;
	}

	/**
	 * @param fulfilmentOrderId the fulfilmentOrderId to set
	 */
	public void setFulfilmentOrderId(String fulfilmentOrderId) {
		this.fulfilmentOrderId = fulfilmentOrderId;
	}

	/**
	 * @return the statusId
	 */
	public String getStatusId() {
		return statusId;
	}

	/**
	 * @param statusId the statusId to set
	 */
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	/**
	 * @return the pendingDoList
	 */
	public List<FlFulfilmentOrder> getPendingDoList() {
		return pendingDoList;
	}

	/**
	 * @return the deliveryAddressList
	 */
	public List<FlDeliveryAddress> getDeliveryAddressList() {
		return deliveryAddressList;
	}

	/**
	 * @param deliveryAddressList the deliveryAddressList to set
	 */
	public void setDeliveryAddressList(List<FlDeliveryAddress> deliveryAddressList) {
		this.deliveryAddressList = deliveryAddressList;
	}

	/**
	 * @param pendingDoList the pendingDoList to set
	 */
	public void setPendingDoList(List<FlFulfilmentOrder> pendingDoList) {
		this.pendingDoList = pendingDoList;
	}

	/**
	 * @return the deliveryAddress
	 */
	public FlDeliveryAddress getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress the deliveryAddress to set
	 */
	public void setDeliveryAddress(FlDeliveryAddress deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	/**
	 * @return the fulfilmentOrder
	 */
	public FlFulfilmentOrder getFulfilmentOrder() {
		return fulfilmentOrder;
	}

	/**
	 * @param fulfilmentOrder the fulfilmentOrder to set
	 */
	public void setFulfilmentOrder(FlFulfilmentOrder fulfilmentOrder) {
		this.fulfilmentOrder = fulfilmentOrder;
	}

	/**
	 * @return the fulfilmentRequest
	 */
	public FlFulfilmentRequest getFulfilmentRequest() {
		return fulfilmentRequest;
	}

	/**
	 * @param fulfilmentRequest the fulfilmentRequest to set
	 */
	public void setFulfilmentRequest(FlFulfilmentRequest fulfilmentRequest) {
		this.fulfilmentRequest = fulfilmentRequest;
	}

	/**
	 * @return the docProcess
	 */
	public String getDocProcess() {
		return docProcess;
	}

	/**
	 * @param docProcess the docProcess to set
	 */
	public void setDocProcess(String docProcess) {
		this.docProcess = docProcess;
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
	 * @return the selectedLovDelAddr
	 */
	public String getSelectedLovDelAddr() {
		return selectedLovDelAddr;
	}

	/**
	 * @param selectedLovDelAddr the selectedLovDelAddr to set
	 */
	public void setSelectedLovDelAddr(String selectedLovDelAddr) {
		this.selectedLovDelAddr = selectedLovDelAddr;
	}

	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * @return the selectedDelAddr
	 */
	public FlDeliveryAddress getSelectedDelAddr() {
		return selectedDelAddr;
	}

	/**
	 * @param selectedDelAddr the selectedDelAddr to set
	 */
	public void setSelectedDelAddr(FlDeliveryAddress selectedDelAddr) {
		this.selectedDelAddr = selectedDelAddr;
	}

	/**
	 * @return the delOrderItem
	 */
	public FlDelOrderItem getDelOrderItem() {
		return delOrderItem;
	}

	/**
	 * @param delOrderItem the delOrderItem to set
	 */
	public void setDelOrderItem(FlDelOrderItem delOrderItem) {
		this.delOrderItem = delOrderItem;
	}

	/**
	 * @return the statusDesc
	 */
	public String getStatusDesc() {
		return statusDesc;
	}

	/**
	 * @param statusDesc the statusDesc to set
	 */
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
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
	 * @return the selectedItemRow
	 */
	public FlFulfilmentItemAddr getSelectedItemRow() {
		return selectedItemRow;
	}

	/**
	 * @param selectedItemRow the selectedItemRow to set
	 */
	public void setSelectedItemRow(FlFulfilmentItemAddr selectedItemRow) {
		this.selectedItemRow = selectedItemRow;
	}

	/**
	 * @return the selectedItemAmountSection
	 */
	public FlFulfilmentItemAddr getSelectedItemAmountSection() {
		return selectedItemAmountSection;
	}

	/**
	 * @param selectedItemAmountSection the selectedItemAmountSection to set
	 */
	public void setSelectedItemAmountSection(
			FlFulfilmentItemAddr selectedItemAmountSection) {
		this.selectedItemAmountSection = selectedItemAmountSection;
	}

	/**
	 * @return the selectedItemRowList
	 */
	public List<FlFulfilmentItemAddr> getSelectedItemRowList() {
		return selectedItemRowList;
	}

	/**
	 * @param selectedItemRowList the selectedItemRowList to set
	 */
	public void setSelectedItemRowList(
			List<FlFulfilmentItemAddr> selectedItemRowList) {
		this.selectedItemRowList = selectedItemRowList;
	}

	/**
	 * @return the selectedItemRowCalList
	 */
	public List<FlFulfilmentItemAddr> getSelectedItemRowCalList() {
		return selectedItemRowCalList;
	}

	/**
	 * @param selectedItemRowCalList the selectedItemRowCalList to set
	 */
	public void setSelectedItemRowCalList(
			List<FlFulfilmentItemAddr> selectedItemRowCalList) {
		this.selectedItemRowCalList = selectedItemRowCalList;
	}

	/**
	 * @return the delQtyCal
	 */
	public int getDelQtyCal() {
		return delQtyCal;
	}

	/**
	 * @param delQtyCal the delQtyCal to set
	 */
	public void setDelQtyCal(int delQtyCal) {
		this.delQtyCal = delQtyCal;
	}

	/**
	 * @return the delAmtCal
	 */
	public int getDelAmtCal() {
		return delAmtCal;
	}

	/**
	 * @param delAmtCal the delAmtCal to set
	 */
	public void setDelAmtCal(int delAmtCal) {
		this.delAmtCal = delAmtCal;
	}

	/**
	 * @return the remarkList
	 */
	public List<FlRemark> getRemarkList() {
		return remarkList;
	}

	/**
	 * @param remarkList the remarkList to set
	 */
	public void setRemarkList(List<FlRemark> remarkList) {
		this.remarkList = remarkList;
	}

	/**
	 * @return the attachmentList
	 */
	public List<FlAttachment> getAttachmentList() {
		return attachmentList;
	}

	/**
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(List<FlAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	/**
	 * @return the pmUser
	 */
	public PmUser getPmUser() {
		return pmUser;
	}

	/**
	 * @param pmUser the pmUser to set
	 */
	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
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
	 * @return the supplierDoRef
	 */
	public String getSupplierDoRef() {
		return supplierDoRef;
	}

	/**
	 * @param supplierDoRef the supplierDoRef to set
	 */
	public void setSupplierDoRef(String supplierDoRef) {
		this.supplierDoRef = supplierDoRef;
	}

	public List<FlFulfilmentNoteItem> getRejectionList() {
		if(rejectionList==null) {
			rejectionList = new ArrayList<FlFulfilmentNoteItem>();
		}
		return rejectionList;
	}

	public void setRejectionList(List<FlFulfilmentNoteItem> rejectionList) {
		this.rejectionList = rejectionList;
	}

	/**
	 * @return the modificationCancel
	 */
	public boolean isModificationCancel() {
		return modificationCancel;
	}

	/**
	 * @param modificationCancel the modificationCancel to set
	 */
	public void setModificationCancel(boolean modificationCancel) {
		this.modificationCancel = modificationCancel;
	}

	/**
	 * @return the docMaintainRemarks
	 */
	public boolean isDocMaintainRemarks() {
		return docMaintainRemarks;
	}

	/**
	 * @param docMaintainRemarks the docMaintainRemarks to set
	 */
	public void setDocMaintainRemarks(boolean docMaintainRemarks) {
		this.docMaintainRemarks = docMaintainRemarks;
	}

	public String getFromPageUrl() {
		return fromPageUrl;
	}

	public void setFromPageUrl(String fromPageUrl) {
		this.fromPageUrl = fromPageUrl;
	}

	
}
