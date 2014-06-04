package my.ep.web.backingbean.cm.catalogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.model.TreeNode;

import my.ep.biz.service.cm.catalogue.IZoneMaintenanceSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.dto.cm.catalogue.ZoneMaintenanceReqDTO;
import my.ep.dto.cm.catalogue.ZoneMaintenanceResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.persistence.entity.cm.catalog.CmSupplierZone;
import my.ep.persistence.entity.cm.catalog.CmSupplierZoneDTL;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.catalogue.ZoneMaintenanceManagedBean;
import my.ep.web.util.cm.helper.CmSupplierZoneSingleton;
import my.ep.web.util.cm.helper.CmSupplierZoneStateDistrictDTO;
import my.ep.web.util.cm.helper.SourcingZoneDetailHelper;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name = "zoneMaintenanceBackingBean")
@ViewScoped
public class ZoneMaintenanceBackingBean extends
BaseBackingBean<ZoneMaintenanceManagedBean> {

	public ZoneMaintenanceBackingBean() {

	}

	@PostConstruct
	public void init() {
		
		showZonePopup();
		
		getManagedBean().setSelectedSupplierZone(null);
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
				if(cmZone.getSuppZoneId() == zone.getSuppZoneId()){
					System.out.println("Found it");
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
		IZoneMaintenanceSO zoneMaintenanceSO = null;
		try {
			zoneMaintenanceSO = (IZoneMaintenanceSO) BusinessDelegate
					.getInstance().getServiceObject("cm", IZoneMaintenanceSO.class);
			ZoneMaintenanceReqDTO reqDTO = new ZoneMaintenanceReqDTO();
			
			reqDTO.setCmSupplierZoneList(getManagedBean().getDeletedCmSupplierZoneList());
			
			ZoneMaintenanceResDTO resDTO = zoneMaintenanceSO.delete(reqDTO);
			
			if (resDTO.isSuccessful()) {
			
				System.out.println("SuccessMMMMMMMMMMMMMMMMM");
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Records deleted successfully.", ""));
			}
         
		} catch (Exception ex) {
			
			System.out.println("Not SuccessMMMMMMMMMMMMMMMMM");
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sorry We are unable to process.", ""));
			ex.printStackTrace();
		} finally {
			zoneMaintenanceSO = null;
		}
		
		getManagedBean().setZoneTrees(CmSupplierZoneSingleton.getInstance().buildZoneSDTree(this.getManagedBean().getCmSupplierZoneList()));
	}
	
	/**
	 * saveCmSupplierZone method
	 */
	public String saveCmSupplierZones() {
		
		/* First delete any records that are to be deleted */
		
		if(getManagedBean().getDeletedCmSupplierZoneList() != null)
			deleteCmSupplierZones();
		
		IZoneMaintenanceSO zoneMaintenanceSO = null;
		try {
			zoneMaintenanceSO = (IZoneMaintenanceSO) BusinessDelegate
					.getInstance().getServiceObject("cm", IZoneMaintenanceSO.class);
			ZoneMaintenanceReqDTO reqDTO = new ZoneMaintenanceReqDTO();
			
			reqDTO.setCmSupplierZoneList(getManagedBean().getCmSupplierZoneList());
			
			System.out.println("Current Size:" + getManagedBean().getCmSupplierZoneList().size());
			
			ZoneMaintenanceResDTO resDTO = zoneMaintenanceSO.save(reqDTO);
			
			if (resDTO.isSuccessful()) {
			
				System.out.println("SuccessMMMMMMMMMMMMMMMMM");
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", ""));
				return "/xhtml/shared/acknowledge.xhtml";
			}
         
		} catch (Exception ex) {
			
			System.out.println("Not SuccessMMMMMMMMMMMMMMMMM");
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sorry We are unable to process.", ""));
			ex.printStackTrace();
		} finally {
			zoneMaintenanceSO = null;
		}
		
		showZonePopup();
		
		return null;
	}
	
	public void editZone(TreeNode rootZoneLocation, int zoneIndex) {
		List<CmSupplierZoneStateDistrictDTO> detailDTOs = getDetails(rootZoneLocation);

		CmSupplierZone zone = getZone(rootZoneLocation);
		List<PmState> states = SourcingZoneDetailHelper
				.getStateFromDetailDTOs(detailDTOs);
		List<PmDivision> divisions = SourcingZoneDetailHelper
				.getDivisionFromDetailDTOs(detailDTOs);
		List<PmDistrict> districts = SourcingZoneDetailHelper
				.getDistrictFromDetailDTOs(detailDTOs);
		
		getManagedBean().setRootZoneTree(
				CmSupplierZoneSingleton.getInstance().buildTree(1L,	states, districts, divisions));
		int i = zoneIndex;

		this.getManagedBean().setZoneIndex(i);
		/*
		 * getManagedBean().getZoneTrees().remove(
		 * LocationSingleton.getInstance().buildZoneTree(zone, states,
		 * districts, divisions, cities));
		 */
		getManagedBean().setZoneTrees(CmSupplierZoneSingleton.getInstance().buildZoneSDTree(this.getManagedBean().getCmSupplierZoneList()));
		
		
		for(CmSupplierZone suppZone : getManagedBean().getCmSupplierZoneList()){
			if(zone.getSuppZoneId() == suppZone.getSuppZoneId()){
				getManagedBean().setSelectedSupplierZone(suppZone);
				getManagedBean().setZoneName(suppZone.getSuppZoneName());
			}
		}
		
		/*
		 * if (zone != null) { getManagedBean().setZoneName(zone.getZoneName());
		 * }
		 */
	}
	
	/**
    * showZonePopup method for zone popup
    */
	public void showZonePopup() {
//		getManagedBean().setRootZoneTree(
//				CmSupplierZoneSingleton.getInstance().buildCmSupplierZoneTree(1L, null,
//						null, null));

		
		IZoneMaintenanceSO zoneMaintenanceSO = (IZoneMaintenanceSO) BusinessDelegate
				.getInstance().getServiceObject("cm", IZoneMaintenanceSO.class);
		ZoneMaintenanceReqDTO reqDTO = new ZoneMaintenanceReqDTO();

		ZoneMaintenanceResDTO resDTO = zoneMaintenanceSO.getSupplierZones(reqDTO);
		this.getManagedBean().setCmSupplierZoneList(resDTO.getCmSupplierZones());
		
		this.getManagedBean().setDeletedCmSupplierZoneList(null);
		
		getManagedBean().setZoneTrees(CmSupplierZoneSingleton.getInstance().buildZoneSDTree(this.getManagedBean().getCmSupplierZoneList()));
		
		getManagedBean().setRootZoneTree(
				CmSupplierZoneSingleton.getInstance().buildCmSupplierZoneTree(1L, null, null, null));

		getManagedBean().setZoneName(null);
		
	}
		
	
	private List<CmSupplierZoneStateDistrictDTO> getDetails(TreeNode zoneDetail) {
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
	
	public void updateZoneListOnPopUpClose(){

		CmSupplierZone selZone = getManagedBean().getSelectedSupplierZone();
		
		if(selZone.getSuppZoneId() == 0 && !getManagedBean().getCmSupplierZoneList().contains(selZone)){
			selZone = new CmSupplierZone();
			
			selZone.setSuppZoneName(getManagedBean().getSelectedSupplierZone().getSuppZoneName());
			
			if(selZone.getSmSupplier() == null){
				IRegistrationSO smSO = (IRegistrationSO) BusinessDelegate.getInstance()
						.getServiceObject("sm", IRegistrationSO.class);

				RegistrationReqDTO smReqDTO = new RegistrationReqDTO();
				smReqDTO.setSupplierId(new Long(1));

				RegistrationResDTO smResDTO = smSO.retrieveSuppOnlineAcc(smReqDTO);
				selZone.setSmSupplier(smResDTO.getSmSupp());
			}
			
			if(selZone.getSuppZoneDesc() == null){
				selZone.setSuppZoneDesc(selZone.getSuppZoneName());
			}
			
		}
		
		TreeNode[] selectedNodes = getManagedBean().getSelectedNodes();
		
		List<CmSupplierZoneDTL> newCmSupplierZoneDTLs = new ArrayList<CmSupplierZoneDTL>();
		
		HashMap<Long, PmState> exStateMap = new HashMap<Long, PmState>(); 
		HashMap<Long, PmDistrict> exDistrictMap = new HashMap<Long, PmDistrict>();
		HashMap<Long, PmDivision> exDivisionMap = new HashMap<Long, PmDivision>();
		
		List<PmState> existingStates = new ArrayList<PmState>();
		List<PmDistrict> existingDistricts = new ArrayList<PmDistrict>();
		List<PmDivision> existingDivisions = new ArrayList<PmDivision>();
		
		List<CmSupplierZoneDTL> cmDtls = new ArrayList<CmSupplierZoneDTL>(); 
		if(selZone.getCmSupplierZoneDtls() != null)
			cmDtls.addAll(selZone.getCmSupplierZoneDtls()); 
		
		for(CmSupplierZoneDTL cmDtl : cmDtls){
			if(cmDtl.getPmState() != null)
				existingStates.add(cmDtl.getPmState());
			if(cmDtl.getPmDistrict() != null)
				existingDistricts.add(cmDtl.getPmDistrict());
			if(cmDtl.getPmDivision() != null)
				existingDivisions.add(cmDtl.getPmDivision());
			
//			exStateMap.put(cmDtl.getCmSupplierZone().getSuppZoneId(), cmDtl.getPmState());
//			exDistrictMap.put(cmDtl.getCmSupplierZone().getSuppZoneId(), cmDtl.getPmDistrict());
//			exDivisionMap.put(cmDtl.getCmSupplierZone().getSuppZoneId(), cmDtl.getPmDivision());
		}
		
		
		/*
		 * 
		 * New zone details added***********************************************************
		 * 
		 */
		
		List<PmState> selectedStates = new ArrayList<PmState>();
		List<PmDistrict> selectedDistricts = new ArrayList<PmDistrict>();
		List<PmDivision> selectedDivisions = new ArrayList<PmDivision>();
		
		for(TreeNode selectedNode : selectedNodes) {
			if(selectedNode.getType().equals("state")) {
				CmSupplierZoneStateDistrictDTO stateNodeDto = (CmSupplierZoneStateDistrictDTO) selectedNode.getData();
				if(stateNodeDto.getState() != null){

					selectedStates.add(stateNodeDto.getState());
					if(!existingStates.contains(stateNodeDto.getState())){
						CmSupplierZoneDTL cmDtl = new CmSupplierZoneDTL();
						cmDtl.setCmSupplierZone(selZone);

						IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject("pm",IStateSO.class);
						StateReqDTO reqStateDTO = new StateReqDTO();
						reqStateDTO.setStateId(stateNodeDto.getState().getStateId());
						StateResDTO resStateDTO = stateSO.retriveStateByID(reqStateDTO);
						PmState newState = resStateDTO.getPmState();
						
						if(selectedNode.getChildren() != null){
						
							for(TreeNode d : selectedNode.getChildren()){
							
								if(d.getType().equals("district")){
									CmSupplierZoneStateDistrictDTO districtNodeDto = (CmSupplierZoneStateDistrictDTO) d.getData();
								
									if(districtNodeDto.getDistrict() != null){
										selectedDistricts.add(districtNodeDto.getDistrict());
										if(!existingDistricts.contains(districtNodeDto.getDistrict())){
											CmSupplierZoneDTL cmDtl2 = new CmSupplierZoneDTL();
											cmDtl2.setCmSupplierZone(selZone);
											cmDtl2.setPmState(newState);
											
											IDistrictSO districtSO = (IDistrictSO)BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
											DistrictReqDTO reqDistrictDTO = new DistrictReqDTO();
											reqDistrictDTO.setDistrictId(districtNodeDto.getDistrict().getDistrictId());
											DistrictResDTO resDistrictDTO = districtSO.retriveDistrictByID(reqDistrictDTO);
											PmDistrict newDistrict = resDistrictDTO.getPmDistrict();
											
											cmDtl2.setPmDistrict(newDistrict);
											newCmSupplierZoneDTLs.add(cmDtl2);
										}
									}
								} else if(d.getType().equals("division")){
									CmSupplierZoneStateDistrictDTO divisionNodeDto = (CmSupplierZoneStateDistrictDTO) d.getData();
									
									if(divisionNodeDto.getDivision() != null){
										selectedDivisions.add(divisionNodeDto.getDivision());
										if(!existingDivisions.contains(divisionNodeDto.getDivision())){
											CmSupplierZoneDTL cmDtl3 = new CmSupplierZoneDTL();
											cmDtl3.setCmSupplierZone(selZone);
											cmDtl3.setPmState(newState);
											
											IDivisionSO divisionSO = (IDivisionSO)BusinessDelegate.getInstance().getServiceObject("pm",IDivisionSO.class);
											DivisionReqDTO reqDivisionDTO = new DivisionReqDTO();
											reqDivisionDTO.setDivisionId(divisionNodeDto.getDivision().getDivisionId());
											DivisionResDTO resDivisionDTO = divisionSO.retrieveDivisionById(reqDivisionDTO);
											PmDivision newDivision = resDivisionDTO.getPmDivision();
											
											if(d.getChildren() != null){
												
												for(TreeNode ch : d.getChildren()){
												
													if(ch.getType().equals("district")){
														CmSupplierZoneStateDistrictDTO dNodeDto = (CmSupplierZoneStateDistrictDTO) ch.getData();
													
														if(dNodeDto.getDistrict() != null){
															selectedDistricts.add(dNodeDto.getDistrict());
															if(!existingDistricts.contains(dNodeDto.getDistrict())){
																CmSupplierZoneDTL cmDtl4 = new CmSupplierZoneDTL();
																cmDtl4.setCmSupplierZone(selZone);
																cmDtl4.setPmState(newState);
																cmDtl4.setPmDivision(newDivision);
											
																IDistrictSO districtSO = (IDistrictSO)BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
																DistrictReqDTO reqDistrictDTO = new DistrictReqDTO();
																reqDistrictDTO.setDistrictId(dNodeDto.getDistrict().getDistrictId());
																DistrictResDTO resDistrictDTO = districtSO.retriveDistrictByID(reqDistrictDTO);
																PmDistrict newDistrict2 = resDistrictDTO.getPmDistrict();
																
																cmDtl4.setPmDistrict(newDistrict2);
																newCmSupplierZoneDTLs.add(cmDtl4);
															}
														}
													}
												}
											} else {
												cmDtl3.setPmDivision(newDivision);
												newCmSupplierZoneDTLs.add(cmDtl3);
											}
										}
									}
								}
							}
						} else {
							
							cmDtl.setPmState(newState);
							newCmSupplierZoneDTLs.add(cmDtl);
						}
					}
				}
				
			} else if(selectedNode.getType().equals("division")){
				CmSupplierZoneStateDistrictDTO stateNodeDto2 = (CmSupplierZoneStateDistrictDTO) selectedNode.getParent().getData();

				CmSupplierZoneDTL cmDtl5 = new CmSupplierZoneDTL();
				cmDtl5.setCmSupplierZone(selZone);
				
				PmState newState2 = new PmState();
				
				if(stateNodeDto2.getState() != null){
					
					selectedStates.add(stateNodeDto2.getState());
					if(!existingStates.contains(stateNodeDto2.getState())){
						
						IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject("pm",IStateSO.class);
						StateReqDTO reqStateDTO = new StateReqDTO();
						reqStateDTO.setStateId(stateNodeDto2.getState().getStateId());
						StateResDTO resStateDTO = stateSO.retriveStateByID(reqStateDTO);
						newState2 = resStateDTO.getPmState();
					}
				}
				
				CmSupplierZoneStateDistrictDTO divisionNodeDto2 = (CmSupplierZoneStateDistrictDTO) selectedNode.getData();
				
				if(divisionNodeDto2.getDivision() != null){
					selectedDivisions.add(divisionNodeDto2.getDivision());
					if(!existingDivisions.contains(divisionNodeDto2.getDivision())){
						CmSupplierZoneDTL cmDtl6 = new CmSupplierZoneDTL();
						cmDtl6.setCmSupplierZone(selZone);
						cmDtl6.setPmState(newState2);
						
						IDivisionSO divisionSO = (IDivisionSO)BusinessDelegate.getInstance().getServiceObject("pm",IDivisionSO.class);
						DivisionReqDTO reqDivisionDTO = new DivisionReqDTO();
						reqDivisionDTO.setDivisionId(divisionNodeDto2.getDivision().getDivisionId());
						DivisionResDTO resDivisionDTO = divisionSO.retrieveDivisionById(reqDivisionDTO);
						PmDivision newDivision2 = resDivisionDTO.getPmDivision();
						
						if(selectedNode.getChildren() != null){
							
							for(TreeNode dCh : selectedNode.getChildren()){
							
								if(dCh.getType().equals("district")){
									CmSupplierZoneStateDistrictDTO districtNodeDto3 = (CmSupplierZoneStateDistrictDTO) dCh.getData();
								
									if(districtNodeDto3.getDistrict() != null){
										selectedDistricts.add(districtNodeDto3.getDistrict());
										if(!existingDistricts.contains(districtNodeDto3.getDistrict())){
											CmSupplierZoneDTL cmDtl7 = new CmSupplierZoneDTL();
											cmDtl7.setCmSupplierZone(selZone);
											cmDtl7.setPmState(newState2);
											cmDtl7.setPmDivision(newDivision2);
						
											IDistrictSO districtSO = (IDistrictSO)BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
											DistrictReqDTO reqDistrictDTO = new DistrictReqDTO();
											reqDistrictDTO.setDistrictId(districtNodeDto3.getDistrict().getDistrictId());
											DistrictResDTO resDistrictDTO = districtSO.retriveDistrictByID(reqDistrictDTO);
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
						
						selectedStates.add(stateNodeDto3.getState());
						if(!existingStates.contains(stateNodeDto3.getState())){
							
							IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject("pm",IStateSO.class);
							StateReqDTO reqStateDTO = new StateReqDTO();
							reqStateDTO.setStateId(stateNodeDto3.getState().getStateId());
							StateResDTO resStateDTO = stateSO.retriveStateByID(reqStateDTO);
							newState3 = resStateDTO.getPmState();
						}
					}
					
					CmSupplierZoneStateDistrictDTO districtNodeDto4 = (CmSupplierZoneStateDistrictDTO) selectedNode.getData();
					if(districtNodeDto4.getDistrict() != null){
						
						selectedDistricts.add(districtNodeDto4.getDistrict());
						if(!existingDistricts.contains(districtNodeDto4.getDistrict())){
							
							IDistrictSO districtSO = (IDistrictSO)BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
							DistrictReqDTO reqDistrictDTO = new DistrictReqDTO();
							reqDistrictDTO.setDistrictId(districtNodeDto4.getDistrict().getDistrictId());
							DistrictResDTO resDistrictDTO = districtSO.retriveDistrictByID(reqDistrictDTO);
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
						
						selectedDivisions.add(divisionNodeDto3.getDivision());
						if(!existingStates.contains(divisionNodeDto3.getDivision())){
							
							IDivisionSO divisionSO = (IDivisionSO)BusinessDelegate.getInstance().getServiceObject("pm",IDivisionSO.class);
							DivisionReqDTO reqDivisionDTO = new DivisionReqDTO();
							reqDivisionDTO.setDivisionId(divisionNodeDto3.getDivision().getDivisionId());
							DivisionResDTO resDivisionDTO = divisionSO.retrieveDivisionById(reqDivisionDTO);
							newDivision3 = resDivisionDTO.getPmDivision();
						}
					}
					
					cmDtl10.setPmDivision(newDivision3);
					
					CmSupplierZoneStateDistrictDTO stateNodeDto4 = (CmSupplierZoneStateDistrictDTO) parentNode.getParent().getData();
					
					PmState newState4 = new PmState();
					
					if(stateNodeDto4.getState() != null){
						
						selectedStates.add(stateNodeDto4.getState());
						if(!existingStates.contains(stateNodeDto4.getState())){
							
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
						
						selectedDistricts.add(districtNodeDto5.getDistrict());
						if(!existingDistricts.contains(districtNodeDto5.getDistrict())){
							
							IDistrictSO districtSO = (IDistrictSO)BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
							DistrictReqDTO reqDistrictDTO = new DistrictReqDTO();
							reqDistrictDTO.setDistrictId(districtNodeDto5.getDistrict().getDistrictId());
							DistrictResDTO resDistrictDTO = districtSO.retriveDistrictByID(reqDistrictDTO);
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
		 * 
		 * 
		 * Existing unselected details removed***********************************************************
		 * 
		 * 
		 */
		
		List<PmState> unSelectedStates = new ArrayList<PmState>();
		List<PmDistrict> unSelectedDistricts = new ArrayList<PmDistrict>();
		List<PmDivision> unSelectedDivisions = new ArrayList<PmDivision>();
		
		/*
		 * 
		 * Add new details to the current list of details and then remove unselected details
		 * 
		 */		
		
		if(selZone.getCmSupplierZoneDtls() != null){
			System.out.println("Count Before add" + selZone.getCmSupplierZoneDtls().size());
			selZone.getCmSupplierZoneDtls().addAll(newCmSupplierZoneDTLs);
		} else {
			selZone.setCmSupplierZoneDtls(newCmSupplierZoneDTLs);
		}
		
		for(PmState st : existingStates){
			if(!selectedStates.contains(st)){
				unSelectedStates.add(st);
			}
		}
		
		for(PmDistrict dt : existingDistricts){
			if(!selectedDistricts.contains(dt)){
				unSelectedDistricts.add(dt);
			}
		}
		
		for(PmDivision dv : existingDivisions){
			if(!selectedDivisions.contains(dv)){
				unSelectedDivisions.add(dv);
			}
		}
		
		System.out.println("Size sel lists:" + selectedStates.size() + ":" + selectedDistricts.size());
		
		if(unSelectedStates.size() > 0 || unSelectedDistricts.size() > 0 || unSelectedDivisions.size() > 0){
			
//			existingStates.removeAll(selectedStates);
//			existingDivisions.removeAll(selectedDivisions);
//			existingDistricts.removeAll(selectedDistricts);
			
			System.out.println("Size rem lists:" + existingStates.size() + ":" + existingDistricts.size());
			
				
//				unSelectedStates = existingStates;
//				unSelectedDivisions = existingDivisions;
//				unSelectedDistricts = existingDistricts;
				
				if(selZone.getCmSupplierZoneDtls() != null){
					
					Iterator<CmSupplierZoneDTL> ite = selZone.getCmSupplierZoneDtls().iterator();
					
					while(ite.hasNext()){
						CmSupplierZoneDTL cmDtl = ite.next();
						if(unSelectedStates.contains(cmDtl.getPmState()) || unSelectedDistricts.contains(cmDtl.getPmDistrict()) || unSelectedDivisions.contains(cmDtl.getPmDivision())){
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
	

}
