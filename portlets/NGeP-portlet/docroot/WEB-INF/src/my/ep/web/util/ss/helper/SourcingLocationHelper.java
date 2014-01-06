package my.ep.web.util.ss.helper;

import java.util.ArrayList;
import java.util.List;

import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.ss.directpurchase.ScQuoteZone;
import my.ep.persistence.entity.ss.directpurchase.ScQuoteZoneLocation;
import my.ep.web.backingbean.ss.directpurchase.LocationDTO;

public class SourcingLocationHelper {
	public	static	List<PmState>	getStates(ScQuoteZone zone){
		List<PmState>	results	=	new ArrayList<>();
		for(ScQuoteZoneLocation location: zone.getScQBZoneLocations()){
			if(location.getPmState() != null){
				results.add(location.getPmState());
			}
		}
		return results;
	}
	public	static	List<PmState>	getStateFromLocationDTOs(List<LocationDTO> locationDTOs){
		List<PmState>	results	=	new ArrayList<>();
		for(LocationDTO locationDTO: locationDTOs){
			if(locationDTO.getState() != null){
				results.add(locationDTO.getState());
			}
		}
		return results;
	}
	public	static	List<PmDistrict>	getDistricts(ScQuoteZone zone){
		List<PmDistrict>	results	=	new ArrayList<PmDistrict>();
		for(ScQuoteZoneLocation location: zone.getScQBZoneLocations()){
			if(location.getPmDistrict() != null){
				results.add(location.getPmDistrict());
			}
		}
		return results;
	}
	public	static	List<PmDistrict>	getDistrictFromLocationDTOs(List<LocationDTO> locationDTOs){
		List<PmDistrict>	results	=	new ArrayList<PmDistrict>();
		for(LocationDTO locationDTO: locationDTOs){
			if(locationDTO.getDistrict() != null){
				results.add(locationDTO.getDistrict());
			}
		}
		return results;
	}
	public	static	List<PmDivision>	getDivisions(ScQuoteZone zone){
		List<PmDivision>	results	=	new ArrayList<PmDivision>();
		for(ScQuoteZoneLocation location: zone.getScQBZoneLocations()){
			if(location.getDivision() != null){
				results.add(location.getDivision());
			}
		}
		return results;
	}
	public	static	List<PmDivision>	getDivisionFromLocationDTOs(List<LocationDTO> locationDTOs){
		List<PmDivision>	results	=	new ArrayList<PmDivision>();
		for(LocationDTO locationDTO: locationDTOs){
			if(locationDTO.getDivision() != null){
				results.add(locationDTO.getDivision());
			}
		}
		return results;
	}
	public	static	ScQuoteZone	getZoneFromLocationDTOs(List<LocationDTO> locationDTOs){
		ScQuoteZone result	=	null;
		for(LocationDTO locationDTO: locationDTOs){
			if(locationDTO.getZone() != null){
				result = locationDTO.getZone();
				break;
			}
		}
		return result;
	}
	public	static	List<PmCity>	getCities(ScQuoteZone zone){
		List<PmCity>	results	=	new ArrayList<PmCity>();
		for(ScQuoteZoneLocation location: zone.getScQBZoneLocations()){
			if(location.getCity() != null){
				results.add(location.getCity());
			}
		}
		return results;
	}
	public	static	List<PmCity>	getCityFromLocationDTOs(List<LocationDTO> locationDTOs){
		List<PmCity>	results	=	new ArrayList<PmCity>();
		for(LocationDTO locationDTO: locationDTOs){
			if(locationDTO.getCity() != null){
				results.add(locationDTO.getCity());
			}
		}
		return results;
	}
	
}
