package my.ep.web.managedbean.sc.evaluation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtChecklistEvaluation;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreOfferPrice;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPAssetLiability;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPBankStmt;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPCashOnHand;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPCreditFacility;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPEmployee;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPExperience;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalScore;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleItem;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleLable;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.web.backingbean.sc.evaluation.SummaryZoneBaen;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "evaluationMB")
@SessionScoped
public class EvaluationManagedBean extends BaseManagedBean {

	private List<EvaluationReportListBean> evaluationReportListItems;
	private List<EvaluationReportSubBean> evaluationReportSupplierItems;
	private List<ScQtMeeting> scQtMeetings;

	private List<ScQtMeeting> meetingMultiSelect;
	private ScQtMeeting meeting;
	private List<ScQtCommitteeActivity> committeeMemberList;
	private ScQtChecklistEvaluation checklistEvaluation;
	private List<ScQtChecklistEvaluation> manuals1EvaluationList;
	// private List<ScQtManualEvaluation> manualMultiSelect;
	private List<ScQtEvaluationResult> s1TechEvalList;
	private List<ScQtEvaluationResult> s1FinEvalList;
	private List<ScQtEvaluationResult> s2TechSupplier;
	private List<ScQtProposalChecklist> s2TechChecklist;
	private List<ScQtEvaluationResult> s2FinSupplier;
	private List<ScQtProposalChecklist> s2FinChecklist;
	private List<ScQtCommitteeActivity> committeeActivitymultiSelect;
	private ScQtEvaluationResult s2techSingleSelect;
	private ScQtEvaluationResult s2finSingleSelect;
	private ScEvaluationResult evaluationResult;
	private List<ScQtProposalChecklist> complianceList;
	private List<ScQtCommitteeActivity> signOffMemberList;
	private List<ScQtEvaluationResult> qualifiedList;
	private List<ScQtEvaluationResult> disQualifiedList;
	private ScQtEvaluationResult scQtEvaluationResult;
	private int tabActiveIndex;

	private ScQtCommitteeActivity commiteeActivity;

	// OC,EC,TEC,FEC
	private String committeeType;
	private boolean isDemo = true;
	private List<ScQtEvaluationResult> manuals1SupplierList;
	// private List<ScQtProposalLabel> manualProposalLabel;
	// private List<ScQtManualEvaluation> manualEvaluateList;
	private boolean isEvaluateStatus;
	private Long evaluationResultId;
	private int tempTab;
	private TplSpecMaster specMaster;
	private String overAllResult;
	private String stageOneoverAllResult;
	private int result = 1;
	private int manualList;
	private String viewBy = "P";
	private List<ScQtEvaluationResult> requestDemoSelect;
	private String fileName;
	private List<ScQtMeetingAttendance> meetingAttendanceList;
	private List<ScQtMeetingAttendance> meetingAttendanceSelectList;
	private List<ScQtCheckList> complianceTechnicalCheckList;
	private List<ScQtCheckList> compliancefinancialCheckList;
	private ScQtCheckList scQtChecklist;
	private ScQtProposalChecklist qtProposalChecklist;
	private ScQt scQt;
	private int countDays;
	private List<ScQtProposalChecklist> complianceTechnicalProposalCheckList;
	
	private ScQtCommittee scQtCommittee;
	private List<ScQtCommitteRemarks> qtCommitteeRemarks;
	private int complianceActiveIndex;
	private int prepareActiveIndex;
	private int presentationDemoActiveIndex;
	private List<ScQtSampleLable> complianceSampleLblsupplier;
	private ScQtSampleLable scQtSampleLbl;
	private List<ScQtSampleItem> complianceSampleItemList;
	private int evaluatioStageActiveIndex;
	private List<ScQtCheckList> evaluationStgTechnicalCheckList;
	private List<ScQtCheckList> evaluationStgfinancialCheckList;
	private String summaryViewBy;
	private List<ScQtCheckListScore> technicalGovNoOfContracts; 
	private List<ScQtCheckListScore> technicalGovNoOfYears ;
	
	
	
	
	
	
	

	
	
	private String checklistScoreType;
	private String companyScoreType;
	private ScQtCheckListScore technicalContractUserScore;
	private ScQtCheckListScore technicalNoOfYearsUserScore;

	private List<ScQtEvaluationResult> complianceSummaryQualSuppliers;
	private List<ScQtEvaluationResult> complianceSummaryDisQualSuppliers;

	private List<ScQtProposalChecklist> evaluationComplianceList;
	private List<TplSpecQuestion> specItemList;

	private int complianceQualifiedSuppliers;
	private int complianceDisQualifiedSuppliers;
	private List<ScQtPRPExperience> prpCompanyExperienceList;
	private List<ScLoa> loaGovCompanyExpList;
	private List<ScQtSampleItem> sampleItemList;
	private ScQtPRPEmployee noOfEmployeeList;

	private List<ScQtPRPCashOnHand> cashOnHandList;
	private List<ScQtPRPCreditFacility> creditFacilityList;

	private List<ScQtPRPBankStmt> bankStmtList;
	private List<ScQtPRPAssetLiability> assetList;
	private List<ScQtProposalScore> techGovProposalScore;
	
	

	
	
	
	
	
	
	
	

	private List<ScQtEvaluationResult> evaluationSummaryQualSuppliers;
	private List<ScQtEvaluationResult> evaluationSummaryDisQualSuppliers;

	private List<ScRequestItem> summaryItemList;
	private ScRequestItem singlesummaryItemList;
	private List<ScQtEvaluationResult> itemSummaryQualSuppliers;
	private List<ScQtEvaluationResult> itemSummaryDisQualSuppliers;
	private int itemTotQualSuppliers;
	private int itemTotDisQualSuppliers;

	private int evaTotQualifiedSuppliers;
	private int evaTotDisQualifiedSuppliers;
	private List<SummaryZoneBaen> summaryZoneBean;
	private List<SummaryZoneBaen> summaryItemZoneBean;

	private List<TplSpecZoneDtl> tplSpecZoneDtls;

	private TplSpecZoneDtl selectedZone;

	private List<ScQtProposalChecklist> presentationDemoSupplierList;

	private List<ScQtProposalChecklist> finSpecSupplierList;

	private ScQtScoreOfferPrice offerPricePackage;
	private ScQtScoreOfferPrice offerPriceItem;
	private List<SummaryZoneBaen> finSpecZoneBean;
	private List<ScCommitteeMember> endorscommitteeMemberList;
	private String message;
	private String subject;
	private boolean generateEvaluationButton = false;
	private boolean complianceStageHide = false;
	private int complianceSummaryCount=0;
	private ScQtPRPAssetLiability scQtPRPAssetLiability;

	private BigDecimal bankTotalAmt;

	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();
	//payload
	private Object evaluationPayLoad;
	
	private List<ScQtEvaluationReport> scQtEvaluationReportList;
	private List<ScQtEvaluationReport> selectedScQtEvaluationReports;
	private ScQtEvaluationReport scQtEvaluationReport;
	private String reportHeader;

	// private String stage1committeeType="OC";
	public EvaluationManagedBean() {
		super();
		prepareDummyData1();
	}

	private void prepareDummyData1() {
		evaluationReportListItems = new ArrayList<EvaluationReportListBean>();
		evaluationReportSupplierItems = new ArrayList<EvaluationReportSubBean>();
		EvaluationReportListBean requestListItem1 = new EvaluationReportListBean(
				"General");
		List<EvaluationReportSubBean> supplierItems1 = new ArrayList<EvaluationReportSubBean>();
		supplierItems1.add(new EvaluationReportSubBean("Qtn/Tender Details"));
		supplierItems1.add(new EvaluationReportSubBean("Meeting Details"));
		supplierItems1.add(new EvaluationReportSubBean(
				"Committee Attendence List"));
		requestListItem1.setSupplierItems(supplierItems1);
		evaluationReportListItems.add(requestListItem1);

		EvaluationReportListBean requestListItem2 = new EvaluationReportListBean(
				"Stage1");
		List<EvaluationReportSubBean> supplierItems2 = new ArrayList<EvaluationReportSubBean>();
		supplierItems2.add(new EvaluationReportSubBean("Kaedah Pemarkahan"));
		supplierItems2.add(new EvaluationReportSubBean("Complience Summary"));

		requestListItem2.setSupplierItems(supplierItems2);
		evaluationReportListItems.add(requestListItem2);

		EvaluationReportListBean requestListItem3 = new EvaluationReportListBean(
				"Stage2");
		List<EvaluationReportSubBean> supplierItems3 = new ArrayList<EvaluationReportSubBean>();
		supplierItems3.add(new EvaluationReportSubBean("Kaedah Pemarkahan"));
		supplierItems3.add(new EvaluationReportSubBean("Checklist1"));
		supplierItems3.add(new EvaluationReportSubBean("Checklist2"));
		requestListItem3.setSupplierItems(supplierItems3);
		evaluationReportListItems.add(requestListItem3);

		evaluationReportSupplierItems.add(new EvaluationReportSubBean("Hello"));
		evaluationReportSupplierItems.add(new EvaluationReportSubBean("Hello"));
	}

	public List<EvaluationReportListBean> getEvaluationReportListItems() {
		return evaluationReportListItems;
	}

	public void setEvaluationReportListItems(
			List<EvaluationReportListBean> evaluationReportListItems) {
		this.evaluationReportListItems = evaluationReportListItems;
	}

	public List<EvaluationReportSubBean> getEvaluationReportSupplierItems() {
		return evaluationReportSupplierItems;
	}

	public void setEvaluationReportSupplierItems(
			List<EvaluationReportSubBean> evaluationReportSupplierItems) {
		this.evaluationReportSupplierItems = evaluationReportSupplierItems;
	}

	/**
	 * @return the scQtMeetings
	 */
	public List<ScQtMeeting> getScQtMeetings() {
		return scQtMeetings;
	}

	/**
	 * @param scQtMeetings
	 *            the scQtMeetings to set
	 */
	public void setScQtMeetings(List<ScQtMeeting> scQtMeetings) {
		this.scQtMeetings = scQtMeetings;
	}

	/**
	 * @return the meeting
	 */
	public ScQtMeeting getMeeting() {
		return meeting;
	}

	/**
	 * @param meeting
	 *            the meeting to set
	 */
	public void setMeeting(ScQtMeeting meeting) {
		this.meeting = meeting;
	}

	/**
	 * @return the checklistEvaluation
	 */
	public ScQtChecklistEvaluation getChecklistEvaluation() {
		return checklistEvaluation;
	}

	/**
	 * @param checklistEvaluation
	 *            the checklistEvaluation to set
	 */
	public void setChecklistEvaluation(
			ScQtChecklistEvaluation checklistEvaluation) {
		this.checklistEvaluation = checklistEvaluation;
	}

	/**
	 * @return the manuals1EvaluationList
	 */
	public List<ScQtChecklistEvaluation> getManuals1EvaluationList() {
		return manuals1EvaluationList;
	}

	/**
	 * @param manuals1EvaluationList
	 *            the manuals1EvaluationList to set
	 */
	public void setManuals1EvaluationList(
			List<ScQtChecklistEvaluation> manuals1EvaluationList) {
		this.manuals1EvaluationList = manuals1EvaluationList;
	}

	/**
	 * @param meetingMultiSelect
	 *            the meetingMultiSelect to set
	 */
	public void setMeetingMultiSelect(List<ScQtMeeting> meetingMultiSelect) {
		this.meetingMultiSelect = meetingMultiSelect;
	}

	/**
	 * @return the meetingMultiSelect
	 */
	public List<ScQtMeeting> getMeetingMultiSelect() {
		return meetingMultiSelect;
	}

	/**
	 * @return the signOffMemberList
	 */
	public List<ScQtCommitteeActivity> getSignOffMemberList() {
		return signOffMemberList;
	}

	/**
	 * @param signOffMemberList
	 *            the signOffMemberList to set
	 */
	public void setSignOffMemberList(
			List<ScQtCommitteeActivity> signOffMemberList) {
		this.signOffMemberList = signOffMemberList;
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
	 * @return the committeeMemberList
	 */
	public List<ScQtCommitteeActivity> getCommitteeMemberList() {
		return committeeMemberList;
	}

	/**
	 * @param committeeMemberList
	 *            the committeeMemberList to set
	 */
	public void setCommitteeMemberList(
			List<ScQtCommitteeActivity> committeeMemberList) {
		this.committeeMemberList = committeeMemberList;
	}

	/**
	 * @return the committeeActivitymultiSelect
	 */
	public List<ScQtCommitteeActivity> getCommitteeActivitymultiSelect() {
		return committeeActivitymultiSelect;
	}

	/**
	 * @param committeeActivitymultiSelect
	 *            the committeeActivitymultiSelect to set
	 */
	public void setCommitteeActivitymultiSelect(
			List<ScQtCommitteeActivity> committeeActivitymultiSelect) {
		this.committeeActivitymultiSelect = committeeActivitymultiSelect;
	}

	/**
	 * @return the s1TechEvalList
	 */
	public List<ScQtEvaluationResult> getS1TechEvalList() {
		return s1TechEvalList;
	}

	/**
	 * @param s1TechEvalList
	 *            the s1TechEvalList to set
	 */
	public void setS1TechEvalList(List<ScQtEvaluationResult> s1TechEvalList) {
		this.s1TechEvalList = s1TechEvalList;
	}

	/**
	 * @return the s1FinEvalList
	 */
	public List<ScQtEvaluationResult> getS1FinEvalList() {
		return s1FinEvalList;
	}

	/**
	 * @param s1FinEvalList
	 *            the s1FinEvalList to set
	 */
	public void setS1FinEvalList(List<ScQtEvaluationResult> s1FinEvalList) {
		this.s1FinEvalList = s1FinEvalList;
	}

	/**
	 * @return the s2TechSupplier
	 */
	public List<ScQtEvaluationResult> getS2TechSupplier() {
		return s2TechSupplier;
	}

	/**
	 * @param s2TechSupplier
	 *            the s2TechSupplier to set
	 */
	public void setS2TechSupplier(List<ScQtEvaluationResult> s2TechSupplier) {
		this.s2TechSupplier = s2TechSupplier;
	}

	/**
	 * @return the s2FinSupplier
	 */
	public List<ScQtEvaluationResult> getS2FinSupplier() {
		return s2FinSupplier;
	}

	/**
	 * @param s2FinSupplier
	 *            the s2FinSupplier to set
	 */
	public void setS2FinSupplier(List<ScQtEvaluationResult> s2FinSupplier) {
		this.s2FinSupplier = s2FinSupplier;
	}

	/**
	 * @return the evaluationResult
	 */
	public ScEvaluationResult getEvaluationResult() {
		return evaluationResult;
	}

	/**
	 * @param evaluationResult
	 *            the evaluationResult to set
	 */
	public void setEvaluationResult(ScEvaluationResult evaluationResult) {
		this.evaluationResult = evaluationResult;
	}

	/**
	 * @return the scQtEvaluationResult
	 */
	public ScQtEvaluationResult getScQtEvaluationResult() {
		return scQtEvaluationResult;
	}

	/**
	 * @param scQtEvaluationResult
	 *            the scQtEvaluationResult to set
	 */
	public void setScQtEvaluationResult(
			ScQtEvaluationResult scQtEvaluationResult) {
		this.scQtEvaluationResult = scQtEvaluationResult;
	}

	/**
	 * @return the s2techSingleSelect
	 */
	public ScQtEvaluationResult getS2techSingleSelect() {
		return s2techSingleSelect;
	}

	/**
	 * @param s2techSingleSelect
	 *            the s2techSingleSelect to set
	 */
	public void setS2techSingleSelect(ScQtEvaluationResult s2techSingleSelect) {
		this.s2techSingleSelect = s2techSingleSelect;
	}

	/**
	 * @return the s2finSingleSelect
	 */
	public ScQtEvaluationResult getS2finSingleSelect() {
		return s2finSingleSelect;
	}

	/**
	 * @param s2finSingleSelect
	 *            the s2finSingleSelect to set
	 */
	public void setS2finSingleSelect(ScQtEvaluationResult s2finSingleSelect) {
		this.s2finSingleSelect = s2finSingleSelect;
	}

	/**
	 * @return the commiteeActivity
	 */
	public ScQtCommitteeActivity getCommiteeActivity() {
		return commiteeActivity;
	}

	/**
	 * @param commiteeActivity
	 *            the commiteeActivity to set
	 */
	public void setCommiteeActivity(ScQtCommitteeActivity commiteeActivity) {
		this.commiteeActivity = commiteeActivity;
	}

	/**
	 * @return the committeeType
	 */
	public String getCommitteeType() {
		return committeeType;
	}

	/**
	 * @param committeeType
	 *            the committeeType to set
	 */
	public void setCommitteeType(String committeeType) {
		this.committeeType = committeeType;
	}

	/**
	 * @return the manuals1SupplierList
	 */
	public List<ScQtEvaluationResult> getManuals1SupplierList() {
		return manuals1SupplierList;
	}

	/**
	 * @param manuals1SupplierList
	 *            the manuals1SupplierList to set
	 */
	public void setManuals1SupplierList(
			List<ScQtEvaluationResult> manuals1SupplierList) {
		this.manuals1SupplierList = manuals1SupplierList;
	}

	/**
	 * @return the isEvaluateStatus
	 */
	public boolean isEvaluateStatus() {
		return isEvaluateStatus;
	}

	/**
	 * @param isEvaluateStatus
	 *            the isEvaluateStatus to set
	 */
	public void setEvaluateStatus(boolean isEvaluateStatus) {
		this.isEvaluateStatus = isEvaluateStatus;
	}

	/**
	 * @return the tempTab
	 */
	public int getTempTab() {
		return tempTab;
	}

	/**
	 * @param tempTab
	 *            the tempTab to set
	 */
	public void setTempTab(int tempTab) {
		this.tempTab = tempTab;
	}

	/**
	 * @return the complianceList
	 */
	public List<ScQtProposalChecklist> getComplianceList() {
		return complianceList;
	}

	/**
	 * @param complianceList
	 *            the complianceList to set
	 */
	public void setComplianceList(List<ScQtProposalChecklist> complianceList) {
		this.complianceList = complianceList;
	}

	/**
	 * @return the evaluationResultId
	 */
	public Long getEvaluationResultId() {
		return evaluationResultId;
	}

	/**
	 * @param evaluationResultId
	 *            the evaluationResultId to set
	 */
	public void setEvaluationResultId(Long evaluationResultId) {
		this.evaluationResultId = evaluationResultId;
	}

	/**
	 * @return the s2TechChecklist
	 */
	public List<ScQtProposalChecklist> getS2TechChecklist() {
		return s2TechChecklist;
	}

	/**
	 * @param s2TechChecklist
	 *            the s2TechChecklist to set
	 */
	public void setS2TechChecklist(List<ScQtProposalChecklist> s2TechChecklist) {
		this.s2TechChecklist = s2TechChecklist;
	}

	/**
	 * @return the s2FinChecklist
	 */
	public List<ScQtProposalChecklist> getS2FinChecklist() {
		return s2FinChecklist;
	}

	/**
	 * @param s2FinChecklist
	 *            the s2FinChecklist to set
	 */
	public void setS2FinChecklist(List<ScQtProposalChecklist> s2FinChecklist) {
		this.s2FinChecklist = s2FinChecklist;
	}

	/**
	 * @return the qualifiedList
	 */
	public List<ScQtEvaluationResult> getQualifiedList() {
		return qualifiedList;
	}

	/**
	 * @param qualifiedList
	 *            the qualifiedList to set
	 */
	public void setQualifiedList(List<ScQtEvaluationResult> qualifiedList) {
		this.qualifiedList = qualifiedList;
	}

	/**
	 * @return the disQualifiedList
	 */
	public List<ScQtEvaluationResult> getDisQualifiedList() {
		return disQualifiedList;
	}

	/**
	 * @param disQualifiedList
	 *            the disQualifiedList to set
	 */
	public void setDisQualifiedList(List<ScQtEvaluationResult> disQualifiedList) {
		this.disQualifiedList = disQualifiedList;
	}

	/**
	 * @return the specMaster
	 */
	public TplSpecMaster getSpecMaster() {
		return specMaster;
	}

	/**
	 * @param specMaster
	 *            the specMaster to set
	 */
	public void setSpecMaster(TplSpecMaster specMaster) {
		this.specMaster = specMaster;
	}

	/**
	 * @return the overAllResult
	 */
	public String getOverAllResult() {
		return overAllResult;
	}

	/**
	 * @param overAllResult
	 *            the overAllResult to set
	 */
	public void setOverAllResult(String overAllResult) {
		this.overAllResult = overAllResult;
	}

	/**
	 * @return the stageOneoverAllResult
	 */
	public String getStageOneoverAllResult() {
		return stageOneoverAllResult;
	}

	/**
	 * @param stageOneoverAllResult
	 *            the stageOneoverAllResult to set
	 */
	public void setStageOneoverAllResult(String stageOneoverAllResult) {
		this.stageOneoverAllResult = stageOneoverAllResult;
	}

	/**
	 * @return the result
	 */
	public int getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(int result) {
		this.result = result;
	}

	/**
	 * @return the manualList
	 */
	public int getManualList() {
		return manualList;
	}

	/**
	 * @param manualList
	 *            the manualList to set
	 */
	public void setManualList(int manualList) {
		this.manualList = manualList;
	}

	/**
	 * @return the viewBy
	 */
	public String getViewBy() {
		return viewBy;
	}

	/**
	 * @param viewBy
	 *            the viewBy to set
	 */
	public void setViewBy(String viewBy) {
		this.viewBy = viewBy;
	}

	/**
	 * @return the requestDemoSelect
	 */
	public List<ScQtEvaluationResult> getRequestDemoSelect() {
		return requestDemoSelect;
	}

	/**
	 * @param requestDemoSelect
	 *            the requestDemoSelect to set
	 */
	public void setRequestDemoSelect(
			List<ScQtEvaluationResult> requestDemoSelect) {
		this.requestDemoSelect = requestDemoSelect;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
	 * @return the meetingAttendanceList
	 */
	public List<ScQtMeetingAttendance> getMeetingAttendanceList() {
		return meetingAttendanceList;
	}

	/**
	 * @param meetingAttendanceList
	 *            the meetingAttendanceList to set
	 */
	public void setMeetingAttendanceList(
			List<ScQtMeetingAttendance> meetingAttendanceList) {
		this.meetingAttendanceList = meetingAttendanceList;
	}

	/**
	 * @return the meetingAttendanceSelectList
	 */
	public List<ScQtMeetingAttendance> getMeetingAttendanceSelectList() {
		return meetingAttendanceSelectList;
	}

	/**
	 * @param meetingAttendanceSelectList
	 *            the meetingAttendanceSelectList to set
	 */
	public void setMeetingAttendanceSelectList(
			List<ScQtMeetingAttendance> meetingAttendanceSelectList) {
		this.meetingAttendanceSelectList = meetingAttendanceSelectList;
	}

	/**
	 * @return the complianceTechnicalCheckList
	 */
	public List<ScQtCheckList> getComplianceTechnicalCheckList() {
		return complianceTechnicalCheckList;
	}

	/**
	 * @param complianceTechnicalCheckList
	 *            the complianceTechnicalCheckList to set
	 */
	public void setComplianceTechnicalCheckList(
			List<ScQtCheckList> complianceTechnicalCheckList) {
		this.complianceTechnicalCheckList = complianceTechnicalCheckList;
	}

	/**
	 * @return the compliancefinancialCheckList
	 */
	public List<ScQtCheckList> getCompliancefinancialCheckList() {
		return compliancefinancialCheckList;
	}

	/**
	 * @param compliancefinancialCheckList
	 *            the compliancefinancialCheckList to set
	 */
	public void setCompliancefinancialCheckList(
			List<ScQtCheckList> compliancefinancialCheckList) {
		this.compliancefinancialCheckList = compliancefinancialCheckList;
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
	 * @return the countDays
	 */
	public int getCountDays() {
		return countDays;
	}

	/**
	 * @param countDays
	 *            the countDays to set
	 */
	public void setCountDays(int countDays) {
		this.countDays = countDays;
	}

	/**
	 * @return the complianceTechnicalProposalCheckList
	 */
	public List<ScQtProposalChecklist> getComplianceTechnicalProposalCheckList() {
		return complianceTechnicalProposalCheckList;
	}

	/**
	 * @param complianceTechnicalProposalCheckList
	 *            the complianceTechnicalProposalCheckList to set
	 */
	public void setComplianceTechnicalProposalCheckList(
			List<ScQtProposalChecklist> complianceTechnicalProposalCheckList) {
		this.complianceTechnicalProposalCheckList = complianceTechnicalProposalCheckList;
	}

	

	/**
	 * @return the scQtCommittee
	 */
	public ScQtCommittee getScQtCommittee() {
		return scQtCommittee;
	}

	/**
	 * @param scQtCommittee
	 *            the scQtCommittee to set
	 */
	public void setScQtCommittee(ScQtCommittee scQtCommittee) {
		this.scQtCommittee = scQtCommittee;
	}

	/**
	 * @return the qtCommitteeRemarks
	 */
	public List<ScQtCommitteRemarks> getQtCommitteeRemarks() {
		return qtCommitteeRemarks;
	}

	/**
	 * @param qtCommitteeRemarks
	 *            the qtCommitteeRemarks to set
	 */
	public void setQtCommitteeRemarks(
			List<ScQtCommitteRemarks> qtCommitteeRemarks) {
		this.qtCommitteeRemarks = qtCommitteeRemarks;
	}

	/**
	 * @return the complianceActiveIndex
	 */
	public int getComplianceActiveIndex() {
		return complianceActiveIndex;
	}

	/**
	 * @param complianceActiveIndex
	 *            the complianceActiveIndex to set
	 */
	public void setComplianceActiveIndex(int complianceActiveIndex) {
		this.complianceActiveIndex = complianceActiveIndex;
	}

	/**
	 * @return the prepareActiveIndex
	 */
	public int getPrepareActiveIndex() {
		return prepareActiveIndex;
	}

	/**
	 * @param prepareActiveIndex
	 *            the prepareActiveIndex to set
	 */
	public void setPrepareActiveIndex(int prepareActiveIndex) {
		this.prepareActiveIndex = prepareActiveIndex;
	}

	/**
	 * @return the complianceSampleLblsupplier
	 */
	public List<ScQtSampleLable> getComplianceSampleLblsupplier() {
		return complianceSampleLblsupplier;
	}

	/**
	 * @param complianceSampleLblsupplier
	 *            the complianceSampleLblsupplier to set
	 */
	public void setComplianceSampleLblsupplier(
			List<ScQtSampleLable> complianceSampleLblsupplier) {
		this.complianceSampleLblsupplier = complianceSampleLblsupplier;
	}

	/**
	 * @return the scQtSampleLbl
	 */
	public ScQtSampleLable getScQtSampleLbl() {
		return scQtSampleLbl;
	}

	/**
	 * @param scQtSampleLbl
	 *            the scQtSampleLbl to set
	 */
	public void setScQtSampleLbl(ScQtSampleLable scQtSampleLbl) {
		this.scQtSampleLbl = scQtSampleLbl;
	}

	/**
	 * @return the complianceSampleItemList
	 */
	public List<ScQtSampleItem> getComplianceSampleItemList() {
		return complianceSampleItemList;
	}

	/**
	 * @param complianceSampleItemList
	 *            the complianceSampleItemList to set
	 */
	public void setComplianceSampleItemList(
			List<ScQtSampleItem> complianceSampleItemList) {
		this.complianceSampleItemList = complianceSampleItemList;
	}

	/**
	 * @return the evaluatioStageActiveIndex
	 */
	public int getEvaluatioStageActiveIndex() {
		return evaluatioStageActiveIndex;
	}

	/**
	 * @param evaluatioStageActiveIndex
	 *            the evaluatioStageActiveIndex to set
	 */
	public void setEvaluatioStageActiveIndex(int evaluatioStageActiveIndex) {
		this.evaluatioStageActiveIndex = evaluatioStageActiveIndex;
	}

	/**
	 * @return the summaryViewBy
	 */
	public String getSummaryViewBy() {
		return summaryViewBy;
	}

	/**
	 * @param summaryViewBy
	 *            the summaryViewBy to set
	 */
	public void setSummaryViewBy(String summaryViewBy) {
		this.summaryViewBy = summaryViewBy;
	}

	/**
	 * @return the qtProposalChecklist
	 */
	public ScQtProposalChecklist getQtProposalChecklist() {
		return qtProposalChecklist;
	}

	/**
	 * @param qtProposalChecklist
	 *            the qtProposalChecklist to set
	 */
	public void setQtProposalChecklist(ScQtProposalChecklist qtProposalChecklist) {
		this.qtProposalChecklist = qtProposalChecklist;
	}

	/**
	 * @return the evaluationStgTechnicalCheckList
	 */
	public List<ScQtCheckList> getEvaluationStgTechnicalCheckList() {
		return evaluationStgTechnicalCheckList;
	}

	/**
	 * @param evaluationStgTechnicalCheckList
	 *            the evaluationStgTechnicalCheckList to set
	 */
	public void setEvaluationStgTechnicalCheckList(
			List<ScQtCheckList> evaluationStgTechnicalCheckList) {
		this.evaluationStgTechnicalCheckList = evaluationStgTechnicalCheckList;
	}

	/**
	 * @return the evaluationStgfinancialCheckList
	 */
	public List<ScQtCheckList> getEvaluationStgfinancialCheckList() {
		return evaluationStgfinancialCheckList;
	}

	/**
	 * @param evaluationStgfinancialCheckList
	 *            the evaluationStgfinancialCheckList to set
	 */
	public void setEvaluationStgfinancialCheckList(
			List<ScQtCheckList> evaluationStgfinancialCheckList) {
		this.evaluationStgfinancialCheckList = evaluationStgfinancialCheckList;
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
	 * @return the technicalContractUserScore
	 */
	public ScQtCheckListScore getTechnicalContractUserScore() {
		return technicalContractUserScore;
	}

	/**
	 * @param technicalContractUserScore
	 *            the technicalContractUserScore to set
	 */
	public void setTechnicalContractUserScore(
			ScQtCheckListScore technicalContractUserScore) {
		this.technicalContractUserScore = technicalContractUserScore;
	}

	/**
	 * @return the technicalNoOfYearsUserScore
	 */
	public ScQtCheckListScore getTechnicalNoOfYearsUserScore() {
		return technicalNoOfYearsUserScore;
	}

	/**
	 * @param technicalNoOfYearsUserScore
	 *            the technicalNoOfYearsUserScore to set
	 */
	public void setTechnicalNoOfYearsUserScore(
			ScQtCheckListScore technicalNoOfYearsUserScore) {
		this.technicalNoOfYearsUserScore = technicalNoOfYearsUserScore;
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
	 * @return the technicalGovNoOfContracts
	 */
	public List<ScQtCheckListScore> getTechnicalGovNoOfContracts() {
		return technicalGovNoOfContracts;
	}

	/**
	 * @param technicalGovNoOfContracts
	 *            the technicalGovNoOfContracts to set
	 */
	public void setTechnicalGovNoOfContracts(
			List<ScQtCheckListScore> technicalGovNoOfContracts) {
		this.technicalGovNoOfContracts = technicalGovNoOfContracts;
	}

	/**
	 * @return the technicalGovNoOfYears
	 */
	public List<ScQtCheckListScore> getTechnicalGovNoOfYears() {
		return technicalGovNoOfYears;
	}

	/**
	 * @param technicalGovNoOfYears
	 *            the technicalGovNoOfYears to set
	 */
	public void setTechnicalGovNoOfYears(
			List<ScQtCheckListScore> technicalGovNoOfYears) {
		this.technicalGovNoOfYears = technicalGovNoOfYears;
	}




	/**
	 * @return the complianceSummaryQualSuppliers
	 */
	public List<ScQtEvaluationResult> getComplianceSummaryQualSuppliers() {
		return complianceSummaryQualSuppliers;
	}

	/**
	 * @param complianceSummaryQualSuppliers
	 *            the complianceSummaryQualSuppliers to set
	 */
	public void setComplianceSummaryQualSuppliers(
			List<ScQtEvaluationResult> complianceSummaryQualSuppliers) {
		this.complianceSummaryQualSuppliers = complianceSummaryQualSuppliers;
	}

	/**
	 * @return the complianceSummaryDisQualSuppliers
	 */
	public List<ScQtEvaluationResult> getComplianceSummaryDisQualSuppliers() {
		return complianceSummaryDisQualSuppliers;
	}

	/**
	 * @param complianceSummaryDisQualSuppliers
	 *            the complianceSummaryDisQualSuppliers to set
	 */
	public void setComplianceSummaryDisQualSuppliers(
			List<ScQtEvaluationResult> complianceSummaryDisQualSuppliers) {
		this.complianceSummaryDisQualSuppliers = complianceSummaryDisQualSuppliers;
	}

	/**
	 * @return the complianceQualifiedSuppliers
	 */
	public int getComplianceQualifiedSuppliers() {
		return complianceQualifiedSuppliers;
	}

	/**
	 * @param complianceQualifiedSuppliers
	 *            the complianceQualifiedSuppliers to set
	 */
	public void setComplianceQualifiedSuppliers(int complianceQualifiedSuppliers) {
		this.complianceQualifiedSuppliers = complianceQualifiedSuppliers;
	}

	/**
	 * @return the complianceDisQualifiedSuppliers
	 */
	public int getComplianceDisQualifiedSuppliers() {
		return complianceDisQualifiedSuppliers;
	}

	/**
	 * @param complianceDisQualifiedSuppliers
	 *            the complianceDisQualifiedSuppliers to set
	 */
	public void setComplianceDisQualifiedSuppliers(
			int complianceDisQualifiedSuppliers) {
		this.complianceDisQualifiedSuppliers = complianceDisQualifiedSuppliers;
	}

	/**
	 * @return the specItemList
	 */
	public List<TplSpecQuestion> getSpecItemList() {
		return specItemList;
	}

	/**
	 * @param specItemList
	 *            the specItemList to set
	 */
	public void setSpecItemList(List<TplSpecQuestion> specItemList) {
		this.specItemList = specItemList;
	}

	/**
	 * @return the prpCompanyExperienceList
	 */
	public List<ScQtPRPExperience> getPrpCompanyExperienceList() {
		return prpCompanyExperienceList;
	}

	/**
	 * @param prpCompanyExperienceList
	 *            the prpCompanyExperienceList to set
	 */
	public void setPrpCompanyExperienceList(
			List<ScQtPRPExperience> prpCompanyExperienceList) {
		this.prpCompanyExperienceList = prpCompanyExperienceList;
	}

	/**
	 * @return the loaGovCompanyExpList
	 */
	public List<ScLoa> getLoaGovCompanyExpList() {
		return loaGovCompanyExpList;
	}

	/**
	 * @param loaGovCompanyExpList
	 *            the loaGovCompanyExpList to set
	 */
	public void setLoaGovCompanyExpList(List<ScLoa> loaGovCompanyExpList) {
		this.loaGovCompanyExpList = loaGovCompanyExpList;
	}

	/**
	 * @return the sampleItemList
	 */
	public List<ScQtSampleItem> getSampleItemList() {
		return sampleItemList;
	}

	/**
	 * @param sampleItemList
	 *            the sampleItemList to set
	 */
	public void setSampleItemList(List<ScQtSampleItem> sampleItemList) {
		this.sampleItemList = sampleItemList;
	}

	/**
	 * @return the noOfEmployeeList
	 */
	public ScQtPRPEmployee getNoOfEmployeeList() {
		return noOfEmployeeList;
	}

	/**
	 * @param noOfEmployeeList
	 *            the noOfEmployeeList to set
	 */
	public void setNoOfEmployeeList(ScQtPRPEmployee noOfEmployeeList) {
		this.noOfEmployeeList = noOfEmployeeList;
	}

	/**
	 * @return the cashOnHandList
	 */
	public List<ScQtPRPCashOnHand> getCashOnHandList() {
		return cashOnHandList;
	}

	/**
	 * @param cashOnHandList
	 *            the cashOnHandList to set
	 */
	public void setCashOnHandList(List<ScQtPRPCashOnHand> cashOnHandList) {
		this.cashOnHandList = cashOnHandList;
	}

	/**
	 * @return the creditFacilityList
	 */
	public List<ScQtPRPCreditFacility> getCreditFacilityList() {
		return creditFacilityList;
	}

	/**
	 * @param creditFacilityList
	 *            the creditFacilityList to set
	 */
	public void setCreditFacilityList(
			List<ScQtPRPCreditFacility> creditFacilityList) {
		this.creditFacilityList = creditFacilityList;
	}

	/**
	 * @return the bankStmtList
	 */
	public List<ScQtPRPBankStmt> getBankStmtList() {
		return bankStmtList;
	}

	/**
	 * @param bankStmtList
	 *            the bankStmtList to set
	 */
	public void setBankStmtList(List<ScQtPRPBankStmt> bankStmtList) {
		this.bankStmtList = bankStmtList;
	}

	/**
	 * @return the assetList
	 */
	public List<ScQtPRPAssetLiability> getAssetList() {
		return assetList;
	}

	/**
	 * @param assetList
	 *            the assetList to set
	 */
	public void setAssetList(List<ScQtPRPAssetLiability> assetList) {
		this.assetList = assetList;
	}

	/**
	 * @return the techGovProposalScore
	 */
	public List<ScQtProposalScore> getTechGovProposalScore() {
		return techGovProposalScore;
	}

	/**
	 * @param techGovProposalScore
	 *            the techGovProposalScore to set
	 */
	public void setTechGovProposalScore(
			List<ScQtProposalScore> techGovProposalScore) {
		this.techGovProposalScore = techGovProposalScore;
	}


	/**
	 * @return the bankTotalAmt
	 */
	public BigDecimal getBankTotalAmt() {
		return bankTotalAmt;
	}

	/**
	 * @param bankTotalAmt
	 *            the bankTotalAmt to set
	 */
	public void setBankTotalAmt(BigDecimal bankTotalAmt) {
		this.bankTotalAmt = bankTotalAmt;
	}

	/**
	 * @return the evaluationSummaryQualSuppliers
	 */
	public List<ScQtEvaluationResult> getEvaluationSummaryQualSuppliers() {
		return evaluationSummaryQualSuppliers;
	}

	/**
	 * @param evaluationSummaryQualSuppliers
	 *            the evaluationSummaryQualSuppliers to set
	 */
	public void setEvaluationSummaryQualSuppliers(
			List<ScQtEvaluationResult> evaluationSummaryQualSuppliers) {
		this.evaluationSummaryQualSuppliers = evaluationSummaryQualSuppliers;
	}

	/**
	 * @return the evaluationSummaryDisQualSuppliers
	 */
	public List<ScQtEvaluationResult> getEvaluationSummaryDisQualSuppliers() {
		return evaluationSummaryDisQualSuppliers;
	}

	/**
	 * @param evaluationSummaryDisQualSuppliers
	 *            the evaluationSummaryDisQualSuppliers to set
	 */
	public void setEvaluationSummaryDisQualSuppliers(
			List<ScQtEvaluationResult> evaluationSummaryDisQualSuppliers) {
		this.evaluationSummaryDisQualSuppliers = evaluationSummaryDisQualSuppliers;
	}

	/**
	 * @return the evaTotQualifiedSuppliers
	 */
	public int getEvaTotQualifiedSuppliers() {
		return evaTotQualifiedSuppliers;
	}

	/**
	 * @param evaTotQualifiedSuppliers
	 *            the evaTotQualifiedSuppliers to set
	 */
	public void setEvaTotQualifiedSuppliers(int evaTotQualifiedSuppliers) {
		this.evaTotQualifiedSuppliers = evaTotQualifiedSuppliers;
	}

	/**
	 * @return the evaTotDisQualifiedSuppliers
	 */
	public int getEvaTotDisQualifiedSuppliers() {
		return evaTotDisQualifiedSuppliers;
	}

	/**
	 * @param evaTotDisQualifiedSuppliers
	 *            the evaTotDisQualifiedSuppliers to set
	 */
	public void setEvaTotDisQualifiedSuppliers(int evaTotDisQualifiedSuppliers) {
		this.evaTotDisQualifiedSuppliers = evaTotDisQualifiedSuppliers;
	}

	/**
	 * @return the summaryItemList
	 */
	public List<ScRequestItem> getSummaryItemList() {
		return summaryItemList;
	}

	/**
	 * @param summaryItemList
	 *            the summaryItemList to set
	 */
	public void setSummaryItemList(List<ScRequestItem> summaryItemList) {
		this.summaryItemList = summaryItemList;
	}

	/**
	 * @return the itemSummaryQualSuppliers
	 */
	public List<ScQtEvaluationResult> getItemSummaryQualSuppliers() {
		return itemSummaryQualSuppliers;
	}

	/**
	 * @param itemSummaryQualSuppliers
	 *            the itemSummaryQualSuppliers to set
	 */
	public void setItemSummaryQualSuppliers(
			List<ScQtEvaluationResult> itemSummaryQualSuppliers) {
		this.itemSummaryQualSuppliers = itemSummaryQualSuppliers;
	}

	/**
	 * @return the itemSummaryDisQualSuppliers
	 */
	public List<ScQtEvaluationResult> getItemSummaryDisQualSuppliers() {
		return itemSummaryDisQualSuppliers;
	}

	/**
	 * @param itemSummaryDisQualSuppliers
	 *            the itemSummaryDisQualSuppliers to set
	 */
	public void setItemSummaryDisQualSuppliers(
			List<ScQtEvaluationResult> itemSummaryDisQualSuppliers) {
		this.itemSummaryDisQualSuppliers = itemSummaryDisQualSuppliers;
	}

	/**
	 * @return the itemTotQualSuppliers
	 */
	public int getItemTotQualSuppliers() {
		return itemTotQualSuppliers;
	}

	/**
	 * @param itemTotQualSuppliers
	 *            the itemTotQualSuppliers to set
	 */
	public void setItemTotQualSuppliers(int itemTotQualSuppliers) {
		this.itemTotQualSuppliers = itemTotQualSuppliers;
	}

	/**
	 * @return the itemTotDisQualSuppliers
	 */
	public int getItemTotDisQualSuppliers() {
		return itemTotDisQualSuppliers;
	}

	/**
	 * @param itemTotDisQualSuppliers
	 *            the itemTotDisQualSuppliers to set
	 */
	public void setItemTotDisQualSuppliers(int itemTotDisQualSuppliers) {
		this.itemTotDisQualSuppliers = itemTotDisQualSuppliers;
	}

	/**
	 * @return the singlesummaryItemList
	 */
	public ScRequestItem getSinglesummaryItemList() {
		return singlesummaryItemList;
	}

	/**
	 * @param singlesummaryItemList
	 *            the singlesummaryItemList to set
	 */
	public void setSinglesummaryItemList(ScRequestItem singlesummaryItemList) {
		this.singlesummaryItemList = singlesummaryItemList;
	}

	/**
	 * @return the summaryZoneBean
	 */
	public List<SummaryZoneBaen> getSummaryZoneBean() {
		return summaryZoneBean;
	}

	/**
	 * @param summaryZoneBean
	 *            the summaryZoneBean to set
	 */
	public void setSummaryZoneBean(List<SummaryZoneBaen> summaryZoneBean) {
		this.summaryZoneBean = summaryZoneBean;
	}

	/**
	 * @return the summaryItemZoneBean
	 */
	public List<SummaryZoneBaen> getSummaryItemZoneBean() {
		return summaryItemZoneBean;
	}

	/**
	 * @param summaryItemZoneBean
	 *            the summaryItemZoneBean to set
	 */
	public void setSummaryItemZoneBean(List<SummaryZoneBaen> summaryItemZoneBean) {
		this.summaryItemZoneBean = summaryItemZoneBean;
	}

	/**
	 * @return the tplSpecZoneDtls
	 */
	public List<TplSpecZoneDtl> getTplSpecZoneDtls() {
		return tplSpecZoneDtls;
	}

	/**
	 * @param tplSpecZoneDtls
	 *            the tplSpecZoneDtls to set
	 */
	public void setTplSpecZoneDtls(List<TplSpecZoneDtl> tplSpecZoneDtls) {
		this.tplSpecZoneDtls = tplSpecZoneDtls;
	}

	/**
	 * @return the selectedZone
	 */
	public TplSpecZoneDtl getSelectedZone() {
		return selectedZone;
	}

	/**
	 * @param selectedZone
	 *            the selectedZone to set
	 */
	public void setSelectedZone(TplSpecZoneDtl selectedZone) {
		this.selectedZone = selectedZone;
	}

	/**
	 * @return the presentationDemoActiveIndex
	 */
	public int getPresentationDemoActiveIndex() {
		return presentationDemoActiveIndex;
	}

	/**
	 * @param presentationDemoActiveIndex
	 *            the presentationDemoActiveIndex to set
	 */
	public void setPresentationDemoActiveIndex(int presentationDemoActiveIndex) {
		this.presentationDemoActiveIndex = presentationDemoActiveIndex;
	}

	/**
	 * @return the presentationDemoSupplierList
	 */
	public List<ScQtProposalChecklist> getPresentationDemoSupplierList() {
		return presentationDemoSupplierList;
	}

	/**
	 * @param presentationDemoSupplierList
	 *            the presentationDemoSupplierList to set
	 */
	public void setPresentationDemoSupplierList(
			List<ScQtProposalChecklist> presentationDemoSupplierList) {
		this.presentationDemoSupplierList = presentationDemoSupplierList;
	}

	/**
	 * @return the finSpecSupplierList
	 */
	public List<ScQtProposalChecklist> getFinSpecSupplierList() {
		return finSpecSupplierList;
	}

	/**
	 * @param finSpecSupplierList
	 *            the finSpecSupplierList to set
	 */
	public void setFinSpecSupplierList(
			List<ScQtProposalChecklist> finSpecSupplierList) {
		this.finSpecSupplierList = finSpecSupplierList;
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
		return offerPriceItem;
	}

	/**
	 * @param offerPriceItem
	 *            the offerPriceItem to set
	 */
	public void setOfferPriceItem(ScQtScoreOfferPrice offerPriceItem) {
		this.offerPriceItem = offerPriceItem;
	}

	/**
	 * @return the finSpecZoneBean
	 */
	public List<SummaryZoneBaen> getFinSpecZoneBean() {
		return finSpecZoneBean;
	}

	/**
	 * @param finSpecZoneBean
	 *            the finSpecZoneBean to set
	 */
	public void setFinSpecZoneBean(List<SummaryZoneBaen> finSpecZoneBean) {
		this.finSpecZoneBean = finSpecZoneBean;
	}

	/**
	 * @return the endorscommitteeMemberList
	 */
	public List<ScCommitteeMember> getEndorscommitteeMemberList() {
		return endorscommitteeMemberList;
	}

	/**
	 * @param endorscommitteeMemberList
	 *            the endorscommitteeMemberList to set
	 */
	public void setEndorscommitteeMemberList(
			List<ScCommitteeMember> endorscommitteeMemberList) {
		this.endorscommitteeMemberList = endorscommitteeMemberList;
	}

	/**
	 * @return the isDemo
	 */
	public boolean isDemo() {
		return isDemo;
	}

	/**
	 * @param isDemo
	 *            the isDemo to set
	 */
	public void setDemo(boolean isDemo) {
		this.isDemo = isDemo;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the evaluationComplianceList
	 */
	public List<ScQtProposalChecklist> getEvaluationComplianceList() {
		return evaluationComplianceList;
	}

	/**
	 * @param evaluationComplianceList
	 *            the evaluationComplianceList to set
	 */
	public void setEvaluationComplianceList(
			List<ScQtProposalChecklist> evaluationComplianceList) {
		this.evaluationComplianceList = evaluationComplianceList;
	}

	/**
	 * @return the generateEvaluationButton
	 */
	public boolean isGenerateEvaluationButton() {
		return generateEvaluationButton;
	}

	/**
	 * @param generateEvaluationButton
	 *            the generateEvaluationButton to set
	 */
	public void setGenerateEvaluationButton(boolean generateEvaluationButton) {
		this.generateEvaluationButton = generateEvaluationButton;
	}

	/**
	 * @return the complianceStageHide
	 */
	public boolean isComplianceStageHide() {
		return complianceStageHide;
	}

	/**
	 * @param complianceStageHide the complianceStageHide to set
	 */
	public void setComplianceStageHide(boolean complianceStageHide) {
		this.complianceStageHide = complianceStageHide;
	}

	/**
	 * @return the complianceSummaryCount
	 */
	public int getComplianceSummaryCount() {
		return complianceSummaryCount;
	}

	/**
	 * @param complianceSummaryCount the complianceSummaryCount to set
	 */
	public void setComplianceSummaryCount(int complianceSummaryCount) {
		this.complianceSummaryCount = complianceSummaryCount;
	}

	public Object getEvaluationPayLoad() {
		return evaluationPayLoad;
	}

	public void setEvaluationPayLoad(Object evaluationPayLoad) {
		this.evaluationPayLoad = evaluationPayLoad;
	}

	/**
	 * @return the scQtPRPAssetLiability
	 */
	public ScQtPRPAssetLiability getScQtPRPAssetLiability() {
		return scQtPRPAssetLiability;
	}

	/**
	 * @param scQtPRPAssetLiability the scQtPRPAssetLiability to set
	 */
	public void setScQtPRPAssetLiability(ScQtPRPAssetLiability scQtPRPAssetLiability) {
		this.scQtPRPAssetLiability = scQtPRPAssetLiability;
	}

	/**
	 * @return the scQtEvaluationReportList
	 */
	public List<ScQtEvaluationReport> getScQtEvaluationReportList() {
		return scQtEvaluationReportList;
	}

	/**
	 * @param scQtEvaluationReportList the scQtEvaluationReportList to set
	 */
	public void setScQtEvaluationReportList(
			List<ScQtEvaluationReport> scQtEvaluationReportList) {
		this.scQtEvaluationReportList = scQtEvaluationReportList;
	}

	/**
	 * @return the selectedScQtEvaluationReports
	 */
	public List<ScQtEvaluationReport> getSelectedScQtEvaluationReports() {
		return selectedScQtEvaluationReports;
	}

	/**
	 * @param selectedScQtEvaluationReports the selectedScQtEvaluationReports to set
	 */
	public void setSelectedScQtEvaluationReports(
			List<ScQtEvaluationReport> selectedScQtEvaluationReports) {
		this.selectedScQtEvaluationReports = selectedScQtEvaluationReports;
	}

	/**
	 * @return the scQtEvaluationReport
	 */
	public ScQtEvaluationReport getScQtEvaluationReport() {
		return scQtEvaluationReport;
	}

	/**
	 * @param scQtEvaluationReport the scQtEvaluationReport to set
	 */
	public void setScQtEvaluationReport(ScQtEvaluationReport scQtEvaluationReport) {
		this.scQtEvaluationReport = scQtEvaluationReport;
	}

	/**
	 * @return the reportHeader
	 */
	public String getReportHeader() {
		return reportHeader;
	}

	/**
	 * @param reportHeader the reportHeader to set
	 */
	public void setReportHeader(String reportHeader) {
		this.reportHeader = reportHeader;
	}

}
