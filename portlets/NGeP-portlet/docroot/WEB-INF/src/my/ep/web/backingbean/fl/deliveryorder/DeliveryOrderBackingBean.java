/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.deliveryorder;

import java.text.ParseException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.fl.creatdo.DeliveryOrderManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

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
	
	public DeliveryOrderBackingBean(){
		if(MDC.get(LogKey.USER_ID)==null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
	}
	@PostConstruct
	public void init() {
		/*if (getManagedBean().getFlDeliveryOrder() == null) {
			getManagedBean().setFlDeliveryOrder(new FlDeliveryOrder());
			
		}*/
		if (getManagedBean().getFulfilmentOrder() == null) {
			getManagedBean().setFulfilmentRequest(new FlFulfilmentRequest());
			//getManagedBean().getFulfilmentRequest()
			getManagedBean().setFulfilmentOrder(new FlFulfilmentOrder() );
			//getManagedBean().setFlDeliveryOrder(new FlDeliveryOrder());
			getManagedBean().setDeliveryAddress(new FlDeliveryAddress());
		}
	}

	public void createDeliveryOrder(String action) throws ParseException {
		IDeliveryOrderSO deliveryOrderSO = null;
		try {
			String actionName=FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("actionName");
			Long flOrderId;
			if(actionName!=null && actionName!=""){
				flOrderId=this.getManagedBean().getFlDeliveryOrder().getFlFulfilmentOrder().getFulfilmentOrderId();
			}else{
				flOrderId=getManagedBean().getFulfilmentOrder().getFulfilmentOrderId();
			}
			
			Long fulfilmentOrderId=Long.valueOf(flOrderId);
			//Long flRequestId=Long.valueOf(getManagedBean().getFulfilmentOrder().getFlFulfilmentRequest().getFulfilmentReqId());
			String  statusId=this.getManagedBean().getStatusId();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			
			FlRemark flRemark = getManagedBean().getFlRemark();
			flRemark.setDocId(Long.valueOf(1));
			flRemark.setDocType("tex");
			
			FlAttachment flAttachment = this.getManagedBean().getFlAttachment();
			String fileName = this.getManagedBean().getFlAttachment().getFileName();
			String fileDesc = this.getManagedBean().getFlAttachment().getFileDescription();
			String filePath = this.getManagedBean().getFlAttachment().getFilePath();
			flAttachment.setDocType("txt");
			flAttachment.setDocId(Long.valueOf(44));
			flAttachment.setFileName(fileName);
			flAttachment.setFileDescription(fileDesc);
			flAttachment.setFilePath(filePath);
			
			FlDeliveryOrder flDeliveryOrder = getManagedBean().getFlDeliveryOrder();
			deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IDeliveryOrderSO.class);
			//reqDTO.setFlRemark(flRemark);
			//reqDTO.setFlAttachment(flAttachment);
			reqDTO.setFulfilmentOrderId(fulfilmentOrderId);
			reqDTO.setStatusId(statusId);
			//reqDTO.setFlRequestId(flRequestId);
			reqDTO.setFlDeliveryOrder(flDeliveryOrder);
			reqDTO.setFulfilmentOrder(getManagedBean().getFulfilmentOrder());
			//reqDTO.setContractRequest(false);
			//reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			
			//reqDTO.setProcessId("Order_Fullfillment.CreatePurchaseOrder");
			FulfilmentResDTO resDTO = deliveryOrderSO.saveDeliveryOrder(reqDTO);
			//System.out.println("resDTO.isSuccessful() in backing bean:: "+ resDTO.isSuccessful());
		} catch (Exception e) {
			log.error("Exception>>", e);
		} finally {
			deliveryOrderSO = null;
		}
	}
	
	

	
	
	
}
