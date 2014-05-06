/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.deliveryorder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.fl.FlAttachment;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

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
	private FlFulfilmentItemAddr amountSectionItemRow;
	
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
		if(null == getManagedBean().getSelectedItemRowCalList()){
			getManagedBean().setSelectedItemRowCalList(new ArrayList<FlFulfilmentItemAddr>() );
			
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
			
			// do validation for selected address have items with delmount and delQty or not....
			checkItemsWithDoAddress();
			
			Long fulfilmentOrderId = Long.valueOf(flOrderId);
			log.info("fulfilmentOrderId::: "+fulfilmentOrderId);
			String  statusId = this.getManagedBean().getStatusId();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			
			for(FlDeliveryAddress flDeliveryAddress:getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
				/*flDeliveryAddress.getAttachmentList().size();
				if(!flDeliveryAddress.getAttachmentList().isEmpty()){
					flDeliveryAddress.getAttachmentList().get(0).getFileName();
				}*/
				FlDeliveryOrder flDeliveryOrder = new FlDeliveryOrder();
				flDeliveryOrder.setSupplierDoRef("SM123");
				flDeliveryOrder.setEstStartDate(new Timestamp(System.currentTimeMillis()));
				flDeliveryOrder.setFlRequest(getManagedBean().getFulfilmentRequest());
				flDeliveryOrder.setFlDeliveryAddress(flDeliveryAddress);
				//FlDelOrderItem
				ArrayList<FlDelOrderItem> delOrderList= new ArrayList<FlDelOrderItem>();
				for(FlFulfilmentItemAddr itemAddress:flDeliveryAddress.getFlFulfilmentItemAddr()){
					FlDelOrderItem flDelOrderItem= new FlDelOrderItem();
					flDelOrderItem.setDeliveredAmt(itemAddress.getDeliveredAmt());
					flDelOrderItem.setDeliveredQty(itemAddress.getDeliveredQty());
					flDelOrderItem.setFlDeliveryOrder(flDeliveryOrder);
					flDelOrderItem.setFlFulfilmentItemAddr(itemAddress);
					flDelOrderItem.setFlItem(itemAddress.getFlFulfilmentItem());
					delOrderList.add(flDelOrderItem);
					
				}
				flDeliveryOrder.setFlDelOrderItem(delOrderList);
				reqDTO.getDeliveryOrdersList().add(flDeliveryOrder);
				
				reqDTO.setFlAttachmentList(flDeliveryAddress.getAttachmentList());
				if(!flDeliveryAddress.getRemarkList().isEmpty()){
					reqDTO.setFlRemark(flDeliveryAddress.getRemarkList().get(0));
				}
				
				
			}
			
			deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IDeliveryOrderSO.class);
			/*if(getManagedBean().getDocType().equalsIgnoreCase("CR")){
				System.out.println("--CR calculateDelAmt--");
				calculateDelAmt(flDeliveryOrder);
			}*/
			
			reqDTO.setFulfilmentOrderId(fulfilmentOrderId);
			reqDTO.setStatusId(statusId);
			//reqDTO.setFlDeliveryOrder(flDeliveryOrder);
			reqDTO.setFulfilmentOrder(getManagedBean().getFulfilmentOrder());
			reqDTO.setOutcome(action);
			reqDTO.setToken(token);
			
			FulfilmentResDTO resDTO = deliveryOrderSO.saveDeliveryOrder(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				/*String message = "Delivery Order Id. : "+flDeliveryOrder.getDeliveryOrderId();
				if(flDeliveryOrder.getDeliveryOrderNo()!=null) {
					message = message + ". Delivery Order. : "+flDeliveryOrder.getDeliveryOrderNo();
				}*/
				String message="DeliveryOrder";
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
	// this method do validations for items with Delivery Address,Delivery order and Delivery Amount
	private void checkItemsWithDoAddress() {
		// TODO Auto-generated method stub
		getManagedBean().getSelectedDelAddressList().size();
		if(getManagedBean().getSelectedDelAddressList().size()==0){
			throw new EpException(" Select Deliver Address .");
			//System.out.println("Please select Del Address");
		}else{
			for(FlDeliveryAddress selectedDelAddress  :getManagedBean().getSelectedDelAddressList()){
				for(FlFulfilmentItemAddr itemAddress: selectedDelAddress.getFlFulfilmentItemAddr()){
					System.out.println("Item Name is ::: "+itemAddress.getScRequestItem().getItemName());
					itemAddress.getDeliveredQty();
					itemAddress.getDeliveredAmt();
					if(null==itemAddress.getDeliveredAmt()){
						throw new EpException(" Del Qty and Del Amout is Required .");
					}
				}
			}
		}
		
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
	
	public void updateDeliveryAddr(){
		getManagedBean().getFlDeliveryOrder().setFlDeliveryAddress(getManagedBean().getSelectedAddrRow());
		for(FlFulfilmentItemAddr itemAddr : getManagedBean().getSelectedAddrRow().getFlFulfilmentItemAddr()){
			//Doc Condition chck
			FlDelOrderItem orderItem = new FlDelOrderItem();
				orderItem.setFlDeliveryOrder(getManagedBean().getFlDeliveryOrder());
				orderItem.setFlFulfilmentItemAddr(itemAddr);
				getManagedBean().getFlDeliveryOrder().getFlDelOrderItem().add(orderItem);
				//flFulfilmentItemAddr.flFulfilmentItem.scRequestNoteDtl.scRequestSupplierItem.requestNoteItem.itemType
							
				if(itemAddr.getFlFulfilmentItem().getScRequestNoteDtl() != null ){
					itemAddr.getFlFulfilmentItem().getScRequestNoteDtl().getScRequestSupplierItem().getRequestNoteItem().getItemType();
					for(FlDelOrderItem item:getManagedBean().getFlDeliveryOrder().getFlDelOrderItem()){
						/*if(null==item.getDeliveredAmt()){
							item.setDeliveredAmt(itemAddr.getOrderedAmt());
						}*/
						/*if("S".equalsIgnoreCase(itemAddr.getFlFulfilmentItem().getScRequestNoteDtl().getScRequestSupplierItem().getRequestNoteItem().getItemType()) && itemAddr.getOrderedQty().intValue() ==1){
							item.setDeliveredQty(new BigDecimal(1L));
						}*/
					}
				}
			
		}
		
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
			//Doc Condition chck
			FlDelOrderItem orderItem = new FlDelOrderItem();
			
			if(!itemAddr.getOrderedQty().equals(itemAddr.getReceivedQty())){
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
		
	}
	
	public void delAddrList(){
		IFulfilmentOrderSO flOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl", IFulfilmentOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setFlRequestId(getManagedBean().getFulfilmentOrder().getFlFulfilmentRequest().getFulfilmentReqId());
		FulfilmentResDTO resDTO = flOrderSO.getDeliveryAddressListById(reqDTO);
		this.getManagedBean().setDeliveryAddressList(resDTO.getDelAddrList());
	}
	
	/*public void retrievedDelAmount(FlFulfilmentItemAddr flFulfilmentItemAddr){
		int unitPrice = 0;
		int delQnty = 0;
		int delAmoun = 0;
		if(null != flFulfilmentItemAddr.getUnitPrice())
			unitPrice= flFulfilmentItemAddr.getUnitPrice().intValue();
		if(null != flFulfilmentItemAddr.getDeliveredQty())
			delQnty = flFulfilmentItemAddr.getDeliveredQty().intValue();
		delAmoun =((unitPrice) * (delQnty));
		BigDecimal delAmountTotal = new BigDecimal("0");
		delAmountTotal=new BigDecimal(delAmoun);
		flFulfilmentItemAddr.setDeliveredAmt(delAmountTotal);
		
		
	}*/
	
	public void calculateDelAmt(FlDeliveryOrder delOrder){
		for(FlDelOrderItem orderItem : delOrder.getFlDelOrderItem()){
			orderItem.setDeliveredAmt(new BigDecimal(orderItem.getFlFulfilmentItemAddr().getUnitPrice().intValue() * orderItem.getDeliveredQty().intValue()));
		}
	}
	public void onAddressSelect(){
		System.out.println("in on address select ");
		getManagedBean().getSelectedDelAddressList().size();
		
	}
	
	public void selectItemEvent() {
		System.out.println("order amount:::: "+getManagedBean().getSelectedItemRow().getOrderedAmt());
		getManagedBean().setSelectedItemRowCalList(new ArrayList<FlFulfilmentItemAddr>());
		FlFulfilmentItemAddr selectedAddressItem=new FlFulfilmentItemAddr();
		selectedAddressItem.setOrderedAmt(getManagedBean().getSelectedItemRow().getOrderedAmt());
		getManagedBean().getSelectedItemRowCalList().add(selectedAddressItem);
		getManagedBean().getSelectedItemRowCalList().size();
		getManagedBean().setItemSelectedForCal("S");
		/*for(FlFulfilmentItemAddr selectedAddressItem:getManagedBean().getSelectedItemRowList()){
			selectedAddressItem.getOrderedAmt();
			getManagedBean().getSelectedItemRowCalList().add(selectedAddressItem);
		}*/
		
		/*getManagedBean().getSelectedItemRow().getOrderedAmt();
		System.out.println("order amount:::: "+getManagedBean().getSelectedItemRow().getOrderedAmt());
		if(null==getManagedBean().getSelectedItemAmountSection()){
			getManagedBean().setSelectedItemAmountSection(new FlFulfilmentItemAddr());
		}
		getManagedBean().getSelectedItemAmountSection().setOrderedAmt(getManagedBean().getSelectedItemRow().getOrderedAmt());
		*/
		
		/*if(null== getManagedBean().getSelectedAddrRow()){
			throw new EpException(" Select Deliver Address .");
		}else{
			getSelectedItemRow().setOrderedAmt(fulItemAddress.getOrderedAmt());
		}*/
		
	}
	public void retrieveDelAmount(FlFulfilmentItemAddr itemAddress){
		//System.out.println("Del Qty::: "+itemAddress.getDeliveredAmt());
		//System.out.println("Order Amount::: "+itemAddress.getOrderedAmt());
		int delQtyCal=0;
		int delAmtCal=0;
		int unitPriceCal=0;
		unitPriceCal=itemAddress.getUnitPrice().intValue();
		delQtyCal= itemAddress.getDeliveredQty().intValue();
		delAmtCal=unitPriceCal*delQtyCal;
		BigDecimal total=new BigDecimal(delAmtCal);
		itemAddress.setDeliveredAmt(total);

		//FlFulfilmentItemAddr selectedAddressItem=new FlFulfilmentItemAddr();
		//selectedAddressItem.setDeliveredAmt(total);
		//selectedAddressItem.setOrderedAmt(getManagedBean().getSelectedItemRow().getOrderedAmt());
		//getManagedBean().getSelectedItemRowCalList().get(0).setDeliveredAmt(total);
		//getManagedBean().getSelectedItemRowCalList().add(selectedAddressItem);
		//getManagedBean().getSelectedItemRowCalList().size();
		//getManagedBean().setItemSelectedForCal("S");
		
		
	}
	



	/**
	 * @return the amountSectionItemRow
	 */
	public FlFulfilmentItemAddr getAmountSectionItemRow() {
		return amountSectionItemRow;
	}

	/**
	 * @param amountSectionItemRow the amountSectionItemRow to set
	 */
	public void setAmountSectionItemRow(FlFulfilmentItemAddr amountSectionItemRow) {
		this.amountSectionItemRow = amountSectionItemRow;
	}

}
