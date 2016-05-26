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
	
	public Long ctExpInDays;

	public Long getCtExpInDays() {
		return ctExpInDays;
	}

	public void setCtExpInDays(Long ctExpInDays) {
		this.ctExpInDays = ctExpInDays;
	}
	
	
}
