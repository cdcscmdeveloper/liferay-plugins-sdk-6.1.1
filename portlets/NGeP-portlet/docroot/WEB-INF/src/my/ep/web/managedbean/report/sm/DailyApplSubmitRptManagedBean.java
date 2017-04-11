package my.ep.web.managedbean.report.sm;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name = "dqailyApplSubmitRptMB")
@SessionScoped
public class DailyApplSubmitRptManagedBean extends SmReportManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3600160373567150282L;
	private List<SelectItem> applStatusList;
	private String applStatus;

	public List<SelectItem> getApplStatusList() {
		return applStatusList;
	}

	public void setApplStatusList(List<SelectItem> applStatusList) {
		this.applStatusList = applStatusList;
	}

	public String getApplStatus() {
		return applStatus;
	}

	public void setApplStatus(String applStatus) {
		this.applStatus = applStatus;
	}

}
