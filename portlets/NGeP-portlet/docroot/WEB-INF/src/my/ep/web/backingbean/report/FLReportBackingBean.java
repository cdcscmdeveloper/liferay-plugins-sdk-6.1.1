package my.ep.web.backingbean.report;

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
	public void criteriaVariablesByReportByFL(ReportsGenerateBean rptGenBean){
		
	switch (getManagedBean().getReportValue()) {
		case ReportConst.CON_REQ_RPT_MIN_VAL:	
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_REQ_RPT_MIN_NAME));		
			 break;
	 	case ReportConst.CON_ORD_RPT_MIN_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);			
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlJabatanView(true);			
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_ORD_RPT_MIN_NAME));
			 break;
		case ReportConst.DEL_ORD_RPT_MIN_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			//rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_RPT_MIN_NAME));
			 break;
		case ReportConst.FL_REC_NOTE_RPT_MIN_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			//rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlContractRequestView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_RPT_MIN_NAME));
			 break;
		case ReportConst.LATE_DEL_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlContractTypeView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			//rptGenBean.setFlItemTypeView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LATE_DEL_RPT_MIN_NAME));
			 break;
		case ReportConst.DEL_ORD_REJ_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlContractTypeView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlItemNameView(true);
			rptGenBean.setFlItemCodeView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_REJ_RPT_MIN_NAME));
			 break;
		case ReportConst.SHP_INSTR_RPT_MIN_VAL:
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlMtoContractView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SHP_INSTR_RPT_MIN_NAME));	
			 break;
		case ReportConst.STP_INSTR_DEL_RPT_MIN_VAL:
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractTypeView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			//rptGenBean.setFlItemTypeView(true); 
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.STP_INSTR_DEL_RPT_MIN_NAME));		
			 break;
		case ReportConst.INV_RPT_VIEW_SUPL_MIN_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_SUPL_MIN_NAME));	
			 break;
		case ReportConst.INV_RPT_VIEW_PTJ_MIN_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_PTJ_MIN_NAME));	
			 break;
		case ReportConst.PYMT_MATCH_RPT_MIN_VAL:
			//rptGenBean.setFlDurationView(true);
			//rptGenBean.setFlInvoiceNoView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlStatusView(true);	
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PYMT_MATCH_RPT_MIN_NAME));	
			 break;
		case ReportConst.DED_RPT_MIN_VAL:
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlStatusView(true);
			//rptGenBean.setFlDeductionTypeView(true);
			//rptGenBean.setFlPmoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DED_RPT_MIN_NAME));
			 break;
		case ReportConst.CON_REQ_CANC_RPT_MIN_VAL:
			//rptGenBean.setFlCrNoView(true);
			//rptGenBean.setFlApproverNameView(true);
			//rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_REQ_CANC_RPT_MIN_NAME));	
			 break;
		case ReportConst.CON_ORD_CANC_RPT_MIN_VAL:
			//rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_ORD_CANC_RPT_MIN_NAME));		
			 break;
		case ReportConst.DEL_ORD_CANC_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			//rptGenBean.setFlRecievingOfficerView(true);
			//rptGenBean.setFlJabatanView(true);
			//rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_CANC_RPT_MIN_NAME));
			 break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_CANC_RPT_MIN_NAME));	
			 break;
		case ReportConst.INV_CANC_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			//rptGenBean.setFlJabatanView(true);
			//rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_CANC_RPT_MIN_NAME));
			break;
		case ReportConst.YEP_RPT_MIN_VAL:
			rptGenBean.setFlDocumentTypeView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_RPT_MIN_NAME));	
			break;
		case ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_MIN_VAL:
			rptGenBean.setFlDocumentTypeView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_MIN_NAME));	
			break;
		case ReportConst.SUP_PERF_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlCriteriaView(true);
			//rptGenBean.setFlScoringView(true);
			rptGenBean.setFlPtjView(true);
			//rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			//rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SUP_PERF_RPT_MIN_NAME));	
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
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_REQ_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.CON_ORD_RPT_CEN_PEN_VAL:			
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlPtjView(true);
			//rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlStatusView(true);		
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_ORD_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.DEL_ORD_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPtjView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			//rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_RPT_CEN_PEN_NAME));	
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
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.LATE_DEL_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			//rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlContractTypeView(true);
			rptGenBean.setFlSstView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			//rptGenBean.setFlItemTypeView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LATE_DEL_RPT_CEN_PEN_NAME));	
			break;
			
			
			
		case ReportConst.DEL_ORD_REJ_RPT_CEN_PEN_VAL:
			//rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlRecievingOfficerView(true);
			//rptGenBean.setFlDeliveryAddressView(true);
			//rptGenBean.setFlContractNoView(true);
			//rptGenBean.setFlSstView(true);
			//rptGenBean.setFlContractTypeView(true);
			//rptGenBean.setFlDurationView(true);
			//rptGenBean.setFlPkpNoView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			//rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_REJ_RPT_CEN_PEN_NAME));
			break;
		case ReportConst.SHP_INSTR_RPT_CEN_PEN_VAL:
			/*rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlMtoContractView(true);
			rptGenBean.setFromDateView(true);*/
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SHP_INSTR_RPT_CEN_PEN_NAME));
			break;
		case ReportConst.STP_INSTR_DEL_RPT_CEN_PEN_VAL:
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			rptGenBean.setFlItemTypeView(true);
			rptGenBean.setFlContractTypeView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.STP_INSTR_DEL_RPT_CEN_PEN_NAME));		
			break;
		case ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_NAME));	
			break;
		case ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_NAME));	
			break;
		case ReportConst.PYMT_MATCH_RPT_CEN_PEN_VAL:
			//rptGenBean.setFlDurationView(true);
			//rptGenBean.setFlInvoiceNoView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PYMT_MATCH_RPT_CEN_PEN_NAME));
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
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DED_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.CON_REQ_CANC_RPT_CEN_PEN_VAL:
			//rptGenBean.setFlCrNoView(true);
			//rptGenBean.setFlApproverNameView(true);
			//rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			//rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_REQ_CANC_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.CON_ORD_CANC_RPT_CEN_PEN_VAL:
			//rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_ORD_CANC_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.DEL_ORD_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			//rptGenBean.setFlRecievingOfficerView(true);
			//rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_CANC_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_NAME));		
			break;
		case ReportConst.INV_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_CANC_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.YEP_RPT_CEN_PEN_VAL:
			rptGenBean.setFlDocumentTypeView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_RPT_CEN_PEN_NAME));
			break;
		case ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_VAL:
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlDocumentTypeView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_NAME));
			break;
		case ReportConst.SUP_PERF_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			/*rptGenBean.setFlCriteriaView(true);
			rptGenBean.setFlScoringView(true);
*/			rptGenBean.setFlPtjView(true);
			/*rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);*/			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SUP_PERF_RPT_CEN_PEN_NAME));		
			break;
		case ReportConst.PUR_REQ_RPT_PO_VAL:	
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlStatusView(true);			
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PUR_REQ_RPT_PO_NAME));	
			break;
		case ReportConst.PUR_ORD_RPT_PO_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlStatusView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);			
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PUR_ORD_RPT_PO_NAME));
			break;
		case ReportConst.EMR_PROCUR_RPT_PO_VAL:
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlStatusView(true);
			//rptGenBean.setFlDurationView(true);
			//rptGenBean.setFlPoAmountView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.EMR_PROCUR_RPT_PO_NAME));
			break;
		case ReportConst.PUR_ORD_REJ_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			//rptGenBean.setFlDurationView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setToDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PUR_ORD_REJ_RPT_PO_NAME));	
			break;
		case ReportConst.DEL_ORD_RPT_PO_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_RPT_PO_NAME));
			break;
		case ReportConst.FL_REC_NOTE_RPT_PO_VAL:
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlDeliveryAddressView(true);
			//rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_RPT_PO_NAME));
			break;
		case ReportConst.LATE_DEL_RPT_PO_VAL:
			//rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlDeliveryAddressView(true);
			//rptGenBean.setFlRecievingOfficerView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LATE_DEL_RPT_PO_NAME));
			break;
		case ReportConst.DEL_ORD_REJ_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlDeliveryAddressView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlItemNameView(true);
			rptGenBean.setFlItemCodeView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_REJ_RPT_PO_NAME));	
			break;
		case ReportConst.STP_INSTR_DEL_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.STP_INSTR_DEL_RPT_PO_NAME));
			break;
		case ReportConst.INV_RPT_VIEW_SUPL_PO_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPtjView(true); 
			rptGenBean.setFlStatusView(true);			
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_SUPL_PO_NAME));		
			break;
		case ReportConst.INV_RPT_VIEW_PTJ_PO_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlPkpNoView(true);
			//rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_PTJ_PO_NAME));	
			break;
		case ReportConst.PYMT_MATCH_RPT_PO_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlInvoiceNoView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PYMT_MATCH_RPT_PO_NAME));		
			break;
		case ReportConst.PUR_REQ_CANC_RPT_PO_VAL:
			//rptGenBean.setFlPrNoView(true);
			//rptGenBean.setFlApproverNameView(true);
			//rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			//rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setToDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PUR_REQ_CANC_RPT_PO_NAME));
			break;
		case ReportConst.PUR_ORD_CANC_RPT_PO_VAL:
			//rptGenBean.setFlApproverNameView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PUR_ORD_CANC_RPT_PO_NAME));
			break;
		case ReportConst.DEL_ORD_CANC_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			//rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_CANC_RPT_PO_NAME));	
			break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			//rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_CANC_RPT_PO_NAME));
			break;
		case ReportConst.INV_CANC_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			//rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);			
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_CANC_RPT_PO_NAME));	
			break;
		case ReportConst.YEP_RPT_PO_VAL:
			rptGenBean.setFlDocumentTypeView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_RPT_PO_NAME));	
			break;
		case ReportConst.YEP_AP58A_RPT_PO_VAL:
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_AP58A_RPT_PO_NAME));	
			break;
		case ReportConst.SUP_PERF_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlCriteriaView(true);
			//rptGenBean.setFlScoringView(true);
			rptGenBean.setFlPtjView(true);
			//rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			//rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SUP_PERF_RPT_PO_NAME));	
			break;
		case ReportConst.SUMM_RPT_PAN_ITM_VAL:
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlStatusView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SUMM_RPT_PAN_ITM_NAME));	
			break;
		default:
			throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));
            //break;
		}		
	}
}
