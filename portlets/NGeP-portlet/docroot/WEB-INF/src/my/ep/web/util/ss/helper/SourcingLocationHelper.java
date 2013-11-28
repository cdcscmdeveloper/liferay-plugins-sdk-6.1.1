package my.ep.web.util.ss.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;

public class SourcingLocationHelper {
	public	List<PmState>	restructureLocation(List<PmState> pmStates, List<PmDivision> pmDivisions, List<PmDistrict> pmDistrict, List<PmCity> pmCities){
		List<PmState>	results	=	new ArrayList<PmState>();
		//TODO
		return results;
	}

	private	Map<PmDistrict,List<PmCity>> getUniqueMapCity(List<PmCity> cities){
		Map<PmDistrict, List<PmCity>>	results	=	new HashMap<PmDistrict, List<PmCity>> ();
		//TODO
		return results;
	}
}
