/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.backingbean.sc.quatationtender;

import java.io.IOException;
import java.math.BigDecimal;
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

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.supplier.IPanelSupplierSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.supplier.PanelSupplierReqDTO;
import my.ep.dto.ct.supplier.PanelSupplierResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
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
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.ct.circular.panel.PanelBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.SupplierSearchBean;
import my.ep.web.managedbean.sc.quationtender.Node;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.TreeNode;

/**
 * The Class QTSupplierQulCriteriaBackingBean.
 *
 * @author VidyasagarReddy
 */
@ManagedBean(name = "qTSupplierQulCriteriaBackingBean")
@RequestScoped
public class QTSupplierQulCriteriaBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {
	
	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(QTSupplierQulCriteriaBackingBean.class);

	/**
	 * Instantiates a new QT supplier qul criteria backing bean.
	 */
	public QTSupplierQulCriteriaBackingBean() {
		debugLogger("QTSupplierQulCriteriaBackingBean");
	}
	
	/**
	 * The Class SupplierPagingDataModel.
	 */
	private class SupplierPagingDataModel extends PagingDataModel<SmSupplier> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The supplier search bean. */
		private SupplierSearchBean supplierSearchBean;

		/**
		 * Instantiates a new supplier paging data model.
		 *
		 * @param componentId the component id
		 * @param supplierSearchBean the supplier search bean
		 */
		public SupplierPagingDataModel(String componentId, SupplierSearchBean supplierSearchBean) {
			super(componentId, false);
			this.supplierSearchBean = supplierSearchBean;
		}
		
		/* (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		protected ISearchResultBean<SmSupplier> pageChangeListener(ISearchBean<SmSupplier> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			
			IQTDetailSO QtDetailsSO;
			try {
				QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
				
				tenderReqDTO.setCategoryList(supplierSearchBean.getScQtCategoryList());
				tenderReqDTO.setSupplierName(supplierSearchBean.getSupplierName());
				tenderReqDTO.setMofAccountNo(supplierSearchBean.getMofAccountNo());
				tenderReqDTO.setMinPaidUpCapital(supplierSearchBean.getMinPaidUpCapital());
				
				populateSupplierSearchCriteria(supplierSearchBean, searchBean);
				tenderReqDTO.setSupplierSearchBean(searchBean);
				
				QuationTenderResDTO tenderResDTO = QtDetailsSO.retriveSupplierProfiles(tenderReqDTO);
				return tenderResDTO.getSmSupplierResultBean();
			} finally {
				QtDetailsSO = null;
			}
		}
	}

	/**
	 * Populate supplier search criteria.
	 *
	 * @param supplierSearchBean the supplier search bean
	 * @param searchBean the search bean
	 */
	private void populateSupplierSearchCriteria(SupplierSearchBean supplierSearchBean, ISearchBean<SmSupplier> searchBean) {
		if (supplierSearchBean != null) {
			List<ScQtCategory> scQtCategoryList = getManagedBean().getSuppCriteriarestrictedList();
			if(scQtCategoryList != null && scQtCategoryList.size() > 0){
				SearchFilterBean categoryCodeIdFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
				categoryCodeIdFilterBean.setSearchField("smAppl.smSupplierCategories.pmCategoryL3.categoryL3Id");
				categoryCodeIdFilterBean.setSearchValue(constructL3Ids(scQtCategoryList));
				searchBean.getDataFilterBeans().add(categoryCodeIdFilterBean);
			}
			
			String supplierName = supplierSearchBean.getSupplierName();
			if (!StringUtils.isBlank(supplierName)) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("companyName", SearchOperator.OPERATOR_LIKE.value(), supplierName, true));
			}
			
			String mofAccountNo = supplierSearchBean.getMofAccountNo();
			if (!StringUtils.isBlank(mofAccountNo)) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("smMofAccounts.mofNo", SearchOperator.OPERATOR_LIKE.value(), mofAccountNo, true));
			}
			
			BigDecimal paidUpCapital = supplierSearchBean.getMinPaidUpCapital();
			if (paidUpCapital != null && paidUpCapital.longValue() > 0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("smAppls.smCompanyBasic.paidUpCapital", SearchOperator.OPERATOR_GREATER_EQUAL.value(), paidUpCapital.toString(), true));
			}
		}
	}

	/**
	 * Debug logger.
	 *
	 * @param message the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * To add empty row to category open code data table.
	 */
	public void addNewCategoryAction() {
		debugLogger("addNewCategoryAction");
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
		this.getManagedBean().setQtCategoryList(qtCategoryList);
	}

	/**
	 * To add empty row to Restricted Tender Category Code data table.
	 */
	public void addNewRestrictedCategoryAction() {
		debugLogger("addNewRestrictedCategoryAction");
		List<ScQtCategory> scQtCategories = this.getManagedBean()
				.getSuppCriteriarestrictedList();
		if (scQtCategories == null) {
			scQtCategories = new ArrayList<ScQtCategory>();
		}
		ScQtCategory category = new ScQtCategory();
		//category.setScQt(this.getManagedBean().getQt());
		category.setPmCategoryL1(new PmCategoryL1());
		category.setPmCategoryL2(new PmCategoryL2());
		category.setPmCategoryL3(new PmCategoryL3());
		scQtCategories.add(category);
		this.getManagedBean().setSuppCriteriarestrictedList(scQtCategories);
	}

	/**
	 * To Remove Restricted Tender Category codes from data table.
	 */
	public void deleteRestrictedCategoryAction() {
		debugLogger("deleteRestrictedCategoryAction");
		List<ScQtCategory> restrictedList = this.getManagedBean()
				.getSuppCriteriarestrictedList();
		if (null == restrictedList) {
			restrictedList = new ArrayList<ScQtCategory>();
		}
		List<ScQtCategory> restrictedSelectList = this.getManagedBean()
				.getSelectedRestrictedCategoryList();
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

	/**
	 * To remove categoryCode from data table
	 */
	public void deleteCategoryCodes() {
		debugLogger("deleteCategoryCodes");
		List<ScQtCategory> openList = this.getManagedBean().getQtCategoryList();
		if (null == openList) {
			openList = new ArrayList<ScQtCategory>();
		}
		List<ScQtCategory> openSelectList = this.getManagedBean()
				.getSelectedCategoryList();
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
	 *
	 * @param changeEvent the change event
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

	/**
	 * Method to search the supplier for panel.
	 */
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
	 *
	 * @param event the event
	 */
	/*public void searchPanelSupplierAction() {
		debugLogger("searchPanelSupplierAction");

		int totalSearchSupplier = 0;
		TreeNode searchPanelSuppliersNode = null;
		List<CtPanelSupplier> panelSupplierList = retriverSuppliersForPanelQT();

		if (panelSupplierList != null && panelSupplierList.size() > 0) {
		}

		this.getManagedBean().setTotalSearchSupplier(totalSearchSupplier);
		this.getManagedBean().setSearchPanelSuppliersNode(
				searchPanelSuppliersNode);
	}*/

	public void onTabChangeAction(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();
		if (tabTitle.equalsIgnoreCase(getMessageCommon("lbl.qt.supplier.list"))) {
			
			List<ScQtSupplier> sessionList = getManagedBean().getQtSupplierList();
			if(sessionList == null){
				getManagedBean().setQtSupplierList(new ArrayList<ScQtSupplier>());
				IQTDetailSO qtDetailsSO = null;
				try {
					qtDetailsSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
					QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
					
					ScQt scQt = getManagedBean().getQt();
					reqDTO.setQtId(scQt.getQtId());
					
					QuationTenderResDTO resDTO = qtDetailsSO.retrieveScQtSupplierByQtId(reqDTO);
					List<ScQtSupplier> dbList = resDTO.getSupplierResultBean().getResultList();
					getManagedBean().getQtSupplierList().addAll(dbList);
					
				} finally {
					qtDetailsSO = null;
				}
			}
		}
	}

	/**
	 * Method to search the supplier for Restricted Tender.
	 */
	public void searchRestrictedSupplierPopupAction() {
		debugLogger("searchRestrictedSupplierPopupAction");
		List<ScQtCategory> scQtCategoryList = getManagedBean().getSuppCriteriarestrictedList();
		getManagedBean().getSupplierSearchBean().setScQtCategoryList(scQtCategoryList);
		getManagedBean().getSupplierSearchBean().setSupplierName(null);
		getManagedBean().getSupplierSearchBean().setMofAccountNo(null);
		getManagedBean().getSupplierSearchBean().setMinPaidUpCapital(null);
		getManagedBean().setSelectedSupplierList(null);
		
		IPagingDataModel<SmSupplier> searchSupplierDataModel = getManagedBean().getSearchSupplierDataModel();
		if (searchSupplierDataModel == null) {
			getManagedBean().setSearchSupplierDataModel(new SupplierPagingDataModel("ressupplier", getManagedBean().getSupplierSearchBean()));
		}
		getManagedBean().getSearchSupplierDataModel().resetData();
	}
	
	/**
	 * Method to search the supplier for Restricted Tender.
	 */
	public void searchRestrictedSupplierAction() {
		boolean hasSearchValue = false;
		log.info("hasSearchValue: " + hasSearchValue);
		String supplierName = getManagedBean().getSupplierSearchBean().getSupplierName();
		SupplierSearchBean supplierNameFilterBean = new SupplierSearchBean(SearchOperator.OPERATOR_LIKE.value(), supplierName);
		if (StringUtils.isNotBlank((String)supplierNameFilterBean.getSupplierName())) {
			hasSearchValue = true;
		}
		
		String mofNumber = getManagedBean().getSupplierSearchBean().getMofAccountNo();
		SupplierSearchBean mofNumberFilterBean = new SupplierSearchBean(SearchOperator.OPERATOR_LIKE.value(), mofNumber);
		if (StringUtils.isNotBlank((String)mofNumberFilterBean.getMofAccountNo())) {
			hasSearchValue = true;
		}
		
		BigDecimal paidUpCapital = getManagedBean().getSupplierSearchBean().getMinPaidUpCapital();
		SupplierSearchBean capitalFilterBean = new SupplierSearchBean(SearchOperator.OPERATOR_GREATER_EQUAL.value(), paidUpCapital);
		if (capitalFilterBean.getMinPaidUpCapital() != null && capitalFilterBean.getMinPaidUpCapital().longValue() > 0) {
			hasSearchValue = true;
		}
		
		log.info("hasSearchValue: " + hasSearchValue);
		if(!hasSearchValue) {
			throw new EpException(MessageConst.ERROR_760231);
		} else {		
			getManagedBean().getSearchSupplierDataModel().reloadData();
		}
	}

	/**
	 * Retrieving smMofAccount Details from SM.
	 *
	 * @param ScQtSupplier
	 * @param SmSupplier
	 * @return SmMofAccount
	 */
	public SmMofAccount retrieveSmMofAccount(ScQtSupplier scQtSupplier, SmSupplier smSupplier) {
		debugLogger("retrieveSmMofAccount");

		Long supplierId = null;
		SmMofAccount smMofAccount = null;
		if (scQtSupplier != null) {
			supplierId = scQtSupplier.getSmSupplier().getSupplierId();
			smMofAccount = scQtSupplier.getSmMofAccount();
			
			if(smMofAccount != null){
				return smMofAccount;
			}
		} else {
			supplierId = smSupplier.getSupplierId();
			smMofAccount = smSupplier.getSmMofAccount();
			
			if(smMofAccount != null){
				return smMofAccount;
			}
		}

		IMofAccountSO mofAccountSO;
		try {
			mofAccountSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofAccountSO.class);
			MofAccountReqDTO accountReqDTO = new MofAccountReqDTO();
			accountReqDTO.setSupplierId(supplierId);
			accountReqDTO.setSupplierType(ParameterConst.SUPPLIER_TYPE_CONTRACTOR);
			
			MofAccountResDTO mofAccountResDTO = mofAccountSO.retriveMOFAccountBySupplierIdAndType(accountReqDTO);
			if (mofAccountResDTO.getMofAccount() != null) {
				smMofAccount = mofAccountResDTO.getMofAccount();
				if (scQtSupplier != null) {
					scQtSupplier.setSmMofAccount(smMofAccount);
				} else {
					smSupplier.setSmMofAccount(smMofAccount);
				}
			}
		} finally {
			mofAccountSO = null;
		}
		return smMofAccount;
	}

	/**
	 * Adds the restricted to supplier list action.
	 */
	public void addRestrictedToSupplierListAction() {
		debugLogger("addRestrictedToSupplierListAction");
		
		List <ScQtSupplier> qtSupplierList = this.getManagedBean().getQtSupplierList();
		if (null == qtSupplierList ) {
			qtSupplierList = new ArrayList<ScQtSupplier>();
		}
		ScQt scQt = getManagedBean().getQt();
		List<ScQtCategory> qtCategoryList = getManagedBean().getQtCategoryList();
		List<SmSupplier> selectedSupplierList = getManagedBean().getSelectedSupplierList();
		List<String> itemIds = new ArrayList<String>();
		if (!selectedSupplierList.isEmpty()) {
			for (SmSupplier smSupplier : selectedSupplierList) {
				ScQtSupplier scQtSupplier = new ScQtSupplier();
				scQtSupplier.setSmSupplier(smSupplier);
				scQtSupplier.setScQt(scQt);
				scQtSupplier.setScQtSupplierZones(new ArrayList<ScQtSupplierZone>());
				
				if (this.getManagedBean().getQtSupplierList().isEmpty()) {
					this.getManagedBean().getQtSupplierList().add(scQtSupplier);
					getManagedBean().setTotalSupplier(this.getManagedBean().getQtSupplierList().size());
				} else {
					for (ScQtSupplier qtSupplier : this.getManagedBean().getQtSupplierList()) {
						itemIds.add(qtSupplier.getSmSupplier().getSupplierId().toString());
					}
					if (itemIds.size() > 0) {
						if (itemIds.contains(smSupplier.getSupplierId().toString())) {
							throw new EpException(MessageConst.ERROR_760197);
						} else {
							qtSupplierList.add(scQtSupplier);
							this.getManagedBean().setQtSupplierList(qtSupplierList);	
							getManagedBean().setTotalSupplier(this.getManagedBean().getQtSupplierList().size());
						}
					}
				}
			}
		} else {
			throw new EpException(MessageConst.ERROR_760121);
		}
		
		// For save the criteria details when search button if clicked.
		scQt.setScQtCategorys(qtCategoryList);
		
		// Supplier Search Validation
		RequestContext.getCurrentInstance().execute("suppdlg.hide()");
	}

	/**
	 * Gets the seleted tree node.
	 *
	 * @param treeNode the tree node
	 * @param selectedNode the selected node
	 * @return the seleted tree node
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

	/**
	 * Multi location change.
	 *
	 * @param event the event
	 */
	public void multiLocationChange(AjaxBehaviorEvent event) {
		Boolean multipleLoc = (Boolean) event.getComponent().getAttributes()
				.get("value");
		log.info("MultipleLoc Value : " + multipleLoc);
	}

	/**
	 * Supplier Qualification Criteria All States Select Or DeSelect Methods.
	 *
	 * @param event
	 */
	public void stateChange(AjaxBehaviorEvent event) {
		boolean state = (boolean) event.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().getQt().getScQtSupplierCriteria()
				.setAllState(state);

		if (!state) {
			if (null != this.getManagedBean().getZoneTrees()
					&& this.getManagedBean().getZoneTrees().isEmpty()) {
				this.getManagedBean().setZoneTrees(null);
			}
			if (null == this.getManagedBean().getQt()
					.getScQtSupplierCriteria().getLocationLevel()) {
				this.getManagedBean().getQt().getScQtSupplierCriteria()
						.setLocationLevel(new PmParameter());
			}

		} else {
			this.getManagedBean().setZoneTrees(null);
		}
	}
	
	/**
	 * Method to set the location level drop down values.
	 *
	 * @param event the event
	 */
	public void selectLocation(AjaxBehaviorEvent event) {
		Long location = (Long) event.getComponent().getAttributes()
				.get("value");
		if (null != location) {
			if (this.getManagedBean().getQt().isZonal()) {
				if (null != this.getManagedBean().getQt().getZoneLocationLevel()) {
					this.getManagedBean().getQt().getZoneLocationLevel()
					.setParameterId(location);
				}
				if (null != this.getManagedBean().getZoneGeneralTrees()
						&& !this.getManagedBean().getZoneGeneralTrees()
								.isEmpty()) {
					this.getManagedBean().setZoneGeneralTrees(null);
				}
			}
		}
	}

	/**
	 * Method to set the location level drop down values.
	 *
	 * @param event
	 */
	public void selectSupplierLocation(AjaxBehaviorEvent event) {
		debugLogger("selectSupplierLocation");
		Long location = (Long) event.getComponent().getAttributes()
				.get("value");
		log.info("Location : " + location);
		if (null != getManagedBean().getZoneTrees()
				&& !getManagedBean().getZoneTrees().isEmpty()) {
			this.getManagedBean().setSupplierLocationList(getManagedBean().getExistingSupplierLocationList());
			getManagedBean().setZoneTrees(null);
		}
	}

	/**
	 * Gets the locations.
	 *
	 * @param TreeNode
	 * @return List<ScQtLocCriteria>
	 */
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

	/**
	 * Gets the zone locations.
	 *
	 * @param zoneLocation the zone location
	 * @return the zone locations
	 */
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

	/**
	 * Edits the zone location.
	 *
	 * @param rootZoneLocation the root zone location
	 * @param index the index
	 */
	public void editZoneLocation(TreeNode rootZoneLocation, int index) {
		IParameterSO parameterSo = null;
		ICountrySO countrySo = null;
		try {
			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			List<ScQtLocCriteria> locationDTOs = getLocations(rootZoneLocation);
			Long locationLevel = this.getManagedBean().getQt()
					.getScQtSupplierCriteria().getLocationLevel()
					.getParameterId();
			if (locationLevel == null) {
				ParameterReqDTO districtReqDTO = new ParameterReqDTO();
				districtReqDTO
						.setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				districtReqDTO
						.setParameterCode(ParameterConst.LOCATION_LEVEL_DISTRICT);
				ParameterResDTO districtResDTO = parameterSo
						.retrieveParameterByTypeAndCode(districtReqDTO);
				locationLevel = districtResDTO.getPmParameter()
						.getParameterId();
			}
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
			String locationLevelCode = null;
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					locationLevelCode = paramDesc.getPmParameter()
							.getParameterCode();
					this.getManagedBean().setLocalLevelCode(locationLevelCode);
					break;
				}
			}
			// ScQuoteZone zone = getZone(rootZoneLocation);
			List<PmState> states = SourcingLocationHelper
					.getStateFromLocationDTO(locationDTOs);
			List<PmDivision> divisions = SourcingLocationHelper
					.getDivisionFromLocationDTO(locationDTOs);
			List<PmDistrict> districts = SourcingLocationHelper
					.getDistrictFromLocationDTO(locationDTOs);
			List<PmCity> cities = SourcingLocationHelper
					.getCityFromLocationDTO(locationDTOs);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);
			getManagedBean().setRootLocationTree(
					LocationSingleton.getInstance().buildLocationPopupTree(
							countryResDTO.getPmCountry().getCountryId(),
							states, districts, divisions, cities,
							locationLevelCode, ScQtLocCriteria.class));
			int i = index;
			this.getManagedBean().setIndex(i);
		} finally {
			parameterSo = null;
		}
	}

	/**
	 * Retrieve parameter desc by type.
	 *
	 * @param type the type
	 * @return the list
	 */
	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	/**
	 * Show zone location popup.
	 */
	public void showZoneLocationPopup() {
		debugLogger("showZoneLocationPopup");
		ICountrySO countrySo = null;
		try {

			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);

			Long locationLevel = this.getManagedBean().getQt()
					.getScQtSupplierCriteria().getLocationLevel()
					.getParameterId();

			if (locationLevel == null) {
				/*
				 * ParameterReqDTO districtReqDTO = new ParameterReqDTO();
				 * districtReqDTO
				 * .setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				 * districtReqDTO
				 * .setParameterCode(ParameterConst.LOCATION_LEVEL_DISTRICT);
				 * ParameterResDTO districtResDTO = parameterSo
				 * .retrieveParameterByTypeAndCode(districtReqDTO);
				 * 
				 * locationLevel = districtResDTO.getPmParameter()
				 * .getParameterId();
				 */
				throw new EpException(MessageConst.ERROR_760063);
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);

			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					this.getManagedBean().setLocalLevelCode(
							paramDesc.getPmParameter().getParameterCode());
					CountryReqDTO countryReqDTo = new CountryReqDTO();
					countryReqDTo
							.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
					CountryResDTO countryResDTO = countrySo
							.retrieveCountryByCode(countryReqDTo);
					// List<TplSpecZoneLoc> locationDTOs = new
					// ArrayList<TplSpecZoneLoc>();
					List<ScQtLocCriteria> locationDTOs = new ArrayList<ScQtLocCriteria>();
					if (getManagedBean().getZoneTrees() != null) {
						for (TreeNode treeNode : getManagedBean()
								.getZoneTrees()) {
							List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
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
							LocationSingleton.getInstance()
									.buildLocationPopupTree(
											countryResDTO.getPmCountry()
													.getCountryId(),
											states,
											districts,
											divisions,
											null,
											paramDesc.getPmParameter()
													.getParameterCode(),
											ScQtLocCriteria.class));
					this.getManagedBean().setIndex(-1);
				}
			}
			RequestContext.getCurrentInstance().execute("ZoneLocation.show()");
		} finally {

		}
	}

	/**
	 * Update zone location.
	 */
	public void updateZoneLocation() {
		debugLogger("updateZoneLocation");
		List<ScQtLocCriteria> locationDTOs = new ArrayList<ScQtLocCriteria>();
		this.getManagedBean().setSupplierLocationList(getManagedBean().getExistingSupplierLocationList());
		if (null == this.getManagedBean().getTotalSuppliersSpecLoc()) {
			this.getManagedBean().setTotalSuppliersSpecLoc(new ArrayList<TplSpecLoc>());
		}
		List<TplSpecLoc> specLoc = new ArrayList<TplSpecLoc>();
		if (getManagedBean().getSelectedNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedNodes()) {
				if (!treeNode.getParent().isSelected()) {
					List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
					for (ScQtLocCriteria locCritera : locationDTO) {
						TplSpecLoc localSpecLoc = (TplSpecLoc) locCritera;
						specLoc.add(localSpecLoc);
					}
					this.getManagedBean().getTotalSuppliersSpecLoc().addAll(specLoc);
					locationDTOs.addAll(locationDTO);
				} else {
					List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
					for (ScQtLocCriteria locCritera : locationDTO) {
						TplSpecLoc localSpecLoc = (TplSpecLoc) locCritera;
						specLoc.add(localSpecLoc);
					}
					this.getManagedBean().getTotalSuppliersSpecLoc().addAll(specLoc);
					locationDTOs.addAll(locationDTO);
				}
			}
		}
		retrievLocationDtls(locationDTOs);
		
	}

	/**
	 * To delete the selected Restricted Supplier from list..
	 */
	public void deleteRestrictedSupplierList() {
		debugLogger("deleteRestrictedSupplierList");
		List<ScQtSupplier> list = getManagedBean().getQtSupplierList();
		List<ScQtSupplier> selectedList = getManagedBean()
				.getSelectedQtSupplierList();
		if (selectedList != null && !selectedList.isEmpty()) {
			if (list.size() == selectedList.size()) {
				list.clear();
				getManagedBean().setTotalSupplier(list.size());
			} else {
				for (ScQtSupplier scQtSupplier : selectedList) {
					list.remove(scQtSupplier);
					getManagedBean().setTotalSupplier(list.size());
				}
			}
		} else {
			throw new EpException(MessageConst.ERROR_760121);
		}
	}

	/**
	 * Retrive zone by supplier.
	 *
	 * @param event the event
	 */
	public void retriveZoneBySupplier(NodeSelectEvent event) {
		debugLogger("retriveZoneBySupplier");
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(true);
		TplSpecZoneDtl ctPanelzone = (TplSpecZoneDtl) event.getTreeNode()
				.getData();

		PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();
		reqDTO.setCircularZoneId(ctPanelzone.getPanelZone().getPanelZoneId());
		IPanelSupplierSO panelSupplierSO = (IPanelSupplierSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT,
						IPanelSupplierSO.class);
		PanelSupplierResDTO resDTO = panelSupplierSO
				.retrieveSupplierByZone(reqDTO);
		this.getManagedBean().setCtZoneSupplierList(
				new ArrayList<CtPanelSupplier>());
		this.getManagedBean().setTotalSuppliers(0);

		if (null != resDTO.getPanelSuppliers()
				&& !resDTO.getPanelSuppliers().isEmpty()) {

			this.getManagedBean().setTotalSuppliers(
					resDTO.getPanelSuppliers().size());

			this.getManagedBean().setCtZoneSupplierList(
					resDTO.getPanelSuppliers());
			List<CtPanelItem> itemList = this.getManagedBean()
					.getSelectpanelItem();
			itemList = new ArrayList<CtPanelItem>();
			itemList.add(this.getManagedBean().getCtPanelItem());

			this.getManagedBean().setSelectpanelItem(itemList);
		}

	}

	/**
	 * Reset.
	 * 
	 * clear the key in data in Business owner search.
	 */
	public void reset() {
		debugLogger("reset");
		getManagedBean().getSupplierSearchBean().setSupplierName(null);
		getManagedBean().getSupplierSearchBean().setMofAccountNo(null);
		getManagedBean().getSupplierSearchBean().setMinPaidUpCapital(null);
	}

	/**
	 * To delete the selected TreeNode from list..
	 */
	public void deleteSupplierQualificationTreeNode() {
		debugLogger("deleteSupplierQualificationTreeNode");
		List<TreeNode> rootTrees = getManagedBean().getZoneTrees();
		deleteTreeNodes(rootTrees);
		this.getManagedBean().setTotalSuppliersSpecLoc(new ArrayList<TplSpecLoc>());
		if (!rootTrees.isEmpty()) {
			ScQtSupplierCriteria supplierCriteria = null;
			List<TplSpecLoc> localLoc = new ArrayList<TplSpecLoc>();
			this.getManagedBean().getQt().getScQtSupplierCriteria().setScQtLocCriteria(getQuoteZones(rootTrees, supplierCriteria));
			for (ScQtLocCriteria locCriteria : this.getManagedBean().getQt().getScQtSupplierCriteria().getScQtLocCriteria()) {
				TplSpecLoc specLoc = (TplSpecLoc) locCriteria;
				localLoc.add(specLoc);
			}
			this.getManagedBean().setTotalSuppliersSpecLoc(localLoc);
		}
		this.getManagedBean().setIndex(-1);
	}

	/**
	 * Gets the quote zones.
	 *
	 * @param treeNodes the tree nodes
	 * @param supplierCriteria the supplier criteria
	 * @return the quote zones
	 */
	private List<ScQtLocCriteria> getQuoteZones(List<TreeNode> treeNodes,
			ScQtSupplierCriteria supplierCriteria) {
		List<ScQtLocCriteria> results = new ArrayList<ScQtLocCriteria>();
		if (null != treeNodes && !treeNodes.isEmpty()) {
			for (TreeNode treeNode : treeNodes) {
				List<TreeNode> stateNodes = treeNode.getChildren();
				for (TreeNode treeNode2 : stateNodes) {
					List<TreeNode> district = treeNode2.getChildren();
					if (null != district && !district.isEmpty()) {
						for (TreeNode devision : district) {
							if (devision.getChildCount() > 0) {
								for (TreeNode districtNode : devision.getChildren()) {
									ScQtLocCriteria criteria = ((ScQtLocCriteria) districtNode.getData());
									criteria.setPmDistrict(((ScQtLocCriteria) districtNode.getData()).getPmDistrict());
									criteria.setPmDivision(((ScQtLocCriteria) districtNode.getParent().getData()).getPmDivision());
									criteria.setPmState(((ScQtLocCriteria) treeNode2.getData()).getPmState());
									criteria.setScQtSupplierCriteria(supplierCriteria);
									setUserDetails(criteria);
									results.add(criteria);
								}
							} else {
								ScQtLocCriteria criteria = ((ScQtLocCriteria) devision.getData());
								criteria.setPmDistrict(((ScQtLocCriteria) devision.getData()).getPmDistrict());
								criteria.setPmState(((ScQtLocCriteria) treeNode2.getData()).getPmState());
								criteria.setScQtSupplierCriteria(supplierCriteria);
								setUserDetails(criteria);
								results.add(criteria);
							}
						}
					} else {
						ScQtLocCriteria criteria = (ScQtLocCriteria) treeNode2.getData();
						criteria.setScQtSupplierCriteria(supplierCriteria);
						setUserDetails(criteria);
						results.add(criteria);
					}
				}
			}
		}
		return results;
	}

	/**
	 * Delete tree nodes.
	 *
	 * @param rootTrees the root trees
	 */
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

	/**
	 * Delete child tree nodes.
	 *
	 * @param treeNodes the tree nodes
	 */
	private void deleteChildTreeNodes(List<TreeNode> treeNodes) {
		debugLogger("deleteChildTreeNodes");
		if (treeNodes != null) {
			List<TreeNode> nodesToRemoved = new ArrayList<TreeNode>();
			for (TreeNode treeNode : treeNodes) {
				if (treeNode.isSelected()) {
					nodesToRemoved.add(treeNode);
					// deleteSupplierLocacriteria(loccriteria);
				} else {
					if (treeNode.getChildren() != null && treeNode.getChildren().size() > 0) {
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

	/**
	 * Gets the dropdown location values.
	 *
	 * @param locationLevel the location level
	 * @return the dropdown location values
	 */
	public SelectItem[] getDropdownLocationValues(String locationLevel) {
		debugLogger("getDropdownLocationValues");
		IOrgProfileSO orgProfile = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileResDTO orgResDto = orgProfile
				.retrieveOrgStateCodeByOrgProfileId(this.getManagedBean()
						.getQt().getPmOrgProfile().getOrgProfileId());
		this.getManagedBean().setAllStateCode(orgResDto.getStateStatusId());
		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCATION_LEVEL_STATE);
		if (this.getManagedBean().getAllStateCode() == 2) {
			types.add(ParameterConst.LOCATION_LEVEL_DIVISION);
		}
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

	/**
	 * Show supplier details.
	 *
	 * @param qtSupplier the qt supplier
	 */
	public void showSupplierDetails(ScQtSupplier qtSupplier) {
		debugLogger("showSupplierDetails");
		IQTSupplierSO qtSupplierSO = null;
		try {
			qtSupplierSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSupplierSO.class);
			
			QTSupplierReqDTO reqDTO = new QTSupplierReqDTO();
			reqDTO.setSupplierId(qtSupplier.getSmSupplier().getSupplierId());
			
			QTSupplierResDTO resDTO = qtSupplierSO.retrieveSupplierDtlById(reqDTO);
			
			this.getManagedBean().setSmSupplier(resDTO.getSmSupplier());
			this.getManagedBean().setSmMofAccount(resDTO.getSmMofAccount());
			/*List<SmPersonnel> personnelsList = resDTO.getSmSupplier()
					.getSmAppl().getSmPersonnels();
			if (null != personnelsList) {
				for (SmPersonnel smPersonnel : personnelsList) {
					getManagedBean().setPersonnel(smPersonnel);
				}
			}*/
			SmCompanyBasic smCompanyBasic = resDTO.getSmSupplier().getSmAppl().getSmCompanyBasic();
			for (SmCompanyAddress smCompanyAddress : smCompanyBasic.getSmCompanyAddresses()) {
				if (AddressType.CONTACT_ADDRESS.value().equals(smCompanyAddress.getAddressType())) {
					log.info("found hq address");
					getManagedBean().setSupplierBranch("HQ");
					getManagedBean().setSupplierAddress(smCompanyAddress.getSmAddress());
					//getManagedBean().setSupplierContact(getManagedBean().getPersonnel().getName());
					//getManagedBean().setSupplierEmail(getManagedBean().getPersonnel().getEmail());
					break;
				}
			}
		} finally {
			qtSupplierSO = null;
		}
	}

	/**
	 * Select location tree node.
	 *
	 * @param event the event
	 */
	public void selectLocationTreeNode(NodeSelectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(true);
	}

	/**
	 * Un select location tree node.
	 *
	 * @param event the event
	 */
	public void unSelectLocationTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(false);
	}

	/**
	 * Category codes update.
	 *
	 * @param index the index
	 * @param categoryList the category list
	 * @param categoryL3 the category l3
	 */
	public void categoryCodesUpdate(int index, List<ScQtCategory> categoryList,
			PmCategoryL3 categoryL3) {
		ICategoryCodeSO categoryCodeSo;
		try {
			categoryCodeSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
			CategoryCodeReqDTO codeReqDTO = new CategoryCodeReqDTO();
			codeReqDTO.setCategoryL3Id(categoryL3.getCategoryL3Id());
			CategoryCodeResDTO CategoryCodeResDTO = categoryCodeSo
					.retrieveCategoryL3DtlById(codeReqDTO);
			if (null != categoryList && categoryList.size() > 1) {
				boolean isDuplicate = false;
				for (int i = 0; i < categoryList.size() - 1; i++) {
					ScQtCategory scQtCategory = categoryList.get(i);
					if (scQtCategory
							.getPmCategoryL3()
							.getCategoryL3Code()
							.equalsIgnoreCase(
									CategoryCodeResDTO.getCategoryL3()
											.getCategoryL3Code())
							&& scQtCategory
									.getPmCategoryL2()
									.getCategoryL2Code()
									.equalsIgnoreCase(
											CategoryCodeResDTO.getCategoryL3()
													.getPmCategoryL2()
													.getCategoryL2Code())
							&& scQtCategory
									.getPmCategoryL1()
									.getCategoryL1Code()
									.equalsIgnoreCase(
											CategoryCodeResDTO.getCategoryL3()
													.getPmCategoryL2()
													.getPmCategoryL1()
													.getCategoryL1Code())) {
						isDuplicate = true;
						break;
					}
				}
				if (isDuplicate) {
					ScQtCategory scQtCategory = categoryList.get(index);
					this.getManagedBean().getQtCategoryList().remove(index);
					// .setScQt(scQt);
					scQtCategory.setPmCategoryL1(new PmCategoryL1());
					scQtCategory.setPmCategoryL2(new PmCategoryL2());
					scQtCategory.setPmCategoryL3(new PmCategoryL3());
					this.getManagedBean().getQtCategoryList()
							.add(index, scQtCategory);
					throw new EpException(MessageConst.ERROR_760111);
				} else {
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
			} else {
				ScQtCategory category = categoryList.get(index);
				category.setPmCategoryL3(CategoryCodeResDTO.getCategoryL3());
				category.setPmCategoryL2(CategoryCodeResDTO.getCategoryL3()
						.getPmCategoryL2());
				category.setPmCategoryL1(CategoryCodeResDTO.getCategoryL3()
						.getPmCategoryL2().getPmCategoryL1());
				category.setScQt(this.getManagedBean().getQt());
				setUserDetails(category);
				this.getManagedBean().getQtCategoryList().remove(index);
				this.getManagedBean().getQtCategoryList().add(index, category);
			}
		} finally {

		}
	}

	/**
	 * Restricted category codes update.
	 *
	 * @param index the index
	 * @param categoryList the category list
	 * @param categoryL3 the category l3
	 */
	public void restrictedCategoryCodesUpdate(int index,
			List<ScQtCategory> categoryList, PmCategoryL3 categoryL3) {
		ICategoryCodeSO categoryCodeSo;
		try {
			categoryCodeSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
			CategoryCodeReqDTO codeReqDTO = new CategoryCodeReqDTO();
			codeReqDTO.setCategoryL3Id(categoryL3.getCategoryL3Id());
			CategoryCodeResDTO CategoryCodeResDTO = categoryCodeSo
					.retrieveCategoryL3DtlById(codeReqDTO);
			if (null != categoryList && categoryList.size() > 1) {
				boolean isDuplicate = false;
				for (int i = 0; i < categoryList.size() - 1; i++) {
					ScQtCategory scQtCategory = categoryList.get(i);
					if (scQtCategory
							.getPmCategoryL3()
							.getCategoryL3Code()
							.equalsIgnoreCase(
									CategoryCodeResDTO.getCategoryL3()
											.getCategoryL3Code())
							&& scQtCategory
									.getPmCategoryL2()
									.getCategoryL2Code()
									.equalsIgnoreCase(
											CategoryCodeResDTO.getCategoryL3()
													.getPmCategoryL2()
													.getCategoryL2Code())
							&& scQtCategory
									.getPmCategoryL1()
									.getCategoryL1Code()
									.equalsIgnoreCase(
											CategoryCodeResDTO.getCategoryL3()
													.getPmCategoryL2()
													.getPmCategoryL1()
													.getCategoryL1Code())) {
						isDuplicate = true;
						break;
					}
				}
				if (isDuplicate) {
					ScQtCategory scQtCategory = categoryList.get(index);
					this.getManagedBean().getSuppCriteriarestrictedList()
							.remove(index);
					// .setScQt(scQt);
					scQtCategory.setPmCategoryL1(new PmCategoryL1());
					scQtCategory.setPmCategoryL2(new PmCategoryL2());
					scQtCategory.setPmCategoryL3(new PmCategoryL3());
					this.getManagedBean().getSuppCriteriarestrictedList()
							.add(index, scQtCategory);
					throw new EpException(MessageConst.ERROR_760111);
				} else {
					ScQtCategory category = categoryList.get(index);
					category.setPmCategoryL3(CategoryCodeResDTO.getCategoryL3());
					category.setPmCategoryL2(CategoryCodeResDTO.getCategoryL3()
							.getPmCategoryL2());
					category.setPmCategoryL1(CategoryCodeResDTO.getCategoryL3()
							.getPmCategoryL2().getPmCategoryL1());
					category.setScQt(this.getManagedBean().getQt());
					setUserDetails(category);
					this.getManagedBean().getSuppCriteriarestrictedList()
							.remove(index);
					this.getManagedBean().getSuppCriteriarestrictedList()
							.add(index, category);
				}
			} else {
				ScQtCategory category = categoryList.get(index);
				category.setPmCategoryL3(CategoryCodeResDTO.getCategoryL3());
				category.setPmCategoryL2(CategoryCodeResDTO.getCategoryL3()
						.getPmCategoryL2());
				category.setPmCategoryL1(CategoryCodeResDTO.getCategoryL3()
						.getPmCategoryL2().getPmCategoryL1());
				category.setScQt(this.getManagedBean().getQt());
				setUserDetails(category);
				this.getManagedBean().getSuppCriteriarestrictedList()
						.remove(index);
				this.getManagedBean().getSuppCriteriarestrictedList()
						.add(index, category);
			}
		} finally {
		}
	}

	/**
	 * Search for supplier count.
	 */
	public void searchForSupplierCount() {
		List<String> codes = new ArrayList<String>();
		List<ScQtCategory> suppCriteriaopenList = getManagedBean()
				.getQtCategoryList();
		ScQtSupplierCriteria scQtSupplierCriteria = getManagedBean().getQt()
				.getScQtSupplierCriteria();
		BigDecimal minPaidUpCapital = scQtSupplierCriteria
				.getMinPaidUpCapital();

		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		supplierSearchReqDTO.setCount(true);
		supplierSearchReqDTO
				.setCategoryCodeIds(constructL3Ids(suppCriteriaopenList));
		supplierSearchReqDTO.setPaidUpCapital(minPaidUpCapital);
		String locLevel = "";
		List<TplSpecLoc> tplSpecLocList = new ArrayList<TplSpecLoc>();

		ScQt scQt = getManagedBean().getQt();
		int estimatedAmt = scQt.getEstimatedAmount().intValue();
		if (estimatedAmt >= SourcingConstants.ESTMT_FIVE_HUNDERED_THOUSAND) {
			// value > 500k - always get the
			// scQtSupplierCriteria.getLocationLevel()
			if (getManagedBean().getAllStateCode() != 3
					&& !scQtSupplierCriteria.isAllState()) {
				if (null != scQtSupplierCriteria
						&& null != scQtSupplierCriteria.getLocationLevel()) {
					List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
					Long locLevelId = scQt.getScQtSupplierCriteria()
							.getLocationLevel().getParameterId();
					if (null != locLevelId) {
						for (PmParameterDesc paramDesc : localParamDesc) {
							Long paramDescId = paramDesc.getPmParameter()
									.getParameterId();
							if (paramDescId.equals(locLevelId)) {
								locLevel = paramDesc.getPmParameter()
										.getParameterCode();
							}
						}
					}
				}
				tplSpecLocList = getManagedBean().getTotalSuppliersSpecLoc();
			} else {
				// semenanjung, always all state = true or sabah sarawak all
				// state = true
				tplSpecLocList = null;
			}
		} else {
			if (scQt.isZonal()) {
				// value < 500k - if zonal, get the scQt.zoneLocationLevel()
				locLevel = scQt.getZoneLocationLevel().getParameterCode();
				if (null != scQt.getZoneLocationLevel()) {
					List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
					Long locLevelId = scQt.getZoneLocationLevel()
							.getParameterId();
					if (null != locLevelId) {
						for (PmParameterDesc paramDesc : localParamDesc) {
							Long paramDescId = paramDesc.getPmParameter()
									.getParameterId();
							if (paramDescId.equals(locLevelId)) {
								locLevel = paramDesc.getPmParameter()
										.getParameterCode();
							}
						}
					}
				}
				tplSpecLocList = getManagedBean().getTotalSuppliersSpecLoc();
				if (null == tplSpecLocList) {
					tplSpecLocList = new ArrayList<TplSpecLoc>();
				}
				List<TreeNode> rootZoneLocation = getManagedBean()
						.getZoneGeneralTrees();
				if (rootZoneLocation != null) {
					for (TreeNode treeNode : rootZoneLocation) {
						List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
						tplSpecLocList.addAll(locationDTO);
					}
				}
			} else {
				// value < 500k - if not zonal, get the
				// scQtSupplierCriteria.getLocationLevel()
				if (null != scQtSupplierCriteria
						&& null != scQtSupplierCriteria.getLocationLevel()) {
					List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
					Long locLevelId = scQt.getScQtSupplierCriteria()
							.getLocationLevel().getParameterId();
					if (null != locLevelId) {
						for (PmParameterDesc paramDesc : localParamDesc) {
							Long paramDescId = paramDesc.getPmParameter()
									.getParameterId();
							if (paramDescId.equals(locLevelId)) {
								locLevel = paramDesc.getPmParameter()
										.getParameterCode();
							}
						}
					}
				}
				tplSpecLocList = getManagedBean().getTotalSuppliersSpecLoc();
			}
		}
		getStatesNDistrict(tplSpecLocList, codes, locLevel);
		if (ParameterConst.LOCATION_LEVEL_STATE.equalsIgnoreCase(locLevel)) {
			supplierSearchReqDTO.setStateCodes(codes);
		} else if (ParameterConst.LOCATION_LEVEL_DIVISION
				.equalsIgnoreCase(locLevel)) {
			supplierSearchReqDTO.setDivisionCodes(codes);
		} else if (ParameterConst.LOCATION_LEVEL_DISTRICT
				.equalsIgnoreCase(locLevel)) {
			supplierSearchReqDTO.setDistrictCodes(codes);
		}

		ISupplierSearchSO supplierSearchSO = EpEjbBusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISupplierSearchSO.class);
		SupplierSearchResDTO searchResDTO = supplierSearchSO
				.retriveSupplierProfiles(supplierSearchReqDTO);
		this.getManagedBean().setTotalSuppliers(searchResDTO.getCount());
	}

	/**
	 * Gets the states n district.
	 *
	 * @param locations the locations
	 * @param codes the codes
	 * @param locLevel the loc level
	 * @return the states n district
	 */
	private void getStatesNDistrict(List<? extends TplSpecLoc> locations,
			List<String> codes, String locLevel) {

		List<PmState> states = new ArrayList<PmState>();
		List<PmDistrict> districts = new ArrayList<PmDistrict>();
		List<PmDivision> divisions = new ArrayList<PmDivision>();
		if (null != locations) {
			for (TplSpecLoc specLoc : locations) {
				if (ParameterConst.LOCATION_LEVEL_STATE
						.equalsIgnoreCase(locLevel)) {
					PmState state = specLoc.getPmState();
					if (null != state) {
						if (!codes.contains(state.getStateCode())) {
							codes.add(state.getStateCode());
							states.add(state);
						}
					}
					this.getManagedBean().setSupplierStateCode(codes);
					if (null == this.getManagedBean().getStateList()) {
						this.getManagedBean().setStateList(
								new ArrayList<PmState>());
					}
					this.getManagedBean().setStateList(states);

				} else if (ParameterConst.LOCATION_LEVEL_DISTRICT
						.equalsIgnoreCase(locLevel)) {
					PmDistrict district = specLoc.getPmDistrict();
					if (null != district) {
						if (!codes.contains(district.getDistrictCode())) {
							codes.add(district.getDistrictCode());
							districts.add(district);
						}
					}
					this.getManagedBean().setSupplierDistrictCode(codes);
					if (null == this.getManagedBean().getDistrictList()) {
						this.getManagedBean().setDistrictList(
								new ArrayList<PmDistrict>());
					}
					this.getManagedBean().setDistrictList(districts);

				} else if (ParameterConst.LOCATION_LEVEL_DIVISION
						.equalsIgnoreCase(locLevel)) {
					PmDivision divison = specLoc.getPmDivision();
					if (null != divison) {
						if (!codes.contains(divison.getDivisionCode())) {
							codes.add(divison.getDivisionCode());
							divisions.add(divison);
						}
					}
					this.getManagedBean().setSupplierDivisionCode(codes);
					if (null == this.getManagedBean().getDivisionList()) {
						this.getManagedBean().setDivisionList(
								new ArrayList<PmDivision>());
					}
					this.getManagedBean().setDivisionList(divisions);
				}
			}
		}
	}

	/**
	 * Construct l3 ids.
	 *
	 * @param categories the categories
	 * @return the list
	 */
	private List<Long> constructL3Ids(List<ScQtCategory> categories) {
		List<Long> categoryIds = null;
		if (null != categories) {
			categoryIds = new ArrayList<Long>();
			for (ScQtCategory category : categories) {
				Long code = category.getPmCategoryL3().getCategoryL3Id();
				categoryIds.add(code);
			}
		}
		return categoryIds;
	}

	// View Circular Attachmnets Download Method
	/**
	 * Download circular dtls.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
/*	public void downloadCircularDtls() throws IOException {
		debugLogger("downloadCircularDtls");
		List<CtPanelItem> itemList = this.getManagedBean().getSelectpanelItem();
		itemList = new ArrayList<CtPanelItem>();
		itemList.add(this.getManagedBean().getCtPanelItem());

		String filePath = this.getManagedBean().getCtCircularVer()
				.getCircularFilePath();
		String fileName = this.getManagedBean().getCtCircularVer()
				.getCircularFileName();

		String docRepoBaseDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		StringBuilder actualFilePath = new StringBuilder(docRepoBaseDir);
		actualFilePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		actualFilePath.append(filePath);

		downloadFile(actualFilePath.toString(), fileName);
		this.getManagedBean().setSelectpanelItem(itemList);
	}*/
	
	public String viewCircularAttachments(String type) {
		List<CtPanelItem> itemList = this.getManagedBean().getSelectpanelItem();
		itemList = new ArrayList<CtPanelItem>();
		itemList.add(this.getManagedBean().getCtPanelItem());
		CtCircularVer ctCircularVer = this.getManagedBean().getCtCircularVer();
		String pageView = "";
		if (null != ctCircularVer) {
			PanelBackingBean panelbackingbean = new PanelBackingBean();
			panelbackingbean.getManagedBean().setDifferentModule(true);
			if (SourcingConstants.FLOW_TYPE_BO.equalsIgnoreCase(type)) {
				panelbackingbean.getManagedBean().setGotoPreviewPage("/xhtml/sc/specification/specificationCreation.xhtml");
			}
			if (SourcingConstants.FLOW_TYPE_SPEC_COMMITTE.equalsIgnoreCase(type)) {
				panelbackingbean.getManagedBean().setGotoPreviewPage("/xhtml/sc/specification/specificationCommitteeView.xhtml");
			}
			pageView = panelbackingbean.gotoCommonViewPage(ctCircularVer.getCircularVerId());
		}
		this.getManagedBean().setSelectpanelItem(itemList);
		return pageView;
	}

	/**
	 * Retrive supplier dtls.
	 *
	 * @param panelSupplier the panel supplier
	 */
	public void retriveSupplierDtls(CtPanelSupplier panelSupplier) {
		this.getManagedBean().setCtpanelSupplier(panelSupplier);
		if (null != panelSupplier.getSupplier()
				&& null != panelSupplier.getSupplier().getSmAppl()
				&& null != panelSupplier.getSupplier().getSmAppl()
						.getSmPersonnels()) {
			List<SmPersonnel> list = panelSupplier.getSupplier().getSmAppl()
					.getSmPersonnels();
			if (null != list) {
				for (SmPersonnel smPersonnel : list) {
					getManagedBean().setPersonnel(smPersonnel);
				}
				SmPersonnel supplierHQPersonnel = this.getManagedBean()
						.getPersonnel();
				getManagedBean().setSupplierContact("");
				getManagedBean().setSupplierEmail("");
				if (null != supplierHQPersonnel) {
					getManagedBean().setSupplierContact(
							supplierHQPersonnel.getName());
					getManagedBean().setSupplierEmail(
							supplierHQPersonnel.getEmail());
				}

				SmCompanyBasic smCompanyBasic = panelSupplier.getSupplier()
						.getSmAppl().getSmCompanyBasic();
				if (null != smCompanyBasic) {
					if (null != smCompanyBasic.getSmCompanyAddresses()) {
						for (SmCompanyAddress smCompanyAddress : smCompanyBasic
								.getSmCompanyAddresses()) {
							if (null != smCompanyAddress) {
								if (AddressType.CONTACT_ADDRESS.value().equals(
										smCompanyAddress.getAddressType())) {
									getManagedBean().setSupplierBranch("HQ");
									getManagedBean().setSupplierAddress(
											smCompanyAddress.getSmAddress());
									break;
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Check all conditions.
	 *
	 * @param event the event
	 */
	public void checkAllConditions(AjaxBehaviorEvent event) {
		debugLogger("checkAllCondition");
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<TreeNode> list = getManagedBean().getZoneTrees();
		if (null != list) {
			for (TreeNode node : list) {
				for (TreeNode childNode : node.getChildren()) {
					if (childNode.getChildCount() > 0) {
						for (TreeNode node2 : childNode.getChildren()) {
							node2.setSelected(flag);
						}
					}
					childNode.setSelected(flag);
				}
			}
			getManagedBean().setZoneTrees(list);
		} else {
			
		}
	}
	
	/**
	 * Retriev location dtls.
	 *
	 * @param scQtLocCriteriaList the sc qt loc criteria list
	 */
	private void retrievLocationDtls(List<ScQtLocCriteria> scQtLocCriteriaList) {
		log.info("retrievLocationDtls - START");
		List<PmState> states = SourcingLocationHelper.getStateFromLocationDTO(scQtLocCriteriaList);
		List<PmDivision> divisions = SourcingLocationHelper.getDivisionFromLocationDTO(scQtLocCriteriaList);
		List<PmDistrict> districts = SourcingLocationHelper.getDistrictFromLocationDTO(scQtLocCriteriaList);
		List<PmCity> cities = SourcingLocationHelper.getCityFromLocationDTO(scQtLocCriteriaList);
		/*if (getManagedBean().getIndex() == -1) {
			getManagedBean().setZoneTrees(new ArrayList<TreeNode>());
		}*/
		getManagedBean().setZoneTrees(new ArrayList<TreeNode>());
		//List<TreeNode> localList = new ArrayList<TreeNode>();
		if (ParameterConst.LOCATION_LEVEL_STATE.equalsIgnoreCase(this.getManagedBean().getLocalLevelCode())) {
			/*if (getManagedBean().getIndex() >= 0) {
				getManagedBean().getZoneTrees().remove(
						getManagedBean().getIndex());
			}*/
			if (null != states && !states.isEmpty()) {
				for (PmState state : states) {
					List<PmState> localStateList = new ArrayList<PmState>();
					localStateList.add(state);
					/*if (getManagedBean().getIndex() == -1) {*/

						getManagedBean().getZoneTrees().add(
								LocationSingleton.getInstance()
										.buildLocationTree(null, localStateList,
												districts, divisions, cities,
												ScQtLocCriteria.class));
/*					}
					if (getManagedBean().getIndex() >= 0) {

						localList.add(LocationSingleton.getInstance()
								.buildLocationTree(null, localStateList,
										districts, divisions, cities,
										ScQtLocCriteria.class));
					}*/
				}
				//getManagedBean().getZoneTrees().addAll(localList);
			}
		} else if (ParameterConst.LOCATION_LEVEL_DISTRICT.equalsIgnoreCase(this.getManagedBean().getLocalLevelCode())) {
			/*if (getManagedBean().getIndex() >= 0) {

				getManagedBean().getZoneTrees().remove(
						getManagedBean().getIndex());
			}*/
			if (null != districts && !districts.isEmpty()) {
				Map<PmState, Map<PmDivision, Map<PmDistrict, List<PmCity>>>> mapStatesDivisionsDistrictsCities = LocationSingleton
						.getInstance().getUniqueStatesDivisionsDistrictsCities(districts, null);

				Set<PmState> statesDivisionsDistrictsCitiesKeySet = mapStatesDivisionsDistrictsCities.keySet();
				Map<PmState, Map<PmDistrict, List<PmCity>>> mapStatesDistrictsCities = LocationSingleton
						.getInstance().getUniqueStateDistrictsCities(districts, null);
				Set<PmState> statesDistrictsCitiesKeySet = mapStatesDistrictsCities.keySet();
				if (statesDivisionsDistrictsCitiesKeySet != null && !statesDivisionsDistrictsCitiesKeySet.isEmpty()) {
					for (PmState state : statesDivisionsDistrictsCitiesKeySet) {
						Map<PmDivision, Map<PmDistrict, List<PmCity>>> mapDivisionsDistrictsCities = mapStatesDivisionsDistrictsCities
								.get(state);
						Set<PmDivision> divisionsDistrictsCitiesKeySet = mapDivisionsDistrictsCities
								.keySet();
						if (divisionsDistrictsCitiesKeySet != null && !divisionsDistrictsCitiesKeySet.isEmpty()) {
							divisions.clear();
							districts.clear();
							for (PmDivision division : divisionsDistrictsCitiesKeySet) {
								division.setPmState(state);
								Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapDivisionsDistrictsCities.get(division);
								Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities.keySet();
								if (districtsCitiesKeySet != null && !districtsCitiesKeySet.isEmpty()) {
									for (PmDistrict district : districtsCitiesKeySet) {
										districts.add(district);
									}
								}
							}
							/*if (getManagedBean().getIndex() == -1) {*/
								getManagedBean().getZoneTrees().add(
										LocationSingleton.getInstance()
												.buildLocationTree(null,
														null, districts,
														divisions, cities,
														ScQtLocCriteria.class));
							/*}
							if (getManagedBean().getIndex() >= 0) {

								localList.add(LocationSingleton.getInstance()
										.buildLocationTree(null, states,
												districts, divisions, cities,
												ScQtLocCriteria.class));
							}*/
						}
					}
					//getManagedBean().getZoneTrees().addAll(localList);
				}
				if (statesDistrictsCitiesKeySet != null && !statesDistrictsCitiesKeySet.isEmpty()) {
					for (PmState state : statesDistrictsCitiesKeySet) {
						divisions.clear();
						Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapStatesDistrictsCities.get(state);
						Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities.keySet();
						if (districtsCitiesKeySet != null && !districtsCitiesKeySet.isEmpty()) {
							districts.clear();
							for (PmDistrict district : districtsCitiesKeySet) {
								district.setPmState(state);
								districts.add(district);
							}
						}
						/*if (getManagedBean().getIndex() == -1) {*/
							getManagedBean().getZoneTrees().add(
									LocationSingleton.getInstance()
											.buildLocationTree(null, null,
													districts, divisions,
													cities,
													ScQtLocCriteria.class));
						/*}
						if (getManagedBean().getIndex() >= 0) {

							localList.add(LocationSingleton.getInstance()
									.buildLocationTree(null, states, districts,
											divisions, cities,
											ScQtLocCriteria.class));
						}*/
					}
					//getManagedBean().getZoneTrees().addAll(localList);
				}
			}
		} else if (ParameterConst.LOCATION_LEVEL_DIVISION.equalsIgnoreCase(this.getManagedBean().getLocalLevelCode())) {
			/*if (getManagedBean().getIndex() >= 0) {

				getManagedBean().getZoneTrees().remove(
						getManagedBean().getIndex());
			}*/
			if (null != divisions && !divisions.isEmpty()) {
				Map<PmState, List<PmDivision>> mapStatesDivision = LocationSingleton.getInstance().getUniqueStatesDivision(divisions);
				Set<PmState> statesDivisionKeySet = mapStatesDivision.keySet();
				if (statesDivisionKeySet != null && !statesDivisionKeySet.isEmpty()) {
					for (PmState state : statesDivisionKeySet) {
						divisions.clear();
						List<PmDivision> divisions1 = mapStatesDivision.get(state);
						if (divisions1 != null && !divisions1.isEmpty()) {
							for (PmDivision division : divisions1) {
								division.setPmState(state);
								divisions.add(division);
							}
							/*if (getManagedBean().getIndex() == -1) {*/
								getManagedBean().getZoneTrees().add(
										LocationSingleton.getInstance()
												.buildLocationTree(null,
														states, districts,
														divisions, cities,
														ScQtLocCriteria.class));
							/*}
							if (getManagedBean().getIndex() >= 0) {

								localList.add(LocationSingleton.getInstance()
										.buildLocationTree(null, states,
												districts, divisions, cities,
												ScQtLocCriteria.class));
							}*/
						}
					}
					//getManagedBean().getZoneTrees().addAll(localList);
				}
			}
		}
		/*this.getManagedBean().setIndex(-1);*/
		log.info("retrievLocationDtls - END");
	}

}
