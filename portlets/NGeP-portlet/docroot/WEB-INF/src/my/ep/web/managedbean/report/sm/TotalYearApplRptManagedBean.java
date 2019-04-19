package my.ep.web.managedbean.report.sm;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name = "totalYearApplRptMB")
@SessionScoped
public class TotalYearApplRptManagedBean extends SmReportManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2236785264233902436L;
	private List<SelectItem> monthList;
	private String monthFrom;
	private String monthTo;

	public List<SelectItem> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<SelectItem> monthList) {
		this.monthList = monthList;
	}

	public String getMonthFrom() {
		return monthFrom;
	}

	public void setMonthFrom(String monthFrom) {
		this.monthFrom = monthFrom;
	}

	public String getMonthTo() {
		return monthTo;
	}

	public void setMonthTo(String monthTo) {
		this.monthTo = monthTo;
	}

}
