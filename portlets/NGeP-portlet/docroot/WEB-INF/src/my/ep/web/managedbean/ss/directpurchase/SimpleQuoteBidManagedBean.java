package my.ep.web.managedbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ss.directpurchase.ScBusinessNatureQuoteBidCriteria;
import my.ep.persistence.entity.ss.directpurchase.ScQuoteBid;
import my.ep.persistence.entity.ss.directpurchase.ScQuoteBidSupplier;
import my.ep.persistence.entity.ss.directpurchase.ScRequestList;
import my.ep.persistence.entity.ss.directpurchase.ScRequestListItem;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.ss.helper.LocationSingleton;

import org.primefaces.model.TreeNode;
@ManagedBean
@SessionScoped
public class SimpleQuoteBidManagedBean extends BaseManagedBean{
	private	ScRequestList	scRequestList;
	private	ScQuoteBid		scQuoteBid;
	private	String	action = "";
	
	private	List<ScRequestListItem>	quoteBidItems = new ArrayList<ScRequestListItem>();
	private	List<ScRequestListItem>	selectedItems	=	new ArrayList<ScRequestListItem>();
	
	private	List<ScQuoteBidSupplier>	quoteBidSuppliers	=	new ArrayList<ScQuoteBidSupplier>();
	private	List<ScBusinessNatureQuoteBidCriteria>	businessNatureCriterias	=	new ArrayList<ScBusinessNatureQuoteBidCriteria>();
	private	String	zoneName;
	private List<TreeNode>	zoneTrees = new ArrayList<TreeNode>();
	private	List<TreeNode>	selectedZoneTree	=	new ArrayList<TreeNode>();
	private	TreeNode	rootLocationTree;
	private TreeNode[] selectedNodes;
	
	private	String	supplierFilterType;
	private	String	supplierFilterValue;
	private	int	activeTabIndex	=	0;
	
	private static final long serialVersionUID = 1L;

	public SimpleQuoteBidManagedBean() {
		prepareDummyData();
		buildLocationTree();
	}
	
	private	void	prepareDummyData(){
		//TODO	hard code dummy data, remove it later
		ScBusinessNatureQuoteBidCriteria	criteria1	=	new ScBusinessNatureQuoteBidCriteria();
		criteria1.setCriteriaValue("100201");
		businessNatureCriterias.add(criteria1);
	}
	
	private	void	buildLocationTree(){
		rootLocationTree = LocationSingleton.getInstance().buildLocationTree(1L,null, null, null, null);
	}
	
	public ScRequestList getScRequestList() {
		return scRequestList;
	}

	public void setScRequestList(ScRequestList scRequestList) {
		this.scRequestList = scRequestList;
	}

	public ScQuoteBid getScQuoteBid() {
		return scQuoteBid;
	}

	public void setScQuoteBid(ScQuoteBid scQuoteBid) {
		this.scQuoteBid = scQuoteBid;
	}

	public List<ScRequestListItem> getQuoteBidItems() {
		return quoteBidItems;
	}

	public void setQuoteBidItems(List<ScRequestListItem> quoteBidItems) {
		this.quoteBidItems = quoteBidItems;
	}

	public List<ScBusinessNatureQuoteBidCriteria> getBusinessNatureCriterias() {
		return businessNatureCriterias;
	}

	public void setBusinessNatureCriterias(List<ScBusinessNatureQuoteBidCriteria> businessNatureCriterias) {
		this.businessNatureCriterias = businessNatureCriterias;
	}

	public String getSupplierFilterType() {
		return supplierFilterType;
	}

	public void setSupplierFilterType(String supplierFilterType) {
		this.supplierFilterType = supplierFilterType;
	}

	public String getSupplierFilterValue() {
		return supplierFilterValue;
	}

	public void setSupplierFilterValue(String supplierFilterValue) {
		this.supplierFilterValue = supplierFilterValue;
	}

	public List<ScQuoteBidSupplier> getQuoteBidSuppliers() {
		return quoteBidSuppliers;
	}

	public void setQuoteBidSuppliers(List<ScQuoteBidSupplier> quoteBidSuppliers) {
		this.quoteBidSuppliers = quoteBidSuppliers;
	}

	public TreeNode getRootLocationTree() {
		return rootLocationTree;
	}

	public void setRootLocationTree(TreeNode rootLocationTree) {
		this.rootLocationTree = rootLocationTree;
	}

	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<ScRequestListItem> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<ScRequestListItem> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public List<TreeNode> getZoneTrees() {
		return zoneTrees;
	}

	public void setZoneTrees(List<TreeNode> zoneTrees) {
		this.zoneTrees = zoneTrees;
	}

	public List<TreeNode> getSelectedZoneTree() {
		return selectedZoneTree;
	}

	public void setSelectedZoneTree(List<TreeNode> selectedZoneTree) {
		this.selectedZoneTree = selectedZoneTree;
	}

	public int getActiveTabIndex() {
		return activeTabIndex;
	}

	public void setActiveTabIndex(int activeTabIndex) {
		this.activeTabIndex = activeTabIndex;
	}
	
}
