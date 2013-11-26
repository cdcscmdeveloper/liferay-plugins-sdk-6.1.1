/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.pr;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNote;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmMaksud;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNote;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

/**
 * Managed Bean for Create Purchase Order
 * 
 * @author Giribabu
 * @since 7/10/2013
 */
@ManagedBean(name = "prMB")
@SessionScoped
public class PurchaseRequestManagedBean extends BaseManagedBean {
	private static Logger log = Logger.getLogger(PurchaseRequestManagedBean.class);
	private static final long serialVersionUID = 1L;

	private FlRemark flRemark = new FlRemark();
	private FlAttachment flAttachment = new FlAttachment();
	private FlFulfilmentRequest fulfilmentRequest;

	//private List<DeliveryAddressPopup> daPopupList;
	//private List<DeliveryAddressPopup> selectedPopupAddress;
	
	private FlDeliveryAddress selectedAddrRow;
	private FlDeliveryAddress selectedDelAddr;
	
	private List<PmOrgAddress> selectedPmDelAddrList;
	private List<PmOrgAddress> delAddrList;
	
	private List<PmAddressType> addressTypesList; 
	private List<PmAddressType> selectedPmAddrTypeList;
	private List<PmMaksud> maksudCodeList;
	private List<ScRequestNote> requestNoteList;
	private boolean checkPtjFlg = true;
	private ScRequestNote selectedRequestNote;
	
	private String fulfillmentReqId;
	private String taskId;
	private String[] actions;
	private String taskName;
	private String docProcess;
	private String flDocId;
	private String buttonVal;
	private Boolean submitButtonStatus =true;
	private String tempLoginId;
	
	private DualListModel<String> approverModel;
	
	//getcontract
	private CtContract ctContract = new CtContract();
	private List <CtContract> contractList;
	private boolean selectedRowFlg;
	
	//getItems
	private List<ScRequestNote> requestNoteWithDtlsList;
	private List<ScRequestNote> selectedPurchasedItemList;
	private FlFulfilmentItem  selectedItemRow;
	
	
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
	 * @return the maksudCodeList
	 */
	public List<PmMaksud> getMaksudCodeList() {
		return maksudCodeList;
	}

	/**
	 * @param maksudCodeList the maksudCodeList to set
	 */
	public void setMaksudCodeList(List<PmMaksud> maksudCodeList) {
		this.maksudCodeList = maksudCodeList;
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
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return the actions
	 */
	public String[] getActions() {
		return actions;
	}

	/**
	 * @param actions the actions to set
	 */
	public void setActions(String[] actions) {
		this.actions = actions;
	}

	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
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
	 * @return the flDocId
	 */
	public String getFlDocId() {
		return flDocId;
	}

	/**
	 * @param flDocId the flDocId to set
	 */
	public void setFlDocId(String flDocId) {
		this.flDocId = flDocId;
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
	 * @param requestNoteList the requestNoteList to set
	 */
	public void setRequestNoteList(List<ScRequestNote> requestNoteList) {
		this.requestNoteList = requestNoteList;
	}

	/**
	 * @return the requestNoteList
	 */
	public List<ScRequestNote> getRequestNoteList() {
		return requestNoteList;
	}

	/**
	 * @return the selectedRequestNote
	 */
	public ScRequestNote getSelectedRequestNote() {
		return selectedRequestNote;
	}

	/**
	 * @param selectedRequestNote the selectedRequestNote to set
	 */
	public void setSelectedRequestNote(ScRequestNote selectedRequestNote) {
		this.selectedRequestNote = selectedRequestNote;
	}


	/**
	 * @return the submitButtonStatus
	 */
	public Boolean getSubmitButtonStatus() {
		return submitButtonStatus;
	}

	/**
	 * @param submitButtonStatus the submitButtonStatus to set
	 */
	public void setSubmitButtonStatus(Boolean submitButtonStatus) {
		this.submitButtonStatus = submitButtonStatus;
	}

	/**
	 * @return the tempLoginId
	 */
	public String getTempLoginId() {
		return tempLoginId;
	}

	/**
	 * @param tempLoginId the tempLoginId to set
	 */
	public void setTempLoginId(String tempLoginId) {
		this.tempLoginId = tempLoginId;
	}

	/**
	 * @return the addressTypesList
	 */
	public List<PmAddressType> getAddressTypesList() {
		return addressTypesList;
	}

	/**
	 * @param addressTypesList the addressTypesList to set
	 */
	public void setAddressTypesList(List<PmAddressType> addressTypesList) {
		this.addressTypesList = addressTypesList;
	}

	/**
	 * @return the selectedPmAddrTypeList
	 */
	public List<PmAddressType> getSelectedPmAddrTypeList() {
		return selectedPmAddrTypeList;
	}

	/**
	 * @param selectedPmAddrTypeList the selectedPmAddrTypeList to set
	 */
	public void setSelectedPmAddrTypeList(List<PmAddressType> selectedPmAddrTypeList) {
		this.selectedPmAddrTypeList = selectedPmAddrTypeList;
	}

	/**
	 * @return the ctContract
	 */
	public CtContract getCtContract() {
		return ctContract;
	}

	/**
	 * @param ctContract the ctContract to set
	 */
	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}

	/**
	 * @return the contractList
	 */
	public List<CtContract> getContractList() {
		return contractList;
	}

	/**
	 * @param contractList the contractList to set
	 */
	public void setContractList(List<CtContract> contractList) {
		this.contractList = contractList;
	}

	/**
	 * @return the selectedRowFlg
	 */
	public boolean isSelectedRowFlg() {
		return selectedRowFlg;
	}

	/**
	 * @param selectedRowFlg the selectedRowFlg to set
	 */
	public void setSelectedRowFlg(boolean selectedRowFlg) {
		this.selectedRowFlg = selectedRowFlg;
	}

	

	/**
	 * @return the requestNoteWithDtlsList
	 */
	public List<ScRequestNote> getRequestNoteWithDtlsList() {
		return requestNoteWithDtlsList;
	}

	/**
	 * @param requestNoteWithDtlsList the requestNoteWithDtlsList to set
	 */
	public void setRequestNoteWithDtlsList(
			List<ScRequestNote> requestNoteWithDtlsList) {
		this.requestNoteWithDtlsList = requestNoteWithDtlsList;
	}

	/**
	 * @return the selectedPurchasedItemList
	 */
	public List<ScRequestNote> getSelectedPurchasedItemList() {
		return selectedPurchasedItemList;
	}

	/**
	 * @param selectedPurchasedItemList the selectedPurchasedItemList to set
	 */
	public void setSelectedPurchasedItemList(
			List<ScRequestNote> selectedPurchasedItemList) {
		this.selectedPurchasedItemList = selectedPurchasedItemList;
	}

	/**
	 * @return the selectedItemRow
	 */
	public FlFulfilmentItem getSelectedItemRow() {
		return selectedItemRow;
	}

	/**
	 * @param selectedItemRow the selectedItemRow to set
	 */
	public void setSelectedItemRow(FlFulfilmentItem selectedItemRow) {
		this.selectedItemRow = selectedItemRow;
	}

	/**
	 * @return the approverModel
	 */
	public DualListModel<String> getApproverModel() {
		return approverModel;
	}

	/**
	 * @param approverModel the approverModel to set
	 */
	public void setApproverModel(DualListModel<String> approverModel) {
		this.approverModel = approverModel;
	}

	
}
