/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.pr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
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
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.programactivity.IProgramActivitySO;
import my.ep.biz.service.pm.common.projectsetia.IProjectSetiaSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseRequestSO;
import my.ep.biz.service.sc.directpurchase.IRequestNoteSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
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
import my.ep.dto.pm.common.glaccount.GlAccountReqDTO;
import my.ep.dto.pm.common.glaccount.GlAccountResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityReqDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityResDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
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
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
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
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
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
	private static Map<Long, PmGlAccount> glAccountCodeMap = new HashMap<Long, PmGlAccount>();
	private static Map<Long, PmPrgActivity> progmActivityCodeMap = new HashMap<Long, PmPrgActivity>();
	private static Map<Long, PmProject> projectCodeMap = new HashMap<Long, PmProject>();
	private static Map<Long, PmSetia> setiaCodeMap = new HashMap<Long, PmSetia>();
	private static Map<Long, PmSubSetia> subSetiaCodeMap = new HashMap<Long, PmSubSetia>();
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
			} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
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
					setFulfilmentRequestField(resDTO);
				}
			} 
		}  finally {
			fulfilmentRequestSO = null;
		}
		return null;
	
	}

	private void setFulfilmentRequestField(FulfilmentResDTO resDTO) {
		getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
		getManagedBean().getFulfilmentRequest().setTitle(getManagedBean().getFulfilmentRequest().getTitle().trim());
		FulfilmentBackingBean flBB = new FulfilmentBackingBean(ParameterConst.DOCTYPE_PURCHASE_REQUEST, resDTO.getFlFulfilmentRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
		flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
		getManagedBean().setSelectedTabIndex(0);
		getSumOrderedQty(getManagedBean().getFulfilmentRequest());

		if(getManagedBean().getApproverModel()==null) {
			List<String> source = new ArrayList<String>(); 
	        List<String> target = new ArrayList<String>();  
	        getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}	
		
		if(getManagedBean().getFulfilmentRequest().getPmUserGroup()!=null) {
			List<String> source = new ArrayList<String>();
			PmUserGroup userGroup = null;
			if(getManagedBean().getUserGroupMap()!=null) {
				userGroup = getManagedBean().getUserGroupMap().get(getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId());
			} else {
				userGroup = getManagedBean().getFulfilmentRequest().getPmUserGroup();
			}
			for(PmUserGroupUser user : userGroup.getPmUserGroupUsers()) {
				source.add(user.getPmUser().getUserName());
			}
			getManagedBean().getApproverModel().setSource(source);
		}
		getManagedBean().getApproverModel().setTarget(resDTO.getSelectedApproverList());
		getManagedBean().getApproverModel().getSource().removeAll(resDTO.getSelectedApproverList());

		if(resDTO.getFlFulfilmentRequest().getSmSupplier().getSmAppl().getSmSupplierBranches() != null && !resDTO.getFlFulfilmentRequest().getSmSupplier().getSmAppl().getSmSupplierBranches().isEmpty()){
			getSupplierBranchesList(resDTO.getFlFulfilmentRequest().getSmSupplier().getSmAppl().getSmSupplierBranches());
		}
		
		for(FlFulfilmentItem flItem : getManagedBean().getFulfilmentRequest().getFlFulfilmentItems()) { 
			String key = flItem.getCmItem().getItemId().toString() + flItem.getUnitPrice().doubleValue() + "";
		    if(!getManagedBean().getFlItemMap().containsKey(key)) {
			    getManagedBean().getFlItemMap().put(key, flItem);
		    }
		}
		
		if(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().size()>0) {
			getManagedBean().setSelectedDelAddr(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().get(0));
			selectAddrEvent();
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
	}
	
	public void flManualDeliveryAddr() {
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
				getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().add(deliveryAddr);
			}
		}
		getManagedBean().setAddressTypesList(new ArrayList<PmAddressType>());
		selectAddrEvent();
	}
	public void updateManualDeliveryAddrDt() {
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
		if(null !=getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			Iterator<FlDeliveryAddress> delAddressIterator=getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().iterator();
			while(delAddressIterator.hasNext()){
				FlDeliveryAddress  deliveryAddress=delAddressIterator.next();
				if(deliveryAddress.isSelectedAddressRowDelete()){
					delAddressIterator.remove();
				}
			}
			if(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().size()>0) {
				getManagedBean().setSelectedDelAddr(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().get(0));
			} else {
				getManagedBean().setSelectedDelAddr(null);
			}
			populateItemsForSelectedAddress(getManagedBean().getShowAllItemsRows());
		}
	}
	
	public void selectAddrEvent() {
		clearItemsForSelectedAddress();
		populateItemsForSelectedAddress(getManagedBean().getShowAllItemsRows());
	}

	private void populateItemsForSelectedAddress(List<FlFulfilmentItemAddr> showAllItemsRows) {
		List<FlFulfilmentItemAddr> itemsList = new ArrayList<FlFulfilmentItemAddr>();

		for (FlFulfilmentItemAddr newItem : showAllItemsRows) {
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
				itemsList.add(newItem);
			}
		}
		itemsList.size();
		getManagedBean().setShowAllItemsRows(itemsList);
	}
	
	public void clearItemsForSelectedAddress(){
		addPurchsedItems();
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
		
		for (ScRequestNoteDtl purchaseItem : getManagedBean().getRequestNoteWithDtlsList()) {
			flItemAddr = new FlFulfilmentItemAddr();
			flItemAddr.setItemType(purchaseItem.getScRequestSupplierItem().getRequestNoteItem().getItemType());
			reqDTO.setRequestSupplierItemId(purchaseItem.getScRequestSupplierItem().getRequestSupplierItemId());
			reqDTO.setPriceType(purchaseItem.getPriceType());
			reqDTO.setOrderQuantity(new BigDecimal(1));
			purResDTO = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
			flItemAddr.setUnitPrice(purResDTO.getUnitPrice());
			flItemAddr.setScSupplierItemPrice(purResDTO.getSupplierItemPrice());
			flItemAddr.setScRequestItem(purchaseItem.getScRequestSupplierItem().getRequestNoteItem());
			flItemAddr.setCmItem(purchaseItem.getScRequestSupplierItem().getRequestNoteItem().getCmItem());
			flItemAddr.setOrderedQty(new BigDecimal(0));
			flItemAddr.setOrderedAmt(new BigDecimal(0));
			allItemsList.add(flItemAddr);
		}
		getManagedBean().setShowAllItemsRows(allItemsList);
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
		reqDTO.setRequestSupplierItemId(flItemAddr.getScSupplierItemPrice().getScRequestSupplierItem().getRequestSupplierItemId());
		reqDTO.setPriceType(flItemAddr.getScSupplierItemPrice().getPriceType());
		  
		if(null == flItemAddr.getOrderedQty()){
			flItemAddr.setOrderedAmt(new BigDecimal("0"));
		} else {
			reqDTO.setOrderQuantity(flItemAddr.getOrderedQty());
			PurchaseRequestResDTO purResDTO = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
			flItemAddr.setUnitPrice(purResDTO.getUnitPrice());
			flItemAddr.setScSupplierItemPrice(purResDTO.getSupplierItemPrice());
			flItemAddr.setOrderedAmt(flItemAddr.getOrderedQty().multiply(flItemAddr.getUnitPrice()));
			flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
			
			if(flItemAddr.getFlFulfilmentItem()!=null) {
				flItemAddr.getFlFulfilmentItem().setUnitPrice(flItemAddr.getUnitPrice());
			} else {
				FlFulfilmentItem fulfilmentItem = null;
				String key = flItemAddr.getCmItem().getItemId().toString() + flItemAddr.getUnitPrice().doubleValue() + "";
			    if(!getManagedBean().getFlItemMap().containsKey(key)) {
					fulfilmentItem = new FlFulfilmentItem();
					fulfilmentItem.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
					fulfilmentItem.setChargeLineSeq(1);
					fulfilmentItem.setItemType(flItemAddr.getItemType());
					fulfilmentItem.setCmItem(flItemAddr.getCmItem());
					fulfilmentItem.setPmVotFund(flItemAddr.getPmVotFund());
					fulfilmentItem.setAssetInd(flItemAddr.getAssetInd());
					fulfilmentItem.setPmGlAccount(flItemAddr.getPmGlAccount());
					fulfilmentItem.setPmPrgActivity(flItemAddr.getPmPrgActivity());
					fulfilmentItem.setPmSubSetia(flItemAddr.getPmSubSetia());
					fulfilmentItem.setUnitPrice(flItemAddr.getUnitPrice());
				    flItemAddr.setFlFulfilmentItem(fulfilmentItem);
				    getManagedBean().getFlItemMap().put(key, flItemAddr.getFlFulfilmentItem());
			    } else {
			    	fulfilmentItem = getManagedBean().getFlItemMap().get(key);
					fulfilmentItem.setUnitPrice(flItemAddr.getUnitPrice());
				    flItemAddr.setFlFulfilmentItem(fulfilmentItem);
				    flItemAddr.setPmVotFund(fulfilmentItem.getPmVotFund());
				    flItemAddr.setAssetInd(fulfilmentItem.getAssetInd());
				    flItemAddr.setPmGlAccount(fulfilmentItem.getPmGlAccount());
				    flItemAddr.setPmPrgActivity(fulfilmentItem.getPmPrgActivity());
				    flItemAddr.setPmSubSetia(fulfilmentItem.getPmSubSetia());
			    }
			    
			    if(!fulfilmentItem.getFlFulfilmentItemAddr().contains(flItemAddr)) {
			    	fulfilmentItem.getFlFulfilmentItemAddr().add(flItemAddr);
			    }
			    
			    if(!getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().contains(fulfilmentItem)) {
			    	getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().add(fulfilmentItem);
			    }
			}
		}
		// add  or remove items to Selected delivery address
		itemsToSelectedAddress(flItemAddr);
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
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "prBasePage.xhtml?faces-redirect=true");
	}
	
	private void getSumOrderedQty(FlFulfilmentRequest flRequest) {
		if(flRequest.getFlFulfilmentItems() != null && !flRequest.getFlFulfilmentItems().isEmpty()){
			BigDecimal sumOrderedQty = new BigDecimal(0);
			for(FlFulfilmentItem fulfilmentItem : flRequest.getFlFulfilmentItems()){
				for(FlFulfilmentItemAddr itemAddress : fulfilmentItem.getFlFulfilmentItemAddr()){
					sumOrderedQty = sumOrderedQty.add(itemAddress.getOrderedQty());
				}
				fulfilmentItem.setSumOrderedQty(sumOrderedQty);
			}
		}
	}

	public void calculateSubTotal(BigDecimal quantity){
		getManagedBean().getSelectedItemAddr().setSubTotal(quantity);
	}
	
	private void initializeApproverGroup() {
		IUserSO userSO = null;
		try {
			if(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId() != getManagedBean().getCurrentPreparedOrgId()){
				userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

				UserReqDTO reqDTO = new UserReqDTO();
				UserResDTO resDTO = new UserResDTO();

				reqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId());
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
					getManagedBean().getUserGroupList().add(userGroup);

					for(PmUserGroupUser user : userGroup.getPmUserGroupUsers()) {
						getManagedBean().getApproverMap().put(user.getPmUser().getUserName(), user);
					}
					if(i==0) {
						getManagedBean().getFulfilmentRequest().getPmUserGroup().setUserGroupId(userGroup.getUserGroupId());
						List<String> source = new ArrayList<String>();
						for(PmUserGroupUser user : userGroup.getPmUserGroupUsers()) {
							source.add(user.getPmUser().getUserName());
						}
						if(getManagedBean().getApproverModel().getSource().size()==0) {
							getManagedBean().getApproverModel().setSource(source);
						}
					}
					i++;
				}
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
		TreeNode selectedNode=getManagedBean().getSelectedNode();
		onNodeUnSelect();
		if(null!=selectedNode){
			for(TreeNode children:selectedNode.getChildren()){
				FlFulfilmentItemAddr childData=((FlFulfilmentItemAddr)children.getData());
				if(null !=childData.getPmVotFund()){
					getManagedBean().setVotFundCode(childData.getPmVotFund().getVotFundCode());
				}
				
				if(childData.getAssetInd()!=null) {
					getManagedBean().setAssetIndCode(childData.getAssetInd().getParameterCode());
				}
					
				if(childData.getPmGlAccount()!=null) {
					String glAccountCode = glAccountCodeMap.get(childData.getPmGlAccount().getGlAccountId()).getGlAccCode();
					getManagedBean().setGlAccountCode(glAccountCode);
				}
				
				if(childData.getPmPrgActivity()!=null) {
					if(progmActivityCodeMap.size()==0) {
						IProgramActivitySO programActivitySO = (IProgramActivitySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IProgramActivitySO.class);
						ProgramActivityReqDTO reqDTO = new ProgramActivityReqDTO();
						reqDTO.setVotFundId(childData.getPmVotFund().getVotFundId());
						ProgramActivityResDTO resDTO = programActivitySO.retrieveProgramActivityList(reqDTO);
						
						for (PmPrgActivity pmPrgActivity : resDTO.getPrgActivityList()) {
							progmActivityCodeMap.put(pmPrgActivity.getPrgActivityId(), pmPrgActivity);
						}
					}
					String progActvCode = progmActivityCodeMap.get(childData.getPmPrgActivity().getPrgActivityId()).getPrgActivityCode();
					getManagedBean().setPrgActivityCode(progActvCode);
				}
				
				if(childData.getPmSubSetia()!=null) {
					if(subSetiaCodeMap.size()==0) {
						IProjectSetiaSO projectSetiaSO = (IProjectSetiaSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IProjectSetiaSO.class);
						ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
						reqDTO.setSetiaId(childData.getPmSubSetia().getPmSetia().getSetiaId());
						ProjectSetiaResDTO resDTO = projectSetiaSO.retrieveSubSetiaList(reqDTO);
						for (PmSubSetia pmSubSetia : resDTO.getSubSetiaList()) {
							subSetiaCodeMap.put(pmSubSetia.getSubSetiaId(), pmSubSetia);
						}						
					}
					String subSetiaCode = subSetiaCodeMap.get(childData.getPmSubSetia().getSubSetiaId()).getSubSetiaCode();
					getManagedBean().setSubSetiaCode((subSetiaCode));
					
					if(childData.getPmSubSetia().getPmSetia()!=null) {
						//String setiaCode = setiaCodeMap.get(childData.getPmSubSetia().getPmSetia().getSetiaId()).getSetiaCode();
						getManagedBean().setSetiaCode(childData.getPmSubSetia().getPmSetia().getSetiaCode());
						
						if(childData.getPmSubSetia().getPmSetia().getPmProject()!=null) {
//							String projectCode = projectCodeMap.get(childData.getPmSubSetia().getPmSetia().getPmProject().getProjectId()).getProjectCode();
							getManagedBean().setProjectCode(childData.getPmSubSetia().getPmSetia().getPmProject().getProjectCode());
						}
					}
					getManagedBean().setFinancingTypeCode(subSetiaCodeMap.get(childData.getPmSubSetia().getSubSetiaId()).getFinancingType().getParameterCode());
				}
			}
		}
	}
	
	public void onNodeUnSelect() {
		getManagedBean().setVotFundCode("");
		getManagedBean().setAssetIndCode("");
		getManagedBean().setGlAccountCode("");
		getManagedBean().setPrgActivityCode("");
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
			chargeLineValidation();
			
			PmVotFund pmVotFund = votFundCodeMap.get(getManagedBean().getVotFundId());
			PmGlAccount pmGlAccount = glAccountCodeMap.get(getManagedBean().getGlAccountId());
			PmPrgActivity pmPrgActivity = progmActivityCodeMap.get(getManagedBean().getPrgActivityId());
			PmProject pmProject = projectCodeMap.get(getManagedBean().getProjectId());
			PmSetia pmSetia = setiaCodeMap.get(getManagedBean().getSetiaId());
			PmSubSetia pmSubSetia = subSetiaCodeMap.get(getManagedBean().getSubSetiaId());
			
			ParameterReqDTO paramReqDTO = new ParameterReqDTO();
			paramReqDTO.setParameterId(getManagedBean().getAssetIndId());
			parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			PmParameter assetInd = parameterSO.retrieveParameterById(paramReqDTO).getPmParameter();
			
			updateAllItemsWithChargeDrops(getManagedBean().getSelectedNodes()); // set COA to Y
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
					
					childItemAddress.getFlFulfilmentItem().setPmVotFund(pmVotFund);
					childItemAddress.getFlFulfilmentItem().setAssetInd(assetInd);
					childItemAddress.getFlFulfilmentItem().setPmGlAccount(pmGlAccount);
					childItemAddress.getFlFulfilmentItem().setPmPrgActivity(pmPrgActivity);
					childItemAddress.getFlFulfilmentItem().setPmSubSetia(pmSubSetia);
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
							
							itemDelAddress.getFlFulfilmentItem().setPmVotFund(pmVotFund);
							itemDelAddress.getFlFulfilmentItem().setAssetInd(assetInd);
							itemDelAddress.getFlFulfilmentItem().setPmGlAccount(pmGlAccount);
							itemDelAddress.getFlFulfilmentItem().setPmPrgActivity(pmPrgActivity);
							itemDelAddress.getFlFulfilmentItem().setPmSubSetia(pmSubSetia);
						}
					}
				}
			}
			populateChargeLineData(true);
			onNodeSelect();
			RequestContext.getCurrentInstance().execute("applyBulkCharge.hide()");
		} finally {
			parameterSO = null;
		}
	}
	
	private void populateChargeLineData(boolean retainPreviousSelection) {
		getManagedBean().setChargeLineItemRoot(new DefaultTreeNode("root", null));
		Map<Long, List<FlFulfilmentItemAddr>> chargeLineItemsMap = new HashMap<Long, List<FlFulfilmentItemAddr>>();
		
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
			
			for(FlFulfilmentItemAddr itemAddr : itemEntry.getValue()) {
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
		
		boolean isVotSOrAmanahExist = false;
		String votFundType = null;
		for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddress:deliveryAddress.getFlFulfilmentItemAddr()){
				if(itemAddress.getPmVotFund()!=null) {
					votFundType = votFundCodeMap.get(itemAddress.getPmVotFund().getVotFundId()).getVotFundType();
					if(votFundType.equalsIgnoreCase(VotFundType.SETIA.value()) || votFundType.equalsIgnoreCase(VotFundType.AMANAH.value())) {
						isVotSOrAmanahExist = true;
						break;
					}
				}
			}
		}		
		PmVotFund pmVotFund = votFundCodeMap.get(getManagedBean().getVotFundId());
		if(pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.BEKALAN.value()) || 
			pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.TANGGUNGAN.value()) ||
			pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.PEMBANGUNAN.value())) {
			if(isVotSOrAmanahExist) {
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

	private void updateAllItemsWithChargeDrops(TreeNode[] selectedNodes) {
		for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddress:deliveryAddress.getFlFulfilmentItemAddr()){
				for(TreeNode node:selectedNodes){
					FlFulfilmentItemAddr selectedItem=(FlFulfilmentItemAddr)node.getData();
					if(itemAddress.getScRequestItem().getRequestNoteItemId().equals(selectedItem.getScRequestItem().getRequestNoteItemId())){
						itemAddress.setCoaValue("Y");
						break;
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
	
	public OrgProfileResDTO getVotFundList(Long profileId){
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IOrgProfileSO.class);
		IGlAccountSO glAccountSO = (IGlAccountSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IGlAccountSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(profileId);
		OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileByProfileId(reqDTO);
		
		for (PmVotFund pmVotFund : resDTO.getPmOrgProfile().getPmPtjGroup().getPmVotFunds()) {
			votFundCodeMap.put(pmVotFund.getVotFundId(), pmVotFund);
		}
		
		GlAccountReqDTO glAccountReqDTO = new GlAccountReqDTO();
		glAccountReqDTO.setPtjGroupId(resDTO.getPmOrgProfile().getPmPtjGroup().getPtjGroupId());
		GlAccountResDTO glAccountResDTO = glAccountSO.retrieveGlAccountList(glAccountReqDTO);
		getManagedBean().setGlAccountList(glAccountResDTO.getGlAccountList());
		for(PmGlAccount pmGlAcount : glAccountResDTO.getGlAccountList()) {
			glAccountCodeMap.put(pmGlAcount.getGlAccountId(), pmGlAcount);
		}
		getManagedBean().setSetiaId(0L);
		getManagedBean().setSubSetiaId(0L);	
		getManagedBean().setVotFundList(resDTO.getPmOrgProfile().getPmPtjGroup().getPmVotFunds());
		
		return resDTO;
	}
	
	public void getProjectList(AjaxBehaviorEvent e) {
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
	}
	
	public void getProgramList(AjaxBehaviorEvent e) {
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
	}
	
	
	public void getSetiaList(AjaxBehaviorEvent e) {
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
	}
	
	public void getSubSetiaList(AjaxBehaviorEvent e) {
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
	}
	
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
			if(getManagedBean().getFulfilmentRequest().getFlFulfilmentItems() != null && !getManagedBean().getFulfilmentRequest().getFlFulfilmentItems().isEmpty()){
				for(FlFulfilmentItem fulfilmentItem : getManagedBean().getFulfilmentRequest().getFlFulfilmentItems()){
					for(FlFulfilmentItemAddr itemAddr : fulfilmentItem.getFlFulfilmentItemAddr()){
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
	
	public List<SelectItem> getSupplierBranchesList(List<SmSupplierBranch> branches){
		
		List<SelectItem> supplierBranches = new ArrayList<SelectItem>();
		for (SmSupplierBranch supplierBranch : branches) {
			supplierBranches.add(new SelectItem(supplierBranch.getSupplierBranchId(),supplierBranch.getBranchName()));
		}
		getManagedBean().setSupplierBranchesList(supplierBranches);
		return supplierBranches;
	}
	public void getSelectedSupplierBranchDtl(AjaxBehaviorEvent e){
		Integer supplierBranchId = (Integer) e.getComponent().getAttributes().get("value");
		if(supplierBranchId!=null) {
			IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setBranchId(new Long(supplierBranchId));
			MofRegistrationResDTO resDTO = mofRegistrationSO.retrieveSupplierBranchDtlByBranchId(reqDTO);
			
			SmSupplierBranch supplierBranch = resDTO.getSupplierBranch();//all this should reset into our Flrequest.FlSupplierDtl
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
			SmSupplier supplier = getManagedBean().getFulfilmentRequest().getSmSupplier();
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

	/**
	 * @return the progmActivityCodeMap
	 */
	public static Map<Long, PmPrgActivity> getProgmActivityCodeMap() {
		return progmActivityCodeMap;
	}

	/**
	 * @param progmActivityCodeMap the progmActivityCodeMap to set
	 */
	public static void setProgmActivityCodeMap(
			Map<Long, PmPrgActivity> progmActivityCodeMap) {
		PurchaseRequestBackingBean.progmActivityCodeMap = progmActivityCodeMap;
	}

	/**
	 * @return the projectCodeMap
	 */
	public static Map<Long, PmProject> getProjectCodeMap() {
		return projectCodeMap;
	}

	/**
	 * @param projectCodeMap the projectCodeMap to set
	 */
	public static void setProjectCodeMap(Map<Long, PmProject> projectCodeMap) {
		PurchaseRequestBackingBean.projectCodeMap = projectCodeMap;
	}

	/**
	 * @return the setiaCodeMap
	 */
	public static Map<Long, PmSetia> getSetiaCodeMap() {
		return setiaCodeMap;
	}

	/**
	 * @param setiaCodeMap the setiaCodeMap to set
	 */
	public static void setSetiaCodeMap(Map<Long, PmSetia> setiaCodeMap) {
		PurchaseRequestBackingBean.setiaCodeMap = setiaCodeMap;
	}

	/**
	 * @return the subSetiaCodeMap
	 */
	public static Map<Long, PmSubSetia> getSubSetiaCodeMap() {
		return subSetiaCodeMap;
	}

	/**
	 * @param subSetiaCodeMap the subSetiaCodeMap to set
	 */
	public static void setSubSetiaCodeMap(Map<Long, PmSubSetia> subSetiaCodeMap) {
		PurchaseRequestBackingBean.subSetiaCodeMap = subSetiaCodeMap;
	}
	
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==3){
			populateChargeLineData(false);	
			validateOrderQty();
			setClassName("@(.prChargeLinePnlClass)");
			onNodeSelect();
			getVotFundList(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId());
		}
		if(selectedTabIndex==1){
			if(getManagedBean().getSelectedDelAddr()==null) { 
				addPurchsedItems();
				retrieveCountryList();
			}
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
		for(FlFulfilmentItem fulfilmentItem : getManagedBean().getFulfilmentRequest().getFlFulfilmentItems()){
		sumorderedQty = new BigDecimal(0);
			for(FlFulfilmentItemAddr itemAddr : fulfilmentItem.getFlFulfilmentItemAddr()){
				if(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().contains(itemAddr.getFlDeliveryAddress())){
					if(sumOrderedQtyMap.get(itemAddr.getScRequestItem().getRequestNoteItemId()) != null ){
						newQty = sumOrderedQtyMap.get(itemAddr.getScRequestItem().getRequestNoteItemId()).add(itemAddr.getOrderedQty());
						sumOrderedQtyMap.put(itemAddr.getScRequestItem().getRequestNoteItemId(), newQty);
					}else{
						sumOrderedQtyMap.put(itemAddr.getScRequestItem().getRequestNoteItemId(), itemAddr.getOrderedQty());
						itemAddrList.add(itemAddr);
					}
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
					throw new EpException(MessageConst.ERROR_740007,new String[]{sumorderedQty.toString() ,requestedQty.toString(),itemAddr.getFlFulfilmentItem().getCmItem().getItemName()});
				}else if(requestedQty.intValue() < sumorderedQty.intValue()){
					throw new EpException(MessageConst.ERROR_740007,new String[]{sumorderedQty.toString() ,requestedQty.toString(),itemAddr.getFlFulfilmentItem().getCmItem().getItemName()});
				}
			}
		}
		
//		for(FlFulfilmentItem fulfilmentItem : getManagedBean().getFulfilmentRequest().getFlFulfilmentItems()){
//			sumorderedQty = new BigDecimal(0);
//			requestedQty = null;
//			for(FlFulfilmentItemAddr itemAddr : fulfilmentItem.getFlFulfilmentItemAddr()){
//				if(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().contains(itemAddr.getFlDeliveryAddress())) {
//					sumorderedQty = sumorderedQty.add(itemAddr.getOrderedQty());
//				}
//				requestedQty = itemAddr.getScRequestItem().getQuantity();
//			}
//			if(requestedQty!=null && requestedQty.intValue() != 0){
//				if(requestedQty.intValue() > sumorderedQty.intValue()){
//					throw new EpException(MessageConst.ERROR_740007,new String[]{sumorderedQty.toString() ,requestedQty.toString(),fulfilmentItem.getCmItem().getItemName()});
//				}else if(requestedQty.intValue() < sumorderedQty.intValue()){
//					throw new EpException(MessageConst.ERROR_740007,new String[]{sumorderedQty.toString() ,requestedQty.toString(),fulfilmentItem.getCmItem().getItemName()});
//				}
//			}
//		}
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
	
}
