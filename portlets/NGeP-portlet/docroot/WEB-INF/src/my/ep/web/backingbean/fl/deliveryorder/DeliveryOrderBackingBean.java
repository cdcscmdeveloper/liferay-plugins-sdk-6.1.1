/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.deliveryorder;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.creatdo.DeliveryOrderManagedBean;
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
	
	public DeliveryOrderBackingBean(){
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
			//getManagedBean().setFlDeliveryOrder(new FlDeliveryOrder());
			getManagedBean().setDeliveryAddress(new FlDeliveryAddress());
		}
	}

	public String createDeliveryOrder(String action) {
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
			System.out.println("fulfilmentOrderId::: "+fulfilmentOrderId);
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
	
	

	
	
	
}
