/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.po;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for purDoCancelRpt.
 *
 * @author NORIZAN
 * @since 30/5/2017
 */
@ManagedBean(name = "purDoCancelRptManagedBean")
@SessionScoped
public class PurDeliveryOrderCancelRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	
	public PurDeliveryOrderCancelRptManagedBean(){}
	
	private String deliveryOrderNo;
	private String supplierDoRefNo;
	private String reportUrl;

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
