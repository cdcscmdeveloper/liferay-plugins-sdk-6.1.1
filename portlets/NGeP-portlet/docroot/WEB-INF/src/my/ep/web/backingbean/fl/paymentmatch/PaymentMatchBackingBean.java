/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.paymentmatch;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.liferay.portal.util.PortalUtil;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.fl.IFulfilmentSO;
import my.ep.biz.service.fl.adjustment.IAdjustmentSO;
import my.ep.biz.service.fl.frn.IReceivedNoteSO;
import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.payment.IPaymentMatchSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.confgeneral.IConfigGeneralSO;
import my.ep.biz.service.pm.common.fund.IFundSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.projectsetia.IProjectSetiaSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.votfund.IVotFundSO;
import my.ep.biz.service.pm.fee.settings.IFeeExemptionSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.fl.FulfilmentConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.confgeneral.ConfigGeneralResDTO;
import my.ep.dto.pm.common.fund.FundReqDTO;
import my.ep.dto.pm.common.fund.FundResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.votfund.VotFundReqDTO;
import my.ep.dto.pm.common.votfund.VotFundResDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionReqDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.payment.instruction.FLPaymentInstructionDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.fl.FlActor;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.FlWorkflowStatus;
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
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.GSFType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.pm.VotFundType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.ct.ContractDetailsBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBaseBackingBean;
import my.ep.web.backingbean.fl.dan.DebitAdjustmentNoteBackingBean;
import my.ep.web.backingbean.fl.deliveryorder.DeliveryOrderBackingBean;
import my.ep.web.backingbean.fl.frn.ReceivedNoteBackingBean;
import my.ep.web.backingbean.fl.invoice.InvoiceBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.backingbean.fl.stopinstr.StopInstrBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.paymentmatch.PaymentMatchManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.InvoiceChargeLineVO;
import my.ep.web.vo.TaxInvoiceVO;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;

/**
 * Payment Match Backing Bean.
 *
 * @author KK LIM
 * @since 30/10/2013
 */
@ManagedBean(name = "paymentMatchBackingBean")
@ViewScoped
public class PaymentMatchBackingBean extends FulfilmentBaseBackingBean<PaymentMatchManagedBean>{
	private static final long serialVersionUID = 1L;
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
	private IPagingDataModel<Object[]> searchVot = new SearchVotPagingDataModel();
	private IPagingDataModel<Object[]> searchFund = new SearchFundPagingDataModel();
	//21229
	private IPagingDataModel<PmOrgProfile> chargedPtjDataModel = new chargedPtjPagingDataModel();
	
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
			//32678-start
			if(flBB.getManagedBean().getRemarkList().size() >0){
				reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			}
			//32678-End
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			//start 21229
			
			//START 28246 NEW implementation: IF SentIddCre = 1 , ACTION CODE CRE already sent and next action must ADJ
			if(getManagedBean().getPaymentAdvice().getFlInvoice() !=null){				
				if( getManagedBean().getPaymentAdvice().getFlInvoice().getSentIddCre()!=null 
						&& getManagedBean().getPaymentAdvice().getFlInvoice().getSentIddCre() == 1 ){
					
					reqDTO.setActionCode(FulfilmentConst.IDD_ACTION_CODE_ADJUSTMENT);
					
				}else{
					reqDTO.setActionCode(FulfilmentConst.IDD_ACTION_CODE_CREATE);
				}
			}else{
				reqDTO.setActionCode(FulfilmentConst.IDD_ACTION_CODE_CREATE);
			}

			//To cater WIP action code: IF column SentIddCre is null
			if(reqDTO.getActionCode().equals(FulfilmentConst.IDD_ACTION_CODE_CREATE)){
				if(getManagedBean().isQueryDisplay() ){
					reqDTO.setActionCode(FulfilmentConst.IDD_ACTION_CODE_ADJUSTMENT);				
				}else{
					reqDTO.setActionCode(FulfilmentConst.IDD_ACTION_CODE_CREATE);
				}
			}
			
			log.info("paymentAdviceNo ["+ paymentAdvice.getPaymentAdviceNo() +"] actionCode[" + reqDTO.getActionCode()+"] ");
			//END 28246
			
			if(getManagedBean().getRemarkReviseList()!=null && getManagedBean().getRemarkReviseList().size() > 0){
					reqDTO.setFlRemarkRevise(getManagedBean().getRemarkReviseList().get(0));
			}
					
			//end 21229
			if(getManagedBean().getPmDigiSign() != null){
				reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			}
			
			reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
			//28221 - start
			if(getManagedBean().getNewSupplierUserId() != null){
				reqDTO.setSupplierSubmittedItd(getManagedBean().getNewSupplierUserId());
				getManagedBean().getPaymentAdvice().getFlInvoice().setSupplierSubmittedId(getManagedBean().getNewSupplierUserId());
				reqDTO.setInvoice(getManagedBean().getPaymentAdvice().getFlInvoice());
			}
			//28221 - end
			
			FulfilmentResDTO resDTO = paymentSO.createPaymentMatch(reqDTO);
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_745013;
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700004;
				}else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
					messageId = MessageConst.INFO_745021;
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
					for(FlPaymentDeduction deduction : resDTO.getPaymentAdvice().getFlPaymentDeduction()){
						CtDeduction ctDeduction = new CtDeduction();
						if(deduction.getCtDeduction() != null){
							ctDeduction.setDeductionType(deduction.getCtDeduction().getDeductionType());
							ctDeduction.setDeductionId(deduction.getCtDeduction().getDeductionId());
						}else{
							ctDeduction.setDeductionType(new PmParameter());
						}
						deduction.setCtDeduction(ctDeduction);
					}
					setAdjustmentDocNo(resDTO.getPaymentAdvice().getFlInvoice());
					setStopInstrNo(resDTO.getPaymentAdvice().getFulfilmentOrder());					
					//32026 start
					List<FlRemark> remarkList = initializeRemarkList(paymentAdvice.getPaymentAdviceId());
					flBB.initializeRemarkAndAttachement(paymentAdvice.getFlWorkflowStatus(), remarkList, resDTO.getAttachmentList());
					//32026 end
					initializeRemarkRevision(ParameterConst.DOCTYPE_PAYMENT_ADVICE, paymentAdvice.getPaymentAdviceId());//32678
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
				//32026 start
				List<FlRemark> remarkList = initializeRemarkList(paymentAdvice.getPaymentAdviceId());
				flBB.initializeRemarkAndAttachement(paymentAdvice.getFlWorkflowStatus(), remarkList, resDTO.getAttachmentList());
				//32026 end
			}
			
			//start 21229
			getManagedBean().setQueryDisplay(false);
			getManagedBean().setHideReviseRemarks(false);
			
			initializeRemarkRevision(ParameterConst.DOCTYPE_PAYMENT_ADVICE, paymentAdvice.getPaymentAdviceId());
			
			if(paymentAdvice!=null && paymentAdvice.getStatus()!= null 
					&& (paymentAdvice.getStatus().getStatusId().equals(FulfilmentStatusConst.PA_PENDING_PAYMENT_QUERY)
							|| paymentAdvice.getStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_PAYMENT_QUERY) 
							|| paymentAdvice.getStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_PAYMENT_QUERY)) ){
				getManagedBean().setButtonVal(BpmOutcomeConst.BPM_REVISE);
				
				FulfilmentReqDTO reqQueryDTO = new FulfilmentReqDTO();
				FulfilmentResDTO resQueryDTO = new FulfilmentResDTO();
				reqQueryDTO.setFlOrderId(paymentObj.getFlOrderId());			
				resQueryDTO = paymentMatchSO.retrievePaymentQuery(reqQueryDTO);
				
				if(resQueryDTO.getQueryPayment() != null){
					getManagedBean().setQueryDisplay(true);
					getManagedBean().setQueryPaymentDesc(resQueryDTO.getQueryPayment().getQueryDesc());

				}
			}
			//end 21229			
			
			for(FlPaymentDeduction deduction : paymentAdvice.getFlPaymentDeduction()){
				CtDeduction ctDeduction = new CtDeduction();
				if(deduction.getCtDeduction() != null){
					ctDeduction.setDeductionType(deduction.getCtDeduction().getDeductionType());
					ctDeduction.setDeductionId(deduction.getCtDeduction().getDeductionId());
				}else{
					ctDeduction.setDeductionType(new PmParameter());
				}
				deduction.setCtDeduction(ctDeduction);
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
				getManagedBean().setGfmasErrorCode(errRes.getErrorCode());
				getManagedBean().setGfmasErrorDesc(MessageUtils.getMessage("ERR-"+errRes.getErrorCode(), new String[]{}, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale()));
				getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
			}
		}else{
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			if(null == getManagedBean().getButtonVal() || !getManagedBean().getButtonVal().equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(false);
			}
		}
		//check is normal invoice or tax invoice
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		IInvoiceSO invoiceSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IInvoiceSO.class);
		if(getManagedBean().getPaymentAdvice() !=null){
			reqDTO.setInvoice(getManagedBean().getPaymentAdvice().getFlInvoice());
			resDTO = invoiceSO.isInvoiceTaxable(reqDTO);
			getManagedBean().setTaxInvoice(resDTO.isTaxable());
		}
		
		//28221 start
		//WIP data patch
		if (getManagedBean().getPaymentAdvice().getFlInvoice().getSupplierSubmittedId() == null) {
			getManagedBean().getPaymentAdvice().getFlInvoice()
					.setSupplierSubmittedId(getManagedBean().getPaymentAdvice().getFlInvoice().getCreatedBy());
			getManagedBean().setNewSupplierUserId(getManagedBean().getPaymentAdvice().getFlInvoice().getCreatedBy());
		}

		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setUserId(getManagedBean().getPaymentAdvice().getFlInvoice().getSupplierSubmittedId());

		UserResDTO userResDTO = userSO.retrieveUserDtlById(userReqDTO);		
		getManagedBean().setSuppInvoiceSubmitted(userResDTO.getPmUser());

		checkSupplierUserActive();
		//28221 end
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
			if(getManagedBean().getButtonVal()==null || !getManagedBean().getButtonVal().equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				validateDuplicateDeduction("");
				validateDeductionAmt();
			}
			calPmSummary();
			setClassName("@(.pmSummaryPnlClass)");
		}
		if(selectedTabIndex==4){
			getNumberOfContractOrder();
			if(getManagedBean().getButtonVal()==null || !getManagedBean().getButtonVal().equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				validateDuplicateDeduction("");
				validateDeductionAmt();
			}
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
			if(paymentDeduction.getCtDeduction().getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_ADVANCE_PAYMENT)){
				if(paymentDeduction.getCtDeduction().getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_ADVANCE_PAYMENT)){
					
				}
				currentDeductionAmt = currentDeductionAmt.add(paymentDeduction.getDeductionAmt());	
			}
		}
		ContractReqDTO ctReqDTO = new ContractReqDTO();
		ctReqDTO.setContractId(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getContractId());
		ctReqDTO.setDocId(getManagedBean().getPaymentAdvice().getFlRequest().getFulfilmentReqId());
		ContractResDTO ctRes = contractSO.calAdvancePaymentTracking(ctReqDTO);
		if(ctRes.getPaidAdvancePaymentAmt() == null){
			ctRes.setPaidAdvancePaymentAmt(new BigDecimal(0));
		}
		newPaidAmt = ctRes.getPaidAdvancePaymentAmt().add(currentDeductionAmt);
		getManagedBean().setPaidAmt(ctRes.getPaidAdvancePaymentAmt().add(currentDeductionAmt));
		if(deductionAmt.doubleValue() == 0){
			getManagedBean().setPaidPerc(new BigDecimal(0));
		}else{
			getManagedBean().setPaidPerc(newPaidAmt.divide(deductionAmt,4,RoundingMode.HALF_UP).multiply(new BigDecimal(100)));	
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
				contractReqDTO.setAsAtDate(getManagedBean().getPaymentAdvice().getFlRequest().getSubmissionDate());
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
		Map<Long, TreeNode> lvl1Map = new HashMap<Long, TreeNode>();
		BigDecimal grandTotalOrderAmt = new BigDecimal(0);
		BigDecimal grandTotalReceivedAmt = new BigDecimal(0);
		BigDecimal grandTotalInvAmt = new BigDecimal(0);
		BigDecimal grandTotalDanAmt = new BigDecimal(0);
		BigDecimal grandTotalCanAmt = new BigDecimal(0);
		Map<String, InvoiceChargeLineVO> lvl3TaxMap = null;

		TreeNode lv1TreeNode = null;
		for(FlInvoiceItemSummary invItemSummLv2 : getManagedBean().getPaymentAdvice().getFlInvoice().getFlInvoiceItemSummary()){
			if(lvl1Map.containsKey(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId())) {
				lv1TreeNode = lvl1Map.get(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId());
			} else {
				InvoiceChargeLineVO chargeLineLv1 = new InvoiceChargeLineVO();
				chargeLineLv1.setLevel(1);
				chargeLineLv1.setFlInvoiceSummary(invItemSummLv2);
				
				lv1TreeNode = new DefaultTreeNode(chargeLineLv1, getManagedBean().getChargeLineItemRoot());
				lvl1Map.put(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId(), lv1TreeNode);
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
			if(lvl3TaxMap == null){
				lvl3TaxMap = new HashMap<String, InvoiceChargeLineVO>();
			}else{
				lvl3TaxMap.clear();
			}
			InvoiceChargeLineVO chargeLineLv2 = new InvoiceChargeLineVO();
			chargeLineLv2.setLevel(2);
			chargeLineLv2.setFlInvoiceSummary(invItemSummLv2);
			TreeNode lv2TreeNode = new DefaultTreeNode(chargeLineLv2, lv1TreeNode);
			for(FlInvoiceItem invItemLv3 : invItemSummLv2.getFlInvoiceItem()){
				if(lvl3TaxMap.containsKey(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode())) {
					InvoiceChargeLineVO sameInvItemObj = lvl3TaxMap.get(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode());
					sameInvItemObj.getFlInvoiceItem().setTotalReceivedQty(sameInvItemObj.getFlInvoiceItem().getTotalReceivedQty().add(invItemLv3.getReceivedQty()));
					sameInvItemObj.getFlInvoiceItem().setTotalReceivedAmt(sameInvItemObj.getFlInvoiceItem().getTotalReceivedAmt().add(invItemLv3.getReceivedAmt()));
					lvl3TaxMap.put(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode(),sameInvItemObj);
				}else{
					InvoiceChargeLineVO chargeLineLv3 = new InvoiceChargeLineVO();
					chargeLineLv3.setFlInvoiceSummary(invItemSummLv2);
					chargeLineLv3.setLevel(3);
					invItemLv3.setTotalReceivedQty(invItemLv3.getReceivedQty());
					invItemLv3.setTotalReceivedAmt(invItemLv3.getReceivedAmt());
					chargeLineLv3.setFlInvoiceItem(invItemLv3);
					lvl3TaxMap.put(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode(),chargeLineLv3);
				}
				grandTotalReceivedAmt = grandTotalReceivedAmt.add(invItemLv3.getReceivedAmt());
			}
			for(Entry<String, InvoiceChargeLineVO> entrySet : lvl3TaxMap.entrySet()){
				new DefaultTreeNode(entrySet.getValue(), lv2TreeNode);
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
		IRuleSO ruleSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);	
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
			if(getManagedBean().getPaymentAdvice().getFlRequest().getGstRate() == null){
				getManagedBean().getPaymentAdvice().getFlRequest().setGstRate(BigDecimal.ZERO);
			}
			BigDecimal divideRate = getManagedBean().getPaymentAdvice().getFlRequest().getGstRate().add(BigDecimal.valueOf(100));
			BigDecimal gstAmount = getManagedBean().getPaymentAdvice().getFlRequest().getGstRate().multiply(gstTotalAmount).divide(divideRate, CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE, RoundingMode.HALF_UP);
			getManagedBean().setGstAmount(gstAmount);
		} else {
			getManagedBean().setGstAmount(BigDecimal.ZERO);
		}
		getManagedBean().setInvoiceAmountWithoutGST(invoiceTotalAmount.subtract(getManagedBean().getGstAmount()));
		BigDecimal serviceFee = new BigDecimal(0);
		if(getManagedBean().getPaymentAdvice().getFlRequest().getFlFulfilmentOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
			RuleReqDTO req = new RuleReqDTO();
			req.setRuleType(RuleTypeConst.TRANSACTION_FEE_ONE_OFF);
			serviceFee = new BigDecimal(ruleSO.getRuleValuesByTypeInCurrentDate(req).getRuleValueList().get(0).getRuleValue());
		} else {
			if(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getServiceFee() != null){
				serviceFee = getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getServiceFee();
			}
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
			for(FlInvoiceItemSummary invSummary : getManagedBean().getPaymentAdvice().getFlInvoice().getFlInvoiceItemSummary()){
				for(FlInvoiceItem invItem : invSummary.getFlInvoiceItem()){
					if(invSummary.getFlFulfilmentItem().getCmItem() != null){
						reqDTO.setItemId(invSummary.getFlFulfilmentItem().getCmItem().getItemId());
					}
					reqDTO.setRequestItemId(invSummary.getFlFulfilmentItem().getFlFulfilmentItemAddr().get(0).getScRequestItem().getRequestNoteItemId());
					reqDTO.setOrgProfileId(getManagedBean().getPaymentAdvice().getFlRequest().getSmSupplier().getSupplierId());
					if(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract() != null){
						reqDTO.setContractId(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getContractId());
					}
					resDTO = feeExemptionSO.checkForFeeException(reqDTO);

					if(resDTO.getFeeExemptionResult() == null ||resDTO.getFeeExemptionResult() == false){//item not exempted
						if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.STANDARD.value())){
							if(getManagedBean().getPaymentAdvice().getFlRequest().getGstRate() == null){
								getManagedBean().getPaymentAdvice().getFlRequest().setGstRate(BigDecimal.ZERO);
							}
							Double recalculateServiceFee = 100 + getManagedBean().getPaymentAdvice().getFlRequest().getGstRate().doubleValue();
							BigDecimal serviceGstAmount = (new BigDecimal((invItem.getInvAmt().doubleValue() * getManagedBean().getPaymentAdvice().getFlRequest().getGstRate().doubleValue()) / recalculateServiceFee));
							nonExemptedItemList.add(invItem.getInvAmt().subtract(serviceGstAmount));
						}else{
							nonExemptedItemList.add(invItem.getInvAmt()); 
						}
					}
				}
			}
		}
		
		BigDecimal nonExemptedItemAmt = new BigDecimal(0);
		if(!nonExemptedItemList.isEmpty()){
			for(BigDecimal normalItemAmt : nonExemptedItemList){
				nonExemptedItemAmt = nonExemptedItemAmt.add(normalItemAmt);
			}
		}else{
			serviceFee = new BigDecimal(0);
		}

		getManagedBean().setServiceCharge(Double.toString(serviceFee.doubleValue()));

		BigDecimal percentage = new BigDecimal(100);
		BigDecimal invMulty = new BigDecimal(0.0);

		boolean haveDeduction = false;
		BigDecimal calPaymentAmt = new BigDecimal(0.0);
		BigDecimal totalDeductionAmt = new BigDecimal(0);
		for(FlPaymentDeduction deduction : getManagedBean().getPaymentAdvice().getFlPaymentDeduction()) {
			if(deduction.getCtDeduction() == null){
				break;
			}
			if(deduction.getCtDeduction().getDeductionType().getParameterCode()!=null && 
					!deduction.getCtDeduction().getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_TRANSACTION_FEE)) {
				if(deduction.getCtDeduction().getDeductionType().getParameterCode()!=null && 
						!deduction.getCtDeduction().getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_TRANSACTION_FEE)){
					
				}
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

			calPaymentAmt=calPaymentAmt.add(getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceAmtNew().subtract(totalDeductionAmt));
		}else if(haveDeduction && nonExemptedItemAmt.doubleValue() <= 0){
			if(totalDeductionAmt != null && totalDeductionAmt.doubleValue() > 0){
				getManagedBean().getPaymentAdvice().setFeeAmt(transFee);
				getManagedBean().setDeductionAmt(totalDeductionAmt);
				calPaymentAmt=calPaymentAmt.add(getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceAmtNew().subtract(totalDeductionAmt));
			}
		}else if(nonExemptedItemAmt.doubleValue() >= 0 && haveDeduction == false){
			if(nonExemptedItemAmt.doubleValue() > 0){
				invMulty = nonExemptedItemAmt.multiply(serviceFee);
				transFee = invMulty.divide(percentage,2,RoundingMode.HALF_UP);
				getManagedBean().getPaymentAdvice().setFeeAmt(transFee);
				getManagedBean().setDeductionAmt(totalDeductionAmt);
				calPaymentAmt=calPaymentAmt.add(getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceAmtNew());
				if(totalDeductionAmt.doubleValue() > 0){
					calPaymentAmt=calPaymentAmt.subtract(totalDeductionAmt);
				}

			} else if(nonExemptedItemAmt.doubleValue() <= 0){
				calPaymentAmt=getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceAmtNew();
			}
		} 
		
		BigDecimal ruleFee = new BigDecimal(0);
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		if(getManagedBean().getPaymentAdvice().getFulfilmentOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
			ruleReqDTO.setRuleType(FulfilmentConst.FLONE);
			ruleFee = new BigDecimal(ruleSO.getRuleValuesByTypeInCurrentDate(ruleReqDTO).getRuleValueList().get(0).getRuleValue());
			if(getManagedBean().getPaymentAdvice().getFeeAmt().doubleValue() > ruleFee.doubleValue()){
				getManagedBean().getPaymentAdvice().setFeeAmt(ruleFee);
			}
		}else{
			if(getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF)){
				ruleReqDTO.setRuleType(FulfilmentConst.FLONE);
				ruleFee = new BigDecimal(ruleSO.getRuleValuesByTypeInCurrentDate(ruleReqDTO).getRuleValueList().get(0).getRuleValue());
				if(getManagedBean().getPaymentAdvice().getFeeAmt().doubleValue() > ruleFee.doubleValue()){
					getManagedBean().getPaymentAdvice().setFeeAmt(ruleFee);
				}
			}else{
				ruleReqDTO.setRuleType(FulfilmentConst.FLPER);
				ruleFee = new BigDecimal(ruleSO.getRuleValuesByTypeInCurrentDate(ruleReqDTO).getRuleValueList().get(0).getRuleValue());
				if(getManagedBean().getPaymentAdvice().getFeeAmt().doubleValue() > ruleFee.doubleValue()){
					getManagedBean().getPaymentAdvice().setFeeAmt(ruleFee);
				}
			}
		}
		
		getManagedBean().getPaymentAdvice().setPaymentAmt(calPaymentAmt.subtract(getManagedBean().getPaymentAdvice().getFeeAmt()));
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
						taxInvVO.setAmountWithoutGst(new BigDecimal(taxInvVO.getAmountWithoutGst()).add(invItem.getInvAmt()).toString());
						taxAmountMap.put(invItem.getGstFlag().getParameterCode(), taxInvVO);
					}else{
						TaxInvoiceVO taxInvVO = new TaxInvoiceVO();
						taxInvVO.setAmountWithoutGst(invItem.getInvAmt().toString());
						if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.STANDARD.value())){
							if(getManagedBean().getPaymentAdvice().getFlRequest().getGstRate() == null){
								getManagedBean().getPaymentAdvice().getFlRequest().setGstRate(BigDecimal.ZERO);
							}
							taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = " +getManagedBean().getPaymentAdvice().getFlRequest().getGstRate().setScale(2,RoundingMode.HALF_UP)+"%");
							taxInvVO.setGstAmount(getManagedBean().getGstAmount().toString());	
						}else if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.ZERO_RATED.value())){
							taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = 0%");
							taxInvVO.setGstAmount("0.00");	
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
					taxInvVO.setAmountWithoutGst(invItem.getInvAmt().toString());
					if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.STANDARD.value())){
						if(getManagedBean().getPaymentAdvice().getFlRequest().getGstRate() == null){
							getManagedBean().getPaymentAdvice().getFlRequest().setGstRate(BigDecimal.ZERO);
						}
						taxInvVO.setGstAmount(getManagedBean().getGstAmount().toString());	
						taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = " +getManagedBean().getPaymentAdvice().getFlRequest().getGstRate().setScale(2,RoundingMode.HALF_UP)+"%");
					}else if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.ZERO_RATED.value())){
						taxInvVO.setGstAmount("0.00");	
						taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = 0%");
					}else {//RS n OS
						taxInvVO.setGstAmount("Nil");	
						taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = Nil");
					}
					taxInvVO.setGstFlagCode(invItem.getGstFlag().getParameterCode());
					taxAmountMap.put(invItem.getGstFlag().getParameterCode(), taxInvVO);
				}
			}
		}
		getManagedBean().setTaxInvoiceList(new ArrayList<TaxInvoiceVO>());
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat amountDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		if(taxAmountMap.containsKey(GSFType.STANDARD.value())){
			TaxInvoiceVO taxInvoice = taxAmountMap.get(GSFType.STANDARD.value());
			BigDecimal amtWithoutGst = (new BigDecimal(taxInvoice.getAmountWithoutGst())).subtract(getManagedBean().getGstAmount()) ;
			taxInvoice.setAmountWithoutGst(amountDF.format(amtWithoutGst));
			BigDecimal amtGst = new BigDecimal(taxInvoice.getGstAmount());
			taxInvoice.setGstAmount(amountDF.format(amtGst));
			getManagedBean().getTaxInvoiceList().add(taxInvoice);
		}else{
			TaxInvoiceVO taxInvoice = new TaxInvoiceVO();
			taxInvoice.setAmountWithoutGst("0.00");
			taxInvoice.setGstAmount("0.00");
			if(getManagedBean().getPaymentAdvice().getFlRequest().getGstRate() == null){
				getManagedBean().getPaymentAdvice().getFlRequest().setGstRate(BigDecimal.ZERO);
			}
			taxInvoice.setGstFlag(GSFType.STANDARD.value()+" = " +getManagedBean().getPaymentAdvice().getFlRequest().getGstRate().setScale(2,RoundingMode.HALF_UP)+"%");
			getManagedBean().getTaxInvoiceList().add(taxInvoice);
		}
		if(taxAmountMap.containsKey(GSFType.ZERO_RATED.value())){
			TaxInvoiceVO taxInvoice = taxAmountMap.get(GSFType.ZERO_RATED.value());
			BigDecimal amtWithoutGst = new BigDecimal(taxInvoice.getAmountWithoutGst());
			taxInvoice.setAmountWithoutGst(amountDF.format(amtWithoutGst));
			BigDecimal amtGst = new BigDecimal(taxInvoice.getGstAmount());
			taxInvoice.setGstAmount(amountDF.format(amtGst));
			getManagedBean().getTaxInvoiceList().add(taxInvoice);
		}else{
			TaxInvoiceVO taxInvoice = new TaxInvoiceVO();
			taxInvoice.setAmountWithoutGst("0.00");
			taxInvoice.setGstAmount("0.00");
			taxInvoice.setGstFlag(GSFType.ZERO_RATED.value()+" = 0%");
			getManagedBean().getTaxInvoiceList().add(taxInvoice);
		}
		if(taxAmountMap.containsKey(GSFType.RELIEF.value())){
			TaxInvoiceVO taxInvoice = taxAmountMap.get(GSFType.RELIEF.value());
			BigDecimal amtWithoutGst = new BigDecimal(taxInvoice.getAmountWithoutGst());
			taxInvoice.setAmountWithoutGst(amountDF.format(amtWithoutGst));
			getManagedBean().getTaxInvoiceList().add(taxInvoice);
		}else{
			TaxInvoiceVO taxInvoice = new TaxInvoiceVO();
			taxInvoice.setAmountWithoutGst("Nil");
			taxInvoice.setGstAmount("Nil");
			taxInvoice.setGstFlag(GSFType.RELIEF.value()+" = Nil");
			getManagedBean().getTaxInvoiceList().add(taxInvoice);
		}
		if(taxAmountMap.containsKey(GSFType.OUT_OF_SCOPE.value())){
			TaxInvoiceVO taxInvoice = taxAmountMap.get(GSFType.OUT_OF_SCOPE.value());
			BigDecimal amtWithoutGst = new BigDecimal(taxInvoice.getAmountWithoutGst());
			taxInvoice.setAmountWithoutGst(amountDF.format(amtWithoutGst));
			getManagedBean().getTaxInvoiceList().add(taxInvoice);
		}else{
			TaxInvoiceVO taxInvoice = new TaxInvoiceVO();
			taxInvoice.setAmountWithoutGst("Nil");
			taxInvoice.setGstAmount("Nil");
			taxInvoice.setGstFlag(GSFType.OUT_OF_SCOPE.value()+" = Nil");
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
						getManagedBean().setVotFundType(childData.getPmVotFund().getVotFundType());
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
						getManagedBean().setFinancingTypeCode(childData.getPmSubSetia().getFinancingTypeValue());
					}
					//24893 - start
					if(null != childData.getFlFulfilmentItemAddr().get(0).getChargePTJOrg()) {
						getManagedBean().setChargePTJOrg(childData.getFlFulfilmentItemAddr().get(0).getChargePTJOrg());
						System.out.println("Item Charge PTJ");
					} else {
						getManagedBean().setChargePTJOrg(getManagedBean().getPaymentAdvice().getFlRequest().getChargeOrgProfile());
						System.out.println("FL Request Charge PTJ");
					}
					//24893 - end
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
		//24893 - start
		getManagedBean().setChargePTJOrg(new PmOrgProfile());
		//24893 - end
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
		paymentDeduction.setCtDeduction(new CtDeduction());
		paymentDeduction.getCtDeduction().setDeductionType(new PmParameter());
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
		if(deduction.getCtDeduction().getDeductionId() == null){
			getManagedBean().setCtDeductionClauseList(null);
		}
		if(deduction.getCtDeduction().getDeductionType().getParameterCode() != null){
			for(CtDeduction ctDeduction : getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getLatestContractVer().getCtDeductions()){
				if(ctDeduction.getDeductionType().getParameterCode().equals(deduction.getCtDeduction().getDeductionType().getParameterCode())){
					if(deduction.getCtDeduction().getDeductionId() != null){
						if(ctDeduction.getDeductionId().equals(deduction.getCtDeduction().getDeductionId())){
							getManagedBean().setCtDeductionClauseList(ctDeduction.getCtDeductionClauses());
						}
					}
				}
			}
		}
	}
	
	/**
	 * #32678-start
	 * Page navigation to FLDoc search page in PA.
	 *
	 * @return the string
	 * @throws PortalException the portal exception
	 * @throws SystemException the system exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String backToPervious() throws PortalException, SystemException, IOException {
		if(getManagedBean().isDocMaintain()) {
			return "flDocSearch";
		}else if(getManagedBean().isHyperLinkPage()){
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.getManagedBean().setRemarkList(getManagedBean().getPreviousRemarkList());
			flBB.getManagedBean().setAttachmentList(getManagedBean().getPreviousAttachmentList());
			flBB.getManagedBean().setHideRemarks(getManagedBean().isPrevHideRemarks());
			return getManagedBean().getBackToPrevious();
		} else {
			String commonUrl = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_APPLICATION_URL);
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.redirect(commonUrl + "notifications");
			return null;
		}
	}
	//32678-end
	
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
		CtDeduction mapDeduction  = null;
		if(deduction.getCtDeduction().getDeductionId() != null){
			if(getManagedBean().getCtDeductionMap().containsKey(deduction.getCtDeduction().getDeductionId())){
				mapDeduction = getManagedBean().getCtDeductionMap().get(deduction.getCtDeduction().getDeductionId());
			}
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(mapDeduction.getDeductionType().getParameterId());
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			deduction.getCtDeduction().setDeductionType(resDTO.getPmParameter());
		}
		if(mapDeduction != null){
			if(mapDeduction.getDeductionDesc() != null){
				deduction.setDeductionDesc(mapDeduction.getDeductionDesc());
			}
		}else{
			deduction.setDeductionDesc("");
		}
		
//			deduction.setPmGlAccount(null);
//			deduction.setPmVotFund(null);
		if(deduction.getCtDeduction().getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_FIXED)){
			for(CtDeduction ctDeduction : getManagedBean().getPaymentAdvice().getFlRequest().getCtContract().getLatestContractVer().getCtDeductions()){
				if(ctDeduction.getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_FIXED)){
					if(deduction.getCtDeduction().getDeductionId() != null){
						if(ctDeduction.getDeductionId().equals(deduction.getCtDeduction().getDeductionId())){
							deduction.setDeductionAmt(new BigDecimal(ctDeduction.getAmount()));
						}
					}else{
						deduction.setDeductionAmt(new BigDecimal(0));
					}
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
						if(getManagedBean().getCtDeductionMap().get(obj.getDeductionId()) != null){
							continue;
						}else{
							getManagedBean().getCtDeductionMap().put(obj.getDeductionId(), obj);
						}
						if(obj.getDeductionDesc() == null){
							getManagedBean().getPopulateDeductionList().add(new SelectItem(obj.getDeductionId(),getParameterDesc(obj.getDeductionType().getParameterType(), obj.getDeductionType().getParameterCode())));
						}else{
							getManagedBean().getPopulateDeductionList().add(new SelectItem(obj.getDeductionId(),getParameterDesc(obj.getDeductionType().getParameterType(), obj.getDeductionType().getParameterCode())+" - "+obj.getDeductionDesc()));
						}
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
					if(deduction.getCtDeduction() == null){
						break;
					}
					if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){//save function
						if(deduction.getCtDeduction().getDeductionType().getParameterCode() == null && 
								(deduction.getPmVotFund() == null || deduction.getPmVotFund().getVotFundCode() == null) &&
								(deduction.getPmGlAccount() == null || deduction.getPmGlAccount().getGlAccCode() == null)){
							emptyDeductionList.add(deduction);
						}else if(deduction.getCtDeduction().getDeductionType().getParameterCode() == null){
							throw new EpException(MessageConst.ERROR_740035);
						}
						if(deduction.getDeductionAmt() == null){
							deduction.setDeductionAmt(new BigDecimal(0));
						}
					}else {
						if(deduction.getCtDeduction().getDeductionType().getParameterCode() == null){
							throw new EpException(MessageConst.ERROR_740035);
						}

						if(deduction.getCtDeduction().getDeductionId() == null){
							throw new EpException(MessageConst.ERROR_740035);
						}
						if(deduction.getCtDeduction().getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_OTHERS)){
							if(deduction.getDeductionDesc().trim().equalsIgnoreCase("") || deduction.getDeductionDesc() == null){
								searchBean.getDataFilterBeans().add(new SearchFilterBean(
										"pmParameter.parameterCode",
										SearchOperator.OPERATOR_EQUALS.value(),
										deduction.getCtDeduction().getDeductionType().getParameterCode()));
								reqType.setParameterDescSearchBean(searchBean);
								resType = parameterSO.retrieveParameterDescListByType(reqType);
								throw new EpException(MessageConst.ERROR_745058,new String[]{resType.getParameterDescList().get(0).getCodeName()});
							}
						}
						if(deduction.getPmVotFund() == null || deduction.getPmVotFund().getVotFundCode() == null){
							searchBean.getDataFilterBeans().add(new SearchFilterBean(
									"pmParameter.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									deduction.getCtDeduction().getDeductionType().getParameterCode()));
							reqType.setParameterDescSearchBean(searchBean);

							resType = parameterSO.retrieveParameterDescListByType(reqType);
							throw new EpException(MessageConst.ERROR_745059,new String[]{resType.getParameterDescList().get(0).getCodeName()});
						}
						if(deduction.getPmGlAccount() == null || deduction.getPmGlAccount().getGlAccCode() == null){
							searchBean.getDataFilterBeans().add(new SearchFilterBean(
									"pmParameter.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									deduction.getCtDeduction().getDeductionType().getParameterCode()));
							reqType.setParameterDescSearchBean(searchBean);

							resType = parameterSO.retrieveParameterDescListByType(reqType);
							throw new EpException(MessageConst.ERROR_745060,new String[]{resType.getParameterDescList().get(0).getCodeName()});
						}
						if(deduction.getDeductionAmt() == null || deduction.getDeductionAmt().doubleValue() <= 0){
							searchBean.getDataFilterBeans().add(new SearchFilterBean(
									"pmParameter.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									deduction.getCtDeduction().getDeductionType().getParameterCode()));
							reqType.setParameterDescSearchBean(searchBean);

							resType = parameterSO.retrieveParameterDescListByType(reqType);
							throw new EpException(MessageConst.ERROR_745061,new String[]{resType.getParameterDescList().get(0).getCodeName()});
						}
						String unique = deduction.getCtDeduction().getDeductionId().toString() +deduction.getPmVotFund().getVotFundCode() +deduction.getPmGlAccount().getGlAccCode();
						if(deductionList.contains(unique)){
							throw new EpException(MessageConst.ERROR_740036);
						}else{
							deductionList.add(deduction.getCtDeduction().getDeductionId().toString() +deduction.getPmVotFund().getVotFundCode() +deduction.getPmGlAccount().getGlAccCode());
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
		if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_MATCH ) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
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
			if(paymentAdvice.getDescription() == null || paymentAdvice.getDescription().trim().isEmpty()){
				if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					paymentAdvice.setDescription(" ");
				}else{
					getManagedBean().setSelectedTabIndex(0);
					throw new EpException(MessageConst.ERROR_100022, new String[]{
							ResourceBundleUtils.getMessage("lbl.fl.payment.match.payment.desc",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
				}
			}
			validateDuplicateDeduction(outcome);
			validateDeductionAmt();
			if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_MATCH)){
				calPmSummary();
			}
			
			paymentSO.validatePaymentMatch(reqDTO);
				
			//start 21229
			if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) { 
				if(getManagedBean().getRemarkReviseList().size() > 0){
					
					FlRemark remarkRevise = getManagedBean().getRemarkReviseList().get(0);
					if(remarkRevise.getRemark() == null || remarkRevise.getRemark().isEmpty()){
						getManagedBean().setSelectedTabIndex(0);
						throw new EpException(MessageConst.ERROR_100022, new String[]{
								ResourceBundleUtils.getMessage("lbl.txt.catatankajisemula",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
					}
					
				}
			}
			
			//28221
			if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) 
					&& !getManagedBean().isSuppInvoiceActive() && getManagedBean().getNewSupplierUserId() == null){
				getManagedBean().setSelectedTabIndex(0);
				throw new EpException(MessageConst.ERROR_740065, new String[]{getManagedBean().getSuppInvoiceSubmitted().getUserName()});
			}
			
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
			
			//if(paymentAdvice.getFlRequest().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
				if(getManagedBean().getTaskId()==null) {
					if(getManagedBean().getPaymentAdvice().getFlPaymentDeduction().size()>0) {
						getManagedBean().setShowDeductionTab(false);
						for(FlPaymentDeduction deduction : getManagedBean().getPaymentAdvice().getFlPaymentDeduction()){
							if(deduction.getCtDeduction() != null){
								getManagedBean().setShowDeductionTab(true);
								break;
							}
						}
					} 
				} else { // on going task
					if(paymentAdvice.getFlRequest().getCtContract().getLatestContractVer().getCtDeductions().size()>0) { 
						getManagedBean().setShowDeductionTab(true);
					} else if(getManagedBean().getPaymentAdvice().getFlPaymentDeduction().size()>0) { // clear payment deduction table if already exist
						getManagedBean().getPaymentAdvice().getFlPaymentDeduction().clear();
					} 
				}
			//}
			
			for(CtDeduction deduction : paymentAdvice.getFlRequest().getCtContract().getLatestContractVer().getCtDeductions()){
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
			
			if(getManagedBean().getPaymentAdvice().getFlPaymentDeduction() != null && !getManagedBean().getPaymentAdvice().getFlPaymentDeduction().isEmpty()){
				IProjectSetiaSO projectSetiaSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IProjectSetiaSO.class);
				ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
				ProjectSetiaResDTO resDTO = new ProjectSetiaResDTO();
				for(FlPaymentDeduction deduction : getManagedBean().getPaymentAdvice().getFlPaymentDeduction()){
					if(deduction.getPmVotFund() != null){
					if(deduction.getPmVotFund().getVotFundType().equalsIgnoreCase(VotFundType.AMANAH.value())
							|| deduction.getPmVotFund().getVotFundType().equalsIgnoreCase(VotFundType.BEKALAN.value()) 
							|| deduction.getPmVotFund().getVotFundType().equalsIgnoreCase(VotFundType.TANGGUNGAN.value())){
						deduction.setEnableCp(true);
					}else{
						deduction.setEnableCp(false);
					}
					if(deduction.getPmVotFund().getVotFundId() != null){
						reqDTO.setVotFundId(deduction.getPmVotFund().getVotFundId());
						resDTO = projectSetiaSO.retrieveSubSetiaFinancingTypeListByVotFundId(reqDTO);
						if(getManagedBean().getPopulateSubSetiaList()== null || getManagedBean().getPopulateSubSetiaList().isEmpty()){
							if (resDTO.getFinancingTypeValueList() != null) {
								for (String subSetia : resDTO.getFinancingTypeValueList()) {
									getManagedBean().getPopulateSubSetiaList().add(new SelectItem(subSetia,subSetia));
								}
							}
						}
					}
				}
			  }
			}
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
			if(deduction.getCtDeduction() == null){
				return null;
			}
			//start 21229
			if(deduction.getOrgProfile()!=null){
				deduction.getOrgProfile().getOrgCode();
			}
			//end 21229
			
			if(deduction.getCtDeduction().getDeductionType().getParameterCode() == null){
				deductionTable.add(deduction);
			}else if(!deduction.getCtDeduction().getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_TRANSACTION_FEE)){
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
			if(deduction.getCtDeduction() == null){
				break;
			}
			if(deduction.getCtDeduction().getDeductionType().getParameterCode()!=null && 
					!deduction.getCtDeduction().getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_TRANSACTION_FEE)) {
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
			orderBackingBean.getManagedBean().setSelectedTabIndex(0);
			orderBackingBean.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
			orderBackingBean.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			FacesContext fc = FacesContext.getCurrentInstance();
			orderBackingBean.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
			orderBackingBean.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
			orderBackingBean.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());
			orderBackingBean.getManagedBean().setHyperLinkPage(true);
			orderBackingBean.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			orderBackingBean.getManagedBean().setPrevHideRemarks(flBB.getManagedBean().isHideRemarks());
			flBB.getManagedBean().setHideRemarks(true);
			flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
			flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			if(resDTO.getFlOrder().isFactored()) {
				orderBackingBean.getManagedBean().setFactoringCheck("true");
			} else {
				orderBackingBean.getManagedBean().setFactoringCheck("false");
			}
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
			adjBB.getManagedBean().setSelectedTabIndex(0);
			adjBB.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
			adjBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
			adjBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());
			adjBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			adjBB.getManagedBean().setHyperLinkPage(true);
			adjBB.getManagedBean().setPrevHideRemarks(flBB.getManagedBean().isHideRemarks());
			flBB.getManagedBean().setHideRemarks(true);
			flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
			flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
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
			adjBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
			adjBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());
			adjBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			adjBB.getManagedBean().setHyperLinkPage(true);
			adjBB.getManagedBean().setPrevHideRemarks(flBB.getManagedBean().isHideRemarks());
			flBB.getManagedBean().setHideRemarks(true);
			flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
			flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			return NAV_ADJ;
		}else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_STOP_DELIVERY)){
			IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
			FulfilmentResDTO resDTO = orderSO.retrieveStopDeliveryByDocNo(getManagedBean().getStopInstrNo());
			StopInstrBackingBean siBB = new StopInstrBackingBean();
			siBB.getManagedBean().setStopInstr(resDTO.getFlStopInstr());
			siBB.populateChargeLineData();
			FacesContext fc = FacesContext.getCurrentInstance();
			siBB.getManagedBean().setSelectedTabIndex(0);
			siBB.getManagedBean().setBackToDocument(fc.getViewRoot().getViewId());
			siBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
			siBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());
			siBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			siBB.getManagedBean().setHyperLinkPage(true);
			siBB.getManagedBean().setPrevHideRemarks(flBB.getManagedBean().isHideRemarks());
			flBB.getManagedBean().setHideRemarks(true);
			flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
			flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
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
			invBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			invBB.getManagedBean().setReliefDeclarationCheck(true);
			//start - 32678
			invBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
			invBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());
			invBB.getManagedBean().setPrevHideRemarks(flBB.getManagedBean().isHideRemarks());
			flBB.getManagedBean().setHideRemarks(true);
			flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
			flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			//end - 32678
			reqDTO.setInvoice(resDTO.getInvoice());
			resDTO = invoiceSO.isInvoiceTaxable(reqDTO);
			invBB.getManagedBean().setTaxable(resDTO.isTaxable());
			invBB.getManagedBean().setTaxInvoice(resDTO.isTaxable());
			
			FacesContext fc = FacesContext.getCurrentInstance();
			invBB.getManagedBean().setSelectedTabIndex(0);
			invBB.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
			invBB.getManagedBean().setDocMaintain(false);
			invBB.getManagedBean().setDelOrderList(new ArrayList<FlDeliveryOrder>()); 
			for(FlDeliveryOrder flDeliveryOrder : invBB.getManagedBean().getInvoice().getFlFulfilmentOrder().getFlDeliveryOrders()) {
				if(flDeliveryOrder.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
					invBB.getManagedBean().getDelOrderList().add(flDeliveryOrder);
				}
			}		
			return NAV_INVOICE;
		}if(docType.equalsIgnoreCase(ParameterConst.CONTRACT)){
			ContractDetailsBackingBean ctDetBB = new ContractDetailsBackingBean();
			FacesContext fc = FacesContext.getCurrentInstance();
			ctDetBB.getManagedBean().setGotoPreviewPage(fc.getViewRoot().getViewId()+"?faces-redirect=true");
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
			doBB.getManagedBean().setSelectedTabIndex(0);
			doBB.getManagedBean().setFlDeliveryOrder(resDTO.getFlDeliveryOrder());
			doBB.getManagedBean().setFulfilmentRequest(resDTO.getFlDeliveryOrder().getFlRequest());
			doBB.getManagedBean().setFulfilmentOrder(resDTO.getFlDeliveryOrder().getFlFulfilmentOrder());
			doBB.getManagedBean().setFromPageUrl(fc.getViewRoot().getViewId());
			doBB.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
			doBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
			doBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());		
			doBB.getManagedBean().getFulfilmentOrder().setStatus(resDTO.getFlDeliveryOrder().getStatus());
			doBB.getManagedBean().setHyperLinkPage(true);
			doBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			doBB.getManagedBean().setPrevHideRemarks(flBB.getManagedBean().isHideRemarks());
			flBB.getManagedBean().setHideRemarks(true);
			flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
			flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
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
			IFulfilmentSO iFulfilmentSO = (IFulfilmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentSO.class);
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
			recNoteBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			reqDTO.setDeliveryOrderId(resDTO.getFulfilmentNote().getFlDeliveryOrder().getDeliveryOrderId());
			recNoteBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			for(FlFulfilmentNoteItem fulfilmentNoteItem: resDTO.getFulfilmentNote().getFlFulfilmentNoteItem()){
				recNoteBB.getManagedBean().setSelectedItem(fulfilmentNoteItem);
			}
			recNoteBB.getManagedBean().getFulfilmentNote().setFlFulfilmentNoteItem(resDTO.getFulfilmentNote().getFlFulfilmentNoteItem());
			if(recNoteBB.getManagedBean().getFlApprover() == null){
				reqDTO.setFlRequestId(recNoteBB.getManagedBean().getFulfilmentNote().getFlRequest().getFulfilmentReqId());
				FlActor actor = iFulfilmentSO.retrieveFlActors(recNoteBB.getManagedBean().getFulfilmentNote().getFlRequest().getDocType(), recNoteBB.getManagedBean().getFulfilmentNote().getFlRequest().getFulfilmentReqId(), RoleCode.FL_APPROVER.value());
				if(actor != null){
					recNoteBB.getManagedBean().setFlApprover(actor.getUser());
					recNoteBB.getManagedBean().setAcknowledgeDate(actor.getActionDate());
				}
			}
			FacesContext fc = FacesContext.getCurrentInstance();
			recNoteBB.getManagedBean().setFromPageUrl(fc.getViewRoot().getViewId());
			recNoteBB.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
			recNoteBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
			recNoteBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());	
			recNoteBB.getManagedBean().setAcknowledged(true);
			recNoteBB.getManagedBean().setHyperLinkPage(true);
			recNoteBB.getManagedBean().setPrevHideRemarks(flBB.getManagedBean().isHideRemarks());
			flBB.getManagedBean().setHideRemarks(true);
			flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
			flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			for(FlFulfilmentNoteItem noteItem : recNoteBB.getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem()){
				if(noteItem.getFulfilmentNoteItemId() != null){
					reqDTO.setDocId(noteItem.getFulfilmentNoteItemId());
					reqDTO.setDocType(ParameterConst.DOCTYPE_FULFILMENT_NOTE_ITEM);
					resDTO = receivedNoteSO.retrieveRejectionRemarksHistory(reqDTO);
					noteItem.setRejectionRemarks(resDTO.getFNIRejectionRemarks());
					recNoteBB.initializeRejectionRemarks(recNoteBB.getManagedBean().getFulfilmentNote().getFlWorkflowStatus() ,resDTO.getFNIRejectionRemarks(),noteItem,false);
				}else{
					noteItem.getRejectionRemarks().add(new FlRemark());
				}
			}
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
			}finally{
				paymentSO = null;
			}
			
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			getManagedBean().setSelectedTabIndex(0);
//			getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
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
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "pmBasePage?faces-redirect=true");
		}
	}
	
	/**
	 * retrieve VOT and Fund values
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getDropdownVOTFund() {

		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		try {
			List<String> votFundType = new ArrayList<String>();
			votFundType.add(ResourceBundleUtils.getMessage(
					"lbl.gfmas.gldanadef", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			votFundType.add(ResourceBundleUtils.getMessage(
					"lbl.gfmas.glvotdef", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			
			items = new SelectItem[votFundType.size()+1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (String votFund : votFundType) {
				items[i++] = new SelectItem(i,votFund);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}
	
	/**
	 * search action Vot Fund
	 */
	public void searchAction(){
		if(getManagedBean().getSelectedVotFundId() != null){
			if(getManagedBean().getSelectedVotFundId() == 2){//Dana
				getSearchFund().reloadData();
			}else if(getManagedBean().getSelectedVotFundId() == 3){// Vot
				getSearchVot().reloadData();
			}
		}
		
	}
	
	/**
	 * reset action
	 */
	public void resetAction(){
		getManagedBean().setSelectedFundObj(null);
		getManagedBean().setSelectedVotObj(null);
		getSearchVot().resetData();
		getSearchFund().resetData();
		getManagedBean().setSelectedVotFundId(null);
		getManagedBean().setSelectedVotValue(null);
		getManagedBean().setSearchVotField(null);
		getManagedBean().setSelectedPmOrgProfile(null); //start 21229
	}
	
	public boolean isSelectButtonDisable() {
		if(getManagedBean().getSelectedVotObj()!=null) {
			return false;
		}
		if(getManagedBean().getSelectedFundObj()!=null) {
			return false;
		}
		return true;
	}

	private class SearchVotPagingDataModel extends PagingDataModel<Object[]> {
		private static final long serialVersionUID = 1L;

		public SearchVotPagingDataModel() {
			super("votList",true);
		}

		@Override
		public ISearchResultBean<Object[]> pageChangeListener(ISearchBean<Object[]> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IVotFundSO votFundSO;

			try {
				VotFundReqDTO votReqDTO = new VotFundReqDTO();
				votReqDTO.setVotRecordStatus(DbValueConst.RECORD_STATUS_ACTIVE);
				
				//start 21229
				if(getManagedBean().getSelectedDeduction().getOrgProfile()!=null){
					System.out.println("PaymentMatchBackingBean.populateSelectedObj() Jabatan Id"+getManagedBean().getSelectedDeduction().getOrgProfile().getOrgProfileId());

					PmOrgProfile obj = retrieveJabatanOrgProfile(getManagedBean().getSelectedDeduction().getOrgProfile().getOrgProfileId());
					System.out.println("PaymentMatchBackingBean.SearchVotPagingDataModel.pageChangeListener()"+ obj.getOrgCode());
					votReqDTO.setPtjGroupCode(obj.getOrgCode());
				}else{
					votReqDTO.setPtjGroupCode(getManagedBean().getPaymentAdvice().getFlRequest().getChargeOrgProfile().getParentOrgProfile().getOrgCode());
				}	
				//end 21229
				if(StringUtils.isNotBlank(getManagedBean().getSearchVotField())){
					if(getManagedBean().getSelectedVotValue() ==  2){
						votReqDTO.setVotFundCode(getManagedBean().getSearchVotField());
					}else if(getManagedBean().getSelectedVotValue() ==  3){
						votReqDTO.setVotFundDesc(getManagedBean().getSearchVotField());
					}
					
				}
				
				votReqDTO.setVotFundObjectSearchBean(searchBean);
				votReqDTO.setFLSearch(true);
				votFundSO = (IVotFundSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IVotFundSO.class);
				VotFundResDTO resDTO = votFundSO.searchVotFundData(votReqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getVotObjectResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}

			return null;
		}
	}

	public IPagingDataModel<Object[]> getSearchVot() {
		return searchVot;
	}

	public void setSearchVot(IPagingDataModel<Object[]> searchVot) {
		this.searchVot = searchVot;
	}
	
	private class SearchFundPagingDataModel extends PagingDataModel<Object[]> {
		private static final long serialVersionUID = 1L;

		public SearchFundPagingDataModel() {
			super("fundList",true);
		}

		@Override
		public ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IFundSO fundSO;

			try {
//				ISearchBean<Object[]> searchBeanData = getSearchBeanData(searchBean);
				FundReqDTO fundReqDTO = new FundReqDTO();
				if(StringUtils.isNotBlank(getManagedBean().getSearchVotField())){
					if(getManagedBean().getSelectedVotValue() ==  2){
						fundReqDTO.setFundCode(getManagedBean().getSearchVotField());
					}else if(getManagedBean().getSelectedVotValue() ==  3){
						fundReqDTO.setFundName(getManagedBean().getSearchVotField());
					}
					
				}

				fundReqDTO.setFundRecordStatus(DbValueConst.RECORD_STATUS_ACTIVE);
				
				fundReqDTO.setFundSearchBean(searchBean);
				fundSO = (IFundSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IFundSO.class);
				FundResDTO resDTO = fundSO.searchFundData(fundReqDTO);

				if (resDTO.isSuccessful()) {

					return resDTO.getFundResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}

			return null;
		}
	}

	public IPagingDataModel<Object[]> getSearchFund() {
		return searchFund;
	}

	public void setSearchFund(IPagingDataModel<Object[]> searchFund) {
		this.searchFund = searchFund;
	}
	
	/**
	 * retrieve VOT and Fund values
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getDropdownVotCodeAndDesc() {

		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		try {
			List<String> columnValues = new ArrayList<String>();
			columnValues.add(ResourceBundleUtils.getMessage(
					"lbl.pm.category.code.only", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			columnValues.add(ResourceBundleUtils.getMessage(
					"lbl.pm.category.desc.only", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			
			items = new SelectItem[columnValues.size()+1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (String value : columnValues) {
				items[i++] = new SelectItem(i,value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}
	
	/**
	 * populate selected obj VotFund.
	 */
	public void populateSelectedObj(){
		Object[] obj = null;
		BigDecimal votFundId = null;
		if(getManagedBean().getSelectedFundObj() != null){
			obj = getManagedBean().getSelectedFundObj();
			votFundId  = (BigDecimal) obj[5];
		}else if(getManagedBean().getSelectedVotObj() != null){
			obj = getManagedBean().getSelectedVotObj();
			votFundId  = (BigDecimal) obj[7];
		}
		IVotFundSO votFundSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IVotFundSO.class);
		VotFundReqDTO reqDTO = new VotFundReqDTO();
		reqDTO.setVotFundId(votFundId.longValue());
		getManagedBean().getSelectedDeduction().setPmVotFund(votFundSO.retrieveVotFundByFundId(reqDTO).getPmVotFund());
		
		resetAction();
	}
	
	/**
	 * Gets the dropdown values that vot type is not Vot A, vot B,vot T .
	 *
	 * @return the dropdown values 
	 */
	public List<SelectItem> getFinancingTypeDropdownValues(){
		IProjectSetiaSO projectSetiaSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IProjectSetiaSO.class);
		ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
		ProjectSetiaResDTO resDTO = new ProjectSetiaResDTO();
		if(getManagedBean().getSelectedDeduction() != null && getManagedBean().getSelectedDeduction().getPmVotFund() != null){
			if(getManagedBean().getSelectedDeduction().getPmVotFund().getVotFundType() != null){
				if(getManagedBean().getSelectedDeduction().getPmVotFund().getVotFundType().equalsIgnoreCase(VotFundType.AMANAH.value())
						|| getManagedBean().getSelectedDeduction().getPmVotFund().getVotFundType().equalsIgnoreCase(VotFundType.BEKALAN.value()) 
						|| getManagedBean().getSelectedDeduction().getPmVotFund().getVotFundType().equalsIgnoreCase(VotFundType.TANGGUNGAN.value())){
					getManagedBean().getSelectedDeduction().setEnableCp(true);
				}else{
					getManagedBean().getSelectedDeduction().setEnableCp(false);
				}
			}
			
			if(getManagedBean().getSelectedDeduction().getPmVotFund().getVotFundId() != null){
				reqDTO.setVotFundId(getManagedBean().getSelectedDeduction().getPmVotFund().getVotFundId());
				resDTO = projectSetiaSO.retrieveSubSetiaFinancingTypeListByVotFundId(reqDTO);
			}
		}
		
		if(getManagedBean().getPopulateSubSetiaList()== null || getManagedBean().getPopulateSubSetiaList().isEmpty()){
			if (resDTO.getFinancingTypeValueList() != null) {
				for (String subSetia : resDTO.getFinancingTypeValueList()) {
					getManagedBean().getPopulateSubSetiaList().add(new SelectItem(subSetia,subSetia));
				}
			}
		}
		return getManagedBean().getPopulateSubSetiaList();
	}
	
	/**
	 * download file Panduan_Potongan_Bayaran.pdf.
	 */
	public void downloadFile(String filePath) throws IOException {
		StringBuilder downloadPath = new StringBuilder();
		downloadPath.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)).append(filePath);
		
		super.downloadFile(downloadPath.toString());
	}
	
	//start 21229
	/**
	 * The method used for generating a list of available ministry
	 * for searching charged PTJ as selection.
	 * 
	 * @return the selection list of ministry
	 */
	public SelectItem[] getMinistryDropdown() {		
		SelectItem[] items = null;
		
		try {
		    int i = 1;
		    IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByMinistry(reqDTO);
		    
		    List<PmOrgProfile> list = resDTO.getOrgProfileList();
		    items = new SelectItem[list.size() + 1];
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
		    		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			for (PmOrgProfile orgProfile : list) {
			  items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	/**
	 * The method used for generating a list of available jabatan
	 * for searching charged PTJ as selection.
	 * 
	 * @return the selection list of jabatan
	 */
	public SelectItem[] getJabatanDropdown() {		
		SelectItem[] items = null;
		
		try {
		    int i = 1;
		    IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
		    		ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		    OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		    if(getManagedBean().getMinistryProfileId()!=null){
				reqDTO.setParentOrgProfileId(getManagedBean().getMinistryProfileId());
				reqDTO.setOrgCode(OrganizationType.DEPARTMENT.value());
				OrgProfileResDTO resDTO = orgProfileSO.retrieveChildDDListByParentOrgProfileId(reqDTO);
				
			    List<Object[]> list = resDTO.getOrgProfileDDList();
			    if(list==null) {
			    	list = new ArrayList<Object[]>();
			    }
			    items = new SelectItem[list.size() + 1];
			    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
			    		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
				
			    for(Object[] object : list){
				  items[i++] = new SelectItem(Long.parseLong(object[0].toString()), object[1].toString());
				}
		    }else{		    				    
			   items = new SelectItem[1];
			   items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
			    		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	/**
	 * The method used for searching the charged PTJ.
	 */
	public void searchChargedPtjDetails() {
		try {
			chargedPtjDataModel.reloadData();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), "contract search failed."));
		}
	}
	
	/**
	 * Pagination for available charged PTJ search list
	 */
	private class chargedPtjPagingDataModel extends PagingDataModel<PmOrgProfile> {
		private static final long serialVersionUID = 1L;

		public chargedPtjPagingDataModel() {
			super("tableChargePtj", false);
		}
		
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
	 * .biz.bean.search.ISearchBean)
	 */
	@Override
	public ISearchResultBean<PmOrgProfile> pageChangeListener(ISearchBean<PmOrgProfile> searchBean) {			
		try {
			// Filter By - parameterType
			searchBean.getDataFilterBeans().add(new SearchFilterBean("orgTypeCode.pmParameterType.parameterType",SearchOperator.OPERATOR_EQUALS.value(),ParameterTypeConst.ORGANISATION_TYPES));
		
			// Filter By - Org Type
			searchBean.getDataFilterBeans().add(new SearchFilterBean("orgTypeCode.parameterCode",SearchOperator.OPERATOR_EQUALS.value(),OrganizationType.DIVISION.value()));
			
			// Filter By - Ministry Profile Id
			Long ministryProfileId = getManagedBean().getMinistryProfileId();
			if (ministryProfileId == null) {
				ministryProfileId = 0L;
			}
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
							SearchOperator.OPERATOR_EQUALS.value(),
							ministryProfileId));
			if (getManagedBean().getJabatanProfileId() != null) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()),
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getJabatanProfileId()));
			}
			
			Date currentDate = DateUtils.getCurrentDateWithoutTime();
			// Filter By - Org effDate
			searchBean.getDataFilterBeans()
					.add(new SearchFilterBean("pmOrgValidity.effDate",SearchOperator.OPERATOR_LESS_EQUAL.value(), currentDate));

			// Filter By - Org expDate
			searchBean.getDataFilterBeans().add(
					new SearchMultiFilterBean(new SearchFilterBean(
							"pmOrgValidity.expDate",
							SearchOperator.OPERATOR_GREATER_EQUAL.value(),
							currentDate), new SearchFilterBean(
							"pmOrgValidity.expDate",
							SearchOperator.OPERATOR_IS_NULL.value(), null,
							SearchCondition.CONDITION_OR.value())));
			
			if (getManagedBean().getPtjCodeName() != null
					&& (getManagedBean().getPtjCodeNameSearchValues() != null && !getManagedBean().getPtjCodeNameSearchValues().equalsIgnoreCase(""))) {
				if (getManagedBean().getPtjCodeName().equalsIgnoreCase(ContractConst.PTJ_CODE)) {
					SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgCode", SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getPtjCodeNameSearchValues());
					filterBean.setCaseInsensitive(true);
				    searchBean.getDataFilterBeans().add(filterBean);
				
				} else if (getManagedBean().getPtjCodeName().equalsIgnoreCase(ContractConst.PTJ_NAME)) {
					SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgName", SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getPtjCodeNameSearchValues());
					filterBean.setCaseInsensitive(true);
				    searchBean.getDataFilterBeans().add(filterBean);
				}
			}
			
			IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileSearchBean(searchBean);
			OrgProfileResDTO resDTO = orgProfileSO.searchOrgProfile(reqDTO);
			if (resDTO.isSuccessful()) {
				 return resDTO.getOrgProfileResultBean();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
	
	/**
	 * The method used to reset the search criteria for charged PTJ.
	 */
	public void resetChargedPtjDetails() {
		getManagedBean().setMinistryProfileId(retrieveMinistryOrgProfile(
				getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId()).getOrgProfileId());
		getManagedBean().setJabatanProfileId(null);
		//getManagedBean().setJabatanProfileId(retrieveJabatanOrgProfile(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId()).getOrgProfileId()) ;
		getManagedBean().setPtjCodeName(ContractConst.PTJ_CODE);		
		getManagedBean().setPtjCodeNameSearchValues(null);
		chargedPtjDataModel.resetData();
	}

	
	public void addChargedPtjDetails() {
		getManagedBean().getSelectedDeduction().setOrgProfile(getManagedBean().getSelectedPmOrgProfile());
		
		///resetAction();
	}

	/**
	 * @return the chargedPtjDataModel
	 */
	public IPagingDataModel<PmOrgProfile> getChargedPtjDataModel() {
		return chargedPtjDataModel;
	}
	/**
	 * @param chargedPtjDataModel the chargedPtjDataModel to set
	 */
	public void setChargedPtjDataModel(
			IPagingDataModel<PmOrgProfile> chargedPtjDataModel) {
		this.chargedPtjDataModel = chargedPtjDataModel;
	}
	
	/**
	 * Initialize remark #21229 .
	 * <p> get remarks lists.
	 * @param flWorkflowStatus the fl workflow status
	 * @param remarkList the remark list
	 */
	public void initializeRemarkRevision(String docType, Long docId) {
		FacesScopes facesScopes = new FacesScopes();

		IPaymentMatchSO paymentMatchSO = (IPaymentMatchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IPaymentMatchSO.class);
		FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		flReqDTO.setDocId(docId);
		flReqDTO.setDocType(docType);
		flReqDTO.setSectionId(FulfilmentConst.REMARK_SECTION_INVOICE_REVISION);
		resDTO = paymentMatchSO.retrieveRemarkAndAttachmentSection(flReqDTO);
		List remarklist = resDTO.getRemarkList();

		FlRemark remark = new FlRemark();
		remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		remark.setChangedBy(remark.getCreatedBy());
		remark.setChangedDate(remark.getCreatedDate());
		remark.setCreatedUsername(facesScopes.getUserSessionBean().getUserName());
		remark.setUserId(facesScopes.getUserSessionBean().getLoginId());

		if (remarklist.size() == 0 ) {		
			List remarklistT = new ArrayList();
			remarklistT.add(remark);
			getManagedBean().setRemarkReviseList(remarklistT);
		}else {
//			remarklist.add(0,remark); // 32026 *comment out for #32678
			getManagedBean().setRemarkReviseList(remarklist);
		}
	}
	
	/**
	 * Initialize remark #32026 .
	 * <p> get remarks lists.
	 * @param docid
	 */
	public List<FlRemark> initializeRemarkList(Long docId) {
		//32026 start
		IPaymentMatchSO paymentMatchSO = (IPaymentMatchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IPaymentMatchSO.class);
		
		FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTORemark = new FulfilmentResDTO();
		flReqDTO.setDocId(docId);
		flReqDTO.setDocType(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
		resDTORemark = paymentMatchSO.retrieveRemarkByDocIdAndTypeAndNullSection(flReqDTO);
		return resDTORemark.getRemarkList();
		//32026 end
		
	}
	
	/**
	 * Show item query.
	 *
	 * @param flItemAddress the fl item address
	 */
	public void showQuery(){
		
	}
	
	//end 21229
	
	//28221 -start
	public void checkSupplierUserActive(){
		IPersonnelSO personnelSO = (IPersonnelSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,IPersonnelSO.class);
		PersonnelReqDTO personnelReqDTO = new PersonnelReqDTO();
		PersonnelResDTO personnelResDTO = new PersonnelResDTO();
		personnelReqDTO.setUserId(getManagedBean().getPaymentAdvice().getFlInvoice().getSupplierSubmittedId());
		Long supplierId = getManagedBean().getPaymentAdvice().getFlRequest().getSmSupplier().getSupplierId();			
		personnelReqDTO.setSmSupplierId(supplierId);
		personnelResDTO = personnelSO.retrieveLatestActivePersonnelFL(personnelReqDTO);		
		if(personnelResDTO.getSmPersonnelList().size() > 0){
			getManagedBean().setSuppInvoiceActive(true);
		}else{
			getManagedBean().setSuppInvoiceActive(false);
		}
			
	}
		
	public SelectItem[] showSupplierList(){
			SelectItem[] items = null;
			int i = 1;
			IPersonnelSO personnelSO = (IPersonnelSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);
			PersonnelReqDTO personnelReqDTO = new PersonnelReqDTO();
			PersonnelResDTO personnelResDTO = new PersonnelResDTO();
			
			Long supplierId = getManagedBean().getPaymentAdvice().getFlRequest().getSmSupplier().getSupplierId();
			personnelReqDTO.setSmSupplierId(supplierId);
			personnelResDTO = personnelSO.retrieveLatestActivePersonnelBySupplierId(personnelReqDTO);
		
			items = new SelectItem[personnelResDTO.getSmPersonnelList().size() + 1];
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
		    		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			for (SmPersonnel smPersonnel : personnelResDTO.getSmPersonnelList()) {				
			  items[i++] = new SelectItem(smPersonnel.getPmUser().getUserId(), smPersonnel.getPmUser().getUserName());
			}
			return items;
	}
	//28221 -end
}
