/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.deliveryorder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.liferay.portal.util.PortalUtil;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.fl.IFulfilmentSO;
import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.usergroup.IUserGroupSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupReqDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.uom.UomReqDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.maintenace.cancel.dofulfillment.FLDOFulfillmentCancelReqType;
import my.ep.jaxb.fl.maintenace.modification.dofulfillment.FLDOFulfillmentModificationReqType;
import my.ep.persistence.entity.fl.FlActor;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNoteItem;
import my.ep.persistence.entity.fl.order.FlDelOrderItem;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.order.FlTempDoAddr;
import my.ep.persistence.entity.fl.order.FlTempDoItem;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.common.CommonBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBaseBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.backingbean.fl.stopinstr.StopInstrBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.fl.creatdo.DeliveryOrderManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

// TODO: Auto-generated Javadoc
/**
 * Backing Bean for Create Delivery Order.
 *
 * @author Giribabu
 * @since 7/10/2013
 */
@ManagedBean(name = "createDOBackingBean")
@RequestScoped
public class DeliveryOrderBackingBean extends FulfilmentBaseBackingBean<DeliveryOrderManagedBean> {
	
	private static final String NAV_PO = "/xhtml/fl/purchaseorder/poBasePage.xhtml";
	private static final String NAV_CO = "/xhtml/fl/contractorder/coBasePage.xhtml";
	
	private static final String NAV_STOP_INSTRUCTION = "/xhtml/fl/stopInstruction/siBasePage.xhtml";
	
	private static Logger log = Logger.getLogger(DeliveryOrderBackingBean.class);
	private FlFulfilmentItemAddr amountSectionItemRow;
	
	private IPagingDataModel<PmUom> unitOfMeasurementDataModel = new UnitOfMeasurementPagingDataModel();
	
	/**
	 * Instantiates a new delivery order backing bean.
	 */
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

	/**
	 * Gets the user role.
	 *
	 * @return the user role
	 */
	private String getUserRole() {		
		if(hasSupplierRole())
			return getUserSupplierRole();
		else if(getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()))
			return RoleCode.FL_APPROVER.value();		
		else 
			throw new EpException(MessageConst.ERROR_750040);
	}
	
	/**
	 * save,submit delivery order.
	 * <p>submit - save data into database and BPM process will route to the next respective user.
	 * <p>save - save data into database.
	 * @param action the action
	 * navigate to acknowledge page.
	 */
	public String createDeliveryOrder(String action) {
		FacesScopes facesScopes = new FacesScopes();
		Object token = getFacesScopes().getBPMAuthenticationCtxToken();
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
			
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
				reValidateRetrieveDelAmt();
			}
			
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			reqDTO.setSupplier(hasSupplierRole());
			reqDTO.setPmUser(facesScopes.getUserSessionBean());
			
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
				for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
					if(flDeliveryAddress.getFlTempDoAddr().isSelected()) {
						FlDeliveryOrder flDeliveryOrder = new FlDeliveryOrder();
						flDeliveryOrder.setSupplierDoRef(flDeliveryAddress.getFlTempDoAddr().getSupplierDoRef());
						flDeliveryOrder.setEstStartDate(flDeliveryAddress.getStartDate());
						flDeliveryOrder.setEstEndDate(flDeliveryAddress.getEndDate());
						flDeliveryOrder.setFlRequest(getManagedBean().getFulfilmentRequest());
						flDeliveryOrder.setFlFulfilmentOrder(getManagedBean().getFulfilmentOrder());
						flDeliveryOrder.setFlDeliveryAddress(flDeliveryAddress);
						flDeliveryOrder.setRemarkList(flDeliveryAddress.getRemarkList());
						flDeliveryOrder.setAttachmentList(flDeliveryAddress.getAttachmentList());
						flDeliveryOrder.setEstDeliveryDate(flDeliveryAddress.getFlTempDoAddr().getEstDeliveryDate());
						flDeliveryOrder.setGstDeclaredDate(getManagedBean().getGstDeclareDate());
						flDeliveryOrder.setGstEffectiveDate(getManagedBean().getGstEffDate());
						flDeliveryOrder.setGstRegNo(getManagedBean().getGstRegNo());
						
						//FlDelOrderItem
						ArrayList<FlDelOrderItem> delOrderList= new ArrayList<FlDelOrderItem>();
						for(FlFulfilmentItemAddr itemAddress : flDeliveryAddress.getFlFulfilmentItemAddr()){
							if(itemAddress.getDeliveredQty().doubleValue()>0) {
								FlDelOrderItem flDelOrderItem = new FlDelOrderItem();
								flDelOrderItem.setFlDeliveryOrder(flDeliveryOrder);
								flDelOrderItem.setFlFulfilmentItemAddr(itemAddress);
								flDelOrderItem.setFlItem(itemAddress.getFlFulfilmentItem());
								flDelOrderItem.setDeliveredQty(itemAddress.getDeliveredQty());
								flDelOrderItem.setDeliveredAmt(itemAddress.getDeliveredAmt());
								flDelOrderItem.setGstAmount(itemAddress.getDoGstAmt());
								flDelOrderItem.setGstUnitPrice(itemAddress.getDoGstUnitPrice());
								delOrderList.add(flDelOrderItem);
							}
						}
						flDeliveryOrder.setFlDelOrderItem(delOrderList);
						reqDTO.getDeliveryOrdersList().add(flDeliveryOrder);
					}
				}
			} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
				for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
					flDeliveryAddress.getFlTempDoAddr().setFlDeliveryAddress(flDeliveryAddress);
					if(flDeliveryAddress.getFlTempDoAddr().getEstDeliveryDate() == null){
						flDeliveryAddress.getFlTempDoAddr().setEstDeliveryDate(new Date());
					}
					flDeliveryAddress.getFlTempDoAddr().setEstDeliveryDate(flDeliveryAddress.getFlTempDoAddr().getEstDeliveryDate());
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
			if(null != getManagedBean().getPmDigiSign()){
				reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			}
			if(!(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE))){
				IFulfilmentSO fulfilmentSO = (IFulfilmentSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentSO.class);
				IUserGroupSO userGroupSO = (IUserGroupSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserGroupSO.class);
			
				if(null == getManagedBean().getSupplierDoRef()){
					//TODO
					// do valudation
				}else{
					getManagedBean().getFlDeliveryOrder().setSupplierDoRef(getManagedBean().getSupplierDoRef());
				}
				if(null != getManagedBean().getTaskId()){
					reqDTO.setTaskId(getManagedBean().getTaskId());
				}
				
				reqDTO.setFlDeliveryOrder(getManagedBean().getFlDeliveryOrder());
				reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
				if(flBB.getManagedBean().getRemarkList().size() >0){
					reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
				}
				BigDecimal sumItemAmt = fulfilmentSO.getSumTotalItemsAmtByReqId(getManagedBean().getFulfilmentRequest().getFulfilmentReqId());
				UserGroupReqDTO userGroupReqDTO = new UserGroupReqDTO();
				userGroupReqDTO.setGroupId(getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId());
				userGroupReqDTO.setApprovalLimit(sumItemAmt);
				UserGroupResDTO userGroupResDTO = userGroupSO.retrieveUserGroupUserListByGroupId(userGroupReqDTO);
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
					if(userGroupResDTO.getUserGroupUserList() == null || userGroupResDTO.getUserGroupUserList().isEmpty()){
						throw new EpException(MessageConst.ERROR_740020, new String[]{ResourceBundleUtils.getMessage("lbl.txt.approverUser",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
					}
				}
				FlActor actor = fulfilmentSO.retrieveFlActors(getManagedBean().getFulfilmentRequest().getDocType(), getManagedBean().getFulfilmentRequest().getFulfilmentReqId(), RoleCode.FL_APPROVER.value());
				List<PmUser> approverUserList = sortApproverUserList(userGroupResDTO.getUserGroupUserList(), actor.getUser(),getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId());
				reqDTO.setApproverUserList(approverUserList);
				
				resDTO = deliveryOrderSO.deliveryOrderModification(reqDTO);
			}
			else{
				resDTO = deliveryOrderSO.saveDeliveryOrder(reqDTO);
			}
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700043;
				if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
					String docNo = null;
					String messagePrefix = "";
					if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
						messageId = MessageConst.INFO_700004;
						docNo = getManagedBean().getFulfilmentOrder().getDocNo();
					} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
						messageId = MessageConst.INFO_700007;
						//messagePrefix = "Cancellation for ";
						messagePrefix=ResourceBundleUtils.getMessage("lbl.fl.cancellation.for",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+"  ";
						docNo = getManagedBean().getFlDeliveryOrder().getSupplierDoRef();
					} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
						messageId = MessageConst.INFO_700008;
						//messagePrefix = "Cancellation for ";
						messagePrefix=ResourceBundleUtils.getMessage("lbl.fl.cancellation.for",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+"  ";
						docNo = getManagedBean().getFlDeliveryOrder().getSupplierDoRef();
					} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
						messageId = MessageConst.INFO_700011;
						//messagePrefix = "Update for ";
						messagePrefix=ResourceBundleUtils.getMessage("lbl.fl.update.for",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+"  ";
						docNo = getManagedBean().getFlDeliveryOrder().getSupplierDoRef();
					} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)){
						messageId = MessageConst.INFO_700043;
						//messagePrefix = "Update for ";
						messagePrefix=ResourceBundleUtils.getMessage("lbl.fl.update.for",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+"  ";
						docNo = getManagedBean().getFlDeliveryOrder().getSupplierDoRef();
					} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL) && (resDTO.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_INVOICE) || resDTO.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_INVOICE))) {
						messageId = MessageConst.INFO_745016;
						docNo = getManagedBean().getFlDeliveryOrder().getSupplierDoRef();
					}else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL) && (!resDTO.getPmStatus().getStatusId().equals(FulfilmentStatusConst.PO_PENDING_INVOICE) || !resDTO.getPmStatus().getStatusId().equals(FulfilmentStatusConst.CO_PENDING_INVOICE))) {
						messagePrefix = ResourceBundleUtils.getMessage("lbl.fl.cancellation.for",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+"  ";
						messageId = MessageConst.INFO_700005;
						docNo = getManagedBean().getFlDeliveryOrder().getSupplierDoRef();
					}
					else {	
						/*if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
							messagePrefix = ResourceBundleUtils.getMessage("lbl.fl.cancellation.for",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+"  ";
						}*/
						messageId = MessageConst.INFO_700005;
						docNo = getManagedBean().getFlDeliveryOrder().getSupplierDoRef();
					}
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,	null,
							MessageUtils.getMessage(messageId, 
									new String[]{messagePrefix+getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_DELIVERY_ORDER), docNo}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, 
									new FacesScopes().getUserLocale())));
				} else {
					messageId = MessageConst.INFO_700043;
					String message = "";
					for(String supplierDORef : resDTO.getSupplierDeliveryOrderNoList()) {
						message = MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_DELIVERY_ORDER), supplierDORef}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale());
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,	null, message));
						//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,	null, message+"View "+"<a href='"+generateReportURL(ReportConst.FL_NGEP_DELIVERY,paramMap)+ "'>"+"Delivery Order"+"</a>"));
					}
					
				}
			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
			if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
				renewSesionObject();
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
					Map<String, String> paramMap = new HashMap<String, String>();
					String deliveryOrderNoList = "";
					for(String deliveryOrderNo : resDTO.getDeliveryOrderNoList()) {
						if(deliveryOrderNo.length()==0) {
							deliveryOrderNoList = deliveryOrderNo;
						} else {
							deliveryOrderNoList = deliveryOrderNoList + "," + deliveryOrderNo;
						}
					}
					paramMap.put(ReportConst.PARAM_DOC_NO, deliveryOrderNoList);
					paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
					CommonBackingBean commonBackingBean = new CommonBackingBean();
					commonBackingBean.getManagedBean().setReportName(ReportConst.FL_NGEP_DELIVERY);
					commonBackingBean.getManagedBean().setReportDisplayName(ResourceBundleUtils.getMessage("lbl.fl.do",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
					commonBackingBean.getManagedBean().setParamMap(paramMap);
					return CommonUIConst.ACKNOWLEDGE_REPORT_PAGE;
				}		
				RequestContext.getCurrentInstance().update("@form");
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			} else {
				reqDTO.setFlOrderId(resDTO.getFulfilmentOrderId());
				reqDTO.setDocType(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
				resDTO = orderSO.retrieveFulfilmentOrderById(reqDTO);
				setDeliveryOrder(resDTO);
				getSupplierGst(resDTO.getFlOrder().getFlFulfilmentRequest());
			}
		} finally {
			deliveryOrderSO = null;
		}
		return null;
	}
	
	/**
	 * Before Save do validation for delivery amount
	 */
	private void reValidateRetrieveDelAmt() {
		BigDecimal remainingQty=new BigDecimal(0.0);
		for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddr : flDeliveryAddress.getFlFulfilmentItemAddr()){
				remainingQty = itemAddr.getOrderedQty().subtract(itemAddr.getSumDeliveredQty()).add(itemAddr.getRejectedQty());
				if(itemAddr.getDeliveredQty() == null){
					itemAddr.setDeliveredQty(new BigDecimal(0));
				}
				if(itemAddr.getDeliveredQty().doubleValue()>remainingQty.doubleValue()) {
					itemAddr.setDeliveredQty(new BigDecimal(0));
					itemAddr.setDeliveredAmt(new BigDecimal(0));
					throw new EpException(MessageConst.ERROR_100032, new String[]{
							ResourceBundleUtils.getMessage("lbl.fl.do.delivery.qty",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()),
							ResourceBundleUtils.getMessage("lbl.fl.do.remaining.qty",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
				}
			}
		}
		
	}

	/**
	 * Sets the delivery order.
	 *
	 * @param resDTO the new delivery order
	 */
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
					itemAddr.setDeliveredQty(new BigDecimal(0));
					itemAddr.setDeliveredAmt(new BigDecimal(0));
				} else {
					itemAddr.setDeliveredQty(itemAddr.getFlTempDoItem().getDeliveryQty());
					itemAddr.setDeliveredAmt(itemAddr.getFlTempDoItem().getDeliveryAmt());
				}
				BigDecimal remainOrderQty=itemAddr.getOrderedQty().subtract(itemAddr.getSumDeliveredQty()).add(itemAddr.getRejectedQty());
				itemAddr.setDelQtyDisabled(false);
				if(remainOrderQty.doubleValue()==0){
					itemAddr.setDelQtyDisabled(true);
				}
				
			}
		}
		
		getManagedBean().setSelectedTabIndex(0);
		getManagedBean().setFulfilmentRequest(flOrder.getFlFulfilmentRequest());
		getManagedBean().setFulfilmentOrder(flOrder);
		getManagedBean().setDocType(flOrder.getFlFulfilmentRequest().getDocType());
		
		if(flOrder.getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
			new FulfilmentBackingBean(ParameterConst.DOCTYPE_DELIVERY_ORDER, flOrder.getFlFulfilmentRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
		} else {
			new FulfilmentBackingBean(ParameterConst.DOCTYPE_DELIVERY_ORDER, flOrder.getFlFulfilmentRequest().getCtContract().getContractNo());
		}
	}
	
	/**
	 * validations for items with Delivery Address,Delivery order and Delivery Amount
	 *
	 * @param actionName the action name
	 */
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
			throw new EpException(MessageConst.ERROR_740045, new String[]{ResourceBundleUtils.getMessage("lbl.fl.delivery.address",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
		}else{
			if(!FLProcessNameConst.UPDATE_BTN.equalsIgnoreCase(actionName)){
				for(FlDeliveryAddress selectedDelAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
					if(selectedDelAddress.getFlTempDoAddr().isSelected()) {
						if(selectedDelAddress.getFlTempDoAddr().getSupplierDoRef()==null || selectedDelAddress.getFlTempDoAddr().getSupplierDoRef().length()==0) {
							throw new EpException(MessageConst.ERROR_100051, new String[]{ResourceBundleUtils.getMessage("lbl.fl.frn.supp.do.no",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+" ",ResourceBundleUtils.getMessage("lbl.fl.del.delivery.address",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+" "+getFullAddress(selectedDelAddress.getFlAddress())});
						}
						if(selectedDelAddress.getFlTempDoAddr().getEstDeliveryDate()==null) {
							throw new EpException(MessageConst.ERROR_100051, new String[]{ResourceBundleUtils.getMessage("lbl.fl.exp.del.date",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+" ",ResourceBundleUtils.getMessage("lbl.fl.del.delivery.address",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+" "+getFullAddress(selectedDelAddress.getFlAddress())});
						}
						totalDeliveryQty = 0;
						totalDeliveryAmount = 0;
						for(FlFulfilmentItemAddr itemAddress: selectedDelAddress.getFlFulfilmentItemAddr()){
							if(itemAddress.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
								if(itemAddress.getDeliveredAmt() == null){
									itemAddress.setDeliveredAmt(new BigDecimal(0));
								}
								if(itemAddress.getDeliveredAmt().doubleValue() > 0 && itemAddress.getDeliveredQty().doubleValue() <= 0){
									throw new EpException(MessageConst.ERROR_740008, new String[]{ResourceBundleUtils.getMessage("lbl.fl.do.delivery.qty",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+" "+getFullAddress(selectedDelAddress.getFlAddress()),ResourceBundleUtils.getMessage("lbl.fl.del.delivery.address",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+" "+getFullAddress(selectedDelAddress.getFlAddress()),getFullAddress(selectedDelAddress.getFlAddress())});
								}else if(itemAddress.getDeliveredAmt().doubleValue() <= 0 && itemAddress.getDeliveredQty().doubleValue() > 0){
									throw new EpException(MessageConst.ERROR_740008, new String[]{ResourceBundleUtils.getMessage("lbl.fl.delivery.amount",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+" "+getFullAddress(selectedDelAddress.getFlAddress()),ResourceBundleUtils.getMessage("lbl.fl.del.delivery.address",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+" "+getFullAddress(selectedDelAddress.getFlAddress()),getFullAddress(selectedDelAddress.getFlAddress()),getFullAddress(selectedDelAddress.getFlAddress())});
								}
							}
							totalDeliveryQty = totalDeliveryQty + itemAddress.getDeliveredQty().doubleValue();
							totalDeliveryAmount = totalDeliveryAmount + itemAddress.getDeliveredAmt().doubleValue();
						}
						if(totalDeliveryQty==0) {
							throw new EpException(MessageConst.ERROR_740008, new String[]{ResourceBundleUtils.getMessage("lbl.fl.total.del.quantity",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()),getFullAddress(selectedDelAddress.getFlAddress())});
						}
						if(totalDeliveryAmount==0) {
							throw new EpException(MessageConst.ERROR_740008, new String[]{ResourceBundleUtils.getMessage("lbl.fl.total.del.amount",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()),getFullAddress(selectedDelAddress.getFlAddress())});
						}
					}
				}
			}
		}
	}
	
	/**
	 * Calculate delivery amount based on unit price and delivery quantity.
	 *
	 * @param delOrder the del order
	 */
	public void calculateDelAmt(FlDeliveryOrder delOrder){//TODO i checked there's nthg calling this
		for(FlDelOrderItem orderItem : delOrder.getFlDelOrderItem()){
			orderItem.setDeliveredAmt(new BigDecimal(orderItem.getFlFulfilmentItemAddr().getGstUnitPrice().intValue() * orderItem.getDeliveredQty().intValue()));
		}
	}
	
	/**
	 * Select item event.
	 */
	public void selectItemEvent() {
		getManagedBean().setSelectedItemRowCalList(new ArrayList<FlFulfilmentItemAddr>());
		FlFulfilmentItemAddr selectedAddressItem=new FlFulfilmentItemAddr();
		selectedAddressItem.setOrderedAmt(getManagedBean().getSelectedItemRow().getOrderedAmt());
		getManagedBean().getSelectedItemRowCalList().add(selectedAddressItem);
		getManagedBean().getSelectedItemRowCalList().size();
		getManagedBean().setItemSelectedForCal("S");
		
	}
	
	/**
	 * Calculate delivery amount based on unit price and delivery quantity.
	 *
	 * @param itemAddress the item address
	 */
	public void retrieveDelAmount(FlFulfilmentItemAddr itemAddress){
		
		BigDecimal unitPriceCal = BigDecimal.ZERO;
		BigDecimal delQtyCal= BigDecimal.ZERO;
		BigDecimal delAmtCal= BigDecimal.ZERO;
		BigDecimal remainingQty = BigDecimal.ZERO;
		
		unitPriceCal=itemAddress.getDoGstUnitPrice();		
		if(null != itemAddress.getDeliveredQty()){
			BigDecimal formattedDeliveredQty = itemAddress.getDeliveredQty().setScale(
					itemAddress.getScRequestItem().getUom().getDecimalScale(),BigDecimal.ROUND_HALF_UP);
			itemAddress.setDeliveredQty(formattedDeliveredQty);
		}
		delQtyCal= itemAddress.getDeliveredQty();
		remainingQty = itemAddress.getOrderedQty().subtract(itemAddress.getSumDeliveredQty()).add(itemAddress.getRejectedQty());
		if(delQtyCal == null){
			delQtyCal = BigDecimal.ZERO;
		}
		if(delQtyCal.doubleValue()<0) {		
			itemAddress.setDeliveredQty(BigDecimal.ZERO);
			itemAddress.setDeliveredAmt(BigDecimal.ZERO);
			throw new EpException(MessageConst.ERROR_100033, new String[]{ResourceBundleUtils.getMessage("lbl.fl.do.delivery.qty",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()),"0"});
		}
		
		if(delQtyCal.doubleValue()>remainingQty.doubleValue()) {
			itemAddress.setDeliveredQty(BigDecimal.ZERO);
			itemAddress.setDeliveredAmt(BigDecimal.ZERO);
			throw new EpException(MessageConst.ERROR_100032, new String[]{
					ResourceBundleUtils.getMessage("lbl.fl.do.delivery.qty",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()),
					ResourceBundleUtils.getMessage("lbl.fl.do.remaining.qty",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});			
		}
		
		if(itemAddress.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE) 
			&& itemAddress.getDeliveredQty().doubleValue() == 0) {
			itemAddress.setDeliveredAmt(BigDecimal.ZERO);
		} else if(itemAddress.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)){
			delAmtCal = unitPriceCal.multiply(delQtyCal);
//			if(delQtyCal.doubleValue()==remainingQty.doubleValue()) { 
//				delAmtCal = itemAddress.getOrderedAmt().subtract(itemAddress.getSumDeliveredAmt()).add(itemAddress.getRejectedAmt());
//			}
			itemAddress.setDeliveredAmt(delAmtCal.setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE, RoundingMode.HALF_UP));
		}
		
	}
	
	/**
	 * Pre render view.
	 */
	public void preRenderView() {
		log.debug("preRenderView()");
		IDeliveryOrderSO deliveryOrderSO = null;
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		String orderId = originalServletRequest.getParameter("orderId");
		if(orderId != null){
			IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			reqDTO.setFlOrderId(Long.parseLong(orderId));
			FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(reqDTO);
			setDeliveryOrder(resDTO);
		}
		//if taskid != null process modification or cancellation is running
		if(null!=taskId && !"".equals(taskId)){
			IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
			Object obj = super.getBpmTaskDetails(Object.class, null);
			getManagedBean().setModificationCancel(false);
			if(FLProcessNameConst.DO_MODIFICATION_PROCESS_NAME.equals(this.getManagedBean().getProcessName())){
				FLDOFulfillmentModificationReqType doModDataType = (FLDOFulfillmentModificationReqType) obj;
				if(doModDataType.getErrorId() != null && doModDataType.getErrorId() > 0){
					getManagedBean().setAutoUpdate(Boolean.FALSE);
					getManagedBean().setGfmasError(true);
					FulfilmentReqDTO errReq = new FulfilmentReqDTO();
					errReq.setErrorId(doModDataType.getErrorId());
					FulfilmentResDTO errRes = fulfilmentSO.retrieveErrorContent(errReq);
					getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
				}
				getManagedBean().setButtonVal("ACK");
				getManagedBean().setModificationCancel(true);
				reqDTO.setDeliveryOrderId(Long.valueOf(doModDataType.getDoId()));
			}else if(FLProcessNameConst.DO_CANCELLATION_PROCESS_NAME.equals(this.getManagedBean().getProcessName())){
				FLDOFulfillmentCancelReqType doCancelDataType = (FLDOFulfillmentCancelReqType) obj;
				if(doCancelDataType.getErrorId() != null && doCancelDataType.getErrorId() > 0){
					getManagedBean().setAutoUpdate(Boolean.FALSE);
					getManagedBean().setGfmasError(true);
					FulfilmentReqDTO errReq = new FulfilmentReqDTO();
					errReq.setErrorId(doCancelDataType.getErrorId());
					FulfilmentResDTO errRes = fulfilmentSO.retrieveErrorContent(errReq);
					getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
				}
				getManagedBean().setButtonVal("ACK");
				getManagedBean().setModificationCancel(true);
				reqDTO.setDeliveryOrderId(Long.valueOf(doCancelDataType.getDoId()));
			}
				
				deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IDeliveryOrderSO.class);
				FulfilmentResDTO resDTO = deliveryOrderSO.retrieveDeliveryOrderById(reqDTO);
//				getManagedBean().setDocProcess(res.getDocType());
//				getManagedBean().setDocType(res.getDocType());
				getManagedBean().setFlDeliveryOrder(resDTO.getFlDeliveryOrder());
				getManagedBean().setFulfilmentRequest(resDTO.getFlDeliveryOrder().getFlRequest());
				getManagedBean().setFulfilmentOrder(resDTO.getFlDeliveryOrder().getFlFulfilmentOrder());
				getManagedBean().getFulfilmentOrder().setStatus(deliveryOrderSO.retrieveDelOrderStatus(reqDTO).getPmStatus());
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				flBB.getManagedBean().setHideRemarks(false);
				flBB.initializeRemarkAndAttachement(resDTO.getFlDeliveryOrder().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
				
				
		}else{
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			if(null == getManagedBean().getButtonVal() || !getManagedBean().getButtonVal().equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(false);
			}
		}
		
		if(getManagedBean().isGstAmtChanged()) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
				MessageUtils.getMessage(MessageConst.INFO_745009, new String[]{getManagedBean().getFulfilmentRequest().getDocNo()}, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
			getManagedBean().setGstAmtChanged(false);
		}
	}

	/**
	 * Retrieve rejection data.
	 *
	 * @param flFulfilmentItemAddr the fl fulfilment item addr
	 */
	public void retrieveRejectionData(FlFulfilmentItemAddr flFulfilmentItemAddr) {
		getManagedBean().setRejectionList(new ArrayList<FlFulfilmentNoteItem>());
		for(FlDelOrderItem delOrderItem : flFulfilmentItemAddr.getTempFlDelOrderItems()) {
			for(FlFulfilmentNoteItem frnItem : delOrderItem.getTempFlFulfilmentNoteItem()) {
				if(frnItem.getRejectedQty().doubleValue()>0 || frnItem.getRejectedAmt().doubleValue()>0) {
					getManagedBean().getRejectionList().add(frnItem);
				}
			}
		}
	}
	
	/**
	 * Gets the portlet title.
	 *
	 * @return the portlet title
	 */
	public String getPortletTitle() {
		String portletTitle = ResourceBundleUtils.getMessage("lbl.fl.doc.maintenance",ResourceBundleUtils.BUNDLE_COMMON_LABELS,getFacesScopes().getUserLocale());
		if(getManagedBean().isModificationCancel()) {
			portletTitle = ResourceBundleUtils.getMessage("lbl.fl.doc.maintenance",ResourceBundleUtils.BUNDLE_COMMON_LABELS,getFacesScopes().getUserLocale());
		}
		return portletTitle;
	}
	
	/**
	 * On tab change.
	 *
	 * @param selectedTabIndex the selected tab index
	 */
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==1){
			if(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()!=null && getManagedBean().getButtonVal()==null) {
				sortAddress(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses());
				sortOrderSummaryList(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses());
				for(FlDeliveryAddress deliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()) {
					for(FlFulfilmentItemAddr itemAddr : deliveryAddress.getFlFulfilmentItemAddr()) {
						BigDecimal remainOrderQty=itemAddr.getOrderedQty().subtract(itemAddr.getSumDeliveredQty()).add(itemAddr.getRejectedQty());
						deliveryAddress.setSuppDelOrdNoEnabled(false);
						if(remainOrderQty.doubleValue()> 0){
							deliveryAddress.setSuppDelOrdNoEnabled(true);
							break;
						}
					}
				}
			}
		}
	}
	
	// sorting Item for OrderSummary Tab
	/**
	 * Sort order summary list.
	 *
	 * @param deliveryAddressList the delivery address list
	 */
	public void sortOrderSummaryList(List<FlDeliveryAddress> deliveryAddressList) {
		// Creating Comparator
		if (null == deliveryAddressList || deliveryAddressList.isEmpty()) {
			return;
		}
		for (FlDeliveryAddress flDeliveryAddress : deliveryAddressList) {
			sortItems(flDeliveryAddress.getFlFulfilmentItemAddr());
		}
	}

	/**
	 * Sort items.
	 *
	 * @param flItemAddressList the fl item address list
	 */
	public void sortItems(List<FlFulfilmentItemAddr> flItemAddressList) {
		// Creating Comparator
		if (null == flItemAddressList || flItemAddressList.isEmpty()) {
			return;
		}
		Comparator<FlFulfilmentItemAddr> comparator = new Comparator<FlFulfilmentItemAddr>() {
			@Override
			public int compare(FlFulfilmentItemAddr o1, FlFulfilmentItemAddr o2) {
				return new CompareToBuilder().append(
						o1.getItemCode(),
						o2.getItemCode())
						.toComparison();
			}
		};
		Collections.sort(flItemAddressList, comparator);
	}

	/**
	 * Sort delivery address.
	 *
	 * @param flDelAddressList the fl del address list
	 */
	public void sortAddress(List<FlDeliveryAddress> flDelAddressList) {
		// Creating Comparator
		if (null == flDelAddressList || flDelAddressList.isEmpty()) {
			return;
		}
		Comparator<FlDeliveryAddress> comparator = new Comparator<FlDeliveryAddress>() {
			@Override
			public int compare(FlDeliveryAddress o1, FlDeliveryAddress o2) {
				return new CompareToBuilder()
						.append(o1.getFlAddress().getAddress1(),
								o2.getFlAddress().getAddress1())
						.append(o1.getFlAddress().getAddress2(),
								o2.getFlAddress().getAddress2())
						.append(o1.getFlAddress().getAddress3(),
								o2.getFlAddress().getAddress3()).toComparison();
			}
		};
		Collections.sort(flDelAddressList, comparator);
	}
	
	
	/**
	 * Gets the amount section item row.
	 *
	 * @return the amountSectionItemRow
	 */
	public FlFulfilmentItemAddr getAmountSectionItemRow() {
		return amountSectionItemRow;
	}

	/**
	 * Sets the amount section item row.
	 *
	 * @param amountSectionItemRow the amountSectionItemRow to set
	 */
	public void setAmountSectionItemRow(FlFulfilmentItemAddr amountSectionItemRow) {
		this.amountSectionItemRow = amountSectionItemRow;
	}

	/**
	 * Back action.
	 *
	 * @return the string
	 */
	public String backAction() {
		if(this.getManagedBean().getBackToPrevious() !=null){
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.getManagedBean().setRemarkList(getManagedBean().getPreviousRemarkList());
			flBB.getManagedBean().setAttachmentList(getManagedBean().getPreviousAttachmentList());
			if(getManagedBean().isFromDoPage()){
				FulfilmentOrderBackingBean flOrderBB = new FulfilmentOrderBackingBean();
				flOrderBB.getManagedBean().getPendingDoDataModel().reloadData();
			}
			return getManagedBean().getBackToPrevious();
		}
		else{
			return "flDocSearch";
		}
		//System.out.println("getManagedBean().getFromPageUrl(): "+getManagedBean().getFromPageUrl());
		//return getManagedBean().getFromPageUrl()+"?faces-rediret=true";
	}
	
	/**
	 * Page navigation to FLDoc search page .
	 *
	 * @return the string
	 */
	public String backToPervious() {
		return "flDocSearch";
	}
	
	/**
	 * Signing action.
	 *
	 * @param outcome the outcome
	 * @return the string
	 * @throws Exception the exception
	 */
	public String signingAction(String outcome) throws Exception{
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
			getManagedBean().setAutoUpdate(Boolean.TRUE);
			if(flBB.getManagedBean().getRemarkList().get(0).getRemark()==null || flBB.getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.txt.remarks",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			}
		}
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)){ //DO maintenance by supplier
			List <String> supplierDoNoList = null;
			if(null != getManagedBean().getFlDeliveryOrder().getSupplierDoRef()){
				IDeliveryOrderSO deliveryOrderSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IDeliveryOrderSO.class);
				supplierDoNoList = new ArrayList<String>();
				supplierDoNoList.add(getManagedBean().getFlDeliveryOrder().getSupplierDoRef());
				FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
				reqDTO.setSupplierId(getManagedBean().getFulfilmentRequest().getSmSupplier().getSupplierId());
				reqDTO.setSupplierDoNoList(supplierDoNoList);
				FulfilmentResDTO resDTO = deliveryOrderSO.searchDuplicateSupplierDoNo(reqDTO);
				if(resDTO.isDuplicateSupplierDoNo()){
					throw new EpException(MessageConst.ERROR_740042, new String[]{resDTO.getDuplicatedSupplierDoNoList().get(0),getManagedBean().getFulfilmentRequest().getFlSupplierDtl().getSupplierName()});
				}
			}
		}
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || // DO Submission by Supplier
				outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){ // DO Maintenance by Supplier
			if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
				validateDeliveryOrder();
			}
			if(hasGovtSellerRole()){
				setTextBeforeSign(outcome);
				getManagedBean().setButtonAction(outcome);
				getManagedBean().setAutoUpdate(Boolean.FALSE);
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
				return null;
			}else{
				getManagedBean().setButtonAction(outcome);
				getManagedBean().setAutoUpdate(Boolean.FALSE);
				getManagedBean().getSpkiBackingBean().getChallengeQuestions();
				RequestContext.getCurrentInstance().execute("spki_dlg.show()");
				return null;
			}
		}else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE) || 
				outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT) || 
				outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) { // DO Maintenance Approval by FL Approver
			setTextBeforeSign(outcome);
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		}
		else{
			return createDeliveryOrder(outcome);
		}
	}
	
	
	/**
	 * Validate delivery order before signing Action.
	 */
	private void validateDeliveryOrder() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		if(getManagedBean().getFulfilmentRequest().getCtContract() != null){
			Calendar calendar = Calendar.getInstance();
			for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
				if(flDeliveryAddress.getFlTempDoAddr().isSelected()) {
					Date currentDate = new Timestamp(System.currentTimeMillis());
					Date startDate = flDeliveryAddress.getStartDate();
					int deliveryTerm = 0;
					deliveryTerm = getManagedBean().getFulfilmentRequest().getCtContract().getLatestContractVer().getCtSetting().getSupplierDoDays();
					calendar.setTime(currentDate);
					calendar.add(Calendar.DATE, deliveryTerm);
					Date newDate = calendar.getTime();

					if(newDate.before(startDate)){
						throw new EpException(MessageConst.ERROR_745057, new String[]{getFullAddress(flDeliveryAddress.getFlAddress())});
					}
				}
			}
		}
		List <String> supplierDoNoList = new ArrayList<String>();
		IAddressSO addressSO = (IAddressSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
		for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			AddressReqDTO reqDTO = new AddressReqDTO();
			if(flDeliveryAddress.getFlTempDoAddr().isSelected()) {
				if(supplierDoNoList.contains(flDeliveryAddress.getFlTempDoAddr().getSupplierDoRef())){
					throw new EpException(MessageConst.ERROR_740042, new String[]{flDeliveryAddress.getFlTempDoAddr().getSupplierDoRef(),getManagedBean().getFulfilmentRequest().getFlSupplierDtl().getSupplierName()});
				}else{
					supplierDoNoList.add(flDeliveryAddress.getFlTempDoAddr().getSupplierDoRef());
				}
				
				Long addressId = flDeliveryAddress.getPmAddressId();
				if(flDeliveryAddress.getReceivingGrp()!=null && flDeliveryAddress.getReceivingGrp().getAddressTypeId()!=null) { // in case of Manual address
					reqDTO = new AddressReqDTO();
					reqDTO.setAddressTypeId(flDeliveryAddress.getReceivingGrp().getAddressTypeId());
					AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
					addressId = resDTO.getAddressTypeList().get(0).getPmAddress().getAddressId();
				}
				
				reqDTO.setAddressId(addressId);
				reqDTO.setAddressType(AddressType.RECEIVING_OFFICER_ADDRESS.value());
				AddressResDTO resDTO = addressSO.retrieveReceivingOfficerList(reqDTO);
				if(resDTO.getAddressTypeList().isEmpty()){//TODO others as well needed to throw if list is empty
					throw new EpException(MessageConst.ERROR_740020, new String[]{ResourceBundleUtils.getMessage("lbl.txt.receivingOfficer",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
				}
			}
		}
		if(!supplierDoNoList.isEmpty()){
			IDeliveryOrderSO deliveryOrderSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IDeliveryOrderSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setSupplierId(getManagedBean().getFulfilmentRequest().getSmSupplier().getSupplierId());
			reqDTO.setSupplierDoNoList(supplierDoNoList);
			FulfilmentResDTO resDTO = deliveryOrderSO.searchDuplicateSupplierDoNo(reqDTO);
			if(resDTO.isDuplicateSupplierDoNo()){
				String supplierRefNo = null;
				for(int i = 0;i < resDTO.getDuplicatedSupplierDoNoList().size();i++){
					if(supplierRefNo != null){
						supplierRefNo = ", "+resDTO.getDuplicatedSupplierDoNoList().get(i);
					}else{
						supplierRefNo = resDTO.getDuplicatedSupplierDoNoList().get(i);
					}
				}
				
				throw new EpException(MessageConst.ERROR_740042, new String[]{supplierRefNo,getManagedBean().getFulfilmentRequest().getFlSupplierDtl().getSupplierName()});
			}
		}
		
		reValidateRetrieveDelAmt();

		// do validation for selected address have items with delmount and delQty or not....
		checkItemsWithDoAddress(getManagedBean().getButtonAction());
	}

	/**
	 * Gets the total delivery order amount.
	 *
	 * @return the delivery order amt
	 */
	private String getDeliveryOrderAmt() {
		String doAmt = "";
		BigDecimal totalAmt = new BigDecimal(0);
		
		for(FlDeliveryAddress delAddr : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddr : delAddr.getFlFulfilmentItemAddr()){
				totalAmt = totalAmt.add(itemAddr.getDeliveredAmt());
			}
		}
		doAmt = totalAmt.toString();
		return doAmt;
	}

	/**
	 * Pre submit delivery order.
	 *
	 * @param pmDigiSign the pm digi sign
	 * @return the string
	 * @throws Exception the exception
	 */
	public String preSubmitDeliveryOrder(PmDigiSign pmDigiSign) throws Exception {
		if(pmDigiSign != null) {
			getManagedBean().setPmDigiSign(pmDigiSign);
			if(hasSupplierRole() && !hasGovtSellerRole()) {
				setTextBeforeSign(getManagedBean().getButtonAction());
				updateSpkiDigiSignData(getManagedBean().getPmDigiSign(), ParameterConst.DOCTYPE_DELIVERY_ORDER, 
					getManagedBean().getFulfilmentOrder().getFulfilmentOrderId(), getManagedBean().getFulfilmentOrder().getDocNo(), 
					getManagedBean().getTextBeforeSign());
			}
		}
		return createDeliveryOrder(getManagedBean().getButtonAction());
	}
	
	/**
	 * Enable auto update.
	 */
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}	
	
	/**
	 * The Class UnitOfMeasurementPagingDataModel.
	 */
	private class UnitOfMeasurementPagingDataModel extends PagingDataModel<PmUom> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new unit of measurement paging data model.
		 */
		public UnitOfMeasurementPagingDataModel() {
			super("measurementSR");
		}

		/* (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmUom> pageChangeListener(ISearchBean<PmUom> searchBean) {
			try {	
				ISearchBean<PmUom> searchBeanData = getMeasurementSearchBeanData(searchBean);
				UomReqDTO reqDTO = new UomReqDTO();
				reqDTO.setMeasurementSearchBean(searchBeanData);				
				IUomSO reqSO = (IUomSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUomSO.class);	
				UomResDTO resDTO = reqSO.retrieveMeasurementBySearch(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getMeasurementSearchResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}
	
	/**
	 * Gets the measurement search bean data 
	 *<p> if Measurement Codes not null search criteria based on uomCode,Measurement Codes.
	 *<p> if Measurement name not null search criteria based on uomName,Measurement names.
	 * @param searchBean the search bean
	 * @return the measurement search bean data
	 */
	private ISearchBean<PmUom> getMeasurementSearchBeanData(ISearchBean<PmUom> searchBean){
		
		if (getManagedBean().getMeasurementCodes() != null
				&& !"".equalsIgnoreCase(getManagedBean().getMeasurementCodes())) {
			SearchFilterBean uomSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			uomSearchBean
					.setSearchField("uomCode");
			uomSearchBean
					.setCaseInsensitive(true);
			uomSearchBean.setSearchValue(getManagedBean()
					.getMeasurementCodes());
			searchBean.getDataFilterBeans().add(uomSearchBean);
		}

		if (getManagedBean().getMeasurementNames() != null
				&& !"".equalsIgnoreCase(getManagedBean().getMeasurementNames())) {
			SearchFilterBean uomSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			uomSearchBean
					.setSearchField("uomName");
			uomSearchBean
					.setCaseInsensitive(true);
			uomSearchBean.setSearchValue(getManagedBean()
					.getMeasurementNames());
			searchBean.getDataFilterBeans().add(uomSearchBean);
		}
		return searchBean;
	}

	/**
	 * Gets the unit of measurement data model.
	 *
	 * @return the unit of measurement data model
	 */
	public IPagingDataModel<PmUom> getUnitOfMeasurementDataModel() {
		return unitOfMeasurementDataModel;
	}

	/**
	 * Sets the unit of measurement data model.
	 *
	 * @param unitOfMeasurementDataModel the new unit of measurement data model
	 */
	public void setUnitOfMeasurementDataModel(
			IPagingDataModel<PmUom> unitOfMeasurementDataModel) {
		this.unitOfMeasurementDataModel = unitOfMeasurementDataModel;
	}	
	
	/**
	 * Retrieve measurement.
	 */
	public void retrieveMeasurement(){		
		unitOfMeasurementDataModel.reloadData();		
	}
	
	/**
	 * Reset measurement search.
	 */
	public void resetMeasurementSearch() {		
		this.getManagedBean().setMeasurementCodes(null);
		this.getManagedBean().setMeasurementNames(null);
		getUnitOfMeasurementDataModel().resetData();
		unitOfMeasurementDataModel.reloadData();		
	}	
	
	// To update digisign data for spki
	/**
	 * Update spki digi sign data.
	 *
	 * @param pmDigiSign the pm digi sign
	 * @param docType the doc type
	 * @param docId the doc id
	 * @param docNumber the doc number
	 * @param textBeforeSign the text before sign
	 */
	private void updateSpkiDigiSignData(PmDigiSign pmDigiSign, String docType,
			Long docId, String docNumber , String textBeforeSign) {
		FacesScopes facesScope = new FacesScopes();
		Long userId = facesScope.getUserSessionBean().getUserId();
		Timestamp date = new Timestamp(System.currentTimeMillis());
		pmDigiSign.setDocId(new BigDecimal(docId));
		pmDigiSign.setDocNo(docNumber);
		pmDigiSign.setDocType(docType);
		pmDigiSign.setCreatedBy(userId);
		pmDigiSign.setCreatedDate(date);
		pmDigiSign.setOriginalData(textBeforeSign);
		// spki 
		pmDigiSign.setSpkiSignedData(getManagedBean().getSpkiBackingBean().spkiSignTransaction(textBeforeSign));
	}
	
	/**
	 * Navigate to orderNo Hyper link page.
	 *
	 * @param docType the doc type
	 * @return the string
	 */
	public String navigateOrderNoPage(String docType){
		String navgPage="";
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		//PurchaseRequestBackingBean prBB = new PurchaseRequestBackingBean();
		IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setFlOrderId(getManagedBean().getFulfilmentOrder().getFulfilmentOrderId());
		reqDTO.setDocType(docType);
		FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(reqDTO);
		FulfilmentOrderBackingBean orderBackingBean = new FulfilmentOrderBackingBean();
		orderBackingBean.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
		orderBackingBean.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
		orderBackingBean.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
		orderBackingBean.getManagedBean().setSupplier(hasSupplierRole());
		
		FacesContext fc = FacesContext.getCurrentInstance();
		orderBackingBean.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
		orderBackingBean.getManagedBean().setSelectedTabIndex(0);
		orderBackingBean.getManagedBean().setHyperLinkPage(true);
		orderBackingBean.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
		orderBackingBean.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());
		
		flBB.getManagedBean().setHideRemarks(true);
		flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
		flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());

		if(resDTO.getFlOrder().isFactored()) {
			orderBackingBean.getManagedBean().setFactoringCheck("true");
			orderBackingBean.getFactoringCompanyDtl();
		}
		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
			return NAV_PO;
		}else if (docType.equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
			return NAV_CO;
		}
		return navgPage;
		
	}
	
	/**
	 * Navigate to stop instruction view page.
	 * @param docId
	 * @return String gotoCommonViewPage
	 */
	public String navigateDocument(Long docId){
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		IFulfilmentOrderSO orderSO= (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
		StopInstrBackingBean siBB = new StopInstrBackingBean();
		FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
		flReqDTO.setFlOrderId(getManagedBean().getFulfilmentOrder().getFulfilmentOrderId());
		flReqDTO.setStopRequestId(docId);
		flReqDTO.setStatusDesc(FulfilmentStatusConst.SD_REJECTED.toString());
		FulfilmentResDTO resDTO = orderSO.retrieveStopInstruction(flReqDTO);
		siBB.getManagedBean().setStopInstr(resDTO.getFlStopInstr());
		siBB.getManagedBean().setSelectedTabIndex(0);
		siBB.populateChargeLineData();
		siBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
		siBB.getManagedBean().setHyperLinkPage(true);
		flBB.getManagedBean().setHideRemarks(true);
		flBB.initializeRemarkAndAttachement(resDTO.getFlStopInstr().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
		FacesContext fc = FacesContext.getCurrentInstance();
		siBB.getManagedBean().setBackToDocument(fc.getViewRoot().getViewId());
		
		
		return NAV_STOP_INSTRUCTION;
	}
	
	/**
	 * View delivery order.
	 */
	public void viewDeliveryOrder() {
		IDeliveryOrderSO doSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IDeliveryOrderSO.class);
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		
		String doId = originalServletRequest.getParameter("doId");
		
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		if(doId!=null) {
			reqDTO.setDeliveryOrderId(Long.valueOf(doId));
			reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			reqDTO.setDocType(ParameterConst.DOCTYPE_DELIVERY_ORDER);
			reqDTO.setInvoiceLink(Boolean.TRUE);
			renewSesionObject();
			try {
				resDTO = doSO.retrieveDeliveryOrderById(reqDTO);
			} catch (EpException e) {
				handleException(e);
				return;
			}
			
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			getManagedBean().setSelectedTabIndex(0);
			getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			getManagedBean().setTaskId(BpmOutcomeConst.BPM_VIEW);//for the purpose of hide the back button.
//			getManagedBean().setDocProcess(FulfilmentConst.DOC_TYPE_DO);
			getManagedBean().setDocType(ParameterConst.DOCTYPE_DELIVERY_ORDER);
			getManagedBean().setFlDeliveryOrder(resDTO.getFlDeliveryOrder());
			getManagedBean().setFulfilmentRequest(resDTO.getFlDeliveryOrder().getFlRequest());
			getManagedBean().setFulfilmentOrder(resDTO.getFlDeliveryOrder().getFlFulfilmentOrder());
			getManagedBean().setDocMaintainRemarks(true);
			getManagedBean().getFulfilmentOrder().setStatus(doSO.retrieveDelOrderStatus(reqDTO).getPmStatus());
//			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(true);
//			}
			flBB.initializeRemarkAndAttachement(resDTO.getFlDeliveryOrder().getFlWorkflowStatus(), resDTO.getRemarkList(),resDTO.getAttachmentList());
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "doBasePage?faces-redirect=true");
		}
	}	
	
	/**
	 * Show 1GFMAS error
	 *
	 */
	public void show1GFMASError(){
		getManagedBean().setAutoUpdate(Boolean.FALSE);
		RequestContext.getCurrentInstance().update("@form");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,null,getManagedBean().getGfmasErrorContent()));
	}
	
	/**
	 * Retrieve supplier gst
	 * Recalculate the gst unit price
	 *
	 */
	public void getSupplierGst(FlFulfilmentRequest flRequest) {
		ISupplierGstSO supplierGstSO = (ISupplierGstSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierGstSO.class);
		
		SupplierGstReqDTO reqDTO = new SupplierGstReqDTO();
		reqDTO.setBranchCode(flRequest.getFlSupplierDtl().getBranchCode());
		reqDTO.setSupplierId(flRequest.getSmSupplier().getSupplierId());
		SupplierGstResDTO suppGstResDTO = supplierGstSO.retrieveGstInfoBySupplierIdAndBranchCode(reqDTO);
		if(suppGstResDTO.getSmGstSupplier() != null){
			getManagedBean().setGstEffDate(suppGstResDTO.getSmGstSupplier().getGstEffDate());
			getManagedBean().setGstDeclareDate(suppGstResDTO.getSmGstSupplier().getGstDeclaredDate());
			getManagedBean().setGstRegNo(suppGstResDTO.getSmGstSupplier().getGstRegNo());
		} else {
			getManagedBean().setGstEffDate(null);
			getManagedBean().setGstDeclareDate(null);
			getManagedBean().setGstRegNo(null);
		}
		
		boolean amtChanged = reCalculateGstUnitPrice(suppGstResDTO.getSmGstSupplier(),flRequest);
		getManagedBean().setGstAmtChanged(amtChanged);
	}
	
	private boolean reCalculateGstUnitPrice(SmGstSupplier smGstSupplier, FlFulfilmentRequest flRequest){
		boolean amtChanged = false;
		boolean isDoGST = smGstSupplier!=null && smGstSupplier.getGstEffDate()!=null && smGstSupplier.getGstEffDate().getTime() <= DateUtils.getCurrentDateWithoutTime().getTime();

		if(flRequest.getGstRate() == null){
			flRequest.setGstRate(BigDecimal.ZERO);
		}
		
		BigDecimal gstAmt = BigDecimal.ZERO;
		BigDecimal deliveredAmt = BigDecimal.ZERO;
		for(FlDeliveryAddress deliveryAddress : flRequest.getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddr : deliveryAddress.getFlFulfilmentItemAddr()){
				
				if(!isDoGST || !itemAddr.isGst()) {
					itemAddr.setDoGstAmt(BigDecimal.ZERO);
					itemAddr.setDoGstUnitPrice(itemAddr.getUnitPrice());
				} else {
					gstAmt = itemAddr.getUnitPrice().multiply(flRequest.getGstRate().divide(new BigDecimal(100)));
					itemAddr.setDoGstAmt(gstAmt.setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP));
					itemAddr.setDoGstUnitPrice(itemAddr.getUnitPrice().add(itemAddr.getDoGstAmt()));
				}
				if(itemAddr.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)){
					deliveredAmt = itemAddr.getDeliveredQty().multiply(itemAddr.getDoGstUnitPrice());
					itemAddr.setDeliveredAmt(deliveredAmt.setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP));
				}
				
				if(!amtChanged && itemAddr.getGstAmount().compareTo(itemAddr.getDoGstAmt())!=0) {
					amtChanged = true;
				}
			}
		}
		return amtChanged;
	}
	
	private void setTextBeforeSign(String outcome) {
		StringBuffer sb = new StringBuffer();
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
			sb.append(getManagedBean().getFulfilmentOrder().getDocNo()+"|");
			sb.append(ParameterConst.DOCTYPE_DELIVERY_ORDER+"|");
			sb.append(getDeliveryOrderAmt()+"|");//DO AMT
			sb.append(getFacesScopes().getUserSessionBean().getIdentificationNo());
		} else {
			sb.append(getManagedBean().getFulfilmentOrder().getDocNo()+"|");
			sb.append(getManagedBean().getFlDeliveryOrder().getDeliveryOrderNo()+"|");
			sb.append(outcome+"|");
			sb.append(getFacesScopes().getUserSessionBean().getIdentificationNo());
		}
		getManagedBean().setTextBeforeSign(sb.toString());
	}
}
