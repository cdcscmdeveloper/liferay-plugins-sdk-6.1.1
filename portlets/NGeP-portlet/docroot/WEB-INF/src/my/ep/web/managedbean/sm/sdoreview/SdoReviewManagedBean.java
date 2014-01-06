package my.ep.web.managedbean.sm.sdoreview;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.jaxb.sm.mofregdata.SMMOFRegDataType;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "sdoReviewMB")
@SessionScoped
public class SdoReviewManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	
	private SmAppl smAppl;
	private long appId;
	private String[] bpmAction;
	private String taskId;
	private String taskName;
	private SMMOFRegDataType bpmPojo;

	public SmAppl getSmAppl() {
		return smAppl;
	}

	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
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

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public SMMOFRegDataType getBpmPojo() {
		return bpmPojo;
	}

	public void setBpmPojo(SMMOFRegDataType bpmPojo) {
		this.bpmPojo = bpmPojo;
	}

	
}
