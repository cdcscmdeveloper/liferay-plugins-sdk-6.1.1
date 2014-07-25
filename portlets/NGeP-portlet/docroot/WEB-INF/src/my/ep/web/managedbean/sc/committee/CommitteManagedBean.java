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

/**
 * @author VidyasagarReddy
 * 
 */
@ManagedBean(name = "committeManagedBean")
@SessionScoped
public class CommitteManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger
			.getLogger(CommitteManagedBean.class);

	private ScQt qt;

	private String outComes[];

	private String taskId;

	private List<ScQtCommittee> qtcommittees;
	private ScQtCommittee qtCommittee;

	private String selectedCommitte;

	private List<SelectItem> committeType;

	private ScCommitteeMember committeMember = new ScCommitteeMember();

	private List<ScCommitteeMember> committeMembers;
	private List<ScCommitteeMember> selectedCommitteMemebers;

	// clone committee
	private List<ScCommitteeMember> cloneCommitteMemebers;
	private List<ScCommitteeMember> selectedCloneCommitteMemebers;

	private List<PmOrgProfile> minstryList;

	private List<PmOrgProfile> divisionList;

	private List<PmOrgProfile> deptList;

	private ScCommittee scCommittee;
	private List<ScCommitteeRemark> scCommitteeRemarksList;

	private List<SelectItem> selections;

	private List<SelectItem> userTypes;

	private List<SelectItem> roles;

	private String icType;

	private List<SelectItem> idTypes;

	private List<PmUser> approvers;

	private String approverId;

	private List<PmUser> govUsers;

	private String userId;
	
	private IPagingDataModel<PmUser> userSearchQCDataModel;
	private UserCommitteeSearchBean userQCSearchBean;

	private List<PmUser> externalUsers;
	private List<PmUser> selectedPmUser;
	
	private List<SelectItem> representativesTypeslist;
	private List<SelectItem> addRepresentativesTypeslist;

	private String tenderNum;
	private List<ScQt> scQt;

	private List<ScQtCommittee> cloneSearchCommittes;
	private ScQtCommittee selectedCloneCommittee;

	private List<SelectItem> approvalStatus;

	// Search properties
	private String title = null;
	private String committeNo = null;
	private Date createdDt;
	private String status;
	private String committeeType;
	private String qtNo = null;
	private String procurementMode;
	private Date createdDtFrom;
	private Date createdDtTo;

	private boolean isEditable = true;

	// Search Committee
	private IPagingDataModel<ScQtCommittee> searchQTDataModel;
	private IPagingDataModel<ScQt> cloneSearchQTDataModel;

	private QtnTenderSearchBean qtnTenderSearchBean;
	private QtnTenderSearchBean cloneQtnTenderSearchBean;
	private List<ScQt> qtnTenderList = new ArrayList<ScQt>();
	private List<ScQt> cloneCommitteeTenderList = new ArrayList<ScQt>();
	private ScQt selectedCloneTender;

	private TreeNode committeeNode;

	private List<ScCommitteeMember> scCommitteeMemberList;

	private String name = null;
	private String identificationNo = null;
	private String identificationType;

	private SelectItem[] roleTypes; // Role type drop down

	private String headerTitle;
	private String commTypeTitle;

	public CommitteManagedBean() {
		log.info("inside ");

		qtnTenderSearchBean = new QtnTenderSearchBean();
		cloneQtnTenderSearchBean = new QtnTenderSearchBean();
		
		userQCSearchBean = new UserCommitteeSearchBean(); 
	}

	public SelectItem[] getRoleTypes() {
		return roleTypes;
	}

	public void setRoleTypes(SelectItem[] roleTypes) {
		this.roleTypes = roleTypes;
	}

	public String getName() {
		return name;
	}

	public List<PmUser> getSelectedPmUser() {
		return selectedPmUser;
	}

	public void setSelectedPmUser(List<PmUser> selectedPmUser) {
		this.selectedPmUser = selectedPmUser;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentificationNo() {
		return identificationNo;
	}

	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}

	public String getIdentificationType() {
		return identificationType;
	}

	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}

	public List<ScCommitteeMember> getScCommitteeMemberList() {
		return scCommitteeMemberList;
	}

	public void setScCommitteeMemberList(
			List<ScCommitteeMember> scCommitteeMemberList) {
		this.scCommitteeMemberList = scCommitteeMemberList;
	}

	public TreeNode getCommitteeNode() {
		return committeeNode;
	}

	public void setCommitteeNode(TreeNode committeeNode) {
		this.committeeNode = committeeNode;
	}

	public ScQtCommittee getSelectedCloneCommittee() {
		return selectedCloneCommittee;
	}

	public void setSelectedCloneCommittee(ScQtCommittee selectedCloneCommittee) {
		this.selectedCloneCommittee = selectedCloneCommittee;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProcurementMode() {
		return procurementMode;
	}

	public void setProcurementMode(String procurementMode) {
		this.procurementMode = procurementMode;
	}

	public Date getCreatedDtFrom() {
		return createdDtFrom;
	}

	public void setCreatedDtFrom(Date createdDtFrom) {
		this.createdDtFrom = createdDtFrom;
	}

	public Date getCreatedDtTo() {
		return createdDtTo;
	}

	public void setCreatedDtTo(Date createdDtTo) {
		this.createdDtTo = createdDtTo;
	}

	public String getSelectedCommitte() {
		return selectedCommitte;
	}

	public void setSelectedCommitte(String selectedCommitte) {
		this.selectedCommitte = selectedCommitte;
	}

	public List<ScQtCommittee> getQtcommittees() {
		return qtcommittees;
	}

	public void setQtcommittees(List<ScQtCommittee> qtcommittees) {
		this.qtcommittees = qtcommittees;
	}

	public ScQtCommittee getQtCommittee() {
		return qtCommittee;
	}

	public void setQtCommittee(ScQtCommittee qtCommittee) {
		this.qtCommittee = qtCommittee;
	}

	public List<SelectItem> getCommitteType() {
		return committeType;
	}

	public void setCommitteType(List<SelectItem> committeType) {
		this.committeType = committeType;
	}

	public ScCommitteeMember getCommitteMember() {
		return committeMember;
	}

	public void setCommitteMember(ScCommitteeMember committeMember) {
		this.committeMember = committeMember;
	}

	public List<ScCommitteeMember> getCommitteMembers() {
		return committeMembers;
	}

	public void setCommitteMembers(List<ScCommitteeMember> committeMembers) {
		this.committeMembers = committeMembers;
	}

	public ScCommittee getScCommittee() {
		return scCommittee;
	}

	public void setScCommittee(ScCommittee scCommittee) {
		this.scCommittee = scCommittee;
	}

	public List<SelectItem> getSelections() {
		return selections;
	}

	public void setSelections(List<SelectItem> selections) {
		this.selections = selections;
	}

	public List<SelectItem> getUserTypes() {
		return userTypes;
	}

	public void setUserTypes(List<SelectItem> userTypes) {
		this.userTypes = userTypes;
	}

	public List<SelectItem> getRoles() {
		return roles;
	}

	public void setRoles(List<SelectItem> roles) {
		this.roles = roles;
	}

	public String getIcType() {
		return icType;
	}

	public void setIcType(String icType) {
		this.icType = icType;
	}

	public List<SelectItem> getIdTypes() {
		return idTypes;
	}

	public void setIdTypes(List<SelectItem> idTypes) {
		this.idTypes = idTypes;
	}

	public String getQtNo() {
		return qtNo;
	}

	public void setQtNo(String qtNo) {
		this.qtNo = qtNo;
	}

	public Date getCreatedDt() {
		return createdDt;
	}

	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getApproverId() {
		return approverId;
	}

	public void setApproverId(String approverId) {
		this.approverId = approverId;
	}

	public String getCommitteNo() {
		return committeNo;
	}

	public void setCommitteNo(String committeNo) {
		this.committeNo = committeNo;
	}

	public String getTenderNum() {
		return tenderNum;
	}

	public void setTenderNum(String tenderNum) {
		this.tenderNum = tenderNum;
	}

	public List<ScQt> getScQt() {
		return scQt;
	}

	public void setScQt(List<ScQt> scQt) {
		this.scQt = scQt;
	}

	public List<ScQtCommittee> getCloneSearchCommittes() {
		return cloneSearchCommittes;
	}

	public void setCloneSearchCommittes(List<ScQtCommittee> cloneSearchCommittes) {
		this.cloneSearchCommittes = cloneSearchCommittes;
	}

	public List<ScCommitteeMember> getCloneCommitteMemebers() {
		return cloneCommitteMemebers;
	}

	public void setCloneCommitteMemebers(
			List<ScCommitteeMember> cloneCommitteMemebers) {
		this.cloneCommitteMemebers = cloneCommitteMemebers;
	}

	public List<ScCommitteeMember> getSelectedCommitteMemebers() {
		return selectedCommitteMemebers;
	}

	public void setSelectedCommitteMemebers(
			List<ScCommitteeMember> selectedCommitteMemebers) {
		this.selectedCommitteMemebers = selectedCommitteMemebers;
	}

	public List<SelectItem> getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(List<SelectItem> approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public List<PmUser> getGovUsers() {
		return govUsers;
	}

	public void setGovUsers(List<PmUser> govUsers) {
		this.govUsers = govUsers;
	}

	public List<PmOrgProfile> getMinstryList() {
		return minstryList;
	}

	public void setMinstryList(List<PmOrgProfile> minstryList) {
		this.minstryList = minstryList;
	}

	public List<PmOrgProfile> getDivisionList() {
		return divisionList;
	}

	public void setDivisionList(List<PmOrgProfile> divisionList) {
		this.divisionList = divisionList;
	}

	public List<PmOrgProfile> getDeptList() {
		return deptList;
	}

	public void setDeptList(List<PmOrgProfile> deptList) {
		this.deptList = deptList;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<PmUser> getExternalUsers() {
		return externalUsers;
	}

	public void setExternalUsers(List<PmUser> externalUsers) {
		this.externalUsers = externalUsers;
	}

	public List<PmUser> getApprovers() {
		return approvers;
	}

	public void setApprovers(List<PmUser> approvers) {
		this.approvers = approvers;
	}

	public String[] getOutComes() {
		return outComes;
	}

	public void setOutComes(String[] outComes) {
		this.outComes = outComes;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public String getCommitteeType() {
		return committeeType;
	}

	public void setCommitteeType(String committeeType) {
		this.committeeType = committeeType;
	}

	public QtnTenderSearchBean getQtnTenderSearchBean() {
		return qtnTenderSearchBean;
	}

	public void setQtnTenderSearchBean(QtnTenderSearchBean qtnTenderSearchBean) {
		this.qtnTenderSearchBean = qtnTenderSearchBean;
	}

	public List<ScQt> getQtnTenderList() {
		return qtnTenderList;
	}

	public void setQtnTenderList(List<ScQt> qtnTenderList) {
		this.qtnTenderList = qtnTenderList;
	}

	public IPagingDataModel<ScQtCommittee> getSearchQTDataModel() {
		return searchQTDataModel;
	}

	public void setSearchQTDataModel(
			IPagingDataModel<ScQtCommittee> searchQTDataModel) {
		this.searchQTDataModel = searchQTDataModel;
	}

	public ScQt getQt() {
		return qt;
	}

	public void setQt(ScQt qt) {
		this.qt = qt;
	}

	public String getHeaderTitle() {
		return headerTitle;
	}

	public void setHeaderTitle(String headerTitle) {
		this.headerTitle = headerTitle;
	}

	public String getCommTypeTitle() {
		return commTypeTitle;
	}

	public void setCommTypeTitle(String commTypeTitle) {
		this.commTypeTitle = commTypeTitle;
	}

	public List<ScCommitteeRemark> getScCommitteeRemarksList() {
		return scCommitteeRemarksList;
	}

	public void setScCommitteeRemarksList(
			List<ScCommitteeRemark> scCommitteeRemarksList) {
		this.scCommitteeRemarksList = scCommitteeRemarksList;
	}

	public QtnTenderSearchBean getCloneQtnTenderSearchBean() {
		return cloneQtnTenderSearchBean;
	}

	public void setCloneQtnTenderSearchBean(
			QtnTenderSearchBean cloneQtnTenderSearchBean) {
		this.cloneQtnTenderSearchBean = cloneQtnTenderSearchBean;
	}

	public List<ScQt> getCloneCommitteeTenderList() {
		return cloneCommitteeTenderList;
	}

	public void setCloneCommitteeTenderList(List<ScQt> cloneCommitteeTenderList) {
		this.cloneCommitteeTenderList = cloneCommitteeTenderList;
	}

	public ScQt getSelectedCloneTender() {
		return selectedCloneTender;
	}

	public void setSelectedCloneTender(ScQt selectedCloneTender) {
		this.selectedCloneTender = selectedCloneTender;
	}

	public List<ScCommitteeMember> getSelectedCloneCommitteMemebers() {
		return selectedCloneCommitteMemebers;
	}

	public void setSelectedCloneCommitteMemebers(
			List<ScCommitteeMember> selectedCloneCommitteMemebers) {
		this.selectedCloneCommitteMemebers = selectedCloneCommitteMemebers;
	}

	public IPagingDataModel<ScQt> getCloneSearchQTDataModel() {
		return cloneSearchQTDataModel;
	}

	public void setCloneSearchQTDataModel(
			IPagingDataModel<ScQt> cloneSearchQTDataModel) {
		this.cloneSearchQTDataModel = cloneSearchQTDataModel;
	}

	public IPagingDataModel<PmUser> getUserSearchQCDataModel() {
		return userSearchQCDataModel;
	}

	public void setUserSearchQCDataModel(
			IPagingDataModel<PmUser> userSearchQCDataModel) {
		this.userSearchQCDataModel = userSearchQCDataModel;
	}

	public UserCommitteeSearchBean getUserQCSearchBean() {
		return userQCSearchBean;
	}

	public void setUserQCSearchBean(UserCommitteeSearchBean userQCSearchBean) {
		this.userQCSearchBean = userQCSearchBean;
	}

	public List<SelectItem> getRepresentativesTypeslist() {
		return representativesTypeslist;
	}

	public void setRepresentativesTypeslist(
			List<SelectItem> representativesTypeslist) {
		this.representativesTypeslist = representativesTypeslist;
	}

	public List<SelectItem> getAddRepresentativesTypeslist() {
		return addRepresentativesTypeslist;
	}

	public void setAddRepresentativesTypeslist(
			List<SelectItem> addRepresentativesTypeslist) {
		this.addRepresentativesTypeslist = addRepresentativesTypeslist;
	}

}