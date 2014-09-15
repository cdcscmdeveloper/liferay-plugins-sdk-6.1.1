/**
 * Copyright © 2014 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.stopinstr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.stopfullfillmentdata.FLStopFullfillmentDataType;
import my.ep.persistence.entity.fl.order.FlStopInstr;
import my.ep.persistence.entity.fl.order.FlStopInstrItem;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.stopinstr.StopInstrManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.liferay.portal.util.PortalUtil;

/**
 * Backingbean for Stop Instruction
 * 
 * @author Kieyun
 * @since 7/7/2014
 */
@ManagedBean(name="stopInstrBB")
@RequestScoped
public class StopInstrBackingBean extends BaseBackingBean<StopInstrManagedBean>{

	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	private IFulfilmentOrderSO getFulfilmentOrderSO() {
		return (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
	}
	
	public String saveStopInstruction(String action) {
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		if(FulfilmentConst.REASON_CODE.equalsIgnoreCase(getManagedBean().getStopInstr().getPmReason().getReasonCode())) {
			if(null == getManagedBean().getStopInstr().getReasonDesc() || getManagedBean().getStopInstr().getReasonDesc().isEmpty()){
				throw new EpException(MessageConst.ERROR_740026);
			}
		}
		if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)){
			if(flBB.getManagedBean().getRemarkList().get(0).getRemark()==null || flBB.getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{"Remark"});
			}
		}
		String token = getFacesScopes().getBPMAuthenticationCtxToken();
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FlStopInstr flStopInstr = getManagedBean().getStopInstr();

		reqDTO.setStopIns(flStopInstr);
		reqDTO.setTaskId(getManagedBean().getTaskId());
		reqDTO.setOutcome(action);
		reqDTO.setToken(token);
		reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
		reqDTO.setUserRole(getUserRole());
		
		reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
		reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
	
		FulfilmentResDTO resDTO = null;
		if(isFlFulfilmentApprover()) {
			reqDTO.setApprover(Boolean.TRUE);
		}
		resDTO = getFulfilmentOrderSO().stopFulfilment(reqDTO);
		
		if(resDTO.isSuccessful()) {
			String messageId = MessageConst.INFO_700005;
			if(reqDTO.isApprover()) {
				messageId = MessageConst.INFO_700026;
			}
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
				messageId = MessageConst.INFO_700008;
			} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
				messageId = MessageConst.INFO_700007;
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, null,
					MessageUtils.getMessage(messageId, 
							new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_STOP_DELIVERY), resDTO.getFlStopInstr().getStopInstrNo()}, 
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		renewSesionObject();
		return CommonUIConst.ACKNOWLEDGE_PAGE;
	}

	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		if(taskId != null) {
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();

			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();

			FLStopFullfillmentDataType stopFlData = (FLStopFullfillmentDataType)obj;
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			flReqDTO.setFlOrderId(stopFlData.getOrderId());
			flReqDTO.setStopRequestId(stopFlData.getStopRequestId().longValue());
			
			FulfilmentResDTO resDTO = getFulfilmentOrderSO().retrieveStopInstruction(flReqDTO);
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(res.getAction());
			getManagedBean().setStopInstr(resDTO.getFlStopInstr());
			
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			if(resDTO.getFlStopInstr().getFlFulfilmentOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
				new FulfilmentBackingBean(ParameterConst.DOCTYPE_STOP_DELIVERY, resDTO.getFlStopInstr().getFlRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
			} else {
				new FulfilmentBackingBean(ParameterConst.DOCTYPE_STOP_DELIVERY, resDTO.getFlStopInstr().getFlRequest().getCtContract().getContractNo());
			}
			flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
		}
	}
	
	public List<SelectItem> getPopulateReasonList() {
		IReasonSO reasonSO = (IReasonSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IReasonSO.class);
		
		List<SelectItem> populateReasonList = new ArrayList<SelectItem>();
		
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
		reqDTO.setReasonCategory(ParameterConst.FULFILMENT_REASON_CODE);
		ReasonResDTO resDTO = reasonSO.retrieveReasonDescListByCategory(reqDTO);
		getManagedBean().setReasonDescs(resDTO.getReasonDescList());
		List<PmReasonDesc> reasonDescs = resDTO.getReasonDescList();
		for (PmReasonDesc obj : reasonDescs) {
			populateReasonList.add(new SelectItem(obj.getPmReason().getReasonCode(), obj.getReasonDesc()));
		}
		return populateReasonList;
	}
	
	public void populateStopInstrItem() {
		FlStopInstrItem flStopInstrItem = null;
		List<FlStopInstrItem> stopInstrItems = new ArrayList<FlStopInstrItem>();
		for (FlDeliveryAddress flDelAddress : getManagedBean().getStopInstr().getFlRequest().getFlDeliveryAddresses()) {
			for (FlFulfilmentItemAddr itemAddr : flDelAddress.getFlFulfilmentItemAddr()) {
				flStopInstrItem = new FlStopInstrItem();
				flStopInstrItem.setFlFulfilmentItemAddr(itemAddr);
				flStopInstrItem.setFlFulfilmentItem(itemAddr.getFlFulfilmentItem());
				flStopInstrItem.setFlStopInstr(getManagedBean().getStopInstr());
				flStopInstrItem.setRecalledQty(itemAddr.getOrderedQty().subtract(itemAddr.getChargeReceivedQty()));
				if(itemAddr.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
					flStopInstrItem.setRecalledAmt(flStopInstrItem.getRecalledQty().multiply(itemAddr.getUnitPrice()));
				} else {
					flStopInstrItem.setRecalledAmt(itemAddr.getOrderedAmt().subtract(itemAddr.getChargeReceivedAmt()));
				}
				stopInstrItems.add(flStopInstrItem);
			}
		}
		getManagedBean().getStopInstr().setStopInstrItems(stopInstrItems);
	}
	
	public void populateChargeLineData(){
		getManagedBean().setChargeLineItemRoot(new DefaultTreeNode("root", null));
		
		Map<Long, List<FlStopInstrItem>> siItemsMap = new HashMap<Long, List<FlStopInstrItem>>();
		List<FlStopInstrItem> flStopInstrItemList = null;
		for (FlStopInstrItem flStopInstrItem : getManagedBean().getStopInstr().getStopInstrItems()) {
			if (siItemsMap.containsKey(flStopInstrItem.getFlFulfilmentItem().getCmItem().getItemId())) {
				flStopInstrItemList = siItemsMap.get(flStopInstrItem.getFlFulfilmentItem().getCmItem().getItemId());
			} else {
				flStopInstrItemList = new ArrayList<FlStopInstrItem>();
			}
			flStopInstrItemList.add(flStopInstrItem);
			siItemsMap.put(flStopInstrItem.getFlFulfilmentItem().getCmItem().getItemId(), flStopInstrItemList);
		}
		
		BigDecimal totalOrderAmt = new BigDecimal(0);
		BigDecimal totalReceiveAmt = new BigDecimal(0);
		BigDecimal totalRecalledAmt = new BigDecimal(0);
		List<Long> flFulfilmentItemIds = null;
		
		for(Entry<Long, List<FlStopInstrItem>> itemEntry : siItemsMap.entrySet()){
			List<FlStopInstrItem> stopInstrItems = itemEntry.getValue();
			FlStopInstrItem flsiItem = (FlStopInstrItem) stopInstrItems.get(0).cloneEntity(); 
			flsiItem.setFlFulfilmentItem((FlFulfilmentItem)flsiItem.getFlFulfilmentItem().cloneEntity());
			flsiItem.setParent(true);
			TreeNode childItemNode = new DefaultTreeNode(flsiItem.getFlFulfilmentItem().getCmItem().getItemId().toString(), flsiItem, getManagedBean().getChargeLineItemRoot());
			
			totalOrderAmt = new BigDecimal(0);
			totalReceiveAmt = new BigDecimal(0);
			totalRecalledAmt = new BigDecimal(0);
			flFulfilmentItemIds = new ArrayList<Long>();
			
			for(FlStopInstrItem chargeLineItem : stopInstrItems) {
				if(flFulfilmentItemIds.contains(chargeLineItem.getFlFulfilmentItem().getFulfilmentItemId())) {
					chargeLineItem.getFlFulfilmentItem().setSumOrderedQty(chargeLineItem.getFlFulfilmentItem().getSumOrderedQty().add(chargeLineItem.getFlFulfilmentItemAddr().getOrderedQty()));
					chargeLineItem.getFlFulfilmentItem().setSumReceivedQty(chargeLineItem.getFlFulfilmentItem().getSumReceivedQty().add(chargeLineItem.getFlFulfilmentItemAddr().getChargeReceivedQty()));
					chargeLineItem.getFlFulfilmentItem().setSumRecalledQty(chargeLineItem.getFlFulfilmentItem().getSumRecalledQty().add(chargeLineItem.getRecalledQty()));
					chargeLineItem.getFlFulfilmentItem().setSumOrderedAmt(chargeLineItem.getFlFulfilmentItem().getSumOrderedAmt().add(chargeLineItem.getFlFulfilmentItemAddr().getOrderedAmt()));
					chargeLineItem.getFlFulfilmentItem().setSumReceivedAmt(chargeLineItem.getFlFulfilmentItem().getSumReceivedAmt().add(chargeLineItem.getFlFulfilmentItemAddr().getChargeReceivedAmt()));
					chargeLineItem.getFlFulfilmentItem().setSumRecalledAmt(chargeLineItem.getFlFulfilmentItem().getSumRecalledAmt().add(chargeLineItem.getRecalledAmt()));						
				} else {
					flFulfilmentItemIds.add(chargeLineItem.getFlFulfilmentItem().getFulfilmentItemId());
					new DefaultTreeNode(chargeLineItem.getFlFulfilmentItem().getCmItem().getItemId().toString(),chargeLineItem,childItemNode);
					chargeLineItem.getFlFulfilmentItem().setSumOrderedQty(chargeLineItem.getFlFulfilmentItemAddr().getOrderedQty());
					chargeLineItem.getFlFulfilmentItem().setSumReceivedQty(chargeLineItem.getFlFulfilmentItemAddr().getChargeReceivedQty());
					chargeLineItem.getFlFulfilmentItem().setSumRecalledQty(chargeLineItem.getRecalledQty());
					chargeLineItem.getFlFulfilmentItem().setSumOrderedAmt(chargeLineItem.getFlFulfilmentItemAddr().getOrderedAmt());
					chargeLineItem.getFlFulfilmentItem().setSumReceivedAmt(chargeLineItem.getFlFulfilmentItemAddr().getChargeReceivedAmt());
					chargeLineItem.getFlFulfilmentItem().setSumRecalledAmt(chargeLineItem.getRecalledAmt());					
				}
				
				totalOrderAmt=totalOrderAmt.add(chargeLineItem.getFlFulfilmentItemAddr().getOrderedAmt());
				totalReceiveAmt=totalReceiveAmt.add(chargeLineItem.getFlFulfilmentItemAddr().getChargeReceivedAmt());
				totalRecalledAmt=totalRecalledAmt.add(chargeLineItem.getRecalledAmt());
			}			
			flsiItem.getFlFulfilmentItem().setSumOrderedAmt(totalOrderAmt);
			flsiItem.getFlFulfilmentItem().setSumReceivedAmt(totalReceiveAmt);
			flsiItem.getFlFulfilmentItem().setSumRecalledAmt(totalRecalledAmt);
		}
	}
		
	private String getUserRole() {
		if(getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value())) {
			return RoleCode.FL_APPROVER.value();
		} else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
			return RoleCode.BASIC_SUPPLIER_ADMIN.value();
		} else if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
			return RoleCode.MOF_SUPPLIER_ADMIN.value();
		} else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())) {
			return RoleCode.BASIC_SUPPLIER_USER.value();
		} else if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
			return RoleCode.MOF_SUPPLIER_USER.value();
		}
		return null;
	}
	
	private boolean isFlFulfilmentApprover() {
		return getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value());
	}
	
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==1){
			if(null==getManagedBean().getChargeLineItemRoot()){
				 populateChargeLineData();
			}
		}
	}
	
	public String retrieveMofNo(SmSupplier smSupplier) {
		IMofAccountSO mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofAccountSO.class);
		MofAccountReqDTO reqDTO = new MofAccountReqDTO();
		reqDTO.setSupplierId(smSupplier.getSupplierId());
		MofAccountResDTO resDTO = mofAccountSO.retrieveMofNoBySupplierId(reqDTO);
		if(resDTO.isSuccessful() && resDTO.getMofAccount()!=null) {
			return resDTO.getMofAccount().getMofNo();
		}
		return null;
	}
	
}
