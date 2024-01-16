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
	public static final String SC_BRIEFING_PAPER_RPT_VAL_WIP = "SC_081";
	public static final String SC_EVALUATION_COMMITTEE_RPT_VAL = "SC_049";
	public static final String SC_EVALUATION_COMMITTEE_RPT_VAL_WIP = "SC_082";
	public static final String SC_LETTER_OF_ACCPT_CONTENT_RPT_VAL = "SC_075";
	public static final String SC_PROP_VALID_PERIOD_EXT_RPT_VAL = "SC_076";
	public static final String SC_PROP_VALID_PERIOD_EXT_LIST_RPT_VAL = "SC_080";//#21692
	public static final String SC_OPENING_COMMITTEE_RPT_VAL = "SC_071";
	public static final String SC_OPENING_COMMITTEE_RPT_VAL_WIP = "SC_084";
	public static final String SC_TECHINICAL_EVALUATION_COMMITTEE_RPT_VAL = "SC_072";
	public static final String SC_TECHINICAL_EVALUATION_COMMITTEE_RPT_VAL_WIP = "SC_085";
	public static final String SC_FINANCIAL_EVALUATION_COMMITTEE_RPT_VAL = "SC_073";
	public static final String SC_FINANCIAL_EVALUATION_COMMITTEE_RPT_VAL_WIP = "SC_083";
	public static final String SC_QUOT_TENDER_PROPOSAL_OFFER_SUBM_RPT_BY_SUPPLIER_VAL = "SC_079"; //29580_14306
	
	
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
	public static final String PARAM_DECISION_TYPE = "PSTATUSID";//#21692
	public static final String QUOTATION_TENDER_TITLE ="PQTTITLE";//#29580
	public static final String PARAM_PROPOSAL_NO ="PPROPOSALNO";//#14306
	public static final String PARAM_SUPPLIER_ID ="P_SUPP";//#14306

	
	// 29580 - Start (Harun) Add report status for mutiple option 
	//Report status
	public static final String STATUS_ALL = "1";
	public static final String STATUS_PREPARATION = "2";
	public static final String STATUS_PUBLISHING = "3";
	public static final String STATUS_PENDING_EVALUATION = "4";
	public static final String STATUS_PENDING_SUPPLIER_FINALIZATION = "5";
	public static final String STATUS_PENDING_LOILOA = "6";
	public static final String STATUS_LOA_COMPLETED = "7";
	public static final String STATUS_CANCELLED = "8";
	// 29580 - End (Harun)
	
	// 14306 - Start (Harun) Add report status for mutiple option 
	//Report status
	public static final String STATUS_PENDING_PROPOSAL_SUBMISSION = "9";
	public static final String STATUS_PROPOSAL_SUBMITTED = "10";
	// 14306 - End (Harun)
	
	// 29580 - Report status label constant
	public static final String LABEL_ALL = "lbl.rpt.all";
	public static final String LABEL_PREPARATION = "lbl.rpt.preparation";
	public static final String LABEL_PUBLISHING = "lbl.rpt.publishing";
	public static final String LABEL_PENDING_EVALUATION = "lbl.rpt.pendingEvaluation";
	public static final String LABEL_PENDING_SUPPLIER_FINALIZATION = "lbl.rpt.pendingSupplierFinalization";
	public static final String LABEL_PENDING_LOILOA = "lbl.rpt.pendingLOInLOA";
	public static final String LABEL_LOA_COMPLETED = "lbl.rpt.loaCompleted";
	public static final String LABEL_CANCELLED = "lbl.rpt.drpdown.cancelledLabel"; 
	// 29580 - End (Harun)
	
	// 14306 - Report status label constant
	public static final String LABEL_PENDING_PROPOSAL_SUBMISSION = "lbl.rpt.pending.proposal.submission";
	public static final String LABEL_PROPOSAL_SUBMITTED  = "lbl.rpt.proposal.submitted";
	// 14306 - End (Harun)
	
	
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
	
	//#21692 - start
	public String getSC_PROP_VALID_PERIOD_EXT_LIST_RPT_VAL() {
		return SC_PROP_VALID_PERIOD_EXT_LIST_RPT_VAL;
	}
	//#21692 - end
	
	//#29580_14306 - start
	public String getScQuotTenderProposalOfferSubmRptBySupplierVal() {//Changes by Idham(Sonarqube issue55)
		return SC_QUOT_TENDER_PROPOSAL_OFFER_SUBM_RPT_BY_SUPPLIER_VAL;
	}
	//#29580_14306 - end
	
	//#35015-start
	/**
	 * @return the scEvaluationCommitteeRptValWip
	 */
	public String getScEvaluationCommitteeRptValWip() {
	    return SC_EVALUATION_COMMITTEE_RPT_VAL_WIP;
	}

	/**
	 * @return the scOpeningCommitteeRptValWip
	 */
	public String getScOpeningCommitteeRptValWip() {
	    return SC_OPENING_COMMITTEE_RPT_VAL_WIP;
	}

	/**
	 * @return the scTechinicalEvaluationCommitteeRptValWip
	 */
	public String getScTechinicalEvaluationCommitteeRptValWip() {
	    return SC_TECHINICAL_EVALUATION_COMMITTEE_RPT_VAL_WIP;
	}

	/**
	 * @return the scFinancialEvaluationCommitteeRptValWip
	 */
	public String getScFinancialEvaluationCommitteeRptValWip() {
	    return SC_FINANCIAL_EVALUATION_COMMITTEE_RPT_VAL_WIP;
	}
	//#35015 -end

	public String getScBriefingPaperRptValWip() {
	    return SC_BRIEFING_PAPER_RPT_VAL_WIP;
	}
}
