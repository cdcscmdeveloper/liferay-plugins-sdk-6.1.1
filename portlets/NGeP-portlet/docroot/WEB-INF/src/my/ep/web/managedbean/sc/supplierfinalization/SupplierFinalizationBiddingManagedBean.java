/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.supplierfinalization;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.sc.BidItemBean;
import my.ep.biz.bean.sc.BidPackageBean;
import my.ep.persistence.entity.sc.bidding.ScBid;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationEndorsementRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.MenuModel;

/**
 * Class to handle user session data corresponding to SupplierFinalizationBiddingBackingBean
 * 
 * @author Faradiana
 * Supplier Finalization Managed Bean
 */
@ManagedBean(name = "suppFinalBiddingManagedBean")
@SessionScoped
public class SupplierFinalizationBiddingManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private int onlineBiddingActiveIndex;
	
	//Header
	private ScQt scQt;
	private ScQtFinalization scQtFinalization;
	private String committeeType;
	private String processName;
	
	//Summary
	private ScBid scBidding;
	private List<ScBidItem> biddingItemList;
	private ScBidItem selectedBiddingItem;
	private List<ScBidPrice> biddingItemPriceList;
	private List<BidPackageBean> biddingPackagePriceList;
	
	//Meeting preparation
	private ScQtCommittee scQtCommittee;
	private List<ScQtMeeting> meetingList;
	
	//Online Bidding Endorsement
	private List<ScQtCommitteeActivity> committeeEndorsementList;
	private List<ScQtCommitteeActivity> reminderList;
	
	private String subject;
	private String message;
	private String pinNo;
	
	//Offline Bidding Endorsement
	private List<ScQtFinalizationEndorsementRemarks> remarks;
	private String attachmentDocType;
	private String action;
	
	// Item price pop up
	private Integer supplierCode;
	private ScBidSchedule scheduledBid;
	private List<ScBidPrice> ScBidPriceList;
	private List<BidItemBean> bidItemSuppliers;
	
	private BigDecimal lowestNonBumiOfferedPrice;
	private BigDecimal bumiSuppLowestOfferedPrice;
	
	private ScQtEvaluationResult shortlistedSupplier;
	private ScQtEvaluationResultDtl awardedSupplier;
	private BidPackageBean selectedPackageBean;
	private Map<Long,BidPackageBean> selectedPackageBeans;
	private BidItemBean selectedBidItemBean;
	private Map<Long,BidItemBean> selectedItemBeans;
	private SmSupplier smSupplier;
	private SmMofAccount smMofAccount;
	private SmCompanyBasic smCompanyBasic;
	private SmPersonnel smPersonnel;
	private String supplierBranch;
	private SmAddress supplierAddress;
	
	private BigDecimal estimatedCost;
	
	private boolean nonBumiAvailable = false;
	
	private int ZoneActiveIndex;
	private MenuModel zoneMenuModel;
	private TplSpecZoneDtl selectedZone;
	
	private List<TplSpecZoneDtl> biddingZoneDetails;
	private Map<Long,TplSpecZoneDtl> zoneMap;
	private Map<Long,List<ScQtRecommend>> scQtRecommendsByZone;
	private Map<Long,List<ScBidSchedule>> bidSchedulesByZone;
	private Map<Long,List<BidPackageBean>> bidPackageBeansByZone;
	private Map<Long,List<ScBidItem>> bidItemsByZone;
	private Map<Long,List<ScQtEvaluationResultDtl>> itemShortlistedSuppliers;
	private Map<Long,List<ScQtEvaluationResultDtl>> pkgShortlistedSuppliersByZone;
	private Map<Long,Map<Long,List<ScQtEvaluationResultDtl>>> itemShortlistedSuppliersByZone;
	
	// Bidding Finalization Meeting
	private boolean secretaryUser;
	private boolean selectAll;
	private boolean selectAllMeetings;
	private boolean SelectAllMeetingAttendees;
	private boolean zonal;
	private boolean isItemized;
	private boolean onlineBiddingEscalation;
	private boolean disableSupplierSelection;
	private int meetingActiveIndex;
	private String committeeTabLabel;
	private List<ScQtMeetingAttendance> meetingAttendanceList;
	private List<ScQtCommitteeActivity> committeeList;
	private List<ScQtMeeting> deletedMeetingList;
	private List<ScQtMeetingAttendance> selectedMeetingAttList;
	private List<ScQtMeetingAttendance> deletedMeetingAttendanceList;
	private List<ScQtCommitteRemarks> committeeRemarkList;
	private List<ScQtEvaluationResultDtl> shortlistedSuppliers;
	private List<String> awardedRecommendDtlIds;
	
	private boolean bpkUser;
	
	/** The auto update. */
	private boolean autoUpdate = Boolean.TRUE;
	
	/**
	 * @return the onlineBiddingActiveIndex
	 */
	public int getOnlineBiddingActiveIndex() {
		return onlineBiddingActiveIndex;
	}
	
	/**
	 * @param onlineBiddingActiveIndex the onlineBiddingActiveIndex to set
	 */
	public void setOnlineBiddingActiveIndex(int onlineBiddingActiveIndex) {
		this.onlineBiddingActiveIndex = onlineBiddingActiveIndex;
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
	 * @return the committeeEndorsementList
	 */
	public List<ScQtCommitteeActivity> getCommitteeEndorsementList() {
		return committeeEndorsementList;
	}
	
	/**
	 * @param committeeEndorsementList 
	 * 						the committeeEndorsementList to set
	 */
	public void setCommitteeEndorsementList(
			List<ScQtCommitteeActivity> committeeEndorsementList) {
		this.committeeEndorsementList = committeeEndorsementList;
	}
	
	/**
	 * @return the reminderList
	 */
	public List<ScQtCommitteeActivity> getReminderList() {
		return reminderList;
	}
	
	/**
	 * @param reminderList 
	 * 				the reminderList to set
	 */
	public void setReminderList(List<ScQtCommitteeActivity> reminderList) {
		this.reminderList = reminderList;
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
	 * @return the biddingItemList
	 */
	public List<ScBidItem> getBiddingItemList() {
		return biddingItemList;
	}
	
	/**
	 * @param biddingItemList 
	 * 					the biddingItemList to set
	 */
	public void setBiddingItemList(List<ScBidItem> biddingItemList) {
		this.biddingItemList = biddingItemList;
	}
	
	/**
	 * @return the scBidding
	 */
	public ScBid getScBidding() {
		return scBidding;
	}
	
	/**
	 * @param scBidding 
	 * 				the scBidding to set
	 */
	public void setScBidding(ScBid scBidding) {
		this.scBidding = scBidding;
	}
	
	/**
	 * @return the biddingItemPriceList
	 */
	public List<ScBidPrice> getBiddingItemPriceList() {
		return biddingItemPriceList;
	}
	
	/**
	 * @param biddingItemPriceList 
	 * 						the biddingItemPriceList to set
	 */
	public void setBiddingItemPriceList(List<ScBidPrice> biddingItemPriceList) {
		this.biddingItemPriceList = biddingItemPriceList;
	}
	
	/**
	 * @return the biddingPackagePriceList
	 */
	public List<BidPackageBean> getBiddingPackagePriceList() {
		return biddingPackagePriceList;
	}
	
	/**
	 * @param biddingPackagePriceList 
	 * 					the biddingPackagePriceList to set
	 */
	public void setBiddingPackagePriceList(List<BidPackageBean> biddingPackagePriceList) {
		this.biddingPackagePriceList = biddingPackagePriceList;
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
	 * @return the selectedBiddingItem
	 */
	public ScBidItem getSelectedBiddingItem() {
		return selectedBiddingItem;
	}
	
	/**
	 * @param selectedBiddingItem 
	 * 					the selectedBiddingItem to set
	 */
	public void setSelectedBiddingItem(ScBidItem selectedBiddingItem) {
		this.selectedBiddingItem = selectedBiddingItem;
	}
	
	/**
	 * @return the remarks
	 */
	public List<ScQtFinalizationEndorsementRemarks> getRemarks() {
		return remarks;
	}
	
	/**
	 * @param remarks 
	 * 			the remarks to set
	 */
	public void setRemarks(List<ScQtFinalizationEndorsementRemarks> remarks) {
		this.remarks = remarks;
	}
	
	/**
	 * @return the attachmentDocType
	 */
	public String getAttachmentDocType() {
		return attachmentDocType;
	}
	
	/**
	 * @param attachmentDocType 
	 * 					the attachmentDocType to set
	 */
	public void setAttachmentDocType(String attachmentDocType) {
		this.attachmentDocType = attachmentDocType;
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
	 * @return  the action
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
	 * @return  the supplierCode
	 */
	public Integer getSupplierCode() {
		return supplierCode;
	}
	
	/**
	 * @param supplierCode
	 * 				the supplierCode to set
	 */
	public void setSupplierCode(Integer supplierCode) {
		this.supplierCode = supplierCode;
	}
	
	/**
	 * @return the scheduledBid
	 */
	public ScBidSchedule getScheduledBid() {
		return scheduledBid;
	}
	
	/**
	 * @param scheduledBid
	 * 				the scheduledBid to set
	 */
	public void setScheduledBid(ScBidSchedule scheduledBid) {
		this.scheduledBid = scheduledBid;
	}
	
	/**
	 * @return  the ScBidPriceList
	 */
	public List<ScBidPrice> getScBidPriceList() {
		return ScBidPriceList;
	}
	
	/**
	 * @param scBidPriceList
	 * 				the scBidPriceList to set
	 */
	public void setScBidPriceList(List<ScBidPrice> scBidPriceList) {
		ScBidPriceList = scBidPriceList;
	}
	
	/**
	 * @return the bidItemSuppliers
	 */
	public List<BidItemBean> getBidItemSuppliers() {
		return bidItemSuppliers;
	}
	
	/**
	 * @param bidItemSuppliers 
	 * 				the bidItemSuppliers to set
	 */
	public void setBidItemSuppliers(List<BidItemBean> bidItemSuppliers) {
		this.bidItemSuppliers = bidItemSuppliers;
	}
	
	/**
	 * @return  the lowestNonBumiOfferedPrice
	 */
	public BigDecimal getLowestNonBumiOfferedPrice() {
		return lowestNonBumiOfferedPrice;
	}
	
	/**
	 * @param lowestNonBumiOfferedPrice
	 * 				the lowestNonBumiOfferedPrice to set
	 */
	public void setLowestNonBumiOfferedPrice(BigDecimal lowestNonBumiOfferedPrice) {
		this.lowestNonBumiOfferedPrice = lowestNonBumiOfferedPrice;
	}
	
	/**
	 * @return  the bumiSuppLowestOfferedPrice
	 */
	public BigDecimal getBumiSuppLowestOfferedPrice() {
		return bumiSuppLowestOfferedPrice;
	}
	
	/**
	 * @param bumiSuppLowestOfferedPrice
	 * 				the bumiSuppLowestOfferedPrice to set
	 */
	public void setBumiSuppLowestOfferedPrice(BigDecimal bumiSuppLowestOfferedPrice) {
		this.bumiSuppLowestOfferedPrice = bumiSuppLowestOfferedPrice;
	}
	
	/**
	 * @return the shortlistedSupplier
	 */
	public ScQtEvaluationResult getShortlistedSupplier() {
		return shortlistedSupplier;
	}
	
	/**
	 * @param shortlistedSupplier 
	 * 					the shortlistedSupplier to set
	 */
	public void setShortlistedSupplier(ScQtEvaluationResult shortlistedSupplier) {
		this.shortlistedSupplier = shortlistedSupplier;
	}
	
	/**
	 * @return the awardedSupplier
	 */
	public ScQtEvaluationResultDtl getAwardedSupplier() {
		return awardedSupplier;
	}

	/**
	 * @param awardedSupplier 
	 * 				the awardedSupplier to set
	 */
	public void setAwardedSupplier(ScQtEvaluationResultDtl awardedSupplier) {
		this.awardedSupplier = awardedSupplier;
	}

	/**
	 * @return the selectedPackageBean
	 */
	public BidPackageBean getSelectedPackageBean() {
		return selectedPackageBean;
	}

	/**
	 * @param selectedPackageBean 
	 * 				the selectedPackageBean to set
	 */
	public void setSelectedPackageBean(BidPackageBean selectedPackageBean) {
		this.selectedPackageBean = selectedPackageBean;
	}

	/**
	 * @return the selectedBidItemBean
	 */
	public BidItemBean getSelectedBidItemBean() {
		return selectedBidItemBean;
	}

	/**
	 * @param selectedBidItemBean 
	 * 				the selectedBidItemBean to set
	 */
	public void setSelectedBidItemBean(BidItemBean selectedBidItemBean) {
		this.selectedBidItemBean = selectedBidItemBean;
	}

	/**
	 * @return the selectedPackageBeans
	 */
	public Map<Long, BidPackageBean> getSelectedPackageBeans() {
		return selectedPackageBeans;
	}

	/**
	 * @param selectedPackageBeans the selectedPackageBeans to set
	 */
	public void setSelectedPackageBeans(
			Map<Long, BidPackageBean> selectedPackageBeans) {
		this.selectedPackageBeans = selectedPackageBeans;
	}

	/**
	 * @return the selectedItemBeans
	 */
	public Map<Long, BidItemBean> getSelectedItemBeans() {
		return selectedItemBeans;
	}

	/**
	 * @param selectedItemBeans the selectedItemBeans to set
	 */
	public void setSelectedItemBeans(Map<Long, BidItemBean> selectedItemBeans) {
		this.selectedItemBeans = selectedItemBeans;
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
	 * @return the supplierBranch
	 */
	public String getSupplierBranch() {
		return supplierBranch;
	}
	
	/**
	 * @param supplierBranch 
	 * 				the supplierBranch to set
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
	 * @return the estimatedCost
	 */
	public BigDecimal getEstimatedCost() {
		return estimatedCost;
	}
	
	/**
	 * @param estimatedCost 
	 * 				the estimatedCost to set
	 */
	public void setEstimatedCost(BigDecimal estimatedCost) {
		this.estimatedCost = estimatedCost;
	}
	
	/**
	 * @return the nonBumiAvailable
	 */
	public boolean isNonBumiAvailable() {
		return nonBumiAvailable;
	}

	/**
	 * @param nonBumiAvailable 
	 * 				the nonBumiAvailable to set
	 */
	public void setNonBumiAvailable(boolean nonBumiAvailable) {
		this.nonBumiAvailable = nonBumiAvailable;
	}

	/**
	 * @return the zoneActiveIndex
	 */
	public int getZoneActiveIndex() {
		return ZoneActiveIndex;
	}
	
	/**
	 * @param zoneActiveIndex 
	 * 				the zoneActiveIndex to set
	 */
	public void setZoneActiveIndex(int zoneActiveIndex) {
		ZoneActiveIndex = zoneActiveIndex;
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
	 * @return the selectedZone
	 */
	public TplSpecZoneDtl getSelectedZone() {
		return selectedZone;
	}
	
	/**
	 * @param selectedZone 
	 * 				the selectedZone to set
	 */
	public void setSelectedZone(TplSpecZoneDtl selectedZone) {
		this.selectedZone = selectedZone;
	}
	
	/**
	 * @return the biddingZoneDetails
	 */
	public List<TplSpecZoneDtl> getBiddingZoneDetails() {
		return biddingZoneDetails;
	}
	
	/**
	 * @param biddingZoneDetails 
	 * 				the biddingZoneDetails to set
	 */
	public void setBiddingZoneDetails(List<TplSpecZoneDtl> biddingZoneDetails) {
		this.biddingZoneDetails = biddingZoneDetails;
	}
	
	/**
	 * @return the scQtRecommendsByZone
	 */
	public Map<Long, List<ScQtRecommend>> getScQtRecommendsByZone() {
		return scQtRecommendsByZone;
	}
	
	/**
	 * @param scQtRecommendsByZone 
	 * 				the scQtRecommendsByZone to set
	 */
	public void setScQtRecommendsByZone(
			Map<Long, List<ScQtRecommend>> scQtRecommendsByZone) {
		this.scQtRecommendsByZone = scQtRecommendsByZone;
	}
	/**
	 * @return the bidSchedulesByZone
	 */
	public Map<Long, List<ScBidSchedule>> getBidSchedulesByZone() {
		return bidSchedulesByZone;
	}
	
	/**
	 * @param bidSchedulesByZone 
	 * 				the bidSchedulesByZone to set
	 */
	public void setBidSchedulesByZone(
			Map<Long, List<ScBidSchedule>> bidSchedulesByZone) {
		this.bidSchedulesByZone = bidSchedulesByZone;
	}
	
	/**
	 * @return the bidPackageBeansByZone
	 */
	public Map<Long, List<BidPackageBean>> getBidPackageBeansByZone() {
		return bidPackageBeansByZone;
	}
	
	/**
	 * @param bidPackageBeansByZone 
	 * 				the bidPackageBeansByZone to set
	 */
	public void setBidPackageBeansByZone(
			Map<Long, List<BidPackageBean>> bidPackageBeansByZone) {
		this.bidPackageBeansByZone = bidPackageBeansByZone;
	}
	/**
	 * @return the bidItemsByZone
	 */
	public Map<Long, List<ScBidItem>> getBidItemsByZone() {
		return bidItemsByZone;
	}
	
	/**
	 * @param bidItemsByZone 
	 * 				the bidItemsByZone to set
	 */
	public void setBidItemsByZone(Map<Long, List<ScBidItem>> bidItemsByZone) {
		this.bidItemsByZone = bidItemsByZone;
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
	 * @return the itemShortlistedSuppliers
	 */
	public Map<Long, List<ScQtEvaluationResultDtl>> getItemShortlistedSuppliers() {
		return itemShortlistedSuppliers;
	}

	/**
	 * @param itemShortlistedSuppliers 
	 * 				the itemShortlistedSuppliers to set
	 */
	public void setItemShortlistedSuppliers(
			Map<Long, List<ScQtEvaluationResultDtl>> itemShortlistedSuppliers) {
		this.itemShortlistedSuppliers = itemShortlistedSuppliers;
	}

	/**
	 * @return the pkgShortlistedSuppliersByZone
	 */
	public Map<Long, List<ScQtEvaluationResultDtl>> getPkgShortlistedSuppliersByZone() {
		return pkgShortlistedSuppliersByZone;
	}

	/**
	 * @param pkgShortlistedSuppliersByZone 
	 * 				the pkgShortlistedSuppliersByZone to set
	 */
	public void setPkgShortlistedSuppliersByZone(
			Map<Long, List<ScQtEvaluationResultDtl>> pkgShortlistedSuppliersByZone) {
		this.pkgShortlistedSuppliersByZone = pkgShortlistedSuppliersByZone;
	}

	/**
	 * @return the itemShortlistedSuppliersByZone
	 */
	public Map<Long, Map<Long, List<ScQtEvaluationResultDtl>>> getItemShortlistedSuppliersByZone() {
		return itemShortlistedSuppliersByZone;
	}

	/**
	 * @param itemShortlistedSuppliersByZone 
	 * 				the itemShortlistedSuppliersByZone to set
	 */
	public void setItemShortlistedSuppliersByZone(
			Map<Long, Map<Long, List<ScQtEvaluationResultDtl>>> itemShortlistedSuppliersByZone) {
		this.itemShortlistedSuppliersByZone = itemShortlistedSuppliersByZone;
	}

	/**
	 * @return the secretaryUser
	 */
	public boolean isSecretaryUser() {
		return secretaryUser;
	}

	/**
	 * @param secretaryUser 
	 * 				the secretaryUser to set
	 */
	public void setSecretaryUser(boolean secretaryUser) {
		this.secretaryUser = secretaryUser;
	}

	/**
	 * @return the selectAll
	 */
	public boolean isSelectAll() {
		return selectAll;
	}
	
	/**
	 * @param selectAll 
	 * 			the selectAll to set
	 */
	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}
	
	/**
	 * @return the meetingActiveIndex
	 */
	public int getMeetingActiveIndex() {
		return meetingActiveIndex;
	}
	
	/**
	 * @param meetingActiveIndex 
	 * 				the meetingActiveIndex to set
	 */
	public void setMeetingActiveIndex(int meetingActiveIndex) {
		this.meetingActiveIndex = meetingActiveIndex;
	}
	
	/**
	 * @return the committeeTabLabel
	 */
	public String getCommitteeTabLabel() {
		return committeeTabLabel;
	}
	
	/**
	 * @param committeeTabLabel 
	 * 				the committeeTabLabel to set
	 */
	public void setCommitteeTabLabel(String committeeTabLabel) {
		this.committeeTabLabel = committeeTabLabel;
	}
	
	/**
	 * @return the meetingAttendanceList
	 */
	public List<ScQtMeetingAttendance> getMeetingAttendanceList() {
		return meetingAttendanceList;
	}
	
	/**
	 * @param meetingAttendanceList 
	 * 				the meetingAttendanceList to set
	 */
	public void setMeetingAttendanceList(
			List<ScQtMeetingAttendance> meetingAttendanceList) {
		this.meetingAttendanceList = meetingAttendanceList;
	}
	
	/**
	 * @return the committeeList
	 */
	public List<ScQtCommitteeActivity> getCommitteeList() {
		return committeeList;
	}
	
	/**
	 * @param committeeList 
	 * 			the committeeList to set
	 */
	public void setCommitteeList(List<ScQtCommitteeActivity> committeeList) {
		this.committeeList = committeeList;
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
		return SelectAllMeetingAttendees;
	}

	/**
	 * @param selectAllMeetingAttendees
	 *            the selectAllMeetingAttendees to set
	 */
	public void setSelectAllMeetingAttendees(boolean selectAllMeetingAttendees) {
		SelectAllMeetingAttendees = selectAllMeetingAttendees;
	}

	/**
	 * @return the zonal
	 */
	public boolean isZonal() {
		return zonal;
	}

	/**
	 * @param zonal the zonal to set
	 */
	public void setZonal(boolean zonal) {
		this.zonal = zonal;
	}

	/**
	 * @return the isItemized
	 */
	public boolean isItemized() {
		return isItemized;
	}

	/**
	 * @param isItemized the isItemized to set
	 */
	public void setItemized(boolean isItemized) {
		this.isItemized = isItemized;
	}

	/**
	 * @return the onlineBiddingEscalation
	 */
	public boolean isOnlineBiddingEscalation() {
		return onlineBiddingEscalation;
	}

	/**
	 * @param onlineBiddingEscalation 
	 * 				the onlineBiddingEscalation to set
	 */
	public void setOnlineBiddingEscalation(
						boolean onlineBiddingEscalation) {
		this.onlineBiddingEscalation = onlineBiddingEscalation;
	}

	/**
	 * @return the disableSupplierSelection
	 */
	public boolean isDisableSupplierSelection() {
		return disableSupplierSelection;
	}

	/**
	 * @param disableSupplierSelection 
	 * 				the disableSupplierSelection to set
	 */
	public void setDisableSupplierSelection(
						boolean disableSupplierSelection) {
		this.disableSupplierSelection = disableSupplierSelection;
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
	 * @return the selectedMeetingAttList
	 */
	public List<ScQtMeetingAttendance> getSelectedMeetingAttList() {
		return selectedMeetingAttList;
	}
	
	/**
	 * @param selectedMeetingAttList 
	 * 				the selectedMeetingAttList to set
	 */
	public void setSelectedMeetingAttList(
			List<ScQtMeetingAttendance> selectedMeetingAttList) {
		this.selectedMeetingAttList = selectedMeetingAttList;
	}

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
	 * @return the committeeRemarkList
	 */
	public List<ScQtCommitteRemarks> getCommitteeRemarkList() {
		return committeeRemarkList;
	}

	/**
	 * @param committeeRemarkList 
	 * 				the committeeRemarkList to set
	 */
	public void setCommitteeRemarkList(
					List<ScQtCommitteRemarks> committeeRemarkList) {
		this.committeeRemarkList = committeeRemarkList;
	}

	/**
	 * @return the shortlistedSuppliers
	 */
	public List<ScQtEvaluationResultDtl> getShortlistedSuppliers() {
		return shortlistedSuppliers;
	}

	/**
	 * @param shortlistedSuppliers 
	 * 				the shortlistedSuppliers to set
	 */
	public void setShortlistedSuppliers(
			List<ScQtEvaluationResultDtl> shortlistedSuppliers) {
		this.shortlistedSuppliers = shortlistedSuppliers;
	}

	/**
	 * @return the awardedRecommendDtlIds
	 */
	public List<String> getAwardedRecommendDtlIds() {
		return awardedRecommendDtlIds;
	}

	/**
	 * @param awardedRecommendDtlIds 
	 * 				the awardedRecommendDtlIds to set
	 */
	public void setAwardedRecommendDtlIds(
					List<String> awardedRecommendDtlIds) {
		this.awardedRecommendDtlIds = awardedRecommendDtlIds;
	}

	/**
	 * @return the bpkUser
	 */
	public boolean isBpkUser() {
		return bpkUser;
	}

	/**
	 * @param bpkUser the bpkUser to set
	 */
	public void setBpkUser(boolean bpkUser) {
		this.bpkUser = bpkUser;
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