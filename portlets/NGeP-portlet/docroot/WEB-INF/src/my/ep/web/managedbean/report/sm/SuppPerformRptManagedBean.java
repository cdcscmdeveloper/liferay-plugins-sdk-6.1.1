package my.ep.web.managedbean.report.sm;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "suppPerformRptMB")
@SessionScoped
public class SuppPerformRptManagedBean extends SmReportManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7910328254638800865L;
	private String regDateLabel;
	//#33741 - START
	private Date maxToDate; 
	private Date minToDate; 
	private Date maxFromDate;
	private Date minFromDate;
	//#33741 - END

	public String getRegDateLabel() {
		return regDateLabel;
	}

	public void setRegDateLabel(String regDateLabel) {
		this.regDateLabel = regDateLabel;
	}

	//#33741 - START
	public Date getMaxToDate() {
		return maxToDate;
	}

	public void setMaxToDate(Date maxToDate) {
		this.maxToDate = maxToDate;
	}

	public Date getMinToDate() {
		return minToDate;
	}

	public void setMinToDate(Date minToDate) {
		this.minToDate = minToDate;
	}

	public Date getMaxFromDate() {
		return maxFromDate;
	}

	public void setMaxFromDate(Date maxFromDate) {
		this.maxFromDate = maxFromDate;
	}

	public Date getMinFromDate() {
		return minFromDate;
	}

	public void setMinFromDate(Date minFromDate) {
		this.minFromDate = minFromDate;
	}
	
	//#33741 - END
}
