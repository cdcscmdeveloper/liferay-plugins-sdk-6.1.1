/**

\q * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.cr;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
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
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractIntegrationSO;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.circular.ICircularDetailSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.biz.service.ct.dtl.IContractZoneSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.ct.supplier.IContractSupplierSO;
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
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.ct.ContractIntegrationReqDTO;
import my.ep.dto.ct.ContractIntegrationResDTO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.circular.CircularDetailReqDTO;
import my.ep.dto.ct.circular.CircularDetailResDTO;
import my.ep.dto.ct.dtl.ContractItemReqDTO;
import my.ep.dto.ct.dtl.ContractItemResDTO;
import my.ep.dto.ct.dtl.ContractZoneReqDTO;
import my.ep.dto.ct.dtl.ContractZoneResDTO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.dto.ct.supplier.ContractSupplierReqDTO;
import my.ep.dto.ct.supplier.ContractSupplierResDTO;
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
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.uom.UomReqDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.codata.FLContractOrderDataType;
import my.ep.jaxb.fl.phis.codata.FLPHISContractOrderDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtAgencyAddress;
import my.ep.persistence.entity.ct.dtl.CtContractAmount;
import my.ep.persistence.entity.ct.dtl.CtContractValue;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.fl.FlSupplierDtl;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.fl.request.FlTempItem;
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
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.pm.VotFundType;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.ct.ContractDetailsBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBaseBackingBean;
import my.ep.web.backingbean.fl.pr.FLCtSupplierItemVO;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.cr.ContractRequestManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.fl.helper.LocationSingleton;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
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
@ViewScoped
public class ContractRequestBackingBean extends FulfilmentBaseBackingBean<ContractRequestManagedBean> {
	private static Logger log =  Logger.getLogger(ContractRequestBackingBean.class);
	private final static long MILLIS_PER_DAY = 24 * 3600 * 1000;
	public static final String NAV_TO_CREATE_CONTRACT_REQUEST = "/xhtml/fl/contractrequest/crBasePage.xhtml";
	private static final BaseEntity entity = null;
	private IPagingDataModel<PmUom> unitOfMeasurementDataModel = new UnitOfMeasurementPagingDataModel();
	
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
	private boolean selectAllCheckbox;
	
	/**
	 * Instantiates a new fulfilment backing bean.
	 */
	public ContractRequestBackingBean() {
		if (getManagedBean().getFlRequest() == null) {
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
		
		if(null == getManagedBean().getFlManulaAddress()){
			getManagedBean().setFlManulaAddress(new FlDeliveryAddress());
			getManagedBean().getFlManulaAddress().setDeliveryAddressId(1L);
			if(null == getManagedBean().getFlManulaAddress().getAddressType()){
				 getManagedBean().getFlManulaAddress().setAddressType(new PmAddressType());
			}
			getManagedBean().getFlManulaAddress().getAddressType().setPmAddress(new PmAddress());
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
			getManagedBean().getFlManulaAddress().setReceivingGrp(new PmAddressType());
		}
		if(null == getManagedBean().getFlManulaAddress().getPmAddress()){
			 getManagedBean().getFlManulaAddress().setPmAddress(new PmAddress());
		}
		if(null == getManagedBean().getFlManulaAddress().getAddressType().getPmContacts() || getManagedBean().getFlManulaAddress().getAddressType().getPmContacts().size()==0){
			getManagedBean().getFlManulaAddress().getAddressType().setPmContacts(new ArrayList<PmContact>());
			getManagedBean().getFlManulaAddress().getAddressType().getPmContacts().add(new PmContact());
		}
		if(null == getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmCountry()){
			getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmCountry(new PmCountry());
			getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmCountry().setCountryId(1L);
		}
		if (isRequisitioner()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setRequisitioner(facesScopes.getUserSessionBean().getLoginId());
		}
		if (isApprover()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setApprover(facesScopes.getUserSessionBean().getLoginId());
		}
		if(getManagedBean().getFlRequest().getPreparedOrgProfile()!=null && getManagedBean().getFlRequest().getPreparedOrgProfile().getPmOrgValidity() == null){
			getManagedBean().getFlRequest().getPreparedOrgProfile().setPmOrgValidity(new PmOrgValidity());
			
		}
		if(getManagedBean().getFlRequest().getIssuedOrgProfile()!=null && getManagedBean().getFlRequest().getIssuedOrgProfile().getPmOrgValidity() == null){
			getManagedBean().getFlRequest().getIssuedOrgProfile().setPmOrgValidity(new PmOrgValidity());
			
		}
		if(getManagedBean().getFlRequest().getChargeOrgProfile()!=null && getManagedBean().getFlRequest().getChargeOrgProfile().getPmOrgValidity() == null){
			getManagedBean().getFlRequest().getChargeOrgProfile().setPmOrgValidity(new PmOrgValidity());
			
		}
		getManagedBean().setDeleteButtonHide(true);
		getManagedBean().setItemDeleteButtonShow(true);
		
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
	/**
	 * save,submit the contract request
	 * 
	 * @param String action 
	 *  <p>submit - save data into database and BPM process will route to the next respective user.
	 * 	<p>save - save data into database.
	 * 
	 * @return navigate to acknowledge page.
	 */
	public String submitAction(String action) throws Exception {
		IFulfilmentRequestSO iFulfilmentRequestSO = null;
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flRequest = getManagedBean().getFlRequest();
			if(isRequisitioner()) {
				reqDTO.setRequisitioner(Boolean.TRUE);
			}
			
			flRequest.setFinancialYear(Calendar.getInstance().get(Calendar.YEAR));
			
			// reset flTempItem
			flRequest.setFlTempItems(new ArrayList<FlTempItem>());
			if(getManagedBean().getItemAddrList()!=null && getManagedBean().getItemAddrList().size()>0) {
				FlTempItem flTempItem = null;
				for(FlFulfilmentItemAddr itemAddr : getManagedBean().getItemAddrList()) {
					flTempItem = new FlTempItem();
					flTempItem.setFlRequest(flRequest);
					flTempItem.setScRequestItem(itemAddr.getScRequestItem());
					flRequest.getFlTempItems().add(flTempItem);
				}
			}
			
			if(isRequisitioner()){
				if(getManagedBean().getSelectedApprover() != null){
					PmUserGroupUser groupUser = getManagedBean().getApproverMap().get(getManagedBean().getSelectedApprover());
					List<PmUser> approverUserList = sortApproverUserList(getManagedBean().getApproverGroupUserList(), groupUser.getPmUser());
					
					reqDTO.setRequisitioner(Boolean.TRUE);
					reqDTO.setApproverUserList(approverUserList);
					reqDTO.setSelectedApprover(getManagedBean().getSelectedApprover());
				}
				
				UserReqDTO userReqDTO = new UserReqDTO();
				userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
				UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
				flRequest.setCreatedOrgProfile(userResDTO.getUserOrgList().get(0).getPmOrgProfile());
			}
			
			if(!getManagedBean().isValidated()){
				validateContractRequest(action);
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
			reqDTO.setUserLocale(getFacesScopes().getUserLocale());
			reqDTO.setPidValidityExpired(getManagedBean().isPidValidityExpired());
			getSumOrderedQty(flRequest);
			
			if(getManagedBean().getPmDigiSign() != null){
				reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			}
			
			iFulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
			FulfilmentResDTO resDTO = iFulfilmentRequestSO.saveFulfillmentRequest(reqDTO);
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String messageId = MessageConst.INFO_700005;
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700004;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					messageId = MessageConst.INFO_745004;
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
//					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
//							MessageUtils.getMessage(MessageConst.INFO_700002, 
//									new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_CONTRACT_ORDER), resDTO.getFlFulfilmentRequest().getFlFulfilmentOrder().getDocNo()}, 
//									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				}	
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
//					if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
//						Map<String, String> paramMap = new HashMap<String, String>();
//						paramMap.put(ReportConst.PARAM_DOC_NO, resDTO.getFlFulfilmentRequest().getFlFulfilmentOrder().getDocNo());
//						paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
//						CommonBackingBean commonBackingBean = new CommonBackingBean();
//						commonBackingBean.getManagedBean().setReportName(ReportConst.FL_NGEP_PURCHASE_ORDER);
//						commonBackingBean.getManagedBean().setReportDisplayName(ResourceBundleUtils.getMessage("lbl.fl.do.poco",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
//						commonBackingBean.getManagedBean().setParamMap(paramMap);
//						return CommonUIConst.ACKNOWLEDGE_REPORT_PAGE;
//					}						
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
	
	/**
	 * set fulfilment request attribute into managedbean
	 * 
	 * @param FulfilmentResDTO resDTO
	 * 
	 */
	public void setFulfilmentRequestField(FulfilmentResDTO resDTO) {
		getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
		getManagedBean().getFlRequest().setTitle(getManagedBean().getFlRequest().getTitle().trim());
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		flBB.initializeRemarkAndAttachement(getManagedBean().getFlRequest().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
		getManagedBean().setSelectedTabIndex(0);
		getSumOrderedQty(getManagedBean().getFlRequest());

		getSupplierBranchesList(resDTO.getFlFulfilmentRequest().getSmSupplier());
		
		if(!getManagedBean().getFlRequest().getFlDeliveryAddresses().isEmpty()){
			getManagedBean().setSelectedDelAddr(getManagedBean().getFlRequest().getFlDeliveryAddresses().get(0));
		}
		if(getManagedBean().getFlRequest().getApprover() != null){
			getManagedBean().setSelectedApprover(getManagedBean().getFlRequest().getApprover().getUserId());
		}
		
		if(isRequisitioner()){
			initializeApproverGroup();
		}
		getManagedBean().setCurrentPreparedOrgId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
		IOrgProfileSO profileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		OrgProfileReqDTO orgReqDTO = new OrgProfileReqDTO();
		orgReqDTO.setOrgProfileId(getManagedBean().getFlRequest().getChargeOrgProfile().getOrgProfileId());
		getManagedBean().setCurrentChargePtjGroup(profileSO.retrieveJabatanByOrgProfileId(orgReqDTO).getPmOrgProfile());
		
		//initialize all the COA map
		Long jabatanOrgProfileId = -1l;
		PmOrgProfile jabatan = retrieveJabatanOrgProfile(getManagedBean().getFlRequest().getChargeOrgProfile().getOrgProfileId());
		if(jabatan!=null) {
			jabatanOrgProfileId = jabatan.getOrgProfileId();
		}		
		getVotFundList(jabatanOrgProfileId);
		for(FlFulfilmentItem item : getManagedBean().getFlRequest().getFlFulfilmentItems()){
			if(item.getFlQuery() != null){
				getManagedBean().setQueryDisplay(true);
				break;
			}
		}
	}
	
	private void getSumOrderedQty(FlFulfilmentRequest flRequest) {
		if(flRequest.getFlFulfilmentItems() != null && !flRequest.getFlFulfilmentItems().isEmpty()){
			BigDecimal sumOrderedQty = BigDecimal.ZERO;
			for(FlFulfilmentItem fulfilmentItem : flRequest.getFlFulfilmentItems()){
				for(FlFulfilmentItemAddr itemAddress : fulfilmentItem.getFlFulfilmentItemAddr()){
					sumOrderedQty = sumOrderedQty.add(itemAddress.getOrderedQty());
				}
				fulfilmentItem.setSumOrderedQty(sumOrderedQty);
			}
		}
	}
	
	/**
	 * Gets the supplier branches list based on SupplierId.
	 *
	 * @param smSupplier the sm supplier
	 * @return the supplier branches list
	 *//*
	public List<SelectItem> getSupplierBranchesList(List<SmSupplierBranch> branches){
		
		List<SelectItem> supplierBranches = new ArrayList<SelectItem>();
		for (SmSupplierBranch supplierBranch : branches) {
			supplierBranches.add(new SelectItem(supplierBranch.getSupplierBranchId(),supplierBranch.getBranchName()));
		}
		getManagedBean().setSupplierBranchesList(supplierBranches);
		return supplierBranches;
	}*/
	
	/**
	 * Removes the selected delivery addresses from delivery Address List.
	 * <p>
	 * clear the order quantity from item list which is assigned to selected delivery addresses
	 */
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
				for(FlFulfilmentItemAddr flItemAddr : getManagedBean().getShowItemAddrList()) {
					if(getManagedBean().getFlRequest().getProcurementTypeCategory().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_TYPE_CODE_SERVICE)){
						flItemAddr.setGst(true);
					} else {
						flItemAddr.setGst(false);
					}
					flItemAddr.setOrderedQty(BigDecimal.ZERO);
					flItemAddr.setOrderedAmt(BigDecimal.ZERO);
					if(getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_STANDARD)) {
						setGstPrice(flItemAddr, flItemAddr.getUnitPrice().doubleValue());
					} else {
						flItemAddr.setUnitPrice(BigDecimal.ZERO);
						flItemAddr.setGstAmount(BigDecimal.ZERO);
						flItemAddr.setGstUnitPrice(BigDecimal.ZERO);
					}
				}
			}
			if(getManagedBean().getSelectedDelAddr()!=null) {
				populateItemsForSelectedAddress();
			}
		}
	}
	
	/**
	 * To remove a contract item from a delivery address.
	 */
	public void removeItemRow() {
		List<Long> deletedItemId = new ArrayList<Long>();
		for(FlFulfilmentItemAddr flItemAddr : getManagedBean().getSelectedItemsRows()) {
			deletedItemId.add(flItemAddr.getScRequestItem().getRequestNoteItemId());
		}
		if(deletedItemId.size()>0) {
			getManagedBean().setItemDeleteButtonShow(true);
			List<FlFulfilmentItemAddr> deleteFlItemAddrs = new ArrayList<FlFulfilmentItemAddr>();
			for (FlFulfilmentItemAddr flItemAddr : getManagedBean().getItemAddrList()) {
				if(deletedItemId.contains(flItemAddr.getScRequestItem().getRequestNoteItemId())) {
					deleteFlItemAddrs.add(flItemAddr);
				}
			}
			getManagedBean().getItemAddrList().removeAll(deleteFlItemAddrs);
			
			deleteFlItemAddrs = new ArrayList<FlFulfilmentItemAddr>();
			for (FlFulfilmentItemAddr flItemAddr : getManagedBean().getShowItemAddrList()) {
				if(deletedItemId.contains(flItemAddr.getScRequestItem().getRequestNoteItemId())) {
					deleteFlItemAddrs.add(flItemAddr);
				}
			}			
			getManagedBean().getShowItemAddrList().removeAll(deleteFlItemAddrs);
			
			for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()) {
				deleteFlItemAddrs = new ArrayList<FlFulfilmentItemAddr>();
				for (FlFulfilmentItemAddr flItemAddr : flDeliveryAddress.getFlFulfilmentItemAddr()) {
					if(deletedItemId.contains(flItemAddr.getScRequestItem().getRequestNoteItemId())) {
						deleteFlItemAddrs.add(flItemAddr);
					}
				}
				flDeliveryAddress.getFlFulfilmentItemAddr().removeAll(deleteFlItemAddrs);
			}
			getManagedBean().setItemDeleteButtonShow(true);
		}
		if(getManagedBean().getSelectedDelAddr()!=null) {
			populateItemsForSelectedAddress();
		}
	}
	
	/**
	 * Close listener.
	 */
	public void closeListener() {
		log.info("closeListener");
		getManagedBean().setEmailValidate(true);
		RequestContext.getCurrentInstance().execute("addManualDeliveryAddr.hide()");
		getManagedBean().setRequired(Boolean.FALSE);
	}
	
	/**
	 * Close listener for item dialog.
	 */
	public void itemCloseListener() {
		RequestContext.getCurrentInstance().execute("addItems.hide()");
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
	
	/**
	 * sort address
	 * 
	 * @param ContractAmendmentReqDTO ctReqDTO
	 * 
	 */
	public void flDeliveryAddrList() {
		ContractAmendmentReqDTO ctReqDTO = new ContractAmendmentReqDTO();
		ctReqDTO.setCtContract(getManagedBean().getFlRequest().getCtContract());
		ctReqDTO.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
		IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractAmendmentSO.class);
		ContractAmendmentResDTO resDTO = ctAmendmentSO.getActiveLocation(ctReqDTO);
		getManagedBean().setAddressTypesList(resDTO.getAddressTypeList());
		sortAddressType(getManagedBean().getAddressTypesList());
	}
	
	/**
	 * flManualDeliveryAddr Method loads country List.
	 * default selected one is Malaysia.
	 * loads states list based malaysia country.
	 */
	public void flManualDeliveryAddr() {
		getManagedBean().setEmailValidate(false);
		retrieveCountryList();
		getManagedBean().setFlManulaAddress(new FlDeliveryAddress());
		getManagedBean().getFlManulaAddress().setDeliveryAddressId(1L);
		getManagedBean().getFlManulaAddress().setPmAddress(new PmAddress());
		getManagedBean().getFlManulaAddress().setAddressType(new PmAddressType());
		getManagedBean().getFlManulaAddress().getAddressType().setPmAddress(new PmAddress());
		// default to Malaysia
		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICountrySO.class);
		CountryReqDTO countryReqDTO = new CountryReqDTO();
		countryReqDTO.setCountryCode("MY");
		getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmCountry(countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());
		getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmState(new PmState());
		getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmCity(new PmCity());
		getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmDivision(new PmDivision());
		getManagedBean().getFlManulaAddress().setReceivingGrp(new PmAddressType());
		
		if(null == getManagedBean().getFlManulaAddress().getAddressType().getPmContacts() || getManagedBean().getFlManulaAddress().getAddressType().getPmContacts().size()==0){
			getManagedBean().getFlManulaAddress().getAddressType().setPmContacts(new ArrayList<PmContact>());
			getManagedBean().getFlManulaAddress().getAddressType().getPmContacts().add(new PmContact());
		}
		
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmCountry().getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStateList(resDTO.getStateList());
		
		if(getManagedBean().getAddressTypesList()==null) {
			ContractAmendmentReqDTO ctReqDTO = new ContractAmendmentReqDTO();
			ctReqDTO.setCtContract(getManagedBean().getFlRequest().getCtContract());
			ctReqDTO.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
			IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractAmendmentSO.class);
			ContractAmendmentResDTO ctAmendmentResDTO = ctAmendmentSO.getActiveLocation(ctReqDTO);
			getManagedBean().setAddressTypesList(ctAmendmentResDTO.getAddressTypeList());
		}
		if(getManagedBean().getManualAddressMap() == null || getManagedBean().getManualAddressMap().size() == 0){
			setManualAddressList();
		}
		getManagedBean().setAutoUpdate(Boolean.FALSE);
	}
	
	/**
	 * update UI delivery address datatable
	 * 
	 */
	public void updateDeliveryAddrDt(List<BaseEntity> entity) {
		if(getManagedBean().isAgencyAddress()) {
			getManagedBean().setSelectedAddrTypeList(new ArrayList<PmAddressType>());
			if(getManagedBean().getSelectedAgencyAddressList()!=null && getManagedBean().getSelectedAgencyAddressList().size()>0) {
				for(CtAgencyAddress ctAgencyAddress : getManagedBean().getSelectedAgencyAddressList()) {
					getManagedBean().getSelectedAddrTypeList().add(ctAgencyAddress.getAddressType());
				}
			}
		}
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
					deliveryAddr.setPmUom(zoneResDTO.getDeliveryTermUom());
					deliveryAddr.setDeliveryTerm(zoneResDTO.getDeliveryTerm());
					deliveryAddr.setStartDate(new Timestamp(System.currentTimeMillis()));
					
					Date startDate = deliveryAddr.getStartDate();
					int deliveryTerm = deliveryAddr.getDeliveryTerm();
					
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(startDate);
					calendar.add(Calendar.DATE, deliveryTerm);
					deliveryAddr.setEndDate(calendar.getTime());
					
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
		selectAddrEvent();
	}
	
	/**
	 * update UI manual delivery address datatable.
	 * 
	 */
	public void updateManualDeliveryAddrDt() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		getManagedBean().setEmailValidate(true);
		IContractZoneSO contractZoneSO = (IContractZoneSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractZoneSO.class);
		if(null== getManagedBean().getFlManulaAddress().getAddressType().getPmAddress() ||  getManagedBean().getFlManulaAddress().isEditable()){
			// add to cart validation
			RequestContext.getCurrentInstance().execute("addManualDeliveryAddr.hide()");
			getManagedBean().setRequired(Boolean.FALSE);
			return;
		}
		FlDeliveryAddress deliveryAddr = null;
		PmAddress manualAddr=new PmAddress();
		manualAddr=getManagedBean().getFlManulaAddress().getAddressType().getPmAddress();
		PmAddressType pmAddressType = new PmAddressType();
		pmAddressType.setAddressType("M");
		getManagedBean().getFlManulaAddress().getAddressType().getPmContacts().get(0).setPmAddressType(pmAddressType);
		pmAddressType.setPmContacts(getManagedBean().getFlManulaAddress().getAddressType().getPmContacts());
		pmAddressType.setPmAddress(manualAddr);
		getManagedBean().getFlManulaAddress().setAddressType(pmAddressType);		
		//manualAddr.setPmAddressTypes(pmAddressType);	
		deliveryAddr = new FlDeliveryAddress();
		deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFlRequest());
		deliveryAddr.setPmAddress(manualAddr);
		deliveryAddr.setAddressType(pmAddressType);
		
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
			if(deliveryAddr.getPmUom()!=null) {
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
			} else {
				deliveryAddr.setEndDate(startDate);
			}
		}else{
			deliveryAddr.setStartDate(getManagedBean().getFlRequest().getStartDate());
			deliveryAddr.setEndDate(getManagedBean().getFlRequest().getEndDate());
			deliveryAddr.setPmUom(getManagedBean().getFlRequest().getPmUom());
			deliveryAddr.setDeliveryTerm(getManagedBean().getFlRequest().getDeliveryTerm());
		}
		deliveryAddr.setReceivingGrp(getManagedBean().getFlManulaAddress().getReceivingGrp());
		getManagedBean().setSelectedDelAddr(deliveryAddr);
		getManagedBean().getFlRequest().getFlDeliveryAddresses().add(deliveryAddr);
		selectAddrEvent();
		
		// add to cart validation
		RequestContext.getCurrentInstance().execute("addManualDeliveryAddr.hide()");
		getManagedBean().setRequired(Boolean.FALSE);
	}

	/**
	 * Navigate to CR UI
	 * 
	 */
	public void procureContract() throws PortalException, SystemException {
		Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("contractId"));
		String scheduleNo = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("scheduleNo");
		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay)facesScope.getRequestScope(WebKeys.THEME_DISPLAY);
		
		if(themeDisplay!=null) {
			if(themeDisplay.getLayout()!=null) {
				if(themeDisplay.getLayout().getGroup().getFriendlyURL()!=null) {
				}
			}
		}
		
		String commonUrl = themeDisplay.getURLCurrent().substring(0, themeDisplay.getURLCurrent().indexOf(themeDisplay.getLayout().getGroup().getFriendlyURL())+themeDisplay.getLayout().getGroup().getFriendlyURL().length());
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String url = "contract-request-from-crlist";

		try {
			externalContext.redirect(commonUrl+"/" + url+"?contractId="+contractId+"&scheduleNo="+scheduleNo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * retrieve Contract items pagination.
	 * 
	 */
	public void addContractedItems() {
		getManagedBean().setAutoUpdate(false);
		getManagedBean().setItemAutoUpdate(false);
		getManagedBean().setContractedItemList(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractItems());
		getManagedBean().setSelectedContractedItemList(new ArrayList<CtContractItem>());
		this.getManagedBean().setSearchItemType(null);
		this.getManagedBean().setSearchItemCode(null);
		this.getManagedBean().setSearchItemName(null);
		getContractItemPagingDataModel().resetData();
	}
	
	/**
	 * update UI contract item datatable. 
	 * <p>Auto populate items on the selected address.
	 * 
	 */
	public void updateContractedItemDt() {
		IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,	IContractItemSO.class);
		FlFulfilmentItemAddr flItemAddr = null;
		List<FlFulfilmentItemAddr> itemAddrList = new ArrayList<FlFulfilmentItemAddr>();
		boolean itemExisted=false;
		String messageId = null;
		
		ContractItemReqDTO reqDTO = new ContractItemReqDTO();
		ContractItemResDTO resDTO = new ContractItemResDTO();
		reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
		
		if(getManagedBean().getItemAddrList()==null) {
			getManagedBean().setItemAddrList(new ArrayList<FlFulfilmentItemAddr>());
			getManagedBean().setShowItemAddrList(new ArrayList<FlFulfilmentItemAddr>());
		}
		for (CtContractItem contractedItem : getManagedBean().getSelectedContractedItemList()) {
			itemExisted=false;
			for(FlFulfilmentItemAddr itemAddr : getManagedBean().getItemAddrList()) {
				if(itemAddr.getScRequestItem().getRequestNoteItemId().equals(contractedItem.getScRequestItem().getRequestNoteItemId())) {
					itemExisted = true;
					if(getManagedBean().getSelectedDelAddr()!=null) {
						messageId = MessageConst.INFO_745005;
					} else {
						messageId = MessageConst.INFO_745006;
					}
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(messageId, new String[]{itemAddr.getScRequestItem().getItemName()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));					
				}
			}
			if(!itemExisted){
				flItemAddr = new FlFulfilmentItemAddr();
				flItemAddr.setPriceType(getManagedBean().getPmPriceType());
				flItemAddr.setItemType(contractedItem.getItemType());
				flItemAddr.setScRequestItem(contractedItem.getScRequestItem());
				flItemAddr.setCmItem(contractedItem.getScRequestItem().getCmItem());
				if(contractedItem.getScRequestItem().getCmItem()!=null) {
					flItemAddr.setItemCode(contractedItem.getScRequestItem().getCmItem().getExtensionCode());
				} else {
					flItemAddr.setItemCode(contractedItem.getScRequestItem().getTempUnspscCode());
				}					
				if(getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_STANDARD)) {
					if(getManagedBean().getFlRequest().getStartDate()!=null && getManagedBean().getFlRequest().getEndDate()!=null) {
						long day = (getManagedBean().getFlRequest().getEndDate().getTime() - getManagedBean().getFlRequest().getStartDate().getTime()) / MILLIS_PER_DAY;
						reqDTO.setDeliveryTerm(Long.valueOf(day).intValue());
					}
					reqDTO.setItemId(contractedItem.getScRequestItem().getRequestNoteItemId());
					reqDTO.setOrderedQty(BigDecimal.ZERO);
					resDTO = contractItemSO.getItemPrice(reqDTO);
					if(resDTO.getItemPrice().doubleValue() == -1){
						flItemAddr.setUnitPrice(BigDecimal.ZERO);
						flItemAddr.setGstAmount(BigDecimal.ZERO);
						flItemAddr.setGstUnitPrice(BigDecimal.ZERO);
					}else{
						
						flItemAddr.setUnitPrice(resDTO.getItemPrice());
						setGstPrice(flItemAddr,resDTO.getItemPrice().doubleValue());
					}
					
				} else {
					flItemAddr.setUnitPrice(BigDecimal.ZERO);
					flItemAddr.setGstAmount(BigDecimal.ZERO);
					flItemAddr.setGstUnitPrice(BigDecimal.ZERO);
				}
				flItemAddr.setOrderedQty(BigDecimal.ZERO);
				flItemAddr.setOrderedAmt(BigDecimal.ZERO);
				itemAddrList.add(flItemAddr);
			}
		}
		getManagedBean().getItemAddrList().addAll(itemAddrList);
		getManagedBean().getShowItemAddrList().addAll(itemAddrList);
		if(getManagedBean().getShowItemAddrList().size()>0){
			getManagedBean().setItemDeleteButtonShow(true);
		}
		sortItems(getManagedBean().getShowItemAddrList());
		if(getManagedBean().getSelectedDelAddr()!=null) {
			populateItemsForSelectedAddress();
		}
		getManagedBean().setSelectedContractedItemList(new ArrayList<CtContractItem>());
		getManagedBean().setItemAutoUpdate(false);
		getManagedBean().setAutoUpdate(true);
	}

	/**
	 * renew session. Retrieve fulfilment request object and set it into managedBean.
	 * 
	 */
	public void preRenderView() {
		IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractItemSO.class);
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		String contractId = originalServletRequest.getParameter("contractId");
		String scheduleNo = originalServletRequest.getParameter("scheduleNo");
		
		if (taskId != null) {
			renewSesionObject();
			ContractRequestBackingBean crBB = new ContractRequestBackingBean();
			IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,
					IFulfilmentRequestSO.class);
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			
			Object obj = super.getBpmTaskDetails(Object.class, new String[]{BpmOutcomeConst.BPM_SAVE});
			
			FLContractOrderDataType coObj = null;
			if(this.getManagedBean().getProcessName().equalsIgnoreCase(FLProcessNameConst.PHIS_CONTRACT_REQ_INITIATION_PROCESS_NAME) && 
					this.getManagedBean().getTaskName().equalsIgnoreCase(FLProcessNameConst.SUBMIT_PHIS_CR)) {
				FLPHISContractOrderDataType phisCoObj = (FLPHISContractOrderDataType) obj;
				coObj = phisCoObj.getContractOrder();
			} else {
				coObj = (FLContractOrderDataType) obj;
			}
			flReqDTO.setFlRequestId(coObj.getOrderDetails().getRequestId().longValue());

			resDTO = requestSO.retrieveFulfilmentRequest(flReqDTO);

			IContractSupplierSO contractSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSupplierSO.class);
			ContractSupplierReqDTO contractSupplierReqDTO = new ContractSupplierReqDTO();
			if(null != resDTO.getFlFulfilmentRequest()){
				Long supplieContractId=resDTO.getFlFulfilmentRequest().getCtContract().getContractId();
				contractSupplierReqDTO.setContractId(supplieContractId);
				ContractSupplierResDTO contractSupplierResDTO = contractSupplierSO.retrieveFactoringByContract(contractSupplierReqDTO);
				if(contractSupplierResDTO.getFactoring() != null){
					contractSupplierResDTO.getFactoring();
					resDTO.getFlFulfilmentRequest().getCtContract().setCtFactoring(contractSupplierResDTO.getFactoring());
					if(contractSupplierResDTO.getFactoring().getFinancialOrg() != null){
						contractSupplierResDTO.getFactoring().getFinancialOrg().getFinOrgName();
						getSelectedFactoringCompDtl(contractSupplierResDTO.getFactoring().getFinancialOrg().getFinancialOrgId());
					}
				}else{
					resDTO.getFlFulfilmentRequest().getCtContract().setFactored(false);
				}
			}	

			if (coObj.getContractId().intValue() == 0) {
				FlFulfilmentRequest flRequest = new FlFulfilmentRequest();
				IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				ContractReqDTO contractReqDTO = new ContractReqDTO();
				contractReqDTO.setContractId(resDTO.getFlFulfilmentRequest().getCtContract().getContractId());
			
				resDTO.getFlFulfilmentRequest().setCtContract(contractSO.retrieveContract(contractReqDTO).getContract());
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
//			String[] outcome = new String[res.getAction().length + 1];
//			outcome[0] = BpmOutcomeConst.BPM_SAVE;
//			int i = 1;
//			for (String action : res.getAction()) {
//				outcome[i++] = action;
//			}
//			getManagedBean().setTaskId(taskId);
//			getManagedBean().setOutComes(outcome);
			setFulfilmentRequestField(resDTO);
			getManagedBean().setPmPriceType(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractItems().get(0).getPriceType());
			
			if(!getManagedBean().getFlRequest().getFlDeliveryAddresses().isEmpty()){
				getManagedBean().setSelectedDelAddr(getManagedBean().getFlRequest().getFlDeliveryAddresses().get(0));
			}
			
			getManagedBean().setSelectedTabIndex(0);
			if(!getManagedBean().getFlRequest().getFlDeliveryAddresses().isEmpty()){
				sortAddress(getManagedBean().getFlRequest().getFlDeliveryAddresses());
				getManagedBean().setSelectedDelAddr(getManagedBean().getFlRequest().getFlDeliveryAddresses().get(0));
			}	
			for(CtAgency agency : getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtAgencies()){
				if(agency.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE || 
						(agency.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_SUSPENDED && agency.getEffDate().after(new Date()))) {
					getManagedBean().getAgentOrgProfileList().add(agency.getOrgProfile().getOrgProfileId());
					if(agency == getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtAgencies().get(0)){
						getManagedBean().setOrgProfileIds(agency.getOrgProfile().getOrgProfileId().toString());
					}else{
						getManagedBean().setOrgProfileIds(getManagedBean().getOrgProfileIds() + "," +agency.getOrgProfile().getOrgProfileId().toString());
					}
				}
			}
			if(!getManagedBean().getFlRequest().getCtContract().isZonalPricing()){
				updateDeliveryDate();
			}
			if(coObj.getOrderDetails().getErrorId() != null && coObj.getOrderDetails().getErrorId() > 0){
				getManagedBean().setAutoUpdate(Boolean.FALSE);
				getManagedBean().setGfmasError(true);
				FulfilmentReqDTO errReq = new FulfilmentReqDTO();
				errReq.setErrorId(coObj.getOrderDetails().getErrorId());
				FulfilmentResDTO errRes = requestSO.retrieveErrorContent(errReq);
				getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
			}
			updateGstRate(getManagedBean().getFlRequest());
			setFlSupplierDtl(getManagedBean().getFlRequest());
			getSupplierGst(getManagedBean().getFlRequest(), getManagedBean().isGstRateChange());
			getAgAccount(entity);
			if(getManagedBean().getFlRequest().isBrandNew()){
				getManagedBean().getFlRequest().setCreatedDate(new Timestamp(System.currentTimeMillis()));
			}
			IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
			ContractAmendmentReqDTO amendReq = new ContractAmendmentReqDTO();
			amendReq.setContractVerId(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getContractVerId());
			amendReq.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
			ContractAmendmentResDTO amendRes = ctAmendmentSO.checkAgencyAddressExist(amendReq);
			getManagedBean().setAgencyAddress(amendRes.isAgencyAddressExist());
			checkMofAccountExpDate(getManagedBean().getFlRequest());
			if(getManagedBean().getFlRequest().getScPurchaseInDefault() != null){
				if(getManagedBean().getFlRequest().getFlTempItems().isEmpty()){
					ContractItemReqDTO ctItemReqDTO = new ContractItemReqDTO();
					ctItemReqDTO.setContractItemId(getManagedBean().getFlRequest().getScPurchaseInDefault().getContractedItem().getContractItemId());
					ContractItemResDTO ctItemResDTO = contractItemSO.getContractItem(ctItemReqDTO);
					FlTempItem flTempItem = new FlTempItem();
					flTempItem.setFlRequest(getManagedBean().getFlRequest());
					flTempItem.setScRequestItem(ctItemResDTO.getCtContractItem().getScRequestItem());
					getManagedBean().getFlRequest().getFlTempItems().add(flTempItem);
					setFulfilmentItemAddr(getManagedBean().getFlRequest());
				}
				// check pid approval days and CR created date
				int pidValidDays = getManagedBean().getFlRequest().getScPurchaseInDefault().getPidValidDays();
				Date pidExpiredDate = DateUtils.addDays(getManagedBean().getFlRequest().getCreatedDate(), pidValidDays);
				if(pidExpiredDate.compareTo(DateUtils.getCurrentDate())<0) {
					getManagedBean().setPidValidityExpired(true);
					if(isRequisitioner()) {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_740040, null, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
					} else {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_740041, null, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));						
					}
				}
			}
				
		}else if(contractId != null){
			renewSesionObject();
			ContractRequestBackingBean crBB = new ContractRequestBackingBean();
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setContractId(Long.parseLong(contractId));
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.renewSesionObject();
			IContractSO contractSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			IStatusSO statusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			ContractReqDTO ctDTO = new ContractReqDTO();
			ctDTO.setContractId(getManagedBean().getContractId());
			ContractResDTO contractResDTO = contractSO.retrieveContract(ctDTO);
			getManagedBean().getFlRequest().setCtContract(contractResDTO.getContract());
			if( null != getManagedBean().getFlRequest().getCtContract().getCtFactoring()){
				if( null != getManagedBean().getFlRequest().getCtContract().getCtFactoring().getFinancialOrg()){
					getSelectedFactoringCompDtl(getManagedBean().getFlRequest().getCtContract().getCtFactoring().getFinancialOrg().getFinancialOrgId());
				}
			}else{
				getManagedBean().getFlRequest().getCtContract().setFactored(false);
			}
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
			FlTempItem flTempItem = null;
			if(!getManagedBean().getFlRequest().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF) && vCmSupplierItemList != null){
				for(VCmSupplierItem supplierItem : vCmSupplierItemList) {
					ContractItemReqDTO ctItemReqDTO = new ContractItemReqDTO();
					ctItemReqDTO.setContractItemId(supplierItem.getDocId());
					ContractItemResDTO ctItemResDTO = contractItemSO.getContractItem(ctItemReqDTO);
					flTempItem = new FlTempItem();
					flTempItem.setFlRequest(getManagedBean().getFlRequest());
					flTempItem.setScRequestItem(ctItemResDTO.getCtContractItem().getScRequestItem());
					getManagedBean().getFlRequest().getFlTempItems().add(flTempItem);
				}
				externalContext.getApplicationMap().put(facesScopes.getUserSessionBean().getUserId().toString()+contractId.toString(), null);
			} else if(getManagedBean().getFlRequest().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
				for(CtContractItem contractItem : getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractItems()) {
					if(contractItem.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE || 
						(contractItem.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_SUSPENDED && contractItem.getEffDate().after(new Date()))) {
						flTempItem = new FlTempItem();
						flTempItem.setFlRequest(getManagedBean().getFlRequest());
						flTempItem.setScRequestItem(contractItem.getScRequestItem());
						getManagedBean().getFlRequest().getFlTempItems().add(flTempItem);
					}
				}
			}
			for(CtAgency agency : getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtAgencies()){
				if(agency.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE || 
					(agency.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_SUSPENDED && agency.getEffDate().after(new Date()))) {
					getManagedBean().getAgentOrgProfileList().add(agency.getOrgProfile().getOrgProfileId());
					if(agency == getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtAgencies().get(0)){
						getManagedBean().setOrgProfileIds(agency.getOrgProfile().getOrgProfileId().toString());
					}else{
						getManagedBean().setOrgProfileIds(getManagedBean().getOrgProfileIds() + "," +agency.getOrgProfile().getOrgProfileId().toString());
					}
				}
			}
			IFulfilmentRequestSO fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			reqDTO.setUserId(facesScopes.getUserSessionBean().getUserId());
			resDTO = fulfilmentRequestSO.retrievePmOrgProfileDtls(reqDTO);
			getManagedBean().getFlRequest().setPreparedOrgProfile(resDTO.getPmOrgProfile());
			getManagedBean().getFlRequest().setChargeOrgProfile(resDTO.getPmOrgProfile());
			getManagedBean().getFlRequest().setIssuedOrgProfile(resDTO.getPmOrgProfile());
			
			IOrgProfileSO profileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileReqDTO orgReqDTO = new OrgProfileReqDTO();
			orgReqDTO.setOrgProfileId(getManagedBean().getFlRequest().getChargeOrgProfile().getOrgProfileId());
			getManagedBean().setCurrentChargePtjGroup(profileSO.retrieveJabatanByOrgProfileId(orgReqDTO).getPmOrgProfile());
			
			getLoginUserOrg();
			String[] outcome = new String[2];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			outcome[1] = BpmOutcomeConst.BPM_SUBMIT;
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(outcome);
			FulfilmentBackingBean flBackingBean = new FulfilmentBackingBean();
			flBackingBean.initializeRemarkAndAttachement(getManagedBean().getFlRequest().getFlWorkflowStatus(), flBB.getManagedBean().getRemarkList(), flBB.getManagedBean().getAttachmentList());
			getManagedBean().setSelectedTabIndex(0);
			if(!getManagedBean().getFlRequest().getFlDeliveryAddresses().isEmpty()){
				getManagedBean().setSelectedDelAddr(getManagedBean().getFlRequest().getFlDeliveryAddresses().get(0));
			}
			
			if(!getManagedBean().getFlRequest().getCtContract().isZonalPricing()){
				getManagedBean().getFlRequest().setPmUom(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getDeliveryTermUom());
				getManagedBean().getFlRequest().setDeliveryTerm(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getDeliveryTerm());
			}
			getSupplierBranchesList(getManagedBean().getFlRequest().getSmSupplier());
			
			// No schedule no is defined if this is coming from Search Catalogue. Need to assign available scheduleNo for it.
			reqDTO.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
			reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
			if(scheduleNo==null && contractResDTO.getContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
				if(contractResDTO.getContract().isFrequencySchedule() && contractResDTO.getContract().getLatestContractVer().getCtSchedules()!=null) {
					for(CtSchedule ctSchedule : contractResDTO.getContract().getLatestContractVer().getCtSchedules()) {
						scheduleNo = ctSchedule.getScheduleNo().toString();
						reqDTO.setScheduleNo(scheduleNo);
						if(!fulfilmentRequestSO.isContractRequestInUsed(reqDTO)) {
							break;
						}
					}
				} else if(contractResDTO.getContract().getLatestContractVer().getCtProgressivePayments()!=null) {
					for(CtProgressivePayment ctProgressivePayment : contractResDTO.getContract().getLatestContractVer().getCtProgressivePayments()) {
						scheduleNo = ctProgressivePayment.getPhaseNo().toString();
						reqDTO.setScheduleNo(scheduleNo);
						if(!fulfilmentRequestSO.isContractRequestInUsed(reqDTO)) {
							break;
						}						
					}
				}
				getManagedBean().getFlRequest().setScheduleNo(scheduleNo);
			}
			updateGstRate(getManagedBean().getFlRequest());
			getManagedBean().getFlRequest().setProcurementTypeCategory(contractResDTO.getContract().getLatestContractVer().getCtContractValue().getProcurementTypeCategory());
			
			setFlSupplierDtl(getManagedBean().getFlRequest());
			getSupplierGst(getManagedBean().getFlRequest(), getManagedBean().isGstRateChange());
			getAgAccount(entity);
			if(getManagedBean().getFlRequest().isBrandNew()){
				getManagedBean().getFlRequest().setCreatedDate(new Timestamp(System.currentTimeMillis()));
			}
			IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
			ContractAmendmentReqDTO amendReq = new ContractAmendmentReqDTO();
			amendReq.setContractVerId(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getContractVerId());
			amendReq.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
			ContractAmendmentResDTO amendRes = ctAmendmentSO.checkAgencyAddressExist(amendReq);
			getManagedBean().setAgencyAddress(amendRes.isAgencyAddressExist());
			
			checkMofAccountExpDate(getManagedBean().getFlRequest());
		}
		
		//Default procurement type category to Supply of Goods in case of null
		if(getManagedBean().getFlRequest().getProcurementTypeCategory()==null) {
			ParameterReqDTO paramReqDTO = new ParameterReqDTO();
			paramReqDTO.setParameterType(ParameterTypeConst.PROCUREMENT_TYPE_CATEGORY);
			paramReqDTO.setParameterCode(ParameterConst.PROCUREMENT_TYPE_CODE_SUPPLY);
			IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			PmParameter procurementTypeCategory = parameterSO.retrieveParameterByTypeAndCode(paramReqDTO).getPmParameter();
			getManagedBean().getFlRequest().setProcurementTypeCategory(procurementTypeCategory);
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
//		setOneOffFulfilmentItemAddr(getManagedBean().getFlRequest());
		setFulfilmentItemAddr(getManagedBean().getFlRequest());
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
				//getManagedBean().setGstRegistrationDate(flRequest.getSmSupplier().getSmAppl().getSmCompanyBasic().getGstRegistrationDate());
				//getManagedBean().setGstRegistrationNo(flRequest.getSmSupplier().getSmAppl().getSmCompanyBasic().getGstRegistrationNo());
			}
			IContractSupplierSO ctSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSupplierSO.class);
			supplierDtl.setMofNo(ctSupplierSO.retrieveSupplierMOFNoByContractId(flRequest.getCtContract().getContractId()));
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
	
	private void populateItemsForSelectedAddress() {
		IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,	IContractItemSO.class);
		ContractItemReqDTO reqDTO = new ContractItemReqDTO();
		reqDTO.setPmAddress(getManagedBean().getSelectedDelAddr().getPmAddress());
		reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
		
		List<FlFulfilmentItemAddr> itemsList = new ArrayList<FlFulfilmentItemAddr>();

		for (FlFulfilmentItemAddr newItem : getManagedBean().getItemAddrList()) {
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
				newItem.setOrderedQty(BigDecimal.ZERO);
				newItem.setOrderedAmt(BigDecimal.ZERO);
				if(getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)
					||getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER)){
					newItem.setUnitPrice(BigDecimal.ZERO);
					newItem.setGstAmount(BigDecimal.ZERO);
					newItem.setGstUnitPrice(BigDecimal.ZERO);
				} else if(getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_STANDARD)) {
					if(getManagedBean().getSelectedDelAddr()!=null && getManagedBean().getSelectedDelAddr().getStartDate()!=null && getManagedBean().getSelectedDelAddr().getEndDate()!=null) {
						long day = (getManagedBean().getSelectedDelAddr().getEndDate().getTime() - getManagedBean().getSelectedDelAddr().getStartDate().getTime()) / MILLIS_PER_DAY;
						reqDTO.setDeliveryTerm(Long.valueOf(day).intValue());
					}
					reqDTO.setItemId(newItem.getScRequestItem().getRequestNoteItemId());
					reqDTO.setOrderedQty(BigDecimal.ZERO);
					ContractItemResDTO itemResDTO = contractItemSO.getItemPrice(reqDTO);//get zoneId
					if(itemResDTO.getContractZoneId() == null){
						newItem.setZoneId(itemResDTO.getContractZoneId());
						newItem.setUnitPrice(BigDecimal.ZERO);
						newItem.setGstAmount(BigDecimal.ZERO);
						newItem.setGstUnitPrice(BigDecimal.ZERO);
					}else{
						newItem.setZoneId(itemResDTO.getContractZoneId());
						if(itemResDTO.getItemPrice().doubleValue() == -1){
							newItem.setUnitPrice(BigDecimal.ZERO);
							newItem.setGstAmount(BigDecimal.ZERO);
							newItem.setGstUnitPrice(BigDecimal.ZERO);
						}else{
							newItem.setUnitPrice(itemResDTO.getItemPrice());
						}
					}
				}
				if(getManagedBean().getFlRequest().getProcurementTypeCategory().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_TYPE_CODE_SERVICE)){
					newItem.setGst(true);
					setGstPrice(newItem, newItem.getUnitPrice().doubleValue());
				} else {
					newItem.setGst(false);
					newItem.setGstAmount(BigDecimal.ZERO);
					newItem.setGstUnitPrice(newItem.getUnitPrice());
				}
				itemsList.add(newItem);
			}
		}
		getManagedBean().setShowItemAddrList(itemsList);
	}
	
	/**
	 * auto populate items for selected delivery address.
	 */
	public void selectAddrEvent() {
		populateItemsForSelectedAddress();
		RequestContext context = RequestContext.getCurrentInstance();
		context.reset("form:itemDetPanelGroup");
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

	/**
	 * get user login org.
	 */
	public void getLoginUserOrg() {
		PmUser user = getFacesScopes().getUserSessionBean();

		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(user.getUserId());
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

		PmUserOrg userOrg = resDTO.getUserOrgList().get(0);
		getManagedBean().setPmUserOrg(userOrg);

	}

	/**
	 * check role user is requisitioner
	 */
	public boolean isRequisitioner() {
		return getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value());

	}
	
	/**
	 * check role user is fl_approver
	 */
	public boolean isApprover() {
		return getFacesScopes().hasRoleUser(RoleCode.APPROVER.value());

	}
	
	private void checkContractAmount(FlFulfilmentRequest flRequest){
		IFlItemSummarySO itemSummarySO = (IFlItemSummarySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFlItemSummarySO.class);
		FlItemSummaryReqDTO reqDTO = new FlItemSummaryReqDTO();
		reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
		BigDecimal prevSumAmt = itemSummarySO.retrieveContractAccumulatedAmount(reqDTO);
		BigDecimal sumOrderedAmt = BigDecimal.ZERO;
		for(FlDeliveryAddress delAddr: flRequest.getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddr : delAddr.getFlFulfilmentItemAddr()){
				sumOrderedAmt = sumOrderedAmt.add(itemAddr.getOrderedAmt());
			}
		}
		BigDecimal checkTotalAmt = prevSumAmt.add(sumOrderedAmt);
		
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		ContractReqDTO contractReqDTO = new ContractReqDTO();
		contractReqDTO.setContractVerId(flRequest.getCtContract().getLatestContractVer().getContractVerId());
		contractReqDTO.setAsAtDate(new Date());
		CtContractAmount ctContractAmount = contractSO.retrieveContractAmount(contractReqDTO).getCtContractAmount();
		BigDecimal contractAmount = ctContractAmount.getContractAmount();
		if(ctContractAmount.getGstAmount()!=null) {
			contractAmount = contractAmount.add(ctContractAmount.getGstAmount());
		}
		
		if(checkTotalAmt.doubleValue()>contractAmount.doubleValue()) {
			CommonUtils commonUtils = new CommonUtils();
			commonUtils.getDisplayNumberFormat(2);
			DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(
					CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
			throw new EpException(MessageConst.ERROR_740002, new String[]{nf.format(contractAmount).toString()});
		}
	}

	/**
	 * check contract ceiling amount has exceed or not exceed.
	 * 
	 * @param ContractItemReqDTO ctReqDTO - contractId, orgProfileId
	 */
	public void checkCeilingAmount(FlFulfilmentRequest flRequest){
		IContractItemSO contractItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);
		ContractItemReqDTO ctReqDTO = new ContractItemReqDTO();
		ctReqDTO.setContractId(flRequest.getCtContract().getContractId());
		ctReqDTO.setOrgProfileId(flRequest.getPreparedOrgProfile().getOrgProfileId());

		ContractItemResDTO ctResDTO = new ContractItemResDTO();
		ctResDTO = contractItemSO.getAgencyCeilingAmount(ctReqDTO);
		if(ctResDTO.getAgencyCeilingAmount().doubleValue() > 0){
			BigDecimal sumOrderedAmt = BigDecimal.ZERO;
			BigDecimal checkCeilingAmt = BigDecimal.ZERO;
			for(FlDeliveryAddress delAddr: flRequest.getFlDeliveryAddresses()){
				for(FlFulfilmentItemAddr itemAddr : delAddr.getFlFulfilmentItemAddr()){

					sumOrderedAmt = sumOrderedAmt.add(itemAddr.getOrderedAmt());
				}
			}
			IFlItemSummarySO itemSummarySO = (IFlItemSummarySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFlItemSummarySO.class);
			FlItemSummaryReqDTO reqDTO = new FlItemSummaryReqDTO();
			reqDTO.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
			reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
			BigDecimal prevSumAmt = itemSummarySO.retrieveContractAgencyAccumulatedAmount(reqDTO);
			checkCeilingAmt = prevSumAmt.add(sumOrderedAmt);

			if(checkCeilingAmt.doubleValue() >  ctResDTO.getAgencyCeilingAmount().doubleValue()){
				throw new EpException(MessageConst.ERROR_740032);
			}
		}
	}

	/**
	 * check contract ceiling quantity has exceed or not exceed.
	 * 
	 * @param FlItemSummaryReqDTO reqDTO - contractId, orgProfileId,itemId
	 */
	public void checkCeilingQty(FlFulfilmentRequest flRequest){
		IContractItemSO contractItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);
		IFlItemSummarySO itemSummarySO = (IFlItemSummarySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFlItemSummarySO.class);
		
		FlItemSummaryReqDTO reqDTO = new FlItemSummaryReqDTO();
		ContractItemReqDTO ctReqDTO = new ContractItemReqDTO();
		ContractItemResDTO ctResDTO = new ContractItemResDTO();
		ctReqDTO.setContractId(flRequest.getCtContract().getContractId());
		ctReqDTO.setOrgProfileId(flRequest.getPreparedOrgProfile().getOrgProfileId());
		BigDecimal sumOrderedQty = BigDecimal.ZERO;
		BigDecimal checkCeilingQty = BigDecimal.ZERO;
		for(FlDeliveryAddress deliveryAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddr : deliveryAddress.getFlFulfilmentItemAddr()){
				
				ctReqDTO.setItemId(itemAddr.getScRequestItem().getRequestNoteItemId());
				ctResDTO = contractItemSO.getItemCeilingQuantity(ctReqDTO);
				if(ctResDTO.getItemCeilingQuantity().doubleValue() <= 0){
					break;
				}
				sumOrderedQty = sumOrderedQty.add(itemAddr.getOrderedQty());
				
				reqDTO.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
				reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
				BigDecimal prevSumQty = itemSummarySO.retrieveContractAgencyAccumulatedQty(reqDTO);
				checkCeilingQty = prevSumQty.add(sumOrderedQty);

				if(checkCeilingQty.doubleValue() >  ctResDTO.getItemCeilingQuantity().doubleValue()){
					throw new EpException(MessageConst.ERROR_740033,new String[]{itemAddr.getScRequestItem().getItemName()});
				}
			}
		}
	}
	
	private void checkMaximumQty(FlFulfilmentRequest flRequest) {
		IContractItemSO contractItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);
		IFlItemSummarySO itemSummarySO = (IFlItemSummarySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFlItemSummarySO.class);
		
		FlItemSummaryReqDTO reqDTO = new FlItemSummaryReqDTO();
		reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
		
		ContractItemReqDTO ctReqDTO = new ContractItemReqDTO();
		ContractItemResDTO ctResDTO = null;
		ctReqDTO.setContractVerId(flRequest.getCtContract().getLatestContractVer().getContractVerId());
		
		if(!flRequest.getCtContract().isZonalPricing()) {
			Map<ScRequestItem, BigDecimal> requestItemQtyMap = new HashMap<ScRequestItem, BigDecimal>();
			for(FlDeliveryAddress deliveryAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()){
				for(FlFulfilmentItemAddr itemAddr : deliveryAddress.getFlFulfilmentItemAddr()){
					if(requestItemQtyMap.containsKey(itemAddr.getScRequestItem())) {
						requestItemQtyMap.put(itemAddr.getScRequestItem(), requestItemQtyMap.get(itemAddr.getScRequestItem()).add(itemAddr.getOrderedQty()));
					} else{
						requestItemQtyMap.put(itemAddr.getScRequestItem(), itemAddr.getOrderedQty());
					}
				}
			}
			
			BigDecimal itemMaxQty = BigDecimal.ZERO;
			BigDecimal prevSumQty = BigDecimal.ZERO;
			BigDecimal sumQty = BigDecimal.ZERO;
			for(ScRequestItem requestNoteItem : requestItemQtyMap.keySet()) {
				if(requestNoteItem.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
					continue;
				}
				ctReqDTO.setItemId(requestNoteItem.getRequestNoteItemId());
				ctResDTO = contractItemSO.getItemMaxQuantity(ctReqDTO);
				if(ctResDTO.getCtItemMaxQuantity()!=null) {
					itemMaxQty = ctResDTO.getCtItemMaxQuantity().getMaxQuantity();
				}
				if(itemMaxQty!=null) {
					reqDTO.setItemId(requestNoteItem.getRequestNoteItemId());
					prevSumQty = itemSummarySO.retrieveContractAgencyAccumulatedQty(reqDTO);
					sumQty = requestItemQtyMap.get(requestNoteItem).add(prevSumQty);
					if(sumQty.doubleValue()>itemMaxQty.doubleValue()) {
						throw new EpException(MessageConst.ERROR_740038,new String[]{requestNoteItem.getItemName()});
					}
				}
			}
		} else { // in case of zonal pricing
			IContractZoneSO contractZoneSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractZoneSO.class);
			ContractZoneReqDTO czReqDTO = new ContractZoneReqDTO();
			Map<ScRequestItem, Map<Long, BigDecimal>> requestItemZonalQtyMap = new HashMap<ScRequestItem, Map<Long, BigDecimal>>();
			CtContractZone ctContractZone = null;
			Long ctContractZoneId = null;
			Map<Long, BigDecimal> zonalQtyMap = null;
			for(FlDeliveryAddress deliveryAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()){
				czReqDTO.setContractId(flRequest.getCtContract().getContractId());
				czReqDTO.setPmAddress(deliveryAddress.getPmAddress());
				ctContractZone = contractZoneSO.retrieveContractZoneByAddress(czReqDTO).getCtContractZone();
				if(ctContractZone!=null) {
					ctContractZoneId = ctContractZone.getContractZoneId();
				} else {
					continue;
				}
				for(FlFulfilmentItemAddr itemAddr : deliveryAddress.getFlFulfilmentItemAddr()){
					zonalQtyMap = null;
					if(requestItemZonalQtyMap.containsKey(itemAddr.getScRequestItem())) {
						zonalQtyMap = requestItemZonalQtyMap.get(itemAddr.getScRequestItem());
						if(zonalQtyMap.containsKey(ctContractZoneId)) {
							zonalQtyMap.put(ctContractZoneId, zonalQtyMap.get(ctContractZoneId).add(itemAddr.getOrderedQty()));
						} else {
							zonalQtyMap.put(ctContractZoneId, itemAddr.getOrderedQty());
						}
					} else{
						zonalQtyMap = new HashMap<Long, BigDecimal>();
						zonalQtyMap.put(ctContractZoneId, itemAddr.getOrderedQty());
						requestItemZonalQtyMap.put(itemAddr.getScRequestItem(), zonalQtyMap);
					}
				}
			}
			
			BigDecimal itemMaxQty = BigDecimal.ZERO;
			BigDecimal prevSumQty = BigDecimal.ZERO;
			BigDecimal sumQty = BigDecimal.ZERO;
			for(ScRequestItem requestNoteItem : requestItemZonalQtyMap.keySet()) {
				if(requestNoteItem.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
					continue;
				}
				zonalQtyMap = requestItemZonalQtyMap.get(requestNoteItem);
				for(Long contractZoneId : zonalQtyMap.keySet()) {
					ctReqDTO.setItemId(requestNoteItem.getRequestNoteItemId());
					ctReqDTO.setContractZoneId(contractZoneId);
					ctResDTO = contractItemSO.getItemMaxQuantity(ctReqDTO);
					if(ctResDTO.getCtItemMaxQuantity()!=null) {
						itemMaxQty = ctResDTO.getCtItemMaxQuantity().getMaxQuantity();
					}
					if(itemMaxQty!=null) {
						reqDTO.setItemId(requestNoteItem.getRequestNoteItemId());
						reqDTO.setCtContractZoneId(contractZoneId);
						prevSumQty = itemSummarySO.retrieveContractAgencyAccumulatedQty(reqDTO);
						sumQty = zonalQtyMap.get(contractZoneId).add(prevSumQty);
						if(sumQty.doubleValue()>itemMaxQty.doubleValue()) {
							throw new EpException(MessageConst.ERROR_740038,new String[]{requestNoteItem.getItemName()});
						}
					}					
				}
			}			
		}
	}
	
	/**
	 * initialize approver group.
	 */
	public void initializeApproverGroup() {
		IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);;
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		try {
			if(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId() != getManagedBean().getCurrentPreparedOrgId()){
				
				reqDTO.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
				resDTO = requestSO.retrieveGroupListByOrgId(reqDTO);
				
				PmUserGroup pmUserGroup = new PmUserGroup();
				getManagedBean().getFlRequest().setPmUserGroup(pmUserGroup);
				getManagedBean().getUserGroupList().clear();
				
				//new
				getManagedBean().setUserGroupList(resDTO.getUserGroupList());
				if(resDTO.getUserGroupList().size()>0) {
					List<PmUserGroupUser> firstPmUserGroup = null;
					for(PmUserGroup userGroup : getManagedBean().getUserGroupList()) {
						reqDTO.setGroupId(userGroup.getUserGroupId());
						reqDTO.setApproverLimit(calculatesTotalOrderedAmt());
						resDTO = requestSO.retrieveApproverListByGroupId(reqDTO);
						if(firstPmUserGroup==null) {
							firstPmUserGroup = resDTO.getApproverGroupUserList();
						}
						for(PmUserGroupUser groupUser : resDTO.getApproverGroupUserList()) {
							getManagedBean().getApproverMap().put(groupUser.getPmUser().getUserId(), groupUser);
						}
					}
					
					getManagedBean().getApproverList().clear();
					for(PmUserGroupUser groupUser : firstPmUserGroup) {
						getManagedBean().getApproverList().add(groupUser.getPmUser().getUserId());
					}
					if(getManagedBean().getFlRequest().getPmUserGroup().getUserGroupId() == null){
						getManagedBean().getFlRequest().getPmUserGroup().setUserGroupId(getManagedBean().getUserGroupList().get(0).getUserGroupId());
					}
					
				}
			}
			getManagedBean().setCurrentPreparedOrgId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
			if(getManagedBean().getFlRequest().getPmUserGroup()!=null && 
				getManagedBean().getFlRequest().getPmUserGroup().getUserGroupId() == null){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
						MessageUtils.getMessage(MessageConst.ERROR_745070, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			requestSO = null;
		}
	}
	
	/**
	 * Retrieve approver list and set into approverModel.
	 */
	public void userGroupChangeListener() {
		IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		if(getManagedBean().getFlRequest().getPmUserGroup().getUserGroupId() == null){
			return;
		}
		reqDTO.setGroupId(getManagedBean().getFlRequest().getPmUserGroup().getUserGroupId());
		reqDTO.setApproverLimit(calculatesTotalOrderedAmt());
		try {
			resDTO = requestSO.retrieveApproverListByGroupId(reqDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
        getManagedBean().getApproverList().clear();
        getManagedBean().getApproverGroupUserList().clear();
        getManagedBean().getApproverGroupUserList().addAll(resDTO.getApproverGroupUserList());
		for(PmUserGroupUser groupUser : resDTO.getApproverGroupUserList()) {
			getManagedBean().getApproverList().add(groupUser.getPmUser().getUserId());
		}
	}
	
	/**
	 * Retrieve pmUserGroup List
	 */
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
	
	/**
	 * Retrieve order amount base on different price type. 
	 * <li> Tier price type base on total quantity to get the specific unit price to calculates the total amount.
	 * <li> Zonal tier price type base on selected address are within the zone and total quantity to get the specific unit price.
	 * <li> Zonal price type base on the selected address are within the zone to get the specific unit price to calculates the total amount.
	 * <li> Standard price type base on the catalog unit price.
	 * <li> Standard zonal price type base on the selected address are within the zone to get the specific unit price.
	 * lip> Tariff price type base on the quantity range that defined in sourcing to get the specific unit price.
	 * @param contractId, itemId, pmAddress, orderQty
	 */
	public void retrieveOrderAmount(FlFulfilmentItemAddr flItemAddr){
		IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,	IContractItemSO.class);
		ContractItemReqDTO reqDTO = new ContractItemReqDTO();
		reqDTO.setPmAddress(getManagedBean().getSelectedDelAddr().getPmAddress());
		reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
		reqDTO.setItemId(flItemAddr.getScRequestItem().getRequestNoteItemId());
		if(null != flItemAddr.getOrderedQty()){
			BigDecimal formattedOrderedQty = flItemAddr.getOrderedQty().setScale(flItemAddr.getScRequestItem().getUom().getDecimalScale(),BigDecimal.ROUND_HALF_UP);
			flItemAddr.setOrderedQty(formattedOrderedQty);
		}
		// Tier only
		if(getManagedBean().getPmPriceType() != null 
				&& (getManagedBean().getPmPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_TIER))){// TIER PRICE
			if(null == flItemAddr.getOrderedQty()){
				flItemAddr.setOrderedAmt(BigDecimal.ZERO);
			}else{
				flItemAddr.setOrderedQty(flItemAddr.getOrderedQty().setScale(flItemAddr.getScRequestItem().getUom().getDecimalScale(),BigDecimal.ROUND_HALF_UP));
			}
			List<FlFulfilmentItemAddr> itemAddrList = null;
			Long key = null;
			BigDecimal sumOrderQty = BigDecimal.ZERO;

			FlFulfilmentItemAddr oriItemAddr = flItemAddr;

			if(flItemAddr.getOrderedQty() == null || flItemAddr.getOrderedQty().doubleValue() == 0){
				if(getManagedBean().getItemAddrList()!=null && getManagedBean().getItemAddrList().contains(flItemAddr)){
					return;
				}else{
					key = flItemAddr.getScRequestItem().getRequestNoteItemId();
					itemAddrList = getManagedBean().getTierMap().get(key);
				}
			}else{
				if(getManagedBean().getItemAddrList()!=null && getManagedBean().getItemAddrList().contains(flItemAddr)){
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
					itemAddr.setOrderedQty(BigDecimal.ZERO);
				}else{
					sumOrderQty = sumOrderQty.add(itemAddr.getOrderedQty());	
				}
			}
			ContractItemResDTO tierResDTO = new ContractItemResDTO();
			if(sumOrderQty.doubleValue() != 0){
				reqDTO.setOrderedQty(sumOrderQty);
				tierResDTO = contractItemSO.getItemPrice(reqDTO);
				
				for(FlFulfilmentItemAddr itemAddr : itemAddrList){
					itemAddr.setUnitPrice(tierResDTO.getItemPrice());
					setGstPrice(itemAddr, tierResDTO.getItemPrice().doubleValue());
					itemAddr.setOrderedAmt(itemAddr.getOrderedQty().multiply(itemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
				}
			}else{
				for(FlFulfilmentItemAddr itemAddr : itemAddrList){
					itemAddr.setUnitPrice(BigDecimal.ZERO);
					itemAddr.setGstAmount(BigDecimal.ZERO);
					itemAddr.setGstUnitPrice(BigDecimal.ZERO);
					itemAddr.setOrderedAmt(BigDecimal.ZERO);
				}
			}
			
			if(oriItemAddr.getOrderedQty() == null){
				oriItemAddr.setOrderedQty(BigDecimal.ZERO);
			}
			
			if(tierResDTO.getItemPrice() == null || tierResDTO.getItemPrice().doubleValue() == -1){
				oriItemAddr.setUnitPrice(BigDecimal.ZERO);
				oriItemAddr.setGstAmount(BigDecimal.ZERO);
				oriItemAddr.setGstUnitPrice(BigDecimal.ZERO);
				oriItemAddr.setOrderedAmt(BigDecimal.ZERO);
			}else{
				oriItemAddr.setUnitPrice(tierResDTO.getItemPrice());
				 setGstPrice(oriItemAddr, tierResDTO.getItemPrice().doubleValue());
				 oriItemAddr.setOrderedAmt(oriItemAddr.getOrderedQty().multiply(oriItemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
			}
			if(oriItemAddr.getOrderedQty().doubleValue() == 0){
				oriItemAddr.setGstAmount(BigDecimal.ZERO);
				oriItemAddr.setGstUnitPrice(BigDecimal.ZERO);
				oriItemAddr.setUnitPrice(BigDecimal.ZERO);
			}
			if(sumOrderQty.doubleValue() > 0 && tierResDTO.getItemPrice().doubleValue() == -1){
				BigDecimal reCalSumOrderQty = BigDecimal.ZERO;
				flItemAddr.setOrderedQty(BigDecimal.ZERO);
				for(FlFulfilmentItemAddr itemAddr : itemAddrList){
					reCalSumOrderQty = reCalSumOrderQty.add(itemAddr.getOrderedQty());	
				}
				if(reCalSumOrderQty.doubleValue() != 0){
					reqDTO.setOrderedQty(reCalSumOrderQty);
					ContractItemResDTO reCalculateUnitPrice = contractItemSO.getItemPrice(reqDTO);
					for(FlFulfilmentItemAddr itemAddr : itemAddrList){
						itemAddr.setUnitPrice(reCalculateUnitPrice.getItemPrice());
						setGstPrice(itemAddr, reCalculateUnitPrice.getItemPrice().doubleValue());
						itemAddr.setOrderedAmt(itemAddr.getOrderedQty().multiply(itemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
					}
				}else{
					for(FlFulfilmentItemAddr itemAddr : itemAddrList){
						itemAddr.setGstAmount(BigDecimal.ZERO);
						itemAddr.setGstUnitPrice(BigDecimal.ZERO);
						itemAddr.setUnitPrice(BigDecimal.ZERO);
						itemAddr.setOrderedAmt(BigDecimal.ZERO);
					}
				}
				
				itemAddrList.remove(flItemAddr);
				throw new EpException(MessageConst.ERROR_745055);
			}

			flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
		}else if(getManagedBean().getPmPriceType() != null 
				&& (getManagedBean().getPmPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_TIER))){//ZONAL TIER PRICE
			
			List<FlFulfilmentItemAddr> itemAddrList = null;
			String key = null;
			BigDecimal sumOrderQty = BigDecimal.ZERO;
			if(null == flItemAddr.getOrderedQty()){
				flItemAddr.setOrderedQty(BigDecimal.ZERO);
			}
			
			reqDTO.setOrderedQty(flItemAddr.getOrderedQty());
			ContractItemResDTO itemResDTO = contractItemSO.getItemPrice(reqDTO);//get zoneId
			flItemAddr.setZoneId(itemResDTO.getContractZoneId());
			
			FlFulfilmentItemAddr oriItemAddr = flItemAddr;
			
			if(flItemAddr.getOrderedQty() == null || flItemAddr.getOrderedQty().doubleValue() == 0){
				if(getManagedBean().getItemAddrList()!=null && getManagedBean().getItemAddrList().contains(flItemAddr)){
					return;
				}else{
					key = flItemAddr.getZoneId()+" "+flItemAddr.getScRequestItem().getRequestNoteItemId();
					itemAddrList = getManagedBean().getZonalTierMap().get(key);
				}
			}else{
				if(getManagedBean().getItemAddrList()!=null && getManagedBean().getItemAddrList().contains(flItemAddr)){
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
					oriItemAddr.setOrderedQty(BigDecimal.ZERO);
					flItemAddr.setOrderedQty(BigDecimal.ZERO);
					throw new EpException(MessageConst.ERROR_745056);

				}
				if(itemAddr.getOrderedQty() == null){
					itemAddr.setOrderedQty(BigDecimal.ZERO);
				}else{
					sumOrderQty = sumOrderQty.add(itemAddr.getOrderedQty());	
				}
			}
			ContractItemResDTO tierResDTO = new ContractItemResDTO();
			if(sumOrderQty.doubleValue() != 0){
				reqDTO.setOrderedQty(sumOrderQty);
				tierResDTO = contractItemSO.getItemPrice(reqDTO);
				
				for(FlFulfilmentItemAddr itemAddr : itemAddrList){
					itemAddr.setUnitPrice(tierResDTO.getItemPrice());
					setGstPrice(itemAddr, tierResDTO.getItemPrice().doubleValue());
					itemAddr.setOrderedAmt(itemAddr.getOrderedQty().multiply(itemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
				}
			}else{
				for(FlFulfilmentItemAddr itemAddr : itemAddrList){
					itemAddr.setUnitPrice(BigDecimal.ZERO);
					itemAddr.setGstAmount(BigDecimal.ZERO);
					itemAddr.setGstUnitPrice(BigDecimal.ZERO);
					itemAddr.setOrderedAmt(BigDecimal.ZERO);
				}
			}
			
			
			if(oriItemAddr.getOrderedQty() == null){
				oriItemAddr.setOrderedQty(BigDecimal.ZERO);
			}
			if(tierResDTO.getItemPrice() == null || tierResDTO.getItemPrice().doubleValue() == -1){
				oriItemAddr.setUnitPrice(BigDecimal.ZERO);
				oriItemAddr.setGstAmount(BigDecimal.ZERO);
				oriItemAddr.setGstUnitPrice(BigDecimal.ZERO);
				oriItemAddr.setOrderedAmt(BigDecimal.ZERO);
			}else{
				oriItemAddr.setUnitPrice(tierResDTO.getItemPrice());
				setGstPrice(oriItemAddr, tierResDTO.getItemPrice().doubleValue());
				oriItemAddr.setOrderedAmt(oriItemAddr.getOrderedQty().multiply(oriItemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
			}
			if(oriItemAddr.getOrderedQty().doubleValue() == 0){
				oriItemAddr.setUnitPrice(BigDecimal.ZERO);
				oriItemAddr.setGstAmount(BigDecimal.ZERO);
				oriItemAddr.setGstUnitPrice(BigDecimal.ZERO);
			}
			if(sumOrderQty.doubleValue() > 0 && tierResDTO.getItemPrice().doubleValue() == -1){
				BigDecimal reCalSumOrderQty = BigDecimal.ZERO;
				flItemAddr.setOrderedQty(BigDecimal.ZERO);
				for(FlFulfilmentItemAddr itemAddr : itemAddrList){
					reCalSumOrderQty = reCalSumOrderQty.add(itemAddr.getOrderedQty());	
				}
				if(reCalSumOrderQty.doubleValue() != 0){
					reqDTO.setOrderedQty(reCalSumOrderQty);
					ContractItemResDTO reCalculateUnitPrice = contractItemSO.getItemPrice(reqDTO);
					for(FlFulfilmentItemAddr itemAddr : itemAddrList){
						itemAddr.setUnitPrice(reCalculateUnitPrice.getItemPrice());
						setGstPrice(itemAddr,reCalculateUnitPrice.getItemPrice().doubleValue());
						itemAddr.setOrderedAmt(itemAddr.getOrderedQty().multiply(itemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
					}
				}else{
					for(FlFulfilmentItemAddr itemAddr : itemAddrList){
						itemAddr.setUnitPrice(BigDecimal.ZERO);
						itemAddr.setGstAmount(BigDecimal.ZERO);
						itemAddr.setGstUnitPrice(BigDecimal.ZERO);
						itemAddr.setOrderedAmt(BigDecimal.ZERO);
					}
				}
				
				itemAddrList.remove(flItemAddr);
				//					getManagedBean().getZonalTierMap().put(flItemAddr.getZoneId()+" "+flItemAddr.getScRequestItem().getRequestNoteItemId(), itemAddrList);
				throw new EpException(MessageConst.ERROR_745055);
			}

			flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
		} else if(getManagedBean().getPmPriceType() != null 
				&& (getManagedBean().getPmPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_TARIFF) || 
					getManagedBean().getPmPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_TARIFF))){
			if(null == flItemAddr.getOrderedQty() || flItemAddr.getOrderedQty().doubleValue()==0){
				flItemAddr.setOrderedQty(BigDecimal.ZERO);
				flItemAddr.setOrderedAmt(BigDecimal.ZERO);
			} else {
				FlFulfilmentItemAddr oriItemAddr = flItemAddr;
				if(getManagedBean().getItemAddrList()!=null && getManagedBean().getItemAddrList().contains(flItemAddr)){
					FlFulfilmentItemAddr clonedlItemAddr = (FlFulfilmentItemAddr) flItemAddr.cloneEntity();
					flItemAddr = clonedlItemAddr;
				}	
				flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
				flItemAddr.setOrderedAmt(getTariffAmount(flItemAddr));
				oriItemAddr.setUnitPrice(BigDecimal.ZERO);
				oriItemAddr.setGstAmount(BigDecimal.ZERO);
				oriItemAddr.setGstUnitPrice(BigDecimal.ZERO);
				oriItemAddr.setOrderedAmt(flItemAddr.getOrderedAmt());
			}
		} else{//STANDARD PRICE and ZONAL
			if(null == flItemAddr.getOrderedQty()){
				flItemAddr.setOrderedAmt(BigDecimal.ZERO);
			} else {
				if(ParameterConst.ITEM_TYPE_SERVICE.equals(flItemAddr.getItemType().getParameterCode())){
					if(flItemAddr.getOrderedQty().doubleValue() != flItemAddr.getScRequestItem().getQuantity().doubleValue()){
						flItemAddr.setOrderedQty(BigDecimal.ZERO);
						flItemAddr.setOrderedAmt(BigDecimal.ZERO);
						throw new EpException(MessageConst.ERROR_745067,new String[]{flItemAddr.getScRequestItem().getItemName(), getManagedBean().getFlRequest().getCtContract().getContractNo()});
					}
				}
				
				FlFulfilmentItemAddr oriItemAddr = flItemAddr;
				if(getManagedBean().getItemAddrList()!=null && getManagedBean().getItemAddrList().contains(flItemAddr)){
					FlFulfilmentItemAddr clonedlItemAddr = (FlFulfilmentItemAddr) flItemAddr.cloneEntity();
					flItemAddr = clonedlItemAddr;
				}			
				if(getManagedBean().getPmPriceType() != null){
					if(getManagedBean().getPmPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_STANDARD)){
						if(getManagedBean().getSelectedDelAddr()!=null && getManagedBean().getSelectedDelAddr().getStartDate()!=null && getManagedBean().getSelectedDelAddr().getEndDate()!=null) {
							long day = (getManagedBean().getSelectedDelAddr().getEndDate().getTime() - getManagedBean().getSelectedDelAddr().getStartDate().getTime()) / MILLIS_PER_DAY;
							reqDTO.setDeliveryTerm(Long.valueOf(day).intValue());
						}
						reqDTO.setOrderedQty(flItemAddr.getOrderedQty());
						ContractItemResDTO itemResDTO = contractItemSO.getItemPrice(reqDTO);//get zoneId
						flItemAddr.setZoneId(itemResDTO.getContractZoneId());
						if(flItemAddr.getZoneId() == null){
							oriItemAddr.setOrderedQty(BigDecimal.ZERO);
							flItemAddr.setOrderedQty(BigDecimal.ZERO);
							throw new EpException(MessageConst.ERROR_745056);
						}
					}
				}
				
				if(flItemAddr.getOrderedQty() == null){
					flItemAddr.setOrderedQty(BigDecimal.ZERO);
				}
				flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
				flItemAddr.setOrderedAmt(flItemAddr.getOrderedQty().multiply(flItemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
				
				if(oriItemAddr.getOrderedQty() == null){
					oriItemAddr.setOrderedQty(BigDecimal.ZERO);
				}
				oriItemAddr.setUnitPrice(flItemAddr.getUnitPrice());
				setGstPrice(oriItemAddr,flItemAddr.getUnitPrice().doubleValue());
				oriItemAddr.setOrderedAmt(oriItemAddr.getOrderedQty().multiply(oriItemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
			}
		}
		// add  or remove items to Selected delivery address
		itemsToSelectedAddress(flItemAddr);
		populateItemsForSelectedAddress();
	}
	
	private void setCOAInfoForNewItem(FlFulfilmentItemAddr flItemAddr) {
		if(null!= flItemAddr.getOrderedQty() && flItemAddr.getOrderedQty().doubleValue()>=0 && flItemAddr.getPmVotFund()==null) {
			// Check is it FlItemAddr COA set, if yes, set it for this new item
			for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()) {
				if(flDeliveryAddress!=getManagedBean().getSelectedDelAddr()) {
					for(FlFulfilmentItemAddr itemAddr : flDeliveryAddress.getFlFulfilmentItemAddr()) {
						if(itemAddr.getScRequestItem().getRequestNoteItemId().longValue()==flItemAddr.getScRequestItem().getRequestNoteItemId().longValue() 
							// && itemAddr.getUnitPrice().doubleValue()==flItemAddr.getUnitPrice().doubleValue()
							&& itemAddr.getPmVotFund()!=null) {
							flItemAddr.setPmVotFund(itemAddr.getPmVotFund());
							flItemAddr.setPmPrgActivity(itemAddr.getPmPrgActivity());
							flItemAddr.setPmSubSetia(itemAddr.getPmSubSetia());
							flItemAddr.setAssetInd(itemAddr.getAssetInd());
							flItemAddr.setPmGlAccount(itemAddr.getPmGlAccount());
							break;
						}
					}
					if(flItemAddr.getPmVotFund()!=null) {
						break;
					}
				}
			}
		}
	}
	
	private BigDecimal getTariffAmount(FlFulfilmentItemAddr flItemAddr) {
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal quantity = flItemAddr.getOrderedQty();
		double remainingQty = quantity.doubleValue();
		
		IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,	IContractItemSO.class);
		ContractItemReqDTO reqDTO = new ContractItemReqDTO();
		reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
		reqDTO.setItemId(flItemAddr.getScRequestItem().getRequestNoteItemId());
		ContractItemResDTO ciItemResDTO = contractItemSO.retrieveItemPriceAndZone(reqDTO);
		if(ciItemResDTO.getCtItemPriceList() != null && ciItemResDTO.getCtItemPriceList().size() >0){
			for (CtItemPrice contractItemPrice : ciItemResDTO.getCtItemPriceList() ) {
				// First tariff
				if(ciItemResDTO.getCtItemPriceList().get(0)==contractItemPrice && contractItemPrice.getSubSequence()==null) {
					if(quantity.doubleValue()>=contractItemPrice.getFromQty().doubleValue() && 
						quantity.doubleValue()<=contractItemPrice.getToQty().doubleValue()) {
						return contractItemPrice.getUnitPrice();
					} else if(quantity.doubleValue()<contractItemPrice.getFromQty().doubleValue()) {
						return BigDecimal.ZERO;	
					} else {
						amount = amount.add(contractItemPrice.getUnitPrice());
						remainingQty = quantity.doubleValue() - contractItemPrice.getToQty().doubleValue();
					}
				} else  { // Subsequence tariff
					if(quantity.doubleValue()>=contractItemPrice.getFromQty().doubleValue() && 
						quantity.doubleValue()<=contractItemPrice.getToQty().doubleValue()) {
						double num = Math.ceil(remainingQty / contractItemPrice.getSubSequence().doubleValue());
						amount = amount.add(new BigDecimal(contractItemPrice.getUnitPrice().doubleValue()*num));
						return amount;
					} else {
						double num = Math.ceil((contractItemPrice.getToQty().doubleValue()-contractItemPrice.getFromQty().doubleValue()) / contractItemPrice.getSubSequence().doubleValue());
						amount = amount.add(new BigDecimal(contractItemPrice.getUnitPrice().doubleValue()*num));
						remainingQty = remainingQty - contractItemPrice.getToQty().doubleValue();
					}
				}
			}
		}
		return amount;
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
				setCOAInfoForNewItem(flItemAddr);
				getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().add(flItemAddr);
			}
		}
	}
	
	/**
	 * Edits the manual address
	 * @param flDelAddress
	 */
	public void editManualAddress(FlDeliveryAddress flDelAddress){
		getManagedBean().setEmailValidate(false);
		getManagedBean().setFlManulaAddress(flDelAddress);
		if(getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmDivision()==null) {
			getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmDivision(new PmDivision());
		}
		retrieveCountryList();
		
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(flDelAddress.getPmAddress().getPmCountry().getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStateList(resDTO.getStateList());
		
		DistrictReqDTO districtReqDTO = new DistrictReqDTO();
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
		
		if(getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmDivision().getDivisionId()!=null) {
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
	
	/**
	 * Retrieve the receiving officer list from contract 
	 * 
	 * @param flDelAddress
	 */
	public void receivingOfficerList(FlDeliveryAddress flDelAddress){
		Long addressId = flDelAddress.getPmAddress().getAddressId();
		if(flDelAddress.getReceivingGrp()!=null && flDelAddress.getReceivingGrp().getAddressTypeId()!=null) { // in case of Manual address
			ContractAmendmentReqDTO ctReqDTO = new ContractAmendmentReqDTO();
			ctReqDTO.setCtContract(getManagedBean().getFlRequest().getCtContract());
			ctReqDTO.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
			IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractAmendmentSO.class);
			ContractAmendmentResDTO resDTO = ctAmendmentSO.getActiveLocation(ctReqDTO);
			getManagedBean().setAddressTypesList(resDTO.getAddressTypeList());
			
			addressId = resDTO.getAddressTypeList().get(0).getPmAddress().getAddressId();
		}
		AddressResDTO resDTO=getReceivingOfficerList(addressId, AddressType.RECEIVING_OFFICER_ADDRESS.value());
		getManagedBean().setReceivingOffList(resDTO.getAddressTypeList());
	}
	
	/**
	 * To get the receiving officer list from profile management service object
	 * @param addressId
	 * @param addressType
	 * @return AddressResDTO resDTO
	 */
	public AddressResDTO getReceivingOfficerList(Long addressId, String addressType){
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAddressSO.class);
		AddressReqDTO reqDTO = new AddressReqDTO();
		reqDTO.setAddressId(addressId);
		reqDTO.setAddressType(addressType);
		AddressResDTO resDTO = addressSO.retrieveReceivingOfficerList(reqDTO);
		getManagedBean().setReceivingOffList(resDTO.getAddressTypeList());
		//resDTO.get
		return resDTO;
	}
	
	/**
	 * Retrieve the country list from profile management service object
	 */
	public void retrieveCountryList() {
		
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();
		getManagedBean().setCountryList(resDTO.getCountryList());
	}
	
	/**
	 * Retrieve the state list from profile management service object
	 */
	public void retrieveStateList(AjaxBehaviorEvent e) {
		Long countryIdId = (Long) e.getComponent().getAttributes().get("value");
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryIdId);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStateList(resDTO.getStateList());
	}
	
	/**
	 * Retrieve the division list from profile management service object
	 */
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
	
	/**
	 * Retrieve the district list from profile management service object
	 */
	public void retrieveDistrictList(AjaxBehaviorEvent e) {
		Long divisionId = (Long) e.getComponent().getAttributes().get("value");
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setDivisionId(divisionId);
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByDivision(reqDTO);
		getManagedBean().setDistrictList(resDTO.getDistrictList());
		
	}
	
	/**
	 * Retrieve the city list from profile management service object
	 */
	public void retrieveCityList(AjaxBehaviorEvent e) {
		Long districtId = (Long) e.getComponent().getAttributes().get("value");
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(districtId);
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);
		CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
		this.getManagedBean().setCityList(resDTO.getCityList());
	}

	/**
	 * Retrieve the delivery address list from 
	 */
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
	
	/**
	 * Selected tab will have different validation.
	 * <p>selected tab 1: 
	 * <li>validate prepared org. and agencies.
	 * <li> check contract validity
	 * <li>sort addresses
	 * <p>selected tab 2:
	 * <li>validate prepared org. agencies
	 * <li>validate order quantity
	 * <li>sort order summary list
	 * <p>selected tab 3:
	 * <li>validate prepared org. agencies
	 * <li>validate order quantity
	 * <li>populate chargeline tree data
	 * <li> get vot fund list from profile management by charge org profile id
	 * <p>selected tab 4:
	 * <li>validate charge line before approver list generate
	 * <li>initialize approver group.
	 * 
	 * @param selectedTabIndex the selected tab index
	 */
	public void onTabChange(int selectedTabIndex){
		
		initialize();
		
		if(selectedTabIndex==0){
			initializeApproverGroup();
		}
		if(selectedTabIndex==1){
			if(!getManagedBean().isFlRequestLink()){
				validatePreparedOrgAndAgencies();
				checkContractValidity(getManagedBean().getFlRequest());
				initializeApproverGroup();
			}
			sortAddress(getManagedBean().getFlRequest().getFlDeliveryAddresses());
			getManagedBean().setAutoUpdate(false);
			getManagedBean().setEmailValidate(true);
			hideItemAddButtons();
		} else if(selectedTabIndex==2){
			if(!getManagedBean().isFlRequestLink()){
				validatePreparedOrgAndAgencies();
				validateOrderQuantity();
			}
			if(getManagedBean().getFlRequest().getFlDeliveryAddresses()!=null) { 
				sortOrderSummaryList(getManagedBean().getFlRequest().getFlDeliveryAddresses());
			}
		} else if(selectedTabIndex==3){
			if(!getManagedBean().isFlRequestLink()){
				validatePreparedOrgAndAgencies();
				validateOrderQuantity();
			}
			
			populateChargeLineData(false);	
			setClassName("@(.crChargeLinePnlClass)");
			onNodeSelect();
			Long jabatanOrgProfileId = -1l;
			PmOrgProfile jabatan = retrieveJabatanOrgProfile(getManagedBean().getFlRequest().getChargeOrgProfile().getOrgProfileId());
			if(jabatan!=null) {
				jabatanOrgProfileId = jabatan.getOrgProfileId();
			}					
			getVotFundList(jabatanOrgProfileId);
			getManagedBean().setChargeLinePanel(false);
			getManagedBean().setProgramLov(false);
			getManagedBean().setProjectLov(false);
			getManagedBean().setVotFundDisplay(false);
			if(getManagedBean().getFlRequest().getFlDeliveryAddresses()!=null) { 
				sortOrderSummaryList(getManagedBean().getFlRequest().getFlDeliveryAddresses());
			}
		} else if(selectedTabIndex==4){
			if(!getManagedBean().isFlRequestLink()){
				chargeLineValidationBeforeApproverList();
				validatePreparedOrgAndAgencies();
				getApproverList();
			}
			initializeApproverGroup();
			
		}
	}
	
	private void validatePreparedOrgAndAgencies() {
		if(getManagedBean().getFlRequest().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
			if(!getManagedBean().getAgentOrgProfileList().contains(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId())){
				PmOrgProfile orgProfile = getManagedBean().getFlRequest().getPreparedOrgProfile();
				throw new EpException(MessageConst.ERROR_745054, new String[]{orgProfile.getPmOrgValidity().getOrgCode(),orgProfile.getPmOrgValidity().getOrgName(),getManagedBean().getFlRequest().getCtContract().getContractNo()});
			}
		}
	}

	private void validateOrderQuantity() {
		// validate all item quantity must be at least one
		BigDecimal accumulatedQty = BigDecimal.ZERO;
		for(FlFulfilmentItemAddr uiItemAddr : getManagedBean().getItemAddrList()) {
			accumulatedQty = BigDecimal.ZERO;
			for(FlDeliveryAddress delAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()) {
				for(FlFulfilmentItemAddr itemAddr : delAddress.getFlFulfilmentItemAddr()) {
					if(itemAddr.getScRequestItem().getRequestNoteItemId().equals(uiItemAddr.getScRequestItem().getRequestNoteItemId())) {
						accumulatedQty = accumulatedQty.add(itemAddr.getOrderedQty());
					}
				}
			}
			if(accumulatedQty.doubleValue()==0) {
				if(ParameterConst.FULFILMENT_TYPE_ONE_OFF.equalsIgnoreCase(getManagedBean().getFlRequest().getCtContract().getFulfillmentType().getParameterCode())){
					throw new EpException(MessageConst.ERROR_740037, new String[]{uiItemAddr.getScRequestItem().getItemName(),"0"});
				}else{
					throw new EpException(MessageConst.ERROR_740027, new String[]{uiItemAddr.getScRequestItem().getItemName(),"0"});
				}
			}
		}
	}
	
	private void populateChargeLineData(boolean retainPreviousSelection) {
		getManagedBean().setChargeLineItemRoot(new DefaultTreeNode("root", null));
		Map<Long, List<FlFulfilmentItemAddr>> chargeLineItemsMap = new HashMap<Long, List<FlFulfilmentItemAddr>>();
		
		for(FlDeliveryAddress  deliveryAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddress : deliveryAddress.getFlFulfilmentItemAddr()){
				itemAddress.setChargeOrderedAmt(itemAddress.getOrderedAmt());
				itemAddress.setChargeOrderedQty(itemAddress.getOrderedQty());
				itemAddress.setChargeUnitPrice(itemAddress.getGstUnitPrice());
				if(chargeLineItemsMap.containsKey(itemAddress.getScRequestItem().getRequestNoteItemId())){
					//code for calculating duplicate charge line order amount and quantity for same unit price  Condition for checking unit price same.
					boolean notContained=true;
					for(FlFulfilmentItemAddr containedItem : chargeLineItemsMap.get(itemAddress.getScRequestItem().getRequestNoteItemId())){
						if(containedItem.getChargeUnitPrice().equals(itemAddress.getGstUnitPrice())){
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
		BigDecimal grandTotalOrderAmt=BigDecimal.ZERO;
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
			totalOrderAmt = BigDecimal.ZERO;
			
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

	/**
	 * display the charge line details,if charge line applied for selected node,
	 */
	public void onNodeSelect() {
		//reset the checkboxes already selected.
		resetSelectedTreeNodes();
		getManagedBean().setChargeLinePanel(false);
		setSelectAllCheckbox(false);
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
					getManagedBean().setPmPrgActivity(childData.getPmPrgActivity());
				}
				
				if(childData.getPmSubSetia()!=null) {
					getManagedBean().setPmSubSetia(childData.getPmSubSetia());
				}
			}
		}
	
	}
	
	/**
	 * clear the charge line details,if node is unselected,
	 */
	public void onNodeUnSelect() {

		getManagedBean().setProgramLov(false);
		getManagedBean().setProjectLov(false);
		getManagedBean().setChargeLinePanel(false);
		getManagedBean().setVotFundCode("");
		getManagedBean().setVotFundId(0L);
		getManagedBean().setPmVoteFund(new PmVotFund());
		getManagedBean().setPmSubSetia(new PmSubSetia());
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
	
	/**
	 * Charge line apply for selected nodes.
	 */
	public void chargeLineApply() {
		getManagedBean().setSelectChargeLineRow(false);
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
			throw new EpException(MessageConst.ERROR_100022, new String[]{
					ResourceBundleUtils.getMessage("lbl.fl.charge.vot.code",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
		}
		if(getManagedBean().getAssetIndId() == null ){
			throw new EpException(MessageConst.ERROR_100022, new String[]{
					ResourceBundleUtils.getMessage("lbl.fl.asset.ind",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
		}
		if(getManagedBean().getGlAccountId() == null ){
			throw new EpException(MessageConst.ERROR_100022, new String[]{
					ResourceBundleUtils.getMessage("lbl.fl.gosodo",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
		}		
		
		List<Long> selectedItemAddressList = new ArrayList<Long>();
		updateSelectedTreeNodes();
		FlFulfilmentItemAddr flTemp = null;
		for(TreeNode selectedNode : getManagedBean().getSelectedNodes()){
			flTemp = (FlFulfilmentItemAddr)selectedNode.getData();
			selectedItemAddressList.add(flTemp.getScRequestItem().getRequestNoteItemId());
		}
		
		boolean isVotSOrAmanahExist = false;
		boolean isVotBOrPOrTExist = false;
		String votFundType = null;
		for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFlRequest().getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddress:deliveryAddress.getFlFulfilmentItemAddr()){
				if(selectedItemAddressList.contains(itemAddress.getScRequestItem().getRequestNoteItemId())) {
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
			if(getManagedBean().getPmPrgActivity()==null) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.fl.program.activiti",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			}
		} else if(pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.PEMBANGUNAN.value()) || pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.SETIA.value())) {
			if(getManagedBean().getPmSubSetia()==null) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.fl.project",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			}
		}
	}
	//itemAddress.getScRequestItem().getRequestNoteItemId().equals(arg0)

	private void updateSelectedTreeNodes() {
		List<TreeNode> selectedNodes=new ArrayList<TreeNode>();
		TreeNode root=getManagedBean().getChargeLineItemRoot();
		setSelectAllCheckbox(true);
		for(TreeNode node:root.getChildren()){
			FlFulfilmentItemAddr addr=(FlFulfilmentItemAddr) node.getData();
			if(addr.isSelectedChargeItem()){
				selectedNodes.add(node);
			}else{
				setSelectAllCheckbox(false);
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
	
	/**
	 * Gets the vot fund list based on profileId.
	 *
	 * @param profileId the profile id
	 * @return the vot fund list
	 */
	public void getVotFundList(Long profileId){

		IFulfilmentSO fulfilmentSO = (IFulfilmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setOrgProfileId(profileId);
		FulfilmentResDTO votFundRes = fulfilmentSO.retrieveVotFundListByOrgId(reqDTO);
		
		for (PmVotFund pmVotFund : votFundRes.getVotFundList()) {
			votFundCodeMap.put(pmVotFund.getVotFundId(), pmVotFund);
		}
		
		getManagedBean().setSubSetiaId(0L);	
		getManagedBean().setVotFundList(votFundRes.getVotFundList());
	
	}

	/**
	 * Show item query.
	 *
	 * @param flItemAddress the fl item address
	 */
	public void showItemQuery(FlFulfilmentItemAddr flItemAddress){
		setQueryItemCode(flItemAddress.getScRequestItem().getRequestNoteItemId().toString());
		setQueryItemName(flItemAddress.getScRequestItem().getItemName());
		if(null != flItemAddress.getFlFulfilmentItem().getFlQuery()){
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
	/**
	 * if any checkbox selected from delivery address  List,show the deleted button for 
	 * <p>
	 * delete the particular delivery address from delivery address  List.
	 * <p>
	 * if all check boxes unselected,hide the delete button.
	 *
	 */
	public void selectOneCheckboxSelect(AjaxBehaviorEvent e){
		
		boolean delAddrSelected=(boolean) e.getComponent().getAttributes().get("value");
		if(delAddrSelected){
			getManagedBean().setDeleteButtonHide(false);
		}else{
			//Code for check other delivery addresses selected.
			boolean isAnyDelAddeSelected=isAnyAddrSelected();
			if(!isAnyDelAddeSelected){
				getManagedBean().setDeleteButtonHide(true);
			} else {
				getManagedBean().setDeleteButtonHide(false);
			}
		}
		
	}
	
	private boolean isAnyAddrSelected() {
		boolean isAnyAddrSelected=false;
		for(FlDeliveryAddress delAddr:getManagedBean().getFlRequest().getFlDeliveryAddresses()){
			if(delAddr.isSelectedAddressRowDelete()){
				isAnyAddrSelected=true;
				break;
			}
		}
		return isAnyAddrSelected;
	}
	
	/**
	 * Update delivery date based on delivery Term and uomCode.
	 */
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
	
	/**
	 * Update delivery date based on delivery Term and uomCode.
	 */
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

	/**
	 * Gets the ag account based on Issued Org Profile id.
	 *
	 * @return the ag account
	 */
	public AgOfficeResDTO getAgAccount(BaseEntity entity){
		checkPtjGroupAndInitialize();
		getManagedBean().getFlRequest().setIssuedOrgProfile(getManagedBean().getFlRequest().getPreparedOrgProfile());
		initializeApproverGroup();
		initializeDelAddr();
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
	
	private void initializeDelAddr() {
		if(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId() != getManagedBean().getCurrentPreparedOrgId()){
			getManagedBean().getFlRequest().getFlDeliveryAddresses().clear();
			getManagedBean().getShowItemAddrList().clear();
			getManagedBean().setCurrentPreparedOrgId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
		}
		
	}

	private void checkPtjGroupAndInitialize() {
		IOrgProfileSO profileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
		OrgProfileResDTO resDTO = profileSO.retrieveJabatanByOrgProfileId(reqDTO);
		Long orgId = null;
		if(resDTO.getPmOrgProfile() != null){
			orgId = resDTO.getPmOrgProfile().getOrgProfileId();
		}
		getManagedBean().setCurrentPtjGroupId(orgId);
		if(getManagedBean().getCurrentPtjGroupId() != null){
			reqDTO.setOrgProfileId(getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId());
			if(profileSO.retrieveJabatanByOrgProfileId(reqDTO).getPmOrgProfile().getOrgProfileId() != getManagedBean().getCurrentPtjGroupId()){
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
	}
	
	/**
	 * Checks if user is  fl fulfillment requisitioner.
	 *
	 * @return true, if is fl fulfilment requisitioner
	 */
	public boolean isFlFulfilmentRequisitioner() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.REQUISITIONER.value());
	}
	
	/**
	 * update delivery address based on endDate change
	 */
	public void updateDeliveryEndDate() {
		for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()) {
			flDeliveryAddress.setEndDate(getManagedBean().getFlRequest().getEndDate());
		}
	}
	
	/**
	 * Gets the supplier branches list based on SupplierId.
	 *
	 * @param smSupplier the sm supplier
	 * @return the supplier branches list
	 */
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
	
	/**
	 * Gets the selected supplier branch details based on supplier BranchId.
	 *
	 * @param e the e
	 * @return the selected supplier branch dtl
	 */
	public void getSelectedSupplierBranchDtl(AjaxBehaviorEvent e){
		Long supplierBranchId = (Long) e.getComponent().getAttributes().get("value");
		if(supplierBranchId!=null) {
			IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,	IFulfilmentRequestSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setSupplierBranchId(supplierBranchId);
			FulfilmentResDTO resDTO = requestSO.retrieveSupplierBranchDtlByBranchId(reqDTO);
			
			SmSupplierBranch supplierBranch = resDTO.getSmSupplierBranch();//all this should reset into our Flrequest.FlSupplierDtl
			FlSupplierDtl supplierDtl = getManagedBean().getFlRequest().getFlSupplierDtl();
			supplierDtl.setBranchName(supplierBranch.getBranchName());
			supplierDtl.setBranchCode(supplierBranch.getBranchCode());
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
		getSupplierGst(getManagedBean().getFlRequest(), false);
	}
	
	/**
	 * chargeLine Panel enable if  checkbox selected
	 *
	 */
	public void selectCheckbox(AjaxBehaviorEvent e){
		getManagedBean().setSelectChargeLineRow(false);
		onNodeUnSelect();
		getManagedBean().setProgramLov(false);
		getManagedBean().setProjectLov(false);

		//Poulate chargeline data.
		//Get the selected chargeline item.
		updateSelectedTreeNodes();
		TreeNode[] selectedNodes=getManagedBean().getSelectedNodes();
		if(null!=selectedNodes && selectedNodes.length>0){
			getManagedBean().setChargeLinePanel(true);
			getManagedBean().setSelectChargeLineRow(true);
		}else{
			getManagedBean().setChargeLinePanel(false);
			getManagedBean().setSelectChargeLineRow(false);
		}

		//If Selected node is one then display chargeline data.Otherwise clear chargeline Data.
		//Because multiple nodes conflict chargeline data.
		if(null!=selectedNodes && selectedNodes.length==1){
			TreeNode selectedNode=selectedNodes[0];
			//Populate Chargeline data....
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
					getManagedBean().setPmPrgActivity(childData.getPmPrgActivity());
					getManagedBean().setProgramLov(true);
				}

				if(childData.getPmSubSetia()!=null) {
					getManagedBean().setPmSubSetia(childData.getPmSubSetia());
					getManagedBean().setProjectLov(true);
				}
			}
		}	 
	}
	
	/**
	 * Select all checkboxes.
	 * chargeLine Panel enable if  checkboxes selected
	 */
	public void selectAllCheckboxes(){
		getManagedBean().setSelectChargeLineRow(false);
		onNodeUnSelect();
		getManagedBean().setProgramLov(false);
		getManagedBean().setProjectLov(false);
		boolean checkValue=isSelectAllCheckbox();
		TreeNode root=getManagedBean().getChargeLineItemRoot();
		for(TreeNode node:root.getChildren()){
			FlFulfilmentItemAddr addr=(FlFulfilmentItemAddr) node.getData();
			addr.setSelectedChargeItem(checkValue);
		}
		if(checkValue==true){
			getManagedBean().setChargeLinePanel(true);
			getManagedBean().setSelectChargeLineRow(true);
		}else{
			getManagedBean().setSelectChargeLineRow(false);
			getManagedBean().setChargeLinePanel(false);
		}
	}
	
	/**
	 * Check vot fund lov.
	 */
	public void checkVotFundLov(BaseEntity entity){
		getManagedBean().setProgramLov(false);
		getManagedBean().setProjectLov(false);
		getManagedBean().setVotFundDisplay(false);
		getManagedBean().setVotFundCode("");
		getManagedBean().setVotFundId(0L);
		if(getManagedBean().getPmSubSetia()!=null) {
			getManagedBean().getPmSubSetia().setSubSetiaCode("");
			getManagedBean().getPmSubSetia().setSetiaCode("");
			getManagedBean().getPmSubSetia().setProjectCode("");
			getManagedBean().getPmSubSetia().setFinancingType(null);
		}
		getManagedBean().setPmPrgActivity(null);

		if(null != getManagedBean().getPmVoteFund()){
			votFundCodeMap.put(getManagedBean().getPmVoteFund().getVotFundId(), getManagedBean().getPmVoteFund());
		}
	}
	
	/**
	 * Check vot fund dropdown.
	 */
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
		if(getManagedBean().getBackToDocument() !=null){
			return getManagedBean().getBackToDocument();
		}
		else{
			return "flDocSearch";
		}
	}

	/**
	 * get the Request Item Information based on RequestNoteItemId,SupplierId and PriceType.
	 *
	 * @param flItemAddr the fl item addr
	 */
	public void reqItemInfoList(FlFulfilmentItemAddr flItemAddr){
		getManagedBean().setItemName(flItemAddr.getScRequestItem().getItemName());
		flItemAddr.setPriceType(getManagedBean().getPmPriceType());
		getManagedBean().setPriceType(flItemAddr.getPriceType());
		getManagedBean().setStandardUnitPrice(flItemAddr.getUnitPrice().doubleValue());
		getManagedBean().setStandardPrice(false);
		getManagedBean().setZonalPrice(false);
		getManagedBean().setZonalTierPrice(false);
		getManagedBean().setTariffPrice(false);
		getManagedBean().setServiceItem(false);

		IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,	IContractItemSO.class);
		ContractItemReqDTO reqDTO = new ContractItemReqDTO();
		ContractItemResDTO ciItemResDTO = null;
		reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
		reqDTO.setItemId(flItemAddr.getScRequestItem().getRequestNoteItemId());

		if(ParameterConst.ITEM_TYPE_SERVICE.equals(flItemAddr.getItemType().getParameterCode())){
			getManagedBean().setServiceItem(true);
			if(flItemAddr.getScRequestItem().getTplSpecQuestion()!=null) {
				getManagedBean().setUomFrequency(flItemAddr.getScRequestItem().getTplSpecQuestion().getClUomFrequency());
				getManagedBean().setUomPerYear(flItemAddr.getScRequestItem().getTplSpecQuestion().getClUomPerYear());
				getManagedBean().setUomPerMonth(flItemAddr.getScRequestItem().getTplSpecQuestion().getClUomPerMonth());
				getManagedBean().setUomPerDay(flItemAddr.getScRequestItem().getTplSpecQuestion().getClUomPerDay());
			}
			if(ParameterConst.PRICE_TYPE_STANDARD.equals(flItemAddr.getPriceType().getParameterCode())){
				ciItemResDTO = contractItemSO.getPricePerUom(reqDTO);
				getManagedBean().setPricePerUom(ciItemResDTO.getItemPrice());
			}
			getManagedBean().setUomName(flItemAddr.getScRequestItem().getUom() != null ? flItemAddr.getScRequestItem().getUom().getUomName() : "");
		}
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

		ciItemResDTO = contractItemSO.retrieveItemPriceAndZone(reqDTO);
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
	
	private void sortAddress(List<FlDeliveryAddress> flDelAddressList) {
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
	private void sortOrderSummaryList(List<FlDeliveryAddress> deliveryAddressList) {
		//Creating Comparator
		if(null==deliveryAddressList || deliveryAddressList.isEmpty()){
			return;
		}
		for(FlDeliveryAddress flDeliveryAddress : deliveryAddressList) {
			sortItems(flDeliveryAddress.getFlFulfilmentItemAddr());
		}
	}
	// sorting all the items showing in DelAddress and Item Tab
	private void sortItems(List<FlFulfilmentItemAddr> flItemAddressList) {
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
	
	/**
	 * Reset the search.
	 */
	public void searchReset(){
		this.getManagedBean().setSearchItemType(null);
		this.getManagedBean().setSearchItemCode(null);
		this.getManagedBean().setSearchItemName(null);
		getContractItemPagingDataModel().resetData();
	}

	private IPagingDataModel<CtContractItem> contractItemPagingDataModel = new ContractItemPagingDataModel();

	private class ContractItemPagingDataModel extends PagingDataModel<CtContractItem> {
		private static final long serialVersionUID = 1L;

		public ContractItemPagingDataModel() {
			super("itemListResultDt",false);
		}
		@Override
		public ISearchResultBean<CtContractItem> pageChangeListener(ISearchBean<CtContractItem> searchBean) {
			IContractItemSO contractItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);
			ContractItemResDTO resDTO = new ContractItemResDTO();
			ContractItemReqDTO reqDTO = new ContractItemReqDTO();

			searchBean.getDataFilterBeans().add(new SearchFilterBean("ctContractVer.contractVerId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getContractVerId()));
			if(getManagedBean().getSearchItemCode() != null && getManagedBean().getSearchItemCode() != ""){
				searchBean.getDataFilterBeans().add(new SearchFilterBean("scRequestItem.cmItem.extensionCode",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchItemCode(),true));
			}
			if(getManagedBean().getSearchItemName() != null && getManagedBean().getSearchItemName() != ""){
				searchBean.getDataFilterBeans().add(new SearchFilterBean("scRequestItem.itemName",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchItemName(),true));
			}
			if(getManagedBean().getSearchItemType() != null){
				searchBean.getDataFilterBeans().add(new SearchFilterBean("itemType.parameterCode",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchItemType()));
			}

			reqDTO.setContractItemSearchBean(searchBean);
			resDTO = contractItemSO.searchContractItem(reqDTO);

			return resDTO.getContractItemResultBean();

		}

	}
	
	/**
	 * @return the contractItemPagingDataModel
	 */
	public IPagingDataModel<CtContractItem> getContractItemPagingDataModel() {
		return contractItemPagingDataModel;
	}
	
	/**
	 * Search contract item list from paging item data model.
	 */
	public void searchContractItemList(){
		getManagedBean().setItemAutoUpdate(true);
		if((getManagedBean().getSearchItemCode() == null || getManagedBean().getSearchItemCode().isEmpty()) && 
				(getManagedBean().getSearchItemName() == null || getManagedBean().getSearchItemName().isEmpty()) && 
				(getManagedBean().getSearchItemType() == null)){
			throw new EpException(MessageConst.ERROR_100060);
		}else{
			getContractItemPagingDataModel().reloadData();
		}
	}
	
	/**
	 * Get item type values from pmParameter
	 * @return list of dropdown item values.
	 */
	public SelectItem[] getItemTypeValues(){
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		String recommend = ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE;

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(), recommend));

		SelectItem[] items = getDropdownCodeValuesBySearchBean(ParameterTypeConst.SC_ITEM_TYPE, searchBean);
		return items;
	}
			
	private void setFulfilmentItemAddr(FlFulfilmentRequest flRequest) {
		if(getManagedBean().getItemAddrList()==null) {
			IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,	IContractItemSO.class);
			
			getManagedBean().setItemAddrList(new ArrayList<FlFulfilmentItemAddr>());
			getManagedBean().setShowItemAddrList(new ArrayList<FlFulfilmentItemAddr>());
			
			FlFulfilmentItemAddr flItemAddr = null;
			List<FlFulfilmentItemAddr> itemAddrList = new ArrayList<FlFulfilmentItemAddr>();
			ContractItemReqDTO reqDTO = new ContractItemReqDTO();
			ContractItemResDTO resDTO = new ContractItemResDTO();
			reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
			
			for(FlTempItem flTempItem : flRequest.getFlTempItems()) {
				flItemAddr = new FlFulfilmentItemAddr();
				flItemAddr.setPriceType(getManagedBean().getPmPriceType());
				flItemAddr.setItemType(flTempItem.getScRequestItem().getItemType());
				flItemAddr.setScRequestItem(flTempItem.getScRequestItem());
				flItemAddr.setCmItem(flTempItem.getScRequestItem().getCmItem());
				if(flTempItem.getScRequestItem().getCmItem()!=null) {
					flItemAddr.setItemCode(flTempItem.getScRequestItem().getCmItem().getExtensionCode());
				} else {
					flItemAddr.setItemCode(flTempItem.getScRequestItem().getTempUnspscCode());
				}				
				if(getManagedBean().getPmPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_STANDARD)) {
					if(getManagedBean().getFlRequest().getStartDate()!=null && getManagedBean().getFlRequest().getEndDate()!=null) {
						long day = (getManagedBean().getFlRequest().getEndDate().getTime() - getManagedBean().getFlRequest().getStartDate().getTime()) / MILLIS_PER_DAY;
						reqDTO.setDeliveryTerm(Long.valueOf(day).intValue());
					}
					reqDTO.setItemId(flTempItem.getScRequestItem().getRequestNoteItemId());
					reqDTO.setOrderedQty(BigDecimal.ZERO);
					resDTO = contractItemSO.getItemPrice(reqDTO);
					flItemAddr.setUnitPrice(resDTO.getItemPrice());
					setGstPrice(flItemAddr, resDTO.getItemPrice().doubleValue());
				} else {
					flItemAddr.setUnitPrice(BigDecimal.ZERO);
					flItemAddr.setGstAmount(BigDecimal.ZERO);
					flItemAddr.setGstUnitPrice(BigDecimal.ZERO);
				}
				flItemAddr.setOrderedQty(BigDecimal.ZERO);
				flItemAddr.setOrderedAmt(BigDecimal.ZERO);
				itemAddrList.add(flItemAddr);
			}
			if(itemAddrList.size()>0) {
				sortItems(itemAddrList);
				getManagedBean().getItemAddrList().addAll(itemAddrList);
				getManagedBean().getShowItemAddrList().addAll(itemAddrList);
				if(getManagedBean().getSelectedDelAddr()!=null) {
					populateItemsForSelectedAddress();
				}
			}
		}
	}

	/**
	 * Not remove address row.
	 */
	public void notRemoveAddressRow(){
		getManagedBean().setDeleteButtonHide(true);
		if(null !=getManagedBean().getFlRequest().getFlDeliveryAddresses()){
			Iterator<FlDeliveryAddress> delAddressIterator=getManagedBean().getFlRequest().getFlDeliveryAddresses().iterator();
			while(delAddressIterator.hasNext()){
				FlDeliveryAddress  deliveryAddress=delAddressIterator.next();
				deliveryAddress.setSelectedAddressRowDelete(false);
			}
		}
	}
	
	private void resetSelectedTreeNodes() {
		TreeNode root=getManagedBean().getChargeLineItemRoot();
		for(TreeNode node:root.getChildren()){
			FlFulfilmentItemAddr addr=(FlFulfilmentItemAddr) node.getData();
			addr.setSelectedChargeItem(false);
		}
	}
	
	/**
	 * Pre-render contract list
	 */
	public void preRenderContractList() {
		getManagedBean().getActiveContractDataModel().resetData();
		FacesContext fc=FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "crList?faces-redirect=true");
	}
	
	private void checkMinimumQtyItem(FlFulfilmentRequest flRequest){
		Map<Long, BigDecimal> quantityMap = new HashMap<Long, BigDecimal>();
		quantityMap.clear();
		for(FlDeliveryAddress deliveryAddress : flRequest.getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddr : deliveryAddress.getFlFulfilmentItemAddr()){
				Long key = itemAddr.getScRequestItem().getRequestNoteItemId();
				
				if(!quantityMap.containsKey(key)){
					quantityMap.put(itemAddr.getScRequestItem().getRequestNoteItemId(),itemAddr.getOrderedQty());
				}else{
					BigDecimal sumOrderedQty = quantityMap.get(key);
					BigDecimal totalOrderQty = sumOrderedQty.add(itemAddr.getOrderedQty());
					quantityMap.put(itemAddr.getScRequestItem().getRequestNoteItemId(),totalOrderQty);
				}
			}
		}
		for(CtContractItem ctContractItem : flRequest.getCtContract().getLatestContractVer().getCtContractItems()){
			if(ctContractItem.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)){
				if(quantityMap.containsKey(ctContractItem.getScRequestItem().getRequestNoteItemId())){
					BigDecimal flOrederedQty =  quantityMap.get(ctContractItem.getScRequestItem().getRequestNoteItemId());
					if(flOrederedQty.doubleValue() >  ctContractItem.getMinimumOrderQty()){
						throw new EpException(ctContractItem.getScRequestItem().getItemName() +" (" + flOrederedQty.doubleValue() + ")" + " is more than maximum quantity " +ctContractItem.getMinimumOrderQty());
					}
				}
			}else{
				if(quantityMap.containsKey(ctContractItem.getScRequestItem().getRequestNoteItemId())){
					BigDecimal flOrederedQty =  quantityMap.get(ctContractItem.getScRequestItem().getRequestNoteItemId());
					if(flOrederedQty.doubleValue() <  ctContractItem.getMinimumOrderQty()){
						throw new EpException(ctContractItem.getScRequestItem().getItemName() +" (" + flOrederedQty.doubleValue() + ")" + " is lesser than minimum quantity " +ctContractItem.getMinimumOrderQty());
					}
				}
			}
			
		}
	}
	
	/**
	 * Get selected factoring company details from profile management service object
	 * @param financialOrgId
	 */
	public void getSelectedFactoringCompDtl(Long financialOrgId){
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
		getManagedBean().setFacSupplierAddress(null);
		getManagedBean().setPhoneNo(null);
		getManagedBean().setFaxNo(null);
		getManagedBean().setContactPerson(null);
		getManagedBean().setContactEmail(null);
		getManagedBean().setContactPhoneNo(null);
		
		if(financialOrgId!=null) {
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialOrgId(financialOrgId);
			reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
			
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if(!resDTO.getAddressTypeList().isEmpty()){
				getManagedBean().setFacSupplierAddress(getFullAddress(resDTO.getAddressTypeList().get(0).getPmAddress()));
				getManagedBean().setPhoneNo(resDTO.getAddressTypeList().get(0).getPmAddress().getPhoneNo());
				getManagedBean().setFaxNo(resDTO.getAddressTypeList().get(0).getPmAddress().getFaxNo());
				if(!resDTO.getAddressTypeList().get(0).getPmContacts().isEmpty() ){
					PmContact pmContact = resDTO.getAddressTypeList().get(0).getPmContacts().get(0);
					getManagedBean().setContactPerson(pmContact.getContactName());
					getManagedBean().setContactEmail(pmContact.getEmail());
					getManagedBean().setContactPhoneNo(pmContact.getPhoneNo());
				}
			}
		}
	}
	
	/**
	 * Gpki sign action.
	 *
	 * @param outcome the outcome
	 * @return the string
	 * @throws Exception the exception
	 */
	public String gpkiSignAction(String outcome) throws Exception{
		if (!isRequisitioner() &&  (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome))
				|| (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome))) {
			validateContractRequest(outcome);
			// Temp setTextBeforeSign : DocNo+Type
			StringBuilder sb = new StringBuilder();
			sb.append(getManagedBean().getFlRequest().getCtContract().getContractNo());
			sb.append(outcome);
			getManagedBean().setTextBeforeSign(sb.toString());
			// set action
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		}else{
			if(BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome)){
				getManagedBean().setValidated(Boolean.FALSE);
			}
			return submitAction(outcome);
		}
	}
	
	/**
	 * Pre submit fulfillment request.
	 *
	 * @param pmDigiSign the pm digi sign
	 * @return the string
	 * @throws Exception the exception
	 */
	public String preSubmitFulfillmentRequest(
			PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);
		}
		getManagedBean().setValidated(Boolean.TRUE);
		return submitAction(getManagedBean().getButtonAction());
	}
	
	/**
	 * Enable auto update.
	 */
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}
	
	/**
	 * Get preparef PTJ condition for construct the LOV listing
	 * @return String
	 */
	public String getPreparedPTJCondition() {
		String condition = OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value())+"^"+
				retrieveMinistryOrgProfile(getManagedBean().getFlRequest().getPreparedOrgProfile().getOrgProfileId()).getOrgProfileId();
		if(getManagedBean().getFlRequest().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
			condition = condition + "|orgProfileId^"+getManagedBean().getOrgProfileIds()+"^9";
		}
		return condition;
	}
	
	private void checkContractValidity(FlFulfilmentRequest flRequest) {
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		boolean haveFlRecord = false;
		
		IFulfilmentRequestSO fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
		
		if(flRequest.getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF)){
			reqDTO.setOrgProfileId(flRequest.getPreparedOrgProfile().getOrgProfileId());
			reqDTO.setContractId(flRequest.getCtContract().getContractId());
			if(flRequest.getFulfilmentReqId() == null){
				haveFlRecord = fulfilmentRequestSO.isContractRequestInUsed(reqDTO);
			}
			if(haveFlRecord){
				throw new EpException(MessageConst.ERROR_740030);
			}
		}
		
		if(flRequest.getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)){
			ContractIntegrationReqDTO integrationReqDTO = new ContractIntegrationReqDTO();
			integrationReqDTO.setContractVerId(flRequest.getCtContract().getLatestContractVer().getContractVerId());
			integrationReqDTO.setScheduleNo(flRequest.getScheduleNo());
			IContractIntegrationSO contractIntegrationSO = (IContractIntegrationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractIntegrationSO.class);
			ContractIntegrationResDTO integrationResDTO = contractIntegrationSO.retrieveScheduleByContractVerIdAndNo(integrationReqDTO);
			if(integrationResDTO.getCtSchedule() != null){
				if(integrationResDTO.getCtSchedule().getScheduleType().getParameterCode().equalsIgnoreCase(ParameterConst.CT_SCHEDULE_PERIODIC)){
					reqDTO.setOrgProfileId(flRequest.getPreparedOrgProfile().getOrgProfileId());
					reqDTO.setScheduleNo(flRequest.getScheduleNo());
					reqDTO.setContractId(flRequest.getCtContract().getContractId());
					if(flRequest.getFulfilmentReqId() == null){
						haveFlRecord = fulfilmentRequestSO.isContractRequestInUsed(reqDTO);
					}
					if(haveFlRecord){
						throw new EpException(MessageConst.ERROR_740031, new String[]{ResourceBundleUtils.getMessage("lbl.ct.schedule.no",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+" "+flRequest.getScheduleNo()});
					}
				}
			}else{//progressive payment
				reqDTO.setOrgProfileId(flRequest.getPreparedOrgProfile().getOrgProfileId());
				reqDTO.setScheduleNo(flRequest.getScheduleNo());
				reqDTO.setContractId(flRequest.getCtContract().getContractId());
				if(flRequest.getFulfilmentReqId() == null){
					haveFlRecord = fulfilmentRequestSO.isContractRequestInUsed(reqDTO);
				}
				if(haveFlRecord == true){
					throw new EpException(MessageConst.ERROR_740031, new String[]{ResourceBundleUtils.getMessage("lbl.ct.phase",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())+" "+flRequest.getScheduleNo()});
				}
			}
		}
	}
	
	private void validateContractRequest(String action){
		IFulfilmentRequestSO fulfilmentRequestSO = null;
		IUserSO userSO =null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)){
				if(getManagedBean().isPidValidityExpired()) {
					if(isRequisitioner()) {
						throw new EpException(MessageConst.ERROR_740040);
					} else {
						throw new EpException(MessageConst.ERROR_740041);
					}
				}
			}
			FlFulfilmentRequest flRequest = getManagedBean().getFlRequest();
			getManagedBean().setAutoUpdate(Boolean.TRUE);
			validatePreparedOrgAndAgencies();
			checkContractValidity(getManagedBean().getFlRequest());
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)){
				if(getManagedBean().getFlRequest().getFlDeliveryAddresses().size()==0) {
					throw new EpException(MessageConst.ERROR_100023, new String[]{ResourceBundleUtils.getMessage("lbl.fl.delivery.address",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
				}else{
					checkReceivingOfficerList();
				}
				validateOrderQuantity();
				if(getManagedBean().getFlRequest().getScPurchaseInDefault() != null){
					checkPidMinQtyItem(getManagedBean().getFlRequest());
				}else{
					checkMinimumQtyItem(getManagedBean().getFlRequest());
				}
				
				if(flRequest.getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
					checkContractAmount(flRequest);
					checkCeilingAmount(flRequest);
					checkCeilingQty(flRequest);
					checkMaximumQty(flRequest);
				}else if(flRequest.getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)){
					if(getManagedBean().getFlRequest().getScPurchaseInDefault() == null){
						checkCircularItemMinQtyAndOrderAmt(flRequest);
					}
					if(flRequest.getCtContract().getLatestContractVer().getCtContractValue().isFollowLoaAmt()){
						checkContractAmount(flRequest);
					}
				}
				
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					if(getManagedBean().getFlRequest().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
						IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
						ContractReqDTO contractReqDTO = new ContractReqDTO();
						contractReqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
						if(!contractSO.checkContractStatus(contractReqDTO).isActive()) {
							throw new EpException(MessageConst.ERROR_740034, new String[]{getManagedBean().getFlRequest().getCtContract().getContractNo()});
						}
					} else {
						Date expDate = getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getExpDate();
						int days = getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtSetting().getLastCOBeforeExpiryDays();
						Calendar today = Calendar.getInstance();
						today.setTime(DateUtils.getCurrentDateWithoutTime());
						today.add(Calendar.DATE, days);
						if(today.getTime().after(expDate)) {
							throw new EpException(MessageConst.ERROR_740034, new String[]{getManagedBean().getFlRequest().getCtContract().getContractNo()});
						}
					}
					// Unable to approve CR if no service tax flag is identified in Contract Management
					if(getManagedBean().getFlRequest().getGstRegNo()!=null && getManagedBean().getFlRequest().getGstEffectiveDate().compareTo(DateUtils.getCurrentDateWithoutTime())>=0) {
						// Only applicable for Procurement Type category=Service & LOA signed before 1/4/2015
						Date gstEffDate = new Date();
						try {
							gstEffDate = DateUtils.convertStringToDate("dd-MM-yyyy", "01-04-2015");
						} catch (Exception e) {
							log.error(e);
						}
						if(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getProcurementTypeCategory().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_TYPE_CODE_SERVICE) 
							&& getManagedBean().getFlRequest().getCtContract().getLoa().getAckDate().compareTo(gstEffDate)<=0 
							&& getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractValue().getServiceTaxValue()==null) {
							throw new EpException(MessageConst.ERROR_740015);
						}
					}
				} else {
					if(getManagedBean().getSelectedApprover() == null){
						throw new EpException(MessageConst.ERROR_750010);
					}
				}
				if(!getManagedBean().isDeclarationCheck()){
					getManagedBean().setSelectedTabIndex(0);
					throw new EpException(MessageConst.ERROR_745071);
				}
			} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)){
				if(flBB.getManagedBean().getRemarkList().get(0).getRemark()==null || flBB.getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
					throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.txt.remarks",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
				}
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)){
					userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
					UserReqDTO userReqDTO = new UserReqDTO();
					userReqDTO.setUserId(getManagedBean().getFlRequest().getCreatedBy());
					UserResDTO userResDTO = userSO.retrieveUserByUserId(userReqDTO);
					if(userResDTO.getPmUser().getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE){
						throw new EpException(MessageConst.ERROR_740020, new String[]{ResourceBundleUtils.getMessage("lbl.fl.requisitioner",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
					}
				}
			}
			
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			
			if(isRequisitioner()) {
				reqDTO.setRequisitioner(Boolean.TRUE);
			}
			flRequest.setFinancialYear(Calendar.getInstance().get(Calendar.YEAR));
			
			// reset flTempItem
			flRequest.setFlTempItems(new ArrayList<FlTempItem>());
			if(getManagedBean().getItemAddrList()!=null && getManagedBean().getItemAddrList().size()>0) {
				FlTempItem flTempItem = null;
				for(FlFulfilmentItemAddr itemAddr : getManagedBean().getItemAddrList()) {
					flTempItem = new FlTempItem();
					flTempItem.setFlRequest(flRequest);
					flTempItem.setScRequestItem(itemAddr.getScRequestItem());
					flRequest.getFlTempItems().add(flTempItem);
				}
			}
			
			
			if(isRequisitioner()){
				UserReqDTO userReqDTO = new UserReqDTO();
				userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
				UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
				flRequest.setCreatedOrgProfile(userResDTO.getUserOrgList().get(0).getPmOrgProfile());
			}
			if(flRequest.getTitle() == null || flRequest.getTitle().trim().length() < 1){
				flRequest.setTitle(FulfilmentConst.CONTRACT_REQUEST);
			}
			
			reqDTO.setFlFulfilmentRequest(flRequest);
			reqDTO.setContractRequest(Boolean.TRUE);
			reqDTO.setOutcome(action);
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setApproverMap(getManagedBean().getApproverMap());
			reqDTO.setSelectedApprover(getManagedBean().getSelectedApprover());
			reqDTO.setGroupId(getManagedBean().getFlRequest().getPmUserGroup().getUserGroupId());
			reqDTO.setApproverLimit(calculatesTotalOrderedAmt());
			reqDTO.setPmOrgProfile(getManagedBean().getFlRequest().getPreparedOrgProfile());
			fulfilmentRequestSO.validateFulfilmentRequest(reqDTO);		
		}finally{
			fulfilmentRequestSO = null;
		}
	}
	
	private void checkMofAccountExpDate(FlFulfilmentRequest flRequest) {
		IMofAccountSO mofAccountSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofAccountSO.class);
		MofAccountReqDTO reqDTO = new MofAccountReqDTO();
		reqDTO.setSupplierId(flRequest.getSmSupplier().getSupplierId());
		reqDTO.setSupplierType(flRequest.getSmSupplier().getSmAppl().getSupplierType());
		MofAccountResDTO resDTO = mofAccountSO.retriveMOFAccountBySupplierIdAndType(reqDTO);
		
		if(DateUtils.getCurrentDate().before(resDTO.getMofAccount().getEffDate())){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
					MessageUtils.getMessage(MessageConst.INFO_745012, null, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
		}
		if(DateUtils.getCurrentDate().after(resDTO.getMofAccount().getExpDate())){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
					MessageUtils.getMessage(MessageConst.INFO_745012, null, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
		}
	}
	
	private void checkCircularItemMinQtyAndOrderAmt(FlFulfilmentRequest flRequest) {
		ICircularDetailSO circularDetailSO = (ICircularDetailSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, ICircularDetailSO.class);
		CircularDetailReqDTO reqDTO = new CircularDetailReqDTO();
		reqDTO.setContractId(flRequest.getCtContract().getContractId());
		
		Map<Long, List<String>> itemsMap = new HashMap<Long, List<String>>();
		List<String> listOfValues = null;
		for(FlDeliveryAddress delAddr : flRequest.getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddr : delAddr.getFlFulfilmentItemAddr()){
				Long key = itemAddr.getScRequestItem().getRequestNoteItemId();
				if(itemsMap.containsKey(key)){
					listOfValues = itemsMap.get(key);
					listOfValues.set(0, new BigDecimal(listOfValues.get(0)).add(itemAddr.getOrderedQty()).toString());
					listOfValues.set(1, new BigDecimal(listOfValues.get(1)).add(itemAddr.getOrderedAmt()).toString());
					listOfValues.add(2,itemAddr.getScRequestItem().getItemName());
					
				}else{
					listOfValues = new ArrayList<String>();
					listOfValues.add(0,itemAddr.getOrderedQty().toString());
					listOfValues.add(1,itemAddr.getOrderedAmt().toString());
					listOfValues.add(2,itemAddr.getScRequestItem().getItemName());
					itemsMap.put(key, listOfValues);
				}
			}
		}
		
		for(Entry<Long, List<String>> entryQty : itemsMap.entrySet()){
			reqDTO.setRequestItemId(entryQty.getKey());
			CircularDetailResDTO resDTO = circularDetailSO.getCircularItemMinQtyAndOrderAmt(reqDTO);
			
			if(new Double(entryQty.getValue().get(0)) < resDTO.getMinimumOrderQty().doubleValue()){
				throw new EpException(MessageConst.ERROR_745075, new String[]{entryQty.getValue().get(0).toString(),entryQty.getValue().get(2),resDTO.getMinimumOrderQty().toString()});
			}
			if(new Double(entryQty.getValue().get(1)) < resDTO.getMinimumOrderAmt().doubleValue()){
				throw new EpException(MessageConst.ERROR_745076, new String[]{entryQty.getValue().get(1).toString(),entryQty.getValue().get(2),resDTO.getMinimumOrderAmt().toString()});
			}
		}
	}

	private void checkPidMinQtyItem(FlFulfilmentRequest flRequest) {
		BigDecimal sumOrderQty = BigDecimal.ZERO;
		for(FlDeliveryAddress deliveryAddress : flRequest.getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddr : deliveryAddress.getFlFulfilmentItemAddr()){
				sumOrderQty = sumOrderQty.add(itemAddr.getOrderedQty());
			}
		}
		if(flRequest.getScPurchaseInDefault().getQuantity().doubleValue() < sumOrderQty.doubleValue()){
			throw new EpException(MessageConst.ERROR_745074, new String[]{flRequest.getScPurchaseInDefault().getContractedItem().getScRequestItem().getItemName(),Double.toString(flRequest.getScPurchaseInDefault().getQuantity().doubleValue())});
		}
		if(flRequest.getScPurchaseInDefault().getQuantity().doubleValue() > sumOrderQty.doubleValue()){
			throw new EpException(MessageConst.ERROR_745074, new String[]{flRequest.getScPurchaseInDefault().getContractedItem().getScRequestItem().getItemName(),Double.toString(flRequest.getScPurchaseInDefault().getQuantity().doubleValue())});
		}
		
	}

	private void chargeLineValidationBeforeApproverList(){
		if(getManagedBean().getChargeLineItemRoot()!=null) {
			FlFulfilmentItemAddr itemAddress = null;
			updateSelectedTreeNodes();
			TreeNode[] selectedNodesCount=getManagedBean().getSelectedNodes();
			if(null!=selectedNodesCount && selectedNodesCount.length>=1){
				for(TreeNode selectedNode : getManagedBean().getSelectedNodes()){
					itemAddress = (FlFulfilmentItemAddr)selectedNode.getData();
					itemAddress.getCoaValue();
					if("N".equalsIgnoreCase(itemAddress.getCoaValue())){
						throw new EpException(MessageConst.ERROR_745063);
					}
				}
			}
		}
	}
	
	private class UnitOfMeasurementPagingDataModel extends PagingDataModel<PmUom> {
		private static final long serialVersionUID = 1L;

		public UnitOfMeasurementPagingDataModel() {
			super("measurementSR");
		}

		@Override
		public ISearchResultBean<PmUom> pageChangeListener(ISearchBean<PmUom> searchBean) {
			try {	
				ISearchBean<PmUom> searchBeanData = getMeasurementSearchBeanData(searchBean);
				UomReqDTO reqDTO = new UomReqDTO();
				reqDTO.setMeasurementSearchBean(searchBeanData);				
				IUomSO reqSO = (IUomSO) BusinessDelegate.getInstance().getServiceObject("pm", IUomSO.class);	
				UomResDTO resDTO = reqSO.retrieveMeasurementBySearch(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getMeasurementSearchResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}
	
	private ISearchBean<PmUom> getMeasurementSearchBeanData(ISearchBean<PmUom> searchBean){
		
		if (getManagedBean().getMeasurementCodes() != null
				&& !"".equalsIgnoreCase(getManagedBean().getMeasurementCodes())) {
			SearchFilterBean uomSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			uomSearchBean
					.setSearchField("uomCode");
			uomSearchBean
					.setCaseInsensitive(true);
			uomSearchBean.setSearchValue(getManagedBean()
					.getMeasurementCodes());
			searchBean.getDataFilterBeans().add(uomSearchBean);
		}

		if (getManagedBean().getMeasurementNames() != null
				&& !"".equalsIgnoreCase(getManagedBean().getMeasurementNames())) {
			SearchFilterBean uomSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			uomSearchBean
					.setSearchField("uomName");
			uomSearchBean
					.setCaseInsensitive(true);
			uomSearchBean.setSearchValue(getManagedBean()
					.getMeasurementNames());
			searchBean.getDataFilterBeans().add(uomSearchBean);
		}
		return searchBean;
	}

	public IPagingDataModel<PmUom> getUnitOfMeasurementDataModel() {
		return unitOfMeasurementDataModel;
	}

	public void setUnitOfMeasurementDataModel(
			IPagingDataModel<PmUom> unitOfMeasurementDataModel) {
		this.unitOfMeasurementDataModel = unitOfMeasurementDataModel;
	}	
	
	/**
	 * Retrieve measurement.
	 */
	public void retrieveMeasurement(){		
		unitOfMeasurementDataModel.reloadData();		
	}
	
	/**
	 * Reset measurement search.
	 */
	public void resetMeasurementSearch() {		
		this.getManagedBean().setMeasurementCodes(null);
		this.getManagedBean().setMeasurementNames(null);
		getUnitOfMeasurementDataModel().resetData();
		unitOfMeasurementDataModel.reloadData();		
	}

	/**
	 * @return the selectAllCheckbox
	 */
	public boolean isSelectAllCheckbox() {
		return selectAllCheckbox;
	}

	/**
	 * @param selectAllCheckbox the selectAllCheckbox to set
	 */
	public void setSelectAllCheckbox(boolean selectAllCheckbox) {
		this.selectAllCheckbox = selectAllCheckbox;
	}
	
	/**
	 * Gets the item max quantity.
	 *
	 * @param uom the uom
	 * @return the item max quantity
	 */
	public Double getItemMaxQuantity(PmUom uom){
		Double maxQuantity =  Double.valueOf(9999999999L);
		if(uom.getDecimalScale() == 1){
			maxQuantity = 9999999999.9;
		}else if(uom.getDecimalScale() == 2){
			maxQuantity = 9999999999.99;
		}else if(uom.getDecimalScale() == 3){
			maxQuantity = 9999999999.999;
		}	
		return maxQuantity;
	}
	
	/**
	 * Navigate to contract view page.
	 * @param docId
	 * @return String gotoCommonViewPage
	 */
	public String navigateDocument(Long docId){
		ContractDetailsBackingBean ctDetBB = new ContractDetailsBackingBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		ctDetBB.getManagedBean().setGotoPreviewPage(fc.getViewRoot().getViewId());
		return ctDetBB.gotoCommonViewPage(docId);
	}
	
	/**
	 * back to document
	 * @return String backToDocument
	 */
	public String backToDocument() {
		return getManagedBean().getBackToDocument();
	}
	
	/**
	 * The method used to retrieve the delivery address type.
	 * 
	 * @return delivery address type
     */
	public String getDeliveryAddressType() {
		return AddressType.DELIVERY_ADDRESS.value();
	}
	
	/**
	 * Show 1GFMAS error
	 *
	 */
	public void show1GFMASError(){
		getManagedBean().setAutoUpdate(Boolean.FALSE);
		RequestContext.getCurrentInstance().update("@form");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,null,getManagedBean().getGfmasErrorContent()));
	}
	
	/**
	 * Compare PTJ group
	 * if PTJ group is not equals to current PTJ group , FulfilmenItemAddress chargeline will be reset
	 */
	public void comparePtjGroup(BaseEntity entity){
		IOrgProfileSO profileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(getManagedBean().getFlRequest().getChargeOrgProfile().getOrgProfileId());
		OrgProfileResDTO resDTO = profileSO.retrieveJabatanByOrgProfileId(reqDTO);
		Long newKumpOrgId = 0L;//TODO remove after data are all pacthed
		if(resDTO.getPmOrgProfile() != null){
			newKumpOrgId = resDTO.getPmOrgProfile().getOrgProfileId();
		}
		if(newKumpOrgId != getManagedBean().getCurrentChargePtjGroup().getOrgProfileId()){
			if(getManagedBean().getFlRequest().getFlDeliveryAddresses() != null && !getManagedBean().getFlRequest().getFlDeliveryAddresses().isEmpty()){
				for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()){
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
		if(resDTO.getPmOrgProfile() != null){
			getManagedBean().setCurrentChargePtjGroup(resDTO.getPmOrgProfile());
		}
	}
	
	private BigDecimal calculatesTotalOrderedAmt(){
		BigDecimal totalAmt = new BigDecimal(0);
		if(getManagedBean().getFlRequest().getFlDeliveryAddresses() != null){
			for(FlDeliveryAddress  deliveryAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()){
				for(FlFulfilmentItemAddr itemAddress : deliveryAddress.getFlFulfilmentItemAddr()){
					totalAmt = totalAmt.add(itemAddress.getOrderedAmt());
				}
			}
		}
		
		return totalAmt;
	}
	
	private void getApproverList(){
		if(getManagedBean().getFlRequest().getPmUserGroup()==null || getManagedBean().getFlRequest().getPmUserGroup().getUserGroupId()==null) {
			getManagedBean().getApproverList().clear();
			return;
		}
		IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		reqDTO.setGroupId(getManagedBean().getFlRequest().getPmUserGroup().getUserGroupId());
		reqDTO.setApproverLimit(calculatesTotalOrderedAmt());
		try {
			resDTO = requestSO.retrieveApproverListByGroupId(reqDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
        getManagedBean().getApproverList().clear();
        getManagedBean().getApproverGroupUserList().clear();
        getManagedBean().getApproverGroupUserList().addAll(resDTO.getApproverGroupUserList());
		for(PmUserGroupUser groupUser : resDTO.getApproverGroupUserList()) {
			getManagedBean().getApproverList().add(groupUser.getPmUser().getUserId());
		}
	}
	
	private void setGstPrice(FlFulfilmentItemAddr flItemAddr,Double itemPrice) {
		if(getManagedBean().getFlRequest().getGstRate() == null){
			getManagedBean().getFlRequest().setGstRate(BigDecimal.ZERO);
		}
		if(flItemAddr.isGst() && getManagedBean().getFlRequest().getGstEffectiveDate()!=null && 
			getManagedBean().getFlRequest().getGstEffectiveDate().getTime() <= DateUtils.getCurrentDateWithoutTime().getTime()){
			flItemAddr.setGstAmount(new BigDecimal(itemPrice).multiply(getManagedBean().getFlRequest().getGstRate()).divide(new BigDecimal(100), CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP));
		} else {
			flItemAddr.setGstAmount(BigDecimal.ZERO);
		}
		flItemAddr.setGstUnitPrice(flItemAddr.getUnitPrice().add(flItemAddr.getGstAmount()));
	}
	
	private void updateGstRate(FlFulfilmentRequest flRequest) {
		BigDecimal gstRate = getGstRate();
		if(flRequest.getGstRate()!=null && flRequest.getGstRate().compareTo(gstRate)!=0) {
			getManagedBean().setGstRateChange(true);
		}
		flRequest.setGstRate(gstRate);
	}	
	
	/**
	 * set false item row select check box
	 */
	public void itemRowSelectCheckbox(AjaxBehaviorEvent e){
		getManagedBean().setItemDeleteButtonShow(false);
	}
	
	/**
	 * set true item row select check box
	 */
	public void itemRowUnselectCheckbox(AjaxBehaviorEvent e){
		getManagedBean().setItemDeleteButtonShow(true);
	}
	
	/**
	 * set item delete button show to true or false
	 */
	public void itemToggleSelect(AjaxBehaviorEvent e){
		if(null !=getManagedBean().getShowItemAddrList() && getManagedBean().getShowItemAddrList().size()>0){
			if(null !=getManagedBean().getSelectedItemsRows() && getManagedBean().getSelectedItemsRows().size()>0){
				getManagedBean().setItemDeleteButtonShow(false);
			}else{
				getManagedBean().setItemDeleteButtonShow(true);
			}
		}else{
			getManagedBean().setItemDeleteButtonShow(true);
			getManagedBean().getSelectedItemsRows();
		}
	}
	
	/**
	 * if is purchase in default or contract fulfilment type is one off then item add button is true else false.
	 */
	public void hideItemAddButtons(){
		if(getManagedBean().getFlRequest().getScPurchaseInDefault() == null){
			getManagedBean().setItemAddButtonShow(true);
		}
		if(getManagedBean().getFlRequest().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF)){
			getManagedBean().setItemAddButtonShow(false);
		}
	}
	
	private void checkReceivingOfficerList(){
		IAddressSO addressSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
		AddressReqDTO reqDTO = new AddressReqDTO();
		for(FlDeliveryAddress delAddr : getManagedBean().getFlRequest().getFlDeliveryAddresses()){
			Long addressId = delAddr.getPmAddress().getAddressId();
			if(delAddr.getReceivingGrp()!=null && delAddr.getReceivingGrp().getAddressTypeId()!=null) { // in case of Manual address
				reqDTO.setAddressType(AddressType.DELIVERY_ADDRESS.value());
				reqDTO.setAddressTypeId(delAddr.getReceivingGrp().getAddressTypeId());
				AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
				addressId = resDTO.getAddressTypeList().get(0).getPmAddress().getAddressId();
			}
			
			reqDTO.setAddressId(addressId);
			reqDTO.setAddressType(AddressType.RECEIVING_OFFICER_ADDRESS.value());
			AddressResDTO resDTO = addressSO.retrieveReceivingOfficerList(reqDTO);
			if(resDTO.getAddressTypeList().isEmpty()){//TODO others as well needed to throw if list is empty
				throw new EpException(MessageConst.ERROR_745077, new String[]{getFullAddress(delAddr.getPmAddress())});
			}
		}
		
	}
	
	/**
	 * Retrieve a list of approver
	 *
	 */
	public SelectItem[] getApproverListDropDown() {
		SelectItem[] items = null;
		if(getManagedBean().getFlRequest().getPmUserGroup()==null) {
			getManagedBean().getFlRequest().setPmUserGroup(new PmUserGroup());
		}	
		if(getManagedBean().getFlRequest().getPmUserGroup().getUserGroupId() == null){
			return items;
		}
		IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		reqDTO.setGroupId(getManagedBean().getFlRequest().getPmUserGroup().getUserGroupId());
		reqDTO.setApproverLimit(calculatesTotalOrderedAmt());
		try {
			resDTO = requestSO.retrieveApproverListByGroupId(reqDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
        getManagedBean().getApproverList().clear();
		items = new SelectItem[resDTO.getApproverGroupUserList().size() + 1];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				getFacesScopes().getUserLocale()));
		
		int i = 1;
		for(PmUserGroupUser groupUser : resDTO.getApproverGroupUserList()) {
			items[i++] = new SelectItem(groupUser.getPmUser().getUserId(), groupUser.getPmUser().getUserName());
			getManagedBean().getApproverList().add(groupUser.getPmUser().getUserId());
		}
		return items;
	}
	
	private void initialize(){
		
		if (getManagedBean().getFlRequest() == null) {
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
		
		if(null == getManagedBean().getFlManulaAddress()){
			getManagedBean().setFlManulaAddress(new FlDeliveryAddress());
			getManagedBean().getFlManulaAddress().setDeliveryAddressId(1L);
			if(null == getManagedBean().getFlManulaAddress().getAddressType()){
				 getManagedBean().getFlManulaAddress().setAddressType(new PmAddressType());
			}
			getManagedBean().getFlManulaAddress().getAddressType().setPmAddress(new PmAddress());
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
			getManagedBean().getFlManulaAddress().setReceivingGrp(new PmAddressType());
		}
		if(null == getManagedBean().getFlManulaAddress().getPmAddress()){
			 getManagedBean().getFlManulaAddress().setPmAddress(new PmAddress());
		}
		if(null == getManagedBean().getFlManulaAddress().getAddressType().getPmContacts() || getManagedBean().getFlManulaAddress().getAddressType().getPmContacts().size()==0){
			getManagedBean().getFlManulaAddress().getAddressType().setPmContacts(new ArrayList<PmContact>());
			getManagedBean().getFlManulaAddress().getAddressType().getPmContacts().add(new PmContact());
		}
		if(null == getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmCountry()){
			getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setPmCountry(new PmCountry());
			getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().getPmCountry().setCountryId(1L);
		}
		if (isRequisitioner()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setRequisitioner(facesScopes.getUserSessionBean().getLoginId());
		}
		if (isApprover()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setApprover(facesScopes.getUserSessionBean().getLoginId());
		}
		if(getManagedBean().getFlRequest().getPreparedOrgProfile()!=null && getManagedBean().getFlRequest().getPreparedOrgProfile().getPmOrgValidity() == null){
			getManagedBean().getFlRequest().getPreparedOrgProfile().setPmOrgValidity(new PmOrgValidity());
			
		}
		if(getManagedBean().getFlRequest().getIssuedOrgProfile()!=null && getManagedBean().getFlRequest().getIssuedOrgProfile().getPmOrgValidity() == null){
			getManagedBean().getFlRequest().getIssuedOrgProfile().setPmOrgValidity(new PmOrgValidity());
			
		}
		if(getManagedBean().getFlRequest().getChargeOrgProfile()!=null && getManagedBean().getFlRequest().getChargeOrgProfile().getPmOrgValidity() == null){
			getManagedBean().getFlRequest().getChargeOrgProfile().setPmOrgValidity(new PmOrgValidity());
			
		}
		getManagedBean().setDeleteButtonHide(true);
		getManagedBean().setItemDeleteButtonShow(true);
		
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
}