/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.po;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for purSiSupplierRpt.
 *
 * @author NORIZAN
 * @since 29/5/2017
 */
@ManagedBean(name = "purSiSupplierRptManagedBean")
@SessionScoped
public class PurStopInstSuppRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	private String siNo;
	private String reportUrl;
	
	public PurStopInstSuppRptManagedBean(){}
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
