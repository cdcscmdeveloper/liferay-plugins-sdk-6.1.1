package my.ep.web.util.sc.helper;

import java.util.ArrayList;
import java.util.List;

import my.ep.persistence.entity.ct.circular.panel.CtPanelZoneDtl;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteZone;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteZoneLocation;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.web.backingbean.sc.directpurchase.LocationDTO;

public class SourcingLocationHelper {
	public	static	List<PmState>	getStates(ScQuoteZone zone){
		List<PmState>	results	=	new ArrayList<>();
		for(ScQuoteZoneLocation location: zone.getZoneLocations()){
			if(location.getPmState() != null){
				results.add(location.getPmState());
			}
		}
		return results;
	}
	
	public static <LOCATION_CLASS extends TplSpecLoc> List<PmState>	getStateFromLocationDTO(List<LOCATION_CLASS> locationDTOs) {
		List<PmState>	results	=	new ArrayList<PmState>();
		for(LOCATION_CLASS locationDTO: locationDTOs){
			if(locationDTO.getPmState() != null){
				
				results.add(locationDTO.getPmState());
			}
		}
		return results;
	}
	
	@Deprecated
	public	static	List<PmState>	getStateFromLocationDTOs(List<ScQtLocCriteria> locationDTOs){
		List<PmState>	results	=	new ArrayList<>();
		for(TplSpecLoc locationDTO: locationDTOs){
			if(locationDTO.getPmState() != null){
				
				results.add(locationDTO.getPmState());
			}
		}
		return results;
	}
	
	@Deprecated
	public	static	List<PmState>	getStateFromZoneLocationDTOs(List<TplSpecZoneLoc> locationDTOs){
		List<PmState>	results	=	new ArrayList<>();
		for(TplSpecLoc locationDTO: locationDTOs){
			if(locationDTO.getPmState() != null){
				results.add(locationDTO.getPmState());
			}
		}
		return results;
	}
	
	
	public	static	List<PmState>	getStateFromCtPanelZone(List<CtPanelZoneDtl> locationDTOs){
		List<PmState>	results	=	new ArrayList<>();
		for(CtPanelZoneDtl locationDTO: locationDTOs){
			if(locationDTO.getStateId() != null){
				results.add(locationDTO.getStateId());
			}
		}
		return results;
	}
	
	
	public	static	List<PmDistrict>	getDistrictFromCtPanelZone(List<CtPanelZoneDtl> locationDTOs){
		List<PmDistrict>	results	=	new ArrayList<>();
		for(CtPanelZoneDtl locationDTO: locationDTOs){
			if(locationDTO.getDistrictId() != null){
				results.add(locationDTO.getDistrictId());
			}
		}
		return results;
	}
	
	public	static	List<PmDistrict>	getDistricts(ScQuoteZone zone){
		List<PmDistrict>	results	=	new ArrayList<PmDistrict>();
		for(ScQuoteZoneLocation location: zone.getZoneLocations()){
			if(location.getPmDistrict() != null){
				results.add(location.getPmDistrict());
			}
		}
		return results;
	}
	
	public static <LOCATION_CLASS extends TplSpecLoc> List<PmDistrict> getDistrictFromLocationDTO(List<LOCATION_CLASS> locationDTOs){
		List<PmDistrict>	results	=	new ArrayList<PmDistrict>();
		for(LOCATION_CLASS locationDTO: locationDTOs){
			if(locationDTO.getPmDistrict() != null){
				results.add(locationDTO.getPmDistrict());
			}
		}
		return results;
	}
	
	@Deprecated
	public	static	List<PmDistrict>	getDistrictFromLocationDTOs(List<ScQtLocCriteria> locationDTOs){
		List<PmDistrict>	results	=	new ArrayList<PmDistrict>();
		for(TplSpecLoc locationDTO: locationDTOs){
			if(locationDTO.getPmDistrict() != null){
				results.add(locationDTO.getPmDistrict());
			}
		}
		return results;
	}
	
	@Deprecated
	public	static	List<PmDistrict>	getDistrictFromZoneLocationDTOs(List<TplSpecZoneLoc> locationDTOs){
		List<PmDistrict>	results	=	new ArrayList<PmDistrict>();
		for(TplSpecLoc locationDTO: locationDTOs){
			if(locationDTO.getPmDistrict() != null){
				results.add(locationDTO.getPmDistrict());
			}
		}
		return results;
	}
	public	static	List<PmDivision>	getDivisions(ScQuoteZone zone){
		List<PmDivision>	results	=	new ArrayList<PmDivision>();
		for(ScQuoteZoneLocation location: zone.getZoneLocations()){
			if(location.getDivision() != null){
				results.add(location.getDivision());
			}
		}
		return results;
	}
	
	public static <LOCATION_CLASS extends TplSpecLoc> List<PmDivision> getDivisionFromLocationDTO(List<LOCATION_CLASS> locationDTOs){
		List<PmDivision>	results	=	new ArrayList<PmDivision>();
		for(LOCATION_CLASS locationDTO: locationDTOs){
			if(locationDTO.getPmDivision() != null){
				results.add(locationDTO.getPmDivision());
			}
		}
		return results;
	}
	
	@Deprecated
	public	static	List<PmDivision>	getDivisionFromLocationDTOs(List<ScQtLocCriteria> locationDTOs){
		List<PmDivision>	results	=	new ArrayList<PmDivision>();
		for(TplSpecLoc locationDTO: locationDTOs){
			if(locationDTO.getPmDivision() != null){
				results.add(locationDTO.getPmDivision());
			}
		}
		return results;
	}
	
	@Deprecated
	public	static	List<PmDivision>	getDivisionFromZoneLocationDTOs(List<TplSpecZoneLoc> locationDTOs){
		List<PmDivision>	results	=	new ArrayList<PmDivision>();
		for(TplSpecLoc locationDTO: locationDTOs){
			if(locationDTO.getPmDivision() != null){
				results.add(locationDTO.getPmDivision());
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
	
	public static <LOCATION_CLASS extends TplSpecLoc> List<PmCity> getCityFromLocationDTO(List<LOCATION_CLASS> locationDTOs){
		List<PmCity>	results	=	new ArrayList<PmCity>();
		
			
		return results;
	}
	
	@Deprecated
	public	static	List<PmCity>	getCityFromLocationDTOs(List<ScQtLocCriteria> locationDTOs){
		List<PmCity>	results	=	new ArrayList<PmCity>();
		
			
		return results;
	}
	
	@Deprecated
	public	static	List<PmCity>	getCityFromZoneLocationDTOs(List<TplSpecZoneLoc> locationDTOs){
		List<PmCity>	results	=	new ArrayList<PmCity>();
		
			
		return results;
	}
	
	public	static	List<PmState>	getZoneStates(List<TplSpecZoneLoc> locationDTOs){
		List<PmState>	results	=	new ArrayList<>();
		for(TplSpecLoc locationDTO: locationDTOs){
			if(locationDTO.getPmState() != null){
				results.add(locationDTO.getPmState());
			}
		}
		return results;
	}
	
	
	public	static	List<PmState>	retriveStateList(List<PmState> states){
		List<PmState>	results	=	new ArrayList<PmState>();
		for(PmState locationDTO: states){
			if(locationDTO.getStateId() != null){
			
				results.add(locationDTO);
			}
		}
		return results;
	}
	
	public	static	List<PmDivision>	retriveDivisionList(List<PmDivision> division){
		List<PmDivision>	results	=	new ArrayList<PmDivision>();
		for(PmDivision locationDTO: division){
			if(locationDTO.getDivisionId() != null){
			
				results.add(locationDTO);
			}
		}
		return results;
	}
	
	public	static	List<PmDistrict>	retriveDistrictList(List<PmDistrict> district){
		List<PmDistrict>	results	=	new ArrayList<PmDistrict>();
		for(PmDistrict locationDTO: district){
			if(locationDTO.getDistrictId() != null){
			
				results.add(locationDTO);
			}
		}
		return results;
	}
	
}
