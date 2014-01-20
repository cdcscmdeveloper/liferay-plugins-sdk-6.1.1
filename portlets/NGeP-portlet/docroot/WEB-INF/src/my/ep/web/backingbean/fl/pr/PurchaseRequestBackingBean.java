/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.pr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.maksud.IMaksudSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.ss.directpurchase.IPurchaseRequestSO;
import my.ep.biz.service.ss.directpurchase.IRequestNoteSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.maksud.MaksudReqDTO;
import my.ep.dto.pm.common.maksud.MaksudResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.ss.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.ss.directpurchase.PurchaseRequestResDTO;
import my.ep.dto.ss.directpurchase.RequestNoteReqDTO;
import my.ep.dto.ss.directpurchase.RequestNoteResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.podata.FLPurchaseOrderDataType;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.dtl.CtAgencyAddress;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgAddress;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmMaksud;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNote;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNoteDtl;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.pr.PurchaseRequestManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean for Create Purchase Order
 * 
 * @author Giribabu
 * @since 7/10/2013
 */
@ManagedBean(name = "purchaseRequestBackingBean")
@ViewScoped
public class PurchaseRequestBackingBean extends BaseBackingBean<PurchaseRequestManagedBean> {
	private static Logger log = Logger.getLogger(PurchaseRequestBackingBean.class);
	public static final String NAV_TO_CREATE_PURCHASE_REQUEST = "/xhtml/fl/purchaserequest/prBasePage.xhtml";
	private boolean checkFlg = false;
	private  List<SelectItem> issuedPTJList;
	//private  List<SelectItem> receivedPTJList;
	
	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public PurchaseRequestBackingBean() {
	}

	@PostConstruct
	public void init() {
		if (getManagedBean().getFulfilmentRequest() == null) {
			getManagedBean().setFulfilmentRequest(new FlFulfilmentRequest());
			getManagedBean().getFulfilmentRequest().setIssuedOrgProfile(new PmOrgProfile());
			getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().setOrgProfileId(1L);
			getManagedBean().getFulfilmentRequest().setChargeOrgProfile(new PmOrgProfile());
			getManagedBean().getFulfilmentRequest().getChargeOrgProfile().setOrgProfileId(1L);
			getManagedBean().getFulfilmentRequest().setReceivedOrgProfile(new PmOrgProfile());
			getManagedBean().getFulfilmentRequest().getReceivedOrgProfile().setOrgProfileId(1L);
			getManagedBean().getFulfilmentRequest().setSmSupplier(new SmSupplier());
			getManagedBean().getFulfilmentRequest().getSmSupplier().setSupplierId(27L);
			getManagedBean().getFulfilmentRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
			getManagedBean().getFulfilmentRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
			getManagedBean().getFulfilmentRequest().setCtContract(new CtContract());
			getManagedBean().getFulfilmentRequest().getCtContract().setContractType(new PmParameter());
			getManagedBean().getFulfilmentRequest().getCtContract().setFulfillmentType(new PmParameter());
		}
		
		if (getManagedBean().getApproverModel() == null) {
			System.out.println("--renew--");
			List<String> source = getAvailableApproverList();
			//List<String> source = new ArrayList<String>();
			List<String> target = new ArrayList<String>();
			getManagedBean().setApproverModel(new DualListModel<String>(source, target));
			
		}
		if(isFlFulfilmentRequisitioner()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setFulfilmentRequisitioner(facesScopes.getUserSessionBean().getLoginId());
		}
		if(getManagedBean().getUserGroupMap()==null && isFlFulfilmentRequisitioner()) {
			initializeApproverGroup();
		}
	
		//retrieveRequestNoteList();
	}

	public String fulfillmentRequestAction(String action) {
		IFulfilmentRequestSO fulfilmentRequestSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
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
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			reqDTO.setOutcome(action);
			reqDTO.setToken(token);
			reqDTO.setApproverMap(getManagedBean().getApproverMap());
			FulfilmentResDTO resDTO = fulfilmentRequestSO.saveFulfillmentRequest(reqDTO);
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "Fulfilment Request No. : "+ flFulfilmentRequest.getFulfilmentReqId();
				if (flFulfilmentRequest.getDocNo() != null) {
					message = message + ". Doc No. : "+ flFulfilmentRequest.getDocNo();
				}
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.", message));
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
					return "/xhtml/shared/acknowledge.xhtml";
				}
			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception e) {
			log.error("Exception>>", e);
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),e.getMessage()));
		} finally {
			fulfilmentRequestSO = null;
		}
		return null;
	}

	public void updateDeliveryAddrDt() {
		for (PmOrgAddress addrType : getManagedBean().getSelectedPmDelAddrList()	) {
			FlDeliveryAddress deliveryAddr = new FlDeliveryAddress();
			deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
			PmAddressType addressType=new PmAddressType();
			addressType.setAddressType(addrType.getAddressType());
			addressType.setPmAddress(addrType.getPmAddress());
			deliveryAddr.setAddressType(addressType);
			getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().add(deliveryAddr);
		}
	}

	public void flDeliveryAddrList() {
		AddressReqDTO reqDTO = new AddressReqDTO();
		// TODO
		reqDTO.setOrgProfileId(1L);
		reqDTO.setAddressType("D");
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject("pm",IAddressSO.class);
		AddressResDTO resDTO = addressSO.retrieveAddressByOrgProfileAndType(reqDTO);
		// getManagedBean().setAddressTypesList(resDTO.getAddressTypeList());
		System.out.println("Address list "+resDTO.getRetriveaddressList().size());
		getManagedBean().setAddressTypesList(resDTO.getRetriveaddressList());
	}

	public void selectedAddrRow() {
		getManagedBean().setSelectedRowFlg(true);
	}

	/*public String removeAddRow() {
		System.out.println("we are in Remove Function:::: ");
		int delAddressListSize = getManagedBean().getSelectedAddressRows().size();
		if(delAddressListSize==getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().size()){
			getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().clear();
		}else{
			for (FlDeliveryAddress flDeliveryAddress : getManagedBean().getSelectedAddressRows()) {
				getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().remove(flDeliveryAddress);
			}
		}
		return null;
	}*/
	public void removeAddRow(){
		getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().remove(getManagedBean().getSelectedAddrRow());
	}

	/*public void addPurchsedItems() {
		IRequestNoteSO requestNoteSO = (IRequestNoteSO) BusinessDelegate.getInstance().getServiceObject("ss", IRequestNoteSO.class);
		RequestNoteReqDTO reqDTO = new RequestNoteReqDTO();
		reqDTO.setRequestNoteId(1L);
		RequestNoteResDTO resDTO = requestNoteSO.retrievRequestNoteWithDtls(reqDTO);
		resDTO.getRequestNoteList().get(0).getRequestNoteId();
		System.out.println("Purchase item list size::: "+resDTO.getRequestNoteList().size());
		System.out.println("PurchaseList getScRequestList ::: "+resDTO.getRequestNoteList().get(0).getRequestNoteId());
		//System.out.println("Purchase item Req note ID size::: "+resDTO.getRequestNoteList().get(0).getRequestListId());
		getManagedBean().setRequestNoteWithDtlsList(resDTO.getRequestNoteList());
	}*/
	public void addPurchsedItems() {
		
		
		IPurchaseRequestSO purchaseRequestSO = (IPurchaseRequestSO) BusinessDelegate.getInstance().getServiceObject("ss", IPurchaseRequestSO.class);
		PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();
		reqDTO.setAssignedTo(13L);
		PurchaseRequestResDTO resDTO = purchaseRequestSO.retrievePurchaseRequests(reqDTO);
		//System.out.println("@@@@@@@@@@@@@@@@@@@@@@2 "+resDTO.getListPurchaseRequest().size());
		//System.out.println("SIZE 111111111111111    "+resDTO.getListPurchaseRequest().get(0).getRequestNoteDtls().size());
		
		//resDTO.getRequestNoteList().get(0).getRequestNoteId();
		//System.out.println("Purchase item list size::: "+resDTO.getRequestNoteList().size());
		//System.out.println("PurchaseList getScRequestList ::: "+resDTO.getRequestNoteList().get(0).getRequestNoteItems());
		///System.out.println("Purchase item Req note ID size::: "+resDTO.getRequestNoteList().get(0).getScRequestList().getRequestListId());
		//getManagedBean().setRequestNoteWithDtlsList(resDTO.getRequestNoteList());
	}

	public void updatePurchasedItemDt() {
		System.out.println("we are in update method:::: ");
		ScRequestNote requestnote = getManagedBean().getSelectedPurchasedItemList().get(0);
		/*getManagedBean().getFulfilmentRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
		for (ScRequestNoteDtl rnDtl : requestnote.getScRequestNoteDtls()) {
			FlFulfilmentItem flItem = new FlFulfilmentItem();
			flItem.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
			flItem.setScRequestNoteDtl(rnDtl);
			getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().add(flItem);
		}*/
		
		FlFulfilmentItem flItem = null;
		for (ScRequestNoteDtl rnDtl : requestnote.getRequestNoteDtls()) {
			if(getManagedBean().getFlItemMap().get(rnDtl.getRequestNoteDtlId()) == null){
				flItem = new FlFulfilmentItem();
				flItem.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
				flItem.setScRequestNoteDtl(rnDtl);
				getManagedBean().getFulfilmentRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
				getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().add(flItem);
			}else{
				flItem = getManagedBean().getFlItemMap().get(rnDtl.getRequestNoteDtlId());

			}
			FlFulfilmentItemAddr flItemAddr = new FlFulfilmentItemAddr();
			flItemAddr.setFlFulfilmentItem(flItem);
			flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
			getManagedBean().getSelectedDelAddr().setFlFulfilmentItemAddr(new ArrayList<FlFulfilmentItemAddr>());
			getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().add(flItemAddr);

			getManagedBean().getFlItemMap().put(rnDtl.getRequestNoteDtlId(), flItem);
		}
	}

	public void selectedItemRow() {
		getManagedBean().setSelectedRowFlg(true);
	}

	public String removeItemRow() {
		int delItemsListSize=getManagedBean().getSelectedItemsRows().size();
		if(delItemsListSize==getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().size()){
			getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().clear();
		}else{
			for(FlFulfilmentItem fulfilmentItem: getManagedBean().getSelectedItemsRows()){
				getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().remove(fulfilmentItem);
				
			}
		}
		return null;
	}

	private List<String> getAvailableApproverList() {
		List<String> availableApprovers = new ArrayList<String>();
		IUserSO userSO = null;
		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);

			UserReqDTO reqDTO = new UserReqDTO();
			// TODO
			reqDTO.setOrgProfileId(1250l);
			UserResDTO resDTO = userSO.retrieveUserGroupList(reqDTO);
			for (PmUserGroupUser user : resDTO.getUserGroupList().get(0).getPmUserGroupUsers()) {
				getManagedBean().getApproverMap().put(user.getPmUser().getUserName(), user);
				availableApprovers.add(user.getPmUser().getUserName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),"Circular save failed."));
		} finally {
			userSO = null;
		}
		return availableApprovers;
	}

	public List<SelectItem> getPTJ() {
		List<SelectItem> retrievePTJList = new ArrayList<SelectItem>();
		OrgProfileResDTO resDTO = new OrgProfileResDTO();
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();

		if (getManagedBean().getFulfilmentRequest().getIssuedOrgProfile() != null) {
			if (getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId() == null) {
				orgProfileReqDTO.setParentOrgProfileId(1L);
			} else {
				orgProfileReqDTO.setParentOrgProfileId(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId());
			}
			orgProfileReqDTO.setOrgType(OrganizationType.DIVISION.value());
			resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(orgProfileReqDTO);
		}

		if (resDTO.getOrgProfileList() != null	&& !resDTO.getOrgProfileList().isEmpty()) {
			retrievePTJList.add(new SelectItem("", "- Select One -"));
			List<PmOrgProfile> listDB = resDTO.getOrgProfileList();
			for (PmOrgProfile obj : listDB) {
				retrievePTJList.add(new SelectItem(obj.getOrgProfileId(), obj.getOrgName()));
			}
		}
		return retrievePTJList;
	}

	public void retrieveRequestNoteList() {
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		IRequestNoteSO requestNoteSO = (IRequestNoteSO) BusinessDelegate.getInstance().getServiceObject("ss", IRequestNoteSO.class);
		RequestNoteReqDTO reqDTO = new RequestNoteReqDTO();
		reqDTO.setAssignedTo(2L);
		reqDTO.setFulfillmentType("O");
		RequestNoteResDTO resDTO = requestNoteSO.retrievRequestNotes(reqDTO);
		getManagedBean().setRequestNoteList(resDTO.getRequestNoteList());

		FulfilmentBackingBean flBackingBean = new FulfilmentBackingBean();
		flBackingBean.initializeRemarkAndAttachement(flBB.getManagedBean().getRemarkList(), flBB.getManagedBean().getAttachmentList());
	}

	public String navigateToPurchaseRequest() {
		getManagedBean().getSelectedRequestNote();
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		orgProfileReqDTO.setOrgProfileId(4L);// set current login user org profile ID
		OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileByProfileId(orgProfileReqDTO);
		this.getManagedBean().setOutComes(new String[] {"Submit"});
		//System.out.println("Req List ID ::: "+ getManagedBean().getSelectedRequestNote().getScRequestList().getRequestListNo());
		return NAV_TO_CREATE_PURCHASE_REQUEST;
	}

	public List<SelectItem> getDivisionProfileList() {
		List<SelectItem> retrieveDivisionList = new ArrayList<SelectItem>();
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		// TODO
		orgProfileReqDTO.setParentOrgProfileId(1L);
		orgProfileReqDTO.setOrgType(OrganizationType.DEPARTMENT.value());

		OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(orgProfileReqDTO);
		if (resDTO.getOrgProfileList() != null 	&& !resDTO.getOrgProfileList().isEmpty()) {
			retrieveDivisionList.add(new SelectItem("", "- Select One -"));
			List<PmOrgProfile> listDB = resDTO.getOrgProfileList();
			for (PmOrgProfile obj : listDB) {
				retrieveDivisionList.add(new SelectItem(obj.getOrgProfileId(),obj.getOrgName()));
			}
		}

		return retrieveDivisionList;
	}

	/*public void checkPtjFlg() {
		getManagedBean().setCheckPtjFlg(false);
	}*/
	public void checkPtjFlg(){
		getManagedBean().setCheckPtjFlg(false);
		if(getManagedBean().getIssuedJabatanId() != null){
			setCheckFlg(true);
			List<SelectItem> retrievePTJList = new ArrayList<SelectItem>();
			OrgProfileResDTO resDTO = new OrgProfileResDTO();
			IOrgProfileSO orgProfileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			retrievePTJList.add(new SelectItem("", "- Select One -"));
			if(isCheckFlg()){
				System.out.println("-method getIssuedPTJList- getIssuedJabatanId(): "+getManagedBean().getIssuedJabatanId());
				reqDTO.setParentOrgProfileId(getManagedBean().getIssuedJabatanId());
				reqDTO.setOrgType(OrganizationType.DIVISION.value());
				resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
			}
			
			if(resDTO.getOrgProfileList() != null && !resDTO.getOrgProfileList().isEmpty()){
				
				List<PmOrgProfile> listDB = resDTO.getOrgProfileList();
				for(PmOrgProfile obj : listDB){
					retrievePTJList.add(new SelectItem(obj.getOrgProfileId(),obj.getOrgName()));
				}
			}		
	        this.issuedPTJList=retrievePTJList;
		}else{
			setCheckFlg(false);
		}
		
	}
	public void checkRecievedFlg(){
		getManagedBean().setCheckPtjFlg(false);
		if(getManagedBean().getReceivedJabatanId() != null){
			setCheckFlg(true);
			List<SelectItem> retrievePTJList = new ArrayList<SelectItem>();
			OrgProfileResDTO resDTO = new OrgProfileResDTO();
			IOrgProfileSO orgProfileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
			OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
			retrievePTJList.add(new SelectItem("", "- Select One -"));
			if(isCheckFlg()){
				orgProfileReqDTO.setParentOrgProfileId(getManagedBean().getReceivedJabatanId());
				orgProfileReqDTO.setOrgType(OrganizationType.DIVISION.value());
				resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(orgProfileReqDTO);
			}
			
			if(resDTO.getOrgProfileList() != null && !resDTO.getOrgProfileList().isEmpty()){
				
				List<PmOrgProfile> listDB = resDTO.getOrgProfileList();
				for(PmOrgProfile obj : listDB){
					retrievePTJList.add(new SelectItem(obj.getOrgProfileId(),obj.getOrgName()));
				}
			}		
	        //this.receivedPTJList=retrievePTJList;
		}else{
			setCheckFlg(false);
		}
		
	}
	
	

	public List<SelectItem> getPTJList() {
		List<SelectItem> retrievePTJList = new ArrayList<SelectItem>();
		OrgProfileResDTO resDTO = new OrgProfileResDTO();
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();

		if (getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId() != null) {
			orgProfileReqDTO.setParentOrgProfileId(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId());
			orgProfileReqDTO.setOrgType(OrganizationType.DIVISION.value());
			resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(orgProfileReqDTO);
		}

		if (resDTO.getOrgProfileList() != null	&& !resDTO.getOrgProfileList().isEmpty()) {
			retrievePTJList.add(new SelectItem("", "- Select One -"));
			List<PmOrgProfile> listDB = resDTO.getOrgProfileList();
			for (PmOrgProfile obj : listDB) {
				retrievePTJList.add(new SelectItem(obj.getOrgProfileId(), obj.getOrgName()));
			}
		}

		return retrievePTJList;
	}

	public List<SelectItem> getMaksudCodeList() {
		List<SelectItem> maksudList = new ArrayList<SelectItem>();
		MaksudReqDTO maksudDTO = new MaksudReqDTO();
		maksudDTO.setOrgProfileId(1L);

		IMaksudSO maksudSO = (IMaksudSO) BusinessDelegate.getInstance().getServiceObject("pm", IMaksudSO.class);
		MaksudResDTO resDTO = maksudSO.retrieveMaksudCodeList(maksudDTO);
		if (resDTO.getMaksudCodeList() != null 	&& !resDTO.getMaksudCodeList().isEmpty()) {
			maksudList.add(new SelectItem("", "- Select One -"));
			List<PmMaksud> maksudListDB = resDTO.getMaksudCodeList();
			for (PmMaksud obj : maksudListDB) {
				maksudList.add(new SelectItem(obj.getMaksudId(), obj.getMaksudCode()));
			}
		}
		return maksudList;

	}

	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		System.out.println("taskId:" + taskId);
		getLoginUserOrg();
		
		if (taskId != null) {
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();

			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			//System.out.println("Out come is:: "+res.getAction());

			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			FLPurchaseOrderDataType flPurchaseOrd = (FLPurchaseOrderDataType) obj;
			//System.out.println("req id "+flPurchaseOrd.getPurchaseRequestId());
			flReqDTO.setFlRequestId(flPurchaseOrd.getPurchaseRequestId());
			flReqDTO.setSelectedApprovers(flPurchaseOrd.getApproverList());
			resDTO = fulfilmentSO.retrieveFulfilmentRequest(flReqDTO);
			
			//getManagedBean().getFulfilmentRequest().setIssuedOrgProfile(resDTO.getFlFulfilmentRequest().getIssuedOrgProfile());
			System.out.println("org id In Pre render 11111 "+resDTO.getFlFulfilmentRequest().getPreparedOrgProfile().getParentOrgProfile().getParentOrgProfile().getOrgProfileId());
			System.out.println("org id In Pre render 22222 "+resDTO.getFlFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId());
			System.out.println("org id In Pre render 3333 "+resDTO.getFlFulfilmentRequest().getPreparedOrgProfile().getParentOrgProfile().getOrgProfileId());
			flReqDTO.setFlFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			combineSupAddress(resDTO.getFlFulfilmentRequest());
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
			getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(res.getAction());
			getManagedBean().setTaskName(res.getTaskName());
			
			/*List<String> source = new ArrayList<String>();
			List<String> target = new ArrayList<String>();
			getManagedBean().setApproverModel(new DualListModel<String>(source, target));
			getManagedBean().getApproverModel().setTarget(resDTO.getSelectedApproverList());
			getManagedBean().getApproverModel().getSource().removeAll(resDTO.getSelectedApproverList());*/
			
			getManagedBean().getApproverModel().setTarget(resDTO.getSelectedApproverList());
			getManagedBean().getApproverModel().getSource().removeAll(resDTO.getSelectedApproverList());
			if (null != res.getAction() && !"".equals(res.getAction())) {
				getManagedBean().setSubmitButtonStatus(false);
			}
		}
	}
	public List<SelectItem> getGoList(){
		List<SelectItem> goList = new ArrayList<SelectItem>();
		IObjectSO objectSO = (IObjectSO)BusinessDelegate.getInstance().getServiceObject("pm",IObjectSO.class);	
		ObjectResDTO resDTO = objectSO.retrieveGOList();
		goList.add(new SelectItem("", "- Select One -"));
		if(resDTO.getPmGoList() != null && !resDTO.getPmGoList().isEmpty()){
			List<PmGo> goDB = resDTO.getPmGoList();
			for(PmGo obj : goDB){
				goList.add(new SelectItem(obj.getGoId(),obj.getGoCode()));
			}
		}
		return goList;
		
	}
	
	public List<SelectItem> getSoList(){
		List<SelectItem> soList = new ArrayList<SelectItem>();
		IObjectSO objectSO = (IObjectSO)BusinessDelegate.getInstance().getServiceObject("pm",IObjectSO.class);	
		ObjectReqDTO reqDTO = new ObjectReqDTO();
		soList.add(new SelectItem("", "- Select One -"));
		if(getManagedBean().getGoId() != null){

			reqDTO.setGoId(getManagedBean().getGoId());
			ObjectResDTO resDTO = objectSO.retrieveSOListByGOCode(reqDTO);
			if(resDTO.getPmSoList() != null && !resDTO.getPmSoList().isEmpty()){
				List<PmSo> soDB = resDTO.getPmSoList();
				for(PmSo obj : soDB){
					soList.add(new SelectItem(obj.getSoId(),obj.getSoCode()));
				}
			}
		}
		return soList;
	}
	
	public List<SelectItem> getDoList(){
		List<SelectItem> doList = new ArrayList<SelectItem>();
		IObjectSO objectSO = (IObjectSO)BusinessDelegate.getInstance().getServiceObject("pm",IObjectSO.class);	
		ObjectReqDTO reqDTO = new ObjectReqDTO();
		doList.add(new SelectItem("", "- Select One -"));
		if(getManagedBean().getSoId() != null){
			reqDTO.setSoId(getManagedBean().getSoId());
			ObjectResDTO resDTO = objectSO.retrieveDOListBySOCode(reqDTO);
			if(resDTO.getPmDoList() != null && !resDTO.getPmDoList().isEmpty()){
				List<PmDo> doDB = resDTO.getPmDoList();
				for(PmDo obj : doDB){
					doList.add(new SelectItem(obj.getDoId(),obj.getDoCode()));
				}
			}	
		}
		return doList;
	}
	
	public void calculateSubTotal(BigDecimal quantity){
		getManagedBean().getSelectedItemAddr().setSubTotal(quantity);
	}
	
	private void initializeApproverGroup() {
		IUserSO userSO = null;
		try {
			userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
			
			UserReqDTO reqDTO = new UserReqDTO();
			FacesScopes facesScopes = new FacesScopes();
			reqDTO.setUserId(facesScopes.getUserSessionBean().getUserId());
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			
			reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
			resDTO = userSO.retrieveUserGroupList(reqDTO);
			if(getManagedBean().getFulfilmentRequest().getPmUserGroup()==null) {
				PmUserGroup pmUserGroup = new PmUserGroup();
				getManagedBean().getFulfilmentRequest().setPmUserGroup(pmUserGroup);
			}
			if(getManagedBean().getApproverModel()==null) {
				List<String> source = new ArrayList<String>(); 
		        List<String> target = new ArrayList<String>();  
		        getManagedBean().setApproverModel(new DualListModel<String>(source, target));
			}			
			
			getManagedBean().setUserGroupMap(new HashMap<Long, PmUserGroup>());
			int i = 0;
			for(PmUserGroup userGroup : resDTO.getUserGroupList()) {
				getManagedBean().getUserGroupMap().put(userGroup.getUserGroupId(), userGroup);
				if(i==0) {
					getManagedBean().getFulfilmentRequest().getPmUserGroup().setUserGroupId(userGroup.getUserGroupId());
					List<String> source = new ArrayList<String>();
					for(PmUserGroupUser user : userGroup.getPmUserGroupUsers()) {
						source.add(user.getPmUser().getUserName());
					}
					getManagedBean().getApproverModel().setSource(source);
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			userSO = null;
		}
	}
	
	public SelectItem[] getUserGroupDropdown() {
		SelectItem[] items = null;
		getManagedBean().getFulfilmentRequest().setPmUserGroup(new PmUserGroup());
		try {
			items = new SelectItem[getManagedBean().getUserGroupMap().values().size()];
			int i = 0;
			for(PmUserGroup userGroup : getManagedBean().getUserGroupMap().values()) {
				items[i++] = new SelectItem(userGroup.getUserGroupId(), userGroup.getGroupName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	public void userGroupChangeListener() {
		PmUserGroup selectedUserGroup = getManagedBean().getUserGroupMap().get(getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId());
		List<String> source = new ArrayList<String>(); 
        List<String> target = new ArrayList<String>();  
		for(PmUserGroupUser userGroupUser : selectedUserGroup.getPmUserGroupUsers()) {
			source.add(userGroupUser.getPmUser().getUserName());
		}
		getManagedBean().getApproverModel().setSource(source);
		getManagedBean().getApproverModel().setTarget(target);
	}
	public boolean isFlFulfilmentRequisitioner() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.REQUISITIONER.value());
	}
	public List<SelectItem> getIssuedDivisionProfileList(){
		List<SelectItem> retrieveDivisionList = new ArrayList<SelectItem>();
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
		retrieveDivisionList.add(new SelectItem("", "- Select One -"));
		//FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
		
		if(null!=getManagedBean().getFulfilmentRequest().getIssuedOrgProfile()){
			getManagedBean().getFulfilmentRequest().setIssuedOrgProfile(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile());
		}
		else{
			getManagedBean().getFulfilmentRequest().setIssuedOrgProfile(getManagedBean().getPmUserOrg().getPmOrgProfile());
			getManagedBean().setIssuedJabatanId(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getParentOrgProfile().getParentOrgProfile().getOrgProfileId());
		}
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
//		reqDTO.setParentOrgProfileId(getManagedBean().getFlRequest().getIssuedOrgProfile().getParentOrgProfile().getParentOrgProfile().getOrgProfileId());
		System.out.println("-medthod getIssuedDivisionProfileList- getIssuedJabatanId: "+getManagedBean().getIssuedJabatanId());
		reqDTO.setParentOrgProfileId(getManagedBean().getIssuedJabatanId());
		reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
		OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
		
		if(resDTO.getOrgProfileList() != null && !resDTO.getOrgProfileList().isEmpty()){
			
			List<PmOrgProfile> listDB = resDTO.getOrgProfileList();
			for(PmOrgProfile obj : listDB){
				retrieveDivisionList.add(new SelectItem(obj.getOrgProfileId(),obj.getOrgName()));
			}
		}		
		return retrieveDivisionList;
	}
	
	public List<SelectItem> getReceivedDivisionProfileList(){
		List<SelectItem> retrieveDivisionList = new ArrayList<SelectItem>();
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
		retrieveDivisionList.add(new SelectItem("", "- Select One -"));
		
		getManagedBean().getFulfilmentRequest().setReceivedOrgProfile(getManagedBean().getPmUserOrg().getPmOrgProfile());
		getManagedBean().setReceivedJabatanId(getManagedBean().getFulfilmentRequest().getReceivedOrgProfile().getParentOrgProfile().getParentOrgProfile().getOrgProfileId());
		
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
//		reqDTO.setParentOrgProfileId(getManagedBean().getFlRequest().getIssuedOrgProfile().getParentOrgProfile().getParentOrgProfile().getOrgProfileId());
		orgProfileReqDTO.setParentOrgProfileId(getManagedBean().getReceivedJabatanId());
		orgProfileReqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
		OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveOrgProfileListByOrgType(orgProfileReqDTO);
		
		if(!orgProfileResDTO.getOrgProfileList().isEmpty()){
			
			List<PmOrgProfile> listDB = orgProfileResDTO.getOrgProfileList();
			for(PmOrgProfile obj : listDB){
				retrieveDivisionList.add(new SelectItem(obj.getOrgProfileId(),obj.getOrgName()));
			}
		}		
		return retrieveDivisionList;
	}
	/*public List<SelectItem> getReceivedPTJList(){
		

		return retrievePTJList;
	}*/
	public void getLoginUserOrg(){
		PmUser user = getFacesScopes().getUserSessionBean();
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(user.getUserId());
		IUserSO userSO = (IUserSO ) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO .class);
		UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
		PmUserOrg userOrg = resDTO.getUserOrgList().get(0);
		getManagedBean().setPmUserOrg(userOrg);
		System.out.println("user Name:::: "+getManagedBean().getPmUserOrg().getPmUser().getUserName());
	}
	
	/**
	 * @return the issuedPTJList
	 */
	public List<SelectItem> getIssuedPTJList() {
		return issuedPTJList;
	}

	/**
	 * @param issuedPTJList the issuedPTJList to set
	 */
	public void setIssuedPTJList(List<SelectItem> issuedPTJList) {
		this.issuedPTJList = issuedPTJList;
	}
	
	

	/**
	 * @return the checkFlg
	 */
	public boolean isCheckFlg() {
		return checkFlg;
	}
	
	/**
	 * @param checkFlg the checkFlg to set
	 */
	public void setCheckFlg(boolean checkFlg) {
		this.checkFlg = checkFlg;
	}
// get Doc type Description	
	public String getParameterDesc(String parameterType, String parameterCode){
		IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject("pm", IParameterSO.class);
		String desc="";
		//System.out.println("parameterType::: "+parameterType);
		//System.out.println("parameterCode::: "+parameterCode);
		ParameterReqDTO reqDTO=new ParameterReqDTO();
		reqDTO.setParameterCode(parameterCode);
		reqDTO.setParameterType(parameterType);
		ParameterResDTO resDTO=parameterSO.retrieveParameterByTypeAndCode(reqDTO);
		//System.out.println("parameter id:::::: "+resDTO.getPmParameter().getParameterId());
		reqDTO.setParameterId(resDTO.getPmParameter().getParameterId());
		if(null!=resDTO.getPmParameter().getPmParameterDescs()){
			//System.out.println(resDTO.getPmParameter().getPmParameterDescs().size());
			desc=resDTO.getPmParameter().getPmParameterDescs().get(0).getCodeName();
		} else{
			System.out.println("Parameters Description is Empty");
		}
		return desc;
	}
	
	public void updateIssuedJabatan() {
		System.out.println("-updateIssuedJabatan- "
				+ getManagedBean().getFulfilmentRequest().getIssuedOrgProfile()
						.getOrgProfileId());

		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		if (getManagedBean().getFulfilmentRequest().getIssuedOrgProfile()
				.getOrgProfileId() != null) {

			reqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest()
					.getIssuedOrgProfile().getOrgProfileId());
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileByProfileId(reqDTO);
			getManagedBean().getFulfilmentRequest().setIssuedOrgProfile(
					resDTO.getPmOrgProfile());
			getManagedBean().getFulfilmentRequest().setChargeOrgProfile(
					resDTO.getPmOrgProfile());
		} else {
			getManagedBean().getFulfilmentRequest().setIssuedOrgProfile(null);
			getManagedBean().getFulfilmentRequest().setChargeOrgProfile(null);
		}
	}
	public void updateReceivedJabatan() {
		System.out.println("-updateReceivedJabatan- "
				+ getManagedBean().getFulfilmentRequest().getReceivedOrgProfile()
						.getOrgProfileId());

		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		if (getManagedBean().getFulfilmentRequest().getReceivedOrgProfile()
				.getOrgProfileId() != null) {

			reqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest()
					.getReceivedOrgProfile().getOrgProfileId());
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileByProfileId(reqDTO);
			getManagedBean().getFulfilmentRequest().setReceivedOrgProfile(
					resDTO.getPmOrgProfile());
		} else {
			getManagedBean().getFulfilmentRequest().setReceivedOrgProfile(null);
		}
	}
	public List<SelectItem> getReceivedPTJList() {
		List<SelectItem> retrievePTJList = new ArrayList<SelectItem>();
		OrgProfileResDTO resDTO = new OrgProfileResDTO();
		IOrgProfileSO orgProfileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		retrievePTJList.add(new SelectItem("", "- Select One -"));
		System.out.println("profile is for list::: "+getManagedBean().getFulfilmentRequest().getReceivedOrgProfile().getParentOrgProfile().getOrgProfileId());
		orgProfileReqDTO.setParentOrgProfileId(getManagedBean().getFulfilmentRequest().getReceivedOrgProfile().getParentOrgProfile().getOrgProfileId());
		orgProfileReqDTO.setOrgType(OrganizationType.DIVISION.value());
		resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(orgProfileReqDTO);
		if(resDTO.getOrgProfileList() != null && !resDTO.getOrgProfileList().isEmpty()){
			
			List<PmOrgProfile> listDB = resDTO.getOrgProfileList();
			for(PmOrgProfile obj : listDB){
				retrievePTJList.add(new SelectItem(obj.getOrgProfileId(),obj.getOrgName()));
			}
		}		
		
		return retrievePTJList;
		
	}
	 public void combineSupAddress(FlFulfilmentRequest fulfilmentRequest){
	    	
	    	StringBuilder address = new StringBuilder();
	    	String strSpace = " ";
	    	if(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress1() != null){
	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress1());
	    		address.append(strSpace);
	    	
	    	}if(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress2() != null){
	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress2());
	    		address.append(strSpace);
	    	
	    	}if(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress3() != null){
	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress3());
	    		address.append(strSpace);
	    	
	    	}if(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress4() != null){
	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress4());
	    		address.append(strSpace);
	    		
	    	}if(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmDistrict().getDistrictName() != null){
	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmDistrict().getDistrictName());
	    		address.append(strSpace);
	    	
	    	}if(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmState().getStateName() != null){
	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmState().getStateName());
	    		address.append(strSpace);
	    	
	    	}if(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmCountry().getCountryName() != null){
	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmCountry().getCountryName());
	    		address.append("\n");
	    	}if(fulfilmentRequest.getSupplierBranch().getSmAddress().getPostcode() != null){
	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPostcode());
	    	}
	    	if(null != address.toString()){
	    		System.out.println("addresss     "+address.toString());
	    	}
	    	this.getManagedBean().setSupplierAddress(address.toString());
	    }
	


}
