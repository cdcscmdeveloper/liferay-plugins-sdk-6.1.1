/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.constant.sc;

/**
 * QTConstants for constants for sourcing.
 * 
 * @author VidaysagarReddy
 * @since 13.11.2013
 */
public interface QTConstants {

	public static final String QT_NUMBER = "qtNo";
	public static final String QT_TITILE = "qtTitle";
	public static final String PROCUREMENT_MODE = "procurementMode";
	public static final String CREATED_DATE = "createdDate";
	public static final String PUBLISH_DATE = "publishDate";
	public static final String CLOSED_DATE = "closingDate";
	public static final String STATUS = "";

	public static final String MAINTANANCE_PAGE = "quatationtendercreation";
	public static final String BO_MAINTANANCE_PAGE = "/xhtml/sc/quatationtender/businessowner/businessownercreation.xhtml";
	public static final String SEARCH_PAGE = "/xhtml/sc/quatationtender/quatationtendersearch.xhtml";
	public static final String CREATE_COMMITTE = "committeemaintanancepage";

	public static final String QT_DETAILS_TAB = "Quotation Details";
	public static final String QT_SUPPLIERQULIFICATION_TAB = "Supplier Qualification Criteria";
	public static final String QT_APPROVAL_TAB = "Approval";
	public static final String QT_SCHDULE_TAB = "Schedule & Contact Information";
	public static final String QT_DEDUCTION_TAB = "Deduction Payment";
	public static final String QT_DOCUMENTS_TAB = "Documents";
	public static final String QT_REMARKS_TAB = "Remarks";
	public static final String QT_BUSINESS_OWNER = "Business Owner";
	public static final String QT_COMMITTEE_APPOINTMENT = "Committee Appointment";
	public static final String QT_BRIEFING_SITEVISIT = "Briefing/Site Visit & Sample Submission";
	public static final String QT_TECHNICAL_SPECIFICATION = "Technical Specification";
	public static final String QT_FINANCIAL_SPECIFICATION = "Financial Specification";

	// Bsv Attendance Constants
	public static final String BSV_MOCULE_CODE = "SC";
	public static final String BSV_DOC_TYPE = "BSV";

	// Notice Board Constants
	public static final Integer NB_PUBLISH_STATUS = 0;
	public static final Integer NB_CHANGES_STATUS = 1;
	public static final Integer NB_CLOSED_STATUS = 2;
	public static final Integer NB_PRICE_SCHEDULE_STATUS = 3;
	public static final Integer NB_AWARDED_STATUS = 4;
	public static final String CATEGORY_L1_CODE = "categoryL1";
	public static final String CATEGORY_L2_CODE = "categoryL2";
	public static final String CATEGORY_L3_CODE = "categoryL3";

	public static final String PP_MODULE = "pp";

	public static final String DP_LOA_LIST_PAGE = "/xhtml/sc/loa/loalist/dpLoaList.xhtml";
	public static final String LOA_PAGE = "/xhtml/sc/loa/loarequest/loaDetailsMaint.xhtml";
	public static final String SUPPLIER_LOA_PAGE = "loaSupplierMaint.xhtml";
	public static final String DO_CONSOLIDATION_REPORT_PAGE = "/xhtml/sc/supplierFinalization/consolidationReport/consolidationReportMenu.xhtml";
	public static final String FC_COORDINATE_MEETING_PAGE = "/xhtml/sc/supplierFinalization/confirmMeeting/confirmMeeting.xhtml";
	public static final String FC_SUPPLIER_FINALIZATION_PAGE = "/xhtml/sc/supplierFinalization/suppFinalizationMenu.xhtml";
	public static final String FC_ONLINE_BIDDING_ENDORSEMENT_PAGE = "/xhtml/sc/supplierFinalization/onlineBiddingEndorsement/endorsement.xhtml";
	public static final String SCHEDULE_BIDDING_MAIN_PAGE = "/xhtml/sc/bidding/scheduleBidding/scheduleBiddingMain.xhtml";
	public static final String ONLINE_BIDDING_MAIN_PAGE = "/xhtml/sc/bidding/onlineBidding/onlineBiddingMain.xhtml";
	public static final String LOI_LIST_PAGE = "/xhtml/sc/loi/loilist/loiList.xhtml";
	public static final String LOI_PAGE = "/xhtml/sc/loi/loirequest/loiDetailsMaint.xhtml";
	public static final String SUPPLIER_LOI_PAGE = "/xhtml/sc/loi/supplierresponse/loiSupplierMaint.xhtml";
	public static final String MULTI_LOI_PAGE = "loiNegotiationResultMenu.xhtml";
	public static final String QT_LOA_LIST_PAGE = "/xhtml/sc/loa/qtLoaList/qtLoaList.xhtml";
	public static final String QT_SUPPLIER_A1_PAGE = "/xhtml/sc/loa/supplierresponse/a1form/loaSupplierAOneMaint.xhtml";
	public static final String SC_DIRECT_LOA_MAIN_PAGE = "/xhtml/sc/loa/directnego/directLoaDetailsMain.xhtml";
	public static final String SC_DIRECT_LOA_SUPPLIER_MAIN_PAGE = "/xhtml/sc/loa/directnegosuppresponse/directLoaSupplierMain.xhtml";

	public static final String SC_LOI_TAB = "LOI";
	public static final String SC_AWARD_TAB = "Award Details";
	public static final String SC_NEGOTIATION_TAB = "Negotiation Details";

	public static final String SC_LOA_TAB = "LOA";
	public static final String SC_LOA_AWARD_TAB = "Award Details";
	public static final String SC_PERFORMANCE_BOND_TAB = "Performance Bond";
	public static final String SC_PAYMENT_TAB = "Payment Details";
	public static final String LOI_NEGO_TERM_PRICE = "Price";

	public static final Long SC_MEETING_PENDING_STATUS_ID = 62650L;
	public static final Long SC_MEETING_CANCELLED_STATUS_ID = 62651L;
	public static final Long SC_MEETING_DONE_STATUS_ID = 62652L;
	public static final Long SC_MEETING_POSTPONE_STATUS_ID = 62653L;

	// Supplier Finalization Constants
	public static final String SF_FORWARD_TO_HIGHER_AUTH = "Forward Higher Authority";
	public static final String SF_RE_EVALUATION = "Re-Evaluation";
	public static final String SF_ONLINE_BIDDING = "Online Bidding";
	public static final String SF_MULTIPLE_LOI = "Multiple LOI";
	public static final String SF_AWARD = "Award";
	public static final String SF_BASIC = "Basic";
	public static final String SF_MULTIPLE_ITEMS = "Multiple Items";
	public static final String SF_BY_SUPPLIER = "By Supplier";
	public static final String SF_SINGLE = "single";
	public static final String SF_MULTIPLE = "multiple";

	// Deduction Payment
	public static final String DP_PENALTY = "PF";
	public static final String DP_ROYALTY = "RF";
	public static final String DP_FIXED = "FD";
	public static final String DP_NON_FIXED = "NF";
	public static final String DP_OTHERS = "OT";

	public static final String USER_NAME = "userName";
	public static final String IDENTIFICATION_NO = "identificationNo";
	public static final String IDENTIFICATION_TYPE = "identificationCode";

	// S2 bpm page redirect urls
	public static final String BUSINESS_OWNER_SPEC_PREPARATION = "/xhtml/sc/specification/businessOwner/businessOwnerMain.xhtml";
	public static final String SPECIFICICATON_COMM_PREPARATION = "/xhtml/sc/specification/specificationCommittee/specificationCommitteeMain.xhtml";
	public static final String BO_SPEC_CREATION_PAGE = "/xhtml/sc/specification/businessOwner/businessOwnerTabMenu.xhtml";

	// Constants for TreeNode construction..
	public static final String NODE_ROOT = "root";
	public static final String NODE_ZONE = "zone";
	public static final String NODE_STATE = "state";
	public static final String NODE_DIVISION = "division";
	public static final String NODE_DISTRICT = "district";
	public static final String NODE_CITY = "city";

	public static final String DOCTYPE_PREPARE_SPEC = "PS";
	public static final String DOCTYPE_BSV = "BSV";
	public static final String DOCTYPE_PROPOSAL = "PR";
	public static final String DOCTYPE_OPENING_COMMITTE = "OC";
	public static final String DOCTYPE_EVAL_COMMITTE = "EC";
	public static final String DOCTYPE_SPEC_COMMITTE = "SC";
	public static final String DOCTYPE_MEETING = "MS";
	public static final String EVALUATION_ONE_TIER = "1";
	public static final String EVALUATION_TWO_TIER = "2";

	public static final int QUOTAION_VALUE = 20000;
	public static final String OPEN_TYPE = "O";
	public static final String RESTRICTED_TYPE = "R";
	public static final String PRESENTATION_DEMO = "PD";

	public static final String BSV_TYPE_BRIEFING = "B";
	public static final String BSV_TYPE_SITEVISIT = "S";
	public static final String BSV_TYPE_BRIEFING_SITEVIST = "B";

	public static final String USER_SELECTION = "E";
	public static final String ACCESS_RIGHT = "E";

	// SPECIFICATION CONSTANTS
	public static final String TECHNICAL_DOC_TYPE = "1";
	public static final String FINANCIAL_DOC_TYPE = "2";
	public static final String TECHNICAL_SPEC_TYPE = "T";
	public static final String FINANCIAL_SPEC_TYPE = "F";
	public static final String CIRCULAR_PANEL_TYPE = "PQ";
	public static final String CIRCULAR_CENTRAL_CONTRACT_TYPE = "CQ";
	public static final String ITEM_SPECIFIC_TYPE = "Specific";
	public static final String ITEM_GENERAL_TYPE = "General";
	public static final String TEMPLETE_SPEC_CODE = "TS";
	public static final String TECHNICAL_GOV_CODE = "TG";
	public static final String TECHNICAL_PRIVATE_CODE = "TN";
	public static final String TECHNICAL_STAFF_LIST = "TL";
	public static final String TECHNICAL_NUMBER_EMPLOYEES = "NE";
	public static final String FINANCIAL_GOV_CODE = "FG";
	public static final String FINANCIAL_PRIVATE_CODE = "FN";
	public static final String FINANCIAL_CREDIT_FACILITY_CODE = "CF";
	public static final String FINANCIAL_CASH_ON_HAND_CODE = "CH";
	public static final String FINANCIAL_BANK_STMT_CODE = "BS";
	public static final String FINANCIAL_ASSET_LIABILITY_CODE = "AL";
	public static final String FINANCIAL_PAID_UP_CAPITAL_CODE = "PC";
	public static final String FINANCIAL_SUPPLIER_DETAILS_CODE = "SD";

	public static final String AUTO_SCORE = "1";
	public static final String USER_SCORE = "2";

	public static final String SUPPLIER_INFO_HQ = "HQ";

	// Evaluation Constants
	public static final String COMPLIANCE_CHECK = "C";
	public static final String TECHNICAL_EVALUATION = "T";
	public static final String PRESENTATION_DEMO_EVALUATION = "P";
	public static final String FINANCIAL_EVALUATION_BY_PACKAGE = "FP";
	public static final String FINANCIAL_EVALUATION_BY_ITEM = "FI";
	public static final String RECOMMENDATION_BY_PACKAGE = "RP";
	public static final String RECOMMENDATION_BY_ITEM = "RI";
	public static final String AWARD_TYPE_PACKAGE = "P";
	public static final String AWARD_TYPE_ITEM = "I";
	public static final String RECOMMENDATION_PREFERENCE_MARGIN = "RPM";
	public static final String FINANCIAL_PREFERENCE_MARGIN = "FPM";
	public static final String FINANCIAL_EVALUATION = "F";
	public static final String EVALUATION_REPORT = "ERR";
	public static final String OPENING_COMMITTEE_REPORT = "OCR";
	public static final String TECHNICAL_EVALUATION_REPORT = "TCR";
	public static final String FINANCIAL_EVALUATION_REPORT = "FCR";
	
	public static final int SETTING_TYPE_TECHNICAL = 1;
	public static final int SETTING_TYPE_FINANCIAL_BY_PACKAGE = 2;
	public static final int SETTING_TYPE_DEMO = 3;
	public static final int SETTING_TYPE_RECOMMENDATION_BY_PACKAGE = 4;
	public static final int SETTING_TYPE_RECOMMENDATION_BY_ITEM = 5;
	public static final int SETTING_TYPE_FINANCIAL_BY_ITEM = 6;

	public static final int CHECKLIST_USER_SCORE = 2;

	public static final String OTHERS_APPROVAL = "Others Approval";
	public static final String QT_OPEN = "Open";
	
	public static final String CHAIRPERSON = "_CHAIRPERSON";
	
	public static final String REPLACE_CHAR = "_";
	public static final String CT_PANEL_ZONE_COVERAGE = "Z";
	public static final String CT_PANEL_ALL_COVERAGE = "A";
	
	public static final int SUPP_QUAL_TAB_INDEX = 1;
	public static final int COMMITTE_TAB_INDEX = 2;
	public static final int APPROVAL_TAB_INDEX = 3;
	public static final int DEDUCTION_TAB_INDEX = 6;
	public static final int DOCUMENTS_TAB_INDEX = 7;
	public static final int ACTIVE_TAB_INDEX = 8;
}
