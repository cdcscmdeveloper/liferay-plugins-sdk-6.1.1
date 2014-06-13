package my.ep.web.managedbean.sc.supplierfinalization;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.constant.pm.ParameterConst;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtReport;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;

/**
 * @author Faradiana
 * Supplier Finalization Managed Bean
 */
@ManagedBean(name = "suppFinalizationManagedBean")
@SessionScoped
public class SupplierFinalizationManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private int activeIndex;
	private int prepareActiveIndex;
	
	//Header
	private ScQt scQt;
	
	//Preparation
	private ScQtCommittee scQtCommittee;
	
	private List<ScQtMeeting> meetingList;
	private List<ScQtMeeting> selectedMeetingList;
	private List<ScQtCommitteeActivity> committeeList;
	private List<ScQtMeeting> selectedCommitteeList;
	private List<ScQtCommitteRemarks> committeeRemarkList;
	private List<ScQtMeetingAttendance> meetingAttendanceList;
	private List<ScQtMeetingAttendance> selectedMeetingAttList;
	
	//Briefing Paper/Kertas Taklimat
	private ScQtReport scQtReport;
	private List<PmDocument> documentList;
	
	//Supplier Finalization
	private String decisionType = ParameterConst.DECISION_RECOMMEND_SUPPLIER;
	private String recommendType = ParameterConst.RECOMMEND_TYPE_PACKAGE;
	private String biddingEndorsement;
	private String loiLoaPreparedBy = ParameterConst.LOILOA_PREPARED_BY_DO;
	private String fwdHigherAuthority = "false";
	private String finalizationCommittee;
	private String reason;
	private String reEvaluateBy;
	private String reEvaluateReqFor;
	private String reqSpecChanges;
	private String recommendBy = ParameterConst.RECOMMEND_BY_SINGLE_AWARD;
	private String selectionMode = QTConstants.SF_SINGLE;
	private String awardSupplier; 
	
	private boolean isItemize;
	private boolean isRecommendSupplier;
	private boolean isReEvaluate;
	private boolean isRePublish;
	private boolean isForward;
	private boolean isSingleAward;
	private boolean isMultipleAward;
	private boolean isOnlineBidding;
	private boolean isMultipleLoi;
	
	private ScQtEvaluationResult shortlistedSupplier;
	private ScQtFinalization scQtFinalization;
	
	private List<ScQtEvaluationResult> shortlistedSupplierList;
	private List<ScQtEvaluationResult> recommendedSupplierList;
	private List<ScRequestItem> itemList;
	private List<ScRequestItem> selectedItemList; 
	private List<SmSupplier> awardSupplierList; 
	
	//Committee Endorsement
	private List<ScQtCommitteeActivity> committeeEndorsementList;

	/**
	 * @return the activeIndex
	 */
	public int getActiveIndex() {
		return activeIndex;
	}

	/**
	 * @param activeIndex the activeIndex to set
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
	 * @param prepareActiveIndex the prepareActiveIndex to set
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
	 * @param scQt the scQt to set
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
	 * @param scQtCommittee the scQtCommittee to set
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
	 * @param meetingList the meetingList to set
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
	 * @param selectedMeetingList the selectedMeetingList to set
	 */
	public void setSelectedMeetingList(List<ScQtMeeting> selectedMeetingList) {
		this.selectedMeetingList = selectedMeetingList;
	}

	/**
	 * @return the committeeList
	 */
	public List<ScQtCommitteeActivity> getCommitteeList() {
		return committeeList;
	}

	/**
	 * @param committeeList the committeeList to set
	 */
	public void setCommitteeList(List<ScQtCommitteeActivity> committeeList) {
		this.committeeList = committeeList;
	}

	/**
	 * @return the selectedCommitteeList
	 */
	public List<ScQtMeeting> getSelectedCommitteeList() {
		return selectedCommitteeList;
	}

	/**
	 * @param selectedCommitteeList the selectedCommitteeList to set
	 */
	public void setSelectedCommitteeList(List<ScQtMeeting> selectedCommitteeList) {
		this.selectedCommitteeList = selectedCommitteeList;
	}

	/**
	 * @return the committeeRemarkList
	 */
	public List<ScQtCommitteRemarks> getCommitteeRemarkList() {
		return committeeRemarkList;
	}

	/**
	 * @param committeeRemarkList the committeeRemarkList to set
	 */
	public void setCommitteeRemarkList(List<ScQtCommitteRemarks> committeeRemarkList) {
		this.committeeRemarkList = committeeRemarkList;
	}

	/**
	 * @return the meetingAttendanceList
	 */
	public List<ScQtMeetingAttendance> getMeetingAttendanceList() {
		return meetingAttendanceList;
	}

	/**
	 * @param meetingAttendanceList the meetingAttendanceList to set
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
	 * @param selectedMeetingAttList the selectedMeetingAttList to set
	 */
	public void setSelectedMeetingAttList(
			List<ScQtMeetingAttendance> selectedMeetingAttList) {
		this.selectedMeetingAttList = selectedMeetingAttList;
	}

	/**
	 * @return the scQtReport
	 */
	public ScQtReport getScQtReport() {
		return scQtReport;
	}

	/**
	 * @param scQtReport the scQtReport to set
	 */
	public void setScQtReport(ScQtReport scQtReport) {
		this.scQtReport = scQtReport;
	}

	/**
	 * @return the documentList
	 */
	public List<PmDocument> getDocumentList() {
		return documentList;
	}

	/**
	 * @param documentList the documentList to set
	 */
	public void setDocumentList(List<PmDocument> documentList) {
		this.documentList = documentList;
	}

	/**
	 * @return the decisionType
	 */
	public String getDecisionType() {
		return decisionType;
	}

	/**
	 * @param decisionType the decisionType to set
	 */
	public void setDecisionType(String decisionType) {
		this.decisionType = decisionType;
	}

	/**
	 * @return the recommendType
	 */
	public String getRecommendType() {
		return recommendType;
	}

	/**
	 * @param recommendType the recommendType to set
	 */
	public void setRecommendType(String recommendType) {
		this.recommendType = recommendType;
	}

	/**
	 * @return the biddingEndorsement
	 */
	public String getBiddingEndorsement() {
		return biddingEndorsement;
	}

	/**
	 * @param biddingEndorsement the biddingEndorsement to set
	 */
	public void setBiddingEndorsement(String biddingEndorsement) {
		this.biddingEndorsement = biddingEndorsement;
	}

	/**
	 * @return the loiLoaPreparedBy
	 */
	public String getLoiLoaPreparedBy() {
		return loiLoaPreparedBy;
	}

	/**
	 * @param loiLoaPreparedBy the loiLoaPreparedBy to set
	 */
	public void setLoiLoaPreparedBy(String loiLoaPreparedBy) {
		this.loiLoaPreparedBy = loiLoaPreparedBy;
	}

	/**
	 * @return the fwdHigherAuthority
	 */
	public String getFwdHigherAuthority() {
		return fwdHigherAuthority;
	}

	/**
	 * @param fwdHigherAuthority the fwdHigherAuthority to set
	 */
	public void setFwdHigherAuthority(String fwdHigherAuthority) {
		this.fwdHigherAuthority = fwdHigherAuthority;
	}

	/**
	 * @return the finalizationCommittee
	 */
	public String getFinalizationCommittee() {
		return finalizationCommittee;
	}

	/**
	 * @param finalizationCommittee the finalizationCommittee to set
	 */
	public void setFinalizationCommittee(String finalizationCommittee) {
		this.finalizationCommittee = finalizationCommittee;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return the reEvaluateBy
	 */
	public String getReEvaluateBy() {
		return reEvaluateBy;
	}

	/**
	 * @param reEvaluateBy the reEvaluateBy to set
	 */
	public void setReEvaluateBy(String reEvaluateBy) {
		this.reEvaluateBy = reEvaluateBy;
	}

	/**
	 * @return the reEvaluateReqFor
	 */
	public String getReEvaluateReqFor() {
		return reEvaluateReqFor;
	}

	/**
	 * @param reEvaluateReqFor the reEvaluateReqFor to set
	 */
	public void setReEvaluateReqFor(String reEvaluateReqFor) {
		this.reEvaluateReqFor = reEvaluateReqFor;
	}

	/**
	 * @return the reqSpecChanges
	 */
	public String getReqSpecChanges() {
		return reqSpecChanges;
	}

	/**
	 * @param reqSpecChanges the reqSpecChanges to set
	 */
	public void setReqSpecChanges(String reqSpecChanges) {
		this.reqSpecChanges = reqSpecChanges;
	}

	/**
	 * @return the recommendBy
	 */
	public String getRecommendBy() {
		return recommendBy;
	}

	/**
	 * @param recommendBy the recommendBy to set
	 */
	public void setRecommendBy(String recommendBy) {
		this.recommendBy = recommendBy;
	}

	/**
	 * @return the isItemize
	 */
	public boolean isItemize() {
		return isItemize;
	}

	/**
	 * @param isItemize the isItemize to set
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
	 * @param isRecommendSupplier the isRecommendSupplier to set
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
	 * @param isReEvaluate the isReEvaluate to set
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
	 * @param isRePublish the isRePublish to set
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
	 * @param isForward the isForward to set
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
	 * @param isSingleAward the isSingleAward to set
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
	 * @param isMultipleAward the isMultipleAward to set
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
	 * @param isOnlineBidding the isOnlineBidding to set
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
	 * @param isMultipleLoi the isMultipleLoi to set
	 */
	public void setMultipleLoi(boolean isMultipleLoi) {
		this.isMultipleLoi = isMultipleLoi;
	}

	/**
	 * @return the committeeEndorsementList
	 */
	public List<ScQtCommitteeActivity> getCommitteeEndorsementList() {
		return committeeEndorsementList;
	}

	/**
	 * @param committeeEndorsementList the committeeEndorsementList to set
	 */
	public void setCommitteeEndorsementList(
			List<ScQtCommitteeActivity> committeeEndorsementList) {
		this.committeeEndorsementList = committeeEndorsementList;
	}

	/**
	 * @return the shortlistedSupplierList
	 */
	public List<ScQtEvaluationResult> getShortlistedSupplierList() {
		return shortlistedSupplierList;
	}

	/**
	 * @param shortlistedSupplierList the shortlistedSupplierList to set
	 */
	public void setShortlistedSupplierList(
			List<ScQtEvaluationResult> shortlistedSupplierList) {
		this.shortlistedSupplierList = shortlistedSupplierList;
	}

	/**
	 * @return the recommendedSupplierList
	 */
	public List<ScQtEvaluationResult> getRecommendedSupplierList() {
		return recommendedSupplierList;
	}

	/**
	 * @param recommendedSupplierList the recommendedSupplierList to set
	 */
	public void setRecommendedSupplierList(
			List<ScQtEvaluationResult> recommendedSupplierList) {
		this.recommendedSupplierList = recommendedSupplierList;
	}

	/**
	 * @return the itemList
	 */
	public List<ScRequestItem> getItemList() {
		return itemList;
	}

	/**
	 * @param itemList the itemList to set
	 */
	public void setItemList(List<ScRequestItem> itemList) {
		this.itemList = itemList;
	}

	/**
	 * @return the selectedItemList
	 */
	public List<ScRequestItem> getSelectedItemList() {
		return selectedItemList;
	}

	/**
	 * @param selectedItemList the selectedItemList to set
	 */
	public void setSelectedItemList(List<ScRequestItem> selectedItemList) {
		this.selectedItemList = selectedItemList;
	}

	/**
	 * @return the selectionMode
	 */
	public String getSelectionMode() {
		return selectionMode;
	}

	/**
	 * @param selectionMode the selectionMode to set
	 */
	public void setSelectionMode(String selectionMode) {
		this.selectionMode = selectionMode;
	}

	/**
	 * @return the shortlistedSupplier
	 */
	public ScQtEvaluationResult getShortlistedSupplier() {
		return shortlistedSupplier;
	}

	/**
	 * @param shortlistedSupplier the shortlistedSupplier to set
	 */
	public void setShortlistedSupplier(ScQtEvaluationResult shortlistedSupplier) {
		this.shortlistedSupplier = shortlistedSupplier;
	}

	/**
	 * @return the awardSupplier
	 */
	public String getAwardSupplier() {
		return awardSupplier;
	}

	/**
	 * @param awardSupplier the awardSupplier to set
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
	 * @param awardSupplierList the awardSupplierList to set
	 */
	public void setAwardSupplierList(List<SmSupplier> awardSupplierList) {
		this.awardSupplierList = awardSupplierList;
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
	
	
	

}
