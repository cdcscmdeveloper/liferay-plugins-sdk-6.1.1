/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.managedbean.sc.bidding;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.bidding.ScBidContactPerson;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author Venkat Chanumolu
 *
 */

@ManagedBean(name="scheduleBiddingMB")
@SessionScoped

public class ScheduleBiddingManagedBean extends BaseManagedBean {
	
	private static final long serialVersionUID = 1L;
	
	private int documentNo;
	private int ptjName;
	private int status;
	
	private List<ScBidContactPerson> scBidContactPerson;
	private List<ScBidContactPerson> multiSelectContact;
	private List<ScBidSchedule> scBidScheduleList;

		
	public List<ScBidSchedule> getScBidScheduleList() {
		return scBidScheduleList;
	}

	public void setScBidScheduleList(List<ScBidSchedule> scBidScheduleList) {
		this.scBidScheduleList = scBidScheduleList;
	}

	public int getDocumentNo() {
		return documentNo;
	}

	public void setDocumentNo(int documentNo) {
		this.documentNo = documentNo;
	}

	public int getPtjName() {
		return ptjName;
	}

	public void setPtjName(int ptjName) {
		this.ptjName = ptjName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	

	public List<ScBidContactPerson> getMultiSelectContact() {
		return multiSelectContact;
	}

	public void setMultiSelectContact(List<ScBidContactPerson> multiSelectContact) {
		this.multiSelectContact = multiSelectContact;
	}

	public List<ScBidContactPerson> getScBidContactPerson() {
		return scBidContactPerson;
	}

	public void setScBidContactPerson(List<ScBidContactPerson> scBidContactPerson) {
		this.scBidContactPerson = scBidContactPerson;
	}
	
	

}
