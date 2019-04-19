package my.ep.web.backingbean.sc.directpurchase;

import java.io.Serializable;

import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;

// TODO: Auto-generated Javadoc
public class LocationDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8954854049553749821L;
	private	TplSpecZoneDtl	zone;
	private	PmState	state;
	private	PmDistrict	district;
	private	PmCity	city;
	private	PmDivision division;
	private	String	locationName;
	private	int rowKey;
	private PmAddress address;
	
	public LocationDTO() {
		super();
	}
	public LocationDTO(TplSpecZoneDtl zone) {
		super();
		this.zone = zone;
	}


	public LocationDTO(PmState state) {
		super();
		this.state = state;
	}
	
	public LocationDTO(PmDistrict district) {
		super();
		this.district = district;
	}
	public LocationDTO(PmCity city) {
		super();
		this.city = city;
	}
	
	public LocationDTO(PmDivision division) {
		super();
		this.division = division;
	}
	
	public LocationDTO(PmAddress address) {
		super();
		this.address = address;
	}

	/**
	 * Gets the location name.
	 *
	 * @return the location name
	 */
	public String getLocationName() {
		if(locationName == null || "".equalsIgnoreCase(locationName)){
			if(zone != null){
				return zone.getZoneName();
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
			if(city != null){
				return city.getCityName();
			}
		}
		return locationName;
	}
	
	/**
	 * Sets the location name.
	 *
	 * @param locationName the new location name
	 */
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	
	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public PmState getState() {
		return state;
	}
	
	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public void setState(PmState state) {
		this.state = state;
	}
	
	/**
	 * Gets the district.
	 *
	 * @return the district
	 */
	public PmDistrict getDistrict() {
		return district;
	}
	
	/**
	 * Sets the district.
	 *
	 * @param district the new district
	 */
	public void setDistrict(PmDistrict district) {
		this.district = district;
	}

	/**
	 * Gets the city.
	 *
	 * @return the city
	 */
	public PmCity getCity() {
		return city;
	}

	/**
	 * Sets the city.
	 *
	 * @param city the new city
	 */
	public void setCity(PmCity city) {
		this.city = city;
	}

	/**
	 * Gets the division.
	 *
	 * @return the division
	 */
	public PmDivision getDivision() {
		return division;
	}

	/**
	 * Sets the division.
	 *
	 * @param division the new division
	 */
	public void setDivision(PmDivision division) {
		this.division = division;
	}
	
	/**
	 * @return the zone
	 */
	public TplSpecZoneDtl getZone() {
		return zone;
	}
	/**
	 * @param zone the zone to set
	 */
	public void setZone(TplSpecZoneDtl zone) {
		this.zone = zone;
	}
	/**
	 * @return the address
	 */
	public PmAddress getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(PmAddress address) {
		this.address = address;
	}
	
	/**
	 * Gets the row key.
	 *
	 * @return the row key
	 */
	public int getRowKey() {
		final int prime = 31;
		int result = 1;
		result = rowKey + prime * result;
		result = prime * result + ((locationName == null) ? 0 : locationName.hashCode());
		result = prime * result + ((zone == null) ? 0 : zone.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((district == null) ? 0 : district.hashCode());
		result = prime * result + ((division == null) ? 0 : division.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		return result;
	}
	
	/**
	 * Sets the row key.
	 *
	 * @param rowKey the new row key
	 */
	public void setRowKey(int rowKey) {
		this.rowKey = rowKey;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((zone == null) ? 0 : zone.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((district == null) ? 0 : district.hashCode());
		result = prime * result + ((division == null) ? 0 : division.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
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
		
		LocationDTO other = (LocationDTO) obj;
		if (zone == null) {
			if (other.zone != null)
				return false;
		} else if (!zone.equals(other.zone))
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
		
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		
		if (address == null) {
			if(other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		
		return true;
	}
}
