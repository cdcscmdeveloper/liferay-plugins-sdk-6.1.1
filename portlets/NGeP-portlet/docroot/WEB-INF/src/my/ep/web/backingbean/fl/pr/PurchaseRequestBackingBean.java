/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.pr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.maksud.IMaksudSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
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
import my.ep.dto.pm.common.maksud.MaksudReqDTO;
import my.ep.dto.pm.common.maksud.MaksudResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.ss.directpurchase.RequestNoteReqDTO;
import my.ep.dto.ss.directpurchase.RequestNoteResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.podata.FLPurchaseOrderDataType;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCode;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmMaksud;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNote;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNoteDtl;
import my.ep.type.pm.OrganizationType;
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
@RequestScoped
public class PurchaseRequestBackingBean extends BaseBackingBean<PurchaseRequestManagedBean> {
	private static Logger log = Logger.getLogger(PurchaseRequestBackingBean.class);
	public static final String NAV_TO_CREATE_PURCHASE_REQUEST = "/xhtml/fl/purchaserequest/prBasePage.xhtml";

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
			getManagedBean().getFulfilmentRequest().getCtContract().setContractType(new PmCode());
			getManagedBean().getFulfilmentRequest().getCtContract().setFulfillmentType(new PmCode());
		}
		if (getManagedBean().getApproverModel() == null) {
			System.out.println("--renew--");
			List<String> source = getAvailableApproverList();
			List<String> target = new ArrayList<String>();
			getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}
		retrieveRequestNoteList();
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
		for (PmAddressType addrType : getManagedBean().getSelectedPmAddrTypeList()) {
			FlDeliveryAddress deliveryAddr = new FlDeliveryAddress();
			deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
			deliveryAddr.setAddressType(addrType);
			// deliveryAddr.setDeliveryAddressId(addrType.getAddressTypeId());
			getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().add(deliveryAddr);

		}
	}

	public void flDeliveryAddrList() {
		ContractAmendmentReqDTO ctReqDTO = new ContractAmendmentReqDTO();
		ctReqDTO.setOrgProfileId(4L);
		ctReqDTO.setCtContract(getManagedBean().getCtContract());
		IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAmendmentSO.class);
		ContractAmendmentResDTO resDTO = ctAmendmentSO.getActiveLocation(ctReqDTO);
		//System.out.println("Size Of List ::: "	+ resDTO.getAddressTypeList().size());
		getManagedBean().setAddressTypesList(resDTO.getAddressTypeList());
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

	public void addPurchsedItems() {
		IRequestNoteSO requestNoteSO = (IRequestNoteSO) BusinessDelegate.getInstance().getServiceObject("ss", IRequestNoteSO.class);
		RequestNoteReqDTO reqDTO = new RequestNoteReqDTO();
		reqDTO.setRequestNoteId(getManagedBean().getSelectedRequestNote().getRequestNoteId());
		RequestNoteResDTO resDTO = requestNoteSO.retrievRequestNoteWithDtls(reqDTO);
		resDTO.getRequestNoteList().get(0).getRequestNoteId();
		System.out.println("Purchase item list size::: "+resDTO.getRequestNoteList().size());
		System.out.println("PurchaseList getScRequestList ::: "+resDTO.getRequestNoteList().get(0).getScRequestList());
		System.out.println("Purchase item Req note ID size::: "+resDTO.getRequestNoteList().get(0).getScRequestList().getRequestListId());
		getManagedBean().setRequestNoteWithDtlsList(resDTO.getRequestNoteList());
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
		for (ScRequestNoteDtl rnDtl : requestnote.getScRequestNoteDtls()) {
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
		if(resDTO.getPmOrgProfile() != null){
			getManagedBean().getFulfilmentRequest().setCreatedOrgProfile(resDTO.getPmOrgProfile());
		}
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

	public void checkPtjFlg() {
		getManagedBean().setCheckPtjFlg(false);
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
		if (taskId != null) {

			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();

			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();

			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			FLPurchaseOrderDataType flPurchaseOrd = (FLPurchaseOrderDataType) obj;

			flReqDTO.setFlRequestId(flPurchaseOrd.getPurchaseRequestId());
			flReqDTO.setSelectedApprovers(flPurchaseOrd.getApproverList());
			resDTO = fulfilmentSO.retrieveFulfilmentRequest(flReqDTO);
			flReqDTO.setFlFulfilmentRequest(resDTO.getFlFulfilmentRequest());

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


}
