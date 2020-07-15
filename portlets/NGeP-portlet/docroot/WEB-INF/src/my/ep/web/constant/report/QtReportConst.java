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
package my.ep.web.constant.report;

import java.io.Serializable;

/**
 * The Class QtReportConst.
 * 
 * @author:Garikapati Mangaiah.
 */
public class QtReportConst implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String URL_RPT_DATE_ANOTHER_FORMAT = "dd-MMM-yyyy";
	public static final String EMPTY_VAL = "";
	public static final String ZERO_VAL = "0";
	public static final String ONE_VAL = "1";
	
	//Report code
	public static final String SC_QUOT_TENDER_OR_RPT_VAL = "SC_038";
	public static final String SC_SUMMRY_SRC_OR_RPT_VAL = "SC_039";
	public static final String SC_BRIF_SITE_VISIT_OR_RPT_VAL = "SC_040";
	public static final String SC_QUOT_TENDER_MONITOR_OR_RPT_VAL = "SC_041";
	public static final String SC_PRAPOSAL_SUB_LST_OR_RPT_VAL = "SC_042";
	public static final String SC_PRICE_SCHEDULE_OR_RPT_VAL = "SC_043";
	public static final String SC_LETTER_OF_ACCPT_OR_RPT_VAL = "SC_044";
	public static final String SC_COMMITTEE_DTLS_TD_RPT_VAL = "SC_045";
	public static final String SC_PROPOSAL_DTLS_PRODUCT_RPT_VAL = "SC_046";
	public static final String SC_PROPOSAL_DTLS_SERVICE_RPT_VAL = "SC_047";
	public static final String SC_BRIEFING_PAPER_RPT_VAL = "SC_048";
	public static final String SC_EVALUATION_COMMITTEE_RPT_VAL = "SC_049";
	public static final String SC_LETTER_OF_ACCPT_CONTENT_RPT_VAL = "SC_075";
	public static final String SC_PROP_VALID_PERIOD_EXT_RPT_VAL = "SC_076";
	public static final String SC_OPENING_COMMITTEE_RPT_VAL = "SC_071";
	public static final String SC_TECHINICAL_EVALUATION_COMMITTEE_RPT_VAL = "SC_072";
	public static final String SC_FINANCIAL_EVALUATION_COMMITTEE_RPT_VAL = "SC_073";
	
	
	//Report parameterKey
	public static final String PARAM_KEY_REPORT ="report";
	public static final String PARAM_USER_ID ="PUSER";
	public static final String PARAM_REPORT_CODE ="PREPORTID";
	public static final String QUOTATION_TENDER_ID ="qtid";
	public static final String QUOTATION_TENDER_ESTIMATED_AMOUNT ="PESTAMT";
	public static final String PARAM_CREAT_FRM_DT_EQ = "CREAFRDT";
	public static final String PARAM_CREAT_TO_DT_EQ = "CREATODT";
	public static final String PARAM_PUB_FRM_DT_EQ = "PUBFRDT";
	public static final String PARAM_PUB_TO_DT_EQ = "PUBTODT";
	public static final String PARAM_PROCURE_METHOD_EQ = "PM";
	public static final String PARAM_PROCURE_STATUS_EQ = "STATUS";
	public static final String PARAM_DESK_OFFCR_EQ = "DOUSERID";
	public static final String PARAM_PROCURE_PTJ_EQ = "ORGPROFILEID";
	public static final String PARAM_SUBMIT_FR_DT = "SUBMITFRDT";
	public static final String PARAM_SUBMIT_TO_DT = "SUBMITTODT";
	public static final String PARAM_LOGIN = "LOGIN";

	/**
	 * @return the SC_QUOT_TENDER_OR_RPT_VAL
	 */
	public  String getSC_QUOT_TENDER_OR_RPT_VAL() {
		return SC_QUOT_TENDER_OR_RPT_VAL;
	}
	/**
	 * @return the SC_SUMMRY_SRC_OR_RPT_VAL
	 */
	public  String getSC_SUMMRY_SRC_OR_RPT_VAL() {
		return SC_SUMMRY_SRC_OR_RPT_VAL;
	}
	/**
	 * @return the SC_BRIF_SITE_VISIT_OR_RPT_VAL
	 */
	public  String getSC_BRIF_SITE_VISIT_OR_RPT_VAL() {
		return SC_BRIF_SITE_VISIT_OR_RPT_VAL;
	}
	/**
	 * @return the SC_QUOT_TENDER_MONITOR_OR_RPT_VAL
	 */
	public  String getSC_QUOT_TENDER_MONITOR_OR_RPT_VAL() {
		return SC_QUOT_TENDER_MONITOR_OR_RPT_VAL;
	}
	/**
	 * @return the SC_PRAPOSAL_SUB_LST_OR_RPT_VAL
	 */
	public  String getSC_PRAPOSAL_SUB_LST_OR_RPT_VAL() {
		return SC_PRAPOSAL_SUB_LST_OR_RPT_VAL;
	}
	/**
	 * @return the SC_PRICE_SCHEDULE_OR_RPT_VAL
	 */
	public  String getSC_PRICE_SCHEDULE_OR_RPT_VAL() {
		return SC_PRICE_SCHEDULE_OR_RPT_VAL;
	}
	/**
	 * @return the SC_LETTER_OF_ACCPT_OR_RPT_VAL
	 */
	public  String getSC_LETTER_OF_ACCPT_OR_RPT_VAL() {
		return SC_LETTER_OF_ACCPT_OR_RPT_VAL;
	}
	/**
	 * @return the SC_COMMITTEE_DTLS_TD_RPT_VAL
	 */
	public  String getSC_COMMITTEE_DTLS_TD_RPT_VAL() {
		return SC_COMMITTEE_DTLS_TD_RPT_VAL;
	}
	/**
	 * @return the SC_PROPOSAL_DTLS_PRODUCT_RPT_VAL
	 */
	public  String getSC_PROPOSAL_DTLS_PRODUCT_RPT_VAL() {
		return SC_PROPOSAL_DTLS_PRODUCT_RPT_VAL;
	}
	/**
	 * @return the SC_PROPOSAL_DTLS_SERVICE_RPT_VAL
	 */
	public  String getSC_PROPOSAL_DTLS_SERVICE_RPT_VAL() {
		return SC_PROPOSAL_DTLS_SERVICE_RPT_VAL;
	}
	/**
	 * @return the SC_BRIEFING_PAPER_RPT_VAL
	 */
	public  String getSC_BRIEFING_PAPER_RPT_VAL() {
		return SC_BRIEFING_PAPER_RPT_VAL;
	}
	/**
	 * @return the SC_EVALUATION_COMMITTEE_RPT_VAL
	 */
	public  String getSC_EVALUATION_COMMITTEE_RPT_VAL() {
		return SC_EVALUATION_COMMITTEE_RPT_VAL;
	}
	/**
	 * @return the SC_OPENING_COMMITTEE_RPT_VAL
	 */
	public  String getSC_OPENING_COMMITTEE_RPT_VAL() {
		return SC_OPENING_COMMITTEE_RPT_VAL;
	}
	/**
	 * @return the SC_TECHINICAL_EVALUATION_COMMITTEE_RPT_VAL
	 */
	public  String getSC_TECHINICAL_EVALUATION_COMMITTEE_RPT_VAL() {
		return SC_TECHINICAL_EVALUATION_COMMITTEE_RPT_VAL;
	}
	/**
	 * @return the SC_FINANCIAL_EVALUATION_COMMITTEE_RPT_VAL
	 */
	public  String getSC_FINANCIAL_EVALUATION_COMMITTEE_RPT_VAL() {
		return SC_FINANCIAL_EVALUATION_COMMITTEE_RPT_VAL;
	}
	/**
	 * @return the SC_LETTER_OF_ACCPT_CONTENT_RPT_VAL
	 */
	public  String getSC_LETTER_OF_ACCPT_CONTENT_RPT_VAL() {
		return SC_LETTER_OF_ACCPT_CONTENT_RPT_VAL;
	}

	public String getSC_PROP_VALID_PERIOD_EXT_RPT_VAL() {
		return SC_PROP_VALID_PERIOD_EXT_RPT_VAL;
	}
}
