package my.ep.web.managedbean.report.fl.pa;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;

/**
 * Managed Bean for Item Panel Threshold 
 * 
 * @author luqmanhakim
 * @since 13/10/2025
 *
 */
@ManagedBean(name = "itemPanelThresholdRptManagedBean")
@SessionScoped
public class ItemPanelThresholdRptManagedBean extends FlReportManagedBean {
	
	private static final long serialVersionUID = 1L;
	private String reportUrl;
	private String procurementTypeCode;
	private String financialYear;

	public ItemPanelThresholdRptManagedBean(){}

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
	
	
}
