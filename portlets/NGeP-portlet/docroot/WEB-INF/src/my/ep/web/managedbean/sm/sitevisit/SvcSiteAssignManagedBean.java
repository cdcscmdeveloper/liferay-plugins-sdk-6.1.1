package my.ep.web.managedbean.sm.sitevisit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmApplSiteVisit;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "siteVisitAssignMB")
@SessionScoped
public class SvcSiteAssignManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private SmApplSiteVisit smApplSv;
	private long appId;
	private String[] bpmAction;
	private String taskId;

	public SmApplSiteVisit getSmApplSv() {
		return smApplSv;
	}

	public void setSmApplSv(SmApplSiteVisit smApplSv) {
		this.smApplSv = smApplSv;
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public String[] getBpmAction() {
		return bpmAction;
	}

	public void setBpmAction(String[] bpmAction) {
		this.bpmAction = bpmAction;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
