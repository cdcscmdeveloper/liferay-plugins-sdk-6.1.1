package my.ep.web.managedbean.report.fl.po;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.fl.FlReportManagedBean;

/**
 * Managed Bean for PrGeneratedAsPo.
 *
 * Enhancement 29550 - Ad Hoc Report
 * @author Syafiza
 * @since 14/11/2022
 */
@ManagedBean(name = "poPrGenAsPoRptManagedBean")
@SessionScoped
public class PrGeneratedAsPoRptManagedBean extends FlReportManagedBean {
	
	private static final long serialVersionUID = 1L;
	
	public PrGeneratedAsPoRptManagedBean(){}
	
	private String reportUrl;
	private String purchaseRequestNo;
	
	private Date submissionDateFrom;
	private Date submissionDateTo;
	private Date approvedDateFrom;
	private Date approvedDateTo;
	private Date poGenDateFrom;
	private Date poGenDateTo;

	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

	public Date getSubmissionDateFrom() {
		return submissionDateFrom;
	}

	public void setSubmissionDateFrom(Date submissionDateFrom) {
		this.submissionDateFrom = submissionDateFrom;
	}

	public Date getSubmissionDateTo() {
		return submissionDateTo;
	}

	public void setSubmissionDateTo(Date submissionDateTo) {
		this.submissionDateTo = submissionDateTo;
	}

	public Date getApprovedDateFrom() {
		return approvedDateFrom;
	}

	public void setApprovedDateFrom(Date approvedDateFrom) {
		this.approvedDateFrom = approvedDateFrom;
	}

	public Date getApprovedDateTo() {
		return approvedDateTo;
	}

	public void setApprovedDateTo(Date approvedDateTo) {
		this.approvedDateTo = approvedDateTo;
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

	public String getPurchaseRequestNo() {
		return purchaseRequestNo;
	}

	public void setPurchaseRequestNo(String purchaseRequestNo) {
		this.purchaseRequestNo = purchaseRequestNo;
	}
	

}
