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
 * The Class DpReportConst.
 * 
 * @author:An Jie
 */
public class DpReportConst implements Serializable {

	private static final long serialVersionUID = 1L;

	// Report Parameter Key
	// TODO make common param key?
	public static final String PARAM_KEY_REPORT = "report";
	public static final String PARAM_USER_ID = "PUSER";
	public static final String PARAM_REPORT_ID = "PREPORTID";
	public static final String PARAM_CREATED_FROM_DATE = "CREAFRDT";
	public static final String PARAM_CREATED_TO_DATE = "CREATODT";
	public static final String PARAM_STATUS = "STATUS";
	public static final String PARAM_SUPPLIER = "SUPP";
	public static final String PARAM_REQUISITIONER_ID = "REQUIUSRID";
	public static final String PARAM_USER_ACCESS_ROLE = "LOGIN";
	public static final String PARAM_DESK_OFFICER = "DOUSERID";
	public static final String PARAM_PTJ= "ORGPROFILEID";
	
	// SC_050 Item Inquiry Details Report Param
	public static final String PARAM_GROUP = "PGROUP";
	
	public static final String PARAM_PI_NO = "PINO";
	
	// SC_069 LOA Report Param
	public static final String PARAM_SQ_TYPE = "SQTYPE";
	public static final String PARAM_LOA_NUM = "LOANUM";
	public static final String PARAM_SQ_NO = "QUOTENO";
	
	// SC_067 PID Detail Report Param
	public static final String PARAM_PID_NO = "PIDNO";
	public static final String PARAM_CONTRACT_TYPE = "CONTYPE";
	public static final String PARAM_CONTRACT_NO = "CONNO";
	public static final String PARAM_CONTRACT_NAME = "CONNM";
	
	// SC_056 SC_058 RN Detail / Summary Report Param
	public static final String PARAM_RN_NO = "RQNO";
	public static final String PARAM_PROCUREMENT_TYPE = "PROTYPE";
	
	// DP Report Code
	public static final String DP_ITEM_INQUIRY_DETAIL_RPT_VAL = "SC_050";
	public static final String DP_ITEM_INQUIRY_SUMMURY_RPT_VAL = "SC_051";
	public static final String DP_PURCHASE_IN_DEFAULT_SUMMARY_RPT_VAL = "SC_055";
	public static final String DP_REQUEST_NOTE_DETAIL_RPT_VAL = "SC_056";
	public static final String DP_REQUEST_NOTE_SUMMARY_RPT_VAL = "SC_058";
	public static final String DP_SIMPLE_QUOTE_SUMMARY_RPT_VAL = "SC_060";
	public static final String DP_SIMPLE_QUOTE_DETAIL_RPT_VAL = "SC_062";
	public static final String DP_PURCHASE_IN_DEFAULT_DETAIL_RPT_VAL = "SC_067";
	public static final String DP_SIMPLE_QUOTE_LIST_INVITED_SUPPLIER_RPT_VAL = "SC_068";
	public static final String DP_LOA_RPT_VAL = "SC_069";
	public static final String DP_LOA_CONTENT_RPT_VAL = "SC_070";
	
	public DpReportConst() {
	}

	/**
	 * @return the dpItemInquiryDetailRptVal
	 */
	public String getDpItemInquiryDetailRptVal() {
		return DP_ITEM_INQUIRY_DETAIL_RPT_VAL;
	}

	/**
	 * @return the dpItemInquirySummuryRptVal
	 */
	public String getDpItemInquirySummuryRptVal() {
		return DP_ITEM_INQUIRY_SUMMURY_RPT_VAL;
	}

	/**
	 * @return the dpPurchaseInDefaultSummaryRptVal
	 */
	public String getDpPurchaseInDefaultSummaryRptVal() {
		return DP_PURCHASE_IN_DEFAULT_SUMMARY_RPT_VAL;
	}

	/**
	 * @return the dpRequestNoteDetailRptVal
	 */
	public String getDpRequestNoteDetailRptVal() {
		return DP_REQUEST_NOTE_DETAIL_RPT_VAL;
	}

	/**
	 * @return the dpRequestNoteSummaryRptVal
	 */
	public String getDpRequestNoteSummaryRptVal() {
		return DP_REQUEST_NOTE_SUMMARY_RPT_VAL;
	}

	/**
	 * @return the dpSimpleQuoteSummaryRptVal
	 */
	public String getDpSimpleQuoteSummaryRptVal() {
		return DP_SIMPLE_QUOTE_SUMMARY_RPT_VAL;
	}

	/**
	 * @return the dpSimpleQuoteDetailRptVal
	 */
	public String getDpSimpleQuoteDetailRptVal() {
		return DP_SIMPLE_QUOTE_DETAIL_RPT_VAL;
	}

	/**
	 * @return the dpPurchaseInDefaultDetailRptVal
	 */
	public String getDpPurchaseInDefaultDetailRptVal() {
		return DP_PURCHASE_IN_DEFAULT_DETAIL_RPT_VAL;
	}

	/**
	 * @return the dpSimpleQuoteListInvitedSupplierRptVal
	 */
	public String getDpSimpleQuoteListInvitedSupplierRptVal() {
		return DP_SIMPLE_QUOTE_LIST_INVITED_SUPPLIER_RPT_VAL;
	}

	/**
	 * @return the dpLoaRptVal
	 */
	public String getDpLoaRptVal() {
		return DP_LOA_RPT_VAL;
	}

	/**
	 * @return the dpLoaContentRptVal
	 */
	public String getDpLoaContentRptVal() {
		return DP_LOA_CONTENT_RPT_VAL;
	}

}
