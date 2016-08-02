/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.managedbean.pm.accountingoffice;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmAgOffice;
import my.ep.web.bean.pm.AccountingOfficeSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * The Class AccountingOfficeManagedBean.
 * 
 * @author venugopal
 * @since 17/4/2015
 */
@ManagedBean(name = "accountingOfficeManagedBean")
@SessionScoped
public class AccountingOfficeManagedBean extends BaseManagedBean {

	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The accounting office search bean. */
	private AccountingOfficeSearchBean accountingOfficeSearchBean;
	
	/** The pm accounting office. */
	private PmAgOffice pmAccountingOffice;

	/** The accounting office codes. */
	private String accountingOfficeCodes;

	/** The accounting office names. */
	private String accountingOfficeNames;

	/** The status. */
	private Long status;

	/** The is lazy. */
	private boolean isLazy;
	
	public AccountingOfficeManagedBean(){}
	
	/**
	 * Gets the accounting office search bean.
	 * 
	 * @return the accountingOfficeSearchBean
	 */
	public AccountingOfficeSearchBean getAccountingOfficeSearchBean() {
		
		return accountingOfficeSearchBean;
	}
	
	/**
	 * Sets the accounting office search bean.
	 * 
	 * @param accountingOfficeSearchBean
	 *            the accountingOfficeSearchBean to set
	 */
	public void setAccountingOfficeSearchBean(
			AccountingOfficeSearchBean accountingOfficeSearchBean) {
		this.accountingOfficeSearchBean = accountingOfficeSearchBean;
	}
	
	/**
	 * Gets the pm accounting office.
	 * 
	 * @return the pmAccountingOffice
	 */
	public PmAgOffice getPmAccountingOffice() {
		return pmAccountingOffice;
	}
	
	/**
	 * Sets the pm accounting office.
	 * 
	 * @param pmAccountingOffice
	 *            the pmAccountingOffice to set
	 */
	public void setPmAccountingOffice(PmAgOffice pmAccountingOffice) {
		this.pmAccountingOffice = pmAccountingOffice;
	}
	
	/**
	 * Gets the accounting officce codes.
	 * 
	 * @return the accountingOfficceCodes
	 */
	public String getAccountingOfficceCodes() {
		return accountingOfficeCodes;
	}
	
	/**
	 * Sets the accounting officce codes.
	 * 
	 * @param accountingOfficeCodes
	 *            the new accounting officce codes
	 */
	public void setAccountingOfficceCodes(String accountingOfficeCodes) {
		this.accountingOfficeCodes = accountingOfficeCodes;
	}
	
	/**
	 * Gets the accounting office names.
	 * 
	 * @return the accountingOfficeNames
	 */
	public String getAccountingOfficeNames() {
		return accountingOfficeNames;
	}
	
	/**
	 * Sets the accounting office names.
	 * 
	 * @param accountingOfficeNames
	 *            the accountingOfficeNames to set
	 */
	public void setAccountingOfficeNames(String accountingOfficeNames) {
		this.accountingOfficeNames = accountingOfficeNames;
	}
	
	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public Long getStatus() {
		return status;
	}
	
	/**
	 * Sets the status.
	 * 
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Long status) {
		this.status = status;
	}
	
	/**
	 * Checks if is lazy.
	 * 
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}
	
	/**
	 * Sets the lazy.
	 * 
	 * @param isLazy
	 *            the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}
	
}
