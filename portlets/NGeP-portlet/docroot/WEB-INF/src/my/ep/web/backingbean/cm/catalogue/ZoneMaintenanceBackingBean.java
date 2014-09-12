package my.ep.web.backingbean.cm.catalogue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.TreeNode;

import my.ep.biz.service.cm.catalogue.IZoneMaintenanceSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.dto.cm.catalogue.ZoneMaintenanceReqDTO;
import my.ep.dto.cm.catalogue.ZoneMaintenanceResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmSupplierZone;
import my.ep.persistence.entity.cm.catalog.CmSupplierZoneDTL;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.catalogue.ZoneMaintenanceManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.cm.helper.CmSupplierZoneSingleton;
import my.ep.web.util.cm.helper.CmSupplierZoneStateDistrictDTO;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name = "zoneMaintenanceBackingBean")
@ViewScoped
public class ZoneMaintenanceBackingBean extends
BaseBackingBean<ZoneMaintenanceManagedBean> {

	public ZoneMaintenanceBackingBean() {
		System.out.println("********************ZoneMaintenanceBackingBean Constructor***************");
	}

	public void preRenderZoneView(){
		System.out.println("********************ZoneMaintenanceBackingBean PreRender***************");
		getManagedBean().setCurrentSupplier(getCurrentUserSupplier());
		showZonePopup();
		getManagedBean().setSelectedSupplierZone(null);
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode("MY");
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject("pm",ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);
		getManagedBean().setDefaultCountryId(resDTO.getPmCountry().getCountryId());
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "cmSupplierZoneMaintenance");

	}
	
	private CmSupplierZone getZone(TreeNode zoneLocation) {
		CmSupplierZone result = null;
		if (zoneLocation.getData() instanceof CmSupplierZoneStateDistrictDTO) {
			CmSupplierZoneStateDistrictDTO cmDTO = (CmSupplierZoneStateDistrictDTO) zoneLocation.getData();
			if (cmDTO.getCmSupplierZone() != null) {
				result = cmDTO.getCmSupplierZone();
			} else {
				if (zoneLocation.getChildren() != null
						&& zoneLocation.getChildren().size() > 0) {
					for (TreeNode childLocationNode : zoneLocation
							.getChildren()) {
						result = getZone(childLocationNode);
					}
				}
			}
		} else {
			if (zoneLocation.getChildren() != null
					&& zoneLocation.getChildren().size() > 0) {
				for (TreeNode childLocationNode : zoneLocation.getChildren()) {
					result = getZone(childLocationNode);
				}
			}
		}
		return result;
	}

	public void deleteTreeNodes(TreeNode rootZoneLocation) {
		CmSupplierZone zone = getZone(rootZoneLocation);
		List<CmSupplierZone> deleteList = new ArrayList<CmSupplierZone>();
		if(getManagedBean().getDeletedCmSupplierZoneList() != null && getManagedBean().getDeletedCmSupplierZoneList().size() > 0){
			deleteList = getManagedBean().getDeletedCmSupplierZoneList();
		}
			Iterator<CmSupplierZone> ite = getManagedBean().getCmSupplierZoneList().iterator();
			System.out.println("Checking for Delete");
			while(ite.hasNext()){
				CmSupplierZone cmZone = ite.next(); 
				if(cmZone.getSuppZoneName().equalsIgnoreCase(zone.getSuppZoneName())){
					System.out.println("Found it");
					if(cmZone.getCmPricings() != null && cmZone.getCmPricings().size() > 0){
						throw new EpException(MessageConst.ERROR_716005);
					}
					System.out.println("Size Before:" + getManagedBean().getCmSupplierZoneList().size());
					deleteList.add(cmZone);
					ite.remove();
					System.out.println("Size After:" + getManagedBean().getCmSupplierZoneList().size());
				}
			}
		getManagedBean().setDeletedCmSupplierZoneList(deleteList);	
		getManagedBean().setZoneTrees(CmSupplierZoneSingleton.getInstance().buildZoneSDTree(this.getManagedBean().getCmSupplierZoneList()));
	  }
	
	public void deleteCmSupplierZones(){
		System.out.println("********************ZoneMaintenanceBackingBean deleteCmSupplierZones***************");
		IZoneMaintenanceSO zoneMaintenanceSO = null;
		zoneMaintenanceSO = (IZoneMaintenanceSO) BusinessDelegate
					.getInstance().getServiceObject("cm", IZoneMaintenanceSO.class);
		ZoneMaintenanceReqDTO reqDTO = new ZoneMaintenanceReqDTO();
		reqDTO.setCmSupplierZoneList(getManagedBean().getDeletedCmSupplierZoneList());
		ZoneMaintenanceResDTO resDTO = new ZoneMaintenanceResDTO(); 
		if(getManagedBean().getDeletedCmSupplierZoneList().size() > 0)
			resDTO = zoneMaintenanceSO.delete(reqDTO);
		else 
			resDTO.setSuccessful(true);
		getManagedBean().setZoneTrees(CmSupplierZoneSingleton.getInstance().buildZoneSDTree(this.getManagedBean().getCmSupplierZoneList()));
		if (resDTO.isSuccessful()) {
			System.out.println("SuccessMMMMMMMMMMMMMMMMM");
		} else {
			System.out.println("Not SuccessMMMMMMMMMMMMMMMMM");
			throw new EpException(MessageConst.ERROR_716001);
		}
	}
	
	/**
	 * saveCmSupplierZone method
	 */
	public String saveCmSupplierZones() {
		System.out.println("********************ZoneMaintenanceBackingBean saveCmSupplierZones***************");
		assignValReqdToFalse();
		/* First delete any records that are to be deleted */
		if(getManagedBean().getDeletedCmSupplierZoneList() != null)
			deleteCmSupplierZones();
		IZoneMaintenanceSO zoneMaintenanceSO = null;
		zoneMaintenanceSO = (IZoneMaintenanceSO) BusinessDelegate
				.getInstance().getServiceObject("cm", IZoneMaintenanceSO.class);
		ZoneMaintenanceReqDTO reqDTO = new ZoneMaintenanceReqDTO();
		reqDTO.setCmSupplierZoneList(getManagedBean().getCmSupplierZoneList());
		System.out.println("Current Size:" + getManagedBean().getCmSupplierZoneList().size());
		ZoneMaintenanceResDTO resDTO = new ZoneMaintenanceResDTO();
		if(getManagedBean().getCmSupplierZoneList().size() > 0)
			resDTO = zoneMaintenanceSO.save(reqDTO);
		else 
			resDTO.setSuccessful(true);
		
		if(resDTO.isSuccessful()){
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					null,
					MessageUtils.getMessage(MessageConst.INFO_711001, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			return "outputMsg";
		}
		else 
			return null;
		
//		if (resDTO.isSuccessful()) {
//			System.out.println("SuccessMMMMMMMMMMMMMMMMM");
//			showZonePopup();
//			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
//					FacesMessage.SEVERITY_INFO, null, 
//					MessageUtils.getMessage(MessageConst.INFO_711001, 
//							new String[]{""+1}, 
//							MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
//			return null;
//		} else {
//			System.out.println("Not SuccessMMMMMMMMMMMMMMMMM");
//			zoneMaintenanceSO = null;
//			throw new EpException(MessageConst.ERROR_716002);
//		}
	}
	
	public void editZone(TreeNode rootZoneLocation, int zoneIndex) {
		getManagedBean().setEditMode(true);
//		List<CmSupplierZoneStateDistrictDTO> detailDTOs = getDetails(rootZoneLocation);
		CmSupplierZone zone = getZone(rootZoneLocation);
//		List<PmState> states = SourcingZoneDetailHelper
//				.getStateFromDetailDTOs(detailDTOs);
//		List<PmDivision> divisions = SourcingZoneDetailHelper
//				.getDivisionFromDetailDTOs(detailDTOs);
//		List<PmDistrict> districts = SourcingZoneDetailHelper
//				.getDistrictFromDetailDTOs(detailDTOs);

		List<PmState> states = getPmStates(rootZoneLocation.getChildren());
		List<PmDivision> divisions = getPmDivisions(rootZoneLocation.getChildren());
		List<PmDistrict> districts = getPmDistricts(rootZoneLocation.getChildren());
		getManagedBean().setRootZoneTree(
				CmSupplierZoneSingleton.getInstance().buildTree(getManagedBean().getDefaultCountryId(),	states, districts, divisions));
		int i = zoneIndex;
		this.getManagedBean().setZoneIndex(i);
		getManagedBean().setZoneTrees(CmSupplierZoneSingleton.getInstance().buildZoneSDTree(this.getManagedBean().getCmSupplierZoneList()));
		for(CmSupplierZone suppZone : getManagedBean().getCmSupplierZoneList()){
			if(zone.getSuppZoneId() == suppZone.getSuppZoneId()){
				getManagedBean().setSelectedSupplierZone(suppZone);
				getManagedBean().setZoneName(suppZone.getSuppZoneName());
			}
		}
	}
	
	public void addNewZone() {
		System.out.println("********************ZoneMaintenanceBackingBean addNewZone***************");
		getManagedBean().setEditMode(false);
		assignValReqdToTrue();
		getManagedBean().setSelectedSupplierZone(new CmSupplierZone());
		getManagedBean().getSelectedSupplierZone().setSuppZoneName(null);
		getManagedBean().setRootZoneTree(
				CmSupplierZoneSingleton.getInstance().buildCmSupplierZoneTree(getManagedBean().getDefaultCountryId(), null, null, null));
	}
	
	/**
    * showZonePopup method for zone popup
    */
	public void showZonePopup() {
		SmSupplier smSupplier = getManagedBean().getCurrentSupplier();
		if(smSupplier != null && smSupplier.getSupplierId() != null){
			IZoneMaintenanceSO zoneMaintenanceSO = (IZoneMaintenanceSO) BusinessDelegate
					.getInstance().getServiceObject("cm", IZoneMaintenanceSO.class);
			ZoneMaintenanceReqDTO reqDTO = new ZoneMaintenanceReqDTO();
			reqDTO.setSupplierId(smSupplier.getSupplierId());
			ZoneMaintenanceResDTO resDTO = zoneMaintenanceSO.getCmSupplierZonesBySupplier(reqDTO);
			this.getManagedBean().setCmSupplierZoneList(resDTO.getCmSupplierZones());
		} else {
			this.getManagedBean().setCmSupplierZoneList(new ArrayList<CmSupplierZone>());
		}
			this.getManagedBean().setDeletedCmSupplierZoneList(null);
			this.getManagedBean().setZoneTrees(CmSupplierZoneSingleton.getInstance().buildZoneSDTree(this.getManagedBean().getCmSupplierZoneList()));
			this.getManagedBean().setRootZoneTree(
					CmSupplierZoneSingleton.getInstance().buildCmSupplierZoneTree(getManagedBean().getDefaultCountryId(), null, null, null));
			this.getManagedBean().setZoneName(null);
	}
		
	
	private SmSupplier getCurrentUserSupplier() {
		SmSupplier smSupp = new SmSupplier();
		try {
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierSearchReqDTO.setUserId(pmUser.getUserId());
			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);
			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierSearchReqDTO);
			smSupp = (SmSupplier) smResDTO.getResultBean().getResultList()
					.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return smSupp;
	}
	
/*	private List<CmSupplierZoneStateDistrictDTO> getDetails(TreeNode zoneDetail) {
		List<CmSupplierZoneStateDistrictDTO> results = new ArrayList<CmSupplierZoneStateDistrictDTO>();
		if (zoneDetail.isLeaf()) {
			CmSupplierZoneStateDistrictDTO cmSupplierZoneStateDistrictDTO = (CmSupplierZoneStateDistrictDTO) zoneDetail.getData();
			results.add(cmSupplierZoneStateDistrictDTO);
		} else {
			for (TreeNode childrendDetailNode : zoneDetail.getChildren()) {
				results.addAll(getDetails(childrendDetailNode));
			}
		}
		return results;
	}
*/	
	private List<PmState> getPmStates(List<TreeNode> treeNodes) {
		List<PmState> results = new ArrayList<PmState>();
		for (TreeNode rootNode : treeNodes) {
			for (TreeNode treeNode : rootNode.getChildren()) {
					if (treeNode.getType().equals("state")) {
						CmSupplierZoneStateDistrictDTO locationDTO = (CmSupplierZoneStateDistrictDTO) treeNode.getData();
						if (locationDTO.getState() != null) {
							results.add(locationDTO.getState());
						}
					}
			}
		}
		return results;
	}
	
	private List<PmDivision> getPmDivisions(List<TreeNode> treeNodes) {
		List<PmDivision> results = new ArrayList<PmDivision>();
		for (TreeNode rootNode : treeNodes) {
		for (TreeNode treeNode : rootNode.getChildren()) {
			for (TreeNode childNode : treeNode.getChildren()) {
					if (childNode.getType().equals("division")) {
						CmSupplierZoneStateDistrictDTO locationDTO = (CmSupplierZoneStateDistrictDTO) childNode.getData();
						if (locationDTO.getDivision() != null) {
							results.add(locationDTO.getDivision());
						}
					}
				}
		}
		}
		return results;
	}
	
	private List<PmDistrict> getPmDistricts(List<TreeNode> treeNodes) {
		List<PmDistrict> results = new ArrayList<PmDistrict>();
		for (TreeNode treeNode : treeNodes) {
			if (treeNode.isLeaf()) {
				CmSupplierZoneStateDistrictDTO locationDTO = (CmSupplierZoneStateDistrictDTO) treeNode.getData();
				if (locationDTO.getDistrict() != null) {
					results.add(locationDTO.getDistrict());
				}
			} else {
				if (treeNode.getChildren() != null) {
					results.addAll(getPmDistricts(treeNode.getChildren()));
				}
			}
		}
		return results;
	}

	
	public void updateZoneListOnPopUpClose(){
		System.out.println("********************ZoneMaintenanceBackingBean updateZoneListOnPopUpClose***************");
		CmSupplierZone selZone = getManagedBean().getSelectedSupplierZone();
		if(selZone.getSuppZoneName().isEmpty())
			throw new EpException(MessageConst.ERROR_716003);
		else{
			if(!getManagedBean().getCmSupplierZoneList().contains(selZone)){
				for(CmSupplierZone zone : getManagedBean().getCmSupplierZoneList()){
					if(selZone.getSuppZoneName().equalsIgnoreCase(zone.getSuppZoneName())){
						throw new EpException(MessageConst.ERROR_716004);
					}
				}
			}
		}
			
		if(selZone.getSuppZoneId() == 0 && !getManagedBean().getCmSupplierZoneList().contains(selZone)){
			selZone = new CmSupplierZone();
			selZone.setSuppZoneName(getManagedBean().getSelectedSupplierZone().getSuppZoneName());
			if(selZone.getSmSupplier() == null){
				selZone.setSmSupplier(getManagedBean().getCurrentSupplier());
			}
			if(selZone.getSuppZoneDesc() == null){
				selZone.setSuppZoneDesc(selZone.getSuppZoneName());
			}
		}
		TreeNode[] selectedNodes = getManagedBean().getSelectedNodes();
		List<CmSupplierZoneDTL> newCmSupplierZoneDTLs = new ArrayList<CmSupplierZoneDTL>();
		List<Long> existingStates = new ArrayList<Long>();
		List<Long> existingDistricts = new ArrayList<Long>();
		List<Long> existingDivisions = new ArrayList<Long>();
		List<CmSupplierZoneDTL> cmDtls = new ArrayList<CmSupplierZoneDTL>(); 
		if(selZone.getCmSupplierZoneDtls() != null)
			cmDtls.addAll(selZone.getCmSupplierZoneDtls()); 
		for(CmSupplierZoneDTL cmDtl : cmDtls){
			if(cmDtl.getPmState() != null)
				existingStates.add(cmDtl.getPmState().getStateId());
			if(cmDtl.getPmDistrict() != null)
				existingDistricts.add(cmDtl.getPmDistrict().getDistrictId());
			if(cmDtl.getPmDivision() != null)
				existingDivisions.add(cmDtl.getPmDivision().getDivisionId());
		}
		
		/*
		 * New zone details added*
		 */
		List<Long> selectedStates = new ArrayList<Long>();
		List<Long> selectedDistricts = new ArrayList<Long>();
		List<Long> selectedDivisions = new ArrayList<Long>();
		for(TreeNode selectedNode : selectedNodes) {
			if(selectedNode.getType().equals("state") && selectedNode.getChildCount() == 0) {
				CmSupplierZoneStateDistrictDTO stateNodeDto = (CmSupplierZoneStateDistrictDTO) selectedNode.getData();
				if(stateNodeDto.getState() != null){
					selectedStates.add(stateNodeDto.getState().getStateId());
					if(!existingStates.contains(stateNodeDto.getState().getStateId())){
						CmSupplierZoneDTL cmDtl = new CmSupplierZoneDTL();
						cmDtl.setCmSupplierZone(selZone);
						IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject("pm",IStateSO.class);
						StateReqDTO reqStateDTO = new StateReqDTO();
						reqStateDTO.setStateId(stateNodeDto.getState().getStateId());
						StateResDTO resStateDTO = stateSO.retriveStateByID(reqStateDTO);
						PmState newState = resStateDTO.getPmState();
						cmDtl.setPmState(newState);
						newCmSupplierZoneDTLs.add(cmDtl);
					}
				}
			} else if(selectedNode.getType().equals("division") && selectedNode.getChildCount() == 0){
				CmSupplierZoneStateDistrictDTO stateNodeDto2 = (CmSupplierZoneStateDistrictDTO) selectedNode.getParent().getData();
				CmSupplierZoneDTL cmDtl5 = new CmSupplierZoneDTL();
				cmDtl5.setCmSupplierZone(selZone);
				PmState newState2 = new PmState();
				if(stateNodeDto2.getState() != null){
					selectedStates.add(stateNodeDto2.getState().getStateId());
					if(!existingStates.contains(stateNodeDto2.getState().getStateId())){
						IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject("pm",IStateSO.class);
						StateReqDTO reqStateDTO = new StateReqDTO();
						reqStateDTO.setStateId(stateNodeDto2.getState().getStateId());
						StateResDTO resStateDTO = stateSO.retriveStateByID(reqStateDTO);
						newState2 = resStateDTO.getPmState();
					}
				}
				CmSupplierZoneStateDistrictDTO divisionNodeDto2 = (CmSupplierZoneStateDistrictDTO) selectedNode.getData();
				if(divisionNodeDto2.getDivision() != null){
					selectedDivisions.add(divisionNodeDto2.getDivision().getDivisionId());
					if(!existingDivisions.contains(divisionNodeDto2.getDivision().getDivisionId())){
						CmSupplierZoneDTL cmDtl6 = new CmSupplierZoneDTL();
						cmDtl6.setCmSupplierZone(selZone);
						cmDtl6.setPmState(newState2);
						IDivisionSO divisionSO = (IDivisionSO)BusinessDelegate.getInstance().getServiceObject("pm",IDivisionSO.class);
						DivisionReqDTO reqDivisionDTO = new DivisionReqDTO();
						reqDivisionDTO.setDivisionId(divisionNodeDto2.getDivision().getDivisionId());
						DivisionResDTO resDivisionDTO = divisionSO.retrieveDivisionDtlById(reqDivisionDTO);
						PmDivision newDivision2 = resDivisionDTO.getPmDivision();
						if(selectedNode.getChildren() != null){
							for(TreeNode dCh : selectedNode.getChildren()){
								if(dCh.getType().equals("district")){
									CmSupplierZoneStateDistrictDTO districtNodeDto3 = (CmSupplierZoneStateDistrictDTO) dCh.getData();
									if(districtNodeDto3.getDistrict() != null){
										selectedDistricts.add(districtNodeDto3.getDistrict().getDistrictId());
										if(!existingDistricts.contains(districtNodeDto3.getDistrict().getDistrictId())){
											CmSupplierZoneDTL cmDtl7 = new CmSupplierZoneDTL();
											cmDtl7.setCmSupplierZone(selZone);
											cmDtl7.setPmState(newState2);
											cmDtl7.setPmDivision(newDivision2);
											IDistrictSO districtSO = (IDistrictSO)BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
											DistrictReqDTO reqDistrictDTO = new DistrictReqDTO();
											reqDistrictDTO.setDistrictId(districtNodeDto3.getDistrict().getDistrictId());
											DistrictResDTO resDistrictDTO = districtSO.retriveDistrictDtlByID(reqDistrictDTO);
											PmDistrict newDistrict3 = resDistrictDTO.getPmDistrict();
											cmDtl7.setPmDistrict(newDistrict3);
											newCmSupplierZoneDTLs.add(cmDtl7);
										}
									}
								}
							}
						} else {
							cmDtl6.setPmDivision(newDivision2);
							newCmSupplierZoneDTLs.add(cmDtl6);
						}
					}
				}
			} else if(selectedNode.getType().equals("district")) {
				if(selectedNode.getParent().getType().equals("state")){
					CmSupplierZoneStateDistrictDTO stateNodeDto3 = (CmSupplierZoneStateDistrictDTO) selectedNode.getParent().getData();
					CmSupplierZoneDTL cmDtl8 = new CmSupplierZoneDTL();
					cmDtl8.setCmSupplierZone(selZone);
					PmState newState3 = new PmState();
					if(stateNodeDto3.getState() != null){
						selectedStates.add(stateNodeDto3.getState().getStateId());
						if(!existingStates.contains(stateNodeDto3.getState().getStateId())){
							IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject("pm",IStateSO.class);
							StateReqDTO reqStateDTO = new StateReqDTO();
							reqStateDTO.setStateId(stateNodeDto3.getState().getStateId());
							StateResDTO resStateDTO = stateSO.retriveStateByID(reqStateDTO);
							newState3 = resStateDTO.getPmState();
						}
					}
					CmSupplierZoneStateDistrictDTO districtNodeDto4 = (CmSupplierZoneStateDistrictDTO) selectedNode.getData();
					if(districtNodeDto4.getDistrict() != null){
						selectedDistricts.add(districtNodeDto4.getDistrict().getDistrictId());
						if(!existingDistricts.contains(districtNodeDto4.getDistrict().getDistrictId())){
							IDistrictSO districtSO = (IDistrictSO)BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
							DistrictReqDTO reqDistrictDTO = new DistrictReqDTO();
							reqDistrictDTO.setDistrictId(districtNodeDto4.getDistrict().getDistrictId());
							DistrictResDTO resDistrictDTO = districtSO.retriveDistrictDtlByID(reqDistrictDTO);
							PmDistrict newDistrict4 = resDistrictDTO.getPmDistrict();
							CmSupplierZoneDTL cmDtl9 = new CmSupplierZoneDTL();
							cmDtl9.setCmSupplierZone(selZone);
							cmDtl9.setPmState(newState3);
							cmDtl9.setPmDistrict(newDistrict4);
							newCmSupplierZoneDTLs.add(cmDtl9);
						}
					} else {
						cmDtl8.setPmState(newState3);
						newCmSupplierZoneDTLs.add(cmDtl8);
					}
				} else if(selectedNode.getParent().getType().equals("division")){
					TreeNode parentNode = selectedNode.getParent();
					CmSupplierZoneStateDistrictDTO divisionNodeDto3 = (CmSupplierZoneStateDistrictDTO) parentNode.getData();
					CmSupplierZoneDTL cmDtl10 = new CmSupplierZoneDTL();
					cmDtl10.setCmSupplierZone(selZone);
					PmDivision newDivision3 = new PmDivision();
					if(divisionNodeDto3.getDivision() != null){
						selectedDivisions.add(divisionNodeDto3.getDivision().getDivisionId());
						if(!existingDivisions.contains(divisionNodeDto3.getDivision().getDivisionId())){
							IDivisionSO divisionSO = (IDivisionSO)BusinessDelegate.getInstance().getServiceObject("pm",IDivisionSO.class);
							DivisionReqDTO reqDivisionDTO = new DivisionReqDTO();
							reqDivisionDTO.setDivisionId(divisionNodeDto3.getDivision().getDivisionId());
							DivisionResDTO resDivisionDTO = divisionSO.retrieveDivisionDtlById(reqDivisionDTO);
							newDivision3 = resDivisionDTO.getPmDivision();
						}
					}
					cmDtl10.setPmDivision(newDivision3);
					CmSupplierZoneStateDistrictDTO stateNodeDto4 = (CmSupplierZoneStateDistrictDTO) parentNode.getParent().getData();
					PmState newState4 = new PmState();
					if(stateNodeDto4.getState() != null){
						selectedStates.add(stateNodeDto4.getState().getStateId());
						if(!existingStates.contains(stateNodeDto4.getState().getStateId())){
							IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject("pm",IStateSO.class);
							StateReqDTO reqStateDTO = new StateReqDTO();
							reqStateDTO.setStateId(stateNodeDto4.getState().getStateId());
							StateResDTO resStateDTO = stateSO.retriveStateByID(reqStateDTO);
							newState4 = resStateDTO.getPmState();
						}
					}
					cmDtl10.setPmState(newState4);
					CmSupplierZoneStateDistrictDTO districtNodeDto5 = (CmSupplierZoneStateDistrictDTO) selectedNode.getData();
					if(districtNodeDto5.getDistrict() != null){
						selectedDistricts.add(districtNodeDto5.getDistrict().getDistrictId());
						if(!existingDistricts.contains(districtNodeDto5.getDistrict().getDistrictId())){
							IDistrictSO districtSO = (IDistrictSO)BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
							DistrictReqDTO reqDistrictDTO = new DistrictReqDTO();
							reqDistrictDTO.setDistrictId(districtNodeDto5.getDistrict().getDistrictId());
							DistrictResDTO resDistrictDTO = districtSO.retriveDistrictDtlByID(reqDistrictDTO);
							PmDistrict newDistrict5 = resDistrictDTO.getPmDistrict();
							CmSupplierZoneDTL cmDtl11 = new CmSupplierZoneDTL();
							cmDtl11.setCmSupplierZone(selZone);
							cmDtl11.setPmState(newState4);
							cmDtl11.setPmDivision(newDivision3);
							cmDtl11.setPmDistrict(newDistrict5);
							newCmSupplierZoneDTLs.add(cmDtl11);
						}
					} else {
						newCmSupplierZoneDTLs.add(cmDtl10);
					}
				}
			}
		}

		/*
		 * Existing unselected details removed*
		 */
		List<Long> unSelectedStates = new ArrayList<Long>();
		List<Long> unSelectedDistricts = new ArrayList<Long>();
		List<Long> unSelectedDivisions = new ArrayList<Long>();
		/*
		 * Add new details to the current list of details and then remove unselected details
		 */		
		if(selZone.getCmSupplierZoneDtls() != null){
			System.out.println("Count Before add" + selZone.getCmSupplierZoneDtls().size());
			selZone.getCmSupplierZoneDtls().addAll(newCmSupplierZoneDTLs);
		} else {
			selZone.setCmSupplierZoneDtls(newCmSupplierZoneDTLs);
		}
		for(Long st : existingStates){
			if(!selectedStates.contains(st)){
				unSelectedStates.add(st);
			}
		}
		for(Long dt : existingDistricts){
			if(!selectedDistricts.contains(dt)){
				unSelectedDistricts.add(dt);
			}
		}
		for(Long dv : existingDivisions){
			if(!selectedDivisions.contains(dv)){
				unSelectedDivisions.add(dv);
			}
		}
		System.out.println("Size sel lists:" + selectedStates.size() + ":" + selectedDistricts.size());
		if(unSelectedStates.size() > 0 || unSelectedDistricts.size() > 0 || unSelectedDivisions.size() > 0){
			System.out.println("Size rem lists:" + existingStates.size() + ":" + existingDistricts.size());
				if(selZone.getCmSupplierZoneDtls() != null){
					Iterator<CmSupplierZoneDTL> ite = selZone.getCmSupplierZoneDtls().iterator();
					while(ite.hasNext()){
						CmSupplierZoneDTL cmDtl = ite.next();
						if(unSelectedStates.size() > 0 && unSelectedStates.contains(cmDtl.getPmState().getStateId())){	
							ite.remove();
							System.out.println("Removed");
						} else if(unSelectedDistricts.size() > 0 && (cmDtl.getPmDistrict() != null) && unSelectedDistricts.contains(cmDtl.getPmDistrict().getDistrictId())){
							ite.remove();
							System.out.println("Removed");
						} else if(unSelectedDivisions.size() > 0 && (cmDtl.getPmDivision() != null) && unSelectedDivisions.contains(cmDtl.getPmDivision().getDivisionId())){
							ite.remove();
							System.out.println("Removed");
						}
					}	
				}
			}
		System.out.println("Count After" + selZone.getCmSupplierZoneDtls().size());
		if(!getManagedBean().getCmSupplierZoneList().contains(selZone))
			getManagedBean().getCmSupplierZoneList().add(selZone);
		getManagedBean().setSelectedSupplierZone(null);
		getManagedBean().setZoneTrees(CmSupplierZoneSingleton.getInstance().buildZoneSDTree(this.getManagedBean().getCmSupplierZoneList()));
	}
	
	private void assignValReqdToTrue() {
		getManagedBean().setShowParentMsg(false);
		getManagedBean().setValueReqd(true);
	}

	private void assignValReqdToFalse() {
		getManagedBean().setShowParentMsg(true);
		getManagedBean().setValueReqd(false);
	}

}
