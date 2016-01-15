/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.simplequote;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.sc.directpurchase.IQuoteSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sc.directpurchase.QuoteReqDTO;
import my.ep.dto.sc.directpurchase.QuoteResDTO;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.directpurchase.ScBusinessNatureQuoteCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationDistrictCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationDivisionCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationStateCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteSupplier;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.TreeNode;

// TODO: Auto-generated Javadoc

/**
 * The Class SimpleQuoteManagedBean.
 * 
 * @author sri
 * @since 30/04/2014
 */
@ManagedBean(name = "simpleQuoteMB")
@SessionScoped
public class SimpleQuoteManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The source method. */
	private String sourceMethod = ParameterConst.METHOD_SQ;

	/** The sc quote. */
	private ScQuote scQuote;

	/** The fulfillment type. */
	private String fulfillmentType;

	/** The assigned to user id. */
	private Long assignedToUserId;

	/** The period type. */
	private String periodType;

	/** The period value. */
	private int periodValue;

	/** The price type. */
	private String priceType;

	/** The duration. */
	private double duration;

	/** The created by. */
	private String createdBy;

	/** The user division list. */
	private List<PmOrgProfile> userDivisionList;

	/** The cm supplier item. */
	private CmSupplierItem cmSupplierItem;

	/** The supplier items. */
	private List<CmSupplierItem> supplierItems;

	/** The user list. */
	private List<PmUser> userList = new ArrayList<PmUser>();

	/** The request note dtl. */
	private ScRequestNoteDtl requestNoteDtl = new ScRequestNoteDtl();

	/** The request note. */
	private ScRequestNote requestNote = new ScRequestNote(); // contain request
																// list
																// information

	/** The sm suppliers. */
	private List<SmSupplier> smSuppliers = new ArrayList<SmSupplier>();

	/** The price type code. */
	private String priceTypeCode;

	/** The just type option. */
	private boolean justTypeOption;

	/** The proc type option. */
	private boolean procTypeOption;

	/** The just dropdown. */
	private SelectItem[] justDropdown; // justification drop down

	/** The Loc level dropdown. */
	private SelectItem[] LocLevelDropdown; // Location Level drop down

	/** The location level. */
	private String locationLevel;

	/** The by locality. */
	private boolean byLocality = true;

	/** The by business nature. */
	private boolean byBusinessNature;

	/** The loc and business flag. */
	private boolean locAndBusinessFlag;

	/** The g2 g flag. */
	private boolean g2GFlag;

	/** The simple quote status. */
	private PmStatus simpleQuoteStatus;

	/** The zone name. */
	private String zoneName;

	/** The zone trees. */
	private List<TreeNode> zoneTrees = new ArrayList<TreeNode>();

	/** The selected zone tree. */
	private List<TreeNode> selectedZoneTree = new ArrayList<TreeNode>();

	/** The root zone tree. */
	private TreeNode rootZoneTree;

	/** The selected nodes. */
	private TreeNode[] selectedNodes;

	/** The selected tree node. */
	private TreeNode selectedTreeNode;

	/** The zone index. */
	private int zoneIndex = -1;

	/** The loc zone name. */
	private String locZoneName;

	/** The selected location tree. */
	private List<TreeNode> selectedLocationTree = new ArrayList<TreeNode>();

	/** The root location tree. */
	private TreeNode rootLocationTree;

	/** The selected loc nodes. */
	private TreeNode[] selectedLocNodes;

	/** The location trees. */
	private List<TreeNode> locationTrees = new ArrayList<TreeNode>();

	/** The state criterias. */
	private List<ScLocationStateCriteria> stateCriterias = new ArrayList<ScLocationStateCriteria>();

	/** The divistion criterias. */
	private List<ScLocationDivisionCriteria> divistionCriterias = new ArrayList<ScLocationDivisionCriteria>();

	/** The districts criterias. */
	private List<ScLocationDistrictCriteria> districtsCriterias = new ArrayList<ScLocationDistrictCriteria>();

	/** The active index. */
	private int activeIndex = 0;

	/** The product template spec bean. */
	private TemplateSpecBean productTemplateSpecBean = new TemplateSpecBean();

	/** The service template spec bean. */
	private TemplateSpecBean serviceTemplateSpecBean = new TemplateSpecBean();

	/** The search quote data model. */
	private IPagingDataModel<ScQuote> searchQuoteDataModel;
	// search criteria for popup clone item spec
	/** The search quote no. */
	private String searchQuoteNo;

	/** The search proc type. */
	private String searchProcType;

	/** The search title. */
	private String searchTitle;

	/** The search from created date. */
	private Date searchFromCreatedDate;

	/** The search to created date. */
	private Date searchToCreatedDate;

	/** The search status. */
	private Long searchStatus;

	/** The status descs. */
	private List<PmStatusDesc> statusDescs;

	// clone item spec
	/** The selected quote. */
	private ScQuote selectedQuote = new ScQuote();

	/** The quotes. */
	private List<ScQuote> quotes = new ArrayList<ScQuote>();

	/** The product or service. */
	private String productOrService;

	// business nature criteria
	/** The business natures. */
	private List<PmBusinessNature> businessNatures;

	/** The business nature criterias. */
	private List<ScBusinessNatureQuoteCriteria> businessNatureCriterias;

	/** The selected business nature. */
	private List<ScBusinessNatureQuoteCriteria> selectedBusinessNature;

	// panelSupplier for panel item
	/** The ct panel suppliers. */
	private List<CtPanelSupplier> ctPanelSuppliers;

	/** The panel suppliers. */
	private List<ScQuoteSupplier> panelSuppliers;

	/** The selected panel suppliers. */
	private List<ScQuoteSupplier> selectedPanelSuppliers;

	// quoteSuppliers for supplier list
	/** The supplier list. */
	private List<ScQuoteSupplier> supplierList = new ArrayList<ScQuoteSupplier>();

	/** The selected supplier list. */
	private List<ScQuoteSupplier> selectedSupplierList;

	/** The compare suppliers. */
	private List<SmSupplier> compareSuppliers = new ArrayList<SmSupplier>();

	// govtSellers for govt seller
	/** The org profiles. */
	private List<PmOrgProfile> orgProfiles;

	/** The govt sellers. */
	private List<ScQuoteSupplier> govtSellers;

	/** The selected govt sellers. */
	private List<ScQuoteSupplier> selectedGovtSellers;

	/** The ScQuoteSupplier */
	private ScQuoteSupplier scQuoteSupplier;

	/** The today. */
	private Date today = new Date();

	/** The tab active index. */
	private int tabActiveIndex = 0;

	/** The default country id. */
	private Long defaultCountryId;

	/** The title checker. */
	private boolean titleChecker = Boolean.TRUE;

	/** The page nav to request note. */
	private String pageNavToRequestNote;

	/** The procurement types. */
	private SelectItem[] procurementTypes; // for procuremment type dropdown

	/** The panel item flag. */
	private boolean panelItemFlag = Boolean.TRUE;

	/** The newable flag. */
	private boolean newableFlag = Boolean.TRUE;
	
	/** The removable flag */
	private boolean removableFlag = Boolean.TRUE;

	/** The editable flag. */
	private boolean editableFlag = Boolean.TRUE;

	/** The submit button flg. */
	private boolean submitButtonFlg = Boolean.TRUE;

	/** The save button flg. */
	private boolean saveButtonFlg = Boolean.TRUE;

	/** The required. */
	private boolean required;

	/** The prod questions. */
	private List<TplSpecQuestion> prodQuestions = new ArrayList<TplSpecQuestion>();

	/** The service questions. */
	private List<TplSpecQuestion> serviceQuestions = new ArrayList<TplSpecQuestion>();

	/** The prompt message. */
	private String promptMessage;

	/** The selected action. */
	private String selectedAction;

	/** The panel items. */
	private List<CtPanelItem> panelItems = new ArrayList<CtPanelItem>();

	/** The beans. */
	private List<ZonesBean> beans = new ArrayList<ZonesBean>();

	/** The conform dlg btn flag. */
	private boolean conformDlgBtnFlag;

	/** The supplier loc level. */
	private String supplierLocLevel;

	/** The selected flag. */
	private boolean selectedFlag;

	/** The auto update. */
	private boolean autoUpdate = Boolean.TRUE;

	/** The temp zone name. */
	private String tempZoneName;

	/** The spki backing bean. */
	@ManagedProperty(value = "#{spkiBackingBean}")
	private SpkiBackingBean spkiBackingBean;

	/** The end loa token. */
	private Object endLoaToken;

	/** The end loa task id. */
	private String endLoaTaskId;

	/** The end loa outcome. */
	private String endLoaOutcome;

	/** The end loa rn. */
	private ScRequestNote endLoaRN;

	/** The scQuoteSupplier response SQ. */
	private ScQuoteSupplier requestedSupplier;

	/** The sm gst supplier. */
	private SmGstSupplier smGstSupplier;
	
	/** The dp obj. */
	private Object[] dpObj = new Object[6];
	
	/** The tracking dairy view. */
	private boolean trackingDairyView;
	
	/** The task expiry date */
	private Calendar taskExpiryDate;
	
	/** The pid verifier and approvers */
	private String pidApprovers;
	

	/**
	 * Gets the spki backing bean.
	 * 
	 * @return the spki backing bean
	 */
	public SpkiBackingBean getSpkiBackingBean() {
		return spkiBackingBean;
	}

	/**
	 * Sets the spki backing bean.
	 * 
	 * @param spkiBackingBean
	 *            the new spki backing bean
	 */
	public void setSpkiBackingBean(SpkiBackingBean spkiBackingBean) {
		this.spkiBackingBean = spkiBackingBean;
	}

	/**
	 * Instantiates a new simple quote managed bean.
	 */
	public SimpleQuoteManagedBean() {
		super();
	}

	/** The sc quote data model. */
	private IPagingDataModel<ScQuote> scQuoteDataModel = new ScQuotePagingDataModel();

	/**
	 * Search clone item spec.
	 */
	public void searchCloneItemSpec() {
		scQuoteDataModel.reloadData();
	}
	
	/**
	 * Search simple quote.
	 */
	public void searchSimpleQuote() {
		 setScQuote(null);
		scQuoteDataModel.reloadData();
	}

	/**
	 * The Class ScQuotePagingDataModel.
	 */
	private class ScQuotePagingDataModel extends PagingDataModel<ScQuote> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new sc quote paging data model.
		 */
		public ScQuotePagingDataModel() {
			super("scQuoteDataModel");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQuote> pageChangeListener(
				ISearchBean<ScQuote> searchBean) {
			// debugLogger("pageChangeListener");
			try {

				if (getSearchQuoteNo() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("quoteNo",
									SearchOperator.OPERATOR_LIKE.value(),
									getSearchQuoteNo()));
				}

				if (getSearchTitle() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("title",
									SearchOperator.OPERATOR_LIKE.value(),
									getSearchTitle()));
				}

				if (getSearchProcType() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"procurementType.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									getSearchProcType()));
				}

				if(getScQuote() != null){
					// for panel item search
					if (getScQuote().isPanel()) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("panel",
										SearchOperator.OPERATOR_EQUALS.value(), 1));
					} else {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("panel",
										SearchOperator.OPERATOR_NOT_EQUALS.value(),
										1));
					}
				} else{
					// for simple quote search
					
					if(getSearchStatus() != null) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("workflowStatuses.pmStatus.statusId",
										SearchOperator.OPERATOR_EQUALS.value(),
										getSearchStatus()));
						
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("workflowStatuses.current",
										SearchOperator.OPERATOR_EQUALS.value(),
										true));
					}
				}

				if (getSearchFromCreatedDate() != null
						&& getSearchToCreatedDate() != null) {

					SearchFilterBean bean = new SearchFilterBean();
					bean.setSearchField("createdDate");
					bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN
							.value());
					SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
							getSearchFromCreatedDate(),
							getSearchToCreatedDate());
					bean.setSearchValue(betweenVal);
					searchBean.getDataFilterBeans().add(bean);
				}

				if (getProductOrService() != null) {
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									"quoteDocs.quoteTplSpecMaster.itemTypeCode.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									getProductOrService()));
				}

				QuoteReqDTO reqDTO = new QuoteReqDTO();
				reqDTO.setSearchQuoteBean(searchBean);

				IQuoteSO quoteSO = (IQuoteSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQuoteSO.class);
				QuoteResDTO resDTO = quoteSO.searchQuote(reqDTO);

				if (resDTO.isSuccessful()) {
					return resDTO.getSearchQuoteResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * Gets the fulfillment type.
	 * 
	 * @return the fulfillmentType
	 */
	public String getFulfillmentType() {
		return fulfillmentType;
	}

	/**
	 * Sets the fulfillment type.
	 * 
	 * @param fulfillmentType
	 *            the fulfillmentType to set
	 */
	public void setFulfillmentType(String fulfillmentType) {
		this.fulfillmentType = fulfillmentType;
	}

	/**
	 * Gets the assigned to user id.
	 * 
	 * @return the assignedToUserId
	 */
	public Long getAssignedToUserId() {
		return assignedToUserId;
	}

	/**
	 * Sets the assigned to user id.
	 * 
	 * @param assignedToUserId
	 *            the assignedToUserId to set
	 */
	public void setAssignedToUserId(Long assignedToUserId) {
		this.assignedToUserId = assignedToUserId;
	}

	/**
	 * Gets the period type.
	 * 
	 * @return the periodType
	 */
	public String getPeriodType() {
		return periodType;
	}

	/**
	 * Sets the period type.
	 * 
	 * @param periodType
	 *            the periodType to set
	 */
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}

	/**
	 * Gets the period value.
	 * 
	 * @return the periodValue
	 */
	public int getPeriodValue() {
		return periodValue;
	}

	/**
	 * Sets the period value.
	 * 
	 * @param periodValue
	 *            the periodValue to set
	 */
	public void setPeriodValue(int periodValue) {
		this.periodValue = periodValue;
	}

	/**
	 * Gets the user division list.
	 * 
	 * @return the userDivisionList
	 */
	public List<PmOrgProfile> getUserDivisionList() {
		return userDivisionList;
	}

	/**
	 * Sets the user division list.
	 * 
	 * @param userDivisionList
	 *            the userDivisionList to set
	 */
	public void setUserDivisionList(List<PmOrgProfile> userDivisionList) {
		this.userDivisionList = userDivisionList;
	}

	/**
	 * Checks if is just type option.
	 * 
	 * @return the justTypeOption
	 */
	public boolean isJustTypeOption() {
		return justTypeOption;
	}

	/**
	 * Sets the just type option.
	 * 
	 * @param justTypeOption
	 *            the justTypeOption to set
	 */
	public void setJustTypeOption(boolean justTypeOption) {
		this.justTypeOption = justTypeOption;
	}

	/**
	 * Checks if is proc type option.
	 * 
	 * @return the procTypeOption
	 */
	public boolean isProcTypeOption() {
		return procTypeOption;
	}

	/**
	 * Sets the proc type option.
	 * 
	 * @param procTypeOption
	 *            the procTypeOption to set
	 */
	public void setProcTypeOption(boolean procTypeOption) {
		this.procTypeOption = procTypeOption;
	}

	/**
	 * Gets the just dropdown.
	 * 
	 * @return the justDropdown
	 */
	public SelectItem[] getJustDropdown() {
		return justDropdown;
	}

	/**
	 * Sets the just dropdown.
	 * 
	 * @param justDropdown
	 *            the justDropdown to set
	 */
	public void setJustDropdown(SelectItem[] justDropdown) {
		this.justDropdown = justDropdown;
	}

	/**
	 * Gets the user list.
	 * 
	 * @return the userList
	 */
	public List<PmUser> getUserList() {
		return userList;
	}

	/**
	 * Sets the user list.
	 * 
	 * @param userList
	 *            the userList to set
	 */
	public void setUserList(List<PmUser> userList) {
		this.userList = userList;
	}

	/**
	 * Gets the source method.
	 * 
	 * @return the sourceMethod
	 */
	public String getSourceMethod() {
		return sourceMethod;
	}

	/**
	 * Sets the source method.
	 * 
	 * @param sourceMethod
	 *            the sourceMethod to set
	 */
	public void setSourceMethod(String sourceMethod) {
		this.sourceMethod = sourceMethod;
	}

	/**
	 * Gets the price type code.
	 * 
	 * @return the priceTypeCode
	 */
	public String getPriceTypeCode() {
		return priceTypeCode;
	}

	/**
	 * Sets the price type code.
	 * 
	 * @param priceTypeCode
	 *            the priceTypeCode to set
	 */
	public void setPriceTypeCode(String priceTypeCode) {
		this.priceTypeCode = priceTypeCode;
	}

	/**
	 * Gets the request note dtl.
	 * 
	 * @return the requestNoteDtl
	 */
	public ScRequestNoteDtl getRequestNoteDtl() {
		return requestNoteDtl;
	}

	/**
	 * Sets the request note dtl.
	 * 
	 * @param requestNoteDtl
	 *            the requestNoteDtl to set
	 */
	public void setRequestNoteDtl(ScRequestNoteDtl requestNoteDtl) {
		this.requestNoteDtl = requestNoteDtl;
	}

	/**
	 * Gets the request note.
	 * 
	 * @return the requestNote
	 */
	public ScRequestNote getRequestNote() {
		return requestNote;
	}

	/**
	 * Sets the request note.
	 * 
	 * @param requestNote
	 *            the requestNote to set
	 */
	public void setRequestNote(ScRequestNote requestNote) {
		this.requestNote = requestNote;
	}

	/**
	 * Gets the price type.
	 * 
	 * @return the priceType
	 */
	public String getPriceType() {
		return priceType;
	}

	/**
	 * Sets the price type.
	 * 
	 * @param priceType
	 *            the priceType to set
	 */
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	/**
	 * Gets the sc quote.
	 * 
	 * @return the scQuote
	 */
	public ScQuote getScQuote() {
		return scQuote;
	}

	/**
	 * Sets the sc quote.
	 * 
	 * @param scQuote
	 *            the scQuote to set
	 */
	public void setScQuote(ScQuote scQuote) {
		this.scQuote = scQuote;
	}

	/**
	 * Gets the active index.
	 * 
	 * @return the activeIndex
	 */
	public int getActiveIndex() {
		return activeIndex;
	}

	/**
	 * Sets the active index.
	 * 
	 * @param activeIndex
	 *            the activeIndex to set
	 */
	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	/**
	 * Gets the selected business nature.
	 * 
	 * @return the selectedBusinessNature
	 */
	public List<ScBusinessNatureQuoteCriteria> getSelectedBusinessNature() {
		return selectedBusinessNature;
	}

	/**
	 * Sets the selected business nature.
	 * 
	 * @param selectedBusinessNature
	 *            the selectedBusinessNature to set
	 */
	public void setSelectedBusinessNature(
			List<ScBusinessNatureQuoteCriteria> selectedBusinessNature) {
		this.selectedBusinessNature = selectedBusinessNature;
	}

	/**
	 * Gets the sm suppliers.
	 * 
	 * @return the smSuppliers
	 */
	public List<SmSupplier> getSmSuppliers() {
		return smSuppliers;
	}

	/**
	 * Sets the sm suppliers.
	 * 
	 * @param smSuppliers
	 *            the smSuppliers to set
	 */
	public void setSmSuppliers(List<SmSupplier> smSuppliers) {
		this.smSuppliers = smSuppliers;
	}

	/**
	 * Gets the location level.
	 * 
	 * @return the locationLevel
	 */
	public String getLocationLevel() {
		return locationLevel;
	}

	/**
	 * Sets the location level.
	 * 
	 * @param locationLevel
	 *            the locationLevel to set
	 */
	public void setLocationLevel(String locationLevel) {
		this.locationLevel = locationLevel;
	}

	/**
	 * Gets the zone name.
	 * 
	 * @return the zoneName
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * Sets the zone name.
	 * 
	 * @param zoneName
	 *            the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	/**
	 * Gets the zone trees.
	 * 
	 * @return the zoneTrees
	 */
	public List<TreeNode> getZoneTrees() {
		return zoneTrees;
	}

	/**
	 * Sets the zone trees.
	 * 
	 * @param zoneTrees
	 *            the zoneTrees to set
	 */
	public void setZoneTrees(List<TreeNode> zoneTrees) {
		this.zoneTrees = zoneTrees;
	}

	/**
	 * Gets the selected zone tree.
	 * 
	 * @return the selectedZoneTree
	 */
	public List<TreeNode> getSelectedZoneTree() {
		return selectedZoneTree;
	}

	/**
	 * Sets the selected zone tree.
	 * 
	 * @param selectedZoneTree
	 *            the selectedZoneTree to set
	 */
	public void setSelectedZoneTree(List<TreeNode> selectedZoneTree) {
		this.selectedZoneTree = selectedZoneTree;
	}

	/**
	 * Gets the root location tree.
	 * 
	 * @return the rootLocationTree
	 */
	public TreeNode getRootLocationTree() {
		return rootLocationTree;
	}

	/**
	 * Sets the root location tree.
	 * 
	 * @param rootLocationTree
	 *            the rootLocationTree to set
	 */
	public void setRootLocationTree(TreeNode rootLocationTree) {
		this.rootLocationTree = rootLocationTree;
	}

	/**
	 * Gets the selected nodes.
	 * 
	 * @return the selectedNodes
	 */
	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	/**
	 * Sets the selected nodes.
	 * 
	 * @param selectedNodes
	 *            the selectedNodes to set
	 */
	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	/**
	 * Gets the loc level dropdown.
	 * 
	 * @return the locLevelDropdown
	 */
	public SelectItem[] getLocLevelDropdown() {
		return LocLevelDropdown;
	}

	/**
	 * Sets the loc level dropdown.
	 * 
	 * @param locLevelDropdown
	 *            the locLevelDropdown to set
	 */
	public void setLocLevelDropdown(SelectItem[] locLevelDropdown) {
		LocLevelDropdown = locLevelDropdown;
	}

	/**
	 * Gets the location trees.
	 * 
	 * @return the locationTrees
	 */
	public List<TreeNode> getLocationTrees() {
		return locationTrees;
	}

	/**
	 * Sets the location trees.
	 * 
	 * @param locationTrees
	 *            the locationTrees to set
	 */
	public void setLocationTrees(List<TreeNode> locationTrees) {
		this.locationTrees = locationTrees;
	}

	/**
	 * Gets the zone index.
	 * 
	 * @return the zoneIndex
	 */
	public int getZoneIndex() {
		return zoneIndex;
	}

	/**
	 * Sets the zone index.
	 * 
	 * @param zoneIndex
	 *            the zoneIndex to set
	 */
	public void setZoneIndex(int zoneIndex) {
		this.zoneIndex = zoneIndex;
	}

	/**
	 * Gets the root zone tree.
	 * 
	 * @return the rootZoneTree
	 */
	public TreeNode getRootZoneTree() {
		return rootZoneTree;
	}

	/**
	 * Sets the root zone tree.
	 * 
	 * @param rootZoneTree
	 *            the rootZoneTree to set
	 */
	public void setRootZoneTree(TreeNode rootZoneTree) {
		this.rootZoneTree = rootZoneTree;
	}

	/**
	 * Gets the loc zone name.
	 * 
	 * @return the locZoneName
	 */
	public String getLocZoneName() {
		return locZoneName;
	}

	/**
	 * Sets the loc zone name.
	 * 
	 * @param locZoneName
	 *            the locZoneName to set
	 */
	public void setLocZoneName(String locZoneName) {
		this.locZoneName = locZoneName;
	}

	/**
	 * Gets the selected location tree.
	 * 
	 * @return the selectedLocationTree
	 */
	public List<TreeNode> getSelectedLocationTree() {
		return selectedLocationTree;
	}

	/**
	 * Sets the selected location tree.
	 * 
	 * @param selectedLocationTree
	 *            the selectedLocationTree to set
	 */
	public void setSelectedLocationTree(List<TreeNode> selectedLocationTree) {
		this.selectedLocationTree = selectedLocationTree;
	}

	/**
	 * Gets the selected loc nodes.
	 * 
	 * @return the selectedLocNodes
	 */
	public TreeNode[] getSelectedLocNodes() {
		return selectedLocNodes;
	}

	/**
	 * Sets the selected loc nodes.
	 * 
	 * @param selectedLocNodes
	 *            the selectedLocNodes to set
	 */
	public void setSelectedLocNodes(TreeNode[] selectedLocNodes) {
		this.selectedLocNodes = selectedLocNodes;
	}

	/**
	 * Gets the state criterias.
	 * 
	 * @return the stateCriterias
	 */
	public List<ScLocationStateCriteria> getStateCriterias() {
		return stateCriterias;
	}

	/**
	 * Sets the state criterias.
	 * 
	 * @param stateCriterias
	 *            the stateCriterias to set
	 */
	public void setStateCriterias(List<ScLocationStateCriteria> stateCriterias) {
		this.stateCriterias = stateCriterias;
	}

	/**
	 * Gets the divistion criterias.
	 * 
	 * @return the divistionCriterias
	 */
	public List<ScLocationDivisionCriteria> getDivistionCriterias() {
		return divistionCriterias;
	}

	/**
	 * Sets the divistion criterias.
	 * 
	 * @param divistionCriterias
	 *            the divistionCriterias to set
	 */
	public void setDivistionCriterias(
			List<ScLocationDivisionCriteria> divistionCriterias) {
		this.divistionCriterias = divistionCriterias;
	}

	/**
	 * Gets the districts criterias.
	 * 
	 * @return the districtsCriterias
	 */
	public List<ScLocationDistrictCriteria> getDistrictsCriterias() {
		return districtsCriterias;
	}

	/**
	 * Sets the districts criterias.
	 * 
	 * @param districtsCriterias
	 *            the districtsCriterias to set
	 */
	public void setDistrictsCriterias(
			List<ScLocationDistrictCriteria> districtsCriterias) {
		this.districtsCriterias = districtsCriterias;
	}

	/**
	 * Checks if is by locality.
	 * 
	 * @return the byLocality
	 */
	public boolean isByLocality() {
		return byLocality;
	}

	/**
	 * Sets the by locality.
	 * 
	 * @param byLocality
	 *            the byLocality to set
	 */
	public void setByLocality(boolean byLocality) {
		this.byLocality = byLocality;
	}

	/**
	 * Checks if is by business nature.
	 * 
	 * @return the byBusinessNature
	 */
	public boolean isByBusinessNature() {
		return byBusinessNature;
	}

	/**
	 * Sets the by business nature.
	 * 
	 * @param byBusinessNature
	 *            the byBusinessNature to set
	 */
	public void setByBusinessNature(boolean byBusinessNature) {
		this.byBusinessNature = byBusinessNature;
	}

	/**
	 * Gets the duration.
	 * 
	 * @return the duration
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * Sets the duration.
	 * 
	 * @param duration
	 *            the duration to set
	 */
	public void setDuration(double duration) {
		this.duration = duration;
	}

	/**
	 * Gets the created by.
	 * 
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		FacesScopes facesScopes = new FacesScopes();
		this.createdBy = facesScopes.getUserSessionBean().getUserName();
		return createdBy;
	}

	/**
	 * Sets the created by.
	 * 
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Gets the cm supplier item.
	 * 
	 * @return the cmSupplierItem
	 */
	public CmSupplierItem getCmSupplierItem() {
		return cmSupplierItem;
	}

	/**
	 * Sets the cm supplier item.
	 * 
	 * @param cmSupplierItem
	 *            the cmSupplierItem to set
	 */
	public void setCmSupplierItem(CmSupplierItem cmSupplierItem) {
		this.cmSupplierItem = cmSupplierItem;
	}

	/**
	 * Gets the business nature criterias.
	 * 
	 * @return the businessNatureCriterias
	 */
	public List<ScBusinessNatureQuoteCriteria> getBusinessNatureCriterias() {
		return businessNatureCriterias;
	}

	/**
	 * Sets the business nature criterias.
	 * 
	 * @param businessNatureCriterias
	 *            the businessNatureCriterias to set
	 */
	public void setBusinessNatureCriterias(
			List<ScBusinessNatureQuoteCriteria> businessNatureCriterias) {
		this.businessNatureCriterias = businessNatureCriterias;
	}

	/**
	 * Checks if is loc and business flag.
	 * 
	 * @return the locAndBusinessFlag
	 */
	public boolean isLocAndBusinessFlag() {
		return locAndBusinessFlag;
	}

	/**
	 * Sets the loc and business flag.
	 * 
	 * @param locAndBusinessFlag
	 *            the locAndBusinessFlag to set
	 */
	public void setLocAndBusinessFlag(boolean locAndBusinessFlag) {
		this.locAndBusinessFlag = locAndBusinessFlag;
	}

	/**
	 * Checks if is g2 g flag.
	 * 
	 * @return the g2GFlag
	 */
	public boolean isG2GFlag() {
		return g2GFlag;
	}

	/**
	 * Sets the g2 g flag.
	 * 
	 * @param g2gFlag
	 *            the g2GFlag to set
	 */
	public void setG2GFlag(boolean g2gFlag) {
		g2GFlag = g2gFlag;
	}

	/**
	 * Gets the simple quote status.
	 * 
	 * @return the simpleQuoteStatus
	 */
	public PmStatus getSimpleQuoteStatus() {
		return simpleQuoteStatus;
	}

	/**
	 * Sets the simple quote status.
	 * 
	 * @param simpleQuoteStatus
	 *            the simpleQuoteStatus to set
	 */
	public void setSimpleQuoteStatus(PmStatus simpleQuoteStatus) {
		this.simpleQuoteStatus = simpleQuoteStatus;
	}

	/**
	 * Gets the product template spec bean.
	 * 
	 * @return the productTemplateSpecBean
	 */
	public TemplateSpecBean getProductTemplateSpecBean() {
		return productTemplateSpecBean;
	}

	/**
	 * Sets the product template spec bean.
	 * 
	 * @param productTemplateSpecBean
	 *            the productTemplateSpecBean to set
	 */
	public void setProductTemplateSpecBean(
			TemplateSpecBean productTemplateSpecBean) {
		this.productTemplateSpecBean = productTemplateSpecBean;
	}

	/**
	 * Gets the service template spec bean.
	 * 
	 * @return the serviceTemplateSpecBean
	 */
	public TemplateSpecBean getServiceTemplateSpecBean() {
		return serviceTemplateSpecBean;
	}

	/**
	 * Sets the service template spec bean.
	 * 
	 * @param serviceTemplateSpecBean
	 *            the serviceTemplateSpecBean to set
	 */
	public void setServiceTemplateSpecBean(
			TemplateSpecBean serviceTemplateSpecBean) {
		this.serviceTemplateSpecBean = serviceTemplateSpecBean;
	}

	/**
	 * Gets the search quote data model.
	 * 
	 * @return the searchQuoteDataModel
	 */
	public IPagingDataModel<ScQuote> getSearchQuoteDataModel() {
		return searchQuoteDataModel;
	}

	/**
	 * Sets the search quote data model.
	 * 
	 * @param searchQuoteDataModel
	 *            the searchQuoteDataModel to set
	 */
	public void setSearchQuoteDataModel(
			IPagingDataModel<ScQuote> searchQuoteDataModel) {
		this.searchQuoteDataModel = searchQuoteDataModel;
	}

	/**
	 * Gets the search quote no.
	 * 
	 * @return the searchQuoteNo
	 */
	public String getSearchQuoteNo() {
		return searchQuoteNo;
	}

	/**
	 * Sets the search quote no.
	 * 
	 * @param searchQuoteNo
	 *            the searchQuoteNo to set
	 */
	public void setSearchQuoteNo(String searchQuoteNo) {
		this.searchQuoteNo = searchQuoteNo;
	}

	/**
	 * Gets the search proc type.
	 * 
	 * @return the searchProcType
	 */
	public String getSearchProcType() {
		return searchProcType;
	}

	/**
	 * Sets the search proc type.
	 * 
	 * @param searchProcType
	 *            the searchProcType to set
	 */
	public void setSearchProcType(String searchProcType) {
		this.searchProcType = searchProcType;
	}

	/**
	 * Gets the search title.
	 * 
	 * @return the searchTitle
	 */
	public String getSearchTitle() {
		return searchTitle;
	}

	/**
	 * Sets the search title.
	 * 
	 * @param searchTitle
	 *            the searchTitle to set
	 */
	public void setSearchTitle(String searchTitle) {
		this.searchTitle = searchTitle;
	}

	/**
	 * Gets the search from created date.
	 * 
	 * @return the searchFromCreatedDate
	 */
	public Date getSearchFromCreatedDate() {
		return searchFromCreatedDate;
	}

	/**
	 * Sets the search from created date.
	 * 
	 * @param searchFromCreatedDate
	 *            the searchFromCreatedDate to set
	 */
	public void setSearchFromCreatedDate(Date searchFromCreatedDate) {
		this.searchFromCreatedDate = searchFromCreatedDate;
	}

	/**
	 * Gets the search to created date.
	 * 
	 * @return the searchToCreatedDate
	 */
	public Date getSearchToCreatedDate() {
		return searchToCreatedDate;
	}

	/**
	 * Sets the search to created date.
	 * 
	 * @param searchToCreatedDate
	 *            the searchToCreatedDate to set
	 */
	public void setSearchToCreatedDate(Date searchToCreatedDate) {
		this.searchToCreatedDate = searchToCreatedDate;
	}

	/**
	 * Gets the selected quote.
	 * 
	 * @return the selectedQuote
	 */
	public ScQuote getSelectedQuote() {
		return selectedQuote;
	}

	/**
	 * Sets the selected quote.
	 * 
	 * @param selectedQuote
	 *            the selectedQuote to set
	 */
	public void setSelectedQuote(ScQuote selectedQuote) {
		this.selectedQuote = selectedQuote;
	}

	/**
	 * Gets the quotes.
	 * 
	 * @return the quotes
	 */
	public List<ScQuote> getQuotes() {
		return quotes;
	}

	/**
	 * Sets the quotes.
	 * 
	 * @param quotes
	 *            the quotes to set
	 */
	public void setQuotes(List<ScQuote> quotes) {
		this.quotes = quotes;
	}

	/**
	 * Gets the business natures.
	 * 
	 * @return the businessNatures
	 */
	public List<PmBusinessNature> getBusinessNatures() {
		return businessNatures;
	}

	/**
	 * Sets the business natures.
	 * 
	 * @param businessNatures
	 *            the businessNatures to set
	 */
	public void setBusinessNatures(List<PmBusinessNature> businessNatures) {
		this.businessNatures = businessNatures;
	}

	/**
	 * Gets the serialversionuid.
	 * 
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * Gets the ct panel suppliers.
	 * 
	 * @return the ctPanelSuppliers
	 */
	public List<CtPanelSupplier> getCtPanelSuppliers() {
		return ctPanelSuppliers;
	}

	/**
	 * Sets the ct panel suppliers.
	 * 
	 * @param ctPanelSuppliers
	 *            the ctPanelSuppliers to set
	 */
	public void setCtPanelSuppliers(List<CtPanelSupplier> ctPanelSuppliers) {
		this.ctPanelSuppliers = ctPanelSuppliers;
	}

	/**
	 * Gets the sc quote data model.
	 * 
	 * @return the scQuoteDataModel
	 */
	public IPagingDataModel<ScQuote> getScQuoteDataModel() {
		return scQuoteDataModel;
	}

	/**
	 * Sets the sc quote data model.
	 * 
	 * @param scQuoteDataModel
	 *            the scQuoteDataModel to set
	 */
	public void setScQuoteDataModel(IPagingDataModel<ScQuote> scQuoteDataModel) {
		this.scQuoteDataModel = scQuoteDataModel;
	}

	/**
	 * Gets the org profiles.
	 * 
	 * @return the orgProfiles
	 */
	public List<PmOrgProfile> getOrgProfiles() {
		return orgProfiles;
	}

	/**
	 * Sets the org profiles.
	 * 
	 * @param orgProfiles
	 *            the orgProfiles to set
	 */
	public void setOrgProfiles(List<PmOrgProfile> orgProfiles) {
		this.orgProfiles = orgProfiles;
	}

	/**
	 * Gets the product or service.
	 * 
	 * @return the productOrService
	 */
	public String getProductOrService() {
		return productOrService;
	}

	/**
	 * Sets the product or service.
	 * 
	 * @param productOrService
	 *            the productOrService to set
	 */
	public void setProductOrService(String productOrService) {
		this.productOrService = productOrService;
	}

	/**
	 * Gets the govt sellers.
	 * 
	 * @return the govtSellers
	 */
	public List<ScQuoteSupplier> getGovtSellers() {
		return govtSellers;
	}

	/**
	 * Sets the govt sellers.
	 * 
	 * @param govtSellers
	 *            the govtSellers to set
	 */
	public void setGovtSellers(List<ScQuoteSupplier> govtSellers) {
		this.govtSellers = govtSellers;
	}

	/**
	 * Gets the supplier list.
	 * 
	 * @return the supplierList
	 */
	public List<ScQuoteSupplier> getSupplierList() {
		return supplierList;
	}

	/**
	 * Sets the supplier list.
	 * 
	 * @param supplierList
	 *            the supplierList to set
	 */
	public void setSupplierList(List<ScQuoteSupplier> supplierList) {
		this.supplierList = supplierList;
	}

	/**
	 * Gets the selected supplier list.
	 * 
	 * @return the selectedSupplierList
	 */
	public List<ScQuoteSupplier> getSelectedSupplierList() {
		return selectedSupplierList;
	}

	/**
	 * Sets the selected supplier list.
	 * 
	 * @param selectedSupplierList
	 *            the selectedSupplierList to set
	 */
	public void setSelectedSupplierList(
			List<ScQuoteSupplier> selectedSupplierList) {
		this.selectedSupplierList = selectedSupplierList;
	}

	/**
	 * Gets the selected govt sellers.
	 * 
	 * @return the selectedGovtSellers
	 */
	public List<ScQuoteSupplier> getSelectedGovtSellers() {
		return selectedGovtSellers;
	}

	/**
	 * Sets the selected govt sellers.
	 * 
	 * @param selectedGovtSellers
	 *            the selectedGovtSellers to set
	 */
	public void setSelectedGovtSellers(List<ScQuoteSupplier> selectedGovtSellers) {
		this.selectedGovtSellers = selectedGovtSellers;
	}

	/**
	 * Gets the panel suppliers.
	 * 
	 * @return the panelSuppliers
	 */
	public List<ScQuoteSupplier> getPanelSuppliers() {
		return panelSuppliers;
	}

	/**
	 * Sets the panel suppliers.
	 * 
	 * @param panelSuppliers
	 *            the panelSuppliers to set
	 */
	public void setPanelSuppliers(List<ScQuoteSupplier> panelSuppliers) {
		this.panelSuppliers = panelSuppliers;
	}

	/**
	 * Gets the selected panel suppliers.
	 * 
	 * @return the selectedPanelSuppliers
	 */
	public List<ScQuoteSupplier> getSelectedPanelSuppliers() {
		return selectedPanelSuppliers;
	}

	/**
	 * Sets the selected panel suppliers.
	 * 
	 * @param selectedPanelSuppliers
	 *            the selectedPanelSuppliers to set
	 */
	public void setSelectedPanelSuppliers(
			List<ScQuoteSupplier> selectedPanelSuppliers) {
		this.selectedPanelSuppliers = selectedPanelSuppliers;
	}

	public ScQuoteSupplier getScQuoteSupplier() {
		return scQuoteSupplier;
	}

	/**
	 * @param scQuoteSupplier
	 *            the scQuoteSupplier to set
	 */
	public void setScQuoteSupplier(ScQuoteSupplier scQuoteSupplier) {
		this.scQuoteSupplier = scQuoteSupplier;
	}

	/**
	 * Gets the compare suppliers.
	 * 
	 * @return the compareSuppliers
	 */
	public List<SmSupplier> getCompareSuppliers() {
		return compareSuppliers;
	}

	/**
	 * Sets the compare suppliers.
	 * 
	 * @param compareSuppliers
	 *            the compareSuppliers to set
	 */
	public void setCompareSuppliers(List<SmSupplier> compareSuppliers) {
		this.compareSuppliers = compareSuppliers;
	}

	/**
	 * Gets the selected tree node.
	 * 
	 * @return the selectedTreeNode
	 */
	public TreeNode getSelectedTreeNode() {
		return selectedTreeNode;
	}

	/**
	 * Sets the selected tree node.
	 * 
	 * @param selectedTreeNode
	 *            the selectedTreeNode to set
	 */
	public void setSelectedTreeNode(TreeNode selectedTreeNode) {
		this.selectedTreeNode = selectedTreeNode;
	}

	/**
	 * Gets the today.
	 * 
	 * @return the today
	 */
	public Date getToday() {
		return today;
	}

	/**
	 * Sets the today.
	 * 
	 * @param today
	 *            the today to set
	 */
	public void setToday(Date today) {
		this.today = today;
	}

	/**
	 * Gets the tab active index.
	 * 
	 * @return the tabActiveIndex
	 */
	public int getTabActiveIndex() {
		return tabActiveIndex;
	}

	/**
	 * Sets the tab active index.
	 * 
	 * @param tabActiveIndex
	 *            the tabActiveIndex to set
	 */
	public void setTabActiveIndex(int tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	/**
	 * Gets the supplier items.
	 * 
	 * @return the supplierItems
	 */
	public List<CmSupplierItem> getSupplierItems() {
		return supplierItems;
	}

	/**
	 * Sets the supplier items.
	 * 
	 * @param supplierItems
	 *            the supplierItems to set
	 */
	public void setSupplierItems(List<CmSupplierItem> supplierItems) {
		this.supplierItems = supplierItems;
	}

	/**
	 * Gets the default country id.
	 * 
	 * @return the defaultCountryId
	 */
	public Long getDefaultCountryId() {
		return defaultCountryId;
	}

	/**
	 * Sets the default country id.
	 * 
	 * @param defaultCountryId
	 *            the defaultCountryId to set
	 */
	public void setDefaultCountryId(Long defaultCountryId) {
		this.defaultCountryId = defaultCountryId;
	}

	/**
	 * Checks if is title checker.
	 * 
	 * @return the titleChecker
	 */
	public boolean isTitleChecker() {
		return titleChecker;
	}

	/**
	 * Sets the title checker.
	 * 
	 * @param titleChecker
	 *            the titleChecker to set
	 */
	public void setTitleChecker(boolean titleChecker) {
		this.titleChecker = titleChecker;
	}

	/**
	 * Gets the page nav to request note.
	 * 
	 * @return the pageNavToRequestNote
	 */
	public String getPageNavToRequestNote() {
		return pageNavToRequestNote;
	}

	/**
	 * Sets the page nav to request note.
	 * 
	 * @param pageNavToRequestNote
	 *            the pageNavToRequestNote to set
	 */
	public void setPageNavToRequestNote(String pageNavToRequestNote) {
		this.pageNavToRequestNote = pageNavToRequestNote;
	}

	/**
	 * Gets the procurement types.
	 * 
	 * @return the procurementTypes
	 */
	public SelectItem[] getProcurementTypes() {
		return procurementTypes;
	}

	/**
	 * Sets the procurement types.
	 * 
	 * @param procurementTypes
	 *            the procurementTypes to set
	 */
	public void setProcurementTypes(SelectItem[] procurementTypes) {
		this.procurementTypes = procurementTypes;
	}

	/**
	 * Checks if is required.
	 * 
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Sets the required.
	 * 
	 * @param required
	 *            the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * Gets the prod questions.
	 * 
	 * @return the prodQuestions
	 */
	public List<TplSpecQuestion> getProdQuestions() {
		return prodQuestions;
	}

	/**
	 * Sets the prod questions.
	 * 
	 * @param prodQuestions
	 *            the prodQuestions to set
	 */
	public void setProdQuestions(List<TplSpecQuestion> prodQuestions) {
		this.prodQuestions = prodQuestions;
	}

	/**
	 * Gets the service questions.
	 * 
	 * @return the serviceQuestions
	 */
	public List<TplSpecQuestion> getServiceQuestions() {
		return serviceQuestions;
	}

	/**
	 * Sets the service questions.
	 * 
	 * @param serviceQuestions
	 *            the serviceQuestions to set
	 */
	public void setServiceQuestions(List<TplSpecQuestion> serviceQuestions) {
		this.serviceQuestions = serviceQuestions;
	}

	/**
	 * Checks if is panel item flag.
	 * 
	 * @return the panelItemFlag
	 */
	public boolean isPanelItemFlag() {
		return panelItemFlag;
	}

	/**
	 * Sets the panel item flag.
	 * 
	 * @param panelItemFlag
	 *            the panelItemFlag to set
	 */
	public void setPanelItemFlag(boolean panelItemFlag) {
		this.panelItemFlag = panelItemFlag;
	}

	/**
	 * Gets the prompt message.
	 * 
	 * @return the promptMessage
	 */
	public String getPromptMessage() {
		return promptMessage;
	}

	/**
	 * Sets the prompt message.
	 * 
	 * @param promptMessage
	 *            the promptMessage to set
	 */
	public void setPromptMessage(String promptMessage) {
		this.promptMessage = promptMessage;
	}

	/**
	 * Gets the selected action.
	 * 
	 * @return the selectedAction
	 */
	public String getSelectedAction() {
		return selectedAction;
	}

	/**
	 * Sets the selected action.
	 * 
	 * @param selectedAction
	 *            the selectedAction to set
	 */
	public void setSelectedAction(String selectedAction) {
		this.selectedAction = selectedAction;
	}

	/**
	 * Gets the panel items.
	 * 
	 * @return the panelItems
	 */
	public List<CtPanelItem> getPanelItems() {
		return panelItems;
	}

	/**
	 * Sets the panel items.
	 * 
	 * @param panelItems
	 *            the panelItems to set
	 */
	public void setPanelItems(List<CtPanelItem> panelItems) {
		this.panelItems = panelItems;
	}

	/**
	 * Gets the beans.
	 * 
	 * @return the beans
	 */
	public List<ZonesBean> getBeans() {
		return beans;
	}

	/**
	 * Sets the beans.
	 * 
	 * @param beans
	 *            the beans to set
	 */
	public void setBeans(List<ZonesBean> beans) {
		this.beans = beans;
	}

	/**
	 * Checks if is newable flag.
	 * 
	 * @return true, if is newable flag
	 */
	public boolean isNewableFlag() {
		return newableFlag;
	}

	/**
	 * Sets the newable flag.
	 * 
	 * @param newableFlag
	 *            the new newable flag
	 */
	public void setNewableFlag(boolean newableFlag) {
		this.newableFlag = newableFlag;
	}

	/**
	 * @return the removableFlag
	 */
	public boolean isRemovableFlag() {
		return removableFlag;
	}

	/**
	 * @param removableFlag the removableFlag to set
	 */
	public void setRemovableFlag(boolean removableFlag) {
		this.removableFlag = removableFlag;
	}

	/**
	 * Checks if is editable flag.
	 * 
	 * @return true, if is editable flag
	 */
	public boolean isEditableFlag() {
		return editableFlag;
	}

	/**
	 * Sets the editable flag.
	 * 
	 * @param editableFlag
	 *            the new editable flag
	 */
	public void setEditableFlag(boolean editableFlag) {
		this.editableFlag = editableFlag;
	}

	/**
	 * Checks if is conform dlg btn flag.
	 * 
	 * @return true, if is conform dlg btn flag
	 */
	public boolean isConformDlgBtnFlag() {
		return conformDlgBtnFlag;
	}

	/**
	 * Sets the conform dlg btn flag.
	 * 
	 * @param conformDlgBtnFlag
	 *            the new conform dlg btn flag
	 */
	public void setConformDlgBtnFlag(boolean conformDlgBtnFlag) {
		this.conformDlgBtnFlag = conformDlgBtnFlag;
	}

	/**
	 * Gets the supplier loc level.
	 * 
	 * @return the supplier loc level
	 */
	public String getSupplierLocLevel() {
		return supplierLocLevel;
	}

	/**
	 * Sets the supplier loc level.
	 * 
	 * @param supplierLocLevel
	 *            the new supplier loc level
	 */
	public void setSupplierLocLevel(String supplierLocLevel) {
		this.supplierLocLevel = supplierLocLevel;
	}

	/**
	 * Checks if is selected flag.
	 * 
	 * @return true, if is selected flag
	 */
	public boolean isSelectedFlag() {
		return selectedFlag;
	}

	/**
	 * Sets the selected flag.
	 * 
	 * @param selectedFlag
	 *            the new selected flag
	 */
	public void setSelectedFlag(boolean selectedFlag) {
		this.selectedFlag = selectedFlag;
	}

	/**
	 * Checks if is auto update.
	 * 
	 * @return true, if is auto update
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * Sets the auto update.
	 * 
	 * @param autoUpdate
	 *            the new auto update
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * Gets the temp zone name.
	 * 
	 * @return the temp zone name
	 */
	public String getTempZoneName() {
		return tempZoneName;
	}

	/**
	 * Sets the temp zone name.
	 * 
	 * @param tempZoneName
	 *            the new temp zone name
	 */
	public void setTempZoneName(String tempZoneName) {
		this.tempZoneName = tempZoneName;
	}

	/**
	 * Gets the end loa token.
	 * 
	 * @return the end loa token
	 */
	public Object getEndLoaToken() {
		return endLoaToken;
	}

	/**
	 * Sets the end loa token.
	 * 
	 * @param endLoaToken
	 *            the new end loa token
	 */
	public void setEndLoaToken(Object endLoaToken) {
		this.endLoaToken = endLoaToken;
	}

	/**
	 * Gets the end loa task id.
	 * 
	 * @return the end loa task id
	 */
	public String getEndLoaTaskId() {
		return endLoaTaskId;
	}

	/**
	 * Sets the end loa task id.
	 * 
	 * @param endLoaTaskId
	 *            the new end loa task id
	 */
	public void setEndLoaTaskId(String endLoaTaskId) {
		this.endLoaTaskId = endLoaTaskId;
	}

	/**
	 * Gets the end loa outcome.
	 * 
	 * @return the end loa outcome
	 */
	public String getEndLoaOutcome() {
		return endLoaOutcome;
	}

	/**
	 * Sets the end loa outcome.
	 * 
	 * @param endLoaOutcome
	 *            the new end loa outcome
	 */
	public void setEndLoaOutcome(String endLoaOutcome) {
		this.endLoaOutcome = endLoaOutcome;
	}

	/**
	 * Gets the end loa rn.
	 * 
	 * @return the endLoaRN
	 */
	public ScRequestNote getEndLoaRN() {
		return endLoaRN;
	}

	/**
	 * Sets the end loa rn.
	 * 
	 * @param endLoaRN
	 *            the endLoaRN to set
	 */
	public void setEndLoaRN(ScRequestNote endLoaRN) {
		this.endLoaRN = endLoaRN;
	}

	/**
	 * @return the requestedSupplier
	 */
	public ScQuoteSupplier getRequestedSupplier() {
		return requestedSupplier;
	}

	/**
	 * @param requestedSupplier
	 *            the requestedSupplier to set
	 */
	public void setRequestedSupplier(ScQuoteSupplier requestedSupplier) {
		this.requestedSupplier = requestedSupplier;
	}

	/**
	 * Checks if is submit button flg.
	 * 
	 * @return true, if is submit button flg
	 */
	public boolean isSubmitButtonFlg() {
		return submitButtonFlg;
	}

	/**
	 * Sets the submit button flg.
	 * 
	 * @param submitButtonFlg
	 *            the new submit button flg
	 */
	public void setSubmitButtonFlg(boolean submitButtonFlg) {
		this.submitButtonFlg = submitButtonFlg;
	}

	/**
	 * Checks if is save button flg.
	 * 
	 * @return true, if is save button flg
	 */
	public boolean isSaveButtonFlg() {
		return saveButtonFlg;
	}

	/**
	 * Sets the save button flg.
	 * 
	 * @param saveButtonFlg
	 *            the new save button flg
	 */
	public void setSaveButtonFlg(boolean saveButtonFlg) {
		this.saveButtonFlg = saveButtonFlg;
	}

	/**
	 * @return the smGstSupplier
	 */
	public SmGstSupplier getSmGstSupplier() {
		return smGstSupplier;
	}

	/**
	 * @param smGstSupplier the smGstSupplier to set
	 */
	public void setSmGstSupplier(SmGstSupplier smGstSupplier) {
		this.smGstSupplier = smGstSupplier;
	}

	/**
	 * @return the statusDescs
	 */
	public List<PmStatusDesc> getStatusDescs() {
		return statusDescs;
	}

	/**
	 * @param statusDescs the statusDescs to set
	 */
	public void setStatusDescs(List<PmStatusDesc> statusDescs) {
		this.statusDescs = statusDescs;
	}

	/**
	 * @return the searchStatus
	 */
	public Long getSearchStatus() {
		return searchStatus;
	}

	/**
	 * @param searchStatus the searchStatus to set
	 */
	public void setSearchStatus(Long searchStatus) {
		this.searchStatus = searchStatus;
	}

	/**
	 * @return the dpObj
	 */
	public Object[] getDpObj() {
		return dpObj;
	}

	/**
	 * @param dpObj the dpObj to set
	 */
	public void setDpObj(Object[] dpObj) {
		this.dpObj = dpObj;
	}

	/**
	 * @return the trackingDairyView
	 */
	public boolean isTrackingDairyView() {
		return trackingDairyView;
	}

	/**
	 * @param trackingDairyView the trackingDairyView to set
	 */
	public void setTrackingDairyView(boolean trackingDairyView) {
		this.trackingDairyView = trackingDairyView;
	}

	/**
	 * @return the taskExpiryDate
	 */
	public Calendar getTaskExpiryDate() {
		return taskExpiryDate;
	}

	/**
	 * @param taskExpiryDate the taskExpiryDate to set
	 */
	public void setTaskExpiryDate(Calendar taskExpiryDate) {
		this.taskExpiryDate = taskExpiryDate;
	}

	/**
	 * @return the pidApprovers
	 */
	public String getPidApprovers() {
		return pidApprovers;
	}

	/**
	 * @param pidApprovers the pidApprovers to set
	 */
	public void setPidApprovers(String pidApprovers) {
		this.pidApprovers = pidApprovers;
	}
	
}
