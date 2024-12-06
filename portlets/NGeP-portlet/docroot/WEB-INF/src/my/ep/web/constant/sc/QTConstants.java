/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.constant.sc;

import java.math.RoundingMode;	//Bug #35600@Syazani 19/09/2024

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
	
	/* BUG 31120 START*/
	public static final String QT_ORG_PROFILE = "orgProfileId";
	/* BUG 31120 END*/
	
	/* Enhancement 27668 START*/
	public static final String FINALIZATION_SECRETARY_LOGIN_ID = "finalizationSecratery.loginId";
	public static final String FINALIZATION_HIGH_AUTH_SECRETARY_LOGIN_ID = "finalization.higherSecretary.loginId";
	/*  Enhancement 27668 END */

	public static final String MAINTANANCE_PAGE_FULL = "/xhtml/sc/quatationtender/quatationtendercreation.xhtml";
	public static final String MAINTANANCE_PAGE = "quatationtendercreation";
	public static final String BO_MAINTANANCE_PAGE = "/xhtml/sc/quatationtender/businessowner/businessownercreation.xhtml";
	public static final String SEARCH_PAGE = "/xhtml/sc/quatationtender/quatationtendersearch.xhtml";
	public static final String CREATE_COMMITTE = "committeemaintanancepage";
	public static final String QT_SEARCHING_PAGE = "/xhtml/sc/quatationtender/quatationtendersearch.xhtml?faces-redirect=true"; //#9019
	public static final String MAINT_EPV_APPROVAL_DETAIL = "/xhtml/sc/quatationtender/Approver/maintenanceApprovalEpv.xhtml";//#21692 add new page for history

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
	public static final String SUPPLIER_LOA_PAGE = "/xhtml/sc/loa/supplierresponse/loaSupplierMaint.xhtml";
	public static final String DO_CONSOLIDATION_REPORT_PAGE = "/xhtml/sc/supplierFinalization/consolidationReport/consolidationReportMenu.xhtml";
	public static final String FC_COORDINATE_MEETING_PAGE = "/xhtml/sc/supplierFinalization/confirmMeeting/confirmMeeting.xhtml";
	public static final String FC_SUPPLIER_FINALIZATION_PAGE = "/xhtml/sc/supplierFinalization/suppFinalizationMenu.xhtml";
	public static final String FC_ONLINE_BIDDING_ENDORSEMENT_PAGE = "/xhtml/sc/supplierFinalization/onlineBiddingEndorsement/endorsement.xhtml";
	public static final String SCHEDULE_BIDDING_MAIN_PAGE = "/xhtml/sc/bidding/scheduleBidding/scheduleBiddingMain.xhtml";
	public static final String ONLINE_BIDDING_MAIN_PAGE = "/xhtml/sc/bidding/onlineBidding/onlineBiddingMain.xhtml";
	public static final String LOI_LIST_PAGE = "/xhtml/sc/loi/loilist/loiList.xhtml";
	public static final String LOI_PAGE = "/xhtml/sc/loi/loirequest/loiDetailsMaint.xhtml";
	
	/* Enhancement #22211-START */
	public static final String LOA_CANCEL_MAIN_PAGE = "/xhtml/sc/loa/cancellation/cancelLoaMain.xhtml";	
	public static final String LOA_CANCEL_APPROVAL_PAGE = "/xhtml/sc/loa/cancellation/cancelLoaApproval.xhtml";
	/* Enhancement #22211-END */
	
	/* Enhancement #21694-START */
	public static final String LOI_LOA_REVERSE_PAGE = "/xhtml/sc/loiLoaReverse/loiLoaReverseDetails.xhtml";
	public static final String VIEW_QT_COMMITTEE_PAGE = "/xhtml/sc/supplierFinalization/confirmMeeting/viewQtCommitteePage.xhtml";
	public static final String LOI_REVERSE_PAGE = "/xhtml/sc/loi/loirequest/loiReverseDetails.xhtml";
	/* Enhancement #21694-END */
	public static final String SUPPLIER_LOI_PAGE = "/xhtml/sc/loi/supplierresponse/loiSupplierMaint.xhtml";
	public static final String MULTI_LOI_PAGE = "loiNegotiationResultMenu.xhtml";
	public static final String QT_LOA_LIST_PAGE = "/xhtml/sc/loa/qtLoaList/qtLoaList.xhtml";
	public static final String QT_SUPPLIER_A1_PAGE = "/xhtml/sc/loa/supplierresponse/a1form/loaSupplierAOneMaint.xhtml";
	public static final String SC_DIRECT_LOA_MAIN_PAGE = "/xhtml/sc/loa/directnego/directLoaDetailsMain.xhtml";
	public static final String SC_DIRECT_LOA_SUPPLIER_MAIN_PAGE = "/xhtml/sc/loa/directnegosuppresponse/directLoaSupplierMain.xhtml";
	public static final String PENDING_RESPONSE_DP_QT = "/xhtml/sc/pendingresponsefordpandqt/prerenderresponsedpqtmain.xhtml";
	public static final String ONLINE_BIDDING_STATUS_COUNT = "/xhtml/sc/bidding/onlinebiddingstatus/onlinebiddingstatusmain.xhtml";
	public static final String VIEW_QTDETAILS_GENERAL_INFO = "/xhtml/sc/viewqtdetailsgeneralinfo/viewQtDetailsGeneralInfoMain.xhtml";
	public static final String SUPPLIER_VIEW_QTDETAILS_GENERAL_INFO = "/xhtml/sc/viewqtdetailsgeneralinfo/supplierViewQtDetailsGeneralInfoMain.xhtml";//bug #33090
	public static final String NAV_TO_PROPOSAL_MAIN_PAGE = "/xhtml/sc/supplierproposal/proposalcommon/supplierProposalMain.xhtml";  // ***25201

	public static final String SC_LOI_TAB = "LOI";
	public static final String SC_AWARD_TAB = "Award Details";
	public static final String SC_NEGOTIATION_TAB = "Negotiation Details";

	public static final String SC_LOA_TAB = "LOA";
	public static final String SC_LOA_AWARD_TAB = "Award Details";
	public static final String SC_PERFORMANCE_BOND_TAB = "Performance Bond";
	public static final String SC_PAYMENT_TAB = "Payment Details";
	public static final String LOI_NEGO_TERM_PRICE = "Harga";

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
	public static final String SPECIFICICATON_COMM_PREPARATION = "/xhtml/sc/specification/specificationCommittee/specificationCommitteeMain.xhtml";

	// Constants for TreeNode construction..
	public static final String NODE_ROOT = "root";
	public static final String NODE_ZONE = "zone";
	public static final String NODE_STATE = "state";
	public static final String NODE_DIVISION = "division";
	public static final String NODE_DISTRICT = "district";
	public static final String NODE_CITY = "city";
	
	public static final String DOCTYPE_MEETING = "MS";

	public static final String PRESENTATION_DEMO = "PD";

	public static final String BSV_TYPE_BRIEFING = "B";
	public static final String BSV_TYPE_SITEVISIT = "S";
	public static final String BSV_TYPE_BRIEFING_SITEVIST = "B";

	public static final String USER_SELECTION = "E";

	// SPECIFICATION CONSTANTS
	public static final String TECHNICAL_DOC_TYPE = "1";
	public static final String FINANCIAL_DOC_TYPE = "2";
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
	
	public static final String INITIAl_SCORE = "0";//#29168
	public static final String AUTO_SCORE = "1";
	public static final String USER_SCORE = "2";

	public static final String SUPPLIER_INFO_HQ = "HQ";

	// Evaluation Constants
	public static final String COMPLIANCE_CHECK = "C";
	public static final String TECHNICAL_EVALUATION = "T";
	// **** #21695 farid start
	public static final String TECHNICAL_EVALUATION_BY_ITEM = "TI"; // **** farid 
	public static final String PRESENTATION_DEMO_EVALUATION = "P";
	public static final String FINANCIAL_EVALUATION_BY_PACKAGE = "FP";
	public static final String FINANCIAL_EVALUATION_BY_ITEM = "FI";
	public static final String RECOMMENDATION_BY_PACKAGE = "RP";
	public static final String RECOMMENDATION_BY_ITEM = "RI";
	public static final String AWARD_TYPE_PACKAGE = "P";
	public static final String AWARD_TYPE_ITEM = "I";
	public static final String RECOMMENDATION_PREFERENCE_MARGIN = "RPM";
	public static final String FINANCIAL_PREFERENCE_MARGIN = "FPM";
	// **** #21695 farid start
	public static final String TECHNICAL_PREFERENCE_MARGIN = "TPM"; // **** farid 
	public static final String FINANCIAL_EVALUATION = "F";

	public static final int CHECKLIST_USER_SCORE = 2;

	public static final String OTHERS_APPROVAL = "Others Approval";
	public static final String QT_OPEN = "Open";
	
	public static final String REPLACE_CHAR = "_";
	public static final String CT_PANEL_ZONE_COVERAGE = "Z";
	public static final String CT_PANEL_ALL_COVERAGE = "A";

	public static final int DEDUCTION_TAB_INDEX = 6;
	public static final int DOCUMENTS_TAB_INDEX = 7;
	public static final int ACTIVE_TAB_INDEX = 8;
	
	
	public static final int CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_FED_GOV = 10; // TODO
	public static final int CRITERIA_TYPE_TOTAL_NO_OF_CONTRACT_YEARS_FOR_FED_GOV = 11; // TODO
	public static final int CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_FED_GOV = 12; // TODO
	public static final int CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_NON_FED_GOV = 15;
	public static final int CRITERIA_TYPE_TOTAL_NO_OF_CONTRACT_YEARS_FOR_NON_FED_GOV = 16;
	public static final int CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_NON_FED_GOV = 17;
	public static final int CRITERIA_TYPE_TOTAL_YEAR_EXPERIENCE = 20; // TODO
	public static final int CRITERIA_TYPE_TOTAL_EMPLOYEE = 21;
	public static final int CRITERIA_TYPE_NO_OF_YEARS_WARRANTY = 30; // TODO
	public static final int CRITERIA_TYPE_NO_OF_MONTH_OF_TRAINING = 40; // TODO
	public static final int CRITERIA_TYPE_AVERAGE_OF_BANK_STATEMENT = 50;
	public static final int CRITERIA_TYPE_TOTAL_CREDIT_FACILITIES = 51;
	public static final int CRITERIA_TYPE_TOTAL_CASH_ON_HAND = 52;
	public static final int CRITERIA_TYPE_ASSETS_AND_LIABILITIES_ACID_RATIO = 60;
	public static final int CRITERIA_TYPE_PAID_UP_CAPITAL_AMOUNT = 70; // TODO
	public static final int CRITERIA_TYPE_SUPPLIER_DETAILS = 30;
	public static final int CRITERIA_TYPE_ASSETS_AND_LIABILITIES_CURRENT_RATIO = 61;
	public static final String NEW_LINE_CHARACTER = "\r\n";
	public static final String HTML_NEW_LINE_CHARACTER = "<br />";
	public static final String AMBRA_SENT = "&";
	public static final String HTML_AMBRA_SENT = "&#38;";
	public static final String LESS_THEN = "<";
	public static final String HTML_LESS_THEN = "&#60;";
	public static final String GREATER_THEN = ">";
	public static final String HTML_GREATER_THEN = "&#62;";
	
	//#32119 - start
	public static final String PORTLET_NAME_CT_MAINTENANCE = "ctContractMaintenance_WAR_NGePportlet";
	public static final String PORTLET_NAME_CT_SEARCH = "ctContractMaintenance_WAR_NGePportlet";
	//#32199 - end
	
	public static final String PORTLET_NAME_QT_MAINTENANCE = "scQuotationTender_WAR_NGePportlet";
	public static final String PORTLET_NAME_QT_SEARCH = "scQuotationTenderSearch_WAR_NGePportlet";
	public static final String PORTLET_NAME_QT_TECHSUPP = "scQuotationTenderSearchTechSupp_WAR_NGePportlet";
	// ***25692#19 - start
	public static final String PORTLET_NAME_APPROVAL = "QTApproval_WAR_NGePportlet";
	// ***25692#19 - end
	
	public static final String PORTLET_NAME_QT_SUPPLIER_INVITATION = "scSupplierInvitationQt_WAR_NGePportlet";
	public static final String FINANCIAL_SUMMARY_PACKAGE_MARGIN="FPM";
	public static final String FINANCIAL_SUMMARY_ITEM="FI";
	public static final String RECOMMENDATION_SUMMARY_PACKAGE_MARGIN="RPM";
	public static final String RECOMMENDATION_SUMMARY_ITEM="RI";
	// **** #21695 farid start
	public static final String TECHNICAL_SUMMARY_PACKAGE_MARGIN="TPM";
	public static final String TECHNICAL_SUMMARY_ITEM="TI";
	// **** farid end
	public static final String REPORT_ACKNOWLEDGEMENT_LOA_RECEIPT = "Perakuan Penerimaan Surat Setuju Terima";
	
	//kpi code
	public static final String KPI_CODE_QUOTATION_REMINDER  = "SC016"; //Proposal Validity Reminder (Quotation)
	public static final String KPI_CODE_TENDER_REMINDER = "SC017"; // Proposal Validity Reminder (Tender)
	public static final String KPI_CODE_BIDDING_REMINDER = "SC018"; //Upcoming BidSessions Reminder
	public static final String SPEC_MAINTENANCE = "SM"; 
	public static final String SPEC_MAINTENANCE_SEARCH_PAGE = "/xhtml/sc/speccommitteemaintenance/specificationCommitteeMaintenanceSearch.xhtml";
	
	
	public static final long PM_STATUS_INACTIVE_CODE = 0L;
	
	// 25037 - start
	public static final Long ALLOW_COMMITTEE_REASSIGNMENT = 25037L;
	public static final String QT_GPKI_DLG_SHOW = "gpki_dlg.show()";
	// 25037 - end

	//#11104
	public static final String PORTLET_NAME_QT_SPEC_COMMITTEE_MAINTENANCE="scQTPrepareSpec_WAR_NGePportlet";
	public static final String PORTLET_NAME_QT_SPEC_ENDORSMENT_MAINTENANCE="scSpecCommitteeMaintenance_WAR_NGePportlet";
	public static final String PORTLET_NAME_QT_SPEC_ENDORSMENT_TASKLIT="scSpecificationCommitteeEndorsement_WAR_NGePportlet";
	
	public static final String FINANCIAL_ITEM_SPEC_ANSWER="FISA";//32453
	
	//9019 - Start (Harun) WIP transaction Pending QT Submission
	public static final String SC_WFTASK_COMPOSITENAME = "SourcingQT";
	//9019 - End (Harun) 
	
	public static final String PORTLET_NAME_DL_CREATE = "scCreateManualSourcing_WAR_NGePportlet";//#32648
	public static final String PORTLET_NAME_QT_LIST = "scQtLoaList_WAR_NGePportlet";//#32648
	
	// #33831 - Start
	public static final String PORTLET_NAME_SUPPLIER_PROPOSAL = "scSupplierProposal_WAR_NGePportlet";
	// #33831 - Start

	//#29468 Start Vinod
	public static final String TEMPLATE_CHANGE_VALUE = "value";
	public static final String CLOSE_CLONE_LOA_SPEC_POPUP = "cloneLoaSpec.hide()";
	//#29468 End Vinod
	
	// 21692 QT Bpm Composite Version Validity ID
	public static final Long ALLOW_CANCEL_LOA_LOI_FOR_EPV = 21692L;
	
	public static final String PORTLET_NAME_QT_LOI_LIST="scLoiList_WAR_NGePportlet";//#21692
	
	// #29007 Start Vinod
	public static final String LAMPIRAN_A3_LBL = "lbl.sc.doc.name.lampiran.a3";
	public static final String LAMPIRAN_A3_UPDATE = "Updated";
	public static final String LAMPIRAN_A3_DIKEMAS_KINI = "Dikemas kini";
	public static final String LAMPIRAN_A3_LATEST = "Latest";
	public static final String LAMPIRAN_A3_TERKINI = "Terkini";
	public static final String LAMPIRAN_A3_JUSTIFICATION = "Dikemas kini atas sebab ";
	public static final String LAMPIRAN_A3_FILE_EXTENSION_PDF = ".pdf";
	public static final String PORTLET_ATTRIBUTE_NEW_OFFERED_SPECIFICATION = "selectedScLoiLoaItem";
	public static final String REDIRECT_URL_NEW_OFFERED_SPECIFICATION = "/xhtml/sc/loa/directnego/directLoaSuppliersProposalSpecificationUpdatePopup.xhtml";
	public static final String REDIRECT_URL_NEW_OFFERED_SPECIFICATION_LOA = "/xhtml/sc/loa/loarequest/loaSuppliersProposalSpecificationUpdatePopup.xhtml";
	public static final String LBL_SC_LOILOA_SUPPLIERS_PROPOSAL_SPECIFICATION_UPDATE = "lbl.sc.loiloa.suppliers.proposal.specification.update";
	public static final String LBL_SC_JUSTIFICATION = "lbl.sc.justification";
	public static final String LBL_SC_LOILOA_SUPPLIER_SPECIFICATION_SAVE = "lbl.sc.loiloa.supplier.specification.save";
	public static final String LAMPIRAN_A3_TEXT_TB = "T/B";
	public static final String LAMPIRAN_A3_TEXT_NA = "N/A";
	public static final String LAMPIRAN_A3_TEXT_SERVICE_SCOPE = "serviceScope";
	public static final String LAMPIRAN_A3_TEXT_ITEMS_LIST = "itemsList";
	public static final String LAMPIRAN_A3_TEXT_IS_ZONAL = "isZonal";
	public static final String LAMPIRAN_A3_TEXT_BEAN = "bean";
	public static final String LAMPIRAN_A3_TEXT_YA = "Ya";
	public static final String LAMPIRAN_A3_TEXT_TIDAK = "Tidak";
	public static final String NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE = " <p></p> ";
	// #29007 End Vinod
	// #29187 Start Vinod
	public static final String LBL_TXT_SELECT = "lbl.txt.select";
	public static final String LBL_TXT_YES = "lbl.txt.yes";
	public static final String LBL_TXT_NO = "lbl.txt.no";
	public static final String COMPLIANCE_CONFIRMATION_POPUP = "complianceConfirmPopUp.show()";
	public static final String COMPLIANCE_SAMPLE_CONFIRMATION_POPUP = "complianceSampleConfirmPopUp.show()";
	public static final String VIEW_UOM_QUANTITY_DETAILS = "viewUOMQuantityDetails.show()";
	public static final String VIEW_ZONE_QUANTITY_POPUP = "viewZoneQuantityPopup.show()";
	// #29187 End Vinod
	
	public static final String TEC_PRESENTATION_DEMO_REMARK = "TDR";
	
	public static final String SUPPLIER_CRITERIA = "SC";//#29732 change hard code to constant
	
	// #25726 - reopen note 110
	public static final String SURAT_SETUJU_TERIMA = "Surat Setuju Terima (Termasuk Lampiran A)";
	// #25726 end
	
	//#10542 Start Dayan
	public static final String COMPLIANCE_DOCUMENT_SUPPLIER_NO = "Supplier ";
	public static final String FINANCIAL_DOWNLOADBY_SUPPLIER_ZIP_TITLE = "NGEP-Financial-BySupplier";
	public static final String FINANCIAL_DOWNLOADBY_DOCUMENT_ZIP_TITLE = "NGEP-Financial-ByTitleDocument";
	public static final String TECHNICAL_DOWNLOADBY_SUPPLIER_ZIP_TITLE = "NGEP-Technical-BySupplier";
	public static final String TECHNICAL_DOWNLOADBY_DOCUMENT_ZIP_TITLE = "NGEP-Technical-ByTitleDocument";
	//#10542 End Dayan
	public static final int COMPLIANCE_SAMPLE_FINANCIAL_ACTIVE_INDEX=1;//35148
	public static final int COMPLIANCE_FINANCIAL_ACTIVE_INDEX=2;
	public static final int EVALUATION_MAIN_ACTIVE_INDEX=7;//35148
	
	public static final String SERVICE_OBJECT_CC = "cc";
	
	// Bug #35485@Syazani 08/02/2024 -- Start
	public static final String EVALUATION_COMMITTEE_REPORT_ZIP_TITLE = "NGeP-QT-Evaluation-Committee-Report";
	public static final String OPENING_COMMITTEE_REPORT_ZIP_TITLE = "NGeP-QT-Opening-Committee-Report";
	public static final String TECHNICAL_EVALUATION_COMMITTEE_REPORT_ZIP_TITLE = "NGeP-QT-TechnicalEvaluation-Committee-Report";
	public static final String FINANCIAL_EVALUATION_COMMITTEE_REPORT_ZIP_TITLE = "NGeP-QT-FinancialEvaluation-Committee-Report";

	public static final String DOC_TYPE_SC_EVALUATION_COMMITTEE_REPORT_ATTACHMENT = "ECR";
	public static final String DOC_TYPE_SC_OPEN_COMMITTEE_REPORT_ATTACHMENT = "OCR";
	public static final String DOC_TYPE_SC_TECHNICAL_EVALUATION_REPORT_ATTACHMENT = "TCR";
	public static final String DOC_TYPE_SC_FINANCIAL_EVALUATION_REPORT_ATTACHMENT = "FCR";
	public static final String DOC_TYPE_SC_QT_FINALIZATION_ATTACHMENT = "KTR";
	// Bug #35485@Syazani 08/02/2024 -- End
	
	//#29732 -Start
	public static final String LBL_SC_CATEGORY_HAS_CHANGES_ON_PRERENDER_VIEW_1 = "lbl.sc.category.has.changes.on.prerender.view.1";
	public static final String LBL_SC_CATEGORY_HAS_CHANGES_ON_PRERENDER_VIEW_2 = "lbl.sc.category.has.changes.on.prerender.view.2";
	public static final String LBL_SC_CATEGORY_HAS_CHANGES_ON_PRERENDER_VIEW_2_A = "lbl.sc.category.has.changes.on.prerender.view.2.a";
	public static final String QT_CAT_CODE_CHANGE_ALERT_SHOW = "catCodeChangeAlert.show()";
	public static final String QT_STATUS_BUTTON_ENDORSE = "ENDORSE";
	//#29732 -End
	
	// Bug #35688@Syazani 10/07/2024 -- Start
	public static final String TAB_SC_GENERAL_INFORMATION = "0";
	public static final String TAB_SC_SUPPLIER_QUALIFICATION_CRITERIA = "1";
	public static final String TAB_SC_TECHNICAL_CHECKLIST = "2";
	public static final String TAB_SC_FINANCIAL_CHECKLIST = "3";
	public static final String TAB_SC_TECHNICAL_SCORING = "4";
	public static final String TAB_SC_FINANCIAL_SCORING = "5";
	public static final String TAB_SC_OVERALL_SCORING = "6";
	public static final String TAB_SC_DELIVERY_ADDRESS = "7";
	public static final String TAB_SC_PAYMENT_DEDUCTION = "8";
	public static final String TAB_SC_FULFILMENT_SCHEDULE = "9";
	public static final String TAB_SC_SPECIFICATION_ENDORSEMENT_STATUS = "10";
	// Bug #35688@Syazani 10/07/2024 -- End
	
	// 36671 - start
	public static final String QT_CMT_APPROVER_PANEL = "/xhtml/sc/quatationtender/Approver/approverPanel.xhtml";
	// 36671 - end
	
	public static final RoundingMode QT_ROUNDING_MODE = RoundingMode.HALF_UP;	//Bug #35600@Syazani 19/09/2024
 
	// 25791 -start-
	public static final String QT_MAINTENACE_BIDDING_MANAGEMENT_VIEW = "/xhtml/sc/bidding/bidManagement/bidManagementMain.xhtml?faces-redirect=true";
	public static final String QT_CT_LOA_DETAILS_VIEW = "/xhtml/sc/quatationtender/viewQTCTLOADetails.xhtml";
	public static final String LOA_CT_DETAILS_VIEW = "/xhtml/sc/quatationtender/viewLOAContractList.xhtml";
	public static final int TAB_MAIN_INDEX = 0;
	public static final String CONTRACT_PAGE_DETAILS_VIEW = "/xhtml/ct/fulfilmentdetails/creation/fulfilment.xhtml";
	public static final String DATATABLE_QT_DL_LOA_CT_COMPONENT_ID = "ctLoaId";
	public static final String DATATABLE_LOA_CT_COMPONENT_ID = "laCtdvd";
	public static final String CONTRACT_STATUS_EFFECTIVE = "lbl.ct.effective";
	public static final String CONTRACT_STATUS_INACTIVE = "lbl.ct.inactive";
	public static final String CONTRACT_STATUS_EXPIRED = "lbl.ct.expired";
	public static final String CONTRACT_STATUS_TERMINATED = "lbl.ct.terminated";
	public static final String CONTRACT_STATUS_COMPLETE = "lbl.ct.complete";
	public static final String CONTRACT_STATUS_INCOMPLETE = "lbl.ct.incomplete";
	public static final String CONTRACT_STATUS_NOT_APPLICABLE = "lbl.ct.not.applicable";
	// 25791 -end-
}
