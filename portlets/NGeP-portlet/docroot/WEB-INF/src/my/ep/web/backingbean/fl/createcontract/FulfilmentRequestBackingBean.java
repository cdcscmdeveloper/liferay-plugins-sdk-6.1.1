/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.createcontract;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.dto.fl.FulfilmentReqDTO;
import my.ep.biz.dto.fl.FulfilmentResDTO;
import my.ep.biz.service.fl.IFulfilmentRequestSO;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.fl.createcontract.FulfilmentRequestManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Backing Bean for Create Contract Order
 * 
 * @author Giribabu
 * @since 20/9/2013
 */

@ManagedBean(name = "fulfilmentRequestBackingBean")
@RequestScoped
public class FulfilmentRequestBackingBean extends BaseBackingBean<FulfilmentRequestManagedBean>
		implements Cloneable {
	private static Logger log = Logger.getLogger(FulfilmentRequestBackingBean.class);

	public void createFulfillmentRequest() throws ParseException {

		IFulfilmentRequestSO iFulfilmentRequestSO = null;

		try {
			/*
			 * Date d1=new Date(); DateFormat dateFormat = new
			 * SimpleDateFormat("dd/MM/yyyy"); Date date =
			 * dateFormat.parse(dateFormat.format(d1)); long time =
			 * date.getTime();
			 */
			Date currentDatetime = new Date(System.currentTimeMillis());
			java.sql.Timestamp timestamp = new java.sql.Timestamp(
					currentDatetime.getTime());

			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlRemark flRemark = this.getManagedBean().getFlRemark();
			FlAttachment flAttachment = this.getManagedBean().getFlAttachment();
			String fileName = this.getManagedBean().getFlAttachment().getFileName();
			String fileDesc = this.getManagedBean().getFlAttachment().getFileDescription();
			String filePath = this.getManagedBean().getFlAttachment().getFilePath();
			System.out.println("fileName in save Action::: " + fileName);
			flAttachment.setDocType("txt");
			flAttachment.setDocId(Long.valueOf(44));
			flAttachment.setFileName(fileName);
			flAttachment.setFileDescription(fileDesc);
			flAttachment.setFilePath(filePath);
			boolean status = true;
			flAttachment.setRecordStatus(status);
			flAttachment.setCreatedBy(Long.valueOf(11111));
			flAttachment.setChangedBy(Long.valueOf(2222222));
			flAttachment.setCreatedDate(timestamp);
			flAttachment.setChangedDate(timestamp);
			System.out.println("flRemark.getRemark() in backing beans::"+ flRemark.getRemark());
			
			flRemark.setDocId(Long.valueOf(1));
			flRemark.setDocType("tex");
			flRemark.setRecordStatus(true);
			flRemark.setCreatedBy(Long.valueOf(11111));
			flRemark.setChangedBy(Long.valueOf(2222));
			flRemark.setCreatedDate(timestamp);
			flRemark.setChangedDate(timestamp);
			iFulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(IFulfilmentRequestSO.class);
			reqDTO.setFlRemark(flRemark);
			reqDTO.setFlAttachment(flAttachment);
			FulfilmentResDTO resDTO = iFulfilmentRequestSO.createFulfillmentRequest(reqDTO);
		} finally {
			iFulfilmentRequestSO = null;
		}
	}

	public void fileUpload(FileUploadEvent event) {

		UploadedFile file = event.getFile();
		String fileName = file.getFileName();
		byte[] fileBytes = file.getContents();

		System.out.println("Processed uploaded file " + fileName + " of size:"+ fileBytes.length);
		this.getManagedBean().getFlAttachment().setFileName(fileName);
		this.getManagedBean().getFlAttachment().setFileDescription("File uploded for Contract Req Order");
		this.getManagedBean().getFlAttachment().setFilePath("D:\\upload");
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"The file was uploaded successfully.", null);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("D:\\upload\\" + fileName);
			fos.write(fileBytes);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FacesContext.getCurrentInstance().addMessage(null, message);

	}

}
