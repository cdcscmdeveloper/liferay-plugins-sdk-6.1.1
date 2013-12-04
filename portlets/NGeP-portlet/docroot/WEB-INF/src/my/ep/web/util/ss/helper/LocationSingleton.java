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
import my.ep.persistence.entity.ss.directpurchase.ScQBZone;
import my.ep.web.backingbean.ss.directpurchase.LocationDTO;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import weblogic.jdbc.wrapper.Array;

public class LocationSingleton {
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
		// initialization for country with countryId
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject("pm", IStateSO.class);
		/*IDivisionSO divisionSO = (IDivisionSO) BusinessDelegate.getInstance().getServiceObject("pm", IDivisionSO.class);
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject("pm", IDistrictSO.class);
		ICitySO citySO = (ICitySO) BusinessDelegate.getInstance().getServiceObject("pm", ICitySO.class);*/

		StateReqDTO stateReqDTO = new StateReqDTO();
		stateReqDTO.setCountryId(countryId);
		List<PmState> listStates = stateSO.retriveStateDivCityListByCountryId(stateReqDTO).getStateList();
		for (PmState pmSate : listStates) {
			/*DistrictReqDTO districReqDTO = new DistrictReqDTO();
			districReqDTO.setStateId(pmSate.getStateId());
			// initial district for state
			//List<PmDistrict> listDistricts = districtSO.retriveDistrictListByStateID(districReqDTO).getDistrictList();
			//pmSate.setPmDistricts(listDistricts);
			for (PmDistrict pmDistrict : pmSate.getPmDistricts()) {
				CityReqDTO cityReqDTO = new CityReqDTO();
				cityReqDTO.setDistrictId(pmDistrict.getDistrictId());
				// initial cities for district
				List<PmCity> listCities = citySO.retriveCityListByDistrictId(cityReqDTO).getCityList();
				pmDistrict.setPmCities(listCities);
			}*/
			Map<PmDivision, List<PmDistrict>>	uniqueDivisionDistrict	=	getUniqueDivisionDistricts(pmSate.getPmDistricts());
			Set<PmDivision>	divisionKeySet	=	uniqueDivisionDistrict.keySet();
			if(divisionKeySet != null && !divisionKeySet.isEmpty()){
				for(PmDivision division: divisionKeySet){
					division.setPmDistricts(uniqueDivisionDistrict.get(division));
				}
				pmSate.setPmDivisions(new ArrayList<PmDivision>(divisionKeySet));
			}else{
				pmSate.setPmDivisions(null);
			}
			/*DivisionReqDTO divisionReqDTO = new DivisionReqDTO();
			divisionReqDTO.setStateId(pmSate.getStateId());
			// initial divisions for state
			List<PmDivision> listDivisions = divisionSO.retriveDivisionListByState(divisionReqDTO).getDivisionList();
			pmSate.setPmDivisions(listDivisions);
			for (PmDivision pmDivision : pmSate.getPmDivisions()) {
				// get all the district belong to this division
				DistrictReqDTO districReq2DTO = new DistrictReqDTO();
				districReq2DTO.setDivisionId(pmDivision.getDivisionId());
				// initial district for division
				List<PmDistrict> listDistrict2s = districtSO.retriveDistrictListByDivision(districReq2DTO).getDistrictList();
				pmDivision.setPmDistricts(listDistrict2s);
				for (PmDistrict pmDistrict : pmDivision.getPmDistricts()) {
					CityReqDTO cityReqDTO = new CityReqDTO();
					cityReqDTO.setDistrictId(pmDistrict.getDistrictId());
					// initial city for district
					List<PmCity> listCities = citySO.retriveCityListByDistrictId(cityReqDTO).getCityList();
					pmDistrict.setPmCities(listCities);
				}
			}*/
		}
		mapStates.put(countryId, listStates);
		return listStates;
	}

	public TreeNode buildLocationTree(Long countryId, List<PmState> listStates, List<PmDistrict> listDistricts, List<PmDivision> listDivisions, List<PmCity> listCities) {
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
		if (listCities != null && listCities.size() > 0) {
			for (PmCity city : listCities) {
				cityIds.add(city.getCityId());
			}
		}

		if (allStates == null) {
			allStates = initialStatesForCountry(countryId);
		}

		// build tree
		TreeNode rootLocationTree = new DefaultTreeNode("root", null);
		for (PmState state : allStates) {
			LocationDTO stateLocation = new LocationDTO(state);
			TreeNode stateNode = new DefaultTreeNode(stateLocation, rootLocationTree);
			// build state tree
			for (PmDistrict district : state.getPmDistricts()) {
				if(district.getPmDivision() == null){
					LocationDTO districtLocation = new LocationDTO(district);
					TreeNode districtNode = new DefaultTreeNode(districtLocation, stateNode);
					// build district tree
					for (PmCity city : district.getPmCities()) {
						LocationDTO cityLocation = new LocationDTO(city);
						TreeNode cityNode = new DefaultTreeNode(cityLocation, districtNode);
						if (cityIds.contains(city.getCityId())) {
							cityNode.setSelected(Boolean.TRUE);
						}
					}
					if (districtIds.contains(district.getDistrictId())) {
						districtNode.setSelected(Boolean.TRUE);
					}
				}
			}
			if(state.getPmDivisions() != null && !state.getPmDivisions().isEmpty()){
				for (PmDivision division : state.getPmDivisions()) {
					LocationDTO divisionLocation = new LocationDTO(division);
					TreeNode divisionNode = new DefaultTreeNode(divisionLocation, stateNode);
					// build division tree
					for (PmDistrict district : division.getPmDistricts()) {
						LocationDTO districtLocation = new LocationDTO(district);
						TreeNode districtNode = new DefaultTreeNode(districtLocation, divisionNode);
						// build district tree
						for (PmCity city : district.getPmCities()) {
							LocationDTO cityLocation = new LocationDTO(city);
							TreeNode cityNode = new DefaultTreeNode(cityLocation, districtNode);
							if (cityIds.contains(city.getCityId())) {
								cityNode.setSelected(Boolean.TRUE);
							}
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
		return rootLocationTree;
	}
	/**
	 * 
	 */
	public	TreeNode	buildZoneTree(Map<String,List<TreeNode>> zoneTreeNode){
		TreeNode rootLocationTree = new DefaultTreeNode("root", null);
		if(zoneTreeNode != null && !zoneTreeNode.isEmpty()){
			Set<String>	keys	=	zoneTreeNode.keySet();
			for(String zoneName: keys){
				ScQBZone	zone	=	new ScQBZone();
				zone.setZoneName(zoneName);
				LocationDTO	zoneLocation	=	new LocationDTO(zone);
				TreeNode zoneNode = new DefaultTreeNode(zoneLocation, rootLocationTree);
				List<TreeNode>	stateNodes	=	getAllStateNode(zoneTreeNode.get(zoneName));
				zoneNode	=	addAllChildrenNode(zoneNode, stateNodes);
			}
		}
		return rootLocationTree;
	}
	private	List<TreeNode>	getAllStateNode(List<TreeNode> listTreeNodes){
		List<TreeNode>	results	=	new ArrayList<TreeNode>();
		if(listTreeNodes != null && !listTreeNodes.isEmpty()){
			for(TreeNode treeNode: listTreeNodes){
				LocationDTO locationDTO	=	(LocationDTO)treeNode.getData();
				if(locationDTO.getState() != null){
					results.add(treeNode);
				}
			}
		}
		List<TreeNode>	districtNodes	=	getAllDistrictNodes(listTreeNodes);
		Map<TreeNode,List<TreeNode>>	uniqueStateDistrictNodes	=	getUniqueStateNodeDistrictNodes(districtNodes);
		if(uniqueStateDistrictNodes != null && !uniqueStateDistrictNodes.isEmpty()){
			Set<TreeNode>	stateNodes	=	uniqueStateDistrictNodes.keySet();
			for(TreeNode stateNode: stateNodes){
				stateNode = clearAllChildrenNode(stateNode);
				stateNode	=	addAllChildrenNode(stateNode, uniqueStateDistrictNodes.get(stateNode));
				results.add(stateNode);
			}
		}
		List<TreeNode>	divisionNodes	=	getAllDivisionNodes(listTreeNodes);
		Map<TreeNode, List<TreeNode>>	uniqueStateDivisionNodes	=	getUniqueStateNodeDivisionNodes(divisionNodes);
		if(uniqueStateDivisionNodes != null && !uniqueStateDivisionNodes.isEmpty()){
			Set<TreeNode>	stateNodes	=	uniqueStateDivisionNodes.keySet();
			for(TreeNode stateNode: stateNodes){
				stateNode = clearAllChildrenNode(stateNode);
				stateNode	=	addAllChildrenNode(stateNode, uniqueStateDivisionNodes.get(stateNode));
				results.add(stateNode);
			}
		}
		return results;
	}
	
	private	Map<TreeNode, List<TreeNode>>	getUniqueStateNodeDivisionNodes(List<TreeNode> divisionNodes){
		Map<TreeNode, List<TreeNode>>	results	=	new HashMap<TreeNode, List<TreeNode>>();
		for(TreeNode divisionNode: divisionNodes){
			TreeNode	parentNode	=	divisionNode.getParent();
			if(isStateNode(parentNode)){
				if(results.get(parentNode) != null){
					results.get(parentNode).add(divisionNode);
				}else{
					List<TreeNode>	listDistrictNodes	=	new ArrayList<TreeNode>();
					listDistrictNodes.add(divisionNode);
					results.put(parentNode, listDistrictNodes);
				}
			}
		}
		return results;
	}
	private	List<TreeNode>	getAllDivisionNodes(List<TreeNode> treeNodes){
		List<TreeNode>	results	=	new ArrayList<>();
		for(TreeNode treeNode: treeNodes){
			LocationDTO locationDTO	=	(LocationDTO)treeNode.getData();
			if(locationDTO.getDivision() != null){
				results.add(treeNode);
			}
		}
		List<TreeNode>	districtNodes	=	getAllDistrictNodes(treeNodes);
		Map<TreeNode,List<TreeNode>>	uniqueDivisionDistrictNodes	=	getUniqueDivisionDistrictNodes(districtNodes);
		if(uniqueDivisionDistrictNodes != null && !uniqueDivisionDistrictNodes.isEmpty()){
			Set<TreeNode>	divisionNodes	=	uniqueDivisionDistrictNodes.keySet();
			for(TreeNode divisionNode: divisionNodes){
				divisionNode = clearAllChildrenNode(divisionNode);
				divisionNode	=	addAllChildrenNode(divisionNode, uniqueDivisionDistrictNodes.get(divisionNode));
				results.add(divisionNode);
			}
		}
		return results;
	}
	
	private	Map<TreeNode,List<TreeNode>>	getUniqueStateNodeDistrictNodes(List<TreeNode> districtNodes){
		Map<TreeNode, List<TreeNode>>	results	=	new HashMap<TreeNode, List<TreeNode>>();
		for(TreeNode districtNode: districtNodes){
			TreeNode	parentNode	=	districtNode.getParent();
			if(isStateNode(parentNode)){
				if(results.get(parentNode) != null){
					results.get(parentNode).add(districtNode);
				}else{
					List<TreeNode>	listDistrictNodes	=	new ArrayList<TreeNode>();
					listDistrictNodes.add(districtNode);
					results.put(parentNode, listDistrictNodes);
				}
			}
		}
		return results;
	}
	
	private	Map<TreeNode,List<TreeNode>>	getUniqueDivisionDistrictNodes(List<TreeNode> districtNodes){
		Map<TreeNode, List<TreeNode>>	results	=	new HashMap<TreeNode, List<TreeNode>>();
		for(TreeNode districtNode: districtNodes){
			TreeNode	parentNode	=	districtNode.getParent();
			if(isDivisionNode(parentNode)){
				if(results.get(parentNode) != null){
					results.get(parentNode).add(districtNode);
				}else{
					List<TreeNode>	listDistrictNodes	=	new ArrayList<TreeNode>();
					listDistrictNodes.add(districtNode);
					results.put(parentNode, listDistrictNodes);
				}
			}
		}
		return results;
	}
	
	private	List<TreeNode>	getAllDistrictNodes(List<TreeNode> listTreeNodes){
		List<TreeNode>	results	=	new ArrayList<TreeNode>();
		for(TreeNode treeNode : listTreeNodes){
			LocationDTO locationDTO	=	(LocationDTO)treeNode.getData();
			if(locationDTO.getDistrict() != null){
				results.add(treeNode);
			}
		}
		Map<TreeNode,List<TreeNode>>	uniqueDistrictCitiesNodes	=	getUniqueDistrictCitiesNodes(listTreeNodes);
		if(uniqueDistrictCitiesNodes != null && !uniqueDistrictCitiesNodes.isEmpty()){
			Set<TreeNode>	districtNodes	=	uniqueDistrictCitiesNodes.keySet();
			for(TreeNode districtNode: districtNodes){
				districtNode = clearAllChildrenNode(districtNode);
				districtNode	=	addAllChildrenNode(districtNode, uniqueDistrictCitiesNodes.get(districtNode));
				results.add(districtNode);
			}
		}
		return results;
	}
	private	Map<TreeNode,List<TreeNode>>	getUniqueDistrictCitiesNodes(List<TreeNode>	treeNodes){
		Map<TreeNode, List<TreeNode>>	results	=	new HashMap<TreeNode, List<TreeNode>>();
		List<TreeNode>	cityNodes	=	getAllCitiesNode(treeNodes);
		for(TreeNode cityNode: cityNodes){
			TreeNode	districtNode	=	cityNode.getParent();
			if(cityNode != null){
				if(results.get(districtNode) != null){
					results.get(districtNode).add(cityNode);
				}else{
					List<TreeNode>	listCityNodes	=	new ArrayList<TreeNode>();
					listCityNodes.add(cityNode);
					results.put(districtNode, listCityNodes);
				}
			}
		}
		return results;
	}
	private	List<TreeNode>	getAllCitiesNode(List<TreeNode> treeNodes){
		List<TreeNode>	results	=	new ArrayList<TreeNode>();
		if(treeNodes != null && !treeNodes.isEmpty()){
			for(TreeNode treeNode: treeNodes){
				LocationDTO locationDTO	=	(LocationDTO)treeNode.getData();
				if(locationDTO.getCity() != null){
					results.add(treeNode);
				}
			}
		}
		return results;
	}
	/**
	 * 
	 * @param zone
	 * @param listStates
	 * @param listDistricts
	 * @param listDivisions
	 * @param listCities
	 * @return
	 */
	public TreeNode buildZoneTree(ScQBZone zone, List<PmState> listStates, List<PmDistrict> listDistricts, List<PmDivision> listDivisions, List<PmCity> listCities) {
		TreeNode rootLocationTree = new DefaultTreeNode("root", null);
		List<TreeNode> districtNodes = buildDistrictNodes(listDistricts, listCities);
		List<TreeNode>	listDivisionNodes	=	buildDivisionNodes(listDivisions, districtNodes);
		Map<PmState, List<TreeNode>>	uniqueStateDivisionNodes	=	getUniqueStateDivisionNodes(listDivisionNodes);
		Map<PmState, List<TreeNode>>	uniqueStateDistrictNodes	=	getUniqueStateDistrictNodes(districtNodes);
		LocationDTO	zoneLocation	=	new LocationDTO(zone);
		TreeNode zoneNode = new DefaultTreeNode(zoneLocation, rootLocationTree);
		if(listStates != null && listStates.size() > 0){
			for(PmState state: listStates){
				LocationDTO stateLocation = new LocationDTO(state);
				TreeNode stateNode = new DefaultTreeNode(stateLocation, zoneNode);
			}
		}
		/*
		 * assume that state can only has either division or district, can't be both 
		 */
		//State	division
		Set<PmState>	setStateDivision	=	uniqueStateDivisionNodes.keySet();
		if(setStateDivision != null && !setStateDivision.isEmpty()){
			for(PmState state: setStateDivision){
				LocationDTO stateLocation = new LocationDTO(state);
				TreeNode stateNode = new DefaultTreeNode(stateLocation, zoneNode);
				List<TreeNode>	divisionNodes	=	uniqueStateDivisionNodes.get(state);
				stateNode = clearAllChildrenNode(stateNode);
				stateNode.getChildren().addAll(divisionNodes);
				/*for(TreeNode divisionNode: divisionNodes){
					divisionNode.setParent(stateNode);
				}*/
			}
		}
		//State	district
		Set<PmState>	setStateDistrict	=	uniqueStateDistrictNodes.keySet();
		if(setStateDistrict != null && !setStateDistrict.isEmpty()){
			for(PmState state: setStateDistrict){
				LocationDTO stateLocation = new LocationDTO(state);
				TreeNode stateNode = new DefaultTreeNode(stateLocation, zoneNode);
				List<TreeNode>	listDistrictNodes	=	uniqueStateDistrictNodes.get(state);
				stateNode = clearAllChildrenNode(stateNode);
				stateNode.getChildren().addAll(listDistrictNodes);
				/*for(TreeNode districtNode: stateNode.getChildren()){
					districtNode.setParent(stateNode);
				}*/
			}
		}
		return rootLocationTree;
	}

	private Map<PmState, List<TreeNode>> getUniqueStateDistrictNodes(List<TreeNode> listDistrictNodes) {
		Map<PmState, List<TreeNode>> results = new HashMap<PmState, List<TreeNode>>();
		for (TreeNode districtNode : listDistrictNodes) {
			LocationDTO districtLocationDTO = (LocationDTO) districtNode.getData();
			if (!isDivisionDistrict(districtLocationDTO.getDistrict())) {
				if (results.get(districtLocationDTO.getDistrict().getPmState()) != null) {
					results.get(districtLocationDTO.getDistrict().getPmState()).add(districtNode);
				} else {
					List<TreeNode> districtNodes = new ArrayList<TreeNode>();
					districtNodes.add(districtNode);
					results.put(districtLocationDTO.getDistrict().getPmState(), districtNodes);
				}
			}
		}
		return results;
	}

	private Map<PmState, List<TreeNode>> getUniqueStateDivisionNodes(List<TreeNode> listDivisionNodes) {
		Map<PmState, List<TreeNode>> results = new HashMap<PmState, List<TreeNode>>();
		for (TreeNode divisionNode : listDivisionNodes) {
			LocationDTO divisionLocationDTO = (LocationDTO) divisionNode.getData();
			if (results.get(divisionLocationDTO.getDivision().getPmState()) != null) {
				results.get(divisionLocationDTO.getDivision().getPmState()).add(divisionNode);
			} else {
				List<TreeNode> divisionNodes = new ArrayList<TreeNode>();
				divisionNodes.add(divisionNode);
				results.put(divisionLocationDTO.getDivision().getPmState(), divisionNodes);
			}
		}
		return results;
	}

	private List<TreeNode> buildDivisionNodes(List<PmDivision> listDivisions, List<TreeNode> districtNodes) {
		List<TreeNode> results = new ArrayList<>();
		Map<PmDivision, List<TreeNode>> mapUniqueDivision = getUniqueDivisionMap(districtNodes);
		Set<PmDivision> setDivisions = mapUniqueDivision.keySet();
		for (PmDivision division : setDivisions) {
			LocationDTO divisionLocation = new LocationDTO(division);
			TreeNode divisionNode = new DefaultTreeNode(divisionLocation, null);
			List<TreeNode> listDistrictNodes = mapUniqueDivision.get(division);
			for (TreeNode districtNode : listDistrictNodes) {
				districtNode.setParent(districtNode);
			}
			divisionNode.getChildren().addAll(listDistrictNodes);
			results.add(divisionNode);
		}
		if (listDivisions != null && listDivisions.size() > 0) {
			for (PmDivision division : listDivisions) {
				LocationDTO divisionLocation = new LocationDTO(division);
				TreeNode divisionNode = new DefaultTreeNode(divisionLocation, null);
				results.add(divisionNode);
			}
		}
		return results;
	}

	private Map<PmDivision, List<TreeNode>> getUniqueDivisionMap(List<TreeNode> listDistrictNodes) {
		Map<PmDivision, List<TreeNode>> results = new HashMap<PmDivision, List<TreeNode>>();
		for (TreeNode node : listDistrictNodes) {
			LocationDTO districtLocationDTO = (LocationDTO) node.getData();
			if (isDivisionDistrict(districtLocationDTO.getDistrict())) {
				if (results.get(districtLocationDTO.getDistrict().getPmDivision()) != null) {
					results.get(districtLocationDTO.getDistrict().getPmDivision()).add(node);
				} else {
					List<TreeNode> districtNodes = new ArrayList<TreeNode>();
					districtNodes.add(node);
					results.put(districtLocationDTO.getDistrict().getPmDivision(), districtNodes);
				}
			}
		}
		return results;
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

	private List<TreeNode> buildDistrictNodes(List<PmDistrict> districts, List<PmCity> cities) {
		List<TreeNode> results = new ArrayList<TreeNode>();
		for (PmDistrict district : districts) {
			LocationDTO districtLocation = new LocationDTO(district);
			TreeNode districtNode = new DefaultTreeNode(districtLocation, null);
			results.add(districtNode);
		}
		results.addAll(buildDistricTree(cities));
		return results;

	}

	private List<TreeNode> buildDistricTree(List<PmCity> listCities) {
		List<TreeNode> results = new ArrayList<TreeNode>();
		Map<PmDistrict, List<PmCity>> mapCities = getUniqueDistrictCitiesMap(listCities);
		if (mapCities != null && !mapCities.isEmpty()) {
			Set<PmDistrict> districtKeySet = mapCities.keySet();
			for (PmDistrict pmDistrict : districtKeySet) {
				LocationDTO districtLocation = new LocationDTO(pmDistrict);
				TreeNode districtNode = new DefaultTreeNode(districtLocation, null);
				// build district tree
				for (PmCity city : mapCities.get(pmDistrict)) {
					LocationDTO cityLocation = new LocationDTO(city);
					TreeNode cityNode = new DefaultTreeNode(cityLocation, districtNode);
				}
				results.add(districtNode);
			}
		}
		return results;
	}

	private Map<PmDistrict, List<PmCity>> getUniqueDistrictCitiesMap(List<PmCity> cities) {
		Map<PmDistrict, List<PmCity>> results = new HashMap<PmDistrict, List<PmCity>>();
		if (cities != null && cities.size() > 0) {
			for (PmCity city : cities) {
				if (results.get(city.getPmDistrict()) != null) {
					results.get(city.getPmDistrict()).add(city);
				} else {
					List<PmCity> listCities = new ArrayList<PmCity>();
					listCities.add(city);
					results.put(city.getPmDistrict(), listCities);
				}
			}
		}
		return results;
	}

	private Boolean isDivisionDistrict(PmDistrict district) {
		if (district.getPmDivision() != null) {
			return true;
		}
		return false;
	}
	private	Boolean	isDivisionNode(TreeNode treeNode){
		LocationDTO	locationDTO	=	(LocationDTO)	treeNode.getData();
		if(locationDTO.getDivision() != null){
			return true;
		}
		return false;
	}
	private	Boolean	isStateNode(TreeNode treeNode){
		LocationDTO	locationDTO	=	(LocationDTO)	treeNode.getData();
		if(locationDTO.getState() != null){
			return true;
		}
		return false;
	}
	private	TreeNode	clearAllChildrenNode(TreeNode treeNode){
		TreeNode	tmpTreeNode	=	new DefaultTreeNode(treeNode.getData(), treeNode.getParent());
		return tmpTreeNode;
	}
	private	TreeNode	addAllChildrenNode(TreeNode parentNode, List<TreeNode> childrenNode){
		for(TreeNode childNode: childrenNode){
			parentNode.getChildren().add(childNode);
		}
		return parentNode;
	}
}
