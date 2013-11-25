package my.ep.web.managedbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.ss.directpurchase.ScBusinessNatureQuoteBidCriteria;
import my.ep.persistence.entity.ss.directpurchase.ScQuoteBid;
import my.ep.persistence.entity.ss.directpurchase.ScQuoteBidSupplier;
import my.ep.persistence.entity.ss.directpurchase.ScRequestList;
import my.ep.persistence.entity.ss.directpurchase.ScRequestListItem;
import my.ep.web.backingbean.ss.directpurchase.LocationDTO;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
@ManagedBean
@SessionScoped
public class SimpleQuoteBidManagedBean extends BaseManagedBean{
	private	ScRequestList	scRequestList;
	private	ScQuoteBid		scQuoteBid;
	
	private	List<ScRequestListItem>	quoteBidItems = new ArrayList<ScRequestListItem>();
	private	List<ScQuoteBidSupplier>	quoteBidSuppliers	=	new ArrayList<ScQuoteBidSupplier>();
	private	List<ScBusinessNatureQuoteBidCriteria>	businessNatureCriterias	=	new ArrayList<ScBusinessNatureQuoteBidCriteria>();
	private	String	zoneName;
	private TreeNode	rootZoneNode;
	private	TreeNode	rootLocationTree;
	private TreeNode[] selectedNodes;
	
	private	String	supplierFilterType;
	private	String	supplierFilterValue;
	
	private	List<PmState>	states	=	new ArrayList<PmState>();
	
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
		IStateSO stateSO	=	(IStateSO)BusinessDelegate.getInstance().getServiceObject("pm", IStateSO.class);
		IDistrictSO	districtSO	=	(IDistrictSO)BusinessDelegate.getInstance().getServiceObject("pm", IDistrictSO.class);
		StateReqDTO	requestDTO	=	new StateReqDTO();
		requestDTO.setCountryId(1L);//TODO malaysia
		StateResDTO responseDTO	=	stateSO.retriveStatesListByCountryID(requestDTO);
		rootLocationTree = new DefaultTreeNode("root", null);
		for(PmState state: responseDTO.getStateList()){
			LocationDTO stateLocation	=	new LocationDTO(state);
			TreeNode stateNote	=	new DefaultTreeNode(stateLocation , rootLocationTree);
			DistrictReqDTO	districtReqDTO	=	new DistrictReqDTO();
			districtReqDTO.setStateId(state.getStateId());
			DistrictResDTO districtResDTO = districtSO.retriveDistrictListByStateID(districtReqDTO);
			for(PmDistrict district: districtResDTO.getDistrictList()){
				LocationDTO districtLocation	=	new LocationDTO(district);
				TreeNode districtNote	=	new DefaultTreeNode(districtLocation, stateNote);
			}
		}
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

	public List<PmState> getStates() {
		return states;
	}

	public void setStates(List<PmState> states) {
		this.states = states;
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

	public TreeNode getRootZoneNode() {
		return rootZoneNode;
	}

	public void setRootZoneNode(TreeNode rootZoneNode) {
		this.rootZoneNode = rootZoneNode;
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
	
}
