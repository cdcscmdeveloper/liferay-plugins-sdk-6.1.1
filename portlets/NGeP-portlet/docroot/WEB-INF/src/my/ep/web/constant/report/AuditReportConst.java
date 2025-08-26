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
	
	// #28895_35501 - AUDIT TRAIL SM
	public static final String PARAM_GROUP_SECTION_ID = "P_GROUP_SECTION_ID";
	
	public static final String PARAM_DOC_TYPE = "P_DOCTYPE"; //35508
	
	//JSP Report for Audit Trail Details for all module
	public static final String RPT_AUDIT_TRAIL_DP = "AU_DP.jsp";
	public static final String RPT_AUDIT_TRAIL_FL = "AU_FL.jsp"; //35508
	public static final String RPT_AUDIT_TRAIL_CM = "AU_CM.jsp"; //35503
	
	// #28895_35501 - AUDIT TRAIL SM
	public static final String RPT_AUDIT_TRAIL_SM = "AU_SM.jsp";

	private AuditReportConst(){
		
	}

}
