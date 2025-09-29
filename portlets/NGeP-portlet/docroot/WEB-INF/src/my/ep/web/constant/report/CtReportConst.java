package my.ep.web.constant.report;

import java.io.Serializable;

public class CtReportConst implements Serializable {

	private static final long serialVersionUID = 1L;

	// Report Code
	public static final String CT_LIST_OF_CONTRACT_MC_RPT_VAL = "CT_003";
	public static final String CT_CONTRACT_MONITORING_RPT_VAL = "CT_006";
	public static final String CT_LIST_OF_CIRCULAR_CQ_RPT_VAL = "CT_016";
	public static final String CT_FULFILMENT_STATUS_RPT_VAL = "CT_018";
	public static final String CT_STATEMENT_OF_ORDER_CC_RPT_VAL = "CT_021";
	public static final String CT_LIST_OF_CONTRACT_CC_RPT_VAL = "CT_022";
	public static final String CT_LIST_OF_CIRCULAR_PQ_RPT_VAL = "CT_023";
	public static final String CT_STATEMENT_OF_ORDER_PQ_RPT_VAL = "CT_024";
	
	// #29416 added Contract Item Usage Monitoring Report
	public static final String CT_CONTRACT_ITEM_USAGE_MONITORING_RPT_CC_VAL = "CT_027";
	public static final String CT_CONTRACT_ITEM_USAGE_MONITORING_RPT_MC_VAL = "CT_028";
	
	// Report Parameter Key
	public static final String PARAM_KEY_REPORT = "report";
	public static final String PARAM_KEY_REPORT_ID = "PREPORTID";
	public static final String PARAM_KEY_USER_ID = "PUSER";
	public static final String PARAM_KEY_MINISTRY = "PMINIS";
//	public static final String PARAM_KEY_PEGAWAI_PEGAWAL = "PFPP";
	public static final String PARAM_KEY_KUMPULAN_PTJ = "PFJA";
	public static final String PARAM_KEY_PTJ = "PPTJ";
	public static final String PARAM_KEY_CONTRACT_NO = "PCONTRACTNO";
	public static final String PARAM_KEY_PHYSICAL_CONTRACT_NO = "PPHYSICALCONTRACTNO";
	public static final String PARAM_KEY_LOA_NO = "PLOANO";
	public static final String PARAM_KEY_LOA_PHYSICAL_NO = "PLOAPHYSICALNO";
	public static final String PARAM_KEY_AGREEMENT = "PAGREEMENT";
	public static final String PARAM_KEY_PROCUREMENT_MODE = "PPROCUREMENTMODE";
	public static final String PARAM_KEY_STATUS = "PSTATUS";
	public static final String PARAM_KEY_FROM_DATE = "PFROMDATE";
	public static final String PARAM_KEY_TO_DATE = "PTODATE";
	public static final String PARAM_KEY_SUPPLIER_NAME = "PSUPPLIERNAME";
	public static final String PARAM_KEY_CIRCULAR_NO = "PCIRCULARNO";
	public static final String PARAM_KEY_CIRCULAR_NAME = "PCIRCULARNAME";
	public static final String PARAM_KEY_CIRCULAR_FROM_DATE = "PCIRCEFFFROMDATE";
	public static final String PARAM_KEY_CIRCULAR_TO_DATE = "PCIRCEFFTODATE";
	public static final String PARAM_KEY_ITEM_NAME = "PITEMNAME";

	/**
	 * @return the ctListOfContractMcRptVal
	 */
	public String getCtListOfContractMcRptVal() {
		return CT_LIST_OF_CONTRACT_MC_RPT_VAL;
	}
	
	/**
	 * @return the ctContractMonitoringRptVal
	 */
	public String getCtContractMonitoringRptVal() {
		return CT_CONTRACT_MONITORING_RPT_VAL;
	}
	
	/**
	 * @return the ctListOfCircularCqRptVal
	 */
	public String getCtListOfCircularCqRptVal() {
		return CT_LIST_OF_CIRCULAR_CQ_RPT_VAL;
	}
	
	/**
	 * @return the ctFulfilmentStatusRptVal
	 */
	public String getCtFulfilmentStatusRptVal() {
		return CT_FULFILMENT_STATUS_RPT_VAL;
	}
	
	/**
	 * @return the ctStatementOfOrderCcRptVal
	 */
	public String getCtStatementOfOrderCcRptVal() {
		return CT_STATEMENT_OF_ORDER_CC_RPT_VAL;
	}
	
	/**
	 * @return the ctListOfContractCcRptVal
	 */
	public String getCtListOfContractCcRptVal() {
		return CT_LIST_OF_CONTRACT_CC_RPT_VAL;
	}
	
	/**
	 * @return the ctListOfCircularPqRptVal
	 */
	public String getCtListOfCircularPqRptVal() {
		return CT_LIST_OF_CIRCULAR_PQ_RPT_VAL;
	}
	/**
	 * @return the ctStatementOfOrderPqRptVal
	 */
	public String getCtStatementOfOrderPqRptVal() {
		return CT_STATEMENT_OF_ORDER_PQ_RPT_VAL;
	}
	// Start - #29416
	/**
	 * @return the ctContractItemUsageMonitoringRptCcVal
	 */
	public String getCtContractItemUsageMonitoringRptCcVal() {
		return CT_CONTRACT_ITEM_USAGE_MONITORING_RPT_CC_VAL;
	}

	/**
	 * @return the ctContractItemUsageMonitoringRptMcVal
	 */
	public String getCtContractItemUsageMonitoringRptMcVal() {
		return CT_CONTRACT_ITEM_USAGE_MONITORING_RPT_MC_VAL;
	}
	// End - #29416
}
