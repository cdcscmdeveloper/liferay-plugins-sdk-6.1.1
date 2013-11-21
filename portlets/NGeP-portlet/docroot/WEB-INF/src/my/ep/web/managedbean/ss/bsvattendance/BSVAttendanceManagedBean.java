package my.ep.web.managedbean.ss.bsvattendance;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.ss.quationtender.ScQtBriefing;
import my.ep.persistence.entity.ss.quationtender.ScQtBriefingAttendance;
import my.ep.persistence.entity.ss.quationtender.ScQtBriefingRegistration;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author sri
 * Briefing Attendance Managed Bean
 */
@ManagedBean(name = "bsvAttendanceManagedBean")
@SessionScoped
public class BSVAttendanceManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private List<ScQtBriefing> briefingDetailsList; // For Briefing Details listing
	
	private List<ScQtBriefing> siteVisitDetailsList; // For Site Visit Details listing
	
	private List<ScQtBriefing> selectedBriefingDetails; // Selected Briefing Details
	
	private List<ScQtBriefing> selectedSiteVisitDetails; // Selected Site Visit Details
	
	private List<ScQtBriefingRegistration> briefingRegistrationList; // Listing for Registration for Company 
	
	private List<ScQtBriefingAttendance> briefingAttendanceList;  // Listing for Attendance By Company
	
	private ScQtBriefing scQtBriefing; 
	
	private ScQtBriefingRegistration  selectedbriefRegistration; // Selected Briefing Registration for Attendance List 
	
	private List<PmUser>  approverNames; // Drop down Listing for Approver Names
	
	private String briefSameAsSiteVisit; //  briefSameAsSiteVisit  for Briefing Same As Site Visit value
	
	private boolean visibleState=true; //  isVisible  for  site visit component visible or disable
	
	
	/**
	 * @return  briefingDetailsList
	 */
	public List<ScQtBriefing> getBriefingDetailsList() {
		return briefingDetailsList;
	}
	
	/**
	 * @param briefingDetailsList the briefingDetailsList to set
	 */
	public void setBriefingDetailsList(List<ScQtBriefing> briefingDetailsList) {
		this.briefingDetailsList = briefingDetailsList;
	}
	
	/**
	 * @return siteVisitDetailsList
	 */
	public List<ScQtBriefing> getSiteVisitDetailsList() {
		return siteVisitDetailsList;
	}
	
	
	/**
	 * @param siteVisitDetailsList the siteVisitDetailsList to set
	 */
	public void setSiteVisitDetailsList(List<ScQtBriefing> siteVisitDetailsList) {
		this.siteVisitDetailsList = siteVisitDetailsList;
	}

	/**
	 * @return selectedSiteVisitDetails
	 */
	public List<ScQtBriefing> getSelectedSiteVisitDetails() {
		return selectedSiteVisitDetails;
	}
	
	/**
	 * @param selectedSiteVisitDetails the selectedSiteVisitDetails to set
	 */
	public void setSelectedSiteVisitDetails(
			List<ScQtBriefing> selectedSiteVisitDetails) {
		this.selectedSiteVisitDetails = selectedSiteVisitDetails;
	}
	
	/**
	 * @return selectedBriefingDetails
	 */
	public List<ScQtBriefing> getSelectedBriefingDetails() {
		return selectedBriefingDetails;
	}
	
	/**
	 * @param selectedBriefingDetails the selectedBriefingDetails to set
	 */
	public void setSelectedBriefingDetails(
			List<ScQtBriefing> selectedBriefingDetails) {
		this.selectedBriefingDetails = selectedBriefingDetails;
	}
	
	/**
	 * @return scQtBriefing
	 */
	public ScQtBriefing getScQtBriefing() {
		return scQtBriefing;
	}

	/**
	 * @param scQtBriefing the scQtBriefing to set
	 */
	public void setScQtBriefing(ScQtBriefing scQtBriefing) {
		this.scQtBriefing = scQtBriefing;
	}
	
	/**
	 * @return briefingRegistrationList
	 */
	public List<ScQtBriefingRegistration> getBriefingRegistrationList() {
		return briefingRegistrationList;
	}
	
	/**
	 * @param briefingRegistrationList the briefingRegistrationList to set
	 */
	public void setBriefingRegistrationList(
			List<ScQtBriefingRegistration> briefingRegistrationList) {
		this.briefingRegistrationList = briefingRegistrationList;
	}
	
	/**
	 * @return briefingAttendanceList
	 */
	public List<ScQtBriefingAttendance> getBriefingAttendanceList() {
		return briefingAttendanceList;
	}
	
	/**
	 * @param briefingAttendanceList the briefingAttendanceList to set
	 */
	public void setBriefingAttendanceList(
			List<ScQtBriefingAttendance> briefingAttendanceList) {
		this.briefingAttendanceList = briefingAttendanceList;
	}
	
	/**
	 * @return briefSameAsSiteVisit
	 */
	public String getBriefSameAsSiteVisit() {
		return briefSameAsSiteVisit;
	}
	
	/**
	 * @param briefSameAsSiteVisit the briefSameAsSiteVisit to set
	 */
	public void setBriefSameAsSiteVisit(String briefSameAsSiteVisit) {
		this.briefSameAsSiteVisit = briefSameAsSiteVisit;
	}
	
	/**
	 * @return visibleState
	 */
	public boolean isVisibleState() {
		return visibleState;
	}
	
	/**
	 * @param visibleState the visibleState to set
	 */
	public void setVisibleState(boolean visibleState) {
		this.visibleState = visibleState;
	}
	
	/**
	 * @return selectedbriefRegistration
	 */
	public ScQtBriefingRegistration getSelectedbriefRegistration() {
		return selectedbriefRegistration;
	}
	
	/**
	 * @param selectedbriefRegistration the selectedbriefRegistration to set
	 */
	public void setSelectedbriefRegistration(
			ScQtBriefingRegistration selectedbriefRegistration) {
		this.selectedbriefRegistration = selectedbriefRegistration;
	}
	
	/**
	 * @return approverNames
	 */
	public List<PmUser> getApproverNames() {
		return approverNames;
	}
	
	/**
	 * @param approverNames the approverNames to set
	 */
	public void setApproverNames(List<PmUser> approverNames) {
		this.approverNames = approverNames;
	}
	
	
}
