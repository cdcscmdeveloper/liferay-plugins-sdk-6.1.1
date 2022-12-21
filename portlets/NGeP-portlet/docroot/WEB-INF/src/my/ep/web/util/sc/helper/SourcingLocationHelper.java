/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.util.sc.helper;

import java.util.ArrayList;
import java.util.List;

import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZoneDtl;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;

/**
 * 
 * SourcingLocationHelper class is used to construct Locations
 * 
 * @author VidyaSagarReddy
 * @since 22.11.2013
 * 
 */
public class SourcingLocationHelper {

	/**
	 * To get States for LocationDTo object
	 * 
	 * @param locationDTOs
	 * @return List<PmState> states
	 */
	public static <LOCATION_CLASS extends TplSpecLoc> List<PmState> getStateFromLocationDTO(
			List<LOCATION_CLASS> locationDTOs) {
		List<PmState> results = new ArrayList<PmState>();
		for (LOCATION_CLASS locationDTO : locationDTOs) {
			if (locationDTO.getPmState() != null) {

				results.add(locationDTO.getPmState());
				
				updateSelectableForZonal(locationDTO, locationDTO.getPmState());
			}
		}
		return results;
	}
	
	private static <LOCATION_CLASS extends TplSpecLoc> void updateSelectableForZonal(LOCATION_CLASS tplSpecLoc,
			BaseEntity location) {
		if (tplSpecLoc instanceof TplSpecZoneLoc
				&& ((TplSpecZoneLoc)tplSpecLoc).isZonalLocation()) {
			location.setSelectable(false);
		}
	}

	/**
	 * To get States for LocationDTo object
	 * 
	 * @param locationDTOs
	 * @return List<PmState> states
	 */
	public static List<PmState> getStateFromCtPanelZone(
			List<CtPanelZoneDtl> locationDTOs) {
		List<PmState> results = new ArrayList<>();
		for (CtPanelZoneDtl locationDTO : locationDTOs) {
			if (locationDTO.getStateId() != null) {
				results.add(locationDTO.getStateId());
			}
		}
		return results;
	}

	/**
	 * To get District s for LocationDTo object
	 * 
	 * @param locationDTOs
	 * @return List<PmDistrict> districts
	 */
	public static List<PmDistrict> getDistrictFromCtPanelZone(
			List<CtPanelZoneDtl> locationDTOs) {
		List<PmDistrict> results = new ArrayList<>();
		for (CtPanelZoneDtl locationDTO : locationDTOs) {
			if (locationDTO.getDistrictId() != null) {
				results.add(locationDTO.getDistrictId());
			}
		}
		return results;
	}

	/**
	 * To get District s for LocationDTo object
	 * 
	 * @param locationDTOs
	 * @return List<PmDistrict> districts
	 */
	public static <LOCATION_CLASS extends TplSpecLoc> List<PmDistrict> getDistrictFromLocationDTO(
			List<LOCATION_CLASS> locationDTOs) {
		List<PmDistrict> results = new ArrayList<PmDistrict>();
		for (LOCATION_CLASS locationDTO : locationDTOs) {
			if (locationDTO.getPmDistrict() != null) {
				results.add(locationDTO.getPmDistrict());
				
				updateSelectableForZonal(locationDTO, locationDTO.getPmDistrict());
			}
		}
		return results;
	}

	/**
	 * To get Division s for LocationDTo object
	 * 
	 * @param locationDTOs
	 * @return List<PmDivision> divisions
	 */
	public static <LOCATION_CLASS extends TplSpecLoc> List<PmDivision> getDivisionFromLocationDTO(
			List<LOCATION_CLASS> locationDTOs) {
		List<PmDivision> results = new ArrayList<PmDivision>();
		for (LOCATION_CLASS locationDTO : locationDTOs) {
			if (locationDTO.getPmDivision() != null) {
				results.add(locationDTO.getPmDivision());
				
				updateSelectableForZonal(locationDTO, locationDTO.getPmDivision());
			}
		}
		return results;
	}

	/**
	 * To get City s for LocationDTo object
	 * 
	 * @param locationDTOs
	 * @return List<PmCity> cities
	 */

	public static <LOCATION_CLASS extends TplSpecLoc> List<PmCity> getCityFromLocationDTO(
			List<LOCATION_CLASS> locationDTOs) {
		List<PmCity> results = new ArrayList<PmCity>();

		return results;
	}

	/**
	 * To get States s for LocationDTo object
	 * 
	 * @param locationDTOs
	 * @return List<PmState> states
	 */
	public static List<PmState> getZoneStates(List<TplSpecZoneLoc> locationDTOs) {
		List<PmState> results = new ArrayList<>();
		for (TplSpecLoc locationDTO : locationDTOs) {
			if (locationDTO.getPmState() != null) {
				results.add(locationDTO.getPmState());
			}
		}
		return results;
	}

	/**
	 * To get States s for LocationDTo object
	 * 
	 * @param locationDTOs
	 * @return List<PmState> states
	 */
	public static List<PmState> retriveStateList(List<PmState> states) {
		List<PmState> results = new ArrayList<PmState>();
		for (PmState locationDTO : states) {
			if (locationDTO.getStateId() != null) {

				results.add(locationDTO);
			}
		}
		return results;
	}

	/**
	 * To get Division s for LocationDTo object
	 * 
	 * @param locationDTOs
	 * @return List<PmDivision> divisions
	 */
	public static List<PmDivision> retriveDivisionList(List<PmDivision> division) {
		List<PmDivision> results = new ArrayList<PmDivision>();
		for (PmDivision locationDTO : division) {
			if (locationDTO.getDivisionId() != null) {

				results.add(locationDTO);
			}
		}
		return results;
	}

	/**
	 * To get District s for LocationDTo object
	 * 
	 * @param locationDTOs
	 * @return List<PmDistrict> districts
	 */
	public static List<PmDistrict> retriveDistrictList(List<PmDistrict> district) {
		List<PmDistrict> results = new ArrayList<PmDistrict>();
		for (PmDistrict locationDTO : district) {
			if (locationDTO.getDistrictId() != null) {

				results.add(locationDTO);
			}
		}
		return results;
	}

}
