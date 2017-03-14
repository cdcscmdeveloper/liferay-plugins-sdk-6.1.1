package my.ep.web.backingbean.rpt.model;

import java.util.List;

import my.ep.exception.EpException;
import my.ep.web.backingbean.rpt.RptBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.ReportConst;

public class PMRptViewModel{

	protected RptBackingBean rptBackingBean;
	protected String reportValue;
	
	public PMRptViewModel(RptBackingBean rptBackingBean, String reportValue){
		this.rptBackingBean = rptBackingBean;
		this.reportValue = reportValue;
	}
	
	public RptBackingBean getBackingBean(){
		return rptBackingBean;
	}
	
	public String getReportValue(){
		return reportValue;
	}
	
	public void enableCriteriaVariablesByReportByPM(List<String> authoriztionList) {
	
		rptBackingBean.setGenerateButton(true);
		
		switch (reportValue) {

		case ReportConst.PM_LIST_OF_FINACIAL_INSTITUTIONS_RPT_VAL:

			rptBackingBean.setGenerateButton(false);
			rptBackingBean.setConditionGenerateButton(true);
			rptBackingBean.setPmServiceTypeView(true);
			rptBackingBean.setPmStatusView(true);
			rptBackingBean.setPmFinancialOrgNameView(true);
			rptBackingBean.setPmBusinessRegNoView(true);
			
			break;

		case ReportConst.PM_LIST_OF_ORGANIZATION_RPT_VAL:
			
			rptBackingBean.setGenerateButton(false);
			rptBackingBean.setConditionGenerateButton(true);
			rptBackingBean.setPmMandatoryOrgCategoryTypeView(true);
			rptBackingBean.setPmMandatoryOrgTypeView(true);
			rptBackingBean.setPmNonMandatoryOrgNameView(true);
			rptBackingBean.setPmOrgCodeView(true);
			rptBackingBean.setPmStatusView(true);
			
			break;

		case ReportConst.PM_LIST_OF_PROFILES_RPT_VAL:
			
			rptBackingBean.setGenerateButton(false);
			rptBackingBean.setConditionGenerateButton(true);
			rptBackingBean.setPmMandatoryOrgTypeForLOPView(true);
			rptBackingBean.setPmMandatoryOrgNameForLOPView(true);
			rptBackingBean.setPmListOfOrgStatusView(true); 
			rptBackingBean.setPmIdentificationNoView(true);
			rptBackingBean.setPmUserNameView(true);
			rptBackingBean.setPmRoleCodeView(true);
			
			break;

		case ReportConst.PM_LIST_OF_GOVERNMENT_SELLERS_RPT_VAL:
			
			rptBackingBean.setGenerateButton(false);
			rptBackingBean.setConditionGenerateButton(true);
			rptBackingBean.setPmNonMandatoryOrgNameView(true);
			rptBackingBean.setPmOrgCodeView(true);
			rptBackingBean.setPmStatusView(true);
			
			break;

		case ReportConst.PM_LIST_OF_EXEMPTED_ITEMS_BY_SUPPLIER_RPT_VAL:
			
			rptBackingBean.setPmExemptedCompanyNameView(true);
			rptBackingBean.setPmExemptedBusinessRegNoView(true);
			rptBackingBean.setPmExemptedEpNoView(true);
			rptBackingBean.setPmExemptedItemNameView(true);
			rptBackingBean.setPmExemptedItemCodeView(true);
			rptBackingBean.setPmNonMandatorySupDateView(true);
			rptBackingBean.setPmFromDateView(true);
			
			break;

		case ReportConst.PM_LIST_OF_EXEMPTED_ITEMS_RPT_VAL:
			
			rptBackingBean.setPmExemptedItemView(true);
			rptBackingBean.setPmItemNameView(true);
			rptBackingBean.setPmItemCodeView(true);
			rptBackingBean.setPmNonMandatoryDateView(true);
			rptBackingBean.setPmFromDateView(true);
			
			break;

		case ReportConst.PM_LIST_OF_EXEMPTED_CONTRACTED_ITEMS_RPT_VAL:
			
			rptBackingBean.setPmExemptedContractedItemView(true);	
			rptBackingBean.setPmContractItemCodeView(true);
			rptBackingBean.setPmContractItemNameView(true);
			rptBackingBean.setPmNonMandatoryDateView(true);
			rptBackingBean.setPmFromDateView(true);
			rptBackingBean.setPmContractNoView(true);
			rptBackingBean.setPmPhysicalContractNoView(true);
			rptBackingBean.setPmLOANoView(true);
			rptBackingBean.setPmPhysicalLOANoView(true);
			
			break;
			
		case ReportConst.PM_BPK_USER_PROFILE_RPT_VAL:
			
			rptBackingBean.setPmMandatoryZoneViewBPK(true);
			
			break;
			
		case ReportConst.PM_BPK_ADMIN_PROFILE_RPT_VAL:
			
			rptBackingBean.setPmMandatoryZoneView(true);
			rptBackingBean.setPmProfileStatusView(true);
			
			break;

		default:
			
			throw new EpException(
					CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));
		}
	}
}