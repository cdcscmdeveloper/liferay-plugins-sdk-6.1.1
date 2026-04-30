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
	private Long procurementTypeId;
	private String financialYear;
	private boolean reasonFlag;
	private SelectItem[] reasonDropdown;
	private Long reasonId;
	
	public ItemThresholdRptManagedBean(){}
	
	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
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

	/**
	 * @return the procurementTypeId
	 */
	public Long getProcurementTypeId() {
		return procurementTypeId;
	}

	/**
	 * @param procurementTypeId the procurementTypeId to set
	 */
	public void setProcurementTypeId(Long procurementTypeId) {
		this.procurementTypeId = procurementTypeId;
	}

	/**
	 * @return the reasonId
	 */
	public Long getReasonId() {
		return reasonId;
	}

	/**
	 * @param reasonId the reasonId to set
	 */
	public void setReasonId(Long reasonId) {
		this.reasonId = reasonId;
	}
	
}
