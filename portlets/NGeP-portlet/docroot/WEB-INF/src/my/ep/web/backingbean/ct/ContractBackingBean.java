/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.constant.EpPropertiesConst;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.CtWorkflowStatus;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.ContractManagedBean;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Backing Bean For Contract
 * 
 * @author kieyun
 * @since 13/11/2013	 
 */
@ManagedBean(name="ctBackingBean")
@RequestScoped
public class ContractBackingBean extends BaseBackingBean<ContractManagedBean> {

	private static Logger log = Logger.getLogger(ContractBackingBean.class);
	
	public ContractBackingBean() {
		log.debug("ContractBackingBean.constructor()");
		if (getManagedBean().getFileDirectory() == null) {
			getManagedBean().setFileDirectory(EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/CT/temp");
		}
	}
	
	/**
	 * Constructor used for specifying the directory of file storage.
	 *
	 * @param fileDirectory  the directory for file storage. If the fileDirectory is null, 
	 *                       default to temporary directory
	 */
	public ContractBackingBean(String fileDirectory) {
		log.debug("ContractBackingBean.constructor(fileDirectory)");
		if (getManagedBean().getFileDirectory() == null) {
			getManagedBean().setFileDirectory(EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/CT/temp");
		} else {
			getManagedBean().setFileDirectory(fileDirectory);
		}
	}
	
	/**
	 * @deprecated  As of 23/10/2014, replaced by
	 *              {@link #initializeRemarkAndAttachement(CtWorkflowStatus,List<CtRemark>,List<CtAttachment>)}
	 */
	@Deprecated
	public void initializeRemarkAndAttachement(List<CtRemark> remarkList, List<CtAttachment> attachmentList) {
		FacesScopes facesScopes = new FacesScopes();
		
		if (remarkList.size() == 0 || !compareUserName(remarkList.get(0).getCreatedUser().getLoginId())) {
			CtRemark remark = new CtRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUser(facesScopes.getUserSessionBean());
			getManagedBean().setRemarkList(remarkList);
			getManagedBean().getRemarkList().add(0, remark);
		} else {
			getManagedBean().setRemarkList(remarkList);
		}
		getManagedBean().setAttachmentList(attachmentList);
	}
	
	/**
	 * The method used for initializing the remarks and attachments.
	 * This method will create new remark and attachment object
	 * for every single new process, else will display all
	 * remarks and attachments.
	 * 
	 * @param ctWorkflowStatus  the status of current contract. It is null for every single new process
	 * @param remarkList        the list of remarks to display. If the remarkList is null, 
	 *              		    initiate a new remark
	 * @param attachmentList    the list of attachments to display. If the attachmentList is null, 
	 *              		    initiate a new attachment
	 */
	public void initializeRemarkAndAttachement(CtWorkflowStatus ctWorkflowStatus, 
						List<CtRemark> remarkList, List<CtAttachment> attachmentList) {
		FacesScopes facesScopes = new FacesScopes();
		
		boolean ctWorkflowStatusMatch = false;
		if (ctWorkflowStatus != null) {
			if (remarkList.size() > 0 && remarkList.get(0).getCtWorkflowStatus() != null) {
				if (ctWorkflowStatus.getWorkflowStatusId() == remarkList.get(0).getCtWorkflowStatus().getWorkflowStatusId().longValue()) {
					ctWorkflowStatusMatch = true;
				}
			}
		} else { // both also null
			ctWorkflowStatusMatch = true;
		}
		
		if (remarkList.size() == 0 || !ctWorkflowStatusMatch) {
			CtRemark remark = new CtRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUser(facesScopes.getUserSessionBean());
			getManagedBean().setRemarkList(remarkList);
			getManagedBean().getRemarkList().add(0, remark);
		} else {
			getManagedBean().setRemarkList(remarkList);
		}
		getManagedBean().setAttachmentList(attachmentList);
	}	
	
	/**
	 * Returns a representative icon filename that can then be displayed
	 * on the screen for the attachment.
	 * 
	 * @param  attachment     an object of attached file
	 * @return imageFileName  the icon filename base on the filename extension
	 */
	public String determineIcon(CtAttachment attachment) {
		String imageFileName = "icon_file_default.png";
		String fileExtension = "";

		String fileName = attachment.getFileName();
		
		// Retrieve the file extension base on filename
		if (fileName != null) {
			int mid = fileName.lastIndexOf(".");
			fileExtension = fileName.substring(mid + 1, fileName.length());  
		}

		if ("doc".equals(fileExtension) || "docx".equals(fileExtension)) {
			imageFileName = "icon_doc.png";
		} else if ("pdf".equals(fileExtension)) {
			imageFileName = "icon_pdf.png";
		} else if ("xls".equals(fileExtension) || "xlsx".equals(fileExtension) 
				|| "csv".equals(fileExtension)) {
			imageFileName = "icon_exel.png";
		} else if ("jpg".equals(fileExtension) || "bmp".equals(fileExtension) 
				|| "png".equals(fileExtension) || "gif".equals(fileExtension)) {
			imageFileName = "icon_image.png";
		} else if ("txt".equals(fileExtension)) {
			imageFileName = "icon_txt.png";
		}
		return imageFileName;
	}
	
	/**
	 * The method used to upload file into the attachment list.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void fileUpload(FileUploadEvent event) throws Exception {
		
		FacesScopes facesScopes = new FacesScopes();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		
		UploadedFile item = event.getFile();
		
		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
	    
		CtAttachment attachment = new CtAttachment();
		attachment.setFileName(item.getFileName());
		attachment.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
		attachment.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		attachment.setCreatedUser(facesScopes.getUserSessionBean());
		attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		this.getManagedBean().getAttachmentList().add(attachment);
	}
	
	/**
	 * The method used to download the attached file.
	 * 
	 * @param  attachment   an object of attached file
	 * @throws IOException  if an input or output exception occurred
	 * @see    IOException
	 */
	public void downloadFile(CtAttachment attachment) throws IOException {
		PortletResponse portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		res.setHeader("Content-Disposition", "attachment; filename=\"" + attachment.getFileName() + "\"");
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		
		String filePath = null;
		OutputStream out = res.getOutputStream();
		
		if (attachment.getFilePath() != null) {
			filePath = DocumentRepositoryUtils.getBaseDir()+attachment.getFilePath();
			InputStream input = null;
			try {
				URLConnection uc = new URL(filePath).openConnection();
				input = new BufferedInputStream(uc.getInputStream()); 
			} catch (Exception e) {
				// Cannot open connection, maybe local file
				input = new FileInputStream(filePath);
			}
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = input.read( buffer )) != -1) { 
				out.write(buffer, 0, bytesRead);
				buffer = new byte[4096]; 
			}
			input.close();			
		} else {
			/*out.write(filePath.getFileByte()); */
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	/**
	 * The method used to set the file index of attachment list.
	 * 
	 * @param index  file index of attachment list
	 */
	public void setAttachFileIndex(int index) {
		this.getManagedBean().setAttachFileIndex(index);
	}
	
	/**
	 * The method used to delete attachment from the list.
	 * 
	 * @param index  file index of attachment list
	 */
	public void deleteAttachmentAction(int index) {
		this.getManagedBean().getAttachmentList().remove(index);
	}
}
