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
package my.ep.web.managedbean.report.dp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * The Class LetterOfAcceptanceRptManagedBean.
 *
 * @author AnJie
 * @since 03.05.2017
 */
@ManagedBean(name="loaRptManagedBean")
@SessionScoped
public class LetterOfAcceptanceRptManagedBean extends DpReportManagedBean{
	private static final long serialVersionUID = 1L;
	private String simpleQuoteNo;
	private String simpleQuoteType;
	
	/**
	 * @return the simpleQuoteNo
	 */
	public String getSimpleQuoteNo() {
		return simpleQuoteNo;
	}
	/**
	 * @param simpleQuoteNo the simpleQuoteNo to set
	 */
	public void setSimpleQuoteNo(String simpleQuoteNo) {
		this.simpleQuoteNo = simpleQuoteNo;
	}
	/**
	 * @return the simpleQuoteType
	 */
	public String getSimpleQuoteType() {
		return simpleQuoteType;
	}
	/**
	 * @param simpleQuoteType the simpleQuoteType to set
	 */
	public void setSimpleQuoteType(String simpleQuoteType) {
		this.simpleQuoteType = simpleQuoteType;
	}
}
