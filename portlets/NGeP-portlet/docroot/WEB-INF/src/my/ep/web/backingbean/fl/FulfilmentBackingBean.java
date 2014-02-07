/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.fl.FulfilmentManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;
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
public class FulfilmentBackingBean extends BaseBackingBean<FulfilmentManagedBean>{
	private static Logger log = Logger.getLogger(FulfilmentBackingBean.class);

	public FulfilmentBackingBean(){

	}
	public void handleFileUpload(FileUploadEvent event) {
		FlAttachment attachment = new FlAttachment();

		List<FlAttachment> attachmentList=this.getManagedBean().getAttachmentList();
		
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,	"The file was uploaded successfully.", null);
		FacesContext.getCurrentInstance().addMessage(null, message);
		
		fileUpload(event.getFile(), attachment);
		attachmentList.add(attachment);
		System.out.println("Attach size ::: "+attachmentList.size());
		System.out.println("file name "+attachment.getFileName());
		
	}

	public void fileUpload(UploadedFile file, FlAttachment attachment) {
		
		String fileName = file.getFileName();
		byte[] fileBytes = file.getContents();
		Timestamp date = new Timestamp(System.currentTimeMillis());

		attachment.setCreatedBy(1L);
		attachment.setCreatedDate(date);
		attachment.setChangedBy(1L);

		attachment.setFileName(fileName);
		attachment.setFileDescription("File uploded for Fulfilment Req Order");
		File theDir = new File("\\upload\\");

		// if the directory does not exist, create it
		if (!theDir.exists()) {

			theDir.mkdir();
			System.out.println("creating directory: " + theDir);
		}
		attachment.setFilePath(theDir + "\\" + fileName);
		System.out.println("=======get File path===="
				+ attachment.getFilePath());
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(theDir + "\\" + fileName);
			fos.write(fileBytes);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String determineIcon(FlAttachment attachment) {
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

	public void downloadFile(FlAttachment attachment) throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		res.setHeader( "Content-Disposition", "attachment; filename=\"" + attachment.getFileName() + "\"" );//
		res.setHeader( "Content-Transfer-Encoding", "binary" );
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

	public void initializeRemarkAndAttachement(List<FlRemark> remarkList, List<FlAttachment> attachmentList) {
		FacesScopes facesScopes = new FacesScopes();

		if(remarkList.size()==0 || !compareUserName(remarkList.get(0).getCreatedUsername())) {
			FlRemark remark = new FlRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUsername(facesScopes.getUserSessionBean().getUserName());
			remark.setUserId(facesScopes.getUserSessionBean().getLoginId());

			getManagedBean().setRemarkList(remarkList);
			getManagedBean().getRemarkList().add(0, remark);
		} else {
			getManagedBean().setRemarkList(remarkList);
		}
		getManagedBean().setAttachmentList(attachmentList);
	}

}
