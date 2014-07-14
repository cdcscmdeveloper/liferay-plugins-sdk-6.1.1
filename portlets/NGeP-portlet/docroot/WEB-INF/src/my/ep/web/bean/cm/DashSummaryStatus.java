package my.ep.web.bean.cm;

import java.util.List;

public class DashSummaryStatus {
	private String statusId;
	private int critical;
	private int total; //normal +critical
	

	public String getStatusId() {
		return statusId;
	}
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}
	public int getCritical() {
		return critical;
	}
	public void setCritical(int critical) {
		this.critical = critical;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	

}
