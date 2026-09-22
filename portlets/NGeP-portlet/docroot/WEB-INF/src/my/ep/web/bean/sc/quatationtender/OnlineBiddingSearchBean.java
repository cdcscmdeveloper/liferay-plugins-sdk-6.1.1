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
package my.ep.web.bean.sc.quatationtender;

import java.io.Serializable;
import java.util.Date;

import javax.faces.model.SelectItem;

/**
 * //8755 -start- 
 * The Class OnlineBiddingSearchBean.
 * 
 * @author Manga.
 */
public class OnlineBiddingSearchBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private String searchBiddingNo;
	private SelectItem[] statusDescs;
	private Long selectedStatusDesc;
	private String searchDocumentNo;
	private String searchBidTitle;
	private Date searchBiddingDateFrom;
	private Date searchBiddingDateTo;
	private Date searchBiddingDateToMinDate;

	/**
	 * @return the searchBiddingNo
	 */
	public String getSearchBiddingNo() {
		return searchBiddingNo;
	}

	/**
	 * @param searchBiddingNo
	 *            the searchBiddingNo to set
	 */
	public void setSearchBiddingNo(String searchBiddingNo) {
		this.searchBiddingNo = searchBiddingNo;
	}

	/**
	 * @return the statusDescs
	 */
	public SelectItem[] getStatusDescs() {
		return statusDescs;
	}

	/**
	 * @param statusDescs
	 *            the statusDescs to set
	 */
	public void setStatusDescs(SelectItem[] statusDescs) {
		this.statusDescs = statusDescs;
	}

	/**
	 * @return the selectedStatusDesc
	 */
	public Long getSelectedStatusDesc() {
		return selectedStatusDesc;
	}

	/**
	 * @param selectedStatusDesc
	 *            the selectedStatusDesc to set
	 */
	public void setSelectedStatusDesc(Long selectedStatusDesc) {
		this.selectedStatusDesc = selectedStatusDesc;
	}

	/**
	 * @return the searchDocumentNo
	 */
	public String getSearchDocumentNo() {
		return searchDocumentNo;
	}

	/**
	 * @param searchDocumentNo
	 *            the searchDocumentNo to set
	 */
	public void setSearchDocumentNo(String searchDocumentNo) {
		this.searchDocumentNo = searchDocumentNo;
	}

	/**
	 * @return the searchBidTitle
	 */
	public String getSearchBidTitle() {
		return searchBidTitle;
	}

	/**
	 * @param searchBidTitle
	 *            the searchBidTitle to set
	 */
	public void setSearchBidTitle(String searchBidTitle) {
		this.searchBidTitle = searchBidTitle;
	}

	/**
	 * @return the searchBiddingDateFrom
	 */
	public Date getSearchBiddingDateFrom() {
		return searchBiddingDateFrom;
	}

	/**
	 * @param searchBiddingDateFrom
	 *            the searchBiddingDateFrom to set
	 */
	public void setSearchBiddingDateFrom(Date searchBiddingDateFrom) {
		this.searchBiddingDateFrom = searchBiddingDateFrom;
	}

	/**
	 * @return the searchBiddingDateTo
	 */
	public Date getSearchBiddingDateTo() {
		return searchBiddingDateTo;
	}

	/**
	 * @param searchBiddingDateTo
	 *            the searchBiddingDateTo to set
	 */
	public void setSearchBiddingDateTo(Date searchBiddingDateTo) {
		this.searchBiddingDateTo = searchBiddingDateTo;
	}

	/**
	 * @return the searchBiddingDateToMinDate
	 */
	public Date getSearchBiddingDateToMinDate() {
		return searchBiddingDateToMinDate;
	}

	/**
	 * @param searchBiddingDateToMinDate
	 *            the searchBiddingDateToMinDate to set
	 */
	public void setSearchBiddingDateToMinDate(Date searchBiddingDateToMinDate) {
		this.searchBiddingDateToMinDate = searchBiddingDateToMinDate;
	}

}
