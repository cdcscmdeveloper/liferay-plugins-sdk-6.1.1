/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.mc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for DeliveryOrderCancellationRpt.
 *
 * @author Jimmy
 * @since 26/4/2017
 */
@ManagedBean(name = "mcDoCancelRptManagedBean")
@SessionScoped
public class DeliveryOrderCancellationRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	
	public DeliveryOrderCancellationRptManagedBean(){}
	
	private String itemName;
	private String deliveryOrderNo;
	private String supplierDoRefNo;
	private String supplierDeliveryOrderNo;
	
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
	
	public String getSupplierDeliveryOrderNo() {
		return supplierDeliveryOrderNo;
	}

	public void setSupplierDeliveryOrderNo(String supplierDeliveryOrderNo) {
		this.supplierDeliveryOrderNo = supplierDeliveryOrderNo;
	}

}
