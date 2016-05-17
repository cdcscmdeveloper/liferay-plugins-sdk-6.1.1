package my.ep.web.backingbean.fl.po;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

import my.ep.biz.service.ct.supplier.IContractSupplierSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.ct.supplier.ContractSupplierReqDTO;
import my.ep.dto.ct.supplier.ContractSupplierResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.codata.FLContractOrderDataType;
import my.ep.jaxb.fl.fc.FLFactoringRemoveReqType;
import my.ep.jaxb.fl.maintenace.cancel.common.order.FLOrderCancelReqType;
import my.ep.jaxb.fl.podata.FLPurchaseOrderDataType;
import my.ep.jaxb.fl.stopfullfillmentdata.FLStopFullfillmentDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.order.FlStopInstr;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.common.CommonBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBaseBackingBean;
import my.ep.web.backingbean.fl.cr.ContractRequestBackingBean;
import my.ep.web.backingbean.fl.deliveryorder.DeliveryOrderBackingBean;
import my.ep.web.backingbean.fl.pr.PurchaseRequestBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.fl.po.FulfilmentOrderManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

// TODO: Auto-generated Javadoc
/**
 * Backing Bean for Fulfillment Order.
 *
 * @author Giribabu
 * @since 2/11/2013
 */
@ManagedBean(name="flOrderBB")
@ViewScoped
public class FulfilmentOrderBackingBean extends FulfilmentBaseBackingBean<FulfilmentOrderManagedBean>{

	private static final String NAV_TO_CREATE_DO = "createDo";
	private static final String NAV_CR = "/xhtml/fl/contractrequest/crBasePage.xhtml";
	private static final String NAV_PR = "/xhtml/fl/purchaserequest/prBasePage.xhtml";
	private static final String NAV_PO = "/xhtml/fl/purchaseorder/poBasePage.xhtml";
	private static final String NAV_CO = "/xhtml/fl/contractorder/coBasePage.xhtml";
										
	private static Logger log = Logger.getLogger(FulfilmentOrderBackingBean.class);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "MM/dd/yy hh:mm a" );
	
	FulfilmentBackingBean flBB = new FulfilmentBackingBean();
	
	/**
	 * Instantiates a new fulfilment order backing bean.
	 */
	public FulfilmentOrderBackingBean(){
	}

	/**
	 * save,submit Fulfillment order.
	 *
	 * * @param String action 
	 *  <p>submit - save data into database and BPM process will route to the next respective user.
	 * 	<p>save - save data into database.
	 *  navigate to acknowledge page.
	 */
	public String fulfillmentOrderAction(String action) {
		log.debug("FulfilmentOrderBackingBean.fulfillmentOrderAction()");
		IFulfilmentOrderSO fulfilmentOrderSO = null;		
		try {
			validateFulfillmentOrder(action);			
			
			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flRequest = getManagedBean().getFlRequest();
			FlFulfilmentOrder flOrder = null;
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flOrder = getManagedBean().getFulfilmentOrder();
			}
			
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			reqDTO.setFlFulfilmentRequest(flRequest);

			if(getManagedBean().isContractRequest() == Boolean.TRUE){
				reqDTO.setContractRequest(getManagedBean().isContractRequest());
			}else{
				reqDTO.setContractRequest(false);
			}
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)){
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put(ReportConst.PARAM_DOC_NO, getManagedBean().getFulfilmentOrder().getDocNo());
				paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
				reqDTO.setReportUrl("<a href='"+generateReportURL(ReportConst.FL_NGEP_PURCHASE_ORDER,paramMap)+ "'>"+getManagedBean().getFulfilmentOrder().getDocNo()+"</a>");
			}
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			reqDTO.setToken(token);
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			if(!flBB.getManagedBean().getRemarkList().isEmpty()){
				reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			}
			reqDTO.setStopRequestId(getManagedBean().getStopRequestId());
			reqDTO.setContractOrder(getManagedBean().isContractRequest());
			if(reqDTO.isContractRequest() == Boolean.FALSE){
				if(getManagedBean().getFactoringCheck().equalsIgnoreCase("true")) {
					getManagedBean().getFulfilmentOrder().setFactored(true);
				}
			}
			reqDTO.setFulfilmentOrder(getManagedBean().getFulfilmentOrder());
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setUserRole(getUserRole());
			reqDTO.setTaskName(getManagedBean().getTaskName());
			reqDTO.setApproverUserList(getManagedBean().getApproverList());
			
			String messagePrefix = "";
			String messageSuffix = "";
			FulfilmentResDTO resDTO = null;
			if(getManagedBean().getPmDigiSign() != null){
				reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			}
			if((action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) && !hasFactoringUserRole()){
				resDTO = fulfilmentOrderSO.acknowledgeFulfillmentOrder(reqDTO);
			}else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_STOP_FULFILMENT)){
				reqDTO.setApprover(getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()));
				reqDTO.setStopIns(getManagedBean().getStopIns());
				resDTO = fulfilmentOrderSO.stopFulfilment(reqDTO);
			}else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				reqDTO.setFulfilmentOrder(flOrder);
				if(flOrder.getFlFulfilmentRequest().getCtContract() != null){
					reqDTO.setContractRequest(true);
				}
				resDTO = fulfilmentOrderSO.cancelFulfillmentOrder(reqDTO);
			}else if(action.equalsIgnoreCase(FulfilmentConst.REMOVE_FACTORING) 
					|| getManagedBean().getProcessName() != null 
					&& getManagedBean().getProcessName().equalsIgnoreCase(FLProcessNameConst.FC_REMOVAL_REQUEST_PROCESS_NAME)){
				resDTO = fulfilmentOrderSO.removeFactoringComp(reqDTO);
				messagePrefix = ResourceBundleUtils.getMessage("lbl.fl.rfc.for",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale());
			}
			
			if(resDTO.isSuccessful()) {
				String languageCode = getFacesScopes().getUserLocale().getLanguage();
				String messageId = MessageConst.INFO_700005;
				String documentType = ParameterConst.DOCTYPE_PURCHASE_ORDER;
				String docNo = getManagedBean().getFulfilmentOrder().getDocNo();
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
					messageId = MessageConst.INFO_700011;
					if(!hasFactoringUserRole() && CommonConstants.DEFAULT_LANGUAGE_CODE.equals(languageCode)){
//						messagePrefix=ResourceBundleUtils.getMessage("lbl.fl.government",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+"  ";
					}else if(CommonConstants.LANGUAGE_CODE_MALAY.equals(languageCode)){
//						messagePrefix=" "+ResourceBundleUtils.getMessage("lbl.fl.government",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale());
					}
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					messageId = MessageConst.INFO_700007;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					messageId = MessageConst.INFO_700008;
				} else if(action.equalsIgnoreCase(FulfilmentConst.STOP_FULFILMENT)) {
					documentType = ParameterConst.DOCTYPE_STOP_DELIVERY;
					docNo = resDTO.getFlStopInstr().getStopInstrNo();
					if(reqDTO.isApprover()) {
						messageId = MessageConst.INFO_700026;
					} else {
						messageId = MessageConst.INFO_700005;
					}
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
					messageId = MessageConst.INFO_700010;
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(messageId, 
								new String[]{messagePrefix+getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, documentType)+messageSuffix, docNo}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
			if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
				renewSesionObject();
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE) && !hasFactoringUserRole()) {
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put(ReportConst.PARAM_DOC_NO, resDTO.getOrderNo());
					paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
					CommonBackingBean commonBackingBean = new CommonBackingBean();
					commonBackingBean.getManagedBean().setReportName(ReportConst.FL_NGEP_PURCHASE_ORDER);
					if(!getManagedBean().isContractRequest()){
						commonBackingBean.getManagedBean().setReportDisplayName(getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_CONTRACT_ORDER));
					}else{
						commonBackingBean.getManagedBean().setReportDisplayName(getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_ORDER));
					}
					
					commonBackingBean.getManagedBean().setParamMap(paramMap);
					return CommonUIConst.ACKNOWLEDGE_REPORT_PAGE;
				}
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}	
			else {
				throw new EpException("Failed.");
			}
		} finally {
			fulfilmentOrderSO = null;
		}
	}
	
	/**
	 * Validate fulfillment order before submit or save action.
	 *
	 * @param action the action
	 */
	private void validateFulfillmentOrder(String action){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		if(getFacesScopes().hasRoleUser(RoleCode.SUPPLIER_TEMP.value())) {
			throw new EpException(MessageConst.ERROR_740046);
		}
		if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
			if(flBB.getManagedBean().getRemarkList().get(0).getRemark()==null || flBB.getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.txt.remarks",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			}
		}
		if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)){
			if("true".equalsIgnoreCase(getManagedBean().getFactoringCheck())){
				if(null==getManagedBean().getFulfilmentOrder().getPmFinancialOrgId()){
					throw new EpException(MessageConst.ERROR_745062);
				}
			}			
		}
		if(getManagedBean().isRemoveFactoring() && action.equalsIgnoreCase(BpmOutcomeConst.BPM_REMOVE_FACTORING)){
			IUserSO userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			IFinancialOrgSO financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class);
			FinancialOrgReqDTO finReqDTO = new FinancialOrgReqDTO();
			finReqDTO.setFinancialOrgId(getManagedBean().getFulfilmentOrder().getPmFinancialOrgId());
			FinancialOrgResDTO finResDTO = financialOrgSO.retrieveOrgProfileIdByFinancialId(finReqDTO);
			
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setOrgProfileId(finResDTO.getPmFinancialOrg().getPmOrgProfiles().get(0).getOrgProfileId());
			UserResDTO userResDTO = userSO.retrieveUserListByOrgProfileId(userReqDTO);
			if(userResDTO.getUserList() == null || userResDTO.getUserList().isEmpty()){
				throw new EpException(MessageConst.ERROR_740020, new String[]{ResourceBundleUtils.getMessage("lbl.txt.factoringUser",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			}
		}
		if(getManagedBean().isContractRequest() == Boolean.TRUE){
			//Remove the checking if contract terminated/expired during supplier acknowledge, 
			//unitEP(22/1/2015) said should allow to proceed if the order generated.
			//checkValidity(getManagedBean().getFlRequest());
		}
	}

	/**
	 * Navigate to delivery order page.
	 *
	 * @param index the index
	 * @return the string
	 */
	public String navigateToFlOrder(int index) {
		IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
		IReasonSO reasonSO = (IReasonSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IReasonSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		ReasonReqDTO reasonReqDTO = new ReasonReqDTO();
		reqDTO.setFlOrderId(getManagedBean().getSelectedFlOrder().getFulfilmentOrderId());
		reqDTO.setDocType(ParameterConst.DOCTYPE_DELIVERY_ORDER);
		FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(reqDTO);
		BigDecimal delAmtCal= BigDecimal.ZERO;
		//String others="Justification Other";
		
		DeliveryOrderBackingBean doMB = new DeliveryOrderBackingBean();
		doMB.setDeliveryOrder(resDTO);
		for(FlDeliveryAddress deliveryAddress : resDTO.getFlFulfilmentRequest().getFlDeliveryAddresses()) {
			for(FlFulfilmentItemAddr itemAddr : deliveryAddress.getFlFulfilmentItemAddr()) {
				BigDecimal remainOrderQty=itemAddr.getOrderedQty().subtract(itemAddr.getSumDeliveredQty()).add(itemAddr.getRejectedQty());
				if(itemAddr.getDeliveredQty().doubleValue()==0) {
					itemAddr.setDeliveredQty(remainOrderQty);
				}
				if(itemAddr.getDeliveredQty().doubleValue()>0){
					if(itemAddr.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)){
						BigDecimal unitPriceCal=itemAddr.getUnitPrice();
						delAmtCal=unitPriceCal.multiply(itemAddr.getDeliveredQty());
						itemAddr.setDeliveredAmt(delAmtCal.setScale(2, RoundingMode.HALF_UP));
					}
				}
			}
		}
		for(FlDeliveryAddress deliveryAddress : resDTO.getFlFulfilmentRequest().getFlDeliveryAddresses()) {
			for(FlFulfilmentItemAddr itemAddr : deliveryAddress.getFlFulfilmentItemAddr()) {
				BigDecimal remainOrderQty=itemAddr.getOrderedQty().subtract(itemAddr.getSumDeliveredQty()).add(itemAddr.getRejectedQty());
				deliveryAddress.setSuppDelOrdNoEnabled(false);
				if(remainOrderQty.doubleValue()> 0){
					deliveryAddress.setSuppDelOrdNoEnabled(true);
					break;
				}
			}
		}
		doMB.getSupplierGst(resDTO.getFlFulfilmentRequest());
		doMB.getManagedBean().setDisplayStopIntrDetls(false);
		if(resDTO.getFlStopInstr() !=null){
			doMB.getManagedBean().setDisplayStopIntrDetls(true);
			doMB.getManagedBean().setFlStopInstr(resDTO.getFlStopInstr());
			reasonReqDTO.setReasonId(resDTO.getFlStopInstr().getPmReason().getReasonId());
			reasonReqDTO.setLanguage(CommonConstants.DEFAULT_LANGUAGE_CODE);
			ReasonResDTO reasonResDTO = reasonSO.retrieveReasonDescById(reasonReqDTO);
			PmReasonDesc reasonDesc=reasonResDTO.getReasonDesc();
			if(FulfilmentConst.REASON_CODE.equalsIgnoreCase(resDTO.getFlStopInstr().getPmReason().getReasonCode())) {
				doMB.getManagedBean().setReasonDesc(reasonDesc.getReasonDesc() + " " +  "-"+ " "+ resDTO.getFlStopInstr().getReasonDesc());
			}else{
				doMB.getManagedBean().setReasonDesc(reasonDesc.getReasonDesc());
			}
			
			doMB.getManagedBean().getFlStopInstr().setChangedDate(resDTO.getFlStopInstr().getChangedDate());
			doMB.getManagedBean().getFlStopInstr().setStopInstrNo(resDTO.getFlStopInstr().getStopInstrNo());
			doMB.getManagedBean().setRejectedRemark(resDTO.getStopInstrRejectedRemarkList());
		}
		doMB.sortOrderSummaryList(doMB.getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses());
		FacesContext fc = FacesContext.getCurrentInstance();
		doMB.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
		doMB.getManagedBean().setFromDoPage(true);
		doMB.getManagedBean().setFromPageUrl("/xhtml/fl/dopending/doPendingTable.xhtml");
		return NAV_TO_CREATE_DO;
	}

	/**
	 * Pre render view for contract Request.
	 */
	public void preRenderViewCR() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		
		if(taskId != null){
			Object obj = super.getBpmTaskDetails(Object.class, null);
			if (this.getManagedBean().getProcessName().equalsIgnoreCase(FLProcessNameConst.STOP_FULFILMENT_REQUEST_PROCESS_NAME)){
				IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
				FLStopFullfillmentDataType stopFlData = (FLStopFullfillmentDataType)obj;
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();

				FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(flReqDTO);
				getManagedBean().setStopRequestId(stopFlData.getStopRequestId().longValue());
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				getManagedBean().setContractRequest(Boolean.TRUE);
			}else {
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
				FulfilmentResDTO resDTO = new FulfilmentResDTO();
				IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
				if(FLProcessNameConst.RESUBMIT_ORDER_CANCELLATION.equalsIgnoreCase(this.getManagedBean().getProcessName())){
					FLOrderCancelReqType orderCancelReqType = (FLOrderCancelReqType)obj;
					flReqDTO.setDocType(ParameterConst.DOCTYPE_PURCHASE_ORDER);
					flReqDTO.setFlOrderId(orderCancelReqType.getOrderDetails().getOrderId());
					if(orderCancelReqType.getErrorIds() != null && orderCancelReqType.getErrorIds().length() > 0){
						getManagedBean().setAutoUpdate(Boolean.FALSE);
						getManagedBean().setGfmasError(true);
						FulfilmentReqDTO errReq = new FulfilmentReqDTO();
						errReq.setErrorId(new Long(orderCancelReqType.getErrorIds()));
						FulfilmentResDTO errRes = fulfilmentSO.retrieveErrorContent(errReq);
						getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
					}
				}else{
					FLContractOrderDataType crDataType = (FLContractOrderDataType)obj;
					flReqDTO.setFlOrderId(crDataType.getOrderDetails().getOrderId());
					flReqDTO.setDocType(crDataType.getOrderDetails().getOrderType());
				}
				
				resDTO = getFulfilmentOrderSO().retrieveFulfilmentOrderById(flReqDTO);
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				getManagedBean().setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());

				if(getManagedBean().getFlRequest().getCtContract().isFactored()){
					IContractSupplierSO contractSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSupplierSO.class);

					ContractSupplierReqDTO contractSupplierReqDTO = new ContractSupplierReqDTO();
					contractSupplierReqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
					ContractSupplierResDTO contractSupplierResDTO = contractSupplierSO.retrieveFactoringByContract(contractSupplierReqDTO);
					if(contractSupplierResDTO.getFactoring() != null){
						getManagedBean().getFlRequest().getCtContract().setCtFactoring(contractSupplierResDTO.getFactoring());
						if(contractSupplierResDTO.getFactoring().getFinancialOrg() != null){
							getManagedBean().getFulfilmentOrder().setFactored(true);
							getManagedBean().getFulfilmentOrder().setPmFinancialOrgId(contractSupplierResDTO.getFactoring().getFinancialOrg().getFinancialOrgId());
							getCrSelectedFactoringCompDtl(contractSupplierResDTO.getFactoring().getFinancialOrg().getFinancialOrgId());
						}
					}else{
						getManagedBean().getFulfilmentOrder().setFactored(false);
					}
				}
				getManagedBean().setDocType(resDTO.getFlOrder().getFlFulfilmentRequest().getDocType());
				getManagedBean().setContractRequest(Boolean.TRUE);
				sortOrderSummaryList(getManagedBean().getFlRequest().getFlDeliveryAddresses());
				FulfilmentBackingBean flBB = new FulfilmentBackingBean(resDTO.getFlOrder().getDocType(), resDTO.getFlOrder().getFlFulfilmentRequest().getCtContract().getContractNo());
				flBB.getManagedBean().setHideRemarks(false);
				flBB.initializeRemarkAndAttachement(resDTO.getFlOrder().getFlWorkflowStatus(), resDTO.getRemarkList(),resDTO.getAttachmentList());
			}  
			getManagedBean().setSelectedTabIndex(0);
		}else{
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			if(null == getManagedBean().getButtonVal() || !getManagedBean().getButtonVal().equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(false);
			}
		}
		
	}
	
	/**
	 * Gets the fulfilment order so.
	 *
	 * @return the fulfilment order so
	 */
	private IFulfilmentOrderSO getFulfilmentOrderSO() {
		return (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
	}
	
	/**
	 * Pre render view for purchase request.
	 */
	public void preRenderViewPR() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");

		if(taskId != null){
			Object obj = super.getBpmTaskDetails(Object.class, null);
			if(null==this.getManagedBean().getProcessName() || this.getManagedBean().getProcessName().equalsIgnoreCase("")){
				System.out.println("Process name is NUll");
			} else if (FLProcessNameConst.STOP_FULFILMENT_REQUEST_PROCESS_NAME.equalsIgnoreCase(this.getManagedBean().getProcessName())) {
				FLStopFullfillmentDataType stopFlData = (FLStopFullfillmentDataType)obj;
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
				flReqDTO.setFlOrderId(stopFlData.getOrderId());
				
				FulfilmentResDTO resDTO = getFulfilmentOrderSO().retrieveFulfilmentOrderById(flReqDTO);
				getManagedBean().setStopRequestId(stopFlData.getStopRequestId().longValue());
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				getManagedBean().setContractRequest(Boolean.TRUE);
			} else if(FLProcessNameConst.FC_REMOVAL_REQUEST_PROCESS_NAME.equalsIgnoreCase(this.getManagedBean().getProcessName())){
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
				FulfilmentResDTO resDTO = new FulfilmentResDTO();
				FLFactoringRemoveReqType removeReqType = (FLFactoringRemoveReqType)obj;
				flReqDTO.setDocType(ParameterConst.DOCTYPE_FACTORING_COMPANY);
				flReqDTO.setFlOrderId(removeReqType.getOrderId());
				resDTO = getFulfilmentOrderSO().retrieveFulfilmentOrderById(flReqDTO);
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
//				if(null != resDTO.getFlOrder().getPmFinancialOrg().getFinancialOrgId()){//TODO will no longer need this. direct get from florder.factoringaddres
//					getRfcAddress(resDTO.getFlOrder().getPmFinancialOrg().getFinancialOrgId());
//				}				
				getManagedBean().setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
				getManagedBean().setDocType(resDTO.getFlOrder().getFlFulfilmentRequest().getDocType());
				FulfilmentBackingBean flBB = new FulfilmentBackingBean(resDTO.getFlOrder().getDocType(), resDTO.getFlOrder().getFlFulfilmentRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
				flBB.getManagedBean().setHideRemarks(false);
				flBB.initializeRemarkAndAttachement(resDTO.getFlOrder().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
			}else {
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
				FulfilmentResDTO resDTO = new FulfilmentResDTO();
				IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
				if(FLProcessNameConst.RESUBMIT_ORDER_CANCELLATION.equalsIgnoreCase(this.getManagedBean().getProcessName())){
					FLOrderCancelReqType orderCancelReqType = (FLOrderCancelReqType)obj;
					flReqDTO.setDocType(ParameterConst.DOCTYPE_PURCHASE_ORDER);
					flReqDTO.setFlOrderId(orderCancelReqType.getOrderDetails().getOrderId());
					if(orderCancelReqType.getErrorIds() != null && orderCancelReqType.getErrorIds().length() > 0){
						getManagedBean().setAutoUpdate(Boolean.FALSE);
						getManagedBean().setGfmasError(true);
						FulfilmentReqDTO errReq = new FulfilmentReqDTO();
						errReq.setErrorId(new Long(orderCancelReqType.getErrorIds()));
						FulfilmentResDTO errRes = fulfilmentSO.retrieveErrorContent(errReq);
						getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
					}
				}else{
					FLPurchaseOrderDataType flPurchaseOrd = (FLPurchaseOrderDataType)obj;
					flReqDTO.setDocType(ParameterConst.DOCTYPE_PURCHASE_ORDER);
					flReqDTO.setFlOrderId(flPurchaseOrd.getOrderDetails().getOrderId());
					if(flPurchaseOrd.getOrderDetails().getErrorId() != null && flPurchaseOrd.getOrderDetails().getErrorId() > 0){
						getManagedBean().setAutoUpdate(Boolean.FALSE);
						getManagedBean().setGfmasError(true);
						FulfilmentReqDTO errReq = new FulfilmentReqDTO();
						errReq.setErrorId(flPurchaseOrd.getOrderDetails().getErrorId());
						FulfilmentResDTO errRes = fulfilmentSO.retrieveErrorContent(errReq);
						getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
					}
				}
				
				resDTO = getFulfilmentOrderSO().retrieveFulfilmentOrderById(flReqDTO);
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				if(resDTO.getFlOrder().isFactored()) {
					getManagedBean().setFactoringCheck("true");
				} else {
					getManagedBean().setFactoringCheck("false");
				}
				getManagedBean().setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
				getManagedBean().setDocType(resDTO.getFlOrder().getFlFulfilmentRequest().getDocType());
				sortOrderSummaryList(getManagedBean().getFlRequest().getFlDeliveryAddresses());
				
				FulfilmentBackingBean flBB = new FulfilmentBackingBean(resDTO.getFlOrder().getDocType(), resDTO.getFlOrder().getFlFulfilmentRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
				flBB.initializeRemarkAndAttachement(resDTO.getFlOrder().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
			}  
			getManagedBean().setSelectedTabIndex(0);
		}else{
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			if(null == getManagedBean().getButtonVal() || !getManagedBean().getButtonVal().equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(false);
			}
		}
		
	}
	
	/**
	 * if Factored checked,get Factoring company details
	 *
	 * @param e the e
	 */
	public void factoringChecked(AjaxBehaviorEvent e) {
		String id = (String) e.getComponent().getAttributes().get("value");
		getManagedBean().setFactoringCheck(id);
		getFactoringCompanyDtl();
		
	}
	
	/**
	 * Method calculate Order Amount based on order quantity and unit price.
	 *
	 * @param fulfilmentItemAddr the fulfilment item addr
	 */
	public void retrieveOrderAmount(FlFulfilmentItemAddr fulfilmentItemAddr){
		double unitPrice = 0;
		double ordQnty = 0;
		double ordAmoun = 0;
		if(null != fulfilmentItemAddr.getUnitPrice())
			unitPrice= fulfilmentItemAddr.getUnitPrice().doubleValue();
		if(null != fulfilmentItemAddr.getOrderedQty())
			ordQnty = fulfilmentItemAddr.getOrderedQty().doubleValue();
		ordAmoun =((unitPrice) * (ordQnty));
		BigDecimal ordAmount=new BigDecimal(ordAmoun);
		fulfilmentItemAddr.setOrderedAmt(ordAmount.setScale(2, RoundingMode.HALF_UP));
	}
	
	/**
	 * Check validity.
	 *
	 * @param flRequest the fl request
	 */
	public void checkValidity(FlFulfilmentRequest flRequest) {
		Date date = new Date();
		if(flRequest.getCtContract().getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE){
			if(date.compareTo(flRequest.getCtContract().getLatestContractVer().getCtContractValue().getExpDate()) >= 0 ){
				throw new EpException(MessageConst.ERROR_745064,new String[]{flRequest.getCtContract().getContractNo()});
			}
		}else{
			throw new EpException(MessageConst.ERROR_745065,new String[]{flRequest.getCtContract().getContractNo()});
		}
	}
		
	/**
	 * Gets the factoring details.
	 *
	 * @return the factoring details
	 */
	public void getFactoringDetails(){
		getManagedBean().getFulfilmentOrder().isFactored();
	}
	
	/**
	 * Gets the factoring company details based on Service Type.
	 *
	 * @return the factoring company dtl
	 */
	public void getFactoringCompanyDtl(){
		IFinancialOrgSO financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class);
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		reqDTO.setServiceType(ParameterConst.BANK_SERVICES_FACTORING_COMPANY);
		FinancialOrgResDTO resDTO = financialOrgSO.retrieveFinancialOrgListByServiceType(reqDTO);
		getManagedBean().setPmFinancialOrgs(resDTO.getFinancialOrgList());
	}
	
	/**
	 * Gets the selected factoring company details based on  Financial OrgId and address type.
	 *
	 * @param e the e
	 * @return the selected factoring comp dtl
	 */
	public void getSelectedFactoringCompDtl(AjaxBehaviorEvent e){
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
		Long financialOrgId = (Long) e.getComponent().getAttributes().get("value");
		
		getManagedBean().setSupplierAddress(null);
		getManagedBean().setPhoneNo(null);
		getManagedBean().setFaxNo(null);
		getManagedBean().setContactPerson(null);
		getManagedBean().setContactEmail(null);
		getManagedBean().setContactPhoneNo(null);
		
		if(financialOrgId!=null) {
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialOrgId(financialOrgId);
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
				//TODO NEW
				getManagedBean().getFulfilmentOrder().setFactoringOrgName(resDTO.getAddressTypeList().get(0).getPmFinancialOrg().getFinOrgName());
				getManagedBean().getFulfilmentOrder().setFactoringAddress(convertFinAccAddressToFlAddress(resDTO.getAddressTypeList().get(0)));
			}
		}
	}
		
	 /**
 	 * Gets the remove factoring company address based on financial OrgId and address type.
 	 *
 	 * @param financialOrgId the financial org id
 	 * @return the rfc address
 	 */
// 	public void getRfcAddress(Long financialOrgId){
//		 IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
//		 getManagedBean().setRfcAddress(null);
//		 getManagedBean().setRfcPhoneNo(null);
//		 getManagedBean().setRfcFaxNo(null);
//		 getManagedBean().setRfcContactPerson(null);
//		 getManagedBean().setRfcContactEmail(null);
//		 getManagedBean().setRfcContactPhoneNo(null);
//		 if(financialOrgId!=null) {
//				AddressReqDTO reqDTO = new AddressReqDTO();
//				reqDTO.setFinancialOrgId(financialOrgId);
//				reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
//				
//				AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
//				if(!resDTO.getAddressTypeList().isEmpty()){
//					getManagedBean().setRfcAddress(getFullAddress(resDTO.getAddressTypeList().get(0).getPmAddress()));
//					getManagedBean().setRfcPhoneNo(resDTO.getAddressTypeList().get(0).getPmAddress().getPhoneNo());
//					getManagedBean().setRfcFaxNo(resDTO.getAddressTypeList().get(0).getPmAddress().getFaxNo());
//					if(!resDTO.getAddressTypeList().get(0).getPmContacts().isEmpty() ){
//						PmContact pmContact = resDTO.getAddressTypeList().get(0).getPmContacts().get(0);
//						getManagedBean().setRfcContactPerson(pmContact.getContactName());
//						getManagedBean().setRfcContactEmail(pmContact.getEmail());
//						getManagedBean().setRfcContactPhoneNo(pmContact.getPhoneNo());
//					}
//				}
//			}
//	}	
	 
 	//TODO remove it if tested factoring no error.
	/**
	 * Gets the factoring address based on financial org id  and address type.
	 *
	 * @param financialOrgId the financial org id
	 * @return the factoring address
	 */
//	public void getFactoringAddress(Long financialOrgId) {
//		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
//
//		getManagedBean().setSupplierAddress(null);
//		getManagedBean().setPhoneNo(null);
//		getManagedBean().setFaxNo(null);
//		getManagedBean().setContactPerson(null);
//		getManagedBean().setContactEmail(null);
//		getManagedBean().setContactPhoneNo(null);
//
//		if (financialOrgId != null) {
//			AddressReqDTO reqDTO = new AddressReqDTO();
//			reqDTO.setFinancialOrgId(financialOrgId);
//			reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
//
//			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
//			if (!resDTO.getAddressTypeList().isEmpty()) {
//				getManagedBean().setSupplierAddress(getFullAddress(resDTO.getAddressTypeList().get(0).getPmAddress()));
//				getManagedBean().setPhoneNo(resDTO.getAddressTypeList().get(0).getPmAddress().getPhoneNo());
//				getManagedBean().setFaxNo(resDTO.getAddressTypeList().get(0).getPmAddress().getFaxNo());
//				if (!resDTO.getAddressTypeList().get(0).getPmContacts().isEmpty()) {
//					PmContact pmContact = resDTO.getAddressTypeList().get(0).getPmContacts().get(0);
//					getManagedBean().setContactPerson(pmContact.getContactName());
//					getManagedBean().setContactEmail(pmContact.getEmail());
//					getManagedBean().setContactPhoneNo(pmContact.getPhoneNo());
//				}
//			}
//		}
//	}
		
	/**
	 * Gets the portlet title.
	 *
	 * @return the portlet title
	 */
	public String getPortletTitle() {
		String rfc = ResourceBundleUtils.getMessage("lbl.fl.doc.maintain.action.factory.comp.removal",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale());
		String title = ResourceBundleUtils.getMessage("lbl.fl.doc.maintenance",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale());
		String portletTitle = title;
		if(getManagedBean().isRemoveFactoring()) {
			portletTitle = rfc;
		}
		return portletTitle;
	}
		
	/**
	 * Gets the populate reason list based on language and Reason Category.
	 *
	 * @return the populate reason list
	 */
	public List<SelectItem> getPopulateReasonList() {
		IReasonSO reasonSO = (IReasonSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IReasonSO.class);
		
		List<SelectItem> populateReasonList = new ArrayList<SelectItem>();
		
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setLanguage(CommonConstants.DEFAULT_LANGUAGE_CODE);//temp for testing purposes will set back after gustia got the proper data
		reqDTO.setReasonCategory(ParameterConst.FULFILMENT_REASON_CODE);
		ReasonResDTO resDTO = reasonSO.retrieveReasonDescListByCategory(reqDTO);
		getManagedBean().setReasonDescs(resDTO.getReasonDescList());
		List<PmReasonDesc> reasonDescs = resDTO.getReasonDescList();
		for (PmReasonDesc obj : reasonDescs) {
			populateReasonList.add(new SelectItem(obj.getPmReason().getReasonCode(), obj.getReasonDesc()));
		}
		
		if(getManagedBean().getStopIns() == null){
			getManagedBean().setStopIns(new FlStopInstr());
			getManagedBean().getStopIns().setPmReason(new PmReason());
		}
		
		return populateReasonList;
	}
	
	/**
	 * On tab change.
	 *
	 * @param selectedTabIndex the selected tab index
	 */
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==1){
			if(getManagedBean().getFlRequest().getFlDeliveryAddresses()!=null) {
				sortAddress(getManagedBean().getFlRequest().getFlDeliveryAddresses());
				sortOrderSummaryList(getManagedBean().getFlRequest().getFlDeliveryAddresses());
			}
		}
	}

	// Please add role if there is new role for this backingbean
	/**
	 * Gets the user role.
	 *
	 * @return the user role
	 */
	private String getUserRole() {
		if(getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value())) {
			return RoleCode.FL_APPROVER.value();	
		} else if(hasSupplierRole()) {
			return getUserSupplierRole();
		} else if(getFacesScopes().hasRoleUser(RoleCode.FACTORING_ADMIN.value())) {
			return RoleCode.FACTORING_ADMIN.value();	
		} else if(getFacesScopes().hasRoleUser(RoleCode.FACTORING_USER.value())) {
			return RoleCode.FACTORING_USER.value();	
		} else {
			throw new EpException(MessageConst.ERROR_750040);
		}
	}
	
	/**
	 * Sort delivery address list.
	 *
	 * @param flDelAddressList the fl del address list
	 */
	public void sortAddress(List<FlDeliveryAddress> flDelAddressList) {
		//Creating Comparator
		if(null==flDelAddressList || flDelAddressList.isEmpty()){
			return;
		}
		  Comparator<FlDeliveryAddress> comparator = new Comparator<FlDeliveryAddress>() {
	            @Override
	            public int compare(FlDeliveryAddress o1, FlDeliveryAddress o2) {
	                return new CompareToBuilder()
		                .append(o1.getFlAddress().getAddress1(), o2.getFlAddress().getAddress1())
		                .append(o1.getFlAddress().getAddress2(), o2.getFlAddress().getAddress2())
		                .append(o1.getFlAddress().getAddress3(), o2.getFlAddress().getAddress3())
		                .toComparison();
	            }
	        };
			Collections.sort(flDelAddressList, comparator);
    }
	
	// sorting Item for OrderSummary Tab
		/**
	 * Sort order summary list.
	 *
	 * @param deliveryAddressList the delivery address list
	 */
	public void sortOrderSummaryList(List<FlDeliveryAddress> deliveryAddressList) {
			//Creating Comparator
			if(null==deliveryAddressList || deliveryAddressList.isEmpty()){
				return;
			}
			for(FlDeliveryAddress flDeliveryAddress : deliveryAddressList) {
				sortItems(flDeliveryAddress.getFlFulfilmentItemAddr());
			}
	    }
		
		/**
		 * Sort items list.
		 *
		 * @param flItemAddressList the fl item address list
		 */
		public void sortItems(List<FlFulfilmentItemAddr> flItemAddressList) {
			//Creating Comparator
			if(null==flItemAddressList || flItemAddressList.isEmpty()){
				return;
			}
			  Comparator<FlFulfilmentItemAddr> comparator = new Comparator<FlFulfilmentItemAddr>() {
		            @Override
		            public int compare(FlFulfilmentItemAddr o1, FlFulfilmentItemAddr o2) {
		                return new CompareToBuilder()
			                .append(o1.getItemCode(), o2.getItemCode())
			                .toComparison();
		            }
		        };
				Collections.sort(flItemAddressList, comparator);
	    }
		
		/**
		 * Page navigation to FLDoc search page in Order.
		 *
		 * @return the string
		 */
		public String backToPervious() {
			if(getManagedBean().getBackToPrevious() !=null){
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				flBB.getManagedBean().setRemarkList(getManagedBean().getPreviousRemarkList());
				flBB.getManagedBean().setAttachmentList(getManagedBean().getPreviousAttachmentList());
				return getManagedBean().getBackToPrevious();
			}
			else{
				return "flDocSearch";
			}
		}
		
		/**
		 * Gets the contract request factoring company  details based on financial OrgId nad address type.
		 *
		 * @param financialOrgId the financial org id
		 * @return the cr selected factoring comp dtl
		 */
		public void getCrSelectedFactoringCompDtl(Long financialOrgId){
			IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			
			if(financialOrgId!=null) {
				AddressReqDTO reqDTO = new AddressReqDTO();
				reqDTO.setFinancialOrgId(financialOrgId);
				reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
				
				AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
				if(!resDTO.getAddressTypeList().isEmpty()){
					getManagedBean().getFulfilmentOrder().setFactoringOrgName(resDTO.getAddressTypeList().get(0).getPmFinancialOrg().getFinOrgName());
					getManagedBean().getFulfilmentOrder().setFactoringAddress(convertFinAccAddressToFlAddress(resDTO.getAddressTypeList().get(0)));
				}
			}
		}
		
	/**
	 * Signing action. 
	 * FL Approver Cancel PO/CO.
	 * Government Seller/Supplier acknowledge PO/CO or Remove Factoring(PO only).
	 * Factoring user reject/acknowledge Factoring Removal.
	 *
	 * @param outcome the outcome
	 * @return the string
	 * @throws Exception the exception
	 */
	public String signingAction(String outcome) throws Exception{
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || hasFactoringUserRole()) {
			if(hasFactoringUserRole()) {
				validateFulfillmentOrder(outcome);
			}
			return fulfillmentOrderAction(outcome);
		} else if((hasSupplierRole() && !hasGovtSellerRole())) {
			validateFulfillmentOrder(outcome);
			getManagedBean().getSpkiBackingBean().getChallengeQuestions();
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("spki_dlg.show()");
			return null;
		} else {
			validateFulfillmentOrder(outcome);
			setTextBeforeSign(outcome);
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		}
	}
		
	/**
	 * Gets the order amount.
	 *
	 * @return the order amt
	 */
	private double getOrderAmt() {
		BigDecimal totalAmt = new BigDecimal(0);
		for(FlDeliveryAddress delAddr : getManagedBean().getFlRequest().getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddr : delAddr.getFlFulfilmentItemAddr()){
				totalAmt = totalAmt.add(itemAddr.getOrderedAmt());
			}
		}
		return totalAmt.doubleValue();
	}

	/**
	 * Pre submit fulfillment order.
	 *
	 * @param pmDigiSign the pm digi sign
	 * @return the string
	 * @throws Exception the exception
	 */
	public String preSubmitFulfillmentOrder(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);
			if((hasSupplierRole() && !hasGovtSellerRole())) {
				setTextBeforeSign(getManagedBean().getButtonAction());
				updateSpkiDigiSignData(getManagedBean().getPmDigiSign(), getManagedBean().getFulfilmentOrder().getDocType(), 
						getManagedBean().getFulfilmentOrder().getFulfilmentOrderId(), getManagedBean().getFulfilmentOrder().getDocNo(), 
						getManagedBean().getTextBeforeSign());					
			}
		}
		return fulfillmentOrderAction(getManagedBean().getButtonAction());
	}
		
		/**
		 * Enable auto update.
		 */
		public void enableAutoUpdate(){
			getManagedBean().setAutoUpdate(Boolean.TRUE);
			RequestContext.getCurrentInstance().update("@form");
		}
		
		
		// To update digisign data for spki
		/**
		 * Update spki digi sign data.
		 *
		 * @param pmDigiSign the pm digi sign
		 * @param docType the doc type
		 * @param docId the doc id
		 * @param docNumber the doc number
		 * @param textBeforeSign the text before sign
		 */
		private void updateSpkiDigiSignData(PmDigiSign pmDigiSign, String docType,
				Long docId, String docNumber , String textBeforeSign) {
			FacesScopes facesScope = new FacesScopes();
			Long userId = facesScope.getUserSessionBean().getUserId();
			Timestamp date = new Timestamp(System.currentTimeMillis());
			pmDigiSign.setDocId(new BigDecimal(docId));
			pmDigiSign.setDocNo(docNumber);
			pmDigiSign.setDocType(docType);
			pmDigiSign.setCreatedBy(userId);
	        pmDigiSign.setCreatedDate(date);
	        pmDigiSign.setOriginalData(textBeforeSign);
	     // spki 
	        pmDigiSign.setSpkiSignedData(getManagedBean().getSpkiBackingBean().spkiSignTransaction(textBeforeSign));
		}
		
		/**
		 * Back to delivery order.
		 *
		 * @return the string
		 */
		public String backToDeliveryOrder() {
			return getManagedBean().getBackToPrevious();
		}
		
		/**
		 * Navigate to crHyperlink if doc type is CR.
		 * Navigate to prHyperlink if doc type is PR.
		 * Navigate to orderNoHyperlink if doc type is PO or CO.
		 * @param docType the doc type
		 * @return the string
		 */
		public String navigateDocument(String docType){
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER) || docType.equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
				IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
				FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
				reqDTO.setFlOrderId(getManagedBean().getFulfilmentOrder().getFulfilmentOrderId());
				reqDTO.setDocType(docType);
				FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(reqDTO);
				FulfilmentOrderBackingBean orderBackingBean = new FulfilmentOrderBackingBean();
				orderBackingBean.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				orderBackingBean.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				FacesContext fc = FacesContext.getCurrentInstance();
				orderBackingBean.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
				orderBackingBean.getManagedBean().setHyperLinkPage(true);
				orderBackingBean.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);				
				if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
					return NAV_PO;
				}else if (docType.equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
					return NAV_CO;
				}
			}if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_REQUEST)){
				IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
				FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
				reqDTO.setFlRequestId(getManagedBean().getFlRequest().getFulfilmentReqId());
				reqDTO.setFlRequestLink(true);
				FulfilmentResDTO resDTO = requestSO.retrieveFulfilmentRequest(reqDTO);
				
				ContractRequestBackingBean crBB = new ContractRequestBackingBean();
				crBB.renewSesionObject();
				crBB = new ContractRequestBackingBean();
				crBB.getManagedBean().setSelectedTabIndex(0);
				crBB.getManagedBean().setDocProcess(docType);
				
				FacesContext fc = FacesContext.getCurrentInstance();
				crBB.getManagedBean().setBackToDocument(fc.getViewRoot().getViewId());
				crBB.getManagedBean().setHyperLinkPage(true);
				crBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
				crBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());
				
				crBB.setFulfilmentRequestField(resDTO);
				crBB.initializeApproverGroup();
				
				crBB.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				crBB.getManagedBean().setPmUser(reqDTO.getPmUser());
				crBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
				crBB.getManagedBean().setDeclarationCheck(true);
				flBB.getManagedBean().setHideRemarks(true);
				crBB.getManagedBean().setPmPriceType(resDTO.getFlFulfilmentRequest().getCtContract().getLatestContractVer().getCtContractItems().get(0).getPriceType());
				IContractSupplierSO contractSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSupplierSO.class);
				ContractSupplierReqDTO contractSupplierReqDTO = new ContractSupplierReqDTO();
				if(null != resDTO.getFlFulfilmentRequest()){
					if(resDTO.getFlFulfilmentRequest().getCtContract().isFactored()){
						Long supplieContractId=resDTO.getFlFulfilmentRequest().getCtContract().getContractId();
						contractSupplierReqDTO.setContractId(supplieContractId);
						ContractSupplierResDTO contractSupplierResDTO = contractSupplierSO.retrieveFactoringByContract(contractSupplierReqDTO);
						if(contractSupplierResDTO.getFactoring() != null){
							resDTO.getFlFulfilmentRequest().getCtContract().setCtFactoring(contractSupplierResDTO.getFactoring());
							if(contractSupplierResDTO.getFactoring().getFinancialOrg() != null){
								crBB.getSelectedFactoringCompDtl(contractSupplierResDTO.getFactoring());
							}
						}else{
							resDTO.getFlFulfilmentRequest().getCtContract().setFactored(false);
						}
					}
				}
				
				for(CtAgency agency : crBB.getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtAgencies()){
					if(agency.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE || 
							(agency.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_SUSPENDED && agency.getEffDate().after(new Date()))) {
						crBB.getManagedBean().getAgentOrgProfileList().add(agency.getOrgProfile().getOrgProfileId());
						if(agency == crBB.getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtAgencies().get(0)){
							crBB.getManagedBean().setOrgProfileIds(agency.getOrgProfile().getOrgProfileId().toString());
						}else{
							crBB.getManagedBean().setOrgProfileIds(crBB.getManagedBean().getOrgProfileIds() + "," +agency.getOrgProfile().getOrgProfileId().toString());
						}
					}
				}
				return NAV_CR;
			}if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_REQUEST)){
				IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
				FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
				reqDTO.setFlRequestId(getManagedBean().getFlRequest().getFulfilmentReqId());
				reqDTO.setFlRequestLink(true);
				FulfilmentResDTO resDTO = requestSO.retrieveFulfilmentRequest(reqDTO);
				
				PurchaseRequestBackingBean prBB = new PurchaseRequestBackingBean();
				prBB.getManagedBean().setSubmitButtonStatus(false);
				prBB.getManagedBean().setDocProcess(docType);
				
				FacesContext fc = FacesContext.getCurrentInstance();
				prBB.getManagedBean().setBackToDocument(fc.getViewRoot().getViewId());
				prBB.getManagedBean().setHyperLinkPage(true);
				prBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
				prBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());
				
				prBB.setFulfilmentRequestField(resDTO);
				prBB.getManagedBean().setPmUser(reqDTO.getPmUser());
				prBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
				prBB.getManagedBean().setDeclarationCheck(true);
				flBB.getManagedBean().setHideRemarks(true);
				prBB.getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
				
				return NAV_PR;
			}
			return null;
		}
		
		/**
		 * Retrieve purchase order information and display in UI
		 */
		public void viewPurchaseOrder() {
			IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			
			String fulfilmentOrderId = originalServletRequest.getParameter("fulfilmentOrderId");
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			if(fulfilmentOrderId!=null) {
				reqDTO.setFlOrderId(Long.valueOf(fulfilmentOrderId));
				reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
				reqDTO.setDocType(ParameterConst.DOCTYPE_PURCHASE_ORDER);
				reqDTO.setPoLink(Boolean.TRUE);
				renewSesionObject();
				try {
					resDTO = orderSO.retrieveFulfilmentOrderById(reqDTO);
				} catch (EpException e) {
					handleException(e);
					return;
				}
				
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				getManagedBean().setSelectedTabIndex(0);
				getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
				getManagedBean().setHyperLinkPage(true);
				getManagedBean().setTaskId(BpmOutcomeConst.BPM_VIEW);//for the purpose of hide the back button.
				
				getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				getManagedBean().setSelectedTabIndex(0);
				getManagedBean().setPmUser(reqDTO.getPmUser());
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				getManagedBean().setRemoveFactoring(false);
				getManagedBean().setDocMaintainRemarks(true);
				getManagedBean().setDocType(ParameterConst.DOCTYPE_PURCHASE_ORDER);
				
				//reqDTO.setDeliveryOrderId(resDTO.getFulfilmentNote().getFlDeliveryOrder().getDeliveryOrderId());
				getManagedBean().setDocMaintainRemarks(true);
				
				flBB.getManagedBean().setHideRemarks(true);
				sortOrderSummaryList(getManagedBean().getFlRequest().getFlDeliveryAddresses());
				
				flBB.initializeRemarkAndAttachement(resDTO.getFlOrder().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "poBasePage?faces-redirect=true");
			}
		}
		
		/**
		 * Retrieve purchase order information and display in UI
		 */
		public void viewContractOrder() {
			IFulfilmentOrderSO fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			
			String fulfilmentOrderId = originalServletRequest.getParameter("fulfilmentOrderId");
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			if(fulfilmentOrderId!=null) {
				reqDTO.setFlOrderId(Long.valueOf(fulfilmentOrderId));
				reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
				reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_ORDER);
				reqDTO.setCoLink(Boolean.TRUE);
				renewSesionObject();
				try {
					resDTO=fulfilmentOrderSO.retrieveFulfilmentOrderById(reqDTO);
				} catch (EpException e) {
					handleException(e);
					return;
				}
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				getManagedBean().setSelectedTabIndex(0);
				getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
				getManagedBean().setHyperLinkPage(true);
				getManagedBean().setTaskId(BpmOutcomeConst.BPM_VIEW);//for the purpose of hide the back button.
				getManagedBean().setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
				getManagedBean().setSelectedTabIndex(0);
				getManagedBean().setPmUser(reqDTO.getPmUser());
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				getManagedBean().setRemoveFactoring(false);
				getManagedBean().setDocMaintainRemarks(true);
				getManagedBean().setDocType(ParameterConst.DOCTYPE_CONTRACT_ORDER);
				getManagedBean().setDocMaintainRemarks(true);
				
				flBB.getManagedBean().setHideRemarks(true);
				sortOrderSummaryList(getManagedBean().getFlRequest().getFlDeliveryAddresses());
				
				flBB.initializeRemarkAndAttachement(resDTO.getFlOrder().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "coBasePage?faces-redirect=true");
			}
		}
		
	private void setTextBeforeSign(String outcome) {
		StringBuffer sb = new StringBuffer();
		sb.append(getManagedBean().getFulfilmentOrder().getDocNo()+"|");
		if(hasFactoringUserRole()) {
			sb.append(ParameterConst.DOCTYPE_FACTORING_COMPANY+"|");
		} else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
			sb.append(getOrderAmt()+"|");
		}
		sb.append(outcome+"|");
		sb.append(getFacesScopes().getUserSessionBean().getIdentificationNo());
		getManagedBean().setTextBeforeSign(sb.toString());		
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
	
	public void preRenderViewDOTable(){
		getManagedBean().getPendingDoDataModel().reloadData();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "doPendingTable.xhtml");
	}
		
}
