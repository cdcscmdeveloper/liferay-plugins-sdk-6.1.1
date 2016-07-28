package my.ep.web.managedbean.ct.dashboard;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name="contractDashboardManagedBean")
@SessionScoped
public class ContractDashboardManagedBean extends BaseManagedBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int lessThan30Days;
	private int lessThan120Days;

	public int getLessThan30Days() {
		return lessThan30Days;
	}

	public void setLessThan30Days(int lessThan30Days) {
		this.lessThan30Days = lessThan30Days;
	}

	public int getLessThan120Days() {
		return lessThan120Days;
	}

	public void setLessThan120Days(int lessThan120Days) {
		this.lessThan120Days = lessThan120Days;
	}
}
