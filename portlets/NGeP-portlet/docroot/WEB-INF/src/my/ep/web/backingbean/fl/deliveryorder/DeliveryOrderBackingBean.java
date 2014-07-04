/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.deliveryorder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNoteItem;
import my.ep.persistence.entity.fl.order.FlDelOrderItem;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.order.FlTempDoAddr;
import my.ep.persistence.entity.fl.order.FlTempDoItem;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.creatdo.DeliveryOrderManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

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

	private String getUserRole() {		
		if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) 
			return RoleCode.MOF_SUPPLIER_ADMIN.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value()))
			return RoleCode.MOF_SUPPLIER_USER.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()))
			return RoleCode.BASIC_SUPPLIER_ADMIN.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()))
			return RoleCode.BASIC_SUPPLIER_USER.value();
		else 
			return null;
	}
	
	public String createDeliveryOrder(String action) {
		FacesScopes facesScopes = new FacesScopes();
		String token = getFacesScopes().getBPMAuthenticationCtxToken();
		IDeliveryOrderSO deliveryOrderSO = null;
		try {
			String actionName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("actionName");
			Long flOrderId;
			if(actionName != null && actionName != ""){
				flOrderId = this.getManagedBean().getFlDeliveryOrder().getFlFulfilmentOrder().getFulfilmentOrderId();
			}else{
				flOrderId = getManagedBean().getFulfilmentOrder().getFulfilmentOrderId();
			}
			
			// do validation for selected address have items with delmount and delQty or not....
			if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
				checkItemsWithDoAddress(actionName);
			}
			
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
				for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
					if(flDeliveryAddress.getFlTempDoAddr().isSelected()) {
						FlDeliveryOrder flDeliveryOrder = new FlDeliveryOrder();
						flDeliveryOrder.setSupplierDoRef(flDeliveryAddress.getFlTempDoAddr().getSupplierDoRef());
						flDeliveryOrder.setEstStartDate(new Timestamp(System.currentTimeMillis()));
						flDeliveryOrder.setFlRequest(getManagedBean().getFulfilmentRequest());
						flDeliveryOrder.setFlFulfilmentOrder(getManagedBean().getFulfilmentOrder());
						flDeliveryOrder.setFlDeliveryAddress(flDeliveryAddress);
						flDeliveryOrder.setRemarkList(flDeliveryAddress.getRemarkList());
						flDeliveryOrder.setAttachmentList(flDeliveryAddress.getAttachmentList());
						
						//FlDelOrderItem
						ArrayList<FlDelOrderItem> delOrderList= new ArrayList<FlDelOrderItem>();
						for(FlFulfilmentItemAddr itemAddress : flDeliveryAddress.getFlFulfilmentItemAddr()){
							FlDelOrderItem flDelOrderItem = new FlDelOrderItem();
							flDelOrderItem.setFlDeliveryOrder(flDeliveryOrder);
							flDelOrderItem.setFlFulfilmentItemAddr(itemAddress);
							flDelOrderItem.setFlItem(itemAddress.getFlFulfilmentItem());
							flDelOrderItem.setDeliveredQty(itemAddress.getDeliveredQty());
							flDelOrderItem.setDeliveredAmt(itemAddress.getDeliveredAmt());
							delOrderList.add(flDelOrderItem);
						}
						flDeliveryOrder.setFlDelOrderItem(delOrderList);
						reqDTO.getDeliveryOrdersList().add(flDeliveryOrder);
					}
				}
			} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
				for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
					flDeliveryAddress.getFlTempDoAddr().setFlDeliveryAddress(flDeliveryAddress);
					for(FlFulfilmentItemAddr itemAddress : flDeliveryAddress.getFlFulfilmentItemAddr()){
						itemAddress.getFlTempDoItem().setFlFulfilmentItemAddr(itemAddress);
						itemAddress.getFlTempDoItem().setDeliveryQty(itemAddress.getDeliveredQty());
						itemAddress.getFlTempDoItem().setDeliveryAmt(itemAddress.getDeliveredAmt());
					}
				}
			}
			
			deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IDeliveryOrderSO.class);
			
			reqDTO.setFulfilmentOrderId(Long.valueOf(flOrderId));
			reqDTO.setFulfilmentOrder(getManagedBean().getFulfilmentOrder());
			reqDTO.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
			reqDTO.setOutcome(action);
			reqDTO.setToken(token);
			reqDTO.setUserRole(getUserRole());
			if("UPDATE".equalsIgnoreCase(action)){
				reqDTO.setPmUser(getManagedBean().getPmUser());
				if(null == getManagedBean().getSupplierDoRef()){
					//TODO
					// do valudation
				}else{
					getManagedBean().getFlDeliveryOrder().setSupplierDoRef(getManagedBean().getSupplierDoRef());
				}
				reqDTO.setFlDeliveryOrder(getManagedBean().getFlDeliveryOrder());
				resDTO = deliveryOrderSO.deliveryOrderModification(reqDTO);
			}
			else{
				reqDTO.setPmUser(facesScopes.getUserSessionBean());
				resDTO = deliveryOrderSO.saveDeliveryOrder(reqDTO);
			}
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700043;
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700004;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,	null,
							MessageUtils.getMessage(messageId, 
									new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_DELIVERY_ORDER), getManagedBean().getFulfilmentOrder().getDocNo()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, 
									new FacesScopes().getUserLocale())));
				} else {
					messageId = MessageConst.INFO_700043;
					String message = "";
					for(String deliveryOrderNo : resDTO.getDeliveryOrderNoList()) {
						message = MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_DELIVERY_ORDER), deliveryOrderNo}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale());
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,	null, message));
					}
					
				}
			}
			if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			} else {
				setDeliveryOrder(resDTO);
			}
		} finally {
			deliveryOrderSO = null;
		}
		return null;
	}
	
	public void setDeliveryOrder(FulfilmentResDTO resDTO) {
		FlFulfilmentOrder flOrder = resDTO.getFlOrder();
		
		// initialize FlTempDoAddr if null
		for(FlDeliveryAddress deliveryAddress : flOrder.getFlFulfilmentRequest().getFlDeliveryAddresses()) {
			if(deliveryAddress.getFlTempDoAddr()==null) {
				deliveryAddress.setFlTempDoAddr(new FlTempDoAddr());
			}
			for(FlFulfilmentItemAddr itemAddr : deliveryAddress.getFlFulfilmentItemAddr()) {
				if(itemAddr.getFlTempDoItem()==null) {
					itemAddr.setFlTempDoItem(new FlTempDoItem());
				} else {
					itemAddr.setDeliveredQty(itemAddr.getFlTempDoItem().getDeliveryQty());
					itemAddr.setDeliveredAmt(itemAddr.getFlTempDoItem().getDeliveryAmt());
				}
			}
		}
		
		getManagedBean().setSelectedTabIndex(0);
		getManagedBean().setFulfilmentRequest(flOrder.getFlFulfilmentRequest());
		getManagedBean().setFulfilmentOrder(flOrder);
		getManagedBean().setDocType(flOrder.getFlFulfilmentRequest().getDocType());
	}
	
	// this method do validations for items with Delivery Address,Delivery order and Delivery Amount
	private void checkItemsWithDoAddress(String actionName) {
		boolean anyAddressSelected = false;
		for(FlDeliveryAddress selectedDelAddress  :getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			if(selectedDelAddress.getFlTempDoAddr().isSelected()) {
				anyAddressSelected = true;
				break;
			}
		}
		
		double totalDeliveryQty = 0;
		double totalDeliveryAmount = 0;
		if(!anyAddressSelected){
			throw new EpException(MessageConst.ERROR_100050, new String[]{"Delivery Address"});
		}else{
			if(!"UPDATE".equalsIgnoreCase(actionName)){
				for(FlDeliveryAddress selectedDelAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
					if(selectedDelAddress.getFlTempDoAddr().isSelected()) {
						if(selectedDelAddress.getFlTempDoAddr().getSupplierDoRef()==null || selectedDelAddress.getFlTempDoAddr().getSupplierDoRef().length()==0) {
							throw new EpException(MessageConst.ERROR_100051, new String[]{"Supplier DO. No.","Delivery Address "+getFullAddress(selectedDelAddress.getPmAddress())});
						}
						totalDeliveryQty = 0;
						totalDeliveryAmount = 0;
						for(FlFulfilmentItemAddr itemAddress: selectedDelAddress.getFlFulfilmentItemAddr()){
							totalDeliveryQty = totalDeliveryQty + itemAddress.getDeliveredQty().doubleValue();
							totalDeliveryAmount = totalDeliveryAmount + itemAddress.getDeliveredAmt().doubleValue();
						}
						if(totalDeliveryQty==0) {
							throw new EpException(MessageConst.ERROR_740008, new String[]{"Total Delivery Quantity",getFullAddress(selectedDelAddress.getPmAddress())});
						}
						if(totalDeliveryAmount==0) {
							throw new EpException(MessageConst.ERROR_740008, new String[]{"Total Delivery Amount",getFullAddress(selectedDelAddress.getPmAddress())});
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
	
	public void calculateDelAmt(FlDeliveryOrder delOrder){
		for(FlDelOrderItem orderItem : delOrder.getFlDelOrderItem()){
			orderItem.setDeliveredAmt(new BigDecimal(orderItem.getFlFulfilmentItemAddr().getUnitPrice().intValue() * orderItem.getDeliveredQty().intValue()));
		}
	}
	
	public void selectItemEvent() {
		getManagedBean().setSelectedItemRowCalList(new ArrayList<FlFulfilmentItemAddr>());
		FlFulfilmentItemAddr selectedAddressItem=new FlFulfilmentItemAddr();
		selectedAddressItem.setOrderedAmt(getManagedBean().getSelectedItemRow().getOrderedAmt());
		getManagedBean().getSelectedItemRowCalList().add(selectedAddressItem);
		getManagedBean().getSelectedItemRowCalList().size();
		getManagedBean().setItemSelectedForCal("S");
		
	}
	public void retrieveDelAmount(FlFulfilmentItemAddr itemAddress){
		
		BigDecimal unitPriceCal=new BigDecimal(0.0);
		BigDecimal delQtyCal=new BigDecimal(0.0);
		BigDecimal delAmtCal=new BigDecimal(0.0);
		
		unitPriceCal=itemAddress.getUnitPrice();
		delQtyCal= itemAddress.getDeliveredQty();
		
		if(delQtyCal.add(itemAddress.getReceivedQty()).doubleValue()>itemAddress.getOrderedQty().doubleValue()) {
			itemAddress.setDeliveredQty(new BigDecimal(0));
			throw new EpException(MessageConst.ERROR_100032, new String[]{"Delivery Quantity","Remaining Quantity"});
		}
		
		delAmtCal = unitPriceCal.multiply(delQtyCal);
		itemAddress.setDeliveredAmt(delAmtCal);
	}
	
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String orderId = originalServletRequest.getParameter("orderId");
		System.out.println("orderId :" + orderId);
		if(orderId != null){
			IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setFlOrderId(Long.parseLong(orderId));
			FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(reqDTO);
			setDeliveryOrder(resDTO);
		}
		getManagedBean().getFulfilmentRequest();
	}

	public void retrieveRejectionData(FlFulfilmentItemAddr flFulfilmentItemAddr) {
		getManagedBean().setRejectionList(new ArrayList<FlFulfilmentNoteItem>());
		for(FlDelOrderItem delOrderItem : flFulfilmentItemAddr.getFlDelOrderItems()) {
			for(FlFulfilmentNoteItem frnItem : delOrderItem.getFlFulfilmentNoteItem()) {
				if(frnItem.getRejectedQty().doubleValue()>0 || frnItem.getRejectedAmt().doubleValue()>0) {
					getManagedBean().getRejectionList().add(frnItem);
				}
			}
		}
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
