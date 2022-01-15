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
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent; //#29557
import javax.faces.model.SelectItem;
import javax.portlet.PortletSession;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.directpurchase.IPrInquirySO;
import my.ep.biz.service.sc.directpurchase.IRequestNoteSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.PriceTypeConstants; // #29557
import my.ep.constants.CommonConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.directpurchase.PrInquiryReqDTO;
import my.ep.dto.sc.directpurchase.PrInquiryResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierZone; //#29557
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
import my.ep.persistence.entity.sc.directpurchase.ScRNWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteRemark;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZone; //#29557
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
import my.ep.web.managedbean.sc.purchaseinquiry.PurchaseInquiryManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.SourcingZoneHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox; //#29557
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
	
	//#29557 - start
	private boolean flagTierStandard; 
	private boolean flagTierPromotion;
	private boolean flagZonalStandard;
	private boolean flagZonalPromotion;
	private boolean flagZonalTierStandard;
	private boolean flagZonalTierPromotion;
	//#29557 - end
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
		
		//#29557 - start
		getManagedBean().getSupplierItemNormalPrices().clear();
		getManagedBean().getSupplierItemPromoPrices().clear();
		getManagedBean().getSupplierItemTierStandardPrices().clear();
		getManagedBean().getSupplierItemTierPromotionPrices().clear();
		getManagedBean().getSupplierItemZonalStandardPrices().clear();
		getManagedBean().getSupplierItemZonalPromotionPrices().clear();
		getManagedBean().getSupplierItemZonalTierStandardPrices().clear();
		getManagedBean().getSupplierItemZonalTierPromotionPrices().clear();
		
		CatalogueResDTO supplierResDTO = null;
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		reqDTO.setSupplierId(requestSupplierItem.getSmSupplier().getSupplierId());
		
		supplierResDTO = getICatalogueSO().retrieveSupplierZoneBySuppId(reqDTO);
		List<CmSupplierZone> cmSupplierZoneList = supplierResDTO.getSupplierZoneLs();
		
		getManagedBean().getSupplierZoneList().clear();
		TplSpecZone tplSpecZone = getPrInquirySO().getTplSpecZoneByDocTypeNDocId(
				ParameterConst.DOCTYPE_PURCHASE_INQUIRY, scPrInquiry.getPrInquiryId());
		if (!cmSupplierZoneList.isEmpty()) {
			TplSpecZoneDtl tplSpecZoneDtl = null;
			// to find tpl_spec_zone_dtl by spec_zone_id and supplier_zone_id
			if(tplSpecZone != null){
				for(CmSupplierZone cmSupplierZone :  cmSupplierZoneList){
					tplSpecZoneDtl = getPrInquirySO().getTplZoneDtlByCmSupplierZoneNTplSpecZone(cmSupplierZone,tplSpecZone);
					getManagedBean().getSupplierZoneList().add(tplSpecZoneDtl);
				}												
			}	
		}			
						
		for (ScSupplierItemPrice scSupplierItemPrice : requestSupplierItem.getSupplierItemPrices()) {
			/*if(scSupplierItemPrice.getPricing() == null){
				scSupplierItemPrice.setPricing(null);
			}*/
			
			scSupplierItemPrice.setItemPrice(null);//for first load from catalogue
			
			if (scSupplierItemPrice.getPriceType().getParameterId()
					.equals(PriceTypeConstants.NORMAL_STANDARD_PRICE) && scSupplierItemPrice.isRecordStatus()) {				
				getManagedBean().setSupplierItemStandardPrice(scSupplierItemPrice);
				getManagedBean().getSupplierItemNormalPrices().add(scSupplierItemPrice);
			} else if (scSupplierItemPrice.getPriceType().getParameterId()
					.equals(PriceTypeConstants.NORMAL_PROMOTION_PRICE) && scSupplierItemPrice.isRecordStatus()) {
				getManagedBean().setSupplierItemPromotionPrice(scSupplierItemPrice);
				getManagedBean().getSupplierItemPromoPrices().add(scSupplierItemPrice);
			} else if (scSupplierItemPrice.getPriceType().getParameterId()
					.equals(PriceTypeConstants.TIER_STANDARD_PRICE) && scSupplierItemPrice.isRecordStatus()) {
				if(scSupplierItemPrice.getPricing() == null)
					scSupplierItemPrice.setPricing(new CmPricing());
				getManagedBean().getSupplierItemTierStandardPrices().add(scSupplierItemPrice);
			} else if (scSupplierItemPrice.getPriceType().getParameterId()
					.equals(PriceTypeConstants.TIER_PROMOTION_PRICE) && scSupplierItemPrice.isRecordStatus()) {
				if(scSupplierItemPrice.getPricing() == null)
					scSupplierItemPrice.setPricing(new CmPricing());
				getManagedBean().getSupplierItemTierPromotionPrices().add(scSupplierItemPrice);
			}else if (scSupplierItemPrice.getPriceType().getParameterId()
					.equals(PriceTypeConstants.ZONAL_STANDARD_PRICE) && scSupplierItemPrice.isRecordStatus()) {
				if(scSupplierItemPrice.getPricing() == null)
					scSupplierItemPrice.setPricing(new CmPricing());
				if(scSupplierItemPrice.getTplSpecZone() == null)
					scSupplierItemPrice.setTplSpecZone(new TplSpecZoneDtl());  
				getManagedBean().getSupplierItemZonalStandardPrices().add(scSupplierItemPrice);
			} else if (scSupplierItemPrice.getPriceType().getParameterId()
					.equals(PriceTypeConstants.ZONAL_PROMOTION_PRICE) && scSupplierItemPrice.isRecordStatus()) {
				if(scSupplierItemPrice.getPricing() == null)
					scSupplierItemPrice.setPricing(new CmPricing());
				if(scSupplierItemPrice.getTplSpecZone() == null)
					scSupplierItemPrice.setTplSpecZone(new TplSpecZoneDtl()); 
				getManagedBean().getSupplierItemZonalPromotionPrices().add(scSupplierItemPrice);
			} else if (scSupplierItemPrice.getPriceType().getParameterId()
					.equals(PriceTypeConstants.ZONAL_TIER_STANDARD_PRICE) && scSupplierItemPrice.isRecordStatus()) {
				if(scSupplierItemPrice.getPricing() == null)
					scSupplierItemPrice.setPricing(new CmPricing());
				if(scSupplierItemPrice.getTplSpecZone() == null)
					scSupplierItemPrice.setTplSpecZone(new TplSpecZoneDtl()); 
				getManagedBean().getSupplierItemZonalTierStandardPrices().add(scSupplierItemPrice);
			} else if (scSupplierItemPrice.getPriceType().getParameterId()
					.equals(PriceTypeConstants.ZONAL_TIER_PROMOTION_PRICE) && scSupplierItemPrice.isRecordStatus()) {
				if(scSupplierItemPrice.getPricing() == null)
					scSupplierItemPrice.setPricing(new CmPricing());
				if(scSupplierItemPrice.getTplSpecZone() == null)
					scSupplierItemPrice.setTplSpecZone(new TplSpecZoneDtl()); 
				getManagedBean().getSupplierItemZonalTierPromotionPrices().add(scSupplierItemPrice);
			}
		}
		//#29557 - end

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
									//scSupplierItemPrice.setItemPrice(null); #29557 - utk sementara
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
			requestNoteDtl.setProcurementType(getParameterByTypeAndCode(ParameterTypeConst.PROCUREMENT_TYPE,
					requestNoteDtl.getProcurementType().getParameterCode()));

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
					requestNoteDtl.setJustificationType(getReasonByCategoryAndCode(ReasonCategory.JUSTIFICATION_PK7
							.value(), requestNoteDtl.getJustificationType().getReasonCode()));
				}
			} else {
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
				requestNoteDtl.setProcurementType(getParameterByTypeAndCode(ParameterTypeConst.PROCUREMENT_TYPE,
						requestNoteDtl.getProcurementType().getParameterCode()));

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

	public String supplierSave() {
		debugLogger("supplierSave");
		//#29557 - start	
		savePriceTab("SAVE");
		//#29557 - end
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
			scRequestSupplierItem.getSupplierItemPrices().clear(); //#29557
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
		
		// #29557 - start
		savePriceTab("SUBMIT");
		// #29557 - end

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
		if (procurementType.equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL)
				|| procurementType.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			getManagedBean().setProcTypeOption(Boolean.FALSE);
			getManagedBean().setJustTypeOption(Boolean.FALSE);

		} else if (procurementType.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
			getManagedBean().setJustDropdown(getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_AD_HOC.value()));
		} else if (procurementType.equalsIgnoreCase(ParameterConst.PROC_TYPE_PK7)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
			getManagedBean().setJustDropdown(getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_PK7.value()));
		}
		return procurementType;
	}

	public void procurementTypeEvent() {
		log.info("procurementTypeEvent");
		String procurementType = checkProcTypeOption();
		getManagedBean().getRequestNoteDtl().setJustificationType(new PmReason());
		validatePk7(procurementType);
	}
	
	public void validatePk7(String procurementType) {
		if (procurementType.equalsIgnoreCase(ParameterConst.PROC_TYPE_PK7)) {
			// MaxProcurementAmt
			BigDecimal maxProcurementAmt = getPRThresholdAmount();
			DecimalFormat amountDF = new DecimalFormat(
					new CommonUtils().getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
							MessageConst.INFO_760440, new String[] { amountDF.format(maxProcurementAmt) },
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

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
			validateFinalOrderQty(); //#29557
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
					
					requestNote.setRemarks(initializeRemarks(requestNote,
							requestNote.getCurrentStatus(),
							requestNote.getRemarks(), ScRequestNoteRemark.class));

					reqNoteBackingBean.getManagedBean().setFulfillmentType(
							ParameterConst.FULFILMENT_TYPE_ONE_OFF);
					reqNoteBackingBean.getManagedBean().setItemSearchBackBtn(Boolean.TRUE);
					
					//#29557 - start
					reqNoteBackingBean.getManagedBean().setAssignedState(true);
					reqNoteBackingBean.getManagedBean().setPurchaseInquiryContinuity(true);
					//#29557 - end
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
	
	//#29557 - start
	private void validateFinalOrderQty() {
		ScRequestSupplierItem scRequestSupplierItem = getManagedBean().getScRequestSupplierItem();
		BigDecimal  minTierQty = BigDecimal.ZERO;
		BigDecimal  maxTierQty = BigDecimal.ZERO;
		
		if (scRequestSupplierItem.getRequestNoteItem().getQuantity() != null) {			
			ScSupplierItemPrice scSupplierItemPrice = getManagedBean().getSupplierItemPrices().get(0);
			
			minTierQty = getPrInquirySO().getMinimumTierQuantity(scSupplierItemPrice.getScRequestSupplierItem().getRequestSupplierItemId());
			maxTierQty = getPrInquirySO().getMaximumTierQuantity(scSupplierItemPrice.getScRequestSupplierItem().getRequestSupplierItemId());
			
			if ((minTierQty != null && scRequestSupplierItem.getRequestNoteItem().getQuantity().compareTo(minTierQty) < 0)
					|| (maxTierQty != null && scRequestSupplierItem.getRequestNoteItem().getQuantity().compareTo(maxTierQty) > 0)) {
				throw new EpException(MessageConst.ERROR_745055);
			}
		}	
	}

	//#29557 - end

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
				
				//#29557 - start
				getManagedBean().getSupplierItemNormalPrices().clear();
				getManagedBean().getSupplierItemPromoPrices().clear();
				getManagedBean().getSupplierItemTierStandardPrices().clear();
				getManagedBean().getSupplierItemTierPromotionPrices().clear();
				getManagedBean().getSupplierItemZonalStandardPrices().clear();
				getManagedBean().getSupplierItemZonalPromotionPrices().clear();
				getManagedBean().getSupplierItemZonalTierStandardPrices().clear();
				getManagedBean().getSupplierItemZonalTierPromotionPrices().clear();
				
				CatalogueResDTO supplierResDTO = null;
				CatalogueReqDTO reqDTO = new CatalogueReqDTO();
				reqDTO.setSupplierId(requestSupplierItem.getSmSupplier().getSupplierId());
				
				supplierResDTO = getICatalogueSO().retrieveSupplierZoneBySuppId(reqDTO);
				List<CmSupplierZone> cmSupplierZoneList = supplierResDTO.getSupplierZoneLs();
				
				getManagedBean().getSupplierZoneList().clear();
				TplSpecZone tplSpecZone = getPrInquirySO().getTplSpecZoneByDocTypeNDocId(
						ParameterConst.DOCTYPE_PURCHASE_INQUIRY, prInquiry.getPrInquiryId());
				if (!cmSupplierZoneList.isEmpty()) {
					TplSpecZoneDtl tplSpecZoneDtl = null;
					// to find tpl_spec_zone_dtl by spec_zone_id and supplier_zone_id
					if(tplSpecZone != null){
						for(CmSupplierZone cmSupplierZone :  cmSupplierZoneList){
							tplSpecZoneDtl = getPrInquirySO().getTplZoneDtlByCmSupplierZoneNTplSpecZone(cmSupplierZone,tplSpecZone);
							getManagedBean().getSupplierZoneList().add(tplSpecZoneDtl);
						}												
					}	
				}			
								
				for (ScSupplierItemPrice scSupplierItemPrice : requestSupplierItem.getSupplierItemPrices()) {
					/*if(scSupplierItemPrice.getPricing() == null){
						scSupplierItemPrice.setPricing(null);
					}*/
					if (scSupplierItemPrice.getPriceType().getParameterId()
							.equals(PriceTypeConstants.NORMAL_STANDARD_PRICE) && scSupplierItemPrice.isRecordStatus()) {				
						getManagedBean().setSupplierItemStandardPrice(scSupplierItemPrice);
						getManagedBean().getSupplierItemNormalPrices().add(scSupplierItemPrice);
					} else if (scSupplierItemPrice.getPriceType().getParameterId()
							.equals(PriceTypeConstants.NORMAL_PROMOTION_PRICE) && scSupplierItemPrice.isRecordStatus()) {
						getManagedBean().setSupplierItemPromotionPrice(scSupplierItemPrice);
						getManagedBean().getSupplierItemPromoPrices().add(scSupplierItemPrice);
					} else if (scSupplierItemPrice.getPriceType().getParameterId()
							.equals(PriceTypeConstants.TIER_STANDARD_PRICE) && scSupplierItemPrice.isRecordStatus()) {
						if(scSupplierItemPrice.getPricing() == null)
							scSupplierItemPrice.setPricing(new CmPricing());
						getManagedBean().getSupplierItemTierStandardPrices().add(scSupplierItemPrice);
					} else if (scSupplierItemPrice.getPriceType().getParameterId()
							.equals(PriceTypeConstants.TIER_PROMOTION_PRICE) && scSupplierItemPrice.isRecordStatus()) {
						if(scSupplierItemPrice.getPricing() == null)
							scSupplierItemPrice.setPricing(new CmPricing());
						getManagedBean().getSupplierItemTierPromotionPrices().add(scSupplierItemPrice);
					}else if (scSupplierItemPrice.getPriceType().getParameterId()
							.equals(PriceTypeConstants.ZONAL_STANDARD_PRICE) && scSupplierItemPrice.isRecordStatus()) {
						if(scSupplierItemPrice.getPricing() == null)
							scSupplierItemPrice.setPricing(new CmPricing());
						if(scSupplierItemPrice.getTplSpecZone() == null)
							scSupplierItemPrice.setTplSpecZone(new TplSpecZoneDtl());  
						getManagedBean().getSupplierItemZonalStandardPrices().add(scSupplierItemPrice);
					} else if (scSupplierItemPrice.getPriceType().getParameterId()
							.equals(PriceTypeConstants.ZONAL_PROMOTION_PRICE) && scSupplierItemPrice.isRecordStatus()) {
						if(scSupplierItemPrice.getPricing() == null)
							scSupplierItemPrice.setPricing(new CmPricing());
						if(scSupplierItemPrice.getTplSpecZone() == null)
							scSupplierItemPrice.setTplSpecZone(new TplSpecZoneDtl()); 
						getManagedBean().getSupplierItemZonalPromotionPrices().add(scSupplierItemPrice);
					} else if (scSupplierItemPrice.getPriceType().getParameterId()
							.equals(PriceTypeConstants.ZONAL_TIER_STANDARD_PRICE) && scSupplierItemPrice.isRecordStatus()) {
						if(scSupplierItemPrice.getPricing() == null)
							scSupplierItemPrice.setPricing(new CmPricing());
						if(scSupplierItemPrice.getTplSpecZone() == null)
							scSupplierItemPrice.setTplSpecZone(new TplSpecZoneDtl()); 
						getManagedBean().getSupplierItemZonalTierStandardPrices().add(scSupplierItemPrice);
					} else if (scSupplierItemPrice.getPriceType().getParameterId()
							.equals(PriceTypeConstants.ZONAL_TIER_PROMOTION_PRICE) && scSupplierItemPrice.isRecordStatus()) {
						if(scSupplierItemPrice.getPricing() == null)
							scSupplierItemPrice.setPricing(new CmPricing());
						if(scSupplierItemPrice.getTplSpecZone() == null)
							scSupplierItemPrice.setTplSpecZone(new TplSpecZoneDtl()); 
						getManagedBean().getSupplierItemZonalTierPromotionPrices().add(scSupplierItemPrice);
					}
				}
				//#29557 - end

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
			if (isGovtSellerBySupplierId(smSupplier.getSupplierId())) {
				getManagedBean().setProcurementTypes(
						getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE, Boolean.TRUE));
			} else {
				getManagedBean().setProcurementTypes(
						getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE, Boolean.FALSE));
			}
			checkProcTypeOption();
			validatePk7(procurementType);
			getManagedBean().setViewOnly(Boolean.FALSE);
			getManagedBean().setDisProcTyp(Boolean.TRUE);
		} else {
			//#29557 - start
			if (DPStatusConstants.PRI_PENDING_RN_CREATION.equals(currentStatusId)) {
				CmItem cmItem = requestSupplierItem.getRequestNoteItem().getCmItem();
				SmSupplier smSupplier = requestSupplierItem.getSmSupplier();

				List<Long> cmSuppItemIds = getPrInquirySO().cmSupplierItemIds(cmItem.getItemId(),
						smSupplier.getSupplierId());

				List<CmPricing> cmPricings = getICatalogueSO().getCmPricings(cmSuppItemIds);
				getManagedBean().setPriceTypes(
						getPriceTypeDropdownValues(ParameterTypeConst.PRICE_TYPE, getPriceTypes(cmPricings)));
			}
			//#29557 - end
			getManagedBean().setPriceType(getParameterDesc(prInquiry.getPriceType()));
			getManagedBean().setViewOnly(Boolean.TRUE);
			getManagedBean().setDisProcTyp(Boolean.FALSE);
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
	
	// Start - #29557
	public void onChangeToQty(String priceType){
		FacesScopes facesScopes = new FacesScopes();
		List<ScSupplierItemPrice> scSupplierItemPrices = new ArrayList<ScSupplierItemPrice>();

		switch (priceType) {
		case "03":
			scSupplierItemPrices = this.getManagedBean().getSupplierItemTierStandardPrices();
			break;
		case "04":
			scSupplierItemPrices = this.getManagedBean().getSupplierItemTierPromotionPrices();
			break;
		case "07":
			scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalTierStandardPrices();
			break;
		case "08":
			scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalTierPromotionPrices();
			break;
		default:
			// do nothing
			break;
		}

		for (ScSupplierItemPrice scSupplierItemPrice : scSupplierItemPrices) {
			if (scSupplierItemPrice.getFromQty() != null
					&& StringUtils.isNotBlank(scSupplierItemPrice.getFromQty().toString())) {
				
				if (scSupplierItemPrice.getToQty() != null) {
					if (scSupplierItemPrice.getToQty().compareTo(scSupplierItemPrice.getFromQty()) <= 0) {
						throw new EpException(MessageConst.ERROR_760533, new String[] {
								ResourceBundleUtils.getMessage("lbl.sc.price.inquiry.to.qty",
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()),
								ResourceBundleUtils.getMessage("lbl.sc.price.inquiry.from.qty",
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
					}
				}
			}
		}
		
		int index = 0;
		BigDecimal range = BigDecimal.valueOf(0);
		if(scSupplierItemPrices.size() > 1) {
			for (ScSupplierItemPrice scSupplierItemPrice : scSupplierItemPrices) {
				
				if(index == 0){
					if (scSupplierItemPrice.getToQty() == null) {
						throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
								"lbl.sc.to.qty", ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
					}
					
					range = scSupplierItemPrice.getToQty().subtract(scSupplierItemPrice.getFromQty());					
				}else{
					ScSupplierItemPrice prevScSupplierItemPrice = scSupplierItemPrices.get(index - 1);
					if (scSupplierItemPrice.getToQty() == null) {
						if(index != scSupplierItemPrices.size() - 1)
						throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
								"lbl.sc.to.qty", ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
					}
					
					scSupplierItemPrice.setFromQty(prevScSupplierItemPrice.getToQty().add(BigDecimal.valueOf(1)));
					//scSupplierItemPrice.setToQty(scSupplierItemPrice.getFromQty().add(range));
				}
				index++;
			}
		}
	}
	
	public void onChangeFromQty(String priceType) {
		FacesScopes facesScopes = new FacesScopes();
		List<ScSupplierItemPrice> scSupplierItemPrices = new ArrayList<ScSupplierItemPrice>();

		switch (priceType) {
		case "03":
			scSupplierItemPrices = this.getManagedBean().getSupplierItemTierStandardPrices();
			break;
		case "04":
			scSupplierItemPrices = this.getManagedBean().getSupplierItemTierPromotionPrices();
			break;
		case "07":
			scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalTierStandardPrices();
			break;
		case "08":
			scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalTierPromotionPrices();
			break;
		default:
			// do nothing
			break;
		}

		for (ScSupplierItemPrice scSupplierItemPrice : scSupplierItemPrices) {
			if (scSupplierItemPrice.getToQty() != null
					&& StringUtils.isNotBlank(scSupplierItemPrice.getToQty().toString())) {

				if (scSupplierItemPrice.getFromQty().compareTo(scSupplierItemPrice.getToQty()) >= 0) {
					throw new EpException(MessageConst.ERROR_760532, new String[] {
							ResourceBundleUtils.getMessage("lbl.sc.price.inquiry.from.qty",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()),
							ResourceBundleUtils.getMessage("lbl.sc.price.inquiry.to.qty",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
				}
			}
		}
	}
	
	public void onChangeNewPrice(String priceType, BigDecimal newPrice, BigDecimal cataloguePrice) {
		FacesScopes facesScopes = new FacesScopes();
		List<ScSupplierItemPrice> scSupplierItemPrices = new ArrayList<ScSupplierItemPrice>();
		
		if (cataloguePrice != null && cataloguePrice.compareTo(BigDecimal.ZERO) != 0) {
			if (newPrice.compareTo(cataloguePrice) > 0) {
				throw new EpException(MessageConst.ERROR_760535, new String[] {
						ResourceBundleUtils.getMessage("lbl.sc.price.inquiry.price.offered.per.unit",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()),
						ResourceBundleUtils.getMessage("lbl.sc.price.inquiry.price.published.per.unit",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
			}
		}
		
		switch (priceType) {
		case "03":
			scSupplierItemPrices = this.getManagedBean().getSupplierItemTierStandardPrices();
			break;
		case "04":
			scSupplierItemPrices = this.getManagedBean().getSupplierItemTierPromotionPrices();
			break;
		case "07":
			scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalTierStandardPrices();
			break;
		case "08":
			scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalTierPromotionPrices();
			break;
		default:
			// do nothing
			break;
		}
		
		int index = 0;
		for (ScSupplierItemPrice scSupplierItemPrice : scSupplierItemPrices) {
			if(index != 0){
				ScSupplierItemPrice prevScSupplierItemPrice = scSupplierItemPrices.get(index - 1);
				
				if (scSupplierItemPrice.getItemPrice().compareTo(prevScSupplierItemPrice.getItemPrice()) > 0) {
					throw new EpException(MessageConst.ERROR_760536, new String[] {
							this.getManagedBean().getScRequestSupplierItem().getRequestNoteItem().getCmItem()
									.getItemName(),
							scSupplierItemPrice.getFromQty().toString().concat("-")
									.concat(scSupplierItemPrice.getToQty() != null ? scSupplierItemPrice.getToQty().toString() : ""),
							prevScSupplierItemPrice.getItemPrice().toString() });
				}
			}
			index++;
		}
	}
	
	private void validateScSupplierItemPrice(List<ScSupplierItemPrice> scSupplierItemPrices) {
		FacesScopes facesScopes = new FacesScopes();
		for (ScSupplierItemPrice scSupplierItemPrice : scSupplierItemPrices) {
			if (scSupplierItemPrice.getItemPrice() == null) {
				throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
						"lbl.sc.price.inquiry.price.offered.per.unit", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						facesScopes.getUserLocale()) });
			}
		}

	}
	
	private void validateTierRowCount(){
		
		Boolean throwError = false;
		if (this.getManagedBean().getSupplierItemTierStandardPrices().size() == 1)
			throwError = true;
		if (this.getManagedBean().getSupplierItemTierPromotionPrices().size() == 1)
			throwError = true;
		if (this.getManagedBean().getSupplierItemZonalTierStandardPrices().size() == 1)
			throwError = true;
		if (this.getManagedBean().getSupplierItemZonalTierPromotionPrices().size() == 1)
			throwError = true;
		
		if(throwError){
			throw new EpException(MessageConst.ERROR_760537);
		}
	}

	private void validateScSupplierItemPriceList(Integer priceTypeList,ScSupplierItemPrice addedScSupplierItemPrice,Boolean ignoreLastTier) {
		int i = priceTypeList;
		FacesScopes facesScopes = new FacesScopes();
		
		if (i == 0 || i == 1){
			if (addedScSupplierItemPrice.getFromQty() == null){throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.sc.from.qty",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});}
			if (addedScSupplierItemPrice.getToQty() == null) {
				if (!ignoreLastTier) {
					throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
							"lbl.sc.to.qty", ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
				}
			}
			if (addedScSupplierItemPrice.getItemPrice() == null){throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.ss.item.detail.price.offered",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});}
		}			
		else if (i == 2 || i == 3){
			if (addedScSupplierItemPrice.getTplSpecZone() == null){
				addedScSupplierItemPrice.setTplSpecZone(new TplSpecZoneDtl());
				throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.pm.zone.name",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});}
			if (addedScSupplierItemPrice.getItemPrice() == null){throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.ss.item.detail.price.offered",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});}
		}

		else if (i == 4 || i == 5){
			if (addedScSupplierItemPrice.getTplSpecZone() == null){
				addedScSupplierItemPrice.setTplSpecZone(new TplSpecZoneDtl());
				throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.pm.zone.name",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});}
			if (addedScSupplierItemPrice.getFromQty() == null){throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.sc.from.qty",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});}
			if (addedScSupplierItemPrice.getToQty() == null){
				if (!ignoreLastTier) {
					throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
							"lbl.sc.to.qty", ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
				}
			}
			if (addedScSupplierItemPrice.getItemPrice() == null){throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.ss.item.detail.price.offered",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale())});}
		}
			
	}
	
	public void savePriceTab(String outcome) {

		HashMap<Long, List<ScSupplierItemPrice>> scSupplierItemPriceListing = new HashMap<Long, List<ScSupplierItemPrice>>();
		scSupplierItemPriceListing.put(PriceTypeConstants.TIER_STANDARD_PRICE, this.getManagedBean()
				.getSupplierItemTierStandardPrices());
		scSupplierItemPriceListing.put(PriceTypeConstants.TIER_PROMOTION_PRICE, this.getManagedBean()
				.getSupplierItemTierPromotionPrices());
		scSupplierItemPriceListing.put(PriceTypeConstants.ZONAL_STANDARD_PRICE, this.getManagedBean()
				.getSupplierItemZonalStandardPrices());
		scSupplierItemPriceListing.put(PriceTypeConstants.ZONAL_PROMOTION_PRICE, this.getManagedBean()
				.getSupplierItemZonalPromotionPrices());
		scSupplierItemPriceListing.put(PriceTypeConstants.ZONAL_TIER_STANDARD_PRICE, this.getManagedBean()
				.getSupplierItemZonalTierStandardPrices());
		scSupplierItemPriceListing.put(PriceTypeConstants.ZONAL_TIER_PROMOTION_PRICE, this.getManagedBean()
				.getSupplierItemZonalTierPromotionPrices());

		// save for type 01
		if (this.getManagedBean().getSupplierItemNormalPrices().size() != 0 && !this.getManagedBean().getSupplierItemNormalPrices().isEmpty()) {
			List<ScSupplierItemPrice> tempScSupplierItemPrices = this.getManagedBean().getSupplierItemNormalPrices();

			for (ScSupplierItemPrice addedScSupplierItemPrice : tempScSupplierItemPrices) {
				if (addedScSupplierItemPrice.getPricing() == null) {
					addedScSupplierItemPrice.setPricing(null);
				} else if (addedScSupplierItemPrice.getPricing().getPrice() == null) {
					addedScSupplierItemPrice.setPricing(null);
				}
			}

			List<ScSupplierItemPrice> addedScSupplierItemPrice = getPrInquirySO().saveSupplierItemPrices(
					tempScSupplierItemPrices);

			this.getManagedBean().getSupplierItemNormalPrices().clear();
            this.getManagedBean().getSupplierItemNormalPrices().add(addedScSupplierItemPrice.get(0));
			validateScSupplierItemPrice(this.getManagedBean().getSupplierItemNormalPrices());

		}

		// save for type 02
		if (this.getManagedBean().getSupplierItemPromoPrices().size() != 0 && !this.getManagedBean().getSupplierItemPromoPrices().isEmpty()) {
			List<ScSupplierItemPrice> tempScSupplierItemPrices = this.getManagedBean().getSupplierItemPromoPrices();

			for (ScSupplierItemPrice addedScSupplierItemPrice : tempScSupplierItemPrices) {
				if (addedScSupplierItemPrice.getPricing() == null) {
					addedScSupplierItemPrice.setPricing(null);
				} else if (addedScSupplierItemPrice.getPricing().getPrice() == null) {
					addedScSupplierItemPrice.setPricing(null);
				}
			}

			List<ScSupplierItemPrice> addedScSupplierItemPrice = getPrInquirySO().saveSupplierItemPrices(
					tempScSupplierItemPrices);

			this.getManagedBean().getSupplierItemPromoPrices().clear();
			this.getManagedBean().getSupplierItemPromoPrices().add(addedScSupplierItemPrice.get(0));
			//validateScSupplierItemPrice(this.getManagedBean().getSupplierItemPromoPrices());

		}

		// save for type 03,04,05,06,07 & 08
		for (int i = 0; i < scSupplierItemPriceListing.size(); i++) {
			Object currentKey = scSupplierItemPriceListing.keySet().toArray()[i];
			
			if (scSupplierItemPriceListing.get(currentKey).size() != 0 && !scSupplierItemPriceListing.get(currentKey).isEmpty()) {
				int index = 0;
				Boolean ignoreLastTier = false;
				for (ScSupplierItemPrice addedScSupplierItemPrice : scSupplierItemPriceListing.get(currentKey)) {
					if (addedScSupplierItemPrice.getPricing() == null) {
						addedScSupplierItemPrice.setPricing(null);
					} else if (addedScSupplierItemPrice.getPricing().getPrice() == null) {
						addedScSupplierItemPrice.setPricing(null);
					}
					
					if(i != 0 &&  i!= 1){
						if (addedScSupplierItemPrice.getTplSpecZone().getSpecZoneId() == null
								|| addedScSupplierItemPrice.getTplSpecZone().getSpecZoneId() == 0) {
							addedScSupplierItemPrice.setTplSpecZone(null);
						}
					}
					
					if(index == scSupplierItemPriceListing.get(currentKey).size() - 1){
						ignoreLastTier = true;						
					}
					
					validateScSupplierItemPriceList(i ,addedScSupplierItemPrice,ignoreLastTier);					
					index++;
				}
				
				PrInquiryReqDTO prInquiryReqDTO = new PrInquiryReqDTO();
				prInquiryReqDTO.setPrInquiryId(this.getManagedBean().getScPrInquiry().getPrInquiryId());
				prInquiryReqDTO.setSupplierItemPrices(scSupplierItemPriceListing.get(currentKey));
				prInquiryReqDTO.setPmUser(new FacesScopes().getUserSessionBean());
				
				if(i == 0 || i == 1 || i == 4 || i == 5){
					for(ScSupplierItemPrice scSupplierItemPrice : prInquiryReqDTO.getSupplierItemPrices()){
						onChangeFromQty(scSupplierItemPrice.getPriceType().getParameterCode());
						onChangeToQty(scSupplierItemPrice.getPriceType().getParameterCode());
						onChangeNewPrice(scSupplierItemPrice.getPriceType().getParameterCode(),
								scSupplierItemPrice.getItemPrice(), scSupplierItemPrice.getPricing().getPrice());
					}
				}				
				
				List<ScSupplierItemPrice> addedScSupplierItemPrice = getPrInquirySO().saveSupplierItemPrices(prInquiryReqDTO);

				if (i == 0)
					this.getManagedBean().setSupplierItemTierStandardPrices(addedScSupplierItemPrice);
				else if (i == 1)
					this.getManagedBean().setSupplierItemTierPromotionPrices(addedScSupplierItemPrice);
				else if (i == 2)
					this.getManagedBean().setSupplierItemZonalStandardPrices(addedScSupplierItemPrice);
				else if (i == 3)
					this.getManagedBean().setSupplierItemZonalPromotionPrices(addedScSupplierItemPrice);
				else if (i == 4)
					this.getManagedBean().setSupplierItemZonalTierStandardPrices(addedScSupplierItemPrice);
				else if (i == 5)
					this.getManagedBean().setSupplierItemZonalTierPromotionPrices(addedScSupplierItemPrice);
			}
		}
		
		if(outcome.equals("SUBMIT")){
			validateTierRowCount();
		}
	}
	
	public void deleteScSupplierItemPrices() {
		
		List<ScSupplierItemPrice> deleteScSupplierItemPrices = new ArrayList<ScSupplierItemPrice>();
			
		if (this.getManagedBean().getDeleteSupplierItemTierStandardPrices() != null && !this.getManagedBean().getDeleteSupplierItemTierStandardPrices().isEmpty())
			deleteScSupplierItemPrices = this.getManagedBean().getDeleteSupplierItemTierStandardPrices();
		else if (this.getManagedBean().getDeleteSupplierItemTierPromotionPrices() != null && !this.getManagedBean().getDeleteSupplierItemTierPromotionPrices().isEmpty())
			deleteScSupplierItemPrices = this.getManagedBean().getDeleteSupplierItemTierPromotionPrices();
		else if (this.getManagedBean().getDeleteSupplierItemZonalStandardPrices() != null && !this.getManagedBean().getDeleteSupplierItemZonalStandardPrices().isEmpty())
			deleteScSupplierItemPrices = this.getManagedBean().getDeleteSupplierItemZonalStandardPrices();
		else if (this.getManagedBean().getDeleteSupplierItemZonalPromotionPrices() != null && !this.getManagedBean().getDeleteSupplierItemZonalPromotionPrices().isEmpty())
			deleteScSupplierItemPrices = this.getManagedBean().getDeleteSupplierItemZonalPromotionPrices();
		else if (this.getManagedBean().getDeleteSupplierItemZonalTierStandardPrices() != null && !this.getManagedBean().getDeleteSupplierItemZonalTierStandardPrices().isEmpty())
			deleteScSupplierItemPrices = this.getManagedBean().getDeleteSupplierItemZonalTierStandardPrices();
		else if (this.getManagedBean().getDeleteSupplierItemZonalTierPromotionPrices() != null && !this.getManagedBean().getDeleteSupplierItemZonalTierPromotionPrices().isEmpty())
			deleteScSupplierItemPrices = this.getManagedBean().getDeleteSupplierItemZonalTierPromotionPrices();
			
		for (ScSupplierItemPrice deleteScSupplierItemPrice : deleteScSupplierItemPrices){
			if(deleteScSupplierItemPrice.getPricing()== null){
				deleteScSupplierItemPrice.setPricing(null);
			}
			else if(deleteScSupplierItemPrice.getPricing().getPrice() == null){
				deleteScSupplierItemPrice.setPricing(null);
			}
		}
		
		List<ScSupplierItemPrice> resultList = getPrInquirySO().deleteSupplierItemPrices(deleteScSupplierItemPrices);
	}
	
	// ZonalTierStandardPrice -- start
	public void addSupplierItemZonalTierStandardPrices() {	
		FacesScopes facesScopes = new FacesScopes();
		
		if(getManagedBean().getSupplierItemZonalTierStandardPrices() == null){
			this.getManagedBean().setSupplierItemZonalTierStandardPrices(new ArrayList<ScSupplierItemPrice>());
		}
		
		ScSupplierItemPrice scSupplierItemPrice = new ScSupplierItemPrice();
		ScRequestSupplierItem requestSupplierItem = this.getManagedBean().getScRequestSupplierItem();
		ScSupplierItemPrice prevScSupplierItemPrice = null;
		if(this.getManagedBean().getSupplierItemZonalTierStandardPrices().size() > 0) {
			prevScSupplierItemPrice = this.getManagedBean().getSupplierItemZonalTierStandardPrices().get(this.getManagedBean().getSupplierItemZonalTierStandardPrices().size() - 1);
		}
		if(prevScSupplierItemPrice == null){
			scSupplierItemPrice.setFromQty(BigDecimal.valueOf(1));
		}
		if(prevScSupplierItemPrice != null){
			if (prevScSupplierItemPrice.getToQty() == null) {
				throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
						"lbl.sc.to.qty", ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
			}

			scSupplierItemPrice.setFromQty(prevScSupplierItemPrice.getToQty().add(BigDecimal.valueOf(1)));
		}
		
		scSupplierItemPrice.setScRequestSupplierItem(requestSupplierItem);
		scSupplierItemPrice.setPricing(null);
		scSupplierItemPrice.setTplSpecZone(new TplSpecZoneDtl());
		scSupplierItemPrice.setPriceType(getParameterByTypeAndCode(
				ParameterTypeConst.PRICE_TYPE, ParameterConst.PRICE_TYPE_ZONAL_TIER));
		
		this.getManagedBean().getSupplierItemZonalTierStandardPrices().add(scSupplierItemPrice); 
		
	}
	
	public void selectSingleRowZonalTierStandardPrice(AjaxBehaviorEvent event) {
		boolean isSelectedALL= Boolean.TRUE;
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		if (flag) {
			setFlagZonalTierStandard(Boolean.FALSE);
		} 

		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalTierStandardPrices();
		
		if (scSupplierItemPrices != null) {
			for (ScSupplierItemPrice scSupplierItemPrice : scSupplierItemPrices) {
				if (!scSupplierItemPrice.isSelected()) {
					isSelectedALL= Boolean.FALSE;
					break;
				} 
			}
			
			if(isSelectedALL){
				setFlagZonalTierStandard(isSelectedALL);
			}
			this.getManagedBean().setEnableZonalTierNormalDel(true);
		}
		
		if (scSupplierItemPrices.size() == 0) {
			this.getManagedBean().setEnableZonalTierNormalDel(false);
		}
		
		this.getManagedBean().setPricingTabIndex(3); 
		this.getManagedBean().setZonalTierPricingIndex(0);
	}
	
	public void selectAllZonalTierStandardPrice(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalTierStandardPrices();
		
		if (scSupplierItemPrices != null) {
			for (ScSupplierItemPrice scSupplierItemPrice: scSupplierItemPrices) {
				if (flag) {
					scSupplierItemPrice.setSelected(true);
				} else {
					scSupplierItemPrice.setSelected(false);
				}
			}
			this.getManagedBean().setEnableZonalTierNormalDel(true);
		}
		if (scSupplierItemPrices.size() == 0) {
			this.getManagedBean().setEnableZonalTierNormalDel(false);
		}

		this.getManagedBean().setPricingTabIndex(3); 
		this.getManagedBean().setZonalTierPricingIndex(0);
	}
	
	public void deleteSupplierItemZonalTierStandardPrices() {
		log.info("deleteZonalTierStandard");
		
		int counter = 0;
		List<ScSupplierItemPrice> deleteScSupplierItemPrices = new ArrayList<ScSupplierItemPrice>();
		List<ScSupplierItemPrice> mainScSupplierItemPrices = new ArrayList<ScSupplierItemPrice>();
		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalTierStandardPrices();
		
		if (scSupplierItemPrices != null && !scSupplierItemPrices.isEmpty()) {
			for (ScSupplierItemPrice scSupplierItemPriceDtl : scSupplierItemPrices) {
				if (scSupplierItemPriceDtl.isSelected()) {
					
					scSupplierItemPriceDtl.setDeleted(Boolean.TRUE);
					
					if(scSupplierItemPriceDtl.getSupplierItemPriceId() != null){
						deleteScSupplierItemPrices.add(scSupplierItemPriceDtl);
					}
					
					++ counter;
				} else{
					mainScSupplierItemPrices.add(scSupplierItemPriceDtl);
				}
			}
			
			if(counter == 0){
				throw new EpException(MessageConst.ERROR_760158);
			}
		}
		
		this.getManagedBean().setDeleteSupplierItemZonalTierStandardPrices(deleteScSupplierItemPrices);
		deleteScSupplierItemPrices();
		this.getManagedBean().setSupplierItemZonalTierStandardPrices(mainScSupplierItemPrices);
		
		if (this.getManagedBean().getSupplierItemZonalTierStandardPrices().size() == 0){
			this.getManagedBean().setEnableZonalTierNormalDel(false);
		}
		
		this.getManagedBean().setPricingTabIndex(3); 
		this.getManagedBean().setZonalTierPricingIndex(0);	
		
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setNavToPreviousPage(fc.getViewRoot().getViewId());

		fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, PURCHASE_INQUIRY_PAGE);
	}	
	// ZonalTierStandardPrice -- end
	
	// ZonalTierPromotionPrice -- start
	public void addSupplierItemZonalTierPromotionPrices() {
		FacesScopes facesScopes = new FacesScopes();
		
		if (getManagedBean().getSupplierItemZonalTierPromotionPrices() == null) {
			this.getManagedBean().setSupplierItemZonalTierPromotionPrices(new ArrayList<ScSupplierItemPrice>());
		}

		ScSupplierItemPrice scSupplierItemPrice = new ScSupplierItemPrice();
		ScRequestSupplierItem requestSupplierItem = this.getManagedBean().getScRequestSupplierItem();
		ScSupplierItemPrice prevScSupplierItemPrice = null;
		if(this.getManagedBean().getSupplierItemZonalTierPromotionPrices().size() > 0) {
			prevScSupplierItemPrice = this.getManagedBean().getSupplierItemZonalTierPromotionPrices().get(this.getManagedBean().getSupplierItemZonalTierPromotionPrices().size() - 1);
		}
		if(prevScSupplierItemPrice == null){
			scSupplierItemPrice.setFromQty(BigDecimal.valueOf(1));
		}
		if(prevScSupplierItemPrice != null){
			if (prevScSupplierItemPrice.getToQty() == null) {
				throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
						"lbl.sc.to.qty", ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
			}

			scSupplierItemPrice.setFromQty(prevScSupplierItemPrice.getToQty().add(BigDecimal.valueOf(1)));
		}
		
		scSupplierItemPrice.setScRequestSupplierItem(requestSupplierItem);
		scSupplierItemPrice.setPricing(null);
		scSupplierItemPrice.setTplSpecZone(new TplSpecZoneDtl());
		scSupplierItemPrice.setPriceType(getParameterByTypeAndCode(ParameterTypeConst.PRICE_TYPE,
				ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER));

		this.getManagedBean().getSupplierItemZonalTierPromotionPrices().add(scSupplierItemPrice);

	}

	public void selectSingleRowZonalTierPromotionPrice(AjaxBehaviorEvent event) {
		boolean isSelectedALL = Boolean.TRUE;
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		if (flag) {
			setFlagZonalTierPromotion(Boolean.FALSE);
		}

		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalTierPromotionPrices();

		if (scSupplierItemPrices != null) {
			for (ScSupplierItemPrice scSupplierItemPrice : scSupplierItemPrices) {
				if (!scSupplierItemPrice.isSelected()) {
					isSelectedALL = Boolean.FALSE;
					break;
				}
			}

			if (isSelectedALL) {
				setFlagZonalTierPromotion(isSelectedALL);
			}
			this.getManagedBean().setEnableZonalTierPromoDel(true);
		}
		
		if (scSupplierItemPrices.size() == 0) {
			this.getManagedBean().setEnableZonalTierPromoDel(false);
		}

		this.getManagedBean().setPricingTabIndex(3);
		this.getManagedBean().setZonalTierPricingIndex(1);
	}

	public void selectAllZonalTierPromotionPrice(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalTierPromotionPrices();

		if (scSupplierItemPrices != null) {
			for (ScSupplierItemPrice scSupplierItemPrice : scSupplierItemPrices) {
				if (flag) {
					scSupplierItemPrice.setSelected(true);
				} else {
					scSupplierItemPrice.setSelected(false);
				}
			}
			this.getManagedBean().setEnableZonalTierPromoDel(true);
		}
		if (scSupplierItemPrices.size() == 0) {
			this.getManagedBean().setEnableZonalTierPromoDel(false);
		}

		this.getManagedBean().setPricingTabIndex(3);
		this.getManagedBean().setZonalTierPricingIndex(1);
	}

	public void deleteSupplierItemZonalTierPromotionPrices() {
		log.info("deleteZonalTierStandard");

		int counter = 0;
		List<ScSupplierItemPrice> deleteScSupplierItemPrices = new ArrayList<ScSupplierItemPrice>();
		List<ScSupplierItemPrice> mainScSupplierItemPrices = new ArrayList<ScSupplierItemPrice>();
		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalTierPromotionPrices();

		if (scSupplierItemPrices != null && !scSupplierItemPrices.isEmpty()) {
			for (ScSupplierItemPrice scSupplierItemPriceDtl : scSupplierItemPrices) {
				if (scSupplierItemPriceDtl.isSelected()) {

					scSupplierItemPriceDtl.setDeleted(Boolean.TRUE);

					if (scSupplierItemPriceDtl.getSupplierItemPriceId() != null) {
						deleteScSupplierItemPrices.add(scSupplierItemPriceDtl);
					}

					++counter;
				} else {
					mainScSupplierItemPrices.add(scSupplierItemPriceDtl);
				}
			}

			if (counter == 0) {
				throw new EpException(MessageConst.ERROR_760158);
			}
		}

		this.getManagedBean().setDeleteSupplierItemZonalTierPromotionPrices(deleteScSupplierItemPrices);
		deleteScSupplierItemPrices();
		this.getManagedBean().setSupplierItemZonalTierPromotionPrices(mainScSupplierItemPrices);
		
		if (this.getManagedBean().getSupplierItemZonalTierPromotionPrices().size() == 0){
			this.getManagedBean().setEnableZonalTierPromoDel(false);
		}

		this.getManagedBean().setPricingTabIndex(3);
		this.getManagedBean().setZonalTierPricingIndex(1);

		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setNavToPreviousPage(fc.getViewRoot().getViewId());

		fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, PURCHASE_INQUIRY_PAGE);
	}
	// ZonalTierPromotionPrice -- end
	
	// ZonalStandardPrice -- start
	public void addSupplierItemZonalStandardPrices() {		
		if(getManagedBean().getSupplierItemZonalStandardPrices() == null){
			this.getManagedBean().setSupplierItemZonalStandardPrices(new ArrayList<ScSupplierItemPrice>());
		}
		
		ScSupplierItemPrice scSupplierItemPrice = new ScSupplierItemPrice();
		ScRequestSupplierItem requestSupplierItem = this.getManagedBean().getScRequestSupplierItem();
		
		scSupplierItemPrice.setScRequestSupplierItem(requestSupplierItem);
		scSupplierItemPrice.setPricing(null);
		scSupplierItemPrice.setTplSpecZone(new TplSpecZoneDtl());
		scSupplierItemPrice.setPriceType(getParameterByTypeAndCode(
				ParameterTypeConst.PRICE_TYPE, ParameterConst.PRICE_TYPE_ZONAL_STANDARD));
		
		this.getManagedBean().getSupplierItemZonalStandardPrices().add(scSupplierItemPrice); 
		
	}
	
	public void selectSingleRowZonalStandardPrice(AjaxBehaviorEvent event) {
		boolean isSelectedALL= Boolean.TRUE;
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		if (flag) {
			setFlagZonalStandard(Boolean.FALSE);
		} 

		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalStandardPrices();
		
		if (scSupplierItemPrices != null) {
			for (ScSupplierItemPrice scSupplierItemPrice : scSupplierItemPrices) {
				if (!scSupplierItemPrice.isSelected()) {
					isSelectedALL= Boolean.FALSE;
					break;
				} 
			}
			
			if(isSelectedALL){
				setFlagZonalStandard(isSelectedALL);
			}
			this.getManagedBean().setEnableZonalNormalDel(true);
		}
		if (scSupplierItemPrices.size() == 0) {
			this.getManagedBean().setEnableZonalNormalDel(false);
		}
		
		this.getManagedBean().setPricingTabIndex(2); 
		this.getManagedBean().setZonalPricingIndex(0);
	}
	
	public void selectAllZonalStandardPrice(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalStandardPrices();
		
		if (scSupplierItemPrices != null) {
			for (ScSupplierItemPrice scSupplierItemPrice: scSupplierItemPrices) {
				if (flag) {
					scSupplierItemPrice.setSelected(true);
				} else {
					scSupplierItemPrice.setSelected(false);
				}
			}
			this.getManagedBean().setEnableZonalNormalDel(true);
		}
		if (scSupplierItemPrices.size() == 0) {
			this.getManagedBean().setEnableZonalNormalDel(false);
		}

		this.getManagedBean().setPricingTabIndex(2); 
		this.getManagedBean().setZonalPricingIndex(0);
	}
	
	public void deleteSupplierItemZonalStandardPrices() {
		log.info("deleteZonalStandard");
		
		int counter = 0;
		List<ScSupplierItemPrice> deleteScSupplierItemPrices = new ArrayList<ScSupplierItemPrice>();
		List<ScSupplierItemPrice> mainScSupplierItemPrices = new ArrayList<ScSupplierItemPrice>();
		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalStandardPrices();
		
		if (scSupplierItemPrices != null && !scSupplierItemPrices.isEmpty()) {
			for (ScSupplierItemPrice scSupplierItemPriceDtl : scSupplierItemPrices) {
				if (scSupplierItemPriceDtl.isSelected()) {
					
					scSupplierItemPriceDtl.setDeleted(Boolean.TRUE);
					
					if(scSupplierItemPriceDtl.getSupplierItemPriceId() != null){
						deleteScSupplierItemPrices.add(scSupplierItemPriceDtl);
					}
					
					++ counter;
				} else{
					mainScSupplierItemPrices.add(scSupplierItemPriceDtl);
				}
			}
			
			if(counter == 0){
				throw new EpException(MessageConst.ERROR_760158);
			}
		}
		
		this.getManagedBean().setDeleteSupplierItemZonalStandardPrices(deleteScSupplierItemPrices);
		deleteScSupplierItemPrices();
		this.getManagedBean().setSupplierItemZonalStandardPrices(mainScSupplierItemPrices);
		
		if (this.getManagedBean().getSupplierItemZonalStandardPrices().size() == 0){
			this.getManagedBean().setEnableZonalNormalDel(false);
		}
		
		this.getManagedBean().setPricingTabIndex(2); 
		this.getManagedBean().setZonalPricingIndex(0);	
		
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setNavToPreviousPage(fc.getViewRoot().getViewId());

		fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, PURCHASE_INQUIRY_PAGE);
	}
	// ZonalStandardPrice -- end
	
	// ZonalPromotionPrice -- start
	public void addSupplierItemZonalPromotionPrices() {		
		if(getManagedBean().getSupplierItemZonalPromotionPrices() == null){
			this.getManagedBean().setSupplierItemZonalPromotionPrices(new ArrayList<ScSupplierItemPrice>());
		}
		
		ScSupplierItemPrice scSupplierItemPrice = new ScSupplierItemPrice();
		ScRequestSupplierItem requestSupplierItem = this.getManagedBean().getScRequestSupplierItem();
		
		scSupplierItemPrice.setScRequestSupplierItem(requestSupplierItem);
		scSupplierItemPrice.setPricing(null);
		scSupplierItemPrice.setTplSpecZone(new TplSpecZoneDtl());
		scSupplierItemPrice.setPriceType(getParameterByTypeAndCode(
				ParameterTypeConst.PRICE_TYPE, ParameterConst.PRICE_TYPE_ZONAL_PROMOTION));
		
		this.getManagedBean().getSupplierItemZonalPromotionPrices().add(scSupplierItemPrice); 
		
	}
	
	public void selectSingleRowZonalPromotionPrice(AjaxBehaviorEvent event) {
		boolean isSelectedALL= Boolean.TRUE;
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		if (flag) {
			setFlagZonalPromotion(Boolean.FALSE);
		} 

		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalPromotionPrices();
		
		if (scSupplierItemPrices != null) {
			for (ScSupplierItemPrice scSupplierItemPrice : scSupplierItemPrices) {
				if (!scSupplierItemPrice.isSelected()) {
					isSelectedALL= Boolean.FALSE;
					break;
				} 
			}
			
			if(isSelectedALL){
				setFlagZonalPromotion(isSelectedALL);
			}
			this.getManagedBean().setEnableZonalPromoDel(true);
		}
		if (scSupplierItemPrices.size() == 0) {
			this.getManagedBean().setEnableZonalPromoDel(false);
		}
		
		this.getManagedBean().setPricingTabIndex(2); 
		this.getManagedBean().setZonalPricingIndex(1);
	}
	
	public void selectAllZonalPromotionPrice(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalPromotionPrices();
		
		if (scSupplierItemPrices != null) {
			for (ScSupplierItemPrice scSupplierItemPrice: scSupplierItemPrices) {
				if (flag) {
					scSupplierItemPrice.setSelected(true);
				} else {
					scSupplierItemPrice.setSelected(false);
				}
			}
			this.getManagedBean().setEnableZonalPromoDel(true);
		}
		if (scSupplierItemPrices.size() == 0) {
			this.getManagedBean().setEnableZonalPromoDel(false);
		}

		this.getManagedBean().setPricingTabIndex(2); 
		this.getManagedBean().setZonalPricingIndex(1);
	}
	
	public void deleteSupplierItemZonalPromotionPrices() {
		log.info("deleteZonalPromotion");
		
		int counter = 0;
		List<ScSupplierItemPrice> deleteScSupplierItemPrices = new ArrayList<ScSupplierItemPrice>();
		List<ScSupplierItemPrice> mainScSupplierItemPrices = new ArrayList<ScSupplierItemPrice>();
		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemZonalPromotionPrices();
		
		if (scSupplierItemPrices != null && !scSupplierItemPrices.isEmpty()) {
			for (ScSupplierItemPrice scSupplierItemPriceDtl : scSupplierItemPrices) {
				if (scSupplierItemPriceDtl.isSelected()) {
					
					scSupplierItemPriceDtl.setDeleted(Boolean.TRUE);
					
					if(scSupplierItemPriceDtl.getSupplierItemPriceId() != null){
						deleteScSupplierItemPrices.add(scSupplierItemPriceDtl);
					}
					
					++ counter;
				} else{
					mainScSupplierItemPrices.add(scSupplierItemPriceDtl);
				}
			}
			
			if(counter == 0){
				throw new EpException(MessageConst.ERROR_760158);
			}
		}
		
		this.getManagedBean().setDeleteSupplierItemZonalPromotionPrices(deleteScSupplierItemPrices);
		deleteScSupplierItemPrices();
		this.getManagedBean().setSupplierItemZonalPromotionPrices(mainScSupplierItemPrices);
		
		if (this.getManagedBean().getSupplierItemZonalPromotionPrices().size() == 0){
			this.getManagedBean().setEnableZonalPromoDel(false);
		}
		
		this.getManagedBean().setPricingTabIndex(2); 
		this.getManagedBean().setZonalPricingIndex(1);	
		
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setNavToPreviousPage(fc.getViewRoot().getViewId());

		fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, PURCHASE_INQUIRY_PAGE);
	}
	// ZonalPromotionPrice -- end

	// TierStandardPrice -- start	
	public void addSupplierItemTierStandardPrices() {
		FacesScopes facesScopes = new FacesScopes();
		
		if(getManagedBean().getSupplierItemTierStandardPrices() == null){
			this.getManagedBean().setSupplierItemTierStandardPrices(new ArrayList<ScSupplierItemPrice>());
		}
		
		ScSupplierItemPrice scSupplierItemPrice = new ScSupplierItemPrice();
		ScRequestSupplierItem requestSupplierItem = this.getManagedBean().getScRequestSupplierItem();
		ScSupplierItemPrice prevScSupplierItemPrice = null;
		if(this.getManagedBean().getSupplierItemTierStandardPrices().size() > 0) {
			prevScSupplierItemPrice = this.getManagedBean().getSupplierItemTierStandardPrices().get(this.getManagedBean().getSupplierItemTierStandardPrices().size() - 1);
		}
		if(prevScSupplierItemPrice == null){
			scSupplierItemPrice.setFromQty(BigDecimal.valueOf(1));
		}
		if(prevScSupplierItemPrice != null){
			//BigDecimal range = prevScSupplierItemPrice.getToQty().subtract(prevScSupplierItemPrice.getFromQty());
			if (prevScSupplierItemPrice.getToQty() == null) {
				throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
						"lbl.sc.to.qty", ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
			}

			scSupplierItemPrice.setFromQty(prevScSupplierItemPrice.getToQty().add(BigDecimal.valueOf(1)));
			//scSupplierItemPrice.setToQty(scSupplierItemPrice.getFromQty().add(range));
		}
		
		scSupplierItemPrice.setScRequestSupplierItem(requestSupplierItem);
		scSupplierItemPrice.setPricing(null);
		scSupplierItemPrice.setPriceType(getParameterByTypeAndCode(
				ParameterTypeConst.PRICE_TYPE, ParameterConst.PRICE_TYPE_TIER));
		
		this.getManagedBean().getSupplierItemTierStandardPrices().add(scSupplierItemPrice); 
		
	}
	
	public void selectSingleRowTierStandardPrice(AjaxBehaviorEvent event) {
		boolean isSelectedALL= Boolean.TRUE;
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		if (flag) {
			setFlagTierStandard(Boolean.FALSE);
		} 

		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemTierStandardPrices();
		
		if (scSupplierItemPrices != null) {
			for (ScSupplierItemPrice scSupplierItemPrice : scSupplierItemPrices) {
				if (!scSupplierItemPrice.isSelected()) {
					isSelectedALL= Boolean.FALSE;
					break;
				} 
			}
			
			if(isSelectedALL){
				setFlagTierStandard(isSelectedALL);
			}
			this.getManagedBean().setEnableTierNormalDel(true);
		}
		if (scSupplierItemPrices.size() == 0){
			this.getManagedBean().setEnableTierNormalDel(false);
		}
		
		this.getManagedBean().setPricingTabIndex(1); 
		this.getManagedBean().setTierPricingIndex(0);
	}
	
	public void selectAllTierStandardPrice(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemTierStandardPrices();
		
		if (scSupplierItemPrices != null) {
			for (ScSupplierItemPrice scSupplierItemPrice: scSupplierItemPrices) {
				if (flag) {
					scSupplierItemPrice.setSelected(true);
				} else {
					scSupplierItemPrice.setSelected(false);
				}
			}
			this.getManagedBean().setEnableTierNormalDel(true);
		}
		if (scSupplierItemPrices.size() == 0){
			this.getManagedBean().setEnableTierNormalDel(false);
		}

		this.getManagedBean().setPricingTabIndex(1); 
		this.getManagedBean().setTierPricingIndex(0);
	}
	
	public void deleteSupplierItemTierStandardPrices() {
		log.info("deleteTierStandard");
		
		int counter = 0;
		List<ScSupplierItemPrice> deleteScSupplierItemPrices = new ArrayList<ScSupplierItemPrice>();
		List<ScSupplierItemPrice> mainScSupplierItemPrices = new ArrayList<ScSupplierItemPrice>();
		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemTierStandardPrices();
		
		if (scSupplierItemPrices != null && !scSupplierItemPrices.isEmpty()) {
			for (ScSupplierItemPrice scSupplierItemPriceDtl : scSupplierItemPrices) {
				if (scSupplierItemPriceDtl.isSelected()) {
					
					scSupplierItemPriceDtl.setDeleted(Boolean.TRUE);
					
					if(scSupplierItemPriceDtl.getSupplierItemPriceId() != null){
						deleteScSupplierItemPrices.add(scSupplierItemPriceDtl);
					}
					
					++ counter;
				} else{
					mainScSupplierItemPrices.add(scSupplierItemPriceDtl);
				}
			}
			
			if(counter == 0){
				throw new EpException(MessageConst.ERROR_760158);
			}
		}
		
		this.getManagedBean().setDeleteSupplierItemTierStandardPrices(deleteScSupplierItemPrices);
		deleteScSupplierItemPrices();
		this.getManagedBean().setSupplierItemTierStandardPrices(mainScSupplierItemPrices);
		
		if (this.getManagedBean().getSupplierItemTierStandardPrices().size() == 0){
			this.getManagedBean().setEnableTierNormalDel(false);
		}
		
		this.getManagedBean().setPricingTabIndex(1); 
		this.getManagedBean().setTierPricingIndex(0);	
		
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setNavToPreviousPage(fc.getViewRoot().getViewId());

		fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, PURCHASE_INQUIRY_PAGE);
	}
	// TierStandardPrice -- end

	// TierPromotionPrice -- start
	public void addSupplierItemTierPromotionPrices() {
		FacesScopes facesScopes = new FacesScopes();

		if (getManagedBean().getSupplierItemTierPromotionPrices() == null) {
			this.getManagedBean().setSupplierItemTierPromotionPrices(new ArrayList<ScSupplierItemPrice>());
		}

		ScSupplierItemPrice scSupplierItemPrice = new ScSupplierItemPrice();
		ScRequestSupplierItem requestSupplierItem = this.getManagedBean().getScRequestSupplierItem();
		ScSupplierItemPrice prevScSupplierItemPrice = null;
		if(this.getManagedBean().getSupplierItemTierPromotionPrices().size() > 0) {
			prevScSupplierItemPrice = this.getManagedBean().getSupplierItemTierPromotionPrices().get(this.getManagedBean().getSupplierItemTierPromotionPrices().size() - 1);
		}
		if(prevScSupplierItemPrice == null){
			scSupplierItemPrice.setFromQty(BigDecimal.valueOf(1));
		}
		if(prevScSupplierItemPrice != null){
			if (prevScSupplierItemPrice.getToQty() == null) {
				throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
						"lbl.sc.to.qty", ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()) });
			}

			scSupplierItemPrice.setFromQty(prevScSupplierItemPrice.getToQty().add(BigDecimal.valueOf(1)));
		}
		
		scSupplierItemPrice.setScRequestSupplierItem(requestSupplierItem);
		scSupplierItemPrice.setPricing(null);
		scSupplierItemPrice.setPriceType(getParameterByTypeAndCode(ParameterTypeConst.PRICE_TYPE,
				ParameterConst.PRICE_TYPE_PROMOTION_TIER));

		this.getManagedBean().getSupplierItemTierPromotionPrices().add(scSupplierItemPrice);

	}

	public void selectSingleRowTierPromotionPrice(AjaxBehaviorEvent event) {
		boolean isSelectedALL = Boolean.TRUE;
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		if (flag) {
			setFlagTierPromotion(Boolean.FALSE);
		}

		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemTierPromotionPrices();

		if (scSupplierItemPrices != null) {
			for (ScSupplierItemPrice scSupplierItemPrice : scSupplierItemPrices) {
				if (!scSupplierItemPrice.isSelected()) {
					isSelectedALL = Boolean.FALSE;
					break;
				}
			}

			if (isSelectedALL) {
				setFlagTierPromotion(isSelectedALL);
			}
			this.getManagedBean().setEnableTierPromoDel(true);
		}
		
		if (scSupplierItemPrices.size() == 0){
			this.getManagedBean().setEnableTierPromoDel(false);
		}

		this.getManagedBean().setPricingTabIndex(1);
		this.getManagedBean().setTierPricingIndex(1);
	}

	public void selectAllTierPromotionPrice(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemTierPromotionPrices();

		if (scSupplierItemPrices != null) {
			for (ScSupplierItemPrice scSupplierItemPrice : scSupplierItemPrices) {
				if (flag) {
					scSupplierItemPrice.setSelected(true);
				} else {
					scSupplierItemPrice.setSelected(false);
				}
			}
			this.getManagedBean().setEnableTierPromoDel(true);
		}
		if (scSupplierItemPrices.size() == 0){
			this.getManagedBean().setEnableTierPromoDel(false);
		}

		this.getManagedBean().setPricingTabIndex(1);
		this.getManagedBean().setTierPricingIndex(1);
	}

	public void deleteSupplierItemTierPromotionPrices() {
		log.info("deleteTierPromotion");

		int counter = 0;
		List<ScSupplierItemPrice> deleteScSupplierItemPrices = new ArrayList<ScSupplierItemPrice>();
		List<ScSupplierItemPrice> mainScSupplierItemPrices = new ArrayList<ScSupplierItemPrice>();
		List<ScSupplierItemPrice> scSupplierItemPrices = this.getManagedBean().getSupplierItemTierPromotionPrices();

		if (scSupplierItemPrices != null && !scSupplierItemPrices.isEmpty()) {
			for (ScSupplierItemPrice scSupplierItemPriceDtl : scSupplierItemPrices) {
				if (scSupplierItemPriceDtl.isSelected()) {

					scSupplierItemPriceDtl.setDeleted(Boolean.TRUE);

					if (scSupplierItemPriceDtl.getSupplierItemPriceId() != null) {
						deleteScSupplierItemPrices.add(scSupplierItemPriceDtl);
					}

					++counter;
				} else {
					mainScSupplierItemPrices.add(scSupplierItemPriceDtl);
				}
			}

			if (counter == 0) {
				throw new EpException(MessageConst.ERROR_760158);
			}
		}

		this.getManagedBean().setDeleteSupplierItemTierPromotionPrices(deleteScSupplierItemPrices);
		deleteScSupplierItemPrices();
		this.getManagedBean().setSupplierItemTierPromotionPrices(mainScSupplierItemPrices);
		
		if (this.getManagedBean().getSupplierItemTierPromotionPrices().size() == 0){
			this.getManagedBean().setEnableTierPromoDel(false);
		}

		this.getManagedBean().setPricingTabIndex(1);
		this.getManagedBean().setTierPricingIndex(1);

		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setNavToPreviousPage(fc.getViewRoot().getViewId());

		fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, PURCHASE_INQUIRY_PAGE);
	}
	// TierPromotionPrice -- end

	public boolean isFlagTierStandard() {
		return flagTierStandard;
	}

	public void setFlagTierStandard(boolean flagTierStandard) {
		this.flagTierStandard = flagTierStandard;
	}

	public boolean isFlagTierPromotion() {
		return flagTierPromotion;
	}

	public void setFlagTierPromotion(boolean flagTierPromotion) {
		this.flagTierPromotion = flagTierPromotion;
	}

	public boolean isFlagZonalStandard() {
		return flagZonalStandard;
	}

	public void setFlagZonalStandard(boolean flagZonalStandard) {
		this.flagZonalStandard = flagZonalStandard;
	}

	public boolean isFlagZonalPromotion() {
		return flagZonalPromotion;
	}

	public void setFlagZonalPromotion(boolean flagZonalPromotion) {
		this.flagZonalPromotion = flagZonalPromotion;
	}

	public boolean isFlagZonalTierStandard() {
		return flagZonalTierStandard;
	}

	public void setFlagZonalTierStandard(boolean flagZonalTierStandard) {
		this.flagZonalTierStandard = flagZonalTierStandard;
	}

	public boolean isFlagZonalTierPromotion() {
		return flagZonalTierPromotion;
	}

	public void setFlagZonalTierPromotion(boolean flagZonalTierPromotion) {
		this.flagZonalTierPromotion = flagZonalTierPromotion;
	}
	// End - #29557
	
	
}