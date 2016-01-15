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
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;

// TODO: Auto-generated Javadoc

/**
 * The Class PMReportBackingBean.
 * 
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name = "pmReportBackingBean")
@ViewScoped
public class PMReportBackingBean extends
		SourcingBaseBackingBean<ReportMangedBean> {

	/**
	 * enableCriteriaVariablesByReportByPM - Enable criteria variables by report
	 * of PROFILE_MANAGEMENT.
	 * 
	 * @param rptGenBean
	 *            the rpt gen bean
	 * @param authoriztionList
	 *            the authoriztion list
	 */
	public void enableCriteriaVariablesByReportByPM(
			ReportsGenerateBean rptGenBean, List<String> authoriztionList) {
		switch (getManagedBean().getReportValue().trim()) {

		case ReportConst.PM_LIST_OF_FINACIAL_INSTITUTIONS_RPT_VAL:
			rptGenBean.setPmServiceTypeView(true);
			// rptGenBean.setExtraTwoColumnView(true);
			rptGenBean.setPmStatusView(true);
			rptGenBean.setPmFinancialOrgNameView(true);
			rptGenBean.setPmBusinessRegNoView(true);
			break;

		case ReportConst.PM_LIST_OF_ORGANIZATION_RPT_VAL:
			rptGenBean.setPmMandatoryOrgCategoryTypeView(true);
			rptGenBean.setPmMandatoryOrgTypeView(true);
			rptGenBean.setPmNonMandatoryOrgNameView(true);
			rptGenBean.setPmOrgCodeView(true);
			rptGenBean.setPmStatusView(true);
			break;

		case ReportConst.PM_LIST_OF_PROFILES_RPT_VAL:
			rptGenBean.setPmMandatoryOrgTypeView(true);
			rptGenBean.setPmMandatoryOrgNameView(true);
			rptGenBean.setPmStatusView(true);
			rptGenBean.setPmIdentificationNoView(true);
			rptGenBean.setPmUserNameView(true);
			rptGenBean.setPmRoleCodeView(true);
			break;

		case ReportConst.PM_LIST_OF_GOVERNMENT_SELLERS_RPT_VAL:
			rptGenBean.setPmNonMandatoryOrgNameView(true);
			rptGenBean.setPmOrgCodeView(true);
			rptGenBean.setPmStatusView(true);
			break;

		case ReportConst.PM_LIST_OF_EXEMPTED_ITEMS_BY_SUPPLIER_RPT_VAL:
			rptGenBean.setPmExemptedCompanyNameView(true);
			rptGenBean.setPmExemptedBusinessRegNoView(true);
			rptGenBean.setPmExemptedEpNoView(true);
			rptGenBean.setPmExemptedItemNameView(true);
			rptGenBean.setPmExemptedItemCodeView(true);
			rptGenBean.setPmNonMandatorySupDateView(true);
			rptGenBean.setPmFromDateView(true);
			break;

		case ReportConst.PM_LIST_OF_EXEMPTED_ITEMS_RPT_VAL:
			rptGenBean.setPmItemNameView(true);
			rptGenBean.setPmItmeCodeView(true);
			rptGenBean.setPmNonMandatoryDateView(true);
			rptGenBean.setPmFromDateView(true);
			break;

		case ReportConst.PM_LIST_OF_EXEMPTED_CONTRACTED_ITEMS_RPT_VAL:
			rptGenBean.setPmItemNameView(true);
			rptGenBean.setPmItmeCodeView(true);
			rptGenBean.setPmNonMandatoryDateView(true);
			rptGenBean.setPmFromDateView(true);
			rptGenBean.setPmContractNoView(true);
			rptGenBean.setPmPhysicalContractNoView(true);
			rptGenBean.setPmLOANoView(true);
			rptGenBean.setPmPhysicalLOANoView(true);
			break;
		case ReportConst.PM_BPK_USER_PROFILE_RPT_VAL:
			rptGenBean.setPmMandatoryZoneViewBPK(true);
			break;
		case ReportConst.PM_BPK_ADMIN_PROFILE_RPT_VAL:
			rptGenBean.setPmMandatoryZoneView(true);
			rptGenBean.setPmProfileStatusView(true);
			break;

		default:
			throw new EpException(
					CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));
			// break;
		}
	}
}
