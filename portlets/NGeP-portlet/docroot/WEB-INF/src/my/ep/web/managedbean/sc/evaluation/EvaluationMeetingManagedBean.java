/**
 * 
 */
package my.ep.web.managedbean.sc.evaluation;

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
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author ACER
 *
 */
@ManagedBean(name = "evaluationMeetingMB")
@SessionScoped
public class EvaluationMeetingManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	private List<ScCommitteeMember> committeeMeetingSelectList;
	private QtnTenderSearchBean cloneQtnTenderSearchBean;
	private List<ScQt> cloneTenderList;
	private List<ScQt> selectedCloneTender;
	private List<ScCommitteeMember> cloneCommitteMemebers;
	private List<ScCommitteeMember> cloneCommitteMemebersSelect;
	private List<ScQtCommitteeActivity> committeeMemberActivity;
	private List<ScQtCommitteeActivity> committeeActivitySelect;
	private ScQtCommittee scQtCommittee;
	private List<ScQtCommitteRemarks> qtCommitteeRemarks;
	private List<ScCommitteeMember> scCommitteeMemberList;
	private ScQt qt;
	private Object payLoad;
	private String committeeType;
	private String committeeTypeDesc;
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
	 * @return the committeeMeetingSelectList
	 */
	public List<ScCommitteeMember> getCommitteeMeetingSelectList() {
		return committeeMeetingSelectList;
	}
	/**
	 * @param committeeMeetingSelectList the committeeMeetingSelectList to set
	 */
	public void setCommitteeMeetingSelectList(
			List<ScCommitteeMember> committeeMeetingSelectList) {
		this.committeeMeetingSelectList = committeeMeetingSelectList;
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
	 * @return the cloneCommitteMemebers
	 */
	public List<ScCommitteeMember> getCloneCommitteMemebers() {
		return cloneCommitteMemebers;
	}
	/**
	 * @param cloneCommitteMemebers the cloneCommitteMemebers to set
	 */
	public void setCloneCommitteMemebers(
			List<ScCommitteeMember> cloneCommitteMemebers) {
		this.cloneCommitteMemebers = cloneCommitteMemebers;
	}
	/**
	 * @return the cloneCommitteMemebersSelect
	 */
	public List<ScCommitteeMember> getCloneCommitteMemebersSelect() {
		return cloneCommitteMemebersSelect;
	}
	/**
	 * @param cloneCommitteMemebersSelect the cloneCommitteMemebersSelect to set
	 */
	public void setCloneCommitteMemebersSelect(
			List<ScCommitteeMember> cloneCommitteMemebersSelect) {
		this.cloneCommitteMemebersSelect = cloneCommitteMemebersSelect;
	}
	/**
	 * @return the committeeMemberActivity
	 */
	public List<ScQtCommitteeActivity> getCommitteeMemberActivity() {
		return committeeMemberActivity;
	}
	/**
	 * @param committeeMemberActivity the committeeMemberActivity to set
	 */
	public void setCommitteeMemberActivity(
			List<ScQtCommitteeActivity> committeeMemberActivity) {
		this.committeeMemberActivity = committeeMemberActivity;
	}
	/**
	 * @return the committeeActivitySelect
	 */
	public List<ScQtCommitteeActivity> getCommitteeActivitySelect() {
		return committeeActivitySelect;
	}
	/**
	 * @param committeeActivitySelect the committeeActivitySelect to set
	 */
	public void setCommitteeActivitySelect(
			List<ScQtCommitteeActivity> committeeActivitySelect) {
		this.committeeActivitySelect = committeeActivitySelect;
	}
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
	
	
	public void setScCommitteeMemberList(
			List<ScCommitteeMember> scCommitteeMemberList) {
		this.scCommitteeMemberList = scCommitteeMemberList;
	}

	public List<ScCommitteeMember> getScCommitteeMemberList() {
		return scCommitteeMemberList;
	}
	/**
	 * @return the qt
	 */
	public ScQt getQt() {
		return qt;
	}
	/**
	 * @param qt the qt to set
	 */
	public void setQt(ScQt qt) {
		this.qt = qt;
	}
	public Object getPayLoad() {
		return payLoad;
	}
	public void setPayLoad(Object payLoad) {
		this.payLoad = payLoad;
	}
	/**
	 * @return the committeeType
	 */
	public String getCommitteeType() {
		return committeeType;
	}
	/**
	 * @param committeeType the committeeType to set
	 */
	public void setCommitteeType(String committeeType) {
		this.committeeType = committeeType;
	}
	/**
	 * @return the committeeTypeDesc
	 */
	public String getCommitteeTypeDesc() {
		return committeeTypeDesc;
	}
	/**
	 * @param committeeTypeDesc the committeeTypeDesc to set
	 */
	public void setCommitteeTypeDesc(String committeeTypeDesc) {
		this.committeeTypeDesc = committeeTypeDesc;
	}

}
