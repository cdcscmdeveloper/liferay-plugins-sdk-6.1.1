/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.mc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for FRNCancellationRpt.
 *
 * @author Jimmy
 * @since 26/4/2017
 */
@ManagedBean(name = "mcFrnRptManagedBean")
@SessionScoped
public class FRNCancellationRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	
	public FRNCancellationRptManagedBean(){}
	
	private String frnNo;
	private String reportUrl;
	
	public String getFrnNo() {
		return frnNo;
	}

	public void setFrnNo(String frnNo) {
		this.frnNo = frnNo;
	}

	public String getReportUrl() {
		return reportUrl;
	}
	
	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}
}
