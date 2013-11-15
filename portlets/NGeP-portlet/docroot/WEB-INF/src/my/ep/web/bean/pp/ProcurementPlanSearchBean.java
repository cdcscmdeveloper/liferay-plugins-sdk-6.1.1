package my.ep.web.bean.pp;

import java.util.Date;

public class ProcurementPlanSearchBean {
	
	private String status;
	
	private String planNo;
	
	private Date fromDate;
	
	private Date toDate;

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the planNo
	 */
	public String getPlanNo() {
		return planNo;
	}

	/**
	 * @param planNo the planNo to set
	 */
	public void setPlanNo(String planNo) {
		this.planNo = planNo;
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
