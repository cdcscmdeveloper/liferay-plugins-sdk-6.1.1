/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.purchaseinquiry;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;

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
import my.ep.constant.BpmOutcomeConst;
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
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScPRIRemark;
import my.ep.persistence.entity.sc.directpurchase.ScPRIWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.OrganizationType;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.backingbean.sc.directpurchase.RequestNoteBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.purchaseinquiry.PurchaseInquiryManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

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
public class PurchaseInquiryBackingBean extends BaseBackingBean<PurchaseInquiryManagedBean> {
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
	  public String prInquiryAction(CmSupplierItem  cmSupplierItem){
		  debugLogger("prInquiryAction");
		  IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		  ParameterReqDTO reqDTO = new ParameterReqDTO();
		  ScRequestSupplierItem requestSupplierItem = getManagedBean().getScRequestSupplierItem();
		  ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, ICatalogueSO.class);
		  IStatusSO iStatusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		  IPrInquirySO prInquirySO = (IPrInquirySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
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
			  supplierItemPrice.setPricing(cmSupplierItem.getCmPricings().get(0));
			  requestSupplierItem.setSmSupplier(cmSupplierItem.getSmSupplier());
			  supplierItemPrice.setScRequestSupplierItem(requestSupplierItem);
			  requestSupplierItem.setSupplierItemPrice(supplierItemPrice);
			  requestSupplierItem.setRequestNoteItem(scRequestItem);
		  }
		  // WorkFlow Status
		  List<ScPRIWorkFlowStatus> workFlowStatuses =  new  ArrayList<ScPRIWorkFlowStatus>();
		  ScPRIWorkFlowStatus workFlowStatus = new ScPRIWorkFlowStatus();
		  workFlowStatus.setPrInquiry(scPrInquiry);
		  PmStatus status = new PmStatus();
		  status.setStatusId(DPStatusConstants.PRI_PENDING_SUBMISSION);
		  StatusReqDTO statusReqDTO = new StatusReqDTO();
		  if (status != null) {
			  statusReqDTO.setStatusId(status.getStatusId());
			  StatusResDTO statusResDTO = iStatusSO.retrieveStatusByStatusId(statusReqDTO);
			  workFlowStatus.setPmStatus(statusResDTO.getPmStatus());
		  }
		  workFlowStatus.setCurrent(true);
		  // workFlowStatuses.add(workFlowStatus);
		  scPrInquiry.setWorkflowStatuses(workFlowStatuses);

		  scPrInquiry.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		  scPrInquiry.setDayToResponse(ParameterConst.PIQ_RESP_DAYS); // by default days
		  scPrInquiry.setDateToResponse(calcDayToResponse(scPrInquiry));
		  scPrInquiry.setPrInquiryNo(prInquirySO.generatePrInquiryNo());
		  // Initializing Remarks 
		  scPrInquiry.setRemarks(initializeRemarks(scPrInquiry));
		  // TODO check supplier is government seller or not 
		  ScRequestNoteDtl requestNoteDtl =  getManagedBean().getRequestNoteDtl();
		  if(cmSupplierItem.getSmSupplier().getPmOrgProfile().isGovtSeller()){
			  reqDTO = new ParameterReqDTO();
			  reqDTO.setParameterType(ParameterTypeConst.PROCUREMENT_TYPE);
			  reqDTO.setParameterCode(ParameterConst.PROC_TYPE_G2G);
			  ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			  requestNoteDtl.setProcurementType(resDTO.getPmParameter());
		  } else {
			  reqDTO = new ParameterReqDTO();
			  reqDTO.setParameterType(ParameterTypeConst.PROCUREMENT_TYPE);
			  reqDTO.setParameterCode(ParameterConst.PROC_TYPE_NORMAL);
			  ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			  requestNoteDtl.setProcurementType(resDTO.getPmParameter());
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
				  String	priceType	=	priceTypes.get(0);
				  for (CmPricing princing : cmSupplierItem.getCmPricings()) {
					  if(princing.getPricingType().equalsIgnoreCase(priceType)){
						  cmPricings.add(princing);
					  }
				  }
				  requestSupplierItem.setSupplierItemPrices(createScSupplierItemPrices(requestSupplierItem, cmPricings));
			  }
			  // TODO PriceType drop down list in AddToCart
			  getManagedBean().setPriceTypes(getPriceTypeDropdownValues( ParameterTypeConst.PRICE_TYPE, priceTypes));
		  }
		  // prepare  requestSupplierItems , request item and supplier item price 
		  IViewSupplierItemSO viewSupplierItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, IViewSupplierItemSO.class);
		  CatalogueReqDTO catalogueReqDTO = new CatalogueReqDTO();
		  catalogueReqDTO.setViewSearchBean(new CatalogueBackingBean().getManagedBean().getSearchDataModel().getSearchBean());
		  catalogueReqDTO.setSupplierItemId(cmSupplierItem.getSupplierItemId());
		  catalogueReqDTO.setExtensionCode(cmSupplierItem.getCmItem().getExtensionCode());
		  CatalogueResDTO catalogueResDTO = viewSupplierItemSO.getSuppliersItemPricingByExtensionCode(catalogueReqDTO);
		  ScRequestItem requestItem = createRequestItem(cmSupplierItem);
		  List<ScRequestSupplierItem> supplierItems = new ArrayList<ScRequestSupplierItem>();
		  for (VCmSupplierItem vCmSupplierItem : catalogueResDTO.getViewSuppItemResultBean().getResultList()) {
			  if (vCmSupplierItem.getCmPricings() != null && vCmSupplierItem.getCmPricings().size() > 0) {
				  CmSupplierItem supplierItem = vCmSupplierItem.getCmPricings().get(0).getCmSupplierItem();
				  ScRequestSupplierItem scRequestSupplierItem = createScRequestSupplierItem(
						  requestItem, supplierItem.getSmSupplier(), vCmSupplierItem.getCmPricings());
				  supplierItems.add(scRequestSupplierItem);	
			  }
		  }
		  if(supplierItems.isEmpty()){
			  supplierItems.add(requestSupplierItem);
			  
		  }
		  // Add selected supplier item.
		  requestItem.setSupplierItems(supplierItems);
		  scPrInquiry.setRequestItem(requestItem);
		  scPrInquiry.setScRequestSupplierItems(supplierItems);
		  scPrInquiry.setRequestSupplierItem(requestSupplierItem);
		  getManagedBean().setScPrInquiry(scPrInquiry);
		  requestNoteDtl.setRequestItem(requestItem);
		  requestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
		  getManagedBean().setRequestNoteDtl(requestNoteDtl);
		  requestSupplierItem.setRequestNoteItem(requestItem);
		  getManagedBean().setScRequestSupplierItem(requestSupplierItem);
		  
		  // pr inquiry status
		  getCurrentStatus(status.getStatusId());
		  //TODO default value to  price type drop down listing
		  getManagedBean().setPriceType(ParameterConst.PRICE_TYPE_STANDARD);
		  populateUserDivisionList();
		  return "prInquiryMain";		 
	  }

	  private ScRequestSupplierItem createScRequestSupplierItem(ScRequestItem requestItem, SmSupplier smSupplier, List<CmPricing> cmPricings) {
		  ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();

		  requestSupplierItem.setRequestNoteItem(requestItem);
		  requestSupplierItem.setSmSupplier(smSupplier);

		  requestSupplierItem.setSupplierItemPrices(createScSupplierItemPrices(requestSupplierItem, cmPricings));

		  return requestSupplierItem;
	  }

	  private ScRequestItem createRequestItem(CmSupplierItem cmSupplierItem){
		  CmItem cmItem = cmSupplierItem.getCmItem();
		  ScRequestItem scRequestItem	= new ScRequestItem();
		  scRequestItem.setCmItem(cmItem);
		  scRequestItem.setItemName(cmItem.getItemName());
		  scRequestItem.setItemDesc(cmSupplierItem.getItemDesc());
		  scRequestItem.setMinqty(cmSupplierItem.getMinOrder());
		  scRequestItem.setUom(cmSupplierItem.getPmUom());

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
		List<CmPricing> pricings = new ArrayList<CmPricing>();
		if (getManagedBean().getSupplierItem() != null) {
			for (CmPricing cmPricing : getManagedBean().getSupplierItem()
					.getCmPricings()) {
				if (getManagedBean().getPriceType().equalsIgnoreCase(
						cmPricing.getPricingType())) {
					pricings.add(cmPricing);
				}
			}
		}
		if(getManagedBean().getScPrInquiry().getScRequestSupplierItems() != null){
			ScRequestSupplierItem requestSupplierItem = getManagedBean().getScPrInquiry().getScRequestSupplierItems().get(0);
			requestSupplierItem.setSupplierItemPrices(createScSupplierItemPrices(requestSupplierItem, pricings));
		}
	}
		
	 /**
	  *  submitPrEnquiryDtl  method to submit purchase inquiry details  in Purchase inquiry
	  */	
	  public String submitPrInquiryDtl() {
		  debugLogger("submitPrInquiryDtl");
		    IPrInquirySO prInquirySO = null;
		    IStatusSO iStatusSO = null;
		    IDirectPurchaseSO directPurchaseSO = null;
		    ParameterResDTO resDTO = null;
		    try {
		    	IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		    	prInquirySO = (IPrInquirySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
		    	iStatusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		    	directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
		    	PrInquiryReqDTO reqDTO = new PrInquiryReqDTO();	
		    	ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		    	FacesScopes facesScopes = new FacesScopes();
		    	ScPrInquiry prInquiry = getManagedBean().getScPrInquiry();

		    	List<ScRequestNoteDtl> requestNoteDtls =  new  ArrayList<ScRequestNoteDtl>();
		    	ScRequestNoteDtl requestNoteDtl = getManagedBean().getRequestNoteDtl();
		    	ScRequestNote	requestNote	=	new ScRequestNote();
		    	//requestNote.setPmOrgProfile(getManagedBean().getPmOrgProfile()).
		    	requestNote.setRequestNoteNo(directPurchaseSO.generateRequestNoteNo());
		    	requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_PRI);
		    	requestNoteDtl.setScRequestNote(requestNote);
		    	requestNoteDtl.setAssignedUser(facesScopes.getUserSessionBean());
		    	
		    	parameterReqDTO = new ParameterReqDTO();
		    	parameterReqDTO.setParameterType(ParameterTypeConst.PROCUREMENT_TYPE);
		    	parameterReqDTO.setParameterCode(requestNoteDtl.getProcurementType().getParameterCode());
		    	resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
			    requestNoteDtl.setProcurementType(resDTO.getPmParameter());
		    	
		    	if (getManagedBean().getPriceType() != null) {
		    		parameterReqDTO = new ParameterReqDTO();
		    		parameterReqDTO.setParameterType(ParameterTypeConst.PRICE_TYPE);
		    		parameterReqDTO.setParameterCode(getManagedBean().getPriceType());
		    		resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
		    	}
		    	requestNoteDtl.setPriceType(resDTO.getPmParameter());
		    	requestNoteDtls.add(requestNoteDtl);
		    	
		    	List<ScRequestSupplierItem> requestSupplierItems =  new  ArrayList<ScRequestSupplierItem>();
		    	if(prInquiry.getScRequestSupplierItems() != null && !prInquiry.getScRequestSupplierItems().isEmpty()){
		    		for (ScRequestSupplierItem scRequestSupplierItem :  prInquiry.getScRequestSupplierItems()) {
		    			List<ScSupplierItemPrice> results	=	new ArrayList<ScSupplierItemPrice>();
		    			for (ScSupplierItemPrice scSupplierItemPrice : scRequestSupplierItem.getSupplierItemPrices()) {
		    				scSupplierItemPrice.setPriceType(resDTO.getPmParameter());
		    				results.add(scSupplierItemPrice);
		    			}
		    			scRequestSupplierItem.setSupplierItemPrices(results);
		    			scRequestSupplierItem.setScRequestNoteDtl(requestNoteDtls);
		    			scRequestSupplierItem.setScPrInquiy(prInquiry);
		    			requestSupplierItems.add(scRequestSupplierItem);
		    		}
		    	}

			     prInquiry.setScRequestSupplierItems(requestSupplierItems);
			     prInquiry.setCreatedDate(DateUtils.getCurrentDate());
			     // setting remarks 
			     prInquiry.setRemarks(initializeRemarks(prInquiry));
			      // WorkFlow Status
				  List<ScPRIWorkFlowStatus> workFlowStatuses =  new  ArrayList<ScPRIWorkFlowStatus>();
				  ScPRIWorkFlowStatus workFlowStatus = new ScPRIWorkFlowStatus();
				  workFlowStatus.setPrInquiry(prInquiry);
				  PmStatus status = new PmStatus();
				  status.setStatusId(DPStatusConstants.RN_PENDING_SUBMISSION);
				  StatusReqDTO statusReqDTO = new StatusReqDTO();
				  if (status != null) {
				      statusReqDTO.setStatusId(status.getStatusId());
				      StatusResDTO statusResDTO = iStatusSO.retrieveStatusByStatusId(statusReqDTO);
				      workFlowStatus.setPmStatus(statusResDTO.getPmStatus());
				   }
				 workFlowStatus.setCurrent(true);
			    // workFlowStatuses.add(workFlowStatus);
			     prInquiry.setWorkflowStatuses(workFlowStatuses);
			     
			     if(prInquiry.getRemarks() != null ){
			    	 for (int i = 0; i < prInquiry.getRemarks().size(); i++) {
			    		 ScPRIRemark remark = prInquiry.getRemarks().get(i);
			    		 if(StringUtils.isBlank(remark.getRemark())){
			    			 prInquiry.getRemarks().remove(remark);
			    		 }	
			    	 } 
			     }
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
				iStatusSO = null;
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
			   if(prInquiry.getRemarks() != null ){
				   for (int i = 0; i < prInquiry.getRemarks().size(); i++) {
					   ScPRIRemark remark = prInquiry.getRemarks().get(i);
					   if(StringUtils.isBlank(remark.getRemark())){
						   prInquiry.getRemarks().remove(remark);
					   }	
				   } 
			   }
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
	 * to get Division drop down Listing in Purchase inquiry
	 */
	private void populateUserDivisionList() {
		debugLogger("populateUserMinistryList");
		PmUser user = new FacesScopes().getUserSessionBean();
		if (user != null) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setUserId(user.getUserId());
			reqDTO.setOrgType(OrganizationType.DIVISION.value());

			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			List<PmOrgProfile> userDivisionList = resDTO.getOrgProfileList();
			System.out.println("userDivisionList" + userDivisionList.size());
			getManagedBean().setUserDivisionList(userDivisionList);
		}
	}

	/**
	 * Event for procurement type drop down in Purchase inquiry
	 */
	public void procurementTypeEvent() {
		log.info("procurementTypeEvent");
		if (getManagedBean().getRequestNoteDtl().getProcurementType().getParameterCode()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL)) {
			getManagedBean().setProcTypeOption(false);
			getManagedBean().setJustTypeOption(false);

		} else if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			getManagedBean().setProcTypeOption(false);
			getManagedBean().setJustTypeOption(false);

		} else if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			getManagedBean().setProcTypeOption(true);
			getManagedBean()
					.setJustDropdown(
							getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_ADHOC));

		} else if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_EMERGENCY)) {
			getManagedBean().setProcTypeOption(true);
			getManagedBean()
					.setJustDropdown(
							getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_EMERGENCY));
		}
	}
		
	/**
	 * Event for justification type drop down in Purchase inquiry
	 */
	public void justificationTypeEvent() {
		log.info("justificationTypeEvent");
		if (getManagedBean().getRequestNoteDtl().getJustificationType()
				.equalsIgnoreCase(ParameterTypeConst.JUSTIFICATION_TYPE_OTHER)) {
			getManagedBean().setJustTypeOption(true);
		} else {
			getManagedBean().setJustTypeOption(false);
		}
	}
	
	public	String	requisitionerAction(String action){
		ScPrInquiry prInquiry = getManagedBean().getScPrInquiry();
		if(StringUtils.equalsIgnoreCase("PROCESSED",action)){
			//redirect to requestNote screen
			IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			RequestNoteReqDTO	reqDTO	=	new RequestNoteReqDTO();
			
			// checking wheather stock available or not
			if(prInquiry.getIsStockAvailable().equals(1)){
				ScRequestNote	requestNote	=	prInquiry.getScRequestSupplierItems().get(0).getRequestNoteItem().getRequestNote();
				reqDTO.setRequestNoteId(requestNote.getRequestNoteId());
				IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
				RequestNoteResDTO resDTO = directPurchaseSO.getRequestNote(reqDTO);
				requestNote	=	resDTO.getRequestNoteDetail();

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
						scRequestNoteDtl.setFulfillmentType(parameterResDTO.getPmParameter());
						scRequestNoteDtl.setDeliverTermType(ParameterConst.SQ_DURATION_DAY);
						scRequestNoteDtl.setAssignedUser(new PmUser());
						requestNoteBackingBean.getManagedBean().setJustificationType(scRequestNoteDtl.getJustificationType());
						requestNoteBackingBean.getManagedBean().setProcurementType(scRequestNoteDtl.getProcurementType().getParameterCode());
						requestNoteDtls.add(scRequestNoteDtl);
					}
					requestNoteBackingBean.getManagedBean().setRequestSupplierItem(requestNoteDtls.get(0).getScRequestSupplierItem());
				}
				
				requestNoteBackingBean.getManagedBean().setRequestSupplierItem(requestNote.getRequestNoteDtls().get(0).getScRequestSupplierItem());
				IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
				FacesScopes facesScopes = new FacesScopes();
				String token = facesScopes.getBPMAuthenticationCtxToken();
				BpmRequestDTO reqBPMDTO = new BpmRequestDTO();
				reqBPMDTO.setCtxToken(token);
				reqBPMDTO.setTaskId(getManagedBean().getTaskId());
				BpmResponseDTO res = bpmSO.getTaskDetails(reqBPMDTO);
				requestNoteBackingBean.getManagedBean().setOutComes(res.getAction());

				requestNote.setRemarks(requestNoteBackingBean.initializeRemarks(new ScRequestNote()));
				requestNoteBackingBean.getManagedBean().setRequestNote(requestNote);
				requestNoteBackingBean.getManagedBean().setRequestNoteDtls(requestNoteDtls);
				requestNoteBackingBean.getManagedBean().setSourceMethod(ParameterConst.METHOD_SC);
				requestNoteBackingBean.getManagedBean().setTaskId(getManagedBean().getTaskId());
				
				requestNoteBackingBean.getManagedBean().setOrgProfile(getManagedBean().getPmOrgProfile());
				requestNoteBackingBean.getManagedBean().setOrgProfileId(getManagedBean().getPmOrgProfile().getOrgProfileId());
				ScRequestNoteDtl scRequestNoteDtl = new ScRequestNoteDtl();
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO.setParameterType(ParameterTypeConst.FULFILMENT_TYPE);
				parameterReqDTO.setParameterCode(ParameterConst.FULFILMENT_TYPE_ONE_OFF);
				ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
				
				scRequestNoteDtl.setFulfillmentType(parameterResDTO.getPmParameter());
				requestNoteBackingBean.fulfilmentTypeEvent(scRequestNoteDtl);
				requestNoteBackingBean.initialInformation(new ScRequestNote());
				requestNoteBackingBean.getCurrentStatus(DPStatusConstants.RN_PENDING_SUBMISSION);
				requestNoteBackingBean.populateUserDivisionList();
				requestNoteBackingBean.getManagedBean().setFulfillmentType(ParameterConst.FULFILMENT_TYPE_ONE_OFF);
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

		 if(prInquiry.getScRequestSupplierItems() != null && prInquiry.getScRequestSupplierItems().size()>0 ){
			 ScRequestSupplierItem requestSupplierItem = prInquiry.getScRequestSupplierItems().get(0);
			 ScSupplierItemPrice itemPrice =requestSupplierItem.getSupplierItemPrices().get(0);
			 ScRequestNoteDtl requestNoteDtl = requestSupplierItem.getScRequestNoteDtl().get(0);
			 requestSupplierItem.setSupplierItemPrice(itemPrice);
			 getManagedBean().setPmOrgProfile(requestSupplierItem.getRequestNoteItem().getRequestNote().getPmOrgProfile());
			 getManagedBean().setSelectedPtj(requestSupplierItem.getRequestNoteItem().getRequestNote().getPmOrgProfile());
			 getManagedBean().setRequestNoteDtl(requestNoteDtl);
			 prInquiry.setRequestSupplierItem(requestSupplierItem);
			 prInquiry.setDayToResponse(ParameterConst.PIQ_RESP_DAYS); // by default days

			 List<String> priceTypes= new ArrayList<String>();
			 priceTypes.add(itemPrice.getPricing().getPricingType());
			 getManagedBean().setPriceType(itemPrice.getPricing().getPricingType());
			 getManagedBean().setPriceTypes(getPriceTypeDropdownValues(ParameterTypeConst.PRICE_TYPE, priceTypes));
		 }
		 getManagedBean().setTaskId(taskId);
		 getManagedBean().setProcessName(res.getProcessName());
		 getManagedBean().setOutComes(res.getAction());
		 getManagedBean().setStockAvailable(prInquiry.getIsStockAvailable());
		 getManagedBean().setScPrInquiry(prInquiry);
		 initializeRemarks(prInquiry);
		 //initialInformation(new ScRequestNote());
		 getCurrentStatus(res.getDocStatus().longValue()); 
		 FacesContext fc = FacesContext.getCurrentInstance();
		 fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "prInquiryMain");
	 }	
 }

	/**
	 * Page navigation to catalogue search page in Purchase inquiry
	 */
	public String backToPervious() {
		log.info("backToPervious");
		return "catalogueSearch";
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
	 
	 
	public void ptjNameEvent() {
		if (getManagedBean().getOrgProfileId() != null) {
			for (PmOrgProfile orgProfile : getManagedBean()
					.getUserDivisionList()) {
				if (orgProfile.getOrgProfileId().equals(
						getManagedBean().getOrgProfileId())) {
					getManagedBean().setPmOrgProfile(orgProfile);
				}
			}
		}
	}
	
	private	List<ScSupplierItemPrice>	createScSupplierItemPrices(ScRequestSupplierItem	scRequestSupplierItem, List<CmPricing> pricings){
		List<ScSupplierItemPrice> results	=	new ArrayList<>();
		for(CmPricing pricing: pricings){
			results.add(createSupplierItemPrice(scRequestSupplierItem, pricing));
		}
		return results;
	}
	private ScSupplierItemPrice		createSupplierItemPrice(ScRequestSupplierItem	scRequestSupplierItem, CmPricing pricing){
		ScSupplierItemPrice	result	=	new ScSupplierItemPrice();
		result.setScRequestSupplierItem(scRequestSupplierItem);
		result.setItemPrice(new BigDecimal(0.0));
		result.setPricing(pricing);
		return result;
	}
}