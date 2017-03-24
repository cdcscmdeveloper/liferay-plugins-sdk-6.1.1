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
	
	private String reportName;
	
	private boolean tempAccess;

	private boolean pdf;
	
	
	
	
	
	
	
	
	/**
	 * @return the reportName
	 */
	public String getReportName() {
		return reportName;
	}

	/**
	 * @param reportName the reportName to set
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	/**
	 * @return the tempAccess
	 */
	public boolean isTempAccess() {
		return tempAccess;
	}

	/**
	 * @param tempAccess the tempAccess to set
	 */
	public void setTempAccess(boolean tempAccess) {
		this.tempAccess = tempAccess;
	}

	/**
	 * @return the pdf
	 */
	public boolean isPdf() {
		return pdf;
	}

	/**
	 * @param pdf the pdf to set
	 */
	public void setPdf(boolean pdf) {
		this.pdf = pdf;
	}

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
