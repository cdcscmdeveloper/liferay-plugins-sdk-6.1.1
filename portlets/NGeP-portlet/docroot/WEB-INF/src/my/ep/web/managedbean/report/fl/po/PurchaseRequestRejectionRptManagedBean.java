/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.po;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for PurchaseRequestRejectionRpt.
 *
 * @author NORIZAN
 * @since 30/5/2017
 */
@ManagedBean(name = "prRejectRptManagedBean")
@SessionScoped
public class PurchaseRequestRejectionRptManagedBean extends FlReportManagedBean{
	
	private String purchaseRequestNo;
	private String reportUrl;

	private static final long serialVersionUID = 1L;
	
	public PurchaseRequestRejectionRptManagedBean(){}
	
	public String getPurchaseRequestNo() {
		return purchaseRequestNo;
	}

	public void setPurchaseRequestNo(String purchaseRequestNo) {
		this.purchaseRequestNo = purchaseRequestNo;
	}
	
	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

}
