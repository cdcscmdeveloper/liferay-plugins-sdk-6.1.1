/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.managedbean.sc.bidding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.constant.pm.ParameterConst;
import my.ep.persistence.entity.sc.bidding.ScBid;
import my.ep.persistence.entity.sc.bidding.ScBidContactPerson;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.DateUtils;

/**
 * Schedule Bidding Managed Bean
 * 
 * @author Venkat Chanumolu
 * 
 */

@ManagedBean(name = "scheduleBiddingMB")
@SessionScoped
public class ScheduleBiddingManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private ScQt scQt;
	private ScQtFinalization scQtFinalization;
	private List<ScBidContactPerson> scBidContactPersonList;
	private List<ScBidItem> bidItemList;
	private List<ScBidItem> selectedBidItemList;
	private ScBid scBid;
	private List<ScQtRecommendDtl> bidParticipants;
	private ScBidSchedule scheduleForParticipant;

	private List<ScBidSchedule> bidScheduleList;
	private List<ScBidSchedule> selectedBidBidScheduleList;

	private boolean selectedAll;
	private boolean selected;
	private List<Integer> selectedIndexes;

	private boolean selectedAllBidContacts;

	private ScBidItem selectedBidItem;
	private ScBidSchedule rowSelectedBid;

	private Date minStartDate;
	private Date minEndDate;
	private Date maxEndDate;

	private Long bidMinDuration;
	private Long bidMaxDuration;
	private SelectItem[] selectItems;
	private ScRequestItem requestItem;
	private String itemTypeService = ParameterConst.ITEM_TYPE_SERVICE;
	private BigDecimal awardedQuantity;
	// #30451-Start.
	private boolean isBdSfFlow;
	// #30451-End.

	/**
	 * Default COnstructor
	 */
	public ScheduleBiddingManagedBean() {
		Calendar c = Calendar.getInstance();
		c.setTime(DateUtils.getCurrentDateWithoutTime());
		c.add(Calendar.DATE, 1);
		minStartDate = c.getTime();
		minEndDate = c.getTime();
	}

	/**
	 * @return the scheduleForParticipant
	 */
	public ScBidSchedule getScheduleForParticipant() {
		return scheduleForParticipant;
	}

	/**
	 * @param scheduleForParticipant
	 *            the scheduleForParticipant to set
	 */
	public void setScheduleForParticipant(ScBidSchedule scheduleForParticipant) {
		this.scheduleForParticipant = scheduleForParticipant;
	}

	/**
	 * @return the rowSelectedBid
	 */
	public ScBidSchedule getRowSelectedBid() {
		return rowSelectedBid;
	}

	/**
	 * @param rowSelectedBid
	 *            the rowSelectedBid to set
	 */
	public void setRowSelectedBid(ScBidSchedule rowSelectedBid) {
		this.rowSelectedBid = rowSelectedBid;
	}

	/**
	 * @return the bidScheduleList
	 */
	public List<ScBidSchedule> getBidScheduleList() {
		return bidScheduleList;
	}

	/**
	 * @param bidScheduleList
	 *            the bidScheduleList to set
	 */
	public void setBidScheduleList(List<ScBidSchedule> bidScheduleList) {
		this.bidScheduleList = bidScheduleList;
	}

	/**
	 * @return the selectedBidBidScheduleList
	 */
	public List<ScBidSchedule> getSelectedBidBidScheduleList() {
		return selectedBidBidScheduleList;
	}

	/**
	 * @param selectedBidBidScheduleList
	 *            the selectedBidBidScheduleList to set
	 */
	public void setSelectedBidBidScheduleList(
			List<ScBidSchedule> selectedBidBidScheduleList) {
		this.selectedBidBidScheduleList = selectedBidBidScheduleList;
	}

	/**
	 * @return the bidParticipants
	 */
	public List<ScQtRecommendDtl> getBidParticipants() {
		return bidParticipants;
	}

	/**
	 * @param bidParticipants
	 *            the bidParticipants to set
	 */
	public void setBidParticipants(List<ScQtRecommendDtl> bidParticipants) {
		this.bidParticipants = bidParticipants;
	}

	/**
	 * @return the scQtFinalization
	 */
	public ScQtFinalization getScQtFinalization() {
		return scQtFinalization;
	}

	/**
	 * @param scQtFinalization
	 *            the scQtFinalization to set
	 */
	public void setScQtFinalization(ScQtFinalization scQtFinalization) {
		this.scQtFinalization = scQtFinalization;
	}

	/**
	 * @return the scBid
	 */
	public ScBid getScBid() {
		return scBid;
	}

	/**
	 * @param scBid
	 *            the scBid to set
	 */
	public void setScBid(ScBid scBid) {
		this.scBid = scBid;
	}

	/**
	 * @return the minEndDate
	 */
	public Date getMinEndDate() {
		return minEndDate;
	}

	/**
	 * @param minEndDate
	 *            the minEndDate to set
	 */
	public void setMinEndDate(Date minEndDate) {
		this.minEndDate = minEndDate;
	}

	/**
	 * @return the maxEndDate
	 */
	public Date getMaxEndDate() {
		return maxEndDate;
	}

	/**
	 * @param maxEndDate
	 *            the maxEndDate to set
	 */
	public void setMaxEndDate(Date maxEndDate) {
		this.maxEndDate = maxEndDate;
	}

	/**
	 * @return the minStartDate
	 */
	public Date getMinStartDate() {
		return minStartDate;
	}

	/**
	 * @param minStartDate
	 *            the minStartDate to set
	 */
	public void setMinStartDate(Date minStartDate) {
		this.minStartDate = minStartDate;
	}

	/**
	 * @return the selectedBidItemList
	 */
	public List<ScBidItem> getSelectedBidItemList() {
		return selectedBidItemList;
	}

	/**
	 * @param selectedBidItemList
	 *            the selectedBidItemList to set
	 */
	public void setSelectedBidItemList(List<ScBidItem> selectedBidItemList) {
		this.selectedBidItemList = selectedBidItemList;
	}

	/**
	 * @return the bidItemList
	 */
	public List<ScBidItem> getBidItemList() {
		return bidItemList;
	}

	/**
	 * @param bidItemList
	 *            the bidItemList to set
	 */
	public void setBidItemList(List<ScBidItem> bidItemList) {
		this.bidItemList = bidItemList;
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
	 * @return the scBidContactPersonList
	 */
	public List<ScBidContactPerson> getScBidContactPersonList() {
		return scBidContactPersonList;
	}

	/**
	 * @param scBidContactPersonList
	 *            the scBidContactPersonList to set
	 */
	public void setScBidContactPersonList(
			List<ScBidContactPerson> scBidContactPersonList) {
		this.scBidContactPersonList = scBidContactPersonList;
	}

	/**
	 * @return the selectedAll
	 */
	public boolean isSelectedAll() {
		return selectedAll;
	}

	/**
	 * @param selectedAll
	 *            the selectedAll to set
	 */
	public void setSelectedAll(boolean selectedAll) {
		this.selectedAll = selectedAll;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the selectedIndexes
	 */
	public List<Integer> getSelectedIndexes() {

		if (selectedIndexes == null) {
			selectedIndexes = new ArrayList<Integer>();
		}

		return selectedIndexes;
	}

	/**
	 * @param selectedIndexes
	 *            the selectedIndexes to set
	 */
	public void setSelectedIndexes(List<Integer> selectedIndexes) {
		this.selectedIndexes = selectedIndexes;
	}

	/**
	 * @return the selectedAllBidContacts
	 */
	public boolean isSelectedAllBidContacts() {
		return selectedAllBidContacts;
	}

	/**
	 * @param selectedAllBidContacts
	 *            the selectedAllBidContacts to set
	 */
	public void setSelectedAllBidContacts(boolean selectedAllBidContacts) {
		this.selectedAllBidContacts = selectedAllBidContacts;
	}

	/**
	 * @return the selectedBidItem
	 */
	public ScBidItem getSelectedBidItem() {
		return selectedBidItem;
	}

	/**
	 * @param selectedBidItem
	 *            the selectedBidItem to set
	 */
	public void setSelectedBidItem(ScBidItem selectedBidItem) {
		this.selectedBidItem = selectedBidItem;
	}

	/**
	 * @return the bidMinDuration
	 */
	public Long getBidMinDuration() {
		return bidMinDuration;
	}

	/**
	 * @param bidMinDuration
	 *            the bidMinDuration to set
	 */
	public void setBidMinDuration(Long bidMinDuration) {
		this.bidMinDuration = bidMinDuration;
	}

	/**
	 * @return the bidMaxDuration
	 */
	public Long getBidMaxDuration() {
		return bidMaxDuration;
	}

	/**
	 * @param bidMaxDuration
	 *            the bidMaxDuration to set
	 */
	public void setBidMaxDuration(Long bidMaxDuration) {
		this.bidMaxDuration = bidMaxDuration;
	}

	/**
	 * @return the selectItems
	 */
	public SelectItem[] getSelectItems() {
		return selectItems;
	}

	/**
	 * @param selectItems
	 *            the selectItems to set
	 */
	public void setSelectItems(SelectItem[] selectItems) {
		this.selectItems = selectItems;
	}

	/**
	 * @return the requestItem
	 */
	public ScRequestItem getRequestItem() {
		return requestItem;
	}

	/**
	 * @param requestItem
	 *            the requestItem to set
	 */
	public void setRequestItem(ScRequestItem requestItem) {
		this.requestItem = requestItem;
	}

	/**
	 * @return the itemTypeService
	 */
	public String getItemTypeService() {
		return itemTypeService;
	}

	/**
	 * @param itemTypeService the itemTypeService to set
	 */
	public void setItemTypeService(String itemTypeService) {
		this.itemTypeService = itemTypeService;
	}

	/**
	 * @return the awardedQuantity
	 */
	public BigDecimal getAwardedQuantity() {
		return awardedQuantity;
	}

	/**
	 * @param awardedQuantity the awardedQuantity to set
	 */
	public void setAwardedQuantity(BigDecimal awardedQuantity) {
		this.awardedQuantity = awardedQuantity;
	}

	// #30451-Start.
	/**
	 * @return the isBdSfFlow
	 */
	public boolean isBdSfFlow() {
		return isBdSfFlow;
	}

	/**
	 * @param isBdSfFlow the isBdSfFlow to set
	 */
	public void setBdSfFlow(boolean isBdSfFlow) {
		this.isBdSfFlow = isBdSfFlow;
	}
	// #30451-End.
	

}
