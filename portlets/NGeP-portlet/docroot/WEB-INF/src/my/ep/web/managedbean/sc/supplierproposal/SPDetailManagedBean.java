package my.ep.web.managedbean.sc.supplierproposal;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPAssetLiability;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPBankStmt;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPCashOnHand;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPCreditFacility;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPEmployee;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPExperience;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPExperienceNCheckList;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalScore;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleItem;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.web.backingbean.sc.supplierproposal.TechnicalStaffBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "supplierProposalDetailManagedBean")
@SessionScoped
public class SPDetailManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(SPDetailManagedBean.class);

	private ScQt qt;
	private ScQtProposal qtProposal;
	private ScQtProposalChecklist qtProposalChecklist;
	private List<ScQtProposalChecklist> tecCheckDetailsList;
	private List<ScQtProposalChecklist> financeCheckDetailsList;
	private List<ScQtProposalChecklist> selectedCheckDetails;

	private ScQtCheckList scQtCheckList;
	private long days;
	private int activeIndex;
	private int proposalIndex;

	// Tab Menu
	private int prepareActiveIndex;

	private ScQtProposalChecklist selectDetails;
	public boolean checkStatus = true;

	// payLoad object Added By Vidyasagar.
	private Object proposalData;
	private String supplierId;

	// private ScAttachment scAttachment = new ScAttachment();
	private List<ScAttachment> scAttachmentList;

	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();

	private List<ScQtProposalScore> scQtProposalScoreList;
	private ScQtProposalScore scQtProposalScore;

	private ScQtDoc scQtDoc;

	private List<TplSpecZoneDtl> zones;
	private TplSpecZoneDtl selectedZone;
	private int prepareProposal;
	private List<ScQtProposal> qtProposalsList;

	// Federal Government
	private List<ScLoa> scLoaList;
	private int contractSize;
	private int noOfYears;

	// Non-Federal Government
	private List<ScQtPRPExperience> scQtPRPExperiences;
	private List<ScQtPRPExperience> selectedPRPExperiences;
	private List<ScQtPRPExperienceNCheckList> scQtPRPExperienceNCheckLists;
	private int totalContracts;
	private int totalYears;
	private BigDecimal totalContractAmount;
	// Business rule 
	private Date nonFedMinDate;
	private Date nonFedEffMaxDate;
	private List<ScQtPRPExperience> deletedPRPExperiences;

	// Credit Facilities
	private List<ScQtPRPCreditFacility> scQtPRPCreditFacilities;
	private List<ScQtPRPCreditFacility> selectedPRPCreditFacilities;
	private BigDecimal totalAmountCredit;
	private BigDecimal totalAmountPaid;
	private BigDecimal totalPercentagePaid;
	private List<ScQtPRPCreditFacility> toBeDeletedCreditFacilities;

	// Cash On Hand
	private List<ScQtPRPCashOnHand> scQtPRPCashOnHand;
	private List<ScQtPRPCashOnHand> selectedPRPCashOnHand;
	private BigDecimal totalcashHand;
	private List<ScQtPRPCashOnHand> toBeDeletedCashOnHandList;

	// Bank Statement
	private List<ScQtPRPBankStmt> scQtPRPBankStmts;
	private List<ScQtPRPBankStmt> selectedPRPBankStmts;
	private BigDecimal totalAmount;
	private BigDecimal avgAmount;

	// Asset Liability
	private ScQtPRPAssetLiability scQtPRPAssetLiability;
	private List<ScQtPRPAssetLiability> scQtPRPAssetLiabilities;
	private List<ScQtPRPAssetLiability> selectedPRPAssetLiabilities;

	// No of Employees
	private ScQtPRPEmployee scQtPRPEmployee;
	
	// Technical Staff List
	private int totalNoOfYearsExperience;

	// Sample Address
	private ScQtSampleSubmission scQtSampleSubmission;
	private List<ScQtSampleSubmission> scQtSampleSubmissionList;
	private List<ScQtSampleItem> scQtSampleItemList;

	// Supplier Information
	private SmSupplier smSupplier;
	private SmAppl smAppl;
	private List<TechnicalStaffBean> smPersonnelList;
	
	private int technicalTableRow;
	private int financialTableRow;

	public SPDetailManagedBean() {
		log.info("inside ");
	}
	
	

	/**
	 * @return the toBeDeletedCashOnHandList
	 */
	public List<ScQtPRPCashOnHand> getToBeDeletedCashOnHandList() {
		return toBeDeletedCashOnHandList;
	}



	/**
	 * @param toBeDeletedCashOnHandList the toBeDeletedCashOnHandList to set
	 */
	public void setToBeDeletedCashOnHandList(
			List<ScQtPRPCashOnHand> toBeDeletedCashOnHandList) {
		this.toBeDeletedCashOnHandList = toBeDeletedCashOnHandList;
	}



	/**
	 * @return the toBeDeletedCreditFacilities
	 */
	public List<ScQtPRPCreditFacility> getToBeDeletedCreditFacilities() {
		return toBeDeletedCreditFacilities;
	}



	/**
	 * @param toBeDeletedCreditFacilities the toBeDeletedCreditFacilities to set
	 */
	public void setToBeDeletedCreditFacilities(
			List<ScQtPRPCreditFacility> toBeDeletedCreditFacilities) {
		this.toBeDeletedCreditFacilities = toBeDeletedCreditFacilities;
	}



	/**
	 * @return the deletedPRPExperiences
	 */
	public List<ScQtPRPExperience> getDeletedPRPExperiences() {
		return deletedPRPExperiences;
	}



	/**
	 * @param deletedPRPExperiences the deletedPRPExperiences to set
	 */
	public void setDeletedPRPExperiences(
			List<ScQtPRPExperience> deletedPRPExperiences) {
		this.deletedPRPExperiences = deletedPRPExperiences;
	}



	/**
	 * @return the nonFedEffMaxDate
	 */
	public Date getNonFedEffMaxDate() {
		return nonFedEffMaxDate;
	}



	/**
	 * @param nonFedEffMaxDate the nonFedEffMaxDate to set
	 */
	public void setNonFedEffMaxDate(Date nonFedEffMaxDate) {
		this.nonFedEffMaxDate = nonFedEffMaxDate;
	}



	/**
	 * @return the nonFedMinDate
	 */
	public Date getNonFedMinDate() {
		
		if(nonFedMinDate == null){
			nonFedMinDate = new Date(01/01/2008);
		}
		return nonFedMinDate;
	}

	/**
	 * @param nonFedMinDate
	 *            the nonFedMinDate to set
	 */
	public void setNonFedMinDate(Date nonFedMinDate) {
		this.nonFedMinDate = nonFedMinDate;
	}

	public boolean isCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(boolean checkStatus) {
		this.checkStatus = checkStatus;
	}

	public ScQtProposalChecklist getSelectDetails() {
		return selectDetails;
	}

	public void setSelectDetails(ScQtProposalChecklist selectDetails) {
		this.selectDetails = selectDetails;
	}

	public List<ScAttachment> getScAttachmentList() {
		return scAttachmentList;
	}

	public void setScAttachmentList(List<ScAttachment> scAttachmentList) {
		this.scAttachmentList = scAttachmentList;
	}

	public ScQtProposalChecklist getQtProposalChecklist() {
		return qtProposalChecklist;
	}

	public void setQtProposalChecklist(ScQtProposalChecklist qtProposalChecklist) {
		this.qtProposalChecklist = qtProposalChecklist;
	}

	public List<ScQtProposalChecklist> getSelectedCheckDetails() {
		return selectedCheckDetails;
	}

	public void setSelectedCheckDetails(
			List<ScQtProposalChecklist> selectedCheckDetails) {
		this.selectedCheckDetails = selectedCheckDetails;
	}

	public List<ScQtProposalChecklist> getFinanceCheckDetailsList() {
		return financeCheckDetailsList;
	}

	public void setFinanceCheckDetailsList(
			List<ScQtProposalChecklist> financeCheckDetailsList) {
		this.financeCheckDetailsList = financeCheckDetailsList;
	}

	public List<ScQtProposalChecklist> getTecCheckDetailsList() {
		return tecCheckDetailsList;
	}

	public void setTecCheckDetailsList(
			List<ScQtProposalChecklist> tecCheckDetailsList) {
		this.tecCheckDetailsList = tecCheckDetailsList;
	}

	public ScQtProposal getQtProposal() {
		return qtProposal;
	}

	public void setQtProposal(ScQtProposal qtProposal) {
		this.qtProposal = qtProposal;
	}

	public ScQt getQt() {
		return qt;
	}

	public void setQt(ScQt qt) {
		this.qt = qt;
	}

	public Object getProposalData() {
		return proposalData;
	}

	public void setProposalData(Object proposalData) {
		this.proposalData = proposalData;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public TemplateSpecBean getTemplateSpecBean() {
		return templateSpecBean;
	}

	public void setTemplateSpecBean(TemplateSpecBean templateSpecBean) {
		this.templateSpecBean = templateSpecBean;
	}

	public ScQtCheckList getScQtCheckList() {
		return scQtCheckList;
	}

	public void setScQtCheckList(ScQtCheckList scQtCheckList) {
		this.scQtCheckList = scQtCheckList;
	}

	public int getPrepareActiveIndex() {
		return prepareActiveIndex;
	}

	public void setPrepareActiveIndex(int prepareActiveIndex) {
		this.prepareActiveIndex = prepareActiveIndex;
	}

	public List<ScQtPRPExperience> getScQtPRPExperiences() {
		return scQtPRPExperiences;
	}

	public void setScQtPRPExperiences(List<ScQtPRPExperience> scQtPRPExperiences) {
		this.scQtPRPExperiences = scQtPRPExperiences;
	}

	public List<ScQtPRPExperience> getSelectedPRPExperiences() {
		return selectedPRPExperiences;
	}

	public void setSelectedPRPExperiences(
			List<ScQtPRPExperience> selectedPRPExperiences) {
		this.selectedPRPExperiences = selectedPRPExperiences;
	}

	/**
	 * @return the scQtPRPExperienceNCheckLists
	 */
	public List<ScQtPRPExperienceNCheckList> getScQtPRPExperienceNCheckLists() {
		return scQtPRPExperienceNCheckLists;
	}

	/**
	 * @param scQtPRPExperienceNCheckLists the scQtPRPExperienceNCheckLists to set
	 */
	public void setScQtPRPExperienceNCheckLists(
			List<ScQtPRPExperienceNCheckList> scQtPRPExperienceNCheckLists) {
		this.scQtPRPExperienceNCheckLists = scQtPRPExperienceNCheckLists;
	}

	public List<ScQtPRPCreditFacility> getScQtPRPCreditFacilities() {
		return scQtPRPCreditFacilities;
	}

	public void setScQtPRPCreditFacilities(
			List<ScQtPRPCreditFacility> scQtPRPCreditFacilities) {
		this.scQtPRPCreditFacilities = scQtPRPCreditFacilities;
	}

	public List<ScQtPRPCreditFacility> getSelectedPRPCreditFacilities() {
		return selectedPRPCreditFacilities;
	}

	public void setSelectedPRPCreditFacilities(
			List<ScQtPRPCreditFacility> selectedPRPCreditFacilities) {
		this.selectedPRPCreditFacilities = selectedPRPCreditFacilities;
	}

	public List<ScQtPRPCashOnHand> getScQtPRPCashOnHand() {
		return scQtPRPCashOnHand;
	}

	public void setScQtPRPCashOnHand(List<ScQtPRPCashOnHand> scQtPRPCashOnHand) {
		this.scQtPRPCashOnHand = scQtPRPCashOnHand;
	}

	public List<ScQtPRPCashOnHand> getSelectedPRPCashOnHand() {
		return selectedPRPCashOnHand;
	}

	public void setSelectedPRPCashOnHand(
			List<ScQtPRPCashOnHand> selectedPRPCashOnHand) {
		this.selectedPRPCashOnHand = selectedPRPCashOnHand;
	}

	public List<ScQtPRPBankStmt> getScQtPRPBankStmts() {
		return scQtPRPBankStmts;
	}

	public void setScQtPRPBankStmts(List<ScQtPRPBankStmt> scQtPRPBankStmts) {
		this.scQtPRPBankStmts = scQtPRPBankStmts;
	}

	public List<ScQtPRPBankStmt> getSelectedPRPBankStmts() {
		return selectedPRPBankStmts;
	}

	public void setSelectedPRPBankStmts(
			List<ScQtPRPBankStmt> selectedPRPBankStmts) {
		this.selectedPRPBankStmts = selectedPRPBankStmts;
	}

	public ScQtPRPAssetLiability getScQtPRPAssetLiability() {
		return scQtPRPAssetLiability;
	}

	public void setScQtPRPAssetLiability(
			ScQtPRPAssetLiability scQtPRPAssetLiability) {
		this.scQtPRPAssetLiability = scQtPRPAssetLiability;
	}

	public List<ScQtPRPAssetLiability> getScQtPRPAssetLiabilities() {
		return scQtPRPAssetLiabilities;
	}

	public void setScQtPRPAssetLiabilities(
			List<ScQtPRPAssetLiability> scQtPRPAssetLiabilities) {
		this.scQtPRPAssetLiabilities = scQtPRPAssetLiabilities;
	}

	public List<ScQtPRPAssetLiability> getSelectedPRPAssetLiabilities() {
		return selectedPRPAssetLiabilities;
	}

	public void setSelectedPRPAssetLiabilities(
			List<ScQtPRPAssetLiability> selectedPRPAssetLiabilities) {
		this.selectedPRPAssetLiabilities = selectedPRPAssetLiabilities;
	}

	public BigDecimal getTotalAmountCredit() {
		return totalAmountCredit;
	}

	public void setTotalAmountCredit(BigDecimal totalAmountCredit) {
		this.totalAmountCredit = totalAmountCredit;
	}

	public BigDecimal getTotalAmountPaid() {
		return totalAmountPaid;
	}

	public void setTotalAmountPaid(BigDecimal totalAmountPaid) {
		this.totalAmountPaid = totalAmountPaid;
	}

	public BigDecimal getTotalPercentagePaid() {
		return totalPercentagePaid;
	}

	public void setTotalPercentagePaid(BigDecimal totalPercentagePaid) {
		this.totalPercentagePaid = totalPercentagePaid;
	}

	public List<ScQtProposalScore> getScQtProposalScoreList() {
		return scQtProposalScoreList;
	}

	public void setScQtProposalScoreList(
			List<ScQtProposalScore> scQtProposalScoreList) {
		this.scQtProposalScoreList = scQtProposalScoreList;
	}

	public ScQtProposalScore getScQtProposalScore() {
		return scQtProposalScore;
	}

	public void setScQtProposalScore(ScQtProposalScore scQtProposalScore) {
		this.scQtProposalScore = scQtProposalScore;
	}

	public BigDecimal getTotalcashHand() {
		return totalcashHand;
	}

	public void setTotalcashHand(BigDecimal totalcashHand) {
		this.totalcashHand = totalcashHand;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getAvgAmount() {
		return avgAmount;
	}

	public void setAvgAmount(BigDecimal avgAmount) {
		this.avgAmount = avgAmount;
	}

	public ScQtPRPEmployee getScQtPRPEmployee() {
		return scQtPRPEmployee;
	}

	public void setScQtPRPEmployee(ScQtPRPEmployee scQtPRPEmployee) {
		this.scQtPRPEmployee = scQtPRPEmployee;
	}

	public int getTotalContracts() {
		return totalContracts;
	}

	public void setTotalContracts(int totalContracts) {
		this.totalContracts = totalContracts;
	}

	public int getTotalYears() {
		return totalYears;
	}

	public void setTotalYears(int totalYears) {
		this.totalYears = totalYears;
	}

	public BigDecimal getTotalContractAmount() {
		return totalContractAmount;
	}

	public void setTotalContractAmount(BigDecimal totalContractAmount) {
		this.totalContractAmount = totalContractAmount;
	}

	public ScQtSampleSubmission getScQtSampleSubmission() {
		return scQtSampleSubmission;
	}

	public void setScQtSampleSubmission(
			ScQtSampleSubmission scQtSampleSubmission) {
		this.scQtSampleSubmission = scQtSampleSubmission;
	}

	public List<ScQtSampleSubmission> getScQtSampleSubmissionList() {
		return scQtSampleSubmissionList;
	}

	public void setScQtSampleSubmissionList(
			List<ScQtSampleSubmission> scQtSampleSubmissionList) {
		this.scQtSampleSubmissionList = scQtSampleSubmissionList;
	}

	public List<ScQtSampleItem> getScQtSampleItemList() {
		return scQtSampleItemList;
	}

	public void setScQtSampleItemList(List<ScQtSampleItem> scQtSampleItemList) {
		this.scQtSampleItemList = scQtSampleItemList;
	}

	public List<ScLoa> getScLoaList() {
		return scLoaList;
	}

	public void setScLoaList(List<ScLoa> scLoaList) {
		this.scLoaList = scLoaList;
	}

	public int getContractSize() {
		return contractSize;
	}

	public void setContractSize(int contractSize) {
		this.contractSize = contractSize;
	}

	public int getNoOfYears() {
		return noOfYears;
	}

	public void setNoOfYears(int noOfYears) {
		this.noOfYears = noOfYears;
	}

	public ScQtDoc getScQtDoc() {
		return scQtDoc;
	}

	public void setScQtDoc(ScQtDoc scQtDoc) {
		this.scQtDoc = scQtDoc;
	}

	public long getDays() {
		return days;
	}

	public void setDays(long days) {
		this.days = days;
	}

	public List<TplSpecZoneDtl> getZones() {
		return zones;
	}

	public void setZones(List<TplSpecZoneDtl> zones) {
		this.zones = zones;
	}

	public TplSpecZoneDtl getSelectedZone() {
		return selectedZone;
	}

	public void setSelectedZone(TplSpecZoneDtl selectedZone) {
		this.selectedZone = selectedZone;
	}

	public int getPrepareProposal() {
		return prepareProposal;
	}

	public void setPrepareProposal(int prepareProposal) {
		this.prepareProposal = prepareProposal;
	}

	public List<ScQtProposal> getQtProposalsList() {
		return qtProposalsList;
	}

	public void setQtProposalsList(List<ScQtProposal> qtProposalsList) {
		this.qtProposalsList = qtProposalsList;
	}

	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	public int getProposalIndex() {
		return proposalIndex;
	}

	public void setProposalIndex(int proposalIndex) {
		this.proposalIndex = proposalIndex;
	}

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	public SmAppl getSmAppl() {
		return smAppl;
	}

	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}

	public List<TechnicalStaffBean> getSmPersonnelList() {
		return smPersonnelList;
	}

	public void setSmPersonnelList(List<TechnicalStaffBean> smPersonnelList) {
		this.smPersonnelList = smPersonnelList;
	}

	public int getTotalNoOfYearsExperience() {
		return totalNoOfYearsExperience;
	}

	public void setTotalNoOfYearsExperience(int totalNoOfYearsExperience) {
		this.totalNoOfYearsExperience = totalNoOfYearsExperience;
	}

	/**
	 * @return the technicalTableRow
	 */
	public int getTechnicalTableRow() {
		return technicalTableRow;
	}

	/**
	 * @param technicalTableRow the technicalTableRow to set
	 */
	public void setTechnicalTableRow(int technicalTableRow) {
		this.technicalTableRow = technicalTableRow;
	}

	/**
	 * @return the financialTableRow
	 */
	public int getFinancialTableRow() {
		return financialTableRow;
	}

	/**
	 * @param financialTableRow the financialTableRow to set
	 */
	public void setFinancialTableRow(int financialTableRow) {
		this.financialTableRow = financialTableRow;
	}
	
}
