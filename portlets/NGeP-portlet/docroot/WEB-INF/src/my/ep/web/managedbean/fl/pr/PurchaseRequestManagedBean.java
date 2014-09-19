/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.pr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgAddress;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmProject;
import my.ep.persistence.entity.pm.common.PmSetia;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

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
	private PmParameter pmPriceType;

	//remove multiple selection from data table
	private List<FlDeliveryAddress> selectedAddressRows;
	//private List<FlFulfilmentItem> selectedItemsRows;
	private List<FlDeliveryAddress> selectedAddressRowsList;
	private List <FlFulfilmentItemAddr>  selectedItemsRows;
	private List <FlFulfilmentItemAddr>  showAllItemsRows;
	
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
	private List<ScRequestNote> requestNoteList;
	private boolean checkPtjFlg = true;
	private ScRequestNote selectedRequestNote;
	private List<PmAddressType> manualAddressList;
	private List<FlFulfilmentItemAddr> requestNoteItemList;
	
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
	private Long currentPtjGroupId;
	private Long currentPreparedOrgId;
	
	
	private PmUserOrg pmUserOrg = new PmUserOrg();
	
	private DualListModel<String> approverModel;
	private HashMap<String, PmUserGroupUser> approverMap;
	private HashMap<Long, Long> scRequestSupplierItemIdMap;
	private HashMap<String, List<FlFulfilmentItemAddr>> zonalTierMap;
	private HashMap<Long, List<FlFulfilmentItemAddr>> tierMap;
	
	//getcontract
	private CtContract ctContract = new CtContract();
	private List <CtContract> contractList;
	private boolean selectedRowFlg;
	
	//getItems
	private List<ScRequestNoteDtl> requestNoteWithDtlsList;
	private List<ScRequestNoteDtl> selectedPurchasedItemList;
	private FlFulfilmentItem  selectedItemRow;
	private List<FlFulfilmentItemAddr> zonalTierItemList;
	private FlFulfilmentItemAddr zonalTierItem;
	
	public List<ScRequestItem> requestNoteItems;
	//ScRequestNoteDtl
	
	//get GOSODO
	private Long goId;
	private Long soId;
	private Long doId;
	private BigDecimal subTotal;
	private FlFulfilmentItemAddr selectedItemAddr;
	
	private HashMap<String, FlFulfilmentItem> flItemMap;
	private HashMap<Long, ScRequestNoteDtl> flReqNoteMap;
	private Map<Long, List<PmAddressType>> manualAddressMap;
	
	// getUserGroup
	private List<PmUserGroup> userGroupList;
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
	
	private String chargeCheck ="false";
	
	// for Manual Address
	private List<PmCountry> countryList;
	private List<PmState> stateList;
	private List<PmDivision> divisionList;
	private List<PmCity> cityList;
	private List<PmDistrict> districtList;
	
	//chargeline
	private TreeNode chargeLineItemRoot ;
	private FlFulfilmentItemAddr chargeLineSelectedItemAddress;
	//private  List<TreeNode> selectedNodes;
	private TreeNode[] selectedNodes;
	private TreeNode selectedNode;
	private FlFulfilmentItemAddr selectedItemChargeSection;
	//private Long pmPrjSetia;
	private BigDecimal orderedAmtGrandTotal ;
	
	// charge Line dropdown lists
	private List<PmVotFund> votFundList;
	private List<PmGlAccount> glAccountList;
	private List<PmProject> projectList;
	private List<PmSetia> setiaList;
	private List<PmSubSetia> subSetiaList;
	private List<PmPrgActivity> prgActivityList;
	private List<SelectItem> manualAddrList;
	private List<SelectItem> supplierBranchesList;
	
	// set chargeline for selected items
	private String votFundCode;
	private String assetIndCode;
	private String glAccountCode;
	private String projectCode;
	private String setiaCode;
	private String subSetiaCode;
	private String prgActivityCode;
	private String votFundType;
	
	private Long votFundId;
	private Long assetIndId;
	private Long glAccountId;
	private Long projectId;
	private Long setiaId;
	private Long subSetiaId;
	private Long prgActivityId;
	private String financingTypeCode;
	
	//receving officer List
	private List<PmAddressType> receivingOffList;
	
	//get Ag Office details
	private Long agOfficeId;
	private String agOfficeName;
	
	//temp
	private String termDays;
	
	private boolean deleteButtonHide;
	
	private int selectedTabIndex;
	
	private boolean required;
	private PmUser pmUser;
	private Date maxDate;
	
	//RequestItemInfo List
	private List<ScSupplierItemPrice> reqItemInfoList;
	private String itemName;
	private Double quantity;
	private Double standardUnitPrice;
	private PmParameter priceType;
	private boolean standardPrice = false;
	private boolean zonalPrice = false;
	private boolean zonalTierPrice = false;
	private boolean emailValidate = false;
	
	private boolean chargeLinePanel=false;
	private boolean programLov=false;
	private boolean projectLov=false;
	private boolean votFundDisplay=false;
	
	private PmVotFund pmVoteFund;
	private PmGlAccount gosodo;
	private PmPrgActivity pmPrgActivity;
	private PmProject pmProject;
	private PmSetia pmSetia;
	private PmSubSetia pmSubSetia;
	
/**
	 * @return the chargeLineItemRoot
	 */
	public TreeNode getChargeLineItemRoot() {
		return chargeLineItemRoot;
	}

	/**
	 * @param chargeLineItemRoot the chargeLineItemRoot to set
	 */
	public void setChargeLineItemRoot(TreeNode chargeLineItemRoot) {
		this.chargeLineItemRoot = chargeLineItemRoot;
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
	public HashMap<String, FlFulfilmentItem> getFlItemMap() {
		if(flItemMap == null){
			setFlItemMap(new HashMap<String, FlFulfilmentItem>());
		}
		return flItemMap;
	}


	/**
	 * @param flItemMap the flItemMap to set
	 */
	public void setFlItemMap(HashMap<String, FlFulfilmentItem> flItemMap) {
		this.flItemMap = flItemMap;
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

	/**
	 * @return the chargeCheck
	 */
	public String getChargeCheck() {
		return chargeCheck;
	}

	/**
	 * @param chargeCheck the chargeCheck to set
	 */
	public void setChargeCheck(String chargeCheck) {
		this.chargeCheck = chargeCheck;
	}

	/**
	 * @return the showAllItemsRows
	 */
	public List<FlFulfilmentItemAddr> getShowAllItemsRows() {
		return showAllItemsRows;
	}

	/**
	 * @param showAllItemsRows the showAllItemsRows to set
	 */
	public void setShowAllItemsRows(List<FlFulfilmentItemAddr> showAllItemsRows) {
		this.showAllItemsRows = showAllItemsRows;
	}

	/**
	 * @return the countryList
	 */
	public List<PmCountry> getCountryList() {
		return countryList;
	}

	/**
	 * @param countryList the countryList to set
	 */
	public void setCountryList(List<PmCountry> countryList) {
		this.countryList = countryList;
	}

	/**
	 * @return the stateList
	 */
	public List<PmState> getStateList() {
		return stateList;
	}

	/**
	 * @param stateList the stateList to set
	 */
	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}

	/**
	 * @return the cityList
	 */
	public List<PmCity> getCityList() {
		return cityList;
	}

	/**
	 * @param cityList the cityList to set
	 */
	public void setCityList(List<PmCity> cityList) {
		this.cityList = cityList;
	}

	/**
	 * @return the districtList
	 */
	public List<PmDistrict> getDistrictList() {
		return districtList;
	}

	/**
	 * @param districtList the districtList to set
	 */
	public void setDistrictList(List<PmDistrict> districtList) {
		this.districtList = districtList;
	}

	/**
	 * @return the divisionList
	 */
	public List<PmDivision> getDivisionList() {
		return divisionList;
	}

	/**
	 * @param divisionList the divisionList to set
	 */
	public void setDivisionList(List<PmDivision> divisionList) {
		this.divisionList = divisionList;
	}

	/**
	 * @return the chargeLineSelectedItemAddress
	 */
	public FlFulfilmentItemAddr getChargeLineSelectedItemAddress() {
		return chargeLineSelectedItemAddress;
	}

	/**
	 * @param chargeLineSelectedItemAddress the chargeLineSelectedItemAddress to set
	 */
	public void setChargeLineSelectedItemAddress(
			FlFulfilmentItemAddr chargeLineSelectedItemAddress) {
		this.chargeLineSelectedItemAddress = chargeLineSelectedItemAddress;
	}
	/**
	 * @return the selectedItemChargeSection
	 */
	public FlFulfilmentItemAddr getSelectedItemChargeSection() {
		return selectedItemChargeSection;
	}

	/**
	 * @param selectedItemChargeSection the selectedItemChargeSection to set
	 */
	public void setSelectedItemChargeSection(
			FlFulfilmentItemAddr selectedItemChargeSection) {
		this.selectedItemChargeSection = selectedItemChargeSection;
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
	 * @return the selectedNode
	 */
	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	/**
	 * @param selectedNode the selectedNode to set
	 */
	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	/**
	 * @return the orderedAmtGrandTotal
	 */
	public BigDecimal getOrderedAmtGrandTotal() {
		return orderedAmtGrandTotal;
	}

	/**
	 * @param orderedAmtGrandTotal the orderedAmtGrandTotal to set
	 */
	public void setOrderedAmtGrandTotal(BigDecimal orderedAmtGrandTotal) {
		this.orderedAmtGrandTotal = orderedAmtGrandTotal;
	}

	/**
	 * @return the votFundList
	 */
	public List<PmVotFund> getVotFundList() {
		return votFundList;
	}

	/**
	 * @param votFundList the votFundList to set
	 */
	public void setVotFundList(List<PmVotFund> votFundList) {
		this.votFundList = votFundList;
	}

	/**
	 * @return the projectList
	 */
	public List<PmProject> getProjectList() {
		return projectList;
	}

	/**
	 * @param projectList the projectList to set
	 */
	public void setProjectList(List<PmProject> projectList) {
		this.projectList = projectList;
	}

	/**
	 * @return the setiaList
	 */
	public List<PmSetia> getSetiaList() {
		return setiaList;
	}

	/**
	 * @param setiaList the setiaList to set
	 */
	public void setSetiaList(List<PmSetia> setiaList) {
		this.setiaList = setiaList;
	}

	/**
	 * @return the subSetiaList
	 */
	public List<PmSubSetia> getSubSetiaList() {
		return subSetiaList;
	}

	/**
	 * @param subSetiaList the subSetiaList to set
	 */
	public void setSubSetiaList(List<PmSubSetia> subSetiaList) {
		this.subSetiaList = subSetiaList;
	}


	/**
	 * @return the prgActivityList
	 */
	public List<PmPrgActivity> getPrgActivityList() {
		return prgActivityList;
	}

	/**
	 * @param prgActivityList the prgActivityList to set
	 */
	public void setPrgActivityList(List<PmPrgActivity> prgActivityList) {
		this.prgActivityList = prgActivityList;
	}

	/**
	 * @return the receivingOffList
	 */
	public List<PmAddressType> getReceivingOffList() {
		return receivingOffList;
	}

	/**
	 * @param receivingOffList the receivingOffList to set
	 */
	public void setReceivingOffList(List<PmAddressType> receivingOffList) {
		this.receivingOffList = receivingOffList;
	}

	/**
	 * @return the agOfficeId
	 */
	public Long getAgOfficeId() {
		return agOfficeId;
	}

	/**
	 * @param agOfficeId the agOfficeId to set
	 */
	public void setAgOfficeId(Long agOfficeId) {
		this.agOfficeId = agOfficeId;
	}

	/**
	 * @return the agOfficeName
	 */
	public String getAgOfficeName() {
		return agOfficeName;
	}

	/**
	 * @param agOfficeName the agOfficeName to set
	 */
	public void setAgOfficeName(String agOfficeName) {
		this.agOfficeName = agOfficeName;
	}

	/**
	 * @param manualAddressList the manualAddressList to set
	 */
	public void setManualAddressList(List<PmAddressType> manualAddressList) {
		this.manualAddressList = manualAddressList;
	}

	/**
	 * @return the manualAddressList
	 */
	public List<PmAddressType> getManualAddressList() {
		return manualAddressList;
	}

	/**
	 * @return the manualAddressMap
	 */
	public Map<Long, List<PmAddressType>> getManualAddressMap() {
		return manualAddressMap;
	}

	/**
	 * @param manualAddressMap the manualAddressMap to set
	 */
	public void setManualAddressMap(Map<Long, List<PmAddressType>> manualAddressMap) {
		this.manualAddressMap = manualAddressMap;
	}

	/**
	 * @return the termDays
	 */
	public String getTermDays() {
		return termDays;
	}

	/**
	 * @param termDays the termDays to set
	 */
	public void setTermDays(String termDays) {
		this.termDays = termDays;
	}

	/**
	 * @return the selectedAddressRowsList
	 */
	public List<FlDeliveryAddress> getSelectedAddressRowsList() {
		return selectedAddressRowsList;
	}

	/**
	 * @param selectedAddressRowsList the selectedAddressRowsList to set
	 */
	public void setSelectedAddressRowsList(
			List<FlDeliveryAddress> selectedAddressRowsList) {
		this.selectedAddressRowsList = selectedAddressRowsList;
	}

	/**
	 * @return the deleteButtonHide
	 */
	public boolean isDeleteButtonHide() {
		return deleteButtonHide;
	}

	/**
	 * @param deleteButtonHide the deleteButtonHide to set
	 */
	public void setDeleteButtonHide(boolean deleteButtonHide) {
		this.deleteButtonHide = deleteButtonHide;
	}

	/**
	 * @return the manualAddrList
	 */
	public List<SelectItem> getManualAddrList() {
		return manualAddrList;
	}

	/**
	 * @param manualAddrList the manualAddrList to set
	 */
	public void setManualAddrList(List<SelectItem> manualAddrList) {
		this.manualAddrList = manualAddrList;
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
	 * @return the votFundId
	 */
	public Long getVotFundId() {
		return votFundId;
	}

	/**
	 * @param votFundId the votFundId to set
	 */
	public void setVotFundId(Long votFundId) {
		this.votFundId = votFundId;
	}

	/**
	 * @return the projectId
	 */
	public Long getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return the setiaId
	 */
	public Long getSetiaId() {
		return setiaId;
	}

	/**
	 * @param setiaId the setiaId to set
	 */
	public void setSetiaId(Long setiaId) {
		this.setiaId = setiaId;
	}

	/**
	 * @return the subSetiaId
	 */
	public Long getSubSetiaId() {
		return subSetiaId;
	}

	/**
	 * @param subSetiaId the subSetiaId to set
	 */
	public void setSubSetiaId(Long subSetiaId) {
		this.subSetiaId = subSetiaId;
	}

	/**
	 * @return the prgActivityId
	 */
	public Long getPrgActivityId() {
		return prgActivityId;
	}

	/**
	 * @param prgActivityId the prgActivityId to set
	 */
	public void setPrgActivityId(Long prgActivityId) {
		this.prgActivityId = prgActivityId;
	}

	/**
	 * @return the supplierBranchesList
	 */
	public List<SelectItem> getSupplierBranchesList() {
		return supplierBranchesList;
	}

	/**
	 * @param supplierBranchesList the supplierBranchesList to set
	 */
	public void setSupplierBranchesList(List<SelectItem> supplierBranchesList) {
		this.supplierBranchesList = supplierBranchesList;
	}

	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public List<PmGlAccount> getGlAccountList() {
		if(glAccountList==null) {
			glAccountList = new ArrayList<PmGlAccount>();
		}
		return glAccountList;
	}

	public void setGlAccountList(List<PmGlAccount> glAccountList) {
		this.glAccountList = glAccountList;
	}

	public Long getAssetIndId() {
		return assetIndId;
	}

	public void setAssetIndId(Long assetIndId) {
		this.assetIndId = assetIndId;
	}

	public Long getGlAccountId() {
		return glAccountId;
	}

	public void setGlAccountId(Long glAccountId) {
		this.glAccountId = glAccountId;
	}

	public String getFinancingTypeCode() {
		return financingTypeCode;
	}

	public void setFinancingTypeCode(String financingTypeCode) {
		this.financingTypeCode = financingTypeCode;
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

	public String getAssetIndCode() {
		return assetIndCode;
	}

	public void setAssetIndCode(String assetIndCode) {
		this.assetIndCode = assetIndCode;
	}

	public String getGlAccountCode() {
		return glAccountCode;
	}

	public void setGlAccountCode(String glAccountCode) {
		this.glAccountCode = glAccountCode;
	}

	public List<PmUserGroup> getUserGroupList() {
		if(userGroupList==null) {
			userGroupList = new ArrayList<PmUserGroup>();
		}
		return userGroupList;
	}

	public void setUserGroupList(List<PmUserGroup> userGroupList) {
		this.userGroupList = userGroupList;
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
	 * @return the maxDate
	 */
	public Date getMaxDate() {
		return maxDate;
	}

	/**
	 * @param maxDate the maxDate to set
	 */
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	/**
	 * @return the currentPtjGroupId
	 */
	public Long getCurrentPtjGroupId() {
		return currentPtjGroupId;
	}

	/**
	 * @param currentPtjGroupId the currentPtjGroupId to set
	 */
	public void setCurrentPtjGroupId(Long currentPtjGroupId) {
		this.currentPtjGroupId = currentPtjGroupId;
	}

	/**
	 * @return the currentPreparedOrgId
	 */
	public Long getCurrentPreparedOrgId() {
		return currentPreparedOrgId;
	}

	/**
	 * @param currentPreparedOrgId the currentPreparedOrgId to set
	 */
	public void setCurrentPreparedOrgId(Long currentPreparedOrgId) {
		this.currentPreparedOrgId = currentPreparedOrgId;
	}


	/**
	 * @return the pmPriceType
	 */
	public PmParameter getPmPriceType() {
		return pmPriceType;
	}

	/**
	 * @param pmPriceType the pmPriceType to set
	 */
	public void setPmPriceType(PmParameter pmPriceType) {
		this.pmPriceType = pmPriceType;
	}

	/**
	 * @return the scRequestSupplierItemIdMap
	 */
	public HashMap<Long, Long> getScRequestSupplierItemIdMap() {
		if(scRequestSupplierItemIdMap == null){
			scRequestSupplierItemIdMap = new HashMap<Long,Long>();
		}
		return scRequestSupplierItemIdMap;
	}

	/**
	 * @param scRequestSupplierItemIdMap the scRequestSupplierItemIdMap to set
	 */
	public void setScRequestSupplierItemIdMap(
			HashMap<Long, Long> scRequestSupplierItemIdMap) {
		this.scRequestSupplierItemIdMap = scRequestSupplierItemIdMap;
	}

	/**
	 * @return the zonalTierMap
	 */
	public HashMap<String, List<FlFulfilmentItemAddr>> getZonalTierMap() {
		if(zonalTierMap == null){
			zonalTierMap = new HashMap<String,List<FlFulfilmentItemAddr>>();
		}
		return zonalTierMap;
	}

	/**
	 * @param zonalTierMap the zonalTierMap to set
	 */
	public void setZonalTierMap(HashMap<String, List<FlFulfilmentItemAddr>> zonalTierMap) {
		this.zonalTierMap = zonalTierMap;
	}

	/**
	 * @return the zonalTierItemList
	 */
	public List<FlFulfilmentItemAddr> getZonalTierItemList() {
		if(zonalTierItemList == null){
			zonalTierItemList = new ArrayList<FlFulfilmentItemAddr>();
		}
		return zonalTierItemList;
	}

	/**
	 * @param zonalTierItemList the zonalTierItemList to set
	 */
	public void setZonalTierItemList(List<FlFulfilmentItemAddr> zonalTierItemList) {
		this.zonalTierItemList = zonalTierItemList;
	}

	/**
	 * @return the zonalTierItem
	 */
	public FlFulfilmentItemAddr getZonalTierItem() {
		return zonalTierItem;
	}

	/**
	 * @param zonalTierItem the zonalTierItem to set
	 */
	public void setZonalTierItem(FlFulfilmentItemAddr zonalTierItem) {
		this.zonalTierItem = zonalTierItem;
	}

	/**
	 * @return the tierMap
	 */
	public HashMap<Long, List<FlFulfilmentItemAddr>> getTierMap() {
		if(tierMap == null){
			tierMap = new HashMap<Long, List<FlFulfilmentItemAddr>>();
		}
		return tierMap;
	}

	/**
	 * @param tierMap the tierMap to set
	 */
	public void setTierMap(HashMap<Long, List<FlFulfilmentItemAddr>> tierMap) {
		this.tierMap = tierMap;
	}

	/**
	 * @return the reqItemInfoList
	 */
	public List<ScSupplierItemPrice> getReqItemInfoList() {
		return reqItemInfoList;
	}

	/**
	 * @param reqItemInfoList the reqItemInfoList to set
	 */
	public void setReqItemInfoList(List<ScSupplierItemPrice> reqItemInfoList) {
		this.reqItemInfoList = reqItemInfoList;
	}

	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @return the quantity
	 */
	public Double getQuantity() {
		return quantity;
	}

	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the priceType
	 */
	public PmParameter getPriceType() {
		return priceType;
	}

	/**
	 * @param priceType the priceType to set
	 */
	public void setPriceType(PmParameter priceType) {
		this.priceType = priceType;
	}

	/**
	 * @return the standardPrice
	 */
	public boolean isStandardPrice() {
		return standardPrice;
	}

	/**
	 * @return the zonalPrice
	 */
	public boolean isZonalPrice() {
		return zonalPrice;
	}

	/**
	 * @return the zonalTierPrice
	 */
	public boolean isZonalTierPrice() {
		return zonalTierPrice;
	}

	/**
	 * @param standardPrice the standardPrice to set
	 */
	public void setStandardPrice(boolean standardPrice) {
		this.standardPrice = standardPrice;
	}

	/**
	 * @param zonalPrice the zonalPrice to set
	 */
	public void setZonalPrice(boolean zonalPrice) {
		this.zonalPrice = zonalPrice;
	}

	/**
	 * @param zonalTierPrice the zonalTierPrice to set
	 */
	public void setZonalTierPrice(boolean zonalTierPrice) {
		this.zonalTierPrice = zonalTierPrice;
	}

	/**
	 * @return the standardUnitPrice
	 */
	public Double getStandardUnitPrice() {
		return standardUnitPrice;
	}

	/**
	 * @param standardUnitPrice the standardUnitPrice to set
	 */
	public void setStandardUnitPrice(Double standardUnitPrice) {
		this.standardUnitPrice = standardUnitPrice;
	}
	
	/**
	 * @return the requestNoteItemList
	 */
	public List<FlFulfilmentItemAddr> getRequestNoteItemList() {
		return requestNoteItemList;
	}

	/**
	 * @param requestNoteItemList the requestNoteItemList to set
	 */
	public void setRequestNoteItemList(
			List<FlFulfilmentItemAddr> requestNoteItemList) {
		this.requestNoteItemList = requestNoteItemList;
	}



	/**
	 * @return the emailValidate
	 */
	public boolean isEmailValidate() {
		return emailValidate;
	}

	/**
	 * @param emailValidate the emailValidate to set
	 */
	public void setEmailValidate(boolean emailValidate) {
		this.emailValidate = emailValidate;
	}

	/**
	 * @return the chargeLinePanel
	 */
	public boolean isChargeLinePanel() {
		return chargeLinePanel;
	}

	/**
	 * @param chargeLinePanel the chargeLinePanel to set
	 */
	public void setChargeLinePanel(boolean chargeLinePanel) {
		this.chargeLinePanel = chargeLinePanel;
	}

	/**
	 * @return the pmVoteFund
	 */
	public PmVotFund getPmVoteFund() {
		return pmVoteFund;
	}

	/**
	 * @param pmVoteFund the pmVoteFund to set
	 */
	public void setPmVoteFund(PmVotFund pmVoteFund) {
		this.pmVoteFund = pmVoteFund;
	}

	/**
	 * @return the gosodo
	 */
	public PmGlAccount getGosodo() {
		return gosodo;
	}

	/**
	 * @param gosodo the gosodo to set
	 */
	public void setGosodo(PmGlAccount gosodo) {
		this.gosodo = gosodo;
	}

	/**
	 * @return the pmPrgActivity
	 */
	public PmPrgActivity getPmPrgActivity() {
		return pmPrgActivity;
	}

	/**
	 * @param pmPrgActivity the pmPrgActivity to set
	 */
	public void setPmPrgActivity(PmPrgActivity pmPrgActivity) {
		this.pmPrgActivity = pmPrgActivity;
	}

	/**
	 * @return the pmProject
	 */
	public PmProject getPmProject() {
		return pmProject;
	}

	/**
	 * @param pmProject the pmProject to set
	 */
	public void setPmProject(PmProject pmProject) {
		this.pmProject = pmProject;
	}

	/**
	 * @return the pmSetia
	 */
	public PmSetia getPmSetia() {
		return pmSetia;
	}

	/**
	 * @param pmSetia the pmSetia to set
	 */
	public void setPmSetia(PmSetia pmSetia) {
		this.pmSetia = pmSetia;
	}

	/**
	 * @return the pmSubSetia
	 */
	public PmSubSetia getPmSubSetia() {
		return pmSubSetia;
	}

	/**
	 * @param pmSubSetia the pmSubSetia to set
	 */
	public void setPmSubSetia(PmSubSetia pmSubSetia) {
		this.pmSubSetia = pmSubSetia;
	}

	/**
	 * @return the programLov
	 */
	public boolean isProgramLov() {
		return programLov;
	}

	/**
	 * @return the projectLov
	 */
	public boolean isProjectLov() {
		return projectLov;
	}

	/**
	 * @param programLov the programLov to set
	 */
	public void setProgramLov(boolean programLov) {
		this.programLov = programLov;
	}

	/**
	 * @param projectLov the projectLov to set
	 */
	public void setProjectLov(boolean projectLov) {
		this.projectLov = projectLov;
	}

	/**
	 * @return the votFundDisplay
	 */
	public boolean isVotFundDisplay() {
		return votFundDisplay;
	}

	/**
	 * @param votFundDisplay the votFundDisplay to set
	 */
	public void setVotFundDisplay(boolean votFundDisplay) {
		this.votFundDisplay = votFundDisplay;
	}

	

	
}
