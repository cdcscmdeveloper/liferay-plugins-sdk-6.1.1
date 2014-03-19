/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.directpurchase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.sc.directpurchase.IQuoteSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.sc.directpurchase.QuoteReqDTO;
import my.ep.dto.sc.directpurchase.QuoteResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
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
import my.ep.persistence.entity.sc.directpurchase.ScQuoteZone;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteZoneLocation;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScSQRemark;
import my.ep.persistence.entity.sc.directpurchase.ScSQWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierMofNoCriteria;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.OrganizationType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.directpurchase.simpleQuoteManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

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
		ScQuote scQuote = new ScQuote();
		initializeRemarks(scQuote);
		populateUserDivisionList();
	}
	
	public void searchSuppliers() {
		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		
		if(getManagedBean().getBusinessNatureCriterias()!= null){
			List<String> businessNatures = new ArrayList<String>();
			for (ScBusinessNatureQuoteCriteria supplierMofNoCriteria : getManagedBean().getBusinessNatureCriterias()) {
				 businessNatures.add(supplierMofNoCriteria.getBusinessNature().getLevelName());
				System.out.println("Business nature Level Name :"+ supplierMofNoCriteria.getBusinessNature().getLevelName());
			}
			reqDTO.setBusinessNatures(businessNatures);
		}
		
		if(getManagedBean().getSelectedSuppMofNoCriterias() != null){
			for (ScSupplierMofNoCriteria supplierMofNoCriteria : getManagedBean().getSelectedSuppMofNoCriterias()) {
				System.out.println("company Name :"+supplierMofNoCriteria.getSmSupplier().getCompanyName());
			}
		}
		
		if(getManagedBean().getLocationTrees() != null){
			List<Long> stateIds = new ArrayList<Long>(); List<Long> districtIds = new ArrayList<Long>();
			for (ScLocationStateCriteria stateCriteria : getLocationStateCriterias(getManagedBean().getLocationTrees())) {
				stateIds.add(Long.valueOf(stateCriteria.getCriteriaValue()));
			}
			reqDTO.setStateIds(stateIds);
			
			for (ScLocationDivisionCriteria divisionCriteria : getLocationDivisionCriterias(getManagedBean().getLocationTrees())) {
				districtIds.add(Long.valueOf(divisionCriteria.getCriteriaValue()));
			}
			//reqDTO.setStateIds(stateIds);
			
			for (ScLocationDistrictCriteria districtCriteria : getLocationDistrictCriterias(getManagedBean().getLocationTrees())) {
				districtIds.add(Long.valueOf(districtCriteria.getCriteriaValue()));
			}
			reqDTO.setDistrictIds(districtIds);
		}
		
		SupplierSearchResDTO resDTO = supplierSearchSO.retriveSupplierProfiles(reqDTO);
		System.out.println("result: " + resDTO.getResultBean());
		
		getManagedBean().setSmSuppliers(resDTO.getResultBean().getResultList());
		
		for(SmSupplier supplier : resDTO.getResultBean().getResultList()) {
			System.out.println("supplier: " + supplier.getCompanyName());
		}
	}
	
	/**
	 * pricingDetails for getting pricing details in simpleQuote
	 */
	public String simpleQuoteAction(CmSupplierItem cmSupplierItem) {
		debugLogger("simpleQuoteAction");
		ScQuote scQuote = new ScQuote();
		//ScRequestItem scRequestItem = new ScRequestItem();
		if (cmSupplierItem != null) {
			/*ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
			List<ScRequestSupplierItem> scRequestSupplierItems = new ArrayList<ScRequestSupplierItem>();
			this.getManagedBean().setSupplierItem(cmSupplierItem);
			requestSupplierItem.setPricing(cmSupplierItem.getCmPricings()
					.get(0));
			requestSupplierItem.setSmSupplier(cmSupplierItem.getSmSupplier());
			requestSupplierItem.setRequestNoteItem(scRequestItem);
			scRequestSupplierItems.add(requestSupplierItem);*/
			// WorkFlow Status
			List<ScSQWorkFlowStatus> wflowStatus = new ArrayList<ScSQWorkFlowStatus>();
			ScSQWorkFlowStatus workFlowStatus = new ScSQWorkFlowStatus(
					SourcingConstants.SQ_PENDING_SUBMISSION);
			workFlowStatus.setCurrent(true);
			workFlowStatus.setQuote(scQuote);
			wflowStatus.add(workFlowStatus);
			scQuote.setWorkflowStatuses(wflowStatus);
			scQuote.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			//scQuote.setScRequestListSupplierItems(scRequestSupplierItems);
			// Initializing Remarks
			scQuote.setRemarks(initializeRemarks(scQuote));
			IQuoteSO quoteSO = (IQuoteSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQuoteSO.class);
			scQuote.setQuoteNo(quoteSO.generateQuoteNo());
			getManagedBean().setScQuote(scQuote);

			if (cmSupplierItem.getCmPricings() != null
					&& cmSupplierItem.getCmPricings().size() > 0) {
				List<String> priceTypes = new ArrayList<String>();
				for (CmPricing princing : cmSupplierItem.getCmPricings()) {
					if (!priceTypes.contains(princing.getPricingType())) {
						priceTypes.add(princing.getPricingType());
					}
				}
			}
		}
		
		 getManagedBean().setLocLevelDropdown(getLocationLevelDropdown());
		// TODO default value to Procurement type and price type drop down listing
		getManagedBean().getScQuote().setProcurementType(
				ParameterConst.PROC_TYPE_NORMAL);

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
			ScRequestItem requestItem = new ScRequestItem();
			List<ScRequestItem> requestItems = new ArrayList<ScRequestItem>();
			requestItems.add(requestItem);
			ScRequestNote requestNote = new ScRequestNote();
			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
			requestNote.setRequestNoteNo(directPurchaseSO.generateRequestNoteNo());
			requestNote.setSourceMethod("SQ");
			requestNote.setPmOrgProfile(ptjNameEvent(getManagedBean().getOrgProfileId()));
			List<ScRequestNote> requestNotes = new ArrayList<ScRequestNote>();
			requestNotes.add(requestNote);
			quote.setRequestItems(requestItems);
			quote.setRequestNotes(requestNotes);
			quote.setCreatedDate(DateUtils.getCurrentDate());
			//zone
			quote.setScZones(getQuoteZones(getManagedBean().getZoneTrees(),quote));
			//by location
			quote.setLocationStateCriterias(getLocationStateCriterias(getManagedBean().getLocationTrees()));
			quote.setLocationDivisionCriterias(getLocationDivisionCriterias(getManagedBean().getLocationTrees()));
			quote.setLocationDistrictCriterias(getLocationDistrictCriterias(getManagedBean().getLocationTrees()));
			// by business nature
			quote.setBusinessNatureCriterias(getBusinessNatureCriterias(getManagedBean().getBusinessNatureCriterias()));
			// by supplier 
			quote.setSupplierMofNoCriterias(geSupplierCriterias(getManagedBean().getSupplierMofNoCriterias()));
			// by govt seller
			quote.setGovEPNoCriterias(getGovtSeller(getManagedBean().getGovEPNoCriterias()));
			reqDTO.setOrgProfileId(getManagedBean().getOrgProfileId());
			reqDTO.setQuoteBidDtl(quote);
			QuoteResDTO quoteResDTO = quoteSO.saveQuoteDtl(reqDTO);
			if (quoteResDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", ""));
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
	
	public void supplierCriteriaEvent() {
		debugLogger("supplierCriteriaEvent");
		 List<TabBean> CriteriaList = new ArrayList<TabBean>();
		for (String criteria : getManagedBean().getSelectedCriteriaList()) {
			if (criteria.equalsIgnoreCase("Locality")) {
				TabBean tabBean = new TabBean(1, criteria);
				CriteriaList.add(tabBean);
				getManagedBean().setActiveIndex(0);

			} else if (criteria.equalsIgnoreCase("Business Nature")) {
				TabBean tabBean = new TabBean(2, criteria);
				CriteriaList.add(tabBean);
				getManagedBean().setActiveIndex(1);

			} else if (criteria.equalsIgnoreCase("Supplier")) {
				TabBean tabBean = new TabBean(3,criteria);
				CriteriaList.add(tabBean);
				getManagedBean().setActiveIndex(2);
				
			}else if (criteria.equalsIgnoreCase("Government Seller")) {
				TabBean tabBean = new TabBean(4,criteria);
				CriteriaList.add(tabBean);
				getManagedBean().setActiveIndex(3);
			}
		}
		this.getManagedBean().setCriteriaTabs(CriteriaList);
	}

	public SelectItem[] getSupplierCriteriaList() {
		 SelectItem[] selectItems = null;
	  if(getManagedBean().getScQuote().getProcurementType() != null){
		 if (getManagedBean().getScQuote().getProcurementType()
					.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			    selectItems = new SelectItem[1];
				selectItems[0] = new SelectItem("Government Seller", "By Government Seller");
				return selectItems;
			} else {
				selectItems = new SelectItem[3];
				selectItems[0] = new SelectItem("Locality", "By Location");
				selectItems[1] = new SelectItem("Business Nature","By Business Nature");
				selectItems[2] = new SelectItem("Supplier", "By Supplier");
			}
	  }
	  return selectItems;
	}
	
	/**
	 * initializing remarks in simpleQuote
	 */
	private List<ScSQRemark> initializeRemarks(ScQuote scQuote) {
		List<ScSQRemark> results = new ArrayList<ScSQRemark>();
		FacesScopes facesScopes = new FacesScopes();
		if (results.size() == 0
				|| !compareUserName(results.get(0).getUser().getLoginId())) {
			ScSQRemark remark = new ScSQRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setUser(facesScopes.getUserSessionBean());
			if (getManagedBean().getRequestNote().getRemarks() != null
					&& getManagedBean().getRequestNote().getRemarks().size() > 0) {
				remark.setRemark(getManagedBean().getRequestNote().getRemarks()
						.get(0).getRemark());
			}
			PmStatus status = new PmStatus();
			status.setStatusId(SourcingConstants.SQ_PENDING_SUBMISSION);
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
		if (getManagedBean().getScQuote().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL)) {
			getManagedBean().setProcTypeOption(false);
			getManagedBean().setJustTypeOption(false);

		} else if (getManagedBean().getScQuote().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			getManagedBean().setProcTypeOption(false);
			getManagedBean().setJustTypeOption(false);

		} else if (getManagedBean().getScQuote().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			getManagedBean().setProcTypeOption(true);
			getManagedBean()
					.setJustDropdown(
							getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_ADHOC));

		} else if (getManagedBean().getScQuote().getProcurementType()
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
	
	/**
	 * Adding new row for BusinessNature in SQ
	 */
	public void addSupplier() {
		debugLogger("addSupplier");
		ScSupplierMofNoCriteria supplierMofNoCriteria = new ScSupplierMofNoCriteria();
		SmSupplier smSupplier = new SmSupplier();
		supplierMofNoCriteria.setSmSupplier(smSupplier);
		
		try {
			List<ScSupplierMofNoCriteria>	supplierMofNoCriterias = getManagedBean()
					.getScQuote().getSupplierMofNoCriterias();
			
			if (null == supplierMofNoCriterias) {
				supplierMofNoCriterias = new ArrayList<ScSupplierMofNoCriteria>();
			}
			supplierMofNoCriterias.add(supplierMofNoCriteria);
			this.getManagedBean().setSupplierMofNoCriterias(supplierMofNoCriterias);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}
	
	
	/**
	 * Adding new row for GovtSeller in SQ
	 */
	public void addGovtSeller() {
		debugLogger("addGovtSeller");
		ScGovEPNoCriteria  govEPNoCriteria = new ScGovEPNoCriteria();
		SmSupplier smSupplier = new SmSupplier();
		govEPNoCriteria.setSmSupplier(smSupplier);
		try {
			List<ScGovEPNoCriteria>	govEPNoCriterias = getManagedBean()
					.getScQuote().getGovEPNoCriterias();
			
			if (null == govEPNoCriterias) {
				govEPNoCriterias = new ArrayList<ScGovEPNoCriteria>();
			}
			govEPNoCriterias.add(govEPNoCriteria);
			this.getManagedBean().setGovEPNoCriterias(govEPNoCriterias);
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
	
	public void deleteSupplier() {
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
	}
	
	public void deleteGovtSeller() {
		debugLogger("deleteGovtSeller");
		if (getManagedBean().getSelectedGovEPNoCriterias() != null) {
			for (ScGovEPNoCriteria govEPNoCriteria : getManagedBean()
					.getSelectedGovEPNoCriterias()) {
				if(getManagedBean().getBusinessNatureCriterias().contains(govEPNoCriteria)){
					List<ScGovEPNoCriteria>  govEPNoCriterias = getManagedBean().getGovEPNoCriterias();
					govEPNoCriterias.remove(govEPNoCriteria);
					getManagedBean().setGovEPNoCriterias(govEPNoCriterias);
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
			locationLevel = "C";
		}
		getManagedBean().setRootZoneTree(
				LocationSingleton.getInstance().buildLocationZoneTree(1L, null,
						null, null, null, locationLevel));
	}
	
   /**
	 * showLocationPopup method for location popup in simple quote
    */
	public void showLocationPopup() {
		String locationLevel = this.getManagedBean().getLocationLevel();
		if (locationLevel == null) {
			locationLevel = "C";
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
				if (!treeNode.getParent().isSelected()) {
					List<LocationDTO> locationDTO = getLocations(treeNode);
					locationDTOs.addAll(locationDTO);
				}
			}
		}
		ScQuoteZone zone = new ScQuoteZone();
		zone.setZoneName(getManagedBean().getLocZoneName());
		getManagedBean().setZoneName(null);

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
					LocationSingleton.getInstance().buildZoneTree(zone, states,
							districts, divisions, cities));
		} else if (getManagedBean().getLocIndex() >= 0) {
			getManagedBean().getLocationTrees().remove(getManagedBean().getLocIndex());
			getManagedBean().getLocationTrees().add(
					getManagedBean().getLocIndex(),
					LocationSingleton.getInstance().buildZoneTree(zone, states,
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
						states, districts, divisions, cities, "C"));
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
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationZoneTree(1L,
						states, districts, divisions, cities, "C"));
		int i = LocIndex;

		this.getManagedBean().setLocIndex(i);
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

	private	List<ScLocationStateCriteria>	getLocationStateCriterias(List<TreeNode> treeNodes){
			List<ScLocationStateCriteria>	results = new ArrayList<ScLocationStateCriteria>();
			for (TreeNode treeNode : treeNodes) {
			List<PmState>	states	=	getPmStates(treeNode.getChildren());
			if(states != null && states.size() > 0){
				for(PmState state: states){
					ScLocationStateCriteria stateCriteria	=	new ScLocationStateCriteria();
					stateCriteria.setCriteriaValue(String.valueOf(state.getStateId()));
					results.add(stateCriteria);
				}
			}
			}
			return results;
	  }
		
	private	List<ScLocationDivisionCriteria> getLocationDivisionCriterias(List<TreeNode> treeNodes){
			List<ScLocationDivisionCriteria>	results = new ArrayList<ScLocationDivisionCriteria>();
			for (TreeNode treeNode : treeNodes) {
			List<PmDivision>	divisions	=	getPmDivisions(treeNode.getChildren());
			if(divisions != null && divisions.size() > 0){
				for(PmDivision division: divisions){
					ScLocationDivisionCriteria  divisionCriteria = new ScLocationDivisionCriteria();
					divisionCriteria.setCriteriaValue(String.valueOf(division.getDivisionId()));
					results.add(divisionCriteria);
				}
			}
			}
			return results;
	  }	
		
	private List<ScLocationDistrictCriteria> getLocationDistrictCriterias(List<TreeNode> treeNodes){
			List<ScLocationDistrictCriteria>	results = new ArrayList<ScLocationDistrictCriteria>();
			for (TreeNode treeNode : treeNodes) {
			List<PmDistrict>	districts	=	getPmDistricts(treeNode.getChildren());
			if(districts != null && districts.size() > 0){
				for(PmDistrict district: districts){
					ScLocationDistrictCriteria districtCriteria	=	new ScLocationDistrictCriteria();
					districtCriteria.setCriteriaValue(String.valueOf(district.getDistrictId()));
					results.add(districtCriteria);
				}
			}
			}
			return results;
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
		
	private List<ScBusinessNatureQuoteCriteria> getBusinessNatureCriterias(List<ScBusinessNatureQuoteCriteria> businessNatureQuoteCriterias){
			List<ScBusinessNatureQuoteCriteria> businessNatureCriterias = new ArrayList<ScBusinessNatureQuoteCriteria>();
			if(businessNatureQuoteCriterias != null){
				for (ScBusinessNatureQuoteCriteria businessNatureQuoteCriteria : businessNatureQuoteCriterias) {
					ScBusinessNatureQuoteCriteria natureQuoteCriteria = new ScBusinessNatureQuoteCriteria();
					natureQuoteCriteria.setCriteriaValue(String.valueOf(businessNatureQuoteCriteria.getBusinessNature().getBusinessNatureId()));
					businessNatureCriterias.add(natureQuoteCriteria);
				}
			}
			return businessNatureCriterias;
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

	public String getCurrentStatus() {
		String result = "";
		if (getManagedBean().getScQuote().getWorkflowStatuses() != null) {
			for (ScSQWorkFlowStatus status : getManagedBean().getScQuote()
					.getWorkflowStatuses()) {
				if (status.isCurrent()) {
					result = getStatusDescById(status.getPmStatus()
							.getStatusId());
					break;
				}
			}
		}
		return result;
	}
	
	public SelectItem[]  getProcTypeDropdown() {
		SelectItem[] selectItems = new SelectItem[4];
		selectItems[0] = new SelectItem("N", "Normal");
		selectItems[1] = new SelectItem("A","AdHoc");
		selectItems[2] = new SelectItem("E", "Emergency");
		selectItems[3] = new SelectItem("G", "G2G");
		return selectItems;
	}
		
	public SelectItem[] getLocationLevelDropdown() {
		SelectItem[] selectItems = new SelectItem[4];
		selectItems[0] = new SelectItem(null, "- Select One -");
		selectItems[1] = new SelectItem("S", "State");
		selectItems[2] = new SelectItem("V", "Division");
		selectItems[3] = new SelectItem("D", "District");
		return selectItems;
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
		
	public PmOrgProfile ptjNameEvent(Long orgProfileId) {
		if (orgProfileId != null) {
			for (PmOrgProfile orgProfile : getManagedBean()
					.getUserDivisionList()) {
				if (orgProfile.getOrgProfileId().equals(
						orgProfileId)) {
					getManagedBean().setPmOrgProfile(orgProfile);
				}
			}
		}
		return this.getManagedBean().getPmOrgProfile();
	}
	
}