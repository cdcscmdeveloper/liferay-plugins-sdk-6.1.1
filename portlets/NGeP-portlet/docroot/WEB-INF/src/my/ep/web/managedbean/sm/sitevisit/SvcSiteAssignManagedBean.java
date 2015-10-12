package my.ep.web.managedbean.sm.sitevisit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.jaxb.sm.sitevisitdata.SMSiteVisitDataType;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;

@ManagedBean(name = "smSiteVisitAssignMB")
@SessionScoped
public class SvcSiteAssignManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private SmSvTask smSvTask;
	private Long appId;
	private String[] bpmAction;
	private String taskId;
	private SMSiteVisitDataType bpmPojo;
	//private List<PmUser> svoUserList;
	private DualListModel<PmUser> svoUserList;  
	private List<PmUser> sourceSVO;
	private List<PmUser> selectedSVO;
	private String currDate;
	private SmRemark smRemark;
	private String poRmrkToSvc;
	
	private String processingOfficerName;
	private String businessRegNo;
	private String siteVisitStatus;
	

	public String getPoRmrkToSvc() {
		return poRmrkToSvc;
	}

	public void setPoRmrkToSvc(String poRmrkToSvc) {
		this.poRmrkToSvc = poRmrkToSvc;
	}

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

	public SmRemark getSmRemark() {
		return smRemark;
	}

	public void setSmRemark(SmRemark smRemark) {
		this.smRemark = smRemark;
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

}
