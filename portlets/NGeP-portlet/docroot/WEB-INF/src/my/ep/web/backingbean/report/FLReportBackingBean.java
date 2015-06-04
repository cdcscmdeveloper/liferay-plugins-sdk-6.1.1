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


/**
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name="flReportBackingBean")
@ViewScoped
public class FLReportBackingBean extends SourcingBaseBackingBean<ReportMangedBean>{

	/*
	 * Enable FL module criteria by report name
	 */
	public void enableCriteriaVariablesByReportByFL(ReportsGenerateBean rptGenBean,	List<String> authoriztionList){
		
	switch (getManagedBean().getReportValue()) {
		case ReportConst.CON_REQ_RPT_MIN_VAL:			
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlGroupOneView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);
			 break;
	 	case ReportConst.CON_ORD_RPT_MIN_VAL:
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);		
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlGroupTwoView(true);			
			rptGenBean.setFromDateView(true);
			 break;
		case ReportConst.DEL_ORD_RPT_MIN_VAL:			
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlGroupThreeView(true);	
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);
			 break;
		case ReportConst.FL_REC_NOTE_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlGroupThreeView(true);
			rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlContractRequestView(true);
			rptGenBean.setFromDateView(true);
			 break;
		case ReportConst.LATE_DEL_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlGroupFourView(true);
			rptGenBean.setFromDateView(true);
			 break;
		case ReportConst.DEL_ORD_REJ_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlItemNameView(true);
			rptGenBean.setFlItemCodeView(true);
			rptGenBean.setFromDateView(true);
			 break;
		case ReportConst.SHP_INSTR_RPT_MIN_VAL:
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlMtoContractView(true);
			rptGenBean.setFromDateView(true);
			 break;
		case ReportConst.STP_INSTR_DEL_RPT_MIN_VAL:
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlGroupFiveView(true);
			rptGenBean.setFromDateView(true);		
			 break;
		case ReportConst.INV_RPT_VIEW_SUPL_MIN_VAL:
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			 break;
		case ReportConst.INV_RPT_VIEW_PTJ_MIN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupSixView(true);
			 break;
		case ReportConst.PYMT_MATCH_RPT_MIN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlStatusView(true);	
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupSevenView(true);
			 break;
		case ReportConst.DED_RPT_MIN_VAL:
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlGroupSixView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFromDateView(true);
			 break;
		case ReportConst.CON_REQ_CANC_RPT_MIN_VAL:
			rptGenBean.setFlGroupSixView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFromDateView(true);
			 break;
		case ReportConst.CON_ORD_CANC_RPT_MIN_VAL:
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlGroupSixView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);
			 break;
		case ReportConst.DEL_ORD_CANC_RPT_MIN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlGroupOneView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFromDateView(true);
			 break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlGroupOneView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFromDateView(true);
			 break;
		case ReportConst.INV_CANC_RPT_MIN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlGroupOneView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.YEP_RPT_MIN_VAL:
			rptGenBean.setFlDocumentTypeView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_MIN_VAL:
			rptGenBean.setFlDocumentTypeView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.SUP_PERF_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlGroupEightView(true);
			break;
		case ReportConst.CON_REQ_RPT_CEN_PEN_VAL:	
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlSupplierView(true);		
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupOneView(true);
			break;
		case ReportConst.CON_ORD_RPT_CEN_PEN_VAL:			
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlGroupTwoView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlStatusView(true);		
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.DEL_ORD_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			rptGenBean.setFlGroupThreeView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlContractRequestView(true);
			rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);	
			rptGenBean.setFlGroupThreeView(true);
			break;
		case ReportConst.LATE_DEL_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlGroupFourView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);
			break;
			
			
			
		case ReportConst.DEL_ORD_REJ_RPT_CEN_PEN_VAL:			
			break;
		case ReportConst.SHP_INSTR_RPT_CEN_PEN_VAL:			
			break;
		case ReportConst.STP_INSTR_DEL_RPT_CEN_PEN_VAL:
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);			
			rptGenBean.setFlGroupFiveView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_VAL:
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupSixView(true);
			break;
		case ReportConst.PYMT_MATCH_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupSevenView(true);
			break;
		case ReportConst.DED_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlDeductionTypeView(true);
			rptGenBean.setFlPmoView(true);
			rptGenBean.setFlPkpNoView(true);			
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupSixView(true);
			break;
		case ReportConst.CON_REQ_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupSixView(true);
			break;
		case ReportConst.CON_ORD_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupSixView(true);
			break;
		case ReportConst.DEL_ORD_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupOneView(true);
			break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupOneView(true);
			break;
		case ReportConst.INV_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupOneView(true);
			break;
		case ReportConst.YEP_RPT_CEN_PEN_VAL:
			rptGenBean.setFlDocumentTypeView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_VAL:
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlDocumentTypeView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.SUP_PERF_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlGroupEightView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPtjView(true);			
			break;
		case ReportConst.PUR_REQ_RPT_PO_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlStatusView(true);			
			rptGenBean.setFromDateView(true);	
			rptGenBean.setFlGroupSeventeenView(true);
			break;
		case ReportConst.PUR_ORD_RPT_PO_VAL:
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);		
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupElevenView(true);
			break;
		case ReportConst.EMR_PROCUR_RPT_PO_VAL:
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupTenView(true);
			break;
		case ReportConst.PUR_ORD_REJ_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setToDateView(true);
			rptGenBean.setFlGroupTenView(true);
			break;
		case ReportConst.DEL_ORD_RPT_PO_VAL:
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupTwelveView(true);
			break;
		case ReportConst.FL_REC_NOTE_RPT_PO_VAL:
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);	
			rptGenBean.setFlGroupTwelveView(true);
			break;
		case ReportConst.LATE_DEL_RPT_PO_VAL:
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);	
			rptGenBean.setFlGroupThirteenView(true);
			break;
		case ReportConst.DEL_ORD_REJ_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlItemNameView(true);
			rptGenBean.setFlItemCodeView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.STP_INSTR_DEL_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);	
			rptGenBean.setFlGroupTenView(true);
			break;
		case ReportConst.INV_RPT_VIEW_SUPL_PO_VAL:
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPtjView(true); 
			rptGenBean.setFlStatusView(true);			
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.INV_RPT_VIEW_PTJ_PO_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupTenView(true);
			break;
		case ReportConst.PYMT_MATCH_RPT_PO_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlInvoiceNoView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupFourteenView(true);
			break;
		case ReportConst.PUR_REQ_CANC_RPT_PO_VAL:
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setToDateView(true);
			rptGenBean.setFlGroupTenView(true);
			break;
		case ReportConst.PUR_ORD_CANC_RPT_PO_VAL:
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);	
			rptGenBean.setFlGroupTenView(true);
			break;
		case ReportConst.DEL_ORD_CANC_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupFifteenView(true);
			break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);	
			rptGenBean.setFlGroupFifteenView(true);
			break;
		case ReportConst.INV_CANC_RPT_PO_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);	
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupFifteenView(true);
			break;
		case ReportConst.YEP_RPT_PO_VAL:
			rptGenBean.setFlDocumentTypeView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.YEP_AP58A_RPT_PO_VAL:
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			break;
		case ReportConst.SUP_PERF_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlGroupSixteenView(true);
			break;
		case ReportConst.SUMM_RPT_PAN_ITM_VAL:
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setFlGroupNineView(true);
			break;
		default:
			throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));           
		}		
	}
}
