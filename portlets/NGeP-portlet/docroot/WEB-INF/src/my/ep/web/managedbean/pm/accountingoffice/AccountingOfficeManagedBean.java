package my.ep.web.managedbean.pm.accountingoffice;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmAgOffice;
import my.ep.web.bean.pm.AccountingOfficeSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "accountingOfficeManagedBean")
@SessionScoped
public class AccountingOfficeManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AccountingOfficeSearchBean accountingOfficeSearchBean;
	
	private PmAgOffice pmAccountingOffice;

	private String accountingOfficeCodes;

	private String accountingOfficeNames;

	private Long status;

	private boolean isLazy;
	
	/**
	 * @return the accountingOfficeSearchBean
	 */
	public AccountingOfficeSearchBean getAccountingOfficeSearchBean() {
		
		return accountingOfficeSearchBean;
	}
	/**
	 * @param accountingOfficeSearchBean the accountingOfficeSearchBean to set
	 */
	public void setAccountingOfficeSearchBean(
			AccountingOfficeSearchBean accountingOfficeSearchBean) {
		this.accountingOfficeSearchBean = accountingOfficeSearchBean;
	}
	/**
	 * @return the pmAccountingOffice
	 */
	public PmAgOffice getPmAccountingOffice() {
		return pmAccountingOffice;
	}
	/**
	 * @param pmAccountingOffice the pmAccountingOffice to set
	 */
	public void setPmAccountingOffice(PmAgOffice pmAccountingOffice) {
		this.pmAccountingOffice = pmAccountingOffice;
	}
	/**
	 * @return the accountingOfficceCodes
	 */
	public String getAccountingOfficceCodes() {
		return accountingOfficeCodes;
	}
	/**
	 * @param accountingOfficceCodes the accountingOfficceCodes to set
	 */
	public void setAccountingOfficceCodes(String accountingOfficeCodes) {
		this.accountingOfficeCodes = accountingOfficeCodes;
	}
	/**
	 * @return the accountingOfficeNames
	 */
	public String getAccountingOfficeNames() {
		return accountingOfficeNames;
	}
	/**
	 * @param accountingOfficeNames the accountingOfficeNames to set
	 */
	public void setAccountingOfficeNames(String accountingOfficeNames) {
		this.accountingOfficeNames = accountingOfficeNames;
	}
	/**
	 * @return the status
	 */
	public Long getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Long status) {
		this.status = status;
	}
	/**
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}
	/**
	 * @param isLazy the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}
	
	
	

}
