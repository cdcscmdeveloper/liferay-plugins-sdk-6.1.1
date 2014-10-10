package my.ep.web.managedbean.sc.supplierfinalization;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.sc.BidPackageBean;
import my.ep.persistence.entity.sc.bidding.ScBid;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationEndorsementRemarks;
import my.ep.web.managedbean.BaseManagedBean;

/**
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
	 * @param scQt the scQt to set
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
	 * @param scQtFinalization the scQtFinalization to set
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
	 * @param committeeEndorsementList the committeeEndorsementList to set
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
	 * @param reminderList the reminderList to set
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
	 * @return the pinNo
	 */
	public String getPinNo() {
		return pinNo;
	}
	/**
	 * @param pinNo the pinNo to set
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
	 * @param biddingItemList the biddingItemList to set
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
	 * @param scBidding the scBidding to set
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
	 * @param biddingItemPriceList the biddingItemPriceList to set
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
	 * @param biddingPackagePriceList the biddingPackagePriceList to set
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
	 * @param committeeType the committeeType to set
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
	 * @param selectedBiddingItem the selectedBiddingItem to set
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
	 * @param remarks the remarks to set
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
	 * @param attachmentDocType the attachmentDocType to set
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
	 * @param processName the processName to set
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	

	

}
