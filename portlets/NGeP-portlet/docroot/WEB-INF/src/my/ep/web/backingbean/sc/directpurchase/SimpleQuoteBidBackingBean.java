package my.ep.web.backingbean.sc.directpurchase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sc.directpurchase.IQuoteSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.sc.directpurchase.QuoteReqDTO;
import my.ep.dto.sc.directpurchase.QuoteResDTO;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.directpurchase.ScBusinessNatureQuoteCriteria;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteZone;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.directpurchase.SimpleQuoteBidManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "simpleQuoteBidBackingBean")
@RequestScoped
public class SimpleQuoteBidBackingBean extends BaseBackingBean<SimpleQuoteBidManagedBean> {
	private static Logger log = Logger.getLogger(SimpleQuoteBidBackingBean.class);
	public SimpleQuoteBidBackingBean() {
		super();
		
	}
	public	void	openMarketResearch(){
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "directPurchase");
	}
	public void saveQuoteBid(String action) {
		QuoteReqDTO reqDTO = new QuoteReqDTO();
		reqDTO.setQuoteBidDtl(this.getManagedBean().getScQuoteBid());
		IQuoteSO quoteBidSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
		QuoteResDTO resDTO = quoteBidSO.saveQuoteDtl(reqDTO);
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
		obj.setDpId(getManagedBean().getScQuoteBid().getQuoteId().intValue());
		//obj.setPtj("Asdqwe");
		reqDTO.setOutcome("SUBMIT");
		if(getManagedBean().getTaskId() != null){
			reqDTO.setTaskId(getManagedBean().getTaskId());
		}
		reqDTO.setPojo(obj);
		BpmResponseDTO res = bpmSO.initiateAndUpdateTask(reqDTO);
		log.info("done submit.....");
	}
	public	void	submitQuoteBid(String action){
		QuoteReqDTO reqDTO = new QuoteReqDTO();
		reqDTO.setQuoteBidDtl(this.getManagedBean().getScQuoteBid());
		IQuoteSO quoteBidSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQuoteSO.class);
		QuoteResDTO resDTO = quoteBidSO.saveQuoteDtl(reqDTO);
		getManagedBean().setScQuoteBid(reqDTO.getQuoteBidDtl());
		createBPMTask(true);
	}
	public void updateZoneLocation() {
		List<TplSpecLoc>	locationDTOs	=	new ArrayList<TplSpecLoc>();
		//TreeNode[]	listSelectedTreeNode	=	getManagedBean().getSelectedNodes().clone();
		if (getManagedBean().getSelectedNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedNodes()) {
				if (!treeNode.getParent().isSelected()) {
					TplSpecLoc	locationDTO	=	(TplSpecLoc)treeNode.getData();
					locationDTOs.add(locationDTO);
				}
			}
		}
		TplSpecZoneDtl zone = new TplSpecZoneDtl();
		zone.setZoneName(getManagedBean().getZoneName());
		getManagedBean().setZoneName(null);
		
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTO(locationDTOs);
		
		getManagedBean().getZoneTrees().add(LocationSingleton.getInstance().buildZoneTree(zone, states, districts, divisions, cities));
		getManagedBean().setRootLocationTree(LocationSingleton.getInstance().buildLocationTree(1L, null, null, null, null));
		getManagedBean().setActiveTabIndex(1);
		//return "createSimpleQuoteBid";
	}

	public void addBusinessNature() {
		ScBusinessNatureQuoteCriteria businessNature = new ScBusinessNatureQuoteCriteria();
		getManagedBean().getBusinessNatureCriterias().add(businessNature);
	}
	public	void	editZoneLocation(TreeNode rootZoneLocation){
		List<TplSpecLoc>	locationDTOs	=	getLocations(rootZoneLocation);
		ScQuoteZone	zone	=	getZone(rootZoneLocation);
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTO(locationDTOs);
		getManagedBean().setRootLocationTree(LocationSingleton.getInstance().buildLocationTree(1L, states, districts, divisions, cities));
		if(zone != null){
			//getManagedBean().setZoneName(zone.getZoneName());
		}
	}
	public	void	showZoneLocationPopup(){
		getManagedBean().setRootLocationTree(LocationSingleton.getInstance().buildLocationTree(1L, null, null, null, null));
	}

	public void addItem() {
		ScRequestItem item = new ScRequestItem();
		getManagedBean().getQuoteBidItems().add(item);
	}
	private	List<TplSpecLoc>	getLocations(TreeNode zoneLocation){
		List<TplSpecLoc> results	=	new ArrayList<TplSpecLoc>();
		if(zoneLocation.isLeaf()){
			TplSpecLoc	locationDTO	=	(TplSpecLoc)zoneLocation.getData();
			results.add(locationDTO);
		}else{
			for(TreeNode childrendLocationNode: zoneLocation.getChildren()){
				results.addAll(getLocations(childrendLocationNode));
			}
		}
		return results;
	}
	private	ScQuoteZone	getZone(TreeNode zoneLocation){
		ScQuoteZone result	=	null;
		if(zoneLocation.getData() instanceof LocationDTO){
			LocationDTO locationDTO	=	(LocationDTO)zoneLocation.getData();
			if(locationDTO.getZone() != null){
				result = locationDTO.getZone();
			}else{
				if(zoneLocation.getChildren() != null && zoneLocation.getChildren().size() > 0){
					for(TreeNode childLocationNode: zoneLocation.getChildren()){
						result	=	getZone(childLocationNode);
					}
				}
			}
		}else{
			if(zoneLocation.getChildren() != null && zoneLocation.getChildren().size() > 0){
				for(TreeNode childLocationNode: zoneLocation.getChildren()){
					result	=	getZone(childLocationNode);
				}
			}
		}
		
		return result;
	}
	private	BpmResponseDTO	createBPMTask(Boolean submit){
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		FacesScopes facesScopes = new FacesScopes();
		String token = facesScopes.getBPMAuthenticationCtxToken();

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(token);
		reqDTO.setProcessId(SCProcessNameConst.DIRECTPURCHASE_CREATION_PROCESS_ID);
		SCDirectPurchaseDataType obj = new SCDirectPurchaseDataType();
		obj.setDpId(getManagedBean().getScQuoteBid().getQuoteId().intValue());
		//obj.setPtj("Asdqwe");
		if(submit){
			obj.getSuppliers().clear();
			//TODO hardcode supplier user to test BPM flow
			obj.getSuppliers().add("user011");
			if(DPStatusConstants.CREATE_SIMPLE_QUOTE.equalsIgnoreCase(getManagedBean().getAction())){
				obj.setDpMode("SQ");
			}else{
				obj.setDpMode("SB");
			}
			/*if(getManagedBean().getScQuoteBid().getIsQuote() == 1){
				obj.setDpMode("SQ");
			}else{
				obj.setDpMode("SB");
			}*/
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
