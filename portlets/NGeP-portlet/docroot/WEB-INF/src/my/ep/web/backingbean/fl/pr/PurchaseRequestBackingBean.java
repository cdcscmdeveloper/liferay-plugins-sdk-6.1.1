/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.pr;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.bean.application.NgepApplicationBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.cm.catalogue.IViewSupplierItemSO;
import my.ep.biz.service.cm.codification.ICodificationSO;
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
import my.ep.biz.service.pm.common.glaccount.IGlAccountSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.projectsetia.IProjectSetiaSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseRequestSO;
import my.ep.biz.service.sc.directpurchase.IRequestNoteSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.fl.FulfilmentConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
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
import my.ep.dto.pm.common.glaccount.GlAccountReqDTO;
import my.ep.dto.pm.common.glaccount.GlAccountResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.uom.UomReqDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.common.user.FLUserDataType;
import my.ep.jaxb.fl.podata.FLPurchaseOrderDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.fl.FlActor;
import my.ep.persistence.entity.fl.FlAddress;
import my.ep.persistence.entity.fl.FlSupplierDtl;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.fl.request.VAddressTypeDeliveryAddress;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmAgOffice;
import my.ep.persistence.entity.pm.common.PmCity;
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
import my.ep.persistence.entity.pm.common.VPmDo;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.pm.VotFundType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBaseBackingBean;
import my.ep.web.backingbean.sc.requestnote.RequestNoteBackingBean;
import my.ep.web.backingbean.sm.applicationView.ViewSuppProfileBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.GlAccountSearchBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.pr.PurchaseRequestManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.fl.helper.LocationSingleton;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.DisciplinaryActionVO;
import my.ep.web.vo.FLSupplierItemVO;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean for Create Purchase Order.
 *
 * @author Giribabu
 * @since 7/10/2013
 */
@ManagedBean(name = "purchaseRequestBackingBean")
@ViewScoped
public class PurchaseRequestBackingBean extends FulfilmentBaseBackingBean<PurchaseRequestManagedBean> {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(PurchaseRequestBackingBean.class);
	public static final String NAV_TO_CREATE_PURCHASE_REQUEST = "/xhtml/fl/purchaserequest/prBasePage.xhtml";
	private boolean checkFlg = false;
	private boolean sqExpiredMessageFlag = false;
	private boolean displayItemPnl;
	private boolean selectedAllChargeItem;
	private String className="@(.myPrClassName)";
	private IPagingDataModel<PmUom> unitOfMeasurementDataModel = new UnitOfMeasurementPagingDataModel();
	private boolean selectAllCheckbox;
	private IPagingDataModel<PmOrgProfile> chargedPtjDataModel = new chargedPtjPagingDataModel();
	private PmOrgProfile selectedPmOrgProfile;
	//start added for #24353
	private IPagingDataModel<PmAddressType> pmAddressTypeDataModel = new pmAddressTypePagingDataModel();
	private PmAddressType selectedPmAddressType;
	private PmAddressType defaultPmAddressType;
	//end added for #24353
	private IPagingDataModel<VPmDo> searchGlAccount = new SearchGlAccountPagingDataModel();
	/**
	 * Gets the class name.
	 *
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Sets the class name.
	 *
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
	
	private String queryItemCode;
	private String queryItemName;
	private String queryDesc;
	/* #17087 zonal flag */
	private String zonalTest = "";
	
	private static Map<Long, PmVotFund> votFundCodeMap = new HashMap<Long, PmVotFund>();

	/**
	 * Instantiates a new purchase request backing bean.
	 */
	public PurchaseRequestBackingBean() {
		
	}

	/**
	 * save,submit the purchase request
	 * 
	 * * @param String action 
	 *  <p>submit - save data into database and BPM process will route to the next respective user.
	 * 	<p>save - save data into database.
	 *  navigate to acknowledge page.
	 */
	public String fulfillmentRequestAction(String action) throws Exception {
		IFulfilmentRequestSO fulfilmentRequestSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			//24893 - start
			//to handle WIP task
			for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
				for(FlFulfilmentItemAddr itemAddr : flDeliveryAddress.getFlFulfilmentItemAddr()){
					if(null == itemAddr.getChargePTJOrg()) {
						if(getManagedBean().getFulfilmentRequest().getStatus().getStatusId().equals(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION) 
								|| getManagedBean().getFulfilmentRequest().getStatus().getStatusId().equals(FulfilmentStatusConst.PR_PENDING_APPROVAL) 
								|| getManagedBean().getFulfilmentRequest().getStatus().getStatusId().equals(FulfilmentStatusConst.PR_PENDING_REVISION) 
								|| getManagedBean().getFulfilmentRequest().getStatus().getStatusId().equals(FulfilmentStatusConst.PR_PENDING_REVISION_APPROVAL)
								|| getManagedBean().getFulfilmentRequest().getStatus().getStatusId().equals(FulfilmentStatusConst.PR_PENDING_QUERY_VERIFICATION)) {
							itemAddr.setChargePTJOrg(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile());
						}
					}
				}
			}
			//24893 - end
			if(!getManagedBean().isValidated()){
			   validateFulfilmentRequest(action);
			}
			
			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flFulfilmentRequest = getManagedBean().getFulfilmentRequest();
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
			
			//Bug #20701 Problem to generate DO - to monitor
			log.info(":::fulfillmentRequestAction() "+ action +". Doc no.: "+ flFulfilmentRequest.getDocNo() +". Deliv adrs size: "+ flFulfilmentRequest.getFlDeliveryAddresses().size());
			//end Bug #20701
			
			//29728 - start
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
				if(flFulfilmentRequest.getFlSupplierDtl().getBranchCode()==null && getManagedBean().getBranchCode()!=null){
					flFulfilmentRequest.getFlSupplierDtl().setBranchCode(getManagedBean().getBranchCode());
				}
			}
			//29728 - end
			
			reqDTO.setFlFulfilmentRequest(flFulfilmentRequest);
			reqDTO.setContractRequest(false);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlDeleteAttachmentList(flBB.getManagedBean().getDeleteAttachments());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			reqDTO.setOutcome(action);
			reqDTO.setToken(token);
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setApproverMap(getManagedBean().getApproverMap());
			reqDTO.setUserLocale(getFacesScopes().getUserLocale());
			reqDTO.setPidValidityExpired(getManagedBean().isPidValidityExpired());
			reqDTO.setTaskDeadLine(getManagedBean().getTaskDeadLine());
			// 12177 - Start
			reqDTO.setSqExpiredStatus(getManagedBean().getSqExpiredStatus());
			reqDTO.setSqNumber(getManagedBean().getSqNumber());
			reqDTO.setRequestNoteId(getManagedBean().getRequestNoteId());
			// 12177 - End
			// start - #25790
			if (BpmOutcomeConst.BPM_DRAFT.equals(action) && flFulfilmentRequest.getCreatedOrgProfile() != null) {
				reqDTO.setPtjOfOrgProfileId(retrievePTJByOrgProfileId(flFulfilmentRequest.getCreatedOrgProfile()
						.getOrgProfileId(), flFulfilmentRequest.getCreatedOrgProfile().getCreatedDate()));
			}// end - #25790
			if(isFlFulfilmentRequisitioner()) {
				reqDTO.setRequisitioner(Boolean.TRUE);
				if(getManagedBean().getSelectedApprover() != null){
					PmUserGroupUser groupUser = getManagedBean().getApproverMap().get(getManagedBean().getSelectedApprover());
					if(groupUser!=null) {
						List<PmUser> approverUserList = sortApproverUserList(getManagedBean().getApproverGroupUserList(), groupUser.getPmUser(),getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId());
						
						reqDTO.setApproverUserList(approverUserList);
						reqDTO.setSelectedApprover(getManagedBean().getSelectedApprover());
					}
				}
			}
			if(reqDTO.getApproverUserList() == null || reqDTO.getApproverUserList().isEmpty()){
				reqDTO.setBpmApproverList(getManagedBean().getBpmApproverList());
			}
			if(getManagedBean().getPmDigiSign() != null){
				reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			}
			FulfilmentResDTO resDTO = fulfilmentRequestSO.saveFulfillmentRequest(reqDTO);
			reqDTO.setTriggerBPM(true);
			reqDTO.setFlFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			resDTO = fulfilmentRequestSO.saveFulfillmentRequest(reqDTO);
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully for doc no. "+ flFulfilmentRequest.getDocNo());
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
				//28718 - start
				if(!getManagedBean().isTriggerPRErrorMsg()) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(messageId, 
									new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_REQUEST), flFulfilmentRequest.getDocNo()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				}
				//28718 - end
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					reqDTO.setFlRequestId(resDTO.getFlRequestId());
					resDTO = fulfilmentRequestSO.retrieveFulfilmentRequest(reqDTO);
					setFulfilmentRequestField(resDTO);
				}
			} else {
				throw new EpException(MessageConst.ERROR_700020);
			} 
		}  finally {
			fulfilmentRequestSO = null;
		}
		return null;
	}

	/**
	 * set fulfilment request attribute into managedbean for save purchase request details.
	 *
	 * @param resDTO the new fulfilment request field
	 */
	public void setFulfilmentRequestField(FulfilmentResDTO resDTO) {
		PurchaseRequestReqDTO scReqDTO = new PurchaseRequestReqDTO();
		IPurchaseRequestSO purchaseRequestSO = (IPurchaseRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,	IPurchaseRequestSO.class);
		getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
		getManagedBean().getFulfilmentRequest().setTitle(getManagedBean().getFulfilmentRequest().getTitle().trim());
		FulfilmentBackingBean flBB = new FulfilmentBackingBean(ParameterConst.DOCTYPE_PURCHASE_REQUEST, resDTO.getFlFulfilmentRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
		flBB.initializeRemarkAndAttachement(resDTO.getFlFulfilmentRequest().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
		getManagedBean().setSelectedTabIndex(0);
		getSupplierBranchesList(resDTO.getFlFulfilmentRequest().getSmSupplier());
		if(resDTO.getFlFulfilmentRequest().getApproverId() != null){
			getManagedBean().setSelectedApprover(resDTO.getFlFulfilmentRequest().getApproverId());
		}
		if(isFlFulfilmentRequisitioner()){
			initializeApproverGroup();
			getApproverList();
		}
		getManagedBean().setCurrentPreparedOrgId(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId());
		addPurchsedItems();
		if(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().size()>0) {
			getManagedBean().setTierMap(new HashMap<Long,List<FlFulfilmentItemAddr>>());
			getManagedBean().setZonalTierMap(new HashMap<String,List<FlFulfilmentItemAddr>>());
			
			sortAddress(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses());
			getManagedBean().setSelectedDelAddr(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().get(0));
			setPriceType();
			populateItemsForSelectedAddress();
			List<FlFulfilmentItemAddr> fulfilmentItemAddrList = null;
			for(FlDeliveryAddress delAddr : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
				for(FlFulfilmentItemAddr itemAddr : delAddr.getFlFulfilmentItemAddr()){
					getManagedBean().setGrouped(itemAddr.isGrouped());//28718
					if(itemAddr.getCmItem()==null && itemAddr.getScRequestItem().getCmItem()!=null) {
						itemAddr.setCmItem(itemAddr.getScRequestItem().getCmItem());
					}
					
					//re-contruct zonal-tier map
					if(itemAddr.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER) 
							|| itemAddr.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER)){
						scReqDTO.setPriceType(itemAddr.getPriceType());
						if(getManagedBean().getScRequestSupplierItemIdMap().get(itemAddr.getScRequestItem().getRequestNoteItemId()) != null){
							scReqDTO.setRequestSupplierItemId(getManagedBean().getScRequestSupplierItemIdMap().get(itemAddr.getScRequestItem().getRequestNoteItemId()));
						}

						scReqDTO.setFlAddress(getManagedBean().getSelectedDelAddr().getFlAddress());//TODO
						PurchaseRequestResDTO zoneIdResDTO = purchaseRequestSO.getSupplierItemZoneIdByFlAddress(scReqDTO);
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
					}else if(itemAddr.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER) 
							|| itemAddr.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_PROMOTION_TIER)){
						
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
		//Redmine #20011
		getManagedBean().setOldGstRateFlg(checkOldGSTRate(getManagedBean().getFulfilmentRequest()));
		
		//initialize all the COA map
		Long jabatanOrgProfileId = -1l;
		PmOrgProfile jabatan = retrieveJabatanOrgProfile(getManagedBean().getFulfilmentRequest().getChargeOrgProfile().getOrgProfileId());
		if(jabatan!=null) {
			jabatanOrgProfileId = jabatan.getOrgProfileId();
		}
		getVotFundList(jabatanOrgProfileId);
		IOrgProfileSO profileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		OrgProfileReqDTO orgReqDTO = new OrgProfileReqDTO();
		orgReqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest().getChargeOrgProfile().getOrgProfileId());
		getManagedBean().setCurrentChargePtjGroup(profileSO.retrieveJabatanByOrgProfileId(orgReqDTO).getPmOrgProfile());
		if(getManagedBean().getFulfilmentRequest().getStartDate() == null){
			getManagedBean().getFulfilmentRequest().setStartDate(new Timestamp(System.currentTimeMillis()));
			Date startDate = getManagedBean().getFulfilmentRequest().getStartDate();
			int deliveryTerm = 0;
			deliveryTerm = getManagedBean().getFulfilmentRequest().getDeliveryTerm();
			String uomCode = getManagedBean().getFulfilmentRequest().getPmUom().getParameterCode();
			Calendar calendar = Calendar.getInstance();
			if(uomCode.equalsIgnoreCase(ParameterConst.DELIVERY_TERM_DAYS)){
				getManagedBean().setMaxDate(DateUtils.getEndDate(startDate, deliveryTerm,ParameterConst.DELIVERY_TERM_DAYS));
				getManagedBean().getFulfilmentRequest().setEndDate(DateUtils.getEndDate(startDate, deliveryTerm,ParameterConst.DELIVERY_TERM_DAYS));//34555
			}else if(uomCode.equalsIgnoreCase(ParameterConst.DELIVERY_TERM_MONTHS)){
				getManagedBean().setMaxDate(DateUtils.getEndDate(startDate, deliveryTerm,ParameterConst.DELIVERY_TERM_MONTHS));
				getManagedBean().getFulfilmentRequest().setEndDate(DateUtils.getEndDate(startDate, deliveryTerm,ParameterConst.DELIVERY_TERM_MONTHS));//34555
			}else if(uomCode.equalsIgnoreCase(ParameterConst.DELIVERY_TERM_YEARS)) {
				getManagedBean().setMaxDate(DateUtils.getEndDate(startDate, deliveryTerm,ParameterConst.DELIVERY_TERM_YEARS));
				getManagedBean().getFulfilmentRequest().setEndDate(DateUtils.getEndDate(startDate, deliveryTerm,ParameterConst.DELIVERY_TERM_YEARS));//34555
			}
//			getManagedBean().getFulfilmentRequest().setEndDate(calendar.getTime()); //Close for #34555
		}
		for(FlFulfilmentItem item : getManagedBean().getFulfilmentRequest().getFlFulfilmentItems()){
			if(item.getFlQuery() != null){
				getManagedBean().setQueryDisplay(true);
				break;
			}
		}
		if(isFlFulfilmentApprover() || getManagedBean().isFlDocMaintain()){
			getManagedBean().getFulfilmentRequest().setSubmissionDate(getManagedBean().getFulfilmentRequest().getSubmissionDate());
		}
		getManagedBean().setMinistryProfileId(retrieveMinistryOrgProfile(
				getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId()).getOrgProfileId());
		String gfmasEnable = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.GFMAS_ENABLED);

/*		gfmasEnable="false";
		log.info("gfmasEnable is : " +gfmasEnable);*/
		if(!"true".equalsIgnoreCase(gfmasEnable)){

			getManagedBean().setEspkb(true);
		}
		//29555 start
		getPreparedByName();
	    //29555 end
	}

	/**
	 * Sets the price type.
	 */
	private void setPriceType() {
		Map<Long, PmParameter> priceTypeMap = new HashMap<Long,PmParameter>();
		for(ScRequestNoteDtl scRequestNoteDtl : getManagedBean().getRequestNoteWithDtlsList()){
			if(priceTypeMap.get(scRequestNoteDtl.getRequestItem().getRequestNoteItemId()) != null){
				continue;
			}else{
				priceTypeMap.put(scRequestNoteDtl.getRequestItem().getRequestNoteItemId(), scRequestNoteDtl.getPriceType());
			}
		}
		for(FlDeliveryAddress delAddr : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddr : delAddr.getFlFulfilmentItemAddr()){
				if(priceTypeMap.containsKey(itemAddr.getScRequestItem().getRequestNoteItemId())){
					itemAddr.setPriceType(priceTypeMap.get(itemAddr.getScRequestItem().getRequestNoteItemId()));
				}
			}
		}
	}

	/**
	 * flDeliveryAddrList method get Delivery address list based on OrgProfileId and AddressType.
	 */
	public void flDeliveryAddrList() {
		AddressReqDTO reqDTO = new AddressReqDTO();
		reqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId());
		reqDTO.setAddressType(AddressType.DELIVERY_ADDRESS.value());
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAddressSO.class);
		AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
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
		getManagedBean().setFlManualAddress(new FlDeliveryAddress());
		getManagedBean().getFlManualAddress().setDeliveryAddressId(1L);//TODO 
		getManagedBean().getFlManualAddress().setPmAddressId(null);
		getManagedBean().getFlManualAddress().setFlAddress(new FlAddress());
		// default to Malaysia
		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICountrySO.class);
		CountryReqDTO countryReqDTO = new CountryReqDTO();
		countryReqDTO.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
		getManagedBean().getFlManualAddress().getFlAddress().setPmCountry(countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());
		getManagedBean().getFlManualAddress().getFlAddress().setPmState(new PmState());
		getManagedBean().getFlManualAddress().getFlAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getFlManualAddress().getFlAddress().setPmCity(new PmCity());
		getManagedBean().getFlManualAddress().getFlAddress().setPmDivision(new PmDivision());
		getManagedBean().getFlManualAddress().setReceivingGrp(new PmAddressType());
		
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(getManagedBean().getFlManualAddress().getFlAddress().getPmCountry().getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStateList(resDTO.getStateList());
		
		if(getManagedBean().getAddressTypesList()==null) {
			AddressReqDTO addReqDTO = new AddressReqDTO();
			addReqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId());
			addReqDTO.setAddressType(AddressType.DELIVERY_ADDRESS.value());
			IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAddressSO.class);
			AddressResDTO addResDTO = addressSO.retrieveAddressTypeList(addReqDTO);
			getManagedBean().setAddressTypesList(addResDTO.getAddressTypeList());
		}
		if(getManagedBean().getManualAddressMap() == null || getManagedBean().getManualAddressMap().size() == 0){
			setManualAddressList();
		}
		getManagedBean().setAutoUpdate(Boolean.FALSE);
	}
	
	/**
	 * updating the user selected delivery addresses to delivery address list(intended address for goods delivery).
	 * <p>
	 * It does not allow the duplicate addresses selected. If any,it will give the "Address already selected" Message.
	 * <p>
	 * Sorts delivery addresses based on the address.
	 */
	public void updateDeliveryAddrDt(List<BaseEntity> entity) {
		if(getManagedBean().getSelectedDeliveryFullAddr() != null){
			getManagedBean().setSelectedPmDelAddrList(new ArrayList<PmAddressType>());
			for(VAddressTypeDeliveryAddress vAddressTypeDeliveryAddress : getManagedBean().getSelectedDeliveryFullAddr()){
				getManagedBean().getSelectedPmDelAddrList().add(vAddressTypeDeliveryAddress.getPmAddressType());
			}
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
				deliveryAddr.setPmAddressId(addrType.getPmAddress().getAddressId());//TODO remove comment when no error hit
				deliveryAddr.setStartDate(getManagedBean().getFulfilmentRequest().getStartDate());
				deliveryAddr.setEndDate(getManagedBean().getFulfilmentRequest().getEndDate());
				deliveryAddr.setPmUom(getManagedBean().getFulfilmentRequest().getPmUom());
				deliveryAddr.setDeliveryTerm(getManagedBean().getFulfilmentRequest().getDeliveryTerm());
				deliveryAddr.setFlAddress(convertPmAddressToFlAddress(addrType.getPmAddress()));//TODO NEW
				if (null == getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()) {
					getManagedBean().getFulfilmentRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
				}
				
				getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().add(deliveryAddr);
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_745053, new String[]{getFullAddress(addrType.getPmAddress())}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				getManagedBean().setAutoUpdate(Boolean.FALSE);
			}
		}
		getManagedBean().setSelectedDelAddr(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().get(0));
		getManagedBean().setAddressTypesList(new ArrayList<PmAddressType>());
		selectAddrEvent();
	}
	
	/**
	 * add manual delivery address to delivery address list(intended address for goods delivery).
	 */
	public void updateManualDeliveryAddrDt() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		getManagedBean().setEmailValidate(true);
		if(null== getManagedBean().getFlManualAddress().getFlAddress() ||  getManagedBean().getFlManualAddress().isEditable()){
			// add to cart validation
			RequestContext.getCurrentInstance().execute("addManualDeliveryAddr.hide()");
			getManagedBean().setRequired(Boolean.FALSE);			
			return;
		}
		FlDeliveryAddress deliveryAddr = null;
		FlAddress manualAddr=new FlAddress();
		manualAddr=getManagedBean().getFlManualAddress().getFlAddress();
		deliveryAddr = new FlDeliveryAddress();
		deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFulfilmentRequest());
		deliveryAddr.setPmAddressId(manualAddr.getAddressId());//for display RO use
		deliveryAddr.setFlAddress(manualAddr);
		
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
		deliveryAddr.setReceivingGrp(getManagedBean().getFlManualAddress().getReceivingGrp());
		
		deliveryAddr.setSelectedPtjCodeNameValue(getManagedBean().getFlManualAddress().getSelectedPtjCodeNameValue());//#24353
		getManagedBean().setSelectedDelAddr(deliveryAddr);
		getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().add(deliveryAddr);
		selectAddrEvent();
		
		// add to cart validation
		RequestContext.getCurrentInstance().execute("addManualDeliveryAddr.hide()");
		getManagedBean().setRequired(Boolean.FALSE);
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
	 * Checks if is delivery address contains in delivery address list.
	 *
	 * @param addrType selected delivery address.
	 * @param flDeliveryAddresses list of delivery addresses.
	 * @return returns true if selected delivery address is already added in delivery address list,otherwise false.
	 */
	private boolean isDeliveryAddressContains(PmAddressType addrType,
			List<FlDeliveryAddress> flDeliveryAddresses) {
		boolean isContains=false;
		if(null==flDeliveryAddresses|| flDeliveryAddresses.isEmpty()){
			return false;
		}
		for(FlDeliveryAddress flDelAddress:flDeliveryAddresses){
			if (flDelAddress.getReceivingGrp() == null ){
			if(addrType.getPmAddress().getAddressId().longValue()==flDelAddress.getPmAddressId().longValue()){
				isContains=true;
				}
			}
		}
		return isContains;
	}
	
	/**
	 * if any checkbox selected from delivery address  List,show the deleted button for 
	 * <p>
	 * delete the particular delivery address from delivery address  List.
	 * <p>
	 * if all check boxes unselected,hide the delete button.
	 *
	 * @param e the e
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
	
	/**
	 * Checks if is any address selected.
	 *
	 * @return true, if is any address selected
	 */
	private boolean isAnyAddrSelected() {
		boolean isAnyAddrSelected=false;
		for(FlDeliveryAddress delAddr:getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			if(delAddr.isSelectedAddressRowDelete()){
				isAnyAddrSelected=true;
				break;
			}
		}
		return isAnyAddrSelected;
	}

	/**
	 * Removes the selected delivery addresses from delivery Address List.
	 * <p>
	 * clear the order quantity from item list which is assigned to selected delivery addresses
	 */
	public void removeAddRow(){
		IPurchaseRequestSO purchaseRequestSO = (IPurchaseRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,	IPurchaseRequestSO.class);
		PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();
		List<FlFulfilmentItemAddr> flItemAddrList = null;
				
		if(null !=getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			getManagedBean().setDeleteButtonHide(true);
			//removal itemAddrList from tier and zonaltier map
			for(FlDeliveryAddress deliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
				for(FlFulfilmentItemAddr itemAddr : deliveryAddress.getFlFulfilmentItemAddr()){
					if(deliveryAddress.isSelectedAddressRowDelete()){
						if(itemAddr.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_PROMOTION_TIER) 
								||itemAddr.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER)){
							Long key = itemAddr.getScRequestItem().getRequestNoteItemId();
							if(getManagedBean().getTierMap().containsKey(key)){
								flItemAddrList = getManagedBean().getTierMap().get(key);
								flItemAddrList.remove(itemAddr);
							}
						}else if(itemAddr.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER) 
								||itemAddr.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)){
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
				for(FlDeliveryAddress deliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
					for(FlFulfilmentItemAddr itemAddr : deliveryAddress.getFlFulfilmentItemAddr()){
						if(itemAddr.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER) 
								||itemAddr.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)){
							for (List<FlFulfilmentItemAddr> values : getManagedBean().getZonalTierMap().values()) {
								for(FlFulfilmentItemAddr reCalculate : values){
									BigDecimal sumOrderQty = new BigDecimal(0);
									sumOrderQty = sumOrderQty.add(reCalculate.getOrderedQty());	
									if(getManagedBean().getScRequestSupplierItemIdMap().get(reCalculate.getScRequestItem().getRequestNoteItemId()) != null){
										reqDTO.setRequestSupplierItemId(getManagedBean().getScRequestSupplierItemIdMap().get(reCalculate.getScRequestItem().getRequestNoteItemId()));
									}
									reqDTO.setPriceType(itemAddr.getPriceType());
									reqDTO.setFlAddress(getManagedBean().getSelectedDelAddr().getFlAddress());
									reqDTO.setOrderQuantity(sumOrderQty);
									PurchaseRequestResDTO purResDTO = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
									reCalculate.setScSupplierItemPrice(purResDTO.getSupplierItemPrice());
									reCalculate.setUnitPrice(purResDTO.getUnitPrice());
									setGstPrice(reCalculate, purResDTO.getUnitPrice().doubleValue());
									reCalculate.setOrderedAmt(reCalculate.getOrderedQty().multiply(reCalculate.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
								}
							}	
						}else if(itemAddr.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_PROMOTION_TIER) 
								||itemAddr.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER)){
							for (List<FlFulfilmentItemAddr> values : getManagedBean().getTierMap().values()) {
								for(FlFulfilmentItemAddr reCalculate : values){
									BigDecimal sumOrderQty = new BigDecimal(0);
									sumOrderQty = sumOrderQty.add(reCalculate.getOrderedQty());	
									reqDTO.setRequestSupplierItemId(reCalculate.getScSupplierItemPrice().getScRequestSupplierItem().getRequestSupplierItemId());
									reqDTO.setPriceType(itemAddr.getPriceType());
									reqDTO.setFlAddress(getManagedBean().getSelectedDelAddr().getFlAddress());
									reqDTO.setOrderQuantity(sumOrderQty);
									PurchaseRequestResDTO purResDTO = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
									reCalculate.setScSupplierItemPrice(purResDTO.getSupplierItemPrice());
									reCalculate.setUnitPrice(purResDTO.getUnitPrice());
									setGstPrice(reCalculate, purResDTO.getUnitPrice().doubleValue());
									reCalculate.setOrderedAmt(reCalculate.getOrderedQty().multiply(reCalculate.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
								}
							}	
						}
					}
				}
				
			} else {
				getManagedBean().setSelectedDelAddr(null);
				for(FlFulfilmentItemAddr flItemAddr : getManagedBean().getShowAllItemsRows()) {
					if(getManagedBean().getFulfilmentRequest().getProcurementTypeCategory().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_TYPE_CODE_SERVICE)){
						flItemAddr.setGst(true);
					} else {
						flItemAddr.setGst(false);
					}
					flItemAddr.setOrderedQty(BigDecimal.ZERO);
					flItemAddr.setOrderedAmt(BigDecimal.ZERO);
					if(flItemAddr.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_STANDARD)) {
						setGstPrice(flItemAddr, flItemAddr.getUnitPrice().doubleValue());
					} else {
						flItemAddr.setUnitPrice(BigDecimal.ZERO);
						flItemAddr.setGstAmount(BigDecimal.ZERO);
						flItemAddr.setGstUnitPrice(BigDecimal.ZERO);
					}
				}				
			}
			populateItemsForSelectedAddress();
		}
	}
	
	/**
	 * Select addr event.
	 */
	public void selectAddrEvent() {
		populateItemsForSelectedAddress();
		RequestContext context = RequestContext.getCurrentInstance();
		context.reset("form:itemDetPanelGroup");
	}

	/**
	 * assign items to selected delivery address ,once user key in Ordered Quantity to item from item list
	 * </br>
	 * populating items for selected delivery address from delivery address list.
	 */
	private void populateItemsForSelectedAddress() {
		List<FlFulfilmentItemAddr> itemsList = new ArrayList<FlFulfilmentItemAddr>();

		for (FlFulfilmentItemAddr newItem : getManagedBean().getRequestNoteItemList()) {
			boolean existed = false;
			if (getManagedBean().getSelectedDelAddr() != null && getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr() != null) {
				for (FlFulfilmentItemAddr selectedItem : getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()) {
					if (selectedItem.getScRequestItem().getRequestNoteItemId().equals(newItem.getScRequestItem().getRequestNoteItemId())) {
						existed = true;
						selectedItem.setPriceType(newItem.getPriceType());
						itemsList.add(selectedItem);
						break;
					}
				}
			}
			if (!existed) {
				newItem.setOrderedQty(new BigDecimal(0));
				newItem.setOrderedAmt(new BigDecimal(0));
				if(newItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER) 
						||newItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)
						|| newItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_PROMOTION_TIER) 
						||newItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER)){
					newItem.setUnitPrice(new BigDecimal(0));
				}
				/*if(getManagedBean().getFulfilmentRequest().getProcurementTypeCategory()!=null && 
					getManagedBean().getFulfilmentRequest().getProcurementTypeCategory().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_TYPE_CODE_SERVICE)){
					newItem.setGst(true);
					setGstPrice(newItem, newItem.getUnitPrice().doubleValue());
				} else {
					newItem.setGst(false);
					newItem.setGstAmount(BigDecimal.ZERO);
					newItem.setGstUnitPrice(newItem.getUnitPrice());
				}*/
				//Redmine #20011
				newItem.setGst(true);
				setGstPrice(newItem, newItem.getUnitPrice().doubleValue());
				
				itemsList.add(newItem);
			}
		}
		itemsList.size();
		if(null != getManagedBean().getSelectedDelAddr()){
			validatePriceType(itemsList);
		}
		getManagedBean().setShowAllItemsRows(itemsList);
	}
	
	/**
	 * Validate price type.
	 *
	 * @param itemsList the items list
	 */
	private void validatePriceType(List<FlFulfilmentItemAddr> itemsList) {
		IPurchaseRequestSO purchaseRequestSO = (IPurchaseRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseRequestSO.class);
		PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();

		for(FlFulfilmentItemAddr itemAddr : itemsList){
			if(itemAddr.getPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_STANDARD)
					|| itemAddr.getPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION)){
				reqDTO.setPriceType(itemAddr.getPriceType());
				if(getManagedBean().getScRequestSupplierItemIdMap().get(itemAddr.getScRequestItem().getRequestNoteItemId()) != null){
					reqDTO.setRequestSupplierItemId(getManagedBean().getScRequestSupplierItemIdMap().get(itemAddr.getScRequestItem().getRequestNoteItemId()));
				}
				reqDTO.setFlAddress(getManagedBean().getSelectedDelAddr().getFlAddress());
				PurchaseRequestResDTO purResDTO = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
				itemAddr.setUnitPrice(purResDTO.getUnitPrice());
				setGstPrice(itemAddr, purResDTO.getUnitPrice().doubleValue());
				itemAddr.setScSupplierItemPrice(purResDTO.getSupplierItemPrice());
				PurchaseRequestResDTO zoneIdResDTO = purchaseRequestSO.getSupplierItemZoneIdByFlAddress(reqDTO);
				itemAddr.setZoneId(zoneIdResDTO.getSpecZoneId());
			}
		}
	}

	/**
	 * Method get item list based on PurchaseRequestId.
	 */
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
				flItemAddr.setPmUomId(purchaseItem.getRequestItem().getUom().getUomId());
				if(purchaseItem.getRequestItem().getCmItem()!=null) {
					flItemAddr.setItemCode(purchaseItem.getRequestItem().getCmItem().getExtensionCode());
				} else {
					flItemAddr.setItemCode(purchaseItem.getRequestItem().getTempUnspscCode());
				}
				//zonal & zonal tier & zonal promo & tier promo
				if(purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_STANDARD) 
						|| purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER) 
						|| purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION)
						|| purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER)){
					if(!getManagedBean().getScRequestSupplierItemIdMap().containsKey(purchaseItem.getRequestItem().getRequestNoteItemId())){
						getManagedBean().getScRequestSupplierItemIdMap().put(purchaseItem.getRequestItem().getRequestNoteItemId(), purchaseItem.getScRequestSupplierItem().getRequestSupplierItemId());
					}
				}
				//stand & stand promo & tier & tier promo
				if(purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_STANDARD) 
						|| purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_PROMOTION)
						|| purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER)
						|| purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_PROMOTION_TIER)){
					flItemAddr.setRequestSupplierItemId(purchaseItem.getScRequestSupplierItem().getRequestSupplierItemId());
					reqDTO.setRequestSupplierItemId(purchaseItem.getScRequestSupplierItem().getRequestSupplierItemId());
					reqDTO.setPriceType(purchaseItem.getPriceType());
					reqDTO.setOrderQuantity(new BigDecimal(1));
					purResDTO = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
					if(purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_STANDARD) 
						|| purchaseItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_PROMOTION)) {
						flItemAddr.setUnitPrice(purResDTO.getUnitPrice());
						setGstPrice(flItemAddr, purResDTO.getUnitPrice().doubleValue());
					} else {
						flItemAddr.setUnitPrice(new BigDecimal(0));
						flItemAddr.setGstAmount(new BigDecimal(0));
						flItemAddr.setGstUnitPrice(new BigDecimal(0));
					}

					flItemAddr.setScSupplierItemPrice(purResDTO.getSupplierItemPrice());
					
				}else{//zonal & zonal tier & zonal promo & zonal tier promo
					flItemAddr.setUnitPrice(new BigDecimal(0));
					flItemAddr.setGstAmount(new BigDecimal(0));
					flItemAddr.setGstUnitPrice(new BigDecimal(0));
					flItemAddr.setScSupplierItemPrice(null);

					flItemAddr.setScSupplierItemPrice(purResDTO.getSupplierItemPrice());
					
				}
				flItemAddr.setOrderedQty(new BigDecimal(0));
				flItemAddr.setOrderedAmt(new BigDecimal(0));
				allItemsList.add(flItemAddr);
			}else{
				if(purchaseItem.getRequestItem().getCmItem()!=null) {
					flItemAddr.setItemCode(purchaseItem.getRequestItem().getCmItem().getExtensionCode());
				} else {
					flItemAddr.setItemCode(purchaseItem.getRequestItem().getTempUnspscCode());

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

	/**
	 * Checks if is purchase item contains.
	 *
	 * @param purchaseItem the purchase item
	 * @param showAllItemsRows the show all items rows
	 * @return true, if is purchase item contains
	 */
	private boolean isPurchaseItemContains(ScRequestNoteDtl purchaseItem,
			List<FlFulfilmentItemAddr> showAllItemsRows) {
		boolean isContains=false;
		if(null==showAllItemsRows|| showAllItemsRows.isEmpty()){
			return false;
		}
		for(FlFulfilmentItemAddr flFulItemAddress:showAllItemsRows){
			if((""+purchaseItem.getRequestItem().getRequestNoteItemId()+purchaseItem.getRequestItem().getUom().getUomId()).equalsIgnoreCase(""+flFulItemAddress.getScRequestItem().getRequestNoteItemId()+flFulItemAddress.getPmUomId())){
				isContains=true;
			}
		}
		return isContains;
	}
	

	/**
	 * Selected item row.
	 */
	public void selectedItemRow() {
		getManagedBean().setSelectedRowFlg(true);
	}
	
	/**
	 * Method calculate Order Amount based on order quantity.
	 *
	 * @param flItemAddr the fl item addr
	 */
	public void retrieveOrderAmount(FlFulfilmentItemAddr flItemAddr){
		IPurchaseRequestSO purchaseRequestSO = (IPurchaseRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,	IPurchaseRequestSO.class);
		PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();
		if(null != flItemAddr.getOrderedQty()){
			BigDecimal formattedOrderedQty = flItemAddr.getOrderedQty().setScale(flItemAddr.getScRequestItem().getUom().getDecimalScale(),BigDecimal.ROUND_HALF_UP);
			flItemAddr.setOrderedQty(formattedOrderedQty);
		}	
		// Tier only
		if(flItemAddr.getPriceType() != null 
				&& (flItemAddr.getPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_TIER)
						|| flItemAddr.getPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_PROMOTION_TIER))){
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
			reqDTO.setRequestSupplierItemId(flItemAddr.getRequestSupplierItemId());
			reqDTO.setPriceType(flItemAddr.getPriceType());
			reqDTO.setFlAddress(getManagedBean().getSelectedDelAddr().getFlAddress());
			reqDTO.setOrderQuantity(sumOrderQty);
			PurchaseRequestResDTO purResDTO = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
			for(FlFulfilmentItemAddr itemAddr : itemAddrList){
				itemAddr.setUnitPrice(purResDTO.getUnitPrice());
				setGstPrice(itemAddr, purResDTO.getUnitPrice().doubleValue());
				itemAddr.setOrderedAmt(itemAddr.getOrderedQty().multiply(itemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
			}
			if(oriItemAddr.getOrderedQty() == null){
				oriItemAddr.setOrderedQty(new BigDecimal(0));
			}
			oriItemAddr.setUnitPrice(purResDTO.getUnitPrice());
			setGstPrice(oriItemAddr, purResDTO.getUnitPrice().doubleValue());
			oriItemAddr.setOrderedAmt(oriItemAddr.getOrderedQty().multiply(oriItemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
			if(sumOrderQty.doubleValue() > 0 && purResDTO.getUnitPrice().doubleValue() == 0){
				BigDecimal reCalSumOrderQty = new BigDecimal(0);
				flItemAddr.setOrderedQty(new BigDecimal(0));
				for(FlFulfilmentItemAddr itemAddr : itemAddrList){
					reCalSumOrderQty = reCalSumOrderQty.add(itemAddr.getOrderedQty());	
				}
				reqDTO.setRequestSupplierItemId(flItemAddr.getRequestSupplierItemId());
				reqDTO.setPriceType(flItemAddr.getPriceType());
				reqDTO.setFlAddress(getManagedBean().getSelectedDelAddr().getFlAddress());
				reqDTO.setOrderQuantity(reCalSumOrderQty);
				PurchaseRequestResDTO reCalculateUnitPrice = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
				for(FlFulfilmentItemAddr itemAddr : itemAddrList){
					itemAddr.setUnitPrice(reCalculateUnitPrice.getUnitPrice());
					setGstPrice(itemAddr, reCalculateUnitPrice.getUnitPrice().doubleValue());
					itemAddr.setOrderedAmt(itemAddr.getOrderedQty().multiply(itemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
				}
				itemAddrList.remove(flItemAddr);
				throw new EpException(MessageConst.ERROR_745055);
			}
			flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
			// add  or remove items to Selected delivery address
			itemsToSelectedAddress(flItemAddr);
		}else if(flItemAddr.getPriceType() != null 
				&& (flItemAddr.getPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_TIER)
						|| flItemAddr.getPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER))){
			
			List<FlFulfilmentItemAddr> itemAddrList = null;
			String key = null;
			BigDecimal sumOrderQty = new BigDecimal(0);
			
			reqDTO.setPriceType(flItemAddr.getPriceType());
			if(getManagedBean().getScRequestSupplierItemIdMap().get(flItemAddr.getScRequestItem().getRequestNoteItemId()) != null){
				reqDTO.setRequestSupplierItemId(getManagedBean().getScRequestSupplierItemIdMap().get(flItemAddr.getScRequestItem().getRequestNoteItemId()));
			}

			reqDTO.setFlAddress(getManagedBean().getSelectedDelAddr().getFlAddress());
			PurchaseRequestResDTO zoneIdResDTO = purchaseRequestSO.getSupplierItemZoneIdByFlAddress(reqDTO);
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
						&& (""+itemAddr.getScRequestItem().getRequestNoteItemId().longValue()+itemAddr.getPmUomId()).equalsIgnoreCase(""+flItemAddr.getScRequestItem().getRequestNoteItemId()+flItemAddr.getPmUomId())){

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
					throw new EpException(MessageConst.ERROR_745056);
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
			reqDTO.setPriceType(flItemAddr.getPriceType());
			reqDTO.setFlAddress(getManagedBean().getSelectedDelAddr().getFlAddress());
			reqDTO.setOrderQuantity(sumOrderQty);
			PurchaseRequestResDTO purResDTO = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
			flItemAddr.setScSupplierItemPrice(purResDTO.getSupplierItemPrice());
			for(FlFulfilmentItemAddr itemAddr : itemAddrList){
				itemAddr.setUnitPrice(purResDTO.getUnitPrice());
				setGstPrice(itemAddr, purResDTO.getUnitPrice().doubleValue());
				itemAddr.setOrderedAmt(itemAddr.getOrderedQty().multiply(itemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
			}
			if(oriItemAddr.getOrderedQty() == null){
				oriItemAddr.setOrderedQty(new BigDecimal(0));
			}
			oriItemAddr.setUnitPrice(purResDTO.getUnitPrice());
			setGstPrice(oriItemAddr, purResDTO.getUnitPrice().doubleValue());
			oriItemAddr.setOrderedAmt(oriItemAddr.getOrderedQty().multiply(oriItemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
			
			if(sumOrderQty.doubleValue() > 0 && purResDTO.getUnitPrice().doubleValue() == 0){
				BigDecimal reCalSumOrderQty = new BigDecimal(0);
				flItemAddr.setOrderedQty(new BigDecimal(0));
				for(FlFulfilmentItemAddr itemAddr : itemAddrList){
					reCalSumOrderQty = reCalSumOrderQty.add(itemAddr.getOrderedQty());	
				}
				if(getManagedBean().getScRequestSupplierItemIdMap().get(flItemAddr.getScRequestItem().getRequestNoteItemId()) != null){
					reqDTO.setRequestSupplierItemId(getManagedBean().getScRequestSupplierItemIdMap().get(flItemAddr.getScRequestItem().getRequestNoteItemId()));
				}
				reqDTO.setPriceType(flItemAddr.getPriceType());
				reqDTO.setFlAddress(getManagedBean().getSelectedDelAddr().getFlAddress());
				reqDTO.setOrderQuantity(reCalSumOrderQty);
				PurchaseRequestResDTO reCalculateUnitPrice = purchaseRequestSO.getUnitPriceByQuantityAndPriceType(reqDTO);
				for(FlFulfilmentItemAddr itemAddr : itemAddrList){
					itemAddr.setUnitPrice(reCalculateUnitPrice.getUnitPrice());
					setGstPrice(itemAddr, reCalculateUnitPrice.getUnitPrice().doubleValue());
					itemAddr.setOrderedAmt(itemAddr.getOrderedQty().multiply(itemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
				}
				itemAddrList.remove(flItemAddr);
				throw new EpException(MessageConst.ERROR_745055);
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
			if(flItemAddr.getPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_STANDARD) 
					|| flItemAddr.getPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION)){
				if(flItemAddr.getZoneId() == null){
					oriItemAddr.setOrderedQty(new BigDecimal(0));
					flItemAddr.setOrderedQty(new BigDecimal(0));
					throw new EpException(MessageConst.ERROR_745056);
				}
			}
			if(flItemAddr.getOrderedQty() == null){
				flItemAddr.setOrderedQty(new BigDecimal(0));
			}
			flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
			flItemAddr.setOrderedAmt(flItemAddr.getOrderedQty().multiply(flItemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
			if(oriItemAddr.getOrderedQty() == null){
				oriItemAddr.setOrderedQty(new BigDecimal(0));
			}
			oriItemAddr.setUnitPrice(flItemAddr.getUnitPrice());
			setGstPrice(oriItemAddr, flItemAddr.getUnitPrice().doubleValue());
			oriItemAddr.setOrderedAmt(oriItemAddr.getOrderedQty().multiply(oriItemAddr.getGstUnitPrice()).setScale(2, RoundingMode.HALF_UP));
			// add  or remove items to Selected delivery address
			itemsToSelectedAddress(flItemAddr);
		}
		populateItemsForSelectedAddress();
		if(!getManagedBean().getFulfilmentRequest().getProcurementType().getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_PK7)){
			checkPRThresholdPerItem();
		}
	}
	
	
	/**
	 * Items to selected address.
	 *
	 * @param flItemAddr the fl item addr
	 */
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
				getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().add(flItemAddr);
			}
		}
	}

	/**
	 * Pre render view.
	 */
	public void preRenderView() {
		
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		// temp: to monitor performance
		if (taskId != null) {
			MDC.put("taskId", taskId);
		}
		log.info("start - getLoginUserOrg");
		getLoginUserOrg();
		log.info("end - getLoginUserOrg");
		IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
		if (taskId != null) {
			renewSesionObject();
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.renewSesionObject();
			log.info("start - getBpmTaskDetails");
			Object obj = super.getBpmTaskDetails(Object.class, new String[]{BpmOutcomeConst.BPM_SAVE});
			log.info("end - getBpmTaskDetails");
			log.info("start - initializePRBeans");
			initializePRBeans();
			log.info("start - initializePRBeans");
			FLPurchaseOrderDataType flPurchaseOrd = (FLPurchaseOrderDataType) obj;
			if(flPurchaseOrd.getOrderDetails().getErrorId() != null && flPurchaseOrd.getOrderDetails().getErrorId() > 0){
				getManagedBean().setAutoUpdate(Boolean.FALSE);
				getManagedBean().setGfmasError(true);
				FulfilmentReqDTO errReq = new FulfilmentReqDTO();
				errReq.setErrorId(flPurchaseOrd.getOrderDetails().getErrorId());
				FulfilmentResDTO errRes = fulfilmentSO.retrieveErrorContent(errReq);
				getManagedBean().setGfmasErrorCode(errRes.getErrorCode());
				getManagedBean().setGfmasErrorDesc(MessageUtils.getMessage("ERR-"+errRes.getErrorCode(), new String[]{}, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale()));
				getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
			}
			if (null != this.getManagedBean().getOutComes() && !"".equals(this.getManagedBean().getOutComes())) {
				getManagedBean().setSubmitButtonStatus(false);
			}
			if(!flPurchaseOrd.getOrderDetails().getApproverList().isEmpty()){
				for(FLUserDataType userDataType : flPurchaseOrd.getOrderDetails().getApproverList()){
					getManagedBean().getBpmApproverList().add(userDataType.getUserId());	
				}
			}
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			flReqDTO.setFlRequestId(flPurchaseOrd.getOrderDetails().getRequestId().longValue());
			log.info("start - retrieveFulfilmentRequest");
			FulfilmentResDTO resDTO = fulfilmentSO.retrieveFulfilmentRequest(flReqDTO);
			log.info("end - retrieveFulfilmentRequest");
			log.info("start - setFulfilmentRequestField");
			setFulfilmentRequestField(resDTO);
			log.info("end - setFulfilmentRequestField");
			BigDecimal gstRate = getGstRate();
			if(getManagedBean().getFulfilmentRequest().getGstRate()!=null && getManagedBean().getFulfilmentRequest().getGstRate().compareTo(gstRate)!=0) {
				getManagedBean().setGstRateChange(true);
			}
			getManagedBean().getFulfilmentRequest().setGstRate(gstRate);
						
			getSupplierGst(getManagedBean().getFulfilmentRequest(), getManagedBean().isGstRateChange());
			
			// in case of PID
			log.info("start - PID validity process");
			if(getManagedBean().getFulfilmentRequest().getScPurchaseInDefault()!=null) {
				int pidValidDays = getManagedBean().getFulfilmentRequest().getScPurchaseInDefault().getPidValidDays();
				Date pidExpiredDate = new Date(); 
				
				if(getManagedBean().getFulfilmentRequest().getScPurchaseRequest().getRequestNote().getQuote()!=null) {
					pidExpiredDate = DateUtils.addDays(getManagedBean().getFulfilmentRequest().getScPurchaseRequest().getRequestNote().getQuote().getCreatedDate(), pidValidDays);
				} else {
					pidExpiredDate = DateUtils.addDays(getManagedBean().getFulfilmentRequest().getScPurchaseRequest().getRequestNote().getCreatedDate(), pidValidDays);
				}
				if(pidExpiredDate.compareTo(DateUtils.getCurrentDate())<0) {
					getManagedBean().setPidValidityExpired(true);
					if(isFlFulfilmentRequisitioner()) {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_740040, null, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
					} else {
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_740041, null, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));						
					}
				}
			}
			
			log.info("end - PID validity process");
			checkDisciplinaryAction(); //26279
			checkSupplierDeleteBranch(); //29728
		}
		log.info("start - initializePRBeans");
		initializePRBeans();
		log.info("end - initializePRBeans");
		//34555-start
		log.info("start - updateMaxDeliveryDate");
		updateMaxDeliveryDate();
		log.info("end - updateMaxDeliveryDate");
		//34555-end
		/*#17087 Get Zonal flag*/
		log.info("start - getzonalTest");
		getzonalTest();
		log.info("end - getzonalTest");
		
		log.info("start - 20011");
		//Redmine #20011
		getManagedBean().setOldGstRateFlg(checkOldGSTRate(getManagedBean().getFulfilmentRequest()));
		if(!getManagedBean().getFulfilmentRequest().getProcurementType().getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_PK7)){
			checkPRThresholdPerItem();
		}else if(getManagedBean().getFulfilmentRequest().getProcurementType().getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_PK7)){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
					MessageUtils.getMessage(MessageConst.INFO_745084, null, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
			if (getManagedBean().getTaskId() != null){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
					MessageUtils.getMessage(MessageConst.INFO_745085, null, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
			}
		}
		log.info("end - 20011");
		log.info("start - addButton");
		//Redmine Enhancement #12177
	    addButton();
	    log.info("end - addButton");

	    MDC.clear();
	    if(sqExpiredMessageFlag){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
					MessageUtils.getMessage(MessageConst.ERROR_760481, 
							null,MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
	    
	    FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "prBasePage.xhtml");
	}
	
	// 26279 - start
	/**
	 * Checks Disciplinary Action
	 * 
	 * if true will display Disciplinary Action
	 * 
	 */
	private void checkDisciplinaryAction() {
		if (isDisciplinaryActionPRPendingStatus()) {
			SmSupplier smSupplier = getManagedBean().getFulfilmentRequest().getSmSupplier();
			SmDisciplinaryAction smDisciplinaryAction = getImposedDisciplinaryAction(smSupplier);
			if (smDisciplinaryAction != null) {
				getManagedBean().setSmDisciplinaryAction(smDisciplinaryAction);
				getManagedBean().setDisciplinaryActionMessage(getDisciplinaryActionMsg(smDisciplinaryAction));
			} else {
				getManagedBean().setDisciplinaryActionMessage(StringUtils.EMPTY);
			}
		}
	}

	/**
	 * Checks if the current PR's status is one of the pending statuses
	 * 
	 * @return true if the PR's status is one of the pending statuses,
	 *         false for otherwise statuses
	 */
	private boolean isDisciplinaryActionPRPendingStatus() {
		Long statusId = getManagedBean().getFulfilmentRequest().getStatus().getStatusId();
		Set<Long> pendingStatusIds = new HashSet<>(Arrays.asList(
				FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION, FulfilmentStatusConst.PR_PENDING_APPROVAL,
				FulfilmentStatusConst.PR_PENDING_REVISION, FulfilmentStatusConst.PR_PENDING_REVISION_APPROVAL,
				FulfilmentStatusConst.PR_PENDING_QUERY_VERIFICATION));
		return pendingStatusIds.contains(statusId);
	}
	// 26279 - End
	
	// 29728 - start
	/**
	 * Checks if the current PR's status is one of the pending statuses
	 * 
	 * @return true if the PR's status is one of the pending statuses, false for
	 *         otherwise statuses
	 */
	private boolean isSupplierDeleteBranchPRPendingStatus() {
		Long statusId = getManagedBean().getFulfilmentRequest().getStatus().getStatusId();
		Set<Long> pendingStatusIds = new HashSet<>(Arrays.asList(
				FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION, FulfilmentStatusConst.PR_PENDING_APPROVAL,
				FulfilmentStatusConst.PR_PENDING_REVISION, FulfilmentStatusConst.PR_PENDING_REVISION_APPROVAL,
				FulfilmentStatusConst.PR_PENDING_QUERY_VERIFICATION));
		return pendingStatusIds.contains(statusId);
	}

	/**
	 * Checks if the supplier's branch is deleted and needs to be changed.
	 * 
	 * If the branch is marked for deletion and is currently in a pending
	 * status, this method sets the branch as needing a change and updates the
	 * branch code.
	 */
	private void checkSupplierDeleteBranch() {
		if (isSupplierDeleteBranchPRPendingStatus()) {
			if (getManagedBean().getFulfilmentRequest().getFlSupplierDtl().getBranchCode() != null
					&& isSelectedBranchDeleted(getManagedBean().getFulfilmentRequest().getFlSupplierDtl()
							.getBranchCode(), getManagedBean().getFulfilmentRequest().getSmSupplier().getSupplierId())) {
				getManagedBean().setBranchNeedToBeChanged(true);

				getManagedBean().setBranchCode(
						getManagedBean().getFulfilmentRequest().getFlSupplierDtl().getBranchCode());
			}
		}
	}
	// 29728 - End
	
	/**
	 * Calculate sub total based on quantity
	 *
	 * @param quantity the quantity
	 */
	public void calculateSubTotal(BigDecimal quantity){
		getManagedBean().getSelectedItemAddr().setSubTotal(quantity);
	}
	
	/**
	 * Method get approver group and approver List.
	 */
	private void initializeApproverGroup() {
		IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		try {
			if(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId() != getManagedBean().getCurrentPreparedOrgId()){
				reqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId());				
				resDTO = requestSO.retrieveGroupListByOrgId(reqDTO);

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
							getManagedBean().getApproverIdList().add(groupUser.getPmUser().getUserId());
							getManagedBean().getApproverMap().put(groupUser.getPmUser().getUserId(), groupUser);
						}
					}
					getManagedBean().getApproverIdList().clear();
					for(PmUserGroupUser groupUser : firstPmUserGroup) {
						getManagedBean().getApproverIdList().add(groupUser.getPmUser().getUserId());
					}
					if(getManagedBean().getFulfilmentRequest().getPmUserGroup()==null || getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId() == null){
						PmUserGroup pmUserGroup = new PmUserGroup();
						getManagedBean().getFulfilmentRequest().setPmUserGroup(pmUserGroup);
						getManagedBean().getFulfilmentRequest().getPmUserGroup().setUserGroupId(getManagedBean().getUserGroupList().get(0).getUserGroupId());
					}
					
				}
			}
			if(getManagedBean().getFulfilmentRequest().getPmUserGroup()!=null && getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId() == null){
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
	 * Gets the user group dropdown.
	 *
	 * @return the user group dropdown
	 */
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
	
	/**
	 * User group change listener.
	 */
	public void userGroupChangeListener() {
		IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		if(getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId() == null){
			return;
		}
		reqDTO.setGroupId(getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId());
		reqDTO.setApproverLimit(calculatesTotalOrderedAmt());
		try {
			resDTO = requestSO.retrieveApproverListByGroupId(reqDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		getManagedBean().getApproverIdList().clear();
		getManagedBean().getApproverGroupUserList().clear();
		getManagedBean().getApproverGroupUserList().addAll( resDTO.getApproverGroupUserList());
		for(PmUserGroupUser groupUser : resDTO.getApproverGroupUserList()) {
			getManagedBean().getApproverIdList().add(groupUser.getPmUser().getUserId());
		}
	}
	
	/**
	 * Checks if user is  fl fulfillment requisitioner.
	 *
	 * @return true, if is fl fulfilment requisitioner
	 */
	public boolean isFlFulfilmentRequisitioner() {
		return getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value());
	}
	
	/**
	 * Checks if is fl fulfilment approver.
	 *
	 * @return true, if is fl fulfilment approver
	 */
	public boolean isFlFulfilmentApprover() {
		return getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value());
	}
	
	/**
	 * Gets the login user org based on user id.
	 *
	 * @return the login user org
	 */
	private void getLoginUserOrg(){
		PmUser user = getFacesScopes().getUserSessionBean();
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(user.getUserId());
		IUserSO userSO = (IUserSO ) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO .class);
		UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
		PmUserOrg userOrg = resDTO.getUserOrgList().get(0);
		getManagedBean().setPmUserOrg(userOrg);
	}
	//29555 start
	/*
	 * set preparedby for managedbean by retrieving requistioner and docid
	 */
	public void getPreparedByName() {
		IFulfilmentSO fulfilmentSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentSO.class);
		FlActor actor;

		actor = fulfilmentSO.retrieveFlActors(
				getManagedBean().getFulfilmentRequest().getDocType(),
				getManagedBean().getFulfilmentRequest().getFulfilmentReqId(),
				FulfilmentConst.ROLE_CODE_REQUISITIONER);

		if(actor != null){
			getManagedBean().setPreparedBy(actor.getUserName());
		}else{
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getUserIdSessionFromLoginId());
			UserResDTO userResDTO = userSO.retrieveUserByUserId(userReqDTO);
			if(userResDTO.getPmUser().getUserName() != null){
				getManagedBean().setPreparedBy(userResDTO.getPmUser().getUserName());
			}
		}
	}
	//29555 end
	
	/**
	 * Checks if is check flg.
	 *
	 * @return the checkFlg
	 */
	public boolean isCheckFlg() {
		return checkFlg;
	}
	
	/**
	 * Sets the check flg.
	 *
	 * @param checkFlg the checkFlg to set
	 */
	public void setCheckFlg(boolean checkFlg) {
		this.checkFlg = checkFlg;
	}
	
	/**
	 * Gets the received ptj list based on org type.
	 *
	 * @return the received ptj list
	 */
	public List<SelectItem> getReceivedPTJList() {
		List<SelectItem> retrievePTJList = new ArrayList<SelectItem>();
		OrgProfileResDTO resDTO = new OrgProfileResDTO();
		IOrgProfileSO orgProfileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		retrievePTJList.add(new SelectItem("", ResourceBundleUtils.getMessage("lbl.txt.select",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())));
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
	
	/**
	 * Edits the manual address.
	 *
	 * @param flDelAddress the fl del address
	 */
	public void editManualAddress(FlDeliveryAddress flDelAddress){//retrieveCountryList();
		getManagedBean().setEmailValidate(false);
		getManagedBean().setFlManualAddress(flDelAddress);
		if(getManagedBean().getFlManualAddress().getFlAddress().getPmDivision()==null) {
			getManagedBean().getFlManualAddress().getFlAddress().setPmDivision(new PmDivision());
		}
		retrieveCountryList();
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(flDelAddress.getFlAddress().getPmCountry().getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStateList(resDTO.getStateList());
		
		DistrictReqDTO districtReqDTO = new DistrictReqDTO();
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
		
		if(getManagedBean().getFlManualAddress().getFlAddress().getPmDivision().getDivisionId()!=null) {
			DivisionReqDTO divReqDTO= new DivisionReqDTO();
			divReqDTO.setStateId(flDelAddress.getFlAddress().getPmState().getStateId());
			IDivisionSO divisionSO = (IDivisionSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDivisionSO.class);
			DivisionResDTO divResDTO = divisionSO.retriveDivisionListByState(divReqDTO);
			getManagedBean().setDivisionList(divResDTO.getDivisionList());
			districtReqDTO.setDivisionId(flDelAddress.getFlAddress().getPmDivision().getDivisionId());
			DistrictResDTO districtResDTO = districtSO.retriveDistrictListByDivision(districtReqDTO);
			getManagedBean().setDistrictList(districtResDTO.getDistrictList());		
		} else {
			districtReqDTO.setStateId(flDelAddress.getFlAddress().getPmState().getStateId());
			DistrictResDTO districtResDTO = districtSO.retriveDistrictListByStateID(districtReqDTO);
			getManagedBean().setDistrictList(districtResDTO.getDistrictList());			
		}
		
		CityReqDTO cityReqDTO = new CityReqDTO();
		cityReqDTO.setDistrictId(flDelAddress.getFlAddress().getPmDistrict().getDistrictId());
		ICitySO citySO = (ICitySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);
		CityResDTO cityResDTO = citySO.retriveCityListByDistrictId(cityReqDTO);
		this.getManagedBean().setCityList(cityResDTO.getCityList());
		if(getManagedBean().getManualAddressMap() == null || getManagedBean().getManualAddressMap().size() == 0){
			setManualAddressList();
		}
		getManagedBean().setManualAddressList(getManagedBean().getManualAddressMap().get(flDelAddress.getFlAddress().getPmDistrict().getDistrictId()));
		List<SelectItem> manualAddrList = new ArrayList<SelectItem>();
		if(getManagedBean().getManualAddressList()!=null) {
			for (PmAddressType obj : getManagedBean().getManualAddressList()) {
				manualAddrList.add(new SelectItem(obj.getAddressTypeId(), obj.getPmAddress().getAddressName()));
			}
		}
		getManagedBean().setManualAddrList(manualAddrList);
		flDelAddress.setEditable(true);
	}
	
	/**
	 * Receiving officer list based on address Id.
	 *
	 * @param flDelAddress the fl del address
	 */
	public void receivingOfficerList(FlDeliveryAddress flDelAddress){
		Long addressId = flDelAddress.getPmAddressId();
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
	
	/**
	 * Retrieve country list.
	 */
	public void retrieveCountryList() {
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();
		getManagedBean().setCountryList(resDTO.getCountryList());
	}
	
	/**
	 * Retrieve state list based on CountryID.
	 *
	 * @param e the e
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
	 * Retrieve division list based on stateId.
	 *
	 * @param e the e
	 */
	public void retrieveDivisionList(AjaxBehaviorEvent e){
		if(getManagedBean().getFlManualAddress().getFlAddress().getPmDivision() != null){
			getManagedBean().setDivisionList(null);
			getManagedBean().getFlManualAddress().getFlAddress().getPmDivision().setDivisionId(null);
		}
		if(getManagedBean().getFlManualAddress().getFlAddress().getPmDistrict() != null){
			getManagedBean().setDistrictList(null);
			getManagedBean().getFlManualAddress().getFlAddress().getPmDistrict().setDistrictId(null);
		}
		if(getManagedBean().getFlManualAddress().getFlAddress().getPmCity() != null){
			getManagedBean().setCityList(null);
			getManagedBean().getFlManualAddress().getFlAddress().getPmCity().setCityId(null);
		}
		
		Long stateId = (Long) e.getComponent().getAttributes().get("value");
		DivisionReqDTO reqDTO= new DivisionReqDTO();
		reqDTO.setStateId(stateId);
		IDivisionSO reqSO = (IDivisionSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDivisionSO.class);
		DivisionResDTO resDTO = reqSO.retriveDivisionListByState(reqDTO);
		getManagedBean().setDivisionList(resDTO.getDivisionList());
		getManagedBean().setDivisionDisplay(true);
		if(resDTO.getDivisionList().size()==0) {
			DistrictReqDTO districtReqDTO = new DistrictReqDTO();
			districtReqDTO.setStateId(stateId);
			IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
			DistrictResDTO districtResDTO = districtSO.retriveDistrictListByStateID(districtReqDTO);
			getManagedBean().setDistrictList(districtResDTO.getDistrictList());			
		}else{
			getManagedBean().setDivisionDisplay(false);
		}
	}
	
	/**
	 * start added for #24353
	 * Retrieve the division list for PTJSearch from profile management service object
	 */
	public void retrieveDivisionListForSearch(AjaxBehaviorEvent e){
		if(getManagedBean().getDistrictCode() != null){
			getManagedBean().setSearchDistrictList(null);
			getManagedBean().setDistrictCode(null);
		}
		Long stateId = (Long) e.getComponent().getAttributes().get("value");
		DistrictReqDTO districtReqDTO = new DistrictReqDTO();
		districtReqDTO.setStateId(stateId);
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
		DistrictResDTO districtResDTO = districtSO.retriveDistrictListByStateID(districtReqDTO);		
		getManagedBean().setSearchDistrictList(districtResDTO.getDistrictList());	
	}
	//end added for #24353
	
	/**
	 * Retrieve district list based on divisionId.
	 *
	 * @param e the e
	 */
	public void retrieveDistrictList(AjaxBehaviorEvent e) {
		if(getManagedBean().getFlManualAddress().getFlAddress().getPmDistrict() != null){
			getManagedBean().setDistrictList(null);
			getManagedBean().getFlManualAddress().getFlAddress().getPmDistrict().setDistrictId(null);
		}
		if(getManagedBean().getFlManualAddress().getFlAddress().getPmCity() != null){
			getManagedBean().setCityList(null);
			getManagedBean().getFlManualAddress().getFlAddress().getPmCity().setCityId(null);
		}
		Long divisionId = (Long) e.getComponent().getAttributes().get("value");
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setDivisionId(divisionId);
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByDivision(reqDTO);
		getManagedBean().setDistrictList(resDTO.getDistrictList());
		
	}
	
	/**
	 * Retrieve city list based on districtId.
	 *
	 * @param e the e
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
				//24893 - start
				if(null != childData.getChargePTJOrg()) {
					getManagedBean().setChargePTJOrg(childData.getChargePTJOrg());
					System.out.println("Item Charge PTJ");
				} else {
					if(getManagedBean().getFulfilmentRequest().getStatus().getStatusId().equals(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION) 
							|| getManagedBean().getFulfilmentRequest().getStatus().getStatusId().equals(FulfilmentStatusConst.PR_PENDING_APPROVAL) 
							|| getManagedBean().getFulfilmentRequest().getStatus().getStatusId().equals(FulfilmentStatusConst.PR_PENDING_REVISION) 
							|| getManagedBean().getFulfilmentRequest().getStatus().getStatusId().equals(FulfilmentStatusConst.PR_PENDING_REVISION_APPROVAL)
							|| getManagedBean().getFulfilmentRequest().getStatus().getStatusId().equals(FulfilmentStatusConst.PR_PENDING_QUERY_VERIFICATION)) {
						getManagedBean().setChargePTJOrg(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile());
						System.out.println("PTJ Pembayar");
					} else {
						getManagedBean().setChargePTJOrg(getManagedBean().getFulfilmentRequest().getChargeOrgProfile());
						System.out.println("FL Request Charge PTJ");
					}					
				}
				//24893 - end
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
		//24893 - start
		getManagedBean().setChargePTJOrg(new PmOrgProfile());
		//24893 - end
	}
	
	/**
	 * Charge line apply for selected nodes.
	 */
	public void chargeLineApply() {
		IParameterSO parameterSO = null;
		try {
			getManagedBean().setSelectChargeLineRow(false);
			if(null == getManagedBean().getVotFundId() || getManagedBean().getVotFundId().longValue()==0){
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
					//24893 - start
					childItemAddress.setChargePTJOrg(getManagedBean().getChargePTJOrg());
					//24893 - end
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
							//24893 - start
							itemDelAddress.setChargePTJOrg(getManagedBean().getChargePTJOrg());
							//24893 - end
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
	
	/**
	 * Charge line apply for selected nodes.
	 */
	public void resetChargeLineDetails() {
		getManagedBean().setSelectChargeLineRow(false);
		getManagedBean().setVotFundId(null);
		getManagedBean().setGlAccountId(null);
		getManagedBean().setPrgActivityId(null);
		getManagedBean().setSubSetiaId(null);
			
		resetItemsWithChargeDrops(); // set COA to Y
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
					childItemAddress.setPmVotFund(null);
					childItemAddress.setAssetInd(null);
					childItemAddress.setPmGlAccount(null);
					childItemAddress.setPmPrgActivity(null);
					childItemAddress.setPmSubSetia(null);
					childItemAddress.setVotFundDisplay(false);
				}
				childItemAddress = (FlFulfilmentItemAddr)selectedNode.getData();
				
				//Code for multiple delivery addresses based on item code
				for (FlDeliveryAddress delAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()) {
					for (FlFulfilmentItemAddr itemDelAddress : delAddress.getFlFulfilmentItemAddr()) {
						if (itemDelAddress.getScRequestItem().getRequestNoteItemId().longValue()==childItemAddress.getScRequestItem().getRequestNoteItemId().longValue()) {
							itemDelAddress.setPmVotFund(null);
							itemDelAddress.setAssetInd(null);
							itemDelAddress.setPmGlAccount(null);
							itemDelAddress.setPmPrgActivity(null);
							itemDelAddress.setPmSubSetia(null);
						}
					}
				}
			}
			populateChargeLineData(true);
			onNodeSelect();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
					MessageUtils.getMessage(MessageConst.INFO_745079, new String[]{}, 
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			
	}
	
	/**
	 * Reset items with charge drops.
	 */
	private void resetItemsWithChargeDrops() {
		updateSelectedTreeNodes();
		if(getManagedBean().getSelectedNodes().length==0) {
			throw new EpException(MessageConst.ERROR_100049);
		}
		for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddress:deliveryAddress.getFlFulfilmentItemAddr()){
				for(TreeNode node:getManagedBean().getSelectedNodes()){
					FlFulfilmentItemAddr selectedItem=(FlFulfilmentItemAddr)node.getData();
					if(itemAddress.getScRequestItem().getRequestNoteItemId().equals(selectedItem.getScRequestItem().getRequestNoteItemId())){
						itemAddress.setCoaValue("N");
						break;
					}
				}
			}
		}
	}
	
	/**
	 *    call this method  as user click on charge line summary menu or click on charge line update button.
	 * 
	 *<p> Populate charge line data.
	 *
	 * @param retainPreviousSelection the retain previous selection
	 */
	private void populateChargeLineData(boolean retainPreviousSelection) {
		getManagedBean().setChargeLineItemRoot(new DefaultTreeNode("root", null));
		Map<Long, List<FlFulfilmentItemAddr>> chargeLineItemsMap = new HashMap<Long, List<FlFulfilmentItemAddr>>();
		for(FlDeliveryAddress  deliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
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
		BigDecimal grandTotalOrderAmt=new BigDecimal(0);
		getManagedBean().setOrderedAmtGrandTotal(grandTotalOrderAmt);
		int i=0;
		for(Entry<Long, List<FlFulfilmentItemAddr>> itemEntry : chargeLineItemsMap.entrySet()){
			flItemAddress = (FlFulfilmentItemAddr) itemEntry.getValue().get(0).cloneEntity();
			flItemAddress.setParent(true);
			if(null != flItemAddress.getPmVotFund() && null != flItemAddress.getPmVotFund().getVotFundId()){
				//28718 - start
				if(flItemAddress.isGrouped() && !flItemAddress.isGroupedItemValidated()) {
					flItemAddress.setCoaValue("N");
				} else {
					flItemAddress.setCoaValue("Y");
				}	
				//28718 - end
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

	/**
	 * Charge line validation before update charge line .
	 */
	private void chargeLineValidation() {
		if(getManagedBean().getVotFundId()==null || getManagedBean().getVotFundId().longValue()==0) {
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
		//24893 - start
		if(null == getManagedBean().getChargePTJOrg() || null == getManagedBean().getChargePTJOrg().getOrgProfileId()) {
			throw new EpException(MessageConst.ERROR_100022, new String[]{
					ResourceBundleUtils.getMessage("lbl.fl.charge.ptj",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
		}
		//24893 - end
		List<Long> selectedItemAddressList = new ArrayList<Long>();
		updateSelectedTreeNodes();
		FlFulfilmentItemAddr flTemp = null;
		for(TreeNode selectedNode : getManagedBean().getSelectedNodes()){
			flTemp = (FlFulfilmentItemAddr)selectedNode.getData();
			selectedItemAddressList.add(flTemp.getScRequestItem().getRequestNoteItemId());
		}
		//#28718 - Start
/*		boolean isVotSOrAmanahExist = false;
		//boolean isVotBOrPOrTExist = false;
		boolean isVotBOrTExist = false;
		boolean isVotPExist = false;
		
		String votFundType = null;
		for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
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
					} else if(votFundType.equalsIgnoreCase(VotFundType.BEKALAN.value()) || votFundType.equalsIgnoreCase(VotFundType.TANGGUNGAN.value())) {
						isVotBOrTExist = true;
						break;
					} else if(votFundType.equalsIgnoreCase(VotFundType.PEMBANGUNAN.value()) ) {
						isVotPExist = true;

						break;
					}
				}
			}
			if(isVotSOrAmanahExist || isVotBOrTExist || isVotPExist) {
				break;
			}
		}*/
		//#28718 - End	
		PmVotFund pmVotFund = votFundCodeMap.get(getManagedBean().getVotFundId());
		//#28718 - Start
/*		if(pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.BEKALAN.value()) || 
				pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.TANGGUNGAN.value())) {
				if(isVotSOrAmanahExist || isVotPExist) {
					throw new EpException(MessageConst.ERROR_740005);
				}
				
			} else if(pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.PEMBANGUNAN.value())) {
					if(isVotSOrAmanahExist || isVotBOrTExist) {
						throw new EpException(MessageConst.ERROR_740005);
					}
			} else if(pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.SETIA.value()) || 
					pmVotFund.getVotFundType().equalsIgnoreCase(VotFundType.AMANAH.value())) {
				if(isVotBOrTExist || isVotPExist) {
					throw new EpException(MessageConst.ERROR_740005);
				}
		}*/
		//#28718 - End
		
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
	
	/**
	 * Update selected charge line tree nodes.
	 */
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

	/**
	 * Update all items with charge drops.
	 */
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
						//28718 - start
						if(itemAddress.isGrouped()) {
							itemAddress.setGroupedItemValidated(true);
						}
						//28718 - end
						itemAddress.setCoaValue("Y");
						break;
					}
				}
			}
		}
	}

	/**
	 * Show item query.
	 *
	 * @param flItemAddress the fl item address
	 */
	public void showItemQuery(FlFulfilmentItemAddr flItemAddress){
		if(flItemAddress.getScRequestItem().getCmItem()!=null) {
			setQueryItemCode(flItemAddress.getScRequestItem().getCmItem().getExtensionCode());
		} else {
			setQueryItemCode(flItemAddress.getScRequestItem().getTempUnspscCode());
		}
		setQueryItemName(flItemAddress.getScRequestItem().getItemName());
		if(null != flItemAddress.getFlFulfilmentItem().getFlQuery()){
			setQueryDesc(flItemAddress.getFlFulfilmentItem().getFlQuery().getQueryDesc());
		}
	}
	
	/**
	 * Checks if is display item pnl.
	 *
	 * @return the displayItemPnl
	 */
	public boolean isDisplayItemPnl() {
		return displayItemPnl;
	}

	/**
	 * Sets the display item pnl.
	 *
	 * @param displayItemPnl the displayItemPnl to set
	 */
	public void setDisplayItemPnl(boolean displayItemPnl) {
		this.displayItemPnl = displayItemPnl;
	}
	
	/**
	 * Gets the pm prj setia.
	 *
	 * @return the pmPrjSetia
	 */
	public Long getPmPrjSetia() {
		return pmPrjSetia;
	}

	/**
	 * Sets the pm prj setia.
	 *
	 * @param pmPrjSetia the pmPrjSetia to set
	 */
	public void setPmPrjSetia(Long pmPrjSetia) {
		this.pmPrjSetia = pmPrjSetia;
	}

	/**
	 * Gets the pm prg activity.
	 *
	 * @return the pmPrgActivity
	 */
	public Long getPmPrgActivity() {
		return pmPrgActivity;
	}

	/**
	 * Sets the pm prg activity.
	 *
	 * @param pmPrgActivity the pmPrgActivity to set
	 */
	public void setPmPrgActivity(Long pmPrgActivity) {
		this.pmPrgActivity = pmPrgActivity;
	}

	/**
	 * Gets the query item code.
	 *
	 * @return the queryItemCode
	 */
	public String getQueryItemCode() {
		return queryItemCode;
	}

	/**
	 * Sets the query item code.
	 *
	 * @param queryItemCode the queryItemCode to set
	 */
	public void setQueryItemCode(String queryItemCode) {
		this.queryItemCode = queryItemCode;
	}

	/**
	 * Gets the query item name.
	 *
	 * @return the queryItemName
	 */
	public String getQueryItemName() {
		return queryItemName;
	}

	/**
	 * Sets the query item name.
	 *
	 * @param queryItemName the queryItemName to set
	 */
	public void setQueryItemName(String queryItemName) {
		this.queryItemName = queryItemName;
	}

	/**
	 * Gets the query desc.
	 *
	 * @return the queryDesc
	 */
	public String getQueryDesc() {
		return queryDesc;
	}

	/**
	 * Sets the query desc.
	 *
	 * @param queryDesc the queryDesc to set
	 */
	public void setQueryDesc(String queryDesc) {
		this.queryDesc = queryDesc;
	}

	/**
	 * Gets the dis pm prj setia.
	 *
	 * @return the disPmPrjSetia
	 */
	public Long getDisPmPrjSetia() {
		return disPmPrjSetia;
	}

	/**
	 * Sets the dis pm prj setia.
	 *
	 * @param disPmPrjSetia the disPmPrjSetia to set
	 */
	public void setDisPmPrjSetia(Long disPmPrjSetia) {
		this.disPmPrjSetia = disPmPrjSetia;
	}

	/**
	 * Gets the dis pm prg activity.
	 *
	 * @return the disPmPrgActivity
	 */
	public Long getDisPmPrgActivity() {
		return disPmPrgActivity;
	}

	/**
	 * Sets the dis pm prg activity.
	 *
	 * @param disPmPrgActivity the disPmPrgActivity to set
	 */
	public void setDisPmPrgActivity(Long disPmPrgActivity) {
		this.disPmPrgActivity = disPmPrgActivity;
	}

	/**
	 * Checks if is selected all charge item.
	 *
	 * @return the selectedAllChargeItem
	 */
	public boolean isSelectedAllChargeItem() {
		return selectedAllChargeItem;
	}

	/**
	 * Sets the selected all charge item.
	 *
	 * @param selectedAllChargeItem the selectedAllChargeItem to set
	 */
	public void setSelectedAllChargeItem(boolean selectedAllChargeItem) {
		this.selectedAllChargeItem = selectedAllChargeItem;
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
	 * Gets the financing type based on subSetiaId.
	 *
	 * @param e the e
	 * @return the financing type
	 */
	public void getFinancingType(AjaxBehaviorEvent e) {
		Long subSetiaId = (Long) e.getComponent().getAttributes().get("value");
		if(subSetiaId!=null) {
			IProjectSetiaSO projectSetiaSO = (IProjectSetiaSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IProjectSetiaSO.class);
			ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
			reqDTO.setSubSetiaId(subSetiaId);
			ProjectSetiaResDTO resDTO = projectSetiaSO.retrieveSubSetia(reqDTO);
			getManagedBean().setFinancingTypeCode(resDTO.getPmSubSetia().getFinancingTypeValue());
		}
	}
	
	/**
	 * Gets the user profiles based on LoginId.
	 *
	 * @return the user profiles
	 */
	public void getUserProfiles(){
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setLoginId(getFacesScopes().getUserSessionBean().getLoginId());
		UserResDTO userResDTO = userSO.retrieveUserByLoginId(userReqDTO);
		FulfilmentReqDTO fulfilmentReqDTO = new FulfilmentReqDTO();
		fulfilmentReqDTO.setPmUser(userResDTO.getPmUser());
	}
	
	/**
	 * Gets the receiving officer list based on addressId and addressType.
	 *
	 * @param addressId the address id
	 * @param addressType the address type
	 * @return the receiving officer list
	 */
	public AddressResDTO getReceivingOfficerList(Long addressId, String addressType){
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAddressSO.class);
		AddressReqDTO reqDTO = new AddressReqDTO();
		reqDTO.setAddressId(addressId);
		reqDTO.setAddressType(addressType);
		AddressResDTO resDTO = addressSO.retrieveReceivingOfficerList(reqDTO);
		getManagedBean().setReceivingOffList(resDTO.getAddressTypeList());
		return resDTO;
	}
	
	/**
	 * Gets the ag account based on Issued Org Profile id.
	 *
	 * @return the ag account
	 */
	public void getAgAccount(BaseEntity entity){
		checkPtjGroupAndInitialize();
		getManagedBean().getFulfilmentRequest().setIssuedOrgProfile(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile());
		initializeApproverGroup();
		initializeDelAddr();
		IAgOfficeSO glAccountSO = (IAgOfficeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAgOfficeSO.class);
		AgOfficeReqDTO reqDTO = new AgOfficeReqDTO();
		reqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId());
		AgOfficeResDTO resDTO = glAccountSO.retrieveAgOfficeByOrgProfileId(reqDTO);
		if(getManagedBean().getFulfilmentRequest().getAgOffice()==null){
			getManagedBean().getFulfilmentRequest().setAgOffice(new PmAgOffice());
		}
		getManagedBean().getFulfilmentRequest().setAgOffice(resDTO.getAgOffice());
		getManagedBean().getFulfilmentRequest().setAgOfficeName(resDTO.getAgOffice().getOfficeName());
	}
	
	/**
	 * Initialize deliver address.
	 */
	private void initializeDelAddr() {
		if(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId() != getManagedBean().getCurrentPreparedOrgId()){
			getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().clear();
			for(FlFulfilmentItemAddr itemAddr : getManagedBean().getShowAllItemsRows()){
				itemAddr.setOrderedQty(new BigDecimal(0));
			}
			getManagedBean().setCurrentPreparedOrgId(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId());
		}
	}

	/**
	 * Check ptj group and initialize.
	 */
	private void checkPtjGroupAndInitialize() {
		IOrgProfileSO profileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId());
		OrgProfileResDTO resDTO = profileSO.retrieveJabatanByOrgProfileId(reqDTO);
		Long orgId = null;
		if(resDTO.getPmOrgProfile() != null){
			orgId = resDTO.getPmOrgProfile().getOrgProfileId();
		}
		getManagedBean().setCurrentPtjGroupId(orgId);
		if(getManagedBean().getCurrentPtjGroupId() != null){
			reqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId());
			if(profileSO.retrieveJabatanByOrgProfileId(reqDTO).getPmOrgProfile().getOrgProfileId() != getManagedBean().getCurrentPtjGroupId()){
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
	}

	/**
	 * Sets the manual address list.
	 */
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
	 * Retrieve delivery address list based on districtId for manual address popup.
	 *
	 * @param e the e
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
			supplierBranches.add(new SelectItem(supplierBranch.getBranchCode(),supplierBranch.getBranchName()));
		}
		getManagedBean().setSupplierBranchesList(supplierBranches);
		
		//29728 -Start
		if((getManagedBean().getSupplierBranchesList().isEmpty() || getManagedBean().getSupplierBranchesList()==null)
				&& getManagedBean().getFulfilmentRequest().getFlSupplierDtl()!=null){			
			if(getManagedBean().getFulfilmentRequest().getFlSupplierDtl().getBranchCode() != null) {
				isSelectedBranchDeleted(getManagedBean().getFulfilmentRequest().getFlSupplierDtl().getBranchCode(),getManagedBean().getFulfilmentRequest().getSmSupplier().getSupplierId());
			}
			resetSupplierInfo();
		}
		//29728 - End	
	}
	
	/**
	 * Gets the selected supplier branch details based on supplier BranchId.
	 *
	 * @param e the e
	 * @return the selected supplier branch dtl
	 */
	public void getSelectedSupplierBranchDtl(AjaxBehaviorEvent e){
		Long supplierBranchCode = (Long) e.getComponent().getAttributes().get("value");
		if(supplierBranchCode!=null) {
			//29728 - start
			if(!isSelectedBranchDeleted(supplierBranchCode, getManagedBean().getFulfilmentRequest().getSmSupplier().getSupplierId())) {
				IFulfilmentRequestSO requestSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,	IFulfilmentRequestSO.class);
				FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
				reqDTO.setSupplierBranchCode(supplierBranchCode);
				reqDTO.setLatestApplId(getManagedBean().getFulfilmentRequest().getSmSupplier().getSmAppl().getApplId());
				FulfilmentResDTO resDTO = requestSO.retrieveSupplierBranchDtlByBranchCode(reqDTO);
			
				SmSupplierBranch supplierBranch = resDTO.getSmSupplierBranch();//all this should reset into our Flrequest.FlSupplierDtl
				FlSupplierDtl supplierDtl = getManagedBean().getFulfilmentRequest().getFlSupplierDtl();
				supplierDtl.setBranchName(supplierBranch.getBranchName());
				supplierDtl.setBranchCode(supplierBranch.getBranchCode());
				updateSupplierBranchAddress(supplierBranch,supplierDtl);
			}//29728 - end		
		} else { // reset to HQ info
			resetSupplierInfo(); //29728
		}
		getSupplierGst(getManagedBean().getFulfilmentRequest(), false);
		//29728 - start
		getManagedBean().setBranchNeedToBeChanged(false);
		//29728 - end
	}
	
	/**
	 * Gets the vot fund code map.
	 *
	 * @return the votFundCodeMap
	 */
	public static Map<Long, PmVotFund> getVotFundCodeMap() {
		return votFundCodeMap;
	}

	/**
	 * Sets the vot fund code map.
	 *
	 * @param votFundCodeMap the votFundCodeMap to set
	 */
	public static void setVotFundCodeMap(Map<Long, PmVotFund> votFundCodeMap) {
		PurchaseRequestBackingBean.votFundCodeMap = votFundCodeMap;
	}

	/**
	 * On tab change.
	 *
	 * @param selectedTabIndex the selected tab index
	 */
	public void onTabChange(int selectedTabIndex){
		//28718 - start
		getManagedBean().setTriggerPRErrorMsg(Boolean.FALSE);
		//28718 - end
		if(selectedTabIndex==0){
			initializeApproverGroup();
		}
		if(selectedTabIndex==1){
			sortAddress(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses());
			getManagedBean().setEmailValidate(true);
			initializeApproverGroup();
		}
		if(selectedTabIndex==2){
			if(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()!=null) { 
				sortOrderSummaryList(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses());
				getOrderSummaryGrandTotal();
			}
		}
		if(selectedTabIndex==3){
			populateChargeLineData(false);	
			setClassName("@(.prChargeLinePnlClass)");
			onNodeSelect();
			getManagedBean().setChargeLinePanel(false);
			getManagedBean().setProgramLov(false);
			getManagedBean().setProjectLov(false);
			getManagedBean().setVotFundDisplay(false);
			preLoadingGlAccount();
			//28718 - start
			if(getManagedBean().isGrouped() && !BpmOutcomeConst.BPM_VIEW.equalsIgnoreCase(getManagedBean().getButtonVal())) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_745089, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}			
			//28718 - end
		}
		if(selectedTabIndex==4){
			chargeLineValidationBeforeApproverList();
			getApproverList();
			initializeApproverGroup();
		}
	}
	
	/**
	 * Gets the order summary grand total.
	 *
	 * @return the order summary grand total
	 */
	private void getOrderSummaryGrandTotal() {
		BigDecimal orderSummaryTotalOrderAmt = null;
		BigDecimal grandOrderSummaryTotalOrderAmt=new BigDecimal(0);
		for(FlDeliveryAddress  deliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			orderSummaryTotalOrderAmt = new BigDecimal(0);
			for(FlFulfilmentItemAddr itemAddress : deliveryAddress.getFlFulfilmentItemAddr()){
				orderSummaryTotalOrderAmt = orderSummaryTotalOrderAmt.add(itemAddress.getOrderedAmt());
			}
			grandOrderSummaryTotalOrderAmt = grandOrderSummaryTotalOrderAmt.add(orderSummaryTotalOrderAmt);
		}
		getManagedBean().setOrderSummaryOrderedAmtGrandTotal(grandOrderSummaryTotalOrderAmt);
		
	}

	/**
	 * Update delivery date based on delivery Term and uomCode.
	 */
	public void updateDeliveryDate() {
		Date startDate = getManagedBean().getFulfilmentRequest().getStartDate();
		int deliveryTerm = 0;
		String uomCode = "";
		deliveryTerm = getManagedBean().getFulfilmentRequest().getDeliveryTerm();
		uomCode = getManagedBean().getFulfilmentRequest().getPmUom().getParameterCode();
		
		if(uomCode.equalsIgnoreCase(ParameterConst.DELIVERY_TERM_DAYS)){
			getManagedBean().setMaxDate(DateUtils.getEndDate(startDate, deliveryTerm,ParameterConst.DELIVERY_TERM_DAYS));
			getManagedBean().getFulfilmentRequest().setEndDate(DateUtils.getEndDate(startDate, deliveryTerm,ParameterConst.DELIVERY_TERM_DAYS));
		}else if(uomCode.equalsIgnoreCase(ParameterConst.DELIVERY_TERM_MONTHS)){
			getManagedBean().setMaxDate(DateUtils.getEndDate(startDate, deliveryTerm,ParameterConst.DELIVERY_TERM_MONTHS));
			getManagedBean().getFulfilmentRequest().setEndDate(DateUtils.getEndDate(startDate, deliveryTerm,ParameterConst.DELIVERY_TERM_MONTHS));
		}
		//update delivery address
		for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()) {
			flDeliveryAddress.setStartDate(getManagedBean().getFulfilmentRequest().getStartDate());
			flDeliveryAddress.setEndDate(getManagedBean().getFulfilmentRequest().getEndDate());
		}
	}
	
	/* #17087 Get current zonal state flag */
	public String getzonalTest() {
		long requestNoteId = getManagedBean().getFulfilmentRequest().getScPurchaseRequest().getRequestNote()
				.getRequestNoteId();
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		requestNoteReqDTO.setRequestNoteId(requestNoteId);

		RequestNoteResDTO requestNoteResDTO = getRequestNoteSO().getRequestNote(requestNoteReqDTO);
		ScRequestNote requestNotew = requestNoteResDTO.getRequestNoteDetail();

		zonalTest = Long.toString(requestNotew.getRequestNoteDtls().get(0).getPriceType().getParameterId());

		setzonalTest(zonalTest);
		return zonalTest;

	}

	
	/**
	 * update delivery address based on endDate change
	 */
	public void updateDeliveryEndDate() {
		for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()) {
			flDeliveryAddress.setEndDate(getManagedBean().getFulfilmentRequest().getEndDate());
		}
	}
	/**
	 * sorting  Order Summary List.
	 *
	 * @param deliveryAddressList the delivery address list
	 */
	public void sortOrderSummaryList(List<FlDeliveryAddress> deliveryAddressList) {
		//Creating Comparator
		if(null==deliveryAddressList || deliveryAddressList.isEmpty()){
			return;
		}
		for(FlDeliveryAddress flDeliveryAddress : deliveryAddressList) {
			sortItems(flDeliveryAddress.getFlFulfilmentItemAddr());
		}
    }
	/**
	 * sorting all the items showing in DelAddress and Item Tab
	 *
	 * @param flItemAddressList the fl item address list
	 */
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
	
	/**
	 * Sort delivery address list.
	 *
	 * @param flDelAddressList the fl del address list
	 */
	public void sortAddress(List<FlDeliveryAddress> flDelAddressList) {
		//Creating Comparator
		if(null==flDelAddressList || flDelAddressList.isEmpty()){
			return;
		}
		  Comparator<FlDeliveryAddress> comparator = new Comparator<FlDeliveryAddress>() {
	            @Override
	            public int compare(FlDeliveryAddress o1, FlDeliveryAddress o2) {
	                return new CompareToBuilder()
		                .append(o1.getFlAddress().getAddress1(), o2.getFlAddress().getAddress1())
		                .append(o1.getFlAddress().getAddress2(), o2.getFlAddress().getAddress2())
		                .append(o1.getFlAddress().getAddress3(), o2.getFlAddress().getAddress3())
		                .toComparison();
	            }
	        };
			Collections.sort(flDelAddressList, comparator);
    }
	
	/**
	 * get the Request Item Information based on RequestNoteItemId,SupplierId and PriceType.
	 *
	 * @param flItemAddr the fl item addr
	 */
	public void reqItemInfoList(FlFulfilmentItemAddr flItemAddr){
		getManagedBean().setItemName(flItemAddr.getScRequestItem().getItemName());
		getManagedBean().setItemDesc(flItemAddr.getScRequestItem().getItemDesc());
		if(null == flItemAddr.getScRequestItem().getQuantity()){
			getManagedBean().setQuantity(new Double(0));
		}else{
			getManagedBean().setQuantity(flItemAddr.getScRequestItem().getQuantity().doubleValue());
		}
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
		reqDTO.setPriceType(flItemAddr.getPriceType());
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
	
	/**
	 * Sort address type.
	 *
	 * @param addressTypeList the address type list
	 */
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
	
	/**
	 * chargeLine Panel enable if  checkbox selected
	 *
	 * @param e the e
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
					//24893 - start
					if(null != childData.getChargePTJOrg()) {
						getManagedBean().setChargePTJOrg(childData.getChargePTJOrg());
						System.out.println("Item Charge PTJ");
					} else {
						if(getManagedBean().getFulfilmentRequest().getStatus().getStatusId().equals(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION) 
								|| getManagedBean().getFulfilmentRequest().getStatus().getStatusId().equals(FulfilmentStatusConst.PR_PENDING_APPROVAL) 
								|| getManagedBean().getFulfilmentRequest().getStatus().getStatusId().equals(FulfilmentStatusConst.PR_PENDING_REVISION) 
								|| getManagedBean().getFulfilmentRequest().getStatus().getStatusId().equals(FulfilmentStatusConst.PR_PENDING_REVISION_APPROVAL)
								|| getManagedBean().getFulfilmentRequest().getStatus().getStatusId().equals(FulfilmentStatusConst.PR_PENDING_QUERY_VERIFICATION)) {
							getManagedBean().setChargePTJOrg(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile());
							System.out.println("PTJ Pembayar");
						} else {
							getManagedBean().setChargePTJOrg(getManagedBean().getFulfilmentRequest().getChargeOrgProfile());
							System.out.println("FL Request Charge PTJ");
						}					
					}
					//24893 - end
				}
		}//24893 - start 
		else if(null != selectedNodes && selectedNodes.length > 1) {
			getManagedBean().setChargePTJOrg(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile());
		}
		//24893 - end
	}
	
	/**
	 * Select all checkboxes.
	 * chargeLine Panel enable if  checkboxes selected
	 */
	public void selectAllCheckboxes(){
		if(getManagedBean().getChargeLineItemRoot().getChildren().size() > 1){
			onNodeUnSelect();
			getManagedBean().setChargePTJOrg(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile());//24893
		}
		getManagedBean().setSelectChargeLineRow(false);
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
			getManagedBean().setChargeLinePanel(false);
			getManagedBean().setSelectChargeLineRow(false);
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
			getManagedBean().getPmSubSetia().setFinancingTypeValue(null);
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
		getManagedBean().setVotFundCode("");
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
	 * Check vot lov.
	 */
	public void checkVotLov(BaseEntity entity){
		getManagedBean().setProgramLov(false);
		getManagedBean().setProjectLov(false);
		getManagedBean().setVotFundDisplay(true);
		if(null != getManagedBean().getPmVoteFund()){
			getManagedBean().setVotFundId(getManagedBean().getPmVoteFund().getVotFundId());
			if(!"".equalsIgnoreCase(getManagedBean().getPmVoteFund().getVotFundCode())){
				getManagedBean().getPmVoteFund().setVotFundCode("");
				getManagedBean().getPmVoteFund().getVotFundCode();
			}
		}
		PmVotFund pmVotFund = votFundCodeMap.get(getManagedBean().getVotFundId());
		getManagedBean().setVotFundCode(pmVotFund.getVotFundCode());
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
	 * Page navigation to FLDoc search page in PR.
	 *
	 * @return the string
	 * @throws IOException //36519
	 * 
	 */
	public String backToPervious() throws IOException{//36519
		if(getManagedBean().getBackToDocument() !=null){
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.getManagedBean().setRemarkList(getManagedBean().getPreviousRemarkList());
			flBB.getManagedBean().setAttachmentList(getManagedBean().getPreviousAttachmentList());
			flBB.getManagedBean().setHideRemarks(getManagedBean().isPrevHideRemarks());
			return getManagedBean().getBackToDocument();
		}
		// 36519-Start
		else {
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect(
							EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_APPLICATION_URL)
									+ "notifications");
			return null;
		}
		// 36519-End
	}
	
	/**
	 * Not remove address row.
	 */
	public void notRemoveAddressRow(){
		getManagedBean().setDeleteButtonHide(true);
		if(null !=getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			Iterator<FlDeliveryAddress> delAddressIterator=getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().iterator();
			while(delAddressIterator.hasNext()){
				FlDeliveryAddress  deliveryAddress=delAddressIterator.next();
				deliveryAddress.setSelectedAddressRowDelete(false);
			}
		}
	}
	
	/**
	 * Reset selected tree nodes.
	 */
	private void resetSelectedTreeNodes() {
		TreeNode root=getManagedBean().getChargeLineItemRoot();
		for(TreeNode node:root.getChildren()){
			FlFulfilmentItemAddr addr=(FlFulfilmentItemAddr) node.getData();
			addr.setSelectedChargeItem(false);
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
		//#28718 - Start
		getManagedBean().setTriggerPRErrorMsg(Boolean.FALSE);
		boolean isTriggerNewPRNeeded = false;
		if(BpmOutcomeConst.BPM_SUBMIT.equals(outcome) || BpmOutcomeConst.BPM_APPROVE.equals(outcome)){
			isTriggerNewPRNeeded = checkTriggeredNewPR();
		}
		//#28718 - End
		//29728 - start
		if(BpmOutcomeConst.BPM_SUBMIT.equals(outcome) || BpmOutcomeConst.BPM_SAVE.equals(outcome) 
				|| BpmOutcomeConst.BPM_APPROVE.equals(outcome)){			
			if(!getManagedBean().isBranchNeedToBeChanged() && getManagedBean().getFulfilmentRequest().getFlSupplierDtl().getBranchCode() != null 
					&& isSelectedBranchDeleted(getManagedBean().getFulfilmentRequest().getFlSupplierDtl().getBranchCode(), 
							getManagedBean().getFulfilmentRequest().getSmSupplier().getSupplierId())) {
				getManagedBean().setSelectedTabIndex(0);
				return null;
			} 
			if(getManagedBean().isBranchNeedToBeChanged()){
				FacesContext.getCurrentInstance().addMessage("delBranchMsg", new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_745087, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				getManagedBean().setSelectedTabIndex(0);
				return null;
			}
		}
		//29728 - end
		if (!isFlFulfilmentRequisitioner() &&  (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome))
				|| (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome))) {
			validateFulfilmentRequest(outcome);
			StringBuilder sb = new StringBuilder();
			sb.append(getManagedBean().getFulfilmentRequest().getDocNo()+"|");
			sb.append(getOrderAmount()+"|");
			sb.append(outcome+"|");
			sb.append(getFacesScopes().getUserSessionBean().getIdentificationNo());
			getManagedBean().setTextBeforeSign(sb.toString());
			// set action
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			//28718 - start
			if(isTriggerNewPRNeeded) {
				getManagedBean().setTriggerPRErrorMsg(isTriggerNewPRNeeded);
				return null;
			}
			//28718 - end
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		}else{
			if(BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome)
					|| BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)){
				getManagedBean().setValidated(Boolean.FALSE);
			}else if (BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)){
				validateFulfilmentRequest(outcome);
				RequestContext.getCurrentInstance().execute("cancelConfirmDlg.show()");
				return null;
			}
			//#28718 - Start
			if(isTriggerNewPRNeeded) {
				getManagedBean().setTriggerPRErrorMsg(isTriggerNewPRNeeded);
				return null;
			}
			//#28718 - End	
			return fulfillmentRequestAction(outcome);				
		}
	}
	
	/**
	 * Gets the order amount.
	 *
	 * @return the order amount
	 */
	private double getOrderAmount() {
		BigDecimal totalOrderAmount = BigDecimal.ZERO;
		for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()) {
			for(FlFulfilmentItemAddr itemAddr : flDeliveryAddress.getFlFulfilmentItemAddr()) {
				totalOrderAmount = totalOrderAmount.add(itemAddr.getOrderedAmt());
			}
		}
		return totalOrderAmount.doubleValue();
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
		return fulfillmentRequestAction(getManagedBean().getButtonAction());
	}
	
	/**
	 * Enable auto update.
	 */
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}
	
	/**
	 * Method do validations Before save purchase request details
	 *
	 * @param outcome the outcome
	 */
	private void validateFulfilmentRequest(String outcome){		
		IFulfilmentRequestSO fulfilmentRequestSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			getManagedBean().setAutoUpdate(Boolean.TRUE);
			if(BpmOutcomeConst.BPM_SUBMIT.equals(outcome) || BpmOutcomeConst.BPM_APPROVE.equals(outcome)){
				if(getManagedBean().isPidValidityExpired()) {
					if(isFlFulfilmentRequisitioner()) {
						throw new EpException(MessageConst.ERROR_740040);
					} else {
						throw new EpException(MessageConst.ERROR_740041);
					}
				}
				if(getManagedBean().getFulfilmentRequest().getTitle() == null || getManagedBean().getFulfilmentRequest().getTitle().trim().length() < 1){
					getManagedBean().setSelectedTabIndex(0);
					throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.fl.title",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
				}
				if(BpmOutcomeConst.BPM_SUBMIT.equals(outcome) && getManagedBean().getSelectedApprover() == null){
					getManagedBean().setSelectedTabIndex(4);
					throw new EpException(MessageConst.ERROR_750010);
				}
				if(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses().size()==0) {
					getManagedBean().setSelectedTabIndex(1);
					getManagedBean().setEmailValidate(true);
					throw new EpException(MessageConst.ERROR_100023, new String[]{ResourceBundleUtils.getMessage("lbl.fl.delivery.address",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
				}else{
					checkReceivingOfficerList();
				}
				validateChargelineLimit(outcome);
				if(!getManagedBean().isDeclarationCheck()){
					getManagedBean().setSelectedTabIndex(0);
					throw new EpException(MessageConst.ERROR_745071);
				}
				// #18648
				if(getManagedBean().getFulfilmentRequest().getStartDate()==null && getManagedBean().getFulfilmentRequest().getEndDate()!=null) {
					getManagedBean().getFulfilmentRequest().setStartDate(DateUtils.getStartDate(getManagedBean().getFulfilmentRequest().getEndDate(), getManagedBean().getFulfilmentRequest().getDeliveryTerm(), getManagedBean().getFulfilmentRequest().getPmUom().getParameterCode()));
					updateDeliveryDate();
				}
				validateDeliveryAddress(outcome);//29401
			} else if(BpmOutcomeConst.BPM_REVISE.equals(outcome) || BpmOutcomeConst.BPM_CANCEL.equals(outcome) || BpmOutcomeConst.BPM_REJECT.equals(outcome)){
				if(flBB.getManagedBean().getRemarkList().get(0).getRemark()==null || flBB.getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
					getManagedBean().setSelectedTabIndex(0);
					throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.txt.remarks",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
				}
				if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)){
					IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
					UserReqDTO userReqDTO = new UserReqDTO();
					userReqDTO.setUserId(getManagedBean().getFulfilmentRequest().getCreatedBy());
					UserResDTO userResDTO = userSO.retrieveUserByUserId(userReqDTO);
					if(userResDTO.getPmUser().getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE){
						throw new EpException(MessageConst.ERROR_740020, new String[]{ResourceBundleUtils.getMessage("lbl.fl.requisitioner",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
					}
				}
			}
			
			if(BpmOutcomeConst.BPM_APPROVE.equals(outcome)){
				IRuleSO ruleSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
				RuleReqDTO ruleReqDTO = new RuleReqDTO();
				RuleResDTO roleResDTO = null;
				ruleReqDTO.setRuleType(RuleTypeConst.LO_PO_ACKNOWLEDGMENT_PERIOD); //#25147
				roleResDTO = ruleSO.getRuleValuesByRuleType(ruleReqDTO);
				if(roleResDTO.isSuccessful() && roleResDTO.getRuleValueList()!=null && roleResDTO.getRuleValueList().size()>0) {
					getManagedBean().setTaskDeadLine(roleResDTO.getRuleValueList().get(0).getRuleValue());
				} else {
					getManagedBean().setTaskDeadLine("0"); //temporary #25147
				}
				
				for(FlDeliveryAddress deliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
					for(FlFulfilmentItemAddr itemAddr : deliveryAddress.getFlFulfilmentItemAddr()){
						if(itemAddr.getCmItem() == null || itemAddr.getCmItem().getExtensionCode() == null || itemAddr.getCmItem().getItemId().longValue() == 1){
							throw new EpException(MessageConst.ERROR_740053);
						}
					}
				}
			}
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flFulfilmentRequest = getManagedBean().getFulfilmentRequest();
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
			reqDTO.setFlFulfilmentRequest(flFulfilmentRequest);
			reqDTO.setContractRequest(Boolean.FALSE);
			reqDTO.setOutcome(outcome);
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setApproverMap(getManagedBean().getApproverMap());
			reqDTO.setSelectedApprover(getManagedBean().getSelectedApprover());

			//Bug #25244
			if(!(BpmOutcomeConst.BPM_REJECT.equals(outcome) || BpmOutcomeConst.BPM_CANCEL.equals(outcome))
					&& getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId() != null){
				reqDTO.setGroupId(getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId());
			}
			
			reqDTO.setApproverLimit(calculatesTotalOrderedAmt());
			reqDTO.setPmOrgProfile(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile());
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());
			fulfilmentRequestSO.validateFulfilmentRequest(reqDTO);
			
		}
		//CR-25147: Static Code Analysis - shaqimi start
		finally{
			fulfilmentRequestSO = null;
		}
		//CR-25147L Static Code Analysis - shaqimi end
	}
	
	/**
	 * Charge line validation before click the approver list menu.
	 */
	public void chargeLineValidationBeforeApproverList(){
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
	
	/**
	 * The Class UnitOfMeasurementPagingDataModel.
	 */
	private class UnitOfMeasurementPagingDataModel extends PagingDataModel<PmUom> {
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new unit of measurement paging data model.
		 */
		public UnitOfMeasurementPagingDataModel() {
			super("measurementSR");
		}

		/* (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
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
	
	/**
	 * Gets the measurement search bean data.
	 *
	 * @param searchBean the search bean
	 * @return the measurement search bean data
	 */
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

	/**
	 * Gets the unit of measurement data model.
	 *
	 * @return the unit of measurement data model
	 */
	public IPagingDataModel<PmUom> getUnitOfMeasurementDataModel() {
		return unitOfMeasurementDataModel;
	}

	/**
	 * Sets the unit of measurement data model.
	 *
	 * @param unitOfMeasurementDataModel the new unit of measurement data model
	 */
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
	 * Checks if is select all checkbox.
	 *
	 * @return the selectAllCheckbox
	 */
	public boolean isSelectAllCheckbox() {
		return selectAllCheckbox;
	}

	/**
	 * Sets the select all checkbox.
	 *
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
	 * Back to document.
	 *
	 * @return the string
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
	 * Comapre PTJ group 
	 * If the current charge PTJ group is different, then chargeline will be initialize
	 *
	 */
	public void comparePtjGroup(BaseEntity entity){
		IOrgProfileSO profileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest().getChargeOrgProfile().getOrgProfileId());
		OrgProfileResDTO resDTO = profileSO.retrieveJabatanByOrgProfileId(reqDTO);
		Long newKumpOrgId = 0L;//TODO remove after data are all pacthed
		if(resDTO.getPmOrgProfile() != null){
			newKumpOrgId = resDTO.getPmOrgProfile().getOrgProfileId();
		}
		if(newKumpOrgId != getManagedBean().getCurrentChargePtjGroup().getOrgProfileId()){
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
		if(resDTO.getPmOrgProfile() != null){
			getManagedBean().setCurrentChargePtjGroup(resDTO.getPmOrgProfile());
		}
	}
	
	/**
	 * Get preparef PTJ condition for construct the LOV listing
	 * @return String
	 */
	public String getPreparedPTJCondition() {
		String condition = OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value())+"^"+
				retrieveMinistryOrgProfile(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId()).getOrgProfileId();
		return condition;
	}
	
	/**
	 * Calculates total ordered amt.
	 *
	 * @return the big decimal
	 */
	private BigDecimal calculatesTotalOrderedAmt(){
		BigDecimal totalAmt = new BigDecimal(0);
		if(getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses() != null){
			for(FlDeliveryAddress  deliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
				for(FlFulfilmentItemAddr itemAddress : deliveryAddress.getFlFulfilmentItemAddr()){
					totalAmt = totalAmt.add(itemAddress.getOrderedAmt());
				}
			}
		}
		return totalAmt;
	}
	
	/**
	 * Gets the approver list.
	 *
	 * @return the approver list
	 */
	private void getApproverList(){
		if(getManagedBean().getFulfilmentRequest().getPmUserGroup()==null || getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId()==null) {
			getManagedBean().getApproverIdList().clear();
			return;
		}		
		IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		reqDTO.setGroupId(getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId());
		reqDTO.setApproverLimit(calculatesTotalOrderedAmt());
		try {
			resDTO = requestSO.retrieveApproverListByGroupId(reqDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
        getManagedBean().getApproverIdList().clear();
        getManagedBean().getApproverGroupUserList().clear();
        getManagedBean().getApproverGroupUserList().addAll(resDTO.getApproverGroupUserList());
		for(PmUserGroupUser groupUser : resDTO.getApproverGroupUserList()) {
			getManagedBean().getApproverIdList().add(groupUser.getPmUser().getUserId());
		}
	}
	
	/**
	 * Sets the gst price.
	 *
	 * @param flItemAddr the fl item addr
	 * @param itemPrice the item price
	 */
	private void setGstPrice(FlFulfilmentItemAddr flItemAddr,Double itemPrice) {
		if(getManagedBean().getFulfilmentRequest().getGstRate() == null){
			getManagedBean().getFulfilmentRequest().setGstRate(BigDecimal.ZERO);
		}
		if(flItemAddr.isGst() && getManagedBean().getFulfilmentRequest().getGstEffectiveDate()!=null && 
			getManagedBean().getFulfilmentRequest().getGstEffectiveDate().getTime() <= DateUtils.getCurrentDateWithoutTime().getTime()){
			flItemAddr.setGstAmount(new BigDecimal(itemPrice).multiply(getManagedBean().getFulfilmentRequest().getGstRate()).divide(new BigDecimal(100), CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP));
		} else {
			flItemAddr.setGstAmount(BigDecimal.ZERO);
		}
		flItemAddr.setGstUnitPrice(flItemAddr.getUnitPrice().add(flItemAddr.getGstAmount()));
	}
	
	/**
	 * Check receiving officer list.
	 */
	private void checkReceivingOfficerList(){
		IAddressSO addressSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
		AddressReqDTO reqDTO = new AddressReqDTO();
		for(FlDeliveryAddress delAddr : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			Long addressId = delAddr.getPmAddressId();
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
				throw new EpException(MessageConst.ERROR_745077, new String[]{getFullAddress(delAddr.getFlAddress())});
			}
		}
		
	}
	
	/**
	 * Retrieve a list of approver
	 *
	 */
	public SelectItem[] getApproverListDropDown() {
		SelectItem[] items = null;
		if(getManagedBean().getFulfilmentRequest().getPmUserGroup()==null) {
			getManagedBean().getFulfilmentRequest().setPmUserGroup(new PmUserGroup());
		}	
		if(getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId() == null){
			return items;
		}
		IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		reqDTO.setGroupId(getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId());
		reqDTO.setApproverLimit(calculatesTotalOrderedAmt());
		try {
			resDTO = requestSO.retrieveApproverListByGroupId(reqDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
        getManagedBean().getApproverIdList().clear();
        items = new SelectItem[resDTO.getApproverGroupUserList().size() + 1];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				getFacesScopes().getUserLocale()));
		
		int i = 1;
		for(PmUserGroupUser groupUser : resDTO.getApproverGroupUserList()) {
			items[i++] = new SelectItem(groupUser.getPmUser().getUserId(), groupUser.getPmUser().getUserName());
			getManagedBean().getApproverIdList().add(groupUser.getPmUser().getUserId());
		}
		return items;
	}
	/**
	 * Navigate to RequestNote view page.
	 * @param docId
	 * @return String gotoRequestNotePage
	 */
	public void navigateRequestNotePage(Long requestNoteId){
		RequestNoteBackingBean reqNoteBB = new RequestNoteBackingBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		reqNoteBB.getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId());
		// start - #25790
		reqNoteBB.getManagedBean().setTrackingDairyView(Boolean.TRUE);
		reqNoteBB.viewRequestNote(requestNoteId);
		// end - #25790
	}
	
	/**
	 * Check pr threshold per item.
	 */
	private void checkPRThresholdPerItem(){
		IRuleSO ruleSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
		IFlItemSummarySO itemSummSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFlItemSummarySO.class);
		Map<Long, ScRequestItem> requestItemMap = new HashMap<Long, ScRequestItem>();
		Map<Long, BigDecimal> itemAmtMap = new HashMap<Long, BigDecimal>();
		for(FlDeliveryAddress delAddr : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr item : delAddr.getFlFulfilmentItemAddr()){
				if(itemAmtMap.containsKey(item.getScRequestItem().getRequestNoteItemId())){
					itemAmtMap.put(item.getScRequestItem().getRequestNoteItemId(), itemAmtMap.get(item.getScRequestItem().getRequestNoteItemId()).add(new BigDecimal(item.getUnitPrice().doubleValue()*item.getOrderedQty().doubleValue())));
				}else{
					// Skip validation if item not yet codified.
					if(item.getCmItem()==null) {
						continue;
					}
					itemAmtMap.put(item.getScRequestItem().getRequestNoteItemId(), new BigDecimal(item.getUnitPrice().doubleValue()*item.getOrderedQty().doubleValue()));
					requestItemMap.put(item.getScRequestItem().getRequestNoteItemId(), item.getScRequestItem());
				}
			}
		}
		double thresoldAmt = 0;
		for(Map.Entry<Long, BigDecimal> map : itemAmtMap.entrySet() ){
			FlItemSummaryReqDTO summReq = new FlItemSummaryReqDTO();
			summReq.setFlRequestId(getManagedBean().getFulfilmentRequest().getFulfilmentReqId());
			summReq.setOrgProfileId(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId());
			summReq.setSupplierId(getManagedBean().getFulfilmentRequest().getSmSupplier().getSupplierId());
			summReq.setYear(getManagedBean().getFulfilmentRequest().getFinancialYear());
			summReq.setItemId(requestItemMap.get(map.getKey()).getCmItem().getItemId());
			summReq.setExtensionCode(requestItemMap.get(map.getKey()).getCmItem().getExtensionCode().substring(0, 8));
			//BigDecimal sumAmt = itemSummSO.retrievePerItemPerPtjPerYear(summReq);
			BigDecimal sumAmt = itemSummSO.retrievePerItemPerPtjPerYearByExtensionCode(summReq);
			
			sumAmt = map.getValue().add(sumAmt);
			if(sumAmt != null){
				RuleReqDTO ruleReqDTO = new RuleReqDTO();
				//#26047 -- start
				ScRequestNote requestNote = getManagedBean().getFulfilmentRequest().getScPurchaseRequest().getRequestNote();
				DecimalFormat amountDF = new DecimalFormat(new CommonUtils().getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_INTEGER));
				if(requestNote.getQuote()!=null && requestNote.getQuote().isPanel()) {
					ruleReqDTO.setRuleType(RuleTypeConst.PANEL_THRESHOLD);
				} else {
					ruleReqDTO.setRuleType(RuleTypeConst.ITEM_THRESHOLD);
				}
				RuleResDTO roleResDTO = ruleSO.getRuleValuesByRuleType(ruleReqDTO);
				if(roleResDTO.isSuccessful() && roleResDTO.getRuleValueList()!=null && roleResDTO.getRuleValueList().size()>0) {
					thresoldAmt = Double.parseDouble(roleResDTO.getRuleValueList().get(0).getRuleValue());
				} else {
					throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(ruleReqDTO.getRuleType())});
				}
				
				//#27377 - start
				ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT,ICodificationSO.class);
				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setItemId(requestItemMap.get(map.getKey()).getCmItem().getItemId());
				CodificationResDTO cResDTO = unspscSO.findUnspcObj(reqDTO);
				
				CmUnspsc cmUnspsc = new CmUnspsc();
				cmUnspsc = cResDTO.getUnspscObj();
				
				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				//#27377 - End
				
				log.info("SumAmt: " + sumAmt);
				log.info("ThresholdAmt: " + thresoldAmt);
				if(sumAmt.doubleValue() > thresoldAmt){
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.ERROR_740004, 
									new String[]{CommonConstants.DEFAULT_LANGUAGE_CODE.equals(languageCode) ? cmUnspsc.getUnspscTitleEng() : cmUnspsc.getUnspscTitle() , StringUtils.EMPTY, //#27377
											amountDF.format(thresoldAmt)}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
									//#26047 -- end
				}
			}
		}
	}
	
	/**
	 * The method used for generating a list of available ministry
	 * for searching charged PTJ as selection.
	 * 
	 * @return the selection list of ministry
	 */
	public SelectItem[] getMinistryDropdown() {		
		SelectItem[] items = null;
		
		try {
		    int i = 1;
		    IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByMinistry(reqDTO);
		    
		    List<PmOrgProfile> list = resDTO.getOrgProfileList();
		    items = new SelectItem[list.size() + 1];
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
		    		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			for (PmOrgProfile orgProfile : list) {
			  items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	/**
	 * The method used for generating a list of available jabatan
	 * for searching charged PTJ as selection.
	 * 
	 * @return the selection list of jabatan
	 */
	public SelectItem[] getJabatanDropdown() {		
		SelectItem[] items = null;
		
		try {
		    int i = 1;
		    IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
		    		ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		    OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setParentOrgProfileId(getManagedBean().getMinistryProfileId());
			reqDTO.setOrgCode(OrganizationType.DEPARTMENT.value());
			OrgProfileResDTO resDTO = orgProfileSO.retrieveChildDDListByParentOrgProfileId(reqDTO);
			
		    List<Object[]> list = resDTO.getOrgProfileDDList();
		    if(list==null) {
		    	list = new ArrayList<Object[]>();
		    }
		    items = new SelectItem[list.size() + 1];
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
		    		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			
		    for(Object[] object : list){
			  items[i++] = new SelectItem(Long.parseLong(object[0].toString()), object[1].toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	/**
	 * The method used for searching the charged PTJ.
	 */
	public void searchChargedPtjDetails() {
		try {
			chargedPtjDataModel.reloadData();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), "contract search failed."));
		}
	}
	
	/**
	 * The method used to reset the search criteria for charged PTJ.
	 */
	public void resetChargedPtjDetails() {
		getManagedBean().setMinistryProfileId(retrieveMinistryOrgProfile(
				getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgProfileId()).getOrgProfileId());
		
		getManagedBean().setJabatanProfileId(null);
		getManagedBean().setPtjCodeName(ContractConst.PTJ_CODE);		
		getManagedBean().setPtjCodeNameSearchValues(null);
		chargedPtjDataModel.resetData();
	}
	
	//start added for #24353 
	/**
	 * The method used for searching the PM Address Type.
	 */
	public void searchPMAddressTypeDetails() {
		try {
			pmAddressTypeDataModel.reloadData();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), "PM Address Type search failed."));
		}
	}
	
	/**
	 * The method used to reset the search criteria for PM Address Type.
	 */
	public void resetPMAddressTypeDetails() {
		getManagedBean().setStateId(null);
		getManagedBean().setDistrictCode(null);
		getManagedBean().setPtjCodeNameNew(ContractConst.PTJ_CODE_NEW);
		getManagedBean().setPtjCodeNameSearchValuesNew(null);
		pmAddressTypeDataModel.resetData();
	}
	
	public void initPMAddressTypeDetails() {
		getManagedBean().setStateId(null);
		getManagedBean().setDistrictCode(null);
		getManagedBean().setPtjCodeNameNew(ContractConst.PTJ_CODE_NEW);
		getManagedBean().setPtjCodeNameSearchValuesNew(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().getOrgCode());
		pmAddressTypeDataModel.reloadData();
	}
	//end added for #24353
	
	
	/**
	 * Pagination for available charged PTJ search list
	 */
	private class chargedPtjPagingDataModel extends PagingDataModel<PmOrgProfile> {
		private static final long serialVersionUID = 1L;

		public chargedPtjPagingDataModel() {
			super("tableChargePtj", false);
		}
		
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
	 * .biz.bean.search.ISearchBean)
	 */
	@Override
	public ISearchResultBean<PmOrgProfile> pageChangeListener(ISearchBean<PmOrgProfile> searchBean) {			
		try {
			// Filter By - parameterType
			searchBean.getDataFilterBeans().add(new SearchFilterBean("orgTypeCode.pmParameterType.parameterType",SearchOperator.OPERATOR_EQUALS.value(),ParameterTypeConst.ORGANISATION_TYPES));
		
			// Filter By - Org Type
			searchBean.getDataFilterBeans().add(new SearchFilterBean("orgTypeCode.parameterCode",SearchOperator.OPERATOR_EQUALS.value(),OrganizationType.DIVISION.value()));
			
			
			// Filter By - Ministry Profile Id
			Long ministryProfileId = getManagedBean().getMinistryProfileId();
			if (ministryProfileId == null) {
				ministryProfileId = 0L;
			}
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
							SearchOperator.OPERATOR_EQUALS.value(),
							ministryProfileId));
			
			if (getManagedBean().getJabatanProfileId() != null) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()),
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getJabatanProfileId()));
			}
			
			Date currentDate = DateUtils.getCurrentDateWithoutTime();
			// Filter By - Org effDate
			searchBean.getDataFilterBeans()
					.add(new SearchFilterBean("pmOrgValidity.effDate",SearchOperator.OPERATOR_LESS_EQUAL.value(), currentDate));

			// Filter By - Org expDate
			searchBean.getDataFilterBeans().add(
					new SearchMultiFilterBean(new SearchFilterBean(
							"pmOrgValidity.expDate",
							SearchOperator.OPERATOR_GREATER_EQUAL.value(),
							currentDate), new SearchFilterBean(
							"pmOrgValidity.expDate",
							SearchOperator.OPERATOR_IS_NULL.value(), null,
							SearchCondition.CONDITION_OR.value())));
			
			if (getManagedBean().getPtjCodeName() != null
					&& (getManagedBean().getPtjCodeNameSearchValues() != null && !getManagedBean().getPtjCodeNameSearchValues().equalsIgnoreCase(""))) {
				if (getManagedBean().getPtjCodeName().equalsIgnoreCase(ContractConst.PTJ_CODE)) {
					SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgCode", SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getPtjCodeNameSearchValues());
					filterBean.setCaseInsensitive(true);
				    searchBean.getDataFilterBeans().add(filterBean);
				
				} else if (getManagedBean().getPtjCodeName().equalsIgnoreCase(ContractConst.PTJ_NAME)) {
					SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgName", SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getPtjCodeNameSearchValues());
					filterBean.setCaseInsensitive(true);
				    searchBean.getDataFilterBeans().add(filterBean);
				}
			}
			
			IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileSearchBean(searchBean);
			OrgProfileResDTO resDTO = orgProfileSO.searchOrgProfile(reqDTO);
			if (resDTO.isSuccessful()) {
				 return resDTO.getOrgProfileResultBean();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
	
	//start added for #24353
	/**
	 * Pagination for available new PMAddressType search list
	 */
	private class pmAddressTypePagingDataModel extends PagingDataModel<PmAddressType> {
		private static final long serialVersionUID = 1L;

		public pmAddressTypePagingDataModel() {
			super("tableChargePtjNew2", false);
		}
		
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
	 * .biz.bean.search.ISearchBean)
	 */
	@Override
	public ISearchResultBean<PmAddressType> pageChangeListener(ISearchBean<PmAddressType> searchBean) {			
		IAddressSO addressSO;
		AddressResDTO resDto;

		try {
			AddressReqDTO addressReqDTO = new AddressReqDTO();
			addressReqDTO
					.setSearchAddressBean(new SearchBean<PmAddressType>());
			
			// Filter By - parameterType
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmOrgProfile.orgTypeCode.pmParameterType.parameterType",SearchOperator.OPERATOR_EQUALS.value(),ParameterTypeConst.ORGANISATION_TYPES));
			
			// Filter By - Org Type
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmOrgProfile.orgTypeCode.parameterCode",SearchOperator.OPERATOR_EQUALS.value(),OrganizationType.DIVISION.value()));
			
			Long ministryProfileId = getManagedBean().getMinistryProfileId();
			if (ministryProfileId == null) {
				ministryProfileId = 0L;
			}
			
			//ptjlist by ministryid
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmOrgProfile.parentOrgProfile.parentOrgProfile.parentOrgProfile.orgProfileId",SearchOperator.OPERATOR_EQUALS.value(),ministryProfileId));
			
			Long stateId = getManagedBean().getStateId();
			if (stateId!=null) {
				SearchFilterBean ptjSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_EQUALS.value());
				ptjSearchBean.setSearchField("pmAddress.pmDistrict.pmState.stateId");
				ptjSearchBean.setSearchValue(stateId);
				searchBean.getDataFilterBeans().add(ptjSearchBean);
			}
			
			String districtCode = getManagedBean().getDistrictCode();
			if (districtCode!=null) {
				SearchFilterBean ptjSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_EQUALS.value());
				ptjSearchBean.setSearchField("pmAddress.pmDistrict.districtCode");
				ptjSearchBean.setSearchValue(districtCode);
				searchBean.getDataFilterBeans().add(ptjSearchBean);
			}
			
			if (getManagedBean().getPtjCodeNameNew() != null
					&& (getManagedBean().getPtjCodeNameSearchValuesNew() != null && !getManagedBean().getPtjCodeNameSearchValuesNew().equalsIgnoreCase(""))) {
				if (getManagedBean().getPtjCodeNameNew().equalsIgnoreCase(ContractConst.PTJ_CODE_NEW)) {
					SearchFilterBean filterBean = new SearchFilterBean("pmOrgProfile.pmOrgValidity.orgCode", SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getPtjCodeNameSearchValuesNew());
					filterBean.setCaseInsensitive(true);
				    searchBean.getDataFilterBeans().add(filterBean);
				
				} else if (getManagedBean().getPtjCodeNameNew().equalsIgnoreCase(ContractConst.PTJ_NAME_NEW)) {
					SearchFilterBean filterBean = new SearchFilterBean("pmOrgProfile.pmOrgValidity.orgName", SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getPtjCodeNameSearchValuesNew());
					filterBean.setCaseInsensitive(true);
				    searchBean.getDataFilterBeans().add(filterBean);
				}
			}

			addressReqDTO.setSearchAddressBean(searchBean);

			addressSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
 
			resDto = addressSO.searchPTJDeliveryAddressAsPtjPrepared(addressReqDTO);

			if (resDto != null
					&& !resDto.getSearchAddressResultBean().getResultList()
							.isEmpty()) {

				return resDto.getSearchAddressResultBean();
			}

		} finally {
			addressSO=null;
		}
		return null;
	}
}
	
	public PmAddressType retrieveDefaultPmAddressType(String orgCode) {
		ISearchBean<PmAddressType> searchBean = new SearchBean<PmAddressType>();
		IAddressSO addressSO;
		AddressResDTO resDto;

		try {
			AddressReqDTO addressReqDTO = new AddressReqDTO();
			addressReqDTO
					.setSearchAddressBean(new SearchBean<PmAddressType>());
			
			// Filter By - parameterType
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmOrgProfile.orgTypeCode.pmParameterType.parameterType",SearchOperator.OPERATOR_EQUALS.value(),ParameterTypeConst.ORGANISATION_TYPES));
			
			// Filter By - Org Type
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmOrgProfile.orgTypeCode.parameterCode",SearchOperator.OPERATOR_EQUALS.value(),OrganizationType.DIVISION.value()));
			
			Long ministryProfileId = getManagedBean().getMinistryProfileId();
			if (ministryProfileId == null) {
				ministryProfileId = 0L;
			}
			
			//ptjlist by ministryid
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmOrgProfile.parentOrgProfile.parentOrgProfile.parentOrgProfile.orgProfileId",SearchOperator.OPERATOR_EQUALS.value(),ministryProfileId));

			
			//filter by orgcode
			SearchFilterBean filterBean = new SearchFilterBean("pmOrgProfile.pmOrgValidity.orgCode", SearchOperator.OPERATOR_LIKE.value(), orgCode);
			filterBean.setCaseInsensitive(true);
		    searchBean.getDataFilterBeans().add(filterBean);

			addressReqDTO.setSearchAddressBean(searchBean);

			addressSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
 
			resDto = addressSO.searchPTJDeliveryAddressAsPtjPrepared(addressReqDTO);

			if (resDto != null
					&& !resDto.getSearchAddressResultBean().getResultList()
							.isEmpty()) {

				List<PmAddressType> pmAddressResultList = resDto.getSearchAddressResultBean().getResultList();
				PmAddressType retrievedPmAddressType = resDto.getSearchAddressResultBean().getResultList().get(0);
				return retrievedPmAddressType;
			}

		} finally {
			addressSO=null;
		}
		return null;
	}
	//end added for #24353
	
	/**
	 * @return the chargedPtjDataModel
	 */
	public IPagingDataModel<PmOrgProfile> getChargedPtjDataModel() {
		return chargedPtjDataModel;
	}

	/**
	 * @param chargedPtjDataModel the chargedPtjDataModel to set
	 */
	public void setChargedPtjDataModel(
			IPagingDataModel<PmOrgProfile> chargedPtjDataModel) {
		this.chargedPtjDataModel = chargedPtjDataModel;
	}

	
	public void addChargedPtjDetails() {
		getManagedBean().getFulfilmentRequest().setChargeOrgProfile(selectedPmOrgProfile);
		//24893 - start
		getManagedBean().setChargePTJOrg(selectedPmOrgProfile);
		Long jabatanOrgProfileId = -1l;
		PmOrgProfile jabatan = retrieveJabatanOrgProfile(getManagedBean().getChargePTJOrg().getOrgProfileId());
		if(jabatan!=null) {
			jabatanOrgProfileId = jabatan.getOrgProfileId();
		}			
		getVotFundList(jabatanOrgProfileId);
		getManagedBean().setVotFundCode(null);
		getManagedBean().setVotFundId(null);
		//24893 - end
		selectedPmOrgProfile = null;
	}
	
	//start added for #24353 
	public void addChargedPmAddressType() {
		getManagedBean().getFlManualAddress().setReceivingGrp(selectedPmAddressType);
		getManagedBean().getFlManualAddress().setSelectedPtjCodeNameValue(getManagedBean().getFlManualAddress().getReceivingGrp().getPmOrgProfile().getPmOrgValidity().getOrgCode() 
				+ " " + getManagedBean().getFlManualAddress().getReceivingGrp().getPmOrgProfile().getPmOrgValidity().getOrgName()
				+ "\n\n" + getManagedBean().getFlManualAddress().getReceivingGrp().getPtjAddress());
		getManagedBean().getFlManualAddress().setNewRecord(true);
		selectedPmAddressType = null;
	}
	//end added for #24353
	
	
	/**
	 * @return the selectedPmOrgProfile
	 */
	public PmOrgProfile getSelectedPmOrgProfile() {
		return selectedPmOrgProfile;
	}
	/**
	 * @param selectedPmOrgProfile the selectedPmOrgProfile to set
	 */
	public void setSelectedPmOrgProfile(PmOrgProfile selectedPmOrgProfile) {
		this.selectedPmOrgProfile = selectedPmOrgProfile;
	}
	
	/**
	 * Retrieve purchase request by fulfilment request id and display in UI
	 *
	 */
	public void viewPurchaseRequest() {
		IFulfilmentRequestSO fulfilmentRequestSO =(IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String fulfilmentReqId = originalServletRequest.getParameter("flReqId");
		
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		if(fulfilmentReqId!=null) {
			reqDTO.setFlRequestId(Long.valueOf(fulfilmentReqId));
			reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			reqDTO.setDocType(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
			renewSesionObject();
			initializePRBeans();
			try {
				resDTO = fulfilmentRequestSO.retrieveFulfilmentRequest(reqDTO);
			} catch (EpException e) {
				handleException(e);
				return;
			}finally{
				fulfilmentRequestSO = null;
			}
			
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			getManagedBean().setSelectedTabIndex(0);
			getManagedBean().setButtonVal(BpmOutcomeConst.BPM_VIEW);//35357
//			getManagedBean().setTaskId(BpmOutcomeConst.BPM_VIEW);//for the purpose of hide the back button.//36519
			getManagedBean().setDocProcess(FulfilmentConst.DOC_TYPE_PR);
			getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			getManagedBean().setPmUser(reqDTO.getPmUser());
			getManagedBean().setSubmitButtonStatus(false);
			getManagedBean().setFlDocId(fulfilmentReqId);
			getManagedBean().setFlDocMaintain(true);
			
			setFulfilmentRequestField(resDTO);
			getManagedBean().setBackToDocument(null);
			flBB.getManagedBean().setHideRemarks(true);
			getManagedBean().setDeclarationCheck(true);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "prRenderer?faces-redirect=true");
		}
	}
	
	private void validateChargelineLimit(String action){
		Map<String, Integer> fulfilmentItemMap = new HashMap<String, Integer>();
		int countChargelineLimit = 0;
		for(FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddr : flDeliveryAddress.getFlFulfilmentItemAddr()){
				if(itemAddr.getOrderedAmt().doubleValue() > 0 && itemAddr.getOrderedQty().doubleValue() > 0){
					if(fulfilmentItemMap.get(itemAddr.getScRequestItem().getRequestNoteItemId()+ " "+itemAddr.getGstUnitPrice())==null) {
						countChargelineLimit = countChargelineLimit + 1;
						fulfilmentItemMap.put(itemAddr.getScRequestItem().getRequestNoteItemId()+ " "+itemAddr.getGstUnitPrice(), countChargelineLimit);
					} 
				}
			}
		}
		String gfmasEnable = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.GFMAS_ENABLED);
		if(gfmasEnable.equalsIgnoreCase("true")){
			if(countChargelineLimit > FulfilmentConst.GFMAS_CHARGELINE_LIMIT){
				throw new EpException(MessageConst.ERROR_740049, new String[]{getManagedBean().getFulfilmentRequest().getDocNo(),FulfilmentConst.GFMAS_CHARGELINE_LIMIT.toString()});
			}
		}else{
			if(countChargelineLimit > FulfilmentConst.ESPKB_CHARGELINE_LIMIT){
				throw new EpException(MessageConst.ERROR_740049, new String[]{getManagedBean().getFulfilmentRequest().getDocNo(),FulfilmentConst.ESPKB_CHARGELINE_LIMIT.toString()});
			}
		}
	}
	
	/**
	 * retrieve account type.
	 * @return parameter description by parameter type and search bean
	 */
	public List<PmParameterDesc> retrieveAccountType() {
		log.info("retrieveAccountType");
		return ParameterHelper.getInstance().getParameterDescListBySearchBean(
				ParameterTypeConst.TYPE_OF_ACCOUNT_TYPE, null);
	}
	
	/**
	 * on change of account type, the selectedAccClass and selectedGenObjCode to set.
	 * 
	 */
	public void onChangeAccType(){
		getManagedBean().getGlAccountSearchBean().setSelectedAccClass(null);
		getManagedBean().getGlAccountSearchBean().setSelectedGenObjCode(null);
	}
	
	/**
	 * retrieve account class.
	 * @return item
	 */
	public SelectItem[] retrieveAccountClass() {
		log.info("retrieveAccountClass");
		SelectItem[] items = null;	
		if (StringUtils.isNotBlank(getManagedBean().getGlAccountSearchBean().getSelectedAccType())) {
			IObjectSO objectSO = null;
			try {
				int i = 1;
				ObjectReqDTO reqDTO = new ObjectReqDTO();
				SearchBean<String> accDoSearchBean = new SearchBean<String>();
				accDoSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("accType",
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getGlAccountSearchBean().getSelectedAccType()));
				accDoSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatus",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));
				
				accDoSearchBean.getSortColumns().add(new SearchOrderBean("accClass"));

				reqDTO.setAccDoSearchBean(accDoSearchBean);
				objectSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IObjectSO.class);
				ObjectResDTO resDTO = objectSO
						.retrieveVPmDoAccClassListBySearchBean(reqDTO);
				List<String> list = resDTO.getAccDoResultBean().getResultList();
				items = new SelectItem[list.size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
				for (String object : list) {
					items[i++] = new SelectItem(object,
							object);
				}
			} catch (Exception e) {
				throw new EpException(e.getMessage());
			} finally {
			  objectSO = null;
			}
		}else{
			items = new SelectItem[1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
		}
		return items;
	}
	
	/**
	 * on change of account class, the selectedGenObjCode to set.
	 * 
	 */
	public void onChangeAccClass(){
		getManagedBean().getGlAccountSearchBean().setSelectedGenObjCode(null);
	}
	
	/**
	 * retrieve GoCode List.
	 * @return item
	 */
	public SelectItem[] retrieveGoCodeList() {
		log.info("retrieveGoCodeList");
		SelectItem[] items = null;	
		if(getManagedBean().isEspkb()){
			getManagedBean().getGlAccountSearchBean().setSelectedAccClass("00");
			getManagedBean().getGlAccountSearchBean().setSelectedAccType("0");
		}
		if (StringUtils.isNotBlank(getManagedBean().getGlAccountSearchBean().getSelectedAccClass())) {
			IObjectSO objectSO = null;
			try {
				int i = 1;
				ObjectReqDTO reqDTO = new ObjectReqDTO();
				SearchBean<String> accDoSearchBean = new SearchBean<String>();
				accDoSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("accType",
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getGlAccountSearchBean().getSelectedAccType()));
				
				accDoSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("accClass",
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getGlAccountSearchBean().getSelectedAccClass()));
				
				accDoSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatus",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));
				
				accDoSearchBean.getSortColumns().add(new SearchOrderBean("goCode"));

				reqDTO.setAccDoSearchBean(accDoSearchBean);
				objectSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IObjectSO.class);
				ObjectResDTO resDTO = objectSO
						.retrieveVPmDoGoCodeListBySearchBean(reqDTO);
				List<String> list = resDTO.getAccDoResultBean().getResultList();
				items = new SelectItem[list.size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
				for (String object : list) {
					items[i++] = new SelectItem(object,
							object);
				}
			} catch (Exception e) {
				throw new EpException(e.getMessage());
			} finally {
			  objectSO = null;
			}
		}else{
			items = new SelectItem[1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
			
		}
		return items;
	}
	
	private class SearchGlAccountPagingDataModel extends PagingDataModel<VPmDo> {
		private static final long serialVersionUID = 1L;

		public SearchGlAccountPagingDataModel() {
			super("glAccountSR");
		}

		@Override
		public ISearchResultBean<VPmDo> pageChangeListener(ISearchBean<VPmDo> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IGlAccountSO glAccountSO;
			try {
				//ISearchBean<PmGlAccount> searchBeanData = getSearchBeanData(searchBean);
				GlAccountReqDTO glAccountReqDTO = new GlAccountReqDTO();
				GlAccountSearchBean glAccountSearchBean = getManagedBean().getGlAccountSearchBean();	

				if (StringUtils.isNotBlank(glAccountSearchBean.getSelectedAccType())) {
					glAccountReqDTO.setAccType(glAccountSearchBean.getSelectedAccType());					
				}

				if (StringUtils.isNotBlank(glAccountSearchBean.getSelectedAccClass())) {
					glAccountReqDTO.setAccClass(glAccountSearchBean.getSelectedAccClass());					
				}

				if (StringUtils.isNotBlank(glAccountSearchBean.getSelectedGenObjCode())) {
					glAccountReqDTO.setGoCode(glAccountSearchBean.getSelectedGenObjCode());					
				}

//				if (StringUtils.isNotBlank(glAccountSearchBean.getAccountCode())) {
//					glAccountReqDTO.setGlAccountCode(glAccountSearchBean.getAccountCode());					
//				}

				if (StringUtils.isNotBlank(glAccountSearchBean.getAccountName())) {
					glAccountReqDTO.setGlAccountDesc(glAccountSearchBean.getAccountName());
				}

				searchBean.setUseEntityClass(true);
				glAccountReqDTO.setGlAccountRecordStatus(DbValueConst.RECORD_STATUS_ACTIVE);
				glAccountReqDTO.setPtjGroupId(retrieveJabatanOrgProfile(getManagedBean().getFulfilmentRequest().getChargeOrgProfile().getOrgProfileId()).getOrgProfileId());
				glAccountReqDTO.setGlAccountObjByKumpPTJSearchBean(searchBean);
				glAccountSO = (IGlAccountSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IGlAccountSO.class);
				GlAccountResDTO resDTO = glAccountSO.searchGlAccountDataByKumpPTJ(glAccountReqDTO);				
				getManagedBean().setLazy(true);		
				if (resDTO.isSuccessful()) {

					return resDTO.getGlAccountObjByKumpPtjResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}

			return null;
		}
	}
	
	/**
	 * reset search bean fields
	 */
	public void resetGlAccountSearchBean() {
		log.debug("resetGlAccountSearchBean");		
	    this.getManagedBean().setGlAccountSearchBean(new GlAccountSearchBean());
		searchGlAccount = new SearchGlAccountPagingDataModel();
	}
	
	/**
	 * preloading Gl Account
	 */
	public void preLoadingGlAccount() {
		log.info("preLoading GlAccount method");
		this.resetGlAccountSearchBean();
	}
	
	/**
	 * Search Gl Account.
	 */
	public void searchGlAccount() {
		searchGlAccount.reloadData();
	}

	/**
	 * Search Gl Account from paging item data model.
	 */	
	public IPagingDataModel<VPmDo> getSearchGlAccount() {
		return searchGlAccount;
	}

	public void setSearchGlAccount(IPagingDataModel<VPmDo> searchGlAccount) {
		this.searchGlAccount = searchGlAccount;
	}
	
	public void selectedAccCode(){
		getManagedBean().setGosodo(getAccCode());
	}
	
	private PmGlAccount accCode;

	public PmGlAccount getAccCode() {
		return accCode;
	}
	public void setAccCode(PmGlAccount accCode) {
		this.accCode = accCode;
	}
	
	/**
	 * Initialize Purchase Request.
	 * 
	 * Method get order details and FL Manual Address.
	 */
	public void initializePRBeans(){
		if(getManagedBean().getFeeVoteFund()==null) {
			IConfigGeneralSO configGeneralSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IConfigGeneralSO.class);
			ConfigGeneralResDTO configGeneralResDTO = configGeneralSO.getConfigGeneral(null);
			getManagedBean().setFeeVoteFund(configGeneralResDTO.getPmConfigGeneral().getPmVotFund());
		}

		if (getManagedBean().getFulfilmentRequest() == null) {
			getManagedBean().setFulfilmentRequest(new FlFulfilmentRequest());
			getManagedBean().getFulfilmentRequest().setIssuedOrgProfile(new PmOrgProfile());
			getManagedBean().getFulfilmentRequest().getIssuedOrgProfile().setOrgProfileId(1L);
			getManagedBean().getFulfilmentRequest().setChargeOrgProfile(new PmOrgProfile());
			getManagedBean().getFulfilmentRequest().getChargeOrgProfile().setOrgProfileId(1L);
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
		if(null == getManagedBean().getFlManualAddress()){
			getManagedBean().setFlManualAddress(new FlDeliveryAddress());
			getManagedBean().getFlManualAddress().setDeliveryAddressId(1L);
			if(null == getManagedBean().getFlManualAddress().getFlAddress()){
				getManagedBean().getFlManualAddress().setFlAddress(new FlAddress());
			}
			getManagedBean().getFlManualAddress().getFlAddress().setPmCountry(new PmCountry());
			getManagedBean().getFlManualAddress().getFlAddress().getPmCountry().setCountryId(1L);
			getManagedBean().getFlManualAddress().getFlAddress().setPmState(new PmState());
			getManagedBean().getFlManualAddress().getFlAddress().getPmState().setStateId(1L);
			getManagedBean().getFlManualAddress().getFlAddress().setPmDistrict(new PmDistrict());
			getManagedBean().getFlManualAddress().getFlAddress().getPmDistrict().setDistrictId(1L);
			getManagedBean().getFlManualAddress().getFlAddress().setPmCity(new PmCity());
			getManagedBean().getFlManualAddress().getFlAddress().getPmCity().setCityId(1L);
			getManagedBean().getFlManualAddress().getFlAddress().setPmDivision(new PmDivision());
			getManagedBean().getFlManualAddress().getFlAddress().getPmDivision().setDivisionId(1L);
			getManagedBean().getFlManualAddress().setReceivingGrp(new PmAddressType());
		}
		if(getManagedBean().getFlManualAddress().getReceivingGrp()==null){
			getManagedBean().getFlManualAddress().setReceivingGrp(new PmAddressType());
		}
		getManagedBean().getFlManualAddress().setPmAddressId(null);

		if(isFlFulfilmentRequisitioner()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setFulfilmentRequisitioner(facesScopes.getUserSessionBean().getLoginId());
		}
		getManagedBean().setDeleteButtonHide(true);
	}
	
	/**
	 * download file Garis Panduan Penetapan Item Bercukai.pdf.
	 */
	public void downloadFile(String filePath) throws IOException {
		StringBuilder downloadPath = new StringBuilder();
		downloadPath.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)).append(filePath);
		
		super.downloadFile(downloadPath.toString());
	}
	
	/**
	 * to check old GST Rate
	 */
	public Boolean checkOldGSTRate(FlFulfilmentRequest flRequest) {
		boolean oldGstRateFlg = false;
		BigDecimal gstRate = BigDecimal.valueOf(6);
		
		if(flRequest.getGstRate() != null) {
			if(flRequest.getGstRate().compareTo(gstRate)==0) {
				oldGstRateFlg = true;
			}
		}
		return oldGstRateFlg;
	}
	
	//start added for #24353
	public IPagingDataModel<PmAddressType> getPmAddressTypeDataModel() {
		return pmAddressTypeDataModel;
	}
	public void setPmAddressTypeDataModel(IPagingDataModel<PmAddressType> pmAddressTypeDataModel) {
		this.pmAddressTypeDataModel = pmAddressTypeDataModel;
	}
	public PmAddressType getSelectedPmAddressType() {
		return selectedPmAddressType;
	}
	public void setSelectedPmAddressType(PmAddressType selectedPmAddressType) {
		this.selectedPmAddressType = selectedPmAddressType;
	}
	public PmAddressType getDefaultPmAddressType() {
		return defaultPmAddressType;
	}
	public void setDefaultPmAddressType(PmAddressType defaultPmAddressType) {
		this.defaultPmAddressType = defaultPmAddressType;
	}
	//end added for #24353
	
	
	/*
	 * #17087
	 * Ya - true - delivery start date remain the same
	 * Tidak - false - delivery start date will get from approve 1GFMAS
	 */
	
	public void upDateStartDateFromBpm(){
		boolean radioCheck = getManagedBean().getFulfilmentRequest().isRadioCheckDeliveryDate();
		if (radioCheck == false) {
			getManagedBean().getFulfilmentRequest().setRadioCheckDeliveryDate(false);
		} else {
			getManagedBean().getFulfilmentRequest().setRadioCheckDeliveryDate(true);
		}
	}
	private IRequestNoteSO getRequestNoteSO() {
		return BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IRequestNoteSO.class);
	}

	/* #17087 zonal set */
	public void setzonalTest(String zonalTest) {
		this.zonalTest = zonalTest;
	}

	
	/**
	 * Add/display only cancel/reject button if SQ not valid 
	 */
	public void addButton(){
		Long currentStatusId = getManagedBean().getCurrentStatusId();
		//checking if SQ still valid, if not valid thus cancel/reject button will be added
		
		
		if(getManagedBean().isPidValidityExpired()){
			//purchaseRequestBackingBean.managedBean.fulfilmentRequest.status.statusId
			getManagedBean().getFulfilmentRequest().getStatus().setStatusId(60755L);
			if(currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_APPROVAL) || 
					   currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION_APPROVAL)){
						String[] addReject = new String[]{BpmOutcomeConst.BPM_REJECT};    		
						getManagedBean().setOutComes(addReject);	
						
					}else if(currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION) || 
							   currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION)){
						String[] addCancel = new String[]{BpmOutcomeConst.BPM_CANCEL};    		
						getManagedBean().setOutComes(addCancel);
					}
		}else{
			if(!isSqStillValid()){
				if(currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_APPROVAL) || 
				   currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION_APPROVAL) ){
					String[] addReject = new String[]{BpmOutcomeConst.BPM_REJECT};    		
					getManagedBean().setOutComes(addReject);	
					
				}else{
					String[] addCancel = new String[]{BpmOutcomeConst.BPM_CANCEL};    		
					getManagedBean().setOutComes(addCancel);
				}
						
			}		
		}
	}
	
	/**
	 * Check either SQ is valid or not (SQ only valid for 60 days after it closing date)
	 */
	public boolean isSqStillValid(){		
		
		Boolean validDate = true;		
		Long currentStatusId = getManagedBean().getFulfilmentRequest().getFlWorkflowStatus().getPmStatus().getStatusId();
		ScQuote scQuote = getManagedBean().getFulfilmentRequest().getScPurchaseRequest().getRequestNote().getQuote();
		ScRequestNote scRequestNote = getManagedBean().getFulfilmentRequest().getScPurchaseRequest().getRequestNote();
		
		// Refactor: #12177 - Start
		Integer sqTimeValidity = NgepApplicationBean.getSqTimeValidity();
		log.info("Refactor 12177 (PurchaseRequestBackingBean) : getSqTimeValidity - " + sqTimeValidity);
		// Refactor: #12177 - End		
		
		if(scQuote != null){
			if(getManagedBean().getFulfilmentRequest().getScPurchaseInDefault() == null){
				Date currentDate = new Date();
				Date closingDate = scQuote.getEndDate();
				
				Calendar cal = Calendar.getInstance();
				cal.clear(); 
				cal.setTime(closingDate);
				cal.add(Calendar.DATE, sqTimeValidity); //add 60days to closing/end date
				
				if(currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION)
				  || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_APPROVAL) 
				  || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION) 
				  || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION_APPROVAL) 
				  || currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_QUERY_VERIFICATION) ){
					if(currentDate.compareTo(cal.getTime()) >= 0){
						validDate = false;
						sqExpiredMessageFlag = true;
						getManagedBean().setSqExpiredStatus(true);
						getManagedBean().setSqNumber(scQuote.getQuoteNo());
						getManagedBean().setRequestNoteId(scRequestNote.getRequestNoteId());
					}
				}
			}				
		}	
		return validDate;	
	}
	
	public String getSkuDescription(ScRequestItem scRequestItem) {
		String skuDesc = "";
//			if(scRequestItem.getCmItem().getCmSupplierItems() != null && scRequestItem.getCmItem().getCmSupplierItems().size() > 0){
//				skuDesc = scRequestItem.getCmItem().getCmSupplierItems().get(0).getSkuNumber();	
//				
//		}
		//29396-start
		CatalogueReqDTO catalogueReqDTO = new CatalogueReqDTO();
		IViewSupplierItemSO viewSupplierItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, IViewSupplierItemSO.class);
		
		catalogueReqDTO.setSupplierId(getManagedBean().getFulfilmentRequest().getSmSupplier().getSupplierId());
		catalogueReqDTO.setSupplierItemId(scRequestItem.getCmItem().getItemId());
		catalogueReqDTO.setUomId(scRequestItem.getUom().getUomId());
		CatalogueResDTO  catalogueResDTO = viewSupplierItemSO.getSkuNumber(catalogueReqDTO);		
		skuDesc = catalogueResDTO.getSkuNumber();
		//29396-end
		
		return skuDesc;
	}

	// 26279 - start
	/**
	 * View list of Disciplinary Action
	 */
	public void viewDisciplinaryAction() {
		SmSupplier supplier;

		if (getManagedBean().getFulfilmentRequest().getSmSupplier() != null) {
			supplier = getManagedBean().getFulfilmentRequest().getSmSupplier();
			supplier.setFlagSupplier(true);
			supplier.setFlagDASuppProfile(true);

			List<DisciplinaryActionVO> disciplinaryActionVO = ViewSuppProfileBackingBean
					.getDisciplinaryActionVO(supplier);
			getManagedBean().setDisciplinaryActionVO(disciplinaryActionVO);
			RequestContext.getCurrentInstance().execute("disciplinaryActionHistoryDlg.show();");
		}
	}
	//26279 - end
	//24893 - start
	/**
	 * check role user is requisitioner
	 */
	public boolean isRequisitioner() {
		return getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value());
	}
	//24893 - end
	
	//12183 - Start
	/**
	 * Triggered from: prReviewHeaderPage.xhtml.
	 * 
	 * Code performance risk: triggered everytime page loads.
	 * 
	 * Mitigation: 
	 * 1. Make sure logic and service calls is optimized. 
	 * 2. Create new service to execute and return specific value. Fast execution time.
	 * 3. Disable logging
	 * 
	 * @param docNo
	 * @param docStatusId
	 * @param docType
	 * @return String 
	 */
	public String getDocStatus(FlFulfilmentRequest flFulfilmentRequest){
		
		if(checkStatusPidExpired(flFulfilmentRequest)){
			return getStatusDescById(DPStatusConstants.PID_EXPIRED);
		}else if(!isSqStillValid()){
			return getStatusDescById(DPStatusConstants.SQ_EXPIRED);
		}else{
			return getStatusDescById(getManagedBean().getFulfilmentRequest().getFlWorkflowStatus().getPmStatus().getStatusId());
		}
		
	}
	
	public boolean checkStatusPidExpired(FlFulfilmentRequest flFulfilmentRequest){
		
		Long currentStatusId = null;
	    Boolean pidExpired = false;
		
		if(flFulfilmentRequest != null){
			
			currentStatusId = flFulfilmentRequest.getFlWorkflowStatus().getPmStatus().getStatusId();
			
			if(currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION)
				|| currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_APPROVAL)
				|| currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION)
				|| currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION_APPROVAL)
				|| currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_QUERY_VERIFICATION)){
				
				 if(flFulfilmentRequest.getScPurchaseInDefault()!=null){
					 	Date today  = DateUtils.getCurrentDate();
						Date ExpDate = DateUtils.addDays(flFulfilmentRequest.getScPurchaseInDefault().getChangedDate(), flFulfilmentRequest.getScPurchaseInDefault().getPidValidDays());

						//log.info("pID validity begin");
						
							if(ExpDate.before(today)){
								pidExpired = true;
							}  
						
						//log.info("pID validity end");
				 }	
			}	
		}	
		return pidExpired;
	}
	//12183 - End
	
	//29401-start
		private void validateDeliveryAddress(String outcome) {
			if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)
					|| outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
				int checkDelAddr = 1;
				IAddressSO addressSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAddressSO.class);
				AddressReqDTO reqDTO = new AddressReqDTO();
				for (FlDeliveryAddress flDeliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()) {
					if(flDeliveryAddress.getPmAddressId()!=null){
						reqDTO.setPmOrgProfile(getManagedBean().getFulfilmentRequest().getIssuedOrgProfile());
						reqDTO.setAddressId(flDeliveryAddress.getPmAddressId());
						AddressResDTO resDTO = addressSO.ValidateDeliveryAddrByOrgProfileIdAndAddressID(reqDTO);
						checkDelAddr = resDTO.getCheckDelAddr();
			    		if(checkDelAddr==0){
			    			throw new EpException(MessageConst.ERROR_740068);
			    		}					
					}
					
				}
			}
		}
		//29401-end
		
		//#28718 - Start
		private boolean checkTriggeredNewPR() {
			Long currentStatusId;

			FlFulfilmentRequest flFulfilmentRequest = getManagedBean().getFulfilmentRequest();

			currentStatusId = flFulfilmentRequest.getFlWorkflowStatus().getPmStatus().getStatusId();
			if (currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION)
					|| currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_APPROVAL)
					|| currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION)
					|| currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_REVISION_APPROVAL)
					|| currentStatusId.equals(FulfilmentStatusConst.PR_PENDING_QUERY_VERIFICATION)) {

				boolean isVotSOrAmanahExist = false;
				boolean isVotBOrTExist = false;
				boolean isVotPExist = false;

				String votFundType;
				for (FlDeliveryAddress deliveryAddress : getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses()) {
					for (FlFulfilmentItemAddr itemAddress : deliveryAddress.getFlFulfilmentItemAddr()) {
						if (itemAddress.getPmVotFund() != null) {
							if (null == votFundCodeMap.get(itemAddress.getPmVotFund().getVotFundId())) {
								votFundType = itemAddress.getPmVotFund().getVotFundType();
							} else {
								votFundType = votFundCodeMap.get(itemAddress.getPmVotFund().getVotFundId())
										.getVotFundType();
							}
							if (votFundType.equalsIgnoreCase(VotFundType.SETIA.value())
									|| votFundType.equalsIgnoreCase(VotFundType.AMANAH.value())) {
								isVotSOrAmanahExist = true;
							} else if (votFundType.equalsIgnoreCase(VotFundType.BEKALAN.value())
									|| votFundType.equalsIgnoreCase(VotFundType.TANGGUNGAN.value())) {
								isVotBOrTExist = true;
							} else if (votFundType.equalsIgnoreCase(VotFundType.PEMBANGUNAN.value())) {
								isVotPExist = true;
							}
						}
					}
					if((isVotSOrAmanahExist && isVotBOrTExist) || (isVotSOrAmanahExist && isVotPExist) 
							|| (isVotBOrTExist && isVotPExist)){
						return true;
					}
				}
			}
			return false;
		}

		/**
		 * Separate PR that according to the correct combination of charge lines code
		 * @throws Exception
		 */
		public void triggeredNewPR() throws Exception{
			fulfillmentRequestAction(BpmOutcomeConst.BPM_SAVE);
			IFulfilmentRequestSO fulfilmentRequestSO;
					
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flFulfilmentRequest = getManagedBean().getFulfilmentRequest();
			fulfilmentRequestSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);
			reqDTO.setFlFulfilmentRequest(flFulfilmentRequest);
			reqDTO.setPurchaseRequestId(getManagedBean().getFulfilmentRequest().getScPurchaseRequest().getPurchaseRequestId());
			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
			reqDTO.setToken(token);
			if(getManagedBean().getFulfilmentRequest().getStatus().getStatusId().longValue() == FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION.longValue()) {
				reqDTO.setOutcome(BpmOutcomeConst.BPM_DRAFT);
			} else {
				reqDTO.setOutcome(BpmOutcomeConst.BPM_SUBMIT);
				if(getManagedBean().getSelectedApprover() != null){
					PmUserGroupUser groupUser = getManagedBean().getApproverMap().get(getManagedBean().getSelectedApprover());
					if(groupUser!=null) {
						List<PmUser> approverUserList = sortApproverUserList(getManagedBean().getApproverGroupUserList(), groupUser.getPmUser(),getManagedBean().getFulfilmentRequest().getPmUserGroup().getUserGroupId());
						reqDTO.setApproverUserList(approverUserList);
						reqDTO.setSelectedApprover(getManagedBean().getSelectedApprover());
					}
				}
				if(reqDTO.getApproverUserList() == null || reqDTO.getApproverUserList().isEmpty()){
					reqDTO.setBpmApproverList(getManagedBean().getBpmApproverList());
				}
			}
			reqDTO.setS1DocNo(flFulfilmentRequest.getScPurchaseRequest().getRequestNote().getRequestNoteNo());
			reqDTO.setScRequestNoteDtlList(getManagedBean().getRequestNoteWithDtlsList());
			reqDTO.setClone(Boolean.TRUE);
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setRequisitioner(isFlFulfilmentRequisitioner());
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());
			fulfilmentRequestSO.triggerNewPurchaseRequest(reqDTO);

			String[] param = null;
			successMessage(MessageConst.INFO_745088, param, true);
		}
		//#28718 - End
		//#21416 start
		public String filterItemName(String itemName) {
			String filteredItemName = filterItemNameNA(itemName);
			return filteredItemName;
			
		}
		//#21416 end
		
		//29728  - start
		private void resetSupplierInfo() {
			log.info("******* ENTER resetSupplierInfo METHOD ***********");
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setApplId(getManagedBean().getFulfilmentRequest().getSmSupplier().getSmAppl().getApplId());
			List<SmCompanyBasic> compBasicList = SmServiceLookup.getMofRegistrationSO()
					.retrieveSmCompanyBasicByApplId(reqDTO).getCompanyBasicList();
	
			FlSupplierDtl supplierDtl = getManagedBean().getFulfilmentRequest().getFlSupplierDtl();
			supplierDtl.setSupplierName(getManagedBean().getFulfilmentRequest().getSmSupplier().getCompanyName());
			supplierDtl.setBranchCode(null);
			supplierDtl.setBranchName(null);
			updateSupplierHqAddress(supplierDtl, getManagedBean().getFulfilmentRequest().getSmSupplier().getSupplierId(),
					compBasicList);
		}
		//29728 -end
		
		//34555-start
		/**
		 * Update delivery date based on delivery Term and uomCode.
		 */
		public void updateMaxDeliveryDate() {
			Date startDate = getManagedBean().getFulfilmentRequest().getStartDate();
			int deliveryTerm = 0;
			String uomCode = "";
			deliveryTerm = getManagedBean().getFulfilmentRequest().getDeliveryTerm();
			uomCode = getManagedBean().getFulfilmentRequest().getPmUom().getParameterCode();
			
			if(uomCode.equalsIgnoreCase(ParameterConst.DELIVERY_TERM_DAYS)){
				getManagedBean().setMaxDate(DateUtils.getEndDate(startDate, deliveryTerm,ParameterConst.DELIVERY_TERM_DAYS));
			}else if(uomCode.equalsIgnoreCase(ParameterConst.DELIVERY_TERM_MONTHS)){
				getManagedBean().setMaxDate(DateUtils.getEndDate(startDate, deliveryTerm,ParameterConst.DELIVERY_TERM_MONTHS));
			}
		}
		//34555-End		
}