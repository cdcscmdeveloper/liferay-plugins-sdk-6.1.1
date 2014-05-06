/**
 * 
 */
package my.ep.web.backingbean.sc.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.ct.supplier.IPanelSupplierSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.ct.supplier.PanelSupplierReqDTO;
import my.ep.dto.ct.supplier.PanelSupplierResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteZone;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.sc.directpurchase.LocationDTO;
import my.ep.web.managedbean.sc.quationtender.Node;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.NodeSelectEvent;
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
		/*
		 * if (this.getManagedBean().getQtCriteria() == null) {
		 * this.getManagedBean().setQtCriteria(new ScQtCriteria()); }
		 */
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

		// ScQt scQt = getManagedBean().getQt();
		List<ScQtCategory> qtCategoryList = this.getManagedBean()
				.getQtCategoryList();
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
	}

	public void addNewRestrictedCategoryAction() {
		debugLogger("addNewRestrictedCategoryAction");
		List<ScQtCategory> scQtCategories = this.getManagedBean().getSuppCriteriarestrictedList();
		if(scQtCategories == null) {
			scQtCategories = new ArrayList<ScQtCategory>();
			this.getManagedBean().setSuppCriteriarestrictedList(scQtCategories);
		}
		ScQtCategory category = new ScQtCategory();
		category.setPmCategoryL1(new PmCategoryL1());
		category.setPmCategoryL2(new PmCategoryL2());
		category.setPmCategoryL3(new PmCategoryL3());
		scQtCategories.add(category);
	}
	
	public void deleteRestrictedCategoryAction() {
		debugLogger("deleteRestrictedCategoryAction");
		List<ScQtCategory> categoriesList = getManagedBean().getSuppCriteriarestrictedList();
		List<ScQtCategory> selectedcategoriesList = getManagedBean()
				.getSelectedRestrictedCategoryList();
		if (categoriesList.size() == selectedcategoriesList.size()) {
			categoriesList.clear();
		} else {
			for (ScQtCategory scQtCategory : selectedcategoriesList) {
				categoriesList.remove(scQtCategory);
			}
		}
	}
	
	/*
	 * To remove categoryCode from data table
	 */
	public void deleteCategoryCode(ValueChangeEvent changeEvent) {
		ScQtCategory category = (ScQtCategory) changeEvent.getNewValue();
		List<ScQtCategory> categories = this.getManagedBean().getQt()
				.getScQtCategorys();
		if (categories.contains(category)) {
			categories.remove(category);
		}

	}

	/*
	 * Method to call onchange of L1category
	 */
	public void handleL1CategoryChange(ScQtCategory qtCategory) {
		debugLogger("handleL1CategoryChange");
		PmCategoryL1 categoryL1 = qtCategory.getPmCategoryL1();
		if (categoryL1 != null && categoryL1.getCategoryL1Id() != null) {
			List<PmCategoryL1> pmCategoryL1s = this.getManagedBean()
					.getCategoryL1s();
			for (PmCategoryL1 l1 : pmCategoryL1s) {
				if (l1.getCategoryL1Id().longValue() == categoryL1
						.getCategoryL1Id().longValue()) {
					qtCategory.setPmCategoryL1(l1);
					debugLogger("Set l1");
					break;
				}

			}

			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			reqDTO.setCategoryL1Id(categoryL1.getCategoryL1Id());

			ICategoryCodeSO categoryCodeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);
			CategoryCodeResDTO resDTO = categoryCodeSO
					.retrieveCategoryL2ListByL1Id(reqDTO);
			this.getManagedBean().setCategoryL2s(resDTO.getCategoryL2List());
		} else {
			this.getManagedBean().setCategoryL2s(new ArrayList<PmCategoryL2>());
		}
		qtCategory.setPmCategoryL2(new PmCategoryL2());
		handleL2CategoryChange(qtCategory);
	}

	/*
	 * Method to call onchange of L2category
	 */
	public void handleL2CategoryChange(ScQtCategory qtCategory) {
		debugLogger("handleL2CategoryChange");

		PmCategoryL2 categoryL2 = qtCategory.getPmCategoryL2();
		if (categoryL2 != null && categoryL2.getCategoryL2Id() != null) {
			List<PmCategoryL2> categoryL2s = this.getManagedBean()
					.getCategoryL2s();
			for (PmCategoryL2 l2 : categoryL2s) {
				if (l2.getCategoryL2Id().longValue() == categoryL2
						.getCategoryL2Id().longValue()) {
					qtCategory.setPmCategoryL2(l2);
					debugLogger("Set l2");
					break;
				}
			}
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			reqDTO.setCategoryL2Id(categoryL2.getCategoryL2Id());

			ICategoryCodeSO categoryCodeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);
			CategoryCodeResDTO resDTO = categoryCodeSO
					.retrieveCategoryL3ListByL2Id(reqDTO);
			this.getManagedBean().setCategoryL3s(resDTO.getCategoryL3List());
		} else {
			this.getManagedBean().setCategoryL3s(new ArrayList<PmCategoryL3>());
		}
		qtCategory.setPmCategoryL3(new PmCategoryL3());
	}

	public void handleL3CategoryChange(ScQtCategory qtCategory) {
		debugLogger("handleL3CategoryChange");

		PmCategoryL3 categoryL3 = qtCategory.getPmCategoryL3();
		if (categoryL3 != null && categoryL3.getCategoryL3Id() != null) {
			List<PmCategoryL3> categoryL3s = this.getManagedBean()
					.getCategoryL3s();
			for (PmCategoryL3 l3 : categoryL3s) {
				if (l3.getCategoryL3Id().longValue() == categoryL3
						.getCategoryL3Id().longValue()) {
					qtCategory.setPmCategoryL3(l3);
					debugLogger("Set l3");
					break;
				}
			}
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
			/*Map<CtCircularZone, List<CtPanelSupplier>> zoneMap = new LinkedHashMap<CtCircularZone, List<CtPanelSupplier>>();

			// Group Supplier by zone
			for (CtPanelSupplier panelSupplier : panelSupplierList) {
				CtCircularZone panelZone = panelSupplier.getCtCircularZone();
				List<CtPanelSupplier> zonePanelSuppliers = zoneMap
						.get(panelZone);

				if (zonePanelSuppliers == null) {
					zonePanelSuppliers = new ArrayList<CtPanelSupplier>();
					zoneMap.put(panelZone, zonePanelSuppliers);
				}
				zonePanelSuppliers.add(panelSupplier);
			}

			searchPanelSuppliersNode = new DefaultTreeNode();
			totalSearchSupplier = populateSupplierTreeNode(zoneMap,
					searchPanelSuppliersNode);*/
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
		getManagedBean().setSearchSupplierList(null);
		getManagedBean().setSelectedSupplierList(null);
	}

	/**
	 * Method to search the supplier for restricted tender
	 */
	public void searchRestrictedSupplierAction() {
		debugLogger("searchRestrictedSupplierAction");

		// TODO Temporary get from panel supplier.
		List<CtPanelSupplier> panelSupplierList = retriverSuppliersForPanelQT();
		List<SmSupplier> searchSupplierList = new ArrayList<SmSupplier>();

		for (CtPanelSupplier panelSupplier : panelSupplierList) {
			searchSupplierList.add(panelSupplier.getSupplier());
		}

		getManagedBean().setSearchSupplierList(searchSupplierList);
	}

	/*
	 * public void addPanelToSupplierListAction() {
	 * debugLogger("addPanelToSupplierListAction");
	 * 
	 * TreeNode searchPanelSuppliersNode = getManagedBean()
	 * .getSearchPanelSuppliersNode();
	 * 
	 * if (searchPanelSuppliersNode != null) { SmSupplier smSupplier = null;
	 * CtCircular ctCircular = null; ScQtZoneCriteria scQtZoneCriteria = null;
	 * ScQtLocCriteria scQtLocCriteria = null; ScQtCriteria scQtCriteria =
	 * getManagedBean().getQtCriteria(); List<SmSupplier> supplierList = new
	 * ArrayList<SmSupplier>(); TreeNode suppliersNode = new DefaultTreeNode();
	 * List<ScQtZoneCriteria> scQtZoneCriterias = new
	 * ArrayList<ScQtZoneCriteria>();
	 * 
	 * for (TreeNode searchZoneNode : searchPanelSuppliersNode .getChildren()) {
	 * 
	 * @SuppressWarnings("unchecked") TreeNodeBean<CtCircularZone> zoneBean =
	 * (TreeNodeBean<CtCircularZone>) searchZoneNode .getData();
	 * 
	 * // Add selected zone into supplier list. if (zoneBean.isSelected()) {
	 * CtCircularZone zone = zoneBean.getObject();
	 * 
	 * // create new zone criteria. scQtZoneCriteria = new ScQtZoneCriteria();
	 * scQtZoneCriteria.setZoneName(zone.getName());
	 * scQtZoneCriteria.setScQtCriteria(scQtCriteria); scQtZoneCriteria
	 * .setScQtLocCriterias(new ArrayList<ScQtLocCriteria>());
	 * scQtZoneCriterias.add(scQtZoneCriteria);
	 * 
	 * for (CtCircularZoneDtl state : zone.getCtCircularZoneDtls()) { // create
	 * new location criteria. scQtLocCriteria = new ScQtLocCriteria();
	 * scQtLocCriteria.setScQtCriteria(scQtCriteria);
	 * scQtLocCriteria.setScQtZoneCriteria(scQtZoneCriteria);
	 * scQtLocCriteria.setPmState(state.getState()); //
	 * scQtLocCriteria.setLocationLevel(LocationLevel.STATE.value());
	 * scQtZoneCriteria.getScQtLocCriterias().add( scQtLocCriteria); }
	 * 
	 * // build zone tree node. TreeNode zoneNode = new DefaultTreeNode( new
	 * TreeNodeBean<ScQtZoneCriteria>( zoneBean.getType(), scQtZoneCriteria),
	 * suppliersNode); zoneBean.setSelected(false);
	 * 
	 * for (TreeNode searchSupplierNode : searchZoneNode .getChildren()) {
	 * 
	 * @SuppressWarnings("unchecked") TreeNodeBean<CtPanelSupplier> supplierBean
	 * = (TreeNodeBean<CtPanelSupplier>) searchSupplierNode .getData();
	 * CtCircular searchCtCircular = supplierBean.getObject()
	 * .getCtPanelItem().getCtCircularVer() .getCtCircular();
	 * 
	 * if (ctCircular == null) { // Get selected circular number. ctCircular =
	 * supplierBean.getObject() .getCtPanelItem().getCtCircularVer()
	 * .getCtCircular(); } else { // All supplier must be same circular. if
	 * (ctCircular.getCircularId().longValue() != searchCtCircular
	 * .getCircularId().longValue()) { FacesContext .getCurrentInstance()
	 * .addMessage( null, new FacesMessage( FacesMessage.SEVERITY_ERROR,
	 * "Supplier must be same circular.", null)); return; } }
	 * 
	 * // build supplier tree node. smSupplier =
	 * supplierBean.getObject().getSupplier(); new DefaultTreeNode(new
	 * TreeNodeBean<SmSupplier>( supplierBean.getType(), smSupplier), zoneNode);
	 * 
	 * // Add to qt supplier list for save. supplierList.add(smSupplier); } } }
	 * 
	 * getManagedBean().getQtCriteria().setCtCircular(ctCircular);
	 * getManagedBean().getQtCriteria().setScQtZoneCriterias(
	 * scQtZoneCriterias); getManagedBean().setSuppliersNode(suppliersNode);
	 * 
	 * populateQtSupplierList(supplierList); } }
	 */

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

	/*private int populateSupplierTreeNode(
			Map<CtCircularZone, List<CtPanelSupplier>> zoneMap,
			TreeNode suppliersNode) {
		int totalSupplier = 0;

		for (Entry<CtCircularZone, List<CtPanelSupplier>> zoneEntry : zoneMap
				.entrySet()) {
			CtCircularZone zone = zoneEntry.getKey();
			List<CtPanelSupplier> zonePanelSuppliers = zoneEntry.getValue();

			TreeNode supplierNode = new DefaultTreeNode(
					new TreeNodeBean<CtCircularZone>("zone", zone),
					suppliersNode);
			for (CtPanelSupplier supplier : zonePanelSuppliers) {
				new DefaultTreeNode(new TreeNodeBean<CtPanelSupplier>(
						"supplier", supplier), supplierNode);
				totalSupplier++;
			}
		}

		return totalSupplier;
	}*/

	private void populateQtSupplierList(List<SmSupplier> suppliers) {
		/*
		 * ScQtSupplier scQtSupplier; ScQtSupplierZone scQtSupplierZone;
		 * List<ScQtLocCriteria> scQtLocCriterias; ScQt scQt =
		 * getManagedBean().getQt(); ScQtCriteria scQtCriteria =
		 * getManagedBean().getQtCriteria(); List<ScQtZoneCriteria>
		 * scQtZoneCriterias = scQtCriteria.getScQtZoneCriterias();
		 * List<ScQtCategory> qtCategoryList =
		 * getManagedBean().getQtCategoryList(); List<ScQtSupplier>
		 * qtSupplierList = new ArrayList<ScQtSupplier>();
		 * 
		 * for (SmSupplier smSupplier : suppliers) { // create Qt supplier.
		 * scQtSupplier = new ScQtSupplier();
		 * scQtSupplier.setSmSupplier(smSupplier); scQtSupplier.setScQt(scQt);
		 * scQtSupplier.setScQtSupplierZones(new ArrayList<ScQtSupplierZone>());
		 * 
		 * if (scQtZoneCriterias != null) { for (ScQtZoneCriteria
		 * scQtZoneCriteria : scQtZoneCriterias) { scQtLocCriterias =
		 * scQtZoneCriteria.getScQtLocCriterias();
		 * 
		 * for (ScQtLocCriteria scQtLocCriteria : scQtLocCriterias) { if
		 * (isSupplierInLocation(smSupplier, scQtLocCriteria)) { // create
		 * supplier zone. scQtSupplierZone = new ScQtSupplierZone();
		 * scQtSupplierZone.setScQtSupplier(scQtSupplier);
		 * scQtSupplierZone.setScQtZoneCriteria(scQtZoneCriteria);
		 * scQtSupplier.getScQtSupplierZones().add(scQtSupplierZone); } } } }
		 * 
		 * qtSupplierList.add(scQtSupplier); }
		 * 
		 * // update qt supplier list.
		 * getManagedBean().setTotalSupplier(qtSupplierList.size());
		 * getManagedBean().setQtSupplierList(qtSupplierList);
		 * 
		 * // For save the criteria details when search button if clicked.
		 * scQt.setScQtCriterias(scQtCriteria);
		 * scQt.setScQtCategorys(qtCategoryList);
		 */
	}

	/*
	 * private boolean isSupplierInLocation(SmSupplier smSupplier,
	 * ScQtLocCriteria scQtLocCriteria) { //String locationLevel =
	 * scQtLocCriteria.getLocationLevel(); //SmAddress smAddress =
	 * smSupplier.getSmAppl().getSmAddress();
	 * 
	 * if (LocationLevel.STATE.value().equals(locationLevel)) { return
	 * scQtLocCriteria.getPmState().getStateId().longValue() == smAddress
	 * .getPmState().getStateId().longValue(); } else if
	 * (LocationLevel.DIVISION.value().equals(locationLevel)) { return
	 * scQtLocCriteria.getPmDivision().getDivisionId().longValue() == smAddress
	 * .getPmDistrict().getPmDivision().getDivisionId().longValue(); } else if
	 * (LocationLevel.DISTRICT.value().equals(locationLevel)) { return
	 * scQtLocCriteria.getPmDistrict().getDistrictId().longValue() == smAddress
	 * .getPmDistrict().getDistrictId().longValue(); } else if
	 * (LocationLevel.CITY.value().equals(locationLevel)) { return
	 * scQtLocCriteria.getPmCity().getCityId().longValue() == smAddress
	 * .getPmCity().getCityId().longValue(); } else if
	 * (LocationLevel.DELIVERY_ADDRESS.value().equals(locationLevel)) { // TODO
	 * Based on delivery address. }
	 * 
	 * return false; }
	 */

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

		Boolean multipleLoc = (Boolean) event.getComponent().getAttributes()
				.get("value");
		System.out.println("MultipleLoc Value : " + multipleLoc);
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

	public void stateChange(AjaxBehaviorEvent e) {
		// doing some stuff here
		System.out.println("====enter process checked===="
				+ getManagedBean().getSelectedBoolean1());
		// String dueDate = (String) ((UIOutput)e.getSource()).getValue();
		Boolean id = (Boolean) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
		// getManagedBean().setSelectedBoolean(id);
		// this.getManagedBean().getQtCriteria().setAllState(id);
	}

	public void selectLocation(AjaxBehaviorEvent event) {
		Long location = (Long) event.getComponent().getAttributes()
				.get("value");
		System.out.println("=======location level====" + location);
		this.getManagedBean().getQt().getZoneLocationLevel().setParameterId(location);
		//this.getManagedBean().getQt().setZoneLocationLevel(location);
		// this.getManagedBean().getQtCriteria().setSupplierLocation(location);
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

	private TplSpecZoneDtl getZone(TreeNode zoneLocation) {
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

	public void editZoneLocation(TreeNode rootZoneLocation, int index) {
		List<ScQtLocCriteria> locationDTOs = getLocations(rootZoneLocation);

		// ScQuoteZone zone = getZone(rootZoneLocation);
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
		int i = index;

		this.getManagedBean().setIndex(i);
		//getManagedBean().setZoneName(zone.getZoneName());
		}

	public void showZoneLocationPopup() {
		debugLogger("showZoneLocationPopup");
		Integer locationLevel = this.getManagedBean().getQt().getZoneLocationLevel().getParameterId().intValue();
				

		if (locationLevel == null) {
			locationLevel = 121;
		}
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationZoneTree(1L, null,
						null, null, null, locationLevel));
	}

	public void showZoneLocationListPopup() {
		getManagedBean().setRootLocationTreeList(
				LocationSingleton.getInstance().buildLocationTree(1L, null,
						null, null, null));
	}

	public void showZoneDeliveryAddressListPopup() {
		getManagedBean().setRootAddressLocationTreeList(
				LocationSingleton.getInstance().buildAddressLocationTree(1L,
						null));
	}

	public void updateZoneLocation() {
		List<ScQtLocCriteria> locationDTOs = new ArrayList<ScQtLocCriteria>();
		// TreeNode[] listSelectedTreeNode =
		// getManagedBean().getSelectedNodes().clone();
		if (getManagedBean().getSelectedNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedNodes()) {
				if (!treeNode.getParent().isSelected()) {
					List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
					// LocationDTO locationDTO = (LocationDTO)
					// treeNode.getData();
					locationDTOs.addAll(locationDTO);
				}
			}
		}
		
		// ScQuoteZone zone = new ScQuoteZone();
		// zone.setZoneName(getManagedBean().getZoneName());
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTOs(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTOs(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTOs(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTOs(locationDTOs);
		
		if (getManagedBean().getIndex() == -1) {

			getManagedBean().getZoneTrees().add(
					LocationSingleton.getInstance().buildZoneTree(null, states,
							districts, divisions, cities));
		}

		if (getManagedBean().getIndex() >= 0) {
			getManagedBean().getZoneTrees().remove(getManagedBean().getIndex());
			getManagedBean().getZoneTrees().add(
					getManagedBean().getIndex(),
					LocationSingleton.getInstance().buildZoneTree(null, states,
							districts, divisions, cities));
		}
		this.getManagedBean().setIndex(-1);

		/*if (this.getManagedBean().getQt().isZonal() == true
				&& this.getManagedBean().getSupplierCriteria().isAllState() == false) {
			List<PmState> states = SourcingLocationHelper
					.getStateFromLocationDTOs(locationDTOs);
			List<PmDivision> divisions = SourcingLocationHelper
					.getDivisionFromLocationDTOs(locationDTOs);
			List<PmDistrict> districts = SourcingLocationHelper
					.getDistrictFromLocationDTOs(locationDTOs);
			List<PmCity> cities = SourcingLocationHelper
					.getCityFromLocationDTOs(locationDTOs);

			if (getManagedBean().getIndex() == -1) {

				getManagedBean().getZoneTrees().add(
						LocationSingleton.getInstance().buildZoneTree(zone,
								states, districts, divisions, cities));
			}
		}

		if (this.getManagedBean().getQt().isZonal() == false
				&& this.getManagedBean().getSupplierCriteria().isAllState() == false) {
			List<PmState> states = SourcingLocationHelper
					.getStateFromLocationDTOs(locationDTOs);

			List<PmDivision> divisions = SourcingLocationHelper
					.getDivisionFromLocationDTOs(locationDTOs);
			List<PmDistrict> districts = SourcingLocationHelper
					.getDistrictFromLocationDTOs(locationDTOs);
			List<PmCity> cities = SourcingLocationHelper
					.getCityFromLocationDTOs(locationDTOs);

			if (getManagedBean().getIndex() == -1) {

				getManagedBean().getZoneTrees().add(
						LocationSingleton.getInstance().buildZoneTree(null,
								states, districts, divisions, cities));
			}
		}

		if (this.getManagedBean().getQt().isZonal() == true
				&& this.getManagedBean().getSupplierCriteria().isAllState() == true) {

			List<PmState> states = new ArrayList<PmState>();

			List<PmDivision> divisions = new ArrayList<PmDivision>();
			List<PmDistrict> district = new ArrayList<PmDistrict>();
			List<PmCity> cities = new ArrayList<PmCity>();
			System.out.println("===========true true Zone name==="
					+ zone.getZoneName());
			if (getManagedBean().getIndex() == -1) {

				getManagedBean().getZonesList().add(
						LocationSingleton.getInstance().buildZoneTree(zone,
								states, district, divisions, cities));
			}
		}

		if (getManagedBean().getIndex() >= 0) {
			System.out.println("====enter greate o loop====");
			List<PmState> states = SourcingLocationHelper
					.getStateFromLocationDTOs(locationDTOs);

			List<PmDivision> divisions = SourcingLocationHelper
					.getDivisionFromLocationDTOs(locationDTOs);
			List<PmDistrict> districts = SourcingLocationHelper
					.getDistrictFromLocationDTOs(locationDTOs);
			List<PmCity> cities = SourcingLocationHelper
					.getCityFromLocationDTOs(locationDTOs);

			getManagedBean().getZoneTrees().remove(getManagedBean().getIndex());
			getManagedBean().getZoneTrees().add(
					getManagedBean().getIndex(),
					LocationSingleton.getInstance().buildZoneTree(zone, states,
							districts, divisions, cities));
		}
		this.getManagedBean().setIndex(-1);*/

	}

	public void updateZoneGeneralLocation() {
		List<TplSpecZoneLoc> allNodes = new ArrayList<TplSpecZoneLoc>();
		if (getManagedBean().getSelectedGeneralTreeNodes() != null) {
			for (TreeNode treeNode : getManagedBean()
					.getSelectedGeneralTreeNodes()) {

				if (!treeNode.getParent().isSelected()) {
					List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
					// LocationDTO locationDTO = (LocationDTO)
					// treeNode.getData();
					allNodes.addAll(locationDTO);
				}

			}
		}
		
		System.out.println("==managed bean zone name==="
				+ getManagedBean().getZoneName());

		TplSpecZoneDtl zone = new TplSpecZoneDtl();

		zone.setZoneName(getManagedBean().getZoneName());
		getManagedBean().setZoneName(null);

		List<PmState> states = SourcingLocationHelper
				.getStateFromZoneLocationDTOs(allNodes);
		System.out.println("===stae===" + states.size());
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromZoneLocationDTOs(allNodes);
		System.out.println("===divisions===" + divisions.size());
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromZoneLocationDTOs(allNodes);
		System.out.println("===districts===" + districts.size());
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromZoneLocationDTOs(allNodes);

		if (getManagedBean().getIndex() == -1) {
			System.out.println("====enter -1 loop" + zone.getZoneName());
			getManagedBean().getZoneGeneralTrees().add(
					LocationSingleton.getInstance().buildGeneralZoneTree(zone,
							states, districts, divisions, cities));

		}

		else if (getManagedBean().getIndex() >= 0) {
			System.out.println("====enter 0 loop");
			getManagedBean().getZoneGeneralTrees().remove(
					getManagedBean().getIndex());
			getManagedBean().getZoneGeneralTrees().add(
					getManagedBean().getIndex(),
					LocationSingleton.getInstance().buildGeneralZoneTree(zone,
							states, districts, divisions, cities));
		}
		this.getManagedBean().setIndex(-1);
	}

	public void updateZoneLocationList() {
		List<LocationDTO> locationDTOs = new ArrayList<LocationDTO>();
		// TreeNode[] listSelectedTreeNode =
		// getManagedBean().getSelectedNodes().clone();
		if (getManagedBean().getSelectedLocNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedLocNodes()) {
				if (!treeNode.getParent().isSelected()) {
					LocationDTO locationDTO = (LocationDTO) treeNode.getData();
					locationDTOs.add(locationDTO);
				}
			}
		}
		ScQuoteZone zone = new ScQuoteZone();
		System.out.println("zone name : " + getManagedBean().getZoneName1());
		zone.setZoneName(getManagedBean().getZoneName1());
		getManagedBean().setZoneName(null);
		System.out.println("Zone Name : " + zone.getZoneName());

		/*List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTOs(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTOs(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTOs(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTOs(locationDTOs);

		getManagedBean().getZoneTreeList().add(
				LocationSingleton.getInstance().buildZoneTree(zone, states,
						districts, divisions, cities));*/
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationTree(1L, null,
						null, null, null));
		System.out.println("Zone Tree : "
				+ getManagedBean().getZoneTrees().size());
		// System.out.println("Zone Name : " + getManagedBean().getZoneName());
	}

	public void deleteRestrictedSupplierList() {
		List<ScQtSupplier> list = getManagedBean().getQtSupplierList();
		List<ScQtSupplier> selectedList = getManagedBean()
				.getSelectedQtSupplierList();
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
		/*CtCircularZone circularZone = (CtCircularZone) event.getTreeNode()
				.getData();*/
		PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();
		/*System.out.println("====ctzone id==="
				+ circularZone.getCircularZoneId());*/
		reqDTO.setCircularZoneId(1L);
		IPanelSupplierSO panelSupplierSO = (IPanelSupplierSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT,
						IPanelSupplierSO.class);
		PanelSupplierResDTO resDTO = panelSupplierSO
				.retrieveSupplierByZone(reqDTO);
		if (resDTO.getPanelSuppliers() != null) {
			System.out.println(resDTO.getPanelSuppliers());
			this.getManagedBean().setTotalSupplier(
					resDTO.getPanelSuppliers().size());
			this.getManagedBean().setCtZoneSupplierList(
					resDTO.getPanelSuppliers());
		}
	}

	public void reset() {
		debugLogger("reset");
		getManagedBean().setSupplierName(null);
		getManagedBean().setMofAccountNo(null);
	}

	public void showSupplierDetails() {
		debugLogger("showSupplierDetails");
	}
}
