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
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.web.bean.paginator.IPagingDataModel;
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

	//Header
	private ScQt scQt;
	private String processName;
	private Object payLoad;
		
	private List<ScQtMeeting> scQtMeetings;
	private List<ScQtMeeting> meetingMultiSelect;
	private ScCommitteeMember committeMember;
	private List<SelectItem> selections;
	private String tabTitle;
	private String name = null;
	private String identificationNo = null;
	private String identificationType;
	private List<PmUser> externalUsers;
	private List<PmUser> selectedPmUser;
	private SelectItem[] roleTypes; // Role type drop down
	private String roleName;
	private List<ScCommitteeMember> committeeMembersList;
	private List<ScCommitteeMember> originalCommitteeMembersList;
	private List<ScCommitteeMember> selectedCommitteeMembersList;
	private QtnTenderSearchBean cloneQtnTenderSearchBean;
	private List<ScQt> cloneTenderList;
	private List<ScQt> selectedCloneTender;
	private List<ScCommitteeMember> cloneCommitteMembers;
	private List<ScCommitteeMember> cloneCommitteMembersSelect;
	private ScQtCommittee scQtCommittee;
	private List<ScQtCommitteRemarks> qtCommitteeRemarks;
	
	private IPagingDataModel<ScQt> cloneSearchQTDataModel;
	
	/**
	 * @return the scQtMeetings
	 */
	public List<ScQtMeeting> getScQtMeetings() {
		return scQtMeetings;
	}
	/**
	 * @param scQtMeetings the scQtMeetings to set
	 */
	public void setScQtMeetings(List<ScQtMeeting> scQtMeetings) {
		this.scQtMeetings = scQtMeetings;
	}
	/**
	 * @return the meetingMultiSelect
	 */
	public List<ScQtMeeting> getMeetingMultiSelect() {
		return meetingMultiSelect;
	}
	/**
	 * @param meetingMultiSelect the meetingMultiSelect to set
	 */
	public void setMeetingMultiSelect(List<ScQtMeeting> meetingMultiSelect) {
		this.meetingMultiSelect = meetingMultiSelect;
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
	 * @return the selections
	 */
	public List<SelectItem> getSelections() {
		return selections;
	}
	/**
	 * @param selections the selections to set
	 */
	public void setSelections(List<SelectItem> selections) {
		this.selections = selections;
	}
	/**
	 * @return the tabTitle
	 */
	public String getTabTitle() {
		return tabTitle;
	}
	/**
	 * @param tabTitle the tabTitle to set
	 */
	public void setTabTitle(String tabTitle) {
		this.tabTitle = tabTitle;
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
	 * @return the committeeMembersList
	 */
	public List<ScCommitteeMember> getCommitteeMembersList() {
		return committeeMembersList;
	}
	/**
	 * @param committeeMembersList the committeeMembersList to set
	 */
	public void setCommitteeMembersList(List<ScCommitteeMember> committeeMembersList) {
		this.committeeMembersList = committeeMembersList;
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
	 * @return the selectedCloneTender
	 */
	public List<ScQt> getSelectedCloneTender() {
		return selectedCloneTender;
	}
	/**
	 * @param selectedCloneTender the selectedCloneTender to set
	 */
	public void setSelectedCloneTender(List<ScQt> selectedCloneTender) {
		this.selectedCloneTender = selectedCloneTender;
	}
	/**
	 * @return the committeeMemberActivity
	 */
	/**
	 * @return the scQtCommittee
	 */
	public ScQtCommittee getScQtCommittee() {
		return scQtCommittee;
	}
	/**
	 * @param scQtCommittee the scQtCommittee to set
	 */
	public void setScQtCommittee(ScQtCommittee scQtCommittee) {
		this.scQtCommittee = scQtCommittee;
	}
	/**
	 * @return the qtCommitteeRemarks
	 */
	public List<ScQtCommitteRemarks> getQtCommitteeRemarks() {
		return qtCommitteeRemarks;
	}
	/**
	 * @param qtCommitteeRemarks the qtCommitteeRemarks to set
	 */
	public void setQtCommitteeRemarks(List<ScQtCommitteRemarks> qtCommitteeRemarks) {
		this.qtCommitteeRemarks = qtCommitteeRemarks;
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
	 * @return the cloneSearchQTDataModel
	 */
	public IPagingDataModel<ScQt> getCloneSearchQTDataModel() {
		return cloneSearchQTDataModel;
	}
	/**
	 * @param cloneSearchQTDataModel the cloneSearchQTDataModel to set
	 */
	public void setCloneSearchQTDataModel(
			IPagingDataModel<ScQt> cloneSearchQTDataModel) {
		this.cloneSearchQTDataModel = cloneSearchQTDataModel;
	}
	/**
	 * @return the cloneCommitteMembers
	 */
	public List<ScCommitteeMember> getCloneCommitteMembers() {
		return cloneCommitteMembers;
	}
	/**
	 * @param cloneCommitteMembers the cloneCommitteMembers to set
	 */
	public void setCloneCommitteMembers(List<ScCommitteeMember> cloneCommitteMembers) {
		this.cloneCommitteMembers = cloneCommitteMembers;
	}
	/**
	 * @return the cloneCommitteMembersSelect
	 */
	public List<ScCommitteeMember> getCloneCommitteMembersSelect() {
		return cloneCommitteMembersSelect;
	}
	/**
	 * @param cloneCommitteMembersSelect the cloneCommitteMembersSelect to set
	 */
	public void setCloneCommitteMembersSelect(
			List<ScCommitteeMember> cloneCommitteMembersSelect) {
		this.cloneCommitteMembersSelect = cloneCommitteMembersSelect;
	}
	/**
	 * @return the processName
	 */
	public String getProcessName() {
		return processName;
	}
	/**
	 * @param processName the processName to set
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	/**
	 * @return the payLoad
	 */
	public Object getPayLoad() {
		return payLoad;
	}
	/**
	 * @param payLoad the payLoad to set
	 */
	public void setPayLoad(Object payLoad) {
		this.payLoad = payLoad;
	}
	/**
	 * @return the selectedCommitteeMembersList
	 */
	public List<ScCommitteeMember> getSelectedCommitteeMembersList() {
		return selectedCommitteeMembersList;
	}
	/**
	 * @param selectedCommitteeMembersList the selectedCommitteeMembersList to set
	 */
	public void setSelectedCommitteeMembersList(
			List<ScCommitteeMember> selectedCommitteeMembersList) {
		this.selectedCommitteeMembersList = selectedCommitteeMembersList;
	}
	/**
	 * @return the originalCommitteeMembersList
	 */
	public List<ScCommitteeMember> getOriginalCommitteeMembersList() {
		return originalCommitteeMembersList;
	}
	/**
	 * @param originalCommitteeMembersList the originalCommitteeMembersList to set
	 */
	public void setOriginalCommitteeMembersList(
			List<ScCommitteeMember> originalCommitteeMembersList) {
		this.originalCommitteeMembersList = originalCommitteeMembersList;
	}
	
}
