/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.managedbean.sc.pendingresponseqtdp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * The Class PendingResponseDpQtManagedBean.
 * 
 * @author:Garikapati Mangaiah.
 */
@ManagedBean
@SessionScoped
public class PendingResponseDpQtManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(PendingResponseDpQtManagedBean.class);
	private int quotationCount=0;
	private int tenderCount=0;
	private int briefingSiteVisitCount=0;
	private int biddingCount=0;
	private int simpleQuoteCount=0;
	/**
	 * @return the quotationCount
	 */
	public int getQuotationCount() {
		return quotationCount;
	}
	/**
	 * @param quotationCount the quotationCount to set
	 */
	public void setQuotationCount(int quotationCount) {
		this.quotationCount = quotationCount;
	}
	/**
	 * @return the tenderCount
	 */
	public int getTenderCount() {
		return tenderCount;
	}
	/**
	 * @param tenderCount the tenderCount to set
	 */
	public void setTenderCount(int tenderCount) {
		this.tenderCount = tenderCount;
	}
	/**
	 * @return the briefingSiteVisitCount
	 */
	public int getBriefingSiteVisitCount() {
		return briefingSiteVisitCount;
	}
	/**
	 * @param briefingSiteVisitCount the briefingSiteVisitCount to set
	 */
	public void setBriefingSiteVisitCount(int briefingSiteVisitCount) {
		this.briefingSiteVisitCount = briefingSiteVisitCount;
	}
	/**
	 * @return the biddingCount
	 */
	public int getBiddingCount() {
		return biddingCount;
	}
	/**
	 * @param biddingCount the biddingCount to set
	 */
	public void setBiddingCount(int biddingCount) {
		this.biddingCount = biddingCount;
	}
	/**
	 * @return the simpleQuoteCount
	 */
	public int getSimpleQuoteCount() {
		return simpleQuoteCount;
	}
	/**
	 * @param simpleQuoteCount the simpleQuoteCount to set
	 */
	public void setSimpleQuoteCount(int simpleQuoteCount) {
		this.simpleQuoteCount = simpleQuoteCount;
	}

}
