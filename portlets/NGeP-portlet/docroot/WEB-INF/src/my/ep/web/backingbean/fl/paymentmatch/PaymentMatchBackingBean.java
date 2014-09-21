/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.paymentmatch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.payment.IPaymentMatchSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.confgeneral.IConfigGeneralSO;
import my.ep.biz.service.pm.common.glaccount.IGlAccountSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
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
import my.ep.dto.pm.common.confgeneral.ConfigGeneralResDTO;
import my.ep.dto.pm.common.glaccount.GlAccountReqDTO;
import my.ep.dto.pm.common.glaccount.GlAccountResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.jaxb.fl.payment.instruction.FLPaymentInstructionDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.persistence.entity.fl.adjust.FlAdjustmentItem;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.payment.FlInvoice;
import my.ep.persistence.entity.fl.payment.FlInvoiceItem;
import my.ep.persistence.entity.fl.payment.FlPaymentAdvice;
import my.ep.persistence.entity.fl.payment.FlPaymentDeduction;
import my.ep.persistence.entity.fl.payment.FlPaymentItem;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmProject;
import my.ep.persistence.entity.pm.common.PmSetia;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.AddressType;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.paymentmatch.PaymentMatchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.liferay.portal.util.PortalUtil;

/**
 * Payment Match Backing Bean
 * @author KK LIM
 * @since 30/10/2013
 *
 */
@ManagedBean(name = "paymentMatchBackingBean")
@RequestScoped
public class PaymentMatchBackingBean extends BaseBackingBean<PaymentMatchManagedBean>{

	private static Logger log = Logger.getLogger(PaymentMatchBackingBean.class);
	private String className="@(.myPmClassName)";
	
	private static Map<Long, PmVotFund> votFundCodeMap = new HashMap<Long, PmVotFund>();
	private static Map<Long, PmGlAccount> glAccountCodeMap = new HashMap<Long, PmGlAccount>();
	private static Map<Long, PmPrgActivity> progmActivityCodeMap = new HashMap<Long, PmPrgActivity>();
	private static Map<Long, PmProject> projectCodeMap = new HashMap<Long, PmProject>();
	private static Map<Long, PmSetia> setiaCodeMap = new HashMap<Long, PmSetia>();
	private static Map<Long, PmSubSetia> subSetiaCodeMap = new HashMap<Long, PmSubSetia>();
	
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
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	@PostConstruct
	public void init() {
		if (getManagedBean().getFulfilmentRequest() == null) {
			getManagedBean().setFulfilmentRequest(new FlFulfilmentRequest());
			getManagedBean().getFulfilmentRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
		}
	}
	
	public String submitAction(String action){
		log.debug("PaymentMatchBackingBean.submitAction()");
		IPaymentMatchSO paymentSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlPaymentAdvice paymentAdvice = getManagedBean().getPaymentAdvice();
			  
			paymentSO = (IPaymentMatchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IPaymentMatchSO.class);
			reqDTO.setPaymentAdvice(paymentAdvice);
			
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setToken(token);
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			FulfilmentResDTO resDTO = paymentSO.createPaymentMatch(reqDTO);
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700026;
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700004;
				}				
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, 
						null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PAYMENT_ADVICE), resDTO.getPaymentAdviceNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, 
								new FacesScopes().getUserLocale())));
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					reqDTO.setFlOrderId(resDTO.getFulfilmentOrderId());
					resDTO = paymentSO.retrievePaymentAdviseByOrderId(reqDTO);
					
					getManagedBean().setSelectedTabIndex(0);
					getManagedBean().setPaymentAdvice(resDTO.getPaymentAdvice());
					for(FlPaymentItem flPaymentItem : getManagedBean().getPaymentAdvice().getFlPaymentItems()) {
						if(flPaymentItem.getFlAdjustmentItem()==null) {
							flPaymentItem.setFlAdjustmentItem(new FlAdjustmentItem());
							flPaymentItem.getFlAdjustmentItem().setAdjustedAmt(new BigDecimal(0));
						}
					}					
					setAdjustmentDocNo(resDTO.getPaymentAdvice().getFlInvoice());
					setStopInstrNo(resDTO.getPaymentAdvice().getFulfilmentOrder());					
					flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
				}
			}
		}finally {
			paymentSO = null;
		}
		return null;
	}

	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		System.out.println("taskId:" + taskId);
		if(taskId != null){
			renewSesionObject();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			String[] outcome = new String[res.getAction().length + 1];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			int i = 1;
			for (String action : res.getAction()) {
				outcome[i++] = action;
			}

			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			FLPaymentInstructionDataType paymentObj = (FLPaymentInstructionDataType)obj;
			flReqDTO.setFlOrderId(paymentObj.getFlOrderId());
			IInvoiceSO invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IInvoiceSO.class);
			IPaymentMatchSO paymentMatchSO = (IPaymentMatchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IPaymentMatchSO.class);
			
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			FlPaymentAdvice paymentAdvice = null;
			resDTO = paymentMatchSO.retrievePaymentAdviseByOrderId(flReqDTO);
			if(resDTO.getPaymentAdvice()==null) {
				flReqDTO.setDocType(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
				resDTO = invoiceSO.retrieveInvoiceByFlOrderId(flReqDTO);
				paymentAdvice = initializePaymentAdvice(resDTO);
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			} else {
				paymentAdvice = resDTO.getPaymentAdvice();
				for(FlPaymentItem flPaymentItem : paymentAdvice.getFlPaymentItems()) {
					if(flPaymentItem.getFlAdjustmentItem()==null) {
						flPaymentItem.setFlAdjustmentItem(new FlAdjustmentItem());
						flPaymentItem.getFlAdjustmentItem().setAdjustedAmt(new BigDecimal(0));
					}
				}
				setAdjustmentDocNo(paymentAdvice.getFlInvoice());
				setStopInstrNo(paymentAdvice.getFulfilmentOrder());
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
			}

			getManagedBean().setPaymentAdvice(paymentAdvice);
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(outcome);
			
			// For PO No Deduction Panel 
			getManagedBean().setDocType(paymentAdvice.getFlRequest().getDocType());
			getManagedBean().setFulfilmentRequest(paymentAdvice.getFlRequest());
			
			if(getManagedBean().getPaymentAdvice().getFulfilmentOrder().isFactored()){
				getFactoringBranchDtl(); 
			}
			
			getManagedBean().setSelectedTabIndex(0);
			if(getManagedBean().getPaymentAdvice().getStatus()==null) {
				IStatusSO statusSO = (IStatusSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
				StatusReqDTO statusReqDTO = new StatusReqDTO();
				statusReqDTO.setStatusId(FulfilmentStatusConst.PA_PENDING_PAYMENT_MATCH);
				getManagedBean().getPaymentAdvice().setStatus(statusSO.retrieveStatusByStatusId(statusReqDTO).getPmStatus());
			}	
			
			if(getManagedBean().getPaymentAdvice().getFulfilmentOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
				new FulfilmentBackingBean(ParameterConst.DOCTYPE_PAYMENT_ADVICE, getManagedBean().getPaymentAdvice().getFlRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
			} else {
				new FulfilmentBackingBean(ParameterConst.DOCTYPE_PAYMENT_ADVICE, getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getContractNo());
			}	
			populateChargeLineData();
			if(null == getManagedBean().getPaymentAdvice().getDescription()){
				getManagedBean().getPaymentAdvice().setDescription(getManagedBean().getPaymentAdvice().getFlRequest().getTitle());
			}
		}
	}
	
	private FlPaymentAdvice initializePaymentAdvice(FulfilmentResDTO resDTO) {
		IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IParameterSO.class);
		IConfigGeneralSO configGeneralSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IConfigGeneralSO.class);
		
		FlPaymentAdvice paymentAdvice = new FlPaymentAdvice();
		paymentAdvice.setFlInvoice(resDTO.getInvoice());
		paymentAdvice.setFeeAmt(resDTO.getInvoice().getFeeAmt());
		paymentAdvice.setFlRequest(resDTO.getInvoice().getFlRequest());
		paymentAdvice.setFulfilmentOrder(resDTO.getInvoice().getFlFulfilmentOrder());
		
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		parameterReqDTO.setParameterType(ParameterTypeConst.DEDUCTION_TYPE);
		parameterReqDTO.setParameterCode(ParameterConst.TRANSACTION_FEE);
		ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
		
		FlPaymentDeduction paymentDeduction = new FlPaymentDeduction();
		paymentDeduction.setFlPaymentAdvice(paymentAdvice);
		paymentDeduction.setDeductionType(parameterResDTO.getPmParameter());
		paymentDeduction.setDeductionAmt(paymentAdvice.getFeeAmt());
		paymentDeduction.setDeductionDesc(" ");
		
		ConfigGeneralResDTO configGeneralResDTO = configGeneralSO.getConfigGeneral(null);
		paymentDeduction.setPmVotFund(configGeneralResDTO.getPmConfigGeneral().getPmVotFund());
		paymentDeduction.setPmGlAccount(configGeneralResDTO.getPmConfigGeneral().getPmGlAccount());
		
		paymentAdvice.setFlPaymentDeduction(new ArrayList<FlPaymentDeduction>());
		paymentAdvice.getFlPaymentDeduction().add(paymentDeduction);
		
		setAdjustmentDocNo(resDTO.getInvoice());
		setStopInstrNo(resDTO.getInvoice().getFlFulfilmentOrder());
		
		FlPaymentItem flPaymentItem = null;
		paymentAdvice.setFlPaymentItems(new ArrayList<FlPaymentItem>());
		for(FlInvoiceItem invoiceItem : resDTO.getInvoice().getFlInvoiceItem()) {
			flPaymentItem = new FlPaymentItem();
			flPaymentItem.setFlPaymentAdvice(paymentAdvice);
			flPaymentItem.setFlItem(invoiceItem.getFlFulfilmentItem());
			flPaymentItem.setFlInvoiceItem(invoiceItem);
			if(getManagedBean().getAdjustmentDocNo()!=null) {
				for(FlAdjustment flAdjustment : paymentAdvice.getFlInvoice().getFlAdjustments()) {
					if((flAdjustment.getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE) || flAdjustment.getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE)) && 
							(flAdjustment.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE)) {
						for(FlAdjustmentItem flAdjustmentItem : flAdjustment.getFlAdjustmentItem()) {
							if(flAdjustmentItem.getFlInvoiceItem()==invoiceItem) {
								flPaymentItem.setFlAdjustmentItem(flAdjustmentItem);
								break;
							}
						}
					}
				}
			}
			if(flPaymentItem.getFlAdjustmentItem()==null) {
				flPaymentItem.setFlAdjustmentItem(new FlAdjustmentItem());
				flPaymentItem.getFlAdjustmentItem().setAdjustedAmt(new BigDecimal(0));
			}
			paymentAdvice.getFlPaymentItems().add(flPaymentItem);
		}

		return paymentAdvice;
	}
	
	private void setAdjustmentDocNo(FlInvoice flInvoice) {
		String adjustmentDocNo = "";
		for(FlAdjustment flAdjustment : flInvoice.getFlAdjustments()) {
			if(flAdjustment.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
				if(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE.equalsIgnoreCase(flAdjustment.getDocType())){
					getManagedBean().setAdjustmentDanDocNo(flAdjustment.getDocNo());
				}else if(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE.equalsIgnoreCase(flAdjustment.getDocType())){
					getManagedBean().setAdjustmentCanDocNo(flAdjustment.getDocNo());
				}
				if(flInvoice.getFlAdjustments().get(0)==flAdjustment) {
					adjustmentDocNo = flAdjustment.getDocNo();
				} else {
					adjustmentDocNo = adjustmentDocNo + "," + flAdjustment.getDocNo();
				}
			}
		}
		getManagedBean().setAdjustmentDocNo(adjustmentDocNo);
	}
	
	private void setStopInstrNo(FlFulfilmentOrder flFulfilmentOrder) {
		IFulfilmentOrderSO fulfilmentOrderSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
		
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setFulfilmentOrderId(flFulfilmentOrder.getFulfilmentOrderId());
		FulfilmentResDTO resDTO = fulfilmentOrderSO.retrieveStopInstructionByOrder(reqDTO);
		if(resDTO.getFlStopInstr()!=null) {
			getManagedBean().setStopInstrNo(resDTO.getFlStopInstr().getStopInstrNo());
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
		
		if(getManagedBean().getPaymentAdvice().getFulfilmentOrder().getPmFinancialBranch()!=null && getManagedBean().getPaymentAdvice().getFulfilmentOrder().getPmFinancialBranch().getFinancialBranchId()!=null) {
			getManagedBean().setBranchName(getManagedBean().getPaymentAdvice().getFulfilmentOrder().getPmFinancialBranch().getBranchName());
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialBranchId(getManagedBean().getPaymentAdvice().getFulfilmentOrder().getPmFinancialBranch().getFinancialBranchId());
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
		} else { //reset back to HQ info
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialOrgId(getManagedBean().getPaymentAdvice().getFulfilmentOrder().getPmFinancialOrg().getFinancialOrgId());
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
		}
	}
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==1){
			//populateChargeLineData();	
			setClassName("@(.pmChargeLinePnlClass)");
		}
		if(selectedTabIndex==3){
			calPmSummary();
			setClassName("@(.pmSummaryPnlClass)");
		}
	}
	
	public void populateChargeLineData(){
		getManagedBean().setChargeLineItemRoot(new DefaultTreeNode("root", null));
		 
		//Map<Long, List<FlPaymentItem>> paymentItemsMap = new HashMap<Long, List<FlPaymentItem>>();
		Map<String, List<FlPaymentItem>> paymentItemsMap = new TreeMap<String, List<FlPaymentItem>>();
		List<FlPaymentItem> flPaymentItemList = null;
		for (FlPaymentItem flPaymentItem : getManagedBean().getPaymentAdvice().getFlPaymentItems()) {
			if (paymentItemsMap.containsKey(flPaymentItem.getFlItem().getCmItem().getExtensionCode())) {
				flPaymentItemList = paymentItemsMap.get(flPaymentItem.getFlItem().getCmItem().getExtensionCode());
			} else {
				flPaymentItemList = new ArrayList<FlPaymentItem>();
			}
			flPaymentItemList.add(flPaymentItem);
			paymentItemsMap.put(flPaymentItem.getFlItem().getCmItem().getExtensionCode(), flPaymentItemList);
		}
		
		BigDecimal grandTotalOrderAmt = new BigDecimal(0);
		BigDecimal grandTotalReceivedAmt = new BigDecimal(0);
		BigDecimal grandTotalInvAmt = new BigDecimal(0);
		for(Entry<String, List<FlPaymentItem>> itemEntry : paymentItemsMap.entrySet()){
		//for(Entry<Long, List<FlPaymentItem>> itemEntry : paymentItemsMap.entrySet()){
			List<FlPaymentItem> paymentItems = itemEntry.getValue();
			FlPaymentItem flPaymentItem = (FlPaymentItem) paymentItems.get(0).cloneEntity(); // parent entity, no need stored in DB.
			flPaymentItem.setFlInvoiceItem((FlInvoiceItem) flPaymentItem.getFlInvoiceItem().cloneEntity());
			if(flPaymentItem.getFlAdjustmentItem()==null) {
				flPaymentItem.setFlAdjustmentItem(new FlAdjustmentItem());
			} else {
				flPaymentItem.setFlAdjustmentItem((FlAdjustmentItem) flPaymentItem.getFlAdjustmentItem().cloneEntity());
			}
			flPaymentItem.setParent(true);
			TreeNode childItemNode = new DefaultTreeNode(flPaymentItem.getFlItem().getCmItem().getExtensionCode(), flPaymentItem, getManagedBean().getChargeLineItemRoot());
			
			BigDecimal totalOrderAmt = new BigDecimal(0);
			BigDecimal totalReceiveAmt = new BigDecimal(0);
			BigDecimal totalRecallAmt = new BigDecimal(0);
			BigDecimal totalInvAmt = new BigDecimal(0);
			BigDecimal totalDanAmt = new BigDecimal(0);
			for(FlPaymentItem chargeLineItem : paymentItems) {
				new DefaultTreeNode(chargeLineItem.getFlItem().getCmItem().getExtensionCode(),chargeLineItem,childItemNode);
				totalOrderAmt=totalOrderAmt.add(chargeLineItem.getFlInvoiceItem().getOrderedAmt());
				totalReceiveAmt=totalReceiveAmt.add(chargeLineItem.getFlInvoiceItem().getReceivedAmt());
				totalRecallAmt=totalRecallAmt.add(chargeLineItem.getFlInvoiceItem().getRecalledAmt());
				totalInvAmt=totalInvAmt.add(chargeLineItem.getFlInvoiceItem().getInvAmt());
				if(chargeLineItem.getFlAdjustmentItem()!=null) {
					totalDanAmt=totalDanAmt.add(chargeLineItem.getFlAdjustmentItem().getAdjustedAmt());
				}
			}			

			flPaymentItem.getFlInvoiceItem().setOrderedAmt(totalOrderAmt);
			flPaymentItem.getFlInvoiceItem().setReceivedAmt(totalReceiveAmt);
			flPaymentItem.getFlInvoiceItem().setRecalledAmt(totalRecallAmt);
			flPaymentItem.getFlInvoiceItem().setInvAmt(totalInvAmt);
			flPaymentItem.getFlAdjustmentItem().setAdjustedAmt(totalDanAmt);
			
			grandTotalOrderAmt = grandTotalOrderAmt.add(totalOrderAmt);
			grandTotalReceivedAmt = grandTotalReceivedAmt.add(totalReceiveAmt);
			grandTotalInvAmt = grandTotalInvAmt.add(totalInvAmt);
			
			if(getManagedBean().getSelectedNode()==null) {
				getManagedBean().setSelectedNode(childItemNode);
				childItemNode.setSelected(true);
				onNodeSelect();
			}
		}
		getManagedBean().setOrderedAmtGrandTotal(grandTotalOrderAmt);
		getManagedBean().setReceivedAmtGrandTotal(grandTotalReceivedAmt);
		getManagedBean().setInvAmtGrandTotal(grandTotalInvAmt);
	}
	
	public void calPmSummary(){
		BigDecimal totalDeductionAmt = new BigDecimal(0);
		for(FlPaymentDeduction deduction : getManagedBean().getPaymentAdvice().getFlPaymentDeduction()) {
			if(deduction.getDeductionType().getParameterCode()!=null && 
				!deduction.getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_TRANSACTION_FEE)) {
				totalDeductionAmt = totalDeductionAmt.add(deduction.getDeductionAmt());
			}
		}
		
		BigDecimal calPaymentAmt = new BigDecimal(0.0);
		getManagedBean().getPaymentAdvice().getFlInvoice().setInvoiceAmtNew(getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceAmtNew());
		getManagedBean().getPaymentAdvice().setFeeAmt(getManagedBean().getPaymentAdvice().getFlInvoice().getFeeAmt());
		getManagedBean().setDeductionAmt(totalDeductionAmt);
		calPaymentAmt=calPaymentAmt.add(getManagedBean().getPaymentAdvice().getFlInvoice().getFeeAmt().add(totalDeductionAmt));
		BigDecimal deductPaymentAmt = new BigDecimal(0.0);
		deductPaymentAmt=getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceAmtNew().subtract(calPaymentAmt);
		getManagedBean().getPaymentAdvice().setPaymentAmt(deductPaymentAmt);
	} 
	
	public void onNodeSelect() {
		TreeNode selectedNode=getManagedBean().getSelectedNode();
		onNodeUnSelect();
		if(null!=selectedNode){
			for(TreeNode children:selectedNode.getChildren()){
				FlFulfilmentItem childData=((FlPaymentItem)children.getData()).getFlItem();
				if(null !=childData.getPmVotFund()){
					String votFundCode = childData.getPmVotFund().getVotFundCode();
					getManagedBean().setVotFundCode(votFundCode);
				}
				if(childData.getAssetInd()!=null) {
					getManagedBean().setAssetIndCode(childData.getAssetInd().getParameterCode());
				}
				if(childData.getPmGlAccount()!=null) {
					String glAccountCode = childData.getPmGlAccount().getGlAccCode();
					getManagedBean().setGlAccountCode(glAccountCode);
				}
				if(childData.getPmPrgActivity()!=null) {
					String progActvCode = childData.getPmPrgActivity().getPrgActivityCode();
					getManagedBean().setPrgActivityCode(progActvCode);
				}
				if(childData.getPmSubSetia()!=null) {
					String subSetiaCode = childData.getPmSubSetia().getSubSetiaCode();
					getManagedBean().setSubSetiaCode((subSetiaCode));
					
					if(childData.getPmSubSetia().getPmSetia()!=null) {
						getManagedBean().setSetiaCode(childData.getPmSubSetia().getPmSetia().getSetiaCode());
						
						if(childData.getPmSubSetia().getPmSetia().getPmProject()!=null) {
							getManagedBean().setProjectCode(childData.getPmSubSetia().getPmSetia().getPmProject().getProjectCode());
						}
					}
					getManagedBean().setFinancingTypeCode(childData.getPmSubSetia().getFinancingType().getParameterCode());
							
				}
			}
		}
	}
	
	public void onNodeUnSelect() {
		getManagedBean().setVotFundCode("");
		getManagedBean().setAssetIndCode("");
		getManagedBean().setGlAccountCode("");
		getManagedBean().setPrgActivityCode("");
		getManagedBean().setProjectCode("");
		getManagedBean().setSetiaCode("");
		getManagedBean().setSubSetiaCode("");
		getManagedBean().setFinancingTypeCode("");		
	}
	
	/**
	 * @return the votFundCodeMap
	 */
	public static Map<Long, PmVotFund> getVotFundCodeMap() {
		return votFundCodeMap;
	}
	/**
	 * @param votFundCodeMap the votFundCodeMap to set
	 */
	public static void setVotFundCodeMap(Map<Long, PmVotFund> votFundCodeMap) {
		PaymentMatchBackingBean.votFundCodeMap = votFundCodeMap;
	}
	/**
	 * @return the glAccountCodeMap
	 */
	public static Map<Long, PmGlAccount> getGlAccountCodeMap() {
		return glAccountCodeMap;
	}
	/**
	 * @param glAccountCodeMap the glAccountCodeMap to set
	 */
	public static void setGlAccountCodeMap(Map<Long, PmGlAccount> glAccountCodeMap) {
		PaymentMatchBackingBean.glAccountCodeMap = glAccountCodeMap;
	}
	/**
	 * @return the progmActivityCodeMap
	 */
	public static Map<Long, PmPrgActivity> getProgmActivityCodeMap() {
		return progmActivityCodeMap;
	}
	/**
	 * @param progmActivityCodeMap the progmActivityCodeMap to set
	 */
	public static void setProgmActivityCodeMap(
			Map<Long, PmPrgActivity> progmActivityCodeMap) {
		PaymentMatchBackingBean.progmActivityCodeMap = progmActivityCodeMap;
	}
	/**
	 * @return the projectCodeMap
	 */
	public static Map<Long, PmProject> getProjectCodeMap() {
		return projectCodeMap;
	}
	/**
	 * @param projectCodeMap the projectCodeMap to set
	 */
	public static void setProjectCodeMap(Map<Long, PmProject> projectCodeMap) {
		PaymentMatchBackingBean.projectCodeMap = projectCodeMap;
	}
	/**
	 * @return the setiaCodeMap
	 */
	public static Map<Long, PmSetia> getSetiaCodeMap() {
		return setiaCodeMap;
	}
	/**
	 * @param setiaCodeMap the setiaCodeMap to set
	 */
	public static void setSetiaCodeMap(Map<Long, PmSetia> setiaCodeMap) {
		PaymentMatchBackingBean.setiaCodeMap = setiaCodeMap;
	}
	/**
	 * @return the subSetiaCodeMap
	 */
	public static Map<Long, PmSubSetia> getSubSetiaCodeMap() {
		return subSetiaCodeMap;
	}
	/**
	 * @param subSetiaCodeMap the subSetiaCodeMap to set
	 */
	public static void setSubSetiaCodeMap(Map<Long, PmSubSetia> subSetiaCodeMap) {
		PaymentMatchBackingBean.subSetiaCodeMap = subSetiaCodeMap;
	}
	public void addDeductionRow(){
		FlPaymentDeduction paymentDeduction = new FlPaymentDeduction();
		paymentDeduction.setFlPaymentAdvice(getManagedBean().getPaymentAdvice());
		paymentDeduction.setDeductionType(new PmParameter());
		paymentDeduction.setDeductionDesc("");
		paymentDeduction.setPmVotFund(new PmVotFund());
		paymentDeduction.setPmGlAccount(new PmGlAccount());
		getManagedBean().getPaymentAdvice().getFlPaymentDeduction().add(paymentDeduction);
		getGlAccountList(getManagedBean().getPaymentAdvice().getFlRequest().getIssuedOrgProfile());
		getManagedBean().setVotFundList(getManagedBean().getPaymentAdvice().getFlRequest().getIssuedOrgProfile().getPmPtjGroup().getPmVotFunds());
	}
	
	public void retrieveClauses(FlPaymentDeduction deduction){
		if(deduction.getDeductionType().getParameterCode() != null){
			for(CtDeduction ctDeduction : getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getLatestContractVer().getCtDeductions()){
				if(ctDeduction.getDeductionType().getParameterCode().equals(deduction.getDeductionType().getParameterCode())){
					getManagedBean().setCtDeductionClauseList(ctDeduction.getCtDeductionClauses());
				}
			}
		}
	}
	
	private void getGlAccountList(PmOrgProfile pmOrgProfile){
		IGlAccountSO glAccountSO = (IGlAccountSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IGlAccountSO.class);
		
		GlAccountReqDTO glAccountReqDTO = new GlAccountReqDTO();
		glAccountReqDTO.setPtjGroupId(pmOrgProfile.getPmPtjGroup().getPtjGroupId());
		GlAccountResDTO glAccountResDTO = glAccountSO.retrieveGlAccountList(glAccountReqDTO);
		getManagedBean().setGlAccountList(glAccountResDTO.getGlAccountList());
	}
	
	public String retrieveMofNo(SmSupplier smSupplier) {
		IMofAccountSO mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofAccountSO.class);
		MofAccountReqDTO reqDTO = new MofAccountReqDTO();
		reqDTO.setSupplierId(smSupplier.getSupplierId());
		MofAccountResDTO resDTO = mofAccountSO.retrieveMofNoBySupplierId(reqDTO);
		if(resDTO.isSuccessful() && resDTO.getMofAccount()!=null) {
			return resDTO.getMofAccount().getMofNo();
		}
		return null;
	}
	
}
