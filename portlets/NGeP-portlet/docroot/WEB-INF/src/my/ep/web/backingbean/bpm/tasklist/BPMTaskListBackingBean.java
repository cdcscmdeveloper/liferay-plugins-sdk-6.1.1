package my.ep.web.backingbean.bpm.tasklist;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.FLProcessNameConst;
import my.ep.constants.PPProcessNameConst;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.bpm.tasklist.BPMTaskListManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pm.helper.StatusHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;

import org.apache.log4j.Logger;

import com.liferay.compat.portal.util.PortalUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

@ManagedBean(name = "bpmTasklistBackingBean")
@RequestScoped
public class BPMTaskListBackingBean extends BaseBackingBean<BPMTaskListManagedBean> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(BPMTaskListBackingBean.class);
	
	private String language = new FacesScopes().getUserLocale().getLanguage();
	private static final String planDetailConstStr = "plan-detail";
	private static final String mofApplicationConstStr = "mof-application";
	private static final String fulfilmentDetailsConstStr = "fulfilment-details";
	private static final String contractReqadminConstStr = "contract-reqadmin";
	private static final String contractRequestConstStr = "contract-request";
	private static final String shippingInstructionConstStr = "shipping-instruction";
	private static final String contractOrderConstStr = "contract-order";
	private static final String purchaseOrderConstStr = "purchase-order";
	private static final String invoiceConstStr = "invoice";
	private static final String deliveryOrderConstStr = "delivery-order";
	private static final String purchaseRequestConstStr = "purchase-request";

	private ISearchBean<PmParameterDesc> docTypeSearchBean;

	public BPMTaskListBackingBean() {

		if(this.getManagedBean().getBpmMap()==null){
			this.getManagedBean().setBpmMap(new HashMap<Object, Object>());
			Map<String, String> temp = new HashMap<String, String>();

			temp.put("UNSPSC Code Request", "review-unspsc-code");
			temp.put("Extension Code Request", "review-extension");
			temp.put("Extension Code Maintenance", "review-extension-maintenance");

			// Procurement Plan - Start
			temp.put(PPProcessNameConst.EXPENDITURE_PREPARATION_PROCESS_NAME, planDetailConstStr);
			temp.put(PPProcessNameConst.PROCUREMENT_PREPARATION_PROCESS_NAME, planDetailConstStr);
			temp.put("Start Expenditure Preparation", planDetailConstStr);
			temp.put("Start Procurement Preparation", planDetailConstStr);
			temp.put("Access Application", "access-application-details");
			// Procurement Plan - End

			// Supplier Management - Start
			//temp.put("Disciplinary Action", "disciplinary-action");
			temp.put("Site Visit Manage", "site-visit");
			temp.put("MOF Supplier Site Visit", mofApplicationConstStr);
			temp.put("MOF Supplier Registration", mofApplicationConstStr);
			temp.put("MOF Account Maintain", mofApplicationConstStr);
			temp.put("Start MOF Supplier Registration", mofApplicationConstStr);
			temp.put("Start MOF Account Maintain", mofApplicationConstStr);
			temp.put("MOF Registration Cancellation", mofApplicationConstStr);
			temp.put("MOF Application Cancellation", mofApplicationConstStr);
			temp.put("User Add Additional", "soft-cert-payment");

			// s1 module
			temp.put(SCProcessNameConst.REVIEW_DIRECT_PURCHASE, SourcingConstants.NAV_PAGE_REVIEW_REQUEST_NOTE);
			temp.put(SCProcessNameConst.START_DIRECT_PURCHASE_CREATION, SourcingConstants.NAV_PAGE_REVIEW_REQUEST_NOTE);
			temp.put(SCProcessNameConst.DIRECT_PURCHASE_PROCESS_NAME, SourcingConstants.NAV_PAGE_REVIEW_REQUEST_NOTE);
			temp.put(SCProcessNameConst.SUBMIT_DIRECT_PURCHASE, SourcingConstants.NAV_PAGE_REVIEW_REQUEST_NOTE);
			temp.put(SCProcessNameConst.SUPPLIER_CODIFICATION_PROCESS_NAME, SourcingConstants.NAV_PAGE_REQUEST_NOTE_CODE_ASSIGNMENT);

			temp.put(SCProcessNameConst.PURCHASE_INQUIRY_PROCESS_NAME, SourcingConstants.NAV_PAGE_REVIEW_PURCHASE_INQUIRY);
			//temp.put(SCProcessNameConst.PURCHASE_IN_DEFAULT_PROCESS_NAME, SourcingConstants.NAV_PAGE_REVIEW_PURCHASE_IN_DEFAULT); //#28865				
			temp.put(SCProcessNameConst.PURCHASE_IN_DEFAULT_PROCESS_NAME + SCProcessNameConst.REQUISITIONER_RESPONSE_PURCHASE_IN_DEFAULT, SourcingConstants.NAV_PAGE_REVIEW_PURCHASE_IN_DEFAULT); //#28865
			temp.put(SCProcessNameConst.PURCHASE_IN_DEFAULT_PROCESS_NAME + SCProcessNameConst.VERIFY_PID, SourcingConstants.NAV_PAGE_REVIEW_PURCHASE_IN_DEFAULT); //#28865
			temp.put(SCProcessNameConst.PURCHASE_IN_DEFAULT_PROCESS_NAME + SCProcessNameConst.APPROVE_PID, SourcingConstants.NAV_PAGE_REVIEW_PURCHASE_IN_DEFAULT); //#28865
			temp.put(SCProcessNameConst.PURCHASE_IN_DEFAULT_PROCESS_NAME + SCProcessNameConst.SUPPLIER_RESPONSE_PURCHASE_IN_DEFAULT, SourcingConstants.NAV_PAGE_RESPONSE_PURCHASE_IN_DEFAULT); //#28865
			temp.put(SCProcessNameConst.PURCHASE_IN_DEFAULT_PROCESS_NAME + SCProcessNameConst.REQUISITIONER_REVISION, SourcingConstants.NAV_PAGE_REVIEW_PURCHASE_IN_DEFAULT); //#9133
			temp.put(SCProcessNameConst.START_PURCHASE_IN_DEFAULT + SCProcessNameConst.SUBMIT_PURCHASE_IN_DEFAULT, SourcingConstants.NAV_PAGE_REVIEW_PURCHASE_IN_DEFAULT); //#9133
			temp.put(SCProcessNameConst.START_PURCHASE_INQUIRY_CREATION + SCProcessNameConst.START_PURCHASE_INQUIRY, SourcingConstants.NAV_PAGE_REVIEW_PURCHASE_INQUIRY);

			temp.put(SCProcessNameConst.START_SIMPLE_QUOTE_CREATION, SourcingConstants.NAV_PAGE_REVIEW_SIMPLE_QUOTE);
			temp.put(SCProcessNameConst.START_SIMPLE_QUOTE_CREATION + SCProcessNameConst.SUBMIT_SIMPLE_QUOTE, SourcingConstants.NAV_PAGE_REVIEW_SIMPLE_QUOTE);
			temp.put(SCProcessNameConst.SIMPLE_QUOTE_PROCESS_NAME, SourcingConstants.NAV_PAGE_REVIEW_REQUEST_NOTE);
			temp.put(SCProcessNameConst.SIMPLE_QUOTE_PROCESS_NAME + SCProcessNameConst.PREPARE_REQUEST_NOTE, SourcingConstants.NAV_PAGE_REVIEW_REQUEST_NOTE);

			// Contract Management
			temp.put(CTProcessNameConst.CIRCULAR_MAINTENANCE_INITIATION_PROCESS_NAME + ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR, "central-contract-circular");
			temp.put(CTProcessNameConst.CIRCULAR_MAINTENANCE_INITIATION_PROCESS_NAME + ParameterConst.DOCTYPE_PANEL_CIRCULAR, "panel-circular");
			temp.put(CTProcessNameConst.CIRCULAR_MAINTENANCE_PROCESS_NAME + ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR, "central-contract-circular");
			temp.put(CTProcessNameConst.CIRCULAR_MAINTENANCE_PROCESS_NAME + ParameterConst.DOCTYPE_PANEL_CIRCULAR, "panel-circular");
			temp.put(CTProcessNameConst.CIRCULAR_CANCELLATION_PROCESS_NAME, "cancel-circular-approval");
			temp.put(CTProcessNameConst.FACTORING_REQUEST_PROCESS_NAME + CTProcessNameConst.ACK_FACTORING_REQUEST_HT_NAME, "review-factoring-request");
			temp.put(CTProcessNameConst.FACTORING_REQUEST_PROCESS_NAME + CTProcessNameConst.ENDORSE_FACTORING_REQUEST_HT_NAME, "review-factoring-request");
			temp.put(CTProcessNameConst.CONTRACT_CREATION_PROCESS_NAME, fulfilmentDetailsConstStr);
			temp.put(CTProcessNameConst.CONTRACT_INITIATION_PROCESS_NAME, fulfilmentDetailsConstStr);
			temp.put(CTProcessNameConst.CONTRACT_SUSPEND_ACTIVE_INIT_PROCESS_NAME, "contract-suspension");
			temp.put(CTProcessNameConst.CONTRACT_SUSPEND_ACTIVE_PROCESS_NAME, "contract-suspension");
			temp.put(CTProcessNameConst.REQUEST_CONTRACT_ADMIN_INIT_PROCESS_NAME, contractReqadminConstStr);
			temp.put(CTProcessNameConst.REQUEST_CONTRACT_ADMIN_PROCESS_NAME, contractReqadminConstStr);
			temp.put(CTProcessNameConst.REQUEST_CONTRACT_ADMIN_APPROVER_INIT_PROCESS_NAME, contractReqadminConstStr);
			temp.put(CTProcessNameConst.CONTRACT_AMENDMENT_INITIATION_PROCESS_NAME + ParameterConst.DOCTYPE_CONTRACT_AMENDMENT, "contract-amendment");
			temp.put(CTProcessNameConst.CONTRACT_AMENDMENT_INITIATION_PROCESS_NAME + ParameterConst.DOCTYPE_CONTRACT_UPDATE, fulfilmentDetailsConstStr);
			temp.put(CTProcessNameConst.CONTRACT_AMENDMENT_INITIATION_PROCESS_NAME + ParameterConst.DOCTYPE_NOTIFICATION_SETTING, fulfilmentDetailsConstStr);
			temp.put(CTProcessNameConst.CONTRACT_AMENDMENT_INITIATION_PROCESS_NAME + ParameterConst.DOCTYPE_AMENDMENT_WITHOUT_SUPPLEMENT, fulfilmentDetailsConstStr);
			temp.put(CTProcessNameConst.CONTRACT_AMENDMENT_PROCESS_NAME + ParameterConst.DOCTYPE_CONTRACT_AMENDMENT, "contract-amendment");
			temp.put(CTProcessNameConst.CONTRACT_AMENDMENT_PROCESS_NAME + ParameterConst.DOCTYPE_NOTIFICATION_SETTING, fulfilmentDetailsConstStr);
			temp.put(CTProcessNameConst.CONTRACT_AMENDMENT_PROCESS_NAME + ParameterConst.DOCTYPE_CONTRACT_UPDATE, fulfilmentDetailsConstStr);
			temp.put(CTProcessNameConst.CONTRACT_AMENDMENT_PROCESS_NAME + ParameterConst.DOCTYPE_AMENDMENT_WITHOUT_SUPPLEMENT, fulfilmentDetailsConstStr);
			temp.put(CTProcessNameConst.CONTRACT_TERMINATION_INIT_PROCESS_NAME, "contract-termination");
			temp.put(CTProcessNameConst.CONTRACT_TERMINATION_PROCESS_NAME, "contract-termination");
			temp.put(CTProcessNameConst.AGREEMENT_CREATION_PROCESS_NAME, "contract-agreement");
			temp.put(CTProcessNameConst.AGREEMENT_INITIATION_PROCESS_NAME, "contract-agreement");

			temp.put(CTProcessNameConst.BANK_ACCOUNT_CHANGE_PROCESS_NAME + CTProcessNameConst.REVIEW_CB_REQUEST_HT_NAME, "change-bank-account");
			
			temp.put(CTProcessNameConst.CONTRACT_OWNER_MANAGEMENT_NAME, "contract-owner-management");
			temp.put(CTProcessNameConst.CONTRACT_OWNER_MANAGEMENT_INITIATION_PROCESS_NAME, "contract-owner-management");
			

			// Fulfilment Management - Start
			temp.put(FLProcessNameConst.PHIS_CONTRACT_REQ_INITIATION_PROCESS_NAME + FLProcessNameConst.SUBMIT_PHIS_CR, contractRequestConstStr);
			temp.put(FLProcessNameConst.CONTRACT_REQ_INITIATION_PROCESS_NAME + FLProcessNameConst.SUBMIT_CR_HT, contractRequestConstStr);
			temp.put(FLProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.REVIEW_CR_HT, contractRequestConstStr);
			temp.put(FLProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.REVISE_CR_HT, contractRequestConstStr);
			temp.put(FLProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.CREATE_NORMAL_CO_HT, contractRequestConstStr);
			temp.put(FLProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.REVISE_MTO_CR_HT, contractRequestConstStr);
			temp.put(FLProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.REVIEW_MTO_CR_HT, contractRequestConstStr);
			temp.put(FLProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.CREATE_CO_HT, contractRequestConstStr);
			temp.put(FLProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.CREATE_NEW_CO_HT, contractRequestConstStr);
			temp.put(FLProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.INTIATE_MTO_CR_HT, contractRequestConstStr);
			temp.put(FLProcessNameConst.CONTRACT_REQ_INITIATION_PROCESS_NAME + FLProcessNameConst.CREATE_NEW_CO_HT, contractRequestConstStr);
			temp.put(FLProcessNameConst.CONTRACT_REQ_INITIATION_PROCESS_NAME + FLProcessNameConst.CREATE_NORMAL_CO_HT, contractRequestConstStr);
			temp.put(FLProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.CREATE_NEW_MTO_CO_HT, contractRequestConstStr);

			temp.put(FLProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.CREATE_SI_HT, shippingInstructionConstStr);
			temp.put(FLProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.REVIEW_SI_HT, shippingInstructionConstStr);
			temp.put(FLProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.REVISE_SI_HT, shippingInstructionConstStr);

			temp.put(FLProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.MTO_CO_ACK_HT, contractOrderConstStr);
			temp.put(FLProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.CO_ACK_HT, contractOrderConstStr);
			temp.put(FLProcessNameConst.RESUBMIT_ORDER_CANCELLATION + ParameterConst.DOCTYPE_CONTRACT_ORDER, contractOrderConstStr);
			temp.put(FLProcessNameConst.RESUBMIT_ORDER_CANCELLATION + ParameterConst.DOCTYPE_PURCHASE_ORDER, purchaseOrderConstStr);

			temp.put(FLProcessNameConst.INVOICE_AND_PAYMENT_CREATION_PROCESS_NAME + FLProcessNameConst.CREATE_INVOICE, invoiceConstStr);
			temp.put(FLProcessNameConst.INVOICE_AND_PAYMENT_CREATION_PROCESS_NAME + FLProcessNameConst.PERFORM_PAYMNET_MATCH_HT, "payment-match");
			temp.put(FLProcessNameConst.INVOICE_AND_PAYMENT_CREATION_PROCESS_NAME + FLProcessNameConst.REVIEW_GST, "review-gst");

			temp.put(FLProcessNameConst.DELIVERY_ORDER_FULFILMENT_PROCESS_NAME, "fulfilment-received-note");
			temp.put(FLProcessNameConst.FRN_CANCELLATION_PROCESS_NAME, "fulfilment-received-note");
			temp.put(FLProcessNameConst.DELIVERY_ORDER_FL_INITIATION_PROCESS_NAME, deliveryOrderConstStr);
			temp.put(FLProcessNameConst.DAN_GENERATION_PROCESS_NAME, "debit-adjustment-note");
			temp.put(FLProcessNameConst.STOP_FULFILMENT_REQUEST_PROCESS_NAME, "stop-delivery");
			temp.put(FLProcessNameConst.STOP_FULFILLMENT_INIT_PROCESS_NAME, "stop-delivery");
			temp.put(FLProcessNameConst.PURCHASE_REQ_INITIATION_PROCESS_NAME + FLProcessNameConst.SUBMIT_PR_HT, purchaseRequestConstStr);
			temp.put(FLProcessNameConst.PURCHASE_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.CREATE_PR_HT, purchaseRequestConstStr);
			temp.put(FLProcessNameConst.PURCHASE_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.REVIEW_PR_HT, purchaseRequestConstStr);
			temp.put(FLProcessNameConst.PURCHASE_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.REVISE_PR_HT, purchaseRequestConstStr);
			temp.put(FLProcessNameConst.PURCHASE_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.PR_ACK_HT, purchaseOrderConstStr);
			temp.put(FLProcessNameConst.PURCHASE_REQ_CREATION_PROCESS_NAME + FLProcessNameConst.REVIEW_REJECTED_PO_HT, purchaseOrderConstStr);
			temp.put(FLProcessNameConst.DO_MODIFICATION_PROCESS_NAME + FLProcessNameConst.ACK_MODIFIED_DO_HT, deliveryOrderConstStr);
			temp.put(FLProcessNameConst.INVOICE_AND_PAYMENT_CREATION_PROCESS_NAME + FLProcessNameConst.ACK_MODIFIED_INVOICE_HT, invoiceConstStr);
			temp.put(FLProcessNameConst.DO_CANCELLATION_PROCESS_NAME + FLProcessNameConst.REVIEW_CANCEL_DO_HT, deliveryOrderConstStr);
			temp.put(FLProcessNameConst.DO_CANCELLATION_PROCESS_NAME + FLProcessNameConst.REVIEW_FAILED_CANCEL_DO_HT, deliveryOrderConstStr);			
			temp.put(FLProcessNameConst.INVOICE_AND_PAYMENT_CREATION_PROCESS_NAME + FLProcessNameConst.REVIEW_CANCEL_INVOICE_HT, invoiceConstStr);
			temp.put(FLProcessNameConst.FC_REMOVAL_REQUEST_PROCESS_NAME, "remove-factoring-company");
			temp.put(FLProcessNameConst.RESUBMIT_ORDER_CANCELLATION + FLProcessNameConst.RESUBMIT_AP59_ORDER, "resubmit-ap59-order");
			/*#28375 - Logan - YEP Process Beginning*/
			//temp.put(FLProcessNameConst.PROCEED_TO_BPM_CARRY_FORWARD_PO, "po-carryForward");
			//temp.put(FLProcessNameConst.PROCEED_TO_BPM_CARRY_FORWARD_CO, "co-carryForward");
			/*#28375 - End*/
			//21229
			temp.put(FLProcessNameConst.INVOICE_AND_PAYMENT_CREATION_PROCESS_NAME + FLProcessNameConst.REVISE_INVOICE, invoiceConstStr);
			// Added for qt..
			temp.put(SCProcessNameConst.SCORING_PREPARATION_PROCESS_NAME + SCProcessNameConst.SPECIFICATION_APPROVAL, SourcingConstants.NAV_PAGE_PREPARE_CHECKLIST_SCORING);
			temp.put(SCProcessNameConst.QUOTATION_TENDER_PROCESS_NAME + SCProcessNameConst.PREPARE_QT_DETAILS, SourcingConstants.NAV_PAGE_PREPARE_QT_DETAILS);
			temp.put(SCProcessNameConst.QUOTATION_TENDER_PROCESS_NAME + SCProcessNameConst.REVISE_QT_SCHEDULE, SourcingConstants.NAV_PAGE_PREPARE_QT_DETAILS);
			temp.put(SCProcessNameConst.QUOTATION_TENDER_PROCESS_NAME + SCProcessNameConst.REVISE_QT_DOCUMENT, SourcingConstants.NAV_PAGE_PREPARE_QT_DETAILS);
			
			temp.put(SCProcessNameConst.BUDGET_APPROVAL_PROCESS_NAME + SCProcessNameConst.APPROVE_BUDGET, SourcingConstants.NAV_PAGE_QT_APPROVAL);
			temp.put(SCProcessNameConst.BPK_APPROVAL_PROCESS_NAME + SCProcessNameConst.APPROVE_BPK, SourcingConstants.NAV_PAGE_QT_APPROVAL);
			temp.put(SCProcessNameConst.SCORING_PREPARATION_PROCESS_NAME + SCProcessNameConst.UPDATE_SCORING, SourcingConstants.NAV_PAGE_PREPARE_CHECKLIST_SCORING);
			temp.put(SCProcessNameConst.SCORING_PREPARATION_PROCESS_NAME + SCProcessNameConst.FINALISE_SCORING, SourcingConstants.NAV_PAGE_PREPARE_CHECKLIST_SCORING);
			temp.put(SCProcessNameConst.SCORING_PREPARATION_PROCESS_NAME + SCProcessNameConst.ENDORSE_SCORING, SourcingConstants.NAV_PAGE_PREPARE_CHECKLIST_SCORING);
			temp.put(SCProcessNameConst.SCORING_PREPARATION_PROCESS_NAME + SCProcessNameConst.SIGNOFF_SCORING, SourcingConstants.NAV_PAGE_PREPARE_CHECKLIST_SCORING);
			temp.put(SCProcessNameConst.SCORING_PREPARATION_PROCESS_NAME + SCProcessNameConst.VIEW_SCORING, SourcingConstants.NAV_PAGE_PREPARE_CHECKLIST_SCORING);
			temp.put(SCProcessNameConst.QUOTATION_TENDER_PROCESS_NAME + SCProcessNameConst.SIGNOFF_SCORING, SourcingConstants.NAV_PAGE_PREPARE_CHECKLIST_SCORING);
			

			temp.put(SCProcessNameConst.PCB_APPROVAL_PROCESS_NAME + SCProcessNameConst.APPROVE_PCB, SourcingConstants.NAV_PAGE_QT_APPROVAL);
			temp.put(SCProcessNameConst.PCB_APPROVAL_PROCESS_NAME + SCProcessNameConst.REVISE_PCB, SourcingConstants.NAV_PAGE_PREPARE_QT_DETAILS);

			temp.put(SCProcessNameConst.BPK_APPROVAL_PROCESS_NAME + SCProcessNameConst.REVISE_BPK, SourcingConstants.NAV_PAGE_PREPARE_QT_DETAILS);
			temp.put(SCProcessNameConst.QUOTATION_TENDER_PROCESS_NAME + SCProcessNameConst.PREPARE_QT_SCHEDULE, SourcingConstants.NAV_PAGE_PREPARE_QT_DETAILS);
			temp.put(SCProcessNameConst.COMMITTEE_APPROVAL_PROCESS_NAME + SCProcessNameConst.APPROVE_COMMITTEE, SourcingConstants.NAV_PAGE_QT_APPROVAL);
			temp.put(SCProcessNameConst.COMMITTEE_APPROVAL_PROCESS_NAME + SCProcessNameConst.REVISE_COMMITTEE, SourcingConstants.NAV_PAGE_PREPARE_QT_DETAILS);
			temp.put(SCProcessNameConst.QUOTATION_TENDER_PROCESS_NAME + SCProcessNameConst.ASSIGN_SCORING_COMMITTEE, SourcingConstants.NAV_PAGE_PREPARE_QT_DETAILS);

			temp.put(SCProcessNameConst.SITE_VISIT_PROPOSAL_PROCESS_NAME + SCProcessNameConst.FINALIZE_ATTANDANCE, SourcingConstants.NAV_PAGE_BSV_ATTENDANCE);
			temp.put(SCProcessNameConst.SITE_VISIT_PROPOSAL_PROCESS_NAME + SCProcessNameConst.UPDATE_ATTANDANCE, SourcingConstants.NAV_PAGE_BSV_ATTENDANCE);
			temp.put(SCProcessNameConst.SUPPLIER_DP_AWARD_PROCESS_NAME + SCProcessNameConst.PREPARE_LOA, SourcingConstants.NAV_PAGE_DIRECT_PURCHASE_LOA_LIST);
			temp.put(SCProcessNameConst.SUPPLIER_DP_ACCEPTANCE_PROCESS_NAME + SCProcessNameConst.APPROVE_LOA, SourcingConstants.NAV_PAGE_REVIEW_LOA);
			temp.put(SCProcessNameConst.SUPPLIER_DP_ACCEPTANCE_PROCESS_NAME + SCProcessNameConst.REVISE_LOA, SourcingConstants.NAV_PAGE_REVIEW_LOA);
			temp.put(SCProcessNameConst.SUPPLIER_DP_ACCEPTANCE_PROCESS_NAME + SCProcessNameConst.ACCEPT_LOA, SourcingConstants.NAV_PAGE_SUPPLIER_RESPONSE_LOA);
			temp.put(SCProcessNameConst.SUPPLIER_DP_ACCEPTANCE_PROCESS_NAME + SCProcessNameConst.VERIFY_LOA, SourcingConstants.NAV_PAGE_REVIEW_LOA);
			temp.put(SCProcessNameConst.QUOTATION_TENDER_PROCESS_NAME + SCProcessNameConst.APPROVE_PUBLICATION, SourcingConstants.NAV_PAGE_QT_APPROVAL);

			temp.put(SCProcessNameConst.EVALUATION_PROCESS_NAME + SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE, SourcingConstants.NAV_PAGE_PREPARE_QT_DETAILS);
			temp.put(SCProcessNameConst.EVALUATION_PROCESS_NAME + SCProcessNameConst.ASSIGN_TEC_COMMITTEE, SourcingConstants.NAV_PAGE_PREPARE_QT_DETAILS);
			temp.put(SCProcessNameConst.EVALUATION_PROCESS_NAME + SCProcessNameConst.ASSIGN_FEC_COMMITTEE, SourcingConstants.NAV_PAGE_PREPARE_QT_DETAILS);
			temp.put(SCProcessNameConst.EC_EVALUATION_PROCESS_NAME + SCProcessNameConst.COORDINATE_EC_MEETING, SourcingConstants.NAV_PAGE_EVALUATION_COORDINATE_MEETING);
			temp.put(SCProcessNameConst.OC_EVALUATION_PROCESS_NAME + SCProcessNameConst.COORDINATE_OC_MEETING, SourcingConstants.NAV_PAGE_EVALUATION_COORDINATE_MEETING);
			temp.put(SCProcessNameConst.TEC_EVALUATION_PROCESS_NAME + SCProcessNameConst.COORDINATE_TEC_MEETING, SourcingConstants.NAV_PAGE_EVALUATION_COORDINATE_MEETING);
			temp.put(SCProcessNameConst.FEC_EVALUATION_PROCESS_NAME + SCProcessNameConst.COORDINATE_FEC_MEETING, SourcingConstants.NAV_PAGE_EVALUATION_COORDINATE_MEETING);

			temp.put(SCProcessNameConst.EC_EVALUATION_PROCESS_NAME + SCProcessNameConst.FINALISE_EC_EVALUATION, SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.EC_EVALUATION_PROCESS_NAME + SCProcessNameConst.UPDATE_EC_EVALUATION, SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.EC_EVALUATION_PROCESS_NAME + SCProcessNameConst.VIEW_EC_EVALUATION, SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.EC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.ENDORSE_EC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.EC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.SIGNOFF_EC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.OC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.FINALISE_OC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.OC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.UPDATE_OC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.OC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.VIEW_OC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.OC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.ENDORSE_OC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.OC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.SIGNOFF_OC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);

			temp.put(SCProcessNameConst.TEC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.FINALISE_TEC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.TEC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.UPDATE_TEC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.TEC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.ENDORSE_TEC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.TEC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.SIGNOFF_TEC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.TEC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.VIEW_TEC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);

			temp.put(SCProcessNameConst.FEC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.FINALISE_FEC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.FEC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.UPDATE_FEC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.FEC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.ENDORSE_FEC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.FEC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.SIGNOFF_FEC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);
			temp.put(SCProcessNameConst.FEC_EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.VIEW_FEC_EVALUATION,
					SourcingConstants.NAV_PAGE_EVALUATION_PROPOSAL);

			temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
					+ SCProcessNameConst.CONSOLIDATION_REPORT_PROCESS_NAME,
					SourcingConstants.NAV_PAGE_CONSOLIDATION_REPORT);
			temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
					+ SCProcessNameConst.COORDINATE_FC_MEETING,
					SourcingConstants.NAV_PAGE_FC_COORDINATE_MEETING);
			temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
					+ SCProcessNameConst.UPDATE_FC_SELECTION,
					SourcingConstants.NAV_PAGE_FC_SUPPLIER_FINALIZATION);
			temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
					+ SCProcessNameConst.FINALISE_FC_SELECTION,
					SourcingConstants.NAV_PAGE_FC_SUPPLIER_FINALIZATION);
			
			// #27502 - Start 
			temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
					+ SCProcessNameConst.MOF_FINALIZATION,
					SourcingConstants.NAV_PAGE_FC_SUPPLIER_FINALIZATION);
			// #27502 - End 
			
			temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
					+ SCProcessNameConst.FINALISE_BPK,
					SourcingConstants.NAV_PAGE_FC_SUPPLIER_FINALIZATION);
			temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
					+ SCProcessNameConst.ENDORSE_FC_SELECTION,
					SourcingConstants.NAV_PAGE_FC_SUPPLIER_FINALIZATION);
			temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
					+ SCProcessNameConst.SIGNOFF_FC_SELECTION,
					SourcingConstants.NAV_PAGE_FC_SUPPLIER_FINALIZATION);
			temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
					+ SCProcessNameConst.VIEW_FC_SELECTION,
					SourcingConstants.NAV_PAGE_FC_SUPPLIER_FINALIZATION);
			temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
					+ SCProcessNameConst.SIGNOFF_SCORING,
					SourcingConstants.NAV_PAGE_PREPARE_CHECKLIST_SCORING);
			temp.put(SCProcessNameConst.DIRECT_AWARD_PROCESS_NAME
					+ SCProcessNameConst.ENDORSE_FC_SELECTION,
					SourcingConstants.NAV_PAGE_FC_SUPPLIER_FINALIZATION);
			temp.put(SCProcessNameConst.DIRECT_AWARD_PROCESS_NAME
					+ SCProcessNameConst.SIGNOFF_FC_SELECTION,
					SourcingConstants.NAV_PAGE_FC_SUPPLIER_FINALIZATION);

			temp.put(SCProcessNameConst.BIDDING_AWARD_PROCESS_NAME
					+ SCProcessNameConst.CREATE_BIDDING_DETAILS,
					SourcingConstants.NAV_PAGE_SCHEDULE_BIDDING);
			temp.put(SCProcessNameConst.MULTI_AWARD_PROCESS_NAME
					+ SCProcessNameConst.PREPARE_LOI,
					SourcingConstants.NAV_PAGE_LOI_LIST);
			/*
			 * temp.put(SCProcessNameConst.MULTI_AWARD_PROCESS_NAME +
			 * SCProcessNameConst.NEGOTIATE_MA,
			 * SourcingConstants.MULTI_LOI_NEGOTIATION_PAGE);
			 */
			temp.put(SCProcessNameConst.MULTI_AWARD_PROCESS_NAME
					+ SCProcessNameConst.RECOMMEND_MA_SUPPLIER,
					SourcingConstants.NAV_PAGE_MULTIPLE_LOI_RECOMMENDATION);
			temp.put(SCProcessNameConst.MULTI_AWARD_PROCESS_NAME
					+ SCProcessNameConst.ENDORSE_FC_SELECTION,
					SourcingConstants.NAV_PAGE_MULTIPLE_LOI_RECOMMENDATION);
			temp.put(SCProcessNameConst.MULTI_AWARD_PROCESS_NAME
					+ SCProcessNameConst.SIGNOFF_FC_SELECTION,
					SourcingConstants.NAV_PAGE_MULTIPLE_LOI_RECOMMENDATION);
			temp.put(SCProcessNameConst.DIRECT_AWARD_PROCESS_NAME
					+ SCProcessNameConst.PREPARE_LOI,
					SourcingConstants.NAV_PAGE_LOI_LIST);
			temp.put(SCProcessNameConst.BIDDING_AWARD_PROCESS_NAME
					+ SCProcessNameConst.PREPARE_LOI,
					SourcingConstants.NAV_PAGE_LOI_LIST);
			temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
					+ SCProcessNameConst.PREPARE_LOA,
					SourcingConstants.NAV_PAGE_QUOTATION_TENDER_LOA_LIST);
			temp.put(SCProcessNameConst.LOA_ACCEPTANCE_PROCESS_NAME
					+ SCProcessNameConst.APPROVE_LOA,
					SourcingConstants.NAV_PAGE_REVIEW_LOA);
			temp.put(SCProcessNameConst.LOA_ACCEPTANCE_PROCESS_NAME
					+ SCProcessNameConst.REVISE_LOA, SourcingConstants.NAV_PAGE_REVIEW_LOA);
			temp.put(SCProcessNameConst.LOA_ACCEPTANCE_PROCESS_NAME
					+ SCProcessNameConst.ACCEPT_LOA,
					SourcingConstants.NAV_PAGE_SUPPLIER_RESPONSE_LOA);
			temp.put(SCProcessNameConst.LOA_ACCEPTANCE_PROCESS_NAME
					+ SCProcessNameConst.VERIFY_LOA, SourcingConstants.NAV_PAGE_REVIEW_LOA);

			temp.put(SCProcessNameConst.BIDDING_AWARD_PROCESS_NAME
					+ SCProcessNameConst.UPDATE_FC_SELECTION,
					SourcingConstants.NAV_PAGE_ONLINE_BIDDING_ENDORSEMENT);
			temp.put(SCProcessNameConst.BIDDING_AWARD_PROCESS_NAME
					+ SCProcessNameConst.FINALISE_FC_SELECTION,
					SourcingConstants.NAV_PAGE_ONLINE_BIDDING_ENDORSEMENT);
			temp.put(SCProcessNameConst.BIDDING_AWARD_PROCESS_NAME
					+ SCProcessNameConst.VIEW_FC_SELECTION,
					SourcingConstants.NAV_PAGE_ONLINE_BIDDING_ENDORSEMENT);
			temp.put(SCProcessNameConst.BIDDING_AWARD_PROCESS_NAME
					+ SCProcessNameConst.ENDORSE_FC_SELECTION,
					SourcingConstants.NAV_PAGE_ONLINE_BIDDING_ENDORSEMENT);			
			temp.put(SCProcessNameConst.LOI_ACCEPTANCE_PROCESS_NAME
					+ SCProcessNameConst.APPROVE_LOI,
					SourcingConstants.NAV_PAGE_REVIEW_LOI);
			temp.put(SCProcessNameConst.LOI_ACCEPTANCE_PROCESS_NAME
					+ SCProcessNameConst.NEGOTIATE_LOI,
					SourcingConstants.NAV_PAGE_REVIEW_LOI);
			temp.put(SCProcessNameConst.LOI_ACCEPTANCE_PROCESS_NAME
					+ SCProcessNameConst.REVISE_LOI, SourcingConstants.NAV_PAGE_REVIEW_LOI);
			temp.put(SCProcessNameConst.LOI_ACCEPTANCE_PROCESS_NAME
					+ SCProcessNameConst.ACCEPT_LOI,
					SourcingConstants.NAV_PAGE_SUPPLIER_RESPONSE_LOI);
			temp.put(SCProcessNameConst.BIDDING_AWARD_PROCESS_NAME
					+ SCProcessNameConst.ESCALATE_BIDDING,
					SourcingConstants.NAV_PAGE_ONLINE_BIDDING_ENDORSEMENT);
			temp.put(SCProcessNameConst.BIDDING_AWARD_PROCESS_NAME
					+ SCProcessNameConst.SIGNOFF_FC_SELECTION,
					SourcingConstants.NAV_PAGE_ONLINE_BIDDING_ENDORSEMENT);
			temp.put(SCProcessNameConst.BIDDING_AWARD_PROCESS_NAME
					+ SCProcessNameConst.COORDINATE_FC_MEETING,
					SourcingConstants.NAV_PAGE_FC_COORDINATE_MEETING);
			
			temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
					+ SCProcessNameConst.ASSIGN_FC_SECRETARY, 
					SourcingConstants.NAV_PAGE_PREPARE_QT_DETAILS);

			temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
					+ SCProcessNameConst.ASSIGN_FC_MEMBER, 
					SourcingConstants.NAV_PAGE_FC_APPOINT_MEMBERS);

			temp.put(SCProcessNameConst.EVALUATION_PROCESS_NAME
					+ SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE,
					SourcingConstants.NAV_PAGE_PREPARE_QT_DETAILS);

			temp.put(SCProcessNameConst.QT_MAINTENANCE_PROCESS_NAME
					+ SCProcessNameConst.APPROVE_CANCELATION,
					SourcingConstants.NAV_PAGE_QT_APPROVAL);

			temp.put(SCProcessNameConst.QT_MAINTENANCE_PROCESS_NAME
					+ SCProcessNameConst.APPROVE_SUPPLIER_ADD,
					SourcingConstants.NAV_PAGE_ADDITIONAL_BSV_ATTANDANCE);

			temp.put(SCProcessNameConst.QT_MAINTENANCE_PROCESS_NAME
					+ SCProcessNameConst.BPK_APPROVE_QT_CHANGE,
					SourcingConstants.NAV_PAGE_QT_APPROVAL);
			temp.put(SCProcessNameConst.QT_MAINTENANCE_PROCESS_NAME
					+ SCProcessNameConst.FC_APPROVE_QT_CHANGE,
					SourcingConstants.NAV_PAGE_APPL_PROPOSAL_VALID_PERIOD_EXTENSION);
			temp.put(SCProcessNameConst.QT_MAINTENANCE_PROCESS_NAME
					+ SCProcessNameConst.PUBLICATION_APPROVE_QT_CHANGE,
					SourcingConstants.NAV_PAGE_QT_APPROVAL);

			temp.put(SCProcessNameConst.QT_MAINTENANCE_PROCESS_NAME
					+ SCProcessNameConst.SUPPLIER_ACCEPT_QT_CHANGE,
					SourcingConstants.NAV_PAGE_QT_SUPPLIER_ACCEPTANCE);

			temp.put(SCProcessNameConst.START_MANUAL_QT_CREATION
					+ SCProcessNameConst.SUBMIT_MANUAL_QUOTATION_TENDER,
					SourcingConstants.NAV_PAGE_REVIEW_DIRECT_LOA);
			temp.put(SCProcessNameConst.LOA_MANUAL_PROCESS_NAME
					+ SCProcessNameConst.APPROVE_MANUAL_LOA,
					SourcingConstants.NAV_PAGE_REVIEW_DIRECT_LOA);

			temp.put(SCProcessNameConst.LOA_MANUAL_PROCESS_NAME
					+ SCProcessNameConst.REVISE_MANUAL_LOA,
					SourcingConstants.NAV_PAGE_REVIEW_DIRECT_LOA);

			temp.put(SCProcessNameConst.LOA_MANUAL_PROCESS_NAME
					+ SCProcessNameConst.ACCEPT_MANUAL_LOA,
					SourcingConstants.NAV_PAGE_SUPPLIER_RESPONSE_DIRECT_LOA);



			temp.put(SCProcessNameConst.LOA_MANUAL_PROCESS_NAME
					+ SCProcessNameConst.VERIFY_MANUAL_LOA,
					SourcingConstants.NAV_PAGE_REVIEW_DIRECT_LOA);
			
			temp.put(SCProcessNameConst.SPEC_MAINTENANCE_PROCESS_NAME
					+ SCProcessNameConst.SPEC_MAINTENANCE_ENDORSE_SCORING,
					SourcingConstants.NAV_PAGE_SPECIFICATION_ENDORSMENT);
			
			temp.put(SCProcessNameConst.SPEC_MAINTENANCE_PROCESS_NAME
					+ SCProcessNameConst.SPEC_MAINTENANCE_SIGNOFF_SCORING,
					SourcingConstants.NAV_PAGE_SPECIFICATION_ENDORSMENT);

			temp.put(SCProcessNameConst.SPEC_MAINTENANCE_PROCESS_NAME
					+ SCProcessNameConst.SPEC_MAINTENANCE_REVISE_SCORING,
					SourcingConstants.NAV_PAGE_SPECIFICATION_ENDORSMENT);
			// 25627
			temp.put(SCProcessNameConst.QT_MAINTENANCE_PROCESS_NAME
					+ SCProcessNameConst.APPROVE_DO_REASSIGNMENT,
					SourcingConstants.NAV_PAGE_DO_REASSIGNMENT_APPROVAL);
			// 25627
			//START - 22211
			temp.put(SCProcessNameConst.DIRECT_PURCHASE_MAINTENANCE
					+ ParameterConst.DOCTYPE_LETTER_OF_AWARD,
					SourcingConstants.NAV_PAGE_CANCELLATION_LOA_APPROVAL);
			//END - 22211
			//#25037-START
			temp.put(SCProcessNameConst.COMMITTEE_APPROVAL_PROCESS_NAME
					+ SCProcessNameConst.REVISE_EVALUATION_MEETING,
					SourcingConstants.NAV_PAGE_REVISE_EVAL_MEETING);
			temp.put(SCProcessNameConst.QT_MAINTENANCE_PROCESS_NAME
					+ SCProcessNameConst.GENERIC_APPROVAL_APPROVE,
					SourcingConstants.NAV_PAGE_GENERIC_APPROVAL_APPROVE);
			temp.put(SCProcessNameConst.QT_MAINTENANCE_PROCESS_NAME
					+ SCProcessNameConst.GENERIC_APPROVAL_REVISE,
					SourcingConstants.NAV_PAGE_GENERIC_APPROVAL_REVISE);
			//#25037-END
			this.getManagedBean().setBpmMap(temp);
		}

		log.info("testing BPM******");

	}

	public void navigateToDeliveryOrderURL() throws IOException, PortalException, SystemException {
		String orderId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("orderId");
		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay) facesScope.getRequestScope(WebKeys.THEME_DISPLAY);

		log.info("getURLCurrent" + themeDisplay.getURLCurrent());
		String commonUrl = themeDisplay.getURLCurrent().substring(0, themeDisplay.getURLCurrent().indexOf( themeDisplay.getLayout().getGroup().getFriendlyURL()) + themeDisplay.getLayout().getGroup().getFriendlyURL() .length());
		log.info("commonUrl" + commonUrl);

		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		externalContext.redirect(commonUrl + "/delivery-order?orderId="+ orderId);
	}

	public void navigateToURL() throws PortalException, SystemException {
		log.info("......................navigateToURL");
		//IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		String taskId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("taskId");
		String process = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("process");
		String taskName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("taskName");
		String docType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docType");
		//26238 -  Disciplinary Action - 28111
		String docNo = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docNo");

		String claim = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("isClaim");

		log.info("****claim****"+claim);

		//FacesScopes face = new FacesScopes();
		//Object token = face.getBPMAuthenticationCtxToken();
		//BpmRequestDTO reqDTO = new BpmRequestDTO();

		try {
			log.info("taskId:" + taskId);

			log.info("********getProcessName********" + process);
			log.info("********getTaskName********" + taskName);
			// this.getManagedBean().setProcessName(res.getProcessName());
			// this.getManagedBean().setAction(res.getAction());
			// Object obj = res.getPojo();

			String url = "";

			if (!this.getManagedBean().getBpmMap().isEmpty() && this.getManagedBean().getBpmMap().size() > 0) {
				log.info("********do Bpm Map********");
				url = (String) this.getManagedBean().getBpmMap().get(process);
				if (url == null || url.isEmpty()) {
					url = (String) this.getManagedBean().getBpmMap().get(process + taskName);
				}
				if (url == null || url.isEmpty()) {
					url = (String) this.getManagedBean().getBpmMap().get(process + docType);
				}
			}
			log.info("url:" + url);

			FacesScopes facesScope = new FacesScopes();
			ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
					.getRequestScope(WebKeys.THEME_DISPLAY);

			log.info("getURLCurrent" + themeDisplay.getURLCurrent());

			String commonUrl = themeDisplay.getURLCurrent().substring(
					0,
					themeDisplay.getURLCurrent().indexOf(
							themeDisplay.getLayout().getGroup()
							.getFriendlyURL())
							+ themeDisplay.getLayout().getGroup()
							.getFriendlyURL().length());
			log.info("commonUrl" + commonUrl);
			
			/*#31422-basri 190821*/			
			StringBuilder strbuildtask = new StringBuilder();
			if(docType.equalsIgnoreCase(ContractConst.DOC_TYPE_AGREEMENT) || docType.equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_CREATION)){
				List<WorklistItemVO>  list = this.getManagedBean().getTaskListGroupDataModel().getList();
			    
				    for(WorklistItemVO a : list){
				    	String var = a.getDocNumber();
				    	String taski = a.getTaskId();
				    	if(var.equalsIgnoreCase(docNo) && !taski.equalsIgnoreCase(taskId)){
				    		strbuildtask.append(taski);
				    	}
				    }
				}else{
					strbuildtask = null; //#31681 - basri 22/10/21
				}
			/*#31422-basri 190821*/
			
			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();
			//26238 -  Disciplinary Action - 28111
			// externalContext.redirect("/web/guest/" +url +"?taskId="+taskId);
			externalContext.redirect(commonUrl + "/" + url + "?taskId="
					+ taskId + "&taskIdPair="+strbuildtask+"&claim=" + claim+"&docNo="+docNo);/*#31422-basri 190821*/
			// externalContext.redirect("http://localhost:8080/web/guest/" +url
			// +"?taskId="+taskId);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void filterViewbyNAssignment() {
		log.info(">>>>>>>>>>>>>>>filterViewbyNAssignment<<<<<<<<<<<<<<<<<");
		log.info("this.getManagedBean().getViewBy()"
				+ this.getManagedBean().getViewBy());
		log.info("this.getManagedBean().getAssignment()"
				+ this.getManagedBean().getAssignment());
		log.info("this.getManagedBean().getDocType()="
				+ this.getManagedBean().getDocType());
		// retrieveWorklistItems("ASSIGNED",this.getManagedBean().getViewBy(),
		// this.getManagedBean().getAssignment());
		// this.getManagedBean().getTaskListDataModel().setPageNo(1);

		System.out
		.println(">>>>>>>>this.getManagedBean().getTaskListDataModel().pageNo()="
				+ this.getManagedBean().getTaskListDataModel()
				.getPageNo());
		this.getManagedBean().getTaskListDataModel().reloadData();

	}

	/*
	 * public void filterStatus() {
	 * 
	 * BpmResponseDTO responseDTO = retrieveWorklistItems("ASSIGNED" ,this
	 * .getManagedBean().getStatus()); }
	 */

	/*
	 * public void tempFilterLogin() {
	 * 
	 * BpmResponseDTO responseDTO = retrieveWorklistItems("ASSIGNED" ,this
	 * .getManagedBean().getStatus()); }
	 */
	public String backToTaskList() {

		log.info("*******backToTaskList*******");

		return "/xhtml/bpmtasklist/taskListView";
	}

	public String applicationStatus(Integer docInt) {
		PmStatusDesc pmStatusDesc = StatusHelper.getInstance().getStatusDescById(docInt.longValue());

		if (log.isDebugEnabled()) {
			log.debug("docInt:" + docInt);	
		}

		return pmStatusDesc != null ? pmStatusDesc.getStatusName() : "Review";
	}

	public void refreshTaskList() {
		this.getManagedBean().getTaskListDataModel().reloadData();
		// retrieveWorklistItems("ASSIGNED",this.getManagedBean().getViewBy(),
		// this.getManagedBean().getAssignment());
	}

	public void refreshAdvanceTaskList() {

		this.getManagedBean().getTaskListGroupDataModel().reloadData();
	}


	public void refreshNormalTaskList(){

		this.getManagedBean().getTaskListMyTaskDataModel().reloadData();
	}
	
	//Enhancement #24714
	public void refreshBothTaskList(){

		this.getManagedBean().getTaskListBothDataModel().reloadData();
	}

	public String goToMyTasks() {
		log.info(">>>>>>>>>>goToMyTasks<<<<<<<<<<");
		// System.out.println(">>>>>showIndex = "+showIndex);
		// this.getManagedBean().setShowIndex(showIndex);

		// if(showIndex == 0){
		// set filters to default for MyTasks
		this.getManagedBean().setViewBy("ALL");
		//this.getManagedBean().setAssignment("ALL");
		// }
		// if(showIndex == 2){
		//
		// this.getManagedBean().setFlFulfilmentOrderList(retrievePendingDeliverylist());
		// }

		return "taskListview";
	}

	public ISearchBean<PmParameterDesc> getDocTypeSearchBean() {
		if (this.docTypeSearchBean == null) {
			this.docTypeSearchBean = super.createSearchBean(PmParameterDesc.class, "docTypeId");
		}

		return this.docTypeSearchBean;
	}

	public void preRenderTaskListView() {
		log.info("......................preRenderTaskListView");
	}

	public void preRenderTaskView() {
		log.info("......................preRenderTaskView");
		log.info("THE SERVER INFO : " + new FacesScopes().getRequest().getServerName() +" " + new FacesScopes().getRequest().getServerPort());
		this.getManagedBean().setViewBy("");
		this.getManagedBean().setViewByGroup("");
		this.getManagedBean().setDocNo("");
		this.getManagedBean().setDocNoGroup("");
		this.getManagedBean().setTaskDesc("");
		this.getManagedBean().setTaskDescGroup("");
		this.getManagedBean().setSelectedStatusDesc(new PmStatusDesc());
		this.getManagedBean().setSelectedStatusDescAdvance(new PmStatusDesc());
		this.getManagedBean().setDocStatusGroup(0);
		this.getManagedBean().setDocTypeGroup("");		
		//this.getManagedBean().setAssignment("ALL");

		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String docTypeId = originalServletRequest.getParameter("docTypeId");

		if (docTypeId != null && !docTypeId.isEmpty()) {
			this.getManagedBean().setDocType(docTypeId);
			originalServletRequest.getParameterMap().remove("docTypeId");
		} else {
			this.getManagedBean().setDocType("");
		}
		//this.getManagedBean().getTaskListDataModel().reloadData();
		this.getManagedBean().getTaskListGroupDataModel().reloadData();
		this.getManagedBean().getTaskListMyTaskDataModel().reloadData();
		
		getManagedBean().setSdoWithMultipleRoles(sdoWithMultipleRole());
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "taskListView");
	
	}
	
	// #24714
	public void preRenderTaskViewCombined() {
		log.info("......................preRenderTaskView (Combined)");

		this.getManagedBean().setViewBy("");
		this.getManagedBean().setViewByGroup("");
		this.getManagedBean().setDocNo("");
		this.getManagedBean().setDocNoGroup("");
		this.getManagedBean().setTaskDesc("");
		this.getManagedBean().setTaskDescGroup("");
		this.getManagedBean().setSelectedStatusDesc(new PmStatusDesc());
		this.getManagedBean().setSelectedStatusDescAdvance(new PmStatusDesc());
		this.getManagedBean().setDocStatusGroup(0);
		this.getManagedBean().setDocTypeGroup("");

		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String docTypeId = originalServletRequest.getParameter("docTypeId");

		if (docTypeId != null && !docTypeId.isEmpty()) {
			this.getManagedBean().setDocType(docTypeId);
			originalServletRequest.getParameterMap().remove("docTypeId");
		} else {
			this.getManagedBean().setDocType("");
		}
		
		this.getManagedBean().getTaskListBothDataModel().reloadData();
		
		getManagedBean().setSdoWithMultipleRoles(sdoWithMultipleRole());
		
		FacesScopes sc = new FacesScopes();
		getManagedBean().setLoginIc(sc.getUserSessionBean().getLoginId());

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "taskListCombined");

	}


	public String claimTask()  {

		log.info("**********claimTask");
		
		/*21387 - basri*/
		//String taskId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("taskId");
		
		String docNumber = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docNumber");
		
		String docType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docType");
	
		
		if(docType.equalsIgnoreCase(ContractConst.DOC_TYPE_AGREEMENT) || docType.equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_CREATION)){
		    List<WorklistItemVO>  list = this.getManagedBean().getTaskListGroupDataModel().getList();
		    
		    for(WorklistItemVO a : list){
		    	String var = a.getDocNumber();
		    	String taski = a.getTaskId();
		    	if(var.equalsIgnoreCase(docNumber)){
		    		claimBpmTaskGroup(taski);
		    	}
		    }
		}else{
			claimBpmTask();
		}
		    /*21387 - basri*/
		    
			
		this.getManagedBean().getTaskListGroupDataModel().reloadData();
		this.getManagedBean().getTaskListMyTaskDataModel().reloadData();

		return "taskListView?faces-redirect=true";

	}


	public String releaseTask()  {
		log.info("**********releaseTask");
		
		releaseBpmTask();
		
		this.getManagedBean().getTaskListGroupDataModel().reloadData();
		this.getManagedBean().getTaskListMyTaskDataModel().reloadData();

		return "taskListView?faces-redirect=true";
	}
	
	
	public String claimTaskCombined()  {

		log.info("**********claimTaskCombined");
		
		claimBpmTask();
		
		this.getManagedBean().getTaskListBothDataModel().reloadData();
		//this.getManagedBean().getTaskListGroupDataModel().reloadData();
		//this.getManagedBean().getTaskListMyTaskDataModel().reloadData();
		
		return "taskListCombined?faces-redirect=true";

	}
	
	public String releaseTaskCombined()  {
		log.info("**********releaseTaskCombined");
		
		releaseBpmTask();
		
		this.getManagedBean().getTaskListBothDataModel().reloadData();

		return "taskListCombined?faces-redirect=true";

	}


	public void claimTaskInDetailPage()  {
		log.info("**********claimTask");

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);


		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		log.info("taskId:"+this.getManagedBean().getTaskId());
		log.info("token:"+token);
		
		//#31681 - basri 22/10/21
		if(null == this.getManagedBean().getTaskIdPair()){
			this.getManagedBean().setTaskIdPair("null");
		}
		
		/*#31422-basri 190821*///(null == this.getManagedBean().getTaskIdPair())
		if(!(this.getManagedBean().getTaskIdPair().equals("null"))){//#31681 - basri 22/10/21
			reqDTO.setTaskId(this.getManagedBean().getTaskIdPair());
	    	reqDTO.setCtxToken(token);
	    	bpmSO.claimTask(reqDTO);
		}
		
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
    	reqDTO.setCtxToken(token);
    	bpmSO.claimTask(reqDTO);
		
		/*#31422-basri 190821*/

		this.getManagedBean().getTaskListGroupDataModel().reloadData();
		this.getManagedBean().getTaskListMyTaskDataModel().reloadData();
		this.getManagedBean().setClaim(true);

		//return "/xhtml/bpmtasklist/taskListView.xhtml?faces-redirect=true";


	}



	public String constructCondition(){

		IParameterSO statusSO = (IParameterSO) BusinessDelegate
				.getInstance().getServiceObject("pm",
						IParameterSO.class);
		
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		
		ParameterResDTO resDTO = statusSO.findDOcTypeList(reqDTO);
		
		StringBuilder docType = new StringBuilder();
		docType.append("CD,");
		if(resDTO.getDocTypeLs()!=null && resDTO.getDocTypeLs().size()>0){
			for(String doc :resDTO.getDocTypeLs()){
				docType.append(doc+",");
			}
		}
		
		String outCome = docType.substring(0, docType.length()-1);
		log.info("outCome:"+outCome);
		
		StringBuilder sb = new StringBuilder();
		String languageCode = new FacesScopes().getUserLocale().getLanguage();

		sb.append("languageCode^"+languageCode);

		sb.append("|pmStatus.docType^");
		sb.append(outCome);
		sb.append("^9");

		log.info("sb.toString()"+sb.toString());
		return sb.toString();
	}
	
	public void findDocStatus() {
		if (this.getManagedBean().getDocType() != null) {
				log.info("*********findDocStatus*********");
				log.info("For Doc Type Id:" + this.getManagedBean().getDocType());

				IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject("pm", IStatusSO.class);

				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setDocType(this.getManagedBean().getDocType());
				reqDTO.setLanguageCode(language);
				// 25627 - Start.
				if (isCommitteeApproverWithQT(reqDTO)) {
					reqDTO.setRoleCode(RoleCode.COMMITTEE_APPROVER.value());
				}
				// 25627 - End.
				StatusResDTO resDTO = statusSO.retrieveStatusDescListByDocType(reqDTO);

				List<SelectItem> docStatusList = new ArrayList<SelectItem>();
				for (PmStatusDesc docStatus : resDTO.getStatusDescList()) {
					// 25627 - Start.
					if (((QTStatusConst.DESK_OFFICER_MAINTENANCE_APPROVED.longValue() == docStatus.getPmStatus().getStatusId()) || (QTStatusConst.DESK_OFFICER_MAINTENANCE_REJECTED.longValue() == docStatus.getPmStatus().getStatusId()))
							&& isCommitteeApproverWithQT(reqDTO)) {
						continue;
					}
					// 25627 - End.
					SelectItem si = new SelectItem();
					si.setLabel(docStatus.getStatusName());
					si.setValue(docStatus.getPmStatus().getStatusId());
					log.info("statusId" + si.getValue());
					log.info("statusName" + si.getLabel());
					docStatusList.add(si);
				}
				this.getManagedBean().setDocStatusList(docStatusList);
		} else {
			log.info("*********doc type empty*********");
			this.getManagedBean().setDocType("");
			this.getManagedBean().setDocStatusList(null);
		}
	}
	
	public void findDocStatusGroup() {
		if (this.getManagedBean().getDocTypeGroup() != null) {
				log.info("*********findDocStatusGroup*********");
				log.info("For Doc Type Id:" + this.getManagedBean().getDocTypeGroup());

				IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject("pm", IStatusSO.class);

				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setDocType(this.getManagedBean().getDocTypeGroup());
				reqDTO.setLanguageCode(language);
				// 25627 - Start.
				if (isCommitteeApproverWithQT(reqDTO)) {
					reqDTO.setRoleCode(RoleCode.COMMITTEE_APPROVER.value());
				}
				// 25627 - End.
				StatusResDTO resDTO = statusSO.retrieveStatusDescListByDocType(reqDTO);

				List<SelectItem> docStatusList = new ArrayList<SelectItem>();
				for (PmStatusDesc docStatus : resDTO.getStatusDescList()) {
					// 25627 - Start.
					if (((QTStatusConst.DESK_OFFICER_MAINTENANCE_APPROVED.longValue() == docStatus.getPmStatus().getStatusId()) || (QTStatusConst.DESK_OFFICER_MAINTENANCE_REJECTED.longValue() == docStatus.getPmStatus().getStatusId()))
							&& isCommitteeApproverWithQT(reqDTO)) {
						continue;
					}
					// 25627 - End.
					SelectItem si = new SelectItem();
					si.setLabel(docStatus.getStatusName());
					si.setValue(docStatus.getPmStatus().getStatusId());
					log.info("statusId" + si.getValue());
					log.info("statusName" + si.getLabel());
					docStatusList.add(si);
				}
				this.getManagedBean().setDocStatusListGroup(docStatusList);
		} else {
			log.info("*********doc type empty*********");
			this.getManagedBean().setDocTypeGroup("");
			this.getManagedBean().setDocStatusListGroup(null);
			this.getManagedBean().setDocStatusGroup(0);
		}
	}


	private boolean sdoWithMultipleRole(){
		log.info("***********hasManyRole**********");
		FacesScopes fs = new FacesScopes();
		long roleCount = PmCommonUtil.retrieveActiveOrg(fs.getUserSessionBean().getUserId()).get(0).getPmUserRoles().size();
		
		if(roleCount > 1){
			return true;
		}
		
		return false;

	}
	
	public String poKpiByCompanyState(int docId, int statusId) {

		String retVal = SmApplicationUtil.getInstance()
				.calculatePoKpiByCompanyState(docId, statusId);

		return retVal;

	}
	
	// 25627 - Start.
	public boolean isCommitteeApproverWithQT(StatusReqDTO reqDTO) {
		if (ParameterConst.DOCTYPE_QUOTATION.equals(reqDTO.getDocType())) {
			FacesScopes fs = new FacesScopes(); 
			if (fs.hasRoleUser(RoleCode.COMMITTEE_APPROVER.value())) {
				return true;
			}
		}
		return false;
	}
	// 25627 -End.
}

