/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.deliveryorder;

import java.text.ParseException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
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
		if (getManagedBean().getFlDeliveryOrder() == null) {
			getManagedBean().setFlDeliveryOrder(new FlDeliveryOrder());
			
		}
		if (getManagedBean().getFulfilmentOrder() == null) {
			getManagedBean().setFulfilmentRequest(new FlFulfilmentRequest());
			//getManagedBean().getFulfilmentRequest()
			getManagedBean().setFulfilmentOrder(new FlFulfilmentOrder() );
			getManagedBean().setFlDeliveryOrder(new FlDeliveryOrder());
			getManagedBean().setDeliveryAddress(new FlDeliveryAddress());
		}
	}

	public void createDeliveryOrder() throws ParseException {
		IDeliveryOrderSO deliveryOrderSO = null;
		try {
			//fulfilmentOrderId
			System.out.println("3333333333333333  "+getManagedBean().getFulfilmentOrder().getFlFulfilmentRequest().getFulfilmentReqId());
			//System.out.println("fulfilmentOrderId in CreateDOBackingBean:: "+this.getManagedBean().getFulfilmentOrderId());
			Long fulfilmentOrderId=Long.valueOf(getManagedBean().getFulfilmentOrder().getFulfilmentOrderId());
			Long flRequestId=Long.valueOf(getManagedBean().getFulfilmentOrder().getFlFulfilmentRequest().getFulfilmentReqId());
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
			reqDTO.setFlRequestId(flRequestId);
			reqDTO.setFlDeliveryOrder(flDeliveryOrder);
			System.out.println("3333333333333333  "+getManagedBean().getFulfilmentOrder().getDocNo());
			reqDTO.setFulfilmentOrder(getManagedBean().getFulfilmentOrder());
			//reqDTO.setContractRequest(false);
			//reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome("SUBMIT");
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
