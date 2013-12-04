package my.ep.web.backingbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.ss.directpurchase.IQuoteBidSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.ss.directpurchase.QuoteBidReqDTO;
import my.ep.dto.ss.directpurchase.QuoteBidResDTO;
import my.ep.dto.ss.directpurchase.RequestListResDTO;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.ss.directpurchase.ScApprovalUser;
import my.ep.persistence.entity.ss.directpurchase.ScBusinessNatureQuoteBidCriteria;
import my.ep.persistence.entity.ss.directpurchase.ScRequestListItem;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.ss.directpurchase.SimpleQuoteBidManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.ss.helper.LocationSingleton;
import my.ep.web.util.ss.helper.SourcingLocationHelper;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "simpleQuoteBidBackingBean")
@RequestScoped
public class SimpleQuoteBidBackingBean extends BaseBackingBean<SimpleQuoteBidManagedBean> {
	private static Logger log = Logger.getLogger(SimpleQuoteBidBackingBean.class);
	public SimpleQuoteBidBackingBean() {
		super();
		
	}

	public void saveQuoteBid(String action) {
		QuoteBidReqDTO reqDTO = new QuoteBidReqDTO();
		reqDTO.setQuoteBidDtl(this.getManagedBean().getScQuoteBid());
		IQuoteBidSO quoteBidSO = (IQuoteBidSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IQuoteBidSO.class);
		if ("saveQuote".equalsIgnoreCase(action)) {
			reqDTO.getQuoteBidDtl().setIsQuote(1);
		}
		QuoteBidResDTO resDTO = quoteBidSO.saveQuoteBidDtl(reqDTO);
		getManagedBean().setScQuoteBid(reqDTO.getQuoteBidDtl());

	}
	public	void	supplierSubmit(){
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		FacesScopes facesScopes = new FacesScopes();
		String token = facesScopes.getBPMAuthenticationCtxToken();

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(token);
		reqDTO.setProcessId(SCProcessNameConst.DIRECTPURCHASE_CREATION_PROCESS_ID);
		SCDirectPurchaseDataType obj = new SCDirectPurchaseDataType();
		obj.setDpId(getManagedBean().getScQuoteBid().getScRequestList().getRequestListId().intValue());
		obj.setPtj("Asdqwe");
		reqDTO.setOutcome("SUBMIT");
		if(getManagedBean().getTaskId() != null){
			reqDTO.setTaskId(getManagedBean().getTaskId());
		}
		reqDTO.setPojo(obj);
		BpmResponseDTO res = bpmSO.initiateAndUpdateTask(reqDTO);
		log.info("done submit.....");
	}
	public	void	submitQuoteBid(String action){
		QuoteBidReqDTO reqDTO = new QuoteBidReqDTO();
		reqDTO.setQuoteBidDtl(this.getManagedBean().getScQuoteBid());
		IQuoteBidSO quoteBidSO = (IQuoteBidSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IQuoteBidSO.class);
		if ("saveQuote".equalsIgnoreCase(action)) {
			reqDTO.getQuoteBidDtl().setIsQuote(1);
		}
		QuoteBidResDTO resDTO = quoteBidSO.saveQuoteBidDtl(reqDTO);
		getManagedBean().setScQuoteBid(reqDTO.getQuoteBidDtl());
		createBPMTask(true);
	}
	public String updateZoneLocation() {
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
		log.info("start show Zone location");
		log.info("success");
	}
	public	void	showZoneLocationPopup(){
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
	
	private	BpmResponseDTO	createBPMTask(Boolean submit){
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		FacesScopes facesScopes = new FacesScopes();
		String token = facesScopes.getBPMAuthenticationCtxToken();

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(token);
		reqDTO.setProcessId(SCProcessNameConst.DIRECTPURCHASE_CREATION_PROCESS_ID);
		SCDirectPurchaseDataType obj = new SCDirectPurchaseDataType();
		obj.setDpId(getManagedBean().getScQuoteBid().getScRequestList().getRequestListId().intValue());
		obj.setPtj("Asdqwe");
		if(submit){
			obj.getSuppliers().clear();
			//TODO hardcode supplier user to test BPM flow
			obj.getSuppliers().add("user011");
			if(getManagedBean().getScQuoteBid().getIsQuote() == 1){
				obj.setDpMode("SQ");
			}else{
				obj.setDpMode("SB");
			}
			reqDTO.setOutcome("SUBMIT");
		}
		if(getManagedBean().getTaskId() != null){
			reqDTO.setTaskId(getManagedBean().getTaskId());
		}
		reqDTO.setPojo(obj);
		BpmResponseDTO res = bpmSO.initiateAndUpdateTask(reqDTO);
		return res;
	}
}
