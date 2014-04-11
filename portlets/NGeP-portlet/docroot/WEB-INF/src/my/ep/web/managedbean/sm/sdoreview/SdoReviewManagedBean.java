package my.ep.web.managedbean.sm.sdoreview;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.jaxb.sm.mofregdata.SMMOFRegDataType;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "sdoReviewMB")
@SessionScoped
public class SdoReviewManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	
	private SmAppl smAppl;
	private SmAddress companyAdd;
	private long appId;
	private String[] bpmAction;
	private String taskId;
	private String taskName;
	private SMMOFRegDataType bpmPojo;
	private SmRemark smRemark;
	private SmPersonnel smPersonnel;

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

	public SmAddress getCompanyAdd() {
		return companyAdd;
	}

	public void setCompanyAdd(SmAddress companyAdd) {
		this.companyAdd = companyAdd;
	}

	public SmRemark getSmRemark() {
		return smRemark;
	}

	public void setSmRemark(SmRemark smRemark) {
		this.smRemark = smRemark;
	}

	public SmPersonnel getSmPersonnel() {
		return smPersonnel;
	}

	public void setSmPersonnel(SmPersonnel smPersonnel) {
		this.smPersonnel = smPersonnel;
	}

	
}
