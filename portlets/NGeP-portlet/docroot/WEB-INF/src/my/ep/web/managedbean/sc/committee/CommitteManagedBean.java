/**
 * 
 */
package my.ep.web.managedbean.sc.committee;

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
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String outComes[];

	private String taskId;

	private static final Logger log = EpLogger
			.getLogger(CommitteManagedBean.class);

	private List<ScQtCommittee> qtcommittees;

	private ScQtCommittee qtCommittee;

	private String selectedCommitte;

	private List<SelectItem> committeType;

	private String profileSelction;

	private ScCommitteeMember committeMember;

	private List<ScCommitteeMember> committemembers;

	private List<PmOrgProfile> minstryList;

	private List<PmOrgProfile> divisionList;

	private List<PmOrgProfile> deptList;

	private ScCommittee scCommittee;

	private List<SelectItem> selections;

	private List<SelectItem> userTypes;

	private List<SelectItem> roles;

	private String userSelection;

	private String icType;

	private List<SelectItem> idTypes;

	private List<PmUser> approvers;

	private String approverId;

	private List<PmUser> govUsers;

	private String userId;

	private List<PmUser> externalUsers;
	private List<PmUser> selectedPmUser;

	private String tenderNum;
	private List<ScQt> scQt;

	private List<ScQtCommittee> cloneSearchCommittes;
	private ScQtCommittee selectedCloneCommittee;

	private List<ScCommitteeMember> cloneCommitteMemebers;
	private List<ScCommitteeMember> selectedCommitteMemebers;

	private List<SelectItem> approvalStatus;

	private boolean ecType;

	private boolean tecType;

	// Search properties

	private String committeNo = null;

	private String qtNo = null;

	private Date createdDt;

	private String status;

	private String title = null;
	private String procurementMode;
	private Date createdDtFrom;
	private Date createdDtTo;

	private boolean isEditable = true;

	private String evaluationType = "2";
	
	private List<ScCommitteeMember> specificationCommitteList;
	private List<ScCommitteeMember> selectSpecificationCommitteeList;
	
	private List<ScCommitteeMember> openingCommitteList;
	private List<ScCommitteeMember> selectOpeningCommitteeList;
	
	private List<ScCommitteeMember> evaluationCommitteList;
	private List<ScCommitteeMember> selectEvaluationCommitteeList;
	
	private List<ScCommitteeMember> techEvaluationCommitteList;
	private List<ScCommitteeMember> selectTechEvaluationCommitteeList;
	
	private List<ScCommitteeMember> finEvaluationCommitteList;
	private List<ScCommitteeMember> selectFinEvaluationCommitteeList;
	
	private TreeNode committeeNode;
	private TreeNode searchcommitteeNode;
	
	private List<ScCommitteeMember> scCommitteeMemberList;
	
	private String name = null;
	private String identificationNo = null;
	private String identificationType;
	
	
	public CommitteManagedBean() {
		log.info("inside ");
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
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
	 * @return the scCommitteeMemberList
	 */
	public List<ScCommitteeMember> getScCommitteeMemberList() {
		return scCommitteeMemberList;
	}

	/**
	 * @param scCommitteeMemberList the scCommitteeMemberList to set
	 */
	public void setScCommitteeMemberList(
			List<ScCommitteeMember> scCommitteeMemberList) {
		this.scCommitteeMemberList = scCommitteeMemberList;
	}

	/**
	 * @return the searchcommitteeNode
	 */
	public TreeNode getSearchcommitteeNode() {
		return searchcommitteeNode;
	}

	/**
	 * @param searchcommitteeNode the searchcommitteeNode to set
	 */
	public void setSearchcommitteeNode(TreeNode searchcommitteeNode) {
		this.searchcommitteeNode = searchcommitteeNode;
	}

	/**
	 * @return the committeeNode
	 */
	public TreeNode getCommitteeNode() {
		return committeeNode;
	}

	/**
	 * @param committeeNode the committeeNode to set
	 */
	public void setCommitteeNode(TreeNode committeeNode) {
		this.committeeNode = committeeNode;
	}

	/**
	 * @return the selectOpeningCommitteeList
	 */
	public List<ScCommitteeMember> getSelectOpeningCommitteeList() {
		return selectOpeningCommitteeList;
	}

	/**
	 * @param selectOpeningCommitteeList the selectOpeningCommitteeList to set
	 */
	public void setSelectOpeningCommitteeList(
			List<ScCommitteeMember> selectOpeningCommitteeList) {
		this.selectOpeningCommitteeList = selectOpeningCommitteeList;
	}

	/**
	 * @return the selectEvaluationCommitteeList
	 */
	public List<ScCommitteeMember> getSelectEvaluationCommitteeList() {
		return selectEvaluationCommitteeList;
	}

	/**
	 * @param selectEvaluationCommitteeList the selectEvaluationCommitteeList to set
	 */
	public void setSelectEvaluationCommitteeList(
			List<ScCommitteeMember> selectEvaluationCommitteeList) {
		this.selectEvaluationCommitteeList = selectEvaluationCommitteeList;
	}

	/**
	 * @return the selectTechEvaluationCommitteeList
	 */
	public List<ScCommitteeMember> getSelectTechEvaluationCommitteeList() {
		return selectTechEvaluationCommitteeList;
	}

	/**
	 * @param selectTechEvaluationCommitteeList the selectTechEvaluationCommitteeList to set
	 */
	public void setSelectTechEvaluationCommitteeList(
			List<ScCommitteeMember> selectTechEvaluationCommitteeList) {
		this.selectTechEvaluationCommitteeList = selectTechEvaluationCommitteeList;
	}

	/**
	 * @return the selectFinEvaluationCommitteeList
	 */
	public List<ScCommitteeMember> getSelectFinEvaluationCommitteeList() {
		return selectFinEvaluationCommitteeList;
	}

	/**
	 * @param selectFinEvaluationCommitteeList the selectFinEvaluationCommitteeList to set
	 */
	public void setSelectFinEvaluationCommitteeList(
			List<ScCommitteeMember> selectFinEvaluationCommitteeList) {
		this.selectFinEvaluationCommitteeList = selectFinEvaluationCommitteeList;
	}

	/**
	 * @return the selectSpecificationCommitteeList
	 */
	public List<ScCommitteeMember> getSelectSpecificationCommitteeList() {
		return selectSpecificationCommitteeList;
	}

	/**
	 * @param selectSpecificationCommitteeList the selectSpecificationCommitteeList to set
	 */
	public void setSelectSpecificationCommitteeList(
			List<ScCommitteeMember> selectSpecificationCommitteeList) {
		this.selectSpecificationCommitteeList = selectSpecificationCommitteeList;
	}

	/**
	 * @return the evaluationCommitteList
	 */
	public List<ScCommitteeMember> getEvaluationCommitteList() {
		return evaluationCommitteList;
	}

	/**
	 * @param evaluationCommitteList the evaluationCommitteList to set
	 */
	public void setEvaluationCommitteList(
			List<ScCommitteeMember> evaluationCommitteList) {
		this.evaluationCommitteList = evaluationCommitteList;
	}

	/**
	 * @return the techEvaluationCommitteList
	 */
	public List<ScCommitteeMember> getTechEvaluationCommitteList() {
		return techEvaluationCommitteList;
	}

	/**
	 * @param techEvaluationCommitteList the techEvaluationCommitteList to set
	 */
	public void setTechEvaluationCommitteList(
			List<ScCommitteeMember> techEvaluationCommitteList) {
		this.techEvaluationCommitteList = techEvaluationCommitteList;
	}

	/**
	 * @return the finEvaluationCommitteList
	 */
	public List<ScCommitteeMember> getFinEvaluationCommitteList() {
		return finEvaluationCommitteList;
	}

	/**
	 * @param finEvaluationCommitteList the finEvaluationCommitteList to set
	 */
	public void setFinEvaluationCommitteList(
			List<ScCommitteeMember> finEvaluationCommitteList) {
		this.finEvaluationCommitteList = finEvaluationCommitteList;
	}

	/**
	 * @return the openingCommitteList
	 */
	public List<ScCommitteeMember> getOpeningCommitteList() {
		return openingCommitteList;
	}

	/**
	 * @param openingCommitteList the openingCommitteList to set
	 */
	public void setOpeningCommitteList(List<ScCommitteeMember> openingCommitteList) {
		this.openingCommitteList = openingCommitteList;
	}

	/**
	 * @return the specificationCommitteList
	 */
	public List<ScCommitteeMember> getSpecificationCommitteList() {
		return specificationCommitteList;
	}

	/**
	 * @param specificationCommitteList the specificationCommitteList to set
	 */
	public void setSpecificationCommitteList(
			List<ScCommitteeMember> specificationCommitteList) {
		this.specificationCommitteList = specificationCommitteList;
	}

	/**
	 * @return the selectedCloneCommittee
	 */
	public ScQtCommittee getSelectedCloneCommittee() {
		return selectedCloneCommittee;
	}

	/**
	 * @param selectedCloneCommittee the selectedCloneCommittee to set
	 */
	public void setSelectedCloneCommittee(ScQtCommittee selectedCloneCommittee) {
		this.selectedCloneCommittee = selectedCloneCommittee;
	}

	/**
	 * @return the evaluationType
	 */
	public String getEvaluationType() {
		return evaluationType;
	}

	/**
	 * @param evaluationType the evaluationType to set
	 */
	public void setEvaluationType(String evaluationType) {
		this.evaluationType = evaluationType;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the procurementMode
	 */
	public String getProcurementMode() {
		return procurementMode;
	}

	/**
	 * @param procurementMode
	 *            the procurementMode to set
	 */
	public void setProcurementMode(String procurementMode) {
		this.procurementMode = procurementMode;
	}

	/**
	 * @return the createdDtFrom
	 */
	public Date getCreatedDtFrom() {
		return createdDtFrom;
	}

	/**
	 * @param createdDtFrom
	 *            the createdDtFrom to set
	 */
	public void setCreatedDtFrom(Date createdDtFrom) {
		this.createdDtFrom = createdDtFrom;
	}

	/**
	 * @return the createdDtTo
	 */
	public Date getCreatedDtTo() {
		return createdDtTo;
	}

	/**
	 * @param createdDtTo
	 *            the createdDtTo to set
	 */
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

	public String getProfileSelction() {
		return profileSelction;
	}

	public void setProfileSelction(String profileSelction) {
		this.profileSelction = profileSelction;
	}

	public ScCommitteeMember getCommitteMember() {
		return committeMember;
	}

	public void setCommitteMember(ScCommitteeMember committeMember) {
		this.committeMember = committeMember;
	}

	public List<ScCommitteeMember> getCommittemembers() {
		return committemembers;
	}

	public void setCommittemembers(List<ScCommitteeMember> committemembers) {
		this.committemembers = committemembers;
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

	public String getUserSelection() {
		return userSelection;
	}

	public void setUserSelection(String userSelection) {
		this.userSelection = userSelection;
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

	public boolean isEcType() {
		return ecType;
	}

	public void setEcType(boolean ecType) {
		this.ecType = ecType;
	}

	public boolean isTecType() {
		return tecType;
	}

	public void setTecType(boolean tecType) {
		this.tecType = tecType;
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

}
