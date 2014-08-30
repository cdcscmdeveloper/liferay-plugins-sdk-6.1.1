/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.invoice;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.invoice.FLInvoiceDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.order.FlStopInstrItem;
import my.ep.persistence.entity.fl.payment.FlInvoice;
import my.ep.persistence.entity.fl.payment.FlInvoiceItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.invoice.InvoiceManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

import com.liferay.portal.util.PortalUtil;
//import my.ep.jaxb.fl.invoicepaymentdata.FLInvoiceAndPaymentDataType;

/**
 * Backing Bean for invoice
 * 
 * @author KK LIM
 * @since 30/10/2013
 */

@ManagedBean(name = "invoiceBackingBean")
@ViewScoped
public class InvoiceBackingBean extends BaseBackingBean<InvoiceManagedBean> {
	private static Logger log = Logger.getLogger(InvoiceBackingBean.class);
	private String className="@(.myInvClassName)";
	private IPagingDataModel<FlInvoice> flInvoiceDataModel = new FlInvoicePagingDataModel();

	
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public InvoiceBackingBean(){
	}

	public String submitAction(String action) {
		String token = getFacesScopes().getBPMAuthenticationCtxToken();
		IInvoiceSO invoiceSO = null;
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			FlInvoice invoice = getManagedBean().getInvoice();
			invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
			
			// Need to call this as user may not click on summary menu
			calInvSummary();
			
			// Check is it DAN exist
			if(action.equalsIgnoreCase(my.ep.constant.BpmOutcomeConst.BPM_SUBMIT)) {
				BigDecimal adjustedAmt = new BigDecimal(0);
				for(FlInvoiceItem invoiceItem : invoice.getFlInvoiceItem()){
					adjustedAmt = invoiceItem.getInvAmt().subtract(invoiceItem.getOrderedAmt());
					if(adjustedAmt.doubleValue()>0) {
						reqDTO.setDan(true);
					} else if(adjustedAmt.doubleValue()<0) {
						reqDTO.setCan(true);
					}
					if(reqDTO.isDan() && reqDTO.isCan()) {
						break;
					}
				}
			}
			
			reqDTO.setInvoice(invoice);
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setToken(token);
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			if(flBB.getManagedBean().getRemarkList().size() >0){
				reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			}
			
			reqDTO.setUserRole(getUserRole());
			if(!(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE))){
				reqDTO.setPmUser(getManagedBean().getPmUser());
				if(null != getManagedBean().getInvoice().getSupplierInvoiceRef()){
					getManagedBean().getInvoice().setSupplierInvoiceRef(getManagedBean().getInvoice().getSupplierInvoiceRef());
				}
				reqDTO.setInvoice(getManagedBean().getInvoice());
				resDTO = invoiceSO.invoiceModification(reqDTO);
			}else{
				reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
				resDTO = invoiceSO.saveInvoice(reqDTO);
			}
			if(resDTO.isSuccessful()) {
				String messagePrefix = "";
				String messageId = MessageConst.INFO_700026;
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700004;
				} else if(resDTO.getInvoice()!=null && resDTO.getInvoice().getAp59Status()!=null && 
						resDTO.getInvoice().getAp59Status().getStatusId().longValue()==FulfilmentStatusConst.AP59_PENDING_AP59_DECISION) {
					messageId = MessageConst.INFO_700004;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					messageId = MessageConst.INFO_700007;
					messagePrefix = "Cancellation for ";
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					messageId = MessageConst.INFO_700008;
					messagePrefix = "Cancellation for ";
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
					messageId = MessageConst.INFO_700011;
					messagePrefix = "Update for ";						
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)){
					messageId = MessageConst.INFO_700043;
					messagePrefix = "Update for ";
				} else {
					if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
						messagePrefix = "Cancellation for ";	
					}
					messageId = MessageConst.INFO_700005;
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, 
						null,
						MessageUtils.getMessage(messageId, 
								new String[]{messagePrefix+getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_INVOICE), resDTO.getInvoiceNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, 
								new FacesScopes().getUserLocale())));
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && resDTO.getInvoice()!=null && resDTO.getInvoice().getAp59Status()!=null && 
					resDTO.getInvoice().getAp59Status().getStatusId().longValue()==FulfilmentStatusConst.AP59_PENDING_AP59_DECISION) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_745001, null, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				}
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					getManagedBean().setInvoice(resDTO.getInvoice());
					flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
					if(getManagedBean().getInvoice().getFlFulfilmentOrder().isFactored()){
						getFactoringBranchDtl(); 
					}
				}
			}
		}finally {
			invoiceSO = null;
		}
		return null;
	}
	
	private String getUserRole() {		
		if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) 
			return RoleCode.MOF_SUPPLIER_ADMIN.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value()))
			return RoleCode.MOF_SUPPLIER_USER.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()))
			return RoleCode.BASIC_SUPPLIER_ADMIN.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()))
			return RoleCode.BASIC_SUPPLIER_USER.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()))
			return RoleCode.BPK_APPROVER.value();
		else 
			return null;
	}

	public void preRenderView() {
		IInvoiceSO invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		System.out.println("taskId:" + taskId);
		if(taskId != null){
			renewSesionObject();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
			bpmReqDTO.setCtxToken(ctx);
			bpmReqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(bpmReqDTO);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			getManagedBean().setModificationCancel(false);
			if(FLProcessNameConst.ACK_MODIFIED_INVOICE_HT.equals(res.getTaskName()) || FLProcessNameConst.REVIEW_CANCEL_INVOICE_HT.equals(res.getTaskName())){
				invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
				reqDTO.setInvoiceId(Long.valueOf(res.getDocId()));
				resDTO = invoiceSO.retrieveInvoiceByInvoiceId(reqDTO);
				getManagedBean().setDocProcess(res.getDocType());
				getManagedBean().setInvoice(resDTO.getInvoice());
				//invBB.getManagedBean().getInvoice().getFlFulfilmentOrder().setFlFulfilmentRequest(resDTO.getInvoice().getFlRequest());
				getManagedBean().getInvoice().setFlRequest(resDTO.getInvoice().getFlRequest());
				getManagedBean().setFulfilmentRequest(resDTO.getInvoice().getFlRequest());
				getManagedBean().setPmUser(reqDTO.getPmUser());
				getManagedBean().setButtonVal("ACK");
				getManagedBean().setModificationCancel(true);
				getManagedBean().setTaskId(taskId);
				getManagedBean().setOutComes(res.getAction());
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
			}else{
				Object obj = res.getPojo();
				String[] outcome = new String[res.getAction().length + 1];
				outcome[0] = BpmOutcomeConst.BPM_SAVE;
				int i = 1;
				for (String action : res.getAction()) {
					outcome[i++] = action;
				}
				FLInvoiceDataType paymentObj = (FLInvoiceDataType)obj;
				IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
				
				reqDTO.setFlOrderId(paymentObj.getFlOrderId());
				resDTO = invoiceSO.retrieveInvoiceByFlOrderId(reqDTO);//if save button are exist
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				
				if(resDTO.getInvoice() == null){
					getManagedBean().setInvoice(new FlInvoice());
					getManagedBean().getInvoice().setFlInvoiceItem(new ArrayList<FlInvoiceItem>());
					reqDTO.setFlRequestId(paymentObj.getFlRequestId().longValue());
					resDTO = fulfilmentSO.retrieveFulfilmentRequest(reqDTO);
					flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
					getInvoiceTable(resDTO);				
				} else {
					getManagedBean().setInvoice(resDTO.getInvoice());
					flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
				}
				
				getManagedBean().setChargeLineItemRoot(null);
				getManagedBean().setDocType(resDTO.getFlFulfilmentRequest().getDocType());
				getManagedBean().setTaskId(taskId);
				getManagedBean().setOutComes(outcome);
				
				getManagedBean().getInvoice().setFlFulfilmentOrder(resDTO.getFlFulfilmentRequest().getFlFulfilmentOrder());
				getManagedBean().getInvoice().setFlRequest(resDTO.getFlFulfilmentRequest());
				if(getManagedBean().getInvoice().getFlFulfilmentOrder().isFactored()){
					getFactoringBranchDtl(); 
				}
				
				getManagedBean().setSelectedTabIndex(0);
				if(getManagedBean().getInvoice().getStatus()==null) {
					IStatusSO statusSO = (IStatusSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
					StatusReqDTO statusReqDTO = new StatusReqDTO();
					statusReqDTO.setStatusId(FulfilmentStatusConst.IN_PENDING_INVOICE);
					getManagedBean().getInvoice().setStatus(statusSO.retrieveStatusByStatusId(statusReqDTO).getPmStatus());
				}
				
				if(getManagedBean().getInvoice().getFlFulfilmentOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
					new FulfilmentBackingBean(ParameterConst.DOCTYPE_INVOICE, getManagedBean().getInvoice().getFlRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
				} else {
					new FulfilmentBackingBean(ParameterConst.DOCTYPE_INVOICE, getManagedBean().getInvoice().getFlRequest().getCtContract().getContractNo());
				}	
			}
		}
	}
	
	public void factoringChecked(AjaxBehaviorEvent e) {
		System.out.println("====enter process checked====");
		// String dueDate = (String) ((UIOutput)e.getSource()).getValue();
		String id = (String) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
		getManagedBean().setFactoringCheck(id);

	}
	
	private void getInvoiceTable(FulfilmentResDTO resDTO){
		BigDecimal invoiceAmt = new BigDecimal(0);
		for(FlFulfilmentItem item : resDTO.getFlFulfilmentRequest().getFlFulfilmentItems()){
			FlInvoiceItem invoiceItem = new FlInvoiceItem();
			invoiceItem.setFlInvoice(getManagedBean().getInvoice());
			BigDecimal sumOrderedQty = new BigDecimal(0);
			BigDecimal sumOrderedAmt = new BigDecimal(0);
			BigDecimal sumRecalledQty = new BigDecimal(0);
			BigDecimal sumRecalledAmt = new BigDecimal(0);
			BigDecimal sumReceivedQty = new BigDecimal(0);
			BigDecimal sumReceivedAmt = new BigDecimal(0);
			invoiceItem.setFlFulfilmentItem(item);
			for(FlFulfilmentItemAddr itemAddr : item.getFlFulfilmentItemAddr()){
				sumOrderedQty = sumOrderedQty.add(itemAddr.getOrderedQty());
				sumOrderedAmt = sumOrderedAmt.add(itemAddr.getOrderedAmt());
				sumReceivedQty = sumReceivedQty.add(itemAddr.getChargeReceivedQty());
				sumReceivedAmt = sumReceivedAmt.add(itemAddr.getChargeReceivedAmt());
				if(itemAddr.getTempStopInstrItems()!=null && !itemAddr.getTempStopInstrItems().isEmpty()){
					for(FlStopInstrItem stopInstrItem : itemAddr.getTempStopInstrItems()){
						sumRecalledQty = sumRecalledQty.add(stopInstrItem.getRecalledQty());
						sumRecalledAmt = sumRecalledAmt.add(stopInstrItem.getRecalledAmt());
					}
				}
			}
			invoiceItem.setOrderedQty(sumOrderedQty);
			invoiceItem.setOrderedAmt(sumOrderedAmt);
			invoiceItem.setRecalledQty(sumRecalledQty);
			invoiceItem.setRecalledAmt(sumRecalledAmt);
			invoiceItem.setReceivedQty(sumReceivedQty);
			invoiceItem.setReceivedAmt(sumReceivedAmt);
			invoiceItem.setInvAmt(sumReceivedAmt);
			getManagedBean().getInvoice().getFlInvoiceItem().add(invoiceItem);
			invoiceAmt = invoiceAmt.add(sumReceivedAmt);
		}
		getManagedBean().getInvoice().setInvoiceAmtOri(invoiceAmt);
		getManagedBean().getInvoice().setInvoiceAmtNew(invoiceAmt);
		
		BigDecimal serviceFee = null;
		if(resDTO.getFlFulfilmentRequest().getFlFulfilmentOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
			serviceFee = new BigDecimal(FulfilmentConst.SERVICE_FEE_ZERO_POINT_EIGHT);
		} else {
			serviceFee = new BigDecimal(resDTO.getFlFulfilmentRequest().getCtContract().getServiceFee());
		}
		
		getManagedBean().setServiceCharge(Double.toString(serviceFee.doubleValue()));
		BigDecimal percentage = new BigDecimal(100);
		BigDecimal invMulty = new BigDecimal(0.0);
		BigDecimal feeAmount = new BigDecimal(0.0);
		invMulty = invoiceAmt.multiply(serviceFee);
		feeAmount = invMulty.divide(percentage);
		getManagedBean().getInvoice().setFeeAmt(feeAmount);
		getManagedBean().getInvoice().setSupplierOrFactorAmt(invoiceAmt.subtract(feeAmount));
	}
	
	public void retrieveInvTotalAmount(FlInvoiceItem flInvoiceItem){
		if(flInvoiceItem.getInvAmt().doubleValue()>flInvoiceItem.getReceivedAmt().doubleValue()) {
			flInvoiceItem.setInvAmt(flInvoiceItem.getReceivedAmt());
			throw new EpException(MessageConst.ERROR_100032, new String[]{"Invoice Amount","Received Amount"});
		}
	   BigDecimal totalInvoiceAmt = new BigDecimal(0);
	   for(TreeNode childNode : getManagedBean().getChargeLineItemRoot().getChildren()){
		   FlInvoiceItem data= (FlInvoiceItem) childNode.getData();
		   
		   if(flInvoiceItem.getFlFulfilmentItem().getCmItem().getItemId().longValue() == data.getFlFulfilmentItem().getCmItem().getItemId().longValue()){
			   BigDecimal invoiceAmount=new BigDecimal(0);
			   for(TreeNode subNode:childNode.getChildren()){
				   FlInvoiceItem subData=(FlInvoiceItem)subNode.getData();
				   invoiceAmount=invoiceAmount.add(subData.getInvAmt());
			   }
			   data.setInvAmt(invoiceAmount);
			   totalInvoiceAmt = totalInvoiceAmt.add(invoiceAmount);			  
		   } else {
			   totalInvoiceAmt = totalInvoiceAmt.add(data.getInvAmt());	
		   }
	   }
	   getManagedBean().setInvAmtGrandTotal(totalInvoiceAmt);
	}
	
	public void calInvSummary() {
		if (null == getManagedBean().getChargeLineItemRoot()) {
			getManagedBean().populateChargeLineData();
		}
		TreeNode root = getManagedBean().getChargeLineItemRoot();
		BigDecimal invoiceTotalAmount = new BigDecimal(0);
		BigDecimal orderTotalAmount = new BigDecimal(0);
		for (TreeNode childNode : root.getChildren()) {
			FlInvoiceItem data = (FlInvoiceItem) childNode.getData();
			invoiceTotalAmount = invoiceTotalAmount.add(data.getInvAmt());
			orderTotalAmount = orderTotalAmount.add(data.getOrderedAmt());
		}
		getManagedBean().getInvoice().setInvoiceAmtOri(orderTotalAmount);
		getManagedBean().getInvoice().setInvoiceAmtNew(invoiceTotalAmount);
		
		BigDecimal serviceFee = null;
		if(getManagedBean().getInvoice().getFlFulfilmentOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
			serviceFee = new BigDecimal(FulfilmentConst.SERVICE_FEE_ZERO_POINT_EIGHT);
		} else {
			serviceFee = new BigDecimal(getManagedBean().getInvoice().getFlRequest().getCtContract().getServiceFee());
		}
		
		getManagedBean().setServiceCharge(Double.toString(serviceFee.doubleValue()));
		BigDecimal percentage = new BigDecimal(100);
		BigDecimal invMulty = new BigDecimal(0.0);
		BigDecimal feeAmount = new BigDecimal(0.0);
		invMulty = invoiceTotalAmount.multiply(serviceFee);
		feeAmount = invMulty.divide(percentage);
		getManagedBean().getInvoice().setFeeAmt(feeAmount);
		getManagedBean().getInvoice().setSupplierOrFactorAmt(invoiceTotalAmount.subtract(feeAmount));
	}
	
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==1){
			if(null==getManagedBean().getChargeLineItemRoot()){
				 getManagedBean().populateChargeLineData();
			}
		setClassName("@(.invItemPnlClass)");
		}
		if(selectedTabIndex==2){
			calInvSummary();
		}
	}


	
	public List<SelectItem> getPopulateBankAccountList() {
		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,ISupplierSearchSO.class);
		List<SelectItem> populateBankAccountList = new ArrayList<SelectItem>();
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		
		if (this.getManagedBean().getInvoice() != null) {
			if(this.getManagedBean().getInvoice().getFlRequest().getFlSupplierDtl().getBranchName() != null ){
				supplierSearchReqDTO.setBranchName(getManagedBean().getInvoice().getFlRequest().getFlSupplierDtl().getBranchName());
				supplierSearchReqDTO.setSupplierId(getManagedBean().getInvoice().getFlRequest().getSmSupplier().getSupplierId());
				SupplierSearchResDTO resDTO = supplierSearchSO.retrieveSupplierBranchBanksByBranchName (supplierSearchReqDTO);
				for (SmSupplierBank obj : resDTO.getSmSupplierBankList()) {
					if(obj.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
						populateBankAccountList.add(new SelectItem(obj.getAccountNo(), obj.getAccountNo()));
						if(!getManagedBean().getSupplierBankAccountMap().containsKey(obj.getAccountNo())) {
							getManagedBean().getSupplierBankAccountMap().put(obj.getAccountNo(), obj);
						}
					}
				}
			}
			
			if (populateBankAccountList.size()==0  
					&& this.getManagedBean().getInvoice().getFlRequest().getSmSupplier().getSmAppl().getSmSupplierBanks() != null
					&& this.getManagedBean().getInvoice().getFlRequest().getSmSupplier().getSmAppl().getSmSupplierBanks().size() > 0) {
				List<SmSupplierBank> supplierBank = this.getManagedBean().getInvoice().getFlRequest().getSmSupplier().getSmAppl().getSmSupplierBanks();
				for (SmSupplierBank obj : supplierBank) {
					if(obj.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
						populateBankAccountList.add(new SelectItem(obj.getAccountNo(), obj.getAccountNo()));
						if(!getManagedBean().getSupplierBankAccountMap().containsKey(obj.getAccountNo())) {
							getManagedBean().getSupplierBankAccountMap().put(obj.getAccountNo(), obj);
						}
					}
				}
			}
		}
		return populateBankAccountList;
	}
	
	public void bankInfoActionListener() {
		SmSupplierBank supplierBank = getManagedBean().getSupplierBankAccountMap().get(getManagedBean().getInvoice().getAccountNo());
		if(supplierBank!=null) {
			getManagedBean().getInvoice().setPaymentBank(supplierBank.getPmFinancialOrg());
		}
	}
	public void getFactoringBranchDtl(){
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
		getManagedBean().setBranchName(null);
		getManagedBean().setSupplierAddress(null);
		getManagedBean().setPhoneNo(null);
		getManagedBean().setFaxNo(null);
		getManagedBean().setContactPerson(null);
		getManagedBean().setContactEmail(null);
		getManagedBean().setContactPhoneNo(null);
		
		if(getManagedBean().getInvoice().getFlFulfilmentOrder().getPmFinancialBranch()!=null && getManagedBean().getInvoice().getFlFulfilmentOrder().getPmFinancialBranch().getFinancialBranchId()!=null) {
			getManagedBean().setBranchName(getManagedBean().getInvoice().getFlFulfilmentOrder().getPmFinancialBranch().getBranchName());
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialBranchId(getManagedBean().getInvoice().getFlFulfilmentOrder().getPmFinancialBranch().getFinancialBranchId());
			reqDTO.setAddressType(AddressType.BILLING_ADDRESS.toString());
			
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if(!resDTO.getAddressTypeList().isEmpty()){
				getManagedBean().setSupplierAddress(getFullAddress(resDTO.getAddressTypeList().get(0).getPmAddress()));
				
				getManagedBean().setPhoneNo(resDTO.getAddressTypeList().get(0).getPmAddress().getPhoneNo());
				getManagedBean().setFaxNo(resDTO.getAddressTypeList().get(0).getPmAddress().getFaxNo());
				if(resDTO.getAddressTypeList().get(0).getPmContacts() != null ){
					PmContact pmContact = resDTO.getAddressTypeList().get(0).getPmContacts().get(0);
					getManagedBean().setContactPerson(pmContact.getContactName());
					getManagedBean().setContactEmail(pmContact.getEmail());
					getManagedBean().setContactPhoneNo(pmContact.getPhoneNo());
				}
			}
			for (PmFinancialAcc financialAcc : getManagedBean().getInvoice().getFlFulfilmentOrder().getPmFinancialBranch().getPmFinancialAccs()) {
				if(financialAcc.getEffDate().before(new Date()) && (financialAcc.getExpDate()==null || financialAcc.getExpDate().after(new Date()))) {
					getManagedBean().getInvoice().setAccountNo(financialAcc.getAccountNo());
					getManagedBean().getInvoice().setPaymentBank(financialAcc.getPmPaymentBankOrg());
					break;
				}
	        }
		} else { //reset back to HQ info
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialOrgId(getManagedBean().getInvoice().getFlFulfilmentOrder().getPmFinancialOrg().getFinancialOrgId());
			reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
			
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if(!resDTO.getAddressTypeList().isEmpty()){
				getManagedBean().setSupplierAddress(getFullAddress(resDTO.getAddressTypeList().get(0).getPmAddress()));
				getManagedBean().setPhoneNo(resDTO.getAddressTypeList().get(0).getPmAddress().getPhoneNo());
				getManagedBean().setFaxNo(resDTO.getAddressTypeList().get(0).getPmAddress().getFaxNo());
				if(!resDTO.getAddressTypeList().get(0).getPmContacts().isEmpty() ){
					PmContact pmContact = resDTO.getAddressTypeList().get(0).getPmContacts().get(0);
					getManagedBean().setContactPerson(pmContact.getContactName());
					getManagedBean().setContactEmail(pmContact.getEmail());
					getManagedBean().setContactPhoneNo(pmContact.getPhoneNo());
				}
			}
			
			for (PmFinancialAcc financialAcc : getManagedBean().getInvoice().getFlFulfilmentOrder().getPmFinancialOrg().getPmFinancialAccs()) {
				if(financialAcc.getEffDate().before(new Date()) && (financialAcc.getExpDate()==null || financialAcc.getExpDate().after(new Date()))) {
					getManagedBean().getInvoice().setAccountNo(financialAcc.getAccountNo());
					getManagedBean().getInvoice().setPaymentBank(financialAcc.getPmPaymentBankOrg());
					break;
				}
	        }
		}
	}
	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	
	public void searchDocument(){
		flInvoiceDataModel.reloadData();
	}
	
	private class FlInvoicePagingDataModel extends PagingDataModel<FlInvoice> {
		private static final long serialVersionUID = 1L;

		public FlInvoicePagingDataModel() {
			super("flInvoiceListDt");
		}
		
		@Override
	public ISearchResultBean<FlInvoice> pageChangeListener(ISearchBean<FlInvoice> searchBean) {
		if (log.isDebugEnabled()) {
			log.debug("pageChangeListener");
		}
		try {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("ap59Status.statusId",SearchOperator.OPERATOR_EQUALS.value(),FulfilmentStatusConst.AP59_PENDING_AP59_DECISION));
			
			if(getManagedBean().getLoaNo()!=null && getManagedBean().getLoaNo().length() > 0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("flRequest.ctContract.loa.loaNo",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getLoaNo()));
			}
			if(getManagedBean().getContractNo()!=null && getManagedBean().getContractNo().length() > 0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("flRequest.ctContract.contractNo",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getContractNo()));
			}
			if(getManagedBean().getTitle()!=null && getManagedBean().getTitle().length() > 0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("flRequest.title",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getTitle()));
			}
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setSearchBean(searchBean);
			
			IInvoiceSO invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
			FulfilmentResDTO resDTO = invoiceSO.searchAP59(reqDTO);
			if(resDTO.isSuccessful()) {
				return resDTO.getResultBean();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

	/**
	 * @return the flInvoiceDataModel
	 */
	public IPagingDataModel<FlInvoice> getFlInvoiceDataModel() {
		return flInvoiceDataModel;
	}

	/**
	 * @param flInvoiceDataModel the flInvoiceDataModel to set
	 */
	public void setFlInvoiceDataModel(IPagingDataModel<FlInvoice> flInvoiceDataModel) {
		this.flInvoiceDataModel = flInvoiceDataModel;
	}
	
	public void searchReset(){
		this.getManagedBean().setTitle(null);
		this.getManagedBean().setContractNo(null);
		this.getManagedBean().setLoaNo(null);
	}
	public String getPortletTitle() {
		String portletTitle = "Invoice";
		if(getManagedBean().isModificationCancel()) {
			portletTitle = "Fulfilment Document Maintenance";
		}
		return portletTitle;
	}
	
	public String navigateToPage(){
		renewSesionObject();
		String navgPage="/xhtml/fl/ap59/ap59BasePage.xhtml?faces-redirect=true";
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
		String invoiceId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("invoiceId");
		
		IInvoiceSO invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
		IStatusSO statusSO = (IStatusSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
		reqDTO.setInvoiceId(new Long(invoiceId));
		FulfilmentResDTO resDTO = invoiceSO.retrieveInvoiceByInvoiceId(reqDTO);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setDocType(FulfilmentConst.DOC_TYPE_AP59);
		statusReqDTO.setLanguageCode(CommonConstants.DEFAULT_LANGUAGE_CODE);
		statusReqDTO.setModuleCode(FulfilmentConst.FULFILMENT);
		StatusResDTO statusResDTO = statusSO.retrieveStatusDescList(statusReqDTO);
		InvoiceBackingBean invBB = new InvoiceBackingBean();
		invBB.getManagedBean().setInvoice(resDTO.getInvoice());
		BigDecimal sumOrderedAmt = new BigDecimal(0);
		for(FlFulfilmentItem item : resDTO.getInvoice().getFlRequest().getFlFulfilmentItems()){
			for(FlFulfilmentItemAddr itemAddr : item.getFlFulfilmentItemAddr()){
				sumOrderedAmt = sumOrderedAmt.add(itemAddr.getOrderedAmt());
			}
		}
		invBB.getManagedBean().setSumOrderedAmt(sumOrderedAmt);
		BigDecimal serviceFee = null;
		if(resDTO.getInvoice().getFlFulfilmentOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
			serviceFee = new BigDecimal(FulfilmentConst.SERVICE_FEE_ZERO_POINT_EIGHT);
		} else {
			serviceFee = new BigDecimal(resDTO.getInvoice().getFlRequest().getCtContract().getServiceFee());
		}
		
		getManagedBean().setServiceCharge(Double.toString(serviceFee.doubleValue()));
		for(PmStatusDesc statusDesc : statusResDTO.getStatusDescList()){
			if(!statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.AP59_PENDING_AP59_DECISION)){
				if(invBB.getManagedBean().getDecisionList() == null	){
					invBB.getManagedBean().setDecisionList(new ArrayList<PmStatusDesc>());
				}
				invBB.getManagedBean().getDecisionList().add(statusDesc);
			}
		}
		 SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
		 String expDate=null;
		 String effDate=null;
		 expDate = DATE_FORMAT.format(invBB.getManagedBean().getInvoice().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getExpDate());
		 effDate = DATE_FORMAT.format(invBB.getManagedBean().getInvoice().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getEffDate());
		 invBB.getManagedBean().setExpDate(expDate);
		 invBB.getManagedBean().setEffDate(effDate);
		 FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
		return navgPage;
	}
	
	public String submitAp59(){
		IInvoiceSO invoiceSO = null;
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		String action = BpmOutcomeConst.BPM_SUBMIT;
		try {
			FlInvoice invoice = getManagedBean().getInvoice();
			invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
			
			reqDTO.setStatusId(getManagedBean().getStatusId());
			reqDTO.setInvoice(invoice);
			reqDTO.setOutcome(action);
			reqDTO.setUserRole(getUserRole());
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			if(flBB.getManagedBean().getRemarkList().size() >0){
				reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			}
			resDTO = invoiceSO.updateInvoiceWithAP59(reqDTO);
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700026;
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700004;
				} else if(resDTO.getInvoice()!=null && resDTO.getInvoice().getAp59Status()!=null && 
						resDTO.getInvoice().getAp59Status().getStatusId().longValue()==FulfilmentStatusConst.AP59_PENDING_AP59_DECISION) {
					messageId = MessageConst.INFO_700004;
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, 
						null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_AP59), resDTO.getInvoiceNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, 
								new FacesScopes().getUserLocale())));
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && resDTO.getInvoice()!=null && resDTO.getInvoice().getAp59Status()!=null && 
					resDTO.getInvoice().getAp59Status().getStatusId().longValue()==FulfilmentStatusConst.AP59_PENDING_AP59_DECISION) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_745001, null, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				}
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					getManagedBean().setInvoice(resDTO.getInvoice());
					flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
					if(getManagedBean().getInvoice().getFlFulfilmentOrder().isFactored()){
						getFactoringBranchDtl(); 
					}
				}
			}
		}finally {
			invoiceSO = null;
		}
		return null;
	}
	
}
