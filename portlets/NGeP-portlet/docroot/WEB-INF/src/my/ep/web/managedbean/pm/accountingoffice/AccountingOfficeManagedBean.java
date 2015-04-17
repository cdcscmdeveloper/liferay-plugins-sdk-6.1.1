package my.ep.web.managedbean.pm.accountingoffice;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

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
	
	
	

}
