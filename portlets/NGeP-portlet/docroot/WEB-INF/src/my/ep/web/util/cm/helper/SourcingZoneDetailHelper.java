package my.ep.web.util.cm.helper;

import java.util.ArrayList;
import java.util.List;

import my.ep.persistence.entity.cm.catalog.CmSupplierZone;
import my.ep.persistence.entity.cm.catalog.CmSupplierZoneDTL;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;

public class SourcingZoneDetailHelper {
	public	static	List<PmState>	getStates(CmSupplierZone zone){
		List<PmState>	results	=	new ArrayList<>();
		for(CmSupplierZoneDTL detail: zone.getCmSupplierZoneDtls()){
			if(detail.getPmState() != null){
				results.add(detail.getPmState());
			}
		}
		return results;
	}
	public	static	List<PmState>	getStateFromDetailDTOs(List<CmSupplierZoneStateDistrictDTO> detailDTOs){
		List<PmState>	results	=	new ArrayList<PmState>();
		for(CmSupplierZoneStateDistrictDTO detailDTO: detailDTOs){
			if(detailDTO.getState() != null){
				results.add(detailDTO.getState());
			}
		}
		return results;
	}
	public	static	List<PmDistrict>	getDistricts(CmSupplierZone zone){
		List<PmDistrict>	results	=	new ArrayList<PmDistrict>();
		for(CmSupplierZoneDTL detail: zone.getCmSupplierZoneDtls()){
			if(detail.getPmDistrict() != null){
				results.add(detail.getPmDistrict());
			}
		}
		return results;
	}
	public	static	List<PmDistrict>	getDistrictFromDetailDTOs(List<CmSupplierZoneStateDistrictDTO> detailDTOs){
		List<PmDistrict>	results	=	new ArrayList<PmDistrict>();
		for(CmSupplierZoneStateDistrictDTO detailDTO: detailDTOs){
			if(detailDTO.getDistrict() != null){
				results.add(detailDTO.getDistrict());
			}
		}
		return results;
	}
	public	static	List<PmDivision>	getDivisions(CmSupplierZone zone){
		List<PmDivision>	results	=	new ArrayList<PmDivision>();
		for(CmSupplierZoneDTL detail: zone.getCmSupplierZoneDtls()){
			if(detail.getPmDivision() != null){
				results.add(detail.getPmDivision());
			}
		}
		return results;
	}
	public	static	List<PmDivision>	getDivisionFromDetailDTOs(List<CmSupplierZoneStateDistrictDTO> detailDTOs){
		List<PmDivision>	results	=	new ArrayList<PmDivision>();
		for(CmSupplierZoneStateDistrictDTO detailDTO: detailDTOs){
			if(detailDTO.getDivision() != null){
				results.add(detailDTO.getDivision());
			}
		}
		return results;
	}
	public	static	CmSupplierZone	getZoneFromDetailDTOs(List<CmSupplierZoneStateDistrictDTO> detailDTOs){
		CmSupplierZone result	=	null;
		for(CmSupplierZoneStateDistrictDTO detailDTO: detailDTOs){
			if(detailDTO.getCmSupplierZone() != null){
				result = detailDTO.getCmSupplierZone();
				break;
			}
		}
		return result;
	}
	
}
