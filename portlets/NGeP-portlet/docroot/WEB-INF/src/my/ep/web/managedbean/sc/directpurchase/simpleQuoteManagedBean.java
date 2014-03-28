/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.directpurchase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.constant.pm.ParameterConst;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.directpurchase.ScBusinessNatureQuoteCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScGovEPNoCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationDistrictCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationDivisionCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScLocationStateCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierMofNoCriteria;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.web.backingbean.sc.directpurchase.TabBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.TreeNode;
/**
 * 
 * @author sri
 *
 */
@ManagedBean(name="simpleQuoteMB")
@SessionScoped
public class simpleQuoteManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	private String sourceMethod = ParameterConst.METHOD_SQ;
	private ScQuote scQuote = new ScQuote();
	private	String	fulfillmentType;
	private	Long	assignedToUserId;
	private	String	periodType;
	private	int	periodValue;
	private Long orgProfileId;
	private String priceType;
	private String duration;
	
	private List<PmOrgProfile> userDivisionList;
	private CmSupplierItem supplierItem;
    private List<PmUser> userList = new ArrayList<PmUser>();
    private ScRequestNoteDtl requestNoteDtl = new ScRequestNoteDtl();
    private	ScRequestNote	requestNote = new	ScRequestNote(); //contain request list information
    private List<ScBusinessNatureQuoteCriteria> businessNatureCriterias;
    private List<ScBusinessNatureQuoteCriteria> selectedBusinessNature;
    private List<ScSupplierMofNoCriteria>	supplierMofNoCriterias;
    private List<ScSupplierMofNoCriteria>	selectedSuppMofNoCriterias;
    private List<ScGovEPNoCriteria>	govEPNoCriterias;
    private List<ScGovEPNoCriteria>	selectedGovEPNoCriterias;
    
    private List<SmSupplier> smSuppliers;
    private PmOrgProfile pmOrgProfile;
    
	private	List<TplSpecMaster>	productTpls = new ArrayList<TplSpecMaster>();
	private	List<TplSpecMaster>	serviceTpls = new ArrayList<TplSpecMaster>();
    
    private String priceTypeCode;
    private boolean justTypeOption;
    private boolean procTypeOption;
    private SelectItem[] justDropdown; // justification drop down
    private SelectItem[] LocLevelDropdown; // Location Level drop down
    private Integer locationLevel;
    
    private	String	zoneName;
	private List<TreeNode>	zoneTrees = new ArrayList<TreeNode>();
	private	List<TreeNode>	selectedZoneTree	=	new ArrayList<TreeNode>();
	private	TreeNode	rootZoneTree;
	private TreeNode[] selectedNodes;
	private int zoneIndex = -1;
	
	private	String	locZoneName;
	private	List<TreeNode>	selectedLocationTree	=	new ArrayList<TreeNode>();
	private	TreeNode	rootLocationTree;
	private TreeNode[] selectedLocNodes;
	private	List<TreeNode>	locationTrees	=	new ArrayList<TreeNode>();
	private int locIndex = -1;
    
    private List<String> selectedCriteriaList = new ArrayList<String>();
    private List<TabBean> criteriaTabs = new ArrayList<TabBean>();
   	private List<ScLocationStateCriteria>	stateCriterias = new ArrayList<ScLocationStateCriteria>();
	private List<ScLocationDivisionCriteria> divistionCriterias = new ArrayList<ScLocationDivisionCriteria>();
	private List<ScLocationDistrictCriteria>	districtsCriterias = new ArrayList<ScLocationDistrictCriteria>();
    private int activeIndex = 0;
    
    
    public simpleQuoteManagedBean() {
		super();
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
	 * @return the orgProfileId
	 */
	public Long getOrgProfileId() {
		return orgProfileId;
	}
	/**
	 * @param orgProfileId the orgProfileId to set
	 */
	public void setOrgProfileId(Long orgProfileId) {
		this.orgProfileId = orgProfileId;
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
	 * @return the supplierItem
	 */
	public CmSupplierItem getSupplierItem() {
		return supplierItem;
	}
	/**
	 * @param supplierItem the supplierItem to set
	 */
	public void setSupplierItem(CmSupplierItem supplierItem) {
		this.supplierItem = supplierItem;
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
	 * @return the selectedCriteriaList
	 */
	public List<String> getSelectedCriteriaList() {
		return selectedCriteriaList;
	}

	/**
	 * @param selectedCriteriaList the selectedCriteriaList to set
	 */
	public void setSelectedCriteriaList(List<String> selectedCriteriaList) {
		this.selectedCriteriaList = selectedCriteriaList;
	}

	/**
	 * @return the criteriaTabs
	 */
	public List<TabBean> getCriteriaTabs() {
		return criteriaTabs;
	}

	/**
	 * @param criteriaTabs the criteriaTabs to set
	 */
	public void setCriteriaTabs(List<TabBean> criteriaTabs) {
		TabBean tabBean = new TabBean(1, "Locality");
		criteriaTabs.add(tabBean);
		this.criteriaTabs = criteriaTabs;
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
	 * @return the supplierMofNoCriterias
	 */
	public List<ScSupplierMofNoCriteria> getSupplierMofNoCriterias() {
		return supplierMofNoCriterias;
	}

	/**
	 * @param supplierMofNoCriterias the supplierMofNoCriterias to set
	 */
	public void setSupplierMofNoCriterias(
			List<ScSupplierMofNoCriteria> supplierMofNoCriterias) {
		this.supplierMofNoCriterias = supplierMofNoCriterias;
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
	 * @return the selectedSuppMofNoCriterias
	 */
	public List<ScSupplierMofNoCriteria> getSelectedSuppMofNoCriterias() {
		return selectedSuppMofNoCriterias;
	}

	/**
	 * @param selectedSuppMofNoCriterias the selectedSuppMofNoCriterias to set
	 */
	public void setSelectedSuppMofNoCriterias(
			List<ScSupplierMofNoCriteria> selectedSuppMofNoCriterias) {
		this.selectedSuppMofNoCriterias = selectedSuppMofNoCriterias;
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
	public Integer getLocationLevel() {
		return locationLevel;
	}

	/**
	 * @param locationLevel the locationLevel to set
	 */
	public void setLocationLevel(Integer locationLevel) {
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
	 * @return the pmOrgProfile
	 */
	public PmOrgProfile getPmOrgProfile() {
		return pmOrgProfile;
	}

	/**
	 * @param pmOrgProfile the pmOrgProfile to set
	 */
	public void setPmOrgProfile(PmOrgProfile pmOrgProfile) {
		this.pmOrgProfile = pmOrgProfile;
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
	 * @return the govEPNoCriterias
	 */
	public List<ScGovEPNoCriteria> getGovEPNoCriterias() {
		return govEPNoCriterias;
	}

	/**
	 * @param govEPNoCriterias the govEPNoCriterias to set
	 */
	public void setGovEPNoCriterias(List<ScGovEPNoCriteria> govEPNoCriterias) {
		this.govEPNoCriterias = govEPNoCriterias;
	}

	/**
	 * @return the selectedGovEPNoCriterias
	 */
	public List<ScGovEPNoCriteria> getSelectedGovEPNoCriterias() {
		return selectedGovEPNoCriterias;
	}

	/**
	 * @param selectedGovEPNoCriterias the selectedGovEPNoCriterias to set
	 */
	public void setSelectedGovEPNoCriterias(
			List<ScGovEPNoCriteria> selectedGovEPNoCriterias) {
		this.selectedGovEPNoCriterias = selectedGovEPNoCriterias;
	}

	/**
	 * @return the duration
	 */
	public String getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}

	/**
	 * @return the productTpls
	 */
	public List<TplSpecMaster> getProductTpls() {
		return productTpls;
	}

	/**
	 * @param productTpls the productTpls to set
	 */
	public void setProductTpls(List<TplSpecMaster> productTpls) {
		this.productTpls = productTpls;
	}

	/**
	 * @return the serviceTpls
	 */
	public List<TplSpecMaster> getServiceTpls() {
		return serviceTpls;
	}

	/**
	 * @param serviceTpls the serviceTpls to set
	 */
	public void setServiceTpls(List<TplSpecMaster> serviceTpls) {
		this.serviceTpls = serviceTpls;
	}

	
	
}
