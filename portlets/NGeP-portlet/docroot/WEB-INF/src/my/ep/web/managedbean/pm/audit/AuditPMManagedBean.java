/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.audit;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.dto.pm.common.audit.VAuditPMDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.VPmLocation;
import my.ep.web.bean.pm.AuditPMSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Location
 * 
 * 
 */
@ManagedBean(name = "locationAMB")
@SessionScoped
public class AuditPMManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private AuditPMSearchBean searchBean;

	private String selectedType;

	private PmState state;

	private PmState resState;

	private PmDivision resDivision;

	private PmDistrict resDistrict;

	private PmCity resCity;

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

	private StateResDTO stateOldVal;

	private DivisionResDTO divisionOldVal;

	private DistrictResDTO districtOldVal;

	private CityResDTO cityOldVal;

	private String level2Selected;

	private String level3Selected;

	private boolean disabledVal = false;

	private VPmLocation location;
	private boolean divisionFlag = false;

	private boolean divisionBox = false;

	private boolean isLazy = false;

	private String locationDetailType;

	private String paramLevelCode3;

	private List<VAuditPMDTO> vpmAuditLocationDTOList;

	private VAuditPMDTO vAuditPMDTO;

	/**
	 * Constructor
	 */
	public AuditPMManagedBean() {
		state = new PmState();
		division = new PmDivision();
		district = new PmDistrict();
		city = new PmCity();
		state.setPmCountry(new PmCountry());
		district.setPmState(state);
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

	public PmState getResState() {
		return resState;
	}

	public void setResState(PmState resState) {
		this.resState = resState;
	}

	public PmDivision getResDivision() {
		return resDivision;
	}

	public void setResDivision(PmDivision resDivision) {
		this.resDivision = resDivision;
	}

	public PmDistrict getResDistrict() {
		return resDistrict;
	}

	public void setResDistrict(PmDistrict resDistrict) {
		this.resDistrict = resDistrict;
	}

	public PmCity getResCity() {
		return resCity;
	}

	public void setResCity(PmCity resCity) {
		this.resCity = resCity;
	}

	public StateResDTO getStateOldVal() {
		return stateOldVal;
	}

	public void setStateOldVal(StateResDTO stateOldVal) {
		this.stateOldVal = stateOldVal;
	}

	public DivisionResDTO getDivisionOldVal() {
		return divisionOldVal;
	}

	public void setDivisionOldVal(DivisionResDTO divisionOldVal) {
		this.divisionOldVal = divisionOldVal;
	}

	public DistrictResDTO getDistrictOldVal() {
		return districtOldVal;
	}

	public void setDistrictOldVal(DistrictResDTO districtOldVal) {
		this.districtOldVal = districtOldVal;
	}

	public CityResDTO getCityOldVal() {
		return cityOldVal;
	}

	public void setCityOldVal(CityResDTO cityOldVal) {
		this.cityOldVal = cityOldVal;
	}

	public String getLocationDetailType() {
		return locationDetailType;
	}

	public void setLocationDetailType(String locationDetailType) {
		this.locationDetailType = locationDetailType;
	}

	public String getLevel2Selected() {
		return level2Selected;
	}

	public void setLevel2Selected(String level2Selected) {
		this.level2Selected = level2Selected;
	}

	public String getLevel3Selected() {
		return level3Selected;
	}

	public void setLevel3Selected(String level3Selected) {
		this.level3Selected = level3Selected;
	}

	public List<VAuditPMDTO> getVpmAuditLocationDTOList() {
		return vpmAuditLocationDTOList;
	}

	public void setVpmAuditLocationDTOList(List<VAuditPMDTO> vpmAuditLocationDTOList) {
		this.vpmAuditLocationDTOList = vpmAuditLocationDTOList;
	}

	public AuditPMSearchBean getSearchBean() {
		if (searchBean == null)
			searchBean = new AuditPMSearchBean();
		return searchBean;
	}

	public void setSearchBean(AuditPMSearchBean searchBean) {
		this.searchBean = searchBean;
	}

	public String getParamLevelCode3() {
		return paramLevelCode3;
	}

	public void setParamLevelCode3(String paramLevelCode3) {
		this.paramLevelCode3 = paramLevelCode3;
	}

	public VAuditPMDTO getvAuditPMDTO() {
		return vAuditPMDTO;
	}

	public void setvAuditPMDTO(VAuditPMDTO vAuditPMDTO) {
		this.vAuditPMDTO = vAuditPMDTO;
	}
}
