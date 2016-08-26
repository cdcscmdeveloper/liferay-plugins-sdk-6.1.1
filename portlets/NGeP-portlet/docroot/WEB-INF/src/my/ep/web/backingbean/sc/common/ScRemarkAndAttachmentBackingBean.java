/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.util.encrypt.BouncyCastleProvider_AES_CBC;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.util.FacesScopes;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
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
	
	private Exception fileUploadException;
	private String selectedComponentId;
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
			fileUploadException = null;
			
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
			fileUploadException = e;
		}
	}
	
	/**
	 * Post Upload Action If Success
	 * 
	 * @param cc
	 * 		the composite component
	 * @param widgetComponent
	 * 		the widget component string
	 * @throws Exception
	 */
	public void postUploadActionIfSuccess(UIComponent cc, String widgetComponent) throws Exception {
		if (fileUploadException == null) {			
			if (StringUtils.isNotBlank(widgetComponent)) {
				RequestContext.getCurrentInstance().execute(
						widgetComponent + "reRender();");
				RequestContext.getCurrentInstance().execute(
						widgetComponent + ".hide();");
			}
		} else {
			if (StringUtils.isNotBlank(widgetComponent)) {
				RequestContext.getCurrentInstance().execute(
						widgetComponent + "FU.buttonBar.find('input[type=file]').get(0).disabled=false;");	
			}
			throw fileUploadException;
		}
	}
	
	/**
	 * set Auto Update Value
	 * 
	 * @param cc
	 * 		the composite component
	 */
	public void setAutoUpdateValue(UIComponent cc) {
		String enableAutoUpdate = (String)FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("enableAutoUpdate");
		
		if (StringUtils.isNotBlank(enableAutoUpdate)) {
			setAttributeValue(cc,
					"autoUpdateValue",
					Boolean.valueOf(enableAutoUpdate));	
		}
	}
	
	private void setAttributeValue(UIComponent cc, String attribute, Object value) {
		ValueExpression attributeVE = cc.getValueExpression(attribute);
		
		if (attributeVE != null) {
			attributeVE.setValue(
					FacesContext.getCurrentInstance().getELContext(), 
					value);	
		}
	}
	
	/**
	 * Delete Multiple Attachment Action
	 */
	public void deleteAttachmentAction() {
		super.deleteRowAction(attachmentList, selectedAttachment);
		
		// reset to null after delete.
		this.attachmentList = null;
		this.selectedAttachment = null;
	}
	
	/**
	 * Execute Delete Single Attachment Action
	 */
	public void executeDeleteAttachmentAction() {
		RequestContext.getCurrentInstance().execute(
				this.selectedComponentId + "deleteSelAttachment()");
	}
	
	/**
	 * Delete Single Attachment Action
	 */
	public void deleteSingleAttachmentAction(BaseEntity selectedAttachment, String deleteFileUploadListener, String widgetComponent) {
		if (StringUtils.isNotBlank(deleteFileUploadListener)) {
			MethodExpression methodExp = FacesScopes
	                .createMethodExpression(FacesContext.getCurrentInstance(),
	                		deleteFileUploadListener, null,
	                        new Class[] { BaseEntity.class });
			
			methodExp.invoke(
					FacesContext.getCurrentInstance().getELContext(), 
					new Object[]{selectedAttachment});
			
			if (StringUtils.isNotBlank(widgetComponent)) {
				RequestContext.getCurrentInstance().execute(
						widgetComponent + "reRender();");
			}
		}
	}
	
	/**
	 * method to handle download events related to encrypted file
	 * 
	 * @param scAttachment
	 * 		the ScAttachment instance
	 * @param encryptionKey
	 * 		the encryption key
	 * @return the encrypted file
	 * @throws IOException
	 */
	public <ATTACHMENT extends ScAttachment> File downloadEncryptedFile(
			ATTACHMENT scAttachment, String encryptionKey) throws IOException {
		if (scAttachment != null
				&& StringUtils.isNotBlank(scAttachment.getFileName())) {

			return downloadEncryptedFile(encryptionKey,
					getActualFilePath(scAttachment),
					scAttachment.getFileName());
		} else {
			throw new EpException(MessageConst.ERROR_760329);
		}
	}

	/**
	 * method to handle download events related to encrypted file
	 * 
	 * @param filePath
	 * @param fileName
	 * @return encrypted file
	 * @throws IOException
	 */
	private File downloadEncryptedFile(String encryptionKey, String filePath, String fileName)
			throws IOException {

		log.info("Inside downloadFile(...)");
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);

		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		// response.setContentType("application/vnd.ms-excel");
		// response.setHeader("Content-Transfer-Encoding", "binary");
		// fire fox issue regarding space
		String finalFileName = fileName.replaceAll("\\s+",QTConstants.REPLACE_CHAR);
		response.setHeader("Content-Disposition", "attachment; filename="
				+ finalFileName);
		response.setContentType("application/octet-stream");
		response.flushBuffer();

		OutputStream out = response.getOutputStream();

		if (filePath != null) {
			InputStream input = null;
			try {
				log.info("Inside filePath(...)");
				URLConnection uc = new URL(filePath).openConnection();
				input = new BufferedInputStream(uc.getInputStream());

			} catch (Exception e) {
				// Cannot open connection, maybe local file
				log.info("Inside filePaths(...)");
				input = new FileInputStream(filePath);
			}
			// byte[] buffer = new byte[4096];
			// int bytesRead;
			// while ((bytesRead = input.read(buffer)) != -1) {
			// out.write(buffer, 0, bytesRead);
			// buffer = new byte[4096];

			BouncyCastleProvider_AES_CBC bc = new BouncyCastleProvider_AES_CBC(encryptionKey);
			bc.InitDecryptCipher();
			bc.CBCDecryptFile(input, out);

			input.close();
		}

		out.flush();
		out.close();
		FacesContext.getCurrentInstance().responseComplete();

		// use to upload back to database
		File file = new File(filePath);
		return file;
	}

	/**
	 * @return the selectedComponentId
	 */
	public String getSelectedComponentId() {
		return selectedComponentId;
	}

	/**
	 * @param selectedComponentId the selectedComponentId to set
	 */
	public void setSelectedComponentId(String selectedComponentId) {
		this.selectedComponentId = selectedComponentId;
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
