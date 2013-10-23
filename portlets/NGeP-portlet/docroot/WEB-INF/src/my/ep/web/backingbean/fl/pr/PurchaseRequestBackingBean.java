/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.pr;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.orgunit.IOrgUnitSO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.orgunit.OrgUnitReqDTO;
import my.ep.dto.pm.authorization.orgunit.OrgUnitResDTO;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgAddress;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.fl.pr.PurchaseRequestManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

/**
 * Backing Bean for Create Purchase Order
 * 
 * @author Giribabu
 * @since 7/10/2013
 */
@ManagedBean(name = "purchaseRequestBackingBean")
@RequestScoped
public class PurchaseRequestBackingBean extends BaseBackingBean<PurchaseRequestManagedBean> {
	private static Logger log = Logger.getLogger(PurchaseRequestBackingBean.class);

	@PostConstruct
	public void init() {
		if (getManagedBean().getFulfilmentRequest() == null) {
			getManagedBean().setFulfilmentRequest(new FlFulfilmentRequest());
			getManagedBean().getFulfilmentRequest().setPmDocStatus(new PmStatus());
			getManagedBean().getFulfilmentRequest().setIssuedOrgProfile(new PmOrgProfile());
			getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().setOrgProfileId(1L);
			getManagedBean().getFulfilmentRequest().setChargeOrgProfile(new PmOrgProfile());
			getManagedBean().getFulfilmentRequest().getChargeOrgProfile().setOrgProfileId(1L);
			getManagedBean().getFulfilmentRequest().setReceivedOrgProfile(new PmOrgProfile());
			getManagedBean().getFulfilmentRequest().getReceivedOrgProfile().setOrgProfileId(1L);
			getManagedBean().getFulfilmentRequest().setCreatedOrgProfile(new PmOrgProfile());
			getManagedBean().getFulfilmentRequest().getCreatedOrgProfile().setOrgProfileId(1L);
			getManagedBean().getFulfilmentRequest().setSmSupplier(new SmSupplier());
			getManagedBean().getFulfilmentRequest().getSmSupplier().setSupplierId(27L);
			getManagedBean().getFulfilmentRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
		}
	}

	public void createFulfillmentRequest() throws ParseException {
		IFulfilmentRequestSO fulfilmentRequestSO = null;
		try {
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlRemark flRemark = getManagedBean().getFlRemark();
			FlAttachment flAttachment = this.getManagedBean().getFlAttachment();
			FlFulfilmentRequest flFulfilmentRequest = getManagedBean().getFulfilmentRequest();
			SmSupplier smSupplier = new SmSupplier();
			smSupplier.setSupplierId(27);
			flFulfilmentRequest.setSmSupplier(smSupplier);
			String fileName = this.getManagedBean().getFlAttachment().getFileName();
			String fileDesc = this.getManagedBean().getFlAttachment().getFileDescription();
			String filePath = this.getManagedBean().getFlAttachment().getFilePath();
			flAttachment.setDocType("txt");
			flAttachment.setDocId(Long.valueOf(44));
			flAttachment.setFileName(fileName);
			flAttachment.setFileDescription(fileDesc);
			flAttachment.setFilePath(filePath);
			log.info("flRemark.getRemark() in backing beans::"+ flRemark.getRemark());
			flRemark.setDocId(Long.valueOf(1));
			flRemark.setDocType("tex");
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			reqDTO.setFlRemark(flRemark);
			reqDTO.setFlAttachment(flAttachment);
			reqDTO.setFlFulfilmentRequest(flFulfilmentRequest);
			reqDTO.setContractRequest(false);
			FulfilmentResDTO resDTO = fulfilmentRequestSO.createFulfillmentRequest(reqDTO);
			System.out.println("resDTO.isSuccessful() in backing bean:: "+ resDTO.isSuccessful());
		} catch (Exception e) {
			log.error("Exception>>", e);
		} finally {
			fulfilmentRequestSO = null;
		}
	}

	public FulfilmentResDTO retrieveFulfilmentRequest() throws ParseException {
		IFulfilmentRequestSO fulfilmentRequestSO = null;
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		try {
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();

			FlAttachment flAttachment = this.getManagedBean().getFlAttachment();
			FlFulfilmentRequest flFulfilmentRequest = this.getManagedBean().getFulfilmentRequest();
			flFulfilmentRequest.setFulfilmentReqId(Long.valueOf(76));

			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			reqDTO.setFlFulfilmentRequest(flFulfilmentRequest);
			reqDTO.setFlAttachment(flAttachment);
			resDTO = fulfilmentRequestSO.retrieveFulfilmentRequest(reqDTO);

		} catch (Exception e) {
			log.error("Exception>>", e);
		} finally {
			fulfilmentRequestSO = null;
		}
		return resDTO;
	}

	public void updateFulfilmentRequest() throws ParseException {
		IFulfilmentRequestSO fulfilmentRequestSO = null;
		try {
			log.info("We are in updateFulfilmentRequest:: ");
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlRemark flRemarks = this.getManagedBean().getFlRemark();
			String remarks = flRemarks.getRemark();
			FlRemark flRemark = new FlRemark();
			flRemark.setDocId(Long.valueOf(23));
			flRemark.setDocType("txt");
			//flRemark.setStatusId(Long.valueOf(5));
			flRemark.setRemark(remarks);

			FlFulfilmentRequest flFulfilmentRequest = this.getManagedBean().getFulfilmentRequest();

			String title = flFulfilmentRequest.getTitle();
			String year = flFulfilmentRequest.getFinancialYear();
			System.out.println("Title in update Method:::: " + title);
			System.out.println("year in update Method:::: " + year);

			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			reqDTO.setFlFulfilmentRequest(flFulfilmentRequest);
			reqDTO.setFlRemark(flRemark);
			reqDTO.setContractRequest(false);
			fulfilmentRequestSO.updateFulfilmentRequest(reqDTO);
		} catch (Exception e) {
			log.error("Exception>>", e);
		} finally {
			fulfilmentRequestSO = null;
		}
	}

	public void fileUpload(FileUploadEvent event) {
		UploadedFile file = event.getFile();
		String fileName = file.getFileName();
		byte[] fileBytes = file.getContents();
		log.info("Processed uploaded file " + fileName + " of size:"+ fileBytes.length);
		this.getManagedBean().getFlAttachment().setFileName(fileName);
		this.getManagedBean().getFlAttachment().setFileDescription("File uploded for Contract Req Order");
		this.getManagedBean().getFlAttachment().setFilePath("D:\\upload");
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"The file was uploaded successfully.", null);
		FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("D:\\upload\\" + fileName);
			fos.write(fileBytes);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void addDeliveryAddr() {
		for (PmOrgAddress pmOrgAddr : getManagedBean().getSelectedPmDelAddrList()) {
			FlDeliveryAddress deliveryAddr = new FlDeliveryAddress();
			deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
			deliveryAddr.setOrgAddress(pmOrgAddr);
			deliveryAddr.setDeliveryAddressId(pmOrgAddr.getOrgAddressId());
			getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().add(deliveryAddr);
		}
	}

	public void check(SelectEvent event) {
		System.out.println("in check");
	}

	public void removeAddRow() {
		getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().remove(getManagedBean().getSelectedAddrRow());
	}

	public List<SelectItem> getPTJ() {
		List<SelectItem> retrievePTJList = new ArrayList<SelectItem>();
		OrgUnitResDTO resDTO = new OrgUnitResDTO();
		IOrgUnitSO orgProfileSO = (IOrgUnitSO)BusinessDelegate.getInstance().getServiceObject("pm",IOrgUnitSO.class);
		OrgUnitReqDTO orgProfileReqDTO = new OrgUnitReqDTO();
		
		if(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile() != null){
			if(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId()==null) {
				orgProfileReqDTO.setOrgProfileId(1L);
			} else {
				orgProfileReqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId());
			}
			orgProfileReqDTO.setTypeCodeId(1L);
			resDTO = orgProfileSO.retrieveOrgProfileByParentIdAndType(orgProfileReqDTO);
		}
		
		if(resDTO.getPmorgProfileByParentList() != null && !resDTO.getPmorgProfileByParentList().isEmpty()){
			retrievePTJList.add(new SelectItem("", "- Select One -"));
			List<PmOrgProfile> listDB = resDTO.getPmorgProfileByParentList();
			for(PmOrgProfile obj : listDB){
				retrievePTJList.add(new SelectItem(obj.getOrgProfileId(),obj.getOrgName()));
			}
		}		
		return retrievePTJList;
	}

	public void flDeliveryAddrList() {
		ContractAmendmentReqDTO ctReqDTO = new ContractAmendmentReqDTO();
		ctReqDTO.setOrgProfileId(1L);
		IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAmendmentSO.class);
		ContractAmendmentResDTO resDTO = ctAmendmentSO.getActiveLocation(ctReqDTO);
		getManagedBean().setDelAddrList(resDTO.getOrgAddresses());
	}

}
