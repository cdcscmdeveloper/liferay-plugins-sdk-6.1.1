/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.supplierfinalization;

import java.math.BigDecimal;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.sc.BidItemBean;
import my.ep.biz.bean.sc.BidPackageBean;
import my.ep.persistence.entity.sc.bidding.ScBid;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationEndorsementRemarks;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;

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
		
	//Online Bidding Endorsement
	private List<ScQtCommitteeActivity> committeeEndorsementList;
	private List<ScQtCommitteeActivity> reminderList;
	
	private String subject;
	private String message;
	private String pinNo;
	
	//Offline Bidding Endorsement
	private List<ScQtFinalizationEndorsementRemarks> remarks;
	private String attachmentDocType;
	private Object payLoad;
	private String action;
	
	// Item price pop up
	private Long supplierCode;
	private ScBidSchedule scheduledBid;
	private List<ScBidPrice> ScBidPriceList;
	private List<BidItemBean> bidPriceItems;
	
	private BigDecimal lowestNonBumiOfferedPrice;
	private BigDecimal bumiSuppLowestOfferedPrice;
	
	private ScQtEvaluationResult shortlistedSupplier;
	private SmSupplier smSupplier;
	private SmMofAccount smMofAccount;
	private SmCompanyBasic smCompanyBasic;
	private SmPersonnel smPersonnel;
	private String supplierBranch;
	private SmAddress supplierAddress;
	
	private BigDecimal estimatedCost;
	
	private boolean isBumiAndNonBumiCombination = false;
	
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
	 * @return  the payLoad
	 */
	public Object getPayLoad() {
		return payLoad;
	}
	/**
	 * @param payLoad
	 * 			the payLoad to set
	 */
	public void setPayLoad(Object payLoad) {
		this.payLoad = payLoad;
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
	public Long getSupplierCode() {
		return supplierCode;
	}
	/**
	 * @param supplierCode
	 * 				the supplierCode to set
	 */
	public void setSupplierCode(Long supplierCode) {
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
	 * @return the bidPriceItems
	 */
	public List<BidItemBean> getBidPriceItems() {
		return bidPriceItems;
	}
	/**
	 * @param bidPriceItems 
	 * 				the bidPriceItems to set
	 */
	public void setBidPriceItems(List<BidItemBean> bidPriceItems) {
		this.bidPriceItems = bidPriceItems;
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
	 * @return the isBumiAndNonBumiCombination
	 */
	public boolean isBumiAndNonBumiCombination() {
		return isBumiAndNonBumiCombination;
	}
	
	/**
	 * @param isBumiAndNonBumiCombination 
	 * 				the isBumiAndNonBumiCombination to set
	 */
	public void setBumiAndNonBumiCombination(boolean isBumiAndNonBumiCombination) {
		this.isBumiAndNonBumiCombination = isBumiAndNonBumiCombination;
	}
	
}
