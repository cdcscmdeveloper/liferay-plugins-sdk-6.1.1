/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.common;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean For Common
 * 
 * @author kieyun
 * @since 27/10/2014	 
 */
@ManagedBean(name="commonManagedBean")
@SessionScoped
public class CommonManagedBean extends BaseManagedBean{

	private static final long serialVersionUID = 1L;
	
	private String reportName;
	private String reportDisplayName;
	private Map<String, String> paramMap;
	
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getReportDisplayName() {
		return reportDisplayName;
	}
	public void setReportDisplayName(String reportDisplayName) {
		this.reportDisplayName = reportDisplayName;
	}
	public Map<String, String> getParamMap() {
		return paramMap;
	}
	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}
	
}
