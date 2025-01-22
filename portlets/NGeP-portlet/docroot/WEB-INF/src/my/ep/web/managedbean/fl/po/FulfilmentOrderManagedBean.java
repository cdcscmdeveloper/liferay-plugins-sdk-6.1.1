/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.po;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.persistence.entity.ct.VCtActiveContract;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.order.FlStopInstr;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.fl.yep.FlYepTasklist;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.vo.DisciplinaryActionVO;

/**
 * Managed Bean for Fulfillment Order.
 *
 * @author Giribabu
 * @since 2/11/2013
 */
@ManagedBean(name = "flOrderManagedBean")
@SessionScoped
public class FulfilmentOrderManagedBean extends BaseManagedBean {
	//test
	private static Logger log = Logger.getLogger(FulfilmentOrderManagedBean.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private List<WorklistItemVO> taskList;
	private String processName;
	private String status;
	private String taskName;
	private Long flOrderId;
	private Long stopRequestId;
	private String docNo;
	private String delOrdNo;
	private String delOrdSupNo;
	private String title;
	private PmOrgProfile selectedOrgProfile;
	private SelectItem[] docStatusList;
	private SelectItem[] docDeliveryOrderStatusList;
	private Date fromDate;
	private Date toDate;
	
	private boolean contractRequest;
	
	/*#28375 Logan*/
	private String carryForward="";
	private String Proceed="";
	private FlYepTasklist checkcarryForward;
	private String patCheck="";
	private boolean disable;
	private String buttonVal;
	private String backToPrevious;
	private List<FlRemark> previousRemarkList;
	private List<FlAttachment> previousAttachmentList;	
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
	private List<PmFinancialOrg> pmFinancialOrgs;
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
	
	private TreeNode deliveryLineItemRoot ;
	
	//for termdays
	private String termDays;
	private boolean removeFactoring;
	private int selectedTabIndex;
	
	private PmUser pmUser;
	private String flApprover;
	private FlFulfilmentItemAddr stopInstr;
	private boolean docMaintainRemarks = false;
	
	//gpki signing
	private PmDigiSign pmDigiSign;
	private boolean autoUpdate = true;
	private String textBeforeSign;
	private String buttonAction;
    @ManagedProperty(value="#{spkiBackingBean}")
    private SpkiBackingBean spkiBackingBean;
    
    private boolean supplier = false;
    private boolean hyperLinkPage = false;
    private boolean prevHideRemarks;
    private List<PmUser> approverList;
    
    private boolean gfmasError=false;
	private String gfmasErrorContent;
	private String gfmasErrorCode;
	private String gfmasErrorDesc;
	
	private Long docStatusId;
	private Long delOrdStatusId;
	private List<Long> statusIds;
	private BigDecimal totalPendingFl;
	private BigDecimal totalPendingPartialFl;
	private BigDecimal totalPendingSupplierAck;
	private BigDecimal totalPendingInvoice;
    
    private IPagingDataModel<FlFulfilmentOrder> pendingDoDataModel = new PendingDoPagingDataModel();
    private IPagingDataModel<FlFulfilmentOrder> pendingFlDataModel = new PendingFlPagingDataModel();
	private BigDecimal orderSummaryOrderedAmtGrandTotal ;
	private boolean showCommitmentNo;
	private String espkbCommitmentNo;
	private boolean yep;
	//26279 - start
	private String disciplinaryActionMessage;
	private List<DisciplinaryActionVO> disciplineVOs;
	private SmDisciplinaryAction smDisciplinaryAction;
	//26279 - end
	//28377 - start
	private String FileDirectory;
	private FlAttachment attachment;
	//28377 - end
	
	//#28374	Vinod Start
	private String progressStatus;
	private String iscarryForward="";
	//#28374	Vinod End
	
	//34316-Start
	private int yepButtonFlag =0;
	//34316-End
	
	private boolean isDpContinuityView = false;//#26790 note 266
	
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
				if(getDocStatusId() != null){
					reqDTO.setStatusId(docStatusId);
				}else{
					reqDTO.setStatusId(FulfilmentStatusConst.PO_PENDING_BOTH);
				}
				if(!getStatusIds().isEmpty()){
					reqDTO.setStatusIds(getStatusIds());
				}
				
				if(getTitle() != null && getTitle().length() > 0){
					reqDTO.setTitle(getTitle());
				}
				if(getDocNo() != null && StringUtils.isNotBlank(getDocNo())){
					reqDTO.setFlDocNo(getDocNo());
				}
				if(getFromDate() != null){
					reqDTO.setFromDate(getFromDate());
					reqDTO.setToDate(getToDate());
				}
				if(getSelectedOrgProfile() != null){
					reqDTO.setOrgProfileId(getSelectedOrgProfile().getOrgProfileId());
				}
				
				if(getDelOrdNo() != null && StringUtils.isNotBlank(getDelOrdNo())){
					reqDTO.setFlDelOrdNo(getDelOrdNo());
				}
				
				if(getDelOrdSupNo() != null && StringUtils.isNotBlank(getDelOrdSupNo())){
					reqDTO.setFlDelOrdSupNo(getDelOrdSupNo());
				}

				if(getDelOrdStatusId() != null){
					reqDTO.setDelStatusId(delOrdStatusId);
				}
				
				FlDeliveryOrder deliveryReqDTO = new FlDeliveryOrder();
				
				IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
				IDeliveryOrderSO deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IDeliveryOrderSO.class);
				
				FulfilmentResDTO resDTO = orderSO.searchPendingDoList(reqDTO);
				if(resDTO.isSuccessful()) {
					
					for(FlFulfilmentOrder flFulfilmentPurchaseOrder : resDTO.getPendingDoResultBean().getResultList()) {
							flFulfilmentPurchaseOrder.setFlDeliveryOrders(deliveryOrderSO.getDeliveryOrderByFulfilmentOrderId(flFulfilmentPurchaseOrder.getFulfilmentOrderId()).getDeliveryOrdersList());
					}
					return resDTO.getPendingDoResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	/**
	 * The Class PendingFlPagingDataModel.
	 */
	private class PendingFlPagingDataModel extends PagingDataModel<FlFulfilmentOrder> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new pending do paging data model.
		 */
		public PendingFlPagingDataModel() {
			super("flPendingFlListDt");
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
				List<PmUserOrg> pmUserOrgs = PmCommonUtil.retrieveActiveOrg(getFacesScopes().getUserSessionBean().getUserId());
				if(pmUserOrgs.get(0) == null){
					log.info("User does not have any active orgs.");
				}
				if(pmUserOrgs.get(0) != null || pmUserOrgs.get(0).getPmOrgProfile() != null){
					reqDTO.setOrgProfileId(pmUserOrgs.get(0).getPmOrgProfile().getOrgProfileId());
				}
				
				searchBean.setUseEntityClass(true);
				reqDTO.setPendingDoSearchBean(searchBean);
				if(!getStatusIds().isEmpty()){
					reqDTO.setStatusIds(getStatusIds());
				}
				
				IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
				FulfilmentResDTO resDTO = orderSO.searchPendingFlList(reqDTO);
				if(resDTO.isSuccessful()) {
					return resDTO.getPendingFlResultBean();
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

	public boolean isGfmasError() {
		return gfmasError;
	}

	public void setGfmasError(boolean gfmasError) {
		this.gfmasError = gfmasError;
	}

	public String getGfmasErrorContent() {
		return gfmasErrorContent;
	}

	public void setGfmasErrorContent(String gfmasErrorContent) {
		this.gfmasErrorContent = gfmasErrorContent;
	}

	public List<Long> getStatusIds() {
		if(statusIds == null){
			statusIds = new ArrayList<Long>();
		}
		return statusIds;
	}

	public void setStatusIds(List<Long> statusIds) {
		this.statusIds = statusIds;
	}

	public IPagingDataModel<FlFulfilmentOrder> getPendingFlDataModel() {
		return pendingFlDataModel;
	}

	public void setPendingFlDataModel(IPagingDataModel<FlFulfilmentOrder> pendingFlDataModel) {
		this.pendingFlDataModel = pendingFlDataModel;
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

	public Long getDocStatusId() {
		return docStatusId;
	}

	public void setDocStatusId(Long docStatusId) {
		this.docStatusId = docStatusId;
	}

	public Long getDelOrdStatusId() {
		return delOrdStatusId;
	}

	public void setDelOrdStatusId(Long delOrdStatusId) {
		this.delOrdStatusId = delOrdStatusId;
	}

	public SelectItem[] getDocDeliveryOrderStatusList() {
		return docDeliveryOrderStatusList;
	}

	public void setDocDeliveryOrderStatusList(
			SelectItem[] docDeliveryOrderStatusList) {
		this.docDeliveryOrderStatusList = docDeliveryOrderStatusList;
	}
	
	public String getGfmasErrorDesc() {
		return gfmasErrorDesc;
	}

	public void setGfmasErrorDesc(String gfmasErrorDesc) {
		this.gfmasErrorDesc = gfmasErrorDesc;
	}

	public String getGfmasErrorCode() {
		return gfmasErrorCode;
	}

	public void setGfmasErrorCode(String gfmasErrorCode) {
		this.gfmasErrorCode = gfmasErrorCode;
	}

	public BigDecimal getTotalPendingFl() {
		return totalPendingFl;
	}

	public void setTotalPendingFl(BigDecimal totalPendingFl) {
		this.totalPendingFl = totalPendingFl;
	}

	public BigDecimal getTotalPendingPartialFl() {
		return totalPendingPartialFl;
	}

	public void setTotalPendingPartialFl(BigDecimal totalPendingPartialFl) {
		this.totalPendingPartialFl = totalPendingPartialFl;
	}

	public BigDecimal getTotalPendingSupplierAck() {
		return totalPendingSupplierAck;
	}

	public void setTotalPendingSupplierAck(BigDecimal totalPendingSupplierAck) {
		this.totalPendingSupplierAck = totalPendingSupplierAck;
	}

	public BigDecimal getTotalPendingInvoice() {
		return totalPendingInvoice;
	}

	public void setTotalPendingInvoice(BigDecimal totalPendingInvoice) {
		this.totalPendingInvoice = totalPendingInvoice;
	}

	public boolean isPrevHideRemarks() {
		return prevHideRemarks;
	}

	public void setPrevHideRemarks(boolean prevHideRemarks) {
		this.prevHideRemarks = prevHideRemarks;
	}

	public boolean isShowCommitmentNo() {
		return showCommitmentNo;
	}

	public void setShowCommitmentNo(boolean showCommitmentNo) {
		this.showCommitmentNo = showCommitmentNo;
	}

	public String getEspkbCommitmentNo() {
		return espkbCommitmentNo;
	}

	public void setEspkbCommitmentNo(String espkbCommitmentNo) {
		this.espkbCommitmentNo = espkbCommitmentNo;
	}

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}
	
	public String getDelOrdNo() {
		return delOrdNo;
	}

	public void setDelOrdNo(String delOrdNo) {
		this.delOrdNo = delOrdNo;
	}

	public String getDelOrdSupNo() {
		return delOrdSupNo;
	}

	public void setDelOrdSupNo(String delOrdSupNo) {
		this.delOrdSupNo = delOrdSupNo;
	}

	public TreeNode getDeliveryLineItemRoot() {
		return deliveryLineItemRoot;
	}

	public void setDeliveryLineItemRoot(TreeNode deliveryLineItemRoot) {
		this.deliveryLineItemRoot = deliveryLineItemRoot;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public PmOrgProfile getSelectedOrgProfile() {
		return selectedOrgProfile;
	}

	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	public void setDocStatusList(SelectItem[] docStatusList) {
		this.docStatusList = docStatusList;
	}

	public SelectItem[] getDocStatusList() {
		return docStatusList;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	//26279 - start
	public String getDisciplinaryActionMessage() {
		return disciplinaryActionMessage;
	}

	public void setDisciplinaryActionMessage(String disciplinaryActionMessage) {
		this.disciplinaryActionMessage = disciplinaryActionMessage;
	}	

	public List<DisciplinaryActionVO> getDisciplineVOs() {
		return disciplineVOs;
	}

	public void setDisciplineVOs(List<DisciplinaryActionVO> disciplineVOs) {
		this.disciplineVOs = disciplineVOs;
	}

	public SmDisciplinaryAction getSmDisciplinaryAction() {
		return smDisciplinaryAction;
	}

	public void setSmDisciplinaryAction(SmDisciplinaryAction smDisciplinaryAction) {
		this.smDisciplinaryAction = smDisciplinaryAction;
	}
	//26279 - end

	public boolean isYep() {
		return yep;
	}

	public void setYep(boolean yep) {
		this.yep = yep;
	}
    /*#28375 - Beginning Logan*/
	public String getCarryForward() {
		return carryForward;
	}

	public void setCarryForward(String carryForward) {
		this.carryForward = carryForward;
	}
	
	public String getProceed() {
		return Proceed;
	}

	public void setProceed(String proceed) {
		Proceed = proceed;
	}
	
	public boolean isDisable() {
		return disable;
	}

	public void setDisable(boolean disable) {
		this.disable = disable;
	}
	public FlYepTasklist getCheckcarryForward() {
		return checkcarryForward;
	}

	public void setCheckcarryForward(FlYepTasklist checkcarryForward) {
		this.checkcarryForward = checkcarryForward;
	}
	
	public String getPatCheck() {
		return patCheck;
	}

	public void setPatCheck(String patCheck) {
		this.patCheck = patCheck;
	}	
	
	/*#28375 - Ending Logan*/
	
	//#28374	Vinod Start
	public String getProgressStatus() {
		return progressStatus;
	}
	public void setProgressStatus(String progressStatus) {
		this.progressStatus = progressStatus;
	}
	public String getIscarryForward() {
		return iscarryForward;
	}
	public void setIscarryForward(String iscarryForward) {
		this.iscarryForward = iscarryForward;
	}
	//#28374	Vinod End
	
	public String getFileDirectory() {
		return FileDirectory;
	}

	public void setFileDirectory(String fileDirectory) {
		FileDirectory = fileDirectory;
	}

	public FlAttachment getAttachment() {
		return attachment;
	}

	public void setAttachment(FlAttachment attachment) {
		this.attachment = attachment;
	}
	
	//34316 - start
	/**
	 * Gets the yepButtonFlag.
	 *
	 * @return the yepButtonFlag
	 */
	public int getYepButtonFlag() {
		return yepButtonFlag;
	}

	/**
	 * Sets the yepButtonFlag.
	 *
	 * @param yepButtonFlag the yepButtonFlag to set
	 */
	public void setYepButtonFlag(int yepButtonFlag) {
		this.yepButtonFlag = yepButtonFlag;
	}
	//34316 - end

	//#25790 - start
	/**
	 * Checks if from DP continuity view.
	 *
	 * @return {@code true} if from DP continuity view, {@code false} otherwise.
	 */
	public boolean isDpContinuityView() {
	    return isDpContinuityView;
	}

	/**
	 * Sets the DP continuity view .
	 *
	 * @param isDpContinuityView {@code true} to enable the DP continuity view, {@code false} to disable it.
	 */
	public void setDpContinuityView(boolean isDpContinuityView) {
	    this.isDpContinuityView = isDpContinuityView;
	}
	//#25790 - end
	

}
