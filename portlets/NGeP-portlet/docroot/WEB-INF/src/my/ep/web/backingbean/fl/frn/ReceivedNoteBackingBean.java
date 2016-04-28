/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.frn;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.liferay.portal.util.PortalUtil;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.supplier.IContractSupplierSO;
import my.ep.biz.service.fl.IFulfilmentSO;
import my.ep.biz.service.fl.frn.IReceivedNoteSO;
import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.criteria.ICriteriaSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.designatedarea.IDesignatedAreaSO;
import my.ep.biz.service.pm.general.settings.gstitem.IGstItemSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.supplier.ContractSupplierReqDTO;
import my.ep.dto.ct.supplier.ContractSupplierResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.criteria.CriteriaReqDTO;
import my.ep.dto.pm.common.criteria.CriteriaResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.general.settings.designatedarea.DesignatedAreaReqDTO;
import my.ep.dto.pm.general.settings.designatedarea.DesignatedAreaResDTO;
import my.ep.dto.pm.general.settings.gstitem.GstItemReqDTO;
import my.ep.dto.pm.general.settings.gstitem.GstItemResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.frndata.FLFulFillmentRecievedNoteDataType;
import my.ep.jaxb.fl.maintenace.cancel.dofulfillment.FLDOFulfillmentCancelReqType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.fl.FlActor;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNote;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNoteItem;
import my.ep.persistence.entity.fl.frn.FlSupplierEvaluation;
import my.ep.persistence.entity.fl.order.FlDelOrderItem;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCriteria;
import my.ep.persistence.entity.pm.common.PmCriteriaDesc;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.type.pm.GSLType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.common.CommonBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBaseBackingBean;
import my.ep.web.backingbean.fl.cr.ContractRequestBackingBean;
import my.ep.web.backingbean.fl.deliveryorder.DeliveryOrderBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.backingbean.fl.pr.PurchaseRequestBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.fl.frn.ReceivedNoteManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

/**
 * Backing Bean for Create Received Note .
 *
 * @author Giribabu
 * @since 7/10/2013
 */
@ManagedBean(name = "receivedNoteBackingBean")
@RequestScoped
public class ReceivedNoteBackingBean extends FulfilmentBaseBackingBean<ReceivedNoteManagedBean> {
	private static Logger log = Logger.getLogger(ReceivedNoteBackingBean.class);
	private String className="@(.myFrnEvalClassName)";
	private static final String NAV_DELIVERYORDER_PAGE = "/xhtml/fl/deliveryorder/doBasePage.xhtml";
	private static final String NAV_PO = "/xhtml/fl/purchaseorder/poBasePage.xhtml";
	private static final String NAV_CO = "/xhtml/fl/contractorder/coBasePage.xhtml";
	public static final String NAV_CR = "/xhtml/fl/contractrequest/crBasePage.xhtml";
	public static final String NAV_PR = "/xhtml/fl/purchaserequest/prBasePage.xhtml";
	
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
	 * save,submit received note.
	 * <p>submit - save data into database and BPM process will route to the next respective user.
	 * <p>save - save data into database.
	 * @param action the action
	 * navigate to acknowledge page.
	 */
	public String submitAction(String action) {
		log.debug("ReceivedNoteBackingBean.submitAction()");
		IReceivedNoteSO receivedNoteSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		try {
			if(!getManagedBean().isValidated()){
				validateFulfilmentNote(action);
			}
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL) 
					|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)){
				// UnitEP request can reject service
//				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
//					for(FlFulfilmentNoteItem frnItem : getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem()) {
//						if(frnItem.getFlDelOrderItem().getFlFulfilmentItemAddr().getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
//							throw new EpException(MessageConst.ERROR_745069);
//						}
//					}
//				}				
				if(flBB.getManagedBean().getRemarkList().get(0).getRemark()==null || flBB.getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
					throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.txt.remarks",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
				}
				
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)){
					IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
					UserReqDTO userReqDTO = new UserReqDTO();
					userReqDTO.setUserId(getManagedBean().getFulfilmentNote().getCreatedBy());
					UserResDTO userResDTO = userSO.retrieveUserByUserId(userReqDTO);
					if(userResDTO.getPmUser().getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE){
						throw new EpException(MessageConst.ERROR_740020, new String[]{ResourceBundleUtils.getMessage("lbl.txt.receivingOfficer",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
					}
				}
			}
			
			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
			FlFulfilmentNote fulfilmentNote = getManagedBean().getFulfilmentNote();
			receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IReceivedNoteSO.class);
			
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setFulfilmentNote(fulfilmentNote);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			if(getManagedBean().getSelectedAckOfficer()==null) {
				setSelectedAckOfficer();
			} 
			reqDTO.setSelectedAckOfficer(getManagedBean().getSelectedAckOfficer());
			if(flBB.getManagedBean().getRemarkList().size()>1) {
				reqDTO.setFlRemarkList(flBB.getManagedBean().getRemarkList());
			}
			if(getManagedBean().getBpmTaskName()!=null && 
				(getManagedBean().getBpmTaskName().equalsIgnoreCase(FLProcessNameConst.CREATE_FRN_HT) || 
				getManagedBean().getBpmTaskName().equalsIgnoreCase(FLProcessNameConst.REVISE_FRN_HT))){
				reqDTO.setUserRole(RoleCode.RECEIVING_OFFICER.value());	
			}else if(getManagedBean().getBpmTaskName()!=null && 
				getManagedBean().getBpmTaskName().equalsIgnoreCase(FLProcessNameConst.APPROVE_FRN_HT)){
				reqDTO.setUserRole(RoleCode.ACKNOWLEDGE_OFFICER.value());	
			}else{
				reqDTO.setUserRole(getUserRole());
			}
			reqDTO.setToken(token);
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setFrnCancellationProcess(getManagedBean().isFrnCancellationProcess());
			reqDTO.setLastFRNTaxInv(getManagedBean().isLastFRNTaxInv());
			reqDTO.setApproverUserList(getManagedBean().getApproverList());
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL) || 
				(getManagedBean().getProcessName()!=null && getManagedBean().getProcessName().equalsIgnoreCase(FLProcessNameConst.FRN_CANCELLATION_PROCESS_NAME))){	
				reqDTO.setPmUser(getManagedBean().getPmUser());
				resDTO = receivedNoteSO.receiveNoteModification(reqDTO);
			} else {
				if(getManagedBean().getPmDigiSign() !=null){
					reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
				}
				resDTO = receivedNoteSO.saveReceivedNote(reqDTO);
			}
			log.info("######## FRN SLA end : "+System.currentTimeMillis()+" : "+new Date());
			if (resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700011;
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
					if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
						messageId = MessageConst.INFO_700004;
					} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)) {
						messageId = MessageConst.INFO_745002;
					} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
						messageId = MessageConst.INFO_745003;
					} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
						messageId = MessageConst.INFO_700009;
					}else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
						messageId = MessageConst.INFO_700008;
					}else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
						messageId = MessageConst.INFO_700018;
						if(getManagedBean().getProcessName()!=null && getManagedBean().getProcessName().equalsIgnoreCase(FLProcessNameConst.FRN_CANCELLATION_PROCESS_NAME)) {
							messageId = MessageConst.INFO_700005;
						}
					}
					if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(messageId, 
										new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_DELIVERY_ORDER), 
										" "+ resDTO.getFlDeliveryOrder().getSupplierDoRef()}, 
										MessageUtils.BUNDLE_COMMON_MESSAGES, 
										new FacesScopes().getUserLocale())));
					}else{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(messageId, 
										new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_FULFILMENT_NOTE), 
										ResourceBundleUtils.getMessage("lbl.fl.frn.product.service",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+" "+ resDTO.getFulfilmentNoteNo()}, 
										MessageUtils.BUNDLE_COMMON_MESSAGES, 
										new FacesScopes().getUserLocale())));
					}
				} else {
					if(getManagedBean().isLastFRNTaxInv() && checkTaxInvoice(getManagedBean().getFulfilmentNote().getFlRequest().getFulfilmentReqId())){
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(MessageConst.INFO_745010, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, 
										ParameterConst.DOCTYPE_FULFILMENT_NOTE),
										ResourceBundleUtils.getMessage("lbl.fl.frn.product.service",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+" "+resDTO.getFulfilmentNoteNo()},
												MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					
					}else{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(messageId, 
										new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_FULFILMENT_NOTE), 
										ResourceBundleUtils.getMessage("lbl.fl.frn.product.service",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+" "+resDTO.getFulfilmentNoteNo()}, 
										MessageUtils.BUNDLE_COMMON_MESSAGES, 
										new FacesScopes().getUserLocale())));
					}
				}

				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					renewSesionObject();
					if ((action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE))) {
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put(ReportConst.PARAM_DOC_NO, resDTO.getFulfilmentNoteNo());
						paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
						CommonBackingBean commonBackingBean = new CommonBackingBean();
						commonBackingBean.getManagedBean().setReportName(ReportConst.FL_NGEP_FRN);
						commonBackingBean.getManagedBean().setReportDisplayName(ResourceBundleUtils.getMessage("lbl.fl.frn",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
						commonBackingBean.getManagedBean().setParamMap(paramMap);
						return CommonUIConst.ACKNOWLEDGE_REPORT_PAGE;
					}
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					reqDTO.setFulfilmentNoteId(resDTO.getFulfilmentNoteId());
					resDTO = receivedNoteSO.retrieveReceivedNoteByReceivedNoteId(reqDTO);
					setFulfilmentNoteField(resDTO);
				}
			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
		} finally {
			receivedNoteSO = null;
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
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();		
		if(taskId != null){
			renewSesionObject();
			IFulfilmentSO iFulfilmentSO = (IFulfilmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentSO.class);
			IFulfilmentRequestSO iFulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
			IReceivedNoteSO receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReceivedNoteSO.class);
			IDeliveryOrderSO deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IDeliveryOrderSO.class);
			IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
			
			Object obj = super.getBpmTaskDetails(Object.class, new String[]{BpmOutcomeConst.BPM_SAVE});
			getManagedBean().setBpmTaskName(this.getManagedBean().getTaskName());
			if(this.getManagedBean().getProcessName().equalsIgnoreCase(FLProcessNameConst.FRN_CANCELLATION_PROCESS_NAME)){
				FLDOFulfillmentCancelReqType fulfillmentCancelReqType = (FLDOFulfillmentCancelReqType)obj;
				reqDTO.setDeliveryOrderId(fulfillmentCancelReqType.getDoId());

				resDTO = receivedNoteSO.retrieveReceivedNoteByDeliveryOrderId(reqDTO);
				
				getManagedBean().setFulfilmentNote(resDTO.getFulfilmentNote());
				getManagedBean().setFulfilmentRequest(resDTO.getFulfilmentNote().getFlRequest());
				getManagedBean().setDocType(resDTO.getFulfilmentNote().getFlRequest().getDocType());
				
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				flBB.initializeRemarkAndAttachement(resDTO.getFulfilmentNote().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
				
				getManagedBean().setFrnCancellationProcess(true);
				getManagedBean().setDocMaintainRemarks(true);
				if(fulfillmentCancelReqType.getErrorId() != null && fulfillmentCancelReqType.getErrorId() > 0){
					getManagedBean().setAutoUpdate(Boolean.FALSE);
					getManagedBean().setGfmasError(true);
					FulfilmentReqDTO errReq = new FulfilmentReqDTO();
					errReq.setErrorId(fulfillmentCancelReqType.getErrorId());
					FulfilmentResDTO errRes = iFulfilmentRequestSO.retrieveErrorContent(errReq);
					getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
				}
				if(getManagedBean().getFlApprover() == null){
					FlActor actor = iFulfilmentSO.retrieveFlActors(getManagedBean().getFulfilmentNote().getFlRequest().getDocType(), getManagedBean().getFulfilmentNote().getFlRequest().getFulfilmentReqId(), RoleCode.FL_APPROVER.value());
					if(actor != null){
						getManagedBean().setFlApprover(actor.getUser());
					}
				}
				if(getManagedBean().getFulfilmentNote().getApproverId() != null){
					getManagedBean().setUserLoginId(getManagedBean().getFulfilmentNote().getApproverId());
				}
			}else{
				FLFulFillmentRecievedNoteDataType fulFillmentRecievedNoteDataType = (FLFulFillmentRecievedNoteDataType)obj;
				reqDTO.setDeliveryOrderId(fulFillmentRecievedNoteDataType.getDoId());
				
				resDTO = receivedNoteSO.retrieveReceivedNoteByDeliveryOrderId(reqDTO);
				if(resDTO.getFulfilmentNote()!=null) {
					getManagedBean().setFulfilmentNote(resDTO.getFulfilmentNote());
					getManagedBean().setFulfilmentRequest(resDTO.getFulfilmentNote().getFlRequest());
					getManagedBean().setDocType(resDTO.getFulfilmentNote().getFlRequest().getDocType());
					if(getManagedBean().getFulfilmentNote().getApproverId() != null){
						getManagedBean().setUserLoginId(getManagedBean().getFulfilmentNote().getApproverId());
					}
					if(getManagedBean().getFlApprover() == null){
						FlActor actor = iFulfilmentSO.retrieveFlActors(getManagedBean().getFulfilmentNote().getFlRequest().getDocType(), getManagedBean().getFulfilmentNote().getFlRequest().getFulfilmentReqId(), RoleCode.FL_APPROVER.value());
						if(actor != null){
							getManagedBean().setFlApprover(actor.getUser());
						}
					}
				} else {
					reqDTO.setDocType(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
					resDTO = deliveryOrderSO.retrieveDeliveryOrderById(reqDTO);
					getManagedBean().setFulfilmentRequest(resDTO.getFlDeliveryOrder().getFlRequest());
					getManagedBean().getFulfilmentNote().setFlDeliveryOrder(resDTO.getFlDeliveryOrder());
					getManagedBean().getFulfilmentNote().setFlRequest(resDTO.getFlDeliveryOrder().getFlRequest());
					
					if(getManagedBean().getFlApprover() == null){
						FlActor actor = iFulfilmentSO.retrieveFlActors(resDTO.getFlDeliveryOrder().getFlRequest().getDocType(), resDTO.getFlDeliveryOrder().getFlRequest().getFulfilmentReqId(), RoleCode.FL_APPROVER.value());
						if(actor != null){
							getManagedBean().setFlApprover(actor.getUser());
						}
					}
					
					getManagedBean().setDocType(resDTO.getFlDeliveryOrder().getFlRequest().getDocType());
					// Default status
					StatusReqDTO statusReqDTO = new StatusReqDTO();
					statusReqDTO.setStatusId(FulfilmentStatusConst.FN_PENDING_DO_ACKNOWLEDGMENT);
					getManagedBean().getFulfilmentNote().setStatus(statusSO.retrieveStatusByStatusId(statusReqDTO).getPmStatus());
					
					List<FlFulfilmentNoteItem> noteItemList=new ArrayList<FlFulfilmentNoteItem>();
					for (FlDelOrderItem delOrderItem : resDTO.getFlDeliveryOrder().getFlDelOrderItem()){
						if(delOrderItem.getDeliveredQty().doubleValue()>0) { 
							FlFulfilmentNoteItem noteItem=new FlFulfilmentNoteItem();
							noteItem.setFlDelOrderItem(delOrderItem);
							noteItem.setFlFulfilmentNote(getManagedBean().getFulfilmentNote());
							noteItem.setFlItem(delOrderItem.getFlItem());
							noteItem.setGstAmount(delOrderItem.getGstAmount());
							noteItem.setGstUnitPrice(delOrderItem.getGstUnitPrice());
							noteItem.setReceivedQty(delOrderItem.getDeliveredQty());
							noteItem.setReceivedAmt(delOrderItem.getDeliveredAmt());
							noteItemList.add(noteItem);
						}
					}
					getManagedBean().getFulfilmentNote().setFlFulfilmentNoteItem(noteItemList);
				}
				getManagedBean().setSelectedItem(getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem().get(0));
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				flBB.getManagedBean().setHideRemarks(false);
				if(getManagedBean().getFulfilmentRequest().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_REQUEST)) {
					new FulfilmentBackingBean(ParameterConst.DOCTYPE_FULFILMENT_NOTE, getManagedBean().getFulfilmentRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
				} else {
					new FulfilmentBackingBean(ParameterConst.DOCTYPE_FULFILMENT_NOTE, getManagedBean().getFulfilmentRequest().getCtContract().getContractNo());
				}				
				if(resDTO.getFulfilmentNote()!=null) {
					flBB.initializeRemarkAndAttachement(resDTO.getFulfilmentNote().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
				} else {
					flBB.initializeRemarkAndAttachement(resDTO.getFlDeliveryOrder().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
				}
				
				if(fulFillmentRecievedNoteDataType.getErrorId() != null && fulFillmentRecievedNoteDataType.getErrorId() > 0){
					getManagedBean().setAutoUpdate(Boolean.FALSE);
					getManagedBean().setGfmasError(true);
					FulfilmentReqDTO errReq = new FulfilmentReqDTO();
					errReq.setErrorId(fulFillmentRecievedNoteDataType.getErrorId());
					FulfilmentResDTO errRes = iFulfilmentRequestSO.retrieveErrorContent(errReq);
					getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
				}
				if(null == getManagedBean().getFulfilmentNote().getReceivedDate()){
					getManagedBean().getFulfilmentNote().setReceivedDate(DateUtils.getCurrentDateWithoutTime());
				}
				getSupplierGst(getManagedBean().getFulfilmentNote());
			}
			getManagedBean().setSelectedTabIndex(0);
			System.out.println("getTaskName: "+this.getManagedBean().getTaskName());
			if(this.getManagedBean().getTaskName().equalsIgnoreCase(FLProcessNameConst.APPROVE_FRN_HT)){
				checkLastTaxFRN();//Issuance of CoGSTR
			}
			getAcknowledgeOfficerDropdown();
			
			// Set evaluation
			if(this.getManagedBean().getTaskName().equalsIgnoreCase(FLProcessNameConst.APPROVE_FRN_HT)){
				getManagedBean().setDisableEvaluation(Boolean.TRUE);
			}
			if(getManagedBean().getFulfilmentNote().getFlSupplierEvaluation().size()==0) {
				retrieveCriteriaList();
			}
			if(null == getManagedBean().getFulfilmentNote().getReceivedDate()){
				getManagedBean().getFulfilmentNote().setReceivedDate(DateUtils.getCurrentDateWithoutTime());
			}
			if(null == getManagedBean().getFulfilmentNote().getCreatedDate()){
				getManagedBean().getFulfilmentNote().setCreatedDate(new Timestamp(System.currentTimeMillis()));
			}		
			if(null == getManagedBean().getFulfilmentNote().getSubmissionDate() || 
				(getManagedBean().getFulfilmentNote().getStatus()!=null && getManagedBean().getFulfilmentNote().getStatus().getStatusId().longValue()==FulfilmentStatusConst.FN_PENDING_DO_ACKNOWLEDGMENT.longValue())){
				getManagedBean().getFulfilmentNote().setSubmissionDate(DateUtils.getCurrentDateWithoutTime());
			}			
		}else{
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			if(null == getManagedBean().getButtonVal() || !getManagedBean().getButtonVal().equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(false);
			}
		}
	}
	
	/**
	 * calculate  received amount based on received quantity and unit price.
	 *
	 * @param item the item
	 */
	public void retrieveReceivedAmt() {
		if(null != getManagedBean().getSelectedItem()){
			FlFulfilmentNoteItem item=	getManagedBean().getSelectedItem();
		
			if(item.getReceivedQty()==null) {
				item.setReceivedQty(BigDecimal.ZERO);
			}
			if(item.getReceivedQty().doubleValue() > item.getFlDelOrderItem().getDeliveredQty().doubleValue()) {
				item.setReceivedQty(BigDecimal.ZERO);
				item.setReceivedAmt(BigDecimal.ZERO);
				throw new EpException(MessageConst.ERROR_100032, new String[]{
						ResourceBundleUtils.getMessage("lbl.fl.received.qty",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()),
						ResourceBundleUtils.getMessage("lbl.fl.do.delivery.qty",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});	
			}
			if(null != item.getReceivedQty()){
				BigDecimal formattedReceivedQty = item.getReceivedQty().setScale(
						item.getFlDelOrderItem().getFlFulfilmentItemAddr().getScRequestItem().getUom().getDecimalScale(),BigDecimal.ROUND_HALF_UP);
				item.setReceivedQty(formattedReceivedQty);
			}
			BigDecimal receivedAmt = item.getReceivedQty().multiply(item.getGstUnitPrice());
	//		if(item.getReceivedQty().doubleValue() == item.getFlDelOrderItem().getDeliveredQty().doubleValue()) {
	//			receivedAmt = item.getFlDelOrderItem().getDeliveredAmt();
	//		}
			item.setReceivedAmt(receivedAmt.setScale(2, RoundingMode.HALF_UP));
			if(getManagedBean().isAcknowledged()){
				getManagedBean().setRemainingQty(item.getFlDelOrderItem().getFlFulfilmentItemAddr().getOrderedQty().subtract(item.getReceivedQty()));
			}else{
				BigDecimal remainQty = new BigDecimal(getRemainingQuantity(item));
				getManagedBean().setRemainingQty(remainQty);
			}
		}
	}
	 
	/**
	 * Retrieve criteria list based on language code.
	 */
	private void retrieveCriteriaList() {
		ICriteriaSO criteriaSO = (ICriteriaSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICriteriaSO.class);
		CriteriaReqDTO reqDTO = new CriteriaReqDTO();
		reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
		CriteriaResDTO resDTO = criteriaSO.retrieveCriteriaDescList(reqDTO);

		for (PmCriteriaDesc pmCriteriaDesc : resDTO.getCriteriaDescList()) {
			FlSupplierEvaluation evaluationCriteria = new FlSupplierEvaluation();
			evaluationCriteria.setPmCriteria(pmCriteriaDesc.getPmCriteria());
			evaluationCriteria.setFlFulfilmentNote(getManagedBean().getFulfilmentNote());
			getManagedBean().getFulfilmentNote().getFlSupplierEvaluation().add(evaluationCriteria);
		}
	}
	
	/**
	 * Gets the criteria description based on language code and criteria id.
	 *
	 * @param pmCriteria the pm criteria
	 * @return the criteria desc
	 */
	public String getCriteriaDesc(PmCriteria pmCriteria) {
		ICriteriaSO criteriaSO = (ICriteriaSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICriteriaSO.class);
		CriteriaReqDTO reqDTO = new CriteriaReqDTO();
		reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
		reqDTO.setCriteriaId(pmCriteria.getCriteriaId());
		CriteriaResDTO resDTO = criteriaSO.retrieveCriteriaDescById(reqDTO);
		return resDTO.getPmCriteriaDesc().getCriteria();
	}
	
	/**
	 * Validate fulfilment note before submit and gpki Sign Action.
	 *
	 * @param outcome the outcome
	 */
	public void validateFulfilmentNote(String outcome) {
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		if(getManagedBean().getFulfilmentNote().getReceivedDate()==null) {
			throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.fl.frn.act.received.date",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
		}
		for(FlSupplierEvaluation supplierEvaluation : getManagedBean().getFulfilmentNote().getFlSupplierEvaluation()) {
			if(supplierEvaluation.getScore()==null ||  supplierEvaluation.getScore()==0) {
				throw new EpException(MessageConst.ERROR_100051, new String[]{ResourceBundleUtils.getMessage("lbl.fl.frn.score",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()),getCriteriaDesc(supplierEvaluation.getPmCriteria())});
			}
		}
		BigDecimal sumReceiveQty = new BigDecimal(0);
		for(FlFulfilmentNoteItem frnItem : getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem()) {
			sumReceiveQty = sumReceiveQty.add(frnItem.getReceivedQty());
		}
		if(sumReceiveQty.doubleValue() == 0){
			throw new EpException(MessageConst.ERROR_100033, new String[]{ResourceBundleUtils.getMessage("lbl.fl.received.qty",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
		}
		
		for(FlFulfilmentNoteItem frnItem : getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem()) {
			frnItem.setRejectedQty(frnItem.getFlDelOrderItem().getDeliveredQty().subtract(frnItem.getReceivedQty()));
			if(frnItem.getReceivedQty().doubleValue() < frnItem.getFlDelOrderItem().getDeliveredQty().doubleValue()) {
				if(frnItem.getRejectionRemark()==null || frnItem.getRejectionRemark().length()==0) {
					throw new EpException(MessageConst.ERROR_740024, new String[]{ResourceBundleUtils.getMessage("lbl.txt.remarks",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()),frnItem.getFlDelOrderItem().getFlFulfilmentItemAddr().getScRequestItem().getItemName()});
				}
			}
			frnItem.setRejectedAmt(frnItem.getFlDelOrderItem().getDeliveredAmt().subtract(frnItem.getReceivedAmt()));
			if(frnItem.getRejectedAmt().doubleValue()<=0) {
				frnItem.setRejectedAmt(new BigDecimal(0));
			}
			if(ParameterConst.ITEM_TYPE_PRODUCT.equalsIgnoreCase(frnItem.getFlDelOrderItem().getFlFulfilmentItemAddr().getItemType().getParameterCode())){
				if(frnItem.getReceivedAmt().doubleValue() < frnItem.getFlDelOrderItem().getDeliveredAmt().doubleValue()) {
					if(frnItem.getRejectionRemark()==null || frnItem.getRejectionRemark().length()==0) {
						throw new EpException(MessageConst.ERROR_745051, new String[]{frnItem.getFlDelOrderItem().getFlFulfilmentItemAddr().getScRequestItem().getItemName()});
					}
				}		
			}
		}
		
		Date today = new Date();
		if(getManagedBean().getFulfilmentNote().getReceivedDate().after(today)){
			throw new EpException("ERR-740012");
		}
		
		FlFulfilmentNote fulfilmentNote = getManagedBean().getFulfilmentNote();	
		if(fulfilmentNote.getFlRequest().getCtContract() != null) {
			if(fulfilmentNote.getFlRequest().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
				IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				ContractReqDTO contractReqDTO = new ContractReqDTO();
				contractReqDTO.setContractId(fulfilmentNote.getFlRequest().getCtContract().getContractId());
				if(!contractSO.checkContractStatus(contractReqDTO).isActive()) {
					throw new EpException(MessageConst.ERROR_700020);
				}
			} else if((fulfilmentNote.getReceivedDate().after(fulfilmentNote.getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getExpDate()) 
					|| fulfilmentNote.getReceivedDate().before(fulfilmentNote.getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getEffDate()))){
				throw new EpException(MessageConst.ERROR_700020);
			}
		}
		
		if(outcome.equalsIgnoreCase(my.ep.constant.BpmOutcomeConst.BPM_SUBMIT)) {
			List<PmUser> orderApproverList = new ArrayList<PmUser>();
			List<PmUser> pendingApproverList = new ArrayList<PmUser>();
			if(getManagedBean().getPmUserList().isEmpty()){
				throw new EpException(MessageConst.ERROR_740020, new String[]{ResourceBundleUtils.getMessage("lbl.fl.acknowledgeOfficer",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			}
			if(getManagedBean().getUserLoginId() == null) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{
						ResourceBundleUtils.getMessage("lbl.fl.acknowledgeOfficer",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			}
			for(PmUser user : getManagedBean().getPmUserList()){
				if(getManagedBean().getUserLoginId().equals(user.getUserId())){
					orderApproverList.add(user);
				} else if (orderApproverList.size() > 0) {
					orderApproverList.add(user);
				} else if (orderApproverList.size() == 0) {
					pendingApproverList.add(user);
				}
			}
			orderApproverList.addAll(pendingApproverList);
			getManagedBean().getApproverList().addAll(orderApproverList);
		}
		if(outcome.equalsIgnoreCase(my.ep.constant.BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
			IDeliveryOrderSO orderSO = (IDeliveryOrderSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IDeliveryOrderSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setDeliveryOrderId(getManagedBean().getFulfilmentNote().getFlDeliveryOrder().getDeliveryOrderId());
			PmStatus status = orderSO.retrieveDelOrderStatus(reqDTO).getPmStatus();
			if(status==null || status.getStatusId().longValue()!=FulfilmentStatusConst.DO_PENDING_ACKNOWLEDGEMENT) {
				throw new EpException(MessageConst.ERROR_745068, new String[]{getManagedBean().getFulfilmentNote().getFlDeliveryOrder().getDeliveryOrderNo()});
			}
			checkLastTaxFRN();
			IPersonnelSO personnelSO = (IPersonnelSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);
			PersonnelReqDTO personnelReqDTO = new PersonnelReqDTO();
			personnelReqDTO.setSmSupplierId(fulfilmentNote.getFlRequest().getSmSupplier().getSupplierId());
			PersonnelResDTO personnelResDTO = personnelSO.retrievePersonnelBySupplierId(personnelReqDTO);
			
			if(personnelResDTO.getSmPersonnelList().isEmpty()){
				throw new EpException(MessageConst.ERROR_740020, new String[]{ResourceBundleUtils.getMessage("lbl.txt.supplierUser",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			}
			if(!getManagedBean().isIssuanceCogstrCheck() && getManagedBean().isLastFRNTaxInv()){
				getManagedBean().setSelectedTabIndex(0);
				onTabChange(getManagedBean().getSelectedTabIndex());
				throw new EpException(MessageConst.ERROR_745078);
			}
		}
	}
	
	/**
	 * Gets the current date.
	 *
	 * @return the current date
	 */
	public Date getCurrentDate() {
		return new Date();
	}
	
	/**
	 * Check supplier Evaluation score.
	 * supplier Evaluation score between the 1 and 5
	 * @param supplierEvaluation the supplier evaluation
	 */
	public void checkScore(FlSupplierEvaluation supplierEvaluation){
		if(supplierEvaluation.getScore()!=null) {
			if(supplierEvaluation.getScore() > 5 || supplierEvaluation.getScore() == 0){
				String score = supplierEvaluation.getScore().toString();
				supplierEvaluation.setScore(null);
				throw new EpException(MessageConst.ERROR_100039, new String[]{score,"1","5"});
			}
		}
	}
	
	/**
	 * Received amount validation. Remove this checking as GST implemented.
	 * <p>Received Amount must be less than or equal to Delivery Amount.
	 * @param item the item
	 */
	public void receivedAmtValidation(FlFulfilmentNoteItem item) {
//		if(item.getReceivedAmt().doubleValue() > item.getFlDelOrderItem().getDeliveredAmt().doubleValue()) {
//			item.setReceivedAmt(new BigDecimal(0));
//			throw new EpException(MessageConst.ERROR_100032, new String[]{ResourceBundleUtils.getMessage("lbl.fl.do.received.amount",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()),ResourceBundleUtils.getMessage("lbl.fl.delivery.amount",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
//		}
//		if(item.getFlDelOrderItem().getFlFulfilmentItemAddr().getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)){
//			if(item.getReceivedAmt().doubleValue() <= 0){
//				throw new EpException(MessageConst.ERROR_100033, new String[]{"Received Amount","Delivery Amount"});
//			}
//		}
	}
	
	/**
	 * Page navigation to FLDoc search page in FRN.
	 *
	 * @return the string
	 */
	public String backToPervious() {
		return "flDocSearch";
	}
	
	/**
	 * Gpki sign action.
	 *
	 * @param outcome the outcome
	 */
	public String gpkiSignAction(String outcome){
		if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {		
			validateFulfilmentNote(outcome);
			setTextBeforeSign(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			getManagedBean().setButtonAction(outcome);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		} else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)){
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			if(flBB.getManagedBean().getRemarkList().get(0).getRemark()==null || flBB.getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.txt.remarks",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			}
			if(getManagedBean().getTaskName().equalsIgnoreCase(FLProcessNameConst.APPROVE_FRN_HT)){
				setTextBeforeSign(outcome);		
				getManagedBean().setAutoUpdate(Boolean.FALSE);
				getManagedBean().setButtonAction(outcome);
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
				return null;
			}
			getManagedBean().setValidated(Boolean.TRUE);
			getManagedBean().setAutoUpdate(Boolean.TRUE);
			return submitAction(outcome);
		}
		else if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
			getManagedBean().setValidated(Boolean.FALSE);
			return submitAction(outcome);
		} else{
			getManagedBean().setValidated(Boolean.TRUE);
			getManagedBean().setAutoUpdate(Boolean.TRUE);
			return submitAction(outcome);
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
		getManagedBean().setValidated(Boolean.TRUE);
		log.info("######## FRN SLA start : "+System.currentTimeMillis()+" : "+new Date());
		return submitAction(getManagedBean().getButtonAction());
	}
	
	/**
	 * Gets the user role.
	 *
	 * @return the user role
	 */
	private String getUserRole() {
		if(getFacesScopes().hasRoleUser(RoleCode.RECEIVING_OFFICER.value())){
			return RoleCode.RECEIVING_OFFICER.value();		
		}else if(getFacesScopes().hasRoleUser(RoleCode.ACKNOWLEDGE_OFFICER.value())){
			return RoleCode.ACKNOWLEDGE_OFFICER.value();		
		}else{  
			throw new EpException(MessageConst.ERROR_750040);
		}
	}
	
	/**
	 * Gets the remaining quantity.
	 *
	 * @param flFulfilmentNoteItem the fl fulfilment note item
	 * @return the remaining quantity
	 */
	public double getRemainingQuantity(FlFulfilmentNoteItem flFulfilmentNoteItem) {
		if(flFulfilmentNoteItem.getReceivedQty()==null) {
			flFulfilmentNoteItem.setReceivedQty(BigDecimal.ZERO);
		}
		double orderQty = flFulfilmentNoteItem.getFlDelOrderItem().getFlFulfilmentItemAddr().getOrderedQty().doubleValue();
		double receivedQty = flFulfilmentNoteItem.getFlDelOrderItem().getFlFulfilmentItemAddr().getReceivedQty().doubleValue();
		return orderQty - receivedQty - flFulfilmentNoteItem.getReceivedQty().doubleValue();	
	}
	
	/**
	 * Navigate to do Hyper link page if doc type is DO.
	 *<p> Navigate to order No Hyper link page if doc type is PO.
	 * @param docType the doc type
	 * @return the string
	 */
	public String navigateDocument(String docType){
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_DELIVERY_ORDER)){
			IDeliveryOrderSO orderSO = (IDeliveryOrderSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IDeliveryOrderSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setDeliveryOrderId(getManagedBean().getFulfilmentNote().getFlDeliveryOrder().getDeliveryOrderId());
			reqDTO.setDocType(docType);
			FulfilmentResDTO resDTO = orderSO.retrieveDeliveryOrderById(reqDTO);
			DeliveryOrderBackingBean doBB = new DeliveryOrderBackingBean();
			FacesContext fc = FacesContext.getCurrentInstance();
			doBB.getManagedBean().setFlDeliveryOrder(resDTO.getFlDeliveryOrder());
			doBB.getManagedBean().setFulfilmentRequest(resDTO.getFlDeliveryOrder().getFlRequest());
			//doBB.getManagedBean().setFromPageUrl(fc.getViewRoot().getViewId());
			doBB.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
			doBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
			doBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());
			// Set DO status
			doBB.getManagedBean().setFulfilmentOrder(new FlFulfilmentOrder());
			doBB.getManagedBean().getFulfilmentOrder().setFulfilmentOrderId(resDTO.getFlDeliveryOrder().getFlFulfilmentOrder().getFulfilmentOrderId());
			doBB.getManagedBean().getFulfilmentOrder().setDocNo(resDTO.getFlDeliveryOrder().getFlFulfilmentOrder().getDocNo());
			doBB.getManagedBean().getFulfilmentOrder().setDocType(resDTO.getFlDeliveryOrder().getFlFulfilmentOrder().getDocType());
			doBB.getManagedBean().getFulfilmentOrder().setStatus(resDTO.getFlDeliveryOrder().getStatus());
			doBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			doBB.getManagedBean().setHyperLinkPage(true);
			flBB.getManagedBean().setHideRemarks(true);
			flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
			flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			return NAV_DELIVERYORDER_PAGE;
		}else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)||docType.equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
			IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setFlOrderId(getManagedBean().getFulfilmentNote().getFlRequest().getFlFulfilmentOrder().getFulfilmentOrderId());
			reqDTO.setDocType(docType);
			FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(reqDTO);
			FulfilmentOrderBackingBean orderBackingBean = new FulfilmentOrderBackingBean();
			orderBackingBean.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
			orderBackingBean.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			FacesContext fc = FacesContext.getCurrentInstance();
			orderBackingBean.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
			orderBackingBean.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
			orderBackingBean.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());
			orderBackingBean.getManagedBean().setHyperLinkPage(true);
			orderBackingBean.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			flBB.getManagedBean().setHideRemarks(true);
			flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
			flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			orderBackingBean.getManagedBean().setSupplier(hasSupplierRole());
			if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
				return NAV_PO;
			}else if (docType.equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
				return NAV_CO;
			}
		}
		return null;
	}
	
	/**
	 * sorting all the items showing in Item menu
	 *
	 * @param flFulfilmentNoteItemList the Fl Fulfilment Note Item list
	 */
	public void sortItems(List<FlFulfilmentNoteItem> flFulfilmentNoteItemList) {
		//Creating Comparator
		if(null==flFulfilmentNoteItemList || flFulfilmentNoteItemList.isEmpty()){
			return;
		}
		  Comparator<FlFulfilmentNoteItem> comparator = new Comparator<FlFulfilmentNoteItem>() {
	            @Override
	            public int compare(FlFulfilmentNoteItem o1, FlFulfilmentNoteItem o2) {
	            	String object1Code = o1.getFlDelOrderItem().getFlFulfilmentItemAddr().getItemCode();
	            	String object2Code = o2.getFlDelOrderItem().getFlFulfilmentItemAddr().getItemCode();
	                return new CompareToBuilder()
		                .append(object1Code, object2Code)
		                .toComparison();
	            }
	        };
			Collections.sort(flFulfilmentNoteItemList, comparator);
    }
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==1){
			sortItems(getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem());
			if(getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem().get(0).getFulfilmentNoteItemId() != null){
			}
			
		    getManagedBean().setSelectedItem(getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem().get(0));
			
			selectedItemRow();
		}
		
	}
	/**
	 * Enable auto update.
	 */
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}
	
	/**
	 * Back to document.
	 *
	 * @return the string
	 */
	public String backAction() {
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
	 * Show 1GFMAS error
	 *
	 */
	public void show1GFMASError(){
		getManagedBean().setAutoUpdate(Boolean.FALSE);
		RequestContext.getCurrentInstance().update("@form");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,null,getManagedBean().getGfmasErrorContent()));
	}
	
	/**
	 * Retrieve received note by fulfilment note id and display in UI
	 *
	 */
	public void viewReceivedNote() {
		IFulfilmentSO iFulfilmentSO = (IFulfilmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentSO.class);
		IReceivedNoteSO receivedNoteSO =(IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReceivedNoteSO.class);
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		
		String fulNoteId = originalServletRequest.getParameter("fulNoteId");
		
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		if(fulNoteId!=null) {
			reqDTO.setFulfilmentNoteId(Long.valueOf(fulNoteId));
			reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			reqDTO.setDocType(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
			reqDTO.setFrnLink(Boolean.TRUE);
			renewSesionObject();
			try {
				resDTO = receivedNoteSO.retrieveReceivedNoteByReceivedNoteId(reqDTO);
			} catch (EpException e) {
				handleException(e);
				return;
			}
			
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			getManagedBean().setSelectedTabIndex(0);
			getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			getManagedBean().setTaskId(BpmOutcomeConst.BPM_VIEW);//for the purpose of hide the back button.
//			getManagedBean().setDocProcess(FulfilmentConst.DOC_TYPE_DO);
			getManagedBean().setDocType(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
			getManagedBean().setFulfilmentNote(resDTO.getFulfilmentNote());
			getManagedBean().setFulfilmentRequest(resDTO.getFulfilmentNote().getFlRequest());
			getManagedBean().getFulfilmentNote().setFlRequest(resDTO.getFulfilmentNote().getFlRequest());
			
			reqDTO.setDeliveryOrderId(resDTO.getFulfilmentNote().getFlDeliveryOrder().getDeliveryOrderId());
			getManagedBean().setPmUser(reqDTO.getPmUser());
			getManagedBean().getFulfilmentNote().setFlFulfilmentNoteItem(resDTO.getFulfilmentNote().getFlFulfilmentNoteItem());
			getManagedBean().setDocMaintainRemarks(true);
			getManagedBean().setAcknowledged(true);
			getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			if(getManagedBean().getAcknowledgeDate() == null){
				FlActor actor = iFulfilmentSO.retrieveFlActors(ParameterConst.DOCTYPE_FULFILMENT_NOTE, getManagedBean().getFulfilmentNote().getFulfilmentNoteId(), RoleCode.ACKNOWLEDGE_OFFICER.value());
				if(actor != null){
					getManagedBean().setAcknowledgeDate(actor.getActionDate());
				}
			}
			if(getManagedBean().getFlApprover() == null){
				FlActor actor = iFulfilmentSO.retrieveFlActors(getManagedBean().getFulfilmentNote().getFlRequest().getDocType(), getManagedBean().getFulfilmentNote().getFlRequest().getFulfilmentReqId(), RoleCode.FL_APPROVER.value());
				if(actor != null){
					getManagedBean().setFlApprover(actor.getUser());
				}
			}
			
			flBB.getManagedBean().setHideRemarks(true);
			flBB.initializeRemarkAndAttachement(resDTO.getFulfilmentNote().getFlWorkflowStatus(), resDTO.getRemarkList(),resDTO.getAttachmentList());
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "frnBasePage?faces-redirect=true");
		}
	}	

	public boolean isFlFulfilmentApprover() {
		return getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value());
	}
	
	/**
	 * Check user login is receiving officer role true or false
	 *
	 */
	public boolean isReceivingOfficer() {
		if(getFacesScopes().hasRoleUser(RoleCode.RECEIVING_OFFICER.value())){
			return true;
		}else{
			return false;
		}
	}
	
	private void getSupplierGst(FlFulfilmentNote fulfilmentNote) {
		ISupplierGstSO supplierGstSO = (ISupplierGstSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierGstSO.class);
		
		SupplierGstReqDTO reqDTO = new SupplierGstReqDTO();
		reqDTO.setBranchCode(fulfilmentNote.getFlRequest().getFlSupplierDtl().getBranchCode());
		reqDTO.setSupplierId(fulfilmentNote.getFlRequest().getSmSupplier().getSupplierId());
		SupplierGstResDTO resDTO = supplierGstSO.retrieveGstInfoBySupplierIdAndBranchCode(reqDTO);
		if(resDTO.getSmGstSupplier() != null){
			fulfilmentNote.setGstEffectiveDate(resDTO.getSmGstSupplier().getGstEffDate());
			fulfilmentNote.setGstDeclaredDate(resDTO.getSmGstSupplier().getGstDeclaredDate());
			fulfilmentNote.setGstRegNo(resDTO.getSmGstSupplier().getGstRegNo());
		} else {
			fulfilmentNote.setGstEffectiveDate(null);
			fulfilmentNote.setGstDeclaredDate(null);
			fulfilmentNote.setGstRegNo(null);
		}

		boolean amtChanged = reCalculateGstUnitPrice(fulfilmentNote);
		if(amtChanged == true){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
					MessageUtils.getMessage(MessageConst.INFO_745009, new String[]{getManagedBean().getFulfilmentNote().getFlRequest().getDocNo()}, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
		}
	}
	
	/**
	 * Recalculate gst unit price
	 *
	 */
	public boolean reCalculateGstUnitPrice(FlFulfilmentNote fulfilmentNote){
		boolean amtChanged = false;
		if(fulfilmentNote.getFlRequest().getGstRate() == null){
			fulfilmentNote.getFlRequest().setGstRate(BigDecimal.ZERO);
		}
		
		boolean isFrnGST = fulfilmentNote.getGstEffectiveDate()!=null && fulfilmentNote.getGstEffectiveDate().getTime() <= getManagedBean().getFulfilmentNote().getReceivedDate().getTime();
		BigDecimal gstAmt = BigDecimal.ZERO;
		BigDecimal frnGstAmt = BigDecimal.ZERO;
		BigDecimal receivedAmt = BigDecimal.ZERO;
		
		for(FlFulfilmentNoteItem noteItem : fulfilmentNote.getFlFulfilmentNoteItem()){
			if(!fulfilmentNote.isBrandNew()) {
				frnGstAmt = noteItem.getGstAmount();
			}
			if(isFrnGST && noteItem.getFlDelOrderItem().getFlFulfilmentItemAddr().isGst()){
				gstAmt = noteItem.getFlDelOrderItem().getFlFulfilmentItemAddr().getUnitPrice().multiply(fulfilmentNote.getFlRequest().getGstRate().divide(new BigDecimal(100)));
				noteItem.setGstAmount(gstAmt.setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP));
				noteItem.setGstUnitPrice(noteItem.getFlDelOrderItem().getFlFulfilmentItemAddr().getUnitPrice().add(noteItem.getGstAmount()));
			} else {
				noteItem.setGstAmount(BigDecimal.ZERO);
				noteItem.setGstUnitPrice(noteItem.getFlDelOrderItem().getFlFulfilmentItemAddr().getUnitPrice());
			}
			if(noteItem.getFlDelOrderItem().getFlFulfilmentItemAddr().getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)){
				receivedAmt = noteItem.getReceivedQty().multiply(noteItem.getGstUnitPrice());
				noteItem.setReceivedAmt(receivedAmt.setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP));
			}
			if(!amtChanged && fulfilmentNote.isBrandNew() && noteItem.getFlDelOrderItem().getGstAmount().compareTo(noteItem.getGstAmount())!=0) {
				amtChanged = true;
			}
			if(!amtChanged && !fulfilmentNote.isBrandNew() && noteItem.getGstAmount().compareTo(frnGstAmt)!=0) {
				amtChanged = true;
			}
		}
		return amtChanged;
	}
	
	/**
	 * Applicable in the delivery order item rejection column. Selected the item the item amount, quantity, delivery quantity, delivery amount 
	 * total received amount , total received quantity and remaining quantity will be set and display
	 *
	 */
	public void selectedItemRow(){
		if(null != getManagedBean().getSelectedItem()){
			getManagedBean().setOrderedAmt(getManagedBean().getSelectedItem().getFlDelOrderItem().getFlFulfilmentItemAddr().getOrderedAmt());
			getManagedBean().setOrderedQty(getManagedBean().getSelectedItem().getFlDelOrderItem().getFlFulfilmentItemAddr().getOrderedQty());
			getManagedBean().setDeliveredQty(getManagedBean().getSelectedItem().getFlDelOrderItem().getDeliveredQty());
			getManagedBean().setDeliveredAmt(getManagedBean().getSelectedItem().getFlDelOrderItem().getDeliveredAmt());
			if(getManagedBean().isAcknowledged()){
				getManagedBean().setRemainingQty(getManagedBean().getSelectedItem().getFlDelOrderItem().getFlFulfilmentItemAddr().getOrderedQty().subtract(getManagedBean().getSelectedItem().getReceivedQty()));
			}else{
				BigDecimal remainQty = new BigDecimal(getRemainingQuantity(getManagedBean().getSelectedItem()));
				getManagedBean().setRemainingQty(remainQty);
			}
			getManagedBean().setItemTypeProduct(false);
			if(getManagedBean().getSelectedItem().getFlDelOrderItem().getFlFulfilmentItemAddr().getItemType().getParameterCode().equals(ParameterConst.ITEM_TYPE_PRODUCT)){
				getManagedBean().setItemTypeProduct(true);
			}else if(getManagedBean().getSelectedItem().getFlDelOrderItem().getFlFulfilmentItemAddr().getItemType().getParameterCode().equals(ParameterConst.ITEM_TYPE_SERVICE)){
				getManagedBean().setItemTypeProduct(false);
			}
		}
	}
	
	/**
	 * Retrieve a list of acknowledge officers 
	 *
	 */
	public SelectItem[] getAcknowledgeOfficerDropdown() {
		SelectItem[] items = null;
		getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId();
		
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId());
		reqDTO.setRoleCode(RoleCode.ACKNOWLEDGE_OFFICER.value());
		UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
		getManagedBean().setPmUserList(resDTO.getUserList());
		items = new SelectItem[resDTO.getUserList().size() + 1];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				getFacesScopes().getUserLocale()));
		
		int i = 1;
		for(PmUser user : resDTO.getUserList()) {
			items[i++] = new SelectItem(user.getUserId(), user.getUserName());
		}
	    return items;
	}
	
	public void setSelectedAckOfficer(){
		for(PmUser user : getManagedBean().getPmUserList()){
			if(user.getUserId().equals(getManagedBean().getUserLoginId())){
				getManagedBean().setSelectedAckOfficer(user);
				break;
			}
		}
	}
	
	public String getUsername(Long userId) {
		return PmCommonUtil.retriveUserName(userId);
	}
	

	
	public boolean checkUserAuthority(){
		boolean userAuthority = false;
		
		if(this.getManagedBean().getProcessName() != null){
			userAuthority = true;
		}
		
		return userAuthority;
	}
	
	private void setFulfilmentNoteField(FulfilmentResDTO resDTO){
		if(resDTO.getFulfilmentNote() != null){
			getManagedBean().setFulfilmentNote(resDTO.getFulfilmentNote());
		}
		getManagedBean().setSelectedItem(getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem().get(0));
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		flBB.getManagedBean().setHideRemarks(false);
		if(getManagedBean().getFulfilmentRequest().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_REQUEST)) {
			new FulfilmentBackingBean(ParameterConst.DOCTYPE_FULFILMENT_NOTE, getManagedBean().getFulfilmentRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
		} else {
			new FulfilmentBackingBean(ParameterConst.DOCTYPE_FULFILMENT_NOTE, getManagedBean().getFulfilmentRequest().getCtContract().getContractNo());
		}				
		if(resDTO.getFulfilmentNote()!=null) {
			flBB.initializeRemarkAndAttachement(resDTO.getFulfilmentNote().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
		}

		if(null == getManagedBean().getFulfilmentNote().getReceivedDate()){
			getManagedBean().getFulfilmentNote().setReceivedDate(DateUtils.getCurrentDateWithoutTime());
		}
		getSupplierGst(getManagedBean().getFulfilmentNote());
	
		if(getManagedBean().getFulfilmentNote().getApproverId() != null){
			getManagedBean().setUserLoginId(getManagedBean().getFulfilmentNote().getApproverId()); 
		}
		getManagedBean().setSelectedTabIndex(0);

		// Set evaluation
		if(this.getManagedBean().getTaskName().equalsIgnoreCase(FLProcessNameConst.APPROVE_FRN_HT)){
			getManagedBean().setDisableEvaluation(Boolean.TRUE);
		}
		if(getManagedBean().getFulfilmentNote().getFlSupplierEvaluation().size()==0) {
			retrieveCriteriaList();
		}
		if(null == getManagedBean().getFulfilmentNote().getReceivedDate()){
			getManagedBean().getFulfilmentNote().setReceivedDate(DateUtils.getCurrentDateWithoutTime());
		}
		if(null == getManagedBean().getFulfilmentNote().getCreatedDate()){
			getManagedBean().getFulfilmentNote().setCreatedDate(new Timestamp(System.currentTimeMillis()));
		}		
		if(null == getManagedBean().getFulfilmentNote().getSubmissionDate() || 
				(getManagedBean().getFulfilmentNote().getStatus()!=null && getManagedBean().getFulfilmentNote().getStatus().getStatusId().longValue()==FulfilmentStatusConst.FN_PENDING_DO_ACKNOWLEDGMENT.longValue())){
			getManagedBean().getFulfilmentNote().setSubmissionDate(DateUtils.getCurrentDateWithoutTime());
		}		
	}
	
	public void checkLastTaxFRN(){
		getManagedBean().setLastFRNTaxInv(false);
		BigDecimal sumTotalReceivedQty = BigDecimal.ZERO;
		BigDecimal sumTotalOrderQty = BigDecimal.ZERO;
		BigDecimal sumTotalOrderReceivedQty = BigDecimal.ZERO;
		for(FlFulfilmentNoteItem noteItem : getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem()){
			sumTotalReceivedQty = sumTotalReceivedQty.add(noteItem.getReceivedQty());
		}
		for(FlDeliveryAddress delAddr : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddr : delAddr.getFlFulfilmentItemAddr()){
				sumTotalOrderQty = sumTotalOrderQty.add(itemAddr.getOrderedQty());
				sumTotalOrderReceivedQty = sumTotalOrderReceivedQty.add(itemAddr.getReceivedQty());
			}
		}
		sumTotalReceivedQty = sumTotalOrderReceivedQty.add(sumTotalReceivedQty);
		if(sumTotalReceivedQty.doubleValue() == sumTotalOrderQty.doubleValue()){
			getManagedBean().setLastFRNTaxInv(true); 
		}
		if(getManagedBean().isLastFRNTaxInv()){
			IDesignatedAreaSO designatedAreaSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDesignatedAreaSO.class);
			IGstItemSO gstItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IGstItemSO.class);
			
			GstItemReqDTO gstItemReqDTO = new GstItemReqDTO();
			GstItemResDTO gstItemResDTO = null;
			gstItemReqDTO.setG2g(false);
			if(getManagedBean().getFulfilmentNote().getFlRequest().getProcurementType()!=null) {
				if(getManagedBean().getFulfilmentNote().getFlRequest().getProcurementType().getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
					gstItemReqDTO.setG2g(true);
				}
			}
			gstItemReqDTO.setGstEffDate(getManagedBean().getFulfilmentNote().getGstEffectiveDate());
			gstItemReqDTO.setProcurementCatCode(ParameterConst.PROCUREMENT_TYPE_CODE_SUPPLY);
			if(getManagedBean().getFulfilmentNote().getFlRequest().getProcurementTypeCategory()!=null) {
				gstItemReqDTO.setProcurementCatCode(getManagedBean().getFulfilmentNote().getFlRequest().getProcurementTypeCategory().getParameterCode());
			}
			if(getManagedBean().getFulfilmentNote().getFlRequest().getCtContract()!=null) {
				gstItemReqDTO.setSvcCharge(getManagedBean().getFulfilmentNote().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().isServiceTax());
				gstItemReqDTO.setLoaSignedDate(getManagedBean().getFulfilmentNote().getFlRequest().getCtContract().getLoa().getAckDate());
			}
			for(FlFulfilmentNoteItem frnItem : getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem()) {
				gstItemReqDTO.setGst(frnItem.getFlDelOrderItem().getFlFulfilmentItemAddr().isGst());
				gstItemReqDTO.setRequestType(getManagedBean().getFulfilmentRequest().getDocType());
				// default to PCA to PCA
				gstItemReqDTO.setGslTypeCode(GSLType.PCA_TO_PCA.value());
				
				// Set GSL Type
				DesignatedAreaReqDTO daReqDTO = new DesignatedAreaReqDTO();
				DesignatedAreaResDTO daResDTO = new DesignatedAreaResDTO();
				daReqDTO.setFromAddressId(getManagedBean().getFulfilmentNote().getFlRequest().getFlSupplierDtl().getFlAddress().getAddressId());
				daReqDTO.setToAddressId(getManagedBean().getFulfilmentNote().getFlDeliveryOrder().getFlDeliveryAddress().getFlAddress().getAddressId());
				daReqDTO.setActualReceivedDate(getManagedBean().getFulfilmentNote().getReceivedDate());
				daResDTO = designatedAreaSO.retrieveGSLParam(daReqDTO);
				if(daResDTO.isSuccessful()) {
					gstItemReqDTO.setGslTypeCode(daResDTO.getGslParam().getParameterCode());
				}
									
				gstItemResDTO = gstItemSO.retrieveGSTFlag(gstItemReqDTO);
				if(gstItemResDTO.isCoGSTRFlag()){
					getManagedBean().setLastFRNTaxInv(true);
					break;
				}else{
					getManagedBean().setLastFRNTaxInv(false); 
				}
			}
			if(!getManagedBean().isLastFRNTaxInv()){
				IReceivedNoteSO receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IReceivedNoteSO.class);
				FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
				reqDTO.setFlRequestId(getManagedBean().getFulfilmentNote().getFlRequest().getFulfilmentReqId());
				FulfilmentResDTO resDTO = receivedNoteSO.retrieveReceivedNoteByReqId(reqDTO);
				for(FlFulfilmentNote note : resDTO.getFulfilmentNoteList()){
					if(getManagedBean().isLastFRNTaxInv()){
						break;
					}
					for(FlFulfilmentNoteItem frnItem : note.getFlFulfilmentNoteItem()) {
						if(frnItem.isCoGSTRReq()){
							getManagedBean().setLastFRNTaxInv(true);
							break;
						}else{
							getManagedBean().setLastFRNTaxInv(false); 
						}
					}
				}
			}
		}
	}
	
	public String navigateToFlRequest(){
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		if(getManagedBean().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_REQUEST)){
			IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setFlRequestId(getManagedBean().getFulfilmentRequest().getFulfilmentReqId());
			reqDTO.setFlRequestLink(true);
			FulfilmentResDTO resDTO = requestSO.retrieveFulfilmentRequest(reqDTO);
			
			ContractRequestBackingBean crBB = new ContractRequestBackingBean();
			crBB.renewSesionObject();
			crBB = new ContractRequestBackingBean();
			crBB.getManagedBean().setSelectedTabIndex(0);
			crBB.getManagedBean().setDocProcess(getManagedBean().getDocType());
			
			FacesContext fc = FacesContext.getCurrentInstance();
			crBB.getManagedBean().setBackToDocument(fc.getViewRoot().getViewId());
			crBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
			crBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());		
			crBB.getManagedBean().setHyperLinkPage(true);
			flBB.getManagedBean().setHideRemarks(true);
			
			crBB.setFulfilmentRequestField(resDTO);
			crBB.initializeApproverGroup();
			
			crBB.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			crBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			crBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
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
		}if(getManagedBean().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_REQUEST)){
			IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setFlRequestId(getManagedBean().getFulfilmentRequest().getFulfilmentReqId());
			reqDTO.setFlRequestLink(true);
			FulfilmentResDTO resDTO = requestSO.retrieveFulfilmentRequest(reqDTO);
			
			PurchaseRequestBackingBean prBB = new PurchaseRequestBackingBean();
			prBB.getManagedBean().setSubmitButtonStatus(false);
			prBB.getManagedBean().setDocProcess(getManagedBean().getDocType());
			
			FacesContext fc = FacesContext.getCurrentInstance();
			prBB.getManagedBean().setBackToDocument(fc.getViewRoot().getViewId());
			prBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
			prBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());		
			flBB.getManagedBean().setHideRemarks(true);
			
			prBB.setFulfilmentRequestField(resDTO);
			prBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			prBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			prBB.getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			prBB.getManagedBean().setHyperLinkPage(true);
			return NAV_PR;
		}
		return null;
		
	}
	
	private void setTextBeforeSign(String outcome) {
		StringBuffer sb = new StringBuffer();
		sb.append(getManagedBean().getFulfilmentRequest().getFlFulfilmentOrder().getDocNo()+"|");
		sb.append(getManagedBean().getFulfilmentNote().getFulfilmentNoteNo()+"|");
		sb.append(outcome+"|");
		sb.append(getFacesScopes().getUserSessionBean().getIdentificationNo());
		getManagedBean().setTextBeforeSign(sb.toString());
	}
	
	private boolean checkTaxInvoice(Long reqId){
		IFulfilmentSO fulfilmentSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentSO.class);
		return fulfilmentSO.checkTaxInvoice(reqId);
	}
		
}
