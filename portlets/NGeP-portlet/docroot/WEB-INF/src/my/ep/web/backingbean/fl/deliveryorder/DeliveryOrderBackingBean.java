/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.deliveryorder;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.fl.order.FlDelOrderItem;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.creatdo.DeliveryOrderManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Backing Bean for Create Purchase Order
 * 
 * @author Giribabu
 * @since 7/10/2013
 */
@ManagedBean(name = "createDOBackingBean")
@RequestScoped
public class DeliveryOrderBackingBean extends BaseBackingBean<DeliveryOrderManagedBean> {
	private static Logger log = Logger.getLogger(DeliveryOrderBackingBean.class);
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public DeliveryOrderBackingBean(){
		if (getManagedBean().getFlDeliveryOrder() == null) {
			getManagedBean().setFlDeliveryOrder(new FlDeliveryOrder());
			getManagedBean().getFlDeliveryOrder().setFlDelOrderItem(new ArrayList<FlDelOrderItem>());
		}
		if (getManagedBean().getFulfilmentOrder() == null) {
			getManagedBean().setFulfilmentRequest(new FlFulfilmentRequest());
			getManagedBean().setFulfilmentOrder(new FlFulfilmentOrder() );
			getManagedBean().setDeliveryAddress(new FlDeliveryAddress());
		}
	}

	public String createDeliveryOrder(String action) {
		String token = getFacesScopes().getBPMAuthenticationCtxToken();
		IDeliveryOrderSO deliveryOrderSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			String actionName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("actionName");
			Long flOrderId;
			if(actionName != null && actionName != ""){
				flOrderId = this.getManagedBean().getFlDeliveryOrder().getFlFulfilmentOrder().getFulfilmentOrderId();
			}else{
				flOrderId = getManagedBean().getFulfilmentOrder().getFulfilmentOrderId();
			}
			
			Long fulfilmentOrderId = Long.valueOf(flOrderId);
			log.info("fulfilmentOrderId::: "+fulfilmentOrderId);
			String  statusId = this.getManagedBean().getStatusId();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlDeliveryOrder flDeliveryOrder = getManagedBean().getFlDeliveryOrder();
			deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IDeliveryOrderSO.class);
			if(getManagedBean().getDocType().equalsIgnoreCase("CR")){
				System.out.println("--CR calculateDelAmt--");
				calculateDelAmt(flDeliveryOrder);
			}
			
			reqDTO.setFulfilmentOrderId(fulfilmentOrderId);
			reqDTO.setStatusId(statusId);
			reqDTO.setFlDeliveryOrder(flDeliveryOrder);
			reqDTO.setFulfilmentOrder(getManagedBean().getFulfilmentOrder());
			reqDTO.setOutcome(action);
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			reqDTO.setToken(token);
			FulfilmentResDTO resDTO = deliveryOrderSO.saveDeliveryOrder(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "Delivery Order Id. : "+flDeliveryOrder.getDeliveryOrderId();
				if(flDeliveryOrder.getDeliveryOrderNo()!=null) {
					message = message + ". Delivery Order. : "+flDeliveryOrder.getDeliveryOrderNo();
				}
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
			deliveryOrderSO = null;
		}
		return null;
	}
	public void stopDelivery() {
		
		Long flOrderId;
		flOrderId=getManagedBean().getFulfilmentOrder().getFulfilmentOrderId();
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		//Long orderId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("orderId"));
		IFulfilmentOrderSO flOrderSO = BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentOrderSO.class);
		FulfilmentReqDTO flDTO = new FulfilmentReqDTO();
		flDTO.setFlOrderId(flOrderId);
		getManagedBean().setFulfilmentOrder(flOrderSO.retrieveFulfilmentOrder(flDTO).getFlOrder());
		reqDTO.setFulfilmentOrder(getManagedBean().getFulfilmentOrder());
		flOrderSO.stopDeliveryWSDL(reqDTO);
		
	}
	
	public void updateDeliveryAddrText(){
		if(getManagedBean().getSelectedDelAddr().getAddressType() != null){
			getManagedBean().setSelectedLovDelAddr(getManagedBean().getSelectedDelAddr().getAddressType().getPmAddress().getAddress1()
					+" "+getManagedBean().getSelectedDelAddr().getAddressType().getPmAddress().getAddress2()
					+" "+getManagedBean().getSelectedDelAddr().getAddressType().getPmAddress().getAddress3()
					+" "+getManagedBean().getSelectedDelAddr().getAddressType().getPmAddress().getAddress4());
		}else{
			getManagedBean().setSelectedLovDelAddr(getManagedBean().getSelectedDelAddr().getPmAddress().getAddress1()
					+" "+getManagedBean().getSelectedDelAddr().getPmAddress().getAddress2()
					+" "+getManagedBean().getSelectedDelAddr().getPmAddress().getAddress3()
					+" "+getManagedBean().getSelectedDelAddr().getPmAddress().getAddress4());
		}
		
		
		
		
		if(!getManagedBean().getFlDeliveryOrder().getFlDelOrderItem().isEmpty()){
			getManagedBean().getFlDeliveryOrder().getFlDelOrderItem().clear();
		}
		getManagedBean().getFlDeliveryOrder().setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
		
		for(FlFulfilmentItemAddr itemAddr : getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()){
			FlDelOrderItem orderItem = new FlDelOrderItem();
			orderItem.setFlDeliveryOrder(getManagedBean().getFlDeliveryOrder());
			orderItem.setFlFulfilmentItemAddr(itemAddr);
			getManagedBean().getFlDeliveryOrder().getFlDelOrderItem().add(orderItem);
			//flFulfilmentItemAddr.flFulfilmentItem.scRequestNoteDtl.scRequestSupplierItem.requestNoteItem.itemType
						
			if(itemAddr.getFlFulfilmentItem().getScRequestNoteDtl() != null ){
				itemAddr.getFlFulfilmentItem().getScRequestNoteDtl().getScRequestSupplierItem().getRequestNoteItem().getItemType();
				for(FlDelOrderItem item:getManagedBean().getFlDeliveryOrder().getFlDelOrderItem()){
					if(null==item.getDeliveredAmt()){
						item.setDeliveredAmt(itemAddr.getOrderedAmt());
					}
					if("S".equalsIgnoreCase(itemAddr.getFlFulfilmentItem().getScRequestNoteDtl().getScRequestSupplierItem().getRequestNoteItem().getItemType()) && itemAddr.getOrderedQty().intValue() ==1){
						item.setDeliveredQty(new BigDecimal(1L));
					}
				}
			}
		}
		
	}
	
	public void delAddrList(){
		IFulfilmentOrderSO flOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl", IFulfilmentOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setFlRequestId(getManagedBean().getFulfilmentOrder().getFlFulfilmentRequest().getFulfilmentReqId());
		FulfilmentResDTO resDTO = flOrderSO.getDeliveryAddressListById(reqDTO);
		this.getManagedBean().setDeliveryAddressList(resDTO.getDelAddrList());
	}
	public void retrievedDelAmount(FlDelOrderItem flDelOrderItem){
		int unitPrice = 0;
		int delQnty = 0;
		int delAmoun = 0;
		if(null != flDelOrderItem.getFlFulfilmentItemAddr().getUnitPrice())
			unitPrice= flDelOrderItem.getFlFulfilmentItemAddr().getUnitPrice().intValue();
		if(null != flDelOrderItem.getDeliveredQty())
			delQnty = flDelOrderItem.getDeliveredQty().intValue();
		delAmoun =((unitPrice) * (delQnty));
		BigDecimal delAmountTotal = new BigDecimal("0");
		delAmountTotal=new BigDecimal(delAmoun);
		flDelOrderItem.setDeliveredAmt(delAmountTotal);
	}
	
	
	public void calculateDelAmt(FlDeliveryOrder delOrder){
		for(FlDelOrderItem orderItem : delOrder.getFlDelOrderItem()){
			orderItem.setDeliveredAmt(new BigDecimal(orderItem.getFlFulfilmentItemAddr().getUnitPrice().intValue() * orderItem.getDeliveredQty().intValue()));
		}
	}
	
	

}
