/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */

package my.ep.web.managedbean.sc.bidding;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import my.ep.constant.pm.ParameterConst;
import my.ep.constants.CommonConstants;
import my.ep.persistence.entity.sc.bidding.ScBidAction;
import my.ep.persistence.entity.sc.bidding.ScBidContactPerson;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.sc.bidding.OnlineBiddingBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.sc.quatationtender.OnlineBiddingSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.CommonUtils;

/**
 * #8755 -start- Online Bidding Search Managed Bean
 * 
 * @author Manga
 * 
 */

@ManagedBean(name = "onlineBidSearchBBMB")
@SessionScoped
public class OnlineBiddingSearchManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private OnlineBiddingSearchBean onlineBidSearch;

	private IPagingDataModel<ScBidSchedule> scheduleBidDataModel;



	private List<ScBidSchedule> activeBidSchedules;
	private ScBidSchedule scBidSchedule;
	private List<ScBidPrice> bidPrices;
	private List<ScBidContactPerson> bidContactPersons;
	private SmSupplier smSupplier;
	private ScBidAction scBidAction;
	private BigDecimal packageRanking;
	private ScBidPrice scBidPrice;
	private ScBidPrice newScBidPrice;
	private BigDecimal selectedDecrementAmt;
	private BigDecimal newBidPrice;
	private Long selectedBidScheduleId;

	private Long previousStatusId;

	private boolean isBidPaused = false;

	private BigDecimal itemStartPriceTotal;
	private BigDecimal itemLastBidPriceTotal;
	private BigDecimal itemNewBidPriceTotal;
	private Long interval;
	private Long remainingBidTime;

	private String remainingTime;
	private String remainingHours;
	private String remainingMinutes;
	private String remainingSeconds;
	private int index;

	private BigDecimal tolerancePriceLowerBound;
	private BigDecimal tolerancePriceUpperBound;
	private boolean toleranceCheck = Boolean.FALSE;
	private String previousPageUrl = null;
	private BigDecimal decrementBlockPercentage;
	private String decrementBlockPopupMessage = "";

	private Long servicetype = Long.parseLong(ParameterConst.ITEM_TYPE_SERVICE);

	private ScQt scQt;




	/**
	 * @return the onlineBidSearch
	 */
	public OnlineBiddingSearchBean getOnlineBidSearch() {
		return onlineBidSearch;
	}

	/**
	 * @param onlineBidSearch
	 *            the onlineBidSearch to set
	 */
	public void setOnlineBidSearch(OnlineBiddingSearchBean onlineBidSearch) {
		this.onlineBidSearch = onlineBidSearch;
	}

	/**
	 * @return the scheduleBidDataModel
	 */
	public IPagingDataModel<ScBidSchedule> getScheduleBidDataModel() {
		return scheduleBidDataModel;
	}

	/**
	 * @param scheduleBidDataModel
	 *            the scheduleBidDataModel to set
	 */
	public void setScheduleBidDataModel(
			IPagingDataModel<ScBidSchedule> scheduleBidDataModel) {
		this.scheduleBidDataModel = scheduleBidDataModel;
	}

	

	/**
	 * @return the activeBidSchedules
	 */
	public List<ScBidSchedule> getActiveBidSchedules() {
		return activeBidSchedules;
	}

	/**
	 * @param activeBidSchedules the activeBidSchedules to set
	 */
	public void setActiveBidSchedules(List<ScBidSchedule> activeBidSchedules) {
		this.activeBidSchedules = activeBidSchedules;
	}

	/**
	 * @return the selectedBidScheduleId
	 */
	public Long getSelectedBidScheduleId() {
		return selectedBidScheduleId;
	}

	/**
	 * @param selectedBidScheduleId the selectedBidScheduleId to set
	 */
	public void setSelectedBidScheduleId(Long selectedBidScheduleId) {
		this.selectedBidScheduleId = selectedBidScheduleId;
	}

	/**
	 * @return the scBidSchedule
	 */
	public ScBidSchedule getScBidSchedule() {
		return scBidSchedule;
	}

	/**
	 * @param scBidSchedule the scBidSchedule to set
	 */
	public void setScBidSchedule(ScBidSchedule scBidSchedule) {
		this.scBidSchedule = scBidSchedule;
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
	 * @return the previousStatusId
	 */
	public Long getPreviousStatusId() {
		return previousStatusId;
	}

	/**
	 * @param previousStatusId the previousStatusId to set
	 */
	public void setPreviousStatusId(Long previousStatusId) {
		this.previousStatusId = previousStatusId;
	}

	/**
	 * @return the remainingTime
	 */
	public String getRemainingTime() {
		return remainingTime;
	}

	/**
	 * @param remainingTime the remainingTime to set
	 */
	public void setRemainingTime(String remainingTime) {
		this.remainingTime = remainingTime;
	}

	/**
	 * @return the bidPrices
	 */
	public List<ScBidPrice> getBidPrices() {
		return bidPrices;
	}

	/**
	 * @param bidPrices the bidPrices to set
	 */
	public void setBidPrices(List<ScBidPrice> bidPrices) {
		this.bidPrices = bidPrices;
	}

	/**
	 * @return the bidContactPersons
	 */
	public List<ScBidContactPerson> getBidContactPersons() {
		return bidContactPersons;
	}

	/**
	 * @param bidContactPersons the bidContactPersons to set
	 */
	public void setBidContactPersons(List<ScBidContactPerson> bidContactPersons) {
		this.bidContactPersons = bidContactPersons;
	}

	/**
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * @param smSupplier the smSupplier to set
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	/**
	 * @return the scBidAction
	 */
	public ScBidAction getScBidAction() {
		return scBidAction;
	}

	/**
	 * @param scBidAction the scBidAction to set
	 */
	public void setScBidAction(ScBidAction scBidAction) {
		this.scBidAction = scBidAction;
	}

	/**
	 * @return the packageRanking
	 */
	public BigDecimal getPackageRanking() {
		return packageRanking;
	}

	/**
	 * @param packageRanking the packageRanking to set
	 */
	public void setPackageRanking(BigDecimal packageRanking) {
		this.packageRanking = packageRanking;
	}

	/**
	 * @return the scBidPrice
	 */
	public ScBidPrice getScBidPrice() {
		return scBidPrice;
	}

	/**
	 * @param scBidPrice the scBidPrice to set
	 */
	public void setScBidPrice(ScBidPrice scBidPrice) {
		this.scBidPrice = scBidPrice;
	}

	/**
	 * @return the newScBidPrice
	 */
	public ScBidPrice getNewScBidPrice() {
		return newScBidPrice;
	}

	/**
	 * @param newScBidPrice the newScBidPrice to set
	 */
	public void setNewScBidPrice(ScBidPrice newScBidPrice) {
		this.newScBidPrice = newScBidPrice;
	}

	/**
	 * @return the selectedDecrementAmt
	 */
	public BigDecimal getSelectedDecrementAmt() {
		return selectedDecrementAmt;
	}

	/**
	 * @param selectedDecrementAmt the selectedDecrementAmt to set
	 */
	public void setSelectedDecrementAmt(BigDecimal selectedDecrementAmt) {
		this.selectedDecrementAmt = selectedDecrementAmt;
	}

	/**
	 * @return the newBidPrice
	 */
	public BigDecimal getNewBidPrice() {
		return newBidPrice;
	}

	/**
	 * @param newBidPrice the newBidPrice to set
	 */
	public void setNewBidPrice(BigDecimal newBidPrice) {
		this.newBidPrice = newBidPrice;
	}



	/**
	 * @return the isBidPaused
	 */
	public boolean isBidPaused() {
		return isBidPaused;
	}

	/**
	 * @param isBidPaused the isBidPaused to set
	 */
	public void setBidPaused(boolean isBidPaused) {
		this.isBidPaused = isBidPaused;
	}

	/**
	 * @return the itemStartPriceTotal
	 */
	public BigDecimal getItemStartPriceTotal() {
		return itemStartPriceTotal;
	}

	/**
	 * @param itemStartPriceTotal the itemStartPriceTotal to set
	 */
	public void setItemStartPriceTotal(BigDecimal itemStartPriceTotal) {
		this.itemStartPriceTotal = itemStartPriceTotal;
	}

	/**
	 * @return the itemLastBidPriceTotal
	 */
	public BigDecimal getItemLastBidPriceTotal() {
		return itemLastBidPriceTotal;
	}

	/**
	 * @param itemLastBidPriceTotal the itemLastBidPriceTotal to set
	 */
	public void setItemLastBidPriceTotal(BigDecimal itemLastBidPriceTotal) {
		this.itemLastBidPriceTotal = itemLastBidPriceTotal;
	}

	/**
	 * @return the itemNewBidPriceTotal
	 */
	public BigDecimal getItemNewBidPriceTotal() {
		return itemNewBidPriceTotal;
	}

	/**
	 * @param itemNewBidPriceTotal the itemNewBidPriceTotal to set
	 */
	public void setItemNewBidPriceTotal(BigDecimal itemNewBidPriceTotal) {
		this.itemNewBidPriceTotal = itemNewBidPriceTotal;
	}

	/**
	 * @return the interval
	 */
	public Long getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(Long interval) {
		this.interval = interval;
	}

	/**
	 * @return the remainingBidTime
	 */
	public Long getRemainingBidTime() {
		return remainingBidTime;
	}

	/**
	 * @param remainingBidTime the remainingBidTime to set
	 */
	public void setRemainingBidTime(Long remainingBidTime) {
		this.remainingBidTime = remainingBidTime;
	}

	/**
	 * @return the remainingHours
	 */
	public String getRemainingHours() {
		return remainingHours;
	}

	/**
	 * @param remainingHours the remainingHours to set
	 */
	public void setRemainingHours(String remainingHours) {
		this.remainingHours = remainingHours;
	}

	/**
	 * @return the remainingMinutes
	 */
	public String getRemainingMinutes() {
		return remainingMinutes;
	}

	/**
	 * @param remainingMinutes the remainingMinutes to set
	 */
	public void setRemainingMinutes(String remainingMinutes) {
		this.remainingMinutes = remainingMinutes;
	}

	/**
	 * @return the remainingSeconds
	 */
	public String getRemainingSeconds() {
		return remainingSeconds;
	}

	/**
	 * @param remainingSeconds the remainingSeconds to set
	 */
	public void setRemainingSeconds(String remainingSeconds) {
		this.remainingSeconds = remainingSeconds;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the tolerancePriceLowerBound
	 */
	public BigDecimal getTolerancePriceLowerBound() {
		return tolerancePriceLowerBound;
	}

	/**
	 * @param tolerancePriceLowerBound the tolerancePriceLowerBound to set
	 */
	public void setTolerancePriceLowerBound(BigDecimal tolerancePriceLowerBound) {
		this.tolerancePriceLowerBound = tolerancePriceLowerBound;
	}

	/**
	 * @return the tolerancePriceUpperBound
	 */
	public BigDecimal getTolerancePriceUpperBound() {
		return tolerancePriceUpperBound;
	}

	/**
	 * @param tolerancePriceUpperBound the tolerancePriceUpperBound to set
	 */
	public void setTolerancePriceUpperBound(BigDecimal tolerancePriceUpperBound) {
		this.tolerancePriceUpperBound = tolerancePriceUpperBound;
	}

	/**
	 * @return the toleranceCheck
	 */
	public boolean isToleranceCheck() {
		return toleranceCheck;
	}

	/**
	 * @param toleranceCheck the toleranceCheck to set
	 */
	public void setToleranceCheck(boolean toleranceCheck) {
		this.toleranceCheck = toleranceCheck;
	}

	/**
	 * @return the previousPageUrl
	 */
	public String getPreviousPageUrl() {
		return previousPageUrl;
	}

	/**
	 * @param previousPageUrl the previousPageUrl to set
	 */
	public void setPreviousPageUrl(String previousPageUrl) {
		this.previousPageUrl = previousPageUrl;
	}

	/**
	 * @return the decrementBlockPercentage
	 */
	public BigDecimal getDecrementBlockPercentage() {
		return decrementBlockPercentage;
	}

	/**
	 * @param decrementBlockPercentage the decrementBlockPercentage to set
	 */
	public void setDecrementBlockPercentage(BigDecimal decrementBlockPercentage) {
		this.decrementBlockPercentage = decrementBlockPercentage;
	}

	/**
	 * @return the decrementBlockPopupMessage
	 */
	public String getDecrementBlockPopupMessage() {
		return decrementBlockPopupMessage;
	}

	/**
	 * @param decrementBlockPopupMessage the decrementBlockPopupMessage to set
	 */
	public void setDecrementBlockPopupMessage(String decrementBlockPopupMessage) {
		this.decrementBlockPopupMessage = decrementBlockPopupMessage;
	}

	/**
	 * @return the servicetype
	 */
	public Long getServicetype() {
		return servicetype;
	}

	/**
	 * @param servicetype the servicetype to set
	 */
	public void setServicetype(Long servicetype) {
		this.servicetype = servicetype;
	}


	

}
