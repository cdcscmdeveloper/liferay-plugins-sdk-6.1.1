/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.po;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.supplier.IContractSupplierSO;
import my.ep.biz.service.fl.IFulfilmentYepSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.fee.settings.IFeeExemptionSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.supplier.ContractSupplierReqDTO;
import my.ep.dto.ct.supplier.ContractSupplierResDTO;
import my.ep.dto.fl.FlYepReqDTO;
import my.ep.dto.fl.FlYepResDTO;
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
import my.ep.dto.pm.fee.settings.FeeExemptionReqDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.codata.FLContractOrderDataType;
import my.ep.jaxb.fl.fc.FLFactoringRemoveReqType;
import my.ep.jaxb.fl.maintenace.cancel.codata.FLCOCancelReqType;
import my.ep.jaxb.fl.maintenace.cancel.podata.FLPOCancelReqType;
import my.ep.jaxb.fl.podata.FLPurchaseOrderDataType;
import my.ep.jaxb.fl.stopfullfillmentdata.FLStopFullfillmentDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.order.FlStopInstr;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.fee.settings.PmFeeExemption;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
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
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.fl.po.FulfilmentOrderManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Backing Bean for Fulfillment Order.
 *
 * @author Giribabu
 * @since 2/11/2013
 */
@ManagedBean(name="flOrderBB")
@ViewScoped
public class FulfilmentOrderBackingBean extends FulfilmentBaseBackingBean<FulfilmentOrderManagedBean> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String NAV_TO_CREATE_DO = "createDo";
	private static final String NAV_CR = "/xhtml/fl/contractrequest/crBasePage.xhtml";
	private static final String NAV_PR = "/xhtml/fl/purchaserequest/prBasePage.xhtml";
	private static final String NAV_PO = "/xhtml/fl/purchaseorder/poBasePage.xhtml";
	private static final String NAV_CO = "/xhtml/fl/contractorder/coBasePage.xhtml";
										
	private static Logger log = Logger.getLogger(FulfilmentOrderBackingBean.class);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "MM/dd/yy hh:mm a" );
	
	FulfilmentBackingBean flBB = new FulfilmentBackingBean();
	FlAttachment attachment = new FlAttachment(); //#28377
	
	/**
	 * Instantiates a new fulfilment order backing bean.
	 */
	public FulfilmentOrderBackingBean(){
	}
	
	/**
	 * close/manual payment fulfilment order.
	 *
	 *  navigate to acknowledge page.
	 *  @throws IOException 
	 */

  	public String closeManualPayment() throws IOException{
		
		IFulfilmentOrderSO fulfilementOrderSO = null;
		FulfilmentResDTO resDTO = null;
		validateCloseManualRequest();
		
		try{
			fulfilementOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			Object token = getFacesScopes().getBPMAuthenticationCtxToken(); //30663
			
			reqDTO.setOutcome(BpmOutcomeConst.BPM_CLOSE_MANUAL);
			reqDTO.setToken(token); //30663
			reqDTO.setFulfilmentOrder(getManagedBean().getFulfilmentOrder());
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setUserRole(getUserRole());
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			if(!flBB.getManagedBean().getRemarkList().isEmpty()){
				reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			}
			
			resDTO = fulfilementOrderSO.closeManualPayment(reqDTO);			
			
			if(resDTO.isSuccessful()) {
				successMessage(MessageConst.INFO_700072, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_PURCHASE_ORDER,getManagedBean().getFulfilmentOrder().getDocNo().toString(), Boolean.FALSE);
				/*String messageId = MessageConst.INFO_700005;
				String documentType = ParameterConst.DOCTYPE_PURCHASE_ORDER;
				String docNo = getManagedBean().getFulfilmentOrder().getDocNo();
				getManagedBean().setAllowCloseManualPay(false);
				
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(messageId, 
								new String[]{messagePrefix+getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, documentType)+messageSuffix, docNo}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));*/
				
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
				
			} else {
				throw new EpException(MessageConst.ERROR_740017);
			}
			
			
		}finally{
			fulfilementOrderSO = null;
		}
	}
  	
  	private void validateCloseManualRequest() {	
	
			/*validate remark cannot be null*/
			if(flBB.getManagedBean().getRemarkList().get(0).getRemark()==null || flBB.getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.txt.remarks",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			}
		
  	}
	
	/**
	 * save,submit Fulfillment order.
	 *
	 * * @param String action 
	 *  <p>submit - save data into database and BPM process will route to the next respective user.
	 * 	<p>save - save data into database.
	 *  navigate to acknowledge page.
	 *  @throws IOException 
	 */
   	public String fulfillmentOrderAction(String action) throws IOException {
		log.debug("FulfilmentOrderBackingBean.fulfillmentOrderAction()");
		IFulfilmentOrderSO fulfilmentOrderSO = null;
		IFulfilmentRequestSO fulfilmentSO = null;

		try {
			
			fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);

			validateFulfillmentOrder(action);			
			
			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flRequest = getManagedBean().getFlRequest();
			FlFulfilmentOrder flOrder = null;
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flOrder = getManagedBean().getFulfilmentOrder();
			}

			//#24638 check SAPOrderNo at status supp ack, cannot be null after Idd approved 
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE) &&
					getManagedBean().getFulfilmentOrder()!=null && getManagedBean().getFulfilmentOrder().getSapOrderNo() == null){
				String sapOrderNo = checkSapOrderNo(getManagedBean().getFulfilmentOrder().getDocNo());

				if(sapOrderNo!=null){
					getManagedBean().getFulfilmentOrder().setSapOrderNo(sapOrderNo);
				}			
			}
			
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			reqDTO.setFlFulfilmentRequest(flRequest);

			if(getManagedBean().isContractRequest() == Boolean.TRUE){
				reqDTO.setContractRequest(getManagedBean().isContractRequest());
			}else{
				reqDTO.setContractRequest(false);
			}
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_CARRYFORWARD)){ //28375
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put(ReportConst.PARAM_DOC_NO, getManagedBean().getFulfilmentOrder().getDocNo());
				paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
				reqDTO.setReportUrl("<a href='"+generateReportURL(ReportConst.FL_NGEP_PURCHASE_ORDER,paramMap)+ "'>"+getManagedBean().getFulfilmentOrder().getDocNo()+"</a>");
			}
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setTaskName(this.getManagedBean().getTaskName()); //28375
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
			//28377
			reqDTO.setAttachment(getManagedBean().getAttachment());
			//28377
			
			String messagePrefix = "";
			String messageSuffix = "";
			FulfilmentResDTO resDTO = null;
			if(getManagedBean().getPmDigiSign() != null){
				reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			}
			log.info("added checking include process name without space to cater for Oracle 12C ["+this.getClass().getSimpleName()+"][fulfillmentOrderAction]");
			if((action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) && !hasFactoringUserRole()){
				resDTO = fulfilmentOrderSO.acknowledgeFulfillmentOrder(reqDTO);
				//28508-start
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)){
					fulfilmentSO.checkFulfilmentItemByFLReqId(flRequest.getFulfilmentReqId());
				}
				//28508-end
			}
			//#34686 start
			else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_AP58_PROCEED) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
				if(getManagedBean().getAttachment().getFileName() != null){
					FulfilmentReqDTO AttachreqDTO = new FulfilmentReqDTO();
					AttachreqDTO.setAttachment(this.getManagedBean().getAttachment());
					AttachreqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
					AttachreqDTO.setFlRemarkList(flBB.getManagedBean().getRemarkList());
					AttachreqDTO.setFulfilmentOrder(this.getManagedBean().getFulfilmentOrder());
					resDTO = fulfilmentOrderSO.saveAP58AL(AttachreqDTO);
				}
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_AP58_PROCEED)){
					reqDTO.setPmDigiSign(this.getManagedBean().getPmDigiSign());
					resDTO = fulfilmentOrderSO.proceedAP58(reqDTO);
				}
     		}
			//#34686 end
			else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_STOP_FULFILMENT)){
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
					&& (getManagedBean().getProcessName().equalsIgnoreCase(FLProcessNameConst.FC_REMOVAL_REQUEST_PROCESS_NAME) || getManagedBean().getProcessName()
							.equalsIgnoreCase(FLProcessNameConst.FC_REMOVAL_REQUEST_PROCESS_NAME.replaceAll("\\s", "")))){
				resDTO = fulfilmentOrderSO.removeFactoringComp(reqDTO);
				messagePrefix = ResourceBundleUtils.getMessage("lbl.fl.rfc.for",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale());
			}	
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700005;
				String documentType = ParameterConst.DOCTYPE_PURCHASE_ORDER;
				String docNo = getManagedBean().getFulfilmentOrder().getDocNo();
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
					messageId = MessageConst.INFO_700011;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					messageId = MessageConst.INFO_700007;
				}
				//30663 start
			    else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
				    messageId = MessageConst.INFO_700004;
			    }
				//30663 end
				//#28377 - razin
				else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_AP58_PROCEED)) {
					messageId = MessageConst.INFO_700005;
				}
				//#28377 - end razin
				else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
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
					messageId = MessageConst.INFO_745016;
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
		if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_CARRYFORWARD)){ //28375
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
			if (finResDTO.getPmFinancialOrg().getPmOrgProfiles() == null || finResDTO.getPmFinancialOrg().getPmOrgProfiles().size() == 0 ) {
				throw new EpException(MessageConst.ERROR_740020, new String[]{ResourceBundleUtils.getMessage("lbl.txt.factoringUser",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			}
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setOrgProfileId(finResDTO.getPmFinancialOrg().getPmOrgProfiles().get(0).getOrgProfileId());
			UserResDTO userResDTO = userSO.retrieveUserListByOrgProfileId(userReqDTO);
			if(userResDTO.getUserList() == null || userResDTO.getUserList().isEmpty()){
				throw new EpException(MessageConst.ERROR_740020, new String[]{ResourceBundleUtils.getMessage("lbl.txt.factoringUser",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			}
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
		doMB.renewSesionObject();
		doMB.setDeliveryOrder(resDTO);
		for(FlDeliveryAddress deliveryAddress : resDTO.getFlFulfilmentRequest().getFlDeliveryAddresses()) {
			for(FlFulfilmentItemAddr itemAddr : deliveryAddress.getFlFulfilmentItemAddr()) {
				BigDecimal remainOrderQty=itemAddr.getOrderedQty().subtract(itemAddr.getSumDeliveredQty()).add(itemAddr.getRejectedQty());
				if(itemAddr.getDeliveredQty().doubleValue()==0) {
					itemAddr.setDeliveredQty(remainOrderQty);
				}
				if(itemAddr.getDeliveredQty().doubleValue()>0){
					if(itemAddr.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)){
						BigDecimal unitPriceCal=itemAddr.getGstUnitPrice();
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
			log.info("added checking include process name without space to cater for Oracle 12C ["+this.getClass().getSimpleName()+"][preRenderViewCR]");
			if (this.getManagedBean().getProcessName().equalsIgnoreCase(FLProcessNameConst.STOP_FULFILMENT_REQUEST_PROCESS_NAME) || getManagedBean().getProcessName()
					.equalsIgnoreCase(FLProcessNameConst.STOP_FULFILMENT_REQUEST_PROCESS_NAME.replaceAll("\\s", ""))){
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
				if(FLProcessNameConst.RESUBMIT_ORDER_CANCELLATION.equalsIgnoreCase(this.getManagedBean().getProcessName()) || getManagedBean().getProcessName()
						.equalsIgnoreCase(FLProcessNameConst.RESUBMIT_ORDER_CANCELLATION.replaceAll("\\s", ""))){
					FLCOCancelReqType coCancelDataType = (FLCOCancelReqType)obj;
					flReqDTO.setDocType(ParameterConst.DOCTYPE_PURCHASE_ORDER);
					flReqDTO.setFlOrderId(coCancelDataType.getOrderCancelDetails().getOrderDetails().getOrderId());
					if(coCancelDataType.getOrderCancelDetails().getErrorIds() != null && coCancelDataType.getOrderCancelDetails().getErrorIds().length() > 0){
						getManagedBean().setAutoUpdate(Boolean.FALSE);
						getManagedBean().setGfmasError(true);
						FulfilmentReqDTO errReq = new FulfilmentReqDTO();
						String errorId = coCancelDataType.getOrderCancelDetails().getErrorIds();
						if(coCancelDataType.getOrderCancelDetails().getErrorIds().contains(",")) {
							errorId = coCancelDataType.getOrderCancelDetails().getErrorIds().split(",")[0];
						}
						//33177-start
						if(coCancelDataType.getOrderCancelDetails().getErrorIds().contains(";")) {
							errorId = coCancelDataType.getOrderCancelDetails().getErrorIds().split(";")[0];
						}
						//33177-End
						errReq.setErrorId(new Long(errorId));
						FulfilmentResDTO errRes = fulfilmentSO.retrieveErrorContent(errReq);
						getManagedBean().setGfmasErrorCode(errRes.getErrorCode());
						getManagedBean().setGfmasErrorDesc(MessageUtils.getMessage("ERR-"+errRes.getErrorCode(), new String[]{}, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale()));
						getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
					}
					getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_CANCEL});
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
			if(null != getManagedBean().getButtonVal() && getManagedBean().getButtonVal().equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				if(hasGovernmentUserRole()){
					//remarks
					IFulfilmentOrderSO fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
					Map<String, Long> docTypesMap = new HashMap<String, Long>();
					docTypesMap.clear();
					FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
					docTypesMap.put(getManagedBean().getFulfilmentOrder().getDocType(), getManagedBean().getFulfilmentOrder().getFulfilmentOrderId());
					docTypesMap.put(getManagedBean().getFulfilmentOrder().getFlFulfilmentRequest().getDocType(), getManagedBean().getFulfilmentOrder().getFlFulfilmentRequest().getFulfilmentReqId());
					reqDTO.setDocTypesMap(docTypesMap);
					
					FulfilmentResDTO resDTO = fulfilmentOrderSO.retrieveRemarks(reqDTO);
					flBB.initializeRemark(getManagedBean().getFulfilmentOrder().getFlWorkflowStatus(), resDTO.getRemarkList());
				}
				
			}
			
			//34316-Start
			if(getManagedBean().getFulfilmentOrder().isYep()){
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
				flReqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_ORDER);
				flReqDTO.setFlOrderId(getManagedBean().getFulfilmentOrder().getFulfilmentOrderId());
				
				FulfilmentResDTO resDTO = null;
				resDTO= getFulfilmentOrderSO().validateYepPOCOCurrentStatus(flReqDTO);
				getManagedBean().setYepButtonFlag(resDTO.getYepButtonFlag());
			}
			//34316-End
		}

		getEspkbCommitmentNo();
		
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
			log.info("added checking include process name without space to cater for Oracle 12C ["+this.getClass().getSimpleName()+"][preRenderViewPR]");
			if(null==this.getManagedBean().getProcessName() || this.getManagedBean().getProcessName().equalsIgnoreCase("")){
				log.info("Process name is NUll");
			} else if (FLProcessNameConst.STOP_FULFILMENT_REQUEST_PROCESS_NAME.equalsIgnoreCase(this.getManagedBean().getProcessName()) || getManagedBean().getProcessName()
					.equalsIgnoreCase(FLProcessNameConst.STOP_FULFILMENT_REQUEST_PROCESS_NAME.replaceAll("\\s", ""))) {
				FLStopFullfillmentDataType stopFlData = (FLStopFullfillmentDataType)obj;
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
				flReqDTO.setFlOrderId(stopFlData.getOrderId());
				
				FulfilmentResDTO resDTO = getFulfilmentOrderSO().retrieveFulfilmentOrderById(flReqDTO);
				getManagedBean().setStopRequestId(stopFlData.getStopRequestId().longValue());
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				getManagedBean().setContractRequest(Boolean.TRUE);
			} else if(FLProcessNameConst.FC_REMOVAL_REQUEST_PROCESS_NAME.equalsIgnoreCase(this.getManagedBean().getProcessName()) || getManagedBean().getProcessName()
					.equalsIgnoreCase(FLProcessNameConst.FC_REMOVAL_REQUEST_PROCESS_NAME.replaceAll("\\s", ""))){
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
				FulfilmentResDTO resDTO = new FulfilmentResDTO();
				FLFactoringRemoveReqType removeReqType = (FLFactoringRemoveReqType)obj;
				flReqDTO.setDocType(ParameterConst.DOCTYPE_FACTORING_COMPANY);
				flReqDTO.setFlOrderId(removeReqType.getOrderId());
				resDTO = getFulfilmentOrderSO().retrieveFulfilmentOrderById(flReqDTO);
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				getManagedBean().setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
				getManagedBean().setDocType(resDTO.getFlOrder().getFlFulfilmentRequest().getDocType());
				FulfilmentBackingBean flBB = new FulfilmentBackingBean(resDTO.getFlOrder().getDocType(), resDTO.getFlOrder().getFlFulfilmentRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
				flBB.getManagedBean().setHideRemarks(false);
				flBB.initializeRemarkAndAttachement(resDTO.getFlOrder().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
			}else {
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
				FulfilmentResDTO resDTO = new FulfilmentResDTO();
				IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
				if(FLProcessNameConst.RESUBMIT_ORDER_CANCELLATION.equalsIgnoreCase(this.getManagedBean().getProcessName()) || getManagedBean().getProcessName()
						.equalsIgnoreCase(FLProcessNameConst.RESUBMIT_ORDER_CANCELLATION.replaceAll("\\s", ""))){
					FLPOCancelReqType poCancelReqType = (FLPOCancelReqType)obj;
					flReqDTO.setDocType(ParameterConst.DOCTYPE_PURCHASE_ORDER);
					flReqDTO.setFlOrderId(poCancelReqType.getOrderCancelDetails().getOrderDetails().getOrderId());
					if(poCancelReqType.getOrderCancelDetails().getErrorIds() != null && poCancelReqType.getOrderCancelDetails().getErrorIds().length() > 0){
						getManagedBean().setAutoUpdate(Boolean.FALSE);
						getManagedBean().setGfmasError(true);
						FulfilmentReqDTO errReq = new FulfilmentReqDTO();
						String errorId = poCancelReqType.getOrderCancelDetails().getErrorIds();
						if(poCancelReqType.getOrderCancelDetails().getErrorIds().contains(",")) {
							errorId = poCancelReqType.getOrderCancelDetails().getErrorIds().split(",")[0];
						}
						//33177-start
						if(poCancelReqType.getOrderCancelDetails().getErrorIds().contains(";")) {
							errorId = poCancelReqType.getOrderCancelDetails().getErrorIds().split(";")[0];
						}
						//33177-end
						errReq.setErrorId(new Long(errorId));
						FulfilmentResDTO errRes = fulfilmentSO.retrieveErrorContent(errReq);
						getManagedBean().setGfmasErrorCode(errRes.getErrorCode());
						getManagedBean().setGfmasErrorDesc(MessageUtils.getMessage("ERR-"+errRes.getErrorCode(), new String[]{}, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale()));
						getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
					}
					getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_CANCEL});
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
						getManagedBean().setGfmasErrorCode(errRes.getErrorCode());
						getManagedBean().setGfmasErrorDesc(MessageUtils.getMessage("ERR-"+errRes.getErrorCode(), new String[]{}, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale()));
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
			
			//34316-Start
			if(getManagedBean().getFulfilmentOrder().isYep()){
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
				flReqDTO.setDocType(ParameterConst.DOCTYPE_PURCHASE_ORDER);
				flReqDTO.setFlOrderId(getManagedBean().getFulfilmentOrder().getFulfilmentOrderId());
				
				FulfilmentResDTO resDTO = null;
				resDTO= getFulfilmentOrderSO().validateYepPOCOCurrentStatus(flReqDTO);
				getManagedBean().setYepButtonFlag(resDTO.getYepButtonFlag());
			}
			//34316-End
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
		
		//#24969
		Long status = getManagedBean().getFulfilmentOrder().getStatus().getStatusId();
		if(getManagedBean().getFactoringCheck().equalsIgnoreCase("true") && status.equals(FulfilmentStatusConst.PO_PENDING_SUPPLIER_ACKNOWLEDGEMENT)) {
			RequestContext.getCurrentInstance().execute("popUpFactoredDlg.show()");
		}
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
				getManagedBean().getFulfilmentOrder().setFactoringOrgName(resDTO.getAddressTypeList().get(0).getPmFinancialOrg().getFinOrgName());
				getManagedBean().getFulfilmentOrder().setFactoringAddress(convertFinAccAddressToFlAddress(resDTO.getAddressTypeList().get(0)));
			}
		}
	}
		
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
				getOrderSummaryGrandTotal();
				
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
		public String backToPervious() throws PortalException, SystemException, IOException {
			if(getManagedBean().getBackToPrevious() !=null){
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				flBB.getManagedBean().setRemarkList(getManagedBean().getPreviousRemarkList());
				flBB.getManagedBean().setAttachmentList(getManagedBean().getPreviousAttachmentList());
				flBB.getManagedBean().setHideRemarks(getManagedBean().isPrevHideRemarks());
				return getManagedBean().getBackToPrevious();
			}
			else{
				FacesContext.getCurrentInstance().getExternalContext().redirect(
						 EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_APPLICATION_URL) + "notifications");
				return null;
			}
		}

		/* #28375 - Beginning - Logan */
		public String carryForward(String action) throws PortalException, SystemException, IOException {

			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FeeExemptionReqDTO reqFeeDTO = new FeeExemptionReqDTO();
			FulfilmentResDTO resDTO = null;
			FeeExemptionResDTO resFeeDTO = null;

			IFeeExemptionSO feeExemptionsSO = (IFeeExemptionSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IFeeExemptionSO.class);
			IFulfilmentOrderSO fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
			IFulfilmentYepSO fulfilmentYepSO = (IFulfilmentYepSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentYepSO.class);
			String messagePrefix = "";
			String messageSuffix = "";
			FlYepReqDTO yepReqDTO = new FlYepReqDTO();
			FlYepResDTO yepResDTO =null;
			
			reqFeeDTO.setOrgProfileId((long) 1955);
			resFeeDTO = feeExemptionsSO.getFeeExemptionsYepProcessing(reqFeeDTO);

			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			Calendar currTime = Calendar.getInstance();
			
			if(action==BpmOutcomeConst.BPM_CARRYFORWARD){
				yepReqDTO.setFlOrder(getManagedBean().getFulfilmentOrder());
				reqDTO.setFulfilmentOrder(getManagedBean().getFulfilmentOrder());
				yepReqDTO.setDocNo(getManagedBean().getFulfilmentOrder().getDocNo());
				yepReqDTO.setCarryFoward(true);
				yepReqDTO.setProgressStatus((long) 0);
				yepReqDTO.setUserRole(getUserRole());
				log.info("User role is : " +getUserRole());
		
				yepResDTO=fulfilmentYepSO.updateCarryForwardFlYepTaskList(yepReqDTO);
				log.info("Carry Forwarded");
				
				if(yepResDTO.isSuccessful()) {
					String messageId = MessageConst.INFO_700005;
					String documentType = ParameterConst.DOCTYPE_PURCHASE_ORDER;
					String docNo = getManagedBean().getFulfilmentOrder().getDocNo();
					String docType = getManagedBean().getFulfilmentOrder().getDocType();
					//#33659 start
					/*if(docType.equalsIgnoreCase("PO"))
						messagePrefix="Purchase Order";
					else
						messagePrefix="Contract Order";*/
					if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CARRYFORWARD)) {
						if(docType.equalsIgnoreCase("PO")){
							messageId = MessageConst.INFO_745022;
						}else{
							messageId = MessageConst.INFO_745028;
						}
					}
					/*FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(messageId, 
									new String[]{messagePrefix+getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, documentType)+messageSuffix, docNo}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));*/
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(messageId, 
									new String[]{docNo}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					//#33659 end
				} else {
					throw new EpException(MessageConst.ERROR_700020);
				}
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			
			
			}
			

			try {
				Date d1 = df.parse(df.format(currTime.getTime()));
				Date d2 = df.parse(df.format(resFeeDTO.getFeeExemptions().get(2).getEffDate()));
				Date d3 = df.parse(df.format(resFeeDTO.getFeeExemptions().get(2).getExpDate()));
				d3.setTime(d3.getTime() + TimeUnit.HOURS.toMillis(23));
				d3.setTime(d3.getTime() + TimeUnit.MINUTES.toMillis(59));
				d3.setTime(d3.getTime() + TimeUnit.SECONDS.toMillis(59));
				log.info("d2 is : " +d2);
				log.info("d3 is : " +d3);

				if (getManagedBean().getCarryForward() != null) {
				
				  if ((d1.compareTo(d3) < 0||d1.compareTo(d3)==0) && (d1.compareTo(d2) > 0||d1.compareTo(d2)==0))  {
						reqDTO.setOutcome(FulfilmentConst.IDD_ACTION_CODE_CREATE_YEP);
					}else{
						throw new EpException(MessageConst.ERROR_776107);
					}
					reqDTO.setOutcome(FulfilmentConst.IDD_ACTION_CODE_CREATE_YEP);
					validateFulfillmentOrder(action);
					Object token = getFacesScopes().getBPMAuthenticationCtxToken();
					
					
					reqDTO.setFlOrderId(getManagedBean().getFulfilmentOrder().getFulfilmentOrderId());
					
					reqDTO.setFlDocNo(getManagedBean().getFulfilmentOrder().getDocNo());
					reqDTO.setYep(getManagedBean().getFulfilmentOrder().isYep());
					reqDTO.setStartDate(getManagedBean().getFlRequest().getStartDate());
					log.info("getFacesScopes().getUserSessionBean()"+getFacesScopes().getUserSessionBean());
					log.info("getUserRole("+getUserRole());
					reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
					reqDTO.setUserRole(getUserRole());
					reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
					reqDTO.setFlFulfilmentRequest(getManagedBean().getFlRequest());
					reqDTO.setSmSupplier(getManagedBean().getFlRequest().getSmSupplier());
					if(!flBB.getManagedBean().getRemarkList().isEmpty()){
						reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
					}
					if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_PROCEED)){
						yepReqDTO.setFlOrder(getManagedBean().getFulfilmentOrder());
						yepReqDTO.setDocType(getManagedBean().getFulfilmentOrder().getDocType());
						yepReqDTO.setDocNo(getManagedBean().getFulfilmentOrder().getDocNo());
						yepReqDTO.setFlRequest(getManagedBean().getFulfilmentOrder().getFlFulfilmentRequest());
						yepReqDTO.setCarryFoward(true);
						yepReqDTO.setProgressStatus((long) 4);
						yepReqDTO.setUserRole(getUserRole());
						//#33729 start
						yepReqDTO.setPmuser(getFacesScopes().getUserSessionBean());
						//#33729 end
						yepResDTO=fulfilmentYepSO.updateCarryForwardFlYepTaskList(yepReqDTO);
						log.info("Update task list carry forward as true");
					}
					reqDTO.setTaskId(getManagedBean().getTaskId());
					reqDTO.setOutcome(FulfilmentConst.IDD_ACTION_CODE_CREATE_YEP);
					
										
					if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_PROCEED)){
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put(ReportConst.PARAM_DOC_NO, getManagedBean().getFulfilmentOrder().getDocNo());
						paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
						reqDTO.setReportUrl("<a href='"+generateReportURL(ReportConst.FL_NGEP_PURCHASE_ORDER,paramMap)+ "'>"+getManagedBean().getFulfilmentOrder().getDocNo()+"</a>");
					}
					reqDTO.setTaskId(this.getManagedBean().getTaskId());
					reqDTO.setTaskName(this.getManagedBean().getTaskName());
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
					reqDTO.setDocType(getManagedBean().getFulfilmentOrder().getDocType());
					
					if(getManagedBean().getPmDigiSign() != null){
						reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
					}
					
					if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_PROCEED))
						resDTO = fulfilmentOrderSO.acknowledgeForCarryForward(reqDTO);
					
					yepReqDTO.setFlOrder(getManagedBean().getFulfilmentOrder());
					yepReqDTO.setDocNo(getManagedBean().getFulfilmentOrder().getDocNo());
					yepReqDTO.setFlRequest(getManagedBean().getFulfilmentOrder().getFlFulfilmentRequest());
					yepReqDTO.setCarryFoward(true);
					yepReqDTO.setProgressStatus((long) 4);
					yepReqDTO.setUserRole(getUserRole());
					//#33729 start
					yepReqDTO.setPmuser(getFacesScopes().getUserSessionBean());
					//#33729 end
					yepResDTO=fulfilmentYepSO.updateCarryForwardFlYepTaskList(yepReqDTO);
					log.info("Progress status set to 4");
					
					if(resDTO.isSuccessful()) {
						String messageId = MessageConst.INFO_700033;
						String documentType = getManagedBean().getFulfilmentOrder().getDocType();
						String docNo = getManagedBean().getFulfilmentOrder().getDocNo();
						
						String docType = getManagedBean().getFulfilmentOrder().getDocType();
						//#33725 start
						if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_PROCEED)) {
							if(docType.equalsIgnoreCase(FulfilmentConst.DOC_TYPE_PO)){
								messageId = MessageConst.INFO_745023;
							}else{
								messageId = MessageConst.INFO_745123;
							}
						}
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(messageId, 
										new String[]{docNo}, 
										MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
						//#33725 end
					} else {
						throw new EpException(MessageConst.ERROR_700020);
					}
					return CommonUIConst.ACKNOWLEDGE_PAGE;
					
					/*Ending*/
				
			}

				else {
					FacesContext
							.getCurrentInstance()
							.getExternalContext()
							.redirect(
									EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_APPLICATION_URL)
											+ "notifications");

				}

			} catch (ParseException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				throw new EpException(MessageConst.ERROR_000001);//35462
				
			}
			return messageSuffix;	
						
					
			
			}
		
		public static String getMessageCommon(String name) {
			return ResourceBundleUtils.getMessage(name,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		}
		
		public void successMessage(String messageCode, String messageCommon, String docNo, boolean isAcknowledgeRequired) {
			
			if (isAcknowledgeRequired) {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
			}
		}
		
		/*#28375 - Ending*/

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
			if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT) && hasFactoringUserRole()){
				validateFulfillmentOrder(outcome);
				RequestContext.getCurrentInstance().execute("cancelConfirmDlg.show()");
				return null;
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
			log.info("#34686: Original Data: "+getManagedBean().getTextBeforeSign()); //#34686 log for original_data
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
			if(((hasSupplierRole() && !hasGovtSellerRole()))) {
				setTextBeforeSign(getManagedBean().getButtonAction());
				updateSpkiDigiSignData(getManagedBean().getPmDigiSign(), getManagedBean().getFulfilmentOrder().getDocType(), 
						getManagedBean().getFulfilmentOrder().getFulfilmentOrderId(), getManagedBean().getFulfilmentOrder().getDocNo(), 
						getManagedBean().getTextBeforeSign());					
			}
			//#34686 start
			if(getManagedBean().getPmDigiSign().getOriginalData().isEmpty()) {
				setTextBeforeSign(getManagedBean().getButtonAction());
				getManagedBean().getPmDigiSign().setOriginalData(getManagedBean().getTextBeforeSign());
			}
			//#34686 end
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
				
				boolean isFactoredSelected=resDTO.getFlOrder().isFactored();
				if(isFactoredSelected){
					orderBackingBean.getManagedBean().setFactoringCheck("true");
				} else {
					orderBackingBean.getManagedBean().setFactoringCheck("false");
				}
				
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
				FacesContext fc = FacesContext.getCurrentInstance();
				ContractRequestBackingBean crBB = new ContractRequestBackingBean();
				crBB.renewSesionObject();
				crBB = new ContractRequestBackingBean();
				crBB.initializeCRBeans();
				crBB.getManagedBean().setSelectedTabIndex(0);
				crBB.getManagedBean().setDocProcess(docType);
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
				crBB.getManagedBean().setPrevHideRemarks(flBB.getManagedBean().isHideRemarks());
				flBB.getManagedBean().setHideRemarks(true);
				IContractSupplierSO contractSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSupplierSO.class);
				ContractSupplierReqDTO contractSupplierReqDTO = new ContractSupplierReqDTO();
				if(null != resDTO.getFlFulfilmentRequest()){
					if(resDTO.getFlFulfilmentRequest().getCtContract().isFactored()){
						Long supplieContractId=resDTO.getFlFulfilmentRequest().getCtContract().getContractId();
						contractSupplierReqDTO.setContractId(supplieContractId);
						ContractSupplierResDTO contractSupplierResDTO = contractSupplierSO.retrieveFactoringByContract(contractSupplierReqDTO);
						if(contractSupplierResDTO.getFactoring() != null){
							resDTO.getFlFulfilmentRequest().getCtContract().setCtFactoring(contractSupplierResDTO.getFactoring());
							crBB.getManagedBean().setFactored(true);
							if(contractSupplierResDTO.getFactoring().getFinancialOrg() != null){
								crBB.getSelectedFactoringCompDtl(contractSupplierResDTO.getFactoring());
							}
						}else{
							crBB.getManagedBean().setFactored(false);
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
				
				crBB.setFulfilmentItemAddr(crBB.getManagedBean().getFlRequest());
				crBB.getManagedBean().setMinistryProfileId(retrieveMinistryOrgProfile(
				getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId()).getOrgProfileId());
				return NAV_CR;
			}if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_REQUEST)){
				IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
				FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
				reqDTO.setFlRequestId(getManagedBean().getFlRequest().getFulfilmentReqId());
				reqDTO.setFlRequestLink(true);
				FulfilmentResDTO resDTO = requestSO.retrieveFulfilmentRequest(reqDTO);
				FacesContext fc = FacesContext.getCurrentInstance();
				PurchaseRequestBackingBean prBB = new PurchaseRequestBackingBean();
				prBB.initializePRBeans();
				prBB.getManagedBean().setSubmitButtonStatus(false);
				prBB.getManagedBean().setDocProcess(docType);
				prBB.getManagedBean().setBackToDocument(fc.getViewRoot().getViewId());
				prBB.getManagedBean().setHyperLinkPage(true);
				prBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
				prBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());
				prBB.setFulfilmentRequestField(resDTO);
				prBB.getManagedBean().setPmUser(reqDTO.getPmUser());
				prBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
				prBB.getManagedBean().setDeclarationCheck(true);
				prBB.getManagedBean().setPrevHideRemarks(flBB.getManagedBean().isHideRemarks());
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
				}finally{
					orderSO = null;
				}
				
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				getManagedBean().setSelectedTabIndex(0);
//				getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
//				getManagedBean().setHyperLinkPage(true);
				getManagedBean().setTaskId(BpmOutcomeConst.BPM_VIEW);//for the purpose of hide the back button.
				getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				getManagedBean().setSelectedTabIndex(0);
				getManagedBean().setPmUser(reqDTO.getPmUser());
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				getManagedBean().setRemoveFactoring(false);
				getManagedBean().setDocMaintainRemarks(true);
				getManagedBean().setDocType(ParameterConst.DOCTYPE_PURCHASE_ORDER);
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
				}finally{
					fulfilmentOrderSO = null;
				}
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				getManagedBean().setSelectedTabIndex(0);
//				getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
//				getManagedBean().setHyperLinkPage(true);
				getManagedBean().setTaskId(BpmOutcomeConst.BPM_VIEW);//for the purpose of hide the back button.
				getManagedBean().setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
				getManagedBean().setSelectedTabIndex(0);
				getManagedBean().setPmUser(reqDTO.getPmUser());
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				getManagedBean().setRemoveFactoring(false);
				getManagedBean().setDocMaintainRemarks(true);
				getManagedBean().setDocType(ParameterConst.DOCTYPE_CONTRACT_ORDER);
				getManagedBean().setDocMaintainRemarks(true);
				getEspkbCommitmentNo();
				flBB.getManagedBean().setHideRemarks(true);
				sortOrderSummaryList(getManagedBean().getFlRequest().getFlDeliveryAddresses());
				flBB.initializeRemarkAndAttachement(resDTO.getFlOrder().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "coBasePage?faces-redirect=true");
			}
		}
		
		/**
		 * Sets the text before sign.
		 *
		 * @param outcome the new text before sign
		 */
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
	
		/**
		 * Pre render view do table.
		 */
		public void preRenderViewDOTable(){
			renewSesionObject();
			getManagedBean().getPendingDoDataModel().reloadData();
			getDocStatusList();
			getDocDeliveryOrderStatusList();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "doPendingTable.xhtml");
		}
	

		public void getDocDeliveryOrderStatusList() {
			SelectItem[] items = null;
			FacesScopes face = new FacesScopes();
			
			try{
				
				List<String> deliveryStatusDescs = new ArrayList<String>();
				List<Long> deliveryStatusIds = new ArrayList<Long>();
				deliveryStatusIds.add(FulfilmentStatusConst.DO_PENDING_ACKNOWLEDGEMENT);
				deliveryStatusIds.add(FulfilmentStatusConst.DO_ACKNOWLEDGED);
				deliveryStatusIds.add(FulfilmentStatusConst.DO_PENDING_MODIFICATION_APRROVAL);
				deliveryStatusIds.add(FulfilmentStatusConst.DO_PENDING_CANCELLATION_APPROVAL);
				deliveryStatusIds.add(FulfilmentStatusConst.DO_PENDING_CANCELLATION_SUBMISSION);
				deliveryStatusIds.add(FulfilmentStatusConst.DO_CLOSED_DUE_TO_AP59);
				
				Map<String, Long> deliveryStatusMap = new HashMap<String, Long>();
				for(Long deliveryStatusId : deliveryStatusIds){
					if(deliveryStatusMap.get(deliveryStatusId) == null){
						deliveryStatusMap.put(getStatusDescById(deliveryStatusId),deliveryStatusId);
						deliveryStatusDescs.add(getStatusDescById(deliveryStatusId));
					}
				}
				
				items = new SelectItem[deliveryStatusDescs.size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						face.getUserLocale()));
				int i = 1;
				for (String deliveryStatusDesc : deliveryStatusDescs) {
					items[i++] = new SelectItem(deliveryStatusMap.get(deliveryStatusDesc),deliveryStatusDesc);
				}
				
			}catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
								e.getMessage()));
			}
			
			getManagedBean().setDocDeliveryOrderStatusList(items);
		}
		
		public void getDocStatusList() {
			SelectItem[] items = null;
			FacesScopes face = new FacesScopes();

			try {
				List<String> statusDescs = new ArrayList<String>();
				List<Long> statusIds = new ArrayList<Long>();
				statusIds.add(FulfilmentStatusConst.PO_PENDING_PARTIAL_FL);
				statusIds.add(FulfilmentStatusConst.PO_PENDING_FULFILMENT);
				//statusIds.add(FulfilmentStatusConst.PO_PENDING_BOTH);
				
				Map<String, Long> statusMap = new HashMap<String, Long>();
				for(Long statusId : statusIds){
					if(statusMap.get(statusId) == null){
						statusMap.put(getStatusDescById(statusId), statusId);
						statusDescs.add(getStatusDescById(statusId));
					}
				}
				items = new SelectItem[statusDescs.size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						face.getUserLocale()));
				int i = 1;
				for (String statusDesc : statusDescs) {
					items[i++] = new SelectItem(statusMap.get(statusDesc),statusDesc);
				}
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
								e.getMessage()));
			}

			getManagedBean().setDocStatusList(items); ;
		}

		/**
		 * Pre render view do dash board table.
		 */
		public void preRenderViewDODashBoardTable(){
			IFulfilmentOrderSO orderSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			if(!getManagedBean().getStatusIds().isEmpty()){
				reqDTO.setStatusIds(getManagedBean().getStatusIds());
			}
		
			FulfilmentResDTO resDTO = orderSO.countPendingDo(reqDTO);
			getManagedBean().setTotalPendingFl(resDTO.getTotalPendingFl());
			getManagedBean().setTotalPendingPartialFl(resDTO.getTotalPendingPartialFl());
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "doPendingTableDashBoard.xhtml");
		}
	
		/**
		 * Pre render view fl dash board table.
		 */
		public void preRenderViewFlDashBoardTable(){
			IFulfilmentOrderSO orderSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			List<PmUserOrg> pmUserOrgs = PmCommonUtil.retrieveActiveOrg(getFacesScopes().getUserSessionBean().getUserId());
			if(pmUserOrgs.get(0) == null){
				log.info("User does not have any active orgs.");
			}
			if(pmUserOrgs.get(0) != null || pmUserOrgs.get(0).getPmOrgProfile() != null){
				reqDTO.setOrgProfileId(pmUserOrgs.get(0).getPmOrgProfile().getOrgProfileId());
			}
			if(!getManagedBean().getStatusIds().isEmpty()){
				reqDTO.setStatusIds(getManagedBean().getStatusIds());
			}
		
			FulfilmentResDTO resDTO = orderSO.countPendingFl(reqDTO);
			getManagedBean().setTotalPendingFl(resDTO.getTotalPendingFl());
			getManagedBean().setTotalPendingPartialFl(resDTO.getTotalPendingPartialFl());
			getManagedBean().setTotalPendingSupplierAck(resDTO.getTotalPendingSupplierAck());
			getManagedBean().setTotalPendingInvoice(resDTO.getTotalPendingInvoice());
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "pendingFulfilment.xhtml");
		}
	
	/**
	 * Selected order type.
	 *
	 * @param e the e
	 */
	public void getSelectedDocStatusDesc(AjaxBehaviorEvent e) {
		Long id = (Long) e.getComponent().getAttributes().get("value");

		if(id!=null) {
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			getManagedBean().getStatusIds().clear();
			if(id.equals(FulfilmentStatusConst.PO_PENDING_PARTIAL_FL)){
				getManagedBean().getStatusIds().add(FulfilmentStatusConst.CO_PENDING_PARTIAL_FL);
				getManagedBean().getStatusIds().add(FulfilmentStatusConst.PO_PENDING_PARTIAL_FL);
			}else if(id.equals(FulfilmentStatusConst.PO_PENDING_FULFILMENT)){
				getManagedBean().getStatusIds().add(FulfilmentStatusConst.CO_PENDING_FULFILMENT);
				getManagedBean().getStatusIds().add(FulfilmentStatusConst.PO_PENDING_FULFILMENT);
			}
		}
		if(id == null){
			getManagedBean().getStatusIds().clear();
		}
		getManagedBean().getPendingDoDataModel().reloadData();
	}
	
	/**
	 * Selected order type.
	 *
	 * @param e the e
	 */
	public void getSelectedFlDocStatusDesc(AjaxBehaviorEvent e) {
		Long docStatus = (Long) e.getComponent().getAttributes().get("value");
		onFlStatusChange(docStatus);
	}
	
	/**
	 * On status changed.
	 * @param docStatus
	 */
	public void onFlStatusChange(Long docStatus) {
		if(docStatus!=null) {
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			getManagedBean().getStatusIds().clear();
			if(docStatus.equals(FulfilmentStatusConst.PO_PENDING_PARTIAL_FL)){
				getManagedBean().getStatusIds().add(FulfilmentStatusConst.CO_PENDING_PARTIAL_FL);
				getManagedBean().getStatusIds().add(FulfilmentStatusConst.PO_PENDING_PARTIAL_FL);
			}else if(docStatus.equals(FulfilmentStatusConst.PO_PENDING_FULFILMENT)){
				getManagedBean().getStatusIds().add(FulfilmentStatusConst.CO_PENDING_FULFILMENT);
				getManagedBean().getStatusIds().add(FulfilmentStatusConst.PO_PENDING_FULFILMENT);
			}else if(docStatus.equals(FulfilmentStatusConst.PO_PENDING_INVOICE)){
				getManagedBean().getStatusIds().add(FulfilmentStatusConst.CO_PENDING_INVOICE);
				getManagedBean().getStatusIds().add(FulfilmentStatusConst.PO_PENDING_INVOICE);
			}else if(docStatus.equals(FulfilmentStatusConst.PO_PENDING_SUPPLIER_ACKNOWLEDGEMENT)){
				getManagedBean().getStatusIds().add(FulfilmentStatusConst.CO_PENDING_SUPPLIER_ACKNOWLEDGEMENT);
				getManagedBean().getStatusIds().add(FulfilmentStatusConst.PO_PENDING_SUPPLIER_ACKNOWLEDGEMENT);
			}
		}
		if(docStatus == null){
			getManagedBean().getStatusIds().clear();
		}
		getManagedBean().getPendingFlDataModel().reloadData();
	}
	
	/**
	 * Get status select items by module and doc type for dropdown.
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @return array of SelectItem
	 */
	public SelectItem[] getPendingFlListStatus() {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			List<String> statusDescs = new ArrayList<String>();
			List<Long> statusIds = new ArrayList<Long>();
			statusIds.add(FulfilmentStatusConst.PO_PENDING_PARTIAL_FL);
			statusIds.add(FulfilmentStatusConst.PO_PENDING_FULFILMENT);
			statusIds.add(FulfilmentStatusConst.PO_PENDING_INVOICE);
			statusIds.add(FulfilmentStatusConst.PO_PENDING_SUPPLIER_ACKNOWLEDGEMENT);
			
			Map<String, Long> statusMap = new HashMap<String, Long>();
			for(Long statusId : statusIds){
				if(statusMap.get(statusId) == null){
					statusMap.put(getStatusDescById(statusId), statusId);
					statusDescs.add(getStatusDescById(statusId));
				}
			}
			items = new SelectItem[statusDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (String statusDesc : statusDescs) {
				items[i++] = new SelectItem(statusMap.get(statusDesc),statusDesc);
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
	 * Navigate to delivery order page.
	 *
	 * @param index the index
	 * @return the string
	 */
	public String navigateToFlOrderFromDashBoard(int index) {
		IFulfilmentOrderSO fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentOrderBackingBean flOrderBB=new FulfilmentOrderBackingBean();
		if(getManagedBean().getSelectedFlOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
			reqDTO.setDocType(ParameterConst.DOCTYPE_PURCHASE_ORDER);
			reqDTO.setFlOrderId(Long.valueOf(getManagedBean().getSelectedFlOrder().getFulfilmentOrderId()));
			FulfilmentResDTO resDTO = fulfilmentOrderSO.retrieveFulfilmentOrderById(reqDTO);			
			
			flOrderBB.renewSesionObject();
			flOrderBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			flOrderBB.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			flOrderBB.getManagedBean().setSelectedTabIndex(0);
			flOrderBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			flOrderBB.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
			flOrderBB.getManagedBean().setRemoveFactoring(false);
			flOrderBB.getManagedBean().setDocMaintainRemarks(true);
			flOrderBB.getManagedBean().setBackToPrevious("flDocSearch");
			if(resDTO.getFlOrder().isFactored()) {
				flOrderBB.getManagedBean().setFactoringCheck("true");
				flOrderBB.getFactoringCompanyDtl();
			}
			
			flBB.initializeRemarkAndAttachement(null, resDTO.getRemarkList(), resDTO.getAttachmentList());
			flBB.getManagedBean().setHideRemarks(true);
		}else{
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_ORDER);
			reqDTO.setFlOrderId(Long.valueOf(getManagedBean().getSelectedFlOrder().getFulfilmentOrderId()));
			FulfilmentResDTO coResDTO=fulfilmentOrderSO.retrieveFulfilmentOrderById(reqDTO);
			flOrderBB=new FulfilmentOrderBackingBean();
			flOrderBB.renewSesionObject();
			flOrderBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			flOrderBB.getManagedBean().setSelectedTabIndex(0);
			flOrderBB.getManagedBean().setFlRequest(coResDTO.getFlOrder().getFlFulfilmentRequest());
			flOrderBB.getManagedBean().setFulfilmentOrder(coResDTO.getFlOrder());
			flOrderBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			flOrderBB.getManagedBean().setBackToPrevious("flDocSearch");
			
			flBB.initializeRemarkAndAttachement(null, coResDTO.getRemarkList(), coResDTO.getAttachmentList());
			flBB.getManagedBean().setHideRemarks(true);
		}
		
		return constructOrderUrl(getManagedBean().getSelectedFlOrder().getFulfilmentOrderId(), getManagedBean().getSelectedFlOrder().getDocNo(), getManagedBean().getSelectedFlOrder().getDocType());
	}
	
	/**
	 * Construct order url.
	 *
	 * @param orderId the order id
	 * @param docNo the doc no
	 * @param docType the doc type
	 * @return the string
	 */
	private String constructOrderUrl(Long orderId,String docNo,String docType) {
		  StringBuilder orderUILink = new StringBuilder();
		  
		  orderUILink.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_APPLICATION_URL)); // portal application url
		  if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
			  orderUILink.append(FulfilmentConst.VIEW_PURCHASE_ORDER); // PO
		  }else{
			  orderUILink.append(FulfilmentConst.VIEW_CONTRACT_ORDER); // CO
		  }
		  
		  orderUILink.append("?fulfilmentOrderId="+orderId); // parameter - orderId
		  
		  String url = orderUILink.toString();
		  ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		    try {
				externalContext.redirect(url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  return url;
		  
	}
	
	/**
	 * Navigate to delivery order page.
	 *
	 * @param index the index
	 * @return the string
	 */
	public String navigateToDeliveryOrder() {
		return constructDOUrl();
	}
	
	/**
	 * Construct do url.
	 *
	 * @return the string
	 */
	private String constructDOUrl() {
		StringBuilder doUrlLink = new StringBuilder();

		doUrlLink.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_APPLICATION_URL)); // portal application url
		doUrlLink.append(FulfilmentConst.VIEW_PENDING_DO_PAGE); // do page
		String url = doUrlLink.toString();
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		try {
			externalContext.redirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;

	}
	
	/**
	 * Gets the order summary grand total.
	 *
	 * @return the order summary grand total
	 */
	private void getOrderSummaryGrandTotal() {
		BigDecimal orderSummaryTotalOrderAmt = null;
		BigDecimal grandOrderSummaryTotalOrderAmt=new BigDecimal(0);
		for(FlDeliveryAddress  deliveryAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()){
			orderSummaryTotalOrderAmt = new BigDecimal(0);
			for(FlFulfilmentItemAddr itemAddress : deliveryAddress.getFlFulfilmentItemAddr()){
				orderSummaryTotalOrderAmt = orderSummaryTotalOrderAmt.add(itemAddress.getOrderedAmt());
			}
			grandOrderSummaryTotalOrderAmt = grandOrderSummaryTotalOrderAmt.add(orderSummaryTotalOrderAmt);
		}
		getManagedBean().setOrderSummaryOrderedAmtGrandTotal(grandOrderSummaryTotalOrderAmt);
		
	}
		
	/**
	 * Navigate to fl audit page.
	 *
	 * @return the string
	 */
	public String navigateToFlAudit(){
		return constructFlAuditUrl();
	}
	
	/**
	 * Construct fl audit url.
	 *
	 * @return the string
	 */
	private String constructFlAuditUrl() {
		StringBuilder urlLink = new StringBuilder();

		urlLink.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_APPLICATION_URL)); // portal application url
		urlLink.append(FulfilmentConst.VIEW_FULFILMENT_SEARCH); // do page
		String url = urlLink.toString();
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		try {
			externalContext.redirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;

	}
	
	private void getEspkbCommitmentNo() {
		String gfmasEnable = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.GFMAS_ENABLED);
		if(!gfmasEnable.equalsIgnoreCase("true")){//isEspkb
			if(getManagedBean().getFlRequest().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN)
					|| getManagedBean().getFlRequest().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF)
					&& getManagedBean().getFlRequest().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)){
				IContractSO contractSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				ContractReqDTO reqDTO = new ContractReqDTO();
				reqDTO.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
				reqDTO.setContractVerId(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getContractVerId());
				getManagedBean().setShowCommitmentNo(true);
				ContractResDTO resDTO = contractSO.retrieveAgencyCommitmentNo(reqDTO);
				if(resDTO.getCommitmentNo() == null){//cater for migrated data if did not patch commitmentNo
					getManagedBean().setEspkbCommitmentNo("K"+String.format("%1$08d", getManagedBean().getFlRequest().getCtContract().getContractId().longValue())+String.format("%1$08d", getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId().longValue()));
				}else{
					getManagedBean().setEspkbCommitmentNo(resDTO.getCommitmentNo());
				}
				
			}else{
				getManagedBean().setShowCommitmentNo(false);
			}
			
		}else{
			getManagedBean().setShowCommitmentNo(false);
		}
	}
	
	/**
	 * download file Garis_Panduan_Item_dan_Spesifikasi.pdf.
	 */
	public void downloadFile(String filePath) throws IOException {
		StringBuilder downloadPath = new StringBuilder();
		downloadPath.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)).append(filePath);
		
		super.downloadFile(downloadPath.toString());
	}
	
	public void searchPendingDeliveryList() {
		if(getManagedBean().getFromDate() != null && getManagedBean().getToDate() == null){
			throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.fl.created.date.to",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
		}
		
		if(getManagedBean().getFromDate() == null && getManagedBean().getToDate() != null){
			throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.fl.created.date.from",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
		}
		getManagedBean().getPendingDoDataModel().reloadData();
	}
	
	/**
	 * Search reset.
	 */
	public void searchReset(){
		getManagedBean().setTitle(null);
		getManagedBean().setDocNo(null);
		getManagedBean().setFromDate(null);
		getManagedBean().setToDate(null);
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setDocStatusId(null);
		getManagedBean().setDelOrdNo(null);
		getManagedBean().setDelOrdSupNo(null);
		getManagedBean().setDelOrdStatusId(null);
		getManagedBean().getPendingDoDataModel().reloadData();
	}
	
	/**
	 * Check SapOrderNo.
	 */
	private String checkSapOrderNo(String pocoNo){
		//#24638
		IFulfilmentOrderSO  fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);

		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setFlDocNo(pocoNo);
		
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		resDTO = fulfilmentOrderSO.getSapOrderNoOSB(reqDTO);
		String sapOrderNo = null;
		if(resDTO!=null && resDTO.getOrderNo() != null){
			sapOrderNo = resDTO.getOrderNo();
		}else{
			throw new EpException(MessageConst.ERROR_71024, new String[]{"SapOrderNo"});
		}
		return sapOrderNo;
		
	}
	
	//#28374	Vinod Start
	public String cancelCarryForwardAction(String action) throws IOException {
		log.debug("FulfilmentOrderBackingBean.cancelCarryForwardAction()");
		IFulfilmentOrderSO fulfilmentOrderSO = null;
		IFeeExemptionSO feeSO = null;
		try {
			//validateFulfillmentOrder(action);		//29653	
			
			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flRequest = getManagedBean().getFlRequest();

			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE) &&
					getManagedBean().getFulfilmentOrder()!=null && getManagedBean().getFulfilmentOrder().getSapOrderNo() == null){
				String sapOrderNo = checkSapOrderNo(getManagedBean().getFulfilmentOrder().getDocNo());

				if(sapOrderNo!=null){
					getManagedBean().getFulfilmentOrder().setSapOrderNo(sapOrderNo);
				}			
			}
			
			feeSO = (IFeeExemptionSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IFeeExemptionSO.class);
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
			FeeExemptionReqDTO reqFDTO = new FeeExemptionReqDTO();
			FeeExemptionResDTO resFDTO = null;
			Date systemDate = DateUtils.getCurrentDate();
			if(getManagedBean().getPmDigiSign() != null){
				reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			}
			
			if(reqDTO != null && action.equals(BpmOutcomeConst.BPM_CANCEL_YEP)){//#29219 Removed condition for carry forward --Vinod
				//retrieve pm_fee_exemption records
				resFDTO = feeSO.getFeeExemptionsYepProcessing(reqFDTO);
				Date dtEff = null;
				Date dtExp = null;
				reqDTO.setActionCode(FulfilmentConst.IDD_ACTION_CODE_DELETION);
				
				if(resFDTO.getResultBean() != null && resFDTO.getResultBean().getResultList().size() > 0){
					
					for (PmFeeExemption feeExemption : resFDTO.getResultBean().getResultList()) {
						dtEff = feeExemption.getEffDate();
						dtExp = feeExemption.getExpDate();
							
						if(feeExemption.getOrgTypeId().getParameterId().equals(new Long(1955)))
							break;
					}
					
					if(dtEff != null && dtExp != null && systemDate.after(dtEff) && systemDate.before(dtExp)){
						reqDTO.setActionCode(FulfilmentConst.IDD_ACTION_CODE_DELETE_YEP);
					}
				}
				resDTO = fulfilmentOrderSO.cancelCarryForwardAction(reqDTO);
			}
			
			if(resDTO != null && resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_745024;//#29389 logan Changed INFO_700005 to INFO_745024
				String documentType = ParameterConst.DOCTYPE_PURCHASE_ORDER;
				String docNo = getManagedBean().getFulfilmentOrder().getDocNo();
				//#29389 logan Start
				String docType = getManagedBean().getFulfilmentOrder().getDocType();
				if(docType.equalsIgnoreCase("PO"))
					messagePrefix="Purchase Order";
				else
					messagePrefix="Contract Order";
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
									messageId = MessageConst.INFO_745024;
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(messageId, 
								new String[]{messagePrefix/*+getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, documentType)*/+messageSuffix, docNo}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				//#29389 logan End
			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
			return CommonUIConst.ACKNOWLEDGE_PAGE;
		}finally {
				fulfilmentOrderSO = null;
		}
			
		}
	//#28374	Vinod End
//	28377 - start
	
	  public void UploadApprovalLetterAP(FileUploadEvent event) throws Exception {
			AttachmentFormat attachmentFormat = new AttachmentDateFormat();
			UploadedFile item = event.getFile();
			
			getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(
					ModuleCodeConst.ORDER_FULFILLMENT, getManagedBean().getFulfilmentOrder().getDocType(), getManagedBean().getFulfilmentOrder().getDocNo() ));
			String filePath = getManagedBean().getFileDirectory();
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}			

			String fileFormat= attachmentFormat.format(item.getFileName(), null);
			filePath = filePath + "/" + fileFormat;
			FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
			fileOuputStream.write(item.getContents());
			fileOuputStream.close();
			attachment.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
			attachment.setFileName(item.getFileName());
			attachment.setDocType(getManagedBean().getFulfilmentOrder().getDocType());
			attachment.setDocId(getManagedBean().getFulfilmentOrder().getFulfilmentOrderId());
			attachment.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			attachment.setSecId(FulfilmentConst.AP58_AL);
			attachment.setBrandNew(this.getManagedBean().getAttachment().isBrandNew());
			
			this.getManagedBean().setAttachment(attachment);			
			//add section id and document id
		}
	  public void downloadAPLetter() {
			try {
				commonDownload(this.getManagedBean().getAttachment().getFilePath(),this.getManagedBean().getAttachment().getFileName());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	  public void DeleteApprovalLetterAP() {
		  boolean temp = this.getManagedBean().getAttachment().isBrandNew();
		  this.getManagedBean().setAttachment(new FlAttachment());
		  this.getManagedBean().getAttachment().setBrandNew(temp);
		}
	  public void saveAP58() throws Exception{
			  if(this.getManagedBean().getAttachment().getFileName() != null){
				  try {
					  signingAction(BpmOutcomeConst.BPM_SAVE);
					} catch (Exception e) {
						e.printStackTrace();
					}
			  }else{
				  validateApprovalLetter();
			  }
	  }
	  public void submitAP58(){
			  if(this.getManagedBean().getAttachment().getFileName() != null){
				  try {
					signingAction(BpmOutcomeConst.BPM_AP58_PROCEED);
				} catch (Exception e) {
					e.printStackTrace();
				}
			  }else{
				  validateApprovalLetter();
			  }
	  }
	  
	  private void validateApprovalLetter() {
		throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.fl.ap58letter",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
	}
	
//	28377 - end
	  //29440 start
	  public String proceedNonAP58() throws IOException{
	    	//log.info("inside hideyep");
	    	Long docId = this.getManagedBean().getFlOrderId();
			//log.info("29440: docid: "+docId);
			FulfilmentReqDTO reqDTOnonAP58 = new FulfilmentReqDTO();
			FulfilmentReqDTO reqDTOflorder = new FulfilmentReqDTO();
			IFulfilmentOrderSO fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			reqDTOflorder.setFlOrderId(docId);
			resDTO = fulfilmentOrderSO.getFulfilmentOrderById(reqDTOflorder);
			reqDTOnonAP58.setFulfilmentOrder(resDTO.getFlOrder());
			resDTO = fulfilmentOrderSO.hideYEPtasklist(reqDTOnonAP58);
			//log.info("29440: "+resDTO.isSuccessful());
			if(resDTO.isSuccessful()) {
				successMessage(MessageConst.INFO_700005, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_PURCHASE_ORDER,getManagedBean().getFulfilmentOrder().getDocNo().toString(), Boolean.FALSE);
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
				
			} else {
				throw new EpException(MessageConst.ERROR_740017);
			}	
		}
	  //29440 end
	  
	// 29442 start
	public void saveCloseManual() throws Exception {
		try {
			signingAction(BpmOutcomeConst.BPM_SAVE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 29442 end
	
	// Start - #30866

	public String goToCRPage(String docType, Long flRequestId) {
		IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.ORDER_FULFILLMENT,
						IFulfilmentRequestSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		if (flRequestId != null && docType != null) {
			reqDTO.setFlRequestId(flRequestId);
		}
		reqDTO.setFlRequestLink(true);
		FulfilmentResDTO resDTO = requestSO.retrieveFulfilmentRequest(reqDTO);
		FacesContext fc = FacesContext.getCurrentInstance();
		ContractRequestBackingBean crBB = new ContractRequestBackingBean();
		crBB.renewSesionObject();
		crBB = new ContractRequestBackingBean();
		crBB.initializeCRBeans();
		crBB.getManagedBean().setSelectedTabIndex(0);
		crBB.getManagedBean().setDocProcess(docType);
		crBB.getManagedBean().setBackToDocument(fc.getViewRoot().getViewId());
		crBB.getManagedBean().setHyperLinkPage(false);
		crBB.getManagedBean().setPreviousRemarkList(
				flBB.getManagedBean().getRemarkList());
		crBB.getManagedBean().setPreviousAttachmentList(
				flBB.getManagedBean().getAttachmentList());
		crBB.setFulfilmentRequestField(resDTO);
		crBB.initializeApproverGroup();

		crBB.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());

		crBB.getManagedBean().setPmUser(reqDTO.getPmUser());
		crBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
		crBB.getManagedBean().setDeclarationCheck(true);
		crBB.getManagedBean().setPrevHideRemarks(
				flBB.getManagedBean().isHideRemarks());
		flBB.getManagedBean().setHideRemarks(true);
		IContractSupplierSO contractSupplierSO = (IContractSupplierSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT,
						IContractSupplierSO.class);
		ContractSupplierReqDTO contractSupplierReqDTO = new ContractSupplierReqDTO();
		if (null != resDTO.getFlFulfilmentRequest()) {
			if (resDTO.getFlFulfilmentRequest().getCtContract().isFactored()) {
				Long supplieContractId = resDTO.getFlFulfilmentRequest()
						.getCtContract().getContractId();
				contractSupplierReqDTO.setContractId(supplieContractId);
				ContractSupplierResDTO contractSupplierResDTO = contractSupplierSO
						.retrieveFactoringByContract(contractSupplierReqDTO);
				if (contractSupplierResDTO.getFactoring() != null) {
					resDTO.getFlFulfilmentRequest()
							.getCtContract()
							.setCtFactoring(
									contractSupplierResDTO.getFactoring());
					crBB.getManagedBean().setFactored(true);
					if (contractSupplierResDTO.getFactoring().getFinancialOrg() != null) {
						crBB.getSelectedFactoringCompDtl(contractSupplierResDTO
								.getFactoring());
					}
				} else {
					crBB.getManagedBean().setFactored(false);
				}
			}
		}

		for (CtAgency agency : crBB.getManagedBean().getFlRequest()
				.getCtContract().getLatestContractVer().getCtAgencies()) {
			if (agency.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE
					|| (agency.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_SUSPENDED && agency
							.getEffDate().after(new Date()))) {
				crBB.getManagedBean().getAgentOrgProfileList()
						.add(agency.getOrgProfile().getOrgProfileId());
				if (agency == crBB.getManagedBean().getFlRequest()
						.getCtContract().getLatestContractVer().getCtAgencies()
						.get(0)) {
					crBB.getManagedBean()
							.setOrgProfileIds(
									agency.getOrgProfile().getOrgProfileId()
											.toString());
				} else {
					crBB.getManagedBean().setOrgProfileIds(
							crBB.getManagedBean().getOrgProfileIds()
									+ ","
									+ agency.getOrgProfile().getOrgProfileId()
											.toString());
				}
			}
		}
		crBB.setFulfilmentItemAddr(crBB.getManagedBean().getFlRequest());
		crBB.getManagedBean().setMinistryProfileId(
				retrieveMinistryOrgProfile(
						crBB.getManagedBean().getFlRequest()
								.getIssuedOrgProfile().getOrgProfileId())
						.getOrgProfileId());
		return NAV_CR;
	}

	public String goToCOPage(String docType, Long flOrderId) {
		IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.ORDER_FULFILLMENT,
						IFulfilmentOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		if (flOrderId != null && docType != null) {
			reqDTO.setFlOrderId(flOrderId);
			reqDTO.setDocType(docType);
		}
		FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(reqDTO);
		FulfilmentOrderBackingBean orderBackingBean = new FulfilmentOrderBackingBean();
		orderBackingBean.getManagedBean().setFulfilmentOrder(
				resDTO.getFlOrder());
		orderBackingBean.getManagedBean().setFlRequest(
				resDTO.getFlFulfilmentRequest());

		FacesContext fc = FacesContext.getCurrentInstance();
		orderBackingBean.getManagedBean().setBackToPrevious(
				fc.getViewRoot().getViewId());
		orderBackingBean.getManagedBean().setHyperLinkPage(false);
		orderBackingBean.getManagedBean()
				.setButtonVal(BpmOutcomeConst.BPM_VIEW);

		boolean isFactoredSelected = resDTO.getFlOrder().isFactored();
		if (isFactoredSelected) {
			orderBackingBean.getManagedBean().setFactoringCheck("true");
		} else {
			orderBackingBean.getManagedBean().setFactoringCheck("false");
		}

		return NAV_CO;
	}
	// End - #30866
	// Start - #25790
	/**
	 * 
	 * Navigates to the purchase request page by retrieving the fulfillment
	 * request details and initializing the necessary backing beans. This method
	 * sets various attributes in the PurchaseRequestBackingBean based on the
	 * retrieved fulfillment request information.
	 * 
	 * @return the string
	 */
	public String navigatePurchaseRequest() {
		FulfilmentBackingBean fulfilmentBB = new FulfilmentBackingBean();
		IFulfilmentRequestSO requestSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setFlRequestId(getManagedBean().getFlRequest().getFulfilmentReqId());
		reqDTO.setFlRequestLink(true);
		FulfilmentResDTO resDTO = requestSO.retrieveFulfilmentRequest(reqDTO);
		FacesContext fc = FacesContext.getCurrentInstance();
		PurchaseRequestBackingBean prBB = new PurchaseRequestBackingBean();
		prBB.initializePRBeans();
		prBB.getManagedBean().setSubmitButtonStatus(false);
		prBB.getManagedBean().setDocProcess(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
		prBB.getManagedBean().setBackToDocument(fc.getViewRoot().getViewId());
		prBB.getManagedBean().setHyperLinkPage(false);
		prBB.getManagedBean().setPreviousRemarkList(fulfilmentBB.getManagedBean().getRemarkList());
		prBB.getManagedBean().setPreviousAttachmentList(fulfilmentBB.getManagedBean().getAttachmentList());
		prBB.setFulfilmentRequestField(resDTO);
		prBB.getManagedBean().setPmUser(reqDTO.getPmUser());
		prBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
		prBB.getManagedBean().setDeclarationCheck(true);
		prBB.getManagedBean().setPrevHideRemarks(fulfilmentBB.getManagedBean().isHideRemarks());
		fulfilmentBB.getManagedBean().setHideRemarks(true);
		prBB.getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());

		return NAV_PR;
	}
	// End - #25790
}
