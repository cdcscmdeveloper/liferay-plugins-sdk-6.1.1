package my.ep.web.managedbean.sc.loa;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.StreamedContent;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCurrency;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaApproval;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoaWitness;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.loaloi.ScPerformanceBond;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.web.backingbean.sc.loa.SuppLoiLoaBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "lOADetailManagedBean")
@SessionScoped
public class LOADetailManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	
	private int selectedTabIndex;
	private Long sourceId;
	private Long selectedBankId;
	private boolean agreeWithTermsConditions;
	private String termsConditions;
	private String selectedSupplierBranchName;
	private String selectedContractAgency;
	private String attachmentDocType;
	private ScLoa scLoa;
	private ScQt scQt;
	private SmPersonnel supplierHQPersonnel;
	private ScLoaWitness loaAgencyWitness;
	private ScLoaWitness loaSupplierWitness;
	private ScQtDeduction selectedScQtDeduction;
	private ScLoaReport selectedScLoaReport;
	private PmUser contractSignerUser;
	private PmUser approverUser;
	private ScLoaApproval scLoaApproval;
	private StreamedContent loaStreamContent;
	private List<ScLoaReport> selectedScLoaReports;
	private List<ScQtDeductionClause> selectedDeductionClauses;
	private ScLoiLoa selectedLoiLoa;
	private SuppLoiLoaBean selectedSuppLoiLoaBean;
	
	// Selection list - LOA Details.
	private List<PmParameterDesc> agencyList;
	private List<PmOrgProfile> agencyProfileList;
	private List<PmUser> contractAdminList;
	private List<PmUser> contractSignerList;
	private List<SmSupplierBranch> branchsList;
	private List<SmSupplierBank> bankList;
	private List<PmUser> approverList;
	
	// TODO - To be removed
	private List<SelectItem> comitteeUsers;
	public String approvalDate;
	private ScQtProposalRanking scQtProposalRanking;
	public String address;
	private String status;
	private ScPerformanceBond scBondDtl;
	private List<ScPerformanceBond> scBondDtlList;
	private List<ScPerformanceBond> selectScBondDtlList;
	private PmCurrency pmCurrency;
	private List<SelectItem> selectItemList;	
	private double percentageBond;
	private int bondAmount;
	private String bondPercentage;
	
	private int totalFrequency;
	private List<ScQtSchedule> scheduleList;
	private List<SelectItem> yearList;
	private List<SelectItem> yearsDisplayList;
	
	private List<SuppLoiLoaBean> suppLoiLoaList;
	
	private int totalProgressivePayment;
	private List<ScQtProgressivePayment> progressivePaymentList;
	private String bondAmountWord;
	
	
	public LOADetailManagedBean() {
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
	 * @return the sourceId
	 */
	public Long getSourceId() {
		return sourceId;
	}

	/**
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * @return the selectedBankId
	 */
	public Long getSelectedBankId() {
		return selectedBankId;
	}

	/**
	 * @param selectedBankId the selectedBankId to set
	 */
	public void setSelectedBankId(Long selectedBankId) {
		this.selectedBankId = selectedBankId;
	}

	/**
	 * @return the agreeWithTermsConditions
	 */
	public boolean isAgreeWithTermsConditions() {
		return agreeWithTermsConditions;
	}

	/**
	 * @param agreeWithTermsConditions the agreeWithTermsConditions to set
	 */
	public void setAgreeWithTermsConditions(boolean agreeWithTermsConditions) {
		this.agreeWithTermsConditions = agreeWithTermsConditions;
	}

	/**
	 * @return the termsConditions
	 */
	public String getTermsConditions() {
		return termsConditions;
	}

	/**
	 * @param termsConditions the termsConditions to set
	 */
	public void setTermsConditions(String termsConditions) {
		this.termsConditions = termsConditions;
	}

	/**
	 * @return the selectedSupplierBranchName
	 */
	public String getSelectedSupplierBranchName() {
		return selectedSupplierBranchName;
	}

	/**
	 * @param selectedSupplierBranchName the selectedSupplierBranchName to set
	 */
	public void setSelectedSupplierBranchName(String selectedSupplierBranchName) {
		this.selectedSupplierBranchName = selectedSupplierBranchName;
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
	 * @return the attachmentDocType
	 */
	public String getAttachmentDocType() {
		return attachmentDocType;
	}

	/**
	 * @param attachmentDocType the attachmentDocType to set
	 */
	public void setAttachmentDocType(String attachmentDocType) {
		this.attachmentDocType = attachmentDocType;
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
	 * @return the supplierHQPersonnel
	 */
	public SmPersonnel getSupplierHQPersonnel() {
		return supplierHQPersonnel;
	}

	/**
	 * @param supplierHQPersonnel the supplierHQPersonnel to set
	 */
	public void setSupplierHQPersonnel(SmPersonnel supplierHQPersonnel) {
		this.supplierHQPersonnel = supplierHQPersonnel;
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
	 * @return the selectedScLoaReport
	 */
	public ScLoaReport getSelectedScLoaReport() {
		return selectedScLoaReport;
	}

	/**
	 * @param selectedScLoaReport the selectedScLoaReport to set
	 */
	public void setSelectedScLoaReport(ScLoaReport selectedScLoaReport) {
		this.selectedScLoaReport = selectedScLoaReport;
	}

	/**
	 * @return the contractSignerUser
	 */
	public PmUser getContractSignerUser() {
		return contractSignerUser;
	}

	/**
	 * @param contractSignerUser the contractSignerUser to set
	 */
	public void setContractSignerUser(PmUser contractSignerUser) {
		this.contractSignerUser = contractSignerUser;
	}

	/**
	 * @return the approverUser
	 */
	public PmUser getApproverUser() {
		return approverUser;
	}

	/**
	 * @param approverUser the approverUser to set
	 */
	public void setApproverUser(PmUser approverUser) {
		this.approverUser = approverUser;
	}

	/**
	 * @return the scLoaApproval
	 */
	public ScLoaApproval getScLoaApproval() {
		return scLoaApproval;
	}

	/**
	 * @param scLoaApproval the scLoaApproval to set
	 */
	public void setScLoaApproval(ScLoaApproval scLoaApproval) {
		this.scLoaApproval = scLoaApproval;
	}

	/**
	 * @return the loaStreamContent
	 */
	public StreamedContent getLoaStreamContent() {
		return loaStreamContent;
	}

	/**
	 * @param loaStreamContent the loaStreamContent to set
	 */
	public void setLoaStreamContent(StreamedContent loaStreamContent) {
		this.loaStreamContent = loaStreamContent;
	}

	/**
	 * @return the selectedScLoaReports
	 */
	public List<ScLoaReport> getSelectedScLoaReports() {
		return selectedScLoaReports;
	}

	/**
	 * @param selectedScLoaReports the selectedScLoaReports to set
	 */
	public void setSelectedScLoaReports(List<ScLoaReport> selectedScLoaReports) {
		this.selectedScLoaReports = selectedScLoaReports;
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
	 * @return the bankList
	 */
	public List<SmSupplierBank> getBankList() {
		return bankList;
	}

	/**
	 * @param bankList the bankList to set
	 */
	public void setBankList(List<SmSupplierBank> bankList) {
		this.bankList = bankList;
	}

	/**
	 * @return the approverList
	 */
	public List<PmUser> getApproverList() {
		return approverList;
	}

	/**
	 * @param approverList the approverList to set
	 */
	public void setApproverList(List<PmUser> approverList) {
		this.approverList = approverList;
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

	public int getTotalFrequency() {
		return totalFrequency;
	}

	public void setTotalFrequency(int totalFrequency) {
		this.totalFrequency = totalFrequency;
	}

	public List<ScQtSchedule> getScheduleList() {
		return scheduleList;
	}

	public void setScheduleList(List<ScQtSchedule> scheduleList) {
		this.scheduleList = scheduleList;
	}

	public List<SelectItem> getYearList() {
		return yearList;
	}

	public void setYearList(List<SelectItem> yearList) {
		this.yearList = yearList;
	}

	public List<SelectItem> getYearsDisplayList() {
		return yearsDisplayList;
	}

	public void setYearsDisplayList(List<SelectItem> yearsDisplayList) {
		this.yearsDisplayList = yearsDisplayList;
	}

	public List<SuppLoiLoaBean> getSuppLoiLoaList() {
		return suppLoiLoaList;
	}

	public void setSuppLoiLoaList(List<SuppLoiLoaBean> suppLoiLoaList) {
		this.suppLoiLoaList = suppLoiLoaList;
	}

	public SuppLoiLoaBean getSelectedSuppLoiLoaBean() {
		return selectedSuppLoiLoaBean;
	}

	public void setSelectedSuppLoiLoaBean(SuppLoiLoaBean selectedSuppLoiLoaBean) {
		this.selectedSuppLoiLoaBean = selectedSuppLoiLoaBean;
	}

	public ScLoiLoa getSelectedLoiLoa() {
		return selectedLoiLoa;
	}

	public void setSelectedLoiLoa(ScLoiLoa selectedLoiLoa) {
		this.selectedLoiLoa = selectedLoiLoa;
	}

	public ScQt getScQt() {
		return scQt;
	}

	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}

	public List<ScQtProgressivePayment> getProgressivePaymentList() {
		return progressivePaymentList;
	}

	public void setProgressivePaymentList(List<ScQtProgressivePayment> progressivePaymentList) {
		this.progressivePaymentList = progressivePaymentList;
	}

	public int getTotalProgressivePayment() {
		return totalProgressivePayment;
	}

	public void setTotalProgressivePayment(int totalProgressivePayment) {
		this.totalProgressivePayment = totalProgressivePayment;
	}

	public String getBondAmountWord() {
		return bondAmountWord;
	}

	public void setBondAmountWord(String bondAmountWord) {
		this.bondAmountWord = bondAmountWord;
	}

}
