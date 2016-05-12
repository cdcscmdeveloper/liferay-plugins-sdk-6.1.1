/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.common;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.BaseEntity;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;

import org.apache.log4j.Logger;

/**
 * Common Remark and Attachment Backing Bean
 * 
 * @author Kenny
 * @since 12.05.2016
 */
@ManagedBean
@ViewScoped
public class ScRemarkAndAttachmentBackingBean extends SourcingBaseBackingBean {
	public static Logger log = EpLogger
			.getLogger(ScRemarkAndAttachmentBackingBean.class);
	
	private BaseEntity selectedAttachment;
	private List<? extends BaseEntity> attachmentList;

	public ScRemarkAndAttachmentBackingBean() {
	}
	
	public void deleteAttachmentAction() {
		super.deleteRowAction(attachmentList, selectedAttachment);
	}

	/**
	 * @return the selectedAttachment
	 */
	public BaseEntity getSelectedAttachment() {
		return selectedAttachment;
	}

	/**
	 * @param selectedAttachment the selectedAttachment to set
	 */
	public void setSelectedAttachment(BaseEntity selectedAttachment) {
		this.selectedAttachment = selectedAttachment;
	}

	/**
	 * @return the attachmentList
	 */
	public List<? extends BaseEntity> getAttachmentList() {
		return attachmentList;
	}

	/**
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(List<? extends BaseEntity> attachmentList) {
		this.attachmentList = attachmentList;
	}
}
