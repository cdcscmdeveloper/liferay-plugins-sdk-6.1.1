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
@ManagedBean(name = "ldRptManagedBean")
@SessionScoped
public class LateDeliveryRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	
	public LateDeliveryRptManagedBean(){}
	
	private String itemName;
	private String deliveryOrderNo;
	private String supplierDoRefNo;

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

}
