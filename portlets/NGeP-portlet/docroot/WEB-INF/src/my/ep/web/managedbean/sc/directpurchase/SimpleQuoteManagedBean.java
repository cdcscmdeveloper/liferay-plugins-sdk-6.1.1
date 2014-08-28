/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.directpurchase;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
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
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.sc.directpurchase.ScBusinessNatureQuoteCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationDistrictCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationDivisionCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationStateCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteSupplier;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.TreeNode;
/**
 * 
 * @author sri
 *
 */
@ManagedBean(name="simpleQuoteMB")
@SessionScoped
public class SimpleQuoteManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	private String sourceMethod = ParameterConst.METHOD_SQ;
	private ScQuote scQuote;
	private	String	fulfillmentType;
	private	Long	assignedToUserId;
	private	String	periodType;
	private	int	periodValue;
	private String priceType;
	private double duration;
	private String createdBy;
	private String processName;

	private List<PmOrgProfile> userDivisionList;
	private CmSupplierItem cmSupplierItem;
	private List<CmSupplierItem> supplierItems;
	
	private List<PmUser> userList = new ArrayList<PmUser>();
	private ScRequestNoteDtl requestNoteDtl = new ScRequestNoteDtl();
	private	ScRequestNote	requestNote = new	ScRequestNote(); //contain request list information

	private List<SmSupplier> smSuppliers = new ArrayList<SmSupplier>();

	private String priceTypeCode;
	private boolean justTypeOption;
	private boolean procTypeOption;
	private SelectItem[] justDropdown; // justification drop down
	private SelectItem[] LocLevelDropdown; // Location Level drop down
	private String locationLevel;
	private boolean byLocality = true;
	private boolean byBusinessNature;
	private boolean locAndBusinessFlag;
	private boolean g2GFlag;
	private String simpleQuoteStatus;

	private	String	zoneName;
	private List<TreeNode>	zoneTrees = new ArrayList<TreeNode>();
	private	List<TreeNode>	selectedZoneTree	=	new ArrayList<TreeNode>();
	private	TreeNode	rootZoneTree;
	private TreeNode[] selectedNodes;
	private TreeNode selectedTreeNode;
	private int zoneIndex = -1;

	private	String	locZoneName;
	private	List<TreeNode>	selectedLocationTree	=	new ArrayList<TreeNode>();
	private	TreeNode	rootLocationTree;
	private TreeNode[] selectedLocNodes;
	private	List<TreeNode>	locationTrees	=	new ArrayList<TreeNode>();
	private int locIndex = -1;

	private List<ScLocationStateCriteria>	stateCriterias = new ArrayList<ScLocationStateCriteria>();
	private List<ScLocationDivisionCriteria> divistionCriterias = new ArrayList<ScLocationDivisionCriteria>();
	private List<ScLocationDistrictCriteria>	districtsCriterias = new ArrayList<ScLocationDistrictCriteria>();
	private int activeIndex = 0;

	private TemplateSpecBean productTemplateSpecBean = new TemplateSpecBean();
	private TemplateSpecBean serviceTemplateSpecBean = new TemplateSpecBean();

	private IPagingDataModel<ScQuote> searchQuoteDataModel;
	// search criteria for popup clone item spec
	private	String searchQuoteNo;
	private String searchProcType;
	private String searchTitle;
	private Date searchFromCreatedDate;
	private Date searchToCreatedDate;
    
	// clone item spec
	private ScQuote selectedQuote =  new ScQuote();
	private List<ScQuote> quotes = new ArrayList<ScQuote>();
	private String productOrService;
   
	// business nature criteria	
	private List<PmBusinessNature>	businessNatures;
	private List<ScBusinessNatureQuoteCriteria> businessNatureCriterias;
	private List<ScBusinessNatureQuoteCriteria> selectedBusinessNature;

	// panelSupplier for panel item
	private List<CtPanelSupplier>	ctPanelSuppliers;
	private List<ScQuoteSupplier> panelSuppliers;
	private List<ScQuoteSupplier> selectedPanelSuppliers;

	// quoteSuppliers for  supplier list
	private List<ScQuoteSupplier> supplierList = new ArrayList<ScQuoteSupplier>();
	private List<ScQuoteSupplier> selectedSupplierList;
	private List<SmSupplier> compareSuppliers = new ArrayList<SmSupplier>();

	//govtSellers for govt seller 
	private List<PmOrgProfile> orgProfiles; 
	private List<ScQuoteSupplier> govtSellers; 
	private List<ScQuoteSupplier> selectedGovtSellers;
	
	// supplier view
	private SmSupplier smSupplier;
	private Date  today = new Date();
	private int tabActiveIndex = 0;
	private Long defaultCountryId;
	private boolean titleChecker = Boolean.TRUE;
	private String pageNavToRequestNote;
	private SelectItem[] procurementTypes; // for procuremment type dropdown
	private boolean rnToSQFlag = Boolean.TRUE;
	private boolean panelItemFlag = Boolean.TRUE;
	private boolean panelItemBtnFlag = Boolean.TRUE;
	
	private boolean supplierViewBtn = Boolean.TRUE;
	private boolean required;
	private List<TplSpecQuestion> prodQuestions = new ArrayList<TplSpecQuestion>();
	private List<TplSpecQuestion> serviceQuestions = new ArrayList<TplSpecQuestion>();
	
	private String promptMessage;
	private String selectedAction;

	public SimpleQuoteManagedBean() {
		super();
	}


	private IPagingDataModel<ScQuote> scQuoteDataModel = new ScQuotePagingDataModel();

	public void searchCloneItemSpec(){
		scQuoteDataModel.reloadData();
	}

	private class ScQuotePagingDataModel extends PagingDataModel<ScQuote> {
		private static final long serialVersionUID = 1L;

		public ScQuotePagingDataModel() {
			super("scQuoteDataModel");
		}

		@Override
		public ISearchResultBean<ScQuote> pageChangeListener(ISearchBean<ScQuote> searchBean) {
			// debugLogger("pageChangeListener");
			try {

				if(getSearchQuoteNo() !=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("quoteNo",SearchOperator.OPERATOR_LIKE.value(),getSearchQuoteNo()));
				}

				if(getSearchTitle() !=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("title",SearchOperator.OPERATOR_LIKE.value(),getSearchTitle()));
				}

				if(getSearchProcType() !=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("procurementType.parameterCode",SearchOperator.OPERATOR_EQUALS.value(),getSearchProcType()));
				}

				if (getSearchFromCreatedDate() != null
						&& getSearchToCreatedDate() != null) {

					SearchFilterBean bean = new SearchFilterBean();
					bean.setSearchField("createdDate");
					bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
					SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(getSearchFromCreatedDate(), getSearchToCreatedDate());
					bean.setSearchValue(betweenVal);
					searchBean.getDataFilterBeans().add(bean);
				}

				if(getProductOrService() !=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("quoteDocs.quoteTplSpecMaster.itemTypeCode.parameterCode",SearchOperator.OPERATOR_EQUALS.value(),getProductOrService()));
				}

				QuoteReqDTO reqDTO = new QuoteReqDTO();
				reqDTO.setSearchQuoteBean(searchBean);

				IQuoteSO quoteSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQuoteSO.class);
				QuoteResDTO resDTO = quoteSO.searchQuote(reqDTO);

				if(resDTO.isSuccessful()) {
					return resDTO.getSearchQuoteResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * @return the fulfillmentType
	 */
	public String getFulfillmentType() {
		return fulfillmentType;
	}
	/**
	 * @param fulfillmentType the fulfillmentType to set
	 */
	public void setFulfillmentType(String fulfillmentType) {
		this.fulfillmentType = fulfillmentType;
	}
	/**
	 * @return the assignedToUserId
	 */
	public Long getAssignedToUserId() {
		return assignedToUserId;
	}
	/**
	 * @param assignedToUserId the assignedToUserId to set
	 */
	public void setAssignedToUserId(Long assignedToUserId) {
		this.assignedToUserId = assignedToUserId;
	}
	/**
	 * @return the periodType
	 */
	public String getPeriodType() {
		return periodType;
	}
	/**
	 * @param periodType the periodType to set
	 */
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}
	/**
	 * @return the periodValue
	 */
	public int getPeriodValue() {
		return periodValue;
	}
	/**
	 * @param periodValue the periodValue to set
	 */
	public void setPeriodValue(int periodValue) {
		this.periodValue = periodValue;
	}

	/**
	 * @return the userDivisionList
	 */
	public List<PmOrgProfile> getUserDivisionList() {
		return userDivisionList;
	}
	/**
	 * @param userDivisionList the userDivisionList to set
	 */
	public void setUserDivisionList(List<PmOrgProfile> userDivisionList) {
		this.userDivisionList = userDivisionList;
	}

	/**
	 * @return the justTypeOption
	 */
	public boolean isJustTypeOption() {
		return justTypeOption;
	}
	/**
	 * @param justTypeOption the justTypeOption to set
	 */
	public void setJustTypeOption(boolean justTypeOption) {
		this.justTypeOption = justTypeOption;
	}
	/**
	 * @return the procTypeOption
	 */
	public boolean isProcTypeOption() {
		return procTypeOption;
	}
	/**
	 * @param procTypeOption the procTypeOption to set
	 */
	public void setProcTypeOption(boolean procTypeOption) {
		this.procTypeOption = procTypeOption;
	}
	/**
	 * @return the justDropdown
	 */
	public SelectItem[] getJustDropdown() {
		return justDropdown;
	}
	/**
	 * @param justDropdown the justDropdown to set
	 */
	public void setJustDropdown(SelectItem[] justDropdown) {
		this.justDropdown = justDropdown;
	}
	/**
	 * @return the userList
	 */
	public List<PmUser> getUserList() {
		return userList;
	}
	/**
	 * @param userList the userList to set
	 */
	public void setUserList(List<PmUser> userList) {
		this.userList = userList;
	}

	/**
	 * @return the sourceMethod
	 */
	public String getSourceMethod() {
		return sourceMethod;
	}

	/**
	 * @param sourceMethod the sourceMethod to set
	 */
	public void setSourceMethod(String sourceMethod) {
		this.sourceMethod = sourceMethod;
	}

	/**
	 * @return the priceTypeCode
	 */
	public String getPriceTypeCode() {
		return priceTypeCode;
	}
	/**
	 * @param priceTypeCode the priceTypeCode to set
	 */
	public void setPriceTypeCode(String priceTypeCode) {
		this.priceTypeCode = priceTypeCode;
	}
	/**
	 * @return the requestNoteDtl
	 */
	public ScRequestNoteDtl getRequestNoteDtl() {
		return requestNoteDtl;
	}
	/**
	 * @param requestNoteDtl the requestNoteDtl to set
	 */
	public void setRequestNoteDtl(ScRequestNoteDtl requestNoteDtl) {
		this.requestNoteDtl = requestNoteDtl;
	}
	/**
	 * @return the requestNote
	 */
	public ScRequestNote getRequestNote() {
		return requestNote;
	}

	/**
	 * @param requestNote the requestNote to set
	 */
	public void setRequestNote(ScRequestNote requestNote) {
		this.requestNote = requestNote;
	}
	/**
	 * @return the priceType
	 */
	public String getPriceType() {
		return priceType;
	}
	/**
	 * @param priceType the priceType to set
	 */
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}
	/**
	 * @return the scQuote
	 */
	public ScQuote getScQuote() {
		return scQuote;
	}
	/**
	 * @param scQuote the scQuote to set
	 */
	public void setScQuote(ScQuote scQuote) {
		this.scQuote = scQuote;
	}


	/**
	 * @return the activeIndex
	 */
	public int getActiveIndex() {
		return activeIndex;
	}

	/**
	 * @param activeIndex the activeIndex to set
	 */
	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}




	/**
	 * @return the selectedBusinessNature
	 */
	public List<ScBusinessNatureQuoteCriteria> getSelectedBusinessNature() {
		return selectedBusinessNature;
	}

	/**
	 * @param selectedBusinessNature the selectedBusinessNature to set
	 */
	public void setSelectedBusinessNature(
			List<ScBusinessNatureQuoteCriteria> selectedBusinessNature) {
		this.selectedBusinessNature = selectedBusinessNature;
	}

	/**
	 * @return the smSuppliers
	 */
	public List<SmSupplier> getSmSuppliers() {
		return smSuppliers;
	}

	/**
	 * @param smSuppliers the smSuppliers to set
	 */
	public void setSmSuppliers(List<SmSupplier> smSuppliers) {
		this.smSuppliers = smSuppliers;
	}

	/**
	 * @return the locationLevel
	 */
	public String getLocationLevel() {
		return locationLevel;
	}

	/**
	 * @param locationLevel the locationLevel to set
	 */
	public void setLocationLevel(String locationLevel) {
		this.locationLevel = locationLevel;
	}

	/**
	 * @return the zoneName
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * @param zoneName the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	/**
	 * @return the zoneTrees
	 */
	public List<TreeNode> getZoneTrees() {
		return zoneTrees;
	}

	/**
	 * @param zoneTrees the zoneTrees to set
	 */
	public void setZoneTrees(List<TreeNode> zoneTrees) {
		this.zoneTrees = zoneTrees;
	}

	/**
	 * @return the selectedZoneTree
	 */
	public List<TreeNode> getSelectedZoneTree() {
		return selectedZoneTree;
	}

	/**
	 * @param selectedZoneTree the selectedZoneTree to set
	 */
	public void setSelectedZoneTree(List<TreeNode> selectedZoneTree) {
		this.selectedZoneTree = selectedZoneTree;
	}

	/**
	 * @return the rootLocationTree
	 */
	public TreeNode getRootLocationTree() {
		return rootLocationTree;
	}

	/**
	 * @param rootLocationTree the rootLocationTree to set
	 */
	public void setRootLocationTree(TreeNode rootLocationTree) {
		this.rootLocationTree = rootLocationTree;
	}

	/**
	 * @return the selectedNodes
	 */
	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	/**
	 * @param selectedNodes the selectedNodes to set
	 */
	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}
	/**
	 * @return the locLevelDropdown
	 */
	public SelectItem[] getLocLevelDropdown() {
		return LocLevelDropdown;
	}

	/**
	 * @param locLevelDropdown the locLevelDropdown to set
	 */
	public void setLocLevelDropdown(SelectItem[] locLevelDropdown) {
		LocLevelDropdown = locLevelDropdown;
	}

	/**
	 * @return the locationTrees
	 */
	public List<TreeNode> getLocationTrees() {
		return locationTrees;
	}

	/**
	 * @param locationTrees the locationTrees to set
	 */
	public void setLocationTrees(List<TreeNode> locationTrees) {
		this.locationTrees = locationTrees;
	}

	/**
	 * @return the zoneIndex
	 */
	public int getZoneIndex() {
		return zoneIndex;
	}

	/**
	 * @param zoneIndex the zoneIndex to set
	 */
	public void setZoneIndex(int zoneIndex) {
		this.zoneIndex = zoneIndex;
	}

	/**
	 * @return the rootZoneTree
	 */
	public TreeNode getRootZoneTree() {
		return rootZoneTree;
	}

	/**
	 * @param rootZoneTree the rootZoneTree to set
	 */
	public void setRootZoneTree(TreeNode rootZoneTree) {
		this.rootZoneTree = rootZoneTree;
	}

	/**
	 * @return the locZoneName
	 */
	public String getLocZoneName() {
		return locZoneName;
	}

	/**
	 * @param locZoneName the locZoneName to set
	 */
	public void setLocZoneName(String locZoneName) {
		this.locZoneName = locZoneName;
	}

	/**
	 * @return the selectedLocationTree
	 */
	public List<TreeNode> getSelectedLocationTree() {
		return selectedLocationTree;
	}

	/**
	 * @param selectedLocationTree the selectedLocationTree to set
	 */
	public void setSelectedLocationTree(List<TreeNode> selectedLocationTree) {
		this.selectedLocationTree = selectedLocationTree;
	}

	/**
	 * @return the selectedLocNodes
	 */
	public TreeNode[] getSelectedLocNodes() {
		return selectedLocNodes;
	}

	/**
	 * @param selectedLocNodes the selectedLocNodes to set
	 */
	public void setSelectedLocNodes(TreeNode[] selectedLocNodes) {
		this.selectedLocNodes = selectedLocNodes;
	}

	/**
	 * @return the locIndex
	 */
	public int getLocIndex() {
		return locIndex;
	}

	/**
	 * @param locIndex the locIndex to set
	 */
	public void setLocIndex(int locIndex) {
		this.locIndex = locIndex;
	}

	/**
	 * @return the stateCriterias
	 */
	public List<ScLocationStateCriteria> getStateCriterias() {
		return stateCriterias;
	}

	/**
	 * @param stateCriterias the stateCriterias to set
	 */
	public void setStateCriterias(List<ScLocationStateCriteria> stateCriterias) {
		this.stateCriterias = stateCriterias;
	}

	/**
	 * @return the divistionCriterias
	 */
	public List<ScLocationDivisionCriteria> getDivistionCriterias() {
		return divistionCriterias;
	}

	/**
	 * @param divistionCriterias the divistionCriterias to set
	 */
	public void setDivistionCriterias(
			List<ScLocationDivisionCriteria> divistionCriterias) {
		this.divistionCriterias = divistionCriterias;
	}

	/**
	 * @return the districtsCriterias
	 */
	public List<ScLocationDistrictCriteria> getDistrictsCriterias() {
		return districtsCriterias;
	}

	/**
	 * @param districtsCriterias the districtsCriterias to set
	 */
	public void setDistrictsCriterias(
			List<ScLocationDistrictCriteria> districtsCriterias) {
		this.districtsCriterias = districtsCriterias;
	}
	

	/**
	 * @return the byLocality
	 */
	public boolean isByLocality() {
		return byLocality;
	}

	/**
	 * @param byLocality the byLocality to set
	 */
	public void setByLocality(boolean byLocality) {
		this.byLocality = byLocality;
	}

	/**
	 * @return the byBusinessNature
	 */
	public boolean isByBusinessNature() {
		return byBusinessNature;
	}

	/**
	 * @param byBusinessNature the byBusinessNature to set
	 */
	public void setByBusinessNature(boolean byBusinessNature) {
		this.byBusinessNature = byBusinessNature;
	}

	/**
	 * @return the duration
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(double duration) {
		this.duration = duration;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		FacesScopes facesScopes = new FacesScopes();
		this.createdBy = facesScopes.getUserSessionBean().getUserName();
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the cmSupplierItem
	 */
	public CmSupplierItem getCmSupplierItem() {
		return cmSupplierItem;
	}

	/**
	 * @param cmSupplierItem the cmSupplierItem to set
	 */
	public void setCmSupplierItem(CmSupplierItem cmSupplierItem) {
		this.cmSupplierItem = cmSupplierItem;
	}

	/**
	 * @return the processName
	 */
	public String getProcessName() {
		return processName;
	}

	/**
	 * @param processName the processName to set
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	/**
	 * @return the businessNatureCriterias
	 */
	public List<ScBusinessNatureQuoteCriteria> getBusinessNatureCriterias() {
		return businessNatureCriterias;
	}

	/**
	 * @param businessNatureCriterias the businessNatureCriterias to set
	 */
	public void setBusinessNatureCriterias(
			List<ScBusinessNatureQuoteCriteria> businessNatureCriterias) {
		this.businessNatureCriterias = businessNatureCriterias;
	}
	
	/**
	 * @return the locAndBusinessFlag
	 */
	public boolean isLocAndBusinessFlag() {
		return locAndBusinessFlag;
	}

	/**
	 * @param locAndBusinessFlag the locAndBusinessFlag to set
	 */
	public void setLocAndBusinessFlag(boolean locAndBusinessFlag) {
		this.locAndBusinessFlag = locAndBusinessFlag;
	}

	/**
	 * @return the g2GFlag
	 */
	public boolean isG2GFlag() {
		return g2GFlag;
	}

	/**
	 * @param g2gFlag the g2GFlag to set
	 */
	public void setG2GFlag(boolean g2gFlag) {
		g2GFlag = g2gFlag;
	}

	/**
	 * @return the simpleQuoteStatus
	 */
	public String getSimpleQuoteStatus() {
		return simpleQuoteStatus;
	}

	/**
	 * @param simpleQuoteStatus the simpleQuoteStatus to set
	 */
	public void setSimpleQuoteStatus(String simpleQuoteStatus) {
		this.simpleQuoteStatus = simpleQuoteStatus;
	}

	/**
	 * @return the productTemplateSpecBean
	 */
	public TemplateSpecBean getProductTemplateSpecBean() {
		return productTemplateSpecBean;
	}

	/**
	 * @param productTemplateSpecBean the productTemplateSpecBean to set
	 */
	public void setProductTemplateSpecBean(TemplateSpecBean productTemplateSpecBean) {
		this.productTemplateSpecBean = productTemplateSpecBean;
	}

	/**
	 * @return the serviceTemplateSpecBean
	 */
	public TemplateSpecBean getServiceTemplateSpecBean() {
		return serviceTemplateSpecBean;
	}

	/**
	 * @param serviceTemplateSpecBean the serviceTemplateSpecBean to set
	 */
	public void setServiceTemplateSpecBean(TemplateSpecBean serviceTemplateSpecBean) {
		this.serviceTemplateSpecBean = serviceTemplateSpecBean;
	}

	/**
	 * @return the searchQuoteDataModel
	 */
	public IPagingDataModel<ScQuote> getSearchQuoteDataModel() {
		return searchQuoteDataModel;
	}

	/**
	 * @param searchQuoteDataModel the searchQuoteDataModel to set
	 */
	public void setSearchQuoteDataModel(
			IPagingDataModel<ScQuote> searchQuoteDataModel) {
		this.searchQuoteDataModel = searchQuoteDataModel;
	}


	/**
	 * @return the searchQuoteNo
	 */
	public String getSearchQuoteNo() {
		return searchQuoteNo;
	}

	/**
	 * @param searchQuoteNo the searchQuoteNo to set
	 */
	public void setSearchQuoteNo(String searchQuoteNo) {
		this.searchQuoteNo = searchQuoteNo;
	}


	/**
	 * @return the searchProcType
	 */
	public String getSearchProcType() {
		return searchProcType;
	}

	/**
	 * @param searchProcType the searchProcType to set
	 */
	public void setSearchProcType(String searchProcType) {
		this.searchProcType = searchProcType;
	}

	/**
	 * @return the searchTitle
	 */
	public String getSearchTitle() {
		return searchTitle;
	}

	/**
	 * @param searchTitle the searchTitle to set
	 */
	public void setSearchTitle(String searchTitle) {
		this.searchTitle = searchTitle;
	}

	/**
	 * @return the searchFromCreatedDate
	 */
	public Date getSearchFromCreatedDate() {
		return searchFromCreatedDate;
	}

	/**
	 * @param searchFromCreatedDate the searchFromCreatedDate to set
	 */
	public void setSearchFromCreatedDate(Date searchFromCreatedDate) {
		this.searchFromCreatedDate = searchFromCreatedDate;
	}

	/**
	 * @return the searchToCreatedDate
	 */
	public Date getSearchToCreatedDate() {
		return searchToCreatedDate;
	}

	/**
	 * @param searchToCreatedDate the searchToCreatedDate to set
	 */
	public void setSearchToCreatedDate(Date searchToCreatedDate) {
		this.searchToCreatedDate = searchToCreatedDate;
	}

	/**
	 * @return the selectedQuote
	 */
	public ScQuote getSelectedQuote() {
		return selectedQuote;
	}

	/**
	 * @param selectedQuote the selectedQuote to set
	 */
	public void setSelectedQuote(ScQuote selectedQuote) {
		this.selectedQuote = selectedQuote;
	}

	/**
	 * @return the quotes
	 */
	public List<ScQuote> getQuotes() {
		return quotes;
	}

	/**
	 * @param quotes the quotes to set
	 */
	public void setQuotes(List<ScQuote> quotes) {
		this.quotes = quotes;
	}

	/**
	 * @return the businessNatures
	 */
	public List<PmBusinessNature> getBusinessNatures() {
		return businessNatures;
	}

	/**
	 * @param businessNatures the businessNatures to set
	 */
	public void setBusinessNatures(List<PmBusinessNature> businessNatures) {
		this.businessNatures = businessNatures;
	}
	
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
	 * @return the ctPanelSuppliers
	 */
	public List<CtPanelSupplier> getCtPanelSuppliers() {
		return ctPanelSuppliers;
	}

	/**
	 * @param ctPanelSuppliers the ctPanelSuppliers to set
	 */
	public void setCtPanelSuppliers(List<CtPanelSupplier> ctPanelSuppliers) {
		this.ctPanelSuppliers = ctPanelSuppliers;
	}
	
	/**
	 * @return the scQuoteDataModel
	 */
	public IPagingDataModel<ScQuote> getScQuoteDataModel() {
		return scQuoteDataModel;
	}
	/**
	 * @param scQuoteDataModel the scQuoteDataModel to set
	 */
	public void setScQuoteDataModel(IPagingDataModel<ScQuote> scQuoteDataModel) {
		this.scQuoteDataModel = scQuoteDataModel;
	}
	/**
	 * @return the orgProfiles
	 */
	public List<PmOrgProfile> getOrgProfiles() {
		return orgProfiles;
	}
	/**
	 * @param orgProfiles the orgProfiles to set
	 */
	public void setOrgProfiles(List<PmOrgProfile> orgProfiles) {
		this.orgProfiles = orgProfiles;
	}
	/**
	 * @return the productOrService
	 */
	public String getProductOrService() {
		return productOrService;
	}
	/**
	 * @param productOrService the productOrService to set
	 */
	public void setProductOrService(String productOrService) {
		this.productOrService = productOrService;
	}

	/**
	 * @return the govtSellers
	 */
	public List<ScQuoteSupplier> getGovtSellers() {
		return govtSellers;
	}
	/**
	 * @param govtSellers the govtSellers to set
	 */
	public void setGovtSellers(List<ScQuoteSupplier> govtSellers) {
		this.govtSellers = govtSellers;
	}
	/**
	 * @return the supplierList
	 */
	public List<ScQuoteSupplier> getSupplierList() {
		return supplierList;
	}
	/**
	 * @param supplierList the supplierList to set
	 */
	public void setSupplierList(List<ScQuoteSupplier> supplierList) {
		this.supplierList = supplierList;
	}

	/**
	 * @return the selectedSupplierList
	 */
	public List<ScQuoteSupplier> getSelectedSupplierList() {
		return selectedSupplierList;
	}
	/**
	 * @param selectedSupplierList the selectedSupplierList to set
	 */
	public void setSelectedSupplierList(List<ScQuoteSupplier> selectedSupplierList) {
		this.selectedSupplierList = selectedSupplierList;
	}
	/**
	 * @return the selectedGovtSellers
	 */
	public List<ScQuoteSupplier> getSelectedGovtSellers() {
		return selectedGovtSellers;
	}
	/**
	 * @param selectedGovtSellers the selectedGovtSellers to set
	 */
	public void setSelectedGovtSellers(List<ScQuoteSupplier> selectedGovtSellers) {
		this.selectedGovtSellers = selectedGovtSellers;
	}
	/**
	 * @return the panelSuppliers
	 */
	public List<ScQuoteSupplier> getPanelSuppliers() {
		return panelSuppliers;
	}
	/**
	 * @param panelSuppliers the panelSuppliers to set
	 */
	public void setPanelSuppliers(List<ScQuoteSupplier> panelSuppliers) {
		this.panelSuppliers = panelSuppliers;
	}
	/**
	 * @return the selectedPanelSuppliers
	 */
	public List<ScQuoteSupplier> getSelectedPanelSuppliers() {
		return selectedPanelSuppliers;
	}
	/**
	 * @param selectedPanelSuppliers the selectedPanelSuppliers to set
	 */
	public void setSelectedPanelSuppliers(
			List<ScQuoteSupplier> selectedPanelSuppliers) {
		this.selectedPanelSuppliers = selectedPanelSuppliers;
	}

	/**
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}
	/**
	 * @param smSupplier the smSupplier to set
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}
	
	/**
	 * @return the compareSuppliers
	 */
	public List<SmSupplier> getCompareSuppliers() {
		return compareSuppliers;
	}
	/**
	 * @param compareSuppliers the compareSuppliers to set
	 */
	public void setCompareSuppliers(List<SmSupplier> compareSuppliers) {
		this.compareSuppliers = compareSuppliers;
	}
	/**
	 * @return the selectedTreeNode
	 */
	public TreeNode getSelectedTreeNode() {
		return selectedTreeNode;
	}
	/**
	 * @param selectedTreeNode the selectedTreeNode to set
	 */
	public void setSelectedTreeNode(TreeNode selectedTreeNode) {
		this.selectedTreeNode = selectedTreeNode;
	}
	/**
	 * @return the today
	 */
	public Date getToday() {
		return today;
	}
	/**
	 * @param today the today to set
	 */
	public void setToday(Date today) {
		this.today = today;
	}
	/**
	 * @return the tabActiveIndex
	 */
	public int getTabActiveIndex() {
		return tabActiveIndex;
	}
	/**
	 * @param tabActiveIndex the tabActiveIndex to set
	 */
	public void setTabActiveIndex(int tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	/**
	 * @return the supplierItems
	 */
	public List<CmSupplierItem> getSupplierItems() {
		return supplierItems;
	}
	/**
	 * @param supplierItems the supplierItems to set
	 */
	public void setSupplierItems(List<CmSupplierItem> supplierItems) {
		this.supplierItems = supplierItems;
	}
	
	/**
	 * @return the defaultCountryId
	 */
	public Long getDefaultCountryId() {
		return defaultCountryId;
	}
	/**
	 * @param defaultCountryId the defaultCountryId to set
	 */
	public void setDefaultCountryId(Long defaultCountryId) {
		this.defaultCountryId = defaultCountryId;
	}
	/**
	 * @return the titleChecker
	 */
	public boolean isTitleChecker() {
		return titleChecker;
	}
	/**
	 * @param titleChecker the titleChecker to set
	 */
	public void setTitleChecker(boolean titleChecker) {
		this.titleChecker = titleChecker;
	}
	/**
	 * @return the pageNavToRequestNote
	 */
	public String getPageNavToRequestNote() {
		return pageNavToRequestNote;
	}
	/**
	 * @param pageNavToRequestNote the pageNavToRequestNote to set
	 */
	public void setPageNavToRequestNote(String pageNavToRequestNote) {
		this.pageNavToRequestNote = pageNavToRequestNote;
	}
	/**
	 * @return the procurementTypes
	 */
	public SelectItem[] getProcurementTypes() {
		return procurementTypes;
	}
	/**
	 * @param procurementTypes the procurementTypes to set
	 */
	public void setProcurementTypes(SelectItem[] procurementTypes) {
		this.procurementTypes = procurementTypes;
	}
	/**
	 * @return the rnToSQFlag
	 */
	public boolean isRnToSQFlag() {
		return rnToSQFlag;
	}
	/**
	 * @param rnToSQFlag the rnToSQFlag to set
	 */
	public void setRnToSQFlag(boolean rnToSQFlag) {
		this.rnToSQFlag = rnToSQFlag;
	}
	/**
	 * @return the supplierViewBtn
	 */
	public boolean isSupplierViewBtn() {
		return supplierViewBtn;
	}
	/**
	 * @param supplierViewBtn the supplierViewBtn to set
	 */
	public void setSupplierViewBtn(boolean supplierViewBtn) {
		this.supplierViewBtn = supplierViewBtn;
	}
	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}
	/**
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}
	/**
	 * @return the prodQuestions
	 */
	public List<TplSpecQuestion> getProdQuestions() {
		return prodQuestions;
	}
	/**
	 * @param prodQuestions the prodQuestions to set
	 */
	public void setProdQuestions(List<TplSpecQuestion> prodQuestions) {
		this.prodQuestions = prodQuestions;
	}
	/**
	 * @return the serviceQuestions
	 */
	public List<TplSpecQuestion> getServiceQuestions() {
		return serviceQuestions;
	}
	/**
	 * @param serviceQuestions the serviceQuestions to set
	 */
	public void setServiceQuestions(List<TplSpecQuestion> serviceQuestions) {
		this.serviceQuestions = serviceQuestions;
	}
	/**
	 * @return the panelItemFlag
	 */
	public boolean isPanelItemFlag() {
		return panelItemFlag;
	}
	/**
	 * @param panelItemFlag the panelItemFlag to set
	 */
	public void setPanelItemFlag(boolean panelItemFlag) {
		this.panelItemFlag = panelItemFlag;
	}
	/**
	 * @return the panelItemBtnFlag
	 */
	public boolean isPanelItemBtnFlag() {
		return panelItemBtnFlag;
	}
	/**
	 * @param panelItemBtnFlag the panelItemBtnFlag to set
	 */
	public void setPanelItemBtnFlag(boolean panelItemBtnFlag) {
		this.panelItemBtnFlag = panelItemBtnFlag;
	}
	/**
	 * @return the promptMessage
	 */
	public String getPromptMessage() {
		return promptMessage;
	}
	/**
	 * @param promptMessage the promptMessage to set
	 */
	public void setPromptMessage(String promptMessage) {
		this.promptMessage = promptMessage;
	}
	/**
	 * @return the selectedAction
	 */
	public String getSelectedAction() {
		return selectedAction;
	}
	/**
	 * @param selectedAction the selectedAction to set
	 */
	public void setSelectedAction(String selectedAction) {
		this.selectedAction = selectedAction;
	}
	
}
