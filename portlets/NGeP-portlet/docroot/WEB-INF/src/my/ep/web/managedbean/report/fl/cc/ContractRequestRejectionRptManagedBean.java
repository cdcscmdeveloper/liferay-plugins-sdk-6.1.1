/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.cc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for ContractRequestRejectionRpt.
 *
 * @author KK LIM
 * @since 26/4/2017
 */
@ManagedBean(name = "crRejectRptManagedBean")
@SessionScoped
public class ContractRequestRejectionRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	
	public ContractRequestRejectionRptManagedBean(){}

	private String reportUrl;
	
	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}
}