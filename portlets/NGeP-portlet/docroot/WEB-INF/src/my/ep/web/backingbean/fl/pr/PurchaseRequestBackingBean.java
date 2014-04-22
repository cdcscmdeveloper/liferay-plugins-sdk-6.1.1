/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.pr;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.agoffice.IAgOfficeSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.glaccount.IGlAccountSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.programactivity.IProgramActivitySO;
import my.ep.biz.service.pm.common.projectsetia.IProjectSetiaSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.votfund.IVotFundSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseRequestSO;
import my.ep.biz.service.sc.directpurchase.IRequestNoteSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeReqDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.dto.pm.common.glaccount.GlAccountReqDTO;
import my.ep.dto.pm.common.glaccount.GlAccountResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityReqDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityResDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.votfund.VotFundReqDTO;
import my.ep.dto.pm.common.votfund.VotFundResDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.podata.FLPurchaseOrderDataType;
import my.ep.persistence.entity.cm.catalog.CmItem;
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
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmProject;
import my.ep.persistence.entity.pm.common.PmSetia;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseRequest;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
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
import org.primefaces.model.TreeNode;

import com.liferay.portal.util.PortalUtil;
//import my.ep.persistence.entity.pm.common.PmProject;

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
	private boolean checkFlg = false;
	private boolean displayItemPnl;
	private boolean selectedAllChargeItem;
	
	// bulk charge popup
	private Long pmPrjSetia;
	private Long pmPrgActivity;
	
	// display charge for selected item in main form
	
	private Long disPmPrjSetia;
	private Long disPmPrgActivity;
	
	
	private String test;
	
	private String queryItemCode;
	private String queryItemName;
	private String queryDesc;
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
			//getManagedBean().getFulfilmentRequest().setReceivedOrgProfile(new PmOrgProfile());
			//getManagedBean().getFulfilmentRequest().getReceivedOrgProfile().setOrgProfileId(1L);
			getManagedBean().getFulfilmentRequest().setSmSupplier(new SmSupplier());
			getManagedBean().getFulfilmentRequest().getSmSupplier().setSupplierId(1L);
			getManagedBean().getFulfilmentRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
			getManagedBean().getFulfilmentRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
			getManagedBean().getFulfilmentRequest().setCtContract(new CtContract());
			getManagedBean().getFulfilmentRequest().getCtContract().setContractType(new PmParameter());
			getManagedBean().getFulfilmentRequest().getCtContract().setFulfillmentType(new PmParameter());
			
			if(getManagedBean().getRequestNoteWithDtlsList()==null){
				getManagedBean().setRequestNoteWithDtlsList(new ArrayList<ScRequestNoteDtl>());
				
			}
			if(getManagedBean().getShowAllItemsRows()==null){
				getManagedBean().setShowAllItemsRows(new ArrayList<FlFulfilmentItemAddr>());
			}
			
		}
		if(null == getManagedBean().getFlManulaAddress()){
			getManagedBean().setFlManulaAddress(new FlDeliveryAddress());
			getManagedBean().getFlManulaAddress().setDeliveryAddressId(1L);
			//getManagedBean().getFlManulaAddress().setAddressType(new PmAddressType());
			//getManagedBean().getFlManulaAddress().getAddressType().setAddressTypeId(1L);
			getManagedBean().getFlManulaAddress().setPmAddress(new PmAddress());
			//getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setAddressId(1L);
			getManagedBean().getFlManulaAddress().getPmAddress().setPmCountry(new PmCountry());
			getManagedBean().getFlManulaAddress().getPmAddress().getPmCountry().setCountryId(1L);
			getManagedBean().getFlManulaAddress().getPmAddress().setPmState(new PmState());
			getManagedBean().getFlManulaAddress().getPmAddress().getPmState().setStateId(1L);
			getManagedBean().getFlManulaAddress().getPmAddress().setPmDistrict(new PmDistrict());
			getManagedBean().getFlManulaAddress().getPmAddress().getPmDistrict().setDistrictId(1L);
			getManagedBean().getFlManulaAddress().getPmAddress().setPmCity(new PmCity());
			getManagedBean().getFlManulaAddress().getPmAddress().getPmCity().setCityId(1L);
			getManagedBean().getFlManulaAddress().getPmAddress().setPmDivision(new PmDivision());
			getManagedBean().getFlManulaAddress().getPmAddress().getPmDivision().setDivisionId(1L);
			
		}
		if(null == getManagedBean().getFlManulaAddress().getPmAddress()){
			 getManagedBean().getFlManulaAddress().setPmAddress(new PmAddress());
		}
		if(null == getManagedBean().getFlManulaAddress().getPmAddress().getPmCountry()){
			getManagedBean().getFlManulaAddress().getPmAddress().setPmCountry(new PmCountry());
			getManagedBean().getFlManulaAddress().getPmAddress().getPmCountry().setCountryId(1L);
		}
		/*if(null == getManagedBean().getSelectedItemChargeSection()){
			getManagedBean().setSelectedItemChargeSection(new FlFulfilmentItemAddr());
			
		}*/
		if (getManagedBean().getApproverModel() == null) {
			//System.out.println("--renew--");
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
		addPurchsedItems();
		
		//get this lists for manual Address dropdowns
		retrieveCountryList();
		//retrieveStateList();
		//retrieveDivisionList();
		//retrieveCityList(1L);
		//getManagedBean().getChargeLineItemRoot();
		
		//Populate Charge Line Items
		getManagedBean().populateChargeLineData();
		getUserProfiles();
		
		//chargeline dropdown
		getManagedBean().setVotFundList(new ArrayList<PmVotFund>());
		getVotFundList(4L);
		
		//get GL_ACCOUNT details
		getGlAccount(4L);
	}

	public String fulfillmentRequestAction(String action) {
		IFulfilmentRequestSO fulfilmentRequestSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flFulfilmentRequest = getManagedBean().getFulfilmentRequest();
			SmSupplier smSupplier = new SmSupplier();
			smSupplier.setSupplierId(1);
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
			if(isFlFulfilmentRequisitioner()) {
				reqDTO.setRequisitioner(Boolean.TRUE);
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
		//getManagedBean().getFlManulaAddress().setAddressType(new PmAddressType());
		//getManagedBean().getFlManulaAddress().getAddressType().setAddressTypeId(1L);
		getManagedBean().getFlManulaAddress().setPmAddress(new PmAddress());
		//getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setAddressId(1L);
		getManagedBean().getFlManulaAddress().getPmAddress().setPmCountry(new PmCountry());
		getManagedBean().getFlManulaAddress().getPmAddress().getPmCountry().setCountryId(1L);
		getManagedBean().getFlManulaAddress().getPmAddress().setPmState(new PmState());
		getManagedBean().getFlManulaAddress().getPmAddress().getPmState().setStateId(1L);
		getManagedBean().getFlManulaAddress().getPmAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getFlManulaAddress().getPmAddress().getPmDistrict().setDistrictId(1L);
		getManagedBean().getFlManulaAddress().getPmAddress().setPmCity(new PmCity());
		getManagedBean().getFlManulaAddress().getPmAddress().getPmCity().setCityId(1L);
		getManagedBean().getFlManulaAddress().getPmAddress().setPmDivision(new PmDivision());
		getManagedBean().getFlManulaAddress().getPmAddress().getPmDivision().setDivisionId(1L);
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
				 deliveryAddr.setPmAddress(addrType.getPmAddress());
				//System.out.println("9999999999999999999  "+addrType.getAddressTypeId());
				//deliveryAddr.setAddressType(addrType);
				deliveryAddr.setStartDate(new Timestamp(System.currentTimeMillis()));
				deliveryAddr.setEndDate(new Timestamp(System.currentTimeMillis()));
				PmParameter pmUom=new PmParameter();
				pmUom.setParameterId(1L);
				deliveryAddr.setPmUom(pmUom);
				deliveryAddr.setDeliveryTerm(10);
				
				if (null == getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()) {
					getManagedBean().getFulfilmentRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
				}
				getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().add(deliveryAddr);
			}
		}
		getManagedBean().setAddressTypesList(new ArrayList<PmAddressType>());
	}
	public void updateManualDeliveryAddrDt() {
		if(null== getManagedBean().getFlManulaAddress().getPmAddress() ||  getManagedBean().getFlManulaAddress().isEditable()){
			return;
		}
		FlDeliveryAddress deliveryAddr = null;
		PmAddress manualAddr=new PmAddress();
		manualAddr=getManagedBean().getFlManulaAddress().getPmAddress();
		deliveryAddr = new FlDeliveryAddress();
		deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
		deliveryAddr.setPmAddress(manualAddr);
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
			if(addrType.getPmAddress().getAddressId()==flAddress.getPmAddress().getAddressId()){
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
			//getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().clear();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	public void selectAddrEvent() {
		clearItemsForSelectedAddress();
		populateItemsForSelectedAddress(getManagedBean().getShowAllItemsRows());
	}

	private void populateItemsForSelectedAddress(
			List<FlFulfilmentItemAddr> showAllItemsRows) {
		List<FlFulfilmentItemAddr> itemsList=new ArrayList<FlFulfilmentItemAddr>();
		if (null != getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()) {
			for (FlFulfilmentItemAddr newItem : showAllItemsRows) {
				boolean existed=false;
				for (FlFulfilmentItemAddr selectedItem : getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()) {
					if (selectedItem.getScRequestItem().getRequestNoteItemId().equals(newItem.getScRequestItem().getRequestNoteItemId())) {
						existed=true;
						itemsList.add(selectedItem);
						break;
					}
				}
				if(!existed){
					itemsList.add(newItem);
				}
			}
			itemsList.size();
			getManagedBean().setShowAllItemsRows(itemsList);
		}

	}

	public void clearItemsForSelectedAddress(){
		addPurchsedItems();
		
	}
	
	//flFulfilmentItemAddr.setScRequestItem
	
	// PurchaseRequestReqDTO purReqDTO = new PurchaseRequestReqDTO();
	 // purReqDTO.setPurchaseRequestId(1L);
	 // purReqDTO.setIncludeDetail(true);
	 // PurchaseRequestResDTO purResDTO = purchaseRequestSO.getPurchaseRequest(purReqDTO);
	
	
	public void addPurchsedItems() {
		FlFulfilmentItem flItem = null;
		IPurchaseRequestSO purchaseRequestSO = (IPurchaseRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseRequestSO.class);
		PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();
		reqDTO.setAssignedTo(1L);
		reqDTO.setIncludeDetail(true);
		 PurchaseRequestResDTO purResDTO = purchaseRequestSO.getPurchaseRequest(reqDTO);
		//PurchaseRequestResDTO resDTO = purchaseRequestSO.retrievePurchaseRequests(reqDTO);
		 purResDTO.getPurchaseRequest().getRequestNoteDtls().size();
		System.out.println("Item name::: "+purResDTO.getPurchaseRequest().getRequestNoteDtls().get(0).getScRequestSupplierItem().getRequestNoteItem().getItemName());
		List<ScRequestNoteDtl> noteDtlList=new ArrayList<ScRequestNoteDtl>();
		noteDtlList.addAll( purResDTO.getPurchaseRequest().getRequestNoteDtls());
		
		/*if(null!=purResDTO && !purResDTO.getPurchaseRequest().getRequestNoteDtls().isEmpty()){
			for(ScRequestNoteDtl purchaseRequest:purResDTO.getPurchaseRequest().getRequestNoteDtls()){
				//purchaseRequest.getRequestNoteDtls().
				noteDtlList.addAll(purchaseRequest);
			}
		}*/
		noteDtlList.size();
		getManagedBean().setRequestNoteWithDtlsList(noteDtlList);
		List <FlFulfilmentItemAddr> allItemsList=new ArrayList<FlFulfilmentItemAddr>();
		for (ScRequestNoteDtl purchaseItem : getManagedBean().getRequestNoteWithDtlsList()) {
			
			BigDecimal unitPrice = new BigDecimal("200");
			flItem = new FlFulfilmentItem();
			PmParameter pmParameter = new PmParameter();
		    pmParameter.setParameterId(232L);
			CmItem cmItem = new CmItem();
			cmItem.setItemId(purchaseItem.getScRequestSupplierItem().getRequestNoteItem().getCmItem().getItemId());
			//PmVotFund pmVotFund=new PmVotFund();
		    //pmVotFund.setVotFundId(1L);
		   // wqwqwqwqw
			   flItem.setItemType(pmParameter);
			   flItem.setCmItem(cmItem);
			   //flItem.setPmVotFund(pmVotFund);
			   flItem.setUnitPrice(unitPrice);
			   flItem.setChargeLineSeq(1);
			   flItem.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
				//flItem.setScRequestNoteDtl(purchaseItem);
				//flItem.setScRequestNoteDtl(requestNoteDtl);
				//getManagedBean().getFulfilmentRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
				
				
				
				FlFulfilmentItemAddr flItemAddr = new FlFulfilmentItemAddr();
				//flItem.setItemType("P");
				BigDecimal unitPrice1 = new BigDecimal("200");
				BigDecimal unitPrice2 = new BigDecimal("300");
				if(purchaseItem.getScRequestSupplierItem().getRequestNoteItem().getRequestNoteItemId()==50){
					flItemAddr.setUnitPrice(unitPrice1);
					flItem.setUnitPrice(unitPrice1);
				}else{
					flItemAddr.setUnitPrice(unitPrice2);
					flItem.setUnitPrice(unitPrice2);
				}
				//getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().add(flItem);
				
				//flItemAddr.setItemType("P");
				//flItemAddr.setOrderedAmt(orderedAmt);
				flItemAddr.setScRequestItem(purchaseItem.getScRequestSupplierItem().getRequestNoteItem());
				//PmDo pmDo=new PmDo();
				//pmDo.setDoId(2442L);
				//flItemAddr.setPmDo(pmDo);
				flItemAddr.setCmItem(cmItem);
				allItemsList.add(flItemAddr);
				
		}
		getManagedBean().setShowAllItemsRows(allItemsList);
		
		
		//getManagedBean().setShowAllItemsRows(noteDtlList);
		
		//List<ScRequestNoteDtl> noteDtlList=getScRequestNoteDLs();
		//noteDtlList.size();
		//getManagedBean().setRequestNoteWithDtlsList(noteDtlList);
		/*List <FlFulfilmentItemAddr> allItemsList=new ArrayList<FlFulfilmentItemAddr>();
		for (ScRequestNoteDtl purchaseItem : getManagedBean().getRequestNoteWithDtlsList()) {
			BigDecimal unitPrice = new BigDecimal("200");
			flItem = new FlFulfilmentItem();
			PmParameter pmParameter = new PmParameter();
		    pmParameter.setParameterId(232L);
			CmItem cmItem = new CmItem();
			cmItem.setItemId(1L);
			PmVotFund pmVotFund=new PmVotFund();
		    pmVotFund.setVotFundId(1L);
			   flItem.setItemType(pmParameter);
			   //flItem.setCmItem(cmItem);
			   flItem.setPmVotFund(pmVotFund);
			   flItem.setUnitPrice(unitPrice);
			   flItem.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
				//flItem.setScRequestNoteDtl(purchaseItem);
				//flItem.setScRequestNoteDtl(requestNoteDtl);
				//getManagedBean().getFulfilmentRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
				
				
				
				FlFulfilmentItemAddr flItemAddr = new FlFulfilmentItemAddr();
				//flItem.setItemType("P");
				BigDecimal unitPrice1 = new BigDecimal("200");
				BigDecimal unitPrice2 = new BigDecimal("300");
				if(purchaseItem.getScRequestSupplierItem().getRequestNoteItem().getRequestNoteItemId()==50){
					flItemAddr.setUnitPrice(unitPrice1);
					flItem.setUnitPrice(unitPrice1);
				}else{
					flItemAddr.setUnitPrice(unitPrice2);
					flItem.setUnitPrice(unitPrice2);
				}
				getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().add(flItem);
				
				//flItemAddr.setItemType("P");
				//flItemAddr.setOrderedAmt(orderedAmt);
				flItemAddr.setScRequestItem(purchaseItem.getScRequestSupplierItem().getRequestNoteItem());
				PmDo pmDo=new PmDo();
				pmDo.setDoId(2442L);
				flItemAddr.setPmDo(pmDo);
				allItemsList.add(flItemAddr);
				
		}
		//getManagedBean().setShowAllItemsRows(allItemsList);
		*/
	}
	private List<ScRequestNoteDtl> getScRequestNoteDLs() {
	//	resDTO.getListPurchaseRequest().get(0).getRequestNoteDtls().get(0).getScRequestSupplierItem().getRequestNoteItem().getItemName()
		List<ScRequestNoteDtl> scList=new ArrayList<>();
		ScRequestNoteDtl aNote=new ScRequestNoteDtl();
		aNote.setScRequestSupplierItem(new ScRequestSupplierItem());
		aNote.getScRequestSupplierItem().setRequestNoteItem(new ScRequestItem());
		//aNote.getScRequestSupplierItem().getRequestNoteItem().setAmount(new Double(100));
		aNote.getScRequestSupplierItem().getRequestNoteItem().setRequestNoteItemId(1L);
		aNote.getScRequestSupplierItem().getRequestNoteItem().setItemType("P");
		aNote.getScRequestSupplierItem().getRequestNoteItem().setItemName("Blue Pen");
		scList.add(aNote);
		ScRequestNoteDtl bNote=new ScRequestNoteDtl();
		bNote.setScRequestSupplierItem(new ScRequestSupplierItem());
		bNote.getScRequestSupplierItem().setRequestNoteItem(new ScRequestItem());
		//bNote.getScRequestSupplierItem().getRequestNoteItem().setAmount(new Double(100));
		bNote.getScRequestSupplierItem().getRequestNoteItem().setRequestNoteItemId(33L);
		bNote.getScRequestSupplierItem().getRequestNoteItem().setItemType("S");
		bNote.getScRequestSupplierItem().getRequestNoteItem().setItemName("Apsara Pencil");
		scList.add(bNote);
		ScRequestNoteDtl cNote=new ScRequestNoteDtl();
		cNote.setScRequestSupplierItem(new ScRequestSupplierItem());
		cNote.getScRequestSupplierItem().setRequestNoteItem(new ScRequestItem());
		//bNote.getScRequestSupplierItem().getRequestNoteItem().setAmount(new Double(100));
		cNote.getScRequestSupplierItem().getRequestNoteItem().setRequestNoteItemId(34L);
		cNote.getScRequestSupplierItem().getRequestNoteItem().setItemType("S");
		cNote.getScRequestSupplierItem().getRequestNoteItem().setItemName("Red Pen");
		scList.add(cNote);
		ScRequestNoteDtl dNote=new ScRequestNoteDtl();
		dNote.setScRequestSupplierItem(new ScRequestSupplierItem());
		dNote.getScRequestSupplierItem().setRequestNoteItem(new ScRequestItem());
		//bNote.getScRequestSupplierItem().getRequestNoteItem().setAmount(new Double(100));
		dNote.getScRequestSupplierItem().getRequestNoteItem().setRequestNoteItemId(50L);
		dNote.getScRequestSupplierItem().getRequestNoteItem().setItemType("S");
		dNote.getScRequestSupplierItem().getRequestNoteItem().setItemName("HB Pencil");
		scList.add(dNote);
		
		
		
		return scList;
	}

	public void addPurchsedItems1() {
		IPurchaseRequestSO purchaseRequestSO = (IPurchaseRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseRequestSO.class);
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
		//noteDtlList.get(0).getScRequestSupplierItem().getSupplierItemPrices().get(0).getItemPrice();
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
				//flItem.setScRequestNoteDtl(purchaseItem);
				//flItem.setScRequestNoteDtl(requestNoteDtl);
				//getManagedBean().getFulfilmentRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
				getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().add(flItem);
				
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
			if(purchaseItem.getRequestNoteDtlId()==flItemAddr.getScRequestItem().getRequestNoteItemId()){
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
		//System.out.println("event "+flItem.getFulfilmentItemAddrId());
		getManagedBean().getSelectedDelAddr();
		int unitPrice = 0;
		int ordQnty = 0;
		int ordAmoun = 0;
		BigDecimal ordAmount = new BigDecimal("0");
		if(null != flItem.getUnitPrice())
			unitPrice= flItem.getUnitPrice().intValue();
		if(null == flItem.getOrderedQty()){
			flItem.setOrderedAmt(ordAmount);
		}else{
			ordQnty = flItem.getOrderedQty().intValue();
			ordAmoun =((unitPrice) * (ordQnty));
			ordAmount=new BigDecimal(ordAmoun);
			flItem.setOrderedAmt(ordAmount);
			PmParameter pmParameter = new PmParameter();
		    pmParameter.setParameterId(232L);
			CmItem cmItem = new CmItem();
			cmItem.setItemId(flItem.getCmItem().getItemId());
			//.getScRequestSupplierItem().getRequestNoteItem().getCmItem().getItemId()
			//PmVotFund pmVotFund=new PmVotFund();
		   // pmVotFund.setVotFundId(1L);
			FlFulfilmentItem fulfilmentItem =new FlFulfilmentItem();
			
		    flItem.setItemType(pmParameter);
		    flItem.setCmItem(cmItem);
		    //flItem.setPmVotFund(pmVotFund);
		    
		   // preparing FlFulfilmentItem for SAVE
			fulfilmentItem.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
			fulfilmentItem.setUnitPrice(flItem.getUnitPrice());
			fulfilmentItem.setChargeLineSeq(1);
			fulfilmentItem.setItemType(pmParameter);
			fulfilmentItem.setCmItem(cmItem);
			//fulfilmentItem.setPmVotFund(pmVotFund);
			//flItem.setFlFulfilmentItem(fulfilmentItem);
			
			
			//getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().add(fulfilmentItem);
			//adsdsdsd
			//getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().add(deliveryAddr);
		    if(null == getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()){
				getManagedBean().getSelectedDelAddr().setFlFulfilmentItemAddr(new ArrayList<FlFulfilmentItemAddr>());
			}
		    flItem.setFlFulfilmentItem(fulfilmentItem);
		    flItem.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
			getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().add(flItem);
			//(flItem);
			//getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().add(getManagedBean().getSelectedDelAddr());
			//getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().add(fulfilmentItem);
			
		}
		// add  or remove items to Selected delivery address
		itemsToSelectedAddress(flItem);
	}
	
	public void itemsToSelectedAddress(FlFulfilmentItemAddr flItem){
		if(null == getManagedBean().getSelectedDelAddr()){
			throw new EpException(" Select Deliver Address .");
		}else{
			if(null== flItem.getOrderedQty() || flItem.getOrderedQty().intValue()==0){
				int index=0;
				if(null!=getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()){
					for(FlFulfilmentItemAddr item:getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()){
						if(flItem.getScRequestItem().getRequestNoteItemId().equals(item.getScRequestItem().getRequestNoteItemId())){
							getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().remove(index);
							break;
						}
						index++;
					}	
				}
			}else{
				if(null == getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()){
					getManagedBean().getSelectedDelAddr().setFlFulfilmentItemAddr(new ArrayList<FlFulfilmentItemAddr>());
				}
				boolean itemExisted=false;
				for (FlFulfilmentItemAddr selectedItem : getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()) {
					if (selectedItem.getScRequestItem().getRequestNoteItemId().equals(flItem.getScRequestItem().getRequestNoteItemId())) {
						selectedItem.setOrderedAmt(flItem.getOrderedAmt());
						selectedItem.setOrderedQty(flItem.getOrderedQty());
						itemExisted=true;
						break;
						
					}
				}
				
				if(!itemExisted){
					getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().add(flItem);
				}
			}
		}
		getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().size();
		
	}

	public void retrieveRequestNoteList() {
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		
		IRequestNoteSO requestNoteSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IRequestNoteSO.class);
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
	/*
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
	}*/

	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		//System.out.println("taskId:" + taskId);
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
			
			flReqDTO.setFlRequestId(flPurchaseOrd.getOrderDetails().getRequestId());
			//flPurchaseOrd.getOrderDetails().getApproverList().add(approver);
			//flReqDTO.setSelectedApprovers(flPurchaseOrd.getApproverList());
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
			//getManagedBean().getFulfilmentRequest().setReceivedOrgProfile(resDTO.getFlFulfilmentRequest().getReceivedOrgProfile());
			//getManagedBean().getFulfilmentRequest().setPreparedOrgProfile(resDTO.getFlFulfilmentRequest().getIssuedOrgProfile());
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
		//System.out.println("user Name:::: "+getManagedBean().getPmUserOrg().getPmUser().getUserName());
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
			//System.out.println("Parameters Description is Empty");
		}
		return desc;
	}
	
	public List<SelectItem> getReceivedPTJList() {
		List<SelectItem> retrievePTJList = new ArrayList<SelectItem>();
		OrgProfileResDTO resDTO = new OrgProfileResDTO();
		IOrgProfileSO orgProfileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		retrievePTJList.add(new SelectItem("", "- Select One -"));
		//System.out.println("profile is for list::: "+getManagedBean().getFulfilmentRequest().getReceivedOrgProfile().getParentOrgProfile().getOrgProfileId());
		//orgProfileReqDTO.setParentOrgProfileId(getManagedBean().getFulfilmentRequest().getReceivedOrgProfile().getParentOrgProfile().getOrgProfileId());
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
	    		//System.out.println("addresss     "+address.toString());
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
			 reqDTO.setSupplierId(1L);
			 reqDTO.setIssuedOrgId(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId());
			 reqDTO.setYear(2014);
			// getManagedBean().getFlReqNoteMap().put(item.getScRequestNoteDtl().getRequestNoteDtlId(),dtl);
			 //System.out.println("map Size::: "+getManagedBean().getFlReqNoteMap().size());
			 //System.out.println("Map Value;;; "+ getManagedBean().getFlReqNoteMap().get(dtl).getRequestNoteDtlId());
			 getManagedBean().getFlReqNoteMap().get(dtl);
			 //reqDTO.setItemId(item.getScRequestNoteDtl().getRequestNoteDtlId());
			 FulfilmentResDTO resDTO = fulfilmentRequestSO.accumulativeProcurementValue(reqDTO);
			 accumulativeProcuValue = resDTO.getAccumulativeProcuValue().intValue();
			 	if(accumulativeProcuValue > 50000){
						//throw new EpException("Item --- "+ item.getScRequestNoteDtl().getScRequestSupplierItem().getRequestNoteItem().getItemName()+" Accumulative Procu Value  has been exceeded the RM 50K .");
					}
		 }	
	 }
	
	public void editManualAddress(FlDeliveryAddress flDelAddress){
		//retrieveCountryList();
		getManagedBean().setFlManulaAddress(flDelAddress);
		flDelAddress.setEditable(true);
	}
	
	public void receivingOfficerList(FlDeliveryAddress flDelAddress){
		System.out.println("Address ID :: "+flDelAddress.getPmAddress().getAddressId());
		//System.out.println("Address TYPE:: "+flDelAddress.getPmAddress().getPmAddressTypes().get(0).getAddressType());
		String addressType="R";
		AddressResDTO resDTO=getReceivingOfficerList(flDelAddress.getPmAddress().getAddressId(),addressType);
		getManagedBean().setReceivingOffList(resDTO.getAddressTypeList());
		System.out.println("size ::: "+resDTO.getAddressTypeList().size());
	}
	
	public void retrieveCountryList() {
		
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();
		getManagedBean().setCountryList(resDTO.getCountryList());
	}
	
	public void retrieveStateList(AjaxBehaviorEvent e) {
		Long countryIdId = (Long) e.getComponent().getAttributes().get("value");
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryIdId);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStateList(resDTO.getStateList());
	}
	
	public void retrieveDivisionList(AjaxBehaviorEvent e){
		Long stateId = (Long) e.getComponent().getAttributes().get("value");
		DivisionReqDTO reqDTO= new DivisionReqDTO();
		reqDTO.setStateId(stateId);
		IDivisionSO reqSO = (IDivisionSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDivisionSO.class);
		DivisionResDTO resDTO = reqSO.retriveDivisionListByState(reqDTO);
		getManagedBean().setDivisionList(resDTO.getDivisionList());
	}
	
	public void retrieveDistrictList(AjaxBehaviorEvent e) {
		Long divisionId = (Long) e.getComponent().getAttributes().get("value");
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setDivisionId(divisionId);
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByDivision(reqDTO);
		getManagedBean().setDistrictList(resDTO.getDistrictList());
		
	}
	
	public void retrieveCityList(AjaxBehaviorEvent e) {
		Long districtId = (Long) e.getComponent().getAttributes().get("value");
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(districtId);
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);
		CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
		this.getManagedBean().setCityList(resDTO.getCityList());
	}
	
	/*public void chargeChecked(AjaxBehaviorEvent e) {
		// unselect all selected nodes
		getManagedBean().setSelectedNodes(new TreeNode[0]);
		//getManagedBean().setSelectedNodes(new ArrayList<TreeNode> () );
		//clear all dropdowns
		setPmPrjSetia(0L);
		setPmPrgActivity(0L);
		System.out.println("====enter process checked====");
		// String dueDate = (String) ((UIOutput)e.getSource()).getValue();
		String id = (String) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
		getManagedBean().setChargeCheck(id);

	}*/
	public void selectItemNodeEvent() {
		if(null != getManagedBean().getSelectedNodes() && getManagedBean().getSelectedNodes().length >1){
			for(TreeNode selectedNode:getManagedBean().getSelectedNodes()){
				for(TreeNode children:selectedNode.getChildren()){
					//do it for others
					getManagedBean().setSetiaId(((FlFulfilmentItemAddr)children.getData()).getPmSubSetia().getSubSetiaId());
					getManagedBean().setPrgActivityId(((FlFulfilmentItemAddr)children.getData()).getPmPrgActivity().getPrgActivityId());
				}
			}
		}else{
			
		}
		
	}
	
	public void onNodeSelect() {
		//getManagedBean().getSelectedNodes();
		getManagedBean().setVotFundList(new ArrayList<PmVotFund>());
		getVotFundList(4L);
		getManagedBean().setProjectList(new ArrayList<PmProject>());
		getManagedBean().setPrgActivityList(new ArrayList<PmPrgActivity>());
		getManagedBean().setSetiaList(new ArrayList<PmSetia>());
		getManagedBean().setSubSetiaList(new ArrayList<PmSubSetia>());
		TreeNode selectedNode=getManagedBean().getSelectedNode();
				if(null!=selectedNode){
					for(TreeNode children:selectedNode.getChildren()){
						FlFulfilmentItemAddr childData=((FlFulfilmentItemAddr)children.getData());
						if(null !=childData.getPmVotFund()){
							//do it for others
							getManagedBean().setVotFundId(childData.getPmVotFund().getVotFundId());
							if(null !=(childData.getPmVotFund().getVotFundId())){
								getVotFundList(4L);
								getProgramListDis(childData.getPmVotFund().getVotFundId());
								getProjectListDis(childData.getPmVotFund().getVotFundId());
								getManagedBean().setPrgActivityId(childData.getPmPrgActivity().getPrgActivityId());
								//getManagedBean().setProjectId(((FlFulfilmentItemAddr)children.getData()).getPmVotFund().getPmPrjSetias().get(0).getPrjSetiaId());
								//getSetiaListDis(((FlFulfilmentItemAddr)children.getData()).getPmVotFund().getPmPrjSetias().get(0).getPrjSetiaId());
								//getManagedBean().setSetiaId(((FlFulfilmentItemAddr)children.getData()).getPmVotFund().getPmPrjSetias().get(0).getp.getPrjSetiaId());
							}
							if(null !=(childData.getPmSubSetia().getSubSetiaId())){
								getSubSetiaListDis(childData.getPmSubSetia().getPmSetia().getSetiaId()); 
								getManagedBean().setSubSetiaId(childData.getPmSubSetia().getSubSetiaId()); 
								getManagedBean().setSetiaId(childData.getPmSubSetia().getPmSetia().getSetiaId()); 
								getSetiaListDis(childData.getPmSubSetia().getPmSetia().getPmProject().getProjectId());
								getManagedBean().setProjectId(childData.getPmSubSetia().getPmSetia().getPmProject().getProjectId()); 
							}
							/*if(null !=((FlFulfilmentItemAddr)children.getData()).getPmPrgActivity().getPrgActivityId()){
							 getSetiaListDis(Long projectId) {
								 getSubSetiaList
							
								 
								
								
							}*/
							//getManagedBean().setSetiaId(((FlFulfilmentItemAddr)children.getData()).getPmSubSetia().getSubSetiaId());
							//getManagedBean().setPrgActivityId(((FlFulfilmentItemAddr)children.getData()).getPmPrgActivity().getPrgActivityId());
						}else{
							System.out.println("nothing do to ");
							
						}
						
						
					}
				}
	}
	public void clearPopupLists(){
		getManagedBean().setVotFundList(new ArrayList<PmVotFund>());
		getVotFundList(4L);
		getManagedBean().setProjectList(new ArrayList<PmProject>());
		getManagedBean().setPrgActivityList(new ArrayList<PmPrgActivity>());
		getManagedBean().setSetiaList(new ArrayList<PmSetia>());
		getManagedBean().setSubSetiaList(new ArrayList<PmSubSetia>());
		
		
	}
	
	public void onNodeUnSelect() {
		getManagedBean().getSelectedNodes();
		
		if("true".equalsIgnoreCase(getManagedBean().getChargeCheck())){
			if(null != getManagedBean().getSelectedNodes() ){
				for(TreeNode selectedNode:getManagedBean().getSelectedNodes()){
					for(TreeNode children:selectedNode.getChildren()){
						//do it for others
						getManagedBean().setPrgActivityId(((FlFulfilmentItemAddr)children.getData()).getPmSubSetia().getSubSetiaId());
						getManagedBean().setPrgActivityId(((FlFulfilmentItemAddr)children.getData()).getPmPrgActivity().getPrgActivityId());
					}
				}
			}else{
				setPmPrjSetia(0L);
				setPmPrgActivity(0L);
				/*this.setTest("GiriBabu");
				for(TreeNode selectedNode:getManagedBean().getSelectedNodes()){
					for(TreeNode children:selectedNode.getChildren()){
						//do it for others
						setPmPrjSetia(0L);
					}
				}*/
				
			}
		}else{
			if(null != getManagedBean().getSelectedNodes() ){
				for(TreeNode selectedNode:getManagedBean().getSelectedNodes()){
					for(TreeNode children:selectedNode.getChildren()){
						//do it for others
						setPmPrjSetia(0L);
						setPmPrgActivity(0L);
					}
				}
			}
		}
	}
	
	public void selectAllCheckboxSelect(AjaxBehaviorEvent e){
		
		//System.out.println("we are in select allllllllllllllll");
		//e.getComponent().getAttributes().get("value");
		for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			if(null!=deliveryAddress.getFlFulfilmentItemAddr()){
				for(FlFulfilmentItemAddr itemAddress:deliveryAddress.getFlFulfilmentItemAddr()){
					if(null !=itemAddress){
						itemAddress.setSelectedChargeItem(true);
					}
				}
			}
		}	
					
	}
	
	/*public void chargeLineApply() {
		if(null != getManagedBean().getSelectedNodes() && getManagedBean().getSelectedNodes().length ==1){
			if(0 == getPmPrjSetia() || 0== getPmPrgActivity()){
				throw new EpException("Please Select Setia / Sub Setia.");
			}
			updateAllItemsWithChargeDrops(getManagedBean().getSelectedNodes()); 
			for(TreeNode selectedNode:getManagedBean().getSelectedNodes()){
				for(TreeNode children:selectedNode.getChildren()){
					//do it for others
					((FlFulfilmentItemAddr)children.getData()).setPmPrjSetia(getPmPrjSetia());
					((FlFulfilmentItemAddr)children.getData()).setPmPrgActivity(getPmPrgActivity());
				}
			}
			
		}
		
		
	}*/
	public void chargeLineApply() {
		getPmPrjSetia();
		getPmPrgActivity();
		getManagedBean().getVotFundId();
		
		
		  updateSelectedTreeNodes();
		
			if(null == getManagedBean().getSelectedNodes()){
				throw new EpException("Please Select Item Node.");
				//(getManagedBean().getSelectedNodes().length >0 && null !=getManagedBean().getSelectedNodes())
			}else{
				
				if(null == getManagedBean().getVotFundId() || null== getManagedBean().getProjectId() || 
						null == getManagedBean().getPrgActivityId() || null == getManagedBean().getSetiaId() ||
						null== getManagedBean().getSubSetiaId()){
					throw new EpException("Please Select All Dropdowns");
				}
				updateAllItemsWithChargeDrops(getManagedBean().getSelectedNodes()); 
				for(TreeNode selectedNode:getManagedBean().getSelectedNodes()){
					for(TreeNode children:selectedNode.getChildren()){
						//do it for others
						PmVotFund pmVotFund=new PmVotFund();
						PmSetia pmSetia=new PmSetia();
						PmSubSetia pmSubSetia =new PmSubSetia();
						PmPrgActivity pmPrgActivity=new PmPrgActivity();
						PmProject pmProject= new PmProject();
						pmVotFund.setVotFundId(getManagedBean().getVotFundId());
						pmSetia.setSetiaId(getManagedBean().getSetiaId());
						if(null != getManagedBean().getProjectId()){
							pmProject.setProjectId(getManagedBean().getProjectId());
							pmSetia.setPmProject(pmProject);
						}
						pmSubSetia.setSubSetiaId(getManagedBean().getSetiaId());
						pmSubSetia.setPmSetia(pmSetia);
						pmPrgActivity.setPrgActivityId(getManagedBean().getPrgActivityId());
						
						FlFulfilmentItemAddr childItemAddress=(FlFulfilmentItemAddr)children.getData();
						childItemAddress.setPmSubSetia(pmSubSetia);
						childItemAddress.setPmPrgActivity(pmPrgActivity);
						childItemAddress.setPmVotFund(pmVotFund);
						
						
						
						childItemAddress.getFlFulfilmentItem().setPmSubSetia(pmSubSetia);
						childItemAddress.getFlFulfilmentItem().setPmPrgActivity(pmPrgActivity);
						childItemAddress.getFlFulfilmentItem().setPmVotFund(pmVotFund);
					}
				}
			}
			getManagedBean().populateChargeLineData();
		/*}else{
			if(null == getManagedBean().getSelectedNodes()){
				throw new EpException("Please Select Item Node.");
			}else{
				if(0 == getPmPrjSetia() || 0== getPmPrgActivity()){
					throw new EpException("Please Select Setia / Sub Setia.");
				}
				for(int i =0;i<getManagedBean().getSelectedNodes().length;i++){
					
				}
				updateAllItemsWithChargeDrops(getManagedBean().getSelectedNodes()); 
				for(TreeNode selectedNode:getManagedBean().getSelectedNodes()){
					for(TreeNode children:selectedNode.getChildren()){
						//do it for others
						//((FlFulfilmentItemAddr)children.getData()).setAssetInd(false);
						((FlFulfilmentItemAddr)children.getData()).setPmPrjSetia(getPmPrjSetia());
						((FlFulfilmentItemAddr)children.getData()).setPmPrgActivity(getPmPrgActivity());
					}
				}
			}
			
		}*/
		
	}
	//itemAddress.getScRequestItem().getRequestNoteItemId().equals(arg0)

	private void updateSelectedTreeNodes() {
		List<TreeNode> selectedNodes=new ArrayList<TreeNode>();
		TreeNode root=getManagedBean().getChargeLineItemRoot();
		for(TreeNode node:root.getChildren()){
			FlFulfilmentItemAddr addr=(FlFulfilmentItemAddr) node.getData();
			if(addr.isSelectedChargeItem()){
				//System.out.println("in update methos check ");
				selectedNodes.add(node);
			}
		}
		
      getManagedBean().setSelectedNodes(selectedNodes.toArray(new TreeNode[selectedNodes.size()]));
		
	}

	private void updateAllItemsWithChargeDrops(TreeNode[] selectedNodes) {
		if(null !=getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
				if(null!=deliveryAddress.getFlFulfilmentItemAddr()){
					for(FlFulfilmentItemAddr itemAddress:deliveryAddress.getFlFulfilmentItemAddr()){
						if(null !=selectedNodes){
							for(TreeNode node:selectedNodes){
								FlFulfilmentItemAddr selectedItem=(FlFulfilmentItemAddr)node.getData();
								if(itemAddress.getScRequestItem().getRequestNoteItemId().equals(selectedItem.getScRequestItem().getRequestNoteItemId())){
									PmPrgActivity pmPrgActivity=new PmPrgActivity();
									
									itemAddress.setCoaValue("Y");
									//itemAddress.setPmSubSetia(getManagedBean().getSetiaId());
									//itemAddress.setPmPrgActivity(getManagedBean().getPrgActivityId());
								}
								
							}
						}
						
					}
				}	
			}
		}	
		
	}

	// display itemCode,itemName,QueryDesc in popup
	public void showItemQuery(FlFulfilmentItemAddr flItemAddress){
		
		flItemAddress.getScRequestItem().getItemName();
		setQueryItemCode(flItemAddress.getScRequestItem().getRequestNoteItemId().toString());
		setQueryItemName(flItemAddress.getScRequestItem().getItemName());
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
	
	/**
	 * @return the pmPrjSetia
	 */
	public Long getPmPrjSetia() {
		return pmPrjSetia;
	}

	/**
	 * @param pmPrjSetia the pmPrjSetia to set
	 */
	public void setPmPrjSetia(Long pmPrjSetia) {
		this.pmPrjSetia = pmPrjSetia;
	}

	/**
	 * @return the test
	 */
	public String getTest() {
		return test;
	}

	/**
	 * @param test the test to set
	 */
	public void setTest(String test) {
		this.test = test;
	}

	/**
	 * @return the pmPrgActivity
	 */
	public Long getPmPrgActivity() {
		return pmPrgActivity;
	}

	/**
	 * @param pmPrgActivity the pmPrgActivity to set
	 */
	public void setPmPrgActivity(Long pmPrgActivity) {
		this.pmPrgActivity = pmPrgActivity;
	}

	/**
	 * @return the queryItemCode
	 */
	public String getQueryItemCode() {
		return queryItemCode;
	}

	/**
	 * @param queryItemCode the queryItemCode to set
	 */
	public void setQueryItemCode(String queryItemCode) {
		this.queryItemCode = queryItemCode;
	}

	/**
	 * @return the queryItemName
	 */
	public String getQueryItemName() {
		return queryItemName;
	}

	/**
	 * @param queryItemName the queryItemName to set
	 */
	public void setQueryItemName(String queryItemName) {
		this.queryItemName = queryItemName;
	}

	/**
	 * @return the queryDesc
	 */
	public String getQueryDesc() {
		return queryDesc;
	}

	/**
	 * @param queryDesc the queryDesc to set
	 */
	public void setQueryDesc(String queryDesc) {
		this.queryDesc = queryDesc;
	}

	/**
	 * @return the disPmPrjSetia
	 */
	public Long getDisPmPrjSetia() {
		return disPmPrjSetia;
	}

	/**
	 * @param disPmPrjSetia the disPmPrjSetia to set
	 */
	public void setDisPmPrjSetia(Long disPmPrjSetia) {
		this.disPmPrjSetia = disPmPrjSetia;
	}

	/**
	 * @return the disPmPrgActivity
	 */
	public Long getDisPmPrgActivity() {
		return disPmPrgActivity;
	}

	/**
	 * @param disPmPrgActivity the disPmPrgActivity to set
	 */
	public void setDisPmPrgActivity(Long disPmPrgActivity) {
		this.disPmPrgActivity = disPmPrgActivity;
	}

	/**
	 * @return the selectedAllChargeItem
	 */
	public boolean isSelectedAllChargeItem() {
		return selectedAllChargeItem;
	}

	/**
	 * @param selectedAllChargeItem the selectedAllChargeItem to set
	 */
	public void setSelectedAllChargeItem(boolean selectedAllChargeItem) {
		this.selectedAllChargeItem = selectedAllChargeItem;
	}
	
	public VotFundResDTO getVotFundList(Long profileId){
		IVotFundSO fundSO = (IVotFundSO) BusinessDelegate.getInstance().getServiceObject("pm",IVotFundSO.class);
		VotFundReqDTO reqDTO = new VotFundReqDTO();
		reqDTO.setOrgId(profileId);
		VotFundResDTO resDTO = fundSO.retrieveVotFundListByOrgId(reqDTO);
		getManagedBean().setVotFundList(resDTO.getVotFundList());
		return resDTO;
	}
	
	public ProjectSetiaResDTO getProjectList(AjaxBehaviorEvent e) {
		Long votFundId = (Long) e.getComponent().getAttributes().get("value");
		IProjectSetiaSO projectSetiaSO = (IProjectSetiaSO) BusinessDelegate.getInstance().getServiceObject("pm",IProjectSetiaSO.class);
		ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
		reqDTO.setVotFundId(votFundId);
		ProjectSetiaResDTO resDTO = projectSetiaSO.retrieveProjectList(reqDTO);
		getManagedBean().setProjectList(resDTO.getProjectList());
		return resDTO;
	}
	public ProjectSetiaResDTO getProjectListDis(Long votFundId) {
		IProjectSetiaSO projectSetiaSO = (IProjectSetiaSO) BusinessDelegate.getInstance().getServiceObject("pm",IProjectSetiaSO.class);
		ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
		reqDTO.setVotFundId(votFundId);
		ProjectSetiaResDTO resDTO = projectSetiaSO.retrieveProjectList(reqDTO);
		getManagedBean().setProjectList(resDTO.getProjectList());
		return resDTO;
	}
	
	public ProgramActivityResDTO getProgramList(AjaxBehaviorEvent e) {
		Long votFundId = (Long) e.getComponent().getAttributes().get("value");
		IProgramActivitySO programActivitySO = (IProgramActivitySO) BusinessDelegate.getInstance().getServiceObject("pm",IProgramActivitySO.class);
		ProgramActivityReqDTO reqDTO = new ProgramActivityReqDTO();
		reqDTO.setVotFundId(votFundId);
		ProgramActivityResDTO resDTO = programActivitySO.retrieveProgramActivityList(reqDTO);
		getManagedBean().setPrgActivityList(resDTO.getPrgActivityList());
		
		return resDTO;
	}
	
	public ProgramActivityResDTO getProgramListDis(Long votFundId) {
		IProgramActivitySO programActivitySO = (IProgramActivitySO) BusinessDelegate.getInstance().getServiceObject("pm",IProgramActivitySO.class);
		ProgramActivityReqDTO reqDTO = new ProgramActivityReqDTO();
		reqDTO.setVotFundId(votFundId);
		ProgramActivityResDTO resDTO = programActivitySO.retrieveProgramActivityList(reqDTO);
		getManagedBean().setPrgActivityList(resDTO.getPrgActivityList());
		
		return resDTO;
	}
	
	public ProjectSetiaResDTO getSetiaList(AjaxBehaviorEvent e) {
		Long projectId = (Long) e.getComponent().getAttributes().get("value");
		IProjectSetiaSO projectSetiaSO = (IProjectSetiaSO) BusinessDelegate.getInstance().getServiceObject("pm",IProjectSetiaSO.class);
		ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
		reqDTO.setProjectId(projectId);
		ProjectSetiaResDTO resDTO = projectSetiaSO.retrieveSetiaList(reqDTO);
		getManagedBean().setSetiaList(resDTO.getSetiaList());
		return resDTO;
	}
	public ProjectSetiaResDTO getSetiaListDis(Long projectId) {
		//Long projectId = (Long) e.getComponent().getAttributes().get("value");
		IProjectSetiaSO projectSetiaSO = (IProjectSetiaSO) BusinessDelegate.getInstance().getServiceObject("pm",IProjectSetiaSO.class);
		ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
		reqDTO.setProjectId(projectId);
		ProjectSetiaResDTO resDTO = projectSetiaSO.retrieveSetiaList(reqDTO);
		getManagedBean().setSetiaList(resDTO.getSetiaList());
		return resDTO;
	}
	
	public ProjectSetiaResDTO getSubSetiaList(AjaxBehaviorEvent e) {
		Long setiaId = (Long) e.getComponent().getAttributes().get("value");
		IProjectSetiaSO projectSetiaSO = (IProjectSetiaSO) BusinessDelegate.getInstance().getServiceObject("pm",IProjectSetiaSO.class);
		ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
		reqDTO.setSetiaId(setiaId);
		ProjectSetiaResDTO resDTO = projectSetiaSO.retrieveSubSetiaList(reqDTO);
		getManagedBean().setSubSetiaList(resDTO.getSubSetiaList());
		return resDTO;
	}
	
	public ProjectSetiaResDTO getSubSetiaListDis(Long  setiaId) {
		//Long setiaId = (Long) e.getComponent().getAttributes().get("value");
		IProjectSetiaSO projectSetiaSO = (IProjectSetiaSO) BusinessDelegate.getInstance().getServiceObject("pm",IProjectSetiaSO.class);
		ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
		reqDTO.setSetiaId(setiaId);
		ProjectSetiaResDTO resDTO = projectSetiaSO.retrieveSubSetiaList(reqDTO);
		getManagedBean().setSubSetiaList(resDTO.getSubSetiaList());
		return resDTO;
	}
	
	public void getUserProfiles(){
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setLoginId(getFacesScopes().getUserSessionBean().getLoginId());
		UserResDTO userResDTO = userSO.retrieveUserByLoginId(userReqDTO);
		FulfilmentReqDTO fulfilmentReqDTO = new FulfilmentReqDTO();
		fulfilmentReqDTO.setPmUser(userResDTO.getPmUser());
	}
	
	public GlAccountResDTO getGlAccount(Long orgProfileId){
		IGlAccountSO glAccountSO = (IGlAccountSO) BusinessDelegate.getInstance().getServiceObject("pm",IGlAccountSO.class);
		GlAccountReqDTO reqDTO = new GlAccountReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		GlAccountResDTO resDTO = glAccountSO.retrieveGlAccountList(reqDTO);
		return resDTO;
	}
	
	public AddressResDTO getReceivingOfficerList(Long addressId, String addressType){
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject("pm",IAddressSO.class);
		AddressReqDTO reqDTO = new AddressReqDTO();
		reqDTO.setAddressId(addressId);
		reqDTO.setAddressType(addressType);
		AddressResDTO resDTO = addressSO.retrieveReceivingOfficerList(reqDTO);
		getManagedBean().setReceivingOffList(resDTO.getAddressTypeList());
		System.out.println("size ::: "+resDTO.getAddressTypeList().size());
		//resDTO.get
		return resDTO;
	}
	
	public AgOfficeResDTO getAgAccount(Long orgProfileId){
		IAgOfficeSO glAccountSO = (IAgOfficeSO) BusinessDelegate.getInstance().getServiceObject("pm",IAgOfficeSO.class);
		AgOfficeReqDTO reqDTO = new AgOfficeReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		AgOfficeResDTO resDTO = glAccountSO.retrieveAgOfficeByOrgProfileId(reqDTO);

		return resDTO;
	}
	
}
