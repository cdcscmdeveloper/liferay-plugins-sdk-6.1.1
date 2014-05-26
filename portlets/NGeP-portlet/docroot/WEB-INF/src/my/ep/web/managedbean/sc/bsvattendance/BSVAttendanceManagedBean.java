package my.ep.web.managedbean.sc.bsvattendance;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBSVRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author sri Briefing Attendance Managed Bean
 */
@ManagedBean(name = "bsvAttendanceManagedBean")
@SessionScoped
public class BSVAttendanceManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private List<ScQtBsvDtl> briefingDetailsList; // For Briefing Details
													// listing

	private List<ScQtBsvDtl> siteVisitDetailsList; // For Site Visit Details
													// listing

	private List<ScQtBsvDtl> selectedBriefingDetails; // Selected Briefing
														// Details

	private List<ScQtBsvDtl> selectedSiteVisitDetails; // Selected Site Visit
														// Details

	private List<ScQtBsvRegistration> briefingRegistrationList; // briefingRegistrationList
																// for
																// Registration
																// for
																// Company
																// List

	private List<ScQtBsvAttendance> briefingAttendanceList; // Listing for
															// Attendance
															// By
															// Company

	private ScQtBsvDtl scQtBriefing;

	private ScQtBsvRegistration selectedbriefRegistration; // Selected
															// Briefing
															// Registration
															// for
															// Attendance
															// List

	private ScQtBsvRegistration scQtBriefingRegistration;
	private ScQtBsvAttendance scQtBriefingAttendance;
	private ScQt scQt;
	private SmMofAccount smMofAccount;
	private SmSupplier smSupplier;

	// Drop down listing
	private List<PmUser> approverNames; // Drop down Listing for Approver Names
	private List<PmStatusDesc> pmStatus;
	private List<SmSupplier> smSuppliers;
	private List<SmMofAccount> smMofAccounts;

	/** added by Vidyasagar */
	private Object siteVisitPayLoad;

	private String previousPage;

	private String location;
	private String address;
	private List<TplSpecZoneDtl> zones;
	private List<ScQtBSVRemark> remarks;
	private TplSpecZoneDtl selectedZone;
	private ScQtBsv scQtBsv;
	public List<ScQtBsvDtl> getBriefingDetailsList() {
		return briefingDetailsList;
	}
	public void setBriefingDetailsList(List<ScQtBsvDtl> briefingDetailsList) {
		this.briefingDetailsList = briefingDetailsList;
	}
	public List<ScQtBsvDtl> getSiteVisitDetailsList() {
		return siteVisitDetailsList;
	}
	public void setSiteVisitDetailsList(List<ScQtBsvDtl> siteVisitDetailsList) {
		this.siteVisitDetailsList = siteVisitDetailsList;
	}
	public List<ScQtBsvDtl> getSelectedBriefingDetails() {
		return selectedBriefingDetails;
	}
	public void setSelectedBriefingDetails(List<ScQtBsvDtl> selectedBriefingDetails) {
		this.selectedBriefingDetails = selectedBriefingDetails;
	}
	public List<ScQtBsvDtl> getSelectedSiteVisitDetails() {
		return selectedSiteVisitDetails;
	}
	public void setSelectedSiteVisitDetails(
			List<ScQtBsvDtl> selectedSiteVisitDetails) {
		this.selectedSiteVisitDetails = selectedSiteVisitDetails;
	}
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
	public void setScQtBriefingAttendance(ScQtBsvAttendance scQtBriefingAttendance) {
		this.scQtBriefingAttendance = scQtBriefingAttendance;
	}
	public ScQt getScQt() {
		return scQt;
	}
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}
	public SmMofAccount getSmMofAccount() {
		return smMofAccount;
	}
	public void setSmMofAccount(SmMofAccount smMofAccount) {
		this.smMofAccount = smMofAccount;
	}
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}
	public List<PmUser> getApproverNames() {
		return approverNames;
	}
	public void setApproverNames(List<PmUser> approverNames) {
		this.approverNames = approverNames;
	}
	public List<PmStatusDesc> getPmStatus() {
		return pmStatus;
	}
	public void setPmStatus(List<PmStatusDesc> pmStatus) {
		this.pmStatus = pmStatus;
	}
	public List<SmSupplier> getSmSuppliers() {
		return smSuppliers;
	}
	public void setSmSuppliers(List<SmSupplier> smSuppliers) {
		this.smSuppliers = smSuppliers;
	}
	public List<SmMofAccount> getSmMofAccounts() {
		return smMofAccounts;
	}
	public void setSmMofAccounts(List<SmMofAccount> smMofAccounts) {
		this.smMofAccounts = smMofAccounts;
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
	public List<TplSpecZoneDtl> getZones() {
		return zones;
	}
	public void setZones(List<TplSpecZoneDtl> zones) {
		this.zones = zones;
	}
	public List<ScQtBSVRemark> getRemarks() {
		return remarks;
	}
	public void setRemarks(List<ScQtBSVRemark> remarks) {
		this.remarks = remarks;
	}
	public TplSpecZoneDtl getSelectedZone() {
		return selectedZone;
	}
	public void setSelectedZone(TplSpecZoneDtl selectedZone) {
		this.selectedZone = selectedZone;
	}
	public ScQtBsv getScQtBsv() {
		return scQtBsv;
	}
	public void setScQtBsv(ScQtBsv scQtBsv) {
		this.scQtBsv = scQtBsv;
	}

}
