/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.supplierfinalization;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.MenuModel;
import org.primefaces.model.StreamedContent;
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
	private List<ScQtMeetingAttendance> meetingAttendanceList;
	private List<ScQtMeetingAttendance> selectedMeetingAttList;
	private List<ScQtMeetingAttendance> deletedMeetingAttendanceList;
	
	//Briefing Paper/Kertas Taklimat
	private ScQtFinalizationReport selectedFinalizationReport;
	private List<ScQtFinalizationReport> documentList;
	private StreamedContent streamContent;
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
	private boolean isInitialSelectionItemized;
	private boolean isInitialDecisionFwdHighAuth;
	private boolean isInitialDecisionFwdToBPK;
	private boolean isSelectedItemAlreadyAwarded;
	private boolean hasAwardedLOA;
	private boolean hasPreviousMeeting;
	private boolean hasItemWithPriceTypeTier;
	private boolean itemCancelled;
	private Boolean qtRefinalized;
	private boolean refinalizedAndBiddingAward;
	private boolean disableFwdToHighAuthorityOption;
	
	private ScQtEvaluationResultDtl shortlistedSupplier;
	private ScQtFinalization scQtFinalization;
	private TplSpecZoneDtl currentZone;
	private ScQtEvaluationResultDtl recommendedSupplier;
	private ScQtEvaluationResultDtl recommendedSupplierByItem;
	private ScQtEvaluationResultDtl prevRecommendedSupplierByItem;
	
	private List<ScQtEvaluationResultDtl> shortlistedPackageList;
	private List<ScQtEvaluationResultDtl> shortlistedSupplierList;
	private List<ScQtEvaluationResultDtl> recommendedSupplierList;
	private List<ScQtEvaluationResultDtl> unRecommendedSupplierList;
	private Set<ScQtEvaluationResultDtl> resetSupplierList;
	private List<ScQtRecommend> selectedItemList; 
	private List<ScQtRecommend> itemList;
	private List<SmSupplier> awardSupplierList; 
	private List<Long> rejectedScQtRecommendIds;
	
	private Map<Long, TplSpecZoneDtl> zoneMap;
	private Map<Long, List<ScQtEvaluationResultDtl>> pkgShortlistedSupppliersByZone;
	private Map<Long, List<ScQtEvaluationResultDtl>> recommendedSupppliersByZone;
	
	private Map<String,String[]> higherAuthoritiesMap;
	
	//Committee Endorsement
	private List<ScQtCommitteeActivity> reminderList;
	
	//Online Bidding Endorsement
	private List<ScQtCommitteeActivity> onlineBiddingEndorsementList;
	private List<ScQtCommitteeActivity> onlineBiddingReminderList;
	
	private String subject;
	private String message;
	private String pinNo;
	
	private boolean selectAll;
	private boolean selectAllMeetings;
	private String action;
	private Long loggedInUserId;
	
	private int preparationTabIndex;
	private int reportTabIndex;
	private int finalizationTabIndex;
	private int commEndorsementTabIndex;
	private int endorsementReportActiveIndex;
	

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

	/**
	 * @return the shortlistedSupplierList
	 */
	public List<ScQtEvaluationResultDtl> getShortlistedSupplierList() {
		return shortlistedSupplierList;
	}

	/**
	 * @param shortlistedSupplierList 
	 * 						the shortlistedSupplierList to set
	 */
	public void setShortlistedSupplierList(
			List<ScQtEvaluationResultDtl> shortlistedSupplierList) {
		this.shortlistedSupplierList = shortlistedSupplierList;
	}

	/**
	 * @return the recommendedSupplierList
	 */
	public List<ScQtEvaluationResultDtl> getRecommendedSupplierList() {
		return recommendedSupplierList;
	}

	/**
	 * @param recommendedSupplierList 
	 * 						the recommendedSupplierList to set
	 */
	public void setRecommendedSupplierList(
			List<ScQtEvaluationResultDtl> recommendedSupplierList) {
		this.recommendedSupplierList = recommendedSupplierList;
	}

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

	/**
	 * @return the pkgShortlistedSupppliersByZone
	 */
	public Map<Long, List<ScQtEvaluationResultDtl>> getPkgShortlistedSupppliersByZone() {
		return pkgShortlistedSupppliersByZone;
	}

	/**
	 * @param pkgShortlistedSupppliersByZone the pkgShortlistedSupppliersByZone to set
	 */
	public void setPkgShortlistedSupppliersByZone(
			Map<Long, List<ScQtEvaluationResultDtl>> pkgShortlistedSupppliersByZone) {
		this.pkgShortlistedSupppliersByZone = pkgShortlistedSupppliersByZone;
	}

	/**
	 * @return the recommendedSupppliersByZone
	 */
	public Map<Long, List<ScQtEvaluationResultDtl>> 
				getRecommendedSupppliersByZone() {
		return recommendedSupppliersByZone;
	}

	/**
	 * @param recommendedSupppliersByZone 
	 * 				the recommendedSupppliersByZone to set
	 */
	public void setRecommendedSupppliersByZone(
			Map<Long, List<ScQtEvaluationResultDtl>> recommendedSupppliersByZone) {
		this.recommendedSupppliersByZone = recommendedSupppliersByZone;
	}

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
	 * @return the streamContent
	 */
	public StreamedContent getStreamContent() {
		return streamContent;
	}

	/**
	 * @param streamContent 
	 * 					the streamContent to set
	 */
	public void setStreamContent(StreamedContent streamContent) {
		this.streamContent = streamContent;
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

	/**
	 * @return the unRecommendedSupplierList
	 */
	public List<ScQtEvaluationResultDtl> getUnRecommendedSupplierList() {
		return unRecommendedSupplierList;
	}

	/**
	 * @param unRecommendedSupplierList 
	 * 					the unRecommendedSupplierList to set
	 */
	public void setUnRecommendedSupplierList(
			List<ScQtEvaluationResultDtl> unRecommendedSupplierList) {
		this.unRecommendedSupplierList = unRecommendedSupplierList;
	}

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

	/**
	 * @return the shortlistedPackageList
	 */
	public List<ScQtEvaluationResultDtl> getShortlistedPackageList() {
		return shortlistedPackageList;
	}

	/**
	 * @param shortlistedPackageList 
	 * 						the shortlistedPackageList to set
	 */
	public void setShortlistedPackageList(
			List<ScQtEvaluationResultDtl> shortlistedPackageList) {
		this.shortlistedPackageList = shortlistedPackageList;
	}

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

}
