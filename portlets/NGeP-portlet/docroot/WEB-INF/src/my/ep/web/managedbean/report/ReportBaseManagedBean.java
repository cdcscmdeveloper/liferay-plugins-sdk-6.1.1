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

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.web.backingbean.rpt.ReportUtil;
import my.ep.web.managedbean.BaseManagedBean;

@SessionScoped
public class ReportBaseManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private List<SelectItem> reportCategoryList = new ArrayList<SelectItem>();
	
	private List<SelectItem> reportList = new ArrayList<SelectItem>();
	
	private String reportCategory;
	
	private String reportCode;
	
	private String reportName;
	
	private boolean tempAccess;

	private boolean pdf;
	
	private String popUpMsg;
	
	private String defaultOutputType;
	
	private String reportTitle;
	
	private String reportUrl;
	
	/**
	 * @return the reportCategoryList
	 */
	public List<SelectItem> getReportCategoryList() {
		
		if(reportCategoryList==null){
			reportCategoryList = new ArrayList<SelectItem>();
		}
		
		return reportCategoryList;
	}

	
	
	/**
	 * @param reportCategoryList the reportCategoryList to set
	 */
	public void setReportCategoryList(List<SelectItem> reportCategoryList) {
		this.reportCategoryList = reportCategoryList;
	}
	
	/**
	 * @return the defaultOutputType
	 */
	public String getDefaultOutputType() {
		return defaultOutputType;
	}

	/**
	 * @param defaultOutputType the defaultOutputType to set
	 */
	public void setDefaultOutputType(String defaultOutputType) {
		this.defaultOutputType = defaultOutputType;
	}

	/**
	 * @return the popUpMsg
	 */
	public String getPopUpMsg() {
		return popUpMsg;
	}

	/**
	 * @param popUpMsg the popUpMsg to set
	 */
	public void setPopUpMsg(String popUpMsg) {
		this.popUpMsg = popUpMsg;
	}

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
	 * @return the reportCategory
	 */
	public String getReportCategory() {
		return reportCategory;
	}

	/**
	 * @param reportCategory the reportCategory to set
	 */
	public void setReportCategory(String reportCategory) {
		this.reportCategory = reportCategory;
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

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}
	
	public void setReportUrl(String reportUrl) {
		if(isShowReportUrl()){
			this.reportUrl = reportUrl;
		}
	}
	
	public String getReportUrl() {
		return reportUrl;
	}
	
	public static boolean isShowReportUrl(){
		
		try{
		
			String ip = Inet4Address.getLocalHost().getHostAddress();
			//System.out.println("ntf ip = " + ip);
			
			if(ip != null){
				
				if(ip.equals("192.168.120.15")){
					return false;
				} 
				else
				if(ip.equals("192.168.120.19")){
					return false;
				} 
				else
				if(ip.equals("192.168.62.15")){
					return false;
				}
				else
				if(ip.equals("192.168.62.18")){
					return false;
				}
				else
				if(ip.equals("192.168.63.17")){
					return false;
				} else {
					// Jimmy : set this to true if you want to see the 
					//	       report url in your local laptop.
					return false;
				}
			}
			
		} catch (Exception ex){
			System.err.println(ex.toString());
		}
		
		return false;
	}
}
