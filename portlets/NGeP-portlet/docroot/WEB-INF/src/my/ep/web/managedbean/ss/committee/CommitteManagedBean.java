/**
 * 
 */
package my.ep.web.managedbean.ss.committee;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ss.quationtender.ScCommittee;
import my.ep.persistence.entity.ss.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCommittee;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
/**
 * @author VidyasagarReddy
 *
 */
@ManagedBean(name="committeManagedBean")
@SessionScoped
public class CommitteManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log= EpLogger.getLogger(CommitteManagedBean.class);
	
	private List<ScQtCommittee>	qtcommittees= new ArrayList();
	
	private ScQtCommittee qtCommittee= new ScQtCommittee();
	
	private String selectedCommitte;
	
	private List<SelectItem> committeType;
	
		
	private String profileSelction;
	
	private ScCommitteeMember committeMember = new ScCommitteeMember();
	
	private List<ScCommitteeMember> committemembers;
	
	private List<SelectItem> minstryList;
	
	private List<SelectItem> divisionList;
	
	private List<SelectItem> deptList;
	 
	private ScCommittee scCommittee;
	
	private List<SelectItem> selections;
	
	private List<SelectItem> userTypes;
	
	private List<SelectItem> roles;
	
	private String userSelection;
	
	private String icType;
	
	private List<SelectItem> idTypes;
	
	private List<SelectItem> approvers;
	
	private String approverId;
	
	private List<SelectItem> govUsers;
	
	private List<SelectItem> externalUsers;
	
	private String tenderNum;
	private List<ScQt> scQt;
	
	//Search properties
	
	private String committeNo=null;
	
	private String qtNo=null;	
		
	private Date createdDt;
	
	private String status;
	
	public CommitteManagedBean(){
		log.info("inside ");		
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


	public List<SelectItem> getMinstryList() {
		return minstryList;
	}


	public void setMinstryList(List<SelectItem> minstryList) {
		this.minstryList = minstryList;
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


	public List<SelectItem> getDivisionList() {
		return divisionList;
	}


	public void setDivisionList(List<SelectItem> divisionList) {
		this.divisionList = divisionList;
	}


	public List<SelectItem> getDeptList() {
		return deptList;
	}


	public void setDeptList(List<SelectItem> deptList) {
		this.deptList = deptList;
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

	public List<SelectItem> getApprovers() {
		return approvers;
	}


	public void setApprovers(List<SelectItem> approvers) {
		this.approvers = approvers;
	}


	public String getApproverId() {
		return approverId;
	}


	public void setApproverId(String approverId) {
		this.approverId = approverId;
	}


	public List<SelectItem> getGovUsers() {
		return govUsers;
	}


	public void setGovUsers(List<SelectItem> govUsers) {
		this.govUsers = govUsers;
	}


	public List<SelectItem> getExternalUsers() {
		return externalUsers;
	}


	public void setExternalUsers(List<SelectItem> externalUsers) {
		this.externalUsers = externalUsers;
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



}
