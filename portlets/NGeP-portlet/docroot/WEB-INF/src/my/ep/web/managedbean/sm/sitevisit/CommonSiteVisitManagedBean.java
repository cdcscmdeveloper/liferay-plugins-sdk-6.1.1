package my.ep.web.managedbean.sm.sitevisit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "smCommonSiteVisitMB")
@SessionScoped
public class CommonSiteVisitManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;

	private String[] bpmAction;
	private String taskName;
		
	public String[] getBpmAction() {
		return bpmAction;
	}
	public void setBpmAction(String[] bpmAction) {
		this.bpmAction = bpmAction;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
}
