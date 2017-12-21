/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.cc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for DeliveryOrderRpt.
 *
 * @author KK LIM
 * @since 26/4/2017
 */
@ManagedBean(name = "doRptManagedBean")
@SessionScoped
public class DeliveryOrderRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	
	public DeliveryOrderRptManagedBean(){}
	
	private String itemName;
	private String deliveryOrderNo;
	private String supplierDoRefNo;
	private String reportUrl;
	
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

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
