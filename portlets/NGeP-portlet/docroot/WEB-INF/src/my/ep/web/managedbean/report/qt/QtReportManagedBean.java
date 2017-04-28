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

package my.ep.web.managedbean.report.qt;

import my.ep.web.managedbean.report.ReportBaseManagedBean;

/**
 * The Class QtReportManagedBean.
 * 
 * @author:Garikapati Mangaiah.
 */


public abstract class QtReportManagedBean extends ReportBaseManagedBean {
	private static final long serialVersionUID = 1L;
	
	private String quotationNumner;
	private String quotationId;
	/**
	 * @return the quotationNumner
	 */
	public String getQuotationNumner() {
		return quotationNumner;
	}
	/**
	 * @param quotationNumner the quotationNumner to set
	 */
	public void setQuotationNumner(String quotationNumner) {
		this.quotationNumner = quotationNumner;
	}
	/**
	 * @return the quotationId
	 */
	public String getQuotationId() {
		return quotationId;
	}
	/**
	 * @param quotationId the quotationId to set
	 */
	public void setQuotationId(String quotationId) {
		this.quotationId = quotationId;
	}
	

}
