package my.ep.web.bean.pp;

import java.util.Date;

public class BudgetAllocSearchBean {
	
	private String budgetAllocNo;
	
	private String budgetAllocDesc;
	
	private String gosodoCode;
	
	private String amanahCode;
	
	private String ptjCode;
	
	private Long statusId;
	

	
	/**
	 * @return the budgetAllocNo
	 */
	public String getBudgetAllocNo() {
		return budgetAllocNo;
	}



	/**
	 * @param budgetAllocNo the budgetAllocNo to set
	 */
	public void setBudgetAllocNo(String budgetAllocNo) {
		this.budgetAllocNo = budgetAllocNo;
	}



	/**
	 * @return the budgetAllocDesc
	 */
	public String getBudgetAllocDesc() {
		return budgetAllocDesc;
	}



	/**
	 * @param budgetAllocDesc the budgetAllocDesc to set
	 */
	public void setBudgetAllocDesc(String budgetAllocDesc) {
		this.budgetAllocDesc = budgetAllocDesc;
	}




	/**
	 * @return the gosodoCode
	 */
	public String getGosodoCode() {
		return gosodoCode;
	}



	/**
	 * @param gosodoCode the gosodoCode to set
	 */
	public void setGosodoCode(String gosodoCode) {
		this.gosodoCode = gosodoCode;
	}



	/**
	 * @return the amanahCode
	 */
	public String getAmanahCode() {
		return amanahCode;
	}



	/**
	 * @param amanahCode the amanahCode to set
	 */
	public void setAmanahCode(String amanahCode) {
		this.amanahCode = amanahCode;
	}



	/**
	 * @return the ptjCode
	 */
	public String getPtjCode() {
		return ptjCode;
	}



	/**
	 * @param ptjCode the ptjCode to set
	 */
	public void setPtjCode(String ptjCode) {
		this.ptjCode = ptjCode;
	}



	/**
	 * @return the statusId
	 */
	public Long getStatusId() {
		return statusId;
	}



	/**
	 * @param statusId the statusId to set
	 */
	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}



	/**
	 * clear the bean values
	 */
	public void clear(){
		budgetAllocDesc = null;
		budgetAllocNo = null;
		gosodoCode = null;
		amanahCode = null;
		ptjCode = null;
		statusId = null;
	}
	
	
}
