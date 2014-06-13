package my.ep.web.managedbean.sc.supplierfinalization;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author Faradiana
 * Supplier Finalization Meeting Managed Bean
 */
@ManagedBean(name = "suppFinalMeetingManagedBean")
@SessionScoped
public class SupplierFinalizationMeetingManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private ScQt qtnTender;
	private ScQtCommittee committee;
	private ScCommitteeMember committeMember;
	
	private List<ScQtMeeting> meetingList;
	private List<ScQtMeeting> selectedMeetingList;
	private List<ScQtCommitteeActivity> committeeList;
	private List<ScQtMeeting> selectedCommitteeList;
	private List<ScQtCommitteRemarks> committeeRemarkList;
	private List<ScCommitteeMember> committeeMemberList;
	private List<ScCommitteeMember> selectedCommitteeMemberList;
	private List<ScQtCommitteeActivity> committeeActivityList;
	private List<ScQtCommitteeActivity> selectedCommitteeActivityList;
	
	private QtnTenderSearchBean cloneQtnTenderSearchBean;
	private List<ScQt> cloneTenderList;
	private List<ScQt> selectedCloneTenderList;
	private List<ScCommitteeMember> cloneCommitteeMemberList;
	private List<ScCommitteeMember> selectedCloneCommitteeMemberList;
	
	private String name = null;
	private String identificationNo = null;
	private String identificationType;
	private String roleName;
	private List<PmUser> externalUsers;
	private List<PmUser> selectedPmUser;
	private List<SelectItem> selectionList;
	private SelectItem[] roleTypes; // Role type drop down
	/**
	 * @return the qtnTender
	 */
	public ScQt getQtnTender() {
		return qtnTender;
	}
	/**
	 * @param qtnTender the qtnTender to set
	 */
	public void setQtnTender(ScQt qtnTender) {
		this.qtnTender = qtnTender;
	}
	/**
	 * @return the committee
	 */
	public ScQtCommittee getCommittee() {
		return committee;
	}
	/**
	 * @param committee the committee to set
	 */
	public void setCommittee(ScQtCommittee committee) {
		this.committee = committee;
	}
	/**
	 * @return the committeMember
	 */
	public ScCommitteeMember getCommitteMember() {
		return committeMember;
	}
	/**
	 * @param committeMember the committeMember to set
	 */
	public void setCommitteMember(ScCommitteeMember committeMember) {
		this.committeMember = committeMember;
	}
	/**
	 * @return the meetingList
	 */
	public List<ScQtMeeting> getMeetingList() {
		return meetingList;
	}
	/**
	 * @param meetingList the meetingList to set
	 */
	public void setMeetingList(List<ScQtMeeting> meetingList) {
		this.meetingList = meetingList;
	}
	/**
	 * @return the selectedMeetingList
	 */
	public List<ScQtMeeting> getSelectedMeetingList() {
		return selectedMeetingList;
	}
	/**
	 * @param selectedMeetingList the selectedMeetingList to set
	 */
	public void setSelectedMeetingList(List<ScQtMeeting> selectedMeetingList) {
		this.selectedMeetingList = selectedMeetingList;
	}
	/**
	 * @return the committeeList
	 */
	public List<ScQtCommitteeActivity> getCommitteeList() {
		return committeeList;
	}
	/**
	 * @param committeeList the committeeList to set
	 */
	public void setCommitteeList(List<ScQtCommitteeActivity> committeeList) {
		this.committeeList = committeeList;
	}
	/**
	 * @return the selectedCommitteeList
	 */
	public List<ScQtMeeting> getSelectedCommitteeList() {
		return selectedCommitteeList;
	}
	/**
	 * @param selectedCommitteeList the selectedCommitteeList to set
	 */
	public void setSelectedCommitteeList(List<ScQtMeeting> selectedCommitteeList) {
		this.selectedCommitteeList = selectedCommitteeList;
	}
	/**
	 * @return the committeeRemarkList
	 */
	public List<ScQtCommitteRemarks> getCommitteeRemarkList() {
		return committeeRemarkList;
	}
	/**
	 * @param committeeRemarkList the committeeRemarkList to set
	 */
	public void setCommitteeRemarkList(List<ScQtCommitteRemarks> committeeRemarkList) {
		this.committeeRemarkList = committeeRemarkList;
	}
	/**
	 * @return the committeeMemberList
	 */
	public List<ScCommitteeMember> getCommitteeMemberList() {
		return committeeMemberList;
	}
	/**
	 * @param committeeMemberList the committeeMemberList to set
	 */
	public void setCommitteeMemberList(List<ScCommitteeMember> committeeMemberList) {
		this.committeeMemberList = committeeMemberList;
	}
	/**
	 * @return the selectedCommitteeMemberList
	 */
	public List<ScCommitteeMember> getSelectedCommitteeMemberList() {
		return selectedCommitteeMemberList;
	}
	/**
	 * @param selectedCommitteeMemberList the selectedCommitteeMemberList to set
	 */
	public void setSelectedCommitteeMemberList(
			List<ScCommitteeMember> selectedCommitteeMemberList) {
		this.selectedCommitteeMemberList = selectedCommitteeMemberList;
	}
	/**
	 * @return the committeeActivityList
	 */
	public List<ScQtCommitteeActivity> getCommitteeActivityList() {
		return committeeActivityList;
	}
	/**
	 * @param committeeActivityList the committeeActivityList to set
	 */
	public void setCommitteeActivityList(
			List<ScQtCommitteeActivity> committeeActivityList) {
		this.committeeActivityList = committeeActivityList;
	}
	/**
	 * @return the selectedCommitteeActivityList
	 */
	public List<ScQtCommitteeActivity> getSelectedCommitteeActivityList() {
		return selectedCommitteeActivityList;
	}
	/**
	 * @param selectedCommitteeActivityList the selectedCommitteeActivityList to set
	 */
	public void setSelectedCommitteeActivityList(
			List<ScQtCommitteeActivity> selectedCommitteeActivityList) {
		this.selectedCommitteeActivityList = selectedCommitteeActivityList;
	}
	/**
	 * @return the cloneQtnTenderSearchBean
	 */
	public QtnTenderSearchBean getCloneQtnTenderSearchBean() {
		return cloneQtnTenderSearchBean;
	}
	/**
	 * @param cloneQtnTenderSearchBean the cloneQtnTenderSearchBean to set
	 */
	public void setCloneQtnTenderSearchBean(
			QtnTenderSearchBean cloneQtnTenderSearchBean) {
		this.cloneQtnTenderSearchBean = cloneQtnTenderSearchBean;
	}
	/**
	 * @return the cloneTenderList
	 */
	public List<ScQt> getCloneTenderList() {
		return cloneTenderList;
	}
	/**
	 * @param cloneTenderList the cloneTenderList to set
	 */
	public void setCloneTenderList(List<ScQt> cloneTenderList) {
		this.cloneTenderList = cloneTenderList;
	}
	/**
	 * @return the selectedCloneTenderList
	 */
	public List<ScQt> getSelectedCloneTenderList() {
		return selectedCloneTenderList;
	}
	/**
	 * @param selectedCloneTenderList the selectedCloneTenderList to set
	 */
	public void setSelectedCloneTenderList(List<ScQt> selectedCloneTenderList) {
		this.selectedCloneTenderList = selectedCloneTenderList;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the identificationNo
	 */
	public String getIdentificationNo() {
		return identificationNo;
	}
	/**
	 * @param identificationNo the identificationNo to set
	 */
	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}
	/**
	 * @return the identificationType
	 */
	public String getIdentificationType() {
		return identificationType;
	}
	/**
	 * @param identificationType the identificationType to set
	 */
	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}
	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}
	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	/**
	 * @return the externalUsers
	 */
	public List<PmUser> getExternalUsers() {
		return externalUsers;
	}
	/**
	 * @param externalUsers the externalUsers to set
	 */
	public void setExternalUsers(List<PmUser> externalUsers) {
		this.externalUsers = externalUsers;
	}
	/**
	 * @return the selectedPmUser
	 */
	public List<PmUser> getSelectedPmUser() {
		return selectedPmUser;
	}
	/**
	 * @param selectedPmUser the selectedPmUser to set
	 */
	public void setSelectedPmUser(List<PmUser> selectedPmUser) {
		this.selectedPmUser = selectedPmUser;
	}
	/**
	 * @return the selectionList
	 */
	public List<SelectItem> getSelectionList() {
		return selectionList;
	}
	/**
	 * @param selectionList the selectionList to set
	 */
	public void setSelectionList(List<SelectItem> selectionList) {
		this.selectionList = selectionList;
	}
	/**
	 * @return the roleTypes
	 */
	public SelectItem[] getRoleTypes() {
		return roleTypes;
	}
	/**
	 * @param roleTypes the roleTypes to set
	 */
	public void setRoleTypes(SelectItem[] roleTypes) {
		this.roleTypes = roleTypes;
	}
	/**
	 * @return the cloneCommitteeMemberList
	 */
	public List<ScCommitteeMember> getCloneCommitteeMemberList() {
		return cloneCommitteeMemberList;
	}
	/**
	 * @param cloneCommitteeMemberList the cloneCommitteeMemberList to set
	 */
	public void setCloneCommitteeMemberList(
			List<ScCommitteeMember> cloneCommitteeMemberList) {
		this.cloneCommitteeMemberList = cloneCommitteeMemberList;
	}
	/**
	 * @return the selectedCloneCommitteeMemberList
	 */
	public List<ScCommitteeMember> getSelectedCloneCommitteeMemberList() {
		return selectedCloneCommitteeMemberList;
	}
	/**
	 * @param selectedCloneCommitteeMemberList the selectedCloneCommitteeMemberList to set
	 */
	public void setSelectedCloneCommitteeMemberList(
			List<ScCommitteeMember> selectedCloneCommitteeMemberList) {
		this.selectedCloneCommitteeMemberList = selectedCloneCommitteeMemberList;
	}
	
	
	
}
