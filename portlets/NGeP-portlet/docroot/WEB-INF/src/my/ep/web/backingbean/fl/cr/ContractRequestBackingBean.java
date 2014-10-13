/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.cr;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
import my.ep.biz.service.fl.IFulfilmentSO;
import my.ep.biz.service.fl.itemsummary.IFlItemSummarySO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.agoffice.IAgOfficeSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.confgeneral.IConfigGeneralSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.projectsetia.IProjectSetiaSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
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
import my.ep.dto.fl.itemsummary.FlItemSummaryReqDTO;
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
import my.ep.dto.pm.common.confgeneral.ConfigGeneralResDTO;
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
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.codata.FLContractOrderDataType;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.VCtActiveContract;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtContractValue;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.fl.FlSupplierDtl;
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
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.type.pm.VotFundType;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.pr.FLCtSupplierItemVO;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.cr.ContractRequestManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.fl.helper.LocationSingleton;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.CtActiveContractVO;

import org.apache.commons.lang.builder.CompareToBuilder;
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
	private static Logger log =  Logger.getLogger(ContractRequestBackingBean.class);
	public static final String NAV_TO_CREATE_CONTRACT_REQUEST = "/xhtml/fl/contractrequest/crBasePage.xhtml";
	private boolean checkFlg = false;
	private String className="@(.myCrClassName)";
	
	private String queryItemCode;
	private String queryItemName;
	private String queryDesc;
	
	private static Map<Long, PmVotFund> votFundCodeMap = new HashMap<Long, PmVotFund>();
	
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
		if(getManagedBean().getActiveContractList()==null && isRequisitioner()) {
			IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();

			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			FulfilmentResDTO resDTO = requestSO.retrieveActiveContractList(reqDTO);
			getManagedBean().setActiveContractList(resDTO.getActiveContractList());
			getManagedBean().setPmOrgProfile(resDTO.getPmOrgProfile());
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
		
		if(getManagedBean().getFeeVoteFund()==null) {
			IConfigGeneralSO configGeneralSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IConfigGeneralSO.class);
			ConfigGeneralResDTO configGeneralResDTO = configGeneralSO.getConfigGeneral(null);
			getManagedBean().setFeeVoteFund(configGeneralResDTO.getPmConfigGeneral().getPmVotFund());
		}
	}

	public String submitAction(String action) throws Exception {
		IFulfilmentRequestSO iFulfilmentRequestSO = null;
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)){
				if(getManagedBean().getFlRequest().getFlDeliveryAddresses().size()==0) {
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
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
					messageId = MessageConst.INFO_700010;
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_CONTRACT_REQUEST), resDTO.getFlFulfilmentRequest().getDocNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_700002, 
									new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_CONTRACT_ORDER), resDTO.getFlFulfilmentRequest().getFlFulfilmentOrder().getDocNo()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				}	
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId() == null){
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}
				else {
					reqDTO.setFlRequestId(resDTO.getFlRequestId());
					resDTO = iFulfilmentRequestSO.retrieveFulfilmentRequest(reqDTO);
					setFulfilmentRequestField(resDTO);
				}
			} 

		}  finally {
			iFulfilmentRequestSO = null;
		}
		return null;
	}
	public void setFulfilmentRequestField(FulfilmentResDTO resDTO) {
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

		getSupplierBranchesList(resDTO.getFlFulfilmentRequest().getSmSupplier());
		
		for(FlFulfilmentItem flItem : getManagedBean().getFlRequest().getFlFulfilmentItems()) { 
			String key = flItem.getCmItem().getItemId().toString() + flItem.getUnitPrice().doubleValue() + "";
		    if(!getManagedBean().getFlItemMap().containsKey(key)) {
			    getManagedBean().getFlItemMap().put(key, flItem);
		    }
		}
		if(!getManagedBean().getFlRequest().getFlDeliveryAddresses().isEmpty()){
			getManagedBean().setSelectedDelAddr(getManagedBean().getFlRequest().getFlDeliveryAddresses().get(0));
		}
		
		//initialize all the COA map
		getVotFundList(getManagedBean().getFlRequest().getChargeOrgProfile().getOrgProfileId());
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
		getManagedBean().setEmailValidate(true);
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
				sortAddress(getManagedBean().getFlRequest().getFlDeliveryAddresses());
			}else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_745053, new String[]{getFullAddress(addrType.getPmAddress())}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}	
		}
		getManagedBean().setAddressTypesList(new ArrayList<PmAddressType>());
	}
	public void updateManualDeliveryAddrDt() {
		getManagedBean().setEmailValidate(true);
		IContractZoneSO contractZoneSO = (IContractZoneSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractZoneSO.class);
		if(null== getManagedBean().getFlManulaAddress().getPmAddress() ||  getManagedBean().getFlManulaAddress().isEditable()){
			// add to cart validation
			RequestContext.getCurrentInstance().execute("addManualDeliveryAddr.hide()");
			getManagedBean().setRequired(Boolean.FALSE);
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
			ContractRequestBackingBean crBB = new ContractRequestBackingBean();
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
				flReqDTO.setFlRequestId(coObj.getAssociatedCrId().longValue());

			} else {
				flReqDTO.setFlRequestId(coObj.getContractId().longValue());
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
				
			} 
			String[] outcome = new String[res.getAction().length + 1];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			int i = 1;
			for (String action : res.getAction()) {
				outcome[i++] = action;
			}
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(outcome);
			setFulfilmentRequestField(resDTO);
			if(isRequisitioner()){
				initializeApproverGroup();
			}
			
			if(!getManagedBean().getFlRequest().getFlDeliveryAddresses().isEmpty()){
				getManagedBean().setSelectedDelAddr(getManagedBean().getFlRequest().getFlDeliveryAddresses().get(0));
			}
			
			getManagedBean().setSelectedTabIndex(0);
			if(!getManagedBean().getFlRequest().getFlDeliveryAddresses().isEmpty()){
				getManagedBean().setSelectedDelAddr(getManagedBean().getFlRequest().getFlDeliveryAddresses().get(0));
			}			
		}else if(contractId != null){
			renewSesionObject();
			ContractRequestBackingBean crBB = new ContractRequestBackingBean();
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
			if(!getManagedBean().getFlRequest().getCtContract().isZonalPricing()){
				getManagedBean().getFlRequest().setStartDate(new Timestamp(System.currentTimeMillis()));
				updateDeliveryDate();
			}
			getManagedBean().setPmPriceType(contractResDTO.getContract().getLatestContractVer().getCtContractItems().get(0).getPriceType());
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
//			UserReqDTO userReqDTO = new UserReqDTO();
//			userReqDTO.setUserId(facesScopes.getUserSessionBean().getUserId());
//			UserResDTO userResDTO = profileSO.retrieveUserOrgListByUserId(userReqDTO);
			getManagedBean().getFlRequest().setPreparedOrgProfile(getManagedBean().getPmOrgProfile());
			getManagedBean().getFlRequest().setChargeOrgProfile(getManagedBean().getPmOrgProfile());
			getManagedBean().getFlRequest().setIssuedOrgProfile(getManagedBean().getPmOrgProfile());
			getAgAccount();
			getLoginUserOrg();
			String[] outcome = new String[2];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			outcome[1] = BpmOutcomeConst.BPM_SUBMIT;
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(outcome);
			FulfilmentBackingBean flBackingBean = new FulfilmentBackingBean();
			flBackingBean.initializeRemarkAndAttachement(flBB.getManagedBean().getRemarkList(), flBB.getManagedBean().getAttachmentList());
			getManagedBean().setSelectedTabIndex(0);
			if(!getManagedBean().getFlRequest().getFlDeliveryAddresses().isEmpty()){
				getManagedBean().setSelectedDelAddr(getManagedBean().getFlRequest().getFlDeliveryAddresses().get(0));
			}
		}
		
		if(getManagedBean().getFlRequest().getCtContract().isZonalPricing()){
			getManagedBean().getFlRequest().setStartDate(null);
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
		setFlSupplierDtl(getManagedBean().getFlRequest());
	}

	private void setFlSupplierDtl(FlFulfilmentRequest flRequest) {
		if(flRequest.getFlSupplierDtl() == null){
			FlSupplierDtl supplierDtl = new FlSupplierDtl();
			CtContractValue ctSupplierDtl = flRequest.getCtContract().getLatestContractVer().getCtContractValue();
			if(flRequest.getCtContract().getLatestContractVer().getCtContractValue().getSupplierBranch() != null){
				supplierDtl.setBranchCode(null);
				supplierDtl.setBranchName(ctSupplierDtl.getSupplierBranch());
			}else{
				supplierDtl.setBranchCode(null);
				supplierDtl.setBranchName(null);
			}
			supplierDtl.setFaxNo(ctSupplierDtl.getSupplierFaxNo());
			supplierDtl.setFlFulfilmentRequest(flRequest);
			supplierDtl.setPhoneNo(ctSupplierDtl.getSupplierPhoneNo());
			supplierDtl.setContactEmail(ctSupplierDtl.getSupplierEmail());
			supplierDtl.setContactMobileNo(ctSupplierDtl.getSupplierMobileNo());
			supplierDtl.setContactPerson(ctSupplierDtl.getSupplierContact());
			supplierDtl.setSmAddress(ctSupplierDtl.getSupplierAddress());
			supplierDtl.setSupplierName(ctSupplierDtl.getSupplierName());
//			for(SmPersonnel smPersonnel: flRequest.getCtContract().getSmSupplier().getSmAppl().getSmPersonnels()){
//				if(smPersonnel.isContactPerson()){
					supplierDtl.setContactFaxNo(null);
					supplierDtl.setContactPhoneNo(null);
//				}
//			}

			flRequest.setFlSupplierDtl(supplierDtl);
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
		IContractItemSO contractItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);
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
		IFlItemSummarySO itemSummarySO = (IFlItemSummarySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFlItemSummarySO.class);
		FlItemSummaryReqDTO reqDTO = new FlItemSummaryReqDTO();
		reqDTO.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
		reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
		BigDecimal prevSumAmt = itemSummarySO.retrieveContractAgencyAccumulatedAmount(reqDTO);
		checkCeilingAmt = prevSumAmt.add(sumOrderedAmt);
		
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
			IFlItemSummarySO itemSummarySO = (IFlItemSummarySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFlItemSummarySO.class);
			FlItemSummaryReqDTO reqDTO = new FlItemSummaryReqDTO();
			reqDTO.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
			reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
			BigDecimal prevSumQty = itemSummarySO.retrieveContractAgencyAccumulatedQty(reqDTO);
			checkCeilingQty = prevSumQty.add(sumOrderedQty);

			System.out.println("checkCeilingQty total: "+checkCeilingQty);
			System.out.println("getAgencyCeilingAmount: "+ctResDTO.getItemCeilingQuantity());
			if(checkCeilingQty.intValue() >  ctResDTO.getItemCeilingQuantity().intValue()){
				throw new EpException("Item "+ item.getCmItem().getItemName() +"  has been exceeded the quantity limit.");
//				throw new EpException("Item "+ item.getQtItem().getItemName() +"  has been exceeded the quantity limit.");
			}
		}
		
	}
	
	
	public void initializeApproverGroup() {
		IFulfilmentRequestSO requestSO = null;
		try {
			if(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId() != getManagedBean().getCurrentPreparedOrgId()){
				requestSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
				FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
				FulfilmentResDTO resDTO = new FulfilmentResDTO();
				reqDTO.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
				resDTO = requestSO.retrieveGroupListByOrgId(reqDTO);
				
				if(getManagedBean().getFlRequest().getPmUserGroup()==null) {
					PmUserGroup pmUserGroup = new PmUserGroup();
					getManagedBean().getFlRequest().setPmUserGroup(pmUserGroup);
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
					getManagedBean().getFlRequest().getPmUserGroup().setUserGroupId(getManagedBean().getUserGroupList().get(0).getUserGroupId());
				}
				
				//end
				getManagedBean().setCurrentPreparedOrgId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			requestSO = null;
		}
	}
	
	public void userGroupChangeListener() {
		IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		reqDTO.setGroupId(getManagedBean().getFlRequest().getPmUserGroup().getUserGroupId());
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
	
	public SelectItem[] getUserGroupDropdown() {
		SelectItem[] items = null;
		if(getManagedBean().getFlRequest().getPmUserGroup()==null) {
			getManagedBean().getFlRequest().setPmUserGroup(new PmUserGroup());
		}
		items = new SelectItem[getManagedBean().getUserGroupList().size()];
		int i = 0;
		for(PmUserGroup userGroup : getManagedBean().getUserGroupList()) {
			items[i++] = new SelectItem(userGroup.getUserGroupId(), userGroup.getGroupName());
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
			populateChargeLineData(false);	
			setClassName("@(.crChargeLinePnlClass)");
			onNodeSelect();
			getVotFundList(getManagedBean().getFlRequest().getChargeOrgProfile().getOrgProfileId());
			getManagedBean().setChargeLinePanel(false);
			getManagedBean().setProgramLov(false);
			getManagedBean().setProjectLov(false);
			getManagedBean().setVotFundDisplay(false);
		}
		if(selectedTabIndex==1){
			sortAddress(getManagedBean().getFlRequest().getFlDeliveryAddresses());
			/*if(getManagedBean().getSelectedDelAddr()==null) { 
				retrieveCountryList();
			}*/
			getManagedBean().setEmailValidate(true);
		}
		if(selectedTabIndex==2){
			if(getManagedBean().getFlRequest().getFlDeliveryAddresses()!=null) { 
				sortOrderSummaryList(getManagedBean().getFlRequest().getFlDeliveryAddresses());
			}
		}
		if(selectedTabIndex==4){
			initializeApproverGroup();
		}
	}
	private void populateChargeLineData(boolean retainPreviousSelection) {
		getManagedBean().setChargeLineItemRoot(new DefaultTreeNode("root", null));
		Map<Long, List<FlFulfilmentItemAddr>> chargeLineItemsMap = new HashMap<Long, List<FlFulfilmentItemAddr>>();
		
		for(FlDeliveryAddress  deliveryAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()){
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
				for (FlDeliveryAddress delAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()) {
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
		for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFlRequest().getFlDeliveryAddresses()){
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

	private void updateAllItemsWithChargeDrops() {
		updateSelectedTreeNodes();
		if(getManagedBean().getSelectedNodes().length==0) {
			throw new EpException(MessageConst.ERROR_100049);
		}
		for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFlRequest().getFlDeliveryAddresses()){
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

	// display itemCode,itemName,QueryDesc in popup
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
			getManagedBean().setMaxDate(calendar.getTime());
			getManagedBean().getFlRequest().setEndDate(calendar.getTime());
		}else if(uomCode.equalsIgnoreCase(ParameterConst.DELIVERY_TERM_MONTHS)){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.MONTH, deliveryTerm);
			getManagedBean().setMaxDate(calendar.getTime());
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
			getManagedBean().setMaxDate(calendar.getTime());
			getManagedBean().getSelectedDelAddr().setEndDate(calendar.getTime());
		}else if(getManagedBean().getSelectedDelAddr().getPmUom().getParameterCode().equalsIgnoreCase(ParameterConst.DELIVERY_TERM_MONTHS)){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.MONTH, deliveryTerm);
			getManagedBean().setMaxDate(calendar.getTime());
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
		checkPtjGroupAndInitialize();
		getManagedBean().getFlRequest().setIssuedOrgProfile(getManagedBean().getFlRequest().getPreparedOrgProfile());
		initializeApproverGroup();
		getManagedBean().getFlRequest().setIssuedOrgProfile(getManagedBean().getFlRequest().getPreparedOrgProfile());
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
	
	private void checkPtjGroupAndInitialize() {
		getManagedBean().setCurrentPtjGroupId(getManagedBean().getFlRequest().getPreparedOrgProfile().getPmPtjGroup().getPtjGroupId());
		if(getManagedBean().getFlRequest().getIssuedOrgProfile().getPmPtjGroup().getPtjGroupId() != getManagedBean().getCurrentPtjGroupId()){
			if(getManagedBean().getFlRequest().getFlFulfilmentItems() != null && !getManagedBean().getFlRequest().getFlFulfilmentItems().isEmpty()){
				for(FlFulfilmentItem fulfilmentItem : getManagedBean().getFlRequest().getFlFulfilmentItems()){
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
	
	//update delivery address based on endDate change
	public void updateDeliveryEndDate() {
		for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()) {
			flDeliveryAddress.setEndDate(getManagedBean().getFlRequest().getEndDate());
		}
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
			FlSupplierDtl supplierDtl = getManagedBean().getFlRequest().getFlSupplierDtl();
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
			reqDTO.setSupplierId(getManagedBean().getFlRequest().getSmSupplier().getSupplierId());
			MofRegistrationResDTO resDTO = mofRegistrationSO.retrieveSupplierProfile(reqDTO);
			SmSupplier supplier = resDTO.getSmSupplier();
			
			FlSupplierDtl supplierDtl = getManagedBean().getFlRequest().getFlSupplierDtl();
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

			if(pmVotFund!=null && !"".equalsIgnoreCase(pmVotFund.getVotFundType())){
				if(VotFundType.BEKALAN.value().equalsIgnoreCase(pmVotFund.getVotFundType()) || VotFundType.TANGGUNGAN.value().equalsIgnoreCase(pmVotFund.getVotFundType())){
					getManagedBean().setProgramLov(true);
				} else if(VotFundType.PEMBANGUNAN.value().equalsIgnoreCase(pmVotFund.getVotFundType()) || VotFundType.SETIA.value().equalsIgnoreCase(pmVotFund.getVotFundType())){
					getManagedBean().setProjectLov(true);
				}
			}
		}
	
	/**
	 * Page navigation to FLDoc search page in CR
	 */
		public String backToPervious() {
			return "flDocSearch";
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
		// get the Request Item Information based on ReqItemId and SuppId
		
		public void reqItemInfoList(FlFulfilmentItemAddr flItemAddr){
			getManagedBean().setItemName(flItemAddr.getScRequestItem().getItemName());
			flItemAddr.setPriceType(getManagedBean().getPmPriceType());
			getManagedBean().setPriceType(flItemAddr.getPriceType());
			getManagedBean().setStandardUnitPrice(flItemAddr.getUnitPrice().doubleValue());
			getManagedBean().setStandardPrice(false);
			getManagedBean().setZonalPrice(false);
			getManagedBean().setZonalTierPrice(false);
			getManagedBean().setTariffPrice(false);
			if(ParameterConst.PRICE_TYPE_STANDARD.equals(flItemAddr.getPriceType().getParameterCode())){
				getManagedBean().setStandardPrice(true);
			}else if(ParameterConst.PRICE_TYPE_ZONAL_STANDARD.equals(flItemAddr.getPriceType().getParameterCode())){
				getManagedBean().setZonalPrice(true);
			}else if(ParameterConst.PRICE_TYPE_ZONAL_TIER.equals(flItemAddr.getPriceType().getParameterCode()) || ParameterConst.PRICE_TYPE_ZONAL_TARIFF.equals(flItemAddr.getPriceType().getParameterCode())){
				getManagedBean().setZonalTierPrice(true);
			}
			if(ParameterConst.PRICE_TYPE_TARIFF.equals(flItemAddr.getPriceType().getParameterCode()) || ParameterConst.PRICE_TYPE_ZONAL_TARIFF.equals(flItemAddr.getPriceType().getParameterCode())) {
				getManagedBean().setTariffPrice(true);
			}
			IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,	IContractItemSO.class);
			ContractItemReqDTO reqDTO = new ContractItemReqDTO();
			reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
			reqDTO.setItemId(flItemAddr.getScRequestItem().getRequestNoteItemId());
			ContractItemResDTO ciItemResDTO = contractItemSO.retrieveItemPriceAndZone(reqDTO);
			getManagedBean().setReqItemInfoList(ciItemResDTO.getCtItemPriceList());
			List<FLCtSupplierItemVO> supplierItems=new ArrayList<FLCtSupplierItemVO>();
			if(ciItemResDTO.getCtItemPriceList() != null && ciItemResDTO.getCtItemPriceList().size() >0){
				for (CtItemPrice contractItemPrice : ciItemResDTO.getCtItemPriceList() ) {
					if(null != contractItemPrice.getCtContractZone()){
						CtContractZone ctContractZone = contractItemPrice.getCtContractZone();
						if(ctContractZone.getCtContractZoneDtls() != null && ctContractZone.getCtContractZoneDtls().size()>0){
							FLCtSupplierItemVO itemPriceVO=new FLCtSupplierItemVO();
							itemPriceVO.setSupplierItemPrice(contractItemPrice);
							itemPriceVO.setItemZoneTree(LocationSingleton.getInstance().constuctContractTreeNode(ctContractZone));
							supplierItems.add(itemPriceVO);
						}
					} else {
						FLCtSupplierItemVO itemPriceVO=new FLCtSupplierItemVO();
						itemPriceVO.setSupplierItemPrice(contractItemPrice);
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
}
