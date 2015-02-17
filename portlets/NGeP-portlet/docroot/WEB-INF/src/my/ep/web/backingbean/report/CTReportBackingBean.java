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
@ManagedBean(name="ctReportBackingBean")
@ViewScoped
public class CTReportBackingBean  extends SourcingBaseBackingBean<ReportMangedBean>{
	
	/*
	 *Enable CT module criteria by report name 
	 */
	public void enableCriteriaVariablesByReportByCT(ReportsGenerateBean rptGenBean, List<String> authoriztionList){
		
		switch (getManagedBean().getReportValue()) {		
	        case ReportConst.CT_LST_OF_CONTRACT_USERS_ALL_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setCtStatusView(true);
	        	rptGenBean.setCtLoanNoView(true);
	        	rptGenBean.setCtMinistryView(true);
	        	rptGenBean.setCtPtjView(true);
	        	rptGenBean.setCtGroupOneView(true);
	        	rptGenBean.setCtProcurementModeView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
	        /*case ReportConst.CT_LST_OF_CONTRACT_USERS_CONTRCT_NO_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;*/
	        case ReportConst.CT_LST_OF_CONTRACT_ALL_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setCtStatusView(true);
	        	rptGenBean.setCtLoanNoView(true);
	        	rptGenBean.setCtMinistryView(true);
	        	rptGenBean.setCtPtjView(true);
	        	rptGenBean.setCtProcurementModeView(true);
	        	rptGenBean.setCtGroupTwoView(true);	        	
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
	        /*case ReportConst.CT_LST_OF_CONTRACT_ACTIVE_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
	        case ReportConst.CT_LST_OF_CONTRACT_INACTIVE_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;*/
	        case ReportConst.CT_CONTRACT_MONITORING_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setCtAgreementView(true);
	        	rptGenBean.setCtContrMontrStatusView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
	        case ReportConst.CT_LST_OF_ITEM_PRODUCT_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setCtStatusView(true);
	        	rptGenBean.setCtProductServiceView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
	        /*case ReportConst.CT_LST_OF_ITEM_SERVICE_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setCtStatusView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;*/
	        case ReportConst.CT_LST_OF_AMENDMENT_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setCtStatusView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
	        case ReportConst.CT_LST_OF_UPDATE_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setCtStatusView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
	        case ReportConst.CT_LST_OF_SUSPENSION_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setCtStatusView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
	        case ReportConst.CT_LST_OF_TERMINATION_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setCtStatusView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
	        case ReportConst.CT_LST_OF_SUPPLIER_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setCtStatusView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
	        case ReportConst.CT_LST_OF_AGREEMENT_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setCtStatusView(true);
	        	rptGenBean.setCtCompleteView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
	        case ReportConst.CT_LST_OF_SUPPLAGREEMENT_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setCtStatusView(true);
	        	rptGenBean.setCtCompleteView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
	        case ReportConst.CT_LST_OF_CIRCULAR_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setCtStatusView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
	        case ReportConst.CT_LST_OF_PANEL_CIRCULAR_NO_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setCtStatusView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
	        case ReportConst.CT_LST_OF_PANEL_ITEM_NAME_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setCtStatusView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
	        case ReportConst.CT_LST_OF_PANEL_COVERAGE_RPT_VAL:
	        	rptGenBean.setCtContractNoView(true);
	        	rptGenBean.setCtStatusView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
	        default: 
	        	throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));	           
		}		
	}	
}
