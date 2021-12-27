package my.ep.web.managedbean.report.fl.tf;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmAgOffice;
import my.ep.persistence.entity.pm.integration.PmInterfaceAgOff;
import my.ep.web.bean.pm.AccountingOfficeSearchBean;
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
	private String offDropdown;
	private String isAllAgOff;
	private String inputAgOff;
	
	//for searchbean ag office
	private AccountingOfficeSearchBean accountingOfficeSearchBean;
	private PmAgOffice pmAccountingOffice;
	private String accountingOfficeCodes;
	private boolean isLazy;
	private Long status;
	
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

	public AccountingOfficeSearchBean getAccountingOfficeSearchBean() {
		return accountingOfficeSearchBean;
	}

	public void setAccountingOfficeSearchBean(AccountingOfficeSearchBean accountingOfficeSearchBean) {
		this.accountingOfficeSearchBean = accountingOfficeSearchBean;
	}

	public PmAgOffice getPmAccountingOffice() {
		return pmAccountingOffice;
	}

	public void setPmAccountingOffice(PmAgOffice pmAccountingOffice) {
		this.pmAccountingOffice = pmAccountingOffice;
	}

	public String getAccountingOfficeCodes() {
		return accountingOfficeCodes;
	}

	public void setAccountingOfficeCodes(String accountingOfficeCodes) {
		this.accountingOfficeCodes = accountingOfficeCodes;
	}

	public boolean isLazy() {
		return isLazy;
	}

	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public String getOffDropdown() {
		return offDropdown;
	}

	public void setOffDropdown(String offDropdown) {
		this.offDropdown = offDropdown;
	}

	public String getIsAllAgOff() {
		return isAllAgOff;
	}

	public void setIsAllAgOff(String isAllAgOff) {
		this.isAllAgOff = isAllAgOff;
	}

	public String getInputAgOff() {
		return inputAgOff;
	}

	public void setInputAgOff(String inputAgOff) {
		this.inputAgOff = inputAgOff;
	}
}
