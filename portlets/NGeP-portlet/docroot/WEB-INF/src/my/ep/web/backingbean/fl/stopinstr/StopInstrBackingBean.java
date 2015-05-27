/**
 * Copyright © 2014 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.stopinstr;

import java.math.BigDecimal;
import java.sql.Timestamp;
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

import my.ep.biz.service.ct.supplier.IContractSupplierSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
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
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.stopinstr.StopInstrManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.liferay.portal.util.PortalUtil;

// TODO: Auto-generated Javadoc
/**
 * Backingbean for Stop Instruction.
 *
 * @author Kieyun
 * @since 7/7/2014
 */
@ManagedBean(name="stopInstrBB")
@RequestScoped
public class StopInstrBackingBean extends BaseBackingBean<StopInstrManagedBean>{
	private static final String NAV_ORDERNO = "/xhtml/fl/shared/hyperlinkView/orderNoPage/orderNoHyperlink.xhtml";
	
	/**
	 * Gets the faces scopes.
	 *
	 * @return the faces scopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	private IFulfilmentOrderSO getFulfilmentOrderSO() {
		return (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
	}
	
	/**
	 * Save stop instruction.
	 *
	 * @param action the action
	 * navigate to acknowledge page.
	 */
	public String saveStopInstruction(String action) {
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		
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
		
		if(getManagedBean().getPmDigiSign() != null){
			reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
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

	/**
	 * Pre render view.
	 */
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		if(taskId != null) {
			IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
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
			getManagedBean().setSelectedTabIndex(0);
			getManagedBean().setStopInstr(resDTO.getFlStopInstr());
			populateChargeLineData();
			
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			if(resDTO.getFlStopInstr().getFlFulfilmentOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
				new FulfilmentBackingBean(ParameterConst.DOCTYPE_STOP_DELIVERY, resDTO.getFlStopInstr().getFlRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
			} else {
				new FulfilmentBackingBean(ParameterConst.DOCTYPE_STOP_DELIVERY, resDTO.getFlStopInstr().getFlRequest().getCtContract().getContractNo());
			}
			flBB.initializeRemarkAndAttachement(resDTO.getFlStopInstr().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
			if(stopFlData.getErrorId() != null && stopFlData.getErrorId() > 0){
				getManagedBean().setAutoUpdate(Boolean.FALSE);
				getManagedBean().setGfmasError(true);
				FulfilmentReqDTO errReq = new FulfilmentReqDTO();
				errReq.setErrorId(stopFlData.getErrorId());
				FulfilmentResDTO errRes = fulfilmentSO.retrieveErrorContent(errReq);
				getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
			}
		}
	}
	
	/**
	 * Gets the populate reason list based on language and reason category.
	 *
	 * @return the populate reason list
	 */
	public List<SelectItem> getPopulateReasonList() {
		IReasonSO reasonSO = (IReasonSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IReasonSO.class);
		
		List<SelectItem> populateReasonList = new ArrayList<SelectItem>();
		
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
		reqDTO.setReasonCategory(ParameterConst.FULFILMENT_REASON_CODE);
		reqDTO.setSortBy("pmReason.reasonCode");
		ReasonResDTO resDTO = reasonSO.retrieveReasonDescListByCategory(reqDTO);
		getManagedBean().setReasonDescs(resDTO.getReasonDescList());
		List<PmReasonDesc> reasonDescs = resDTO.getReasonDescList();
		for (PmReasonDesc obj : reasonDescs) {
			populateReasonList.add(new SelectItem(obj.getPmReason().getReasonCode(), obj.getReasonDesc()));
		}
		return populateReasonList;
	}
	
	/**
	 * Populate stop instr item.
	 */
	public void populateStopInstrItem() {
		FlStopInstrItem flStopInstrItem = null;
		List<FlStopInstrItem> stopInstrItems = new ArrayList<FlStopInstrItem>();
		for (FlDeliveryAddress flDelAddress : getManagedBean().getStopInstr().getFlRequest().getFlDeliveryAddresses()) {
			for (FlFulfilmentItemAddr itemAddr : flDelAddress.getFlFulfilmentItemAddr()) {
				if(itemAddr.getOrderedQty().subtract(itemAddr.getChargeReceivedQty()).doubleValue()==0) {
					continue;
				}
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
	
	/**
	 * Populate charge line data.
	 */
	public void populateChargeLineData(){
		getManagedBean().setChargeLineItemRoot(new DefaultTreeNode("root", null));
		
		Map<String, List<FlStopInstrItem>> siItemsMap = new HashMap<String, List<FlStopInstrItem>>();
		List<FlStopInstrItem> flStopInstrItemList = null;
		for (FlStopInstrItem flStopInstrItem : getManagedBean().getStopInstr().getStopInstrItems()) {
			if (siItemsMap.containsKey(flStopInstrItem.getFlFulfilmentItem().getItemCode())) {
				flStopInstrItemList = siItemsMap.get(flStopInstrItem.getFlFulfilmentItem().getItemCode());
			} else {
				flStopInstrItemList = new ArrayList<FlStopInstrItem>();
			}
			flStopInstrItemList.add(flStopInstrItem);
			siItemsMap.put(flStopInstrItem.getFlFulfilmentItem().getItemCode(), flStopInstrItemList);
		}
		
		BigDecimal totalOrderAmt = new BigDecimal(0);
		BigDecimal totalReceiveAmt = new BigDecimal(0);
		BigDecimal totalRecalledAmt = new BigDecimal(0);
		List<Long> flFulfilmentItemIds = null;
		
		for(Entry<String, List<FlStopInstrItem>> itemEntry : siItemsMap.entrySet()){
			List<FlStopInstrItem> stopInstrItems = itemEntry.getValue();
			FlStopInstrItem flsiItem = (FlStopInstrItem) stopInstrItems.get(0).cloneEntity(); 
			flsiItem.setFlFulfilmentItem((FlFulfilmentItem)flsiItem.getFlFulfilmentItem().cloneEntity());
			flsiItem.setParent(true);
			TreeNode childItemNode = new DefaultTreeNode(flsiItem.getFlFulfilmentItem().getItemCode(), flsiItem, getManagedBean().getChargeLineItemRoot());
			
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
					new DefaultTreeNode(chargeLineItem.getFlFulfilmentItem().getItemCode(),chargeLineItem,childItemNode);
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
		
	/**
	 * Gets the user role.
	 *
	 * @return the user role
	 */
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
		} else if (getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value())) {
			return RoleCode.GOVT_SELLER.value();		
		}
		return null;
	}
	
	/**
	 * Checks if is fl fulfilment approver.
	 *
	 * @return true, if is fl fulfilment approver
	 */
	private boolean isFlFulfilmentApprover() {
		return getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value());
	}
	
	/**
	 * On tab change.
	 *
	 * @param selectedTabIndex the selected tab index
	 */
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==1){
			if(null==getManagedBean().getChargeLineItemRoot()){
//				 populateChargeLineData();
			}
		}
	}
	
	/**
	 * Retrieve mof no based on supplier id.
	 *
	 * @param smSupplier the sm supplier
	 * @return the string
	 */
	public String retrieveMofNo(SmSupplier smSupplier) {
		if(getManagedBean().getStopInstr().getFlRequest().getCtContract() != null){
			IContractSupplierSO ctSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSupplierSO.class);
			return ctSupplierSO.retrieveSupplierMOFNoByContractId(getManagedBean().getStopInstr().getFlRequest().getCtContract().getContractId());
		}else{
			IMofAccountSO mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofAccountSO.class);
			MofAccountReqDTO reqDTO = new MofAccountReqDTO();
			reqDTO.setSupplierId(smSupplier.getSupplierId());
			MofAccountResDTO resDTO = mofAccountSO.retrieveMofNoBySupplierId(reqDTO);
			if(resDTO.isSuccessful() && resDTO.getMofAccount()!=null) {
				return resDTO.getMofAccount().getMofNo();
			}
		}
		return null;
	}
	
	/**
	 * Page navigation to FLDoc search page in StopInstr.
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
		if(FulfilmentConst.REASON_CODE.equalsIgnoreCase(getManagedBean().getStopInstr().getPmReason().getReasonCode())) {
			if(null == getManagedBean().getStopInstr().getReasonDesc() || getManagedBean().getStopInstr().getReasonDesc().isEmpty()){
				throw new EpException(MessageConst.ERROR_740026);
			}
		}
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)){
			if(flBB.getManagedBean().getRemarkList().get(0).getRemark()==null || flBB.getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{"Remark"});
			}
		}
		if ((outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE) || 
				outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_STOP_FULFILMENT)) 
				||  hasGovtSellerRole()) {
			// Temp setTextBeforeSign : DocNo+SAP
			StringBuilder sb = new StringBuilder();
			sb.append(getManagedBean().getStopInstr().getFlFulfilmentOrder().getDocNo());
			sb.append(getManagedBean().getStopInstr().getFlFulfilmentOrder().getSapOrderNo());
			sb.append(getManagedBean().getButtonAction());
			getManagedBean().setTextBeforeSign(sb.toString());
			// set action
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		}else if (getUserRole().equalsIgnoreCase(RoleCode.BASIC_SUPPLIER_ADMIN.value()) 
				|| getUserRole().equalsIgnoreCase(RoleCode.BASIC_SUPPLIER_USER.value())
						|| getUserRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.value())
								|| getUserRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value())) {
			getManagedBean().getSpkiBackingBean().getChallengeQuestions();
			// set action
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("spki_dlg.show()");
			return null;
		}
		else{
			return saveStopInstruction(outcome);
		}
	}
	
	
	/**
	 * Pre submit stop instruction.
	 *
	 * @param pmDigiSign the pm digi sign
	 * @return the string
	 * @throws Exception the exception
	 */
	public String preSubmitStopInstruction(
			PmDigiSign pmDigiSign) throws Exception {
		this.getManagedBean().setPmDigiSign(pmDigiSign);
		if (pmDigiSign != null) {
			if (getUserRole().equalsIgnoreCase(RoleCode.BASIC_SUPPLIER_ADMIN.value()) 
					|| getUserRole().equalsIgnoreCase(RoleCode.BASIC_SUPPLIER_USER.value())
							|| getUserRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.value())
									|| getUserRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value())) {
				// Temp setTextBeforeSign : DocNo+SAP
				StringBuilder sb = new StringBuilder();
				sb.append(getManagedBean().getStopInstr().getFlFulfilmentOrder().getDocNo());
				sb.append(getManagedBean().getStopInstr().getFlFulfilmentOrder().getSapOrderNo());
				sb.append(getManagedBean().getButtonAction());
				getManagedBean().setTextBeforeSign(sb.toString());
				updateSpkiDigiSignData(getManagedBean().getPmDigiSign(), getManagedBean().getStopInstr().getFlFulfilmentOrder().getDocType(), 
						getManagedBean().getStopInstr().getFlFulfilmentOrder().getFulfilmentOrderId(), getManagedBean().getStopInstr().getFlFulfilmentOrder().getDocNo(), 
						getManagedBean().getTextBeforeSign());
			}
		}
		
		return saveStopInstruction(getManagedBean().getButtonAction());
	}
	
	/**
	 * Enable auto update.
	 */
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}
	
	/**
	 * Checks if is supplier role.
	 *
	 * @return true, if is supplier role
	 */
	private boolean isSupplierRole(){
		if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
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
		pmDigiSign.setOrginalData(textBeforeSign);
		// spki 
		String signedData =  getManagedBean().getSpkiBackingBean().spkiSignTransaction(textBeforeSign);
		pmDigiSign.setSignedData(signedData);
	}
	
	/**
	 * Back to document.
	 *
	 * @return the string
	 */
	public String backToDocument() {
		return getManagedBean().getBackToDocument();
	}

	/**
	 * Navigate to order No Hyper link page ,if doc type is FL.
	 *
	 * @param docType the doc type
	 * @return the string
	 */
	public String navigateDocument(String docType){
		IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setFlOrderId(getManagedBean().getStopInstr().getFlFulfilmentOrder().getFulfilmentOrderId());
		reqDTO.setDocType(docType);
		FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(reqDTO);
		
		FulfilmentOrderBackingBean orderBackingBean = new FulfilmentOrderBackingBean();
		orderBackingBean.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
		orderBackingBean.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
		FacesContext fc = FacesContext.getCurrentInstance();
		orderBackingBean.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());

		return NAV_ORDERNO;
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
	
	private boolean hasGovtSellerRole() {
		 if (getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value()))
				return true;
		 else
		return false;
	}
}
