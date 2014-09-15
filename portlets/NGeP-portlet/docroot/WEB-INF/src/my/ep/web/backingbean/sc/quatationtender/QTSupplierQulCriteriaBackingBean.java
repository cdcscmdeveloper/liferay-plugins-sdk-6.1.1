/**
 * 
 */
package my.ep.web.backingbean.sc.quatationtender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.ct.supplier.IPanelSupplierSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.ct.supplier.PanelSupplierReqDTO;
import my.ep.dto.ct.supplier.PanelSupplierResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierZone;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.web.managedbean.sc.quationtender.Node;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.TreeNode;

/**
 * @author VidyasagarReddy
 * 
 */
@ManagedBean(name = "qTSupplierQulCriteriaBackingBean")
@RequestScoped
public class QTSupplierQulCriteriaBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {
	private static final Logger log = EpLogger
			.getLogger(QTSupplierQulCriteriaBackingBean.class);
	private Boolean renderZone;
	private Boolean checked;

	public QTSupplierQulCriteriaBackingBean() {
		debugLogger("QTSupplierQulCriteriaBackingBean");

		if (getManagedBean().getSupplierCriteria() == null) {
			this.getManagedBean().setSupplierCriteria(
					new ScQtSupplierCriteria());
		}
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * To add empty row to category open code data table
	 */
	public void addNewCategoryAction() {
		debugLogger("addNewCategoryAction");
		List<ScQtCategory> qtCategoryList = this.getManagedBean().getQtCategoryList();
		if (qtCategoryList == null) {
			qtCategoryList = new ArrayList<ScQtCategory>();
			this.getManagedBean().setQtCategoryList(qtCategoryList);
		}
		ScQtCategory category = new ScQtCategory();
		// category.setScQt(scQt);
		category.setPmCategoryL1(new PmCategoryL1());
		category.setPmCategoryL2(new PmCategoryL2());
		category.setPmCategoryL3(new PmCategoryL3());
		qtCategoryList.add(category);
		this.getManagedBean().setQtCategoryList(qtCategoryList);
	}

	public void addNewRestrictedCategoryAction() {
		debugLogger("addNewRestrictedCategoryAction");
		List<ScQtCategory> scQtCategories = this.getManagedBean().getSuppCriteriarestrictedList();
		if (scQtCategories == null) {
			scQtCategories = new ArrayList<ScQtCategory>();
		}
		ScQtCategory category = new ScQtCategory();
		category.setPmCategoryL1(new PmCategoryL1());
		category.setPmCategoryL2(new PmCategoryL2());
		category.setPmCategoryL3(new PmCategoryL3());
		scQtCategories.add(category);
		this.getManagedBean().setSuppCriteriarestrictedList(scQtCategories);
	}

	public void deleteRestrictedCategoryAction() {
		debugLogger("deleteRestrictedCategoryAction");
		List<ScQtCategory> restrictedList = this.getManagedBean().getSuppCriteriarestrictedList();
		if (null == restrictedList) {
			restrictedList = new ArrayList<ScQtCategory>();
		}
		List<ScQtCategory> restrictedSelectList = this.getManagedBean().getSelectedRestrictedCategoryList();
		if (null == restrictedSelectList) {
			restrictedSelectList = new ArrayList<ScQtCategory>();
		}
		if (restrictedList.size() == restrictedSelectList.size()) {
			restrictedList.clear();
		} else {
			for (ScQtCategory category : restrictedSelectList)
				restrictedList.remove(category);
		}
	}

	/*
	 * To remove categoryCode from data table
	 */
	public void deleteCategoryCodes() {
		debugLogger("deleteCategoryCodes");
		List<ScQtCategory> openList = this.getManagedBean().getQtCategoryList();
		if (null == openList) {
			openList = new ArrayList<ScQtCategory>();
		}
		List<ScQtCategory> openSelectList = this.getManagedBean().getSelectedCategoryList();
		if (null == openSelectList) {
			openSelectList = new ArrayList<ScQtCategory>();
		}
		if (openList.size() == openSelectList.size()) {
			openList.clear();
		} else {
			for (ScQtCategory category : openSelectList)
				openList.remove(category);
		}
	}

	/**
	 * To retrive districts based on state value changes.
	 */
	public void handleStateChange(ValueChangeEvent changeEvent) {
		PmState state = (PmState) changeEvent.getNewValue();
		if (state != null && state.getStateId() != null) {
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			reqDTO.setStateId(state.getStateId());
			IDistrictSO districtSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDistrictSO.class);
			DistrictResDTO resDTO = districtSO
					.retriveDistrictListByStateID(reqDTO);
			if (resDTO != null && !resDTO.getDistrictList().isEmpty()) {
				this.getManagedBean().getDistricts().clear();
				this.getManagedBean().setDistricts(resDTO.getDistrictList());
			}
		}
	}

	public void searchPanelSupplierPopupAction() {
		debugLogger("searchPanelSupplierPopupAction");

		getManagedBean().setCircularNo(null);
		getManagedBean().setCircularName(null);
		getManagedBean().setItemName(null);
		getManagedBean().setCircularCoverage(null);
		getManagedBean().setTotalSearchSupplier(0);
		getManagedBean().setSearchPanelSuppliersNode(null);
	}

	/**
	 * Method to search th*e supplier for panel and restricted tender.
	 */
	public void searchPanelSupplierAction() {
		debugLogger("searchPanelSupplierAction");

		int totalSearchSupplier = 0;
		TreeNode searchPanelSuppliersNode = null;
		List<CtPanelSupplier> panelSupplierList = retriverSuppliersForPanelQT();

		if (panelSupplierList != null && panelSupplierList.size() > 0) {
		}

		this.getManagedBean().setTotalSearchSupplier(totalSearchSupplier);
		this.getManagedBean().setSearchPanelSuppliersNode(
				searchPanelSuppliersNode);
	}

	/**
	 * Method to search the supplier for normal tender
	 */
	public void searchNormalSupplierAction() {
		debugLogger("searchNormalSupplierAction");

		/*
		 * ScQtCriteria scQtCriteria = getManagedBean().getQtCriteria();
		 * 
		 * if (!scQtCriteria.isMultiLocality() ||
		 * scQtCriteria.isMultiLocality()) { // TODO Temporary get from panel
		 * supplier. List<CtPanelSupplier> panelSupplierList =
		 * retriverSuppliersForPanelQT(); List<SmSupplier> supplierList = new
		 * ArrayList<SmSupplier>();
		 * 
		 * for (CtPanelSupplier panelSupplier : panelSupplierList) {
		 * supplierList.add(panelSupplier.getSupplier()); }
		 * 
		 * populateQtSupplierList(supplierList);
		 */// System.out.println("Supplier Name : " +
			// supplierList.get(0).getSmAppl().getSmCompanyBasic().getCompanyName());

	}

	public void searchRestrictedSupplierPopupAction() {
		debugLogger("searchRestrictedSupplierPopupAction");
		getManagedBean().setSupplierName(null);
		getManagedBean().setMofAccountNo(null);
		getManagedBean().setMinPaidUpCapital(null);
		getManagedBean().setSearchSupplierList(null);
		getManagedBean().setSelectedSupplierList(null);
	}

	/**
	 * Method to search the supplier for restricted tender
	 */
	public void searchRestrictedSupplierAction() {
		debugLogger("searchRestrictedSupplierAction");
		IQTDetailSO QtDetailsSO;
		try {
			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setCategoryList(getManagedBean().getSuppCriteriarestrictedList());
			tenderReqDTO.setSupplierName(getManagedBean().getSupplierName());
			tenderReqDTO.setMofAccountNo(getManagedBean().getMofAccountNo());
			tenderReqDTO.setMinPaidUpCapital(getManagedBean().getMinPaidUpCapital());
			QuationTenderResDTO tenderResDTO = QtDetailsSO.retriveSupplierProfiles(tenderReqDTO);
			List<SmSupplier> list = tenderResDTO.getSupplierList();
			getManagedBean().setSearchSupplierList(list);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			QtDetailsSO = null;
		}
	}

	public void addRestrictedToSupplierListAction() {
		debugLogger("addRestrictedToSupplierListAction");

		List<SmSupplier> selectedSupplierList = getManagedBean()
				.getSelectedSupplierList();

		populateQtSupplierList(selectedSupplierList);
	}

	private List<CtPanelSupplier> retriverSuppliersForPanelQT() {
		ICircularSO circularSO = null;
		List<CtPanelSupplier> ctPanelSuppliers = null;

		try {
			circularSO = BusinessDelegate.getInstance().getServiceObject("ct",
					ICircularSO.class);
			CircularReqDTO reqDTO = new CircularReqDTO();
			SearchBean<CtPanelSupplier> searchBean = new SearchBean<CtPanelSupplier>();

			// Filter By - circularNo
			String circularNo = getManagedBean().getCircularNo();
			if (StringUtils.isNotBlank(circularNo)) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"ctPanelItem.ctCircularVer.circularNo",
								SearchOperator.OPERATOR_EQUALS.value(),
								circularNo));
			}

			// Filter By - circularName
			String circularName = getManagedBean().getCircularName();
			if (StringUtils.isNotBlank(circularName)) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("ctPanelItem.ctCircularVer.name",
								SearchOperator.OPERATOR_LIKE.value(),
								circularName));
			}

			// Filter By - itemName
			String itemName = getManagedBean().getItemName();
			if (StringUtils.isNotBlank(itemName)) {
				searchBean
						.getDataFilterBeans()
						.add(new SearchFilterBean("ctPanelItem.itemName",
								SearchOperator.OPERATOR_LIKE.value(), itemName));
			}

			// Filter By - circularCoverage
			String circularCoverage = getManagedBean().getCircularCoverage();
			if (StringUtils.isNotBlank(circularCoverage)) {
				if (ParameterConst.COVERAGE_PENINSULAR.equals(circularCoverage)) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"ctPanelItem.ctCircularVer.peninsular",
									SearchOperator.OPERATOR_EQUALS.value(),
									true));
				} else if (ParameterConst.COVERAGE_SABAH
						.equals(circularCoverage)) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"ctPanelItem.ctCircularVer.sabah",
									SearchOperator.OPERATOR_EQUALS.value(),
									true));
				} else if (ParameterConst.COVERAGE_SARAWAK
						.equals(circularCoverage)) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"ctPanelItem.ctCircularVer.sarawak",
									SearchOperator.OPERATOR_EQUALS.value(),
									true));
				}
			}

			reqDTO.setSearchBean(searchBean);

			CircularResDTO resDTO = circularSO.searchPanelSuppliers(reqDTO);

			ctPanelSuppliers = resDTO.getPanelSupplierList();

		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		} finally {
			circularSO = null;
		}

		return ctPanelSuppliers;
	}

	private void populateQtSupplierList(List<SmSupplier> suppliers) {
		ScQtSupplier scQtSupplier;
		ScQt scQt = getManagedBean().getQt();
		List<ScQtCategory> qtCategoryList = getManagedBean().getQtCategoryList();
		List<ScQtSupplier> qtSupplierList = new ArrayList<ScQtSupplier>();
		for (SmSupplier smSupplier : suppliers) { // create Qt supplier.
			scQtSupplier = new ScQtSupplier();
			scQtSupplier.setSmSupplier(smSupplier);
			scQtSupplier.setScQt(scQt);
			scQtSupplier.setScQtSupplierZones(new ArrayList<ScQtSupplierZone>());
			qtSupplierList.add(scQtSupplier);
		}
		// update qt supplier list.
		getManagedBean().setTotalSupplier(qtSupplierList.size());
		getManagedBean().setQtSupplierList(qtSupplierList);

		// For save the criteria details when search button if clicked.
		// scQt.setScQtCriterias(scQtCriteria);
		scQt.setScQtCategorys(qtCategoryList);
	}

	public TreeNode getSeletedTreeNode(TreeNode treeNode, Node selectedNode) {
		TreeNode selecetedTreeNode = null;
		for (TreeNode childNode : treeNode.getChildren()) {
			Node node = (Node) childNode.getData();
			if (selectedNode.getNodeName().equals(node.getNodeName())) {
				selecetedTreeNode = childNode;
				break;
			}
			for (TreeNode childNode1 : childNode.getChildren()) {
				Node node1 = (Node) childNode1.getData();
				if (selectedNode.getNodeName().equals(node1.getNodeName())) {
					selecetedTreeNode = childNode1;
					break;
				}
			}
		}
		return selecetedTreeNode;
	}

	public void multiLocationChange(AjaxBehaviorEvent event) {
		Boolean multipleLoc = (Boolean) event.getComponent().getAttributes().get("value");
		log.info("MultipleLoc Value : " + multipleLoc);
		/*
		 * if(multipleLoc == false) { List<TreeNode> zoneTrees = new
		 * ArrayList<TreeNode>(); getManagedBean().setZoneTrees(zoneTrees); }
		 */

		// System.out.println("ADSFAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		/*
		 * System.out.println("PPPPPPPPPPPPPPPPPP" +event.getNewValue());
		 * Boolean multipleLoc = (Boolean) event.getNewValue();
		 * 
		 * TreeNode root3 = new DefaultTreeNode("Root", null); if (multipleLoc)
		 * { List<PmZone> pmZoneList = QTDetailManagedBean.pmZoneList; if (null
		 * != pmZoneList) { for (PmZone zone : pmZoneList) { TreeNode documents
		 * = new DefaultTreeNode(new Node( zone.getZoneId(), zone.getZoneName(),
		 * "Zone"), root3); documents.setSelectable(true); } }
		 * 
		 * } else { List<PmState> pmStateList = QTDetailManagedBean.pmStateList;
		 * if (null != pmStateList) { for (PmState state : pmStateList) {
		 * TreeNode documents = new DefaultTreeNode(new Node(
		 * state.getStateId(), state.getStateName(), "State"), root3);
		 * documents.setSelectable(true); } }
		 * 
		 * } this.getManagedBean().setRoot3(root3);
		 */

	}

	public Boolean getRenderZone() {
		return renderZone;
	}

	public void setRenderZone(Boolean renderZone) {
		this.renderZone = renderZone;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public void stateChange(AjaxBehaviorEvent event) {
		boolean state = (boolean) event.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().getSupplierCriteria().setAllState(state);
	}

	public void selectLocation(AjaxBehaviorEvent event) {
		Long location = (Long) event.getComponent().getAttributes()
				.get("value");
		if (this.getManagedBean().getQt().isZonal()) {
			this.getManagedBean().getQt().getZoneLocationLevel()
					.setParameterId(location);
		} else {
			this.getManagedBean().getQt().getScQtSupplierCriteria()
					.getLocationLevel().setParameterId(location);
		}
	}

	private List<ScQtLocCriteria> getLocations(TreeNode zoneLocation) {
		List<ScQtLocCriteria> results = new ArrayList<ScQtLocCriteria>();
		if (zoneLocation.isLeaf()) {
			ScQtLocCriteria locationDTO = (ScQtLocCriteria) zoneLocation
					.getData();
			results.add(locationDTO);
		} else {
			for (TreeNode childrendLocationNode : zoneLocation.getChildren()) {
				results.addAll(getLocations(childrendLocationNode));
			}
		}
		return results;
	}

	private List<TplSpecZoneLoc> getZoneLocations(TreeNode zoneLocation) {
		List<TplSpecZoneLoc> results = new ArrayList<TplSpecZoneLoc>();
		if (zoneLocation.isLeaf()) {
			TplSpecZoneLoc locationDTO = (TplSpecZoneLoc) zoneLocation
					.getData();
			results.add(locationDTO);
		} else {
			for (TreeNode childrendLocationNode : zoneLocation.getChildren()) {
				results.addAll(getZoneLocations(childrendLocationNode));
			}
		}
		return results;
	}

/*	private TplSpecZoneDtl getZone(TreeNode zoneLocation) {
		TplSpecZoneDtl result = null;
		if (zoneLocation.getData() instanceof TplSpecZoneDtl) {
			TplSpecZoneDtl locationDTO = (TplSpecZoneDtl) zoneLocation
					.getData();
			if (locationDTO != null) {
				result = locationDTO;
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
*/
	/**
	 * @param rootZoneLocation
	 * @param index
	 */
	public void editZoneLocation(TreeNode rootZoneLocation, int index) {
		IParameterSO parameterSo = null;
		ICountrySO countrySo = null;
		try {
			parameterSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			countrySo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			List<ScQtLocCriteria> locationDTOs = getLocations(rootZoneLocation);
			Long locationLevel = this.getManagedBean().getQt().getScQtSupplierCriteria().getLocationLevel().getParameterId();
			if (locationLevel == null) {
				ParameterReqDTO districtReqDTO = new ParameterReqDTO();
				districtReqDTO.setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				districtReqDTO.setParameterCode(ParameterConst.LOCATION_LEVEL_DISTRICT);
				ParameterResDTO districtResDTO = parameterSo.retrieveParameterByTypeAndCode(districtReqDTO);
				locationLevel = districtResDTO.getPmParameter().getParameterId();
			}
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
			String locationLevelCode = null;
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId().equals(locationLevel)) {
					locationLevelCode = paramDesc.getPmParameter().getParameterCode();
					this.getManagedBean().setLocalLevelCode(locationLevelCode);
					break;
				}
			}
			// ScQuoteZone zone = getZone(rootZoneLocation);
			List<PmState> states = SourcingLocationHelper.getStateFromLocationDTO(locationDTOs);
			List<PmDivision> divisions = SourcingLocationHelper.getDivisionFromLocationDTO(locationDTOs);
			List<PmDistrict> districts = SourcingLocationHelper.getDistrictFromLocationDTO(locationDTOs);
			List<PmCity> cities = SourcingLocationHelper.getCityFromLocationDTO(locationDTOs);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);
			getManagedBean().setRootLocationTree(
					LocationSingleton.getInstance().buildLocationPopupTree(countryResDTO.getPmCountry().getCountryId(),
							states, districts, divisions, cities, locationLevelCode, ScQtLocCriteria.class));
			int i = index;
			this.getManagedBean().setIndex(i);
		} finally {
			parameterSo = null;
		}
	}

	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}
	
	public void showZoneLocationPopup() {
		IParameterSO parameterSo = null;
		ICountrySO countrySo = null;
		try {
			parameterSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			countrySo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			Long locationLevel = this.getManagedBean().getQt().getScQtSupplierCriteria().getLocationLevel().getParameterId();
			if (locationLevel == null) {
				ParameterReqDTO districtReqDTO = new ParameterReqDTO();
				districtReqDTO.setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				districtReqDTO.setParameterCode(ParameterConst.LOCATION_LEVEL_DISTRICT);
				ParameterResDTO districtResDTO = parameterSo.retrieveParameterByTypeAndCode(districtReqDTO);
				locationLevel = districtResDTO.getPmParameter().getParameterId();
			}
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId().equals(locationLevel)) {
					this.getManagedBean().setLocalLevelCode(paramDesc.getPmParameter().getParameterCode());
					CountryReqDTO countryReqDTo = new CountryReqDTO();
					countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
					CountryResDTO countryResDTO = countrySo.retrieveCountryByCode(countryReqDTo);
					getManagedBean().setRootLocationTree(
							LocationSingleton.getInstance()
									.buildLocationPopupTree(countryResDTO.getPmCountry().getCountryId(),
											null, null, null, null, paramDesc.getPmParameter().getParameterCode(), ScQtLocCriteria.class));
				}
			}
		} finally {
			parameterSo = null;
		}
	}

	public void updateZoneLocation() {
		List<ScQtLocCriteria> locationDTOs = new ArrayList<ScQtLocCriteria>();
		if (getManagedBean().getSelectedNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedNodes()) {
				if (!treeNode.getParent().isSelected()) {
					List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
					locationDTOs.addAll(locationDTO);
				} else {
					List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
					locationDTOs.addAll(locationDTO);
				}
			}
		}
		List<PmState> states = SourcingLocationHelper.getStateFromLocationDTO(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper.getDivisionFromLocationDTO(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper.getDistrictFromLocationDTO(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper.getCityFromLocationDTO(locationDTOs);
		if (null == getManagedBean().getZoneTrees()) {
			getManagedBean().setZoneTrees(new ArrayList<TreeNode>());
		}
		if (ParameterConst.LOCATION_LEVEL_STATE.equalsIgnoreCase(this.getManagedBean().getLocalLevelCode())) {
			if (null != states && !states.isEmpty()) {
				for (PmState state : states) {
					List<PmState> localStaeList = new ArrayList<PmState>();
					localStaeList.add(state);
					if (getManagedBean().getIndex() == -1) {
						getManagedBean().getZoneTrees().add(LocationSingleton.getInstance()
										.buildLocationTree(null, localStaeList, districts, divisions, cities, ScQtLocCriteria.class));
					}
					if (getManagedBean().getIndex() >= 0) {
						getManagedBean().getZoneTrees().remove(getManagedBean().getIndex());
						getManagedBean().getZoneTrees().add(getManagedBean().getIndex(), LocationSingleton.getInstance().buildLocationTree(null, localStaeList, districts, divisions, cities, ScQtLocCriteria.class));
					}
				}
			}
		} else if (ParameterConst.LOCATION_LEVEL_DISTRICT.equalsIgnoreCase(this.getManagedBean().getLocalLevelCode())) {
			if (null != districts && !districts.isEmpty()) {
				List<TreeNode> localList = new ArrayList<TreeNode>();
				Map<PmState, Map<PmDivision, Map<PmDistrict, List<PmCity>>>> mapStatesDivisionsDistrictsCities = LocationSingleton.getInstance().getUniqueStatesDivisionsDistrictsCities(districts, null);
				Set<PmState> statesDivisionsDistrictsCitiesKeySet = mapStatesDivisionsDistrictsCities.keySet();
				Map<PmState, Map<PmDistrict, List<PmCity>>> mapStatesDistrictsCities = LocationSingleton.getInstance().getUniqueStateDistrictsCities(districts, null);
				Set<PmState> statesDistrictsCitiesKeySet = mapStatesDistrictsCities.keySet();
				if (statesDivisionsDistrictsCitiesKeySet != null && !statesDivisionsDistrictsCitiesKeySet.isEmpty()) {
					for (PmState state : statesDivisionsDistrictsCitiesKeySet) {
						Map<PmDivision, Map<PmDistrict, List<PmCity>>> mapDivisionsDistrictsCities = mapStatesDivisionsDistrictsCities.get(state);
						Set<PmDivision> divisionsDistrictsCitiesKeySet = mapDivisionsDistrictsCities.keySet();
						if (divisionsDistrictsCitiesKeySet != null && !divisionsDistrictsCitiesKeySet.isEmpty()) {
							divisions.clear();
							districts.clear();
							for (PmDivision division : divisionsDistrictsCitiesKeySet) {
								// divisions.add(division);
								Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapDivisionsDistrictsCities.get(division);
								Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities.keySet();
								if (districtsCitiesKeySet != null && !districtsCitiesKeySet.isEmpty()) {
									for (PmDistrict district : districtsCitiesKeySet) {
										districts.add(district);
									}
								}
							}
							if (getManagedBean().getIndex() == -1) {
								getManagedBean().getZoneTrees().add(LocationSingleton.getInstance().buildLocationTree(null, states, districts, divisions, cities, ScQtLocCriteria.class));
							}
							if (getManagedBean().getIndex() >= 0) {
								getManagedBean().getZoneTrees().remove(getManagedBean().getIndex());
								getManagedBean().getZoneTrees().add(getManagedBean().getIndex(), LocationSingleton.getInstance().buildLocationTree(null, states, districts, divisions, cities, ScQtLocCriteria.class));
							}
						}
					}
				}
				if (statesDistrictsCitiesKeySet != null && !statesDistrictsCitiesKeySet.isEmpty()) {
					for (PmState state : statesDistrictsCitiesKeySet) {
						divisions.clear();
						Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapStatesDistrictsCities.get(state);
						Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities.keySet();
						if (districtsCitiesKeySet != null && !districtsCitiesKeySet.isEmpty()) {
							districts.clear();
							for (PmDistrict district : districtsCitiesKeySet) {
								districts.add(district);
							}
						}
						if (getManagedBean().getIndex() == -1) {
							getManagedBean().getZoneTrees().add(LocationSingleton.getInstance().buildLocationTree(null, states, districts, divisions, cities, ScQtLocCriteria.class));
						}
						if (getManagedBean().getIndex() >= 0) {
							getManagedBean().getZoneTrees().remove(getManagedBean().getIndex());
							getManagedBean().getZoneTrees().add(getManagedBean().getIndex(), LocationSingleton.getInstance().buildLocationTree(null, states, districts, divisions, cities, ScQtLocCriteria.class));
						}
					}
				}
			}
		} else if (ParameterConst.LOCATION_LEVEL_DIVISION.equalsIgnoreCase(this.getManagedBean().getLocalLevelCode())) {
			if (null != divisions && !divisions.isEmpty()) {
				Map<PmState, List<PmDivision>> mapStatesDivision = LocationSingleton.getInstance().getUniqueStatesDivision(divisions);
				Set<PmState> statesDivisionKeySet = mapStatesDivision.keySet();
				if (statesDivisionKeySet != null && !statesDivisionKeySet.isEmpty()) {
					for (PmState state : statesDivisionKeySet) {
						divisions.clear();
						List<PmDivision> divisions1 = mapStatesDivision.get(state);
						if (divisions1 != null && !divisions1.isEmpty()) {
							for (PmDivision division : divisions1) {
								divisions.add(division);
							}
							if (getManagedBean().getIndex() == -1) {
								getManagedBean().getZoneTrees().add(LocationSingleton.getInstance().buildLocationTree(null, states, districts, divisions, cities, ScQtLocCriteria.class));
							}
							if (getManagedBean().getIndex() >= 0) {
								getManagedBean().getZoneTrees().remove(getManagedBean().getIndex());
								getManagedBean().getZoneTrees().add(getManagedBean().getIndex(), LocationSingleton.getInstance().buildLocationTree(null, states, districts, divisions, cities, ScQtLocCriteria.class));
							}
						}
					}
				}
			}
		}
		this.getManagedBean().setIndex(-1);
	}

	public void updateZoneGeneralLocation() {
		List<TplSpecZoneLoc> allNodes = new ArrayList<TplSpecZoneLoc>();
		if (getManagedBean().getSelectedGeneralTreeNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedGeneralTreeNodes()) {
				if (!treeNode.getParent().isSelected()) {
					List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
					allNodes.addAll(locationDTO);
				} else {
					List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
					allNodes.addAll(locationDTO);
				}
			}
		}
		TplSpecZoneDtl zone = new TplSpecZoneDtl();
		zone.setZoneName(getManagedBean().getZoneName());
		getManagedBean().setZoneName(null);
		List<PmState> states = SourcingLocationHelper.getStateFromLocationDTO(allNodes);
		List<PmDivision> divisions = SourcingLocationHelper.getDivisionFromLocationDTO(allNodes);
		List<PmDistrict> districts = SourcingLocationHelper.getDistrictFromLocationDTO(allNodes);
		List<PmCity> cities = SourcingLocationHelper.getCityFromLocationDTO(allNodes);
		if (getManagedBean().getIndex() == -1) {
			getManagedBean().getZoneGeneralTrees().add(LocationSingleton.getInstance().buildLocationTree(zone, states, districts, divisions, cities, TplSpecZoneLoc.class));
		} else if (getManagedBean().getIndex() >= 0) {
			getManagedBean().getZoneGeneralTrees().remove(getManagedBean().getIndex());
			getManagedBean().getZoneGeneralTrees().add(getManagedBean().getIndex(), LocationSingleton.getInstance().buildLocationTree(zone, states, districts, divisions, cities, TplSpecZoneLoc.class));
		}
		this.getManagedBean().setIndex(-1);
	}

	public void deleteRestrictedSupplierList() {
		List<ScQtSupplier> list = getManagedBean().getQtSupplierList();
		List<ScQtSupplier> selectedList = getManagedBean().getSelectedQtSupplierList();
		if (list.size() == selectedList.size()) {
			list.clear();
			getManagedBean().setTotalSupplier(list.size());
		} else {
			for (ScQtSupplier scQtSupplier : selectedList) {
				list.remove(scQtSupplier);
				getManagedBean().setTotalSupplier(list.size());
			}
		}
	}

	public void retriveZoneBySupplier(NodeSelectEvent event) {
		CtPanelZone ctPanelzone = (CtPanelZone) event.getTreeNode().getData();
		PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();
		reqDTO.setCircularZoneId(ctPanelzone.getPanelZoneId());
		IPanelSupplierSO panelSupplierSO = (IPanelSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IPanelSupplierSO.class);
		PanelSupplierResDTO resDTO = panelSupplierSO.retrieveSupplierByZone(reqDTO);
		this.getManagedBean().setCtZoneSupplierList(new ArrayList<CtPanelSupplier>());
		this.getManagedBean().setTotalSupplier(0);
		if (null != resDTO.getPanelSuppliers() && !resDTO.getPanelSuppliers().isEmpty()) {
			this.getManagedBean().setTotalSupplier(resDTO.getPanelSuppliers().size());
			this.getManagedBean().setCtZoneSupplierList(resDTO.getPanelSuppliers());
		}
	}

	public void reset() {
		debugLogger("reset");
		getManagedBean().setSupplierName(null);
		getManagedBean().setMofAccountNo(null);
		getManagedBean().setMinPaidUpCapital(null);
	}
	
	/**
	 * To delete the selected TreeNode from list..
	 */
	public void deleteSupplierQualificationTreeNode() {
		debugLogger("deleteSupplierQualificationTreeNode");
		List<TreeNode> rootTrees = getManagedBean().getZoneTrees();
		deleteTreeNodes(rootTrees);
		this.getManagedBean().setIndex(-1);
	}
	
	public void deleteTreeNodes(List<TreeNode> rootTrees) {
		debugLogger("deleteTreeNodes");
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

	private void deleteChildTreeNodes(List<TreeNode> treeNodes) {
		debugLogger("deleteChildTreeNodes");
		if (treeNodes != null) {
			List<TreeNode> nodesToRemoved = new ArrayList<TreeNode>();
			for (TreeNode treeNode : treeNodes) {
				if (treeNode.isSelected()) {
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
	}
	
	public SelectItem[] getDropdownLocationValues(String locationLevel) {

		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCATION_LEVEL_STATE);
		types.add(ParameterConst.LOCATION_LEVEL_DIVISION);
		types.add(ParameterConst.LOCATION_LEVEL_DISTRICT);

		SelectItem[] items = null;
		try {
			items = new SelectItem[types.size() + 1];
			items[0] = new SelectItem(null, "- Select One -");
			int i = 1;
			for (String location : types) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(locationLevel);
				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterCode()
							.equals(location)) {

						items[i++] = new SelectItem(paramDesc.getPmParameter()
								.getParameterId(), paramDesc.getCodeName());
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
		// return getDropdownCodeValues(committeeRole);
	}
	
	public void showSupplierDetails() {
		debugLogger("showSupplierDetails");
		ScQtSupplier qtSupplier = this.getManagedBean().getScQtSupplier();
		List<SmPersonnel> personnelsList = qtSupplier.getSmSupplier().getSmAppl().getSmPersonnels();
		for (SmPersonnel smPersonnel : personnelsList) {
			getManagedBean().setPersonnel(smPersonnel);
		}
		//SmPersonnel supplierHQPersonnel = this.getManagedBean().getPersonnel();
		SmCompanyBasic smCompanyBasic = qtSupplier.getSmSupplier().getSmAppl().getSmCompanyBasic();
		
		for (SmCompanyAddress smCompanyAddress : smCompanyBasic.getSmCompanyAddresses()) {
			if (AddressType.CONTACT_ADDRESS.value().equals(smCompanyAddress.getAddressType())) {
				log.info("found hq address");
				getManagedBean().setSupplierBranch("HQ");
				getManagedBean().setSupplierAddress(smCompanyAddress.getSmAddress());
				getManagedBean().setSupplierContact(getManagedBean().getPersonnel().getName());
				getManagedBean().setSupplierEmail(getManagedBean().getPersonnel().getEmail());
				break;
			}
		}
	}
	
	public void selectLocationTreeNode(NodeSelectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();
		
		selectedTreeNode.setSelected(true);
	}
	
	public void unSelectLocationTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();
		
		selectedTreeNode.setSelected(false);
	}
	
	public void categoryCodesUpdate(int index, List<ScQtCategory> categoryList, PmCategoryL3 categoryL3) {
		ICategoryCodeSO categoryCodeSo;
		try {
			categoryCodeSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
			CategoryCodeReqDTO codeReqDTO = new CategoryCodeReqDTO();
			codeReqDTO.setCategoryL3Id(categoryL3.getCategoryL3Id());
			CategoryCodeResDTO CategoryCodeResDTO = categoryCodeSo
					.retrieveCategoryL3DtlById(codeReqDTO);
			if (null != CategoryCodeResDTO
					&& null != CategoryCodeResDTO.getCategoryL3()) {
				ScQtCategory category = categoryList.get(index);
				category.setPmCategoryL3(CategoryCodeResDTO.getCategoryL3());
				category.setPmCategoryL2(CategoryCodeResDTO.getCategoryL3()
						.getPmCategoryL2());
				category.setPmCategoryL1(CategoryCodeResDTO.getCategoryL3()
						.getPmCategoryL2().getPmCategoryL1());
				category.setScQt(this.getManagedBean().getQt());
				setUserDetails(category);
				this.getManagedBean().getQtCategoryList().remove(index);
				this.getManagedBean().getQtCategoryList()
						.add(index, category);
			}
		} finally {

		}
	}
	
	public void restrictedCategoryCodesUpdate(int index, List<ScQtCategory> categoryList, PmCategoryL3 categoryL3) {
		ICategoryCodeSO categoryCodeSo;
		try {
			categoryCodeSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
			CategoryCodeReqDTO codeReqDTO = new CategoryCodeReqDTO();
			codeReqDTO.setCategoryL3Id(categoryL3.getCategoryL3Id());
			CategoryCodeResDTO CategoryCodeResDTO = categoryCodeSo
					.retrieveCategoryL3DtlById(codeReqDTO);
			if (null != CategoryCodeResDTO
					&& null != CategoryCodeResDTO.getCategoryL3()) {
				ScQtCategory category = categoryList.get(index);
				category.setPmCategoryL3(CategoryCodeResDTO.getCategoryL3());
				category.setPmCategoryL2(CategoryCodeResDTO.getCategoryL3()
						.getPmCategoryL2());
				category.setPmCategoryL1(CategoryCodeResDTO.getCategoryL3()
						.getPmCategoryL2().getPmCategoryL1());
				category.setScQt(this.getManagedBean().getQt());
				setUserDetails(category);
				this.getManagedBean().getSuppCriteriarestrictedList().remove(index);
				this.getManagedBean().getSuppCriteriarestrictedList()
						.add(index, category);
			}
		} finally {

		}
	}
}
