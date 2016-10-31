/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.managedbean.sc.evaluation;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRemark;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationCommitteeRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinancialCommitteeRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtOpeningCommitteeRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklistAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreOfferPrice;
import my.ep.persistence.entity.sc.quationtender.ScQtTechnicalCommitteeRemark;
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
import my.ep.persistence.entity.tpl.spec.TplSpecScoring;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.web.backingbean.sc.supplierproposal.TechnicalStaffBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.MenuModel;

/**
 * The Class EvaluationManagedBean.
 * 
 * @author Garikapati Mangaiah.
 */
@ManagedBean(name = "evaluationMB")
@SessionScoped
public class EvaluationManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	/** The sc qt meetings. */
	private List<ScQtMeeting> scQtMeetings;

	/** The meeting multi select. */
	private List<ScQtMeeting> meetingMultiSelect;

	/** The committee member list. */
	private List<ScQtCommitteeActivity> committeeMemberList;

	/** The committee activitymulti select. */
	private List<ScQtCommitteeActivity> committeeActivitymultiSelect;

	/** The qualified list. */
	private List<ScQtEvaluationResult> qualifiedList;

	/** The dis qualified list. */
	private List<ScQtEvaluationResult> disQualifiedList;

	/** The sc qt evaluation result. */
	private ScQtEvaluationResult scQtEvaluationResult;

	/** The tab active index. */
	private int tabActiveIndex;

	/** The commitee activity. */
	private ScQtCommitteeActivity commiteeActivity;

	// OC,EC,TEC,FEC
	/** The committee type. */
	private String committeeType;

	/** The is demo. */
	private boolean isDemo;

	/** The is evaluate status. */
	private boolean isEvaluateStatus;

	/** The evaluation result id. */
	private Long evaluationResultId;

	/** The temp tab. */
	private int tempTab;

	/** The spec master. */
	private TplSpecMaster specMaster;

	/** The result. */
	private int result = 1;

	/** The view by. */
	private String viewBy = "P";

	/** The meeting attendance list. */
	private List<ScQtMeetingAttendance> meetingAttendanceList;

	/** The meeting attendance select list. */
	private List<ScQtMeetingAttendance> meetingAttendanceSelectList;

	/** The compliance technical check list. */
	private List<ScQtCheckList> complianceTechnicalCheckList;

	/** The compliancefinancial check list. */
	private List<ScQtCheckList> compliancefinancialCheckList;

	/** The sc qt checklist. */
	private ScQtCheckList scQtChecklist;

	/** The qt proposal checklist. */
	private ScQtProposalChecklist qtProposalChecklist;

	/** The sc qt. */
	private ScQt scQt;

	/** The count days. */
	private int countDays;

	/** The compliance technical proposal check list. */
	private List<ScQtProposalChecklist> complianceTechnicalProposalCheckList;

	/** The sc qt committee. */
	private ScQtCommittee scQtCommittee;

	/** The qt committee remarks. */
	private List<ScQtCommitteRemarks> qtCommitteeRemarks;

	/** The compliance active index. */
	private int complianceActiveIndex;

	/** The prepare active index. */
	private int prepareActiveIndex;

	/** The presentation demo active index. */
	private int presentationDemoActiveIndex;

	/** The compliance sample lblsupplier. */
	private List<ScQtSampleLable> complianceSampleLblsupplier;

	/** The sc qt sample lbl. */
	private ScQtSampleLable scQtSampleLbl;

	/** The compliance sample item list. */
	private List<ScQtSampleItem> complianceSampleItemList;

	/** The evaluatio stage active index. */
	private int evaluatioStageActiveIndex;

	/** The evaluation stg technical check list. */
	private List<ScQtCheckList> evaluationStgTechnicalCheckList;

	/** The evaluation stgfinancial check list. */
	private List<ScQtCheckList> evaluationStgfinancialCheckList;

	/** The summary view by. */
	private String summaryViewBy;

	/** The technical gov no of contracts. */
	private List<ScQtCheckListScore> technicalGovNoOfContracts;

	/** The technical gov no of years. */
	private List<ScQtCheckListScore> technicalGovNoOfYears;

	/** The checklist score type. */
	private String checklistScoreType;

	/** The company score type. */
	private String companyScoreType;

	/** The technical contract user score. */
	private ScQtCheckListScore technicalContractUserScore;

	/** The technical no of years user score. */
	private ScQtCheckListScore technicalNoOfYearsUserScore;

	/** The evaluation compliance list. */
	private List<ScQtProposalChecklist> evaluationComplianceList;

	/** The spec item list. */
	private List<TplSpecQuestion> specItemList;

	/** The prp company experience list. */
	private List<ScQtPRPExperience> prpCompanyExperienceList;

	/** The loa gov company exp list. */
	private List<ScLoa> loaGovCompanyExpList;

	/** The sample item list. */
	private List<ScQtSampleItem> sampleItemList;

	/** The no of employee list. */
	private ScQtPRPEmployee noOfEmployeeList;

	/** The cash on hand list. */
	private List<ScQtPRPCashOnHand> cashOnHandList;

	/** The credit facility list. */
	private List<ScQtPRPCreditFacility> creditFacilityList;

	/** The bank stmt list. */
	private List<ScQtPRPBankStmt> bankStmtList;

	/** The asset list. */
	private List<ScQtPRPAssetLiability> assetList;

	/** The tech gov proposal score. */
	private List<ScQtProposalScore> techGovProposalScore;

	/** The evaluation summary qual suppliers. */
	private IPagingDataModel<ScQtEvaluationResultDtl> qualifiedSuppliersDM;

	/** The evaluation summary dis qual suppliers. */
	private IPagingDataModel<ScQtEvaluationResultDtl> disqualifiedSuppliersDM;

	/** The summary item list. */
	private List<ScRequestItem> summaryItemList;

	/** The singlesummary item list. */
	private ScRequestItem singlesummaryItemList;

	/** The tpl spec zone dtls. */
	private List<TplSpecZoneDtl> tplSpecZoneDtls;

	/** The selected zone. */
	private TplSpecZoneDtl selectedZone;

	/** The presentation demo supplier list. */
	private List<ScQtProposalChecklist> presentationDemoSupplierList;

	/** The fin spec supplier list. */
	private List<ScQtProposalChecklist> finSpecSupplierList;

	/** The offer price package. */
	private ScQtScoreOfferPrice offerPricePackage;

	/** The offer price item. */
	private ScQtScoreOfferPrice offerPriceItem;

	/** The endorscommittee member list. */
	private List<ScQtCommitteeActivity> endorscommitteeMemberList;

	/** The message. */
	private String message;

	/** The subject. */
	private String subject;

	/** The generate evaluation button. */
	private boolean generateEvaluationButton = false;

	/** The compliance stage hide. */
	private boolean complianceStageHide = false;

	/** The compliance summary count. */
	private int complianceSummaryCount = 0;

	/** The sc qt prp asset liability. */
	private ScQtPRPAssetLiability scQtPRPAssetLiability;

	/** The bank total amt. */
	private BigDecimal bankTotalAmt;

	/** The template spec bean. */
	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();

	/** The sc qt evaluation report list. */
	private List<ScQtEvaluationReport> scQtEvaluationReportList;

	/** The selected sc qt evaluation reports. */
	private List<ScQtEvaluationReport> selectedScQtEvaluationReports;

	/** The sc qt evaluation report. */
	private ScQtEvaluationReport scQtEvaluationReport;

	/** The report header. */
	private String reportHeader;

	/** The sm personnels list. */
	private List<TechnicalStaffBean> smPersonnelsList;

	/** The total no of years experience. */
	private int totalNoOfYearsExperience;

	/** The meeting remove list. */
	private List<ScQtMeeting> meetingRemoveList;

	/** The meeting attendance remove list. */
	private List<ScQtMeetingAttendance> meetingAttendanceRemoveList;
	// Added by vidyasagarreddy for reusing commmon screens from proposal
	/** The evaluation page. */
	private String evaluationPage;

	/** The proposal page. */
	private String proposalPage;

	/** The doc definition key. */
	private String docDefinitionKey;

	/** The technical complinace list. */
	private List<ScQtProposalChecklist> technicalComplinaceList;

	/** The is status hide. */
	private boolean isStatusHide;

	/** The no of contracts proposal score. */
	private ScQtProposalScore noOfContractsProposalScore;

	/** The no ofyears proposal score. */
	private ScQtProposalScore noOfyearsProposalScore;

	/** The spec zone tab index. */
	private int specZoneTabIndex;

	/** The evaluationtechnical table row. */
	private int evaluationtechnicalTableRow;

	/** The evaluationfinancial table row. */
	private int evaluationfinancialTableRow;

	/** The evaluationsample table row. */
	private int evaluationsampleTableRow;

	/** The spec zone dtls. */
	private List<TplSpecZoneDtl> specZoneDtls;

	/** The local out come. */
	private String localOutCome;

	/** The read only. */
	private boolean readOnly;

	/** The member only. */
	private boolean memberOnly;

	/** The evaluation sign off remarks. */
	private List<ScQtCommitteRemarks> evaluationSignOffRemarks;

	/** The today. */
	private Date today = new Date();

	/** The user scores. */
	private String userScores;

	/** The contract scores. */
	private String contractScores;

	/** The techindex. */
	private int techindex = -1;

	/** The status only. */
	private boolean statusOnly;

	/** The sc attachment. */
	private ScAttachment scAttachment;

	/** The sc remark. */
	private List<ScRemark> scRemark;

	/** The evaluation committee remark. */
	private List<ScQtEvaluationCommitteeRemark> evaluationCommitteeRemark;

	/** The evaluation opening committee remark. */
	private List<ScQtOpeningCommitteeRemark> evaluationOpeningCommitteeRemark;

	/** The evaluation technical committee remark. */
	private List<ScQtTechnicalCommitteeRemark> evaluationTechnicalCommitteeRemark;

	/** The evaluation financial committee remark. */
	private List<ScQtFinancialCommitteeRemark> evaluationFinancialCommitteeRemark;

	/** The with in varieance. */
	private BigDecimal withInVarieance;

	/** The check list type. */
	private String checkListType;

	/** The evaluation stage zones summary. */
	private List<TplSpecZoneDtl> evaluationStageZonesSummary;

	/** The compliance only. */
	private boolean complianceOnly;

	/** The evaluation stage status hide. */
	private boolean evaluationStageStatusHide = true;

	/** The spec item scoring list. */
	private List<TplSpecScoring> specItemScoringList;

	/** The compliance check list index. */
	private int complianceCheckListIndex = -1;

	/** The evaluation check list type. */
	private String evaluationCheckListType;

	/** The evaluation check list index. */
	private int evaluationCheckListIndex = -1;

	/** The evaluation technical financial type. */
	private String evaluationTechnicalFinancialType;

	/** The package from varieance. */
	private BigDecimal packageFromVarieance;

	/** The package to varieance. */
	private BigDecimal packageToVarieance;

	// MenuModel
	private MenuModel evaluationSpecZonalMenuModel;

	/** check Qualified suppliers. */
	private boolean checkQualifiedSuppliers = true;

	/** report download filename. */
	private String reportDownloadFileName;

	/** check report download. */
	private boolean checkReportDownload;

	private BigDecimal packageScore;

	/** passing score */
	private int passingScore;

	private String financialSummaryView;

	private String roleCode;

	private int oneTierFinancialActiveIndex;
	private BigDecimal finSpecZoneEstimatedPrice;
	private List<TplSpecQuestion> sampleItemSpecifications;
	private String itemName;
	private ScQtSampleItem sampleItemSelection;
	private boolean checkTechnicalQualifiedOpenPopup;
	private boolean checkFinancialQualifiedOpenPopup;
	private boolean complianceSummaryGenerated;
	private boolean technicalSummaryGenerated;
	private boolean presentationDemoSummaryGenerated;
	private boolean financialSummaryGenerated;
	private IPagingDataModel<ScQtProposalChecklist> complianceTechnicalProposalCheckListDM;
	private IPagingDataModel<ScQtProposalChecklist> finSpecSupplierListDM;
	private IPagingDataModel<ScQtSampleItem> complianceSampleItemListDM;
	private IPagingDataModel<ScQtSampleLable> complianceSampleLblsupplierDM;
	private IPagingDataModel<ScQtProposalChecklist> presentationDemoSupplierListDM;
	private int demoPassingScore;
	private boolean isProfileHide = false;
	private int currentTime=0;
	private boolean isComplianceReEvaluateRequired;
	private boolean isTechnicalOrFinancialReEvaluateRequired;
	private String userScoreSchemaType;
	private String dummyScoreType;
	
	private ScQtEvaluationReport selectedReport;
	
	private String encryptionKey;
	
	private List<ScQtProposalChecklistAttachment> toBeRemovedProposalAttachmentsChecklists;
	
	/** The auto update. */
	private boolean autoUpdate = Boolean.TRUE;
	/**
	 * Gets the today.
	 * 
	 * @return the today
	 */
	public Date getToday() {
		return today;
	}

	/**
	 * Sets the today.
	 * 
	 * @param today
	 *            the new today
	 */
	public void setToday(Date today) {
		this.today = today;
	}

	/**
	 * Gets the evaluation sign off remarks.
	 * 
	 * @return the evaluation sign off remarks
	 */
	public List<ScQtCommitteRemarks> getEvaluationSignOffRemarks() {
		return evaluationSignOffRemarks;
	}

	/**
	 * Sets the evaluation sign off remarks.
	 * 
	 * @param evaluationSignOffRemarks
	 *            the new evaluation sign off remarks
	 */
	public void setEvaluationSignOffRemarks(
			List<ScQtCommitteRemarks> evaluationSignOffRemarks) {
		this.evaluationSignOffRemarks = evaluationSignOffRemarks;
	}

	/**
	 * Checks if is read only.
	 * 
	 * @return true, if is read only
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * Checks if is member only.
	 * 
	 * @return true, if is member only
	 */
	public boolean isMemberOnly() {
		return memberOnly;
	}

	/**
	 * Sets the member only.
	 * 
	 * @param memberOnly
	 *            the new member only
	 */
	public void setMemberOnly(boolean memberOnly) {
		this.memberOnly = memberOnly;
	}

	/**
	 * Sets the read only.
	 * 
	 * @param readOnly
	 *            the new read only
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Gets the local out come.
	 * 
	 * @return the local out come
	 */
	public String getLocalOutCome() {
		return localOutCome;
	}

	/**
	 * Sets the local out come.
	 * 
	 * @param localOutCome
	 *            the new local out come
	 */
	public void setLocalOutCome(String localOutCome) {
		this.localOutCome = localOutCome;
	}

	/**
	 * Gets the spec zone dtls.
	 * 
	 * @return the spec zone dtls
	 */
	public List<TplSpecZoneDtl> getSpecZoneDtls() {
		return specZoneDtls;
	}

	/**
	 * Sets the spec zone dtls.
	 * 
	 * @param specZoneDtls
	 *            the new spec zone dtls
	 */
	public void setSpecZoneDtls(List<TplSpecZoneDtl> specZoneDtls) {
		this.specZoneDtls = specZoneDtls;
	}

	/**
	 * Gets the evaluationsample table row.
	 * 
	 * @return the evaluationsample table row
	 */
	public int getEvaluationsampleTableRow() {
		return evaluationsampleTableRow;
	}

	/**
	 * Sets the evaluationsample table row.
	 * 
	 * @param evaluationsampleTableRow
	 *            the new evaluationsample table row
	 */
	public void setEvaluationsampleTableRow(int evaluationsampleTableRow) {
		this.evaluationsampleTableRow = evaluationsampleTableRow;
	}

	/**
	 * Gets the evaluationtechnical table row.
	 * 
	 * @return the evaluationtechnical table row
	 */
	public int getEvaluationtechnicalTableRow() {
		return evaluationtechnicalTableRow;
	}

	/**
	 * Sets the evaluationtechnical table row.
	 * 
	 * @param evaluationtechnicalTableRow
	 *            the new evaluationtechnical table row
	 */
	public void setEvaluationtechnicalTableRow(int evaluationtechnicalTableRow) {
		this.evaluationtechnicalTableRow = evaluationtechnicalTableRow;
	}

	/**
	 * Gets the evaluationfinancial table row.
	 * 
	 * @return the evaluationfinancial table row
	 */
	public int getEvaluationfinancialTableRow() {
		return evaluationfinancialTableRow;
	}

	/**
	 * Sets the evaluationfinancial table row.
	 * 
	 * @param evaluationfinancialTableRow
	 *            the new evaluationfinancial table row
	 */
	public void setEvaluationfinancialTableRow(int evaluationfinancialTableRow) {
		this.evaluationfinancialTableRow = evaluationfinancialTableRow;
	}

	/**
	 * Gets the spec zone tab index.
	 * 
	 * @return the spec zone tab index
	 */
	public int getSpecZoneTabIndex() {
		return specZoneTabIndex;
	}

	/**
	 * Sets the spec zone tab index.
	 * 
	 * @param specZoneTabIndex
	 *            the new spec zone tab index
	 */
	public void setSpecZoneTabIndex(int specZoneTabIndex) {
		this.specZoneTabIndex = specZoneTabIndex;
	}

	/**
	 * Gets the no of contracts proposal score.
	 * 
	 * @return the no of contracts proposal score
	 */
	public ScQtProposalScore getNoOfContractsProposalScore() {
		return noOfContractsProposalScore;
	}

	/**
	 * Sets the no of contracts proposal score.
	 * 
	 * @param noOfContractsProposalScore
	 *            the new no of contracts proposal score
	 */
	public void setNoOfContractsProposalScore(
			ScQtProposalScore noOfContractsProposalScore) {
		this.noOfContractsProposalScore = noOfContractsProposalScore;
	}

	/**
	 * Gets the no ofyears proposal score.
	 * 
	 * @return the no ofyears proposal score
	 */
	public ScQtProposalScore getNoOfyearsProposalScore() {
		return noOfyearsProposalScore;
	}

	/**
	 * Sets the no ofyears proposal score.
	 * 
	 * @param noOfyearsProposalScore
	 *            the new no ofyears proposal score
	 */
	public void setNoOfyearsProposalScore(
			ScQtProposalScore noOfyearsProposalScore) {
		this.noOfyearsProposalScore = noOfyearsProposalScore;
	}

	/**
	 * Checks if is status hide.
	 * 
	 * @return true, if is status hide
	 */
	public boolean isStatusHide() {
		return isStatusHide;
	}

	/**
	 * Sets the status hide.
	 * 
	 * @param isStatusHide
	 *            the new status hide
	 */
	public void setStatusHide(boolean isStatusHide) {
		this.isStatusHide = isStatusHide;
	}

	/**
	 * Gets the technical complinace list.
	 * 
	 * @return the technical complinace list
	 */
	public List<ScQtProposalChecklist> getTechnicalComplinaceList() {
		return technicalComplinaceList;
	}

	/**
	 * Sets the technical complinace list.
	 * 
	 * @param technicalComplinaceList
	 *            the new technical complinace list
	 */
	public void setTechnicalComplinaceList(
			List<ScQtProposalChecklist> technicalComplinaceList) {
		this.technicalComplinaceList = technicalComplinaceList;
	}

	/**
	 * Gets the meeting remove list.
	 * 
	 * @return the meeting remove list
	 */
	public List<ScQtMeeting> getMeetingRemoveList() {
		return meetingRemoveList;
	}

	/**
	 * Sets the meeting remove list.
	 * 
	 * @param meetingRemoveList
	 *            the new meeting remove list
	 */
	public void setMeetingRemoveList(List<ScQtMeeting> meetingRemoveList) {
		this.meetingRemoveList = meetingRemoveList;
	}

	/**
	 * Gets the meeting attendance remove list.
	 * 
	 * @return the meeting attendance remove list
	 */
	public List<ScQtMeetingAttendance> getMeetingAttendanceRemoveList() {
		return meetingAttendanceRemoveList;
	}

	/**
	 * Sets the meeting attendance remove list.
	 * 
	 * @param meetingAttendanceRemoveList
	 *            the new meeting attendance remove list
	 */
	public void setMeetingAttendanceRemoveList(
			List<ScQtMeetingAttendance> meetingAttendanceRemoveList) {
		this.meetingAttendanceRemoveList = meetingAttendanceRemoveList;
	}

	// private String stage1committeeType="OC";
	/**
	 * Instantiates a new evaluation managed bean.
	 */
	public EvaluationManagedBean() {

	}

	/**
	 * Gets the sc qt meetings.
	 * 
	 * @return the sc qt meetings
	 */
	public List<ScQtMeeting> getScQtMeetings() {
		return scQtMeetings;
	}

	/**
	 * Sets the sc qt meetings.
	 * 
	 * @param scQtMeetings
	 *            the new sc qt meetings
	 */
	public void setScQtMeetings(List<ScQtMeeting> scQtMeetings) {
		this.scQtMeetings = scQtMeetings;
	}

	/**
	 * Sets the meeting multi select.
	 * 
	 * @param meetingMultiSelect
	 *            the new meeting multi select
	 */
	public void setMeetingMultiSelect(List<ScQtMeeting> meetingMultiSelect) {
		this.meetingMultiSelect = meetingMultiSelect;
	}

	/**
	 * Gets the meeting multi select.
	 * 
	 * @return the meeting multi select
	 */
	public List<ScQtMeeting> getMeetingMultiSelect() {
		return meetingMultiSelect;
	}

	/**
	 * Gets the tab active index.
	 * 
	 * @return the tab active index
	 */
	public int getTabActiveIndex() {
		return tabActiveIndex;
	}

	/**
	 * Sets the tab active index.
	 * 
	 * @param tabActiveIndex
	 *            the new tab active index
	 */
	public void setTabActiveIndex(int tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	/**
	 * Gets the committee member list.
	 * 
	 * @return the committee member list
	 */
	public List<ScQtCommitteeActivity> getCommitteeMemberList() {
		return committeeMemberList;
	}

	/**
	 * Sets the committee member list.
	 * 
	 * @param committeeMemberList
	 *            the new committee member list
	 */
	public void setCommitteeMemberList(
			List<ScQtCommitteeActivity> committeeMemberList) {
		this.committeeMemberList = committeeMemberList;
	}

	/**
	 * Gets the committee activitymulti select.
	 * 
	 * @return the committee activitymulti select
	 */
	public List<ScQtCommitteeActivity> getCommitteeActivitymultiSelect() {
		return committeeActivitymultiSelect;
	}

	/**
	 * Sets the committee activitymulti select.
	 * 
	 * @param committeeActivitymultiSelect
	 *            the new committee activitymulti select
	 */
	public void setCommitteeActivitymultiSelect(
			List<ScQtCommitteeActivity> committeeActivitymultiSelect) {
		this.committeeActivitymultiSelect = committeeActivitymultiSelect;
	}

	/**
	 * Gets the sc qt evaluation result.
	 * 
	 * @return the sc qt evaluation result
	 */
	public ScQtEvaluationResult getScQtEvaluationResult() {
		return scQtEvaluationResult;
	}

	/**
	 * Sets the sc qt evaluation result.
	 * 
	 * @param scQtEvaluationResult
	 *            the new sc qt evaluation result
	 */
	public void setScQtEvaluationResult(
			ScQtEvaluationResult scQtEvaluationResult) {
		this.scQtEvaluationResult = scQtEvaluationResult;
	}

	/**
	 * Gets the commitee activity.
	 * 
	 * @return the commitee activity
	 */
	public ScQtCommitteeActivity getCommiteeActivity() {
		return commiteeActivity;
	}

	/**
	 * Sets the commitee activity.
	 * 
	 * @param commiteeActivity
	 *            the new commitee activity
	 */
	public void setCommiteeActivity(ScQtCommitteeActivity commiteeActivity) {
		this.commiteeActivity = commiteeActivity;
	}

	/**
	 * Gets the committee type.
	 * 
	 * @return the committee type
	 */
	public String getCommitteeType() {
		return committeeType;
	}

	/**
	 * Sets the committee type.
	 * 
	 * @param committeeType
	 *            the new committee type
	 */
	public void setCommitteeType(String committeeType) {
		this.committeeType = committeeType;
	}

	/**
	 * Checks if is evaluate status.
	 * 
	 * @return true, if is evaluate status
	 */
	public boolean isEvaluateStatus() {
		return isEvaluateStatus;
	}

	/**
	 * Sets the evaluate status.
	 * 
	 * @param isEvaluateStatus
	 *            the new evaluate status
	 */
	public void setEvaluateStatus(boolean isEvaluateStatus) {
		this.isEvaluateStatus = isEvaluateStatus;
	}

	/**
	 * Gets the temp tab.
	 * 
	 * @return the temp tab
	 */
	public int getTempTab() {
		return tempTab;
	}

	/**
	 * Sets the temp tab.
	 * 
	 * @param tempTab
	 *            the new temp tab
	 */
	public void setTempTab(int tempTab) {
		this.tempTab = tempTab;
	}

	/**
	 * Gets the evaluation result id.
	 * 
	 * @return the evaluation result id
	 */
	public Long getEvaluationResultId() {
		return evaluationResultId;
	}

	/**
	 * Sets the evaluation result id.
	 * 
	 * @param evaluationResultId
	 *            the new evaluation result id
	 */
	public void setEvaluationResultId(Long evaluationResultId) {
		this.evaluationResultId = evaluationResultId;
	}

	/**
	 * Gets the qualified list.
	 * 
	 * @return the qualified list
	 */
	public List<ScQtEvaluationResult> getQualifiedList() {
		return qualifiedList;
	}

	/**
	 * Sets the qualified list.
	 * 
	 * @param qualifiedList
	 *            the new qualified list
	 */
	public void setQualifiedList(List<ScQtEvaluationResult> qualifiedList) {
		this.qualifiedList = qualifiedList;
	}

	/**
	 * Gets the dis qualified list.
	 * 
	 * @return the dis qualified list
	 */
	public List<ScQtEvaluationResult> getDisQualifiedList() {
		return disQualifiedList;
	}

	/**
	 * Sets the dis qualified list.
	 * 
	 * @param disQualifiedList
	 *            the new dis qualified list
	 */
	public void setDisQualifiedList(List<ScQtEvaluationResult> disQualifiedList) {
		this.disQualifiedList = disQualifiedList;
	}

	/**
	 * Gets the spec master.
	 * 
	 * @return the spec master
	 */
	public TplSpecMaster getSpecMaster() {
		return specMaster;
	}

	/**
	 * Sets the spec master.
	 * 
	 * @param specMaster
	 *            the new spec master
	 */
	public void setSpecMaster(TplSpecMaster specMaster) {
		this.specMaster = specMaster;
	}

	/**
	 * Gets the result.
	 * 
	 * @return the result
	 */
	public int getResult() {
		return result;
	}

	/**
	 * Sets the result.
	 * 
	 * @param result
	 *            the new result
	 */
	public void setResult(int result) {
		this.result = result;
	}

	/**
	 * Gets the view by.
	 * 
	 * @return the view by
	 */
	public String getViewBy() {
		return viewBy;
	}

	/**
	 * Sets the view by.
	 * 
	 * @param viewBy
	 *            the new view by
	 */
	public void setViewBy(String viewBy) {
		this.viewBy = viewBy;
	}

	/**
	 * Gets the template spec bean.
	 * 
	 * @return the template spec bean
	 */
	public TemplateSpecBean getTemplateSpecBean() {
		return templateSpecBean;
	}

	/**
	 * Sets the template spec bean.
	 * 
	 * @param templateSpecBean
	 *            the new template spec bean
	 */
	public void setTemplateSpecBean(TemplateSpecBean templateSpecBean) {
		this.templateSpecBean = templateSpecBean;
	}

	/**
	 * Gets the meeting attendance list.
	 * 
	 * @return the meeting attendance list
	 */
	public List<ScQtMeetingAttendance> getMeetingAttendanceList() {
		return meetingAttendanceList;
	}

	/**
	 * Sets the meeting attendance list.
	 * 
	 * @param meetingAttendanceList
	 *            the new meeting attendance list
	 */
	public void setMeetingAttendanceList(
			List<ScQtMeetingAttendance> meetingAttendanceList) {
		this.meetingAttendanceList = meetingAttendanceList;
	}

	/**
	 * Gets the meeting attendance select list.
	 * 
	 * @return the meeting attendance select list
	 */
	public List<ScQtMeetingAttendance> getMeetingAttendanceSelectList() {
		return meetingAttendanceSelectList;
	}

	/**
	 * Sets the meeting attendance select list.
	 * 
	 * @param meetingAttendanceSelectList
	 *            the new meeting attendance select list
	 */
	public void setMeetingAttendanceSelectList(
			List<ScQtMeetingAttendance> meetingAttendanceSelectList) {
		this.meetingAttendanceSelectList = meetingAttendanceSelectList;
	}

	/**
	 * Gets the compliance technical check list.
	 * 
	 * @return the compliance technical check list
	 */
	public List<ScQtCheckList> getComplianceTechnicalCheckList() {
		return complianceTechnicalCheckList;
	}

	/**
	 * Sets the compliance technical check list.
	 * 
	 * @param complianceTechnicalCheckList
	 *            the new compliance technical check list
	 */
	public void setComplianceTechnicalCheckList(
			List<ScQtCheckList> complianceTechnicalCheckList) {
		this.complianceTechnicalCheckList = complianceTechnicalCheckList;
	}

	/**
	 * Gets the compliancefinancial check list.
	 * 
	 * @return the compliancefinancial check list
	 */
	public List<ScQtCheckList> getCompliancefinancialCheckList() {
		return compliancefinancialCheckList;
	}

	/**
	 * Sets the compliancefinancial check list.
	 * 
	 * @param compliancefinancialCheckList
	 *            the new compliancefinancial check list
	 */
	public void setCompliancefinancialCheckList(
			List<ScQtCheckList> compliancefinancialCheckList) {
		this.compliancefinancialCheckList = compliancefinancialCheckList;
	}

	/**
	 * Gets the sc qt checklist.
	 * 
	 * @return the sc qt checklist
	 */
	public ScQtCheckList getScQtChecklist() {
		return scQtChecklist;
	}

	/**
	 * Sets the sc qt checklist.
	 * 
	 * @param scQtChecklist
	 *            the new sc qt checklist
	 */
	public void setScQtChecklist(ScQtCheckList scQtChecklist) {
		this.scQtChecklist = scQtChecklist;
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
	 * Gets the count days.
	 * 
	 * @return the count days
	 */
	public int getCountDays() {
		return countDays;
	}

	/**
	 * Sets the count days.
	 * 
	 * @param countDays
	 *            the new count days
	 */
	public void setCountDays(int countDays) {
		this.countDays = countDays;
	}

	/**
	 * Gets the compliance technical proposal check list.
	 * 
	 * @return the compliance technical proposal check list
	 */
	public List<ScQtProposalChecklist> getComplianceTechnicalProposalCheckList() {
		return complianceTechnicalProposalCheckList;
	}

	/**
	 * Sets the compliance technical proposal check list.
	 * 
	 * @param complianceTechnicalProposalCheckList
	 *            the new compliance technical proposal check list
	 */
	public void setComplianceTechnicalProposalCheckList(
			List<ScQtProposalChecklist> complianceTechnicalProposalCheckList) {
		this.complianceTechnicalProposalCheckList = complianceTechnicalProposalCheckList;
	}

	/**
	 * Gets the sc qt committee.
	 * 
	 * @return the sc qt committee
	 */
	public ScQtCommittee getScQtCommittee() {
		return scQtCommittee;
	}

	/**
	 * Sets the sc qt committee.
	 * 
	 * @param scQtCommittee
	 *            the new sc qt committee
	 */
	public void setScQtCommittee(ScQtCommittee scQtCommittee) {
		this.scQtCommittee = scQtCommittee;
	}

	/**
	 * Gets the qt committee remarks.
	 * 
	 * @return the qt committee remarks
	 */
	public List<ScQtCommitteRemarks> getQtCommitteeRemarks() {
		return qtCommitteeRemarks;
	}

	/**
	 * Sets the qt committee remarks.
	 * 
	 * @param qtCommitteeRemarks
	 *            the new qt committee remarks
	 */
	public void setQtCommitteeRemarks(
			List<ScQtCommitteRemarks> qtCommitteeRemarks) {
		this.qtCommitteeRemarks = qtCommitteeRemarks;
	}

	/**
	 * Gets the compliance active index.
	 * 
	 * @return the compliance active index
	 */
	public int getComplianceActiveIndex() {
		return complianceActiveIndex;
	}

	/**
	 * Sets the compliance active index.
	 * 
	 * @param complianceActiveIndex
	 *            the new compliance active index
	 */
	public void setComplianceActiveIndex(int complianceActiveIndex) {
		this.complianceActiveIndex = complianceActiveIndex;
	}

	/**
	 * Gets the prepare active index.
	 * 
	 * @return the prepare active index
	 */
	public int getPrepareActiveIndex() {
		return prepareActiveIndex;
	}

	/**
	 * Sets the prepare active index.
	 * 
	 * @param prepareActiveIndex
	 *            the new prepare active index
	 */
	public void setPrepareActiveIndex(int prepareActiveIndex) {
		this.prepareActiveIndex = prepareActiveIndex;
	}

	/**
	 * Gets the compliance sample lblsupplier.
	 * 
	 * @return the compliance sample lblsupplier
	 */
	public List<ScQtSampleLable> getComplianceSampleLblsupplier() {
		return complianceSampleLblsupplier;
	}

	/**
	 * Sets the compliance sample lblsupplier.
	 * 
	 * @param complianceSampleLblsupplier
	 *            the new compliance sample lblsupplier
	 */
	public void setComplianceSampleLblsupplier(
			List<ScQtSampleLable> complianceSampleLblsupplier) {
		this.complianceSampleLblsupplier = complianceSampleLblsupplier;
	}

	/**
	 * Gets the sc qt sample lbl.
	 * 
	 * @return the sc qt sample lbl
	 */
	public ScQtSampleLable getScQtSampleLbl() {
		return scQtSampleLbl;
	}

	/**
	 * Sets the sc qt sample lbl.
	 * 
	 * @param scQtSampleLbl
	 *            the new sc qt sample lbl
	 */
	public void setScQtSampleLbl(ScQtSampleLable scQtSampleLbl) {
		this.scQtSampleLbl = scQtSampleLbl;
	}

	/**
	 * Gets the compliance sample item list.
	 * 
	 * @return the compliance sample item list
	 */
	public List<ScQtSampleItem> getComplianceSampleItemList() {
		return complianceSampleItemList;
	}

	/**
	 * Sets the compliance sample item list.
	 * 
	 * @param complianceSampleItemList
	 *            the new compliance sample item list
	 */
	public void setComplianceSampleItemList(
			List<ScQtSampleItem> complianceSampleItemList) {
		this.complianceSampleItemList = complianceSampleItemList;
	}

	/**
	 * Gets the evaluatio stage active index.
	 * 
	 * @return the evaluatio stage active index
	 */
	public int getEvaluatioStageActiveIndex() {
		return evaluatioStageActiveIndex;
	}

	/**
	 * Sets the evaluatio stage active index.
	 * 
	 * @param evaluatioStageActiveIndex
	 *            the new evaluatio stage active index
	 */
	public void setEvaluatioStageActiveIndex(int evaluatioStageActiveIndex) {
		this.evaluatioStageActiveIndex = evaluatioStageActiveIndex;
	}

	/**
	 * Gets the summary view by.
	 * 
	 * @return the summary view by
	 */
	public String getSummaryViewBy() {
		return summaryViewBy;
	}

	/**
	 * Sets the summary view by.
	 * 
	 * @param summaryViewBy
	 *            the new summary view by
	 */
	public void setSummaryViewBy(String summaryViewBy) {
		this.summaryViewBy = summaryViewBy;
	}

	/**
	 * Gets the qt proposal checklist.
	 * 
	 * @return the qt proposal checklist
	 */
	public ScQtProposalChecklist getQtProposalChecklist() {
		return qtProposalChecklist;
	}

	/**
	 * Sets the qt proposal checklist.
	 * 
	 * @param qtProposalChecklist
	 *            the new qt proposal checklist
	 */
	public void setQtProposalChecklist(ScQtProposalChecklist qtProposalChecklist) {
		this.qtProposalChecklist = qtProposalChecklist;
	}

	/**
	 * Gets the evaluation stg technical check list.
	 * 
	 * @return the evaluation stg technical check list
	 */
	public List<ScQtCheckList> getEvaluationStgTechnicalCheckList() {
		return evaluationStgTechnicalCheckList;
	}

	/**
	 * Sets the evaluation stg technical check list.
	 * 
	 * @param evaluationStgTechnicalCheckList
	 *            the new evaluation stg technical check list
	 */
	public void setEvaluationStgTechnicalCheckList(
			List<ScQtCheckList> evaluationStgTechnicalCheckList) {
		this.evaluationStgTechnicalCheckList = evaluationStgTechnicalCheckList;
	}

	/**
	 * Gets the evaluation stgfinancial check list.
	 * 
	 * @return the evaluation stgfinancial check list
	 */
	public List<ScQtCheckList> getEvaluationStgfinancialCheckList() {
		return evaluationStgfinancialCheckList;
	}

	/**
	 * Sets the evaluation stgfinancial check list.
	 * 
	 * @param evaluationStgfinancialCheckList
	 *            the new evaluation stgfinancial check list
	 */
	public void setEvaluationStgfinancialCheckList(
			List<ScQtCheckList> evaluationStgfinancialCheckList) {
		this.evaluationStgfinancialCheckList = evaluationStgfinancialCheckList;
	}

	/**
	 * Gets the checklist score type.
	 * 
	 * @return the checklist score type
	 */
	public String getChecklistScoreType() {
		return checklistScoreType;
	}

	/**
	 * Sets the checklist score type.
	 * 
	 * @param checklistScoreType
	 *            the new checklist score type
	 */
	public void setChecklistScoreType(String checklistScoreType) {
		this.checklistScoreType = checklistScoreType;
	}

	/**
	 * Gets the technical contract user score.
	 * 
	 * @return the technical contract user score
	 */
	public ScQtCheckListScore getTechnicalContractUserScore() {
		return technicalContractUserScore;
	}

	/**
	 * Sets the technical contract user score.
	 * 
	 * @param technicalContractUserScore
	 *            the new technical contract user score
	 */
	public void setTechnicalContractUserScore(
			ScQtCheckListScore technicalContractUserScore) {
		this.technicalContractUserScore = technicalContractUserScore;
	}

	/**
	 * Gets the technical no of years user score.
	 * 
	 * @return the technical no of years user score
	 */
	public ScQtCheckListScore getTechnicalNoOfYearsUserScore() {
		return technicalNoOfYearsUserScore;
	}

	/**
	 * Sets the technical no of years user score.
	 * 
	 * @param technicalNoOfYearsUserScore
	 *            the new technical no of years user score
	 */
	public void setTechnicalNoOfYearsUserScore(
			ScQtCheckListScore technicalNoOfYearsUserScore) {
		this.technicalNoOfYearsUserScore = technicalNoOfYearsUserScore;
	}

	/**
	 * Gets the company score type.
	 * 
	 * @return the company score type
	 */
	public String getCompanyScoreType() {
		return companyScoreType;
	}

	/**
	 * Sets the company score type.
	 * 
	 * @param companyScoreType
	 *            the new company score type
	 */
	public void setCompanyScoreType(String companyScoreType) {
		this.companyScoreType = companyScoreType;
	}

	/**
	 * Gets the technical gov no of contracts.
	 * 
	 * @return the technical gov no of contracts
	 */
	public List<ScQtCheckListScore> getTechnicalGovNoOfContracts() {
		return technicalGovNoOfContracts;
	}

	/**
	 * Sets the technical gov no of contracts.
	 * 
	 * @param technicalGovNoOfContracts
	 *            the new technical gov no of contracts
	 */
	public void setTechnicalGovNoOfContracts(
			List<ScQtCheckListScore> technicalGovNoOfContracts) {
		this.technicalGovNoOfContracts = technicalGovNoOfContracts;
	}

	/**
	 * Gets the technical gov no of years.
	 * 
	 * @return the technical gov no of years
	 */
	public List<ScQtCheckListScore> getTechnicalGovNoOfYears() {
		return technicalGovNoOfYears;
	}

	/**
	 * Sets the technical gov no of years.
	 * 
	 * @param technicalGovNoOfYears
	 *            the new technical gov no of years
	 */
	public void setTechnicalGovNoOfYears(
			List<ScQtCheckListScore> technicalGovNoOfYears) {
		this.technicalGovNoOfYears = technicalGovNoOfYears;
	}

	/**
	 * Gets the spec item list.
	 * 
	 * @return the spec item list
	 */
	public List<TplSpecQuestion> getSpecItemList() {
		return specItemList;
	}

	/**
	 * Sets the spec item list.
	 * 
	 * @param specItemList
	 *            the new spec item list
	 */
	public void setSpecItemList(List<TplSpecQuestion> specItemList) {
		this.specItemList = specItemList;
	}

	/**
	 * Gets the prp company experience list.
	 * 
	 * @return the prp company experience list
	 */
	public List<ScQtPRPExperience> getPrpCompanyExperienceList() {
		return prpCompanyExperienceList;
	}

	/**
	 * Sets the prp company experience list.
	 * 
	 * @param prpCompanyExperienceList
	 *            the new prp company experience list
	 */
	public void setPrpCompanyExperienceList(
			List<ScQtPRPExperience> prpCompanyExperienceList) {
		this.prpCompanyExperienceList = prpCompanyExperienceList;
	}

	/**
	 * Gets the loa gov company exp list.
	 * 
	 * @return the loa gov company exp list
	 */
	public List<ScLoa> getLoaGovCompanyExpList() {
		return loaGovCompanyExpList;
	}

	/**
	 * Sets the loa gov company exp list.
	 * 
	 * @param loaGovCompanyExpList
	 *            the new loa gov company exp list
	 */
	public void setLoaGovCompanyExpList(List<ScLoa> loaGovCompanyExpList) {
		this.loaGovCompanyExpList = loaGovCompanyExpList;
	}

	/**
	 * Gets the sample item list.
	 * 
	 * @return the sample item list
	 */
	public List<ScQtSampleItem> getSampleItemList() {
		return sampleItemList;
	}

	/**
	 * Sets the sample item list.
	 * 
	 * @param sampleItemList
	 *            the new sample item list
	 */
	public void setSampleItemList(List<ScQtSampleItem> sampleItemList) {
		this.sampleItemList = sampleItemList;
	}

	/**
	 * Gets the no of employee list.
	 * 
	 * @return the no of employee list
	 */
	public ScQtPRPEmployee getNoOfEmployeeList() {
		return noOfEmployeeList;
	}

	/**
	 * Sets the no of employee list.
	 * 
	 * @param noOfEmployeeList
	 *            the new no of employee list
	 */
	public void setNoOfEmployeeList(ScQtPRPEmployee noOfEmployeeList) {
		this.noOfEmployeeList = noOfEmployeeList;
	}

	/**
	 * Gets the cash on hand list.
	 * 
	 * @return the cash on hand list
	 */
	public List<ScQtPRPCashOnHand> getCashOnHandList() {
		return cashOnHandList;
	}

	/**
	 * Sets the cash on hand list.
	 * 
	 * @param cashOnHandList
	 *            the new cash on hand list
	 */
	public void setCashOnHandList(List<ScQtPRPCashOnHand> cashOnHandList) {
		this.cashOnHandList = cashOnHandList;
	}

	/**
	 * Gets the credit facility list.
	 * 
	 * @return the credit facility list
	 */
	public List<ScQtPRPCreditFacility> getCreditFacilityList() {
		return creditFacilityList;
	}

	/**
	 * Sets the credit facility list.
	 * 
	 * @param creditFacilityList
	 *            the new credit facility list
	 */
	public void setCreditFacilityList(
			List<ScQtPRPCreditFacility> creditFacilityList) {
		this.creditFacilityList = creditFacilityList;
	}

	/**
	 * Gets the bank stmt list.
	 * 
	 * @return the bank stmt list
	 */
	public List<ScQtPRPBankStmt> getBankStmtList() {
		return bankStmtList;
	}

	/**
	 * Sets the bank stmt list.
	 * 
	 * @param bankStmtList
	 *            the new bank stmt list
	 */
	public void setBankStmtList(List<ScQtPRPBankStmt> bankStmtList) {
		this.bankStmtList = bankStmtList;
	}

	/**
	 * Gets the asset list.
	 * 
	 * @return the asset list
	 */
	public List<ScQtPRPAssetLiability> getAssetList() {
		return assetList;
	}

	/**
	 * Sets the asset list.
	 * 
	 * @param assetList
	 *            the new asset list
	 */
	public void setAssetList(List<ScQtPRPAssetLiability> assetList) {
		this.assetList = assetList;
	}

	/**
	 * Gets the tech gov proposal score.
	 * 
	 * @return the tech gov proposal score
	 */
	public List<ScQtProposalScore> getTechGovProposalScore() {
		return techGovProposalScore;
	}

	/**
	 * Sets the tech gov proposal score.
	 * 
	 * @param techGovProposalScore
	 *            the new tech gov proposal score
	 */
	public void setTechGovProposalScore(
			List<ScQtProposalScore> techGovProposalScore) {
		this.techGovProposalScore = techGovProposalScore;
	}

	/**
	 * Gets the bank total amt.
	 * 
	 * @return the bank total amt
	 */
	public BigDecimal getBankTotalAmt() {
		return bankTotalAmt;
	}

	/**
	 * Sets the bank total amt.
	 * 
	 * @param bankTotalAmt
	 *            the new bank total amt
	 */
	public void setBankTotalAmt(BigDecimal bankTotalAmt) {
		this.bankTotalAmt = bankTotalAmt;
	}

	/**
	 * Gets the summary item list.
	 * 
	 * @return the summary item list
	 */
	public List<ScRequestItem> getSummaryItemList() {
		return summaryItemList;
	}

	/**
	 * Sets the summary item list.
	 * 
	 * @param summaryItemList
	 *            the new summary item list
	 */
	public void setSummaryItemList(List<ScRequestItem> summaryItemList) {
		this.summaryItemList = summaryItemList;
	}

	/**
	 * Gets the singlesummary item list.
	 * 
	 * @return the singlesummary item list
	 */
	public ScRequestItem getSinglesummaryItemList() {
		return singlesummaryItemList;
	}

	/**
	 * Sets the singlesummary item list.
	 * 
	 * @param singlesummaryItemList
	 *            the new singlesummary item list
	 */
	public void setSinglesummaryItemList(ScRequestItem singlesummaryItemList) {
		this.singlesummaryItemList = singlesummaryItemList;
	}

	/**
	 * Gets the tpl spec zone dtls.
	 * 
	 * @return the tpl spec zone dtls
	 */
	public List<TplSpecZoneDtl> getTplSpecZoneDtls() {
		return tplSpecZoneDtls;
	}

	/**
	 * Sets the tpl spec zone dtls.
	 * 
	 * @param tplSpecZoneDtls
	 *            the new tpl spec zone dtls
	 */
	public void setTplSpecZoneDtls(List<TplSpecZoneDtl> tplSpecZoneDtls) {
		this.tplSpecZoneDtls = tplSpecZoneDtls;
	}

	/**
	 * Gets the selected zone.
	 * 
	 * @return the selected zone
	 */
	public TplSpecZoneDtl getSelectedZone() {
		return selectedZone;
	}

	/**
	 * Sets the selected zone.
	 * 
	 * @param selectedZone
	 *            the new selected zone
	 */
	public void setSelectedZone(TplSpecZoneDtl selectedZone) {
		this.selectedZone = selectedZone;
	}

	/**
	 * Gets the presentation demo active index.
	 * 
	 * @return the presentation demo active index
	 */
	public int getPresentationDemoActiveIndex() {
		return presentationDemoActiveIndex;
	}

	/**
	 * Sets the presentation demo active index.
	 * 
	 * @param presentationDemoActiveIndex
	 *            the new presentation demo active index
	 */
	public void setPresentationDemoActiveIndex(int presentationDemoActiveIndex) {
		this.presentationDemoActiveIndex = presentationDemoActiveIndex;
	}

	/**
	 * Gets the presentation demo supplier list.
	 * 
	 * @return the presentation demo supplier list
	 */
	public List<ScQtProposalChecklist> getPresentationDemoSupplierList() {
		return presentationDemoSupplierList;
	}

	/**
	 * Sets the presentation demo supplier list.
	 * 
	 * @param presentationDemoSupplierList
	 *            the new presentation demo supplier list
	 */
	public void setPresentationDemoSupplierList(
			List<ScQtProposalChecklist> presentationDemoSupplierList) {
		this.presentationDemoSupplierList = presentationDemoSupplierList;
	}

	/**
	 * Gets the fin spec supplier list.
	 * 
	 * @return the fin spec supplier list
	 */
	public List<ScQtProposalChecklist> getFinSpecSupplierList() {
		return finSpecSupplierList;
	}

	/**
	 * Sets the fin spec supplier list.
	 * 
	 * @param finSpecSupplierList
	 *            the new fin spec supplier list
	 */
	public void setFinSpecSupplierList(
			List<ScQtProposalChecklist> finSpecSupplierList) {
		this.finSpecSupplierList = finSpecSupplierList;
	}

	/**
	 * Gets the offer price package.
	 * 
	 * @return the offer price package
	 */
	public ScQtScoreOfferPrice getOfferPricePackage() {
		return offerPricePackage;
	}

	/**
	 * Sets the offer price package.
	 * 
	 * @param offerPricePackage
	 *            the new offer price package
	 */
	public void setOfferPricePackage(ScQtScoreOfferPrice offerPricePackage) {
		this.offerPricePackage = offerPricePackage;
	}

	/**
	 * Gets the offer price item.
	 * 
	 * @return the offer price item
	 */
	public ScQtScoreOfferPrice getOfferPriceItem() {
		return offerPriceItem;
	}

	/**
	 * Sets the offer price item.
	 * 
	 * @param offerPriceItem
	 *            the new offer price item
	 */
	public void setOfferPriceItem(ScQtScoreOfferPrice offerPriceItem) {
		this.offerPriceItem = offerPriceItem;
	}

	/**
	 * Gets the endorscommittee member list.
	 * 
	 * @return the endorscommittee member list
	 */
	public List<ScQtCommitteeActivity> getEndorscommitteeMemberList() {
		return endorscommitteeMemberList;
	}

	/**
	 * Sets the endorscommittee member list.
	 * 
	 * @param endorscommitteeMemberList
	 *            the new endorscommittee member list
	 */
	public void setEndorscommitteeMemberList(
			List<ScQtCommitteeActivity> endorscommitteeMemberList) {
		this.endorscommitteeMemberList = endorscommitteeMemberList;
	}

	/**
	 * Checks if is demo.
	 * 
	 * @return true, if is demo
	 */
	public boolean isDemo() {
		return isDemo;
	}

	/**
	 * Sets the demo.
	 * 
	 * @param isDemo
	 *            the new demo
	 */
	public void setDemo(boolean isDemo) {
		this.isDemo = isDemo;
	}

	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 * 
	 * @param message
	 *            the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the subject.
	 * 
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the subject.
	 * 
	 * @param subject
	 *            the new subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Gets the evaluation compliance list.
	 * 
	 * @return the evaluation compliance list
	 */
	public List<ScQtProposalChecklist> getEvaluationComplianceList() {
		return evaluationComplianceList;
	}

	/**
	 * Sets the evaluation compliance list.
	 * 
	 * @param evaluationComplianceList
	 *            the new evaluation compliance list
	 */
	public void setEvaluationComplianceList(
			List<ScQtProposalChecklist> evaluationComplianceList) {
		this.evaluationComplianceList = evaluationComplianceList;
	}

	/**
	 * Checks if is generate evaluation button.
	 * 
	 * @return true, if is generate evaluation button
	 */
	public boolean isGenerateEvaluationButton() {
		return generateEvaluationButton;
	}

	/**
	 * Sets the generate evaluation button.
	 * 
	 * @param generateEvaluationButton
	 *            the new generate evaluation button
	 */
	public void setGenerateEvaluationButton(boolean generateEvaluationButton) {
		this.generateEvaluationButton = generateEvaluationButton;
	}

	/**
	 * Checks if is compliance stage hide.
	 * 
	 * @return true, if is compliance stage hide
	 */
	public boolean isComplianceStageHide() {
		return complianceStageHide;
	}

	/**
	 * Sets the compliance stage hide.
	 * 
	 * @param complianceStageHide
	 *            the new compliance stage hide
	 */
	public void setComplianceStageHide(boolean complianceStageHide) {
		this.complianceStageHide = complianceStageHide;
	}

	/**
	 * Gets the compliance summary count.
	 * 
	 * @return the compliance summary count
	 */
	public int getComplianceSummaryCount() {
		return complianceSummaryCount;
	}

	/**
	 * Sets the compliance summary count.
	 * 
	 * @param complianceSummaryCount
	 *            the new compliance summary count
	 */
	public void setComplianceSummaryCount(int complianceSummaryCount) {
		this.complianceSummaryCount = complianceSummaryCount;
	}

	/**
	 * Gets the sc qt prp asset liability.
	 * 
	 * @return the sc qt prp asset liability
	 */
	public ScQtPRPAssetLiability getScQtPRPAssetLiability() {
		return scQtPRPAssetLiability;
	}

	/**
	 * Sets the sc qt prp asset liability.
	 * 
	 * @param scQtPRPAssetLiability
	 *            the new sc qt prp asset liability
	 */
	public void setScQtPRPAssetLiability(
			ScQtPRPAssetLiability scQtPRPAssetLiability) {
		this.scQtPRPAssetLiability = scQtPRPAssetLiability;
	}

	/**
	 * Gets the sc qt evaluation report list.
	 * 
	 * @return the sc qt evaluation report list
	 */
	public List<ScQtEvaluationReport> getScQtEvaluationReportList() {
		return scQtEvaluationReportList;
	}

	/**
	 * Sets the sc qt evaluation report list.
	 * 
	 * @param scQtEvaluationReportList
	 *            the new sc qt evaluation report list
	 */
	public void setScQtEvaluationReportList(
			List<ScQtEvaluationReport> scQtEvaluationReportList) {
		this.scQtEvaluationReportList = scQtEvaluationReportList;
	}

	/**
	 * Gets the selected sc qt evaluation reports.
	 * 
	 * @return the selected sc qt evaluation reports
	 */
	public List<ScQtEvaluationReport> getSelectedScQtEvaluationReports() {
		return selectedScQtEvaluationReports;
	}

	/**
	 * Sets the selected sc qt evaluation reports.
	 * 
	 * @param selectedScQtEvaluationReports
	 *            the new selected sc qt evaluation reports
	 */
	public void setSelectedScQtEvaluationReports(
			List<ScQtEvaluationReport> selectedScQtEvaluationReports) {
		this.selectedScQtEvaluationReports = selectedScQtEvaluationReports;
	}

	/**
	 * Gets the sc qt evaluation report.
	 * 
	 * @return the sc qt evaluation report
	 */
	public ScQtEvaluationReport getScQtEvaluationReport() {
		return scQtEvaluationReport;
	}

	/**
	 * Sets the sc qt evaluation report.
	 * 
	 * @param scQtEvaluationReport
	 *            the new sc qt evaluation report
	 */
	public void setScQtEvaluationReport(
			ScQtEvaluationReport scQtEvaluationReport) {
		this.scQtEvaluationReport = scQtEvaluationReport;
	}

	/**
	 * Gets the report header.
	 * 
	 * @return the report header
	 */
	public String getReportHeader() {
		return reportHeader;
	}

	/**
	 * Sets the report header.
	 * 
	 * @param reportHeader
	 *            the new report header
	 */
	public void setReportHeader(String reportHeader) {
		this.reportHeader = reportHeader;
	}

	/**
	 * Gets the sm personnels list.
	 * 
	 * @return the sm personnels list
	 */
	public List<TechnicalStaffBean> getSmPersonnelsList() {
		return smPersonnelsList;
	}

	/**
	 * Sets the sm personnels list.
	 * 
	 * @param smPersonnelsList
	 *            the new sm personnels list
	 */
	public void setSmPersonnelsList(List<TechnicalStaffBean> smPersonnelsList) {
		this.smPersonnelsList = smPersonnelsList;
	}

	/**
	 * Gets the total no of years experience.
	 * 
	 * @return the total no of years experience
	 */
	public int getTotalNoOfYearsExperience() {
		return totalNoOfYearsExperience;
	}

	/**
	 * Sets the total no of years experience.
	 * 
	 * @param totalNoOfYearsExperience
	 *            the new total no of years experience
	 */
	public void setTotalNoOfYearsExperience(int totalNoOfYearsExperience) {
		this.totalNoOfYearsExperience = totalNoOfYearsExperience;
	}

	/**
	 * Gets the evaluation page.
	 * 
	 * @return the evaluation page
	 */
	public String getEvaluationPage() {
		return evaluationPage;
	}

	/**
	 * Sets the evaluation page.
	 * 
	 * @param evaluationPage
	 *            the new evaluation page
	 */
	public void setEvaluationPage(String evaluationPage) {
		this.evaluationPage = evaluationPage;
	}

	/**
	 * Gets the proposal page.
	 * 
	 * @return the proposal page
	 */
	public String getProposalPage() {
		return proposalPage;
	}

	/**
	 * Sets the proposal page.
	 * 
	 * @param proposalPage
	 *            the new proposal page
	 */
	public void setProposalPage(String proposalPage) {
		this.proposalPage = proposalPage;
	}

	/**
	 * Gets the doc definition key.
	 * 
	 * @return the doc definition key
	 */
	public String getDocDefinitionKey() {
		return docDefinitionKey;
	}

	/**
	 * Sets the doc definition key.
	 * 
	 * @param docDefinitionKey
	 *            the new doc definition key
	 */
	public void setDocDefinitionKey(String docDefinitionKey) {
		this.docDefinitionKey = docDefinitionKey;
	}

	/**
	 * Gets the techindex.
	 * 
	 * @return the techindex
	 */
	public int getTechindex() {
		return techindex;
	}

	/**
	 * Sets the techindex.
	 * 
	 * @param techindex
	 *            the new techindex
	 */
	public void setTechindex(int techindex) {
		this.techindex = techindex;
	}

	/**
	 * @return the qualifiedSuppliersDM
	 */
	public IPagingDataModel<ScQtEvaluationResultDtl> getQualifiedSuppliersDM() {
		return qualifiedSuppliersDM;
	}

	/**
	 * @param qualifiedSuppliersDM
	 *            the qualifiedSuppliersDM to set
	 */
	public void setQualifiedSuppliersDM(
			IPagingDataModel<ScQtEvaluationResultDtl> qualifiedSuppliersDM) {
		this.qualifiedSuppliersDM = qualifiedSuppliersDM;
	}

	/**
	 * @return the disqualifiedSuppliersDM
	 */
	public IPagingDataModel<ScQtEvaluationResultDtl> getDisqualifiedSuppliersDM() {
		return disqualifiedSuppliersDM;
	}

	/**
	 * @param disqualifiedSuppliersDM
	 *            the disqualifiedSuppliersDM to set
	 */
	public void setDisqualifiedSuppliersDM(
			IPagingDataModel<ScQtEvaluationResultDtl> disqualifiedSuppliersDM) {
		this.disqualifiedSuppliersDM = disqualifiedSuppliersDM;
	}

	/**
	 * Checks if is status only.
	 * 
	 * @return true, if is status only
	 */
	public boolean isStatusOnly() {
		return statusOnly;
	}

	/**
	 * Sets the status only.
	 * 
	 * @param statusOnly
	 *            the new status only
	 */
	public void setStatusOnly(boolean statusOnly) {
		this.statusOnly = statusOnly;
	}

	/**
	 * Gets the sc attachment.
	 * 
	 * @return the sc attachment
	 */
	public ScAttachment getScAttachment() {
		return scAttachment;
	}

	/**
	 * Sets the sc attachment.
	 * 
	 * @param scAttachment
	 *            the new sc attachment
	 */
	public void setScAttachment(ScAttachment scAttachment) {
		this.scAttachment = scAttachment;
	}

	/**
	 * Gets the sc remark.
	 * 
	 * @return the sc remark
	 */
	public List<ScRemark> getScRemark() {
		return scRemark;
	}

	/**
	 * Sets the sc remark.
	 * 
	 * @param scRemark
	 *            the new sc remark
	 */
	public void setScRemark(List<ScRemark> scRemark) {
		this.scRemark = scRemark;
	}

	/**
	 * Gets the evaluation committee remark.
	 * 
	 * @return the evaluation committee remark
	 */
	public List<ScQtEvaluationCommitteeRemark> getEvaluationCommitteeRemark() {
		return evaluationCommitteeRemark;
	}

	/**
	 * Sets the evaluation committee remark.
	 * 
	 * @param evaluationCommitteeRemark
	 *            the new evaluation committee remark
	 */
	public void setEvaluationCommitteeRemark(
			List<ScQtEvaluationCommitteeRemark> evaluationCommitteeRemark) {
		this.evaluationCommitteeRemark = evaluationCommitteeRemark;
	}

	/**
	 * Gets the evaluation opening committee remark.
	 * 
	 * @return the evaluation opening committee remark
	 */
	public List<ScQtOpeningCommitteeRemark> getEvaluationOpeningCommitteeRemark() {
		return evaluationOpeningCommitteeRemark;
	}

	/**
	 * Sets the evaluation opening committee remark.
	 * 
	 * @param evaluationOpeningCommitteeRemark
	 *            the new evaluation opening committee remark
	 */
	public void setEvaluationOpeningCommitteeRemark(
			List<ScQtOpeningCommitteeRemark> evaluationOpeningCommitteeRemark) {
		this.evaluationOpeningCommitteeRemark = evaluationOpeningCommitteeRemark;
	}

	/**
	 * Gets the evaluation technical committee remark.
	 * 
	 * @return the evaluation technical committee remark
	 */
	public List<ScQtTechnicalCommitteeRemark> getEvaluationTechnicalCommitteeRemark() {
		return evaluationTechnicalCommitteeRemark;
	}

	/**
	 * Sets the evaluation technical committee remark.
	 * 
	 * @param evaluationTechnicalCommitteeRemark
	 *            the new evaluation technical committee remark
	 */
	public void setEvaluationTechnicalCommitteeRemark(
			List<ScQtTechnicalCommitteeRemark> evaluationTechnicalCommitteeRemark) {
		this.evaluationTechnicalCommitteeRemark = evaluationTechnicalCommitteeRemark;
	}

	/**
	 * Gets the evaluation financial committee remark.
	 * 
	 * @return the evaluation financial committee remark
	 */
	public List<ScQtFinancialCommitteeRemark> getEvaluationFinancialCommitteeRemark() {
		return evaluationFinancialCommitteeRemark;
	}

	/**
	 * Sets the evaluation financial committee remark.
	 * 
	 * @param evaluationFinancialCommitteeRemark
	 *            the new evaluation financial committee remark
	 */
	public void setEvaluationFinancialCommitteeRemark(
			List<ScQtFinancialCommitteeRemark> evaluationFinancialCommitteeRemark) {
		this.evaluationFinancialCommitteeRemark = evaluationFinancialCommitteeRemark;
	}

	/**
	 * Gets the with in varieance.
	 * 
	 * @return the with in varieance
	 */
	public BigDecimal getWithInVarieance() {
		return withInVarieance;
	}

	/**
	 * Sets the with in varieance.
	 * 
	 * @param withInVarieance
	 *            the new with in varieance
	 */
	public void setWithInVarieance(BigDecimal withInVarieance) {
		this.withInVarieance = withInVarieance;
	}

	/**
	 * Gets the check list type.
	 * 
	 * @return the check list type
	 */
	public String getCheckListType() {
		return checkListType;
	}

	/**
	 * Sets the check list type.
	 * 
	 * @param checkListType
	 *            the new check list type
	 */
	public void setCheckListType(String checkListType) {
		this.checkListType = checkListType;
	}

	/**
	 * Gets the evaluation stage zones summary.
	 * 
	 * @return the evaluation stage zones summary
	 */
	public List<TplSpecZoneDtl> getEvaluationStageZonesSummary() {
		return evaluationStageZonesSummary;
	}

	/**
	 * Sets the evaluation stage zones summary.
	 * 
	 * @param evaluationStageZonesSummary
	 *            the new evaluation stage zones summary
	 */
	public void setEvaluationStageZonesSummary(
			List<TplSpecZoneDtl> evaluationStageZonesSummary) {
		this.evaluationStageZonesSummary = evaluationStageZonesSummary;
	}

	/**
	 * Checks if is compliance only.
	 * 
	 * @return true, if is compliance only
	 */
	public boolean isComplianceOnly() {
		return complianceOnly;
	}

	/**
	 * Sets the compliance only.
	 * 
	 * @param complianceOnly
	 *            the new compliance only
	 */
	public void setComplianceOnly(boolean complianceOnly) {
		this.complianceOnly = complianceOnly;
	}

	/**
	 * Checks if is evaluation stage status hide.
	 * 
	 * @return true, if is evaluation stage status hide
	 */
	public boolean isEvaluationStageStatusHide() {
		return evaluationStageStatusHide;
	}

	/**
	 * Sets the evaluation stage status hide.
	 * 
	 * @param evaluationStageStatusHide
	 *            the new evaluation stage status hide
	 */
	public void setEvaluationStageStatusHide(boolean evaluationStageStatusHide) {
		this.evaluationStageStatusHide = evaluationStageStatusHide;
	}

	/**
	 * Gets the spec item scoring list.
	 * 
	 * @return the spec item scoring list
	 */
	public List<TplSpecScoring> getSpecItemScoringList() {
		return specItemScoringList;
	}

	/**
	 * Sets the spec item scoring list.
	 * 
	 * @param specItemScoringList
	 *            the new spec item scoring list
	 */
	public void setSpecItemScoringList(List<TplSpecScoring> specItemScoringList) {
		this.specItemScoringList = specItemScoringList;
	}

	/**
	 * Gets the compliance check list index.
	 * 
	 * @return the compliance check list index
	 */
	public int getComplianceCheckListIndex() {
		return complianceCheckListIndex;
	}

	/**
	 * Sets the compliance check list index.
	 * 
	 * @param complianceCheckListIndex
	 *            the new compliance check list index
	 */
	public void setComplianceCheckListIndex(int complianceCheckListIndex) {
		this.complianceCheckListIndex = complianceCheckListIndex;
	}

	/**
	 * Gets the evaluation check list type.
	 * 
	 * @return the evaluation check list type
	 */
	public String getEvaluationCheckListType() {
		return evaluationCheckListType;
	}

	/**
	 * Sets the evaluation check list type.
	 * 
	 * @param evaluationCheckListType
	 *            the new evaluation check list type
	 */
	public void setEvaluationCheckListType(String evaluationCheckListType) {
		this.evaluationCheckListType = evaluationCheckListType;
	}

	/**
	 * Gets the evaluation check list index.
	 * 
	 * @return the evaluation check list index
	 */
	public int getEvaluationCheckListIndex() {
		return evaluationCheckListIndex;
	}

	/**
	 * Sets the evaluation check list index.
	 * 
	 * @param evaluationCheckListIndex
	 *            the new evaluation check list index
	 */
	public void setEvaluationCheckListIndex(int evaluationCheckListIndex) {
		this.evaluationCheckListIndex = evaluationCheckListIndex;
	}

	/**
	 * Gets the evaluation technical financial type.
	 * 
	 * @return the evaluation technical financial type
	 */
	public String getEvaluationTechnicalFinancialType() {
		return evaluationTechnicalFinancialType;
	}

	/**
	 * Sets the evaluation technical financial type.
	 * 
	 * @param evaluationTechnicalFinancialType
	 *            the new evaluation technical financial type
	 */
	public void setEvaluationTechnicalFinancialType(
			String evaluationTechnicalFinancialType) {
		this.evaluationTechnicalFinancialType = evaluationTechnicalFinancialType;
	}

	/**
	 * Gets the user scores.
	 * 
	 * @return the user scores
	 */
	public String getUserScores() {
		return userScores;
	}

	/**
	 * Sets the user scores.
	 * 
	 * @param userScores
	 *            the new user scores
	 */
	public void setUserScores(String userScores) {
		this.userScores = userScores;
	}

	/**
	 * Gets the contract scores.
	 * 
	 * @return the contract scores
	 */
	public String getContractScores() {
		return contractScores;
	}

	/**
	 * Sets the contract scores.
	 * 
	 * @param contractScores
	 *            the new contract scores
	 */
	public void setContractScores(String contractScores) {
		this.contractScores = contractScores;
	}

	/**
	 * Gets the package from varieance.
	 * 
	 * @return the package from varieance
	 */
	public BigDecimal getPackageFromVarieance() {
		return packageFromVarieance;
	}

	/**
	 * Sets the package from varieance.
	 * 
	 * @param packageFromVarieance
	 *            the new package from varieance
	 */
	public void setPackageFromVarieance(BigDecimal packageFromVarieance) {
		this.packageFromVarieance = packageFromVarieance;
	}

	/**
	 * Gets the package to varieance.
	 * 
	 * @return the package to varieance
	 */
	public BigDecimal getPackageToVarieance() {
		return packageToVarieance;
	}

	/**
	 * Sets the package to varieance.
	 * 
	 * @param packageToVarieance
	 *            the new package to varieance
	 */
	public void setPackageToVarieance(BigDecimal packageToVarieance) {
		this.packageToVarieance = packageToVarieance;
	}

	/**
	 * @return the evaluationSpecZonalMenuModel
	 */
	public MenuModel getEvaluationSpecZonalMenuModel() {
		return evaluationSpecZonalMenuModel;
	}

	/**
	 * @param evaluationSpecZonalMenuModel
	 *            the evaluationSpecZonalMenuModel to set
	 */
	public void setEvaluationSpecZonalMenuModel(
			MenuModel evaluationSpecZonalMenuModel) {
		this.evaluationSpecZonalMenuModel = evaluationSpecZonalMenuModel;
	}

	/**
	 * @return the checkQualifiedSuppliers
	 */
	public boolean isCheckQualifiedSuppliers() {
		return checkQualifiedSuppliers;
	}

	/**
	 * @param checkQualifiedSuppliers
	 *            the checkQualifiedSuppliers to set
	 */
	public void setCheckQualifiedSuppliers(boolean checkQualifiedSuppliers) {
		this.checkQualifiedSuppliers = checkQualifiedSuppliers;
	}

	/**
	 * @return the reportDownloadFileName
	 */
	public String getReportDownloadFileName() {
		return reportDownloadFileName;
	}

	/**
	 * @param reportDownloadFileName
	 *            the reportDownloadFileName to set
	 */
	public void setReportDownloadFileName(String reportDownloadFileName) {
		this.reportDownloadFileName = reportDownloadFileName;
	}

	/**
	 * @return the checkReportDownload
	 */
	public boolean isCheckReportDownload() {
		return checkReportDownload;
	}

	/**
	 * @param checkReportDownload
	 *            the checkReportDownload to set
	 */
	public void setCheckReportDownload(boolean checkReportDownload) {
		this.checkReportDownload = checkReportDownload;
	}

	/**
	 * @return the packageScore
	 */
	public BigDecimal getPackageScore() {
		return packageScore;
	}

	/**
	 * @param packageScore
	 *            the packageScore to set
	 */
	public void setPackageScore(BigDecimal packageScore) {
		this.packageScore = packageScore;
	}

	/**
	 * @return the passingScore
	 */
	public int getPassingScore() {
		return passingScore;
	}

	/**
	 * @param passingScore
	 *            the passingScore to set
	 */
	public void setPassingScore(int passingScore) {
		this.passingScore = passingScore;
	}

	/**
	 * @return the oneTierFinancialActiveIndex
	 */
	public int getOneTierFinancialActiveIndex() {
		return oneTierFinancialActiveIndex;
	}

	/**
	 * @param oneTierFinancialActiveIndex
	 *            the oneTierFinancialActiveIndex to set
	 */
	public void setOneTierFinancialActiveIndex(int oneTierFinancialActiveIndex) {
		this.oneTierFinancialActiveIndex = oneTierFinancialActiveIndex;
	}

	/**
	 * @return the financialSummaryView
	 */
	public String getFinancialSummaryView() {
		return financialSummaryView;
	}

	/**
	 * @param financialSummaryView
	 *            the financialSummaryView to set
	 */
	public void setFinancialSummaryView(String financialSummaryView) {
		this.financialSummaryView = financialSummaryView;
	}

	/**
	 * @return the roleCode
	 */
	public String getRoleCode() {
		return roleCode;
	}

	/**
	 * @param roleCode
	 *            the roleCode to set
	 */
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	/**
	 * @return the finSpecZoneEstimatedPrice
	 */
	public BigDecimal getFinSpecZoneEstimatedPrice() {
		return finSpecZoneEstimatedPrice;
	}

	/**
	 * @param finSpecZoneEstimatedPrice
	 *            the finSpecZoneEstimatedPrice to set
	 */
	public void setFinSpecZoneEstimatedPrice(
			BigDecimal finSpecZoneEstimatedPrice) {
		this.finSpecZoneEstimatedPrice = finSpecZoneEstimatedPrice;
	}

	/**
	 * @return the sampleItemSpecifications
	 */
	public List<TplSpecQuestion> getSampleItemSpecifications() {
		return sampleItemSpecifications;
	}

	/**
	 * @param sampleItemSpecifications
	 *            the sampleItemSpecifications to set
	 */
	public void setSampleItemSpecifications(
			List<TplSpecQuestion> sampleItemSpecifications) {
		this.sampleItemSpecifications = sampleItemSpecifications;
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
	 * @return the sampleItemSelection
	 */
	public ScQtSampleItem getSampleItemSelection() {
		return sampleItemSelection;
	}

	/**
	 * @param sampleItemSelection
	 *            the sampleItemSelection to set
	 */
	public void setSampleItemSelection(ScQtSampleItem sampleItemSelection) {
		this.sampleItemSelection = sampleItemSelection;
	}

	/**
	 * @return the checkTechnicalQualifiedOpenPopup
	 */
	public boolean isCheckTechnicalQualifiedOpenPopup() {
		return checkTechnicalQualifiedOpenPopup;
	}

	/**
	 * @param checkTechnicalQualifiedOpenPopup
	 *            the checkTechnicalQualifiedOpenPopup to set
	 */
	public void setCheckTechnicalQualifiedOpenPopup(
			boolean checkTechnicalQualifiedOpenPopup) {
		this.checkTechnicalQualifiedOpenPopup = checkTechnicalQualifiedOpenPopup;
	}

	/**
	 * @return the checkFinancialQualifiedOpenPopup
	 */
	public boolean isCheckFinancialQualifiedOpenPopup() {
		return checkFinancialQualifiedOpenPopup;
	}

	/**
	 * @param checkFinancialQualifiedOpenPopup
	 *            the checkFinancialQualifiedOpenPopup to set
	 */
	public void setCheckFinancialQualifiedOpenPopup(
			boolean checkFinancialQualifiedOpenPopup) {
		this.checkFinancialQualifiedOpenPopup = checkFinancialQualifiedOpenPopup;
	}

	/**
	 * @return the complianceSummaryGenerated
	 */
	public boolean isComplianceSummaryGenerated() {
		return complianceSummaryGenerated;
	}

	/**
	 * @param complianceSummaryGenerated
	 *            the complianceSummaryGenerated to set
	 */
	public void setComplianceSummaryGenerated(boolean complianceSummaryGenerated) {
		this.complianceSummaryGenerated = complianceSummaryGenerated;
	}

	/**
	 * @return the technicalSummaryGenerated
	 */
	public boolean isTechnicalSummaryGenerated() {
		return technicalSummaryGenerated;
	}

	/**
	 * @param technicalSummaryGenerated
	 *            the technicalSummaryGenerated to set
	 */
	public void setTechnicalSummaryGenerated(boolean technicalSummaryGenerated) {
		this.technicalSummaryGenerated = technicalSummaryGenerated;
	}

	/**
	 * @return the presentationDemoSummaryGenerated
	 */
	public boolean isPresentationDemoSummaryGenerated() {
		return presentationDemoSummaryGenerated;
	}

	/**
	 * @param presentationDemoSummaryGenerated
	 *            the presentationDemoSummaryGenerated to set
	 */
	public void setPresentationDemoSummaryGenerated(
			boolean presentationDemoSummaryGenerated) {
		this.presentationDemoSummaryGenerated = presentationDemoSummaryGenerated;
	}

	/**
	 * @return the financialSummaryGenerated
	 */
	public boolean isFinancialSummaryGenerated() {
		return financialSummaryGenerated;
	}

	/**
	 * @param financialSummaryGenerated
	 *            the financialSummaryGenerated to set
	 */
	public void setFinancialSummaryGenerated(boolean financialSummaryGenerated) {
		this.financialSummaryGenerated = financialSummaryGenerated;
	}

	/**
	 * @return the complianceTechnicalProposalCheckListDM
	 */
	public IPagingDataModel<ScQtProposalChecklist> getComplianceTechnicalProposalCheckListDM() {
		return complianceTechnicalProposalCheckListDM;
	}

	/**
	 * @param complianceTechnicalProposalCheckListDM
	 *            the complianceTechnicalProposalCheckListDM to set
	 */
	public void setComplianceTechnicalProposalCheckListDM(
			IPagingDataModel<ScQtProposalChecklist> complianceTechnicalProposalCheckListDM) {
		this.complianceTechnicalProposalCheckListDM = complianceTechnicalProposalCheckListDM;
	}

	/**
	 * @return the finSpecSupplierListDM
	 */
	public IPagingDataModel<ScQtProposalChecklist> getFinSpecSupplierListDM() {
		return finSpecSupplierListDM;
	}

	/**
	 * @param finSpecSupplierListDM
	 *            the finSpecSupplierListDM to set
	 */
	public void setFinSpecSupplierListDM(
			IPagingDataModel<ScQtProposalChecklist> finSpecSupplierListDM) {
		this.finSpecSupplierListDM = finSpecSupplierListDM;
	}

	/**
	 * @return the complianceSampleItemListDM
	 */
	public IPagingDataModel<ScQtSampleItem> getComplianceSampleItemListDM() {
		return complianceSampleItemListDM;
	}

	/**
	 * @param complianceSampleItemListDM
	 *            the complianceSampleItemListDM to set
	 */
	public void setComplianceSampleItemListDM(
			IPagingDataModel<ScQtSampleItem> complianceSampleItemListDM) {
		this.complianceSampleItemListDM = complianceSampleItemListDM;
	}

	/**
	 * @return the complianceSampleLblsupplierDM
	 */
	public IPagingDataModel<ScQtSampleLable> getComplianceSampleLblsupplierDM() {
		return complianceSampleLblsupplierDM;
	}

	/**
	 * @param complianceSampleLblsupplierDM
	 *            the complianceSampleLblsupplierDM to set
	 */
	public void setComplianceSampleLblsupplierDM(
			IPagingDataModel<ScQtSampleLable> complianceSampleLblsupplierDM) {
		this.complianceSampleLblsupplierDM = complianceSampleLblsupplierDM;
	}

	/**
	 * @return the presentationDemoSupplierListDM
	 */
	public IPagingDataModel<ScQtProposalChecklist> getPresentationDemoSupplierListDM() {
		return presentationDemoSupplierListDM;
	}

	/**
	 * @param presentationDemoSupplierListDM
	 *            the presentationDemoSupplierListDM to set
	 */
	public void setPresentationDemoSupplierListDM(
			IPagingDataModel<ScQtProposalChecklist> presentationDemoSupplierListDM) {
		this.presentationDemoSupplierListDM = presentationDemoSupplierListDM;
	}

	/**
	 * @return the demoPassingScore
	 */
	public int getDemoPassingScore() {
		return demoPassingScore;
	}

	/**
	 * @param demoPassingScore
	 *            the demoPassingScore to set
	 */
	public void setDemoPassingScore(int demoPassingScore) {
		this.demoPassingScore = demoPassingScore;
	}

	/**
	 * @return the isProfileHide
	 */
	public boolean isProfileHide() {
		return isProfileHide;
	}

	/**
	 * @param isProfileHide
	 *            the isProfileHide to set
	 */
	public void setProfileHide(boolean isProfileHide) {
		this.isProfileHide = isProfileHide;
	}

	/**
	 * @return the currentTime
	 */
	public int getCurrentTime() {
		return currentTime;
	}

	/**
	 * @param currentTime the currentTime to set
	 */
	public void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
	}

	/**
	 * @return the isComplianceReEvaluateRequired
	 */
	public boolean isComplianceReEvaluateRequired() {
		return isComplianceReEvaluateRequired;
	}

	/**
	 * @param isComplianceReEvaluateRequired the isComplianceReEvaluateRequired to set
	 */
	public void setComplianceReEvaluateRequired(
			boolean isComplianceReEvaluateRequired) {
		this.isComplianceReEvaluateRequired = isComplianceReEvaluateRequired;
	}

	/**
	 * @return the isTechnicalOrFinancialReEvaluateRequired
	 */
	public boolean isTechnicalOrFinancialReEvaluateRequired() {
		return isTechnicalOrFinancialReEvaluateRequired;
	}

	/**
	 * @param isTechnicalOrFinancialReEvaluateRequired the isTechnicalOrFinancialReEvaluateRequired to set
	 */
	public void setTechnicalOrFinancialReEvaluateRequired(
			boolean isTechnicalOrFinancialReEvaluateRequired) {
		this.isTechnicalOrFinancialReEvaluateRequired = isTechnicalOrFinancialReEvaluateRequired;
	}

	/**
	 * @return the userScoreSchemaType
	 */
	public String getUserScoreSchemaType() {
		return userScoreSchemaType;
	}

	/**
	 * @param userScoreSchemaType the userScoreSchemaType to set
	 */
	public void setUserScoreSchemaType(String userScoreSchemaType) {
		this.userScoreSchemaType = userScoreSchemaType;
	}

	/**
	 * @return the dummyScoreType
	 */
	public String getDummyScoreType() {
		return dummyScoreType;
	}

	/**
	 * @param dummyScoreType the dummyScoreType to set
	 */
	public void setDummyScoreType(String dummyScoreType) {
		this.dummyScoreType = dummyScoreType;
	}

	/**
	 * @return the selectedReport
	 */
	public ScQtEvaluationReport getSelectedReport() {
		return selectedReport;
	}

	/**
	 * @param selectedReport
	 *            the selectedReport to set
	 */
	public void setSelectedReport(ScQtEvaluationReport selectedReport) {
		this.selectedReport = selectedReport;
	}

	/**
	 * @return the encryptionKey
	 */
	public String getEncryptionKey() {
		return encryptionKey;
	}

	/**
	 * @param encryptionKey the encryptionKey to set
	 */
	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	/**
	 * @return the toBeRemovedProposalAttachmentsChecklists
	 */
	public List<ScQtProposalChecklistAttachment> getToBeRemovedProposalAttachmentsChecklists() {
		return toBeRemovedProposalAttachmentsChecklists;
	}

	/**
	 * @param toBeRemovedProposalAttachmentsChecklists the toBeRemovedProposalAttachmentsChecklists to set
	 */
	public void setToBeRemovedProposalAttachmentsChecklists(
			List<ScQtProposalChecklistAttachment> toBeRemovedProposalAttachmentsChecklists) {
		this.toBeRemovedProposalAttachmentsChecklists = toBeRemovedProposalAttachmentsChecklists;
	}

	/**
	 * @return the autoUpdate
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	

}
