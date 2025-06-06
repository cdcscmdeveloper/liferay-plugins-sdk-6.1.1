/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.invoice;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.supplier.IContractSupplierSO;
import my.ep.biz.service.fl.IFulfilmentSO;
import my.ep.biz.service.fl.adjustment.IAdjustmentSO;
import my.ep.biz.service.fl.audit.IAuditFulfilmentSO;
import my.ep.biz.service.fl.frn.IReceivedNoteSO;
import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.payment.IPaymentMatchSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.fee.settings.IFeeExemptionSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.AuditFLConst;
import my.ep.constant.fl.FulfilmentConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.supplier.ContractSupplierReqDTO;
import my.ep.dto.ct.supplier.ContractSupplierResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.fl.audit.AuFLReqDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionReqDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.invoice.FLInvoiceDataType;
import my.ep.jaxb.fl.maintenace.cancel.codata.FLCOCancelReqType;
import my.ep.jaxb.fl.maintenace.cancel.invoice.FLInvoiceCancelReqType;
import my.ep.jaxb.fl.maintenace.modification.invoice.FLInvoiceModificationReqType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.fl.FlActor;
import my.ep.persistence.entity.fl.FlAddress;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.FlWorkflowStatus;
import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.persistence.entity.fl.audit.FlAuditTrail;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNote;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNoteItem;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.payment.FlInvoice;
import my.ep.persistence.entity.fl.payment.FlInvoiceItem;
import my.ep.persistence.entity.fl.payment.FlInvoiceItemSummary;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.GSFType;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.common.CommonBackingBean;
import my.ep.web.backingbean.ct.ContractDetailsBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBaseBackingBean;
import my.ep.web.backingbean.fl.audit.AuditFulfilmentBackingBean;
import my.ep.web.backingbean.fl.deliveryorder.DeliveryOrderBackingBean;
import my.ep.web.backingbean.fl.frn.ReceivedNoteBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.backingbean.sm.applicationView.ViewSuppProfileBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.fl.invoice.InvoiceManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.DisciplinaryActionVO;
import my.ep.web.vo.InvoiceChargeLineVO;
import my.ep.web.vo.TaxInvoiceVO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean for invoice.
 *
 * @author KK LIM
 * @since 30/10/2013
 */

@ManagedBean(name = "invoiceBackingBean")
@ViewScoped
public class InvoiceBackingBean extends FulfilmentBaseBackingBean<InvoiceManagedBean> {
	
	private static final long serialVersionUID = 1L;

	/** The log. */
	private static Logger log = Logger.getLogger(InvoiceBackingBean.class);
	
	/** The class name. */
	private String className="@(.myInvClassName)";
	
	/** The fl invoice data model. */
	private IPagingDataModel<FlInvoice> flInvoiceDataModel = new FlInvoicePagingDataModel();
	private List<InvoiceChargeLineVO> emptyTableList;
	
	/** The Constant NAV_ORDERNO. */
	
	private static final String NAV_PO = "/xhtml/fl/purchaseorder/poBasePage.xhtml";
	private static final String NAV_CO = "/xhtml/fl/contractorder/coBasePage.xhtml";
	
	/** The Constant NAV_INVOICE. */
	private static final String NAV_INVOICE = "/xhtml/fl/invoice/invBasePage.xhtml";
	
	private static final String NAV_DELIVERYORDER_PAGE = "/xhtml/fl/deliveryorder/doBasePage.xhtml";
	private static final String NAV_FULFILMENTNOTE_PAGE= "/xhtml/fl/receivednote/frnBasePage.xhtml";

	/**
	 * Instantiates a new invoice backing bean.
	 */
	public InvoiceBackingBean(){
	}

	/**
	 * save,submit the Invoice
	 * 
	 * * @param String action 
	 *  <p>submit - save data into database and BPM process will route to the next respective user.
	 * 	<p>save - save data into database.
	 *  navigate to acknowledge page.
	 *  @throws IOException 
	 */
	public String submitAction(String action) throws IOException {
		Object token = getFacesScopes().getBPMAuthenticationCtxToken();
		IInvoiceSO invoiceSO = null;
		IAdjustmentSO adjustmentSO = null;
		IFulfilmentRequestSO requestSO = null;
		IFulfilmentSO fulfilmentSO = null;
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			//36754 -start
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
				validateOrderFulfiledAndDOFRNPendingCancellation();
			}
			//36754 -end
			if(!getManagedBean().getInvoice().getFlFulfilmentOrder().isFactored()){//33238
				//32967-start
				if(null != getManagedBean().getAccountNo()){
					getManagedBean().getInvoice().setAccountNo(getManagedBean().getAccountNo());
				}
				//32967-end
			}
			log.info("LOG for 33574:: is Factored submitAction="+ getManagedBean().getInvoice().getFlFulfilmentOrder().isFactored());
			FlInvoice invoice = getManagedBean().getInvoice();
			invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
			adjustmentSO = (IAdjustmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IAdjustmentSO.class);
			requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
			fulfilmentSO = (IFulfilmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentSO.class);
			
			reqDTO.setInvoice(invoice);
			// Generate invoice number
			if(invoice.isBrandNew()) {
				invoice.setInvoiceNo(invoiceSO.generateInvoiceNumber(reqDTO).getInvoiceNo());
				log.info("InvoiceBackingBean.submitAction(). A new invoice number is generated. "+invoice.getInvoiceNo());
			}
			// Need to call this as user may not click on summary menu
			removeDuplicateInvoiceSummaryItem();
			calInvSummary();
			// Check is it DAN exist
			BigDecimal adjustedAmt = new BigDecimal(0);
			for(FlInvoiceItemSummary invItemSumm : getManagedBean().getInvoice().getFlInvoiceItemSummary()){

				adjustedAmt = invItemSumm.getInvAmt().subtract(invItemSumm.getOrderedAmt());
				if(invItemSumm.getRecalledAmt()!=null && invItemSumm.getRecalledAmt().doubleValue()>0) {
					//Bug #21515
					//adjustedAmt = invItemSumm.getInvAmt().subtract(invItemSumm.getOrderedAmt().subtract(invItemSumm.getRecalledAmt()));
			
					BigDecimal receivedItemAmount = new BigDecimal(0);
					for(FlInvoiceItem invItem : invItemSumm.getFlInvoiceItem()){
						if(invItem.getReceivedQty() != null && invItem.getReceivedQty().doubleValue() >=0
								&& invItem.getGstUnitPrice() != null && invItem.getGstUnitPrice().doubleValue() >=0){
								BigDecimal receivedAmt = new BigDecimal(0);
								receivedAmt = invItem.getReceivedQty().multiply(invItem.getGstUnitPrice());
								receivedItemAmount = receivedItemAmount.add(receivedAmt);								
						}
					}
					
					adjustedAmt = invItemSumm.getInvAmt().subtract(receivedItemAmount.setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE,BigDecimal.ROUND_HALF_UP));				
				}

				if(adjustedAmt.doubleValue()>0) {
					reqDTO.setDan(true);
				} else if(adjustedAmt.doubleValue()<0) {
					reqDTO.setCan(true);
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put(ReportConst.PARAM_DOC_NO, "TEMP_DOC_NO");
					paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
					reqDTO.setReportUrl(generateReportURL(ReportConst.FL_NGEP_CREDIT_NOTE,paramMap));
				}
				if(reqDTO.isDan() && reqDTO.isCan()) {
					break;
				}
			}
			List<PmUser> listOfApprover = null;
			if(reqDTO.isDan() || action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) ){ //  #28956 set approverlist for submit wihout DAN;
				BigDecimal sumItemAmt = fulfilmentSO.getSumTotalItemsAmtByReqId(invoice.getFlRequest().getFulfilmentReqId());
				reqDTO.setGroupId(invoice.getFlRequest().getPmUserGroup().getUserGroupId());
				reqDTO.setApproverLimit(sumItemAmt);
				log.info("retrieveApproverListByGroupId reqId ["+invoice.getFlRequest().getFulfilmentReqId()+"] GroupId ["+reqDTO.getGroupId()+"] ApproverLimit ["+reqDTO.getApproverLimit()+"] ");			
				try {
					resDTO = requestSO.retrieveApproverListByGroupId(reqDTO);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(resDTO.getApproverGroupUserList().isEmpty()){
					//#24764
					throw new EpException(MessageConst.ERROR_740058);
				}
				FlActor actor = fulfilmentSO.retrieveFlActors(getManagedBean().getInvoice().getFlRequest().getDocType(), getManagedBean().getInvoice().getFlRequest().getFulfilmentReqId(), RoleCode.FL_APPROVER.value());
				listOfApprover = sortApproverUserList(resDTO.getApproverGroupUserList(),actor.getUser(),getManagedBean().getInvoice().getFlRequest().getPmUserGroup().getUserGroupId());
			}
			reqDTO.setApproverUserList(listOfApprover);
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			//start 21229
			reqDTO.setInvoiceRevise(getManagedBean().isReviseInvoice());
			//end 21229
			reqDTO.setToken(token);
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlDeleteAttachmentList(flBB.getManagedBean().getDeleteAttachments());//29708
			if(flBB.getManagedBean().getRemarkList().size() >0){
				reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			}

			reqDTO.setUserRole(getUserRole());
			reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
			if(null != getManagedBean().getPmDigiSign()){
				reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			}
			if(!(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE))){

				reqDTO.setPmUser(getManagedBean().getPmUser());
				// dunno what's the purpose of the above code
				if(reqDTO.getPmUser()==null) {
					reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
				}
				reqDTO.setInvoice(getManagedBean().getInvoice());

				BigDecimal sumItemAmt = fulfilmentSO.getSumTotalItemsAmtByReqId(invoice.getFlRequest().getFulfilmentReqId());
				reqDTO.setGroupId(invoice.getFlRequest().getPmUserGroup().getUserGroupId());
				reqDTO.setApproverLimit(sumItemAmt);
				try {
					resDTO = requestSO.retrieveApproverListByGroupId(reqDTO);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
					if(resDTO.getApproverGroupUserList() ==null || resDTO.getApproverGroupUserList().isEmpty()){
						//#24764
						throw new EpException(MessageConst.ERROR_740058);
					}
				}
				PmUser approver = null;
				if(hasSupplierRole() && !getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value())) {
					approver = fulfilmentSO.retrieveFlActors(getManagedBean().getInvoice().getFlRequest().getDocType(), getManagedBean().getInvoice().getFlRequest().getFulfilmentReqId(), RoleCode.FL_APPROVER.value()).getUser();
				} else {
					approver = getFacesScopes().getUserSessionBean();
				}
				listOfApprover = sortApproverUserList(resDTO.getApproverGroupUserList(),approver,getManagedBean().getInvoice().getFlRequest().getPmUserGroup().getUserGroupId());
				reqDTO.setApproverUserList(listOfApprover);
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					if(reqDTO.isDan()){
						if(null !=getManagedBean().getInvoice().getFlAdjustments()){
							for(FlAdjustment adjustment: getManagedBean().getInvoice().getFlAdjustments()){
								if(adjustment.getDocType().equals(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE)){
									reqDTO.setDanId(Long.valueOf(adjustment.getAdjustmentId()));
									reqDTO.setDocType(adjustment.getDocType());
									FulfilmentResDTO adjustmentResDTO  = adjustmentSO.retrieveAdjustmentById(reqDTO);
									if(adjustmentResDTO.getAdjustment().getFlWorkflowStatus().getPmStatus().getStatusId().longValue()==FulfilmentStatusConst.DN_APPROVED.longValue()){
										getManagedBean().setDanStatusId(adjustmentResDTO.getAdjustment().getFlWorkflowStatus().getPmStatus().getStatusId().longValue());
										break;
									}
								}
							}
						}
					}
				}
				//33654-Start
				if(reqDTO.isInvoiceRevise() && action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
					reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
				}
				//33654-End
				
				resDTO = invoiceSO.invoiceModification(reqDTO);
				//#35508 fl audit trail - inv modification start
				AuditFulfilmentBackingBean auflBB = new AuditFulfilmentBackingBean();
				if (resDTO.isSuccessful() && auflBB.getAuditFlConfigState()) {
					insertAuditFulfilmentInvoice(action);
				}
				//#35508 fl audit trail - inv modification end
			}else{
				reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
				log.info("LOG for 33574:: is Factored submitAction :before saveInvoice ="+reqDTO.getInvoice().getFlFulfilmentOrder().isFactored());
				resDTO = invoiceSO.saveInvoice(reqDTO);
			}
			if(resDTO.isSuccessful()) {
				String messagePrefix = "";
				String messageId = MessageConst.INFO_700026;
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700025;
				} else if(resDTO.getInvoice()!=null && resDTO.getInvoice().getAp59Status()!=null && 
						resDTO.getInvoice().getAp59Status().getStatusId().longValue()==FulfilmentStatusConst.AP59_PENDING_AP59_DECISION) {
					//28719-Start
					if(!getManagedBean().isSupplementContractComplete()){
						messageId = MessageConst.INFO_745026;
					} else {
						messageId = MessageConst.INFO_700004;
					}
					//28719-End
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					messageId = MessageConst.INFO_745018;
					if(null != getManagedBean().getDanStatusId()){
						if(getManagedBean().getDanStatusId().longValue()==FulfilmentStatusConst.DN_APPROVED.longValue()){
							messageId = MessageConst.INFO_745082;
						}
					}
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					messageId = MessageConst.INFO_700008;
					messagePrefix=ResourceBundleUtils.getMessage("lbl.fl.cancellation.for",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+"  ";
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
					messageId = MessageConst.INFO_700011;
					messagePrefix=ResourceBundleUtils.getMessage("lbl.fl.update.for",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+"  ";
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)){
					messageId = MessageConst.INFO_700043;
					messagePrefix=ResourceBundleUtils.getMessage("lbl.fl.update.for",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+"  ";
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
					messageId = MessageConst.INFO_745017;
					//start 21229
					if(reqDTO.isInvoiceRevise()){
						messageId = MessageConst.INFO_745018;
					}
					//end 21229
				}
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(messageId, 
									new String[]{resDTO.getSupplierInvoiceNo()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, 
									new FacesScopes().getUserLocale())));
				}else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)){
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(messageId, 
									new String[]{resDTO.getSupplierInvoiceNo()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, 
									new FacesScopes().getUserLocale())));
				}else{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_INFO, 
							null,
							MessageUtils.getMessage(messageId, 
									new String[]{messagePrefix+getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_INVOICE), resDTO.getSupplierInvoiceNo()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, 
									new FacesScopes().getUserLocale())));
				}
				
				boolean isPendingAP59Decision = false;
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && resDTO.getInvoice()!=null && resDTO.getInvoice().getAp59Status()!=null && 
						resDTO.getInvoice().getAp59Status().getStatusId().longValue()==FulfilmentStatusConst.AP59_PENDING_AP59_DECISION) {
					isPendingAP59Decision = true;
					// 28719-Start
					if (!getManagedBean().isSupplementContractComplete()) {
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
										MessageConst.INFO_745027, null, MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
					} else {
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
										MessageConst.INFO_745001, null, MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
					}
					// 28719-End
				}
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && reqDTO.isCan()) {
					if(resDTO.getInvoice().getAp59Status()==null || 
							(resDTO.getInvoice().getAp59Status()!=null && resDTO.getInvoice().getAp59Status().getStatusId().longValue()!=FulfilmentStatusConst.AP59_PENDING_AP59_DECISION)) {
						reqDTO.setFlDocNo(resDTO.getFulfilmentNoteNo());
						reqDTO.setAdjustment(resDTO.getAdjustment());//36519
						invoiceSO.sendCANNotification(reqDTO);
					}
				}
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
					if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && !isPendingAP59Decision) {
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put(ReportConst.PARAM_DOC_NO, resDTO.getInvoiceNo());
						paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
						CommonBackingBean commonBackingBean = new CommonBackingBean();
						reqDTO.setInvoiceId(resDTO.getInvId());
						resDTO = invoiceSO.isInvoiceTaxable(reqDTO);
						if(resDTO.isTaxable()){
							commonBackingBean.getManagedBean().setReportName(ReportConst.FL_NGEP_TAX_INVOICE);
						}else{
							commonBackingBean.getManagedBean().setReportName(ReportConst.FL_NGEP_INVOICE);
						}

						commonBackingBean.getManagedBean().setReportDisplayName(ResourceBundleUtils.getMessage("lbl.fl.invoice.supp.invoice",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
						commonBackingBean.getManagedBean().setParamMap(paramMap);
						return CommonUIConst.ACKNOWLEDGE_REPORT_PAGE;
					}

					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					reqDTO.setDocType(ParameterConst.DOCTYPE_INVOICE);
					reqDTO.setFlOrderId(resDTO.getFulfilmentOrderId());
					resDTO = invoiceSO.retrieveInvoiceByFlOrderId(reqDTO);
					getManagedBean().setSelectedTabIndex(0);
					getManagedBean().setInvoice(resDTO.getInvoice());
					flBB.initializeRemarkAndAttachement(resDTO.getInvoice().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
				}
			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
		}finally {
			invoiceSO = null;
			adjustmentSO = null;
			requestSO = null;
			fulfilmentSO = null;
		}
		return null;
	}
	
	/**
	 * Gets the user role based on role code.
	 *
	 * @return the user role
	 */
	private String getUserRole() {		
		if(hasSupplierRole())
			return getUserSupplierRole();	
		else if(getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()))
			return RoleCode.FL_APPROVER.value();		
		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()))
			return RoleCode.BPK_APPROVER.value();
		else 
			throw new EpException(MessageConst.ERROR_750040);
	}

	/**
	 * Pre render view.
	 */
	public void preRenderView() {
		IInvoiceSO invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		if(taskId != null){
			renewSesionObject();
			Object obj = super.getBpmTaskDetails(Object.class, null);
			getManagedBean().setModificationCancel(false);
			
			FinancialOrgReqDTO finOrgReqDTO = new FinancialOrgReqDTO();
			FinancialOrgResDTO finOrgResDTO = null;
			IFinancialOrgSO financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IFinancialOrgSO.class);
			
			if(FLProcessNameConst.ACK_MODIFIED_INVOICE_HT.equals(this.getManagedBean().getTaskName())){
				FLInvoiceModificationReqType invModObj = (FLInvoiceModificationReqType) obj;
				reqDTO.setInvoiceId(Long.valueOf(invModObj.getInvoiceId()));
				resDTO = invoiceSO.retrieveInvoiceByInvoiceId(reqDTO);
				getManagedBean().setInvoice(resDTO.getInvoice());
				getManagedBean().getInvoice().setFlRequest(resDTO.getInvoice().getFlRequest());
				getManagedBean().setFulfilmentRequest(resDTO.getInvoice().getFlRequest());
				getManagedBean().setPmUser(reqDTO.getPmUser());
				getManagedBean().setButtonVal("ACK");
				getManagedBean().setModificationCancel(true);
				if(getManagedBean().getInvoice().getStatus().getStatusId().equals(FulfilmentStatusConst.IN_PENDING_MODIFICATION_APPROVAL)){
					getManagedBean().setReliefDeclarationCheck(true);
				}
				getManagedBean().setDelOrderList(new ArrayList<FlDeliveryOrder>()); 
				for(FlDeliveryOrder flDeliveryOrder : getManagedBean().getInvoice().getFlFulfilmentOrder().getFlDeliveryOrders()) {
					if(flDeliveryOrder.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
						getManagedBean().getDelOrderList().add(flDeliveryOrder);
					}
				}
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				flBB.initializeRemarkAndAttachement(resDTO.getInvoice().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
			}
			else if(FLProcessNameConst.REVIEW_CANCEL_INVOICE_HT.equals(this.getManagedBean().getTaskName())){
				FLInvoiceCancelReqType invCanObj = (FLInvoiceCancelReqType) obj;
				reqDTO.setInvoiceId(Long.valueOf(invCanObj.getInvoiceId()));
				resDTO = invoiceSO.retrieveInvoiceByInvoiceId(reqDTO);
				getManagedBean().setInvoice(resDTO.getInvoice());
				getManagedBean().getInvoice().setFlRequest(resDTO.getInvoice().getFlRequest());
				if(getManagedBean().getInvoice().getStatus().getStatusId().equals(FulfilmentStatusConst.IN_PENDING_CANCEL_APPROVAL)){
					getManagedBean().setReliefDeclarationCheck(true);
				}
				getManagedBean().setDelOrderList(new ArrayList<FlDeliveryOrder>()); 
				for(FlDeliveryOrder flDeliveryOrder : getManagedBean().getInvoice().getFlFulfilmentOrder().getFlDeliveryOrders()) {
					if(flDeliveryOrder.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
						getManagedBean().getDelOrderList().add(flDeliveryOrder);
					}
				}	
				getManagedBean().setFulfilmentRequest(resDTO.getInvoice().getFlRequest());
				getManagedBean().setPmUser(reqDTO.getPmUser());
				getManagedBean().setButtonVal("ACK");
				getManagedBean().setModificationCancel(true);
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				flBB.initializeRemarkAndAttachement(resDTO.getInvoice().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
				
				if(invCanObj.getErrorId() != null && invCanObj.getErrorId() > 0){
					getManagedBean().setAutoUpdate(Boolean.FALSE);
					getManagedBean().setGfmasError(true);
					FulfilmentReqDTO errReq = new FulfilmentReqDTO();
					errReq.setErrorId(invCanObj.getErrorId());
					IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
					FulfilmentResDTO errRes = requestSO.retrieveErrorContent(errReq);
					getManagedBean().setGfmasErrorCode(errRes.getErrorCode());
					getManagedBean().setGfmasErrorDesc(MessageUtils.getMessage("ERR-"+errRes.getErrorCode(), new String[]{}, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale()));
					getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
				}
				
			}else if(FLProcessNameConst.RESUBMIT_AP59_ORDER.equals(this.getManagedBean().getTaskName())){
				IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
				FLCOCancelReqType reqType = (FLCOCancelReqType)obj;

				reqDTO = new FulfilmentReqDTO();
				reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
				Long invoiceId = reqType.getOrderCancelDetails().getInvoiceId();

				if(reqType.getOrderCancelDetails().getErrorIds() != null && Long.parseLong(reqType.getOrderCancelDetails().getErrorIds()) > 0){
					getManagedBean().setAutoUpdate(Boolean.FALSE);
					getManagedBean().setGfmasError(true);
					FulfilmentReqDTO errReq = new FulfilmentReqDTO();
					errReq.setErrorId(Long.parseLong(reqType.getOrderCancelDetails().getErrorIds()));
					FulfilmentResDTO errRes = fulfilmentSO.retrieveErrorContent(errReq);
					getManagedBean().setGfmasErrorCode(errRes.getErrorCode());
					getManagedBean().setGfmasErrorDesc(MessageUtils.getMessage("ERR-"+errRes.getErrorCode(), new String[]{}, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale()));
					getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
				}
				
				invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
				IStatusSO statusSO = (IStatusSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
				reqDTO.setInvoiceId(invoiceId);
				reqDTO.setDocType(ParameterConst.DOCTYPE_AP59);
				resDTO = invoiceSO.retrieveInvoiceByInvoiceId(reqDTO);
				StatusReqDTO statusReqDTO = new StatusReqDTO();
				statusReqDTO.setDocType(FulfilmentConst.DOC_TYPE_AP59);
				statusReqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
				statusReqDTO.setModuleCode(FulfilmentConst.FULFILMENT);
				StatusResDTO statusResDTO = statusSO.retrieveStatusDescList(statusReqDTO);
				InvoiceBackingBean invBB = new InvoiceBackingBean();
				invBB.getManagedBean().setInvoice(resDTO.getInvoice());
				BigDecimal sumOrderedAmt = new BigDecimal(0);
				for(FlFulfilmentItem item : resDTO.getInvoice().getFlRequest().getFlFulfilmentItems()){
					for(FlFulfilmentItemAddr itemAddr : item.getFlFulfilmentItemAddr()){
						sumOrderedAmt = sumOrderedAmt.add(itemAddr.getOrderedAmt());
					}
				}
				invBB.getManagedBean().setSumOrderedAmt(sumOrderedAmt);
				BigDecimal serviceFee = new BigDecimal(0);
				if(resDTO.getInvoice().getFlFulfilmentOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
					serviceFee = new BigDecimal(FulfilmentConst.SERVICE_FEE_ZERO_POINT_EIGHT);
				} else {
					if(getManagedBean().getInvoice().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getServiceFee() != null){
						serviceFee = getManagedBean().getInvoice().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getServiceFee();
					}
				}

				getManagedBean().setServiceCharge(Double.toString(serviceFee.doubleValue()));
				for(PmStatusDesc statusDesc : statusResDTO.getStatusDescList()){
					if(statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.AP59_PENDING_AP59_DECISION)){
						getManagedBean().setReSubmitAp59(true);
					}
					if(statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.AP59_PAYMENT_VIA_AP59)){
						getManagedBean().setBpkDecisionValue(statusDesc.getStatusName());
						getManagedBean().setStatusId(statusDesc.getPmStatus().getStatusId());
					}
				}
				SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
				String expDate=null;
				String effDate=null;
				expDate = DATE_FORMAT.format(invBB.getManagedBean().getInvoice().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getExpDate());
				effDate = DATE_FORMAT.format(invBB.getManagedBean().getInvoice().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getEffDate());
				invBB.getManagedBean().setExpDate(expDate);
				invBB.getManagedBean().setEffDate(effDate);
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				flBB.initializeRemarkAndAttachement(resDTO.getInvoice().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
			}
			else{
				if(!FLProcessNameConst.REVISE_INVOICE.equals(this.getManagedBean().getTaskName())){
					String[] outcome = new String[this.getManagedBean().getOutComes().length + 1];
					outcome[0] = BpmOutcomeConst.BPM_SAVE;
					int i = 1;
					for (String action : this.getManagedBean().getOutComes()) {
						outcome[i++] = action;
					}
					this.getManagedBean().setOutComes(outcome);

				}
				
				FLInvoiceDataType paymentObj = (FLInvoiceDataType)obj;
			
				//29193 all 
				getManagedBean().setGfmasError(false);
				if(paymentObj.getErrorId() != null && !paymentObj.getErrorId().isEmpty() ){
					long errorId = Long.parseLong(paymentObj.getErrorId());
					if(errorId > 0	){
						getManagedBean().setAutoUpdate(Boolean.FALSE);
						getManagedBean().setGfmasError(true);
						FulfilmentReqDTO errReq = new FulfilmentReqDTO();
						errReq.setErrorId(Long.parseLong(paymentObj.getErrorId()));
						IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
						FulfilmentResDTO errRes = requestSO.retrieveErrorContent(errReq);
						getManagedBean().setGfmasErrorCode(errRes.getErrorCode());
						getManagedBean().setGfmasErrorDesc(MessageUtils.getMessage("ERR-"+errRes.getErrorCode(), new String[]{}, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale()));
						getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
					}
				}
				
				IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
				reqDTO.setDocType(ParameterConst.DOCTYPE_INVOICE);
				reqDTO.setFlOrderId(paymentObj.getFlOrderId());
				resDTO = invoiceSO.retrieveInvoiceByFlOrderId(reqDTO);//if save button are exist
				
				//Start  #21229	
				if(FLProcessNameConst.REVISE_INVOICE.equals(this.getManagedBean().getTaskName())){
					getManagedBean().setReviseInvoice(true);
					getManagedBean().setButtonVal(BpmOutcomeConst.BPM_CANCEL);
				}
				
				if(resDTO.getInvoice()!=null && resDTO.getInvoice().getInvoiceId() !=null){
					FulfilmentReqDTO flRemarkDTO = new FulfilmentReqDTO();
					FulfilmentResDTO resRemarkDTO = new FulfilmentResDTO();
					flRemarkDTO.setFlOrderId(paymentObj.getFlOrderId());
					IPaymentMatchSO paymentMatchSO = (IPaymentMatchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IPaymentMatchSO.class);

					resRemarkDTO = paymentMatchSO.retrievePaymentAdviseByOrderId(flRemarkDTO);

					if(resRemarkDTO.getPaymentAdvice()!=null) {
						FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
						FulfilmentResDTO flresDTO = new FulfilmentResDTO();
						flReqDTO.setDocId(resRemarkDTO.getPaymentAdvice().getPaymentAdviceId());
						flReqDTO.setDocType(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
						flReqDTO.setSectionId(FulfilmentConst.REMARK_SECTION_INVOICE_REVISION);
						flresDTO = paymentMatchSO.retrieveRemarkAndAttachmentSection(flReqDTO);
						List remarklist = flresDTO.getRemarkList();
						initializeRemarkRevision(null, remarklist,false);

					}
					
				}
				//End  #21229
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				flBB.getManagedBean().setHideRemarks(false);
				if(resDTO.getInvoice() == null){
					getManagedBean().setInvoice(new FlInvoice());
					getManagedBean().getInvoice().setFlInvoiceItemSummary(new ArrayList<FlInvoiceItemSummary>());
					reqDTO.setFlRequestId(paymentObj.getFlRequestId().longValue());
					reqDTO.setDocType(ParameterConst.DOCTYPE_INVOICE);
					resDTO = fulfilmentSO.retrieveFulfilmentRequestByDocType(reqDTO);
					
					getManagedBean().getInvoice().setFlFulfilmentOrder(resDTO.getFlFulfilmentRequest().getFlFulfilmentOrder());
					getManagedBean().getInvoice().setFlRequest(resDTO.getFlFulfilmentRequest());					
					flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
					getInvoiceTable(resDTO);				
				} else {
					getManagedBean().setInvoice(resDTO.getInvoice());
					flBB.initializeRemarkAndAttachement(resDTO.getInvoice().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
					//27098- start
					if(FLProcessNameConst.REVISE_INVOICE.equals(this.getManagedBean().getTaskName())){
						getManagedBean().getInvoice().getStatus().setStatusId(FulfilmentStatusConst.PA_INVOICE_REVISION);		
					}
					//27098- end
				}
				
				getManagedBean().setChargeLineItemRoot(null);
				getManagedBean().setDocType(resDTO.getFlFulfilmentRequest().getDocType());
				
				getManagedBean().setSelectedTabIndex(0);
				if(getManagedBean().getInvoice().getStatus()==null) {
					IStatusSO statusSO = (IStatusSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
					StatusReqDTO statusReqDTO = new StatusReqDTO();
					statusReqDTO.setStatusId(FulfilmentStatusConst.IN_PENDING_INVOICE);
					getManagedBean().getInvoice().setStatus(statusSO.retrieveStatusByStatusId(statusReqDTO).getPmStatus());
				}
				// If there is only one bank account (under the specific branch), the data for Bank Name/Account No will be defaulted to this value
				List<SelectItem> bankAccountList = getPopulateBankAccountList();
				if((getManagedBean().getInvoice().getAccountNo()==null || getManagedBean().getInvoice().getAccountNo().length()==0) && bankAccountList.size()==1) {
					getManagedBean().getInvoice().setAccountNo(bankAccountList.get(0).getValue().toString());
					bankInfoActionListener();
				}
				
				if(getManagedBean().getInvoice().getFlFulfilmentOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
					new FulfilmentBackingBean(ParameterConst.DOCTYPE_INVOICE, getManagedBean().getInvoice().getFlRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
					if(getManagedBean().getInvoice().getFlFulfilmentOrder().isFactored()){
						getFactoringDtl(); 
					}
					
				} else {
					new FulfilmentBackingBean(ParameterConst.DOCTYPE_INVOICE, getManagedBean().getInvoice().getFlRequest().getCtContract().getContractNo());
					//FD during Invoice Submission
					IContractSupplierSO contractSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSupplierSO.class);
					ContractSupplierReqDTO contractSupplierReqDTO = new ContractSupplierReqDTO();
					if(null != resDTO.getFlFulfilmentRequest()){
						Long supplieContractId=resDTO.getFlFulfilmentRequest().getCtContract().getContractId();
						contractSupplierReqDTO.setContractId(supplieContractId);
						ContractSupplierResDTO contractSupplierResDTO = contractSupplierSO.retrieveFactoringByContract(contractSupplierReqDTO);
						if(contractSupplierResDTO.getFactoring() != null){
							contractSupplierResDTO.getFactoring();
							getManagedBean().getInvoice().getFlFulfilmentOrder().setFactored(DbValueConst.FLAG_YES);
							if(contractSupplierResDTO.getFactoring().getFinancialOrg() != null){
								getManagedBean().getInvoice().getFlFulfilmentOrder().setPmFinancialOrgId(contractSupplierResDTO.getFactoring().getFinancialOrg().getFinancialOrgId());
								getSelectedFactoringCompDtl(contractSupplierResDTO.getFactoring().getFinancialOrg().getFinancialOrgId());
							}
							//Factoring company
							finOrgReqDTO.setFinancialOrgId(getManagedBean().getInvoice().getFlFulfilmentOrder().getPmFinancialOrgId());
							//Start #27880
							String accountNo = contractSupplierResDTO.getFactoring().getBankAccountNo();	

							finOrgReqDTO.setAccountNo(accountNo);
							//finOrgResDTO = financialOrgSO.retriveFinAccDtlByFinOrgId(finOrgReqDTO);
							finOrgResDTO = financialOrgSO.retriveFinAccDtlByFinOrgIdAccNo(finOrgReqDTO);
							//End #27880

							if(finOrgResDTO.isSuccessful()) {
								if(finOrgResDTO.getPmAddressType()!=null){
									getManagedBean().getInvoice().setPaymentBankAddress(convertFinAccAddressToFlAddress(finOrgResDTO.getPmAddressType())); 
								}
									//for contract, factoring acc not editable
								if(finOrgResDTO.getPmFinancialAcc()!=null) {
									getManagedBean().getInvoice().setAccountNo(contractSupplierResDTO.getFactoring().getBankAccountNo()); // account number from ct_factoring
									getManagedBean().getInvoice().setPaymentBank(finOrgResDTO.getPmFinancialAcc().getPmPaymentBankOrg());
									getManagedBean().getInvoice().setPaymentBankName(getManagedBean().getInvoice().getPaymentBank().getFinOrgName());
								}
							}
						} else {
							getManagedBean().getInvoice().getFlFulfilmentOrder().setFactored(DbValueConst.FLAG_NO);
						}
					}	
				}
				getManagedBean().setDelOrderList(new ArrayList<FlDeliveryOrder>()); 
				for(FlDeliveryOrder flDeliveryOrder : getManagedBean().getInvoice().getFlFulfilmentOrder().getFlDeliveryOrders()) {
					if(flDeliveryOrder.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
						getManagedBean().getDelOrderList().add(flDeliveryOrder);
					}
				}			
				getSupplierGst(getManagedBean().getInvoice());
			}
			validateAdvancedPayment();
			
			checkDisciplinaryAction();//26279
		}else{
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			if(null == getManagedBean().getButtonVal() || !getManagedBean().getButtonVal().equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(false);
			}
		}
		
		//28221 - start
		if(getManagedBean().getInvoice().getSupplierSubmittedId() == null){
			getManagedBean().getInvoice().setSupplierSubmittedId(getFacesScopes().getUserSessionBean().getUserId());
		}
		//28221 - end
		
		//24956 - start
		if (getManagedBean().getInvoice().getFlRequest().getCtContract() != null
				&& getManagedBean().getInvoice().getFlRequest().getCtContract().getContractType().
				getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
			getManagedBean().setCtTypeMinistry(true);
		}
		//24956 - end
		
		//check is normal invoice or tax invoice
		reqDTO.setInvoice(getManagedBean().getInvoice());
		resDTO = invoiceSO.isInvoiceTaxable(reqDTO);
		getManagedBean().setTaxInvoice(resDTO.isTaxable());
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "invBasePage.xhtml");
	}
	
	// 26279 - start
	/**
	 * Checks Disciplinary Action
	 * 
	 * if true will display Disciplinary Action
	 * 
	 */
	private void checkDisciplinaryAction() {
		if (getManagedBean().getInvoice().getStatus().getStatusId().longValue() == FulfilmentStatusConst.IN_PENDING_CANCEL_APPROVAL
				.longValue()
				|| getManagedBean().getInvoice().getStatus().getStatusId().longValue() == FulfilmentStatusConst.IN_PENDING_MODIFICATION_APPROVAL
						.longValue()) {
			SmSupplier smSupplier = getManagedBean().getFulfilmentRequest().getSmSupplier();
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
	 * View invoice.
	 */
	public void viewInvoice() {
		IInvoiceSO invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		
		String invoiceId = originalServletRequest.getParameter("invoiceId");
		
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		
		if(invoiceId!=null) {
			reqDTO.setInvoiceId(Long.valueOf(invoiceId));
			reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			renewSesionObject();
			try {
				resDTO = invoiceSO.retrieveInvoiceByInvoiceId(reqDTO);
			} catch (EpException e) {
				handleException(e);
				return;
			}finally{
				invoiceSO = null;
			}
			getManagedBean().setInvoiceId(resDTO.getInvoice().getInvoiceId());
			getManagedBean().setSelectedTabIndex(0);
//			getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
			getManagedBean().setInvoice(resDTO.getInvoice());
			getManagedBean().setReliefDeclarationCheck(true);
			getManagedBean().setFulfilmentRequest(resDTO.getInvoice().getFlRequest());
			if(getManagedBean().getInvoice().getFlFulfilmentOrder().isFactored()){
				getFactoringDtl(); 
			}			
			getManagedBean().setDelOrderList(new ArrayList<FlDeliveryOrder>()); 
			for(FlDeliveryOrder flDeliveryOrder : getManagedBean().getInvoice().getFlFulfilmentOrder().getFlDeliveryOrders()) {
				if(flDeliveryOrder.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
					getManagedBean().getDelOrderList().add(flDeliveryOrder);
				}
			}	
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.getManagedBean().setHideRemarks(true);
			flBB.initializeRemarkAndAttachement(resDTO.getInvoice().getFlWorkflowStatus(), resDTO.getRemarkList(),resDTO.getAttachmentList());
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "invBasePage?faces-redirect=true");
		}
	}	
	
	/**
	 * Validate advanced payment based on deduction amount.
	 */
	private void validateAdvancedPayment() {
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		boolean haveAdvancedPmyt = false;
		BigDecimal deductionAmt = new BigDecimal(0);
		BigDecimal balanceAmt = new BigDecimal(0);
		BigDecimal perc = new BigDecimal(100);
		Double newTotalAmt = new Double(0);
		BigDecimal percAmt = new BigDecimal(0);
		BigDecimal contractValue = new BigDecimal(0);
		if(getManagedBean().getInvoice().getFlRequest().getCtContract() != null){
			ContractReqDTO contractReqDTO = new ContractReqDTO();
			contractReqDTO.setContractVerId(getManagedBean().getInvoice().getFlRequest().getCtContract().getLatestContractVer().getContractVerId());
			contractReqDTO.setAsAtDate(getManagedBean().getInvoice().getFlRequest().getSubmissionDate());
			contractValue = contractSO.retrieveContractAmount(contractReqDTO).getCtContractAmount().getContractAmount();
			for(CtDeduction deduction : getManagedBean().getInvoice().getFlRequest().getCtContract().getLatestContractVer().getCtDeductions()){
				if(deduction.getDeductionType().getParameterCode().equalsIgnoreCase(ParameterConst.DEDUCTION_TYPE_ADVANCE_PAYMENT)){
					haveAdvancedPmyt = true;
					deductionAmt = deductionAmt.add(new BigDecimal(deduction.getAmount()));
					break;
				}
			}

			if(haveAdvancedPmyt){
				ContractReqDTO ctReqDTO = new ContractReqDTO();
				ctReqDTO.setContractId(getManagedBean().getInvoice().getFlRequest().getCtContract().getContractId());
				
				ContractResDTO ctRes = contractSO.calAdvancePaymentTracking(ctReqDTO);
				if(deductionAmt!=null && ctRes.getPaidAdvancePaymentAmt()!=null) {
					balanceAmt = deductionAmt.subtract(ctRes.getPaidAdvancePaymentAmt());
				}
				
				if(balanceAmt.doubleValue() > 0){
					newTotalAmt = ctRes.getPaidInvoiceAmt().doubleValue() / (contractValue.doubleValue());
					percAmt =	new BigDecimal(newTotalAmt).multiply(perc);	
					if(contractValue.doubleValue() <= FulfilmentConst.CONTRACT_VALUE_500K){
						if(percAmt.doubleValue() >= FulfilmentConst.PERC_50){
							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage(FacesMessage.SEVERITY_WARN, null, MessageUtils.getMessage(MessageConst.INFO_745008,
											new String[]{getManagedBean().getInvoice().getFlRequest().getCtContract().getContractNo(),FulfilmentConst.PERC_50.toString()},
											MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
						}
					}else if(contractValue.doubleValue() >= FulfilmentConst.CONTRACT_VALUE_500K){
						if(percAmt.doubleValue() >= FulfilmentConst.PERC_90){
							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage(FacesMessage.SEVERITY_WARN, null, MessageUtils.getMessage(MessageConst.INFO_745008,
											new String[]{getManagedBean().getInvoice().getFlRequest().getCtContract().getContractNo(),FulfilmentConst.PERC_90.toString()},
											MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
						}
					}
				}
			}
		}
	}

	private void getInvoiceTable(FulfilmentResDTO resDTO){

		IReceivedNoteSO receivedNoteSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IReceivedNoteSO.class);
		IFulfilmentOrderSO orderSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setFlRequestId(getManagedBean().getInvoice().getFlRequest().getFulfilmentReqId());
		FulfilmentResDTO frnNoteRes = receivedNoteSO.retrieveReceivedNoteByReqId(reqDTO);
		Map<Long, FlInvoiceItemSummary> itemMapLv1 = new HashMap<Long, FlInvoiceItemSummary>();
		FlInvoiceItemSummary invItemSummary = null;
		for(FlFulfilmentNote fulfilmentNote : frnNoteRes.getFulfilmentNoteList()){
			for(FlFulfilmentNoteItem noteItem : fulfilmentNote.getFlFulfilmentNoteItem()){
				if(noteItem.getReceivedQty().doubleValue() == 0){
					continue;
				}
					String key = noteItem.getGstUnitPrice().toString()+noteItem.getGstFlag().getParameterCode();
					if(itemMapLv1.containsKey(noteItem.getFlItem().getFulfilmentItemId())){
						FlInvoiceItemSummary extItemSumm = itemMapLv1.get(noteItem.getFlItem().getFulfilmentItemId());
						boolean keyMatch = false;
						for(FlInvoiceItem invItem : extItemSumm.getFlInvoiceItem()){
							if(key.equalsIgnoreCase(invItem.getGstUnitPrice().toString()+invItem.getGstFlag().getParameterCode())){
								invItem.setReceivedQty(invItem.getReceivedQty().add(noteItem.getReceivedQty()));
								invItem.setReceivedAmt(invItem.getReceivedAmt().add(noteItem.getReceivedAmt()));
								invItem.setInvAmt(invItem.getReceivedAmt());
								invItem.setDeliveryAmt(invItem.getDeliveryAmt().add(noteItem.getFlDelOrderItem().getDeliveredAmt()));
								invItem.setDeliveryQty(invItem.getDeliveryQty().add(noteItem.getFlDelOrderItem().getDeliveredQty()));
								keyMatch = true;
								break;
							}
						}
						if(!keyMatch){
							FlInvoiceItem invItem = new FlInvoiceItem();
							invItem.setReceivedQty(noteItem.getReceivedQty());
							invItem.setReceivedAmt(noteItem.getReceivedAmt());
							invItem.setInvAmt(noteItem.getReceivedAmt());
							invItem.setGstAmount(noteItem.getGstAmount());
							invItem.setGstUnitPrice(noteItem.getGstUnitPrice());
							invItem.setDeliveryAmt(noteItem.getFlDelOrderItem().getDeliveredAmt());
							invItem.setDeliveryQty(noteItem.getFlDelOrderItem().getDeliveredQty());
							invItem.setGstFlag(noteItem.getGstFlag());
							invItem.setFlInvoiceItemSummary(extItemSumm);

							extItemSumm.getFlInvoiceItem().add(invItem);
						}
					}else{
						FulfilmentReqDTO siReqDTO = new FulfilmentReqDTO();
						invItemSummary = new FlInvoiceItemSummary();
						invItemSummary.setOrderedAmt(BigDecimal.ZERO);
						invItemSummary.setOrderedQty(BigDecimal.ZERO);
						invItemSummary.setFlInvoice(getManagedBean().getInvoice());
						for(FlFulfilmentItemAddr itemAddr : noteItem.getFlItem().getFlFulfilmentItemAddr()){
							invItemSummary.setOrderedQty(invItemSummary.getOrderedQty().add(itemAddr.getOrderedQty()));
							invItemSummary.setOrderedAmt(invItemSummary.getOrderedAmt().add(itemAddr.getOrderedAmt()));
						}
						siReqDTO.setItemId(noteItem.getFlItem().getFulfilmentItemId());
						siReqDTO.setFulfilmentOrderId(getManagedBean().getInvoice().getFlRequest().getFlFulfilmentOrder().getFulfilmentOrderId());
						FulfilmentResDTO siResDTO = orderSO.retrieveStopInstrTotalRecallByOrderId(siReqDTO);
						invItemSummary.setRecalledQty(siResDTO.getTotalRecallQty());
						invItemSummary.setRecalledAmt(siResDTO.getTotalRecallAmt());
						invItemSummary.setFlFulfilmentItem(noteItem.getFlItem());

						FlInvoiceItem invItem = new FlInvoiceItem();
						invItem.setFlInvoiceItemSummary(invItemSummary);
						invItem.setReceivedQty(noteItem.getReceivedQty());
						invItem.setReceivedAmt(noteItem.getReceivedAmt());
						invItem.setInvAmt(noteItem.getReceivedAmt());
						invItem.setGstAmount(noteItem.getGstAmount());
						invItem.setGstUnitPrice(noteItem.getGstUnitPrice());
						invItem.setDeliveryAmt(noteItem.getFlDelOrderItem().getDeliveredAmt());
						invItem.setDeliveryQty(noteItem.getFlDelOrderItem().getDeliveredQty());
						invItem.setGstFlag(noteItem.getGstFlag());
						invItemSummary.setFlInvoiceItem(new ArrayList<FlInvoiceItem>());
						invItemSummary.getFlInvoiceItem().add(invItem);
						itemMapLv1.put(noteItem.getFlItem().getFulfilmentItemId(), invItemSummary);
					}
			}
		}
		FlInvoiceItemSummary invItemSummMapValue = null;
		
		for(Entry<Long, FlInvoiceItemSummary> itemEntry : itemMapLv1.entrySet()){	
			invItemSummMapValue = itemEntry.getValue();
			BigDecimal sumInvAmt = BigDecimal.ZERO;
			for(FlInvoiceItem flInvoiceItem : invItemSummMapValue.getFlInvoiceItem()) {
				sumInvAmt = sumInvAmt.add(flInvoiceItem.getInvAmt());
			}
			invItemSummMapValue.setInvAmt(sumInvAmt);
			getManagedBean().getInvoice().getFlInvoiceItemSummary().add(invItemSummMapValue);
		}
	}
	
	/**
	 * call this method  as user click on summary menu
	 * <p>to calculate invoice summary.
	 */
	public void calInvSummary(){
		IFeeExemptionSO feeExemptionSO = (IFeeExemptionSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFeeExemptionSO.class);
		FeeExemptionReqDTO reqDTO = new FeeExemptionReqDTO();
		FeeExemptionResDTO resDTO = new FeeExemptionResDTO();
		
		BigDecimal invoiceTotalAmount = new BigDecimal(0);//total non-exempted amt
		BigDecimal orderTotalAmount = new BigDecimal(0);
		BigDecimal gstAmount = new BigDecimal(0);
		BigDecimal gstTotalAmount = new BigDecimal(0);
		for(FlInvoiceItemSummary invSummary : getManagedBean().getInvoice().getFlInvoiceItemSummary()){
			invoiceTotalAmount = invoiceTotalAmount.add(invSummary.getInvAmt());
			orderTotalAmount = orderTotalAmount.add(invSummary.getOrderedAmt());
			for(FlInvoiceItem invItem : invSummary.getFlInvoiceItem()){
				if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.STANDARD.value())){
					if(invSummary.getFlFulfilmentItem().getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)){
						if(getManagedBean().getInvoice().getFlRequest().getGstRate() == null){
							getManagedBean().getInvoice().getFlRequest().setGstRate(BigDecimal.ZERO);
						}
						Double serviceFee = 100 + getManagedBean().getInvoice().getFlRequest().getGstRate().doubleValue();
						BigDecimal serviceGstAmount = (new BigDecimal((invItem.getInvAmt().doubleValue() * getManagedBean().getInvoice().getFlRequest().getGstRate().doubleValue()) / serviceFee));
						gstTotalAmount = gstTotalAmount.add(serviceGstAmount);
					}else{
						gstAmount = (new BigDecimal(invItem.getGstAmount().doubleValue() * (invItem.getReceivedQty().doubleValue())));
						gstTotalAmount = gstTotalAmount.add(gstAmount);
					}
				}
			}
		}
		if(gstTotalAmount.doubleValue()>0) {
			getManagedBean().setGstAmount(gstTotalAmount.setScale(2, RoundingMode.HALF_UP));
		} else {
			getManagedBean().setGstAmount(BigDecimal.ZERO);
		}
		getManagedBean().setInvoiceAmountWithoutGST(invoiceTotalAmount.subtract(getManagedBean().getGstAmount()));
		getManagedBean().getInvoice().setInvoiceAmtOri(orderTotalAmount);
		getManagedBean().getInvoice().setInvoiceAmtNew(invoiceTotalAmount);
		
		BigDecimal serviceFee = new BigDecimal(0);
		if(getManagedBean().getInvoice().getFlFulfilmentOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
			IRuleSO ruleSO = (IRuleSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
			RuleReqDTO req = new RuleReqDTO();
			req.setRuleType(RuleTypeConst.TRANSACTION_FEE_ONE_OFF);
			serviceFee = new BigDecimal(ruleSO.getRuleValuesByTypeInCurrentDate(req).getRuleValueList().get(0).getRuleValue());
		} else {
			if(getManagedBean().getInvoice().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getServiceFee() != null){
				serviceFee = getManagedBean().getInvoice().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getServiceFee();
			}
		}
		
		if(getManagedBean().getInvoice().getFlRequest().getProcurementType()!=null && getManagedBean().getInvoice().getFlRequest().getProcurementType().getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			serviceFee = new BigDecimal(0);
		}
		
		List<BigDecimal> nonExemptedItemList = new ArrayList<BigDecimal>();
		reqDTO.setOrgProfileId(getManagedBean().getInvoice().getFlRequest().getSmSupplier().getSupplierId());
		if(getManagedBean().getInvoice().getFlRequest().getCtContract() != null){
			reqDTO.setContractId(getManagedBean().getInvoice().getFlRequest().getCtContract().getContractId());
		}
		resDTO = feeExemptionSO.checkForFeeExceptionFirstLevel(reqDTO);
		if(resDTO.getFeeExemptionResult()!=null && resDTO.getFeeExemptionResult().booleanValue()) {
			serviceFee = new BigDecimal(0);
		}
		
		//check item whther is exempted for this supplier.
		if(serviceFee.doubleValue()>0){
			for(FlInvoiceItemSummary invSummary : getManagedBean().getInvoice().getFlInvoiceItemSummary()){
				for(FlInvoiceItem invItem : invSummary.getFlInvoiceItem()){
					if(invSummary.getFlFulfilmentItem().getCmItem() != null){
						reqDTO.setItemId(invSummary.getFlFulfilmentItem().getCmItem().getItemId());
					}
					reqDTO.setRequestItemId(invSummary.getFlFulfilmentItem().getFlFulfilmentItemAddr().get(0).getScRequestItem().getRequestNoteItemId());
					reqDTO.setOrgProfileId(getManagedBean().getInvoice().getFlRequest().getSmSupplier().getSupplierId());
					if(getManagedBean().getInvoice().getFlRequest().getCtContract() != null){
						reqDTO.setContractId(getManagedBean().getInvoice().getFlRequest().getCtContract().getContractId());
					}
					resDTO = feeExemptionSO.checkForFeeException(reqDTO);

					if(resDTO.getFeeExemptionResult() == null ||resDTO.getFeeExemptionResult() == false){//item not exempted
						if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.STANDARD.value())){
							if(getManagedBean().getInvoice().getFlRequest().getGstRate() == null){
								getManagedBean().getInvoice().getFlRequest().setGstRate(BigDecimal.ZERO);
							}
							Double recalculateServiceFee = 100 + getManagedBean().getInvoice().getFlRequest().getGstRate().doubleValue();
							BigDecimal serviceGstAmount = (new BigDecimal((invItem.getInvAmt().doubleValue() * getManagedBean().getInvoice().getFlRequest().getGstRate().doubleValue()) / recalculateServiceFee));
							nonExemptedItemList.add(invItem.getInvAmt().subtract(serviceGstAmount));
						}else{
							nonExemptedItemList.add(invItem.getInvAmt()); 
						}
					}
				}
			}
		}
		IRuleSO ruleSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
		BigDecimal nonExemptedItem = new BigDecimal(0);
		if(!nonExemptedItemList.isEmpty()){
			for(BigDecimal normalItemAmt : nonExemptedItemList){
				nonExemptedItem = nonExemptedItem.add(normalItemAmt);
			}
		}else{
			serviceFee = new BigDecimal(0);
		}
		
		getManagedBean().setServiceCharge(Double.toString(serviceFee.doubleValue()));
		BigDecimal percentage = new BigDecimal(100);
		BigDecimal invMulty = new BigDecimal(0.0);
		BigDecimal feeAmount = new BigDecimal(0.0);
		invMulty = nonExemptedItem.multiply(serviceFee);
		feeAmount = invMulty.divide(percentage,2,RoundingMode.HALF_UP);
		
		BigDecimal ruleFee = new BigDecimal(0);
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		if(getManagedBean().getInvoice().getFlFulfilmentOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
			ruleReqDTO.setRuleType(FulfilmentConst.FLONE);
			ruleFee = new BigDecimal(ruleSO.getRuleValuesByTypeInCurrentDate(ruleReqDTO).getRuleValueList().get(0).getRuleValue());
			if(feeAmount.doubleValue() > ruleFee.doubleValue()){
				feeAmount = ruleFee;
			}
		}else{
			if(getManagedBean().getInvoice().getFlRequest().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF)){
				ruleReqDTO.setRuleType(FulfilmentConst.FLONE);
				ruleFee = new BigDecimal(ruleSO.getRuleValuesByTypeInCurrentDate(ruleReqDTO).getRuleValueList().get(0).getRuleValue());
				if(feeAmount.doubleValue() > ruleFee.doubleValue()){
					feeAmount = ruleFee;
				}
			}else{
				ruleReqDTO.setRuleType(FulfilmentConst.FLPER);
				ruleFee = new BigDecimal(ruleSO.getRuleValuesByTypeInCurrentDate(ruleReqDTO).getRuleValueList().get(0).getRuleValue());
				if(feeAmount.doubleValue() > ruleFee.doubleValue()){
					feeAmount = ruleFee;
				}
			}
		}
		getManagedBean().getInvoice().setSupplierOrFactorAmt(invoiceTotalAmount.subtract(feeAmount));
		getManagedBean().getInvoice().setFeeAmt(feeAmount);
		getManagedBean().getInvoice().setServiceFee(serviceFee);
		getTaxinvoice();
	}
	
	private void getTaxinvoice(){
		getManagedBean().setTaxInvoiceList(null);
		Map<String, TaxInvoiceVO> taxAmountMap = null;
		for(FlInvoiceItemSummary invItemSumm : getManagedBean().getInvoice().getFlInvoiceItemSummary()){
			for(FlInvoiceItem invItem : invItemSumm.getFlInvoiceItem()){
				if(taxAmountMap != null){
					if(taxAmountMap.containsKey(invItem.getGstFlag().getParameterCode())){
						TaxInvoiceVO taxInvVO = taxAmountMap.get(invItem.getGstFlag().getParameterCode());
						taxInvVO.setAmountWithoutGst(new BigDecimal(taxInvVO.getAmountWithoutGst()).add(invItem.getInvAmt()).toString());
						taxAmountMap.put(invItem.getGstFlag().getParameterCode(), taxInvVO);
					}else{
						TaxInvoiceVO taxInvVO = new TaxInvoiceVO();
						taxInvVO.setAmountWithoutGst(invItem.getInvAmt().toString());
						if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.STANDARD.value())){
							if(getManagedBean().getInvoice().getFlRequest().getGstRate() == null){
								getManagedBean().getInvoice().getFlRequest().setGstRate(BigDecimal.ZERO);
							}
							taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = " +getManagedBean().getInvoice().getFlRequest().getGstRate().setScale(2,RoundingMode.HALF_UP)+"%");
							taxInvVO.setGstAmount(getManagedBean().getGstAmount().toString());	
						}else if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.ZERO_RATED.value())){
							taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = 0%");
							taxInvVO.setGstAmount("0.00");	
						}else {
							taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = Nil");
							taxInvVO.setGstAmount("Nil");	
						}
						taxInvVO.setGstFlagCode(invItem.getGstFlag().getParameterCode());
						taxAmountMap.put(invItem.getGstFlag().getParameterCode(), taxInvVO);
					}
				}else{
					taxAmountMap = new HashMap<String, TaxInvoiceVO>();
					TaxInvoiceVO taxInvVO = new TaxInvoiceVO();
					taxInvVO.setAmountWithoutGst(invItem.getInvAmt().toString());
					if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.STANDARD.value())){
						if(getManagedBean().getInvoice().getFlRequest().getGstRate() == null){
							getManagedBean().getInvoice().getFlRequest().setGstRate(BigDecimal.ZERO);
						}
						taxInvVO.setGstAmount(getManagedBean().getGstAmount().toString());	
						taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = " +getManagedBean().getInvoice().getFlRequest().getGstRate().setScale(2,RoundingMode.HALF_UP)+"%");
					}else if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.ZERO_RATED.value())){
						taxInvVO.setGstAmount("0.00");	
						taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = 0%");
					}else {//RS n OS
						taxInvVO.setGstAmount("Nil");	
						taxInvVO.setGstFlag(invItem.getGstFlag().getParameterCode()+" = Nil");
					}
					taxInvVO.setGstFlagCode(invItem.getGstFlag().getParameterCode());
					
					taxAmountMap.put(invItem.getGstFlag().getParameterCode(), taxInvVO);
				}
			}
		}
		getManagedBean().setTaxInvoiceList(new ArrayList<TaxInvoiceVO>());
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat amountDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		if(taxAmountMap.containsKey(GSFType.STANDARD.value())){
			TaxInvoiceVO taxInvoice = taxAmountMap.get(GSFType.STANDARD.value());
			BigDecimal amtWithoutGst = (new BigDecimal(taxInvoice.getAmountWithoutGst())).subtract(getManagedBean().getGstAmount()) ;
			taxInvoice.setAmountWithoutGst(amountDF.format(amtWithoutGst));
			BigDecimal amtGst = new BigDecimal(taxInvoice.getGstAmount());
			taxInvoice.setGstAmount(amountDF.format(amtGst));
			getManagedBean().getTaxInvoiceList().add(taxInvoice);
		}else{
			TaxInvoiceVO taxInvoice = new TaxInvoiceVO();
			taxInvoice.setAmountWithoutGst("0.00");
			taxInvoice.setGstAmount("0.00");
			if(getManagedBean().getInvoice().getFlRequest().getGstRate() == null){
				getManagedBean().getInvoice().getFlRequest().setGstRate(BigDecimal.ZERO);
			}
			taxInvoice.setGstFlag(GSFType.STANDARD.value()+" = " +getManagedBean().getInvoice().getFlRequest().getGstRate().setScale(2,RoundingMode.HALF_UP)+"%");
			getManagedBean().getTaxInvoiceList().add(taxInvoice);
		}
		if(taxAmountMap.containsKey(GSFType.ZERO_RATED.value())){
			TaxInvoiceVO taxInvoice = taxAmountMap.get(GSFType.ZERO_RATED.value());
			BigDecimal amtWithoutGst = new BigDecimal(taxInvoice.getAmountWithoutGst());
			taxInvoice.setAmountWithoutGst(amountDF.format(amtWithoutGst));
			BigDecimal amtGst = new BigDecimal(taxInvoice.getGstAmount());
			taxInvoice.setGstAmount(amountDF.format(amtGst));
			getManagedBean().getTaxInvoiceList().add(taxInvoice);
		}else{
			TaxInvoiceVO taxInvoice = new TaxInvoiceVO();
			taxInvoice.setAmountWithoutGst("0.00");
			taxInvoice.setGstAmount("0.00");
			taxInvoice.setGstFlag(GSFType.ZERO_RATED.value()+" = 0%");
			getManagedBean().getTaxInvoiceList().add(taxInvoice);
		}
		if(taxAmountMap.containsKey(GSFType.RELIEF.value())){
			TaxInvoiceVO taxInvoice = taxAmountMap.get(GSFType.RELIEF.value());
			BigDecimal amtWithoutGst = new BigDecimal(taxInvoice.getAmountWithoutGst());
			taxInvoice.setAmountWithoutGst(amountDF.format(amtWithoutGst));
			getManagedBean().getTaxInvoiceList().add(taxInvoice);
		}else{
			TaxInvoiceVO taxInvoice = new TaxInvoiceVO();
			taxInvoice.setAmountWithoutGst("Nil");
			taxInvoice.setGstAmount("Nil");
			taxInvoice.setGstFlag(GSFType.RELIEF.value()+" = Nil");
			getManagedBean().getTaxInvoiceList().add(taxInvoice);
		}
		if(taxAmountMap.containsKey(GSFType.OUT_OF_SCOPE.value())){
			TaxInvoiceVO taxInvoice = taxAmountMap.get(GSFType.OUT_OF_SCOPE.value());
			BigDecimal amtWithoutGst = new BigDecimal(taxInvoice.getAmountWithoutGst());
			taxInvoice.setAmountWithoutGst(amountDF.format(amtWithoutGst));
			getManagedBean().getTaxInvoiceList().add(taxInvoice);
		}else{
			TaxInvoiceVO taxInvoice = new TaxInvoiceVO();
			taxInvoice.setAmountWithoutGst("Nil");
			taxInvoice.setGstAmount("Nil");
			taxInvoice.setGstFlag(GSFType.OUT_OF_SCOPE.value()+" = Nil");
			getManagedBean().getTaxInvoiceList().add(taxInvoice);
		}
	}
	
	/**
	 * On tab change.
	 *
	 * @param selectedTabIndex the selected tab index
	 */
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==1){
			if(!getManagedBean().isTaxInvoice()){
				if(null==getManagedBean().getChargeLineItemRoot()){
					 populateChargeLineData();
				}
			}else if(getManagedBean().isTaxInvoice()){
				if(null==getManagedBean().getTaxSectionItemRoot() && null==getManagedBean().getOutOfScopeSectionItemRoot()){
					populateTaxAbleChargeLineData();
				}
			}
			setClassName("@(.invItemPnlClass)");
		}
		if(selectedTabIndex==2){
			calInvSummary();
		}
	}
	
	/**
	 * Gets the populate bank account list based on branch name and supplier id.
	 *
	 * @return the populate bank account list
	 */
	public List<SelectItem> getPopulateBankAccountList() {
		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,ISupplierSearchSO.class);
		List<SelectItem> populateBankAccountList = new ArrayList<SelectItem>();
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		
		if (this.getManagedBean().getInvoice() != null) {
			if(!getManagedBean().getInvoice().getFlFulfilmentOrder().isFactored()){//33238
				//32967-Start
				if(this.getManagedBean().getInvoice().getAccountNo()!=null){
					if(getManagedBean().getAccountNo()==null){
						getManagedBean().setAccountNo(this.getManagedBean().getInvoice().getAccountNo());
					}			
				
					if(this.getManagedBean().getInvoice().getAccountNo().equalsIgnoreCase(getManagedBean().getAccountNo())){
						this.getManagedBean().getInvoice().setAccountNo(this.getManagedBean().getInvoice().getPaymentBank().getFinancialOrgId()+"|"+this.getManagedBean().getInvoice().getAccountNo());
					}
				}
				//32967-End
			}
			if(this.getManagedBean().getInvoice().getFlRequest().getFlSupplierDtl().getBranchName() != null ){
				supplierSearchReqDTO.setBranchCode(getManagedBean().getInvoice().getFlRequest().getFlSupplierDtl().getBranchCode());
				supplierSearchReqDTO.setSupplierId(getManagedBean().getInvoice().getFlRequest().getSmSupplier().getSupplierId());
				SupplierSearchResDTO resDTO = supplierSearchSO.retrieveSupplierBranchBanksByBranchCode (supplierSearchReqDTO);
				for (SmSupplierBank obj : resDTO.getSmSupplierBankList()) {
					if(obj.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
						String account = obj.getAccountNo();
						if (obj.getPmFinancialOrg() != null)
							account = account + "  " + obj.getPmFinancialOrg().getFinOrgName();
						//32967-Start
						populateBankAccountList.add(new SelectItem(obj.getPmFinancialOrg().getFinancialOrgId()+"|"+ obj.getAccountNo(), account));
						if(!getManagedBean().getSupplierBankAccountMap().containsKey(obj.getPmFinancialOrg().getFinancialOrgId()+"|"+ obj.getAccountNo())) {
							getManagedBean().getSupplierBankAccountMap().put(obj.getPmFinancialOrg().getFinancialOrgId()+"|"+ obj.getAccountNo(), obj);
						//32967-End
						}
					}
				}
			}
			
			if (populateBankAccountList.size()==0) {
				supplierSearchReqDTO.setSupplierId(getManagedBean().getInvoice().getFlRequest().getSmSupplier().getSupplierId());
				SupplierSearchResDTO resDTO = supplierSearchSO.retrieveSmSupplierBankHQBySupplier(supplierSearchReqDTO);
				for (SmSupplierBank obj : resDTO.getSmSupplierBankList()) {
					if(obj.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
						String account = obj.getAccountNo();
						if (obj.getPmFinancialOrg() != null)
							account = account + "  " + obj.getPmFinancialOrg().getFinOrgName();
						//32967-Start
						populateBankAccountList.add(new SelectItem(obj.getPmFinancialOrg().getFinancialOrgId()+"|"+ obj.getAccountNo(), account));
						if(!getManagedBean().getSupplierBankAccountMap().containsKey(obj.getPmFinancialOrg().getFinancialOrgId()+"|"+ obj.getAccountNo())) {
							getManagedBean().getSupplierBankAccountMap().put(obj.getPmFinancialOrg().getFinancialOrgId()+"|"+ obj.getAccountNo(), obj);
						//32967-End
						}
					}
				}
			}
		}
		return populateBankAccountList;
	}
	
	/**
	 * Bank info action listener.
	 */
	public void bankInfoActionListener() {
		if(null != getManagedBean().getInvoice().getAccountNo()){
			SmSupplierBank supplierBank = getManagedBean().getSupplierBankAccountMap().get(getManagedBean().getInvoice().getAccountNo());
			if(supplierBank!=null) {
				getManagedBean().getInvoice().setPaymentBank(supplierBank.getPmFinancialOrg());
				if(getManagedBean().getInvoice().getPaymentBankAddress() != null){
					getManagedBean().getInvoice().setPaymentBankAddress(convertSmAddressToFlAddress(supplierBank.getSmAddress(),getManagedBean().getInvoice().getPaymentBankAddress()));
				}else{
					FlAddress flAddress = new FlAddress();
					getManagedBean().getInvoice().setPaymentBankAddress(convertSmAddressToFlAddress(supplierBank.getSmAddress(),flAddress));
				}
				getManagedBean().getInvoice().setPaymentBankName(supplierBank.getPmFinancialOrg().getFinOrgName());
				if(!getManagedBean().getInvoice().getFlFulfilmentOrder().isFactored()){//33238
					getManagedBean().setAccountNo(getManagedBean().getInvoice().getAccountNo().substring(supplierBank.getPmFinancialOrg().getFinancialOrgId().toString().length()+1));//32967
				}
			}
		}else{
			getManagedBean().getInvoice().setPaymentBankAddress(null);
			
		}
		
	}
	
	/**
	 * Gets the company factoring details based on financial org id.
	 *
	 * @return the factoring dtl
	 */
	public void getFactoringDtl(){
		IFinancialOrgSO financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IFinancialOrgSO.class);
		FinancialOrgReqDTO finOrgReqDTO = new FinancialOrgReqDTO();
		FinancialOrgResDTO finOrgResDTO = null;
		
		//HQ info
		finOrgReqDTO.setFinancialOrgId(getManagedBean().getInvoice().getFlFulfilmentOrder().getPmFinancialOrgId());
		finOrgResDTO = financialOrgSO.retriveFinAccDtlByFinOrgId(finOrgReqDTO);
		
		if(finOrgResDTO.isSuccessful()) {
			if(finOrgResDTO.getPmAddressType()!=null){
				//TODO NEW
				getManagedBean().getInvoice().setPaymentBankAddress(convertFinAccAddressToFlAddress(finOrgResDTO.getPmAddressType()));
			}
			
			/* Start  #21229
			if(finOrgResDTO.getPmFinancialAcc()!=null) { 				
				//to set default value
				getManagedBean().getInvoice().setAccountNo(finOrgResDTO.getPmFinancialAcc().getAccountNo());
				getManagedBean().getInvoice().setPaymentBank(finOrgResDTO.getPmFinancialAcc().getPmPaymentBankOrg());
				getManagedBean().getInvoice().setPaymentBankName(getManagedBean().getInvoice().getPaymentBank().getFinOrgName());
			}
			end  #21229 */
		}
	}
	
	/**
	 * Gets the class name.
	 *
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Sets the class name.
	 *
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	
	/**
	 * Search document.
	 */
	public void searchDocument(){
		flInvoiceDataModel.reloadData();
	}
	
	/**
	 * Pre render doc search.
	 */
	public void preRenderDocSearch() {
		FacesContext fc=FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "flAp59Search?faces-redirect=true");
		
	}
	
	/**
	 * The Class FlInvoicePagingDataModel.
	 */
	private class FlInvoicePagingDataModel extends PagingDataModel<FlInvoice> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new fl invoice paging data model.
		 */
		public FlInvoicePagingDataModel() {
			super("flInvoiceListDt");
		}
		
		/* (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
	public ISearchResultBean<FlInvoice> pageChangeListener(ISearchBean<FlInvoice> searchBean) {
		if (log.isDebugEnabled()) {
			log.debug("pageChangeListener");
		}
		try {

			//28719 -Start
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			searchBean.setUseEntityClass(true);
			reqDTO.setSearchBean(searchBean);
			
			if(getManagedBean().getLoaNo()!=null && getManagedBean().getLoaNo().length() > 0) {
				reqDTO.setLoaNo(getManagedBean().getLoaNo());
			}
			if(getManagedBean().getContractNo()!=null && getManagedBean().getContractNo().length() > 0) {
				reqDTO.setContractNo(getManagedBean().getContractNo());
			}
			if(getManagedBean().getTitle()!=null && getManagedBean().getTitle().length() > 0) {
				reqDTO.setContractName(getManagedBean().getTitle());
			}
			if(getManagedBean().getPhysicalLoaNo()!=null && getManagedBean().getPhysicalLoaNo().length() > 0) {
				reqDTO.setLoaPhysicalNo(getManagedBean().getPhysicalLoaNo());
			}
			if(getManagedBean().getPhysicalContractNo()!=null && getManagedBean().getPhysicalContractNo().length() > 0) {
				reqDTO.setContractPhysicalNo(getManagedBean().getPhysicalContractNo());
			}
			//28719 -End
			IInvoiceSO invoiceSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);//28719
			FulfilmentResDTO resDTO = invoiceSO.searchAP59(reqDTO);
			if(resDTO.isSuccessful()) {
				return resDTO.getResultBean();
			}
		} catch (Exception e) {
			log.error("Error occurred in pageChangeListener method", e);//28719
		}
		return null;
	}
}

	/**
	 * Gets the fl invoice data model.
	 *
	 * @return the flInvoiceDataModel
	 */
	public IPagingDataModel<FlInvoice> getFlInvoiceDataModel() {
		return flInvoiceDataModel;
	}

	/**
	 * Sets the fl invoice data model.
	 *
	 * @param flInvoiceDataModel the flInvoiceDataModel to set
	 */
	public void setFlInvoiceDataModel(IPagingDataModel<FlInvoice> flInvoiceDataModel) {
		this.flInvoiceDataModel = flInvoiceDataModel;
	}
	
	/**
	 * Search reset.
	 */
	public void searchReset(){
		this.getManagedBean().setTitle(null);
		this.getManagedBean().setContractNo(null);
		this.getManagedBean().setLoaNo(null);
		renewSesionObject();
	}
	
	/**
	 * Gets the portlet title.
	 *
	 * @return the portlet title
	 */
	public String getPortletTitle() {
		String portletTitle = ResourceBundleUtils.getMessage("lbl.fl.invoice.supp.invoice",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale());
		if(getManagedBean().isModificationCancel()) {
			portletTitle = ResourceBundleUtils.getMessage("lbl.fl.doc.maintenance",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale());
		}else if(getManagedBean().isTaxInvoice()){
			portletTitle = ResourceBundleUtils.getMessage("lbl.fl.tax.invoice",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale());
		}
		return portletTitle;
	}
	
	/**
	 * Navigate to ap59BasePage if doc type is A59.
	 *
	 * @return the string
	 */
	public String navigateToPage(){
		renewSesionObject();
		String navgPage="/xhtml/fl/ap59/ap59BasePage.xhtml?faces-redirect=true";
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
		String invoiceId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("invoiceId");
		
		IInvoiceSO invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
		IStatusSO statusSO = (IStatusSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
		reqDTO.setInvoiceId(new Long(invoiceId));
		reqDTO.setDocType(ParameterConst.DOCTYPE_AP59);
		FulfilmentResDTO resDTO = invoiceSO.retrieveInvoiceByInvoiceId(reqDTO);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setDocType(FulfilmentConst.DOC_TYPE_AP59);
		statusReqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
		statusReqDTO.setModuleCode(FulfilmentConst.FULFILMENT);
		StatusResDTO statusResDTO = statusSO.retrieveStatusDescList(statusReqDTO);
		InvoiceBackingBean invBB = new InvoiceBackingBean();
		invBB.getManagedBean().setInvoice(resDTO.getInvoice());
		BigDecimal sumOrderedAmt = new BigDecimal(0);
		for(FlFulfilmentItem item : resDTO.getInvoice().getFlRequest().getFlFulfilmentItems()){
			for(FlFulfilmentItemAddr itemAddr : item.getFlFulfilmentItemAddr()){
				sumOrderedAmt = sumOrderedAmt.add(itemAddr.getOrderedAmt());
			}
		}
		invBB.getManagedBean().setSumOrderedAmt(sumOrderedAmt);
		BigDecimal serviceFee = new BigDecimal(0);
		if(resDTO.getInvoice().getFlFulfilmentOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
			serviceFee = new BigDecimal(FulfilmentConst.SERVICE_FEE_ZERO_POINT_EIGHT);
		} else {
			if(getManagedBean().getInvoice().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getServiceFee() != null){
				serviceFee = getManagedBean().getInvoice().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getServiceFee();
			}
		}
		
		getManagedBean().setServiceCharge(Double.toString(serviceFee.doubleValue()));
		for(PmStatusDesc statusDesc : statusResDTO.getStatusDescList()){
			if(!statusDesc.getPmStatus().getStatusId().equals(FulfilmentStatusConst.AP59_PENDING_AP59_DECISION)){
				if(invBB.getManagedBean().getDecisionList() == null	){
					invBB.getManagedBean().setDecisionList(new ArrayList<PmStatusDesc>());
				}
				invBB.getManagedBean().getDecisionList().add(statusDesc);
			}
		}
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
		String expDate=null;
		String effDate=null;
		expDate = DATE_FORMAT.format(invBB.getManagedBean().getInvoice().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getExpDate());
		effDate = DATE_FORMAT.format(invBB.getManagedBean().getInvoice().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getEffDate());
		invBB.getManagedBean().setExpDate(expDate);
		invBB.getManagedBean().setEffDate(effDate);
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		flBB.initializeRemarkAndAttachement(resDTO.getInvoice().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
		return navgPage;
	}
	
	/**
	 * save,submit the ap59
	 * 
	 * @param String action 
	 * <p>submit - save data into database and BPM process will route to the next respective user.
	 * <p>save - save data into database.
	 * navigate to acknowledge page.
	 *
	 * @return the string
	 */
	public String submitAp59(){
		IInvoiceSO invoiceSO = null;
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		String action = BpmOutcomeConst.BPM_SUBMIT;
		List<PmUser> listOfApprover = null;
		IFulfilmentSO fulfilmentSO = null;
		IFulfilmentRequestSO requestSO = null;
		try {
			fulfilmentSO = (IFulfilmentSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentSO.class);
			FlInvoice invoice = getManagedBean().getInvoice();
			invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
			requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
			reqDTO.setStatusId(getManagedBean().getStatusId());
			reqDTO.setInvoice(invoice);
			reqDTO.setOutcome(action);
			reqDTO.setUserRole(getUserRole());
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlDeleteAttachmentList(flBB.getManagedBean().getDeleteAttachments());//29708
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			if(flBB.getManagedBean().getRemarkList().size() >0){
				reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			}
			BigDecimal sumItemAmt = fulfilmentSO.getSumTotalItemsAmtByReqId(invoice.getFlRequest().getFulfilmentReqId());
			reqDTO.setGroupId(invoice.getFlRequest().getPmUserGroup().getUserGroupId());
			reqDTO.setApproverLimit(sumItemAmt);
			try {
				resDTO = requestSO.retrieveApproverListByGroupId(reqDTO);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(resDTO.getApproverGroupUserList().isEmpty()){
				throw new EpException(MessageConst.ERROR_740020, new String[]{ResourceBundleUtils.getMessage("lbl.fl.approverUser",ResourceBundleUtils.BUNDLE_COMMON_LABELS,reqDTO.getUserLocale())});
			}
			FlActor actor = fulfilmentSO.retrieveFlActors(getManagedBean().getInvoice().getFlRequest().getDocType(),getManagedBean().getInvoice().getFlRequest().getFulfilmentReqId(), RoleCode.FL_APPROVER.value());
			listOfApprover = sortApproverUserList(resDTO.getApproverGroupUserList(),actor.getUser(),getManagedBean().getInvoice().getFlRequest().getPmUserGroup().getUserGroupId());
			reqDTO.setApproverUserList(listOfApprover);
			resDTO = invoiceSO.updateInvoiceWithAP59(reqDTO);
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_745019;
                
				if(getManagedBean().getStatusId().equals(FulfilmentStatusConst.AP59_PAYMENT_VIA_AP59)){
					messageId = MessageConst.INFO_745007;
				}
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700004;
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, 
						null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_INVOICE), resDTO.getSupplierInvoiceNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					getManagedBean().setInvoice(resDTO.getInvoice());
					flBB.initializeRemarkAndAttachement(resDTO.getInvoice().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
					if(getManagedBean().getInvoice().getFlFulfilmentOrder().isFactored()){
						getFactoringDtl();
					}
				}
			}
		}finally {
			invoiceSO = null;
		}
		return null;
	}
	
	/**
	 * Page navigation to FLDoc search page in INV.
	 *
	 * @return the string
	 * @throws PortalException the portal exception
	 * @throws SystemException the system exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String backToPervious() throws PortalException, SystemException, IOException {
		if(getManagedBean().isDocMaintain()) {
			return "flDocSearch";
		}else if(getManagedBean().isHyperLinkPage()){
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.getManagedBean().setRemarkList(getManagedBean().getPreviousRemarkList());
			flBB.getManagedBean().setAttachmentList(getManagedBean().getPreviousAttachmentList());
			flBB.getManagedBean().setHideRemarks(getManagedBean().isPrevHideRemarks());
			return getManagedBean().getBackToPrevious();
		} else {
			String commonUrl = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_APPLICATION_URL);
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.redirect(commonUrl + "notifications");
			return null;
		}
	}
	
	/**
	 * Page navigation to FLAP59 search page in INV.
	 *
	 * @return the string
	 */
	public String backToPerviousAp59() {
		return "flAp59Search";
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
		IInvoiceSO invoiceSO = (IInvoiceSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IInvoiceSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
			if(flBB.getManagedBean().getRemarkList().get(0).getRemark()==null || flBB.getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
				getManagedBean().setSelectedTabIndex(0);
				throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.txt.remarks",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			}
		}
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)){
			if(null == getManagedBean().getInvoice().getAccountNo() || getManagedBean().getInvoice().getAccountNo().length()==0 || getManagedBean().getInvoice().getPaymentBank() == null ) {
				getManagedBean().setSelectedTabIndex(0);
				throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.fl.bank.acc.no.name",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			} else if(null == getManagedBean().getInvoice().getSupplierInvoiceRef() || getManagedBean().getInvoice().getSupplierInvoiceRef().length()==0) {
				getManagedBean().setSelectedTabIndex(0);
				throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.fl.supp.invoice.no",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			} else {
				reqDTO.setSupplierId(getManagedBean().getInvoice().getFlRequest().getSmSupplier().getSupplierId());
				reqDTO.setSupplierInvNo(getManagedBean().getInvoice().getSupplierInvoiceRef());
				if(getManagedBean().getInvoice().getInvoiceId() != null){
					reqDTO.setInvoiceId(getManagedBean().getInvoice().getInvoiceId());
				}
				FulfilmentResDTO resDTO = invoiceSO.searchDuplicateSupplierInvoiceNo(reqDTO);
				if(resDTO.isDuplicateSupplierInvNo()){
					getManagedBean().setSelectedTabIndex(0);
					throw new EpException(MessageConst.ERROR_740043, new String[]{getManagedBean().getInvoice().getSupplierInvoiceRef(),getManagedBean().getInvoice().getFlRequest().getFlSupplierDtl().getSupplierName()});
				}
			}
		}
		
		if(getManagedBean().getInvoice().isBrandNew()) { // validate to avoid duplicate invoice record for one order/request
			reqDTO.setFlRequestId(getManagedBean().getInvoice().getFlRequest().getFulfilmentReqId());
			if(invoiceSO.retrieveInvoiceByFlRequestId(reqDTO).getInvoice()!=null) {
				throw new EpException(MessageConst.ERROR_100048);
			}
		}
		
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
			if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
				if(!getManagedBean().isReliefDeclarationCheck()){
					if(!getManagedBean().isTaxInvoice()){
						if(null==getManagedBean().getChargeLineItemRoot()){
							 populateChargeLineData();
						}
					}else if(getManagedBean().isTaxInvoice()){
						if(null==getManagedBean().getTaxSectionItemRoot() || null==getManagedBean().getOutOfScopeSectionItemRoot()){
							populateTaxAbleChargeLineData();
						}
					}
					getManagedBean().setSelectedTabIndex(0);
					throw new EpException(MessageConst.ERROR_745072);
				}
				IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);//28719
				UserReqDTO userReqDTO = new UserReqDTO();
				userReqDTO.setRoleCode(RoleCode.PAY_OFFICER.toString());
				userReqDTO.setOrgProfileId(getManagedBean().getInvoice().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
				UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
				if(userResDTO.getUserList() == null || userResDTO.getUserList().isEmpty()){
					throw new EpException(MessageConst.ERROR_740020, new String[]{ResourceBundleUtils.getMessage("lbl.txt.paymentMatchOfficer",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
				}				
			}
			reqDTO.setOutcome(outcome);
			reqDTO.setInvoice(getManagedBean().getInvoice());
			FulfilmentReqDTO reqDTO2 = invoiceSO.validationCheck(reqDTO);
			getManagedBean().setInvoice(reqDTO2.getInvoice());
			getManagedBean().setSupplementContractComplete(reqDTO2.isSupplementContractComplete());//28719
			
			if(hasGovtSellerRole()){
				setTextBeforeSign(outcome);
				getManagedBean().setButtonAction(outcome);
				getManagedBean().setAutoUpdate(Boolean.FALSE);	
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
				return null;
			}else{
				getManagedBean().getSpkiBackingBean().getChallengeQuestions();
				getManagedBean().setButtonAction(outcome);
				getManagedBean().setAutoUpdate(Boolean.FALSE);	
				RequestContext.getCurrentInstance().execute("spki_dlg.show()");
				return null;
			}
		} else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
			setTextBeforeSign(outcome);
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);	
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
		} else { // save or update
			if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)) {
				reqDTO.setOutcome(outcome);
				reqDTO.setInvoice(getManagedBean().getInvoice());
				FulfilmentReqDTO reqDTO2 = invoiceSO.validationCheck(reqDTO);
				getManagedBean().setInvoice(reqDTO2.getInvoice());
			}
			return submitAction(outcome);
		}
		return null;
	}
	
	/**
	 * Pre submit action for digisign(spki).
	 *
	 * @param pmDigiSign the pm digi sign
	 * @return the string
	 * @throws Exception the exception
	 */
	public String preSubmitAction(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);
			if(hasSupplierRole() && !hasGovtSellerRole()) {
				setTextBeforeSign(getManagedBean().getButtonAction());
				updateSpkiDigiSignData(getManagedBean().getPmDigiSign(), ParameterConst.DOCTYPE_INVOICE, 
						getManagedBean().getInvoice().getFlFulfilmentOrder().getFulfilmentOrderId(), getManagedBean().getInvoice().getFlFulfilmentOrder().getDocNo(), 
						getManagedBean().getTextBeforeSign());
			}
			if(getManagedBean().getPmDigiSign().getOriginalData().isEmpty()) {
				setTextBeforeSign(getManagedBean().getButtonAction());
				getManagedBean().getPmDigiSign().setOriginalData(getManagedBean().getTextBeforeSign());
			}
		}
		return submitAction(getManagedBean().getButtonAction());
	}
	
	/**
	 * calculate the invoice total amount based on invoice amount.
	 *
	 * @return the invoice amt
	 */
	private String getInvoiceAmt() {
		String invAmt = "";
		BigDecimal totalAmt = new BigDecimal(0);
		
		for(FlInvoiceItemSummary invItemSumm : getManagedBean().getInvoice().getFlInvoiceItemSummary()){
			totalAmt = totalAmt.add(invItemSumm.getInvAmt());
		}
		invAmt = totalAmt.toString();
		return invAmt;
	}


	/**
	 * Enable auto update.
	 */
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}
	
	/**
	 * Gets the selected factoring company details based on  financial org id and address type.
	 *
	 * @param financialOrgId the financial org id
	 * @return the selected factoring comp dtl
	 */
	public void getSelectedFactoringCompDtl(Long financialOrgId){
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
		
		if(financialOrgId!=null) {
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialOrgId(financialOrgId);
			reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());

			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if(!resDTO.getAddressTypeList().isEmpty()){
				getManagedBean().getInvoice().getFlFulfilmentOrder().setFactoringAddress(convertPmAddressToFlAddress(resDTO.getAddressTypeList().get(0).getPmAddress()));
				getManagedBean().getInvoice().getFlFulfilmentOrder().setFactoringOrgName(resDTO.getAddressTypeList().get(0).getPmFinancialOrg().getFinOrgName());
			}
		}
	}
	
		/**
	 * Update digisign data for spki.
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
		 * Navigate document based on doc type.
		 *
		 * @param docType the doc type
		 * @return the string
		 */
		public String navigateDocument(String docType){
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER) || docType.equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
				IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
				FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
				reqDTO.setFlOrderId(getManagedBean().getInvoice().getFlFulfilmentOrder().getFulfilmentOrderId());
				reqDTO.setDocType(docType);
				FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(reqDTO);
				FulfilmentOrderBackingBean orderBackingBean = new FulfilmentOrderBackingBean();
				orderBackingBean.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				orderBackingBean.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				FacesContext fc = FacesContext.getCurrentInstance();
				orderBackingBean.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
				orderBackingBean.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
				orderBackingBean.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());		
				orderBackingBean.getManagedBean().setHyperLinkPage(true);
				orderBackingBean.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
				boolean isFactoredSelected=getManagedBean().getInvoice().getFlFulfilmentOrder().isFactored();
				if(isFactoredSelected){
					orderBackingBean.getManagedBean().setFactoringCheck("true");
				} else {
					orderBackingBean.getManagedBean().setFactoringCheck("false");
				}
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
			}else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_INVOICE)){

				IInvoiceSO invoiceSO = (IInvoiceSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IInvoiceSO.class);
				FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
				reqDTO.setInvoiceId(getManagedBean().getInvoice().getInvoiceId());
				reqDTO.setDocType(docType);
				reqDTO.setInvoiceLink(true);
				FulfilmentResDTO resDTO = invoiceSO.retrieveInvoiceByInvoiceId(reqDTO);
				InvoiceBackingBean invBB = new InvoiceBackingBean();
				invBB.getManagedBean().setInvoice(resDTO.getInvoice());
				invBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
				invBB.getManagedBean().setHyperLinkPage(true);
				FacesContext fc = FacesContext.getCurrentInstance();
				flBB.getManagedBean().setHideRemarks(true);
				flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
				flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
				invBB.getManagedBean().setSelectedTabIndex(0);
				invBB.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
				invBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
				invBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());
				flBB.getManagedBean().setHideRemarks(true);
				invBB.getManagedBean().setReliefDeclarationCheck(true);
				invBB.getManagedBean().setDocMaintain(false);
				invBB.getManagedBean().setDelOrderList(new ArrayList<FlDeliveryOrder>()); 
				for(FlDeliveryOrder flDeliveryOrder : invBB.getManagedBean().getInvoice().getFlFulfilmentOrder().getFlDeliveryOrders()) {
					if(flDeliveryOrder.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
						invBB.getManagedBean().getDelOrderList().add(flDeliveryOrder);
					}
				}		
				return NAV_INVOICE;
			}else if(docType.equalsIgnoreCase(FulfilmentConst.CONTRACT)){
				ContractDetailsBackingBean ctDetBB = new ContractDetailsBackingBean();
				FacesContext fc = FacesContext.getCurrentInstance();
				ctDetBB.getManagedBean().setGotoPreviewPage(fc.getViewRoot().getViewId()+"?faces-redirect=true");
				return ctDetBB.gotoCommonViewPage(getManagedBean().getInvoice().getFlRequest().getCtContract().getContractId());
			}
			return null;
		}
		
		/**
		 * Gets the supplier gst.
		 *
		 * @param invoice the invoice
		 * @return the supplier gst
		 */
		private void getSupplierGst(FlInvoice invoice) {
			ISupplierGstSO supplierGstSO = (ISupplierGstSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierGstSO.class);
			SupplierGstReqDTO reqDTO = new SupplierGstReqDTO();
			reqDTO.setBranchCode(invoice.getFlRequest().getFlSupplierDtl().getBranchCode());
			reqDTO.setSupplierId(invoice.getFlRequest().getSmSupplier().getSupplierId());
			SupplierGstResDTO resDTO = supplierGstSO.retrieveGstInfoBySupplierIdAndBranchCode(reqDTO);
			if(resDTO.getSmGstSupplier() != null){
				invoice.setGstEffectiveDate(resDTO.getSmGstSupplier().getGstEffDate());
				invoice.setGstDeclaredDate(resDTO.getSmGstSupplier().getGstDeclaredDate());
				invoice.setGstRegNo(resDTO.getSmGstSupplier().getGstRegNo());
			} else {
				invoice.setGstEffectiveDate(null);
				invoice.setGstDeclaredDate(null);
				invoice.setGstRegNo(null);
			}
		}
		
		/**
		 * call this method  as user click on items menu
		 * <p>Populate charge line data.
		 */
		private void populateChargeLineData(){
			getManagedBean().setChargeLineItemRoot(new DefaultTreeNode("root", null));
			Map<String, TreeNode> lvl1Map = new HashMap<String, TreeNode>();
			BigDecimal grandTotalOrderAmt = new BigDecimal(0);
			BigDecimal grandTotalReceivedAmt = new BigDecimal(0);
			BigDecimal grandTotalInvAmt = new BigDecimal(0);
			TreeNode lv1TreeNode = null;
			Map<String, InvoiceChargeLineVO> lvl3Map = null;
			
			removeDuplicateInvoiceSummaryItem();
			for(FlInvoiceItemSummary invItemSummLv2 : getManagedBean().getInvoice().getFlInvoiceItemSummary()){
				if(lvl1Map.containsKey(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId())) {
					lv1TreeNode = lvl1Map.get(invItemSummLv2.getFlFulfilmentItem().getItemCode()+invItemSummLv2.getFlFulfilmentItem().getPmUomId());
				} else {
					InvoiceChargeLineVO chargeLineLv1 = new InvoiceChargeLineVO();
					chargeLineLv1.setLevel(1);
					chargeLineLv1.setFlInvoiceSummary(invItemSummLv2);
					
					lv1TreeNode = new DefaultTreeNode(chargeLineLv1, getManagedBean().getChargeLineItemRoot());
					lvl1Map.put(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId(), lv1TreeNode);
				}
				grandTotalOrderAmt = grandTotalOrderAmt.add(invItemSummLv2.getOrderedAmt());
				grandTotalInvAmt = grandTotalInvAmt.add(invItemSummLv2.getInvAmt());
				InvoiceChargeLineVO chargeLineLv2 = new InvoiceChargeLineVO();
				chargeLineLv2.setLevel(2);
				chargeLineLv2.setFlInvoiceSummary(invItemSummLv2);
				if(lvl3Map == null){
					lvl3Map = new HashMap<String, InvoiceChargeLineVO>();
				}else{
					lvl3Map.clear();
				}
				TreeNode lv2TreeNode = new DefaultTreeNode(chargeLineLv2, lv1TreeNode);
				for(FlInvoiceItem invItemLv3 : invItemSummLv2.getFlInvoiceItem()){
					if(lvl3Map.containsKey(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode())) {
						InvoiceChargeLineVO sameInvItemObj = lvl3Map.get(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode());
						sameInvItemObj.getFlInvoiceItem().setTotalReceivedQty(sameInvItemObj.getFlInvoiceItem().getTotalReceivedQty().add(invItemLv3.getReceivedQty()));
						sameInvItemObj.getFlInvoiceItem().setTotalReceivedAmt(sameInvItemObj.getFlInvoiceItem().getTotalReceivedAmt().add(invItemLv3.getReceivedAmt()));
						lvl3Map.put(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode(),sameInvItemObj);
					}else{
						InvoiceChargeLineVO chargeLineLv3 = new InvoiceChargeLineVO();
						chargeLineLv3.setFlInvoiceSummary(invItemSummLv2);
						chargeLineLv3.setLevel(3);
						invItemLv3.setTotalReceivedQty(invItemLv3.getReceivedQty());
						invItemLv3.setTotalReceivedAmt(invItemLv3.getReceivedAmt());
						chargeLineLv3.setFlInvoiceItem(invItemLv3);
						lvl3Map.put(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode(),chargeLineLv3);
					}
					grandTotalReceivedAmt = grandTotalReceivedAmt.add(invItemLv3.getReceivedAmt());
				}
				for(Entry<String, InvoiceChargeLineVO> entrySet : lvl3Map.entrySet()){
					new DefaultTreeNode(entrySet.getValue(), lv2TreeNode);
				}
			}
			getManagedBean().setOrderedAmtGrandTotal(grandTotalOrderAmt);
			getManagedBean().setReceivedAmtGrandTotal(grandTotalReceivedAmt);
			getManagedBean().setInvAmtGrandTotal(grandTotalInvAmt);
		}
		
		/**
		 * call this method  as user click on items menu
		 * <p>Populate charge line data for tax invoice
		 */
		private void populateTaxAbleChargeLineData(){
			
			Map<String, TreeNode> lvl1TaxMapTree = new HashMap<String, TreeNode>();
			Map<String, TreeNode> lvl2TaxMapTree = new HashMap<String, TreeNode>();
			Map<String, TreeNode> lvl2OosMapTree = new HashMap<String, TreeNode>();
			Map<String, InvoiceChargeLineVO> lvl3TaxMap = null;
			Map<String, InvoiceChargeLineVO> lvl3OosMap = null;
			Map<String, TreeNode> lvl1OOSMap = new HashMap<String, TreeNode>();
			BigDecimal grandTotalInvAmt = new BigDecimal(0);
			TreeNode lv1TreeNode = null;
			List<FlInvoiceItemSummary> taxList = new ArrayList<FlInvoiceItemSummary>(); 
			List<FlInvoiceItemSummary> outOfScopeList = new ArrayList<FlInvoiceItemSummary>(); 
			
			removeDuplicateInvoiceSummaryItem();
			for(FlInvoiceItemSummary invoiceItemSummary : getManagedBean().getInvoice().getFlInvoiceItemSummary()){
				for(FlInvoiceItem invItem : invoiceItemSummary.getFlInvoiceItem()){
					if(!invItem.getGstFlag().getParameterCode().equalsIgnoreCase(ParameterConst.TAX_CODE_OUT_OF_SCOPE)){
						if(!taxList.contains(invoiceItemSummary)){
							taxList.add(invoiceItemSummary);
						}
					}
					if(invItem.getGstFlag().getParameterCode().equalsIgnoreCase(ParameterConst.TAX_CODE_OUT_OF_SCOPE)){
						if(!outOfScopeList.contains(invoiceItemSummary)){
							outOfScopeList.add(invoiceItemSummary);
						}
					}
				}
			}
			if(!taxList.isEmpty()){
				getManagedBean().setTaxSectionItemRoot(new DefaultTreeNode("root", null));
			}
			if(!outOfScopeList.isEmpty()){
				getManagedBean().setOutOfScopeSectionItemRoot(new DefaultTreeNode("root", null));
			}
			InvoiceChargeLineVO chargeLineLv1 = null;
			TreeNode lv2TaxTreeNode = null;
				for(FlInvoiceItemSummary invItemSummLv2 : taxList){
					if(lvl1TaxMapTree.containsKey(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId())) {
						InvoiceChargeLineVO chargeLineLv3 = null;
						for(FlInvoiceItem invItemLv3 : invItemSummLv2.getFlInvoiceItem()){
							if(!invItemLv3.getGstFlag().getParameterCode().equalsIgnoreCase(ParameterConst.TAX_CODE_OUT_OF_SCOPE)){
								if(lvl3TaxMap.containsKey(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode())) {
								}else{
									chargeLineLv3 = new InvoiceChargeLineVO();
									chargeLineLv3.setFlInvoiceSummary(invItemSummLv2);
									chargeLineLv3.setLevel(3);
									chargeLineLv3.setFlInvoiceItem(invItemLv3);
									new DefaultTreeNode(chargeLineLv3, lvl2TaxMapTree.get(invItemSummLv2.getFlFulfilmentItem().getItemCode()));
									lvl3TaxMap.put(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode(), chargeLineLv3);
									grandTotalInvAmt = grandTotalInvAmt.add(invItemLv3.getInvAmt());	
								}
							}
						}
					} else {
						chargeLineLv1 = new InvoiceChargeLineVO();
						chargeLineLv1.setLevel(1);
						chargeLineLv1.setFlInvoiceSummary(invItemSummLv2);
						
						lv1TreeNode = new DefaultTreeNode(chargeLineLv1, getManagedBean().getTaxSectionItemRoot());
						lvl1TaxMapTree.put(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId(), lv1TreeNode);
						InvoiceChargeLineVO chargeLineLv2 = new InvoiceChargeLineVO();
						chargeLineLv2.setLevel(2);
						chargeLineLv2.setFlInvoiceSummary(invItemSummLv2);
						lv2TaxTreeNode = new DefaultTreeNode(chargeLineLv2, lv1TreeNode);
						lvl2TaxMapTree.put(invItemSummLv2.getFlFulfilmentItem().getItemCode(), lv2TaxTreeNode);
						if(lvl3TaxMap == null){
							lvl3TaxMap = new HashMap<String, InvoiceChargeLineVO>();
						}else{
							lvl3TaxMap.clear();
						}
						InvoiceChargeLineVO chargeLineLv3 = null;
						for(FlInvoiceItem invItemLv3 : invItemSummLv2.getFlInvoiceItem()){
							if(!invItemLv3.getGstFlag().getParameterCode().equalsIgnoreCase(ParameterConst.TAX_CODE_OUT_OF_SCOPE)){
								if(lvl3TaxMap.containsKey(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode())) {
									InvoiceChargeLineVO sameInvItemObj = lvl3TaxMap.get(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode());
									sameInvItemObj.getFlInvoiceItem().setTotalReceivedQty(sameInvItemObj.getFlInvoiceItem().getTotalReceivedQty().add(invItemLv3.getReceivedQty()));
									sameInvItemObj.getFlInvoiceItem().setTotalReceivedAmt(sameInvItemObj.getFlInvoiceItem().getTotalReceivedAmt().add(invItemLv3.getReceivedAmt()));
									lvl3TaxMap.put(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode(),sameInvItemObj);
								}else{
									chargeLineLv3 = new InvoiceChargeLineVO();
									chargeLineLv3.setFlInvoiceSummary(invItemSummLv2);
									chargeLineLv3.setLevel(3);
									invItemLv3.setTotalReceivedQty(invItemLv3.getReceivedQty());
									invItemLv3.setTotalReceivedAmt(invItemLv3.getReceivedAmt());
									chargeLineLv3.setFlInvoiceItem(invItemLv3);
									lvl3TaxMap.put(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode(),chargeLineLv3);
								}
									grandTotalInvAmt = grandTotalInvAmt.add(invItemLv3.getInvAmt());
							}
						}
						for(Entry<String, InvoiceChargeLineVO> entrySet : lvl3TaxMap.entrySet()){
							new DefaultTreeNode(entrySet.getValue(), lv2TaxTreeNode);
						}
					}
				}
			
			getManagedBean().setInvAmtGrandTotal(grandTotalInvAmt);
			
			TreeNode lv2OosTreeNode = null;
			BigDecimal grandTotalOosAmt = new BigDecimal(0);
				for(FlInvoiceItemSummary invItemSummLv2 : outOfScopeList){
					if(lvl1OOSMap.containsKey(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId())) {
						InvoiceChargeLineVO chargeLineLv3 = null;
						for(FlInvoiceItem invItemLv3 : invItemSummLv2.getFlInvoiceItem()){
							if(invItemLv3.getGstFlag().getParameterCode().equalsIgnoreCase(ParameterConst.TAX_CODE_OUT_OF_SCOPE)){
								
								if(lvl3OosMap.containsKey(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode())) {
								}else{
									chargeLineLv3 = new InvoiceChargeLineVO();
									chargeLineLv3.setFlInvoiceSummary(invItemSummLv2);
									chargeLineLv3.setLevel(3);
									chargeLineLv3.setFlInvoiceItem(invItemLv3);
									new DefaultTreeNode(chargeLineLv3, lvl2OosMapTree.get(invItemSummLv2.getFlFulfilmentItem().getItemCode()));
									lvl3OosMap.put(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode(), chargeLineLv3);
									grandTotalOosAmt = grandTotalOosAmt.add(invItemLv3.getInvAmt());	
								}
							}
						}
					} else {
						chargeLineLv1 = new InvoiceChargeLineVO();
						chargeLineLv1.setLevel(1);
						chargeLineLv1.setFlInvoiceSummary(invItemSummLv2);
						
						lv1TreeNode = new DefaultTreeNode(chargeLineLv1, getManagedBean().getOutOfScopeSectionItemRoot());
						lvl1OOSMap.put(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId(), lv1TreeNode);
						
						InvoiceChargeLineVO chargeLineLv2 = new InvoiceChargeLineVO();
						chargeLineLv2.setLevel(2);
						chargeLineLv2.setFlInvoiceSummary(invItemSummLv2);
						lv2OosTreeNode = new DefaultTreeNode(chargeLineLv2, lv1TreeNode);
						lvl2OosMapTree.put(invItemSummLv2.getFlFulfilmentItem().getItemCode(), lv2OosTreeNode);
						if(lvl3OosMap == null){
							lvl3OosMap = new HashMap<String, InvoiceChargeLineVO>();
						}else{
							lvl3OosMap.clear();
						}
						InvoiceChargeLineVO chargeLineLv3 = null;
						for(FlInvoiceItem invItemLv3 : invItemSummLv2.getFlInvoiceItem()){
							if(invItemLv3.getGstFlag().getParameterCode().equalsIgnoreCase(ParameterConst.TAX_CODE_OUT_OF_SCOPE)){
								if(lvl3OosMap.containsKey(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode())) {
									InvoiceChargeLineVO sameInvItemObj = lvl3OosMap.get(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode());
									sameInvItemObj.getFlInvoiceItem().setTotalReceivedQty(sameInvItemObj.getFlInvoiceItem().getTotalReceivedQty().add(invItemLv3.getReceivedQty()));
									sameInvItemObj.getFlInvoiceItem().setTotalReceivedAmt(sameInvItemObj.getFlInvoiceItem().getTotalReceivedAmt().add(invItemLv3.getReceivedAmt()));
									lvl3OosMap.put(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode(),sameInvItemObj);
								}else{
									chargeLineLv3 = new InvoiceChargeLineVO();
									chargeLineLv3.setFlInvoiceSummary(invItemSummLv2);
									chargeLineLv3.setLevel(3);
									invItemLv3.setTotalReceivedQty(invItemLv3.getReceivedQty());
									invItemLv3.setTotalReceivedAmt(invItemLv3.getReceivedAmt());
									chargeLineLv3.setFlInvoiceItem(invItemLv3);
									lvl3OosMap.put(invItemSummLv2.getFlFulfilmentItem().getFulfilmentItemId().toString()+invItemSummLv2.getFlFulfilmentItem().getPmUomId()+invItemLv3.getGstFlag().getParameterCode(),chargeLineLv3);
								}
								grandTotalOosAmt = grandTotalOosAmt.add(invItemLv3.getInvAmt());
							}
						}
						for(Entry<String, InvoiceChargeLineVO> entrySet : lvl3OosMap.entrySet()){
							new DefaultTreeNode(entrySet.getValue(), lv2OosTreeNode);
						}
					}
				}
			
			
			getManagedBean().setOosInvAmtGrandTotal(grandTotalOosAmt);
			//Clause under Invoice  Item page only display if any of the invoice item is 'RS'and 'OS'
			
			getManagedBean().setTaxItemTableClause(false);
			getManagedBean().setOosItemTableClause(false);
			for(FlInvoiceItemSummary invItemsSummary:getManagedBean().getInvoice().getFlInvoiceItemSummary()){
				if(getManagedBean().isTaxItemTableClause()){
					break;
				}
				for(FlInvoiceItem invItems:invItemsSummary.getFlInvoiceItem()){
					if(invItems.getGstFlag().getParameterCode().equalsIgnoreCase(ParameterConst.TAX_CODE_RELIEF)){
						getManagedBean().setTaxItemTableClause(true);
						break;
					}
				}
			}
			for(FlInvoiceItemSummary invItemsSummary:getManagedBean().getInvoice().getFlInvoiceItemSummary()){
				if(getManagedBean().isOosItemTableClause()){
					break;
				}
				for(FlInvoiceItem invItems:invItemsSummary.getFlInvoiceItem()){
					if(invItems.getGstFlag().getParameterCode().equalsIgnoreCase(ParameterConst.TAX_CODE_OUT_OF_SCOPE)){
						getManagedBean().setOosItemTableClause(true);
						break;
					}
					
				}
			}
		}
		
		/**
		 * Navigate to other page based on document type.
		 *
		 * @param docType the doc type
		 * @return the string
		 */
		
		public String navigateDelOrder(FlDeliveryOrder delOrder){
				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				IDeliveryOrderSO orderSO = (IDeliveryOrderSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IDeliveryOrderSO.class);
				FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
				reqDTO.setDeliveryOrderId(delOrder.getDeliveryOrderId());
				reqDTO.setDocType(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				FulfilmentResDTO resDTO = orderSO.retrieveDeliveryOrderById(reqDTO);
				DeliveryOrderBackingBean doBB = new DeliveryOrderBackingBean();
				doBB.renewSesionObject();
				FacesContext fc = FacesContext.getCurrentInstance();
				doBB.getManagedBean().setFlDeliveryOrder(resDTO.getFlDeliveryOrder());
				doBB.getManagedBean().setFulfilmentRequest(resDTO.getFlDeliveryOrder().getFlRequest());
				doBB.getManagedBean().setFulfilmentOrder(resDTO.getFlDeliveryOrder().getFlFulfilmentOrder());
				doBB.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
				doBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
				doBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());		
				doBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
				doBB.getManagedBean().setHyperLinkPage(true);
				doBB.getManagedBean().setPrevHideRemarks(flBB.getManagedBean().isHideRemarks());
				flBB.getManagedBean().setHideRemarks(true);
				flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
				flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
				doBB.getManagedBean().getFulfilmentOrder().setStatus(orderSO.retrieveDelOrderStatus(reqDTO).getPmStatus());
				return NAV_DELIVERYORDER_PAGE;
		}
		
	/**
	 * Navigate to other page based on document type.
	 *
	 * @param docType the doc type
	 * @return the string
	 */

	public String navigateFulfilmentNote(FlDeliveryOrder delOrder) {
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		IFulfilmentSO iFulfilmentSO = (IFulfilmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentSO.class);
		IReceivedNoteSO receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReceivedNoteSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setFulfilmentNoteId(delOrder.getFulfilmentNote().getFulfilmentNoteId());
		reqDTO.setDocType(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
		FulfilmentResDTO resDTO = receivedNoteSO.retrieveReceivedNoteByReceivedNoteId(reqDTO);
		ReceivedNoteBackingBean recNoteBB = new ReceivedNoteBackingBean();
		recNoteBB.renewSesionObject();
		recNoteBB.getManagedBean().setSelectedTabIndex(0);
		recNoteBB.getManagedBean().setFulfilmentNote(resDTO.getFulfilmentNote());
		recNoteBB.getManagedBean().setFulfilmentRequest(resDTO.getFulfilmentNote().getFlRequest());
		recNoteBB.getManagedBean().getFulfilmentNote().setFlRequest(resDTO.getFulfilmentNote().getFlRequest());
		reqDTO.setDeliveryOrderId(resDTO.getFulfilmentNote().getFlDeliveryOrder().getDeliveryOrderId());
		recNoteBB.getManagedBean().setPmUser(reqDTO.getPmUser());
		for(FlFulfilmentNoteItem fulfilmentNoteItem: resDTO.getFulfilmentNote().getFlFulfilmentNoteItem()){
			recNoteBB.getManagedBean().setSelectedItem(fulfilmentNoteItem);
		}
		recNoteBB.getManagedBean().getFulfilmentNote().setFlFulfilmentNoteItem(resDTO.getFulfilmentNote().getFlFulfilmentNoteItem());
		if(recNoteBB.getManagedBean().getFlApprover() == null){
			FlActor actor = iFulfilmentSO.retrieveFlActors(recNoteBB.getManagedBean().getFulfilmentNote().getFlRequest().getDocType(), recNoteBB.getManagedBean().getFulfilmentNote().getFlRequest().getFulfilmentReqId(), RoleCode.FL_APPROVER.value());
			if(actor != null){
				recNoteBB.getManagedBean().setFlApprover(actor.getUser());
				recNoteBB.getManagedBean().setAcknowledgeDate(actor.getActionDate());
			}
		}

		FacesContext fc = FacesContext.getCurrentInstance();
		recNoteBB.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
		recNoteBB.getManagedBean().setPreviousRemarkList(flBB.getManagedBean().getRemarkList());
		recNoteBB.getManagedBean().setPreviousAttachmentList(flBB.getManagedBean().getAttachmentList());	
		recNoteBB.getManagedBean().setAcknowledged(true);
		recNoteBB.getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);
		recNoteBB.getManagedBean().setHyperLinkPage(true);
		recNoteBB.getManagedBean().setPrevHideRemarks(flBB.getManagedBean().isHideRemarks());
		flBB.getManagedBean().setHideRemarks(true);
		flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
		flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
		for(FlFulfilmentNoteItem noteItem : recNoteBB.getManagedBean().getFulfilmentNote().getFlFulfilmentNoteItem()){
			if(noteItem.getFulfilmentNoteItemId() != null){
				reqDTO.setDocId(noteItem.getFulfilmentNoteItemId());
				reqDTO.setDocType(ParameterConst.DOCTYPE_FULFILMENT_NOTE_ITEM);
				resDTO = receivedNoteSO.retrieveRejectionRemarksHistory(reqDTO);
				noteItem.setRejectionRemarks(resDTO.getFNIRejectionRemarks());
				recNoteBB.initializeRejectionRemarks(recNoteBB.getManagedBean().getFulfilmentNote().getFlWorkflowStatus() ,resDTO.getFNIRejectionRemarks(),noteItem,false);
			}else{
				noteItem.getRejectionRemarks().add(new FlRemark());
			}
		}

		return NAV_FULFILMENTNOTE_PAGE;
	}
		
	/**
	 * Sets the text before sign.
	 *
	 * @param outcome the new text before sign
	 */
	private void setTextBeforeSign(String outcome) {
		StringBuffer sb = new StringBuffer();
		if(getManagedBean().getInvoice().isBrandNew()) {
			sb.append(getManagedBean().getInvoice().getFlFulfilmentOrder().getDocNo()+"|");
			sb.append(ParameterConst.DOCTYPE_INVOICE+"|");
			sb.append(getInvoiceAmt()+"|");
			sb.append(getFacesScopes().getUserSessionBean().getIdentificationNo());
		} else {
			sb.append(getManagedBean().getInvoice().getFlFulfilmentOrder().getDocNo()+"|");
			sb.append(getManagedBean().getInvoice().getInvoiceNo()+"|");
			sb.append(outcome+"|");
			sb.append(getFacesScopes().getUserSessionBean().getIdentificationNo());
		}
		getManagedBean().setTextBeforeSign(sb.toString());
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
	
	public List<InvoiceChargeLineVO> getEmptyTableList() {
		return emptyTableList;
	}

	public void setEmptyTableList(List<InvoiceChargeLineVO> emptyTableList) {
		this.emptyTableList = emptyTableList;
	}
	
	private void removeDuplicateInvoiceSummaryItem() {
		Map<Long,FlInvoiceItemSummary> checkDupMap = new HashMap<Long,FlInvoiceItemSummary>();
		List<FlInvoiceItemSummary> removeInvSummItemObj = new ArrayList<FlInvoiceItemSummary>(); 
		for(FlInvoiceItemSummary invoiceItemSummary : getManagedBean().getInvoice().getFlInvoiceItemSummary()){
			if(!checkDupMap.containsKey(invoiceItemSummary.getFlFulfilmentItem().getFulfilmentItemId())){
				checkDupMap.put(invoiceItemSummary.getFlFulfilmentItem().getFulfilmentItemId(), invoiceItemSummary);
			}else{
				removeInvSummItemObj.add(checkDupMap.get(invoiceItemSummary.getFlFulfilmentItem().getFulfilmentItemId()));
			}
		}
		getManagedBean().getInvoice().getFlInvoiceItemSummary().removeAll(removeInvSummItemObj);
	}
	
	//Start  #21229
	/**
	 * Gets the populate factoring account list based on supplier id.
	 *
	 * @return the populate factoring account list
	 */
	public List<SelectItem> getFactoringAccountList() {
		IFinancialOrgSO financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IFinancialOrgSO.class);
		FinancialOrgReqDTO finOrgReqDTO = new FinancialOrgReqDTO();
		FinancialOrgResDTO finOrgResDTO = null;
		List<SelectItem> populateAccountFactoringList = new ArrayList<SelectItem>();
		
		//List of Account no.
		finOrgReqDTO.setFinancialOrgId(getManagedBean().getInvoice().getFlFulfilmentOrder().getPmFinancialOrgId());
		finOrgResDTO = financialOrgSO.retriveFinAccListByFinOrgId(finOrgReqDTO);
	
		
		for (PmFinancialAcc obj : finOrgResDTO.getPmFinancialAccList()) {
			String account = obj.getAccountNo();
			obj.getPmPaymentBankOrg().getFinOrgName();
			obj.getPmPaymentBankOrg();
			if (obj.getPmPaymentBankOrg() != null){
				account = account + "  " + obj.getPmPaymentBankOrg().getFinOrgName();
			}
			populateAccountFactoringList.add(new SelectItem(obj.getAccountNo(), account));
			
			if(!getManagedBean().getFactoringAccountMap().containsKey(obj.getAccountNo())) {
				getManagedBean().getFactoringAccountMap().put(obj.getAccountNo(), obj.getPmPaymentBankOrg());
			}
		}

		return populateAccountFactoringList;
	}
	
	
	/**
	 * download file Garis_Panduan_Invoice_Revise.pdf.
	 */
	public void downloadFile(String filePath) throws IOException {
		StringBuilder downloadPath = new StringBuilder();
		downloadPath.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)).append(filePath);
		
		super.downloadFile(downloadPath.toString());
	}
	
	/**
	 * Initialize remark .
	 * <p> get remarks lists.
	 * @param flWorkflowStatus the fl workflow status
	 * @param remarkList the remark list
	 */
	public void initializeRemarkRevision(FlWorkflowStatus flWorkflowStatus ,List<FlRemark> remarkList, boolean isHideRemarks) {
		FacesScopes facesScopes = new FacesScopes();

		boolean flWorkflowStatusMatch = false;
		
		if(remarkList.size()>0  ) {
				
				flWorkflowStatusMatch = true;
		}
				
	
		if (flWorkflowStatusMatch) {			
			getManagedBean().setRemarkRevise(remarkList);
		}
	}
	
	
	/**
	 * Factoring info action listener.
	 */
	public void factoringInfoActionListener() {
		if(null != getManagedBean().getInvoice().getAccountNo()){
	
			PmFinancialOrg factoringBank = getManagedBean().getFactoringAccountMap().get(getManagedBean().getInvoice().getAccountNo());
			if(factoringBank!=null) {
				getManagedBean().getInvoice().setPaymentBank(factoringBank);			
				getManagedBean().getInvoice().setPaymentBankName(factoringBank.getFinOrgName());
				
			}
		}

	}
	
	//End  #21229
	
	// 26279 - start
	/**
	 * View list of Disciplinary Action
	 */
	public void viewDisciplinaryAction() {
		SmSupplier supplier;

		if (getManagedBean().getFulfilmentRequest().getSmSupplier() != null) {
			supplier = getManagedBean().getFulfilmentRequest().getSmSupplier();
			supplier.setFlagSupplier(true);
			supplier.setFlagDASuppProfile(true);

			List<DisciplinaryActionVO> disciplinaryActionVO = ViewSuppProfileBackingBean
					.getDisciplinaryActionVO(supplier);
			getManagedBean().setDisciplinaryActionVO(disciplinaryActionVO);
			RequestContext.getCurrentInstance().execute("disciplinaryActionHistoryDlg.show();");
		}
	}

	// 26279 - end
	
	// #35508 fl audit trail - insert method
	/**
	 * insert audit for invoice
	 * 
	 * @param invBean
	 *            invoice manage bean
	 * @param action
	 *            action type for audit
	 * 
	 */
	private void insertAuditFulfilmentInvoice(String action) {
		Long userAuditId = getUserIdSessionFromLoginId();
		IAuditFulfilmentSO auditFulfilmentSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.ORDER_FULFILLMENT, IAuditFulfilmentSO.class);

		FlAuditTrail auFlAuditTrailObj;
		List<FlAuditTrail> objList = new ArrayList<>();

		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
			auFlAuditTrailObj = setAuditFulfilmentInvoice(userAuditId, action, null, null, null);
			objList.add(auFlAuditTrailObj);
		} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)) {
			// audit fl for supplier invoice reference
			FlInvoice flinv = getManagedBean().getInvoice();
			if (!flinv.getSupplierInvoiceRef().equals(getManagedBean().getOriginalSupplierInvRef())) {
				auFlAuditTrailObj = setAuditFulfilmentInvoice(userAuditId, action,
						AuditFLConst.FL_AU_INV_NO_FIELDLABELCODE, getManagedBean().getOriginalSupplierInvRef(),
						flinv.getSupplierInvoiceRef());
				objList.add(auFlAuditTrailObj);
			}
			String newBankAccNoNameInv = flinv.getAccountNo() + " " + flinv.getPaymentBankName();
			if (!newBankAccNoNameInv.equals(getManagedBean().getOriginalBankAccNoName())) {
				auFlAuditTrailObj = setAuditFulfilmentInvoice(userAuditId, action,
						AuditFLConst.FL_AU_BANK_ACC_FIELDLABELCODE, getManagedBean().getOriginalBankAccNoName(),
						newBankAccNoNameInv);
				objList.add(auFlAuditTrailObj);
			}
		}

		if (!objList.isEmpty()) {
			AuFLReqDTO auFlDTO = new AuFLReqDTO();
			auFlDTO.setFlAuditTrailList(objList);
			auditFulfilmentSO.insertAuditFLDetails(auFlDTO);
		}
	}

	/**
	 * set audit for invoice
	 * 
	 * @param invBean
	 *            invoice manage bean
	 * @param userAudit
	 *            pmuser audit
	 * @param action
	 *            action type for audit
	 * @param fieldLabelCode
	 *            field label code for audit
	 * @param oldValueInv
	 *            value before change
	 * @param newValueInv
	 *            value after change
	 *            
	 * @return FlAuditTrail FlAuditTrail obj
	 * 
	 */
	public FlAuditTrail setAuditFulfilmentInvoice(Long userAuditId, String action, String fieldLabelCode,
			String oldValueInv, String newValueInv) {
		// add audit set
		FlInvoice flinv = getManagedBean().getInvoice();

		Long orgProfileId;
		Long statusId;
		String orgName;
		String auditActionType;
		String roleCode;

		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)) {
			// audit fl supplier
			auditActionType = AuditFLConst.AUD_ACTION_TYPE_MODIFY;
			orgProfileId = flinv.getFlRequest().getSmSupplier().getSupplierId();
			orgName = flinv.getFlRequest().getSmSupplier().getCompanyName();
			statusId = FulfilmentStatusConst.IN_PENDING_MODIFICATION_APPROVAL;
			roleCode = getUserRole();
		} else {
			// audit fl approver
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(userAuditId);
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			PmOrgProfile pmOrgAudit = userResDTO.getUserOrgList().get(0).getPmOrgProfile();

			auditActionType = null;
			orgProfileId = pmOrgAudit.getOrgProfileId();
			orgName = pmOrgAudit.getOrgName();
			statusId = FulfilmentStatusConst.IN_SUBMITTED;
			roleCode = RoleCode.FL_APPROVER.value();
		}
		// insert for DO
		FlAuditTrail auFlAuditTrail = new FlAuditTrail();
		auFlAuditTrail.setDocType(ParameterConst.DOCTYPE_INVOICE);
		auFlAuditTrail.setRoleCode(roleCode);
		auFlAuditTrail.setOrgProfileId(orgProfileId);
		auFlAuditTrail.setOrgName(orgName);
		auFlAuditTrail.setDocId(flinv.getInvoiceId());
		auFlAuditTrail.setDocNo(flinv.getInvoiceNo());
		auFlAuditTrail.setStatusId(statusId);
		auFlAuditTrail.setActionType(auditActionType);
		auFlAuditTrail.setFulfilmentRequestId(flinv.getFlRequest().getFulfilmentReqId());
		auFlAuditTrail.setOldValue(oldValueInv);
		auFlAuditTrail.setNewValue(newValueInv);
		auFlAuditTrail.setCreatedBy(userAuditId);
		auFlAuditTrail.setChangedBy(userAuditId);
		auFlAuditTrail.setFieldLabelCode(fieldLabelCode);

		return auFlAuditTrail;
	}
	// #35508 fl audit trail
	
	//36754 -Start
	/**
	 * Validates if an order can proceed based on the following checks:
	 * 1. Check quantities of ordered and received items fully fulfilled
	 * 2. Check Stop Delivery Instruction (SDI) with approval status if Quantities is not fully fulfilled 
	 * 3. Check if any Pending cancellations for Delivery Orders (DO) or Fulfilment Notes (FRN).
	 *
	 * @throws EpException if the order validation fails with a specific error code.
	 */
	private void validateOrderFulfiledAndDOFRNPendingCancellation() {
		IFulfilmentOrderSO orderSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		
		reqDTO.setFlRequestId(getManagedBean().getInvoice().getFlRequest().getFulfilmentReqId());
		reqDTO.setFlOrderId(getManagedBean().getInvoice().getFlFulfilmentOrder().getFulfilmentOrderId());
		FulfilmentResDTO resDTO= orderSO.verifyOrderQtySDIAndDOFRNPendingCancellation(reqDTO);
		Boolean allowToProceed = resDTO.isAllowToProceed();		
		if(!allowToProceed){
			throw new EpException(MessageConst.ERROR_740069);
		}	
	}
	//36754 -End
}
