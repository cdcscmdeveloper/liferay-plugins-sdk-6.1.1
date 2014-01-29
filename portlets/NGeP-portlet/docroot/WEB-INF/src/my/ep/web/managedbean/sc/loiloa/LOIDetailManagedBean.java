package my.ep.web.managedbean.sc.loiloa;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.quationtender.ScLoi;
import my.ep.persistence.entity.sc.quationtender.ScNegoLocation;
import my.ep.persistence.entity.sc.quationtender.ScNegoTerm;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtItem;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.sc.quationtender.ScQtSpec;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.sc.quationtender.ScTermDesc;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "lOIDetailManagedBean")
@SessionScoped
public class LOIDetailManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private ScQt qt;
	private ScQtSpec scQtSpec;
	private List<SelectItem> comitteeUsers;
	public String approvalDate;

	private ScLoi scLoi;
	private SmSupplier smSupplier;
	private SmMofAccount smMofAccount;

	private ScQtCriteria scQtCriteria;
	private ScNegoLocation scNegoLocation;
	private List<ScNegoLocation> negoLocationList;
	private List<ScNegoLocation> selectedNegoLocationList;
	private ScNegoTerm scNegoTerm;
	private List<ScNegoTerm> negoTermsList;
	private List<ScNegoTerm> selectedNegoTermsList;
	private ScTermDesc scTermDesc;
	private List<ScTermDesc> scTermDescList;
	private List<ScTermDesc> selectedCheckDetails;
	private PmUser pmUser;
	private ScQtProposalRanking scQtProposalRanking;
	private List<SmSupplierBranch> branchsList;
	
	private ScQtEvaluationResult scQtEvaluationResult;
	
	private String status;
	public String reqApprove;
	public String Address;

	private List<ScQtZoneCriteria> zonelist;
	
	private List<ScQtItem> scQtItemList;
	
	private List<PmOrgProfile> userMinistryList;
	private List<PmOrgProfile> userDepartmentList;
	private List<PmOrgProfile> userDivisionList;

	private SmSupplierBranch smSupplierBranch;
	private String branchName;
	private List<SelectItem> selectItemList;
	private List<PmUser>  approverNames; // Drop down Listing for Approver Names
	
	/**
	 * @return the scQtEvaluationResult
	 */
	public ScQtEvaluationResult getScQtEvaluationResult() {
		return scQtEvaluationResult;
	}

	/**
	 * @param scQtEvaluationResult the scQtEvaluationResult to set
	 */
	public void setScQtEvaluationResult(ScQtEvaluationResult scQtEvaluationResult) {
		this.scQtEvaluationResult = scQtEvaluationResult;
	}

	/**
	 * @return the approverNames
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
	 * @return the selectItemList
	 */
	public List<SelectItem> getSelectItemList() {
		return selectItemList;
	}

	/**
	 * @param selectItemList the selectItemList to set
	 */
	public void setSelectItemList(List<SelectItem> selectItemList) {
		this.selectItemList = selectItemList;
	}

	/**
	 * @return the selectedNegoTermsList
	 */
	public List<ScNegoTerm> getSelectedNegoTermsList() {
		return selectedNegoTermsList;
	}

	/**
	 * @param selectedNegoTermsList the selectedNegoTermsList to set
	 */
	public void setSelectedNegoTermsList(List<ScNegoTerm> selectedNegoTermsList) {
		this.selectedNegoTermsList = selectedNegoTermsList;
	}

	/**
	 * @return the selectedNegoLocationList
	 */
	public List<ScNegoLocation> getSelectedNegoLocationList() {
		return selectedNegoLocationList;
	}

	/**
	 * @param selectedNegoLocationList the selectedNegoLocationList to set
	 */
	public void setSelectedNegoLocationList(
			List<ScNegoLocation> selectedNegoLocationList) {
		this.selectedNegoLocationList = selectedNegoLocationList;
	}

	/**
	 * @return the branchName
	 */
	public String getBranchName() {
		return branchName;
	}

	/**
	 * @param branchName the branchName to set
	 */
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	/**
	 * @return the smSupplierBranch
	 */
	public SmSupplierBranch getSmSupplierBranch() {
		return smSupplierBranch;
	}

	/**
	 * @param smSupplierBranch the smSupplierBranch to set
	 */
	public void setSmSupplierBranch(SmSupplierBranch smSupplierBranch) {
		this.smSupplierBranch = smSupplierBranch;
	}

	/**
	 * @return the userMinistryList
	 */
	public List<PmOrgProfile> getUserMinistryList() {
		return userMinistryList;
	}

	/**
	 * @param userMinistryList the userMinistryList to set
	 */
	public void setUserMinistryList(List<PmOrgProfile> userMinistryList) {
		this.userMinistryList = userMinistryList;
	}

	/**
	 * @return the userDepartmentList
	 */
	public List<PmOrgProfile> getUserDepartmentList() {
		return userDepartmentList;
	}

	/**
	 * @param userDepartmentList the userDepartmentList to set
	 */
	public void setUserDepartmentList(List<PmOrgProfile> userDepartmentList) {
		this.userDepartmentList = userDepartmentList;
	}

	/**
	 * @return the userDivisionList
	 */
	public List<PmOrgProfile> getUserDivisionList() {
		return userDivisionList;
	}

	/**
	 * @param userDivisionList the userDivisionList to set
	 */
	public void setUserDivisionList(List<PmOrgProfile> userDivisionList) {
		this.userDivisionList = userDivisionList;
	}

	/**
	 * @return the scQtItemList
	 */
	public List<ScQtItem> getScQtItemList() {
		return scQtItemList;
	}

	/**
	 * @param scQtItemList the scQtItemList to set
	 */
	public void setScQtItemList(List<ScQtItem> scQtItemList) {
		this.scQtItemList = scQtItemList;
	}

	/**
	 * @return the zonelist
	 */
	public List<ScQtZoneCriteria> getZonelist() {
		return zonelist;
	}

	/**
	 * @param zonelist the zonelist to set
	 */
	public void setZonelist(List<ScQtZoneCriteria> zonelist) {
		this.zonelist = zonelist;
	}

	/**
	 * @return the scTermDesc
	 */
	public ScTermDesc getScTermDesc() {
		return scTermDesc;
	}

	/**
	 * @param scTermDesc the scTermDesc to set
	 */
	public void setScTermDesc(ScTermDesc scTermDesc) {
		this.scTermDesc = scTermDesc;
	}

	/**
	 * @return the selectedCheckDetails
	 */
	public List<ScTermDesc> getSelectedCheckDetails() {
		return selectedCheckDetails;
	}

	/**
	 * @param selectedCheckDetails the selectedCheckDetails to set
	 */
	public void setSelectedCheckDetails(List<ScTermDesc> selectedCheckDetails) {
		this.selectedCheckDetails = selectedCheckDetails;
	}

	/**
	 * @return the branchsList
	 */
	public List<SmSupplierBranch> getBranchsList() {
		return branchsList;
	}

	/**
	 * @param branchsList the branchsList to set
	 */
	public void setBranchsList(List<SmSupplierBranch> branchsList) {
		this.branchsList = branchsList;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return Address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		Address = address;
	}

	/**
	 * @return the pmUser
	 */
	public PmUser getPmUser() {
		return pmUser;
	}

	/**
	 * @param pmUser
	 *            the pmUser to set
	 */
	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}

	/**
	 * @return the scQtProposalRanking
	 */
	public ScQtProposalRanking getScQtProposalRanking() {
		return scQtProposalRanking;
	}

	/**
	 * @param scQtProposalRanking
	 *            the scQtProposalRanking to set
	 */
	public void setScQtProposalRanking(ScQtProposalRanking scQtProposalRanking) {
		this.scQtProposalRanking = scQtProposalRanking;
	}

	/**
	 * @return the reqApprove
	 */
	public String getReqApprove() {
		return reqApprove;
	}

	/**
	 * @param reqApprove
	 *            the reqApprove to set
	 */
	public void setReqApprove(String reqApprove) {
		this.reqApprove = reqApprove;
	}

	/**
	 * @return the scTermDescList
	 */
	public List<ScTermDesc> getScTermDescList() {
		return scTermDescList;
	}

	/**
	 * @param scTermDescList
	 *            the scTermDescList to set
	 */
	public void setScTermDescList(List<ScTermDesc> scTermDescList) {
		this.scTermDescList = scTermDescList;
	}

	/**
	 * @return the scNegoTerm
	 */
	public ScNegoTerm getScNegoTerm() {
		return scNegoTerm;
	}

	/**
	 * @param scNegoTerm
	 *            the scNegoTerm to set
	 */
	public void setScNegoTerm(ScNegoTerm scNegoTerm) {
		this.scNegoTerm = scNegoTerm;
	}

	/**
	 * @return the negoTermsList
	 */
	public List<ScNegoTerm> getNegoTermsList() {
		return negoTermsList;
	}

	/**
	 * @param negoTermsList
	 *            the negoTermsList to set
	 */
	public void setNegoTermsList(List<ScNegoTerm> negoTermsList) {
		this.negoTermsList = negoTermsList;
	}

	/**
	 * @return the negoLocationList
	 */
	public List<ScNegoLocation> getNegoLocationList() {
		return negoLocationList;
	}

	/**
	 * @param negoLocationList
	 *            the negoLocationList to set
	 */
	public void setNegoLocationList(List<ScNegoLocation> negoLocationList) {
		this.negoLocationList = negoLocationList;
	}

	/**
	 * @return the scNegoLocation
	 */
	public ScNegoLocation getScNegoLocation() {
		return scNegoLocation;
	}

	/**
	 * @param scNegoLocation
	 *            the scNegoLocation to set
	 */
	public void setScNegoLocation(ScNegoLocation scNegoLocation) {
		this.scNegoLocation = scNegoLocation;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the scQtCriteria
	 */
	public ScQtCriteria getScQtCriteria() {
		return scQtCriteria;
	}

	/**
	 * @param scQtCriteria
	 *            the scQtCriteria to set
	 */
	public void setScQtCriteria(ScQtCriteria scQtCriteria) {
		this.scQtCriteria = scQtCriteria;
	}

	/**
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * @param smSupplier
	 *            the smSupplier to set
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	/**
	 * @return the smMofAccount
	 */
	public SmMofAccount getSmMofAccount() {
		return smMofAccount;
	}

	/**
	 * @param smMofAccount
	 *            the smMofAccount to set
	 */
	public void setSmMofAccount(SmMofAccount smMofAccount) {
		this.smMofAccount = smMofAccount;
	}

	/**
	 * @return the scLoi
	 */
	public ScLoi getScLoi() {
		return scLoi;
	}

	/**
	 * @param scLoi
	 *            the scLoi to set
	 */
	public void setScLoi(ScLoi scLoi) {
		this.scLoi = scLoi;
	}

	/**
	 * @return the comitteeUsers
	 */
	public List<SelectItem> getComitteeUsers() {
		return comitteeUsers;
	}

	/**
	 * @param comitteeUsers
	 *            the comitteeUsers to set
	 */
	public void setComitteeUsers(List<SelectItem> comitteeUsers) {
		this.comitteeUsers = comitteeUsers;
	}

	/**
	 * @return the approvalDate
	 */
	public String getApprovalDate() {
		return approvalDate;
	}

	/**
	 * @param approvalDate
	 *            the approvalDate to set
	 */
	public void setApprovalDate(String approvalDate) {
		this.approvalDate = approvalDate;
	}

	/**
	 * @return the scQtSpec
	 */
	public ScQtSpec getScQtSpec() {
		return scQtSpec;
	}

	/**
	 * @param scQtSpec
	 *            the scQtSpec to set
	 */
	public void setScQtSpec(ScQtSpec scQtSpec) {
		this.scQtSpec = scQtSpec;
	}

	/**
	 * @return the qt
	 */
	public ScQt getQt() {
		return qt;
	}

	/**
	 * @param qt
	 *            the qt to set
	 */
	public void setQt(ScQt qt) {
		this.qt = qt;
	}

}
