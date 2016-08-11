/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.simplequote;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.sc.directpurchase.IQuoteSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.directpurchase.QuoteReqDTO;
import my.ep.dto.sc.directpurchase.QuoteResDTO;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemGroup;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScBusinessNatureQuoteCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationDistrictCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationDivisionCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationStateCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteAttachment;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteDoc;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteSupplier;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteZone;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScSQRemark;
import my.ep.persistence.entity.sc.directpurchase.ScSQWorkFlowStatus;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmGstSupplier;
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
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.simplequote.SimpleQuoteManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;

// TODO: Auto-generated Javadoc

/**
 * The Class SimpleQuoteBackingBean.
 * 
 * @author sri
 * @since 25.02.2014
 */
@ManagedBean(name = "simpleQuoteBackingBean")
@RequestScoped
public class SimpleQuoteBackingBean extends
		SourcingBaseBackingBean<SimpleQuoteManagedBean> {

	/** The log. */
	private static Logger log = Logger.getLogger(SimpleQuoteBackingBean.class);

	/** The text before sign. */
	private String textBeforeSign;
	
	/** The simple quote page nav. */
	private String SIMPLE_QUOTE_PAGE_NAV = "simpleQuoteMain";

	/**
	 * Instantiates a new simple quote backing bean.
	 */
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
	 * View quote.
	 *
	 * @param scQuoteId the sc quote id
	 */
	public void viewQuote(long scQuoteId){
		debugLogger("viewQuote");
		TemplateSpecBean productTplSpecBean = getManagedBean().getProductTemplateSpecBean();
		TemplateSpecBean serviceTplSpecBean = getManagedBean().getServiceTemplateSpecBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		ScRequestNote scRequestNote =  null;
		try{
			QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
			quoteReqDTO.setQuoteId(scQuoteId);
			ScQuote scQuote = getQuoteSO().getQuoteDtl(quoteReqDTO).getQuoteBidDtl();
			getManagedBean().setScQuote(scQuote);

			if(scQuote != null){
				if (scQuote.getRequestNotes() != null
						&& scQuote.getRequestNotes().size() > 0) {
					scRequestNote = scQuote.getRequestNotes().get(0);
					getManagedBean().setRequestNote(scRequestNote);
				}

				if(scQuote.getJustificationType() == null){
					scQuote.setJustificationType(new PmReason());
				}
				// service and product
				if (scQuote.getQuoteDocs() != null) {
					for (ScQuoteDoc scQuoteDoc : scQuote.getQuoteDocs()) {
						TplSpecMaster specMaster = scQuoteDoc.getSpecMaster();
						if (ParameterConst.SQ_ITEM_LIST_PRODUCT
								.equalsIgnoreCase(specMaster.getItemTypeCode()
										.getParameterCode())) {
							if (specMaster.getTplSpecQuestions() == null) {
								specMaster
								.setTplSpecQuestions(new ArrayList<TplSpecQuestion>());
							}
							productTplSpecBean.setSpecMaster(specMaster);
							getManagedBean().setProductTemplateSpecBean(
									productTplSpecBean);
							// service Tpl Spe
						} else if (ParameterConst.SQ_ITEM_LIST_SERVICE
								.equalsIgnoreCase(specMaster.getItemTypeCode()
										.getParameterCode())) {

							if (specMaster.getTplSpecQuestions() == null) {
								specMaster
								.setTplSpecQuestions(new ArrayList<TplSpecQuestion>());
							}
							serviceTplSpecBean.setSpecMaster(specMaster);
							getManagedBean().setServiceTemplateSpecBean(
									serviceTplSpecBean);
						}
					}
				}

				// zone details
				if(scQuote.getQuoteZone() != null){
					if(scQuote.getQuoteZone().getTplSpecZoneDtls() != null && scQuote.getQuoteZone().getTplSpecZoneDtls().size() >0){
						List<TreeNode>	zoneTrees = new ArrayList<TreeNode>();
						for (TplSpecZoneDtl tplSpecZoneDtl : scQuote.getQuoteZone().getTplSpecZoneDtls() ) {
							if(tplSpecZoneDtl.getTplSpecZoneLocs() != null && tplSpecZoneDtl.getTplSpecZoneLocs().size()>0){
								zoneTrees.add(LocationSingleton.getInstance().constuctTreeNode(tplSpecZoneDtl, tplSpecZoneDtl.getTplSpecZoneLocs()));
								getManagedBean().setZoneTrees(zoneTrees);
							}
						}
					}
				}
				
				// by locality
				List<PmState> states = new ArrayList<PmState>();
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
					if(getManagedBean().getLocationTrees() == null || getManagedBean().getLocationTrees().isEmpty()){
						getManagedBean().getLocationTrees().add(
								LocationSingleton.getInstance().buildLocationTree(null, states,
										districts, divisions, null,TplSpecZoneLoc.class));
					}
				}

				if(scQuote.getRequestNotes() != null && scQuote.getRequestNotes().size()>0){
					getManagedBean().getRequestNote().setPmOrgProfile(scRequestNote.getPmOrgProfile());
				}
				
				// procurement  type category
				if(null == scQuote.getProcurementTypeCategory()){
					scQuote.setProcurementTypeCategory(new PmParameter());
				}

				// zone location level by default - district 
				if(scQuote.getZoneLocatonLevel() != null){
					getManagedBean().setLocationLevel(scQuote.getZoneLocatonLevel().getParameterCode());
				}

				// supplier location level by default - district 
				if(scQuote.getSupplierLocationLevel() != null){
					getManagedBean().setSupplierLocLevel(scQuote.getSupplierLocationLevel().getParameterCode());
				}

				// business nature details
				if(scQuote.getBusinessNatureCriterias() != null && scQuote.getBusinessNatureCriterias().size() > 0){
					getManagedBean().setByBusinessNature(Boolean.TRUE);
					getManagedBean().setBusinessNatureCriterias(scQuote.getBusinessNatureCriterias());
				}
				// supplier list
				if(scQuote.getScQuoteSuppliers() != null && scQuote.getScQuoteSuppliers().size() > 0){
					// panel supplier
					if(scQuote.isPanel()){
						getManagedBean().setPanelSuppliers(scQuote.getScQuoteSuppliers());
					} else {
						if(scQuote.getProcurementType() != null &&  scQuote.getProcurementType().getParameterCode()
								.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)){
							// govt supplers
							getManagedBean().setGovtSellers(scQuote.getScQuoteSuppliers());

						} else { // supplier list
							getManagedBean().setSupplierList(scQuote.getScQuoteSuppliers());
						}
					}
				}

				// status
				if(scQuote.getCurrentStatus() != null){
					getCurrentStatus(scQuote.getCurrentStatus().getPmStatus());
				}
				// remarks
				scQuote.setRemarks(initializeRemarks(
						scQuote,
						scQuote.getCurrentStatus(),
						scQuote.getRemarks(),
						ScSQRemark.class));

				// procurement type drop down.
				if(scQuote.getProcurementType() != null){
					if(scQuote.getProcurementType().getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)){
						getManagedBean().setProcurementTypes(getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE , Boolean.TRUE));
					} else {
						getManagedBean().setProcurementTypes(getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE , Boolean.FALSE));
					} 
				}
			}
			// duration calc
			calculateDuration(scQuote);
			// country id
			CountryReqDTO countryReqDTO = new CountryReqDTO();
			countryReqDTO.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICountrySO.class);
			CountryResDTO resDTO = reqSO.retrieveCountryByCode(countryReqDTO);
			getManagedBean().setDefaultCountryId(resDTO.getPmCountry().getCountryId());

			// title checker
			getManagedBean().setTitleChecker(Boolean.FALSE);

			// procurement type event
			procurementTypeEvent();

			// current page url for page navigation from sq to request note
			getManagedBean().setNewableFlag(Boolean.FALSE);
			getManagedBean().setEditableFlag(Boolean.FALSE);
			getManagedBean().setPageNavToRequestNote(fc.getViewRoot().getViewId());
			
			// page navigation to simple quote
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, SIMPLE_QUOTE_PAGE_NAV);

		} finally {

		}
	}
	
		
	private boolean isPanelItemAddable(CtPanelItem panelItem) {
		boolean addable = false;
		ScQuote quote = getManagedBean().getScQuote();
		if(quote != null) {
			CtPanelItem existPanelItem = getTopPanelItem();
			if(existPanelItem != null) {
				if(panelItem.getItemCategoryId()
									.getParameterCode()
									.equals(ParameterConst.SQ_PANEL_ITEM_GENERAL)) {
					addable = false;
				} else {
					CtPanelItemGroup existGroup = existPanelItem.getCtPanelItemGroup();
					CtPanelItemGroup newGroup = panelItem.getCtPanelItemGroup();
					if(existGroup != null && newGroup != null && 
							existGroup.getPanelItemGroupId() == newGroup.getPanelItemGroupId()) {
						//check both item zonal
						PmParameter existCoverage = existPanelItem.getPanelCoverageId();
						PmParameter newCoverage = panelItem.getPanelCoverageId();
						if(existCoverage != null && newCoverage != null && 
								existCoverage.getParameterCode().equals(newCoverage.getParameterCode())) {
							addable = true;
						} else {
							throw new EpException(MessageConst.ERROR_760362);
						}
					} 
				}
			} else {
				addable = true;
			}
		} else {
			addable = true;
		}
		return addable;
	} 

	/**
	 * add panel or contract and non contract to simple quote from search
	 * catalogue.
	 * 
	 * @param docId
	 *            the doc id
	 * @param panelItemFlag
	 *            the panel item flag
	 * @param compareSupplierItems
	 *            the compare supplier items
	 * @return the string page navigate to simple quote page.
	 */
	public String simpleQuoteAction(Long docId, boolean panelItemFlag,
			List<CmSupplierItem> compareSupplierItems) {
		debugLogger("simpleQuoteAction");
		boolean isGovtSeller = Boolean.FALSE;
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.CATALOGUE_MANAGEMENT,
						ICatalogueSO.class);
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		ScQuote scQuote = getManagedBean().getScQuote();
		boolean isPanelItemAdded = Boolean.FALSE;
		
		if (scQuote == null) {
			scQuote = new ScQuote();
			
		} else {
			
			if (docId != null && docId > 0) {
				boolean isPanel = scQuote.isPanel();
				if (isPanel) {
					isPanelItemAdded = Boolean.TRUE;
					CtPanelItem panelItem = getTopPanelItem();
					if(panelItem == null) {
						panelItem = scQuote.getPanelItem();
					}
					if(panelItem == null) {
						isPanelItemAdded = false;
					}
					else if(panelItem != null && panelItem.getItemCategoryId()
							.getParameterCode()
							.equals(ParameterConst.SQ_PANEL_ITEM_GENERAL)) {
						// add only one general panel item(can't add multiple)
						//ERR-760160 = Simple quote must have only one Panel Item.
						throw new EpException(MessageConst.ERROR_760160);
					} 
					else if(panelItem != null && panelItem.getItemCategoryId()
							.getParameterCode()
							.equals(ParameterConst.SQ_PANEL_ITEM_SPECIFIC)) {
						// add multiple panel specific items
						isPanelItemAdded = Boolean.FALSE;
					} 
					
					if(!panelItemFlag){
						//ERR-760162 = Select panel items only.
						throw new EpException(MessageConst.ERROR_760162);
					}
					
				} else if (panelItemFlag) {
					//ERR-760161 = Select non contract items only.
					throw new EpException(MessageConst.ERROR_760161);
				} 
			}
		}
		
		getManagedBean().setScQuote(scQuote);
		
		if (docId != null && docId > 0) {
			// panel item
			if (panelItemFlag) {
				// check non contract item list
				scQuote.setPanel(Boolean.TRUE);
				boolean isGeneralOrSpecific = Boolean.FALSE;
				
				// set procurement type as normal for panel
				if(null == scQuote.getProcurementType()){
					PmParameter pmParameter = new PmParameter();
					pmParameter.setParameterCode(ParameterConst.PROC_TYPE_NORMAL);
					scQuote.setProcurementType(pmParameter);
				}
				
				QuoteReqDTO reqDTO = new QuoteReqDTO();
				reqDTO.setPanelItemId(docId);
				QuoteResDTO resDTO = getQuoteSO().getPanelSupplierByPanelItem(
						reqDTO);
				CtPanelItem selectedPanelItem = resDTO.getCtPanelItem();
				
				if (selectedPanelItem != null && selectedPanelItem.getItemCategoryId() != null) {
					if (!isPanelItemAddable(selectedPanelItem)) {
						throw new EpException(MessageConst.ERROR_760160);
					}
					
					if (selectedPanelItem.getItemCategoryId()
							.getParameterCode()
							.equals(ParameterConst.SQ_PANEL_ITEM_GENERAL)) {
						// panel item general
						isGeneralOrSpecific = Boolean.FALSE;
						
						// add ct panel item to sc quote when panel item is general only.
						scQuote.setPanelItem(selectedPanelItem);
						
					} else if (selectedPanelItem.getItemCategoryId()
							.getParameterCode()
							.equals(ParameterConst.SQ_PANEL_ITEM_SPECIFIC)) {
						// panel item specific
						isGeneralOrSpecific = Boolean.TRUE;
					}

					boolean alreadyExists = checkItemExists(null, selectedPanelItem);
					if (alreadyExists) {
						String itemName = "";
						ScRequestItem requestItem = selectedPanelItem.getScRequestItem();
						if(requestItem != null) {
							if (requestItem.getCmItem() != null) {
								itemName = requestItem.getCmItem().getItemName();
							} 
							
							if (selectedPanelItem.getItemCategoryId().getParameterCode()
									.equals(ParameterConst.SQ_PANEL_ITEM_SPECIFIC)) {
								itemName = selectedPanelItem.getItemName();
							}
						}
						throw new EpException(MessageConst.ERROR_700027, new String[] {itemName});
						
					} else {
						prepareQuestions(selectedPanelItem, null, isGeneralOrSpecific);
					}
					
					if(!isPanelItemAdded) {
						
						if (!isGeneralOrSpecific) {
							// panel item general
							scQuote.setTitle(selectedPanelItem
									.getScRequestItem().getItemName());
							getManagedBean().setPanelItemFlag(Boolean.TRUE);
							
						} else if (selectedPanelItem.getItemCategoryId()
								.getParameterCode()
								.equals(ParameterConst.SQ_PANEL_ITEM_SPECIFIC)) {
							// panel item specific
							getManagedBean().setPanelItemFlag(Boolean.FALSE);
							getManagedBean().setNewableFlag(Boolean.FALSE);
						}

						// for panel zone
						if (selectedPanelItem.getPanelCoverageId()
								.getParameterCode()
								.equals(ParameterConst.SQ_PANEL_ITEM_ZONE)) {
							preparePanelZones(selectedPanelItem, scQuote);
							zoneEvent();

						} else {
							// copy suppliers from compare screen to the panel
							// item supplier list
							if (selectedPanelItem.getCtPanelSuppliers() != null
									&& selectedPanelItem.getCtPanelSuppliers().size() > 0) {
								panelItemListener(selectedPanelItem.getCtPanelSuppliers(), scQuote);
							}
						}
					}
					
					// adding selected item to item List
					//scQuote.setPanelItem(selectedPanelItem);
					getManagedBean().getPanelItems().add(selectedPanelItem);
				}
				
			} else {
				// non contract item and non panel item
				PmOrgProfile pmOrgProfile = null;
				CatalogueReqDTO catalogueReqDTO = new CatalogueReqDTO();
				catalogueReqDTO.setSupplierItemId(docId);
				CatalogueResDTO catalogueResDTO = catalogueSO
						.retrieveSupplierItemDtlsById(catalogueReqDTO);
				CmSupplierItem selectedSupplierItem = catalogueResDTO
						.getSupplierItem();

				if (selectedSupplierItem != null) {
					// For checking govt.seller
					if (selectedSupplierItem.getSmSupplier() != null) {
						OrgProfileReqDTO profileReqDTO = new OrgProfileReqDTO();
						profileReqDTO.setOrgProfileId(selectedSupplierItem
								.getSmSupplier().getSupplierId());
						OrgProfileResDTO orgProfileResDTO = orgProfileSO
								.retrieveOrgProfileByProfileId(profileReqDTO);
						 pmOrgProfile = orgProfileResDTO.getPmOrgProfile();
						 isGovtSeller = checkGovtSeller(pmOrgProfile, scQuote);
					}
					
					// govt seller
					if(isGovtSeller){
						List<PmOrgProfile> orgProfiles = new ArrayList<PmOrgProfile>();
						orgProfiles.add(pmOrgProfile);
						govtSellerListener(orgProfiles);
						
					} else{
						// copy all selected suppliers from the catalogue search
				        /*	if (compareSupplierItems == null) {
							catalogueReqDTO = new CatalogueReqDTO();
							catalogueReqDTO
							.setViewSearchBean(new CatalogueBackingBean()
							.getManagedBean().getSearchDataModel()
							.getSearchBean());
							catalogueReqDTO.setSupplierItemId(selectedSupplierItem
									.getSupplierItemId());
							catalogueReqDTO.setExtensionCode(selectedSupplierItem
									.getCmItem().getExtensionCode());
							catalogueReqDTO.setUomId(selectedSupplierItem
									.getPmUom().getUomId());
							catalogueResDTO = catalogueSO
									.getSuppliersItemsByExtensionCode(catalogueReqDTO);

							List<SmSupplier> selectedSuppliers = getManagedBean()
									.getCompareSuppliers();
							for (CmSupplierItem supplierItem : catalogueResDTO
									.getSupplierItemList()) {
								if (supplierItem.getSmSupplier() != null) {
									selectedSuppliers.add(supplierItem
											.getSmSupplier());
								}
							}
							selectedSuppliers.add(selectedSupplierItem
									.getSmSupplier());
							
							supplierListListener(selectedSuppliers, scQuote);

						} else {
							// copy suppliers from the compare screen
							supplierListListener(
									addSelectedSuppliers(compareSupplierItems,
											selectedSupplierItem), scQuote);
						}*/
					}
					
					List<SmSupplier> selectedSuppliers = new ArrayList<SmSupplier>();
					selectedSuppliers.add(selectedSupplierItem
							.getSmSupplier());
					
					supplierListListener(selectedSuppliers, scQuote);
					
					List<CmSupplierItem> supplierItems = getManagedBean()
							.getSupplierItems();
					if (supplierItems == null) {
						supplierItems = new ArrayList<CmSupplierItem>();
					}

					boolean alreadyExists = checkItemExists(selectedSupplierItem, null);
					if (alreadyExists) {
						throw new EpException(MessageConst.ERROR_700027,
								new String[] {selectedSupplierItem.getCmItem().getItemName()});
						
					} else {
						supplierItems.add(selectedSupplierItem);
						prepareQuestions(null, selectedSupplierItem, Boolean.TRUE);
					}
					
					getManagedBean().setSupplierItems(supplierItems);
				}
			}
		} else {
			// initializing product and service item list
			if (getManagedBean().getProductTemplateSpecBean().getSpecMaster() == null
					&& getManagedBean().getServiceTemplateSpecBean()
							.getSpecMaster() == null) {
				prepareQuestions(null, null, Boolean.FALSE);
			}
			
			// for procurement type drop down
			 checkGovtSeller(null, scQuote);
		}
		
		// for procurement type drop down
		if(null == getManagedBean().getProcurementTypes()){
			getManagedBean().setProcurementTypes(
					getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE));
		}
		
		// proc type category
		if (null == scQuote.getProcurementTypeCategory()) {
			scQuote.setProcurementTypeCategory(new PmParameter());
		}
		
		if(null == scQuote.getMultipleQuote()){
			scQuote.setMultipleQuote(Boolean.FALSE);	
		}
		
		// Initializing Remarks
		scQuote.setRemarks(initializeRemarks(scQuote, null,
				scQuote.getRemarks(), ScSQRemark.class));

		// WorkFlow Status should be set In the save
		updateWorkFlowAndRemarks(scQuote);
		
		scQuote.setCreatedBy(new FacesScopes().getUserSessionBean().getUserId());
		getManagedBean().setLocLevelDropdown(getLocationLevelDropdown());
		
		// zone location level by default - district
		if (null == getManagedBean().getLocationLevel()) {
			getManagedBean().setLocationLevel(
					ParameterConst.LOCATION_LEVEL_DISTRICT);
		}
		// supplier location level by default - district
		if (null == getManagedBean().getSupplierLocLevel()) {
			getManagedBean().setSupplierLocLevel(
					ParameterConst.LOCATION_LEVEL_DISTRICT);
		}
		
		if (null == scQuote.getStartDate()) {
			Date startDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.clear(); 
			cal.setTime(startDate);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			startDate = cal.getTime();
			scQuote.setStartDate(startDate);
		}
		
		// to get ptj
		PmUser user = new FacesScopes().getUserSessionBean();
		if (user != null) {
			OrgProfileReqDTO profileReqDTO = new OrgProfileReqDTO();
			profileReqDTO.setUserId(user.getUserId());
			profileReqDTO.setOrgType(OrganizationType.DIVISION.value());
			OrgProfileResDTO orgProfileResDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(profileReqDTO);
			if (orgProfileResDTO.getOrgProfileList() != null
					&& orgProfileResDTO.getOrgProfileList().size() > 0) {
				getManagedBean().getRequestNote().setPmOrgProfile(
						orgProfileResDTO.getOrgProfileList().get(0));
			}
		}
		
		// to get country id
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);
		getManagedBean().setDefaultCountryId(
				resDTO.getPmCountry().getCountryId());

		// to get ptj locality by default
		ptjLocality(ParameterConst.LOCATION_LEVEL_DISTRICT);
		getManagedBean().setEditableFlag(Boolean.TRUE);
		CatalogueBackingBean catalogueBackingBean = new CatalogueBackingBean();
		catalogueBackingBean.getManagedBean().setSimpleQuoteFlag(Boolean.FALSE);

		return SIMPLE_QUOTE_PAGE_NAV;
	}

	/**
	 * Get 
	 * 
	 * @param cmSupplierItem
	 *            the cm supplier item
	 * @return true, if successful
	 */
	private CtPanelItem getTopPanelItem() {
		TemplateSpecBean productTplSpecBean = getManagedBean()
				.getProductTemplateSpecBean();
		TemplateSpecBean serviceTplSpecBean = getManagedBean()
				.getServiceTemplateSpecBean();

		CtPanelItem panelItem = null;
		// product
		if (productTplSpecBean.getSpecMaster() != null) {
			TplSpecMaster prodSpecMaster = productTplSpecBean
					.getSpecMaster();

			if (prodSpecMaster.getTplSpecQuestions() != null
					&& prodSpecMaster.getTplSpecQuestions().size() > 0) {
				for (TplSpecQuestion question : prodSpecMaster
						.getTplSpecQuestions()) {
					if(question.getCtPanelItem() != null) {
						panelItem = question.getCtPanelItem();
						break;
					}
				}
			}
		} 
		if (serviceTplSpecBean.getSpecMaster() != null) {
			TplSpecMaster serviceSpecMaster = serviceTplSpecBean
					.getSpecMaster();
			if (serviceSpecMaster.getTplSpecQuestions() != null
					&& serviceSpecMaster.getTplSpecQuestions().size() > 0) {
				for (TplSpecQuestion question : serviceSpecMaster
						.getTplSpecQuestions()) {
					if (question.getCtPanelItem() != null) {
						panelItem = question.getCtPanelItem();
						break;
					}
				}
			}
		}
		return panelItem;
	}

	/**
	 * Check item exists.
	 * 
	 * @param cmSupplierItem
	 *            the cm supplier item
	 * @return true, if successful
	 */
	private boolean checkItemExists(CmSupplierItem cmSupplierItem, CtPanelItem ctPanelItem) {
		TemplateSpecBean productTplSpecBean = getManagedBean()
				.getProductTemplateSpecBean();
		TemplateSpecBean serviceTplSpecBean = getManagedBean()
				.getServiceTemplateSpecBean();
		boolean exists = false;

		if (cmSupplierItem != null || ctPanelItem != null) {
			// product
			if (productTplSpecBean.getSpecMaster() != null) {
				TplSpecMaster prodSpecMaster = productTplSpecBean
						.getSpecMaster();

				if (prodSpecMaster.getTplSpecQuestions() != null
						&& prodSpecMaster.getTplSpecQuestions().size() > 0) {
					for (TplSpecQuestion question : prodSpecMaster
							.getTplSpecQuestions()) {
						if(cmSupplierItem != null) {
							if (cmSupplierItem.getCmItem() != null
									&& question.getCmItem() != null) {
								if (cmSupplierItem.getCmItem().getItemId()
										.equals(question.getCmItem().getItemId())) {
									exists = true;
									break;
								}
							}
						} else if(ctPanelItem != null){
							if (ctPanelItem.getPanelItemId()
										.equals(question.getCtPanelItem().getPanelItemId())) {
									exists = true;
									break;
							}
						}
						
					}
				}
				// service
				if (serviceTplSpecBean.getSpecMaster() != null) {
					TplSpecMaster serviceSpecMaster = serviceTplSpecBean
							.getSpecMaster();
					if (serviceSpecMaster.getTplSpecQuestions() != null
							&& serviceSpecMaster.getTplSpecQuestions().size() > 0) {
						for (TplSpecQuestion question : serviceSpecMaster
								.getTplSpecQuestions()) {
							if(cmSupplierItem != null) {
								if (cmSupplierItem.getCmItem() != null && 
										question.getCmItem() != null) {
									if (cmSupplierItem
											.getCmItem()
											.getItemId()
											.equals(question.getCmItem()
													.getItemId())) {
										exists = true;
										break;
									}
								}
							} else if(ctPanelItem != null) {
								if (ctPanelItem.getPanelItemId()
										.equals(question.getCtPanelItem().getPanelItemId())) {
									exists = true;
									break;
								}
							}
						}
					}
				}
			}
		}
		return exists;
	}

	/**
	 * Prepare panel zones.
	 * 
	 * @param ctPanelItem
	 *            the ct panel item
	 * @param scQuote
	 *            the sc quote
	 */
	public void preparePanelZones(CtPanelItem ctPanelItem, ScQuote scQuote) {
		// location level
		if (ctPanelItem.getLocationLevelId() != null) {
			if (ctPanelItem.getLocationLevelId().getParameterCode()
					.equals(ParameterConst.LOCATION_LEVEL_STATE)) {
				getManagedBean().setLocationLevel(
						ParameterConst.LOCATION_LEVEL_STATE);
			} else if (ctPanelItem.getLocationLevelId().getParameterCode()
					.equals(ParameterConst.LOCATION_LEVEL_DISTRICT)) {
				getManagedBean().setLocationLevel(
						ParameterConst.LOCATION_LEVEL_DISTRICT);
			} else {
				getManagedBean().setLocationLevel(
						ParameterConst.LOCATION_LEVEL_DIVISION);
			}
		}
		List<TplSpecZoneDtl> tplSpecZoneDtls = new ArrayList<TplSpecZoneDtl>();
		if (ctPanelItem.getCtPanelZones() != null
				&& ctPanelItem.getCtPanelZones().size() > 0) {
						
			ScQuoteZone quoteZone = scQuote.getQuoteZone();
			if(quoteZone == null) {
				quoteZone = new ScQuoteZone();
			}
			if(quoteZone.getTplSpecZoneDtls() != null) {
				tplSpecZoneDtls = quoteZone.getTplSpecZoneDtls(); 
			}

			for (CtPanelZone ctPanelZone : ctPanelItem.getCtPanelZones()) {
				TplSpecZoneDtl tplSpecZoneDtl = new TplSpecZoneDtl();
				tplSpecZoneDtl.setTplSpecZone(quoteZone);
				tplSpecZoneDtl.setZoneName(ctPanelZone.getName());
				tplSpecZoneDtl.setPanelZone(ctPanelZone);

				List<PmState> states = SourcingLocationHelper
						.getStateFromCtPanelZone(ctPanelZone.getPanelZoneDtls());
				List<PmDistrict> districts = SourcingLocationHelper
						.getDistrictFromCtPanelZone(ctPanelZone
								.getPanelZoneDtls());

				getManagedBean().getZoneTrees().add(
						LocationSingleton.getInstance()
								.buildTreeWithDisabledChilds(tplSpecZoneDtl,
										states, districts, null, null, false,
										TplSpecZoneLoc.class));

				tplSpecZoneDtls.add(tplSpecZoneDtl);

				if (ctPanelZone.getCtPanelSuppliers() != null
						&& ctPanelZone.getCtPanelSuppliers().size() > 0) {
					createPanelSuppliersWithZone(
							ctPanelZone.getCtPanelSuppliers(), scQuote,
							tplSpecZoneDtl);
				}
			}

			// for zonal
			scQuote.setZonal(Boolean.TRUE);
			quoteZone.setScQuote(scQuote);
			quoteZone.setTplSpecZoneDtls(tplSpecZoneDtls);
			scQuote.setQuoteZone(quoteZone);
			getManagedBean().setScQuote(scQuote);
		}
	}

	/**
	 * Creates the panel suppliers with zone.
	 * 
	 * @param ctPanelSuppliers
	 *            the ct panel suppliers
	 * @param quote
	 *            the quote
	 * @param tplSpecZoneDtl
	 *            the tpl spec zone dtl
	 */
	private void createPanelSuppliersWithZone(
			List<CtPanelSupplier> ctPanelSuppliers, ScQuote quote,
			TplSpecZoneDtl tplSpecZoneDtl) {
		debugLogger("createPanelSuppliersWithZone");
		List<ScQuoteSupplier> panelSuppliers = getManagedBean()
				.getPanelSuppliers();
		if (panelSuppliers == null) {
			panelSuppliers = new ArrayList<ScQuoteSupplier>();
		}
		if (ctPanelSuppliers != null) {
			for (CtPanelSupplier panelSupplier : ctPanelSuppliers) {
				SmSupplier smSupplier = panelSupplier.getSupplier();
				ScQuoteSupplier scQuoteSupplier = new ScQuoteSupplier();
				scQuoteSupplier.setSmSupplier(smSupplier);
				scQuoteSupplier.setQuote(quote);
				scQuoteSupplier.setTplSpecZoneDtl(tplSpecZoneDtl);
				
				String mofNo = getMofNor(smSupplier.getSupplierId());
				scQuoteSupplier.setSupplierName(smSupplier.getCompanyName());
				scQuoteSupplier.setMofNumber(mofNo);

				panelSuppliers.add(scQuoteSupplier);
				getManagedBean().setPanelSuppliers(panelSuppliers);

			}
		}
	}

	/**
	 * Adds the selected suppliers.
	 * 
	 * @param compareSupplierItems
	 *            the compare supplier items
	 * @param selectedSupplierItem
	 *            the selected supplier item
	 * @return the list
	 */
	/*
	 * private List<SmSupplier> addSelectedSuppliers( List<CmSupplierItem>
	 * compareSupplierItems, CmSupplierItem selectedSupplierItem) {
	 * List<SmSupplier> suppliers = getManagedBean().getCompareSuppliers(); for
	 * (CmSupplierItem supplierItem : compareSupplierItems) { if
	 * (supplierItem.getCmItem().getItemId()
	 * .equals(selectedSupplierItem.getCmItem().getItemId())) {
	 * suppliers.add(supplierItem.getSmSupplier()); } } return suppliers; }
	 */

	/**
	 * Prepare questions. prepare question based on item product or service.
	 * 
	 * @param ctPanelItem
	 *            the ct panel item
	 * @param cmSupplierItem
	 *            the cm supplier item
	 * @param isSpecificOrGenOrNonCtItem
	 *            the is specific or gen or non ct item
	 */
	private void prepareQuestions(CtPanelItem ctPanelItem,
			CmSupplierItem cmSupplierItem, boolean isSpecificOrGenOrNonCtItem) {
		int itemCode = 0;
		CmItem cmItem = null;
		PmUom uom = null;
		String ItemName = "";
		String itemSpec = "";
		// check flag for panel item or non contract item
		if (ctPanelItem != null) {
			if (ctPanelItem.getScRequestItem() != null) {
				ScRequestItem scRequestItem = ctPanelItem.getScRequestItem();
				cmItem = scRequestItem.getCmItem();
				if (scRequestItem.getCmItem() != null) {
					String extCode = cmItem.getExtensionCode();
					CharSequence charSeq = extCode.subSequence(0, 2);
					itemCode = Integer.valueOf(charSeq.toString());
					ItemName = cmItem.getItemName();

				} else {
					// panel item - product by default when the cmItem is null
					itemCode = 2;
				}
				if (ctPanelItem.getItemCategoryId().getParameterCode()
						.equals(ParameterConst.SQ_PANEL_ITEM_SPECIFIC)) {
					ItemName = ctPanelItem.getItemName();
					itemSpec = ctPanelItem.getSpecification();
				}
				uom = ctPanelItem.getPmUom();
			}
		} else if (cmSupplierItem != null) {
			String extCode = cmSupplierItem.getCmItem().getExtensionCode();
			CharSequence charSeq = extCode.subSequence(0, 2);
			itemCode = Integer.valueOf(charSeq.toString());
			cmItem = cmSupplierItem.getCmItem();
			ItemName = cmSupplierItem.getCmItem().getItemName();
			uom = cmSupplierItem.getPmUom();
		}

		// checking two digits in itemCode whether service or product
		ScQuote scQuote = getManagedBean().getScQuote();
		List<ScQuoteDoc>  quoteDocs = null;
		if(scQuote != null) {
			quoteDocs = scQuote.getQuoteDocs();
		}
		
		if(CollectionUtils.isEmpty(quoteDocs)) {
			QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
			QuoteResDTO quoteResDTO = getQuoteSO()
					.getQuoteDtlFirstTime(quoteReqDTO);
			quoteDocs = quoteResDTO.getQuoteDocs();
		} 

		// get Template Spec Backing Bean
		PortletSession portletSession = (PortletSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);
		TemplateSpecBackingBean templateSpecBackingBean = (TemplateSpecBackingBean) portletSession
				.getAttribute("templateSpecBackingBean");
		if (templateSpecBackingBean == null) {
			templateSpecBackingBean = new TemplateSpecBackingBean();
		}

		for (ScQuoteDoc quoteDoc : quoteDocs) {
			if (ParameterConst.SQ_ITEM_LIST_SERVICE.equalsIgnoreCase(quoteDoc
					.getSpecMaster().getItemTypeCode().getParameterCode())) {
				TemplateSpecBean serviceTemplateSpecBean = getManagedBean()
						.getServiceTemplateSpecBean();
				TplSpecMaster serviceTpl = null;

				if (serviceTemplateSpecBean.getSpecMaster() == null) {
					serviceTpl = quoteDoc.getSpecMaster();
				} else {
					serviceTpl = serviceTemplateSpecBean.getSpecMaster();
				}

				List<TplSpecQuestion> tplSpecQuestions = serviceTpl
						.getTplSpecQuestions();
				if (null == tplSpecQuestions) {
					tplSpecQuestions = new ArrayList<TplSpecQuestion>();
				}
				if (isSpecificOrGenOrNonCtItem) {
					if (itemCode >= 70 && itemCode <= 94) { // for service
															// between 70 and 94
						templateSpecBackingBean.addQuestion(serviceTemplateSpecBean, 
								serviceTpl,
								tplSpecQuestions, tplSpecQuestions.size() - 1,
								TemplateSpecConst.QUESTION_TYPE_ITEM);
						TplSpecQuestion specQuestion = tplSpecQuestions
								.get(tplSpecQuestions.size() - 1);
						specQuestion.setClUom(uom);
						specQuestion.setCmItem(cmItem);
						specQuestion.setCtPanelItem(ctPanelItem);
						specQuestion.setClItem(ItemName);
						specQuestion.setClSpecDetail(itemSpec);
					}
				} else{
					serviceTpl.setCtPanelItem(ctPanelItem);
				}
				serviceTpl.setTplSpecQuestions(tplSpecQuestions);
				serviceTemplateSpecBean.setSpecMaster(serviceTpl);
				getManagedBean().setServiceTemplateSpecBean(
						serviceTemplateSpecBean);

			} else if (ParameterConst.SQ_ITEM_LIST_PRODUCT
					.equalsIgnoreCase(quoteDoc.getSpecMaster()
							.getItemTypeCode().getParameterCode())) {
				TemplateSpecBean productTemplateSpecBean = getManagedBean()
						.getProductTemplateSpecBean();
				TplSpecMaster prodTpl = null;

				if (productTemplateSpecBean.getSpecMaster() == null) {
					prodTpl = quoteDoc.getSpecMaster();
				} else {
					prodTpl = productTemplateSpecBean.getSpecMaster();
				}

				List<TplSpecQuestion> productQuestions = prodTpl
						.getTplSpecQuestions();
				if (null == productQuestions) {
					productQuestions = new ArrayList<TplSpecQuestion>();
				}

				if (isSpecificOrGenOrNonCtItem) {
					if (itemCode > 0) {
						if (itemCode < 70 || itemCode > 94) { // for product <
																// 70 and > 94

							templateSpecBackingBean.addQuestion(productTemplateSpecBean,
									prodTpl,
									productQuestions,
									productQuestions.size() - 1,
									TemplateSpecConst.QUESTION_TYPE_ITEM);
							TplSpecQuestion prodQuestion = productQuestions
									.get(productQuestions.size() - 1);
							prodQuestion.setClUom(uom);
							prodQuestion.setCmItem(cmItem);
							prodQuestion.setCtPanelItem(ctPanelItem);
							prodQuestion.setClItem(ItemName);
							prodQuestion.setClSpecDetail(itemSpec);
						}
					}
				} else {
					prodTpl.setCtPanelItem(ctPanelItem);
				}
				prodTpl.setTplSpecQuestions(productQuestions);
				prodTpl.setBrandNew(Boolean.FALSE);
				productTemplateSpecBean.setSpecMaster(prodTpl);
				getManagedBean().setProductTemplateSpecBean(
						productTemplateSpecBean);
			}
		}
	}

	/**
	 * if govt. seller , enable G2G radio button only. if not govt. seller enable Normal
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @param g2gSeller
	 *            the g2g seller
	 * @return array of SelectItem
	 */
	private boolean checkGovtSeller(PmOrgProfile pmOrgProfile, ScQuote scQuote) {
		boolean isGovtSeller = Boolean.FALSE;
		PmParameter pmParameter = new PmParameter();
		if (pmOrgProfile != null && pmOrgProfile.isGovtSeller()) {
			isGovtSeller = Boolean.TRUE;
		}
		
		CatalogueBackingBean catalogueBackingBean = new CatalogueBackingBean();
		 boolean isG2G = catalogueBackingBean.getManagedBean().isG2gFlag();
		
		// check supplier is government seller or not
		if(null == scQuote.getProcurementType()){
			if(isGovtSeller || isG2G){
				pmParameter.setParameterCode(ParameterConst.PROC_TYPE_G2G);
				scQuote.setProcurementType(pmParameter);
			} else{
				pmParameter.setParameterCode(ParameterConst.PROC_TYPE_NORMAL);
				scQuote.setProcurementType(pmParameter);
			}

		} else{
			if(pmOrgProfile != null){
				if(isGovtSeller){
					if(!scQuote.getProcurementType().getParameterCode().equals(ParameterConst.PROC_TYPE_G2G)){
						throw new EpException("Already added Normal supplier,  can't add G2G Supplier");
					} 

				} else{
					if(scQuote.getProcurementType().getParameterCode().equals(ParameterConst.PROC_TYPE_G2G)){
						throw new EpException("Already added G2G supplier , can't add normal Supplier");
					}
				}
			}
		}
		return isGovtSeller;
	}

	/**
	 * save Simple Quote save - save data into database and initiate task for
	 * Simple Quote.
	 * 
	 * @param outcome
	 *            the outcome
	 * @return the string
	 */
	public String saveSimpleQuote(String outcome) {
		debugLogger("saveSimpleQuote");
		ScRequestNote requestNote = null;
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		String[] outcomes = getManagedBean().getOutComes();
		String taskId = getManagedBean().getTaskId();
		Long defaultCountryId = getManagedBean().getDefaultCountryId();
		boolean autoUpdate = getManagedBean().isAutoUpdate();
		Date pidExpDate = getManagedBean().getPidExpiryDate();
		try {
			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION,
							IDirectPurchaseSO.class);
			QuoteReqDTO reqDTO = new QuoteReqDTO();
			ScQuote quote = getManagedBean().getScQuote();

			// validate start date and end date
			validateStartDateAndEndDate(quote);
			if (null == quote.getStartDate() ||  null == quote.getEndDate()) {
				throw new EpException(MessageConst.ERROR_760388);
			}
			
			if (quote.getRequestItems() == null) {
				List<ScRequestItem> requestItems = new ArrayList<ScRequestItem>();
				ScRequestItem requestItem = new ScRequestItem();
				requestItem.setQuote(quote);
				requestItems.add(requestItem);
				quote.setRequestItems(requestItems);
			}

			if (quote.getRequestNotes() == null
					|| quote.getRequestNotes().isEmpty()) {
				List<ScRequestNote> requestNotes = new ArrayList<ScRequestNote>();
				requestNote = getManagedBean().getRequestNote();
				setUserDetails(requestNote);
				
				if(StringUtils.isBlank(requestNote.getRequestNoteNo())){
					requestNote.setRequestNoteNo(directPurchaseSO
							.generateRequestNoteNo());
				}
				requestNote.setTitle(quote.getTitle());
				requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SQ);
				requestNote.setWorkFlowStatuses(null);
				requestNote.setQuote(quote);
				requestNotes.add(requestNote);
				quote.setRequestNotes(requestNotes);
			}

			quote.setProcurementType(getParameterByTypeAndCode(
					ParameterTypeConst.PROCUREMENT_TYPE, quote
							.getProcurementType().getParameterCode()));

			// location level
			if (quote.isZonal()) {
				quote.setZoneLocatonLevel(getParameterByTypeAndCode(
						ParameterTypeConst.LOCATION_LEVEL, getManagedBean()
								.getLocationLevel()));
			} else {
				quote.setZoneLocatonLevel(null);
			}
			// supplier location level by default - district
			if (getManagedBean().isByLocality()) {
				quote.setSupplierLocationLevel(getParameterByTypeAndCode(
						ParameterTypeConst.LOCATION_LEVEL, getManagedBean()
								.getSupplierLocLevel()));
			} else {
				quote.setSupplierLocationLevel(null);
			}

			// justification type
			if (quote.getProcurementType().getParameterCode()
					.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
				if (quote.getJustificationType().getReasonCode() != null) {
					quote.setJustificationType(getReasonByCategoryAndCode(
							ReasonCategory.JUSTIFICATION_AD_HOC.value(), quote
							.getJustificationType().getReasonCode()));

				}
			}  else {
				quote.setJustificationType(null);
			}

			// procurement type category
			if (null != quote.getProcurementTypeCategory()) {
				ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
				parameterReqDTO.setParameterId(quote
						.getProcurementTypeCategory().getParameterId());
				PmParameter procTypeCategory = getParameterSO()
						.retrieveParameterById(parameterReqDTO)
						.getPmParameter();
				quote.setProcurementTypeCategory(procTypeCategory);
			}

			// by business nature
			if (getManagedBean().getBusinessNatureCriterias() != null
					&& getManagedBean().getBusinessNatureCriterias().size() > 0) {
				quote.setBusinessNatureCriterias(getManagedBean()
						.getBusinessNatureCriterias());
			}

			// zone details
			quote.setQuoteZone(getQuoteZones(getManagedBean().getZoneTrees()));
			if (quote.getQuoteZone() != null
					&& quote.getQuoteZone().isBrandNew()) {
				quote.getQuoteZone().setDocId(quote.getQuoteId());
				quote.getQuoteZone().setDocType(
						ParameterConst.DOCTYPE_SIMPLE_QUOTE);
			}

			List<ScQuoteSupplier> suppliers = new ArrayList<ScQuoteSupplier>();
			// panel item zone
			if(quote.isPanel()){
				if (quote.getPanelItem() != null) {
					validatePanelItem(quote.getPanelItem());
				}
				// panel suppliers
				if (getManagedBean().getPanelSuppliers() != null) {
					for (ScQuoteSupplier scQuoteSupplier : getManagedBean()
							.getPanelSuppliers()) {
						if (quote.getScQuoteSuppliers() != null
								&& !quote.getScQuoteSuppliers().isEmpty()) {
							if (!quote.getScQuoteSuppliers().contains(
									scQuoteSupplier)) {
								suppliers.add(scQuoteSupplier);
							}
						} else {
							suppliers.add(scQuoteSupplier);
						}
					}
				}

			} else {
				if (quote.getProcurementType() != null) {

					if (ParameterConst.PROC_TYPE_G2G.equals(quote
							.getProcurementType().getParameterCode())) {
						// govt sellers
						if (getManagedBean().getGovtSellers() != null) {
							suppliers.addAll(getManagedBean().getGovtSellers());
						}
					} else {
						// by supplier
						if (getManagedBean().getSupplierList() != null) {
							suppliers
									.addAll(getManagedBean().getSupplierList());
						}
					}
				}

				// by location
				if (!getManagedBean().getLocationTrees().isEmpty()) {
					quote.setLocationStateCriterias(getLocationStateCriterias(
							getManagedBean().getLocationTrees(), quote));
					quote.setLocationDivisionCriterias(getLocationDivisionCriterias(
							getManagedBean().getLocationTrees(), quote));
					quote.setLocationDistrictCriterias(getLocationDistrictCriterias(
							getManagedBean().getLocationTrees(), quote));
				} else {
					if (quote.getLocationStateCriterias() != null) {
						quote.getLocationStateCriterias().clear();
					}
					if (quote.getLocationDivisionCriterias() != null) {
						quote.getLocationDivisionCriterias().clear();
					}
					if (quote.getLocationDistrictCriterias() != null) {
						quote.getLocationDistrictCriterias().clear();
					}
				}
			}

			if (suppliers != null && !suppliers.isEmpty()) {
				quote.setScQuoteSuppliers(suppliers);
			}
			
			//update workflow status and remarks
			updateWorkFlowAndRemarks(quote);
			
			finalizeRemarks(quote.getRemarks());
			// product and service
			List<ScQuoteDoc> quoteDocs = new ArrayList<ScQuoteDoc>();
			if (quote.getQuoteDocs() != null && quote.getQuoteDocs().size() > 0) {
				for (ScQuoteDoc doc : quote.getQuoteDocs()) {

					if (doc.getSpecMaster().getItemTypeCode()
							.getParameterCode()
							.equals(ParameterConst.ITEM_TYPE_PRODUCT)) {
						doc.setSpecMaster(getManagedBean()
								.getProductTemplateSpecBean().getSpecMaster());
						quoteDocs.add(doc);
					} else if (doc.getSpecMaster().getItemTypeCode()
							.getParameterCode()
							.equals(ParameterConst.ITEM_TYPE_SERVICE)) {
						doc.setSpecMaster(getManagedBean()
								.getServiceTemplateSpecBean().getSpecMaster());
						quoteDocs.add(doc);
					}
				}
			} else {
				// tpl spec - product
				if (getManagedBean().getProductTemplateSpecBean()
						.getSpecMaster() != null) {
					if (getManagedBean().getProductTemplateSpecBean()
							.getSpecMaster().getTplSpecQuestions() != null
							&& getManagedBean().getProductTemplateSpecBean()
									.getSpecMaster().getTplSpecQuestions()
									.size() > 0) {
						ScQuoteDoc quoteDoc = new ScQuoteDoc();
						TplSpecMaster specMaster = getManagedBean()
								.getProductTemplateSpecBean().getSpecMaster();
						if (quote.getQuoteZone() != null) {
							specMaster.setTplSpecZone(quote.getQuoteZone());
						}
						quoteDoc.setSpecMaster(specMaster);
						quoteDoc.setQuote(quote);
						quoteDocs.add(quoteDoc);
						quote.setQuoteDocs(quoteDocs);
					}
				}
				// tpl spec - service
				if (getManagedBean().getServiceTemplateSpecBean()
						.getSpecMaster() != null) {
					if (getManagedBean().getServiceTemplateSpecBean()
							.getSpecMaster().getTplSpecQuestions() != null
							&& getManagedBean().getServiceTemplateSpecBean()
									.getSpecMaster().getTplSpecQuestions()
									.size() > 0) {
						ScQuoteDoc quoteDoc = new ScQuoteDoc();
						TplSpecMaster specMaster = getManagedBean()
								.getServiceTemplateSpecBean().getSpecMaster();
						if (quote.getQuoteZone() != null) {
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
			reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setSubmit(Boolean.FALSE);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setPmUser(facesScopes.getUserSessionBean());
			reqDTO.setOrgProfileId(getManagedBean().getRequestNote()
					.getPmOrgProfile().getOrgProfileId());
			reqDTO.setQuoteBidDtl(quote);
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setTaskExpiredDate(getManagedBean().getTaskExpiryDate());
			reqDTO.setPidApprovers(getManagedBean().getPidApprovers());

			// ReQuote
			reqDTO.setEndLoaOutcome(getManagedBean().getEndLoaOutcome());
			reqDTO.setEndLoaTaskId(getManagedBean().getEndLoaTaskId());
			reqDTO.setEndLoaToken(getManagedBean().getEndLoaToken());
			reqDTO.setEndLoaRN(getManagedBean().getEndLoaRN());

			QuoteResDTO quoteResDTO = null;
			if (outcome.isEmpty()) {
				quoteResDTO = getQuoteSO().saveQuoteDtl(reqDTO);
			} else if (BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(outcome)) {
				quoteResDTO = getQuoteSO().updateSimpleQuote(reqDTO);
				renewSesionObject();
			}
			if (quoteResDTO.isSuccessful()) {
				ScQuote scQuote = quoteResDTO.getQuoteBidDtl();

				if (BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(outcome)) {
					reqDTO.setQuoteId(scQuote.getQuoteId());
					reqDTO.setFetchSubEntity(true);
					scQuote = getQuoteSO().getQuoteDtl(reqDTO).getQuoteBidDtl();
					// quoteResDTO = getQuoteSO().getQuoteDtl((reqDTO));
					getManagedBean().setOutComes(outcomes);
					getManagedBean().setTaskId(taskId);
					getManagedBean().setDefaultCountryId(defaultCountryId);
					getManagedBean().setAutoUpdate(autoUpdate);
					getManagedBean().setPidExpiryDate(pidExpDate);
					reloadSimpleQuote(scQuote);
					successMessage(MessageConst.INFO_700025, ParameterTypeConst.DOCUMENT_TYPES, 
							ParameterConst.DOCTYPE_SIMPLE_QUOTE,scQuote.getQuoteNo(), Boolean.FALSE);

				} else {
					successMessage(MessageConst.INFO_700024, ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_SIMPLE_QUOTE, scQuote.getQuoteNo(), Boolean.TRUE);
					// clear SQ data
					super.renewSesionObject();
				}

			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
		} finally {
			// quoteSO = null;
		}
		return null;
	}

	/**
	 * Validate panel item.
	 * 
	 * @param ctPanelItem
	 *            the ct panel item
	 */
	private void validatePanelItem(CtPanelItem ctPanelItem) {
		FacesScopes facesScopes = new FacesScopes();
		TemplateSpecBean productTplSpecBean = getManagedBean()
				.getProductTemplateSpecBean();
		TemplateSpecBean serviceTplSpecBean = getManagedBean()
				.getServiceTemplateSpecBean();
		int checkQty = 0;
		if (productTplSpecBean.getSpecMaster() != null && 
				productTplSpecBean.getSpecMaster().getTplSpecQuestions() != null
				&& productTplSpecBean.getSpecMaster().getTplSpecQuestions()
						.size() > 0) {

			for (TplSpecQuestion tplSpecQuestion : productTplSpecBean
					.getSpecMaster().getTplSpecQuestions()) {
				if (tplSpecQuestion.getClQuantity() != null) {
					checkQty = Double.compare(tplSpecQuestion.getClQuantity()
							.doubleValue(), ctPanelItem.getMinimumOrderQty());
				}

				ScQuote quote = getManagedBean().getScQuote();
				if (quote != null) {
					if (quote.getPurchaseInDefault() == null && checkQty < 0) {
						throw new EpException(
								MessageConst.ERROR_100057,
								new String[] {
										ResourceBundleUtils
												.getMessage(
														"lbl.sc.quantity",
														ResourceBundleUtils.BUNDLE_COMMON_LABELS,
														facesScopes
																.getUserLocale()),
										String.valueOf((int) ctPanelItem
												.getMinimumOrderQty()) });
					}
				}
			}
		}

		if (serviceTplSpecBean.getSpecMaster() != null
				&& serviceTplSpecBean.getSpecMaster().getTplSpecQuestions() != null
				&& serviceTplSpecBean.getSpecMaster().getTplSpecQuestions()
						.size() > 0) {

			for (TplSpecQuestion tplSpecQuestion : productTplSpecBean
					.getSpecMaster().getTplSpecQuestions()) {

				if (tplSpecQuestion.getClQuantity() != null) {
					checkQty = Double.compare(tplSpecQuestion.getClQuantity()
							.doubleValue(), ctPanelItem.getMinimumOrderQty());
				}

				if (checkQty < 0) {
					throw new EpException(
							MessageConst.ERROR_100057,
							new String[] {
									ResourceBundleUtils
											.getMessage(
													"lbl.sc.quantity",
													ResourceBundleUtils.BUNDLE_COMMON_LABELS,
													facesScopes.getUserLocale()),
									String.valueOf((int) ctPanelItem
											.getMinimumOrderQty()) });
				}
			}
		}
	}

	/**
	 * Reload simple quote.
	 * 
	 * @param scQuote
	 *            the sc quote
	 */
	public void reloadSimpleQuote(ScQuote scQuote) {
		TemplateSpecBean productTplSpecBean = getManagedBean()
				.getProductTemplateSpecBean();
		TemplateSpecBean serviceTplSpecBean = getManagedBean()
				.getServiceTemplateSpecBean();
		
		getManagedBean().setAutoUpdate(Boolean.TRUE);

		getManagedBean().setTitleChecker(Boolean.TRUE);
		getManagedBean().setTabActiveIndex(0);
		getManagedBean().setPanelItemFlag(Boolean.TRUE);
		getManagedBean().setEditableFlag(Boolean.TRUE);
		getManagedBean().setActiveIndex(0);

		if (scQuote.getCurrentStatus() == null) {
			List<ScSQWorkFlowStatus> workFlowStatuses = new ArrayList<ScSQWorkFlowStatus>();
			ScSQWorkFlowStatus scWorkFlowStatus = new ScSQWorkFlowStatus();
			PmStatus currentStatus = getPmStatusByStatusId(DPStatusConstants.SQ_PENDING_SUBMISSION);
			getCurrentStatus(currentStatus);
			scWorkFlowStatus.setPmStatus(currentStatus);
			scWorkFlowStatus.setQuote(scQuote);
			scWorkFlowStatus.setCurrent(Boolean.TRUE);
			workFlowStatuses.add(scWorkFlowStatus);
			scQuote.setWorkflowStatuses(workFlowStatuses);

		} else {
			getCurrentStatus(scQuote.getCurrentStatus().getPmStatus());
		}
		
		// service and product
		if (scQuote.getQuoteDocs() != null) {
			for (ScQuoteDoc scQuoteDoc : scQuote.getQuoteDocs()) {
				TplSpecMaster specMaster = scQuoteDoc.getSpecMaster();
				if (ParameterConst.SQ_ITEM_LIST_PRODUCT
						.equalsIgnoreCase(specMaster.getItemTypeCode()
								.getParameterCode())) {
					if (specMaster.getTplSpecQuestions() == null) {
						specMaster
								.setTplSpecQuestions(new ArrayList<TplSpecQuestion>());
					}
					productTplSpecBean.setSpecMaster(specMaster);
					getManagedBean().setProductTemplateSpecBean(
							productTplSpecBean);
					// service Tpl Spe
				} else if (ParameterConst.SQ_ITEM_LIST_SERVICE
						.equalsIgnoreCase(specMaster.getItemTypeCode()
								.getParameterCode())) {

					if (specMaster.getTplSpecQuestions() == null) {
						specMaster
								.setTplSpecQuestions(new ArrayList<TplSpecQuestion>());
					}
					serviceTplSpecBean.setSpecMaster(specMaster);
					getManagedBean().setServiceTemplateSpecBean(
							serviceTplSpecBean);
				}
			}
		}
		// zone details
		if (scQuote.getQuoteZone() != null) {
			List<TreeNode> zoneTrees = new ArrayList<TreeNode>();
			List<ZonesBean> beans = getManagedBean().getBeans();

			if (scQuote.getQuoteZone().getTplSpecZoneDtls() != null
					&& scQuote.getQuoteZone().getTplSpecZoneDtls().size() > 0) {
				for (TplSpecZoneDtl tplSpecZoneDtl : scQuote.getQuoteZone()
						.getTplSpecZoneDtls()) {
					ZonesBean zonesBean = new ZonesBean();
					if (tplSpecZoneDtl.getTplSpecZoneLocs() != null
							&& tplSpecZoneDtl.getTplSpecZoneLocs().size() > 0) {
						zonesBean.setTplSpecZoneDtl(tplSpecZoneDtl);
						zonesBean.setTplSpecZoneLocs(tplSpecZoneDtl
								.getTplSpecZoneLocs());
						beans.add(zonesBean);
						if (tplSpecZoneDtl.getPanelZone() != null) {
							zoneTrees.add(constuctTreeNodeWithDisableChilds(
									tplSpecZoneDtl,
									tplSpecZoneDtl.getTplSpecZoneLocs()));
						} else {
							zoneTrees
									.add(LocationSingleton
											.getInstance()
											.constuctTreeNode(
													tplSpecZoneDtl,
													tplSpecZoneDtl
															.getTplSpecZoneLocs()));
						}
					}
				}
				getManagedBean().setZoneTrees(zoneTrees);
				// to check duplicate state or division or district in multiple
				// zones
				getManagedBean().setBeans(beans);
			}
		}

		// by locality
		List<PmDivision> divisions = new ArrayList<PmDivision>();
		if (scQuote.getLocationDivisionCriterias() != null) {
			if (scQuote.getLocationDivisionCriterias().size() > 0) {
				for (ScLocationDivisionCriteria locationDivisionCriteria : scQuote
						.getLocationDivisionCriterias()) {
					divisions.add(locationDivisionCriteria.getPmDivision());
				}
			}
		}
		List<PmDistrict> districts = new ArrayList<PmDistrict>();
		if (scQuote.getLocationDistrictCriterias() != null) {
			if (scQuote.getLocationDistrictCriterias().size() > 0) {
				for (ScLocationDistrictCriteria locationDistrictCriteria : scQuote
						.getLocationDistrictCriterias()) {
					districts.add(locationDistrictCriteria.getPmDistrict());
				}
			}
		}

		List<PmState> states = new ArrayList<PmState>();
		if (districts.isEmpty() && divisions.isEmpty()) {
			if (scQuote.getLocationStateCriterias() != null) {
				if (scQuote.getLocationStateCriterias().size() > 0) {
					for (ScLocationStateCriteria locationStateCriteria : scQuote
							.getLocationStateCriterias()) {
						states.add(locationStateCriteria.getPmState());
					}
				}
			}

		}
		// division
		String locLovel = "";
		if (scQuote.getSupplierLocationLevel() != null) {
			if (scQuote.getSupplierLocationLevel().getParameterCode()
					.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DIVISION)) {
				locLovel = ParameterConst.LOCATION_LEVEL_DIVISION;
				if (scQuote.getLocationStateCriterias() != null) {
					if (scQuote.getLocationStateCriterias().size() > 0) {
						for (ScLocationStateCriteria locationStateCriteria : scQuote
								.getLocationStateCriterias()) {

							if (locationStateCriteria.getPmState() != null) {
								PmState state = locationStateCriteria
										.getPmState();
								if (!(state.getStateName().equalsIgnoreCase(
										"SARAWAK") || state.getStateName()
										.equalsIgnoreCase("SABAH"))) {
									states.add(locationStateCriteria
											.getPmState());
								}
							}
						}
					}
				}
			}
		}
		if (states.size() > 0 || divisions.size() > 0 || districts.size() > 0) {
			getManagedBean().setByLocality(Boolean.TRUE);
			List<TreeNode> LocTrees = new ArrayList<TreeNode>();
			LocTrees.add(LocationSingleton.getInstance().constuctLocationTree(
					locLovel, states, districts, divisions, null,
					TplSpecZoneLoc.class));
			getManagedBean().setLocationTrees(LocTrees.get(0).getChildren());
		} else {
			getManagedBean().setByLocality(Boolean.FALSE);
		}

		if (scQuote.getProcurementType() != null) {
			getManagedBean()
					.setProcurementTypes(
							getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE));
		}

		if (scQuote.getRequestNotes() != null
				&& scQuote.getRequestNotes().size() > 0) {
			getManagedBean().setRequestNote(scQuote.getRequestNotes().get(0));
		}

		// zone location level by default - district
		if (scQuote.getZoneLocatonLevel() != null) {
			getManagedBean().setLocationLevel(
					scQuote.getZoneLocatonLevel().getParameterCode());
		}

		// supplier location level by default - district
		if (scQuote.getSupplierLocationLevel() != null) {
			getManagedBean().setSupplierLocLevel(
					scQuote.getSupplierLocationLevel().getParameterCode());
		}

		// business nature details
		if (scQuote.getBusinessNatureCriterias() != null
				&& scQuote.getBusinessNatureCriterias().size() > 0) {
			getManagedBean().setByBusinessNature(Boolean.TRUE);
			getManagedBean().setBusinessNatureCriterias(
					scQuote.getBusinessNatureCriterias());
		}
		
		CtPanelItem quotePanelItem = null;
		if(null == scQuote.getPanelItem()){
			// panel general item
			quotePanelItem = getTopPanelItem();
		} else{
			quotePanelItem = scQuote.getPanelItem();
		}
		if (scQuote.getScQuoteSuppliers() != null
				&& scQuote.getScQuoteSuppliers().size() > 0) {
			// panel supplier
			if (scQuote.isPanel()) {
				getManagedBean().setPanelSuppliers(
						scQuote.getScQuoteSuppliers());

			} else {
				if (scQuote.getProcurementType() != null
						&& scQuote.getProcurementType().getParameterCode()
								.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {

					// govt supplers
					getManagedBean().setGovtSellers(
							scQuote.getScQuoteSuppliers());

				} else { // supplier list
					getManagedBean().setSupplierList(
							scQuote.getScQuoteSuppliers());
				}
			}
		}
		
		if (quotePanelItem != null) {
			if (quotePanelItem.getItemCategoryId().getParameterCode()
					.equals(ParameterConst.SQ_PANEL_ITEM_GENERAL)) {
				// panel item general
				getManagedBean().setNewableFlag(Boolean.TRUE);
			} else if (quotePanelItem.getItemCategoryId().getParameterCode()
					.equals(ParameterConst.SQ_PANEL_ITEM_SPECIFIC)) {
				// panel item specific
				getManagedBean().setPanelItemFlag(Boolean.FALSE);
				getManagedBean().setNewableFlag(Boolean.FALSE);
			}
		}

		// Initializing Remarks
		scQuote.setRemarks(initializeRemarks(scQuote, null,
				scQuote.getRemarks(), ScSQRemark.class));

		getManagedBean().setScQuote(scQuote);
		// duration calc
		calculateDuration(scQuote);
		procurementTypeEvent();
		
		// re quote 
		if(scQuote.getParentQuoteId() != null){
			getManagedBean().setPanelItemFlag(Boolean.FALSE);
			getManagedBean().setNewableFlag(Boolean.FALSE);
			getManagedBean().setRemovableFlag(Boolean.FALSE);
		}
	}
	

	/**
	 * submit simple quote details. submit - save data into database and BPM
	 * process will route to the next respective user.
	 * 
	 * @param outcome
	 *            the outcome
	 * @return the string page navigate to acknowledge page.
	 */
	public String submitSimpleQuote(String outcome) {
		debugLogger("submitSimpleQuote");
		try {
			QuoteReqDTO reqDTO = new QuoteReqDTO();
			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION,
							IDirectPurchaseSO.class);
			ScQuote quote = getManagedBean().getScQuote();

			if (quote.getRequestItems() == null) {
				List<ScRequestItem> requestItems = new ArrayList<ScRequestItem>();
				ScRequestItem requestItem = new ScRequestItem();
				requestItem.setQuote(quote);
				requestItems.add(requestItem);
				quote.setRequestItems(requestItems);
			}
			
			if (quote.getRequestNotes() == null
					|| quote.getRequestNotes().isEmpty()) {
				List<ScRequestNote> requestNotes = new ArrayList<ScRequestNote>();
				ScRequestNote requestNote = getManagedBean().getRequestNote();
				requestNote.setTitle(quote.getTitle());
				setUserDetails(requestNote);
				requestNote.setRequestNoteNo(directPurchaseSO
						.generateRequestNoteNo());
				requestNote.setSourceMethod(ParameterConst.REQUEST_NOTE_SQ);
				requestNote.setWorkFlowStatuses(null);
				requestNote.setQuote(quote);
				requestNotes.add(requestNote);
				quote.setRequestNotes(requestNotes);
			}

			quote.setProcurementType(getParameterByTypeAndCode(
					ParameterTypeConst.PROCUREMENT_TYPE, getManagedBean()
							.getScQuote().getProcurementType()
							.getParameterCode()));

			// location level
			if (quote.isZonal()) {
				quote.setZoneLocatonLevel(getParameterByTypeAndCode(
						ParameterTypeConst.LOCATION_LEVEL, getManagedBean()
								.getLocationLevel()));
			} else {
				quote.setZoneLocatonLevel(null);
			}
			// supplier location level
			if (getManagedBean().isByLocality()) {
				quote.setSupplierLocationLevel(getParameterByTypeAndCode(
						ParameterTypeConst.LOCATION_LEVEL, getManagedBean()
								.getSupplierLocLevel()));
			} else {
				quote.setSupplierLocationLevel(null);
			}
			
			if(outcome.equals(BpmOutcomeConst.BPM_SUBMIT)){
				if (null == quote.getProcurementTypeCategory()) {
					throw new EpException(
							MessageConst.ERROR_760315,
							new String[] {ResourceBundleUtils.getMessage("lbl.sc.procurment.type.category",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				} else {
					// procurement type category
					ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO.setParameterId(quote
							.getProcurementTypeCategory().getParameterId());
					PmParameter procTypeCategory = getParameterSO()
							.retrieveParameterById(parameterReqDTO)
							.getPmParameter();
					quote.setProcurementTypeCategory(procTypeCategory);
				}
			}	


			// justification type
			if (quote.getProcurementType().getParameterCode()
					.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {

				if (quote.getJustificationType().getReasonCode() == null) {
					throw new EpException(MessageConst.ERROR_100022,
							new String[] { ResourceBundleUtils.getMessage(
									"lbl.sc.reason",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale()) });
				}

				quote.setJustificationType(getReasonByCategoryAndCode(
						ReasonCategory.JUSTIFICATION_AD_HOC.value(), quote
								.getJustificationType().getReasonCode()));
			} else {
				quote.setJustificationType(null);
			}

			// by business nature
			if (getManagedBean().getBusinessNatureCriterias() != null
					&& getManagedBean().getBusinessNatureCriterias().size() > 0) {
				quote.setBusinessNatureCriterias(getManagedBean()
						.getBusinessNatureCriterias());
			}

			// zone details
			quote.setQuoteZone(getQuoteZones(getManagedBean().getZoneTrees()));
			List<ScQuoteSupplier> suppliers = new ArrayList<ScQuoteSupplier>();
			// panel item
			if(quote.isPanel()){
				if (quote.getPanelItem() != null) {
					validatePanelItem(quote.getPanelItem());
				}
				// panel suppliers
				if (getManagedBean().getPanelSuppliers() != null) {
					for (ScQuoteSupplier scQuoteSupplier : getManagedBean()
							.getPanelSuppliers()) {
						if (quote.getScQuoteSuppliers() != null
								&& !quote.getScQuoteSuppliers().isEmpty()) {
							if (!quote.getScQuoteSuppliers().contains(
									scQuoteSupplier)) {
								suppliers.add(scQuoteSupplier);
							}
						} else {
							suppliers.add(scQuoteSupplier);
						}
					}
				}

			} else {
				if (quote.getProcurementType() != null) {

					if (quote.getProcurementType().getParameterCode()
							.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
						// govt sellers
						if (getManagedBean().getGovtSellers() != null) {
							suppliers.addAll(getManagedBean().getGovtSellers());
						}
					} else {
						// by supplier
						if (getManagedBean().getSupplierList() != null) {
							suppliers
									.addAll(getManagedBean().getSupplierList());
						}
					}
				}
				// by location
				if (!getManagedBean().getLocationTrees().isEmpty()) {
					quote.setLocationStateCriterias(getLocationStateCriterias(
							getManagedBean().getLocationTrees(), quote));
					quote.setLocationDivisionCriterias(getLocationDivisionCriterias(
							getManagedBean().getLocationTrees(), quote));
					quote.setLocationDistrictCriterias(getLocationDistrictCriterias(
							getManagedBean().getLocationTrees(), quote));
				} else {
					if (quote.getLocationStateCriterias() != null) {
						quote.getLocationStateCriterias().clear();
					}
					if (quote.getLocationDivisionCriterias() != null) {
						quote.getLocationDivisionCriterias().clear();
					}
					if (quote.getLocationDistrictCriterias() != null) {
						quote.getLocationDistrictCriterias().clear();
					}
				}
			}

			if (suppliers != null && !suppliers.isEmpty()) {
				quote.setScQuoteSuppliers(suppliers);
			}
			
			// remarks
			if(!BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)){
				finalizeRemarks(quote.getRemarks());
			}
			
			quote.setWorkflowStatuses(null);
			// To avoid insert workflow status when direct submit without save.
			if (quote.isBrandNew()) {
				List<ScSQRemark> remarks = quote.getRemarks();
				if (CollectionUtils.isNotEmpty(remarks)) {
					for (ScSQRemark remark : remarks) {
						remark.setScWorkFlowStatus(null);
					}
				}	
			}

			// product and service
			List<ScQuoteDoc> quoteDocs = new ArrayList<ScQuoteDoc>();
			if (quote.getQuoteDocs() != null && quote.getQuoteDocs().size() > 0) {
				for (ScQuoteDoc doc : quote.getQuoteDocs()) {

					if (doc.getSpecMaster().getItemTypeCode()
							.getParameterCode()
							.equals(ParameterConst.ITEM_TYPE_PRODUCT)) {
						doc.setSpecMaster(getManagedBean()
								.getProductTemplateSpecBean().getSpecMaster());
						quoteDocs.add(doc);
					} else if (doc.getSpecMaster().getItemTypeCode()
							.getParameterCode()
							.equals(ParameterConst.ITEM_TYPE_SERVICE)) {
						doc.setSpecMaster(getManagedBean()
								.getServiceTemplateSpecBean().getSpecMaster());
						quoteDocs.add(doc);
					}
				}
			} else {
				// tpl spec - product
				if (getManagedBean().getProductTemplateSpecBean()
						.getSpecMaster() != null) {
					if (getManagedBean().getProductTemplateSpecBean()
							.getSpecMaster().getTplSpecQuestions() != null
							&& getManagedBean().getProductTemplateSpecBean()
									.getSpecMaster().getTplSpecQuestions()
									.size() > 0) {
						ScQuoteDoc quoteDoc = new ScQuoteDoc();
						TplSpecMaster specMaster = getManagedBean()
								.getProductTemplateSpecBean().getSpecMaster();
						if (quote.getQuoteZone() != null) {
							specMaster.setTplSpecZone(quote.getQuoteZone());
						}
						quoteDoc.setSpecMaster(specMaster);
						quoteDoc.setQuote(quote);
						quoteDocs.add(quoteDoc);
						quote.setQuoteDocs(quoteDocs);
					}
				}
				// tpl spec - service
				if (getManagedBean().getServiceTemplateSpecBean()
						.getSpecMaster() != null) {
					if (getManagedBean().getServiceTemplateSpecBean()
							.getSpecMaster().getTplSpecQuestions() != null
							&& getManagedBean().getServiceTemplateSpecBean()
									.getSpecMaster().getTplSpecQuestions()
									.size() > 0) {
						ScQuoteDoc quoteDoc = new ScQuoteDoc();
						TplSpecMaster specMaster = getManagedBean()
								.getServiceTemplateSpecBean().getSpecMaster();
						if (quote.getQuoteZone() != null) {
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
			reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setOrgProfileId(getManagedBean().getRequestNote()
					.getPmOrgProfile().getOrgProfileId());
			reqDTO.setQuoteBidDtl(quote);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setPmUser(facesScopes.getUserSessionBean());
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setTaskExpiredDate(getManagedBean().getTaskExpiryDate());
			reqDTO.setPidApprovers(getManagedBean().getPidApprovers());

			// ReQuote
			reqDTO.setEndLoaOutcome(getManagedBean().getEndLoaOutcome());
			reqDTO.setEndLoaTaskId(getManagedBean().getEndLoaTaskId());
			reqDTO.setEndLoaToken(getManagedBean().getEndLoaToken());
			reqDTO.setEndLoaRN(getManagedBean().getEndLoaRN());

			QuoteResDTO quoteResDTO = null;
			if (outcome.isEmpty()) {
				reqDTO.setSubmit(Boolean.TRUE);
				quoteResDTO = getQuoteSO().saveQuoteDtl(reqDTO);
			} else if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome)
					|| BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)) {
				reqDTO.setOutcome(outcome);
				quoteResDTO = getQuoteSO().updateSimpleQuote(reqDTO);
			}
			// submit prompt hiding
			// RequestContext.getCurrentInstance().execute("promptWidget.hide()");

			if (quoteResDTO.isSuccessful()) {
				ScQuote scQuote = quoteResDTO.getQuoteBidDtl();
				if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome)
						|| outcome.isEmpty()) {
					successMessage(MessageConst.INFO_700026,
							ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_SIMPLE_QUOTE,
							scQuote.getQuoteNo(), Boolean.TRUE);
				} else if (BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)) {
					successMessage(MessageConst.INFO_700010,
							ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_SIMPLE_QUOTE,
							scQuote.getQuoteNo(), Boolean.TRUE);
				}
				// clear SQ data
				super.renewSesionObject();

			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
		} finally {
			// quoteSO = null;
		}
		return null;
	}

	/**
	 * By loc and business event. gets the supplier location tab if selects by
	 * location gets the business nature tab if selects by business nature in
	 * supplier criteria
	 */
	public void byLocAndBusinessEvent() {
		debugLogger("byBusinessEvent");
		if (getManagedBean().isByLocality()) {
			getManagedBean().setTabActiveIndex(0);

			if (getManagedBean().getSupplierLocLevel() == null) {
				getManagedBean().setSupplierLocLevel(
						ParameterConst.LOCATION_LEVEL_DISTRICT);
			}

			if(null == getManagedBean().getStoreLocationTree()){
				ptjLocality(ParameterConst.LOCATION_LEVEL_DISTRICT);
			} else{
				getManagedBean().setLocationTrees(getManagedBean().getStoreLocationTree());
			}

		} else {
			getManagedBean().setByLocality(Boolean.FALSE);
			if (getManagedBean().getLocationTrees() != null
					&& getManagedBean().getLocationTrees().size() > 0) {
				List<TreeNode> locationTrees = new ArrayList<TreeNode>();
				locationTrees.addAll(getManagedBean().getLocationTrees());
				getManagedBean().setStoreLocationTree(locationTrees);
				getManagedBean().getLocationTrees().clear();
			}
			/*if (getManagedBean().getScQuote().getLocationStateCriterias() != null
					&& getManagedBean().getScQuote()
							.getLocationStateCriterias().size() > 0) {
				getManagedBean().getScQuote().getLocationStateCriterias()
						.clear();
			}
			if (getManagedBean().getScQuote().getLocationDivisionCriterias() != null
					&& getManagedBean().getScQuote()
							.getLocationDivisionCriterias().size() > 0) {
				getManagedBean().getScQuote().getLocationDivisionCriterias()
						.clear();
			}
			if (getManagedBean().getScQuote().getLocationDistrictCriterias() != null
					&& getManagedBean().getScQuote()
							.getLocationDistrictCriterias().size() > 0) {
				getManagedBean().getScQuote().getLocationDistrictCriterias()
						.clear();
			}*/
			getManagedBean().setSupplierLocLevel(null);
		}

		if (getManagedBean().isByBusinessNature()) {
			getManagedBean().setTabActiveIndex(1);

		} else {
			List<ScBusinessNatureQuoteCriteria> businessNatureCriterias = getManagedBean().getBusinessNatureCriterias();
			if (businessNatureCriterias != null
					&& businessNatureCriterias.size() > 0) {

				// issue : select one business nature and save then uncheck and check business nature checkbox
				//re add same business nature and then save 
				Map<Long, ScBusinessNatureQuoteCriteria> deletedBusinessNatureMap = getManagedBean().getDeletedBusinessNatureMap();
				if(null == deletedBusinessNatureMap){
					deletedBusinessNatureMap =  new HashMap<Long, ScBusinessNatureQuoteCriteria>();
				}

				for (ScBusinessNatureQuoteCriteria natureQuoteCriteria : businessNatureCriterias) {
					deletedBusinessNatureMap.put(natureQuoteCriteria.getPmBusinessNature().getBusinessNatureId(), natureQuoteCriteria);
				}
				getManagedBean().setDeletedBusinessNatureMap(deletedBusinessNatureMap);

				businessNatureCriterias.clear();
			}

			if (getManagedBean().getBusinessNatures() != null
					&& getManagedBean().getBusinessNatures().size() > 0) {
				getManagedBean().getBusinessNatures().clear();
			}

			// set index to default tab
			getManagedBean().setTabActiveIndex(0);
		}
	}

	/**
	 * Event for procurement type drop down in SQ. to show justification drop
	 * down and justification remark based on procurement type.
	 */
	public void procurementTypeEvent() {
		debugLogger("procurementTypeEvent");
		PortletSession portletSession = (PortletSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);
		TemplateSpecBackingBean templateSpecBackingBean = (TemplateSpecBackingBean) portletSession
				.getAttribute("templateSpecBackingBean");
		if (templateSpecBackingBean == null) {
			templateSpecBackingBean = new TemplateSpecBackingBean();
		}
		templateSpecBackingBean.getManagedBean().setG2G(false);
		if (getManagedBean().getScQuote().getProcurementType()
				.getParameterCode()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL)) {
			getManagedBean().setProcTypeOption(Boolean.FALSE);
			getManagedBean().setJustTypeOption(Boolean.FALSE);
			getManagedBean().setG2GFlag(Boolean.FALSE);
			getManagedBean().setLocAndBusinessFlag(Boolean.TRUE);

		} else if (getManagedBean().getScQuote().getProcurementType()
				.getParameterCode()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			getManagedBean().setProcTypeOption(Boolean.FALSE);
			getManagedBean().setJustTypeOption(Boolean.FALSE);
			getManagedBean().setG2GFlag(Boolean.TRUE);
			getManagedBean().setLocAndBusinessFlag(Boolean.FALSE);
			getManagedBean().setByBusinessNature(Boolean.FALSE);
			getManagedBean().setByLocality(Boolean.FALSE);
			templateSpecBackingBean.getManagedBean().setG2G(true);
		} else if (getManagedBean().getScQuote().getProcurementType()
				.getParameterCode()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			getManagedBean().setProcTypeOption(Boolean.TRUE);
			getManagedBean().setG2GFlag(Boolean.FALSE);
			getManagedBean().setLocAndBusinessFlag(Boolean.TRUE);
		    getManagedBean().setJustDropdown(getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_AD_HOC.value()));
		} 

		if (getManagedBean().getScQuote().isPanel()) {
			getManagedBean().setLocAndBusinessFlag(Boolean.FALSE);
			getManagedBean().setG2GFlag(Boolean.FALSE);
			getManagedBean().setByBusinessNature(Boolean.FALSE);
			getManagedBean().setByLocality(Boolean.FALSE);
		}

		if (getManagedBean().getScQuote().getJustificationType() == null) {
			getManagedBean().getScQuote().setJustificationType(
					new PmReason());
		}

	}

	
	/**
	 * delete business nature from business nature in supplier criteria
	 */
	public void deleteBusinessNature() {
		debugLogger("deleteBusinessNature");
		if (getManagedBean().getSelectedBusinessNature() != null) {
			
			Map<Long, ScBusinessNatureQuoteCriteria> deletedBusinessNatureMap = getManagedBean().getDeletedBusinessNatureMap();
			if(null == deletedBusinessNatureMap){
				deletedBusinessNatureMap =  new HashMap<Long, ScBusinessNatureQuoteCriteria>();
			}
			
			for (ScBusinessNatureQuoteCriteria natureQuoteCriteria : getManagedBean()
					.getSelectedBusinessNature()) {
				if (getManagedBean().getBusinessNatureCriterias().contains(
						natureQuoteCriteria)) {
					List<ScBusinessNatureQuoteCriteria> businessNatureQuoteCriterias = getManagedBean()
							.getBusinessNatureCriterias();
					deletedBusinessNatureMap.put(natureQuoteCriteria.getPmBusinessNature().getBusinessNatureId(), natureQuoteCriteria);
					businessNatureQuoteCriterias.remove(natureQuoteCriteria);
					getManagedBean().setBusinessNatureCriterias(
							businessNatureQuoteCriterias);
					getManagedBean().setDeletedBusinessNatureMap(deletedBusinessNatureMap);
				}
			}
		}
		 getManagedBean()
			.setSelectedBusinessNature(null);
	}

	/**
	 * delete supplier from supplier list in supplier criteria
	 */
	public void deleteSupplier() {
		debugLogger("deleteSupplier");
		if (getManagedBean().getSelectedSupplierList() != null
				&& getManagedBean().getSelectedSupplierList().size() > 0) {

			Map<Long, ScQuoteSupplier> deletedSupplierMap = getManagedBean().getDeletedSupplierMap();
			if(null == deletedSupplierMap){
				deletedSupplierMap =  new HashMap<Long, ScQuoteSupplier>();
			}

			for (ScQuoteSupplier supplier : getManagedBean()
					.getSelectedSupplierList()) {
				if (getManagedBean().getSupplierList().contains(supplier)) {
					List<ScQuoteSupplier> supplierList = getManagedBean()
							.getSupplierList();
					deletedSupplierMap.put(supplier.getSmSupplier().getSupplierId(), supplier);
					supplierList.remove(supplier);
					getManagedBean().setSupplierList(supplierList);
					getManagedBean().setDeletedSupplierMap(deletedSupplierMap);
				}
			}
			getManagedBean().setSelectedSupplierList(null);
		} else {
			Locale locale = new FacesScopes().getUserLocale();
			throw new EpException(MessageConst.ERROR_760165,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.sc.supplier.list",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
		}
	}

	/**
	 * delete govt seller from govt seller in supplier criteria
	 */
	public void deleteGovtSeller() {
		debugLogger("deleteGovtSeller");
		if (getManagedBean().getSelectedGovtSellers() != null
				&& getManagedBean().getSelectedGovtSellers().size() > 0) {
			for (ScQuoteSupplier govtSeller : getManagedBean()
					.getSelectedGovtSellers()) {
				if (getManagedBean().getGovtSellers().contains(govtSeller)) {
					List<ScQuoteSupplier> govtSuppliers = getManagedBean()
							.getGovtSellers();
					govtSuppliers.remove(govtSeller);
					getManagedBean().setGovtSellers(govtSuppliers);
				}
			}
			getManagedBean().setSelectedGovtSellers(null);
		} else {
			Locale locale = new FacesScopes().getUserLocale();
			throw new EpException(MessageConst.ERROR_760165,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.sc.agency",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
		}
	}

	/**
	 * delete panel supplier from panel supplier list in supplier criteria
	 */
	public void deletePanelSupplier() {
		debugLogger("deletePanelSupplier");

		if (getManagedBean().getSelectedPanelSuppliers() != null
				&& getManagedBean().getSelectedPanelSuppliers().size() > 0) {
			for (ScQuoteSupplier scQuoteSupplier : getManagedBean()
					.getSelectedPanelSuppliers()) {
				if (getManagedBean().getPanelSuppliers().contains(
						scQuoteSupplier)) {
					List<ScQuoteSupplier> suppliers = getManagedBean()
							.getPanelSuppliers();
					suppliers.remove(scQuoteSupplier);
					getManagedBean().setPanelSuppliers(suppliers);
				}
			}
			getManagedBean().setSelectedPanelSuppliers(null);
		} else {
			Locale locale = new FacesScopes().getUserLocale();
			throw new EpException(MessageConst.ERROR_760165,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.sc.sq.panel.supplier",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
		}
	}

	/**
	 * show zone popup to add the zone in Zone in simple quote.
	 */
	public void showZonePopup() {
		String locationLevel = this.getManagedBean().getLocationLevel();
		if (locationLevel == null) {
			locationLevel = ParameterConst.LOCATION_LEVEL_DISTRICT;
		}
		getManagedBean().setRootZoneTree(
				LocationSingleton.getInstance().buildLocationPopupTree(
						getManagedBean().getDefaultCountryId(), null, null,
						null, null, locationLevel, TplSpecZoneLoc.class,
						false));
		// zone required validation
		getManagedBean().setRequired(Boolean.TRUE);
		getManagedBean().setZoneName("");
		getManagedBean().setAutoUpdate(Boolean.FALSE);
	}

	/**
	 * show location popup to add the location in supplier location in simple
	 * quote.
	 */
	public void showLocationPopup() {
		String locationLevel = this.getManagedBean().getSupplierLocLevel();
		if (locationLevel == null) {
			locationLevel = ParameterConst.LOCATION_LEVEL_DISTRICT;
		}

		List<TplSpecZoneLoc> locationDTOs = new ArrayList<TplSpecZoneLoc>();
		if (getManagedBean().getLocationTrees() != null) {
			for (TreeNode treeNode : getManagedBean().getLocationTrees()) {
				List<TplSpecZoneLoc> locationDTO = getLocations(treeNode);
				locationDTOs.addAll(locationDTO);
			}
		}

		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(locationDTOs);

		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationPopupTree(
						getManagedBean().getDefaultCountryId(), states,
						districts, divisions, null, locationLevel,
						TplSpecZoneLoc.class,
						false));
	}

	/**
	 * adding selected zone to the zone from the zone popup in simple quote.
	 */
	public void updateZone() {
		ScQuote scQuote = getManagedBean().getScQuote();
		List<ZonesBean> beans = getManagedBean().getBeans();
		ZonesBean zonesBean = new ZonesBean();
		String locationLevel = this.getManagedBean().getLocationLevel();
		List<TplSpecZoneLoc> locationDTOs = new ArrayList<TplSpecZoneLoc>();
		if (getManagedBean().getSelectedNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedNodes()) {
				if (locationLevel
						.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DIVISION)) {
					if (treeNode.getType().equals("state")) {
						if (treeNode.getChildCount() == 0
								&& treeNode.getChildren().isEmpty()) {
							List<TplSpecZoneLoc> locationDTO = getLocations(treeNode);
							locationDTOs.addAll(locationDTO);
						}
					} else {
						List<TplSpecZoneLoc> locationDTO = getLocations(treeNode);
						locationDTOs.addAll(locationDTO);
					}
				} else {

					if (treeNode.getParent().isSelected()) {
						List<TplSpecZoneLoc> locationDTO = getLocations(treeNode);
						locationDTOs.addAll(locationDTO);
					} else {
						List<TplSpecZoneLoc> locationDTO = getLocations(treeNode);
						locationDTOs.addAll(locationDTO);
					}
				}
			}
		}

		if (locationDTOs != null && !locationDTOs.isEmpty()) {
			TplSpecZoneDtl tplSpecZoneDtl = new TplSpecZoneDtl();
			ScQuoteZone quoteZone = getManagedBean().getScQuote()
					.getQuoteZone();
			quoteZone.setModified(Boolean.TRUE);
			quoteZone.setScQuote(scQuote);
			tplSpecZoneDtl.setTplSpecZone(quoteZone);

			if (null == getManagedBean().getZoneName()
					|| StringUtils.isBlank(getManagedBean().getZoneName())) {
				throw new EpException(MessageConst.ERROR_716003);
			}

			tplSpecZoneDtl.setZoneName(getManagedBean().getZoneName());
			List<PmState> states = SourcingLocationHelper
					.getStateFromLocationDTO(locationDTOs);
			List<PmDivision> divisions = SourcingLocationHelper
					.getDivisionFromLocationDTO(locationDTOs);
			List<PmDistrict> districts = SourcingLocationHelper
					.getDistrictFromLocationDTO(locationDTOs);
			/*
			 * List<PmCity> cities = SourcingLocationHelper
			 * .getCityFromLocationDTO(locationDTOs);
			 */

			String editZoneName = getManagedBean().getTempZoneName();
			if (getManagedBean().getZoneIndex() >= 0) {
				updateZoneBean(editZoneName);
			}

			tplSpecZoneDtl.setTplSpecZoneLocs(locationDTOs);
			zonesBean.setTplSpecZoneDtl(tplSpecZoneDtl);

			checkZoneExistsOrNot(tplSpecZoneDtl);

			if (locationLevel
					.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_STATE)) {
				// check whether state exists or not
				if (!getManagedBean().getBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(tplSpecZoneDtl);
				}
			}
			if (locationLevel
					.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DIVISION)) {
				// check whether division exists or not
				if (!getManagedBean().getBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(tplSpecZoneDtl);
				}
			}
			if (locationLevel
					.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DISTRICT)) {
				// check whether district exists or not
				if (!getManagedBean().getBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(tplSpecZoneDtl);
				}
			}
			beans.add(zonesBean);

			if (getManagedBean().getZoneIndex() == -1) {
				getManagedBean().getZoneTrees().add(
						LocationSingleton.getInstance().buildLocationTree(
								tplSpecZoneDtl, states, districts, divisions,
								null, TplSpecZoneLoc.class));

			} else if (getManagedBean().getZoneIndex() >= 0) {
				beans.remove(getManagedBean().getZoneIndex());

				getManagedBean().getZoneTrees().remove(
						getManagedBean().getZoneIndex());
				getManagedBean().getZoneTrees().add(
						getManagedBean().getZoneIndex(),
						LocationSingleton.getInstance().buildLocationTree(
								tplSpecZoneDtl, states, districts, divisions,
								null, TplSpecZoneLoc.class));
			}
			getManagedBean().getBeans().add(zonesBean);
			this.getManagedBean().setZoneIndex(-1);

			// zone details
			if (getManagedBean().getScQuote().isZonal()) {
				if (!getManagedBean().getZoneTrees().isEmpty()) {
					scQuote.setQuoteZone(getQuoteZones(getManagedBean()
							.getZoneTrees()));
				}
			}
		} else {
			throw new EpException(MessageConst.ERROR_760165,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.sc.zone.location",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		}

		getManagedBean().setZoneName(null);
		getManagedBean().setTempZoneName(null);

		// update zone when add or edit zone
		updateSpecMasterZone(scQuote);

		// zone required validation
		getManagedBean().setRequired(Boolean.FALSE);
		// hide zone location popup
		RequestContext.getCurrentInstance().execute("ZoneLocation.hide();");
	}

	/**
	 * Update spec master zone.
	 * 
	 * @param scQuote
	 *            the sc quote
	 */
	private void updateSpecMasterZone(ScQuote scQuote) {

		// product
		if (getManagedBean().getProductTemplateSpecBean().getSpecMaster() != null) {
			TemplateSpecHelper.getInstance().updateZoneDtl(
					getManagedBean().getProductTemplateSpecBean()
							.getSpecMaster(), scQuote.getQuoteZone());
		}

		// service
		if (getManagedBean().getServiceTemplateSpecBean().getSpecMaster() != null) {
			TemplateSpecHelper.getInstance().updateZoneDtl(
					getManagedBean().getServiceTemplateSpecBean()
							.getSpecMaster(), scQuote.getQuoteZone());
		}
	}

	/**
	 * Update zone bean.
	 * 
	 * @param zoneName
	 *            the zone name
	 */
	private void updateZoneBean(String zoneName) {
		List<ZonesBean> beans = new ArrayList<ZonesBean>();
		List<ZonesBean> zoneBeans = getManagedBean().getBeans();

		if (zoneBeans != null && zoneBeans.size() > 0) {
			for (ZonesBean zonesBean : zoneBeans) {
				TplSpecZoneDtl prevsZone = zonesBean.getTplSpecZoneDtl();
				if (prevsZone != null) {
					if (!prevsZone.getZoneName().equalsIgnoreCase(zoneName)) {
						beans.add(zonesBean);
					}
				}
			}
		}
		getManagedBean().setBeans(beans);

	}

	/**
	 * adding selected location to the supplier location from the location popup
	 * in simple quote.
	 */
	public void updateLocation() {
		getManagedBean().getLocationTrees().clear();
		String locationLevel = this.getManagedBean().getSupplierLocLevel();
		List<TreeNode> LocTrees = new ArrayList<TreeNode>();
		List<TplSpecZoneLoc> locationDTOs = new ArrayList<TplSpecZoneLoc>();
		if (getManagedBean().getSelectedLocNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedLocNodes()) {

				if (locationLevel
						.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DIVISION)) {
					if (treeNode.getType().equals("state")) {
						if (treeNode.getChildCount() == 0
								&& treeNode.getChildren().isEmpty()) {
							List<TplSpecZoneLoc> locationDTO = getLocations(treeNode);
							locationDTOs.addAll(locationDTO);
						}
					} else {
						List<TplSpecZoneLoc> locationDTO = getLocations(treeNode);
						locationDTOs.addAll(locationDTO);
					}
				} else {

					if (treeNode.getParent().isSelected()) {
						List<TplSpecZoneLoc> locationDTO = getLocations(treeNode);
						locationDTOs.addAll(locationDTO);
					} else {
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

		LocTrees.add(LocationSingleton.getInstance().constuctLocationTree(
				locationLevel, states, districts, divisions, null,
				TplSpecZoneLoc.class));
		getManagedBean().getLocationTrees().addAll(
				LocTrees.get(0).getChildren());

		getManagedBean().getSelectedLocationTree().clear();
		// hide location popup
		RequestContext.getCurrentInstance().execute("locationPopup.hide();");
	}

	/**
	 * Gets the locations.
	 * 
	 * @param zoneLocation
	 *            the zone location
	 * @return the locations
	 */
	private List<TplSpecZoneLoc> getLocations(TreeNode zoneLocation) {
		List<TplSpecZoneLoc> results = new ArrayList<TplSpecZoneLoc>();
		if (zoneLocation.isLeaf()) {
			TplSpecZoneLoc locationDTO = (TplSpecZoneLoc) zoneLocation
					.getData();
			results.add(locationDTO);
		} else {
			for (TreeNode childrendLocationNode : zoneLocation.getChildren()) {
				results.addAll(getLocations(childrendLocationNode));
			}
		}
		return results;
	}

	/*
	 * private ScQuoteZone getZone(TreeNode zoneLocation) { ScQuoteZone result =
	 * null; if (zoneLocation.getData() instanceof LocationDTO) { LocationDTO
	 * locationDTO = (LocationDTO) zoneLocation.getData(); if
	 * (locationDTO.getZone() != null) { //result = locationDTO.getZone(); }
	 * else { if (zoneLocation.getChildren() != null &&
	 * zoneLocation.getChildren().size() > 0) { for (TreeNode childLocationNode
	 * : zoneLocation .getChildren()) { result = getZone(childLocationNode); } }
	 * } } else { if (zoneLocation.getChildren() != null &&
	 * zoneLocation.getChildren().size() > 0) { for (TreeNode childLocationNode
	 * : zoneLocation.getChildren()) { result = getZone(childLocationNode); } }
	 * } return result; }
	 */

	/**
	 * edit selected zone in zone
	 * 
	 * @param rootZoneLocation
	 *            the root zone location
	 * @param zoneIndex
	 *            the zone index
	 */
	public void editZone(TreeNode rootZoneLocation, int zoneIndex) {
		String locationLevel = this.getManagedBean().getLocationLevel();
		List<TplSpecZoneLoc> locationDTOs = getLocations(rootZoneLocation);
		if (rootZoneLocation.getChildCount() > 0) {
			for (TreeNode rootNode : rootZoneLocation.getChildren()) {
				TplSpecZoneDtl tplSpecZoneDtl = (TplSpecZoneDtl) rootNode
						.getData();
				getManagedBean().setZoneName(tplSpecZoneDtl.getZoneName());
				getManagedBean().setTempZoneName(tplSpecZoneDtl.getZoneName());
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
				LocationSingleton.getInstance().buildLocationPopupTree(
						getManagedBean().getDefaultCountryId(), states,
						districts, divisions, cities, locationLevel,
						TplSpecZoneLoc.class,
						false));
		int i = zoneIndex;
		this.getManagedBean().setZoneIndex(i);
		getManagedBean().setAutoUpdate(Boolean.FALSE);
	}

	/**
	 * edit selected supplier location in supplier location
	 * 
	 * @param rootZoneLocation
	 *            the root zone location
	 * @param LocIndex
	 *            the loc index
	 */
	public void editLocation(TreeNode rootZoneLocation, int LocIndex) {
		String locationLevel = this.getManagedBean().getSupplierLocLevel();

		List<TplSpecZoneLoc> locationDTOs = new ArrayList<TplSpecZoneLoc>();
		if (getManagedBean().getLocationTrees() != null) {
			for (TreeNode treeNode : getManagedBean().getLocationTrees()) {
				List<TplSpecZoneLoc> locationDTO = getLocations(treeNode);
				locationDTOs.addAll(locationDTO);
			}
		}

		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(locationDTOs);

		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationPopupTree(
						getManagedBean().getDefaultCountryId(), states,
						districts, divisions, null, locationLevel,
						TplSpecZoneLoc.class,
						false));
	}

	/**
	 * Gets the pm states.
	 * 
	 * @param treeNodes
	 *            the tree nodes
	 * @return the pm states
	 */
	private List<PmState> getPmStates(List<TreeNode> treeNodes) {
		List<PmState> results = new ArrayList<PmState>();
		for (TreeNode rootNode : treeNodes) {
			for (TreeNode treeNode : rootNode.getChildren()) {
				if (treeNode.getType().equals("state")) {
					TplSpecZoneLoc locationDTO = (TplSpecZoneLoc) treeNode
							.getData();
					if (locationDTO.getPmState() != null) {

						results.add(locationDTO.getPmState());
					}
				}
			}
		}
		return results;
	}

	/**
	 * Gets the pm divisions.
	 * 
	 * @param treeNodes
	 *            the tree nodes
	 * @return the pm divisions
	 */
	private List<PmDivision> getPmDivisions(List<TreeNode> treeNodes) {
		List<PmDivision> results = new ArrayList<PmDivision>();
		for (TreeNode rootNode : treeNodes) {
			for (TreeNode treeNode : rootNode.getChildren()) {
				for (TreeNode childNode : treeNode.getChildren()) {
					if (childNode.getType().equals("division")) {
						TplSpecZoneLoc locationDTO = (TplSpecZoneLoc) childNode
								.getData();
						if (locationDTO.getPmDivision() != null) {
							results.add(locationDTO.getPmDivision());
						}
					}
				}
			}
		}
		return results;
	}

	/**
	 * Gets the pm districts.
	 * 
	 * @param treeNodes
	 *            the tree nodes
	 * @return the pm districts
	 */
	private List<PmDistrict> getPmDistricts(List<TreeNode> treeNodes) {
		List<PmDistrict> results = new ArrayList<PmDistrict>();
		for (TreeNode treeNode : treeNodes) {
			if (treeNode.isLeaf()) {
				TplSpecZoneLoc locationDTO = (TplSpecZoneLoc) treeNode
						.getData();
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

	/**
	 * Gets the location state criterias.
	 * 
	 * @param treeNodes
	 *            the tree nodes
	 * @param quote
	 *            the quote
	 * @return the location state criterias
	 */
	private List<ScLocationStateCriteria> getLocationStateCriterias(
			List<TreeNode> treeNodes, ScQuote quote) {
		List<ScLocationStateCriteria> results = new ArrayList<ScLocationStateCriteria>();
		List<PmState> states = getPmStates(treeNodes);
		if (states != null && states.size() > 0) {
			for (PmState state : states) {
				boolean isState = false;
				if (quote.getLocationStateCriterias() != null
						&& quote.getLocationStateCriterias().size() > 0) {
					for (ScLocationStateCriteria criteriaState : quote
							.getLocationStateCriterias()) {
						if (state.getStateId().equals(
								criteriaState.getPmState().getStateId())) {
							isState = true;
							results.add(criteriaState);
						}
					}
				}
				if (!isState) {
					ScLocationStateCriteria stateCriteria = new ScLocationStateCriteria();
					stateCriteria.setPmState(state);
					stateCriteria.setQuote(quote);
					results.add(stateCriteria);
				}
			}
		}
		return results;
	}

	/**
	 * Gets the location division criterias.
	 * 
	 * @param treeNodes
	 *            the tree nodes
	 * @param quote
	 *            the quote
	 * @return the location division criterias
	 */
	private List<ScLocationDivisionCriteria> getLocationDivisionCriterias(
			List<TreeNode> treeNodes, ScQuote quote) {
		List<ScLocationDivisionCriteria> results = new ArrayList<ScLocationDivisionCriteria>();
		List<PmDivision> divisions = getPmDivisions(treeNodes);
		if (divisions != null && divisions.size() > 0) {
			for (PmDivision division : divisions) {
				boolean isDivision = false;
				if (quote.getLocationDivisionCriterias() != null
						&& quote.getLocationDivisionCriterias().size() > 0) {
					for (ScLocationDivisionCriteria criteriaDiv : quote
							.getLocationDivisionCriterias()) {
						if (division.getDivisionId().equals(
								criteriaDiv.getPmDivision().getDivisionId())) {
							isDivision = true;
							results.add(criteriaDiv);
						}
					}
				}
				if (!isDivision) {
					ScLocationDivisionCriteria divisionCriteria = new ScLocationDivisionCriteria();
					divisionCriteria.setPmDivision(division);
					divisionCriteria.setQuote(quote);
					results.add(divisionCriteria);
				}
			}
		}
		return results;
	}

	/**
	 * Gets the location district criterias.
	 * 
	 * @param treeNodes
	 *            the tree nodes
	 * @param quote
	 *            the quote
	 * @return the location district criterias
	 */
	private List<ScLocationDistrictCriteria> getLocationDistrictCriterias(
			List<TreeNode> treeNodes, ScQuote quote) {
		List<ScLocationDistrictCriteria> results = new ArrayList<ScLocationDistrictCriteria>();
		for (TreeNode treeNode : treeNodes) {
			List<PmDistrict> districts = getPmDistricts(treeNode.getChildren());
			if (districts != null && districts.size() > 0) {
				for (PmDistrict district : districts) {
					boolean isDistict = false;
					if (quote.getLocationDistrictCriterias() != null
							&& quote.getLocationDistrictCriterias().size() > 0) {
						for (ScLocationDistrictCriteria criteriaDist : quote
								.getLocationDistrictCriterias()) {
							if (district.getDistrictId().equals(
									criteriaDist.getPmDistrict()
											.getDistrictId())) {
								isDistict = true;
								results.add(criteriaDist);
							}
						}
					}
					if (!isDistict) {
						ScLocationDistrictCriteria districtCriteria = new ScLocationDistrictCriteria();
						districtCriteria.setQuote(quote);
						districtCriteria.setPmDistrict(district);
						results.add(districtCriteria);
					}
				}
			}
		}
		return results;
	}

	/**
	 * Select zone tree node.
	 * 
	 * @param event
	 *            the event
	 */
	public void selectZoneTreeNode(NodeSelectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();
		List<TreeNode> childNodes = event.getTreeNode().getChildren();
		if(null!=childNodes && childNodes.size()>0){
			
		for (int i = 0; i < childNodes.size(); i++) {
			childNodes.get(i).setSelected(true);
			
		}
		}else{
			
		selectedTreeNode.setSelected(true);
		
		}
	}

	/**
	 * Un select zone tree node.
	 * 
	 * @param event
	 *            the event
	 */
	public void unSelectZoneTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();
		List<TreeNode> childNodes = event.getTreeNode().getChildren();
		if(null!=childNodes && childNodes.size()>0){
			
		for (int i = 0; i < childNodes.size(); i++) {
			childNodes.get(i).setSelected(false);
			
		}
		}else{
			
		selectedTreeNode.setSelected(false);
		}
	}

	/**
	 * delete selected zone form zone
	 */
	public void deleteZoneTreeNode() {
		getManagedBean().setSelectedFlag(true);
		List<TreeNode> rootTrees = getManagedBean().getZoneTrees();
		deleteTreeNodes(rootTrees);

		ScQuote scQuote = getManagedBean().getScQuote();

		// zone details
		scQuote.setQuoteZone(getQuoteZones(rootTrees));
		scQuote.getQuoteZone().setModified(Boolean.TRUE);

		updateSpecMasterZone(scQuote);

		if (getManagedBean().isSelectedFlag()) {
			Locale locale = new FacesScopes().getUserLocale();
			throw new EpException(MessageConst.ERROR_760165,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.sc.simple.quote.delivery.location",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
		}

		this.getManagedBean().setZoneIndex(-1);
	}

	/**
	 * Select location tree node.
	 * 
	 * @param event
	 *            the event
	 */
	public void selectLocationTreeNode(NodeSelectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		List<TreeNode> childNodes = event.getTreeNode().getChildren();
		if(null!=childNodes && childNodes.size()>0){
			
		for (int i = 0; i < childNodes.size(); i++) {
			childNodes.get(i).setSelected(true);
			
		}
		}else{
			
		selectedTreeNode.setSelected(true);
		
		}
	}

	/**
	 * Un select location tree node.
	 * 
	 * @param event
	 *            the event
	 */
	public void unSelectLocationTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		List<TreeNode> childNodes = event.getTreeNode().getChildren();
		if(null!=childNodes && childNodes.size()>0){
			
		for (int i = 0; i < childNodes.size(); i++) {
			childNodes.get(i).setSelected(false);
			
		}
		}else{
			
		selectedTreeNode.setSelected(false);
		}
	}

	/**
	 * Delete tree nodes.
	 * 
	 * @param rootTrees
	 *            the root trees
	 * @return the list
	 */
	private List<TreeNode> deleteTreeNodes(List<TreeNode> rootTrees) {
		TplSpecZoneDtl tplSpecZoneDtl = null;
		List<TreeNode> removedList = new ArrayList<TreeNode>();
		if (rootTrees != null) {
			for (int row = 0; row < rootTrees.size(); row++) {
				TreeNode rootTree = rootTrees.get(row);

				for (TreeNode zoneTree : rootTree.getChildren()) {
					if (zoneTree.isSelected()) {
						getManagedBean().setSelectedFlag(false); // to check
																	// wheather
																	// zone
																	// selected
																	// or not
						tplSpecZoneDtl = (TplSpecZoneDtl) zoneTree.getData();
						if (tplSpecZoneDtl.getPanelZone() != null) {
							deletePanelItemSuppliers(tplSpecZoneDtl);

						} else {
							deleteStateOrDivOrDist(tplSpecZoneDtl, null);
						}

						rootTrees.remove(row--);
					} else {

						removedList = deleteChildTreeNodes(zoneTree
								.getChildren());

						if (removedList != null) {
							for (TreeNode treeNode : removedList) {
								List<TplSpecZoneLoc> locationDTOs = getLocations(treeNode);

								deleteStateOrDivOrDist(tplSpecZoneDtl,
										locationDTOs);
							}
						}

						if (zoneTree.getChildren().size() == 0) {
							rootTrees.remove(row--);
						}
					}

				}
			}
		}
		return removedList;
	}

	/**
	 * Delete child tree nodes.
	 * 
	 * @param treeNodes
	 *            the tree nodes
	 * @return the list
	 */
	private List<TreeNode> deleteChildTreeNodes(List<TreeNode> treeNodes) {
		List<TreeNode> nodesToRemoved = new ArrayList<TreeNode>();
		if (treeNodes != null) {

			for (TreeNode treeNode : treeNodes) {
				if (treeNode.isSelected()) {
					getManagedBean().setSelectedFlag(false); // to check
																// wheather
																// childs are
																// selected or
																// not
					nodesToRemoved.add(treeNode);
				} else {
					if (treeNode.getChildren() != null
							&& treeNode.getChildren().size() > 0) {
						deleteChildTreeNodes(treeNode.getChildren());

						if (treeNode.getChildren().size() == 0) {
							nodesToRemoved.add(treeNode);
						}
					}
				}
			}

			if (nodesToRemoved.size() > 0) {
				treeNodes.removeAll(nodesToRemoved);
			}
		}
		return nodesToRemoved;
	}

	/**
	 * Delete panel item suppliers.
	 * 
	 * @param tplSpecZoneDtl
	 *            the tpl spec zone dtl
	 */
	private void deletePanelItemSuppliers(TplSpecZoneDtl tplSpecZoneDtl) {
		List<ScQuoteSupplier> panelSuppliers = new ArrayList<ScQuoteSupplier>();
		// delete suppliers by selected zone to be delete
		if (tplSpecZoneDtl.getPanelZone() != null) {
			CtPanelZone panelZone = tplSpecZoneDtl.getPanelZone();
			if (getManagedBean().getPanelSuppliers() != null
					&& getManagedBean().getPanelSuppliers().size() > 0) {
				for (ScQuoteSupplier sqQuoteSupplier : getManagedBean()
						.getPanelSuppliers()) {
					if (sqQuoteSupplier.getTplSpecZoneDtl() != null
							&& sqQuoteSupplier.getTplSpecZoneDtl()
									.getPanelZone() != null) {
						if (!sqQuoteSupplier.getTplSpecZoneDtl().getPanelZone()
								.getPanelZoneId()
								.equals(panelZone.getPanelZoneId())) {
							panelSuppliers.add(sqQuoteSupplier);
						}
					}
				}
			}
		}
		// delete panel zones
		removePanelItemZones(tplSpecZoneDtl);
		if (panelSuppliers == null || panelSuppliers.isEmpty()) {
			this.getManagedBean().getScQuote().setScQuoteSuppliers(null);

		} else {
			getManagedBean().getScQuote().setScQuoteSuppliers(panelSuppliers);
		}
		getManagedBean().setPanelSuppliers(panelSuppliers);

	}

	/**
	 * Removes the panel item zones.
	 * 
	 * @param tplSpecZoneDtl
	 *            the tpl spec zone dtl
	 */
	private void removePanelItemZones(TplSpecZoneDtl tplSpecZoneDtl) {
		ScQuote quote = getManagedBean().getScQuote();
		if (tplSpecZoneDtl != null) {
			if (quote.getQuoteZone() != null
					&& quote.getQuoteZone().getTplSpecZoneDtls() != null
					&& quote.getQuoteZone().getTplSpecZoneDtls().size() > 0) {
				quote.getQuoteZone().getTplSpecZoneDtls()
						.remove(tplSpecZoneDtl);
				quote.getQuoteZone().setModified(Boolean.TRUE);
			}
		}
		// after delete zone update prod and service item list
		updateProdNServiceItemList(quote);
		getManagedBean().setScQuote(quote);
	}

	/**
	 * Update prod n service item list.
	 * 
	 * @param quote
	 *            the quote
	 */
	private void updateProdNServiceItemList(ScQuote quote) {

		if (getManagedBean().getProductTemplateSpecBean().getSpecMaster() != null) {
			getManagedBean().getProductTemplateSpecBean().getSpecMaster()
					.setTplSpecZone(quote.getQuoteZone());
		}

		if (getManagedBean().getServiceTemplateSpecBean().getSpecMaster() != null) {
			getManagedBean().getServiceTemplateSpecBean().getSpecMaster()
					.setTplSpecZone(quote.getQuoteZone());
		}

	}

	/**
	 * Select panel item zone.
	 * 
	 * @param event
	 *            the event
	 */
	public void selectPanelItemZone(NodeSelectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();
		List<TreeNode> childNodes = event.getTreeNode().getChildren();
		if(null!=childNodes && childNodes.size()>0){
			
		for (int i = 0; i < childNodes.size(); i++) {
			childNodes.get(i).setSelected(true);
			
		}
		}else{
			
		selectedTreeNode.setSelected(true);
		
		}
	}

	/**
	 * Un select panel item zone.
	 * 
	 * @param event
	 *            the event
	 */
	public void unSelectPanelItemZone(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();
		List<TreeNode> childNodes = event.getTreeNode().getChildren();
		if(null!=childNodes && childNodes.size()>0){
			
		for (int i = 0; i < childNodes.size(); i++) {
			childNodes.get(i).setSelected(false);
			
		}
		}else{
			
		selectedTreeNode.setSelected(false);
		}
	}

	/**
	 * To delete the selected TreeNode from the location.
	 */
	public void deleteLocationTreeNode() {
		List<TreeNode> rootTrees = getManagedBean().getLocationTrees();
		deleteLocTreeNodes(rootTrees);

		List<TplSpecZoneLoc> locationDTOs = new ArrayList<TplSpecZoneLoc>();
		if (rootTrees != null) {
			for (TreeNode treeNode : rootTrees) {
				if (treeNode.getChildren().size() > 0) {
					for (TreeNode mainNode : treeNode.getChildren()) {
						locationDTOs = getLocations(mainNode);
						locationDTOs.addAll(locationDTOs);
					}
				}
			}
		}
	}

	/**
	 * Delete loc tree nodes.
	 * 
	 * @param rootTrees
	 *            the root trees
	 */
	private void deleteLocTreeNodes(List<TreeNode> rootTrees) {
		if (rootTrees != null) {
			for (int row = 0; row < rootTrees.size(); row++) {
				TreeNode rootTree = rootTrees.get(row);

				for (TreeNode zoneTree : rootTree.getChildren()) {
					if (zoneTree.isSelected()) {
						rootTrees.remove(row--);
					} else {
						deleteChildTreeNodes(zoneTree.getChildren());

						if (zoneTree.getChildren().size() == 0) {
							// rootTrees.remove(row--);
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the price type drop down values.
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @param priceTypes
	 *            the price types
	 * @return the price type drop down values
	 */
	public SelectItem[] getPriceTypeDropdownValues(String parameterType,
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
	 * Page navigation to catalogue search page from simpleQuote.
	 */
	public void backToPrevious() {
		debugLogger("backToPrevious");
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setEditableFlag(Boolean.FALSE);
		if (getManagedBean().getPageNavToRequestNote() != null) {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							getManagedBean().getPageNavToRequestNote());
		} else {
			CatalogueBackingBean catalogueBackingBean = new CatalogueBackingBean();
			/*
			 * TemplateSpecBean productTplSpecBean = getManagedBean()
			 * .getProductTemplateSpecBean(); TemplateSpecBean
			 * serviceTplSpecBean = getManagedBean()
			 * .getServiceTemplateSpecBean();
			 * 
			 * if (getManagedBean().getScQuote().getPanelItem() != null) {
			 * catalogueBackingBean.getManagedBean().setSimpleQuoteFlag(false);
			 * } else {
			 * 
			 * if (productTplSpecBean.getSpecMaster() != null &&
			 * productTplSpecBean.getSpecMaster() .getTplSpecQuestions() != null
			 * && productTplSpecBean.getSpecMaster()
			 * .getTplSpecQuestions().size() > 0 ||
			 * serviceTplSpecBean.getSpecMaster() != null &&
			 * serviceTplSpecBean.getSpecMaster() .getTplSpecQuestions() != null
			 * && serviceTplSpecBean.getSpecMaster()
			 * .getTplSpecQuestions().size() > 0) {
			 * catalogueBackingBean.getManagedBean().setSimpleQuoteFlag(false);
			 * } else {
			 * catalogueBackingBean.getManagedBean().setSimpleQuoteFlag(true);
			 * } }
			 */
			
			catalogueBackingBean.getManagedBean().setSimpleQuoteFlag(Boolean.FALSE);

			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "catalogueSearch");
		}
	}

	/**
	 * Gets the current status to show in SQ.
	 * 
	 * @param pmStatus
	 *            the pm status
	 * @return the current status
	 */
	public void getCurrentStatus(PmStatus pmStatus) {
		getManagedBean().setSimpleQuoteStatus(pmStatus);
	}

	/**
	 * Gets the location level drop down.
	 * 
	 * @return the location level drop down
	 */
	public SelectItem[] getLocationLevelDropdown() {
		List<String> parameterCodes = new ArrayList<String>();
		parameterCodes.add(ParameterConst.LOCATION_LEVEL_STATE);
		parameterCodes.add(ParameterConst.LOCATION_LEVEL_DIVISION);
		parameterCodes.add(ParameterConst.LOCATION_LEVEL_DISTRICT);
		FacesScopes face = new FacesScopes();
		SelectItem[] items = null;
		try {
			items = new SelectItem[parameterCodes.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (String parameterCode : parameterCodes) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterCode()
							.equals(parameterCode)) {
						items[i++] = new SelectItem(paramDesc.getPmParameter()
								.getParameterCode(), paramDesc.getCodeName());
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

	/**
	 * Retrieve parameter desc by type.
	 * 
	 * @param type
	 *            the type
	 * @return the list
	 */
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

	/**
	 * Date listener. validate start date and end date in simple quote general
	 * information
	 */
	public void dateListener() {
		ScQuote quote = getManagedBean().getScQuote();

		// validate start date and end date
		validateStartDateAndEndDate(quote);

		// calculate duration
		calculateDuration(quote);
	}

	/**
	 * Validate start date and end date.
	 * 
	 * @param quote
	 *            the quote
	 */
	private void validateStartDateAndEndDate(ScQuote quote) {
		if (quote.getStartDate() != null && quote.getEndDate() != null) {
			if (quote.getEndDate().before(quote.getStartDate())
					|| quote.getStartDate().equals(quote.getEndDate())) {
				throw new EpException(MessageConst.ERROR_760164);
			}
		}
	}

	/**
	 * Calculate duration.
	 * 
	 * @param quote
	 *            the quote
	 */
	public void calculateDuration(ScQuote quote) {
		if (quote.getStartDate() != null && quote.getEndDate() != null) {
					getDaysAndHours(quote.getStartDate(), quote.getEndDate());
		}
	}
	
	
	/**
	 * Gets the days and hours.
	 *
	 * @param dateOne the date one
	 * @param dateTwo the date two
	 * @return the days and hours
	 */
	private  void getDaysAndHours(Date dateOne, Date dateTwo){
		long diff = dateTwo.getTime() - dateOne.getTime();
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		double time = (double)diffHours + (double)diffMinutes / 60;
        getManagedBean().setDays(diffDays);
        getManagedBean().setHours(time);
	}

	
	/**
	 * Submit prompt action. before submit simple quote, it shows the
	 * prompt(dialogue) that Total time Supplier responds for this Simple Quote
	 */
	public void submitPromptAction() {
		debugLogger("submitPromptAction");
		ScQuote quote = getManagedBean().getScQuote();
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		if (quote.getEndDate() != null && quote.getStartDate() != null) {
			Date currentDate = new Date();
			Date startDate = quote.getStartDate();
			Date endDate = quote.getEndDate();
			String message ="";

			if(getManagedBean().getPidExpiryDate() != null){
				/*Rule : during preparation of SQ upon select start and end date, error : 
				the end date not allowed to enter equal or more than PID approval validity period date & time. */
				Date pidExpDate = getManagedBean().getPidExpiryDate();
				//Calendar cal = Calendar.getInstance(); 
				//cal.setTime(pidExpDate);
				//cal.add(Calendar.DATE, -1); // - one day for pid exp date

				// End date should be before one day pid exp date
				//Date oneDayBeforePidExpDate =   cal.getTime();

				if(endDate.after(pidExpDate)){
					throw new EpException(MessageConst.ERROR_760387);
				} 
				
				String hours = getHours(currentDate, pidExpDate);
				String mins = getMinuts(currentDate, pidExpDate);

				String pidExpMessage = getMessage(
						"lbl.sc.quote.pid.approval.valid.duration", new String[] {hours, mins});

				getManagedBean().setPidExpiryMessage(pidExpMessage);
			}

			if (endDate.after(currentDate)) {
				String hours;
				String mins;
				if(startDate.after(currentDate)) {
					hours = getHours(startDate, endDate);
					mins = getMinuts(startDate, endDate);
					message = getMessage(
							"lbl.sc.quote.supplier.resp.duration", new String[] {hours, mins});
					getManagedBean().setConformDlgBtnFlag(Boolean.TRUE);
				} else {
					hours = getHours(currentDate, endDate);
					mins = getMinuts(currentDate, endDate);
					throw new EpException(MessageConst.ERROR_760428);
				}
			} else {
				message = getMessage(
						"lbl.sc.quote.enddate.cannot.be.lessthan.currentdate", null);
				getManagedBean().setConformDlgBtnFlag(Boolean.FALSE);
			}
			RequestContext.getCurrentInstance().execute("sqConformDlg.show()");
			getManagedBean().setPromptMessage(message.toString());

		} else if ( null == quote.getStartDate() ||  null == quote.getEndDate()) {
			throw new EpException(MessageConst.ERROR_760388);
		}
	}
	
	private String getHours(Date fromDate, Date toDate){
		long timeDiff = Math.abs(fromDate.getTime() - toDate.getTime());
		String hours = String.format("%d",
				TimeUnit.MILLISECONDS.toHours(timeDiff));
		
		return hours;
	}
	
	private String getMinuts(Date fromDate, Date toDate){
		long timeDiff = Math.abs(fromDate.getTime() - toDate.getTime());
		String mins = String
				.format("%d", (TimeUnit.MILLISECONDS
						.toMinutes(timeDiff) - TimeUnit.HOURS
						.toMinutes(TimeUnit.MILLISECONDS
								.toHours(timeDiff))));
		return mins;
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

	/**
	 * Tab menu listener. to view selected tab
	 * 
	 * @param activeIndex
	 *            the active index
	 */
	public void tabMenuListener(Integer activeIndex) {
		debugLogger("tabMenuListener");
		if (activeIndex.equals(0)) {
			getManagedBean().setActiveIndex(0);
		} else if (activeIndex.equals(1)) {
			getManagedBean().setActiveIndex(1);
		} else if (activeIndex.equals(2)) {
			getManagedBean().setActiveIndex(2);
		} else if (activeIndex.equals(3)) {
			getManagedBean().setActiveIndex(3);
		}
		procurementTypeEvent();
	}

	/**
	 * Ptj locality. show the ptj locality in supplier location in supplier
	 * criteria.
	 * 
	 * @param locationLevel
	 *            the location level
	 */
	public void ptjLocality(String locationLevel) {
		debugLogger("ptjLocality");
		List<TplSpecZoneLoc> tplSpecZoneLocs = new ArrayList<TplSpecZoneLoc>();
		// ptj locality address
		if (getManagedBean().getLocationTrees() == null
				|| getManagedBean().getLocationTrees().isEmpty()) {
			IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IAddressSO.class);
			AddressReqDTO reqDTO = new AddressReqDTO();
			if (getManagedBean().getRequestNote().getPmOrgProfile() != null) {
				reqDTO.setOrgProfileId(getManagedBean().getRequestNote()
						.getPmOrgProfile().getOrgProfileId());
				reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
				AddressResDTO resDTO = addressSO
						.retrieveAddressTypeList(reqDTO);
				List<PmAddressType> list = resDTO.getAddressTypeList();
				if (list != null && list.size() > 0) {
					PmAddressType addressType = list.get(0);
					List<PmState> states = new ArrayList<PmState>();
					List<PmDivision> divisions = new ArrayList<PmDivision>();
					List<PmDistrict> districts = new ArrayList<PmDistrict>();

					if (locationLevel
							.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DIVISION)) {
						TplSpecZoneLoc specZoneLoc = new TplSpecZoneLoc();
						if (addressType.getPmAddress().getPmDivision() != null) {
							divisions.add(addressType.getPmAddress()
									.getPmDivision());
							specZoneLoc.setPmDivision(addressType
									.getPmAddress().getPmDivision());
							tplSpecZoneLocs.add(specZoneLoc);
						}
					}

					if (locationLevel
							.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DISTRICT)) {
						TplSpecZoneLoc specZoneLoc = new TplSpecZoneLoc();
						if (addressType.getPmAddress().getPmDistrict() != null) {
							districts.add(addressType.getPmAddress()
									.getPmDistrict());
							specZoneLoc.setPmDistrict(addressType
									.getPmAddress().getPmDistrict());
							tplSpecZoneLocs.add(specZoneLoc);
						}
					}

					if (addressType.getPmAddress().getPmState() != null
							&& districts.isEmpty() && divisions.isEmpty()) {
						states.add(addressType.getPmAddress().getPmState());
						TplSpecZoneLoc specZoneLoc = new TplSpecZoneLoc();
						specZoneLoc.setPmState(addressType.getPmAddress()
								.getPmState());
						tplSpecZoneLocs.add(specZoneLoc);
					}

					getManagedBean().getLocationTrees().add(
							LocationSingleton.getInstance().buildLocationTree(
									null, states, districts, divisions, null,
									TplSpecZoneLoc.class));
				}
			}
		}
	}

	/**
	 * Reset clear the search criteria in simple quote
	 */
	public void reset() {
		this.getManagedBean().setSearchQuoteNo(null);
		this.getManagedBean().setSearchTitle(null);
		this.getManagedBean().setSearchProcType(null);
		this.getManagedBean().setSearchFromCreatedDate(null);
		this.getManagedBean().setSearchToCreatedDate(null);
		getManagedBean().setSearchStatus(null);
	}

	/**
	 * Adds the clone item spec. add clone item to simple quote to item list
	 * 
	 * @param productOrService
	 *            the product or service
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 */
	public void addCloneItemSpec(String productOrService)
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		log.info("addCloneItemSpec");
		TplSpecMaster cloneSpecMaster = null;
		TplSpecMaster prodSpecMaster = null;
		TplSpecMaster serviceSpecMaster = null;
		ITemplateSpecSO templateSpecSO = (ITemplateSpecSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
						ITemplateSpecSO.class);
		TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
		TemplateSpecBean productTplSpecBean = getManagedBean()
				.getProductTemplateSpecBean();
		TemplateSpecBean serviceTplSpecBean = getManagedBean()
				.getServiceTemplateSpecBean();
		if (productTplSpecBean.getSpecMaster() != null) {
			prodSpecMaster = productTplSpecBean.getSpecMaster();
		}
		if (serviceTplSpecBean.getSpecMaster() != null) {
			serviceSpecMaster = serviceTplSpecBean.getSpecMaster();
		}

		try {
			if (getManagedBean().getSelectedQuote() != null
					&& getManagedBean().getSelectedQuote().getQuoteDocs()
							.size() > 0) {
				getManagedBean().getSelectedQuote();
				QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
				quoteReqDTO.setQuoteId(getManagedBean().getSelectedQuote()
						.getQuoteId());
				QuoteResDTO quoteResDTO = getQuoteSO().getQuoteDocByQuoteId(
						quoteReqDTO);
				if (quoteResDTO.getQuoteDocs() != null
						&& quoteResDTO.getQuoteDocs().size() > 0) {
					List<ScQuoteDoc> quoteDocs = quoteResDTO.getQuoteDocs();
					for (ScQuoteDoc quoteDoc : quoteDocs) {
						// adding selected product or service question only
						if (quoteDoc.getSpecMaster().getItemTypeCode()
								.getParameterCode().equals(productOrService)) {
							reqDTO.setSpecMaster(quoteDoc.getSpecMaster());
						}
					}
				}

				reqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
				TemplateSpecResDTO resDTO = templateSpecSO
						.getTemplateMaster(reqDTO);
				cloneSpecMaster = resDTO.getSpecMaster();

				// product Tpl Spec master
				if (ParameterConst.SQ_ITEM_LIST_PRODUCT
						.equals(productOrService)) {
					List<TplSpecQuestion> questions = new ArrayList<TplSpecQuestion>();
					List<TplSpecQuestion> cloneQuestions = new ArrayList<TplSpecQuestion>();

					// non clone spec master
					if (productTplSpecBean.getSpecMaster() != null
							&& productTplSpecBean.getSpecMaster()
									.getTplSpecQuestions() != null) {
						getManagedBean().setProdQuestions(
								prodSpecMaster.getTplSpecQuestions());
					}

					if (getManagedBean().getProdQuestions() != null
							&& getManagedBean().getProdQuestions().size() > 0) {
						List<TplSpecQuestion> prodQuestions = getManagedBean()
								.getProdQuestions();
						questions.addAll(prodQuestions);
					}
					cloneQuestions = TemplateSpecHelper.getInstance()
							.cloneTplSpecQuestions(prodSpecMaster,
									cloneSpecMaster.getTplSpecQuestions());
					if (cloneQuestions != null && cloneQuestions.size() > 0) {
						questions.addAll(cloneQuestions);
					}

					prodSpecMaster.setTplSpecQuestions(questions);

					if (getManagedBean().getScQuote().getMultipleQuote()) { // for
																			// multiple
																			// quote
						prodSpecMaster.setEstCostReq(Boolean.TRUE);
					} else {
						prodSpecMaster.setEstCostReq(Boolean.FALSE);
					}

					productTplSpecBean.setSpecMaster(prodSpecMaster);
					getManagedBean().setProductTemplateSpecBean(
							productTplSpecBean);

					// service Tpl Spec master
				} else if (ParameterConst.SQ_ITEM_LIST_SERVICE
						.equals(productOrService)) {
					List<TplSpecQuestion> questions = new ArrayList<TplSpecQuestion>();
					List<TplSpecQuestion> cloneQuestions = new ArrayList<TplSpecQuestion>();

					// non clone spec master - already selected list
					if (serviceTplSpecBean.getSpecMaster() != null
							&& serviceTplSpecBean.getSpecMaster()
									.getTplSpecQuestions() != null) {
						getManagedBean().setServiceQuestions(
								serviceSpecMaster.getTplSpecQuestions());
					}

					if (getManagedBean().getServiceQuestions() != null
							&& getManagedBean().getServiceQuestions().size() > 0) {
						List<TplSpecQuestion> serviceQuestions = getManagedBean()
								.getServiceQuestions();
						questions.addAll(serviceQuestions);
					}
					cloneQuestions = TemplateSpecHelper.getInstance()
							.cloneTplSpecQuestions(serviceSpecMaster,
									cloneSpecMaster.getTplSpecQuestions());
					if (cloneQuestions != null && cloneQuestions.size() > 0) {
						questions.addAll(cloneQuestions);
					}
					serviceSpecMaster.setTplSpecQuestions(questions);

					if (getManagedBean().getScQuote().getMultipleQuote()) { // for
																			// multiple
																			// quote
						serviceSpecMaster.setEstCostReq(Boolean.TRUE);
					} else {
						serviceSpecMaster.setEstCostReq(Boolean.FALSE);
					}

					serviceTplSpecBean.setSpecMaster(serviceSpecMaster);
					getManagedBean().setServiceTemplateSpecBean(
							serviceTplSpecBean);
				}
			}
		} finally {
			templateSpecSO = null;
		}
		getManagedBean().setSelectedQuote(null);
	}

	/*
	 * private List<TplSpecQuestion> cloneTplSpecQuetions(TplSpecMaster
	 * cloneMaster ,TplSpecMaster tplSpecMaster) throws IllegalAccessException,
	 * InstantiationException, InvocationTargetException, NoSuchMethodException{
	 * List<TplSpecQuestion> questions = new ArrayList<TplSpecQuestion>(); for
	 * (TplSpecQuestion tplSpecQuestion : cloneMaster.getTplSpecQuestions()) {
	 * TplSpecQuestion question = (TplSpecQuestion)
	 * BeanUtils.cloneBean(tplSpecQuestion); question.setSpecQuestionId(null);
	 * question.setTplSpecAnswers(null); question.setTplSpecScorings(null);
	 * question.setTplSpecTierQties(null); question.setBrandNew(Boolean.TRUE);
	 * if(tplSpecMaster !=null){ question.setTplSpecMaster(tplSpecMaster); }
	 * else{ question.setTplSpecMaster(cloneMaster); }
	 * 
	 * if(question.getTplSpecEstimations() !=null &&
	 * question.getTplSpecEstimations().size() >0 ){
	 * question.setTplSpecEstimations(cloneTplSpecEstimations(question)); }
	 * questions.add(question); } return questions; }
	 */

	/*
	 * private List<TplSpecEstimation> cloneTplSpecEstimations(TplSpecQuestion
	 * question) throws IllegalAccessException, InstantiationException,
	 * InvocationTargetException, NoSuchMethodException{ List<TplSpecEstimation>
	 * specEstimations = new ArrayList<TplSpecEstimation>(); for
	 * (TplSpecEstimation specEstimation : question.getTplSpecEstimations()) {
	 * specEstimation = (TplSpecEstimation) BeanUtils.cloneBean(specEstimation);
	 * specEstimation.setSpecEstimationId(null);
	 * specEstimation.setEstimatedCost(null);
	 * specEstimation.setBrandNew(Boolean.TRUE);
	 * specEstimations.add(specEstimation); } return specEstimations; }
	 */

	/**
	 * Clone action. show the clone item popup to clone item to item list.
	 * 
	 * @param productOrService
	 *            the product or service
	 */
	public void cloneAction(String productOrService) {
		debugLogger("cloneAction - clone product or service item");
		getManagedBean().setSelectedQuote(null);
		if (productOrService != null) {
			if (productOrService.equals(ParameterConst.SQ_ITEM_LIST_PRODUCT)) {
				getManagedBean().setProductOrService(productOrService);

			} else if (productOrService
					.equals(ParameterConst.SQ_ITEM_LIST_SERVICE)) {
				getManagedBean().setProductOrService(productOrService);
			}
		}

		getManagedBean().searchCloneItemSpec();
		RequestContext.getCurrentInstance().execute("cloneItemSpec.show()");
	}

	/**
	 * Supplier list listener. populate supplers from selected item from
	 * catalogue or comapare page in supplier criteria.
	 * 
	 * @param suppliers
	 *            the suppliers
	 * @param quote
	 *            the quote
	 */
	public void supplierListListener(List<SmSupplier> suppliers, ScQuote quote) {
		debugLogger("supplierListListener");
		List<ScQuoteSupplier> supplierList = quote.getScQuoteSuppliers();
		if (supplierList == null) {
			supplierList = new ArrayList<ScQuoteSupplier>();
		}
		if (suppliers != null) {
			for (SmSupplier supplier : suppliers) {
				// after save supplier list, can't add new supplier if the suppler exists
				Map<Long, ScQuoteSupplier> deletedSupplierMap = getManagedBean().getDeletedSupplierMap();
				if(deletedSupplierMap != null && deletedSupplierMap.containsKey(supplier.getSupplierId())){
					supplierList.add(deletedSupplierMap.get(supplier.getSupplierId()));
					
				} else{
					ScQuoteSupplier quoteSupplier = new ScQuoteSupplier();
					quoteSupplier.setSmSupplier(supplier);
					quoteSupplier.setMofNumber(getMofNor(supplier.getSupplierId()));
					quoteSupplier.setSupplierName(supplier.getCompanyName());
					quoteSupplier.setQuote(quote);

					getManagedBean().setSupplierList(supplierList);
					if (getManagedBean().getSupplierList().isEmpty()) {
						supplierList.add(quoteSupplier);
						
					} else {
						List<Long> supplierIds = new ArrayList<Long>();
						for (ScQuoteSupplier scQuoteSupplier : getManagedBean()
								.getSupplierList()) {
							supplierIds.add(scQuoteSupplier.getSmSupplier()
									.getSupplierId());
						}
						if (supplierIds.size() > 0) {
							if (!supplierIds.contains(supplier.getSupplierId())) {
								supplierList.add(quoteSupplier);

							} else {
								// throw new EpException(MessageConst.ERROR_700027,
								// new String[]{"Supplier","in the Supplier List"});
							}
						}
					}

				}

			}
		}
	}

	/**
	 * Business nature listener. adding bussiness natures from lov popup in
	 * supplier criteria.
	 * 
	 * @param businessNatures
	 *            the business natures
	 */
	public void businessNatureListener(List<PmBusinessNature> businessNatures) {
		debugLogger("supplierListListener");
		List<ScBusinessNatureQuoteCriteria> natureQuoteCriterias = getManagedBean()
				.getScQuote().getBusinessNatureCriterias();
		if (natureQuoteCriterias == null) {
			natureQuoteCriterias = new ArrayList<ScBusinessNatureQuoteCriteria>();
		}
		if (businessNatures != null) {
			for (PmBusinessNature busiNature : businessNatures) {

				// after save business nature , can't add new business nature if the business nature exists
				Map<Long, ScBusinessNatureQuoteCriteria> deletedBusinessNatureMap = getManagedBean().getDeletedBusinessNatureMap();
				if(deletedBusinessNatureMap != null && deletedBusinessNatureMap.containsKey(busiNature.getBusinessNatureId())){
					natureQuoteCriterias.add(deletedBusinessNatureMap.get(busiNature.getBusinessNatureId()));
					
				} else{
					ScBusinessNatureQuoteCriteria natureQuoteCriteria = new ScBusinessNatureQuoteCriteria();
					natureQuoteCriteria.setPmBusinessNature(busiNature);
					natureQuoteCriteria.setQuote(getManagedBean().getScQuote());

					getManagedBean().setBusinessNatureCriterias(natureQuoteCriterias);
					if (getManagedBean().getBusinessNatureCriterias() == null
							|| getManagedBean().getBusinessNatureCriterias()
							.isEmpty()) {
						natureQuoteCriterias.add(natureQuoteCriteria);

					} else {
						List<Long> busNatureIds = new ArrayList<Long>();
						for (ScBusinessNatureQuoteCriteria bnCriteria : getManagedBean()
								.getBusinessNatureCriterias()) {
							busNatureIds.add(bnCriteria.getPmBusinessNature()
									.getBusinessNatureId());
						}
						if (busNatureIds.size() > 0) {
							if (!busNatureIds.contains(busiNature
									.getBusinessNatureId())) {
								natureQuoteCriterias.add(natureQuoteCriteria);
							} else {
								throw new EpException(
										MessageConst.ERROR_700027,
										new String[] { ResourceBundleUtils
												.getMessage(
														natureQuoteCriteria
														.getPmBusinessNature()
														.getLevelName(),
														ResourceBundleUtils.BUNDLE_COMMON_LABELS,
														new FacesScopes()
														.getUserLocale()) });
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Govt seller listener. adding govt sellers from lov popup in supplier
	 * criteria.
	 * 
	 * @param orgProfiles
	 *            the org profiles
	 */
	public void govtSellerListener(List<PmOrgProfile> orgProfiles) {
		debugLogger("govtSellerListener");
		List<ScQuoteSupplier> govtSuppliers = getManagedBean().getGovtSellers();
		if (govtSuppliers == null) {
			govtSuppliers = new ArrayList<ScQuoteSupplier>();
		}
		if (orgProfiles != null) {
			IQTSupplierSO qtSupplierSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSupplierSO.class);
			QTSupplierReqDTO reqDTO = new QTSupplierReqDTO();
			
			for (PmOrgProfile orgProfile : orgProfiles) {
				ScQuoteSupplier scQuoteSupplier = new ScQuoteSupplier();
				reqDTO.setSupplierId(orgProfile.getOrgProfileId());
				SmSupplier smSupplier = qtSupplierSO
						.retrieveGovSellerById(reqDTO).getSmSupplier();
				scQuoteSupplier.setSmSupplier(smSupplier);
				scQuoteSupplier.setQuote(getManagedBean().getScQuote());
				scQuoteSupplier.setMofNumber(getMofNor(smSupplier.getSupplierId()));
				scQuoteSupplier.setSupplierName(smSupplier.getCompanyName());
				
				getManagedBean().setGovtSellers(govtSuppliers);
				if(getManagedBean().getGovtSellers() == null || getManagedBean().getGovtSellers().isEmpty()){
					govtSuppliers.add(scQuoteSupplier);
				} else{
					List<Long> govtSellerIds = new ArrayList<Long>();
					for (ScQuoteSupplier govtSeller : getManagedBean().getGovtSellers() ) {
						govtSellerIds.add(govtSeller.getSmSupplier().getSupplierId());
					}
					
					if (govtSellerIds.size() > 0) {
						if(!govtSellerIds.contains(scQuoteSupplier.getSmSupplier().getSupplierId())){
							govtSuppliers.add(scQuoteSupplier);
							
						} /*else {
							throw new EpException(
									MessageConst.ERROR_700027,
									new String[] { ResourceBundleUtils
											.getMessage(
													scQuoteSupplier.getSmSupplier().getCompanyName(),
													ResourceBundleUtils.BUNDLE_COMMON_LABELS,
													new FacesScopes()
													.getUserLocale()) });
						}*/
					}
				}
			}
		}
	}

	/**
	 * Panel item listener. populate panel supplers from selected panel item
	 * from search catalogue in supplier criteria.
	 * 
	 * @param ctPanelSuppliers
	 *            the ct panel suppliers
	 * @param quote
	 *            the quote
	 */
	public void panelItemListener(List<CtPanelSupplier> ctPanelSuppliers,
			ScQuote quote) {
		debugLogger("panelItemListener");
		List<ScQuoteSupplier> panelSuppliers = quote.getScQuoteSuppliers();
		if (panelSuppliers == null) {
			panelSuppliers = new ArrayList<ScQuoteSupplier>();
		}
		if (ctPanelSuppliers != null) {
			for (CtPanelSupplier panelSupplier : ctPanelSuppliers) {
				ScQuoteSupplier scQuoteSupplier = new ScQuoteSupplier();
				SmSupplier smSupplier = panelSupplier.getSupplier();
				scQuoteSupplier.setSmSupplier(smSupplier);
				scQuoteSupplier.setQuote(quote);
				
				String mofNo = getMofNor(smSupplier.getSupplierId());
				scQuoteSupplier.setSupplierName(smSupplier.getCompanyName());
				scQuoteSupplier.setMofNumber(mofNo);
				
				getManagedBean().setPanelSuppliers(panelSuppliers);
				if (panelSuppliers.isEmpty()) {
					panelSuppliers.add(scQuoteSupplier);
				} else {
					List<Long> supplierIds = new ArrayList<Long>();
					for (ScQuoteSupplier quoteSupplier : getManagedBean()
							.getPanelSuppliers()) {
						supplierIds.add(quoteSupplier.getSmSupplier()
								.getSupplierId());
					}
					if (supplierIds.size() > 0) {
						if (!supplierIds.contains(scQuoteSupplier
								.getSmSupplier().getSupplierId())) {
							panelSuppliers.add(scQuoteSupplier);
							
						} else {
							// throw new EpException(MessageConst.ERROR_700027,
							// new String[]{"Supplier","in panel Supplier"});
						}
					}
				}
			}
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
	 * Gets the supplier list criteria. filter supplier list based on supplier
	 * location and businesss nature.
	 * 
	 * @return the supplier list criteria
	 */
	public String getSupplierListCriteria() {
		StringBuilder criteria = new StringBuilder();
		StringBuilder busiNatureCriteria = new StringBuilder();
		StringBuilder supplierCriteria = new StringBuilder();
		StringBuilder stateCriteria = new StringBuilder();
		StringBuilder divCriteria = new StringBuilder();
		StringBuilder distCriteria = new StringBuilder();

		// business nature criteria
		if (getManagedBean().isByBusinessNature()) {
			// Business Nature Criteria
			if (null != getManagedBean().getBusinessNatureCriterias()) {
				for (ScBusinessNatureQuoteCriteria natureQuoteCriteria : getManagedBean()
						.getBusinessNatureCriterias()) {
					if (natureQuoteCriteria.getPmBusinessNature() != null) {
						busiNatureCriteria.append(
								natureQuoteCriteria.getPmBusinessNature()
										.getBusinessNatureId()).append(",");
					}
				}
			}

			if (busiNatureCriteria != null && busiNatureCriteria.length() > 0) {
				busiNatureCriteria.deleteCharAt(busiNatureCriteria
						.lastIndexOf(","));
				busiNatureCriteria.append("^").append(
						SearchOperator.OPERATOR_IN.value());
				criteria.append(
						"smSupplierBusinesses.pmBusinessNature.businessNatureId")
						.append("^").append(busiNatureCriteria.toString())
						.append("|");
			}
		}

		// By locality criteria
		if (getManagedBean().isByLocality()) {
			// Locality Criteria
			List<TplSpecZoneLoc> locationDTOs = new ArrayList<TplSpecZoneLoc>();
			if (getManagedBean().getLocationTrees() != null) {
				for (TreeNode treeNode : getManagedBean().getLocationTrees()) {
					List<TplSpecZoneLoc> locationDTO = getLocations(treeNode);
					locationDTOs.addAll(locationDTO);
				}
			}
			List<PmState> states = SourcingLocationHelper
					.getStateFromLocationDTO(locationDTOs);
			List<PmDivision> divisions = SourcingLocationHelper
					.getDivisionFromLocationDTO(locationDTOs);
			List<PmDistrict> districts = SourcingLocationHelper
					.getDistrictFromLocationDTO(locationDTOs);

			if (states != null && states.size() > 0) {
				for (PmState state : states) {
					stateCriteria.append(state.getStateId()).append(",");
				}
			}

			if (divisions != null && divisions.size() > 0) {
				for (PmDivision division : divisions) {
					divCriteria.append(division.getDivisionId()).append(",");
				}
			}

			if (districts != null && districts.size() > 0) {
				for (PmDistrict district : districts) {
					distCriteria.append(district.getDistrictId()).append(",");
				}
			}

			if (stateCriteria != null && stateCriteria.length() > 0) {
				stateCriteria.deleteCharAt(stateCriteria.lastIndexOf(","));
				stateCriteria.append("^").append(
						SearchOperator.OPERATOR_IN.value());
				criteria.append(
						"smAppl.smCompanyBasic.smCompanyAddresses.smAddress.pmState.stateId")
						.append("^").append(stateCriteria.toString())
						.append("|");
			}

			if (divCriteria != null && divCriteria.length() > 0) {
				divCriteria.deleteCharAt(divCriteria.lastIndexOf(","));
				divCriteria.append("^").append(
						SearchOperator.OPERATOR_IN.value());
			} else {
				divCriteria.append("");
			}
			if (distCriteria != null && distCriteria.length() > 0) {
				distCriteria.deleteCharAt(distCriteria.lastIndexOf(","));
				distCriteria.append("^").append(
						SearchOperator.OPERATOR_IN.value());
				criteria.append(
						"smAppl.smCompanyBasic.smCompanyAddresses.smAddress.pmDistrict.districtId")
						.append("^").append(distCriteria.toString())
						.append("|");
			}
		}

		// selected suppliers criteria
		if (getManagedBean().getSupplierList() != null
				&& getManagedBean().getSupplierList().size() > 0) {
			for (ScQuoteSupplier scQuoteSupplier : getManagedBean()
					.getSupplierList()) {
				supplierCriteria.append(
						scQuoteSupplier.getSmSupplier().getSupplierId())
						.append(",");
			}
		}

		// add in rejected Supplier Id
		if (getManagedBean().getScQuote().getRejectedSupplierList() != null
				&& getManagedBean().getScQuote().getRejectedSupplierList()
						.size() > 0) {
			for (Long rejectedSupplierId : getManagedBean().getScQuote()
					.getRejectedSupplierList()) {
				supplierCriteria.append(rejectedSupplierId).append(",");
			}
		}

		if (supplierCriteria.length() > 0) {
			supplierCriteria.deleteCharAt(supplierCriteria.lastIndexOf(","));
			supplierCriteria.append("^").append(
					SearchOperator.OPERATOR_NOT_IN.value());
			criteria.append("supplierId").append("^")
					.append(supplierCriteria.toString()).append("|");
		}

		criteria.append("recordStatus^1");
		return criteria.toString();
	}
	
	/**
	 * Gets the govt seller criteria.
	 *
	 * @return the govt seller criteria
	 */
	public String getGovtSellerCriteria() {
		StringBuilder criteria = new StringBuilder();
		StringBuilder supplierCriteria = new StringBuilder();

		// selected govt seller criteria
		if (getManagedBean().getGovtSellers() != null
				&& getManagedBean().getGovtSellers().size() > 0) {
			for (ScQuoteSupplier scQuoteSupplier : getManagedBean()
					.getGovtSellers()) {
				supplierCriteria.append(
						scQuoteSupplier.getSmSupplier().getSupplierId())
						.append(",");
			}
		}

		if (supplierCriteria.length() > 0) {
			supplierCriteria.deleteCharAt(supplierCriteria.lastIndexOf(","));
			supplierCriteria.append("^").append(
					SearchOperator.OPERATOR_NOT_IN.value());
			criteria.append("smSupplier.supplierId").append("^")
					.append(supplierCriteria.toString()).append("|");
		} 
		criteria.append("recordStatus^1")
		.append("|")
		.append("govtSeller^1");
		return criteria.toString();
	}


	/**
	 * render the view based on the task id page navigation to simple quote
	 */
	public void preRenderView() {
		QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
		SCDirectPurchaseDataType directPurchaseDataType = super.getBpmTaskDetails(SCDirectPurchaseDataType.class, null);

		// pid expiry date
		if(directPurchaseDataType.getPidExpiryDate() != null){
			getManagedBean().setPidExpiryDate(directPurchaseDataType.getPidExpiryDate().getTime());	
			getManagedBean().setTaskExpiryDate(directPurchaseDataType.getPidExpiryDate());
			TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
			templateSpecBackingBean.getManagedBean().setPidSQ(Boolean.TRUE);
		} else{
			TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
			templateSpecBackingBean.getManagedBean().setPidSQ(Boolean.FALSE);
		}
		
		if (directPurchaseDataType.getPidId() != null) {
			getPurchaseInDefaultExpiredMessage(getManagedBean().getCurrentStatusId());
		}
		if (directPurchaseDataType.getPidApprover() != null) {
			getManagedBean().setPidApprovers(directPurchaseDataType.getPidApprover());
		}

		Long quoteId = Long.valueOf(directPurchaseDataType.getDpId());
		quoteReqDTO.setQuoteId(quoteId);
		quoteReqDTO.setFetchSubEntity(true);
		QuoteResDTO quoteResDTO = getQuoteSO().getQuoteDtl((quoteReqDTO));
		ScQuote scQuote = quoteResDTO.getQuoteBidDtl();
		getManagedBean().setScQuote(scQuote);

		preRenderScQuote(scQuote);

		if (scQuote.getProcurementType() != null) {
			getManagedBean()
			.setProcurementTypes(
					getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE));
		}

		if (scQuote.getRequestNotes() != null
				&& scQuote.getRequestNotes().size() > 0) {
			getManagedBean().getRequestNote().setPmOrgProfile(
					scQuote.getRequestNotes().get(0).getPmOrgProfile());
		}

		// zone location level by default - district
		if (scQuote.getZoneLocatonLevel() != null) {
			getManagedBean().setLocationLevel(
					scQuote.getZoneLocatonLevel().getParameterCode());
		}

		// procurement  type category
		if(null == scQuote.getProcurementTypeCategory()){
			scQuote.setProcurementTypeCategory(new PmParameter());
		}

		// supplier location level by default - district
		if (scQuote.getSupplierLocationLevel() != null) {
			getManagedBean().setSupplierLocLevel(
					scQuote.getSupplierLocationLevel().getParameterCode());
		}

		// business nature details
		if (scQuote.getBusinessNatureCriterias() != null
				&& scQuote.getBusinessNatureCriterias().size() > 0) {
			getManagedBean().setByBusinessNature(Boolean.TRUE);
			getManagedBean().setBusinessNatureCriterias(
					scQuote.getBusinessNatureCriterias());
		}

		// re quote 
		if(scQuote.getParentQuoteId() != null){
			getManagedBean().setPanelItemFlag(Boolean.FALSE);
			getManagedBean().setNewableFlag(Boolean.FALSE);
			getManagedBean().setRemovableFlag(Boolean.FALSE);
		}
		if (null == scQuote.getStartDate()) {
			Date startDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.clear(); 
			cal.setTime(startDate);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			startDate = cal.getTime();
			scQuote.setStartDate(startDate);
		}

		// page navigation to simple quote
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, SIMPLE_QUOTE_PAGE_NAV);
	}

	/**
	 * render the supplier view based on the task id from notification.
	 */
	public void supplierView() {
		TplSpecMaster serviceMaster = null;
		TplSpecMaster productMaster = null;
		HttpServletRequest servletRequest = new FacesScopes()
				.getOriginalServletRequest();
		String quoteIdStr = servletRequest
				.getParameter(SourcingConstants.NAV_PARAM_SIMPLE_QUOTE_ID);
		TemplateSpecBean productTplSpecBean = getManagedBean()
				.getProductTemplateSpecBean();
		TemplateSpecBean serviceTplSpecBean = getManagedBean()
				.getServiceTemplateSpecBean();

		if (StringUtils.isNotBlank(quoteIdStr)) {

			try {
				FacesScopes facesScopes = new FacesScopes();
				QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
				Long quoteId = Long.valueOf(quoteIdStr);
				quoteReqDTO.setQuoteId(quoteId);
				quoteReqDTO.setUserId(facesScopes.getUserSessionBean()
						.getUserId());
				quoteReqDTO.setFetchSubEntity(true);
				QuoteResDTO quoteResDTO = getQuoteSO().getQuoteDtlForSupplier(
						quoteReqDTO);
				ScQuote scQuote = quoteResDTO.getQuoteBidDtl();

				// clear session data.
				renewSesionObject();

				if (scQuote.getQuoteDocs() != null
						&& scQuote.getQuoteDocs().size() > 0) {
					for (ScQuoteDoc quoteDoc : scQuote.getQuoteDocs()) {
						// adding selected product or service question only
						if (quoteDoc.getSpecMaster().getItemTypeCode()
								.getParameterCode()
								.equals(ParameterConst.SQ_ITEM_LIST_PRODUCT)) {
							productMaster = quoteDoc.getSpecMaster();
						} else if (quoteDoc.getSpecMaster().getItemTypeCode()
								.getParameterCode()
								.equals(ParameterConst.SQ_ITEM_LIST_SERVICE)) {
							serviceMaster = quoteDoc.getSpecMaster();
						}
					}
				}

				List<TplSpecAnswer> specAnswers = new ArrayList<TplSpecAnswer>();
				// service and product
				if (quoteResDTO.getProductAnswersTpl() != null
						&& quoteResDTO.getProductAnswersTpl().size() > 0) {
					List<TplSpecAnswer> productAnswersTpl = quoteResDTO
							.getProductAnswersTpl();
					specAnswers.addAll(productAnswersTpl);

					productTplSpecBean.setSpecAnswerList(productAnswersTpl);
					productTplSpecBean.setSpecMaster(productMaster);
					productTplSpecBean
							.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);
					getManagedBean().setProductTemplateSpecBean(
							productTplSpecBean);
				}

				if (quoteResDTO.getServiceAnswersTpl() != null
						&& quoteResDTO.getServiceAnswersTpl().size() > 0) {
					List<TplSpecAnswer> serviceAnswersTpl = quoteResDTO
							.getServiceAnswersTpl();
					specAnswers.addAll(serviceAnswersTpl);

					serviceTplSpecBean.setSpecAnswerList(serviceAnswersTpl);
					serviceTplSpecBean.setSpecMaster(serviceMaster);
					serviceTplSpecBean
							.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);
					getManagedBean().setServiceTemplateSpecBean(
							serviceTplSpecBean);
				}

				if (quoteResDTO.getScQuoteSupplier() != null) {
					ScQuoteSupplier sQuoteSupplier = quoteResDTO
							.getScQuoteSupplier();

					if (scQuote.getMultipleQuote()) { // for multiple quote
						getManagedBean().setSubmitButtonFlg(Boolean.TRUE);
						if (sQuoteSupplier.isSubmitted()) {
							getManagedBean().setSaveButtonFlg(Boolean.FALSE);
						} else {
							getManagedBean().setSaveButtonFlg(Boolean.TRUE);
						}
					} else { // for single quote
						if (sQuoteSupplier.isSubmitted()) {
							getManagedBean().setSaveButtonFlg(Boolean.FALSE);
							getManagedBean().setSubmitButtonFlg(Boolean.FALSE);
						} else {
							getManagedBean().setSaveButtonFlg(Boolean.TRUE);
							getManagedBean().setSubmitButtonFlg(Boolean.TRUE);
						}
					}
					getManagedBean().setScQuoteSupplier(sQuoteSupplier);
				}

				getManagedBean().setScQuote(scQuote);
				quoteReqDTO.setQuoteBidDtl(scQuote);
				quoteReqDTO.setSpecAnswers(specAnswers);
				quoteReqDTO.setQuoteSupplier(quoteResDTO.getScQuoteSupplier());
				if (null != quoteResDTO
						&& null != quoteResDTO.getScQuoteSupplier()
						&& null != quoteResDTO.getScQuoteSupplier()
								.getSmSupplier()) {
					retriveSimpleQuoteSupplierGstInfo(quoteResDTO
							.getScQuoteSupplier().getSmSupplier());
				}

				// remarks
				scQuote.setRemarks(initializeRemarks(scQuote,
						scQuote.getCurrentStatus(), scQuote.getRemarks(),
						ScSQRemark.class));

				getQuoteSO().validateSupplierResponse(quoteReqDTO);

			} catch (EpException e) {
				String message = e.getMessage();
				if (MessageConst.ERROR_760390.equals(message)) {
					Locale userLocale = new FacesScopes().getUserLocale();
					message = MessageUtils.getMessage(e.getMessage(),
							e.getParameters(),
							MessageUtils.BUNDLE_COMMON_MESSAGES, userLocale);

					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									message));
					// hide the save & submit button when Simple quote is not start yet
					// in supplier view
					getManagedBean().setSubmitButtonFlg(Boolean.FALSE);
					getManagedBean().setSaveButtonFlg(Boolean.FALSE);
				} else if (MessageConst.ERROR_760034.equals(message)) {
					Locale userLocale = new FacesScopes().getUserLocale();
					message = MessageUtils.getMessage(e.getMessage(),
							e.getParameters(),
							MessageUtils.BUNDLE_COMMON_MESSAGES, userLocale);

					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									message));
					// hide the save & submit button when Simple quote is closed
					// in supplier view
					getManagedBean().setSubmitButtonFlg(Boolean.FALSE);
					getManagedBean().setSaveButtonFlg(Boolean.FALSE);
				} else if (MessageConst.ERROR_760033.equals(message)) {
					// hide the save & submit button when Quote has been
					// submitted previously. You are not allowed to submit twice
					// in supplier view
					Locale userLocale = new FacesScopes().getUserLocale();
					message = MessageUtils.getMessage(e.getMessage(),
							e.getParameters(),
							MessageUtils.BUNDLE_COMMON_MESSAGES, userLocale);

					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									message));
					getManagedBean().setSubmitButtonFlg(Boolean.FALSE);
					getManagedBean().setSaveButtonFlg(Boolean.FALSE);

				} else {
					handleException(e);
				}

			} catch (Exception e) {
				handleException(e);
			} finally {
				// quoteSO = null;
			}

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "supplerView");
		}
	}

	/**
	 * Gets the quote zones.
	 * 
	 * @param treeNodes
	 *            the tree nodes
	 * @return the quote zones
	 */
	private ScQuoteZone getQuoteZones(List<TreeNode> treeNodes) {
		TplSpecZoneDtl tplSpecZoneDtl = null;
		ScQuoteZone scQuoteZone = getManagedBean().getScQuote().getQuoteZone();
		if (scQuoteZone != null) {
			List<TplSpecZoneLoc> results = new ArrayList<TplSpecZoneLoc>();
			List<TplSpecZoneDtl> tplSpecDtls = new ArrayList<TplSpecZoneDtl>();

			for (TreeNode rootNode : treeNodes) {
				results = new ArrayList<TplSpecZoneLoc>();

				for (TreeNode ZoneNode : rootNode.getChildren()) {

					for (TreeNode stateNode : ZoneNode.getChildren()) {

						if (null != stateNode.getChildren()
								&& !stateNode.getChildren().isEmpty()) {
							for (TreeNode devisionNode : stateNode
									.getChildren()) {

								if (devisionNode.getChildCount() > 0) {
									for (TreeNode districtNode : devisionNode
											.getChildren()) {
										TplSpecZoneLoc tplSpecZoneLoc = (TplSpecZoneLoc) districtNode
												.getData();
										tplSpecZoneDtl = tplSpecZoneLoc
												.getTplSpecZoneDtl();
										results.add(tplSpecZoneLoc);
										tplSpecZoneDtl
												.setTplSpecZoneLocs(results);
									}

								} else {
									TplSpecZoneLoc tplSpecZoneLoc = (TplSpecZoneLoc) devisionNode
											.getData();
									tplSpecZoneDtl = tplSpecZoneLoc
											.getTplSpecZoneDtl();
									results.add(tplSpecZoneLoc);
									tplSpecZoneDtl.setTplSpecZoneLocs(results);
								}
							}

						} else {
							TplSpecZoneLoc tplSpecZoneLoc = (TplSpecZoneLoc) stateNode
									.getData();
							tplSpecZoneDtl = tplSpecZoneLoc.getTplSpecZoneDtl();
							results.add(tplSpecZoneLoc);
							tplSpecZoneDtl.setTplSpecZoneLocs(results);

						}
					}
				}
				if (tplSpecZoneDtl != null) {
					tplSpecZoneDtl.setTplSpecZoneLocs(results);
					tplSpecDtls.add(tplSpecZoneDtl);
					// scQuoteZone.setTplSpecZoneDtls(tplSpecDtls);
				}
			}
			scQuoteZone.setTplSpecZoneDtls(tplSpecDtls);
		}
		return scQuoteZone;
	}

	/**
	 * save supplier response details.
	 * 
	 * save - save data into database supplier response details
	 */
	public String supplierSave() {
		debugLogger("supplierSave");
		try {
			FacesScopes facesScopes = new FacesScopes();
			QuoteReqDTO reqDTO = new QuoteReqDTO();
			ScQuote quote = getManagedBean().getScQuote();

			// tpl spec - product
			List<TplSpecAnswer> specAnswers = new ArrayList<TplSpecAnswer>();
			if (getManagedBean().getProductTemplateSpecBean()
					.getSpecAnswerList() != null) {
				specAnswers.addAll(getManagedBean()
						.getProductTemplateSpecBean().getSpecAnswerList());
			}

			// tpl spec - service
			if (getManagedBean().getServiceTemplateSpecBean()
					.getSpecAnswerList() != null) {
				specAnswers.addAll(getManagedBean()
						.getServiceTemplateSpecBean().getSpecAnswerList());
			}

			// update is submitted by supplier
			if (quote.getScQuoteSuppliers() != null
					&& quote.getScQuoteSuppliers().size() > 0) {
				for (ScQuoteSupplier scQuoteSupplier : quote
						.getScQuoteSuppliers()) {
					if (getManagedBean().getScQuoteSupplier() != null) {
						ScQuoteSupplier selectedQuoteSupplier = getManagedBean()
								.getScQuoteSupplier();
						if (selectedQuoteSupplier.getQuoteSupplierId().equals(
								scQuoteSupplier.getQuoteSupplierId())) {
							scQuoteSupplier.setSubmitted(Boolean.FALSE);
						}
					}
				}
			}

			// remarks
			finalizeRemarks(quote.getRemarks());
			reqDTO.setSpecAnswers(specAnswers);
			reqDTO.setQuoteBidDtl(quote);
			reqDTO.setRoleCode(getUserRoleCode());

			reqDTO.setPmUser(facesScopes.getUserSessionBean());
			reqDTO.setQuoteSupplier(getManagedBean().getScQuoteSupplier());
			reqDTO.setOutcome(BpmOutcomeConst.BPM_SAVE);
			QuoteResDTO quoteResDTO = getQuoteSO().saveSupplierResponse(reqDTO);
			if (quoteResDTO.isSuccessful()) {
				ScQuote scQuote = getManagedBean().getScQuote();

				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_700025,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_SIMPLE_QUOTE),
																scQuote.getQuoteNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));

				FacesContext fc = FacesContext.getCurrentInstance();
				retrieveSupplierView(scQuote);
				fc.getApplication().getNavigationHandler()
						.handleNavigation(fc, null, "supplerView");

			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
		} finally {
			// quoteSO = null;
		}
		return null;
	}

	/**
	 * submit supplier response details. submit - save data into database and
	 * BPM process will route to the next respective user. and also
	 * authenticating spki before submit price details
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
	 * Update supplier response. and also authenticating gpki and spki before
	 * submit price details
	 * 
	 * @param pmDigiSign
	 *            the pm digi sign
	 * @return the string
	 */
	public String updateSupplierResponse(PmDigiSign pmDigiSign) {
		debugLogger("updateSupplierResponse");
		try {
			FacesScopes facesScopes = new FacesScopes();
			QuoteReqDTO reqDTO = new QuoteReqDTO();
			ScQuote quote = getManagedBean().getScQuote();

			// tpl spec - product
			List<TplSpecAnswer> specAnswers = new ArrayList<TplSpecAnswer>();
			if (getManagedBean().getProductTemplateSpecBean()
					.getSpecAnswerList() != null) {
				specAnswers.addAll(getManagedBean()
						.getProductTemplateSpecBean().getSpecAnswerList());
			}

			// gpki
			if(isDigiSigningRequired(RoleCode.GOVT_SELLER.value())) {
				if (null != pmDigiSign) { 
					updateSpkiDigiSignData(pmDigiSign,
							ParameterConst.DOCTYPE_SIMPLE_QUOTE, quote.getQuoteId(),
							quote.getQuoteNo(), prepareOriginalText(Boolean.TRUE)); 
				}
				RequestContext.getCurrentInstance().execute("gpki_dlg.hide();");

			} else {
				// spki
				String originalText = prepareOriginalText(Boolean.FALSE); 
				if (null != pmDigiSign) {
					updateSpkiDigiSignData(pmDigiSign,
							ParameterConst.DOCTYPE_SIMPLE_QUOTE,
							quote.getQuoteId(), quote.getQuoteNo(),
							originalText);
				}
				
				byte[] signedData = getManagedBean().getSpkiBackingBean()
						.spkiSignTransaction(originalText);
				pmDigiSign.setSpkiSignedData(signedData);
				
				RequestContext.getCurrentInstance().execute("spki_dlg.hide();");
			}

			
			// hide blcok ui
			RequestContext.getCurrentInstance().execute("blockWidget.hide();");

			// tpl spec - service
			if (getManagedBean().getServiceTemplateSpecBean()
					.getSpecAnswerList() != null) {
				specAnswers.addAll(getManagedBean()
						.getServiceTemplateSpecBean().getSpecAnswerList());
			}

			// update is submitted by supplier
			if (quote.getScQuoteSuppliers() != null
					&& quote.getScQuoteSuppliers().size() > 0) {
				for (ScQuoteSupplier scQuoteSupplier : quote
						.getScQuoteSuppliers()) {
					if (getManagedBean().getScQuoteSupplier() != null) {
						ScQuoteSupplier selectedQuoteSupplier = getManagedBean()
								.getScQuoteSupplier();
						if (selectedQuoteSupplier.getQuoteSupplierId().equals(
								scQuoteSupplier.getQuoteSupplierId())) {
							scQuoteSupplier.setSubmitted(Boolean.TRUE);
						}
					}
				}
			}

			// remarks
			finalizeRemarks(quote.getRemarks());
			reqDTO.setSpecAnswers(specAnswers);
			reqDTO.setQuoteBidDtl(quote);
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPmDigiSign(pmDigiSign);

			reqDTO.setPmUser(facesScopes.getUserSessionBean());
			reqDTO.setQuoteSupplier(getManagedBean().getScQuoteSupplier());
			reqDTO.setOutcome(BpmOutcomeConst.BPM_SUBMIT);
			QuoteResDTO quoteResDTO = getQuoteSO().saveSupplierResponse(reqDTO);
			if (quoteResDTO.isSuccessful()) {
				ScQuote scQuote = getManagedBean().getScQuote();

				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_700026,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_SIMPLE_QUOTE),
																scQuote.getQuoteNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));

				FacesContext fc = FacesContext.getCurrentInstance();
				if (scQuote.getMultipleQuote().equals(Boolean.TRUE)) {
					retrieveSupplierView(scQuote);
					fc.getApplication().getNavigationHandler()
							.handleNavigation(fc, null, "supplerView");
				} else {
					fc.getApplication()
							.getNavigationHandler()
							.handleNavigation(fc, null,
									CommonUIConst.ACKNOWLEDGE_PAGE);
					// clear SQ data
					super.renewSesionObject();
				}

			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
		} finally {
			// quoteSO = null;
		}
		return null;
	}

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

		if (getManagedBean().getScQuoteSupplier() != null) {
			smSupplier = getManagedBean().getScQuoteSupplier().getSmSupplier();

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

		sb.append(getManagedBean().getScQuote().getQuoteNo()).append("|")
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
	 * retrieve Supplier details in the supplier View. gets simple quote details
	 * to view quote details after supplier submits.
	 * 
	 * @param scQuote
	 *            the sc quote
	 */
	public void retrieveSupplierView(ScQuote scQuote) {
		TemplateSpecBean productTplSpecBean = getManagedBean()
				.getProductTemplateSpecBean();
		TemplateSpecBean serviceTplSpecBean = getManagedBean()
				.getServiceTemplateSpecBean();

		FacesScopes facesScopes = new FacesScopes();
		QuoteReqDTO quoteReqDTO = new QuoteReqDTO();
		quoteReqDTO.setQuoteId(scQuote.getQuoteId());
		quoteReqDTO.setUserId(facesScopes.getUserSessionBean().getUserId());
		if (getManagedBean().getScQuoteSupplier() != null) {
			quoteReqDTO.setSupplierId(getManagedBean().getScQuoteSupplier()
					.getSmSupplier().getSupplierId());
		}
		quoteReqDTO.setFetchSubEntity(true);
		QuoteResDTO quoteResDTO = getQuoteSO().getQuoteDtlForSupplier(
				quoteReqDTO);
		scQuote = quoteResDTO.getQuoteBidDtl();

		// service and product
		if (quoteResDTO.getProductAnswersTpl() != null
				&& quoteResDTO.getProductAnswersTpl().size() > 0) {
			productTplSpecBean.setSpecAnswerList(quoteResDTO
					.getProductAnswersTpl());
			productTplSpecBean.setSpecMaster(scQuote.getQuoteDocs().get(0)
					.getSpecMaster());
			productTplSpecBean
					.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);
			getManagedBean().setProductTemplateSpecBean(productTplSpecBean);
		}

		if (quoteResDTO.getServiceAnswersTpl() != null
				&& quoteResDTO.getServiceAnswersTpl().size() > 0) {
			serviceTplSpecBean.setSpecAnswerList(quoteResDTO
					.getServiceAnswersTpl());
			serviceTplSpecBean.setSpecMaster(scQuote.getQuoteDocs().get(0)
					.getSpecMaster());
			serviceTplSpecBean
					.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);
			getManagedBean().setServiceTemplateSpecBean(serviceTplSpecBean);
		}

		if (quoteResDTO.getScQuoteSupplier() != null) {
			ScQuoteSupplier sQuoteSupplier = quoteResDTO.getScQuoteSupplier();

			if (scQuote.getMultipleQuote()) { // for multiple quote
				getManagedBean().setSubmitButtonFlg(Boolean.TRUE);
				if (sQuoteSupplier.isSubmitted()) {
					getManagedBean().setSaveButtonFlg(Boolean.FALSE);
				} else {
					getManagedBean().setSaveButtonFlg(Boolean.TRUE);
				}
			} else {
				if (sQuoteSupplier.isSubmitted()) {
					getManagedBean().setSaveButtonFlg(Boolean.FALSE);
					getManagedBean().setSubmitButtonFlg(Boolean.FALSE);
				} else {
					getManagedBean().setSaveButtonFlg(Boolean.TRUE);
					getManagedBean().setSubmitButtonFlg(Boolean.TRUE);
				}
			}
			getManagedBean().setScQuoteSupplier(sQuoteSupplier);
		}

		getManagedBean().setScQuote(scQuote);

		// remarks
		scQuote.setRemarks(initializeRemarks(scQuote,
				scQuote.getCurrentStatus(), scQuote.getRemarks(),
				ScSQRemark.class));
	}

	/**
	 * show the zone if zone is selected 'yes' in simple quote general info.
	 * 
	 * if not hide zone.
	 */
	public void zoneEvent() {
		ScQuoteZone quoteZone = null;
		ScQuote quote = getManagedBean().getScQuote();
		if (quote.isZonal()) {
			if (quote.getQuoteZone() == null) {
				quoteZone = new ScQuoteZone();
			} else {
				quoteZone = quote.getQuoteZone();
			}
			quoteZone.setScQuote(quote);
			quote.setQuoteZone(quoteZone);
			quote.setZonal(Boolean.TRUE);
			// zone location level by default - district
			if (getManagedBean().getLocationLevel() == null) {
				getManagedBean().setLocationLevel(
						ParameterConst.LOCATION_LEVEL_DISTRICT);
			}

		} else {
			quote.setZonal(Boolean.FALSE);
			quote.setQuoteZone(null);
			if (getManagedBean().getProductTemplateSpecBean().getSpecMaster() != null) {
				getManagedBean().getProductTemplateSpecBean().getSpecMaster()
						.setTplSpecZone(null);
			}
			if (getManagedBean().getServiceTemplateSpecBean().getSpecMaster() != null) {
				getManagedBean().getServiceTemplateSpecBean().getSpecMaster()
						.setTplSpecZone(null);
			}

			if (getManagedBean().getZoneTrees() != null) {
				this.getManagedBean().getZoneTrees().clear();
			}
			if (getManagedBean().getBeans() != null) {
				getManagedBean().getBeans().clear();
			}
			getManagedBean().setLocationLevel(null);
		}

		// product
		if (getManagedBean().getProductTemplateSpecBean().getSpecMaster() != null) {
			TemplateSpecHelper.getInstance().updateZoneDtl(
					getManagedBean().getProductTemplateSpecBean()
							.getSpecMaster(), quoteZone);
		}

		// service
		if (getManagedBean().getServiceTemplateSpecBean().getSpecMaster() != null) {
			TemplateSpecHelper.getInstance().updateZoneDtl(
					getManagedBean().getServiceTemplateSpecBean()
							.getSpecMaster(), quoteZone);
		}

		quote.setQuoteZone(quoteZone);
		this.getManagedBean().setScQuote(quote);
	}

	/**
	 * For the multiple quote, estimated price is show to key in in item list.
	 * 
	 * For the single quote, hide the estimated price in item list.
	 */
	public void noOfQuoteEvent() {
		ScQuote quote = getManagedBean().getScQuote();
		if (quote.getMultipleQuote()) { // for multiple quote

			// get Template spec backing bean and manage bean
			PortletSession portletSession = (PortletSession) FacesContext
					.getCurrentInstance().getExternalContext()
					.getSession(false);
			TemplateSpecBackingBean templateSpecBackingBean = (TemplateSpecBackingBean) portletSession
					.getAttribute("templateSpecBackingBean");
			if (templateSpecBackingBean == null) {
				templateSpecBackingBean = new TemplateSpecBackingBean();
			}
			TplSpecMaster serviceTplSpec = getManagedBean()
					.getServiceTemplateSpecBean().getSpecMaster();
			TplSpecMaster productTplSpec = getManagedBean()
					.getProductTemplateSpecBean().getSpecMaster();

			// product
			if (productTplSpec != null) {
				getManagedBean().getProductTemplateSpecBean().getSpecMaster()
						.setEstCostReq(Boolean.TRUE);
				for (TplSpecQuestion TplSpecQuestion : productTplSpec
						.getTplSpecQuestions()) {
					TemplateSpecHelper.getInstance().resetPriceTypeDtl(
							TplSpecQuestion);
				}
			}

			// service
			if (serviceTplSpec != null) {
				getManagedBean().getServiceTemplateSpecBean().getSpecMaster()
						.setEstCostReq(Boolean.TRUE);
				for (TplSpecQuestion TplSpecQuestion : serviceTplSpec
						.getTplSpecQuestions()) {
					TemplateSpecHelper.getInstance().resetPriceTypeDtl(
							TplSpecQuestion);
				}
			}
		} else {
			getManagedBean().getProductTemplateSpecBean().getSpecMaster()
					.setEstCostReq(Boolean.FALSE);
			getManagedBean().getServiceTemplateSpecBean().getSpecMaster()
					.setEstCostReq(Boolean.FALSE);
		}
	}

	/**
	 * Prepare re quote.
	 * 
	 * @param scQuote
	 *            the sc quote
	 */
	public void prepareReQuote(ScQuote scQuote) {
		getManagedBean().setScQuote(scQuote);
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);

		scQuote.setMultipleQuote(Boolean.FALSE);
		
		// Initializing Remarks
		scQuote.setRemarks(initializeRemarks(scQuote, null,
				scQuote.getRemarks(), ScSQRemark.class));
		
		// WorkFlow Status should be set In the save
		updateWorkFlowAndRemarks(scQuote);

		scQuote.setCreatedBy(new FacesScopes().getUserSessionBean().getUserId());
		getManagedBean().setLocLevelDropdown(getLocationLevelDropdown());
		// zone location level by default - district
		if (getManagedBean().getLocationLevel() == null) {
			getManagedBean().setLocationLevel(
					ParameterConst.LOCATION_LEVEL_DISTRICT);
		}
		// supplier location level by default - district
		if (getManagedBean().getSupplierLocLevel() == null) {
			getManagedBean().setSupplierLocLevel(
					ParameterConst.LOCATION_LEVEL_DISTRICT);
		}
		// to get ptj
		PmUser user = new FacesScopes().getUserSessionBean();
		if (user != null) {
			OrgProfileReqDTO profileReqDTO = new OrgProfileReqDTO();
			profileReqDTO.setUserId(user.getUserId());
			profileReqDTO.setOrgType(OrganizationType.DIVISION.value());
			OrgProfileResDTO orgProfileResDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(profileReqDTO);
			if (orgProfileResDTO.getOrgProfileList() != null
					&& orgProfileResDTO.getOrgProfileList().size() > 0) {
				getManagedBean().getRequestNote().setPmOrgProfile(
						orgProfileResDTO.getOrgProfileList().get(0));
			}
		}
		// to get country id
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);
		getManagedBean().setDefaultCountryId(
				resDTO.getPmCountry().getCountryId());

		// to get ptj locality by default
		ptjLocality(ParameterConst.LOCATION_LEVEL_DISTRICT);

		// procurement dropdown
		getManagedBean().setProcurementTypes(
				getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE));
		procurementTypeEvent();
	}

	/**
	 * Locality loc level event. when the location level changes, clear
	 * previously added locations and supplier locality remains.
	 * 
	 * @param locationLevel
	 *            the location level
	 */
	public void localityLocLevelEvent(String locationLevel) {
		if (getManagedBean().getLocationTrees() != null
				&& getManagedBean().getLocationTrees().size() > 0) {
			getManagedBean().getLocationTrees().clear();
		}
		if (locationLevel == null) {
			locationLevel = ParameterConst.LOCATION_LEVEL_DISTRICT;
		}
		ptjLocality(locationLevel);
	}

	/**
	 * Zone loc level event. when the delivery coverage changes, clear
	 * previously added zones.
	 */
	public void zoneLocLevelEvent() {
		getManagedBean().getZoneTrees().clear();
		getManagedBean().getBeans().clear();
		getManagedBean().setZoneIndex(-1);
	}

	/**
	 * cancel the simple quote and page navigation to search catalogue.
	 * 
	 * @return the string
	 */
	public String cancel() {
		log.info("cancel");
		// clear sq
		super.renewSesionObject();
		CatalogueBackingBean catalogueBackingBean = new CatalogueBackingBean();
		catalogueBackingBean.getManagedBean().setSimpleQuoteFlag(true);
		return "catalogueSearch";
	}

	/**
	 * Cancel zone pop up.
	 */
	public void cancelZonePopUp() {
		log.info("cancelZonePopUp");
		RequestContext.getCurrentInstance().execute("ZoneLocation.hide();");
		// zone required validation
		getManagedBean().setRequired(Boolean.FALSE);
	}

	/**
	 * Get parameter code select items by parameter type for radio buttons.
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
	 * Show created by in clone item specification in simple quote
	 * 
	 * @param createdBy
	 *            the created by
	 * @return the string
	 */
	public String showCreatedBy(Long createdBy) {
		String userName = "";
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		if (createdBy != null) {
			reqDTO.setUserId(createdBy);
			UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);

			if (resDTO.getPmUser() != null) {
				userName = resDTO.getPmUser().getUserName();
			}
		}
		return userName;
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
	 * On tab change.
	 * 
	 * @param event
	 *            the event
	 */
	public void onTabChange(TabChangeEvent event) {
		if (event.getTab().getTitle().equalsIgnoreCase("Supplier List")) {
			// call supplier criteria
			getSupplierListCriteria();
		}
	}

	/**
	 * Gets the pm state ids.
	 * 
	 * @param tplSpecZoneLocs
	 *            the tpl spec zone locs
	 * @return the pm state ids
	 */
	private List<Long> getPmStateIds(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> results = new ArrayList<Long>();

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			for (TplSpecZoneLoc TplSpecZoneLoc : tplSpecZoneLocs) {

				if (TplSpecZoneLoc.getPmState() != null) {
					results.add(TplSpecZoneLoc.getPmState().getStateId());
				}
			}
		}
		return results;
	}

	/**
	 * Gets the pm division ids.
	 * 
	 * @param tplSpecZoneLocs
	 *            the tpl spec zone locs
	 * @return the pm division ids
	 */
	private List<Long> getPmDivisionIds(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> results = new ArrayList<Long>();

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			for (TplSpecZoneLoc TplSpecZoneLoc : tplSpecZoneLocs) {

				if (TplSpecZoneLoc.getPmDivision() != null) {
					results.add(TplSpecZoneLoc.getPmDivision().getDivisionId());
				}
			}
		}
		return results;
	}

	/**
	 * Gets the pm districts ids.
	 * 
	 * @param tplSpecZoneLocs
	 *            the tpl spec zone locs
	 * @return the pm districts ids
	 */
	private List<Long> getPmDistrictsIds(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> results = new ArrayList<Long>();

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			for (TplSpecZoneLoc TplSpecZoneLoc : tplSpecZoneLocs) {

				if (TplSpecZoneLoc.getPmDistrict() != null) {
					results.add(TplSpecZoneLoc.getPmDistrict().getDistrictId());
				}
			}
		}
		return results;
	}

	/**
	 * Delete state or div or dist.
	 * 
	 * @param tplSpecZoneDtl
	 *            the tpl spec zone dtl
	 * @param tplSpecZoneLocs
	 *            the tpl spec zone locs
	 */
	private void deleteStateOrDivOrDist(TplSpecZoneDtl tplSpecZoneDtl,
			List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> stateIds = null;
		List<Long> divIds = null;
		List<Long> distIds = null;

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			stateIds = getPmStateIds(tplSpecZoneLocs);

			divIds = getPmDivisionIds(tplSpecZoneLocs);

			distIds = getPmDistrictsIds(tplSpecZoneLocs);
		}

		List<ZonesBean> results = new ArrayList<ZonesBean>();
		List<ZonesBean> list = getManagedBean().getBeans();
		if (list != null && list.size() > 0) {
			for (ZonesBean zonesBean : list) {
				ZonesBean bean = new ZonesBean();
				TplSpecZoneDtl zone = zonesBean.getTplSpecZoneDtl();

				if (zone != null) {

					if (tplSpecZoneDtl != null) {
						if (!zone.getZoneName().equalsIgnoreCase(
								tplSpecZoneDtl.getZoneName())) {
							bean = zonesBean;
							results.add(bean);
						}

					} else {
						if (zonesBean.getTplSpecZoneLocs() != null
								&& zonesBean.getTplSpecZoneLocs().size() > 0) {
							List<TplSpecZoneLoc> specZoneLocs = new ArrayList<TplSpecZoneLoc>();
							for (TplSpecZoneLoc TplSpecZoneLoc : zonesBean
									.getTplSpecZoneLocs()) {

								if (TplSpecZoneLoc.getPmState() != null) {

									if (stateIds != null && stateIds.size() > 0) {

										if (!stateIds.contains(TplSpecZoneLoc
												.getPmState().getStateId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}

								} else if (TplSpecZoneLoc.getPmDivision() != null) {
									if (divIds != null && divIds.size() > 0) {
										if (!divIds.contains(TplSpecZoneLoc
												.getPmDivision()
												.getDivisionId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}
								} else if (TplSpecZoneLoc.getPmDistrict() != null) {
									if (distIds != null && distIds.size() > 0) {
										if (!distIds.contains(TplSpecZoneLoc
												.getPmDistrict()
												.getDistrictId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}
								}
							}
							bean.setTplSpecZoneLocs(specZoneLocs);
						}
						results.add(bean);
					}
				}
			}
			getManagedBean().setBeans(results);
		}
	}

	/**
	 * Check zone exists or not.
	 * 
	 * @param tplSpecZoneDtl
	 *            the tpl spec zone dtl
	 * @return the list
	 */
	private List<Long> checkZoneExistsOrNot(TplSpecZoneDtl tplSpecZoneDtl) {
		List<Long> results = new ArrayList<Long>();
		List<ZonesBean> list = getManagedBean().getBeans();
		if (list != null && list.size() > 0) {

			for (ZonesBean zonesBean : list) {
				TplSpecZoneDtl zone = zonesBean.getTplSpecZoneDtl();

				if (zone != null) {

					if (zone.getZoneName().equals(tplSpecZoneDtl.getZoneName())) {
						throw new EpException(
								MessageConst.ERROR_700027,
								new String[] { ResourceBundleUtils.getMessage(
										tplSpecZoneDtl.getZoneName(),
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale()) });
					}
				}
			}
		}
		return results;
	}

	/**
	 * Check duplicate state or div or dist.
	 * 
	 * @param tplSpecZoneDtl
	 *            the tpl spec zone dtl
	 */
	private void checkDuplicateStateOrDivOrDist(TplSpecZoneDtl tplSpecZoneDtl) {
		List<Long> stateIds = null;
		List<Long> divIds = null;
		List<Long> distIds = null;

		if (tplSpecZoneDtl.getTplSpecZoneLocs() != null
				&& tplSpecZoneDtl.getTplSpecZoneLocs().size() > 0) {
			List<TplSpecZoneLoc> currentTplSpecLocs = tplSpecZoneDtl
					.getTplSpecZoneLocs();
			stateIds = getPmStateIds(currentTplSpecLocs);
			divIds = getPmDivisionIds(currentTplSpecLocs);
			distIds = getPmDistrictsIds(currentTplSpecLocs);
		}

		List<ZonesBean> list = getManagedBean().getBeans();

		if (list != null && list.size() > 0) {

			for (ZonesBean zonesBean : list) {
				TplSpecZoneDtl prevsZone = zonesBean.getTplSpecZoneDtl();

				if (prevsZone != null) {

					if (prevsZone.getTplSpecZoneLocs() != null
							&& prevsZone.getTplSpecZoneLocs().size() > 0) {
						List<TplSpecZoneLoc> previousTplSpecLocs = prevsZone
								.getTplSpecZoneLocs();
						for (TplSpecZoneLoc TplSpecZoneLoc : previousTplSpecLocs) {

							if (TplSpecZoneLoc.getPmState() != null) {

								if (stateIds != null && stateIds.size() > 0) {

									if (stateIds.contains(TplSpecZoneLoc
											.getPmState().getStateId())) {
										throw new EpException(
												MessageConst.ERROR_760168,
												new String[] {
														TplSpecZoneLoc
																.getPmState()
																.getStateName(),
														prevsZone.getZoneName() });
									}
								}

							}

							if (TplSpecZoneLoc.getPmDivision() != null) {
								if (divIds != null && divIds.size() > 0) {
									if (divIds.contains(TplSpecZoneLoc
											.getPmDivision().getDivisionId())) {
										throw new EpException(
												MessageConst.ERROR_760167,
												new String[] {
														TplSpecZoneLoc
																.getPmDivision()
																.getDivisionName(),
														prevsZone.getZoneName() });
									}
								}
							}

							if (TplSpecZoneLoc.getPmDistrict() != null) {
								if (distIds != null && distIds.size() > 0) {
									if (distIds.contains(TplSpecZoneLoc
											.getPmDistrict().getDistrictId())) {
										throw new EpException(
												MessageConst.ERROR_760166,
												new String[] {
														TplSpecZoneLoc
																.getPmDistrict()
																.getDistrictName(),
														prevsZone.getZoneName() });
									}
								}
							}
						}
					}
					if (prevsZone.getZoneName().equals(
							tplSpecZoneDtl.getZoneName())) {
						throw new EpException(
								MessageConst.ERROR_700027,
								new String[] { ResourceBundleUtils.getMessage(
										tplSpecZoneDtl.getZoneName(),
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale()) });
					}
				}
			}
		}
	}

	/**
	 * Constuct tree node with disable childs.
	 * 
	 * @param tplSpecZoneDtl
	 *            the tpl spec zone dtl
	 * @param specLocs
	 *            the spec locs
	 * @return the tree node
	 */
	private TreeNode constuctTreeNodeWithDisableChilds(
			TplSpecZoneDtl tplSpecZoneDtl, List<TplSpecZoneLoc> specLocs) {
		TreeNode root = new DefaultTreeNode(QTConstants.NODE_ROOT, null);
		if (tplSpecZoneDtl != null) {
			TreeNode zoneNode = new DefaultTreeNode(QTConstants.NODE_ZONE,
					tplSpecZoneDtl, root);
			List<PmState> states = new ArrayList<PmState>();
			List<PmDivision> divisions = new ArrayList<PmDivision>();
			List<PmDistrict> districts = new ArrayList<PmDistrict>();
			TreeNode stateNode = null;
			TreeNode divisonNode = null;
			for (TplSpecLoc locCriteria : specLocs) {
				PmState pmState = locCriteria.getPmState();

				if (null != pmState && !states.contains(pmState)) {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							locCriteria, zoneNode);
					stateNode.setSelectable(false);
				}
				PmDivision division = locCriteria.getPmDivision();

				if (null != division && !divisions.contains(pmState)) {
					divisonNode = new DefaultTreeNode(
							QTConstants.NODE_DIVISION, locCriteria, stateNode);
					divisonNode.setSelectable(false);
					divisions.add(division);
				}
				PmDistrict pmDistrict = locCriteria.getPmDistrict();

				if (null != pmDistrict && !districts.contains(pmDistrict)) {
					if (divisonNode == null) {
						new DefaultTreeNode(QTConstants.NODE_DISTRICT,
								locCriteria, stateNode).setSelectable(false);

					} else {
						new DefaultTreeNode(QTConstants.NODE_DISTRICT,
								locCriteria, divisonNode).setSelectable(false);

					}
					districts.add(pmDistrict);
				}
				states.add(pmState);
			}
		} else {

		}
		return root;
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
	 * Gets the text before sign. prepare sign in data for gpki authentication.
	 * 
	 * @return the text before sign
	 */
	public String getTextBeforeSign() {
		StringBuilder sb = new StringBuilder();
		if (getManagedBean().getScQuote() != null) {
			sb.append(getManagedBean().getScQuote().getQuoteNo());
		}
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		textBeforeSign = sb.toString();
		return textBeforeSign;
	}

	/**
	 * Retrive simple quote supplier gst info.
	 *
	 * @param supplier the supplier
	 */
	private void retriveSimpleQuoteSupplierGstInfo(SmSupplier supplier) {
		ISupplierGstSO supplierGstSo = null;
		try {
			if (null != supplier && null != supplier.getSupplierId()) {
				supplierGstSo = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
								ISupplierGstSO.class);
				SupplierGstReqDTO supplierGstreqDTO = new SupplierGstReqDTO();
				supplierGstreqDTO.setSupplierId(supplier.getSupplierId());
				supplierGstreqDTO.setBranchCode(null);
				SupplierGstResDTO supplierGstresDTO = supplierGstSo
						.retrieveGstInfoBySupplierIdAndBranchCode(supplierGstreqDTO);
				if (null != supplierGstresDTO
						&& null != supplierGstresDTO.getSmGstSupplier()) {
					this.getManagedBean().setSmGstSupplier(
							supplierGstresDTO.getSmGstSupplier());

				} else {
					this.getManagedBean().setSmGstSupplier(new SmGstSupplier());
				}

			} else {
				this.getManagedBean().setSmGstSupplier(new SmGstSupplier());
			}

		} finally {
			supplierGstSo = null;
		}
	}
	
	private void preRenderScQuote(ScQuote scQuote) {
		TemplateSpecBean productTplSpecBean = getManagedBean()
				.getProductTemplateSpecBean();
		TemplateSpecBean serviceTplSpecBean = getManagedBean()
				.getServiceTemplateSpecBean();

		if (scQuote.getJustificationType() == null) {
			scQuote.setJustificationType(new PmReason());
		}

		// service and product
		if (scQuote.getQuoteDocs() != null) {
			for (ScQuoteDoc scQuoteDoc : scQuote.getQuoteDocs()) {
				TplSpecMaster specMaster = scQuoteDoc.getSpecMaster();
				if (ParameterConst.SQ_ITEM_LIST_PRODUCT
						.equalsIgnoreCase(specMaster.getItemTypeCode()
								.getParameterCode())) {
					if (specMaster.getTplSpecQuestions() == null) {
						specMaster
						.setTplSpecQuestions(new ArrayList<TplSpecQuestion>());
					}
					productTplSpecBean.setSpecMaster(specMaster);
					getManagedBean().setProductTemplateSpecBean(
							productTplSpecBean);
					// service Tpl Spe
				} else if (ParameterConst.SQ_ITEM_LIST_SERVICE
						.equalsIgnoreCase(specMaster.getItemTypeCode()
								.getParameterCode())) {
					TplSpecMaster serviceMaster = scQuoteDoc
							.getSpecMaster();
					if (serviceMaster.getTplSpecQuestions() == null) {
						serviceMaster
						.setTplSpecQuestions(new ArrayList<TplSpecQuestion>());
					}
					serviceTplSpecBean.setSpecMaster(serviceMaster);
					getManagedBean().setServiceTemplateSpecBean(
							serviceTplSpecBean);
				}
			}
		}

		// zone details
		if (scQuote.getQuoteZone() != null) {
			List<TreeNode> zoneTrees = new ArrayList<TreeNode>();
			List<ZonesBean> beans = getManagedBean().getBeans();

			if (scQuote.getQuoteZone().getTplSpecZoneDtls() != null
					&& scQuote.getQuoteZone().getTplSpecZoneDtls().size() > 0) {
				for (TplSpecZoneDtl tplSpecZoneDtl : scQuote.getQuoteZone()
						.getTplSpecZoneDtls()) {
					ZonesBean zonesBean = new ZonesBean();
					if (tplSpecZoneDtl.getTplSpecZoneLocs() != null
							&& tplSpecZoneDtl.getTplSpecZoneLocs().size() > 0) {
						zonesBean.setTplSpecZoneDtl(tplSpecZoneDtl);
						beans.add(zonesBean);
						if (tplSpecZoneDtl.getPanelZone() != null) {
							zoneTrees
							.add(constuctTreeNodeWithDisableChilds(
									tplSpecZoneDtl, tplSpecZoneDtl
									.getTplSpecZoneLocs()));
						} else {
							zoneTrees.add(LocationSingleton.getInstance()
									.constuctTreeNode(
											tplSpecZoneDtl,
											tplSpecZoneDtl
											.getTplSpecZoneLocs()));
						}
					}
				}
				getManagedBean().setZoneTrees(zoneTrees);
				// to check duplicate state or division or district in
				// multiple zones
				getManagedBean().setBeans(beans);
			}
		}

		// by locality
		List<PmDivision> divisions = new ArrayList<PmDivision>();
		if (scQuote.getLocationDivisionCriterias() != null) {
			if (scQuote.getLocationDivisionCriterias().size() > 0) {
				for (ScLocationDivisionCriteria locationDivisionCriteria : scQuote
						.getLocationDivisionCriterias()) {
					divisions.add(locationDivisionCriteria.getPmDivision());
				}
			}
		}
		List<PmDistrict> districts = new ArrayList<PmDistrict>();
		if (scQuote.getLocationDistrictCriterias() != null) {
			if (scQuote.getLocationDistrictCriterias().size() > 0) {
				for (ScLocationDistrictCriteria locationDistrictCriteria : scQuote
						.getLocationDistrictCriterias()) {
					districts.add(locationDistrictCriteria.getPmDistrict());
				}
			}
		}

		List<PmState> states = new ArrayList<PmState>();
		if (districts.isEmpty() && divisions.isEmpty()) {
			if (scQuote.getLocationStateCriterias() != null) {
				if (scQuote.getLocationStateCriterias().size() > 0) {
					for (ScLocationStateCriteria locationStateCriteria : scQuote
							.getLocationStateCriterias()) {
						states.add(locationStateCriteria.getPmState());
					}
				}
			}
		}

		// division
		String locLovel = "";
		if (scQuote.getSupplierLocationLevel() != null) {
			if (scQuote
					.getSupplierLocationLevel()
					.getParameterCode()
					.equalsIgnoreCase(
							ParameterConst.LOCATION_LEVEL_DIVISION)) {
				locLovel = ParameterConst.LOCATION_LEVEL_DIVISION;
				if (scQuote.getLocationStateCriterias() != null) {
					if (scQuote.getLocationStateCriterias().size() > 0) {
						for (ScLocationStateCriteria locationStateCriteria : scQuote
								.getLocationStateCriterias()) {

							if (locationStateCriteria.getPmState() != null) {
								PmState state = locationStateCriteria
										.getPmState();
								if (!(state.getStateName()
										.equalsIgnoreCase("SARAWAK") || state
										.getStateName().equalsIgnoreCase(
												"SABAH"))) {
									states.add(locationStateCriteria
											.getPmState());
								}
							}
						}
					}
				}
			}
		}
		if (states.size() > 0 || divisions.size() > 0
				|| districts.size() > 0) {
			getManagedBean().setByLocality(Boolean.TRUE);
			List<TreeNode> LocTrees = new ArrayList<TreeNode>();
			LocTrees.add(LocationSingleton.getInstance()
					.constuctLocationTree(locLovel, states, districts,
							divisions, null, TplSpecZoneLoc.class));
			getManagedBean()
			.setLocationTrees(LocTrees.get(0).getChildren());
		} else {
			getManagedBean().setByLocality(Boolean.FALSE);
		}


		if (scQuote.getProcurementType() != null) {
			getManagedBean()
			.setProcurementTypes(
					getRadioButtonCodeValues(ParameterTypeConst.PROCUREMENT_TYPE));
		}

		if (scQuote.getRequestNotes() != null
				&& scQuote.getRequestNotes().size() > 0) {
			getManagedBean().getRequestNote().setPmOrgProfile(
					scQuote.getRequestNotes().get(0).getPmOrgProfile());
		}

		// zone location level by default - district
		if (scQuote.getZoneLocatonLevel() != null) {
			getManagedBean().setLocationLevel(
					scQuote.getZoneLocatonLevel().getParameterCode());
		}

		// procurement  type category
		if(null == scQuote.getProcurementTypeCategory()){
			scQuote.setProcurementTypeCategory(new PmParameter());
		}

		// supplier location level by default - district
		if (scQuote.getSupplierLocationLevel() != null) {
			getManagedBean().setSupplierLocLevel(
					scQuote.getSupplierLocationLevel().getParameterCode());
		}

		// business nature details
		if (scQuote.getBusinessNatureCriterias() != null
				&& scQuote.getBusinessNatureCriterias().size() > 0) {
			getManagedBean().setByBusinessNature(Boolean.TRUE);
			getManagedBean().setBusinessNatureCriterias(
					scQuote.getBusinessNatureCriterias());
		}

		CtPanelItem quotePanelItem = null;

		if(null == scQuote.getPanelItem()){
			// panel general item
			quotePanelItem = getTopPanelItem();
		} else{
			quotePanelItem = scQuote.getPanelItem();
		}
		if (scQuote.getScQuoteSuppliers() != null
				&& scQuote.getScQuoteSuppliers().size() > 0) {
			// panel supplier
			if (scQuote.isPanel()) {
				getManagedBean().setPanelSuppliers(
						scQuote.getScQuoteSuppliers());

			} else {
				if (scQuote.getProcurementType() != null
						&& scQuote
						.getProcurementType()
						.getParameterCode()
						.equalsIgnoreCase(
								ParameterConst.PROC_TYPE_G2G)) {
					// govt supplers
					getManagedBean().setGovtSellers(
							scQuote.getScQuoteSuppliers());

				} else { // supplier list
					getManagedBean().setSupplierList(
							scQuote.getScQuoteSuppliers());
				}
			}
		}
		// country id
		CountryReqDTO countryReqDTO = new CountryReqDTO();
		countryReqDTO.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(countryReqDTO);
		getManagedBean().setDefaultCountryId(
				resDTO.getPmCountry().getCountryId());

		if (quotePanelItem != null) {
			if (quotePanelItem.getItemCategoryId().getParameterCode()
					.equals(ParameterConst.SQ_PANEL_ITEM_GENERAL)) {
				// panel item general
				getManagedBean().setNewableFlag(Boolean.TRUE);
			} else if (quotePanelItem.getItemCategoryId().getParameterCode()
					.equals(ParameterConst.SQ_PANEL_ITEM_SPECIFIC)) {
				// panel item specific
				getManagedBean().setPanelItemFlag(Boolean.FALSE);
				getManagedBean().setNewableFlag(Boolean.FALSE);
			}
		}
		// duration calc
		calculateDuration(scQuote);
		procurementTypeEvent();

		
		Long currentStatusId = getManagedBean().getCurrentStatusId();
		if(currentStatusId != null) {
			getCurrentStatus(getPmStatusByStatusId(currentStatusId));
		}

		// remarks
		scQuote.setRemarks(initializeRemarks(scQuote,
				scQuote.getCurrentStatus(), scQuote.getRemarks(),
				ScSQRemark.class));

		getManagedBean().setTitleChecker(Boolean.FALSE);
		getManagedBean().setEditableFlag(Boolean.TRUE);
	}

	/**
	 * Gets the status desc list.
	 *
	 * @return the status desc list
	 */
	public List<PmStatusDesc> getStatusDescList() {
		List<PmStatusDesc> statusDescs = getManagedBean().getStatusDescs();
		
		if (statusDescs == null || statusDescs.size() == 0) {
			try {
				
				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				
				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setModuleCode(ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase());
				reqDTO.setDocType(ParameterConst.DOCTYPE_SIMPLE_QUOTE);
				reqDTO.setLanguageCode(languageCode);

				StatusResDTO resDTO = getStatusSO().retrieveStatusDescList(reqDTO);
				if(resDTO.isSuccessful()) {
					statusDescs = resDTO.getStatusDescList();
					
					getManagedBean().setStatusDescs(statusDescs);
				}
			} finally {
				//statusSO = null;
			}	
		}
		return statusDescs;
	}

	/**
	 * Proc type categories.
	 * 
	 * @return the list
	 */
	public List<PmParameterDesc> getProcTypeCategories() {
		List<PmParameterDesc> procTypeCategories = ParameterHelper
				.getInstance().getParameterDescList(
						ParameterTypeConst.PROCUREMENT_TYPE_CATEGORY);
		return procTypeCategories;
	}

	/**
	 * param is SmGstSupplier.
	 * 
	 * @return supplier gst dates.
	 */
	public String retrieveSupplierGstDates(Date date) {
		String gstDate = "";
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
			if (null != date) {
				gstDate = dateFormat.format(date);
			}

		} finally {

		}
		return gstDate;
	}
	
	
	/**
	 * handleFileUpload event for file upload in pid.
	 * 
	 * @param event
	 *            the event
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		debugLogger("handleFileUpload");
		FacesScopes facesScopes = new FacesScopes();
		if (event.getFile() != null) {
			ScQuote scQuote = getManagedBean().getScQuote();
			
			if (scQuote.getQuoteNo() == null) {
				scQuote.setQuoteNo(getQuoteSO().generateQuoteNo());
			}
			
			ScQuoteAttachment attachment = scQuote.getScQuoteAttachment();
			if (null == attachment) {
				attachment = new ScQuoteAttachment();
				attachment.setScQuote(scQuote);
			}
			attachment.setDocNo(scQuote.getQuoteNo());
			attachment
					.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			attachment.setCreatedUser(facesScopes.getUserSessionBean());

			uploadAttachment(event.getFile(), attachment);
			scQuote.setScQuoteAttachment(attachment);
		}
	}

	/**
	 * show the attached icon based on attached type file
	 * 
	 * @param attachment
	 *            the attachment
	 * @return the string
	 */
	public String determineIcon(ScQuoteAttachment attachment) {
		debugLogger("determineIcon");
		String imageFileName = "icon_file_default.png";
		String ext = "";

		if (attachment != null) {
			String fileName = attachment.getFileName();

			if (fileName != null) {
				int mid = fileName.lastIndexOf(".");
				ext = fileName.substring(mid + 1, fileName.length());
			}

			if ("doc".equals(ext) || "docx".equals(ext)) {
				imageFileName = "icon_doc.png";
			} else if ("pdf".equals(ext)) {
				imageFileName = "icon_pdf.png";
			} else if ("xls".equals(ext) || "xlsx".equals(ext)
					|| "csv".equals(ext)) {
				imageFileName = "icon_exel.png";
			} else if ("jpg".equals(ext) || "bmp".equals(ext)
					|| "png".equals(ext) || "gif".equals(ext)) {
				imageFileName = "icon_image.png";
			} else if ("txt".equals(ext)) {
				imageFileName = "icon_txt.png";
			}
		}
		return imageFileName;
	}

	/**
	 * delete uploaded file in purchase in default
	 */
	public void deleteUploadFile() {
		debugLogger("deleteUploadFile");
		getManagedBean().getScQuote().setScQuoteAttachment(null);
	}
	

	/**
	 * Gets the purchase in default expired message.
	 *
	 * @param statusId the status id
	 * @return the purchase in default expired message
	 */
	private void getPurchaseInDefaultExpiredMessage(long statusId){
		if(DPStatusConstants.PID_EXPIRED.equals(statusId)){
			Locale locale = new FacesScopes().getUserLocale();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
							.getMessage(
									ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default.exp.msg",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale),
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
		}
	}
	
	
	/**
	 * Update work flow and remarks.
	 *
	 * @param scQuote the sc quote
	 */
	private void updateWorkFlowAndRemarks(ScQuote scQuote){
		// remarks and workflow status
		if(scQuote.isBrandNew()){
			ScSQWorkFlowStatus scWorkFlowStatus = null;
			List<ScSQWorkFlowStatus> workFlowStatuses =  new  ArrayList<ScSQWorkFlowStatus>();
			scWorkFlowStatus = new ScSQWorkFlowStatus();
			PmStatus currentStatus = getPmStatusByStatusId(DPStatusConstants.SQ_PENDING_SUBMISSION);
			getCurrentStatus(currentStatus);
			scWorkFlowStatus.setCurrent(Boolean.TRUE);
			scWorkFlowStatus.setPmStatus(currentStatus);
			scWorkFlowStatus.setQuote(scQuote);
			workFlowStatuses.add(scWorkFlowStatus);
			scQuote.setWorkflowStatuses(workFlowStatuses);
			
			// To avoid workflow status reset to null when got error during direct submit without save.
			List<ScSQRemark> remarks = scQuote.getRemarks();
			if (CollectionUtils.isNotEmpty(remarks)) {
				for (ScSQRemark remark : remarks) {
					remark.setScWorkFlowStatus(scWorkFlowStatus);
				}
			}
		}
	}

	/**
	 * Sets the text before sign.
	 * 
	 * @param textBeforeSign
	 *            the new text before sign
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	/**
	 * The method to used auto update of request.
	 */
	public void enableAutoUpdate() {
		RequestContext.getCurrentInstance().update("@form");
	}

	/**
	 * Gets the quote so.
	 * 
	 * @return the quote so
	 */
	private IQuoteSO getQuoteSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
	}

	/**
	 * Gets the status so.
	 * 
	 * @return the status so
	 */
	private IStatusSO getStatusSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
	}

	/**
	 * Gets the parameter so.
	 * 
	 * @return the parameter so
	 */
	private IParameterSO getParameterSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}
	
	
	/**
	 * based on treenode selection button enable/disable.
	 * 
	 * @param event
	 *            the event
	 */
	public boolean checkLocationDeleteButtonDisable(){
		List<TreeNode> list = getManagedBean().getLocationTrees();
		if (null != list) {
			for (TreeNode node : list) {
				for (TreeNode childNode : node.getChildren()) {
					if (childNode.getChildCount() > 0) {
						for (TreeNode state : childNode.getChildren()) {
							if(state.isSelected()){
								return false;
							}
                            for(TreeNode district : state.getChildren()){
								
								if(district.getChildCount()>0){
									for(TreeNode division : district.getChildren()){
										if(division.isSelected()){
											return false;
										}
										if(division.getChildCount()>0){
											for(TreeNode anotherDistrict : district.getChildren()){
												if(anotherDistrict.isSelected()){
													return false;
												}
										}
									}
								}
								}else{
								if(district.isSelected()){
									return false;
								}
								}
							}
							
					}
					}else{
					if(childNode.isSelected()){
						return false;
					}
					}
				}
			}
			
		} else {
			return true;
		}
		return true;
	}

	
	/**
	 * based on treenode selection button enable/disable.
	 * 
	 * @param event
	 *            the event
	 */
	 public boolean zoneLocationDeleteBtnDisable(){
		List<TreeNode> list = getManagedBean().getZoneTrees();
		
		if (CollectionUtils.isNotEmpty(list) ) {
			for (TreeNode node : list) {
				for (TreeNode childNode : node.getChildren()) {
					if (childNode.getChildCount() > 0) {
						
						for (TreeNode state : childNode.getChildren()) {
							
							if(state.isSelected()){
								return false;
							}
							for(TreeNode district : state.getChildren()){
								
								if(district.getChildCount()>0){
									for(TreeNode division : district.getChildren()){
										if(division.isSelected()){
											return false;
										}
										if(division.getChildCount()>0){
											for(TreeNode anotherDistrict : district.getChildren()){
												if(anotherDistrict.isSelected()){
													return false;
												}
										}
									}
								}
								}else{
								if(district.isSelected()){
									return false;
								}
								}
							}
						}
					}else{
					if(childNode.isSelected()){
						return false;
					}
					}
				}
			}
		} else {
			return true;
		}
	return true;
	 }
}
