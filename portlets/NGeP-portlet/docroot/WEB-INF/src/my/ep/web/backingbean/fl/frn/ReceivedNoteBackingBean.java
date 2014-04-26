/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.frn;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.frn.IReceivedNoteSO;
import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.dodata.FLDeliveryOrderDataType;
import my.ep.jaxb.fl.frndata.FLFulFillmentRecievedNoteDataType;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNote;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNoteItem;
import my.ep.persistence.entity.fl.order.FlDelOrderItem;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
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
		try {
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentNote fulfilmentNote = getManagedBean().getFulfilmentNote();
			receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject("fl",IReceivedNoteSO.class);
			System.out.println("getReceivedQty: "+getManagedBean().getFulfilmentNoteItem().getReceivedQty());
			getUserProfiles(reqDTO);
			reqDTO.setFulfilmentNote(fulfilmentNote);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			reqDTO.setDeliveryOrderId(getManagedBean().getDeliveryOrderId());
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			reqDTO.setToken(token);
			FulfilmentResDTO resDTO = receivedNoteSO.saveReceivedNote(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "fulfilmentNote id. : "+fulfilmentNote.getFulfilmentNoteId();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.",message));
				if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { // save action will remain in the same page
					renewSesionObject();
					return "/xhtml/shared/acknowledge.xhtml";
				}
			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception e) {
			log.error("Exception>>", e);
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		
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
		if(taskId != null){

			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			FacesScopes facesScopes = new FacesScopes();
			String token = facesScopes.getBPMAuthenticationCtxToken();

			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();

			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO delOrderResDTO = new FulfilmentResDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			IDeliveryOrderSO deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IDeliveryOrderSO.class);
			FLFulFillmentRecievedNoteDataType fulFillmentRecievedNoteDataType = (FLFulFillmentRecievedNoteDataType)obj;
//			flReqDTO.setDeliveryOrderId(fulFillmentRecievedNoteDataType.getDoId());//TODO ask bpm provide doId
			delOrderResDTO = deliveryOrderSO.retrieveDeliveryOrderById(flReqDTO);
			flReqDTO.setFlRequestId(fulFillmentRecievedNoteDataType.getFlRequestId());
			resDTO = fulfilmentSO.retrieveFulfilmentRequest(flReqDTO);
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
			
			getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			//getManagedBean().setFulfilmentRequest(delOrderResDTO.getFlDeliveryOrder().getFlFulfilmentOrder().getFlFulfilmentRequest());
//			getManagedBean().setDeliveryOrderId(deliveryOrderDataType.getDoId());
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(res.getAction());
			System.out.println("Doc type in FRN::: "+resDTO.getFlFulfilmentRequest().getDocType());
			getManagedBean().setDocType(resDTO.getFlFulfilmentRequest().getDocType());
			
			getManagedBean().getFulfilmentNote().setFlDeliveryOrder(delOrderResDTO.getFlDeliveryOrder());
			List<FlFulfilmentNoteItem> noteItemList=new ArrayList<FlFulfilmentNoteItem>();
			for (FlDelOrderItem delOrderItem : delOrderResDTO.getFlDeliveryOrder().getFlDelOrderItem()){
				FlFulfilmentNoteItem noteItem=new FlFulfilmentNoteItem();
				noteItem.setFlDelOrderItem(delOrderItem);
				noteItem.setFlFulfilmentNote(getManagedBean().getFulfilmentNote());
				noteItemList.add(noteItem);
				//getManagedBean().getFulfilmentNoteItem().setFlDelOrderItem(delOrderItem);
				//getManagedBean().getFulfilmentNoteItem().setFlFulfilmentNote(getManagedBean().getFulfilmentNote());
			}
			//Add noteItemList to Managed Bean List.
			getManagedBean().getFulfilmentNote().setFlFulfilmentNoteItem(noteItemList);
			
			getManagedBean().getFulfilmentNoteItem();
			/*if(getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem() == null){
				
			}*/
			//getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem().add(getManagedBean().getFulfilmentNoteItem());
			

		}
	}
	
	public void getUserProfiles(FulfilmentReqDTO reqDTO){
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setLoginId(getFacesScopes().getUserSessionBean().getLoginId());
		UserResDTO userResDTO = userSO.retrieveUserByLoginId(userReqDTO);
		reqDTO.setPmUser(userResDTO.getPmUser());
	}
}
