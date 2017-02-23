package my.ep.web.backingbean.rpt.model;

import my.ep.exception.EpException;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.CommonMethod;

public class CTRptViewModel{

	protected ReportsGenerateBean rptGenBean;
	protected String reportValue;
	
	public CTRptViewModel(ReportsGenerateBean rptGenBean, String reportValue){
		this.rptGenBean = rptGenBean;
		this.reportValue = reportValue;
	}
	
	public ReportsGenerateBean getRptGenBean(){
		return rptGenBean;
	}
	
	public String getReportValue(){
		return reportValue;
	}
	
	public void enableCriteriaVariables() {
		     
		rptGenBean.setConditionGenerateButton(true);
		
		switch (reportValue) {
		
		case ReportConst.CT_LST_OF_CONTRACT_USERS_ALL_RPT_VAL:
		
			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtPhysicalContractNoView(true);
			rptGenBean.setCtAgreementView(true);
			rptGenBean.setCtStatusTwoView(true);
			rptGenBean.setCtLoanNoView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupThreeView(true);
			rptGenBean.setCtProcurementModeView(true);
			rptGenBean.setCtLOAPhysicalNoView(true);
			rptGenBean.setToDateView(true);
			
			break;

		case ReportConst.CT_LST_OF_CONTRACT_ALL_RPT_VAL:

			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtPhysicalContractNoView(true);
			rptGenBean.setCtAgreementView(true);
			rptGenBean.setCtStatusTwoView(true);
			rptGenBean.setCtLoanNoView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupFourView(true);
			rptGenBean.setCtProcurementModeView(true);
			rptGenBean.setCtLOAPhysicalNoView(true);
			rptGenBean.setCtContractFromDateView(true);
			rptGenBean.setCtContractEffectiveFromDateLabelView(true);
			
			break;

		case ReportConst.CT_CONTRACT_MONITORING_RPT_VAL:

			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtPhysicalContractNoView(true);
			rptGenBean.setCtAgreementView(true);
			rptGenBean.setCtStatusThreeView(true);
			rptGenBean.setCtLoanNoView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupThreeView(true);
			rptGenBean.setCtProcurementModeView(true);
			rptGenBean.setCtLOAPhysicalNoView(true);
			rptGenBean.setCtContractFromDateView(true);
			rptGenBean.setCtContractEffectiveFromDateLabelView(true);

			break;
		
		case ReportConst.CT_LST_OF_ITEM_PRODUCT_RPT_VAL:

			rptGenBean.setCtProductServiceView(true);
			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtPhysicalContractNoView(true);
			rptGenBean.setCtAgreementView(true);
			rptGenBean.setCtStatusTwoView(true);
			rptGenBean.setCtLoanNoView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupThreeView(true);
			rptGenBean.setCtProcurementModeView(true);
			rptGenBean.setCtLOAPhysicalNoView(true);
			rptGenBean.setToDateView(true);
		
			break;

		case ReportConst.CT_LST_OF_AMENDMENT_RPT_VAL:

			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtPhysicalContractNoView(true);
			rptGenBean.setCtAgreementView(true);
			rptGenBean.setCtStatusTwoView(true);
			rptGenBean.setCtLoanNoView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupThreeView(true);
			rptGenBean.setCtProcurementModeView(true);
			rptGenBean.setCtLOAPhysicalNoView(true);
			rptGenBean.setToDateView(true);
			
			break;
		
		case ReportConst.CT_LST_OF_UPDATE_RPT_VAL:

			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtPhysicalContractNoView(true);
			rptGenBean.setCtAgreementView(true);
			rptGenBean.setCtStatusTwoView(true);
			rptGenBean.setCtLoanNoView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupThreeView(true);
			rptGenBean.setCtProcurementModeView(true);
			rptGenBean.setCtLOAPhysicalNoView(true);
			rptGenBean.setToDateView(true);
		
			break;
		
		case ReportConst.CT_LST_OF_SUSPENSION_RPT_VAL:

			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtPhysicalContractNoView(true);
			rptGenBean.setCtAgreementView(true);
			rptGenBean.setCtStatusTwoView(true);
			rptGenBean.setCtLoanNoView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupThreeView(true);
			rptGenBean.setCtProcurementModeView(true);
			rptGenBean.setCtLOAPhysicalNoView(true);
			rptGenBean.setToDateView(true);
			
			break;
			
		case ReportConst.CT_LST_OF_TERMINATION_RPT_VAL:

			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtPhysicalContractNoView(true);
			rptGenBean.setCtAgreementView(true);
			rptGenBean.setCtStatusTwoView(true);
			rptGenBean.setCtLoanNoView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupThreeView(true);
			rptGenBean.setCtProcurementModeView(true);
			rptGenBean.setCtLOAPhysicalNoView(true);
			rptGenBean.setToDateView(true);
			
			break;
			
		case ReportConst.CT_LST_OF_SUPPLIER_RPT_VAL:

			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtPhysicalContractNoView(true);
			rptGenBean.setCtAgreementView(true);
			rptGenBean.setCtStatusTwoView(true);
			rptGenBean.setCtLoanNoView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupThreeView(true);
			rptGenBean.setCtProcurementModeView(true);
			rptGenBean.setCtLOAPhysicalNoView(true);
			rptGenBean.setToDateView(true);
			
			break;
			
		case ReportConst.CT_LST_OF_AGREEMENT_RPT_VAL:

			rptGenBean.setCtCompleteView(true);
			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtPhysicalContractNoView(true);
			rptGenBean.setCtAgreementView(true);
			rptGenBean.setCtStatusTwoView(true);
			rptGenBean.setCtLoanNoView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupThreeView(true);
			rptGenBean.setCtProcurementModeView(true);
			rptGenBean.setCtLOAPhysicalNoView(true);
			rptGenBean.setToDateView(true);
		
			break;
		
		case ReportConst.CT_LST_OF_SUPPLAGREEMENT_RPT_VAL:

			rptGenBean.setCtCompleteView(true);
			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtPhysicalContractNoView(true);
			rptGenBean.setCtAgreementView(true);
			rptGenBean.setCtStatusTwoView(true);
			rptGenBean.setCtLoanNoView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupThreeView(true);
			rptGenBean.setCtProcurementModeView(true);
			rptGenBean.setCtLOAPhysicalNoView(true);
			rptGenBean.setToDateView(true);
		
			break;
		
		case ReportConst.CT_LST_OF_CIRCULAR_RPT_VAL:

			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtPhysicalContractNoView(true);
			rptGenBean.setCtAgreementView(true);
			rptGenBean.setCtStatusTwoView(true);
			rptGenBean.setCtLoanNoView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupThreeExtraView(true);
			rptGenBean.setCtProcurementModeView(true);
			rptGenBean.setCtLOAPhysicalNoView(true);
			rptGenBean.setCtCircularNoView(true);
			rptGenBean.setCtCircularNameView(true);
			rptGenBean.setCtCircularTypeView(true);
			rptGenBean.setCtContractFromDateView(true);
			rptGenBean.setCtContractEffectiveFromDateLabelView(true);
			rptGenBean.setCtCircularEffectiveFromDateView(true);
			
			break;
		
		case ReportConst.CT_LST_OF_PANEL_CIRCULAR_NO_RPT_VAL:
		
			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtStatusView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setToDateView(true);
			rptGenBean.setCtGroupOneView(true);
		
			break;
		
		case ReportConst.CT_FULFILMENT_STATUS_RPT_VAL:
			
			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtPhysicalContractNoView(true);
			rptGenBean.setCtAgreementView(true);
			rptGenBean.setCtStatusThreeView(true);
			rptGenBean.setCtLoanNoView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupFourView(true);
			rptGenBean.setCtProcurementModeView(true);
			rptGenBean.setCtLOAPhysicalNoView(true);
			rptGenBean.setCtContractFromDateView(true);
			rptGenBean.setCtContractEffectiveFromDateLabelView(true);
			
			break;
		
		case ReportConst.CT_CONTRACT_TRANSACTION_MONITORING_CC_RPT_VAL:
		
			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtPhysicalContractNoView(true);
			rptGenBean.setCtAgreementView(true);
			rptGenBean.setCtStatusThreeView(true);
			rptGenBean.setCtLoanNoView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupFourView(true);
			rptGenBean.setCtProcurementModeView(true);
			rptGenBean.setCtLOAPhysicalNoView(true);
			rptGenBean.setCtContractFromDateView(true);
			rptGenBean.setCtContractEffectiveFromDateLabelView(true);
		
			break;
		
		case ReportConst.CT_CONTRACT_UTILIZATION_MONITORING_CC_RPT_VAL:
			
			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtPhysicalContractNoView(true);
			rptGenBean.setCtAgreementView(true);
			rptGenBean.setCtStatusThreeView(true);
			rptGenBean.setCtLoanNoView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupFourView(true);
			rptGenBean.setCtProcurementModeView(true);
			rptGenBean.setCtLOAPhysicalNoView(true);
			rptGenBean.setCtContractFromDateView(true);
			rptGenBean.setCtContractEffectiveFromDateLabelView(true);
		
			break;
		
		case ReportConst.CT_STATEMENT_OF_ORDER_REPORT_CC_PANEL_RPT_VAL:
		
		case ReportConst.CT_PANEL_STATEMENT_OF_ORDER_REPORT_CC_PANEL_RPT_VAL:
			rptGenBean.setCtContractNoCPView(true);
			rptGenBean.setCtPhysicalContractNoCPView(true);
			rptGenBean.setCtAgreementExtraView(true);
			rptGenBean.setCtStatusThreeCPView(true);
			rptGenBean.setCtLoanNoCPView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupFiveView(true);
			rptGenBean.setCtProcurementModeCPView(true);
			rptGenBean.setCtLOAPhysicalNoCPView(true);
			rptGenBean.setCtSupplierNameCPView(true);
			rptGenBean.setCtContractFromDateView(true);
			rptGenBean.setCtContractOrderFromDateLabelView(true);

			break;
		
		case ReportConst.CT_LIST_OF_CONTRACT_CC_RPT_VAL:
			
		case ReportConst.CT_PANEL_LIST_OF_CONTRACT_CC_RPT_VAL:
			rptGenBean.setCtContractNoView(true);
			rptGenBean.setCtPhysicalContractNoView(true);
			rptGenBean.setCtAgreementView(true);
			rptGenBean.setCtStatusTwoView(true);
			rptGenBean.setCtLoanNoView(true);
			rptGenBean.setCtMinistryView(true);
			rptGenBean.setCtPegaWaiPengawalView(true);
			rptGenBean.setCtKumpulanPTJView(true);
			rptGenBean.setCtPtjView(true);
			rptGenBean.setCtGroupFourView(true);
			rptGenBean.setCtProcurementModeView(true);
			rptGenBean.setCtLOAPhysicalNoView(true);
			rptGenBean.setCtContractFromDateView(true);
			rptGenBean.setCtContractEffectiveFromDateLabelView(true);
		
			break;

		default:
			throw new EpException(
					CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));
		}
	}
}
