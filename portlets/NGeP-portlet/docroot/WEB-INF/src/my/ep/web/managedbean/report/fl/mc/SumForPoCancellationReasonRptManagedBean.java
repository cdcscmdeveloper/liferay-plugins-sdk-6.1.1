package my.ep.web.managedbean.report.fl.mc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;

/**
 * Managed Bean for SumForPoCancellationReason.
 *
 * Enhancement 29550 - Ad Hoc Report
 * @author Syafiza
 * @since 14/11/2022
 */
@ManagedBean(name = "mcSumTotalNumValofPoRptManagedBean")
@SessionScoped
public class SumForPoCancellationReasonRptManagedBean extends FlReportManagedBean {

	private static final long serialVersionUID = 1L;
	
	public SumForPoCancellationReasonRptManagedBean(){}
	
	private String reportUrl;

	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

}
