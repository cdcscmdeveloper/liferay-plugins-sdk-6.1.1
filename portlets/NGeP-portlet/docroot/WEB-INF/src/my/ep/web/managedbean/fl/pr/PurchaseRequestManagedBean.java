/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.pr;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgAddress;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmMaksud;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
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

	//remove multiple selection from data table
	private List<FlDeliveryAddress> selectedAddressRows;
	//private List<FlFulfilmentItem> selectedItemsRows;
	//private List<FlDeliveryAddress> selectedAddressRows;
	private List <FlFulfilmentItemAddr>  selectedItemsRows;
	
	private FlDeliveryAddress selectedAddrRow;
	private FlDeliveryAddress selectedDelAddr;
	
	private List<FlDeliveryAddress> selectedAddrList;
	
	private List<PmAddressType> selectedPmDelAddrList;
	private List<PmOrgAddress> delAddrList;
	
	//private List<PmAddressType> addressTypesList; 
	//private List<CtAgencyAddress> addressTypesList;
	private List<PmAddressType> addressTypesList;
	
	//private List<PmAddressType> selectedPmAddrTypeList;FFFF
	private List<PmOrgAddress> selectedAddrTypeList;
	private List<PmMaksud> maksudCodeList;
	private List<ScRequestNote> requestNoteList;
	private boolean checkPtjFlg = true;
	private ScRequestNote selectedRequestNote;
	
	private String fulfillmentReqId;
	private String taskName;
	private String docProcess;
	private String flDocId;
	private String buttonVal;
	private Boolean submitButtonStatus =true;
	private Long docStatusId;
	
	private String ministryName;
	private Long issuedJabatanId;
	private Long receivedJabatanId;
	private Long issuedPtjId;
	private Long receivedPtjId;
	private Long chargedPtjId;
	
	private PmUserOrg pmUserOrg = new PmUserOrg();
	
	private DualListModel<String> approverModel;
	private HashMap<String, PmUserGroupUser> approverMap;
	
	//getcontract
	private CtContract ctContract = new CtContract();
	private List <CtContract> contractList;
	private boolean selectedRowFlg;
	
	//getItems
	private List<ScRequestNoteDtl> requestNoteWithDtlsList;
	private List<ScRequestNoteDtl> selectedPurchasedItemList;
	private FlFulfilmentItem  selectedItemRow;
	
	public List<ScRequestItem> requestNoteItems;
	//ScRequestNoteDtl
	
	//get GOSODO
	private Long goId;
	private Long soId;
	private Long doId;
	private BigDecimal subTotal;
	private FlFulfilmentItemAddr selectedItemAddr;
	
	private HashMap<Long, FlFulfilmentItem> flItemMap;
	private HashMap<Long, ScRequestNoteDtl> flReqNoteMap;
	
	// getUserGroup
	private Map<Long, PmUserGroup> userGroupMap;
	private String fulfilmentRequisitioner;
	
	//getSuplier address details
	private String supplierAddress;
	private PmAddressType pmAddressType;
	private PmAddress pmAddress;
	private PmCountry pmCountry ;
	private PmState pmState;
	private PmDistrict pmDistrict ;
	private PmCity pmCity ;
	private FlDeliveryAddress flManulaAddress;
	
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
	public List<PmAddressType> getSelectedPmDelAddrList() {
		return selectedPmDelAddrList;
	}

	/**
	 * @param selectedPmDelAddrList the selectedPmDelAddrList to set
	 */
	public void setSelectedPmDelAddrList(List<PmAddressType> selectedPmDelAddrList) {
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
	 * @return the addressTypesList
	 *//*
	public List<CtAgencyAddress> getAddressTypesList() {
		return addressTypesList;
	}

	*//**
	 * @param addressTypesList the addressTypesList to set
	 *//*
	public void setAddressTypesList(List<CtAgencyAddress> addressTypesList) {
		this.addressTypesList = addressTypesList;
	}*/

	

	/**
	 * @return the selectedCtAddrTypeList
	 *//*
	public List<CtAgencyAddress> getSelectedCtAddrTypeList() {
		return selectedCtAddrTypeList;
	}

	*//**
	 * @param selectedCtAddrTypeList the selectedCtAddrTypeList to set
	 *//*
	public void setSelectedCtAddrTypeList(
			List<CtAgencyAddress> selectedCtAddrTypeList) {
		this.selectedCtAddrTypeList = selectedCtAddrTypeList;
	}*/

	/**
	 * @return the ctContract
	 */
	public CtContract getCtContract() {
		return ctContract;
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
	 * @return the selectedAddrTypeList
	 */
	public List<PmOrgAddress> getSelectedAddrTypeList() {
		return selectedAddrTypeList;
	}

	/**
	 * @param selectedAddrTypeList the selectedAddrTypeList to set
	 */
	public void setSelectedAddrTypeList(List<PmOrgAddress> selectedAddrTypeList) {
		this.selectedAddrTypeList = selectedAddrTypeList;
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
	public List<ScRequestNoteDtl> getRequestNoteWithDtlsList() {
		return requestNoteWithDtlsList;
	}

	/**
	 * @param requestNoteWithDtlsList the requestNoteWithDtlsList to set
	 */
	public void setRequestNoteWithDtlsList(
			List<ScRequestNoteDtl> requestNoteWithDtlsList) {
		this.requestNoteWithDtlsList = requestNoteWithDtlsList;
	}

	/**
	 * @return the selectedPurchasedItemList
	 */
	public List<ScRequestNoteDtl> getSelectedPurchasedItemList() {
		return selectedPurchasedItemList;
	}

	/**
	 * @param selectedPurchasedItemList the selectedPurchasedItemList to set
	 */
	public void setSelectedPurchasedItemList(
			List<ScRequestNoteDtl> selectedPurchasedItemList) {
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

	/**
	 * @return the approverMap
	 */
	public HashMap<String, PmUserGroupUser> getApproverMap() {
		if(approverMap == null){
			setApproverMap(new HashMap<String,PmUserGroupUser>());
		}
		return approverMap;
	}

	/**
	 * @param approverMap the approverMap to set
	 */
	public void setApproverMap(HashMap<String, PmUserGroupUser> approverMap) {
		this.approverMap = approverMap;
	}

	/**
	 * @return the selectedAddrList
	 */
	public List<FlDeliveryAddress> getSelectedAddrList() {
		return selectedAddrList;
	}

	/**
	 * @param selectedAddrList the selectedAddrList to set
	 */
	public void setSelectedAddrList(List<FlDeliveryAddress> selectedAddrList) {
		this.selectedAddrList = selectedAddrList;
	}

	/**
	 * @return the selectedAddressRows
	 */
	public List<FlDeliveryAddress> getSelectedAddressRows() {
		return selectedAddressRows;
	}

	/**
	 * @param selectedAddressRows the selectedAddressRows to set
	 */
	public void setSelectedAddressRows(List<FlDeliveryAddress> selectedAddressRows) {
		this.selectedAddressRows = selectedAddressRows;
	}

	

	/**
	 * @return the selectedItemsRows
	 */
	public List<FlFulfilmentItemAddr> getSelectedItemsRows() {
		return selectedItemsRows;
	}

	/**
	 * @param selectedItemsRows the selectedItemsRows to set
	 */
	public void setSelectedItemsRows(List<FlFulfilmentItemAddr> selectedItemsRows) {
		this.selectedItemsRows = selectedItemsRows;
	}

	/**
	 * @return the goId
	 */
	public Long getGoId() {
		return goId;
	}

	/**
	 * @param goId the goId to set
	 */
	public void setGoId(Long goId) {
		this.goId = goId;
	}

	/**
	 * @return the soId
	 */
	public Long getSoId() {
		return soId;
	}

	/**
	 * @param soId the soId to set
	 */
	public void setSoId(Long soId) {
		this.soId = soId;
	}

	/**
	 * @return the doId
	 */
	public Long getDoId() {
		return doId;
	}

	/**
	 * @param doId the doId to set
	 */
	public void setDoId(Long doId) {
		this.doId = doId;
	}

	/**
	 * @return the subTotal
	 */
	public BigDecimal getSubTotal() {
		return subTotal;
	}

	/**
	 * @param subTotal the subTotal to set
	 */
	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	/**
	 * @return the selectedItemAddr
	 */
	public FlFulfilmentItemAddr getSelectedItemAddr() {
		return selectedItemAddr;
	}

	/**
	 * @param selectedItemAddr the selectedItemAddr to set
	 */
	public void setSelectedItemAddr(FlFulfilmentItemAddr selectedItemAddr) {
		this.selectedItemAddr = selectedItemAddr;
	}
	
	/**
	 * @return the flItemMap
	 */
	public HashMap<Long, FlFulfilmentItem> getFlItemMap() {
		if(flItemMap == null){
			setFlItemMap(new HashMap<Long, FlFulfilmentItem>());
		}
		return flItemMap;
	}


	/**
	 * @param flItemMap the flItemMap to set
	 */
	public void setFlItemMap(HashMap<Long, FlFulfilmentItem> flItemMap) {
		this.flItemMap = flItemMap;
	}

	/**
	 * @return the userGroupMap
	 */
	public Map<Long, PmUserGroup> getUserGroupMap() {
		return userGroupMap;
	}

	/**
	 * @param userGroupMap the userGroupMap to set
	 */
	public void setUserGroupMap(Map<Long, PmUserGroup> userGroupMap) {
		this.userGroupMap = userGroupMap;
	}

	/**
	 * @return the fulfilmentRequisitioner
	 */
	public String getFulfilmentRequisitioner() {
		return fulfilmentRequisitioner;
	}

	/**
	 * @param fulfilmentRequisitioner the fulfilmentRequisitioner to set
	 */
	public void setFulfilmentRequisitioner(String fulfilmentRequisitioner) {
		this.fulfilmentRequisitioner = fulfilmentRequisitioner;
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
	 * @return the ministryName
	 */
	public String getMinistryName() {
		return ministryName;
	}

	/**
	 * @param ministryName the ministryName to set
	 */
	public void setMinistryName(String ministryName) {
		this.ministryName = ministryName;
	}

	/**
	 * @return the issuedJabatanId
	 */
	public Long getIssuedJabatanId() {
		return issuedJabatanId;
	}

	/**
	 * @param issuedJabatanId the issuedJabatanId to set
	 */
	public void setIssuedJabatanId(Long issuedJabatanId) {
		this.issuedJabatanId = issuedJabatanId;
	}

	/**
	 * @return the receivedJabatanId
	 */
	public Long getReceivedJabatanId() {
		return receivedJabatanId;
	}

	/**
	 * @param receivedJabatanId the receivedJabatanId to set
	 */
	public void setReceivedJabatanId(Long receivedJabatanId) {
		this.receivedJabatanId = receivedJabatanId;
	}

	/**
	 * @return the issuedPtjId
	 */
	public Long getIssuedPtjId() {
		return issuedPtjId;
	}

	/**
	 * @param issuedPtjId the issuedPtjId to set
	 */
	public void setIssuedPtjId(Long issuedPtjId) {
		this.issuedPtjId = issuedPtjId;
	}

	/**
	 * @return the receivedPtjId
	 */
	public Long getReceivedPtjId() {
		return receivedPtjId;
	}

	/**
	 * @param receivedPtjId the receivedPtjId to set
	 */
	public void setReceivedPtjId(Long receivedPtjId) {
		this.receivedPtjId = receivedPtjId;
	}

	/**
	 * @return the pmUserOrg
	 */
	public PmUserOrg getPmUserOrg() {
		return pmUserOrg;
	}

	/**
	 * @param pmUserOrg the pmUserOrg to set
	 */
	public void setPmUserOrg(PmUserOrg pmUserOrg) {
		this.pmUserOrg = pmUserOrg;
	}

	/**
	 * @return the chargedPtjId
	 */
	public Long getChargedPtjId() {
		return chargedPtjId;
	}

	/**
	 * @param chargedPtjId the chargedPtjId to set
	 */
	public void setChargedPtjId(Long chargedPtjId) {
		this.chargedPtjId = chargedPtjId;
	}

	/**
	 * @return the docStatusId
	 */
	public Long getDocStatusId() {
		return docStatusId;
	}

	/**
	 * @param docStatusId the docStatusId to set
	 */
	public void setDocStatusId(Long docStatusId) {
		this.docStatusId = docStatusId;
	}

	/**
	 * @return the requestNoteItems
	 */
	public List<ScRequestItem> getRequestNoteItems() {
		return requestNoteItems;
	}

	/**
	 * @param requestNoteItems the requestNoteItems to set
	 */
	public void setRequestNoteItems(List<ScRequestItem> requestNoteItems) {
		this.requestNoteItems = requestNoteItems;
	}

	/**
	 * @return the flReqNoteMap
	 */
	public HashMap<Long, ScRequestNoteDtl> getFlReqNoteMap() {
		if(flReqNoteMap == null){
			setFlReqNoteMap(new HashMap<Long, ScRequestNoteDtl>());
		}
		return flReqNoteMap;
	}

	/**
	 * @param flReqNoteMap the flReqNoteMap to set
	 */
	public void setFlReqNoteMap(HashMap<Long, ScRequestNoteDtl> flReqNoteMap) {
		this.flReqNoteMap = flReqNoteMap;
	}

	/**
	 * @return the pmAddress
	 */
	public PmAddress getPmAddress() {
		return pmAddress;
	}

	/**
	 * @param pmAddress the pmAddress to set
	 */
	public void setPmAddress(PmAddress pmAddress) {
		this.pmAddress = pmAddress;
	}

	/**
	 * @return the pmCountry
	 */
	public PmCountry getPmCountry() {
		return pmCountry;
	}

	/**
	 * @param pmCountry the pmCountry to set
	 */
	public void setPmCountry(PmCountry pmCountry) {
		this.pmCountry = pmCountry;
	}

	/**
	 * @return the pmState
	 */
	public PmState getPmState() {
		return pmState;
	}

	/**
	 * @param pmState the pmState to set
	 */
	public void setPmState(PmState pmState) {
		this.pmState = pmState;
	}

	/**
	 * @return the pmDistrict
	 */
	public PmDistrict getPmDistrict() {
		return pmDistrict;
	}

	/**
	 * @param pmDistrict the pmDistrict to set
	 */
	public void setPmDistrict(PmDistrict pmDistrict) {
		this.pmDistrict = pmDistrict;
	}

	/**
	 * @return the pmCity
	 */
	public PmCity getPmCity() {
		return pmCity;
	}

	/**
	 * @param pmCity the pmCity to set
	 */
	public void setPmCity(PmCity pmCity) {
		this.pmCity = pmCity;
	}

	/**
	 * @return the pmAddressType
	 */
	public PmAddressType getPmAddressType() {
		return pmAddressType;
	}

	/**
	 * @param pmAddressType the pmAddressType to set
	 */
	public void setPmAddressType(PmAddressType pmAddressType) {
		this.pmAddressType = pmAddressType;
	}

	/**
	 * @return the flManulaAddress
	 */
	public FlDeliveryAddress getFlManulaAddress() {
		return flManulaAddress;
	}

	/**
	 * @param flManulaAddress the flManulaAddress to set
	 */
	public void setFlManulaAddress(FlDeliveryAddress flManulaAddress) {
		this.flManulaAddress = flManulaAddress;
	}
}
