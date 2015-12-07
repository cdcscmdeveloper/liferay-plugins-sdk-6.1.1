package my.ep.web.backingbean.ct;

import java.io.Serializable;

import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
/** 
* @author Alam
* @since 26/9/2014
*/
public class LocationDTO implements Serializable{
	private static final long serialVersionUID = -8954854049553749821L;
	private	String zone;
	private	String zoneId;
	private Long zoneDtlId;
	private	PmState	state;
	private	PmDistrict	district;
	private	PmCity	city;
	private	PmDivision division;
	private	String	locationName;
	private	int rowKey;
	private PmAddress address;
	private	int	deliveryTerm;
	private Long uomId;
	
	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize object of class
	 */
	public LocationDTO() {
		super();
	}
	
	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize the 
	 * define variable in method of class
	 */
	public LocationDTO(String zoneId, String zone,  int	deliveryTerm, Long uomId) {
		super();
		this.zone = zone;
		this.zoneId = zoneId;
		this.deliveryTerm = deliveryTerm;
		this.uomId = uomId;
	}

	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize the 
	 * define variable in method of class
	 */
	public LocationDTO(Long zoneDtlId,PmState state) {
		super();
		this.state = state;
		this.zoneDtlId = zoneDtlId;
	}
	
	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize the 
	 * define variable in method of class
	 */
	public LocationDTO(Long zoneDtlId, PmDistrict district) {
		super();
		this.district = district;
		this.zoneDtlId = zoneDtlId;
	}
	
	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize the 
	 * define variable in method of class
	 */
	public LocationDTO(PmCity city) {
		super();
		this.city = city;
	}
	
	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize the 
	 * define variable in method of class
	 */
	public LocationDTO(PmDivision division) {
		super();
		this.division = division;
	}
	
	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize the 
	 * define variable in method of class
	 */
	public LocationDTO(PmAddress address) {
		super();
		this.address = address;
	}

	/**
	 * @return the locationName
	 */
	public String getLocationName() {
		if(locationName == null || "".equalsIgnoreCase(locationName)){
			if(zone != null){
				return getZone();
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
	 * @param locationName the locationName to set
	 */
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	
	/**
	 * @return the state
	 */
	public PmState getState() {
		return state;
	}
	
	/**
	 * @param state the state to set
	 */
	public void setState(PmState state) {
		this.state = state;
	}
	
	/**
	 * @return the district
	 */
	public PmDistrict getDistrict() {
		return district;
	}
	
	/**
	 * @param district the district to set
	 */
	public void setDistrict(PmDistrict district) {
		this.district = district;
	}

	/**
	 * @return the city
	 */
	public PmCity getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(PmCity city) {
		this.city = city;
	}

	/**
	 * @return the division
	 */
	public PmDivision getDivision() {
		return division;
	}
    
	/**
	 * @param division the division to set
	 */
	public void setDivision(PmDivision division) {
		this.division = division;
	}
	
	/**
	 * @return the zone
	 */
	public String getZone() {
		return zone;
	}
	
	/**
	 * @param zone the zone to set
	 */
	public void setZone(String zone) {
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
	 * @return the RowKey for hashcode
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
	
	/**
	 * @return the deliveryTerm
	 */
	public int getDeliveryTerm() {
		return deliveryTerm;
	}
	
	/**
	 * @param deliveryTerm the deliveryTerm to set
	 */
	public void setDeliveryTerm(int deliveryTerm) {
		this.deliveryTerm = deliveryTerm;
	}
	
	/**
	 * @return the uomId
	 */
	public Long getUomId() {
		return uomId;
	}
	
	/**
	 * @param uomId the uomId to set
	 */
	public void setUomId(Long uomId) {
		this.uomId = uomId;
	}
	
	/**
	 * @return the zoneId
	 */
	public String getZoneId() {
		return zoneId;
	}
	
	/**
	 * @param zoneId the zoneId to set
	 */
	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}
	
	/**
	 * @return the zoneDtlId
	 */
	public Long getZoneDtlId() {
		return zoneDtlId;
	}

	/**
	 * @param zoneDtlId the zoneDtlId to set
	 */
	public void setZoneDtlId(Long zoneDtlId) {
		this.zoneDtlId = zoneDtlId;
	}
}	
