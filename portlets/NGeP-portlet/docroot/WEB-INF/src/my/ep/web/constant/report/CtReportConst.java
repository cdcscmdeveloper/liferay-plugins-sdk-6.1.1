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
	
	// Param Key
	public static final String PARAM_CC_MINISTRY = "PMINIS";
	public static final String PARAM_CC_PEGAWAI_PEGAWAL = "PFPP";
	public static final String PARAM_CC_KUMPULAN_PTJ = "PFJA";
	public static final String PARAM_CC_PTJ = "PPTJ";
	
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
	
}
