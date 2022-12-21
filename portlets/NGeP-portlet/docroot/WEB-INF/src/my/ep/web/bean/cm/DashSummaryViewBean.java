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

import java.util.ArrayList;
import java.util.List;


/**
 * The Class DashSummaryViewBean.
 * @author zeinab
 */
public class DashSummaryViewBean {
	
	/** The doc type id. */
	private String docTypeId;
	
	/** The dash summary status list. */
	private List<DashSummaryStatus> dashSummaryStatusList;
	
	/**
	 * Gets the doc type id.
	 * 
	 * @return the doc type id
	 */
	public String getDocTypeId() {
		return docTypeId;
	}
	
	/**
	 * Sets the doc type id.
	 * 
	 * @param docTypeId
	 *            the new doc type id
	 */
	public void setDocTypeId(String docTypeId) {
		this.docTypeId = docTypeId;
	}
	
	/**
	 * Gets the dash summary status list.
	 * 
	 * @return the dash summary status list
	 */
	public List<DashSummaryStatus> getDashSummaryStatusList() {
		if(this.dashSummaryStatusList == null){
			this.dashSummaryStatusList = new ArrayList<DashSummaryStatus>();
		}
		return this.dashSummaryStatusList;
	}
	
	/**
	 * Sets the dash summary status list.
	 * 
	 * @param dashSummaryStatusList
	 *            the new dash summary status list
	 */
	public void setDashSummaryStatusList(
			List<DashSummaryStatus> dashSummaryStatusList) {
		this.dashSummaryStatusList = dashSummaryStatusList;
	}
	
	
	
}
