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

import my.ep.factory.EpLogger;

import org.apache.log4j.Logger;

/**
 * The Class SimpleQuoteInvitedSupplierRptManagedBean.
 *
 * @author Manga Garikapati
 * @since 08.06.2017
 */
@ManagedBean(name="SimpleQuoteInvitedSupplierRptManagedBean")
@SessionScoped
public class SimpleQuoteInvitedSupplierRptManagedBean extends DpReportManagedBean{
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger.getLogger(SimpleQuoteInvitedSupplierRptManagedBean.class);
	private String simpleQuoteNum;
	/**
	 * @return the simpleQuoteNum
	 */
	public String getSimpleQuoteNum() {
		return simpleQuoteNum;
	}
	/**
	 * @param simpleQuoteNum the simpleQuoteNum to set
	 */
	public void setSimpleQuoteNum(String simpleQuoteNum) {
		this.simpleQuoteNum = simpleQuoteNum;
	}
	
}
