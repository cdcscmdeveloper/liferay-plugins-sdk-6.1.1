/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.frn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.frn.IReceivedNoteSO;
import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.pm.common.criteria.ICriteriaSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.common.criteria.CriteriaReqDTO;
import my.ep.dto.pm.common.criteria.CriteriaResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.frndata.FLFulFillmentRecievedNoteDataType;
import my.ep.jaxb.fl.maintenace.cancel.dofulfillment.FLDOFulfillmentCancelReqType;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNote;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNoteItem;
import my.ep.persistence.entity.fl.frn.FlSupplierEvaluation;
import my.ep.persistence.entity.fl.order.FlDelOrderItem;
import my.ep.persistence.entity.pm.common.PmCriteria;
import my.ep.persistence.entity.pm.common.PmCriteriaDesc;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.frn.ReceivedNoteManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean for Create Received Note 
 * 
 * @author Giribabu
 * @since 7/10/2013
 */
@ManagedBean(name = "receivedNoteBackingBean")
@RequestScoped
public class ReceivedNoteBackingBean extends BaseBackingBean<ReceivedNoteManagedBean> {
	private static Logger log = Logger.getLogger(ReceivedNoteBackingBean.class);
	private String className="@(.myFrnEvalClassName)";
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
	
	public String submitAction(String action) {
		log.debug("ReceivedNoteBackingBean.submitAction()");
		IReceivedNoteSO receivedNoteSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		try {
			validateFulfilmentNote();
			
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FlFulfilmentNote fulfilmentNote = getManagedBean().getFulfilmentNote();
			receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IReceivedNoteSO.class);
			
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setFulfilmentNote(fulfilmentNote);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			if(flBB.getManagedBean().getRemarkList().size()>1) {
				reqDTO.setFlRemarkList(flBB.getManagedBean().getRemarkList());
			}
			
			reqDTO.setToken(token);
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setFrnCancellationProcess(getManagedBean().isFrnCancellationProcess());
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){	
				reqDTO.setPmUser(getManagedBean().getPmUser());
				resDTO = receivedNoteSO.receiveNoteModification(reqDTO);
			} else {
				resDTO = receivedNoteSO.saveReceivedNote(reqDTO);
			}
			
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700011;
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700004;
				}else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)){
					messageId = MessageConst.INFO_700050;
				}
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)){
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(messageId, 
									new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_FULFILMENT_NOTE), fulfilmentNote.getFulfilmentNoteNo()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, 
									new FacesScopes().getUserLocale())));
				}else{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(messageId, 
									new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_FULFILMENT_NOTE), resDTO.getFulfilmentNoteNo()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, 
									new FacesScopes().getUserLocale())));
				}
				
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					getManagedBean().setFulfilmentNote(resDTO.getFulfilmentNote());
					getManagedBean().setSelectedItem(getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem().get(0));
					reqDTO.setDeliveryOrderId(getManagedBean().getFulfilmentNote().getFlDeliveryOrder().getDeliveryOrderId());
					getManagedBean().getFulfilmentNote().setStatus(receivedNoteSO.retrieveReceivedNoteStatus(reqDTO).getPmStatus());
					flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
				}
			}
		} finally {
			receivedNoteSO = null;
		}
		return null;
	}
	
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		if(taskId != null){
			renewSesionObject();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO bpmRequestDTO = new BpmRequestDTO();
			bpmRequestDTO.setCtxToken(ctx);
			bpmRequestDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(bpmRequestDTO);
			Object obj = res.getPojo();
			
			IReceivedNoteSO receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReceivedNoteSO.class);
			IDeliveryOrderSO deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IDeliveryOrderSO.class);
			if(res.getProcessName().equalsIgnoreCase(FLProcessNameConst.FRN_CANCELLATION_PROCESS_NAME)){
				FLDOFulfillmentCancelReqType fulfillmentCancelReqType = (FLDOFulfillmentCancelReqType)obj;
				reqDTO.setDeliveryOrderId(fulfillmentCancelReqType.getDoId());
				resDTO = deliveryOrderSO.retrieveDeliveryOrderById(reqDTO);
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
				
				getManagedBean().setFulfilmentRequest(resDTO.getFlDeliveryOrder().getFlRequest());
				getManagedBean().setTaskId(taskId);
				getManagedBean().setOutComes(res.getAction());
				getManagedBean().setDocType(resDTO.getFlDeliveryOrder().getFlRequest().getDocType());
				
				getManagedBean().getFulfilmentNote().setFlDeliveryOrder(resDTO.getFlDeliveryOrder());
				getManagedBean().getFulfilmentNote().setFlRequest(getManagedBean().getFulfilmentRequest());
				List<FlFulfilmentNoteItem> noteItemList=new ArrayList<FlFulfilmentNoteItem>();
				int i = 0;
				for (FlDelOrderItem delOrderItem : resDTO.getFlDeliveryOrder().getFlDelOrderItem()){
					if(delOrderItem.getDeliveredQty().doubleValue()>0) { 
						FlFulfilmentNoteItem noteItem=new FlFulfilmentNoteItem();
						noteItem.setFlDelOrderItem(delOrderItem);
						noteItem.setFlFulfilmentNote(getManagedBean().getFulfilmentNote());
						noteItem.setFlItem(delOrderItem.getFlItem());
						if(i == 0){
							getManagedBean().setSelectedItem(noteItem);
						}
						i++;
						noteItemList.add(noteItem);
					}
				}
				//Add noteItemList to Managed Bean List.
				getManagedBean().getFulfilmentNote().setFlFulfilmentNoteItem(noteItemList);
				getManagedBean().getFulfilmentNote().setFlRequest(resDTO.getFlDeliveryOrder().getFlRequest());
				getManagedBean().setFrnCancellationProcess(true);
			}else{
				FLFulFillmentRecievedNoteDataType fulFillmentRecievedNoteDataType = (FLFulFillmentRecievedNoteDataType)obj;
				reqDTO.setDeliveryOrderId(fulFillmentRecievedNoteDataType.getDoId());
				
				resDTO = receivedNoteSO.retrieveReceivedNoteByDeliveryOrderId(reqDTO);
				if(resDTO.getFulfilmentNote()!=null) {
					getManagedBean().setFulfilmentNote(resDTO.getFulfilmentNote());
					getManagedBean().setFulfilmentRequest(resDTO.getFulfilmentNote().getFlRequest());
					getManagedBean().setDocType(resDTO.getFulfilmentNote().getFlRequest().getDocType());
				} else {
					resDTO = deliveryOrderSO.retrieveDeliveryOrderById(reqDTO);
					getManagedBean().setFulfilmentRequest(resDTO.getFlDeliveryOrder().getFlRequest());
					getManagedBean().getFulfilmentNote().setFlDeliveryOrder(resDTO.getFlDeliveryOrder());
					getManagedBean().getFulfilmentNote().setFlRequest(resDTO.getFlDeliveryOrder().getFlRequest());
					getManagedBean().setDocType(resDTO.getFlDeliveryOrder().getFlRequest().getDocType());
					
					List<FlFulfilmentNoteItem> noteItemList=new ArrayList<FlFulfilmentNoteItem>();
					for (FlDelOrderItem delOrderItem : resDTO.getFlDeliveryOrder().getFlDelOrderItem()){
						if(delOrderItem.getDeliveredQty().doubleValue()>0) { 
							FlFulfilmentNoteItem noteItem=new FlFulfilmentNoteItem();
							noteItem.setFlDelOrderItem(delOrderItem);
							noteItem.setFlFulfilmentNote(getManagedBean().getFulfilmentNote());
							noteItem.setFlItem(delOrderItem.getFlItem());
							if(delOrderItem.getFlFulfilmentItemAddr().getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
								noteItem.setReceivedQty(delOrderItem.getDeliveredQty());
								noteItem.setReceivedAmt(delOrderItem.getDeliveredAmt());
							}						
							noteItemList.add(noteItem);
						}
					}
					getManagedBean().getFulfilmentNote().setFlFulfilmentNoteItem(noteItemList);
				}
				getManagedBean().setSelectedItem(getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem().get(0));
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				if(getManagedBean().getFulfilmentRequest().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_REQUEST)) {
					new FulfilmentBackingBean(ParameterConst.DOCTYPE_FULFILMENT_NOTE, getManagedBean().getFulfilmentRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
				} else {
					new FulfilmentBackingBean(ParameterConst.DOCTYPE_FULFILMENT_NOTE, getManagedBean().getFulfilmentRequest().getCtContract().getContractNo());
				}				
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
				
				String[] outcome = new String[res.getAction().length + 1];
				outcome[0] = BpmOutcomeConst.BPM_SAVE;
				int x = 1;
				for (String action : res.getAction()) {
					outcome[x++] = action;
				}
				getManagedBean().setTaskId(taskId);
				getManagedBean().setOutComes(outcome);
			}
			getManagedBean().setSelectedTabIndex(0);
			reqDTO.setDeliveryOrderId(getManagedBean().getFulfilmentNote().getFlDeliveryOrder().getDeliveryOrderId());
			getManagedBean().getFulfilmentNote().setStatus(receivedNoteSO.retrieveReceivedNoteStatus(reqDTO).getPmStatus());
			
			// Set evaluation
			if(getManagedBean().getFulfilmentNote().getFlSupplierEvaluation().size()==0) {
				retrieveCriteriaList();
			}
			if(null == getManagedBean().getFulfilmentNote().getReceivedDate()){
				getManagedBean().getFulfilmentNote().setReceivedDate(getCurrentDate());
			}
			
		}
	}
	
	public void retrieveReceivedAmt(FlFulfilmentNoteItem item) {
		if(item.getReceivedQty().doubleValue() > item.getFlDelOrderItem().getDeliveredQty().doubleValue()) {
			item.setReceivedQty(new BigDecimal(0));
			throw new EpException(MessageConst.ERROR_100032, new String[]{"Received Quantity","Delivery Quantity"});
		}
		BigDecimal receivedAmt = item.getReceivedQty().multiply(item.getFlDelOrderItem().getFlFulfilmentItemAddr().getUnitPrice());
		item.setReceivedAmt(receivedAmt);
	}
	 
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
	
	public String getCriteriaDesc(PmCriteria pmCriteria) {
		ICriteriaSO criteriaSO = (ICriteriaSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICriteriaSO.class);
		CriteriaReqDTO reqDTO = new CriteriaReqDTO();
		reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
		reqDTO.setCriteriaId(pmCriteria.getCriteriaId());
		CriteriaResDTO resDTO = criteriaSO.retrieveCriteriaDescById(reqDTO);
		return resDTO.getPmCriteriaDesc().getCriteria();
	}
	
	private void validateFulfilmentNote() {
		if(getManagedBean().getFulfilmentNote().getReceivedDate()==null) {
			throw new EpException(MessageConst.ERROR_100022, new String[]{"Actual Received Date"});
		}
		for(FlSupplierEvaluation supplierEvaluation : getManagedBean().getFulfilmentNote().getFlSupplierEvaluation()) {
			if(supplierEvaluation.getScore()==null) {
				throw new EpException(MessageConst.ERROR_100051, new String[]{"Score",getCriteriaDesc(supplierEvaluation.getPmCriteria())});
			}
		}
		for(FlFulfilmentNoteItem frnItem : getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem()) {
			if(frnItem.getReceivedQty().doubleValue() < frnItem.getFlDelOrderItem().getDeliveredQty().doubleValue()) {
				frnItem.setRejectedQty(frnItem.getFlDelOrderItem().getDeliveredQty().subtract(frnItem.getReceivedQty()));
				if(frnItem.getRejectionRemark()==null || frnItem.getRejectionRemark().length()==0) {
					throw new EpException(MessageConst.ERROR_740024, new String[]{"Remark",frnItem.getFlDelOrderItem().getFlFulfilmentItemAddr().getScRequestItem().getItemName()});
				}
			}
			if(frnItem.getReceivedAmt().doubleValue() < frnItem.getFlDelOrderItem().getDeliveredAmt().doubleValue()) {
				frnItem.setRejectedAmt(frnItem.getFlDelOrderItem().getDeliveredAmt().subtract(frnItem.getReceivedAmt()));
				if(frnItem.getRejectionRemark()==null || frnItem.getRejectionRemark().length()==0) {
					throw new EpException(MessageConst.ERROR_740024, new String[]{"Remark",frnItem.getFlDelOrderItem().getFlFulfilmentItemAddr().getScRequestItem().getItemName()});
				}
			}			
		}
	}
	
	public Date getCurrentDate() {
		return new Date();
	}
	
	public void checkScore(FlSupplierEvaluation supplierEvaluation){
		if(supplierEvaluation.getScore() > 5 || supplierEvaluation.getScore() == 0){
			String score = supplierEvaluation.getScore().toString();
			supplierEvaluation.setScore(null);
			throw new EpException(MessageConst.ERROR_100039, new String[]{score,"1","5"});
		}
	}
	public void receivedAmtValidation(FlFulfilmentNoteItem item) {
		if(item.getReceivedAmt().doubleValue() > item.getFlDelOrderItem().getDeliveredAmt().doubleValue()) {
			item.setReceivedAmt(new BigDecimal(0));
			throw new EpException(MessageConst.ERROR_100032, new String[]{"Received Amount","Delivery Amount"});
		}
	}
}
