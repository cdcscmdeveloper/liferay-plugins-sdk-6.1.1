/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.po;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for purchaseInvPtjRpt.
 *
 * @author NORIZAN
 * @since 29/5/2017
 */
@ManagedBean(name = "purchaseInvPtjRptManagedBean")
@SessionScoped
public class PurchaseInvPtjRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	
	public PurchaseInvPtjRptManagedBean(){}
	
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
