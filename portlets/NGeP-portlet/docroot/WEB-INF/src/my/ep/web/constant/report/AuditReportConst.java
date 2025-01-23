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
	
	//JSP Report for Audit Trail Details for all module
	public static final String RPT_AUDIT_TRAIL_DP = "AU_DP.jsp";
	
	private AuditReportConst(){
		
	}

}
