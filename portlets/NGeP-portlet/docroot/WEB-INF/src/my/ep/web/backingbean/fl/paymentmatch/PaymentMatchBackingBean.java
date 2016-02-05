/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.paymentmatch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.liferay.portal.util.PortalUtil;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.fl.adjustment.IAdjustmentSO;
import my.ep.biz.service.fl.frn.IReceivedNoteSO;
import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.payment.IPaymentMatchSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.confgeneral.IConfigGeneralSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.fee.settings.IFeeExemptionSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.confgeneral.ConfigGeneralResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionReqDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.payment.instruction.FLPaymentInstructionDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNoteItem;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.payment.FlInvoice;
import my.ep.persistence.entity.fl.payment.FlInvoiceItem;
import my.ep.persistence.entity.fl.payment.FlInvoiceItemSummary;
import my.ep.persistence.entity.fl.payment.FlPaymentAdvice;
import my.ep.persistence.entity.fl.payment.FlPaymentDeduction;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.GSFType;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.common.CommonBackingBean;
import my.ep.web.backingbean.ct.ContractDetailsBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBaseBackingBean;
import my.ep.web.backingbean.fl.dan.DebitAdjustmentNoteBackingBean;
import my.ep.web.backingbean.fl.deliveryorder.DeliveryOrderBackingBean;
import my.ep.web.backingbean.fl.frn.ReceivedNoteBackingBean;
import my.ep.web.backingbean.fl.invoice.InvoiceBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.backingbean.fl.stopinstr.StopInstrBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.fl.paymentmatch.PaymentMatchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.InvoiceChargeLineVO;
import my.ep.web.vo.TaxInvoiceVO;

// TODO: Auto-generated Javadoc
/**
 * Payment Match Backing Bean.
 *
 * @author KK LIM
 * @since 30/10/2013
 */
@ManagedBean(name = "paymentMatchBackingBean")
@RequestScoped
public class PaymentMatchBackingBean extends FulfilmentBaseBackingBean<PaymentMatchManagedBean>{
	
	private static Logger log = Logger.getLogger(PaymentMatchBackingBean.class);
	private String className="@(.myPmClassName)";
	
	private static Map<Long, PmVotFund> votFundCodeMap = new HashMap<Long, PmVotFund>();
	private static Map<Long, PmGlAccount> glAccountCodeMap = new HashMap<Long, PmGlAccount>();
	private static Map<Long, PmPrgActivity> progmActivityCodeMap = new HashMap<Long, PmPrgActivity>();
	private static Map<Long, PmSubSetia> subSetiaCodeMap = new HashMap<Long, PmSubSetia>();
	private static final String NAV_PO = "/xhtml/fl/purchaseorder/poBasePage.xhtml";
	private static final String NAV_CO = "/xhtml/fl/contractorder/coBasePage.xhtml";
	private static final String NAV_ADJ = "/xhtml/fl/invoice/adjustment/danBasePage.xhtml";
	private static final String NAV_SI = "/xhtml/fl/stopInstruction/siBasePage.xhtml";
	private static final String NAV_INVOICE = "/xhtml/fl/invoice/invBasePage.xhtml";
	private static final String NAV_DELIVERYORDER_PAGE = "/xhtml/fl/deliveryorder/doBasePage.xhtml";
	private static final String NAV_FULFILMENTNOTE_PAGE= "/xhtml/fl//receivednote/frnBasePage.xhtml";
	
	/**
	 * Gets the class name.
	 *
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	
	/**
	 * Sets the class name.
	 *
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	
	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {
		if (getManagedBean().getFulfilmentRequest() == null) {
			getManagedBean().setFulfilmentRequest(new FlFulfilmentRequest());
			getManagedBean().getFulfilmentRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
		}
		
		if(getManagedBean().getFeeVotFund()==null) {
			IConfigGeneralSO configGeneralSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IConfigGeneralSO.class);
			ConfigGeneralResDTO configGeneralResDTO = configGeneralSO.getConfigGeneral(null);
			getManagedBean().setFeeVotFund(configGeneralResDTO.getPmConfigGeneral().getPmVotFund());
		}
	}
	
	/**
	 * save,submit payment match details.
	 * </br>submit - save data into database and BPM process will route to the next respective user.
	 * </br>save - save data into database.
	 * @param action the action
	 * navigate to acknowledge page.
	 */
	public String submitAction(String action){
		log.debug("PaymentMatchBackingBean.submitAction()");
		IPaymentMatchSO paymentSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			//trigger for other non-gpki signing actions
			if(!getManagedBean().isValidated()){
				validatePaymentMatch(action);
			}
			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
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
				
			if(getManagedBean().getPmDigiSign() != null){
				reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			}
			
			reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
			
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
					if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_MATCH)) {
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put(ReportConst.PARAM_DOC_NO, resDTO.getPaymentAdviceNo());
						paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
						CommonBackingBean commonBackingBean = new CommonBackingBean();
						commonBackingBean.getManagedBean().setReportName(ReportConst.FL_NGEP_PAYMENT_MATCH);
						commonBackingBean.getManagedBean().setReportDisplayName(ResourceBundleUtils.getMessage("lbl.fl.payment.match",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
						commonBackingBean.getManagedBean().setParamMap(paramMap);
						return CommonUIConst.ACKNOWLEDGE_REPORT_PAGE;
					}					
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					reqDTO.setFlOrderId(resDTO.getFulfilmentOrderId());
					resDTO = paymentSO.retrievePaymentAdviseByOrderId(reqDTO);
					
					getManagedBean().setSelectedTabIndex(0);
					getManagedBean().setPaymentAdvice(resDTO.getPaymentAdvice());
					setAdjustmentDocNo(resDTO.getPaymentAdvice().getFlInvoice());
					setStopInstrNo(resDTO.getPaymentAdvice().getFulfilmentOrder());					
					flBB.initializeRemarkAndAttachement(resDTO.getPaymentAdvice().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
				}
			}
		}finally {
			paymentSO = null;
		}
		return null;
	}

	/**
	 * Pre render view.
	 */
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		System.out.println("taskId:" + taskId);
		if(taskId != null){
			renewSesionObject();
			IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
//			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
//					.getServiceObject(IBpmSO.class);
//			Object ctx = getFacesScopes().getBPMAuthenticationCtxToken();
//			BpmRequestDTO reqDTO = new BpmRequestDTO();
//			reqDTO.setCtxToken(ctx);
//			reqDTO.setTaskId(taskId);
//			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
//			Object obj = res.getPojo();
//			String[] outcome = new String[res.getAction().length + 1];
//			outcome[0] = BpmOutcomeConst.BPM_SAVE;
//			int i = 1;
//			for (String action : res.getAction()) {
//				outcome[i++] = action;
//			}
			Object obj = super.getBpmTaskDetails(Object.class, new String[]{BpmOutcomeConst.BPM_SAVE});
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			FLPaymentInstructionDataType paymentObj = (FLPaymentInstructionDataType)obj;
			flReqDTO.setFlOrderId(paymentObj.getFlOrderId());
			IInvoiceSO invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IInvoiceSO.class);
			IPaymentMatchSO paymentMatchSO = (IPaymentMatchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IPaymentMatchSO.class);
			
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.getManagedBean().setHideRemarks(false);
			FlPaymentAdvice paymentAdvice = null;
			resDTO = paymentMatchSO.retrievePaymentAdviseByOrderId(flReqDTO);
			if(resDTO.getPaymentAdvice()==null) {
				flReqDTO.setDocType(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
				resDTO = invoiceSO.retrieveInvoiceByFlOrderId(flReqDTO);
				paymentAdvice = initializePaymentAdvice(resDTO);
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			} else {
				paymentAdvice = resDTO.getPaymentAdvice();
				setAdjustmentDocNo(paymentAdvice.getFlInvoice());
				setStopInstrNo(paymentAdvice.getFulfilmentOrder());
				flBB.initializeRemarkAndAttachement(paymentAdvice.getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
			}

			getManagedBean().setPaymentAdvice(paymentAdvice);
			getManagedBean().setDelOrderList(new ArrayList<FlDeliveryOrder>()); 
			for(FlDeliveryOrder flDeliveryOrder : paymentAdvice.getFulfilmentOrder().getFlDeliveryOrders()) {
				if(flDeliveryOrder.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
					getManagedBean().getDelOrderList().add(flDeliveryOrder);
				}
			}			
			// For PO No Deduction Panel 
			getManagedBean().setDocType(paymentAdvice.getFlRequest().getDocType());
			getManagedBean().setFulfilmentRequest(paymentAdvice.getFlRequest());
			
			if(getManagedBean().getPaymentAdvice().getFulfilmentOrder().isFactored()){
				getFactoringDtl(); 
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
			checkHaveAdvancedPayment(paymentAdvice);
			validateAdvancedPayment();
			if(getManagedBean().getPaymentAdvice().isBrandNew()){
				getManagedBean().getPaymentAdvice().setCreatedDate(new Timestamp(System.currentTimeMillis()));
			}
			SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
			String newDate = df.format(getManagedBean().getPaymentAdvice().getFlInvoice().getSubmissionDate());
			getManagedBean().setInvoiceDate(newDate);
			if(paymentObj.getErrorId() != null && paymentObj.getErrorId() > 0){
				getManagedBean().setAutoUpdate(Boolean.FALSE);
				getManagedBean().setGfmasError(true);
				FulfilmentReqDTO errReq = new FulfilmentReqDTO();
				errReq.setErrorId(paymentObj.getErrorId());
				FulfilmentResDTO errRes = fulfilmentSO.retrieveErrorContent(errReq);
				getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
			}
		}else{
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			if(null == getManagedBean().getButtonVal() || !getManagedBean().getButtonVal().equalsIgnoreCase("View")){
				flBB.getManagedBean().setHideRemarks(false);
			}
		}
		//check is normal invoice or tax invoice
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		IInvoiceSO invoiceSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IInvoiceSO.class);
		reqDTO.setInvoice(getManagedBean().getPaymentAdvice().getFlInvoice());
		resDTO = invoiceSO.isInvoiceTaxable(reqDTO);
		getManagedBean().setTaxInvoice(resDTO.isTaxable());
	}
	
	/**
	 * Initialize payment advice.
	 *
	 * @param resDTO the res dto
	 * @return the fl payment advice
	 */
	private FlPaymentAdvice initializePaymentAdvice(FulfilmentResDTO resDTO) {
		FlPaymentAdvice paymentAdvice = new FlPaymentAdvice();
		paymentAdvice.setFlInvoice(resDTO.getInvoice());
		paymentAdvice.setFeeAmt(resDTO.getInvoice().getFeeAmt());
		paymentAdvice.setFlRequest(resDTO.getInvoice().getFlRequest());
		paymentAdvice.setFulfilmentOrder(resDTO.getInvoice().getFlFulfilmentOrder());
		
		setAdjustmentDocNo(resDTO.getInvoice());
		setStopInstrNo(resDTO.getInvoice().getFlFulfilmentOrder());

		return paymentAdvice;
	}
	
	/**
	 * set adjustment details into managedbean for save payment match details.
	 *
	 * @param flInvoice the new adjustment doc no
	 */
	public void setAdjustmentDocNo(FlInvoice flInvoice) {
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
	
	/**
	 * Sets the stop instruction no based on Fulfilment Order Id.
	 *
	 * @param flFulfilmentOrder the new stop instr no
	 */
	public void setStopInstrNo(FlFulfilmentOrder flFulfilmentOrder) {
		IFulfilmentOrderSO fulfilmentOrderSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
		
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setFulfilmentOrderId(flFulfilmentOrder.getFulfilmentOrderId());
		FulfilmentResDTO resDTO = fulfilmentOrderSO.retrieveStopInstructionByOrder(reqDTO);
		if(resDTO.getFlStopInstr()!=null) {
			getManagedBean().setStopInstrNo(resDTO.getFlStopInstr().getStopInstrNo());
		}
	}
	
	/**
	 * Gets the factoring details based on financial org id and address type if  isFactored.
	 *
	 * @return the factoring dtl
	 */
	public void getFactoringDtl(){
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
		getManagedBean().setSupplierAddress(null);
		getManagedBean().setPhoneNo(null);
		getManagedBean().setFaxNo(null);
		getManagedBean().setContactPerson(null);
		getManagedBean().setContactEmail(null);
		getManagedBean().setContactPhoneNo(null);

		//reset back to HQ info
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
	
	/**
	 * On tab change.
	 *
	 * @param selectedTabIndex the selected tab index
	 */
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==0){
		}
		if(selectedTabIndex==1){
			//populateChargeLineData();	
			setClassName("@(.pmChargeLinePnlClass)");
		}
		
		if(selectedTabIndex==2){
			setClassName("@(.pmSummaryPnlClass)");
		}
		if(selectedTabIndex==3){
			validateDuplicateDeduction("");
			validateDeductionAmt();
			calPmSummary();
			setClassName("@(.pmSummaryPnlClass)");
		}
		if(selectedTabIndex==4){
			getNumberOfContractOrder();
			validateDuplicateDeduction("");
			validateDeductionAmt();
			recalculateAdvancePayment();
		}
	}
	
	/**
	 * Recalculate advance payment.
	 */
	private void recalculateAdvancePayment() {
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
		BigDecimal deductionAmt = new BigDecimal(0);
		BigDecimal currentDeductionAmt = new BigDecimal(0);
		BigDecimal newPaidAmt = new BigDecimal(0);
		for(CtDeduction deduction : getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getLatestContractVer().getCtDeductions()){
			if(deduction.getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_ADVANCE_PAYMENT)){
				deductionAmt = deductionAmt.add(new BigDecimal(deduction.getAmount()));
			}
		}
		for(FlPaymentDeduction paymentDeduction : getManagedBean().getPaymentAdvice().getFlPaymentDeduction()){
			if(paymentDeduction.getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_ADVANCE_PAYMENT)){
				currentDeductionAmt = currentDeductionAmt.add(paymentDeduction.getDeductionAmt());	
			}
		}
		ContractReqDTO ctReqDTO = new ContractReqDTO();
		ctReqDTO.setContractId(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getContractId());
		ContractResDTO ctRes = contractSO.calAdvancePaymentTracking(ctReqDTO);
		if(ctRes.getPaidAdvancePaymentAmt() == null){
			ctRes.setPaidAdvancePaymentAmt(new BigDecimal(0));
		}
		newPaidAmt = ctRes.getPaidAdvancePaymentAmt().add(currentDeductionAmt);
		getManagedBean().setPaidAmt(ctRes.getPaidAdvancePaymentAmt().add(currentDeductionAmt));
		if(deductionAmt.doubleValue() == 0){
			getManagedBean().setPaidPerc(new BigDecimal(0));
		}else{
			getManagedBean().setPaidPerc(newPaidAmt.divide(deductionAmt,2,RoundingMode.HALF_UP).multiply(new BigDecimal(100)));	
		}
		getManagedBean().setBalanceAmt(deductionAmt.subtract(newPaidAmt));
	}
	
	/**
	 * Validate advanced payment.
	 */
	private void validateAdvancedPayment() {
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		ContractResDTO resDTO = new ContractResDTO();
		
		if(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract() != null){
			if(getManagedBean().isHaveAdvancedPymt()){
				BigDecimal perc = new BigDecimal(100);
				Double newTotalAmt = new Double(0);
				BigDecimal percAmt = new BigDecimal(0);
				
				ContractReqDTO contractReqDTO = new ContractReqDTO();
				contractReqDTO.setContractVerId(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getLatestContractVer().getContractVerId());
				contractReqDTO.setAsAtDate(new Date());
				BigDecimal contractValue = contractSO.retrieveContractAmount(contractReqDTO).getCtContractAmount().getContractAmount();
				
				ContractReqDTO ctReqDTO = new ContractReqDTO();
				ctReqDTO.setContractId(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getContractId());
				resDTO = contractSO.calAdvancePaymentTracking(ctReqDTO);
				if(resDTO.getPaidInvoiceAmt() == null){
					resDTO.setPaidInvoiceAmt(new BigDecimal(0));
				}
				if(getManagedBean().getBalanceAmt().doubleValue() > 0){
					if(contractValue == null || contractValue.doubleValue() == 0){
						return; 
					}else{
						newTotalAmt = resDTO.getPaidInvoiceAmt().doubleValue() / (contractValue.doubleValue());
					}
					
					percAmt = new BigDecimal(newTotalAmt).multiply(perc);	
					if(contractValue.doubleValue() <= FulfilmentConst.CONTRACT_VALUE_500K){
						if(percAmt.doubleValue() >= FulfilmentConst.PERC_50){
							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage(FacesMessage.SEVERITY_WARN, null, MessageUtils.getMessage(MessageConst.INFO_745008,
											new String[]{getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getContractNo(),FulfilmentConst.PERC_50.toString()},
											MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
						}
					}
					if(contractValue.doubleValue() >= FulfilmentConst.CONTRACT_VALUE_500K){
						if(percAmt.doubleValue() >= FulfilmentConst.PERC_90){
							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage(FacesMessage.SEVERITY_WARN, null, MessageUtils.getMessage(MessageConst.INFO_745008,
											new String[]{getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getContractNo(),FulfilmentConst.PERC_90.toString()},
											MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
						}
					}
				}
			}
		}
	}
	
	/**
	 * Gets the number of contract order based on contract id.
	 *
	 * @return the number of contract order
	 */
	private void getNumberOfContractOrder() {
		IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setContractId(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getContractId());
		FulfilmentResDTO resDTO = orderSO.getIssuedContractOrderCountByContractId(reqDTO);
		getManagedBean().setNumberOfCO(resDTO.getCount());
	}
	
	/**
	 * Populate charge line data.
	 */
	public void populateChargeLineData(){
		getManagedBean().setChargeLineItemRoot(new DefaultTreeNode("root", null));
		Map<String, TreeNode> lvl1Map = new HashMap<String, TreeNode>();
		BigDecimal grandTotalOrderAmt = new BigDecimal(0);
		BigDecimal grandTotalReceivedAmt = new BigDecimal(0);
		BigDecimal grandTotalInvAmt = new BigDecimal(0);
		BigDecimal grandTotalDanAmt = new BigDecimal(0);
		BigDecimal grandTotalCanAmt = new BigDecimal(0);
		TreeNode lv1TreeNode = null;
		for(FlInvoiceItemSummary invItemSummLv2 : getManagedBean().getPaymentAdvice().getFlInvoice().getFlInvoiceItemSummary()){
			if(lvl1Map.containsKey(invItemSummLv2.getFlFulfilmentItem().getItemCode())) {
				lv1TreeNode = lvl1Map.get(invItemSummLv2.getFlFulfilmentItem().getItemCode());
			} else {
				InvoiceChargeLineVO chargeLineLv1 = new InvoiceChargeLineVO();
				chargeLineLv1.setLevel(1);
				chargeLineLv1.setFlInvoiceSummary(invItemSummLv2);
				
				lv1TreeNode = new DefaultTreeNode(chargeLineLv1, getManagedBean().getChargeLineItemRoot());
				lvl1Map.put(invItemSummLv2.getFlFulfilmentItem().getItemCode(), lv1TreeNode);
			}
			
			grandTotalOrderAmt = grandTotalOrderAmt.add(invItemSummLv2.getOrderedAmt());
			grandTotalInvAmt = grandTotalInvAmt.add(invItemSummLv2.getInvAmt());
			if(invItemSummLv2.getFlAdjustmentItem() != null){
				if(invItemSummLv2.getFlAdjustmentItem().getFlAdjustment() != null){
					if(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE.equalsIgnoreCase(invItemSummLv2.getFlAdjustmentItem().getFlAdjustment().getDocType())){
						grandTotalCanAmt = grandTotalCanAmt.add(invItemSummLv2.getFlAdjustmentItem().getAdjustedAmt());
					}else if(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE.equalsIgnoreCase(invItemSummLv2.getFlAdjustmentItem().getFlAdjustment().getDocType())){
						grandTotalDanAmt = grandTotalDanAmt.add(invItemSummLv2.getFlAdjustmentItem().getAdjustedAmt());
					}
				}
			}
			
			InvoiceChargeLineVO chargeLineLv2 = new InvoiceChargeLineVO();
			chargeLineLv2.setLevel(2);
			chargeLineLv2.setFlInvoiceSummary(invItemSummLv2);
			TreeNode lv2TreeNode = new DefaultTreeNode(chargeLineLv2, lv1TreeNode);
			for(FlInvoiceItem invItemLv3 : invItemSummLv2.getFlInvoiceItem()){
				grandTotalReceivedAmt = grandTotalReceivedAmt.add(invItemLv3.getReceivedAmt());
				InvoiceChargeLineVO chargeLineLv3 = new InvoiceChargeLineVO();
				chargeLineLv3.setFlInvoiceSummary(invItemSummLv2);
				chargeLineLv3.setLevel(3);
				chargeLineLv3.setFlInvoiceItem(invItemLv3);
				new DefaultTreeNode(chargeLineLv3, lv2TreeNode);
			}
			if(getManagedBean().getSelectedNode()==null) {
				getManagedBean().setSelectedNode(lv1TreeNode);
				lv1TreeNode.setSelected(true);
				onNodeSelect();
			}
		}
		getManagedBean().setOrderedAmtGrandTotal(grandTotalOrderAmt);
		getManagedBean().setReceivedAmtGrandTotal(grandTotalReceivedAmt);
		getManagedBean().setInvAmtGrandTotal(grandTotalInvAmt);
		getManagedBean().setCanAmtGrandTotal(grandTotalCanAmt);
		getManagedBean().setDanAmtGrandTotal(grandTotalDanAmt);
	}
	
	/**
	 * Calculate payment match summary.
	 */
	public void calPmSummary(){
		IFeeExemptionSO feeExemptionSO = (IFeeExemptionSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFeeExemptionSO.class);
		FeeExemptionReqDTO reqDTO = new FeeExemptionReqDTO();
		FeeExemptionResDTO resDTO = new FeeExemptionResDTO();
		List<BigDecimal> nonExemptedItemList = new ArrayList<BigDecimal>();
		getManagedBean().getPaymentAdvice().setFeeAmt(new BigDecimal(0));
		getManagedBean().setDeductionAmt(new BigDecimal(0));
		getManagedBean().setGstAmount(BigDecimal.ZERO);
		BigDecimal invoiceTotalAmount = new BigDecimal(0);//total non-exempted amt
		BigDecimal gstTotalAmount = new BigDecimal(0);
		
		for(FlInvoiceItemSummary invItemSumm : getManagedBean().getPaymentAdvice().getFlInvoice().getFlInvoiceItemSummary()){
			invoiceTotalAmount = invoiceTotalAmount.add(invItemSumm.getInvAmt());
			for(FlInvoiceItem invItem : invItemSumm.getFlInvoiceItem()){
				if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.STANDARD.value())) {
					gstTotalAmount = gstTotalAmount.add(invItem.getInvAmt());
				}
			}
		}
		if(gstTotalAmount.doubleValue()>0) {
			BigDecimal divideRate = getManagedBean().getPaymentAdvice().getFlRequest().getGstRate().add(BigDecimal.valueOf(100));
			BigDecimal gstAmount = getManagedBean().getPaymentAdvice().getFlRequest().getGstRate().multiply(gstTotalAmount).divide(divideRate, CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE, RoundingMode.HALF_UP);
			getManagedBean().setGstAmount(gstAmount);
		} else {
			getManagedBean().setGstAmount(BigDecimal.ZERO);
		}
		getManagedBean().setInvoiceAmountWithoutGST(invoiceTotalAmount.subtract(getManagedBean().getGstAmount()));
		BigDecimal serviceFee = new BigDecimal(0);
		if(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract() != null){
			serviceFee = new BigDecimal(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getServiceFee());
		}else{
			serviceFee = new BigDecimal(FulfilmentConst.SERVICE_FEE_ZERO_POINT_EIGHT);;
		}		

		if(getManagedBean().getPaymentAdvice().getFlRequest().getProcurementType()!=null && getManagedBean().getPaymentAdvice().getFlRequest().getProcurementType().getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			serviceFee = new BigDecimal(0);
		}

		reqDTO.setOrgProfileId(getManagedBean().getPaymentAdvice().getFlRequest().getSmSupplier().getSupplierId());
		if(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract() != null){
			reqDTO.setContractId(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getContractId());
		}
		resDTO = feeExemptionSO.checkForFeeExceptionFirstLevel(reqDTO);
		if(resDTO.getFeeExemptionResult()!=null && resDTO.getFeeExemptionResult().booleanValue()) {
			serviceFee = new BigDecimal(0);
		}

		if(serviceFee.doubleValue()>0){
			for (FlInvoiceItemSummary data : getManagedBean().getPaymentAdvice().getFlInvoice().getFlInvoiceItemSummary()) {
				//check item whther is exempted for this supplier.

				if(data.getFlFulfilmentItem().getCmItem()!=null) {
					reqDTO.setItemId(data.getFlFulfilmentItem().getCmItem().getItemId());
				} 
				reqDTO.setRequestItemId(data.getFlFulfilmentItem().getFlFulfilmentItemAddr().get(0).getScRequestItem().getRequestNoteItemId());
				reqDTO.setOrgProfileId(getManagedBean().getPaymentAdvice().getFlRequest().getSmSupplier().getSupplierId());
				if(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract() != null){
					reqDTO.setContractId(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getContractId());
				}
				resDTO = feeExemptionSO.checkForFeeException(reqDTO);
				if(resDTO.getFeeExemptionResult() == null ||resDTO.getFeeExemptionResult() == false){
					nonExemptedItemList.add(data.getInvAmt());
				}
			}
		}

		getManagedBean().setServiceCharge(Double.toString(serviceFee.doubleValue()));

		BigDecimal nonExemptedItemAmt = new BigDecimal(0);
		BigDecimal percentage = new BigDecimal(100);
		BigDecimal invMulty = new BigDecimal(0.0);
		if(!nonExemptedItemList.isEmpty()){
			for(BigDecimal normalItemAmt : nonExemptedItemList){
				nonExemptedItemAmt = nonExemptedItemAmt.add(normalItemAmt);
			}
		}

		boolean haveDeduction = false;
		BigDecimal calPaymentAmt = new BigDecimal(0.0);
		BigDecimal totalDeductionAmt = new BigDecimal(0);
		for(FlPaymentDeduction deduction : getManagedBean().getPaymentAdvice().getFlPaymentDeduction()) {
			if(deduction.getDeductionType().getParameterCode()!=null && 
					!deduction.getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_TRANSACTION_FEE)) {
				if(deduction.getDeductionAmt() == null){
					deduction.setDeductionAmt(new BigDecimal(0));
				}
				totalDeductionAmt = totalDeductionAmt.add(deduction.getDeductionAmt());
				if(totalDeductionAmt.doubleValue() > getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceAmtNew().doubleValue()){
					throw new EpException(MessageConst.ERROR_745066);
				}
				haveDeduction = true;
			}
		}

		BigDecimal newInvoiceAmt = new BigDecimal(0);
		BigDecimal perc = new BigDecimal(100);
		BigDecimal transFee = new BigDecimal(0);
		if(haveDeduction && nonExemptedItemAmt.doubleValue() > 0){
			if(totalDeductionAmt != null && totalDeductionAmt.doubleValue() > 0){
				
				newInvoiceAmt = nonExemptedItemAmt.subtract(totalDeductionAmt);
				if(newInvoiceAmt.doubleValue() <= 0){
					getManagedBean().getPaymentAdvice().setFeeAmt(new BigDecimal(0));
				}else{
					newInvoiceAmt = newInvoiceAmt.multiply(serviceFee);
					transFee = newInvoiceAmt.divide(perc,2,RoundingMode.HALF_UP);
					getManagedBean().getPaymentAdvice().setFeeAmt(transFee);
				}
			}
			getManagedBean().setDeductionAmt(totalDeductionAmt);

			calPaymentAmt=calPaymentAmt.add(getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceAmtNew().subtract(totalDeductionAmt).subtract(transFee));
		}else if(haveDeduction && nonExemptedItemAmt.doubleValue() <= 0){
			if(totalDeductionAmt != null && totalDeductionAmt.doubleValue() > 0){
				getManagedBean().getPaymentAdvice().setFeeAmt(transFee);
				getManagedBean().setDeductionAmt(totalDeductionAmt);
				calPaymentAmt=calPaymentAmt.add(getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceAmtNew().subtract(totalDeductionAmt).subtract(transFee));
			}
		}else if(nonExemptedItemAmt.doubleValue() >= 0 && haveDeduction == false){
			if(nonExemptedItemAmt.doubleValue() > 0){
				invMulty = nonExemptedItemAmt.multiply(serviceFee);
				transFee = invMulty.divide(percentage,2,RoundingMode.HALF_UP);
				getManagedBean().getPaymentAdvice().setFeeAmt(transFee);
				getManagedBean().setDeductionAmt(totalDeductionAmt);
				calPaymentAmt=calPaymentAmt.add(getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceAmtNew().subtract(transFee));
				if(totalDeductionAmt.doubleValue() > 0){
					calPaymentAmt=calPaymentAmt.subtract(totalDeductionAmt);
				}

			} else if(nonExemptedItemAmt.doubleValue() <= 0){
				calPaymentAmt=getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceAmtNew();
			}
			
		} 
		getManagedBean().getPaymentAdvice().setPaymentAmt(calPaymentAmt);
		getTaxinvoice();
	}
	
	private void getTaxinvoice(){
		getManagedBean().setTaxInvoiceList(null);
		Map<String, TaxInvoiceVO> taxAmountMap = null;
		for(FlInvoiceItemSummary invItemSumm : getManagedBean().getPaymentAdvice().getFlInvoice().getFlInvoiceItemSummary()){
			for(FlInvoiceItem invItem : invItemSumm.getFlInvoiceItem()){
				if(taxAmountMap != null){
					if(taxAmountMap.containsKey(invItem.getGstFlag().getParameterCode())){
						TaxInvoiceVO taxInvVO = taxAmountMap.get(invItem.getGstFlag().getParameterCode());
						taxInvVO.setAmountWithoutGst(taxInvVO.getAmountWithoutGst().add(invItem.getInvAmt()));
						taxAmountMap.put(invItem.getGstFlag().getParameterCode(), taxInvVO);
					}else{
						TaxInvoiceVO taxInvVO = new TaxInvoiceVO();
						taxInvVO.setAmountWithoutGst(invItem.getInvAmt());
						if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.STANDARD.value())){
							taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = " +getManagedBean().getPaymentAdvice().getFlRequest().getGstRate()+"%");
							taxInvVO.setGstAmount(getManagedBean().getGstAmount().toString());	
						}else if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.ZERO_RATED.value())){
							taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = 0%");
							taxInvVO.setGstAmount(BigDecimal.ZERO.toString());	
						}else {
							taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = Nil");
							taxInvVO.setGstAmount("Nil");	
						}
						taxInvVO.setGstFlagCode(invItem.getGstFlag().getParameterCode());
						taxAmountMap.put(invItem.getGstFlag().getParameterCode(), taxInvVO);
					}
				}else{
					taxAmountMap = new HashMap<String, TaxInvoiceVO>();
					TaxInvoiceVO taxInvVO = new TaxInvoiceVO();
					taxInvVO.setAmountWithoutGst(invItem.getInvAmt());
					if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.STANDARD.value())){
						taxInvVO.setGstAmount(getManagedBean().getGstAmount().toString());	
						taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = " +getManagedBean().getPaymentAdvice().getFlRequest().getGstRate()+"%");
					}else if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.ZERO_RATED.value())){
						taxInvVO.setGstAmount(BigDecimal.ZERO.toString());	
						taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = 0%");
					}else {
						taxInvVO.setGstAmount("Nil");	
						taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = Nil");
					}
					taxInvVO.setGstFlagCode(invItem.getGstFlag().getParameterCode());
					
					taxAmountMap.put(invItem.getGstFlag().getParameterCode(), taxInvVO);
				}
				
				
			}
		}
		getManagedBean().setTaxInvoiceList(new ArrayList<TaxInvoiceVO>());
		for(Entry<String, TaxInvoiceVO> itemEntry : taxAmountMap.entrySet()){
			TaxInvoiceVO taxInvoice = itemEntry.getValue();
			if(taxInvoice.getGstFlagCode().equalsIgnoreCase(GSFType.STANDARD.value())){
				BigDecimal amtWithoutGst = taxInvoice.getAmountWithoutGst().subtract(getManagedBean().getGstAmount()) ;
				taxInvoice.setAmountWithoutGst(amtWithoutGst);
			}
			getManagedBean().getTaxInvoiceList().add(taxInvoice);
		}
	}
	
	/**
	 * charge line On node select.
	 */
	public void onNodeSelect() {
		TreeNode selectedNode=getManagedBean().getSelectedNode();
		onNodeUnSelect();
		if(null!=selectedNode){
			InvoiceChargeLineVO treeNode = (InvoiceChargeLineVO)selectedNode.getData();
			if(treeNode.getLevel() == 1){
				for(TreeNode lv2Tree:selectedNode.getChildren()){
					FlFulfilmentItem childData=((InvoiceChargeLineVO)lv2Tree.getData()).getFlInvoiceSummary().getFlFulfilmentItem();
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
						getManagedBean().setSetiaCode(childData.getPmSubSetia().getSetiaCode());
						getManagedBean().setProjectCode(childData.getPmSubSetia().getProjectCode());
						getManagedBean().setFinancingTypeCode(childData.getPmSubSetia().getFinancingType().getParameterCode());
					}
				}
			}
			
		}
	}
	
	/**
	 * charge line On node un select.
	 */
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
	 * Gets the vot fund code map.
	 *
	 * @return the votFundCodeMap
	 */
	public static Map<Long, PmVotFund> getVotFundCodeMap() {
		return votFundCodeMap;
	}
	
	/**
	 * Sets the vot fund code map.
	 *
	 * @param votFundCodeMap the votFundCodeMap to set
	 */
	public static void setVotFundCodeMap(Map<Long, PmVotFund> votFundCodeMap) {
		PaymentMatchBackingBean.votFundCodeMap = votFundCodeMap;
	}
	
	/**
	 * Gets the gl account code map.
	 *
	 * @return the glAccountCodeMap
	 */
	public static Map<Long, PmGlAccount> getGlAccountCodeMap() {
		return glAccountCodeMap;
	}
	
	/**
	 * Sets the gl account code map.
	 *
	 * @param glAccountCodeMap the glAccountCodeMap to set
	 */
	public static void setGlAccountCodeMap(Map<Long, PmGlAccount> glAccountCodeMap) {
		PaymentMatchBackingBean.glAccountCodeMap = glAccountCodeMap;
	}
	
	/**
	 * Gets the progm activity code map.
	 *
	 * @return the progmActivityCodeMap
	 */
	public static Map<Long, PmPrgActivity> getProgmActivityCodeMap() {
		return progmActivityCodeMap;
	}
	
	/**
	 * Sets the progm activity code map.
	 *
	 * @param progmActivityCodeMap the progmActivityCodeMap to set
	 */
	public static void setProgmActivityCodeMap(
			Map<Long, PmPrgActivity> progmActivityCodeMap) {
		PaymentMatchBackingBean.progmActivityCodeMap = progmActivityCodeMap;
	}
	
	/**
	 * Gets the sub setia code map.
	 *
	 * @return the subSetiaCodeMap
	 */
	public static Map<Long, PmSubSetia> getSubSetiaCodeMap() {
		return subSetiaCodeMap;
	}
	
	/**
	 * Sets the sub setia code map.
	 *
	 * @param subSetiaCodeMap the subSetiaCodeMap to set
	 */
	public static void setSubSetiaCodeMap(Map<Long, PmSubSetia> subSetiaCodeMap) {
		PaymentMatchBackingBean.subSetiaCodeMap = subSetiaCodeMap;
	}
	
	/**
	 * Adds the payment match deduction row.
	 */
	public void addDeductionRow(){
		
		FlPaymentDeduction paymentDeduction = new FlPaymentDeduction();
		paymentDeduction.setFlPaymentAdvice(getManagedBean().getPaymentAdvice());
		paymentDeduction.setDeductionType(new PmParameter());
		paymentDeduction.setDeductionDesc("");
		paymentDeduction.setPmVotFund(new PmVotFund());
		paymentDeduction.setPmGlAccount(new PmGlAccount());
		getManagedBean().getPaymentAdvice().getFlPaymentDeduction().add(paymentDeduction);
	}
	
	/**
	 * Retrieve clauses.
	 *
	 * @param deduction the deduction
	 */
	public void retrieveClauses(FlPaymentDeduction deduction){
		if(deduction.getDeductionType().getParameterCode() != null){
			for(CtDeduction ctDeduction : getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getLatestContractVer().getCtDeductions()){
				if(ctDeduction.getDeductionType().getParameterCode().equals(deduction.getDeductionType().getParameterCode())){
					getManagedBean().setCtDeductionClauseList(ctDeduction.getCtDeductionClauses());
				}
			}
		}
	}
	
	/**
	 * Page navigation to FLDoc search page in PA.
	 *
	 * @return the string
	 */
	public String backToPervious() {
		return "flDocSearch";
	}
	
	/**
	 * Removes Payment match deduction row.
	 */
	public void removeAddRow(){
		try {
			if(this.getManagedBean().getPaymentAdvice().getFlPaymentDeduction() != null){
				Iterator<FlPaymentDeduction> iterator = this.getManagedBean().getPaymentAdvice().getFlPaymentDeduction().iterator();
				while(iterator.hasNext()){
					FlPaymentDeduction  paymentDeduction = iterator.next();
					if(getManagedBean().getSelectedDeductions().contains(paymentDeduction)){
						iterator.remove();
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	/**
	 * Check deduction list based on parameter id.
	 *
	 * @param deduction the deduction
	 */
	public void checkDeductionList(FlPaymentDeduction deduction){
		IParameterSO parameterSO = null;
		ParameterReqDTO reqDTO = null;
		if(deduction.getDeductionType().getParameterId() != null){
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(deduction.getDeductionType().getParameterId());
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			deduction.setDeductionType(resDTO.getPmParameter());
		}
			deduction.setDeductionDesc(null);
			deduction.setPmGlAccount(null);
			deduction.setPmVotFund(null);

		if(deduction.getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_FIXED)){
			for(CtDeduction ctDeduction : getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getLatestContractVer().getCtDeductions()){
				if(ctDeduction.getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_FIXED)){
					deduction.setDeductionAmt(new BigDecimal(ctDeduction.getAmount()));
				}
			}
		}else{
			deduction.setDeductionAmt(new BigDecimal(0));
		}
	}
	
	/**
	 * Gets the dropdown values by ct details.
	 *
	 * @return the dropdown values by ct details
	 */
	public List<SelectItem> getDropdownValuesByCtDetails(){
		if(getManagedBean().getPopulateDeductionList()== null || getManagedBean().getPopulateDeductionList().isEmpty()){
			if (this.getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getLatestContractVer().getCtDeductions() != null) {
				for (CtDeduction obj : this.getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getLatestContractVer().getCtDeductions()) {
					if(obj.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
						getManagedBean().getPopulateDeductionList().add(new SelectItem(obj.getDeductionType().getParameterId(),getParameterDesc(obj.getDeductionType().getParameterType(), obj.getDeductionType().getParameterCode())));

					}
				}
			}
		}
		return getManagedBean().getPopulateDeductionList();
	}
	
	/**
	 * Validate duplicate deduction.
	 *
	 * @param outcome the outcome
	 */
	public void validateDuplicateDeduction(String outcome){
		IParameterSO parameterSO = (IParameterSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		List<String> deductionList = new ArrayList<String>();
		deductionList.clear();
		
		if(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract() != null){
			if(getManagedBean().getPaymentAdvice().getFlPaymentDeduction() != null && !getManagedBean().getPaymentAdvice().getFlPaymentDeduction().isEmpty()){
				ParameterReqDTO reqType = new ParameterReqDTO();
				ParameterResDTO resType = new ParameterResDTO();
				ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();;
				reqType.setParameterType(ParameterTypeConst.DEDUCTION_TYPE);
				reqType.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
				
				List<FlPaymentDeduction> emptyDeductionList = new ArrayList<FlPaymentDeduction>();
				for(FlPaymentDeduction deduction : getManagedBean().getPaymentAdvice().getFlPaymentDeduction()){

					if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){//save function
						if(deduction.getDeductionType().getParameterCode() == null && 
								(deduction.getPmVotFund() == null || deduction.getPmVotFund().getVotFundCode() == null) &&
								(deduction.getPmGlAccount() == null || deduction.getPmGlAccount().getGlAccCode() == null)){
							emptyDeductionList.add(deduction);
						}else if(deduction.getDeductionType().getParameterCode() == null){
							throw new EpException(MessageConst.ERROR_740035);
						}
						if(deduction.getDeductionAmt() == null){
							deduction.setDeductionAmt(new BigDecimal(0));
						}
					}else{
						if(deduction.getDeductionType().getParameterCode() == null){
							throw new EpException(MessageConst.ERROR_740035);
						}

						if(deduction.getDeductionType().getParameterId() == null){
							throw new EpException(MessageConst.ERROR_740035);
						}
						if(deduction.getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_OTHERS)){
							if(deduction.getDeductionDesc().trim().equalsIgnoreCase("") || deduction.getDeductionDesc() == null){
								searchBean.getDataFilterBeans().add(new SearchFilterBean(
										"pmParameter.parameterCode",
										SearchOperator.OPERATOR_EQUALS.value(),
										deduction.getDeductionType().getParameterCode()));
								reqType.setParameterDescSearchBean(searchBean);

								resType = parameterSO.retrieveParameterDescListByType(reqType);
								throw new EpException(MessageConst.ERROR_745058,new String[]{resType.getParameterDescList().get(0).getCodeName()});
							}

						}
						if(deduction.getPmVotFund() == null || deduction.getPmVotFund().getVotFundCode() == null){
							searchBean.getDataFilterBeans().add(new SearchFilterBean(
									"pmParameter.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									deduction.getDeductionType().getParameterCode()));
							reqType.setParameterDescSearchBean(searchBean);

							resType = parameterSO.retrieveParameterDescListByType(reqType);
							throw new EpException(MessageConst.ERROR_745059,new String[]{resType.getParameterDescList().get(0).getCodeName()});
						}
						if(deduction.getPmGlAccount() == null || deduction.getPmGlAccount().getGlAccCode() == null){
							searchBean.getDataFilterBeans().add(new SearchFilterBean(
									"pmParameter.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									deduction.getDeductionType().getParameterCode()));
							reqType.setParameterDescSearchBean(searchBean);

							resType = parameterSO.retrieveParameterDescListByType(reqType);
							throw new EpException(MessageConst.ERROR_745060,new String[]{resType.getParameterDescList().get(0).getCodeName()});
						}
						if(deduction.getDeductionAmt() == null || deduction.getDeductionAmt().doubleValue() <= 0){
							searchBean.getDataFilterBeans().add(new SearchFilterBean(
									"pmParameter.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									deduction.getDeductionType().getParameterCode()));
							reqType.setParameterDescSearchBean(searchBean);

							resType = parameterSO.retrieveParameterDescListByType(reqType);
							throw new EpException(MessageConst.ERROR_745061,new String[]{resType.getParameterDescList().get(0).getCodeName()});
						}
						String unique = deduction.getDeductionType().getParameterId().toString() +deduction.getPmVotFund().getVotFundCode() +deduction.getPmGlAccount().getGlAccCode();
						if(deductionList.contains(unique)){
							throw new EpException(MessageConst.ERROR_740036);
						}else{
							deductionList.add(deduction.getDeductionType().getParameterId().toString() +deduction.getPmVotFund().getVotFundCode() +deduction.getPmGlAccount().getGlAccCode());
						}
					}
				}
				getManagedBean().getPaymentAdvice().getFlPaymentDeduction().removeAll(emptyDeductionList);
				emptyDeductionList.clear();
			}
		}
	}
	
	/**
	 * Gpki sign action.
	 *
	 * @param outcome the outcome
	 */
	public void gpkiSignAction(String outcome){
		if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_MATCH)) {
			validatePaymentMatch(outcome);
			StringBuilder sb = new StringBuilder();
			sb.append(getManagedBean().getPaymentAdvice().getFulfilmentOrder().getDocNo()+"|");
			if(getManagedBean().getPaymentAdvice().isBrandNew()) {
				sb.append(ParameterConst.DOCTYPE_PAYMENT_ADVICE+"|");
			} else {
				sb.append(getManagedBean().getPaymentAdvice().getPaymentAdviceNo()+"|");
			}
			sb.append(outcome+"|");
			sb.append(getFacesScopes().getUserSessionBean().getIdentificationNo());
			getManagedBean().setTextBeforeSign(sb.toString());
			// set action
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
		}else{
			submitAction(outcome);
		}
	}
	
	/**
	 * Pre submit action.
	 *
	 * @param pmDigiSign the pm digi sign
	 * @return the string
	 * @throws Exception the exception
	 */
	public String preSubmitAction(
			PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);
		}
		return submitAction(getManagedBean().getButtonAction());
	}
	
	/**
	 * Enable auto update.
	 */
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}
	
	/**
	 * Validate payment match.
	 *
	 * @param outcome the outcome
	 */
	private void validatePaymentMatch(String outcome){
		IPaymentMatchSO paymentSO = null;
		try {	
			getManagedBean().setAutoUpdate(Boolean.TRUE);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlPaymentAdvice paymentAdvice = getManagedBean().getPaymentAdvice();				  
			paymentSO = (IPaymentMatchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IPaymentMatchSO.class);
			reqDTO.setPaymentAdvice(paymentAdvice);
			reqDTO.setOutcome(outcome);
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());				
			reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
			validateDuplicateDeduction(outcome);
			validateDeductionAmt();
			if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_MATCH)){
				calPmSummary();
			}
			
			paymentSO.validatePaymentMatch(reqDTO);
			
		}finally {
			paymentSO = null;
		}
	}
	
	/**
	 * Check have advanced payment.
	 *
	 * @param paymentAdvice the payment advice
	 */
	public void checkHaveAdvancedPayment(FlPaymentAdvice paymentAdvice){
		if(paymentAdvice.getFlRequest().getCtContract() != null){
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
			BigDecimal deductionAmt = new BigDecimal(0);
			
			if(paymentAdvice.getFlRequest().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
				if(getManagedBean().getTaskId()==null) {
					if(getManagedBean().getPaymentAdvice().getFlPaymentDeduction().size()>0) {
						getManagedBean().setShowDeductionTab(true);
					} 
				} else { // on going task
					if(paymentAdvice.getFlRequest().getCtContract().getLatestContractVer().getCtDeductions().size()>0) { 
						getManagedBean().setShowDeductionTab(true);
					} else if(getManagedBean().getPaymentAdvice().getFlPaymentDeduction().size()>0) { // clear payment deduction table if already exist
						getManagedBean().getPaymentAdvice().getFlPaymentDeduction().clear();
					} 
				}
			}
			
			for(CtDeduction deduction : paymentAdvice.getFlRequest().getCtContract().getLatestContractVer().getCtDeductions()){
				/*if(!deduction.getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_TRANSACTION_FEE)){
					getManagedBean().setShowDeductionTab(true);
				}*/
				if(deduction.getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_ADVANCE_PAYMENT)){
					getManagedBean().setHaveAdvancedPymt(true);
					deductionAmt = deductionAmt.add(new BigDecimal(deduction.getAmount()));
				}
			}
			ContractReqDTO ctReqDTO = new ContractReqDTO();
			ctReqDTO.setContractId(paymentAdvice.getFlRequest().getCtContract().getContractId());
			ContractResDTO ctRes = contractSO.calAdvancePaymentTracking(ctReqDTO);
			if(ctRes.getPaidAdvancePaymentAmt() == null){
				ctRes.setPaidAdvancePaymentAmt(new BigDecimal(0));
			}
			getManagedBean().setPaidAmt(ctRes.getPaidAdvancePaymentAmt());
			if(deductionAmt.doubleValue() == 0){	
				getManagedBean().setPaidPerc(new BigDecimal(0));
			}else{
				getManagedBean().setPaidPerc(ctRes.getPaidAdvancePaymentAmt().divide(deductionAmt,2,RoundingMode.HALF_UP).multiply(new BigDecimal(100)));	
			}
			getManagedBean().setBalanceAmt(deductionAmt.subtract(ctRes.getPaidAdvancePaymentAmt()));
		}
	}
	
	/**
	 * Gets the deduction table.
	 *
	 * @return the deduction table
	 */
	public List<FlPaymentDeduction> getDeductionTable(){
		List<FlPaymentDeduction> deductionTable = new ArrayList<FlPaymentDeduction>();
		for(FlPaymentDeduction deduction : getManagedBean().getPaymentAdvice().getFlPaymentDeduction()){
			if(deduction.getDeductionType().getParameterCode() == null){
				deductionTable.add(deduction);
			}else if(!deduction.getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_TRANSACTION_FEE)){
				deductionTable.add(deduction);
			}
		}
		
		return deductionTable;
	}
	
	/**
	 * Validate deduction amount .
	 */
	public void validateDeductionAmt(){
		BigDecimal totalDeductionAmt = new BigDecimal(0);
		for(FlPaymentDeduction deduction : getManagedBean().getPaymentAdvice().getFlPaymentDeduction()) {
			if(deduction.getDeductionType().getParameterCode()!=null && 
					!deduction.getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_TRANSACTION_FEE)) {
				if(deduction.getDeductionAmt() == null){
					deduction.setDeductionAmt(new BigDecimal(0));
				}
				totalDeductionAmt = totalDeductionAmt.add(deduction.getDeductionAmt());
				if(totalDeductionAmt.doubleValue() > getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceAmtNew().doubleValue()){
					throw new EpException(MessageConst.ERROR_100032, new String[]{
							ResourceBundleUtils.getMessage("lbl.fl.payment.match.total.deduction",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()),
							ResourceBundleUtils.getMessage("lbl.fl.amount.invoice",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
				}
			}
		}
	}
	
	/**
	 * Navigate to other page based on document type.
	 *
	 * @param docType the doc type
	 * @return the string
	 */
	public String navigateDocument(String docType){
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER) ||docType.equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
			IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setFlOrderId(getManagedBean().getPaymentAdvice().getFulfilmentOrder().getFulfilmentOrderId());
			reqDTO.setDocType(docType);
			FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(reqDTO);
			FulfilmentOrderBackingBean orderBackingBean = new FulfilmentOrderBackingBean();
			orderBackingBean.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
			orderBackingBean.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			FacesContext fc = FacesContext.getCurrentInstance();
			orderBackingBean.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
			orderBackingBean.getManagedBean().setHyperLinkPage(true);
			orderBackingBean.getManagedBean().setButtonVal("View");
			flBB.getManagedBean().setHideRemarks(true);
			if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
				return NAV_PO;
			}else if (docType.equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
				return NAV_CO;
			}
		}else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE)){
			IAdjustmentSO adjustmentSO = (IAdjustmentSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IAdjustmentSO.class);
			Long adjId = null;
			for(FlAdjustment adjOb : getManagedBean().getPaymentAdvice().getFlInvoice().getFlAdjustments()){
				if(adjOb.getDocType().equalsIgnoreCase(docType)){
					adjId = adjOb.getAdjustmentId();
					break;
				}
			}
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setDanId(adjId);
			reqDTO.setDocType(docType);
			FulfilmentResDTO resDTO = adjustmentSO.retrieveAdjustmentById(reqDTO);
			DebitAdjustmentNoteBackingBean adjBB = new DebitAdjustmentNoteBackingBean();
			adjBB.getManagedBean().setAdjustment(resDTO.getAdjustment());
			resDTO.setAdjLink(true);
			adjBB.setAdjustmentNote(resDTO);
			FacesContext fc = FacesContext.getCurrentInstance();
			adjBB.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
			adjBB.getManagedBean().setButtonVal("View");
			adjBB.getManagedBean().setHyperLinkPage(true);
			flBB.getManagedBean().setHideRemarks(true);

			return NAV_ADJ;
		}else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE)){
			IAdjustmentSO adjustmentSO = (IAdjustmentSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IAdjustmentSO.class);
			Long adjId = null;
			for(FlAdjustment adjOb : getManagedBean().getPaymentAdvice().getFlInvoice().getFlAdjustments()){
				if(adjOb.getDocType().equalsIgnoreCase(docType)){
					adjId = adjOb.getAdjustmentId();
					break;
				}
			}
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setDanId(adjId);
			reqDTO.setDocType(docType);
			FulfilmentResDTO resDTO = adjustmentSO.retrieveAdjustmentById(reqDTO);
			DebitAdjustmentNoteBackingBean adjBB = new DebitAdjustmentNoteBackingBean();
			adjBB.getManagedBean().setAdjustment(resDTO.getAdjustment());
			adjBB.getManagedBean().setSelectedTabIndex(0);
			FacesContext fc = FacesContext.getCurrentInstance();
			resDTO.setAdjLink(true);
			adjBB.setAdjustmentNote(resDTO);
			adjBB.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
			adjBB.getManagedBean().setButtonVal("View");
			adjBB.getManagedBean().setHyperLinkPage(true);
			flBB.getManagedBean().setHideRemarks(true);

			return NAV_ADJ;
		}else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_STOP_DELIVERY)){
			IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
			FulfilmentResDTO resDTO = orderSO.retrieveStopDeliveryByDocNo(getManagedBean().getStopInstrNo());
			StopInstrBackingBean siBB = new StopInstrBackingBean();
			siBB.getManagedBean().setStopInstr(resDTO.getFlStopInstr());
			siBB.populateChargeLineData();
			FacesContext fc = FacesContext.getCurrentInstance();
			siBB.getManagedBean().setBackToDocument(fc.getViewRoot().getViewId());
			siBB.getManagedBean().setButtonVal("View");
			siBB.getManagedBean().setHyperLinkPage(true);
			flBB.getManagedBean().setHideRemarks(true);
			return NAV_SI;
		}else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_INVOICE)){
			IInvoiceSO invoiceSO = (IInvoiceSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IInvoiceSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setInvoiceId(getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceId());
			reqDTO.setDocType(docType);
			reqDTO.setInvoiceLink(true);
			FulfilmentResDTO resDTO = invoiceSO.retrieveInvoiceByInvoiceId(reqDTO);
			InvoiceBackingBean invBB = new InvoiceBackingBean();
			invBB.getManagedBean().setInvoice(resDTO.getInvoice());
			invBB.getManagedBean().setHyperLinkPage(true);
			invBB.getManagedBean().setButtonVal("View");
			flBB.getManagedBean().setHideRemarks(true);
			invBB.getManagedBean().setDocMaintain(false);
			invBB.getManagedBean().setDelOrderList(new ArrayList<FlDeliveryOrder>()); 
			for(FlDeliveryOrder flDeliveryOrder : invBB.getManagedBean().getInvoice().getFlFulfilmentOrder().getFlDeliveryOrders()) {
				if(flDeliveryOrder.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
					invBB.getManagedBean().getDelOrderList().add(flDeliveryOrder);
				}
			}		
			
			FacesContext fc = FacesContext.getCurrentInstance();
			invBB.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());

			return NAV_INVOICE;
		}if(docType.equalsIgnoreCase(ParameterConst.CONTRACT)){
			ContractDetailsBackingBean ctDetBB = new ContractDetailsBackingBean();
			FacesContext fc = FacesContext.getCurrentInstance();
			ctDetBB.getManagedBean().setGotoPreviewPage(fc.getViewRoot().getViewId());
			return ctDetBB.gotoCommonViewPage(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getContractId());
		}
		return null;
	}
	
	/**
	 * Navigate to other page based on document type.
	 *
	 * @param docType the doc type
	 * @return the string
	 */
	
	public String navigateDelOrder(FlDeliveryOrder delOrder){
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			IDeliveryOrderSO orderSO = (IDeliveryOrderSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IDeliveryOrderSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setDeliveryOrderId(delOrder.getDeliveryOrderId());
			reqDTO.setDocType(ParameterConst.DOCTYPE_DELIVERY_ORDER);
			FulfilmentResDTO resDTO = orderSO.retrieveDeliveryOrderById(reqDTO);
			DeliveryOrderBackingBean doBB = new DeliveryOrderBackingBean();
			doBB.renewSesionObject();
			FacesContext fc = FacesContext.getCurrentInstance();
			doBB.getManagedBean().setFlDeliveryOrder(resDTO.getFlDeliveryOrder());
			doBB.getManagedBean().setFulfilmentRequest(resDTO.getFlDeliveryOrder().getFlRequest());
			doBB.getManagedBean().setFulfilmentOrder(resDTO.getFlDeliveryOrder().getFlFulfilmentOrder());
			doBB.getManagedBean().setFromPageUrl(fc.getViewRoot().getViewId());
			doBB.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
			//doBB.getManagedBean().setButtonVal("Review");
			doBB.getManagedBean().setHyperLinkPage(true);
			doBB.getManagedBean().setButtonVal("View");
			flBB.getManagedBean().setHideRemarks(true);
			return NAV_DELIVERYORDER_PAGE;
	}
	
	/**
	 * Navigate to other page based on document type.
	 *
	 * @param docType the doc type
	 * @return the string
	 */
	
	public String navigateFulfilmentNote(FlDeliveryOrder delOrder){
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			IReceivedNoteSO receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReceivedNoteSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setFulfilmentNoteId(delOrder.getFulfilmentNote().getFulfilmentNoteId());
			reqDTO.setDocType(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
			FulfilmentResDTO resDTO = receivedNoteSO.retrieveReceivedNoteByReceivedNoteId(reqDTO);
			ReceivedNoteBackingBean recNoteBB = new ReceivedNoteBackingBean();
			recNoteBB.renewSesionObject();
			recNoteBB.getManagedBean().setSelectedTabIndex(0);
			recNoteBB.getManagedBean().setFulfilmentNote(resDTO.getFulfilmentNote());
			recNoteBB.getManagedBean().setFulfilmentRequest(resDTO.getFulfilmentNote().getFlRequest());
			recNoteBB.getManagedBean().getFulfilmentNote().setFlRequest(resDTO.getFulfilmentNote().getFlRequest());
			recNoteBB.getManagedBean().setButtonVal("View");
			reqDTO.setDeliveryOrderId(resDTO.getFulfilmentNote().getFlDeliveryOrder().getDeliveryOrderId());
			recNoteBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			for(FlFulfilmentNoteItem fulfilmentNoteItem: resDTO.getFulfilmentNote().getFlFulfilmentNoteItem()){
				recNoteBB.getManagedBean().setSelectedItem(fulfilmentNoteItem);
			}
			recNoteBB.getManagedBean().getFulfilmentNote().setFlFulfilmentNoteItem(resDTO.getFulfilmentNote().getFlFulfilmentNoteItem());
			recNoteBB.getManagedBean().setAcknowledged(true);
			recNoteBB.getManagedBean().setHyperLinkPage(true);
			//recNoteBB.getManagedBean().setButtonVal("View");
			flBB.getManagedBean().setHideRemarks(true);
			FacesContext fc = FacesContext.getCurrentInstance();
			recNoteBB.getManagedBean().setFromPageUrl(fc.getViewRoot().getViewId());
			recNoteBB.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
			
			
		return NAV_FULFILMENTNOTE_PAGE;
	}
	
	/**
	 * Show 1GFMAS error
	 *
	 */
	public void show1GFMASError(){
		getManagedBean().setAutoUpdate(Boolean.FALSE);
		RequestContext.getCurrentInstance().update("@form");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,null,getManagedBean().getGfmasErrorContent()));
	}
	
	/**
	 * Retrieve payment advise information by payment id and display in UI
	 *
	 */
	public void viewPaymentMatch() {
		IPaymentMatchSO paymentSO = (IPaymentMatchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IPaymentMatchSO.class);
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		
		String paymentAdviseId = originalServletRequest.getParameter("paymentAdviseId");
		
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		if(paymentAdviseId!=null) {
			reqDTO.setPaymentAdviseId(Long.valueOf(paymentAdviseId));
			reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			reqDTO.setDocType(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
			reqDTO.setFrnLink(Boolean.TRUE);
			renewSesionObject();
			try {
				resDTO = paymentSO.retrievePaymentAdviseByPaymentId(reqDTO);
			} catch (EpException e) {
				handleException(e);
				return;
			}
			
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			getManagedBean().setSelectedTabIndex(0);
			getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			getManagedBean().setTaskId(BpmOutcomeConst.BPM_VIEW);//for the purpose of hide the back button.
			
			getManagedBean().setPaymentAdvice(resDTO.getPaymentAdvice());
			getManagedBean().setDelOrderList(new ArrayList<FlDeliveryOrder>()); 
			for(FlDeliveryOrder flDeliveryOrder : resDTO.getPaymentAdvice().getFulfilmentOrder().getFlDeliveryOrders()) {
				if(flDeliveryOrder.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
					getManagedBean().getDelOrderList().add(flDeliveryOrder);
				}
			}
			setAdjustmentDocNo(resDTO.getPaymentAdvice().getFlInvoice());
			setStopInstrNo(resDTO.getPaymentAdvice().getFulfilmentOrder());
			populateChargeLineData();
			getManagedBean().setFulfilmentRequest(resDTO.getPaymentAdvice().getFlRequest());
			getManagedBean().getPaymentAdvice().setFlPaymentDeduction(resDTO.getPaymentAdvice().getFlPaymentDeduction());
			getManagedBean().setPmUser(reqDTO.getPmUser());
			checkHaveAdvancedPayment(resDTO.getPaymentAdvice());
			
			SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
			String newDate = df.format(getManagedBean().getPaymentAdvice().getFlInvoice().getSubmissionDate());
			getManagedBean().setInvoiceDate(newDate);
			flBB.getManagedBean().setHideRemarks(true);
			flBB.initializeRemarkAndAttachement(resDTO.getPaymentAdvice().getFlWorkflowStatus(), resDTO.getRemarkList(),resDTO.getAttachmentList());
			if(getManagedBean().getPaymentAdvice().getFulfilmentOrder().isFactored()){
				getFactoringDtl(); 
			}
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "pmBasePage?faces-redirect=true");
		}
	}	
}

