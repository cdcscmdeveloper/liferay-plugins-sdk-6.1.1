package my.ep.web.managedbean.sm.sitevisit;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.jaxb.sm.sitevisitdata.SMSiteVisitDataType;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSvAddress;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.persistence.entity.sm.SmSvTemplate;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "svoSiteVisitMB")
@SessionScoped
public class SvoSiteVisitManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private long appId;
	private String[] bpmAction;
	private String taskId;
	private SMSiteVisitDataType bpmPojo;
	private int activeIndex;
	private SmSvTask smSvTask;
	private Map<Long,SmRemark> remarkMap;
	private SmSvAddress selectedSmSvAddress;
	private Map<String,SmSvTemplate> smSvTemplateMap;
	private SmSvTemplate selectedSvTemplate;
	
	public SMSiteVisitDataType getBpmPojo() {
		return bpmPojo;
	}

	public void setBpmPojo(SMSiteVisitDataType bpmPojo) {
		this.bpmPojo = bpmPojo;
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

	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	public SmSvTask getSmSvTask() {
		return smSvTask;
	}

	public void setSmSvTask(SmSvTask smSvTask) {
		this.smSvTask = smSvTask;
	}

	public Map<Long, SmRemark> getRemarkMap() {
		return remarkMap;
	}

	public void setRemarkMap(Map<Long, SmRemark> remarkMap) {
		this.remarkMap = remarkMap;
	}

	public SmSvAddress getSelectedSmSvAddress() {
		return selectedSmSvAddress;
	}

	public void setSelectedSmSvAddress(SmSvAddress selectedSmSvAddress) {
		this.selectedSmSvAddress = selectedSmSvAddress;
	}

	public Map<String, SmSvTemplate> getSmSvTemplateMap() {
		return smSvTemplateMap;
	}

	public void setSmSvTemplateMap(Map<String, SmSvTemplate> smSvTemplateMap) {
		this.smSvTemplateMap = smSvTemplateMap;
	}

	public SmSvTemplate getSelectedSvTemplate() {
		return selectedSvTemplate;
	}

	public void setSelectedSvTemplate(SmSvTemplate selectedSvTemplate) {
		this.selectedSvTemplate = selectedSvTemplate;
	}


}
