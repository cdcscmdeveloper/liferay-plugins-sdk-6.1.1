/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.po;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for PurchaseRequestRpt.
 *
 * @author NORIZAN
 * @since 25/5/2017
 */
@ManagedBean(name = "prRptManagedBean")
@SessionScoped
public class PurchaseRequestRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	private List<String> highRoleCodeList = new ArrayList<String>();
	private List<SelectItem> requisitionerList = new ArrayList<SelectItem>();
	private String requisitioner;
	private String purchaseRequestNo;
	private String reportUrl;
	
	//#27476 start
	private String procumentType;
	//#27476 end
	
	public PurchaseRequestRptManagedBean(){}
	
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
