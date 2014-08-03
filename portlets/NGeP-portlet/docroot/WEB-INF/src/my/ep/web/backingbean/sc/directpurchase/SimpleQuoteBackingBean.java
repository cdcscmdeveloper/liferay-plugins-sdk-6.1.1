/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.directpurchase;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import my.ep.biz.service.ct.supplier.IPanelSupplierSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.sc.directpurchase.IQuoteSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.ct.supplier.PanelSupplierReqDTO;
import my.ep.dto.ct.supplier.PanelSupplierResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.directpurchase.QuoteReqDTO;
import my.ep.dto.sc.directpurchase.QuoteResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sc.directpurchase.ScBusinessNatureQuoteCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationDistrictCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationDivisionCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationStateCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteDoc;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteSupplier;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteZone;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sc.directpurchase.ScSQRemark;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.directpurchase.SimpleQuoteManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.primefaces.model.TreeNode;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
/**
 * 
 * @author sri
 * @since 25.02.2014
 * 
 */
@ManagedBean(name = "simpleQuoteBackingBean")
@RequestScoped
public class SimpleQuoteBackingBean extends BaseBackingBean<SimpleQuoteManagedBean> {
	private static Logger log = Logger.getLogger(SimpleQuoteBackingBean.class);

	public SimpleQuoteBackingBean() {
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
	 * simpleQuoteAction for getting cmSupplierItem from search catalogue in
	 * simpleQuote
	 */
	public String simpleQuoteAction(Long docId, boolean panelItemFlag ,List<CmSupplierItem> compareSupplierItems) {
		debugLogger("simpleQuoteAction");
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT,ICatalogueSO.class);
		IPanelSupplierSO panelSupplierSO	=	(IPanelSupplierSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IPanelSupplierSO.class);
		IQuoteSO quoteSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQuoteSO.class);
		ScQuote scQuote = getManagedBean().getScQuote();
		if(scQuote == null){
			scQuote = new ScQuote();
		}
		
		// initializing product and service item list 
		if(docId == 0){
			if(getManagedBean().getProductTemplateSpecBean().getSpecMaster() == null && getManagedBean().getServiceTemplateSpecBean().getSpecMaster() == null){
				prepareQuestions(null, null);
			}
		}
		
		if(docId != null && docId >0 ){
			// panel item 
			if(panelItemFlag){
				// check non contract item list 
				if(getManagedBean().getSupplierItems() ==null || getManagedBean().getSupplierItems().isEmpty()){
					scQuote.setPanel(Boolean.TRUE);
					scQuote.setZonal(Boolean.FALSE);

					PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();
					reqDTO.setPanelItemId(docId);
					PanelSupplierResDTO resDTO= panelSupplierSO.retrievePanelItem(reqDTO);
					List<CtPanelItem> panelItems = getManagedBean().getPanelItems();
					if(panelItems == null){
						panelItems = new ArrayList<CtPanelItem>();
					}

					if(resDTO.getCtPanelItem() != null) {
					   if(panelItems.isEmpty()){
							panelItems.add(resDTO.getCtPanelItem());
							getManagedBean().setPanelItems(panelItems);
							prepareQuestions(resDTO.getCtPanelItem() , null);

					 } else {
						List<Long> itemIds = new ArrayList<Long>();
						for (CtPanelItem ctPanelItem : panelItems) {
							 itemIds.add(ctPanelItem.getPanelItemId());
						}
						
					   if (itemIds.size() > 0) {
						 if(itemIds.contains(resDTO.getCtPanelItem().getPanelItemId())){
							 throw new EpException(MessageConst.INFO_700027, new String[]{resDTO.getCtPanelItem().getScRequestItem().getCmItem().getItemName(),""});
							 
						} else {
							panelItems.add(resDTO.getCtPanelItem());
							getManagedBean().setPanelItems(panelItems);
							prepareQuestions(resDTO.getCtPanelItem() , null);
						}
					   }
					 }
					}
				} else{
					throw new EpException("Select non contract items only");
				}

			}  else { // non contract item
				
			     if(getManagedBean().getPanelItems() == null || getManagedBean().getPanelItems().isEmpty()){ // check panel item list
				    CatalogueReqDTO catalogueReqDTO = new CatalogueReqDTO();
				    catalogueReqDTO.setDocId(docId);
					CatalogueResDTO catalogueResDTO = catalogueSO.searchSupplierItemByStatus(catalogueReqDTO);

				   if(catalogueResDTO.getSupplierItemList() != null && !catalogueResDTO.getSupplierItemList().isEmpty()){
					 CmSupplierItem selectedSupplierItem = catalogueResDTO.getSupplierItemList().get(0);

					 // copy all selected suppliers from the catalogue search
					 if(compareSupplierItems == null){
						 IViewSupplierItemSO viewSupplierItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, IViewSupplierItemSO.class);
						 catalogueReqDTO = new CatalogueReqDTO();
						 catalogueReqDTO.setViewSearchBean(new CatalogueBackingBean().getManagedBean().getSearchDataModel().getSearchBean());
						 catalogueReqDTO.setSupplierItemId(selectedSupplierItem.getSupplierItemId());
						 catalogueReqDTO.setExtensionCode(selectedSupplierItem.getCmItem().getExtensionCode());
						 catalogueResDTO = viewSupplierItemSO.getSuppliersItemPricingByExtensionCode(catalogueReqDTO);
						 List<SmSupplier> selectedSuppliers = getManagedBean().getCompareSuppliers();
						 for (VCmSupplierItem vCmSupplierItem : catalogueResDTO.getViewSuppItemResultBean().getResultList()) {
							 if (vCmSupplierItem.getCmPricings() != null && vCmSupplierItem.getCmPricings().size() > 0) {
								 CmSupplierItem supplierItem = vCmSupplierItem.getCmPricings().get(0).getCmSupplierItem();
								 if(supplierItem.getSmSupplier() != null){
									 selectedSuppliers.add(supplierItem.getSmSupplier());
								 }
							 }
						 }
						 selectedSuppliers.add(selectedSupplierItem.getSmSupplier());
						 supplierListListener(selectedSuppliers , scQuote);
					 } else {
						 supplierListListener(addSelectedSuppliers(compareSupplierItems) , scQuote);
					 }


					 List<CmSupplierItem> supplierItems = getManagedBean().getSupplierItems();
					 if(supplierItems == null){
						 supplierItems = new ArrayList<CmSupplierItem>();
					 }

					 if(selectedSupplierItem != null) {
						 if(supplierItems.isEmpty()){
							 supplierItems.add(selectedSupplierItem);
							 prepareQuestions(null , selectedSupplierItem);
							 getManagedBean().setSupplierItems(supplierItems);
						 } else {
							 List<Long> itemIds = new ArrayList<Long>();
							 for (CmSupplierItem cmSupplierItem : supplierItems) {
								 itemIds.add(cmSupplierItem.getCmItem().getItemId());
							 }
							 if (itemIds.size() > 0) {
								 if(itemIds.contains(selectedSupplierItem.getCmItem().getItemId())){
									 throw new EpException(MessageConst.INFO_700027, new String[]{selectedSupplierItem.getCmItem().getItemName(),""});
								 } else {
									 supplierItems.add(selectedSupplierItem);
									 getManagedBean().setSupplierItems(supplierItems);
									 prepareQuestions(null , selectedSupplierItem);
								 }
							 }
						 }
					 }
				   }
			     }else {
			    	 throw new EpException( "Select panel items only");
			     }
			}
		}
		
		scQuote.setMultipleQuote(Boolean.FALSE);
		scQuote.setQuoteNo(quoteSO.generateQuoteNo());
		scQuote.setWorkflowStatuses(null);
		getCurrentStatus(DPStatusConstants.SQ_PENDING_SUBMISSION);
		// Initializing Remarks
		scQuote.setRemarks(initializeRemarks(scQuote));
		
		// procurement type by default as normal
		PmParameter parameter = new PmParameter();
		scQuote.setJustificationType(parameter);
		parameter.setParameterCode(ParameterConst.PROC_TYPE_NORMAL);
		scQuote.setProcurementType(parameter);
		this.getManagedBean().setScQuote(scQuote);

		getManagedBean().setLocLevelDropdown(getLocationLevelDropdown());
		// location level by default - district 
		getManagedBean().setLocationLevel(ParameterConst.LOCATION_LEVEL_DISTRICT);
		  
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
				getManagedBean().getRequestNote().setPmOrgProfile(orgProfileResDTO.getOrgProfileList().get(0));
			}
		}

		// to get country id
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode("MY");
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);
		getManagedBean().setDefaultCountryId(resDTO.getPmCountry().getCountryId());
		
		// update tabmenu
		 RequestContext.getCurrentInstance().update("tabMenuId");
		 return "simpleQuote";
		 
	}
	
	private List<SmSupplier>  addSelectedSuppliers(List<CmSupplierItem> compareSupplierItems) {
		List<SmSupplier> suppliers = getManagedBean().getCompareSuppliers();
		for(CmSupplierItem selectedSupplier : compareSupplierItems){
				suppliers.add(selectedSupplier.getSmSupplier());
		}
		return suppliers;
	}
	
	public void prepareQuestions(CtPanelItem ctPanelItem, CmSupplierItem cmSupplierItem) {
		int itemCode = 0;
		CmItem cmItem = null;
		PmUom uom = null;
		String ItemName ="";
		IQuoteSO quoteSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQuoteSO.class);
		// TODO check flag for panel item or non contract item
		if(ctPanelItem != null){
			String extCode = ctPanelItem.getScRequestItem().getCmItem().getExtensionCode();
			CharSequence charSeq = extCode.subSequence(0, 2);
			itemCode = Integer.valueOf(charSeq.toString());
			cmItem = ctPanelItem.getScRequestItem().getCmItem();
			ItemName = ctPanelItem.getScRequestItem().getCmItem().getItemName();
			uom =  ctPanelItem.getPmUom();
			
		} else if(cmSupplierItem !=null){
			String extCode = cmSupplierItem.getCmItem().getExtensionCode();
			CharSequence charSeq = extCode.subSequence(0, 2);
			itemCode = Integer.valueOf(charSeq.toString());
			cmItem = cmSupplierItem.getCmItem();
			ItemName = cmSupplierItem.getCmItem().getItemName();
			uom =  cmSupplierItem.getPmUom();
		}

		// TODO checking  two digits in itemCode whether service or product
		QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
		QuoteResDTO quoteResDTO = quoteSO.getQuoteDtlFirstTime(quoteReqDTO);
		//get Template Spec Backing Bean
		PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		TemplateSpecBackingBean  templateSpecBackingBean = (TemplateSpecBackingBean) portletSession.getAttribute("templateSpecBackingBean");
		if (templateSpecBackingBean == null) {
			templateSpecBackingBean = new TemplateSpecBackingBean();
		}
		
		for (ScQuoteDoc quoteDoc : quoteResDTO.getQuoteDocs()) {
			if(ParameterConst.SQ_ITEM_LIST_SERVICE.equalsIgnoreCase(quoteDoc.getSpecMaster().getItemTypeCode().getParameterCode())){
				TemplateSpecBean serviceTemplateSpecBean = getManagedBean().getServiceTemplateSpecBean();
				  TplSpecMaster serviceTpl = null;

				  if(serviceTemplateSpecBean.getSpecMaster() == null){
					  serviceTpl =  quoteDoc.getSpecMaster();
				  } else {
					  serviceTpl = serviceTemplateSpecBean.getSpecMaster();
				  }
				  
				  List<TplSpecQuestion> tplSpecQuestions = serviceTpl.getTplSpecQuestions();
				  if(null == tplSpecQuestions){
					  tplSpecQuestions = new ArrayList<TplSpecQuestion>();
				  }
				  
				  if (itemCode >= 70 && itemCode <= 94) { // for service  between  70 and 94  
					
					templateSpecBackingBean.addQuestion(serviceTpl, tplSpecQuestions, tplSpecQuestions.size()-1, TemplateSpecConst.QUESTION_TYPE_ITEM);
				    TplSpecQuestion specQuestion = tplSpecQuestions.get(tplSpecQuestions.size()-1);
					specQuestion.setClUom(uom);
					specQuestion.setCmItem(cmItem);
					specQuestion.setClItem(ItemName);
				} 
				serviceTpl.setTplSpecQuestions(tplSpecQuestions);
				serviceTemplateSpecBean.setSpecMaster(serviceTpl);
				getManagedBean().setServiceTemplateSpecBean(serviceTemplateSpecBean);

			}else if(ParameterConst.SQ_ITEM_LIST_PRODUCT.equalsIgnoreCase(quoteDoc.getSpecMaster().getItemTypeCode().getParameterCode())){
				TemplateSpecBean productTemplateSpecBean = getManagedBean().getProductTemplateSpecBean();
				TplSpecMaster prodTpl = null;

				if(productTemplateSpecBean.getSpecMaster() == null){
					prodTpl = quoteDoc.getSpecMaster();
				} else {
					prodTpl = productTemplateSpecBean.getSpecMaster();
				}
				
				List<TplSpecQuestion> productQuestions = prodTpl.getTplSpecQuestions();
				if(null == productQuestions){
					productQuestions = new ArrayList<TplSpecQuestion>();
				}
               
				if(itemCode>0){
					if (itemCode < 70 || itemCode > 94) { // for product  < 70 and  > 94  
					
						templateSpecBackingBean.addQuestion(prodTpl ,productQuestions, productQuestions.size()-1, TemplateSpecConst.QUESTION_TYPE_ITEM);
						TplSpecQuestion prodQuestion = productQuestions.get(productQuestions.size()-1);

						prodQuestion.setClUom(uom);
						prodQuestion.setCmItem(cmItem);
						prodQuestion.setClItem(ItemName);
						
					}
				}
				prodTpl.setTplSpecQuestions(productQuestions);
				productTemplateSpecBean.setSpecMaster(prodTpl);
				getManagedBean().setProductTemplateSpecBean(productTemplateSpecBean);
			}
		}
	}
	
	/**
	 * saveSimpleQuote method to save SQ
	 */
	public String saveSimpleQuote(String outcome) {
		debugLogger("saveSimpleQuote");
		IQuoteSO quoteSO = null;
		ScRequestNote requestNote = null;
		try {
			IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			quoteSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQuoteSO.class);
			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
			QuoteReqDTO reqDTO = new QuoteReqDTO();
			ScQuote quote = getManagedBean().getScQuote();
			
			if(quote.getRequestItems() == null){
				List<ScRequestItem> requestItems = new ArrayList<ScRequestItem>();
				ScRequestItem requestItem = new ScRequestItem();
				requestItem.setQuote(quote);
				requestItems.add(requestItem);
				quote.setRequestItems(requestItems);
			}
			
			if(quote.getRequestNote() == null){
				requestNote = getManagedBean().getRequestNote();
				requestNote.setRequestNoteNo(directPurchaseSO.generateRequestNoteNo());
				requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SQ);
				requestNote.setWorkFlowStatuses(null);
				requestNote.setQuote(quote);
				quote.setRequestNote(requestNote);
			}

			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterType(ParameterTypeConst.PROCUREMENT_TYPE);
			parameterReqDTO.setParameterCode(quote.getProcurementType().getParameterCode());
			ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
			quote.setProcurementType(resDTO.getPmParameter());

			if(getManagedBean().getLocationLevel() != null){
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO.setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				parameterReqDTO.setParameterCode(getManagedBean().getLocationLevel());
				resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
				quote.setZoneLocatonLevel(resDTO.getPmParameter());
			}
			
			// justification type
			parameterReqDTO = new ParameterReqDTO();
			if (quote.getProcurementType().getParameterCode()
					.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
				parameterReqDTO.setParameterType(ParameterTypeConst.JUSTIFICATION_TYPE_ADHOC);
				if(quote.getJustificationType().getParameterCode() != null){
					parameterReqDTO.setParameterCode(quote.getJustificationType().getParameterCode());
					resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
					quote.setJustificationType(resDTO.getPmParameter());
				}
			} else if (quote.getProcurementType().getParameterCode()
					.equalsIgnoreCase(ParameterConst.PROC_TYPE_EMERGENCY)) {
				parameterReqDTO.setParameterType(ParameterTypeConst.JUSTIFICATION_TYPE_EMERGENCY);
				if(quote.getJustificationType().getParameterCode() != null){
					parameterReqDTO.setParameterCode(quote.getJustificationType().getParameterCode());
					resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
					quote.setJustificationType(resDTO.getPmParameter());
				}
			} else {
				quote.setJustificationType(null);
			}
			
			// zone details
			if(!getManagedBean().getZoneTrees().isEmpty()){
				quote.setQuoteZone(getQuoteZones(getManagedBean().getZoneTrees()));
			}
			
			//by location
			if(!getManagedBean().getLocationTrees().isEmpty()){

				if(quote.getLocationStateCriterias() == null || quote.getLocationStateCriterias().isEmpty()){
					quote.setLocationStateCriterias(getLocationStateCriterias(getManagedBean().getLocationTrees(), quote));	
				}
				if(quote.getLocationDivisionCriterias() == null || quote.getLocationDivisionCriterias().isEmpty()){
					quote.setLocationDivisionCriterias(getLocationDivisionCriterias(getManagedBean().getLocationTrees(), quote));
				}
				if(quote.getLocationDistrictCriterias() == null || quote.getLocationDistrictCriterias().isEmpty()){
					quote.setLocationDistrictCriterias(getLocationDistrictCriterias(getManagedBean().getLocationTrees(), quote));
				}
			}

			// by business nature
			if(getManagedBean().getBusinessNatureCriterias() != null && getManagedBean().getBusinessNatureCriterias().size() >0){
				quote.setBusinessNatureCriterias(getManagedBean().getBusinessNatureCriterias());
			}
			
			// by supplier 
			List<ScQuoteSupplier> suppliers = new ArrayList<ScQuoteSupplier>();
			if(getManagedBean().getSupplierList() != null){
				suppliers.addAll(getManagedBean().getSupplierList());
			}
			// panel item
			if(getManagedBean().getPanelSuppliers() != null){
				suppliers.addAll(getManagedBean().getPanelSuppliers());
			}
			// govt sellers
			if(getManagedBean().getGovtSellers() != null){
				suppliers.addAll(getManagedBean().getGovtSellers());
			}
			if(suppliers != null){
				quote.setScQuoteSuppliers(suppliers);
			}
			
			// remarks
			quote.setRemarks(initializeRemarks(quote));
			if(quote.getRemarks() != null ){
				for (int i = 0; i < quote.getRemarks().size(); i++) {
					ScSQRemark remark = quote.getRemarks().get(i);
					if(StringUtils.isBlank(remark.getRemark())){
						quote.getRemarks().remove(remark);
					}	
				} 
			}
			quote.setWorkflowStatuses(null);
			
			// product and service
			List<ScQuoteDoc> quoteDocs = new ArrayList<ScQuoteDoc>();
			if(quote.getQuoteDocs() !=null && quote.getQuoteDocs().size() >0){
				for(ScQuoteDoc doc : quote.getQuoteDocs()) {
					
					if(doc.getSpecMaster().getItemTypeCode().getParameterCode().equals(ParameterConst.ITEM_TYPE_PRODUCT)) {
						 doc.setSpecMaster(getManagedBean().getProductTemplateSpecBean().getSpecMaster());
						 quoteDocs.add(doc);
					}
					else if(doc.getSpecMaster().getItemTypeCode().getParameterCode().equals(ParameterConst.ITEM_TYPE_SERVICE)) {
						 doc.setSpecMaster(getManagedBean().getServiceTemplateSpecBean().getSpecMaster());
						 quoteDocs.add(doc);
					}
				}
			} else {
				// tpl spec - product
				if(getManagedBean().getProductTemplateSpecBean().getSpecMaster() != null){
					if(getManagedBean().getProductTemplateSpecBean().getSpecMaster().getTplSpecQuestions() != null && 
							getManagedBean().getProductTemplateSpecBean().getSpecMaster().getTplSpecQuestions().size() >0 ){
						ScQuoteDoc quoteDoc = new ScQuoteDoc();
						 TplSpecMaster specMaster = getManagedBean().getProductTemplateSpecBean().getSpecMaster();
						 if(quote.getQuoteZone() != null){
							 specMaster.setTplSpecZone(quote.getQuoteZone());
						 }
						quoteDoc.setSpecMaster(specMaster);
						quoteDoc.setQuote(quote);
						quoteDocs.add(quoteDoc);
						quote.setQuoteDocs(quoteDocs);
					}
				}
				// tpl spec - service
				if(getManagedBean().getServiceTemplateSpecBean().getSpecMaster() != null){
					if(getManagedBean().getServiceTemplateSpecBean().getSpecMaster().getTplSpecQuestions() != null && 
							getManagedBean().getServiceTemplateSpecBean().getSpecMaster().getTplSpecQuestions().size() >0 ){
						ScQuoteDoc quoteDoc = new ScQuoteDoc();
						 TplSpecMaster specMaster = getManagedBean().getServiceTemplateSpecBean().getSpecMaster();
						 if(quote.getQuoteZone() != null){
							 specMaster.setTplSpecZone(quote.getQuoteZone());
						 }
						quoteDoc.setSpecMaster(specMaster);
						quoteDoc.setQuote(quote);
						quoteDocs.add(quoteDoc);
						quote.setQuoteDocs(quoteDocs);
					}
				}
			}
						
			FacesScopes facesScopes = new FacesScopes();
			String token = facesScopes.getBPMAuthenticationCtxToken();
			reqDTO.setToken(token);
			reqDTO.setSubmit(Boolean.FALSE);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setOrgProfileId(getManagedBean().getRequestNote().getPmOrgProfile().getOrgProfileId());
			reqDTO.setQuoteBidDtl(quote);
			QuoteResDTO quoteResDTO = null;
			if(outcome.isEmpty()){
				quoteResDTO = quoteSO.saveQuoteDtl(reqDTO);
			} else if(BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(outcome)){
				quoteResDTO = quoteSO.updateSimpleQuote(reqDTO);
			}
			if (quoteResDTO.isSuccessful()) {
				ScQuote scQuote = quoteResDTO.getQuoteBidDtl();
				successMessage(MessageConst.INFO_700025,ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_SIMPLE_QUOTE,scQuote.getQuoteNo());
				// clear SQ data
				super.renewSesionObject();
			} else{
				throw new EpException("Failed.");
			}
		}  finally {
			quoteSO = null;
		}
		return null;
	}
	
	/**
	 * submitSimpleQuote method to submit SQ
	 */
	public String submitSimpleQuote(String outcome) {
		debugLogger("submitSimpleQuote");
		IQuoteSO quoteSO = null;
		try {
			quoteSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQuoteSO.class);
			QuoteReqDTO reqDTO = new QuoteReqDTO();
			IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			ScQuote quote = getManagedBean().getScQuote();
			
			if(quote.getRequestItems() == null){
				List<ScRequestItem> requestItems = new ArrayList<ScRequestItem>();
				ScRequestItem requestItem = new ScRequestItem();
				requestItem.setQuote(quote);
				requestItems.add(requestItem);
				quote.setRequestItems(requestItems);
			}
			if(quote.getRequestNote() == null){
				ScRequestNote requestNote = getManagedBean().getRequestNote();
				requestNote.setRequestNoteNo(directPurchaseSO.generateRequestNoteNo());
				requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SQ);
				requestNote.setWorkFlowStatuses(null);
				requestNote.setQuote(quote);
				quote.setRequestNote(requestNote);
			}
			
				parameterReqDTO.setParameterType(ParameterTypeConst.PROCUREMENT_TYPE);
				parameterReqDTO.setParameterCode(getManagedBean().getScQuote().getProcurementType().getParameterCode());
				ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
				quote.setProcurementType(resDTO.getPmParameter());
			
			if(getManagedBean().getLocationLevel() != null){
				parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO.setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				parameterReqDTO.setParameterCode(getManagedBean().getLocationLevel());
				 resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
				quote.setZoneLocatonLevel(resDTO.getPmParameter());
			}
			
			// justification type
			parameterReqDTO = new ParameterReqDTO();
			if (quote.getProcurementType().getParameterCode()
					.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
				parameterReqDTO.setParameterType(ParameterTypeConst.JUSTIFICATION_TYPE_ADHOC);
				
				if(quote.getJustificationType().getParameterCode() !=null){
					parameterReqDTO.setParameterCode(quote.getJustificationType().getParameterCode());
				} else{
					throw new EpException("Justification is madatory.");
				}
				
				resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
				quote.setJustificationType(resDTO.getPmParameter());
			} else if (quote.getProcurementType().getParameterCode()
					.equalsIgnoreCase(ParameterConst.PROC_TYPE_EMERGENCY)) {
				parameterReqDTO.setParameterType(ParameterTypeConst.JUSTIFICATION_TYPE_EMERGENCY);
				
				if(quote.getJustificationType().getParameterCode() !=null){
					parameterReqDTO.setParameterCode(quote.getJustificationType().getParameterCode());
				} else{
					throw new EpException("Justification is madatory.");
				}
				
				resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
				quote.setJustificationType(resDTO.getPmParameter());

			} else{
				quote.setJustificationType(null);
			}
			
			// zone details
			if(!getManagedBean().getZoneTrees().isEmpty()){
				quote.setQuoteZone(getQuoteZones(getManagedBean().getZoneTrees()));
			}
			
			//by location
			if(!getManagedBean().getLocationTrees().isEmpty()){

				if(quote.getLocationStateCriterias() == null || quote.getLocationStateCriterias().isEmpty()){
					quote.setLocationStateCriterias(getLocationStateCriterias(getManagedBean().getLocationTrees(), quote));	
				}
				if(quote.getLocationDivisionCriterias() == null || quote.getLocationDivisionCriterias().isEmpty()){
					quote.setLocationDivisionCriterias(getLocationDivisionCriterias(getManagedBean().getLocationTrees(), quote));
				}
				if(quote.getLocationDistrictCriterias() == null || quote.getLocationDistrictCriterias().isEmpty()){
					quote.setLocationDistrictCriterias(getLocationDistrictCriterias(getManagedBean().getLocationTrees(), quote));
				}
			}

			// by business nature
			if(getManagedBean().getBusinessNatureCriterias() != null && getManagedBean().getBusinessNatureCriterias().size() >0){
				quote.setBusinessNatureCriterias(getManagedBean().getBusinessNatureCriterias());
			}
			
			List<ScQuoteSupplier> suppliers = new ArrayList<ScQuoteSupplier>();
			// by supplier 
			if(getManagedBean().getSupplierList() != null){
				suppliers.addAll(getManagedBean().getSupplierList());
			}
			
			// panel item
			if(getManagedBean().getPanelSuppliers() != null){
				suppliers.addAll(getManagedBean().getPanelSuppliers());
			}
			// govt sellers
			if(getManagedBean().getGovtSellers() != null){
				suppliers.addAll(getManagedBean().getGovtSellers());
			}
			if(suppliers != null){
				quote.setScQuoteSuppliers(suppliers);
			}
			
			// remarks
			quote.setRemarks(initializeRemarks(quote));
			if(quote.getRemarks() != null ){
				for (int i = 0; i < quote.getRemarks().size(); i++) {
					ScSQRemark remark = quote.getRemarks().get(i);
					if(StringUtils.isBlank(remark.getRemark())){
						quote.getRemarks().remove(remark);
					}	
				} 
			}
			quote.setWorkflowStatuses(null);

			// product and service
			List<ScQuoteDoc> quoteDocs = new ArrayList<ScQuoteDoc>();
			if(quote.getQuoteDocs() !=null && quote.getQuoteDocs().size() >0){
				for(ScQuoteDoc doc : quote.getQuoteDocs()) {
					
					if(doc.getSpecMaster().getItemTypeCode().getParameterCode().equals(ParameterConst.ITEM_TYPE_PRODUCT)) {
						 doc.setSpecMaster(getManagedBean().getProductTemplateSpecBean().getSpecMaster());
						 quoteDocs.add(doc);
					}
					else if(doc.getSpecMaster().getItemTypeCode().getParameterCode().equals(ParameterConst.ITEM_TYPE_SERVICE)) {
						 doc.setSpecMaster(getManagedBean().getServiceTemplateSpecBean().getSpecMaster());
						 quoteDocs.add(doc);
					}
				}
			} else {
				// tpl spec - product
				if(getManagedBean().getProductTemplateSpecBean().getSpecMaster() != null){
					if(getManagedBean().getProductTemplateSpecBean().getSpecMaster().getTplSpecQuestions() != null && 
							getManagedBean().getProductTemplateSpecBean().getSpecMaster().getTplSpecQuestions().size() >0 ){
						ScQuoteDoc quoteDoc = new ScQuoteDoc();
						 TplSpecMaster specMaster = getManagedBean().getProductTemplateSpecBean().getSpecMaster();
						 if(quote.getQuoteZone() != null){
							 specMaster.setTplSpecZone(quote.getQuoteZone());
						 }
						quoteDoc.setSpecMaster(specMaster);
						quoteDoc.setQuote(quote);
						quoteDocs.add(quoteDoc);
						quote.setQuoteDocs(quoteDocs);
					}
				}
				// tpl spec - service
				if(getManagedBean().getServiceTemplateSpecBean().getSpecMaster() != null){
					if(getManagedBean().getServiceTemplateSpecBean().getSpecMaster().getTplSpecQuestions() != null && 
							getManagedBean().getServiceTemplateSpecBean().getSpecMaster().getTplSpecQuestions().size() >0 ){
						ScQuoteDoc quoteDoc = new ScQuoteDoc();
						 TplSpecMaster specMaster = getManagedBean().getServiceTemplateSpecBean().getSpecMaster();
						 if(quote.getQuoteZone() != null){
							 specMaster.setTplSpecZone(quote.getQuoteZone());
						 }
						quoteDoc.setSpecMaster(specMaster);
						quoteDoc.setQuote(quote);
						quoteDocs.add(quoteDoc);
						quote.setQuoteDocs(quoteDocs);
					}
				}
			}
			
	
			FacesScopes facesScopes = new FacesScopes();
			String token = facesScopes.getBPMAuthenticationCtxToken();
			reqDTO.setToken(token);
			reqDTO.setOrgProfileId(getManagedBean().getRequestNote().getPmOrgProfile().getOrgProfileId());
			reqDTO.setQuoteBidDtl(quote);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			QuoteResDTO quoteResDTO = null;
			if(outcome.isEmpty()){
				reqDTO.setSubmit(Boolean.TRUE);
				quoteResDTO = quoteSO.saveQuoteDtl(reqDTO);
			} else if(BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome) || BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)){
				reqDTO.setOutcome(outcome);
				quoteResDTO = quoteSO.updateSimpleQuote(reqDTO);
			} 
			if (quoteResDTO.isSuccessful()) {
				ScQuote scQuote = quoteResDTO.getQuoteBidDtl();
				if(BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome) || outcome.isEmpty()){
					successMessage(MessageConst.INFO_700026,ParameterTypeConst.DOCUMENT_TYPES,ParameterConst.DOCTYPE_SIMPLE_QUOTE,scQuote.getQuoteNo());
				} else  if(BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)){
					successMessage(MessageConst.INFO_700010,ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_SIMPLE_QUOTE,scQuote.getQuoteNo());
				}
				 // clear SQ data
				 super.renewSesionObject();
				
			} else {
				throw new EpException("Failed.");
			}
		}  finally {
			quoteSO = null;
		}
		return null;
	}
	
	   public void approverAction(String outcome) {
		   IQuoteSO quoteSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQuoteSO.class);
		   try {
			   QuoteReqDTO reqDTO = new QuoteReqDTO();
			   reqDTO.setQuoteBidDtl(getManagedBean().getScQuote());
			   FacesScopes facesScopes = new FacesScopes();
			   String token = facesScopes.getBPMAuthenticationCtxToken();
			   reqDTO.setToken(token);
			   reqDTO.setOutcome(outcome);
			   reqDTO.setTaskId(getManagedBean().getTaskId());
			   QuoteResDTO resDTO = quoteSO.updateSimpleQuote(reqDTO);

			   if(resDTO.isSuccessful()){
				   if(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)){
					   successMessage(MessageConst.INFO_700009, ParameterTypeConst.DOCUMENT_TYPES,  ParameterConst.DOCTYPE_SIMPLE_QUOTE, getManagedBean().getScQuote().getQuoteNo());

				   } else if(BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)){
					   successMessage(MessageConst.INFO_700011, ParameterTypeConst.DOCUMENT_TYPES,  ParameterConst.DOCTYPE_SIMPLE_QUOTE, getManagedBean().getScQuote().getQuoteNo());

				   } else if(BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)){
					   successMessage(MessageConst.INFO_700008, ParameterTypeConst.DOCUMENT_TYPES,  ParameterConst.DOCTYPE_SIMPLE_QUOTE, getManagedBean().getScQuote().getQuoteNo());

				   }else if(BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)){
					   successMessage(MessageConst.INFO_700010, ParameterTypeConst.DOCUMENT_TYPES,  ParameterConst.DOCTYPE_SIMPLE_QUOTE, getManagedBean().getScQuote().getQuoteNo());
				   }
			   } else {
				   throw new EpException("Failed.");
			   }

		   } finally {
			   quoteSO = null;
		   }
	   } 

	
	 public void byLocAndBusinessEvent() {
		 debugLogger("byBusinessEvent");
		 if (getManagedBean().isByLocality()) {
			 getManagedBean().setTabActiveIndex(0);
		 } 
		 if (getManagedBean().isByBusinessNature()) {
			 getManagedBean().setTabActiveIndex(1);
		 }
	 }

	/**
	 * initializing remarks in simpleQuote
	 */
	private List<ScSQRemark> initializeRemarks(ScQuote scQuote) {
		IStatusSO iStatusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		List<ScSQRemark> results = scQuote.getRemarks();
		if(results == null){
			results = new ArrayList<ScSQRemark>();
		}
		FacesScopes facesScopes = new FacesScopes();
		if (results.size() == 0
				|| !compareUserName(results.get(0).getCreatedUser().getLoginId())) {
			ScSQRemark remark = new ScSQRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setCreatedUser(facesScopes.getUserSessionBean());
			PmStatus status = new PmStatus();
			status.setStatusId(DPStatusConstants.SQ_PENDING_SUBMISSION);
		    StatusReqDTO statusReqDTO = new StatusReqDTO();
		    if (status != null) {
		     statusReqDTO.setStatusId(status.getStatusId());
		     StatusResDTO statusResDTO = iStatusSO.retrieveStatusByStatusId(statusReqDTO);
		     remark.setPmStatus(statusResDTO.getPmStatus());
		    }
		    remark.setPmStatus(status);
			remark.setQuote(scQuote);
			results.add(0, remark);
		}
		return results;
	}

	/**
	 * Event for procurement type drop down in Add to Cart
	 */
	public void procurementTypeEvent() {
		debugLogger("procurementTypeEvent");
		if (getManagedBean().getScQuote().getProcurementType().getParameterCode()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL)) {
			getManagedBean().setProcTypeOption(Boolean.FALSE);
			getManagedBean().setJustTypeOption(Boolean.FALSE);
			getManagedBean().setG2GFlag(Boolean.FALSE);
			getManagedBean().setLocAndBusinessFlag(Boolean.TRUE);

		} else if(getManagedBean().getScQuote().getProcurementType().getParameterCode()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)){
			getManagedBean().setProcTypeOption(Boolean.FALSE);
			getManagedBean().setJustTypeOption(Boolean.FALSE);
			getManagedBean().setG2GFlag(Boolean.TRUE);
			getManagedBean().setLocAndBusinessFlag(Boolean.FALSE);
			getManagedBean().setByBusinessNature(Boolean.FALSE);
			
		}else if (getManagedBean().getScQuote().getProcurementType().getParameterCode()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
			getManagedBean().setG2GFlag(Boolean.FALSE);
			getManagedBean().setLocAndBusinessFlag(Boolean.TRUE);
			getManagedBean().setJustDropdown(getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_ADHOC));
		} else if (getManagedBean().getScQuote().getProcurementType().getParameterCode()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_EMERGENCY)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
			getManagedBean().setG2GFlag(Boolean.FALSE);
			getManagedBean().setLocAndBusinessFlag(Boolean.TRUE);
			getManagedBean().setJustDropdown(getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_EMERGENCY));
		}
		
		if(getManagedBean().getScQuote().isPanel()){
			getManagedBean().setLocAndBusinessFlag(Boolean.FALSE);
			getManagedBean().setG2GFlag(Boolean.FALSE);
			getManagedBean().setByBusinessNature(Boolean.FALSE);
		}
		
		if(getManagedBean().getScQuote().getJustificationType() == null){
			getManagedBean().getScQuote().setJustificationType(new PmParameter());	
		}
		
	}
	
	/**
	 * Adding new row for BusinessNature in SQ
	 */
	public void addBusinessNature() {
		debugLogger("addBusinessNature");
		ScBusinessNatureQuoteCriteria businessNatureCriteria = new ScBusinessNatureQuoteCriteria();
		PmBusinessNature businessNature = new PmBusinessNature();
		businessNatureCriteria.setPmBusinessNature(businessNature);
		try {
			List<ScBusinessNatureQuoteCriteria> businessNatureCriterias = getManagedBean()
					.getScQuote().getBusinessNatureCriterias();
			if (null == businessNatureCriterias) {
				businessNatureCriterias = new ArrayList<ScBusinessNatureQuoteCriteria>();
			}
			businessNatureCriterias.add(businessNatureCriteria);
			this.getManagedBean().setBusinessNatureCriterias(businessNatureCriterias);
		} finally {
		}
	}
	
	/**
	 * deleteBusinessNature method for deleting business nature
	 */	
	public void deleteBusinessNature() {
		debugLogger("deleteBusinessNature");
		if (getManagedBean().getSelectedBusinessNature() != null) {
			for (ScBusinessNatureQuoteCriteria natureQuoteCriteria : getManagedBean()
					.getSelectedBusinessNature()) {
				if(getManagedBean().getBusinessNatureCriterias().contains(natureQuoteCriteria)){
					List<ScBusinessNatureQuoteCriteria>  businessNatureQuoteCriterias = getManagedBean().getBusinessNatureCriterias();
					businessNatureQuoteCriterias.remove(natureQuoteCriteria);
					getManagedBean().setBusinessNatureCriterias(businessNatureQuoteCriterias);
				}
			}
		}
	}
	
	/**
	 * deleteSupplier method for deleting supplier in supplier list
	 */	
	public void deleteSupplier() {
		debugLogger("deleteSupplier");
		if (getManagedBean().getSelectedSupplierList() != null) {
			for (ScQuoteSupplier supplier : getManagedBean()
					.getSelectedSupplierList()) {
				if(getManagedBean().getSupplierList().contains(supplier)){
					List<ScQuoteSupplier>  supplierList = getManagedBean().getSupplierList();
					supplierList.remove(supplier);
					getManagedBean().setSupplierList(supplierList);
				}
			}
		}
	}
	
	/**
	 * deleteGovtSeller method for deleting govt seller
	 */	
	public void deleteGovtSeller() {
		debugLogger("deleteGovtSeller");
		if (getManagedBean().getSelectedGovtSellers() != null) {
			for (ScQuoteSupplier govtSeller : getManagedBean()
					.getSelectedGovtSellers()) {
				if(getManagedBean().getGovtSellers().contains(govtSeller)){
					List<ScQuoteSupplier>  govtSuppliers = getManagedBean().getGovtSellers();
					govtSuppliers.remove(govtSeller);
					getManagedBean().setGovtSellers(govtSuppliers);
				}
			}
		}
	}
	
	/**
	 * deletePanelSupplier method for deleting panel item 
	 */
	public void deletePanelSupplier() {
		debugLogger("deletePanelSupplier");
		if (getManagedBean().getSelectedPanelSuppliers() != null) {
			for (ScQuoteSupplier scQuoteSupplier : getManagedBean()
					.getSelectedPanelSuppliers()) {
				if(getManagedBean().getPanelSuppliers().contains(scQuoteSupplier)){
					List<ScQuoteSupplier>  suppliers = getManagedBean().getPanelSuppliers();
					suppliers.remove(scQuoteSupplier);
					getManagedBean().setPanelSuppliers(suppliers);
				}
			}
		}
	}	
	
   /**
    * showZonePopup method for zone popup in simple quote
    */
	public void showZonePopup() {
	  String locationLevel = this.getManagedBean().getLocationLevel();
		if (locationLevel == null) {
			locationLevel = ParameterConst.LOCATION_LEVEL_DISTRICT;
		}
		getManagedBean().setRootZoneTree(
				LocationSingleton.getInstance().buildLocationPopupTree(getManagedBean().getDefaultCountryId(), null,
						null, null, null, locationLevel, TplSpecZoneLoc.class));
	}
	
   /**
	 * showLocationPopup method for location popup in simple quote
    */
	public void showLocationPopup() {
		String locationLevel = this.getManagedBean().getLocationLevel();
		if (locationLevel == null) {
			locationLevel = ParameterConst.LOCATION_LEVEL_DISTRICT;
		}
	
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationPopupTree(getManagedBean().getDefaultCountryId(), null,
						null, null, null, locationLevel, TplSpecZoneLoc.class));
	}
	
	/**
	 * updateZone for zone selection  in simple quote
    */
	public void updateZone() {
		FacesScopes facesScopes = new FacesScopes();
		List<TplSpecZoneLoc> locationDTOs = new ArrayList<TplSpecZoneLoc>();
		if (getManagedBean().getSelectedNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedNodes()) {
				if (treeNode.getParent().isSelected()) {
					List<TplSpecZoneLoc> locationDTO = getLocations(treeNode);
					locationDTOs.addAll(locationDTO);
				} else{
					List<TplSpecZoneLoc> locationDTO = getLocations(treeNode);
					locationDTOs.addAll(locationDTO);
				}
			}
		}
		
		TplSpecZoneDtl tplSpecZoneDtl = new TplSpecZoneDtl();
		ScQuoteZone quoteZone = getManagedBean().getScQuote().getQuoteZone();
		quoteZone.setScQuote(getManagedBean().getScQuote());
		tplSpecZoneDtl.setTplSpecZone(quoteZone);
		tplSpecZoneDtl.setZoneName(getManagedBean().getZoneName());
		tplSpecZoneDtl.setChangedBy(facesScopes.getUserSessionBean().getUserId());
		tplSpecZoneDtl.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		tplSpecZoneDtl.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		getManagedBean().setZoneName(null);

		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTO(locationDTOs);


		if (getManagedBean().getZoneIndex() == -1) {
			getManagedBean().getZoneTrees().add(
					LocationSingleton.getInstance().buildLocationTree(tplSpecZoneDtl, states,
							districts, divisions, cities,TplSpecZoneLoc.class));

		} else if (getManagedBean().getZoneIndex() >= 0) {

			getManagedBean().getZoneTrees().remove(getManagedBean().getZoneIndex());
			getManagedBean().getZoneTrees().add(
					getManagedBean().getZoneIndex(),
					LocationSingleton.getInstance().buildLocationTree(tplSpecZoneDtl, states,
							districts, divisions, cities ,TplSpecZoneLoc.class));
		}
		this.getManagedBean().setZoneIndex(-1);
	}
	
   /**
	 * updateZone for location selection  in simple quote
    */
	public void updateLocation() {
		List<TplSpecLoc> locationDTOs = new ArrayList<TplSpecLoc>();
		if (getManagedBean().getSelectedLocNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedLocNodes()) {
				if (treeNode.getParent().isSelected()) {
					List<TplSpecZoneLoc> locationDTO = getAllLocations(treeNode);
					locationDTOs.addAll(locationDTO);
				} else{
					List<TplSpecZoneLoc> locationDTO = getAllLocations(treeNode);
					locationDTOs.addAll(locationDTO);
				}
			}
		}
		TplSpecZoneDtl zone = null;
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTO(locationDTOs);

		if (getManagedBean().getLocIndex() == -1) {
			
			getManagedBean().getLocationTrees().add(
					LocationSingleton.getInstance().buildLocationTree(zone, states,
							districts, divisions, cities,TplSpecZoneLoc.class));

			
		} else if (getManagedBean().getLocIndex() >= 0) {
			getManagedBean().getLocationTrees().remove(getManagedBean().getLocIndex());
			getManagedBean().getLocationTrees().add(
					getManagedBean().getLocIndex(),
					LocationSingleton.getInstance().buildLocationTree(zone, states,
							districts, divisions, cities ,TplSpecZoneLoc.class));
		}
		this.getManagedBean().setLocIndex(-1);
	}
	
   private List<TplSpecZoneLoc> getLocations(TreeNode zoneLocation) {
			List<TplSpecZoneLoc> results = new ArrayList<TplSpecZoneLoc>();
			if (zoneLocation.isLeaf()) {
				TplSpecZoneLoc locationDTO = (TplSpecZoneLoc) zoneLocation.getData();
				results.add(locationDTO);
			} else {
				for (TreeNode childrendLocationNode : zoneLocation.getChildren()) {
					results.addAll(getLocations(childrendLocationNode));
				}
			}
			return results;
		}
		
	private ScQuoteZone getZone(TreeNode zoneLocation) {
		ScQuoteZone result = null;
		if (zoneLocation.getData() instanceof LocationDTO) {
			LocationDTO locationDTO = (LocationDTO) zoneLocation.getData();
			if (locationDTO.getZone() != null) {
				//result = locationDTO.getZone();
			} else {
				if (zoneLocation.getChildren() != null
						&& zoneLocation.getChildren().size() > 0) {
					for (TreeNode childLocationNode : zoneLocation
							.getChildren()) {
						result = getZone(childLocationNode);
					}
				}
			}
		} else {
			if (zoneLocation.getChildren() != null
					&& zoneLocation.getChildren().size() > 0) {
				for (TreeNode childLocationNode : zoneLocation.getChildren()) {
					result = getZone(childLocationNode);
				}
			}
		}
		return result;
	}
	
   /**
	* editZone Method for zone in simple quote
	*/
	public void editZone(TreeNode rootZoneLocation, int zoneIndex) {
		List<TplSpecZoneLoc> locationDTOs = getLocations(rootZoneLocation);
		if(rootZoneLocation.getChildCount() >0){
			for (TreeNode rootNode : rootZoneLocation.getChildren()) {
				TplSpecZoneDtl tplSpecZoneDtl = (TplSpecZoneDtl) rootNode.getData();
				getManagedBean().setZoneName(tplSpecZoneDtl.getZoneName());
			}
		}
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTO(locationDTOs);
		
		getManagedBean().setRootZoneTree(
				LocationSingleton.getInstance().buildLocationPopupTree(getManagedBean().getDefaultCountryId(),
						states, districts, divisions, cities, ParameterConst.LOCATION_LEVEL_DISTRICT,TplSpecZoneLoc.class));
		int i = zoneIndex;
		this.getManagedBean().setZoneIndex(i);
	}
	
	/**
     * editLocation Method for location in simple quote
	*/
	public void editLocation(TreeNode rootZoneLocation, int LocIndex) {
		List<TplSpecZoneLoc> locationDTOs  = getAllLocations(rootZoneLocation);
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTO(locationDTOs);
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationPopupTree(getManagedBean().getDefaultCountryId(),
						states, districts, divisions, cities, ParameterConst.LOCATION_LEVEL_DISTRICT,TplSpecZoneLoc.class));
		int i = LocIndex;
		this.getManagedBean().setLocIndex(i);
		
	}

    private List<TplSpecZoneLoc> getAllLocations(TreeNode treeNode) {
    	List<TplSpecZoneLoc> results = new ArrayList<TplSpecZoneLoc>();
    	if(treeNode.getChildCount() > 0){
    		for (TreeNode stateNode : treeNode.getChildren()) {
    			// state location
    			TplSpecZoneLoc locationDTO = (TplSpecZoneLoc) stateNode.getData();
    			results.add(locationDTO);

    			for (TreeNode divNode : stateNode.getChildren()) {
    				// division location
    				TplSpecZoneLoc divLocationDTO = (TplSpecZoneLoc) divNode.getData();
    				results.add(divLocationDTO);

    				for (TreeNode distNode : divNode.getChildren()) {
    					//district location
    					TplSpecZoneLoc distLocationDTO = (TplSpecZoneLoc) distNode.getData();
    					results.add(distLocationDTO);
    				}
    			}
    		}
    	} else{
    		TplSpecZoneLoc parentNode = (TplSpecZoneLoc) treeNode.getData();
    		results.add(parentNode);
    	}
    	return results;
    }
	

	private List<PmState> getPmStates(List<TreeNode> treeNodes) {
		List<PmState> results = new ArrayList<PmState>();
		for (TreeNode rootNode : treeNodes) {
			for (TreeNode treeNode : rootNode.getChildren()) {
					if (treeNode.getType().equals("state")) {
						TplSpecZoneLoc locationDTO = (TplSpecZoneLoc) treeNode.getData();
						if (locationDTO.getPmState() != null) {

							results.add(locationDTO.getPmState());
						}
					}
			}
		}
		return results;
	}
	
	private List<PmDivision> getPmDivisions(List<TreeNode> treeNodes) {
		List<PmDivision> results = new ArrayList<PmDivision>();
		for (TreeNode rootNode : treeNodes) {
		for (TreeNode treeNode : rootNode.getChildren()) {
			for (TreeNode childNode : treeNode.getChildren()) {
					if (childNode.getType().equals("division")) {
						TplSpecZoneLoc locationDTO = (TplSpecZoneLoc) childNode.getData();
						if (locationDTO.getPmDivision() != null) {
							results.add(locationDTO.getPmDivision());
						}
					}
				}
		}
		}
		return results;
	}
	
	private List<PmDistrict> getPmDistricts(List<TreeNode> treeNodes) {
		List<PmDistrict> results = new ArrayList<PmDistrict>();
		for (TreeNode treeNode : treeNodes) {
			if (treeNode.isLeaf()) {
				TplSpecZoneLoc locationDTO = (TplSpecZoneLoc) treeNode.getData();
				if (locationDTO.getPmDistrict() != null) {
					results.add(locationDTO.getPmDistrict());
				}
			} else {
				if (treeNode.getChildren() != null) {
					results.addAll(getPmDistricts(treeNode.getChildren()));
				}
			}
		}
		return results;
	}

	

	private	List<ScLocationStateCriteria>	getLocationStateCriterias(List<TreeNode> treeNodes ,ScQuote quote){
			List<ScLocationStateCriteria>	results = new ArrayList<ScLocationStateCriteria>();
			List<PmState>	states	=	getPmStates(treeNodes);
			if(states != null && states.size() > 0){
				for(PmState state: states){
					ScLocationStateCriteria stateCriteria	=	new ScLocationStateCriteria();
					//stateCriteria.setCriteriaValue(state.getStateId());
					stateCriteria.setPmState(state);
					stateCriteria.setQuote(quote);
					results.add(stateCriteria);
				}
			}
			return results;
	  }
		
	private	List<ScLocationDivisionCriteria> getLocationDivisionCriterias(List<TreeNode> treeNodes ,ScQuote quote){
			List<ScLocationDivisionCriteria>	results = new ArrayList<ScLocationDivisionCriteria>();
			List<PmDivision>	divisions	=	getPmDivisions(treeNodes);
			if(divisions != null && divisions.size() > 0){
				for(PmDivision division: divisions){
					ScLocationDivisionCriteria  divisionCriteria = new ScLocationDivisionCriteria();
					//divisionCriteria.setCriteriaValue(division.getDivisionId());
					divisionCriteria.setPmDivision(division);
					divisionCriteria.setQuote(quote);
					results.add(divisionCriteria);
				}
			}
			return results;
	  }	
		
	private List<ScLocationDistrictCriteria> getLocationDistrictCriterias(List<TreeNode> treeNodes ,ScQuote quote){
			List<ScLocationDistrictCriteria>	results = new ArrayList<ScLocationDistrictCriteria>();
			for (TreeNode treeNode : treeNodes) {
			List<PmDistrict>	districts	=	getPmDistricts(treeNode.getChildren());
			if(districts != null && districts.size() > 0){
				for(PmDistrict district: districts){
					ScLocationDistrictCriteria districtCriteria	=	new ScLocationDistrictCriteria();
					//districtCriteria.setCriteriaValue(district.getDistrictId());
					districtCriteria.setQuote(quote);
					districtCriteria.setPmDistrict(district);
					results.add(districtCriteria);
				}
			}
			}
			return results;
	  }	
	
	/**
	 * To delete the selected TreeNode from the zone
	 */
	public void deleteZoneTreeNode() {
		TreeNode selectedTreeNode = this.getManagedBean().getSelectedTreeNode();
		if (null == selectedTreeNode) {
			throw new EpException("Please select At least one Zone");
		}
		TplSpecZoneDtl zoneDtls = (TplSpecZoneDtl) selectedTreeNode
				.getChildren().get(0).getData();
		if (null != selectedTreeNode) {
			List<TreeNode> zoneTrees = this.getManagedBean()
					.getZoneTrees();
			for (TreeNode node : zoneTrees) {
				if (zoneDtls.getZoneName().equalsIgnoreCase(
						((TplSpecZoneDtl) node.getChildren().get(0).getData())
						.getZoneName())) {
					zoneTrees.remove(node);
					break;
				}
			}
			this.getManagedBean().setZoneTrees(zoneTrees);
		}
	}
	
	/**
	 * To delete the selected TreeNode from the location
	 */
	public void deleteLocationTreeNode() {
		TreeNode selectedTreeNode = this.getManagedBean().getSelectedTreeNode();
		if (null == selectedTreeNode) {
			throw new EpException("Please select At least one state");
		}
		TplSpecZoneLoc zoneLoc = (TplSpecZoneLoc) selectedTreeNode
				.getChildren().get(0).getData();
		if (null != selectedTreeNode) {
			List<TreeNode> locTrees = this.getManagedBean()
					.getLocationTrees();
			for (TreeNode node : locTrees) {
				if (zoneLoc == (((TplSpecZoneLoc) node.getChildren().get(0).getData()))) {
					locTrees.remove(node);
					break;
				}
			}
			this.getManagedBean().setLocationTrees(locTrees);
		}
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
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return items;
	}

	/**
	 * Event for justification type drop down in simpleQuote
	 */
	public void justificationTypeEvent() {
		debugLogger("justificationTypeEvent");
		/*if(getManagedBean().getScQuote().getJustificationType().getParameterCode() != null){
			if (getManagedBean().getScQuote().getJustificationType().getParameterCode()
					.equalsIgnoreCase(ParameterTypeConst.JUSTIFICATION_TYPE_OTHER)) {
				getManagedBean().setJustTypeOption(Boolean.TRUE);
			} else {
				getManagedBean().setJustTypeOption(Boolean.FALSE);
			}
		}*/
	}

	/**
	 * Page navigation to catalogue search page in simpleQuote
	 */
	public String backToPervious() {
		debugLogger("backToPervious");
		// super.renewSesionObject();
		
		return "catalogueSearch";
	}

	 public void  getCurrentStatus(Long statusId) {
			if (statusId != null) {
				 getManagedBean().setSimpleQuoteStatus(getStatusDescById(statusId));
			 	
			}
		  }

	 public SelectItem[] getLocationLevelDropdown() {
		 List<String> parameterCodes = new ArrayList<String>();
		 parameterCodes.add(ParameterConst.LOCATION_LEVEL_STATE);
		 parameterCodes.add(ParameterConst.LOCATION_LEVEL_DIVISION);
		 parameterCodes.add(ParameterConst.LOCATION_LEVEL_DISTRICT);

		 SelectItem[] items = null;
		 try {
			 items = new SelectItem[parameterCodes.size() + 1];
			 items[0] = new SelectItem(null, "- Select One -");
			 int i = 1;
			 for (String parameterCode : parameterCodes) {
				 List<PmParameterDesc> localParamDesc =
						 retrieveParameterDescByType
						 (ParameterTypeConst.LOCATION_LEVEL); 
				 for (PmParameterDesc
						 paramDesc : localParamDesc) { 
					 if(paramDesc.getPmParameter().getParameterCode().equals(parameterCode)) {
						 items[i++] = new SelectItem(paramDesc.getPmParameter().getParameterCode(), 
								 paramDesc.getCodeName());
					 }
				 }
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
			 FacesContext.getCurrentInstance().addMessage(
					 null,
					 new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							 e.getMessage()));
		 }
		 return items;
	 }
	 public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		 return ParameterHelper.getInstance().getParameterDescList(type);

	 }
	 
	/*
	 * public void durationEvent(){ try {
	 * if(getManagedBean().getScQuote().getStartDate() != null &&
	 * getManagedBean().getScQuote().getEndDate() != null){ String sdate =
	 * DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT,
	 * getManagedBean().getScQuote().getStartDate()); String edate =
	 * DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT,
	 * getManagedBean().getScQuote().getEndDate());
	 * getManagedBean().setDuration(
	 * hoursDifference(DateUtils.convertStringToDate
	 * (CommonConstants.DEFAULT_DATE_FORMAT, edate),
	 * DateUtils.convertStringToDate( CommonConstants.DEFAULT_DATE_FORMAT,
	 * sdate))); } } catch (Exception e) { e.printStackTrace(); } }
	 */
	/*
	 * private static int minsDifference(Date toDate, Date fromDate) { final int
	 * MILLI_TO_MINS = 1000 * 60; return (int) (toDate.getTime() -
	 * fromDate.getTime()) / MILLI_TO_MINS; }
	 * 
	 * private static int hoursDifference(Date toDate, Date fromDate) { final
	 * int MILLI_TO_HOURS = 1000 * 60 * 60; return (int) (toDate.getTime() -
	 * fromDate.getTime()) / MILLI_TO_HOURS; }
	 * 
	 * public static int daysBetween(Date toDate, Date fromDate) { final int
	 * MILLI_TO_DAYS = 1000 * 60 * 60 * 24; return (int) ((toDate.getTime() -
	 * fromDate.getTime()) / MILLI_TO_DAYS ); }
	 */
	 
	 
	 public void dateListener() throws Exception {
		 ScQuote quote = getManagedBean().getScQuote();
		 if (quote.getStartDate() != null &&  quote.getEndDate() != null){

			 if(quote.getEndDate().after(quote.getStartDate())){
				 getManagedBean().setDuration(getTimeDiff(quote.getEndDate(),  quote.getStartDate()));
			 } else{
				 throw new EpException("The End date must be greater than the start date");
			 }
		 }
	 }
		
	 public static double getTimeDiff(Date dateOne, Date dateTwo) {
		 long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
		 String hours = String.format("%d", TimeUnit.MILLISECONDS.toHours(timeDiff));
		 String mins = String.format("%d", (TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff))));
		 double time  = (double) Integer.valueOf(hours) + (double)(Integer.valueOf(mins))/60;
		
		 return time;
	 }
		
	/*
	 * public void calcDuration(Integer duration) throws Exception { ScQuote
	 * quote = getManagedBean().getScQuote(); if
	 * (getManagedBean().getScQuote().getStartDate() != null &&
	 * getManagedBean().getScQuote().getEndDate() != null){ Calendar startDate =
	 * Calendar.getInstance(); startDate.setTime(quote.getStartDate());
	 * startDate.add(Calendar.HOUR_OF_DAY, duration); Date endDate =
	 * startDate.getTime(); quote.setEndDate(endDate);
	 * getManagedBean().setScQuote(quote);
	 * getManagedBean().setDuration(hoursDifference(quote.getEndDate(),
	 * quote.getStartDate())); }
	 * 
	 * }
	 */
		
	public void tabMenuListener(Integer activeIndex) {
		debugLogger("tabMenuListener");
		System.out.println("active index :"+ getManagedBean().getActiveIndex());
		if(activeIndex.equals(0)){
			getManagedBean().setActiveIndex(0);
		} else if(activeIndex.equals(1)){
			getManagedBean().setActiveIndex(1);
		} else if(activeIndex.equals(2)){
			getManagedBean().setActiveIndex(2);
		} else if(activeIndex.equals(3)){
			getManagedBean().setActiveIndex(3);
		}
		
		// ptj locality address
		if(getManagedBean().getLocationTrees() == null || getManagedBean().getLocationTrees().isEmpty()){
			IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IAddressSO.class);
			AddressReqDTO reqDTO = new AddressReqDTO();
			if(getManagedBean().getRequestNote().getPmOrgProfile() != null){
				reqDTO.setOrgProfileId(getManagedBean().getRequestNote().getPmOrgProfile().getOrgProfileId());
				reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
				AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
				List<PmAddressType> list = resDTO.getAddressTypeList();
				if(list != null && list.size() >0){
					PmAddressType addressType = list.get(0);
					List<PmState> states = new ArrayList<PmState>();
					List<PmDivision> divisions = new ArrayList<PmDivision>();
					List<PmDistrict> districts = new ArrayList<PmDistrict>();
					
					if(addressType.getPmAddress().getPmDivision() != null){
						divisions.add(addressType.getPmAddress().getPmDivision());
					}
					
					if(addressType.getPmAddress().getPmDistrict() != null){
						districts.add(addressType.getPmAddress().getPmDistrict());
					}
					
					if(addressType.getPmAddress().getPmState() != null && districts.isEmpty() && divisions.isEmpty()){
						states.add(addressType.getPmAddress().getPmState());
					}
					
					getManagedBean().getLocationTrees().add(
							LocationSingleton.getInstance().buildLocationTree(null, states,
									districts, divisions, null,TplSpecZoneLoc.class));
				}
			}
		}
		procurementTypeEvent();
	}
	
	public void reset() {
		this.getManagedBean().setSearchQuoteNo(null);
		this.getManagedBean().setSearchTitle(null);
		this.getManagedBean().setSearchProcType(null);
		this.getManagedBean().setSearchFromCreatedDate(null);
		this.getManagedBean().setSearchToCreatedDate(null);
	}
	
	public void addCloneItemSpec() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		log.info("addCloneItemSpec");
		ITemplateSpecSO templateSpecSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.COMMON_COMPONENT,ITemplateSpecSO.class);
		TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
		TemplateSpecBean productTplSpecBean = getManagedBean().getProductTemplateSpecBean();
		TemplateSpecBean serviceTplSpecBean = getManagedBean().getServiceTemplateSpecBean();
		try {
		    if(getManagedBean().getSelectedQuote() != null) {
			   reqDTO.setSpecMaster(getManagedBean().getSelectedQuote().getQuoteDocs().get(0).getSpecMaster());
			   reqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
			   TemplateSpecResDTO resDTO = templateSpecSO.getTemplateMaster(reqDTO);
			   TplSpecMaster specMaster = resDTO.getSpecMaster();

			// product Tpl Spe
			if(ParameterConst.SQ_ITEM_LIST_PRODUCT.equalsIgnoreCase(specMaster.getItemTypeCode().getParameterCode())){
				TplSpecMaster cloneSpecMaster = (TplSpecMaster) BeanUtils.cloneBean(specMaster);
				cloneSpecMaster.setSpecMasterId(null);
				cloneSpecMaster.setTplSpecQuestions(cloneTplSpecQuetions(cloneSpecMaster));
				cloneSpecMaster.setBrandNew(Boolean.TRUE);
				productTplSpecBean.setSpecMaster(cloneSpecMaster);
				getManagedBean().setProductTemplateSpecBean(productTplSpecBean);

			   // service Tpl Spe
			} else  if(ParameterConst.SQ_ITEM_LIST_SERVICE.equalsIgnoreCase(specMaster.getItemTypeCode().getParameterCode())){
				TplSpecMaster cloneSpecMaster = (TplSpecMaster) BeanUtils.cloneBean(specMaster);
				cloneSpecMaster.setSpecMasterId(null);
				cloneSpecMaster.setTplSpecQuestions(cloneTplSpecQuetions(cloneSpecMaster));
				cloneSpecMaster.setBrandNew(Boolean.TRUE);
				serviceTplSpecBean.setSpecMaster(cloneSpecMaster);
				serviceTplSpecBean.setSpecMaster(specMaster);
				getManagedBean().setServiceTemplateSpecBean(serviceTplSpecBean);
			}
		   }
		} finally {
			templateSpecSO = null;
		}
	}
	
	private List<TplSpecQuestion>  cloneTplSpecQuetions(TplSpecMaster master) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException{
		List<TplSpecQuestion> questions = new ArrayList<TplSpecQuestion>();
		for (TplSpecQuestion tplSpecQuestion : master.getTplSpecQuestions()) {
			TplSpecQuestion question = (TplSpecQuestion) BeanUtils.cloneBean(tplSpecQuestion);
			question.setSpecQuestionId(null);
			question.setTplSpecMaster(master);
			question.setTplSpecAnswers(null);
			question.setTplSpecScorings(null);
			question.setTplSpecTierQties(null);
			question.setTplSpecEstimations(null);
			questions.add(question);
		}
		return questions;
	}
	
	public void cloneAction(String productOrService){
		if(productOrService != null){
			if(productOrService.equals(ParameterConst.SQ_ITEM_LIST_PRODUCT)){
				getManagedBean().setProductOrService(productOrService);
				
			} else if(productOrService.equals(ParameterConst.SQ_ITEM_LIST_SERVICE)){
				getManagedBean().setProductOrService(productOrService);
			}
		}
	}
	
	public void supplierListListener(List<SmSupplier> suppliers  , ScQuote quote){
		debugLogger("supplierListListener");
		List<ScQuoteSupplier> supplierList = quote.getScQuoteSuppliers();
		if(supplierList == null){
			supplierList = new ArrayList<ScQuoteSupplier>();
		}
		if (suppliers != null){
			for (SmSupplier supplier : suppliers) {
				ScQuoteSupplier quoteSupplier = new ScQuoteSupplier();
				quoteSupplier.setSmSupplier(supplier);
				quoteSupplier.setQuote(quote);

				if(getManagedBean().getSupplierList().isEmpty()){
					supplierList.add(quoteSupplier);
					getManagedBean().setSupplierList(supplierList);

				} else {
					List<Long> supplierIds = new ArrayList<Long>();
					for (ScQuoteSupplier scQuoteSupplier : getManagedBean().getSupplierList()) {
						supplierIds.add(scQuoteSupplier.getSmSupplier().getSupplierId());
					}
					if (supplierIds.size() > 0) {
						if(!supplierIds.contains(supplier.getSupplierId())){
							supplierList.add(quoteSupplier);
							getManagedBean().setSupplierList(supplierList);

						} else{
							//throw new EpException(MessageConst.INFO_700027, new String[]{"Already selected supplier.","Supplier List"});
						}
					}
				}
			}

		}
	}
	
	public void businessNatureListener(List<PmBusinessNature> businessNatures){
		debugLogger("businessNatureListener");
		List<ScBusinessNatureQuoteCriteria> natureQuoteCriterias = getManagedBean().getScQuote().getBusinessNatureCriterias();
		if(natureQuoteCriterias == null){
			natureQuoteCriterias = new ArrayList<ScBusinessNatureQuoteCriteria>();
		}
		if (businessNatures != null){
		   for (PmBusinessNature busiNature : businessNatures) {
			   ScBusinessNatureQuoteCriteria natureQuoteCriteria = new ScBusinessNatureQuoteCriteria();
			   natureQuoteCriteria.setPmBusinessNature(busiNature);
			   natureQuoteCriteria.setQuote(getManagedBean().getScQuote());
			   natureQuoteCriterias.add(natureQuoteCriteria);
			}
		   getManagedBean().setBusinessNatureCriterias(natureQuoteCriterias);
		}
	}
	
	public void govtSellerListener(List<PmOrgProfile> orgProfiles){
		debugLogger("govtSellerListener");
		List<ScQuoteSupplier> govtSuppliers = getManagedBean().getGovtSellers();
		if(govtSuppliers == null){
			govtSuppliers = new ArrayList<ScQuoteSupplier>();
		}
		if (orgProfiles != null){
		   for (PmOrgProfile orgProfile : orgProfiles) {
			   ScQuoteSupplier scQuoteSupplier = new ScQuoteSupplier();
			   scQuoteSupplier.setSmSupplier(orgProfile.getSmSupplier());
			   scQuoteSupplier.setQuote(getManagedBean().getScQuote());
			   govtSuppliers.add(scQuoteSupplier);
			}
		   getManagedBean().setGovtSellers(govtSuppliers);
		}
	}

	public void panelItemListener(List<CtPanelSupplier>	ctPanelSuppliers){
		debugLogger("panelItemListener");
		List<ScQuoteSupplier> panelSuppliers = getManagedBean().getPanelSuppliers();
		if(panelSuppliers == null){
			panelSuppliers = new ArrayList<ScQuoteSupplier>();
		}
		if (ctPanelSuppliers != null){
			for (CtPanelSupplier panelSupplier : ctPanelSuppliers) {
				ScQuoteSupplier scQuoteSupplier = new ScQuoteSupplier();
				scQuoteSupplier.setSmSupplier(panelSupplier.getSupplier());
				scQuoteSupplier.setQuote(getManagedBean().getScQuote());
				panelSuppliers.add(scQuoteSupplier);
			}
			getManagedBean().setPanelSuppliers(panelSuppliers);
		}
	}
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public String getSupplierListCriteria(){
		StringBuilder  criteria = new StringBuilder();
		StringBuilder  busiNatureCriteria = new StringBuilder();
		StringBuilder  stateCriteria = new StringBuilder();
		StringBuilder  divCriteria = new StringBuilder();
		StringBuilder  distCriteria = new StringBuilder();

		// Business Nature Criteria
		if(getManagedBean().getBusinessNatureCriterias() != null){
			for (ScBusinessNatureQuoteCriteria natureQuoteCriteria : getManagedBean().getBusinessNatureCriterias()) {
				if(natureQuoteCriteria.getPmBusinessNature() !=null){
					busiNatureCriteria.append(natureQuoteCriteria.getPmBusinessNature().getBusinessNatureId())
					.append(",");
				}
			}
		}

		if(busiNatureCriteria != null && busiNatureCriteria.length() > 0){
			busiNatureCriteria.deleteCharAt(busiNatureCriteria.lastIndexOf(","));
			busiNatureCriteria.append("^").append(SearchOperator.OPERATOR_IN.value());
			criteria.append("smSupplierBusinesses.pmBusinessNature.businessNatureId").append("^").append(busiNatureCriteria.toString())
			.append("|");
		} 

		// Locality Criteria
		List<TplSpecZoneLoc> locationDTOs = new ArrayList<TplSpecZoneLoc>();
		if (getManagedBean().getLocationTrees() != null) {
			for (TreeNode treeNode : getManagedBean().getLocationTrees()) {
				if(treeNode.getParent() != null){
					if (treeNode.getParent().isSelected()) {
						List<TplSpecZoneLoc> locationDTO = getLocations(treeNode);
						locationDTOs.addAll(locationDTO);
					}	
				}
			}
		}
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(locationDTOs);

		if(states != null && states.size() >0 ){
			for (PmState state : states) {
				stateCriteria.append(state.getStateId())
				.append(",");
			}
		}
		
		if(divisions != null && divisions.size() >0 ){
			for (PmDivision division : divisions) {
				divCriteria.append(division.getDivisionId())
				.append(",");
			}
		}

		if(districts != null && districts.size() >0 ){
			for (PmDistrict district : districts) {
				distCriteria.append(district.getDistrictId())
				.append(",");
			}
		}

		if(stateCriteria !=null && stateCriteria.length() > 0){
			stateCriteria.deleteCharAt(stateCriteria.lastIndexOf(","));
			stateCriteria.append("^").append(SearchOperator.OPERATOR_IN.value());
			criteria.append("smAppl.smCompanyBasic.smCompanyAddresses.smAddress.pmState.stateId").append("^").append(stateCriteria.toString())
			.append("|");
		} 

		if(divCriteria !=null && divCriteria.length() > 0){
			divCriteria.deleteCharAt(divCriteria.lastIndexOf(","));
			divCriteria.append("^").append(SearchOperator.OPERATOR_IN.value());
		} else{
			divCriteria.append("");
		}
		if(distCriteria !=null && distCriteria.length() > 0){
			distCriteria.deleteCharAt(distCriteria.lastIndexOf(","));
			distCriteria.append("^").append(SearchOperator.OPERATOR_IN.value());
			criteria.append("smAppl.smCompanyBasic.smCompanyAddresses.smAddress.pmDistrict.districtId").append("^").append(distCriteria.toString())
			.append("|");
		} 

		criteria.append("recordStatus^1");
		return criteria.toString();
	}
	
	/**
	 * render the view based on the task id
	 * page nav. to simple quote and request note
	 * @throws Exception 
	 */
	public void preRenderView() throws Exception {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		TemplateSpecBean productTplSpecBean = getManagedBean().getProductTemplateSpecBean();
		TemplateSpecBean serviceTplSpecBean = getManagedBean().getServiceTemplateSpecBean();
		IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		IQuoteSO quoteSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQuoteSO.class);
		QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
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
			if((getStatusDescById(res.getDocStatus().longValue()).equalsIgnoreCase("Pending Submission"))){
				Long quoteId = Long.valueOf(directPurchaseDataType.getDpId());
				quoteReqDTO.setQuoteId(quoteId);
				quoteReqDTO.setFetchSubEntity(true);
				QuoteResDTO  quoteResDTO = quoteSO.getQuoteDtl((quoteReqDTO));
				ScQuote scQuote = quoteResDTO.getQuoteBidDtl();
				
				if(scQuote.getJustificationType() == null){
					scQuote.setJustificationType(new PmParameter());
				}
				
				// service and product
				if(scQuote.getQuoteDocs() != null){
					for (ScQuoteDoc scQuoteDoc : scQuote.getQuoteDocs()) {
						TplSpecMaster specMaster = scQuoteDoc.getSpecMaster();
						if(ParameterConst.SQ_ITEM_LIST_PRODUCT.equalsIgnoreCase(specMaster.getItemTypeCode().getParameterCode())){
							productTplSpecBean.setSpecMaster(specMaster);
							getManagedBean().setProductTemplateSpecBean(productTplSpecBean);
							// service Tpl Spe
						} else  if(ParameterConst.SQ_ITEM_LIST_SERVICE.equalsIgnoreCase(specMaster.getItemTypeCode().getParameterCode())){
							serviceTplSpecBean.setSpecMaster(specMaster);
							getManagedBean().setServiceTemplateSpecBean(serviceTplSpecBean);
						}
					}
				}
				// zone details
				if(scQuote.getQuoteZone() != null){
					if(scQuote.getQuoteZone().getTplSpecZoneDtls() != null && scQuote.getQuoteZone().getTplSpecZoneDtls().size() >0){
						for (TplSpecZoneDtl tplSpecZoneDtl : scQuote.getQuoteZone().getTplSpecZoneDtls() ) {
							if(tplSpecZoneDtl.getTplSpecZoneLocs() != null && tplSpecZoneDtl.getTplSpecZoneLocs().size()>0){
									getManagedBean().getZoneTrees().add(LocationSingleton.getInstance().constuctTreeNode(tplSpecZoneDtl, tplSpecZoneDtl.getTplSpecZoneLocs()));
							}
						}
					}
				}

				// by locality
				List<PmState> states = new ArrayList<PmState>();
				if(scQuote.getLocationStateCriterias() != null) {
					if(scQuote.getLocationStateCriterias().size() >0){
						for (ScLocationStateCriteria locationStateCriteria : scQuote.getLocationStateCriterias() ) {
							//states.add(locationStateCriteria.getPmState());
						}
					}
				}
				List<PmDivision> divisions = new ArrayList<PmDivision>();
				if(scQuote.getLocationDivisionCriterias() != null) {
					if(scQuote.getLocationDivisionCriterias().size() >0){
						for (ScLocationDivisionCriteria locationDivisionCriteria : scQuote.getLocationDivisionCriterias() ) {
							divisions.add(locationDivisionCriteria.getPmDivision());
						}
					}
				}	
				List<PmDistrict> districts = new ArrayList<PmDistrict>();
				if(scQuote.getLocationDistrictCriterias() != null) {
					if(scQuote.getLocationDistrictCriterias().size() >0){
						for (ScLocationDistrictCriteria locationDistrictCriteria : scQuote.getLocationDistrictCriterias() ) {
							districts.add(locationDistrictCriteria.getPmDistrict());
						}
					}
				}	
				if(states.size() > 0 || divisions.size() > 0 || districts.size() > 0){
					getManagedBean().getLocationTrees().add(
							LocationSingleton.getInstance().buildLocationTree(null, states,
									districts, divisions, null,TplSpecZoneLoc.class));
				}
				getManagedBean().getRequestNote().setPmOrgProfile(scQuote.getRequestNote().getPmOrgProfile());
				// location level by default - district 
				getManagedBean().setLocationLevel(ParameterConst.LOCATION_LEVEL_DISTRICT);
				// business nature details
				if(scQuote.getBusinessNatureCriterias() !=null && scQuote.getBusinessNatureCriterias().size() > 0){
					getManagedBean().setByBusinessNature(Boolean.TRUE);
					getManagedBean().setBusinessNatureCriterias(scQuote.getBusinessNatureCriterias());
				}
				
				// supplier list
				if(scQuote.getScQuoteSuppliers() != null && scQuote.getScQuoteSuppliers().size() > 0){
					getManagedBean().setSupplierList(scQuote.getScQuoteSuppliers());
				}
                
				// remarks
				scQuote.setRemarks(initializeRemarks(scQuote));
				getManagedBean().setScQuote(scQuote);
				
				// duration calc
				dateListener();
				procurementTypeEvent();
				getCurrentStatus(res.getDocStatus().longValue()); 
				getManagedBean().setProcessName(res.getProcessName());

				// page navigation to simple quote
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "simpleQuote");

			} else if((getStatusDescById(res.getDocStatus().longValue()).equalsIgnoreCase("Pending RN Creation"))){
				// page navigation from simple quote  to Request note 
				quoteReqDTO = new QuoteReqDTO();
				Long quoteId = Long.valueOf(directPurchaseDataType.getDpId());
				quoteReqDTO.setQuoteId(quoteId);
				ScQuote quote = quoteSO.getQuoteDtl(quoteReqDTO).getQuoteBidDtl();
				//get RequestNote Backing bean and manage bean and set Request Note to RequestNote manage bean
				PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				RequestNoteBackingBean requestNoteBackingBean = (RequestNoteBackingBean) portletSession.getAttribute("requestNoteBackingBean");
				if (requestNoteBackingBean == null) {
					requestNoteBackingBean = new RequestNoteBackingBean();
				}

				if(quote != null){

					IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
					RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
					requestNoteReqDTO.setRequestNoteId(quote.getRequestNote().getRequestNoteId());
					RequestNoteResDTO requestNoteResDTO = directPurchaseSO.getRequestNote(requestNoteReqDTO);
					ScRequestNote requestNote =  requestNoteResDTO.getRequestNoteDetail();
					
				 if(requestNote != null){
					requestNoteBackingBean.getManagedBean().setRequestSupplierItem(requestNote.getRequestNoteDtls().get(0).getScRequestSupplierItem());
					token = facesScopes.getBPMAuthenticationCtxToken();
					BpmRequestDTO reqBPMDTO = new BpmRequestDTO();
					reqBPMDTO.setCtxToken(token);
					reqBPMDTO.setTaskId(getManagedBean().getTaskId());
					res = bpmSO.getTaskDetails(reqBPMDTO);
					requestNoteBackingBean.getManagedBean().setOutComes(res.getAction());
				     
					// remarks
				    requestNote.setRemarks(requestNoteBackingBean.initializeRemarks(requestNote));
					
					List<ScRequestNoteDtl>	requestNoteDtls = new ArrayList<ScRequestNoteDtl>();
					for (ScRequestNoteDtl requestNoteDtl : requestNote.getRequestNoteDtls()) {
						ParameterResDTO parameterResDTO=null;
						if(requestNoteDtl.getFulfilmentType() == null){
							parameterReqDTO = new ParameterReqDTO();
							parameterReqDTO.setParameterType(ParameterTypeConst.FULFILMENT_TYPE);
							parameterReqDTO.setParameterCode(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN);
							parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
							requestNoteDtl.setFulfilmentType(parameterResDTO.getPmParameter());

						}
						
						if(requestNoteDtl.getDeliverTermType() == null){
							parameterReqDTO = new ParameterReqDTO();
							parameterReqDTO.setParameterType(ParameterTypeConst.DELIVERY_TERM);
							parameterReqDTO.setParameterCode(ParameterConst.SQ_DURATION_DAY);
							parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
							requestNoteDtl.setDeliverTermType(parameterResDTO.getPmParameter());
						}
						
						if(requestNoteDtl.getPriceType() == null){
							parameterReqDTO = new ParameterReqDTO();
							parameterReqDTO.setParameterType(ParameterTypeConst.PRICE_TYPE);
							parameterReqDTO.setParameterCode(ParameterConst.PRICE_TYPE_STANDARD);
							parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
							requestNoteDtl.setDeliverTermType(parameterResDTO.getPmParameter());
						}
						
						if(requestNoteDtl.getJustificationType() != null){
							requestNoteBackingBean.getManagedBean().setJustificationType(requestNoteDtl.getJustificationType().getParameterCode());
						}
						if(requestNoteDtl.getProcurementType().getParameterCode() != null){
							requestNoteBackingBean.getManagedBean().setProcurementType(requestNoteDtl.getProcurementType().getParameterCode());
						}
						
						if(requestNoteDtl.getJustification() != null){
							requestNoteBackingBean.getManagedBean().setJustification(requestNoteDtl.getJustification());
						}
						
						if(requestNoteDtl.getAssignedUser() == null){
							requestNoteDtl.setAssignedUser(new PmUser());	
						}
						
						if(requestNoteDtl.getScRequestSupplierItem() == null){
							ScRequestSupplierItem  requestSupplierItem = new ScRequestSupplierItem();
							SmSupplier  smSupplier = new SmSupplier();
							requestSupplierItem.setSmSupplier(smSupplier);
							requestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
						}
						// by default selected rec.supplier , price type, assigned by and offered price 
						requestNoteBackingBean.supplierLister(requestNoteDtl.getScRequestSupplierItem().getSmSupplier().getSupplierId(),
								requestNoteDtl.getPriceType().getParameterId(), requestNoteDtl, requestNoteDtl.getRequestItem().getSupplierItems() , requestNoteDtl.getAssignedUser().getUserId() ,requestNoteDtl.getDeliverTermType().getParameterId());
						
						requestNoteDtls.add(requestNoteDtl);
					}
					
					requestNoteBackingBean.getManagedBean().setRequestNoteDtls(requestNoteDtls);
					requestNoteBackingBean.getManagedBean().setSourceMethod(ParameterConst.METHOD_SQ);
					
					requestNoteBackingBean.getManagedBean().setProcessName(res.getProcessName());
					requestNoteBackingBean.getManagedBean().setOutComes(res.getAction());
					requestNoteBackingBean.getManagedBean().setTaskId(taskId);
					
					ScRequestNoteDtl scRequestNoteDtl = requestNote.getRequestNoteDtls().get(0);
					requestNoteBackingBean.getManagedBean().setRequestNote(requestNote);
					requestNoteBackingBean.fulfilmentTypeEvent(scRequestNoteDtl);
					requestNoteBackingBean.getManagedBean().setFulfillmentType(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN);
					requestNoteBackingBean.getManagedBean().setRequestNoteDtl(scRequestNoteDtl);
					
					// For status 
					requestNoteBackingBean.getCurrentStatus(DPStatusConstants.RN_PENDING_SUBMISSION);
					//approvers list
					requestNoteBackingBean.initialInformation(requestNote);
			      }
				}	
				
				// page navigation to request note
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "commonRequestNote");
			}
		}
	}
	
	/**
	 * render the suppliver view based on the task id
	 */
	public void supplierView() {
		HttpServletRequest servletRequest = new FacesScopes().getOriginalServletRequest();
		String quoteIdStr = servletRequest.getParameter(SourcingConstants.SUPPLIER_QUOTE_PARAM_QUOTE_ID);
		String hashSupplierIdStr = servletRequest.getParameter(SourcingConstants.SUPPLIER_QUOTE_PARAM_SUPPLIER_ID);
		TemplateSpecBean productTplSpecBean = getManagedBean().getProductTemplateSpecBean();
		TemplateSpecBean serviceTplSpecBean = getManagedBean().getServiceTemplateSpecBean();
		
		if (StringUtils.isNotBlank(quoteIdStr) && StringUtils.isNotBlank(hashSupplierIdStr)) {
			IQuoteSO quoteSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQuoteSO.class);
			FacesScopes facesScopes = new FacesScopes();
			QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
			Long quoteId = Long.valueOf(quoteIdStr);
			quoteReqDTO.setQuoteId(quoteId);
			quoteReqDTO.setUserId(facesScopes.getUserSessionBean().getUserId());
			quoteReqDTO.setHashSupplierId(hashSupplierIdStr);
			quoteReqDTO.setFetchSubEntity(true);
			QuoteResDTO  quoteResDTO = quoteSO.getQuoteDtlForSupplier(quoteReqDTO);
			ScQuote scQuote = quoteResDTO.getQuoteBidDtl();
			
			// remarks
			scQuote.setRemarks(initializeRemarks(scQuote));
            // service and product
			if(quoteResDTO.getProductAnswersTpl() != null && quoteResDTO.getProductAnswersTpl().size() >0){
				productTplSpecBean.setSpecAnswerList(quoteResDTO.getProductAnswersTpl());
				productTplSpecBean.setSpecMaster(scQuote.getQuoteDocs().get(0).getSpecMaster());
				productTplSpecBean.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);
				getManagedBean().setProductTemplateSpecBean(productTplSpecBean);
			}
			
			 if(quoteResDTO.getServiceAnswersTpl() != null && quoteResDTO.getServiceAnswersTpl().size() >0){
				serviceTplSpecBean.setSpecAnswerList(quoteResDTO.getServiceAnswersTpl());
				serviceTplSpecBean.setSpecMaster(scQuote.getQuoteDocs().get(0).getSpecMaster());
				serviceTplSpecBean.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);
				getManagedBean().setServiceTemplateSpecBean(serviceTplSpecBean);
			} 
			 
		    if(quoteResDTO.getSupplier() != null){
		    	getManagedBean().setSmSupplier(quoteResDTO.getSupplier());
		    }
			getManagedBean().setScQuote(scQuote);
			initializeRemarks(scQuote);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "supplerView");
		}
	}
	
	private ScQuoteZone getQuoteZones(List<TreeNode> treeNodes) {
		TplSpecZoneDtl tplSpecZoneDtl = null;
		ScQuoteZone scQuoteZone = getManagedBean().getScQuote().getQuoteZone();
		List<TplSpecZoneLoc> results = new ArrayList<TplSpecZoneLoc>();
		List<TplSpecZoneDtl> tplSpecDtls = new ArrayList<TplSpecZoneDtl>();
		
		for (TreeNode rootNode : treeNodes) {
			for (TreeNode ZoneNode : rootNode.getChildren()) {

				for (TreeNode stateNode : ZoneNode.getChildren()) {

					if (null != stateNode.getChildren() && !stateNode.getChildren().isEmpty()) {
						for (TreeNode devisionNode : stateNode.getChildren()) {

							if (devisionNode.getChildCount() > 0) {
								
								for (TreeNode districtNode : devisionNode.getChildren()) {
									log.info("===divisions=====");
									TplSpecZoneLoc tplSpecZoneLoc = (TplSpecZoneLoc) districtNode.getData();
									tplSpecZoneDtl  = tplSpecZoneLoc.getTplSpecZoneDtl();
									results.add(tplSpecZoneLoc);
									tplSpecZoneDtl.setTplSpecZoneLocs(results);
									//tplSpecZoneDtl.setTplSpecZone(scQuoteZone);
									//tplSpecDtls.add(tplSpecZoneDtl);
								}
								
							} else {
								log.info("===districts=====");
								TplSpecZoneLoc tplSpecZoneLoc = (TplSpecZoneLoc) devisionNode.getData();
								tplSpecZoneDtl  = tplSpecZoneLoc.getTplSpecZoneDtl();
								results.add(tplSpecZoneLoc);
								tplSpecZoneDtl.setTplSpecZoneLocs(results);
								//tplSpecZoneDtl.setTplSpecZone(scQuoteZone);
								//tplSpecDtls.add(tplSpecZoneDtl);
							}
						}
						
					} else {
						log.info("===states=====");
						TplSpecZoneLoc tplSpecZoneLoc = (TplSpecZoneLoc) stateNode.getData();
						tplSpecZoneDtl = tplSpecZoneLoc.getTplSpecZoneDtl();
						results.add(tplSpecZoneLoc);
						tplSpecZoneDtl.setTplSpecZoneLocs(results);
						//tplSpecZoneDtl.setTplSpecZone(scQuoteZone);
						//tplSpecDtls.add(tplSpecZoneDtl);
					}
				}
			}
			if(tplSpecZoneDtl != null){
				tplSpecZoneDtl.setTplSpecZoneLocs(results);
				tplSpecDtls.add(tplSpecZoneDtl);
				scQuoteZone.setTplSpecZoneDtls(tplSpecDtls);
			}
		}
		return scQuoteZone;
	}
	
	/**
	 * supplierSubmit method to submit suppler info in SQ
	 */
	public String supplierSubmit() {
		debugLogger("supplierSubmit");
		IQuoteSO quoteSO = null;
		try {
			quoteSO = (IQuoteSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQuoteSO.class);
			QuoteReqDTO reqDTO = new QuoteReqDTO();
			ScQuote quote = getManagedBean().getScQuote();
			
			// tpl spec - product
			List<TplSpecAnswer> specAnswers  = new ArrayList<TplSpecAnswer>();
			if(getManagedBean().getProductTemplateSpecBean().getSpecAnswerList() != null){
				specAnswers.addAll(getManagedBean().getProductTemplateSpecBean().getSpecAnswerList());
			}
			
			// tpl spec - service
			if(getManagedBean().getServiceTemplateSpecBean().getSpecAnswerList() != null){
				specAnswers.addAll(getManagedBean().getServiceTemplateSpecBean().getSpecAnswerList());
			}
			
			// remarks
			if(quote.getRemarks() != null ){
				for (int i = 0; i < quote.getRemarks().size(); i++) {
					ScSQRemark remark = quote.getRemarks().get(i);
					if(StringUtils.isBlank(remark.getRemark())){
						quote.getRemarks().remove(remark);
					}	
				} 
			}
			reqDTO.setSpecAnswers(specAnswers);
			reqDTO.setQuoteBidDtl(quote);
			QuoteResDTO quoteResDTO = quoteSO.saveSupplierResponse(reqDTO);
			if (quoteResDTO.isSuccessful()) {
				ScQuote scQuote = getManagedBean().getScQuote();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, 
						null,
						MessageUtils.getMessage(MessageConst.INFO_700026, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_SIMPLE_QUOTE), scQuote.getQuoteNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, 
								new FacesScopes().getUserLocale())));
				 // clear SQ data
				 super.renewSesionObject();
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);	
			} else {
				throw new EpException("Failed.");
			}
		}  finally {
			quoteSO = null;
		}
		return null;
	}

	/**
	 * Event for Zone in SQ
	 */
	public void zoneEvent() {
		ScQuote quote = getManagedBean().getScQuote();
		if (quote.isZonal()){
			ScQuoteZone quoteZone = new  ScQuoteZone();
			quoteZone.setScQuote(quote);
			quote.setQuoteZone(quoteZone);
			quote.setZonal(Boolean.TRUE);
		} else {
			quote.setZonal(Boolean.FALSE);
		}
		
		this.getManagedBean().setScQuote(quote);
	}
	
	/**
	 * Event for No.Of quote in SQ
	 */
	public void noOfQuoteEvent() {
		ScQuote quote = getManagedBean().getScQuote();
		if (quote.getMultipleQuote()){ // for multiple quote
			
			//get Template spec backing bean and manage bean
			PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			TemplateSpecBackingBean templateSpecBackingBean = (TemplateSpecBackingBean) portletSession.getAttribute("templateSpecBackingBean");
			if (templateSpecBackingBean == null) {
				templateSpecBackingBean = new TemplateSpecBackingBean();
			}
			TplSpecMaster  serviceTplSpec = getManagedBean().getProductTemplateSpecBean().getSpecMaster();
			TplSpecMaster  productTplSpec = getManagedBean().getProductTemplateSpecBean().getSpecMaster();
			
			// product
			if(productTplSpec != null){
				if(productTplSpec.getTplSpecQuestions() != null && productTplSpec.getTplSpecQuestions().size() >0){
					getManagedBean().getProductTemplateSpecBean().getSpecMaster().setEstCostReq(Boolean.TRUE);
					for (TplSpecQuestion TplSpecQuestion : productTplSpec.getTplSpecQuestions()) {
						templateSpecBackingBean.resetPriceTypeDtl(TplSpecQuestion);
					}
				}
			  }

			// service 
			if(serviceTplSpec != null){
				if(serviceTplSpec.getTplSpecQuestions() != null && serviceTplSpec.getTplSpecQuestions().size() >0){
					getManagedBean().getServiceTemplateSpecBean().getSpecMaster().setEstCostReq(Boolean.TRUE);
					for (TplSpecQuestion TplSpecQuestion : serviceTplSpec.getTplSpecQuestions()) {
						templateSpecBackingBean.resetPriceTypeDtl(TplSpecQuestion);
					}
				}
			}
		} else{
			getManagedBean().getProductTemplateSpecBean().getSpecMaster().setEstCostReq(Boolean.FALSE);
			getManagedBean().getServiceTemplateSpecBean().getSpecMaster().setEstCostReq(Boolean.FALSE);
		}
	}
	
	public String cancel() {
		log.info("cancel");
		// clear sq
		super.renewSesionObject();
		return "catalogueSearch";
	}
}

