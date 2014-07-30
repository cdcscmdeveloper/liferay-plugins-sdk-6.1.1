package my.ep.web.managedbean.pm.location;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.VPmLocation;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "locationMB")
@SessionScoped
public class LocationManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private VPmLocation pmLocation = new VPmLocation();
	private List<SearchFilterBean> advSearchBeans = new ArrayList<SearchFilterBean>();

	private Long locationId;

	private String locationCode;

	private String selectedType;

	private String locationName;
	
	private String recordStatus;

	private PmState state = new PmState();

	private PmDivision division = new PmDivision();

	private PmDistrict district = new PmDistrict();

	private PmCity city = new PmCity();

	private PmCountry country;

	private List<PmCountry> countries;

	private List<PmState> states;

	private List<PmDivision> divisions;

	private List<PmDistrict> districts;

	private List<PmCity> cites;

	private List<VPmLocation> vPmLocation;

	private Long countryId;

	private Long stateId;

	private Long divisionId;

	private Long districtId;
	
	private int status;

	
	private String stateCode;
	private String stateName;
	private String divisionCode;
	private String divisionName;
	private String districtCode;
	private String districtName;
	private String cityCode;
	private String cityName;

	public LocationManagedBean() {
		if (advSearchBeans.isEmpty()) {
			advSearchBeans.add(new SearchFilterBean());
			advSearchBeans.add(new SearchFilterBean());
			advSearchBeans.add(new SearchFilterBean());
			advSearchBeans.add(new SearchFilterBean());
			advSearchBeans.add(new SearchFilterBean());

		}

	}

	public String getDivisionCode() {
		return divisionCode;
	}

	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public List<PmCountry> getCountries() {
		return countries;
	}

	public void setCountries(List<PmCountry> countries) {
		this.countries = countries;
	}

	public String getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
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

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public PmCountry getCountry() {
		return country;
	}

	public void setCountry(PmCountry country) {
		this.country = country;
	}

	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	public Long getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Long divisionId) {
		this.divisionId = divisionId;
	}

	public Long getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Long districtId) {
		this.districtId = districtId;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public List<SearchFilterBean> getAdvSearchBeans() {

		return advSearchBeans;
	}

	public void setAdvSearchBeans(List<SearchFilterBean> advSearchBeans) {
		this.advSearchBeans = advSearchBeans;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the recordStatus
	 */
	public String getRecordStatus() {
		return recordStatus;
	}

	/**
	 * @param recordStatus the recordStatus to set
	 */
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	

}
