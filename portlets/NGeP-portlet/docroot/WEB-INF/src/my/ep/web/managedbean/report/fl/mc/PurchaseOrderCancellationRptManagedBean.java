/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.mc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for PurchaseOrderCancellationRpt.
 *
 * @author Jimmy
 * @since 26/4/2017
 */
@ManagedBean(name = "mcPoCancelRptManagedBean")
@SessionScoped
public class PurchaseOrderCancellationRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	
	public PurchaseOrderCancellationRptManagedBean(){}
	
	private String itemName;
	private String reportUrl;
	
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public String getReportUrl() {
		return reportUrl;
	}
	
	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}
}
