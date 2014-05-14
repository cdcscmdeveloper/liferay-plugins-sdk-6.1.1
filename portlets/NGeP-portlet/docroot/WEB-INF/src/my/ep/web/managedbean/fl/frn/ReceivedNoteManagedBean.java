/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.frn;

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
import my.ep.persistence.entity.pm.authorization.PmOrgAddress;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * Managed Bean for Create Received Note
 * 
 * @author Giribabu
 * @since 7/10/2013
 */
@ManagedBean(name = "frnMB")
@SessionScoped
public class ReceivedNoteManagedBean extends BaseManagedBean {
	private static Logger log = Logger.getLogger(ReceivedNoteManagedBean.class);
	private static final long serialVersionUID = 1L;

	private FlRemark flRemark = new FlRemark();
	private FlAttachment flAttachment = new FlAttachment();
	private FlFulfilmentNote fulfilmentNote = new FlFulfilmentNote();
	private FlFulfilmentNoteItem fulfilmentNoteItem = new FlFulfilmentNoteItem();
	private FlFulfilmentRequest fulfilmentRequest;
	private FlFulfilmentNoteItem selectedItem = new FlFulfilmentNoteItem();

	//private List<DeliveryAddressPopup> daPopupList;
	//private List<DeliveryAddressPopup> selectedPopupAddress;
	
	private FlDeliveryAddress selectedAddrRow;
	private List<PmOrgAddress> selectedPmDelAddrList;
	private List<PmOrgAddress> delAddrList;
	private FlDeliveryAddress selectedDelAddr;
//	private List<PmMaksud> maksudCodeList;
	private boolean checkPtjFlg = true;
	
	private String fulfillmentReqId;
	private String buttonVal;
	private long deliveryOrderId;
	
	private String docType;
	private String supplierAddress;
	
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
	 * @return the fulfilmentNote
	 */
	public FlFulfilmentNote getFulfilmentNote() {
		return fulfilmentNote;
	}

	/**
	 * @param fulfilmentNote the fulfilmentNote to set
	 */
	public void setFulfilmentNote(FlFulfilmentNote fulfilmentNote) {
		this.fulfilmentNote = fulfilmentNote;
	}

	/**
	 * @return the fulfilmentNoteItem
	 */
	public FlFulfilmentNoteItem getFulfilmentNoteItem() {
		return fulfilmentNoteItem;
	}

	/**
	 * @param fulfilmentNoteItem the fulfilmentNoteItem to set
	 */
	public void setFulfilmentNoteItem(FlFulfilmentNoteItem fulfilmentNoteItem) {
		this.fulfilmentNoteItem = fulfilmentNoteItem;
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
	 * @return the deliveryOrderId
	 */
	public long getDeliveryOrderId() {
		return deliveryOrderId;
	}

	/**
	 * @param deliveryOrderId the deliveryOrderId to set
	 */
	public void setDeliveryOrderId(long deliveryOrderId) {
		this.deliveryOrderId = deliveryOrderId;
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
	 * @return the selectedItem
	 */
	public FlFulfilmentNoteItem getSelectedItem() {
		return selectedItem;
	}

	/**
	 * @param selectedItem the selectedItem to set
	 */
	public void setSelectedItem(FlFulfilmentNoteItem selectedItem) {
		this.selectedItem = selectedItem;
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


}
