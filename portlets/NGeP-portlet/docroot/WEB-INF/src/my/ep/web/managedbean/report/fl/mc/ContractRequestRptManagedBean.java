/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.mc;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for ContractRequestRpt.
 *
 * @author Jimmy
 * @since 26/4/2017
 */
@ManagedBean(name = "mcCrRptManagedBean")
@SessionScoped
public class ContractRequestRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	private List<String> highRoleCodeList = new ArrayList<String>();
	private List<SelectItem> requisitionerList = new ArrayList<SelectItem>();
	private String requisitioner;
	private String reportUrl;
	
	public ContractRequestRptManagedBean(){}
	
	public List<String> getHighRoleCodeList() {
		if(highRoleCodeList == null){
			setHighRoleCodeList(new ArrayList<String>());
		}
		return highRoleCodeList;
	}
	public void setHighRoleCodeList(List<String> highRoleCodeList) {
		this.highRoleCodeList = highRoleCodeList;
	}

	public List<SelectItem> getRequisitionerList() {
		return requisitionerList;
	}

	public void setRequisitionerList(List<SelectItem> requisitionerList) {
		this.requisitionerList = requisitionerList;
	}

	public String getRequisitioner() {
		return requisitioner;
	}

	public void setRequisitioner(String requisitioner) {
		this.requisitioner = requisitioner;
	}

	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}
}
