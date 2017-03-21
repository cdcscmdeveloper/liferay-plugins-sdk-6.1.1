package my.ep.web.backingbean.rpt.model;

import my.ep.exception.EpException;
import my.ep.web.backingbean.rpt.RptBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.CommonMethod;

public class CTRptViewModel{

//	protected RptBackingBean rptBackingBean;
//	protected String reportValue;
//	
//	public CTRptViewModel(RptBackingBean rptBackingBean, String reportValue){
//		this.rptBackingBean = rptBackingBean;
//		this.reportValue = reportValue;
//	}
//	
//	public RptBackingBean getBackingBean(){
//		return rptBackingBean;
//	}
//	
//	public String getReportValue(){
//		return reportValue;
//	}
//	
//	public void enableCriteriaVariables() {
//		     
//		rptBackingBean.setConditionGenerateButton(true);
//		
//		switch (reportValue) {
//		
//		case ReportConst.CT_LST_OF_CONTRACT_USERS_ALL_RPT_VAL:
//		
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtPhysicalContractNoView(true);
//			rptBackingBean.setCtAgreementView(true);
//			rptBackingBean.setCtStatusTwoView(true);
//			rptBackingBean.setCtLoanNoView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupThreeView(true);
//			rptBackingBean.setCtProcurementModeView(true);
//			rptBackingBean.setCtLOAPhysicalNoView(true);
//			rptBackingBean.setToDateView(true);
//			
//			break;
//
//		case ReportConst.CT_LST_OF_CONTRACT_ALL_RPT_VAL:
//
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtPhysicalContractNoView(true);
//			rptBackingBean.setCtAgreementView(true);
//			rptBackingBean.setCtStatusTwoView(true);
//			rptBackingBean.setCtLoanNoView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupFourView(true);
//			rptBackingBean.setCtProcurementModeView(true);
//			rptBackingBean.setCtLOAPhysicalNoView(true);
//			rptBackingBean.setCtContractFromDateView(true);
//			rptBackingBean.setCtContractEffectiveFromDateLabelView(true);
//			
//			break;
//
//		case ReportConst.CT_CONTRACT_MONITORING_RPT_VAL:
//
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtPhysicalContractNoView(true);
//			rptBackingBean.setCtAgreementView(true);
//			rptBackingBean.setCtStatusThreeView(true);
//			rptBackingBean.setCtLoanNoView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupThreeView(true);
//			rptBackingBean.setCtProcurementModeView(true);
//			rptBackingBean.setCtLOAPhysicalNoView(true);
//			rptBackingBean.setCtContractFromDateView(true);
//			rptBackingBean.setCtContractEffectiveFromDateLabelView(true);
//
//			break;
//		
//		case ReportConst.CT_LST_OF_ITEM_PRODUCT_RPT_VAL:
//
//			rptBackingBean.setCtProductServiceView(true);
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtPhysicalContractNoView(true);
//			rptBackingBean.setCtAgreementView(true);
//			rptBackingBean.setCtStatusTwoView(true);
//			rptBackingBean.setCtLoanNoView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupThreeView(true);
//			rptBackingBean.setCtProcurementModeView(true);
//			rptBackingBean.setCtLOAPhysicalNoView(true);
//			rptBackingBean.setToDateView(true);
//		
//			break;
//
//		case ReportConst.CT_LST_OF_AMENDMENT_RPT_VAL:
//
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtPhysicalContractNoView(true);
//			rptBackingBean.setCtAgreementView(true);
//			rptBackingBean.setCtStatusTwoView(true);
//			rptBackingBean.setCtLoanNoView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupThreeView(true);
//			rptBackingBean.setCtProcurementModeView(true);
//			rptBackingBean.setCtLOAPhysicalNoView(true);
//			rptBackingBean.setToDateView(true);
//			
//			break;
//		
//		case ReportConst.CT_LST_OF_UPDATE_RPT_VAL:
//
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtPhysicalContractNoView(true);
//			rptBackingBean.setCtAgreementView(true);
//			rptBackingBean.setCtStatusTwoView(true);
//			rptBackingBean.setCtLoanNoView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupThreeView(true);
//			rptBackingBean.setCtProcurementModeView(true);
//			rptBackingBean.setCtLOAPhysicalNoView(true);
//			rptBackingBean.setToDateView(true);
//		
//			break;
//		
//		case ReportConst.CT_LST_OF_SUSPENSION_RPT_VAL:
//
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtPhysicalContractNoView(true);
//			rptBackingBean.setCtAgreementView(true);
//			rptBackingBean.setCtStatusTwoView(true);
//			rptBackingBean.setCtLoanNoView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupThreeView(true);
//			rptBackingBean.setCtProcurementModeView(true);
//			rptBackingBean.setCtLOAPhysicalNoView(true);
//			rptBackingBean.setToDateView(true);
//			
//			break;
//			
//		case ReportConst.CT_LST_OF_TERMINATION_RPT_VAL:
//
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtPhysicalContractNoView(true);
//			rptBackingBean.setCtAgreementView(true);
//			rptBackingBean.setCtStatusTwoView(true);
//			rptBackingBean.setCtLoanNoView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupThreeView(true);
//			rptBackingBean.setCtProcurementModeView(true);
//			rptBackingBean.setCtLOAPhysicalNoView(true);
//			rptBackingBean.setToDateView(true);
//			
//			break;
//			
//		case ReportConst.CT_LST_OF_SUPPLIER_RPT_VAL:
//
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtPhysicalContractNoView(true);
//			rptBackingBean.setCtAgreementView(true);
//			rptBackingBean.setCtStatusTwoView(true);
//			rptBackingBean.setCtLoanNoView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupThreeView(true);
//			rptBackingBean.setCtProcurementModeView(true);
//			rptBackingBean.setCtLOAPhysicalNoView(true);
//			rptBackingBean.setToDateView(true);
//			
//			break;
//			
//		case ReportConst.CT_LST_OF_AGREEMENT_RPT_VAL:
//
//			rptBackingBean.setCtCompleteView(true);
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtPhysicalContractNoView(true);
//			rptBackingBean.setCtAgreementView(true);
//			rptBackingBean.setCtStatusTwoView(true);
//			rptBackingBean.setCtLoanNoView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupThreeView(true);
//			rptBackingBean.setCtProcurementModeView(true);
//			rptBackingBean.setCtLOAPhysicalNoView(true);
//			rptBackingBean.setToDateView(true);
//		
//			break;
//		
//		case ReportConst.CT_LST_OF_SUPPLAGREEMENT_RPT_VAL:
//
//			rptBackingBean.setCtCompleteView(true);
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtPhysicalContractNoView(true);
//			rptBackingBean.setCtAgreementView(true);
//			rptBackingBean.setCtStatusTwoView(true);
//			rptBackingBean.setCtLoanNoView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupThreeView(true);
//			rptBackingBean.setCtProcurementModeView(true);
//			rptBackingBean.setCtLOAPhysicalNoView(true);
//			rptBackingBean.setToDateView(true);
//		
//			break;
//		
//		case ReportConst.CT_LST_OF_CIRCULAR_RPT_VAL:
//
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtPhysicalContractNoView(true);
//			rptBackingBean.setCtAgreementView(true);
//			rptBackingBean.setCtStatusTwoView(true);
//			rptBackingBean.setCtLoanNoView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupThreeExtraView(true);
//			rptBackingBean.setCtProcurementModeView(true);
//			rptBackingBean.setCtLOAPhysicalNoView(true);
//			rptBackingBean.setCtCircularNoView(true);
//			rptBackingBean.setCtCircularNameView(true);
//			rptBackingBean.setCtCircularTypeView(true);
//			rptBackingBean.setCtContractFromDateView(true);
//			rptBackingBean.setCtContractEffectiveFromDateLabelView(true);
//			rptBackingBean.setCtCircularEffectiveFromDateView(true);
//			
//			break;
//		
//		case ReportConst.CT_LST_OF_PANEL_CIRCULAR_NO_RPT_VAL:
//		
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtStatusView(true);
//			rptBackingBean.setFromDateView(true);
//			rptBackingBean.setToDateView(true);
//			rptBackingBean.setCtGroupOneView(true);
//		
//			break;
//		
//		case ReportConst.CT_FULFILMENT_STATUS_RPT_VAL:
//			
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtPhysicalContractNoView(true);
//			rptBackingBean.setCtAgreementView(true);
//			rptBackingBean.setCtStatusThreeView(true);
//			rptBackingBean.setCtLoanNoView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupFourView(true);
//			rptBackingBean.setCtProcurementModeView(true);
//			rptBackingBean.setCtLOAPhysicalNoView(true);
//			rptBackingBean.setCtContractFromDateView(true);
//			rptBackingBean.setCtContractEffectiveFromDateLabelView(true);
//			
//			break;
//		
//		case ReportConst.CT_CONTRACT_TRANSACTION_MONITORING_CC_RPT_VAL:
//		
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtPhysicalContractNoView(true);
//			rptBackingBean.setCtAgreementView(true);
//			rptBackingBean.setCtStatusThreeView(true);
//			rptBackingBean.setCtLoanNoView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupFourView(true);
//			rptBackingBean.setCtProcurementModeView(true);
//			rptBackingBean.setCtLOAPhysicalNoView(true);
//			rptBackingBean.setCtContractFromDateView(true);
//			rptBackingBean.setCtContractEffectiveFromDateLabelView(true);
//		
//			break;
//		
//		case ReportConst.CT_CONTRACT_UTILIZATION_MONITORING_CC_RPT_VAL:
//			
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtPhysicalContractNoView(true);
//			rptBackingBean.setCtAgreementView(true);
//			rptBackingBean.setCtStatusThreeView(true);
//			rptBackingBean.setCtLoanNoView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupFourView(true);
//			rptBackingBean.setCtProcurementModeView(true);
//			rptBackingBean.setCtLOAPhysicalNoView(true);
//			rptBackingBean.setCtContractFromDateView(true);
//			rptBackingBean.setCtContractEffectiveFromDateLabelView(true);
//		
//			break;
//		
//		case ReportConst.CT_STATEMENT_OF_ORDER_REPORT_CC_PANEL_RPT_VAL:
//		
//		case ReportConst.CT_PANEL_STATEMENT_OF_ORDER_REPORT_CC_PANEL_RPT_VAL:
//			rptBackingBean.setCtContractNoCPView(true);
//			rptBackingBean.setCtPhysicalContractNoCPView(true);
//			rptBackingBean.setCtAgreementExtraView(true);
//			rptBackingBean.setCtStatusThreeCPView(true);
//			rptBackingBean.setCtLoanNoCPView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupFiveView(true);
//			rptBackingBean.setCtProcurementModeCPView(true);
//			rptBackingBean.setCtLOAPhysicalNoCPView(true);
//			rptBackingBean.setCtSupplierNameCPView(true);
//			rptBackingBean.setCtContractFromDateView(true);
//			rptBackingBean.setCtContractOrderFromDateLabelView(true);
//
//			break;
//		
//		case ReportConst.CT_LIST_OF_CONTRACT_CC_RPT_VAL:
//			
//		case ReportConst.CT_PANEL_LIST_OF_CONTRACT_CC_RPT_VAL:
//			rptBackingBean.setCtContractNoView(true);
//			rptBackingBean.setCtPhysicalContractNoView(true);
//			rptBackingBean.setCtAgreementView(true);
//			rptBackingBean.setCtStatusTwoView(true);
//			rptBackingBean.setCtLoanNoView(true);
//			rptBackingBean.setCtMinistryView(true);
//			rptBackingBean.setCtPegaWaiPengawalView(true);
//			rptBackingBean.setCtKumpulanPTJView(true);
//			rptBackingBean.setCtPtjView(true);
//			rptBackingBean.setCtGroupFourView(true);
//			rptBackingBean.setCtProcurementModeView(true);
//			rptBackingBean.setCtLOAPhysicalNoView(true);
//			rptBackingBean.setCtContractFromDateView(true);
//			rptBackingBean.setCtContractEffectiveFromDateLabelView(true);
//		
//			break;
//
//		default:
//			throw new EpException(
//					CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));
//		}
//	}
}