package my.ep.web.util.cm.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.persistence.entity.cm.catalog.CmSupplierZone;
import my.ep.persistence.entity.cm.catalog.CmSupplierZoneDTL;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

public class CmSupplierZoneSingleton {
	private static CmSupplierZoneSingleton instance = null;
	private Map<Long, List<PmState>> mapStates = new HashMap<Long, List<PmState>>();

	private CmSupplierZoneSingleton() {
	}

	public static CmSupplierZoneSingleton getInstance() {
		if (instance == null) {
			instance = new CmSupplierZoneSingleton();
		}
		return instance;
	}

	public List<PmState> getPmStates(Long countryId) {
		if (mapStates.get(countryId) == null) {
			initialStatesForCountry(countryId);
		}
		return mapStates.get(countryId);
	}

	public void updateMapStates(Long countryId, List<PmState> pmStates) {
		mapStates.put(countryId, pmStates);
	}

	public Boolean isInitialization(Long countryId) {
		if (mapStates.get(countryId) != null && mapStates.get(countryId).size() > 0) {
			return true;
		}
		return false;
	}

	public List<PmState> initialStatesForCountry(Long countryId) {
		// initialization for country with countryId
		
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);

		StateReqDTO stateReqDTO = new StateReqDTO();
		stateReqDTO.setCountryId(countryId);
		List<PmState> listStates = stateSO.retriveStateDivCityListByCountryId(stateReqDTO).getStateList();
		for (PmState pmState : listStates) {
			
		
			Map<PmDivision, List<PmDistrict>>	uniqueDivisionDistrict	=	getUniqueDivisionDistricts(pmState.getPmDistricts());
			
			Set<PmDivision>	divisionKeySet	=	uniqueDivisionDistrict.keySet();
			if(divisionKeySet != null && !divisionKeySet.isEmpty()){
				for(PmDivision division: divisionKeySet){
					
					division.setPmDistricts(uniqueDivisionDistrict.get(division));
				}
				pmState.setPmDivisions(new ArrayList<PmDivision>(divisionKeySet));
			}else{
				pmState.setPmDivisions(null);
			}
			//update the relationship for the parent
/*			if(pmState.getPmDistricts() != null && !pmState.getPmDistricts().isEmpty()){
				for(PmDistrict district: pmState.getPmDistricts()){
					district.setPmState(pmState);
					
					if(district.getPmCities() != null && !district.getPmCities().isEmpty()){
						
						for(PmCity city: district.getPmCities()){
							
							city.setPmDistrict(district);
						}
					}
				}
			}
			if(pmState.getPmDivisions() != null && !pmState.getPmDivisions().isEmpty()){
				for(PmDivision division: pmState.getPmDivisions()){
					division.setPmState(pmState);
					if(division.getPmDistricts() != null && !division.getPmDistricts().isEmpty()){
						for(PmDistrict district: division.getPmDistricts()){
							district.setPmState(pmState);
							if(district.getPmCities() != null && !district.getPmCities().isEmpty()){
								for(PmCity city: district.getPmCities()){
									city.setPmDistrict(district);
									
								}
							}
						}
					}
				}
			}*/
		}
		mapStates.put(countryId, listStates);
		return listStates;
	}

	public TreeNode buildCmSupplierZoneTree(Long countryId, List<PmState> listStates, List<PmDistrict> listDistricts, List<PmDivision> listDivisions) {
		List<PmState> allStates = mapStates.get(countryId);
		Set<Long> stateIds = new HashSet<Long>();
		Set<Long> districtIds = new HashSet<Long>();
		Set<Long> divisionIds = new HashSet<Long>();
		
		// get list Id for all state, district, division, city
		if (listStates != null && listStates.size() > 0) {
			
			for (PmState state : listStates) {
				stateIds.add(state.getStateId());
			}
		}
		if (listDistricts != null && listDistricts.size() > 0) {
			
			for (PmDistrict district : listDistricts) {
				districtIds.add(district.getDistrictId());
			}
		}
		if (listDivisions != null && listDivisions.size() > 0) {
			
			for (PmDivision division : listDivisions) {
				divisionIds.add(division.getDivisionId());
			}
		}
		
		if (allStates == null) {
			
			allStates = initialStatesForCountry(countryId);
		}

		// build tree
		TreeNode rootNode = new DefaultTreeNode("root", null);
		
		for (PmState state : allStates) {
			CmSupplierZoneStateDistrictDTO stateSDDTO = new CmSupplierZoneStateDistrictDTO(state);
			TreeNode stateNode = new DefaultTreeNode("state",stateSDDTO, rootNode);
			
				for (PmDistrict district : state.getPmDistricts()) {
					if(district.getPmDivision() == null){
						
						CmSupplierZoneStateDistrictDTO districtSDDTO = new CmSupplierZoneStateDistrictDTO(district);
						TreeNode districtNode = new DefaultTreeNode("district",districtSDDTO, stateNode);
						// build district tree
						if (districtIds.contains(district.getDistrictId())) {
							districtNode.setSelected(Boolean.TRUE);
						}
					}
				}
				if(state.getPmDivisions() != null && !state.getPmDivisions().isEmpty()){
				
					for (PmDivision division : state.getPmDivisions()) {
						CmSupplierZoneStateDistrictDTO divisionLocation = new CmSupplierZoneStateDistrictDTO(division);
						TreeNode divisionNode = new DefaultTreeNode("division",divisionLocation, stateNode);
						// build division tree
						for (PmDistrict district : division.getPmDistricts()) {
							CmSupplierZoneStateDistrictDTO districtLocation = new CmSupplierZoneStateDistrictDTO(district);
							TreeNode districtNode = new DefaultTreeNode("district",districtLocation, divisionNode);
							// build district tree
							if (districtIds.contains(district.getDistrictId())) {
								districtNode.setSelected(Boolean.TRUE);
							}
						}
						if (divisionIds.contains(division.getDivisionId())) {
							divisionNode.setSelected(Boolean.TRUE);
						}
					}
				}
				if (stateIds.contains(state.getStateId())) {
					stateNode.setSelected(Boolean.TRUE);
				}
			}
			
		return rootNode;
	}
	
	private Map<PmDivision, List<PmDistrict>> getUniqueDivisionDistricts(List<PmDistrict> pmDistricts) {
		
		Map<PmDivision, List<PmDistrict>> results = new HashMap<PmDivision, List<PmDistrict>>();
		for (PmDistrict district : pmDistricts) {
			
			if (district.getPmDivision() != null) {
				if (results.get(district.getPmDivision()) != null) {
					results.get(district.getPmDivision()).add(district);
				
				} else {
					List<PmDistrict> districts = new ArrayList<PmDistrict>();
					districts.add(district);
					
					results.put(district.getPmDivision(), districts);
				}
			}
		}
		return results;
	}
	
	
	
	public TreeNode buildTree(Long countryId, List<PmState> listStates, List<PmDistrict> listDistricts, List<PmDivision> listDivisions){
		
		List<PmState> allStates = mapStates.get(countryId);
		Set<Long> stateIds = new HashSet<Long>();
		Set<Long> districtIds = new HashSet<Long>();
		Set<Long> divisionIds = new HashSet<Long>();
		Set<Long> cityIds = new HashSet<Long>();
		
		// get list Id for all state, district, division, city
		if (listStates != null && listStates.size() > 0) {
			
			for (PmState state : listStates) {
				stateIds.add(state.getStateId());
			}
		}
		if (listDistricts != null && listDistricts.size() > 0) {
			
			for (PmDistrict district : listDistricts) {
				districtIds.add(district.getDistrictId());
			}
		}
		if (listDivisions != null && listDivisions.size() > 0) {
			
			for (PmDivision division : listDivisions) {
				divisionIds.add(division.getDivisionId());
			}
		}
		if (allStates == null) {
			
			allStates = initialStatesForCountry(countryId);
		}

		// build tree
		TreeNode rootLocationTree = new DefaultTreeNode("root", null);
		
		for (PmState state : allStates) {
			CmSupplierZoneStateDistrictDTO stateLocation = new CmSupplierZoneStateDistrictDTO(state);
			TreeNode stateNode = new DefaultTreeNode("state",stateLocation, rootLocationTree);
			
			for (PmDistrict district : state.getPmDistricts()) {
				if(district.getPmDivision() == null){
					
					CmSupplierZoneStateDistrictDTO districtLocation = new CmSupplierZoneStateDistrictDTO(district);
					TreeNode districtNode = new DefaultTreeNode("district",districtLocation, stateNode);
					// build district tree
					if (districtIds.contains(district.getDistrictId())) {
						districtNode.setSelected(Boolean.TRUE);
					}
				}
			}
			if(state.getPmDivisions() != null && !state.getPmDivisions().isEmpty()){
			
				for (PmDivision division : state.getPmDivisions()) {
					CmSupplierZoneStateDistrictDTO divisionLocation = new CmSupplierZoneStateDistrictDTO(division);
					TreeNode divisionNode = new DefaultTreeNode("division",divisionLocation, stateNode);
					// build division tree
					for (PmDistrict district : division.getPmDistricts()) {
						CmSupplierZoneStateDistrictDTO districtLocation = new CmSupplierZoneStateDistrictDTO(district);
						TreeNode districtNode = new DefaultTreeNode("district",districtLocation, divisionNode);
						// build district tree
						if (districtIds.contains(district.getDistrictId())) {
							districtNode.setSelected(Boolean.TRUE);
						}
					}
					if (divisionIds.contains(division.getDivisionId()) && checkAllChildsSelected(divisionNode)) {
						divisionNode.setSelected(Boolean.TRUE);
					}
				}
			}
			if (stateIds.contains(state.getStateId()) && checkAllChildsSelected(stateNode)) {
				stateNode.setSelected(Boolean.TRUE);
			}
		}
		
		return rootLocationTree;
	}
	
	public boolean checkAllChildsSelected(TreeNode node){
		boolean chkSel = true;
		if(node.getChildren() != null && node.getChildCount() > 0){
			for(TreeNode chNode : node.getChildren()){
				if(!chNode.isSelected()){
					chkSel = false;
					break;
				}	
			}
		}		
		return chkSel;
	}
	
	
	
	public List<TreeNode> buildZoneSDTree(List<CmSupplierZone> zoneList) {
		
		
		Set<Long> stateIds = new HashSet<Long>();
		Set<Long> districtIds = new HashSet<Long>();
		Set<Long> divisionIds = new HashSet<Long>();
		
		List<PmState> listStates = new ArrayList<PmState>();
		List<PmDistrict> listDistricts = new ArrayList<PmDistrict>();
		List<PmDivision> listDivisions = new ArrayList<PmDivision>();
		
		List<TreeNode> roots = new ArrayList<TreeNode>();

		for(CmSupplierZone zone : zoneList){
		
			TreeNode rootNode = new DefaultTreeNode("root", null);
			
			roots.add(rootNode);
			
			CmSupplierZoneStateDistrictDTO cmSupplierZoneStateDistrictDTO = new CmSupplierZoneStateDistrictDTO(zone);
			TreeNode zoneNode = new DefaultTreeNode("cmSupplierZone",cmSupplierZoneStateDistrictDTO, rootNode);
			
//			listStates = initialStatesForCountry(1L);
			listStates = new ArrayList<PmState>();
			stateIds = new HashSet<Long>();
			districtIds = new HashSet<Long>();
			divisionIds = new HashSet<Long>();
			listDistricts = new ArrayList<PmDistrict>();
			listDivisions = new ArrayList<PmDivision>();
			
			List<CmSupplierZoneDTL> zoneDTLs = zone.getCmSupplierZoneDtls();
			
			for(CmSupplierZoneDTL cmSupplierZoneDTL : zoneDTLs){
				
				if(cmSupplierZoneDTL.getPmState() != null){
					stateIds.add(cmSupplierZoneDTL.getPmState().getStateId());
					
					List<Long> stIdList = new ArrayList<Long>();
					for(PmState st : listStates){
						stIdList.add(st.getStateId());
					}
					if(!stIdList.contains(cmSupplierZoneDTL.getPmState().getStateId())){
						listStates.add(cmSupplierZoneDTL.getPmState());
					}
				}
				
				if(cmSupplierZoneDTL.getPmDivision() != null){
					divisionIds.add(cmSupplierZoneDTL.getPmDivision().getDivisionId());
					
					List<Long> divIdList = new ArrayList<Long>();
					for(PmDivision div : listDivisions){
						divIdList.add(div.getDivisionId());
					}
					if(!divIdList.contains(cmSupplierZoneDTL.getPmDivision().getDivisionId())){
						listDivisions.add(cmSupplierZoneDTL.getPmDivision());
					}
				}
				
				if(cmSupplierZoneDTL.getPmDistrict() != null){
					districtIds.add(cmSupplierZoneDTL.getPmDistrict().getDistrictId());
					
					List<Long> distIdList = new ArrayList<Long>();
					for(PmDistrict dis : listDistricts){
						distIdList.add(dis.getDistrictId());
					}
					if(!distIdList.contains(cmSupplierZoneDTL.getPmDistrict().getDistrictId())){
						listDistricts.add(cmSupplierZoneDTL.getPmDistrict());
					}
				}
			}
			
			for (PmState state : listStates) {
				CmSupplierZoneStateDistrictDTO stateSDDTO = new CmSupplierZoneStateDistrictDTO(state);
				TreeNode stateNode = new DefaultTreeNode("state",stateSDDTO, zoneNode);
				if (stateIds.contains(state.getStateId())) {
					stateNode.setSelected(Boolean.TRUE);
				}
								
				if(listDistricts != null && listDistricts.size() > 0){
					for (PmDistrict district : listDistricts) {
						
						if(district.getPmDivision() == null){
							PmState dSt = district.getPmState();
							
							if(dSt != null && dSt.getStateId() == state.getStateId()){
								CmSupplierZoneStateDistrictDTO districtSDDTO = new CmSupplierZoneStateDistrictDTO(district);
								TreeNode districtNode = new DefaultTreeNode("district",districtSDDTO, stateNode);
								// build district tree
								if (districtIds.contains(district.getDistrictId())) {
									districtNode.setSelected(Boolean.TRUE);
								}
							}
						}
					}
				}	
				
				
				if(listDivisions != null && listDivisions.size() > 0){
					for (PmDivision division : listDivisions) {
							
						PmState div = division.getPmState();
						
						if(div.getStateId() == state.getStateId()){
							
							CmSupplierZoneStateDistrictDTO divisionLocation = new CmSupplierZoneStateDistrictDTO(division);
							TreeNode divisionNode = new DefaultTreeNode("division",divisionLocation, stateNode);
							// build division tree
							
							for (PmDistrict district : listDistricts) {
								
								PmDivision d = district.getPmDivision();
								
								if(d!=null && d.getDivisionId() == division.getDivisionId()){
								
									CmSupplierZoneStateDistrictDTO districtLocation = new CmSupplierZoneStateDistrictDTO(district);
									TreeNode districtNode = new DefaultTreeNode("district",districtLocation, divisionNode);
									// build district tree
									if (districtIds.contains(district.getDistrictId())) {
										districtNode.setSelected(Boolean.TRUE);
									}
								}
							}
							if (divisionIds.contains(division.getDivisionId())) {
								divisionNode.setSelected(Boolean.TRUE);
							}
						}
					}
				}
			}	
		}
		
		return roots;
	}
	
	
	public TreeNode buildZoneTree(CmSupplierZone zone, List<PmState> listStates, List<PmDistrict> listDistricts, List<PmDivision> listDivisions) {
		TreeNode rootTree = new DefaultTreeNode("root", null);
		
		Map<PmState, List<PmDistrict>> mapStatesDistricts = getUniqueStateDistricts(listDistricts);
		
		TreeNode zoneNode=null;
		if(zone !=null){
		CmSupplierZoneStateDistrictDTO zoneDetail = new CmSupplierZoneStateDistrictDTO(zone);
		
		// Build zone - state
		 zoneNode = new DefaultTreeNode("cmSupplierZone",zoneDetail, rootTree);
		}

		//build zone - state - district - city
		Set<PmState>	statesDistrictsKeySet	=	mapStatesDistricts.keySet();
		if(statesDistrictsKeySet != null && !statesDistrictsKeySet.isEmpty()){
			for(PmState state: statesDistrictsKeySet){
				CmSupplierZoneStateDistrictDTO stateLocation = new CmSupplierZoneStateDistrictDTO(state);
				TreeNode stateNode=null;
				if(zoneNode !=null){
					stateNode = new DefaultTreeNode("state",stateLocation, zoneNode);
					}else{
						 stateNode = new DefaultTreeNode("state",stateLocation,
								rootTree);
					}

				//TreeNode stateNode = new DefaultTreeNode("state",stateLocation, zoneNode);
				List<PmDistrict>	districts	=	mapStatesDistricts.get(state);
				if(districts != null && !districts.isEmpty()){
					for(PmDistrict district: districts){
						CmSupplierZoneStateDistrictDTO districtLocation = new CmSupplierZoneStateDistrictDTO(district);
						TreeNode districtNode = new DefaultTreeNode("district",districtLocation, stateNode);
					}
				}
			}
		}
		return rootTree;
	}
	
	private	Map<PmState,List<PmDistrict>>	getUniqueStateDistricts(List<PmDistrict> districts){
		Map<PmState,List<PmDistrict>>	results	=	new HashMap<PmState,List<PmDistrict>>();
		if(districts != null && !districts.isEmpty()){
			for(PmDistrict district: districts){
				PmState	state	=	district.getPmState();
				if(results.get(state) != null && !results.get(state).isEmpty()){
					results.get(state).add(district);
				}else{
					List<PmDistrict>	listDistricts	=	new ArrayList<PmDistrict>();
					listDistricts.add(district);
					results.put(state, listDistricts);
				}
			}
		}
		return results;
	}
	
	
}


