package my.ep.web.managedbean.report.fl.cc;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;

/**
 * Managed Bean for SumForTotalNumAndValOfPoByStat.
 *
 * Enhancement 29550 - Ad Hoc Report
 * @author Syafiza
 * @since 14/11/2022
 */
@ManagedBean(name = "ccSumTotalNumValofPoRptManagedBean")
@SessionScoped
public class SumForTotalNumAndValOfPoByStatRptManagedBean extends FlReportManagedBean {
	
	private static final long serialVersionUID = 1L;
	
	public SumForTotalNumAndValOfPoByStatRptManagedBean(){}
	
	private String reportUrl;
	private String purchaseOrderStatus;
	
	private Date poGenDateFrom;
	private Date poGenDateTo;
	
	//29550 start
	private String groupStatus;
	//29550 end

	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

	public String getPurchaseOrderStatus() {
		return purchaseOrderStatus;
	}

	public void setPurchaseOrderStatus(String purchaseOrderStatus) {
		this.purchaseOrderStatus = purchaseOrderStatus;
	}

	public Date getPoGenDateFrom() {
		return poGenDateFrom;
	}

	public void setPoGenDateFrom(Date poGenDateFrom) {
		this.poGenDateFrom = poGenDateFrom;
	}

	public Date getPoGenDateTo() {
		return poGenDateTo;
	}

	public void setPoGenDateTo(Date poGenDateTo) {
		this.poGenDateTo = poGenDateTo;
	}

	public String getGroupStatus() {
		return groupStatus;
	}

	public void setGroupStatus(String groupStatus) {
		this.groupStatus = groupStatus;
	}
	
	

}
