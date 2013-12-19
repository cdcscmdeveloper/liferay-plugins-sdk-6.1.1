package my.ep.web.managedbean.ss.supplierfinalization;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.ss.quationtender.ScQtItem;
import my.ep.persistence.entity.ss.quationtender.ScQtMeeting;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.ss.quationtender.ScQtZoneCriteria;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author sri
 * Supplier Finalization Managed Bean
 */
@ManagedBean(name = "sfManagedBean")
@SessionScoped
public class SupplierFinalizationManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	private String selectItem;
	private String [] decisionArray;
	private String optionType = "Basic";
	private String selectionMode;
	private int tabActiveIndex;
	
	// Enable and disable Parameters
	private boolean awardType = true;
	private boolean byItemStatus;
	private boolean byPackageStatus;
	private boolean reEvaluation;
	private boolean fhAuthority; 
	private boolean basicSelection = true; //  basicSelection option for basic selection 
	private boolean multipleItemSelection; //  multipleItemSelection option for multiple selection
	private boolean bySupplierSelection;  //  bySupplierSelection option for By Supplier Selection
	private boolean locVisible; // location enabled and disabled
	private boolean shortlistedSuppVisible; // Shortlisted Supplier List Enable and disable
	private boolean recommendedSuppVisible; // Recommend Supplier List Enable and disable
	
	private List<ScQtMeeting> meetingDetails;  // meetingDetails list for Meeting Details in Preparation tab
	private List<ScQtCommitteeActivity> committeeMemberList; // committeeMemberList  for Committee member list Details in Preparation tab
	private  ScQt scQt;
	private ScQtProposalRanking scQtProposalRanking; // for Supplier Details
	private ScQtZoneCriteria scQtZoneCriteria; // for selected zone to get Shortlisted Supplier listing and Recommended supplier Listing
	private ScQtMeeting scQtMeeting;
	
	private List<ScQtProposalRanking> qualifiedSupplist;
	private List<ScQtProposalRanking> disQualifiedSupplist;
	private List<ScQtCommitteeActivity> committeeSignOffMemberlist;
	private List<ScQtZoneCriteria> zonelist;
	private List<ScQtItem> itemSummarylist; // for Item List in Summary
	private List<ScQtItem> itemSuppFinalizationlist; // for Item List in Supplier Finalization
	private List<ScQtProposalRanking> recommendedSuppList ; // for Recommended Supplier Listing
	private List<ScQtItem> selectedItemsSummary; // for Selected Items  in Summary
	private List<ScQtItem> selectedItemsSuppFinalization; // for  Selected Items in Supplier Finalization
	private List<ScQtProposalRanking> shortlistedSuppList ; // for ShortlistedSupplier Listing
	private Integer totalSelectedSupplier; // Total for selected suppliers
	private Integer totalQulifiedSupplier; // Total for Qualified Suppliers
	private Integer totalDisQualifiedSupplier; // Total for DisQualified Suppliers
	
	
	/**
	 * @return the byItemStatus
	 */
	public boolean isByItemStatus() {
		return byItemStatus;
	}
	/**
	 * @param byItemStatus the byItemStatus to set
	 */
	public void setByItemStatus(boolean byItemStatus) {
		this.byItemStatus = byItemStatus;
	}
	/**
	 * @return the byPackageStatus
	 */
	public boolean isByPackageStatus() {
		return byPackageStatus;
	}
	/**
	 * @param byPackageStatus the byPackageStatus to set
	 */
	public void setByPackageStatus(boolean byPackageStatus) {
		this.byPackageStatus = byPackageStatus;
	}
	/**
	 * @return the selectItem
	 */
	public String getSelectItem() {
		return selectItem;
	}
	/**
	 * @param selectItem the selectItem to set
	 */
	public void setSelectItem(String selectItem) {
		this.selectItem = selectItem;
	}
	/**
	 * @return the awardType
	 */
	public boolean isAwardType() {
		return awardType;
	}
	/**
	 * @param awardType the awardType to set
	 */
	public void setAwardType(boolean awardType) {
		this.awardType = awardType;
	}


	/**
	 * @return the decisionArray
	 */
	public String[] getDecisionArray() {
		return decisionArray;
	}

	/**
	 * @param decisionArray the decisionArray to set
	 */
	public void setDecisionArray(String[] decisionArray) {
		this.decisionArray = decisionArray;
	}

	/**
	 * @return the reEvaluation
	 */
	public boolean isReEvaluation() {
		return reEvaluation;
	}

	/**
	 * @param reEvaluation the reEvaluation to set
	 */
	public void setReEvaluation(boolean reEvaluation) {
		this.reEvaluation = reEvaluation;
	}

	/**
	 * @return the fhAuthority
	 */
	public boolean isFhAuthority() {
		return fhAuthority;
	}

	/**
	 * @param fhAuthority the fhAuthority to set
	 */
	public void setFhAuthority(boolean fhAuthority) {
		this.fhAuthority = fhAuthority;
	}

	/**
	 * @return the basicSelection
	 */
	public boolean isBasicSelection() {
		return basicSelection;
	}

	/**
	 * @param basicSelection the basicSelection to set
	 */
	public void setBasicSelection(boolean basicSelection) {
		this.basicSelection = basicSelection;
	}

	/**
	 * @return the multipleItemSelection
	 */
	public boolean isMultipleItemSelection() {
		return multipleItemSelection;
	}

	/**
	 * @param multipleItemSelection the multipleItemSelection to set
	 */
	public void setMultipleItemSelection(boolean multipleItemSelection) {
		this.multipleItemSelection = multipleItemSelection;
	}

	/**
	 * @return the optionType
	 */
	public String getOptionType() {
		return optionType;
	}

	/**
	 * @param optionType the optionType to set
	 */
	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}

	/**
	 * @return the tabActiveIndex
	 */
	public int getTabActiveIndex() {
		return tabActiveIndex;
	}

	/**
	 * @param tabActiveIndex the tabActiveIndex to set
	 */
	public void setTabActiveIndex(int tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	/**
	 * @return the meetingDetails
	 */
	public List<ScQtMeeting> getMeetingDetails() {
		return meetingDetails;
	}

	/**
	 * @param meetingDetails the meetingDetails to set
	 */
	public void setMeetingDetails(List<ScQtMeeting> meetingDetails) {
		this.meetingDetails = meetingDetails;
	}

	/**
	 * @return the committeeMemberList
	 */
	public List<ScQtCommitteeActivity> getCommitteeMemberList() {
		return committeeMemberList;
	}

	/**
	 * @param committeeMemberList the committeeMemberList to set
	 */
	public void setCommitteeMemberList(
			List<ScQtCommitteeActivity> committeeMemberList) {
		this.committeeMemberList = committeeMemberList;
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
	 * @return the qualifiedSupplist
	 */
	public List<ScQtProposalRanking> getQualifiedSupplist() {
		return qualifiedSupplist;
	}

	/**
	 * @param qualifiedSupplist the qualifiedSupplist to set
	 */
	public void setQualifiedSupplist(List<ScQtProposalRanking> qualifiedSupplist) {
		this.qualifiedSupplist = qualifiedSupplist;
	}

	/**
	 * @return the disQualifiedSupplist
	 */
	public List<ScQtProposalRanking> getDisQualifiedSupplist() {
		return disQualifiedSupplist;
	}

	/**
	 * @param disQualifiedSupplist the disQualifiedSupplist to set
	 */
	public void setDisQualifiedSupplist(
			List<ScQtProposalRanking> disQualifiedSupplist) {
		this.disQualifiedSupplist = disQualifiedSupplist;
	}

	/**
	 * @return the committeeSignOffMemberlist
	 */
	public List<ScQtCommitteeActivity> getCommitteeSignOffMemberlist() {
		return committeeSignOffMemberlist;
	}

	/**
	 * @param committeeSignOffMemberlist the committeeSignOffMemberlist to set
	 */
	public void setCommitteeSignOffMemberlist(
			List<ScQtCommitteeActivity> committeeSignOffMemberlist) {
		this.committeeSignOffMemberlist = committeeSignOffMemberlist;
	}

	/**
	 * @return the zonelist
	 */
	public List<ScQtZoneCriteria> getZonelist() {
		return zonelist;
	}

	/**
	 * @param zonelist the zonelist to set
	 */
	public void setZonelist(List<ScQtZoneCriteria> zonelist) {
		this.zonelist = zonelist;
	}


	/**
	 * @return the locVisible
	 */
	public boolean isLocVisible() {
		return locVisible;
	}

	/**
	 * @param locVisible the locVisible to set
	 */
	public void setLocVisible(boolean locVisible) {
		this.locVisible = locVisible;
	}

	/**
	 * @return the shortlistedSuppVisible
	 */
	public boolean isShortlistedSuppVisible() {
		return shortlistedSuppVisible;
	}

	/**
	 * @param shortlistedSuppVisible the shortlistedSuppVisible to set
	 */
	public void setShortlistedSuppVisible(boolean shortlistedSuppVisible) {
		this.shortlistedSuppVisible = shortlistedSuppVisible;
	}

	/**
	 * @return the recommendedSuppVisible
	 */
	public boolean isRecommendedSuppVisible() {
		return recommendedSuppVisible;
	}

	/**
	 * @param recommendedSuppVisible the recommendedSuppVisible to set
	 */
	public void setRecommendedSuppVisible(boolean recommendedSuppVisible) {
		this.recommendedSuppVisible = recommendedSuppVisible;
	}

	/**
	 * @return the bySupplierSelection
	 */
	public boolean isBySupplierSelection() {
		return bySupplierSelection;
	}

	/**
	 * @param bySupplierSelection the bySupplierSelection to set
	 */
	public void setBySupplierSelection(boolean bySupplierSelection) {
		this.bySupplierSelection = bySupplierSelection;
	}

	/**
	 * @return the recommendedSuppList
	 */
	public List<ScQtProposalRanking> getRecommendedSuppList() {
		return recommendedSuppList;
	}

	/**
	 * @param recommendedSuppList the recommendedSuppList to set
	 */
	public void setRecommendedSuppList(List<ScQtProposalRanking> recommendedSuppList) {
		this.recommendedSuppList = recommendedSuppList;
	}

	/**
	 * @return the shortlistedSuppList
	 */
	public List<ScQtProposalRanking> getShortlistedSuppList() {
		return shortlistedSuppList;
	}

	/**
	 * @param shortlistedSuppList the shortlistedSuppList to set
	 */
	public void setShortlistedSuppList(List<ScQtProposalRanking> shortlistedSuppList) {
		this.shortlistedSuppList = shortlistedSuppList;
	}

	/**
	 * @return the totalSelectedSupplier
	 */
	public Integer getTotalSelectedSupplier() {
		return totalSelectedSupplier;
	}

	/**
	 * @param totalSelectedSupplier the totalSelectedSupplier to set
	 */
	public void setTotalSelectedSupplier(Integer totalSelectedSupplier) {
		this.totalSelectedSupplier = totalSelectedSupplier;
	}

	/**
	 * @return the totalQulifiedSupplier
	 */
	public Integer getTotalQulifiedSupplier() {
		return totalQulifiedSupplier;
	}

	/**
	 * @param totalQulifiedSupplier the totalQulifiedSupplier to set
	 */
	public void setTotalQulifiedSupplier(Integer totalQulifiedSupplier) {
		this.totalQulifiedSupplier = totalQulifiedSupplier;
	}

	/**
	 * @return the totalDisQualifiedSupplier
	 */
	public Integer getTotalDisQualifiedSupplier() {
		return totalDisQualifiedSupplier;
	}

	/**
	 * @param totalDisQualifiedSupplier the totalDisQualifiedSupplier to set
	 */
	public void setTotalDisQualifiedSupplier(Integer totalDisQualifiedSupplier) {
		this.totalDisQualifiedSupplier = totalDisQualifiedSupplier;
	}


	/**
	 * @return the itemSummarylist
	 */
	public List<ScQtItem> getItemSummarylist() {
		return itemSummarylist;
	}

	/**
	 * @param itemSummarylist the itemSummarylist to set
	 */
	public void setItemSummarylist(List<ScQtItem> itemSummarylist) {
		this.itemSummarylist = itemSummarylist;
	}

	/**
	 * @return the itemSuppFinalizationlist
	 */
	public List<ScQtItem> getItemSuppFinalizationlist() {
		return itemSuppFinalizationlist;
	}

	/**
	 * @param itemSuppFinalizationlist the itemSuppFinalizationlist to set
	 */
	public void setItemSuppFinalizationlist(List<ScQtItem> itemSuppFinalizationlist) {
		this.itemSuppFinalizationlist = itemSuppFinalizationlist;
	}

	/**
	 * @return the selectedItemsSummary
	 */
	public List<ScQtItem> getSelectedItemsSummary() {
		return selectedItemsSummary;
	}

	/**
	 * @param selectedItemsSummary the selectedItemsSummary to set
	 */
	public void setSelectedItemsSummary(List<ScQtItem> selectedItemsSummary) {
		this.selectedItemsSummary = selectedItemsSummary;
	}

	/**
	 * @return the selectedItemsSuppFinalization
	 */
	public List<ScQtItem> getSelectedItemsSuppFinalization() {
		return selectedItemsSuppFinalization;
	}

	/**
	 * @param selectedItemsSuppFinalization the selectedItemsSuppFinalization to set
	 */
	public void setSelectedItemsSuppFinalization(
			List<ScQtItem> selectedItemsSuppFinalization) {
		this.selectedItemsSuppFinalization = selectedItemsSuppFinalization;
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
	 * @return the scQtZoneCriteria
	 */
	public ScQtZoneCriteria getScQtZoneCriteria() {
		return scQtZoneCriteria;
	}

	/**
	 * @param scQtZoneCriteria the scQtZoneCriteria to set
	 */
	public void setScQtZoneCriteria(ScQtZoneCriteria scQtZoneCriteria) {
		this.scQtZoneCriteria = scQtZoneCriteria;
	}

	/**
	 * @return the scQtMeeting
	 */
	public ScQtMeeting getScQtMeeting() {
		return scQtMeeting;
	}

	/**
	 * @param scQtMeeting the scQtMeeting to set
	 */
	public void setScQtMeeting(ScQtMeeting scQtMeeting) {
		this.scQtMeeting = scQtMeeting;
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
	
	
	
}
