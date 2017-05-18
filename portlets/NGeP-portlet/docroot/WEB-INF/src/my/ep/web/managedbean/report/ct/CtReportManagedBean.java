package my.ep.web.managedbean.report.ct;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * The Class CtReportManagedBean.
 * 
 * @author Nancy Ang
 */
@ManagedBean(name = "ctReportMB")
@SessionScoped
public class CtReportManagedBean extends CtReportBaseManagedBean {
	
	private static final long serialVersionUID = 1L;	

	private Date fromDate;
	private Date toDate;

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
