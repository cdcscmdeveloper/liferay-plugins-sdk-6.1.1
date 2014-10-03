/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.pr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.IFulfilmentSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.agoffice.IAgOfficeSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.projectsetia.IProjectSetiaSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseRequestSO;
import my.ep.biz.service.sc.directpurchase.IRequestNoteSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
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
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.podata.FLPurchaseOrderDataType;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.fl.FlSupplierDtl;
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
import my.ep.persistence.entity.pm.common.PmAgOffice;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmProject;
import my.ep.persistence.entity.pm.common.PmSetia;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.pm.VotFundType;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.pr.PurchaseRequestManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.fl.helper.LocationSingleton;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

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
	private boolean checkFlg = false;
	private boolean displayItemPnl;
	private boolean selectedAllChargeItem;
	private String className="@(.myPrClassName)";
	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

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
	
	private static Map<Long, PmVotFund> votFundCodeMap = new HashMap<Long, PmVotFund>();

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
			getManagedBean().getFlManulaAddress().setReceivingGrp(new PmAddressType());
		}
		if(null == getManagedBean().getFlManulaAddress().getPmAddress()){
			 getManagedBean().getFlManulaAddress().setPmAddress(new PmAddress());
		}
		if(null == getManagedBean().getFlManulaAddress().getPmAddress().getPmCountry()){
			getManagedBean().getFlManulaAddress().getPmAddress().setPmCountry(new PmCountry());
			getManagedBean().getFlManulaAddress().getPmAddress().getPmCountry().setCountryId(1L);
		}
		
		if(isFlFulfilmentRequisitioner()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setFulfilmentRequisitioner(facesScopes.getUserSessionBean().getLoginId());
		}
//		getManagedBean().setItemAddrVoteFundDisp(new FlFulfilmentItemAddr());
		getManagedBean().setDeleteButtonHide(true);
		
	}

	public String fulfillmentRequestAction(String action) throws Exception {

		IFulfilmentRequestSO fulfilmentRequestSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)){
				if(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().size()==0) {
					throw new EpException(MessageConst.ERROR_100023, new String[]{"Delivery Address"});
				}				
				if(null==getManagedBean().getApproverModel().getTarget() || getManagedBean().getApproverModel().getTarget().isEmpty()) {
					throw new EpException(MessageConst.ERROR_750017);
				}
			} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)){
				if(flBB.getManagedBean().getRemarkList().get(0).getRemark()==null || flBB.getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
					throw new EpException(MessageConst.ERROR_100022, new String[]{"Remark"});
				}
			}
			
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flFulfilmentRequest = getManagedBean().getFulfilmentRequest();
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
			reqDTO.setFlFulfilmentRequest(flFulfilmentRequest);
			reqDTO.setContractRequest(false);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			reqDTO.setOutcome(action);
			reqDTO.setToken(token);
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setApproverMap(getManagedBean().getApproverMap());
			if(isFlFulfilmentRequisitioner()) {
				reqDTO.setRequisitioner(Boolean.TRUE);
			}
			
			FulfilmentResDTO resDTO = fulfilmentRequestSO.saveFulfillmentRequest(reqDTO);
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String messageId = MessageConst.INFO_700005;
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700004;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					//messageId = MessageConst.INFO_745004;
					messageId = MessageConst.INFO_700007;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
					messageId = MessageConst.INFO_700009;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					messageId = MessageConst.INFO_700008;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
					messageId = MessageConst.INFO_700010;
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_REQUEST), flFulfilmentRequest.getDocNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_700002, 
									new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_ORDER), resDTO.getFlFulfilmentRequest().getFlFulfilmentOrder().getDocNo()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				}	
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					reqDTO.setFlRequestId(resDTO.getFlRequestId());
					resDTO = fulfilmentRequestSO.retrieveFulfilmentRequest(reqDTO);
					setFulfilmentRequestField(resDTO);
				}
			} 
		}  finally {
			fulfilmentRequestSO = null;
		}
		return null;
	
	}

	public void setFulfilmentRequestField(FulfilmentResDTO resDTO) {
		PurchaseRequestReqDTO scReqDTO = new PurchaseRequestReqDTO();
		IPurchaseRequestSO purchaseRequestSO = (IPurchaseRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,	IPurchaseRequestSO.class);
		IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,	IFulfilmentRequestSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
		getManagedBean().getFulfilmentRequest().setTitle(getManagedBean().getFulfilmentRequest().getTitle().trim());
		FulfilmentBackingBean flBB = new FulfilmentBackingBean(ParameterConst.DOCTYPE_PURCHASE_REQUEST, resDTO.getFlFulfilmentRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
		flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
		getManagedBean().setSelectedTabIndex(0);

		if(getManagedBean().getApproverModel()==null) {
			List<String> source = new ArrayList<String>(); 
	        List<String> target = new ArrayList<String>();  
	        getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}	
		
		if(getManagedBean().getFulfilmentRequest().getPmUserGroup()!=null) {
			FulfilmentResDTO approverListRes = new FulfilmentResDTO();
			List<String> source = new ArrayList<String>();
				reqDTO.setGroupId(getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId());
				try {
					approverListRes = requestSO.retrieveApproverListByGroupId(reqDTO);
				} catch (Exception e) {
					e.printStackTrace();
				}
			for(PmUserGroupUser groupUser : approverListRes.getApproverGroupUserList()) {
				source.add(groupUser.getPmUser().getUserName());
			}
			getManagedBean().getApproverModel().setSource(source);
		}
		getManagedBean().getApproverModel().setTarget(resDTO.getSelectedApproverList());
		getManagedBean().getApproverModel().getSource().removeAll(resDTO.getSelectedApproverList());

		getSupplierBranchesList(resDTO.getFlFulfilmentRequest().getSmSupplier());
		
		addPurchsedItems();
		if(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().size()>0) {
			getManagedBean().setTierMap(new HashMap<Long,List<FlFulfilmentItemAddr>>());
			getManagedBean().setZonalTierMap(new HashMap<String,List<FlFulfilmentItemAddr>>());
			
			sortAddress(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses());
			getManagedBean().setSelectedDelAddr(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().get(0));
			selectAddrEvent();
			List<FlFulfilmentItemAddr> fulfilmentItemAddrList = null;
			for(FlDeliveryAddress delAddr : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
				for(FlFulfilmentItemAddr itemAddr : delAddr.getFlFulfilmentItemAddr()){
					if(itemAddr.getCmItem()==null && itemAddr.getScRequestItem().getCmItem()!=null) {
						itemAddr.setCmItem(itemAddr.getScRequestItem().getCmItem());
					}
					
					//re-contruct zonal-tier map
					if(getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER) 
							|| getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER)){
						scReqDTO.setPriceType(getManagedBean().getPmPriceType());
						if(getManagedBean().getScRequestSupplierItemIdMap().get(itemAddr.getScRequestItem().getRequestNoteItemId()) != null){
							scReqDTO.setRequestSupplierItemId(getManagedBean().getScRequestSupplierItemIdMap().get(itemAddr.getScRequestItem().getRequestNoteItemId()));
						}

						scReqDTO.setPmAddress(getManagedBean().getSelectedDelAddr().getPmAddress());
						PurchaseRequestResDTO zoneIdResDTO = purchaseRequestSO.getSupplierItemZoneIdByPmAddress(scReqDTO);
						itemAddr.setZoneId(zoneIdResDTO.getSpecZoneId());

						String key = itemAddr.getZoneId()+" "+itemAddr.getScRequestItem().getRequestNoteItemId();
						if(getManagedBean().getZonalTierMap().containsKey(key)){
							fulfilmentItemAddrList = getManagedBean().getZonalTierMap().get(key);
							fulfilmentItemAddrList.add(itemAddr);
						}else{
							fulfilmentItemAddrList = new ArrayList<FlFulfilmentItemAddr>();
							fulfilmentItemAddrList.add(itemAddr);
							getManagedBean().getZonalTierMap().put(key, fulfilmentItemAddrList);
						}
						
						//re-construct tier map
					}else if(getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER) 
							|| getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_PROMOTION_TIER)){
						
						Long key = itemAddr.getScRequestItem().getRequestNoteItemId();
						
						if(getManagedBean().getTierMap().containsKey(key)){
							fulfilmentItemAddrList = getManagedBean().getTierMap().get(key);
							fulfilmentItemAddrList.add(itemAddr);
						}else{
							fulfilmentItemAddrList = new ArrayList<FlFulfilmentItemAddr>();
							fulfilmentItemAddrList.add(itemAddr);
							getManagedBean().getTierMap().put(key, fulfilmentItemAddrList);
						}
					}
				}
			}
		}
		
		//initialize all the COA map
		getVotFundList(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId());
		
		Date startDate = getManagedBean().getFulfilmentRequest().getStartDate();
		int deliveryTerm = 0;
		deliveryTerm = getManagedBean().getFulfilmentRequest().getDeliveryTerm();
		String uomCode = getManagedBean().getFulfilmentRequest().getPmUom().getParameterCode();
		if(uomCode.equalsIgnoreCase(ParameterConst.DELIVERY_TERM_DAYS)){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.DATE, deliveryTerm);
			getManagedBean().setMaxDate(calendar.getTime());
		}else if(uomCode.equalsIgnoreCase(ParameterConst.DELIVERY_TERM_MONTHS)){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.MONTH, deliveryTerm);
			getManagedBean().setMaxDate(calendar.getTime());
		}
	}

	public void flDeliveryAddrList() {
		AddressReqDTO reqDTO = new AddressReqDTO();
		reqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId());
		reqDTO.setAddressType(AddressType.DELIVERY_ADDRESS.value());
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAddressSO.class);
		AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
		getManagedBean().setAddressTypesList(resDTO.getAddressTypeList());
		sortAddressType(getManagedBean().getAddressTypesList());
	}
	
	public void flManualDeliveryAddr() {
		getManagedBean().setEmailValidate(false);
		retrieveCountryList();
		getManagedBean().setFlManulaAddress(new FlDeliveryAddress());
		getManagedBean().getFlManulaAddress().setDeliveryAddressId(1L);
		getManagedBean().getFlManulaAddress().setPmAddress(new PmAddress());
		// default to Malaysia
		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICountrySO.class);
		CountryReqDTO countryReqDTO = new CountryReqDTO();
		countryReqDTO.setCountryCode("MY");
		getManagedBean().getFlManulaAddress().getPmAddress().setPmCountry(countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());
		getManagedBean().getFlManulaAddress().getPmAddress().setPmState(new PmState());
		getManagedBean().getFlManulaAddress().getPmAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getFlManulaAddress().getPmAddress().setPmCity(new PmCity());
		getManagedBean().getFlManulaAddress().getPmAddress().setPmDivision(new PmDivision());
		getManagedBean().getFlManulaAddress().setReceivingGrp(new PmAddressType());
		
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(getManagedBean().getFlManulaAddress().getPmAddress().getPmCountry().getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStateList(resDTO.getStateList());
		
		if(getManagedBean().getAddressTypesList()==null) {
			AddressReqDTO addReqDTO = new AddressReqDTO();
			addReqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId());
			addReqDTO.setAddressType(AddressType.DELIVERY_ADDRESS.value());
			IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject("pm",IAddressSO.class);
			AddressResDTO addResDTO = addressSO.retrieveAddressTypeList(addReqDTO);
			getManagedBean().setAddressTypesList(addResDTO.getAddressTypeList());
		}
		if(getManagedBean().getManualAddressMap() == null || getManagedBean().getManualAddressMap().size() == 0){
			setManualAddressList();
		}		
	}
	
	public void updateDeliveryAddrDt() {
		if (null == getManagedBean().getSelectedPmDelAddrList()
				|| getManagedBean().getSelectedPmDelAddrList().isEmpty()) {
			return;
		}
		FlDeliveryAddress deliveryAddr = null;
		int i = 0;
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
				deliveryAddr.setAddressType(addrType);
				deliveryAddr.setStartDate(getManagedBean().getFulfilmentRequest().getStartDate());
				deliveryAddr.setEndDate(getManagedBean().getFulfilmentRequest().getEndDate());
				deliveryAddr.setPmUom(getManagedBean().getFulfilmentRequest().getPmUom());
				deliveryAddr.setDeliveryTerm(getManagedBean().getFulfilmentRequest().getDeliveryTerm());
				if(i==0) {
					getManagedBean().setSelectedDelAddr(deliveryAddr);
				}
				i++;
				if (null == getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()) {
					getManagedBean().getFulfilmentRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
				}
				
				//sortAddress(deliveryAddr);
				getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().add(deliveryAddr);
				sortAddress(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses());
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_745053, new String[]{getFullAddress(addrType.getPmAddress())}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}
		}
		getManagedBean().setAddressTypesList(new ArrayList<PmAddressType>());
		selectAddrEvent();
	}
	public void updateManualDeliveryAddrDt() {
		getManagedBean().setEmailValidate(true);
		if(null== getManagedBean().getFlManulaAddress().getPmAddress() ||  getManagedBean().getFlManulaAddress().isEditable()){
			// add to cart validation
			RequestContext.getCurrentInstance().execute("addManualDeliveryAddr.hide()");
			getManagedBean().setRequired(Boolean.FALSE);			
			return;
		}
		FlDeliveryAddress deliveryAddr = null;
		PmAddress manualAddr=new PmAddress();
		manualAddr=getManagedBean().getFlManulaAddress().getPmAddress();
		deliveryAddr = new FlDeliveryAddress();
		deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
		deliveryAddr.setPmAddress(manualAddr);
		
		ICitySO citySO = (ICitySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);
		CityReqDTO cityReqDTO = new CityReqDTO();
		cityReqDTO.setCityId(manualAddr.getPmCity().getCityId());
		CityResDTO cityResDTO = citySO.retriveCityById(cityReqDTO);
		manualAddr.setPmCity(cityResDTO.getPmCity());
		
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setStateId(manualAddr.getPmState().getStateId());
		StateResDTO resDTO = reqSO.retriveStateByID(reqDTO);
		manualAddr.setPmState(resDTO.getPmState());
		
		deliveryAddr.setStartDate(getManagedBean().getFulfilmentRequest().getStartDate());
		deliveryAddr.setEndDate(getManagedBean().getFulfilmentRequest().getEndDate());
		deliveryAddr.setPmUom(getManagedBean().getFulfilmentRequest().getPmUom());
		deliveryAddr.setDeliveryTerm(getManagedBean().getFulfilmentRequest().getDeliveryTerm());
		deliveryAddr.setReceivingGrp(getManagedBean().getFlManulaAddress().getReceivingGrp());
		getManagedBean().setSelectedDelAddr(deliveryAddr);
		getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().add(deliveryAddr);
		selectAddrEvent();
		
		// add to cart validation
		RequestContext.getCurrentInstance().execute("addManualDeliveryAddr.hide()");
		getManagedBean().setRequired(Boolean.FALSE);
	}
	
	public void closeListener() {
		log.info("closeListener");
		getManagedBean().setEmailValidate(true);
		RequestContext.getCurrentInstance().execute("addManualDeliveryAddr.hide()");
		getManagedBean().setRequired(Boolean.FALSE);
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
	
	
	public void selectOneCheckboxSelect(AjaxBehaviorEvent e){
		
		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setDeleteButtonHide(false);
		}else{
			getManagedBean().setDeleteButtonHide(true);
		}
		
	}
	/*public void onAddressUnSelect(){
		getManagedBean().setDeleteSelectedAddress(true);
		for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			if(null!=deliveryAddress){
				deliveryAddress.setSelectedAddressRowDelete(true);
			}
		}	
	}*/
	
	public void removeAddRow(){
		IPurchaseRequestSO purchaseRequestSO = (IPurchaseRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,	IPurchaseRequestSO.class);
		PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();
		List<FlFulfilmentItemAddr> flItemAddrList = null;
				
		if(null !=getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			//removal itemAddrList from tier and zonaltier map
			for(FlDeliveryAddress deliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
				for(FlFulfilmentItemAddr itemAddr : deliveryAddress.getFlFulfilmentItemAddr()){
					if(deliveryAddress.isSelectedAddressRowDelete()){
						if(getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_PROMOTION_TIER) 
								||getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER)){
							Long key = itemAddr.getScRequestItem().getRequestNoteItemId();
							if(getManagedBean().getTierMap().containsKey(key)){
								flItemAddrList = getManagedBean().getTierMap().get(key);
								flItemAddrList.remove(itemAddr);
							}

						}else if(getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER) 
								||getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)){
							String key = itemAddr.getZoneId()+" "+itemAddr.getScRequestItem().getRequestNoteItemId();
							if(getManagedBean().getZonalTierMap().containsKey(key)){
								flItemAddrList = getManagedBean().getZonalTierMap().get(key);
								flItemAddrList.remove(itemAddr);
							}
						}
					}
				}
			}
			
			//remove the selected parent flDeliveryAddr
			if(null !=getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
				Iterator<FlDeliveryAddress> delAddressIterator=getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().iterator();
				while(delAddressIterator.hasNext()){
					FlDeliveryAddress  deliveryAddress=delAddressIterator.next();
					if(deliveryAddress.isSelectedAddressRowDelete()){
						delAddressIterator.remove();
					}
				}
			}
			if(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().size()>0) {
				getManagedBean().setSelectedDelAddr(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().get(0));
				//recalculate the latest tier n zonaltier map
				if(getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER) 
						||getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)){
					
					for (List<FlFulfilmentItemAddr> values : getManagedBean().getZonalTierMap().values()) {
						for(FlFulfilmentItemAddr reCalculate : values){
							BigDecimal sumOrderQty = new BigDecimal(0);
							sumOrderQty = sumOrderQty.add(reCalculate.getOrderedQty());	
							if(getManagedBean().getScRequestSupplierItemIdMap().get(reCalculate.getScRequestItem().getRequestNoteItemId()) != null){
								reqDTO.setRequestSupplierItemId(getManagedBean().getScRequestSupplierItemIdMap().get(reCalculate.getScRequestItem().getRequestNoteItemId()));
							}
							reqDTO.setPriceType(getManagedBean().getPmPriceType());
							reqDTO.setPmAddress(getManagedBean().getSelectedDelAddr().getPmAddress());
							reqDTO.setOrderQuantity(sumOrderQty);
							PurchaseRequestResDTO purResDTO = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
							reCalculate.setScSupplierItemPrice(purResDTO.getSupplierItemPrice());
							reCalculate.setUnitPrice(purResDTO.getUnitPrice());
							reCalculate.setOrderedAmt(reCalculate.getOrderedQty().multiply(reCalculate.getUnitPrice()));
						}
					}	
				}else if(getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_PROMOTION_TIER) 
						||getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER)){
					for (List<FlFulfilmentItemAddr> values : getManagedBean().getTierMap().values()) {
						for(FlFulfilmentItemAddr reCalculate : values){
							BigDecimal sumOrderQty = new BigDecimal(0);
							sumOrderQty = sumOrderQty.add(reCalculate.getOrderedQty());	
							reqDTO.setRequestSupplierItemId(reCalculate.getScSupplierItemPrice().getScRequestSupplierItem().getRequestSupplierItemId());
							reqDTO.setPriceType(getManagedBean().getPmPriceType());
							reqDTO.setPmAddress(getManagedBean().getSelectedDelAddr().getPmAddress());
							reqDTO.setOrderQuantity(sumOrderQty);
							PurchaseRequestResDTO purResDTO = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
							reCalculate.setScSupplierItemPrice(purResDTO.getSupplierItemPrice());
							reCalculate.setUnitPrice(purResDTO.getUnitPrice());
							reCalculate.setOrderedAmt(reCalculate.getOrderedQty().multiply(reCalculate.getUnitPrice()));
						}
					}	
				}
			} else {
				getManagedBean().setSelectedDelAddr(null);
			}
			populateItemsForSelectedAddress();
		}
	}
	
	public void selectAddrEvent() {
		populateItemsForSelectedAddress();
	}

	private void populateItemsForSelectedAddress() {
		List<FlFulfilmentItemAddr> itemsList = new ArrayList<FlFulfilmentItemAddr>();

		for (FlFulfilmentItemAddr newItem : getManagedBean().getRequestNoteItemList()) {
			boolean existed = false;
			if (getManagedBean().getSelectedDelAddr() != null && getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr() != null) {
				for (FlFulfilmentItemAddr selectedItem : getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()) {
					if (selectedItem.getScRequestItem().getRequestNoteItemId().equals(newItem.getScRequestItem().getRequestNoteItemId())) {
						existed = true;
						itemsList.add(selectedItem);
						break;
					}
				}
			}
			if (!existed) {
				newItem.setOrderedQty(new BigDecimal(0));
				newItem.setOrderedAmt(new BigDecimal(0));
				if(getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER) 
						||getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)
						|| getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_PROMOTION_TIER) 
						||getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER)){
					newItem.setUnitPrice(new BigDecimal(0));
				}
				
				itemsList.add(newItem);
			}
		}
		itemsList.size();
		if(null != getManagedBean().getSelectedDelAddr()){
			validatePriceType(itemsList);
		}
		getManagedBean().setShowAllItemsRows(itemsList);
	}
	
	private void validatePriceType(List<FlFulfilmentItemAddr> itemsList) {
		if(getManagedBean().getPmPriceType() != null){
			IPurchaseRequestSO purchaseRequestSO = (IPurchaseRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseRequestSO.class);
			PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();
			if(getManagedBean().getPmPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_STANDARD)
					|| getManagedBean().getPmPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION)){
				for(FlFulfilmentItemAddr itemAddr : itemsList){
					reqDTO.setPriceType(getManagedBean().getPmPriceType());
					if(getManagedBean().getScRequestSupplierItemIdMap().get(itemAddr.getScRequestItem().getRequestNoteItemId()) != null){
						reqDTO.setRequestSupplierItemId(getManagedBean().getScRequestSupplierItemIdMap().get(itemAddr.getScRequestItem().getRequestNoteItemId()));
					}
					reqDTO.setPmAddress(getManagedBean().getSelectedDelAddr().getPmAddress());
					PurchaseRequestResDTO purResDTO = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
					itemAddr.setUnitPrice(purResDTO.getUnitPrice());
					itemAddr.setScSupplierItemPrice(purResDTO.getSupplierItemPrice());
					PurchaseRequestResDTO zoneIdResDTO = purchaseRequestSO.getSupplierItemZoneIdByPmAddress(reqDTO);
					itemAddr.setZoneId(zoneIdResDTO.getSpecZoneId());
				}
			}
		}
	}

	public void addPurchsedItems() {
		FlFulfilmentItemAddr flItemAddr = null;
		
		IPurchaseRequestSO purchaseRequestSO = (IPurchaseRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,	IPurchaseRequestSO.class);
		PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();
		reqDTO.setPurchaseRequestId(getManagedBean().getFulfilmentRequest().getScPurchaseRequest().getPurchaseRequestId());
		reqDTO.setIncludeDetail(true);
		PurchaseRequestResDTO purResDTO = purchaseRequestSO.getPurchaseRequest(reqDTO);
		List<ScRequestNoteDtl> noteDtlList = new ArrayList<ScRequestNoteDtl>();
		noteDtlList.addAll(purResDTO.getPurchaseRequest().getRequestNoteDtls());
		getManagedBean().setRequestNoteWithDtlsList(noteDtlList);
		List<FlFulfilmentItemAddr> allItemsList = new ArrayList<FlFulfilmentItemAddr>();
		getManagedBean().setScRequestSupplierItemIdMap(null);
		for (ScRequestNoteDtl purchaseItem : getManagedBean().getRequestNoteWithDtlsList()) {
			//Validate to eliminate duplicate item. 
			boolean isItemContains = isPurchaseItemContains(purchaseItem,allItemsList);
			//If Item is not already added.
			if (!isItemContains) {
				flItemAddr = new FlFulfilmentItemAddr();
				flItemAddr.setPriceType(purchaseItem.getPriceType());
				flItemAddr.setItemType(purchaseItem.getRequestItem().getItemType());
				flItemAddr.setScRequestItem(purchaseItem.getRequestItem());
				flItemAddr.setCmItem(purchaseItem.getRequestItem().getCmItem());
				//zonal & zonal tier & zonal promo & tier promo
				if(purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_STANDARD) 
						|| purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER) 
						|| purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION)
						|| purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER)){
					getManagedBean().setPmPriceType(purchaseItem.getPriceType());
					if(!getManagedBean().getScRequestSupplierItemIdMap().containsKey(purchaseItem.getRequestItem().getRequestNoteItemId())){
						getManagedBean().getScRequestSupplierItemIdMap().put(purchaseItem.getRequestItem().getRequestNoteItemId(), purchaseItem.getScRequestSupplierItem().getRequestSupplierItemId());
					}
				}
				//stand & stand promo & tier & tier promo
				if(purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_STANDARD) 
						|| purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_PROMOTION)
						|| purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER)
						|| purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_PROMOTION_TIER)){
					getManagedBean().setPmPriceType(purchaseItem.getPriceType());
					reqDTO.setRequestSupplierItemId(purchaseItem.getScRequestSupplierItem().getRequestSupplierItemId());
					reqDTO.setPriceType(purchaseItem.getPriceType());
					reqDTO.setOrderQuantity(new BigDecimal(1));
					purResDTO = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
					if(purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_STANDARD) 
						|| purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_PROMOTION)) {
						flItemAddr.setUnitPrice(purResDTO.getUnitPrice());
					} else {
						flItemAddr.setUnitPrice(new BigDecimal(0));
					}
					flItemAddr.setScSupplierItemPrice(purResDTO.getSupplierItemPrice());
					
				}else{//zonal & zonal tier & zonal promo & zonal tier promo
					flItemAddr.setUnitPrice(new BigDecimal(0));
					flItemAddr.setScSupplierItemPrice(null);
				}
				
				
				flItemAddr.setOrderedQty(new BigDecimal(0));
				flItemAddr.setOrderedAmt(new BigDecimal(0));
				allItemsList.add(flItemAddr);
			}
		}
		sortItems(allItemsList);
		getManagedBean().setRequestNoteItemList(allItemsList);
		getManagedBean().setShowAllItemsRows(allItemsList);
		
	}

	private boolean isPurchaseItemContains(ScRequestNoteDtl purchaseItem,
			List<FlFulfilmentItemAddr> showAllItemsRows) {
		boolean isContains=false;
		if(null==showAllItemsRows|| showAllItemsRows.isEmpty()){
			return false;
		}
		for(FlFulfilmentItemAddr flFulItemAddress:showAllItemsRows){
			if(purchaseItem.getRequestItem().getRequestNoteItemId()==flFulItemAddress.getScRequestItem().getRequestNoteItemId()){
				isContains=true;
			}
		}
		return isContains;
	}
	

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

	public void retrieveOrderAmount(FlFulfilmentItemAddr flItemAddr){
		IPurchaseRequestSO purchaseRequestSO = (IPurchaseRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,	IPurchaseRequestSO.class);
		PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();
		// Tier only
		if(flItemAddr.getScSupplierItemPrice() != null 
				&& getManagedBean().getPmPriceType() != null 
				&& (getManagedBean().getPmPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_TIER)
						|| getManagedBean().getPmPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_PROMOTION_TIER))){
			List<FlFulfilmentItemAddr> itemAddrList = null;
			Long key = null;
			BigDecimal sumOrderQty = new BigDecimal(0);
			
			FlFulfilmentItemAddr oriItemAddr = flItemAddr;
			
			if(flItemAddr.getOrderedQty() == null || flItemAddr.getOrderedQty().doubleValue() == 0){
				if(getManagedBean().getRequestNoteItemList().contains(flItemAddr)){
					return;
				}else{
					key = flItemAddr.getScRequestItem().getRequestNoteItemId();
					itemAddrList = getManagedBean().getTierMap().get(key);
				}
			}else{
				if(getManagedBean().getRequestNoteItemList().contains(flItemAddr)){
					FlFulfilmentItemAddr clonedlItemAddr = (FlFulfilmentItemAddr) flItemAddr.cloneEntity();
					flItemAddr = clonedlItemAddr;
				}
				key = flItemAddr.getScRequestItem().getRequestNoteItemId();
				if(getManagedBean().getTierMap().get(key) == null){
					itemAddrList = new ArrayList<FlFulfilmentItemAddr>();
				}else{
					itemAddrList = getManagedBean().getTierMap().get(key);
				}
			}

			boolean isExist = false;
			for(FlFulfilmentItemAddr itemAddr : itemAddrList){
				if(itemAddr.getFlDeliveryAddress() == getManagedBean().getSelectedDelAddr() 
						&& itemAddr.getScRequestItem().getRequestNoteItemId().longValue() == flItemAddr.getScRequestItem().getRequestNoteItemId()){

					isExist = true;
					break;
				}
			}
			if(!isExist){
				itemAddrList.add(flItemAddr);
			}
			if(flItemAddr.getOrderedQty() == null || flItemAddr.getOrderedQty().doubleValue() == 0){
				itemAddrList.remove(flItemAddr);
			}

			getManagedBean().getTierMap().put(key, itemAddrList);
			for(FlFulfilmentItemAddr itemAddr : itemAddrList){

				if(itemAddr.getOrderedQty() == null){
					itemAddr.setOrderedQty(new BigDecimal(0));
				}else{
					sumOrderQty = sumOrderQty.add(itemAddr.getOrderedQty());	
				}
			}
			reqDTO.setRequestSupplierItemId(flItemAddr.getScSupplierItemPrice().getScRequestSupplierItem().getRequestSupplierItemId());
			reqDTO.setPriceType(getManagedBean().getPmPriceType());
			reqDTO.setPmAddress(getManagedBean().getSelectedDelAddr().getPmAddress());
			reqDTO.setOrderQuantity(sumOrderQty);
			PurchaseRequestResDTO purResDTO = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
//			flItemAddr.setScSupplierItemPrice(purResDTO.getSupplierItemPrice());
			for(FlFulfilmentItemAddr itemAddr : itemAddrList){
				itemAddr.setUnitPrice(purResDTO.getUnitPrice());
				itemAddr.setOrderedAmt(itemAddr.getOrderedQty().multiply(itemAddr.getUnitPrice()));
			}
			if(oriItemAddr.getOrderedQty() == null){
				oriItemAddr.setOrderedQty(new BigDecimal(0));
			}
			oriItemAddr.setUnitPrice(purResDTO.getUnitPrice());
			oriItemAddr.setOrderedAmt(oriItemAddr.getOrderedQty().multiply(oriItemAddr.getUnitPrice()));
			if(sumOrderQty.intValue() > 0 && purResDTO.getUnitPrice().intValue() == 0){
				BigDecimal reCalSumOrderQty = new BigDecimal(0);
				flItemAddr.setOrderedQty(new BigDecimal(0));
				for(FlFulfilmentItemAddr itemAddr : itemAddrList){
					reCalSumOrderQty = reCalSumOrderQty.add(itemAddr.getOrderedQty());	
				}
				reqDTO.setRequestSupplierItemId(flItemAddr.getScSupplierItemPrice().getScRequestSupplierItem().getRequestSupplierItemId());
				reqDTO.setPriceType(getManagedBean().getPmPriceType());
				reqDTO.setPmAddress(getManagedBean().getSelectedDelAddr().getPmAddress());
				reqDTO.setOrderQuantity(reCalSumOrderQty);
				PurchaseRequestResDTO reCalculateUnitPrice = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
				for(FlFulfilmentItemAddr itemAddr : itemAddrList){
					itemAddr.setUnitPrice(reCalculateUnitPrice.getUnitPrice());
					itemAddr.setOrderedAmt(itemAddr.getOrderedQty().multiply(itemAddr.getUnitPrice()));
				}
				itemAddrList.remove(flItemAddr);
				throw new EpException("this item is out of the tier range.");
			}

			flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
			// add  or remove items to Selected delivery address
			itemsToSelectedAddress(flItemAddr);
		}else if(getManagedBean().getPmPriceType() != null 
				&& (getManagedBean().getPmPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_TIER)
						|| getManagedBean().getPmPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER))){
			
			List<FlFulfilmentItemAddr> itemAddrList = null;
			String key = null;
			BigDecimal sumOrderQty = new BigDecimal(0);
			
			reqDTO.setPriceType(getManagedBean().getPmPriceType());
			if(getManagedBean().getScRequestSupplierItemIdMap().get(flItemAddr.getScRequestItem().getRequestNoteItemId()) != null){
				reqDTO.setRequestSupplierItemId(getManagedBean().getScRequestSupplierItemIdMap().get(flItemAddr.getScRequestItem().getRequestNoteItemId()));
			}

			reqDTO.setPmAddress(getManagedBean().getSelectedDelAddr().getPmAddress());
			PurchaseRequestResDTO zoneIdResDTO = purchaseRequestSO.getSupplierItemZoneIdByPmAddress(reqDTO);
			flItemAddr.setZoneId(zoneIdResDTO.getSpecZoneId());
			
			FlFulfilmentItemAddr oriItemAddr = flItemAddr;
			
			if(flItemAddr.getOrderedQty() == null || flItemAddr.getOrderedQty().doubleValue() == 0){
				if(getManagedBean().getRequestNoteItemList().contains(flItemAddr)){
					return;
				}else{
					key = flItemAddr.getZoneId()+" "+flItemAddr.getScRequestItem().getRequestNoteItemId();
					itemAddrList = getManagedBean().getZonalTierMap().get(key);
				}
			}else{
				if(getManagedBean().getRequestNoteItemList().contains(flItemAddr)){
					FlFulfilmentItemAddr clonedlItemAddr = (FlFulfilmentItemAddr) flItemAddr.cloneEntity();
					flItemAddr = clonedlItemAddr;
				}
				key = flItemAddr.getZoneId()+" "+flItemAddr.getScRequestItem().getRequestNoteItemId();
				if(getManagedBean().getZonalTierMap().get(key) == null){
					itemAddrList = new ArrayList<FlFulfilmentItemAddr>();
				}else{
					itemAddrList = getManagedBean().getZonalTierMap().get(key);
				}
			}

			boolean isExist = false;
			for(FlFulfilmentItemAddr itemAddr : itemAddrList){
				if(itemAddr.getFlDeliveryAddress() == getManagedBean().getSelectedDelAddr() 
						&& itemAddr.getScRequestItem().getRequestNoteItemId().longValue() == flItemAddr.getScRequestItem().getRequestNoteItemId()){

					isExist = true;
					break;
				}
			}
			if(!isExist){
				itemAddrList.add(flItemAddr);
			}
			if(flItemAddr.getOrderedQty() == null || flItemAddr.getOrderedQty().doubleValue() == 0){
				itemAddrList.remove(flItemAddr);
			}

			getManagedBean().getZonalTierMap().put(key, itemAddrList);
			for(FlFulfilmentItemAddr itemAddr : itemAddrList){

				if(itemAddr.getZoneId() == null){
					oriItemAddr.setOrderedQty(new BigDecimal(0));
					flItemAddr.setOrderedQty(new BigDecimal(0));
					throw new EpException("this item is not within the zone.");

				}
				if(itemAddr.getOrderedQty() == null){
					itemAddr.setOrderedQty(new BigDecimal(0));
				}else{
					sumOrderQty = sumOrderQty.add(itemAddr.getOrderedQty());	
				}
			}
			if(getManagedBean().getScRequestSupplierItemIdMap().get(flItemAddr.getScRequestItem().getRequestNoteItemId()) != null){
				reqDTO.setRequestSupplierItemId(getManagedBean().getScRequestSupplierItemIdMap().get(flItemAddr.getScRequestItem().getRequestNoteItemId()));
			}
			reqDTO.setPriceType(getManagedBean().getPmPriceType());
			reqDTO.setPmAddress(getManagedBean().getSelectedDelAddr().getPmAddress());
			reqDTO.setOrderQuantity(sumOrderQty);
			PurchaseRequestResDTO purResDTO = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
			flItemAddr.setScSupplierItemPrice(purResDTO.getSupplierItemPrice());
			for(FlFulfilmentItemAddr itemAddr : itemAddrList){
				itemAddr.setUnitPrice(purResDTO.getUnitPrice());
				itemAddr.setOrderedAmt(itemAddr.getOrderedQty().multiply(itemAddr.getUnitPrice()));
			}
			if(oriItemAddr.getOrderedQty() == null){
				oriItemAddr.setOrderedQty(new BigDecimal(0));
			}
			oriItemAddr.setUnitPrice(purResDTO.getUnitPrice());
			oriItemAddr.setOrderedAmt(oriItemAddr.getOrderedQty().multiply(oriItemAddr.getUnitPrice()));
			if(sumOrderQty.intValue() > 0 && purResDTO.getUnitPrice().intValue() == 0){
				BigDecimal reCalSumOrderQty = new BigDecimal(0);
				flItemAddr.setOrderedQty(new BigDecimal(0));
				for(FlFulfilmentItemAddr itemAddr : itemAddrList){
					reCalSumOrderQty = reCalSumOrderQty.add(itemAddr.getOrderedQty());	
				}
				if(getManagedBean().getScRequestSupplierItemIdMap().get(flItemAddr.getScRequestItem().getRequestNoteItemId()) != null){
					reqDTO.setRequestSupplierItemId(getManagedBean().getScRequestSupplierItemIdMap().get(flItemAddr.getScRequestItem().getRequestNoteItemId()));
				}
				reqDTO.setPriceType(getManagedBean().getPmPriceType());
				reqDTO.setPmAddress(getManagedBean().getSelectedDelAddr().getPmAddress());
				reqDTO.setOrderQuantity(reCalSumOrderQty);
				PurchaseRequestResDTO reCalculateUnitPrice = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
				for(FlFulfilmentItemAddr itemAddr : itemAddrList){
					itemAddr.setUnitPrice(reCalculateUnitPrice.getUnitPrice());
					itemAddr.setOrderedAmt(itemAddr.getOrderedQty().multiply(itemAddr.getUnitPrice()));
				}
				itemAddrList.remove(flItemAddr);
				//					getManagedBean().getZonalTierMap().put(flItemAddr.getZoneId()+" "+flItemAddr.getScRequestItem().getRequestNoteItemId(), itemAddrList);
				throw new EpException("this item is out of the tier range.");
			}

			flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
			// add  or remove items to Selected delivery address
			itemsToSelectedAddress(flItemAddr);
		}else{ // zonal & standard & zonal promo & standard promo
			FlFulfilmentItemAddr oriItemAddr = flItemAddr;
			if(getManagedBean().getRequestNoteItemList().contains(flItemAddr)){
				FlFulfilmentItemAddr clonedlItemAddr = (FlFulfilmentItemAddr) flItemAddr.cloneEntity();
				flItemAddr = clonedlItemAddr;
			}			
			if(getManagedBean().getPmPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_STANDARD) 
					|| getManagedBean().getPmPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION)){
				if(flItemAddr.getZoneId() == null){
					oriItemAddr.setOrderedQty(new BigDecimal(0));
					flItemAddr.setOrderedQty(new BigDecimal(0));
					throw new EpException("this item is not within the zone.");
				}
			}
			if(flItemAddr.getOrderedQty() == null){
				flItemAddr.setOrderedQty(new BigDecimal(0));
			}
			flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
			flItemAddr.setOrderedAmt(flItemAddr.getOrderedQty().multiply(flItemAddr.getUnitPrice()));
			if(oriItemAddr.getOrderedQty() == null){
				oriItemAddr.setOrderedQty(new BigDecimal(0));
			}
			oriItemAddr.setUnitPrice(flItemAddr.getUnitPrice());
			oriItemAddr.setOrderedAmt(oriItemAddr.getOrderedQty().multiply(oriItemAddr.getUnitPrice()));
			// add  or remove items to Selected delivery address
			itemsToSelectedAddress(flItemAddr);
		}
		populateItemsForSelectedAddress();
	}
	
	private void itemsToSelectedAddress(FlFulfilmentItemAddr flItemAddr) {
		if(null== flItemAddr.getOrderedQty() || flItemAddr.getOrderedQty().doubleValue()==0) {
			for(FlFulfilmentItemAddr item:getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()){
				if(flItemAddr.getScRequestItem().getRequestNoteItemId().longValue()==item.getScRequestItem().getRequestNoteItemId().longValue()){
					getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().remove(item);
					break;
				}
			}	
		}else{
			boolean itemExisted=false;
			for (FlFulfilmentItemAddr selectedItem : getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()) {
				if (selectedItem.getScRequestItem().getRequestNoteItemId().equals(flItemAddr.getScRequestItem().getRequestNoteItemId())) {
					selectedItem.setOrderedAmt(flItemAddr.getOrderedAmt());
					selectedItem.setOrderedQty(flItemAddr.getOrderedQty());
					itemExisted=true;
					break;
				}
			}
			if(!itemExisted){
				// Check is it FlItemAddr COA set, if yes, set it for this new item
				for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()) {
					if(flDeliveryAddress!=getManagedBean().getSelectedDelAddr()) {
						for(FlFulfilmentItemAddr itemAddr : flDeliveryAddress.getFlFulfilmentItemAddr()) {
							if(itemAddr.getScRequestItem().getRequestNoteItemId().longValue()==flItemAddr.getScRequestItem().getRequestNoteItemId().longValue() 
								&& itemAddr.getUnitPrice().doubleValue()==flItemAddr.getUnitPrice().doubleValue()
								&& itemAddr.getPmVotFund()!=null) {
								flItemAddr.setPmVotFund(itemAddr.getPmVotFund());
								flItemAddr.setPmPrgActivity(itemAddr.getPmPrgActivity());
								flItemAddr.setPmSubSetia(itemAddr.getPmSubSetia());
								flItemAddr.setAssetInd(itemAddr.getAssetInd());
								flItemAddr.setPmGlAccount(itemAddr.getPmGlAccount());
								break;
							}
						}
					}
				}
				getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().add(flItemAddr);
			}
		}
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
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		orgProfileReqDTO.setOrgProfileId(4L);// set current login user org profile ID
		OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileByProfileId(orgProfileReqDTO);
		this.getManagedBean().setOutComes(new String[] { "Submit" });
		return NAV_TO_CREATE_PURCHASE_REQUEST;
	}

	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		getLoginUserOrg();
		
		if (taskId != null) {
			renewSesionObject();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();

			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			FLPurchaseOrderDataType flPurchaseOrd = (FLPurchaseOrderDataType) obj;
			
			String[] outcome = new String[res.getAction().length + 1];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			int i = 1;
			for (String action : res.getAction()) {
				outcome[i++] = action;
			}
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(outcome);
			getManagedBean().setTaskName(res.getTaskName());
			if (null != res.getAction() && !"".equals(res.getAction())) {
				getManagedBean().setSubmitButtonStatus(false);
			}
			
			IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			flReqDTO.setFlRequestId(flPurchaseOrd.getOrderDetails().getRequestId().longValue());
			FulfilmentResDTO resDTO = fulfilmentSO.retrieveFulfilmentRequest(flReqDTO);
			
			setFulfilmentRequestField(resDTO);
			initializeApproverGroup();
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "prBasePage.xhtml?faces-redirect=true");
	}

	public void calculateSubTotal(BigDecimal quantity){
		getManagedBean().getSelectedItemAddr().setSubTotal(quantity);
	}
	
	private void initializeApproverGroup() {
		IFulfilmentRequestSO requestSO = null;
		try {
			if(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId() != getManagedBean().getCurrentPreparedOrgId()){
				requestSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
				FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
				FulfilmentResDTO resDTO = new FulfilmentResDTO();
				reqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId());
				resDTO = requestSO.retrieveGroupListByOrgId(reqDTO);
				
				if(getManagedBean().getFulfilmentRequest().getPmUserGroup()==null) {
					PmUserGroup pmUserGroup = new PmUserGroup();
					getManagedBean().getFulfilmentRequest().setPmUserGroup(pmUserGroup);
				}
				if(getManagedBean().getApproverModel()==null) {
					List<String> source = new ArrayList<String>(); 
					List<String> target = new ArrayList<String>();  
					getManagedBean().setApproverModel(new DualListModel<String>(source, target));
				}			
				getManagedBean().getUserGroupList().clear();
				
				//new
				getManagedBean().setUserGroupList(resDTO.getUserGroupList());
				if(resDTO.getUserGroupList().size()>0) {
					reqDTO.setGroupId(getManagedBean().getUserGroupList().get(0).getUserGroupId());
					resDTO = requestSO.retrieveApproverListByGroupId(reqDTO);
					List<String> source = new ArrayList<String>();
					
					for(PmUserGroupUser groupUser : resDTO.getApproverGroupUserList()) {
							getManagedBean().getApproverMap().put(groupUser.getPmUser().getUserName(), groupUser);
							if(getManagedBean().getApproverModel().getSource().size()==0) {
								getManagedBean().getApproverModel().setSource(source);
							}
							source.add(groupUser.getPmUser().getUserName());
						}
					getManagedBean().getFulfilmentRequest().getPmUserGroup().setUserGroupId(getManagedBean().getUserGroupList().get(0).getUserGroupId());
				}
				
				//end
				getManagedBean().setCurrentPreparedOrgId(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			requestSO = null;
		}
	}
	
	public SelectItem[] getUserGroupDropdown() {
		SelectItem[] items = null;
		if(getManagedBean().getFulfilmentRequest().getPmUserGroup()==null) {
			getManagedBean().getFulfilmentRequest().setPmUserGroup(new PmUserGroup());
		}
		items = new SelectItem[getManagedBean().getUserGroupList().size()];
		int i = 0;
		for(PmUserGroup userGroup : getManagedBean().getUserGroupList()) {
			items[i++] = new SelectItem(userGroup.getUserGroupId(), userGroup.getGroupName());
		}
	    return items;
	}
	
	public void userGroupChangeListener() {
		IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		reqDTO.setGroupId(getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId());
		try {
			resDTO = requestSO.retrieveApproverListByGroupId(reqDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<String> source = new ArrayList<String>(); 
        List<String> target = new ArrayList<String>();  
		for(PmUserGroupUser groupUser : resDTO.getApproverGroupUserList()) {
			source.add(groupUser.getPmUser().getUserName());
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
		return facesScopes.hasRoleUser(RoleCode.FL_APPROVER.value());
	}
	
	private void getLoginUserOrg(){
		PmUser user = getFacesScopes().getUserSessionBean();
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(user.getUserId());
		IUserSO userSO = (IUserSO ) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO .class);
		UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
		PmUserOrg userOrg = resDTO.getUserOrgList().get(0);
		getManagedBean().setPmUserOrg(userOrg);
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
	
	public void editManualAddress(FlDeliveryAddress flDelAddress){
		//retrieveCountryList();
		getManagedBean().setEmailValidate(false);
		getManagedBean().setFlManulaAddress(flDelAddress);
		if(getManagedBean().getFlManulaAddress().getPmAddress().getPmDivision()==null) {
			getManagedBean().getFlManulaAddress().getPmAddress().setPmDivision(new PmDivision());
		}
		retrieveCountryList();
		
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(flDelAddress.getPmAddress().getPmCountry().getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStateList(resDTO.getStateList());
		
		DistrictReqDTO districtReqDTO = new DistrictReqDTO();
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
		
		if(getManagedBean().getFlManulaAddress().getPmAddress().getPmDivision().getDivisionId()!=null) {
			DivisionReqDTO divReqDTO= new DivisionReqDTO();
			divReqDTO.setStateId(flDelAddress.getPmAddress().getPmState().getStateId());
			IDivisionSO divisionSO = (IDivisionSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDivisionSO.class);
			DivisionResDTO divResDTO = divisionSO.retriveDivisionListByState(divReqDTO);
			getManagedBean().setDivisionList(divResDTO.getDivisionList());
			
			districtReqDTO.setDivisionId(flDelAddress.getPmAddress().getPmDivision().getDivisionId());
			DistrictResDTO districtResDTO = districtSO.retriveDistrictListByDivision(districtReqDTO);
			getManagedBean().setDistrictList(districtResDTO.getDistrictList());		
		} else {
			districtReqDTO.setStateId(flDelAddress.getPmAddress().getPmState().getStateId());
			DistrictResDTO districtResDTO = districtSO.retriveDistrictListByStateID(districtReqDTO);
			getManagedBean().setDistrictList(districtResDTO.getDistrictList());			
		}
		
		CityReqDTO cityReqDTO = new CityReqDTO();
		cityReqDTO.setDistrictId(flDelAddress.getPmAddress().getPmDistrict().getDistrictId());
		ICitySO citySO = (ICitySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);
		CityResDTO cityResDTO = citySO.retriveCityListByDistrictId(cityReqDTO);
		this.getManagedBean().setCityList(cityResDTO.getCityList());
		
		if(getManagedBean().getManualAddressMap() == null || getManagedBean().getManualAddressMap().size() == 0){
			setManualAddressList();
		}
		getManagedBean().setManualAddressList(getManagedBean().getManualAddressMap().get(flDelAddress.getPmAddress().getPmDistrict().getDistrictId()));
		List<SelectItem> manualAddrList = new ArrayList<SelectItem>();
		if(getManagedBean().getManualAddressList()!=null) {
			for (PmAddressType obj : getManagedBean().getManualAddressList()) {
				manualAddrList.add(new SelectItem(obj.getAddressTypeId(), obj.getPmAddress().getAddressName()));
			}
		}
		getManagedBean().setManualAddrList(manualAddrList);
		
		flDelAddress.setEditable(true);
	}
	
	public void receivingOfficerList(FlDeliveryAddress flDelAddress){
		Long addressId = flDelAddress.getPmAddress().getAddressId();
		if(flDelAddress.getReceivingGrp()!=null && flDelAddress.getReceivingGrp().getAddressTypeId()!=null) { // in case of Manual address
			IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setAddressTypeId(flDelAddress.getReceivingGrp().getAddressTypeId());
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			
			addressId = resDTO.getAddressTypeList().get(0).getPmAddress().getAddressId();
		}
		AddressResDTO resDTO=getReceivingOfficerList(addressId, AddressType.RECEIVING_OFFICER_ADDRESS.value());
		getManagedBean().setReceivingOffList(resDTO.getAddressTypeList());
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
		
		if(resDTO.getDivisionList().size()==0) {
			DistrictReqDTO districtReqDTO = new DistrictReqDTO();
			districtReqDTO.setStateId(stateId);
			IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
			DistrictResDTO districtResDTO = districtSO.retriveDistrictListByStateID(districtReqDTO);
			getManagedBean().setDistrictList(districtResDTO.getDistrictList());			
		}
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
		getManagedBean().setChargeLinePanel(false);
		TreeNode selectedNode=getManagedBean().getSelectedNode();
		onNodeUnSelect();
		if(null!=selectedNode){
			for(TreeNode children:selectedNode.getChildren()){
				FlFulfilmentItemAddr childData=((FlFulfilmentItemAddr)children.getData());
				if(null !=childData.getPmVotFund()){
					if(VotFundType.AMANAH.value().equalsIgnoreCase(childData.getPmVotFund().getVotFundType())){
						getManagedBean().getPmVoteFund().setVotFundCode(childData.getPmVotFund().getVotFundCode());
						getManagedBean().getPmVoteFund().setVotFundId(childData.getPmVotFund().getVotFundId());
					}else{
						getManagedBean().setVotFundCode(childData.getPmVotFund().getVotFundCode());
						getManagedBean().setVotFundId(childData.getPmVotFund().getVotFundId());
					}
				}
				
				if(childData.getAssetInd()!=null) {
					getManagedBean().setAssetIndCode(childData.getAssetInd().getParameterCode());
					getManagedBean().setAssetIndId(childData.getAssetInd().getParameterId());
				}
					
				if(childData.getPmGlAccount()!=null) {
					getManagedBean().setGosodo(childData.getPmGlAccount());
				}
				
				if(childData.getPmPrgActivity()!=null) {
					getManagedBean().getPmPrgActivity().setPrgActivityCode(childData.getPmPrgActivity().getPrgActivityCode());
				}
				
				if(childData.getPmSubSetia()!=null) {
					getManagedBean().setPmSubSetia(childData.getPmSubSetia());
				}
			}
		}
	}
	
	public void onNodeUnSelect() {
		getManagedBean().setProgramLov(false);
		getManagedBean().setProjectLov(false);
		getManagedBean().setChargeLinePanel(false);
		getManagedBean().setVotFundCode("");
		getManagedBean().setVotFundId(0L);
		getManagedBean().setPmVoteFund(new PmVotFund());
		getManagedBean().setPmSubSetia(new PmSubSetia());
		getManagedBean().getPmSubSetia().setPmSetia(new PmSetia());
		getManagedBean().getPmSubSetia().getPmSetia().setPmProject(new PmProject());
		getManagedBean().setPmPrgActivity(new PmPrgActivity());
		getManagedBean().setGosodo(new PmGlAccount());
		getManagedBean().setAssetIndCode("");
		getManagedBean().setAssetIndId(0L);
		getManagedBean().setGlAccountCode("");
		getManagedBean().setPrgActivityCode("");
		getManagedBean().setPrgActivityId(0L);
		getManagedBean().setProjectCode("");
		getManagedBean().setSetiaCode("");
		getManagedBean().setSubSetiaCode("");
		getManagedBean().setFinancingTypeCode("");		
	}
	
	public void clearPopupLists(){
		updateSelectedTreeNodes();
		if(getManagedBean().getSelectedNodes().length==0) {
			throw new EpException(MessageConst.ERROR_100049);
		}
		getManagedBean().setVotFundId(0L);
		getManagedBean().setAssetIndId(null);
		getManagedBean().setGlAccountId(null);
		getManagedBean().setPrgActivityId(0L);
		getManagedBean().setProjectId(0L);
		getManagedBean().setSetiaId(0L);
		getManagedBean().setSubSetiaId(0L);
		getManagedBean().setFinancingTypeCode(null);
		getManagedBean().setProjectList(new ArrayList<PmProject>());
		getManagedBean().setPrgActivityList(new ArrayList<PmPrgActivity>());
		getManagedBean().setSetiaList(new ArrayList<PmSetia>());
		getManagedBean().setSubSetiaList(new ArrayList<PmSubSetia>());
		
		RequestContext.getCurrentInstance().execute("applyBulkCharge.show()");
	}
	
	public void selectAllCheckboxSelect(AjaxBehaviorEvent e){
		//boolean checkValue=(boolean)e.getComponent().getAttributes().get("value")
		if((boolean) e.getComponent().getAttributes().get("value")){
			for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
				if(null!=deliveryAddress){
					deliveryAddress.setSelectedAddressRowDelete(true);
				}
			}
			getManagedBean().setDeleteButtonHide(false);
			
		}else{
			for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
				if(null!=deliveryAddress){
					deliveryAddress.setSelectedAddressRowDelete(false);
				}
			}
			getManagedBean().setDeleteButtonHide(true);
			
		}
	}
	
	public void chargeLineApply() {
		IParameterSO parameterSO = null;
		try {
			if(null == getManagedBean().getVotFundId()){
				getManagedBean().setVotFundId(getManagedBean().getPmVoteFund().getVotFundId());
			}
			if(null != getManagedBean().getGosodo()){
				getManagedBean().setGlAccountId(getManagedBean().getGosodo().getGlAccountId());
			}
			if(null != getManagedBean().getPmPrgActivity()){
				getManagedBean().setPrgActivityId(getManagedBean().getPmPrgActivity().getPrgActivityId());
			}
			if(null != getManagedBean().getPmSubSetia()){
				getManagedBean().setSubSetiaId(getManagedBean().getPmSubSetia().getSubSetiaId());
				getManagedBean().setSetiaId(getManagedBean().getPmSubSetia().getPmSetia().getSetiaId());
				getManagedBean().setProjectId(getManagedBean().getPmSubSetia().getPmSetia().getPmProject().getProjectId());
			}
			
			chargeLineValidation();
			
			PmVotFund pmVotFund = votFundCodeMap.get(getManagedBean().getVotFundId());
			PmGlAccount pmGlAccount = getManagedBean().getGosodo();
			PmPrgActivity pmPrgActivity = getManagedBean().getPmPrgActivity();
			PmSubSetia pmSubSetia = getManagedBean().getPmSubSetia();
			if(pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.AMANAH.value())) {
				pmPrgActivity = null;
				pmSubSetia = null;
			} else if(VotFundType.BEKALAN.value().equalsIgnoreCase(pmVotFund.getVotFundType()) || VotFundType.TANGGUNGAN.value().equalsIgnoreCase(pmVotFund.getVotFundType())){
				pmSubSetia = null;
			} else if(VotFundType.PEMBANGUNAN.value().equalsIgnoreCase(pmVotFund.getVotFundType()) || VotFundType.SETIA.value().equalsIgnoreCase(pmVotFund.getVotFundType())){
				pmPrgActivity = null;
			}			
			
			ParameterReqDTO paramReqDTO = new ParameterReqDTO();
			paramReqDTO.setParameterId(getManagedBean().getAssetIndId());
			parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			PmParameter assetInd = parameterSO.retrieveParameterById(paramReqDTO).getPmParameter();
			updateAllItemsWithChargeDrops(); // set COA to Y
			FlFulfilmentItemAddr childItemAddress = null;
			boolean selected=false;
			for(TreeNode selectedNode : getManagedBean().getSelectedNodes()){
				//code for selecting the treenode from popupmenu
				if(!selected){
					getManagedBean().setSelectedNode(selectedNode);
					selected=true;
				}
				for(TreeNode children:selectedNode.getChildren()){
					childItemAddress = (FlFulfilmentItemAddr)children.getData();
					childItemAddress.setPmVotFund(pmVotFund);
					childItemAddress.setAssetInd(assetInd);
					childItemAddress.setPmGlAccount(pmGlAccount);
					childItemAddress.setPmPrgActivity(pmPrgActivity);
					childItemAddress.setPmSubSetia(pmSubSetia);
					childItemAddress.setVotFundDisplay(getManagedBean().isVotFundDisplay());
				}
				childItemAddress = (FlFulfilmentItemAddr)selectedNode.getData();
				
				//Code for multiple delivery addresses based on item code
				for (FlDeliveryAddress delAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()) {
					for (FlFulfilmentItemAddr itemDelAddress : delAddress.getFlFulfilmentItemAddr()) {
						if (itemDelAddress.getScRequestItem().getRequestNoteItemId().longValue()==childItemAddress.getScRequestItem().getRequestNoteItemId().longValue()) {
							itemDelAddress.setPmVotFund(pmVotFund);
							itemDelAddress.setAssetInd(assetInd);
							itemDelAddress.setPmGlAccount(pmGlAccount);
							itemDelAddress.setPmPrgActivity(pmPrgActivity);
							itemDelAddress.setPmSubSetia(pmSubSetia);
						}
					}
				}
			}
			populateChargeLineData(true);
			onNodeSelect();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
					MessageUtils.getMessage(MessageConst.INFO_745052, new String[]{}, 
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			
		} finally {
			parameterSO = null;
		}
	}
	
	private void populateChargeLineData(boolean retainPreviousSelection) {
		getManagedBean().setChargeLineItemRoot(new DefaultTreeNode("root", null));
		Map<Long, List<FlFulfilmentItemAddr>> chargeLineItemsMap = new HashMap<Long, List<FlFulfilmentItemAddr>>();
		
		//Map<String, List<FlFulfilmentItemAddr>> chargeLineItemsMap = new TreeMap<String, List<FlFulfilmentItemAddr>>();
		for(FlDeliveryAddress  deliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddress : deliveryAddress.getFlFulfilmentItemAddr()){
				itemAddress.setChargeOrderedAmt(itemAddress.getOrderedAmt());
				itemAddress.setChargeOrderedQty(itemAddress.getOrderedQty());
				itemAddress.setChargeUnitPrice(itemAddress.getUnitPrice());
				if(chargeLineItemsMap.containsKey(itemAddress.getScRequestItem().getRequestNoteItemId())){
					//code for calculating duplicate charge line order amount and quantity for same unit price  Condition for checking unit price same.
					boolean notContained=true;
					for(FlFulfilmentItemAddr containedItem : chargeLineItemsMap.get(itemAddress.getScRequestItem().getRequestNoteItemId())){
						if(containedItem.getChargeUnitPrice().equals(itemAddress.getUnitPrice())){
							containedItem.setChargeOrderedAmt( containedItem.getChargeOrderedAmt().add(itemAddress.getOrderedAmt()));
							containedItem.setChargeOrderedQty(containedItem.getChargeOrderedQty().add(itemAddress.getOrderedQty()));
							notContained=false;
						}
					}
					if(notContained){
						chargeLineItemsMap.get(itemAddress.getScRequestItem().getRequestNoteItemId()).add(itemAddress);
					}
				} else {
					List<FlFulfilmentItemAddr> itemAddressList=new ArrayList<FlFulfilmentItemAddr>();
					itemAddressList.add(itemAddress);
					chargeLineItemsMap.put(itemAddress.getScRequestItem().getRequestNoteItemId(),itemAddressList);
				}
			}
		}
		
		FlFulfilmentItemAddr flItemAddress = null;
		TreeNode childItemNode = null;
		BigDecimal totalOrderAmt = null;
		BigDecimal grandTotalOrderAmt=new BigDecimal(0);
		getManagedBean().setOrderedAmtGrandTotal(grandTotalOrderAmt);
		int i=0;
		for(Entry<Long, List<FlFulfilmentItemAddr>> itemEntry : chargeLineItemsMap.entrySet()){
			flItemAddress = (FlFulfilmentItemAddr) itemEntry.getValue().get(0).cloneEntity();
			flItemAddress.setParent(true);
			if(null != flItemAddress.getPmVotFund() && null != flItemAddress.getPmVotFund().getVotFundId()){
				flItemAddress.setCoaValue("Y");
			}else{
				flItemAddress.setCoaValue("N");
			}
			
			//Do create New Item Address and copy the first element data manually.
			childItemNode = new DefaultTreeNode(flItemAddress.getScRequestItem().getRequestNoteItemId().toString(), flItemAddress,getManagedBean().getChargeLineItemRoot());
			totalOrderAmt = new BigDecimal(0);
			
			if(retainPreviousSelection){
				if(childItemNode.getType().equals(getManagedBean().getSelectedNode().getType())){
					retainPreviousSelection = false;
					getManagedBean().setSelectedNode(childItemNode);
					childItemNode.setSelected(true);
					i++;
				}
			} else if(i==0) {
				getManagedBean().setSelectedNode(childItemNode);
				childItemNode.setSelected(true);
				i++;
			}
			List<FlFulfilmentItemAddr> itemAddress=itemEntry.getValue();
			for(FlFulfilmentItemAddr itemAddr : itemAddress) {
				new DefaultTreeNode(itemAddr.getScRequestItem().getRequestNoteItemId().toString(), itemAddr, childItemNode);
				totalOrderAmt = totalOrderAmt.add(itemAddr.getChargeOrderedAmt());
			}
			flItemAddress.setChargeOrderedAmt(totalOrderAmt);
			grandTotalOrderAmt = grandTotalOrderAmt.add(totalOrderAmt);
		}
		getManagedBean().setOrderedAmtGrandTotal(grandTotalOrderAmt);
	}

	private void chargeLineValidation() {
		if(getManagedBean().getVotFundId()==null) {
			throw new EpException(MessageConst.ERROR_100022, new String[]{"VOT Code"});
		}
		if(getManagedBean().getAssetIndId() == null ){
			throw new EpException(MessageConst.ERROR_100022, new String[]{"Asset Ind"});
		}
		if(getManagedBean().getGlAccountId() == null ){
			throw new EpException(MessageConst.ERROR_100022, new String[]{"GOSODO"});
		}		
		
		List<String> selectedItemAddressList = new ArrayList<String>();
		updateSelectedTreeNodes();
		FlFulfilmentItemAddr flTemp = null;
		for(TreeNode selectedNode : getManagedBean().getSelectedNodes()){
			flTemp = (FlFulfilmentItemAddr)selectedNode.getData();
			selectedItemAddressList.add(flTemp.getScRequestItem().getRequestNoteItemId()+ " "+flTemp.getUnitPrice().doubleValue());
		}
		
		boolean isVotSOrAmanahExist = false;
		boolean isVotBOrPOrTExist = false;
		String votFundType = null;
		for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddress:deliveryAddress.getFlFulfilmentItemAddr()){
				if(selectedItemAddressList.contains(itemAddress.getScRequestItem().getRequestNoteItemId()+ " "+itemAddress.getUnitPrice().doubleValue())) {
					continue;
				}
				if(itemAddress.getPmVotFund()!=null) {
					if(null == votFundCodeMap.get(itemAddress.getPmVotFund().getVotFundId())){
						votFundType=itemAddress.getPmVotFund().getVotFundType();
					}else{
						votFundType = votFundCodeMap.get(itemAddress.getPmVotFund().getVotFundId()).getVotFundType();
					}
					if(votFundType.equalsIgnoreCase(VotFundType.SETIA.value()) || votFundType.equalsIgnoreCase(VotFundType.AMANAH.value())) {
						isVotSOrAmanahExist = true;
						break;
					} else {
						isVotBOrPOrTExist = true;
						break;
					}
				}
			}
			if(isVotSOrAmanahExist || isVotBOrPOrTExist) {
				break;
			}
		}		
		PmVotFund pmVotFund = votFundCodeMap.get(getManagedBean().getVotFundId());
		if(pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.BEKALAN.value()) || 
			pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.TANGGUNGAN.value()) ||
			pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.PEMBANGUNAN.value())) {
			if(isVotSOrAmanahExist) {
				throw new EpException(MessageConst.ERROR_740005);
			}
		} else {
			if(isVotBOrPOrTExist) {
				throw new EpException(MessageConst.ERROR_740005);
			}
		}
		if(pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.BEKALAN.value()) || pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.TANGGUNGAN.value())) {
			if(getManagedBean().getPrgActivityId()==null) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{"Program / Activity"});
			}
		} else if(pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.PEMBANGUNAN.value()) || pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.SETIA.value())) {
			if(getManagedBean().getProjectId()==null) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{"Project"});
			}
			if(getManagedBean().getSetiaId()==null) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{"Setia"});
			}
			if(getManagedBean().getSubSetiaId()==null) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{"Sub Setia"});
			}			
		}
	}
	
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

	private void updateAllItemsWithChargeDrops() {
		updateSelectedTreeNodes();
		if(getManagedBean().getSelectedNodes().length==0) {
			throw new EpException(MessageConst.ERROR_100049);
		}
		
		for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddress:deliveryAddress.getFlFulfilmentItemAddr()){
				for(TreeNode node:getManagedBean().getSelectedNodes()){
					FlFulfilmentItemAddr selectedItem=(FlFulfilmentItemAddr)node.getData();
					if(itemAddress.getScRequestItem().getRequestNoteItemId().equals(selectedItem.getScRequestItem().getRequestNoteItemId())){
						itemAddress.setCoaValue("Y");
						break;
					}
				}
			}
		}
	}

	public void showItemQuery(FlFulfilmentItemAddr flItemAddress){
		getManagedBean().setQueryDisplay(false);
		flItemAddress.getScRequestItem().getItemName();
		setQueryItemCode(flItemAddress.getScRequestItem().getRequestNoteItemId().toString());
		setQueryItemName(flItemAddress.getScRequestItem().getItemName());
		if(null != flItemAddress.getFlFulfilmentItem().getFlQuery()){
			getManagedBean().setQueryDisplay(true);
			setQueryDesc(flItemAddress.getFlFulfilmentItem().getFlQuery().getQueryDesc());
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
	
	public void getVotFundList(Long profileId){
		IFulfilmentSO fulfilmentSO = (IFulfilmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setOrgProfileId(profileId);
		FulfilmentResDTO votFundRes = fulfilmentSO.retrieveVotFundListByOrgId(reqDTO);
		
		for (PmVotFund pmVotFund : votFundRes.getVotFundList()) {
			votFundCodeMap.put(pmVotFund.getVotFundId(), pmVotFund);
		}
		
		getManagedBean().setSetiaId(0L);
		getManagedBean().setSubSetiaId(0L);	
		getManagedBean().setVotFundList(votFundRes.getVotFundList());
	}
	
	/*public void getProjectList(AjaxBehaviorEvent e) {
		Long votFundId = (Long) e.getComponent().getAttributes().get("value");
		if(votFundId!=null) {
			IProjectSetiaSO projectSetiaSO = (IProjectSetiaSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IProjectSetiaSO.class);
			ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
			reqDTO.setVotFundId(votFundId);
			ProjectSetiaResDTO resDTO = projectSetiaSO.retrieveProjectList(reqDTO);
			
			for (PmProject pmProject : resDTO.getProjectList()) {
				projectCodeMap.put(pmProject.getProjectId(), pmProject);
			}
			PmVotFund votFund = votFundCodeMap.get(votFundId);
			getManagedBean().setVotFundType(votFund.getVotFundType());
			getManagedBean().setProjectId(0L);	
			getManagedBean().setSetiaId(0L);
			getManagedBean().setSubSetiaId(0L);	
			getManagedBean().setProjectList(resDTO.getProjectList());
		} else {
			getManagedBean().setVotFundType(null);
			getManagedBean().setProjectId(0L);	
			getManagedBean().setSetiaId(0L);
			getManagedBean().setSubSetiaId(0L);	
			getManagedBean().setProjectList(new ArrayList<PmProject>());
		}
	}*/
	
	/*public void getProgramList(AjaxBehaviorEvent e) {
		Long votFundId = (Long) e.getComponent().getAttributes().get("value");
		if(votFundId!=null) {
			IProgramActivitySO programActivitySO = (IProgramActivitySO) BusinessDelegate.getInstance().getServiceObject("pm",IProgramActivitySO.class);
			ProgramActivityReqDTO reqDTO = new ProgramActivityReqDTO();
			reqDTO.setVotFundId(votFundId);
			ProgramActivityResDTO resDTO = programActivitySO.retrieveProgramActivityList(reqDTO);
			
			for (PmPrgActivity pmPrgActivity : resDTO.getPrgActivityList()) {
				progmActivityCodeMap.put(pmPrgActivity.getPrgActivityId(), pmPrgActivity);
			}
			getManagedBean().setPrgActivityId(0L);	
			getManagedBean().setSetiaId(0L);
			getManagedBean().setSubSetiaId(0L);	
			getManagedBean().setPrgActivityList(resDTO.getPrgActivityList());
		} else {
			getManagedBean().setPrgActivityId(0L);	
			getManagedBean().setSetiaId(0L);
			getManagedBean().setSubSetiaId(0L);	
			getManagedBean().setPrgActivityList(new ArrayList<PmPrgActivity>());
		}
	}*/
	
	
	/*public void getSetiaList(AjaxBehaviorEvent e) {
		Long projectId = (Long) e.getComponent().getAttributes().get("value");
		if(projectId!=null) {
			IProjectSetiaSO projectSetiaSO = (IProjectSetiaSO) BusinessDelegate.getInstance().getServiceObject("pm",IProjectSetiaSO.class);
			ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
			reqDTO.setProjectId(projectId);
			ProjectSetiaResDTO resDTO = projectSetiaSO.retrieveSetiaList(reqDTO);
			for (PmSetia pmSetia : resDTO.getSetiaList()) {
				setiaCodeMap.put(pmSetia.getSetiaId(), pmSetia);
			}
			getManagedBean().setSetiaId(0L);
			getManagedBean().setSetiaList(resDTO.getSetiaList());
		} else {
			getManagedBean().setSetiaId(0L);
			getManagedBean().setSetiaList(new ArrayList<PmSetia>());
		}
	}*/
	
	/*public void getSubSetiaList(AjaxBehaviorEvent e) {
		Long setiaId = (Long) e.getComponent().getAttributes().get("value");
		if(setiaId!=null) {
			IProjectSetiaSO projectSetiaSO = (IProjectSetiaSO) BusinessDelegate.getInstance().getServiceObject("pm",IProjectSetiaSO.class);
			ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
			reqDTO.setSetiaId(setiaId);
			ProjectSetiaResDTO resDTO = projectSetiaSO.retrieveSubSetiaList(reqDTO);
			for (PmSubSetia pmSubSetia : resDTO.getSubSetiaList()) {
				subSetiaCodeMap.put(pmSubSetia.getSubSetiaId(), pmSubSetia);
			}
			getManagedBean().setSubSetiaId(0L);
			getManagedBean().setSubSetiaList(resDTO.getSubSetiaList());
		} else {
			getManagedBean().setSubSetiaId(0L);
			getManagedBean().setSubSetiaList(new ArrayList<PmSubSetia>());
		}
	}*/
	
	public void getFinancingType(AjaxBehaviorEvent e) {
		Long subSetiaId = (Long) e.getComponent().getAttributes().get("value");
		if(subSetiaId!=null) {
			IProjectSetiaSO projectSetiaSO = (IProjectSetiaSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IProjectSetiaSO.class);
			ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
			reqDTO.setSubSetiaId(subSetiaId);
			ProjectSetiaResDTO resDTO = projectSetiaSO.retrieveSubSetia(reqDTO);
			getManagedBean().setFinancingTypeCode(resDTO.getPmSubSetia().getFinancingType().getParameterCode());
		}
	}
	
	public void getUserProfiles(){
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setLoginId(getFacesScopes().getUserSessionBean().getLoginId());
		UserResDTO userResDTO = userSO.retrieveUserByLoginId(userReqDTO);
		FulfilmentReqDTO fulfilmentReqDTO = new FulfilmentReqDTO();
		fulfilmentReqDTO.setPmUser(userResDTO.getPmUser());
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
	
	public void getAgAccount(){
		checkPtjGroupAndInitialize();
		getManagedBean().getFulfilmentRequest().setIssuedOrgProfile(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile());
		getManagedBean().setApproverModel(null);
		initializeApproverGroup();
		IAgOfficeSO glAccountSO = (IAgOfficeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAgOfficeSO.class);
		AgOfficeReqDTO reqDTO = new AgOfficeReqDTO();
		reqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId());
		AgOfficeResDTO resDTO = glAccountSO.retrieveAgOfficeByOrgProfileId(reqDTO);
		if(getManagedBean().getFulfilmentRequest().getAgOffice()==null){
			getManagedBean().getFulfilmentRequest().setAgOffice(new PmAgOffice());
		}
		getManagedBean().getFulfilmentRequest().setAgOffice(resDTO.getAgOffice());
		
	}
	
	private void checkPtjGroupAndInitialize() {
		getManagedBean().setCurrentPtjGroupId(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getPmPtjGroup().getPtjGroupId());
		if(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getPmPtjGroup().getPtjGroupId() != getManagedBean().getCurrentPtjGroupId()){
			if(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses() != null && !getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().isEmpty()){
				for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
					for(FlFulfilmentItemAddr itemAddr : flDeliveryAddress.getFlFulfilmentItemAddr()){
						itemAddr.setPmVotFund(null);
						itemAddr.setPmGlAccount(null);
						itemAddr.setPmSubSetia(null);
						itemAddr.setPmPrgActivity(null);
						itemAddr.setAssetInd(null);
						itemAddr.setCoaValue("N");
					}
				}
			}
		}
	}

	private void setManualAddressList(){
		Map<Long, List<PmAddressType>> addressTypeMap = new HashMap<Long, List<PmAddressType>>();
		if(getManagedBean().getAddressTypesList()==null || getManagedBean().getAddressTypesList().size()==0) {
			flDeliveryAddrList();
		}
		for(PmAddressType addressType : getManagedBean().getAddressTypesList()){
			Long key = addressType.getPmAddress().getPmDistrict().getDistrictId();
			if(!addressTypeMap.containsKey(key)){
				addressTypeMap.put(key, new ArrayList<PmAddressType>());
			}
			addressTypeMap.get(key).add(addressType);
		}
		getManagedBean().setManualAddressMap(addressTypeMap);
	}
	
	public void retrieveDeliveryAddressList(AjaxBehaviorEvent e) {
		Long districtId = (Long) e.getComponent().getAttributes().get("value");
		getManagedBean().setManualAddressList(getManagedBean().getManualAddressMap().get(districtId));
		
		List<SelectItem> manualAddrList = new ArrayList<SelectItem>();
		if(getManagedBean().getManualAddressList()!=null) {
			for (PmAddressType obj : getManagedBean().getManualAddressList()) {
				manualAddrList.add(new SelectItem(obj.getAddressTypeId(), obj.getPmAddress().getAddressName()));
			}
		}
		getManagedBean().setManualAddrList(manualAddrList);
	}
	
	private void getSupplierBranchesList(SmSupplier smSupplier){
		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
		
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setSupplierId(smSupplier.getSupplierId());
		SupplierSearchResDTO resDTO = supplierSearchSO.retrieveSmSupplierBranchBySupplier(reqDTO);
		
		List<SelectItem> supplierBranches = new ArrayList<SelectItem>();
		for (SmSupplierBranch supplierBranch : resDTO.getSmSupplierBranchList()) {
			supplierBranches.add(new SelectItem(supplierBranch.getSupplierBranchId(),supplierBranch.getBranchName()));
		}
		getManagedBean().setSupplierBranchesList(supplierBranches);
	}
	
	public void getSelectedSupplierBranchDtl(AjaxBehaviorEvent e){
		Integer supplierBranchId = (Integer) e.getComponent().getAttributes().get("value");
		if(supplierBranchId!=null) {
			IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,	IFulfilmentRequestSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setSupplierBranchId(new Long(supplierBranchId));
			FulfilmentResDTO resDTO = requestSO.retrieveSupplierBranchDtlByBranchId(reqDTO);
			
			SmSupplierBranch supplierBranch = resDTO.getSmSupplierBranch();//all this should reset into our Flrequest.FlSupplierDtl
			FlSupplierDtl supplierDtl = getManagedBean().getFulfilmentRequest().getFlSupplierDtl();
			supplierDtl.setBranchName(supplierBranch.getBranchName());
			supplierDtl.setFaxNo(supplierBranch.getFaxNo());
			supplierDtl.setPhoneNo(supplierBranch.getPhoneNo());
			supplierDtl.setSmAddress(supplierBranch.getSmAddress());
			supplierDtl.setContactEmail(supplierBranch.getSmPersonnel().getEmail());
			supplierDtl.setContactFaxNo(supplierBranch.getSmPersonnel().getFaxNo());
			supplierDtl.setContactMobileNo(supplierBranch.getSmPersonnel().getMobileNo());
			supplierDtl.setContactPerson(supplierBranch.getSmPersonnel().getName());
			supplierDtl.setContactPhoneNo(supplierBranch.getSmPersonnel().getPhoneNo());
		} else { // reset to HQ info
			IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setSupplierId(getManagedBean().getFulfilmentRequest().getSmSupplier().getSupplierId());
			MofRegistrationResDTO resDTO = mofRegistrationSO.retrieveSupplierProfile(reqDTO);
			SmSupplier supplier = resDTO.getSmSupplier();
			
			FlSupplierDtl supplierDtl = getManagedBean().getFulfilmentRequest().getFlSupplierDtl();
			supplierDtl.setSupplierName(supplier.getCompanyName());		
			supplierDtl.setBranchCode(null);
			supplierDtl.setBranchName(null);
			supplierDtl.setFaxNo(supplier.getSmAppl().getSmCompanyBasic().getFaxNo());
			supplierDtl.setPhoneNo(supplier.getSmAppl().getSmCompanyBasic().getPhoneNo());
			
			for(SmPersonnel smPersonnel: supplier.getSmAppl().getSmPersonnels()){
				if(smPersonnel.isContactPerson()){
					supplierDtl.setContactEmail(smPersonnel.getEmail());
					supplierDtl.setContactFaxNo(smPersonnel.getFaxNo());
					supplierDtl.setContactMobileNo(smPersonnel.getMobileNo());
					supplierDtl.setContactPerson(smPersonnel.getName());
					supplierDtl.setContactPhoneNo(smPersonnel.getPhoneNo());
					break;
				}
			}
			for(SmCompanyAddress smCompanyAddress : supplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses()){
				if(smCompanyAddress.getAddressType().equalsIgnoreCase(AddressType.CONTACT_ADDRESS.value())){
					supplierDtl.setSmAddress(smCompanyAddress.getSmAddress());
				}
			}
		}
	}
	
	/**
	 * @return the votFundCodeMap
	 */
	public static Map<Long, PmVotFund> getVotFundCodeMap() {
		return votFundCodeMap;
	}

	/**
	 * @param votFundCodeMap the votFundCodeMap to set
	 */
	public static void setVotFundCodeMap(Map<Long, PmVotFund> votFundCodeMap) {
		PurchaseRequestBackingBean.votFundCodeMap = votFundCodeMap;
	}

	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==1){
			sortAddress(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses());
			getManagedBean().setEmailValidate(true);
		}
		if(selectedTabIndex==2){
			if(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()!=null) { 
				sortOrderSummaryList(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses());
			}
		}
		if(selectedTabIndex==3){
			populateChargeLineData(false);	
			validateOrderQty();
			setClassName("@(.prChargeLinePnlClass)");
			onNodeSelect();
			getVotFundList(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId());
			getManagedBean().setChargeLinePanel(false);
			getManagedBean().setProgramLov(false);
			getManagedBean().setProjectLov(false);
			getManagedBean().setVotFundDisplay(false);
		}
		if(selectedTabIndex==4){
			initializeApproverGroup();
		}
	}
	
	private void validateOrderQty() {
		BigDecimal requestedQty = null;
		BigDecimal sumorderedQty = null;
		BigDecimal newQty = null;
		Map<Long, BigDecimal> sumOrderedQtyMap = new HashMap<Long,BigDecimal>(); 
		List<FlFulfilmentItemAddr> itemAddrList = new ArrayList<FlFulfilmentItemAddr>();
		for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()) {
			for(FlFulfilmentItemAddr itemAddr : flDeliveryAddress.getFlFulfilmentItemAddr()){
				if(sumOrderedQtyMap.get(itemAddr.getScRequestItem().getRequestNoteItemId()) != null ){
					if(itemAddr.getOrderedQty() == null){
						itemAddr.setOrderedQty(new BigDecimal(0));
					}
					newQty = sumOrderedQtyMap.get(itemAddr.getScRequestItem().getRequestNoteItemId()).add(itemAddr.getOrderedQty());
					sumOrderedQtyMap.put(itemAddr.getScRequestItem().getRequestNoteItemId(), newQty);
				}else{
					sumOrderedQtyMap.put(itemAddr.getScRequestItem().getRequestNoteItemId(), itemAddr.getOrderedQty());
					itemAddrList.add(itemAddr);
				}
			}
		}
		
//		for(Map.Entry<Long,BigDecimal> entry : sumOrderedQtyMap.entrySet()){//TODO tbd by gustia from lilian
//			requestReqDTO.setRequestItemId(entry.getKey());
//			requestReqDTO.setOrderQuantity(entry.getValue());
//			purchaseRequestSO.checkIsOrderQuantityMet(requestReqDTO);
//			
//		}
		
		for(FlFulfilmentItemAddr itemAddr : itemAddrList){
			requestedQty = itemAddr.getScRequestItem().getQuantity();
			sumorderedQty = sumOrderedQtyMap.get(itemAddr.getScRequestItem().getRequestNoteItemId());
			if(requestedQty!=null && requestedQty.intValue() != 0){
				if(requestedQty.intValue() > sumorderedQty.intValue()){
					throw new EpException(MessageConst.ERROR_740007,new String[]{sumorderedQty.toString() ,requestedQty.toString(),itemAddr.getScRequestItem().getItemName()});
				}else if(requestedQty.intValue() < sumorderedQty.intValue()){
					throw new EpException(MessageConst.ERROR_740007,new String[]{sumorderedQty.toString() ,requestedQty.toString(),itemAddr.getScRequestItem().getItemName()});
				}
			}
		}
	}
	
	public void updateDeliveryDate() {
		Date startDate = getManagedBean().getFulfilmentRequest().getStartDate();
		int deliveryTerm = 0;
		String uomCode = "";
		deliveryTerm = getManagedBean().getFulfilmentRequest().getDeliveryTerm();
		uomCode = getManagedBean().getFulfilmentRequest().getPmUom().getParameterCode();
		
		if(uomCode.equalsIgnoreCase(ParameterConst.DELIVERY_TERM_DAYS)){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.DATE, deliveryTerm);
			getManagedBean().setMaxDate(calendar.getTime());
			getManagedBean().getFulfilmentRequest().setEndDate(calendar.getTime());
		}else if(uomCode.equalsIgnoreCase(ParameterConst.DELIVERY_TERM_MONTHS)){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.MONTH, deliveryTerm);
			getManagedBean().setMaxDate(calendar.getTime());
			getManagedBean().getFulfilmentRequest().setEndDate(calendar.getTime());
		}
		
		//update delivery address
		for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()) {
			flDeliveryAddress.setStartDate(getManagedBean().getFulfilmentRequest().getStartDate());
			flDeliveryAddress.setEndDate(getManagedBean().getFulfilmentRequest().getEndDate());
		}
	}
	
	//update delivery address based on endDate change
	public void updateDeliveryEndDate() {
		for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()) {
			flDeliveryAddress.setEndDate(getManagedBean().getFulfilmentRequest().getEndDate());
		}
	}
	// sorting Item for OrderSummary Tab
	public void sortOrderSummaryList(List<FlDeliveryAddress> deliveryAddressList) {
		//Creating Comparator
		if(null==deliveryAddressList || deliveryAddressList.isEmpty()){
			return;
		}
		for(FlDeliveryAddress flDeliveryAddress : deliveryAddressList) {
			sortItems(flDeliveryAddress.getFlFulfilmentItemAddr());
		}
    }
	// sorting all the items showing in DelAddress and Item Tab
	public void sortItems(List<FlFulfilmentItemAddr> flItemAddressList) {
		//Creating Comparator
		if(null==flItemAddressList || flItemAddressList.isEmpty()){
			return;
		}
		  Comparator<FlFulfilmentItemAddr> comparator = new Comparator<FlFulfilmentItemAddr>() {
	            @Override
	            public int compare(FlFulfilmentItemAddr o1, FlFulfilmentItemAddr o2) {
	            	String object1Code = o1.getScRequestItem().getCmItem()!=null?o1.getScRequestItem().getCmItem().getExtensionCode():o1.getScRequestItem().getTempUnspscCode();
	            	String object2Code = o2.getScRequestItem().getCmItem()!=null?o2.getScRequestItem().getCmItem().getExtensionCode():o2.getScRequestItem().getTempUnspscCode();
	                return new CompareToBuilder()
		                .append(object1Code, object2Code)
		                .toComparison();
	            }
	        };
			Collections.sort(flItemAddressList, comparator);
    }
	
	public void sortAddress(List<FlDeliveryAddress> flDelAddressList) {
		//Creating Comparator
		if(null==flDelAddressList || flDelAddressList.isEmpty()){
			return;
		}
		  Comparator<FlDeliveryAddress> comparator = new Comparator<FlDeliveryAddress>() {
	            @Override
	            public int compare(FlDeliveryAddress o1, FlDeliveryAddress o2) {
	                return new CompareToBuilder()
		                .append(o1.getPmAddress().getAddress1(), o2.getPmAddress().getAddress1())
		                .append(o1.getPmAddress().getAddress2(), o2.getPmAddress().getAddress2())
		                .append(o1.getPmAddress().getAddress3(), o2.getPmAddress().getAddress3())
		                .toComparison();
	            }
	        };
			Collections.sort(flDelAddressList, comparator);
    }
	
	// get the Request Item Information based on ReqItemId and SuppId
	
	public void reqItemInfoList(FlFulfilmentItemAddr flItemAddr){
		getManagedBean().setItemName(flItemAddr.getScRequestItem().getItemName());
		if(null == flItemAddr.getScRequestItem().getQuantity()){
			getManagedBean().setQuantity(new Double(0));
		}else{
			getManagedBean().setQuantity(flItemAddr.getScRequestItem().getQuantity().doubleValue());
		}
		flItemAddr.setPriceType(getManagedBean().getPmPriceType());
		getManagedBean().setPriceType(flItemAddr.getPriceType());
		getManagedBean().setStandardUnitPrice(flItemAddr.getUnitPrice().doubleValue());
		getManagedBean().setStandardPrice(false);
		getManagedBean().setZonalPrice(false);
		getManagedBean().setZonalTierPrice(false);
		if(FulfilmentStatusConst.STANDARD_PRICE_TYPE.equals(flItemAddr.getPriceType().getParameterCode()) || FulfilmentStatusConst.STANDARD_PROMOTION_PRICE_TYPE.equals(flItemAddr.getPriceType().getParameterCode())){
			getManagedBean().setStandardPrice(true);
		}else if(FulfilmentStatusConst.ZONAL_PRICE_TYPE.equals(flItemAddr.getPriceType().getParameterCode()) || FulfilmentStatusConst.ZONAL_PROMOTION_PRICE_TYPE.equals(flItemAddr.getPriceType().getParameterCode())){
			getManagedBean().setZonalPrice(true);
		}else if(FulfilmentStatusConst.ZONAL_TIER_PRICE_TYPE.equals(flItemAddr.getPriceType().getParameterCode()) || FulfilmentStatusConst.ZONAL_TIER_PROMOTION_PRICE_TYPE.equals(flItemAddr.getPriceType().getParameterCode())){
			getManagedBean().setZonalTierPrice(true);
		}
		
		IPurchaseRequestSO purchaseRequestSO = (IPurchaseRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,	IPurchaseRequestSO.class);
		PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();
		reqDTO.setRequestItemId(flItemAddr.getScRequestItem().getRequestNoteItemId());
		reqDTO.setSupplierId(getManagedBean().getFulfilmentRequest().getSmSupplier().getSupplierId());
		reqDTO.setPriceType(getManagedBean().getPmPriceType());
		PurchaseRequestResDTO purResDTO = purchaseRequestSO.getSupplierItemByReqItemIdNSupplierId(reqDTO);
		getManagedBean().setReqItemInfoList(purResDTO.getSupplierItemPriceList());
		List<FLSupplierItemVO> supplierItems=new ArrayList<FLSupplierItemVO>();
		if(purResDTO.getSupplierItemPriceList() != null && purResDTO.getSupplierItemPriceList().size() >0){
			for (ScSupplierItemPrice scSupplierItemPrice : purResDTO.getSupplierItemPriceList() ) {
				if(null != scSupplierItemPrice.getTplSpecZone()){
					TplSpecZoneDtl tplSpecZoneDtl = scSupplierItemPrice.getTplSpecZone();
					if(tplSpecZoneDtl.getTplSpecZoneLocs() != null && tplSpecZoneDtl.getTplSpecZoneLocs().size()>0){
						FLSupplierItemVO itemPriceVO=new FLSupplierItemVO();
						itemPriceVO.setSupplierItemPrice(scSupplierItemPrice);
						itemPriceVO.setItemZoneTree(LocationSingleton.getInstance().constuctTreeNode(tplSpecZoneDtl, tplSpecZoneDtl.getTplSpecZoneLocs()));
						supplierItems.add(itemPriceVO);
					}
				} else {
					FLSupplierItemVO itemPriceVO=new FLSupplierItemVO();
					itemPriceVO.setSupplierItemPrice(scSupplierItemPrice);
					supplierItems.add(itemPriceVO);
				}
			}
			getManagedBean().setSupplierItemPriceList(supplierItems);
		}	
		
	}
	
	private void sortAddressType(List<PmAddressType> addressTypeList) {
		  // Creating Comparator
		  if (null == addressTypeList || addressTypeList.isEmpty()) {
		   return;
		  }
		  Comparator<PmAddressType> comparator = new Comparator<PmAddressType>() {
		   @Override
		   public int compare(PmAddressType o1, PmAddressType o2) {
		    return new CompareToBuilder()
		     .append(o1.getPmAddress().getAddress1(), o2.getPmAddress().getAddress1())
		     .append(o1.getPmAddress().getAddress2(), o2.getPmAddress().getAddress2())
		     .append(o1.getPmAddress().getAddress3(), o2.getPmAddress().getAddress3()).toComparison();
		   }
		  };
		  Collections.sort(addressTypeList, comparator);
	}
	
	// chargeLine Panel enable if  checkbox selected
	public void selectCheckbox(AjaxBehaviorEvent e){
		onNodeUnSelect();
		getManagedBean().setProgramLov(false);
		getManagedBean().setProjectLov(false);
		boolean checkValue=(boolean)e.getComponent().getAttributes().get("value");
		System.out.println("checkValue:::: "+checkValue);
		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setChargeLinePanel(true);
		}else{
			getManagedBean().setChargeLinePanel(false);
		}
	}
	
	public String retrieveMofNo(SmSupplier smSupplier) {
		IMofAccountSO mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofAccountSO.class);
		MofAccountReqDTO reqDTO = new MofAccountReqDTO();
		reqDTO.setSupplierId(smSupplier.getSupplierId());
		MofAccountResDTO resDTO = mofAccountSO.retrieveMofNoBySupplierId(reqDTO);
		if(resDTO.isSuccessful() && resDTO.getMofAccount()!=null) {
			return resDTO.getMofAccount().getMofNo();
		}
		return null;
	}
	public void checkVotFundLov(){
		getManagedBean().setProgramLov(false);
		getManagedBean().setProjectLov(false);
		getManagedBean().setVotFundDisplay(false);
		getManagedBean().setVotFundCode("");
		getManagedBean().setVotFundId(0L);
		if(getManagedBean().getPmSubSetia()!=null) {
			getManagedBean().getPmSubSetia().setSubSetiaCode("");
			getManagedBean().getPmSubSetia().getPmSetia().setSetiaCode("");
			getManagedBean().getPmSubSetia().getPmSetia().getPmProject().setProjectCode("");
			getManagedBean().getPmSubSetia().setFinancingType(null);
		}
		getManagedBean().setPmPrgActivity(null);
		
		if(null != getManagedBean().getPmVoteFund()){
			votFundCodeMap.put(getManagedBean().getPmVoteFund().getVotFundId(), getManagedBean().getPmVoteFund());
		}
	}
	
	public void checkVotFundDropdown(){
		getManagedBean().setProgramLov(false);
		getManagedBean().setProjectLov(false);
		getManagedBean().setVotFundDisplay(true);
		
		if(null != getManagedBean().getPmVoteFund()){
			if(!"".equalsIgnoreCase(getManagedBean().getPmVoteFund().getVotFundCode())){
				getManagedBean().getPmVoteFund().setVotFundCode("");
				getManagedBean().getPmVoteFund().getVotFundCode();
			}
		}
		PmVotFund pmVotFund = votFundCodeMap.get(getManagedBean().getVotFundId());
		getManagedBean().setPmPrgActivity(null);
		getManagedBean().setPmSubSetia(null);
//		if(null != getManagedBean().getPmSubSetia()){
//			getManagedBean().getPmSubSetia().setSubSetiaCode("");
//			getManagedBean().getPmSubSetia().getPmSetia().setSetiaCode("");
//			getManagedBean().getPmSubSetia().getPmSetia().getPmProject().setProjectCode("");
//			getManagedBean().getPmSubSetia().setFinancingType(null);
//		}			
		if(pmVotFund!=null && !"".equalsIgnoreCase(pmVotFund.getVotFundType())){
			if(VotFundType.BEKALAN.value().equalsIgnoreCase(pmVotFund.getVotFundType()) || VotFundType.TANGGUNGAN.value().equalsIgnoreCase(pmVotFund.getVotFundType())){
				getManagedBean().setProgramLov(true);
			} else if(VotFundType.PEMBANGUNAN.value().equalsIgnoreCase(pmVotFund.getVotFundType()) || VotFundType.SETIA.value().equalsIgnoreCase(pmVotFund.getVotFundType())){
				getManagedBean().setProjectLov(true);
			}
		}
	}
	
	/**
	 * Page navigation to FLDoc search page in PR
	 */
	public String backToPervious() {
		return "flDocSearch";
	}
	
}
