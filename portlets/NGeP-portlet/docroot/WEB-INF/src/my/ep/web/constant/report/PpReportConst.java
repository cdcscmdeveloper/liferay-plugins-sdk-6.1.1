package my.ep.web.constant.report;

import java.io.Serializable;

public class PpReportConst implements Serializable {

	private static final long serialVersionUID = 1L;

	// PP
	public static final String PP_PROCUREMENT_SUMM_RPT_VAL = "PP_001";
	public static final String PP_PROCUREMENT_DETAIL_RPT_VAL = "PP_002";
	public static final String PP_EXPENDITURE_SUMM_RPT_VAL = "PP_003";
	public static final String PP_EXPENDITURE_DETAIL_RPT_VAL = "PP_004";
	public static final String PP_EXPENDITURE_PERFORMANCE_RPT_VAL = "PP_005";		//[#24976]
	public static final String PP_BUDGET_ALLOCATION_AMENDMENT_RPT_VAL = "PP_006";	//[#24974]
	public static final String PP_OVERALL_PROCUREMENT_RPT_VAL = "PP_009";	//[#28218]
	public static final String [] PP_REPORTS_MINISTRY_IS_MANDATORY = {PP_PROCUREMENT_SUMM_RPT_VAL,PP_PROCUREMENT_DETAIL_RPT_VAL,PP_EXPENDITURE_SUMM_RPT_VAL,PP_EXPENDITURE_DETAIL_RPT_VAL,PP_OVERALL_PROCUREMENT_RPT_VAL};	//[#27197],[#28218]
	public static final String PP_TOTAL_APPROVED_PP_PE_MINISTRY_RPT_VAL = "PP_008";	//[#29484]
	
	public static final String PARAM_PP_MINISTRY = "PMINIS";
	public static final String PARAM_PP_PEGAWAI_PEGAWAL = "PFPP";
	public static final String PARAM_PP_KUMPULAN_PTJ = "PFJA";
	public static final String PARAM_PP_PTJ = "PPTJ";
	public static final String PARAM_PP_YEAR = "PYEAR";
	public static final String PARAM_PP_STATUS = "PSTAT";
	public static final String PARAM_PP_METHOD = "PMET";
	public static final String PARAM_PP_CATEGORY = "PCATS";
	//start [#28218]
	public static final String PARAM_PP_SUBPLAN_PROGRESS = "PSPSTAT";
	public static final String PARAM_PP_INVITATION_DATE = "PINVDT";
	public static final String PARAM_PP_CONTRACT_TYPE = "PCONTYP";

	// Sub Plan Progress Status - values are the report-side codes, not a DB parameter id.
	public static final String SUBPLAN_PROGRESS_ALL = "ALL";
	public static final String SUBPLAN_PROGRESS_NEW = "NEW";
	public static final String SUBPLAN_PROGRESS_IN_PROGRESS = "INPROG";
	public static final String SUBPLAN_PROGRESS_COMPLETED = "COMP";

	// Contract Type
	public static final String CONTRACT_TYPE_ALL = "ALL";
	public static final String CONTRACT_TYPE_MINISTRY = "MIN";
	public static final String CONTRACT_TYPE_CENTRAL = "CEN";
	//end [#28218]
	
	
	public PpReportConst() {
	}

	/**
	 * @return the ppProcurementSummRptVal
	 */
	public String getPpProcurementSummRptVal() {
		return PP_PROCUREMENT_SUMM_RPT_VAL;
	}

	/**
	 * @return the ppProcurementDetailRptVal
	 */
	public String getPpProcurementDetailRptVal() {
		return PP_PROCUREMENT_DETAIL_RPT_VAL;
	}

	/**
	 * @return the ppExpenditureSummRptVal
	 */
	public String getPpExpenditureSummRptVal() {
		return PP_EXPENDITURE_SUMM_RPT_VAL;
	}

	/**
	 * @return the ppExpenditureDetailRptVal
	 */
	public String getPpExpenditureDetailRptVal() {
		return PP_EXPENDITURE_DETAIL_RPT_VAL;
	}
	//[#24976]
	/**
	 * @return the ppProcurementPerformanceRptVal
	 */
	public String getPpProcurementPerformanceRptVal() {
		return PP_EXPENDITURE_PERFORMANCE_RPT_VAL;
	}
	//[#24974]
	/**
	 * @return the ppBudgetAllocationAmendmentRptVal
	 */
	public String getPpBudgetAllocationAmendmentRptVal() {
		return PP_BUDGET_ALLOCATION_AMENDMENT_RPT_VAL;
	}
	
	//start [#29484]
		/**
		 * @return the ppTotalApprovedPpPeMinistry
		 */
	public String getPpTotalApprovedPpPeMinistryRptVal() {
		return PP_TOTAL_APPROVED_PP_PE_MINISTRY_RPT_VAL;
	}
	//end [#29484]

	//start [#28218]
	/**
	 * @return the ppOverallProcurementRptVal
	 */
	public String getPpOverallProcurementRptVal() {
		return PP_OVERALL_PROCUREMENT_RPT_VAL;
	}
	//end [#28218]
	
}
