/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.po;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for purPmRpt.
 *
 * @author NORIZAN
 * @since 30/5/2017
 */
@ManagedBean(name = "purPmRptManagedBean")
@SessionScoped
public class PurPaymentMatchRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	
	public PurPaymentMatchRptManagedBean(){}
	
	private String invNo;
	private String supplierInvNo;
	private String paymentMatchNo;
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
	public String getPaymentMatchNo() {
		return paymentMatchNo;
	}
	public void setPaymentMatchNo(String paymentMatchNo) {
		this.paymentMatchNo = paymentMatchNo;
	}
	
	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}
	
}
