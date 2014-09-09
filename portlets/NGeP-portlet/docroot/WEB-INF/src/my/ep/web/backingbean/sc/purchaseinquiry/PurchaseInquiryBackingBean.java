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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.catalogue.IViewSupplierItemSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.sc.directpurchase.IPrInquirySO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.directpurchase.PrInquiryReqDTO;
import my.ep.dto.sc.directpurchase.PrInquiryResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierZone;
import my.ep.persistence.entity.cm.catalog.CmSupplierZoneDTL;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScPIZone;
import my.ep.persistence.entity.sc.directpurchase.ScPRIRemark;
import my.ep.persistence.entity.sc.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.backingbean.sc.directpurchase.RequestNoteBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.sc.purchaseinquiry.PurchaseInquiryManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
/**
 * 
 * @author sri
 * since 01.15.2014
 */
@ManagedBean(name = "purchaseInquiryBackingBean")
@RequestScoped
public class PurchaseInquiryBackingBean extends SourcingBaseBackingBean<PurchaseInquiryManagedBean> {
	private static Logger log = Logger.getLogger(PurchaseInquiryBackingBean.class);
    public PurchaseInquiryBackingBean() {
		super();
		if (MDC.get(LogKey.USER_ID) == null) {
			PortletRequest request = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
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
	  * pricingDetails for getting pricing details in Purchase Inquiry
	 * @return String
	  */
	  public String prInquiryAction(CmSupplierItem  cmSupplierItem, List<CmSupplierItem> compareSupplierItems){
		  debugLogger("prInquiryAction");
		  ScRequestSupplierItem requestSupplierItem = getManagedBean().getScRequestSupplierItem();
		  ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, ICatalogueSO.class);
		  IPrInquirySO prInquirySO = (IPrInquirySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
		  IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		  ParameterReqDTO parameterReqDTO = new ParameterReqDTO();

		  CatalogueReqDTO catReqDTO = new CatalogueReqDTO();
		  catReqDTO.setSupplierItemId(cmSupplierItem.getSupplierItemId());

		  ScPrInquiry scPrInquiry = new ScPrInquiry(); 
		  ScRequestItem scRequestItem = new ScRequestItem();
		  if(cmSupplierItem != null && cmSupplierItem.getCmPricings().size() > 0) {
			  ScSupplierItemPrice supplierItemPrice = new ScSupplierItemPrice();
			  // TODO Check active CmPricings
			  CatalogueResDTO catResDTO = catalogueSO.findActivePricing(catReqDTO);
			  List<CmPricing> pricings = catResDTO.getPriceLs();
			  cmSupplierItem.setCmPricings(pricings);
			  this.getManagedBean().setSupplierItem(cmSupplierItem);
			  
			  if(cmSupplierItem.getCmPricings() != null && cmSupplierItem.getCmPricings() .size() > 0){
				  supplierItemPrice.setPricing(cmSupplierItem.getCmPricings().get(0));
				  // price type
				  parameterReqDTO = new ParameterReqDTO();
				  parameterReqDTO.setParameterType(ParameterTypeConst.PRICE_TYPE);
				  parameterReqDTO.setParameterCode(cmSupplierItem.getCmPricings().get(0).getPricingType());
				  ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
				  supplierItemPrice.setPriceType(resDTO.getPmParameter());

			  } else {
				  throw new EpException("Price not found");
			  }
			  
			  requestSupplierItem.setSmSupplier(cmSupplierItem.getSmSupplier());
			  supplierItemPrice.setScRequestSupplierItem(requestSupplierItem);
			  requestSupplierItem.setSupplierItemPrice(supplierItemPrice);
			  requestSupplierItem.setRequestNoteItem(scRequestItem);
		  }
		  scPrInquiry.setWorkflowStatuses(null);

		  scPrInquiry.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		  scPrInquiry.setDayToResponse(ParameterConst.PIQ_RESP_DAYS); // by default days
		  scPrInquiry.setDateToResponse(calcDayToResponse(scPrInquiry));
		  scPrInquiry.setPrInquiryNo(prInquirySO.generatePrInquiryNo());
		  // Initializing Remarks 
		  scPrInquiry.setRemarks(initializeRemarks(scPrInquiry));
		  // TODO check supplier is government seller or not 
		  ScRequestNoteDtl requestNoteDtl =  getManagedBean().getRequestNoteDtl();
		  PmParameter pmParameter = new PmParameter();
		  requestNoteDtl.setJustificationType(pmParameter);
		  
		  // TODO check supplier is government seller or not 
		  if(cmSupplierItem.getSmSupplier().getPmOrgProfile() != null &&
				  cmSupplierItem.getSmSupplier().getPmOrgProfile().isGovtSeller()){
			  pmParameter.setParameterCode(ParameterConst.PROC_TYPE_G2G);
			  requestNoteDtl.setProcurementType(pmParameter);
			  getManagedBean().setProcurementTypes(getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE , Boolean.TRUE));

		  } else {
			  pmParameter.setParameterCode(ParameterConst.PROC_TYPE_NORMAL);
			  requestNoteDtl.setProcurementType(pmParameter);
			  getManagedBean().setProcurementTypes(getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE , Boolean.FALSE));
		  } 
		  
		  List<CmPricing>  cmPricings = new ArrayList<CmPricing>();
		  if (cmSupplierItem.getCmPricings() != null
				  && cmSupplierItem.getCmPricings().size() > 0) {
			  List<String> priceTypes = new ArrayList<String>();
			  for (CmPricing princing : cmSupplierItem.getCmPricings()) {
				  if (!priceTypes.contains(princing.getPricingType())) {
					  priceTypes.add(princing.getPricingType());
				  }
			  }
			  if(priceTypes != null && priceTypes.size() > 0){
				  for (CmPricing princing : cmSupplierItem.getCmPricings()) {
						  cmPricings.add(princing);
				  }
				  requestSupplierItem.setSupplierItemPrices(createScSupplierItemPrices(requestSupplierItem, cmPricings ,Boolean.FALSE));
			  }
			  // TODO PriceType drop down list in AddToCart
			  getManagedBean().setPriceTypes(getPriceTypeDropdownValues(ParameterTypeConst.PRICE_TYPE, priceTypes));
		  }
		  
			List<Long> cmSupplieritemIds = new ArrayList<Long>();
			  if(compareSupplierItems != null){
		    		for (CmSupplierItem supplierItem : compareSupplierItems) {
		    			if(supplierItem.getCmItem().getItemId().equals(cmSupplierItem.getCmItem().getItemId())){
		    				cmSupplieritemIds.add(supplierItem.getSupplierItemId());	
		    			}
		    		}
		    	}
			  // prepare  requestSupplierItems , request item and supplier item price 
			  IViewSupplierItemSO viewSupplierItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, IViewSupplierItemSO.class);
			  CatalogueReqDTO catalogueReqDTO = new CatalogueReqDTO();
			  ISearchBean<VCmSupplierItem>  searchBean  = new CatalogueBackingBean().getManagedBean().getSearchDataModel().getSearchBean();
			  searchBean.getDataFilterBeans().clear();
			  catalogueReqDTO.setViewSearchBean(searchBean);
			  catalogueReqDTO.setSupplierItemId(cmSupplierItem.getSupplierItemId());
			  catalogueReqDTO.setExtensionCode(cmSupplierItem.getCmItem().getExtensionCode());
			  CatalogueResDTO catalogueResDTO = viewSupplierItemSO.getSuppliersItemPricingByExtensionCode(catalogueReqDTO);
			  ScRequestItem requestItem = createRequestItem(cmSupplierItem);
			  List<ScRequestSupplierItem> supplierItems = new ArrayList<ScRequestSupplierItem>();
			  for (VCmSupplierItem vCmSupplierItem : catalogueResDTO.getViewSuppItemResultBean().getResultList()) {
				  if (vCmSupplierItem.getCmPricings() != null && vCmSupplierItem.getCmPricings().size() > 0) {
					  CmSupplierItem supplierItem = vCmSupplierItem.getCmPricings().get(0).getCmSupplierItem();

					  if(cmSupplieritemIds.contains(supplierItem.getSupplierItemId())){

						  if(supplierItem.getCmItem().getItemId().equals(cmSupplierItem.getCmItem().getItemId())){

							  ScRequestSupplierItem scRequestSupplierItem = createScRequestSupplierItem(
									  requestItem, supplierItem.getSmSupplier(), vCmSupplierItem.getCmPricings());
							  supplierItems.add(scRequestSupplierItem);	
						  }
					  }
				  }
			  }
		  // Add selected supplier item.
		  supplierItems.add(requestSupplierItem);	
		  requestSupplierItem.setScPrInquiy(scPrInquiry);
		  requestItem.setSupplierItems(supplierItems);
		  scPrInquiry.setRequestItem(requestItem);
		  getManagedBean().setScPrInquiry(scPrInquiry);
		  
		  requestSupplierItem.setRequestNoteItem(requestItem);
		  getManagedBean().setScRequestSupplierItem(requestSupplierItem);
		
		  requestNoteDtl.setRequestItem(requestItem);
		  requestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
		  getManagedBean().setRequestNoteDtl(requestNoteDtl);
		  // pr inquiry status
		  getCurrentStatus(DPStatusConstants.PRI_PENDING_SUBMISSION);
		  
		  //TODO default value to  price type drop down listing
		  getManagedBean().setPriceType(ParameterConst.PRICE_TYPE_STANDARD);
		  priceTypeChangeListener();
		  
			// to get ptj
			PmUser user = new FacesScopes().getUserSessionBean();
			if (user != null) {
				OrgProfileReqDTO profileReqDTO = new OrgProfileReqDTO();
				profileReqDTO.setUserId(user.getUserId());
				profileReqDTO.setOrgType(OrganizationType.DIVISION.value());
				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
				OrgProfileResDTO orgProfileResDTO = orgProfileSO
						.retrieveOrgProfileListByOrgType(profileReqDTO);
				if(orgProfileResDTO.getOrgProfileList() != null && orgProfileResDTO.getOrgProfileList().size()>0){
					getManagedBean().setPmOrgProfile(orgProfileResDTO.getOrgProfileList().get(0));
				}
			}
		  return "prInquiryMain";		 
	  }

	  private ScRequestSupplierItem createScRequestSupplierItem(ScRequestItem requestItem, SmSupplier smSupplier, List<CmPricing> cmPricings) {
		  ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();

		  requestSupplierItem.setRequestNoteItem(requestItem);
		  requestSupplierItem.setSmSupplier(smSupplier);

		  requestSupplierItem.setSupplierItemPrices(createScSupplierItemPrices(requestSupplierItem, cmPricings ,Boolean.TRUE));

		  return requestSupplierItem;
	  }

	  private ScRequestItem createRequestItem(CmSupplierItem cmSupplierItem){
		  IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			ScRequestItem scRequestItem	= new ScRequestItem();
			CmItem cmItem = cmSupplierItem.getCmItem();
			String ItemCode = cmItem.getExtensionCode();
			CharSequence charSeq = ItemCode.subSequence(0, 2);
			Integer itemCode = Integer.valueOf(charSeq.toString());
			reqDTO.setParameterType(ParameterTypeConst.SC_ITEM_TYPE);
			// TODO checking first two digits in itemCode  whether service or product
			if (itemCode >= 70 && itemCode <= 94) { // for service  between  70 and 94  
				reqDTO.setParameterCode(ParameterConst.ITEM_TYPE_SERVICE);
				ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
				scRequestItem.setItemType(resDTO.getPmParameter());
			} else  { // product
				reqDTO.setParameterCode(ParameterConst.ITEM_TYPE_PRODUCT);
				ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
				scRequestItem.setItemType(resDTO.getPmParameter());
			}
		  scRequestItem.setCmItem(cmItem);
		  scRequestItem.setItemName(cmItem.getItemName());
		  scRequestItem.setItemDesc(cmSupplierItem.getItemDesc());
		  scRequestItem.setMinqty(cmSupplierItem.getMinOrder());
		  scRequestItem.setUom(cmSupplierItem.getPmUom());
		  scRequestItem.setLeadTime(cmSupplierItem.getLeadTime());

		  return scRequestItem;
	  }

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

	public SelectItem[] getPriceTypeDropdownValues(String parameterType,
			List<String> priceTypes) {
		SelectItem[] items = null;
		try {
			items = new SelectItem[priceTypes.size() + 1];
			items[0] = new SelectItem(null, "- Select One -");
			int i = 1;
			for (String priceTypeCode : priceTypes) {
				items[i++] = new SelectItem(priceTypeCode, getParameterDesc(
						parameterType, priceTypeCode));
			}
		} finally{
		}
		return items;
	}
		
	/**
	 *  priceTypeChangeListener for getting Item Pricing list in Purchase inquiry
	 */
	public void priceTypeChangeListener() {
		debugLogger("priceTypeChangeListener");
		Map<ScSupplierItemPrice, ScSupplierItemPrice> itemPricesMap = new HashMap<ScSupplierItemPrice, ScSupplierItemPrice>();
		List<ScSupplierItemPrice> ItemPriceList = new ArrayList<ScSupplierItemPrice>();
		if(getManagedBean().getScRequestSupplierItem() != null && 
				getManagedBean().getScRequestSupplierItem().getSupplierItemPrices() != null && getManagedBean().getScRequestSupplierItem().getSupplierItemPrices().size() >0 ){
			ScRequestSupplierItem scRequestSupplierItem = new ScRequestSupplierItem();
			//List<ScSupplierItemPrice>	scSupplierItemPrices= new ArrayList<ScSupplierItemPrice>();
			List<ScSupplierItemPrice>	supplierItemPrices = getManagedBean().getScRequestSupplierItem().getSupplierItemPrices();
			if(supplierItemPrices != null && supplierItemPrices.size() >0){
				for (ScSupplierItemPrice scSupplierItemPrice : supplierItemPrices) {

					if(scSupplierItemPrice.getPriceType() != null){

						if(scSupplierItemPrice.getPriceType().getParameterCode().equals(getManagedBean().getPriceType())){
                            
							if(getManagedBean().getPriStatus().equalsIgnoreCase("Pending Supplier Response")){
								scSupplierItemPrice.setItemPrice(null);	
								//scSupplierItemPrices.add(scSupplierItemPrice);
								itemPricesMap.put(scSupplierItemPrice, scSupplierItemPrice);
							} else{
								//scSupplierItemPrices.add(scSupplierItemPrice);
								itemPricesMap.put(scSupplierItemPrice, scSupplierItemPrice);
								
							}
							
						}
					}
				}
				
				  ItemPriceList  = new ArrayList(itemPricesMap.keySet());
				 //getManagedBean().setSupplierItemPrices(ItemPriceList);
			}
			scRequestSupplierItem.setSupplierItemPrices(ItemPriceList);
			getManagedBean().getScPrInquiry().setRequestSupplierItem(scRequestSupplierItem);
		}
	}

	 /**
	  *  submitPrEnquiryDtl  method to submit purchase inquiry details  in Purchase inquiry
	  */	
	  public String submitPrInquiryDtl() {
		  debugLogger("submitPrInquiryDtl");
		    IPrInquirySO prInquirySO = null;
		    IDirectPurchaseSO directPurchaseSO = null;
		    ParameterResDTO resDTO = null;
		    try {
		    	IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		    	prInquirySO = (IPrInquirySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
		    	directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
		    	PrInquiryReqDTO reqDTO = new PrInquiryReqDTO();	
		    	ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		    	FacesScopes facesScopes = new FacesScopes();
		    	ScRequestSupplierItem scRequestSupplierItem   = getManagedBean().getScRequestSupplierItem();
		    	ScPrInquiry prInquiry = getManagedBean().getScPrInquiry();

		    	List<ScRequestNoteDtl> requestNoteDtls =  new  ArrayList<ScRequestNoteDtl>();
		    	ScRequestNoteDtl requestNoteDtl = getManagedBean().getRequestNoteDtl();
		    	ScRequestNote	requestNote	=	new ScRequestNote();
		    	requestNote.setRequestNoteNo(directPurchaseSO.generateRequestNoteNo());
		    	requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SC);
		    	setUserDetails(requestNote);
		    	requestNoteDtl.setScRequestNote(requestNote);
		    	requestNoteDtl.setAssignedUser(facesScopes.getUserSessionBean());
		    	
		    	// selected supplier item
		    	requestNoteDtl.setScRequestSupplierItem(scRequestSupplierItem);
		    	
		    	// proc type
		    	parameterReqDTO = new ParameterReqDTO();
		    	parameterReqDTO.setParameterType(ParameterTypeConst.PROCUREMENT_TYPE);
		    	parameterReqDTO.setParameterCode(requestNoteDtl.getProcurementType().getParameterCode());
		    	resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
			    requestNoteDtl.setProcurementType(resDTO.getPmParameter());
			    
			    // justification type
				parameterReqDTO = new ParameterReqDTO();
				if (requestNoteDtl.getProcurementType().getParameterCode()
						.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
					parameterReqDTO.setParameterType(ParameterTypeConst.JUSTIFICATION_TYPE_ADHOC);
					if(requestNoteDtl.getJustificationType().getParameterCode() != null){
						parameterReqDTO.setParameterCode(requestNoteDtl.getJustificationType().getParameterCode());
						resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
						requestNoteDtl.setJustificationType(resDTO.getPmParameter());
					}
				} else if (requestNoteDtl.getProcurementType().getParameterCode()
						.equalsIgnoreCase(ParameterConst.PROC_TYPE_EMERGENCY)) {
					parameterReqDTO.setParameterType(ParameterTypeConst.JUSTIFICATION_TYPE_EMERGENCY);
					if(requestNoteDtl.getJustificationType().getParameterCode() != null){
						parameterReqDTO.setParameterCode(requestNoteDtl.getJustificationType().getParameterCode());
						resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
						requestNoteDtl.setJustificationType(resDTO.getPmParameter());
					}
				} else {
					requestNoteDtl.setJustificationType(null);
				}
		    	
			    // price type
		    	if (getManagedBean().getPriceType() != null) {
		    		parameterReqDTO = new ParameterReqDTO();
		    		parameterReqDTO.setParameterType(ParameterTypeConst.PRICE_TYPE);
		    		parameterReqDTO.setParameterCode(getManagedBean().getPriceType());
		    		resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
		    	}
		    	requestNoteDtl.setPriceType(resDTO.getPmParameter());
		    	requestNoteDtls.add(requestNoteDtl);

		    	List<ScRequestSupplierItem> requestSupplierItems =  new  ArrayList<ScRequestSupplierItem>();
		    	// validate order quantity
		    	if(scRequestSupplierItem.getRequestNoteItem().getQuantity() !=null && scRequestSupplierItem.getRequestNoteItem().getQuantity().intValue() <0){
		    		throw new EpException(MessageConst.INFO_100033, new String[]{" Order Quatity" ,"0"});
		    	}
		    	 // Add selected supplier item.
		    	scRequestSupplierItem.setScRequestNoteDtl(requestNoteDtls);
		    	requestSupplierItems.add(scRequestSupplierItem);
		    	prInquiry.setScRequestSupplierItems(requestSupplierItems);
		    	
		    	prInquiry.setCreatedDate(DateUtils.getCurrentDate());
		    	// setting remarks 
		    	prInquiry.setRemarks(initializeRemarks(prInquiry));
		    	prInquiry.setWorkflowStatuses(null);

		    	if(prInquiry.getRemarks() != null ){
		    		for (int i = 0; i < prInquiry.getRemarks().size(); i++) {
		    			ScPRIRemark remark = prInquiry.getRemarks().get(i);
		    			if(StringUtils.isBlank(remark.getRemark())){
		    				prInquiry.getRemarks().remove(remark);
		    			}	
		    		} 
		    	}

		    	List<ScRequestItem>	requestItems	=	new ArrayList<ScRequestItem>();
		    	requestItems.add(prInquiry.getRequestItem());
		    	requestNote.setRequestNoteItems(requestItems);
		    	prInquiry.getRequestItem().setRequestNote(requestNote);
		    	
		    	requestNote.setRequestNoteDtls(requestNoteDtls);
		    	String token = facesScopes.getBPMAuthenticationCtxToken();
		    	reqDTO.setToken(token);
		    	reqDTO.setPrInquiryDetail(prInquiry);
		    	reqDTO.setOrgProfileId(getManagedBean().getPmOrgProfile().getOrgProfileId());
		    	reqDTO.setRequestNote(requestNote);
		    	PrInquiryResDTO prInquiryResDTO = prInquirySO.submitPrInquiry(reqDTO);
		    	if (prInquiryResDTO.isSuccessful()) {
		    		ScPrInquiry scPrInquiry = prInquiryResDTO.getPrInquiryDetail();
		    		successMessage(MessageConst.INFO_700026,ParameterTypeConst.DOCUMENT_TYPES,
		    				ParameterConst.DOCTYPE_PURCHASE_INQUIRY,scPrInquiry.getPrInquiryNo());
						// clear data
						super.renewSesionObject();
					
				 } else{
					 throw new EpException("Failed.");
				 }
			} finally {
				prInquirySO = null;
				directPurchaseSO = null;
			}
			return null;
		}
		
		 /**
		  *  submitPrInquiryPrice  method to submit purchase inquiry price  details by supplier  in Purchase inquiry
		  */	
	  public String submitPrInquiryPrice(String outcome){
		   IPrInquirySO prInquirySO = (IPrInquirySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
			   //call prInquirySO submitPrice method
			   PrInquiryReqDTO reqDTO = new PrInquiryReqDTO();
			   ScPrInquiry prInquiry = getManagedBean().getScPrInquiry();
				ScRequestSupplierItem scRequestSupplierItem   = getManagedBean().getScRequestSupplierItem();
			   //remarks 
			   prInquiry.setRemarks(initializeRemarks(prInquiry));
			   if(prInquiry.getRemarks() != null ){
				   for (int i = 0; i < prInquiry.getRemarks().size(); i++) {
					   ScPRIRemark remark = prInquiry.getRemarks().get(i);
					   if(StringUtils.isBlank(remark.getRemark())){
						   prInquiry.getRemarks().remove(remark);
					   }	
				   } 
			   }
			   
			   // updating price 
			   List<ScRequestSupplierItem>	scRequestSupplierItems = new ArrayList<ScRequestSupplierItem>();
			   scRequestSupplierItems.add(scRequestSupplierItem);
			   prInquiry.setScRequestSupplierItems(scRequestSupplierItems);
			   
			   prInquiry.setIsStockAvailable(getManagedBean().getStockAvailable());
			   reqDTO.setPrInquiryDetail(prInquiry);
			   FacesScopes facesScopes = new FacesScopes();
			   String token = facesScopes.getBPMAuthenticationCtxToken();
			   reqDTO.setToken(token);
			   reqDTO.setOutcome(outcome);
			   reqDTO.setTaskId(getManagedBean().getTaskId());
			   PrInquiryResDTO resDTO = prInquirySO.submitPrInquiryPrice(reqDTO);
			   if(resDTO.isSuccessful()){
				 ScPrInquiry scPrInquiry = resDTO.getPrInquiryDetail();
				 successMessage(MessageConst.INFO_700026,ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_PURCHASE_INQUIRY,scPrInquiry.getPrInquiryNo());
					// clear data
					super.renewSesionObject();
			   }else{
					throw new EpException("Failed.");
		   }
		return null;
	  }
	  
	/**
	 * Initializing remarks in Purchase inquiry
	 */
	public List<ScPRIRemark> initializeRemarks(ScPrInquiry prInquiry) {
		IStatusSO iStatusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		FacesScopes facesScopes = new FacesScopes();
		List<ScPRIRemark> results =   prInquiry.getRemarks();
		if(results == null){
			results = new ArrayList<ScPRIRemark>();
		} 
		if (results.size() == 0
				|| !compareUserName(results.get(0).getCreatedUser().getLoginId())) {
			ScPRIRemark remark = new ScPRIRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setCreatedUser(facesScopes.getUserSessionBean());
			if (getManagedBean().getScPrInquiry().getRemarks() != null
					&& getManagedBean().getScPrInquiry().getRemarks().size() > 0) {
				remark.setRemark(getManagedBean().getScPrInquiry().getRemarks()
						.get(0).getRemark());
			} 
			PmStatus status = new PmStatus();
			status.setStatusId(DPStatusConstants.PRI_PENDING_SUBMISSION);
		    StatusReqDTO statusReqDTO = new StatusReqDTO();
		    if (status != null) {
		     statusReqDTO.setStatusId(status.getStatusId());
		     StatusResDTO statusResDTO = iStatusSO.retrieveStatusByStatusId(statusReqDTO);
		     remark.setPmStatus(statusResDTO.getPmStatus());
		    }
		    remark.setPmStatus(status);
		    remark.setPrInquiry(prInquiry);
		    results.add(0, remark);
		}
		return results;
	}

	/**
	 * Event for procurement type drop down in Purchase inquiry
	 */
	public void procurementTypeEvent() {
		log.info("procurementTypeEvent");
		if (getManagedBean().getRequestNoteDtl().getProcurementType().getParameterCode()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL) || getManagedBean().getRequestNoteDtl().getProcurementType()
				.getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			getManagedBean().setProcTypeOption(Boolean.FALSE);
			getManagedBean().setJustTypeOption(Boolean.FALSE);

		} else if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
			getManagedBean()
					.setJustDropdown(
							getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_ADHOC));

		} else if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_EMERGENCY)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
			getManagedBean()
					.setJustDropdown(
							getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_EMERGENCY));
		}
		getManagedBean().getRequestNoteDtl().setJustificationType(new PmParameter());
	}
		
	/**
	 * Event for justification type drop down in Purchase inquiry
	 */
	public void justificationTypeEvent() {
		log.info("justificationTypeEvent");
		/*if (getManagedBean().getRequestNoteDtl().getJustificationType().getParameterCode()
				.equalsIgnoreCase(ParameterTypeConst.JUSTIFICATION_TYPE_OTHER)) {
			getManagedBean().setJustTypeOption(Boolean.TRUE);
		} else {
			getManagedBean().setJustTypeOption(Boolean.FALSE);
		}*/
	}
	
	public	String	requisitionerAction(String action){
		ScPrInquiry prInquiry = getManagedBean().getScPrInquiry();
		if(StringUtils.equalsIgnoreCase("PROCESSED",action)){
			//redirect to requestNote screen
			IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			
			// checking wheather stock available or not
			if(prInquiry.getIsStockAvailable().equals(ParameterConst.PI_STOCK_AVAILABILITY)){
				ScRequestNote requestNote = getManagedBean().getRequestNote();
				//get RequestNote Backing bean and manage bean and set Request Note to RequestNote manage bean
				PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				RequestNoteBackingBean requestNoteBackingBean = (RequestNoteBackingBean) portletSession.getAttribute("requestNoteBackingBean");
				if (requestNoteBackingBean == null) {
					requestNoteBackingBean = new RequestNoteBackingBean();
				}
				
				List<ScRequestNoteDtl>	requestNoteDtls = new ArrayList<ScRequestNoteDtl>();
				if( requestNote.getRequestNoteDtls() != null && !requestNote.getRequestNoteDtls().isEmpty()){
					for (ScRequestNoteDtl scRequestNoteDtl : requestNote.getRequestNoteDtls()) {
						parameterReqDTO = new ParameterReqDTO();
						parameterReqDTO.setParameterType(ParameterTypeConst.FULFILMENT_TYPE);
						parameterReqDTO.setParameterCode(ParameterConst.FULFILMENT_TYPE_ONE_OFF);
						ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
						scRequestNoteDtl.setFulfilmentType(parameterResDTO.getPmParameter());

						if(scRequestNoteDtl.getDeliverTermType() == null){
							parameterReqDTO = new ParameterReqDTO();
							parameterReqDTO.setParameterType(ParameterTypeConst.DELIVERY_TERM);
							parameterReqDTO.setParameterCode(ParameterConst.SQ_DURATION_DAY);
							parameterResDTO  = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
							scRequestNoteDtl.setDeliverTermType(parameterResDTO.getPmParameter());
						}
						
						// get PI id to Request note
						if(scRequestNoteDtl.getScRequestSupplierItem().getScPrInquiy() != null){
							requestNoteBackingBean.getManagedBean().setPrInquiryId(scRequestNoteDtl.getScRequestSupplierItem().getScPrInquiy().getPrInquiryId());
						}

						if(scRequestNoteDtl.getAssignedUser() == null){
							scRequestNoteDtl.setAssignedUser(new PmUser());
						}
						scRequestNoteDtl.getScRequestSupplierItem().setSupplierItemId(scRequestNoteDtl.getScRequestSupplierItem().getSmSupplier().getSupplierId());

						if (scRequestNoteDtl.getJustificationType() != null){
							requestNoteBackingBean.getManagedBean().setJustificationType(scRequestNoteDtl.getProcurementType().getParameterCode());
						}

						if (scRequestNoteDtl.getJustification() != null){
							requestNoteBackingBean.getManagedBean().setJustification(scRequestNoteDtl.getJustification());
						}

						requestNoteBackingBean.getManagedBean().setProcurementType(scRequestNoteDtl.getProcurementType().getParameterCode());
						requestNoteBackingBean.getManagedBean().setRequestSupplierItem(scRequestNoteDtl.getScRequestSupplierItem());
						requestNoteBackingBean.supplierLister(scRequestNoteDtl.getScRequestSupplierItem().getSmSupplier().getSupplierId(),
								scRequestNoteDtl.getPriceType().getParameterId(), scRequestNoteDtl, scRequestNoteDtl.getRequestItem().getSupplierItems() ,scRequestNoteDtl.getAssignedUser().getUserId() ,scRequestNoteDtl.getDeliverTermType().getParameterId());
						
						// fulfilmentTypeEvent
						requestNoteBackingBean.getManagedBean().setRequestNote(requestNote);
						requestNoteBackingBean.fulfilmentTypeEvent(scRequestNoteDtl);
						requestNoteBackingBean.getManagedBean().setRequestNoteDtl(scRequestNoteDtl);
						
						requestNoteDtls.add(scRequestNoteDtl);
					}
					requestNoteBackingBean.getManagedBean().setRequestSupplierItem(requestNoteDtls.get(0).getScRequestSupplierItem());
				}
				
				IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
				FacesScopes facesScopes = new FacesScopes();
				String token = facesScopes.getBPMAuthenticationCtxToken();
				BpmRequestDTO reqBPMDTO = new BpmRequestDTO();
				reqBPMDTO.setCtxToken(token);
				reqBPMDTO.setTaskId(getManagedBean().getTaskId());
				BpmResponseDTO res = bpmSO.getTaskDetails(reqBPMDTO);
				requestNoteBackingBean.getManagedBean().setOutComes(res.getAction());

				requestNote.setRemarks(requestNoteBackingBean.initializeRemarks(requestNote));
				requestNoteBackingBean.getManagedBean().setRequestNote(requestNote);
				requestNoteBackingBean.getManagedBean().setRequestNoteDtls(requestNoteDtls);
				requestNoteBackingBean.getManagedBean().setSourceMethod(ParameterConst.METHOD_SC);
				requestNoteBackingBean.getManagedBean().setTaskId(getManagedBean().getTaskId());
				// created by 
				requestNoteBackingBean.getManagedBean().setCreatedBy(requestNoteBackingBean.getCreatedBy(requestNote.getCreatedBy()).getUserName());
				
				requestNoteBackingBean.initialInformation(requestNote);
				requestNoteBackingBean.getCurrentStatus(DPStatusConstants.RN_PENDING_SUBMISSION);
				requestNoteBackingBean.getManagedBean().setFulfillmentType(ParameterConst.FULFILMENT_TYPE_ONE_OFF);
				
				// back to search catalouge button hide
				requestNoteBackingBean.getManagedBean().setPiRNChecker(Boolean.FALSE);
				//redirect to RequestNote screen
				return "commonRequestNote";
			} else {
				throw new EpException("Out of stock.");
			}
		} else {
			IPrInquirySO prInquirySO = (IPrInquirySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
			PrInquiryReqDTO reqDTO = new PrInquiryReqDTO();
			if(prInquiry.getRemarks() != null ){
				for (int i = 0; i < prInquiry.getRemarks().size(); i++) {
					ScPRIRemark remark = prInquiry.getRemarks().get(i);
					if(StringUtils.isBlank(remark.getRemark())){
						prInquiry.getRemarks().remove(remark);
					}	
				} 
			}

			reqDTO.setPrInquiryDetail(prInquiry);
			FacesScopes facesScopes = new FacesScopes();
			String token = facesScopes.getBPMAuthenticationCtxToken();
			reqDTO.setToken(token);
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			PrInquiryResDTO resDTO = prInquirySO.submitPrInquiryPrice(reqDTO);

			if(resDTO.isSuccessful()){
				 successMessage(MessageConst.INFO_700010,ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_PURCHASE_INQUIRY,getManagedBean().getScPrInquiry().getPrInquiryNo());
			} else{
				throw new EpException("Failed.");
			}
			return null;
		}
	}
	
	 /**
	 * render the view base on the task id
	 */
 public void preRenderView() {
	 HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
	 HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
	 IDirectPurchaseSO directPurchaseSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
	 RequestNoteReqDTO	requestDTO	=	new RequestNoteReqDTO();
	 String taskId = originalServletRequest.getParameter("taskId");
	 if (StringUtils.isNotBlank(taskId)) {
		 this.getManagedBean().setTaskId(taskId);
		 log.info("taskId : " + taskId);
		 IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		 FacesScopes facesScopes = new FacesScopes();
		 String token = facesScopes.getBPMAuthenticationCtxToken();
		 BpmRequestDTO reqDTO = new BpmRequestDTO();
		 reqDTO.setCtxToken(token);
		 reqDTO.setTaskId(taskId);
		 BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		 this.getManagedBean().setOutComes(res.getAction());
		 this.getManagedBean().setProcessName(res.getProcessName());
		 Object obj = res.getPojo();
		 SCDirectPurchaseDataType directPurchaseDataType = (SCDirectPurchaseDataType) obj;
		 //Purchase Inquiry
		 IPrInquirySO prInquirySO = (IPrInquirySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
		 PrInquiryReqDTO prInquiryReqDTO = new PrInquiryReqDTO();
		 Long prInquiryID = Long.valueOf(directPurchaseDataType.getDpId());
		 prInquiryReqDTO.setPrInquiryId(prInquiryID);
		 prInquiryReqDTO.setFetchSubEntity(true);
		 PrInquiryResDTO prInquiryResDTO = prInquirySO.getPrInquiry(prInquiryReqDTO);
		 ScPrInquiry  prInquiry = prInquiryResDTO.getPrInquiryDetail();
		 // remarks
		 prInquiry.setRemarks(initializeRemarks(prInquiry));
         boolean flag = Boolean.TRUE;
		 if(prInquiry.getScRequestSupplierItems() != null && prInquiry.getScRequestSupplierItems().size()>0 ){
			 // get selected supplier 
			 ScRequestSupplierItem requestSupplierItem = prInquiry.getScRequestSupplierItems().get(0);
			 ScRequestNoteDtl requestNoteDtl = requestSupplierItem.getScRequestNoteDtl().get(0);
			 
			 // lead time 
			  if(requestSupplierItem.getSupplierItemPrices() != null && requestSupplierItem.getSupplierItemPrices().size() >0){
				  requestSupplierItem.getRequestNoteItem().setLeadTime( requestSupplierItem.getSupplierItemPrices().get(0).getPricing().getCmSupplierItem().getLeadTime());
			  }
			  
			 // price type drop down
			 List<String> priceTypes= new ArrayList<String>();
			 priceTypes.add(requestNoteDtl.getPriceType().getParameterCode());
			 getManagedBean().setPriceType(requestNoteDtl.getPriceType().getParameterCode());
			 getManagedBean().setPriceTypes(getPriceTypeDropdownValues(ParameterTypeConst.PRICE_TYPE, priceTypes));
			
			 getManagedBean().setPmOrgProfile(requestNoteDtl.getScRequestNote().getPmOrgProfile());
			 getManagedBean().setRequestNoteDtl(requestNoteDtl);
			 getManagedBean().setScRequestSupplierItem(requestSupplierItem);
			 //RequestNote Details - checking workflow status exits or not for PI to RN
			 requestDTO.setRequestNoteId(requestNoteDtl.getScRequestNote().getRequestNoteId());
			 requestNoteDtl.getRequestItem().setSupplierItems(prInquiry.getScRequestSupplierItems());
			 RequestNoteResDTO	resDTO	=	directPurchaseSO.getRequestNote(requestDTO);
			 ScRequestNote	requestNote	=	resDTO.getRequestNoteDetail();
			 if(requestNote != null){
				 getManagedBean().setRequestNote(requestNote);
				 if(requestNote.getWorkFlowStatuses().size() > 0){
					 flag = Boolean.FALSE;
				 } 
			 }
	    }
		 
		// no of response days
		 prInquiry.setDayToResponse((int)DateUtils.getDaysDifference(prInquiry.getCreatedDate(), getCurrentDateWithoutTime(prInquiry.getDateToResponse())));
		 
		 getManagedBean().setTaskId(taskId);
		 getManagedBean().setProcessName(res.getProcessName());
		 getManagedBean().setOutComes(res.getAction());
		 getManagedBean().setStockAvailable(prInquiry.getIsStockAvailable());
		 getManagedBean().setScPrInquiry(prInquiry);
		 initializeRemarks(prInquiry);
		 getCurrentStatus(res.getDocStatus().longValue()); 
		 // title checker
		 getManagedBean().setTitleChecker(Boolean.FALSE);
		 getManagedBean().setRnToPIFlag(Boolean.FALSE);
		 
		 // price change lister 
		 priceTypeChangeListener();

		 FacesContext fc = FacesContext.getCurrentInstance();
		 if(flag){ // page nav.to purchase inquiry
			 fc = FacesContext.getCurrentInstance();
			 fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "prInquiryMain");

		 } else{ // page nav.to request note
			String commonRequestNote = requisitionerAction("PROCESSED");
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, commonRequestNote);
		 }
	 }	
 }
 
 
 /*
  * Getting a java.util.Date Object without time
  */
 public static Date getCurrentDateWithoutTime(Date respDate) {
	 Calendar c1 = new GregorianCalendar();
	 c1.setTime(respDate);
	 Calendar c2 = Calendar.getInstance();
	 c2.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH),0,0,0);
	 c2.set(Calendar.MILLISECOND, 0);

	 return c2.getTime();
 }
 
	 /**
	  * Page navigation to catalogue search page in Purchase inquiry
	  */
	 public void backToPervious() {
		 log.info("backToPervious");
		 FacesContext fc = FacesContext.getCurrentInstance();
		 if(getManagedBean().getPageNavToRequestNote() != null){
			 fc.getApplication().getNavigationHandler().handleNavigation(fc, null, getManagedBean().getPageNavToRequestNote());  
		 } else {
			 // clear data
			 super.renewSesionObject();
			 fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "catalogueSearch");  
		 }
	 }		
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
	
	 public void  getCurrentStatus(Long statusId) {
		if (statusId != null) {
			 getManagedBean().setPriStatus(getStatusDescById(statusId));
		 	if(getStatusDescById(statusId).equalsIgnoreCase(ParameterConst.PRI_PENDING_SUBMISSION)){
		 		getManagedBean().setRequisitionerFlag(false);
		 		getManagedBean().setSupplierFlag(true);
		 	} else if(getStatusDescById(statusId).equalsIgnoreCase(ParameterConst.PRI_PENDING_SUPP_RESPONSE)){
		 		getManagedBean().setRequisitionerFlag(true);
		 		getManagedBean().setSupplierFlag(false);
		 	} else{
		 		getManagedBean().setRequisitionerFlag(true);
		 		getManagedBean().setSupplierFlag(true);
		 	}
		}
	  }
	
	private	List<ScSupplierItemPrice>	createScSupplierItemPrices(ScRequestSupplierItem	scRequestSupplierItem, List<CmPricing> pricings ,boolean selectedSupplierPrice){
		List<ScSupplierItemPrice> results	=	new ArrayList<>();
		for(CmPricing pricing: pricings){
			results.add(createSupplierItemPrice(scRequestSupplierItem, pricing , selectedSupplierPrice));
		}
		return results;
	}
	private ScSupplierItemPrice		createSupplierItemPrice(ScRequestSupplierItem	scRequestSupplierItem, CmPricing pricing ,boolean selectedSupplierPrice){
		IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
    	ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
    	
    	// price type
    	parameterReqDTO = new ParameterReqDTO();
    	parameterReqDTO.setParameterType(ParameterTypeConst.PRICE_TYPE);
    	parameterReqDTO.setParameterCode(pricing.getPricingType());
    	ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
    	
    	ScSupplierItemPrice	result	=	new ScSupplierItemPrice();
    	// zone dtl
    	if(pricing.getCmSupplierZone() != null){
    		result.setTplSpecZone(createSpecZone(pricing.getCmSupplierZone()));
    	}
		
    	if(pricing.getFromQty() != null){
    		result.setFromQty(pricing.getFromQty());
    	}
    	if(pricing.getToQty() != null){
    		result.setToQty(pricing.getToQty());
    	}
    	result.setScRequestSupplierItem(scRequestSupplierItem);
    	result.setPricing(pricing);
    	if(result.getItemPrice() == null || result.getItemPrice().equals(new BigDecimal(0))){
    		result.setItemPrice(pricing.getPrice());	
    	}
    	
    	result.setPriceType(resDTO.getPmParameter());
    	return result;
	}
	
	 /**
		 *  stockAvailabilityEvent to show item list in Purchase inquiry
	    */
		public void stockAvailabilityEvent() {
			debugLogger("stockAvailabilityEvent");
			
			if(getManagedBean().getStockAvailable() != null){
				
				if(getManagedBean().getStockAvailable().equals(ParameterConst.PI_STOCK_AVAILABILITY)){
					getManagedBean().setStockAvailabilityFlag(Boolean.TRUE);
				} else{
					getManagedBean().setStockAvailabilityFlag(Boolean.FALSE);
				}
				
			}
		}

		private TplSpecZoneDtl createSpecZone(CmSupplierZone supplierZone) {
			TplSpecZoneDtl specZoneDtl = null;
			if(supplierZone != null) {
				if(supplierZone.getCmSupplierZoneDtls() != null) {
					specZoneDtl = new TplSpecZoneDtl();
					specZoneDtl.setZoneName(supplierZone.getSuppZoneName());
					ScPIZone zone = getManagedBean().getScPIZone();
					List<TplSpecZoneLoc> zoneLocs = new ArrayList<TplSpecZoneLoc>();
					for(CmSupplierZoneDTL zoneDtl : supplierZone.getCmSupplierZoneDtls()) {
						TplSpecZoneLoc specZoneLoc = new TplSpecZoneLoc();
						specZoneLoc.setPmDistrict(zoneDtl.getPmDistrict());
						specZoneLoc.setPmState(zoneDtl.getPmState());
						specZoneLoc.setPmDivision(zoneDtl.getPmDivision());
						specZoneLoc.setTplSpecZoneDtl(specZoneDtl);
						zoneLocs.add(specZoneLoc);
					}
					zone.setScPrInquiry(getManagedBean().getScPrInquiry());
					specZoneDtl.setTplSpecZone(zone);
					getManagedBean().getScPrInquiry().setScPIZone(zone);
					specZoneDtl.setTplSpecZoneLocs(zoneLocs);
				}
			}
			return specZoneDtl;
		}
		
		/**
		 * Get parameter code select items by parameter type for radio buttons.
		 * 
		 * @param parameterType
		 * 			the parameter type
		 * @return array of SelectItem
		 */
		public SelectItem[] getRadioButtonCodeValues(String parameterType , boolean g2gSeller) {
			return getRadioButtonCodeValuesBySearchBean(parameterType, null , g2gSeller);
		}
		
		public SelectItem[] getRadioButtonCodeValuesBySearchBean(String parameterType, ISearchBean<PmParameterDesc> searchBean , boolean g2gSeller) {
			SelectItem[] items = null;
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
					.getParameterDescListBySearchBean(parameterType, searchBean);

			items = new SelectItem[parameterDescs.size()];
			int i = 0;
			for(PmParameterDesc parameterDesc : parameterDescs) {
				if(g2gSeller){
					if(parameterDesc.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)){
						items[i] = new SelectItem(parameterDesc.getPmParameter().getParameterCode(), parameterDesc.getCodeName() , null , !g2gSeller );
					} else {
						items[i] = new SelectItem(parameterDesc.getPmParameter().getParameterCode(), parameterDesc.getCodeName() , null , g2gSeller);
					}
				} else{

					if(parameterDesc.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)){
						items[i] = new SelectItem(parameterDesc.getPmParameter().getParameterCode(), parameterDesc.getCodeName() , null , !g2gSeller);
					} else{
						items[i] = new SelectItem(parameterDesc.getPmParameter().getParameterCode(), parameterDesc.getCodeName() , null , g2gSeller);
					}
				}
				
				i++;
			}
			return items;
		}
		
		public boolean isSupplierUser(){
			FacesScopes fs = new FacesScopes();
			return fs.hasRoleUser("SUPPLIER_USER");
		}


		public boolean isRequisitioner(){
			FacesScopes fs = new FacesScopes();
			return fs.hasRoleUser("REQUISITIONER");
		}
}