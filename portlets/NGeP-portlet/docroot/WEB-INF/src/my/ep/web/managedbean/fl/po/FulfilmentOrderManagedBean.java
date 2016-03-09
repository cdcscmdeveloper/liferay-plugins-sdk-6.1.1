package my.ep.web.managedbean.fl.po;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
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
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

// TODO: Auto-generated Javadoc
/**
 * Managed Bean for Fulfillment Order.
 *
 * @author Giribabu
 * @since 2/11/2013
 */
@ManagedBean(name = "flOrderManagedBean")
@SessionScoped
public class FulfilmentOrderManagedBean extends BaseManagedBean {
	
	private static Logger log = Logger.getLogger(FulfilmentOrderManagedBean.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The task list. */
	private List<WorklistItemVO> taskList;
	
	/** The process name. */
	private String processName;
	
	/** The status. */
	private String status;
	
	/** The task name. */
	private String taskName;
	
	/** The fl order id. */
	private Long flOrderId;
	
	/** The stop request id. */
	private Long stopRequestId;
	
	/** The contract request. */
	private boolean contractRequest;
	
	/** The button val. */
	private String buttonVal;
	
	/** The back to previous. */
	private String backToPrevious;
	
	/** The fl remark. */
	private FlRemark flRemark = new FlRemark();
	
	/** The fl attachment. */
	private FlAttachment flAttachment = new FlAttachment();
	
	/** The fl request. */
	private FlFulfilmentRequest flRequest;
	
	/** The fl delivery order. */
	private FlDeliveryOrder flDeliveryOrder;
	
	/** The delivery address. */
	private FlDeliveryAddress deliveryAddress;
	
	/** The fulfilment order. */
	private FlFulfilmentOrder fulfilmentOrder;
	
	/** The selected del addr. */
	private FlDeliveryAddress selectedDelAddr;
	
	/** The selected addr row. */
	private FlDeliveryAddress selectedAddrRow;
	
	/** The selected fl order. */
	private FlFulfilmentOrder selectedFlOrder;
	
	/** The stop ins. */
	private FlStopInstr stopIns;
	
	/** The selected del addr list. */
	private List<FlDeliveryAddress> selectedDelAddrList;
	
	/** The pending do list. */
	private List<FlFulfilmentOrder> pendingDoList;
	
	/** The delivery address list. */
	private List<FlDeliveryAddress>deliveryAddressList;
	
	/** The pm financial orgs. */
	private List<PmFinancialOrg> pmFinancialOrgs;
	
	/** The reason descs. */
	private List<PmReasonDesc> reasonDescs;
	
	/** The fl item list. */
	private List<FlFulfilmentItem> flItemList;
	
	/** The approver model. */
	private DualListModel<String> approverModel;
	
	/** The selected lov del addr. */
	private String selectedLovDelAddr;
	
	/** The factoring check. */
	private String factoringCheck="false";
	
	/** The doc type. */
	private String docType;
	
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
	
	//for termdays
	
	/** The term days. */
	private String termDays;
	
	/** The remove factoring. */
	private boolean removeFactoring;
	
	/** The selected tab index. */
	private int selectedTabIndex;
	
	/** The pm user. */
	private PmUser pmUser;
	
	/** The fl approver. */
	private String flApprover;
	
	/** The stop instr. */
	private FlFulfilmentItemAddr stopInstr;
	
	/** The doc maintain remarks. */
	private boolean docMaintainRemarks = false;
	
	//gpki signing
	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;
	
	/** The auto update. */
	private boolean autoUpdate = true;
	
	/** The text before sign. */
	private String textBeforeSign;
	
	/** The button action. */
	private String buttonAction;
	
	/** The spki backing bean. */
    @ManagedProperty(value="#{spkiBackingBean}")
    private SpkiBackingBean spkiBackingBean;
    
    private boolean supplier = false;
    
    private boolean hyperLinkPage = false;
    private List<PmUser> approverList;
    
    private IPagingDataModel<FlFulfilmentOrder> pendingDoDataModel = new PendingDoPagingDataModel();
    
    /**
	 * @return the pendingDoDataModel
	 */
	public IPagingDataModel<FlFulfilmentOrder> getPendingDoDataModel() {
		return pendingDoDataModel;
	}

	/**
	 * @param pendingDoDataModel the pendingDoDataModel to set
	 */
	public void setPendingDoDataModel(
			IPagingDataModel<FlFulfilmentOrder> pendingDoDataModel) {
		this.pendingDoDataModel = pendingDoDataModel;
	}
	
	/**
	 * Gets the faces scopes.
	 *
	 * @return the faces scopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}


	/**
	 * The Class PendingDoPagingDataModel.
	 */
	private class PendingDoPagingDataModel extends PagingDataModel<FlFulfilmentOrder> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new pending do paging data model.
		 */
		public PendingDoPagingDataModel() {
			super("flPendingDoListDt");
		}
		
		/* (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<FlFulfilmentOrder> pageChangeListener(ISearchBean<FlFulfilmentOrder> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {
				FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
				reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
				searchBean.setUseEntityClass(true);
				reqDTO.setPendingDoSearchBean(searchBean);
				IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
				FulfilmentResDTO resDTO = orderSO.searchPendingDoList(reqDTO);
				if(resDTO.isSuccessful()) {
					return resDTO.getPendingDoResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see my.ep.web.managedbean.BaseManagedBean#getTaskName()
	 */
	public String getTaskName() {
		return taskName;
	}

	/* (non-Javadoc)
	 * @see my.ep.web.managedbean.BaseManagedBean#setTaskName(java.lang.String)
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * Gets the process name.
	 *
	 * @return the process name
	 */
	public String getProcessName() {
		return processName;
	}

	/**
	 * Sets the process name.
	 *
	 * @param processName the new process name
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	/**
	 * Gets the task list.
	 *
	 * @return the task list
	 */
	public List<WorklistItemVO> getTaskList() {
		if(taskList==null)
			taskList = new ArrayList<WorklistItemVO>();
		return taskList;
	}

	/**
	 * Sets the task list.
	 *
	 * @param taskList the new task list
	 */
	public void setTaskList(List<WorklistItemVO> taskList) {
		this.taskList = taskList;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
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
	 * @param flRemark the flRemark to set
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
	 * @param flAttachment the flAttachment to set
	 */
	public void setFlAttachment(FlAttachment flAttachment) {
		this.flAttachment = flAttachment;
	}

	/**
	 * Gets the fl delivery order.
	 *
	 * @return the flDeliveryOrder
	 */
	public FlDeliveryOrder getFlDeliveryOrder() {
		return flDeliveryOrder;
	}

	/**
	 * Sets the fl delivery order.
	 *
	 * @param flDeliveryOrder the flDeliveryOrder to set
	 */
	public void setFlDeliveryOrder(FlDeliveryOrder flDeliveryOrder) {
		this.flDeliveryOrder = flDeliveryOrder;
	}

	/**
	 * Gets the delivery address.
	 *
	 * @return the deliveryAddress
	 */
	public FlDeliveryAddress getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * Sets the delivery address.
	 *
	 * @param deliveryAddress the deliveryAddress to set
	 */
	public void setDeliveryAddress(FlDeliveryAddress deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	/**
	 * Gets the fulfilment order.
	 *
	 * @return the fulfilmentOrder
	 */
	public FlFulfilmentOrder getFulfilmentOrder() {
		return fulfilmentOrder;
	}

	/**
	 * Sets the fulfilment order.
	 *
	 * @param fulfilmentOrder the fulfilmentOrder to set
	 */
	public void setFulfilmentOrder(FlFulfilmentOrder fulfilmentOrder) {
		this.fulfilmentOrder = fulfilmentOrder;
	}

	/**
	 * Gets the pending do list.
	 *
	 * @return the pendingDoList
	 */
	public List<FlFulfilmentOrder> getPendingDoList() {
		return pendingDoList;
	}

	/**
	 * Sets the pending do list.
	 *
	 * @param pendingDoList the pendingDoList to set
	 */
	public void setPendingDoList(List<FlFulfilmentOrder> pendingDoList) {
		this.pendingDoList = pendingDoList;
	}

	/**
	 * Gets the delivery address list.
	 *
	 * @return the deliveryAddressList
	 */
	public List<FlDeliveryAddress> getDeliveryAddressList() {
		return deliveryAddressList;
	}

	/**
	 * Sets the delivery address list.
	 *
	 * @param deliveryAddressList the deliveryAddressList to set
	 */
	public void setDeliveryAddressList(List<FlDeliveryAddress> deliveryAddressList) {
		this.deliveryAddressList = deliveryAddressList;
	}

	/**
	 * Checks if is contract request.
	 *
	 * @return the contractRequest
	 */
	public boolean isContractRequest() {
		return contractRequest;
	}

	/**
	 * Sets the contract request.
	 *
	 * @param contractRequest the contractRequest to set
	 */
	public void setContractRequest(boolean contractRequest) {
		this.contractRequest = contractRequest;
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
	 * Gets the fl item list.
	 *
	 * @return the flItemList
	 */
	public List<FlFulfilmentItem> getFlItemList() {
		return flItemList;
	}

	/**
	 * Sets the fl item list.
	 *
	 * @param flItemList the flItemList to set
	 */
	public void setFlItemList(List<FlFulfilmentItem> flItemList) {
		this.flItemList = flItemList;
	}

	/**
	 * Gets the fl request.
	 *
	 * @return the flRequest
	 */
	public FlFulfilmentRequest getFlRequest() {
		return flRequest;
	}

	/**
	 * Sets the fl request.
	 *
	 * @param flRequest the flRequest to set
	 */
	public void setFlRequest(FlFulfilmentRequest flRequest) {
		this.flRequest = flRequest;
	}

	/**
	 * Gets the approver model.
	 *
	 * @return the approverModel
	 */
	public DualListModel<String> getApproverModel() {
		return approverModel;
	}

	/**
	 * Sets the approver model.
	 *
	 * @param approverModel the approverModel to set
	 */
	public void setApproverModel(DualListModel<String> approverModel) {
		this.approverModel = approverModel;
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
	 * Gets the selected lov del addr.
	 *
	 * @return the selectedLovDelAddr
	 */
	public String getSelectedLovDelAddr() {
		return selectedLovDelAddr;
	}

	/**
	 * Sets the selected lov del addr.
	 *
	 * @param selectedLovDelAddr the selectedLovDelAddr to set
	 */
	public void setSelectedLovDelAddr(String selectedLovDelAddr) {
		this.selectedLovDelAddr = selectedLovDelAddr;
	}

	/**
	 * Gets the selected fl order.
	 *
	 * @return the selectedFlOrder
	 */
	public FlFulfilmentOrder getSelectedFlOrder() {
		return selectedFlOrder;
	}

	/**
	 * Sets the selected fl order.
	 *
	 * @param selectedFlOrder the selectedFlOrder to set
	 */
	public void setSelectedFlOrder(FlFulfilmentOrder selectedFlOrder) {
		this.selectedFlOrder = selectedFlOrder;
	}

	/**
	 * Gets the selected del addr list.
	 *
	 * @return the selectedDelAddrList
	 */
	public List<FlDeliveryAddress> getSelectedDelAddrList() {
		return selectedDelAddrList;
	}

	/**
	 * Sets the selected del addr list.
	 *
	 * @param selectedDelAddrList the selectedDelAddrList to set
	 */
	public void setSelectedDelAddrList(List<FlDeliveryAddress> selectedDelAddrList) {
		this.selectedDelAddrList = selectedDelAddrList;
	}

	/**
	 * Gets the fl order id.
	 *
	 * @return the flOrderId
	 */
	public Long getFlOrderId() {
		return flOrderId;
	}

	/**
	 * Sets the fl order id.
	 *
	 * @param flOrderId the flOrderId to set
	 */
	public void setFlOrderId(Long flOrderId) {
		this.flOrderId = flOrderId;
	}

	/**
	 * Gets the stop request id.
	 *
	 * @return the stopRequestId
	 */
	public Long getStopRequestId() {
		return stopRequestId;
	}

	/**
	 * Sets the stop request id.
	 *
	 * @param stopRequestId the stopRequestId to set
	 */
	public void setStopRequestId(Long stopRequestId) {
		this.stopRequestId = stopRequestId;
	}

	/**
	 * Gets the factoring check.
	 *
	 * @return the factoringCheck
	 */
	public String getFactoringCheck() {
		return factoringCheck;
	}

	/**
	 * Sets the factoring check.
	 *
	 * @param factoringCheck the factoringCheck to set
	 */
	public void setFactoringCheck(String factoringCheck) {
		this.factoringCheck = factoringCheck;
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
	 * Gets the pm financial orgs.
	 *
	 * @return the pmFinancialOrgs
	 */
	public List<PmFinancialOrg> getPmFinancialOrgs() {
		return pmFinancialOrgs;
	}

	/**
	 * Sets the pm financial orgs.
	 *
	 * @param pmFinancialOrgs the pmFinancialOrgs to set
	 */
	public void setPmFinancialOrgs(List<PmFinancialOrg> pmFinancialOrgs) {
		this.pmFinancialOrgs = pmFinancialOrgs;
	}

	/**
	 * Checks if is removes the factoring.
	 *
	 * @return the removeFactoring
	 */
	public boolean isRemoveFactoring() {
		return removeFactoring;
	}

	/**
	 * Sets the removes the factoring.
	 *
	 * @param removeFactoring the removeFactoring to set
	 */
	public void setRemoveFactoring(boolean removeFactoring) {
		this.removeFactoring = removeFactoring;
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
	 * Gets the stop ins.
	 *
	 * @return the stopIns
	 */
	public FlStopInstr getStopIns() {
		return stopIns;
	}

	/**
	 * Sets the stop ins.
	 *
	 * @param stopIns the stopIns to set
	 */
	public void setStopIns(FlStopInstr stopIns) {
		this.stopIns = stopIns;
	}

	/**
	 * Gets the reason descs.
	 *
	 * @return the reasonDescs
	 */
	public List<PmReasonDesc> getReasonDescs() {
		return reasonDescs;
	}

	/**
	 * Sets the reason descs.
	 *
	 * @param reasonDescs the reasonDescs to set
	 */
	public void setReasonDescs(List<PmReasonDesc> reasonDescs) {
		this.reasonDescs = reasonDescs;
	}

	/**
	 * Gets the fl approver.
	 *
	 * @return the flApprover
	 */
	public String getFlApprover() {
		return flApprover;
	}

	/**
	 * Sets the fl approver.
	 *
	 * @param flApprover the flApprover to set
	 */
	public void setFlApprover(String flApprover) {
		this.flApprover = flApprover;
	}

	/**
	 * Gets the stop instr.
	 *
	 * @return the stopInstr
	 */
	public FlFulfilmentItemAddr getStopInstr() {
		return stopInstr;
	}



	/**
	 * Sets the stop instr.
	 *
	 * @param stopInstr the stopInstr to set
	 */
	public void setStopInstr(FlFulfilmentItemAddr stopInstr) {
		this.stopInstr = stopInstr;
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
	 * Gets the back to previous.
	 *
	 * @return the backToPrevious
	 */
	public String getBackToPrevious() {
		return backToPrevious;
	}

	/**
	 * Sets the back to previous.
	 *
	 * @param backToPrevious the backToPrevious to set
	 */
	public void setBackToPrevious(String backToPrevious) {
		this.backToPrevious = backToPrevious;
	}

	/**
	 * @return the spkiBackingBean
	 */
	public SpkiBackingBean getSpkiBackingBean() {
		return spkiBackingBean;
	}

	/**
	 * @param spkiBackingBean the spkiBackingBean to set
	 */
	public void setSpkiBackingBean(SpkiBackingBean spkiBackingBean) {
		this.spkiBackingBean = spkiBackingBean;
	}

	/**
	 * @return the supplier
	 */
	public boolean isSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(boolean supplier) {
		this.supplier = supplier;
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
		return approverList;
	}

	/**
	 * @param approverList the approverList to set
	 */
	public void setApproverList(List<PmUser> approverList) {
		this.approverList = approverList;
	}

}
