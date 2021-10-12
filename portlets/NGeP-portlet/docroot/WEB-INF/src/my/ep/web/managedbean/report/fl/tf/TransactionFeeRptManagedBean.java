package my.ep.web.managedbean.report.fl.tf;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.integration.PmInterfaceAgOff;
import my.ep.web.managedbean.report.fl.FlReportManagedBean;

/**
 * Managed Bean for TransactionFeeRpt.
 *
 * @author razin
 * @since 08/06/2021
 */
@ManagedBean(name = "tfRptManagedBean")
@SessionScoped
public class TransactionFeeRptManagedBean extends FlReportManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String reportUrl;
	private PmInterfaceAgOff agOff;
	private Date transactionFromDate;
	private Date transactionToDate;
	private String documentType;
	private boolean checkSFOneOff;
	private boolean checkSFPeriodic;
	
	public TransactionFeeRptManagedBean(){}

	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

	public PmInterfaceAgOff getAgOff() {
		return agOff;
	}

	public void setAgOff(PmInterfaceAgOff agOff) {
		this.agOff = agOff;
	}

	public Date getTransactionFromDate() {
		return transactionFromDate;
	}

	public void setTransactionFromDate(Date transactionFromDate) {
		this.transactionFromDate = transactionFromDate;
	}

	public Date getTransactionToDate() {
		return transactionToDate;
	}

	public void setTransactionToDate(Date transactionToDate) {
		this.transactionToDate = transactionToDate;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public boolean isCheckSFOneOff() {
		return checkSFOneOff;
	}

	public void setCheckSFOneOff(boolean checkSFOneOff) {
		this.checkSFOneOff = checkSFOneOff;
	}

	public boolean isCheckSFPeriodic() {
		return checkSFPeriodic;
	}

	public void setCheckSFPeriodic(boolean checkSFPeriodic) {
		this.checkSFPeriodic = checkSFPeriodic;
	}

}
