package my.ep.web.managedbean.sc.loa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCurrency;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaApproval;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoaSupportingDoc;
import my.ep.persistence.entity.sc.loaloi.ScLoaWitness;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.type.pm.PTJLocalityCode;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

// TODO: Auto-generated Javadoc
/**
 * The Class directLOAManagedBean.
 * 
 * @author Sri
 * @since 15/06/2015
 */
@ManagedBean(name = "directLOAManagedBean")
@SessionScoped
public class DirectLOAManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The selected tab index. */
	private int selectedTabIndex;

	/** The source id. */
	private Long sourceId;

	/** The selected bank id. */
	private Long selectedBankId;

	/** The agree with terms conditions. */
	private boolean agreeWithTermsConditions;

	/** The terms conditions. */
	private String termsConditions;

	/** The selected supplier branch name. */
	private String selectedSupplierBranchName;

	/** The attachment doc type. */
	private String attachmentDocType;

	/** The sc loa. */
	private ScLoa scLoa;

	/** The sc qt. */
	private ScQt scQt;

	/** The supplier hq personnel. */
	private SmPersonnel supplierHQPersonnel;

	/** The loa supplier witness. */
	private ScLoaWitness loaSupplierWitness;

	/** The selected sc qt deduction. */
	private ScQtDeduction selectedScQtDeduction;

	/** The selected sc loa report. */
	private ScLoaReport selectedScLoaReport;

	/** The contract signer user. */
	private PmUser contractSignerUser;

	/** The approver user. */
	private PmUser approverUser;

	/** The sc loa approval. */
	private ScLoaApproval scLoaApproval;

	/** The loa stream content. */
	private StreamedContent loaStreamContent;

	/** The selected sc loa reports. */
	private List<ScLoaReport> selectedScLoaReports;

	/** The selected deduction clauses. */
	private List<ScQtDeductionClause> selectedDeductionClauses;

	// Selection list - LOA Details.
	/** The agency list. */
	private List<PmParameterDesc> agencyList;

	/** The agency profile list. */
	private List<PmOrgProfile> agencyProfileList;

	/** The agency profile dd list. */
	private List<Object[]> agencyProfileDDList;

	/** The contract admin list. */
	private List<PmUser> contractAdminList;

	/** The contract signer list. */
	private List<PmUser> contractSignerList;

	/** The branchs list. */
	private List<SmSupplierBranch> branchsList;

	/** The bank list. */
	private List<SmSupplierBank> bankList;

	/** The approver list. */
	private List<PmUser> approverList;

	// TODO - To be removed
	/** The comittee users. */
	private List<SelectItem> comitteeUsers;

	/** The approval date. */
	public String approvalDate;

	/** The address. */
	public String address;

	/** The status. */
	private String status;

	/** The pm currency. */
	private PmCurrency pmCurrency;

	/** The select item list. */
	private List<SelectItem> selectItemList;

	/** The percentage bond. */
	private double percentageBond;

	/** The bond amount. */
	private int bondAmount;

	/** The bond percentage. */
	private String bondPercentage;

	/** The total frequency. */
	private int totalFrequency;

	/** The year list. */
	private List<SelectItem> yearList;

	/** The years display list. */
	private List<SelectItem> yearsDisplayList;

	/** The total progressive payment. */
	private int totalProgressivePayment;

	/** The bond amount word. */
	private String bondAmountWord;

	/** The action. */
	private String action;

	/** The edit flag. */
	private boolean editFlag;

	/** The verify btn flag. */
	private boolean verifyBtnFlag = true;

	/** The back to previous page. */
	private String backToPreviousPage;

	/** The ptj dropdown list. */
	private List<PmOrgProfile> ptjDropdownList; // direct nego

	/** The map supp scloiloa list */
	private Map<Long, List<ScLoiLoa>> suppLoiLoaMap;

	/** The item type id. */
	private long itemTypeId = 1;// Item type Id

	/** The prodsc qt doc. */
	private ScQtDoc prodscQtDoc;

	/** The servicesc qt doc. */
	private ScQtDoc servicescQtDoc;

	
	/** The clauses title. */
	private String clausesTitle;

	/** The template spec bean. */
	private TemplateSpecBean prodTemplateSpecBean;

	/** The template spec bean. */
	private TemplateSpecBean specTemplateSpecBean;

	/** The spki backing bean. */
	@ManagedProperty(value = "#{spkiBackingBean}")
	private SpkiBackingBean spkiBackingBean;

	/** The zone trees. */
	private List<TreeNode> locationTreeNodes = new ArrayList<TreeNode>();

	/** The root location tree. */
	private TreeNode rootLocationTree;

	/** The Local level code. */
	private String LocalLevelCode;

	/** The all state code. */
	private PTJLocalityCode allStateCode;

	private boolean required;
	private boolean zoneLocation;
	private boolean autoUpdate;
	private String zoneName;
	private String tempZoneName;
	private int index;
	
	  /** The ministry condition field. */
    private String ministryConditionField;

	/** The total suppliers spec loc. */
	private List<TplSpecLoc> totalSuppliersSpecLoc;

	/** The root zoanl tree. */
	private TreeNode rootZoanlTree;
	/** The existing supplier location list. */
	private List<ScQtLocCriteria> existingLocationList;

	/** The selected nodes. */
	private TreeNode[] selectedNodes;

	/** The zone general trees. */
	private List<TreeNode> zoneTrees = new ArrayList<TreeNode>();

	/** The beans. */
	private List<ZonesBean> zoneBeans = new ArrayList<ZonesBean>();
	/** The selected general tree nodes. */
	private TreeNode[] selectedGeneralTreeNodes;

	private List<ScQtSchedule> toBeRemoveSchedules;
	private List<ScQtProgressivePayment> toBeRemoveProgPayments;

	private BigDecimal overallContractAmt;

	private boolean renderRemainder;

	private List<ScLoaSupportingDoc> selectedScLoaSuppDocs;

	private ScLoaSupportingDoc selectedScLoaSuppDoc;

	private ScQtZoneCriteria toBedeletedSupplierZone;

	private ScLoaReport branchInfoReport;
    
	private boolean displayAlert;
	
    /** The temp loa no. */
    private String tempLoaNo;
    
    /** The temp file path. */
    private String tempFilePath;
	
	/**
	 * Gets the spki backing bean.
	 * 
	 * @return the spki backing bean
	 */
	public SpkiBackingBean getSpkiBackingBean() {
		return spkiBackingBean;
	}

	/**
	 * Sets the spki backing bean.
	 * 
	 * @param spkiBackingBean
	 *            the new spki backing bean
	 */
	public void setSpkiBackingBean(SpkiBackingBean spkiBackingBean) {
		this.spkiBackingBean = spkiBackingBean;
	}

	/**
	 * Instantiates a new LOA detail managed bean.
	 */
	public DirectLOAManagedBean() {
	}

	/**
	 * Gets the selected tab index.
	 * 
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	/**
	 * Sets the selected tab index.
	 * 
	 * @param selectedTabIndex
	 *            the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	/**
	 * Gets the source id.
	 * 
	 * @return the sourceId
	 */
	public Long getSourceId() {
		return sourceId;
	}

	/**
	 * Sets the source id.
	 * 
	 * @param sourceId
	 *            the sourceId to set
	 */
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * Gets the selected bank id.
	 * 
	 * @return the selectedBankId
	 */
	public Long getSelectedBankId() {
		return selectedBankId;
	}

	/**
	 * Sets the selected bank id.
	 * 
	 * @param selectedBankId
	 *            the selectedBankId to set
	 */
	public void setSelectedBankId(Long selectedBankId) {
		this.selectedBankId = selectedBankId;
	}

	/**
	 * Checks if is agree with terms conditions.
	 * 
	 * @return the agreeWithTermsConditions
	 */
	public boolean isAgreeWithTermsConditions() {
		return agreeWithTermsConditions;
	}

	/**
	 * Sets the agree with terms conditions.
	 * 
	 * @param agreeWithTermsConditions
	 *            the agreeWithTermsConditions to set
	 */
	public void setAgreeWithTermsConditions(boolean agreeWithTermsConditions) {
		this.agreeWithTermsConditions = agreeWithTermsConditions;
	}

	/**
	 * Gets the terms conditions.
	 * 
	 * @return the termsConditions
	 */
	public String getTermsConditions() {
		return termsConditions;
	}

	/**
	 * Sets the terms conditions.
	 * 
	 * @param termsConditions
	 *            the termsConditions to set
	 */
	public void setTermsConditions(String termsConditions) {
		this.termsConditions = termsConditions;
	}

	/**
	 * Gets the selected supplier branch name.
	 * 
	 * @return the selectedSupplierBranchName
	 */
	public String getSelectedSupplierBranchName() {
		return selectedSupplierBranchName;
	}

	/**
	 * Sets the selected supplier branch name.
	 * 
	 * @param selectedSupplierBranchName
	 *            the selectedSupplierBranchName to set
	 */
	public void setSelectedSupplierBranchName(String selectedSupplierBranchName) {
		this.selectedSupplierBranchName = selectedSupplierBranchName;
	}

	/**
	 * Gets the attachment doc type.
	 * 
	 * @return the attachmentDocType
	 */
	public String getAttachmentDocType() {
		return attachmentDocType;
	}

	/**
	 * Sets the attachment doc type.
	 * 
	 * @param attachmentDocType
	 *            the attachmentDocType to set
	 */
	public void setAttachmentDocType(String attachmentDocType) {
		this.attachmentDocType = attachmentDocType;
	}

	/**
	 * Gets the sc loa.
	 * 
	 * @return the scLoa
	 */
	public ScLoa getScLoa() {
		return scLoa;
	}

	/**
	 * Sets the sc loa.
	 * 
	 * @param scLoa
	 *            the scLoa to set
	 */
	public void setScLoa(ScLoa scLoa) {
		this.scLoa = scLoa;
	}

	/**
	 * Gets the supplier hq personnel.
	 * 
	 * @return the supplierHQPersonnel
	 */
	public SmPersonnel getSupplierHQPersonnel() {
		return supplierHQPersonnel;
	}

	/**
	 * Sets the supplier hq personnel.
	 * 
	 * @param supplierHQPersonnel
	 *            the supplierHQPersonnel to set
	 */
	public void setSupplierHQPersonnel(SmPersonnel supplierHQPersonnel) {
		this.supplierHQPersonnel = supplierHQPersonnel;
	}

	/**
	 * Gets the loa supplier witness.
	 * 
	 * @return the loaSupplierWitness
	 */
	public ScLoaWitness getLoaSupplierWitness() {
		return loaSupplierWitness;
	}

	/**
	 * Sets the loa supplier witness.
	 * 
	 * @param loaSupplierWitness
	 *            the loaSupplierWitness to set
	 */
	public void setLoaSupplierWitness(ScLoaWitness loaSupplierWitness) {
		this.loaSupplierWitness = loaSupplierWitness;
	}

	/**
	 * Gets the selected sc qt deduction.
	 * 
	 * @return the selectedScQtDeduction
	 */
	public ScQtDeduction getSelectedScQtDeduction() {
		return selectedScQtDeduction;
	}

	/**
	 * Sets the selected sc qt deduction.
	 * 
	 * @param selectedScQtDeduction
	 *            the selectedScQtDeduction to set
	 */
	public void setSelectedScQtDeduction(ScQtDeduction selectedScQtDeduction) {
		this.selectedScQtDeduction = selectedScQtDeduction;
	}

	/**
	 * Gets the selected sc loa report.
	 * 
	 * @return the selectedScLoaReport
	 */
	public ScLoaReport getSelectedScLoaReport() {
		return selectedScLoaReport;
	}

	/**
	 * Sets the selected sc loa report.
	 * 
	 * @param selectedScLoaReport
	 *            the selectedScLoaReport to set
	 */
	public void setSelectedScLoaReport(ScLoaReport selectedScLoaReport) {
		this.selectedScLoaReport = selectedScLoaReport;
	}

	/**
	 * Gets the contract signer user.
	 * 
	 * @return the contractSignerUser
	 */
	public PmUser getContractSignerUser() {
		return contractSignerUser;
	}

	/**
	 * Sets the contract signer user.
	 * 
	 * @param contractSignerUser
	 *            the contractSignerUser to set
	 */
	public void setContractSignerUser(PmUser contractSignerUser) {
		this.contractSignerUser = contractSignerUser;
	}

	/**
	 * Gets the approver user.
	 * 
	 * @return the approverUser
	 */
	public PmUser getApproverUser() {
		return approverUser;
	}

	/**
	 * Sets the approver user.
	 * 
	 * @param approverUser
	 *            the approverUser to set
	 */
	public void setApproverUser(PmUser approverUser) {
		this.approverUser = approverUser;
	}

	/**
	 * Gets the sc loa approval.
	 * 
	 * @return the scLoaApproval
	 */
	public ScLoaApproval getScLoaApproval() {
		return scLoaApproval;
	}

	/**
	 * Sets the sc loa approval.
	 * 
	 * @param scLoaApproval
	 *            the scLoaApproval to set
	 */
	public void setScLoaApproval(ScLoaApproval scLoaApproval) {
		this.scLoaApproval = scLoaApproval;
	}

	/**
	 * Gets the loa stream content.
	 * 
	 * @return the loaStreamContent
	 */
	public StreamedContent getLoaStreamContent() {
		return loaStreamContent;
	}

	/**
	 * Sets the loa stream content.
	 * 
	 * @param loaStreamContent
	 *            the loaStreamContent to set
	 */
	public void setLoaStreamContent(StreamedContent loaStreamContent) {
		this.loaStreamContent = loaStreamContent;
	}

	/**
	 * Gets the selected sc loa reports.
	 * 
	 * @return the selectedScLoaReports
	 */
	public List<ScLoaReport> getSelectedScLoaReports() {
		return selectedScLoaReports;
	}

	/**
	 * Sets the selected sc loa reports.
	 * 
	 * @param selectedScLoaReports
	 *            the selectedScLoaReports to set
	 */
	public void setSelectedScLoaReports(List<ScLoaReport> selectedScLoaReports) {
		this.selectedScLoaReports = selectedScLoaReports;
	}

	/**
	 * Gets the selected deduction clauses.
	 * 
	 * @return the selectedDeductionClauses
	 */
	public List<ScQtDeductionClause> getSelectedDeductionClauses() {
		return selectedDeductionClauses;
	}

	/**
	 * Sets the selected deduction clauses.
	 * 
	 * @param selectedDeductionClauses
	 *            the selectedDeductionClauses to set
	 */
	public void setSelectedDeductionClauses(
			List<ScQtDeductionClause> selectedDeductionClauses) {
		this.selectedDeductionClauses = selectedDeductionClauses;
	}

	/**
	 * Gets the agency list.
	 * 
	 * @return the agencyList
	 */
	public List<PmParameterDesc> getAgencyList() {
		return agencyList;
	}

	/**
	 * Sets the agency list.
	 * 
	 * @param agencyList
	 *            the agencyList to set
	 */
	public void setAgencyList(List<PmParameterDesc> agencyList) {
		this.agencyList = agencyList;
	}

	/**
	 * Gets the agency profile list.
	 * 
	 * @return the agencyProfileList
	 */
	public List<PmOrgProfile> getAgencyProfileList() {
		return agencyProfileList;
	}

	/**
	 * Sets the agency profile list.
	 * 
	 * @param agencyProfileList
	 *            the agencyProfileList to set
	 */
	public void setAgencyProfileList(List<PmOrgProfile> agencyProfileList) {
		this.agencyProfileList = agencyProfileList;
	}

	/**
	 * Gets the contract admin list.
	 * 
	 * @return the contractAdminList
	 */
	public List<PmUser> getContractAdminList() {
		return contractAdminList;
	}

	/**
	 * Sets the contract admin list.
	 * 
	 * @param contractAdminList
	 *            the contractAdminList to set
	 */
	public void setContractAdminList(List<PmUser> contractAdminList) {
		this.contractAdminList = contractAdminList;
	}

	/**
	 * Gets the contract signer list.
	 * 
	 * @return the contractSignerList
	 */
	public List<PmUser> getContractSignerList() {
		return contractSignerList;
	}

	/**
	 * Sets the contract signer list.
	 * 
	 * @param contractSignerList
	 *            the contractSignerList to set
	 */
	public void setContractSignerList(List<PmUser> contractSignerList) {
		this.contractSignerList = contractSignerList;
	}

	/**
	 * Gets the branchs list.
	 * 
	 * @return the branchsList
	 */
	public List<SmSupplierBranch> getBranchsList() {
		return branchsList;
	}

	/**
	 * Sets the branchs list.
	 * 
	 * @param branchsList
	 *            the branchsList to set
	 */
	public void setBranchsList(List<SmSupplierBranch> branchsList) {
		this.branchsList = branchsList;
	}

	/**
	 * Gets the bank list.
	 * 
	 * @return the bankList
	 */
	public List<SmSupplierBank> getBankList() {
		return bankList;
	}

	/**
	 * Sets the bank list.
	 * 
	 * @param bankList
	 *            the bankList to set
	 */
	public void setBankList(List<SmSupplierBank> bankList) {
		this.bankList = bankList;
	}

	/**
	 * Gets the approver list.
	 * 
	 * @return the approverList
	 */
	public List<PmUser> getApproverList() {
		return approverList;
	}

	/**
	 * Sets the approver list.
	 * 
	 * @param approverList
	 *            the approverList to set
	 */
	public void setApproverList(List<PmUser> approverList) {
		this.approverList = approverList;
	}

	/**
	 * Gets the comittee users.
	 * 
	 * @return the comitteeUsers
	 */
	public List<SelectItem> getComitteeUsers() {
		return comitteeUsers;
	}

	/**
	 * Sets the comittee users.
	 * 
	 * @param comitteeUsers
	 *            the comitteeUsers to set
	 */
	public void setComitteeUsers(List<SelectItem> comitteeUsers) {
		this.comitteeUsers = comitteeUsers;
	}

	/**
	 * Gets the approval date.
	 * 
	 * @return the approvalDate
	 */
	public String getApprovalDate() {
		return approvalDate;
	}

	/**
	 * Sets the approval date.
	 * 
	 * @param approvalDate
	 *            the approvalDate to set
	 */
	public void setApprovalDate(String approvalDate) {
		this.approvalDate = approvalDate;
	}

	/**
	 * Gets the address.
	 * 
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the address.
	 * 
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
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
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Gets the pm currency.
	 * 
	 * @return the pmCurrency
	 */
	public PmCurrency getPmCurrency() {
		return pmCurrency;
	}

	/**
	 * Sets the pm currency.
	 * 
	 * @param pmCurrency
	 *            the pmCurrency to set
	 */
	public void setPmCurrency(PmCurrency pmCurrency) {
		this.pmCurrency = pmCurrency;
	}

	/**
	 * Gets the select item list.
	 * 
	 * @return the selectItemList
	 */
	public List<SelectItem> getSelectItemList() {
		return selectItemList;
	}

	/**
	 * Sets the select item list.
	 * 
	 * @param selectItemList
	 *            the selectItemList to set
	 */
	public void setSelectItemList(List<SelectItem> selectItemList) {
		this.selectItemList = selectItemList;
	}

	/**
	 * Gets the percentage bond.
	 * 
	 * @return the percentageBond
	 */
	public double getPercentageBond() {
		return percentageBond;
	}

	/**
	 * Sets the percentage bond.
	 * 
	 * @param percentageBond
	 *            the percentageBond to set
	 */
	public void setPercentageBond(double percentageBond) {
		this.percentageBond = percentageBond;
	}

	/**
	 * Gets the bond amount.
	 * 
	 * @return the bondAmount
	 */
	public int getBondAmount() {
		return bondAmount;
	}

	/**
	 * Sets the bond amount.
	 * 
	 * @param bondAmount
	 *            the bondAmount to set
	 */
	public void setBondAmount(int bondAmount) {
		this.bondAmount = bondAmount;
	}

	/**
	 * Gets the bond percentage.
	 * 
	 * @return the bondPercentage
	 */
	public String getBondPercentage() {
		return bondPercentage;
	}

	/**
	 * Sets the bond percentage.
	 * 
	 * @param bondPercentage
	 *            the bondPercentage to set
	 */
	public void setBondPercentage(String bondPercentage) {
		this.bondPercentage = bondPercentage;
	}

	/**
	 * Gets the total frequency.
	 * 
	 * @return the total frequency
	 */
	public int getTotalFrequency() {
		return totalFrequency;
	}

	/**
	 * Sets the total frequency.
	 * 
	 * @param totalFrequency
	 *            the new total frequency
	 */
	public void setTotalFrequency(int totalFrequency) {
		this.totalFrequency = totalFrequency;
	}

	/**
	 * Gets the year list.
	 * 
	 * @return the year list
	 */
	public List<SelectItem> getYearList() {
		return yearList;
	}

	/**
	 * Sets the year list.
	 * 
	 * @param yearList
	 *            the new year list
	 */
	public void setYearList(List<SelectItem> yearList) {
		this.yearList = yearList;
	}

	/**
	 * Gets the years display list.
	 * 
	 * @return the years display list
	 */
	public List<SelectItem> getYearsDisplayList() {
		return yearsDisplayList;
	}

	/**
	 * Sets the years display list.
	 * 
	 * @param yearsDisplayList
	 *            the new years display list
	 */
	public void setYearsDisplayList(List<SelectItem> yearsDisplayList) {
		this.yearsDisplayList = yearsDisplayList;
	}

	/**
	 * Gets the sc qt.
	 * 
	 * @return the sc qt
	 */
	public ScQt getScQt() {
		return scQt;
	}

	/**
	 * Sets the sc qt.
	 * 
	 * @param scQt
	 *            the new sc qt
	 */
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}

	/**
	 * Gets the total progressive payment.
	 * 
	 * @return the total progressive payment
	 */
	public int getTotalProgressivePayment() {
		return totalProgressivePayment;
	}

	/**
	 * Sets the total progressive payment.
	 * 
	 * @param totalProgressivePayment
	 *            the new total progressive payment
	 */
	public void setTotalProgressivePayment(int totalProgressivePayment) {
		this.totalProgressivePayment = totalProgressivePayment;
	}

	/**
	 * Gets the bond amount word.
	 * 
	 * @return the bond amount word
	 */
	public String getBondAmountWord() {
		return bondAmountWord;
	}

	/**
	 * Sets the bond amount word.
	 * 
	 * @param bondAmountWord
	 *            the new bond amount word
	 */
	public void setBondAmountWord(String bondAmountWord) {
		this.bondAmountWord = bondAmountWord;
	}

	/**
	 * Gets the agency profile dd list.
	 * 
	 * @return the agency profile dd list
	 */
	public List<Object[]> getAgencyProfileDDList() {
		return agencyProfileDDList;
	}

	/**
	 * Sets the agency profile dd list.
	 * 
	 * @param agencyProfileDDList
	 *            the new agency profile dd list
	 */
	public void setAgencyProfileDDList(List<Object[]> agencyProfileDDList) {
		this.agencyProfileDDList = agencyProfileDDList;
	}

	/**
	 * Gets the action.
	 * 
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 * 
	 * @param action
	 *            the new action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Checks if is edits the flag.
	 * 
	 * @return true, if is edits the flag
	 */
	public boolean isEditFlag() {
		return editFlag;
	}

	/**
	 * Sets the edits the flag.
	 * 
	 * @param editFlag
	 *            the new edits the flag
	 */
	public void setEditFlag(boolean editFlag) {
		this.editFlag = editFlag;
	}

	/**
	 * Checks if is verify btn flag.
	 * 
	 * @return true, if is verify btn flag
	 */
	public boolean isVerifyBtnFlag() {
		return verifyBtnFlag;
	}

	/**
	 * Sets the verify btn flag.
	 * 
	 * @param verifyBtnFlag
	 *            the new verify btn flag
	 */
	public void setVerifyBtnFlag(boolean verifyBtnFlag) {
		this.verifyBtnFlag = verifyBtnFlag;
	}

	/**
	 * Gets the back to previous page.
	 * 
	 * @return the back to previous page
	 */
	public String getBackToPreviousPage() {
		return backToPreviousPage;
	}

	/**
	 * Sets the back to previous page.
	 * 
	 * @param backToPreviousPage
	 *            the new back to previous page
	 */
	public void setBackToPreviousPage(String backToPreviousPage) {
		this.backToPreviousPage = backToPreviousPage;
	}

	/**
	 * Gets the ptj dropdown list.
	 * 
	 * @return the ptj dropdown list
	 */
	public List<PmOrgProfile> getPtjDropdownList() {
		return ptjDropdownList;
	}

	/**
	 * Sets the ptj dropdown list.
	 * 
	 * @param ptjDropdownList
	 *            the new ptj dropdown list
	 */
	public void setPtjDropdownList(List<PmOrgProfile> ptjDropdownList) {
		this.ptjDropdownList = ptjDropdownList;
	}

	/**
	 * Gets the item type id.
	 * 
	 * @return the item type id
	 */
	public long getItemTypeId() {
		return itemTypeId;
	}

	/**
	 * Sets the item type id.
	 * 
	 * @param itemTypeId
	 *            the new item type id
	 */
	public void setItemTypeId(long itemTypeId) {
		this.itemTypeId = itemTypeId;
	}

	/**
	 * Gets the supp loi loa map.
	 * 
	 * @return the supp loi loa map
	 */
	public Map<Long, List<ScLoiLoa>> getSuppLoiLoaMap() {
		return suppLoiLoaMap;
	}

	/**
	 * Sets the supp loi loa map.
	 * 
	 * @param suppLoiLoaMap
	 *            the supp loi loa map
	 */
	public void setSuppLoiLoaMap(Map<Long, List<ScLoiLoa>> suppLoiLoaMap) {
		this.suppLoiLoaMap = suppLoiLoaMap;
	}

	
	/**
	 * Gets the clauses title.
	 * 
	 * @return the clauses title
	 */
	public String getClausesTitle() {
		return clausesTitle;
	}

	/**
	 * Sets the clauses title.
	 * 
	 * @param clausesTitle
	 *            the new clauses title
	 */
	public void setClausesTitle(String clausesTitle) {
		this.clausesTitle = clausesTitle;
	}

	public List<TreeNode> getLocationTreeNodes() {
		return locationTreeNodes;
	}

	public void setLocationTreeNodes(List<TreeNode> locationTreeNodes) {
		this.locationTreeNodes = locationTreeNodes;
	}

	public TreeNode getRootLocationTree() {
		return rootLocationTree;
	}

	public void setRootLocationTree(TreeNode rootLocationTree) {
		this.rootLocationTree = rootLocationTree;
	}

	public String getLocalLevelCode() {
		return LocalLevelCode;
	}

	public void setLocalLevelCode(String localLevelCode) {
		LocalLevelCode = localLevelCode;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isZoneLocation() {
		return zoneLocation;
	}

	public void setZoneLocation(boolean zoneLocation) {
		this.zoneLocation = zoneLocation;
	}

	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public TreeNode getRootZoanlTree() {
		return rootZoanlTree;
	}

	public void setRootZoanlTree(TreeNode rootZoanlTree) {
		this.rootZoanlTree = rootZoanlTree;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getTempZoneName() {
		return tempZoneName;
	}

	public void setTempZoneName(String tempZoneName) {
		this.tempZoneName = tempZoneName;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public List<TplSpecLoc> getTotalSuppliersSpecLoc() {
		return totalSuppliersSpecLoc;
	}

	public void setTotalSuppliersSpecLoc(List<TplSpecLoc> totalSuppliersSpecLoc) {
		this.totalSuppliersSpecLoc = totalSuppliersSpecLoc;
	}

	public List<ScQtLocCriteria> getExistingLocationList() {
		return existingLocationList;
	}

	public void setExistingLocationList(
			List<ScQtLocCriteria> existingLocationList) {
		this.existingLocationList = existingLocationList;
	}

	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public List<TreeNode> getZoneTrees() {
		return zoneTrees;
	}

	public void setZoneTrees(List<TreeNode> zoneTrees) {
		this.zoneTrees = zoneTrees;
	}

	public List<ZonesBean> getZoneBeans() {
		return zoneBeans;
	}

	public void setZoneBeans(List<ZonesBean> zoneBeans) {
		this.zoneBeans = zoneBeans;
	}

	public TreeNode[] getSelectedGeneralTreeNodes() {
		return selectedGeneralTreeNodes;
	}

	public void setSelectedGeneralTreeNodes(TreeNode[] selectedGeneralTreeNodes) {
		this.selectedGeneralTreeNodes = selectedGeneralTreeNodes;
	}

	public PTJLocalityCode getAllStateCode() {
		return allStateCode;
	}

	public void setAllStateCode(PTJLocalityCode allStateCode) {
		this.allStateCode = allStateCode;
	}

	public List<ScQtSchedule> getToBeRemoveSchedules() {
		return toBeRemoveSchedules;
	}

	public void setToBeRemoveSchedules(List<ScQtSchedule> toBeRemoveSchedules) {
		this.toBeRemoveSchedules = toBeRemoveSchedules;
	}

	public List<ScQtProgressivePayment> getToBeRemoveProgPayments() {
		return toBeRemoveProgPayments;
	}

	public void setToBeRemoveProgPayments(
			List<ScQtProgressivePayment> toBeRemoveProgPayments) {
		this.toBeRemoveProgPayments = toBeRemoveProgPayments;
	}

	public BigDecimal getOverallContractAmt() {
		return overallContractAmt;
	}

	public void setOverallContractAmt(BigDecimal overallContractAmt) {
		this.overallContractAmt = overallContractAmt;
	}

	public boolean isRenderRemainder() {
		return renderRemainder;
	}

	public void setRenderRemainder(boolean renderRemainder) {
		this.renderRemainder = renderRemainder;
	}

	public List<ScLoaSupportingDoc> getSelectedScLoaSuppDocs() {
		return selectedScLoaSuppDocs;
	}

	public void setSelectedScLoaSuppDocs(
			List<ScLoaSupportingDoc> selectedScLoaSuppDocs) {
		this.selectedScLoaSuppDocs = selectedScLoaSuppDocs;
	}

	public ScLoaSupportingDoc getSelectedScLoaSuppDoc() {
		return selectedScLoaSuppDoc;
	}

	public void setSelectedScLoaSuppDoc(ScLoaSupportingDoc selectedScLoaSuppDoc) {
		this.selectedScLoaSuppDoc = selectedScLoaSuppDoc;
	}

	public TemplateSpecBean getProdTemplateSpecBean() {
		return prodTemplateSpecBean;
	}

	public void setProdTemplateSpecBean(TemplateSpecBean prodTemplateSpecBean) {
		this.prodTemplateSpecBean = prodTemplateSpecBean;
	}

	public TemplateSpecBean getSpecTemplateSpecBean() {
		return specTemplateSpecBean;
	}

	public void setSpecTemplateSpecBean(TemplateSpecBean specTemplateSpecBean) {
		this.specTemplateSpecBean = specTemplateSpecBean;
	}

	public ScQtDoc getProdscQtDoc() {
		return prodscQtDoc;
	}

	public void setProdscQtDoc(ScQtDoc prodscQtDoc) {
		this.prodscQtDoc = prodscQtDoc;
	}

	public ScQtDoc getServicescQtDoc() {
		return servicescQtDoc;
	}

	public void setServicescQtDoc(ScQtDoc servicescQtDoc) {
		this.servicescQtDoc = servicescQtDoc;
	}

	public ScQtZoneCriteria getToBedeletedSupplierZone() {
		return toBedeletedSupplierZone;
	}

	public void setToBedeletedSupplierZone(
			ScQtZoneCriteria toBedeletedSupplierZone) {
		this.toBedeletedSupplierZone = toBedeletedSupplierZone;
	}

	/**
	 * @return the branchInfoReport
	 */
	public ScLoaReport getBranchInfoReport() {
		return branchInfoReport;
	}

	/**
	 * @param branchInfoReport
	 *            the branchInfoReport to set
	 */
	public void setBranchInfoReport(ScLoaReport branchInfoReport) {
		this.branchInfoReport = branchInfoReport;
	}

	/**
	 * @return the displayAlert
	 */
	public boolean isDisplayAlert() {
		return displayAlert;
	}

	/**
	 * @param displayAlert the displayAlert to set
	 */
	public void setDisplayAlert(boolean displayAlert) {
		this.displayAlert = displayAlert;
	}

	/**
	 * @return the tempLoaNo
	 */
	public String getTempLoaNo() {
		return tempLoaNo;
	}

	/**
	 * @param tempLoaNo the tempLoaNo to set
	 */
	public void setTempLoaNo(String tempLoaNo) {
		this.tempLoaNo = tempLoaNo;
	}

	/**
	 * @return the tempFilePath
	 */
	public String getTempFilePath() {
		return tempFilePath;
	}

	/**
	 * @param tempFilePath the tempFilePath to set
	 */
	public void setTempFilePath(String tempFilePath) {
		this.tempFilePath = tempFilePath;
	}

	/**
	 * @return the ministryConditionField
	 */
	public String getMinistryConditionField() {
		return ministryConditionField;
	}

	/**
	 * @param ministryConditionField the ministryConditionField to set
	 */
	public void setMinistryConditionField(String ministryConditionField) {
		this.ministryConditionField = ministryConditionField;
	}
}
