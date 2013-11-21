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
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.ss.directpurchase.IRequestNoteSO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.ss.directpurchase.RequestNoteReqDTO;
import my.ep.dto.ss.directpurchase.RequestNoteResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCode;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNote;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNoteDtl;
import my.ep.type.pm.OrganizationType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.pr.PurchaseRequestManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
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
	public static final String NAV_TO_CREATE_PURCHASE_REQUEST = "/xhtml/fl/purchaserequest/prBasePage.xhtml";
	
	public PurchaseRequestBackingBean(){
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
			getManagedBean().getFulfilmentRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
			getManagedBean().getFulfilmentRequest().setCtContract(new CtContract());
			getManagedBean().getFulfilmentRequest().getCtContract().setContractTypeCode(new PmCode());
			getManagedBean().getFulfilmentRequest().getCtContract().setFulfillmentTypeCode(new PmCode());
		}
		if(getManagedBean().getApproverModel()==null) {
			System.out.println("--renew--");
			List<String> source = getAvailableApproverList();  
			List<String> target = new ArrayList<String>();  
			getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}
		retrieveRequestNoteList();
	}

	public String fulfillmentRequestAction(String action) throws ParseException {
		IFulfilmentRequestSO fulfilmentRequestSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flFulfilmentRequest = getManagedBean().getFulfilmentRequest();
			SmSupplier smSupplier = new SmSupplier();
			smSupplier.setSupplierId(27);
			flFulfilmentRequest.setSmSupplier(smSupplier);
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			reqDTO.setFlFulfilmentRequest(flFulfilmentRequest);
			reqDTO.setContractRequest(false);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			//reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			//reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			reqDTO.setOutcome(action);
			//System.out.println("temploginId: "+ getManagedBean().getTempLoginId());
			if(null==getManagedBean().getTempLoginId() || "".equals(getManagedBean().getTempLoginId())){
				reqDTO.setTempLogin("roslan");
			}else{
				reqDTO.setTempLogin(getManagedBean().getTempLoginId());
			}
			FulfilmentResDTO resDTO = fulfilmentRequestSO.saveFulfillmentRequest(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "Fulfilment Request No. : "+flFulfilmentRequest.getFulfilmentReqId();
				if(flFulfilmentRequest.getDocNo()!=null) {
					message = message + ". Doc No. : "+flFulfilmentRequest.getDocNo();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.",message));
				if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { // save action will remain in the same page
					renewSesionObject();
					return "/xhtml/shared/acknowledge.xhtml";
				}
			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception e) {
			log.error("Exception>>", e);
		} finally {
			fulfilmentRequestSO = null;
		}
		return null;
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
	public void updateDeliveryAddrDt(){
		for(PmAddressType addrType : getManagedBean().getSelectedPmAddrTypeList()){
			FlDeliveryAddress deliveryAddr = new FlDeliveryAddress();
			deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
			deliveryAddr.setAddressType(addrType);
			deliveryAddr.setDeliveryAddressId(addrType.getAddressTypeId());
			getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().add(deliveryAddr);
		}
	}
	public void flDeliveryAddrList(){
		ContractAmendmentReqDTO ctReqDTO = new ContractAmendmentReqDTO();
		ctReqDTO.setOrgProfileId(4L);
		ctReqDTO.setCtContract(getManagedBean().getCtContract());
		IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractAmendmentSO.class);	
		ContractAmendmentResDTO resDTO = ctAmendmentSO.getActiveLocation(ctReqDTO);
		System.out.println("Size Of List ::: "+resDTO.getAddressTypeList().size());
		getManagedBean().setAddressTypesList(resDTO.getAddressTypeList());
	}
	public void selectedAddrRow(){
		getManagedBean().setSelectedRowFlg(true);
	}

	public void removeAddRow() {
		getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().remove(getManagedBean().getSelectedAddrRow());
	}
	public void addPurchsedItems(){
		IRequestNoteSO requestNoteSO = (IRequestNoteSO) BusinessDelegate.getInstance().getServiceObject("ss",IRequestNoteSO.class);
		RequestNoteReqDTO reqDTO = new RequestNoteReqDTO();
		reqDTO.setRequestNoteId(getManagedBean().getSelectedRequestNote().getRequestNoteId());
		RequestNoteResDTO resDTO = requestNoteSO.retrievRequestNoteWithDtls(reqDTO);
		resDTO.getRequestNoteList().get(0).getRequestNoteId();
		getManagedBean().setRequestNoteWithDtlsList(resDTO.getRequestNoteList());
	}
	public void updatePurchasedItemDt(){
		ScRequestNote requestnote = getManagedBean().getSelectedPurchasedItemList().get(0);
		getManagedBean().getFulfilmentRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
		for(ScRequestNoteDtl rnDtl : requestnote.getScRequestNoteDtls()){
			FlFulfilmentItem flItem = new FlFulfilmentItem();
			flItem.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
			flItem.setScRequestNoteDtl(rnDtl);
			getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().add(flItem);
		}
	}
	public void selectedItemRow(){
		getManagedBean().setSelectedRowFlg(true);
	}

	public void removeItemRow() {
		getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().remove(getManagedBean().getSelectedItemRow());
	}
	
	private List<String> getAvailableApproverList() {
		List<String> availableApprovers = new ArrayList<String>(); 
		IUserSO userSO = null;
		try {
			userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);

			UserReqDTO reqDTO = new UserReqDTO();
			//TODO
			reqDTO.setOrgProfileId(1250l);
			UserResDTO resDTO = userSO.retrieveUserGroupList(reqDTO);
			for(PmUserGroupUser user : resDTO.getUserGroupList().get(0).getPmUserGroupUsers()) {
				availableApprovers.add(user.getPmUser().getUserName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "Circular save failed."));
		} finally {
			userSO = null;
		}
		return availableApprovers;
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
	
	public void retrieveRequestNoteList() {
		IRequestNoteSO requestNoteSO = (IRequestNoteSO) BusinessDelegate.getInstance().getServiceObject("ss",IRequestNoteSO.class);
		RequestNoteReqDTO reqDTO = new RequestNoteReqDTO();
		reqDTO.setAssignedTo(2L);
		reqDTO.setFulfillmentType("O");
		RequestNoteResDTO resDTO = requestNoteSO.retrievRequestNotes(reqDTO);
		getManagedBean().setRequestNoteList(resDTO.getRequestNoteList());
	}
	
	public String navigateToPurchaseRequest() {
		getManagedBean().getSelectedRequestNote();
		System.out.println("Req List ID ::: "+getManagedBean().getSelectedRequestNote().getScRequestList().getRequestListId());
		return NAV_TO_CREATE_PURCHASE_REQUEST;
	}
	
	
}
