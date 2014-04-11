package my.ep.web.managedbean.sm.sitevisit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.jaxb.sm.sitevisitdata.SMSiteVisitDataType;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmApplSiteVisit;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;

@ManagedBean(name = "siteVisitAssignMB")
@SessionScoped
public class SvcSiteAssignManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private SmApplSiteVisit smApplSv;
	private long appId;
	private String[] bpmAction;
	private String taskId;
	private SMSiteVisitDataType bpmPojo;
	//private List<PmUser> svoUserList;
	private DualListModel<PmUser> svoUserList;  
	private List<PmUser> sourceSVO;
	private List<PmUser> selectedSVO;
	private String currDate;
	

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

	public SMSiteVisitDataType getBpmPojo() {
		return bpmPojo;
	}

	public void setBpmPojo(SMSiteVisitDataType bpmPojo) {
		this.bpmPojo = bpmPojo;
	}

	public DualListModel<PmUser> getSvoUserList() {
		return svoUserList;
	}

	public void setSvoUserList(DualListModel<PmUser> svoUserList) {
		this.svoUserList = svoUserList;
	}

	public List<PmUser> getSourceSVO() {
		return sourceSVO;
	}

	public void setSourceSVO(List<PmUser> sourceSVO) {
		this.sourceSVO = sourceSVO;
	}

	public List<PmUser> getSelectedSVO() {
		return selectedSVO;
	}

	public void setSelectedSVO(List<PmUser> selectedSVO) {
		this.selectedSVO = selectedSVO;
	}

	public String getCurrDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		currDate = dateFormat.format(date);
		return currDate;
	}

	public void setCurrDate(String currDate) {
		this.currDate = currDate;
	}

}
