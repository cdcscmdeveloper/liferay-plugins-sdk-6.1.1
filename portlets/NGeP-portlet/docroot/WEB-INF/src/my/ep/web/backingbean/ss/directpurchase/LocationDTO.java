package my.ep.web.backingbean.ss.directpurchase;

import java.io.Serializable;

import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;

public class LocationDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8954854049553749821L;
	private	PmState	state;
	private	PmDistrict	district;
	private	String	locationName;
	public LocationDTO(PmState state) {
		super();
		this.state = state;
	}
	
	public LocationDTO(PmDistrict district) {
		super();
		this.district = district;
	}

	public String getLocationName() {
		if(locationName == null || "".equalsIgnoreCase(locationName)){
			if(state != null){
				return state.getStateName();
			}
			if(district != null){
				return district.getDistrictName();
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
	
	
}
