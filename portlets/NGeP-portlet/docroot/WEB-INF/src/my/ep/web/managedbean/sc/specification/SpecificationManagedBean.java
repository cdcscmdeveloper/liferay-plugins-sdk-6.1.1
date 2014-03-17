/**
 * 
 */
package my.ep.web.managedbean.sc.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.circular.dtl.CtCircularZone;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.pm.common.PmCheckListCategory;
import my.ep.persistence.entity.pm.common.PmReportSection;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtFreqSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtProgessivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSpec;
import my.ep.persistence.entity.sc.quationtender.ScQtWeightage;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.web.backingbean.sc.specification.BankingBean;
import my.ep.web.backingbean.sc.specification.SpecificationCompilanceBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

/**
 * @author ACER
 * 
 */
@ManagedBean(name = "specificationMB")
@SessionScoped
public class SpecificationManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(SpecificationManagedBean.class);

	private ScQtSpec scQtSpec;
	
	private ScQtRemark scQtRemarks;
	
	private ScQtCheckList scQtChecklist;
	
	private List<ScQtCheckList> scQtCheckSt1techProposal;
	
	private ScQtDeduction scQtDeduction;
	
	private List<ScQtDeduction> scQtDeductionList;
	
	private List<ScQtCheckList> scQtCheckSt1techAdditionalProposalList;
	private List<ScQtCheckList> scQtCheckSt1financeProposalList;
	
	private List<ScQtCheckList> scQtCheckSt1financeAdditionalProposalList;
	
	private List<ScQtCheckList> scQtCheckSt2techProposal;
	
	private List<ScQtCheckList> scQtCheckSt2techAdditionalProposalList;
	private List<ScQtCheckList> scQtCheckSt2financeProposalList;
	
	private List<ScQtCheckList> scQtCheckSt2financeAdditionalProposalList;
	
	private List<String> compilanceList=new ArrayList<String>();
	 public List<SpecificationCompilanceBean> compilaenceList;
	 private ScRemark scRemark;
	 private List<SelectItem> comitteeUsers;
	 private List<SelectItem> currencyListDetails;
	 private List<SelectItem> procurementListDetails;
	 private List<ScCommitteeMember> committeeMemberList;
	 
	 private Object specificationDataType;
	
	
	public boolean isSampleReq=true;
	public String approvalDate;
	private List<ScQtWeightage> weightageTechnical;
	private List<ScQtWeightage> weightageFinancial;
	private List<ScQtWeightage> weightageTechnicalSelect;
	private List<ScQtWeightage> weightageFinancialSelect;
	 private boolean isSpecStatus;
	 private Long procurementId;
	 private List<SelectItem> approvalList;
	 private String evaluationType;
	private ScQt scQt;
	private List<PmCheckListCategory> stageOneComplianceList;
	private List<PmCheckListCategory> stageOneComplianceSelect;
	private List<PmCheckListCategory> stageTwoTechComplianceList;
	private List<PmCheckListCategory> stageTwoTechComplianceSelect;
	private List<PmCheckListCategory> stageTwoFinComplianceList;
	private List<PmCheckListCategory> stageTwoFinComplianceSelect;
	private List<ScQtCheckList> s1TechSelect;
	private List<ScQtCheckList> s1TechAddSelect;
	private List<ScQtCheckList> s1FinSelect;
	private List<ScQtCheckList> s1FinAddSelect;
	private List<ScQtCheckList> s2TechSelect;
	private List<ScQtCheckList> s2TechAddSelect;
	private List<ScQtCheckList> s2FinSelect;
	private List<ScQtCheckList> s2FinAddSelect;
	private List<ScQtCheckListScore> scoringList;
	private List<ScQtCheckListScore> selectscoring;
	private List<ScQtCheckListScore> collectionScoring;
	private String scheduleType;
	private String fromMonth;
	private String toMonth;
	//B,S
	private String businessOwner="B";
	private List<BankingBean> bankList;
	private List<ScQtCheckListScore> checkListScore=new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> scoringForYears=new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> contractAmount=new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> bankStamentScore=new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> creditFacility=new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> scoringCash=new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> assetScore=new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> warantyScore=new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> trainingScore=new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> totalEmployeeScore=new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> technicalStaffScore=new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> paidUpCapitalScore=new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> nonFederalScore=new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> nonFederalScoreyears=new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> nonFederalContractScore=new ArrayList<ScQtCheckListScore>();
	
	private List<ScQtFreqSchedule> scheduleList;
	private List<ScQtProgessivePayment> progressivePaymentList;
	
	private int totalFrequency;
	private int totalProgressivePayment;
	private String technicalWeightage="2";
	private String financialWeightage="2";
	private String cirCularNo;
	private String circularName;
	private String itemName;
	private BigDecimal technicalWeightageScore;
	private BigDecimal financialWeightageScore;
	private int totalAmount;
	private BigDecimal totalAmountAvg;
	
	private List<CtPanelItem> panelItemList;
	private List<CtPanelItem> selectpanelItem;
	private List<CtPanelItem> panelItemDetailsList;
	private List<CtPanelItem> selectItemDetailsList;
	private boolean isPanel;
	private List<CtCircularZone> ctZoneList;
	private List<CtCircularZone> ctZoneSelectList;
	private TreeNode	ctZoneTrees ;
	private TreeNode[]	ctZoneTreeSelectedNodes ;
	private TreeNode	ctZoneSuppliers ;
	
	private List<CtPanelSupplier> ctZoneSupplierList;
	
	
	//supplier Qualification Criteria List and variables
	private List<ScQtCategory> suppCriteriarestrictedList;
	private List<ScQtCategory> suppCriteriaopenList;
	private ScQtCriteria qtCriteria;
	private	TreeNode rootLocationTree;
	private	String	zoneName;

	private List<TreeNode>	zoneTrees = new ArrayList<TreeNode>();
	private List<TreeNode>	zonesList = new ArrayList<TreeNode>();
	private List<TreeNode>	zoneTreeList = new ArrayList<TreeNode>();
	private List<TreeNode>	zoneTreeAddrList = new ArrayList<TreeNode>();
	private	String	zoneName1;
	private	String	zoneAddrName;
	private List<PmReportSection> pmReportSectionList;
	private List<PmReportSection> selectedPmReportSectionList;
	private	TreeNode rootLocationTreeList;
	private TreeNode rootAddressLocationTreeList;
	private TreeNode[] selectedNodes;
	private TreeNode[] selectedLocNodes;
	private String circularExpDate;
	private String circularType;
	
	//template Specification variables
	private String standardTemplate="ST";
	private String supportingDoc;
	private String templateType;
	private String specificationType;
	public ScQtDoc scqtDoc;
	private String ministryName;
	public List<ScQtCheckList> supportDocList;
	private Long searchItemTypeId;
	private Long searchSpecTypeId;
	private Long searchProcurementId;
	private List<ScQtDoc> docSearchlist;
	private List<ScQtCheckList> checkListItem;
	private List<SelectItem> zoneList;
	private String specName;
	private String qtNum;
	private Long statusId;
	private boolean isRecordStatus;
	private int index=-1;
	private List<ScQtCategory> qtCategoryOpenSelectList;
	private List<ScQtCategory> suppCriteriarestrictedSelectList;
	
	private BigDecimal estimatedPrice=new BigDecimal("50000");
	private int fromJulat;
	private int toJulat;
	private BigDecimal fromPrice;
	private BigDecimal toPrice;
	
	private ScQtCategory qtCategory=new ScQtCategory();
	
	
	//technical Scoring Financial Scoring lists
	private List<ScQtCheckList> technicalScoring;
	private List<ScQtCheckList> financialScoring;
	private List<ScQtCheckListScore> packScoringList;
	private int techPoints;
	private int finPoints;
	private List<ScQtCheckList> technicalSpecList;
	
	private List<SelectItem> yearList;
	
	private int totalSuppliers;
	
	
	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();
	
	
	public SpecificationManagedBean(){
		
	}

	/**
	 * @return the scQtSpec
	 */
	public ScQtSpec getScQtSpec() {
		return scQtSpec;
	}

	/**
	 * @param scQtSpec the scQtSpec to set
	 */
	public void setScQtSpec(ScQtSpec scQtSpec) {
		this.scQtSpec = scQtSpec;
	}

	/**
	 * @return the scQtRemarks
	 */
	public ScQtRemark getScQtRemarks() {
		return scQtRemarks;
	}

	/**
	 * @param scQtRemarks the scQtRemarks to set
	 */
	public void setScQtRemarks(ScQtRemark scQtRemarks) {
		this.scQtRemarks = scQtRemarks;
	}

	/**
	 * @return the scQtChecklist
	 */
	public ScQtCheckList getScQtChecklist() {
		return scQtChecklist;
	}

	/**
	 * @param scQtChecklist the scQtChecklist to set
	 */
	public void setScQtChecklist(ScQtCheckList scQtChecklist) {
		this.scQtChecklist = scQtChecklist;
	}

	/**
	 * @return the scQtCheckSt1techProposal
	 */
	public List<ScQtCheckList> getScQtCheckSt1techProposal() {
		return scQtCheckSt1techProposal;
	}

	/**
	 * @param scQtCheckSt1techProposal the scQtCheckSt1techProposal to set
	 */
	public void setScQtCheckSt1techProposal(
			List<ScQtCheckList> scQtCheckSt1techProposal) {
		this.scQtCheckSt1techProposal = scQtCheckSt1techProposal;
	}

	/**
	 * @return the scQtDeduction
	 */
	public ScQtDeduction getScQtDeduction() {
		return scQtDeduction;
	}

	/**
	 * @param scQtDeduction the scQtDeduction to set
	 */
	public void setScQtDeduction(ScQtDeduction scQtDeduction) {
		this.scQtDeduction = scQtDeduction;
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
	 * @return the scQtCheckSt1techAdditionalProposalList
	 */
	public List<ScQtCheckList> getScQtCheckSt1techAdditionalProposalList() {
		return scQtCheckSt1techAdditionalProposalList;
	}

	/**
	 * @param scQtCheckSt1techAdditionalProposalList the scQtCheckSt1techAdditionalProposalList to set
	 */
	public void setScQtCheckSt1techAdditionalProposalList(
			List<ScQtCheckList> scQtCheckSt1techAdditionalProposalList) {
		this.scQtCheckSt1techAdditionalProposalList = scQtCheckSt1techAdditionalProposalList;
	}

	/**
	 * @return the scQtCheckSt1financeProposalList
	 */
	public List<ScQtCheckList> getScQtCheckSt1financeProposalList() {
		return scQtCheckSt1financeProposalList;
	}

	/**
	 * @param scQtCheckSt1financeProposalList the scQtCheckSt1financeProposalList to set
	 */
	public void setScQtCheckSt1financeProposalList(
			List<ScQtCheckList> scQtCheckSt1financeProposalList) {
		this.scQtCheckSt1financeProposalList = scQtCheckSt1financeProposalList;
	}

	/**
	 * @return the scQtCheckSt1financeAdditionalProposalList
	 */
	public List<ScQtCheckList> getScQtCheckSt1financeAdditionalProposalList() {
		return scQtCheckSt1financeAdditionalProposalList;
	}

	/**
	 * @param scQtCheckSt1financeAdditionalProposalList the scQtCheckSt1financeAdditionalProposalList to set
	 */
	public void setScQtCheckSt1financeAdditionalProposalList(
			List<ScQtCheckList> scQtCheckSt1financeAdditionalProposalList) {
		this.scQtCheckSt1financeAdditionalProposalList = scQtCheckSt1financeAdditionalProposalList;
	}

	/**
	 * @return the scQtCheckSt2techProposal
	 */
	public List<ScQtCheckList> getScQtCheckSt2techProposal() {
		return scQtCheckSt2techProposal;
	}

	/**
	 * @param scQtCheckSt2techProposal the scQtCheckSt2techProposal to set
	 */
	public void setScQtCheckSt2techProposal(
			List<ScQtCheckList> scQtCheckSt2techProposal) {
		this.scQtCheckSt2techProposal = scQtCheckSt2techProposal;
	}

	/**
	 * @return the scQtCheckSt2techAdditionalProposalList
	 */
	public List<ScQtCheckList> getScQtCheckSt2techAdditionalProposalList() {
		return scQtCheckSt2techAdditionalProposalList;
	}

	/**
	 * @param scQtCheckSt2techAdditionalProposalList the scQtCheckSt2techAdditionalProposalList to set
	 */
	public void setScQtCheckSt2techAdditionalProposalList(
			List<ScQtCheckList> scQtCheckSt2techAdditionalProposalList) {
		this.scQtCheckSt2techAdditionalProposalList = scQtCheckSt2techAdditionalProposalList;
	}

	/**
	 * @return the scQtCheckSt2financeProposalList
	 */
	public List<ScQtCheckList> getScQtCheckSt2financeProposalList() {
		return scQtCheckSt2financeProposalList;
	}

	/**
	 * @param scQtCheckSt2financeProposalList the scQtCheckSt2financeProposalList to set
	 */
	public void setScQtCheckSt2financeProposalList(
			List<ScQtCheckList> scQtCheckSt2financeProposalList) {
		this.scQtCheckSt2financeProposalList = scQtCheckSt2financeProposalList;
	}

	/**
	 * @return the scQtCheckSt2financeAdditionalProposalList
	 */
	public List<ScQtCheckList> getScQtCheckSt2financeAdditionalProposalList() {
		return scQtCheckSt2financeAdditionalProposalList;
	}

	/**
	 * @param scQtCheckSt2financeAdditionalProposalList the scQtCheckSt2financeAdditionalProposalList to set
	 */
	public void setScQtCheckSt2financeAdditionalProposalList(
			List<ScQtCheckList> scQtCheckSt2financeAdditionalProposalList) {
		this.scQtCheckSt2financeAdditionalProposalList = scQtCheckSt2financeAdditionalProposalList;
	}

	/**
	 * @return the compilanceList
	 */
	public List<String> getCompilanceList() {
		return compilanceList;
	}

	/**
	 * @param compilanceList the compilanceList to set
	 */
	public void setCompilanceList(List<String> compilanceList) {
		this.compilanceList = compilanceList;
	}

	/**
	 * @return the compilaenceList
	 */
	public List<SpecificationCompilanceBean> getCompilaenceList() {
		return compilaenceList;
	}

	/**
	 * @param compilaenceList the compilaenceList to set
	 */
	public void setCompilaenceList(List<SpecificationCompilanceBean> compilaenceList) {
		this.compilaenceList = compilaenceList;
	}

	/**
	 * @return the scRemark
	 */
	public ScRemark getScRemark() {
		return scRemark;
	}

	/**
	 * @param scRemark the scRemark to set
	 */
	public void setScRemark(ScRemark scRemark) {
		this.scRemark = scRemark;
	}

	/**
	 * @return the isSampleReq
	 */
	public boolean isSampleReq() {
		return isSampleReq;
	}

	/**
	 * @param isSampleReq the isSampleReq to set
	 */
	public void setSampleReq(boolean isSampleReq) {
		this.isSampleReq = isSampleReq;
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
	 * @return the currencyListDetails
	 */
	public List<SelectItem> getCurrencyListDetails() {
		return currencyListDetails;
	}

	/**
	 * @param currencyListDetails the currencyListDetails to set
	 */
	public void setCurrencyListDetails(List<SelectItem> currencyListDetails) {
		this.currencyListDetails = currencyListDetails;
	}

	/**
	 * @return the procurementListDetails
	 */
	public List<SelectItem> getProcurementListDetails() {
		return procurementListDetails;
	}

	/**
	 * @param procurementListDetails the procurementListDetails to set
	 */
	public void setProcurementListDetails(List<SelectItem> procurementListDetails) {
		this.procurementListDetails = procurementListDetails;
	}

	/**
	 * @return the committeeMemberList
	 */
	public List<ScCommitteeMember> getCommitteeMemberList() {
		return committeeMemberList;
	}

	/**
	 * @param committeeMemberList the committeeMemberList to set
	 */
	public void setCommitteeMemberList(List<ScCommitteeMember> committeeMemberList) {
		this.committeeMemberList = committeeMemberList;
	}

	public Object getSpecificationDataType() {
		return specificationDataType;
	}

	public void setSpecificationDataType(Object specificationDataType) {
		this.specificationDataType = specificationDataType;
	}

	/**
	 * @return the weightageTechnical
	 */
	public List<ScQtWeightage> getWeightageTechnical() {
		return weightageTechnical;
	}

	/**
	 * @param weightageTechnical the weightageTechnical to set
	 */
	public void setWeightageTechnical(List<ScQtWeightage> weightageTechnical) {
		this.weightageTechnical = weightageTechnical;
	}

	/**
	 * @return the weightageFinancial
	 */
	public List<ScQtWeightage> getWeightageFinancial() {
		return weightageFinancial;
	}

	/**
	 * @param weightageFinancial the weightageFinancial to set
	 */
	public void setWeightageFinancial(List<ScQtWeightage> weightageFinancial) {
		this.weightageFinancial = weightageFinancial;
	}

	/**
	 * @return the weightageTechnicalSelect
	 */
	public List<ScQtWeightage> getWeightageTechnicalSelect() {
		return weightageTechnicalSelect;
	}

	/**
	 * @param weightageTechnicalSelect the weightageTechnicalSelect to set
	 */
	public void setWeightageTechnicalSelect(
			List<ScQtWeightage> weightageTechnicalSelect) {
		this.weightageTechnicalSelect = weightageTechnicalSelect;
	}

	/**
	 * @return the weightageFinancialSelect
	 */
	public List<ScQtWeightage> getWeightageFinancialSelect() {
		return weightageFinancialSelect;
	}

	/**
	 * @param weightageFinancialSelect the weightageFinancialSelect to set
	 */
	public void setWeightageFinancialSelect(
			List<ScQtWeightage> weightageFinancialSelect) {
		this.weightageFinancialSelect = weightageFinancialSelect;
	}

	/**
	 * @return the isSpecStatus
	 */
	public boolean isSpecStatus() {
		return isSpecStatus;
	}

	/**
	 * @param isSpecStatus the isSpecStatus to set
	 */
	public void setSpecStatus(boolean isSpecStatus) {
		this.isSpecStatus = isSpecStatus;
	}

	/**
	 * @return the procurementId
	 */
	public Long getProcurementId() {
		return procurementId;
	}

	/**
	 * @param procurementId the procurementId to set
	 */
	public void setProcurementId(Long procurementId) {
		this.procurementId = procurementId;
	}

	/**
	 * @return the approvalList
	 */
	public List<SelectItem> getApprovalList() {
		return approvalList;
	}

	/**
	 * @param approvalList the approvalList to set
	 */
	public void setApprovalList(List<SelectItem> approvalList) {
		this.approvalList = approvalList;
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
	 * @return the stageOneComplianceList
	 */
	public List<PmCheckListCategory> getStageOneComplianceList() {
		return stageOneComplianceList;
	}

	/**
	 * @param stageOneComplianceList the stageOneComplianceList to set
	 */
	public void setStageOneComplianceList(
			List<PmCheckListCategory> stageOneComplianceList) {
		this.stageOneComplianceList = stageOneComplianceList;
	}

	/**
	 * @return the stageOneComplianceSelect
	 */
	public List<PmCheckListCategory> getStageOneComplianceSelect() {
		return stageOneComplianceSelect;
	}

	/**
	 * @param stageOneComplianceSelect the stageOneComplianceSelect to set
	 */
	public void setStageOneComplianceSelect(
			List<PmCheckListCategory> stageOneComplianceSelect) {
		this.stageOneComplianceSelect = stageOneComplianceSelect;
	}

	/**
	 * @return the stageTwoTechComplianceList
	 */
	public List<PmCheckListCategory> getStageTwoTechComplianceList() {
		return stageTwoTechComplianceList;
	}

	/**
	 * @param stageTwoTechComplianceList the stageTwoTechComplianceList to set
	 */
	public void setStageTwoTechComplianceList(
			List<PmCheckListCategory> stageTwoTechComplianceList) {
		this.stageTwoTechComplianceList = stageTwoTechComplianceList;
	}

	/**
	 * @return the stageTwoTechComplianceSelect
	 */
	public List<PmCheckListCategory> getStageTwoTechComplianceSelect() {
		return stageTwoTechComplianceSelect;
	}

	/**
	 * @param stageTwoTechComplianceSelect the stageTwoTechComplianceSelect to set
	 */
	public void setStageTwoTechComplianceSelect(
			List<PmCheckListCategory> stageTwoTechComplianceSelect) {
		this.stageTwoTechComplianceSelect = stageTwoTechComplianceSelect;
	}

	/**
	 * @return the stageTwoFinComplianceList
	 */
	public List<PmCheckListCategory> getStageTwoFinComplianceList() {
		return stageTwoFinComplianceList;
	}

	/**
	 * @param stageTwoFinComplianceList the stageTwoFinComplianceList to set
	 */
	public void setStageTwoFinComplianceList(
			List<PmCheckListCategory> stageTwoFinComplianceList) {
		this.stageTwoFinComplianceList = stageTwoFinComplianceList;
	}

	/**
	 * @return the stageTwoFinComplianceSelect
	 */
	public List<PmCheckListCategory> getStageTwoFinComplianceSelect() {
		return stageTwoFinComplianceSelect;
	}

	/**
	 * @param stageTwoFinComplianceSelect the stageTwoFinComplianceSelect to set
	 */
	public void setStageTwoFinComplianceSelect(
			List<PmCheckListCategory> stageTwoFinComplianceSelect) {
		this.stageTwoFinComplianceSelect = stageTwoFinComplianceSelect;
	}

	/**
	 * @return the s1TechSelect
	 */
	public List<ScQtCheckList> getS1TechSelect() {
		return s1TechSelect;
	}

	/**
	 * @param s1TechSelect the s1TechSelect to set
	 */
	public void setS1TechSelect(List<ScQtCheckList> s1TechSelect) {
		this.s1TechSelect = s1TechSelect;
	}

	/**
	 * @return the s1TechAddSelect
	 */
	public List<ScQtCheckList> getS1TechAddSelect() {
		return s1TechAddSelect;
	}

	/**
	 * @param s1TechAddSelect the s1TechAddSelect to set
	 */
	public void setS1TechAddSelect(List<ScQtCheckList> s1TechAddSelect) {
		this.s1TechAddSelect = s1TechAddSelect;
	}

	/**
	 * @return the s1FinSelect
	 */
	public List<ScQtCheckList> getS1FinSelect() {
		return s1FinSelect;
	}

	/**
	 * @param s1FinSelect the s1FinSelect to set
	 */
	public void setS1FinSelect(List<ScQtCheckList> s1FinSelect) {
		this.s1FinSelect = s1FinSelect;
	}

	/**
	 * @return the s1FinAddSelect
	 */
	public List<ScQtCheckList> getS1FinAddSelect() {
		return s1FinAddSelect;
	}

	/**
	 * @param s1FinAddSelect the s1FinAddSelect to set
	 */
	public void setS1FinAddSelect(List<ScQtCheckList> s1FinAddSelect) {
		this.s1FinAddSelect = s1FinAddSelect;
	}

	/**
	 * @return the s2TechSelect
	 */
	public List<ScQtCheckList> getS2TechSelect() {
		return s2TechSelect;
	}

	/**
	 * @param s2TechSelect the s2TechSelect to set
	 */
	public void setS2TechSelect(List<ScQtCheckList> s2TechSelect) {
		this.s2TechSelect = s2TechSelect;
	}

	/**
	 * @return the s2TechAddSelect
	 */
	public List<ScQtCheckList> getS2TechAddSelect() {
		return s2TechAddSelect;
	}

	/**
	 * @param s2TechAddSelect the s2TechAddSelect to set
	 */
	public void setS2TechAddSelect(List<ScQtCheckList> s2TechAddSelect) {
		this.s2TechAddSelect = s2TechAddSelect;
	}

	/**
	 * @return the s2FinSelect
	 */
	public List<ScQtCheckList> getS2FinSelect() {
		return s2FinSelect;
	}

	/**
	 * @param s2FinSelect the s2FinSelect to set
	 */
	public void setS2FinSelect(List<ScQtCheckList> s2FinSelect) {
		this.s2FinSelect = s2FinSelect;
	}

	/**
	 * @return the s2FinAddSelect
	 */
	public List<ScQtCheckList> getS2FinAddSelect() {
		return s2FinAddSelect;
	}

	/**
	 * @param s2FinAddSelect the s2FinAddSelect to set
	 */
	public void setS2FinAddSelect(List<ScQtCheckList> s2FinAddSelect) {
		this.s2FinAddSelect = s2FinAddSelect;
	}

	/**
	 * @return the scoringList
	 */
	public List<ScQtCheckListScore> getScoringList() {
		return scoringList;
	}

	/**
	 * @param scoringList the scoringList to set
	 */
	public void setScoringList(List<ScQtCheckListScore> scoringList) {
		this.scoringList = scoringList;
	}

	/**
	 * @return the selectscoring
	 */
	public List<ScQtCheckListScore> getSelectscoring() {
		return selectscoring;
	}

	/**
	 * @param selectscoring the selectscoring to set
	 */
	public void setSelectscoring(List<ScQtCheckListScore> selectscoring) {
		this.selectscoring = selectscoring;
	}

	/**
	 * @return the collectionScoring
	 */
	public List<ScQtCheckListScore> getCollectionScoring() {
		return collectionScoring;
	}

	/**
	 * @param collectionScoring the collectionScoring to set
	 */
	public void setCollectionScoring(List<ScQtCheckListScore> collectionScoring) {
		this.collectionScoring = collectionScoring;
	}

	/**
	 * @return the scheduleType
	 */
	public String getScheduleType() {
		return scheduleType;
	}

	/**
	 * @param scheduleType the scheduleType to set
	 */
	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	/**
	 * @return the businessOwner
	 */
	public String getBusinessOwner() {
		return businessOwner;
	}

	/**
	 * @param businessOwner the businessOwner to set
	 */
	public void setBusinessOwner(String businessOwner) {
		this.businessOwner = businessOwner;
	}

	/**
	 * @return the fromMonth
	 */
	public String getFromMonth() {
		return fromMonth;
	}

	/**
	 * @param fromMonth the fromMonth to set
	 */
	public void setFromMonth(String fromMonth) {
		this.fromMonth = fromMonth;
	}

	/**
	 * @return the toMonth
	 */
	public String getToMonth() {
		return toMonth;
	}

	/**
	 * @param toMonth the toMonth to set
	 */
	public void setToMonth(String toMonth) {
		this.toMonth = toMonth;
	}

	/**
	 * @return the bankList
	 */
	public List<BankingBean> getBankList() {
		return bankList;
	}

	/**
	 * @param bankList the bankList to set
	 */
	public void setBankList(List<BankingBean> bankList) {
		this.bankList = bankList;
	}

	/**
	 * @return the checkListScore
	 */
	public List<ScQtCheckListScore> getCheckListScore() {
		return checkListScore;
	}

	/**
	 * @param checkListScore the checkListScore to set
	 */
	public void setCheckListScore(List<ScQtCheckListScore> checkListScore) {
		this.checkListScore = checkListScore;
	}

	/**
	 * @return the scoringForYears
	 */
	public List<ScQtCheckListScore> getScoringForYears() {
		return scoringForYears;
	}

	/**
	 * @param scoringForYears the scoringForYears to set
	 */
	public void setScoringForYears(List<ScQtCheckListScore> scoringForYears) {
		this.scoringForYears = scoringForYears;
	}

	/**
	 * @return the contractAmount
	 */
	public List<ScQtCheckListScore> getContractAmount() {
		return contractAmount;
	}

	/**
	 * @param contractAmount the contractAmount to set
	 */
	public void setContractAmount(List<ScQtCheckListScore> contractAmount) {
		this.contractAmount = contractAmount;
	}

	/**
	 * @return the bankStamentScore
	 */
	public List<ScQtCheckListScore> getBankStamentScore() {
		return bankStamentScore;
	}

	/**
	 * @param bankStamentScore the bankStamentScore to set
	 */
	public void setBankStamentScore(List<ScQtCheckListScore> bankStamentScore) {
		this.bankStamentScore = bankStamentScore;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the log
	 */
	public static Logger getLog() {
		return log;
	}

	/**
	 * @return the creditFacility
	 */
	public List<ScQtCheckListScore> getCreditFacility() {
		return creditFacility;
	}

	/**
	 * @param creditFacility the creditFacility to set
	 */
	public void setCreditFacility(List<ScQtCheckListScore> creditFacility) {
		this.creditFacility = creditFacility;
	}

	/**
	 * @return the scoringCash
	 */
	public List<ScQtCheckListScore> getScoringCash() {
		return scoringCash;
	}

	/**
	 * @param scoringCash the scoringCash to set
	 */
	public void setScoringCash(List<ScQtCheckListScore> scoringCash) {
		this.scoringCash = scoringCash;
	}

	/**
	 * @return the assetScore
	 */
	public List<ScQtCheckListScore> getAssetScore() {
		return assetScore;
	}

	/**
	 * @param assetScore the assetScore to set
	 */
	public void setAssetScore(List<ScQtCheckListScore> assetScore) {
		this.assetScore = assetScore;
	}

	/**
	 * @return the scheduleList
	 */
	public List<ScQtFreqSchedule> getScheduleList() {
		return scheduleList;
	}

	/**
	 * @param scheduleList the scheduleList to set
	 */
	public void setScheduleList(List<ScQtFreqSchedule> scheduleList) {
		this.scheduleList = scheduleList;
	}

	/**
	 * @return the progressivePaymentList
	 */
	public List<ScQtProgessivePayment> getProgressivePaymentList() {
		return progressivePaymentList;
	}

	/**
	 * @param progressivePaymentList the progressivePaymentList to set
	 */
	public void setProgressivePaymentList(
			List<ScQtProgessivePayment> progressivePaymentList) {
		this.progressivePaymentList = progressivePaymentList;
	}

	/**
	 * @return the totalFrequency
	 */
	public int getTotalFrequency() {
		return totalFrequency;
	}

	/**
	 * @param totalFrequency the totalFrequency to set
	 */
	public void setTotalFrequency(int totalFrequency) {
		this.totalFrequency = totalFrequency;
	}

	/**
	 * @return the financialWeightage
	 */
	public String getFinancialWeightage() {
		return financialWeightage;
	}

	/**
	 * @param financialWeightage the financialWeightage to set
	 */
	public void setFinancialWeightage(String financialWeightage) {
		this.financialWeightage = financialWeightage;
	}

	/**
	 * @return the technicalWeightage
	 */
	public String getTechnicalWeightage() {
		return technicalWeightage;
	}

	/**
	 * @param technicalWeightage the technicalWeightage to set
	 */
	public void setTechnicalWeightage(String technicalWeightage) {
		this.technicalWeightage = technicalWeightage;
	}

	/**
	 * @return the cirCularNo
	 */
	public String getCirCularNo() {
		return cirCularNo;
	}

	/**
	 * @param cirCularNo the cirCularNo to set
	 */
	public void setCirCularNo(String cirCularNo) {
		this.cirCularNo = cirCularNo;
	}

	/**
	 * @return the circularName
	 */
	public String getCircularName() {
		return circularName;
	}

	/**
	 * @param circularName the circularName to set
	 */
	public void setCircularName(String circularName) {
		this.circularName = circularName;
	}

	/**
	 * @return the panelItemList
	 */
	public List<CtPanelItem> getPanelItemList() {
		return panelItemList;
	}

	/**
	 * @param panelItemList the panelItemList to set
	 */
	public void setPanelItemList(List<CtPanelItem> panelItemList) {
		this.panelItemList = panelItemList;
	}

	/**
	 * @return the selectpanelItem
	 */
	public List<CtPanelItem> getSelectpanelItem() {
		return selectpanelItem;
	}

	/**
	 * @param selectpanelItem the selectpanelItem to set
	 */
	public void setSelectpanelItem(List<CtPanelItem> selectpanelItem) {
		this.selectpanelItem = selectpanelItem;
	}

	/**
	 * @return the panelItemDetailsList
	 */
	public List<CtPanelItem> getPanelItemDetailsList() {
		return panelItemDetailsList;
	}

	/**
	 * @param panelItemDetailsList the panelItemDetailsList to set
	 */
	public void setPanelItemDetailsList(List<CtPanelItem> panelItemDetailsList) {
		this.panelItemDetailsList = panelItemDetailsList;
	}

	/**
	 * @return the selectItemDetailsList
	 */
	public List<CtPanelItem> getSelectItemDetailsList() {
		return selectItemDetailsList;
	}

	/**
	 * @param selectItemDetailsList the selectItemDetailsList to set
	 */
	public void setSelectItemDetailsList(List<CtPanelItem> selectItemDetailsList) {
		this.selectItemDetailsList = selectItemDetailsList;
	}

	/**
	 * @return the technicalWeightageScore
	 */
	public BigDecimal getTechnicalWeightageScore() {
		return technicalWeightageScore;
	}

	/**
	 * @param technicalWeightageScore the technicalWeightageScore to set
	 */
	public void setTechnicalWeightageScore(BigDecimal technicalWeightageScore) {
		this.technicalWeightageScore = technicalWeightageScore;
	}

	/**
	 * @return the totalAmount
	 */
	public int getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * @return the totalAmountAvg
	 */
	public BigDecimal getTotalAmountAvg() {
		return totalAmountAvg;
	}

	/**
	 * @param totalAmountAvg the totalAmountAvg to set
	 */
	public void setTotalAmountAvg(BigDecimal totalAmountAvg) {
		this.totalAmountAvg = totalAmountAvg;
	}

	/**
	 * @return the financialWeightageScore
	 */
	public BigDecimal getFinancialWeightageScore() {
		return financialWeightageScore;
	}

	/**
	 * @param financialWeightageScore the financialWeightageScore to set
	 */
	public void setFinancialWeightageScore(BigDecimal financialWeightageScore) {
		this.financialWeightageScore = financialWeightageScore;
	}

	/**
	 * @return the isPanel
	 */
	public boolean isPanel() {
		return isPanel;
	}

	/**
	 * @param isPanel the isPanel to set
	 */
	public void setPanel(boolean isPanel) {
		this.isPanel = isPanel;
	}

	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * @return the suppCriteriarestrictedList
	 */
	public List<ScQtCategory> getSuppCriteriarestrictedList() {
		return suppCriteriarestrictedList;
	}

	/**
	 * @param suppCriteriarestrictedList the suppCriteriarestrictedList to set
	 */
	public void setSuppCriteriarestrictedList(
			List<ScQtCategory> suppCriteriarestrictedList) {
		this.suppCriteriarestrictedList = suppCriteriarestrictedList;
	}

	/**
	 * @return the suppCriteriaopenList
	 */
	public List<ScQtCategory> getSuppCriteriaopenList() {
		return suppCriteriaopenList;
	}

	/**
	 * @param suppCriteriaopenList the suppCriteriaopenList to set
	 */
	public void setSuppCriteriaopenList(List<ScQtCategory> suppCriteriaopenList) {
		this.suppCriteriaopenList = suppCriteriaopenList;
	}

	/**
	 * @return the qtCriteria
	 */
	public ScQtCriteria getQtCriteria() {
		return qtCriteria;
	}

	/**
	 * @param qtCriteria the qtCriteria to set
	 */
	public void setQtCriteria(ScQtCriteria qtCriteria) {
		this.qtCriteria = qtCriteria;
	}

	/**
	 * @return the rootLocationTree
	 */
	public TreeNode getRootLocationTree() {
		return rootLocationTree;
	}

	/**
	 * @param rootLocationTree the rootLocationTree to set
	 */
	public void setRootLocationTree(TreeNode rootLocationTree) {
		this.rootLocationTree = rootLocationTree;
	}

	/**
	 * @return the zoneName
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * @param zoneName the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	/**
	 * @return the zoneTrees
	 */
	public List<TreeNode> getZoneTrees() {
		return zoneTrees;
	}

	/**
	 * @param zoneTrees the zoneTrees to set
	 */
	public void setZoneTrees(List<TreeNode> zoneTrees) {
		this.zoneTrees = zoneTrees;
	}

	/**
	 * @return the zoneTreeList
	 */
	public List<TreeNode> getZoneTreeList() {
		return zoneTreeList;
	}

	/**
	 * @param zoneTreeList the zoneTreeList to set
	 */
	public void setZoneTreeList(List<TreeNode> zoneTreeList) {
		this.zoneTreeList = zoneTreeList;
	}

	/**
	 * @return the zoneTreeAddrList
	 */
	public List<TreeNode> getZoneTreeAddrList() {
		return zoneTreeAddrList;
	}

	/**
	 * @param zoneTreeAddrList the zoneTreeAddrList to set
	 */
	public void setZoneTreeAddrList(List<TreeNode> zoneTreeAddrList) {
		this.zoneTreeAddrList = zoneTreeAddrList;
	}

	/**
	 * @return the zoneName1
	 */
	public String getZoneName1() {
		return zoneName1;
	}

	/**
	 * @param zoneName1 the zoneName1 to set
	 */
	public void setZoneName1(String zoneName1) {
		this.zoneName1 = zoneName1;
	}

	/**
	 * @return the zoneAddrName
	 */
	public String getZoneAddrName() {
		return zoneAddrName;
	}

	/**
	 * @param zoneAddrName the zoneAddrName to set
	 */
	public void setZoneAddrName(String zoneAddrName) {
		this.zoneAddrName = zoneAddrName;
	}

	/**
	 * @return the pmReportSectionList
	 */
	public List<PmReportSection> getPmReportSectionList() {
		return pmReportSectionList;
	}

	/**
	 * @param pmReportSectionList the pmReportSectionList to set
	 */
	public void setPmReportSectionList(List<PmReportSection> pmReportSectionList) {
		this.pmReportSectionList = pmReportSectionList;
	}

	/**
	 * @return the selectedPmReportSectionList
	 */
	public List<PmReportSection> getSelectedPmReportSectionList() {
		return selectedPmReportSectionList;
	}

	/**
	 * @param selectedPmReportSectionList the selectedPmReportSectionList to set
	 */
	public void setSelectedPmReportSectionList(
			List<PmReportSection> selectedPmReportSectionList) {
		this.selectedPmReportSectionList = selectedPmReportSectionList;
	}

	/**
	 * @return the rootLocationTreeList
	 */
	public TreeNode getRootLocationTreeList() {
		return rootLocationTreeList;
	}

	/**
	 * @param rootLocationTreeList the rootLocationTreeList to set
	 */
	public void setRootLocationTreeList(TreeNode rootLocationTreeList) {
		this.rootLocationTreeList = rootLocationTreeList;
	}

	/**
	 * @return the rootAddressLocationTreeList
	 */
	public TreeNode getRootAddressLocationTreeList() {
		return rootAddressLocationTreeList;
	}

	/**
	 * @param rootAddressLocationTreeList the rootAddressLocationTreeList to set
	 */
	public void setRootAddressLocationTreeList(TreeNode rootAddressLocationTreeList) {
		this.rootAddressLocationTreeList = rootAddressLocationTreeList;
	}

	/**
	 * @return the selectedNodes
	 */
	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	/**
	 * @param selectedNodes the selectedNodes to set
	 */
	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	/**
	 * @return the selectedLocNodes
	 */
	public TreeNode[] getSelectedLocNodes() {
		return selectedLocNodes;
	}

	/**
	 * @param selectedLocNodes the selectedLocNodes to set
	 */
	public void setSelectedLocNodes(TreeNode[] selectedLocNodes) {
		this.selectedLocNodes = selectedLocNodes;
	}

	/**
	 * @return the standardTemplate
	 */
	public String getStandardTemplate() {
		return standardTemplate;
	}

	/**
	 * @param standardTemplate the standardTemplate to set
	 */
	public void setStandardTemplate(String standardTemplate) {
		this.standardTemplate = standardTemplate;
	}

	/**
	 * @return the supportingDoc
	 */
	public String getSupportingDoc() {
		return supportingDoc;
	}

	/**
	 * @param supportingDoc the supportingDoc to set
	 */
	public void setSupportingDoc(String supportingDoc) {
		this.supportingDoc = supportingDoc;
	}

	/**
	 * @return the templateType
	 */
	public String getTemplateType() {
		return templateType;
	}

	/**
	 * @param templateType the templateType to set
	 */
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	/**
	 * @return the specificationType
	 */
	public String getSpecificationType() {
		return specificationType;
	}

	/**
	 * @param specificationType the specificationType to set
	 */
	public void setSpecificationType(String specificationType) {
		this.specificationType = specificationType;
	}

	/**
	 * @return the scqtDoc
	 */
	public ScQtDoc getScqtDoc() {
		return scqtDoc;
	}

	/**
	 * @param scqtDoc the scqtDoc to set
	 */
	public void setScqtDoc(ScQtDoc scqtDoc) {
		this.scqtDoc = scqtDoc;
	}

	/**
	 * @return the ministryName
	 */
	public String getMinistryName() {
		return ministryName;
	}

	/**
	 * @param ministryName the ministryName to set
	 */
	public void setMinistryName(String ministryName) {
		this.ministryName = ministryName;
	}

	/**
	 * @return the supportDocList
	 */
	public List<ScQtCheckList> getSupportDocList() {
		return supportDocList;
	}

	/**
	 * @param supportDocList the supportDocList to set
	 */
	public void setSupportDocList(List<ScQtCheckList> supportDocList) {
		this.supportDocList = supportDocList;
	}

	/**
	 * @return the searchItemTypeId
	 */
	public Long getSearchItemTypeId() {
		return searchItemTypeId;
	}

	/**
	 * @param searchItemTypeId the searchItemTypeId to set
	 */
	public void setSearchItemTypeId(Long searchItemTypeId) {
		this.searchItemTypeId = searchItemTypeId;
	}

	/**
	 * @return the searchSpecTypeId
	 */
	public Long getSearchSpecTypeId() {
		return searchSpecTypeId;
	}

	/**
	 * @param searchSpecTypeId the searchSpecTypeId to set
	 */
	public void setSearchSpecTypeId(Long searchSpecTypeId) {
		this.searchSpecTypeId = searchSpecTypeId;
	}

	/**
	 * @return the searchProcurementId
	 */
	public Long getSearchProcurementId() {
		return searchProcurementId;
	}

	/**
	 * @param searchProcurementId the searchProcurementId to set
	 */
	public void setSearchProcurementId(Long searchProcurementId) {
		this.searchProcurementId = searchProcurementId;
	}

	/**
	 * @return the docSearchlist
	 */
	public List<ScQtDoc> getDocSearchlist() {
		return docSearchlist;
	}

	/**
	 * @param docSearchlist the docSearchlist to set
	 */
	public void setDocSearchlist(List<ScQtDoc> docSearchlist) {
		this.docSearchlist = docSearchlist;
	}

	/**
	 * @return the checkListItem
	 */
	public List<ScQtCheckList> getCheckListItem() {
		return checkListItem;
	}

	/**
	 * @param checkListItem the checkListItem to set
	 */
	public void setCheckListItem(List<ScQtCheckList> checkListItem) {
		this.checkListItem = checkListItem;
	}

	/**
	 * @return the zoneList
	 */
	public List<SelectItem> getZoneList() {
		return zoneList;
	}

	/**
	 * @param zoneList the zoneList to set
	 */
	public void setZoneList(List<SelectItem> zoneList) {
		this.zoneList = zoneList;
	}

	/**
	 * @return the specName
	 */
	public String getSpecName() {
		return specName;
	}

	/**
	 * @param specName the specName to set
	 */
	public void setSpecName(String specName) {
		this.specName = specName;
	}

	/**
	 * @return the qtNum
	 */
	public String getQtNum() {
		return qtNum;
	}

	/**
	 * @param qtNum the qtNum to set
	 */
	public void setQtNum(String qtNum) {
		this.qtNum = qtNum;
	}

	/**
	 * @return the statusId
	 */
	public Long getStatusId() {
		return statusId;
	}

	/**
	 * @param statusId the statusId to set
	 */
	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	/**
	 * @return the isRecordStatus
	 */
	public boolean isRecordStatus() {
		return isRecordStatus;
	}

	/**
	 * @param isRecordStatus the isRecordStatus to set
	 */
	public void setRecordStatus(boolean isRecordStatus) {
		this.isRecordStatus = isRecordStatus;
	}

	/**
	 * @return the templateSpecBean
	 */
	public TemplateSpecBean getTemplateSpecBean() {
		return templateSpecBean;
	}

	/**
	 * @param templateSpecBean the templateSpecBean to set
	 */
	public void setTemplateSpecBean(TemplateSpecBean templateSpecBean) {
		this.templateSpecBean = templateSpecBean;
	}

	

	/**
	 * @param circularExpDate the circularExpDate to set
	 */
	public void setCircularExpDate(String circularExpDate) {
		this.circularExpDate = circularExpDate;
	}

	/**
	 * @return the circularType
	 */
	public String getCircularType() {
		return circularType;
	}

	/**
	 * @param circularType the circularType to set
	 */
	public void setCircularType(String circularType) {
		this.circularType = circularType;
	}

	/**
	 * @return the circularExpDate
	 */
	public String getCircularExpDate() {
		return circularExpDate;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the qtCategoryOpenSelectList
	 */
	public List<ScQtCategory> getQtCategoryOpenSelectList() {
		return qtCategoryOpenSelectList;
	}

	/**
	 * @param qtCategoryOpenSelectList the qtCategoryOpenSelectList to set
	 */
	public void setQtCategoryOpenSelectList(
			List<ScQtCategory> qtCategoryOpenSelectList) {
		this.qtCategoryOpenSelectList = qtCategoryOpenSelectList;
	}

	/**
	 * @return the suppCriteriarestrictedSelectList
	 */
	public List<ScQtCategory> getSuppCriteriarestrictedSelectList() {
		return suppCriteriarestrictedSelectList;
	}

	/**
	 * @param suppCriteriarestrictedSelectList the suppCriteriarestrictedSelectList to set
	 */
	public void setSuppCriteriarestrictedSelectList(
			List<ScQtCategory> suppCriteriarestrictedSelectList) {
		this.suppCriteriarestrictedSelectList = suppCriteriarestrictedSelectList;
	}

	/**
	 * @return the totalProgressivePayment
	 */
	public int getTotalProgressivePayment() {
		return totalProgressivePayment;
	}

	/**
	 * @param totalProgressivePayment the totalProgressivePayment to set
	 */
	public void setTotalProgressivePayment(int totalProgressivePayment) {
		this.totalProgressivePayment = totalProgressivePayment;
	}

	/**
	 * @return the qtCategory
	 */
	public ScQtCategory getQtCategory() {
		return qtCategory;
	}

	/**
	 * @param qtCategory the qtCategory to set
	 */
	public void setQtCategory(ScQtCategory qtCategory) {
		this.qtCategory = qtCategory;
	}

	/**
	 * @return the estimatedPrice
	 */
	public BigDecimal getEstimatedPrice() {
		return estimatedPrice;
	}

	/**
	 * @param estimatedPrice the estimatedPrice to set
	 */
	public void setEstimatedPrice(BigDecimal estimatedPrice) {
		this.estimatedPrice = estimatedPrice;
	}

	/**
	 * @return the fromJulat
	 */
	public int getFromJulat() {
		return fromJulat;
	}

	/**
	 * @param fromJulat the fromJulat to set
	 */
	public void setFromJulat(int fromJulat) {
		this.fromJulat = fromJulat;
	}

	/**
	 * @return the toJulat
	 */
	public int getToJulat() {
		return toJulat;
	}

	/**
	 * @param toJulat the toJulat to set
	 */
	public void setToJulat(int toJulat) {
		this.toJulat = toJulat;
	}

	/**
	 * @return the fromPrice
	 */
	public BigDecimal getFromPrice() {
		return fromPrice;
	}

	/**
	 * @param fromPrice the fromPrice to set
	 */
	public void setFromPrice(BigDecimal fromPrice) {
		this.fromPrice = fromPrice;
	}

	/**
	 * @return the toPrice
	 */
	public BigDecimal getToPrice() {
		return toPrice;
	}

	/**
	 * @param toPrice the toPrice to set
	 */
	public void setToPrice(BigDecimal toPrice) {
		this.toPrice = toPrice;
	}

	/**
	 * @return the zonesList
	 */
	public List<TreeNode> getZonesList() {
		return zonesList;
	}

	/**
	 * @param zonesList the zonesList to set
	 */
	public void setZonesList(List<TreeNode> zonesList) {
		this.zonesList = zonesList;
	}

	/**
	 * @return the technicalScoring
	 */
	public List<ScQtCheckList> getTechnicalScoring() {
		return technicalScoring;
	}

	/**
	 * @param technicalScoring the technicalScoring to set
	 */
	public void setTechnicalScoring(List<ScQtCheckList> technicalScoring) {
		this.technicalScoring = technicalScoring;
	}

	/**
	 * @return the financialScoring
	 */
	public List<ScQtCheckList> getFinancialScoring() {
		return financialScoring;
	}

	/**
	 * @param financialScoring the financialScoring to set
	 */
	public void setFinancialScoring(List<ScQtCheckList> financialScoring) {
		this.financialScoring = financialScoring;
	}

	/**
	 * @return the packScoringList
	 */
	public List<ScQtCheckListScore> getPackScoringList() {
		return packScoringList;
	}

	/**
	 * @param packScoringList the packScoringList to set
	 */
	public void setPackScoringList(List<ScQtCheckListScore> packScoringList) {
		this.packScoringList = packScoringList;
	}

	/**
	 * @return the warantyScore
	 */
	public List<ScQtCheckListScore> getWarantyScore() {
		return warantyScore;
	}

	/**
	 * @param warantyScore the warantyScore to set
	 */
	public void setWarantyScore(List<ScQtCheckListScore> warantyScore) {
		this.warantyScore = warantyScore;
	}

	/**
	 * @return the trainingScore
	 */
	public List<ScQtCheckListScore> getTrainingScore() {
		return trainingScore;
	}

	/**
	 * @param trainingScore the trainingScore to set
	 */
	public void setTrainingScore(List<ScQtCheckListScore> trainingScore) {
		this.trainingScore = trainingScore;
	}

	/**
	 * @return the totalEmployeeScore
	 */
	public List<ScQtCheckListScore> getTotalEmployeeScore() {
		return totalEmployeeScore;
	}

	/**
	 * @param totalEmployeeScore the totalEmployeeScore to set
	 */
	public void setTotalEmployeeScore(List<ScQtCheckListScore> totalEmployeeScore) {
		this.totalEmployeeScore = totalEmployeeScore;
	}

	/**
	 * @return the technicalStaffScore
	 */
	public List<ScQtCheckListScore> getTechnicalStaffScore() {
		return technicalStaffScore;
	}

	/**
	 * @param technicalStaffScore the technicalStaffScore to set
	 */
	public void setTechnicalStaffScore(List<ScQtCheckListScore> technicalStaffScore) {
		this.technicalStaffScore = technicalStaffScore;
	}

	/**
	 * @return the paidUpCapitalScore
	 */
	public List<ScQtCheckListScore> getPaidUpCapitalScore() {
		return paidUpCapitalScore;
	}

	/**
	 * @param paidUpCapitalScore the paidUpCapitalScore to set
	 */
	public void setPaidUpCapitalScore(List<ScQtCheckListScore> paidUpCapitalScore) {
		this.paidUpCapitalScore = paidUpCapitalScore;
	}

	/**
	 * @return the nonFederalScore
	 */
	public List<ScQtCheckListScore> getNonFederalScore() {
		return nonFederalScore;
	}

	/**
	 * @param nonFederalScore the nonFederalScore to set
	 */
	public void setNonFederalScore(List<ScQtCheckListScore> nonFederalScore) {
		this.nonFederalScore = nonFederalScore;
	}

	/**
	 * @return the nonFederalScoreyears
	 */
	public List<ScQtCheckListScore> getNonFederalScoreyears() {
		return nonFederalScoreyears;
	}

	/**
	 * @param nonFederalScoreyears the nonFederalScoreyears to set
	 */
	public void setNonFederalScoreyears(
			List<ScQtCheckListScore> nonFederalScoreyears) {
		this.nonFederalScoreyears = nonFederalScoreyears;
	}

	/**
	 * @return the nonFederalContractScore
	 */
	public List<ScQtCheckListScore> getNonFederalContractScore() {
		return nonFederalContractScore;
	}

	/**
	 * @param nonFederalContractScore the nonFederalContractScore to set
	 */
	public void setNonFederalContractScore(
			List<ScQtCheckListScore> nonFederalContractScore) {
		this.nonFederalContractScore = nonFederalContractScore;
	}

	/**
	 * @return the ctZoneSelectList
	 */
	public List<CtCircularZone> getCtZoneSelectList() {
		return ctZoneSelectList;
	}

	/**
	 * @param ctZoneSelectList the ctZoneSelectList to set
	 */
	public void setCtZoneSelectList(List<CtCircularZone> ctZoneSelectList) {
		this.ctZoneSelectList = ctZoneSelectList;
	}

	/**
	 * @return the ctZoneList
	 */
	public List<CtCircularZone> getCtZoneList() {
		return ctZoneList;
	}

	/**
	 * @param ctZoneList the ctZoneList to set
	 */
	public void setCtZoneList(List<CtCircularZone> ctZoneList) {
		this.ctZoneList = ctZoneList;
	}

	/**
	 * @return the ctZoneSupplierList
	 */
	public List<CtPanelSupplier> getCtZoneSupplierList() {
		return ctZoneSupplierList;
	}

	/**
	 * @param ctZoneSupplierList the ctZoneSupplierList to set
	 */
	public void setCtZoneSupplierList(List<CtPanelSupplier> ctZoneSupplierList) {
		this.ctZoneSupplierList = ctZoneSupplierList;
	}

	/**
	 * @return the totalSuppliers
	 */
	public int getTotalSuppliers() {
		return totalSuppliers;
	}

	/**
	 * @param totalSuppliers the totalSuppliers to set
	 */
	public void setTotalSuppliers(int totalSuppliers) {
		this.totalSuppliers = totalSuppliers;
	}

	

	

	/**
	 * @return the ctZoneTrees
	 */
	public TreeNode getCtZoneTrees() {
		return ctZoneTrees;
	}

	/**
	 * @param ctZoneTrees the ctZoneTrees to set
	 */
	public void setCtZoneTrees(TreeNode ctZoneTrees) {
		this.ctZoneTrees = ctZoneTrees;
	}

	/**
	 * @return the ctZoneTreeSelectedNodes
	 */
	public TreeNode[] getCtZoneTreeSelectedNodes() {
		return ctZoneTreeSelectedNodes;
	}

	/**
	 * @param ctZoneTreeSelectedNodes the ctZoneTreeSelectedNodes to set
	 */
	public void setCtZoneTreeSelectedNodes(TreeNode[] ctZoneTreeSelectedNodes) {
		this.ctZoneTreeSelectedNodes = ctZoneTreeSelectedNodes;
	}

	/**
	 * @return the ctZoneSuppliers
	 */
	public TreeNode getCtZoneSuppliers() {
		return ctZoneSuppliers;
	}

	/**
	 * @param ctZoneSuppliers the ctZoneSuppliers to set
	 */
	public void setCtZoneSuppliers(TreeNode ctZoneSuppliers) {
		this.ctZoneSuppliers = ctZoneSuppliers;
	}

	/**
	 * @return the yearList
	 */
	public List<SelectItem> getYearList() {
		return yearList;
	}

	/**
	 * @param yearList the yearList to set
	 */
	public void setYearList(List<SelectItem> yearList) {
		this.yearList = yearList;
	}

	/**
	 * @return the scQt
	 */
	public ScQt getScQt() {
		return scQt;
	}

	/**
	 * @param scQt the scQt to set
	 */
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}

	/**
	 * @return the techPoints
	 */
	public int getTechPoints() {
		return techPoints;
	}

	/**
	 * @param techPoints the techPoints to set
	 */
	public void setTechPoints(int techPoints) {
		this.techPoints = techPoints;
	}

	/**
	 * @return the finPoints
	 */
	public int getFinPoints() {
		return finPoints;
	}

	/**
	 * @param finPoints the finPoints to set
	 */
	public void setFinPoints(int finPoints) {
		this.finPoints = finPoints;
	}

	/**
	 * @return the technicalSpecList
	 */
	public List<ScQtCheckList> getTechnicalSpecList() {
		return technicalSpecList;
	}

	/**
	 * @param technicalSpecList the technicalSpecList to set
	 */
	public void setTechnicalSpecList(List<ScQtCheckList> technicalSpecList) {
		this.technicalSpecList = technicalSpecList;
	}

	
	



	


	

	

	

	


	
	

}
