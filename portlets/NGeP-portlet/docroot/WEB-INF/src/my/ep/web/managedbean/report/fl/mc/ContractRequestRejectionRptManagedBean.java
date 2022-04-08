/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.mc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for ContractRequestRejectionRpt.
 *
 * @author Jimmy
 * @since 26/4/2017
 */
@ManagedBean(name = "mcCrRejectRptManagedBean")
@SessionScoped
public class ContractRequestRejectionRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	
	private String reportUrl;
	
	public ContractRequestRejectionRptManagedBean(){}

	public String getReportUrl() {
		return reportUrl;
	}
	
	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}
}
