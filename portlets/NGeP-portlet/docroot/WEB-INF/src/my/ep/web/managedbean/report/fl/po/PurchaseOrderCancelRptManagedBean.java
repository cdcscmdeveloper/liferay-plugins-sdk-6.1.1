/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.po;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for PurchaseOrderCancelRpt.
 *
 * @author NORIZAN
 * @since 26/5/2017
 */
@ManagedBean(name = "poPurCancelRptManagedBean")
@SessionScoped
public class PurchaseOrderCancelRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	private String reportUrl;
	
	public PurchaseOrderCancelRptManagedBean(){}
	
	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}
	
}
