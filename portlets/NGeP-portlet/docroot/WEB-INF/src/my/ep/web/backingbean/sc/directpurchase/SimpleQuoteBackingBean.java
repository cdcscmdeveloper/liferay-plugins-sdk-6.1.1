/**
 * Copyright © 2013 Archisoft. All rights reserved.
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

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.sc.directpurchase.IQuoteSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
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
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.directpurchase.QuoteReqDTO;
import my.ep.dto.sc.directpurchase.QuoteResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
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
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.directpurchase.simpleQuoteManagedBean;
import my.ep.web.sc.directpurchase.common.DPStatusConstants;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.model.TreeNode;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
/**
 * 
 * @author sri
 * @since 25.02.2014
 * 
 */
@ManagedBean(name = "simpleQuoteBackingBean")
@RequestScoped
public class SimpleQuoteBackingBean extends
		BaseBackingBean<simpleQuoteManagedBean> {
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
			//scQuote.setIsPanel(1);
			getManagedBean().setIsPanel(1);
			scQuote.setIsZonal(1);
			scQuote.setMultipleQuote(true);
			//getManagedBean().setScQuote(scQuote);
		} else {
			//scQuote.setIsPanel(0);
			getManagedBean().setIsPanel(0);
			scQuote.setIsZonal(0);
			scQuote.setMultipleQuote(true);
			//().setScQuote(scQuote);
		}
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
		workFlowStatus.setCurrent(true);
		workFlowStatuses.add(workFlowStatus);
		getCurrentStatus(status.getStatusId());
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
		// TODO default value to Procurement type and duration drop down listing
		getManagedBean().setDurationDropdown(ParameterConst.SQ_DURATION_DAY);
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
			
			//TODO hard code isZonal
			quote.setProcurementType(getManagedBean().getProcurementType());
			quote.setIsPanel(getManagedBean().getIsPanel());
			quote.setIsZonal(1);
			
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
				quoteDoc.setSpecMaster((getManagedBean().getProductTemplateSpecBean().getSpecMaster()));
				quoteDocs.add(quoteDoc);
				quote.setQuoteDocs(quoteDocs);
			}

			// tpl spec - service
			if(getManagedBean().getServiceTemplateSpecBean().getSpecMaster() != null){
				List<ScQuoteDoc> quoteDocs = new ArrayList<ScQuoteDoc>();
				ScQuoteDoc quoteDoc = new ScQuoteDoc();
				quoteDoc.setSpecMaster((getManagedBean().getServiceTemplateSpecBean().getSpecMaster()));
				quoteDocs.add(quoteDoc);
				quote.setQuoteDocs(quoteDocs);
			}
			
			FacesScopes facesScopes = new FacesScopes();
			String token = facesScopes.getBPMAuthenticationCtxToken();
			reqDTO.setToken(token);
			reqDTO.setSubmit(false);
			reqDTO.setOrgProfileId(getManagedBean().getOrgProfileId());
			reqDTO.setQuoteBidDtl(quote);
			QuoteResDTO quoteResDTO = quoteSO.saveQuoteDtl(reqDTO);
			if (quoteResDTO.isSuccessful()) {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler()
						.handleNavigation(fc, null, "acknowledge");
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
			List<ScQuoteSupplier> suppliers = new ArrayList<ScQuoteSupplier>();
			ScQuoteSupplier quoteSupplier = new ScQuoteSupplier();
			ScQuote quote = getManagedBean().getScQuote();
			quoteSupplier.setSmSupplier(getManagedBean().getCmSupplierItem().getSmSupplier());
			quoteSupplier.setQuote(quote);
			suppliers.add(quoteSupplier);
			quote.setScQuoteSuppliers(suppliers);
			
			//TODO TEMP
			quote.setProcurementType(getManagedBean().getProcurementType());
			quote.setIsPanel(getManagedBean().getIsPanel());
			quote.setIsZonal(1);
			
			quote.setQuoteNo(quoteSO.generateQuoteNo());
			ScRequestItem requestItem = new ScRequestItem();
			List<ScRequestItem> requestItems = new ArrayList<ScRequestItem>();
			requestItems.add(requestItem);
			ScRequestNote requestNote = new ScRequestNote();
			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
			requestNote.setRequestNoteNo(directPurchaseSO.generateRequestNoteNo());
			requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SQ);
			requestNote.setPmOrgProfile(ptjNameEvent());
			quote.setRequestNote(requestNote);
			quote.setCreatedDate(DateUtils.getCurrentDate());
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
			
			//remarks
			quote.setRemarks(initializeRemarks(quote));
			
			// tpl spec - product
			if(getManagedBean().getProductTemplateSpecBean().getSpecMaster() != null){
				List<ScQuoteDoc> quoteDocs = new ArrayList<ScQuoteDoc>();
				ScQuoteDoc quoteDoc = new ScQuoteDoc();
				quoteDoc.setSpecMaster((getManagedBean().getProductTemplateSpecBean().getSpecMaster()));
				quoteDocs.add(quoteDoc);
				quote.setQuoteDocs(quoteDocs);
			}
			
			// tpl spec - service
			if(getManagedBean().getServiceTemplateSpecBean().getSpecMaster() != null){
				List<ScQuoteDoc> quoteDocs = new ArrayList<ScQuoteDoc>();
				ScQuoteDoc quoteDoc = new ScQuoteDoc();
				quoteDoc.setSpecMaster((getManagedBean().getServiceTemplateSpecBean().getSpecMaster()));
				quoteDocs.add(quoteDoc);
				quote.setQuoteDocs(quoteDocs);
			}

			 FacesScopes facesScopes = new FacesScopes();
			 String token = facesScopes.getBPMAuthenticationCtxToken();
			 reqDTO.setToken(token);
			 reqDTO.setSubmit(true);
			 reqDTO.setOrgProfileId(getManagedBean().getOrgProfileId());
			 reqDTO.setQuoteBidDtl(quote);
			 QuoteResDTO quoteResDTO = quoteSO.saveQuoteDtl(reqDTO);
			if (quoteResDTO.isSuccessful()) {
				ScQuote scQuote = quoteResDTO.getQuoteBidDtl();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, 
						null,
						MessageUtils.getMessage(MessageConst.INFO_700005, 
								new String[]{"Purchase In default",scQuote.getQuoteNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, 
								new FacesScopes().getUserLocale())));
				return CommonUIConst.ACKNOWLEDGE_PAGE;
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
	
	 public void updateBpmTask(String outcome) {
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
					boolean result = resBpm != null ? resBpm.isSuccessful() : false;
					if(resBpm.isSuccessful()){
					  FacesContext fc = FacesContext.getCurrentInstance();
					  fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "acknowledge");
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
	
	 public void byLocAndBusinessEvent() {
		 debugLogger("byBusinessEvent");
		 if (getManagedBean().isByLocality()) {
			 getManagedBean().setActiveIndex(0);
			 getManagedBean().setSuppListFlag(true);
		 } 
		 if (getManagedBean().isByBusinessNature()) {
			 getManagedBean().setActiveIndex(1);
			 getManagedBean().setSuppListFlag(false);
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
			getManagedBean().setLocAndBusinessFlag(false);
			getManagedBean().setG2GFlag(false);
			getManagedBean().setByBusinessNature(false);

		}else {
			getManagedBean().setLocAndBusinessFlag(true);
			getManagedBean().setG2GFlag(false);
			getManagedBean().setByBusinessNature(false);
		}

		if (getManagedBean().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL)) {
			getManagedBean().setProcTypeOption(false);
			getManagedBean().setJustTypeOption(false);

		} else if (getManagedBean().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			getManagedBean().setProcTypeOption(false);
			getManagedBean().setJustTypeOption(false);

		} else if (getManagedBean().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			getManagedBean().setProcTypeOption(true);
			getManagedBean()
			.setJustDropdown(
					getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_ADHOC));

		} else if (getManagedBean().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_EMERGENCY)) {
			getManagedBean().setProcTypeOption(true);
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
	
	/*public void deleteSupplier() {
		debugLogger("deleteSupplier");
		if (getManagedBean().getSelectedSuppMofNoCriterias() != null) {
			for (ScSupplierMofNoCriteria supplierMofNoCriteria : getManagedBean()
					.getSelectedSuppMofNoCriterias()) {
				if(getManagedBean().getSupplierMofNoCriterias().contains(supplierMofNoCriteria)){
					List<ScSupplierMofNoCriteria>  supplierMofNoCriterias = getManagedBean().getSupplierMofNoCriterias();
					supplierMofNoCriterias.remove(supplierMofNoCriteria);
					getManagedBean().setSupplierMofNoCriterias(supplierMofNoCriterias);
				}
			}
		}
	}*/
	
/*	public void deleteGovtSeller() {
		debugLogger("deleteGovtSeller");
		if (getManagedBean().getSelectedGovEPNoCriterias() != null) {
			for (ScGovEPNoCriteria govEPNoCriteria : getManagedBean()
					.getSelectedGovEPNoCriterias()) {
				if(getManagedBean().getGovEPNoCriterias().contains(govEPNoCriteria)){
					List<ScGovEPNoCriteria>  govEPNoCriterias = getManagedBean().getGovEPNoCriterias();
					govEPNoCriterias.remove(govEPNoCriteria);
					getManagedBean().setGovEPNoCriterias(govEPNoCriterias);
				}
			}
		}
	}*/
	
	/*public void deletePanelSupplier() {
		debugLogger("deletePanelSupplier");
		if (getManagedBean().getSelectedPanelItemSuppliers() != null) {
			for (ScQuoteSupplier scQuoteSupplier : getManagedBean()
					.getSelectedPanelItemSuppliers()) {
				if(getManagedBean().getPanelItemSuppliers().contains(scQuoteSupplier)){
					List<ScQuoteSupplier>  suppliers = getManagedBean().getPanelItemSuppliers();
					suppliers.remove(scQuoteSupplier);
					getManagedBean().setPanelItemSuppliers(suppliers);
				}
			}
		}
	}	*/
	
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
					/*
					for (TreeNode treeNode : getManagedBean().getZoneTrees()) {
						if(treeNode.equals(selectedNode)){
							 List<TreeNode>	treeNodes =	getManagedBean().getZoneTrees();
							 treeNodes.remove(selectedNode);
							 getManagedBean().setZoneTrees(treeNodes);
						}
					}*/
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
			getManagedBean().setJustTypeOption(true);
		} else {
			getManagedBean().setJustTypeOption(false);
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
		   if(getManagedBean().getDuration() != null && getManagedBean().getScQuote().getStartDate() != null && 
				  getManagedBean().getScQuote().getEndDate() != null){
			String sdate = DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT,  getManagedBean().getScQuote().getStartDate());
			String edate = DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT,  getManagedBean().getScQuote().getEndDate());
			
			if(getManagedBean().getDurationDropdown().equals(ParameterConst.SQ_DURATION_DAY)){
			     getManagedBean().setDuration(daysBetween(DateUtils.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT, edate), DateUtils.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT, sdate)));
				
			} else if(getManagedBean().getDurationDropdown().equals(ParameterConst.SQ_DURATION_HOURS)){
				getManagedBean().setDuration(hoursDifference(DateUtils.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT, edate), DateUtils.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT, sdate)));
				
			} else if(getManagedBean().getDurationDropdown().equals(ParameterConst.SQ_DURATION_MINUTES)){
				getManagedBean().setDuration(minsDifference(DateUtils.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT, edate), DateUtils.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT, sdate)));
			} else {
				 getManagedBean().setDuration(0);
			}
		 } else {
			  getManagedBean().setDuration(0);
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
			  getManagedBean().setLocAndBusinessFlag(false);
			  getManagedBean().setG2GFlag(false);
		   } else {
			   if (getManagedBean().getProcurementType()
							.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
				   getManagedBean().setLocAndBusinessFlag(false);
				   getManagedBean().setG2GFlag(true);
			    } else if (getManagedBean().getProcurementType()
					 .equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL)|| (getManagedBean().getProcurementType()
					 .equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC) || (getManagedBean().getProcurementType()
					 .equalsIgnoreCase(ParameterConst.PROC_TYPE_EMERGENCY)))) {
					 getManagedBean().setLocAndBusinessFlag(true);
					 getManagedBean().setG2GFlag(false);
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
		ScQuoteSupplier quoteSupplier = new ScQuoteSupplier();
		if (suppliers != null){
		   for (SmSupplier supplier : suppliers) {
				quoteSupplier.setSmSupplier(supplier);
				quoteSupplier.setQuote(getManagedBean().getScQuote());
				supplierList.add(quoteSupplier);
				getManagedBean().setSupplierList(supplierList);
			}
		}
	}
	
	public void businessNatureListener(List<PmBusinessNature> businessNatures){
		debugLogger("businessNatureListener");
		List<ScBusinessNatureQuoteCriteria> natureQuoteCriterias = new ArrayList<ScBusinessNatureQuoteCriteria>();
		ScBusinessNatureQuoteCriteria natureQuoteCriteria = new ScBusinessNatureQuoteCriteria();
		if (businessNatures != null){
		   for (PmBusinessNature busiNature : businessNatures) {
			   natureQuoteCriteria.setBusinessNature(busiNature);
			   natureQuoteCriterias.add(natureQuoteCriteria);
			   getManagedBean().setBusinessNatureCriterias(natureQuoteCriterias);
			}
		}
	}
	
	public void govtSellerListener(List<PmOrgProfile> orgProfiles){
		debugLogger("govtSellerListener");
		List<ScQuoteSupplier> scQuoteSuppliers = new ArrayList<ScQuoteSupplier>();
		ScQuoteSupplier scQuoteSupplier = new ScQuoteSupplier();
		if (orgProfiles != null){
		   for (PmOrgProfile orgProfile : orgProfiles) {
			   scQuoteSupplier.setSmSupplier(orgProfile.getSmSupplier());
			   scQuoteSupplier.setQuote(getManagedBean().getScQuote());
			   scQuoteSuppliers.add(scQuoteSupplier);
			   getManagedBean().setGovtSellers(scQuoteSuppliers);
			}
		}
	}

	public void panelItemListener(List<CtPanelSupplier>	ctPanelSuppliers){
		debugLogger("panelItemListener");
		List<ScQuoteSupplier> panelSuppliers = new ArrayList<ScQuoteSupplier>();
		ScQuoteSupplier scQuoteSupplier = new ScQuoteSupplier();
		if (ctPanelSuppliers != null){
			for (CtPanelSupplier panelSupplier : ctPanelSuppliers) {
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
					getManagedBean().setPmOrgProfile(orgProfile);
				}
			}
		}
		return this.getManagedBean().getPmOrgProfile();
	}

	public String getSupplierListCriteria(){
		StringBuilder  criteria = new StringBuilder();
		//criteria.append("supplierId") .append("^").append("27,31,99").append("^").append(SearchOperator.OPERATOR_IN.value())
		//.append("|")
		criteria.append("smSupplierBusinesses.pmBusinessNature.businessNatureId").append("11,12").append("^").append(SearchOperator.OPERATOR_IN.value())
		.append("|")
		.append("recordStatus^1");

		System.out.println("criteria :"+criteria.toString());
		return criteria.toString();
	}

}