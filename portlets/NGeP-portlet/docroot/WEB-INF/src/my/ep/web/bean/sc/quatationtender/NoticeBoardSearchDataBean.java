/* Copyright © 2014 Archisoft. All rights reserved. */
package my.ep.web.bean.sc.quatationtender;

import java.io.Serializable;
import java.util.Date;

/**
 * NoticeBoardSearchDataBean class holds the properties to search data in Notice Board screen. 
 * 
 * @author VidyasagarReddy
 *
 */
public class NoticeBoardSearchDataBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private boolean isToday;
	private boolean isYesterday;
	private Integer passDays;
	private Date fromDate;
	private Date toDate;

	/**
	 * @return the isToday
	 */
	public boolean isToday() {
		return isToday;
	}

	/**
	 * @param isToday the isToday to set
	 */
	public void setToday(boolean isToday) {
		this.isToday = isToday;
	}

	/**
	 * @return the isYesterday
	 */
	public boolean isYesterday() {
		return isYesterday;
	}

	/**
	 * @param isYesterday the isYesterday to set
	 */
	public void setYesterday(boolean isYesterday) {
		this.isYesterday = isYesterday;
	}

	/**
	 * @return the passDays
	 */
	public Integer getPassDays() {
		return passDays;
	}

	/**
	 * @param passDays the passDays to set
	 */
	public void setPassDays(Integer passDays) {
		this.passDays = passDays;
	}

	/**
	 * @return the fromDate
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return the toDate
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

}
