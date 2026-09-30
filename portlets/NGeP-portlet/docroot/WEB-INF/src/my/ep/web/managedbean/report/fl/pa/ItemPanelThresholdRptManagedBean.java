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
	private Long procurementTypeId;
	private String financialYear;

	public ItemPanelThresholdRptManagedBean(){}

	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

	public Long getProcurementTypeId() {
		return procurementTypeId;
	}

	public void setProcurementTypeId(Long procurementTypeId) {
		this.procurementTypeId = procurementTypeId;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}
	
	
}