/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.circular.panel;

import java.util.ArrayList;
import java.util.List;

import my.ep.persistence.entity.ct.circular.panel.CtPanelZoneDtl;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;

public class PanelLocationHelper {
	
	public static <LOCATION_CLASS> List<PmState> getStateFromLocationDTO(List<CtPanelZoneDtl> locationDTOs) {
		List<PmState> results =	new ArrayList<PmState>();
		for(CtPanelZoneDtl locationDTO: locationDTOs){
			if(locationDTO.getStateId() != null){
				results.add(locationDTO.getStateId());
			}
		}
		return results;
	}
	
	public static <LOCATION_CLASS> List<PmDistrict> getDistrictFromLocationDTO(List<CtPanelZoneDtl> locationDTOs){
		List<PmDistrict>	results	=	new ArrayList<PmDistrict>();
		for(CtPanelZoneDtl locationDTO: locationDTOs){
			if(locationDTO.getDistrictId() != null){
				results.add(locationDTO.getDistrictId());
			}
		}
		return results;
	}
	
	public static <LOCATION_CLASS> List<PmDivision> getDivisionFromLocationDTO(
			List<CtPanelZoneDtl> locationDTOs) {
		List<PmDivision> results = new ArrayList<PmDivision>();
		for (CtPanelZoneDtl locationDTO : locationDTOs) {
			if (locationDTO.getDivisionId() != null) {
				results.add(locationDTO.getDivisionId());
			}
		}
		return results;
	}
	
}
