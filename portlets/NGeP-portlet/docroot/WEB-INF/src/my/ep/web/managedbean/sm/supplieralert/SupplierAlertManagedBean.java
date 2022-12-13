package my.ep.web.managedbean.sm.supplieralert;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "supplierAlertMB")
@SessionScoped
public class SupplierAlertManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7744602785370296622L;
	private Date expDate;
	private String mofExpiryLeftDaysMsg;
	private String days;
	private String hours;
	private String mins;
	private String secs;

	public String getMofExpiryLeftDaysMsg() {
		return mofExpiryLeftDaysMsg;
	}

	public void setMofExpiryLeftDaysMsg(String mofExpiryLeftDaysMsg) {
		this.mofExpiryLeftDaysMsg = mofExpiryLeftDaysMsg;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getMins() {
		return mins;
	}

	public void setMins(String mins) {
		this.mins = mins;
	}

	public String getSecs() {
		return secs;
	}

	public void setSecs(String secs) {
		this.secs = secs;
	}

	public Date getExpDate() {
		return expDate;
	}

	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}
}
