package my.ep.web.managedbean.fl.po;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.bpm.vo.WorklistItemVO;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.order.FlStopInstr;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmFinancialBranch;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "flOrderManagedBean")
@SessionScoped
public class FulfilmentOrderManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<WorklistItemVO> taskList;
	private Map bpmMap = new HashMap();
	private String processName;
	private String status;
	private String taskName;
	private Long flOrderId;
	private Long stopRequestId;
	
	private boolean contractRequest;
	private String buttonVal;
	
	private FlRemark flRemark = new FlRemark();
	private FlAttachment flAttachment = new FlAttachment();
	private FlFulfilmentRequest flRequest;
	private FlDeliveryOrder flDeliveryOrder;
	private FlDeliveryAddress deliveryAddress;
	private FlFulfilmentOrder fulfilmentOrder;
	private FlDeliveryAddress selectedDelAddr;
	private FlDeliveryAddress selectedAddrRow;
	private FlFulfilmentOrder selectedFlOrder;
	private FlStopInstr stopIns;
	
	private List<FlDeliveryAddress> selectedDelAddrList;
	private List<FlFulfilmentOrder> pendingDoList;
	private List<FlDeliveryAddress>deliveryAddressList;
	private List<FlDeliveryOrder>deliveryOrderStatusList;
	private List<PmFinancialOrg> pmFinancialOrgs;
	private List<PmFinancialBranch> factoringBranchList;
	private List<PmReasonDesc> reasonDescs;
	
	private List<FlFulfilmentItem> flItemList;
	private DualListModel<String> approverModel;
	private String selectedLovDelAddr;
	
	private String factoringCheck="false";
	private String docType;
	
	//getSuplier address details
	private String supplierAddress;
	private String phoneNo;
	private String faxNo;
	private String contactEmail;
	private String contactPerson;
	private String contactPhoneNo;
	
	//for termdays
	
	private String termDays;
	private boolean removeFactoring;
	private int selectedTabIndex;
	private PmUser pmUser;
	private String flApprover;
	private TreeNode chargeLineItemRoot ;
	private FlFulfilmentItemAddr stopInstr;
	
	private boolean docMaintainRemarks = false;
	
	
	public void populateChargeLineData(){
		this.chargeLineItemRoot = new DefaultTreeNode("root", null);
		
		Map<Long, List<FlFulfilmentItemAddr>> siItemsMap = new HashMap<Long, List<FlFulfilmentItemAddr>>();
		List<FlFulfilmentItemAddr> flStopInstrItemList = null;
		for (FlDeliveryAddress flDelAddress :this.getFlRequest().getFlDeliveryAddresses()) {
			for (FlFulfilmentItemAddr flStopInstrItem : flDelAddress.getFlFulfilmentItemAddr()) {
				if (siItemsMap.containsKey(flStopInstrItem.getFlFulfilmentItem().getCmItem().getItemId())) {
					flStopInstrItemList = siItemsMap.get(flStopInstrItem.getFlFulfilmentItem().getCmItem().getItemId());
				} else {
					flStopInstrItemList = new ArrayList<FlFulfilmentItemAddr>();
				}
				flStopInstrItemList.add(flStopInstrItem);
				siItemsMap.put(flStopInstrItem.getFlFulfilmentItem().getCmItem().getItemId(), flStopInstrItemList);
			}
			
		}
		for(Entry<Long, List<FlFulfilmentItemAddr>> itemEntry : siItemsMap.entrySet()){
			List<FlFulfilmentItemAddr> stopInstrItems = itemEntry.getValue();
			FlFulfilmentItemAddr flsiItem = (FlFulfilmentItemAddr) stopInstrItems.get(0).cloneEntity(); // parent entity, no need stored in DB.
			flsiItem.setParent(true);
			TreeNode childItemNode = new DefaultTreeNode(flsiItem.getFlFulfilmentItem().getCmItem().getItemId().toString(), flsiItem, chargeLineItemRoot);
			
			BigDecimal totalOrderAmt = new BigDecimal(0);
			BigDecimal totalReceiveAmt = new BigDecimal(0);
			
			for(FlFulfilmentItemAddr chargeLineItem : stopInstrItems) {
				new DefaultTreeNode(chargeLineItem.getFlFulfilmentItem().getCmItem().getItemId().toString(),chargeLineItem,childItemNode);
				totalOrderAmt=totalOrderAmt.add(chargeLineItem.getOrderedAmt());
				totalReceiveAmt=totalReceiveAmt.add(chargeLineItem.getReceivedAmt());
			}			
			flsiItem.setOrderedAmt(totalOrderAmt);
			flsiItem.setReceivedAmt(totalReceiveAmt);
		}
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Map getBpmMap() {
		return bpmMap;
	}

	public void setBpmMap(Map bpmMap) {
		this.bpmMap = bpmMap;
	}



	public List<WorklistItemVO> getTaskList() {
		if(taskList==null)
			taskList = new ArrayList<WorklistItemVO>();
		return taskList;
	}

	public void setTaskList(List<WorklistItemVO> taskList) {
		this.taskList = taskList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the flRemark
	 */
	public FlRemark getFlRemark() {
		return flRemark;
	}

	/**
	 * @param flRemark the flRemark to set
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
	 * @param flAttachment the flAttachment to set
	 */
	public void setFlAttachment(FlAttachment flAttachment) {
		this.flAttachment = flAttachment;
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
	 * @return the pendingDoList
	 */
	public List<FlFulfilmentOrder> getPendingDoList() {
		return pendingDoList;
	}

	/**
	 * @param pendingDoList the pendingDoList to set
	 */
	public void setPendingDoList(List<FlFulfilmentOrder> pendingDoList) {
		this.pendingDoList = pendingDoList;
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
	 * @return the deliveryOrderStatusList
	 */
	public List<FlDeliveryOrder> getDeliveryOrderStatusList() {
		return deliveryOrderStatusList;
	}

	/**
	 * @param deliveryOrderStatusList the deliveryOrderStatusList to set
	 */
	public void setDeliveryOrderStatusList(
			List<FlDeliveryOrder> deliveryOrderStatusList) {
		this.deliveryOrderStatusList = deliveryOrderStatusList;
	}

	/**
	 * @return the contractRequest
	 */
	public boolean isContractRequest() {
		return contractRequest;
	}

	/**
	 * @param contractRequest the contractRequest to set
	 */
	public void setContractRequest(boolean contractRequest) {
		this.contractRequest = contractRequest;
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
	 * @return the flItemList
	 */
	public List<FlFulfilmentItem> getFlItemList() {
		return flItemList;
	}

	/**
	 * @param flItemList the flItemList to set
	 */
	public void setFlItemList(List<FlFulfilmentItem> flItemList) {
		this.flItemList = flItemList;
	}

	/**
	 * @return the flRequest
	 */
	public FlFulfilmentRequest getFlRequest() {
		return flRequest;
	}

	/**
	 * @param flRequest the flRequest to set
	 */
	public void setFlRequest(FlFulfilmentRequest flRequest) {
		this.flRequest = flRequest;
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
	 * @return the selectedFlOrder
	 */
	public FlFulfilmentOrder getSelectedFlOrder() {
		return selectedFlOrder;
	}

	/**
	 * @param selectedFlOrder the selectedFlOrder to set
	 */
	public void setSelectedFlOrder(FlFulfilmentOrder selectedFlOrder) {
		this.selectedFlOrder = selectedFlOrder;
	}

	/**
	 * @return the selectedDelAddrList
	 */
	public List<FlDeliveryAddress> getSelectedDelAddrList() {
		return selectedDelAddrList;
	}

	/**
	 * @param selectedDelAddrList the selectedDelAddrList to set
	 */
	public void setSelectedDelAddrList(List<FlDeliveryAddress> selectedDelAddrList) {
		this.selectedDelAddrList = selectedDelAddrList;
	}

	/**
	 * @return the flOrderId
	 */
	public Long getFlOrderId() {
		return flOrderId;
	}

	/**
	 * @param flOrderId the flOrderId to set
	 */
	public void setFlOrderId(Long flOrderId) {
		this.flOrderId = flOrderId;
	}

	/**
	 * @return the stopRequestId
	 */
	public Long getStopRequestId() {
		return stopRequestId;
	}

	/**
	 * @param stopRequestId the stopRequestId to set
	 */
	public void setStopRequestId(Long stopRequestId) {
		this.stopRequestId = stopRequestId;
	}

	/**
	 * @return the factoringCheck
	 */
	public String getFactoringCheck() {
		return factoringCheck;
	}

	/**
	 * @param factoringCheck the factoringCheck to set
	 */
	public void setFactoringCheck(String factoringCheck) {
		this.factoringCheck = factoringCheck;
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
	 * @return the pmFinancialOrgs
	 */
	public List<PmFinancialOrg> getPmFinancialOrgs() {
		return pmFinancialOrgs;
	}

	/**
	 * @param pmFinancialOrgs the pmFinancialOrgs to set
	 */
	public void setPmFinancialOrgs(List<PmFinancialOrg> pmFinancialOrgs) {
		this.pmFinancialOrgs = pmFinancialOrgs;
	}

	/**
	 * @return the factoringBranchList
	 */
	public List<PmFinancialBranch> getFactoringBranchList() {
		return factoringBranchList;
	}

	/**
	 * @param factoringBranchList the factoringBranchList to set
	 */
	public void setFactoringBranchList(List<PmFinancialBranch> factoringBranchList) {
		this.factoringBranchList = factoringBranchList;
	}

	/**
	 * @return the removeFactoring
	 */
	public boolean isRemoveFactoring() {
		return removeFactoring;
	}

	/**
	 * @param removeFactoring the removeFactoring to set
	 */
	public void setRemoveFactoring(boolean removeFactoring) {
		this.removeFactoring = removeFactoring;
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
	 * @return the stopIns
	 */
	public FlStopInstr getStopIns() {
		return stopIns;
	}

	/**
	 * @param stopIns the stopIns to set
	 */
	public void setStopIns(FlStopInstr stopIns) {
		this.stopIns = stopIns;
	}

	/**
	 * @return the reasonDescs
	 */
	public List<PmReasonDesc> getReasonDescs() {
		return reasonDescs;
	}

	/**
	 * @param reasonDescs the reasonDescs to set
	 */
	public void setReasonDescs(List<PmReasonDesc> reasonDescs) {
		this.reasonDescs = reasonDescs;
	}

	/**
	 * @return the flApprover
	 */
	public String getFlApprover() {
		return flApprover;
	}

	/**
	 * @param flApprover the flApprover to set
	 */
	public void setFlApprover(String flApprover) {
		this.flApprover = flApprover;
	}



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
	 * @return the stopInstr
	 */
	public FlFulfilmentItemAddr getStopInstr() {
		return stopInstr;
	}



	/**
	 * @param stopInstr the stopInstr to set
	 */
	public void setStopInstr(FlFulfilmentItemAddr stopInstr) {
		this.stopInstr = stopInstr;
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

}
