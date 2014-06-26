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
import my.ep.biz.service.pm.authorization.user.IUserSO;
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
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.frndata.FLFulFillmentRecievedNoteDataType;
import my.ep.jaxb.fl.maintenace.cancel.dofulfillment.FLDOFulfillmentCancelReqType;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNote;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNoteItem;
import my.ep.persistence.entity.fl.order.FlDelOrderItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
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
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentNote fulfilmentNote = getManagedBean().getFulfilmentNote();
			receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject("fl",IReceivedNoteSO.class);
			System.out.println("getReceivedQty: "+getManagedBean().getFulfilmentNoteItem().getReceivedQty());
			getUserProfiles(reqDTO);
			//fulfilmentNote.setFlRequest(getManagedBean().getFulfilmentRequest());
			reqDTO.setFulfilmentNote(fulfilmentNote);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			reqDTO.setDeliveryOrderId(getManagedBean().getDeliveryOrderId());
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			if(!flBB.getManagedBean().getRemarkList().isEmpty()){
				reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			}
			
			reqDTO.setToken(token);
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setFrnCancellationProcess(getManagedBean().isFrnCancellationProcess());
			if("UPDATE".equalsIgnoreCase(action) || "CANCEL".equalsIgnoreCase(action)){
				//TODO
				// check Actual Received Date shouldnt be empty
				 reqDTO.setPmUser(getManagedBean().getPmUser());
				 resDTO = receivedNoteSO.receiveNoteModification(reqDTO);
			}else{
				resDTO = receivedNoteSO.saveReceivedNote(reqDTO);
			}
			
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700011;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, 
						null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_REQUEST), getManagedBean().getFulfilmentNote().getFulfilmentNoteNo()}, 
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
			
		}  finally {
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

			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
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
					//getManagedBean().getFulfilmentNoteItem().setFlDelOrderItem(delOrderItem);
					//getManagedBean().getFulfilmentNoteItem().setFlFulfilmentNote(getManagedBean().getFulfilmentNote());
				}
				//Add noteItemList to Managed Bean List.
				getManagedBean().getFulfilmentNote().setFlFulfilmentNoteItem(noteItemList);
				getManagedBean().getFulfilmentNoteItem();
				getManagedBean().getFulfilmentNote().setFlRequest(resDTO.getFlDeliveryOrder().getFlRequest());
				/*if(getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem() == null){
					
				}*/
				//getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem().add(getManagedBean().getFulfilmentNoteItem());
				getManagedBean().setFrnCancellationProcess(true);

			}else{
				FLFulFillmentRecievedNoteDataType fulFillmentRecievedNoteDataType = (FLFulFillmentRecievedNoteDataType)obj;
				reqDTO.setDeliveryOrderId(fulFillmentRecievedNoteDataType.getDoId());
				resDTO = deliveryOrderSO.retrieveDeliveryOrderById(reqDTO);
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
				
				getManagedBean().setFulfilmentRequest(resDTO.getFlDeliveryOrder().getFlRequest());
				//getManagedBean().setFulfilmentRequest(delOrderResDTO.getFlDeliveryOrder().getFlFulfilmentOrder().getFlFulfilmentRequest());
				getManagedBean().setDeliveryOrderId(fulFillmentRecievedNoteDataType.getDoId());
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
					//getManagedBean().getFulfilmentNoteItem().setFlDelOrderItem(delOrderItem);
					//getManagedBean().getFulfilmentNoteItem().setFlFulfilmentNote(getManagedBean().getFulfilmentNote());
				}
				//Add noteItemList to Managed Bean List.
				getManagedBean().getFulfilmentNote().setFlFulfilmentNoteItem(noteItemList);
				getManagedBean().getFulfilmentNoteItem();
				getManagedBean().getFulfilmentNote().setFlRequest(resDTO.getFlDeliveryOrder().getFlRequest());
				/*if(getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem() == null){
					
				}*/
				//getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem().add(getManagedBean().getFulfilmentNoteItem());
				

			}
			
		}
	}
	
	public void getUserProfiles(FulfilmentReqDTO reqDTO){
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setLoginId(getFacesScopes().getUserSessionBean().getLoginId());
		UserResDTO userResDTO = userSO.retrieveUserByLoginId(userReqDTO);
		reqDTO.setPmUser(userResDTO.getPmUser());
	}
	
	/* public void combineSupAddress(FlFulfilmentRequest fulfilmentRequest){
	    	
	    	StringBuilder address = new StringBuilder();
	    	String strSpace = " ";
	    	if(null != fulfilmentRequest.getSupplierBranch()){
	    		if(null != fulfilmentRequest.getSupplierBranch().getSmAddress()){
	    			if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress1() ){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress1());
	    	    		address.append(strSpace);
	    	    	
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress2()){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress2());
	    	    		address.append(strSpace);
	    	    	
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress3() ){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress3());
	    	    		address.append(strSpace);
	    	    	
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress4()){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress4());
	    	    		address.append(strSpace);
	    	    		
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPmDistrict().getDistrictName()){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmDistrict().getDistrictName());
	    	    		address.append(strSpace);
	    	    	
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPmState().getStateName()){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmState().getStateName());
	    	    		address.append(strSpace);
	    	    	
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPmCountry().getCountryName()){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmCountry().getCountryName());
	    	    		address.append("\n");
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPostcode()){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPostcode());
	    	    	}
	    	    	if(null != address.toString()){
	    	    		System.out.println("addresss     "+address.toString());
	    	    	}
	    			
	    		}
	    	}	
	    	
	    	this.getManagedBean().setSupplierAddress(address.toString());
	    }*/
	 
	 public void retrieveReceivedAmt(FlFulfilmentNoteItem item) {
			BigDecimal receivedAmt = new BigDecimal(0);
			if(null != item.getReceivedQty()){
				receivedAmt = item.getReceivedQty().multiply(item.getFlDelOrderItem().getFlFulfilmentItemAddr().getUnitPrice());
			}
			item.setReceivedAmt(receivedAmt);
		}
}
