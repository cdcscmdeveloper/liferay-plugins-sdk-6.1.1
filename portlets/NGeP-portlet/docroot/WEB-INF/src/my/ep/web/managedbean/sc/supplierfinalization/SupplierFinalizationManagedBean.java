/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.supplierfinalization;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.bean.sc.QtEvaluationResultDtlBean;
import my.ep.constant.sc.SourcingConstants;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.sc.common.ScRemark;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationEndorsementRecommendationRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationEndorsementRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sc.quationtender.ScQtReverse;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreSetting; //#38151
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.web.backingbean.sc.supplierfinalization.SupplierFinalizationMeetingBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.bean.sc.quatationtender.WeightageScoreBean; //#38151
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.key.sc.supplierfinalization.MapKey;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.MenuModel;
//import my.ep.persistence.entity.sc.quationtender.ScQtReport;

/**
 * Class to handle user session data corresponding to SupplierFinalizationBackingBean
 * 
 * @author Faradiana
 * Supplier Finalization Managed Bean
 */
@ManagedBean(name = "suppFinalizationManagedBean")
@SessionScoped
public class SupplierFinalizationManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private int activeIndex;
	private int prepareActiveIndex;
	private int zoneActiveIndex;
	private int onlineBiddingActiveIndex;
	private String processName;
	
	//MenuModel
	private MenuModel mainMenuModel;
	private MenuModel zoneMenuModel;
	
	//Header
	private ScQt scQt;
	private String committeeType;
	private List<TplSpecZoneDtl> zoneDetails;
	private List<TabBean> tabList;
	public TabBean selectedTabBean;
	
	//Preparation
	private ScQtCommittee scQtCommittee;
	private String committeeTabLabel;
	
	private List<ScQtMeeting> meetingList;
	private List<ScQtMeeting> selectedMeetingList;
	private List<ScQtMeeting> deletedMeetingList;	
	private List<ScQtCommitteeActivity> committeeList;
	private List<ScQtCommitteeActivity> committeeEndorsementList;
	private List<ScQtCommitteRemarks> committeeRemarkList;
	private List<ScQtFinalizationRemarks> finalizationRemarks;
	private List<ScQtFinalizationEndorsementRemarks> finalizationEndorsementRemarks;
	private List<ScQtMeetingAttendance> meetingAttendanceList;
	private List<ScQtMeetingAttendance> selectedMeetingAttList;
	private List<ScQtMeetingAttendance> deletedMeetingAttendanceList;
	
	//Briefing Paper/Kertas Taklimat
	private ScQtFinalizationReport selectedFinalizationReport;
	private List<ScQtFinalizationReport> documentList;
	private String attachmentDocType;
	
	//Supplier Finalization
	/*private String decisionType = ParameterConst.DECISION_RECOMMEND_SUPPLIER;
	private String recommendType = ParameterConst.RECOMMEND_TYPE_PACKAGE;
	private String biddingEndorsement = ParameterConst.ONLINE_BIDDING_MEETING;
	private String loiLoaPreparedBy = ParameterConst.LOILOA_PREPARED_BY_DO;
	private String fwdHigherAuthority = "false";
	private String finalizationCommittee;
	private PmUser committeeSecretary;
	private String committeeSecretaryName;
	private String reason;
	private String reEvaluateBy;
	private boolean reqSpecChanges;
	private boolean reEvaluateReqForTech;
	private boolean reEvaluateReqForFin;*/
	private String finalizationMethod;
	private String selectionMode = QTConstants.SF_SINGLE;
	private String awardSupplier; 
	
	private ScQtRecommend selectedItem;
	
	private String supplierBranch;
	private SmSupplier smSupplier;
	private SmCompanyBasic smCompanyBasic;
	private SmPersonnel smPersonnel;
	private SmAddress supplierAddress;
	private SmMofAccount smMofAccount;
	private List<SmPersonnel> boardofDirectors;
	
	private boolean isItemize;
	private boolean isRecommendSupplier;
	private boolean isReEvaluate;
	private boolean isRePublish;
	private boolean isCancel;
	private boolean isForwardToHigherAuthority;
	private boolean isForward;
	private boolean isForwardToBpk = false;
	private boolean isSingleAward;
	private boolean isMultipleAward;
	private boolean isOnlineBidding;
	private boolean isMultipleLoi;
	private boolean isItemRowSelect;
	private boolean isSubmit;
	private boolean isSecretaryUser;
	private boolean isProcCategoryMedicine;
	private boolean isZoneTabChange;
	private boolean disableSupplierSelection;
	private boolean itemFwdToHighAuthority;
	private boolean isInitialDecisionFwdHighAuth;
	private boolean isInitialDecisionFwdToBPK;
	private boolean isSelectedItemAlreadyAwarded;
	private boolean hasAwardedLOA;
	private boolean hasPreviousMeeting;
	private boolean hasItemWithPriceTypeTier;
	private boolean itemCancelled;
	private boolean refinalizedAndBiddingAward;
	private boolean disableFwdToHighAuthorityOption;
	private Boolean qtRefinalized;
	private boolean isInitialSelectionItemized;
	private ScQtEvaluationResultDtl shortlistedSupplier;
	private ScQtFinalization scQtFinalization;
	private TplSpecZoneDtl currentZone;
	private ScQtEvaluationResultDtl recommendedSupplier;
	private ScQtEvaluationResultDtl recommendedSupplierByItem;
	private ScQtEvaluationResultDtl prevRecommendedSupplierByItem;
	
	private String previousPageUrl;	
	
	//#28784
	private boolean isDemoReq;
	
//	private List<ScQtEvaluationResultDtl> qualifiedSuppliersByEvalStage;
	
	/** Qualified suppliers by evaluation stage */
	private IPagingDataModel<ScQtEvaluationResultDtl> qualifiedSuppliersByEvalStage;	
	
	/** Qualified suppliers by evaluation stage */
	private IPagingDataModel<ScQtEvaluationResultDtl> shortlistedSuppliersByRecommendType;	
	
	private Map<MapKey,List<QtEvaluationResultDtlBean>> qualifiedSuppliersCache;
	
//	private List<ScQtEvaluationResultDtl> shortlistedPackageList;
//	private List<ScQtEvaluationResultDtl> shortlistedSupplierList;
//	private List<ScQtEvaluationResultDtl> recommendedSupplierList;
//	private List<ScQtEvaluationResultDtl> unRecommendedSupplierList;
	private Set<ScQtEvaluationResultDtl> resetSupplierList;
	private List<ScQtRecommend> selectedItemList; 
	private List<ScQtRecommend> itemList;
	private List<SmSupplier> awardSupplierList; 
	private List<Long> rejectedScQtRecommendIds;
	
	private Map<Long, TplSpecZoneDtl> zoneMap;
//	private Map<Long, List<ScQtEvaluationResultDtl>> pkgShortlistedSupppliersByZone;
//	private Map<Long, List<ScQtEvaluationResultDtl>> recommendedSupppliersByZone;
	
	private Map<String,String[]> higherAuthoritiesMap;
	
	//Committee Endorsement
	private List<ScQtCommitteeActivity> reminderList;
	
	//Online Bidding Endorsement
	private List<ScQtCommitteeActivity> onlineBiddingEndorsementList;
	private List<ScQtCommitteeActivity> onlineBiddingReminderList;
	
	//#21695 Farid start
	private boolean pacDisable;
	//#21695 End
	
	//6906 note 117 - start
	private SelectItem[] decisionDropdown;
	private boolean supplierFinalizationReadOnly = true;
	private boolean disabledSelection;
	private boolean noProposalSubmitted;
	private List<String> disqualifiedStages;
	private boolean hasSupplierQualifiedForCurrentSelection;//6906 note 202
	//6906 note 117 - end
	private ScQtRecommend lastSelectedItem;//35157

	
	//20230711@Asyrafhaziq-SQUBE Bug Minor #16
    private final static List<String> directDownloadDocDefKeys = setDirectDownloadDocDefKeys(); 
	private static List<String> setDirectDownloadDocDefKeys(){
		List<String> list = new ArrayList<String>();
			
		list.add(SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT);
		list.add(SourcingConstants.DOC_DEF_KEY_EVALUATION_REPORT);
		list.add(SourcingConstants.DOC_DEF_KEY_OPENNING_REPORT);
		list.add(SourcingConstants.DOC_DEF_KEY_TECHNICAL_EVAALUATION_REPORT);
		list.add(SourcingConstants.DOC_DEF_KEY_FINANCIAL_EVAALUATION_REPORT);
			
	  return list;
	}
	
	private String subject;
	private String message;
	private String pinNo;
	
	private boolean selectAll;
	private boolean selectAllMeetings;
	private boolean selectAllMeetingAttendees;
	private String action;
	private Long loggedInUserId;
	private Long originalFinalizationSecretaryUserId;
	
	private int preparationTabIndex;
	private int reportTabIndex;
	private int finalizationTabIndex;
	private int commEndorsementTabIndex;
	private int endorsementReportActiveIndex;
	/*
	 * Enhancement #21694 START
	 */
	private int reverseTabIndex;
	private ScQtReverse scQtReverse;
	private boolean isReversed = false;
	/* Enhancement #21694 END*/
	
	private int evaluationStageId;
	private String evaluationHeader;
	
	/** The auto update. */
	private boolean autoUpdate = Boolean.TRUE;
	
	private Object[] selectedCommonSupplier;
	private IPagingDataModel<Object[]> shortlistedCommonSuppliers;
	private ScQtEvaluationResultDtl preferenceMarginDtls;
	private List<ScQtFinalizationEndorsementRecommendationRemarks> finalizationPengreysonremarks;
	private boolean renderjustificationTextbox;
	private PmReason justification;
	private List<ScQtFinalizationReport> endorsementReports;
	private ScQtFinalizationReport endorsementReport;
	private SelectItem[] justDropdown;
	private String outcome;
	
	private int evaluationReportIndex;
	
	//#25037-start
	private List<ScCommitteeMember> deletedMemberList;
	private SupplierFinalizationMeetingBackingBean meetingBB;
	//#25037-end
	
	//#25729#142 & #26169 - Start
	private boolean isSuppOfferPriceExceedCommunityAuthority;
	private BigDecimal maxAmountCommitteeAuthority;
	//#25729#142 & #26169 - End
	
	//#26169 #38 - Start
	private boolean isHasSupplierQualified;
	//#26169 #38 - End
	
	// #18685 - Start. 
	private List<ScRemark> pendingRecItemizedRemark;
	private List<ScRemark> pendingRecPackageRemark;
	
    // #25638 - Start.
	private List<ScRemark> newSfRemarks;
    // #25638 - End.
	private List<ScRemark> scRemarks;
	// #18685 - End. 
	
	// #27502 - Start 
	private boolean isMofFinalization;
	private boolean isBpkCentralContract;
	// #27502 - Start 
	
	//#31632
	private boolean isMofCntralCtc;
	
	//28834 starts
	private boolean disableBfButton;
	private boolean ktReportGenerated;
	private boolean disableJustifications;
	//28834 end
	
	// #11134 - Start
	private boolean haveSupplierRejection;
	// #11134 - End
	
	// #28777 & #29729 - Start
	private List<SmSupplierBranch> supplierBranchList;
	private IPagingDataModel<ScQtEvaluationResultDtl> disqualifiedSuppliersByRecommendType;
	private IPagingDataModel<ScQtEvaluationResultDtl> disqualifiedSuppliersByEvalStage;	
	// #28777 & #29729 - End
	
	//29562 - START
	private String finalizationMethodBefore;
	//29562 - END
	
	private ScQt checkCategoryCodeQt;//#29466
	
	//#30008 - start
	private boolean notReachAgrm;
	
	private boolean allSupplierDisagreeEPV;//#21692
	
	private List<WeightageScoreBean> weightageScoreBeans;//#38151
	
	private Long overallTechnicalRecommendationPercent;//#38151
	
	private Long overallFinancialRecommendationPercent;//#38151
	
	private List<ScQtScoreSetting> scoreSettings;//#38151
	
	private boolean WeightageReq;//#38151
	
	private boolean weightage35384WIP; //#38151
	
	// 26247 - start
	private List<String> disabledSupplierIds;

	// 26247 - end

	// 39077 -start-
	private boolean isReviseBriefMsg = Boolean.FALSE;

	// 39077 -end-
	
	//#35320 - Start
	private String selectedSupplierOfferedPrice;
	//#35320 - End
	
	/**
	 * @return the activeIndex
	 */
	public int getActiveIndex() {
		return activeIndex;
	}

	/**
	 * @param activeIndex 
	 * 				the activeIndex to set
	 */
	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	/**
	 * @return the prepareActiveIndex
	 */
	public int getPrepareActiveIndex() {
		return prepareActiveIndex;
	}

	/**
	 * @param prepareActiveIndex 
	 * 					the prepareActiveIndex to set
	 */
	public void setPrepareActiveIndex(int prepareActiveIndex) {
		this.prepareActiveIndex = prepareActiveIndex;
	}

	/**
	 * @return the scQt
	 */
	public ScQt getScQt() {
		return scQt;
	}

	/**
	 * @param scQt 
	 * 			the scQt to set
	 */
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}

	/**
	 * @return the scQtCommittee
	 */
	public ScQtCommittee getScQtCommittee() {
		return scQtCommittee;
	}

	/**
	 * @param scQtCommittee 
	 * 				the scQtCommittee to set
	 */
	public void setScQtCommittee(ScQtCommittee scQtCommittee) {
		this.scQtCommittee = scQtCommittee;
	}

	/**
	 * @return the meetingList
	 */
	public List<ScQtMeeting> getMeetingList() {
		return meetingList;
	}

	/**
	 * @param meetingList 
	 * 				the meetingList to set
	 */
	public void setMeetingList(List<ScQtMeeting> meetingList) {
		this.meetingList = meetingList;
	}

	/**
	 * @return the selectedMeetingList
	 */
	public List<ScQtMeeting> getSelectedMeetingList() {
		return selectedMeetingList;
	}

	/**
	 * @param selectedMeetingList 
	 * 					the selectedMeetingList to set
	 */
	public void setSelectedMeetingList(List<ScQtMeeting> selectedMeetingList) {
		this.selectedMeetingList = selectedMeetingList;
	}

	/**
	 * @return the deletedMeetingList
	 */
	public List<ScQtMeeting> getDeletedMeetingList() {
		return deletedMeetingList;
	}

	/**
	 * @param deletedMeetingList 
	 * 				the deletedMeetingList to set
	 */
	public void setDeletedMeetingList(List<ScQtMeeting> deletedMeetingList) {
		this.deletedMeetingList = deletedMeetingList;
	}

	/**
	 * @return the committeeList
	 */
	public List<ScQtCommitteeActivity> getCommitteeList() {
		return committeeList;
	}

	/**
	 * @param committeeList 
	 * 				the committeeList to set
	 */
	public void setCommitteeList(List<ScQtCommitteeActivity> committeeList) {
		this.committeeList = committeeList;
	}

	/**
	 * @return the committeeEndorsementList
	 */
	public List<ScQtCommitteeActivity> getCommitteeEndorsementList() {
		return committeeEndorsementList;
	}

	/**
	 * @param committeeEndorsementList 
	 * 				the committeeEndorsementList to set
	 */
	public void setCommitteeEndorsementList(
			List<ScQtCommitteeActivity> committeeEndorsementList) {
		this.committeeEndorsementList = committeeEndorsementList;
	}

	/**
	 * @return the committeeRemarkList
	 */
	public List<ScQtCommitteRemarks> getCommitteeRemarkList() {
		return committeeRemarkList;
	}

	/**
	 * @param committeeRemarkList 
	 * 					the committeeRemarkList to set
	 */
	public void setCommitteeRemarkList(List<ScQtCommitteRemarks> committeeRemarkList) {
		this.committeeRemarkList = committeeRemarkList;
	}

	/**
	 * @return the finalizationRemarks
	 */
	public List<ScQtFinalizationRemarks> getFinalizationRemarks() {
		return finalizationRemarks;
	}

	/**
	 * @param finalizationRemarks
	 *            the finalizationRemarks to set
	 */
	public void setFinalizationRemarks(
			List<ScQtFinalizationRemarks> finalizationRemarks) {
		this.finalizationRemarks = finalizationRemarks;
	}

	/**
	 * @return the finalizationEndorsementRemarks
	 */
	public List<ScQtFinalizationEndorsementRemarks> getFinalizationEndorsementRemarks() {
		return finalizationEndorsementRemarks;
	}

	/**
	 * @param finalizationEndorsementRemarks
	 *            the finalizationEndorsementRemarks to set
	 */
	public void setFinalizationEndorsementRemarks(
			List<ScQtFinalizationEndorsementRemarks> finalizationEndorsementRemarks) {
		this.finalizationEndorsementRemarks = finalizationEndorsementRemarks;
	}

	/**
	 * @return the meetingAttendanceList
	 */
	public List<ScQtMeetingAttendance> getMeetingAttendanceList() {
		return meetingAttendanceList;
	}

	/**
	 * @param meetingAttendanceList 
	 * 					the meetingAttendanceList to set
	 */
	public void setMeetingAttendanceList(
			List<ScQtMeetingAttendance> meetingAttendanceList) {
		this.meetingAttendanceList = meetingAttendanceList;
	}

	/**
	 * @return the selectedMeetingAttList
	 */
	public List<ScQtMeetingAttendance> getSelectedMeetingAttList() {
		return selectedMeetingAttList;
	}

	/**
	 * @param selectedMeetingAttList 
	 * 					the selectedMeetingAttList to set
	 */
	public void setSelectedMeetingAttList(
			List<ScQtMeetingAttendance> selectedMeetingAttList) {
		this.selectedMeetingAttList = selectedMeetingAttList;
	}
  	
//
//	/**
//	 * @return the scQtReport
//	 */
//	public ScQtReport getScQtReport() {
//		return scQtReport;
//	}
//
//	/**
//	 * @param scQtReport the scQtReport to set
//	 */
//	public void setScQtReport(ScQtReport scQtReport) {
//		this.scQtReport = scQtReport;
//	}

	/**
	 * @return the deletedMeetingAttendanceList
	 */
	public List<ScQtMeetingAttendance> getDeletedMeetingAttendanceList() {
		return deletedMeetingAttendanceList;
	}

	/**
	 * @param deletedMeetingAttendanceList 
	 * 				the deletedMeetingAttendanceList to set
	 */
	public void setDeletedMeetingAttendanceList(
			List<ScQtMeetingAttendance> deletedMeetingAttendanceList) {
		this.deletedMeetingAttendanceList = deletedMeetingAttendanceList;
	}

	/**
	 * @return the documentList
	 */
	public List<ScQtFinalizationReport> getDocumentList() {
		return documentList;
	}

	/**
	 * @param documentList 
	 * 				the documentList to set
	 */
	public void setDocumentList(List<ScQtFinalizationReport> documentList) {
		this.documentList = documentList;
	}

	/**
	 * @return the selectedFinalizationReport
	 */
	public ScQtFinalizationReport getSelectedFinalizationReport() {
		return selectedFinalizationReport;
	}

	/**
	 * @param selectedFinalizationReport
	 *            the selectedFinalizationReport to set
	 */
	public void setSelectedFinalizationReport(
			ScQtFinalizationReport selectedFinalizationReport) {
		this.selectedFinalizationReport = selectedFinalizationReport;
	}

	/**
	 * @return the finalizationMethod
	 */
	public String getFinalizationMethod() {
		return finalizationMethod;
	}

	/**
	 * @param finalizationMethod 
	 * 				the finalizationMethod to set
	 */
	public void setFinalizationMethod(String finalizationMethod) {
		this.finalizationMethod = finalizationMethod;
	}

	/**
	 * @return the isItemize
	 */
	public boolean isItemize() {
		return isItemize;
	}

	/**
	 * @param isItemize 
	 * 				the isItemize to set
	 */
	public void setItemize(boolean isItemize) {
		this.isItemize = isItemize;
	}

	/**
	 * @return the isRecommendSupplier
	 */
	public boolean isRecommendSupplier() {
		return isRecommendSupplier;
	}

	/**
	 * @param isRecommendSupplier 
	 * 					the isRecommendSupplier to set
	 */
	public void setRecommendSupplier(boolean isRecommendSupplier) {
		this.isRecommendSupplier = isRecommendSupplier;
	}

	/**
	 * @return the isReEvaluate
	 */
	public boolean isReEvaluate() {
		return isReEvaluate;
	}

	/**
	 * @param isReEvaluate 
	 * 				the isReEvaluate to set
	 */
	public void setReEvaluate(boolean isReEvaluate) {
		this.isReEvaluate = isReEvaluate;
	}

	/**
	 * @return the isRePublish
	 */
	public boolean isRePublish() {
		return isRePublish;
	}

	/**
	 * @param isRePublish 
	 * 				the isRePublish to set
	 */
	public void setRePublish(boolean isRePublish) {
		this.isRePublish = isRePublish;
	}

	/**
	 * @return the isForward
	 */
	public boolean isForward() {
		return isForward;
	}

	/**
	 * @param isForward 
	 * 				the isForward to set
	 */
	public void setForward(boolean isForward) {
		this.isForward = isForward;
	}

	/**
	 * @return the isSingleAward
	 */
	public boolean isSingleAward() {
		return isSingleAward;
	}

	/**
	 * @param isSingleAward 
	 * 					the isSingleAward to set
	 */
	public void setSingleAward(boolean isSingleAward) {
		this.isSingleAward = isSingleAward;
	}

	/**
	 * @return the isMultipleAward
	 */
	public boolean isMultipleAward() {
		return isMultipleAward;
	}

	/**
	 * @param isMultipleAward 
	 * 					the isMultipleAward to set
	 */
	public void setMultipleAward(boolean isMultipleAward) {
		this.isMultipleAward = isMultipleAward;
	}

	/**
	 * @return the isOnlineBidding
	 */
	public boolean isOnlineBidding() {
		return isOnlineBidding;
	}

	/**
	 * @param isOnlineBidding 
	 * 					the isOnlineBidding to set
	 */
	public void setOnlineBidding(boolean isOnlineBidding) {
		this.isOnlineBidding = isOnlineBidding;
	}

	/**
	 * @return the isMultipleLoi
	 */
	public boolean isMultipleLoi() {
		return isMultipleLoi;
	}

	/**
	 * @param isMultipleLoi 
	 * 					the isMultipleLoi to set
	 */
	public void setMultipleLoi(boolean isMultipleLoi) {
		this.isMultipleLoi = isMultipleLoi;
	}

	/**
	 * @return the isItemRowSelect
	 */
	public boolean isItemRowSelect() {
		return isItemRowSelect;
	}

	/**
	 * @param isItemRowSelect 
	 * 					the isItemRowSelect to set
	 */
	public void setItemRowSelect(boolean isItemRowSelect) {
		this.isItemRowSelect = isItemRowSelect;
	}

	/**
	 * @return the isSubmit
	 */
	public boolean isSubmit() {
		return isSubmit;
	}

	/**
	 * @param isSubmit 
	 * 				the isSubmit to set
	 */
	public void setSubmit(boolean isSubmit) {
		this.isSubmit = isSubmit;
	}

	/**
	 * @return the isSecretaryUser
	 */
	public boolean isSecretaryUser() {
		return isSecretaryUser;
	}

	/**
	 * @param isSecretaryUser 
	 * 					the isSecretaryUser to set
	 */
	public void setSecretaryUser(boolean isSecretaryUser) {
		this.isSecretaryUser = isSecretaryUser;
	}

	/**
	 * @return the isProcCategoryMedicine
	 */
	public boolean isProcCategoryMedicine() {
		return isProcCategoryMedicine;
	}

	/**
	 * @param isProcCategoryMedicine 
	 * 							the isProcCategoryMedicine to set
	 */
	public void setProcCategoryMedicine(boolean isProcCategoryMedicine) {
		this.isProcCategoryMedicine = isProcCategoryMedicine;
	}

	/**
	 * @return the isZoneTabChange
	 */
	public boolean isZoneTabChange() {
		return isZoneTabChange;
	}

	/**
	 * @param isZoneTabChange 
	 * 				the isZoneTabChange to set
	 */
	public void setZoneTabChange(boolean isZoneTabChange) {
		this.isZoneTabChange = isZoneTabChange;
	}

	/**
	 * @return the disableSupplierSelection
	 */
	public boolean isDisableSupplierSelection() {
		return disableSupplierSelection;
	}

	/**
	 * @param disableSupplierSelection 
	 * 					the disableSupplierSelection to set
	 */
	public void setDisableSupplierSelection(
						boolean disableSupplierSelection) {
		this.disableSupplierSelection = disableSupplierSelection;
	}

	/**
	 * @return the itemFwdToHighAuthority
	 */
	public boolean isItemFwdToHighAuthority() {
		return itemFwdToHighAuthority;
	}

	/**
	 * @param itemFwdToHighAuthority 
	 * 					the itemFwdToHighAuthority to set
	 */
	public void setItemFwdToHighAuthority(
						boolean itemFwdToHighAuthority) {
		this.itemFwdToHighAuthority = itemFwdToHighAuthority;
	}

	/**
	 * @return the isInitialSelectionItemized
	 */
	public boolean isInitialSelectionItemized() {
		return isInitialSelectionItemized;
	}

	/**
	 * @param isInitialSelectionItemized 
	 * 				the isInitialSelectionItemized to set
	 */
	public void setInitialSelectionItemized(
					boolean isInitialSelectionItemized) {
		this.isInitialSelectionItemized = isInitialSelectionItemized;
	}

	/**
	 * @return the isInitialDecisionFwdHighAuth
	 */
	public boolean isInitialDecisionFwdHighAuth() {
		return isInitialDecisionFwdHighAuth;
	}

	/**
	 * @param isInitialDecisionFwdHighAuth 
	 * 				the isInitialDecisionFwdHighAuth to set
	 */
	public void setInitialDecisionFwdHighAuth(
				boolean isInitialDecisionFwdHighAuth) {
		this.isInitialDecisionFwdHighAuth = isInitialDecisionFwdHighAuth;
	}

	/**
	 * @return the isInitialDecisionFwdToBPK
	 */
	public boolean isInitialDecisionFwdToBPK() {
		return isInitialDecisionFwdToBPK;
	}

	/**
	 * @param isInitialDecisionFwdToBPK 
	 * 				the isInitialDecisionFwdToBPK to set
	 */
	public void setInitialDecisionFwdToBPK(
					boolean isInitialDecisionFwdToBPK) {
		this.isInitialDecisionFwdToBPK = isInitialDecisionFwdToBPK;
	}

	/**
	 * @return the isSelectedItemAlreadyAwarded
	 */
	public boolean isSelectedItemAlreadyAwarded() {
		return isSelectedItemAlreadyAwarded;
	}

	/**
	 * @param isSelectedItemAlreadyAwarded 
	 * 				the isSelectedItemAlreadyAwarded to set
	 */
	public void setSelectedItemAlreadyAwarded(
						boolean isSelectedItemAlreadyAwarded) {
		this.isSelectedItemAlreadyAwarded = isSelectedItemAlreadyAwarded;
	}

	/**
	 * @return the hasAwardedLOA
	 */
	public boolean isHasAwardedLOA() {
		return hasAwardedLOA;
	}

	/**
	 * @param hasAwardedLOA 
	 * 				the hasAwardedLOA to set
	 */
	public void setHasAwardedLOA(boolean hasAwardedLOA) {
		this.hasAwardedLOA = hasAwardedLOA;
	}

	/**
	 * @return the hasPreviousMeeting
	 */
	public boolean isHasPreviousMeeting() {
		return hasPreviousMeeting;
	}

	/**
	 * @param hasPreviousMeeting 
	 * 				the hasPreviousMeeting to set
	 */
	public void setHasPreviousMeeting(boolean hasPreviousMeeting) {
		this.hasPreviousMeeting = hasPreviousMeeting;
	}

	/**
	 * @return the hasItemWithPriceTypeTier
	 */
	public boolean isHasItemWithPriceTypeTier() {
		return hasItemWithPriceTypeTier;
	}

	/**
	 * @param hasItemWithPriceTypeTier 
	 * 				the hasItemWithPriceTypeTier to set
	 */
	public void setHasItemWithPriceTypeTier(
						boolean hasItemWithPriceTypeTier) {
		this.hasItemWithPriceTypeTier = hasItemWithPriceTypeTier;
	}

	/**
	 * @return the itemCancelled
	 */
	public boolean isItemCancelled() {
		return itemCancelled;
	}

	/**
	 * @param itemCancelled 
	 * 				the itemCancelled to set
	 */
	public void setItemCancelled(boolean itemCancelled) {
		this.itemCancelled = itemCancelled;
	}
	
	/**
	 * @return the qtRefinalized
	 */
	public Boolean getQtRefinalized() {
		return qtRefinalized;
	}

	/**
	 * @param qtRefinalized 
	 * 				the qtRefinalized to set
	 */
	public void setQtRefinalized(Boolean qtRefinalized) {
		this.qtRefinalized = qtRefinalized;
	}

	/**
	 * @return the refinalizedAndBiddingAward
	 */
	public boolean isRefinalizedAndBiddingAward() {
		return refinalizedAndBiddingAward;
	}

	/**
	 * @param refinalizedAndBiddingAward 
	 * 				the refinalizedAndBiddingAward to set
	 */
	public void setRefinalizedAndBiddingAward(
						boolean refinalizedAndBiddingAward) {
		this.refinalizedAndBiddingAward = refinalizedAndBiddingAward;
	}

	/**
	 * @return the disableFwdToHighAuthorityOption
	 */
	public boolean isDisableFwdToHighAuthorityOption() {
		return disableFwdToHighAuthorityOption;
	}

	/**
	 * @param disableFwdToHighAuthorityOption
	 *            the disableFwdToHighAuthorityOption to set
	 */
	public void setDisableFwdToHighAuthorityOption(
			boolean disableFwdToHighAuthorityOption) {
		this.disableFwdToHighAuthorityOption = disableFwdToHighAuthorityOption;
	}

//	/**
//	 * @return the shortlistedSupplierList
//	 */
//	public List<ScQtEvaluationResultDtl> getShortlistedSupplierList() {
//		return shortlistedSupplierList;
//	}
//
//	/**
//	 * @param shortlistedSupplierList 
//	 * 						the shortlistedSupplierList to set
//	 */
//	public void setShortlistedSupplierList(
//			List<ScQtEvaluationResultDtl> shortlistedSupplierList) {
//		this.shortlistedSupplierList = shortlistedSupplierList;
//	}
//
//	/**
//	 * @return the recommendedSupplierList
//	 */
//	public List<ScQtEvaluationResultDtl> getRecommendedSupplierList() {
//		return recommendedSupplierList;
//	}
//
//	/**
//	 * @param recommendedSupplierList 
//	 * 						the recommendedSupplierList to set
//	 */
//	public void setRecommendedSupplierList(
//			List<ScQtEvaluationResultDtl> recommendedSupplierList) {
//		this.recommendedSupplierList = recommendedSupplierList;
//	}

	/**
	 * @return the selectionMode
	 */
	public String getSelectionMode() {
		return selectionMode;
	}

	/**
	 * @param selectionMode 
	 * 					the selectionMode to set
	 */
	public void setSelectionMode(String selectionMode) {
		this.selectionMode = selectionMode;
	}

	/**
	 * @return the shortlistedSupplier
	 */
	public ScQtEvaluationResultDtl getShortlistedSupplier() {
		return shortlistedSupplier;
	}

	/**
	 * @param shortlistedSupplier 
	 * 						the shortlistedSupplier to set
	 */
	public void setShortlistedSupplier(ScQtEvaluationResultDtl shortlistedSupplier) {
		this.shortlistedSupplier = shortlistedSupplier;
	}

	/**
	 * @return the awardSupplier
	 */
	public String getAwardSupplier() {
		return awardSupplier;
	}

	/**
	 * @param awardSupplier 
	 * 					the awardSupplier to set
	 */
	public void setAwardSupplier(String awardSupplier) {
		this.awardSupplier = awardSupplier;
	}

	/**
	 * @return the awardSupplierList
	 */
	public List<SmSupplier> getAwardSupplierList() {
		return awardSupplierList;
	}

	/**
	 * @param awardSupplierList 
	 * 					the awardSupplierList to set
	 */
	public void setAwardSupplierList(List<SmSupplier> awardSupplierList) {
		this.awardSupplierList = awardSupplierList;
	}

	/**
	 * @return the rejectedScQtRecommendIds
	 */
	public List<Long> getRejectedScQtRecommendIds() {
		return rejectedScQtRecommendIds;
	}

	/**
	 * @param rejectedScQtRecommendIds 
	 * 				the rejectedScQtRecommendIds to set
	 */
	public void setRejectedScQtRecommendIds(List<Long> rejectedScQtRecommendIds) {
		this.rejectedScQtRecommendIds = rejectedScQtRecommendIds;
	}

	/**
	 * @return the zoneMap
	 */
	public Map<Long, TplSpecZoneDtl> getZoneMap() {
		return zoneMap;
	}

	/**
	 * @param zoneMap the zoneMap to set
	 */
	public void setZoneMap(Map<Long, TplSpecZoneDtl> zoneMap) {
		this.zoneMap = zoneMap;
	}

//	/**
//	 * @return the pkgShortlistedSupppliersByZone
//	 */
//	public Map<Long, List<ScQtEvaluationResultDtl>> getPkgShortlistedSupppliersByZone() {
//		return pkgShortlistedSupppliersByZone;
//	}
//
//	/**
//	 * @param pkgShortlistedSupppliersByZone the pkgShortlistedSupppliersByZone to set
//	 */
//	public void setPkgShortlistedSupppliersByZone(
//			Map<Long, List<ScQtEvaluationResultDtl>> pkgShortlistedSupppliersByZone) {
//		this.pkgShortlistedSupppliersByZone = pkgShortlistedSupppliersByZone;
//	}

//	/**
//	 * @return the recommendedSupppliersByZone
//	 */
//	public Map<Long, List<ScQtEvaluationResultDtl>> 
//				getRecommendedSupppliersByZone() {
//		return recommendedSupppliersByZone;
//	}
//
//	/**
//	 * @param recommendedSupppliersByZone 
//	 * 				the recommendedSupppliersByZone to set
//	 */
//	public void setRecommendedSupppliersByZone(
//			Map<Long, List<ScQtEvaluationResultDtl>> recommendedSupppliersByZone) {
//		this.recommendedSupppliersByZone = recommendedSupppliersByZone;
//	}

	/**
	 * @return the scQtFinalization
	 */
	public ScQtFinalization getScQtFinalization() {
		return scQtFinalization;
	}

	/**
	 * @param scQtFinalization 
	 * 					the scQtFinalization to set
	 */
	public void setScQtFinalization(ScQtFinalization scQtFinalization) {
		this.scQtFinalization = scQtFinalization;
	}

	/**
	 * @return the higherAuthoritiesMap
	 */
	public Map<String, String[]> getHigherAuthoritiesMap() {
		return higherAuthoritiesMap;
	}

	/**
	 * @param higherAuthoritiesMap
	 *            the higherAuthoritiesMap to set
	 */
	public void setHigherAuthoritiesMap(
			Map<String, String[]> higherAuthoritiesMap) {
		this.higherAuthoritiesMap = higherAuthoritiesMap;
	}

	/**
	 * @return the reminderList
	 */
	public List<ScQtCommitteeActivity> getReminderList() {
		return reminderList;
	}

	/**
	 * @param reminderList 
	 * 					the reminderList to set
	 */
	public void setReminderList(List<ScQtCommitteeActivity> reminderList) {
		this.reminderList = reminderList;
	}

	/**
	 * @return the pinNo
	 */
	public String getPinNo() {
		return pinNo;
	}

	/**
	 * @param pinNo 
	 * 			the pinNo to set
	 */
	public void setPinNo(String pinNo) {
		this.pinNo = pinNo;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject 
	 * 			the subject to set
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
	 * @param message 
	 * 			the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the zoneActiveIndex
	 */
	public int getZoneActiveIndex() {
		return zoneActiveIndex;
	}

	/**
	 * @param zoneActiveIndex 
	 * 					the zoneActiveIndex to set
	 */
	public void setZoneActiveIndex(int zoneActiveIndex) {
		this.zoneActiveIndex = zoneActiveIndex;
	}

	/**
	 * @return the tplSpecZoneDtl
	 */
	public TplSpecZoneDtl getCurrentZone() {
		return currentZone;
	}

	/**
	 * @param tplSpecZoneDtl 
	 * 					the tplSpecZoneDtl to set
	 */
	public void setCurrentZone(TplSpecZoneDtl currentZone) {
		this.currentZone = currentZone;
	}

	/**
	 * @return the recommendedSupplier
	 */
	public ScQtEvaluationResultDtl getRecommendedSupplier() {
		return recommendedSupplier;
	}

	/**
	 * @param recommendedSupplier 
	 * 						the recommendedSupplier to set
	 */
	public void setRecommendedSupplier(ScQtEvaluationResultDtl recommendedSupplier) {
		this.recommendedSupplier = recommendedSupplier;
	}

	/**
	 * @return the recommendedSupplierByItem
	 */
	public ScQtEvaluationResultDtl getRecommendedSupplierByItem() {
		return recommendedSupplierByItem;
	}

	/**
	 * @param recommendedSupplierByItem 
	 * 					the recommendedSupplierByItem to set
	 */
	public void setRecommendedSupplierByItem(
			ScQtEvaluationResultDtl recommendedSupplierByItem) {
		this.recommendedSupplierByItem = recommendedSupplierByItem;
	}

	/**
	 * @return the prevRecommendedSupplierByItem
	 */
	public ScQtEvaluationResultDtl getPrevRecommendedSupplierByItem() {
		return prevRecommendedSupplierByItem;
	}

	/**
	 * @param prevRecommendedSupplierByItem 
	 * 					the prevRecommendedSupplierByItem to set
	 */
	public void setPrevRecommendedSupplierByItem(
			ScQtEvaluationResultDtl prevRecommendedSupplierByItem) {
		this.prevRecommendedSupplierByItem = prevRecommendedSupplierByItem;
	}

	/**
	 * @return the isForwardToBpk
	 */
	public boolean isForwardToBpk() {
		return isForwardToBpk;
	}

	/**
	 * @param isForwardToBpk 
	 * 					the isForwardToBpk to set
	 */
	public void setForwardToBpk(boolean isForwardToBpk) {
		this.isForwardToBpk = isForwardToBpk;
	}

	/**
	 * @return the onlineBiddingActiveIndex
	 */
	public int getOnlineBiddingActiveIndex() {
		return onlineBiddingActiveIndex;
	}

	/**
	 * @param onlineBiddingActiveIndex 
	 * 							the onlineBiddingActiveIndex to set
	 */
	public void setOnlineBiddingActiveIndex(int onlineBiddingActiveIndex) {
		this.onlineBiddingActiveIndex = onlineBiddingActiveIndex;
	}

	/**
	 * @return the onlineBiddingEndorsementList
	 */
	public List<ScQtCommitteeActivity> getOnlineBiddingEndorsementList() {
		return onlineBiddingEndorsementList;
	}

	/**
	 * @param onlineBiddingEndorsementList 
	 * 							the onlineBiddingEndorsementList to set
	 */
	public void setOnlineBiddingEndorsementList(
			List<ScQtCommitteeActivity> onlineBiddingEndorsementList) {
		this.onlineBiddingEndorsementList = onlineBiddingEndorsementList;
	}

	/**
	 * @return the onlineBiddingReminderList
	 */
	public List<ScQtCommitteeActivity> getOnlineBiddingReminderList() {
		return onlineBiddingReminderList;
	}

	/**
	 * @param onlineBiddingReminderList 
	 * 							the onlineBiddingReminderList to set
	 */
	public void setOnlineBiddingReminderList(
			List<ScQtCommitteeActivity> onlineBiddingReminderList) {
		this.onlineBiddingReminderList = onlineBiddingReminderList;
	}

	/**
	 * @return the selectedItem
	 */
	public ScQtRecommend getSelectedItem() {
		return selectedItem;
	}

	/**
	 * @param selectedItem
	 *            the selectedItem to set
	 */
	public void setSelectedItem(ScQtRecommend selectedItem) {
		this.selectedItem = selectedItem;
	}

	/**
	 * @return the supplierBranch
	 */
	public String getSupplierBranch() {
		return supplierBranch;
	}

	/**
	 * @param supplierBranch 
	 * 					the supplierBranch to set
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
	 * @param supplierAddress 
	 * 					the supplierAddress to set
	 */
	public void setSupplierAddress(SmAddress supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	/**
	 * @return the smPersonnel
	 */
	public SmPersonnel getSmPersonnel() {
		return smPersonnel;
	}

	/**
	 * @param smPersonnel 
	 * 				the smPersonnel to set
	 */
	public void setSmPersonnel(SmPersonnel smPersonnel) {
		this.smPersonnel = smPersonnel;
	}

	/**
	 * @return the boardofDirectors
	 */
	public List<SmPersonnel> getBoardofDirectors() {
		return boardofDirectors;
	}

	/**
	 * @param boardofDirectors 
	 * 					the boardofDirectors to set
	 */
	public void setBoardofDirectors(List<SmPersonnel> boardofDirectors) {
		this.boardofDirectors = boardofDirectors;
	}

	/**
	 * @return the smCompanyBasic
	 */
	public SmCompanyBasic getSmCompanyBasic() {
		return smCompanyBasic;
	}

	/**
	 * @param smCompanyBasic 
	 * 					the smCompanyBasic to set
	 */
	public void setSmCompanyBasic(SmCompanyBasic smCompanyBasic) {
		this.smCompanyBasic = smCompanyBasic;
	}

	/**
	 * @return the smMofAccount
	 */
	public SmMofAccount getSmMofAccount() {
		return smMofAccount;
	}

	/**
	 * @param smMofAccount 
	 * 				the smMofAccount to set
	 */
	public void setSmMofAccount(SmMofAccount smMofAccount) {
		this.smMofAccount = smMofAccount;
	}

	/**
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * @param smSupplier 
	 * 				the smSupplier to set
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	/**
	 * @return the attachmentDocType
	 */
	public String getAttachmentDocType() {
		return attachmentDocType;
	}

	/**
	 * @param attachmentDocType
	 *            the attachmentDocType to set
	 */
	public void setAttachmentDocType(String attachmentDocType) {
		this.attachmentDocType = attachmentDocType;
	}

	/**
	 * @return the tabList
	 */
	public List<TabBean> getTabList() {
		return tabList;
	}

	/**
	 * @param tabList 
	 * 			the tabList to set
	 */
	public void setTabList(List<TabBean> tabList) {
		this.tabList = tabList;
	}

	/**
	 * @return the selectedTabBean
	 */
	public TabBean getSelectedTabBean() {
		return selectedTabBean;
	}

	/**
	 * @param selectedTabBean 
	 * 					the selectedTabBean to set
	 */
	public void setSelectedTabBean(TabBean selectedTabBean) {
		this.selectedTabBean = selectedTabBean;
	}

	/**
	 * @return the committeeTabLabel
	 */
	public String getCommitteeTabLabel() {
		return committeeTabLabel;
	}

	/**
	 * @param committeeTabLabel 
	 * 					the committeeTabLabel to set
	 */
	public void setCommitteeTabLabel(String committeeTabLabel) {
		this.committeeTabLabel = committeeTabLabel;
	}

	/**
	 * @return the mainMenuModel
	 */
	public MenuModel getMainMenuModel() {
		return mainMenuModel;
	}

	/**
	 * @param mainMenuModel
	 *            the mainMenuModel to set
	 */
	public void setMainMenuModel(MenuModel mainMenuModel) {
		this.mainMenuModel = mainMenuModel;
	}

	/**
	 * @return the zoneMenuModel
	 */
	public MenuModel getZoneMenuModel() {
		return zoneMenuModel;
	}

	/**
	 * @param zoneMenuModel 
	 * 				the zoneMenuModel to set
	 */
	public void setZoneMenuModel(MenuModel zoneMenuModel) {
		this.zoneMenuModel = zoneMenuModel;
	}

	/**
	 * @return the processName
	 */
	public String getProcessName() {
		return processName;
	}

	/**
	 * @param processName 
	 * 				the processName to set
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	/**
	 * @return the selectedItemList
	 */
	public List<ScQtRecommend> getSelectedItemList() {
		return selectedItemList;
	}

	/**
	 * @param selectedItemList 
	 * 				the selectedItemList to set
	 */
	public void setSelectedItemList(List<ScQtRecommend> selectedItemList) {
		this.selectedItemList = selectedItemList;
	}

	/**
	 * @return the itemList
	 */
	public List<ScQtRecommend> getItemList() {
		return itemList;
	}

	/**
	 * @param itemList 
	 * 			the itemList to set
	 */
	public void setItemList(List<ScQtRecommend> itemList) {
		this.itemList = itemList;
	}

	/**
	 * @return the zoneDetails
	 */
	public List<TplSpecZoneDtl> getZoneDetails() {
		return zoneDetails;
	}

	/**
	 * @param zoneDetails 
	 * 				the zoneDetails to set
	 */
	public void setZoneDetails(List<TplSpecZoneDtl> zoneDetails) {
		this.zoneDetails = zoneDetails;
	}

//	/**
//	 * @return the unRecommendedSupplierList
//	 */
//	public List<ScQtEvaluationResultDtl> getUnRecommendedSupplierList() {
//		return unRecommendedSupplierList;
//	}
//
//	/**
//	 * @param unRecommendedSupplierList 
//	 * 					the unRecommendedSupplierList to set
//	 */
//	public void setUnRecommendedSupplierList(
//			List<ScQtEvaluationResultDtl> unRecommendedSupplierList) {
//		this.unRecommendedSupplierList = unRecommendedSupplierList;
//	}

	/**
	 * @return the resetSupplierList
	 */
	public Set<ScQtEvaluationResultDtl> getResetSupplierList() {
		return resetSupplierList;
	}

	/**
	 * @param resetSupplierList 
	 * 				the resetSupplierList to set
	 */
	public void setResetSupplierList(
			Set<ScQtEvaluationResultDtl> resetSupplierList) {
		this.resetSupplierList = resetSupplierList;
	}

//	/**
//	 * @return the qualifiedSuppliersByEvalStage
//	 */
//	public List<ScQtEvaluationResultDtl> getQualifiedSuppliersByEvalStage() {
//		return qualifiedSuppliersByEvalStage;
//	}
//
//	/**
//	 * @param qualifiedSuppliersByEvalStage
//	 *            the qualifiedSuppliersByEvalStage to set
//	 */
//	public void setQualifiedSuppliersByEvalStage(
//			List<ScQtEvaluationResultDtl> qualifiedSuppliersByEvalStage) {
//		this.qualifiedSuppliersByEvalStage = qualifiedSuppliersByEvalStage;
//	}

//	/**
//	 * @return the shortlistedPackageList
//	 */
//	public List<ScQtEvaluationResultDtl> getShortlistedPackageList() {
//		return shortlistedPackageList;
//	}

	/**
	 * @return the qualifiedSuppliersByEvalStage
	 */
	public IPagingDataModel<ScQtEvaluationResultDtl> getQualifiedSuppliersByEvalStage() {
		return qualifiedSuppliersByEvalStage;
	}

	/**
	 * @param qualifiedSuppliersByEvalStage
	 *            the qualifiedSuppliersByEvalStage to set
	 */
	public void setQualifiedSuppliersByEvalStage(
			IPagingDataModel<ScQtEvaluationResultDtl> qualifiedSuppliersByEvalStage) {
		this.qualifiedSuppliersByEvalStage = qualifiedSuppliersByEvalStage;
	}

	/**
	 * @return the shortlistedSuppliersByRecommendType
	 */
	public IPagingDataModel<ScQtEvaluationResultDtl> getShortlistedSuppliersByRecommendType() {
		return shortlistedSuppliersByRecommendType;
	}

	/**
	 * @param shortlistedSuppliersByRecommendType
	 *            the shortlistedSuppliersByRecommendType to set
	 */
	public void setShortlistedSuppliersByRecommendType(
			IPagingDataModel<ScQtEvaluationResultDtl> shortlistedSuppliersByRecommendType) {
		this.shortlistedSuppliersByRecommendType = shortlistedSuppliersByRecommendType;
	}

	/**
	 * @return the qualifiedSuppliersCache
	 */
	public Map<MapKey, List<QtEvaluationResultDtlBean>> getQualifiedSuppliersCache() {
		return qualifiedSuppliersCache;
	}

	/**
	 * @param qualifiedSuppliersCache
	 *            the qualifiedSuppliersCache to set
	 */
	public void setQualifiedSuppliersCache(
			Map<MapKey, List<QtEvaluationResultDtlBean>> qualifiedSuppliersCache) {
		this.qualifiedSuppliersCache = qualifiedSuppliersCache;
	}

//	/**
//	 * @param shortlistedPackageList 
//	 * 						the shortlistedPackageList to set
//	 */
//	public void setShortlistedPackageList(
//			List<ScQtEvaluationResultDtl> shortlistedPackageList) {
//		this.shortlistedPackageList = shortlistedPackageList;
//	}

	/**
	 * @return the isCancel
	 */
	public boolean isCancel() {
		return isCancel;
	}

	/**
	 * @param isCancel 
	 * 			the isCancel to set
	 */
	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	/**
	 * @return the isForwardToHigherAuthority
	 */
	public boolean isForwardToHigherAuthority() {
		return isForwardToHigherAuthority;
	}

	/**
	 * @param isForwardToHigherAuthority 
	 * 						the isForwardToHigherAuthority to set
	 */
	public void setForwardToHigherAuthority(boolean isForwardToHigherAuthority) {
		this.isForwardToHigherAuthority = isForwardToHigherAuthority;
	}

	/**
	 * @return the committeeType
	 */
	public String getCommitteeType() {
		return committeeType;
	}

	/**
	 * @param committeeType 
	 * 				the committeeType to set
	 */
	public void setCommitteeType(String committeeType) {
		this.committeeType = committeeType;
	}

	/**
	 * @return the selectAll
	 */
	public boolean isSelectAll() {
		return selectAll;
	}

	/**
	 * @param selectAll 
	 * 				the selectAll to set
	 */
	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	/**
	 * @return the selectAllMeetings
	 */
	public boolean isSelectAllMeetings() {
		return selectAllMeetings;
	}

	/**
	 * @param selectAllMeetings 
	 * 				the selectAllMeetings to set
	 */
	public void setSelectAllMeetings(boolean selectAllMeetings) {
		this.selectAllMeetings = selectAllMeetings;
	}

	/**
	 * @return the selectAllMeetingAttendees
	 */
	public boolean isSelectAllMeetingAttendees() {
		return selectAllMeetingAttendees;
	}

	/**
	 * @param selectAllMeetingAttendees
	 *            the selectAllMeetingAttendees to set
	 */
	public void setSelectAllMeetingAttendees(boolean selectAllMeetingAttendees) {
		this.selectAllMeetingAttendees = selectAllMeetingAttendees;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action 
	 * 			the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the loggedInUserId
	 */
	public Long getLoggedInUserId() {
		return loggedInUserId;
	}

	/**
	 * @param loggedInUserId 
	 * 				the loggedInUserId to set
	 */
	public void setLoggedInUserId(Long loggedInUserId) {
		this.loggedInUserId = loggedInUserId;
	}

	/**
	 * @return the originalFinalizationSecretaryUserId
	 */
	public Long getOriginalFinalizationSecretaryUserId() {
		return originalFinalizationSecretaryUserId;
	}

	/**
	 * @param originalFinalizationSecretaryUserId
	 *            the originalFinalizationSecretaryUserId to set
	 */
	public void setOriginalFinalizationSecretaryUserId(
			Long originalFinalizationSecretaryUserId) {
		this.originalFinalizationSecretaryUserId = originalFinalizationSecretaryUserId;
	}

	/**
	 * @return the preparationTabIndex
	 */
	public int getPreparationTabIndex() {
		return preparationTabIndex;
	}

	/**
	 * @param preparationTabIndex 
	 * 				the preparationTabIndex to set
	 */
	public void setPreparationTabIndex(int preparationTabIndex) {
		this.preparationTabIndex = preparationTabIndex;
	}

	/**
	 * @return the reportTabIndex
	 */
	public int getReportTabIndex() {
		return reportTabIndex;
	}

	/**
	 * @param reportTabIndex 
	 * 				the reportTabIndex to set
	 */
	public void setReportTabIndex(int reportTabIndex) {
		this.reportTabIndex = reportTabIndex;
	}

	/**
	 * @return the finalizationTabIndex
	 */
	public int getFinalizationTabIndex() {
		return finalizationTabIndex;
	}

	/**
	 * @param finalizationTabIndex 
	 * 				the finalizationTabIndex to set
	 */
	public void setFinalizationTabIndex(
						int finalizationTabIndex) {
		this.finalizationTabIndex = finalizationTabIndex;
	}

	/**
	 * @return the commEndorsementTabIndex
	 */
	public int getCommEndorsementTabIndex() {
		return commEndorsementTabIndex;
	}

	/**
	 * @param commEndorsementTabIndex 
	 * 				the commEndorsementTabIndex to set
	 */
	public void setCommEndorsementTabIndex(
						int commEndorsementTabIndex) {
		this.commEndorsementTabIndex = commEndorsementTabIndex;
	}

	/**
	 * @return the endorsementReportActiveIndex
	 */
	public int getEndorsementReportActiveIndex() {
		return endorsementReportActiveIndex;
	}

	/**
	 * @param endorsementReportActiveIndex
	 *            the endorsementReportActiveIndex to set
	 */
	public void setEndorsementReportActiveIndex(int endorsementReportActiveIndex) {
		this.endorsementReportActiveIndex = endorsementReportActiveIndex;
	}

	/**
	 * @return the evaluationStageId
	 */
	public int getEvaluationStageId() {
		return evaluationStageId;
	}

	/**
	 * @param evaluationStageId
	 *            the evaluationStageId to set
	 */
	public void setEvaluationStageId(int evaluationStageId) {
		this.evaluationStageId = evaluationStageId;
	}

	/**
	 * @return the evaluationHeader
	 */
	public String getEvaluationHeader() {
		return evaluationHeader;
	}

	/**
	 * @param evaluationHeader
	 *            the evaluationHeader to set
	 */
	public void setEvaluationHeader(String evaluationHeader) {
		this.evaluationHeader = evaluationHeader;
	}

	/**
	 * @return the directdownloaddocdefkeys
	 */
	public static List<String> getDirectdownloaddocdefkeys() {
		return directDownloadDocDefKeys;
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

	/**
	 * @return the shortlistedCommonSuppliers
	 */
	public IPagingDataModel<Object[]> getShortlistedCommonSuppliers() {
		return shortlistedCommonSuppliers;
	}

	/**
	 * @param shortlistedCommonSuppliers the shortlistedCommonSuppliers to set
	 */
	public void setShortlistedCommonSuppliers(
			IPagingDataModel<Object[]> shortlistedCommonSuppliers) {
		this.shortlistedCommonSuppliers = shortlistedCommonSuppliers;
	}

	/**
	 * @return the selectedCommonSupplier
	 */
	public Object[] getSelectedCommonSupplier() {
		return selectedCommonSupplier;
	}

	/**
	 * @param selectedCommonSupplier the selectedCommonSupplier to set
	 */
	public void setSelectedCommonSupplier(Object[] selectedCommonSupplier) {
		this.selectedCommonSupplier = selectedCommonSupplier;
	}

	/**
	 * @return the preferenceMarginDtls
	 */
	public ScQtEvaluationResultDtl getPreferenceMarginDtls() {
		return preferenceMarginDtls;
	}

	/**
	 * @param preferenceMarginDtls the preferenceMarginDtls to set
	 */
	public void setPreferenceMarginDtls(ScQtEvaluationResultDtl preferenceMarginDtls) {
		this.preferenceMarginDtls = preferenceMarginDtls;
	}

	/**
	 * @return the finalizationPengreysonremarks
	 */
	public List<ScQtFinalizationEndorsementRecommendationRemarks> getFinalizationPengreysonremarks() {
		return finalizationPengreysonremarks;
	}

	/**
	 * @param finalizationPengreysonremarks the finalizationPengreysonremarks to set
	 */
	public void setFinalizationPengreysonremarks(
			List<ScQtFinalizationEndorsementRecommendationRemarks> finalizationPengreysonremarks) {
		this.finalizationPengreysonremarks = finalizationPengreysonremarks;
	}

	/**
	 * @return the renderjustificationTextbox
	 */
	public boolean isRenderjustificationTextbox() {
		return renderjustificationTextbox;
	}

	/**
	 * @param renderjustificationTextbox the renderjustificationTextbox to set
	 */
	public void setRenderjustificationTextbox(boolean renderjustificationTextbox) {
		this.renderjustificationTextbox = renderjustificationTextbox;
	}

	/**
	 * @return the justification
	 */
	public PmReason getJustification() {
		return justification;
	}

	/**
	 * @param justification the justification to set
	 */
	public void setJustification(PmReason justification) {
		this.justification = justification;
	}

	/**
	 * @return the endorsementReports
	 */
	public List<ScQtFinalizationReport> getEndorsementReports() {
		return endorsementReports;
	}

	/**
	 * @param endorsementReports the endorsementReports to set
	 */
	public void setEndorsementReports(
			List<ScQtFinalizationReport> endorsementReports) {
		this.endorsementReports = endorsementReports;
	}

	/**
	 * @return the endorsementReport
	 */
	public ScQtFinalizationReport getEndorsementReport() {
		return endorsementReport;
	}

	/**
	 * @param endorsementReport the endorsementReport to set
	 */
	public void setEndorsementReport(ScQtFinalizationReport endorsementReport) {
		this.endorsementReport = endorsementReport;
	}

	/**
	 * @return the justDropdown
	 */
	public SelectItem[] getJustDropdown() {
		return justDropdown;
	}

	/**
	 * @param justDropdown the justDropdown to set
	 */
	public void setJustDropdown(SelectItem[] justDropdown) {
		this.justDropdown = justDropdown;
	}

	/**
	 * @return the outcome
	 */
	public String getOutcome() {
		return outcome;
	}

	/**
	 * @param outcome
	 *            the outcome to set
	 */
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public String getPreviousPageUrl() {
		return previousPageUrl;
	}

	public void setPreviousPageUrl(String previousPageUrl) {
		this.previousPageUrl = previousPageUrl;
	}

	/**
	 * @return the reverseTabIndex
	 */
	public int getReverseTabIndex() {
		return reverseTabIndex;
	}

	/**
	 * @param reverseTabIndex the reverseTabIndex to set
	 */
	public void setReverseTabIndex(int reverseTabIndex) {
		this.reverseTabIndex = reverseTabIndex;
	}

	/**
	 * @return the scQtReverse
	 */
	public ScQtReverse getScQtReverse() {
		return scQtReverse;
	}

	/**
	 * @param scQtReverse the scQtReverse to set
	 */
	public void setScQtReverse(ScQtReverse scQtReverse) {
		this.scQtReverse = scQtReverse;
	}

	/**
	 * @return the isReversed
	 */
	public boolean isReversed() {
		return isReversed;
	}

	/**
	 * @param isReversed the isReversed to set
	 */
	public void setReversed(boolean isReversed) {
		this.isReversed = isReversed;
	}

	/**
	 * @return the evaluationReportIndex
	 */
	public int getEvaluationReportIndex() {
		return evaluationReportIndex;
	}

	/**
	 * @param evaluationReportIndex the evaluationReportIndex to set
	 */
	public void setEvaluationReportIndex(int evaluationReportIndex) {
		this.evaluationReportIndex = evaluationReportIndex;
	}
	//#25037-start
	public List<ScCommitteeMember> getDeletedMemberList() {
		return deletedMemberList;
	}

	public void setDeletedMemberList(List<ScCommitteeMember> deletedMemberList) {
		this.deletedMemberList = deletedMemberList;
	}

	public SupplierFinalizationMeetingBackingBean getMeetingBB() {
		return meetingBB;
	}

	public void setMeetingBB(SupplierFinalizationMeetingBackingBean meetingBB) {
		this.meetingBB = meetingBB;
	}
	//#25037-end

	/** #25729#142 & #26169 - Start
	 * @return the isSuppOfferPriceExceedCommunityAuthority
	 */
	public boolean isSuppOfferPriceExceedCommunityAuthority() {
		return isSuppOfferPriceExceedCommunityAuthority;
	}

	/**
	 * @param isSuppOfferPriceExceedCommunityAuthority 
	 * 				the isSuppOfferPriceExceedCommunityAuthority to set
	 */
	public void setSuppOfferPriceExceedCommunityAuthority(boolean isSuppOfferPriceExceedCommunityAuthority) {
		this.isSuppOfferPriceExceedCommunityAuthority = isSuppOfferPriceExceedCommunityAuthority;
	}
	
	/**
	 * @return the maxAmountCommitteeAuthority
	 */
	public BigDecimal getMaxAmountCommitteeAuthority() {
		return maxAmountCommitteeAuthority;
	}

	/**
	 * @param maxAmountCommitteeAuthority the maxAmountCommitteeAuthority to set
	 */
	public void setMaxAmountCommitteeAuthority(BigDecimal maxAmountCommitteeAuthority) {
		this.maxAmountCommitteeAuthority = maxAmountCommitteeAuthority;
	}

	//#25729#142 & #26169 - End
	//#26169 #38 - Start
	public boolean isHasSupplierQualified() {
		return isHasSupplierQualified;
	}

	public void setHasSupplierQualified(boolean isHasSupplierQualified) {
		this.isHasSupplierQualified = isHasSupplierQualified;
	}
	//#26169 #38 - End
	
	// #18685 - Start.
	/**
	 * @return the pendingRecItemizedRemark
	 */
	public List<ScRemark> getPendingRecItemizedRemark() {
		return pendingRecItemizedRemark;
	}

	/**
	 * @param pendingRecItemizedRemark the pendingRecItemizedRemark to set
	 */
	public void setPendingRecItemizedRemark(
	    List<ScRemark> pendingRecItemizedRemark) {
		this.pendingRecItemizedRemark = pendingRecItemizedRemark;
	}

	/**
	 * @return the pendingRecPackageRemark
	 */
	public List<ScRemark> getPendingRecPackageRemark() {
		return pendingRecPackageRemark;
	}

	/**
	 * @param pendingRecPackageRemark the pendingRecPackageRemark to set
	 */
	public void setPendingRecPackageRemark(
	    List<ScRemark> pendingRecPackageRemark) {
		this.pendingRecPackageRemark = pendingRecPackageRemark;
	}
	
    /**
     * @return the scRemarks
     */
    public List<ScRemark> getScRemarks() {
      return scRemarks;
    }
  
    /**
     * @param scRemarks the scRemarks to set
     */
    public void setScRemarks(List<ScRemark> scRemarks) {
      this.scRemarks = scRemarks;
    }
    
    // #25638 - Start.
    /**
     * @return the newSfRemarks
     */
    public List<ScRemark> getNewSfRemarks() {
      return newSfRemarks;
    }

    /**
     * @param newSfRemarks the newSfRemarks to set
     */
    public void setNewSfRemarks(List<ScRemark> newSfRemarks) {
      this.newSfRemarks = newSfRemarks;
    }

    // #25638 - End.

    //#18685 - End.
    
	public boolean isMofFinalization() {
		return isMofFinalization;
	}

	public void setMofFinalization(boolean isMofFinalization) {
		this.isMofFinalization = isMofFinalization;
	}

	public boolean isHaveSupplierRejection() {
		return haveSupplierRejection;
	}

	public void setHaveSupplierRejection(boolean haveSupplierRejection) {
		this.haveSupplierRejection = haveSupplierRejection;
	}

	//27502 - Start.
	/**
	 * @return the isBpkCentralContract
	 */
	public boolean isBpkCentralContract() {
		return isBpkCentralContract;
	}

	/**
	 * @param isBpkCentralContract the isBpkCentralContract to set
	 */
	public void setBpkCentralContract(boolean isBpkCentralContract) {
		this.isBpkCentralContract = isBpkCentralContract;
	}
	//27502 - End.
	//28834 start
	public boolean isDisableBfButton() {
		return disableBfButton;
	}

	public void setDisableBfButton(boolean disableBfButton) {
		this.disableBfButton = disableBfButton;
	}
	
	/**
	 * @return the ktReportGenerated
	 */
	public boolean isKtReportGenerated() {
		return ktReportGenerated;
	}
	
	/**
	 * @param ktReportGenerated the ktReportGenerated to set
	 */
	public void setKtReportGenerated(boolean ktReportGenerated) {
		this.ktReportGenerated = ktReportGenerated;
	}
	
	/**
	 * @return the disableJustifications
	 */
	public boolean isDisableJustifications() {
		return disableJustifications;
	}

	/**
	 * @param disableJustifications the disableJustifications to set
	 */
	public void setDisableJustifications(boolean disableJustifications) {
		this.disableJustifications = disableJustifications;
	}
	
	//28834 end

	// #28777 & #29729 - Start
	public List<SmSupplierBranch> getSupplierBranchList() {
		return supplierBranchList;
	}

	public void setSupplierBranchList(List<SmSupplierBranch> supplierBranchList) {
		this.supplierBranchList = supplierBranchList;
	}

	public IPagingDataModel<ScQtEvaluationResultDtl> getDisqualifiedSuppliersByRecommendType() {
		return disqualifiedSuppliersByRecommendType;
	}

	public void setDisqualifiedSuppliersByRecommendType(
			IPagingDataModel<ScQtEvaluationResultDtl> disqualifiedSuppliersByRecommendType) {
		this.disqualifiedSuppliersByRecommendType = disqualifiedSuppliersByRecommendType;
	}

	public IPagingDataModel<ScQtEvaluationResultDtl> getDisqualifiedSuppliersByEvalStage() {
		return disqualifiedSuppliersByEvalStage;
	}

	public void setDisqualifiedSuppliersByEvalStage(
			IPagingDataModel<ScQtEvaluationResultDtl> disqualifiedSuppliersByEvalStage) {
		this.disqualifiedSuppliersByEvalStage = disqualifiedSuppliersByEvalStage;
	}
	// #28777 & #29729 - End

	public boolean isDemoReq() {
		return isDemoReq;
	}

	public void setDemoReq(boolean isDemoReq) {
		this.isDemoReq = isDemoReq;
	}

	public boolean isMofCntralCtc() {
		return isMofCntralCtc;
	}

	public void setMofCntralCtc(boolean isMofCntralCtc) {
		this.isMofCntralCtc = isMofCntralCtc;
	}

	//29562 - START
	public String getFinalizationMethodBefore() {
		return finalizationMethodBefore;
	}

	public String setFinalizationMethodBefore(String finalizationMethodBefore) {
		return this.finalizationMethodBefore = finalizationMethodBefore;
	}
	//29562 - END
	
	//#29466 - START
	/**
	 * @return the checkCategoryCodeQt
	 */
	public ScQt getCheckCategoryCodeQt() {
		return checkCategoryCodeQt;
	}

	/**
	 * @param checkCategoryCodeQt the checkCategoryCodeQt to set
	 */
	public void setCheckCategoryCodeQt(ScQt checkCategoryCodeQt) {
		this.checkCategoryCodeQt = checkCategoryCodeQt;
	}
	//#29466 - END
	
	////#33174 #21695 Farid
	/**
	 * @return 
	 */
	public boolean isPacDisable() {
		return pacDisable;
	}

	/**
	 * @param for disable if there is no supplier is qualified by package
	 */
	public void setOfferAll(boolean pacDisable) {
		this.pacDisable = pacDisable;
	}
	////#33174 #21695 End
	//6906 note 117 - start
	public SelectItem[] getDecisionDropdown() {
		return decisionDropdown;
	}

	public void setDecisionDropdown(SelectItem[] decisionDropdown) {
		this.decisionDropdown = decisionDropdown;
	}

	public boolean isSupplierFinalizationReadOnly() {
		return supplierFinalizationReadOnly;
	}

	public void setSupplierFinalizationReadOnly(boolean supplierFinalizationReadOnly) {
		this.supplierFinalizationReadOnly = supplierFinalizationReadOnly;
	}

	public boolean isDisabledSelection() {
		return disabledSelection;
	}

	public void setDisabledSelection(boolean disabledSelection) {
		this.disabledSelection = disabledSelection;
	}

	public boolean isNoProposalSubmitted() {
		return noProposalSubmitted;
	}

	public void setNoProposalSubmitted(boolean noProposalSubmitted) {
		this.noProposalSubmitted = noProposalSubmitted;
	}

	public List<String> getDisqualifiedStages() {
		return disqualifiedStages;
	}

	public void setDisqualifiedStages(List<String> disqualifiedStages) {
		this.disqualifiedStages = disqualifiedStages;
	}
	//6906 note 117 - end

	//6906 note 202 - start
	public boolean isHasSupplierQualifiedForCurrentSelection() {
		return hasSupplierQualifiedForCurrentSelection;
	}

	public void setHasSupplierQualifiedForCurrentSelection(boolean hasSupplierQualifiedForCurrentSelection) {
		this.hasSupplierQualifiedForCurrentSelection = hasSupplierQualifiedForCurrentSelection;
	}
	//6906 note 202 - end
	//35157 - start
	public ScQtRecommend getLastSelectedItem() {
		return lastSelectedItem;
	}
	
	public void setLastSelectedItem(ScQtRecommend lastSelectedItem) {
		this.lastSelectedItem = lastSelectedItem;
	}
	//35157 - end

	public boolean isNotReachAgrm() {
		return notReachAgrm;
	}

	public void setNotReachAgrm(boolean notReachAgrm) {
		this.notReachAgrm = notReachAgrm;
	}

	//#21692 - Start
	public boolean isAllSupplierDisagreeEPV() {
		return allSupplierDisagreeEPV;
	}

	public void setAllSupplierDisagreeEPV(boolean allSupplierDisagreeEPV) {
		this.allSupplierDisagreeEPV = allSupplierDisagreeEPV;
	}
	//#21692 - End
	//#38151 - Start
	public List<WeightageScoreBean> getWeightageScoreBeans() {
		return weightageScoreBeans;
	}

	public void setWeightageScoreBeans(List<WeightageScoreBean> weightageScoreBeans) {
		this.weightageScoreBeans = weightageScoreBeans;
	}

	public Long getOverallTechnicalRecommendationPercent() {
		return overallTechnicalRecommendationPercent;
	}

	public void setOverallTechnicalRecommendationPercent(Long overallTechnicalRecommendationPercent) {
		this.overallTechnicalRecommendationPercent = overallTechnicalRecommendationPercent;
	}

	public List<ScQtScoreSetting> getScoreSettings() {
		return scoreSettings;
	}

	public void setScoreSettings(List<ScQtScoreSetting> scoreSettings) {
		this.scoreSettings = scoreSettings;
	}

	public Long getOverallFinancialRecommendationPercent() {
		return overallFinancialRecommendationPercent;
	}

	public void setOverallFinancialRecommendationPercent(Long overallFinancialRecommendationPercent) {
		this.overallFinancialRecommendationPercent = overallFinancialRecommendationPercent;
	}

	public boolean isWeightageReq() {
		return WeightageReq;
	}

	public void setWeightageReq(boolean weightageReq) {
		WeightageReq = weightageReq;
	}

	public boolean isWeightage35384WIP() {
		return weightage35384WIP;
	}

	public void setWeightage35384WIP(boolean weightage35384WIP) {
		this.weightage35384WIP = weightage35384WIP;
	}
	//#38151 - End
	
	// 26247 - start
	public List<String> getDisabledSupplierIds() {
		return disabledSupplierIds;
	}

	public void setDisabledSupplierIds(List<String> disabledSupplierIds) {
		this.disabledSupplierIds = disabledSupplierIds;
	}
	// 26247 - end
	
	// 39077 -start-
	/**
	 * @return the isReviseBriefMsg
	 */
	public boolean isReviseBriefMsg() {
		return isReviseBriefMsg;
	}

	/**
	 * @param isReviseBriefMsg
	 *            the isReviseBriefMsg to set
	 */
	public void setReviseBriefMsg(boolean isReviseBriefMsg) {
		this.isReviseBriefMsg = isReviseBriefMsg;
	}
	// 39077 -end-

	public String getSelectedSupplierOfferedPrice() {
		return selectedSupplierOfferedPrice;
	}

	public void setSelectedSupplierOfferedPrice(String selectedSupplierOfferedPrice) {
		this.selectedSupplierOfferedPrice = selectedSupplierOfferedPrice;
	}
	
	
}
// ***25201
