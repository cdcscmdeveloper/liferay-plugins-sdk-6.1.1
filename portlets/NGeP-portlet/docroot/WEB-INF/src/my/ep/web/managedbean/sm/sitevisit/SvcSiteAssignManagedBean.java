package my.ep.web.managedbean.sm.sitevisit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSvCategory;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.SmSvcAssignmentVO;

import org.primefaces.model.DualListModel;

@ManagedBean(name = "smSiteVisitAssignMB")
@SessionScoped
public class SvcSiteAssignManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private SmSvTask smSvTask;
	private Long appId;
	private String[] bpmAction;
	private String taskId;
	private Object bpmPojo;
	private DualListModel<PmUser> svoUserList;  
	private String currDate;
	
	private String processingOfficerName;
	private String businessRegNo;
	private String siteVisitStatus;
	
	private SmAppl smAppl;
	private List<SmSvcAssignmentVO> svcTaskVOLs;
	private List<SmSvCategory> categoryLs;

	public SmSvTask getSmSvTask() {
		return smSvTask;
	}

	public void setSmSvTask(SmSvTask smSvTask) {
		this.smSvTask = smSvTask;
	}

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
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

	public Object getBpmPojo() {
		return bpmPojo;
	}

	public void setBpmPojo(Object bpmPojo) {
		this.bpmPojo = bpmPojo;
	}

	public DualListModel<PmUser> getSvoUserList() {
		return svoUserList;
	}

	public void setSvoUserList(DualListModel<PmUser> svoUserList) {
		this.svoUserList = svoUserList;
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

	public String getProcessingOfficerName() {
		return processingOfficerName;
	}

	public void setProcessingOfficerName(String processingOfficerName) {
		this.processingOfficerName = processingOfficerName;
	}

	public String getBusinessRegNo() {
		return businessRegNo;
	}

	public void setBusinessRegNo(String businessRegNo) {
		this.businessRegNo = businessRegNo;
	}

	public String getSiteVisitStatus() {
		return siteVisitStatus;
	}

	public void setSiteVisitStatus(String siteVisitStatus) {
		this.siteVisitStatus = siteVisitStatus;
	}

	public SmAppl getSmAppl() {
		return smAppl;
	}

	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}

	public List<SmSvcAssignmentVO> getSvcTaskVOLs() {
		return svcTaskVOLs;
	}

	public void setSvcTaskVOLs(List<SmSvcAssignmentVO> svcTaskVOLs) {
		this.svcTaskVOLs = svcTaskVOLs;
	}

	public List<SmSvCategory> getCategoryLs() {
		return categoryLs;
	}

	public void setCategoryLs(List<SmSvCategory> categoryLs) {
		this.categoryLs = categoryLs;
	}

}
