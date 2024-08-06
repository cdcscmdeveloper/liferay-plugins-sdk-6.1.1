package my.ep.web.managedbean.report.sm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import java.util.Date;

@ManagedBean(name="applRevisedRptMB")
@SessionScoped
public class ApplRevisedRptManagedBean extends SmReportManagedBean{
	private static final long serialVersionUID = 1L;
	
	private boolean flag;
	private Date minToDate;
	private Date maxToDate;
	private Date minFromDate;
	private Date maxFromDate;

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public Date getMaxToDate() {
		return maxToDate;
	}

	public void setMaxToDate(Date maxToDate) {
		this.maxToDate = maxToDate;
	}

	public Date getMinFromDate() {
		return minFromDate;
	}

	public void setMinFromDate(Date minFromDate) {
		this.minFromDate = minFromDate;
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
	
}
