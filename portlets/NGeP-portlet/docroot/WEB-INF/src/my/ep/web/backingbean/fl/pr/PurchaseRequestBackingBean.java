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

import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.maksud.IMaksudSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseRequestSO;
import my.ep.biz.service.sc.directpurchase.IRequestNoteSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
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
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.podata.FLPurchaseOrderDataType;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmMaksud;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseRequest;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sm.SmSupplier;
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
	private boolean displayItemPnl;
	
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
		if(null == getManagedBean().getFlManulaAddress()){
			getManagedBean().setFlManulaAddress(new FlDeliveryAddress());
			getManagedBean().getFlManulaAddress().setDeliveryAddressId(1L);
			getManagedBean().getFlManulaAddress().setAddressType(new PmAddressType());
			//getManagedBean().getFlManulaAddress().getAddressType().setAddressTypeId(1L);
			getManagedBean().getFlManulaAddress().getAddressType().setPmAddress(new PmAddress());
			//getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setAddressId(1L);
			getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmCountry(new PmCountry());
			getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmCountry().setCountryId(1L);
			getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmState(new PmState());
			getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmState().setStateId(1L);
			getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmDistrict(new PmDistrict());
			getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmDistrict().setDistrictId(1L);
			getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmCity(new PmCity());
			getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmCity().setCityId(1L);
			getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmDivision(new PmDivision());
			getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmDivision().setDivisionId(1L);
			
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
			
			//System will block the submission of the purchase request 
			//Amount of purchase request to be <= RM50K, with the exception of emergency procurement and G2G procurement.
			
			checkPurchaseRequestAmount();
			
			if(isFlFulfilmentApprover()) {
				System.out.println("Aprover");
				// BPM Doing This Business Rule
				//accumulativeProcurementValue();
			}
			
			//FulfilmentResDTO resDTO=null;
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

	public void flDeliveryAddrList() {
		AddressReqDTO reqDTO = new AddressReqDTO();
		// TODO
		reqDTO.setOrgProfileId(4L);
		reqDTO.setAddressType("B");
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject("pm",IAddressSO.class);
		AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
		//System.out.println("Address list "+resDTO.getAddressTypeList().size());
		getManagedBean().setAddressTypesList(resDTO.getAddressTypeList());
	}
	public void flManualDeliveryAddr() {
		getManagedBean().setFlManulaAddress(new FlDeliveryAddress());
		getManagedBean().getFlManulaAddress().setDeliveryAddressId(1L);
		getManagedBean().getFlManulaAddress().setAddressType(new PmAddressType());
		//getManagedBean().getFlManulaAddress().getAddressType().setAddressTypeId(1L);
		getManagedBean().getFlManulaAddress().getAddressType().setPmAddress(new PmAddress());
		//getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setAddressId(1L);
		getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmCountry(new PmCountry());
		getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmCountry().setCountryId(1L);
		getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmState(new PmState());
		getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmState().setStateId(1L);
		getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmDistrict().setDistrictId(1L);
		getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmCity(new PmCity());
		getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmCity().setCityId(1L);
		getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmDivision(new PmDivision());
		getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmDivision().setDivisionId(1L);
	}
	
	public void updateDeliveryAddrDt() {
		if (null == getManagedBean().getSelectedPmDelAddrList()
				|| getManagedBean().getSelectedPmDelAddrList().isEmpty()) {
			return;
		}
		FlDeliveryAddress deliveryAddr = null;
		for (PmAddressType addrType : getManagedBean().getSelectedPmDelAddrList()) {
			//Validate to eliminate duplicate address. 
			boolean isAddressContains = isDeliveryAddressContains(addrType,
					getManagedBean().getFulfilmentRequest()
							.getFlDeliveryAddresses());
			//If selected address is not already added.
			if (!isAddressContains) {
				deliveryAddr = new FlDeliveryAddress();
				deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
				// deliveryAddr.setPmAddress(addrType.getPmAddress());
				deliveryAddr.setAddressType(addrType);
				if (null == getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()) {
					getManagedBean().getFulfilmentRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
				}
				getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().add(deliveryAddr);
			}
		}
		getManagedBean().setAddressTypesList(new ArrayList<PmAddressType>());
		
				
		
	}
	public void updateManualDeliveryAddrDt() {
		if(null== getManagedBean().getFlManulaAddress().getAddressType() ||  getManagedBean().getFlManulaAddress().isEditable()){
			return;
		}
		FlDeliveryAddress deliveryAddr = null;
		PmAddressType manualAddrType=new PmAddressType();
		manualAddrType=getManagedBean().getFlManulaAddress().getAddressType();
		deliveryAddr = new FlDeliveryAddress();
		deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
		deliveryAddr.setAddressType(manualAddrType);
		manualAddrType.setAddressType("C");
		deliveryAddr.setPmAddress(manualAddrType.getPmAddress());
		deliveryAddr.setCheckManualAddress(true);
		getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().add(deliveryAddr);
	}
	
	/**
	 * @param addrType selected delivery address.
	 * @param flDeliveryAddresses list of delivery addresses.
	 * @return returns true if selected delivery address is already added,otherwise false.
	 */
	private boolean isDeliveryAddressContains(PmAddressType addrType,
			List<FlDeliveryAddress> flDeliveryAddresses) {
		boolean isContains=false;
		if(null==flDeliveryAddresses|| flDeliveryAddresses.isEmpty()){
			return false;
		}
		for(FlDeliveryAddress flAddress:flDeliveryAddresses){
			if(addrType.getPmAddress().getAddressId()==flAddress.getAddressType().getPmAddress().getAddressId()){
				isContains=true;
			}
			
		}
		return isContains;
	}
	
	/*public void updateDeliveryAddrDt() {
		FlDeliveryAddress deliveryAddr=null;
		for (PmAddressType addrType : getManagedBean().getSelectedPmDelAddrList()	) {
			if(getManagedBean().getFlAddressMap().get(addrType.getPmAddress().getAddressId()) == null){
				deliveryAddr = new FlDeliveryAddress();
				deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
				//deliveryAddr.setPmAddress(addrType.getPmAddress());
				System.out.println("adresss111 "+addrType.getPmAddress().getAddress1());
				deliveryAddr.setAddressType(addrType);
				//getManagedBean().getFulfilmentRequest().setFlDeliveryAddresses( new ArrayList<FlDeliveryAddress>());
				getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().add(deliveryAddr);
			}else{
				deliveryAddr = getManagedBean().getFlAddressMap().get(addrType.getPmAddress().getAddressId());
			}
			getManagedBean().setAddressTypesList(new ArrayList<PmAddressType>());
			getManagedBean().getFlAddressMap().put(addrType.getPmAddress().getAddressId(), deliveryAddr);
		}
	}*/
	/*public void updateDeliveryAddrDt() {
		for (PmOrgAddress addrType : getManagedBean().getSelectedPmDelAddrList()	) {
			FlDeliveryAddress deliveryAddr = new FlDeliveryAddress();
			System.out.println("del address object::: "+deliveryAddr);
			deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
			deliveryAddr.setPmAddress(addrType.getPmAddress());
			//deliveryAddr.setAddressType(deliveryAddr.getAddressType());
			getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().add(deliveryAddr);
		}
			
		getManagedBean().setAddressTypesList(new ArrayList<PmOrgAddress>());
	}*/
	
	/*public void selectedAddrRow() {
		getManagedBean().setSelectedRowFlg(true);
	}*/

	

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
		try {
			getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().remove(getManagedBean().getSelectedDelAddr());
			getManagedBean().setSelectedAddrRow(null);
			getManagedBean().setSelectedItemsRows(null);
			getManagedBean().getSelectedPmDelAddrList().remove(getManagedBean().getSelectedDelAddr());
			//getManagedBean().getFlAddressMap().remove(getManagedBean().getSelectedDelAddr().getAddressType().getPmAddress().getAddressId());
			if(null != getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()){
				getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().clear();
			}
			getManagedBean().setSelectedDelAddr(null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	public void selectAddrEvent() {
		getManagedBean().setSelectedItemsRows(null);
		//Handle to display item Panel
		setDisplayItemPnl(true);
	}
	public void addPurchsedItems() {
		IPurchaseRequestSO purchaseRequestSO = (IPurchaseRequestSO) BusinessDelegate.getInstance().getServiceObject("ss", IPurchaseRequestSO.class);
		PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();
		reqDTO.setAssignedTo(13L);
		reqDTO.setIncludeDetail(true);
		PurchaseRequestResDTO resDTO = purchaseRequestSO.retrievePurchaseRequests(reqDTO);
		//System.out.println("Item name::: "+resDTO.getListPurchaseRequest().get(0).getRequestNoteDtls().get(0).getScRequestSupplierItem().getRequestNoteItem().getItemName());
		List<ScRequestNoteDtl> noteDtlList=new ArrayList<ScRequestNoteDtl>();
		if(null!=resDTO && !resDTO.getListPurchaseRequest().isEmpty()){
			for(ScPurchaseRequest purchaseRequest:resDTO.getListPurchaseRequest()){
				noteDtlList.addAll(purchaseRequest.getRequestNoteDtls());
			}
		}
		getManagedBean().setRequestNoteWithDtlsList(noteDtlList);
	}

	public void updatePurchasedItemDt() {
		FlFulfilmentItem flItem = null;
		//If no items are selected then simply return;
		if(null==getManagedBean().getSelectedPurchasedItemList()){
			return;
		}
		for (ScRequestNoteDtl purchaseItem : getManagedBean().getSelectedPurchasedItemList()) {
			boolean isItemContains=isPurchaseItemContains(purchaseItem,getManagedBean().getSelectedDelAddr());
			if(!isItemContains){
				flItem = new FlFulfilmentItem();
				flItem.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
				flItem.setScRequestNoteDtl(purchaseItem);
				//flItem.setScRequestNoteDtl(requestNoteDtl);
				//getManagedBean().getFulfilmentRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
				getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().add(flItem);
				
				FlFulfilmentItemAddr flItemAddr = new FlFulfilmentItemAddr();
				flItem.setItemType("P");
				BigDecimal unitPrice = new BigDecimal("200");
				flItemAddr.setUnitPrice(unitPrice);
				//flItemAddr.setOrderedAmt(orderedAmt);
				
				flItemAddr.setFlFulfilmentItem(flItem);
				if(null==getManagedBean().getSelectedDelAddr()){
					getManagedBean().setSelectedDelAddr(new FlDeliveryAddress());
				}
				flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
				PmDo pmDo=new PmDo();
				pmDo.setDoId(2442L);
				flItemAddr.setPmDo(pmDo);
				//If no purchase items are already there, Then create empty list.
				if(null == getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()){
					getManagedBean().getSelectedDelAddr().setFlFulfilmentItemAddr(new ArrayList<FlFulfilmentItemAddr>());
				}
				getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().add(flItemAddr);
				
			}
		}
		
		
	}

	private boolean isPurchaseItemContains(ScRequestNoteDtl purchaseItem,
			FlDeliveryAddress selectedDelAddr) {
		boolean isContains=false;
		//If no delveryAddress is there then return false.
		if(null==selectedDelAddr || 
				null==selectedDelAddr.getFlFulfilmentItemAddr() || selectedDelAddr.getFlFulfilmentItemAddr().isEmpty()){
			return false;
		}
		for(FlFulfilmentItemAddr flItemAddr:selectedDelAddr.getFlFulfilmentItemAddr()){
			if(purchaseItem.getRequestNoteDtlId()==flItemAddr.getFlFulfilmentItem().getScRequestNoteDtl().getRequestNoteDtlId()){
				isContains=true;
			}
		}
		return isContains;
	}

	/*public void updatePurchasedItemDt() {
		//ScRequestNoteDtl requestNoteDtl = getManagedBean().getSelectedPurchasedItemList().get(0);
		FlFulfilmentItem flItem = null;
		for (ScRequestNoteDtl purchaseItem : getManagedBean().getSelectedPurchasedItemList()) {
			if(getManagedBean().getFlItemMap().get(purchaseItem.getRequestNoteDtlId()) == null){
				flItem = new FlFulfilmentItem();
				flItem.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
				flItem.setScRequestNoteDtl(purchaseItem);
				//flItem.setScRequestNoteDtl(requestNoteDtl);
				//getManagedBean().getFulfilmentRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
				getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().add(flItem);
			}else{
				flItem = getManagedBean().getFlItemMap().get(purchaseItem.getRequestNoteDtlId());
			}
			FlFulfilmentItemAddr flItemAddr = new FlFulfilmentItemAddr();
			//flItem.setItemType("P");
			BigDecimal unitPrice = new BigDecimal("200");
			flItemAddr.setUnitPrice(unitPrice);
			//flItemAddr.setOrderedAmt(orderedAmt);
			
			flItemAddr.setFlFulfilmentItem(flItem);
			if(null==getManagedBean().getSelectedDelAddr()){
				getManagedBean().setSelectedDelAddr(new FlDeliveryAddress());
			}
			flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
			if(null == getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()){
				getManagedBean().getSelectedDelAddr().setFlFulfilmentItemAddr(new ArrayList<FlFulfilmentItemAddr>());
			}	
			getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().add(flItemAddr);
			//getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().get(0).setPmDo(new PmDo());
			getManagedBean().getFlItemMap().put(purchaseItem.getRequestNoteDtlId(), flItem);
		}
	}*/

	public void selectedItemRow() {
		getManagedBean().setSelectedRowFlg(true);
	}

	public String removeItemRow() {
		int delItemsListSize = getManagedBean().getSelectedItemsRows().size();
		if (delItemsListSize == getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().size()) {
			getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().clear();
		} else {
			for (FlFulfilmentItemAddr fulfilmentItem : getManagedBean().getSelectedItemsRows()) {
				getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().remove(fulfilmentItem);
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
	public void retrieveOrderAmount(FlFulfilmentItemAddr flItem){
		System.out.println("event "+flItem.getFulfilmentItemAddrId());
		int unitPrice = 0;
		int ordQnty = 0;
		int ordAmoun = 0;
		if(null != flItem.getUnitPrice())
			unitPrice= flItem.getUnitPrice().intValue();
		if(null != flItem.getOrderedQty())
			ordQnty = flItem.getOrderedQty().intValue();
		ordAmoun =((unitPrice) * (ordQnty));
		BigDecimal ordAmount = new BigDecimal("0");
		ordAmount=new BigDecimal(ordAmoun);
		flItem.setOrderedAmt(ordAmount);
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
		return NAV_TO_CREATE_PURCHASE_REQUEST;
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
			//System.out.println("req id from BPM::::::: "+flPurchaseOrd.getPurchaseRequestId());
			//System.out.println("doc type from BPM::::::: "+flPurchaseOrd.getDocType());
			flReqDTO.setFlRequestId(flPurchaseOrd.getPurchaseRequestId());
			flReqDTO.setSelectedApprovers(flPurchaseOrd.getApproverList());
			resDTO = fulfilmentSO.retrieveFulfilmentRequest(flReqDTO);
			flReqDTO.setFlFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			//flReqDTO.setFlWorkflowStatusId(Long.parseLong(String.valueOf(flPurchaseOrd.getPurchaseRequestId())));
			//flReqDTO.setFlWorkflowDocType(flPurchaseOrd.getDocType());
			//resDTO = fulfilmentSO.getStatusDesc(flReqDTO);
			
			//combineSupAddress(resDTO.getFlFulfilmentRequest());
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
			getManagedBean().getFulfilmentRequest().setIssuedOrgProfile(resDTO.getFlFulfilmentRequest().getIssuedOrgProfile());
			getManagedBean().getFulfilmentRequest().setChargeOrgProfile(resDTO.getFlFulfilmentRequest().getChargeOrgProfile());
			getManagedBean().getFulfilmentRequest().setReceivedOrgProfile(resDTO.getFlFulfilmentRequest().getReceivedOrgProfile());
			getManagedBean().getFulfilmentRequest().setPreparedOrgProfile(resDTO.getFlFulfilmentRequest().getIssuedOrgProfile());
			getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(res.getAction());
			getManagedBean().setTaskName(res.getTaskName());
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
	public boolean isFlFulfilmentApprover() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.APPROVER.value());
	}
	
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
		ParameterReqDTO reqDTO=new ParameterReqDTO();
		reqDTO.setParameterCode(parameterCode);
		reqDTO.setParameterType(parameterType);
		ParameterResDTO resDTO=parameterSO.retrieveParameterByTypeAndCode(reqDTO);
		reqDTO.setParameterId(resDTO.getPmParameter().getParameterId());
		if(null!=resDTO.getPmParameter().getPmParameterDescs()){
			desc=resDTO.getPmParameter().getPmParameterDescs().get(0).getCodeName();
		} else{
			System.out.println("Parameters Description is Empty");
		}
		return desc;
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
	    	if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress1()){
	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress1());
	    		address.append(strSpace);
	    	
	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress2() ){
	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress2());
	    		address.append(strSpace);
	    	
	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress3() ){
	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress3());
	    		address.append(strSpace);
	    	
	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress4() ){
	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress4());
	    		address.append(strSpace);
	    		
	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPmDistrict().getDistrictName() ){
	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmDistrict().getDistrictName());
	    		address.append(strSpace);
	    	
	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPmState().getStateName() ){
	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmState().getStateName());
	    		address.append(strSpace);
	    	
	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPmCountry().getCountryName() ){
	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmCountry().getCountryName());
	    		address.append("\n");
	    	}if(null !=fulfilmentRequest.getSupplierBranch().getSmAddress().getPostcode() ){
	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPostcode());
	    	}
	    	if(null != address.toString()){
	    		System.out.println("addresss     "+address.toString());
	    	}
	    	this.getManagedBean().setSupplierAddress(address.toString());
	    }
	 
	//submission of the Accumulative Procurement Value  by item/PTJ/supplier/year value to be <= RM50K
	// with the exception of emergency procurement and G2G procurement
	// currently assuming that non emergency procurement and non G2G procurement
	 
	 public void accumulativeProcurementValue(){
		 int accumulativeProcuValue = 0;
		 ScRequestNoteDtl dtl=new ScRequestNoteDtl();
		 IFulfilmentRequestSO fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
		 for(FlFulfilmentItem item : getManagedBean().getFulfilmentRequest().getFlFulfilmentItems()){
			 //System.out.println("item id "+item.getScRequestNoteDtl().getRequestNoteDtlId());
			 FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			 reqDTO.setSupplierId(27L);
			 reqDTO.setIssuedOrgId(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId());
			 reqDTO.setYear(2014);
			 getManagedBean().getFlReqNoteMap().put(item.getScRequestNoteDtl().getRequestNoteDtlId(),dtl);
			 //System.out.println("map Size::: "+getManagedBean().getFlReqNoteMap().size());
			 //System.out.println("Map Value;;; "+ getManagedBean().getFlReqNoteMap().get(dtl).getRequestNoteDtlId());
			 getManagedBean().getFlReqNoteMap().get(dtl);
			 reqDTO.setItemId(item.getScRequestNoteDtl().getRequestNoteDtlId());
			 FulfilmentResDTO resDTO = fulfilmentRequestSO.accumulativeProcurementValue(reqDTO);
			 accumulativeProcuValue = resDTO.getAccumulativeProcuValue().intValue();
			 	if(accumulativeProcuValue > 50000){
						throw new EpException("Item --- "+ item.getScRequestNoteDtl().getScRequestSupplierItem().getRequestNoteItem().getItemName()+" Accumulative Procu Value  has been exceeded the RM 50K .");
					}
		 }	
	 }
	//submission of the purchase request amount to be <= RM50K
		// with the exception of emergency procurement and G2G procurement
		// currently assuming that non emergency procurement and non G2G procurement
	 public void checkPurchaseRequestAmount(){
		 int validationOrdAmt=0;
		 for(FlDeliveryAddress item : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
				for(FlFulfilmentItemAddr itemAddr : item.getFlFulfilmentItemAddr()){
					System.out.println("order amount:: "+itemAddr.getOrderedAmt());
					validationOrdAmt=validationOrdAmt+itemAddr.getOrderedAmt().intValue();
				}
					System.out.println("validationOrdAmt: "+validationOrdAmt);
		}
		if(validationOrdAmt>50000){
			throw new EpException(" Order Amount  has been exceeded the RM 50K .");
				
				
			}
		 
	 }

	/**
	 * @return the displayItemPnl
	 */
	public boolean isDisplayItemPnl() {
		return displayItemPnl;
	}

	/**
	 * @param displayItemPnl the displayItemPnl to set
	 */
	public void setDisplayItemPnl(boolean displayItemPnl) {
		this.displayItemPnl = displayItemPnl;
	}
	public void editManualAddress(FlDeliveryAddress flDelAddress){
		getManagedBean().setFlManulaAddress(flDelAddress);
		flDelAddress.setEditable(true);
		
	}
	 
}
