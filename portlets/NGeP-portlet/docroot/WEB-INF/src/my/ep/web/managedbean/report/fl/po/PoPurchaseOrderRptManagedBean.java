/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.po;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for PoPurchaseOrderRpt.
 *
 * @author NORIZAN
 * @since 26/5/2017
 */
@ManagedBean(name = "poPurRptManagedBean")
@SessionScoped
public class PoPurchaseOrderRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	private String reportUrl;
	
	// #27476 start
	private String procumentType;
	// #27476 end
	
	public PoPurchaseOrderRptManagedBean(){}
	
	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

	/**
	 * @return the procumentType
	 */
	public String getProcumentType() {
		return procumentType;
	}

	/**
	 * @param procumentType the procumentType to set
	 */
	public void setProcumentType(String procumentType) {
		this.procumentType = procumentType;
	}
	
}
