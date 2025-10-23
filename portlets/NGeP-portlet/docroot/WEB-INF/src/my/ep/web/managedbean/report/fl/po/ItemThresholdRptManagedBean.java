package my.ep.web.managedbean.report.fl.po;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for Item Threshold
 * Enhancement - 31344
 * 
 * @author luqmanhakim
 * @since 18/9/2025
 *
 */
@ManagedBean(name = "itemThRptManagedBean")
@SessionScoped
public class ItemThresholdRptManagedBean extends FlReportManagedBean {
	 
	private static final long serialVersionUID = 1L;
	private String reportUrl;
	private String procurementTypeCode;
	private String financialYear;
	private boolean reasonFlag;
	private SelectItem[] reasonDropdown;
	private String reasonCode;
	
	public ItemThresholdRptManagedBean(){}
	
	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

	public String getProcurementTypeCode() {
		return procurementTypeCode;
	}

	public void setProcurementTypeCode(String procurementTypeCode) {
		this.procurementTypeCode = procurementTypeCode;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public boolean getReasonFlag() {
		return reasonFlag;
	}

	public void setReasonFlag(boolean reasonFlag) {
		this.reasonFlag = reasonFlag;
	}

	public SelectItem[] getReasonDropdown() {
		return reasonDropdown;
	}

	public void setReasonDropdown(SelectItem[] reasonDropdown) {
		this.reasonDropdown = reasonDropdown;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}
	
	

}
