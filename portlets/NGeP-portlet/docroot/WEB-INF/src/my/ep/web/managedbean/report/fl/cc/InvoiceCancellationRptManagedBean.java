/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.cc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for FlReport.
 *
 * @author KK LIM
 * @since 26/4/2017
 */
@ManagedBean(name = "invCancelRptManagedBean")
@SessionScoped
public class InvoiceCancellationRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	
	public InvoiceCancellationRptManagedBean(){}
	
	private String invNo;
	private String supplierInvNo;

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
}
