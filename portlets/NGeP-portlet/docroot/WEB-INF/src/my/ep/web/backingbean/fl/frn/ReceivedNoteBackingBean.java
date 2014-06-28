/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.frn;

import java.math.BigDecimal;
import java.util.ArrayList;
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
			reqDTO.setDeliveryOrderId(getManagedBean().getDeliveryOrderId());
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			
			reqDTO.setToken(token);
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setFrnCancellationProcess(getManagedBean().isFrnCancellationProcess());
			if ("UPDATE".equalsIgnoreCase(action) || "CANCEL".equalsIgnoreCase(action)) {
				reqDTO.setPmUser(getManagedBean().getPmUser());
				resDTO = receivedNoteSO.receiveNoteModification(reqDTO);
			} else {
				resDTO = receivedNoteSO.saveReceivedNote(reqDTO);
			}
			
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700011;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_FULFILMENT_NOTE), resDTO.getFulfilmentNoteNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, 
								new FacesScopes().getUserLocale())));
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} 
			}
		} finally {
			receivedNoteSO = null;
		}
		return null;
	}
	
	public void preRenderView() {
		System.out.println("Prerender");
		//		BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		//		String loginId = originalServletRequest.getParameter("loginId");
		System.out.println("taskId:" + taskId);
		//		System.out.println("loginId:" + loginId);
		IReceivedNoteSO receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReceivedNoteSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		if(taskId != null){

			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO bpmRequestDTO = new BpmRequestDTO();
			FacesScopes facesScopes = new FacesScopes();
			String token = facesScopes.getBPMAuthenticationCtxToken();
			bpmRequestDTO.setCtxToken(ctx);
			bpmRequestDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(bpmRequestDTO);
			Object obj = res.getPojo();

			IDeliveryOrderSO deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IDeliveryOrderSO.class);
			if(res.getProcessName().equalsIgnoreCase(FLProcessNameConst.FRN_CANCELLATION_PROCESS_NAME)){
				FLDOFulfillmentCancelReqType fulfillmentCancelReqType = (FLDOFulfillmentCancelReqType)obj;
				reqDTO.setDeliveryOrderId(fulfillmentCancelReqType.getDoId());
				resDTO = deliveryOrderSO.retrieveDeliveryOrderById(reqDTO);
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
				
				getManagedBean().setFulfilmentRequest(resDTO.getFlDeliveryOrder().getFlRequest());
				//getManagedBean().setFulfilmentRequest(delOrderResDTO.getFlDeliveryOrder().getFlFulfilmentOrder().getFlFulfilmentRequest());
				getManagedBean().setDeliveryOrderId(fulfillmentCancelReqType.getDoId());
				getManagedBean().setTaskId(taskId);
				getManagedBean().setOutComes(res.getAction());
				System.out.println("Doc type in FRN::: "+resDTO.getFlDeliveryOrder().getFlRequest().getDocType());
				getManagedBean().setDocType(resDTO.getFlDeliveryOrder().getFlRequest().getDocType());
				
				//combineSupAddress(resDTO.getFlDeliveryOrder().getFlRequest());
				getManagedBean().getFulfilmentNote().setFlDeliveryOrder(resDTO.getFlDeliveryOrder());
				List<FlFulfilmentNoteItem> noteItemList=new ArrayList<FlFulfilmentNoteItem>();
				int i = 0;
				for (FlDelOrderItem delOrderItem : resDTO.getFlDeliveryOrder().getFlDelOrderItem()){
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
				//Add noteItemList to Managed Bean List.
				getManagedBean().getFulfilmentNote().setFlFulfilmentNoteItem(noteItemList);
				getManagedBean().getFulfilmentNote().setFlRequest(resDTO.getFlDeliveryOrder().getFlRequest());
				getManagedBean().setFrnCancellationProcess(true);

			}else{
				FLFulFillmentRecievedNoteDataType fulFillmentRecievedNoteDataType = (FLFulFillmentRecievedNoteDataType)obj;
				reqDTO.setDeliveryOrderId(fulFillmentRecievedNoteDataType.getDoId());
				resDTO = deliveryOrderSO.retrieveDeliveryOrderById(reqDTO);
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
				
				getManagedBean().setFulfilmentRequest(resDTO.getFlDeliveryOrder().getFlRequest());
				getManagedBean().setDeliveryOrderId(fulFillmentRecievedNoteDataType.getDoId());
				getManagedBean().setTaskId(taskId);
				getManagedBean().setOutComes(res.getAction());
				System.out.println("Doc type in FRN::: "+resDTO.getFlDeliveryOrder().getFlRequest().getDocType());
				getManagedBean().setDocType(resDTO.getFlDeliveryOrder().getFlRequest().getDocType());
				
				getManagedBean().getFulfilmentNote().setFlDeliveryOrder(resDTO.getFlDeliveryOrder());
				List<FlFulfilmentNoteItem> noteItemList=new ArrayList<FlFulfilmentNoteItem>();
				int i = 0;
				for (FlDelOrderItem delOrderItem : resDTO.getFlDeliveryOrder().getFlDelOrderItem()){
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
				//Add noteItemList to Managed Bean List.
				getManagedBean().getFulfilmentNote().setFlFulfilmentNoteItem(noteItemList);
				getManagedBean().getFulfilmentNote().setFlRequest(resDTO.getFlDeliveryOrder().getFlRequest());
				getManagedBean().getFulfilmentNote().setStatus(receivedNoteSO.retrieveReceivedNoteStatus(reqDTO).getPmStatus());
			}
			getManagedBean().setSelectedTabIndex(0);
			reqDTO.setDeliveryOrderId(getManagedBean().getFulfilmentNote().getFlDeliveryOrder().getDeliveryOrderId());
			getManagedBean().getFulfilmentNote().setStatus(receivedNoteSO.retrieveReceivedNoteStatus(reqDTO).getPmStatus());
			
			// Set evaluation
			if(getManagedBean().getFulfilmentNote().getFlSupplierEvaluation().size()==0) {
				retrieveCriteriaList();
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
					throw new EpException(MessageConst.ERROR_100051, new String[]{"Remark",frnItem.getFlDelOrderItem().getFlFulfilmentItemAddr().getScRequestItem().getItemName()});
				}
			}
			if(frnItem.getReceivedAmt().doubleValue() < frnItem.getFlDelOrderItem().getDeliveredAmt().doubleValue()) {
				frnItem.setRejectedAmt(frnItem.getFlDelOrderItem().getDeliveredAmt().subtract(frnItem.getReceivedAmt()));
				if(frnItem.getRejectionRemark()==null || frnItem.getRejectionRemark().length()==0) {
					throw new EpException(MessageConst.ERROR_100051, new String[]{"Remark",frnItem.getFlDelOrderItem().getFlFulfilmentItemAddr().getScRequestItem().getItemName()});
				}
			}			
		}
	}
}
