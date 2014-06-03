package my.ep.web.managedbean.sc.directpurchase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.directpurchase.ScBusinessNatureQuoteCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteSupplier;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.sc.helper.LocationSingleton;

import org.primefaces.model.TreeNode;
@ManagedBean
@SessionScoped
public class SimpleQuoteBidManagedBean extends BaseManagedBean{
	private	ScRequestNote	scRequestList;
	private	ScQuote		scQuoteBid;
	private	String	action = "";
	
	private	List<ScRequestItem>	quoteBidItems = new ArrayList<ScRequestItem>();
	private	List<ScRequestItem>	selectedItems	=	new ArrayList<ScRequestItem>();
	
	private	List<ScQuoteSupplier>	quoteBidSuppliers	=	new ArrayList<ScQuoteSupplier>();
	private	List<ScBusinessNatureQuoteCriteria>	businessNatureCriterias	=	new ArrayList<ScBusinessNatureQuoteCriteria>();
	private	List<ScBusinessNatureQuoteCriteria>	selectedBusinessNatureCriterias	=	new ArrayList<ScBusinessNatureQuoteCriteria>();
	
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
		ScBusinessNatureQuoteCriteria	criteria1	=	new ScBusinessNatureQuoteCriteria();
		//criteria1.setCriteriaValue("100201");
		businessNatureCriterias.add(criteria1);
	}
	
	private	void	buildLocationTree(){
		rootLocationTree = LocationSingleton.getInstance().buildLocationTree(1L,null, null, null, null);
	}
	
	public ScRequestNote getScRequestList() {
		return scRequestList;
	}

	public void setScRequestList(ScRequestNote scRequestList) {
		this.scRequestList = scRequestList;
	}

	public ScQuote getScQuoteBid() {
		return scQuoteBid;
	}

	public void setScQuoteBid(ScQuote scQuoteBid) {
		this.scQuoteBid = scQuoteBid;
	}

	public List<ScRequestItem> getQuoteBidItems() {
		return quoteBidItems;
	}

	public void setQuoteBidItems(List<ScRequestItem> quoteBidItems) {
		this.quoteBidItems = quoteBidItems;
	}

	public List<ScBusinessNatureQuoteCriteria> getBusinessNatureCriterias() {
		return businessNatureCriterias;
	}

	public void setBusinessNatureCriterias(List<ScBusinessNatureQuoteCriteria> businessNatureCriterias) {
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

	public List<ScQuoteSupplier> getQuoteBidSuppliers() {
		return quoteBidSuppliers;
	}

	public void setQuoteBidSuppliers(List<ScQuoteSupplier> quoteBidSuppliers) {
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

	public List<ScRequestItem> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<ScRequestItem> selectedItems) {
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

	public List<ScBusinessNatureQuoteCriteria> getSelectedBusinessNatureCriterias() {
		return selectedBusinessNatureCriterias;
	}

	public void setSelectedBusinessNatureCriterias(List<ScBusinessNatureQuoteCriteria> selectedBusinessNatureCriterias) {
		this.selectedBusinessNatureCriterias = selectedBusinessNatureCriterias;
	}
	
}
