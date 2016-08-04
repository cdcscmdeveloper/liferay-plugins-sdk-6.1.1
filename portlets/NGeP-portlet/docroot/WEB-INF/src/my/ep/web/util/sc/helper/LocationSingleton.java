/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util.sc.helper;

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
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.web.backingbean.sc.directpurchase.LocationDTO;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * 
 * LocationSingleton class is used to construct locations Based on level
 * 
 * @author VidyaSagarReddy
 * @since 22.11.2013
 * 
 */
public class LocationSingleton {
	public static final String STATE_LEVEL = "S";
	public static final String DIVISION_LEVEL = "V";
	public static final String DISTRICT_LEVEL = "D";
	private static LocationSingleton instance = null;
	private Map<Long, List<PmState>> mapStates = new HashMap<Long, List<PmState>>();

	private LocationSingleton() {
	}

	/**
	 * To get instance of LocationSingleton
	 * 
	 * @return LocationSingleton instance
	 */
	public static LocationSingleton getInstance() {
		if (instance == null) {
			instance = new LocationSingleton();
		}
		return instance;
	}

	/**
	 * To get All states based on countryid
	 * 
	 * @param countryId
	 * @return List<PmState> states
	 */
	public List<PmState> getPmStates(Long countryId) {
		if (mapStates.get(countryId) == null) {
			initialStatesForCountry(countryId);
		}
		return mapStates.get(countryId);
	}

	/**
	 * To map states and country
	 * 
	 * @param countryId
	 * @param pmStates
	 */
	public void updateMapStates(Long countryId, List<PmState> pmStates) {
		mapStates.put(countryId, pmStates);
	}

	/**
	 * To check whether map consist or not
	 * 
	 * @param countryId
	 * @return boolean flag
	 */
	public Boolean isInitialization(Long countryId) {
		if (mapStates.get(countryId) != null
				&& mapStates.get(countryId).size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * To get states for country id
	 * 
	 * @param countryId
	 * @return List<PmState> states
	 */
	public List<PmState> initialStatesForCountry(Long countryId) {
		// initialization for country with countryId

		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);

		StateReqDTO stateReqDTO = new StateReqDTO();
		stateReqDTO.setCountryId(countryId);
		List<PmState> listStates = stateSO.retriveStateDivCityListByCountryId(
				stateReqDTO).getStateList();

		for (PmState pmState : listStates) {

			Map<PmDivision, List<PmDistrict>> uniqueDivisionDistrict = getUniqueDivisionDistricts(pmState
					.getPmDistricts());

			Set<PmDivision> divisionKeySet = uniqueDivisionDistrict.keySet();
			if (divisionKeySet != null && !divisionKeySet.isEmpty()) {
				for (PmDivision division : divisionKeySet) {

					division.setPmDistricts(uniqueDivisionDistrict
							.get(division));
				}
				pmState.setPmDivisions(new ArrayList<PmDivision>(divisionKeySet));
			} else {
				pmState.setPmDivisions(null);
			}
		}
		mapStates.put(countryId, listStates);

		return listStates;
	}

	/**
	 * To build adress location tree
	 * 
	 * @param countryId
	 * @param listAddress
	 * @return TreeNode instance
	 */
	public TreeNode buildAddressLocationTree(Long countryId,
			List<PmAddress> listAddress) {
		List<PmState> allStates = mapStates.get(countryId);

		Set<Long> addressIds = new HashSet<Long>();
		if (listAddress != null && listAddress.size() > 0) {
			for (PmAddress pmAddress : listAddress) {
				addressIds.add(pmAddress.getAddressId());
			}
		}

		if (allStates == null) {
			allStates = initialStatesForCountry(countryId);
		}

		TreeNode rootAddressLocationTree = new DefaultTreeNode(
				QTConstants.NODE_ROOT, null);
		for (PmState state : allStates) {
			LocationDTO stateLocation = new LocationDTO(state);
			TreeNode stateNode = new DefaultTreeNode(stateLocation,
					rootAddressLocationTree);

			for (PmAddress address : state.getPmAddresses()) {
				if (address.getPmDivision() == null) {
					LocationDTO addressLocation = new LocationDTO(address);
					TreeNode addressNode = new DefaultTreeNode(addressLocation,
							stateNode);
					if (addressIds.contains(address.getAddressId())) {
						addressNode.setSelected(Boolean.TRUE);
					}
				}
			}
		}
		return rootAddressLocationTree;

	}

	/**
	 * To buld location PopupTree
	 * 
	 * @param countryId
	 * @param listStates
	 * @param listDistricts
	 * @param listDivisions
	 * @param listCities
	 * @param locationLevelCode
	 * @param locClass
	 * @return TreeNode instance
	 */
	public <LOCATION_CLASS extends TplSpecLoc> TreeNode buildLocationPopupTree(
			Long countryId, List<PmState> listStates,
			List<PmDistrict> listDistricts, List<PmDivision> listDivisions,
			List<PmCity> listCities, String locationLevelCode,
			Class<LOCATION_CLASS> locClass,
			boolean disabledSelectable) {
		// build tree
		TreeNode rootLocationTree = new DefaultTreeNode(QTConstants.NODE_ROOT,
				null);

		try {
			List<PmState> allStates = mapStates.get(countryId);
			Set<Long> stateIds = new HashSet<Long>();
			Set<Long> districtIds = new HashSet<Long>();
			Set<Long> divisionIds = new HashSet<Long>();
			Set<Long> stateNonSelectableIds = new HashSet<Long>();
			Set<Long> districtNonSelectableIds = new HashSet<Long>();
			Set<Long> divisionNonSelectableIds = new HashSet<Long>();

			// get list Id for all state, district, division
			if (listStates != null && listStates.size() > 0) {

				for (PmState state : listStates) {
					stateIds.add(state.getStateId());
					
					if (ParameterConst.LOCATION_LEVEL_STATE.equals(locationLevelCode) &&
							!state.isSelectable()) {
						stateNonSelectableIds.add(state.getStateId());
					}
				}
			}

			if (listDistricts != null && listDistricts.size() > 0) {

				for (PmDistrict district : listDistricts) {
					districtIds.add(district.getDistrictId());
					
					if (ParameterConst.LOCATION_LEVEL_DISTRICT.equals(locationLevelCode) &&
							!district.isSelectable()) {
						districtNonSelectableIds.add(district.getDistrictId());
					}
				}
			}

			if (listDivisions != null && listDivisions.size() > 0) {

				for (PmDivision division : listDivisions) {
					divisionIds.add(division.getDivisionId());
					
					if ((ParameterConst.LOCATION_LEVEL_DISTRICT.equals(locationLevelCode) ||
							ParameterConst.LOCATION_LEVEL_DIVISION.equals(locationLevelCode)) &&
							!division.isSelectable()) {
						divisionNonSelectableIds.add(division.getDivisionId());
					}
				}
			}

			if (allStates == null) {

				allStates = initialStatesForCountry(countryId);
			}

			for (PmState state : allStates) {
				// LocationDTO stateLocation = new LocationDTO(state);
				if (!disabledSelectable || !stateNonSelectableIds.contains(state.getStateId())) {
					LOCATION_CLASS locCriteria = locClass.newInstance();
					locCriteria.setPmState(state);
					TreeNode stateNode = new DefaultTreeNode(
							QTConstants.NODE_STATE, locCriteria, rootLocationTree);
					// build state tree

					if (ParameterConst.LOCATION_LEVEL_STATE
							.equals(locationLevelCode)) {
						if (stateIds.contains(state.getStateId())) {
							stateNode.setSelected(Boolean.TRUE);
						}
					} else if (ParameterConst.LOCATION_LEVEL_DIVISION
							.equals(locationLevelCode)) {

						if (state.getPmDivisions() != null
								&& !state.getPmDivisions().isEmpty()) {

							for (PmDivision division : state.getPmDivisions()) {
								if (!disabledSelectable || !divisionNonSelectableIds.contains(division.getDivisionId())) {
									LOCATION_CLASS locCriteria1 = locClass
											.newInstance();
									locCriteria1.setPmDivision(division);
									TreeNode divisionNode = new DefaultTreeNode(
											QTConstants.NODE_DIVISION, locCriteria1,
											stateNode);
						
									if (divisionIds.contains(division.getDivisionId())) {
										divisionNode.setSelected(Boolean.TRUE);
									}
								}
							}
						}
						if (stateIds.contains(state.getStateId())) {
							stateNode.setSelected(Boolean.TRUE);
						}
					} else if (ParameterConst.LOCATION_LEVEL_DISTRICT
							.equals(locationLevelCode)) {

						for (PmDistrict district : state.getPmDistricts()) {
							if (district.getPmDivision() == null) {
								if (!disabledSelectable || !districtNonSelectableIds.contains(district.getDistrictId())) {
									LOCATION_CLASS criteria = locClass.newInstance();
									criteria.setPmDistrict(district);
									TreeNode districtNode = new DefaultTreeNode(
											QTConstants.NODE_DISTRICT, criteria,
											stateNode);
								
									if (districtIds.contains(district.getDistrictId())) {
										districtNode.setSelected(Boolean.TRUE);
									}
								}
							}
						}
						if (state.getPmDivisions() != null
								&& !state.getPmDivisions().isEmpty()) {

							for (PmDivision division : state.getPmDivisions()) {
								if (!disabledSelectable || !divisionNonSelectableIds.contains(division.getDivisionId())) {
									LOCATION_CLASS locCriteria1 = locClass
											.newInstance();
									locCriteria1.setPmDivision(division);
									// LocationDTO divisionLocation = new
									// LocationDTO(division);
									TreeNode divisionNode = new DefaultTreeNode(
											QTConstants.NODE_DIVISION, locCriteria1,
											stateNode);
									// build division tree
									for (PmDistrict district : division
											.getPmDistricts()) {
										// LocationDTO districtLocation = new
										// LocationDTO(district);
										if (!disabledSelectable || !districtNonSelectableIds.contains(district.getDistrictId())) {
											LOCATION_CLASS locCriteria11 = locClass
													.newInstance();
											locCriteria11.setPmDistrict(district);
											TreeNode districtNode = new DefaultTreeNode(
													QTConstants.NODE_DISTRICT,
													locCriteria11, divisionNode);
											
											if (districtIds.contains(district
													.getDistrictId())) {
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
			}
		} catch (InstantiationException e) {
			throw new EpException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new EpException(e.getMessage());
		}

		return rootLocationTree;
	}

	/**
	 * To Build Location TreeNode
	 * 
	 * @param zone
	 * @param listStates
	 * @param listDistricts
	 * @param listDivisions
	 * @param listCities
	 * @param locClass
	 * @return TreeNode instance
	 */
	public <LOCATION_CLASS extends TplSpecLoc> TreeNode buildLocationTree(
			TplSpecZoneDtl zone, List<PmState> listStates,
			List<PmDistrict> listDistricts, List<PmDivision> listDivisions,
			List<PmCity> listCities, Class<LOCATION_CLASS> locClass) {
		TreeNode rootLocationTree = new DefaultTreeNode(QTConstants.NODE_ROOT,
				null);

		try {
			Map<PmState, Map<PmDistrict, List<PmCity>>> mapStatesDistrictsCities = getUniqueStateDistrictsCities(
					listDistricts, listCities);

			Map<PmState, Map<PmDivision, Map<PmDistrict, List<PmCity>>>> mapStatesDivisionsDistrictsCities = getUniqueStatesDivisionsDistrictsCities(
					listDistricts, listCities);
			Map<PmState, List<PmDivision>> mapStatesDivision = getUniqueStatesDivision(listDivisions);

			TreeNode zoneNode = null;
			if (zone != null) {
				// LocationDTO zoneLocation = new LocationDTO(zone);

				// Build zone - state
				zoneNode = new DefaultTreeNode("zone", zone, rootLocationTree);
			}

			// LocationDTO zoneLocation = new LocationDTO(zone);
			// Build zone - state
			// TreeNode zoneNode = new DefaultTreeNode("zone",zoneLocation,
			// rootLocationTree);
			if (listStates != null && listStates.size() > 0) {
				for (PmState state : listStates) {
					// LocationDTO stateLocation = new LocationDTO(state);
					LOCATION_CLASS stateLocation = locClass.newInstance();
					stateLocation.setPmState(state);

					if (zoneNode != null) {
						if (locClass.isAssignableFrom(TplSpecZoneLoc.class)) {
							((TplSpecZoneLoc) stateLocation)
									.setTplSpecZoneDtl(zone);
						}

						new DefaultTreeNode(QTConstants.NODE_STATE,
								stateLocation, zoneNode);
					} else {
						new DefaultTreeNode(QTConstants.NODE_STATE,
								stateLocation, rootLocationTree);
					}
				}
			}
			Set<PmState> statesDivisionKeySet = mapStatesDivision.keySet();
			if (statesDivisionKeySet != null && !statesDivisionKeySet.isEmpty()) {
				for (PmState state : statesDivisionKeySet) {
					// LocationDTO stateLocation = new LocationDTO(state);
					LOCATION_CLASS stateLocation = locClass.newInstance();
					stateLocation.setPmState(state);
					TreeNode stateNode = null;
					if (zoneNode != null) {
						if (locClass.isAssignableFrom(TplSpecZoneLoc.class)) {
							((TplSpecZoneLoc) stateLocation)
									.setTplSpecZoneDtl(zone);
						}
						stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
								stateLocation, zoneNode);
					} else {
						stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
								stateLocation, rootLocationTree);
					}

					List<PmDivision> divisions = mapStatesDivision.get(state);
					if (divisions != null && !divisions.isEmpty()) {
						for (PmDivision division : divisions) {
							// LocationDTO divisionLocation = new
							// LocationDTO(division);
							LOCATION_CLASS divisionLocation = locClass
									.newInstance();
							divisionLocation.setPmDivision(division);
							divisionLocation.setPmState(state);

							if (locClass.isAssignableFrom(TplSpecZoneLoc.class)) {
								((TplSpecZoneLoc) divisionLocation)
										.setTplSpecZoneDtl(zone);
							}

							new DefaultTreeNode(
									QTConstants.NODE_DIVISION,
									divisionLocation, stateNode);
						}
					}
				}
			}
			// build zone - state - district - city
			Set<PmState> statesDistrictsCitiesKeySet = mapStatesDistrictsCities
					.keySet();
			if (statesDistrictsCitiesKeySet != null
					&& !statesDistrictsCitiesKeySet.isEmpty()) {
				for (PmState state : statesDistrictsCitiesKeySet) {
					// LocationDTO stateLocation = new LocationDTO(state);
					LOCATION_CLASS stateLocation = locClass.newInstance();
					stateLocation.setPmState(state);
					TreeNode stateNode = null;
					if (zoneNode != null) {
						if (locClass.isAssignableFrom(TplSpecZoneLoc.class)) {
							((TplSpecZoneLoc) stateLocation)
									.setTplSpecZoneDtl(zone);
						}
						stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
								stateLocation, zoneNode);
					} else {
						stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
								stateLocation, rootLocationTree);
					}

					// TreeNode stateNode = new
					// DefaultTreeNode(QTConstants.NODE_STATE,stateLocation,
					// zoneNode);
					Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapStatesDistrictsCities
							.get(state);
					Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
							.keySet();
					if (districtsCitiesKeySet != null
							&& !districtsCitiesKeySet.isEmpty()) {
						for (PmDistrict district : districtsCitiesKeySet) {
							// LocationDTO districtLocation = new
							// LocationDTO(district);
							LOCATION_CLASS districtLocation = locClass
									.newInstance();
							districtLocation.setPmDistrict(district);
							districtLocation.setPmState(state);
							if (locClass.isAssignableFrom(TplSpecZoneLoc.class)) {
								((TplSpecZoneLoc) districtLocation)
										.setTplSpecZoneDtl(zone);
							}
							TreeNode districtNode = new DefaultTreeNode(
									QTConstants.NODE_DISTRICT,
									districtLocation, stateNode);
							List<PmCity> cities = mapDistrictsCities
									.get(district);
							if (cities != null && !cities.isEmpty()) {
								for (PmCity city : cities) {
									LocationDTO cityLocation = new LocationDTO(
											city);
									new DefaultTreeNode(
											"city", cityLocation, districtNode);
								}
							}
						}
					}
				}
			}
			// build zone - state - division - city
			Set<PmState> statesDivisionsDistrictsCitiesKeySet = mapStatesDivisionsDistrictsCities
					.keySet();
			if (statesDivisionsDistrictsCitiesKeySet != null
					&& !statesDivisionsDistrictsCitiesKeySet.isEmpty()) {
				for (PmState state : statesDivisionsDistrictsCitiesKeySet) {
					// LocationDTO stateLocation = new LocationDTO(state);
					LOCATION_CLASS stateLocation = locClass.newInstance();
					stateLocation.setPmState(state);
					TreeNode stateNode = null;
					if (zoneNode != null) {
						if (locClass.isAssignableFrom(TplSpecZoneLoc.class)) {
							((TplSpecZoneLoc) stateLocation)
									.setTplSpecZoneDtl(zone);
						}
						stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
								stateLocation, zoneNode);
					} else {
						stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
								stateLocation, rootLocationTree);
					}
					// TreeNode stateNode = new
					// DefaultTreeNode(QTConstants.NODE_STATE,stateLocation,
					// zoneNode);
					Map<PmDivision, Map<PmDistrict, List<PmCity>>> mapDivisionsDistrictsCities = mapStatesDivisionsDistrictsCities
							.get(state);
					Set<PmDivision> divisionsDistrictsCitiesKeySet = mapDivisionsDistrictsCities
							.keySet();
					if (divisionsDistrictsCitiesKeySet != null
							&& !divisionsDistrictsCitiesKeySet.isEmpty()) {
						for (PmDivision division : divisionsDistrictsCitiesKeySet) {
							// LocationDTO divisionLocation = new
							// LocationDTO(division);
							LOCATION_CLASS divisionLocation = locClass
									.newInstance();
							divisionLocation.setPmDivision(division);
							divisionLocation.setPmState(state);
							if (locClass.isAssignableFrom(TplSpecZoneLoc.class)) {
								((TplSpecZoneLoc) divisionLocation)
										.setTplSpecZoneDtl(zone);
							}
							TreeNode divisionNode = new DefaultTreeNode(
									QTConstants.NODE_DIVISION,
									divisionLocation, stateNode);
							Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapDivisionsDistrictsCities
									.get(division);
							Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
									.keySet();
							if (districtsCitiesKeySet != null
									&& !districtsCitiesKeySet.isEmpty()) {
								for (PmDistrict district : districtsCitiesKeySet) {
									// LocationDTO districtLocation = new
									// LocationDTO(district);
									LOCATION_CLASS districtLocation = locClass
											.newInstance();
									districtLocation.setPmDistrict(district);
									districtLocation.setPmState(state);
									districtLocation.setPmDivision(division);
									if (locClass
											.isAssignableFrom(TplSpecZoneLoc.class)) {
										((TplSpecZoneLoc) districtLocation)
												.setTplSpecZoneDtl(zone);
									}
									TreeNode districtNode = new DefaultTreeNode(
											QTConstants.NODE_DISTRICT,
											districtLocation, divisionNode);
									List<PmCity> cities = mapDistrictsCities
											.get(district);
									if (cities != null && !cities.isEmpty()) {
										for (PmCity city : cities) {
											LocationDTO cityLocation = new LocationDTO(
													city);
											new DefaultTreeNode(
													"city", cityLocation,
													districtNode);
										}
									}
								}
							}
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
	 * To getUniqueState and Divisions
	 * 
	 * @param divisions
	 * @return Map insatance
	 */
	public Map<PmState, List<PmDivision>> getUniqueStatesDivision(
			List<PmDivision> divisions) {
		Map<PmState, List<PmDivision>> results = new HashMap<PmState, List<PmDivision>>();
		if (divisions != null && !divisions.isEmpty()) {
			for (PmDivision division : divisions) {
				PmState state = division.getPmState();
				if (results.get(state) != null && !results.get(state).isEmpty()) {
					results.get(state).add(division);
				} else {
					List<PmDivision> listDivision = new ArrayList<PmDivision>();
					listDivision.add(division);
					results.put(state, listDivision);
				}
			}
		}
		return results;
	}

	/**
	 * To getUniqueStatesDivisionsDistrictsCities
	 * 
	 * @param districts
	 * @param cities
	 * @return Map instance
	 */
	public Map<PmState, Map<PmDivision, Map<PmDistrict, List<PmCity>>>> getUniqueStatesDivisionsDistrictsCities(
			List<PmDistrict> districts, List<PmCity> cities) {
		Map<PmState, Map<PmDivision, Map<PmDistrict, List<PmCity>>>> results = new HashMap<PmState, Map<PmDivision, Map<PmDistrict, List<PmCity>>>>();
		Map<PmDivision, Map<PmDistrict, List<PmCity>>> mapDivisionsDistrictsCities = getUniqueDivisionsDistrictsCities(
				districts, cities);
		Set<PmDivision> divisionsKeySet = mapDivisionsDistrictsCities.keySet();
		if (divisionsKeySet != null && !divisionsKeySet.isEmpty()) {
			for (PmDivision division : divisionsKeySet) {
				PmState state = division.getPmState();
				if (results.get(state) != null && !results.get(state).isEmpty()) {
					results.get(state).put(division,
							mapDivisionsDistrictsCities.get(division));
				} else {
					Map<PmDivision, Map<PmDistrict, List<PmCity>>> map = new HashMap<PmDivision, Map<PmDistrict, List<PmCity>>>();
					map.put(division, mapDivisionsDistrictsCities.get(division));
					results.put(state, map);
				}
			}
		}
		return results;
	}

	/**
	 * To get UniqueDivisionsDistrictsCities
	 * 
	 * @param districts
	 * @param cities
	 * @return Map instance
	 */
	private Map<PmDivision, Map<PmDistrict, List<PmCity>>> getUniqueDivisionsDistrictsCities(
			List<PmDistrict> districts, List<PmCity> cities) {

		Map<PmDistrict, List<PmCity>> mapDistrictsCities = getUniqueDistrictCities(
				districts, cities);
		Map<PmDivision, Map<PmDistrict, List<PmCity>>> results = new HashMap<PmDivision, Map<PmDistrict, List<PmCity>>>();
		Set<PmDistrict> setDistricts = mapDistrictsCities.keySet();
		for (PmDistrict district : setDistricts) {
			if (isDivisionDistrict(district)) {
				PmDivision division = district.getPmDivision();
				if (results.get(division) != null
						&& !results.get(division).isEmpty()) {
					results.get(division).put(district,
							mapDistrictsCities.get(district));
				} else {
					Map<PmDistrict, List<PmCity>> map = new HashMap<PmDistrict, List<PmCity>>();
					map.put(district, mapDistrictsCities.get(district));
					results.put(division, map);
				}
			}
		}
		return results;
	}

	private Map<PmDivision, List<PmDistrict>> getUniqueDivisionDistricts(
			List<PmDistrict> pmDistricts) {

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
	 * To get UniqueStateDistrictsCities
	 * 
	 * @param districts
	 * @param cities
	 * @return Map instance
	 */
	public Map<PmState, Map<PmDistrict, List<PmCity>>> getUniqueStateDistrictsCities(
			List<PmDistrict> districts, List<PmCity> cities) {

		Map<PmDistrict, List<PmCity>> mapDistrictsCities = getUniqueDistrictCities(
				districts, cities);
		Map<PmState, Map<PmDistrict, List<PmCity>>> results = new HashMap<PmState, Map<PmDistrict, List<PmCity>>>();
		Set<PmDistrict> setDistricts = mapDistrictsCities.keySet();
		for (PmDistrict district : setDistricts) {
			if (!isDivisionDistrict(district)) {
				PmState state = district.getPmState();
				if (results.get(state) != null && !results.get(state).isEmpty()) {
					results.get(state).put(district,
							mapDistrictsCities.get(district));
				} else {
					Map<PmDistrict, List<PmCity>> map = new HashMap<PmDistrict, List<PmCity>>();
					map.put(district, mapDistrictsCities.get(district));
					results.put(state, map);
				}
			}
		}
		return results;
	}

	private Map<PmDistrict, List<PmCity>> getUniqueDistrictCities(
			List<PmDistrict> districts, List<PmCity> cities) {

		Map<PmDistrict, List<PmCity>> results = new HashMap<PmDistrict, List<PmCity>>();
		for (PmDistrict district : districts) {
			results.put(district, new ArrayList<PmCity>());
		}
		Map<PmDistrict, List<PmCity>> districtCities = getUniqueDistrictCitiesMap(cities);
		Set<PmDistrict> keySet = districtCities.keySet();
		if (keySet != null && !keySet.isEmpty()) {
			for (PmDistrict district : keySet) {
				if (results.get(district) != null) {
					results.get(district).addAll(districtCities.get(district));
				} else {
					results.put(district, districtCities.get(district));
				}
			}
		}
		return results;

	}

	private Map<PmDistrict, List<PmCity>> getUniqueDistrictCitiesMap(
			List<PmCity> cities) {

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

	/**
	 * To construct TreeNode
	 * 
	 * @param tplSpecZoneDtl
	 * @param specLocs
	 * @return TreeNode instance
	 */
	public TreeNode constuctTreeNode(TplSpecZoneDtl tplSpecZoneDtl,
			List<TplSpecZoneLoc> specLocs) {
		TreeNode root = new DefaultTreeNode(QTConstants.NODE_ROOT, null);
		if (tplSpecZoneDtl != null) {
			TreeNode zoneNode = new DefaultTreeNode(QTConstants.NODE_ZONE,
					tplSpecZoneDtl, root);
			Map<Long, TreeNode> stateMap = new HashMap<Long, TreeNode>();
			Map<Long, TreeNode> divisionMap = new HashMap<Long, TreeNode>();
			List<PmDistrict> districts = new ArrayList<PmDistrict>();
			TreeNode stateNode = null;
			TreeNode divisonNode = null;
			// List<TreeNode> locations = new ArrayList<TreeNode>();
			for (TplSpecLoc locCriteria : specLocs) {
				PmState pmState = locCriteria.getPmState();

				if (null != pmState) {
					stateNode = stateMap.get(pmState.getStateId());
					if (stateNode == null) {
						stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
								locCriteria, zoneNode);
						stateMap.put(pmState.getStateId(), stateNode);	
					}
				} else {
					stateNode = null;
				}
				
				PmDivision division = locCriteria.getPmDivision();
				if (null != division) {
					divisonNode = divisionMap.get(division.getDivisionId());
					if (divisonNode == null) {
						divisonNode = new DefaultTreeNode(
								QTConstants.NODE_DIVISION, locCriteria, stateNode);
						divisionMap.put(division.getDivisionId(), divisonNode);	
					}
				} else {
					divisonNode = null;
				}
				
				PmDistrict pmDistrict = locCriteria.getPmDistrict();
				if (null != pmDistrict && !districts.contains(pmDistrict)) {
					if (divisonNode == null) {
						new DefaultTreeNode(QTConstants.NODE_DISTRICT,
								locCriteria, stateNode);

					} else {
						new DefaultTreeNode(QTConstants.NODE_DISTRICT,
								locCriteria, divisonNode);

					}
					districts.add(pmDistrict);
				}
			}
		} else {

		}
		return root;
	}

	/**
	 * To construct TreeNode
	 * 
	 * @param specLocs
	 * @return TreeNode instance
	 */
	public List<TreeNode> constuctTreeNode(List<? extends TplSpecLoc> specLocs) {
		List<TreeNode> rootNode = new ArrayList<TreeNode>();
		if (specLocs != null) {

			List<PmState> states = new ArrayList<PmState>();
			List<PmDivision> divisions = new ArrayList<PmDivision>();
			List<PmDistrict> districts = new ArrayList<PmDistrict>();

			TreeNode stateNode = null;
			TreeNode divisonNode = null;
			// List<TreeNode> locations = new ArrayList<TreeNode>();
			for (TplSpecLoc locCriteria : specLocs) {
				PmState pmState = locCriteria.getPmState();

				if (null != pmState && !states.contains(pmState)) {
					divisonNode = null;
					TreeNode root = new DefaultTreeNode(QTConstants.NODE_ROOT,
							null);
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							locCriteria, root);
					rootNode.add(root);
					// locations.add(root);
				}
				PmDivision division = locCriteria.getPmDivision();

				if (null != division && !divisions.contains(division)) {
					divisonNode = new DefaultTreeNode(
							QTConstants.NODE_DIVISION, locCriteria, stateNode);
					divisions.add(division);

				}
				PmDistrict pmDistrict = locCriteria.getPmDistrict();

				if (null != pmDistrict && !districts.contains(pmDistrict)) {
					if (divisonNode == null) {
						new DefaultTreeNode(QTConstants.NODE_DISTRICT,
								locCriteria, stateNode);

					} else {
						new DefaultTreeNode(QTConstants.NODE_DISTRICT,
								locCriteria, divisonNode);

					}
					districts.add(pmDistrict);
				}
				states.add(pmState);
			}
		}
		return rootNode;
	}

	/**
	 * To construct Panel TreeNode
	 * 
	 * @param ctPanelZones
	 * @param panelZoneDtls
	 * @return TreeNode instance
	 */
	public TreeNode constuctPanelTreeNodes(CtPanelZone ctPanelZones,
			List<CtPanelZoneDtl> panelZoneDtls) {
		TreeNode root = new DefaultTreeNode(QTConstants.NODE_ROOT, null);
		if (null != ctPanelZones) {
			// for (CtPanelZone ctPanelZone : ctPanelZones) {
			TplSpecZoneDtl specZoneDtl = new TplSpecZoneDtl();
			specZoneDtl.setZoneName(ctPanelZones.getName());
			specZoneDtl.setPanelZone(ctPanelZones);
			TreeNode zoneNode = new DefaultTreeNode(QTConstants.NODE_ZONE,
					specZoneDtl, root);
			List<PmState> states = new ArrayList<PmState>();

			TreeNode stateNode = null;
			// List<CtPanelZoneDtl> panelZoneDtls = ctPanelZones
			// .getPanelZoneDtls();

			for (CtPanelZoneDtl panelZoneDtl : panelZoneDtls) {
				TplSpecZoneLoc zoneLoc = new TplSpecZoneLoc();
				PmState pmState = panelZoneDtl.getStateId();

				if (null != pmState && !states.contains(pmState)) {
					zoneLoc.setPmState(pmState);
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							zoneLoc, zoneNode);

				}
				PmDistrict pmDistrict = panelZoneDtl.getDistrictId();
				if (pmDistrict != null) {
					zoneLoc.setPmDistrict(pmDistrict);
					new DefaultTreeNode(QTConstants.NODE_DISTRICT, zoneLoc,
							stateNode);

				}
				states.add(pmState);
			}

			// }
		}
		return root;
	}

	/**
	 * To delete TreeNode
	 * 
	 * @param rootTrees
	 */
	public void deleteTreeNodes(List<TreeNode> rootTrees) {
		if (rootTrees != null) {
			for (int row = 0; row < rootTrees.size(); row++) {
				TreeNode rootTree = rootTrees.get(row);

				for (TreeNode zoneTree : rootTree.getChildren()) {
					if (zoneTree.isSelected()) {
						rootTrees.remove(row--);
					} else {
						deleteChildTreeNodes(zoneTree.getChildren());

						if (zoneTree.getChildren().size() == 0) {
							rootTrees.remove(row--);
						}
					}
				}
			}
		}
	}

	private void deleteChildTreeNodes(List<TreeNode> treeNodes) {
		if (treeNodes != null) {
			List<TreeNode> nodesToRemoved = new ArrayList<TreeNode>();

			for (TreeNode treeNode : treeNodes) {
				if (treeNode.isSelected()) {
					nodesToRemoved.add(treeNode);
				} else {
					if (treeNode.getChildren() != null
							&& treeNode.getChildren().size() > 0) {
						deleteChildTreeNodes(treeNode.getChildren());

						if (treeNode.getChildren().size() == 0) {
							nodesToRemoved.add(treeNode);
						}
					}
				}
			}

			if (nodesToRemoved.size() > 0) {
				treeNodes.removeAll(nodesToRemoved);
			}
		}
	}

	/**
	 * To construct Location TreeNode
	 * 
	 * @param listStates
	 * @param listDistricts
	 * @param listDivisions
	 * @param listCities
	 * @param locClass
	 * @return TreeNode instance
	 */
	public <LOCATION_CLASS extends TplSpecLoc> TreeNode constuctLocationTree(
			String locationLevel, List<PmState> listStates,
			List<PmDistrict> listDistricts, List<PmDivision> listDivisions,
			List<PmCity> listCities, Class<LOCATION_CLASS> locClass) {
		TreeNode mainNode = new DefaultTreeNode(QTConstants.NODE_ROOT, null);

		try {
			Map<PmState, Map<PmDistrict, List<PmCity>>> mapStatesDistrictsCities = getUniqueStateDistrictsCities(
					listDistricts, listCities);
			Map<PmState, Map<PmDivision, Map<PmDistrict, List<PmCity>>>> mapStatesDivisionsDistrictsCities = getUniqueStatesDivisionsDistrictsCities(
					listDistricts, listCities);
			Map<PmState, List<PmDivision>> mapStatesDivision = getUniqueStatesDivision(listDivisions);

			if (listStates != null && listStates.size() > 0) {
				for (PmState state : listStates) {
					LOCATION_CLASS stateLocation = locClass.newInstance();
					stateLocation.setPmState(state);
					TreeNode rootNode = new DefaultTreeNode(
							QTConstants.NODE_ROOT, stateLocation, mainNode);
					new DefaultTreeNode(
							QTConstants.NODE_STATE, stateLocation, rootNode);
				}
			}

			if (ParameterConst.LOCATION_LEVEL_DIVISION.equals(locationLevel)) {
				Set<PmState> statesDivisionKeySet = mapStatesDivision.keySet();
				if (statesDivisionKeySet != null
						&& !statesDivisionKeySet.isEmpty()) {
					for (PmState state : statesDivisionKeySet) {
						LOCATION_CLASS stateLocation = locClass.newInstance();
						stateLocation.setPmState(state);
						TreeNode node = new DefaultTreeNode(
								QTConstants.NODE_ROOT, stateLocation, mainNode);
						TreeNode stateNode = new DefaultTreeNode(
								QTConstants.NODE_STATE, stateLocation, node);

						List<PmDivision> divisions = mapStatesDivision
								.get(state);
						if (divisions != null && !divisions.isEmpty()) {
							for (PmDivision division : divisions) {
								LOCATION_CLASS divisionLocation = locClass
										.newInstance();
								divisionLocation.setPmDivision(division);
								divisionLocation.setPmState(state);
								new DefaultTreeNode(
										QTConstants.NODE_DIVISION,
										divisionLocation, stateNode);
							}
						}
					}
				}
			}
			// build zone - state - district - city
			Set<PmState> statesDistrictsCitiesKeySet = mapStatesDistrictsCities
					.keySet();
			if (statesDistrictsCitiesKeySet != null
					&& !statesDistrictsCitiesKeySet.isEmpty()) {
				for (PmState state : statesDistrictsCitiesKeySet) {
					LOCATION_CLASS stateLocation = locClass.newInstance();
					stateLocation.setPmState(state);

					TreeNode rootLocationTree = new DefaultTreeNode(
							QTConstants.NODE_ROOT, mainNode);
					TreeNode stateNode = new DefaultTreeNode(
							QTConstants.NODE_STATE, stateLocation,
							rootLocationTree);

					Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapStatesDistrictsCities
							.get(state);
					Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
							.keySet();
					if (districtsCitiesKeySet != null
							&& !districtsCitiesKeySet.isEmpty()) {
						for (PmDistrict district : districtsCitiesKeySet) {
							LOCATION_CLASS districtLocation = locClass
									.newInstance();
							districtLocation.setPmDistrict(district);
							districtLocation.setPmState(state);

							TreeNode districtNode = new DefaultTreeNode(
									QTConstants.NODE_DISTRICT,
									districtLocation, stateNode);
							List<PmCity> cities = mapDistrictsCities
									.get(district);
							if (cities != null && !cities.isEmpty()) {
								for (PmCity city : cities) {
									LocationDTO cityLocation = new LocationDTO(
											city);
									new DefaultTreeNode(
											"city", cityLocation, districtNode);
								}
							}
						}
					}
				}
			}
			// build zone - state - division - city
			Set<PmState> statesDivisionsDistrictsCitiesKeySet = mapStatesDivisionsDistrictsCities
					.keySet();
			if (statesDivisionsDistrictsCitiesKeySet != null
					&& !statesDivisionsDistrictsCitiesKeySet.isEmpty()) {
				TreeNode rootLocationTree = new DefaultTreeNode(
						QTConstants.NODE_ROOT, mainNode);
				for (PmState state : statesDivisionsDistrictsCitiesKeySet) {
					LOCATION_CLASS stateLocation = locClass.newInstance();
					stateLocation.setPmState(state);

					TreeNode stateNode = new DefaultTreeNode(
							QTConstants.NODE_STATE, stateLocation,
							rootLocationTree);

					Map<PmDivision, Map<PmDistrict, List<PmCity>>> mapDivisionsDistrictsCities = mapStatesDivisionsDistrictsCities
							.get(state);
					Set<PmDivision> divisionsDistrictsCitiesKeySet = mapDivisionsDistrictsCities
							.keySet();

					if (divisionsDistrictsCitiesKeySet != null
							&& !divisionsDistrictsCitiesKeySet.isEmpty()) {
						for (PmDivision division : divisionsDistrictsCitiesKeySet) {

							LOCATION_CLASS divisionLocation = locClass
									.newInstance();
							divisionLocation.setPmDivision(division);
							divisionLocation.setPmState(state);

							TreeNode divisionNode = new DefaultTreeNode(
									QTConstants.NODE_DIVISION,
									divisionLocation, stateNode);
							Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapDivisionsDistrictsCities
									.get(division);
							Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
									.keySet();
							if (districtsCitiesKeySet != null
									&& !districtsCitiesKeySet.isEmpty()) {
								for (PmDistrict district : districtsCitiesKeySet) {
									LOCATION_CLASS districtLocation = locClass
											.newInstance();
									districtLocation.setPmDistrict(district);
									districtLocation.setPmState(state);
									districtLocation.setPmDivision(division);

									TreeNode districtNode = new DefaultTreeNode(
											QTConstants.NODE_DISTRICT,
											districtLocation, divisionNode);
									List<PmCity> cities = mapDistrictsCities
											.get(district);
									if (cities != null && !cities.isEmpty()) {
										for (PmCity city : cities) {
											LocationDTO cityLocation = new LocationDTO(
													city);
											new DefaultTreeNode(
													"city", cityLocation,
													districtNode);
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (InstantiationException e) {
			throw new EpException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new EpException(e.getMessage());
		}

		return mainNode;
	}

	/**
	 * To construct TreeNode disableChailds
	 * 
	 * @param zone
	 * @param listStates
	 * @param listDistricts
	 * @param listDivisions
	 * @param listCities
	 * @param boolean
	 * @param locClass
	 * @return TreeNode instance
	 */
	public <LOCATION_CLASS extends TplSpecLoc> TreeNode buildTreeWithDisabledChilds(
			TplSpecZoneDtl zone, List<PmState> listStates,
			List<PmDistrict> listDistricts, List<PmDivision> listDivisions,
			List<PmCity> listCities, boolean disable,
			Class<LOCATION_CLASS> locClass) {
		TreeNode rootLocationTree = new DefaultTreeNode(QTConstants.NODE_ROOT,
				null);
		try {
			Map<PmState, Map<PmDistrict, List<PmCity>>> mapStatesDistrictsCities = getUniqueStateDistrictsCities(
					listDistricts, listCities);

			Map<PmState, Map<PmDivision, Map<PmDistrict, List<PmCity>>>> mapStatesDivisionsDistrictsCities = getUniqueStatesDivisionsDistrictsCities(
					listDistricts, listCities);
			Map<PmState, List<PmDivision>> mapStatesDivision = getUniqueStatesDivision(listDivisions);

			TreeNode zoneNode = null;
			if (zone != null) {
				zoneNode = new DefaultTreeNode("zone", zone, rootLocationTree);
			}

			if (listStates != null && listStates.size() > 0) {
				for (PmState state : listStates) {
					LOCATION_CLASS stateLocation = locClass.newInstance();
					stateLocation.setPmState(state);

					TreeNode stateNode = null;
					if (zoneNode != null) {
						if (locClass.isAssignableFrom(TplSpecZoneLoc.class)) {
							((TplSpecZoneLoc) stateLocation)
									.setTplSpecZoneDtl(zone);
						}

						stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
								stateLocation, zoneNode);
						stateNode.setSelectable(disable);
					} else {
						stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
								stateLocation, rootLocationTree);
						stateNode.setSelectable(disable);
					}
				}
			}
			Set<PmState> statesDivisionKeySet = mapStatesDivision.keySet();
			if (statesDivisionKeySet != null && !statesDivisionKeySet.isEmpty()) {
				for (PmState state : statesDivisionKeySet) {
					// LocationDTO stateLocation = new LocationDTO(state);
					LOCATION_CLASS stateLocation = locClass.newInstance();
					stateLocation.setPmState(state);
					TreeNode stateNode = null;
					if (zoneNode != null) {
						if (locClass.isAssignableFrom(TplSpecZoneLoc.class)) {
							((TplSpecZoneLoc) stateLocation)
									.setTplSpecZoneDtl(zone);
						}
						stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
								stateLocation, zoneNode);
						stateNode.setSelectable(disable);
					} else {
						stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
								stateLocation, rootLocationTree);
						stateNode.setSelectable(disable);
					}

					List<PmDivision> divisions = mapStatesDivision.get(state);
					if (divisions != null && !divisions.isEmpty()) {
						for (PmDivision division : divisions) {
							LOCATION_CLASS divisionLocation = locClass
									.newInstance();
							divisionLocation.setPmDivision(division);
							divisionLocation.setPmState(state);

							if (locClass.isAssignableFrom(TplSpecZoneLoc.class)) {
								((TplSpecZoneLoc) divisionLocation)
										.setTplSpecZoneDtl(zone);
							}

							TreeNode divisionNode = new DefaultTreeNode(
									QTConstants.NODE_DIVISION,
									divisionLocation, stateNode);
							divisionNode.setSelectable(disable);
						}
					}
				}
			}
			// build zone - state - district - city
			Set<PmState> statesDistrictsCitiesKeySet = mapStatesDistrictsCities
					.keySet();
			if (statesDistrictsCitiesKeySet != null
					&& !statesDistrictsCitiesKeySet.isEmpty()) {
				for (PmState state : statesDistrictsCitiesKeySet) {
					// LocationDTO stateLocation = new LocationDTO(state);
					LOCATION_CLASS stateLocation = locClass.newInstance();
					stateLocation.setPmState(state);
					TreeNode stateNode = null;
					if (zoneNode != null) {
						if (locClass.isAssignableFrom(TplSpecZoneLoc.class)) {
							((TplSpecZoneLoc) stateLocation)
									.setTplSpecZoneDtl(zone);
						}
						stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
								stateLocation, zoneNode);
						stateNode.setSelectable(disable);
					} else {
						stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
								stateLocation, rootLocationTree);
						stateNode.setSelectable(disable);
					}

					// TreeNode stateNode = new
					// DefaultTreeNode(QTConstants.NODE_STATE,stateLocation,
					// zoneNode);
					Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapStatesDistrictsCities
							.get(state);
					Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
							.keySet();
					if (districtsCitiesKeySet != null
							&& !districtsCitiesKeySet.isEmpty()) {
						for (PmDistrict district : districtsCitiesKeySet) {
							// LocationDTO districtLocation = new
							// LocationDTO(district);
							LOCATION_CLASS districtLocation = locClass
									.newInstance();
							districtLocation.setPmDistrict(district);
							districtLocation.setPmState(state);
							if (locClass.isAssignableFrom(TplSpecZoneLoc.class)) {
								((TplSpecZoneLoc) districtLocation)
										.setTplSpecZoneDtl(zone);
							}
							TreeNode districtNode = new DefaultTreeNode(
									QTConstants.NODE_DISTRICT,
									districtLocation, stateNode);
							districtNode.setSelectable(disable);
						}
					}
				}
			}
			// build zone - state - division - city
			Set<PmState> statesDivisionsDistrictsCitiesKeySet = mapStatesDivisionsDistrictsCities
					.keySet();
			if (statesDivisionsDistrictsCitiesKeySet != null
					&& !statesDivisionsDistrictsCitiesKeySet.isEmpty()) {
				for (PmState state : statesDivisionsDistrictsCitiesKeySet) {
					// LocationDTO stateLocation = new LocationDTO(state);
					LOCATION_CLASS stateLocation = locClass.newInstance();
					stateLocation.setPmState(state);
					TreeNode stateNode = null;
					if (zoneNode != null) {
						if (locClass.isAssignableFrom(TplSpecZoneLoc.class)) {
							((TplSpecZoneLoc) stateLocation)
									.setTplSpecZoneDtl(zone);
						}
						stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
								stateLocation, zoneNode);
						stateNode.setSelectable(disable);
					} else {
						stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
								stateLocation, rootLocationTree);
						stateNode.setSelectable(disable);
					}

					Map<PmDivision, Map<PmDistrict, List<PmCity>>> mapDivisionsDistrictsCities = mapStatesDivisionsDistrictsCities
							.get(state);
					Set<PmDivision> divisionsDistrictsCitiesKeySet = mapDivisionsDistrictsCities
							.keySet();
					if (divisionsDistrictsCitiesKeySet != null
							&& !divisionsDistrictsCitiesKeySet.isEmpty()) {
						for (PmDivision division : divisionsDistrictsCitiesKeySet) {
							LOCATION_CLASS divisionLocation = locClass
									.newInstance();
							divisionLocation.setPmDivision(division);
							divisionLocation.setPmState(state);
							if (locClass.isAssignableFrom(TplSpecZoneLoc.class)) {
								((TplSpecZoneLoc) divisionLocation)
										.setTplSpecZoneDtl(zone);
							}
							TreeNode divisionNode = new DefaultTreeNode(
									QTConstants.NODE_DIVISION,
									divisionLocation, stateNode);
							divisionNode.setSelectable(disable);
							Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapDivisionsDistrictsCities
									.get(division);
							Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
									.keySet();
							if (districtsCitiesKeySet != null
									&& !districtsCitiesKeySet.isEmpty()) {
								for (PmDistrict district : districtsCitiesKeySet) {
									LOCATION_CLASS districtLocation = locClass
											.newInstance();
									districtLocation.setPmDistrict(district);
									districtLocation.setPmState(state);
									districtLocation.setPmDivision(division);
									if (locClass
											.isAssignableFrom(TplSpecZoneLoc.class)) {
										((TplSpecZoneLoc) districtLocation)
												.setTplSpecZoneDtl(zone);
									}
									TreeNode districtNode = new DefaultTreeNode(
											QTConstants.NODE_DISTRICT,
											districtLocation, divisionNode);
									districtNode.setSelectable(disable);

								}
							}
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

}
