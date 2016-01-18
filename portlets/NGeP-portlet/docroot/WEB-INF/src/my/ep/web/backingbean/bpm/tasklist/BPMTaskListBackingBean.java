package my.ep.web.backingbean.bpm.tasklist;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.FLProcessNameConst;
import my.ep.constants.PPProcessNameConst;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.bpm.tasklist.BPMTaskListManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.StatusHelper;

import org.apache.log4j.Logger;

import com.liferay.compat.portal.util.PortalUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

@ManagedBean(name = "bpmTasklistBackingBean")
@RequestScoped
public class BPMTaskListBackingBean extends
		BaseBackingBean<BPMTaskListManagedBean> {

	private static Logger log = Logger.getLogger(BPMTaskListBackingBean.class);

	private ISearchBean<PmParameterDesc> docTypeSearchBean;

	public BPMTaskListBackingBean() {
		this.getManagedBean().setBpmMap(null);
		Map temp = new HashMap();

		temp.put("UNSPSC Code Request", "review-unspsc-code");
		temp.put("Extension Code Request", "review-extension");
		temp.put("Extension Code Maintenance", "review-extension-maintenance");

		// Procurement Plan - Start
		temp.put(PPProcessNameConst.EXPENDITURE_PREPARATION_PROCESS_NAME,
				"plan-detail");
		temp.put(PPProcessNameConst.PROCUREMENT_PREPARATION_PROCESS_NAME,
				"plan-detail");
		temp.put("Start Expenditure Preparation", "plan-detail");
		temp.put("Start Procurement Preparation", "plan-detail");
		// Procurement Plan - End

		// Supplier Management - Start
		temp.put("Disciplinary Action", "disciplinary-action");
		temp.put("Site Visit Manage", "site-visit");
		temp.put("MOF Supplier Registration", "mof-application");
		temp.put("MOF Account Maintain", "mof-application");
		temp.put("Start MOF Supplier Registration", "mof-application");
		temp.put("Start MOF Account Maintain", "mof-application");
		temp.put("MOF Registration Cancellation", "mof-application");
		temp.put("MOF Application Cancellation", "mof-application");
		temp.put("User Add Additional", "soft-cert-payment");

		// s1 module
		temp.put(SCProcessNameConst.REVIEW_DIRECT_PURCHASE,
				"review-directpurchase");
		temp.put(SCProcessNameConst.START_DIRECT_PURCHASE_CREATION,
				"review-directpurchase");
		temp.put(SCProcessNameConst.DIRECT_PURCHASE_PROCESS_NAME,
				"review-directpurchase");
		temp.put(SCProcessNameConst.SUBMIT_DIRECT_PURCHASE,
				"review-directpurchase");

		temp.put(SCProcessNameConst.PURCHASE_INQUIRY_PROCESS_NAME,
				"review-purchase-inq");
		temp.put(SCProcessNameConst.PURCHASE_IN_DEFAULT_PROCESS_NAME,
				"review-purchase-in-default");

		temp.put(SCProcessNameConst.START_SIMPLE_QUOTE_CREATION,
				"review-simple-quote");
		temp.put(SCProcessNameConst.START_SIMPLE_QUOTE_CREATION
				+ SCProcessNameConst.SUBMIT_SIMPLE_QUOTE, "review-simple-quote");
		temp.put(SCProcessNameConst.SIMPLE_QUOTE_PROCESS_NAME,
				"review-directpurchase");
		temp.put(SCProcessNameConst.SIMPLE_QUOTE_PROCESS_NAME
				+ SCProcessNameConst.PREPARE_REQUEST_NOTE,
				"review-directpurchase");

		// Contract Management
		temp.put(
				CTProcessNameConst.CIRCULAR_MAINTENANCE_INITIATION_PROCESS_NAME
						+ ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR,
				"central-contract-circular");
		temp.put(
				CTProcessNameConst.CIRCULAR_MAINTENANCE_INITIATION_PROCESS_NAME
						+ ParameterConst.DOCTYPE_PANEL_CIRCULAR,
				"panel-circular");
		temp.put(CTProcessNameConst.CIRCULAR_MAINTENANCE_PROCESS_NAME
				+ ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR,
				"central-contract-circular");
		temp.put(CTProcessNameConst.CIRCULAR_MAINTENANCE_PROCESS_NAME
				+ ParameterConst.DOCTYPE_PANEL_CIRCULAR, "panel-circular");
		temp.put(CTProcessNameConst.CIRCULAR_CANCELLATION_PROCESS_NAME,
				"cancel-circular-approval");
		temp.put(CTProcessNameConst.FACTORING_REQUEST_PROCESS_NAME
				+ CTProcessNameConst.ACK_FACTORING_REQUEST_HT_NAME,
				"review-factoring-request");
		temp.put(CTProcessNameConst.FACTORING_REQUEST_PROCESS_NAME
				+ CTProcessNameConst.ENDORSE_FACTORING_REQUEST_HT_NAME,
				"review-factoring-request");
		temp.put(CTProcessNameConst.CONTRACT_CREATION_PROCESS_NAME,
				"fulfilment-details");
		temp.put(CTProcessNameConst.CONTRACT_INITIATION_PROCESS_NAME,
				"fulfilment-details");
		temp.put(CTProcessNameConst.CONTRACT_SUSPEND_ACTIVE_INIT_PROCESS_NAME,
				"contract-suspension");
		temp.put(CTProcessNameConst.CONTRACT_SUSPEND_ACTIVE_PROCESS_NAME,
				"contract-suspension");
		temp.put(CTProcessNameConst.CONTRACT_AMENDMENT_INITIATION_PROCESS_NAME
				+ ParameterConst.DOCTYPE_CONTRACT_AMENDMENT,
				"contract-amendment");
		temp.put(CTProcessNameConst.CONTRACT_AMENDMENT_INITIATION_PROCESS_NAME
				+ ParameterConst.DOCTYPE_CONTRACT_UPDATE, "fulfilment-details");
		temp.put(CTProcessNameConst.CONTRACT_AMENDMENT_INITIATION_PROCESS_NAME
				+ ParameterConst.DOCTYPE_AMENDMENT_WITHOUT_SUPPLEMENT, "fulfilment-details");
		temp.put(CTProcessNameConst.CONTRACT_AMENDMENT_PROCESS_NAME
				+ ParameterConst.DOCTYPE_CONTRACT_AMENDMENT,
				"contract-amendment");
		temp.put(CTProcessNameConst.CONTRACT_AMENDMENT_PROCESS_NAME
				+ ParameterConst.DOCTYPE_CONTRACT_UPDATE, "fulfilment-details");
		temp.put(CTProcessNameConst.CONTRACT_AMENDMENT_PROCESS_NAME
				+ ParameterConst.DOCTYPE_AMENDMENT_WITHOUT_SUPPLEMENT, "fulfilment-details");
		temp.put(CTProcessNameConst.CONTRACT_TERMINATION_INIT_PROCESS_NAME,
				"contract-termination");
		temp.put(CTProcessNameConst.CONTRACT_TERMINATION_PROCESS_NAME,
				"contract-termination");
		temp.put(CTProcessNameConst.AGREEMENT_CREATION_PROCESS_NAME,
				"contract-agreement");
		temp.put(CTProcessNameConst.AGREEMENT_INITIATION_PROCESS_NAME,
				"contract-agreement");

		temp.put(CTProcessNameConst.BANK_ACCOUNT_CHANGE_PROCESS_NAME
				+ CTProcessNameConst.REVIEW_CB_REQUEST_HT_NAME,
				"change-bank-account");

		// Fulfilment Management - Start
		temp.put(FLProcessNameConst.PHIS_CONTRACT_REQ_INITIATION_PROCESS_NAME
				+ FLProcessNameConst.SUBMIT_PHIS_CR, "contract-request");
		temp.put(FLProcessNameConst.CONTRACT_REQ_INITIATION_PROCESS_NAME
				+ FLProcessNameConst.SUBMIT_CR_HT, "contract-request");
		temp.put(FLProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.REVIEW_CR_HT, "contract-request");
		temp.put(FLProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.REVISE_CR_HT, "contract-request");
		temp.put(FLProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.CREATE_NORMAL_CO_HT, "contract-request");
		temp.put(FLProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.REVISE_MTO_CR_HT, "contract-request");
		temp.put(FLProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.REVIEW_MTO_CR_HT, "contract-request");
		temp.put(FLProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.CREATE_CO_HT, "contract-request");
		temp.put(FLProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.CREATE_NEW_CO_HT, "contract-request");
		temp.put(FLProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.INTIATE_MTO_CR_HT, "contract-request");
		temp.put(FLProcessNameConst.CONTRACT_REQ_INITIATION_PROCESS_NAME
				+ FLProcessNameConst.CREATE_NEW_CO_HT, "contract-request");
		temp.put(FLProcessNameConst.CONTRACT_REQ_INITIATION_PROCESS_NAME
				+ FLProcessNameConst.CREATE_NORMAL_CO_HT, "contract-request");
		temp.put(FLProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.CREATE_NEW_MTO_CO_HT, "contract-request");

		temp.put(FLProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.CREATE_SI_HT, "shipping-instruction");
		temp.put(FLProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.REVIEW_SI_HT, "shipping-instruction");
		temp.put(FLProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.REVISE_SI_HT, "shipping-instruction");

		temp.put(FLProcessNameConst.MTO_CONTRACT_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.MTO_CO_ACK_HT, "contract-order");
		temp.put(FLProcessNameConst.CONTRACT_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.CO_ACK_HT, "contract-order");
		temp.put(FLProcessNameConst.RESUBMIT_ORDER_CANCELLATION
				+ ParameterConst.DOCTYPE_CONTRACT_ORDER, "contract-order");
		temp.put(FLProcessNameConst.RESUBMIT_ORDER_CANCELLATION
				+ ParameterConst.DOCTYPE_PURCHASE_ORDER, "purchase-order");

		temp.put(FLProcessNameConst.INVOICE_AND_PAYMENT_CREATION_PROCESS_NAME
				+ FLProcessNameConst.CREATE_INVOICE, "invoice");
		temp.put(FLProcessNameConst.INVOICE_AND_PAYMENT_CREATION_PROCESS_NAME
				+ FLProcessNameConst.PERFORM_PAYMNET_MATCH_HT, "payment-match");
		temp.put(FLProcessNameConst.INVOICE_AND_PAYMENT_CREATION_PROCESS_NAME
				+ FLProcessNameConst.REVIEW_GST, "review-gst");

		temp.put(FLProcessNameConst.DELIVERY_ORDER_FULFILMENT_PROCESS_NAME,
				"fulfilment-received-note");
		temp.put(FLProcessNameConst.FRN_CANCELLATION_PROCESS_NAME,
				"fulfilment-received-note");
		temp.put(FLProcessNameConst.DELIVERY_ORDER_FL_INITIATION_PROCESS_NAME,
				"delivery-order");
		temp.put(FLProcessNameConst.DAN_GENERATION_PROCESS_NAME,
				"debit-adjustment-note");
		temp.put(FLProcessNameConst.STOP_FULFILMENT_REQUEST_PROCESS_NAME,
				"stop-delivery");

		temp.put(FLProcessNameConst.PURCHASE_REQ_INITIATION_PROCESS_NAME
				+ FLProcessNameConst.SUBMIT_PR_HT, "purchase-request");
		temp.put(FLProcessNameConst.PURCHASE_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.CREATE_PR_HT, "purchase-request");
		temp.put(FLProcessNameConst.PURCHASE_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.REVIEW_PR_HT, "purchase-request");
		temp.put(FLProcessNameConst.PURCHASE_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.REVISE_PR_HT, "purchase-request");
		temp.put(FLProcessNameConst.PURCHASE_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.PR_ACK_HT, "purchase-order");
		temp.put(FLProcessNameConst.PURCHASE_REQ_CREATION_PROCESS_NAME
				+ FLProcessNameConst.REVIEW_REJECTED_PO_HT, "purchase-order");
		temp.put(FLProcessNameConst.DO_MODIFICATION_PROCESS_NAME
				+ FLProcessNameConst.ACK_MODIFIED_DO_HT, "delivery-order");
		temp.put(FLProcessNameConst.INVOICE_AND_PAYMENT_CREATION_PROCESS_NAME
				+ FLProcessNameConst.ACK_MODIFIED_INVOICE_HT, "invoice");
		temp.put(FLProcessNameConst.DO_CANCELLATION_PROCESS_NAME
				+ FLProcessNameConst.REVIEW_CANCEL_DO_HT, "delivery-order");
		temp.put(FLProcessNameConst.INVOICE_AND_PAYMENT_CREATION_PROCESS_NAME
				+ FLProcessNameConst.REVIEW_CANCEL_INVOICE_HT, "invoice");
		temp.put(FLProcessNameConst.FC_REMOVAL_REQUEST_PROCESS_NAME,
				"remove-factoring-company");

		// Added for qt..
		temp.put(SCProcessNameConst.QUOTATION_TENDER_PROCESS_NAME
				+ SCProcessNameConst.PREPARE_SPECIFICATION, "businessowner");
		temp.put(SCProcessNameConst.QUOTATION_TENDER_PROCESS_NAME
				+ SCProcessNameConst.CONSOLIDATE_SPECIFICATION, "businessowner");
		temp.put(SCProcessNameConst.QUOTATION_TENDER_PROCESS_NAME
				+ SCProcessNameConst.SPECIFICATION_APPROVAL, "businessowner");
		temp.put(SCProcessNameConst.QUOTATION_TENDER_PROCESS_NAME
				+ SCProcessNameConst.PREPARE_QT_DETAILS, SourcingConstants.QT_DETAILS_PAGE);
		temp.put(SCProcessNameConst.QUOTATION_TENDER_PROCESS_NAME
				+ SCProcessNameConst.REVISE_QT_SCHEDULE, SourcingConstants.QT_DETAILS_PAGE);
		temp.put(SCProcessNameConst.BUDGET_APPROVAL_PROCESS_NAME
				+ SCProcessNameConst.APPROVE_BUDGET,
				SourcingConstants.QT_APPROVAL_PAGE);
		temp.put(SCProcessNameConst.BPK_APPROVAL_PROCESS_NAME
				+ SCProcessNameConst.APPROVE_BPK,
				SourcingConstants.QT_APPROVAL_PAGE);
		temp.put(SCProcessNameConst.SCORING_PREPARATION_PROCESS_NAME
				+ SCProcessNameConst.UPDATE_SCORING, "specpreparation");
		temp.put(SCProcessNameConst.SCORING_PREPARATION_PROCESS_NAME
				+ SCProcessNameConst.FINALISE_SCORING, "specpreparation");
		temp.put(SCProcessNameConst.SCORING_PREPARATION_PROCESS_NAME
				+ SCProcessNameConst.ENDORSE_SCORING, "specpreparation");
		temp.put(SCProcessNameConst.SCORING_PREPARATION_PROCESS_NAME
				+ SCProcessNameConst.SIGNOFF_SCORING, "specpreparation");
		temp.put(SCProcessNameConst.SCORING_PREPARATION_PROCESS_NAME
				+ SCProcessNameConst.VIEW_SCORING, "specpreparation");

		temp.put(SCProcessNameConst.PCB_APPROVAL_PROCESS_NAME
				+ SCProcessNameConst.APPROVE_PCB,
				SourcingConstants.QT_APPROVAL_PAGE);
		temp.put(SCProcessNameConst.PCB_APPROVAL_PROCESS_NAME
				+ SCProcessNameConst.REVISE_PCB, SourcingConstants.QT_DETAILS_PAGE);

		temp.put(SCProcessNameConst.BPK_APPROVAL_PROCESS_NAME
				+ SCProcessNameConst.REVISE_BPK, SourcingConstants.QT_DETAILS_PAGE);
		temp.put(SCProcessNameConst.QUOTATION_TENDER_PROCESS_NAME
				+ SCProcessNameConst.PREPARE_QT_SCHEDULE, SourcingConstants.QT_DETAILS_PAGE);
		temp.put(SCProcessNameConst.COMMITTEE_APPROVAL_PROCESS_NAME
				+ SCProcessNameConst.APPROVE_COMMITTEE,
				SourcingConstants.QT_APPROVAL_PAGE);
		temp.put(SCProcessNameConst.COMMITTEE_APPROVAL_PROCESS_NAME
				+ SCProcessNameConst.REVISE_COMMITTEE, SourcingConstants.QT_DETAILS_PAGE);

		temp.put(SCProcessNameConst.SITE_VISIT_PROPOSAL_PROCESS_NAME
				+ SCProcessNameConst.FINALIZE_ATTANDANCE,
				"sitevisit-attendance");
		temp.put(SCProcessNameConst.SITE_VISIT_PROPOSAL_PROCESS_NAME
				+ SCProcessNameConst.UPDATE_ATTANDANCE, "sitevisit-attendance");
		temp.put(SCProcessNameConst.SUPPLIER_DP_AWARD_PROCESS_NAME
				+ SCProcessNameConst.PREPARE_LOA,
				SourcingConstants.LOA_LIST_DP_PAGE);
		temp.put(SCProcessNameConst.SUPPLIER_DP_ACCEPTANCE_PROCESS_NAME
				+ SCProcessNameConst.APPROVE_LOA,
				SourcingConstants.LOA_PTJ_PAGE);
		temp.put(SCProcessNameConst.SUPPLIER_DP_ACCEPTANCE_PROCESS_NAME
				+ SCProcessNameConst.REVISE_LOA, SourcingConstants.LOA_PTJ_PAGE);
		temp.put(SCProcessNameConst.SUPPLIER_DP_ACCEPTANCE_PROCESS_NAME
				+ SCProcessNameConst.ACCEPT_LOA,
				SourcingConstants.LOA_SUPPLIER_PAGE);
		temp.put(SCProcessNameConst.SUPPLIER_DP_ACCEPTANCE_PROCESS_NAME
				+ SCProcessNameConst.VERIFY_LOA, SourcingConstants.LOA_PTJ_PAGE);
		temp.put(SCProcessNameConst.QUOTATION_TENDER_PROCESS_NAME
				+ SCProcessNameConst.APPROVE_PUBLICATION,
				SourcingConstants.QT_APPROVAL_PAGE);

		temp.put(SCProcessNameConst.EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE, SourcingConstants.QT_DETAILS_PAGE);
		temp.put(SCProcessNameConst.EC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.COORDINATE_EC_MEETING,
				"coordinate-meeting");
		temp.put(SCProcessNameConst.OC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.COORDINATE_OC_MEETING,
				"coordinate-meeting");
		temp.put(SCProcessNameConst.TEC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.COORDINATE_TEC_MEETING,
				"coordinate-meeting");
		temp.put(SCProcessNameConst.FEC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.COORDINATE_FEC_MEETING,
				"coordinate-meeting");

		temp.put(SCProcessNameConst.EC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.FINALISE_EC_EVALUATION,
				"proposal-evaluation");
		temp.put(SCProcessNameConst.EC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.UPDATE_EC_EVALUATION,
				"proposal-evaluation");
		temp.put(SCProcessNameConst.EC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.ENDORSE_EC_EVALUATION,
				"proposal-evaluation");
		temp.put(SCProcessNameConst.EC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.SIGNOFF_EC_EVALUATION,
				"proposal-evaluation");
		temp.put(SCProcessNameConst.OC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.FINALISE_OC_EVALUATION,
				"proposal-evaluation");
		temp.put(SCProcessNameConst.OC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.UPDATE_OC_EVALUATION,
				"proposal-evaluation");
		temp.put(SCProcessNameConst.OC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.ENDORSE_OC_EVALUATION,
				"proposal-evaluation");
		temp.put(SCProcessNameConst.OC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.SIGNOFF_OC_EVALUATION,
				"proposal-evaluation");

		temp.put(SCProcessNameConst.TEC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.FINALISE_TEC_EVALUATION,
				"proposal-evaluation");
		temp.put(SCProcessNameConst.TEC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.UPDATE_TEC_EVALUATION,
				"proposal-evaluation");
		temp.put(SCProcessNameConst.TEC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.ENDORSE_TEC_EVALUATION,
				"proposal-evaluation");
		temp.put(SCProcessNameConst.TEC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.SIGNOFF_TEC_EVALUATION,
				"proposal-evaluation");

		temp.put(SCProcessNameConst.FEC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.FINALISE_FEC_EVALUATION,
				"proposal-evaluation");
		temp.put(SCProcessNameConst.FEC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.UPDATE_FEC_EVALUATION,
				"proposal-evaluation");
		temp.put(SCProcessNameConst.FEC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.ENDORSE_FEC_EVALUATION,
				"proposal-evaluation");
		temp.put(SCProcessNameConst.FEC_EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.SIGNOFF_FEC_EVALUATION,
				"proposal-evaluation");

		temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
				+ SCProcessNameConst.COORDINATE_FC_MEETING,
				SourcingConstants.FC_COORDINATE_MEETING_PAGE);
		temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
				+ SCProcessNameConst.UPDATE_FC_SELECTION,
				SourcingConstants.FC_SUPPLIER_FINALIZATION_PAGE);
		temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
				+ SCProcessNameConst.FINALISE_FC_SELECTION,
				SourcingConstants.FC_SUPPLIER_FINALIZATION_PAGE);
		temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
				+ SCProcessNameConst.FINALISE_BPK,
				SourcingConstants.FC_SUPPLIER_FINALIZATION_PAGE);
		temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
				+ SCProcessNameConst.ENDORSE_FC_SELECTION,
				SourcingConstants.FC_SUPPLIER_FINALIZATION_PAGE);
		temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
				+ SCProcessNameConst.SIGNOFF_FC_SELECTION,
				SourcingConstants.FC_SUPPLIER_FINALIZATION_PAGE);		
		temp.put(SCProcessNameConst.DIRECT_AWARD_PROCESS_NAME
				+ SCProcessNameConst.ENDORSE_FC_SELECTION,
				SourcingConstants.FC_SUPPLIER_FINALIZATION_PAGE);
		temp.put(SCProcessNameConst.DIRECT_AWARD_PROCESS_NAME
				+ SCProcessNameConst.SIGNOFF_FC_SELECTION,
				SourcingConstants.FC_SUPPLIER_FINALIZATION_PAGE);
		
		temp.put(SCProcessNameConst.BIDDING_AWARD_PROCESS_NAME
				+ SCProcessNameConst.CREATE_BIDDING_DETAILS,
				SourcingConstants.SCHEDULE_BIDDING_PAGE);
		temp.put(SCProcessNameConst.MULTI_AWARD_PROCESS_NAME
				+ SCProcessNameConst.PREPARE_LOI,
				SourcingConstants.LOI_LIST_PAGE);
		/*
		 * temp.put(SCProcessNameConst.MULTI_AWARD_PROCESS_NAME +
		 * SCProcessNameConst.NEGOTIATE_MA,
		 * SourcingConstants.MULTI_LOI_NEGOTIATION_PAGE);
		 */
		temp.put(SCProcessNameConst.MULTI_AWARD_PROCESS_NAME
				+ SCProcessNameConst.RECOMMEND_MA_SUPPLIER,
				SourcingConstants.MULTI_LOI_RECOMMENDATION_PAGE);
		temp.put(SCProcessNameConst.MULTI_AWARD_PROCESS_NAME
				+ SCProcessNameConst.ENDORSE_FC_SELECTION,
				SourcingConstants.MULTI_LOI_RECOMMENDATION_PAGE);
		temp.put(SCProcessNameConst.MULTI_AWARD_PROCESS_NAME
				+ SCProcessNameConst.SIGNOFF_FC_SELECTION,
				SourcingConstants.MULTI_LOI_RECOMMENDATION_PAGE);
		temp.put(SCProcessNameConst.DIRECT_AWARD_PROCESS_NAME
				+ SCProcessNameConst.PREPARE_LOI,
				SourcingConstants.LOI_LIST_PAGE);
		temp.put(SCProcessNameConst.BIDDING_AWARD_PROCESS_NAME
				+ SCProcessNameConst.PREPARE_LOI,
				SourcingConstants.LOI_LIST_PAGE);
		temp.put(SCProcessNameConst.SUPPLIER_FINALIZATION_PROCESS_NAME
				+ SCProcessNameConst.PREPARE_LOA,
				SourcingConstants.LOA_LIST_QT_PAGE);
		temp.put(SCProcessNameConst.LOA_ACCEPTANCE_PROCESS_NAME
				+ SCProcessNameConst.APPROVE_LOA,
				SourcingConstants.LOA_PTJ_PAGE);
		temp.put(SCProcessNameConst.LOA_ACCEPTANCE_PROCESS_NAME
				+ SCProcessNameConst.REVISE_LOA, SourcingConstants.LOA_PTJ_PAGE);
		temp.put(SCProcessNameConst.LOA_ACCEPTANCE_PROCESS_NAME
				+ SCProcessNameConst.ACCEPT_LOA,
				SourcingConstants.LOA_SUPPLIER_PAGE);
		temp.put(SCProcessNameConst.LOA_ACCEPTANCE_PROCESS_NAME
				+ SCProcessNameConst.VERIFY_LOA, SourcingConstants.LOA_PTJ_PAGE);

		temp.put(SCProcessNameConst.BIDDING_AWARD_PROCESS_NAME
				+ SCProcessNameConst.ENDORSE_FC_SELECTION,
				SourcingConstants.ONLINE_BIDDING_ENDORCEMENT_PAGE);
		temp.put(SCProcessNameConst.BIDDING_AWARD_PROCESS_NAME
				+ SCProcessNameConst.FINALISE_FC_SELECTION,
				SourcingConstants.ONLINE_BIDDING_ENDORCEMENT_PAGE);
		temp.put(SCProcessNameConst.LOI_ACCEPTANCE_PROCESS_NAME
				+ SCProcessNameConst.APPROVE_LOI,
				SourcingConstants.LOI_PTJ_PAGE);
		temp.put(SCProcessNameConst.LOI_ACCEPTANCE_PROCESS_NAME
				+ SCProcessNameConst.NEGOTIATE_LOI,
				SourcingConstants.LOI_PTJ_PAGE);
		temp.put(SCProcessNameConst.LOI_ACCEPTANCE_PROCESS_NAME
				+ SCProcessNameConst.REVISE_LOI, SourcingConstants.LOI_PTJ_PAGE);
		temp.put(SCProcessNameConst.LOI_ACCEPTANCE_PROCESS_NAME
				+ SCProcessNameConst.ACCEPT_LOI,
				SourcingConstants.LOI_SUPPLIER_PAGE);
		temp.put(SCProcessNameConst.BIDDING_AWARD_PROCESS_NAME
				+ SCProcessNameConst.ESCALATE_BIDDING,
				SourcingConstants.ONLINE_BIDDING_ENDORCEMENT_PAGE);
		temp.put(SCProcessNameConst.BIDDING_AWARD_PROCESS_NAME
				+ SCProcessNameConst.SIGNOFF_FC_SELECTION,
				SourcingConstants.ONLINE_BIDDING_ENDORCEMENT_PAGE);
		temp.put(SCProcessNameConst.BIDDING_AWARD_PROCESS_NAME
				+ SCProcessNameConst.COORDINATE_FC_MEETING,
				SourcingConstants.FC_COORDINATE_MEETING_PAGE);

		temp.put(SCProcessNameConst.EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.ASSIGN_FC_SECRETARY, SourcingConstants.QT_DETAILS_PAGE);

		temp.put(SCProcessNameConst.EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.ASSIGN_FC_MEMBER, "appoint-fc-committee");

		temp.put(SCProcessNameConst.EVALUATION_PROCESS_NAME
				+ SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE,
				SourcingConstants.QT_DETAILS_PAGE);

		temp.put(SCProcessNameConst.QT_MAINTENANCE_PROCESS_NAME
				+ SCProcessNameConst.APPROVE_CANCELATION,
				SourcingConstants.QT_APPROVAL_PAGE);

		temp.put(SCProcessNameConst.QT_MAINTENANCE_PROCESS_NAME
				+ SCProcessNameConst.APPROVE_SUPPLIER_ADD,
				SourcingConstants.QT_ADDITIONAL_ATTANDANCE_PAGE);

		temp.put(SCProcessNameConst.QT_MAINTENANCE_PROCESS_NAME
				+ SCProcessNameConst.BPK_APPROVE_QT_CHANGE,
				SourcingConstants.QT_APPROVAL_PAGE);
		temp.put(SCProcessNameConst.QT_MAINTENANCE_PROCESS_NAME
				+ SCProcessNameConst.FC_APPROVE_QT_CHANGE,
				SourcingConstants.QT_APPROVAL_PAGE);
		temp.put(SCProcessNameConst.QT_MAINTENANCE_PROCESS_NAME
				+ SCProcessNameConst.PUBLICATION_APPROVE_QT_CHANGE,
				SourcingConstants.QT_APPROVAL_PAGE);

		temp.put(SCProcessNameConst.QT_MAINTENANCE_PROCESS_NAME
				+ SCProcessNameConst.SUPPLIER_ACCEPT_QT_CHANGE,
				SourcingConstants.QT_APPROVAL_PAGE);

		temp.put(SCProcessNameConst.START_MANUAL_QT_CREATION
				+ SCProcessNameConst.SUBMIT_MANUAL_QUOTATION_TENDER,
				SourcingConstants.DIRECT_LOA_PAGE);
		temp.put(SCProcessNameConst.LOA_MANUAL_PROCESS_NAME
				+ SCProcessNameConst.APPROVE_MANUAL_LOA,
				SourcingConstants.DIRECT_LOA_PAGE);
		
		temp.put(SCProcessNameConst.LOA_MANUAL_PROCESS_NAME
				+ SCProcessNameConst.REVISE_MANUAL_LOA,
				SourcingConstants.DIRECT_LOA_PAGE);
		
		temp.put(SCProcessNameConst.LOA_MANUAL_PROCESS_NAME
				+ SCProcessNameConst.ACCEPT_MANUAL_LOA,
				SourcingConstants.DIRECT_LOA_SUPPLIER_PAGE);
		
		
		
		temp.put(SCProcessNameConst.LOA_MANUAL_PROCESS_NAME
				+ SCProcessNameConst.VERIFY_MANUAL_LOA,
				SourcingConstants.DIRECT_LOA_PAGE);
		

		this.getManagedBean().setBpmMap(temp);

		System.out.println("testing BPM******");

		String viewBy = "";
		// this.getManagedBean().setViewBy("");
		// this.getManagedBean().setAssignment("ALL");
		// this.getManagedBean().setDocType("");
		// FacesScopes facesScope = new FacesScopes();
		// filterViewbyNAssignment();
		try {
			// retrieveWorklistItems("ASSIGNED",this.getManagedBean().getViewBy(),
			// this.getManagedBean().getAssignment());

		} catch (Exception e) {
			log.info("BPM Errors");
			e.printStackTrace();
		}
	}

	// public void retrieveWorklistItems(String status, String viewBy, String
	// assignment) {

	// IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
	// .getServiceObject(IBpmSO.class);
	//
	// // String token = bpmSO.authenticate("roslan", "welcome1");
	// FacesScopes face = new FacesScopes();
	// String token = face.getBPMAuthenticationCtxToken();
	//
	// System.out.println("testing BPM auth******" + token);
	// BpmRequestDTO reqDTO = new BpmRequestDTO();
	//
	// reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
	// reqDTO.setOffset(calculateFinalOffSet(1));
	// reqDTO.setState(status);
	// reqDTO.setViewBy(viewBy);
	// reqDTO.setAssignment(assignment);
	// reqDTO.setCtxToken(token);
	// BpmResponseDTO responseDTO = bpmSO.getWorklistItems(reqDTO);
	//
	// System.out.println("testing BPM******"+responseDTO.getWorklistVO().getWorklistItem().size());
	// System.out.println("testing BPM******"+responseDTO.getWorklistVO().getTotal());
	//
	//
	//

	//
	// this.getManagedBean().setTaskList(responseDTO.getWorklistVO().getWorklistItem());
	//
	//
	//
	//
	// return responseDTO;
	// }

	public void navigateToDeliveryOrderURL() throws IOException,
			PortalException, SystemException {
		String orderId = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("orderId");
		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
				.getRequestScope(WebKeys.THEME_DISPLAY);

		System.out.println("getURLCurrent" + themeDisplay.getURLCurrent());
		String commonUrl = themeDisplay.getURLCurrent().substring(
				0,
				themeDisplay.getURLCurrent().indexOf(
						themeDisplay.getLayout().getGroup().getFriendlyURL())
						+ themeDisplay.getLayout().getGroup().getFriendlyURL()
								.length());
		System.out.println("commonUrl" + commonUrl);

		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		externalContext.redirect(commonUrl + "/delivery-order?orderId="
				+ orderId);
	}

	public void navigateToURL() throws PortalException, SystemException {
		log.info("......................navigateToURL");
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		String taskId = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("taskId");
		String process = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("process");
		String taskName = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("taskName");
		String docType = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("docType");
		
		String claim = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("isClaim");
		
		System.out.println("****claim****"+claim);
		
		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		
		try {
			log.info("taskId:" + taskId);

			System.out.println("********getProcessName********" + process);
			System.out.println("********getTaskName********" + taskName);
			// this.getManagedBean().setProcessName(res.getProcessName());
			// this.getManagedBean().setAction(res.getAction());
			// Object obj = res.getPojo();

			String url = "";

			if (!this.getManagedBean().getBpmMap().isEmpty()
					&& this.getManagedBean().getBpmMap().size() > 0) {

				url = (String) this.getManagedBean().getBpmMap().get(process);
				if (url == null || url.isEmpty()) {
					url = (String) this.getManagedBean().getBpmMap()
							.get(process + taskName);
				}
				if (url == null || url.isEmpty()) {
					url = (String) this.getManagedBean().getBpmMap()
							.get(process + docType);
				}
			}
			log.info("url:" + url);

			FacesScopes facesScope = new FacesScopes();
			ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
					.getRequestScope(WebKeys.THEME_DISPLAY);

			System.out.println("getURLCurrent" + themeDisplay.getURLCurrent());

			String commonUrl = themeDisplay.getURLCurrent().substring(
					0,
					themeDisplay.getURLCurrent().indexOf(
							themeDisplay.getLayout().getGroup()
									.getFriendlyURL())
							+ themeDisplay.getLayout().getGroup()
									.getFriendlyURL().length());
			System.out.println("commonUrl" + commonUrl);

			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();

			// externalContext.redirect("/web/guest/" +url +"?taskId="+taskId);
			externalContext.redirect(commonUrl + "/" + url + "?taskId="
					+ taskId + "&claim=" + claim);
			// externalContext.redirect("http://localhost:8080/web/guest/" +url
			// +"?taskId="+taskId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void filterViewbyNAssignment() {
		System.out
				.println(">>>>>>>>>>>>>>>filterViewbyNAssignment<<<<<<<<<<<<<<<<<");
		System.out.println("this.getManagedBean().getViewBy()"
				+ this.getManagedBean().getViewBy());
		System.out.println("this.getManagedBean().getAssignment()"
				+ this.getManagedBean().getAssignment());
		System.out.println("this.getManagedBean().getDocType()="
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

		System.out.println("*******backToTaskList*******");

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

	public String goToMyTasks() {
		System.out.println(">>>>>>>>>>goToMyTasks<<<<<<<<<<");
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
			this.docTypeSearchBean = super.createSearchBean(
					PmParameterDesc.class, "docTypeId");
		}

		return this.docTypeSearchBean;
	}

	public void preRenderTaskListView() {

		log.info("......................preRenderTaskListView");
		// this.getManagedBean().setViewBy("");
		// this.getManagedBean().setAssignment("ALL");
		// this.getManagedBean().setDocType("");

		// HttpServletRequest servletRequest = PortalUtil
		// .getHttpServletRequest(new FacesScopes().getRequest());
		// HttpServletRequest originalServletRequest = PortalUtil
		// .getOriginalServletRequest(servletRequest);
		// String docTypeId = originalServletRequest.getParameter("docTypeId");
		//
		// if (docTypeId != null && !docTypeId.isEmpty()) {
		// this.getManagedBean().setDocType(docTypeId);
		// originalServletRequest.getParameterMap().remove("docTypeId");
		// }
		//
		// this.getManagedBean().getTaskListDataModel().reloadData();
		// // FacesContext fc = FacesContext.getCurrentInstance();
		// fc.getApplication().getNavigationHandler()
		// .handleNavigation(fc, null, "taskListTable");
		// return "/xhtml/bpmtasklist/taskListTable.xhtml";
	}

	public void preRenderTaskView() {
		log.info("......................preRenderTaskView");

		this.getManagedBean().setViewBy("");
		this.getManagedBean().setViewByGroup("");
		this.getManagedBean().setDocNo("");
		this.getManagedBean().setDocNoGroup("");
		this.getManagedBean().setTaskDesc("");
		this.getManagedBean().setTaskDescGroup("");
		this.getManagedBean().setSelectedStatusDesc(new PmStatusDesc());
		this.getManagedBean().setSelectedStatusDescAdvance(new PmStatusDesc());
		//this.getManagedBean().setAssignment("ALL");

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
		//this.getManagedBean().getTaskListDataModel().reloadData();
		this.getManagedBean().getTaskListGroupDataModel().reloadData();
		this.getManagedBean().getTaskListMyTaskDataModel().reloadData();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "taskListView");

	}
	
	

	
	
	public String claimTask()  {
		
		System.out.println("**********claimTask");
		try{
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
	
			String taskId = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap().get("taskId");
			
			FacesScopes face = new FacesScopes();
			Object token = face.getBPMAuthenticationCtxToken();
			
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setTaskId(taskId);
			reqDTO.setCtxToken(token);
			bpmSO.claimTask(reqDTO);
			
			
			this.getManagedBean().getTaskListGroupDataModel().reloadData();
			this.getManagedBean().getTaskListMyTaskDataModel().reloadData();
		}catch(Exception e){
			throw new EpException(e.getMessage());
		}
		
		return "taskListView?faces-redirect=true";
		
		
	}
	
	
	
	public String releaseTask()  {
		System.out.println("**********releaseTask");
		try{
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
	
			String taskId = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap().get("taskId");
			
			FacesScopes face = new FacesScopes();
			Object token = face.getBPMAuthenticationCtxToken();
			
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setTaskId(taskId);
			reqDTO.setCtxToken(token);
			bpmSO.releaseTask(reqDTO);
			
			
			this.getManagedBean().getTaskListGroupDataModel().reloadData();
			this.getManagedBean().getTaskListMyTaskDataModel().reloadData();
		}catch(Exception e){
			throw new EpException(e.getMessage());
		}
		
		return "taskListView?faces-redirect=true";
		
		
	}
	
	
	
	public void claimTaskInDetailPage()  {
		System.out.println("**********claimTask");
		
		
		
		
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		
		
		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();
		
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		System.out.println("taskId:"+this.getManagedBean().getTaskId());
		System.out.println("token:"+token);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setCtxToken(token);
		bpmSO.claimTask(reqDTO);
		
		
		this.getManagedBean().getTaskListGroupDataModel().reloadData();
		this.getManagedBean().getTaskListMyTaskDataModel().reloadData();
		this.getManagedBean().setClaim(true);
		
		//return "/xhtml/bpmtasklist/taskListView.xhtml?faces-redirect=true";
		
		
	}
	
	
	
	public String constructCondition(){
		
		
		StringBuilder sb = new StringBuilder();
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		
		sb.append("languageCode^"+languageCode);
		
		sb.append("|pmStatus.docType^");
		sb.append("CM,CD");
		sb.append("^9");
		
		System.out.println("sb.toString()"+sb.toString());
		return sb.toString();
	}
	
	
	

}
