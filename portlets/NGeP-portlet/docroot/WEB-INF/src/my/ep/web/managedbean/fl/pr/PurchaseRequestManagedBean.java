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
import my.ep.persistence.entity.fl.request.VAddressTypeDeliveryAddress;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.FLSupplierItemVO;

import org.primefaces.model.TreeNode;

/**
 * Managed Bean for Create Purchase Order.
 *
 * @author Giribabu
 * @since 7/10/2013
 */
@ManagedBean(name = "prMB")
@SessionScoped
public class PurchaseRequestManagedBean extends BaseManagedBean {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The fl remark. */
	private FlRemark flRemark = new FlRemark();
	
	/** The fl attachment. */
	private FlAttachment flAttachment = new FlAttachment();
	
	/** The fulfilment request. */
	private FlFulfilmentRequest fulfilmentRequest;

	//remove multiple selection from data table
	/** The selected address rows. */
	private List<FlDeliveryAddress> selectedAddressRows;
	
	/** The selected address rows list. */
	private List<FlDeliveryAddress> selectedAddressRowsList;
	
	/** The selected items rows. */
	private List <FlFulfilmentItemAddr>  selectedItemsRows;
	
	/** The show all items rows. */
	private List <FlFulfilmentItemAddr>  showAllItemsRows;
	
	/** The selected addr row. */
	private FlDeliveryAddress selectedAddrRow;
	
	/** The selected del addr. */
	private FlDeliveryAddress selectedDelAddr;
	
	/** The selected addr list. */
	private List<FlDeliveryAddress> selectedAddrList;
	
	/** The selected pm del addr list. */
	private List<PmAddressType> selectedPmDelAddrList;
	
	/** The address types list. */
	private List<PmAddressType> addressTypesList;
	
	/** The request note list. */
	private List<ScRequestNote> requestNoteList;
	
	/** The check ptj flg. */
	private boolean checkPtjFlg = true;
	
	/** The selected request note. */
	private ScRequestNote selectedRequestNote;
	
	/** The manual address list. */
	private List<PmAddressType> manualAddressList;
	
	/** The request note item list. */
	private List<FlFulfilmentItemAddr> requestNoteItemList;
	
	/** The fulfillment req id. */
	private String fulfillmentReqId;
	
	/** The task name. */
	private String taskName;
	
	/** The doc process. */
	private String docProcess;
	
	/** The fl doc id. */
	private String flDocId;
	
	/** The button val. */
	private String buttonVal;
	
	/** The submit button status. */
	private Boolean submitButtonStatus =true;
	
	/** The doc status id. */
	private Long docStatusId;
	
	/** The ministry name. */
	private String ministryName;
	
	/** The issued jabatan id. */
	private Long issuedJabatanId;
	
	/** The received jabatan id. */
	private Long receivedJabatanId;
	
	/** The issued ptj id. */
	private Long issuedPtjId;
	
	/** The received ptj id. */
	private Long receivedPtjId;
	
	/** The charged ptj id. */
	private Long chargedPtjId;
	
	/** The current ptj group id. */
	private Long currentPtjGroupId;
	
	/** The current prepared org id. */
	private Long currentPreparedOrgId;
	
	/** The pm user org. */
	private PmUserOrg pmUserOrg = new PmUserOrg();
	
	/** The approver map. */
	private HashMap<Long, PmUserGroupUser> approverMap;
	
	/** The sc request supplier item id map. */
	private HashMap<Long, Long> scRequestSupplierItemIdMap;
	
	/** The zonal tier map. */
	private HashMap<String, List<FlFulfilmentItemAddr>> zonalTierMap;
	
	/** The tier map. */
	private HashMap<Long, List<FlFulfilmentItemAddr>> tierMap;
	
	/** The ct contract. */
	private CtContract ctContract = new CtContract();
	
	/** The contract list. */
	private List <CtContract> contractList;
	private List<VAddressTypeDeliveryAddress> selectedDeliveryFullAddr;
	
	/** The selected row flg. */
	private boolean selectedRowFlg;
	
	private boolean declarationCheck;
	
	private String taskDeadLine;
	private FlDeliveryAddress flManualAddress;
	
	//getItems
	/** The request note with dtls list. */
	private List<ScRequestNoteDtl> requestNoteWithDtlsList;
	
	/** The selected purchased item list. */
	private List<ScRequestNoteDtl> selectedPurchasedItemList;
	
	/** The selected item row. */
	private FlFulfilmentItem  selectedItemRow;
	
	/** The zonal tier item list. */
	private List<FlFulfilmentItemAddr> zonalTierItemList;
	
	/** The zonal tier item. */
	private FlFulfilmentItemAddr zonalTierItem;
	
	/** The request note items. */
	public List<ScRequestItem> requestNoteItems;
	
	//get GOSODO
	/** The go id. */
	private Long goId;
	
	/** The so id. */
	private Long soId;
	
	/** The do id. */
	private Long doId;
	
	/** The sub total. */
	private BigDecimal subTotal;
	
	/** The selected item addr. */
	private FlFulfilmentItemAddr selectedItemAddr;
	
	/** The fl req note map. */
	private HashMap<Long, ScRequestNoteDtl> flReqNoteMap;
	
	/** The manual address map. */
	private Map<Long, List<PmAddressType>> manualAddressMap;
	
	// getUserGroup
	/** The user group list. */
	private List<PmUserGroup> userGroupList;
	
	/** The fulfilment requisitioner. */
	private String fulfilmentRequisitioner;
	
	//getSuplier address details
	/** The supplier address. */
	private String supplierAddress;
	
	/** The pm address type. */
	private PmAddressType pmAddressType;
	
	/** The pm address. */
	private PmAddress pmAddress;
	
	/** The pm country. */
	private PmCountry pmCountry ;
	
	/** The pm state. */
	private PmState pmState;
	
	/** The pm district. */
	private PmDistrict pmDistrict ;
	
	/** The pm city. */
	private PmCity pmCity ;
	
	/** The charge check. */
	private String chargeCheck ="false";
	
	// for Manual Address
	/** The country list. */
	private List<PmCountry> countryList;
	
	/** The state list. */
	private List<PmState> stateList;
	
	/** The division list. */
	private List<PmDivision> divisionList;
	
	/** The city list. */
	private List<PmCity> cityList;
	
	/** The district list. */
	private List<PmDistrict> districtList;
	
	//chargeline
	/** The charge line item root. */
	private TreeNode chargeLineItemRoot ;
	
	/** The charge line selected item address. */
	private FlFulfilmentItemAddr chargeLineSelectedItemAddress;
	
	/** The selected nodes. */
	private TreeNode[] selectedNodes;
	
	/** The selected node. */
	private TreeNode selectedNode;
	
	/** The selected item charge section. */
	private FlFulfilmentItemAddr selectedItemChargeSection;
	
	/** The ordered amt grand total. */
	private BigDecimal orderedAmtGrandTotal ;
	
	// charge Line dropdown lists
	/** The vot fund list. */
	private List<PmVotFund> votFundList;
	
	/** The gl account list. */
	private List<PmGlAccount> glAccountList;
	
	/** The sub setia list. */
	private List<PmSubSetia> subSetiaList;
	
	/** The prg activity list. */
	private List<PmPrgActivity> prgActivityList;
	
	/** The manual addr list. */
	private List<SelectItem> manualAddrList;
	
	/** The supplier branches list. */
	private List<SelectItem> supplierBranchesList;
	
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
	
	/** The vot fund id. */
	private Long votFundId;
	
	/** The asset ind id. */
	private Long assetIndId;
	
	/** The gl account id. */
	private Long glAccountId;
	
	/** The sub setia id. */
	private Long subSetiaId;
	
	/** The prg activity id. */
	private Long prgActivityId;
	
	/** The financing type code. */
	private String financingTypeCode;
	
	//receving officer List
	/** The receiving off list. */
	private List<PmAddressType> receivingOffList;
	
	//get Ag Office details
	/** The ag office id. */
	private Long agOfficeId;
	
	/** The ag office name. */
	private String agOfficeName;
	
	//temp
	/** The term days. */
	private String termDays;
	
	/** The delete button hide. */
	private boolean deleteButtonHide;
	
	/** The selected tab index. */
	private int selectedTabIndex;
	
	/** The required. */
	private boolean required;
	
	/** The pm user. */
	private PmUser pmUser;
	
	/** The max date. */
	private Date maxDate;
	
	//RequestItemInfo List
	/** The req item info list. */
	private List<ScSupplierItemPrice> reqItemInfoList;
	
	/** The item name. */
	private String itemName;
	
	/** The quantity. */
	private Double quantity;
	
	/** The standard unit price. */
	private Double standardUnitPrice;
	
	/** The price type. */
	private PmParameter priceType;
	
	/** The standard price. */
	private boolean standardPrice = false;
	
	/** The zonal price. */
	private boolean zonalPrice = false;
	
	/** The zonal tier price. */
	private boolean zonalTierPrice = false;
	
	/** The email validate. */
	private boolean emailValidate = false;
	
	/** The charge line panel. */
	private boolean chargeLinePanel=false;
	
	/** The program lov. */
	private boolean programLov=false;
	
	/** The project lov. */
	private boolean projectLov=false;
	
	/** The vot fund display. */
	private boolean votFundDisplay=false;
	
	/** The pm vote fund. */
	private PmVotFund pmVoteFund;
	
	/** The gosodo. */
	private PmGlAccount gosodo;
	
	/** The pm prg activity. */
	private PmPrgActivity pmPrgActivity;
	
	/** The pm sub setia. */
	private PmSubSetia pmSubSetia;
	
	/** The item addr vote fund disp. */
	private FlFulfilmentItemAddr itemAddrVoteFundDisp;
	
	/** The root zone tree. */
	private	TreeNode	rootZoneTree;
	
	/** The selected tree node. */
	private TreeNode selectedTreeNode;
	
	/** The supplier item price list. */
	private List<FLSupplierItemVO>	supplierItemPriceList = new ArrayList<FLSupplierItemVO>();
	
	/** The selected zone tree. */
	private	List<TreeNode>	selectedZoneTree	=	new ArrayList<TreeNode>();
	
	/** The query display. */
	private boolean queryDisplay=false;
	
	/** The fee vote fund. */
	private PmVotFund feeVoteFund;
	
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
	
	//uom
	/** The measurement codes. */
	private String measurementCodes;	
	
	/** The measurement names. */
	private String measurementNames;
	
	/** The fl doc maintain. */
	private boolean flDocMaintain = false;
	
	/** The is purchase request link. */
	private boolean isPurchaseRequestLink = false;
	
	/** The back to document. */
	private String backToDocument;
	private List<FlRemark> previousRemarkList;
	private List<FlAttachment> previousAttachmentList;	
	
	private String gstRegistrationNo;
	private Date gstRegistrationDate;
	private boolean gfmasError=false;
	private String gfmasErrorContent;
	private String gfmasErrorCode;
	private String gfmasErrorDesc;
	private PmOrgProfile currentChargePtjGroup;
	private Long selectedApprover;
	private List<Long> approverIdList;
	private List<Long> bpmApproverList;
	
	private boolean selectChargeLineRow=false;
	private boolean gstRateChange;
	
	private boolean hyperLinkPage = false;
	private boolean pidValidityExpired = false;
	private List<PmUserGroupUser> approverGroupUserList;
	
	private boolean divisionDisplay = true;
	
	private Long ministryProfileId;
	private Long jabatanProfileId;
	private String ptjCodeName;
	private String ptjCodeNameSearchValues;
	
	/** The ordered amt grand total. */
	private BigDecimal orderSummaryOrderedAmtGrandTotal ;
	
	private String itemDesc;
	
/**
 * Gets the charge line item root.
 *
 * @return the chargeLineItemRoot
 */
	public TreeNode getChargeLineItemRoot() {
		return chargeLineItemRoot;
	}

	/**
	 * Sets the charge line item root.
	 *
	 * @param chargeLineItemRoot the chargeLineItemRoot to set
	 */
	public void setChargeLineItemRoot(TreeNode chargeLineItemRoot) {
		this.chargeLineItemRoot = chargeLineItemRoot;
	}

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
	 * Gets the selected pm del addr list.
	 *
	 * @return the selectedPmDelAddrList
	 */
	public List<PmAddressType> getSelectedPmDelAddrList() {
		return selectedPmDelAddrList;
	}

	/**
	 * Sets the selected pm del addr list.
	 *
	 * @param selectedPmDelAddrList the selectedPmDelAddrList to set
	 */
	public void setSelectedPmDelAddrList(List<PmAddressType> selectedPmDelAddrList) {
		this.selectedPmDelAddrList = selectedPmDelAddrList;
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
	 * Gets the task name.
	 *
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * Sets the task name.
	 *
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * Gets the doc process.
	 *
	 * @return the docProcess
	 */
	public String getDocProcess() {
		return docProcess;
	}

	/**
	 * Sets the doc process.
	 *
	 * @param docProcess the docProcess to set
	 */
	public void setDocProcess(String docProcess) {
		this.docProcess = docProcess;
	}

	/**
	 * Gets the fl doc id.
	 *
	 * @return the flDocId
	 */
	public String getFlDocId() {
		return flDocId;
	}

	/**
	 * Sets the fl doc id.
	 *
	 * @param flDocId the flDocId to set
	 */
	public void setFlDocId(String flDocId) {
		this.flDocId = flDocId;
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
	 * Sets the request note list.
	 *
	 * @param requestNoteList the requestNoteList to set
	 */
	public void setRequestNoteList(List<ScRequestNote> requestNoteList) {
		this.requestNoteList = requestNoteList;
	}

	/**
	 * Gets the request note list.
	 *
	 * @return the requestNoteList
	 */
	public List<ScRequestNote> getRequestNoteList() {
		return requestNoteList;
	}

	/**
	 * Gets the selected request note.
	 *
	 * @return the selectedRequestNote
	 */
	public ScRequestNote getSelectedRequestNote() {
		return selectedRequestNote;
	}

	/**
	 * Sets the selected request note.
	 *
	 * @param selectedRequestNote the selectedRequestNote to set
	 */
	public void setSelectedRequestNote(ScRequestNote selectedRequestNote) {
		this.selectedRequestNote = selectedRequestNote;
	}


	/**
	 * Gets the submit button status.
	 *
	 * @return the submitButtonStatus
	 */
	public Boolean getSubmitButtonStatus() {
		return submitButtonStatus;
	}

	/**
	 * Sets the submit button status.
	 *
	 * @param submitButtonStatus the submitButtonStatus to set
	 */
	public void setSubmitButtonStatus(Boolean submitButtonStatus) {
		this.submitButtonStatus = submitButtonStatus;
	}

	/**
	 * Gets the ct contract.
	 *
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
	 * Gets the address types list.
	 *
	 * @return the addressTypesList
	 */
	public List<PmAddressType> getAddressTypesList() {
		return addressTypesList;
	}

	/**
	 * Sets the address types list.
	 *
	 * @param addressTypesList the addressTypesList to set
	 */
	public void setAddressTypesList(List<PmAddressType> addressTypesList) {
		this.addressTypesList = addressTypesList;
	}

	/**
	 * Sets the ct contract.
	 *
	 * @param ctContract the ctContract to set
	 */
	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}

	/**
	 * Gets the contract list.
	 *
	 * @return the contractList
	 */
	public List<CtContract> getContractList() {
		return contractList;
	}

	/**
	 * Sets the contract list.
	 *
	 * @param contractList the contractList to set
	 */
	public void setContractList(List<CtContract> contractList) {
		this.contractList = contractList;
	}

	/**
	 * Checks if is selected row flg.
	 *
	 * @return the selectedRowFlg
	 */
	public boolean isSelectedRowFlg() {
		return selectedRowFlg;
	}

	/**
	 * Sets the selected row flg.
	 *
	 * @param selectedRowFlg the selectedRowFlg to set
	 */
	public void setSelectedRowFlg(boolean selectedRowFlg) {
		this.selectedRowFlg = selectedRowFlg;
	}

	

	/**
	 * Gets the request note with dtls list.
	 *
	 * @return the requestNoteWithDtlsList
	 */
	public List<ScRequestNoteDtl> getRequestNoteWithDtlsList() {
		return requestNoteWithDtlsList;
	}

	/**
	 * Sets the request note with dtls list.
	 *
	 * @param requestNoteWithDtlsList the requestNoteWithDtlsList to set
	 */
	public void setRequestNoteWithDtlsList(
			List<ScRequestNoteDtl> requestNoteWithDtlsList) {
		this.requestNoteWithDtlsList = requestNoteWithDtlsList;
	}

	/**
	 * Gets the selected purchased item list.
	 *
	 * @return the selectedPurchasedItemList
	 */
	public List<ScRequestNoteDtl> getSelectedPurchasedItemList() {
		return selectedPurchasedItemList;
	}

	/**
	 * Sets the selected purchased item list.
	 *
	 * @param selectedPurchasedItemList the selectedPurchasedItemList to set
	 */
	public void setSelectedPurchasedItemList(
			List<ScRequestNoteDtl> selectedPurchasedItemList) {
		this.selectedPurchasedItemList = selectedPurchasedItemList;
	}

	/**
	 * Gets the selected item row.
	 *
	 * @return the selectedItemRow
	 */
	public FlFulfilmentItem getSelectedItemRow() {
		return selectedItemRow;
	}

	/**
	 * Sets the selected item row.
	 *
	 * @param selectedItemRow the selectedItemRow to set
	 */
	public void setSelectedItemRow(FlFulfilmentItem selectedItemRow) {
		this.selectedItemRow = selectedItemRow;
	}

	/**
	 * Gets the approver map.
	 *
	 * @return the approverMap
	 */
	public HashMap<Long, PmUserGroupUser> getApproverMap() {
		if(approverMap == null){
			setApproverMap(new HashMap<Long,PmUserGroupUser>());
		}
		return approverMap;
	}

	/**
	 * Sets the approver map.
	 *
	 * @param approverMap the approverMap to set
	 */
	public void setApproverMap(HashMap<Long, PmUserGroupUser> approverMap) {
		this.approverMap = approverMap;
	}

	/**
	 * Gets the selected addr list.
	 *
	 * @return the selectedAddrList
	 */
	public List<FlDeliveryAddress> getSelectedAddrList() {
		return selectedAddrList;
	}

	/**
	 * Sets the selected addr list.
	 *
	 * @param selectedAddrList the selectedAddrList to set
	 */
	public void setSelectedAddrList(List<FlDeliveryAddress> selectedAddrList) {
		this.selectedAddrList = selectedAddrList;
	}

	/**
	 * Gets the selected address rows.
	 *
	 * @return the selectedAddressRows
	 */
	public List<FlDeliveryAddress> getSelectedAddressRows() {
		return selectedAddressRows;
	}

	/**
	 * Sets the selected address rows.
	 *
	 * @param selectedAddressRows the selectedAddressRows to set
	 */
	public void setSelectedAddressRows(List<FlDeliveryAddress> selectedAddressRows) {
		this.selectedAddressRows = selectedAddressRows;
	}

	

	/**
	 * Gets the selected items rows.
	 *
	 * @return the selectedItemsRows
	 */
	public List<FlFulfilmentItemAddr> getSelectedItemsRows() {
		return selectedItemsRows;
	}

	/**
	 * Sets the selected items rows.
	 *
	 * @param selectedItemsRows the selectedItemsRows to set
	 */
	public void setSelectedItemsRows(List<FlFulfilmentItemAddr> selectedItemsRows) {
		this.selectedItemsRows = selectedItemsRows;
	}

	/**
	 * Gets the go id.
	 *
	 * @return the goId
	 */
	public Long getGoId() {
		return goId;
	}

	/**
	 * Sets the go id.
	 *
	 * @param goId the goId to set
	 */
	public void setGoId(Long goId) {
		this.goId = goId;
	}

	/**
	 * Gets the so id.
	 *
	 * @return the soId
	 */
	public Long getSoId() {
		return soId;
	}

	/**
	 * Sets the so id.
	 *
	 * @param soId the soId to set
	 */
	public void setSoId(Long soId) {
		this.soId = soId;
	}

	/**
	 * Gets the do id.
	 *
	 * @return the doId
	 */
	public Long getDoId() {
		return doId;
	}

	/**
	 * Sets the do id.
	 *
	 * @param doId the doId to set
	 */
	public void setDoId(Long doId) {
		this.doId = doId;
	}

	/**
	 * Gets the sub total.
	 *
	 * @return the subTotal
	 */
	public BigDecimal getSubTotal() {
		return subTotal;
	}

	/**
	 * Sets the sub total.
	 *
	 * @param subTotal the subTotal to set
	 */
	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	/**
	 * Gets the selected item addr.
	 *
	 * @return the selectedItemAddr
	 */
	public FlFulfilmentItemAddr getSelectedItemAddr() {
		return selectedItemAddr;
	}

	/**
	 * Sets the selected item addr.
	 *
	 * @param selectedItemAddr the selectedItemAddr to set
	 */
	public void setSelectedItemAddr(FlFulfilmentItemAddr selectedItemAddr) {
		this.selectedItemAddr = selectedItemAddr;
	}

	/**
	 * Gets the fulfilment requisitioner.
	 *
	 * @return the fulfilmentRequisitioner
	 */
	public String getFulfilmentRequisitioner() {
		return fulfilmentRequisitioner;
	}

	/**
	 * Sets the fulfilment requisitioner.
	 *
	 * @param fulfilmentRequisitioner the fulfilmentRequisitioner to set
	 */
	public void setFulfilmentRequisitioner(String fulfilmentRequisitioner) {
		this.fulfilmentRequisitioner = fulfilmentRequisitioner;
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
	 * Gets the ministry name.
	 *
	 * @return the ministryName
	 */
	public String getMinistryName() {
		return ministryName;
	}

	/**
	 * Sets the ministry name.
	 *
	 * @param ministryName the ministryName to set
	 */
	public void setMinistryName(String ministryName) {
		this.ministryName = ministryName;
	}

	/**
	 * Gets the issued jabatan id.
	 *
	 * @return the issuedJabatanId
	 */
	public Long getIssuedJabatanId() {
		return issuedJabatanId;
	}

	/**
	 * Sets the issued jabatan id.
	 *
	 * @param issuedJabatanId the issuedJabatanId to set
	 */
	public void setIssuedJabatanId(Long issuedJabatanId) {
		this.issuedJabatanId = issuedJabatanId;
	}

	/**
	 * Gets the received jabatan id.
	 *
	 * @return the receivedJabatanId
	 */
	public Long getReceivedJabatanId() {
		return receivedJabatanId;
	}

	/**
	 * Sets the received jabatan id.
	 *
	 * @param receivedJabatanId the receivedJabatanId to set
	 */
	public void setReceivedJabatanId(Long receivedJabatanId) {
		this.receivedJabatanId = receivedJabatanId;
	}

	/**
	 * Gets the issued ptj id.
	 *
	 * @return the issuedPtjId
	 */
	public Long getIssuedPtjId() {
		return issuedPtjId;
	}

	/**
	 * Sets the issued ptj id.
	 *
	 * @param issuedPtjId the issuedPtjId to set
	 */
	public void setIssuedPtjId(Long issuedPtjId) {
		this.issuedPtjId = issuedPtjId;
	}

	/**
	 * Gets the received ptj id.
	 *
	 * @return the receivedPtjId
	 */
	public Long getReceivedPtjId() {
		return receivedPtjId;
	}

	/**
	 * Sets the received ptj id.
	 *
	 * @param receivedPtjId the receivedPtjId to set
	 */
	public void setReceivedPtjId(Long receivedPtjId) {
		this.receivedPtjId = receivedPtjId;
	}

	/**
	 * Gets the pm user org.
	 *
	 * @return the pmUserOrg
	 */
	public PmUserOrg getPmUserOrg() {
		return pmUserOrg;
	}

	/**
	 * Sets the pm user org.
	 *
	 * @param pmUserOrg the pmUserOrg to set
	 */
	public void setPmUserOrg(PmUserOrg pmUserOrg) {
		this.pmUserOrg = pmUserOrg;
	}

	/**
	 * Gets the charged ptj id.
	 *
	 * @return the chargedPtjId
	 */
	public Long getChargedPtjId() {
		return chargedPtjId;
	}

	/**
	 * Sets the charged ptj id.
	 *
	 * @param chargedPtjId the chargedPtjId to set
	 */
	public void setChargedPtjId(Long chargedPtjId) {
		this.chargedPtjId = chargedPtjId;
	}

	/**
	 * Gets the doc status id.
	 *
	 * @return the docStatusId
	 */
	public Long getDocStatusId() {
		return docStatusId;
	}

	/**
	 * Sets the doc status id.
	 *
	 * @param docStatusId the docStatusId to set
	 */
	public void setDocStatusId(Long docStatusId) {
		this.docStatusId = docStatusId;
	}

	/**
	 * Gets the request note items.
	 *
	 * @return the requestNoteItems
	 */
	public List<ScRequestItem> getRequestNoteItems() {
		return requestNoteItems;
	}

	/**
	 * Sets the request note items.
	 *
	 * @param requestNoteItems the requestNoteItems to set
	 */
	public void setRequestNoteItems(List<ScRequestItem> requestNoteItems) {
		this.requestNoteItems = requestNoteItems;
	}

	/**
	 * Gets the fl req note map.
	 *
	 * @return the flReqNoteMap
	 */
	public HashMap<Long, ScRequestNoteDtl> getFlReqNoteMap() {
		if(flReqNoteMap == null){
			setFlReqNoteMap(new HashMap<Long, ScRequestNoteDtl>());
		}
		return flReqNoteMap;
	}

	/**
	 * Sets the fl req note map.
	 *
	 * @param flReqNoteMap the flReqNoteMap to set
	 */
	public void setFlReqNoteMap(HashMap<Long, ScRequestNoteDtl> flReqNoteMap) {
		this.flReqNoteMap = flReqNoteMap;
	}

	/**
	 * Gets the pm address.
	 *
	 * @return the pmAddress
	 */
	public PmAddress getPmAddress() {
		return pmAddress;
	}

	/**
	 * Sets the pm address.
	 *
	 * @param pmAddress the pmAddress to set
	 */
	public void setPmAddress(PmAddress pmAddress) {
		this.pmAddress = pmAddress;
	}

	/**
	 * Gets the pm country.
	 *
	 * @return the pmCountry
	 */
	public PmCountry getPmCountry() {
		return pmCountry;
	}

	/**
	 * Sets the pm country.
	 *
	 * @param pmCountry the pmCountry to set
	 */
	public void setPmCountry(PmCountry pmCountry) {
		this.pmCountry = pmCountry;
	}

	/**
	 * Gets the pm state.
	 *
	 * @return the pmState
	 */
	public PmState getPmState() {
		return pmState;
	}

	/**
	 * Sets the pm state.
	 *
	 * @param pmState the pmState to set
	 */
	public void setPmState(PmState pmState) {
		this.pmState = pmState;
	}

	/**
	 * Gets the pm district.
	 *
	 * @return the pmDistrict
	 */
	public PmDistrict getPmDistrict() {
		return pmDistrict;
	}

	/**
	 * Sets the pm district.
	 *
	 * @param pmDistrict the pmDistrict to set
	 */
	public void setPmDistrict(PmDistrict pmDistrict) {
		this.pmDistrict = pmDistrict;
	}

	/**
	 * Gets the pm city.
	 *
	 * @return the pmCity
	 */
	public PmCity getPmCity() {
		return pmCity;
	}

	/**
	 * Sets the pm city.
	 *
	 * @param pmCity the pmCity to set
	 */
	public void setPmCity(PmCity pmCity) {
		this.pmCity = pmCity;
	}

	/**
	 * Gets the pm address type.
	 *
	 * @return the pmAddressType
	 */
	public PmAddressType getPmAddressType() {
		return pmAddressType;
	}

	/**
	 * Sets the pm address type.
	 *
	 * @param pmAddressType the pmAddressType to set
	 */
	public void setPmAddressType(PmAddressType pmAddressType) {
		this.pmAddressType = pmAddressType;
	}

	/**
	 * Gets the charge check.
	 *
	 * @return the chargeCheck
	 */
	public String getChargeCheck() {
		return chargeCheck;
	}

	/**
	 * Sets the charge check.
	 *
	 * @param chargeCheck the chargeCheck to set
	 */
	public void setChargeCheck(String chargeCheck) {
		this.chargeCheck = chargeCheck;
	}

	/**
	 * Gets the show all items rows.
	 *
	 * @return the showAllItemsRows
	 */
	public List<FlFulfilmentItemAddr> getShowAllItemsRows() {
		return showAllItemsRows;
	}

	/**
	 * Sets the show all items rows.
	 *
	 * @param showAllItemsRows the showAllItemsRows to set
	 */
	public void setShowAllItemsRows(List<FlFulfilmentItemAddr> showAllItemsRows) {
		this.showAllItemsRows = showAllItemsRows;
	}

	/**
	 * Gets the country list.
	 *
	 * @return the countryList
	 */
	public List<PmCountry> getCountryList() {
		return countryList;
	}

	/**
	 * Sets the country list.
	 *
	 * @param countryList the countryList to set
	 */
	public void setCountryList(List<PmCountry> countryList) {
		this.countryList = countryList;
	}

	/**
	 * Gets the state list.
	 *
	 * @return the stateList
	 */
	public List<PmState> getStateList() {
		return stateList;
	}

	/**
	 * Sets the state list.
	 *
	 * @param stateList the stateList to set
	 */
	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}

	/**
	 * Gets the city list.
	 *
	 * @return the cityList
	 */
	public List<PmCity> getCityList() {
		return cityList;
	}

	/**
	 * Sets the city list.
	 *
	 * @param cityList the cityList to set
	 */
	public void setCityList(List<PmCity> cityList) {
		this.cityList = cityList;
	}

	/**
	 * Gets the district list.
	 *
	 * @return the districtList
	 */
	public List<PmDistrict> getDistrictList() {
		return districtList;
	}

	/**
	 * Sets the district list.
	 *
	 * @param districtList the districtList to set
	 */
	public void setDistrictList(List<PmDistrict> districtList) {
		this.districtList = districtList;
	}

	/**
	 * Gets the division list.
	 *
	 * @return the divisionList
	 */
	public List<PmDivision> getDivisionList() {
		return divisionList;
	}

	/**
	 * Sets the division list.
	 *
	 * @param divisionList the divisionList to set
	 */
	public void setDivisionList(List<PmDivision> divisionList) {
		this.divisionList = divisionList;
	}

	/**
	 * Gets the charge line selected item address.
	 *
	 * @return the chargeLineSelectedItemAddress
	 */
	public FlFulfilmentItemAddr getChargeLineSelectedItemAddress() {
		return chargeLineSelectedItemAddress;
	}

	/**
	 * Sets the charge line selected item address.
	 *
	 * @param chargeLineSelectedItemAddress the chargeLineSelectedItemAddress to set
	 */
	public void setChargeLineSelectedItemAddress(
			FlFulfilmentItemAddr chargeLineSelectedItemAddress) {
		this.chargeLineSelectedItemAddress = chargeLineSelectedItemAddress;
	}
	
	/**
	 * Gets the selected item charge section.
	 *
	 * @return the selectedItemChargeSection
	 */
	public FlFulfilmentItemAddr getSelectedItemChargeSection() {
		return selectedItemChargeSection;
	}

	/**
	 * Sets the selected item charge section.
	 *
	 * @param selectedItemChargeSection the selectedItemChargeSection to set
	 */
	public void setSelectedItemChargeSection(
			FlFulfilmentItemAddr selectedItemChargeSection) {
		this.selectedItemChargeSection = selectedItemChargeSection;
	}

	/**
	 * Gets the selected nodes.
	 *
	 * @return the selectedNodes
	 */
	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	/**
	 * Sets the selected nodes.
	 *
	 * @param selectedNodes the selectedNodes to set
	 */
	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	/**
	 * Gets the selected node.
	 *
	 * @return the selectedNode
	 */
	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	/**
	 * Sets the selected node.
	 *
	 * @param selectedNode the selectedNode to set
	 */
	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	/**
	 * Gets the ordered amt grand total.
	 *
	 * @return the orderedAmtGrandTotal
	 */
	public BigDecimal getOrderedAmtGrandTotal() {
		return orderedAmtGrandTotal;
	}

	/**
	 * Sets the ordered amt grand total.
	 *
	 * @param orderedAmtGrandTotal the orderedAmtGrandTotal to set
	 */
	public void setOrderedAmtGrandTotal(BigDecimal orderedAmtGrandTotal) {
		this.orderedAmtGrandTotal = orderedAmtGrandTotal;
	}

	/**
	 * Gets the vot fund list.
	 *
	 * @return the votFundList
	 */
	public List<PmVotFund> getVotFundList() {
		return votFundList;
	}

	/**
	 * Sets the vot fund list.
	 *
	 * @param votFundList the votFundList to set
	 */
	public void setVotFundList(List<PmVotFund> votFundList) {
		this.votFundList = votFundList;
	}

	/**
	 * Gets the sub setia list.
	 *
	 * @return the subSetiaList
	 */
	public List<PmSubSetia> getSubSetiaList() {
		return subSetiaList;
	}

	/**
	 * Sets the sub setia list.
	 *
	 * @param subSetiaList the subSetiaList to set
	 */
	public void setSubSetiaList(List<PmSubSetia> subSetiaList) {
		this.subSetiaList = subSetiaList;
	}


	/**
	 * Gets the prg activity list.
	 *
	 * @return the prgActivityList
	 */
	public List<PmPrgActivity> getPrgActivityList() {
		return prgActivityList;
	}

	/**
	 * Sets the prg activity list.
	 *
	 * @param prgActivityList the prgActivityList to set
	 */
	public void setPrgActivityList(List<PmPrgActivity> prgActivityList) {
		this.prgActivityList = prgActivityList;
	}

	/**
	 * Gets the receiving off list.
	 *
	 * @return the receivingOffList
	 */
	public List<PmAddressType> getReceivingOffList() {
		return receivingOffList;
	}

	/**
	 * Sets the receiving off list.
	 *
	 * @param receivingOffList the receivingOffList to set
	 */
	public void setReceivingOffList(List<PmAddressType> receivingOffList) {
		this.receivingOffList = receivingOffList;
	}

	/**
	 * Gets the ag office id.
	 *
	 * @return the agOfficeId
	 */
	public Long getAgOfficeId() {
		return agOfficeId;
	}

	/**
	 * Sets the ag office id.
	 *
	 * @param agOfficeId the agOfficeId to set
	 */
	public void setAgOfficeId(Long agOfficeId) {
		this.agOfficeId = agOfficeId;
	}

	/**
	 * Gets the ag office name.
	 *
	 * @return the agOfficeName
	 */
	public String getAgOfficeName() {
		return agOfficeName;
	}

	/**
	 * Sets the ag office name.
	 *
	 * @param agOfficeName the agOfficeName to set
	 */
	public void setAgOfficeName(String agOfficeName) {
		this.agOfficeName = agOfficeName;
	}

	/**
	 * Sets the manual address list.
	 *
	 * @param manualAddressList the manualAddressList to set
	 */
	public void setManualAddressList(List<PmAddressType> manualAddressList) {
		this.manualAddressList = manualAddressList;
	}

	/**
	 * Gets the manual address list.
	 *
	 * @return the manualAddressList
	 */
	public List<PmAddressType> getManualAddressList() {
		return manualAddressList;
	}

	/**
	 * Gets the manual address map.
	 *
	 * @return the manualAddressMap
	 */
	public Map<Long, List<PmAddressType>> getManualAddressMap() {
		return manualAddressMap;
	}

	/**
	 * Sets the manual address map.
	 *
	 * @param manualAddressMap the manualAddressMap to set
	 */
	public void setManualAddressMap(Map<Long, List<PmAddressType>> manualAddressMap) {
		this.manualAddressMap = manualAddressMap;
	}

	/**
	 * Gets the term days.
	 *
	 * @return the termDays
	 */
	public String getTermDays() {
		return termDays;
	}

	/**
	 * Sets the term days.
	 *
	 * @param termDays the termDays to set
	 */
	public void setTermDays(String termDays) {
		this.termDays = termDays;
	}

	/**
	 * Gets the selected address rows list.
	 *
	 * @return the selectedAddressRowsList
	 */
	public List<FlDeliveryAddress> getSelectedAddressRowsList() {
		return selectedAddressRowsList;
	}

	/**
	 * Sets the selected address rows list.
	 *
	 * @param selectedAddressRowsList the selectedAddressRowsList to set
	 */
	public void setSelectedAddressRowsList(
			List<FlDeliveryAddress> selectedAddressRowsList) {
		this.selectedAddressRowsList = selectedAddressRowsList;
	}

	/**
	 * Checks if is delete button hide.
	 *
	 * @return the deleteButtonHide
	 */
	public boolean isDeleteButtonHide() {
		return deleteButtonHide;
	}

	/**
	 * Sets the delete button hide.
	 *
	 * @param deleteButtonHide the deleteButtonHide to set
	 */
	public void setDeleteButtonHide(boolean deleteButtonHide) {
		this.deleteButtonHide = deleteButtonHide;
	}

	/**
	 * Gets the manual addr list.
	 *
	 * @return the manualAddrList
	 */
	public List<SelectItem> getManualAddrList() {
		return manualAddrList;
	}

	/**
	 * Sets the manual addr list.
	 *
	 * @param manualAddrList the manualAddrList to set
	 */
	public void setManualAddrList(List<SelectItem> manualAddrList) {
		this.manualAddrList = manualAddrList;
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
	 * Gets the vot fund id.
	 *
	 * @return the votFundId
	 */
	public Long getVotFundId() {
		return votFundId;
	}

	/**
	 * Sets the vot fund id.
	 *
	 * @param votFundId the votFundId to set
	 */
	public void setVotFundId(Long votFundId) {
		this.votFundId = votFundId;
	}

	/**
	 * Gets the sub setia id.
	 *
	 * @return the subSetiaId
	 */
	public Long getSubSetiaId() {
		return subSetiaId;
	}

	/**
	 * Sets the sub setia id.
	 *
	 * @param subSetiaId the subSetiaId to set
	 */
	public void setSubSetiaId(Long subSetiaId) {
		this.subSetiaId = subSetiaId;
	}

	/**
	 * Gets the prg activity id.
	 *
	 * @return the prgActivityId
	 */
	public Long getPrgActivityId() {
		return prgActivityId;
	}

	/**
	 * Sets the prg activity id.
	 *
	 * @param prgActivityId the prgActivityId to set
	 */
	public void setPrgActivityId(Long prgActivityId) {
		this.prgActivityId = prgActivityId;
	}

	/**
	 * Gets the supplier branches list.
	 *
	 * @return the supplierBranchesList
	 */
	public List<SelectItem> getSupplierBranchesList() {
		return supplierBranchesList;
	}

	/**
	 * Sets the supplier branches list.
	 *
	 * @param supplierBranchesList the supplierBranchesList to set
	 */
	public void setSupplierBranchesList(List<SelectItem> supplierBranchesList) {
		this.supplierBranchesList = supplierBranchesList;
	}

	/**
	 * Gets the selected tab index.
	 *
	 * @return the selected tab index
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	/**
	 * Sets the selected tab index.
	 *
	 * @param selectedTabIndex the new selected tab index
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	/**
	 * Checks if is required.
	 *
	 * @return true, if is required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Sets the required.
	 *
	 * @param required the new required
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * Gets the gl account list.
	 *
	 * @return the gl account list
	 */
	public List<PmGlAccount> getGlAccountList() {
		if(glAccountList==null) {
			glAccountList = new ArrayList<PmGlAccount>();
		}
		return glAccountList;
	}

	/**
	 * Sets the gl account list.
	 *
	 * @param glAccountList the new gl account list
	 */
	public void setGlAccountList(List<PmGlAccount> glAccountList) {
		this.glAccountList = glAccountList;
	}

	/**
	 * Gets the asset ind id.
	 *
	 * @return the asset ind id
	 */
	public Long getAssetIndId() {
		return assetIndId;
	}

	/**
	 * Sets the asset ind id.
	 *
	 * @param assetIndId the new asset ind id
	 */
	public void setAssetIndId(Long assetIndId) {
		this.assetIndId = assetIndId;
	}

	/**
	 * Gets the gl account id.
	 *
	 * @return the gl account id
	 */
	public Long getGlAccountId() {
		return glAccountId;
	}

	/**
	 * Sets the gl account id.
	 *
	 * @param glAccountId the new gl account id
	 */
	public void setGlAccountId(Long glAccountId) {
		this.glAccountId = glAccountId;
	}

	/**
	 * Gets the financing type code.
	 *
	 * @return the financing type code
	 */
	public String getFinancingTypeCode() {
		return financingTypeCode;
	}

	/**
	 * Sets the financing type code.
	 *
	 * @param financingTypeCode the new financing type code
	 */
	public void setFinancingTypeCode(String financingTypeCode) {
		this.financingTypeCode = financingTypeCode;
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
	 * Gets the asset ind code.
	 *
	 * @return the asset ind code
	 */
	public String getAssetIndCode() {
		return assetIndCode;
	}

	/**
	 * Sets the asset ind code.
	 *
	 * @param assetIndCode the new asset ind code
	 */
	public void setAssetIndCode(String assetIndCode) {
		this.assetIndCode = assetIndCode;
	}

	/**
	 * Gets the gl account code.
	 *
	 * @return the gl account code
	 */
	public String getGlAccountCode() {
		return glAccountCode;
	}

	/**
	 * Sets the gl account code.
	 *
	 * @param glAccountCode the new gl account code
	 */
	public void setGlAccountCode(String glAccountCode) {
		this.glAccountCode = glAccountCode;
	}

	/**
	 * Gets the user group list.
	 *
	 * @return the user group list
	 */
	public List<PmUserGroup> getUserGroupList() {
		if(userGroupList==null) {
			userGroupList = new ArrayList<PmUserGroup>();
		}
		return userGroupList;
	}

	/**
	 * Sets the user group list.
	 *
	 * @param userGroupList the new user group list
	 */
	public void setUserGroupList(List<PmUserGroup> userGroupList) {
		this.userGroupList = userGroupList;
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
	 * Gets the max date.
	 *
	 * @return the maxDate
	 */
	public Date getMaxDate() {
		return maxDate;
	}

	/**
	 * Sets the max date.
	 *
	 * @param maxDate the maxDate to set
	 */
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	/**
	 * Gets the current ptj group id.
	 *
	 * @return the currentPtjGroupId
	 */
	public Long getCurrentPtjGroupId() {
		return currentPtjGroupId;
	}

	/**
	 * Sets the current ptj group id.
	 *
	 * @param currentPtjGroupId the currentPtjGroupId to set
	 */
	public void setCurrentPtjGroupId(Long currentPtjGroupId) {
		this.currentPtjGroupId = currentPtjGroupId;
	}

	/**
	 * Gets the current prepared org id.
	 *
	 * @return the currentPreparedOrgId
	 */
	public Long getCurrentPreparedOrgId() {
		return currentPreparedOrgId;
	}

	/**
	 * Sets the current prepared org id.
	 *
	 * @param currentPreparedOrgId the currentPreparedOrgId to set
	 */
	public void setCurrentPreparedOrgId(Long currentPreparedOrgId) {
		this.currentPreparedOrgId = currentPreparedOrgId;
	}


	/**
	 * Gets the sc request supplier item id map.
	 *
	 * @return the scRequestSupplierItemIdMap
	 */
	public HashMap<Long, Long> getScRequestSupplierItemIdMap() {
		if(scRequestSupplierItemIdMap == null){
			scRequestSupplierItemIdMap = new HashMap<Long,Long>();
		}
		return scRequestSupplierItemIdMap;
	}

	/**
	 * Sets the sc request supplier item id map.
	 *
	 * @param scRequestSupplierItemIdMap the scRequestSupplierItemIdMap to set
	 */
	public void setScRequestSupplierItemIdMap(
			HashMap<Long, Long> scRequestSupplierItemIdMap) {
		this.scRequestSupplierItemIdMap = scRequestSupplierItemIdMap;
	}

	/**
	 * Gets the zonal tier map.
	 *
	 * @return the zonalTierMap
	 */
	public HashMap<String, List<FlFulfilmentItemAddr>> getZonalTierMap() {
		if(zonalTierMap == null){
			zonalTierMap = new HashMap<String,List<FlFulfilmentItemAddr>>();
		}
		return zonalTierMap;
	}

	/**
	 * Sets the zonal tier map.
	 *
	 * @param zonalTierMap the zonalTierMap to set
	 */
	public void setZonalTierMap(HashMap<String, List<FlFulfilmentItemAddr>> zonalTierMap) {
		this.zonalTierMap = zonalTierMap;
	}

	/**
	 * Gets the zonal tier item list.
	 *
	 * @return the zonalTierItemList
	 */
	public List<FlFulfilmentItemAddr> getZonalTierItemList() {
		if(zonalTierItemList == null){
			zonalTierItemList = new ArrayList<FlFulfilmentItemAddr>();
		}
		return zonalTierItemList;
	}

	/**
	 * Sets the zonal tier item list.
	 *
	 * @param zonalTierItemList the zonalTierItemList to set
	 */
	public void setZonalTierItemList(List<FlFulfilmentItemAddr> zonalTierItemList) {
		this.zonalTierItemList = zonalTierItemList;
	}

	/**
	 * Gets the zonal tier item.
	 *
	 * @return the zonalTierItem
	 */
	public FlFulfilmentItemAddr getZonalTierItem() {
		return zonalTierItem;
	}

	/**
	 * Sets the zonal tier item.
	 *
	 * @param zonalTierItem the zonalTierItem to set
	 */
	public void setZonalTierItem(FlFulfilmentItemAddr zonalTierItem) {
		this.zonalTierItem = zonalTierItem;
	}

	/**
	 * Gets the tier map.
	 *
	 * @return the tierMap
	 */
	public HashMap<Long, List<FlFulfilmentItemAddr>> getTierMap() {
		if(tierMap == null){
			tierMap = new HashMap<Long, List<FlFulfilmentItemAddr>>();
		}
		return tierMap;
	}

	/**
	 * Sets the tier map.
	 *
	 * @param tierMap the tierMap to set
	 */
	public void setTierMap(HashMap<Long, List<FlFulfilmentItemAddr>> tierMap) {
		this.tierMap = tierMap;
	}

	/**
	 * Gets the req item info list.
	 *
	 * @return the reqItemInfoList
	 */
	public List<ScSupplierItemPrice> getReqItemInfoList() {
		return reqItemInfoList;
	}

	/**
	 * Sets the req item info list.
	 *
	 * @param reqItemInfoList the reqItemInfoList to set
	 */
	public void setReqItemInfoList(List<ScSupplierItemPrice> reqItemInfoList) {
		this.reqItemInfoList = reqItemInfoList;
	}

	/**
	 * Gets the item name.
	 *
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * Gets the quantity.
	 *
	 * @return the quantity
	 */
	public Double getQuantity() {
		return quantity;
	}

	/**
	 * Sets the item name.
	 *
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * Sets the quantity.
	 *
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	/**
	 * Gets the price type.
	 *
	 * @return the priceType
	 */
	public PmParameter getPriceType() {
		return priceType;
	}

	/**
	 * Sets the price type.
	 *
	 * @param priceType the priceType to set
	 */
	public void setPriceType(PmParameter priceType) {
		this.priceType = priceType;
	}

	/**
	 * Checks if is standard price.
	 *
	 * @return the standardPrice
	 */
	public boolean isStandardPrice() {
		return standardPrice;
	}

	/**
	 * Checks if is zonal price.
	 *
	 * @return the zonalPrice
	 */
	public boolean isZonalPrice() {
		return zonalPrice;
	}

	/**
	 * Checks if is zonal tier price.
	 *
	 * @return the zonalTierPrice
	 */
	public boolean isZonalTierPrice() {
		return zonalTierPrice;
	}

	/**
	 * Sets the standard price.
	 *
	 * @param standardPrice the standardPrice to set
	 */
	public void setStandardPrice(boolean standardPrice) {
		this.standardPrice = standardPrice;
	}

	/**
	 * Sets the zonal price.
	 *
	 * @param zonalPrice the zonalPrice to set
	 */
	public void setZonalPrice(boolean zonalPrice) {
		this.zonalPrice = zonalPrice;
	}

	/**
	 * Sets the zonal tier price.
	 *
	 * @param zonalTierPrice the zonalTierPrice to set
	 */
	public void setZonalTierPrice(boolean zonalTierPrice) {
		this.zonalTierPrice = zonalTierPrice;
	}

	/**
	 * Gets the standard unit price.
	 *
	 * @return the standardUnitPrice
	 */
	public Double getStandardUnitPrice() {
		return standardUnitPrice;
	}

	/**
	 * Sets the standard unit price.
	 *
	 * @param standardUnitPrice the standardUnitPrice to set
	 */
	public void setStandardUnitPrice(Double standardUnitPrice) {
		this.standardUnitPrice = standardUnitPrice;
	}
	
	/**
	 * Gets the request note item list.
	 *
	 * @return the requestNoteItemList
	 */
	public List<FlFulfilmentItemAddr> getRequestNoteItemList() {
		return requestNoteItemList;
	}

	/**
	 * Sets the request note item list.
	 *
	 * @param requestNoteItemList the requestNoteItemList to set
	 */
	public void setRequestNoteItemList(
			List<FlFulfilmentItemAddr> requestNoteItemList) {
		this.requestNoteItemList = requestNoteItemList;
	}



	/**
	 * Checks if is email validate.
	 *
	 * @return the emailValidate
	 */
	public boolean isEmailValidate() {
		return emailValidate;
	}

	/**
	 * Sets the email validate.
	 *
	 * @param emailValidate the emailValidate to set
	 */
	public void setEmailValidate(boolean emailValidate) {
		this.emailValidate = emailValidate;
	}

	/**
	 * Checks if is charge line panel.
	 *
	 * @return the chargeLinePanel
	 */
	public boolean isChargeLinePanel() {
		return chargeLinePanel;
	}

	/**
	 * Sets the charge line panel.
	 *
	 * @param chargeLinePanel the chargeLinePanel to set
	 */
	public void setChargeLinePanel(boolean chargeLinePanel) {
		this.chargeLinePanel = chargeLinePanel;
	}

	/**
	 * Gets the pm vote fund.
	 *
	 * @return the pmVoteFund
	 */
	public PmVotFund getPmVoteFund() {
		return pmVoteFund;
	}

	/**
	 * Sets the pm vote fund.
	 *
	 * @param pmVoteFund the pmVoteFund to set
	 */
	public void setPmVoteFund(PmVotFund pmVoteFund) {
		this.pmVoteFund = pmVoteFund;
	}

	/**
	 * Gets the gosodo.
	 *
	 * @return the gosodo
	 */
	public PmGlAccount getGosodo() {
		return gosodo;
	}

	/**
	 * Sets the gosodo.
	 *
	 * @param gosodo the gosodo to set
	 */
	public void setGosodo(PmGlAccount gosodo) {
		this.gosodo = gosodo;
	}

	/**
	 * Gets the pm prg activity.
	 *
	 * @return the pmPrgActivity
	 */
	public PmPrgActivity getPmPrgActivity() {
		return pmPrgActivity;
	}

	/**
	 * Sets the pm prg activity.
	 *
	 * @param pmPrgActivity the pmPrgActivity to set
	 */
	public void setPmPrgActivity(PmPrgActivity pmPrgActivity) {
		this.pmPrgActivity = pmPrgActivity;
	}

	/**
	 * Gets the pm sub setia.
	 *
	 * @return the pmSubSetia
	 */
	public PmSubSetia getPmSubSetia() {
		return pmSubSetia;
	}

	/**
	 * Sets the pm sub setia.
	 *
	 * @param pmSubSetia the pmSubSetia to set
	 */
	public void setPmSubSetia(PmSubSetia pmSubSetia) {
		this.pmSubSetia = pmSubSetia;
	}

	/**
	 * Checks if is program lov.
	 *
	 * @return the programLov
	 */
	public boolean isProgramLov() {
		return programLov;
	}

	/**
	 * Checks if is project lov.
	 *
	 * @return the projectLov
	 */
	public boolean isProjectLov() {
		return projectLov;
	}

	/**
	 * Sets the program lov.
	 *
	 * @param programLov the programLov to set
	 */
	public void setProgramLov(boolean programLov) {
		this.programLov = programLov;
	}

	/**
	 * Sets the project lov.
	 *
	 * @param projectLov the projectLov to set
	 */
	public void setProjectLov(boolean projectLov) {
		this.projectLov = projectLov;
	}

	/**
	 * Checks if is vot fund display.
	 *
	 * @return the votFundDisplay
	 */
	public boolean isVotFundDisplay() {
		return votFundDisplay;
	}

	/**
	 * Sets the vot fund display.
	 *
	 * @param votFundDisplay the votFundDisplay to set
	 */
	public void setVotFundDisplay(boolean votFundDisplay) {
		this.votFundDisplay = votFundDisplay;
	}

	/**
	 * Gets the item addr vote fund disp.
	 *
	 * @return the itemAddrVoteFundDisp
	 */
	public FlFulfilmentItemAddr getItemAddrVoteFundDisp() {
		return itemAddrVoteFundDisp;
	}

	/**
	 * Sets the item addr vote fund disp.
	 *
	 * @param itemAddrVoteFundDisp the itemAddrVoteFundDisp to set
	 */
	public void setItemAddrVoteFundDisp(FlFulfilmentItemAddr itemAddrVoteFundDisp) {
		this.itemAddrVoteFundDisp = itemAddrVoteFundDisp;
	}

	/**
	 * Gets the root zone tree.
	 *
	 * @return the rootZoneTree
	 */
	public TreeNode getRootZoneTree() {
		return rootZoneTree;
	}

	/**
	 * Gets the selected tree node.
	 *
	 * @return the selectedTreeNode
	 */
	public TreeNode getSelectedTreeNode() {
		return selectedTreeNode;
	}

	/**
	 * Gets the selected zone tree.
	 *
	 * @return the selectedZoneTree
	 */
	public List<TreeNode> getSelectedZoneTree() {
		return selectedZoneTree;
	}

	/**
	 * Sets the root zone tree.
	 *
	 * @param rootZoneTree the rootZoneTree to set
	 */
	public void setRootZoneTree(TreeNode rootZoneTree) {
		this.rootZoneTree = rootZoneTree;
	}

	/**
	 * Sets the selected tree node.
	 *
	 * @param selectedTreeNode the selectedTreeNode to set
	 */
	public void setSelectedTreeNode(TreeNode selectedTreeNode) {
		this.selectedTreeNode = selectedTreeNode;
	}


	/**
	 * Sets the selected zone tree.
	 *
	 * @param selectedZoneTree the selectedZoneTree to set
	 */
	public void setSelectedZoneTree(List<TreeNode> selectedZoneTree) {
		this.selectedZoneTree = selectedZoneTree;
	}

	/**
	 * Gets the supplier item price list.
	 *
	 * @return the supplierItemPriceList
	 */
	public List<FLSupplierItemVO> getSupplierItemPriceList() {
		return supplierItemPriceList;
	}

	/**
	 * Sets the supplier item price list.
	 *
	 * @param supplierItemPriceList the supplierItemPriceList to set
	 */
	public void setSupplierItemPriceList(
			List<FLSupplierItemVO> supplierItemPriceList) {
		this.supplierItemPriceList = supplierItemPriceList;
	}

	/**
	 * Checks if is query display.
	 *
	 * @return the queryDisplay
	 */
	public boolean isQueryDisplay() {
		return queryDisplay;
	}

	/**
	 * Sets the query display.
	 *
	 * @param queryDisplay the queryDisplay to set
	 */
	public void setQueryDisplay(boolean queryDisplay) {
		this.queryDisplay = queryDisplay;
	}

	/**
	 * Gets the fee vote fund.
	 *
	 * @return the fee vote fund
	 */
	public PmVotFund getFeeVoteFund() {
		return feeVoteFund;
	}

	/**
	 * Sets the fee vote fund.
	 *
	 * @param feeVoteFund the new fee vote fund
	 */
	public void setFeeVoteFund(PmVotFund feeVoteFund) {
		this.feeVoteFund = feeVoteFund;
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
	 * Gets the measurement codes.
	 *
	 * @return the measurementCodes
	 */
	public String getMeasurementCodes() {
		return measurementCodes;
	}

	/**
	 * Sets the measurement codes.
	 *
	 * @param measurementCodes the measurementCodes to set
	 */
	public void setMeasurementCodes(String measurementCodes) {
		this.measurementCodes = measurementCodes;
	}

	/**
	 * Gets the measurement names.
	 *
	 * @return the measurementNames
	 */
	public String getMeasurementNames() {
		return measurementNames;
	}

	/**
	 * Sets the measurement names.
	 *
	 * @param measurementNames the measurementNames to set
	 */
	public void setMeasurementNames(String measurementNames) {
		this.measurementNames = measurementNames;
	}

	/**
	 * Checks if is fl doc maintain.
	 *
	 * @return the flDocMaintain
	 */
	public boolean isFlDocMaintain() {
		return flDocMaintain;
	}

	/**
	 * Sets the fl doc maintain.
	 *
	 * @param flDocMaintain the flDocMaintain to set
	 */
	public void setFlDocMaintain(boolean flDocMaintain) {
		this.flDocMaintain = flDocMaintain;
	}

	/**
	 * Checks if is purchase request link.
	 *
	 * @return the isPurchaseRequestLink
	 */
	public boolean isPurchaseRequestLink() {
		return isPurchaseRequestLink;
	}

	/**
	 * Sets the purchase request link.
	 *
	 * @param isPurchaseRequestLink the isPurchaseRequestLink to set
	 */
	public void setPurchaseRequestLink(boolean isPurchaseRequestLink) {
		this.isPurchaseRequestLink = isPurchaseRequestLink;
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
	 * @return the gstRegistrationDate
	 */
	public Date getGstRegistrationDate() {
		return gstRegistrationDate;
	}

	/**
	 * @param gstRegistrationDate the gstRegistrationDate to set
	 */
	public void setGstRegistrationDate(Date gstRegistrationDate) {
		this.gstRegistrationDate = gstRegistrationDate;
	}

	/**
	 * @return the gstRegistrationNo
	 */
	public String getGstRegistrationNo() {
		return gstRegistrationNo;
	}

	/**
	 * @param gstRegistrationNo the gstRegistrationNo to set
	 */
	public void setGstRegistrationNo(String gstRegistrationNo) {
		this.gstRegistrationNo = gstRegistrationNo;
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
	 * @return the currentChargePtjGroup
	 */
	public PmOrgProfile getCurrentChargePtjGroup() {
		return currentChargePtjGroup;
	}

	/**
	 * @param currentChargePtjGroup the currentChargePtjGroup to set
	 */
	public void setCurrentChargePtjGroup(PmOrgProfile currentChargePtjGroup) {
		this.currentChargePtjGroup = currentChargePtjGroup;
	}

	/**
	 * @return the selectChargeLineRow
	 */
	public boolean isSelectChargeLineRow() {
		return selectChargeLineRow;
	}

	/**
	 * @param selectChargeLineRow the selectChargeLineRow to set
	 */
	public void setSelectChargeLineRow(boolean selectChargeLineRow) {
		this.selectChargeLineRow = selectChargeLineRow;
	}

	/**
	 * @return the declarationCheck
	 */
	public boolean isDeclarationCheck() {
		return declarationCheck;
	}

	/**
	 * @param declarationCheck the declarationCheck to set
	 */
	public void setDeclarationCheck(boolean declarationCheck) {
		this.declarationCheck = declarationCheck;
	}

	public boolean isGstRateChange() {
		return gstRateChange;
	}

	public void setGstRateChange(boolean gstRateChange) {
		this.gstRateChange = gstRateChange;
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
	 * @return the selectedApprover
	 */
	public Long getSelectedApprover() {
		return selectedApprover;
	}

	/**
	 * @param selectedApprover the selectedApprover to set
	 */
	public void setSelectedApprover(Long selectedApprover) {
		this.selectedApprover = selectedApprover;
	}

	public boolean isPidValidityExpired() {
		return pidValidityExpired;
	}

	public void setPidValidityExpired(boolean pidValidityExpired) {
		this.pidValidityExpired = pidValidityExpired;
	}

	/**
	 * @return the taskDeadLine
	 */
	public String getTaskDeadLine() {
		return taskDeadLine;
	}

	/**
	 * @param taskDeadLine the taskDeadLine to set
	 */
	public void setTaskDeadLine(String taskDeadLine) {
		this.taskDeadLine = taskDeadLine;
	}

	/**
	 * @return the approverIdList
	 */
	public List<Long> getApproverIdList() {
		if(approverIdList == null){
			approverIdList = new ArrayList<Long>();
		}
		return approverIdList;
	}

	/**
	 * @param approverIdList the approverIdList to set
	 */
	public void setApproverIdList(List<Long> approverIdList) {
		this.approverIdList = approverIdList;
	}

	/**
	 * @return the approverGroupUserList
	 */
	public List<PmUserGroupUser> getApproverGroupUserList() {
		if(approverGroupUserList == null){
			approverGroupUserList = new ArrayList<PmUserGroupUser>();
		}
		return approverGroupUserList;
	}

	/**
	 * @param approverGroupUserList the approverGroupUserList to set
	 */
	public void setApproverGroupUserList(List<PmUserGroupUser> approverGroupUserList) {
		this.approverGroupUserList = approverGroupUserList;
	}

	/**
	 * @return the ministryProfileId
	 */
	public Long getMinistryProfileId() {
		return ministryProfileId;
	}

	/**
	 * @param ministryProfileId the ministryProfileId to set
	 */
	public void setMinistryProfileId(Long ministryProfileId) {
		this.ministryProfileId = ministryProfileId;
	}

	/**
	 * @return the jabatanProfileId
	 */
	public Long getJabatanProfileId() {
		return jabatanProfileId;
	}

	/**
	 * @param jabatanProfileId the jabatanProfileId to set
	 */
	public void setJabatanProfileId(Long jabatanProfileId) {
		this.jabatanProfileId = jabatanProfileId;
	}

	/**
	 * @return the ptjCodeName
	 */
	public String getPtjCodeName() {
		return ptjCodeName;
	}

	/**
	 * @param ptjCodeName the ptjCodeName to set
	 */
	public void setPtjCodeName(String ptjCodeName) {
		this.ptjCodeName = ptjCodeName;
	}

	/**
	 * @return the ptjCodeNameSearchValues
	 */
	public String getPtjCodeNameSearchValues() {
		return ptjCodeNameSearchValues;
	}

	/**
	 * @param ptjCodeNameSearchValues the ptjCodeNameSearchValues to set
	 */
	public void setPtjCodeNameSearchValues(String ptjCodeNameSearchValues) {
		this.ptjCodeNameSearchValues = ptjCodeNameSearchValues;
	}

	public FlDeliveryAddress getFlManualAddress() {
		return flManualAddress;
	}

	public void setFlManualAddress(FlDeliveryAddress flManualAddress) {
		this.flManualAddress = flManualAddress;
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

	public List<Long> getBpmApproverList() {
		if(bpmApproverList == null){
			bpmApproverList = new ArrayList<Long>();
		}
		return bpmApproverList;
	}

	public void setBpmApproverList(List<Long> bpmApproverList) {
		this.bpmApproverList = bpmApproverList;
	}

	/**
	 * @return the orderSummaryOrderedAmtGrandTotal
	 */
	public BigDecimal getOrderSummaryOrderedAmtGrandTotal() {
		return orderSummaryOrderedAmtGrandTotal;
	}

	/**
	 * @param orderSummaryOrderedAmtGrandTotal the orderSummaryOrderedAmtGrandTotal to set
	 */
	public void setOrderSummaryOrderedAmtGrandTotal(
			BigDecimal orderSummaryOrderedAmtGrandTotal) {
		this.orderSummaryOrderedAmtGrandTotal = orderSummaryOrderedAmtGrandTotal;
	}

	/**
	 * @return the divisionDisplay
	 */
	public boolean isDivisionDisplay() {
		return divisionDisplay;
	}

	/**
	 * @param divisionDisplay the divisionDisplay to set
	 */
	public void setDivisionDisplay(boolean divisionDisplay) {
		this.divisionDisplay = divisionDisplay;
	}

	public List<VAddressTypeDeliveryAddress> getSelectedDeliveryFullAddr() {
		return selectedDeliveryFullAddr;
	}

	public void setSelectedDeliveryFullAddr(List<VAddressTypeDeliveryAddress> selectedDeliveryFullAddr) {
		this.selectedDeliveryFullAddr = selectedDeliveryFullAddr;
	}

	public String getGfmasErrorCode() {
		return gfmasErrorCode;
	}

	public void setGfmasErrorCode(String gfmasErrorCode) {
		this.gfmasErrorCode = gfmasErrorCode;
	}

	public String getGfmasErrorDesc() {
		return gfmasErrorDesc;
	}

	public void setGfmasErrorDesc(String gfmasErrorDesc) {
		this.gfmasErrorDesc = gfmasErrorDesc;
	}

	/**
	 * @return the itemDesc
	 */
	public String getItemDesc() {
		return itemDesc;
	}

	/**
	 * @param itemDesc the itemDesc to set
	 */
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

}
