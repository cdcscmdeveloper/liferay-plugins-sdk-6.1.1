package my.ep.web.managedbean.pm.location;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.VPmLocation;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "locationMB")
@SessionScoped
public class LocationManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private VPmLocation pmLocation = new VPmLocation();

	private String locationCode;

	private String selectedType;

	private String locationName;

	private String recordStatus;

	private PmState state;

	private PmDivision division;

	private PmDistrict district;

	private PmCity city;

	private PmCountry country;

	private List<PmCountry> countries;

	private List<PmState> states;

	private List<PmState> divisionStates;

	private List<PmDivision> divisions;

	private List<PmDistrict> districts;

	private List<PmCity> cites;

	private List<VPmLocation> vPmLocation;
	private List <PmSubSetia> signatoryUser;

	private int status;

	private VPmLocation location;

	public LocationManagedBean() {
		state = new PmState();
		division = new PmDivision();
		district = new PmDistrict();
		city = new PmCity();
		state.setPmCountry(new PmCountry());
		division.setPmState(state);
		district.setPmState(state);
		city.setPmDistrict(district);

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
	 * @param recordStatus
	 *            the recordStatus to set
	 */
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
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
	 * @return the divisionStates
	 */
	public List<PmState> getDivisionStates() {
		return divisionStates;
	}

	/**
	 * @param divisionStates
	 *            the divisionStates to set
	 */
	public void setDivisionStates(List<PmState> divisionStates) {
		this.divisionStates = divisionStates;
	}

	/**
	 * @return the signatoryUser
	 */
	public List <PmSubSetia> getSignatoryUser() {
		return signatoryUser;
	}

	/**
	 * @param signatoryUser the signatoryUser to set
	 */
	public void setSignatoryUser(List <PmSubSetia> signatoryUser) {
		this.signatoryUser = signatoryUser;
	}

}
