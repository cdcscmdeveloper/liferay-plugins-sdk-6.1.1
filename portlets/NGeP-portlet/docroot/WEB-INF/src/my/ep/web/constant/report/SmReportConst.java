package my.ep.web.constant.report;

import java.io.Serializable;

public class SmReportConst implements Serializable {
	private static final long serialVersionUID = 1L;

	// Param 

	public static final String PARAM_DATE_TO = "PTODATE";
	public static final String PARAM_DATE_FROM = "PFROMDATE";
	public static final String PARAM_DATE_EXP_TO = "PEXTODATE";
	public static final String PARAM_DATE_EXP_FROM = "PEXFROMDATE";
	public static final String PARAM_USER_ID = "PUSER";
	public static final String PARAM_REPORT_CODE = "PREPORTID";
	public static final String PARAM_KEY_REPORT = "report";
	public static final String PARAM_SUPPLIER_ID = "PSUPPID";
	public static final String PARAM_SUPPLIER_TYPE = "PSUPPTYPE";
	public static final String PARAM_REG_NO = "PREGNO";
	public static final String PARAM_COMPANY_NAME = "PCOMPANYNAME";
	public static final String PARAM_MOF_NO = "PMOFNO";
	public static final String PARAM_ZONE = "PSUPPZONE";
	public static final String PARAM_REG_TYPE = "PREGTYPE";
	public static final String PARAM_GST_REG_NO = "PGSTREGNO";
	public static final String PARAM_GST_STATUS = "PGSTSTAT";
	public static final String PARAM_APPL_NO = "PAPPLNO";
	public static final String PARAM_APPL_TYPE = "PAPPLTYPE";
	public static final String PARAM_STATE = "PSTATEID";
	public static final String PARAM_APPL_STATUS = "PAPPLSTATUS";
	public static final String PARAM_YEAR = "PYEAR";
	public static final String PARAM_PAYMENT_TYPE = "PPAYTYPE";
	public static final String PARAM_PAYMENT_TYPE1 = "PPAYTYPE1";
	public static final String PARAM_PAYMENT_TYPE2 = "PPAYTYPE2";
	public static final String PARAM_PAYMENT_TYPE3 = "PPAYTYPE3";
	public static final String PARAM_FROM_YEAR = "PFROMYEAR";
	public static final String PARAM_TO_YEAR = "PTOYEAR";
	public static final String PARAM_YEAR_FROM = "PYEARFROM";
	public static final String PARAM_YEAR_TO = "PYEARTO";
	public static final String PARAM_SUPP_DOC_MODE = "PSUPPDOCMODE";
	public static final String PARAM_SUPP_DOC_STATUS = "PSUPPDOC";
	public static final String PARAM_DISTRICT = "PDISTRICTID";
	public static final String PARAM_ROLE = "PPERSONNELROLE";
	public static final String PARAM_MOF_REG_NO_FROM = "PFROMMOFNO";
	public static final String PARAM_MOF_REG_NO_TO = "PTOMOFNO";
	public static final String PARAM_EP_NO = "PEPNO";
	public static final String PARAM_RECEIPT_NO = "PRECEIPTNO";
	public static final String PARAM_REPORT_TYPE = "PREPORTTYPE";
	public static final String PARAM_DOCUMENT_TYPE = "PDOCUMENTTYPE";
	public static final String PARAM_RPT_TYPE = "PTYPE";
	public static final String PARAM_CODE = "PKOD";
	public static final String PARAM_USER_NAME = "PUSERNAME";
	public static final String PARAM_IDENT_NO = "PIDENNO";
	public static final String PARAM_DATE_TYPE = "PDATE";
	public static final String PARAM_PROC_OFFICER = "PPROOFFICERNAME";
	public static final String PARAM_CANCEL_BY  = "PCANCELBY";
	public static final String PARAM_CAT_L1 = "PCATEGORYCODE";
	public static final String PARAM_CAT_L2 = "PSUBCATEGORYCODE";
	public static final String PARAM_CAT_L3 = "PSUBCATEGORYDIVISION";
	public static final String PARAM_BUSS_TYPE = "PBUSTYPE";
	public static final String PARAM_REG_STATUS = "PREGSTATUS";
	public static final String PARAM_SV_STATUS = "PSITESTATUS";
	public static final String PARAM_SVO_USER = " PSVOUSERID";
	public static final String PARAM_PAYMENT_METHOD = "PPAYMENTMENTHOD";
	public static final String PARAM_DISC_ZONE = "PZONE";
	public static final String PARAM_DISC_STATUS = "PSTATUS";
	public static final String PARAM_DISC_SUPP_TYPE = "PSUPPTYP";
	public static final String PARAM_DISC_SUPP_ID = "PSUPPID";
	public static final String PARAM_CERT_PROVIDER = "PISSUER";
	public static final String PARAM_CERT_APPL_TYPE = "PAPPTYP";
	public static final String PARAM_CERT_APPL_STATUS = "PAPPSTS";
	//12625 start
	public static final String PARAM_SVO_APPL_STATUS = "PSTATUSPERMOHONAN";
	public static final String PARAM_SVO_PERFORM_SITE_VISIT = "PSTATUSLAWATAN";
	//12625 end
	
	//15174 - start
	public static final String PARAM_PERSONNEL_NAME = "PPERSONNELNAME";
	//15174 - end

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
	public static final String SM_DISC_ACT_EXEC_SUMMARY_RPT_VAL = "SM_083";
	public static final String SM_APPL_CLIENT_CHARTER_K = "SM_085";
	public static final String SM_APPL_CLIENT_CHARTER_J = "SM_086";
	public static final String SM_REG_COMPANY_IN_BASIC_G2G = "SM_087";
	public static final String SM_SUPPLIER_PERFORMANCE_V_RPT_VAL = "SM_091";
	
	//12483 - Start
	public static final String SM_APPL_REVISED_RPT_VAL = "SM_095";
	//12483 - End

	// Screen fields constants
	public static final String SM_ZONE_SEMENANJUNG = "Semenanjung";
	public static final String SM_ZONE_SABAH = "Sabah";
	public static final String SM_ZONE_SARAWAK = "Sarawak";
	public static final String SM_REG_TYPE_BASIC = "Basic";
	public static final String SM_REG_TYPE_G2G = "G2G";
	public static final String SM_REG_TYPE_MOF = "MOF";
	public static final String SM_REG_TYPE_UPGRADE_BASIC_TO_MOF = "UPGRADE FROM BASIC TO MOF ACCOUNT";
	public static final String SM_STATUS_ACTIVE = "Active";
	public static final String SM_STATUS_INACTIVE = "Inactive";
	public static final String SM_ROLE_DIRECTOR = "Director";
	public static final String SM_ROLE_OWNER = "Owner";
	public static final String SM_ROLE_CEO = "CEO";
	public static final String SM_COMPLETE = "C";
	public static final String SM_IN_COMPLETE = "IC";
	public static final String SM_RESPONSE_REPORT = "RESPONSE";
	public static final String SM_SUMMARY_REPORT = "SUMMARY";
	
	public static final String SUPPL_CONSULT_MOF_NO = "465-";
	public static final String SUPPL_CONTRACT_MOF_NO = "357-";
	
	public static final String PAYMENT_MOF = "M";
	public static final String PAYMENT_PROCESSING = "P";
	public static final String PAYMENT_SOFTC_CERT = "S";

	public static final String CLASSIFICATION_CODE_1 = "H0171403";
	public static final String CLASSIFICATION_CODE_2 = "L1121107";														
	public static final String SOFTCERT_ISSUED_DATE = "I";
	public static final String SOFTCERT_REVOCATION_DATE = "K";
	public static final String SOFTCERT_REQUEST_DATE = "R";
	public static final String SOFTCERT_EXPIRY_DATE = "E";
	//public static final String SOFTCERT_RENEW_DATE = "N";
	
	public static final String CERT_APP_NEW = "N";
	public static final String CERT_APP_RENEW = "R";
	public static final String CERT_APP_REAPPLY = "A";
	public static final String CERT_APP_REVOKE = "K";
	
	private final int CERT_STATUS_PENDING_REQUEST = 4;
	private final int CERT_STATUS_PENDING_PAYMENT = 2;
	private final int CERT_STATUS_PENDING_ISSUANCE = 3;
	private final int CERT_STATUS_RENEWAL_READY = 7;
	private final int CERT_STATUS_ISSUED = 1;
	private final int CERT_STATUS_REJECTED = 6;
	
	public static final String PROVIDER_TRUST_GATE = "T";
	public static final String PROVIDER_DIGI_CERT = "D";
	
	public static final String REQUEST_BY_SUPP = "Requested By Supplier";
	public static final String AUTO_BY_SYS = "Auto Cancelled by System";
	
	public static final String PENDING_SV_STATUS = "Pending Prepare Site Visit Report";
	public static final String SV_COMPLETED_STATUS = "Site Visit Report Completed";
	
	public static final String REG_ACC_TYPE_BASIC = "B";
	public static final String REG_ACC_TYPE_G2G = "G";
	
	// #27199 - start
	public static final String SM_SUPPLIER_PERFORMANCE_COMP_1 = "PHARMANIAGA LOGISTICS SDN BHD";
	public static final String SM_SUPPLIER_PERFORMANCE_COMP_2 = "CCM PHARMACEUTICALS SDN BHD";
	public static final String SM_SUPPLIER_PERFORMANCE_COMP_3 = "MS ALI ENTERPRISE";
	// #27199 - end
	
	public String getSM_RESPONSE_REPORT() {
		return SM_RESPONSE_REPORT;
	}

	public String getSM_SUMMARY_REPORT() {
		return SM_SUMMARY_REPORT;
	}

	public String getSM_COMPLETE() {
		return SM_COMPLETE;
	}

	public String getSM_IN_COMPLETE() {
		return SM_IN_COMPLETE;
	}

	public String getSM_ROLE_DIRECTOR() {
		return SM_ROLE_DIRECTOR;
	}

	public String getSM_ROLE_OWNER() {
		return SM_ROLE_OWNER;
	}

	public String getSM_ROLE_CEO() {
		return SM_ROLE_CEO;
	}

	public String getSM_STATUS_ACTIVE() {
		return SM_STATUS_ACTIVE;
	}

	public String getSM_STATUS_INACTIVE() {
		return SM_STATUS_INACTIVE;
	}

	public String getSM_REG_TYPE_BASIC() {
		return SM_REG_TYPE_BASIC;
	}

	public String getSM_REG_TYPE_MOF() {
		return SM_REG_TYPE_MOF;
	}

	public String getSM_REG_TYPE_G2G() {
		return SM_REG_TYPE_G2G;
	}

	public String getSM_REG_TYPE_UPGRADE_BASIC_TO_MOF() {
		return SM_REG_TYPE_UPGRADE_BASIC_TO_MOF;
	}

	public String getSM_ZONE_SEMENANJUNG() {
		return SM_ZONE_SEMENANJUNG;
	}

	public String getSM_ZONE_SABAH() {
		return SM_ZONE_SABAH;
	}

	public String getSM_ZONE_SARAWAK() {
		return SM_ZONE_SARAWAK;
	}

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

	public String getPaymentMof() {
		return PAYMENT_MOF;
	}

	public String getPaymentProcessing() {
		return PAYMENT_PROCESSING;
	}

	public String getPaymentSoftcCert() {
		return PAYMENT_SOFTC_CERT;
	}

	public String getClassificationCode1() {
		return CLASSIFICATION_CODE_1;
	}

	public String getClassificationCode2() {
		return CLASSIFICATION_CODE_2;
	}

	public String getRequestBySupp() {
		return REQUEST_BY_SUPP;
	}

	public String getAutoBySys() {
		return AUTO_BY_SYS;
	}

	public String getPendingSvStatus() {
		return PENDING_SV_STATUS;
	}

	public String getSvCompletedStatus() {
		return SV_COMPLETED_STATUS;
	}

	public String getSmDiscActExecSummaryRptVal() {
		return SM_DISC_ACT_EXEC_SUMMARY_RPT_VAL;
	}

	public static String getSmApplClientCharterK() {
		return SM_APPL_CLIENT_CHARTER_K;
	}

	public static String getSmApplClientCharterJ() {
		return SM_APPL_CLIENT_CHARTER_J;
	}

	public String getCertAppNew() {
		return CERT_APP_NEW;
	}

	public String getCertAppRenew() {
		return CERT_APP_RENEW;
	}
	
	public String getCertAppRevoke() {
		return CERT_APP_REVOKE;
	}

	public String getCertAppReapply() {
		return CERT_APP_REAPPLY;
	}

	public String getSoftcertIssuedDate() {
		return SOFTCERT_ISSUED_DATE;
	}

	public String getSoftcertRevocationDate() {
		return SOFTCERT_REVOCATION_DATE;
	}

	public String getSoftcertExpiryDate() {
		return SOFTCERT_EXPIRY_DATE;
	}

	public String getSoftcertRequestDate() {
		return SOFTCERT_REQUEST_DATE;
	}

	public String getProviderTrustGate() {
		return PROVIDER_TRUST_GATE;
	}

	public String getProviderDigiCert() {
		return PROVIDER_DIGI_CERT;
	}

	public int getCertStatusPendingRequest() {
		return CERT_STATUS_PENDING_REQUEST;
	}

	public int getCertStatusPendingPayment() {
		return CERT_STATUS_PENDING_PAYMENT;
	}

	public int getCertStatusPendingIssuance() {
		return CERT_STATUS_PENDING_ISSUANCE;
	}

	public int getCertStatusRenewalReady() {
		return CERT_STATUS_RENEWAL_READY;
	}

	public int getCertStatusIssued() {
		return CERT_STATUS_ISSUED;
	}

	public int getCertStatusRejected() {
		return CERT_STATUS_REJECTED;
	}

	public String getRegAccTypeBasic() {
		return REG_ACC_TYPE_BASIC;
	}

	public String getRegAccTypeG2g() {
		return REG_ACC_TYPE_G2G;
	}	
	
	public static String getSmSupplierPerformanceVRptVal() {
		return SM_SUPPLIER_PERFORMANCE_V_RPT_VAL;
	}

	public String getSmApplRevisedRptVal() {
		return SM_APPL_REVISED_RPT_VAL;
	}
}
