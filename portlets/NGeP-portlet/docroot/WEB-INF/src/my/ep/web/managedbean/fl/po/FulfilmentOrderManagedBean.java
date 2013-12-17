package my.ep.web.managedbean.fl.po;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.bpm.vo.WorklistItemVO;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;

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
	private List<FlDeliveryAddress> selectedDelAddrList;
	private List<FlFulfilmentOrder> pendingDoList;
	private List<FlDeliveryAddress>deliveryAddressList;
	private List<FlDeliveryOrder>deliveryOrderStatusList;
	
	private List<FlFulfilmentItem> flItemList;
	private DualListModel<String> approverModel;
	private String selectedLovDelAddr;
	
	private String factoringCheck="false";
	private String docType;
	

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

}
