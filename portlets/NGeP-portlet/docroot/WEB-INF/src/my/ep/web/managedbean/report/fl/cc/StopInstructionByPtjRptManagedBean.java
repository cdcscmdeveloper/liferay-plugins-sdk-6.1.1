/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.cc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for StopInstructionByPtjRpt.
 *
 * @author KK LIM
 * @since 26/4/2017
 */
@ManagedBean(name = "siPtjRptManagedBean")
@SessionScoped
public class StopInstructionByPtjRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	private String siNo;
	private String reportUrl;
	
	public StopInstructionByPtjRptManagedBean(){}
	public String getSiNo() {
		return siNo;
	}
	public void setSiNo(String siNo) {
		this.siNo = siNo;
	}
	
	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

}
