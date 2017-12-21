/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.po;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for PurchaseDeliveryRpt.
 *
 * @author NORIZAN
 * @since 26/5/2017
 */
@ManagedBean(name = "pdRptManagedBean")
@SessionScoped
public class PurchaseDeliveryRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	private String reportUrl;
	
	public PurchaseDeliveryRptManagedBean(){}
	
	private String deliveryOrderNo;
	private String supplierDoRefNo;

	public String getDeliveryOrderNo() {
		return deliveryOrderNo;
	}

	public void setDeliveryOrderNo(String deliveryOrderNo) {
		this.deliveryOrderNo = deliveryOrderNo;
	}

	public String getSupplierDoRefNo() {
		return supplierDoRefNo;
	}

	public void setSupplierDoRefNo(String supplierDoRefNo) {
		this.supplierDoRefNo = supplierDoRefNo;
	}
	
	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

}
