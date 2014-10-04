package my.ep.web.util.fl.helper;

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
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZoneDtl;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.web.backingbean.sc.directpurchase.LocationDTO;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

public class LocationSingleton {
	public static final String STATE_LEVEL = "S";
	public static final String DIVISION_LEVEL = "V";
	public static final String DISTRICT_LEVEL = "D";
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
		if (mapStates.get(countryId) != null
				&& mapStates.get(countryId).size() > 0) {
			return true;
		}
		return false;
	}

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
			// update the relationship for the parent
			if (pmState.getPmDistricts() != null
					&& !pmState.getPmDistricts().isEmpty()) {
				for (PmDistrict district : pmState.getPmDistricts()) {
					// district.setPmState(pmState);

					if (district.getPmCities() != null
							&& !district.getPmCities().isEmpty()) {

						for (PmCity city : district.getPmCities()) {

							// city.setPmDistrict(district);
						}
					}
				}
			}
			if (pmState.getPmDivisions() != null
					&& !pmState.getPmDivisions().isEmpty()) {
				for (PmDivision division : pmState.getPmDivisions()) {
					// division.setPmState(pmState);
					if (division.getPmDistricts() != null
							&& !division.getPmDistricts().isEmpty()) {
						for (PmDistrict district : division.getPmDistricts()) {
							// district.setPmState(pmState);
							if (district.getPmCities() != null
									&& !district.getPmCities().isEmpty()) {
								for (PmCity city : district.getPmCities()) {
									// city.setPmDistrict(district);

								}
							}
						}
					}
				}
			}
		}
		mapStates.put(countryId, listStates);
		return listStates;
	}

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
	public <LOCATION_CLASS extends TplSpecLoc> TreeNode buildLocationPopupTree(
			Long countryId, List<PmState> listStates,
			List<PmDistrict> listDistricts, List<PmDivision> listDivisions,
			List<PmCity> listCities, String locationLevelCode,
			Class<LOCATION_CLASS> locClass) {
		// build tree
		TreeNode rootLocationTree = new DefaultTreeNode(QTConstants.NODE_ROOT,
				null);

		try {
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

			for (PmState state : allStates) {
				// LocationDTO stateLocation = new LocationDTO(state);

				LOCATION_CLASS locCriteria = locClass.newInstance();
				locCriteria.setPmState(state);
				TreeNode stateNode = new DefaultTreeNode(
						QTConstants.NODE_STATE, locCriteria, rootLocationTree);
				// build state tree

				if (ParameterConst.LOCATION_LEVEL_STATE
						.equals(locationLevelCode)
						|| ParameterConst.LOCATION_LEVEL_DISTRICT
								.equals(locationLevelCode)) {
					if (stateIds.contains(state.getStateId())) {
						stateNode.setSelected(Boolean.TRUE);
					}
				}

				if (ParameterConst.LOCATION_LEVEL_DIVISION
						.equals(locationLevelCode)) {

					for (PmDistrict district : state.getPmDistricts()) {
						if (district.getPmDivision() == null) {

							// LocationDTO districtLocation = new
							// LocationDTO(district);
							// TreeNode districtNode = new
							// DefaultTreeNode(districtLocation, stateNode);
							// build district tree
							for (PmCity city : district.getPmCities()) {
								// /LocationDTO cityLocation = new
								// LocationDTO(city);
								// TreeNode cityNode = new
								// DefaultTreeNode(cityLocation, districtNode);
								if (cityIds.contains(city.getCityId())) {
									// cityNode.setSelected(Boolean.TRUE);
								}
							}
							if (districtIds.contains(district.getDistrictId())) {
								// districtNode.setSelected(Boolean.TRUE);
							}
						}
					}
					if (state.getPmDivisions() != null
							&& !state.getPmDivisions().isEmpty()) {

						for (PmDivision division : state.getPmDivisions()) {

							LOCATION_CLASS locCriteria1 = locClass
									.newInstance();
							locCriteria1.setPmDivision(division);
							TreeNode divisionNode = new DefaultTreeNode(
									QTConstants.NODE_DIVISION, locCriteria1,
									stateNode);
							// build division tree
							for (PmDistrict district : division
									.getPmDistricts()) {
								// LocationDTO districtLocation = new
								// LocationDTO(district);
								// TreeNode districtNode = new
								// DefaultTreeNode(districtLocation,
								// divisionNode);
								// build district tree
								for (PmCity city : district.getPmCities()) {
									LocationDTO cityLocation = new LocationDTO(
											city);
									// TreeNode cityNode = new
									// DefaultTreeNode(cityLocation,
									// districtNode);
									if (cityIds.contains(city.getCityId())) {
										// cityNode.setSelected(Boolean.TRUE);
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

				if (ParameterConst.LOCATION_LEVEL_DISTRICT
						.equals(locationLevelCode)) {

					for (PmDistrict district : state.getPmDistricts()) {
						if (district.getPmDivision() == null) {

							LOCATION_CLASS criteria = locClass.newInstance();
							criteria.setPmDistrict(district);
							TreeNode districtNode = new DefaultTreeNode(
									QTConstants.NODE_DISTRICT, criteria,
									stateNode);
							// build district tree
							for (PmCity city : district.getPmCities()) {
								LocationDTO cityLocation = new LocationDTO(city);
								// TreeNode cityNode = new
								// DefaultTreeNode(cityLocation, districtNode);
								if (cityIds.contains(city.getCityId())) {
									// cityNode.setSelected(Boolean.TRUE);
								}
							}
							if (districtIds.contains(district.getDistrictId())) {
								districtNode.setSelected(Boolean.TRUE);
							}
						}
					}
					if (state.getPmDivisions() != null
							&& !state.getPmDivisions().isEmpty()) {

						for (PmDivision division : state.getPmDivisions()) {
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
								LOCATION_CLASS locCriteria11 = locClass
										.newInstance();
								locCriteria11.setPmDistrict(district);
								TreeNode districtNode = new DefaultTreeNode(
										QTConstants.NODE_DISTRICT,
										locCriteria11, divisionNode);
								// build district tree
								for (PmCity city : district.getPmCities()) {
									LocationDTO cityLocation = new LocationDTO(
											city);
									// TreeNode cityNode = new
									// DefaultTreeNode(cityLocation,
									// districtNode);
									if (cityIds.contains(city.getCityId())) {
										// cityNode.setSelected(Boolean.TRUE);
									}
								}
								if (districtIds.contains(district
										.getDistrictId())) {
									districtNode.setSelected(Boolean.TRUE);
								}

							}
							if (divisionIds.contains(division.getDivisionId())) {
								divisionNode.setSelected(Boolean.TRUE);
							}
						}
					}
					if (stateIds.contains(state.getStateId())) {
						// stateNode.setSelected(Boolean.TRUE);
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

	@Deprecated
	public TreeNode buildLocationTree(Long countryId, List<PmState> listStates,
			List<PmDistrict> listDistricts, List<PmDivision> listDivisions,
			List<PmCity> listCities) {
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
		TreeNode rootLocationTree = new DefaultTreeNode(QTConstants.NODE_ROOT,
				null);
		for (PmState state : allStates) {
			LocationDTO stateLocation = new LocationDTO(state);
			TreeNode stateNode = new DefaultTreeNode(stateLocation,
					rootLocationTree);
			// build state tree

			for (PmDistrict district : state.getPmDistricts()) {
				if (district.getPmDivision() == null) {

					LocationDTO districtLocation = new LocationDTO(district);
					TreeNode districtNode = new DefaultTreeNode(
							districtLocation, stateNode);
					// build district tree
					for (PmCity city : district.getPmCities()) {
						LocationDTO cityLocation = new LocationDTO(city);
						TreeNode cityNode = new DefaultTreeNode(cityLocation,
								districtNode);
						if (cityIds.contains(city.getCityId())) {
							cityNode.setSelected(Boolean.TRUE);
						}
					}
					if (districtIds.contains(district.getDistrictId())) {
						districtNode.setSelected(Boolean.TRUE);
					}
				}
			}
			if (state.getPmDivisions() != null
					&& !state.getPmDivisions().isEmpty()) {

				for (PmDivision division : state.getPmDivisions()) {
					LocationDTO divisionLocation = new LocationDTO(division);
					TreeNode divisionNode = new DefaultTreeNode(
							divisionLocation, stateNode);
					// build division tree
					for (PmDistrict district : division.getPmDistricts()) {
						LocationDTO districtLocation = new LocationDTO(district);
						TreeNode districtNode = new DefaultTreeNode(
								districtLocation, divisionNode);
						// build district tree
						for (PmCity city : district.getPmCities()) {
							LocationDTO cityLocation = new LocationDTO(city);
							TreeNode cityNode = new DefaultTreeNode(
									cityLocation, districtNode);
							if (cityIds.contains(city.getCityId())) {
								// scityNode.setSelected(Boolean.TRUE);
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

	@Deprecated
	public TreeNode buildLocationZoneTree(Long countryId,
			List<PmState> listStates, List<PmDistrict> listDistricts,
			List<PmDivision> listDivisions, List<PmCity> listCities,
			Integer location) {
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
		TreeNode rootLocationTree = new DefaultTreeNode(QTConstants.NODE_ROOT,
				null);

		for (PmState state : allStates) {
			// LocationDTO stateLocation = new LocationDTO(state);

			ScQtLocCriteria locCriteria = new ScQtLocCriteria();
			locCriteria.setPmState(state);
			TreeNode stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
					locCriteria, rootLocationTree);
			// build state tree

			if (location == 120) {

				for (PmDistrict district : state.getPmDistricts()) {
					if (district.getPmDivision() == null) {

						// LocationDTO districtLocation = new
						// LocationDTO(district);
						// TreeNode districtNode = new
						// DefaultTreeNode(districtLocation, stateNode);
						// build district tree
						for (PmCity city : district.getPmCities()) {
							// /LocationDTO cityLocation = new
							// LocationDTO(city);
							// TreeNode cityNode = new
							// DefaultTreeNode(cityLocation, districtNode);
							if (cityIds.contains(city.getCityId())) {
								// cityNode.setSelected(Boolean.TRUE);
							}
						}
						if (districtIds.contains(district.getDistrictId())) {
							// districtNode.setSelected(Boolean.TRUE);
						}
					}
				}
				if (state.getPmDivisions() != null
						&& !state.getPmDivisions().isEmpty()) {

					for (PmDivision division : state.getPmDivisions()) {

						ScQtLocCriteria locCriteria1 = new ScQtLocCriteria();
						locCriteria1.setPmDivision(division);
						TreeNode divisionNode = new DefaultTreeNode(
								QTConstants.NODE_DIVISION, locCriteria1,
								stateNode);
						// build division tree
						for (PmDistrict district : division.getPmDistricts()) {
							// LocationDTO districtLocation = new
							// LocationDTO(district);
							// TreeNode districtNode = new
							// DefaultTreeNode(districtLocation, divisionNode);
							// build district tree
							for (PmCity city : district.getPmCities()) {
								LocationDTO cityLocation = new LocationDTO(city);
								// TreeNode cityNode = new
								// DefaultTreeNode(cityLocation, districtNode);
								if (cityIds.contains(city.getCityId())) {
									// cityNode.setSelected(Boolean.TRUE);
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

			if (location == 121) {

				for (PmDistrict district : state.getPmDistricts()) {
					if (district.getPmDivision() == null) {

						ScQtLocCriteria criteria = new ScQtLocCriteria();
						criteria.setPmDistrict(district);
						TreeNode districtNode = new DefaultTreeNode(
								QTConstants.NODE_DISTRICT, criteria, stateNode);
						// build district tree
						for (PmCity city : district.getPmCities()) {
							LocationDTO cityLocation = new LocationDTO(city);
							// TreeNode cityNode = new
							// DefaultTreeNode(cityLocation, districtNode);
							if (cityIds.contains(city.getCityId())) {
								// cityNode.setSelected(Boolean.TRUE);
							}
						}
						if (districtIds.contains(district.getDistrictId())) {
							districtNode.setSelected(Boolean.TRUE);
						}
					}
				}
				if (state.getPmDivisions() != null
						&& !state.getPmDivisions().isEmpty()) {

					for (PmDivision division : state.getPmDivisions()) {
						ScQtLocCriteria locCriteria1 = new ScQtLocCriteria();
						locCriteria1.setPmDivision(division);
						// LocationDTO divisionLocation = new
						// LocationDTO(division);
						TreeNode divisionNode = new DefaultTreeNode(
								QTConstants.NODE_DIVISION, locCriteria1,
								stateNode);
						// build division tree
						for (PmDistrict district : division.getPmDistricts()) {
							// LocationDTO districtLocation = new
							// LocationDTO(district);
							ScQtLocCriteria locCriteria11 = new ScQtLocCriteria();
							locCriteria11.setPmDistrict(district);
							TreeNode districtNode = new DefaultTreeNode(
									QTConstants.NODE_DISTRICT, locCriteria11,
									divisionNode);
							// build district tree
							for (PmCity city : district.getPmCities()) {
								LocationDTO cityLocation = new LocationDTO(city);
								// TreeNode cityNode = new
								// DefaultTreeNode(cityLocation, districtNode);
								if (cityIds.contains(city.getCityId())) {
									// cityNode.setSelected(Boolean.TRUE);
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

		}
		return rootLocationTree;
	}

	@Deprecated
	public TreeNode buildLocationZonalTree(Long countryId,
			List<PmState> listStates, List<PmDistrict> listDistricts,
			List<PmDivision> listDivisions, List<PmCity> listCities,
			Integer location) {
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
		TreeNode rootLocationTree = new DefaultTreeNode(QTConstants.NODE_ROOT,
				null);

		for (PmState state : allStates) {
			// LocationDTO stateLocation = new LocationDTO(state);

			TplSpecZoneLoc locCriteria = new TplSpecZoneLoc();
			locCriteria.setPmState(state);
			TreeNode stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
					locCriteria, rootLocationTree);

			// build state tree

			if (location == 2) {

				for (PmDistrict district : state.getPmDistricts()) {
					if (district.getPmDivision() == null) {

						// LocationDTO districtLocation = new
						// LocationDTO(district);
						// TreeNode districtNode = new
						// DefaultTreeNode(districtLocation, stateNode);
						// build district tree
						for (PmCity city : district.getPmCities()) {
							// /LocationDTO cityLocation = new
							// LocationDTO(city);
							// TreeNode cityNode = new
							// DefaultTreeNode(cityLocation, districtNode);
							if (cityIds.contains(city.getCityId())) {
								// cityNode.setSelected(Boolean.TRUE);
							}
						}
						if (districtIds.contains(district.getDistrictId())) {
							// districtNode.setSelected(Boolean.TRUE);
						}
					}
				}
				if (state.getPmDivisions() != null
						&& !state.getPmDivisions().isEmpty()) {

					for (PmDivision division : state.getPmDivisions()) {

						TplSpecZoneLoc locCriteria1 = new TplSpecZoneLoc();
						locCriteria1.setPmDivision(division);
						TreeNode divisionNode = new DefaultTreeNode(
								QTConstants.NODE_DIVISION, locCriteria1,
								stateNode);
						// build division tree
						for (PmDistrict district : division.getPmDistricts()) {
							// LocationDTO districtLocation = new
							// LocationDTO(district);
							// TreeNode districtNode = new
							// DefaultTreeNode(districtLocation, divisionNode);
							// build district tree
							for (PmCity city : district.getPmCities()) {
								LocationDTO cityLocation = new LocationDTO(city);
								// TreeNode cityNode = new
								// DefaultTreeNode(cityLocation, districtNode);
								if (cityIds.contains(city.getCityId())) {
									// cityNode.setSelected(Boolean.TRUE);
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

			if (location == 3) {

				for (PmDistrict district : state.getPmDistricts()) {
					if (district.getPmDivision() == null) {

						TplSpecZoneLoc criteria = new TplSpecZoneLoc();
						criteria.setPmDistrict(district);
						TreeNode districtNode = new DefaultTreeNode(
								QTConstants.NODE_DISTRICT, criteria, stateNode);
						// build district tree
						for (PmCity city : district.getPmCities()) {
							LocationDTO cityLocation = new LocationDTO(city);
							// TreeNode cityNode = new
							// DefaultTreeNode(cityLocation, districtNode);
							if (cityIds.contains(city.getCityId())) {
								// cityNode.setSelected(Boolean.TRUE);
							}
						}
						if (districtIds.contains(district.getDistrictId())) {
							districtNode.setSelected(Boolean.TRUE);
						}
					}
				}
				if (state.getPmDivisions() != null
						&& !state.getPmDivisions().isEmpty()) {

					for (PmDivision division : state.getPmDivisions()) {
						TplSpecZoneLoc locCriteria1 = new TplSpecZoneLoc();
						locCriteria1.setPmDivision(division);
						// LocationDTO divisionLocation = new
						// LocationDTO(division);
						TreeNode divisionNode = new DefaultTreeNode(
								QTConstants.NODE_DIVISION, locCriteria1,
								stateNode);
						// build division tree
						for (PmDistrict district : division.getPmDistricts()) {
							// LocationDTO districtLocation = new
							// LocationDTO(district);
							TplSpecZoneLoc locCriteria11 = new TplSpecZoneLoc();
							locCriteria11.setPmDistrict(district);
							TreeNode districtNode = new DefaultTreeNode(
									QTConstants.NODE_DISTRICT, locCriteria11,
									divisionNode);
							// build district tree
							for (PmCity city : district.getPmCities()) {
								LocationDTO cityLocation = new LocationDTO(city);
								// TreeNode cityNode = new
								// DefaultTreeNode(cityLocation, districtNode);
								if (cityIds.contains(city.getCityId())) {
									// cityNode.setSelected(Boolean.TRUE);
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

		}
		return rootLocationTree;
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

					// new DefaultTreeNode(QTConstants.NODE_STATE,stateLocation,
					// zoneNode);
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

							TreeNode divisionNode = new DefaultTreeNode(
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
									TreeNode cityNode = new DefaultTreeNode(
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
											TreeNode cityNode = new DefaultTreeNode(
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
	 * 
	 * @param zone
	 * @param listStates
	 * @param listDistricts
	 * @param listDivisions
	 * @param listCities
	 * @return
	 */
	@Deprecated
	public TreeNode buildZoneTree(TplSpecZoneDtl zone,
			List<PmState> listStates, List<PmDistrict> listDistricts,
			List<PmDivision> listDivisions, List<PmCity> listCities) {
		TreeNode rootLocationTree = new DefaultTreeNode(QTConstants.NODE_ROOT,
				null);

		Map<PmState, Map<PmDistrict, List<PmCity>>> mapStatesDistrictsCities = getUniqueStateDistrictsCities(
				listDistricts, listCities);
		Map<PmState, Map<PmDivision, Map<PmDistrict, List<PmCity>>>> mapStatesDivisionsDistrictsCities = getUniqueStatesDivisionsDistrictsCities(
				listDistricts, listCities);
		Map<PmState, List<PmDivision>> mapStatesDivision = getUniqueStatesDivision(listDivisions);

		TreeNode zoneNode = null;
		if (zone != null) {
			// LocationDTO zoneLocation = new LocationDTO(zone);

			// Build zone - state
			zoneNode = new DefaultTreeNode("zone", zone.getZoneName(),
					rootLocationTree);
		}

		// LocationDTO zoneLocation = new LocationDTO(zone);
		// Build zone - state
		// TreeNode zoneNode = new DefaultTreeNode("zone",zoneLocation,
		// rootLocationTree);
		if (listStates != null && listStates.size() > 0) {
			for (PmState state : listStates) {
				// LocationDTO stateLocation = new LocationDTO(state);
				ScQtLocCriteria stateLocation = new ScQtLocCriteria();
				stateLocation.setPmState(state);
				TreeNode stateNode = null;
				if (zoneNode != null) {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							stateLocation, zoneNode);
				} else {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							stateLocation, rootLocationTree);
				}

				// TreeNode stateNode = new
				// DefaultTreeNode(QTConstants.NODE_STATE,stateLocation,
				// zoneNode);
			}
		}
		Set<PmState> statesDivisionKeySet = mapStatesDivision.keySet();
		if (statesDivisionKeySet != null && !statesDivisionKeySet.isEmpty()) {
			for (PmState state : statesDivisionKeySet) {
				// LocationDTO stateLocation = new LocationDTO(state);
				ScQtLocCriteria stateLocation = new ScQtLocCriteria();
				stateLocation.setPmState(state);
				TreeNode stateNode = null;
				if (zoneNode != null) {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							stateLocation, zoneNode);
				} else {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							stateLocation, rootLocationTree);
				}

				// new DefaultTreeNode(QTConstants.NODE_STATE,stateLocation,
				// zoneNode);
				List<PmDivision> divisions = mapStatesDivision.get(state);
				if (divisions != null && !divisions.isEmpty()) {
					for (PmDivision division : divisions) {
						// LocationDTO divisionLocation = new
						// LocationDTO(division);
						ScQtLocCriteria divisionLocation = new ScQtLocCriteria();
						divisionLocation.setPmDivision(division);
						TreeNode divisionNode = new DefaultTreeNode(
								QTConstants.NODE_DIVISION, divisionLocation,
								stateNode);
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
				ScQtLocCriteria stateLocation = new ScQtLocCriteria();
				stateLocation.setPmState(state);
				TreeNode stateNode = null;
				if (zoneNode != null) {
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
						ScQtLocCriteria districtLocation = new ScQtLocCriteria();
						districtLocation.setPmDistrict(district);
						TreeNode districtNode = new DefaultTreeNode(
								QTConstants.NODE_DISTRICT, districtLocation,
								stateNode);
						List<PmCity> cities = mapDistrictsCities.get(district);
						if (cities != null && !cities.isEmpty()) {
							for (PmCity city : cities) {
								LocationDTO cityLocation = new LocationDTO(city);
								TreeNode cityNode = new DefaultTreeNode("city",
										cityLocation, districtNode);
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
				ScQtLocCriteria stateLocation = new ScQtLocCriteria();
				stateLocation.setPmState(state);
				TreeNode stateNode = null;
				if (zoneNode != null) {
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
						ScQtLocCriteria divisionLocation = new ScQtLocCriteria();
						divisionLocation.setPmDivision(division);
						TreeNode divisionNode = new DefaultTreeNode(
								QTConstants.NODE_DIVISION, divisionLocation,
								stateNode);
						Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapDivisionsDistrictsCities
								.get(division);
						Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
								.keySet();
						if (districtsCitiesKeySet != null
								&& !districtsCitiesKeySet.isEmpty()) {
							for (PmDistrict district : districtsCitiesKeySet) {
								// LocationDTO districtLocation = new
								// LocationDTO(district);
								ScQtLocCriteria districtLocation = new ScQtLocCriteria();
								districtLocation.setPmDistrict(district);
								TreeNode districtNode = new DefaultTreeNode(
										QTConstants.NODE_DISTRICT,
										districtLocation, divisionNode);
								List<PmCity> cities = mapDistrictsCities
										.get(district);
								if (cities != null && !cities.isEmpty()) {
									for (PmCity city : cities) {
										LocationDTO cityLocation = new LocationDTO(
												city);
										TreeNode cityNode = new DefaultTreeNode(
												QTConstants.NODE_CITY,
												cityLocation, districtNode);
									}
								}
							}
						}
					}
				}
			}
		}
		return rootLocationTree;
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
	@Deprecated
	public TreeNode buildGeneralZoneTree(TplSpecZoneDtl zone,
			List<PmState> listStates, List<PmDistrict> listDistricts,
			List<PmDivision> listDivisions, List<PmCity> listCities) {
		TreeNode rootLocationTree = new DefaultTreeNode(QTConstants.NODE_ROOT,
				null);

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
				TplSpecZoneLoc stateLocation = new TplSpecZoneLoc();
				stateLocation.setPmState(state);
				TreeNode stateNode = null;
				if (zoneNode != null) {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							stateLocation, zoneNode);
				} else {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							stateLocation, rootLocationTree);
				}

				// TreeNode stateNode = new
				// DefaultTreeNode(QTConstants.NODE_STATE,stateLocation,
				// zoneNode);
			}
		}
		Set<PmState> statesDivisionKeySet = mapStatesDivision.keySet();
		if (statesDivisionKeySet != null && !statesDivisionKeySet.isEmpty()) {
			for (PmState state : statesDivisionKeySet) {
				// LocationDTO stateLocation = new LocationDTO(state);
				TplSpecZoneLoc stateLocation = new TplSpecZoneLoc();
				stateLocation.setPmState(state);
				TreeNode stateNode = null;
				if (zoneNode != null) {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							stateLocation, zoneNode);
				} else {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							stateLocation, rootLocationTree);
				}

				// new DefaultTreeNode(QTConstants.NODE_STATE,stateLocation,
				// zoneNode);
				List<PmDivision> divisions = mapStatesDivision.get(state);
				if (divisions != null && !divisions.isEmpty()) {
					for (PmDivision division : divisions) {
						// LocationDTO divisionLocation = new
						// LocationDTO(division);
						TplSpecZoneLoc divisionLocation = new TplSpecZoneLoc();
						divisionLocation.setPmDivision(division);
						TreeNode divisionNode = new DefaultTreeNode(
								QTConstants.NODE_DIVISION, divisionLocation,
								stateNode);
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
				TplSpecZoneLoc stateLocation = new TplSpecZoneLoc();
				stateLocation.setPmState(state);
				TreeNode stateNode = null;
				if (zoneNode != null) {
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
						TplSpecZoneLoc districtLocation = new TplSpecZoneLoc();
						districtLocation.setPmDistrict(district);
						TreeNode districtNode = new DefaultTreeNode(
								QTConstants.NODE_DISTRICT, districtLocation,
								stateNode);
						List<PmCity> cities = mapDistrictsCities.get(district);
						if (cities != null && !cities.isEmpty()) {
							for (PmCity city : cities) {
								LocationDTO cityLocation = new LocationDTO(city);
								TreeNode cityNode = new DefaultTreeNode("city",
										cityLocation, districtNode);
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
				TplSpecZoneLoc stateLocation = new TplSpecZoneLoc();
				stateLocation.setPmState(state);
				TreeNode stateNode = null;
				if (zoneNode != null) {
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
						TplSpecZoneLoc divisionLocation = new TplSpecZoneLoc();
						divisionLocation.setPmDivision(division);
						TreeNode divisionNode = new DefaultTreeNode(
								QTConstants.NODE_DIVISION, divisionLocation,
								stateNode);
						Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapDivisionsDistrictsCities
								.get(division);
						Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
								.keySet();
						if (districtsCitiesKeySet != null
								&& !districtsCitiesKeySet.isEmpty()) {
							for (PmDistrict district : districtsCitiesKeySet) {
								// LocationDTO districtLocation = new
								// LocationDTO(district);
								TplSpecZoneLoc districtLocation = new TplSpecZoneLoc();
								districtLocation.setPmDistrict(district);
								TreeNode districtNode = new DefaultTreeNode(
										QTConstants.NODE_DISTRICT,
										districtLocation, divisionNode);
								List<PmCity> cities = mapDistrictsCities
										.get(district);
								if (cities != null && !cities.isEmpty()) {
									for (PmCity city : cities) {
										LocationDTO cityLocation = new LocationDTO(
												city);
										TreeNode cityNode = new DefaultTreeNode(
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
		return rootLocationTree;
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
	public TreeNode buildCircularZoneTree(CtPanelZone zone,
			List<PmState> listStates, List<PmDistrict> listDistricts,
			List<PmDivision> listDivisions, List<PmCity> listCities) {
		TreeNode rootLocationTree = new DefaultTreeNode(QTConstants.NODE_ROOT,
				null);

		Map<PmState, Map<PmDistrict, List<PmCity>>> mapStatesDistrictsCities = getUniqueStateDistrictsCities(
				listDistricts, listCities);
		Map<PmState, Map<PmDivision, Map<PmDistrict, List<PmCity>>>> mapStatesDivisionsDistrictsCities = getUniqueStatesDivisionsDistrictsCities(
				listDistricts, listCities);
		Map<PmState, List<PmDivision>> mapStatesDivision = getUniqueStatesDivision(listDivisions);

		TreeNode zoneNode = null;
		if (zone != null) {
			// LocationDTO zoneLocation = new LocationDTO(zone);

			// Build zone - state
			zoneNode = new DefaultTreeNode(QTConstants.NODE_ZONE, zone,
					rootLocationTree);
		}

		// LocationDTO zoneLocation = new LocationDTO(zone);
		// Build zone - state
		// TreeNode zoneNode = new DefaultTreeNode("zone",zoneLocation,
		// rootLocationTree);
		if (listStates != null && listStates.size() > 0) {
			for (PmState state : listStates) {
				// LocationDTO stateLocation = new LocationDTO(state);
				CtPanelZoneDtl stateLocation = new CtPanelZoneDtl();
				stateLocation.setStateId(state);
				TreeNode stateNode = null;
				if (zoneNode != null) {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							stateLocation, zoneNode);
				} else {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							stateLocation, rootLocationTree);
				}

				// TreeNode stateNode = new
				// DefaultTreeNode(QTConstants.NODE_STATE,stateLocation,
				// zoneNode);
			}
		}
		Set<PmState> statesDivisionKeySet = mapStatesDivision.keySet();
		if (statesDivisionKeySet != null && !statesDivisionKeySet.isEmpty()) {
			for (PmState state : statesDivisionKeySet) {
				// LocationDTO stateLocation = new LocationDTO(state);
				CtPanelZoneDtl stateLocation = new CtPanelZoneDtl();
				stateLocation.setStateId(state);
				TreeNode stateNode = null;
				if (zoneNode != null) {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							stateLocation, zoneNode);
				} else {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							stateLocation, rootLocationTree);
				}

				// new DefaultTreeNode(QTConstants.NODE_STATE,stateLocation,
				// zoneNode);
				List<PmDivision> divisions = mapStatesDivision.get(state);
				if (divisions != null && !divisions.isEmpty()) {
					for (PmDivision division : divisions) {
						// LocationDTO divisionLocation = new
						// LocationDTO(division);
						// TplSpecZoneLoc divisionLocation= new
						// TplSpecZoneLoc();
						// divisionLocation.setPmDivision(division);
						// TreeNode divisionNode = new
						// DefaultTreeNode(QTConstants.NODE_DIVISION,divisionLocation,
						// stateNode);
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
				CtPanelZoneDtl stateLocation = new CtPanelZoneDtl();
				stateLocation.setStateId(state);
				TreeNode stateNode = null;
				if (zoneNode != null) {
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
						CtPanelZoneDtl districtLocation = new CtPanelZoneDtl();
						districtLocation.setDistrictId(district);
						TreeNode districtNode = new DefaultTreeNode(
								QTConstants.NODE_DISTRICT, districtLocation,
								stateNode);
						List<PmCity> cities = mapDistrictsCities.get(district);
						if (cities != null && !cities.isEmpty()) {
							for (PmCity city : cities) {
								// LocationDTO cityLocation = new
								// LocationDTO(city);
								// TreeNode cityNode = new
								// DefaultTreeNode("city",cityLocation,
								// districtNode);
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
				CtPanelZoneDtl stateLocation = new CtPanelZoneDtl();
				stateLocation.setStateId(state);
				TreeNode stateNode = null;
				if (zoneNode != null) {
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
						// TplSpecZoneLoc divisionLocation= new
						// TplSpecZoneLoc();
						// divisionLocation.setPmDivision(division);
						// TreeNode divisionNode = new
						// DefaultTreeNode(QTConstants.NODE_DIVISION,divisionLocation,
						// stateNode);
						Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapDivisionsDistrictsCities
								.get(division);
						Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
								.keySet();
						if (districtsCitiesKeySet != null
								&& !districtsCitiesKeySet.isEmpty()) {
							for (PmDistrict district : districtsCitiesKeySet) {
								// LocationDTO districtLocation = new
								// LocationDTO(district);
								CtPanelZoneDtl districtLocation = new CtPanelZoneDtl();
								districtLocation.setDistrictId(district);
								TreeNode districtNode = new DefaultTreeNode(
										QTConstants.NODE_DISTRICT,
										districtLocation, stateNode);
								List<PmCity> cities = mapDistrictsCities
										.get(district);
								if (cities != null && !cities.isEmpty()) {
									for (PmCity city : cities) {
										LocationDTO cityLocation = new LocationDTO(
												city);
										// TreeNode cityNode = new
										// DefaultTreeNode("city",cityLocation,
										// districtNode);
									}
								}
							}
						}
					}
				}
			}
		}
		return rootLocationTree;
	}

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

	public TreeNode constuctTreeNode(TplSpecZoneDtl tplSpecZoneDtl,
			List<TplSpecZoneLoc> specLocs) {
		TreeNode root = new DefaultTreeNode(QTConstants.NODE_ROOT, null);
		if (tplSpecZoneDtl != null) {
			TreeNode zoneNode = new DefaultTreeNode(QTConstants.NODE_ZONE,
					tplSpecZoneDtl, root);
			List<PmState> states = new ArrayList<PmState>();
			List<PmDivision> divisions = new ArrayList<PmDivision>();
			List<PmDistrict> districts = new ArrayList<PmDistrict>();
			TreeNode stateNode = null;
			TreeNode divisonNode = null;
			// List<TreeNode> locations = new ArrayList<TreeNode>();
			for (TplSpecLoc locCriteria : specLocs) {
				PmState pmState = locCriteria.getPmState();

				if (null != pmState && !states.contains(pmState)) {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							locCriteria, zoneNode);
					// locations.add(root);
				}
				PmDivision division = locCriteria.getPmDivision();

				if (null != division && !divisions.contains(pmState)) {
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
		} else {

		}
		return root;
	}
	public TreeNode constuctContractTreeNode(CtContractZone zone) {
		TreeNode root = new DefaultTreeNode(QTConstants.NODE_ROOT, null);
		if (zone != null) {
			TreeNode zoneNode = new DefaultTreeNode(QTConstants.NODE_ZONE,
					zone, root);
			List<PmState> states = new ArrayList<PmState>();
			List<PmDivision> divisions = new ArrayList<PmDivision>();
			List<PmDistrict> districts = new ArrayList<PmDistrict>();
			TreeNode stateNode = null;
			TreeNode divisonNode = null;
			// List<TreeNode> locations = new ArrayList<TreeNode>();
			 for (CtContractZoneDtl locCriteria : zone.getCtContractZoneDtls()) {
				PmState pmState = locCriteria.getState();

				if (null != pmState && !states.contains(pmState)) {
					stateNode = new DefaultTreeNode(QTConstants.NODE_STATE,
							locCriteria, zoneNode);
					// locations.add(root);
				}
				PmDivision division = locCriteria.getDivision();

				if (null != division && !divisions.contains(pmState)) {
					divisonNode = new DefaultTreeNode(
							QTConstants.NODE_DIVISION, locCriteria, stateNode);
					divisions.add(division);
				}
				PmDistrict pmDistrict = locCriteria.getDistrict();

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
		} else {

		}
		return root;
	}
}
