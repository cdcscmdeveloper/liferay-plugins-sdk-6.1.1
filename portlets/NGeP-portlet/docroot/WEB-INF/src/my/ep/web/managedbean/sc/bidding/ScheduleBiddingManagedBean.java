/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.managedbean.sc.bidding;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.entity.sc.bidding.ScBid;
import my.ep.persistence.entity.sc.bidding.ScBidContactPerson;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.DateUtils;

/**
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
	private List<ScBidContactPerson> multiSelectContact;
	private Date bidStart;
	private Date bidEnd;
	private List<ScBidItem> bidItemList;
	private List<ScBidItem> selectedBidItemList;
	private ScBid scBid;
	private List<ScQtRecommendDtl> bidParticipants;
	
	private List<ScBidSchedule> bidScheduleList;
	private List<ScBidSchedule> selectedBidBidScheduleList;
	
	
	private Date minStartDate;
	private Date minEndDate;
	private Date maxEndDate;
	private String duration;
	private SCEvaluationDataType payLoad;
	
	public ScheduleBiddingManagedBean() {
		
		Calendar c = Calendar.getInstance(); 
		c.setTime(DateUtils.getCurrentDateWithoutTime()); 
		c.add(Calendar.DATE, 1);
		minStartDate = c.getTime();
	}
	
	
	/**
	 * @return the bidScheduleList
	 */
	public List<ScBidSchedule> getBidScheduleList() {
		return bidScheduleList;
	}


	/**
	 * @param bidScheduleList the bidScheduleList to set
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
	 * @param selectedBidBidScheduleList the selectedBidBidScheduleList to set
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
	 * @param bidParticipants the bidParticipants to set
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
	 * @param scQtFinalization the scQtFinalization to set
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
	 * @param scBid the scBid to set
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
	 * @param minEndDate the minEndDate to set
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
	 * @param maxEndDate the maxEndDate to set
	 */
	public void setMaxEndDate(Date maxEndDate) {
		this.maxEndDate = maxEndDate;
	}

	/**
	 * @return the duration
	 */
	public String getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}

	/**
	 * @return the minStartDate
	 */
	public Date getMinStartDate() {
		return minStartDate;
	}

	/**
	 * @param minStartDate the minStartDate to set
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
	 * @param selectedBidItemList the selectedBidItemList to set
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
	 * @param bidItemList the bidItemList to set
	 */
	public void setBidItemList(List<ScBidItem> bidItemList) {
		this.bidItemList = bidItemList;
	}

	/**
	 * @return the bidStart
	 */
	public Date getBidStart() {
		return bidStart;
	}

	/**
	 * @param bidStart the bidStart to set
	 */
	public void setBidStart(Date bidStart) {
		this.bidStart = bidStart;
	}

	/**
	 * @return the bidEnd
	 */
	public Date getBidEnd() {
		return bidEnd;
	}

	/**
	 * @param bidEnd the bidEnd to set
	 */
	public void setBidEnd(Date bidEnd) {
		this.bidEnd = bidEnd;
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
	 * @return the multiSelectContact
	 */
	public List<ScBidContactPerson> getMultiSelectContact() {
		return multiSelectContact;
	}

	/**
	 * @param multiSelectContact the multiSelectContact to set
	 */
	public void setMultiSelectContact(
			List<ScBidContactPerson> multiSelectContact) {
		this.multiSelectContact = multiSelectContact;
	}

	/**
	 * @return the scBidContactPersonList
	 */
	public List<ScBidContactPerson> getScBidContactPersonList() {
		return scBidContactPersonList;
	}

	/**
	 * @param scBidContactPersonList the scBidContactPersonList to set
	 */
	public void setScBidContactPersonList(
			List<ScBidContactPerson> scBidContactPersonList) {
		this.scBidContactPersonList = scBidContactPersonList;
	}


	public SCEvaluationDataType getPayLoad() {
		return payLoad;
	}


	public void setPayLoad(SCEvaluationDataType payLoad) {
		this.payLoad = payLoad;
	}

}
