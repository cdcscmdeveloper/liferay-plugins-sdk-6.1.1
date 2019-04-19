/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.location;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.VPmLocation;
import my.ep.web.bean.pm.LocationSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Location
 * 
 * @author venugopal
 * @since 30/07/2014
 * 
 */
@ManagedBean(name = "locationMB")
@SessionScoped
public class LocationManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private VPmLocation pmLocation = new VPmLocation();

	private LocationSearchBean locationSearchBean;

	private String selectedType;

	private PmState state;

	private PmDivision division;

	private PmDistrict district;

	private PmCity city;

	private PmCountry country;

	private PmCountry pmCountry;

	private List<PmCountry> countries;

	private List<PmState> states;

	private List<PmDivision> divisions;

	private List<PmDistrict> districts;

	private List<PmCity> cites;

	private List<VPmLocation> vPmLocation;

	private boolean disabledVal = false;

	private VPmLocation location;
	private boolean divisionFlag = false;

	private boolean divisionBox = false;

	private boolean isLazy = false;

	public LocationManagedBean() {
		state = new PmState();
		division = new PmDivision();
		district = new PmDistrict();
		city = new PmCity();
		state.setPmCountry(new PmCountry());
		// division.setPmState(state);
		district.setPmState(state);
		// district.setPmDivision(division);
		city.setPmDistrict(district);

	}

	public List<PmCountry> getCountries() {
		return countries;
	}

	public void setCountries(List<PmCountry> countries) {
		this.countries = countries;
	}

	public List<VPmLocation> getvPmLocation() {
		return vPmLocation;
	}

	public void setvPmLocation(List<VPmLocation> vPmLocation) {
		this.vPmLocation = vPmLocation;
	}

	public VPmLocation getPmLocation() {
		return pmLocation;
	}

	public void setPmLocation(VPmLocation pmLocation) {
		this.pmLocation = pmLocation;
	}

	public List<PmState> getStates() {
		return states;
	}

	public void setStates(List<PmState> states) {
		this.states = states;
	}

	public List<PmDivision> getDivisions() {
		return divisions;
	}

	public void setDivisions(List<PmDivision> divisions) {
		this.divisions = divisions;
	}

	public List<PmDistrict> getDistricts() {
		return districts;
	}

	public void setDistricts(List<PmDistrict> districts) {
		this.districts = districts;
	}

	public List<PmCity> getCites() {
		return cites;
	}

	public void setCites(List<PmCity> cites) {
		this.cites = cites;
	}

	public PmState getState() {
		return state;
	}

	public void setState(PmState state) {
		this.state = state;
	}

	public PmDivision getDivision() {
		return division;
	}

	public void setDivision(PmDivision division) {
		this.division = division;
	}

	public PmDistrict getDistrict() {
		return district;
	}

	public void setDistrict(PmDistrict district) {
		this.district = district;
	}

	public PmCity getCity() {
		return city;
	}

	public void setCity(PmCity city) {
		this.city = city;
	}

	public PmCountry getCountry() {
		return country;
	}

	public void setCountry(PmCountry country) {
		this.country = country;
	}

	/**
	 * @return the location
	 */
	public VPmLocation getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(VPmLocation location) {
		this.location = location;
	}

	/**
	 * @return the divisionFlag
	 */
	public boolean isDivisionFlag() {
		return divisionFlag;
	}

	/**
	 * @param divisionFlag
	 *            the divisionFlag to set
	 */
	public void setDivisionFlag(boolean divisionFlag) {
		this.divisionFlag = divisionFlag;
	}

	/**
	 * @return the locationSearchBean
	 */
	public LocationSearchBean getLocationSearchBean() {
		if (locationSearchBean == null)
			locationSearchBean = new LocationSearchBean();
		return locationSearchBean;
	}

	/**
	 * @param locationSearchBean
	 *            the locationSearchBean to set
	 */
	public void setLocationSearchBean(LocationSearchBean locationSearchBean) {
		this.locationSearchBean = locationSearchBean;
	}

	/**
	 * @return the selectedType
	 */
	public String getSelectedType() {
		return selectedType;
	}

	/**
	 * @param selectedType
	 *            the selectedType to set
	 */
	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}

	/**
	 * @return the disabledVal
	 */
	public boolean isDisabledVal() {
		return disabledVal;
	}

	/**
	 * @param disabledVal
	 *            the disabledVal to set
	 */
	public void setDisabledVal(boolean disabledVal) {
		this.disabledVal = disabledVal;
	}

	/**
	 * @return the pmCountry
	 */
	public PmCountry getPmCountry() {
		return pmCountry;
	}

	/**
	 * @param pmCountry
	 *            the pmCountry to set
	 */
	public void setPmCountry(PmCountry pmCountry) {
		this.pmCountry = pmCountry;
	}

	public boolean getDivisionBox() {
		return divisionBox;
	}

	public void setDivisionBox(boolean divisionBox) {
		this.divisionBox = divisionBox;
	}

	public boolean isLazy() {
		return isLazy;
	}

	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

}
