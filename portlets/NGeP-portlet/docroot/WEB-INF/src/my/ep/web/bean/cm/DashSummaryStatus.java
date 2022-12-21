/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.bean.cm;

import java.util.List;


/**
 * The Class DashSummaryStatus.
 * @author zeinab
 */
public class DashSummaryStatus {
	
	/** The status id. */
	private String statusId;
	
	/** The critical. */
	private int critical;
	
	/** The total. */
	private int total; //normal +critical
	

	/**
	 * Gets the status id.
	 * 
	 * @return the status id
	 */
	public String getStatusId() {
		return statusId;
	}
	
	/**
	 * Sets the status id.
	 * 
	 * @param statusId
	 *            the new status id
	 */
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}
	
	/**
	 * Gets the critical.
	 * 
	 * @return the critical
	 */
	public int getCritical() {
		return critical;
	}
	
	/**
	 * Sets the critical.
	 * 
	 * @param critical
	 *            the new critical
	 */
	public void setCritical(int critical) {
		this.critical = critical;
	}
	
	/**
	 * Gets the total.
	 * 
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}
	
	/**
	 * Sets the total.
	 * 
	 * @param total
	 *            the new total
	 */
	public void setTotal(int total) {
		this.total = total;
	}
	
	

}
