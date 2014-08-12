/**
 * 
 */
package my.ep.web.managedbean.sc.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmReportSection;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeliveryLoc;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreOfferPrice;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreSetting;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreWeightage;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.web.backingbean.sc.specification.BankingBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.CtCircularSearchBean;
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

	private List<String> compilanceList = new ArrayList<String>();
	// public List<SpecificationCompilanceBean> compilaenceList;
	private ScRemark scRemark;
	private List<SelectItem> comitteeUsers;
	private List<SelectItem> currencyListDetails;
	private List<SelectItem> procurementListDetails;
	private List<ScCommitteeMember> committeeMemberList;

	private Object specificationDataType;

	public boolean isSampleReq = true;
	public String approvalDate;

	private boolean isSpecStatus;
	private Long procurementId;
	private List<SelectItem> approvalList;
	private String evaluationType;
	private ScQt scQt;

	private List<PmDocument> stageTwoTechComplianceList;
	private List<PmDocument> stageTwoTechComplianceSelect;
	private List<PmDocument> stageTwoFinComplianceList;
	private List<PmDocument> stageTwoFinComplianceSelect;
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
	private String startYear;
	private String endYear;
	// B,S
	private String businessOwner = "B";
	private List<BankingBean> bankList;
	private List<ScQtCheckListScore> checkListScore = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> scoringForYears = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> contractAmount = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> bankStamentScore = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> creditFacility = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> scoringCash = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> assetScore = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> warantyScore = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> trainingScore = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> totalEmployeeScore = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> technicalStaffScore = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> paidUpCapitalScore = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> nonFederalScore = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> nonFederalScoreyears = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> nonFederalContractScore = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> itemScore = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> checkListSelectScore = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> scoringForSelectYears = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> nonFederalSelectScore = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> nonFederalSelectScoreyears = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> warantySelectScore = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> trainingSelectList = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> technicalStaffSelectList = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> totalEmployeeSelectList = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> finGovContractSelectList = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> finPrivateContractSelectList = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> creditfacilitySelectList = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> cashSelectList = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> bankSelectList = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> assetLiabilitySelectList = new ArrayList<ScQtCheckListScore>();
	private List<ScQtCheckListScore> paidUpSelectList = new ArrayList<ScQtCheckListScore>();

	private List<ScQtSchedule> scheduleList;
	private List<ScQtProgressivePayment> progressivePaymentList;

	private int totalFrequency;
	private int totalProgressivePayment;
	private String technicalWeightage = "2";
	private String financialWeightage = "2";
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
	// private List<CtCircularZone> ctZoneList;
	// private List<CtCircularZone> ctZoneSelectList;
	private TreeNode ctZoneTrees;
	private TreeNode[] ctZoneTreeSelectedNodes;
	private List<TreeNode> ctZoneSuppliers = new ArrayList<TreeNode>();

	private List<CtPanelSupplier> ctZoneSupplierList;

	// supplier Qualification Criteria List and variables
	private List<ScQtCategory> suppCriteriarestrictedList;
	private List<ScQtCategory> suppCriteriaopenList;
	private List<ScQtCategory> suppCriteriaopenSelectList;
	private List<ScQtCategory> suppCriteriarestrictedBoSelectList;

	private TreeNode rootLocationTree;
	private TreeNode rootZoanlTree;
	private String zoneName;
	private CtPanelZone panelZoneSelect;

	private List<TreeNode> zoneTrees = new ArrayList<TreeNode>();
	private List<TreeNode> zoneGeneralTrees = new ArrayList<TreeNode>();
	private List<TreeNode> zonesList = new ArrayList<TreeNode>();
	private List<TreeNode> zoneTreeList = new ArrayList<TreeNode>();
	private List<TreeNode> zoneTreeAddrList = new ArrayList<TreeNode>();
	private String zoneName1;
	private String zoneAddrName;
	private List<PmReportSection> pmReportSectionList;
	private List<PmReportSection> selectedPmReportSectionList;
	private TreeNode rootLocationTreeList;
	private TreeNode rootAddressLocationTreeList;
	private TreeNode[] selectedNodes;
	private TreeNode[] selectedGeneralTreeNodes;
	private TreeNode[] selectedLocNodes;
	private Date circularExpDate;
	private String circularType;

	// template Specification variables
	private String standardTemplate = "ST";
	private String supportingDoc;
	private String templateType;
	private String specificationType;
	private ScQtDoc scqtDoc;
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
	private int index = -1;
	private List<ScQtCategory> qtCategoryOpenSelectList;
	private List<ScQtCategory> suppCriteriarestrictedSelectList;

	private BigDecimal estimatedPrice = new BigDecimal("50000");
	private int fromJulat;
	private int toJulat;
	private BigDecimal fromPrice;
	private BigDecimal toPrice;

	private ScQtCategory qtCategory = new ScQtCategory();

	// technical Scoring Financial Scoring lists
	private List<ScQtCheckList> technicalScoring;
	private List<ScQtCheckList> financialScoring;
	private List<ScQtCheckListScore> packScoringList;
	private int techPoints;
	private int finPoints;
	private List<ScQtCheckList> technicalSpecList;
	private List<ScQtCheckList> financialSpecList;

	private List<SelectItem> yearList;
	private List<SelectItem> yearsDisplayList;

	private int totalSuppliers;

	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();

	private List<BriefingZoneBean> deliveryLocBean;
	public List<ScQtDeliveryLoc> deliveryLoc;
	public List<ScQtDeliveryLoc> deliveryNonZoneList;
	public List<ScQtDeliveryLoc> deleteDeliveryList;
	public List<ScQtDeliveryLoc> selectDeliveryList;

	public ScQtSupplierCriteria supplierCriteria;

	public String deliveyzoneName;
	private int technicalChecklistScoring;
	private String checklistScoreType = "1";
	private String companyScoreType = "1";
	private ScQtCheckListScore scQtCheckListScore;
	private ScQtCheckListScore scQtContractScore;

	private List<SelectItem> technicalChecklistAction;

	private BigDecimal scoreVarience;
	private ScQtScoreOfferPrice offerPricePackage;
	private ScQtScoreOfferPrice OfferPriceItem;
	private ScQtScoreWeightage scQtScoreWeightage;
	private ScQtScoreSetting technicalScoreSetting;
	private ScQtScoreSetting financialScoreSetting;
	private ScQtScoreSetting demoScoreSetting;
	private ScQtScoreSetting recommendationPackageScore;
	private ScQtScoreSetting recommendationScore;
	private String procuremntCategory;
	private String fulfilmentType;

	private BigDecimal technicalpackage;
	private BigDecimal financialpackage;
	private BigDecimal recommendationPackageTotal;
	private BigDecimal recommendTechnicalScore;
	private BigDecimal recommendfinancialScore;
	private int tabActiveIndex;
	private List<ScQtRemark> scQtRemarksList;
	private List<PmState> stateList;
	private List<PmDistrict> districtList;
	private Long stateId;
	private Long districtId;
	private Long ministryId;
	private Long departmentId;
	private Long ptjId;
	private String stateCode;
	private String districtCode;

	private List<PmOrgProfile> ministryList;
	private List<PmOrgProfile> departmentList;
	private List<PmOrgProfile> ptjtList;
	private List<PmAddressType> deliveryAddressSelect;
	private List<TplSpecZoneDtl> deliveryZones;
	private TplSpecZoneDtl tplSpecZoneDtl;;
	private List<BriefingZoneBean> addressZoneBean;
	private String panelItemCategory;

	private List<ScQtCheckList> finSpecList;
	private Long panelItemId;
	private int activeIndex;
	private CtPanelItem ctPanelItem;
	private boolean required = Boolean.FALSE;
	private List<CtPanelZone> ctPanelZones;
	private CtPanelSupplier ctpanelSupplier;
	private ScQtRemark contractremark;
	private CtCircularVer circularVer;
	private String subject;
	private String message;
	private String chairPersonPinNum;
	

	// Added By Sagar.. for Delete funcationality..
	private TreeNode selectedTreeNode;
	private String taskName;
	private CtCircularSearchBean ctSearchBean = new CtCircularSearchBean();
	private List<SelectItem> evaluationTypeItem;
	private TreeNode supplierSelectedTreeNode;
	private SmPersonnel personnel;
	private String supplierBranch;
	private SmAddress supplierAddress;
	private String supplierContact;
	private String supplierEmail;
	private CtCircularVer ctCircularVer;
	private List<ScQtCheckList>  finspecChecklist;
	private String buttonOutCome;
	private int allStateCode;
	private boolean isPanelChange;
	private String newItemCategory;
	private CtPanelItem existingpanelitem;
	private String bankstmntTomonth;
	private int tempIndex;

	public SpecificationManagedBean() {
		
	}

	/**
	 * @return the scQtRemarks
	 */
	public ScQtRemark getScQtRemarks() {
		return scQtRemarks;
	}

	/**
	 * @param scQtRemarks
	 *            the scQtRemarks to set
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
	 * @param scQtChecklist
	 *            the scQtChecklist to set
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
	 * @param scQtCheckSt1techProposal
	 *            the scQtCheckSt1techProposal to set
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
	 * @param scQtDeduction
	 *            the scQtDeduction to set
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
	 * @param scQtDeductionList
	 *            the scQtDeductionList to set
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
	 * @param scQtCheckSt1techAdditionalProposalList
	 *            the scQtCheckSt1techAdditionalProposalList to set
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
	 * @param scQtCheckSt1financeProposalList
	 *            the scQtCheckSt1financeProposalList to set
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
	 * @param scQtCheckSt1financeAdditionalProposalList
	 *            the scQtCheckSt1financeAdditionalProposalList to set
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
	 * @param scQtCheckSt2techProposal
	 *            the scQtCheckSt2techProposal to set
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
	 * @param scQtCheckSt2techAdditionalProposalList
	 *            the scQtCheckSt2techAdditionalProposalList to set
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
	 * @param scQtCheckSt2financeProposalList
	 *            the scQtCheckSt2financeProposalList to set
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
	 * @param scQtCheckSt2financeAdditionalProposalList
	 *            the scQtCheckSt2financeAdditionalProposalList to set
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
	 * @param compilanceList
	 *            the compilanceList to set
	 */
	public void setCompilanceList(List<String> compilanceList) {
		this.compilanceList = compilanceList;
	}

	/**
	 * @return the scRemark
	 */
	public ScRemark getScRemark() {
		return scRemark;
	}

	/**
	 * @param scRemark
	 *            the scRemark to set
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
	 * @param isSampleReq
	 *            the isSampleReq to set
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
	 * @return the currencyListDetails
	 */
	public List<SelectItem> getCurrencyListDetails() {
		return currencyListDetails;
	}

	/**
	 * @param currencyListDetails
	 *            the currencyListDetails to set
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
	 * @param procurementListDetails
	 *            the procurementListDetails to set
	 */
	public void setProcurementListDetails(
			List<SelectItem> procurementListDetails) {
		this.procurementListDetails = procurementListDetails;
	}

	/**
	 * @return the committeeMemberList
	 */
	public List<ScCommitteeMember> getCommitteeMemberList() {
		return committeeMemberList;
	}

	/**
	 * @param committeeMemberList
	 *            the committeeMemberList to set
	 */
	public void setCommitteeMemberList(
			List<ScCommitteeMember> committeeMemberList) {
		this.committeeMemberList = committeeMemberList;
	}

	public Object getSpecificationDataType() {
		return specificationDataType;
	}

	public void setSpecificationDataType(Object specificationDataType) {
		this.specificationDataType = specificationDataType;
	}

	/**
	 * @return the isSpecStatus
	 */
	public boolean isSpecStatus() {
		return isSpecStatus;
	}

	/**
	 * @param isSpecStatus
	 *            the isSpecStatus to set
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
	 * @param procurementId
	 *            the procurementId to set
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
	 * @param approvalList
	 *            the approvalList to set
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
	 * @param evaluationType
	 *            the evaluationType to set
	 */
	public void setEvaluationType(String evaluationType) {
		this.evaluationType = evaluationType;
	}

	/**
	 * @return the s1TechSelect
	 */
	public List<ScQtCheckList> getS1TechSelect() {
		return s1TechSelect;
	}

	/**
	 * @param s1TechSelect
	 *            the s1TechSelect to set
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
	 * @param s1TechAddSelect
	 *            the s1TechAddSelect to set
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
	 * @param s1FinSelect
	 *            the s1FinSelect to set
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
	 * @param s1FinAddSelect
	 *            the s1FinAddSelect to set
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
	 * @param s2TechSelect
	 *            the s2TechSelect to set
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
	 * @param s2TechAddSelect
	 *            the s2TechAddSelect to set
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
	 * @param s2FinSelect
	 *            the s2FinSelect to set
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
	 * @param s2FinAddSelect
	 *            the s2FinAddSelect to set
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
	 * @param scoringList
	 *            the scoringList to set
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
	 * @param selectscoring
	 *            the selectscoring to set
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
	 * @param collectionScoring
	 *            the collectionScoring to set
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
	 * @param scheduleType
	 *            the scheduleType to set
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
	 * @param businessOwner
	 *            the businessOwner to set
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
	 * @param fromMonth
	 *            the fromMonth to set
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
	 * @param toMonth
	 *            the toMonth to set
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
	 * @param bankList
	 *            the bankList to set
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
	 * @param checkListScore
	 *            the checkListScore to set
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
	 * @param scoringForYears
	 *            the scoringForYears to set
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
	 * @param contractAmount
	 *            the contractAmount to set
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
	 * @param bankStamentScore
	 *            the bankStamentScore to set
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
	 * @param creditFacility
	 *            the creditFacility to set
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
	 * @param scoringCash
	 *            the scoringCash to set
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
	 * @param assetScore
	 *            the assetScore to set
	 */
	public void setAssetScore(List<ScQtCheckListScore> assetScore) {
		this.assetScore = assetScore;
	}

	/**
	 * @return the totalFrequency
	 */
	public int getTotalFrequency() {
		return totalFrequency;
	}

	/**
	 * @param totalFrequency
	 *            the totalFrequency to set
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
	 * @param financialWeightage
	 *            the financialWeightage to set
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
	 * @param technicalWeightage
	 *            the technicalWeightage to set
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
	 * @param cirCularNo
	 *            the cirCularNo to set
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
	 * @param circularName
	 *            the circularName to set
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
	 * @param panelItemList
	 *            the panelItemList to set
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
	 * @param selectpanelItem
	 *            the selectpanelItem to set
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
	 * @param panelItemDetailsList
	 *            the panelItemDetailsList to set
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
	 * @param selectItemDetailsList
	 *            the selectItemDetailsList to set
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
	 * @param technicalWeightageScore
	 *            the technicalWeightageScore to set
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
	 * @param totalAmount
	 *            the totalAmount to set
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
	 * @param totalAmountAvg
	 *            the totalAmountAvg to set
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
	 * @param financialWeightageScore
	 *            the financialWeightageScore to set
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
	 * @param isPanel
	 *            the isPanel to set
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
	 * @param itemName
	 *            the itemName to set
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
	 * @param suppCriteriarestrictedList
	 *            the suppCriteriarestrictedList to set
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
	 * @param suppCriteriaopenList
	 *            the suppCriteriaopenList to set
	 */
	public void setSuppCriteriaopenList(List<ScQtCategory> suppCriteriaopenList) {
		this.suppCriteriaopenList = suppCriteriaopenList;
	}

	/**
	 * @return the rootLocationTree
	 */
	public TreeNode getRootLocationTree() {
		return rootLocationTree;
	}

	/**
	 * @param rootLocationTree
	 *            the rootLocationTree to set
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
	 * @param zoneName
	 *            the zoneName to set
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
	 * @param zoneTrees
	 *            the zoneTrees to set
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
	 * @param zoneTreeList
	 *            the zoneTreeList to set
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
	 * @param zoneTreeAddrList
	 *            the zoneTreeAddrList to set
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
	 * @param zoneName1
	 *            the zoneName1 to set
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
	 * @param zoneAddrName
	 *            the zoneAddrName to set
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
	 * @param pmReportSectionList
	 *            the pmReportSectionList to set
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
	 * @param selectedPmReportSectionList
	 *            the selectedPmReportSectionList to set
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
	 * @param rootLocationTreeList
	 *            the rootLocationTreeList to set
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
	 * @param rootAddressLocationTreeList
	 *            the rootAddressLocationTreeList to set
	 */
	public void setRootAddressLocationTreeList(
			TreeNode rootAddressLocationTreeList) {
		this.rootAddressLocationTreeList = rootAddressLocationTreeList;
	}

	/**
	 * @return the selectedNodes
	 */
	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	/**
	 * @param selectedNodes
	 *            the selectedNodes to set
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
	 * @param selectedLocNodes
	 *            the selectedLocNodes to set
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
	 * @param standardTemplate
	 *            the standardTemplate to set
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
	 * @param supportingDoc
	 *            the supportingDoc to set
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
	 * @param templateType
	 *            the templateType to set
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
	 * @param specificationType
	 *            the specificationType to set
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
	 * @param scqtDoc
	 *            the scqtDoc to set
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
	 * @param ministryName
	 *            the ministryName to set
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
	 * @param supportDocList
	 *            the supportDocList to set
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
	 * @param searchItemTypeId
	 *            the searchItemTypeId to set
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
	 * @param searchSpecTypeId
	 *            the searchSpecTypeId to set
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
	 * @param searchProcurementId
	 *            the searchProcurementId to set
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
	 * @param docSearchlist
	 *            the docSearchlist to set
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
	 * @param checkListItem
	 *            the checkListItem to set
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
	 * @param zoneList
	 *            the zoneList to set
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
	 * @param specName
	 *            the specName to set
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
	 * @param qtNum
	 *            the qtNum to set
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
	 * @param statusId
	 *            the statusId to set
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
	 * @param isRecordStatus
	 *            the isRecordStatus to set
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
	 * @param templateSpecBean
	 *            the templateSpecBean to set
	 */
	public void setTemplateSpecBean(TemplateSpecBean templateSpecBean) {
		this.templateSpecBean = templateSpecBean;
	}

	/**
	 * @return the circularType
	 */
	public String getCircularType() {
		return circularType;
	}

	/**
	 * @param circularType
	 *            the circularType to set
	 */
	public void setCircularType(String circularType) {
		this.circularType = circularType;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
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
	 * @param qtCategoryOpenSelectList
	 *            the qtCategoryOpenSelectList to set
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
	 * @param suppCriteriarestrictedSelectList
	 *            the suppCriteriarestrictedSelectList to set
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
	 * @param totalProgressivePayment
	 *            the totalProgressivePayment to set
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
	 * @param qtCategory
	 *            the qtCategory to set
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
	 * @param estimatedPrice
	 *            the estimatedPrice to set
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
	 * @param fromJulat
	 *            the fromJulat to set
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
	 * @param toJulat
	 *            the toJulat to set
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
	 * @param fromPrice
	 *            the fromPrice to set
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
	 * @param toPrice
	 *            the toPrice to set
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
	 * @param zonesList
	 *            the zonesList to set
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
	 * @param technicalScoring
	 *            the technicalScoring to set
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
	 * @param financialScoring
	 *            the financialScoring to set
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
	 * @param packScoringList
	 *            the packScoringList to set
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
	 * @param warantyScore
	 *            the warantyScore to set
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
	 * @param trainingScore
	 *            the trainingScore to set
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
	 * @param totalEmployeeScore
	 *            the totalEmployeeScore to set
	 */
	public void setTotalEmployeeScore(
			List<ScQtCheckListScore> totalEmployeeScore) {
		this.totalEmployeeScore = totalEmployeeScore;
	}

	/**
	 * @return the technicalStaffScore
	 */
	public List<ScQtCheckListScore> getTechnicalStaffScore() {
		return technicalStaffScore;
	}

	/**
	 * @param technicalStaffScore
	 *            the technicalStaffScore to set
	 */
	public void setTechnicalStaffScore(
			List<ScQtCheckListScore> technicalStaffScore) {
		this.technicalStaffScore = technicalStaffScore;
	}

	/**
	 * @return the paidUpCapitalScore
	 */
	public List<ScQtCheckListScore> getPaidUpCapitalScore() {
		return paidUpCapitalScore;
	}

	/**
	 * @param paidUpCapitalScore
	 *            the paidUpCapitalScore to set
	 */
	public void setPaidUpCapitalScore(
			List<ScQtCheckListScore> paidUpCapitalScore) {
		this.paidUpCapitalScore = paidUpCapitalScore;
	}

	/**
	 * @return the nonFederalScore
	 */
	public List<ScQtCheckListScore> getNonFederalScore() {
		return nonFederalScore;
	}

	/**
	 * @param nonFederalScore
	 *            the nonFederalScore to set
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
	 * @param nonFederalScoreyears
	 *            the nonFederalScoreyears to set
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
	 * @param nonFederalContractScore
	 *            the nonFederalContractScore to set
	 */
	public void setNonFederalContractScore(
			List<ScQtCheckListScore> nonFederalContractScore) {
		this.nonFederalContractScore = nonFederalContractScore;
	}

	/**
	 * @return the ctZoneSupplierList
	 */
	public List<CtPanelSupplier> getCtZoneSupplierList() {
		return ctZoneSupplierList;
	}

	/**
	 * @param ctZoneSupplierList
	 *            the ctZoneSupplierList to set
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
	 * @param totalSuppliers
	 *            the totalSuppliers to set
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
	 * @param ctZoneTrees
	 *            the ctZoneTrees to set
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
	 * @param ctZoneTreeSelectedNodes
	 *            the ctZoneTreeSelectedNodes to set
	 */
	public void setCtZoneTreeSelectedNodes(TreeNode[] ctZoneTreeSelectedNodes) {
		this.ctZoneTreeSelectedNodes = ctZoneTreeSelectedNodes;
	}

	/**
	 * @return the yearList
	 */
	public List<SelectItem> getYearList() {
		return yearList;
	}

	/**
	 * @param yearList
	 *            the yearList to set
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
	 * @param scQt
	 *            the scQt to set
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
	 * @param techPoints
	 *            the techPoints to set
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
	 * @param finPoints
	 *            the finPoints to set
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
	 * @param technicalSpecList
	 *            the technicalSpecList to set
	 */
	public void setTechnicalSpecList(List<ScQtCheckList> technicalSpecList) {
		this.technicalSpecList = technicalSpecList;
	}

	/**
	 * @return the itemScore
	 */
	public List<ScQtCheckListScore> getItemScore() {
		return itemScore;
	}

	/**
	 * @param itemScore
	 *            the itemScore to set
	 */
	public void setItemScore(List<ScQtCheckListScore> itemScore) {
		this.itemScore = itemScore;
	}

	/**
	 * @return the financialSpecList
	 */
	public List<ScQtCheckList> getFinancialSpecList() {
		return financialSpecList;
	}

	/**
	 * @param financialSpecList
	 *            the financialSpecList to set
	 */
	public void setFinancialSpecList(List<ScQtCheckList> financialSpecList) {
		this.financialSpecList = financialSpecList;
	}

	/**
	 * @return the zoneGeneralTrees
	 */
	public List<TreeNode> getZoneGeneralTrees() {
		return zoneGeneralTrees;
	}

	/**
	 * @param zoneGeneralTrees
	 *            the zoneGeneralTrees to set
	 */
	public void setZoneGeneralTrees(List<TreeNode> zoneGeneralTrees) {
		this.zoneGeneralTrees = zoneGeneralTrees;
	}

	/**
	 * @return the selectedGeneralTreeNodes
	 */
	public TreeNode[] getSelectedGeneralTreeNodes() {
		return selectedGeneralTreeNodes;
	}

	/**
	 * @param selectedGeneralTreeNodes
	 *            the selectedGeneralTreeNodes to set
	 */
	public void setSelectedGeneralTreeNodes(TreeNode[] selectedGeneralTreeNodes) {
		this.selectedGeneralTreeNodes = selectedGeneralTreeNodes;
	}

	/**
	 * @return the deliveryLocBean
	 */
	public List<BriefingZoneBean> getDeliveryLocBean() {
		return deliveryLocBean;
	}

	/**
	 * @param deliveryLocBean
	 *            the deliveryLocBean to set
	 */
	public void setDeliveryLocBean(List<BriefingZoneBean> deliveryLocBean) {
		this.deliveryLocBean = deliveryLocBean;
	}

	/**
	 * @return the deliveryLoc
	 */
	public List<ScQtDeliveryLoc> getDeliveryLoc() {
		return deliveryLoc;
	}

	/**
	 * @param deliveryLoc
	 *            the deliveryLoc to set
	 */
	public void setDeliveryLoc(List<ScQtDeliveryLoc> deliveryLoc) {
		this.deliveryLoc = deliveryLoc;
	}

	/**
	 * @return the deliveyzoneName
	 */
	public String getDeliveyzoneName() {
		return deliveyzoneName;
	}

	/**
	 * @param deliveyzoneName
	 *            the deliveyzoneName to set
	 */
	public void setDeliveyzoneName(String deliveyzoneName) {
		this.deliveyzoneName = deliveyzoneName;
	}

	/**
	 * @return the deliveryNonZoneList
	 */
	public List<ScQtDeliveryLoc> getDeliveryNonZoneList() {
		return deliveryNonZoneList;
	}

	/**
	 * @param deliveryNonZoneList
	 *            the deliveryNonZoneList to set
	 */
	public void setDeliveryNonZoneList(List<ScQtDeliveryLoc> deliveryNonZoneList) {
		this.deliveryNonZoneList = deliveryNonZoneList;
	}

	/**
	 * @return the deleteDeliveryList
	 */
	public List<ScQtDeliveryLoc> getDeleteDeliveryList() {
		return deleteDeliveryList;
	}

	/**
	 * @param deleteDeliveryList
	 *            the deleteDeliveryList to set
	 */
	public void setDeleteDeliveryList(List<ScQtDeliveryLoc> deleteDeliveryList) {
		this.deleteDeliveryList = deleteDeliveryList;
	}

	/**
	 * @return the selectDeliveryList
	 */
	public List<ScQtDeliveryLoc> getSelectDeliveryList() {
		return selectDeliveryList;
	}

	/**
	 * @param selectDeliveryList
	 *            the selectDeliveryList to set
	 */
	public void setSelectDeliveryList(List<ScQtDeliveryLoc> selectDeliveryList) {
		this.selectDeliveryList = selectDeliveryList;
	}

	/**
	 * @return the supplierCriteria
	 */
	public ScQtSupplierCriteria getSupplierCriteria() {
		return supplierCriteria;
	}

	/**
	 * @param supplierCriteria
	 *            the supplierCriteria to set
	 */
	public void setSupplierCriteria(ScQtSupplierCriteria supplierCriteria) {
		this.supplierCriteria = supplierCriteria;
	}

	/**
	 * @return the technicalChecklistScoring
	 */
	public int getTechnicalChecklistScoring() {
		return technicalChecklistScoring;
	}

	/**
	 * @param technicalChecklistScoring
	 *            the technicalChecklistScoring to set
	 */
	public void setTechnicalChecklistScoring(int technicalChecklistScoring) {
		this.technicalChecklistScoring = technicalChecklistScoring;
	}

	/**
	 * @return the checklistScoreType
	 */
	public String getChecklistScoreType() {
		return checklistScoreType;
	}

	/**
	 * @param checklistScoreType
	 *            the checklistScoreType to set
	 */
	public void setChecklistScoreType(String checklistScoreType) {
		this.checklistScoreType = checklistScoreType;
	}

	/**
	 * @return the scQtCheckListScore
	 */
	public ScQtCheckListScore getScQtCheckListScore() {
		return scQtCheckListScore;
	}

	/**
	 * @param scQtCheckListScore
	 *            the scQtCheckListScore to set
	 */
	public void setScQtCheckListScore(ScQtCheckListScore scQtCheckListScore) {
		this.scQtCheckListScore = scQtCheckListScore;
	}

	/**
	 * @return the companyScoreType
	 */
	public String getCompanyScoreType() {
		return companyScoreType;
	}

	/**
	 * @param companyScoreType
	 *            the companyScoreType to set
	 */
	public void setCompanyScoreType(String companyScoreType) {
		this.companyScoreType = companyScoreType;
	}

	/**
	 * @return the technicalChecklistAction
	 */
	public List<SelectItem> getTechnicalChecklistAction() {
		return technicalChecklistAction;
	}

	/**
	 * @param technicalChecklistAction
	 *            the technicalChecklistAction to set
	 */
	public void setTechnicalChecklistAction(
			List<SelectItem> technicalChecklistAction) {
		this.technicalChecklistAction = technicalChecklistAction;
	}

	/**
	 * @return the scoreVarience
	 */
	public BigDecimal getScoreVarience() {
		return scoreVarience;
	}

	/**
	 * @param scoreVarience
	 *            the scoreVarience to set
	 */
	public void setScoreVarience(BigDecimal scoreVarience) {
		this.scoreVarience = scoreVarience;
	}

	/**
	 * @return the scQtScoreWeightage
	 */
	public ScQtScoreWeightage getScQtScoreWeightage() {
		return scQtScoreWeightage;
	}

	/**
	 * @param scQtScoreWeightage
	 *            the scQtScoreWeightage to set
	 */
	public void setScQtScoreWeightage(ScQtScoreWeightage scQtScoreWeightage) {
		this.scQtScoreWeightage = scQtScoreWeightage;
	}

	/**
	 * @return the scheduleList
	 */
	public List<ScQtSchedule> getScheduleList() {
		return scheduleList;
	}

	/**
	 * @param scheduleList
	 *            the scheduleList to set
	 */
	public void setScheduleList(List<ScQtSchedule> scheduleList) {
		this.scheduleList = scheduleList;
	}

	/**
	 * @return the scQtContractScore
	 */
	public ScQtCheckListScore getScQtContractScore() {
		return scQtContractScore;
	}

	/**
	 * @param scQtContractScore
	 *            the scQtContractScore to set
	 */
	public void setScQtContractScore(ScQtCheckListScore scQtContractScore) {
		this.scQtContractScore = scQtContractScore;
	}

	/**
	 * @return the procuremntCategory
	 */
	public String getProcuremntCategory() {
		return procuremntCategory;
	}

	/**
	 * @param procuremntCategory
	 *            the procuremntCategory to set
	 */
	public void setProcuremntCategory(String procuremntCategory) {
		this.procuremntCategory = procuremntCategory;
	}

	/**
	 * @return the fulfilmentType
	 */
	public String getFulfilmentType() {
		return fulfilmentType;
	}

	/**
	 * @param fulfilmentType
	 *            the fulfilmentType to set
	 */
	public void setFulfilmentType(String fulfilmentType) {
		this.fulfilmentType = fulfilmentType;
	}

	/**
	 * @return the technicalScoreSetting
	 */
	public ScQtScoreSetting getTechnicalScoreSetting() {
		return technicalScoreSetting;
	}

	/**
	 * @param technicalScoreSetting
	 *            the technicalScoreSetting to set
	 */
	public void setTechnicalScoreSetting(ScQtScoreSetting technicalScoreSetting) {
		this.technicalScoreSetting = technicalScoreSetting;
	}

	/**
	 * @return the financialScoreSetting
	 */
	public ScQtScoreSetting getFinancialScoreSetting() {
		return financialScoreSetting;
	}

	/**
	 * @param financialScoreSetting
	 *            the financialScoreSetting to set
	 */
	public void setFinancialScoreSetting(ScQtScoreSetting financialScoreSetting) {
		this.financialScoreSetting = financialScoreSetting;
	}

	/**
	 * @return the technicalpackage
	 */
	public BigDecimal getTechnicalpackage() {
		return technicalpackage;
	}

	/**
	 * @param technicalpackage
	 *            the technicalpackage to set
	 */
	public void setTechnicalpackage(BigDecimal technicalpackage) {
		this.technicalpackage = technicalpackage;
	}

	/**
	 * @return the financialpackage
	 */
	public BigDecimal getFinancialpackage() {
		return financialpackage;
	}

	/**
	 * @param financialpackage
	 *            the financialpackage to set
	 */
	public void setFinancialpackage(BigDecimal financialpackage) {
		this.financialpackage = financialpackage;
	}

	/**
	 * @return the recommendationPackageTotal
	 */
	public BigDecimal getRecommendationPackageTotal() {
		return recommendationPackageTotal;
	}

	/**
	 * @param recommendationPackageTotal
	 *            the recommendationPackageTotal to set
	 */
	public void setRecommendationPackageTotal(
			BigDecimal recommendationPackageTotal) {
		this.recommendationPackageTotal = recommendationPackageTotal;
	}

	/**
	 * @return the recommendTechnicalScore
	 */
	public BigDecimal getRecommendTechnicalScore() {
		return recommendTechnicalScore;
	}

	/**
	 * @param recommendTechnicalScore
	 *            the recommendTechnicalScore to set
	 */
	public void setRecommendTechnicalScore(BigDecimal recommendTechnicalScore) {
		this.recommendTechnicalScore = recommendTechnicalScore;
	}

	/**
	 * @return the recommendfinancialScore
	 */
	public BigDecimal getRecommendfinancialScore() {
		return recommendfinancialScore;
	}

	/**
	 * @param recommendfinancialScore
	 *            the recommendfinancialScore to set
	 */
	public void setRecommendfinancialScore(BigDecimal recommendfinancialScore) {
		this.recommendfinancialScore = recommendfinancialScore;
	}

	/**
	 * @return the checkListSelectScore
	 */
	public List<ScQtCheckListScore> getCheckListSelectScore() {
		return checkListSelectScore;
	}

	/**
	 * @param checkListSelectScore
	 *            the checkListSelectScore to set
	 */
	public void setCheckListSelectScore(
			List<ScQtCheckListScore> checkListSelectScore) {
		this.checkListSelectScore = checkListSelectScore;
	}

	/**
	 * @return the scoringForSelectYears
	 */
	public List<ScQtCheckListScore> getScoringForSelectYears() {
		return scoringForSelectYears;
	}

	/**
	 * @param scoringForSelectYears
	 *            the scoringForSelectYears to set
	 */
	public void setScoringForSelectYears(
			List<ScQtCheckListScore> scoringForSelectYears) {
		this.scoringForSelectYears = scoringForSelectYears;
	}

	/**
	 * @return the nonFederalSelectScore
	 */
	public List<ScQtCheckListScore> getNonFederalSelectScore() {
		return nonFederalSelectScore;
	}

	/**
	 * @param nonFederalSelectScore
	 *            the nonFederalSelectScore to set
	 */
	public void setNonFederalSelectScore(
			List<ScQtCheckListScore> nonFederalSelectScore) {
		this.nonFederalSelectScore = nonFederalSelectScore;
	}

	/**
	 * @return the nonFederalSelectScoreyears
	 */
	public List<ScQtCheckListScore> getNonFederalSelectScoreyears() {
		return nonFederalSelectScoreyears;
	}

	/**
	 * @param nonFederalSelectScoreyears
	 *            the nonFederalSelectScoreyears to set
	 */
	public void setNonFederalSelectScoreyears(
			List<ScQtCheckListScore> nonFederalSelectScoreyears) {
		this.nonFederalSelectScoreyears = nonFederalSelectScoreyears;
	}

	/**
	 * @return the warantySelectScore
	 */
	public List<ScQtCheckListScore> getWarantySelectScore() {
		return warantySelectScore;
	}

	/**
	 * @param warantySelectScore
	 *            the warantySelectScore to set
	 */
	public void setWarantySelectScore(
			List<ScQtCheckListScore> warantySelectScore) {
		this.warantySelectScore = warantySelectScore;
	}

	/**
	 * @return the trainingSelectList
	 */
	public List<ScQtCheckListScore> getTrainingSelectList() {
		return trainingSelectList;
	}

	/**
	 * @param trainingSelectList
	 *            the trainingSelectList to set
	 */
	public void setTrainingSelectList(
			List<ScQtCheckListScore> trainingSelectList) {
		this.trainingSelectList = trainingSelectList;
	}

	/**
	 * @return the technicalStaffSelectList
	 */
	public List<ScQtCheckListScore> getTechnicalStaffSelectList() {
		return technicalStaffSelectList;
	}

	/**
	 * @param technicalStaffSelectList
	 *            the technicalStaffSelectList to set
	 */
	public void setTechnicalStaffSelectList(
			List<ScQtCheckListScore> technicalStaffSelectList) {
		this.technicalStaffSelectList = technicalStaffSelectList;
	}

	/**
	 * @return the totalEmployeeSelectList
	 */
	public List<ScQtCheckListScore> getTotalEmployeeSelectList() {
		return totalEmployeeSelectList;
	}

	/**
	 * @param totalEmployeeSelectList
	 *            the totalEmployeeSelectList to set
	 */
	public void setTotalEmployeeSelectList(
			List<ScQtCheckListScore> totalEmployeeSelectList) {
		this.totalEmployeeSelectList = totalEmployeeSelectList;
	}

	/**
	 * @return the finGovContractSelectList
	 */
	public List<ScQtCheckListScore> getFinGovContractSelectList() {
		return finGovContractSelectList;
	}

	/**
	 * @param finGovContractSelectList
	 *            the finGovContractSelectList to set
	 */
	public void setFinGovContractSelectList(
			List<ScQtCheckListScore> finGovContractSelectList) {
		this.finGovContractSelectList = finGovContractSelectList;
	}

	/**
	 * @return the finPrivateContractSelectList
	 */
	public List<ScQtCheckListScore> getFinPrivateContractSelectList() {
		return finPrivateContractSelectList;
	}

	/**
	 * @param finPrivateContractSelectList
	 *            the finPrivateContractSelectList to set
	 */
	public void setFinPrivateContractSelectList(
			List<ScQtCheckListScore> finPrivateContractSelectList) {
		this.finPrivateContractSelectList = finPrivateContractSelectList;
	}

	/**
	 * @return the creditfacilitySelectList
	 */
	public List<ScQtCheckListScore> getCreditfacilitySelectList() {
		return creditfacilitySelectList;
	}

	/**
	 * @param creditfacilitySelectList
	 *            the creditfacilitySelectList to set
	 */
	public void setCreditfacilitySelectList(
			List<ScQtCheckListScore> creditfacilitySelectList) {
		this.creditfacilitySelectList = creditfacilitySelectList;
	}

	/**
	 * @return the cashSelectList
	 */
	public List<ScQtCheckListScore> getCashSelectList() {
		return cashSelectList;
	}

	/**
	 * @param cashSelectList
	 *            the cashSelectList to set
	 */
	public void setCashSelectList(List<ScQtCheckListScore> cashSelectList) {
		this.cashSelectList = cashSelectList;
	}

	/**
	 * @return the bankSelectList
	 */
	public List<ScQtCheckListScore> getBankSelectList() {
		return bankSelectList;
	}

	/**
	 * @param bankSelectList
	 *            the bankSelectList to set
	 */
	public void setBankSelectList(List<ScQtCheckListScore> bankSelectList) {
		this.bankSelectList = bankSelectList;
	}

	/**
	 * @return the assetLiabilitySelectList
	 */
	public List<ScQtCheckListScore> getAssetLiabilitySelectList() {
		return assetLiabilitySelectList;
	}

	/**
	 * @param assetLiabilitySelectList
	 *            the assetLiabilitySelectList to set
	 */
	public void setAssetLiabilitySelectList(
			List<ScQtCheckListScore> assetLiabilitySelectList) {
		this.assetLiabilitySelectList = assetLiabilitySelectList;
	}

	/**
	 * @return the paidUpSelectList
	 */
	public List<ScQtCheckListScore> getPaidUpSelectList() {
		return paidUpSelectList;
	}

	/**
	 * @param paidUpSelectList
	 *            the paidUpSelectList to set
	 */
	public void setPaidUpSelectList(List<ScQtCheckListScore> paidUpSelectList) {
		this.paidUpSelectList = paidUpSelectList;
	}

	/**
	 * @return the tabActiveIndex
	 */
	public int getTabActiveIndex() {
		return tabActiveIndex;
	}

	/**
	 * @param tabActiveIndex
	 *            the tabActiveIndex to set
	 */
	public void setTabActiveIndex(int tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	/**
	 * @return the rootZoanlTree
	 */
	public TreeNode getRootZoanlTree() {
		return rootZoanlTree;
	}

	/**
	 * @param rootZoanlTree
	 *            the rootZoanlTree to set
	 */
	public void setRootZoanlTree(TreeNode rootZoanlTree) {
		this.rootZoanlTree = rootZoanlTree;
	}

	/**
	 * @return the suppCriteriaopenSelectList
	 */
	public List<ScQtCategory> getSuppCriteriaopenSelectList() {
		return suppCriteriaopenSelectList;
	}

	/**
	 * @param suppCriteriaopenSelectList
	 *            the suppCriteriaopenSelectList to set
	 */
	public void setSuppCriteriaopenSelectList(
			List<ScQtCategory> suppCriteriaopenSelectList) {
		this.suppCriteriaopenSelectList = suppCriteriaopenSelectList;
	}

	/**
	 * @return the progressivePaymentList
	 */
	public List<ScQtProgressivePayment> getProgressivePaymentList() {
		return progressivePaymentList;
	}

	/**
	 * @param progressivePaymentList
	 *            the progressivePaymentList to set
	 */
	public void setProgressivePaymentList(
			List<ScQtProgressivePayment> progressivePaymentList) {
		this.progressivePaymentList = progressivePaymentList;
	}

	/**
	 * @return the scQtRemarksList
	 */
	public List<ScQtRemark> getScQtRemarksList() {
		return scQtRemarksList;
	}

	/**
	 * @param scQtRemarksList
	 *            the scQtRemarksList to set
	 */
	public void setScQtRemarksList(List<ScQtRemark> scQtRemarksList) {
		this.scQtRemarksList = scQtRemarksList;
	}

	/**
	 * @return the stateList
	 */
	public List<PmState> getStateList() {
		return stateList;
	}

	/**
	 * @param stateList
	 *            the stateList to set
	 */
	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}

	/**
	 * @return the districtList
	 */
	public List<PmDistrict> getDistrictList() {
		return districtList;
	}

	/**
	 * @param districtList
	 *            the districtList to set
	 */
	public void setDistrictList(List<PmDistrict> districtList) {
		this.districtList = districtList;
	}

	/**
	 * @return the stateId
	 */
	public Long getStateId() {
		return stateId;
	}

	/**
	 * @param stateId
	 *            the stateId to set
	 */
	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	/**
	 * @return the districtId
	 */
	public Long getDistrictId() {
		return districtId;
	}

	/**
	 * @param districtId
	 *            the districtId to set
	 */
	public void setDistrictId(Long districtId) {
		this.districtId = districtId;
	}

	/**
	 * @return the ministryList
	 */
	public List<PmOrgProfile> getMinistryList() {
		return ministryList;
	}

	/**
	 * @param ministryList
	 *            the ministryList to set
	 */
	public void setMinistryList(List<PmOrgProfile> ministryList) {
		this.ministryList = ministryList;
	}

	/**
	 * @return the ministryId
	 */
	public Long getMinistryId() {
		return ministryId;
	}

	/**
	 * @param ministryId
	 *            the ministryId to set
	 */
	public void setMinistryId(Long ministryId) {
		this.ministryId = ministryId;
	}

	/**
	 * @return the departmentList
	 */
	public List<PmOrgProfile> getDepartmentList() {
		return departmentList;
	}

	/**
	 * @param departmentList
	 *            the departmentList to set
	 */
	public void setDepartmentList(List<PmOrgProfile> departmentList) {
		this.departmentList = departmentList;
	}

	/**
	 * @return the ptjtList
	 */
	public List<PmOrgProfile> getPtjtList() {
		return ptjtList;
	}

	/**
	 * @param ptjtList
	 *            the ptjtList to set
	 */
	public void setPtjtList(List<PmOrgProfile> ptjtList) {
		this.ptjtList = ptjtList;
	}

	/**
	 * @return the departmentId
	 */
	public Long getDepartmentId() {
		return departmentId;
	}

	/**
	 * @param departmentId
	 *            the departmentId to set
	 */
	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	/**
	 * @return the ptjId
	 */
	public Long getPtjId() {
		return ptjId;
	}

	/**
	 * @param ptjId
	 *            the ptjId to set
	 */
	public void setPtjId(Long ptjId) {
		this.ptjId = ptjId;
	}

	/**
	 * @return the deliveryAddressSelect
	 */
	public List<PmAddressType> getDeliveryAddressSelect() {
		return deliveryAddressSelect;
	}

	/**
	 * @param deliveryAddressSelect
	 *            the deliveryAddressSelect to set
	 */
	public void setDeliveryAddressSelect(
			List<PmAddressType> deliveryAddressSelect) {
		this.deliveryAddressSelect = deliveryAddressSelect;
	}

	/**
	 * @return the stageTwoTechComplianceList
	 */
	public List<PmDocument> getStageTwoTechComplianceList() {
		return stageTwoTechComplianceList;
	}

	/**
	 * @param stageTwoTechComplianceList
	 *            the stageTwoTechComplianceList to set
	 */
	public void setStageTwoTechComplianceList(
			List<PmDocument> stageTwoTechComplianceList) {
		this.stageTwoTechComplianceList = stageTwoTechComplianceList;
	}

	/**
	 * @return the stageTwoTechComplianceSelect
	 */
	public List<PmDocument> getStageTwoTechComplianceSelect() {
		return stageTwoTechComplianceSelect;
	}

	/**
	 * @param stageTwoTechComplianceSelect
	 *            the stageTwoTechComplianceSelect to set
	 */
	public void setStageTwoTechComplianceSelect(
			List<PmDocument> stageTwoTechComplianceSelect) {
		this.stageTwoTechComplianceSelect = stageTwoTechComplianceSelect;
	}

	/**
	 * @return the stageTwoFinComplianceList
	 */
	public List<PmDocument> getStageTwoFinComplianceList() {
		return stageTwoFinComplianceList;
	}

	/**
	 * @param stageTwoFinComplianceList
	 *            the stageTwoFinComplianceList to set
	 */
	public void setStageTwoFinComplianceList(
			List<PmDocument> stageTwoFinComplianceList) {
		this.stageTwoFinComplianceList = stageTwoFinComplianceList;
	}

	/**
	 * @return the stageTwoFinComplianceSelect
	 */
	public List<PmDocument> getStageTwoFinComplianceSelect() {
		return stageTwoFinComplianceSelect;
	}

	/**
	 * @param stageTwoFinComplianceSelect
	 *            the stageTwoFinComplianceSelect to set
	 */
	public void setStageTwoFinComplianceSelect(
			List<PmDocument> stageTwoFinComplianceSelect) {
		this.stageTwoFinComplianceSelect = stageTwoFinComplianceSelect;
	}

	/**
	 * @return the startYear
	 */
	public String getStartYear() {
		return startYear;
	}

	/**
	 * @param startYear
	 *            the startYear to set
	 */
	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

	/**
	 * @return the endYear
	 */
	public String getEndYear() {
		return endYear;
	}

	/**
	 * @param endYear
	 *            the endYear to set
	 */
	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}

	/**
	 * @return the offerPricePackage
	 */
	public ScQtScoreOfferPrice getOfferPricePackage() {
		return offerPricePackage;
	}

	/**
	 * @param offerPricePackage
	 *            the offerPricePackage to set
	 */
	public void setOfferPricePackage(ScQtScoreOfferPrice offerPricePackage) {
		this.offerPricePackage = offerPricePackage;
	}

	/**
	 * @return the offerPriceItem
	 */
	public ScQtScoreOfferPrice getOfferPriceItem() {
		return OfferPriceItem;
	}

	/**
	 * @param offerPriceItem
	 *            the offerPriceItem to set
	 */
	public void setOfferPriceItem(ScQtScoreOfferPrice offerPriceItem) {
		OfferPriceItem = offerPriceItem;
	}

	/**
	 * @return the demoScoreSetting
	 */
	public ScQtScoreSetting getDemoScoreSetting() {
		return demoScoreSetting;
	}

	/**
	 * @param demoScoreSetting
	 *            the demoScoreSetting to set
	 */
	public void setDemoScoreSetting(ScQtScoreSetting demoScoreSetting) {
		this.demoScoreSetting = demoScoreSetting;
	}

	/**
	 * @return the recommendationPackageScore
	 */
	public ScQtScoreSetting getRecommendationPackageScore() {
		return recommendationPackageScore;
	}

	/**
	 * @param recommendationPackageScore
	 *            the recommendationPackageScore to set
	 */
	public void setRecommendationPackageScore(
			ScQtScoreSetting recommendationPackageScore) {
		this.recommendationPackageScore = recommendationPackageScore;
	}

	/**
	 * @return the recommendationScore
	 */
	public ScQtScoreSetting getRecommendationScore() {
		return recommendationScore;
	}

	/**
	 * @param recommendationScore
	 *            the recommendationScore to set
	 */
	public void setRecommendationScore(ScQtScoreSetting recommendationScore) {
		this.recommendationScore = recommendationScore;
	}

	/**
	 * @return the stateCode
	 */
	public String getStateCode() {
		return stateCode;
	}

	/**
	 * @param stateCode
	 *            the stateCode to set
	 */
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	/**
	 * @return the districtCode
	 */
	public String getDistrictCode() {
		return districtCode;
	}

	/**
	 * @param districtCode
	 *            the districtCode to set
	 */
	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	/**
	 * @return the deliveryZones
	 */
	public List<TplSpecZoneDtl> getDeliveryZones() {
		return deliveryZones;
	}

	/**
	 * @param deliveryZones
	 *            the deliveryZones to set
	 */
	public void setDeliveryZones(List<TplSpecZoneDtl> deliveryZones) {
		this.deliveryZones = deliveryZones;
	}

	/**
	 * @return the tplSpecZoneDtl
	 */
	public TplSpecZoneDtl getTplSpecZoneDtl() {
		return tplSpecZoneDtl;
	}

	/**
	 * @param tplSpecZoneDtl
	 *            the tplSpecZoneDtl to set
	 */
	public void setTplSpecZoneDtl(TplSpecZoneDtl tplSpecZoneDtl) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
	}

	/**
	 * @return the addressZoneBean
	 */
	public List<BriefingZoneBean> getAddressZoneBean() {
		return addressZoneBean;
	}

	/**
	 * @param addressZoneBean
	 *            the addressZoneBean to set
	 */
	public void setAddressZoneBean(List<BriefingZoneBean> addressZoneBean) {
		this.addressZoneBean = addressZoneBean;
	}

	/**
	 * @return the panelItemCategory
	 */
	public String getPanelItemCategory() {
		return panelItemCategory;
	}

	/**
	 * @param panelItemCategory
	 *            the panelItemCategory to set
	 */
	public void setPanelItemCategory(String panelItemCategory) {
		this.panelItemCategory = panelItemCategory;
	}

	/**
	 * @return the finSpecList
	 */
	public List<ScQtCheckList> getFinSpecList() {
		return finSpecList;
	}

	/**
	 * @param finSpecList
	 *            the finSpecList to set
	 */
	public void setFinSpecList(List<ScQtCheckList> finSpecList) {
		this.finSpecList = finSpecList;
	}

	/**
	 * @return the panelItemId
	 */
	public Long getPanelItemId() {
		return panelItemId;
	}

	/**
	 * @param panelItemId
	 *            the panelItemId to set
	 */
	public void setPanelItemId(Long panelItemId) {
		this.panelItemId = panelItemId;
	}

	/**
	 * @return the activeIndex
	 */
	public int getActiveIndex() {
		return activeIndex;
	}

	/**
	 * @param activeIndex
	 *            the activeIndex to set
	 */
	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	/**
	 * @return the ctPanelItem
	 */
	public CtPanelItem getCtPanelItem() {
		return ctPanelItem;
	}

	/**
	 * @param ctPanelItem
	 *            the ctPanelItem to set
	 */
	public void setCtPanelItem(CtPanelItem ctPanelItem) {
		this.ctPanelItem = ctPanelItem;
	}

	/**
	 * @return the suppCriteriarestrictedBoSelectList
	 */
	public List<ScQtCategory> getSuppCriteriarestrictedBoSelectList() {
		return suppCriteriarestrictedBoSelectList;
	}

	/**
	 * @param suppCriteriarestrictedBoSelectList
	 *            the suppCriteriarestrictedBoSelectList to set
	 */
	public void setSuppCriteriarestrictedBoSelectList(
			List<ScQtCategory> suppCriteriarestrictedBoSelectList) {
		this.suppCriteriarestrictedBoSelectList = suppCriteriarestrictedBoSelectList;
	}

	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * @param required
	 *            the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * @return the ctZoneSuppliers
	 */
	public List<TreeNode> getCtZoneSuppliers() {
		return ctZoneSuppliers;
	}

	/**
	 * @param ctZoneSuppliers
	 *            the ctZoneSuppliers to set
	 */
	public void setCtZoneSuppliers(List<TreeNode> ctZoneSuppliers) {
		this.ctZoneSuppliers = ctZoneSuppliers;
	}

	/**
	 * @return the panelZoneSelect
	 */
	public CtPanelZone getPanelZoneSelect() {
		return panelZoneSelect;
	}

	/**
	 * @param panelZoneSelect
	 *            the panelZoneSelect to set
	 */
	public void setPanelZoneSelect(CtPanelZone panelZoneSelect) {
		this.panelZoneSelect = panelZoneSelect;
	}

	/**
	 * @return the ctPanelZones
	 */
	public List<CtPanelZone> getCtPanelZones() {
		return ctPanelZones;
	}

	/**
	 * @param ctPanelZones
	 *            the ctPanelZones to set
	 */
	public void setCtPanelZones(List<CtPanelZone> ctPanelZones) {
		this.ctPanelZones = ctPanelZones;
	}

	/**
	 * @return the ctpanelSupplier
	 */
	public CtPanelSupplier getCtpanelSupplier() {
		return ctpanelSupplier;
	}

	/**
	 * @param ctpanelSupplier
	 *            the ctpanelSupplier to set
	 */
	public void setCtpanelSupplier(CtPanelSupplier ctpanelSupplier) {
		this.ctpanelSupplier = ctpanelSupplier;
	}

	/**
	 * @return the circularExpDate
	 */
	public Date getCircularExpDate() {
		return circularExpDate;
	}

	/**
	 * @param circularExpDate
	 *            the circularExpDate to set
	 */
	public void setCircularExpDate(Date circularExpDate) {
		this.circularExpDate = circularExpDate;
	}

	/**
	 * @return the contractremark
	 */
	public ScQtRemark getContractremark() {
		return contractremark;
	}

	/**
	 * @param contractremark
	 *            the contractremark to set
	 */
	public void setContractremark(ScQtRemark contractremark) {
		this.contractremark = contractremark;
	}

	public TreeNode getSelectedTreeNode() {
		return selectedTreeNode;
	}

	public void setSelectedTreeNode(TreeNode selectedTreeNode) {
		this.selectedTreeNode = selectedTreeNode;
	}

	/**
	 * @return the circularVer
	 */
	public CtCircularVer getCircularVer() {
		return circularVer;
	}

	/**
	 * @param circularVer
	 *            the circularVer to set
	 */
	public void setCircularVer(CtCircularVer circularVer) {
		this.circularVer = circularVer;
	}

	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName
	 *            the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public CtCircularSearchBean getCtSearchBean() {
		return ctSearchBean;
	}

	public void setCtSearchBean(CtCircularSearchBean ctSearchBean) {
		this.ctSearchBean = ctSearchBean;
	}

	/**
	 * @return the evaluationTypeItem
	 */
	public List<SelectItem> getEvaluationTypeItem() {
		return evaluationTypeItem;
	}

	/**
	 * @param evaluationTypeItem the evaluationTypeItem to set
	 */
	public void setEvaluationTypeItem(List<SelectItem> evaluationTypeItem) {
		this.evaluationTypeItem = evaluationTypeItem;
	}

	/**
	 * @return the supplierSelectedTreeNode
	 */
	public TreeNode getSupplierSelectedTreeNode() {
		return supplierSelectedTreeNode;
	}

	/**
	 * @param supplierSelectedTreeNode the supplierSelectedTreeNode to set
	 */
	public void setSupplierSelectedTreeNode(TreeNode supplierSelectedTreeNode) {
		this.supplierSelectedTreeNode = supplierSelectedTreeNode;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the chairPersonPinNum
	 */
	public String getChairPersonPinNum() {
		return chairPersonPinNum;
	}

	/**
	 * @param chairPersonPinNum the chairPersonPinNum to set
	 */
	public void setChairPersonPinNum(String chairPersonPinNum) {
		this.chairPersonPinNum = chairPersonPinNum;
	}

	/**
	 * @return the personnel
	 */
	public SmPersonnel getPersonnel() {
		return personnel;
	}

	/**
	 * @param personnel the personnel to set
	 */
	public void setPersonnel(SmPersonnel personnel) {
		this.personnel = personnel;
	}

	/**
	 * @return the supplierBranch
	 */
	public String getSupplierBranch() {
		return supplierBranch;
	}

	/**
	 * @param supplierBranch the supplierBranch to set
	 */
	public void setSupplierBranch(String supplierBranch) {
		this.supplierBranch = supplierBranch;
	}

	/**
	 * @return the supplierAddress
	 */
	public SmAddress getSupplierAddress() {
		return supplierAddress;
	}

	/**
	 * @param supplierAddress the supplierAddress to set
	 */
	public void setSupplierAddress(SmAddress supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	/**
	 * @return the supplierContact
	 */
	public String getSupplierContact() {
		return supplierContact;
	}

	/**
	 * @param supplierContact the supplierContact to set
	 */
	public void setSupplierContact(String supplierContact) {
		this.supplierContact = supplierContact;
	}

	/**
	 * @return the supplierEmail
	 */
	public String getSupplierEmail() {
		return supplierEmail;
	}

	/**
	 * @param supplierEmail the supplierEmail to set
	 */
	public void setSupplierEmail(String supplierEmail) {
		this.supplierEmail = supplierEmail;
	}

	/**
	 * @return the ctCircularVer
	 */
	public CtCircularVer getCtCircularVer() {
		return ctCircularVer;
	}

	/**
	 * @param ctCircularVer the ctCircularVer to set
	 */
	public void setCtCircularVer(CtCircularVer ctCircularVer) {
		this.ctCircularVer = ctCircularVer;
	}

	/**
	 * @return the finspecChecklist
	 */
	public List<ScQtCheckList> getFinspecChecklist() {
		return finspecChecklist;
	}

	/**
	 * @param finspecChecklist the finspecChecklist to set
	 */
	public void setFinspecChecklist(List<ScQtCheckList> finspecChecklist) {
		this.finspecChecklist = finspecChecklist;
	}

	/**
	 * @return the buttonOutCome
	 */
	public String getButtonOutCome() {
		return buttonOutCome;
	}

	/**
	 * @param buttonOutCome the buttonOutCome to set
	 */
	public void setButtonOutCome(String buttonOutCome) {
		this.buttonOutCome = buttonOutCome;
	}

	/**
	 * @return the allStateCode
	 */
	public int getAllStateCode() {
		return allStateCode;
	}

	/**
	 * @param allStateCode the allStateCode to set
	 */
	public void setAllStateCode(int allStateCode) {
		this.allStateCode = allStateCode;
	}

	/**
	 * @return the isPanelChange
	 */
	public boolean isPanelChange() {
		return isPanelChange;
	}

	/**
	 * @param isPanelChange the isPanelChange to set
	 */
	public void setPanelChange(boolean isPanelChange) {
		this.isPanelChange = isPanelChange;
	}

	/**
	 * @return the newItemCategory
	 */
	public String getNewItemCategory() {
		return newItemCategory;
	}

	/**
	 * @param newItemCategory the newItemCategory to set
	 */
	public void setNewItemCategory(String newItemCategory) {
		this.newItemCategory = newItemCategory;
	}

	/**
	 * @return the existingpanelitem
	 */
	public CtPanelItem getExistingpanelitem() {
		return existingpanelitem;
	}

	/**
	 * @param existingpanelitem the existingpanelitem to set
	 */
	public void setExistingpanelitem(CtPanelItem existingpanelitem) {
		this.existingpanelitem = existingpanelitem;
	}

	/**
	 * @return the yearsDisplayList
	 */
	public List<SelectItem> getYearsDisplayList() {
		return yearsDisplayList;
	}

	/**
	 * @param yearsDisplayList the yearsDisplayList to set
	 */
	public void setYearsDisplayList(List<SelectItem> yearsDisplayList) {
		this.yearsDisplayList = yearsDisplayList;
	}

	/**
	 * @return the bankstmntTomonth
	 */
	public String getBankstmntTomonth() {
		return bankstmntTomonth;
	}

	/**
	 * @param bankstmntTomonth the bankstmntTomonth to set
	 */
	public void setBankstmntTomonth(String bankstmntTomonth) {
		this.bankstmntTomonth = bankstmntTomonth;
	}

	/**
	 * @return the tempIndex
	 */
	public int getTempIndex() {
		return tempIndex;
	}

	/**
	 * @param tempIndex the tempIndex to set
	 */
	public void setTempIndex(int tempIndex) {
		this.tempIndex = tempIndex;
	}

}
