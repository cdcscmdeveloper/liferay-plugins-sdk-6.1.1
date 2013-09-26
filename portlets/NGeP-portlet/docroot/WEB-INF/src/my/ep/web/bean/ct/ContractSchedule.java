package my.ep.web.bean.ct;

import java.io.Serializable;
import java.util.Date;

public class ContractSchedule implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Date toDate;
	private Date fromDate;
	private String phase;
	private String percentage;
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase;
	}
	public String getPercentage() {
		return percentage;
	}
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
	
	
}
