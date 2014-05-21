package my.ep.web.managedbean.sc.loiloa;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmCurrency;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScPerformanceBond;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "lOADetailManagedBean")
@SessionScoped
public class LOADetailManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private ScQt qt;
	
	private List<SelectItem> comitteeUsers;
	public String approvalDate;

	private ScLoa scLoa;
	private SmSupplier smSupplier;
	private SmMofAccount smMofAccount;
	private PmUser pmUser;
	private ScQtProposalRanking scQtProposalRanking;
	private List<SmSupplierBranch> branchsList;

	private ScQtEvaluationResult scQtEvaluationResult;
	
	public String address;
	private String status;


	private ScPerformanceBond scBondDtl;
	private List<ScPerformanceBond> scBondDtlList;
	private List<ScPerformanceBond> selectScBondDtlList;
	
	private List<ScRequestItem> scQtItemList;
	private PmCurrency pmCurrency;
	
	private List<PmOrgProfile> userMinistryList;
	private List<PmOrgProfile> userDepartmentList;
	private List<PmOrgProfile> userDivisionList;
	private String branchName;
	private List<SelectItem> selectItemList;

	private List<ScQtDeduction> scQtDeductionList;
	private ScQtDeduction selectedScQtDeduction;
	
	private List<ScQtDeductionClause> scQtDeductionClauses;
	private List<ScQtDeductionClause> selectedDeductionClauses;
	private double percentageBond;
	private int bondAmount;
	private String bondPercentage;
	private List<PmUser> pmUserList;
	private List<PmUserOrg> pmUserOrgList;
	private List<PmUser>  approverName; // Drop down Listing for Approver Names
	private List<PmUser> contractSignerList;
	
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
	 * @return the scBondDtl
	 */
	public ScPerformanceBond getScBondDtl() {
		return scBondDtl;
	}

	/**
	 * @param scBondDtl the scBondDtl to set
	 */
	public void setScBondDtl(ScPerformanceBond scBondDtl) {
		this.scBondDtl = scBondDtl;
	}

	/**
	 * @return the approverName
	 */
	public List<PmUser> getApproverName() {
		return approverName;
	}

	/**
	 * @param approverName the approverName to set
	 */
	public void setApproverName(List<PmUser> approverName) {
		this.approverName = approverName;
	}

	/**
	 * @return the contractSignerList
	 */
	public List<PmUser> getContractSignerList() {
		return contractSignerList;
	}

	/**
	 * @param contractSignerList the contractSignerList to set
	 */
	public void setContractSignerList(List<PmUser> contractSignerList) {
		this.contractSignerList = contractSignerList;
	}

	/**
	 * @return the pmUserOrgList
	 */
	public List<PmUserOrg> getPmUserOrgList() {
		return pmUserOrgList;
	}

	/**
	 * @param pmUserOrgList the pmUserOrgList to set
	 */
	public void setPmUserOrgList(List<PmUserOrg> pmUserOrgList) {
		this.pmUserOrgList = pmUserOrgList;
	}

	/**
	 * @return the pmUserList
	 */
	public List<PmUser> getPmUserList() {
		return pmUserList;
	}

	/**
	 * @param pmUserList the pmUserList to set
	 */
	public void setPmUserList(List<PmUser> pmUserList) {
		this.pmUserList = pmUserList;
	}

	/**
	 * @return the bondPercentage
	 */
	public String getBondPercentage() {
		return bondPercentage;
	}

	/**
	 * @param bondPercentage the bondPercentage to set
	 */
	public void setBondPercentage(String bondPercentage) {
		this.bondPercentage = bondPercentage;
	}

	/**
	 * @return the percentageBond
	 */
	public double getPercentageBond() {
		return percentageBond;
	}

	/**
	 * @param percentageBond the percentageBond to set
	 */
	public void setPercentageBond(double percentageBond) {
		this.percentageBond = percentageBond;
	}

	/**
	 * @return the bondAmount
	 */
	public int getBondAmount() {
		return bondAmount;
	}

	/**
	 * @param bondAmount the bondAmount to set
	 */
	public void setBondAmount(int bondAmount) {
		this.bondAmount = bondAmount;
	}

	/**
	 * @return the selectedScQtDeduction
	 */
	public ScQtDeduction getSelectedScQtDeduction() {
		return selectedScQtDeduction;
	}

	/**
	 * @param selectedScQtDeduction the selectedScQtDeduction to set
	 */
	public void setSelectedScQtDeduction(ScQtDeduction selectedScQtDeduction) {
		this.selectedScQtDeduction = selectedScQtDeduction;
	}

	/**
	 * @return the selectedDeductionClauses
	 */
	public List<ScQtDeductionClause> getSelectedDeductionClauses() {
		return selectedDeductionClauses;
	}

	/**
	 * @param selectedDeductionClauses the selectedDeductionClauses to set
	 */
	public void setSelectedDeductionClauses(
			List<ScQtDeductionClause> selectedDeductionClauses) {
		this.selectedDeductionClauses = selectedDeductionClauses;
	}

	/**
	 * @return the scQtDeductionClauses
	 */
	public List<ScQtDeductionClause> getScQtDeductionClauses() {
		return scQtDeductionClauses;
	}

	/**
	 * @param scQtDeductionClauses the scQtDeductionClauses to set
	 */
	public void setScQtDeductionClauses(
			List<ScQtDeductionClause> scQtDeductionClauses) {
		this.scQtDeductionClauses = scQtDeductionClauses;
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
	 * @return the scQtDeductionList
	 */
	public List<ScQtDeduction> getScQtDeductionList() {
		return scQtDeductionList;
	}

	/**
	 * @param scQtDeductionList the scQtDeductionList to set
	 */
	public void setScQtDeductionList(List<ScQtDeduction> scQtDeductionList) {
		this.scQtDeductionList = scQtDeductionList;
	}

	/**
	 * @return the selectScBondDtlList
	 */
	public List<ScPerformanceBond> getSelectScBondDtlList() {
		return selectScBondDtlList;
	}

	/**
	 * @param selectScBondDtlList the selectScBondDtlList to set
	 */
	public void setSelectScBondDtlList(List<ScPerformanceBond> selectScBondDtlList) {
		this.selectScBondDtlList = selectScBondDtlList;
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
	 * @return the pmCurrency
	 */
	public PmCurrency getPmCurrency() {
		return pmCurrency;
	}

	/**
	 * @param pmCurrency the pmCurrency to set
	 */
	public void setPmCurrency(PmCurrency pmCurrency) {
		this.pmCurrency = pmCurrency;
	}

	/**
	 * @return the scQtItemList
	 */
	public List<ScRequestItem> getScQtItemList() {
		return scQtItemList;
	}

	/**
	 * @param scQtItemList the scQtItemList to set
	 */
	public void setScQtItemList(List<ScRequestItem> scQtItemList) {
		this.scQtItemList = scQtItemList;
	}


	/**
	 * @return the scBondDtlList
	 */
	public List<ScPerformanceBond> getScBondDtlList() {
		return scBondDtlList;
	}

	/**
	 * @param scBondDtlList
	 *            the scBondDtlList to set
	 */
	public void setScBondDtlList(List<ScPerformanceBond> scBondDtlList) {
		this.scBondDtlList = scBondDtlList;
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
	 * @return the branchsList
	 */
	public List<SmSupplierBranch> getBranchsList() {
		return branchsList;
	}

	/**
	 * @param branchsList
	 *            the branchsList to set
	 */
	public void setBranchsList(List<SmSupplierBranch> branchsList) {
		this.branchsList = branchsList;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
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
	 * @return the scLoa
	 */
	public ScLoa getScLoa() {
		return scLoa;
	}

	/**
	 * @param scLoa
	 *            the scLoa to set
	 */
	public void setScLoa(ScLoa scLoa) {
		this.scLoa = scLoa;
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
