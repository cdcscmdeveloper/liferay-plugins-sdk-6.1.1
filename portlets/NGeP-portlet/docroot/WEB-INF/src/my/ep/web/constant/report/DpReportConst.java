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
	//26059 - start
	public static final String DP_ITEM_EXCEED_THRESHOLD_RPT = "SC_093";
	public static final Long PARAM_ID_SPECIAL_ITEM = 1015L;
	public static final Long PARAM_ID_PK7 = 1014L;
	public static final String REPORT_CODE_RPT_PK7 = "PK7W";
	public static final String REPORT_CODE_RPT_SPECIAL_ITEM = "SIT";
	//Report Constant
	public static final String PARAM_KEY_MINISTRY = "PMINIS";
	public static final String PARAM_KEY_KUMPULAN_PTJ = "PFJA";
	public static final String PARAM_KEY_PTJ = "PPTJ";
	public static final String PARAM_KEY_CENTRAL_PANEL_CONTRACT = "PCENTRALPANELCONTRACT";
	public static final String PARAM_KEY_CIRCULAR_NO = "PCIRCULARNO";
	public static final String PARAM_KEY_STATUS = "PSTATUS";
	public static final String PARAM_KEY_ITEM_CODE = "PITEMCODE";
	public static final String PARAM_KEY_PROCUREMENT_TYPE = "PPROCUREMENTTYPE";
	public static final String PARAM_KEY_ITEM_NAME = "PITEMNAME";
	public static final String PARAM_KEY_GROUP = "PGROUP";
	public static final String PARAM_KEY_RN_NO = "PREQUESTNOTENUMBER";
	public static final String PARAM_KEY_REASON = "PREASON";
	public static final String PARAM_KEY_FINANCIAL_YEAR = "PFINANCIALYEAR";
	public static final String PARAM_KEY_REPORT_ID = "PREPORTID";
	public static final String PARAM_KEY_USER_ID = "PUSER";
	
	//26059 - end
	
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

	//26059 - start
	/**
	 * @return the dpItemExceedThresholdRpt
	 */
	public String getDpItemExceedThresholdRpt() {
		return DP_ITEM_EXCEED_THRESHOLD_RPT;
	}
	//26059 - end

}
