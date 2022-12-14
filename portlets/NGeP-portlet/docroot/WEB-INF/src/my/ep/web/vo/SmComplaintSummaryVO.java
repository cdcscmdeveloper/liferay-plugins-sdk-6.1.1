package my.ep.web.vo;

import java.io.Serializable;

public class SmComplaintSummaryVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long statusId;
	private String statusName;
	private int totalRecord;
	
	public Long getStatusId() {
		return statusId;
	}
	public String getStatusName() {
		return statusName;
	}
	public int getTotalRecord() {
		return totalRecord;
	}
	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

}
