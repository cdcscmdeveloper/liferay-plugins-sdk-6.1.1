package my.ep.web.bean.pp;

import java.util.Date;

public class ProcurementPlanSearchBean {
	
	private String status;
	
	private String planNo;
	
	private String planDesc;
	
	private String planType;
	
	private String procurementMode;
	
	private Date fromDate;
	
	private Date toDate;

	
	/**
	 * @return the planType
	 */
	public String getPlanType() {
		return planType;
	}

	/**
	 * @param planType the planType to set
	 */
	public void setPlanType(String planType) {
		this.planType = planType;
	}

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

	/**
	 * @return the planDesc
	 */
	public String getPlanDesc() {
		return planDesc;
	}

	/**
	 * @param planDesc the planDesc to set
	 */
	public void setPlanDesc(String planDesc) {
		this.planDesc = planDesc;
	}

	/**
	 * @return the procurementMode
	 */
	public String getProcurementMode() {
		return procurementMode;
	}

	/**
	 * @param procurementMode the procurementMode to set
	 */
	public void setProcurementMode(String procurementMode) {
		this.procurementMode = procurementMode;
	}
	
	/**
	 * clear the bean values
	 */
	public void clear(){
		status = null;
		planNo = null;
		planDesc = null;
		procurementMode = null;
		fromDate = null;
		toDate = null;
	}
	
	
}
