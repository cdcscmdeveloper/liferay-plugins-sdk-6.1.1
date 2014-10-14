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

import org.apache.log4j.Logger;

/**
 * Managed Bean for Fulfilment
 * 
 * @author KK LIM
 * @since 20/11/2013
 */
@ManagedBean(name = "flMB")
@SessionScoped
public class FulfilmentManagedBean extends BaseManagedBean {
	private static Logger log = Logger.getLogger(FulfilmentManagedBean.class);
	private static final long serialVersionUID = 1L;
	
	private FlAttachment flAttachment = new FlAttachment();
	private List<FlRemark> remarkList;
	private List<FlAttachment> attachmentList;
		
	private String docType;
	private String docNo;
	private boolean docMaintainRemarks = false;
	
	private int attachedFileIndex;
	
	public FulfilmentManagedBean(){
		
	}

	/**
	 * @return the flAttachment
	 */
	public FlAttachment getFlAttachment() {
		return flAttachment;
	}

	/**
	 * @param flAttachment the flAttachment to set
	 */
	public void setFlAttachment(FlAttachment flAttachment) {
		this.flAttachment = flAttachment;
	}

	/**
	 * @return the remarkList
	 */
	public List<FlRemark> getRemarkList() {
		if(remarkList==null) {
			remarkList = new ArrayList<FlRemark>();
		}
		return remarkList;
	}

	/**
	 * @param remarkList the remarkList to set
	 */
	public void setRemarkList(List<FlRemark> remarkList) {
		this.remarkList = remarkList;
	}

	/**
	 * @return the attachmentList
	 */
	public List<FlAttachment> getAttachmentList() {
		if(attachmentList==null) {
			attachmentList = new ArrayList<FlAttachment>();
		}
		return attachmentList;
	}

	/**
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(List<FlAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	/**
	 * @return the docMaintainRemarks
	 */
	public boolean isDocMaintainRemarks() {
		return docMaintainRemarks;
	}

	/**
	 * @param docMaintainRemarks the docMaintainRemarks to set
	 */
	public void setDocMaintainRemarks(boolean docMaintainRemarks) {
		this.docMaintainRemarks = docMaintainRemarks;
	}

	/**
	 * @return the attachedFileIndex
	 */
	public int getAttachedFileIndex() {
		return attachedFileIndex;
	}

	/**
	 * @param attachedFileIndex the attachedFileIndex to set
	 */
	public void setAttachedFileIndex(int attachedFileIndex) {
		this.attachedFileIndex = attachedFileIndex;
	}
	
}
