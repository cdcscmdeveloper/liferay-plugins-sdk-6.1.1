/**
 * 
 */
package my.ep.web.bean.sc.quatationtender;

import java.util.Date;

/**
 * @author VidyasagarReddy
 *
 */
public class NoticeBoardSearchDataBean {
	
	private Date today;
	private Date yesterday;
	private String passDays;
	private Date fromDate;
	private Date toDate;
	public Date getToday() {
		return today;
	}
	public void setToday(Date today) {
		this.today = today;
	}
	public Date getYesterday() {
		return yesterday;
	}
	public void setYesterday(Date yesterday) {
		this.yesterday = yesterday;
	}
	public String getPassDays() {
		return passDays;
	}
	public void setPassDays(String passDays) {
		this.passDays = passDays;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

}
