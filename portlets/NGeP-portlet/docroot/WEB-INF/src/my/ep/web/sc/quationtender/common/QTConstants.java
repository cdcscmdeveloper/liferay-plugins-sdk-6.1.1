
/**
 * 
 */
package my.ep.web.sc.quationtender.common;
/**
 * @author VidyasagarReddy
 *
 */
public interface QTConstants {
	
	public static final String QT_NUMBER="qtNo";
	public static final String QT_TITILE="qtTitle";
	public static final String PROCUREMENT_MODE="procurementMode";
	public static final String CREATED_DATE="createdDate";
	public static final String PUBLISH_DATE="publishDate";
	public static final String CLOSED_DATE="closingDate";
	public static final String STATUS="";
	
	public static final String MAINTANANCE_PAGE="quatationtendercreation";
	public static final String BO_MAINTANANCE_PAGE="/xhtml/sc/quatationtender/businessowner/businessownercreation.xhtml";
	public static final String DP_LOA_PAGE = "dploalist";
	
	public static final String CREATE_COMMITTE = "committeemaintanancepage";
	
	public static final String QT_DETAILS_TAB="Quotation Details";
	public static final String QT_SUPPLIERQULIFICATION_TAB="Supplier Qualification Criteria";
	public static final String QT_APPROVAL_TAB="Approval";
	public static final String QT_SCHDULE_TAB="Schedule & Contact Information";
	public static final String QT_DEDUCTION_TAB="Deduction Payment";
	public static final String QT_DOCUMENTS_TAB="Documents";
	public static final String QT_REMARKS_TAB="Remarks";
	public static final String QT_BUSINESS_OWNER="Business Owner";
	public static final String QT_COMMITTEE_APPOINTMENT="Committee Appointment";
	public static final String QT_BRIEFING_SITEVISIT="Briefing/Site Visit & Sample Submission";
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
	public static final String CATEGORY_L1_CODE="categoryL1";
	public static final String CATEGORY_L2_CODE="categoryL2";
	public static final String CATEGORY_L3_CODE="categoryL3";
	
	public static final String PP_MODULE="pp";

	public static final String LOI_PAGE = "loidetailspage";
	public static final String LOA_PAGE = "loaDetailsMaint";
	
	public static final String SC_LOI_TAB="LOI";
	public static final String SC_AWARD_TAB="Award Details";
	public static final String SC_NEGOTIATION_TAB="Negotiation Details";
	
	public static final String SC_LOA_TAB="LOA";
	public static final String SC_LOA_AWARD_TAB = "Award Details";
	public static final String SC_PERFORMANCE_BOND_TAB="Performance Bond";
	public static final String SC_PAYMENT_TAB="Payment Details";
	
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

	//committee Appointment
	
	public static final String SPECIFICATION_COMMITTEE_TAB="Specification Committee";
	public static final String OPENING_COMMITTEE_TAB="Opening Committee";
	public static final String TECHNICAL_COMMITTEE_TAB="Technical Evaluation Committee";
	public static final String FINANCIAL_COMMITTEE_TAB="Financial Evaluation Committee";
	public static final String EVALUATION_COMMITTEE_TAB="Evaluation Committee";
	public static final String FINALIZATION_SECRETARY_TAB="Finalization Secretary";
	
	//S2 bpm page redirect urls
	public static final String BUSINESS_OWNER_SPEC_PREPARATION="/xhtml/sc/specification/specificationview.xhtml";
	public static final String SPECIFICICATON_COMM_PREPARATION="/xhtml/sc/specification/specificationCommitteeView.xhtml";
	public static final String BO_SPEC_CREATION_PAGE="/xhtml/sc/specification/specificationCreation.xhtml";
	
	//Constants for TreeNode construction..
	public static final String NODE_ROOT="root";
	public static final String NODE_ZONE="zone";
	public static final String NODE_STATE="state";
	public static final String NODE_DIVISION="division";
	public static final String NODE_DISTRICT="district";
	public static final String NODE_CITY="city";
	
	public static final String DOCTYPE_QT = "QT";
	public static final String DOCTYPE_PREPARE_SPEC = "PS";
	public static final String DOCTYPE_BSV="BSV";
	public static final String DOCTYPE_PROPOSAL="PR";
	public static final String DOCTYPE_OPENING_COMMITTE="OC";
	public static final String DOCTYPE_EVAL_COMMITTE="EC";
	public static final String DOCTYPE_SPEC_COMMITTE="SC";
	public static final String EVALUATION_ONE_TIER="1";
	public static final String EVALUATION_TWO_TIER="2";
	
	public static final int QUOTAION_VALUE=20000;
	public static final String OPEN_TYPE="O";
	public static final String RESTRICTED_TYPE="R";
	
	
}
