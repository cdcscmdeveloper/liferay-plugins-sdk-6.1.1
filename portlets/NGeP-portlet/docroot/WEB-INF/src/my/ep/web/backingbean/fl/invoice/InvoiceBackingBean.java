/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.invoice.FLInvoiceDataType;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.order.FlStopInstrItem;
import my.ep.persistence.entity.fl.payment.FlInvoice;
import my.ep.persistence.entity.fl.payment.FlInvoiceItem;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
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
@RequestScoped
public class InvoiceBackingBean extends BaseBackingBean<InvoiceManagedBean> {
	private static Logger log = Logger.getLogger(InvoiceBackingBean.class);
	private String className="@(.myInvClassName)";
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public InvoiceBackingBean(){
		if (getManagedBean().getFulfilmentRequest() == null) {
			getManagedBean().setFulfilmentRequest(new FlFulfilmentRequest());
			getManagedBean().getFulfilmentRequest().setIssuedOrgProfile(new PmOrgProfile());
			getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().setOrgProfileId(1L);
			getManagedBean().getFulfilmentRequest().setChargeOrgProfile(new PmOrgProfile());
			getManagedBean().getFulfilmentRequest().getChargeOrgProfile().setOrgProfileId(1L);
			//getManagedBean().getFulfilmentRequest().setReceivedOrgProfile(new PmOrgProfile());
			//getManagedBean().getFulfilmentRequest().getReceivedOrgProfile().setOrgProfileId(1L);
			getManagedBean().getFulfilmentRequest().setSmSupplier(new SmSupplier());
			getManagedBean().getFulfilmentRequest().getSmSupplier().setSupplierId(1L);
			getManagedBean().getFulfilmentRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
			getManagedBean().getFulfilmentRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
			getManagedBean().getFulfilmentRequest().setCtContract(new CtContract());
			getManagedBean().getFulfilmentRequest().getCtContract().setContractType(new PmParameter());
			getManagedBean().getFulfilmentRequest().getCtContract().setFulfillmentType(new PmParameter());
		}
		if (getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses() == null) {
			getManagedBean().getFulfilmentRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
			
		}
		if(null==getManagedBean().getInvoice()){
			getManagedBean().setInvoice(new FlInvoice());
		}
		if(null==getManagedBean().getInvoice().getFlFulfilmentOrder()){
			getManagedBean().getInvoice().setFlFulfilmentOrder(new FlFulfilmentOrder());
		}
		if(null==getManagedBean().getInvoice().getFlFulfilmentOrder().getFlFulfilmentRequest()){
			getManagedBean().getInvoice().getFlFulfilmentOrder().setFlFulfilmentRequest(new FlFulfilmentRequest());
		}
		if(null==getManagedBean().getInvoice().getFlFulfilmentOrder().getFlFulfilmentRequest().getFlDeliveryAddresses()){
			getManagedBean().getInvoice().getFlFulfilmentOrder().getFlFulfilmentRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
		}
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
			
			if(null==getManagedBean().getChargeLineItemRoot()){
				getManagedBean().populateChargeLineData();
				calInvSummary();
			}
			TreeNode root= getManagedBean().getChargeLineItemRoot();
			List<FlInvoiceItem> invoiceItemList=new ArrayList<FlInvoiceItem>();
			   for(TreeNode childNode:root.getChildren()){
				   FlInvoiceItem invoiceItem=new FlInvoiceItem();
				   
				   BigDecimal orderedQty=new BigDecimal(0);
				   BigDecimal receivedQty=new BigDecimal(0);
				   BigDecimal recallQty=new BigDecimal(0);
				   BigDecimal orderAmount=new BigDecimal(0);
				   BigDecimal receivedAmount=new BigDecimal(0);
				   BigDecimal recallAmount=new BigDecimal(0);
				   BigDecimal invoiceAmount=new BigDecimal(0);
				   FlFulfilmentItem flItem=null;
				   for(TreeNode subNode:childNode.getChildren()){
					   FlFulfilmentItemAddr subData=(FlFulfilmentItemAddr)subNode.getData();
					   flItem=subData.getFlFulfilmentItem();
					   orderedQty= orderedQty.add(subData.getChargeOrderedQty());
					   receivedQty=receivedQty.add(subData.getChargeReceivedQty());
					   if(null==subData.getChargeRecalledQty()){
						   //recallQty=recallQty.add(new BigDecimal(0));
						   subData.setChargeRecalledQty(new BigDecimal(0));
					   }
					   recallQty=recallQty.add(subData.getChargeRecalledQty());
					   orderAmount=orderAmount.add(subData.getChargeOrderedAmt());
					   receivedAmount=receivedAmount.add(subData.getChargeReceivedAmt());
					   if(null==subData.getChargeRecalledAmt()){
						   //recallQty=recallQty.add(new BigDecimal(0));
						   subData.setChargeRecalledAmt(new BigDecimal(0));
					   }
					   recallAmount=recallAmount.add(subData.getChargeRecalledAmt());
					   invoiceAmount=invoiceAmount.add(subData.getChargeInvAmt());
				   }
				   invoiceItem.setOrderedQty(orderedQty);
				   invoiceItem.setReceivedQty(receivedQty);
				   invoiceItem.setRecalledQty(recallQty);
				   invoiceItem.setOrderedAmt(orderAmount);
				   invoiceItem.setReceivedAmt(receivedAmount);
				   invoiceItem.setRecalledAmt(recallAmount);
				   invoiceItem.setInvAmt(invoiceAmount);
				   invoiceItem.setFlFulfilmentItem(flItem);
				   invoiceItem.setFlInvoice(invoice);
				   invoiceItemList.add(invoiceItem);
			   }
			   
			getManagedBean().getInvoice().setFlInvoiceItem(invoiceItemList);
			
			reqDTO.setInvoice(invoice);
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
				action = "";
			}
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			System.out.println("getManagedBean().isDan(): "+getManagedBean().isDan());
			reqDTO.setDanRequired(getManagedBean().isDan());// for current testing DAN purpose in SO
			reqDTO.setUpdateDan(getManagedBean().isUpdateDan());
			reqDTO.setToken(token);
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			if(!flBB.getManagedBean().getRemarkList().isEmpty()){
				reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			}
			
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			if("UPDATE".equalsIgnoreCase(action)){
				reqDTO.setPmUser(getManagedBean().getPmUser());
				if(null != getManagedBean().getInvoice().getSupplierInvoiceRef()){
					getManagedBean().getInvoice().setSupplierInvoiceRef(getManagedBean().getInvoice().getSupplierInvoiceRef());
				}
				reqDTO.setInvoice(getManagedBean().getInvoice());
				resDTO = invoiceSO.invoiceModification(reqDTO);
			}else{
				resDTO = invoiceSO.saveInvoice(reqDTO);
			}
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700026;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, 
						null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_REQUEST), getManagedBean().getInvoice().getInvoiceNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, 
								new FacesScopes().getUserLocale())));
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}	
				else {
					throw new EpException("Failed.");
				}
			}
			
		}finally {
			invoiceSO = null;
		}
		return null;
	}

	public void test(){
		System.out.println("getManagedBean().isDan(): "+getManagedBean().isDan());
		System.out.println("getManagedBean().isUpdateDan(): "+getManagedBean().isUpdateDan());
	}

	public void preRenderView() {
		IInvoiceSO invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		System.out.println("taskId:" + taskId);
		if(taskId != null){

			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO bpmReqDTO = new BpmRequestDTO();

			bpmReqDTO.setCtxToken(ctx);
			bpmReqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(bpmReqDTO);
			Object obj = res.getPojo();

			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			FLInvoiceDataType paymentObj = (FLInvoiceDataType)obj;
			IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
			//resDTO = invoiceSO.retrieveInvoice(flReqDTO);//if save button are exist
			if(getManagedBean().getInvoice() == null){
				getManagedBean().setInvoice(new FlInvoice());
				getManagedBean().getInvoice().setFlInvoiceItem(new ArrayList<FlInvoiceItem>());
			}
			getManagedBean().setChargeLineItemRoot(null);
			reqDTO.setFlRequestId(paymentObj.getFlRequestId());
			resDTO = fulfilmentSO.retrieveFulfilmentRequest(reqDTO);
			getInvoiceTable(resDTO);
			getManagedBean().setDocType(resDTO.getFlFulfilmentRequest().getDocType());
			
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(res.getAction());
			
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());//TODO remove comment after remark is fix
//			getManagedBean().getInvoice().getFlFulfilmentOrder().setFlFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			getManagedBean().getInvoice().setFlFulfilmentOrder(resDTO.getFlFulfilmentRequest().getFlFulfilmentOrder());
			getManagedBean().getInvoice().setFlRequest(resDTO.getFlFulfilmentRequest());
			reqDTO.setFlOrderId(paymentObj.getFlOrderId());
			getManagedBean().getInvoice().setStatus(invoiceSO.retrieveInvoiceStatus(reqDTO).getPmStatus());
			
			//if(!"retrieveAmount".equals(actionMethod)){
				//getManagedBean().populateChargeLineData();
//			}
			
		}
	}
	public void factoringChecked(AjaxBehaviorEvent e) {
		System.out.println("====enter process checked====");
		// String dueDate = (String) ((UIOutput)e.getSource()).getValue();
		String id = (String) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
		getManagedBean().setFactoringCheck(id);

	}
	
	public void getInvoiceTable(FulfilmentResDTO resDTO){
		for(FlFulfilmentItem item : resDTO.getFlFulfilmentRequest().getFlFulfilmentItems()){
			FlInvoiceItem invoiceItem = new FlInvoiceItem();
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
				sumReceivedQty = sumReceivedQty.add(itemAddr.getReceivedQty());
				sumReceivedAmt = sumReceivedAmt.add(itemAddr.getReceivedAmt());
				if(!itemAddr.getStopInstrItems().isEmpty()){
					for(FlStopInstrItem stopInstrItem : itemAddr.getStopInstrItems()){
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
			if(null == getManagedBean().getInvoice().getFlInvoiceItem()){
				getManagedBean().getInvoice().setFlInvoiceItem(new ArrayList<FlInvoiceItem>());
				
			}
			getManagedBean().getInvoice().getFlInvoiceItem().add(invoiceItem);
		}
	}
	
	public void updateNewInvoiceAmt(FlFulfilmentItemAddr invItem){
//		System.out.println("getItemAmtNew: "+invItem.getItemAmtNew());
//		invItem.setItemAmtOri(new BigDecimal(invItem.getItemAmtNew().doubleValue()));
	}
	

	public void retrieveInvTotalAmount(FlFulfilmentItemAddr itemAddr){
	   TreeNode root= getManagedBean().getChargeLineItemRoot();
	   for(TreeNode childNode:root.getChildren()){
		   FlFulfilmentItemAddr data= (FlFulfilmentItemAddr)childNode.getData();
		   BigDecimal invoiceTotalAmount=new BigDecimal(0);
		   data.getChargeInvAmt();
		   invoiceTotalAmount=invoiceTotalAmount.add( data.getChargeInvAmt());
		   
		   //Comarision item address using unique identifier.
		   if(itemAddr.getScRequestItem().getRequestNoteItemId() == data.getScRequestItem().getRequestNoteItemId()){
			   BigDecimal invoiceAmount=new BigDecimal(0);
			   for(TreeNode subNode:childNode.getChildren()){
				   FlFulfilmentItemAddr subData=(FlFulfilmentItemAddr)subNode.getData();
				   invoiceAmount=invoiceAmount.add(subData.getChargeInvAmt());
			   }
			   data.setChargeInvAmt(invoiceAmount);
			   // do for inv Summary
			  
		   }
	   }
	   //canInvSummary();
		
	}
	public void calInvSummary(){
		if(null==getManagedBean().getChargeLineItemRoot()){
			getManagedBean().populateChargeLineData();
		}
		TreeNode root= getManagedBean().getChargeLineItemRoot();
		 BigDecimal invoiceTotalAmount=new BigDecimal(0);
		 BigDecimal orderTotalAmount=new BigDecimal(0);
		   for(TreeNode childNode:root.getChildren()){
			   FlFulfilmentItemAddr data= (FlFulfilmentItemAddr)childNode.getData();
			   data.getChargeInvAmt();
			   invoiceTotalAmount=invoiceTotalAmount.add( data.getChargeInvAmt());
			   orderTotalAmount =orderTotalAmount.add(data.getChargeOrderedAmt());
		   }
		   System.out.println("invoiceTotalAmount::: "+invoiceTotalAmount);
		   getManagedBean().getInvoice().setInvoiceAmtOri(orderTotalAmount);
		   getManagedBean().getInvoice().setInvoiceAmtNew(invoiceTotalAmount);
		   getManagedBean().setServiceCharge("0.4");
		   BigDecimal serviceFee=new BigDecimal(0.4);
		   BigDecimal percentage=new BigDecimal(100);
		   BigDecimal invMulty=new BigDecimal(0.4);
		   BigDecimal feeAmount=new BigDecimal(0.4);
		   invMulty=invoiceTotalAmount.multiply(serviceFee);
		   System.out.println("invMulty::: "+invMulty);
		   feeAmount=invMulty.divide(percentage);
		   System.out.println("feeAmount::: "+feeAmount);
		  
		   getManagedBean().getInvoice().setFeeAmt(feeAmount);
		   getManagedBean().getInvoice().setSupplierOrFactorAmt(invoiceTotalAmount.subtract(feeAmount));
		  // getManagedBean().getInvoice().setInvoiceAmtNew(feeAmount);
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
	/*private String actionMethod="";
	
	*//**
	 * @return the actionMethod
	 *//*
	public String getActionMethod() {
		return actionMethod;
	}

	*//**
	 * @param actionMethod the actionMethod to set
	 *//*
	public void setActionMethod(String actionMethod) {
		this.actionMethod = actionMethod;
	}*/

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
	
	public List<SelectItem> getPopulateBankAccountList() {
		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,ISupplierSearchSO.class);
		List<SelectItem> populateBankAccountList = new ArrayList<SelectItem>();
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		
		
		if (this.getManagedBean().getInvoice() != null) {
				if(this.getManagedBean().getInvoice().getFlRequest().getFlSupplierDtl().getBranchName() != null ){
//					supplierSearchReqDTO.setBranchName(getManagedBean().getInvoice().getFlRequest().getFlSupplierDtl().getBranchName());
//					supplierSearchReqDTO.setSupplierId(getManagedBean().getInvoice().getFlRequest().getSmSupplier().getSupplierId());
//					supplierSearchSO.retrieveSmSupplierBankBySupplierBranchName (supplierSearchReqDTO)
				}
			
			
			if (this.getManagedBean().getInvoice().getFlRequest().getSmSupplier().getSmAppl().getSmSupplierBanks() != null
					&& this.getManagedBean().getInvoice().getFlRequest().getSmSupplier().getSmAppl().getSmSupplierBanks().size() > 0) {
				List<SmSupplierBank> supplierBank = this.getManagedBean().getInvoice().getFlRequest().getSmSupplier().getSmAppl().getSmSupplierBanks();
				for (SmSupplierBank obj : supplierBank) {
					populateBankAccountList.add(new SelectItem(obj.getAccountNo(), obj.getAccountNo()));
					if(!getManagedBean().getSupplierBankAccountMap().containsKey(obj.getAccountNo())) {
						getManagedBean().getSupplierBankAccountMap().put(obj.getAccountNo(), obj);
					}
				}
			}
		}
		return populateBankAccountList;
	}
	
	public void bankInfoActionListener() {
		SmSupplierBank supplierBank = getManagedBean().getSupplierBankAccountMap().get(getManagedBean().getInvoice().getAccountNo());
		
		getManagedBean().getInvoice().setPaymentBank(new PmFinancialOrg());
		
		if(supplierBank!=null) {
			getManagedBean().getInvoice().setPaymentBank(supplierBank.getPmFinancialOrg());
		}
	}
}
