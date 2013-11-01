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
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgAddress;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.pr.PurchaseRequestManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
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
	
	public PurchaseRequestBackingBean(){
		if(MDC.get(LogKey.USER_ID)==null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
	}
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

	public void createFulfillmentRequest(String action) throws ParseException {
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
			//log.info("flRemark.getRemark() in backing beans::"+ flRemark.getRemark());
			flRemark.setDocId(Long.valueOf(1));
			flRemark.setDocType("tex");
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			reqDTO.setFlRemark(flRemark);
			reqDTO.setFlAttachment(flAttachment);
			reqDTO.setFlFulfilmentRequest(flFulfilmentRequest);
			reqDTO.setContractRequest(false);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			//reqDTO.setProcessId("Order_Fullfillment.CreatePurchaseOrder");
			FulfilmentResDTO resDTO = fulfilmentRequestSO.saveFulfillmentRequest(reqDTO);
			//System.out.println("resDTO.isSuccessful() in backing bean:: "+ resDTO.isSuccessful());
		} catch (Exception e) {
			log.error("Exception>>", e);
		} finally {
			fulfilmentRequestSO = null;
		}
	}
	public void createFulfillment() throws ParseException {
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
			//log.info("flRemark.getRemark() in backing beans::"+ flRemark.getRemark());
			flRemark.setDocId(Long.valueOf(1));
			flRemark.setDocType("tex");
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			reqDTO.setFlRemark(flRemark);
			reqDTO.setFlAttachment(flAttachment);
			reqDTO.setFlFulfilmentRequest(flFulfilmentRequest);
			reqDTO.setContractRequest(false);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome("SUBMIT");
			//reqDTO.setProcessId("Order_Fullfillment.CreatePurchaseOrder");
			FulfilmentResDTO resDTO = fulfilmentRequestSO.saveFulfillmentRequest(reqDTO);
			//System.out.println("resDTO.isSuccessful() in backing bean:: "+ resDTO.isSuccessful());
		} catch (Exception e) {
			log.error("Exception>>", e);
		} finally {
			fulfilmentRequestSO = null;
		}
	}
	
	public void acknowledgeFulfillmentRequest(String action){
		System.out.println("in acknowledgeFulfillmentRequest");
		IFulfilmentRequestSO fulfilmentRequestSO = null;
		IFulfilmentOrderSO fulfilmentOrderSO=null;
		String taskName=this.getManagedBean().getTaskName();
		try {
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flFulfilmentRequest = this.getManagedBean().getFulfilmentRequest();
			flFulfilmentRequest.getIssuedOrgProfile().setOrgProfileId(1L);
			flFulfilmentRequest.getChargeOrgProfile().setOrgProfileId(1L);
			flFulfilmentRequest.getReceivedOrgProfile().setOrgProfileId(1L);
			flFulfilmentRequest.getCreatedOrgProfile().setOrgProfileId(1L);
			flFulfilmentRequest.getSmSupplier().setSupplierId(27L);
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentOrderSO.class);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			//reqDTO.setProcessId("Order_Fullfillment.ReviewRejectedPOAcknowledgement");
			reqDTO.setFlFulfilmentRequest(flFulfilmentRequest);
			if(taskName.equals("Acknowledge the Purchase Request")){
				System.out.println("Ackowledgw::::::");
				fulfilmentOrderSO.acknowledgeFulfillmentOrder(reqDTO);
			}else{
				fulfilmentRequestSO.saveFulfillmentRequest(reqDTO);
			}
			//fulfilmentRequestSO.saveFulfillmentRequest(reqDTO);
		} catch (Exception e) {
			log.error("Exception>>", e);
		} finally {
			fulfilmentRequestSO = null;
		}
	}
	
	public FulfilmentResDTO retrieveFulfilmentRequest(String action) throws ParseException {
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
		OrgProfileResDTO resDTO = new OrgProfileResDTO();
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		
		if(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile() != null){
			if(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId()==null) {
				orgProfileReqDTO.setParentOrgProfileId(1L);
			} else {
				orgProfileReqDTO.setParentOrgProfileId(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId());
			}
			orgProfileReqDTO.setOrgType(OrganizationType.DIVISION.value());
			resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(orgProfileReqDTO);
		}
		
		if(resDTO.getOrgProfileList() != null && !resDTO.getOrgProfileList().isEmpty()){
			retrievePTJList.add(new SelectItem("", "- Select One -"));
			List<PmOrgProfile> listDB = resDTO.getOrgProfileList();
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
	public void submitFlRequest(){
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		String ctx = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		System.out.println("taskid bb: "+getManagedBean().getTaskId());
		reqDTO.setTaskId(getManagedBean().getTaskId());
		
		reqDTO.setCtxToken(ctx);

		reqDTO.setOutcome("SUBMIT");

		BpmResponseDTO res = bpmSO.updateTask(reqDTO);
		System.out.println(res.isSuccessful());
		System.out.println(res.getTaskId());
	}
	
	/**
	 * execute actions when button is clicked
	 * @param action
	 * 
	 */
	public void submitAction(String action) {
		if(BpmOutcomeConst.BPM_SUBMIT.equals(action)){
			try {
				createFulfillmentRequest(action);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (BpmOutcomeConst.BPM_REVISE.equals(action)){
			try {
				System.out.println("in revise action");
				createFulfillmentRequest(action);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (BpmOutcomeConst.BPM_APPROVE.equals(action)){
			System.out.println("we are in app method::: ");
			try {
				System.out.println("in approve action");
				createFulfillmentRequest(action);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (BpmOutcomeConst.BPM_REJECT.equals(action)){
			try {
				System.out.println("in reject action");
				createFulfillmentRequest(action);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (BpmOutcomeConst.BPM_ACKNOWLEDGE.equals(action)){
				acknowledgeFulfillmentRequest(action);
		}else if (BpmOutcomeConst.BPM_SAVE.equals(action)){
			try {
				System.out.println("==createFulfillmentRequest==");
				createFulfillmentRequest(action);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

	}
}
