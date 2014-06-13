package my.ep.web.managedbean.sm.sitevisit;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.jaxb.sm.sitevisitdata.SMSiteVisitDataType;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSvAddress;
import my.ep.persistence.entity.sm.SmSvInterviewee;
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
	private SmSvInterviewee[] selectedInterviewees;
	
	private int totalBOD;
	private int totalMgmt;
	private int totalWorker;
	
	private int dirBumi;
	private int dirNonBumi;
	private int dirNonMalaysian;
	private int mgmtBumi;
	private int mgmtNonBumi;
	private int mgmtNonMalaysian;
	private int wrkBumi;
	private int wrkNonBumi;
	private int wrkNonMalaysian;
	private List<SmPersonnel> bodLs;
	private List<SmPersonnel> mgmtLs;
	
	private int totalCheqSign;
	private int cheqBumi;
	private int cheqNonBumi;
	private int cheqNonMalaysian;
	
	

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

//	public List<SmSvInterviewee> getSelectedIntervieweeLs() {
//		if(selectedIntervieweeLs == null)
//			selectedIntervieweeLs = new ArrayList<SmSvInterviewee>();
//		return selectedIntervieweeLs;
//	}
//
//	public void setSelectedIntervieweeLs(List<SmSvInterviewee> selectedIntervieweeLs) {
//		this.selectedIntervieweeLs = selectedIntervieweeLs;
//	}

	public int getTotalBOD() {
		return totalBOD;
	}

	public void setTotalBOD(int totalBOD) {
		this.totalBOD = totalBOD;
	}

	public int getTotalMgmt() {
		return totalMgmt;
	}

	public void setTotalMgmt(int totalMgmt) {
		this.totalMgmt = totalMgmt;
	}

	public int getTotalWorker() {
		return totalWorker;
	}

	public void setTotalWorker(int totalWorker) {
		this.totalWorker = totalWorker;
	}
	
	public int getDirBumi() {
		return dirBumi;
	}

	public void setDirBumi(int dirBumi) {
		this.dirBumi = dirBumi;
	}

	public int getDirNonBumi() {
		return dirNonBumi;
	}

	public void setDirNonBumi(int dirNonBumi) {
		this.dirNonBumi = dirNonBumi;
	}

	public int getDirNonMalaysian() {
		return dirNonMalaysian;
	}

	public void setDirNonMalaysian(int dirNonMalaysian) {
		this.dirNonMalaysian = dirNonMalaysian;
	}

	public int getMgmtBumi() {
		return mgmtBumi;
	}

	public void setMgmtBumi(int mgmtBumi) {
		this.mgmtBumi = mgmtBumi;
	}

	public int getMgmtNonBumi() {
		return mgmtNonBumi;
	}

	public void setMgmtNonBumi(int mgmtNonBumi) {
		this.mgmtNonBumi = mgmtNonBumi;
	}

	public int getMgmtNonMalaysian() {
		return mgmtNonMalaysian;
	}

	public void setMgmtNonMalaysian(int mgmtNonMalaysian) {
		this.mgmtNonMalaysian = mgmtNonMalaysian;
	}

	public int getWrkBumi() {
		return wrkBumi;
	}

	public void setWrkBumi(int wrkBumi) {
		this.wrkBumi = wrkBumi;
	}

	public int getWrkNonBumi() {
		return wrkNonBumi;
	}

	public void setWrkNonBumi(int wrkNonBumi) {
		this.wrkNonBumi = wrkNonBumi;
	}

	public int getWrkNonMalaysian() {
		return wrkNonMalaysian;
	}

	public void setWrkNonMalaysian(int wrkNonMalaysian) {
		this.wrkNonMalaysian = wrkNonMalaysian;
	}

	public List<SmPersonnel> getBodLs() {
		return bodLs;
	}

	public void setBodLs(List<SmPersonnel> bodLs) {
		this.bodLs = bodLs;
	}

	public List<SmPersonnel> getMgmtLs() {
		return mgmtLs;
	}

	public void setMgmtLs(List<SmPersonnel> mgmtLs) {
		this.mgmtLs = mgmtLs;
	}

	public SmSvInterviewee[] getSelectedInterviewees() {
		return selectedInterviewees;
	}

	public void setSelectedInterviewees(SmSvInterviewee[] selectedInterviewees) {
		this.selectedInterviewees = selectedInterviewees;
	}

	public int getTotalCheqSign() {
		return totalCheqSign;
	}

	public void setTotalCheqSign(int totalCheqSign) {
		this.totalCheqSign = totalCheqSign;
	}

	public int getCheqBumi() {
		return cheqBumi;
	}

	public void setCheqBumi(int cheqBumi) {
		this.cheqBumi = cheqBumi;
	}

	public int getCheqNonBumi() {
		return cheqNonBumi;
	}

	public void setCheqNonBumi(int cheqNonBumi) {
		this.cheqNonBumi = cheqNonBumi;
	}

	public int getCheqNonMalaysian() {
		return cheqNonMalaysian;
	}

	public void setCheqNonMalaysian(int cheqNonMalaysian) {
		this.cheqNonMalaysian = cheqNonMalaysian;
	}
}
