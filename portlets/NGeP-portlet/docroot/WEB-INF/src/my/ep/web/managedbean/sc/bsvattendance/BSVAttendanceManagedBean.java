package my.ep.web.managedbean.sc.bsvattendance;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBriefing;
import my.ep.persistence.entity.sc.quationtender.ScQtBriefingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtBriefingRegistration;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
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
	
	private List<ScQtBriefingRegistration> briefingRegistrationList; // briefingRegistrationList  for Registration for Company List
	
	private List<ScQtBriefingAttendance> briefingAttendanceList;  // Listing for Attendance By Company
	
	private ScQtBriefing scQtBriefing; 
	
	private ScQtBriefingRegistration  selectedbriefRegistration; // Selected Briefing Registration for Attendance List 
	
	private ScQtBriefingRegistration scQtBriefingRegistration; 
	private ScQtBriefingAttendance scQtBriefingAttendance;
	private ScQt scQt;
	private SmMofAccount smMofAccount;
	private SmSupplier smSupplier;
	
	//Drop down listing
	private List<PmUser>  approverNames; // Drop down Listing for Approver Names
	private List<PmStatusDesc> pmStatus;
	private List<SmSupplier> smSuppliers;
	private List<SmMofAccount> 	smMofAccounts;
	
	/** added by Vidyasagar */
	private Object siteVisitPayLoad;
	
	private String previousPage;
	 
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

	/**
	 * @return the scQtBriefingRegistration
	 */
	public ScQtBriefingRegistration getScQtBriefingRegistration() {
		return scQtBriefingRegistration;
	}

	/**
	 * @param scQtBriefingRegistration the scQtBriefingRegistration to set
	 */
	public void setScQtBriefingRegistration(
			ScQtBriefingRegistration scQtBriefingRegistration) {
		this.scQtBriefingRegistration = scQtBriefingRegistration;
	}

	/**
	 * @return the scQtBriefingAttendance
	 */
	public ScQtBriefingAttendance getScQtBriefingAttendance() {
		return scQtBriefingAttendance;
	}

	/**
	 * @param scQtBriefingAttendance the scQtBriefingAttendance to set
	 */
	public void setScQtBriefingAttendance(
			ScQtBriefingAttendance scQtBriefingAttendance) {
		this.scQtBriefingAttendance = scQtBriefingAttendance;
	}

	/**
	 * @return the scQt
	 */
	public ScQt getScQt() {
		return scQt;
	}

	/**
	 * @param scQt the scQt to set
	 */
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}

	/**
	 * @return the pmStatus
	 */
	public List<PmStatusDesc> getPmStatus() {
		return pmStatus;
	}

	/**
	 * @param pmStatus the pmStatus to set
	 */
	public void setPmStatus(List<PmStatusDesc> pmStatus) {
		this.pmStatus = pmStatus;
	}


	/**
	 * @return the smSuppliers
	 */
	public List<SmSupplier> getSmSuppliers() {
		return smSuppliers;
	}

	/**
	 * @param smSuppliers the smSuppliers to set
	 */
	public void setSmSuppliers(List<SmSupplier> smSuppliers) {
		this.smSuppliers = smSuppliers;
	}

	/**
	 * @return the smMofAccounts
	 */
	public List<SmMofAccount> getSmMofAccounts() {
		return smMofAccounts;
	}

	/**
	 * @param smMofAccounts the smMofAccounts to set
	 */
	public void setSmMofAccounts(List<SmMofAccount> smMofAccounts) {
		this.smMofAccounts = smMofAccounts;
	}

	/**
	 * @return the smMofAccount
	 */
	public SmMofAccount getSmMofAccount() {
		return smMofAccount;
	}

	/**
	 * @param smMofAccount the smMofAccount to set
	 */
	public void setSmMofAccount(SmMofAccount smMofAccount) {
		this.smMofAccount = smMofAccount;
	}

	/**
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * @param smSupplier the smSupplier to set
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
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

	
	
	
	
}
