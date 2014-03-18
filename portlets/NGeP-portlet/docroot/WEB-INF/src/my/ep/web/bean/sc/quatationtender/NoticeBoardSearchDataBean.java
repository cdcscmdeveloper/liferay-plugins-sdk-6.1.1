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
	
	private boolean isToday;
	private boolean isYesterday;
	private String passDays;
	private Date fromDate;
	private Date toDate;
	
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
	public boolean isToday() {
		return isToday;
	}
	public void setToday(boolean isToday) {
		this.isToday = isToday;
	}
	public boolean isYesterday() {
		return isYesterday;
	}
	public void setYesterday(boolean isYesterday) {
		this.isYesterday = isYesterday;
	}

}
