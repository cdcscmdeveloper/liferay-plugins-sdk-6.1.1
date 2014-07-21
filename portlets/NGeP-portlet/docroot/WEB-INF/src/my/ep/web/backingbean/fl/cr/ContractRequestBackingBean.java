/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.cr;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.biz.service.ct.dtl.IContractZoneSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
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
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.dtl.ContractItemReqDTO;
import my.ep.dto.ct.dtl.ContractItemResDTO;
import my.ep.dto.ct.dtl.ContractZoneReqDTO;
import my.ep.dto.ct.dtl.ContractZoneResDTO;
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
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.codata.FLContractOrderDataType;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.VCtActiveContract;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
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
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.type.pm.VotFundType;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.cr.ContractRequestManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.CtActiveContractVO;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean for Create Contract Order
 * 
 * @author Giribabu
 * @since 20/9/2013
 */

@ManagedBean(name = "contractRequestBackingBean")
@RequestScoped
public class ContractRequestBackingBean extends BaseBackingBean<ContractRequestManagedBean> {
	private static Logger log = Logger.getLogger(ContractRequestBackingBean.class);
	public static final String NAV_TO_CREATE_CONTRACT_REQUEST = "/xhtml/fl/contractrequest/crBasePage.xhtml";
	private boolean checkFlg = false;
	private String className="@(.myCrClassName)";
	
	private String queryItemCode;
	private String queryItemName;
	private String queryDesc;
	
	private static Map<Long, PmVotFund> votFundCodeMap = new HashMap<Long, PmVotFund>();
	private static Map<Long, PmGlAccount> glAccountCodeMap = new HashMap<Long, PmGlAccount>();
	private static Map<Long, PmPrgActivity> progmActivityCodeMap = new HashMap<Long, PmPrgActivity>();
	private static Map<Long, PmProject> projectCodeMap = new HashMap<Long, PmProject>();
	private static Map<Long, PmSetia> setiaCodeMap = new HashMap<Long, PmSetia>();
	private static Map<Long, PmSubSetia> subSetiaCodeMap = new HashMap<Long, PmSubSetia>();
	
	// bulk charge popup
	private Long pmPrjSetia;
	private Long pmPrgActivity;
		
		// display charge for selected item in main form
		
	private Long disPmPrjSetia;
	private Long disPmPrgActivity;

	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public ContractRequestBackingBean() {
		if (getManagedBean().getFlRequest() == null) {
			System.out.println("==contructor==");
			getManagedBean().setFlRequest(new FlFulfilmentRequest());
			getManagedBean().getFlRequest().setIssuedOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setChargeOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setCreatedOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setPreparedOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setCtContract(new CtContract());
			getManagedBean().getFlRequest().getCtContract().setContractType(new PmParameter());
			getManagedBean().getFlRequest().getCtContract().setFulfillmentType(new PmParameter());
			getManagedBean().getFlRequest().getCtContract().setSmSupplier(new SmSupplier());
			getManagedBean().getFlRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
			getManagedBean().getFlRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
		}
		if(getManagedBean().getActiveContractList()==null) {
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			ContractReqDTO ctReqDTO = new ContractReqDTO();
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			ctReqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getParentOrgProfile().getOrgProfileId());
			ContractResDTO ctResDTO = contractSO.retrieveActiveContractList(ctReqDTO);
			getManagedBean().setActiveContractList(ctResDTO.getActiveContractList());
			
			populateContractListTree();
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
		if (isRequisitioner()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setRequisitioner(facesScopes.getUserSessionBean().getLoginId());
		}
		if (isApprover()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setApprover(facesScopes.getUserSessionBean().getLoginId());
		}
		if(getManagedBean().getUserGroupMap()==null && isRequisitioner()) {
			initializeApproverGroup();
		}
		if(getManagedBean().getFlRequest().getPreparedOrgProfile().getPmOrgValidity() == null){
			getManagedBean().getFlRequest().getPreparedOrgProfile().setPmOrgValidity(new PmOrgValidity());
			
		}
		if(getManagedBean().getFlRequest().getIssuedOrgProfile().getPmOrgValidity() == null){
			getManagedBean().getFlRequest().getIssuedOrgProfile().setPmOrgValidity(new PmOrgValidity());
			
		}
		if(getManagedBean().getFlRequest().getChargeOrgProfile().getPmOrgValidity() == null){
			getManagedBean().getFlRequest().getChargeOrgProfile().setPmOrgValidity(new PmOrgValidity());
			
		}
		getManagedBean().setDeleteButtonHide(true);
		
		if(isFlFulfilmentRequisitioner()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setFulfilmentRequisitioner(facesScopes.getUserSessionBean().getLoginId());
		}
	}

	public String submitAction(String action) throws Exception {
		IFulfilmentRequestSO iFulfilmentRequestSO = null;
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flRequest = getManagedBean().getFlRequest();
			if(isRequisitioner()) {
				reqDTO.setRequisitioner(Boolean.TRUE);
			}
			flRequest.setFinancialYear(Calendar.getInstance().get(Calendar.YEAR));
			
			//TODO waiting for sm api to check the current date against the suppleir exp date.
//			flRequest.getCtContract().getSmSupplier().getSmMofAccounts();
			//checkCeilingAmount(flRequest);
			//checkCeilingQty(flRequest);
			if(isRequisitioner()){
				System.out.println("getManagedBean().getApproverModel().getTarget(): "+getManagedBean().getApproverModel().getTarget());
				reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
				reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
				UserReqDTO userReqDTO = new UserReqDTO();
				userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
				UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
				flRequest.setCreatedOrgProfile(userResDTO.getUserOrgList().get(0).getPmOrgProfile());
			}
			reqDTO.setFlFulfilmentRequest(flRequest);
			reqDTO.setContractRequest(Boolean.TRUE);
			reqDTO.setMto(getManagedBean().isMto());
			reqDTO.setImportedItems(getManagedBean().isImportedItems());
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			reqDTO.setToken(token);
			reqDTO.setApproverMap(getManagedBean().getApproverMap());
			reqDTO.setPmUserOrg(getManagedBean().getPmUserOrg());
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			getSumOrderedQty(flRequest);

			iFulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
			FulfilmentResDTO resDTO = iFulfilmentRequestSO.saveFulfillmentRequest(reqDTO);
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
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_CONTRACT_REQUEST), flRequest.getDocNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}else {
					setFulfilmentRequestField(resDTO);
				}
			} 

		}  finally {
			iFulfilmentRequestSO = null;
		}
		return null;
	}
	private void setFulfilmentRequestField(FulfilmentResDTO resDTO) {
		getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
		getManagedBean().getFlRequest().setTitle(getManagedBean().getFlRequest().getTitle().trim());
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
		getManagedBean().setSelectedTabIndex(0);
		getSumOrderedQty(getManagedBean().getFlRequest());

		if(getManagedBean().getApproverModel()==null) {
			List<String> source = new ArrayList<String>(); 
	        List<String> target = new ArrayList<String>();  
	        getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}	
		
		if(getManagedBean().getFlRequest().getPmUserGroup()!=null) {
			List<String> source = new ArrayList<String>();
			PmUserGroup userGroup = null;
			if(getManagedBean().getUserGroupMap()!=null) {
				userGroup = getManagedBean().getUserGroupMap().get(getManagedBean().getFlRequest().getPmUserGroup().getUserGroupId());
			} else {
				userGroup = getManagedBean().getFlRequest().getPmUserGroup();
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
		
		for(FlFulfilmentItem flItem : getManagedBean().getFlRequest().getFlFulfilmentItems()) { 
			String key = flItem.getCmItem().getItemId().toString() + flItem.getUnitPrice().doubleValue() + "";
		    if(!getManagedBean().getFlItemMap().containsKey(key)) {
			    getManagedBean().getFlItemMap().put(key, flItem);
		    }
		}
		
		//initialize all the COA map
		getVotFundList(getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId());
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
	public List<SelectItem> getSupplierBranchesList(List<SmSupplierBranch> branches){
		
		List<SelectItem> supplierBranches = new ArrayList<SelectItem>();
		for (SmSupplierBranch supplierBranch : branches) {
			supplierBranches.add(new SelectItem(supplierBranch.getSupplierBranchId(),supplierBranch.getBranchName()));
		}
		getManagedBean().setSupplierBranchesList(supplierBranches);
		return supplierBranches;
	}

	public void removeAddRow(){
		if(null !=getManagedBean().getFlRequest().getFlDeliveryAddresses()){
			Iterator<FlDeliveryAddress> delAddressIterator=getManagedBean().getFlRequest().getFlDeliveryAddresses().iterator();
			while(delAddressIterator.hasNext()){
				FlDeliveryAddress  deliveryAddress=delAddressIterator.next();
				if(deliveryAddress.isSelectedAddressRowDelete()){
					delAddressIterator.remove();
				}
			}
			if(getManagedBean().getFlRequest().getFlDeliveryAddresses().size()>0) {
				getManagedBean().setSelectedDelAddr(getManagedBean().getFlRequest().getFlDeliveryAddresses().get(0));
			} else {
				getManagedBean().setSelectedDelAddr(null);
			}
		}
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
	private boolean isDeliveryAddressContains(PmAddressType addrType, List<FlDeliveryAddress> flDeliveryAddresses) {
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

	public void flDeliveryAddrList() {
		ContractAmendmentReqDTO ctReqDTO = new ContractAmendmentReqDTO();
		ctReqDTO.setCtContract(getManagedBean().getFlRequest().getCtContract());
		ctReqDTO.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
		IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractAmendmentSO.class);
		ContractAmendmentResDTO resDTO = ctAmendmentSO.getActiveLocation(ctReqDTO);
		System.out.println("address List::: "+resDTO.getAddressTypeList().size());
		
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
			ContractAmendmentReqDTO ctReqDTO = new ContractAmendmentReqDTO();
			ctReqDTO.setCtContract(getManagedBean().getFlRequest().getCtContract());
			ctReqDTO.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
			IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractAmendmentSO.class);
			ContractAmendmentResDTO ctAmendmentResDTO = ctAmendmentSO.getActiveLocation(ctReqDTO);
			System.out.println("address List::: "+ctAmendmentResDTO.getAddressTypeList().size());
			getManagedBean().setAddressTypesList(ctAmendmentResDTO.getAddressTypeList());
		}
		if(getManagedBean().getManualAddressMap() == null || getManagedBean().getManualAddressMap().size() == 0){
			setManualAddressList();
		}		
	}
	public void updateDeliveryAddrDt() {
		if (null == getManagedBean().getSelectedAddrTypeList()
				|| getManagedBean().getSelectedAddrTypeList().isEmpty()) {
			return;
		}
		IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IParameterSO.class);
		IContractZoneSO contractZoneSO = (IContractZoneSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractZoneSO.class);
		ParameterReqDTO paramReqDTO = new ParameterReqDTO();
		FlDeliveryAddress deliveryAddr = null;
		int i = 0;
		for (PmAddressType addrType : getManagedBean().getSelectedAddrTypeList()) {
			//Validate to eliminate duplicate address. 
			boolean isAddressContains = isDeliveryAddressContains(addrType,getManagedBean().getFlRequest().getFlDeliveryAddresses());
			if (!isAddressContains) {
				deliveryAddr = new FlDeliveryAddress();
				if(getManagedBean().getFlRequest().getCtContract().isZonalPricing()){
					getManagedBean().setZonal(true);
					ContractZoneReqDTO zoneReqDTO = new ContractZoneReqDTO();
					zoneReqDTO.setPmAddress(addrType.getPmAddress());
					zoneReqDTO.setCtContract(getManagedBean().getFlRequest().getCtContract());
					ContractZoneResDTO zoneResDTO = contractZoneSO.getDeliveryTermByAddress(zoneReqDTO);
					
					deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFlRequest());
					deliveryAddr.setAddressType(addrType);
					paramReqDTO.setParameterId(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getDeliveryTermUom().getParameterId());
					deliveryAddr.setPmUom(zoneResDTO.getDeliveryTermUom());
					deliveryAddr.setDeliveryTerm(zoneResDTO.getDeliveryTerm());
					deliveryAddr.setStartDate(new Timestamp(System.currentTimeMillis()));
					
					Date startDate = deliveryAddr.getStartDate();
					int deliveryTerm = deliveryAddr.getDeliveryTerm();
					if(deliveryAddr.getPmUom().getParameterCode().equalsIgnoreCase(ParameterConst.DELIVERY_TERM_DAYS)){
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(startDate);
						calendar.add(Calendar.DATE, deliveryTerm);
						deliveryAddr.setEndDate(calendar.getTime());
					}else if(deliveryAddr.getPmUom().getParameterCode().equalsIgnoreCase(ParameterConst.DELIVERY_TERM_MONTHS)){
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(startDate);
						calendar.add(Calendar.MONTH, deliveryTerm);
						deliveryAddr.setEndDate(calendar.getTime());
					}
					
					
				}else{
					deliveryAddr.setStartDate(getManagedBean().getFlRequest().getStartDate());
					deliveryAddr.setEndDate(getManagedBean().getFlRequest().getEndDate());
					deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFlRequest());
					deliveryAddr.setAddressType(addrType);
					paramReqDTO.setParameterId(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getDeliveryTermUom().getParameterId());
					deliveryAddr.setPmUom(parameterSO.retrieveParameterById(paramReqDTO).getPmParameter());
					deliveryAddr.setDeliveryTerm(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getDeliveryTerm());
				}
				
				deliveryAddr.setPmAddress(addrType.getPmAddress());
				checkItemsFromSearchCatalogue(addrType,deliveryAddr);
				if(i==0) {
					getManagedBean().setSelectedDelAddr(deliveryAddr);
				}
				i++;
				if (null == getManagedBean().getFlRequest().getFlDeliveryAddresses()) {
					getManagedBean().getFlRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
				}
				
				getManagedBean().getFlRequest().getFlDeliveryAddresses().add(deliveryAddr);
			}	
		}
		getManagedBean().setAddressTypesList(new ArrayList<PmAddressType>());
	}
	public void updateManualDeliveryAddrDt() {
		IContractZoneSO contractZoneSO = (IContractZoneSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractZoneSO.class);
		if(null== getManagedBean().getFlManulaAddress().getPmAddress() ||  getManagedBean().getFlManulaAddress().isEditable()){
			return;
		}
		FlDeliveryAddress deliveryAddr = null;
		PmAddress manualAddr=null;
		manualAddr=getManagedBean().getFlManulaAddress().getPmAddress();
		deliveryAddr = new FlDeliveryAddress();
		deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFlRequest());
		deliveryAddr.setPmAddress(manualAddr);
		//deliveryAddr.setAddressType(manualAddr);
		if(getManagedBean().getFlRequest().getCtContract().isZonalPricing()){
			ContractZoneReqDTO zoneReqDTO = new ContractZoneReqDTO();
			zoneReqDTO.setPmAddress(manualAddr);
			zoneReqDTO.setCtContract(getManagedBean().getFlRequest().getCtContract());
			ContractZoneResDTO zoneResDTO = contractZoneSO.getDeliveryTermByAddress(zoneReqDTO);
			deliveryAddr.setPmUom(zoneResDTO.getDeliveryTermUom());
			deliveryAddr.setDeliveryTerm(zoneResDTO.getDeliveryTerm());
			deliveryAddr.setStartDate(new Timestamp(System.currentTimeMillis()));
			
			Date startDate = deliveryAddr.getStartDate();
			int deliveryTerm = deliveryAddr.getDeliveryTerm();
			if(deliveryAddr.getPmUom().getParameterCode().equalsIgnoreCase(ParameterConst.DELIVERY_TERM_DAYS)){
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(startDate);
				calendar.add(Calendar.DATE, deliveryTerm);
				deliveryAddr.setEndDate(calendar.getTime());
			}else if(deliveryAddr.getPmUom().getParameterCode().equalsIgnoreCase(ParameterConst.DELIVERY_TERM_MONTHS)){
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(startDate);
				calendar.add(Calendar.MONTH, deliveryTerm);
				deliveryAddr.setEndDate(calendar.getTime());
			}
		}else{
			deliveryAddr.setStartDate(getManagedBean().getFlRequest().getStartDate());
			deliveryAddr.setEndDate(getManagedBean().getFlRequest().getEndDate());
			deliveryAddr.setPmUom(getManagedBean().getFlRequest().getPmUom());
			deliveryAddr.setDeliveryTerm(getManagedBean().getFlRequest().getDeliveryTerm());
		}
		deliveryAddr.setReceivingGrp(getManagedBean().getFlManulaAddress().getReceivingGrp());
		checkItemsFromSearchCatalogue(null,deliveryAddr);
		getManagedBean().setSelectedDelAddr(deliveryAddr);
		getManagedBean().getFlRequest().getFlDeliveryAddresses().add(deliveryAddr);
		
		// add to cart validation
		RequestContext.getCurrentInstance().execute("addManualDeliveryAddr.hide()");
		getManagedBean().setRequired(Boolean.FALSE);
	}

	public void procureContract() throws PortalException, SystemException {
		Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("contractId"));
		String scheduleNo = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("scheduleNo");
		System.out.println("scheduleNo: "+scheduleNo);
		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay)facesScope.getRequestScope(WebKeys.THEME_DISPLAY);
		
		String commonUrl = themeDisplay.getURLCurrent().substring(0, themeDisplay.getURLCurrent().indexOf(themeDisplay.getLayout().getGroup().getFriendlyURL())+themeDisplay.getLayout().getGroup().getFriendlyURL().length());
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String url = "contract-request";

		try {
			externalContext.redirect(commonUrl+"/" + url+"?contractId="+contractId+"&scheduleNo="+scheduleNo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void checkPtjFlg() {
		getManagedBean().setCheckPtjFlg(false);

	}

	public void addContractedItems() {
		getManagedBean().setContractedItemList(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractItems());
	}
	
	public void updateContractedItemDt() {//TODO db changes qtitem change
		FlFulfilmentItem flItem = null;
		for (CtContractItem contractedItem : getManagedBean().getSelectedContractedItemList()) {
			
			IContractItemSO ctItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);
			ContractItemReqDTO reqDTO = new ContractItemReqDTO();
			reqDTO.setPmAddress(getManagedBean().getSelectedDelAddr().getPmAddress());
			reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
			reqDTO.setOrderedQty(new BigDecimal(1));
			reqDTO.setItemId(contractedItem.getScRequestItem().getRequestNoteItemId());
			ContractItemResDTO resDTO = ctItemSO.getItemPrice(reqDTO);
			FlFulfilmentItemAddr flItemAddr = new FlFulfilmentItemAddr();
			flItemAddr.setFlFulfilmentItem(flItem);
			flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
			flItemAddr.setUnitPrice(resDTO.getItemPrice());
			flItemAddr.setCmItem(contractedItem.getScRequestItem().getCmItem());
			flItemAddr.setScRequestItem(contractedItem.getScRequestItem());
			flItemAddr.setItemType(contractedItem.getItemType());
			
			if(getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr() == null){
				getManagedBean().getSelectedDelAddr().setFlFulfilmentItemAddr(new ArrayList<FlFulfilmentItemAddr>());
			}
			
			//Code for check items already assigned to Selected Address
			boolean itemExisted=false;
			for (FlFulfilmentItemAddr selectedItem : getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()) {
				if (selectedItem.getScRequestItem().getRequestNoteItemId().equals(flItemAddr.getScRequestItem().getRequestNoteItemId())) {
					itemExisted=true;
					throw new EpException(MessageConst.ERROR_740022);
				}
			}
			if(!itemExisted){
				getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().add(flItemAddr);
			}
		}
	}
	

	private List<String> getAvailableApproverList() {
		List<String> availableApprovers = new ArrayList<String>();
		IUserSO userSO = null;
		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			PmUser user = getFacesScopes().getUserSessionBean();

			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(user.getUserId());
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			PmUserOrg userOrg = resDTO.getUserOrgList().get(0);
			reqDTO.setOrgProfileId(userOrg.getPmOrgProfile().getOrgProfileId());

			resDTO = userSO.retrieveUserGroupList(reqDTO);
			for (PmUserGroupUser userGroup : resDTO.getUserGroupList().get(0).getPmUserGroupUsers()) {
				getManagedBean().getApproverMap().put(userGroup.getPmUser().getUserName(), userGroup);
				availableApprovers.add(userGroup.getPmUser().getUserName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							"Circular save failed."));
		} finally {
			userSO = null;
		}
		return availableApprovers;
	}

	public void preRenderView() {

		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		String contractId = originalServletRequest.getParameter("contractId");
		String scheduleNo = originalServletRequest.getParameter("scheduleNo");
		
		System.out.println("taskId:" + taskId);
		if (taskId != null) {
			renewSesionObject();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();

			IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",
							IFulfilmentRequestSO.class);
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			FLContractOrderDataType coObj = (FLContractOrderDataType) obj;
			if (coObj.getContractId().intValue() == 0) {
				flReqDTO.setFlRequestId(coObj.getAssociatedCrId());

			} else {
				flReqDTO.setFlRequestId(coObj.getContractId());
			}

			///flReqDTO.setSelectedApprovers(coObj.getApproverList());
			resDTO = requestSO.retrieveFulfilmentRequest(flReqDTO);

			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
			flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			flBB.initializeRemarkAndAttachement(flBB.getManagedBean().getRemarkList(), flBB.getManagedBean().getAttachmentList());

			if (coObj.getContractId().intValue() == 0) {//TODO
				FlFulfilmentRequest flRequest = new FlFulfilmentRequest();
				IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
				ContractReqDTO contractReqDTO = new ContractReqDTO();
				contractReqDTO.setContractId(resDTO.getFlFulfilmentRequest().getCtContract().getContractId());
			
				resDTO.getFlFulfilmentRequest().setCtContract(contractSO.retrieveContract(contractReqDTO).getContract());//TODO
				//TODO db changes
//				contractReqDTO.setLoaId(resDTO.getFlFulfilmentRequest().getCtContract().getLatestContractVer().getCtMtoLoas().get(0).getLoa().getLoaId());
				ContractResDTO ctResDTO = contractSO.retrieveContractByLoaId(contractReqDTO);
				
				flRequest.setCtContract(ctResDTO.getContract());
//				flRequest.setCtContract(contractSO.retrieveNormalContractByMtoContract(contractReqDTO).getContract());
				getManagedBean().setFlRequest(flRequest);
				getManagedBean().getFlRequest().setPmUserGroup(new PmUserGroup());
				getManagedBean().getFlRequest().setIssuedOrgProfile(resDTO.getFlFulfilmentRequest().getIssuedOrgProfile());
				getManagedBean().getFlRequest().setChargeOrgProfile(resDTO.getFlFulfilmentRequest().getChargeOrgProfile());
				getManagedBean().getFlRequest().setPreparedOrgProfile(resDTO.getFlFulfilmentRequest().getIssuedOrgProfile());
				getManagedBean().getFlRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
				setFulfilmentRequestField(resDTO);
			} else {
				getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			}
			combineSupAddress(resDTO.getFlFulfilmentRequest());
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(res.getAction());
			if(isRequisitioner()){
				System.out.println("resDTO.getSelectedApproverList(): "+resDTO.getSelectedApproverList());
				getManagedBean().getApproverModel().setTarget(resDTO.getSelectedApproverList());
				getManagedBean().getApproverModel().getSource().removeAll(resDTO.getSelectedApproverList());
			}
			
			getManagedBean().setMinistryName(resDTO.getFlFulfilmentRequest().getIssuedOrgProfile().getParentOrgProfile().getOrgName());
			if(!getManagedBean().getFlRequest().getFlDeliveryAddresses().isEmpty()){
				getManagedBean().setSelectedDelAddr(getManagedBean().getFlRequest().getFlDeliveryAddresses().get(0));
			}
			
			if(getManagedBean().getFlManualAddress() == null){
				getManagedBean().setFlManualAddress(new FlDeliveryAddress());
				getManagedBean().getFlManualAddress().setDeliveryAddressId(1L);
				getManagedBean().getFlManualAddress().setPmAddress(new PmAddress());
				//getManagedBean().getFlManualAddress().getPmAddress().setAddressId(1L);
				getManagedBean().getFlManualAddress().getPmAddress().setPmCountry(new PmCountry());
				getManagedBean().getFlManualAddress().getPmAddress().getPmCountry().setCountryId(1L);
				getManagedBean().getFlManualAddress().getPmAddress().setPmState(new PmState());
				getManagedBean().getFlManualAddress().getPmAddress().getPmState().setStateId(1L);
				getManagedBean().getFlManualAddress().getPmAddress().setPmDistrict(new PmDistrict());
				getManagedBean().getFlManualAddress().getPmAddress().getPmDistrict().setDistrictId(1L);
				getManagedBean().getFlManualAddress().getPmAddress().setPmCity(new PmCity());
				getManagedBean().getFlManualAddress().getPmAddress().getPmCity().setCityId(1L);
				getManagedBean().getFlManualAddress().getPmAddress().setPmDivision(new PmDivision());
				getManagedBean().getFlManualAddress().getPmAddress().getPmDivision().setDivisionId(1L);
			}
			getManagedBean().setSelectedTabIndex(0);
		}else if(contractId != null){
			renewSesionObject();
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setContractId(Long.parseLong(contractId));
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			IContractSO contractSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			IStatusSO statusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			ContractReqDTO ctDTO = new ContractReqDTO();
			ctDTO.setContractId(getManagedBean().getContractId());
			ContractResDTO contractResDTO = contractSO.retrieveContract(ctDTO);
			getManagedBean().getFlRequest().setCtContract(contractResDTO.getContract());
			getManagedBean().getFlRequest().setScheduleNo(scheduleNo);
			getManagedBean().getFlRequest().setSmSupplier(contractResDTO.getContract().getSmSupplier());
			getManagedBean().getFlRequest().setFinancialYear(Calendar.getInstance().get(Calendar.YEAR));
			getManagedBean().getFlRequest().setStartDate(new Timestamp(System.currentTimeMillis()));
			StatusReqDTO statusReqDTO = new StatusReqDTO();
			statusReqDTO.setStatusId(FulfilmentStatusConst.CR_PENDING_SUBMISSION);
			StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
			getManagedBean().getFlRequest().setStatus(statusResDTO.getPmStatus());
			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();
			List<VCmSupplierItem> vCmSupplierItemList = (List<VCmSupplierItem>) externalContext.getApplicationMap().get(facesScopes.getUserSessionBean().getUserId().toString()+contractId.toString());
			if(vCmSupplierItemList != null){
				getManagedBean().setvCmSupplierItemList(vCmSupplierItemList);
				externalContext.getApplicationMap().put(facesScopes.getUserSessionBean().getUserId().toString()+contractId.toString(), null);
			}
			
			for(CtAgency agency : getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtAgencies()){
				
				if(agency == getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtAgencies().get(0)){
					getManagedBean().setOrgProfileIds(agency.getOrgProfile().getOrgProfileId().toString());
				}else{
					getManagedBean().setOrgProfileIds(getManagedBean().getOrgProfileIds() + "," +agency.getOrgProfile().getOrgProfileId().toString());
				}
				
			}
			IUserSO profileSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(facesScopes.getUserSessionBean().getUserId());
			UserResDTO userResDTO = profileSO.retrieveUserOrgListByUserId(userReqDTO);
			getManagedBean().getFlRequest().setPreparedOrgProfile(userResDTO.getUserOrgList().get(0).getPmOrgProfile());
			getManagedBean().getFlRequest().setChargeOrgProfile(userResDTO.getUserOrgList().get(0).getPmOrgProfile());
			getManagedBean().getFlRequest().setIssuedOrgProfile(userResDTO.getUserOrgList().get(0).getPmOrgProfile());
			//TODO
			/*if(contractResDTO.getContract().getSmSupplierBranch() != null){
				getManagedBean().getFlRequest().setSupplierBranch(contractResDTO.getContract().getSmSupplierBranch());
			}*/
			getLoginUserOrg();
			// getManagedBean().setMinistryName(getManagedBean().getPmUserOrg().getPmOrgProfile().getParentOrgProfile().getParentOrgProfile().getOrgName());
			String[] outcome = new String[2];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			outcome[1] = BpmOutcomeConst.BPM_SUBMIT;
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(outcome);
			FulfilmentBackingBean flBackingBean = new FulfilmentBackingBean();
			flBackingBean.initializeRemarkAndAttachement(flBB.getManagedBean().getRemarkList(), flBB.getManagedBean().getAttachmentList());
			getManagedBean().setSelectedTabIndex(0);
		}
		
		if(getManagedBean().getFlRequest().getCtContract().isZonalPricing()){
			getManagedBean().setFlManualAddress(new FlDeliveryAddress());
			getManagedBean().getFlManualAddress().setDeliveryAddressId(1L);
			getManagedBean().getFlManualAddress().setPmAddress(new PmAddress());
			//getManagedBean().getFlManualAddress().getPmAddress().setAddressId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmCountry(new PmCountry());
			getManagedBean().getFlManualAddress().getPmAddress().getPmCountry().setCountryId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmState(new PmState());
			//getManagedBean().getFlManualAddress().getPmAddress().setPmState(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractZones().get(0).getCtContractZoneDtls().get(0).getState());
			getManagedBean().getFlManualAddress().getPmAddress().setPmDistrict(new PmDistrict());
			getManagedBean().getFlManualAddress().getPmAddress().getPmDistrict().setDistrictId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmCity(new PmCity());
			getManagedBean().getFlManualAddress().getPmAddress().getPmCity().setCityId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmDivision(new PmDivision());
			getManagedBean().getFlManualAddress().getPmAddress().getPmDivision().setDivisionId(1L);
		}else if(getManagedBean().getFlManualAddress() == null){
			getManagedBean().setFlManualAddress(new FlDeliveryAddress());
			getManagedBean().getFlManualAddress().setDeliveryAddressId(1L);
			getManagedBean().getFlManualAddress().setPmAddress(new PmAddress());
			//getManagedBean().getFlManualAddress().getPmAddress().setAddressId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmCountry(new PmCountry());
			getManagedBean().getFlManualAddress().getPmAddress().getPmCountry().setCountryId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmState(new PmState());
			getManagedBean().getFlManualAddress().getPmAddress().getPmState().setStateId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmDistrict(new PmDistrict());
			getManagedBean().getFlManualAddress().getPmAddress().getPmDistrict().setDistrictId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmCity(new PmCity());
			getManagedBean().getFlManualAddress().getPmAddress().getPmCity().setCityId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmDivision(new PmDivision());
			getManagedBean().getFlManualAddress().getPmAddress().getPmDivision().setDivisionId(1L);
			
		}
	}

	public void calculateSubTotal(BigDecimal quantity) {
		// getManagedBean().getSelectedItemAddr().setSubTotal(quantity);
		getManagedBean().setSubTotal(quantity);
	}

	/**
	 * @return the checkFlg
	 */
	public boolean isCheckFlg() {
		return checkFlg;
	}

	/**
	 * @param checkFlg
	 *            the checkFlg to set
	 */
	public void setCheckFlg(boolean checkFlg) {
		this.checkFlg = checkFlg;
	}

	public void getLoginUserOrg() {
		PmUser user = getFacesScopes().getUserSessionBean();

		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(user.getUserId());
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IUserSO.class);
		UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

		PmUserOrg userOrg = resDTO.getUserOrgList().get(0);
		getManagedBean().setPmUserOrg(userOrg);

	}

	public void updateIssuedJabatan() {
		System.out.println("-updateIssuedJabatan- "+ getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId());

		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		if (getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId() != null) {

			reqDTO.setOrgProfileId(getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId());
			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileByProfileId(reqDTO);
			getManagedBean().getFlRequest().setIssuedOrgProfile(resDTO.getPmOrgProfile());
			getManagedBean().getFlRequest().setChargeOrgProfile(resDTO.getPmOrgProfile());
		} else {
			getManagedBean().getFlRequest().setIssuedOrgProfile(null);
			getManagedBean().getFlRequest().setChargeOrgProfile(null);
		}
	}

	public boolean isRequisitioner() {
		return getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value());

	}
	
	public boolean isApprover() {
		return getFacesScopes().hasRoleUser(RoleCode.APPROVER.value());

	}
	
	public void checkCeilingAmount(FlFulfilmentRequest flRequest){
		IContractItemSO contractItemSO = BusinessDelegate.getInstance().getServiceObject("ct", IContractItemSO.class);
		ContractItemReqDTO ctReqDTO = new ContractItemReqDTO();
		ctReqDTO.setContractId(flRequest.getCtContract().getContractId());
		ctReqDTO.setOrgProfileId(flRequest.getIssuedOrgProfile().getOrgProfileId());
		
		ContractItemResDTO ctResDTO = new ContractItemResDTO();
		ctResDTO = contractItemSO.getAgencyCeilingAmount(ctReqDTO);
		ctResDTO.getAgencyCeilingAmount();//TODO flRequest.deladdr loop then sum the flItemaddr orederAmt AND create SO get the ptj previous oredered amt , check contractId and issuePtj same then sum it and check with ceiling amt
		BigDecimal sumOrderedAmt = new BigDecimal("0");
		BigDecimal checkCeilingAmt = new BigDecimal("0");
		for(FlDeliveryAddress delAddr: flRequest.getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddr : delAddr.getFlFulfilmentItemAddr()){
				
				sumOrderedAmt = sumOrderedAmt.add(itemAddr.getOrderedQty());//TODO change back to getOrderedAmount
				System.out.println("sumOrderedAmt: "+sumOrderedAmt);
			}
		}
		IFulfilmentRequestSO iFulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setIssuedOrgId(getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId());
		reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
		FulfilmentResDTO resDTO = iFulfilmentRequestSO.retrievePreviousSumOrderedAmtByPtj(reqDTO);
		checkCeilingAmt = resDTO.getPrevSumAmt().add(sumOrderedAmt);
		
		System.out.println("checkCeilingAmt total: "+checkCeilingAmt);
		System.out.println("getAgencyCeilingAmount: "+ctResDTO.getAgencyCeilingAmount().intValue());
		if(checkCeilingAmt.intValue() >  ctResDTO.getAgencyCeilingAmount().intValue()){
			throw new EpException("This PTJ has exceed the limit of the celing amount.");
		}
	}
	
	public void checkCeilingQty(FlFulfilmentRequest flRequest){//TODO db change qt item change
		IContractItemSO contractItemSO = BusinessDelegate.getInstance().getServiceObject("ct", IContractItemSO.class);
		ContractItemReqDTO ctReqDTO = new ContractItemReqDTO();
		ctReqDTO.setContractId(flRequest.getCtContract().getContractId());
		ctReqDTO.setOrgProfileId(flRequest.getIssuedOrgProfile().getOrgProfileId());

		ContractItemResDTO ctResDTO = new ContractItemResDTO();
		
		for(FlFulfilmentItem item : getManagedBean().getFlRequest().getFlFulfilmentItems()){
			BigDecimal sumOrderedQty = new BigDecimal("0");
			BigDecimal checkCeilingQty = new BigDecimal("0");
//			ctReqDTO.setItemId(item.getQtItem().getQtItemId());
			ctResDTO = contractItemSO.getItemCeilingQuantity(ctReqDTO);
			ctResDTO.getItemCeilingQuantity();//TODO loop flrequest.flitems loop again itemAddr and sum ordered qty AND create SO get the ptj previous ordered qty,check contracID and issuePTJ same then sum it and check with ceiling Qty

			for(FlFulfilmentItemAddr itemAddr : item.getFlFulfilmentItemAddr()){
				sumOrderedQty = sumOrderedQty.add(itemAddr.getOrderedQty());
				System.out.println("sumOrderedQty: "+sumOrderedQty);
			}
			IFulfilmentRequestSO iFulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setIssuedOrgId(getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId());
			reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
			FulfilmentResDTO resDTO = iFulfilmentRequestSO.retrievePreviousSumOrderedQtyByPtj(reqDTO);
			checkCeilingQty = resDTO.getPrevSumQty().add(sumOrderedQty);

			System.out.println("checkCeilingQty total: "+checkCeilingQty);
			System.out.println("getAgencyCeilingAmount: "+ctResDTO.getItemCeilingQuantity());
			if(checkCeilingQty.intValue() >  ctResDTO.getItemCeilingQuantity().intValue()){
//				throw new EpException("Item "+ item.getQtItem().getItemName() +"  has been exceeded the quantity limit.");
			}
		}
		
	}
	
	
	private void initializeApproverGroup() {
		IUserSO userSO = null;
		try {
			userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
			
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			
			reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
			resDTO = userSO.retrieveUserGroupList(reqDTO);
			
			if(getManagedBean().getFlRequest().getPmUserGroup()==null) {
				PmUserGroup pmUserGroup = new PmUserGroup();
				getManagedBean().getFlRequest().setPmUserGroup(pmUserGroup);
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
					getManagedBean().getFlRequest().getPmUserGroup().setUserGroupId(userGroup.getUserGroupId());
					List<String> source = new ArrayList<String>();
					for(PmUserGroupUser user : userGroup.getPmUserGroupUsers()) {
						getManagedBean().getApproverMap().put(user.getPmUser().getUserName(), user);
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
	
	public void userGroupChangeListener() {
		PmUserGroup selectedUserGroup = getManagedBean().getUserGroupMap().get(getManagedBean().getFlRequest().getPmUserGroup().getUserGroupId());
		List<String> source = new ArrayList<String>(); 
        List<String> target = new ArrayList<String>();  
		for(PmUserGroupUser userGroupUser : selectedUserGroup.getPmUserGroupUsers()) {
			source.add(userGroupUser.getPmUser().getUserName());
		}
		getManagedBean().getApproverModel().setSource(source);
		getManagedBean().getApproverModel().setTarget(target);
	}
	
	public SelectItem[] getUserGroupDropdown() {
		SelectItem[] items = null;
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
	
	
	public void retrieveZonalPrice(FlFulfilmentItemAddr flItemAddr) {
		System.out.println("event "+flItemAddr.getFulfilmentItemAddrId());
		int unitPrice = 0;
		int orderedQty = 0;
		int orderedAmt = 0;
		if(null != flItemAddr.getUnitPrice()){
			unitPrice= flItemAddr.getUnitPrice().intValue();
			{
				if(null != flItemAddr.getOrderedQty()){
					orderedQty = flItemAddr.getOrderedQty().intValue();
				}
				orderedAmt =((unitPrice) * (orderedQty));
				flItemAddr.setOrderedAmt(new BigDecimal(orderedAmt) );
			}
		}
	}
	
	public void retrieveOrderAmount(FlFulfilmentItemAddr flItemAddr){
		IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,	IContractItemSO.class);
		ContractItemReqDTO reqDTO = new ContractItemReqDTO();
		reqDTO.setPmAddress(getManagedBean().getSelectedDelAddr().getPmAddress());
		reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
		reqDTO.setItemId(flItemAddr.getScRequestItem().getRequestNoteItemId());
		if(null == flItemAddr.getOrderedQty()){
			flItemAddr.setOrderedAmt(new BigDecimal("0"));
		} else {
			reqDTO.setOrderedQty(flItemAddr.getOrderedQty());
			ContractItemResDTO resDTO = contractItemSO.getItemPrice(reqDTO);
			flItemAddr.setUnitPrice(resDTO.getItemPrice());
			flItemAddr.setOrderedAmt(flItemAddr.getOrderedQty().multiply(flItemAddr.getUnitPrice()));
			flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
			    
			if(flItemAddr.getFlFulfilmentItem()!=null) {
				flItemAddr.getFlFulfilmentItem().setUnitPrice(flItemAddr.getUnitPrice());
			} else {
				FlFulfilmentItem fulfilmentItem = null;
				String key = flItemAddr.getCmItem().getItemId().toString() + flItemAddr.getUnitPrice().doubleValue() + "";
			    if(!getManagedBean().getFlItemMap().containsKey(key)) {
					fulfilmentItem = new FlFulfilmentItem();
					fulfilmentItem.setFlFulfilmentRequest(getManagedBean().getFlRequest());
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
			    
			    if(!getManagedBean().getFlRequest().getFlFulfilmentItems().contains(fulfilmentItem)) {
			    	getManagedBean().getFlRequest().getFlFulfilmentItems().add(fulfilmentItem);
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
	
	public void editManualAddress(FlDeliveryAddress flDelAddress){
		getManagedBean().setFlManualAddress(flDelAddress);
		flDelAddress.setEditable(true);
		
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
	 public void combineSupAddress(FlFulfilmentRequest fulfilmentRequest){
	    	
	    	StringBuilder address = new StringBuilder();
	    	String strSpace = " ";
	    	//TODO
	    	/*if(null != fulfilmentRequest.getSupplierBranch()){
	    		if(null != fulfilmentRequest.getSupplierBranch().getSmAddress()){
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
	    			
	    		}
	    		
	    	}*/
	    	
	    	this.getManagedBean().setSupplierAddress(address.toString());
	    }
	
	public void receivingOfficerList(FlDeliveryAddress flDelAddress){
		Long addressId = flDelAddress.getPmAddress().getAddressId();
		if(flDelAddress.getReceivingGrp()!=null && flDelAddress.getReceivingGrp().getAddressTypeId()!=null) { // in case of Manual address
			ContractAmendmentReqDTO ctReqDTO = new ContractAmendmentReqDTO();
			ctReqDTO.setCtContract(getManagedBean().getFlRequest().getCtContract());
			ctReqDTO.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
			IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractAmendmentSO.class);
			ContractAmendmentResDTO resDTO = ctAmendmentSO.getActiveLocation(ctReqDTO);
			System.out.println("address List::: "+resDTO.getAddressTypeList().size());
			getManagedBean().setAddressTypesList(resDTO.getAddressTypeList());
			
			addressId = resDTO.getAddressTypeList().get(0).getPmAddress().getAddressId();
		}
		AddressResDTO resDTO=getReceivingOfficerList(addressId, AddressType.RECEIVING_OFFICER_ADDRESS.value());
		getManagedBean().setReceivingOffList(resDTO.getAddressTypeList());
	}
	public AddressResDTO getReceivingOfficerList(Long addressId, String addressType){
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAddressSO.class);
		AddressReqDTO reqDTO = new AddressReqDTO();
		reqDTO.setAddressId(addressId);
		reqDTO.setAddressType(addressType);
		AddressResDTO resDTO = addressSO.retrieveReceivingOfficerList(reqDTO);
		getManagedBean().setReceivingOffList(resDTO.getAddressTypeList());
		System.out.println("size ::: "+resDTO.getAddressTypeList().size());
		//resDTO.get
		return resDTO;
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
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==3){
			getManagedBean().setSelectedNode(null);
			getManagedBean().setVotFundCode(null);
			getManagedBean().populateChargeLineData();	
			setClassName("@(.crChargeLinePnlClass)");
			getVotFundList(getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId());
		}
		if(selectedTabIndex==1){
			if(getManagedBean().getSelectedDelAddr()==null) { 
				retrieveCountryList();
			}
		}
	}
	public void onNodeSelect() {
		TreeNode selectedNode=getManagedBean().getSelectedNode();
		onNodeUnSelect();
		if(null!=selectedNode){
			for(TreeNode children:selectedNode.getChildren()){
				FlFulfilmentItemAddr childData=((FlFulfilmentItemAddr)children.getData());
				if(null !=childData.getPmVotFund()){
					String votFundCode = votFundCodeMap.get(childData.getPmVotFund().getVotFundId()).getVotFundCode();
					getManagedBean().setVotFundCode(votFundCode);
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
	public void clearPopupLists(){
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
		
		updateSelectedTreeNodes();
		if(getManagedBean().getSelectedNodes().length==0) {
			throw new EpException(MessageConst.ERROR_100049);
		}
		RequestContext.getCurrentInstance().execute("applyBulkCharge.show()");
	}
	public void checkNodeSelected(){
		getManagedBean().setVotFundId(0L);
		getManagedBean().setPrgActivityId(0L);
		getManagedBean().setProjectId(0L);
		getManagedBean().setSetiaId(0L);
		getManagedBean().setSubSetiaId(0L);
		getManagedBean().setVotFundList(new ArrayList<PmVotFund>());
		getVotFundList(4L);
		getManagedBean().setProjectList(new ArrayList<PmProject>());
		getManagedBean().setPrgActivityList(new ArrayList<PmPrgActivity>());
		getManagedBean().setSetiaList(new ArrayList<PmSetia>());
		getManagedBean().setSubSetiaList(new ArrayList<PmSubSetia>());
		/*if(null == getManagedBean().getSelectedNodes()){
			throw new EpException("Please Select Item Node.");
			
		}*/
		
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
	public void selectAllCheckboxSelect(AjaxBehaviorEvent e){
		if((boolean) e.getComponent().getAttributes().get("value")){
			for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFlRequest().getFlDeliveryAddresses()){
				if(null!=deliveryAddress){
					deliveryAddress.setSelectedAddressRowDelete(true);
				}
			}
			getManagedBean().setDeleteButtonHide(false);
			
		}else{
			for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFlRequest().getFlDeliveryAddresses()){
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
			for(TreeNode selectedNode : getManagedBean().getSelectedNodes()){
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
				for (FlDeliveryAddress delAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()) {
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
			getManagedBean().populateChargeLineData();
			RequestContext.getCurrentInstance().execute("applyBulkCharge.hide()");
		} finally {
			parameterSO = null;
		}
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
		for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFlRequest().getFlDeliveryAddresses()){
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
		if(null !=getManagedBean().getFlRequest().getFlDeliveryAddresses()){
			for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFlRequest().getFlDeliveryAddresses()){
				if(null!=deliveryAddress.getFlFulfilmentItemAddr()){
					for(FlFulfilmentItemAddr itemAddress:deliveryAddress.getFlFulfilmentItemAddr()){
						if(null !=selectedNodes){
							for(TreeNode node:selectedNodes){
								//getRequestNoteItemId()
								FlFulfilmentItemAddr selectedItem=(FlFulfilmentItemAddr)node.getData();
								//itemAddress.getCmItem().getItemId()
								if(itemAddress.getCmItem().getItemId().equals(selectedItem.getCmItem().getItemId())){
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

	// display itemCode,itemName,QueryDesc in popup
	public void showItemQuery(FlFulfilmentItemAddr flItemAddress){
		
		flItemAddress.getScRequestItem().getItemName();
		setQueryItemCode(flItemAddress.getCmItem().getItemId().toString());
		setQueryItemName(flItemAddress.getScRequestItem().getItemName());
	}
	

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
	 * @return the glAccountCodeMap
	 */
	public static Map<Long, PmGlAccount> getGlAccountCodeMap() {
		return glAccountCodeMap;
	}

	/**
	 * @param glAccountCodeMap the glAccountCodeMap to set
	 */
	public static void setGlAccountCodeMap(Map<Long, PmGlAccount> glAccountCodeMap) {
		ContractRequestBackingBean.glAccountCodeMap = glAccountCodeMap;
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
		ContractRequestBackingBean.votFundCodeMap = votFundCodeMap;
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
		ContractRequestBackingBean.progmActivityCodeMap = progmActivityCodeMap;
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
		ContractRequestBackingBean.projectCodeMap = projectCodeMap;
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
		ContractRequestBackingBean.setiaCodeMap = setiaCodeMap;
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
		ContractRequestBackingBean.subSetiaCodeMap = subSetiaCodeMap;
	}
	public void selectOneCheckboxSelect(AjaxBehaviorEvent e){
		
		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setDeleteButtonHide(false);
		}else{
			getManagedBean().setDeleteButtonHide(true);
		}
		
	}
	private void populateContractListTree() {
		Map<Long, TreeNode> contractTree = new HashMap<Long, TreeNode>();

		TreeNode root = getManagedBean().getContractListTree();
		root = new DefaultTreeNode("root", null);
		TreeNode parent = null;
		TreeNode child = null;
		
		for(VCtActiveContract vCtActiveContract : getManagedBean().getActiveContractList()){
			if(contractTree.containsKey(vCtActiveContract.getContract().getContractId())){
				parent = contractTree.get(vCtActiveContract.getContract().getContractId());
				child = new DefaultTreeNode(
						new CtActiveContractVO("",vCtActiveContract.getScheduleNo(),"",vCtActiveContract.getPeriodStart(),vCtActiveContract.getPeriodEnd(),null,null,"","","",vCtActiveContract.getContract().getContractId()),parent);
			}else{
				parent = new DefaultTreeNode(
						new CtActiveContractVO(vCtActiveContract.getContractNo(),"",vCtActiveContract.getCircularNo(),vCtActiveContract.getStartEffective(),vCtActiveContract.getEndEffective(),vCtActiveContract.getFulfilmentType(),vCtActiveContract.getContractType(),vCtActiveContract.getBusinessRegno(),vCtActiveContract.getMofNo(),vCtActiveContract.getSupplierName(),vCtActiveContract.getContract().getContractId()),root);
				if(vCtActiveContract.getFulfilmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)){
					child = new DefaultTreeNode(
							new CtActiveContractVO("",vCtActiveContract.getScheduleNo(),"",vCtActiveContract.getPeriodStart(),vCtActiveContract.getPeriodEnd(),null,null,"","","",vCtActiveContract.getContract().getContractId()),parent);
				}
				
				contractTree.put(vCtActiveContract.getContract().getContractId(),parent);
			}
		}
		getManagedBean().setContractListTree(root);
		
	}
	
	public void updateDeliveryDate() {
		Date startDate = getManagedBean().getFlRequest().getStartDate();
		int deliveryTerm = 0;
		String uomCode = "";
		deliveryTerm = getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getDeliveryTerm();
		
		uomCode = getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getDeliveryTermUom().getParameterCode();
		if(uomCode.equalsIgnoreCase(ParameterConst.DELIVERY_TERM_DAYS)){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.DATE, deliveryTerm);
			getManagedBean().getFlRequest().setEndDate(calendar.getTime());
		}else if(uomCode.equalsIgnoreCase(ParameterConst.DELIVERY_TERM_MONTHS)){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.MONTH, deliveryTerm);
			getManagedBean().getFlRequest().setEndDate(calendar.getTime());
		}
		
		
		//update delivery address
		for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()) {
			flDeliveryAddress.setStartDate(getManagedBean().getFlRequest().getStartDate());
			flDeliveryAddress.setEndDate(getManagedBean().getFlRequest().getEndDate());
		}
	}
	
	public void updateDeliveryAddrDate() {
		Date startDate = getManagedBean().getSelectedDelAddr().getStartDate();
		int deliveryTerm = 0;
		deliveryTerm = getManagedBean().getSelectedDelAddr().getDeliveryTerm();
		
		if(getManagedBean().getSelectedDelAddr().getPmUom().getParameterCode().equalsIgnoreCase(ParameterConst.DELIVERY_TERM_DAYS)){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.DATE, deliveryTerm);
			getManagedBean().getSelectedDelAddr().setEndDate(calendar.getTime());
		}else if(getManagedBean().getSelectedDelAddr().getPmUom().getParameterCode().equalsIgnoreCase(ParameterConst.DELIVERY_TERM_MONTHS)){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.MONTH, deliveryTerm);
			getManagedBean().getSelectedDelAddr().setEndDate(calendar.getTime());
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
	
	public AgOfficeResDTO getAgAccount(){
		getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId();
		IAgOfficeSO glAccountSO = (IAgOfficeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAgOfficeSO.class);
		AgOfficeReqDTO reqDTO = new AgOfficeReqDTO();
		reqDTO.setOrgProfileId(getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId());
		AgOfficeResDTO resDTO = glAccountSO.retrieveAgOfficeByOrgProfileId(reqDTO);
		if(getManagedBean().getFlRequest().getAgOffice()==null){
			getManagedBean().getFlRequest().setAgOffice(new PmAgOffice());
		}
		getManagedBean().getFlRequest().setAgOffice(resDTO.getAgOffice());

		return resDTO;
	}
	
	private void checkItemsFromSearchCatalogue(PmAddressType addrType,FlDeliveryAddress deliveryAddress){
		if(getManagedBean().getvCmSupplierItemList() != null){
			
			FlFulfilmentItem flItem = null;
			for(VCmSupplierItem vCmSupplierItem : getManagedBean().getvCmSupplierItemList()){
				IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractItemSO.class);
				ContractItemReqDTO ctItemReqDTO = new ContractItemReqDTO();
				ContractItemReqDTO itemPriceReqDTO = new ContractItemReqDTO();
				ctItemReqDTO.setContractItemId(vCmSupplierItem.getDocId());
				ContractItemResDTO ctItemResDTO = contractItemSO.getContractItem(ctItemReqDTO);
				itemPriceReqDTO.setItemId(ctItemResDTO.getCtContractItem().getScRequestItem().getRequestNoteItemId());
				if(addrType != null){
					itemPriceReqDTO.setPmAddress(addrType.getPmAddress());
				}else{
					itemPriceReqDTO.setPmAddress(deliveryAddress.getPmAddress());
				}
				
				itemPriceReqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
				itemPriceReqDTO.setOrderedQty(new BigDecimal(1));
				ContractItemResDTO resDTO = contractItemSO.getItemPrice(itemPriceReqDTO);
			
				FlFulfilmentItemAddr flItemAddr = new FlFulfilmentItemAddr();
				flItemAddr.setFlFulfilmentItem(flItem);
				flItemAddr.setFlDeliveryAddress(deliveryAddress);
				flItemAddr.setUnitPrice(resDTO.getItemPrice());
				flItemAddr.setCmItem(ctItemResDTO.getCtContractItem().getScRequestItem().getCmItem());
				flItemAddr.setScRequestItem(ctItemResDTO.getCtContractItem().getScRequestItem());
				flItemAddr.setItemType(ctItemResDTO.getCtContractItem().getItemType());
				//Code for check items already assigned to Selected Address
				boolean itemExisted=false;
				for (FlFulfilmentItemAddr selectedItem : deliveryAddress.getFlFulfilmentItemAddr()) {
					if (selectedItem.getScRequestItem().getRequestNoteItemId().equals(flItemAddr.getScRequestItem().getRequestNoteItemId())) {
						itemExisted=true;
						throw new EpException(MessageConst.ERROR_740022);
					}
				}
				if(!itemExisted){
					deliveryAddress.getFlFulfilmentItemAddr().add(flItemAddr);
				}
			}
		}
	}
	
	public boolean isFlFulfilmentRequisitioner() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.REQUISITIONER.value());
	}
	
}
