/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl;

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

import my.ep.constant.ModuleCodeConst;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.fl.FulfilmentManagedBean;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Backing Bean for Fulfilment
 * 
 * @author KK LIM
 * @since 20/11/2013
 */

@ManagedBean(name = "fulfilmentBackingBean")
@RequestScoped
public class FulfilmentBackingBean extends BaseBackingBean<FulfilmentManagedBean> {
	private static Logger log = Logger.getLogger(FulfilmentBackingBean.class);

	public FulfilmentBackingBean() {

	}
	
	public FulfilmentBackingBean(String docType, String docNo) {
		getManagedBean().setDocType(docType);
		getManagedBean().setDocNo(docNo);
	}	

	public void handleFileUpload(FileUploadEvent event) throws Exception {
		log.debug("FulfilmentBackingBean.handleFileUpload()");
		FacesScopes facesScopes = new FacesScopes();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		
		UploadedFile item = event.getFile();
		
		String filePath = DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.ORDER_FULFILLMENT, getManagedBean().getDocType(), getManagedBean().getDocNo());
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
	    
	    FlAttachment attachment = new FlAttachment();
		attachment.setFileName(item.getFileName());
		attachment.setFilePath(filePath);
		
		attachment.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		attachment.setCreatedUsername(facesScopes.getUserSessionBean().getUserName());
		attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		attachment.setChangedBy(attachment.getCreatedBy());
		attachment.setChangedDate(attachment.getCreatedDate());
		
		getManagedBean().getAttachmentList().add(attachment);		
	}

	public String determineIcon(FlAttachment attachment) {
		String imageFileName = "icon_file_default.png";
		String ext = "";

		String fileName = attachment.getFileName();
		if (fileName != null) {
			int mid = fileName.lastIndexOf(".");
			ext = fileName.substring(mid + 1, fileName.length());
		}

		if ("doc".equals(ext) || "docx".equals(ext)) {
			imageFileName = "icon_doc.png";
		} else if ("pdf".equals(ext)) {
			imageFileName = "icon_pdf.png";
		} else if ("xls".equals(ext) || "xlsx".equals(ext) || "csv".equals(ext)) {
			imageFileName = "icon_exel.png";
		} else if ("jpg".equals(ext) || "bmp".equals(ext) || "png".equals(ext)
				|| "gif".equals(ext)) {
			imageFileName = "icon_image.png";
		} else if ("txt".equals(ext)) {
			imageFileName = "icon_txt.png";
		}
		return imageFileName;
	}

	public void downloadFile(FlAttachment attachment) throws IOException {
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);
		res.setHeader("Content-Disposition", "attachment; filename=\""
				+ attachment.getFileName() + "\"");//
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();
		if (attachment.getFilePath() != null) {
			InputStream input = null;
			try {
				URLConnection uc = new URL(attachment.getFilePath())
						.openConnection();
				input = new BufferedInputStream(uc.getInputStream());
			} catch (Exception e) {
				// Cannot open connection, maybe local file
				input = new FileInputStream(attachment.getFilePath());
			}
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				buffer = new byte[4096];
			}
			input.close();
		} else {
			// out.write(attachment.getFileByte());
		}
		out.flush();
		out.close();
		FacesContext.getCurrentInstance().responseComplete();
	}

	public void confirmDeleteAttachment(int index) {
		
		//set to the managedbean property and show the diloag box.
		this.getManagedBean().setAttachedFileIndex(index);
	}
	public void deleteAttachmentAction() {
		//Cal this method when user clicks on confirm.no need to call once the user rejects.
		//Read the index from managed bean and delete
		this.getManagedBean().getAttachmentList().remove(this.getManagedBean().getAttachedFileIndex());
		RequestContext.getCurrentInstance().execute("delFileDlg.hide()");
	}

	public void initializeRemarkAndAttachement(List<FlRemark> remarkList,
			List<FlAttachment> attachmentList) {
		FacesScopes facesScopes = new FacesScopes();

		if (remarkList.size() == 0
				|| !compareUserName(remarkList.get(0).getUserId())) {
			FlRemark remark = new FlRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUsername(facesScopes.getUserSessionBean()
					.getUserName());
			remark.setUserId(facesScopes.getUserSessionBean().getLoginId());

			getManagedBean().setRemarkList(remarkList);
			getManagedBean().getRemarkList().add(0, remark);
		} else {
			getManagedBean().setRemarkList(remarkList);
		}
		getManagedBean().setAttachmentList(attachmentList);
	}

	public void openAttachmentPage(FlDeliveryAddress fldelAddress) {
		if(fldelAddress.getRemarkList().isEmpty() || !compareUserName(fldelAddress.getRemarkList().get(0).getUserId())) {
			FacesScopes facesScopes = new FacesScopes();
			
			FlRemark flRemark = new FlRemark();
			flRemark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			flRemark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			flRemark.setChangedBy(flRemark.getCreatedBy());
			flRemark.setChangedDate(flRemark.getCreatedDate());
			flRemark.setUserId(facesScopes.getUserSessionBean().getLoginId());
			flRemark.setCreatedUsername(facesScopes.getUserSessionBean().getUserName());
			
			getManagedBean().setRemarkList(fldelAddress.getRemarkList());
			getManagedBean().getRemarkList().add(0, flRemark);
		} else {
			getManagedBean().setRemarkList(fldelAddress.getRemarkList());
		}
		getManagedBean().setAttachmentList(fldelAddress.getAttachmentList());
	}

	public boolean compareAttachmentUserName(String username){
		FacesScopes facesScopes = new FacesScopes();
		if(username.equals(facesScopes.getUserSessionBean().getUserName())) {
			return true;
		} else {
			return false;
		}
	}	
	
}
