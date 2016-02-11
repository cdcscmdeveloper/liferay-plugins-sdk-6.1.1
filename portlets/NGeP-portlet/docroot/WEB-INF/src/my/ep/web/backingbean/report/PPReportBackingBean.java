/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.report;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import my.ep.exception.EpException;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;

// TODO: Auto-generated Javadoc

/**
 * The Class PPReportBackingBean.
 * 
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name = "ppReportBackingBean")
@ViewScoped
public class PPReportBackingBean extends
		SourcingBaseBackingBean<ReportMangedBean> {

	/**
	 * enableCriteriaVariablesByReportByPP - Enable criteria variables by report
	 * of PROCUREMENT PLAN.
	 * 
	 * @param rptGenBean
	 *            the rpt gen bean
	 * @param authoriztionList
	 *            the authoriztion list
	 */
	public void enableCriteriaVariablesByReportByPP(
			ReportsGenerateBean rptGenBean, List<String> authoriztionList) {
		switch (getManagedBean().getReportValue().trim()) {

		case ReportConst.PP_PROCUREMENT_PTJ_LEVEL_RPT_VAL:
			rptGenBean.setPpYearView(true);
			rptGenBean.setPpPegaWaiPengawalView(true);
			rptGenBean.setPpKumpulanPTJView(true);
			rptGenBean.setPpPTJView(true);
			// rptGenBean.setPpMinistryView(true);
			checkAuthorizationByRoleCode(rptGenBean, authoriztionList);
			break;
		case ReportConst.PP_PROCUREMENT_MINISTRY_LEVEL_RPT_VAL:
			rptGenBean.setPpYearView(true);
			// rptGenBean.setPpMinistryView(true);
			rptGenBean.setPpPegaWaiPengawalView(true);
			rptGenBean.setPpKumpulanPTJView(true);
			rptGenBean.setPpPTJView(true);
			checkAuthorizationByRoleCode(rptGenBean, authoriztionList);
			break;
		case ReportConst.PP_PROCUREMENT_BKP_LEVEL_RPT_VAL:
			rptGenBean.setPpYearView(true);
			// rptGenBean.setPpMinistryView(true);
			rptGenBean.setPpPegaWaiPengawalView(true);
			rptGenBean.setPpKumpulanPTJView(true);
			rptGenBean.setPpPTJView(true);
			checkAuthorizationByRoleCode(rptGenBean, authoriztionList);
			break;
		case ReportConst.PP_EXPENDITURE_PLAN_PTJ_LEVEL_RPT_VAL:
			rptGenBean.setPpYearView(true);
			// rptGenBean.setPpMinistryView(true);
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
			// break;
		}
	}

	/**
	 * Check authorization by role code.
	 * 
	 * @param rptGenBean
	 *            the rpt gen bean
	 * @param authoriztionList
	 *            the authoriztion list
	 */
	private void checkAuthorizationByRoleCode(ReportsGenerateBean rptGenBean,
			List<String> authoriztionList) {
		if (authoriztionList != null) {
			if (authoriztionList.contains(ReportConst.ROLE_CODE_BPK_OFFICER)) {
				rptGenBean.setPpMandatoryView(true);
				rptGenBean.setPpMinistryView(true);
			} else {
				rptGenBean.setPpNonMandatoryView(true);
				rptGenBean.setPpMinistryView(true);
			}
		}
	}
}
