/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.managedbean.sc.evaluation;

import java.util.Date;
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
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * The Class EvaluationMeetingManagedBean.
 * 
 * @author Garikapati Mangaiah.
 */
@ManagedBean(name = "evaluationMeetingMB")
@SessionScoped
public class EvaluationMeetingManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The sc qt meetings. */
	private List<ScQtMeeting> scQtMeetings;

	/** The meeting multi select. */
	private List<ScQtMeeting> meetingMultiSelect;

	/** The committe member. */
	private ScCommitteeMember committeMember;

	/** The selections. */
	private List<SelectItem> selections;

	/** The tab title. */
	private String tabTitle;

	/** The name. */
	private String name = null;

	/** The identification no. */
	private String identificationNo = null;

	/** The identification type. */
	private String identificationType;

	/** The selected pm user. */
	private List<PmUser> selectedPmUser;

	/** The role types. */
	private SelectItem[] roleTypes; // Role type drop down

	/** The role name. */
	private String roleName;

	/** The committee members list. */
	private List<ScCommitteeMember> committeeMembersList;

	/** The committee meeting select list. */
	private List<ScCommitteeMember> committeeMeetingSelectList;

	/** The clone qtn tender search bean. */
	private QtnTenderSearchBean cloneQtnTenderSearchBean;

	/** The clone tender list. */
	private List<ScQt> cloneTenderList;

	/** The selected clone tender. */
	private List<ScQt> selectedCloneTender;

	/** The clone committe memebers. */
	private List<ScCommitteeMember> cloneCommitteMemebers;

	/** The clone committe memebers select. */
	private List<ScCommitteeMember> cloneCommitteMemebersSelect;

	/** The committee member activity. */
	private List<ScCommitteeMember> committeeMemberActivity;

	/** The committee activity select. */
	private List<ScCommitteeMember> committeeActivitySelect;

	/** The sc qt committee. */
	private ScQtCommittee scQtCommittee;

	/** The qt committee remarks. */
	private List<ScQtCommitteRemarks> qtCommitteeRemarks;

	/** The sc committee member list. */
	private List<ScCommitteeMember> scCommitteeMemberList;

	/** The qt. */
	private ScQt qt;

	/** The committee type. */
	private String committeeType;

	/** The committee type desc. */
	private String committeeTypeDesc;

	/** The meeting remove list. */
	private List<ScQtMeeting> meetingRemoveList;

	/** The remove committee list. */
	private List<ScCommitteeMember> removeCommitteeList;

	/** The template spec bean. */
	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();

	private ScQt cloneSelectQt;
	
	 /** The auto update. */
    private boolean autoUpdate = Boolean.TRUE;

	/** The today. */
	private Date today = new Date();
	private List<ScCommitteeMember> otherCommitteeMembersList;
	private String checkOtherCommitteeType;
	private String checkOtherCommitteeTypes;

	/**
	 * Gets the today.
	 * 
	 * @return the today
	 */
	public Date getToday() {
		return today;
	}

	/**
	 * Sets the today.
	 * 
	 * @param today
	 *            the new today
	 */
	public void setToday(Date today) {
		this.today = today;
	}

	/**
	 * Gets the template spec bean.
	 * 
	 * @return the template spec bean
	 */
	public TemplateSpecBean getTemplateSpecBean() {
		return templateSpecBean;
	}

	/**
	 * Sets the template spec bean.
	 * 
	 * @param templateSpecBean
	 *            the new template spec bean
	 */
	public void setTemplateSpecBean(TemplateSpecBean templateSpecBean) {
		this.templateSpecBean = templateSpecBean;
	}

	/**
	 * Gets the meeting remove list.
	 * 
	 * @return the meeting remove list
	 */
	public List<ScQtMeeting> getMeetingRemoveList() {
		return meetingRemoveList;
	}

	/**
	 * Sets the meeting remove list.
	 * 
	 * @param meetingRemoveList
	 *            the new meeting remove list
	 */
	public void setMeetingRemoveList(List<ScQtMeeting> meetingRemoveList) {
		this.meetingRemoveList = meetingRemoveList;
	}

	/**
	 * Gets the sc qt meetings.
	 * 
	 * @return the sc qt meetings
	 */
	public List<ScQtMeeting> getScQtMeetings() {
		return scQtMeetings;
	}

	/**
	 * Sets the sc qt meetings.
	 * 
	 * @param scQtMeetings
	 *            the new sc qt meetings
	 */
	public void setScQtMeetings(List<ScQtMeeting> scQtMeetings) {
		this.scQtMeetings = scQtMeetings;
	}

	/**
	 * Gets the meeting multi select.
	 * 
	 * @return the meeting multi select
	 */
	public List<ScQtMeeting> getMeetingMultiSelect() {
		return meetingMultiSelect;
	}

	/**
	 * Sets the meeting multi select.
	 * 
	 * @param meetingMultiSelect
	 *            the new meeting multi select
	 */
	public void setMeetingMultiSelect(List<ScQtMeeting> meetingMultiSelect) {
		this.meetingMultiSelect = meetingMultiSelect;
	}

	/**
	 * Gets the committe member.
	 * 
	 * @return the committe member
	 */
	public ScCommitteeMember getCommitteMember() {
		return committeMember;
	}

	/**
	 * Sets the committe member.
	 * 
	 * @param committeMember
	 *            the new committe member
	 */
	public void setCommitteMember(ScCommitteeMember committeMember) {
		this.committeMember = committeMember;
	}

	/**
	 * Gets the selections.
	 * 
	 * @return the selections
	 */
	public List<SelectItem> getSelections() {
		return selections;
	}

	/**
	 * Sets the selections.
	 * 
	 * @param selections
	 *            the new selections
	 */
	public void setSelections(List<SelectItem> selections) {
		this.selections = selections;
	}

	/**
	 * Gets the tab title.
	 * 
	 * @return the tab title
	 */
	public String getTabTitle() {
		return tabTitle;
	}

	/**
	 * Sets the tab title.
	 * 
	 * @param tabTitle
	 *            the new tab title
	 */
	public void setTabTitle(String tabTitle) {
		this.tabTitle = tabTitle;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the identification no.
	 * 
	 * @return the identification no
	 */
	public String getIdentificationNo() {
		return identificationNo;
	}

	/**
	 * Sets the identification no.
	 * 
	 * @param identificationNo
	 *            the new identification no
	 */
	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}

	/**
	 * Gets the identification type.
	 * 
	 * @return the identification type
	 */
	public String getIdentificationType() {
		return identificationType;
	}

	/**
	 * Sets the identification type.
	 * 
	 * @param identificationType
	 *            the new identification type
	 */
	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}

	/**
	 * Gets the selected pm user.
	 * 
	 * @return the selected pm user
	 */
	public List<PmUser> getSelectedPmUser() {
		return selectedPmUser;
	}

	/**
	 * Sets the selected pm user.
	 * 
	 * @param selectedPmUser
	 *            the new selected pm user
	 */
	public void setSelectedPmUser(List<PmUser> selectedPmUser) {
		this.selectedPmUser = selectedPmUser;
	}

	/**
	 * Gets the role types.
	 * 
	 * @return the role types
	 */
	public SelectItem[] getRoleTypes() {
		return roleTypes;
	}

	/**
	 * Sets the role types.
	 * 
	 * @param roleTypes
	 *            the new role types
	 */
	public void setRoleTypes(SelectItem[] roleTypes) {
		this.roleTypes = roleTypes;
	}

	/**
	 * Gets the role name.
	 * 
	 * @return the role name
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * Sets the role name.
	 * 
	 * @param roleName
	 *            the new role name
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * Gets the committee members list.
	 * 
	 * @return the committee members list
	 */
	public List<ScCommitteeMember> getCommitteeMembersList() {
		return committeeMembersList;
	}

	/**
	 * Sets the committee members list.
	 * 
	 * @param committeeMembersList
	 *            the new committee members list
	 */
	public void setCommitteeMembersList(
			List<ScCommitteeMember> committeeMembersList) {
		this.committeeMembersList = committeeMembersList;
	}

	/**
	 * Gets the committee meeting select list.
	 * 
	 * @return the committee meeting select list
	 */
	public List<ScCommitteeMember> getCommitteeMeetingSelectList() {
		return committeeMeetingSelectList;
	}

	/**
	 * Sets the committee meeting select list.
	 * 
	 * @param committeeMeetingSelectList
	 *            the new committee meeting select list
	 */
	public void setCommitteeMeetingSelectList(
			List<ScCommitteeMember> committeeMeetingSelectList) {
		this.committeeMeetingSelectList = committeeMeetingSelectList;
	}

	/**
	 * Gets the clone qtn tender search bean.
	 * 
	 * @return the clone qtn tender search bean
	 */
	public QtnTenderSearchBean getCloneQtnTenderSearchBean() {
		return cloneQtnTenderSearchBean;
	}

	/**
	 * Sets the clone qtn tender search bean.
	 * 
	 * @param cloneQtnTenderSearchBean
	 *            the new clone qtn tender search bean
	 */
	public void setCloneQtnTenderSearchBean(
			QtnTenderSearchBean cloneQtnTenderSearchBean) {
		this.cloneQtnTenderSearchBean = cloneQtnTenderSearchBean;
	}

	/**
	 * Gets the clone tender list.
	 * 
	 * @return the clone tender list
	 */
	public List<ScQt> getCloneTenderList() {
		return cloneTenderList;
	}

	/**
	 * Sets the clone tender list.
	 * 
	 * @param cloneTenderList
	 *            the new clone tender list
	 */
	public void setCloneTenderList(List<ScQt> cloneTenderList) {
		this.cloneTenderList = cloneTenderList;
	}

	/**
	 * Gets the selected clone tender.
	 * 
	 * @return the selected clone tender
	 */
	public List<ScQt> getSelectedCloneTender() {
		return selectedCloneTender;
	}

	/**
	 * Sets the selected clone tender.
	 * 
	 * @param selectedCloneTender
	 *            the new selected clone tender
	 */
	public void setSelectedCloneTender(List<ScQt> selectedCloneTender) {
		this.selectedCloneTender = selectedCloneTender;
	}

	/**
	 * Gets the clone committe memebers.
	 * 
	 * @return the clone committe memebers
	 */
	public List<ScCommitteeMember> getCloneCommitteMemebers() {
		return cloneCommitteMemebers;
	}

	/**
	 * Sets the clone committe memebers.
	 * 
	 * @param cloneCommitteMemebers
	 *            the new clone committe memebers
	 */
	public void setCloneCommitteMemebers(
			List<ScCommitteeMember> cloneCommitteMemebers) {
		this.cloneCommitteMemebers = cloneCommitteMemebers;
	}

	/**
	 * Gets the clone committe memebers select.
	 * 
	 * @return the clone committe memebers select
	 */
	public List<ScCommitteeMember> getCloneCommitteMemebersSelect() {
		return cloneCommitteMemebersSelect;
	}

	/**
	 * Sets the clone committe memebers select.
	 * 
	 * @param cloneCommitteMemebersSelect
	 *            the new clone committe memebers select
	 */
	public void setCloneCommitteMemebersSelect(
			List<ScCommitteeMember> cloneCommitteMemebersSelect) {
		this.cloneCommitteMemebersSelect = cloneCommitteMemebersSelect;
	}

	/**
	 * Gets the sc qt committee.
	 * 
	 * @return the sc qt committee
	 */
	public ScQtCommittee getScQtCommittee() {
		return scQtCommittee;
	}

	/**
	 * Sets the sc qt committee.
	 * 
	 * @param scQtCommittee
	 *            the new sc qt committee
	 */
	public void setScQtCommittee(ScQtCommittee scQtCommittee) {
		this.scQtCommittee = scQtCommittee;
	}

	/**
	 * Gets the qt committee remarks.
	 * 
	 * @return the qt committee remarks
	 */
	public List<ScQtCommitteRemarks> getQtCommitteeRemarks() {
		return qtCommitteeRemarks;
	}

	/**
	 * Sets the qt committee remarks.
	 * 
	 * @param qtCommitteeRemarks
	 *            the new qt committee remarks
	 */
	public void setQtCommitteeRemarks(
			List<ScQtCommitteRemarks> qtCommitteeRemarks) {
		this.qtCommitteeRemarks = qtCommitteeRemarks;
	}

	/**
	 * Sets the sc committee member list.
	 * 
	 * @param scCommitteeMemberList
	 *            the new sc committee member list
	 */
	public void setScCommitteeMemberList(
			List<ScCommitteeMember> scCommitteeMemberList) {
		this.scCommitteeMemberList = scCommitteeMemberList;
	}

	/**
	 * Gets the sc committee member list.
	 * 
	 * @return the sc committee member list
	 */
	public List<ScCommitteeMember> getScCommitteeMemberList() {
		return scCommitteeMemberList;
	}

	/**
	 * Gets the qt.
	 * 
	 * @return the qt
	 */
	public ScQt getQt() {
		return qt;
	}

	/**
	 * Sets the qt.
	 * 
	 * @param qt
	 *            the new qt
	 */
	public void setQt(ScQt qt) {
		this.qt = qt;
	}

	/**
	 * Gets the committee type.
	 * 
	 * @return the committee type
	 */
	public String getCommitteeType() {
		return committeeType;
	}

	/**
	 * Sets the committee type.
	 * 
	 * @param committeeType
	 *            the new committee type
	 */
	public void setCommitteeType(String committeeType) {
		this.committeeType = committeeType;
	}

	/**
	 * Gets the committee type desc.
	 * 
	 * @return the committee type desc
	 */
	public String getCommitteeTypeDesc() {
		return committeeTypeDesc;
	}

	/**
	 * Sets the committee type desc.
	 * 
	 * @param committeeTypeDesc
	 *            the new committee type desc
	 */
	public void setCommitteeTypeDesc(String committeeTypeDesc) {
		this.committeeTypeDesc = committeeTypeDesc;
	}

	/**
	 * @return the committeeMemberActivity
	 */
	public List<ScCommitteeMember> getCommitteeMemberActivity() {
		return committeeMemberActivity;
	}

	/**
	 * @param committeeMemberActivity
	 *            the committeeMemberActivity to set
	 */
	public void setCommitteeMemberActivity(
			List<ScCommitteeMember> committeeMemberActivity) {
		this.committeeMemberActivity = committeeMemberActivity;
	}

	/**
	 * @return the committeeActivitySelect
	 */
	public List<ScCommitteeMember> getCommitteeActivitySelect() {
		return committeeActivitySelect;
	}

	/**
	 * @param committeeActivitySelect
	 *            the committeeActivitySelect to set
	 */
	public void setCommitteeActivitySelect(
			List<ScCommitteeMember> committeeActivitySelect) {
		this.committeeActivitySelect = committeeActivitySelect;
	}

	/**
	 * @return the removeCommitteeList
	 */
	public List<ScCommitteeMember> getRemoveCommitteeList() {
		return removeCommitteeList;
	}

	/**
	 * @param removeCommitteeList
	 *            the removeCommitteeList to set
	 */
	public void setRemoveCommitteeList(
			List<ScCommitteeMember> removeCommitteeList) {
		this.removeCommitteeList = removeCommitteeList;
	}

	/**
	 * @return the cloneSelectQt
	 */
	public ScQt getCloneSelectQt() {
		return cloneSelectQt;
	}

	/**
	 * @param cloneSelectQt
	 *            the cloneSelectQt to set
	 */
	public void setCloneSelectQt(ScQt cloneSelectQt) {
		this.cloneSelectQt = cloneSelectQt;
	}

	/**
	 * @return the otherCommitteeMembersList
	 */
	public List<ScCommitteeMember> getOtherCommitteeMembersList() {
		return otherCommitteeMembersList;
	}

	/**
	 * @param otherCommitteeMembersList
	 *            the otherCommitteeMembersList to set
	 */
	public void setOtherCommitteeMembersList(
			List<ScCommitteeMember> otherCommitteeMembersList) {
		this.otherCommitteeMembersList = otherCommitteeMembersList;
	}

	/**
	 * @return the checkOtherCommitteeType
	 */
	public String getCheckOtherCommitteeType() {
		return checkOtherCommitteeType;
	}

	/**
	 * @param checkOtherCommitteeType the checkOtherCommitteeType to set
	 */
	public void setCheckOtherCommitteeType(String checkOtherCommitteeType) {
		this.checkOtherCommitteeType = checkOtherCommitteeType;
	}

	/**
	 * @return the checkOtherCommitteeTypes
	 */
	public String getCheckOtherCommitteeTypes() {
		return checkOtherCommitteeTypes;
	}

	/**
	 * @param checkOtherCommitteeTypes the checkOtherCommitteeTypes to set
	 */
	public void setCheckOtherCommitteeTypes(String checkOtherCommitteeTypes) {
		this.checkOtherCommitteeTypes = checkOtherCommitteeTypes;
	}

	/**
	 * @return the autoUpdate
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

}
