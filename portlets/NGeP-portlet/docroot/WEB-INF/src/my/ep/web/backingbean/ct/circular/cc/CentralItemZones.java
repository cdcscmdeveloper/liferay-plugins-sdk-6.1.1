/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.circular.cc;

import java.io.Serializable;

import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;

/**
 * Serializable For Central Item Zone
 * 
 * @author Alam
 * @since 2/6/2014
 */
public class CentralItemZones implements Serializable{
	private static final long serialVersionUID = -8954854049553749821L;
	private	String zone;
	private	PmState	state;
	private	PmDistrict	district;
	private	PmCity	city;
	private	int rowKey;
	public CentralItemZones() {
		super();
	}
	
	public CentralItemZones(String zone) {
		super();
		this.zone = zone;
	}

	public CentralItemZones(PmState state) {
		super();
		this.state = state;
	}
	
	public CentralItemZones(PmDistrict district) {
		super();
		this.district = district;
	}
	
	public CentralItemZones(PmCity city) {
		super();
		this.city = city;
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
	

	public int getRowKey() {
		final int prime = 31;
		int result = 1;
		result = rowKey + prime * result;
	
		result = prime * result + ((zone == null) ? 0 : zone.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((district == null) ? 0 : district.hashCode());
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
		
		CentralItemZones other = (CentralItemZones) obj;
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
		
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
	
		
		return true;
	}
}	
