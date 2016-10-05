/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.circular.panel;
/**
 * Singleton For Location Zone
 * 
 * @author Alam
 * @since 2/6/2014
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZoneDtl;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

public class LocationSingleton {
	public	static	final	String	STATE_LEVEL = "S";
	public	static	final	String	DISTRICT_LEVEL	=	"D";
	private static LocationSingleton instance = null;
	private Map<Long, List<PmState>> mapStates = new HashMap<Long, List<PmState>>();

	private LocationSingleton() {
	}

	public static LocationSingleton getInstance() {
		if (instance == null) {
			instance = new LocationSingleton();
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
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStateSO.class);

		StateReqDTO stateReqDTO = new StateReqDTO();
		stateReqDTO.setCountryId(countryId);
		List<PmState> listStates = stateSO.retriveStateDivCityListByCountryId(stateReqDTO).getStateList();
		for (PmState pmState : listStates) {
			Map<PmDivision, List<PmDistrict>> uniqueDivisionDistrict = getUniqueDivisionDistricts(pmState.getPmDistricts());
			Set<PmDivision> divisionKeySet = uniqueDivisionDistrict.keySet();
			if (divisionKeySet != null && !divisionKeySet.isEmpty()) {
				for (PmDivision division : divisionKeySet) {
					division.setPmDistricts(uniqueDivisionDistrict.get(division));
				}
				pmState.setPmDivisions(new ArrayList<PmDivision>(divisionKeySet));
			} else {
				pmState.setPmDivisions(null);
			}
		}
		mapStates.put(countryId, listStates);
		return listStates;
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

	/**
	 * 
	 * @param countryId
	 * @param listStates
	 * @param listDistricts
	 * @param locationLevelCode
	 * @param locClass
	 * @return
	 */
	public <LOCATION_CLASS> TreeNode buildLocationPopupTree(Long countryId, List<PmState> listStates, List<PmDistrict> listDistricts, List<PmDivision> listDivisions, List<PmCity> listCities, String locationLevelCode, Class<CtPanelZoneDtl> locClass) {
		// build tree
		TreeNode rootLocationTree = new DefaultTreeNode("root", null);
			
		try {
			List<PmState> allStates = mapStates.get(countryId);
			Set<Long> stateIds = new HashSet<Long>();
			Set<Long> districtIds = new HashSet<Long>();
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
			if (allStates == null) {
				allStates = initialStatesForCountry(countryId);
			}
			for (PmState state : allStates) {
				CtPanelZoneDtl locCriteria = locClass.newInstance();
				locCriteria.setStateId(state);
				TreeNode stateNode = new DefaultTreeNode("state",locCriteria, rootLocationTree);
				// build state tree
				if(ParameterConst.LOCATION_LEVEL_DISTRICT.equals(locationLevelCode)){
					for (PmDistrict district : state.getPmDistricts()) {
						CtPanelZoneDtl criteria = locClass.newInstance();
						criteria.setDistrictId(district);
						TreeNode districtNode = new DefaultTreeNode("district",criteria, stateNode);
						// build district tree
						if (districtIds.contains(district.getDistrictId())) {
								districtNode.setSelected(Boolean.TRUE);
						}
					}
					if (stateIds.contains(state.getStateId())) {
						stateNode.setSelected(Boolean.TRUE);
					}
				}
			}
		} catch (InstantiationException e) {
			throw new EpException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new EpException(e.getMessage());
		}
		return rootLocationTree;
	}
	
   private Map<PmState, List<PmDistrict>> getUniqueStateDistrictsCities(List<PmDistrict> districts) {
		
		Map<PmState, List<PmDistrict>> results = new HashMap<PmState, List<PmDistrict>> ();
		for(PmDistrict district: districts){
				PmState	state	=	district.getPmState();
				if(results.get(state) != null && !results.get(state).isEmpty()){
					results.get(state).add(district);
				}else{
					List<PmDistrict> map	= new ArrayList<PmDistrict>();
					map.add(district);
					results.put(state, map);
				}
			}
		return results;
	}
   
	   /**
	 * 
	 * 
	 * @param zone
	 * @param listStates
	 * @param listDistricts
	 * @param listDivisions
	 * @param listCities
	 * @param locClass
	 * @return
	 */
	public <LOCATION_CLASS> TreeNode buildLocationTree(CtPanelZone zone, List<PmState> listStates, List<PmDistrict> listDistricts, Class<CtPanelZoneDtl> locClass) {
		TreeNode rootLocationTree = new DefaultTreeNode("root", null);
		try {
			Map<PmState,List<PmDistrict>> mapStatesDistricts = getUniqueStateDistrictsCities(listDistricts);
			TreeNode zoneNode=null;
			if(zone !=null){
				// Build zone - state
				CtPanelZoneDtl zoneLocation = locClass.newInstance();
				((CtPanelZoneDtl) zoneLocation).setCtPanelZone(zone);
				zoneNode = new DefaultTreeNode("zone",zoneLocation, rootLocationTree);
			}
			//Build zone - state
			if(listStates != null && listStates.size() > 0){
				for(PmState state: listStates){
					CtPanelZoneDtl stateLocation = locClass.newInstance();
					stateLocation.setStateId(state);
					if(zoneNode !=null){
						new DefaultTreeNode("state",stateLocation, zoneNode);
					}else{
						 new DefaultTreeNode("state",stateLocation,rootLocationTree);
					}
				}
			}

			//build zone - state - district - city
			Set<PmState>	statesDistrictsCitiesKeySet	=	mapStatesDistricts.keySet();
			if(statesDistrictsCitiesKeySet != null && !statesDistrictsCitiesKeySet.isEmpty()){
				for(PmState state: statesDistrictsCitiesKeySet){
					CtPanelZoneDtl stateLocation = locClass.newInstance();
					stateLocation.setStateId(state);
					TreeNode stateNode=null;
					if(zoneNode !=null){
						stateNode = new DefaultTreeNode("state",stateLocation, zoneNode);
					}else{
						stateNode = new DefaultTreeNode("state",stateLocation,rootLocationTree);
					}

					List<PmDistrict>	mapDistrictsCities	=	mapStatesDistricts.get(state);
					if(mapDistrictsCities != null && !mapDistrictsCities.isEmpty()){
						for(PmDistrict district: mapDistrictsCities){
							CtPanelZoneDtl districtLocation = locClass.newInstance();
							districtLocation.setDistrictId(district);
							new DefaultTreeNode("district",districtLocation, stateNode);
						}
					}
				}
			}
		} catch (InstantiationException e) {
			throw new EpException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new EpException(e.getMessage());
		}
		return rootLocationTree;
	}

	/**
	 * 
	 * @param countryId
	 * @param listStates
	 * @param listDistricts
	 * @param listDivisions
	 * @param listCities
	 * @param locationLevelCode
	 * @param locClass
	 * @return
	 */
	public <LOCATION_CLASS> TreeNode buildLocationPopupTree(Long countryId, List<PmState> listStates,
		 List<PmDistrict> listDistricts, List<PmDivision> listDivisions, String locationLevelCode, Class<CtPanelZoneDtl> locClass, boolean view) {
		// build tree
		TreeNode rootLocationTree = new DefaultTreeNode("root", null);
			try {
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
				for (PmState state : allStates) {
					if(view){
						TreeNode stateNode= null;
						if(ParameterConst.LOCATION_LEVEL_STATE.equals(locationLevelCode)){
							if (stateIds.contains(state.getStateId())) {
								CtPanelZoneDtl locCriteria = locClass.newInstance();
								locCriteria.setStateId(state);
								stateNode = new DefaultTreeNode("state",locCriteria, rootLocationTree);
								stateNode.setSelectable(false);
							}
						}
						if(ParameterConst.LOCATION_LEVEL_DISTRICT.equals(locationLevelCode)){
							if (state.getPmDivisions() != null && !state.getPmDivisions().isEmpty() && "13".equalsIgnoreCase(state.getStateCode())) {
								for (PmDivision division : state.getPmDivisions()) {
									TreeNode divisionNode = null;      
									// build division tree
									for (PmDistrict district : division.getPmDistricts()) {
										if (districtIds.contains(district.getDistrictId())) {
											if(stateNode == null){
												CtPanelZoneDtl locCriteria = locClass.newInstance();
												locCriteria.setStateId(state);
												stateNode = new DefaultTreeNode("state",locCriteria, rootLocationTree);
												stateNode.setSelectable(false);
											}
											if(divisionNode == null){
												CtPanelZoneDtl lodivcriteria  = locClass.newInstance();
												lodivcriteria.setDivisionId(division);
												divisionNode = new DefaultTreeNode("division", lodivcriteria,stateNode);
												divisionNode.setSelectable(false);
											}
											CtPanelZoneDtl lodivcriteria1  = locClass.newInstance();
											lodivcriteria1.setDistrictId(district);
											TreeNode districtNode = new DefaultTreeNode("district",lodivcriteria1, divisionNode);
											districtNode.setSelectable(false);
										}
									}
								}
							} else {

								for (PmDistrict district : state.getPmDistricts()) {
									if(district.getPmDivision() == null){
										// build district tree
										if (districtIds.contains(district.getDistrictId())) {
											if(stateNode == null){
												CtPanelZoneDtl locCriteria = locClass.newInstance();
												locCriteria.setStateId(state);
												stateNode = new DefaultTreeNode("state",locCriteria, rootLocationTree);
												stateNode.setSelectable(false);
											}
											CtPanelZoneDtl criteria = locClass.newInstance();
											criteria.setDistrictId(district);
											TreeNode districtNode = new DefaultTreeNode("district",criteria, stateNode);
											districtNode.setSelectable(false);
										}
									}
								}
							}
							if (stateIds.contains(state.getStateId())) {
								stateNode.setSelectable(false);
							}
						}
					}else {
						//System.out.println("======display state name==="+state.getStateName());
						CtPanelZoneDtl locCriteria = locClass.newInstance();
						locCriteria.setStateId(state);
						TreeNode stateNode = new DefaultTreeNode("state",locCriteria, rootLocationTree);
						if(ParameterConst.LOCATION_LEVEL_STATE.equals(locationLevelCode)){
							if (stateIds.contains(state.getStateId())) {
								stateNode.setSelected(Boolean.TRUE);
							}
						}
						// build state tree
						if(ParameterConst.LOCATION_LEVEL_DISTRICT.equals(locationLevelCode)){
							if (state.getPmDivisions() != null && !state.getPmDivisions().isEmpty() && "13".equalsIgnoreCase(state.getStateCode())) {
								for (PmDivision division : state.getPmDivisions()) {
									CtPanelZoneDtl lodivcriteria  = locClass.newInstance();
									lodivcriteria.setDivisionId(division);
									TreeNode divisionNode = new DefaultTreeNode("division", lodivcriteria,stateNode);
									// build division tree
									for (PmDistrict district : division.getPmDistricts()) {
										CtPanelZoneDtl lodivcriteria1  = locClass.newInstance();
										lodivcriteria1.setDistrictId(district);
										TreeNode districtNode = new DefaultTreeNode("district",lodivcriteria1, divisionNode);
										// build district tree
										if (districtIds.contains(district.getDistrictId())) {
											districtNode.setSelected(Boolean.TRUE);
										}
									}
									if (divisionIds.contains(division.getDivisionId())) {
										divisionNode.setSelected(Boolean.TRUE);
									}
								}
							} else {

								for (PmDistrict district : state.getPmDistricts()) {
									if(district.getPmDivision() == null){
										CtPanelZoneDtl criteria = locClass.newInstance();
										criteria.setDistrictId(district);
										TreeNode districtNode =  null;
										districtNode = new DefaultTreeNode("district",criteria, stateNode);
										// build district tree
										if (districtIds.contains(district.getDistrictId())) {
											districtNode.setSelected(Boolean.TRUE);
										}
									}
									if (stateIds.contains(state.getStateId())) {
										stateNode.setSelected(Boolean.TRUE);
									}
								}
							}
						}
					}
				} 
			}	
			catch (InstantiationException e) {
				throw new EpException(e.getMessage());
			} catch (IllegalAccessException e) {
				throw new EpException(e.getMessage());
			}
			return rootLocationTree;
	}
}
