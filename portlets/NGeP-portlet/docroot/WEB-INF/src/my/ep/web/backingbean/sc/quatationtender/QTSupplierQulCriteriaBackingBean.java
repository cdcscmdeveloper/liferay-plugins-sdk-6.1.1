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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
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
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
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
import my.ep.type.pm.PTJLocalityCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.ct.circular.panel.PanelBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.SupplierSearchBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.beanutils.BeanUtils;
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
		debugLogger("populateSupplierSearchCriteria");
		if (supplierSearchBean != null) {
			List<ScQtCategory> scQtCategoryList = getManagedBean().getQtCategoryList();
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
	 * 
	 * @param pmCategoryL3 the pmCategoryL3 instance
	 */
	public void addNewCategoryAction(PmCategoryL3 pmCategoryL3) {
		debugLogger("addNewCategoryAction");
		List<ScQtCategory> qtCategoryList = this.getManagedBean()
				.getQtCategoryList();	
		if (qtCategoryList == null) {
			qtCategoryList = new ArrayList<ScQtCategory>();
			this.getManagedBean().setQtCategoryList(qtCategoryList);
		}
		validateDuplicateCategoryCode(pmCategoryL3, qtCategoryList);	
		PmCategoryL2 pmCategoryL2 = pmCategoryL3.getPmCategoryL2();
		ScQtCategory category = new ScQtCategory();
		category.setScQt(this.getManagedBean().getQt());
		category.setPmCategoryL1(pmCategoryL2.getPmCategoryL1());
		category.setPmCategoryL2(pmCategoryL2);
		category.setPmCategoryL3(pmCategoryL3);
		qtCategoryList.add(category);
	}

	

	
	/**
	 * To remove categoryCode from data table
	 */
	public void deleteCategoryCodes() {
		debugLogger("deleteCategoryCodes");
		List<ScQtCategory> removeList = this.getManagedBean().getTobeRemoveCategoryCodes();
		if (removeList == null) {
			removeList = new ArrayList<ScQtCategory>();
		}
		List<ScQtCategory> openList = this.getManagedBean().getQtCategoryList();
		if (null == openList) {
			openList = new ArrayList<ScQtCategory>();
		}
		List<ScQtCategory> openSelectList = this.getManagedBean()
				.getSelectedCategoryList();
		if (null == openSelectList || openSelectList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_760146);
		}
		for (ScQtCategory category : openSelectList) {
			openList.remove(category);
			removeList.add(category);
		}
		this.getManagedBean().getQt().setScQtCategorys(openList);
		this.getManagedBean().setTobeRemoveCategoryCodes(removeList);
	}
	
	/**
	 * Select zone tree node.Event handle check non panel Treenodes.
	 * 
	 * @param event
	 *            (TreeNode treeNode)
	 */
	public void selectZoneTreeNode(NodeSelectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(true);
	}

	/**
	 * Un select zone tree node.Event handle uncheck Nonpanel treeNodes.
	 * 
	 * @param event
	 *            (TreeNode treeNode).
	 */
	public void unSelectZoneTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(false);
	}

	/**
	 * To retrive districts based on state value changes.
	 *
	 * @param changeEvent the change event
	 */
	public void handleStateChange(ValueChangeEvent changeEvent) {
		debugLogger("handleStateChange");
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

	public void onTabChangeAction(TabChangeEvent event) {
		debugLogger("onTabChangeAction");
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
					QuationTenderResDTO resDTO = qtDetailsSO.retriveQualifiedRestrictedSuppliers(reqDTO);
					List<ScQtSupplier> restrictedSuppleirs = resDTO.getRestrictedSuppliers();
					getManagedBean().getQtSupplierList().addAll(restrictedSuppleirs);
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
		List<ScQtCategory> scQtCategoryList = getManagedBean().getQtCategoryList();
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
		getManagedBean().setAutoUpdate(false);
	}
	
	/**
	 * Method to search the supplier for Restricted Tender.
	 */
	public void searchRestrictedSupplierAction() {
		debugLogger("searchRestrictedSupplierAction");
		boolean hasSearchValue = false;
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
		if(!hasSearchValue) {
			throw new EpException(MessageConst.ERROR_760231);
		} else {		
			getManagedBean().getSearchSupplierDataModel().reloadData();
		}
	}

	
	public SmMofAccount retrieveSmMofAccountFromSmSupplier(SmSupplier smSupplier) {
		debugLogger("retrieveSmMofAccountFromSmSupplier");
		SmMofAccount smMofAccount = null;
		if (null != smSupplier) {
			log.info("(smSupplier.getSupplierId()) : " + smSupplier.getSupplierId());
			smMofAccount = smSupplier.getSmMofAccount();
			if(smMofAccount != null){
				return smMofAccount;
			}
			IMofAccountSO mofAccountSO;
			try {
				mofAccountSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofAccountSO.class);
				MofAccountReqDTO accountReqDTO = new MofAccountReqDTO();
				accountReqDTO.setSupplierId(smSupplier.getSupplierId());
				accountReqDTO.setSupplierType(ParameterConst.SUPPLIER_TYPE_CONTRACTOR);
				MofAccountResDTO mofAccountResDTO = mofAccountSO.retriveMOFAccountBySupplierIdAndType(accountReqDTO);
				if (mofAccountResDTO.getMofAccount() != null) {
					smMofAccount = mofAccountResDTO.getMofAccount();
					smSupplier.setSmMofAccount(smMofAccount);
				}
			} finally {
				mofAccountSO = null;
			}
			
			return smMofAccount;
		} else {
			smMofAccount = new SmMofAccount();
		}
		return smMofAccount;
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
			log.info("(smSupplier.getSupplierId()) : " + smSupplier.getSupplierId());
			supplierId = smSupplier.getSupplierId();
			log.info("supplierId : " + supplierId);
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
	
	public boolean supplierStatusDateComparision(SmMofAccount mofAccount) {
		debugLogger("supplierStatusDateComparision");
		Date enddate = mofAccount.getExpDate();
		boolean isDate = false;
		
		Date d1 = new Date();

		if (null != enddate) {
			long days = DateUtils.getDaysDifference(d1, enddate);
			int day = (int) days;
			if (day > 0) {
				isDate = true;
			}
		}
		return isDate;
	}

	/**
	 * Adds the restricted to supplier list action.
	 */
	public void addRestrictedToSupplierListAction() {
		debugLogger("addRestrictedToSupplierListAction");
		this.getManagedBean().setRequired(Boolean.FALSE);
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
				Date presentDate = new Date();
				if (null != smSupplier.getSmMofAccount() && null != smSupplier.getSmMofAccount().getExpDate()) {
					Date expDate = smSupplier.getSmMofAccount().getExpDate();
					long days = DateUtils.getDaysDifference(presentDate, expDate);
					int day = (int) days;
					if (day > 0) {
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
					} else {
						throw new EpException(MessageConst.ERROR_760300);
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
	 * Supplier Qualification Criteria All States Select Or DeSelect Methods.
	 *
	 * @param event
	 */
	public void stateChange(AjaxBehaviorEvent event) {
		debugLogger("stateChange");
		boolean state = (boolean) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().getQt().getScQtSupplierCriteria()
				.setAllState(state);
		this.getManagedBean().setPaidupcapital(true);
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
			this.getManagedBean().setSupplierLocationList(getManagedBean().getExistingSupplierLocationList());
			this.getManagedBean().setZoneTrees(null);
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
	 * Gets the locations.convert Treenode to Location Criteria objects in
	 * supplier qualification criteria.
	 * 
	 * @param zoneLocation
	 *            (TreeNode zoneLocation)
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
	 * Gets the zone locations. convert Treenode to TplSpecZoneLoc objects in
	 * general information.
	 * 
	 * @param zoneLocation
	 *            (TreeNode zoneLocation)
	 * @return List<TplSpecZoneLoc>
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
		debugLogger("editZoneLocation");
		IParameterSO parameterSo = null;
		ICountrySO countrySo = null;
		try {
			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			
			List<ScQtLocCriteria> locationDTOs = new ArrayList<ScQtLocCriteria>();
			if (getManagedBean().getZoneTrees() != null) {
				for (TreeNode treeNode : getManagedBean().getZoneTrees()) {
					List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
					locationDTOs.addAll(locationDTO);
				}
			}
			
			//List<ScQtLocCriteria> locationDTOs = getLocations(rootZoneLocation);
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
			// int i = index;
			// this.getManagedBean().setIndex(i);
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
	 * Update supplier locations.Event handle add Locations to Supplier
	 * qualification criteria menu.
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
				treeNode.setSelected(false);
			}
			getManagedBean().setSelectedNodes(null);
		}
		retrievLocationDtls(locationDTOs);
	}

	/**
	 * To delete the selected Restricted Supplier from list..
	 */
	public void deleteRestrictedSupplierList() {
		debugLogger("deleteRestrictedSupplierList");
		this.getManagedBean().setAutoUpdate(true);
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
		TplSpecZoneDtl tplSpecZoneDtl = null;
		selectedTreeNode.setSelected(true);
		if (null != selectedTreeNode) {
			tplSpecZoneDtl = retriveTplSpecZoneDtls(selectedTreeNode);

		}
		QTSupplierReqDTO supplierReqDTO = new QTSupplierReqDTO();
		if (null != tplSpecZoneDtl) {
			this.getManagedBean().setTplSpecZoneDtl(tplSpecZoneDtl);
			supplierReqDTO.setCircularZoneId(tplSpecZoneDtl.getPanelZone()
					.getPanelZoneId());
		}
		
		/*TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(true);
		TplSpecZoneDtl ctPanelzone = (TplSpecZoneDtl) event.getTreeNode()
				.getData();
		QTSupplierReqDTO supplierReqDTO = new QTSupplierReqDTO();*/
		IQTSupplierSO supplierSo = (IQTSupplierSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSupplierSO.class);
		QTSupplierResDTO supplierResDTO = new QTSupplierResDTO();
		supplierResDTO = supplierSo
				.retrievePanelSuppliersByCircularZoneId(supplierReqDTO);
		this.getManagedBean().setCtZoneSupplierList(
				new ArrayList<CtPanelSupplier>());
		this.getManagedBean().setTotalSuppliers(0);

		if (null != supplierResDTO.getCtPanelSuppliers()
				&& !supplierResDTO.getCtPanelSuppliers().isEmpty()) {

			this.getManagedBean().setTotalSuppliers(
					supplierResDTO.getCtPanelSuppliers().size());

			this.getManagedBean().setCtZoneSupplierList(
					supplierResDTO.getCtPanelSuppliers());
			List<CtPanelItem> itemList = this.getManagedBean()
					.getPanelItemList();
			itemList = new ArrayList<CtPanelItem>();
			itemList.add(this.getManagedBean().getCtPanelItem());

			this.getManagedBean().setPanelItemList(itemList);
		}
	}

	/**
	 * Retrive tpl spec zone dtls.
	 * 
	 * @param selectedTreeNode
	 *            the selected tree node
	 * @return the tpl spec zone dtl
	 */
	private TplSpecZoneDtl retriveTplSpecZoneDtls(TreeNode selectedTreeNode) {
		TplSpecZoneDtl tplSpecZoneDtl = null;
		List<TreeNode> zoneNodes = selectedTreeNode.getChildren();

		if (null != zoneNodes && zoneNodes.size() > 0) {
			for (TreeNode treeNode1 : zoneNodes) {

				List<TreeNode> stateNodes = treeNode1.getChildren();

				for (TreeNode treeNode2 : stateNodes) {

					List<TreeNode> district = treeNode2.getChildren();
					if (null != district && !district.isEmpty()) {
						for (TreeNode devision : district) {

							if (devision.getChildCount() > 0) {
								for (TreeNode districtNode : devision
										.getChildren()) {

									TplSpecZoneLoc criteria = (TplSpecZoneLoc) districtNode
											.getData();

									criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) selectedTreeNode
											.getData()));
									tplSpecZoneDtl = criteria
											.getTplSpecZoneDtl();

								}

							} else {

								TplSpecZoneLoc criteria = (TplSpecZoneLoc) devision
										.getData();

								criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) selectedTreeNode
										.getData()));
								tplSpecZoneDtl = criteria.getTplSpecZoneDtl();

							}

						}
					} else {

						TplSpecZoneLoc criteria = (TplSpecZoneLoc) treeNode2
								.getData();

						criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) selectedTreeNode
								.getData()));
						tplSpecZoneDtl = criteria.getTplSpecZoneDtl();

					}

				}
				if (null != stateNodes && stateNodes.size() == 0) {
					TplSpecZoneLoc criteria = (TplSpecZoneLoc) treeNode1
							.getData();
					criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) selectedTreeNode
							.getData()));
					tplSpecZoneDtl = criteria.getTplSpecZoneDtl();
				}

			}
		}
		return tplSpecZoneDtl;
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
	 * Delete supplier qualification tree node.Event handle delete Locations
	 * from supplier qualification criteria.
	 */
	public void deleteSupplierQualificationTreeNode() {
		debugLogger("deleteSupplierQualificationTreeNode");
		try {
			List<TreeNode> rootTrees = getManagedBean().getZoneTrees();
			deleteTreeNodes(rootTrees);
			this.getManagedBean().setTotalSuppliersSpecLoc(new ArrayList<TplSpecLoc>());
			List<ScQtLocCriteria> CriteriaNew = new ArrayList<ScQtLocCriteria>();
			if (!rootTrees.isEmpty()) {
				List<TplSpecLoc> localLoc = new ArrayList<TplSpecLoc>();
				List<ScQtLocCriteria> Criteria = new ArrayList<ScQtLocCriteria>();
				Criteria = convertTreeNodesToLocationCriteria(rootTrees, this
						.getManagedBean().getQt().getScQtSupplierCriteria());
				for (ScQtLocCriteria locCriteria : Criteria) {
					ScQtLocCriteria criteriaNew = (ScQtLocCriteria) BeanUtils
							.cloneBean(locCriteria);
					CriteriaNew.add(criteriaNew);
					TplSpecLoc specLoc = (TplSpecLoc) locCriteria;
					localLoc.add(specLoc);
				}
				this.getManagedBean().setTotalSuppliersSpecLoc(localLoc);
			}
			checkExistingLocation(CriteriaNew, this.getManagedBean()
					.getExistingSupplierLocationList());
			this.getManagedBean().setIndex(-1);
		} catch (IllegalAccessException e) {
			log.error(e);
		} catch (InstantiationException e) {
			log.error(e);
		} catch (InvocationTargetException e) {
			log.error(e);
		} catch (NoSuchMethodException e) {
			log.error(e);
		}
	}

	/**
	 * @param criteriaNew
	 * @param existingSupplierLocationList
	 */
	private void checkExistingLocation(List<ScQtLocCriteria> currentLocationList,
			List<ScQtLocCriteria> existingLocations) {
		debugLogger("checkExistingLocation");
		List<ScQtLocCriteria> localDeleteList = this.getManagedBean()
				.getSupplierLocationList();
		if (null == localDeleteList) {
			localDeleteList = new ArrayList<ScQtLocCriteria>();
		}
		if (null != existingLocations && !existingLocations.isEmpty()
				&& null != currentLocationList
				&& !currentLocationList.isEmpty()) {
			for (ScQtLocCriteria locDeleteCriteria : existingLocations) {
				boolean delectLoc = false;
				for (ScQtLocCriteria locCurrentList : currentLocationList) {
					if (null != locCurrentList.getSpecLocId()) {
						if (locCurrentList.getSpecLocId().equals(
								locDeleteCriteria.getSpecLocId())) {
							delectLoc = true;
							break;
						}
					} else {
						delectLoc = true;
					}
				}
				if (!delectLoc) {
					if (!localDeleteList.contains(locDeleteCriteria)) {
						localDeleteList.add(locDeleteCriteria);
					}
				}
			}
			this.getManagedBean().setSupplierLocationList(localDeleteList);
		}
		if (null == currentLocationList || currentLocationList.isEmpty()) {
			if (null != existingLocations && !existingLocations.isEmpty()) {
				this.getManagedBean()
						.setSupplierLocationList(existingLocations);
			}
		}
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
	 * Gets the dropdown location values.display the Location level dropdown
	 * values.
	 * 
	 * @param locationLevel
	 *            the location level
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
		if (this.getManagedBean().getAllStateCode() == PTJLocalityCode.STATE_SARAWAK) {
			types.add(ParameterConst.LOCATION_LEVEL_DIVISION);
		}
		types.add(ParameterConst.LOCATION_LEVEL_DISTRICT);

		SelectItem[] items = null;
		try {
			items = new SelectItem[types.size() + 1];
			items[0] = new SelectItem(null, 
					ResourceBundleUtils.getMessage(
							"lbl.txt.select", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
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
	}

	/**
	 * Show supplier details.retrive suupplier address based on supplier in
	 * restricted tender tab
	 * 
	 * @param qtSupplier
	 *            (ScQtSupplier qtSupplier).
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
					getManagedBean().setSupplierBranch(QTConstants.SUPPLIER_INFO_HQ);
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
	 * Select location tree node.Event handle check treeNode Locations in
	 * Supplier qualification criteria .
	 * 
	 * @param event
	 *            (TreeNode treeNode).
	 */
	public void selectLocationTreeNode(NodeSelectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(true);
	}

	/**
	 * Un select location tree node.Event handle Uncheck treeNode Locations in
	 * Supplier qualification criteria .
	 * 
	 * @param event
	 *            (TreeNode treeNode).
	 */
	public void unSelectLocationTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(false);
	}

	/**
	 * Search for supplier count.search for Total suppliers in supplier
	 * qualification criteria.
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
		if (ParameterConst.PROCUREMENT_MODE_TENDER.equals(scQt.getProcurementMode().getParameterCode())) {
			// value > 500k - always get the
			// scQtSupplierCriteria.getLocationLevel()
			if (getManagedBean().getAllStateCode() != PTJLocalityCode.STATE_PENINSULAR
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
	 * @param categories
	 *            the categories
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

	/**
	 * View circular attachments.
	 * 
	 * @param type
	 *            (String type)
	 * @return navigate to ctcircular page.
	 */
	public String viewCircularAttachments(String backUrl) {
		debugLogger("viewCircularAttachments");
		List<CtPanelItem> itemList = this.getManagedBean().getPanelItemList();
		itemList = new ArrayList<CtPanelItem>();
		itemList.add(this.getManagedBean().getCtPanelItem());
		CtCircularVer ctCircularVer = this.getManagedBean().getQt().getCircularVer();
		String pageView = "";
		if (null != ctCircularVer) {
			PanelBackingBean panelbackingbean = new PanelBackingBean();
			panelbackingbean.getManagedBean().setDifferentModule(true);
			panelbackingbean.getManagedBean().setGotoPreviewPage(backUrl);
			pageView = panelbackingbean.gotoCommonViewPage(ctCircularVer.getCircularVerId());
		}
		this.getManagedBean().setPanelItemList(itemList);
		return pageView;
	}

	/**
	 * Retrive supplier details.retrive supplier information based on supplier in
	 * supplier qualification criteria.
	 * 
	 * @param panelSupplier
	 *            (CtPanelSupplier panelSupplier).
	 */
	public void retriveSupplierDtls(CtPanelSupplier panelSupplier) {
		
		QTSupplierResDTO qtSupplierResDTO = new QTSupplierResDTO();
		MofAccountResDTO mofAccResDTO = new MofAccountResDTO();
		IQTSupplierSO qtSupplierSO = null;
		IMofAccountSO mofAccountSO = null;

		try {
			if (panelSupplier.getSupplier() != null) {
				qtSupplierSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSupplierSO.class);
				QTSupplierReqDTO qtSupplierReqDTO = new QTSupplierReqDTO();
				qtSupplierReqDTO.setSupplierId(panelSupplier.getSupplier()
						.getSupplierId());
				qtSupplierResDTO = qtSupplierSO
						.retrieveSupplierDetails(qtSupplierReqDTO);
				panelSupplier.setSupplier(qtSupplierResDTO.getSmSupplier());
				// getManagedBean().setSmSupplier(smSupplier);
				// this.getManagedBean().setCtpanelSupplier(panelSupplier);

				mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
								IMofAccountSO.class);
				MofAccountReqDTO mofAccReqDTO = new MofAccountReqDTO();
				mofAccReqDTO.setSupplierId(panelSupplier.getSupplier()
						.getSupplierId());
				mofAccResDTO = mofAccountSO
						.retrieveMofNoBySupplierId(mofAccReqDTO);
				getManagedBean().setSmMofAccount(mofAccResDTO.getMofAccount());

				SmCompanyBasic smCompanyBasic = panelSupplier.getSupplier()
						.getSmAppl().getSmCompanyBasic();
				// getManagedBean().setSmCompanyBasic(smCompanyBasic);

				List<SmPersonnel> smPersonnelList = panelSupplier.getSupplier()
						.getSmAppl().getSmPersonnels();
				if (null != smPersonnelList) {
					for (SmPersonnel smPersonnel : smPersonnelList) {
						if (smPersonnel.isContactPerson()) {
							// getManagedBean().setSmPersonnel(smPersonnel);
							getManagedBean().setPersonnel(smPersonnel);

							break;
						}
					}
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
				if (null != smCompanyBasic) {
					for (SmCompanyAddress smCompanyAddress : smCompanyBasic
							.getSmCompanyAddresses()) {
						if (AddressType.CONTACT_ADDRESS.value().equals(
								smCompanyAddress.getAddressType())) {
							getManagedBean().setSupplierBranch(
									QTConstants.SUPPLIER_INFO_HQ);
							getManagedBean().setSupplierAddress(
									smCompanyAddress.getSmAddress());
							break;
						}
					}
				}
				this.getManagedBean().setCtpanelSupplier(panelSupplier);
			}
		} finally {
			qtSupplierSO = null;
			mofAccountSO = null;
		}
		/*this.getManagedBean().setCtpanelSupplier(panelSupplier);
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
									getManagedBean().setSupplierBranch(QTConstants.SUPPLIER_INFO_HQ);
									getManagedBean().setSupplierAddress(
											smCompanyAddress.getSmAddress());
									break;
								}
							}
						}
					}
				}
			}
		}*/
	}
	
	/**
	 * Method to check all the state/districts in data table.
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
	 * Method to check all the state/districts in data table in dialog.
	 *
	 * @param event the event
	 */
	public void checkAllPopUpConditions(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		TreeNode treeNode = getManagedBean().getRootLocationTree();
		if (treeNode != null) {
			for (TreeNode stateNode : treeNode.getChildren()) {
				if (stateNode.getChildCount() > 0) {
					for (TreeNode districtNode : stateNode.getChildren()) {
						districtNode.setSelected(flag);
					}
				}
				stateNode.setSelected(flag);
			}
			getManagedBean().setRootLocationTree(treeNode);
		} else {
			
		}
	}
	
	/**
	 * Retriev location details.
	 *
	 * @param scQtLocCriteriaList the sc qt loc criteria list
	 */
	private void retrievLocationDtls(List<ScQtLocCriteria> scQtLocCriteriaList) {
		log.info("retrievLocationDtls - START");
		List<PmState> states = SourcingLocationHelper.getStateFromLocationDTO(scQtLocCriteriaList);
		List<PmDivision> divisions = SourcingLocationHelper.getDivisionFromLocationDTO(scQtLocCriteriaList);
		List<PmDistrict> districts = SourcingLocationHelper.getDistrictFromLocationDTO(scQtLocCriteriaList);
		List<PmCity> cities = SourcingLocationHelper.getCityFromLocationDTO(scQtLocCriteriaList);
		getManagedBean().setZoneTrees(new ArrayList<TreeNode>());
		if (ParameterConst.LOCATION_LEVEL_STATE.equalsIgnoreCase(this.getManagedBean().getLocalLevelCode())) {
			if (null != states && !states.isEmpty()) {
				for (PmState state : states) {
					List<PmState> localStateList = new ArrayList<PmState>();
					localStateList.add(state);
					getManagedBean().getZoneTrees().add(
							LocationSingleton.getInstance()
									.buildLocationTree(null, localStateList,
											districts, divisions, cities,
											ScQtLocCriteria.class));
				}
			}
		} else if (ParameterConst.LOCATION_LEVEL_DISTRICT.equalsIgnoreCase(this.getManagedBean().getLocalLevelCode())) {
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
							getManagedBean().getZoneTrees().add(
									LocationSingleton.getInstance()
											.buildLocationTree(null,
													null, districts,
													divisions, cities,
													ScQtLocCriteria.class));
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
								district.setPmState(state);
								districts.add(district);
							}
						}
						getManagedBean().getZoneTrees().add(
								LocationSingleton.getInstance()
										.buildLocationTree(null, null,
												districts, divisions,
												cities,
												ScQtLocCriteria.class));
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
								division.setPmState(state);
								divisions.add(division);
							}
							getManagedBean().getZoneTrees().add(
									LocationSingleton.getInstance()
											.buildLocationTree(null,
													states, districts,
													divisions, cities,
													ScQtLocCriteria.class));
						}
					}
				}
			}
		}
		log.info("retrievLocationDtls - END");
	}

	public void validatePaidupcapital() {
		debugLogger("validatePaidupcapital");
		this.getManagedBean().setPaidupcapital(true);
	}
}
