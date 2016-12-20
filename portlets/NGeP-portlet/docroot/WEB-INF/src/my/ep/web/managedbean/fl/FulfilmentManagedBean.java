/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.web.managedbean.BaseManagedBean;

// TODO: Auto-generated Javadoc
/**
 * Managed Bean for Fulfilment.
 *
 * @author KK LIM
 * @since 20/11/2013
 */
@ManagedBean(name = "flMB")
@SessionScoped
public class FulfilmentManagedBean extends BaseManagedBean {
	
	private static final long serialVersionUID = 1L;
	
	// Remarks and attachments
	private FlAttachment flAttachment = new FlAttachment();
	private List<FlRemark> remarkList;
	private List<FlAttachment> attachmentList;
	private boolean docMaintainRemarks = false;
	private boolean hideRemarks = false;
	private int attachedFileIndex;
	private List<FlAttachment> deleteAttachments;
		
	private String docType;
	private String docNo;
	
	
	/**
	 * Instantiates a new fulfilment managed bean.
	 */
	public FulfilmentManagedBean(){
		
	}

	/**
	 * Gets the fl attachment.
	 *
	 * @return the flAttachment
	 */
	public FlAttachment getFlAttachment() {
		return flAttachment;
	}

	/**
	 * Sets the fl attachment.
	 *
	 * @param flAttachment the flAttachment to set
	 */
	public void setFlAttachment(FlAttachment flAttachment) {
		this.flAttachment = flAttachment;
	}

	/**
	 * Gets the remark list.
	 *
	 * @return the remarkList
	 */
	public List<FlRemark> getRemarkList() {
		if(remarkList==null) {
			remarkList = new ArrayList<FlRemark>();
		}
		return remarkList;
	}

	/**
	 * Sets the remark list.
	 *
	 * @param remarkList the remarkList to set
	 */
	public void setRemarkList(List<FlRemark> remarkList) {
		this.remarkList = remarkList;
	}

	/**
	 * Gets the attachment list.
	 *
	 * @return the attachmentList
	 */
	public List<FlAttachment> getAttachmentList() {
		if(attachmentList==null) {
			attachmentList = new ArrayList<FlAttachment>();
		}
		return attachmentList;
	}

	/**
	 * Sets the attachment list.
	 *
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(List<FlAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	/**
	 * Gets the doc type.
	 *
	 * @return the doc type
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * Sets the doc type.
	 *
	 * @param docType the new doc type
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * Gets the doc no.
	 *
	 * @return the doc no
	 */
	public String getDocNo() {
		return docNo;
	}

	/**
	 * Sets the doc no.
	 *
	 * @param docNo the new doc no
	 */
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	/**
	 * Checks if is doc maintain remarks.
	 *
	 * @return the docMaintainRemarks
	 */
	public boolean isDocMaintainRemarks() {
		return docMaintainRemarks;
	}

	/**
	 * Sets the doc maintain remarks.
	 *
	 * @param docMaintainRemarks the docMaintainRemarks to set
	 */
	public void setDocMaintainRemarks(boolean docMaintainRemarks) {
		this.docMaintainRemarks = docMaintainRemarks;
	}

	/**
	 * Gets the attached file index.
	 *
	 * @return the attachedFileIndex
	 */
	public int getAttachedFileIndex() {
		return attachedFileIndex;
	}

	/**
	 * Sets the attached file index.
	 *
	 * @param attachedFileIndex the attachedFileIndex to set
	 */
	public void setAttachedFileIndex(int attachedFileIndex) {
		this.attachedFileIndex = attachedFileIndex;
	}

	/**
	 * Checks if is hide remarks.
	 *
	 * @return the hideRemarks
	 */
	public boolean isHideRemarks() {
		return hideRemarks;
	}

	/**
	 * Sets the hide remarks.
	 *
	 * @param hideRemarks the hideRemarks to set
	 */
	public void setHideRemarks(boolean hideRemarks) {
		this.hideRemarks = hideRemarks;
	}

	public List<FlAttachment> getDeleteAttachments() {
		if(deleteAttachments==null) {
			deleteAttachments = new ArrayList<FlAttachment>();
		}
		return deleteAttachments;
	}

	public void setDeleteAttachments(List<FlAttachment> deleteAttachments) {
		this.deleteAttachments = deleteAttachments;
	}
	
}
