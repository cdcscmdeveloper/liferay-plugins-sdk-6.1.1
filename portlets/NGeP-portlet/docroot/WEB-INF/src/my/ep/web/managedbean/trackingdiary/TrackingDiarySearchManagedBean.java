/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * Managed Bean for Tracking Diary Search
 * 
 * @author GiriBabu Borugadda
 * @since 22/09/2015
 */
@ManagedBean(name = "trackingDiarySearchManagedBean")
@SessionScoped
public class TrackingDiarySearchManagedBean extends BaseManagedBean {
	private static Logger log = Logger.getLogger(TrackingDiarySearchManagedBean.class);

	private static final long serialVersionUID = 1L;
	
	private String docId; 
	private String docNo; 
	private String docType;
	private Long statusId;
	
	/** The from page url. */
	private String fromPageUrl;
	/**
	 * @return the docId
	 */
	public String getDocId() {
		return docId;
	}
	/**
	 * @param docId the docId to set
	 */
	public void setDocId(String docId) {
		this.docId = docId;
	}
	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}
	/**
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}
	/**
	 * @return the fromPageUrl
	 */
	public String getFromPageUrl() {
		return fromPageUrl;
	}
	/**
	 * @param fromPageUrl the fromPageUrl to set
	 */
	public void setFromPageUrl(String fromPageUrl) {
		this.fromPageUrl = fromPageUrl;
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
	 * @return the docNo
	 */
	public String getDocNo() {
		return docNo;
	}
	/**
	 * @param docNo the docNo to set
	 */
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}
	
	

}
