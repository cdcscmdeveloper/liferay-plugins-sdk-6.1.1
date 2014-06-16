/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.managedbean.sc.bidding;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.bidding.ScBid;
import my.ep.persistence.entity.sc.bidding.ScBidContactPerson;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author Venkat Chanumolu
 * 
 */

@ManagedBean(name = "scheduleBiddingMB")
@SessionScoped
public class ScheduleBiddingManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private ScQt scQt;
	private ScBid scBid;
	private List<ScBidSchedule> selectedScBidScheduleList;
	private List<ScBidContactPerson> scBidContactPersonList;
	private List<ScBidContactPerson> multiSelectContact;
	private Date bidStart;
	private Date bidEnd;

	public ScheduleBiddingManagedBean() {

		if (scBid == null) {

			System.out
					.println("******bid object is initially null********");

			scBid = new ScBid();
		}

	}

	/**
	 * @return the scBid
	 */
	public ScBid getScBid() {
		return scBid;
	}

	/**
	 * @param bid the bid to set
	 */
	public void setBid(ScBid bid) {
		this.scBid = bid;
	}

	/**
	 * @return the selectedScBidScheduleList
	 */
	public List<ScBidSchedule> getSelectedScBidScheduleList() {
		return selectedScBidScheduleList;
	}

	/**
	 * @param scBidScheduleList the scBidScheduleList to set
	 */
	public void setSelectedScBidScheduleList(
			List<ScBidSchedule> scBidScheduleList) {
		this.selectedScBidScheduleList = scBidScheduleList;
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

}
