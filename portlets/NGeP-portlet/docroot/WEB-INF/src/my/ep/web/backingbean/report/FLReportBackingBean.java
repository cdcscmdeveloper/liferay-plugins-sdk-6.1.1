/*
 * 
 */
package my.ep.web.backingbean.report;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.exception.EpException;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;
import my.ep.web.constant.report.CommonMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class FLReportBackingBean.
 * 
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name = "flReportBackingBean")
@ViewScoped
public class FLReportBackingBean extends
		SourcingBaseBackingBean<ReportMangedBean> {

	/**
	 * enableCriteriaVariablesByReportByFL - Enable criteria variables by report
	 * of ORDER FULFILLMENT.
	 * 
	 * @param rptGenBean
	 *            the rpt gen bean
	 * @param authoriztionList
	 *            the authoriztion list
	 */
	public void enableCriteriaVariablesByReportByFL(
			ReportsGenerateBean rptGenBean, List<String> authoriztionList) {

		switch (getManagedBean().getReportValue().trim()) {
		case ReportConst.CON_REQ_RPT_MIN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlGroupThreeView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlContractRequestCreatedDateView(true);
			break;
		case ReportConst.CON_ORD_RPT_MIN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlItemNameView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlGroupThreeView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlContractOrderDateView(true);
			break;
		case ReportConst.DEL_ORD_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlGroupSevenView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlItemNameView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlSupplierDeliveryOrderNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlDeliveryOrderCreatedDateView(true);
			break;
		case ReportConst.FL_REC_NOTE_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlGroupThreeView(true);
			rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlContractRequestView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlFRNCreatedDateView(true);
			break;
		case ReportConst.LATE_DEL_RPT_MIN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			rptGenBean.setFlSupplierDeliveryOrderNoView(true);
			rptGenBean.setFlGroupTwentySevenView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlDeliveryOrderCreatedDateView(true);
			break;

		case ReportConst.SHP_INSTR_RPT_MIN_VAL:
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlMtoContractView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.STP_INSTR_DEL_RPT_MIN_VAL:

			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlGroupFiveView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.INV_RPT_VIEW_SUPL_MIN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.INV_RPT_VIEW_PTJ_MIN_VAL:
			rptGenBean.setFlPurchaseOrderNoView(true);
			// rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlInvoiceNoView(true);
			rptGenBean.setFlSupplierInvoiceNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlInvoiceCreatedDateView(true);
			// rptGenBean.setFlGroupThreeView(true);
			rptGenBean.setFlGroupThreeAllRolecodesWithoutSupplView(true);
			break;
		case ReportConst.PYMT_MATCH_RPT_MIN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlPaymentAdviceNoView(true);
			rptGenBean.setFlInvoiceNoView(true);
			rptGenBean.setFlSupplierInvoiceNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlContractOrderCreatedDateView(true);
			rptGenBean.setFlGroupThreeView(true);
			break;
		case ReportConst.DED_RPT_MIN_VAL:
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlGroupSixView(true);
			rptGenBean.setFlSupplierView(true);
			// rptGenBean.setFromDateView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlContractOrderCreatedDateView(true);
			break;
		case ReportConst.CON_REQ_CANC_RPT_MIN_VAL:
			rptGenBean.setFlGroupSixView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlContractRequestView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlContractRequestRejectionDateView(true);
			break;
		case ReportConst.CON_ORD_CANC_RPT_MIN_VAL:
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlGroupSixView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlContractOrderCancellationDateView(true);
			break;
		case ReportConst.DEL_ORD_CANC_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlGroupTwentySevenView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			rptGenBean.setFlSupplierDeliveryOrderNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlDeliveryOrderCancellationDateView(true);
			break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlGroupTwentySevenView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlFRNCancellationDateView(true);
			break;
		case ReportConst.INV_CANC_RPT_MIN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlGroupOneView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlInvoiceNoView(true);
			rptGenBean.setFlSupplierInvoiceNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlInvoiceCancellationDateView(true);
			break;

		case ReportConst.SUP_PERF_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlGroupEightView(true);
			break;
		case ReportConst.CON_REQ_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlContractRequestCreatedDateView(true);
			rptGenBean.setFlGroupTwoView(true);
			break;
		case ReportConst.CON_ORD_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlGroupTwoView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlItemNameView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlContractOrderDateView(true);
			break;
		case ReportConst.DEL_ORD_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			rptGenBean.setFlGroupTwentyFourView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlItemNameView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlSupplierDeliveryOrderNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlDeliveryOrderCreatedDateView(true);
			break;
		case ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			// rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlContractRequestView(true);
			rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlFRNCreatedDateView(true);
			rptGenBean.setFlGroupTwoView(true);
			break;
		case ReportConst.LATE_DEL_RPT_CEN_PEN_VAL:

			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlGroupFourView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			rptGenBean.setFlSupplierDeliveryOrderNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlDeliveryOrderCreatedDateView(true);
			break;
		case ReportConst.SHP_INSTR_RPT_CEN_PEN_VAL:
			break;
		case ReportConst.STP_INSTR_DEL_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlGroupFiveView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_VAL:
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_VAL:
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlInvoiceNoView(true);
			rptGenBean.setFlSupplierInvoiceNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlInvoiceCreatedDateView(true);
			// rptGenBean.setFlGroupFourteenView(true);
			rptGenBean.setFlGroupFourteenAllRolecodesWithoutSupplView(true);
			break;
		case ReportConst.PYMT_MATCH_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			// rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlPaymentAdviceNoView(true);
			rptGenBean.setFlInvoiceNoView(true);
			rptGenBean.setFlSupplierInvoiceNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlContractOrderCreatedDateView(true);
			rptGenBean.setFlGroupThreeView(true);
			break;
		case ReportConst.CON_REQ_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlContractRequestView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlContractRequestRejectionDateView(true);
			rptGenBean.setFlGroupTwentyFiveView(true);
			break;
		case ReportConst.CON_ORD_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);

			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlContractOrderCancellationDateView(true);
			rptGenBean.setFlGroupTwentyFiveView(true);
			break;
		case ReportConst.DEL_ORD_CANC_RPT_CEN_PEN_VAL:

			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);

			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			rptGenBean.setFlSupplierDeliveryOrderNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlDeliveryOrderCancellationDateView(true);
			rptGenBean.setFlGroupTwentySixView(true);
			break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlFRNCancellationDateView(true);
			rptGenBean.setFlGroupTwentySixView(true);
			break;
		case ReportConst.INV_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlInvoiceNoView(true);
			rptGenBean.setFlSupplierInvoiceNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlInvoiceCancellationDateView(true);
			rptGenBean.setFlGroupOneView(true);
			break;
		case ReportConst.SUP_PERF_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlGroupEightView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			break;
		case ReportConst.PUR_REQ_RPT_PO_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlStatusView(true);
			// rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupSeventeenView(true);
			rptGenBean.setFlPrNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlPurchaseOrderCreatedDateView(true);
			break;
		case ReportConst.PUR_ORD_RPT_PO_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlPkpNoView(true);
			// rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupElevenView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlPurchaseOrderCreatedDateView(true);
			break;
		case ReportConst.EMR_PROCUR_RPT_PO_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupFourteenView(true);
			break;
		case ReportConst.PUR_ORD_REJ_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			// rptGenBean.setFromDateView(true);
			rptGenBean.setToDateView(true);
			rptGenBean.setFlGroupTenView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlPurchaseOrderRejectedDateView(true);
			break;
		case ReportConst.DEL_ORD_RPT_PO_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			// rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlSupplierDeliveryOrderNoView(true);
			// rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupTwelveView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlDeliveryOrderDateView(true);
			break;
		case ReportConst.FL_REC_NOTE_RPT_PO_VAL:
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlAcknowledgeOfficerView(true);
			// rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlPkpNoView(true);
			// rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupTwelveView(true);
			rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlFRNCreatedDateView(true);
			break;
		case ReportConst.LATE_DEL_RPT_PO_VAL:
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlAcknowledgeOfficerView(true);
			// rptGenBean.setFromDateView(true);
			//rptGenBean.setFlGroupThirteenView(true);
			rptGenBean.setFlGroupThirteenWithSupplAckRecView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			rptGenBean.setFlSupplierDeliveryOrderNoView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlFRNCreatedDateView(true);
			break;
		case ReportConst.STP_INSTR_DEL_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupTenView(true);
			break;
		case ReportConst.INV_RPT_VIEW_SUPL_PO_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.INV_RPT_VIEW_PTJ_PO_VAL:
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlInvoiceNoView(true);
			rptGenBean.setFlSupplierInvoiceNoView(true);
			// rptGenBean.setFromDateView(true);
			// rptGenBean.setFlGroupFourteenView(true);
			rptGenBean.setFlGroupFourteenAllRolecodesWithoutSupplView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlInvoiceCreatedDateView(true);
			break;
		case ReportConst.PYMT_MATCH_RPT_PO_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlInvoiceNoView(true);
			rptGenBean.setFlSupplierInvoiceNoView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlPaymentAdviceNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlPayOfficerView(true);
			// rptGenBean.setFromDateView(true);
			//rptGenBean.setFlGroupFourteenView(true);
			rptGenBean.setFlGroupFourteenWithSupplPaymentOffView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlPurchaseOrderCreatedDateView(true);
			break;
		case ReportConst.PUR_REQ_CANC_RPT_PO_VAL:
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlApproverNameView(true);
			// rptGenBean.setFromDateView(true);
			rptGenBean.setToDateView(true);
			//rptGenBean.setFlGroupTenView(true);
			rptGenBean.setFlGroupTenWithSupplReqAppView(true);
			rptGenBean.setFlPrNoView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlPurchaseRequestRejectionDateView(true);
			break;
		case ReportConst.PUR_ORD_CANC_RPT_PO_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPkpNoView(true);
			// rptGenBean.setFromDateView(true);
			//rptGenBean.setFlGroupTwentyThreeView(true);
			rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlGroupTwentyThreeWithSupplCancelView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlPurchaseOrderCancellationDateView(true);
			break;
		case ReportConst.DEL_ORD_CANC_RPT_PO_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			rptGenBean.setFlSupplierDeliveryOrderNoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPkpNoView(true);
			// rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupThirteenView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlDeliveryOrderCancellationDateView(true);
			break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setFlAcknowledgeOfficerView(true);
			/*
			 * rptGenBean.setFlContractNoView(true);
			 * rptGenBean.setFlSstView(true);
			 */

			// rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupThirteenView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlFRNCancellationDateView(true);
			break;
		case ReportConst.INV_CANC_RPT_PO_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlInvoiceNoView(true);
			rptGenBean.setFlSupplierInvoiceNoView(true);
			// rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupFifteenView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlInvoiceCancellationDateView(true);
			break;
		case ReportConst.SUMM_RPT_PAN_ITM_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlDocumentTypeView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlFromDateRangeView(true);
			rptGenBean.setFlGroupNineView(true);
			break;
		case ReportConst.FL_STP_INSTR_INIT_PTJ_RPT_FL_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlStopInstructionCreatedDateView(true);
			/* rptGenBean.setFlPkpNoView(true); */
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlStopInstructionNoView(true);
			rptGenBean.setFlGroupTwentyTwoView(true);
			break;
		case ReportConst.FL_STP_INSTR_INIT_PTJ_RPT_FL_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlStopInstructionCreatedDateView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlStopInstructionNoView(true);
			rptGenBean.setFlGroupNineteenView(true);
			break;
		case ReportConst.FL_STP_INSTR_INIT_PTJ_RPT_FL_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlApproverNameView(true);
			/*
			 * rptGenBean.setFlContractNoView(true);
			 * rptGenBean.setFlSstView(true);
			 */
			// rptGenBean.setFromDateView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlStopInstructionNoView(true);
			rptGenBean.setFlGroupEighteenView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlStopInstructionCreatedDateView(true);
			break;
		case ReportConst.FL_STP_INSTR_INIT_SUP_RPT_FL_MIN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlStopInstructionCreatedDateView(true);
			/* rptGenBean.setFlPkpNoView(true); */
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlStopInstructionNoView(true);
			rptGenBean.setFlGroupTwentyView(true);
			break;
		case ReportConst.FL_STP_INSTR_INIT_SUP_RPT_FL_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlStopInstructionCreatedDateView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlStopInstructionNoView(true);
			rptGenBean.setFlGroupTwentyOneView(true);
			break;
		case ReportConst.FL_STP_INSTR_INIT_SUP_RPT_FL_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPegaWaiPengawalView(true);
			rptGenBean.setFlKumpulanPTJView(true);
			rptGenBean.setFlPtjView(true);
			/*
			 * rptGenBean.setFlContractNoView(true);
			 * rptGenBean.setFlSstView(true);
			 */
			rptGenBean.setFlStatusView(true);
			// rptGenBean.setFromDateView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlStopInstructionNoView(true);
			//rptGenBean.setFlGroupEighteenView(true);
			rptGenBean.setFlGroupEighteenWithSupplApproverView(true);
			rptGenBean.setFlFromDateView(true);
			rptGenBean.setFlStopInstructionCreatedDateView(true);
			break;
		default:
			throw new EpException(
					CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));
		}
	}
}
