/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.jaxb.fl.invoice.FLInvoiceDataType;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.order.FlStopInstrItem;
import my.ep.persistence.entity.fl.payment.FlInvoice;
import my.ep.persistence.entity.fl.payment.FlInvoiceItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
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
			
			reqDTO.setInvoice(invoice);
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			System.out.println("getManagedBean().isDan(): "+getManagedBean().isDan());
			reqDTO.setDanRequired(getManagedBean().isDan());// for current testing DAN purpose in SO
			reqDTO.setUpdateDan(getManagedBean().isUpdateDan());
			reqDTO.setToken(token);
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setUserRole(getUserRole());
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
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700004;
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, 
						null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_INVOICE), resDTO.getInvoiceNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, 
								new FacesScopes().getUserLocale())));
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
		else 
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
			String[] outcome = new String[res.getAction().length + 1];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			int i = 1;
			for (String action : res.getAction()) {
				outcome[i++] = action;
			}

			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			FLInvoiceDataType paymentObj = (FLInvoiceDataType)obj;
			IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
			
			reqDTO.setFlOrderId(paymentObj.getFlOrderId());
			resDTO = invoiceSO.retrieveInvoiceByFlOrderId(reqDTO);//if save button are exist
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			
			if(resDTO.getInvoice() == null){
				getManagedBean().setInvoice(new FlInvoice());
				getManagedBean().getInvoice().setFlInvoiceItem(new ArrayList<FlInvoiceItem>());
				reqDTO.setFlRequestId(paymentObj.getFlRequestId());
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
				PmStatus pmStatus = new PmStatus();
				pmStatus.setStatusId(FulfilmentStatusConst.IN_PENDING_INVOICE);
				getManagedBean().getInvoice().setStatus(pmStatus);
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
			getManagedBean().getInvoice().getFlInvoiceItem().add(invoiceItem);
		}
	}
	
	public void retrieveInvTotalAmount(FlInvoiceItem flInvoiceItem){
	   BigDecimal totalInvoiceAmt = new BigDecimal(0);
	   for(TreeNode childNode : getManagedBean().getChargeLineItemRoot().getChildren()){
		   FlInvoiceItem data= (FlInvoiceItem) childNode.getData();
		   BigDecimal invoiceTotalAmount=new BigDecimal(0);
		   invoiceTotalAmount=invoiceTotalAmount.add(data.getInvAmt());
		   
		   if(flInvoiceItem.getFlFulfilmentItem().getCmItem().getItemId().longValue() == data.getFlFulfilmentItem().getCmItem().getItemId().longValue()){
			   BigDecimal invoiceAmount=new BigDecimal(0);
			   for(TreeNode subNode:childNode.getChildren()){
				   FlInvoiceItem subData=(FlInvoiceItem)subNode.getData();
				   invoiceAmount=invoiceAmount.add(subData.getInvAmt());
			   }
			   data.setInvAmt(invoiceAmount);
			   totalInvoiceAmt = totalInvoiceAmt.add(invoiceAmount);			  
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
		getManagedBean().setServiceCharge("0.4");
		BigDecimal serviceFee = new BigDecimal(0.4);
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
}
