package my.ep.web.vo;

import java.io.Serializable;
import java.util.Date;

public class DisciplinaryActionVO implements Serializable {
	private static final long serialVersionUID = 5887063064190149198L;
	private String remark;
	private Date startDate;
	private Date endDate;
	private String suspend;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getSuspend() {
		return suspend;
	}

	public void setSuspend(String suspend) {
		this.suspend = suspend;
	}
}
