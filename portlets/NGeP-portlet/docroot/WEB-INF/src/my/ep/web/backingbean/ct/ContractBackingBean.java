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
import my.ep.util.EpPropertiesUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.ContractManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Backing Bean For Contract Details
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
		if(getManagedBean().getFileDirectory()==null) {
			getManagedBean().setFileDirectory(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/CT/temp");
		}
	}
	
	public ContractBackingBean(String fileDirectory) {
		log.debug("ContractBackingBean.constructor(fileDirectory)");
		getManagedBean().setFileDirectory(fileDirectory);
	}
	
	public void initializeRemarkAndAttachement(List<CtRemark> remarkList, List<CtAttachment> attachmentList) {
		FacesScopes facesScopes = new FacesScopes();
		
		if(remarkList.size()==0 || !compareUserName(remarkList.get(0).getCreatedUser().getLoginId())) {
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
	
	public String determineIcon(CtAttachment attachment) {
		String imageFileName = "icon_file_default.png";
		String ext = "";

		String fileName = attachment.getFileName();
		if(fileName != null){
			int mid= fileName.lastIndexOf(".");
			ext = fileName.substring(mid+1,fileName.length());  
		}

		if ("doc".equals(ext) || "docx".equals(ext)){
			imageFileName ="icon_doc.png";
		}
		else if ("pdf".equals(ext)){
			imageFileName ="icon_pdf.png";
		}
		else if("xls".equals(ext) || "xlsx".equals(ext) || "csv".equals(ext)){
			imageFileName ="icon_exel.png";
		}
		else if("jpg".equals(ext) || "bmp".equals(ext) || "png".equals(ext) || "gif".equals(ext)){
			imageFileName ="icon_image.png";
		}
		else if("txt".equals(ext)){
			imageFileName ="icon_txt.png";
		}
		return imageFileName;
	}
	
	public void downloadFile(CtAttachment attachment) throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		res.setHeader( "Content-Disposition", "attachment; filename=\"" + attachment.getFileName() + "\"" );//
//		res.setHeader( "Content-Transfer-Encoding", "binary" );
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();   
		if(attachment.getFilePath()!=null) {
			InputStream input = null;
			try {
				URLConnection uc = new URL(attachment.getFilePath()).openConnection();
				input = new BufferedInputStream(uc.getInputStream()); 
			} catch (Exception e) {
				// Cannot open connection, maybe local file
				input = new FileInputStream(attachment.getFilePath());
			}
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = input.read( buffer )) != -1){ 
				out.write( buffer, 0, bytesRead ); buffer = new byte[4096]; 
			}
			input.close();			
		} else {
			//out.write(attachment.getFileByte());
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
	}	
	
	public void deleteAttachmentAction(int index) {
		this.getManagedBean().getAttachmentList().remove(index);
	}
	
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
		attachment.setFilePath(filePath);
		attachment.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		attachment.setCreatedUser(facesScopes.getUserSessionBean());
		attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		this.getManagedBean().getAttachmentList().add(attachment);
	}
	
}
