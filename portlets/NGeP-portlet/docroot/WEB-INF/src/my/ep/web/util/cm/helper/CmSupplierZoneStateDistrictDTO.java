package my.ep.web.util.cm.helper;

import java.io.Serializable;

import my.ep.persistence.entity.cm.catalog.CmSupplierZone;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteZone;

public class CmSupplierZoneStateDistrictDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8954854049553749821L;
	private CmSupplierZone cmSupplierZone;
	private	PmState	state;
	private	PmDistrict	district;
	private	PmDivision division;
	private	String	locationName;
	private	int rowKey;
	
	public CmSupplierZoneStateDistrictDTO() {
		super();
	}
	
	public CmSupplierZoneStateDistrictDTO(CmSupplierZone cmSupplierZone){
		super();
		this.cmSupplierZone = cmSupplierZone;
	}
	
	public CmSupplierZoneStateDistrictDTO(PmState state) {
		super();
		this.state = state;
	}
	
	public CmSupplierZoneStateDistrictDTO(PmDistrict district) {
		super();
		this.district = district;
	}
	
	public CmSupplierZoneStateDistrictDTO(PmDivision division) {
		super();
		this.division = division;
	}
	
	public String getLocationName() {
		if(locationName == null || "".equalsIgnoreCase(locationName)){
			if(cmSupplierZone != null){
				return cmSupplierZone.getSuppZoneName();
			}
			if(state != null){
				return state.getStateName();
			}
			if(district != null){
				return district.getDistrictName();
			}
			if(division != null){
				return division.getDivisionName();
			}
		}
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public PmState getState() {
		return state;
	}
	public void setState(PmState state) {
		this.state = state;
	}
	public PmDistrict getDistrict() {
		return district;
	}
	public void setDistrict(PmDistrict district) {
		this.district = district;
	}

	public PmDivision getDivision() {
		return division;
	}

	public void setDivision(PmDivision division) {
		this.division = division;
	}

	public CmSupplierZone getCmSupplierZone() {
		return cmSupplierZone;
	}
	
	public void setCmSupplierZone(CmSupplierZone cmSupplierZone) {
		this.cmSupplierZone = cmSupplierZone;
	}
	
	public int getRowKey() {
		final int prime = 31;
		int result = 1;
		result = rowKey + prime * result;
		result = prime * result + ((locationName == null) ? 0 : locationName.hashCode());
		result = prime * result + ((cmSupplierZone == null) ? 0 : cmSupplierZone.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((district == null) ? 0 : district.hashCode());
		result = prime * result + ((division == null) ? 0 : division.hashCode());
		return result;
	}
	public void setRowKey(int rowKey) {
		this.rowKey = rowKey;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cmSupplierZone == null) ? 0 : cmSupplierZone.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((district == null) ? 0 : district.hashCode());
		result = prime * result + ((division == null) ? 0 : division.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		
		CmSupplierZoneStateDistrictDTO other = (CmSupplierZoneStateDistrictDTO) obj;
		if(cmSupplierZone == null) {
			if (other.cmSupplierZone != null)
				return false;
		} else if (!cmSupplierZone.equals(other.cmSupplierZone))
			return false;
		
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		
		if (district == null) {
			if (other.district != null)
				return false;
		} else if (!district.equals(other.district))
			return false;
		
		if (division == null) {
			if (other.division != null)
				return false;
		} else if (!division.equals(other.division))
			return false;
		
		return true;
	}
}
