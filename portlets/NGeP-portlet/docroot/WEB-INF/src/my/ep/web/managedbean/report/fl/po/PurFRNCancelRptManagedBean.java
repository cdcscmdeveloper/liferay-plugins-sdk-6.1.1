/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.po;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for PurFRNCancelRpt.
 *
 * @author NORIZAN
 * @since 30/5/2017
 */
@ManagedBean(name = "purFRNCancelRptManagedBean")
@SessionScoped
public class PurFRNCancelRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	
	public PurFRNCancelRptManagedBean(){}
	
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
