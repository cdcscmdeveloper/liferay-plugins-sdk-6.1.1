package my.ep.web.constant.report;

import java.io.Serializable;

public class SmReportConst implements Serializable{
	private static final long serialVersionUID = 1L;
	
	//Param
	public static final String PARAM_DATE_TO = "PTODATE";
	public static final String PARAM_DATE_FROM = "PFROMDATE";
	public static final String PARAM_USER_ID = "PUSER";
	public static final String PARAM_REPORT_CODE = "PREPORTID";
	public static final String PARAM_KEY_REPORT = "report";
	
	public static final String PARAM_COMPANY_NAME = "PCOMPANYNAME";
	public static final String PARAM_MOF_NO = "PMOFNO";
	public static final String PARAM_ZONE = "PSUPPZONE";
	
	public static final String SM_REG_COMPANY_IN_MOF_RPT_VAL = "SM_001";
	public static final String SM_CATEGORY_STATISTIC_RPT_VAL = "SM_002";
	public static final String SM_CAT_STATIC_BY_YEAR_RPT_VAL = "SM_003";
	public static final String SM_CAT_STATIC_BY_STATE_RPT_VAL = "SM_004";
	public static final String SM_APPLICATION_VERIFICATION_RPT_VAL = "SM_005";
	public static final String SM_APPLICATION_APPROVED_RPT_VAL = "SM_006";
	public static final String SM_APPLICATION_REJECTED_RPT_VAL = "SM_007";
	public static final String SM_APPL_PEND_SITE_VISIT_RPT_VAL = "SM_008";
	public static final String SM_TOT_NUM_YEARLY_APPL_RPT_VAL = "SM_009";
	public static final String SM_LST_MONTH_WISE_RPT_VAL = "SM_010";
	public static final String SM_APPL_CLIENT_CHARTER_RPT_VAL = "SM_011";
	public static final String SM_LST_DAILY_NEW_APPL_RPT_VAL = "SM_012";
	public static final String SM_LST_COMP_ADDR_RPT_VAL = "SM_013";
	public static final String SM_SUPPL_STAT_BY_CATEGORY_RPT_VAL = "SM_014";
	public static final String SM_TOT_NUM_DAILY_APPL_RPT_VAL = "SM_015";
	public static final String SM_SUPPL_STAT_RPT_VAL = "SM_016";
	public static final String SM_BPK_USER_PROF_RPT_VAL = "SM_017";
	public static final String SM_BPK_ADMIN_PROF_RPT_VAL = "SM_018";
	public static final String SM_QUESTIONAIRE_RPT_VAL = "SM_019";
	public static final String SM_LAPORAN_TERIMAAN_HARIAN_RPT_VAL = "SM_020";
	public static final String SM_LIST_OF_PRE_EXPIRY_COMPANY_RPT_VAL = "SM_021";
	public static final String SM_REG_COMPANY_IN_MOF_DET_RPT_VAL = "SM_022";
	public static final String SM_REG_COMPANY_IN_MOF_PROJ_RPT_VAL = "SM_023";
	public static final String SM_REG_COMPANY_IN_MOF_CONT_RPT_VAL = "SM_024";
	public static final String SM_REG_COMPANY_IN_MOF_DET_CONT_RPT_VAL = "SM_025";
	public static final String SM_REG_COMPANY_IN_MOF_PROJ_CONT_RPT_VAL = "SM_026";
	public static final String SM_PAY_REGIS_FEE_RPT_VAL = "SM_027";
	public static final String SM_QUESTIONNAIRE_RESPONSE_RPT_VAL = "SM_028";
	public static final String SM_SITE_VISIT_APPL_REQUIRE_RPT_VAL = "SM_029";
	public static final String SM_SITE_VISIT_APPL_OFFICER_RPT_VAL = "SM_030";
	public static final String SM_SITE_VISIT_APPL_PERFORMED_RPT_VAL = "SM_031";
	public static final String SM_LIST_OF_APPLICATION_RPT_VAL = "SM_042";
	public static final String SM_LIST_OFFLINE_SUPP_DOC_RPT_VAL = "SM_043";
	public static final String SM_VIRTUAL_DAY_REPORT_RPT_VAL = "SM_044";
	public static final String SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL = "SM_045";
	public static final String SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL = "SM_046";
	public static final String SM_LIST_OF_DIR_OWN_CEO_COMPANY_RPT_VAL = "SM_047";
	public static final String SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL = "SM_048";
	public static final String SM_STATISTICS_COMPANYTYPE_CAP_RPT_VAL = "SM_049";
	public static final String SM_LIST_OF_PER_MORE_COMPANY_RPT_VAL = "SM_050";
	public static final String SM_LIST_OF_MOF_CANCELLATION_RPT_VAL = "SM_051";
	public static final String SM_COMPANY_ADDRESS_LIST_RPT_VAL = "SM_052";
	public static final String SM_BPK_USER_PROFILE_RPT_VAL = "SM_053";
	public static final String SM_BPK_ADMIN_PROFILE_RPT_VAL = "SM_054";
	public static final String SM_DAILY_EARNING_RECEIPT = "SM_055";
	public static final String SM_DELETE_PENDING_APPL = "SM_056";
	public static final String SM_SUPPLIER_PERFORMANCE = "SM_057";
	public static final String SM_PERSON_BRANCH_BANK = "SM_058";
	public static final String SM_THRESHOLD = "SM_059";
	public static final String SM_LIST_OF_USERS = "SM_060";
	public static final String SM_DAILY_APP_SUBMITTED = "SM_061";
	public static final String SM_THRESHOLD_VALUE = "SM_062";
	public static final String SM_LIST_OF_APPL_STATUS = "SM_063";
	public static final String SM_AGENCY_STATISTICAL = "SM_064";
	public static final String SM_LIST_OF_GST_RGSTRD_SUPPLIER = "SM_065";
	public static final String SM_COLLECTION_AND_TRANSACTION_DATE_RPT_VAL = "SM_071";
	public static final String SM_COLLECTION_PAYMENT_METHODS_RPT_VAL = "SM_072";
	public static final String SM_COLLECTION_CLASSIFICATION_CODE_RPT_VAL = "SM_073";
	public static final String SM_REVENUE_COLLECTION_RPT_VAL = "SM_074";
	public static final String SM_LIST_DAILY_RECIPT_RPT_VAL = "SM_075";
	public static final String SM_CATEGORY_OVERLAPPING_RPT_VAL = "SM_076";
	public static final String SM_CDCSS_DEPOSIT_ADJUSTMENT_RPT_VAL = "SM_077";
	public static final String SM_LIST_OF_SOFTCERT_PAYMENT_RPT_VAL = "SM_078";
	public static final String SM_REJECTED_SOFTCERT_RPT_VAL = "SM_079";
	
	public String getSmRegCompanyInMofRptVal() {
		return SM_REG_COMPANY_IN_MOF_RPT_VAL;
	}
	
	public String getSmCategoryStatisticRptVal() {
		return SM_CATEGORY_STATISTIC_RPT_VAL;
	}
	
	public String getSmCatStaticByYearRptVal() {
		return SM_CAT_STATIC_BY_YEAR_RPT_VAL;
	}
	
	public String getSmCatStaticByStateRptVal() {
		return SM_CAT_STATIC_BY_STATE_RPT_VAL;
	}
	
	public String getSmApplicationVerificationRptVal() {
		return SM_APPLICATION_VERIFICATION_RPT_VAL;
	}
	
	public String getSmApplicationApprovedRptVal() {
		return SM_APPLICATION_APPROVED_RPT_VAL;
	}
	
	public String getSmApplicationRejectedRptVal() {
		return SM_APPLICATION_REJECTED_RPT_VAL;
	}
	
	public String getSmApplPendSiteVisitRptVal() {
		return SM_APPL_PEND_SITE_VISIT_RPT_VAL;
	}
	
	public String getSmTotNumYearlyApplRptVal() {
		return SM_TOT_NUM_YEARLY_APPL_RPT_VAL;
	}
	
	public String getSmLstMonthWiseRptVal() {
		return SM_LST_MONTH_WISE_RPT_VAL;
	}
	
	public String getSmApplClientCharterRptVal() {
		return SM_APPL_CLIENT_CHARTER_RPT_VAL;
	}
	
	public String getSmLstDailyNewApplRptVal() {
		return SM_LST_DAILY_NEW_APPL_RPT_VAL;
	}
	
	public String getSmLstCompAddrRptVal() {
		return SM_LST_COMP_ADDR_RPT_VAL;
	}
	
	public String getSmSupplStatByCategoryRptVal() {
		return SM_SUPPL_STAT_BY_CATEGORY_RPT_VAL;
	}
	
	public String getSmTotNumDailyApplRptVal() {
		return SM_TOT_NUM_DAILY_APPL_RPT_VAL;
	}
	
	public String getSmSupplStatRptVal() {
		return SM_SUPPL_STAT_RPT_VAL;
	}
	
	public String getSmBpkUserProfRptVal() {
		return SM_BPK_USER_PROF_RPT_VAL;
	}
	
	public String getSmBpkAdminProfRptVal() {
		return SM_BPK_ADMIN_PROF_RPT_VAL;
	}
	
	public String getSmQuestionaireRptVal() {
		return SM_QUESTIONAIRE_RPT_VAL;
	}
	
	public String getSmLaporanTerimaanHarianRptVal() {
		return SM_LAPORAN_TERIMAAN_HARIAN_RPT_VAL;
	}
	
	public String getSmListOfPreExpiryCompanyRptVal() {
		return SM_LIST_OF_PRE_EXPIRY_COMPANY_RPT_VAL;
	}
	
	public String getSmRegCompanyInMofDetRptVal() {
		return SM_REG_COMPANY_IN_MOF_DET_RPT_VAL;
	}
	
	public String getSmRegCompanyInMofProjRptVal() {
		return SM_REG_COMPANY_IN_MOF_PROJ_RPT_VAL;
	}
	
	public String getSmRegCompanyInMofContRptVal() {
		return SM_REG_COMPANY_IN_MOF_CONT_RPT_VAL;
	}
	
	public String getSmRegCompanyInMofDetContRptVal() {
		return SM_REG_COMPANY_IN_MOF_DET_CONT_RPT_VAL;
	}
	
	public String getSmRegCompanyInMofProjContRptVal() {
		return SM_REG_COMPANY_IN_MOF_PROJ_CONT_RPT_VAL;
	}
	
	public String getSmPayRegisFeeRptVal() {
		return SM_PAY_REGIS_FEE_RPT_VAL;
	}
	
	public String getSmQuestionnaireResponseRptVal() {
		return SM_QUESTIONNAIRE_RESPONSE_RPT_VAL;
	}
	
	public String getSmSiteVisitApplRequireRptVal() {
		return SM_SITE_VISIT_APPL_REQUIRE_RPT_VAL;
	}
	
	public String getSmSiteVisitApplOfficerRptVal() {
		return SM_SITE_VISIT_APPL_OFFICER_RPT_VAL;
	}
	
	public String getSmSiteVisitApplPerformedRptVal() {
		return SM_SITE_VISIT_APPL_PERFORMED_RPT_VAL;
	}
	
	public String getSmListOfApplicationRptVal() {
		return SM_LIST_OF_APPLICATION_RPT_VAL;
	}
	
	public String getSmListOfflineSuppDocRptVal() {
		return SM_LIST_OFFLINE_SUPP_DOC_RPT_VAL;
	}
	
	public String getSmVirtualDayReportRptVal() {
		return SM_VIRTUAL_DAY_REPORT_RPT_VAL;
	}
	
	public String getSmListOfCompanySuptypeRptVal() {
		return SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL;
	}
	
	public String getSmListOfCompDirOwnCeoRptVal() {
		return SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL;
	}
	
	public String getSmListOfDirOwnCeoCompanyRptVal() {
		return SM_LIST_OF_DIR_OWN_CEO_COMPANY_RPT_VAL;
	}
	
	public String getSmListOfCompSortAlphaRptVal() {
		return SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL;
	}
	
	public String getSmStatisticsCompanytypeCapRptVal() {
		return SM_STATISTICS_COMPANYTYPE_CAP_RPT_VAL;
	}
	
	public String getSmListOfPerMoreCompanyRptVal() {
		return SM_LIST_OF_PER_MORE_COMPANY_RPT_VAL;
	}
	
	public String getSmListOfMofCancellationRptVal() {
		return SM_LIST_OF_MOF_CANCELLATION_RPT_VAL;
	}
	
	public String getSmCompanyAddressListRptVal() {
		return SM_COMPANY_ADDRESS_LIST_RPT_VAL;
	}
	
	public String getSmBpkUserProfileRptVal() {
		return SM_BPK_USER_PROFILE_RPT_VAL;
	}
	
	public String getSmBpkAdminProfileRptVal() {
		return SM_BPK_ADMIN_PROFILE_RPT_VAL;
	}
	
	public String getSmDailyEarningReceipt() {
		return SM_DAILY_EARNING_RECEIPT;
	}
	
	public String getSmDeletePendingAppl() {
		return SM_DELETE_PENDING_APPL;
	}
	
	public String getSmSupplierPerformance() {
		return SM_SUPPLIER_PERFORMANCE;
	}
	
	public String getSmPersonBranchBank() {
		return SM_PERSON_BRANCH_BANK;
	}
	
	public String getSmThreshold() {
		return SM_THRESHOLD;
	}
	
	public String getSmListOfUsers() {
		return SM_LIST_OF_USERS;
	}
	
	public String getSmDailyAppSubmitted() {
		return SM_DAILY_APP_SUBMITTED;
	}
	
	public String getSmThresholdValue() {
		return SM_THRESHOLD_VALUE;
	}
	
	public String getSmListOfApplStatus() {
		return SM_LIST_OF_APPL_STATUS;
	}
	
	public String getSmAgencyStatistical() {
		return SM_AGENCY_STATISTICAL;
	}
	
	public String getSmListOfGstRgstrdSupplier() {
		return SM_LIST_OF_GST_RGSTRD_SUPPLIER;
	}
	
	public String getSmCollectionAndTransactionDateRptVal() {
		return SM_COLLECTION_AND_TRANSACTION_DATE_RPT_VAL;
	}
	
	public String getSmCollectionPaymentMethodsRptVal() {
		return SM_COLLECTION_PAYMENT_METHODS_RPT_VAL;
	}
	
	public String getSmCollectionClassificationCodeRptVal() {
		return SM_COLLECTION_CLASSIFICATION_CODE_RPT_VAL;
	}
	
	public String getSmRevenueCollectionRptVal() {
		return SM_REVENUE_COLLECTION_RPT_VAL;
	}
	
	public String getSmListDailyReciptRptVal() {
		return SM_LIST_DAILY_RECIPT_RPT_VAL;
	}
	
	public String getSmCategoryOverlappingRptVal() {
		return SM_CATEGORY_OVERLAPPING_RPT_VAL;
	}
	
	public String getSmCdcssDepositAdjustmentRptVal() {
		return SM_CDCSS_DEPOSIT_ADJUSTMENT_RPT_VAL;
	}
	
	public String getSmListOfSoftcertPaymentRptVal() {
		return SM_LIST_OF_SOFTCERT_PAYMENT_RPT_VAL;
	}
	
	public String getSmRejectedSoftcertRptVal() {
		return SM_REJECTED_SOFTCERT_RPT_VAL;
	}
}
