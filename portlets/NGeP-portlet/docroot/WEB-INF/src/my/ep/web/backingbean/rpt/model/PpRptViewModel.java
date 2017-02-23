package my.ep.web.backingbean.rpt.model;

import java.util.List;

import my.ep.exception.EpException;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.ReportConst;

public class PpRptViewModel {
	
	protected ReportsGenerateBean rptGenBean;
	protected String reportValue;
	
	public PpRptViewModel(ReportsGenerateBean rptGenBean, String reportValue){
		this.rptGenBean = rptGenBean;
		this.reportValue = reportValue;
	}
	
	public ReportsGenerateBean getRptGenBean(){
		return rptGenBean;
	}
	
	public String getReportValue(){
		return reportValue;
	}
	
	public void enableCriteriaVariablesByReportByPP(
			List<String> authoriztionList) {
		    
		rptGenBean.setConditionGenerateButton(true);
		
	    switch (reportValue) {

		case ReportConst.PP_PROCUREMENT_PTJ_LEVEL_RPT_VAL:
	
			rptGenBean.setPpYearView(true);
			rptGenBean.setPpPegaWaiPengawalView(true);
			rptGenBean.setPpKumpulanPTJView(true);
			rptGenBean.setPpPTJView(true);
			
			checkAuthorizationByRoleCode(rptGenBean, authoriztionList);
			
			break;
			
		case ReportConst.PP_PROCUREMENT_MINISTRY_LEVEL_RPT_VAL:
			
			rptGenBean.setPpYearView(true);
			rptGenBean.setPpPegaWaiPengawalView(true);
			rptGenBean.setPpKumpulanPTJView(true);
			rptGenBean.setPpPTJView(true);
			checkAuthorizationByRoleCode(rptGenBean, authoriztionList);
			
			break;
			
		case ReportConst.PP_PROCUREMENT_BKP_LEVEL_RPT_VAL:
			
			rptGenBean.setPpYearView(true);
			rptGenBean.setPpPegaWaiPengawalView(true);
			rptGenBean.setPpKumpulanPTJView(true);
			rptGenBean.setPpPTJView(true);
			checkAuthorizationByRoleCode(rptGenBean, authoriztionList);
			
			break;
		
		case ReportConst.PP_EXPENDITURE_PLAN_PTJ_LEVEL_RPT_VAL:
		
			rptGenBean.setPpYearView(true);
			rptGenBean.setPpPegaWaiPengawalView(true);
			rptGenBean.setPpKumpulanPTJView(true);
			rptGenBean.setPpPTJView(true);
			
			checkAuthorizationByRoleCode(rptGenBean, authoriztionList);
			
			break;

		case ReportConst.PP_EXPEND_MINISTRY_BY_VOT_PTJ_LEVEL_RPT_VAL:
		
			rptGenBean.setPpYearView(true);
			rptGenBean.setPpMinistryView(true);
			rptGenBean.setPpPegaWaiPengawalView(true);
			rptGenBean.setPpKumpulanPTJView(true);
			rptGenBean.setPpPTJView(true);
			
			break;
			
		case ReportConst.PP_EXPEND_MINISTRY_BY_GOSO_PTJ_LEVEL_RPT_VAL:
			
			rptGenBean.setPpYearView(true);
			rptGenBean.setPpMinistryView(true);
			rptGenBean.setPpPegaWaiPengawalView(true);
			rptGenBean.setPpKumpulanPTJView(true);
			rptGenBean.setPpPTJView(true);
			
			break;
			
		case ReportConst.PP_EXPEND_MINISTRY_BY_GOSO_VOT_LEVEL_RPT_VAL:
			
			rptGenBean.setPpYearView(true);
			rptGenBean.setPpMinistryView(true);
			rptGenBean.setPpPegaWaiPengawalView(true);
			rptGenBean.setPpKumpulanPTJView(true);
			rptGenBean.setPpPTJView(true);
			
			break;
			
		case ReportConst.PP_EXPENDITURE_BPK_LEVEL_RPT_VAL:
			
			rptGenBean.setPpYearView(true);
			rptGenBean.setPpMinistryView(true);
			rptGenBean.setPpPegaWaiPengawalView(true);
			rptGenBean.setPpKumpulanPTJView(true);
			rptGenBean.setPpPTJView(true);
			
			break;
			
		case ReportConst.PP_EXPENDITURE_UNPLANNED_LEVEL_RPT_VAL:
			
			rptGenBean.setPpYearView(true);
			rptGenBean.setPpMinistryView(true);
			rptGenBean.setPpPegaWaiPengawalView(true);
			rptGenBean.setPpKumpulanPTJView(true);
			rptGenBean.setPpPTJView(true);
			
			break;
			
		default:
			
			throw new EpException(
					CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));
		}
	}

	private void checkAuthorizationByRoleCode(ReportsGenerateBean rptGenBean,
			List<String> authoriztionList) {

		if (authoriztionList != null) {
		
			if (authoriztionList.contains(ReportConst.ROLE_CODE_BPK_OFFICER)) {
			
				rptGenBean.setPpMandatoryView(true);
				rptGenBean.setPpMinistryView(true);
				
			} else if(authoriztionList.contains(ReportConst.ROLE_CODE_PLAN_APPROVER) 
				|| authoriztionList.contains(ReportConst.ROLE_CODE_FL_APPROVER)) {				
				
				rptGenBean.setPpNonMandatoryView(true);
				rptGenBean.setPpMinistryView(true);
				
			}else{
				
				rptGenBean.setPpNonMandatoryView(true);
				rptGenBean.setPpMinistryView(true);
				
			}
		}
	}
}