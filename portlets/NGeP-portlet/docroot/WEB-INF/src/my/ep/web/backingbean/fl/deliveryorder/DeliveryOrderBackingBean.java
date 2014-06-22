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
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.fl.order.FlDelOrderItem;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.type.pm.AddressType;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
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
			checkItemsWithDoAddress(actionName);
			
			Long fulfilmentOrderId = Long.valueOf(flOrderId);
			log.info("fulfilmentOrderId::: "+fulfilmentOrderId);
			String  statusId = this.getManagedBean().getStatusId();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			
			for(FlDeliveryAddress flDeliveryAddress:getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
				/*flDeliveryAddress.getAttachmentList().size();
				if(!flDeliveryAddress.getAttachmentList().isEmpty()){
					flDeliveryAddress.getAttachmentList().get(0).getFileName();
				}*/
				FlDeliveryOrder flDeliveryOrder = new FlDeliveryOrder();
				if(null == getManagedBean().getSupplierDoRef()){
					//TODO
					// do valudation
				}else{
					flDeliveryOrder.setSupplierDoRef(getManagedBean().getSupplierDoRef());
				}
				
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
				resDTO = deliveryOrderSO.saveDeliveryOrder(reqDTO);
			}
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700043;
				/*
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
					messageId = MessageConst.INFO_700043;
				} else if(action.equalsIgnoreCase("UPDATE")) {
					messageId = MessageConst.INFO_700047;
				} */
				
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, 
						null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_REQUEST), getManagedBean().getFulfilmentOrder().getDocNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, 
								new FacesScopes().getUserLocale())));
			}
			if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}	
			else {
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
	private void checkItemsWithDoAddress(String actionName) {
		// TODO Auto-generated method stub
		getManagedBean().getSelectedDelAddressList().size();
		if(getManagedBean().getSelectedDelAddressList().size()==0){
			throw new EpException(" Select Deliver Address .");
		}else{
			if(!"UPDATE".equalsIgnoreCase(actionName)){
				for(FlDeliveryAddress selectedDelAddress  :getManagedBean().getSelectedDelAddressList()){
					for(FlFulfilmentItemAddr itemAddress: selectedDelAddress.getFlFulfilmentItemAddr()){
						System.out.println("Item Name is ::: "+itemAddress.getScRequestItem().getItemType());
						System.out.println("Doc type is ::: "+getManagedBean().getDocType());
						itemAddress.getDeliveredQty();
						itemAddress.getDeliveredAmt();
						if(null==itemAddress.getDeliveredAmt()){
							throw new EpException(" Del Qty and Del Amout is Required .");
						}
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
	public void onAddressSelect(){
		getManagedBean().getSelectedDelAddressList().size();
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
			IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setFlOrderId(Long.parseLong(orderId));
			FlFulfilmentOrder flOrder = orderSO.retrieveFulfilmentOrderById(reqDTO).getFlOrder();
			
			getManagedBean().setFulfilmentRequest(flOrder.getFlFulfilmentRequest());
			getManagedBean().setFulfilmentOrder(flOrder);
			getManagedBean().setDocType(flOrder.getFlFulfilmentRequest().getDocType());
			
			if(getManagedBean().getFulfilmentOrder().getPmFinancialBranch()!=null) {
				AddressReqDTO addReqDTO = new AddressReqDTO();
				addReqDTO.setFinancialBranchId(getManagedBean().getFulfilmentOrder().getPmFinancialBranch().getFinancialBranchId());
				addReqDTO.setAddressType(AddressType.BILLING_ADDRESS.toString());
				
				AddressResDTO resDTO = addressSO.retrieveAddressTypeList(addReqDTO);
				if(!resDTO.getAddressTypeList().isEmpty()){
					getManagedBean().setFactoringAddress(getFullAddress(resDTO.getAddressTypeList().get(0).getPmAddress()));
					
					getManagedBean().setPhoneNo(resDTO.getAddressTypeList().get(0).getPmAddress().getPhoneNo());
					getManagedBean().setFaxNo(resDTO.getAddressTypeList().get(0).getPmAddress().getFaxNo());
					if(resDTO.getAddressTypeList().get(0).getPmContacts() != null ){
						PmContact pmContact = resDTO.getAddressTypeList().get(0).getPmContacts().get(0);
						getManagedBean().setContactPerson(pmContact.getContactName());
						getManagedBean().setContactEmail(pmContact.getEmail());
						getManagedBean().setContactPhoneNo(pmContact.getPhoneNo());
					}
				}
			} else { //reset back to HQ info
				AddressReqDTO addReqDTO = new AddressReqDTO();
				addReqDTO.setFinancialOrgId(getManagedBean().getFulfilmentOrder().getPmFinancialOrg().getFinancialOrgId());
				addReqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
				
				AddressResDTO resDTO = addressSO.retrieveAddressTypeList(addReqDTO);
				if(!resDTO.getAddressTypeList().isEmpty()){
					getManagedBean().setFactoringAddress(getFullAddress(resDTO.getAddressTypeList().get(0).getPmAddress()));
					getManagedBean().setPhoneNo(resDTO.getAddressTypeList().get(0).getPmAddress().getPhoneNo());
					getManagedBean().setFaxNo(resDTO.getAddressTypeList().get(0).getPmAddress().getFaxNo());
					if(!resDTO.getAddressTypeList().get(0).getPmContacts().isEmpty() ){
						PmContact pmContact = resDTO.getAddressTypeList().get(0).getPmContacts().get(0);
						getManagedBean().setContactPerson(pmContact.getContactName());
						getManagedBean().setContactEmail(pmContact.getEmail());
						getManagedBean().setContactPhoneNo(pmContact.getPhoneNo());
					}
				}				
			}
		}
		getManagedBean().getFulfilmentRequest();
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
