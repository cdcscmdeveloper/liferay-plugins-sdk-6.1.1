package my.ep.web.managedbean.sc.loa;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCurrency;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaWitness;
import my.ep.persistence.entity.sc.loaloi.ScPerformanceBond;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
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
	
	private List<SelectItem> comitteeUsers;
	public String approvalDate;

	private ScQtProposalRanking scQtProposalRanking;
	private List<SmSupplierBranch> branchsList;
	
	public String address;
	private String status;


	private ScPerformanceBond scBondDtl;
	private List<ScPerformanceBond> scBondDtlList;
	private List<ScPerformanceBond> selectScBondDtlList;
	
	private PmCurrency pmCurrency;
	
	private String branchName;
	private List<SelectItem> selectItemList;

	private List<ScQtDeduction> scQtDeductionList;
	private ScQtDeduction selectedScQtDeduction;
	
	private List<ScQtDeductionClause> scQtDeductionClauses;
	private List<ScQtDeductionClause> selectedDeductionClauses;
	private double percentageBond;
	private int bondAmount;
	private String bondPercentage;
	private List<PmUser>  approverName; // Drop down Listing for Approver Names
	
	private int selectedTabIndex;
	private String selectedContractAgency;
	private ScQtFinalization scQtFinalization;
	private ScLoa scLoa;
	private ScLoaWitness loaAgencyWitness;
	private ScLoaWitness loaSupplierWitness;
	private List<ScQtRecommendDtl> scQtRecommendDtls;
	
	// Selection list - LOA Details.
	private List<PmParameterDesc> agencyList;
	private List<PmOrgProfile> agencyProfileList;
	private List<PmUser> contractAdminList;
	private List<PmUser> contractSignerList;
	
	public LOADetailManagedBean() {
	}

	/**
	 * @return the comitteeUsers
	 */
	public List<SelectItem> getComitteeUsers() {
		return comitteeUsers;
	}

	/**
	 * @param comitteeUsers the comitteeUsers to set
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
	 * @param approvalDate the approvalDate to set
	 */
	public void setApprovalDate(String approvalDate) {
		this.approvalDate = approvalDate;
	}

	/**
	 * @return the scQtProposalRanking
	 */
	public ScQtProposalRanking getScQtProposalRanking() {
		return scQtProposalRanking;
	}

	/**
	 * @param scQtProposalRanking the scQtProposalRanking to set
	 */
	public void setScQtProposalRanking(ScQtProposalRanking scQtProposalRanking) {
		this.scQtProposalRanking = scQtProposalRanking;
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
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
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
	 * @return the scBondDtlList
	 */
	public List<ScPerformanceBond> getScBondDtlList() {
		return scBondDtlList;
	}

	/**
	 * @param scBondDtlList the scBondDtlList to set
	 */
	public void setScBondDtlList(List<ScPerformanceBond> scBondDtlList) {
		this.scBondDtlList = scBondDtlList;
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
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	/**
	 * @param selectedTabIndex the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	/**
	 * @return the selectedContractAgency
	 */
	public String getSelectedContractAgency() {
		return selectedContractAgency;
	}

	/**
	 * @param selectedContractAgency the selectedContractAgency to set
	 */
	public void setSelectedContractAgency(String selectedContractAgency) {
		this.selectedContractAgency = selectedContractAgency;
	}

	/**
	 * @return the scQtFinalization
	 */
	public ScQtFinalization getScQtFinalization() {
		return scQtFinalization;
	}

	/**
	 * @param scQtFinalization the scQtFinalization to set
	 */
	public void setScQtFinalization(ScQtFinalization scQtFinalization) {
		this.scQtFinalization = scQtFinalization;
	}

	/**
	 * @return the scLoa
	 */
	public ScLoa getScLoa() {
		return scLoa;
	}

	/**
	 * @param scLoa the scLoa to set
	 */
	public void setScLoa(ScLoa scLoa) {
		this.scLoa = scLoa;
	}

	/**
	 * @return the loaAgencyWitness
	 */
	public ScLoaWitness getLoaAgencyWitness() {
		return loaAgencyWitness;
	}

	/**
	 * @param loaAgencyWitness the loaAgencyWitness to set
	 */
	public void setLoaAgencyWitness(ScLoaWitness loaAgencyWitness) {
		this.loaAgencyWitness = loaAgencyWitness;
	}

	/**
	 * @return the loaSupplierWitness
	 */
	public ScLoaWitness getLoaSupplierWitness() {
		return loaSupplierWitness;
	}

	/**
	 * @param loaSupplierWitness the loaSupplierWitness to set
	 */
	public void setLoaSupplierWitness(ScLoaWitness loaSupplierWitness) {
		this.loaSupplierWitness = loaSupplierWitness;
	}

	/**
	 * @return the scQtRecommendDtls
	 */
	public List<ScQtRecommendDtl> getScQtRecommendDtls() {
		return scQtRecommendDtls;
	}

	/**
	 * @param scQtRecommendDtls the scQtRecommendDtls to set
	 */
	public void setScQtRecommendDtls(List<ScQtRecommendDtl> scQtRecommendDtls) {
		this.scQtRecommendDtls = scQtRecommendDtls;
	}

	/**
	 * @return the agencyList
	 */
	public List<PmParameterDesc> getAgencyList() {
		return agencyList;
	}

	/**
	 * @param agencyList the agencyList to set
	 */
	public void setAgencyList(List<PmParameterDesc> agencyList) {
		this.agencyList = agencyList;
	}

	/**
	 * @return the agencyProfileList
	 */
	public List<PmOrgProfile> getAgencyProfileList() {
		return agencyProfileList;
	}

	/**
	 * @param agencyProfileList the agencyProfileList to set
	 */
	public void setAgencyProfileList(List<PmOrgProfile> agencyProfileList) {
		this.agencyProfileList = agencyProfileList;
	}

	/**
	 * @return the contractAdminList
	 */
	public List<PmUser> getContractAdminList() {
		return contractAdminList;
	}

	/**
	 * @param contractAdminList the contractAdminList to set
	 */
	public void setContractAdminList(List<PmUser> contractAdminList) {
		this.contractAdminList = contractAdminList;
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

}
