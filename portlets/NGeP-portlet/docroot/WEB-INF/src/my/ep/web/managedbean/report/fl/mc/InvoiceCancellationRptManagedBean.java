/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.mc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for InvoiceCancellationRpt.
 *
 * @author Jimmy
 * @since 26/4/2017
 */
@ManagedBean(name = "mcInvCancelRptManagedBean")
@SessionScoped
public class InvoiceCancellationRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	
	public InvoiceCancellationRptManagedBean(){}
	
	private String invNo;
	private String supplierInvNo;
	private String reportUrl;

	public String getInvNo() {
		return invNo;
	}
	public void setInvNo(String invNo) {
		this.invNo = invNo;
	}
	public String getSupplierInvNo() {
		return supplierInvNo;
	}
	public void setSupplierInvNo(String supplierInvNo) {
		this.supplierInvNo = supplierInvNo;
	}
	
	public String getReportUrl() {
		return reportUrl;
	}
	
	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}
}
