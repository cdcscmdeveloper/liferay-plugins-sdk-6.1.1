/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean For Contract
 * 
 * @author kieyun
 * @since 13/11/2013	 
 */
@ManagedBean(name="ctManagedBean")
@SessionScoped
public class ContractManagedBean extends BaseManagedBean{

	private static final long serialVersionUID = 1L;
	
	private String fileDirectory;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	
	public List<CtRemark> getRemarkList() {
		if(remarkList==null) {
			remarkList = new ArrayList<CtRemark>();
		}
		return remarkList;
	}
	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}
	public List<CtAttachment> getAttachmentList() {
		if(attachmentList==null) {
			attachmentList = new ArrayList<CtAttachment>();
		}
		return attachmentList;
	}
	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}
	public String getFileDirectory() {
		return fileDirectory;
	}
	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}
	
}
