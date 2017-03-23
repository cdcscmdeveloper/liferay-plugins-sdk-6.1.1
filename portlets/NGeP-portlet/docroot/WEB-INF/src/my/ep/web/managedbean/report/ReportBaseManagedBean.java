/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.managedbean.report;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.web.managedbean.BaseManagedBean;

@SessionScoped
public class ReportBaseManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private List<SelectItem> reportList = new ArrayList<SelectItem>();
	
	private String reportCode;

	
	
	
	
	/**
	 * @return the reportList
	 */
	public List<SelectItem> getReportList() {
		if(reportList==null){
			reportList = new ArrayList<SelectItem>();
		}
		return reportList;
	}

	/**
	 * @param reportList the reportList to set
	 */
	public void setReportList(List<SelectItem> reportList) {
		this.reportList = reportList;
	}

	/**
	 * @return the reportCode
	 */
	public String getReportCode() {
		return reportCode;
	}

	/**
	 * @param reportCode the reportCode to set
	 */
	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}
	
	
	
	
	
	

}
