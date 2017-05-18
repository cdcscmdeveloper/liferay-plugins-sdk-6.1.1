package my.ep.web.managedbean.report.ct.cc;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.web.managedbean.report.ct.CtReportBaseManagedBean;

/**
 * The Class StatementOfOrderRptManagedBean.
 * 
 * @author Nancy Ang
 */
@ManagedBean(name = "statementOfOrderRptMB")
@SessionScoped
public class StatementOfOrderRptManagedBean extends CtReportBaseManagedBean {
	
	private static final long serialVersionUID = 1L;

	private PmOrgProfile selectedSupplier;
	private Date fromDate;
	private Date toDate;	
	
	/**
	 * @return the selectedSupplier
	 */
	public PmOrgProfile getSelectedSupplier() {
		return selectedSupplier;
	}
	/**
	 * @param selectedSupplier the selectedSupplier to set
	 */
	public void setSelectedSupplier(PmOrgProfile selectedSupplier) {
		this.selectedSupplier = selectedSupplier;
	}
	/**
	 * @return the fromDate
	 */
	public Date getFromDate() {
		return fromDate;
	}
	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	/**
	 * @return the toDate
	 */
	public Date getToDate() {
		return toDate;
	}
	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
}
