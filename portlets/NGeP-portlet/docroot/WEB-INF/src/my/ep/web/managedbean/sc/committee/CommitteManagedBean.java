/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.managedbean.sc.committee;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeRemark;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.UserCommitteeSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

// TODO: Auto-generated Javadoc
/**
 * The Class CommitteManagedBean.
 *
 * @author VidyasagarReddy
 */
@ManagedBean(name = "committeManagedBean")
@SessionScoped
public class CommitteManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(CommitteManagedBean.class);

	/** The qt. */
	private ScQt qt;

	/** The out comes. */
	private String outComes[];

	/** The task id. */
	private String taskId;

	/** The qtcommittees. */
	private List<ScQtCommittee> qtcommittees;
	
	/** The qt committee. */
	private ScQtCommittee qtCommittee;

	/** The selected committe. */
	private String selectedCommitte;

	/** The committe type. */
	private List<SelectItem> committeType;

	/** The committe member. */
	private ScCommitteeMember committeMember = new ScCommitteeMember();

	/** The committe members. */
	private List<ScCommitteeMember> committeMembers;
	
	/** The selected committe memebers. */
	private List<ScCommitteeMember> selectedCommitteMemebers;

	// clone committee
	/** The clone committe memebers. */
	private List<ScCommitteeMember> cloneCommitteMemebers;
	
	/** The selected clone committe memebers. */
	private List<ScCommitteeMember> selectedCloneCommitteMemebers;

	/** The minstry list. */
	private List<PmOrgProfile> minstryList;

	/** The division list. */
	private List<PmOrgProfile> divisionList;

	/** The dept list. */
	private List<PmOrgProfile> deptList;

	/** The sc committee. */
	private ScCommittee scCommittee;
	
	/** The sc committee remarks list. */
	private List<ScCommitteeRemark> scCommitteeRemarksList;

	/** The selections. */
	private List<SelectItem> selections;

	/** The user types. */
	private List<SelectItem> userTypes;

	/** The roles. */
	private List<SelectItem> roles;

	/** The ic type. */
	private String icType;

	/** The id types. */
	private List<SelectItem> idTypes;

	/** The approvers. */
	private List<PmUser> approvers;

	/** The approver id. */
	private String approverId;

	/** The gov users. */
	private List<PmUser> govUsers;

	/** The user id. */
	private String userId;
	
	/** The user search qc data model. */
	private IPagingDataModel<PmUser> userSearchQCDataModel;
	
	/** The user qc search bean. */
	private UserCommitteeSearchBean userQCSearchBean;
	
	/** The selected pm user. */
	private List<PmUser> selectedPmUser;
	
	/** The representatives typeslist. */
	private List<SelectItem> representativesTypeslist;
	
	/** The add representatives typeslist. */
	private List<SelectItem> addRepresentativesTypeslist;

	/** The tender num. */
	private String tenderNum;
	
	/** The sc qt. */
	private List<ScQt> scQt;

	/** The clone search committes. */
	private List<ScQtCommittee> cloneSearchCommittes;
	
	/** The selected clone committee. */
	private ScQtCommittee selectedCloneCommittee;

	/** The approval status. */
	private List<SelectItem> approvalStatus;

	// Search properties
	/** The title. */
	private String title = null;
	
	/** The committe no. */
	private String committeNo = null;
	
	/** The created dt. */
	private Date createdDt;
	
	/** The status. */
	private String status;
	
	/** The committee type. */
	private String committeeType;
	
	/** The qt no. */
	private String qtNo = null;
	
	/** The procurement mode. */
	private String procurementMode;
	
	/** The created dt from. */
	private Date createdDtFrom;
	
	/** The created dt to. */
	private Date createdDtTo;

	/** The is editable. */
	private boolean isEditable = true;
	
	private IPagingDataModel<ScQtCommittee> committeeQTDataModel;

	// Search Committee
	/** The search qt data model. */
	private IPagingDataModel<ScQtCommittee> searchQTDataModel;
	
	/** The clone search qt data model. */
	private IPagingDataModel<ScQt> cloneSearchQTDataModel;

	/** The qtn tender search bean. */
	private QtnTenderSearchBean qtnTenderSearchBean;
	
	/** The clone qtn tender search bean. */
	private QtnTenderSearchBean cloneQtnTenderSearchBean;
	
	/** The qtn tender list. */
	private List<ScQt> qtnTenderList = new ArrayList<ScQt>();
	
	/** The clone committee tender list. */
	private List<ScQt> cloneCommitteeTenderList = new ArrayList<ScQt>();
	
	/** The selected clone tender. */
	private ScQt selectedCloneTender;

	/** The committee node. */
	private TreeNode committeeNode;

	/** The sc committee member list. */
	private List<ScCommitteeMember> scCommitteeMemberList;

	/** The name. */
	private String name = null;
	
	/** The identification no. */
	private String identificationNo = null;
	
	/** The identification type. */
	private String identificationType;

	/** The role types. */
	private SelectItem[] roleTypes; // Role type drop down

	/** The header title. */
	private String headerTitle;
	
	/** The comm type title. */
	private String commTypeTitle;
	
	/** The selected history committee. */
	private ScCommittee selectedHistoryCommittee;

	/** The history committees. */
	private List<ScCommittee> historyCommittees;

	/** The auto update. */
    private boolean autoUpdate = Boolean.TRUE;
    
	/** The to be del committe memebers. */
	private List<ScCommitteeMember> toBeDelCommitteMemebers;
	
	/** Flag to indicate selection of all committee members **/
	private boolean selectAllMembers;
    
	/**
	 * Instantiates a new committe managed bean.
	 */
	public CommitteManagedBean() {
		log.info("inside ");

		qtnTenderSearchBean = new QtnTenderSearchBean();
		cloneQtnTenderSearchBean = new QtnTenderSearchBean();
		
		userQCSearchBean = new UserCommitteeSearchBean(); 
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
	 * @param roleTypes the new role types
	 */
	public void setRoleTypes(SelectItem[] roleTypes) {
		this.roleTypes = roleTypes;
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
	 * @param selectedPmUser the new selected pm user
	 */
	public void setSelectedPmUser(List<PmUser> selectedPmUser) {
		this.selectedPmUser = selectedPmUser;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
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
	 * @param identificationNo the new identification no
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
	 * @param identificationType the new identification type
	 */
	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
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
	 * Sets the sc committee member list.
	 *
	 * @param scCommitteeMemberList the new sc committee member list
	 */
	public void setScCommitteeMemberList(
			List<ScCommitteeMember> scCommitteeMemberList) {
		this.scCommitteeMemberList = scCommitteeMemberList;
	}

	/**
	 * Gets the committee node.
	 *
	 * @return the committee node
	 */
	public TreeNode getCommitteeNode() {
		return committeeNode;
	}

	/**
	 * Sets the committee node.
	 *
	 * @param committeeNode the new committee node
	 */
	public void setCommitteeNode(TreeNode committeeNode) {
		this.committeeNode = committeeNode;
	}

	/**
	 * Gets the selected clone committee.
	 *
	 * @return the selected clone committee
	 */
	public ScQtCommittee getSelectedCloneCommittee() {
		return selectedCloneCommittee;
	}

	/**
	 * Sets the selected clone committee.
	 *
	 * @param selectedCloneCommittee the new selected clone committee
	 */
	public void setSelectedCloneCommittee(ScQtCommittee selectedCloneCommittee) {
		this.selectedCloneCommittee = selectedCloneCommittee;
	}

	/* (non-Javadoc)
	 * @see my.ep.web.managedbean.BaseManagedBean#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	/* (non-Javadoc)
	 * @see my.ep.web.managedbean.BaseManagedBean#setTitle(java.lang.String)
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the procurement mode.
	 *
	 * @return the procurement mode
	 */
	public String getProcurementMode() {
		return procurementMode;
	}

	/**
	 * Sets the procurement mode.
	 *
	 * @param procurementMode the new procurement mode
	 */
	public void setProcurementMode(String procurementMode) {
		this.procurementMode = procurementMode;
	}

	/**
	 * Gets the created dt from.
	 *
	 * @return the created dt from
	 */
	public Date getCreatedDtFrom() {
		return createdDtFrom;
	}

	/**
	 * Sets the created dt from.
	 *
	 * @param createdDtFrom the new created dt from
	 */
	public void setCreatedDtFrom(Date createdDtFrom) {
		this.createdDtFrom = createdDtFrom;
	}

	/**
	 * Gets the created dt to.
	 *
	 * @return the created dt to
	 */
	public Date getCreatedDtTo() {
		return createdDtTo;
	}

	/**
	 * Sets the created dt to.
	 *
	 * @param createdDtTo the new created dt to
	 */
	public void setCreatedDtTo(Date createdDtTo) {
		this.createdDtTo = createdDtTo;
	}

	/**
	 * Gets the selected committe.
	 *
	 * @return the selected committe
	 */
	public String getSelectedCommitte() {
		return selectedCommitte;
	}

	/**
	 * Sets the selected committe.
	 *
	 * @param selectedCommitte the new selected committe
	 */
	public void setSelectedCommitte(String selectedCommitte) {
		this.selectedCommitte = selectedCommitte;
	}

	/**
	 * Gets the qtcommittees.
	 *
	 * @return the qtcommittees
	 */
	public List<ScQtCommittee> getQtcommittees() {
		return qtcommittees;
	}

	/**
	 * Sets the qtcommittees.
	 *
	 * @param qtcommittees the new qtcommittees
	 */
	public void setQtcommittees(List<ScQtCommittee> qtcommittees) {
		this.qtcommittees = qtcommittees;
	}

	/**
	 * Gets the qt committee.
	 *
	 * @return the qt committee
	 */
	public ScQtCommittee getQtCommittee() {
		return qtCommittee;
	}

	/**
	 * Sets the qt committee.
	 *
	 * @param qtCommittee the new qt committee
	 */
	public void setQtCommittee(ScQtCommittee qtCommittee) {
		this.qtCommittee = qtCommittee;
	}

	/**
	 * Gets the committe type.
	 *
	 * @return the committe type
	 */
	public List<SelectItem> getCommitteType() {
		return committeType;
	}

	/**
	 * Sets the committe type.
	 *
	 * @param committeType the new committe type
	 */
	public void setCommitteType(List<SelectItem> committeType) {
		this.committeType = committeType;
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
	 * @param committeMember the new committe member
	 */
	public void setCommitteMember(ScCommitteeMember committeMember) {
		this.committeMember = committeMember;
	}

	/**
	 * Gets the committe members.
	 *
	 * @return the committe members
	 */
	public List<ScCommitteeMember> getCommitteMembers() {
		return committeMembers;
	}

	/**
	 * Sets the committe members.
	 *
	 * @param committeMembers the new committe members
	 */
	public void setCommitteMembers(List<ScCommitteeMember> committeMembers) {
		this.committeMembers = committeMembers;
	}

	/**
	 * Gets the sc committee.
	 *
	 * @return the sc committee
	 */
	public ScCommittee getScCommittee() {
		return scCommittee;
	}

	/**
	 * Sets the sc committee.
	 *
	 * @param scCommittee the new sc committee
	 */
	public void setScCommittee(ScCommittee scCommittee) {
		this.scCommittee = scCommittee;
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
	 * @param selections the new selections
	 */
	public void setSelections(List<SelectItem> selections) {
		this.selections = selections;
	}

	/**
	 * Gets the user types.
	 *
	 * @return the user types
	 */
	public List<SelectItem> getUserTypes() {
		return userTypes;
	}

	/**
	 * Sets the user types.
	 *
	 * @param userTypes the new user types
	 */
	public void setUserTypes(List<SelectItem> userTypes) {
		this.userTypes = userTypes;
	}

	/**
	 * Gets the roles.
	 *
	 * @return the roles
	 */
	public List<SelectItem> getRoles() {
		return roles;
	}

	/**
	 * Sets the roles.
	 *
	 * @param roles the new roles
	 */
	public void setRoles(List<SelectItem> roles) {
		this.roles = roles;
	}

	/**
	 * Gets the ic type.
	 *
	 * @return the ic type
	 */
	public String getIcType() {
		return icType;
	}

	/**
	 * Sets the ic type.
	 *
	 * @param icType the new ic type
	 */
	public void setIcType(String icType) {
		this.icType = icType;
	}

	/**
	 * Gets the id types.
	 *
	 * @return the id types
	 */
	public List<SelectItem> getIdTypes() {
		return idTypes;
	}

	/**
	 * Sets the id types.
	 *
	 * @param idTypes the new id types
	 */
	public void setIdTypes(List<SelectItem> idTypes) {
		this.idTypes = idTypes;
	}

	/**
	 * Gets the qt no.
	 *
	 * @return the qt no
	 */
	public String getQtNo() {
		return qtNo;
	}

	/**
	 * Sets the qt no.
	 *
	 * @param qtNo the new qt no
	 */
	public void setQtNo(String qtNo) {
		this.qtNo = qtNo;
	}

	/**
	 * Gets the created dt.
	 *
	 * @return the created dt
	 */
	public Date getCreatedDt() {
		return createdDt;
	}

	/**
	 * Sets the created dt.
	 *
	 * @param createdDt the new created dt
	 */
	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Gets the approver id.
	 *
	 * @return the approver id
	 */
	public String getApproverId() {
		return approverId;
	}

	/**
	 * Sets the approver id.
	 *
	 * @param approverId the new approver id
	 */
	public void setApproverId(String approverId) {
		this.approverId = approverId;
	}

	/**
	 * Gets the committe no.
	 *
	 * @return the committe no
	 */
	public String getCommitteNo() {
		return committeNo;
	}

	/**
	 * Sets the committe no.
	 *
	 * @param committeNo the new committe no
	 */
	public void setCommitteNo(String committeNo) {
		this.committeNo = committeNo;
	}

	/**
	 * Gets the tender num.
	 *
	 * @return the tender num
	 */
	public String getTenderNum() {
		return tenderNum;
	}

	/**
	 * Sets the tender num.
	 *
	 * @param tenderNum the new tender num
	 */
	public void setTenderNum(String tenderNum) {
		this.tenderNum = tenderNum;
	}

	/**
	 * Gets the sc qt.
	 *
	 * @return the sc qt
	 */
	public List<ScQt> getScQt() {
		return scQt;
	}

	/**
	 * Sets the sc qt.
	 *
	 * @param scQt the new sc qt
	 */
	public void setScQt(List<ScQt> scQt) {
		this.scQt = scQt;
	}

	/**
	 * Gets the clone search committes.
	 *
	 * @return the clone search committes
	 */
	public List<ScQtCommittee> getCloneSearchCommittes() {
		return cloneSearchCommittes;
	}

	/**
	 * Sets the clone search committes.
	 *
	 * @param cloneSearchCommittes the new clone search committes
	 */
	public void setCloneSearchCommittes(List<ScQtCommittee> cloneSearchCommittes) {
		this.cloneSearchCommittes = cloneSearchCommittes;
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
	 * @param cloneCommitteMemebers the new clone committe memebers
	 */
	public void setCloneCommitteMemebers(
			List<ScCommitteeMember> cloneCommitteMemebers) {
		this.cloneCommitteMemebers = cloneCommitteMemebers;
	}

	/**
	 * Gets the selected committe memebers.
	 *
	 * @return the selected committe memebers
	 */
	public List<ScCommitteeMember> getSelectedCommitteMemebers() {
		return selectedCommitteMemebers;
	}

	/**
	 * Sets the selected committe memebers.
	 *
	 * @param selectedCommitteMemebers the new selected committe memebers
	 */
	public void setSelectedCommitteMemebers(
			List<ScCommitteeMember> selectedCommitteMemebers) {
		this.selectedCommitteMemebers = selectedCommitteMemebers;
	}

	/**
	 * Gets the approval status.
	 *
	 * @return the approval status
	 */
	public List<SelectItem> getApprovalStatus() {
		return approvalStatus;
	}

	/**
	 * Sets the approval status.
	 *
	 * @param approvalStatus the new approval status
	 */
	public void setApprovalStatus(List<SelectItem> approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	/**
	 * Gets the gov users.
	 *
	 * @return the gov users
	 */
	public List<PmUser> getGovUsers() {
		return govUsers;
	}

	/**
	 * Sets the gov users.
	 *
	 * @param govUsers the new gov users
	 */
	public void setGovUsers(List<PmUser> govUsers) {
		this.govUsers = govUsers;
	}

	/**
	 * Gets the minstry list.
	 *
	 * @return the minstry list
	 */
	public List<PmOrgProfile> getMinstryList() {
		return minstryList;
	}

	/**
	 * Sets the minstry list.
	 *
	 * @param minstryList the new minstry list
	 */
	public void setMinstryList(List<PmOrgProfile> minstryList) {
		this.minstryList = minstryList;
	}

	/**
	 * Gets the division list.
	 *
	 * @return the division list
	 */
	public List<PmOrgProfile> getDivisionList() {
		return divisionList;
	}

	/**
	 * Sets the division list.
	 *
	 * @param divisionList the new division list
	 */
	public void setDivisionList(List<PmOrgProfile> divisionList) {
		this.divisionList = divisionList;
	}

	/**
	 * Gets the dept list.
	 *
	 * @return the dept list
	 */
	public List<PmOrgProfile> getDeptList() {
		return deptList;
	}

	/**
	 * Sets the dept list.
	 *
	 * @param deptList the new dept list
	 */
	public void setDeptList(List<PmOrgProfile> deptList) {
		this.deptList = deptList;
	}

	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets the user id.
	 *
	 * @param userId the new user id
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Gets the approvers.
	 *
	 * @return the approvers
	 */
	public List<PmUser> getApprovers() {
		return approvers;
	}

	/**
	 * Sets the approvers.
	 *
	 * @param approvers the new approvers
	 */
	public void setApprovers(List<PmUser> approvers) {
		this.approvers = approvers;
	}

	/* (non-Javadoc)
	 * @see my.ep.web.managedbean.BaseManagedBean#getOutComes()
	 */
	public String[] getOutComes() {
		return outComes;
	}

	/* (non-Javadoc)
	 * @see my.ep.web.managedbean.BaseManagedBean#setOutComes(java.lang.String[])
	 */
	public void setOutComes(String[] outComes) {
		this.outComes = outComes;
	}

	/* (non-Javadoc)
	 * @see my.ep.web.managedbean.BaseManagedBean#getTaskId()
	 */
	public String getTaskId() {
		return taskId;
	}

	/* (non-Javadoc)
	 * @see my.ep.web.managedbean.BaseManagedBean#setTaskId(java.lang.String)
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * Checks if is editable.
	 *
	 * @return true, if is editable
	 */
	public boolean isEditable() {
		return isEditable;
	}

	/**
	 * Sets the editable.
	 *
	 * @param isEditable the new editable
	 */
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
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
	 * @param committeeType the new committee type
	 */
	public void setCommitteeType(String committeeType) {
		this.committeeType = committeeType;
	}

	/**
	 * Gets the qtn tender search bean.
	 *
	 * @return the qtn tender search bean
	 */
	public QtnTenderSearchBean getQtnTenderSearchBean() {
		return qtnTenderSearchBean;
	}

	/**
	 * Sets the qtn tender search bean.
	 *
	 * @param qtnTenderSearchBean the new qtn tender search bean
	 */
	public void setQtnTenderSearchBean(QtnTenderSearchBean qtnTenderSearchBean) {
		this.qtnTenderSearchBean = qtnTenderSearchBean;
	}

	/**
	 * Gets the qtn tender list.
	 *
	 * @return the qtn tender list
	 */
	public List<ScQt> getQtnTenderList() {
		return qtnTenderList;
	}

	/**
	 * Sets the qtn tender list.
	 *
	 * @param qtnTenderList the new qtn tender list
	 */
	public void setQtnTenderList(List<ScQt> qtnTenderList) {
		this.qtnTenderList = qtnTenderList;
	}

	/**
	 * Gets the search qt data model.
	 *
	 * @return the search qt data model
	 */
	public IPagingDataModel<ScQtCommittee> getSearchQTDataModel() {
		return searchQTDataModel;
	}

	/**
	 * Sets the search qt data model.
	 *
	 * @param searchQTDataModel the new search qt data model
	 */
	public void setSearchQTDataModel(
			IPagingDataModel<ScQtCommittee> searchQTDataModel) {
		this.searchQTDataModel = searchQTDataModel;
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
	 * @param qt the new qt
	 */
	public void setQt(ScQt qt) {
		this.qt = qt;
	}

	/**
	 * Gets the header title.
	 *
	 * @return the header title
	 */
	public String getHeaderTitle() {
		return headerTitle;
	}

	/**
	 * Sets the header title.
	 *
	 * @param headerTitle the new header title
	 */
	public void setHeaderTitle(String headerTitle) {
		this.headerTitle = headerTitle;
	}

	/**
	 * Gets the comm type title.
	 *
	 * @return the comm type title
	 */
	public String getCommTypeTitle() {
		return commTypeTitle;
	}

	/**
	 * Sets the comm type title.
	 *
	 * @param commTypeTitle the new comm type title
	 */
	public void setCommTypeTitle(String commTypeTitle) {
		this.commTypeTitle = commTypeTitle;
	}

	/**
	 * Gets the sc committee remarks list.
	 *
	 * @return the sc committee remarks list
	 */
	public List<ScCommitteeRemark> getScCommitteeRemarksList() {
		return scCommitteeRemarksList;
	}

	/**
	 * Sets the sc committee remarks list.
	 *
	 * @param scCommitteeRemarksList the new sc committee remarks list
	 */
	public void setScCommitteeRemarksList(
			List<ScCommitteeRemark> scCommitteeRemarksList) {
		this.scCommitteeRemarksList = scCommitteeRemarksList;
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
	 * @param cloneQtnTenderSearchBean the new clone qtn tender search bean
	 */
	public void setCloneQtnTenderSearchBean(
			QtnTenderSearchBean cloneQtnTenderSearchBean) {
		this.cloneQtnTenderSearchBean = cloneQtnTenderSearchBean;
	}

	/**
	 * Gets the clone committee tender list.
	 *
	 * @return the clone committee tender list
	 */
	public List<ScQt> getCloneCommitteeTenderList() {
		return cloneCommitteeTenderList;
	}

	/**
	 * Sets the clone committee tender list.
	 *
	 * @param cloneCommitteeTenderList the new clone committee tender list
	 */
	public void setCloneCommitteeTenderList(List<ScQt> cloneCommitteeTenderList) {
		this.cloneCommitteeTenderList = cloneCommitteeTenderList;
	}

	/**
	 * Gets the selected clone tender.
	 *
	 * @return the selected clone tender
	 */
	public ScQt getSelectedCloneTender() {
		return selectedCloneTender;
	}

	/**
	 * Sets the selected clone tender.
	 *
	 * @param selectedCloneTender the new selected clone tender
	 */
	public void setSelectedCloneTender(ScQt selectedCloneTender) {
		this.selectedCloneTender = selectedCloneTender;
	}

	/**
	 * Gets the selected clone committe memebers.
	 *
	 * @return the selected clone committe memebers
	 */
	public List<ScCommitteeMember> getSelectedCloneCommitteMemebers() {
		return selectedCloneCommitteMemebers;
	}

	/**
	 * Sets the selected clone committe memebers.
	 *
	 * @param selectedCloneCommitteMemebers the new selected clone committe memebers
	 */
	public void setSelectedCloneCommitteMemebers(
			List<ScCommitteeMember> selectedCloneCommitteMemebers) {
		this.selectedCloneCommitteMemebers = selectedCloneCommitteMemebers;
	}

	/**
	 * Gets the clone search qt data model.
	 *
	 * @return the clone search qt data model
	 */
	public IPagingDataModel<ScQt> getCloneSearchQTDataModel() {
		return cloneSearchQTDataModel;
	}

	/**
	 * Sets the clone search qt data model.
	 *
	 * @param cloneSearchQTDataModel the new clone search qt data model
	 */
	public void setCloneSearchQTDataModel(
			IPagingDataModel<ScQt> cloneSearchQTDataModel) {
		this.cloneSearchQTDataModel = cloneSearchQTDataModel;
	}

	/**
	 * Gets the user search qc data model.
	 *
	 * @return the user search qc data model
	 */
	public IPagingDataModel<PmUser> getUserSearchQCDataModel() {
		return userSearchQCDataModel;
	}

	/**
	 * Sets the user search qc data model.
	 *
	 * @param userSearchQCDataModel the new user search qc data model
	 */
	public void setUserSearchQCDataModel(
			IPagingDataModel<PmUser> userSearchQCDataModel) {
		this.userSearchQCDataModel = userSearchQCDataModel;
	}

	/**
	 * Gets the user qc search bean.
	 *
	 * @return the user qc search bean
	 */
	public UserCommitteeSearchBean getUserQCSearchBean() {
		return userQCSearchBean;
	}

	/**
	 * Sets the user qc search bean.
	 *
	 * @param userQCSearchBean the new user qc search bean
	 */
	public void setUserQCSearchBean(UserCommitteeSearchBean userQCSearchBean) {
		this.userQCSearchBean = userQCSearchBean;
	}

	/**
	 * Gets the representatives typeslist.
	 *
	 * @return the representatives typeslist
	 */
	public List<SelectItem> getRepresentativesTypeslist() {
		return representativesTypeslist;
	}

	/**
	 * Sets the representatives typeslist.
	 *
	 * @param representativesTypeslist the new representatives typeslist
	 */
	public void setRepresentativesTypeslist(
			List<SelectItem> representativesTypeslist) {
		this.representativesTypeslist = representativesTypeslist;
	}

	/**
	 * Gets the adds the representatives typeslist.
	 *
	 * @return the adds the representatives typeslist
	 */
	public List<SelectItem> getAddRepresentativesTypeslist() {
		return addRepresentativesTypeslist;
	}

	/**
	 * Sets the adds the representatives typeslist.
	 *
	 * @param addRepresentativesTypeslist the new adds the representatives typeslist
	 */
	public void setAddRepresentativesTypeslist(
			List<SelectItem> addRepresentativesTypeslist) {
		this.addRepresentativesTypeslist = addRepresentativesTypeslist;
	}

	/**
	 * @return the selectedHistoryCommittee
	 */
	public ScCommittee getSelectedHistoryCommittee() {
		return selectedHistoryCommittee;
	}

	/**
	 * @param selectedHistoryCommittee the selectedHistoryCommittee to set
	 */
	public void setSelectedHistoryCommittee(ScCommittee selectedHistoryCommittee) {
		this.selectedHistoryCommittee = selectedHistoryCommittee;
	}

	/**
	 * @return the historyCommittees
	 */
	public List<ScCommittee> getHistoryCommittees() {
		return historyCommittees;
	}

	/**
	 * @param historyCommittees the historyCommittees to set
	 */
	public void setHistoryCommittees(List<ScCommittee> historyCommittees) {
		this.historyCommittees = historyCommittees;
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

	/**
	 * @return the toBeDelCommitteMemebers
	 */
	public List<ScCommitteeMember> getToBeDelCommitteMemebers() {
		return toBeDelCommitteMemebers;
	}

	/**
	 * @param toBeDelCommitteMemebers the toBeDelCommitteMemebers to set
	 */
	public void setToBeDelCommitteMemebers(
			List<ScCommitteeMember> toBeDelCommitteMemebers) {
		this.toBeDelCommitteMemebers = toBeDelCommitteMemebers;
	}

	/**
	 * @return the selectAllMembers
	 */
	public boolean isSelectAllMembers() {
		return selectAllMembers;
	}

	/**
	 * @param selectAllMembers 
	 * 				the selectAllMembers to set
	 */
	public void setSelectAllMembers(boolean selectAllMembers) {
		this.selectAllMembers = selectAllMembers;
	}

	/**
	 * @return the committeeQTDataModel
	 */
	public IPagingDataModel<ScQtCommittee> getCommitteeQTDataModel() {
		return committeeQTDataModel;
	}

	/**
	 * @param committeeQTDataModel the committeeQTDataModel to set
	 */
	public void setCommitteeQTDataModel(
			IPagingDataModel<ScQtCommittee> committeeQTDataModel) {
		this.committeeQTDataModel = committeeQTDataModel;
	}

}