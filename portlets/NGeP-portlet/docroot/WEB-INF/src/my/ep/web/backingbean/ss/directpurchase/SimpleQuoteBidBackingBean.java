package my.ep.web.backingbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.service.ss.directpurchase.IQuoteBidSO;
import my.ep.dto.ss.directpurchase.QuoteBidReqDTO;
import my.ep.dto.ss.directpurchase.QuoteBidResDTO;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.ss.directpurchase.ScBusinessNatureQuoteBidCriteria;
import my.ep.persistence.entity.ss.directpurchase.ScRequestListItem;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.ss.directpurchase.SimpleQuoteBidManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.ss.helper.LocationSingleton;
import my.ep.web.util.ss.helper.SourcingLocationHelper;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "simpleQuoteBidBackingBean")
@SessionScoped
public class SimpleQuoteBidBackingBean extends BaseBackingBean<SimpleQuoteBidManagedBean> {
	public SimpleQuoteBidBackingBean() {
		super();
	}

	public void saveQuoteBid(String action) {
		QuoteBidReqDTO reqDTO = new QuoteBidReqDTO();
		reqDTO.setQuoteBidDtl(this.getManagedBean().getScQuoteBid());
		IQuoteBidSO quoteBidSO = (IQuoteBidSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IQuoteBidSO.class);
		if ("saveQuote".equalsIgnoreCase(action)) {
			reqDTO.getQuoteBidDtl().setIsQuote(1);
			QuoteBidResDTO resDTO = quoteBidSO.saveQuoteBidDtl(reqDTO);
		}

	}

	public String updateZoneLocation() {
		/*ScQBZone zone = createScQBZone();
		if (getManagedBean().getScQuoteBid().getScQBZones() != null) {
			getManagedBean().getScQuoteBid().getScQBZones().add(zone);
		} else {
			List<ScQBZone> scQBZones = new ArrayList<ScQBZone>();
			scQBZones.add(zone);
			getManagedBean().getScQuoteBid().setScQBZones(scQBZones);
		}*/
		getManagedBean().getZoneTrees().add(
				LocationSingleton.getInstance().buildZoneTree(getSelectedTreeNode()));
		getManagedBean().setActiveTabIndex(1);
		return "createSimpleQuoteBid";
	}

	public void addBusinessNature() {
		ScBusinessNatureQuoteBidCriteria businessNature = new ScBusinessNatureQuoteBidCriteria();
		getManagedBean().getBusinessNatureCriterias().add(businessNature);
	}
	public	void	editZoneLocation(TreeNode rootZoneLocation){
		List<LocationDTO>	locationDTOs	=	getLocations(rootZoneLocation);
		List<PmState>	states	=	SourcingLocationHelper.getStateFromLocationDTOs(locationDTOs);
		List<PmDivision>	divisions	=	SourcingLocationHelper.getDivisionFromLocationDTOs(locationDTOs);
		List<PmDistrict>	districts	=	SourcingLocationHelper.getDistrictFromLocationDTOs(locationDTOs);
		List<PmCity>	cities	=	SourcingLocationHelper.getCityFromLocationDTOs(locationDTOs);
		getManagedBean().setRootLocationTree(LocationSingleton.getInstance().buildLocationTree(1L, states, districts, divisions, cities));
	}
	public	void	zoneLocationPopup(){
		getManagedBean().setRootLocationTree(LocationSingleton.getInstance().buildLocationTree(1L, null, null, null, null));
	}

	public void addItem() {
		ScRequestListItem item = new ScRequestListItem();
		getManagedBean().getQuoteBidItems().add(item);
	}
	private	Map<String,List<TreeNode>>	getSelectedTreeNode(){
		Map<String,List<TreeNode>>	results	=	new HashMap<String, List<TreeNode>>();
		List<TreeNode>	listTreeNode	=	new ArrayList<>();
		//TreeNode[]	listSelectedTreeNode	=	getManagedBean().getSelectedNodes().clone();
		if (getManagedBean().getSelectedNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedNodes()) {
				if (!treeNode.getParent().isSelected()) {
					TreeNode	tmpTreeNode	=	new DefaultTreeNode(treeNode.getData(), treeNode.getParent());
					listTreeNode.add(tmpTreeNode);
				}
			}
		}
		results.put(getManagedBean().getZoneName(), listTreeNode);
		//Reset manage bean
		getManagedBean().setZoneName("");
		return results;
	}
	private	List<LocationDTO>	getLocations(TreeNode zoneLocation){
		List<LocationDTO> results	=	new ArrayList<LocationDTO>();
		if(zoneLocation.isLeaf()){
			LocationDTO	locationDTO	=	(LocationDTO)zoneLocation.getData();
			results.add(locationDTO);
		}else{
			for(TreeNode childrendLocationNode: zoneLocation.getChildren()){
				results.addAll(getLocations(childrendLocationNode));
			}
		}
		return results;
	}
}
