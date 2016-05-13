/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.common;

import java.util.List;

import javax.el.MethodExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.BaseEntity;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.util.FacesScopes;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

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
	
	/**
	 * Handle File Upload Listener to invoke backingbean method.
	 * 
	 * @param event
	 * 		the FileUploadEvent instance
	 */
	public void handleFileUploadListener(FileUploadEvent event) {
		try {
			String fileUploadListenerMethod = (String)event.getComponent().getAttributes().get("fileUploadListenerMethod");
			
			if (StringUtils.isNotBlank(fileUploadListenerMethod)) {
				MethodExpression methodExp = FacesScopes
		                .createMethodExpression(FacesContext.getCurrentInstance(),
		                		fileUploadListenerMethod, null,
		                        new Class[] { FileUploadEvent.class });
				
				methodExp.invoke(
						FacesContext.getCurrentInstance().getELContext(), 
						new Object[]{event});
			}	
		} catch (Exception e) {
			handleException(e);
		}
	}
	
	/**
	 * Delete Attachment Action
	 */
	public void deleteAttachmentAction() {
		super.deleteRowAction(attachmentList, selectedAttachment);
		
		// reset to null after delete.
		this.attachmentList = null;
		this.selectedAttachment = null;
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
