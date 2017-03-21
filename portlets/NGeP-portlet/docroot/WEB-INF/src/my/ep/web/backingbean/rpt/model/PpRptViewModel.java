package my.ep.web.backingbean.rpt.model;

import java.util.List;

import my.ep.exception.EpException;
import my.ep.web.backingbean.rpt.RptBackingBean;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.ReportConst;

public class PpRptViewModel {
	
//	protected RptBackingBean rptBackingBean;
//	protected String reportValue;
//	
//	public PpRptViewModel(RptBackingBean rptBackingBean, String reportValue){
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
//	public void enableCriteriaVariablesByReportByPP(
//			List<String> authoriztionList) {
//		    
//		rptBackingBean.setConditionGenerateButton(true);
//		
//	    switch (reportValue) {
//
//		case ReportConst.PP_PROCUREMENT_PTJ_LEVEL_RPT_VAL:
//	
//			rptBackingBean.setPpYearView(true);
//			rptBackingBean.setPpPegaWaiPengawalView(true);
//			rptBackingBean.setPpKumpulanPTJView(true);
//			rptBackingBean.setPpPTJView(true);
//			
//			checkAuthorizationByRoleCode(rptBackingBean, authoriztionList);
//			
//			break;
//			
//		case ReportConst.PP_PROCUREMENT_MINISTRY_LEVEL_RPT_VAL:
//			
//			rptBackingBean.setPpYearView(true);
//			rptBackingBean.setPpPegaWaiPengawalView(true);
//			rptBackingBean.setPpKumpulanPTJView(true);
//			rptBackingBean.setPpPTJView(true);
//			checkAuthorizationByRoleCode(rptBackingBean, authoriztionList);
//			
//			break;
//			
//		case ReportConst.PP_PROCUREMENT_BKP_LEVEL_RPT_VAL:
//			
//			rptBackingBean.setPpYearView(true);
//			rptBackingBean.setPpPegaWaiPengawalView(true);
//			rptBackingBean.setPpKumpulanPTJView(true);
//			rptBackingBean.setPpPTJView(true);
//			checkAuthorizationByRoleCode(rptBackingBean, authoriztionList);
//			
//			break;
//		
//		case ReportConst.PP_EXPENDITURE_PLAN_PTJ_LEVEL_RPT_VAL:
//		
//			rptBackingBean.setPpYearView(true);
//			rptBackingBean.setPpPegaWaiPengawalView(true);
//			rptBackingBean.setPpKumpulanPTJView(true);
//			rptBackingBean.setPpPTJView(true);
//			
//			checkAuthorizationByRoleCode(rptBackingBean, authoriztionList);
//			
//			break;
//
//		case ReportConst.PP_EXPEND_MINISTRY_BY_VOT_PTJ_LEVEL_RPT_VAL:
//		
//			rptBackingBean.setPpYearView(true);
//			rptBackingBean.setPpMinistryView(true);
//			rptBackingBean.setPpPegaWaiPengawalView(true);
//			rptBackingBean.setPpKumpulanPTJView(true);
//			rptBackingBean.setPpPTJView(true);
//			
//			break;
//			
//		case ReportConst.PP_EXPEND_MINISTRY_BY_GOSO_PTJ_LEVEL_RPT_VAL:
//			
//			rptBackingBean.setPpYearView(true);
//			rptBackingBean.setPpMinistryView(true);
//			rptBackingBean.setPpPegaWaiPengawalView(true);
//			rptBackingBean.setPpKumpulanPTJView(true);
//			rptBackingBean.setPpPTJView(true);
//			
//			break;
//			
//		case ReportConst.PP_EXPEND_MINISTRY_BY_GOSO_VOT_LEVEL_RPT_VAL:
//			
//			rptBackingBean.setPpYearView(true);
//			rptBackingBean.setPpMinistryView(true);
//			rptBackingBean.setPpPegaWaiPengawalView(true);
//			rptBackingBean.setPpKumpulanPTJView(true);
//			rptBackingBean.setPpPTJView(true);
//			
//			break;
//			
//		case ReportConst.PP_EXPENDITURE_BPK_LEVEL_RPT_VAL:
//			
//			rptBackingBean.setPpYearView(true);
//			rptBackingBean.setPpMinistryView(true);
//			rptBackingBean.setPpPegaWaiPengawalView(true);
//			rptBackingBean.setPpKumpulanPTJView(true);
//			rptBackingBean.setPpPTJView(true);
//			
//			break;
//			
//		case ReportConst.PP_EXPENDITURE_UNPLANNED_LEVEL_RPT_VAL:
//			
//			rptBackingBean.setPpYearView(true);
//			rptBackingBean.setPpMinistryView(true);
//			rptBackingBean.setPpPegaWaiPengawalView(true);
//			rptBackingBean.setPpKumpulanPTJView(true);
//			rptBackingBean.setPpPTJView(true);
//			
//			break;
//			
//		default:
//			
//			throw new EpException(
//					CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));
//		}
//	}
//
//	private void checkAuthorizationByRoleCode(RptBackingBean rptBackingBean,
//			List<String> authoriztionList) {
//
//		if (authoriztionList != null) {
//		
//			if (authoriztionList.contains(ReportConst.ROLE_CODE_BPK_OFFICER)) {
//			
//				rptBackingBean.setPpMandatoryView(true);
//				rptBackingBean.setPpMinistryView(true);
//				
//			} else if(authoriztionList.contains(ReportConst.ROLE_CODE_PLAN_APPROVER) 
//				|| authoriztionList.contains(ReportConst.ROLE_CODE_FL_APPROVER)) {				
//				
//				rptBackingBean.setPpNonMandatoryView(true);
//				rptBackingBean.setPpMinistryView(true);
//				
//			}else{
//				
//				rptBackingBean.setPpNonMandatoryView(true);
//				rptBackingBean.setPpMinistryView(true);
//				
//			}
//		}
//	}
}