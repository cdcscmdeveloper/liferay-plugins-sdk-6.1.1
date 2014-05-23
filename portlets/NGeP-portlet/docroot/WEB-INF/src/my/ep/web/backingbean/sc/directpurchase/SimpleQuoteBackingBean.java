/**
 * Copyright � 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.directpurchase;

import java.lang.reflect.InvocationTargetException;
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
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.businessnature.IBusinessNatureSO;
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
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureReqDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.directpurchase.QuoteReqDTO;
import my.ep.dto.sc.directpurchase.QuoteResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScBusinessNatureQuoteCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScGovEPNoCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationDistrictCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationDivisionCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationStateCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteDoc;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteSupplier;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteZone;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteZoneLocation;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScSQRemark;
import my.ep.persistence.entity.sc.directpurchase.ScSQWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierMofNoCriteria;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.directpurchase.SimpleQuoteManagedBean;
import my.ep.web.sc.directpurchase.common.DPStatusConstants;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
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
public class SimpleQuoteBackingBean extends
		BaseBackingBean<SimpleQuoteManagedBean> {
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
		populateUserDivisionList();
	}
	
	/**
	 * simpleQuoteAction for getting cmSupplierItem from search catalogue in
	 * simpleQuote
	 */
	public String simpleQuoteAction(Long cmSupplierItemId, boolean itemPanelFlag) {
		debugLogger("simpleQuoteAction");
		IStatusSO iStatusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT,ICatalogueSO.class);
		IQuoteSO quoteSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQuoteSO.class);
		CatalogueReqDTO catalogueReqDTO = new CatalogueReqDTO();
		catalogueReqDTO.setDocId(cmSupplierItemId);
		CatalogueResDTO catalogueResDTO = catalogueSO.searchSupplierItemByStatus(catalogueReqDTO);
		ScQuote scQuote = getManagedBean().getScQuote();
		scQuote.setQuoteNo(quoteSO.generateQuoteNo());

		if(catalogueResDTO.getSupplierItemList() != null && !catalogueResDTO.getSupplierItemList().isEmpty()){
			CmSupplierItem cmSupplierItem = catalogueResDTO.getSupplierItemList().get(0);
			//ScRequestItem scRequestItem = new ScRequestItem();
			if (cmSupplierItem != null) {
				List<ScQuoteSupplier> suppliers = new ArrayList<ScQuoteSupplier>();
				ScQuoteSupplier quoteSupplier = new ScQuoteSupplier();
				this.getManagedBean().setCmSupplierItem(cmSupplierItem);
				/*	ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
				ScSupplierItemPrice  scSupplierItemPrice = new ScSupplierItemPrice();
				List<ScRequestSupplierItem> requestSupplierItems = new ArrayList<ScRequestSupplierItem>();
				ScRequestItem scRequestItem = new ScRequestItem();
				scSupplierItemPrice.setPricing(cmSupplierItem.getCmPricings().get(0));
				requestSupplierItem.setSupplierItemPrice(scSupplierItemPrice);
				requestSupplierItem.setSmSupplier(cmSupplierItem.getSmSupplier());
				requestSupplierItem.setRequestNoteItem(scRequestItem);
				requestSupplierItems.add(requestSupplierItem);*/
				quoteSupplier.setSmSupplier(cmSupplierItem.getSmSupplier());
				suppliers.add(quoteSupplier);
				scQuote.setScQuoteSuppliers(suppliers);
				//scQuote.setScRequestListSupplierItems(scRequestSupplierItems);
			}
		}
		
		
		// TODO check flag for panel item or non contract item
		if(itemPanelFlag){
			getManagedBean().setIsPanel(1);
			getManagedBean().setIsZonal(1);
			scQuote.setMultipleQuote(Boolean.TRUE);
		} else {
			getManagedBean().setIsPanel(0);
			getManagedBean().setIsZonal(0);
			scQuote.setMultipleQuote(Boolean.TRUE);
			
			if (getManagedBean().getCmSupplierItem() != null) {
				// TODO check flag for panel item or non contract item
				String ItemCode = getManagedBean().getCmSupplierItem().getCmItem().getExtensionCode();
				CharSequence charSeq = ItemCode.subSequence(0, 2);
				Integer icode = Integer.valueOf(charSeq.toString());

				// TODO checking first two digits in itemCode whether service or product
				QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
				QuoteResDTO quoteResDTO = quoteSO.getQuoteDtlFirstTime(quoteReqDTO);
				for (ScQuoteDoc quoteDoc : quoteResDTO.getQuoteDocs()) {
					//if (icode > ParameterConst.PRODUCT_OR_SERVICE) { // for service
						//if(ParameterConst.SQ_ITEM_LIST_SERVICE.equalsIgnoreCase(quoteDoc.getSpecMaster().getItemTypeCode().getParameterCode())){
							TemplateSpecBean serviceTemplateSpecBean = getManagedBean().getServiceTemplateSpecBean();
							TplSpecMaster serviceTpl =  quoteDoc.getSpecMaster();
							List<TplSpecQuestion> tplSpecQuestions = new ArrayList<TplSpecQuestion>();
							TplSpecQuestion specQuestion = new TplSpecQuestion();
							specQuestion.setClItem(getManagedBean().getCmSupplierItem().getCmItem().getItemName());
							tplSpecQuestions.add(specQuestion);
							serviceTpl.setTplSpecQuestions(tplSpecQuestions);
							serviceTemplateSpecBean.setSpecMaster(serviceTpl);
							getManagedBean().setServiceTemplateSpecBean(serviceTemplateSpecBean);
					  
				 // } else if (icode < ParameterConst.PRODUCT_OR_SERVICE) { // product
					  //if(ParameterConst.SQ_ITEM_LIST_PRODUCT.equalsIgnoreCase(quoteDoc.getSpecMaster().getItemTypeCode().getParameterCode())){
						  TemplateSpecBean productTemplateSpecBean = getManagedBean().getProductTemplateSpecBean();
						  TplSpecMaster prodTpl = quoteDoc.getSpecMaster();
						  List<TplSpecQuestion> serviceQuestions = new ArrayList<TplSpecQuestion>();
						  TplSpecQuestion serviceQuestion = new TplSpecQuestion();
						  specQuestion.setClItem(getManagedBean().getCmSupplierItem().getCmItem().getItemName());
						  serviceQuestions.add(serviceQuestion);
						  prodTpl.setTplSpecQuestions(serviceQuestions);
						  productTemplateSpecBean.setSpecMaster(prodTpl);
						  getManagedBean().setProductTemplateSpecBean(productTemplateSpecBean);
					 // }
				 // }
			//}
		}
	}}
		// WorkFlow Status
		List<ScSQWorkFlowStatus> workFlowStatuses = new ArrayList<ScSQWorkFlowStatus>();
		ScSQWorkFlowStatus workFlowStatus = new ScSQWorkFlowStatus();
		workFlowStatus.setQuote(scQuote);
		PmStatus status = new PmStatus();
		status.setStatusId(DPStatusConstants.SQ_PENDING_SUBMISSION);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		if (status != null) {
			statusReqDTO.setStatusId(status.getStatusId());
			StatusResDTO statusResDTO = iStatusSO
					.retrieveStatusByStatusId(statusReqDTO);
			workFlowStatus.setPmStatus(statusResDTO.getPmStatus());
		}
		workFlowStatus.setCurrent(Boolean.TRUE);
		workFlowStatuses.add(workFlowStatus);
		getCurrentStatus(DPStatusConstants.SQ_PENDING_SUBMISSION);
		scQuote.setWorkflowStatuses(workFlowStatuses);
		scQuote.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		// Initializing Remarks
		scQuote.setRemarks(initializeRemarks(scQuote));
		
		this.getManagedBean().setProcurementType(ParameterConst.PROC_TYPE_NORMAL);
		this.getManagedBean().setScQuote(scQuote);
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IAddressSO.class);
		AddressReqDTO reqDTO = new AddressReqDTO();
		reqDTO.setOrgProfileId(4L);
		//reqDTO.setUserId(2L);
		reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
		AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
		List<PmAddressType> list = resDTO.getAddressTypeList();
		System.out.println("Delivery Address List: " + list.size());

		PmAddressType addressType = list.get(0);
		List<PmState> states = new ArrayList<PmState>();
		List<PmDivision> divisions = new ArrayList<PmDivision>();
		List<PmDistrict> districts = new ArrayList<PmDistrict>();
		PmState pmState = addressType.getPmAddress().getPmState();
		states.add(pmState);

		PmDistrict pmDistrict = addressType.getPmAddress().getPmDistrict();
		districts.add(pmDistrict);

		Integer locationLevel = this.getManagedBean().getLocationLevel();
		if (locationLevel == null) {
			locationLevel = 3;
		}
		
		List<TreeNode>	locationTrees =  new ArrayList<TreeNode>();
		TreeNode	locationTree = LocationSingleton.getInstance().buildPtjLocationTree(null,
				states, districts, divisions, null);
		locationTrees.add(locationTree);
		getManagedBean().setLocationTrees(locationTrees);
		
		/*getManagedBean().getLocationTrees().add(
				LocationSingleton.getInstance().buildPtjLocationTree(null,
						states, districts, divisions, null));*/

		getManagedBean().setLocLevelDropdown(getLocationLevelDropdown());
		// location level by default - district 
		getManagedBean().setLocationLevel(3);
		
		return "simpleQuote";
	}

	/**
	 * saveSimpleQuote method to save SQ
	 */
	public String saveSimpleQuote() {
		debugLogger("saveSimpleQuote");
		IQuoteSO quoteSO = null;
		try {
			quoteSO = (IQuoteSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQuoteSO.class);
			QuoteReqDTO reqDTO = new QuoteReqDTO();
			ScQuote quote = getManagedBean().getScQuote();
			quote.setQuoteNo(quoteSO.generateQuoteNo());
			
			ScRequestItem requestItem = new ScRequestItem();
			List<ScRequestItem> requestItems = new ArrayList<ScRequestItem>();
			requestItems.add(requestItem);
			ScRequestNote requestNote = new ScRequestNote();
			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
			requestNote.setRequestNoteNo(directPurchaseSO.generateRequestNoteNo());
			requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SQ);
			requestNote.setPmOrgProfile(ptjNameEvent());
			requestNote.setWorkFlowStatuses(null);
			requestNote.setQuote(quote);
			quote.setRequestItems(requestItems);
			quote.setRequestNote(requestNote);
			quote.setCreatedDate(DateUtils.getCurrentDate());
			
			List<ScQuoteSupplier> suppliers = new ArrayList<ScQuoteSupplier>();
			ScQuoteSupplier quoteSupplier = new ScQuoteSupplier();
			SmSupplier smSupplier = new SmSupplier();
			smSupplier.setSupplierId(1l);
			quoteSupplier.setSmSupplier(smSupplier);
			quoteSupplier.setQuote(quote);
			suppliers.add(quoteSupplier);
			quote.setScQuoteSuppliers(suppliers);
			
			quote.setProcurementType(getManagedBean().getProcurementType());
			quote.setIsPanel(getManagedBean().getIsPanel());
			quote.setIsZonal(getManagedBean().getIsZonal());
			
			//zone
			quote.setScZones(getQuoteZones(getManagedBean().getZoneTrees(),quote));
			//by location
			quote.setLocationStateCriterias(getLocationStateCriterias(getManagedBean().getLocationTrees(), quote));
			quote.setLocationDivisionCriterias(getLocationDivisionCriterias(getManagedBean().getLocationTrees(), quote));
			quote.setLocationDistrictCriterias(getLocationDistrictCriterias(getManagedBean().getLocationTrees(), quote));
			// by business nature
			quote.setBusinessNatureCriterias(getBusinessNatureCriterias(getManagedBean().getBusinessNatureCriterias(), quote));
			
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
			// tpl spec - product
			if(getManagedBean().getProductTemplateSpecBean().getSpecMaster() != null){
				List<ScQuoteDoc> quoteDocs = new ArrayList<ScQuoteDoc>();
				ScQuoteDoc quoteDoc = new ScQuoteDoc();
				if(getManagedBean().getProductTemplateSpecBean().getSpecMaster().getSpecMasterId() != null){
					quoteDoc.setSpecMaster(getManagedBean().getProductTemplateSpecBean().getSpecMaster());
					quoteDoc.setQuote(getManagedBean().getScQuote());
					quoteDocs.add(quoteDoc);
					quote.setQuoteDocs(quoteDocs);
				}
			}
			// tpl spec - service
			if(getManagedBean().getServiceTemplateSpecBean().getSpecMaster() != null){
				List<ScQuoteDoc> quoteDocs = new ArrayList<ScQuoteDoc>();
				ScQuoteDoc quoteDoc = new ScQuoteDoc();
				if(getManagedBean().getServiceTemplateSpecBean().getSpecMaster().getSpecMasterId() != null){
					quoteDoc.setSpecMaster(getManagedBean().getServiceTemplateSpecBean().getSpecMaster());
					quoteDoc.setQuote(getManagedBean().getScQuote());
					quoteDocs.add(quoteDoc);
					quote.setQuoteDocs(quoteDocs);
				}
			}
			FacesScopes facesScopes = new FacesScopes();
			String token = facesScopes.getBPMAuthenticationCtxToken();
			reqDTO.setToken(token);
			reqDTO.setSubmit(Boolean.FALSE);
			reqDTO.setOrgProfileId(getManagedBean().getOrgProfileId());
			reqDTO.setQuoteBidDtl(quote);
			QuoteResDTO quoteResDTO = quoteSO.saveQuoteDtl(reqDTO);
			if (quoteResDTO.isSuccessful()) {
				ScQuote scQuote = quoteResDTO.getQuoteBidDtl();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, 
						null,
						MessageUtils.getMessage(MessageConst.INFO_700025, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_SIMPLE_QUOTE), scQuote.getQuoteNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, 
								new FacesScopes().getUserLocale())));
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);	
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sorry We are unable to process.", ""));
			ex.printStackTrace();
		} finally {
			quoteSO = null;
		}
		return null;
	}
	
	/**
	 * saveSimpleQuote method to save SQ
	 */
	public String submitSimpleQuote() {
		debugLogger("submitSimpleQuote");
		IQuoteSO quoteSO = null;
		try {
			quoteSO = (IQuoteSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQuoteSO.class);
			QuoteReqDTO reqDTO = new QuoteReqDTO();
			ScQuote quote = getManagedBean().getScQuote();
			List<ScQuoteSupplier> suppliers = quote.getScQuoteSuppliers();
			
			quote.setProcurementType(getManagedBean().getProcurementType());
			quote.setIsPanel(getManagedBean().getIsPanel());
			quote.setIsZonal(getManagedBean().getIsZonal());
			
			quote.setQuoteNo(quoteSO.generateQuoteNo());
			ScRequestItem requestItem = new ScRequestItem();
			List<ScRequestItem> requestItems = new ArrayList<ScRequestItem>();
			requestItems.add(requestItem);
			ScRequestNote requestNote = new ScRequestNote();
			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
			requestNote.setRequestNoteNo(directPurchaseSO.generateRequestNoteNo());
			requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SQ);
			requestNote.setWorkFlowStatuses(null);
			requestNote.setPmOrgProfile(ptjNameEvent());
			requestNote.setQuote(quote);
			quote.setRequestNote(requestNote);
			quote.setCreatedDate(DateUtils.getCurrentDate());
			//zone
			quote.setScZones(getQuoteZones(getManagedBean().getZoneTrees(), quote));
			//by location
			quote.setLocationStateCriterias(getLocationStateCriterias(getManagedBean().getLocationTrees(), quote));
			quote.setLocationDivisionCriterias(getLocationDivisionCriterias(getManagedBean().getLocationTrees(), quote));
			quote.setLocationDistrictCriterias(getLocationDistrictCriterias(getManagedBean().getLocationTrees(), quote));
			// by business nature
			quote.setBusinessNatureCriterias(getBusinessNatureCriterias(getManagedBean().getBusinessNatureCriterias(), quote));
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
			//remarks
			quote.setRemarks(initializeRemarks(quote));
			// tpl spec - product
			if(getManagedBean().getProductTemplateSpecBean().getSpecMaster() != null){
				List<ScQuoteDoc> quoteDocs = new ArrayList<ScQuoteDoc>();
				ScQuoteDoc quoteDoc = new ScQuoteDoc();
				if(getManagedBean().getProductTemplateSpecBean().getSpecMaster().getSpecMasterId() != null){
					quoteDoc.setSpecMaster(getManagedBean().getProductTemplateSpecBean().getSpecMaster());
					quoteDoc.setQuote(getManagedBean().getScQuote());
					quoteDocs.add(quoteDoc);
					quote.setQuoteDocs(quoteDocs);
				}
			}
			// tpl spec - service
			if(getManagedBean().getServiceTemplateSpecBean().getSpecMaster() != null){
				List<ScQuoteDoc> quoteDocs = new ArrayList<ScQuoteDoc>();
				ScQuoteDoc quoteDoc = new ScQuoteDoc();
				if(getManagedBean().getServiceTemplateSpecBean().getSpecMaster().getSpecMasterId() != null){
					quoteDoc.setSpecMaster(getManagedBean().getServiceTemplateSpecBean().getSpecMaster());
					quoteDoc.setQuote(getManagedBean().getScQuote());
					quoteDocs.add(quoteDoc);
					quote.setQuoteDocs(quoteDocs);
				}
			}
			FacesScopes facesScopes = new FacesScopes();
			String token = facesScopes.getBPMAuthenticationCtxToken();
			reqDTO.setToken(token);
			reqDTO.setSubmit(Boolean.TRUE);
			reqDTO.setOrgProfileId(getManagedBean().getOrgProfileId());
			reqDTO.setQuoteBidDtl(quote);
			QuoteResDTO quoteResDTO = quoteSO.saveQuoteDtl(reqDTO);
			if (quoteResDTO.isSuccessful()) {
				ScQuote scQuote = quoteResDTO.getQuoteBidDtl();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, 
						null,
						MessageUtils.getMessage(MessageConst.INFO_700005, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_SIMPLE_QUOTE), scQuote.getQuoteNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, 
								new FacesScopes().getUserLocale())));
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);	
			}
         
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sorry We are unable to process.", ""));
			ex.printStackTrace();
		} finally {
			quoteSO = null;
		}
		return null;
	}
	
	/**
	 *  updateBpmTask method to submit simple quote details and update task by supplier in SQ
	 */	
 public String updateBpmTask(String outcome){
	 IQuoteSO quoteSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQuoteSO.class);
	   if(StringUtils.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT,outcome)){
		   QuoteReqDTO reqDTO = new QuoteReqDTO();
		   reqDTO.setQuoteBidDtl(getManagedBean().getScQuote());
		   FacesScopes facesScopes = new FacesScopes();
		   String token = facesScopes.getBPMAuthenticationCtxToken();
		   reqDTO.setToken(token);
		   reqDTO.setTaskId(getManagedBean().getTaskId());
		   QuoteResDTO resDTO = quoteSO.saveQuoteDtl(reqDTO);
		   if(resDTO.isSuccessful()){
			 ScQuote scQuote = resDTO.getQuoteBidDtl();
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					 FacesMessage.SEVERITY_INFO, null,
					 MessageUtils.getMessage(MessageConst.INFO_700026, 
						new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_SIMPLE_QUOTE),scQuote.getQuoteNo()},
					    MessageUtils.BUNDLE_COMMON_MESSAGES, 
						new FacesScopes().getUserLocale())));
			 FacesContext fc = FacesContext.getCurrentInstance();
			 fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE); 
		   }else{
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sorry We are unable to process.", ""));
		   }
	   }else{
		   IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			BpmRequestDTO reqBpm = new BpmRequestDTO();
			FacesScopes facesScopes = new FacesScopes();
			String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
			try {
				reqBpm.setTaskId(getManagedBean().getTaskId());
				reqBpm.setOutcome(outcome);
				if (null != ctxToken) {
					reqBpm.setCtxToken(ctxToken);
					BpmResponseDTO resBpm = bpmSO.updateTask(reqBpm);
					boolean result = resBpm != null ? resBpm.isSuccessful() : Boolean.FALSE;
					if (result) {
						 if(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)){
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
									FacesMessage.SEVERITY_INFO, null, 
									MessageUtils.getMessage(MessageConst.INFO_700009, 
											new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_SIMPLE_QUOTE),getManagedBean().getScQuote().getQuoteNo()}, 
											MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
							FacesContext fc = FacesContext.getCurrentInstance();
							fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);

						} else if(BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)){
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
									FacesMessage.SEVERITY_INFO, null, 
									MessageUtils.getMessage(MessageConst.INFO_700011, 
											new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_SIMPLE_QUOTE),getManagedBean().getScQuote().getQuoteNo()}, 
											MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
							FacesContext fc = FacesContext.getCurrentInstance();
							fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);	   	

						} else if(BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)){
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
									FacesMessage.SEVERITY_INFO, null, 
									MessageUtils.getMessage(MessageConst.INFO_700008, 
											new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_SIMPLE_QUOTE),getManagedBean().getScQuote().getQuoteNo()}, 
											MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
							FacesContext fc = FacesContext.getCurrentInstance();
							fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);	   	
						}else if(BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)){
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
									FacesMessage.SEVERITY_INFO, null, 
									MessageUtils.getMessage(MessageConst.INFO_700010, 
											new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_SIMPLE_QUOTE),getManagedBean().getScQuote().getQuoteNo()}, 
											MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
							FacesContext fc = FacesContext.getCurrentInstance();
							fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);	   	
						}
					}
				}
			} catch (Exception ex) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Sorry We are unable to process.", ""));
				ex.printStackTrace();
			} finally {
				bpmSO = null;
			}
	   }
	return null;
 }
	
	
	 public void byLocAndBusinessEvent() {
		 debugLogger("byBusinessEvent");
		 if (getManagedBean().isByLocality()) {
			 getManagedBean().setActiveIndex(0);
			 getManagedBean().setSuppListFlag(Boolean.TRUE);
			
		 } 
		 if (getManagedBean().isByBusinessNature()) {
			 getManagedBean().setActiveIndex(1);
			 getManagedBean().setSuppListFlag(Boolean.FALSE);
		 }
	 }

	/**
	 * initializing remarks in simpleQuote
	 */
	private List<ScSQRemark> initializeRemarks(ScQuote scQuote) {
		IStatusSO iStatusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		List<ScSQRemark> results = scQuote.getRemarks();
		if(results != null){
			results = new ArrayList<ScSQRemark>();
		}
		FacesScopes facesScopes = new FacesScopes();
		if (results.size() == 0
				|| !compareUserName(results.get(0).getUser().getLoginId())) {
			ScSQRemark remark = new ScSQRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setUser(facesScopes.getUserSessionBean());
			if (getManagedBean().getScQuote().getRemarks() != null
					&& getManagedBean().getScQuote().getRemarks().size() > 0) {
				remark.setRemark(getManagedBean().getScQuote().getRemarks()
						.get(0).getRemark());
			}
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
		// for the panel item enable and disable

		if(getManagedBean().getIsPanel().equals(1)){
			getManagedBean().setLocAndBusinessFlag(Boolean.FALSE);
			getManagedBean().setG2GFlag(Boolean.FALSE);
			getManagedBean().setByBusinessNature(Boolean.FALSE);

		}else {
			getManagedBean().setLocAndBusinessFlag(Boolean.TRUE);
			getManagedBean().setG2GFlag(Boolean.FALSE);
			getManagedBean().setByBusinessNature(Boolean.FALSE);
		}

		if (getManagedBean().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL)) {
			getManagedBean().setProcTypeOption(Boolean.FALSE);
			getManagedBean().setJustTypeOption(Boolean.FALSE);

		} else if (getManagedBean().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			getManagedBean().setProcTypeOption(Boolean.FALSE);
			getManagedBean().setJustTypeOption(Boolean.FALSE);

		} else if (getManagedBean().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
			getManagedBean()
			.setJustDropdown(
					getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_ADHOC));

		} else if (getManagedBean().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_EMERGENCY)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
			getManagedBean()
			.setJustDropdown(
					getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_EMERGENCY));
		}
	}
	
	/**
	 * Adding new row for BusinessNature in SQ
	 */
	public void addBusinessNature() {
		debugLogger("addBusinessNature");
		ScBusinessNatureQuoteCriteria businessNatureCriteria = new ScBusinessNatureQuoteCriteria();
		PmBusinessNature businessNature = new PmBusinessNature();
		businessNatureCriteria.setBusinessNature(businessNature);
		try {
			List<ScBusinessNatureQuoteCriteria> businessNatureCriterias = getManagedBean()
					.getScQuote().getBusinessNatureCriterias();
			if (null == businessNatureCriterias) {
				businessNatureCriterias = new ArrayList<ScBusinessNatureQuoteCriteria>();
			}
			businessNatureCriterias.add(businessNatureCriteria);
			this.getManagedBean().setBusinessNatureCriterias(businessNatureCriterias);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
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
	  Integer locationLevel = this.getManagedBean().getLocationLevel();
		if (locationLevel == null) {
			locationLevel = 3;
		}
		getManagedBean().setRootZoneTree(
				LocationSingleton.getInstance().buildLocationZoneTree(1L, null,
						null, null, null, locationLevel));
	}
	
   /**
	 * showLocationPopup method for location popup in simple quote
    */
	public void showLocationPopup() {
		 Integer locationLevel = this.getManagedBean().getLocationLevel();
			if (locationLevel == null) {
				locationLevel = 3;
			}
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationZoneTree(1L, null,
						null, null, null, locationLevel));
	}
	
	/**
	 * updateZone for zone selection  in simple quote
    */
	public void updateZone() {
		List<LocationDTO> locationDTOs = new ArrayList<LocationDTO>();
		// TreeNode[] listSelectedTreeNode =
		// getManagedBean().getSelectedNodes().clone();
		if (getManagedBean().getSelectedNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedNodes()) {
				if (!treeNode.getParent().isSelected()) {
					List<LocationDTO> locationDTO = getLocations(treeNode);
					// LocationDTO locationDTO = (LocationDTO)
					// treeNode.getData();
					locationDTOs.addAll(locationDTO);
				}
			}
		}

		ScQuoteZone zone = new ScQuoteZone();

		zone.setZoneName(getManagedBean().getZoneName());
		getManagedBean().setZoneName(null);

		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTOs(locationDTOs);

		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTOs(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTOs(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTOs(locationDTOs);

		if (getManagedBean().getZoneIndex() == -1) {

			getManagedBean().getZoneTrees().add(
					LocationSingleton.getInstance().buildZoneTree(zone, states,
							districts, divisions, cities));
		} else if (getManagedBean().getZoneIndex() >= 0) {

			getManagedBean().getZoneTrees().remove(getManagedBean().getZoneIndex());
			getManagedBean().getZoneTrees().add(
					getManagedBean().getZoneIndex(),
					LocationSingleton.getInstance().buildZoneTree(zone, states,
							districts, divisions, cities));
		}
		this.getManagedBean().setZoneIndex(-1);
		/*
		 * getManagedBean().setRootLocationTree(
		 * LocationSingleton.getInstance().buildLocationTree(1L, null, null,
		 * null, null));
		 */

		// System.out.println("Zone Tree : " +
		// getManagedBean().getZoneTrees().get(0).getData());
		// System.out.println("Zone Name : " + getManagedBean().getZoneName());
	}
	
   /**
	 * updateZone for location selection  in simple quote
    */
	public void updateLocation() {
		List<LocationDTO> locationDTOs = new ArrayList<LocationDTO>();
		if (getManagedBean().getSelectedLocNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedLocNodes()) {
				if (treeNode.getParent().isSelected()) {
					List<LocationDTO> locationDTO = getAllLocations(treeNode);
					locationDTOs.addAll(locationDTO);
				} else{
					List<LocationDTO> locationDTO = getAllLocations(treeNode);
					locationDTOs.addAll(locationDTO);
				}
			}
		}
		ScQuoteZone zone = null;
		//zone.setZoneName(getManagedBean().getLocZoneName());
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTOs(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTOs(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTOs(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTOs(locationDTOs);

		if (getManagedBean().getLocIndex() == -1) {
			 
			getManagedBean().getLocationTrees().add(
					LocationSingleton.getInstance().buildPtjLocationTree(zone, states,
							districts, divisions, cities));
		} else if (getManagedBean().getLocIndex() >= 0) {
			getManagedBean().getLocationTrees().remove(getManagedBean().getLocIndex());
			getManagedBean().getLocationTrees().add(
					getManagedBean().getLocIndex(),
					LocationSingleton.getInstance().buildPtjLocationTree(zone, states,
							districts, divisions, cities));
		}
		this.getManagedBean().setLocIndex(-1);
	}
	
   private List<LocationDTO> getLocations(TreeNode zoneLocation) {
			List<LocationDTO> results = new ArrayList<LocationDTO>();
			if (zoneLocation.isLeaf()) {
				LocationDTO locationDTO = (LocationDTO) zoneLocation.getData();
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
				result = locationDTO.getZone();
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
		List<LocationDTO> locationDTOs = getLocations(rootZoneLocation);

		ScQuoteZone zone = getZone(rootZoneLocation);
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTOs(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTOs(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTOs(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTOs(locationDTOs);
		getManagedBean().setRootZoneTree(
				LocationSingleton.getInstance().buildLocationZoneTree(1L,
						states, districts, divisions, cities, 3));
		int i = zoneIndex;

		this.getManagedBean().setZoneIndex(i);
		/*
		 * getManagedBean().getZoneTrees().remove(
		 * LocationSingleton.getInstance().buildZoneTree(zone, states,
		 * districts, divisions, cities));
		 */
		getManagedBean().setZoneName(zone.getZoneName());
		/*
		 * if (zone != null) { getManagedBean().setZoneName(zone.getZoneName());
		 * }
		 */
	}
	
	/**
     * editLocation Method for location in simple quote
	*/
	public void editLocation(TreeNode rootZoneLocation, int LocIndex) {
		List<LocationDTO> locationDTOs = new ArrayList<LocationDTO>();
		//locationDTOs.addAll(getStateLocation(rootZoneLocation));
		//locationDTOs.addAll(getDivisionLocation(rootZoneLocation));
		//locationDTOs.addAll(getDistrictLocation(rootZoneLocation));
		locationDTOs  = getAllLocations(rootZoneLocation);
		
		
		//locationDTOs.clear();

		//ScQuoteZone zone = getZone(rootZoneLocation);
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTOs(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTOs(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTOs(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTOs(locationDTOs);
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationZoneTree(1L,
						states, districts, divisions, cities, 3));
		int i = LocIndex;

		this.getManagedBean().setLocIndex(i);
		//getManagedBean().setZoneName(zone.getZoneName());
		
	}

    private List<LocationDTO> getAllLocations(TreeNode treeNode) {
    	List<LocationDTO> results = new ArrayList<LocationDTO>();
    	if(treeNode.getChildCount() > 0){
    		for (TreeNode stateNode : treeNode.getChildren()) {
    			// state location
    			LocationDTO locationDTO = (LocationDTO) stateNode.getData();
    			results.add(locationDTO);

    			for (TreeNode divNode : stateNode.getChildren()) {
    				// division location
    				LocationDTO divLocationDTO = (LocationDTO) divNode.getData();
    				results.add(divLocationDTO);

    				for (TreeNode distNode : divNode.getChildren()) {
    					//district location
    					LocationDTO distLocationDTO = (LocationDTO) distNode.getData();
    					results.add(distLocationDTO);
    				}
    			}
    		}
    	} else{
    		LocationDTO parentNode = (LocationDTO) treeNode.getData();
    		results.add(parentNode);
    	}
    	return results;
    }
	
	private	List<ScQuoteZone>	getQuoteZones(List<TreeNode> treeNodes, ScQuote quote){
		List<ScQuoteZone> results	=	new ArrayList<ScQuoteZone>();
		for(TreeNode treeNode: treeNodes){
			for (TreeNode childNode : treeNode.getChildren()) {
			LocationDTO locationDTO = (LocationDTO)childNode.getData();
			if(locationDTO.getZone() != null){
				ScQuoteZone zone	=	locationDTO.getZone();
				List<PmState>	pmStates	=	getPmStates(treeNode.getChildren());
				List<ScQuoteZoneLocation>	zoneStateLocations	=	new ArrayList<ScQuoteZoneLocation>();
				if(pmStates!= null && pmStates.size() > 0){
					for(PmState pmState: pmStates){
						ScQuoteZoneLocation	zoneStateLocation	=	new ScQuoteZoneLocation();
						zoneStateLocation.setZone(zone);
						zoneStateLocation.setPmState(pmState);
						zoneStateLocations.add(zoneStateLocation);
					}
				}
				List<ScQuoteZoneLocation>	zoneDivisionLocation	=	new ArrayList<ScQuoteZoneLocation>();
				List<PmDivision>	pmDivisions	=	getPmDivisions(treeNode.getChildren());
				if(pmDivisions!= null && pmDivisions.size() > 0){
					for(PmDivision pmDivision: pmDivisions){
						ScQuoteZoneLocation	zoneStateLocation 	=	new ScQuoteZoneLocation();
						zoneStateLocation.setZone(zone);
						zoneStateLocation.setDivision(pmDivision);
						zoneDivisionLocation.add(zoneStateLocation);
					}
				}
				List<ScQuoteZoneLocation>	zoneDistrictLocation	=	new ArrayList<ScQuoteZoneLocation>();
				List<PmDistrict>	pmDistricts	=	getPmDistricts(treeNode.getChildren());
				if(pmDivisions!= null && pmDivisions.size() > 0){
					for(PmDistrict pmDistrict: pmDistricts){
						ScQuoteZoneLocation	zoneStateLocation 	=	new ScQuoteZoneLocation();
						zoneStateLocation.setZone(zone);
						zoneStateLocation.setPmDistrict(pmDistrict);
						zoneDistrictLocation.add(zoneStateLocation);
					}
				}
				zone.setZoneLocations(zoneStateLocations);
				zone.getZoneLocations().addAll(zoneDivisionLocation);
				zone.getZoneLocations().addAll(zoneDistrictLocation);
				zone.setQuote(quote);
				results.add(zone);
			}
     	}
		}
		return results;
	}	
	
	private List<PmState> getPmStates(List<TreeNode> treeNodes) {
		List<PmState> results = new ArrayList<PmState>();
		for (TreeNode treeNode : treeNodes) {
			for (TreeNode stateNode : treeNode.getChildren()) {
				if (treeNode.getChildren() != null) {
					LocationDTO locationDTO = (LocationDTO) stateNode.getData();
					if (locationDTO.getState() != null) {
						results.add(locationDTO.getState());
					}
				}
			}
		}
		return results;
	}
	
	private List<PmDivision> getPmDivisions(List<TreeNode> treeNodes) {
		List<PmDivision> results = new ArrayList<PmDivision>();
		for (TreeNode treeNode : treeNodes) {
			for (TreeNode childNode : treeNode.getChildren()) {
				for (TreeNode divNode : childNode.getChildren()) {
					if (divNode.getChildren() != null) {
						LocationDTO locationDTO = (LocationDTO) divNode.getData();
						if (locationDTO.getDivision() != null) {
							results.add(locationDTO.getDivision());
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
				LocationDTO locationDTO = (LocationDTO) treeNode.getData();
				if (locationDTO.getDistrict() != null) {
					results.add(locationDTO.getDistrict());
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
			for (TreeNode treeNode : treeNodes) {
			List<PmState>	states	=	getPmStates(treeNode.getChildren());
			if(states != null && states.size() > 0){
				for(PmState state: states){
					ScLocationStateCriteria stateCriteria	=	new ScLocationStateCriteria();
					stateCriteria.setCriteriaValue(String.valueOf(state.getStateId()));
					stateCriteria.setQuote(quote);
					results.add(stateCriteria);
				}
			}
			}
			return results;
	  }
		
	private	List<ScLocationDivisionCriteria> getLocationDivisionCriterias(List<TreeNode> treeNodes ,ScQuote quote){
			List<ScLocationDivisionCriteria>	results = new ArrayList<ScLocationDivisionCriteria>();
			for (TreeNode treeNode : treeNodes) {
			List<PmDivision>	divisions	=	getPmDivisions(treeNode.getChildren());
			if(divisions != null && divisions.size() > 0){
				for(PmDivision division: divisions){
					ScLocationDivisionCriteria  divisionCriteria = new ScLocationDivisionCriteria();
					divisionCriteria.setCriteriaValue(String.valueOf(division.getDivisionId()));
					divisionCriteria.setQuote(quote);
					results.add(divisionCriteria);
				}
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
					districtCriteria.setCriteriaValue(String.valueOf(district.getDistrictId()));
					districtCriteria.setQuote(quote);
					results.add(districtCriteria);
				}
			}
			}
			return results;
	  }	
		
	
		
	private List<ScBusinessNatureQuoteCriteria> getBusinessNatureCriterias(List<ScBusinessNatureQuoteCriteria> businessNatureQuoteCriterias ,ScQuote quote){
			List<ScBusinessNatureQuoteCriteria> businessNatureCriterias = new ArrayList<ScBusinessNatureQuoteCriteria>();
			if(businessNatureQuoteCriterias != null){
				for (ScBusinessNatureQuoteCriteria businessNatureQuoteCriteria : businessNatureQuoteCriterias) {
					ScBusinessNatureQuoteCriteria natureQuoteCriteria = new ScBusinessNatureQuoteCriteria();
					natureQuoteCriteria.setCriteriaValue(String.valueOf(businessNatureQuoteCriteria.getBusinessNature().getBusinessNatureId()));
					natureQuoteCriteria.setQuote(quote);
					businessNatureCriterias.add(natureQuoteCriteria);
				}
			}
			return businessNatureCriterias;
		}
	
	private List<ScSupplierMofNoCriteria> geSupplierCriterias(List<ScSupplierMofNoCriteria> mofNoCriterias){
		List<ScSupplierMofNoCriteria> supplierMofNoCriterias = new ArrayList<ScSupplierMofNoCriteria>();
		if(mofNoCriterias != null){
			for (ScSupplierMofNoCriteria supplierMofNoCriteria : mofNoCriterias) {
				ScSupplierMofNoCriteria mofNoCriteria = new ScSupplierMofNoCriteria();
				mofNoCriteria.setCriteriaValue(String.valueOf(supplierMofNoCriteria.getSmSupplier().getSupplierId()));
				supplierMofNoCriterias.add(mofNoCriteria);
			}
		}
		return supplierMofNoCriterias;
 }
	
	
	private List<ScGovEPNoCriteria> getGovtSeller(List<ScGovEPNoCriteria>  govEPNoCriterias){
		List<ScGovEPNoCriteria> scGovEPNoCriterias = new ArrayList<ScGovEPNoCriteria>();
		if( govEPNoCriterias != null){
			for (ScGovEPNoCriteria govEPNoCriteria :  govEPNoCriterias) {
				ScGovEPNoCriteria scGovEPNoCriteria = new ScGovEPNoCriteria();
				scGovEPNoCriteria.setCriteriaValue(String.valueOf(govEPNoCriteria.getSmSupplier().getSupplierId()));
				scGovEPNoCriterias.add(scGovEPNoCriteria);
			}
		}
		return scGovEPNoCriterias;
	}
	 
	 public void deleteTreeNodes() {
		    debugLogger("deleteTreeNodes");
			if (getManagedBean().getSelectedNodes().length >= 0 && getManagedBean().getSelectedNodes() != null ) {
				for (TreeNode selectedNode : getManagedBean().getSelectedNodes()) {
					
					for (TreeNode treeNode : getManagedBean().getZoneTrees()) {
						if(treeNode.equals(selectedNode)){
							 List<TreeNode>	treeNodes =	getManagedBean().getZoneTrees();
							 treeNodes.remove(selectedNode);
							 getManagedBean().setZoneTrees(treeNodes);
						}
					}
				}
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
	
	private void populateUserDivisionList() {
		debugLogger("populateUserDivisionList");
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
			getManagedBean().setUserDivisionList(userDivisionList);
		}
	}

	/**
	 * Event for justification type drop down in simpleQuote
	 */
	public void justificationTypeEvent() {
		debugLogger("justificationTypeEvent");
		if (getManagedBean().getScQuote().getJustificationType()
				.equalsIgnoreCase(ParameterTypeConst.JUSTIFICATION_TYPE_OTHER)) {
			getManagedBean().setJustTypeOption(Boolean.TRUE);
		} else {
			getManagedBean().setJustTypeOption(Boolean.FALSE);
		}
	}

	/**
	 * Page navigation to catalogue search page in simpleQuote
	 */
	public String backToPervious() {
		debugLogger("backToPervious");
		return "catalogueSearch";
	}

	 public void  getCurrentStatus(Long statusId) {
			if (statusId != null) {
				 getManagedBean().setSimpleQuoteStatus(getStatusDescById(statusId));
			 	
			}
		  }
		
	public SelectItem[] getLocationLevelDropdown() {
		SelectItem[] selectItems = new SelectItem[4];
		selectItems[0] = new SelectItem(null, "- Select One -");
		selectItems[1] = new SelectItem(1, "State");
		selectItems[2] = new SelectItem(2, "Division");
		selectItems[3] = new SelectItem(3, "District");
		return selectItems;
	}
	
	public void durationEvent(){
		try {
			if(getManagedBean().getScQuote().getStartDate() != null && 
					getManagedBean().getScQuote().getEndDate() != null){
				String sdate = DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT,  getManagedBean().getScQuote().getStartDate());
				String edate = DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT,  getManagedBean().getScQuote().getEndDate());
				getManagedBean().setDuration(
						hoursDifference(DateUtils.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT, edate), DateUtils.convertStringToDate(
								CommonConstants.DEFAULT_DATE_FORMAT, sdate)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	
	   private static int minsDifference(Date toDate, Date fromDate) {
			final int MILLI_TO_MINS = 1000 * 60;
			return (int) (toDate.getTime() - fromDate.getTime()) / MILLI_TO_MINS;
		}
	
		private static int hoursDifference(Date toDate, Date fromDate) {
			final int MILLI_TO_HOURS = 1000 * 60 * 60;
			return (int) (toDate.getTime() - fromDate.getTime()) / MILLI_TO_HOURS;
		}
	
		public static int daysBetween(Date toDate, Date fromDate) {
			final int MILLI_TO_DAYS = 1000 * 60 * 60 * 24;
			return (int) ((toDate.getTime() - fromDate.getTime()) / MILLI_TO_DAYS );
		}
		
	public void calcDuration(Integer duration) {
		if (duration != null) {
			ScQuote quote = getManagedBean().getScQuote();
			if (quote != null) {
				quote = new ScQuote();
			}
			Calendar startDate = Calendar.getInstance();
			quote.setStartDate(startDate.getTime());
			
			startDate.setTime(new Date());
			startDate.add(Calendar.HOUR_OF_DAY, duration);
			Date endDate = startDate.getTime();
			
			quote.setEndDate(endDate);
			getManagedBean().setScQuote(quote);
		}
	}
	
	public void tabEvent(){
	
		if(getManagedBean().getProcurementType() != null){
		   if(getManagedBean().getIsPanel().equals(1)){
			  getManagedBean().setLocAndBusinessFlag(Boolean.FALSE);
			  getManagedBean().setG2GFlag(Boolean.FALSE);
		   } else {
			   if (getManagedBean().getProcurementType()
							.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
				   getManagedBean().setLocAndBusinessFlag(Boolean.FALSE);
				   getManagedBean().setG2GFlag(Boolean.TRUE);
			    } else if (getManagedBean().getProcurementType()
					 .equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL)|| (getManagedBean().getProcurementType()
					 .equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC) || (getManagedBean().getProcurementType()
					 .equalsIgnoreCase(ParameterConst.PROC_TYPE_EMERGENCY)))) {
					 getManagedBean().setLocAndBusinessFlag(Boolean.TRUE);
					 getManagedBean().setG2GFlag(Boolean.FALSE);
					}
				}
			}
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
				productTplSpecBean.setSpecMaster(cloneSpecMaster);
				getManagedBean().setProductTemplateSpecBean(productTplSpecBean);

			   // service Tpl Spe
			} else  if(ParameterConst.SQ_ITEM_LIST_SERVICE.equalsIgnoreCase(specMaster.getItemTypeCode().getParameterCode())){
				TplSpecMaster cloneSpecMaster = (TplSpecMaster) BeanUtils.cloneBean(specMaster);
				serviceTplSpecBean.setSpecMaster(cloneSpecMaster);
				serviceTplSpecBean.setSpecMaster(specMaster);
				getManagedBean().setServiceTemplateSpecBean(serviceTplSpecBean);
			}
		   }
		   
		} finally {
			templateSpecSO = null;
		}

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
	
	public void supplierListListener(List<SmSupplier> suppliers){
		debugLogger("supplierListListener");
		List<ScQuoteSupplier> supplierList = new ArrayList<ScQuoteSupplier>();
		
		if (suppliers != null){
		   for (SmSupplier supplier : suppliers) {
			   ScQuoteSupplier quoteSupplier = new ScQuoteSupplier();
				quoteSupplier.setSmSupplier(supplier);
				quoteSupplier.setQuote(getManagedBean().getScQuote());
				supplierList.add(quoteSupplier);
			}
			getManagedBean().setSupplierList(supplierList);
		}
	}
	
	public void businessNatureListener(List<PmBusinessNature> businessNatures){
		debugLogger("businessNatureListener");
		List<ScBusinessNatureQuoteCriteria> natureQuoteCriterias = new ArrayList<ScBusinessNatureQuoteCriteria>();
		if (businessNatures != null){
		   for (PmBusinessNature busiNature : businessNatures) {
			   ScBusinessNatureQuoteCriteria natureQuoteCriteria = new ScBusinessNatureQuoteCriteria();
			   natureQuoteCriteria.setBusinessNature(busiNature);
			   natureQuoteCriterias.add(natureQuoteCriteria);
			}
		   getManagedBean().setBusinessNatureCriterias(natureQuoteCriterias);
		}
	}
	
	public void govtSellerListener(List<PmOrgProfile> orgProfiles){
		debugLogger("govtSellerListener");
		List<ScQuoteSupplier> scQuoteSuppliers = new ArrayList<ScQuoteSupplier>();
		if (orgProfiles != null){
		   for (PmOrgProfile orgProfile : orgProfiles) {
			   ScQuoteSupplier scQuoteSupplier = new ScQuoteSupplier();
			   scQuoteSupplier.setSmSupplier(orgProfile.getSmSupplier());
			   scQuoteSupplier.setQuote(getManagedBean().getScQuote());
			   scQuoteSuppliers.add(scQuoteSupplier);
			}
		   getManagedBean().setGovtSellers(scQuoteSuppliers);
		}
	}

	public void panelItemListener(List<CtPanelSupplier>	ctPanelSuppliers){
		debugLogger("panelItemListener");
		List<ScQuoteSupplier> panelSuppliers = new ArrayList<ScQuoteSupplier>();
		
		if (ctPanelSuppliers != null){
			for (CtPanelSupplier panelSupplier : ctPanelSuppliers) {
				ScQuoteSupplier scQuoteSupplier = new ScQuoteSupplier();
				scQuoteSupplier.setSmSupplier(panelSupplier.getSupplier());
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
		
	public PmOrgProfile ptjNameEvent() {
		if (getManagedBean().getOrgProfileId() != null) {
			for (PmOrgProfile orgProfile : getManagedBean()
					.getUserDivisionList()) {
				if (orgProfile.getOrgProfileId().equals(
						getManagedBean().getOrgProfileId())) {
					getManagedBean().setPtjName(orgProfile.getOrgName());
					return orgProfile;
				}
			}
		}
		return null;
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
				if(natureQuoteCriteria.getBusinessNature() !=null){
					busiNatureCriteria.append(natureQuoteCriteria.getBusinessNature().getBusinessNatureId())
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
		List<LocationDTO> locationDTOs = new ArrayList<LocationDTO>();
		for (TreeNode treeNode : getManagedBean().getLocationTrees()) {
			List<LocationDTO> locations  = getAllLocations(treeNode);
			locationDTOs.addAll(locations);
		}

		if(locationDTOs != null && !locationDTOs.isEmpty() ){
			for (LocationDTO locationDTO : locationDTOs) {

				if(locationDTO.getState() != null){
					stateCriteria.append(locationDTO.getState().getStateId())
					.append(",");
				}
				if(locationDTO.getDivision() != null){
					divCriteria.append(locationDTO.getDivision().getDivisionId())
					.append(",");
				}
				if(locationDTO.getDistrict() != null){
					distCriteria.append(locationDTO.getDistrict().getDistrictId())
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
		}
		
		criteria.append("recordStatus^1");

		System.out.println("criteria :"+criteria.toString());
		return criteria.toString();
	}
	
	
	/**
	 * render the view based on the task id
	 */
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		TemplateSpecBean productTplSpecBean = getManagedBean().getProductTemplateSpecBean();
		TemplateSpecBean serviceTplSpecBean = getManagedBean().getServiceTemplateSpecBean();
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
			String	dpMode	=	directPurchaseDataType.getDpMode();

			if(ParameterConst.REQUEST_NOTE_SQ.equalsIgnoreCase(dpMode)){ 
				// Simple Quote
			
				// TODO based on status pending submission or supplier resp
				if(((Long)(long)res.getDocStatus()).equals(DPStatusConstants.SQ_PENDING_SUBMISSION)){
					getManagedBean().setTaskId(null);
				}else{
					getManagedBean().setTaskId(taskId);
				}

				IQuoteSO quoteSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQuoteSO.class);
				QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
				Long quoteId = Long.valueOf(directPurchaseDataType.getDpId());
				quoteReqDTO.setQuoteId(quoteId);
				quoteReqDTO.setFetchSubEntity(true);
				QuoteResDTO  quoteResDTO = quoteSO.getQuoteDtl((quoteReqDTO));

				ScQuote scQuote = quoteResDTO.getQuoteBidDtl();
				scQuote.setProcurementType(ParameterConst.PROC_TYPE_NORMAL);
				// remarks
				List<ScSQRemark> results= new ArrayList<ScSQRemark>();
				if(scQuote.getRemarks() != null){
					for (ScSQRemark remark : scQuote.getRemarks()) {
						remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
						remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
						remark.setChangedBy(remark.getCreatedBy());
						remark.setUser(facesScopes.getUserSessionBean());
						results.add(remark);
					}
				}
				scQuote.setRemarks(results);
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
			getManagedBean().setProcessName(res.getProcessName());
			getManagedBean().setOutComes(res.getAction());
			getManagedBean().setScQuote(scQuote);
			getManagedBean().setDurationDropdown(ParameterConst.SQ_DURATION_DAY);
			getManagedBean().setIsPanel(scQuote.getIsPanel());
			getManagedBean().setIsZonal(scQuote.getIsZonal());
			getManagedBean().setProcurementType(scQuote.getProcurementType());
			getManagedBean().setDuration(0);
			getManagedBean().setPtjName(scQuote.getRequestNote().getPmOrgProfile().getOrgName());
			
			// business nature details
			IBusinessNatureSO businessNatureSO = (IBusinessNatureSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IBusinessNatureSO.class);
			BusinessNatureReqDTO businessNatureReqDTO = new BusinessNatureReqDTO();
			 List<PmBusinessNature>	businessNatures = new ArrayList<PmBusinessNature>();
			if(scQuote.getBusinessNatureCriterias() !=null && scQuote.getBusinessNatureCriterias().size() > 0){
				for (ScBusinessNatureQuoteCriteria natureQuoteCriteria : scQuote.getBusinessNatureCriterias()) {
					businessNatureReqDTO.setBusinessNatureId(Long.valueOf(natureQuoteCriteria.getCriteriaValue()).longValue());
					BusinessNatureResDTO resDTO = businessNatureSO.retrieveBusinessNatureById(businessNatureReqDTO);
					businessNatures.add(resDTO.getBusinessNature());
				}
			}
			
			if(businessNatures !=null && businessNatures.size() > 0){
				businessNatureListener(businessNatures);
			}
			
			initializeRemarks(scQuote);
			getCurrentStatus(res.getDocStatus().longValue()); 
			getManagedBean().setPmOrgProfile(null);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "simpleQuote");
		}
	}
	}
	
	
	/**
	 * render the suppliver view based on the task id
	 */
	public void supplierView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		TemplateSpecBean productTplSpecBean = getManagedBean().getProductTemplateSpecBean();
		TemplateSpecBean serviceTplSpecBean = getManagedBean().getServiceTemplateSpecBean();
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
			String	dpMode	=	directPurchaseDataType.getDpMode();

			if(ParameterConst.REQUEST_NOTE_SQ.equalsIgnoreCase(dpMode)){ 
				// TODO based on status pending submission or supplier resp
				if(((Long)(long)res.getDocStatus()).equals(DPStatusConstants.SQ_PENDING_SUBMISSION)){
					getManagedBean().setTaskId(null);
				}else{
					getManagedBean().setTaskId(taskId);
				}

				IQuoteSO quoteSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQuoteSO.class);
				QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
				Long quoteId = Long.valueOf(directPurchaseDataType.getDpId());
				quoteReqDTO.setQuoteId(quoteId);
				//TODO have to change supplier id as login user id
				quoteReqDTO.setSupplierId(45L);
				quoteReqDTO.setFetchSubEntity(true);
				QuoteResDTO  quoteResDTO = quoteSO.getQuoteDtlForSupplier(quoteReqDTO);
				ScQuote scQuote = quoteResDTO.getQuoteBidDtl();
				scQuote.setProcurementType(ParameterConst.PROC_TYPE_NORMAL);
				scQuote.setCreatedDate(DateUtils.getCurrentDate());
				// remarks
				List<ScSQRemark> results= new ArrayList<ScSQRemark>();
				if(scQuote.getRemarks() != null){
					for (ScSQRemark remark : scQuote.getRemarks()) {
						remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
						remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
						remark.setChangedBy(remark.getCreatedBy());
						remark.setUser(facesScopes.getUserSessionBean());
						results.add(remark);
					}
				}
				scQuote.setRemarks(results);
                // service and product
				if(quoteResDTO.getProductAnswersTpl() != null && quoteResDTO.getProductAnswersTpl().size() >0){
					productTplSpecBean.setSpecAnswerList(quoteResDTO.getProductAnswersTpl());
					productTplSpecBean.setSpecMaster(new TplSpecMaster());
					getManagedBean().setProductTemplateSpecBean(productTplSpecBean);
					getManagedBean().setProductFlag(Boolean.TRUE);
				}else{
					getManagedBean().setProductFlag(Boolean.FALSE);
				}
				
				if(quoteResDTO.getServiceAnswersTpl() != null && quoteResDTO.getServiceAnswersTpl().size() >0){
					serviceTplSpecBean.setSpecAnswerList(quoteResDTO.getServiceAnswersTpl());
					serviceTplSpecBean.setSpecMaster(new TplSpecMaster());
					getManagedBean().setServiceTemplateSpecBean(serviceTplSpecBean);
					getManagedBean().setServiceFlag(Boolean.TRUE);
				} else{
					getManagedBean().setServiceFlag(Boolean.FALSE);
				}
			
			getManagedBean().setProcessName(res.getProcessName());
			getManagedBean().setOutComes(res.getAction());
			getManagedBean().setScQuote(scQuote);
			getManagedBean().setDurationDropdown(ParameterConst.SQ_DURATION_DAY);
			getManagedBean().setIsPanel(scQuote.getIsPanel());
			getManagedBean().setIsZonal(scQuote.getIsZonal());
			getManagedBean().setProcurementType(scQuote.getProcurementType());
			getManagedBean().setDuration(0);
			getManagedBean().setPtjName(scQuote.getRequestNote().getPmOrgProfile().getOrgName());
			initializeRemarks(scQuote);
			getCurrentStatus(res.getDocStatus().longValue()); 
			getManagedBean().setPmOrgProfile(null);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "supplerView");
		}
	}
	}
}