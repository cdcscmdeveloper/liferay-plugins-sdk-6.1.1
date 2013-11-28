package my.ep.web.util.ss.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.backingbean.ss.directpurchase.LocationDTO;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

public class LocationSingleton {
	private static LocationSingleton instance = null;
	private Map<Long, List<PmState>> mapStates = new HashMap<Long, List<PmState>>();

	private LocationSingleton() {
	}

	public static	LocationSingleton getInstance() {
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
	public	void	updateMapStates(Long countryId, List<PmState> pmStates){
		mapStates.put(countryId, pmStates);
	}
	public	Boolean	isInitialization(Long countryId){
		if(mapStates.get(countryId) != null && mapStates.get(countryId).size() > 0){
			return true;
		}
		return false;
	}
	public List<PmState> initialStatesForCountry(Long countryId) {
		// initialization for country with countryId
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject("pm", IStateSO.class);
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject("pm", IDistrictSO.class);
		IDivisionSO divisionSO = (IDivisionSO) BusinessDelegate.getInstance().getServiceObject("pm", IDivisionSO.class);
		ICitySO citySO = (ICitySO) BusinessDelegate.getInstance().getServiceObject("pm", ICitySO.class);

		StateReqDTO stateReqDTO = new StateReqDTO();
		stateReqDTO.setCountryId(countryId);
		List<PmState> listStates = stateSO.retriveStatesListByCountryID(stateReqDTO).getStateList();
		for (PmState pmSate : listStates) {
			DistrictReqDTO districReqDTO = new DistrictReqDTO();
			districReqDTO.setStateId(pmSate.getStateId());
			//initial district for state
			List<PmDistrict> listDistricts = districtSO.retriveDistrictListByStateID(districReqDTO).getDistrictList();
			pmSate.setPmDistricts(listDistricts);
			for (PmDistrict pmDistrict : listDistricts) {
				CityReqDTO cityReqDTO = new CityReqDTO();
				cityReqDTO.setDistrictId(pmDistrict.getDistrictId());
				//initial cities for district
				List<PmCity> listCities = citySO.retriveCityListByDistrictId(cityReqDTO).getCityList();
				pmDistrict.setPmCities(listCities);
			}
			DivisionReqDTO	divisionReqDTO = new DivisionReqDTO();
			divisionReqDTO.setStateId(pmSate.getStateId());
			//initial divisions for state
			List<PmDivision>	listDivisions	=	divisionSO.retriveDivisionListByState(divisionReqDTO).getDivisionList();
			pmSate.setPmDivisions(listDivisions);
			for (PmDivision pmDivision : listDivisions) {
				//get all the district belong to this division
				DistrictReqDTO districReq2DTO = new DistrictReqDTO();
				districReq2DTO.setDivisionId(pmDivision.getDivisionId());
				//initial district for division
				List<PmDistrict> listDistrict2s = districtSO.retriveDistrictListByDivision(districReq2DTO).getDistrictList();
				pmDivision.setPmDistricts(listDistrict2s);
				for (PmDistrict pmDistrict : listDistrict2s) {
					CityReqDTO cityReqDTO = new CityReqDTO();
					cityReqDTO.setDistrictId(pmDistrict.getDistrictId());
					//initial city for district
					List<PmCity> listCities = citySO.retriveCityListByDistrictId(cityReqDTO).getCityList();
					pmDistrict.setPmCities(listCities);
				}
			}
		}
		mapStates.put(countryId, listStates);
		return listStates;
	}
	public	TreeNode	buildLocationTree(Long countryId, List<PmState> listStates, List<PmDistrict> listDistricts, List<PmDivision> listDivisions, List<PmCity> listCities){
		List<PmState>	allStates	=	mapStates.get(countryId);
		Set<Long>	stateIds	=	new HashSet<Long>();
		Set<Long>	districtIds	=	new HashSet<Long>();
		Set<Long>	divisionIds	=	new HashSet<Long>();
		Set<Long>	cityIds	=	new HashSet<Long>();
		// get list Id for all state, district, division, city
		if(listStates != null && listStates.size() > 0){
			for(PmState state: listStates){
				stateIds.add(state.getStateId());
			}
		}
		if(listDistricts != null && listDistricts.size() > 0){
			for(PmDistrict district: listDistricts){
				districtIds.add(district.getDistrictId());
			}
		}
		if(listDivisions != null && listDivisions.size() > 0){
			for(PmDivision division: listDivisions){
				divisionIds.add(division.getDivisionId());
			}
		}
		if(listCities != null && listCities.size() > 0){
			for(PmCity city: listCities){
				cityIds.add(city.getCityId());
			}
		}
		
		if(allStates == null){
			allStates = initialStatesForCountry(countryId);
		}
		
		//build tree 
		TreeNode rootLocationTree = new DefaultTreeNode("root", null);
		for(PmState state: allStates){
			LocationDTO stateLocation	=	new LocationDTO(state);
			TreeNode stateNode	=	new DefaultTreeNode(stateLocation , rootLocationTree);
			// build state tree
			for(PmDistrict district: state.getPmDistricts()){
				LocationDTO districtLocation	=	new LocationDTO(district);
				TreeNode districtNode	=	new DefaultTreeNode(districtLocation, stateNode);
				//build district tree
				for(PmCity city: district.getPmCities()){
					LocationDTO	cityLocation	=	new LocationDTO(city);
					TreeNode cityNode	=	new DefaultTreeNode(cityLocation, districtNode);
					if(cityIds.contains(city.getCityId())){
						cityNode.setSelected(Boolean.TRUE);
					}
				}
				if(districtIds.contains(district.getDistrictId())){
					districtNode.setSelected(Boolean.TRUE);
				}
			}
			for(PmDivision division: state.getPmDivisions()){
				LocationDTO divisionLocation	=	new LocationDTO(division);
				TreeNode divisionNode	=	new DefaultTreeNode(divisionLocation, stateNode);
				//build division tree
				for(PmDistrict district: division.getPmDistricts()){
					LocationDTO districtLocation	=	new LocationDTO(district);
					TreeNode districtNode	=	new DefaultTreeNode(districtLocation, divisionNode);
					//build district tree
					for(PmCity city: district.getPmCities()){
						LocationDTO	cityLocation	=	new LocationDTO(city);
						TreeNode cityNode	=	new DefaultTreeNode(cityLocation, districtNode);
						if(cityIds.contains(city.getCityId())){
							cityNode.setSelected(Boolean.TRUE);
						}
					}
				}
				if(divisionIds.contains(division.getDivisionId())){
					divisionNode.setSelected(Boolean.TRUE);
				}
			}
			if(stateIds.contains(state.getStateId())){
				stateNode.setSelected(Boolean.TRUE);
			}
		}
		return rootLocationTree;
	}
	public	TreeNode	buildZoneTree(Long countryId, List<PmState> listStates, List<PmDistrict> listDistricts, List<PmDivision> listDivisions, List<PmCity> listCities){
		//TODO impl code
		return null;
	}
	
	private	Boolean	isDivisionDistrict(PmDistrict district){
		if(district.getPmDivision() != null){
			return true;
		}
		return false;
	}
	private	Map<PmDivision, List<TreeNode>>	buildDivisionTree(List<PmDistrict> listDistricts,List<TreeNode>	listDistrictNodes){
		return null;
	}
	
	private	List<TreeNode>	buildDistricTree(List<PmCity> listCities){
		List<TreeNode>	results	=	new ArrayList<TreeNode>();
		Map<PmDistrict, List<PmCity>> mapCities = getUniqueDistrictCitiesMap(listCities);
		if(mapCities != null && !mapCities.isEmpty()){
			Set<PmDistrict>	districtKeySet	=	mapCities.keySet();
			for(PmDistrict pmDistrict: districtKeySet){
				LocationDTO districtLocation	=	new LocationDTO(pmDistrict);
				TreeNode districtNode	=	new DefaultTreeNode(districtLocation, null);
				//build district tree
				for(PmCity city: mapCities.get(pmDistrict)){
					LocationDTO	cityLocation	=	new LocationDTO(city);
					TreeNode cityNode	=	new DefaultTreeNode(cityLocation, districtNode);
				}
				results.add(districtNode);
			}
		}
		return results;
	}
	private	Map<PmDistrict, List<PmCity>>	getUniqueDistrictCitiesMap(List<PmCity> cities){
		Map<PmDistrict, List<PmCity>>	results	=	new HashMap<PmDistrict, List<PmCity>>();
		if(cities != null && cities.size() > 0){
			for(PmCity city: cities){
				if(results.get(city.getPmDistrict()) != null){
					results.get(city.getPmDistrict()).add(city);
				}else{
					List<PmCity>	listCities	=	new ArrayList<PmCity>();
					listCities.add(city);
					results.put(city.getPmDistrict(), listCities);
				}
			}
		}
		return results;
	}
}
