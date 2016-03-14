/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.purchaseinquiry;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.sc.directpurchase.IPrInquirySO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.directpurchase.PrInquiryReqDTO;
import my.ep.dto.sc.directpurchase.PrInquiryResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScPIZone;
import my.ep.persistence.entity.sc.directpurchase.ScPRIRemark;
import my.ep.persistence.entity.sc.directpurchase.ScPrInquiry;
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
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.sc.requestnote.RequestNoteBackingBean;
import my.ep.web.managedbean.sc.purchaseinquiry.PurchaseInquiryManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.SourcingZoneHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;

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
			PortletRequest request = (PortletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			User user = (User) request.getAttribute(WebKeys.USER);
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
					&& cmSupplierItem.getCmPricings().size() > 0) {
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
			requestSupplierItem.setSmSupplier(smSupplier);
			
			requestSupplierItem.setSupplierName(smSupplier.getCompanyName());
			requestSupplierItem.setMofNumber(getMofNor(smSupplier.getSupplierId()));
			
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
		if (ruleValueList != null && ruleValueList.size() > 0) {
			PmConfigRuleValue configRuleValue = ruleValueList.get(0);
			scPrInquiry.setDayToResponse(Integer.valueOf(configRuleValue.getRuleValue()));
		}
		scPrInquiry.setDateToResponse(calcDayToResponse(scPrInquiry));

		// check supplier is government seller or not
		ScRequestNoteDtl requestNoteDtl = getManagedBean().getRequestNoteDtl();
		PmParameter pmParameter = new PmParameter();
		requestNoteDtl.setJustificationType(new PmReason());

		// check supplier is government seller or not
		if (isGovtSellerOrNot(cmSupplierItem)) {
			pmParameter.setParameterCode(ParameterConst.PROC_TYPE_G2G);
			requestNoteDtl.setProcurementType(pmParameter);
			getManagedBean().setProcurementTypes(
					getRadioButtonCodeValues(
							ParameterTypeConst.PROCUREMENT_TYPE, Boolean.TRUE));

		} else {
			pmParameter.setParameterCode(ParameterConst.PROC_TYPE_NORMAL);
			requestNoteDtl.setProcurementType(pmParameter);
			getManagedBean()
					.setProcurementTypes(
							getRadioButtonCodeValues(
									ParameterTypeConst.PROCUREMENT_TYPE,
									Boolean.FALSE));
		}

		List<CmPricing> cmPricings = new ArrayList<CmPricing>();
		if (cmSupplierItem.getCmPricings() != null
				&& cmSupplierItem.getCmPricings().size() > 0) {
			List<String> priceTypes = new ArrayList<String>();
			for (CmPricing princing : cmSupplierItem.getCmPricings()) {
				if (!priceTypes.contains(princing.getPricingType())) {
					priceTypes.add(princing.getPricingType());
				}
			}
			if (priceTypes != null && priceTypes.size() > 0) {
				for (CmPricing princing : cmSupplierItem.getCmPricings()) {
					cmPricings.add(princing);
				}
				requestSupplierItem
						.setSupplierItemPrices(createScSupplierItemPrices(
								requestSupplierItem, cmPricings, Boolean.FALSE));
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

		// prepare requestSupplierItems , request item and supplier item price
		// IViewSupplierItemSO viewSupplierItemSO =
		// BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT,
		// IViewSupplierItemSO.class);
		// CatalogueReqDTO catalogueReqDTO = new CatalogueReqDTO();
		// ISearchBean<VCmSupplierItem> searchBean = new
		// CatalogueBackingBean().getManagedBean().getSearchDataModel().getSearchBean();
		// searchBean.getDataFilterBeans().clear();
		// catalogueReqDTO.setViewSearchBean(searchBean);
		// catalogueReqDTO.setSupplierItemId(cmSupplierItem.getSupplierItemId());
		// catalogueReqDTO.setExtensionCode(cmSupplierItem.getCmItem().getExtensionCode());
		// catalogueReqDTO.setUomId(cmSupplierItem.getPmUom().getUomId());
		// CatalogueResDTO catalogueResDTO =
		// viewSupplierItemSO.getSuppliersItemPricingByExtensionCode(catalogueReqDTO);
		//
		// List<ScRequestSupplierItem> supplierItems = new
		// ArrayList<ScRequestSupplierItem>();
		// for (CmSupplierItem supplierItem :
		// catalogueResDTO.getSupplierItemList()) {
		// if (supplierItem.getItemPricings() != null &&
		// supplierItem.getItemPricings().size() > 0) {
		//
		// if(cmSupplieritemIds.contains(supplierItem.getSupplierItemId())){
		//
		// if(supplierItem.getCmItem().getItemId().equals(cmSupplierItem.getCmItem().getItemId())){
		//
		// ScRequestSupplierItem scRequestSupplierItem =
		// createScRequestSupplierItem(
		// requestItem, supplierItem.getSmSupplier(),
		// supplierItem.getItemPricings());
		// supplierItems.add(scRequestSupplierItem);
		// }
		// }
		// }
		// }

		// Add selected supplier item.
		requestSupplierItem.setCmSupplierItem(cmSupplierItem);
		supplierItems.add(requestSupplierItem);
		requestSupplierItem.setScPrInquiy(scPrInquiry);
		requestItem.setSupplierItems(supplierItems);
		scPrInquiry.setRequestItem(requestItem);

		requestSupplierItem.setRequestNoteItem(requestItem);
		getManagedBean().setScRequestSupplierItem(requestSupplierItem);

		requestNoteDtl.setRequestItem(requestItem);
		requestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
		getManagedBean().setRequestNoteDtl(requestNoteDtl);

		// pr inquiry status
		IStatusSO statusSO = null;
		try {

			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

			StatusReqDTO statusReqDTO = new StatusReqDTO();
			statusReqDTO.setStatusId(DPStatusConstants.PRI_PENDING_SUBMISSION);

			StatusResDTO statusResDTO = statusSO
					.retrieveStatusByStatusId(statusReqDTO);

			getCurrentStatus(statusResDTO.getPmStatus());

		} finally {
			statusSO = null;
		}
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

		if (resDTO.getPmOrgProfile() != null) {
			if (resDTO.getPmOrgProfile().isGovtSeller()) {
				isg2g = Boolean.TRUE;
			} 
		}
		return isg2g;
	}

	/**
	 * Creates the sc request supplier item.
	 * 
	 * @param requestItem
	 *            the request item
	 * @param smSupplier
	 *            the sm supplier
	 * @param cmPricings
	 *            the cm pricings
	 * @return the sc request supplier item
	 */
	/*
	 * private ScRequestSupplierItem createScRequestSupplierItem(ScRequestItem
	 * requestItem, SmSupplier smSupplier, List<CmPricing> cmPricings) {
	 * ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
	 * 
	 * requestSupplierItem.setRequestNoteItem(requestItem);
	 * requestSupplierItem.setSmSupplier(smSupplier);
	 * 
	 * requestSupplierItem.setSupplierItemPrices(createScSupplierItemPrices(
	 * requestSupplierItem, cmPricings ,Boolean.TRUE));
	 * 
	 * return requestSupplierItem; }
	 */

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
		String ItemCode = cmItem.getExtensionCode();
		CharSequence charSeq = ItemCode.subSequence(0, 2);
		Integer itemCode = Integer.valueOf(charSeq.toString());

		// checking first two digits in itemCode whether service or product
		if (itemCode >= 70 && itemCode <= 94) { // for service between 70 and 94
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
		} finally {
		}
		return items;
	}

	/**
	 * priceTypeChangeListener for getting Item Pricing list in Purchase
	 * inquiry.
	 */
	public void priceTypeChangeListener() {
		debugLogger("priceTypeChangeListener");
		Map<ScSupplierItemPrice, ScSupplierItemPrice> itemPricesMap = new HashMap<ScSupplierItemPrice, ScSupplierItemPrice>();
		List<ScSupplierItemPrice> itemPriceList = new ArrayList<ScSupplierItemPrice>();
		if (getManagedBean().getScRequestSupplierItem() != null
				&& getManagedBean().getScRequestSupplierItem()
						.getSupplierItemPrices() != null
				&& getManagedBean().getScRequestSupplierItem()
						.getSupplierItemPrices().size() > 0) {
			ScRequestSupplierItem scRequestSupplierItem = new ScRequestSupplierItem();
			List<ScSupplierItemPrice> supplierItemPrices = getManagedBean()
					.getScRequestSupplierItem().getSupplierItemPrices();
			if (supplierItemPrices != null && supplierItemPrices.size() > 0) {
				for (ScSupplierItemPrice scSupplierItemPrice : supplierItemPrices) {

					if (scSupplierItemPrice.getPriceType() != null) {

						if (scSupplierItemPrice.getPriceType()
								.getParameterCode()
								.equals(getManagedBean().getPriceType())) {

							if (DPStatusConstants.PRI_PENDING_SUPPLIER_RESPONSE
									.equals(getManagedBean().getPriStatus()
											.getStatusId())) {
								scSupplierItemPrice.setItemPrice(null);
								itemPricesMap.put(scSupplierItemPrice,
										scSupplierItemPrice);
							} else {
								itemPricesMap.put(scSupplierItemPrice,
										scSupplierItemPrice);
							}
						}
					}
				}

				itemPriceList = new ArrayList<ScSupplierItemPrice>(
						itemPricesMap.keySet());
			}
			scRequestSupplierItem.setSupplierItemPrices(itemPriceList);
			getManagedBean().getScPrInquiry().setRequestSupplierItem(
					scRequestSupplierItem);
		}
	}

	/**
	 * submit purchase inquiry details. submit - save data into database and BPM
	 * process will route to the next respective user. page navigate to
	 * acknowledge page.
	 * 
	 * @return the string
	 */
	public String submitPrInquiryDtl() {
		debugLogger("submitPrInquiryDtl");
		try {
			PrInquiryReqDTO reqDTO = new PrInquiryReqDTO();
			FacesScopes facesScopes = new FacesScopes();
			ScRequestSupplierItem scRequestSupplierItem = getManagedBean()
					.getScRequestSupplierItem();
			ScPrInquiry prInquiry = getManagedBean().getScPrInquiry();

			List<ScRequestNoteDtl> requestNoteDtls = new ArrayList<ScRequestNoteDtl>();
			ScRequestNoteDtl requestNoteDtl = getManagedBean()
					.getRequestNoteDtl();
			ScRequestNote requestNote = new ScRequestNote();
			requestNote.setTitle(prInquiry.getTitle());
			requestNote.setRequestNoteNo(getDirectPurchaseSO()
					.generateRequestNoteNo());
			requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SC);
			setUserDetails(requestNote);
			requestNoteDtl.setScRequestNote(requestNote);
			requestNoteDtl.setAssignedUser(facesScopes.getUserSessionBean());

			// selected supplier item
			requestNoteDtl.setScRequestSupplierItem(scRequestSupplierItem);

			// proc type
			requestNoteDtl.setProcurementType(getParameterByTypeAndCode(
					ParameterTypeConst.PROCUREMENT_TYPE, requestNoteDtl
							.getProcurementType().getParameterCode()));

			// justification type
			if (requestNoteDtl.getProcurementType().getParameterCode()
					.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
				if (requestNoteDtl.getJustificationType().getReasonCode() != null) {
					requestNoteDtl
							.setJustificationType(getReasonByCategoryAndCode(
									ReasonCategory.JUSTIFICATION_AD_HOC.value(),
									requestNoteDtl.getJustificationType()
											.getReasonCode()));
				}
				
			} else {
				requestNoteDtl.setJustificationType(null);
			}
			
			// title
			requestNoteDtl.setProcurementTitle(prInquiry.getTitle());

			// price type
			if (getManagedBean().getPriceType() != null) {
				requestNoteDtl.setPriceType(getParameterByTypeAndCode(
						ParameterTypeConst.PRICE_TYPE, getManagedBean()
								.getPriceType()));
			}

			requestNoteDtls.add(requestNoteDtl);

			List<ScRequestSupplierItem> requestSupplierItems = new ArrayList<ScRequestSupplierItem>();
			// validate order quantity
			if (scRequestSupplierItem.getRequestNoteItem().getQuantity() != null
					&& scRequestSupplierItem.getRequestNoteItem().getQuantity()
							.intValue() < 0) {
				throw new EpException(MessageConst.ERROR_100033, new String[] {
						ResourceBundleUtils.getMessage("lbl.sc.order.qty",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale())});
			}
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
			reqDTO.setOrgProfileId(getManagedBean().getPmOrgProfile()
					.getOrgProfileId());
			reqDTO.setRequestNote(requestNote);
			PrInquiryResDTO prInquiryResDTO = getPrInquirySO().submitPrInquiry(
					reqDTO);
			if (prInquiryResDTO.isSuccessful()) {
				ScPrInquiry scPrInquiry = prInquiryResDTO.getPrInquiryDetail();
				successMessage(MessageConst.INFO_700026,
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_PURCHASE_INQUIRY,
						scPrInquiry.getPrInquiryNo(), Boolean.TRUE);
				// clear data
				super.renewSesionObject();

			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
		} finally {
			// directPurchaseSO = null;
		}
		return null;
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
			}
			
			byte[] signedData = getManagedBean().getSpkiBackingBean()
					.spkiSignTransaction(originalText);
			pmDigiSign.setSpkiSignedData(signedData);
			RequestContext.getCurrentInstance().execute("spki_dlg.hide();");
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

	/**
	 * Prepare original text.
	 * 
	 * @param smSupplier
	 *            the sm supplier
	 * @return the string
	 *//*
	private String prepareOriginalText(SmSupplier smSupplier) {
		FacesScopes facesScopes = new FacesScopes();
		StringBuilder sb = new StringBuilder();
		String countryCode = "";
		String orginalText = "";

		if (smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses() != null
				&& smSupplier.getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses().size() > 0) {
			List<SmCompanyAddress> companyAddress = smSupplier.getSmAppl()
					.getSmCompanyBasic().getSmCompanyAddresses();

			for (SmCompanyAddress compaAddress : companyAddress) {
				if (compaAddress.getAddressType().equalsIgnoreCase(
						AddressType.CONTACT_ADDRESS.value())) {
					countryCode = compaAddress.getSmAddress().getPmCountry()
							.getCountryCode();
					break;
				}
			}
		}
		sb.append(getManagedBean().getScPrInquiry().getPrInquiryNo())
				.append("|")
				.append(facesScopes.getUserSessionBean().getIdentificationNo())
				.append("|").append(smSupplier.getRegNo()).append("|")
				.append(countryCode);
		orginalText = sb.toString();
		return orginalText;
	}*/
	
	/**
	 * Prepare original text to pass spki authentication
	 * 
	 * @return the string
	 */
	private String prepareOriginalText(boolean isG2GSupplier) {
		/*
		 * The first one is the Government user, it is the combination of IC.No,
		 * > Organization Code and Country Code. >
		 * 0000000081102813580400000000000211602001MY > IC No. , Organization
		 * code, Country code > > The second one is for Supplier, it is the
		 * combination of IC. No, Reg No and > Country Code. >
		 * 00000000680427115037000000000KT0143162-VMY > IC No. , Registration
		 * No., Country code
		 */
		FacesScopes facesScopes = new FacesScopes();
		StringBuilder sb = new StringBuilder();
		SmSupplier smSupplier = null;
		String countryCode = "";
		String orginalText = "";

		if (getManagedBean().getScRequestSupplierItem() != null) {
			ScRequestSupplierItem scRequestSupplierItem = getManagedBean()
					.getScRequestSupplierItem();
			smSupplier = scRequestSupplierItem.getSmSupplier();

			if (smSupplier.getSmAppl().getSmCompanyBasic()
					.getSmCompanyAddresses() != null
					&& smSupplier.getSmAppl().getSmCompanyBasic()
							.getSmCompanyAddresses().size() > 0) {
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
			sb.append(smSupplier.getRegNo());
		}
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
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");

		}  else {
			// spki
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
	public void procurementTypeEvent() {
		log.info("procurementTypeEvent");
		if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.getParameterCode()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL)
				|| getManagedBean().getRequestNoteDtl().getProcurementType()
						.getParameterCode()
						.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			getManagedBean().setProcTypeOption(Boolean.FALSE);
			getManagedBean().setJustTypeOption(Boolean.FALSE);

		} else if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.getParameterCode()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
			getManagedBean()
					.setJustDropdown(
							getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_AD_HOC.value()));

		} 
		getManagedBean().getRequestNoteDtl().setJustificationType(
				new PmReason());
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
			RequestNoteResDTO resDTO = getDirectPurchaseSO().getRequestNote(
					requestDTO);
			ScRequestNote requestNote = resDTO.getRequestNoteDetail();
			reqNoteBackingBean.getManagedBean().setRequestNote(requestNote);

			if (requestNote.getRequestNoteDtls() != null
					&& requestNote.getRequestNoteDtls().size() > 0) {
				List<ScRequestNoteDtl> requestNoteDtls = new ArrayList<ScRequestNoteDtl>();

				for (ScRequestNoteDtl scRequestNoteDtl : requestNote.getRequestNoteDtls()) {
					ScRequestSupplierItem scRequestSupplierItem =  scRequestNoteDtl.getScRequestSupplierItem();
					// validate pi if no stock
					if (prInquiry.getIsStockAvailable().equals(
							ParameterConst.PI_NO_STOCK)
							&& scRequestNoteDtl
							.getProcurementType()
							.getParameterCode()
							.equalsIgnoreCase(
									ParameterConst.PROC_TYPE_NORMAL)) {
						PrInquiryReqDTO inquiryReqDTO = new PrInquiryReqDTO();
						inquiryReqDTO.setRequestItemId(scRequestNoteDtl
								.getRequestItem().getRequestNoteItemId());

						PrInquiryResDTO inquiryResDTO = getPrInquirySO()
								.isPIProceedabletoRN(inquiryReqDTO);
						if (!inquiryResDTO.isProceedable()) {
							throw new EpException(MessageConst.ERROR_760358);
						}
					}

					scRequestNoteDtl.setFulfilmentType(getParameterByTypeAndCode(
							ParameterTypeConst.FULFILMENT_TYPE,
							ParameterConst.FULFILMENT_TYPE_ONE_OFF));

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

					scRequestNoteDtl.getScRequestSupplierItem().setSupplierId(
							scRequestNoteDtl.getScRequestSupplierItem()
							.getSmSupplier().getSupplierId());

					if (scRequestNoteDtl.getJustificationType() != null) {
						reqNoteBackingBean.getManagedBean().setJustificationType(
								scRequestNoteDtl.getProcurementType()
								.getParameterCode());
					}

					if (scRequestNoteDtl.getJustification() != null) {
						reqNoteBackingBean.getManagedBean().setJustification(
								scRequestNoteDtl.getJustification());
					}

					// set condition field for saved request note
					if (!scRequestNoteDtl.isReRecommend()) {
						StringBuilder sb = new StringBuilder();
						sb.append("requestNoteItem.requestNoteItemId^");
						sb.append(scRequestNoteDtl.getRequestItem()
								.getRequestNoteItemId());

						if (prInquiry.getIsStockAvailable().equals(
								ParameterConst.PI_NO_STOCK) && scRequestNoteDtl.isShowPurchaseInquiry()) {
							sb.append("|");
							sb.append("requestSupplierItemId^");
							sb.append(scRequestNoteDtl.getPiRequestSupplierItem()
									.getRequestSupplierItemId());

							sb.append("^");
							sb.append(SearchOperator.OPERATOR_NOT_IN.value());
						}
						scRequestNoteDtl.setConditionField(sb.toString());
					}

					reqNoteBackingBean.getManagedBean().setProcurementType(
							scRequestNoteDtl.getProcurementType()
							.getParameterCode());
					reqNoteBackingBean.getManagedBean().setRequestSupplierItem(
							scRequestNoteDtl.getScRequestSupplierItem());
					reqNoteBackingBean.supplierListener(scRequestNoteDtl
							.getScRequestSupplierItem().getSupplierId(),
							scRequestNoteDtl.getPriceType().getParameterId(),
							scRequestNoteDtl, scRequestNoteDtl.getAssignedUser()
							.getUserId(), scRequestNoteDtl
							.getProcurementTypeCategory().getParameterId());

					// update price
					reqNoteBackingBean.priceTypeChangeListener(scRequestNoteDtl
							.getPriceType().getParameterId(), scRequestNoteDtl);

					// fulfilmentTypeEvent
					reqNoteBackingBean.getManagedBean().setRequestNote(requestNote);
					reqNoteBackingBean.fulfilmentTypeEvent(scRequestNoteDtl);
					reqNoteBackingBean.getManagedBean().setRequestNoteDtl(
							scRequestNoteDtl);

					if (prInquiry.getIsStockAvailable().equals(
							ParameterConst.PI_NO_STOCK)
							&& null == requestNote.getCurrentStatus()) {
						scRequestNoteDtl.getScRequestSupplierItem().setSmSupplier(
								new SmSupplier());

					}
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
//					reqNoteBackingBean.initialInformation(requestNote);

					IStatusSO statusSO = null;
					try {
						statusSO = BusinessDelegate.getInstance()
								.getServiceObject(
										ModuleCodeConst.PROFILE_MANAGEMENT,
										IStatusSO.class);

						StatusReqDTO statusReqDTO = new StatusReqDTO();
						statusReqDTO
						.setStatusId(DPStatusConstants.RN_PENDING_SUBMISSION);

						StatusResDTO statusResDTO = statusSO
								.retrieveStatusByStatusId(statusReqDTO);

						reqNoteBackingBean.getCurrentStatus(statusResDTO
								.getPmStatus());

					} finally {
						statusSO = null;
					}
					requestNote.setRemarks(initializeRemarks(requestNote,
							requestNote.getCurrentStatus(),
							requestNote.getRemarks(), ScRequestNoteRemark.class));

					reqNoteBackingBean.getManagedBean().setFulfillmentType(
							ParameterConst.FULFILMENT_TYPE_ONE_OFF);
					reqNoteBackingBean.getManagedBean().setItemSearchBackBtn(Boolean.TRUE);
				}
			}
			// redirect to RequestNote screen
			return "requestNoteMain";

		} else {
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
		PmParameter deliveryTermType = getParameterByTypeAndCode(
				ParameterTypeConst.DELIVERY_TERM,
				ParameterConst.SQ_DURATION_DAY);
		return deliveryTermType;
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

		if (DPStatusConstants.PRI_PENDING_SUPPLIER_RESPONSE.equals(currentStatusId)) {
			prInquiryReqDTO.setFetchSupplierEntity(true);
		}
		PrInquiryResDTO prInquiryResDTO = getPrInquirySO().getPrInquiry(
				prInquiryReqDTO);
		ScPrInquiry prInquiry = prInquiryResDTO.getPrInquiryDetail();

		boolean isPageNavFlag = Boolean.TRUE;
		if (prInquiry.getScRequestSupplierItems() != null
				&& prInquiry.getScRequestSupplierItems().size() > 0) {
			// get selected supplier
			ScRequestSupplierItem requestSupplierItem = prInquiry
					.getScRequestSupplierItems().get(0);

			if(requestSupplierItem.getScRequestNoteDtl() != null && 
					   requestSupplierItem.getScRequestNoteDtl().size()>0){
				 requestNoteDtl = requestSupplierItem
						.getScRequestNoteDtl().get(0);

				// lead time
				if (requestSupplierItem.getSupplierItemPrices() != null
						&& requestSupplierItem.getSupplierItemPrices().size() > 0) {
					requestSupplierItem.getRequestNoteItem().setLeadTime(
							requestSupplierItem.getSupplierItemPrices().get(0)
							.getPricing().getCmSupplierItem()
							.getLeadTime());
				}

				// price type drop down
				List<String> priceTypes = new ArrayList<String>();
				priceTypes
				.add(requestNoteDtl.getPriceType().getParameterCode());
				getManagedBean().setPriceType(
						requestNoteDtl.getPriceType().getParameterCode());
				getManagedBean().setPriceTypes(
						getPriceTypeDropdownValues(
								ParameterTypeConst.PRICE_TYPE, priceTypes));

				getManagedBean().setPmOrgProfile(
						requestNoteDtl.getScRequestNote().getPmOrgProfile());
				getManagedBean().setRequestNoteDtl(requestNoteDtl);
				getManagedBean().setScRequestSupplierItem(requestSupplierItem);

				// RequestNote Details - checking workflow status exits or not
				// for PI to RN
				requestNoteDtl.getRequestItem().setSupplierItems(
						prInquiry.getScRequestSupplierItems());
				if (requestNoteDtl.getScRequestNote() != null) {
					ScRequestNote requestNote = requestNoteDtl
							.getScRequestNote();
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

		IStatusSO statusSO = null;
		try {
			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

			StatusReqDTO statusReqDTO = new StatusReqDTO();
			statusReqDTO.setStatusId(currentStatusId);

			StatusResDTO statusResDTO = statusSO
					.retrieveStatusByStatusId(statusReqDTO);

			getCurrentStatus(statusResDTO.getPmStatus());

		} finally {
			statusSO = null;
		}
		
		// remarks
		prInquiry.setRemarks(initializeRemarks(prInquiry,
				prInquiry.getCurrentStatus(), prInquiry.getRemarks(),
				ScPRIRemark.class));

		// title checker
		getManagedBean().setTitleChecker(Boolean.FALSE);
		getManagedBean().setViewOnly(Boolean.TRUE);
		getManagedBean().setShowButton(Boolean.TRUE);

		// price change lister
		priceTypeChangeListener();

		FacesContext fc = FacesContext.getCurrentInstance();
		if (isPageNavFlag) { // page nav.to purchase inquiry

			// Weather stock is available or not
			if (prInquiry.getIsStockAvailable() != null) {
				if (prInquiry.getIsStockAvailable().equals(
						ParameterConst.PI_NO_STOCK)) {
					outOfStockMessage();
				}
			}
			
			fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, PURCHASE_INQUIRY_PAGE);

		} else { // page nav.to request note
			String commonRequestNote = requisitionerAction("PROCESSED");
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, commonRequestNote);
		}
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
	 * @param scRequestSupplierItem
	 *            the sc request supplier item
	 * @param pricings
	 *            the pricings
	 * @param selectedSupplierPrice
	 *            the selected supplier price
	 * @return the list
	 */
	private List<ScSupplierItemPrice> createScSupplierItemPrices(
			ScRequestSupplierItem scRequestSupplierItem,
			List<CmPricing> pricings, boolean selectedSupplierPrice) {
		List<ScSupplierItemPrice> results = new ArrayList<>();
		for (CmPricing pricing : pricings) {
			results.add(createSupplierItemPrice(scRequestSupplierItem, pricing,
					selectedSupplierPrice));
		}
		return results;
	}

	/**
	 * Creates the supplier item price.
	 * 
	 * @param scRequestSupplierItem
	 *            the sc request supplier item
	 * @param pricing
	 *            the pricing
	 * @param selectedSupplierPrice
	 *            the selected supplier price
	 * @return the sc supplier item price
	 */
	private ScSupplierItemPrice createSupplierItemPrice(
			ScRequestSupplierItem scRequestSupplierItem, CmPricing pricing,
			boolean selectedSupplierPrice) {
		// price type

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
		// status
		if(prInquiry.getCurrentStatus() != null){
			getCurrentStatus(prInquiry.getCurrentStatus().getPmStatus());
		}

		// get selected supplier 
		ScRequestSupplierItem requestSupplierItem = prInquiry.getScRequestSupplierItems().get(0);
		ScRequestNoteDtl scRequestNoteDtl = requestSupplierItem.getScRequestNoteDtl().get(0);
		prInquiry.setRequestSupplierItem(requestSupplierItem);

		// lead time 
		if(requestSupplierItem.getSupplierItemPrices() != null && requestSupplierItem.getSupplierItemPrices().size() >0){
			requestSupplierItem.getRequestNoteItem().setLeadTime(requestSupplierItem.getSupplierItemPrices().get(0).getPricing().getCmSupplierItem().getLeadTime());
		}

		// price type drop down
		List<String> priceTypes= new ArrayList<String>();
		priceTypes.add(scRequestNoteDtl.getPriceType().getParameterCode());
		getManagedBean().setPriceType(scRequestNoteDtl.getPriceType().getParameterCode());
		getManagedBean().setPriceTypes(getPriceTypeDropdownValues(ParameterTypeConst.PRICE_TYPE, priceTypes));

		getManagedBean().setPmOrgProfile(scRequestNoteDtl.getScRequestNote().getPmOrgProfile());
		getManagedBean().setRequestNoteDtl(scRequestNoteDtl);
		getManagedBean().setScRequestSupplierItem(requestSupplierItem);

		// no of response days
		prInquiry.setDayToResponse((int)DateUtils.getDaysDifference(prInquiry.getCreatedDate(), prInquiry.getDateToResponse()));

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
		
		// current page url for page navigation from pid to request note
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setNavToPreviousPage(fc.getViewRoot().getViewId());

		fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, PURCHASE_INQUIRY_PAGE);
	}

	
	/**
	 * Gets the direct purchase so.
	 * 
	 * @return the direct purchase so
	 */
	private IDirectPurchaseSO getDirectPurchaseSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
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

	/*
	 * private TplSpecZoneDtl createSpecZone(CmSupplierZone supplierZone) {
	 * IZoneMaintenanceSO zoneMaintenanceSO = (IZoneMaintenanceSO)
	 * BusinessDelegate
	 * .getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT,
	 * IZoneMaintenanceSO.class); ZoneMaintenanceReqDTO reqDTO = new
	 * ZoneMaintenanceReqDTO(); TplSpecZoneDtl specZoneDtl = null;
	 * if(supplierZone != null && supplierZone.getSuppZoneId() >0) {
	 * 
	 * reqDTO.setCmSupplierZoneId(supplierZone.getSuppZoneId());
	 * ZoneMaintenanceResDTO resDTO =
	 * zoneMaintenanceSO.getSupplierZoneById(reqDTO); CmSupplierZone
	 * CmSupplierZone = resDTO.getSelectedCmSupplierZone();
	 * 
	 * if(CmSupplierZone.getCmSupplierZoneDtls() != null &&
	 * CmSupplierZone.getCmSupplierZoneDtls().size()>0) {
	 * 
	 * Map<String, TplSpecZoneDtl> zoneDtlMap =
	 * getManagedBean().getZoneDtlMap(); StringBuilder zoneKey = new
	 * StringBuilder(); String suppZoneName = CmSupplierZone.getSuppZoneName();
	 * ScPrInquiry scPrInquiry = getManagedBean().getScPrInquiry();
	 * 
	 * zoneKey.append(CmSupplierZone.getSuppZoneId());
	 * zoneKey.append(ZONE_SEPARATOR); zoneKey.append(suppZoneName);
	 * 
	 * if (zoneDtlMap == null) { zoneDtlMap = new HashMap<String,
	 * TplSpecZoneDtl>();
	 * 
	 * if (!scPrInquiry.isBrandNew()) { putDBSpecZoneDtlsIntoMap(zoneDtlMap); }
	 * 
	 * getManagedBean().setZoneDtlMap(zoneDtlMap); }
	 * 
	 * specZoneDtl = zoneDtlMap.get(zoneKey.toString());
	 * 
	 * if (specZoneDtl == null) { specZoneDtl = new TplSpecZoneDtl();
	 * specZoneDtl.setZoneName(suppZoneName);
	 * specZoneDtl.setSupplierZone(CmSupplierZone);
	 * 
	 * ScPIZone zone = getManagedBean().getScPIZone();
	 * 
	 * zone.setScPrInquiry(getManagedBean().getScPrInquiry());
	 * getManagedBean().getScPrInquiry().setScPIZone(zone);
	 * specZoneDtl.setTplSpecZone(zone);
	 * 
	 * List<TplSpecZoneLoc> zoneLocs = new ArrayList<TplSpecZoneLoc>();
	 * for(CmSupplierZoneDTL zoneDtl : CmSupplierZone.getCmSupplierZoneDtls()) {
	 * TplSpecZoneLoc specZoneLoc = new TplSpecZoneLoc();
	 * specZoneLoc.setPmDistrict(zoneDtl.getPmDistrict());
	 * specZoneLoc.setPmState(zoneDtl.getPmState());
	 * specZoneLoc.setPmDivision(zoneDtl.getPmDivision());
	 * specZoneLoc.setTplSpecZoneDtl(specZoneDtl); zoneLocs.add(specZoneLoc); }
	 * specZoneDtl.setTplSpecZoneLocs(zoneLocs);
	 * 
	 * zoneDtlMap.put(zoneKey.toString(), specZoneDtl); } } } return
	 * specZoneDtl; } private void putDBSpecZoneDtlsIntoMap(Map<String,
	 * TplSpecZoneDtl> zoneDtlMap) { ISpecZoneLocSO specZoneLocSO = null;
	 * 
	 * try { specZoneLocSO =
	 * BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst
	 * .COMMON_COMPONENT, ISpecZoneLocSO.class);
	 * 
	 * ScPIZone zone = getManagedBean().getScPIZone();
	 * 
	 * SpecZoneLocReqDTO reqDTO = new SpecZoneLocReqDTO();
	 * reqDTO.setDocType(ParameterConst.DOCTYPE_REQUEST_NOTE);
	 * reqDTO.setDocId(zone.getSpecZoneId()); //reqDTO.setDocId(3391L);
	 * SpecZoneLocResDTO resDTO =
	 * specZoneLocSO.retrieveZoneDtlListByDocIdNType(reqDTO);
	 * 
	 * StringBuilder dbZoneKey = null; List<TplSpecZoneDtl> dbSpecZoneDtls =
	 * resDTO.getZonesList();
	 * 
	 * for (TplSpecZoneDtl dbSpecZoneDtl : dbSpecZoneDtls) { dbZoneKey = new
	 * StringBuilder();
	 * dbZoneKey.append(dbSpecZoneDtl.getSupplierZone().getSuppZoneId());
	 * dbZoneKey.append(ZONE_SEPARATOR);
	 * dbZoneKey.append(dbSpecZoneDtl.getZoneName());
	 * 
	 * zoneDtlMap.put(dbZoneKey.toString(), dbSpecZoneDtl); } } finally {
	 * specZoneLocSO = null; } }
	 */
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
	 * Gets the mof nor.
	 * 
	 * @param supplierId
	 *            the supplier id
	 * @return the mof nor
	 */
	private String getMofNor(Long supplierId) {
		String mofNo = "";
		IMofAccountSO mofAccountSO = (IMofAccountSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IMofAccountSO.class);
		MofAccountReqDTO accountReqDTO = new MofAccountReqDTO();
		accountReqDTO.setSupplierId(supplierId);
		MofAccountResDTO mofAccountResDTO = mofAccountSO
				.retrieveMofNoBySupplierId(accountReqDTO);
		if (mofAccountResDTO.getMofAccount() != null) {
			mofNo = mofAccountResDTO.getMofAccount().getMofNo();
		}
		return mofNo;
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
		return textBeforeSign;
	}

	/**
	 * @param textBeforeSign the textBeforeSign to set
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}
}