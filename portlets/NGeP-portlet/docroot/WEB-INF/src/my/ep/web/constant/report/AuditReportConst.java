/**
 * 
 */
package my.ep.web.constant.report;

import java.io.Serializable;

/**
 * Report Constant used for generating report in PDF for Audit Trail Details
 * Transaction Record.
 * 
 * @author norulhaidah
 * @since 22/04/2024
 */
public class AuditReportConst implements Serializable {

	private static final long serialVersionUID = 1L;

	// Report Parameter Key
	public static final String PARAM_USER_ID = "PUSER";
	public static final String PARAM_DOC_ID = "P_DOCID";
	public static final String PARAM_DOC_TYPE = "P_DOCTYPE"; //35508
	public static final String PARAM_CT_TYPE = "P_CT_TYPE"; //35506
	public static final String PARAM_PROCESS = "P_PROCESSNAME"; //35509
	// #28895_35501 - AUDIT TRAIL SM
	public static final String PARAM_GROUP_SECTION_ID = "P_GROUP_SECTION_ID";
	
	//JSP Report for Audit Trail Details for all module
	public static final String RPT_AUDIT_TRAIL_DP = "AU_DP.jsp";
	public static final String RPT_AUDIT_TRAIL_FL = "AU_FL.jsp"; //35508
	public static final String RPT_AUDIT_TRAIL_QT = "AU_QT.jsp"; //35500
	public static final String RPT_AUDIT_TRAIL_CM = "AU_CM.jsp"; //35503
	public static final String RPT_AUDIT_TRAIL_PM = "AU_PM.jsp"; //35509
	public static final String RPT_AUDIT_TRAIL_PP = "AU_PP.jsp"; //35502
	public static final String RPT_AUDIT_TRAIL_CT = "AU_CT.jsp"; //35506
	// #28895_35501 - AUDIT TRAIL SM
	public static final String RPT_AUDIT_TRAIL_SM = "AU_SM.jsp";
	
	private AuditReportConst(){
		
	}

}
