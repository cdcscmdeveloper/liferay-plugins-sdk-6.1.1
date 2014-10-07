package my.ep.web.managedbean.sc.bsvattendance;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author sri Briefing Attendance Managed Bean
 */
@ManagedBean(name = "bsvAttendanceManagedBean")
@SessionScoped
public class BSVAttendanceManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private List<ScQtBsvRegistration> briefingRegistrationList;

	private List<ScQtBsvAttendance> briefingAttendanceList;

	private ScQtBsvDtl scQtBriefing;

	private ScQtBsvRegistration selectedbriefRegistration;

	private ScQtBsvRegistration scQtBriefingRegistration;
	private ScQtBsvAttendance scQtBriefingAttendance;
	private ScQt scQt;

	/** added by Vidyasagar */
	private Object siteVisitPayLoad;

	private String previousPage;

	private String location;
	private String address;

	private ScQtBsv scQtBsv;

	private List<BriefingZoneBean> zoneBean;

	private BriefingZoneBean normalBsvBean;
	
	private List<SmMofAccount> mofAccounts;
	private String bsvZoneindx="0";
	
	

	public List<ScQtBsvRegistration> getBriefingRegistrationList() {
		return briefingRegistrationList;
	}

	public void setBriefingRegistrationList(
			List<ScQtBsvRegistration> briefingRegistrationList) {
		this.briefingRegistrationList = briefingRegistrationList;
	}

	public List<ScQtBsvAttendance> getBriefingAttendanceList() {
		return briefingAttendanceList;
	}

	public void setBriefingAttendanceList(
			List<ScQtBsvAttendance> briefingAttendanceList) {
		this.briefingAttendanceList = briefingAttendanceList;
	}

	public ScQtBsvDtl getScQtBriefing() {
		return scQtBriefing;
	}

	public void setScQtBriefing(ScQtBsvDtl scQtBriefing) {
		this.scQtBriefing = scQtBriefing;
	}

	public ScQtBsvRegistration getSelectedbriefRegistration() {
		return selectedbriefRegistration;
	}

	public void setSelectedbriefRegistration(
			ScQtBsvRegistration selectedbriefRegistration) {
		this.selectedbriefRegistration = selectedbriefRegistration;
	}

	public ScQtBsvRegistration getScQtBriefingRegistration() {
		return scQtBriefingRegistration;
	}

	public void setScQtBriefingRegistration(
			ScQtBsvRegistration scQtBriefingRegistration) {
		this.scQtBriefingRegistration = scQtBriefingRegistration;
	}

	public ScQtBsvAttendance getScQtBriefingAttendance() {
		return scQtBriefingAttendance;
	}

	public void setScQtBriefingAttendance(
			ScQtBsvAttendance scQtBriefingAttendance) {
		this.scQtBriefingAttendance = scQtBriefingAttendance;
	}

	public ScQt getScQt() {
		return scQt;
	}

	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}

	public Object getSiteVisitPayLoad() {
		return siteVisitPayLoad;
	}

	public void setSiteVisitPayLoad(Object siteVisitPayLoad) {
		this.siteVisitPayLoad = siteVisitPayLoad;
	}

	public String getPreviousPage() {
		return previousPage;
	}

	public void setPreviousPage(String previousPage) {
		this.previousPage = previousPage;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ScQtBsv getScQtBsv() {
		return scQtBsv;
	}

	public void setScQtBsv(ScQtBsv scQtBsv) {
		this.scQtBsv = scQtBsv;
	}

	public List<BriefingZoneBean> getZoneBean() {
		return zoneBean;
	}

	public void setZoneBean(List<BriefingZoneBean> zoneBean) {
		this.zoneBean = zoneBean;
	}

	public BriefingZoneBean getNormalBsvBean() {
		return normalBsvBean;
	}

	public void setNormalBsvBean(BriefingZoneBean normalBsvBean) {
		this.normalBsvBean = normalBsvBean;
	}

	public List<SmMofAccount> getMofAccounts() {
		return mofAccounts;
	}

	public void setMofAccounts(List<SmMofAccount> mofAccounts) {
		this.mofAccounts = mofAccounts;
	}

	public String getBsvZoneindx() {
		return bsvZoneindx;
	}

	public void setBsvZoneindx(String bsvZoneindx) {
		this.bsvZoneindx = bsvZoneindx;
	}

	
}
