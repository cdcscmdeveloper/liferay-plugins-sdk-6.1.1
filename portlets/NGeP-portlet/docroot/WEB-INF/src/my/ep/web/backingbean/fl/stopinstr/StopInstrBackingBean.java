/**
 * Copyright © 2014 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.stopinstr;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;

import my.ep.biz.service.ct.supplier.IContractSupplierSO;
import my.ep.biz.service.fl.IFulfilmentSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.authorization.usergroup.IUserGroupSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.general.settings.designatedarea.IDesignatedAreaSO;
import my.ep.biz.service.pm.general.settings.gstitem.IGstItemSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.ct.supplier.ContractSupplierReqDTO;
import my.ep.dto.ct.supplier.ContractSupplierResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupReqDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.pm.general.settings.designatedarea.DesignatedAreaReqDTO;
import my.ep.dto.pm.general.settings.designatedarea.DesignatedAreaResDTO;
import my.ep.dto.pm.general.settings.gstitem.GstItemReqDTO;
import my.ep.dto.pm.general.settings.gstitem.GstItemResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.stopfullfillmentdata.FLStopFullfillmentDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.fl.FlActor;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNote;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNoteItem;
import my.ep.persistence.entity.fl.order.FlStopInstr;
import my.ep.persistence.entity.fl.order.FlStopInstrItem;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.GSLType;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBaseBackingBean;
import my.ep.web.backingbean.fl.cr.ContractRequestBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.backingbean.fl.pr.PurchaseRequestBackingBean;
import my.ep.web.backingbean.sm.applicationView.ViewSuppProfileBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.fl.stopinstr.StopInstrManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.vo.DisciplinaryActionVO;

/**
 * Backingbean for Stop Instruction.
 *
 * @author Kieyun
 * @since 7/7/2014
 */
@ManagedBean(name="stopInstrBB")
@RequestScoped
public class StopInstrBackingBean extends FulfilmentBaseBackingBean<StopInstrManagedBean>{
	private static Logger log = Logger.getLogger(StopInstrBackingBean.class);

	private static final long serialVersionUID = 1L;
	private static final String NAV_PO = "/xhtml/fl/purchaseorder/poBasePage.xhtml";
	private static final String NAV_CO = "/xhtml/fl/contractorder/coBasePage.xhtml";
	public static final String NAV_CR = "/xhtml/fl/contractrequest/crBasePage.xhtml";
	public static final String NAV_PR = "/xhtml/fl/purchaserequest/prBasePage.xhtml";
	
	/**
	 * save,submit Stop Instruction.
	 *
	 * * @param String action 
	 *  <p>submit - save data into database and BPM process will route to the next respective user.
	 * 	<p>save - save data into database.
	 *  navigate to acknowledge page.
	 *  @throws IOException 
	 */
	public String saveStopInstruction(String action) throws IOException {
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		IFulfilmentOrderSO orderSO = null;
		try {
			orderSO =BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);//36386
			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlStopInstr flStopInstr = getManagedBean().getStopInstr();
			
			// 36386 - Start
			IFulfilmentSO flfulfilmentSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentSO.class);
			PmUser approver = null;
			String functionType = getManagedBean().getSearchFunctionType();
			if (getManagedBean().getStopInstr() != null && getManagedBean().getStopInstr().getStopInstrId() != null) {
				FlActor flactor = flfulfilmentSO.retrieveFlActors(ParameterConst.DOCTYPE_STOP_DELIVERY,
						getManagedBean().getStopInstr().getStopInstrId(), RoleCode.FL_APPROVER.toString());
				if (flactor != null && flactor.getUserName() != null) {
					approver = flactor.getUser();
				}
			}
			// 36386 - End
			reqDTO.setStopIns(flStopInstr);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			reqDTO.setToken(token);
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setUserRole(getUserRole());
			reqDTO.setApproverUserList(getManagedBean().getApproverList());
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));

			FulfilmentResDTO resDTO;//36386
			reqDTO.setApprover(getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()));
			if(reqDTO.isApprover() && hasSupplierRole()) {

				//36386-start
				if (functionType != null) { // from maintenance
					if (functionType.equalsIgnoreCase(ParameterConst.ORG_TYPE_PTJ)) {
						reqDTO.setUserRole(RoleCode.FL_APPROVER.value());
						reqDTO.setApprover(true);
					} else if (functionType.equalsIgnoreCase(ParameterConst.ORG_TYPE_SUPPLIER)) {
						reqDTO.setUserRole(getUserSupplierRole());
						reqDTO.setApprover(false);
					}
				} else {
				    if (approver != null || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) { // from task list
				        reqDTO.setUserRole(RoleCode.FL_APPROVER.value());
				        reqDTO.setApprover(true);
				    } else {
				        reqDTO.setUserRole(getUserSupplierRole());
				        reqDTO.setApprover(false);
				    }
				}
				//36386-end
			}

			if(getManagedBean().getPmDigiSign() != null){
				reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			}
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)){
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put(ReportConst.PARAM_DOC_NO, getManagedBean().getStopInstr().getFlFulfilmentOrder().getDocNo());
				paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
				reqDTO.setReportUrl("<a href='"+generateReportURL(ReportConst.FL_NGEP_STOP_DELIVERY,paramMap)+ "'>"+getManagedBean().getStopInstr().getFlFulfilmentOrder().getDocNo()+"</a>");
			}
			if(getManagedBean().isCoGstr() && getManagedBean().isIssuanceCogstrCheck()){
				reqDTO.setCoGstr(true);
			}
			resDTO = orderSO.stopFulfilment(reqDTO);

			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700005;

				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700004;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					messageId = MessageConst.INFO_700008;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
					messageId = MessageConst.INFO_700010;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE) || reqDTO.isApprover()) {
					if(checkTaxInvoice(getManagedBean().getStopInstr().getFlRequest().getFulfilmentReqId())){
						messageId = MessageConst.INFO_745011;
					}else{
						messageId = MessageConst.INFO_745015;
					}
				} 
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_STOP_DELIVERY), resDTO.getFlStopInstr().getStopInstrNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
			if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || getManagedBean().getTaskId() == null) { 
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}else{
				reqDTO.setFlOrderId(resDTO.getFlStopInstr().getFlFulfilmentOrder().getFulfilmentOrderId());
				reqDTO.setStopRequestId(resDTO.getFlStopInstr().getStopInstrId());
				resDTO= orderSO.retrieveStopInstruction(reqDTO);
				String buttonVal = getManagedBean().getButtonVal();
				setStopInstrFields(resDTO);
				getManagedBean().setButtonVal(buttonVal);
				return null;
			}
		}finally{
			orderSO = null;
		}
	}

	/**
	 * Sets the stop instruction fields.
	 *
	 * @param resDTO the new stop instr fields
	 */
	public void setStopInstrFields(FulfilmentResDTO resDTO) {
		getManagedBean().setSelectedTabIndex(0);
		getManagedBean().setStopInstr(resDTO.getFlStopInstr());
		getManagedBean().setButtonVal(BpmOutcomeConst.BPM_STOP_FULFILMENT);
		populateChargeLineData();
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		flBB.getManagedBean().setHideRemarks(false);
		if(resDTO.getFlStopInstr().getFlFulfilmentOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
			new FulfilmentBackingBean(ParameterConst.DOCTYPE_STOP_DELIVERY, resDTO.getFlStopInstr().getFlRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
		} else {
			new FulfilmentBackingBean(ParameterConst.DOCTYPE_STOP_DELIVERY, resDTO.getFlStopInstr().getFlRequest().getCtContract().getContractNo());
		}
		flBB.initializeRemarkAndAttachement(resDTO.getFlStopInstr().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
		
		ISupplierGstSO supplierGstSO = (ISupplierGstSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierGstSO.class);
		SupplierGstReqDTO gstReqDTO = new SupplierGstReqDTO();
		gstReqDTO.setBranchCode(resDTO.getFlStopInstr().getFlRequest().getFlSupplierDtl().getBranchCode());
		gstReqDTO.setSupplierId(resDTO.getFlStopInstr().getFlRequest().getSmSupplier().getSupplierId());
		SupplierGstResDTO gstResDTO = supplierGstSO.retrieveGstInfoBySupplierIdAndBranchCode(gstReqDTO);
		if(gstResDTO.getSmGstSupplier() != null){
			getManagedBean().getStopInstr().setGstEffectiveDate(gstResDTO.getSmGstSupplier().getGstEffDate());
			getManagedBean().getStopInstr().setGstDeclaredDate(gstResDTO.getSmGstSupplier().getGstDeclaredDate());
			getManagedBean().getStopInstr().setGstRegNo(gstResDTO.getSmGstSupplier().getGstRegNo());
		}
		
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
			IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			FLStopFullfillmentDataType stopFlData = super.getBpmTaskDetails(FLStopFullfillmentDataType.class, null);
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			flReqDTO.setFlOrderId(stopFlData.getOrderId());
			flReqDTO.setStopRequestId(stopFlData.getStopRequestId().longValue());
			
			FulfilmentResDTO resDTO = orderSO.retrieveStopInstruction(flReqDTO);
			if(resDTO.getFlStopInstr().getPmReason()==null) {
				resDTO.getFlStopInstr().setPmReason(new PmReason());
			}
			getManagedBean().setSelectedTabIndex(0);
			getManagedBean().setStopInstr(resDTO.getFlStopInstr());
			populateChargeLineData();
			if(this.getManagedBean().getTaskName().equalsIgnoreCase(FLProcessNameConst.REVIEW_STOP_FULFILMENT_REQ_HT)
					|| this.getManagedBean().getTaskName().equalsIgnoreCase(FLProcessNameConst.SUBMIT_STOP_FULFILMENT_REQ_HT)){
				checkCogstr();
			}
			
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.getManagedBean().setHideRemarks(false);
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
				getManagedBean().setGfmasErrorCode(errRes.getErrorCode());
				getManagedBean().setGfmasErrorDesc(MessageUtils.getMessage("ERR-"+errRes.getErrorCode(), new String[]{}, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale()));
				getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
			}
			
			ISupplierGstSO supplierGstSO = (ISupplierGstSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierGstSO.class);
			SupplierGstReqDTO gstReqDTO = new SupplierGstReqDTO();
			gstReqDTO.setBranchCode(resDTO.getFlStopInstr().getFlRequest().getFlSupplierDtl().getBranchCode());
			gstReqDTO.setSupplierId(resDTO.getFlStopInstr().getFlRequest().getSmSupplier().getSupplierId());
			SupplierGstResDTO gstResDTO = supplierGstSO.retrieveGstInfoBySupplierIdAndBranchCode(gstReqDTO);
			if(gstResDTO.getSmGstSupplier() != null){
				getManagedBean().getStopInstr().setGstEffectiveDate(gstResDTO.getSmGstSupplier().getGstEffDate());
				getManagedBean().getStopInstr().setGstDeclaredDate(gstResDTO.getSmGstSupplier().getGstDeclaredDate());
				getManagedBean().getStopInstr().setGstRegNo(gstResDTO.getSmGstSupplier().getGstRegNo());
			}
			
			checkDisciplinaryAction(); //26279
		}else{
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			if(null == getManagedBean().getButtonVal() || !getManagedBean().getButtonVal().equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(false);
			}
		}
	}
	
	// 26279 - start
	/**
	 * Checks Disciplinary Action
	 * 
	 * if true will display Disciplinary Action
	 * 
	 */
	private void checkDisciplinaryAction() {
		if (getManagedBean().getStopInstr().getPmStatus().getStatusId().longValue() == FulfilmentStatusConst.SD_PENDING_APPROVAL
				.longValue()) {
			SmSupplier smSupplier = getManagedBean().getStopInstr().getFlRequest().getSmSupplier();
			SmDisciplinaryAction smDisciplinaryAction = getImposedDisciplinaryAction(smSupplier);
			if (smDisciplinaryAction != null) {
				getManagedBean().setSmDisciplinaryAction(smDisciplinaryAction);
				getManagedBean().setDisciplinaryActionMessage(getDisciplinaryActionMsg(smDisciplinaryAction));
			} else {
				getManagedBean().setDisciplinaryActionMessage(StringUtils.EMPTY);
			}
		}
	}
	// 26279 - End
	
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
				
				flStopInstrItem.setRecalledAmt(flStopInstrItem.getRecalledQty().multiply(itemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
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
			if (siItemsMap.containsKey(flStopInstrItem.getFlFulfilmentItem().getItemCode()+flStopInstrItem.getFlFulfilmentItem().getPmUomId())) {
				flStopInstrItemList = siItemsMap.get(flStopInstrItem.getFlFulfilmentItem().getItemCode()+flStopInstrItem.getFlFulfilmentItem().getPmUomId());
			} else {
				flStopInstrItemList = new ArrayList<FlStopInstrItem>();
			}
			flStopInstrItemList.add(flStopInstrItem);
			siItemsMap.put(flStopInstrItem.getFlFulfilmentItem().getItemCode()+flStopInstrItem.getFlFulfilmentItem().getPmUomId()+flStopInstrItem.getFlFulfilmentItem().getFulfilmentItemId(), flStopInstrItemList); //#26559
		}
		
		BigDecimal totalRecalledAmt = new BigDecimal(0);
		BigDecimal totalRecalledQty = new BigDecimal(0); //26559
		List<String> flFulfilmentItemIds = null;
		
		for(Entry<String, List<FlStopInstrItem>> itemEntry : siItemsMap.entrySet()){
			List<FlStopInstrItem> stopInstrItems = itemEntry.getValue();
			FlStopInstrItem flsiItem = (FlStopInstrItem) stopInstrItems.get(0).cloneEntity(); 
			flsiItem.setFlFulfilmentItem((FlFulfilmentItem)flsiItem.getFlFulfilmentItem().cloneEntity());
			flsiItem.setParent(true);
			TreeNode childItemNode = new DefaultTreeNode(flsiItem.getFlFulfilmentItem().getItemCode()+flsiItem.getFlFulfilmentItem().getFulfilmentItemId(), flsiItem, getManagedBean().getChargeLineItemRoot()); //26559
			
			totalRecalledAmt = new BigDecimal(0);
			totalRecalledQty = new BigDecimal(0);//26559
			
			flFulfilmentItemIds = new ArrayList<String>();
			
			for(FlStopInstrItem chargeLineItem : stopInstrItems) {
				if(flFulfilmentItemIds.contains(chargeLineItem.getFlFulfilmentItem().getFulfilmentItemId()+""+chargeLineItem.getFlFulfilmentItem().getPmUomId())) {
					chargeLineItem.getFlFulfilmentItem().setSumRecalledQty(chargeLineItem.getFlFulfilmentItem().getSumRecalledQty().add(chargeLineItem.getRecalledQty()));
					chargeLineItem.getFlFulfilmentItem().setSumRecalledAmt(chargeLineItem.getFlFulfilmentItem().getSumRecalledAmt().add(chargeLineItem.getRecalledAmt()));						
				} else {
					flFulfilmentItemIds.add(chargeLineItem.getFlFulfilmentItem().getFulfilmentItemId()+""+chargeLineItem.getFlFulfilmentItem().getPmUomId());
					new DefaultTreeNode(chargeLineItem.getFlFulfilmentItem().getItemCode(),chargeLineItem,childItemNode);

					//start -26559
					totalRecalledQty = (chargeLineItem.getFlFulfilmentItem().getSumOrderedQty()!=null? chargeLineItem.getFlFulfilmentItem().getSumOrderedQty(): BigDecimal.ZERO).subtract((chargeLineItem.getFlFulfilmentItem().getSumReceivedQty()!=null? chargeLineItem.getFlFulfilmentItem().getSumReceivedQty(): BigDecimal.ZERO));//28620
					chargeLineItem.getFlFulfilmentItem().setSumRecalledQty(totalRecalledQty);
					//end -26559
					
					chargeLineItem.getFlFulfilmentItem().setSumRecalledAmt(chargeLineItem.getRecalledAmt());					
				}
				
				totalRecalledAmt=totalRecalledAmt.add(chargeLineItem.getRecalledAmt());
			}			
			flsiItem.getFlFulfilmentItem().setSumRecalledAmt(totalRecalledAmt);
		}
	}
		
	/**
	 * Gets the user role.
	 *
	 * @return the user role
	 */
	private String getUserRole() {		
		//33615-start
		if(getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()))
			return RoleCode.FL_APPROVER.value();
		else if(hasSupplierRole())
			return getUserSupplierRole();	
		//33615-end
		else 
			throw new EpException(MessageConst.ERROR_750040);
	}
	
	/**
	 * On tab change.
	 *
	 * @param selectedTabIndex the selected tab index
	 */
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==2){
			if(null==getManagedBean().getChargeLineItemRoot()){
			}
		}
	}
	
	/**
	 * Page navigation to FLDoc search page in StopInstr.
	 *
	 * @return the string
	 * @throws IOException 
	 */
	public String backToPervious() throws PortalException, SystemException, IOException {
		if(getManagedBean().getBackToDocument() !=null){
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.getManagedBean().setRemarkList(getManagedBean().getPreviousRemarkList());
			flBB.getManagedBean().setAttachmentList(getManagedBean().getPreviousAttachmentList());	
			flBB.getManagedBean().setHideRemarks(getManagedBean().isPrevHideRemarks());
			return getManagedBean().getBackToDocument();
		}
		else if(getManagedBean().getBackToNtf() != null){
			FacesContext.getCurrentInstance().getExternalContext().redirect(
					 EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_APPLICATION_URL) + "notifications");
			return null;
		}else{
			return "flDocSearch";
		}
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
		validatePOCOCurrentStatus(outcome); //29870-38104

		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
			if(flBB.getManagedBean().getRemarkList().get(0).getRemark()==null || flBB.getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{"Remark"});
			}
		} else {
			if(getManagedBean().getStopInstr().getPmReason().getReasonCode() == null){
				throw new EpException(MessageConst.ERROR_100022, new String[]{
						ResourceBundleUtils.getMessage("lbl.fl.stop.instuc.reason.justification",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			}
			if(FulfilmentConst.REASON_CODE.equalsIgnoreCase(getManagedBean().getStopInstr().getPmReason().getReasonCode())) {
				if(null == getManagedBean().getStopInstr().getReasonDesc() || getManagedBean().getStopInstr().getReasonDesc().isEmpty()){
					throw new EpException(MessageConst.ERROR_740026);
				}
			}
		}
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_STOP_FULFILMENT) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)){
			if(hasSupplierRole() || getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value())) {
				IFulfilmentSO fulfilmentSO = (IFulfilmentSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentSO.class);
				IUserGroupSO userGroupSO = (IUserGroupSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserGroupSO.class);
				BigDecimal sumItemAmt = fulfilmentSO.getSumTotalItemsAmtByReqId(getManagedBean().getStopInstr().getFlRequest().getFulfilmentReqId());
				UserGroupReqDTO userGroupReqDTO = new UserGroupReqDTO();
				userGroupReqDTO.setGroupId(getManagedBean().getStopInstr().getFlRequest().getPmUserGroup().getUserGroupId());
				userGroupReqDTO.setApprovalLimit(sumItemAmt);
				UserGroupResDTO userGroupResDTO = userGroupSO.retrieveUserGroupUserListByGroupId(userGroupReqDTO);
				if(userGroupResDTO.getUserGroupUserList().isEmpty()){
					//#24764
					throw new EpException(MessageConst.ERROR_740058);
				}
				
				PmUser approver = null;
				
				// 36386-start
				String functionType = getManagedBean().getSearchFunctionType();
				IFulfilmentSO flfulfilmentSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentSO.class);
				PmUser approverCreatedSdi = null;
				if (getManagedBean().getStopInstr() != null && getManagedBean().getStopInstr().getStopInstrId() != null) {
					FlActor flactor = flfulfilmentSO.retrieveFlActors(ParameterConst.DOCTYPE_STOP_DELIVERY,
							getManagedBean().getStopInstr().getStopInstrId(), RoleCode.FL_APPROVER.toString());
					if (flactor != null && flactor.getUserName() != null) {
						approverCreatedSdi = flactor.getUser();
					}
				}
				if (functionType != null) { // from maintenance
					if (functionType.equalsIgnoreCase(ParameterConst.ORG_TYPE_PTJ)) {
						approver = getFacesScopes().getUserSessionBean();
					} else if (functionType.equalsIgnoreCase(ParameterConst.ORG_TYPE_SUPPLIER)) {
						approver = fulfilmentSO.retrieveFlActors(
								getManagedBean().getStopInstr().getFlRequest().getDocType(),
								getManagedBean().getStopInstr().getFlRequest().getFulfilmentReqId(),
								RoleCode.FL_APPROVER.value()).getUser();
					}
				} else {
					if (approverCreatedSdi != null || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) { // from task list
						approver = getFacesScopes().getUserSessionBean();
					} else {
						approver = fulfilmentSO.retrieveFlActors(
								getManagedBean().getStopInstr().getFlRequest().getDocType(),
								getManagedBean().getStopInstr().getFlRequest().getFulfilmentReqId(),
								RoleCode.FL_APPROVER.value()).getUser();
					}
				}
				// 36386-end
				
				List<PmUser> approverUserList = sortApproverUserList(userGroupResDTO.getUserGroupUserList(), approver, getManagedBean().getStopInstr().getFlRequest().getPmUserGroup().getUserGroupId());
				getManagedBean().getApproverList().addAll(approverUserList);
			}
		}

		if(!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
			if(hasSupplierRole() && !hasGovtSellerRole()) { // Supplier stop delivery
				getManagedBean().getSpkiBackingBean().getChallengeQuestions();
				// set action
				getManagedBean().setButtonAction(outcome);
				getManagedBean().setAutoUpdate(Boolean.FALSE);
				RequestContext.getCurrentInstance().execute("spki_dlg.show()");
				return null;
			} else {
				if(getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()) && 
					(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_STOP_FULFILMENT))){
					if(!getManagedBean().isIssuanceCogstrCheck() && getManagedBean().isCoGstr()){
						getManagedBean().setSelectedTabIndex(0);
						throw new EpException(MessageConst.ERROR_745078);
					}
					
					IPersonnelSO personnelSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);
					PersonnelReqDTO personnelReqDTO = new PersonnelReqDTO();
					personnelReqDTO.setSmSupplierId(getManagedBean().getStopInstr().getFlRequest().getSmSupplier().getSupplierId());
					PersonnelResDTO personnelResDTO = personnelSO.retrieveLatestActivePersonnelBySupplierId(personnelReqDTO);
					
					if(personnelResDTO.getSmPersonnelList().isEmpty()){
						throw new EpException(MessageConst.ERROR_740020, new String[]{ResourceBundleUtils.getMessage("lbl.txt.supplierUser",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
					}
				}
				// set action
				setTextBeforeSign(outcome);
				getManagedBean().setButtonAction(outcome);
				getManagedBean().setAutoUpdate(Boolean.FALSE);
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
				return null;
			}
		} else {
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
	public String preSubmitStopInstruction(PmDigiSign pmDigiSign) throws Exception {
		getManagedBean().setPmDigiSign(pmDigiSign);
		if (pmDigiSign != null) {
			if(hasSupplierRole() && !hasGovtSellerRole()) {
				setTextBeforeSign(getManagedBean().getButtonAction());
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
		Timestamp date = new Timestamp(System.currentTimeMillis());
		pmDigiSign.setDocId(new BigDecimal(docId));
		pmDigiSign.setDocNo(docNumber);
		pmDigiSign.setDocType(docType);
		pmDigiSign.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
		pmDigiSign.setCreatedDate(date);
		pmDigiSign.setOriginalData(textBeforeSign);
		// spki 
		pmDigiSign.setSpkiSignedData(getManagedBean().getSpkiBackingBean().spkiSignTransaction(textBeforeSign));
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
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setFlOrderId(getManagedBean().getStopInstr().getFlFulfilmentOrder().getFulfilmentOrderId());
		reqDTO.setDocType(docType);
		FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(reqDTO);
		
		FulfilmentOrderBackingBean orderBackingBean = new FulfilmentOrderBackingBean();
		orderBackingBean.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
		boolean isFactoredSelected=orderBackingBean.getManagedBean().getFulfilmentOrder().isFactored();
		if(isFactoredSelected){
			orderBackingBean.getManagedBean().setFactoringCheck("true");
		} else {
			orderBackingBean.getManagedBean().setFactoringCheck("false");
		}
		orderBackingBean.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
		FacesContext fc = FacesContext.getCurrentInstance();
		orderBackingBean.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
		orderBackingBean.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());
		orderBackingBean.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
		orderBackingBean.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
		orderBackingBean.getManagedBean().setHyperLinkPage(true);
		orderBackingBean.getManagedBean().setSupplier(hasSupplierRole());
		orderBackingBean.getManagedBean().setPrevHideRemarks(flBB.getManagedBean().isHideRemarks());
		flBB.getManagedBean().setHideRemarks(true);
		flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
		//#34686 start
		orderBackingBean.getManagedBean().setAttachment(resDTO.getFlAttachment());
		//#34686 end
		flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
			return NAV_PO;
		}else if (docType.equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
			return NAV_CO;
		}
		return null;
		
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
	 * Check cogstr.
	 */
	public void checkCogstr(){
		IDesignatedAreaSO designatedAreaSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDesignatedAreaSO.class);
		IGstItemSO gstItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IGstItemSO.class);

		GstItemReqDTO gstItemReqDTO = new GstItemReqDTO();
		GstItemResDTO gstItemResDTO = null;
		for(FlFulfilmentNote fulfilmentNote : getManagedBean().getStopInstr().getFlRequest().getFlFulfilmentNotes()) {
			if(fulfilmentNote.getRecordStatusFlg()==DbValueConst.RECORD_STATUS_ACTIVE){
				gstItemReqDTO.setG2g(false);
				if(getManagedBean().getStopInstr().getFlRequest().getProcurementType()!=null) {
					if(getManagedBean().getStopInstr().getFlRequest().getProcurementType().getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
						gstItemReqDTO.setG2g(true);
					}
				}
				gstItemReqDTO.setGstEffDate(fulfilmentNote.getGstEffectiveDate());
				gstItemReqDTO.setActualReceivedDate(fulfilmentNote.getReceivedDate());
				gstItemReqDTO.setProcurementCatCode(ParameterConst.PROCUREMENT_TYPE_CODE_SUPPLY);
				gstItemReqDTO.setGstRate(getManagedBean().getStopInstr().getFlRequest().getGstRate());
				
				if(getManagedBean().getStopInstr().getFlRequest().getProcurementTypeCategory()!=null) {
					gstItemReqDTO.setProcurementCatCode(getManagedBean().getStopInstr().getFlRequest().getProcurementTypeCategory().getParameterCode());
				}
				if(getManagedBean().getStopInstr().getFlRequest().getCtContract()!=null) {
					gstItemReqDTO.setSvcCharge(getManagedBean().getStopInstr().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().isServiceTax());
					gstItemReqDTO.setLoaSignedDate(getManagedBean().getStopInstr().getFlRequest().getCtContract().getLoa().getAckDate());
				}
				for(FlFulfilmentNoteItem frnItem : fulfilmentNote.getFlFulfilmentNoteItem()){
					
					gstItemReqDTO.setGst(frnItem.getFlDelOrderItem().getFlFulfilmentItemAddr().isGst());
					gstItemReqDTO.setRequestType(getManagedBean().getStopInstr().getFlRequest().getDocType());
					// default to PCA to PCA
					gstItemReqDTO.setGslTypeCode(GSLType.PCA_TO_PCA.value());

					// Set GSL Type
					DesignatedAreaReqDTO daReqDTO = new DesignatedAreaReqDTO();
					DesignatedAreaResDTO daResDTO = new DesignatedAreaResDTO();
					daReqDTO.setFromAddressId(getManagedBean().getStopInstr().getFlRequest().getFlSupplierDtl().getFlAddress().getAddressId());
					daReqDTO.setToAddressId(fulfilmentNote.getFlDeliveryOrder().getFlDeliveryAddress().getFlAddress().getAddressId());
					daReqDTO.setActualReceivedDate(fulfilmentNote.getReceivedDate());
					daResDTO = designatedAreaSO.retrieveGSLParam(daReqDTO);
					if(daResDTO.isSuccessful()) {
						gstItemReqDTO.setGslTypeCode(daResDTO.getGslParam().getParameterCode());
					}

					gstItemResDTO = gstItemSO.retrieveGSTFlag(gstItemReqDTO);
					/*if(gstItemResDTO.isCoGSTRFlag()){
						getManagedBean().setCoGstr(true);
						break;
					}else{
						getManagedBean().setCoGstr(false); 
					}*/
					
					if(!gstItemResDTO.isCoGSTRFlag()){
						getManagedBean().setCoGstr(false);
						break;
					}else{
						getManagedBean().setCoGstr(true); 
					}
				}
			}
		}
	}
	
	/**
	 * Navigate to view page based on dco type
	 *
	 * @return the string
	 */
	public String navigateToFlRequest(){
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		if(getManagedBean().getStopInstr().getFlRequest().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_REQUEST)){
			IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setFlRequestId(getManagedBean().getStopInstr().getFlRequest().getFulfilmentReqId());
			reqDTO.setFlRequestLink(true);
			FulfilmentResDTO resDTO = requestSO.retrieveFulfilmentRequest(reqDTO);
			FacesContext fc = FacesContext.getCurrentInstance();
			ContractRequestBackingBean crBB = new ContractRequestBackingBean();
			crBB.renewSesionObject();
			crBB = new ContractRequestBackingBean();
			crBB.getManagedBean().setSelectedTabIndex(0);
			crBB.getManagedBean().setDocProcess(getManagedBean().getStopInstr().getFlRequest().getDocType());
			crBB.getManagedBean().setBackToDocument(fc.getViewRoot().getViewId());
			crBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
			crBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());	
			crBB.getManagedBean().setHyperLinkPage(true);
			crBB.getManagedBean().setPrevHideRemarks(flBB.getManagedBean().isHideRemarks());
			flBB.getManagedBean().setHideRemarks(true);
			crBB.setFulfilmentRequestField(resDTO);
			crBB.initializeApproverGroup();
			crBB.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			crBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			crBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			IContractSupplierSO contractSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSupplierSO.class);
			ContractSupplierReqDTO contractSupplierReqDTO = new ContractSupplierReqDTO();
			if(null != resDTO.getFlFulfilmentRequest()){
				if(resDTO.getFlFulfilmentRequest().getCtContract().isFactored()){
					Long supplieContractId=resDTO.getFlFulfilmentRequest().getCtContract().getContractId();
					contractSupplierReqDTO.setContractId(supplieContractId);
					ContractSupplierResDTO contractSupplierResDTO = contractSupplierSO.retrieveFactoringByContract(contractSupplierReqDTO);
					if(contractSupplierResDTO.getFactoring() != null){
						resDTO.getFlFulfilmentRequest().getCtContract().setCtFactoring(contractSupplierResDTO.getFactoring());
						crBB.getManagedBean().setFactored(true);
						if(contractSupplierResDTO.getFactoring().getFinancialOrg() != null){
							crBB.getSelectedFactoringCompDtl(contractSupplierResDTO.getFactoring());
						}
					}else{
						crBB.getManagedBean().setFactored(false);
					}
				}
			}
			
			for(CtAgency agency : crBB.getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtAgencies()){
				if(agency.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE || 
						(agency.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_SUSPENDED && agency.getEffDate().after(new Date()))) {
					crBB.getManagedBean().getAgentOrgProfileList().add(agency.getOrgProfile().getOrgProfileId());
					if(agency == crBB.getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtAgencies().get(0)){
						crBB.getManagedBean().setOrgProfileIds(agency.getOrgProfile().getOrgProfileId().toString());
					}else{
						crBB.getManagedBean().setOrgProfileIds(crBB.getManagedBean().getOrgProfileIds() + "," +agency.getOrgProfile().getOrgProfileId().toString());
					}
				}
			}
			crBB.setFulfilmentItemAddr(crBB.getManagedBean().getFlRequest());
			crBB.getManagedBean().setMinistryProfileId(retrieveMinistryOrgProfile(
			getManagedBean().getStopInstr().getFlRequest().getIssuedOrgProfile().getOrgProfileId()).getOrgProfileId());
			return NAV_CR;
		}if(getManagedBean().getStopInstr().getFlRequest().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_REQUEST)){
			IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setFlRequestId(getManagedBean().getStopInstr().getFlRequest().getFulfilmentReqId());
			reqDTO.setFlRequestLink(true);
			FulfilmentResDTO resDTO = requestSO.retrieveFulfilmentRequest(reqDTO);
			FacesContext fc = FacesContext.getCurrentInstance();
			
			PurchaseRequestBackingBean prBB = new PurchaseRequestBackingBean();
			prBB.initializePRBeans();
			prBB.getManagedBean().setSubmitButtonStatus(false);
			prBB.getManagedBean().setDocProcess(getManagedBean().getStopInstr().getFlRequest().getDocType());
			prBB.getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			prBB.getManagedBean().setBackToDocument(fc.getViewRoot().getViewId());
			prBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
			prBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());					
			prBB.getManagedBean().setHyperLinkPage(true);
			prBB.getManagedBean().setPrevHideRemarks(flBB.getManagedBean().isHideRemarks());
			flBB.getManagedBean().setHideRemarks(true);
			prBB.setFulfilmentRequestField(resDTO);
			prBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			prBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);

			return NAV_PR;
		}
		return null;
	}
	
	/**
	 * Sets the text before sign.
	 *
	 * @param outcome the new text before sign
	 */
	private void setTextBeforeSign(String outcome) {
		StringBuffer sb = new StringBuffer();
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_STOP_FULFILMENT)) {
			sb.append(getManagedBean().getStopInstr().getFlFulfilmentOrder().getDocNo()+"|");
			sb.append(ParameterConst.DOCTYPE_STOP_DELIVERY+"|");
			sb.append(getFacesScopes().getUserSessionBean().getIdentificationNo());
		} else {
			sb.append(getManagedBean().getStopInstr().getFlFulfilmentOrder().getDocNo()+"|");
			sb.append(getManagedBean().getStopInstr().getStopInstrNo()+"|");
			sb.append(outcome+"|");
			sb.append(getFacesScopes().getUserSessionBean().getIdentificationNo());
		}
		getManagedBean().setTextBeforeSign(sb.toString());
	}
	
	/**
	 * Retrieve stop instruction by stop instruction id and display in UI
	 *
	 */
	public void viewStopInstruction() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String stopInstrId = originalServletRequest.getParameter("stopInstructionId");
		IFulfilmentOrderSO orderSO = null;
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		if(stopInstrId!=null) {
			reqDTO.setStopRequestId(Long.valueOf(stopInstrId));
			reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			reqDTO.setDocType(ParameterConst.DOCTYPE_STOP_DELIVERY);
			renewSesionObject();
			try {
				orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
				resDTO = orderSO.retrieveStopInstruction(reqDTO);
			} catch (EpException e) {
				handleException(e);
				return;
			}finally{
				orderSO = null;
			}
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			getManagedBean().setSelectedTabIndex(0);
			getManagedBean().setStopInstr(resDTO.getFlStopInstr());
			getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);//35392
			getManagedBean().setBackToDocument(null);
			getManagedBean().setBackToNtf("notifications");
			getPopulateReasonList();
			populateChargeLineData();
			flBB.getManagedBean().setHideRemarks(true);
			flBB.initializeRemarkAndAttachement(resDTO.getFlStopInstr().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "siBasePage?faces-redirect=true");
		}
	}
	
	/**
	 * Check tax invoice.
	 *
	 * @param reqId the req id
	 * @return true, if successful
	 */
	private boolean checkTaxInvoice(Long reqId){
		IFulfilmentSO fulfilmentSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentSO.class);
		return fulfilmentSO.checkTaxInvoice(reqId);
	}

	// 26279 - start
	/**
	 * View list of Disciplinary Action
	 */
	public void viewDisciplinaryAction() {
		SmSupplier supplier;

		if (getManagedBean().getStopInstr().getFlRequest().getSmSupplier() != null) {
			supplier = getManagedBean().getStopInstr().getFlRequest().getSmSupplier();
			supplier.setFlagSupplier(true);
			supplier.setFlagDASuppProfile(true);
			
			List<DisciplinaryActionVO> disciplinaryActionVO = ViewSuppProfileBackingBean
					.getDisciplinaryActionVO(supplier);
			getManagedBean().setDisciplinaryActionVO(disciplinaryActionVO);
			RequestContext.getCurrentInstance().execute("disciplinaryActionHistoryDlg.show();");
		}
	}

	// 26279 - end
	
	//29870-start
	private void validatePOCOCurrentStatus(String outcome) {//38104
		Boolean allowToProceed = true;
		IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = null;
		
		reqDTO.setFlOrderId(getManagedBean().getStopInstr().getFlFulfilmentOrder().getFulfilmentOrderId());
		reqDTO.setDocType(getManagedBean().getStopInstr().getFlFulfilmentOrder().getDocType());
		// 36247-Start
		reqDTO.setTaskDocType(ParameterConst.DOCTYPE_STOP_DELIVERY);
		reqDTO.setActionCode(FulfilmentConst.STOP_FULFILMENT);
		// 36247-End
		
		//38104-Start
		if (getManagedBean().getTaskId()==null && outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_STOP_FULFILMENT))
			reqDTO.setFulfilmentOrderId(getManagedBean().getStopInstr().getFlFulfilmentOrder().getFulfilmentOrderId());
		FulfilmentResDTO resDTOSdi = orderSO.retrieveStopInstructionByOrder(reqDTO);
		if (resDTOSdi.getFlStopInstr() != null) {
			throw new EpException(MessageConst.ERROR_740071);
		}
		//38104-End
		resDTO= orderSO.validatePOCOCurrentStatus(reqDTO);
		
		allowToProceed = resDTO.isFlag();		
		if(!allowToProceed){
			throw new EpException(MessageConst.ERROR_740069);
		}	
	}		
	//29870-end
	//#21416 start
	public String filterItemName(String itemName) {
		if(this.getManagedBean().getStopInstr() != null){
			if(this.getManagedBean().getStopInstr().getFlRequest().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_REQUEST)){
				String filteredItemName = filterItemNameNA(itemName);
				return filteredItemName;
			}
		}
		return itemName;		
	}
	//#21416 end
}
