/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.purchaseinquiry;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;// 27868
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletSession;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;//#27868
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.pm.map.IPmPK7MapSO;//#27868
import my.ep.biz.service.sc.directpurchase.IPrInquirySO;
import my.ep.biz.service.sc.directpurchase.IRequestNoteSO;
import my.ep.biz.service.sm.supplierDataMaintenance.ISupplierDataMaintenanceSO;//#27868
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.userorg.UserOrgReqDTO;  //#27868
import my.ep.dto.pm.authorization.userorg.UserOrgResDTO;  //#27868
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;  //#27868
import my.ep.dto.pm.common.reason.ReasonResDTO;  //#27868
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.directpurchase.PrInquiryReqDTO;
import my.ep.dto.sc.directpurchase.PrInquiryResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;//#27868
import my.ep.exception.EpException;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg; //#27868
import my.ep.persistence.entity.pm.authorization.PmOrgProfile; //#27868
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc; //#27868
import my.ep.persistence.entity.pm.common.PmReasonRestr; //#27868
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.pm.map.PmPK7Mapping; //#27868
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScPIZone;
import my.ep.persistence.entity.sc.directpurchase.ScPRIRemark;
import my.ep.persistence.entity.sc.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.sc.directpurchase.ScRNWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteRemark;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.sc.requestnote.RequestNoteBackingBean;
import my.ep.web.constant.sc.DPConstants;  //#27868
import my.ep.web.constant.SSTConstant;  //#27868
import my.ep.web.managedbean.sc.purchaseinquiry.PurchaseInquiryManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.ReasonHelper; //#27868
import my.ep.web.util.sc.helper.SourcingZoneHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;

// Auto-generated Javadoc

/**
 * The Class PurchaseInquiryBackingBean.
 * 
 * @author sri 
 * 
 * @since 01.15.2014
 * 
 */
@ManagedBean(name = "purchaseInquiryBackingBean")
@RequestScoped
public class PurchaseInquiryBackingBean extends
		SourcingBaseBackingBean<PurchaseInquiryManagedBean> {
	private static final long serialVersionUID = 1L;

	/** The log. */
	private static Logger log = Logger
			.getLogger(PurchaseInquiryBackingBean.class);

	/** The Constant ZONE_SEPARATOR. */
	public static final String ZONE_SEPARATOR = "_";
	
	/** The text before sign. */
	private String textBeforeSign;


	/** The purchase inquiry page. */
	private String PURCHASE_INQUIRY_PAGE = "prInquiryMain";

	/**
	 * Instantiates a new purchase inquiry backing bean.
	 */
	public PurchaseInquiryBackingBean() {
		super();
		if (MDC.get(LogKey.USER_ID) == null) {
			PmUser user = new FacesScopes().getUserSessionBean();
			if (user != null) {
				Long userName = user.getUserId();
				new LogKey(userName);
				if (log.isDebugEnabled()) {
					log.debug("userName: " + userName);
				}
			}
		}
	}

	/**
	 * add non contract item from search catalogue to Purchase Inquiry. page
	 * navigate to prInquiry page.
	 * 
	 * @param cmSupplierItem
	 *            the cm supplier item
	 * @param compareSupplierItems
	 *            the compare supplier items
	 * @return String
	 */
	public String prInquiryAction(CmSupplierItem cmSupplierItem,
			List<CmSupplierItem> compareSupplierItems) {
		debugLogger("prInquiryAction");
		ISearchBean<VCmSupplierItem> viewSearchBean = null;
		List<Long> cmSupplieritemIds = null;
		ScRequestSupplierItem requestSupplierItem = getManagedBean()
				.getScRequestSupplierItem();
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.CATALOGUE_MANAGEMENT,
						ICatalogueSO.class);
		List<ScRequestSupplierItem> supplierItems = new ArrayList<ScRequestSupplierItem>();

		CatalogueReqDTO catReqDTO = new CatalogueReqDTO();
		catReqDTO.setSupplierItemId(cmSupplierItem.getSupplierItemId());
		ScPrInquiry scPrInquiry = new ScPrInquiry();
		ScRequestItem requestItem = createRequestItem(cmSupplierItem);
		if (cmSupplierItem != null) {
			ScSupplierItemPrice supplierItemPrice = new ScSupplierItemPrice();
			// Check active CmPricings
			CatalogueResDTO catResDTO = catalogueSO
					.findActivePricing(catReqDTO);
			List<CmPricing> pricings = catResDTO.getPriceLs();
			cmSupplierItem.setCmPricings(pricings);
			this.getManagedBean().setSupplierItem(cmSupplierItem);

			if (cmSupplierItem.getCmPricings() != null
					//#29236 CR 26129: Static Code Analysis Use isEmpty() to check whether the collection is empty or not.
					&& !cmSupplierItem.getCmPricings().isEmpty()) {
				supplierItemPrice.setPricing(cmSupplierItem.getCmPricings()
						.get(0));
				// price type
				supplierItemPrice.setPriceType(getParameterByTypeAndCode(
						ParameterTypeConst.PRICE_TYPE, cmSupplierItem
						.getCmPricings().get(0).getPricingType()));

			} else {
				throw new EpException(MessageConst.ERROR_760159);
			}

			SmSupplier smSupplier = cmSupplierItem.getSmSupplier();
			validateSupplierAddress(smSupplier.getSupplierId());
			requestSupplierItem.setSmSupplier(smSupplier);
			
			requestSupplierItem.setSupplierName(smSupplier.getCompanyName());
			requestSupplierItem.setMofNumber(getMofNoBySupplierId(smSupplier.getSupplierId()));
			
			supplierItemPrice.setScRequestSupplierItem(requestSupplierItem);
			requestSupplierItem.setSupplierItemPrice(supplierItemPrice);
			requestSupplierItem.setRequestNoteItem(requestItem);
		}
		scPrInquiry.setWorkflowStatuses(null);
		scPrInquiry.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		
		
		// by default days - 7
		IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		ruleReqDTO.setRuleType(ParameterConst.ITEM_INQUIRY);
		List<PmConfigRuleValue> ruleValueList = ruleSO.getRuleValuesByTypeInCurrentDate(ruleReqDTO).getRuleValueList();
		//#29236 CR 26129: Static Code Analysis Use isEmpty() to check whether the collection is empty or not.
		if (ruleValueList != null && !ruleValueList.isEmpty()) {
			PmConfigRuleValue configRuleValue = ruleValueList.get(0);
			scPrInquiry.setDayToResponse(Integer.valueOf(configRuleValue.getRuleValue()));
		} else {
			throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(ruleReqDTO.getRuleType())});
		}
		scPrInquiry.setDateToResponse(calcDayToResponse(scPrInquiry));

		// check supplier is government seller or not
		ScRequestNoteDtl requestNoteDtl = getManagedBean().getRequestNoteDtl();
		PmParameter pmParameter = new PmParameter();
		requestNoteDtl.setJustificationType(new PmReason());

		// check supplier is government seller or not
		// 27868 - start
		Date dataCreatedDate;
		dataCreatedDate = assignActualDate();
		getManagedBean().setCreatedDate(dataCreatedDate);
		String parameterType = ParameterTypeConst.PROCUREMENT_TYPE_RPT;
		pmParameter.setParameterType(parameterType);
		getManagedBean().setParameterTypeRPT(parameterType);
		// 27868 - end
		if (isGovtSellerOrNot(cmSupplierItem)) {
			pmParameter.setParameterCode(ParameterConst.PROC_TYPE_G2G);
			requestNoteDtl.setProcurementType(pmParameter);
			// 27868 - start
			getManagedBean().setProcurementTypes(getRadioButtonCodeValues(parameterType, Boolean.TRUE));
			if (!isAgencyAllowedForSpecialItem()) {
				disableSpecialItemRadioButton();
			}
			// 27868 - end

		} else {
			pmParameter.setParameterCode(ParameterConst.PROC_TYPE_NORMAL);
			requestNoteDtl.setProcurementType(pmParameter);
			// 27868 - start
			getManagedBean().setProcurementTypes(getRadioButtonCodeValues(parameterType, Boolean.FALSE));
			if (!isAgencyAllowedForSpecialItem()) {
				disableSpecialItemRadioButton();
			}
			// 27868 - end
		}
		
		if(cmSupplierItem.getLeadTime() != null) {
			requestNoteDtl.setDeliverTerm(cmSupplierItem.getLeadTime().intValue());
		}

		List<CmPricing> cmPricings = new ArrayList<CmPricing>();
		if (cmSupplierItem.getCmPricings() != null
				//#29236 CR 26129: Static Code Analysis Use isEmpty() to check whether the collection is empty or not.
				&& !cmSupplierItem.getCmPricings().isEmpty()) {
			List<String> priceTypes = new ArrayList<String>();
			for (CmPricing princing : cmSupplierItem.getCmPricings()) {
				if (!priceTypes.contains(princing.getPricingType())) {
					priceTypes.add(princing.getPricingType());
				}
			}
			//#29236 CR 26129: Static Code Analysis Use isEmpty() to check whether the collection is empty or not.
			if (priceTypes != null && !priceTypes.isEmpty()) {
				for (CmPricing princing : cmSupplierItem.getCmPricings()) {
					cmPricings.add(princing);
				}
				requestSupplierItem
						.setSupplierItemPrices(createScSupplierItemPrices(
								requestSupplierItem, cmPricings));
			}
			// PriceType drop down list in AddToCart
			getManagedBean().setPriceTypes(
					getPriceTypeDropdownValues(ParameterTypeConst.PRICE_TYPE,
							priceTypes));
		}

		// compare suppliers criteria
		String extensionCode = requestItem.getCmItem().getExtensionCode();
		Long uomId = cmSupplierItem.getPmUom().getUomId();
		StringBuilder sb = new StringBuilder();
		sb.append("ITEM_CODE^").append(extensionCode).append("|")// item Code
				// .append("catType^").append("XC").append("|")
				.append("UOM_ID^").append(uomId);

		// adding suppliers from the compare screen
		StringBuilder supplierItemIds = new StringBuilder();
		cmSupplieritemIds = new ArrayList<Long>();
		if (compareSupplierItems != null) {
			for (CmSupplierItem supplierItem : compareSupplierItems) {
				if (supplierItem.getCmItem().getItemId()
						.equals(cmSupplierItem.getCmItem().getItemId())
						&& supplierItem.getPmUom().getUomName()
								.equals(cmSupplierItem.getPmUom().getUomName())) {
					Long supplierItemId = supplierItem.getSupplierItemId();

					cmSupplieritemIds.add(supplierItemId);
					if (supplierItemIds.length() > 0) {
						supplierItemIds.append(",");
					}
					supplierItemIds.append(supplierItemId);
				}
			}
		}
		sb.append("|").append("DOC_ID^").append(supplierItemIds).append("^")
				.append(SearchOperator.OPERATOR_IN.value());

		viewSearchBean = new CatalogueBackingBean().getManagedBean()
				.getSearchDataModel().getSearchBean();
		List<BaseSearchFilterBean> filterBeans = viewSearchBean
				.getDataFilterBeans();

		for (BaseSearchFilterBean baseFilterBean : filterBeans) {
			if (baseFilterBean instanceof SearchFilterBean) {
				SearchFilterBean filterBean = (SearchFilterBean) baseFilterBean;
				sb.append("|").append(filterBean.getSearchField()).append("^")
						.append(filterBean.getSearchValue()).append("^")
						.append(filterBean.getSearchOperator());
			}
		}
		requestNoteDtl.setConditionField(sb.toString());

		// Add selected supplier item.
		requestSupplierItem.setCmSupplierItem(cmSupplierItem);
		supplierItems.add(requestSupplierItem);
		requestSupplierItem.setScPrInquiy(scPrInquiry);
		requestItem.setSupplierItems(supplierItems);
		scPrInquiry.setRequestItem(requestItem);

		requestSupplierItem.setRequestNoteItem(requestItem);
		requestNoteDtl.setRequestItem(requestItem);

		// pr inquiry status
		getCurrentStatus(getPmStatusByStatusId(DPStatusConstants.PRI_PENDING_SUBMISSION));
		
		scPrInquiry.setCreatedBy(new FacesScopes().getUserSessionBean()
				.getUserId());

		// Initializing Remarks
		scPrInquiry.setRemarks(initializeRemarks(scPrInquiry, null,
				scPrInquiry.getRemarks(), ScPRIRemark.class));

		// default value to price type drop down listing
		getManagedBean().setPriceType(ParameterConst.PRICE_TYPE_STANDARD);
		getManagedBean().setScPrInquiry(scPrInquiry);
		priceTypeChangeListener();

		// to get ptj
		PmUser user = new FacesScopes().getUserSessionBean();
		if (user != null) {
			OrgProfileReqDTO profileReqDTO = new OrgProfileReqDTO();
			profileReqDTO.setUserId(user.getUserId());
			profileReqDTO.setOrgType(OrganizationType.DIVISION.value());
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO orgProfileResDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(profileReqDTO);
			if (orgProfileResDTO.getOrgProfileList() != null
					&& orgProfileResDTO.getOrgProfileList().size() > 0) {
				getManagedBean().setPmOrgProfile(
						orgProfileResDTO.getOrgProfileList().get(0));
			}
		}
		getManagedBean().setShowButton(Boolean.TRUE);
		return PURCHASE_INQUIRY_PAGE;
	}

	/**
	 * Checks if is govt seller or not.
	 * 
	 * @param cmSupplierItem
	 *            the cm supplier item
	 * @return true, if is govt seller or not
	 */
	private boolean isGovtSellerOrNot(CmSupplierItem cmSupplierItem) {
		Boolean isg2g = Boolean.FALSE;
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(cmSupplierItem.getSmSupplier().getSupplierId());
		OrgProfileResDTO resDTO = orgProfileSO
				.retrieveOrgProfileByProfileId(reqDTO);
		//#29236 CR 26129: Static Code Analysis Start Merge this if statement with the enclosing one.
		/*
		if (resDTO.getPmOrgProfile() != null) {
			if (resDTO.getPmOrgProfile().isGovtSeller()) {
				isg2g = Boolean.TRUE;
			} 
		}
		*/
		if (resDTO.getPmOrgProfile() != null && resDTO.getPmOrgProfile().isGovtSeller()) {
			isg2g = Boolean.TRUE;
		}
		//#29236 CR 26129: Static Code Analysis End
		return isg2g;
	}

	private boolean isGovtSellerBySupplierId(Long supplierId) {
		Boolean isg2g = Boolean.FALSE;
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(supplierId);
		OrgProfileResDTO resDTO = getIOrgProfileSO().retrieveOrgProfileByProfileId(reqDTO);
		//#29236 CR 26129: Static Code Analysis Start Merge this if statement with the enclosing one.
		/*
		if (resDTO.getPmOrgProfile() != null) {
			if (resDTO.getPmOrgProfile().isGovtSeller()) {
				isg2g = Boolean.TRUE;
			}
		}
		*/
		if (resDTO.getPmOrgProfile() != null && resDTO.getPmOrgProfile().isGovtSeller()) {
			isg2g = Boolean.TRUE;
		}
		//#29236 CR 26129: Static Code Analysis End
		return isg2g;
	}

	/**
	 * Creates the request item.
	 * 
	 * @param cmSupplierItem
	 *            the cm supplier item
	 * @return the sc request item
	 */
	private ScRequestItem createRequestItem(CmSupplierItem cmSupplierItem) {
		ScRequestItem scRequestItem = new ScRequestItem();
		CmItem cmItem = cmSupplierItem.getCmItem();
		boolean prodOrService = false;
		String extensionCode = cmItem.getExtensionCode();
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.CATALOGUE_MANAGEMENT,
						ICatalogueSO.class);
		
		if(extensionCode != null) {
			String pOrS = catalogueSO.checkProductOrService(extensionCode);
			if(pOrS.equalsIgnoreCase("P")) {
				prodOrService = true;
			}
		}

		if (!prodOrService) { 
			scRequestItem.setItemType(getParameterByTypeAndCode(
					ParameterTypeConst.SC_ITEM_TYPE,
					ParameterConst.ITEM_TYPE_SERVICE));
		} else { // product
			scRequestItem.setItemType(getParameterByTypeAndCode(
					ParameterTypeConst.SC_ITEM_TYPE,
					ParameterConst.ITEM_TYPE_PRODUCT));
		}
		scRequestItem.setCmItem(cmItem);
		scRequestItem.setItemName(cmItem.getItemName());
		scRequestItem.setItemDesc(cmSupplierItem.getItemDesc());
		scRequestItem.setMinqty(cmSupplierItem.getMinOrder());
		scRequestItem.setUom(cmSupplierItem.getPmUom());
		scRequestItem.setLeadTime(cmSupplierItem.getLeadTime());

		return scRequestItem;
	}

	/**
	 * Calculate days to response for purchase inquiry.
	 * 
	 * @param scPrInquiry
	 *            the sc pr inquiry
	 * @return the date
	 */
	public Date calcDayToResponse(ScPrInquiry scPrInquiry) {
		Date dayToResp = null;
		if (scPrInquiry.getDayToResponse() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(scPrInquiry.getCreatedDate());
			cal.add(Calendar.DATE, scPrInquiry.getDayToResponse());
			dayToResp = cal.getTime();
			scPrInquiry.setDateToResponse(cal.getTime());
			getManagedBean().setScPrInquiry(scPrInquiry);
		}
		return dayToResp;
	}

	/**
	 * Gets the price type dropdown values.
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @param priceTypes
	 *            the price types
	 * @return the price type dropdown values
	 */
	private SelectItem[] getPriceTypeDropdownValues(String parameterType,
			List<String> priceTypes) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		try {
			items = new SelectItem[priceTypes.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (String priceTypeCode : priceTypes) {
				items[i++] = new SelectItem(priceTypeCode, getParameterDesc(
						parameterType, priceTypeCode));
			}
		} finally { //#29739 - bugs error validation not appear
		}
		return items;
	}

	/**
	 * priceTypeChangeListener for getting Item Pricing list in Purchase
	 * inquiry.
	 */
	public void priceTypeChangeListener() {
		debugLogger("priceTypeChangeListener");
		ScPrInquiry prInquiry  = getManagedBean().getScPrInquiry();
		List<ScSupplierItemPrice>	priceList = new ArrayList<ScSupplierItemPrice>();
		ScRequestSupplierItem scRequestSupplierItem = getManagedBean().getScRequestSupplierItem();
		
		if(prInquiry.isBrandNew()){
			prInquiry.setPriceType(getParameterByTypeAndCode(ParameterTypeConst.PRICE_TYPE, getManagedBean().getPriceType()));	
		}
		
		if (scRequestSupplierItem != null
				&& scRequestSupplierItem.getSupplierItemPrices() != null) {

			List<ScSupplierItemPrice> supplierItemPrices = scRequestSupplierItem.getSupplierItemPrices();
			
			if (supplierItemPrices != null) {
				for (ScSupplierItemPrice scSupplierItemPrice : supplierItemPrices) {
					if (scSupplierItemPrice.getPriceType() != null) {
						if(prInquiry.getPriceType() != null){

							if (scSupplierItemPrice.getPriceType()
									.getParameterCode()
									.equals(prInquiry.getPriceType().getParameterCode())) {
								Integer stockAvailable = getManagedBean().getStockAvailable();
								Long statusId = getManagedBean().getPriStatus().getStatusId();
								
								if (DPStatusConstants.PRI_PENDING_SUBMISSION.equals(statusId)
										|| (stockAvailable != ParameterConst.PI_STOCK_AVAILABILITY && DPStatusConstants.PRI_PENDING_SUPPLIER_RESPONSE
												.equals(statusId))) {
									scSupplierItemPrice.setItemPrice(null);
								}
								priceList.add(scSupplierItemPrice);
							}
						}
					}
				}
			}
			getManagedBean().setSupplierItemPrices(priceList);
		}
	}

	/**
	 * submit purchase inquiry details. submit - save data into database and BPM
	 * process will route to the next respective user. page navigate to
	 * acknowledge page.
	 * 
	 * @return the string
	 */
	public String submitPrInquiryDtl(String outcome) {
		debugLogger("submitPrInquiryDtl");
		try {
			PrInquiryReqDTO reqDTO = new PrInquiryReqDTO();
			FacesScopes facesScopes = new FacesScopes();
			ScRequestSupplierItem scRequestSupplierItem = getManagedBean().getScRequestSupplierItem();
			ScPrInquiry prInquiry = getManagedBean().getScPrInquiry();
			String prInquiryNo = prInquiry.getPrInquiryNo();
			PrInquiryResDTO prInquiryResDTO;

			ScRequestNoteDtl requestNoteDtl = getManagedBean().getRequestNoteDtl();
			IRequestNoteSO iRequestNoteSO = getRequestNoteSO();

			// proc type
			// 27868 - start
			Date dataCreatedDate;
			dataCreatedDate = assignActualDate();
			getManagedBean().setCreatedDate(dataCreatedDate);

			String procParameterType = validateProcurementTypeisRPT(dataCreatedDate);

			if (ParameterConst.PROC_TYPE_PK7.equalsIgnoreCase(requestNoteDtl.getProcurementType().getParameterCode())
					|| ParameterConst.PROC_TYPE_SPECIAL_ITEM.equalsIgnoreCase(requestNoteDtl.getProcurementType()
							.getParameterCode())) {
				procParameterType = ParameterTypeConst.PROCUREMENT_TYPE_RPT;
			}

			requestNoteDtl.setProcurementType(getParameterByTypeAndCode(procParameterType, requestNoteDtl
					.getProcurementType().getParameterCode()));
			// 27868 - end

			// justification type
			if (requestNoteDtl.getProcurementType().getParameterCode()
					.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
				if (requestNoteDtl.getJustificationType().getReasonCode() != null) {
					requestNoteDtl.setJustificationType(getReasonByCategoryAndCode(ReasonCategory.JUSTIFICATION_AD_HOC
							.value(), requestNoteDtl.getJustificationType().getReasonCode()));
				}
			} else if (requestNoteDtl.getProcurementType().getParameterCode()
					.equalsIgnoreCase(ParameterConst.PROC_TYPE_PK7)) {
				if (requestNoteDtl.getJustificationType().getReasonCode() != null) {
					// 27868 - start
					String originalReasonCode = requestNoteDtl.getJustificationType().getReasonCode();
					requestNoteDtl.setJustificationType(getReasonByCategoryAndCode(ReasonCategory.JUSTIFICATION_PK7W.value(), requestNoteDtl
							.getJustificationType().getReasonCode()));
					finaliseJustype(requestNoteDtl, originalReasonCode);
					// 27868 - end
				}
			} // 27868 - start
			else if (requestNoteDtl.getProcurementType().getParameterCode()
					.equalsIgnoreCase(ParameterConst.PROC_TYPE_SPECIAL_ITEM)) {
				requestNoteDtl.setProcurementType(getParameterByTypeAndCode(ParameterTypeConst.PROCUREMENT_TYPE_RPT,
						requestNoteDtl.getProcurementType().getParameterCode()));
				if (requestNoteDtl.getJustificationType().getReasonCode() != null) {
					requestNoteDtl.setJustificationType(getReasonByCategoryAndCode(ReasonCategory.JUSTIFICATION_SIT
							.value(), requestNoteDtl.getJustificationType().getReasonCode()));
				}
			}
			// 27868 - end
			else {
				requestNoteDtl.setJustificationType(null);
			}

			// price type
			if (getManagedBean().getPriceType() != null) {
				requestNoteDtl.setPriceType(getParameterByTypeAndCode(ParameterTypeConst.PRICE_TYPE, getManagedBean()
						.getPriceType()));
				prInquiry.setPriceType(requestNoteDtl.getPriceType());
			}

			if (prInquiryNo == null) {
				List<ScRequestNoteDtl> requestNoteDtls = new ArrayList<ScRequestNoteDtl>();

				ScRequestNote requestNote = new ScRequestNote();
				requestNote.setTitle(prInquiry.getTitle());
				requestNote.setRequestNoteNo(iRequestNoteSO.generateRequestNoteNo());

				requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SC);
				setUserDetails(requestNote);
				requestNoteDtl.setScRequestNote(requestNote);

				requestNoteDtl.setAssignedUser(facesScopes.getUserSessionBean());

				// selected supplier item
				requestNoteDtl.setScRequestSupplierItem(scRequestSupplierItem);

				// proc type
				// 27868 - start
				requestNoteDtl.setProcurementType(getParameterByTypeAndCode(
						validateProcurementTypeisRPT(dataCreatedDate), requestNoteDtl.getProcurementType()
								.getParameterCode()));
				// 27868 - end
				// title
				requestNoteDtl.setProcurementTitle(prInquiry.getTitle());

				requestNoteDtls.add(requestNoteDtl);

				List<ScRequestSupplierItem> requestSupplierItems = new ArrayList<ScRequestSupplierItem>();
				// validate order quantity
				if (scRequestSupplierItem.getRequestNoteItem().getQuantity() != null
						&& scRequestSupplierItem.getRequestNoteItem().getQuantity().intValue() < 0) {
					throw new EpException(MessageConst.ERROR_100033, new String[] { ResourceBundleUtils.getMessage(
							"lbl.sc.order.qty", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
				}
				// 27868 - start
				if (validateProcurementTypeisRPT(dataCreatedDate).equalsIgnoreCase( ParameterTypeConst.PROCUREMENT_TYPE)) {
					validateUserProfileAgencyForSpecialItem(requestNoteDtls);
				}
				validateReasonStatus(requestNoteDtls);
				validateSupplierAccountTypeForPK7AndSpecialItem(requestNoteDtls, scRequestSupplierItem);
				// 27868 - end
				// Add selected supplier item.
				scRequestSupplierItem.setScRequestNoteDtl(requestNoteDtls);
				requestSupplierItems.add(scRequestSupplierItem);
				prInquiry.setScRequestSupplierItems(requestSupplierItems);

				prInquiry.setCreatedDate(DateUtils.getCurrentDate());
				prInquiry.setWorkflowStatuses(null);

				finalizeRemarks(prInquiry.getRemarks());

				List<ScRequestItem> requestItems = new ArrayList<ScRequestItem>();
				requestItems.add(prInquiry.getRequestItem());
				requestNote.setRequestNoteItems(requestItems);
				prInquiry.getRequestItem().setRequestNote(requestNote);

				requestNote.setRequestNoteDtls(requestNoteDtls);
				reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
				reqDTO.setPmUser(facesScopes.getUserSessionBean());
				reqDTO.setRoleCode(getUserRoleCode());
				reqDTO.setPrInquiryDetail(prInquiry);
				reqDTO.setOrgProfileId(getManagedBean().getPmOrgProfile().getOrgProfileId());
				reqDTO.setRequestNote(requestNote);
				if (outcome.isEmpty()) {
					reqDTO.setSubmit(Boolean.FALSE);
				} else if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome)) {
					reqDTO.setSubmit(Boolean.TRUE);
				}

				prInquiryResDTO = getPrInquirySO().submitPrInquiry(reqDTO);
			} else {
				ScRequestNote requestNote = requestNoteDtl.getScRequestNote();
				SmSupplier smSupplier = scRequestSupplierItem.getSmSupplier();
				
				//Start - #26129
				if (prInquiry.getCurrentStatus() != null) {
					getCurrentStatus(prInquiry.getCurrentStatus().getPmStatus());
					PmStatus pmStatus = getManagedBean().getPriStatus();
					if (ParameterConst.DOCTYPE_PURCHASE_INQUIRY.equals(pmStatus.getDocType())
							&& DPStatusConstants.PRI_PENDING_SUBMISSION.equals(pmStatus.getStatusId())) {
						validateRequiredRemark(prInquiry);
					}
				}							
				//End - #26129
				finalizeRemarks(prInquiry.getRemarks());

				requestNote.setRequestNoteDtls(Arrays.asList(requestNoteDtl));
				
				// 27868 - start
				if (validateProcurementTypeisRPT(dataCreatedDate).equalsIgnoreCase( ParameterTypeConst.PROCUREMENT_TYPE)) {					
					validateUserProfileAgencyForSpecialItem(requestNote.getRequestNoteDtls());
				}
				validateReasonStatus(requestNote.getRequestNoteDtls());
				validateSupplierAccountTypeForPK7AndSpecialItem(requestNote.getRequestNoteDtls(),
						scRequestSupplierItem);
				// 27868 - end
				
				reqDTO.setPmUser(facesScopes.getUserSessionBean());
				reqDTO.setRoleCode(getUserRoleCode());
				reqDTO.setPrInquiryDetail(prInquiry);
				reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
				reqDTO.setTaskId(getManagedBean().getTaskId());
				reqDTO.setRequestNote(requestNote);
				reqDTO.setOutcome(outcome);
				reqDTO.setOrgProfileId(getManagedBean().getPmOrgProfile().getOrgProfileId());
				reqDTO.setSupplierId(smSupplier.getSupplierId());

				prInquiryResDTO = getPrInquirySO().updatePrInquiry(reqDTO);
			}

			if (prInquiryResDTO.isSuccessful()) {
				ScPrInquiry scPrInquiry = prInquiryResDTO.getPrInquiryDetail();
				if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome)) {
					successMessage(MessageConst.INFO_700026, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_PURCHASE_INQUIRY, scPrInquiry.getPrInquiryNo(), Boolean.TRUE);
					// clear data
					super.renewSesionObject();
				} else {
					if (prInquiryNo == null) {
						successMessage(MessageConst.INFO_700024, ParameterTypeConst.DOCUMENT_TYPES,
								ParameterConst.DOCTYPE_PURCHASE_INQUIRY, scPrInquiry.getPrInquiryNo(), Boolean.TRUE);
						super.renewSesionObject();
					} else {
						successMessage(MessageConst.INFO_700025, ParameterTypeConst.DOCUMENT_TYPES,
								ParameterConst.DOCTYPE_PURCHASE_INQUIRY, scPrInquiry.getPrInquiryNo(), Boolean.FALSE);
						getManagedBean().setScPrInquiry(scPrInquiry);
						getManagedBean().setRequestNoteDtl(prInquiryResDTO.getScRequestNoteDtl());
					}
				}
			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
		} finally { //#29739 - bugs error validation not appear
		}
		return null;
	}

	// 27868 - start
	/**
	 * Set justification type if missing using original reason code.
	 */
	private void finaliseJustype(ScRequestNoteDtl requestNoteDtl, String originalReasonCode) {
	    if (requestNoteDtl.getJustificationType() == null) {
	        requestNoteDtl.setJustificationType(
	            getReasonByCategoryAndCode(
	                ReasonCategory.JUSTIFICATION_PK7.value(),
	                originalReasonCode
	            )
	        );
	    }
	}
	// 27868 - end

	public String supplierSave() {
		debugLogger("supplierSave");
		Integer stockAvailable = getManagedBean().getStockAvailable();
		validateSupplierSave(stockAvailable);

		ScRequestNote scRequestNote = getManagedBean().getRequestNote();
		ScRequestNoteDtl scRequestNoteDtl = getManagedBean().getRequestNoteDtl();

		ScPrInquiry prInquiry = getManagedBean().getScPrInquiry();
		finalizeRemarks(prInquiry.getRemarks());

		ScRequestSupplierItem scRequestSupplierItem = getManagedBean().getScRequestSupplierItem();
		updatePricing(scRequestSupplierItem);
		//#29236 CR 26129: Static Code Analysis Start A "NullPointerException" could be thrown; "scRequestSupplierItem" is nullable here.
		if (scRequestSupplierItem != null) {
			prInquiry.setScRequestSupplierItems(Arrays.asList(scRequestSupplierItem));
			prInquiry.setIsStockAvailable(stockAvailable);

			PrInquiryReqDTO reqDTO = new PrInquiryReqDTO();
			reqDTO.setPrInquiryId(prInquiry.getPrInquiryId());
			reqDTO.setPrInquiryDetail(prInquiry);

			FacesScopes facesScopes = new FacesScopes();
			reqDTO.setPmUser(facesScopes.getUserSessionBean());
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(getManagedBean().getTaskId());

			PrInquiryResDTO resDTO = getPrInquirySO().savePrInquiryPrice(reqDTO);

			if (resDTO.isSuccessful()) {
				ScPrInquiry resPrInquiry = resDTO.getPrInquiryDetail();
				successMessage(MessageConst.INFO_700025, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_PURCHASE_INQUIRY, resPrInquiry.getPrInquiryNo(), Boolean.FALSE);
				getManagedBean().setSupplierItemPrices(resDTO.getSupplierItemPrices());

				ScRequestSupplierItem resRequestSupplierItem = resDTO.getScRequestSupplierItem();
				scRequestSupplierItem.setSupplierItemPrices(resDTO.getSupplierItemPrices());
				scRequestSupplierItem.setRequestNoteItem(resDTO.getScRequestItem());
				scRequestSupplierItem.setChangedDate(resRequestSupplierItem.getChangedDate());

				scRequestNoteDtl.setRequestItem(resDTO.getScRequestItem());
				scRequestNote.setRequestNoteItems(Arrays.asList(resDTO.getScRequestItem()));
				scRequestNote.setRequestNoteDtls(Arrays.asList(scRequestNoteDtl));

				getManagedBean().setRequestNote(scRequestNote);
				getManagedBean().setRequestNoteDtl(scRequestNoteDtl);
				getManagedBean().setScPrInquiry(resPrInquiry);
				getManagedBean().setScRequestSupplierItem(scRequestSupplierItem);
			}
			//#29236 CR 26129: Static Code Analysis End 
		} else {
			throw new EpException(MessageConst.ERROR_700020);
		}
		return null;
	}

	public void validateSupplierSave(Integer stockAvailable) {
		if (stockAvailable == null) {
			throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
					"lbl.sc.price.inquiry.stock.avail", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()) });
		} else if (stockAvailable == ParameterConst.PI_STOCK_AVAILABILITY) {
			List<ScSupplierItemPrice> scSupplierItemPrices = getManagedBean().getSupplierItemPrices();

			if (scSupplierItemPrices != null) {
				for (ScSupplierItemPrice scSupplierItemPrice : scSupplierItemPrices) {
					if (scSupplierItemPrice.getItemPrice() == null) {
						throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
								"lbl.sc.price.inquiry.price.offered", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale()) });
					}
				}
			}
		}
	}

	/**
	 * update supplier response - save data into database
	 * and BPM process will route to the next respective user. and also
	 * authenticating spki before submit price details
	 * 
	 * @param pmDigiSign
	 *            the pm digi sign page navigate to acknowledge page.
	 */
	public String updateSupplierResponse(PmDigiSign pmDigiSign) {
		log.info("updateSupplierResponse");
		PrInquiryReqDTO reqDTO = new PrInquiryReqDTO();
		ScPrInquiry prInquiry = getManagedBean().getScPrInquiry();
		ScRequestSupplierItem scRequestSupplierItem = getManagedBean()
				.getScRequestSupplierItem();
		
		// update pricing
		updatePricing(scRequestSupplierItem);
		
		finalizeRemarks(prInquiry.getRemarks());

		// gpki only for govt seller 
		if(isDigiSigningRequired(RoleCode.GOVT_SELLER.value())) {
			if (null != pmDigiSign) { 
				updateSpkiDigiSignData(pmDigiSign,
						ParameterConst.DOCTYPE_PURCHASE_INQUIRY, prInquiry.getPrInquiryId(),
						prInquiry.getPrInquiryNo(), 
						prepareOriginalText(Boolean.TRUE)); 
			}
			RequestContext.getCurrentInstance().execute("gpki_dlg.hide();");

		} else {
			// spki
			String originalText = prepareOriginalText(Boolean.FALSE); 
			if (null != pmDigiSign) {
				updateSpkiDigiSignData(pmDigiSign,
						ParameterConst.DOCTYPE_PURCHASE_INQUIRY,
						prInquiry.getPrInquiryId(), prInquiry.getPrInquiryNo(),
						originalText);
				//#29236 CR 26129: Static Code Analysis Start A "NullPointerException" could be thrown; "pmDigiSign" is nullable here.
				byte[] signedData = getManagedBean().getSpkiBackingBean()
						.spkiSignTransaction(originalText);
				pmDigiSign.setSpkiSignedData(signedData);
				//#29236 CR 26129: Static Code Analysis End
			}			
		}

		RequestContext.getCurrentInstance().execute("blockWidget.hide();");

		// updating price
		List<ScRequestSupplierItem> scRequestSupplierItems = new ArrayList<ScRequestSupplierItem>();
		scRequestSupplierItems.add(scRequestSupplierItem);
		prInquiry.setScRequestSupplierItems(scRequestSupplierItems);

		prInquiry.setIsStockAvailable(getManagedBean().getStockAvailable());
		reqDTO.setPrInquiryDetail(prInquiry);
		FacesScopes facesScopes = new FacesScopes();
		reqDTO.setPmUser(facesScopes.getUserSessionBean());
		reqDTO.setRoleCode(getUserRoleCode());
		reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
		reqDTO.setOutcome(getManagedBean().getAction());
		reqDTO.setTaskId(getManagedBean().getTaskId());
		reqDTO.setPmDigiSign(pmDigiSign);
		//#26129 - Start
		reqDTO.setCurrentStatusId(getManagedBean().getCurrentStatusId());
		//#26129 - End

		PrInquiryResDTO resDTO = getPrInquirySO().submitPrInquiryPrice(reqDTO);
		if (resDTO.isSuccessful()) {
			ScPrInquiry scPrInquiry = resDTO.getPrInquiryDetail();
			successMessage(MessageConst.INFO_700026,
					ParameterTypeConst.DOCUMENT_TYPES,
					ParameterConst.DOCTYPE_PURCHASE_INQUIRY,
					scPrInquiry.getPrInquiryNo(), Boolean.TRUE);
			// clear data
			super.renewSesionObject();
		} else {
			throw new EpException(MessageConst.ERROR_700020);
		}
		return null;
	}
	
	private void updatePricing(ScRequestSupplierItem scRequestSupplierItem){
		List<ScSupplierItemPrice> result = getManagedBean().getSupplierItemPrices();
		if (result != null) {
			for (ScSupplierItemPrice scSupplierItemPrice : result) {
				int index =  scRequestSupplierItem.getSupplierItemPrices().indexOf(scSupplierItemPrice);
				scRequestSupplierItem.getSupplierItemPrices().set(index, scSupplierItemPrice);
			}
		}
	}
	
	/**
	 * Prepare original text to pass spki authentication
	 * 
	 * @return the string
	 */
	private String prepareOriginalText(boolean isG2GSupplier) {
		FacesScopes facesScopes = new FacesScopes();
		StringBuilder sb = new StringBuilder();
		SmSupplier smSupplier = null;
		String countryCode = "";
		String orginalText = "";
		//#29236 CR 26129: Static Code Analysis Start A "NullPointerException" could be thrown; "smSupplier" is nullable here.
		String regNo = "";

		if (getManagedBean().getScRequestSupplierItem() != null) {
			ScRequestSupplierItem scRequestSupplierItem = getManagedBean()
					.getScRequestSupplierItem();
			smSupplier = scRequestSupplierItem.getSmSupplier();

			if (smSupplier != null) {
				if (smSupplier.getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses() != null
						//#29236 CR 26129: Static Code Analysis Start Use isEmpty() to check whether the collection is empty or not. 
						&& !smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().isEmpty()) {
					List<SmCompanyAddress> companyAddress = smSupplier.getSmAppl()
							.getSmCompanyBasic().getSmCompanyAddresses();

					for (SmCompanyAddress compaAddress : companyAddress) {
						if (compaAddress.getAddressType().equalsIgnoreCase(
								AddressType.CONTACT_ADDRESS.value())) {
							countryCode = compaAddress.getSmAddress()
									.getPmCountry().getCountryCode();
							break;
						}
					}
				}
				regNo = smSupplier.getRegNo();				
			}		
		}
		
		sb.append(getManagedBean().getScPrInquiry().getPrInquiryNo()).append("|")
				.append(facesScopes.getUserSessionBean().getIdentificationNo())
				.append("|");

		if (isG2GSupplier) {
			UserReqDTO userReqDTO  = new UserReqDTO();
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			userReqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			PmUser pmUser = userSO.retrieveUserProfileByUserId(userReqDTO).getPmUser();
			if(pmUser.getOrgTypeCode() != null){
				sb.append(pmUser.getOrgTypeCode().getParameterCode());
			}

		} else {
			sb.append(regNo);
		}
		//#29236 CR 26129: Static Code Analysis End
		sb.append("|").append(countryCode);
		orginalText = sb.toString();
		return orginalText;
	}
	
	
	/**
	 * submit supplier response details. submit - save data into database and
	 * BPM process will route to the next respective user. and also
	 * authenticating spki or gpki before submit price details
	 * 
	 * @param pmDigiSign
	 *            the pm digi sign if quote is single, page navigate to
	 *            acknowledge page otherwise remains.
	 */
	public void supplierSubmit() {
		debugLogger("supplierSubmit");

		//gpki for only govt seller
		if(isDigiSigningRequired(RoleCode.GOVT_SELLER.value())) {
			this.getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");

		}  else {
			// spki
			getManagedBean().getSpkiBackingBean().getChallengeQuestions();
			this.getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("spki_dlg.show();");
		}
	}

	/**
	 * Gpki sign action. Authenticating gpki before update bpm.
	 * 
	 * @param digiSign
	 *            the digi sign
	 */
	public void gpkiSignAction(PmDigiSign digiSign) {
		updateSupplierResponse(digiSign);
	}

	/**
	 * Event for procurement type drop down in Purchase inquiry. to show
	 * justification drop down and justification remark based on procurement
	 */
	public String checkProcTypeOption() {
		String procurementType = getManagedBean().getRequestNoteDtl().getProcurementType().getParameterCode();
		// 27868 - start
		Date dataCreatedDate;
		dataCreatedDate = assignActualDate();
		// 27868 - end
		
		if (procurementType.equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL)
				|| procurementType.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			getManagedBean().setProcTypeOption(Boolean.FALSE);
			getManagedBean().setJustTypeOption(Boolean.FALSE);

		} else if (procurementType.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
			getManagedBean().setJustDropdown(getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_AD_HOC.value()));
		} else if (procurementType.equalsIgnoreCase(ParameterConst.PROC_TYPE_PK7)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
			// 27868 - start
			getManagedBean().setJustDropdown(getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_PK7W.value()));
			if (getParameterCodeForPK7(dataCreatedDate).equalsIgnoreCase(ReasonCategory.JUSTIFICATION_PK7.value())) {
				finalizeWIPDropdownPK7();
			} else {
				getManagedBean()
						.setJustDropdown(getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_PK7W.value()));
				inactiveReasonChecking();
			}
			// 27868 - end
		} // 27868 - start
		else if (procurementType.equalsIgnoreCase(ParameterConst.PROC_TYPE_SPECIAL_ITEM)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
			PmOrgProfile pmOrgProfile = retrievePmOrgProfile();
			if (pmOrgProfile != null) {
				getManagedBean().setJustDropdown(
						getReasonCodeDropdownValues(pmOrgProfile.getOrgProfileId(),
								ReasonCategory.JUSTIFICATION_SIT.value()));
			} else {
				getManagedBean().setJustDropdown(getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_SIT.value()));
			}
			inactiveReasonChecking();
		}
		// 27868 - end
		return procurementType;
	}

	/**
	 * Finalize PK7 justification dropdown by resolving old-to-new reason
	 * mapping, handling special item cases, and filtering inactive
	 * justification reasons.
	 */
	private void finalizeWIPDropdownPK7() {
		if (getManagedBean().getRequestNoteDtl().getJustificationType() != null
				&& getManagedBean().getRequestNoteDtl().getJustificationType().getReasonId() != null) {
			IPmPK7MapSO pk7MapSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IPmPK7MapSO.class);

			PmPK7Mapping pmPK7Mapping = pk7MapSO.findByOldReasonId(getManagedBean().getRequestNoteDtl()
					.getJustificationType().getReasonId());

			if (pmPK7Mapping != null) {
				defaultToPK7W(pmPK7Mapping);
				if (pmPK7Mapping.getNewParameterCode() != null
						&& pmPK7Mapping.getNewParameterCode()
								.equalsIgnoreCase(ReasonCategory.JUSTIFICATION_SIT.value())) {
					defaultToSpecialtem();
				}
			} else {
				defaultToSpecialtem();
			}
		} else {
			// set default PK7
			getManagedBean().getRequestNoteDtl().setProcurementType(
					getParameterByTypeAndCode(ParameterTypeConst.PROCUREMENT_TYPE_RPT, ParameterConst.PROC_TYPE_PK7));
			getManagedBean().setJustDropdown(getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_PK7W.value()));
			inactiveReasonChecking();
		}
	}

	/**
	 * Defaults quotation to Special Item flow when justification matches SIT
	 * list. Falls back to PK7W when no matching Special Item is found.
	 */
	protected void defaultToSpecialtem() {
		// special item
		IReasonSO reasonSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IReasonSO.class);

		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setReasonId(getManagedBean().getRequestNoteDtl().getJustificationType().getReasonId());
		reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());

		ReasonResDTO resDTO = reasonSO.retrieveReasonDescById(reqDTO);
		PmReasonDesc reasonDesc = (resDTO != null) ? resDTO.getReasonDesc() : null;
		boolean matched = false;

		if (reasonDesc != null && reasonDesc.getReasonDesc() != null) {

			List<PmReasonDesc> reasonDescs = ReasonHelper.getInstance().getReasonDescList(
					ReasonCategory.JUSTIFICATION_SIT.value());

			for (PmReasonDesc sitReason : reasonDescs) {
				if (sitReason != null
						&& reasonDesc.getReasonDesc().trim().equalsIgnoreCase(sitReason.getReasonDesc().trim())) {
					matched = true;
					// set procurement type = Special Item
					getManagedBean().getRequestNoteDtl().setProcurementType(
							getParameterByTypeAndCode(ParameterTypeConst.PROCUREMENT_TYPE_RPT,
									ParameterConst.PROC_TYPE_SPECIAL_ITEM));
					getManagedBean().getRequestNoteDtl().setJustificationType(sitReason.getPmReason());

					PmOrgProfile pmOrgProfile = retrievePmOrgProfile();

					finaliseSpecialItemDropDown(pmOrgProfile);

					inactiveReasonChecking();
					break;
				}
			}

			if (!matched) {
				// set default PK7
				getManagedBean().getRequestNoteDtl().setProcurementType(
						getManagedBean().getRequestNoteDtl().getProcurementType());
				getManagedBean().getRequestNoteDtl().setJustificationType(
						getManagedBean().getRequestNoteDtl().getJustificationType());
				getManagedBean()
						.setJustDropdown(getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_PK7W.value()));
				inactiveReasonChecking();
			}
		}
	}

	/**
	 * Finalises Special Item justification dropdown based on organisation
	 * profile. Includes current WIP reason when applicable.
	 */
	protected void finaliseSpecialItemDropDown(PmOrgProfile pmOrgProfile) {
		if (pmOrgProfile != null) {
			getManagedBean().setJustDropdown(
					getReasonCodeDropdownValuesCombineWIPCode(
							getManagedBean().getRequestNoteDtl().getJustificationType().getReasonId(),
							getReasonCodeDropdownValues(pmOrgProfile.getOrgProfileId(),
									ReasonCategory.JUSTIFICATION_SIT.value())));
		} else {
			getManagedBean().setJustDropdown(
					getReasonCodeDropdownValuesCombineWIPCode(getManagedBean().getRequestNoteDtl()
							.getJustificationType().getReasonId(),
							getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_SIT.value())));
		}
	}

	/**
	 * Applies PK7W defaults based on PK7 mapping. Handles inactive old PK7 or
	 * mapped PK7W justification.
	 */
	protected void defaultToPK7W(PmPK7Mapping pmPK7Mapping) {
		if (pmPK7Mapping.getNewReason() == null) {
			// no PK7 New mapping, basically the old PK7 data already inactive
			getManagedBean().getRequestNoteDtl().setProcurementType(
					getManagedBean().getRequestNoteDtl().getProcurementType());
			getManagedBean().getRequestNoteDtl().setJustificationType(
					getManagedBean().getRequestNoteDtl().getJustificationType());
			getManagedBean().setJustDropdown(getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_PK7W.value()));
			inactiveReasonChecking();
		} else if (pmPK7Mapping.getNewParameterCode().equalsIgnoreCase(ReasonCategory.JUSTIFICATION_PK7W.value())) {
			// PK7 New
			getManagedBean().getRequestNoteDtl().setProcurementType(pmPK7Mapping.getNewProcurementType());
			getManagedBean().getRequestNoteDtl().setJustificationType(pmPK7Mapping.getNewReason());
			getManagedBean().setJustDropdown(getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_PK7W.value()));
			inactiveReasonChecking();
		}
	}

	public void procurementTypeEvent() {
		log.info("procurementTypeEvent");
		String procurementType = checkProcTypeOption();
		getManagedBean().getRequestNoteDtl().setJustificationType(new PmReason());
		// 27868 - start
		validatePK7(getManagedBean().getRequestNoteDtl().getProcurementType());
		// 27868 - end
	}
	
	// 27868 - start
	/**
	 * Validates the PK7 parameter and retrieves related special item and PK7
	 * information messages.
	 * 
	 * @param param
	 *            the {@link PmParameter} object to validate
	 */
	public void validatePK7(PmParameter param) {
		getSpecialItemAndPK7InfoMessage(param);

	}

	// 27868 - end
	
	/**
	 * Validate type of procurement and display threshold message
	 * 
	 * @param procurementType
	 *            the type of procurement
	 */
	public void validatePk7(String procurementType) {
		if (procurementType.equalsIgnoreCase(ParameterConst.PROC_TYPE_PK7)) {
			// MaxProcurementAmt
			// Temp(#34274 - Start)
			// BigDecimal maxProcurementAmt = getPRThresholdAmount();
			// DecimalFormat amountDF = new DecimalFormat(
			// new
			// CommonUtils().getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
							MessageConst.INFO_760440, null, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			// Temp(#34274 - End)
		}
	}

	/**
	 * get task from task list to view purchase inquiry and proceed to the
	 * request note to prepare request note details
	 * 
	 * @param action
	 *            the action
	 * @return the string
	 */
	public String requisitionerAction(String action) {
		log.info("requisitionerAction");

		ScPrInquiry prInquiry = getManagedBean().getScPrInquiry();
		if(StringUtils.equalsIgnoreCase("PROCESSED", action)) {
			// get RequestNote Backing bean and manage bean and set Request Note
			// to RequestNote manage bean
			PortletSession portletSession = (PortletSession) FacesContext
					.getCurrentInstance().getExternalContext()
					.getSession(false);
			RequestNoteBackingBean reqNoteBackingBean = (RequestNoteBackingBean) portletSession
					.getAttribute("requestNoteBackingBean");
			if (reqNoteBackingBean == null) {
				reqNoteBackingBean = new RequestNoteBackingBean();
			}

			// redirect to requestNote screen
			RequestNoteReqDTO requestDTO = new RequestNoteReqDTO();
			requestDTO.setRequestNoteId(getManagedBean().getRequestNote()
					.getRequestNoteId());
			RequestNoteResDTO resDTO = getRequestNoteSO().getRequestNote(
					requestDTO);
			ScRequestNote requestNote = resDTO.getRequestNoteDetail();
			reqNoteBackingBean.getManagedBean().setRequestNote(requestNote);

			if (requestNote.getRequestNoteDtls() != null
					&& requestNote.getRequestNoteDtls().size() > 0) {
				List<ScRequestNoteDtl> requestNoteDtls = new ArrayList<ScRequestNoteDtl>();

				for (ScRequestNoteDtl scRequestNoteDtl : requestNote.getRequestNoteDtls()) {
					ScRequestSupplierItem scRequestSupplierItem =  scRequestNoteDtl.getScRequestSupplierItem();

					scRequestNoteDtl.setFulfilmentType(getParameterByTypeAndCode(
							ParameterTypeConst.FULFILMENT_TYPE,
							ParameterConst.FULFILMENT_TYPE_ONE_OFF));

					if (null == scRequestNoteDtl.getPriceType()) {
						scRequestNoteDtl.setPriceType(new PmParameter());
					} else {
						scRequestNoteDtl.setPriceTypeId(scRequestNoteDtl
								.getPriceType().getParameterId());
					}

					if (null == scRequestNoteDtl.getAssignedUser()) {
						scRequestNoteDtl.setAssignedUser(new PmUser());
					}

					if (null == scRequestNoteDtl.getProcurementTypeCategory()) {
						scRequestNoteDtl
						.setProcurementTypeCategory(new PmParameter());
					}

					if (null == scRequestNoteDtl.getDeliverTermType()) {
						scRequestNoteDtl.setDeliverTermType(getDeliveryTermType());
					}

					if (scRequestNoteDtl.getJustificationType() != null) {
						reqNoteBackingBean.getManagedBean().setJustificationType(
								scRequestNoteDtl.getProcurementType()
								.getParameterCode());
					}

					if (scRequestNoteDtl.getJustification() != null) {
						reqNoteBackingBean.getManagedBean().setJustification(
								scRequestNoteDtl.getJustification());
					}
					
					if(scRequestSupplierItem != null){
						scRequestNoteDtl.getScRequestSupplierItem().setSupplierId(
								scRequestNoteDtl.getScRequestSupplierItem()
								.getSmSupplier().getSupplierId());

						// check purchase inquiry item
						if(scRequestNoteDtl.isShowPurchaseInquiry()){
							ScPrInquiry scPrInquiry = null;
							if (scRequestNoteDtl.getPiRequestSupplierItem() != null){
								scPrInquiry = scRequestNoteDtl.getPiRequestSupplierItem().getScPrInquiy();
							}
							if(scRequestSupplierItem.getScPrInquiy() != null){
								scPrInquiry = scRequestSupplierItem.getScPrInquiy();
							}
							if(scPrInquiry != null){
								reqNoteBackingBean.getManagedBean().setPrInquiryId(scPrInquiry.getPrInquiryId());
							}
						}
						
						reqNoteBackingBean.supplierListener(scRequestNoteDtl
								.getScRequestSupplierItem().getSupplierId(),
								scRequestNoteDtl.getPriceType().getParameterId(),
								scRequestNoteDtl, scRequestNoteDtl.getAssignedUser()
								.getUserId(), scRequestNoteDtl
								.getProcurementTypeCategory().getParameterId());
					}
					
					StringBuilder sb = new StringBuilder();
					sb.append("requestNoteItem.requestNoteItemId^");
					sb.append(scRequestNoteDtl.getRequestItem()
							.getRequestNoteItemId());
					
					// purchase inquiry
					if (prInquiry.getIsStockAvailable().equals(
							ParameterConst.PI_NO_STOCK)){
						
						// set condition field for saved request note
						if (scRequestNoteDtl.isShowPurchaseInquiry()) {
							sb.append("|");
							sb.append("requestSupplierItemId^");
							sb.append(scRequestNoteDtl.getPiRequestSupplierItem()
									.getRequestSupplierItemId());

							sb.append("^");
							sb.append(SearchOperator.OPERATOR_NOT_IN.value());
						}

						// bofore save rn set null as supplier item for no stock supplier
						if (null == requestNote.getCurrentStatus()) {
							scRequestNoteDtl.setScRequestSupplierItem(null);
						}
						
						String procType = scRequestNoteDtl
								.getProcurementType()
								.getParameterCode();
						// validate pi if no stock
						if (procType.equalsIgnoreCase(
										ParameterConst.PROC_TYPE_NORMAL) ||
										procType.equalsIgnoreCase(
												ParameterConst.PROC_TYPE_PK7)) {
							PrInquiryReqDTO inquiryReqDTO = new PrInquiryReqDTO();
							inquiryReqDTO.setRequestItemId(scRequestNoteDtl
									.getRequestItem().getRequestNoteItemId());

							PrInquiryResDTO inquiryResDTO = getPrInquirySO()
									.isPIProceedabletoRN(inquiryReqDTO);
							if (!inquiryResDTO.isProceedable()) {
								throw new EpException(MessageConst.ERROR_760358);
							}
						}
					}
					
					scRequestNoteDtl.setConditionField(sb.toString());
					
					reqNoteBackingBean.getManagedBean().setProcurementType(
							scRequestNoteDtl.getProcurementType()
							.getParameterCode());
					reqNoteBackingBean.getManagedBean().setRequestSupplierItem(
							scRequestNoteDtl.getScRequestSupplierItem());

					// update price
					reqNoteBackingBean.priceTypeChangeListener(scRequestNoteDtl
							.getPriceType().getParameterId(), scRequestNoteDtl);

					reqNoteBackingBean.getManagedBean().setRequestNote(requestNote);
					reqNoteBackingBean.getPreparedBy(scRequestNoteDtl);
					reqNoteBackingBean.getManagedBean().setRequestNoteDtl(
							scRequestNoteDtl);
					
					requestNoteDtls.add(scRequestNoteDtl);
					reqNoteBackingBean.getManagedBean().setRequestSupplierItem(
							scRequestNoteDtl.getScRequestSupplierItem());

					reqNoteBackingBean.getManagedBean()
					.setOutComes(getManagedBean().getOutComes());

					reqNoteBackingBean.getManagedBean().setRequestNoteDtls(
							requestNoteDtls);
					reqNoteBackingBean.getManagedBean().setSourceMethod(
							ParameterConst.METHOD_SC);
					reqNoteBackingBean.getManagedBean().setTaskId(
							getManagedBean().getTaskId());
					// created by
					reqNoteBackingBean.getManagedBean().setCreatedBy(
							reqNoteBackingBean.getUserById(
									requestNote.getCreatedBy()).getUserName());

					reqNoteBackingBean.initializeApproverGroup();
					
					Long rnStatusId = DPStatusConstants.RN_PENDING_SUBMISSION;
					reqNoteBackingBean.getManagedBean().setCurrentStatusId(rnStatusId);

					reqNoteBackingBean.getCurrentStatus(getPmStatusByStatusId(rnStatusId));
					
					reqNoteBackingBean.getManagedBean().setDocStatus(getStatusDescById(rnStatusId)); //#38538																		  
					requestNote.setRemarks(initializeRemarks(requestNote,
							requestNote.getCurrentStatus(),
							requestNote.getRemarks(), ScRequestNoteRemark.class));

					reqNoteBackingBean.getManagedBean().setFulfillmentType(
							ParameterConst.FULFILMENT_TYPE_ONE_OFF);
					reqNoteBackingBean.getManagedBean().setItemSearchBackBtn(Boolean.TRUE);
				}
			}
			
			// add workflow status for RN
			 updateWorkFlowAndRemarks(requestNote);
			// redirect to RequestNote screen
			return "requestNoteMain";

		} else if (StringUtils.equalsIgnoreCase("CANCELPI", action)) { //Start - #26129
			validateRequiredRemark(prInquiry);
			finalizeRemarks(prInquiry.getRemarks());
			getPrInquirySO().updatePICurrentWorkflowStatus(prInquiry, DPStatusConstants.PRI_CANCELLED);
			PrInquiryReqDTO reqDTO = new PrInquiryReqDTO();
			reqDTO.setPrInquiryDetail(prInquiry);
			FacesScopes facesScopes = new FacesScopes();
			reqDTO.setPmUser(facesScopes.getUserSessionBean());
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setOutcome(BpmOutcomeConst.BPM_CANCEL);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			PrInquiryResDTO resDTO = getPrInquirySO().submitPrInquiryPrice(reqDTO);

			if (resDTO.isSuccessful()) {
				successMessage(MessageConst.INFO_700089, new String[] { getManagedBean().getScPrInquiry()
						.getPrInquiryNo() }, Boolean.TRUE);
			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
			return null;
		} else {													   //End - #26129
			finalizeRemarks(prInquiry.getRemarks());
			PrInquiryReqDTO reqDTO = new PrInquiryReqDTO();
			reqDTO.setPrInquiryDetail(prInquiry);
			FacesScopes facesScopes = new FacesScopes();
			reqDTO.setPmUser(facesScopes.getUserSessionBean());
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			PrInquiryResDTO resDTO = getPrInquirySO().submitPrInquiryPrice(
					reqDTO);

			if (resDTO.isSuccessful()) {
				successMessage(MessageConst.INFO_700010,
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_PURCHASE_INQUIRY,
						getManagedBean().getScPrInquiry().getPrInquiryNo(),
						Boolean.TRUE);
			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
			return null;
		}
	}

	/**
	 * Gets the delivery term type.
	 * 
	 * @return the delivery term type
	 */
	private PmParameter getDeliveryTermType() {
		//#29236 CR 26129: Static Code Analysis Start Immediately return this expression instead of assigning it to the temporary variable "deliveryTermType".
		//PmParameter deliveryTermType = getParameterByTypeAndCode( ParameterTypeConst.DELIVERY_TERM, ParameterConst.SQ_DURATION_DAY);
		//return deliveryTermType;
		return getParameterByTypeAndCode( ParameterTypeConst.DELIVERY_TERM, ParameterConst.SQ_DURATION_DAY);
		//#29236 CR 26129: Static Code Analysis End
	}

	/**
	 * Pre render page navigation to purchase inquiry based on task id
	 * 
	 * @return void
	 */
	public void preRenderView() {
		log.info("preRenderView");

		ScRequestNoteDtl requestNoteDtl = null;
		SCDirectPurchaseDataType directPurchaseDataType = super.getBpmTaskDetails(SCDirectPurchaseDataType.class, null);
		Long currentStatusId = getManagedBean().getCurrentStatusId();
		// Purchase Inquiry
		PrInquiryReqDTO prInquiryReqDTO = new PrInquiryReqDTO();
		Long prInquiryID = Long.valueOf(directPurchaseDataType.getDpId());
		prInquiryReqDTO.setPrInquiryId(prInquiryID);
		prInquiryReqDTO.setFetchSubEntity(true);

		if (DPStatusConstants.PRI_PENDING_SUPPLIER_RESPONSE.equals(currentStatusId) || DPStatusConstants.PRI_PENDING_SUBMISSION.equals(currentStatusId)) {
			prInquiryReqDTO.setFetchSupplierEntity(true);
		}

		IPrInquirySO iPrInquirySO = getPrInquirySO();
		PrInquiryResDTO prInquiryResDTO = iPrInquirySO.getPrInquiry(prInquiryReqDTO);
		ScPrInquiry prInquiry = prInquiryResDTO.getPrInquiryDetail();
		// 27868 - start
		Date dataCreatedDate;
		if (prInquiry != null && prInquiry.getCreatedDate() != null) {
			dataCreatedDate = prInquiry.getCreatedDate();
		} else {
			dataCreatedDate = new Date();
		}
		getManagedBean().setCreatedDate(dataCreatedDate);
		getManagedBean().setParameterTypeRPT(ParameterTypeConst.PROCUREMENT_TYPE_RPT);
		// 27868 - end
		
		boolean isPageNavFlag = Boolean.TRUE;
		if (prInquiry.getScRequestSupplierItems() != null
				&& prInquiry.getScRequestSupplierItems().size() > 0) {
			// get selected supplier
			ScRequestSupplierItem requestSupplierItem = prInquiry.getScRequestSupplierItems().get(0);

			if (requestSupplierItem.getScRequestNoteDtl() != null
					&& requestSupplierItem.getScRequestNoteDtl().size() > 0) {
				requestNoteDtl = requestSupplierItem.getScRequestNoteDtl().get(0);

				// lead time
				if (requestSupplierItem.getSupplierItemPrices() != null
						&& requestSupplierItem.getSupplierItemPrices().size() > 0) {
					requestSupplierItem.getRequestNoteItem().setLeadTime(
							requestSupplierItem.getSupplierItemPrices().get(0).getPricing().getCmSupplierItem()
									.getLeadTime());
				}
				getManagedBean().setRequestNoteDtl(requestNoteDtl);

				processPriPendingSubmission(prInquiry, currentStatusId);
				getManagedBean().setPmOrgProfile(requestSupplierItem.getRequestNoteItem().getRequestNote().getPmOrgProfile());
				getManagedBean().setScRequestSupplierItem(requestSupplierItem);
				getManagedBean().setSupplierItemPrices(requestSupplierItem.getSupplierItemPrices());

				//#26059 - Start
				CatalogueBackingBean catalogueBackingBean = new CatalogueBackingBean();
				if(requestSupplierItem.getRequestNoteItem().getCmItem() !=null){
					CmItem cmItem = requestSupplierItem.getRequestNoteItem().getCmItem(); 
				    catalogueBackingBean.cleanItemNameNA(cmItem);
					requestSupplierItem.getRequestNoteItem().setCmItem(cmItem);
				}
				//#26059 - End

				// RequestNote Details - checking workflow status exits or not
				// for PI to RN
				requestNoteDtl.getRequestItem().setSupplierItems(prInquiry.getScRequestSupplierItems());
				if (requestNoteDtl.getScRequestNote() != null) {
					ScRequestNote requestNote = requestNoteDtl.getScRequestNote();
					getManagedBean().setRequestNote(requestNote);
					if (requestNote.getCurrentStatus() != null) {
						isPageNavFlag = Boolean.FALSE;
					}
				}
				getManagedBean().setRequestNoteDtl(requestNoteDtl);
				
			} else{
				// RequestNote Details - checking workflow status exits or not
				// for PI to RN
				ScRequestNote scRequestNote = requestSupplierItem.getRequestNoteItem().getRequestNote();
				if (scRequestNote != null) {
				    getManagedBean().setRequestNote(scRequestNote);
					if (scRequestNote.getCurrentStatus() != null) {
						isPageNavFlag = Boolean.FALSE;
					}
				}
			}
		}

		// no of response days
		prInquiry.setDayToResponse((int) DateUtils.getDaysDifference(
				getCurrentDateWithoutTime(prInquiry.getCreatedDate()),
				getCurrentDateWithoutTime(prInquiry.getDateToResponse())));

		getManagedBean().setStockAvailable(prInquiry.getIsStockAvailable());
		getManagedBean().setScPrInquiry(prInquiry);

		getCurrentStatus(getPmStatusByStatusId(currentStatusId));
		
		// remarks
		prInquiry.setRemarks(initializeRemarks(prInquiry, prInquiry.getCurrentStatus(), prInquiry.getRemarks(),
				ScPRIRemark.class));

		// title checker
		getManagedBean().setTitleChecker(Boolean.FALSE);
		getManagedBean().setShowButton(Boolean.TRUE);

		// price change lister
		stockAvailabilityEvent();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		if (isPageNavFlag) { // page nav.to purchase inquiry

			// Weather stock is available or not
			//#29236 CR 26129: Static Code Analysis Start Merge this if statement with the enclosing one.
			/*
			if (prInquiry.getIsStockAvailable() != null) {
				if (prInquiry.getIsStockAvailable().equals(ParameterConst.PI_NO_STOCK)) {
					outOfStockMessage();
				}
			}
			*/
			if (prInquiry.getIsStockAvailable() != null && prInquiry.getIsStockAvailable().equals(ParameterConst.PI_NO_STOCK)) {
				outOfStockMessage();
			}
			//#29236 CR 26129: Static Code Analysis End
			
			fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, PURCHASE_INQUIRY_PAGE);

		} else { // page nav.to request note
			String commonRequestNote = requisitionerAction("PROCESSED");
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, commonRequestNote);
		}
	}

	private void processPriPendingSubmission(ScPrInquiry prInquiry, Long currentStatusId) {
		ScRequestSupplierItem requestSupplierItem = prInquiry.getScRequestSupplierItems().get(0);
		ScRequestNoteDtl requestNoteDtl = requestSupplierItem.getScRequestNoteDtl().get(0);

		if (DPStatusConstants.PRI_PENDING_SUBMISSION.equals(currentStatusId)) {
			CmItem cmItem = requestSupplierItem.getRequestNoteItem().getCmItem();
			SmSupplier smSupplier = requestSupplierItem.getSmSupplier();

			List<Long> cmSuppItemIds = getPrInquirySO().cmSupplierItemIds(cmItem.getItemId(),
					smSupplier.getSupplierId());

			List<CmPricing> cmPricings = getICatalogueSO().getCmPricings(cmSuppItemIds);
			getManagedBean().setPriceTypes(
					getPriceTypeDropdownValues(ParameterTypeConst.PRICE_TYPE, getPriceTypes(cmPricings)));

			if (prInquiry.getPriceType() != null) {
				getManagedBean().setPriceType(prInquiry.getPriceType().getParameterCode());
			}

			String procurementType = requestNoteDtl.getProcurementType().getParameterCode();
			getManagedBean().setProcurementType(procurementType);
			// 27868 - start
			Date dataCreatedDate;
			if (getManagedBean().getCreatedDate() != null) {
				dataCreatedDate = getManagedBean().getCreatedDate();
				getManagedBean().setScPrInquiry(prInquiry);
			} else {
				dataCreatedDate = new Date();
			}
			getManagedBean().setCreatedDate(dataCreatedDate);
			String parameterType = validateProcurementTypeisRPT(dataCreatedDate);
			if (isGovtSellerBySupplierId(smSupplier.getSupplierId())) {
				getManagedBean().setProcurementTypes(
						getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE_RPT, Boolean.TRUE));
				inactiveReasonChecking();
				if (getManagedBean().isReasonCodeInActive()) {
					getManagedBean().setDisProcTyp(Boolean.TRUE);
				}
			} else {
				getManagedBean().setProcurementTypes(
						getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE_RPT, Boolean.FALSE));
				inactiveReasonChecking();
				if (getManagedBean().isReasonCodeInActive()) {
					getManagedBean().setDisProcTyp(Boolean.TRUE);
				}
			}
			// 27868 - end
			checkProcTypeOption();
			validatePK7(requestNoteDtl.getProcurementType());// 27868
			getManagedBean().setViewOnly(Boolean.FALSE);
			getManagedBean().setDisProcTyp(Boolean.TRUE);
		} else {
			getManagedBean().setPriceType(getParameterDesc(prInquiry.getPriceType()));
			getManagedBean().setViewOnly(Boolean.TRUE);
			getManagedBean().setDisProcTyp(Boolean.FALSE);
			// 27868 - start
			inactiveReasonChecking();
			if (getManagedBean().isReasonCodeInActive()) {
				getManagedBean().setDisProcTyp(Boolean.TRUE);
			}
			// 27868 - end
		}
	}

	private List<String> getPriceTypes(List<CmPricing> cmPricings) {
		List<String> priceTypes = new ArrayList<String>();
		for (CmPricing cmPricing : cmPricings) {
			if (!priceTypes.contains(cmPricing.getPricingType())) {
				priceTypes.add(cmPricing.getPricingType());
			}
		}
		return priceTypes;
	}

	/**
	 * Gets the current date without time.
	 * 
	 * @param respDate
	 *            the resp date
	 * @return the current date without time
	 */
	public static Date getCurrentDateWithoutTime(Date respDate) {
		Calendar c1 = new GregorianCalendar();
		c1.setTime(respDate);
		Calendar c2 = Calendar.getInstance();
		c2.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
				c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		c2.set(Calendar.MILLISECOND, 0);

		return c2.getTime();
	}

	/**
	 * Page navigation to catalogue search page in Purchase inquiry.
	 */
	public void backToPrevious() {
		log.info("backToPrevious");
		FacesContext fc = FacesContext.getCurrentInstance();
		if (getManagedBean().getNavToPreviousPage() != null) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							getManagedBean().getNavToPreviousPage());
		} else {
			// clear data
			super.renewSesionObject();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "catalogueSearch");
		}
	}

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * Gets the current status.
	 * 
	 * @param pmStatus
	 *            the pm status
	 * @return the current status
	 */
	public void getCurrentStatus(PmStatus pmStatus) {
		getManagedBean().setPriStatus(pmStatus);
	}


	/**
	 * Creates the sc supplier item prices.
	 *
	 * @param scRequestSupplierItem the sc request supplier item
	 * @param pricings the pricings
	 * @return the list
	 */
	private List<ScSupplierItemPrice> createScSupplierItemPrices(
			ScRequestSupplierItem scRequestSupplierItem,
			List<CmPricing> pricings) {
		List<ScSupplierItemPrice> results = new ArrayList<>();
		for (CmPricing pricing : pricings) {
			results.add(createSupplierItemPrice(scRequestSupplierItem, pricing));
		}
		return results;
	}

	/**
	 * Creates the supplier item price.
	 *
	 * @param scRequestSupplierItem the sc request supplier item
	 * @param pricing the pricing
	 * @return the sc supplier item price
	 */
	private ScSupplierItemPrice createSupplierItemPrice(
			ScRequestSupplierItem scRequestSupplierItem, CmPricing pricing) {
		ScSupplierItemPrice result = new ScSupplierItemPrice();
		ScPrInquiry prInquiry = getManagedBean().getScPrInquiry();
		ScPIZone zone = getManagedBean().getScPIZone();

		Map<String, TplSpecZoneDtl> zoneDtlMap = getManagedBean()
				.getZoneDtlMap();
		if (zoneDtlMap == null) {
			zoneDtlMap = new HashMap<String, TplSpecZoneDtl>();
		}

		// zone dtl
		if (pricing.getCmSupplierZone() != null) {
			result.setTplSpecZone(SourcingZoneHelper.getInstance()
					.createSpecZone(pricing.getCmSupplierZone(), zoneDtlMap,
							zone, prInquiry.isBrandNew()));
			if (zone.isBrandNew()) {
				zone.setScPrInquiry(prInquiry);
				prInquiry.setScPIZone(zone);
			}
		}
		getManagedBean().setZoneDtlMap(zoneDtlMap);

		if (pricing.getFromQty() != null) {
			result.setFromQty(new BigDecimal(pricing.getFromQty()));
		}
		if (pricing.getToQty() != null) {
			result.setToQty(new BigDecimal(pricing.getToQty()));
		}
		result.setScRequestSupplierItem(scRequestSupplierItem);
		result.setPricing(pricing);
		if (result.getItemPrice() == null
				|| result.getItemPrice().equals(new BigDecimal(0))) {
			result.setItemPrice(pricing.getPrice());
		}

		result.setPriceType(getParameterByTypeAndCode(
				ParameterTypeConst.PRICE_TYPE, pricing.getPricingType()));
		return result;
	}

	/**
	 * check weather stock available or not if stock available , can proceed to
	 * request note to prepare request note details if stock not available
	 * ,can't proceed to request note
	 */
	public void stockAvailabilityEvent() {
		debugLogger("stockAvailabilityEvent");

		if (getManagedBean().getStockAvailable() != null) {

			if (getManagedBean().getStockAvailable().equals(
					ParameterConst.PI_STOCK_AVAILABILITY)) {
				getManagedBean().setStockAvailabilityFlag(Boolean.TRUE);
			} else {
				getManagedBean().setStockAvailabilityFlag(Boolean.FALSE);
			}
		}
		priceTypeChangeListener();
	}
	

	/**
	 * View purchase inquiry.
	 *
	 * @param purchaseInqId the purchase inq id
	 */
	public void viewPurchaseInquiry(long purchaseInqId) {
		debugLogger("viewPurchaseInquiry");
		IPrInquirySO prInquirySO = (IPrInquirySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
		ScPrInquiry  prInquiry = null;
		//Purchase Inquiry
		PrInquiryReqDTO prInquiryReqDTO = new PrInquiryReqDTO();
		prInquiryReqDTO.setPrInquiryId(purchaseInqId);
		prInquiryReqDTO.setFetchSubEntity(true);
		PrInquiryResDTO prInquiryResDTO = prInquirySO.getPrInquiry(prInquiryReqDTO);
		prInquiry  = prInquiryResDTO.getPrInquiryDetail();

		getManagedBean().setPriceType(getParameterDesc(prInquiry.getPriceType()));
		// status
		if (prInquiry.getCurrentStatus() != null) {
			getCurrentStatus(prInquiry.getCurrentStatus().getPmStatus());
			PmStatus pmStatus = getManagedBean().getPriStatus();
			if (ParameterConst.DOCTYPE_PURCHASE_INQUIRY.equals(pmStatus.getDocType())
					&& DPStatusConstants.PRI_PENDING_SUBMISSION.equals(pmStatus.getStatusId())) {
				String priceType = prInquiry.getPriceType().getParameterCode();
				getManagedBean().setPriceType(priceType);
				getManagedBean().setPriceTypes(
						getPriceTypeDropdownValues(ParameterTypeConst.PRICE_TYPE, Arrays.asList(priceType)));
			}
		}

		// get selected supplier 
		ScRequestSupplierItem requestSupplierItem = prInquiry.getScRequestSupplierItems().get(0);
		prInquiry.setRequestSupplierItem(requestSupplierItem);

		// lead time 
		if(requestSupplierItem.getSupplierItemPrices() != null && requestSupplierItem.getSupplierItemPrices().size() >0){
			requestSupplierItem.getRequestNoteItem().setLeadTime(requestSupplierItem.getSupplierItemPrices().get(0).getPricing().getCmSupplierItem().getLeadTime());
		}

		if(requestSupplierItem.getScRequestNoteDtl() != null 
				&& requestSupplierItem.getScRequestNoteDtl().size()>0){
			ScRequestNoteDtl scRequestNoteDtl = requestSupplierItem.getScRequestNoteDtl().get(0);
			getManagedBean().setRequestNoteDtl(scRequestNoteDtl);
		}
		
		// #26059 - Start
		filterItemName(requestSupplierItem.getRequestNoteItem());
		// #26059 - End
				
		getManagedBean().setPmOrgProfile(requestSupplierItem.getRequestNoteItem().getRequestNote().getPmOrgProfile());
		getManagedBean().setScRequestSupplierItem(requestSupplierItem);
		getManagedBean().setSupplierItemPrices(requestSupplierItem.getSupplierItemPrices());

		// no of response days
				prInquiry.setDayToResponse((int) DateUtils.getDaysDifference(
						getCurrentDateWithoutTime(prInquiry.getCreatedDate()),
						getCurrentDateWithoutTime(prInquiry.getDateToResponse())));

		getManagedBean().setStockAvailable(prInquiry.getIsStockAvailable());
		getManagedBean().setScPrInquiry(prInquiry);
		// price change lister 
		priceTypeChangeListener();

		// remarks
		prInquiry.setRemarks(initializeRemarks(
				prInquiry,
				prInquiry.getCurrentStatus(),
				prInquiry.getRemarks(),
				ScPRIRemark.class));

		// title checker
		getManagedBean().setTitleChecker(Boolean.FALSE);
		getManagedBean().setViewOnly(Boolean.TRUE);
		getManagedBean().setShowButton(Boolean.FALSE);
		getManagedBean().setShowBackButton(Boolean.TRUE);
		getManagedBean().setDisabledRemark(Boolean.TRUE);
		
		// #27868 - start
		Date dataCreatedDate;
		SmSupplier smSupplier = requestSupplierItem.getSmSupplier();
		checkProcTypeOption();
		dataCreatedDate = assignActualDate();
		getManagedBean().setCreatedDate(dataCreatedDate);
		String parameterType = getManagedBean().getRequestNoteDtl().getProcurementType().getParameterType();
		if (isGovtSellerBySupplierId(smSupplier.getSupplierId())) {
			getManagedBean().setProcurementTypes(getRadioButtonCodeValues(parameterType, Boolean.TRUE));
			inactiveReasonChecking();
			if (getManagedBean().isReasonCodeInActive()) {
				getManagedBean().setDisProcTyp(Boolean.TRUE);
			}
		} else {
			getManagedBean().setProcurementTypes(getRadioButtonCodeValues(parameterType, Boolean.FALSE));
			inactiveReasonChecking();
			if (getManagedBean().isReasonCodeInActive()) {
				getManagedBean().setDisProcTyp(Boolean.TRUE);
			}
		}
		getManagedBean().setDisProcTyp(Boolean.TRUE);
		getManagedBean().setParameterTypeRPT(parameterType);
		// #27868 - end
		
		// current page url for page navigation from pid to request note
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setNavToPreviousPage(fc.getViewRoot().getViewId());

		fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, PURCHASE_INQUIRY_PAGE);
	}

	
	/**
	 * Gets the request note so.
	 * 
	 * @return the request note so
	 */
	private IRequestNoteSO getRequestNoteSO() {
		return BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IRequestNoteSO.class);
	}
	
	private ICatalogueSO getICatalogueSO() {
		return BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, ICatalogueSO.class);
	}

	private IOrgProfileSO getIOrgProfileSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
	}
	
	/**
	 * Gets the pr inquiry so.
	 * 
	 * @return the pr inquiry so
	 */
	private IPrInquirySO getPrInquirySO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
	}

	/**
	 * Get parameter code select items by parameter type for radio buttons. if
	 * govt. seller , enable G2G radio button only. if not govt. seller enable
	 * normal, ad Hoc and Emergency.
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @param g2gSeller
	 *            the g2g seller
	 * @return array of SelectItem
	 */
	public SelectItem[] getRadioButtonCodeValues(String parameterType,
			boolean g2gSeller) {
		return getRadioButtonCodeValuesBySearchBean(parameterType, null,
				g2gSeller);
	}

	/**
	 * Gets the radio button code values by search bean.
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @param searchBean
	 *            the search bean
	 * @param g2gSeller
	 *            the g2g seller
	 * @return the radio button code values by search bean
	 */
	public SelectItem[] getRadioButtonCodeValuesBySearchBean(
			String parameterType, ISearchBean<PmParameterDesc> searchBean,
			boolean g2gSeller) {
		SelectItem[] items = null;
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescListBySearchBean(parameterType, searchBean);

		items = new SelectItem[parameterDescs.size()];
		int i = 0;
		for (PmParameterDesc parameterDesc : parameterDescs) {
			if (g2gSeller) {
				if (parameterDesc.getPmParameter().getParameterCode()
						.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
					items[i] = new SelectItem(parameterDesc.getPmParameter()
							.getParameterCode(), parameterDesc.getCodeName(),
							null, !g2gSeller);
				} else {
					items[i] = new SelectItem(parameterDesc.getPmParameter()
							.getParameterCode(), parameterDesc.getCodeName(),
							null, g2gSeller);
				}
			} else {

				if (parameterDesc.getPmParameter().getParameterCode()
						.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
					items[i] = new SelectItem(parameterDesc.getPmParameter()
							.getParameterCode(), parameterDesc.getCodeName(),
							null, !g2gSeller);
				} else {
					items[i] = new SelectItem(parameterDesc.getPmParameter()
							.getParameterCode(), parameterDesc.getCodeName(),
							null, g2gSeller);
				}
			}

			i++;
		}
		return items;
	}
	
	/**
	 * Out of stock message.
	 */
	private void outOfStockMessage(){
		FacesScopes face = new FacesScopes();
		FacesContext
		.getCurrentInstance()
		.addMessage(
				null,
				new FacesMessage(
						FacesMessage.SEVERITY_INFO,
						null,
						ResourceBundleUtils
						.getMessage(
								"lbl.sc.purchase.inquiry.out.of.stock",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								face.getUserLocale())));
	}
	
	/**
	 * Checks if is supplier user.
	 * 
	 * @return true, if is supplier user
	 */
	public boolean isSupplierUser() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_USER");
	}

	/**
	 * Checks if is requisitioner.
	 * 
	 * @return true, if is requisitioner
	 */
	public boolean isRequisitioner() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("REQUISITIONER");
	}

	/**
	 * @return the textBeforeSign
	 */
	public String getTextBeforeSign() {
		// #36466
		StringBuilder sb = new StringBuilder();
		if (getManagedBean().getScPrInquiry() != null) {
			sb.append(getManagedBean().getScPrInquiry().getPrInquiryNo());
		}
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		textBeforeSign = sb.toString();
		return textBeforeSign;
	}

	/**
	 * @param textBeforeSign the textBeforeSign to set
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}
	
	/**
	 * The method to used auto update of request.
	 */
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);
	}
	
	private void validateSupplierAddress(Long supplierId) {
		try {
			IRequestNoteSO requestNoteSO = (IRequestNoteSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION,
							IRequestNoteSO.class);
			RequestNoteReqDTO rnReqDTO = new RequestNoteReqDTO();
			rnReqDTO.setSupplierId(supplierId);
			requestNoteSO.validateSupplierAddressExist(rnReqDTO);
		} finally {
		}
	}
	
	/**
	 * Update work flow and remarks.
	 *
	 * @param requestNote the request note
	 */
	private void updateWorkFlowAndRemarks(ScRequestNote requestNote){
		// remarks and workflow status
		ScRNWorkFlowStatus scWorkFlowStatus = null;
		List<ScRNWorkFlowStatus> workFlowStatuses =  new  ArrayList<ScRNWorkFlowStatus>();
		scWorkFlowStatus = new ScRNWorkFlowStatus();
		PmStatus currentStatus = getPmStatusByStatusId(DPStatusConstants.RN_PENDING_SUBMISSION);
		getCurrentStatus(currentStatus);
		scWorkFlowStatus.setCurrent(Boolean.TRUE);
		scWorkFlowStatus.setPmStatus(currentStatus);
		scWorkFlowStatus.setRequestNote(requestNote);
		workFlowStatuses.add(scWorkFlowStatus);
		requestNote.setWorkFlowStatuses(workFlowStatuses);
		// To avoid workflow status reset to null when got error during direct submit without save.
		List<ScRequestNoteRemark> remarks = requestNote.getRemarks();
		if (CollectionUtils.isNotEmpty(remarks)) {
			for (ScRequestNoteRemark remark : remarks) {
				remark.setScWorkFlowStatus(scWorkFlowStatus);
			}
		}
	}

	// Start - #26129
	private void validateRequiredRemark(ScPrInquiry prInquiryDetail) {
		boolean newRemark = false;
		if (prInquiryDetail != null) {
			for (ScPRIRemark remark : prInquiryDetail.getRemarks()) {
				// no new remark being insert
				if (remark.getRemark() == null || remark.getRemark().isEmpty()) {
					newRemark = true;
				}
			}
			//#29236 CR 26129: Static Code Analysis Use isEmpty() to check whether the collection is empty or not
			if (prInquiryDetail.getRemarks().isEmpty()) {
				newRemark = true;
			}				
		}

		if (newRemark) {
			if ("en".equals(new FacesScopes().getUserLocale().getLanguage())) 
				throw new EpException(MessageConst.ERROR_100022, new String[] { "Remark" });
			else
				throw new EpException(MessageConst.ERROR_100022, new String[] { "Catatan" });
		}
	}
	// End - #26129
	
	// 27868 - start
	/**
	 * Generates a justification message based on the selected procurement type.
	 * <p>
	 * If the procurement type is Ad Hoc, an exception is thrown. For PK7 or
	 * Special Item types, a specific justification message is returned.
	 * </p>
	 * 
	 * @return the justification message, or an empty string if none applies
	 * @throws EpException
	 *             if the procurement type is Ad Hoc
	 */
	public String getJustificationRequiredMessage() {
		String parameterCode = getManagedBean().getRequestNoteDtl().getProcurementType().getParameterCode();
		Date dataCreatedDate;
		dataCreatedDate = assignActualDate();
		getManagedBean().setCreatedDate(dataCreatedDate);
		String parameterType = validateProcurementTypeisRPT(dataCreatedDate);
		String procurementSelected = getParameterDesc(parameterType, parameterCode);
		String[] msgParam = new String[] { procurementSelected };

		if (parameterCode.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
					"lbl.sc.price.inquiry.just.rmks", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()) });

		} else if (parameterCode.equalsIgnoreCase(ParameterConst.PROC_TYPE_PK7)
				|| parameterCode.equalsIgnoreCase(ParameterConst.PROC_TYPE_SPECIAL_ITEM)) {
			return MessageUtils.getMessage(MessageConst.ERROR_760707, msgParam, MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale());
		}
		return SSTConstant.EMPTY_STRING;
	}

	/**
	 * Generates a reason message based on the selected procurement type.
	 * <p>
	 * If the procurement type is Ad Hoc, an exception is thrown. For PK7 or
	 * Special Item types, a specific reason message is returned.
	 * </p>
	 * 
	 * @return the reason message, or an empty string if none applies
	 * @throws EpException
	 *             if the procurement type is Ad Hoc
	 */
	public String getReasonRequiredMessage() {
		String parameterCode = getManagedBean().getRequestNoteDtl().getProcurementType().getParameterCode();
		Date dataCreatedDate;
		dataCreatedDate = assignActualDate();
		getManagedBean().setCreatedDate(dataCreatedDate);
		String parameterType = validateProcurementTypeisRPT(dataCreatedDate);
		String procurementSelected = getParameterDesc(parameterType, parameterCode);
		String[] msgParam = new String[] { procurementSelected };

		if (parameterCode.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
					"lbl.sc.reason'", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
		} else if (parameterCode.equalsIgnoreCase(ParameterConst.PROC_TYPE_PK7)
				|| parameterCode.equalsIgnoreCase(ParameterConst.PROC_TYPE_SPECIAL_ITEM)) {
			return MessageUtils.getMessage(MessageConst.ERROR_760706, msgParam, MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale());
		}
		return SSTConstant.EMPTY_STRING;
	}

	/**
	 * Validates the reason status for the provided request note details.
	 * <p>
	 * If the reason code is inactive and there are existing request note
	 * details, an exception is thrown with the corresponding procurement type
	 * and reason description.
	 * </p>
	 * 
	 * @param requestNoteDtls
	 *            the list of request note details to validate
	 * @throws EpException
	 *             if the reason code is inactive for any request note detail
	 */
	public void validateReasonStatus(List<ScRequestNoteDtl> requestNoteDtls) {
		if (null != getManagedBean().getRequestNoteDtl().getJustificationType()) {
			PmReason reason = retrieveReasonDtlById(getManagedBean().getRequestNoteDtl().getJustificationType()
					.getReasonId());
			
			if (reason == null) {
				return;
			}
			boolean reasonStatus = retrievePK7ReasonStatus(reason.getReasonId());
			
			if (!reasonStatus && requestNoteDtls != null && !requestNoteDtls.isEmpty()) {
				for (ScRequestNoteDtl rnDtl : requestNoteDtls) {
					throw new EpException(MessageConst.ERROR_760716, new String[] {
							getParameterDesc(rnDtl.getProcurementType().getParameterType(), rnDtl.getProcurementType()
									.getParameterCode()), getReasonDesc(rnDtl.getJustificationType()) });
				}
			}
		}
	}

	/**
	 * Validates the user's profile agency for Special Item procurement types.
	 * <p>
	 * If the procurement type is Special Item and the user's agency profile
	 * does not match the required criteria, an exception is thrown.
	 * </p>
	 * 
	 * @param requestNoteDtls
	 *            list of {@link ScRequestNoteDtl} details to validate
	 * @throws EpException
	 *             if the user profile agency does not match for PK7 or Special
	 *             Item
	 */
	public void validateUserProfileAgencyForSpecialItem(List<ScRequestNoteDtl> requestNoteDtls) {
		if (requestNoteDtls != null && !requestNoteDtls.isEmpty()) {
			for (ScRequestNoteDtl rnDtl : requestNoteDtls) {
				if (rnDtl.getProcurementType().getParameterCode()
						.equalsIgnoreCase(ParameterConst.PROC_TYPE_SPECIAL_ITEM)
						&& !compareUserProfileAgencyForSpecialItem(rnDtl)) {
					throw new EpException(MessageConst.ERROR_760715, new String[] {
							getParameterDesc(rnDtl.getProcurementType().getParameterType(), rnDtl.getProcurementType()
									.getParameterCode()), getReasonDesc(rnDtl.getJustificationType()) });
				}
			}
		}
	}

	/**
	 * Validates the supplier account type for PK7 and Special Item procurement
	 * types.
	 * <p>
	 * For each request note detail with a procurement type of PK7 or Special
	 * Item, this method compares the supplier's account type against the
	 * restrictions defined in <code>pm_reason_restr</code>.
	 * </p>
	 * 
	 * @param requestNoteDtls
	 *            list of {@link ScRequestNoteDtl} details to validate
	 * @param supplierItem
	 *            the {@link ScRequestSupplierItem} containing supplier account
	 *            details
	 */
	public void validateSupplierAccountTypeForPK7AndSpecialItem(List<ScRequestNoteDtl> requestNoteDtls,
			ScRequestSupplierItem supplierItem) {
		if (requestNoteDtls != null && !requestNoteDtls.isEmpty()) {
			for (ScRequestNoteDtl rnDtl : requestNoteDtls) {
				if (rnDtl.getProcurementType().getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_PK7)
						|| rnDtl.getProcurementType().getParameterCode()
								.equalsIgnoreCase(ParameterConst.PROC_TYPE_SPECIAL_ITEM)) {
					// retrieve from pm_reason_restr and compare with suppler
					// acc type
					compareSuppAccTypePK7AndSpecialItem(rnDtl, supplierItem);
				}
			}
		}
	}

	/**
	 * Compares the supplier's account type with the allowed types for PK7 and
	 * Special Item procurement.
	 * <p>
	 * This method retrieves the restriction list from
	 * <code>pm_reason_restr</code> based on the justification type of the given
	 * request note detail, determines the supplier's account type, and verifies
	 * whether it matches the allowed account types. If it does not match, an
	 * appropriate error message is generated.
	 * </p>
	 * 
	 * @param rnDtl
	 *            the {@link ScRequestNoteDtl} containing procurement and
	 *            justification details
	 * @param supplierItem
	 *            the {@link ScRequestSupplierItem} containing supplier account
	 *            information
	 * @throws EpException
	 *             if the supplier account type does not match the restriction
	 *             rules
	 */
	public void compareSuppAccTypePK7AndSpecialItem(ScRequestNoteDtl rnDtl, ScRequestSupplierItem supplierItem) {
		boolean isSupplierAccTypeMatch = false;
		IReasonSO reqSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IReasonSO.class);
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setReasonId(rnDtl.getJustificationType().getReasonId());
		List<PmReasonRestr> listReasonRestr = reqSO.retrieveReasonRestrListByParam(reqDTO).getReasonRestrList();
		List<PmReasonRestr> distinctReasonRestrList = new ArrayList<>();
		distinctSupplierAccType(listReasonRestr, distinctReasonRestrList);

		StringBuilder suppAccTypeSb = new StringBuilder();
		String suppAccType = null;
		if (supplierItem != null && supplierItem.getSmSupplier().getSmAppl() != null) {
			suppAccType = identifySupplierAccTypeFromSmAppl(supplierItem.getSmSupplier().getSmAppl().getSupplierType());
			suppAccType = verifyMOFAccount(supplierItem, suppAccType);
		}

		if ((distinctReasonRestrList != null && !distinctReasonRestrList.isEmpty()) && suppAccType != null) {
			isSupplierAccTypeMatch = initializeReasonRestr(distinctReasonRestrList, isSupplierAccTypeMatch,
					suppAccTypeSb, suppAccType);

			if (!isSupplierAccTypeMatch) {
				getSupplierTypeErrorMessage(rnDtl, suppAccTypeSb.toString());
			}
		}
	}

	/**
	 * Builds a distinct list of {@link PmReasonRestr} based on supplier account
	 * type.
	 * <p>
	 * A record is considered duplicate if its {@code supplAcctTypeId} is null
	 * or has the same {@code parameterCode} (case-insensitive) as an existing
	 * entry.
	 * </p>
	 * 
	 * @param listReasonRestr
	 *            source list containing possible duplicates
	 * @param distinctReasonRestrList
	 *            target list to store distinct records
	 */
	protected void distinctSupplierAccType(List<PmReasonRestr> listReasonRestr,
			List<PmReasonRestr> distinctReasonRestrList) {
		for (PmReasonRestr rr : listReasonRestr) {
			if(rr.getSupplAcctTypeId() != null){
				PmParameter param = rr.getSupplAcctTypeId();
				boolean exists = false;
	
				for (PmReasonRestr dr : distinctReasonRestrList) {
					PmParameter existingParam = dr.getSupplAcctTypeId();
	
					if (param == null ? existingParam == null : param.getParameterCode().equalsIgnoreCase(
							existingParam != null ? existingParam.getParameterCode() : "")) {
						exists = true;
						break;
					}
				}
	
				if (!exists) {
					distinctReasonRestrList.add(rr);
				}
			}
		}
	}

	/**
	 * Validates supplier’s MOF account; returns basic account if missing,
	 * otherwise keeps the supplied type.
	 * 
	 * @param supplierItem
	 *            supplier data to check MOF account
	 * @param suppAccType
	 *            current supplier account type
	 * @return validated supplier account type
	 */
	protected String verifyMOFAccount(ScRequestSupplierItem supplierItem, String suppAccType) {
		if (suppAccType.equalsIgnoreCase(DPConstants.PARAMETER_CODE_MOF_ACCOUNT) && !StringUtils.isBlank(suppAccType)) {
			ISupplierDataMaintenanceSO supplierDataMaintenanceSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierDataMaintenanceSO.class);
			SupplierDataMaintenanceReqDTO suppReqDTO = new SupplierDataMaintenanceReqDTO();
			suppReqDTO.setSupplier(supplierItem.getSmSupplier());

			if (null == supplierDataMaintenanceSO.retrieveMofSupplierBySupplierId(suppReqDTO).getSmMofAccount()) {
				return DPConstants.PARAMETER_CODE_BASIC_ACCOUNT;
			}
		}
		return suppAccType;
	}

	/**
	 * Compares the current user's agency with the allowed agencies for a given
	 * Special Item or PK7 justification.
	 * <p>
	 * This method retrieves restriction data from <code>pm_reason_restr</code>
	 * based on the justification type in the provided {@link ScRequestNoteDtl},
	 * then compares the user's associated organization profile against the
	 * restricted agency list. Returns <code>true</code> if a match is found.
	 * </p>
	 * 
	 * @param rnDtl
	 *            the {@link ScRequestNoteDtl} containing justification type
	 *            information
	 * @return {@code true} if the user's agency matches one of the allowed
	 *         agencies, {@code false} otherwise
	 */
	public boolean compareUserProfileAgencyForSpecialItem(ScRequestNoteDtl rnDtl) {
		IReasonSO reqSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IReasonSO.class);
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setReasonId(rnDtl.getJustificationType().getReasonId());
		List<PmReasonRestr> listReasonRestr = reqSO.retrieveReasonRestrListByParam(reqDTO).getReasonRestrList();

		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IUserSO.class);

		UserOrgReqDTO userOrgReqDTO = new UserOrgReqDTO();
		userOrgReqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
		UserOrgResDTO userOrgResDTO = userSO.retrievePmUserOrgListByUserId(userOrgReqDTO);

		if (userOrgResDTO.getPmUserOrgList().isEmpty()) {
			return false;
		}

		for (PmUserOrg userOrg : userOrgResDTO.getPmUserOrgList()) {
			PmOrgProfile userAgency = userOrg.getPmOrgProfile();
			if (userAgency != null && isUserAgencyInRestrictionList(userAgency, listReasonRestr)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the given user agency exists in the list of restricted
	 * agencies.
	 * 
	 * @param userAgency
	 *            the user's organization profile
	 * @param listReasonRestr
	 *            the list of restricted reason-agency mappings
	 * @return {@code true} if the user's agency is found in the restriction
	 *         list; {@code false} otherwise
	 */
	private boolean isUserAgencyInRestrictionList(PmOrgProfile userAgency, List<PmReasonRestr> listReasonRestr) {
		if (userAgency == null) {
			return false;
		}

		for (PmReasonRestr restr : listReasonRestr) {
			if (restr.getAgencyId() != null
					&& restr.getAgencyId().getOrgProfileId().equals(userAgency.getOrgProfileId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Validates supplier account type against restrictions.
	 * 
	 * @param listReasonRestr
	 *            reason restrictions
	 * @param supplierAccTypeMatch
	 *            current match state
	 * @param suppAccTypeSb
	 *            supplier type message builder
	 * @param suppAccType
	 *            supplier account type
	 * @return true if matched, false otherwise
	 */
	public boolean initializeReasonRestr(List<PmReasonRestr> listReasonRestr, boolean supplierAccTypeMatch,
			StringBuilder suppAccTypeSb, String suppAccType) {

		Boolean isSupplierAccTypeMatch = supplierAccTypeMatch;

		for (PmReasonRestr reasonRestr : listReasonRestr) {
			if (!isSupplierAccTypeMatch) {
				if ((reasonRestr.getSupplAcctTypeId() != null && reasonRestr.getSupplAcctTypeId().getParameterCode()
						.equalsIgnoreCase(suppAccType))
						|| reasonRestr.getSupplAcctTypeId() == null) {
					isSupplierAccTypeMatch = true;
				} else if (reasonRestr.getSupplAcctTypeId() != null
						&& !reasonRestr.getSupplAcctTypeId().getParameterCode().equalsIgnoreCase(suppAccType)) {

					String desc = getParameterDesc(reasonRestr.getSupplAcctTypeId(), new FacesScopes().getUserLocale());

					appendUniqueDescription(suppAccTypeSb, desc);
				}

				getManagedBean().setSupplierAccountTypeErrorMessage(suppAccTypeSb.toString());
			}
		}

		return isSupplierAccTypeMatch;
	}

	/**
	 * Adds unique description to StringBuilder, separated by commas.
	 * 
	 * @param suppAccTypeSb
	 *            builder
	 * @param desc
	 *            text to add
	 */
	protected void appendUniqueDescription(StringBuilder suppAccTypeSb, String desc) {
		if (suppAccTypeSb.indexOf(desc) == -1) {
			if (suppAccTypeSb.length() > 0) {
				suppAccTypeSb.append(DPConstants.SEPARATOR_STRING);
			}
			suppAccTypeSb.append(desc);
		}
	}

	/**
	 * Throws an exception with a supplier type error message based on
	 * justification category and locale.
	 * 
	 * @param rnDtl
	 *            request note detail
	 * @param suppAccTypeSb
	 *            supplier account type descriptions
	 * @param dataCreatedDate
	 *            data creation date
	 * @param supplierItem
	 *            supplier item details
	 * @throws EpException
	 *             when supplier account type is invalid for the justification
	 *             type
	 */
	public void getSupplierTypeErrorMessage(ScRequestNoteDtl rnDtl, String suppAccTypeSb) {
		int appendedCount = 0;

		if (suppAccTypeSb.length() > 0) {
			appendedCount = suppAccTypeSb.split(",").length;
		}

		Date createdDate = rnDtl.getCreatedDate() != null ? rnDtl.getCreatedDate() : new Date();
		if (validateProcurementTypeisRPT(createdDate).equalsIgnoreCase(ParameterTypeConst.PROCUREMENT_TYPE)) {
			throwSupplierAccountTypeError(rnDtl, suppAccTypeSb, appendedCount);
		} else {
			throwJustificationTypeError(rnDtl, suppAccTypeSb, appendedCount);
		}
	}

	/**
	 * Throws localized error for supplier account type validation.
	 */
	protected void throwSupplierAccountTypeError(ScRequestNoteDtl rnDtl, String suppAccTypeSb, int appendedCount) {
		if (DPConstants.LANGUAGE_CODE_EN.equalsIgnoreCase(new FacesScopes().getUserLocale().getLanguage())) {
			String isOrAre;

			if (appendedCount > 1) {
				isOrAre = DPConstants.ARE_STRING;
			} else {
				isOrAre = DPConstants.IS_STRING;
			}
			throw new EpException(MessageConst.ERROR_760717, new String[] {
					getManagedBean().getScRequestSupplierItem().getSupplierName(),
					getParameterDesc(rnDtl.getProcurementType().getParameterType(), rnDtl.getProcurementType()
							.getParameterCode()), getReasonDesc(rnDtl.getJustificationType()), isOrAre, suppAccTypeSb });
		} else {
			// for MY
			throw new EpException(MessageConst.ERROR_760717, new String[] {
					getManagedBean().getScRequestSupplierItem().getSupplierName(),
					getParameterDesc(rnDtl.getProcurementType().getParameterType(), rnDtl.getProcurementType()
							.getParameterCode()), getReasonDesc(rnDtl.getJustificationType()), suppAccTypeSb });
		}
	}

	/**
	 * Throws localized error for invalid justification type.
	 */
	protected void throwJustificationTypeError(ScRequestNoteDtl rnDtl, String suppAccTypeSb, int appendedCount) {
		if (DPConstants.LANGUAGE_CODE_EN.equalsIgnoreCase(new FacesScopes().getUserLocale().getLanguage())) {
			String isOrAre;

			if (appendedCount > 1) {
				isOrAre = DPConstants.ARE_STRING;
			} else {
				isOrAre = DPConstants.IS_STRING;
			}
			throw new EpException(MessageConst.ERROR_760709, new String[] {
					getParameterDesc(rnDtl.getProcurementType().getParameterType(), rnDtl.getProcurementType()
							.getParameterCode()), getReasonDesc(rnDtl.getJustificationType()), isOrAre, suppAccTypeSb });
		} else {
			// for MY
			throw new EpException(MessageConst.ERROR_760709, new String[] {
					getParameterDesc(rnDtl.getProcurementType().getParameterType(), rnDtl.getProcurementType()
							.getParameterCode()), getReasonDesc(rnDtl.getJustificationType()), suppAccTypeSb });
		}
	}

	/**
	 * Checks whether any of the "as-is" justification reasons associated with
	 * the current request note detail is present in the justification dropdown.
	 * 
	 * @return {@code true} if at least one dropdown item value matches a reason
	 *         code from the request note detail; {@code false} otherwise
	 */
	private boolean isAsIsReasonIncludedInDropdown() {
		SelectItem[] dropdownItems = getManagedBean().getJustDropdown();

		if (dropdownItems == null || getManagedBean().getRequestNoteDtl() == null
				|| getManagedBean().getRequestNoteDtl().getJustificationType() == null
				|| getManagedBean().getRequestNoteDtl().getJustificationType().getReasonCode() == null) {
			return false;
		}

		List<?> reasonCodes = Arrays
				.asList(getManagedBean().getRequestNoteDtl().getJustificationType().getReasonCode());

		for (SelectItem item : dropdownItems) {
			Object value = item.getValue();
			if (value != null && reasonCodes.contains(value)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Updates the managed bean to mark the PK7 code as not new and the reason
	 * code as inactive. Also sets the inactive reason description if available.
	 */
	protected void updateInactiveReasonStatus() {
		PmReason reason = retrieveReasonDtlById(getManagedBean().getRequestNoteDtl().getJustificationType()
				.getReasonId());

		if (reason != null) {
			getManagedBean().setNewPK7Code(Boolean.FALSE);
			getManagedBean().setReasonCodeInActive(true);
			String reasonDesc = getReasonDesc(reason);
			if (!StringUtils.isEmpty(reasonDesc)) {
				getManagedBean().setInActiveReasonDesc(reasonDesc);
			}
		}
	}

	/**
	 * Checks if the selected justification reason is inactive.
	 * <p>
	 * If inactive, updates managed bean flags and sets the inactive reason
	 * description.
	 * </p>
	 * 
	 * @throws NullPointerException
	 *             if justification type or reason ID is null
	 */
	private void inactiveReasonChecking() {
		if (getManagedBean().getRequestNoteDtl() == null
				|| getManagedBean().getRequestNoteDtl().getJustificationType() == null
				|| getManagedBean().getRequestNoteDtl().getJustificationType().getReasonId() == null) {
			return;
		}

		PmReason reason = retrieveReasonDtlById(getManagedBean().getRequestNoteDtl().getJustificationType()
				.getReasonId());

		if (reason == null) {
			return;
		}

		boolean reasonStatus = retrievePK7ReasonStatus(reason.getReasonId());
		boolean isInactive = !reasonStatus;
		boolean isActiveButMissingAsIs = reasonStatus && !isAsIsReasonIncludedInDropdown();

		if (isInactive || isActiveButMissingAsIs) {
			getManagedBean().setReasonCodeInActive(true);

			String reasonDesc = getReasonDesc(reason);
			if (StringUtils.isNotEmpty(reasonDesc)) {
				getManagedBean().setInActiveReasonDesc(reasonDesc);
			}
		}
	}
	
	/**
	 * Retrieves the PK7 reason status by reason ID.
	 *
	 * @param reasonId
	 *            the reason ID
	 * @return true if the reason is active, otherwise false
	 */
	private boolean retrievePK7ReasonStatus(Long reasonId) {
		IReasonSO reqSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);
		return reqSO.getPK7ReasonStatus(reasonId);
	}

	/**
	 * Retrieves the division-level organization profile for the current user.
	 * 
	 * @return the first {@link PmOrgProfile} for the user, or {@code null} if
	 *         none found
	 */
	public PmOrgProfile retrievePmOrgProfile() {
		PmUser user = new FacesScopes().getUserSessionBean();
		if (user != null) {
			OrgProfileReqDTO profileReqDTO = new OrgProfileReqDTO();
			profileReqDTO.setUserId(user.getUserId());
			profileReqDTO.setOrgType(OrganizationType.DIVISION.value());
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveOrgProfileListByOrgType(profileReqDTO);
			if (!orgProfileResDTO.getOrgProfileList().isEmpty()) {
				return orgProfileResDTO.getOrgProfileList().get(0);
			}
		}
		return null;
	}

	/**
	 * Checks if the current user's agency is allowed for special items.
	 * 
	 * @return true if at least one active reason restriction exists for the
	 *         user's agency, false otherwise
	 */
	public boolean isAgencyAllowedForSpecialItem() {
		PmOrgProfile pmOrgProfile = retrievePmOrgProfile();

		return pmOrgProfile != null ? getReasonCodeDropdownValues(pmOrgProfile.getOrgProfileId(),
				ReasonCategory.JUSTIFICATION_SIT.value()).length > 1
				: getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_SIT.value()).length > 1;
	}

	/**
	 * Removes "Special Item" from the procurement types, effectively disabling
	 * its radio button.
	 */
	public void disableSpecialItemRadioButton() {
		SelectItem[] procurementTypesArray = getManagedBean().getProcurementTypes();

		if (procurementTypesArray != null && procurementTypesArray.length > 0) {
			List<SelectItem> list = new ArrayList<>(Arrays.asList(procurementTypesArray));

			for (Iterator<SelectItem> iterator = list.iterator(); iterator.hasNext();) {
				SelectItem item = iterator.next();
				if (ParameterConst.PROC_TYPE_SPECIAL_ITEM.equals(item.getValue())) {
					iterator.remove();
				}
			}
			SelectItem[] filteredArray = new SelectItem[list.size()];
			list.toArray(filteredArray);

			getManagedBean().setProcurementTypes(filteredArray);
		}

	}

	/**
	 * Retrieves a {@link PmReason} detail by its ID.
	 * 
	 * @param reasonId
	 *            the ID of the reason
	 * @return the {@link PmReason} corresponding to the given ID
	 */
	private PmReason retrieveReasonDtlById(Long reasonId) {
		IReasonSO reqSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IReasonSO.class);
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setReasonId(reasonId);

		return reqSO.retrieveReasonDtlById(reqDTO).getPmReason();
	}

	/**
	 * Returns the appropriate PK7 reason category code based on whether it's a
	 * new PK7 code.
	 * 
	 * @return PK7 reason category code as {@link String}
	 */
	private String finalizePK7Code() {
		if (getManagedBean().isNewPK7Code()) {
			return ReasonCategory.JUSTIFICATION_PK7W.value();
		}

		return ReasonCategory.JUSTIFICATION_PK7.value();
	}

	/**
	 * Determines actual date using quote’s created date; defaults to current
	 * date.
	 * 
	 * @return resolved actual date
	 */
	protected Date assignActualDate() {
		Date dataCreatedDate;
		if (getManagedBean().getScPrInquiry() != null && getManagedBean().getScPrInquiry().getCreatedDate() != null) {
			dataCreatedDate = getManagedBean().getScPrInquiry().getCreatedDate();
		} else {
			dataCreatedDate = new Date();
		}
		return dataCreatedDate;
	}

	// 27868 - end
	
	// #25790 - start
	/**
	 * Retrieve purchase inquiry.
	 * 
	 * @param purchaseInqId
	 *            the purchase Inquiry Id
	 * @return ScPrInquiry
	 */
	public ScPrInquiry retrievePurchaseInquiry(long purchaseInqId) {
		IPrInquirySO prInquirySO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IPrInquirySO.class);
		ScPrInquiry prInquiry;
		// Purchase Inquiry
		PrInquiryReqDTO prInquiryReqDTO = new PrInquiryReqDTO();
		prInquiryReqDTO.setPrInquiryId(purchaseInqId);
		prInquiryReqDTO.setFetchSubEntity(true);
		PrInquiryResDTO prInquiryResDTO = prInquirySO.getPrInquiry(prInquiryReqDTO);
		prInquiry = prInquiryResDTO.getPrInquiryDetail();
		getManagedBean().setTrackingDairyView(true);
		return prInquiry;
	}
	// #25790 - end
	
	// #26059 - Start
	/**
	 * Cleans the item name of the given request item, if available.
	 * 
	 * @param scRequestItem
	 *            the request item containing the item to clean
	 */
	private void filterItemName(ScRequestItem scRequestItem) {
		if (scRequestItem != null && scRequestItem.getCmItem() != null) {
			new CatalogueBackingBean().cleanItemNameNA(scRequestItem
					.getCmItem());
		}
	}
	// #26059 - End
}