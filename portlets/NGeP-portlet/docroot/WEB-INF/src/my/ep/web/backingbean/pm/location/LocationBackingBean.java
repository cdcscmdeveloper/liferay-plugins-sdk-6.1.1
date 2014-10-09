package my.ep.web.backingbean.pm.location;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.location.ILocationSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.dto.pm.common.location.LocationReqDTO;
import my.ep.dto.pm.common.location.LocationResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.VPmLocation;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.location.LocationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "locationBackingBean")
@ViewScoped
public class LocationBackingBean extends BaseBackingBean<LocationManagedBean> {
	private static Logger log = Logger.getLogger(LocationBackingBean.class);
	private IPagingDataModel<VPmLocation> searchLocation = new SearchLocatonPagingDataModel();
	boolean result = false;

	public LocationBackingBean() {

	}

	public void searchLocation() {

		searchLocation.reloadData();

	}

	public void reset() {
		this.getManagedBean().setLocationCode(null);
		this.getManagedBean().setLocationName(null);
		this.getManagedBean().setSelectedType(null);
		this.getManagedBean().setRecordStatus(null);
		this.getManagedBean().setStatus(-1);
	}

	public void resetLocation() {
		getManagedBean().setState(new PmState());
		getManagedBean().setCity(new PmCity());
		getManagedBean().setDivision(new PmDivision());
		getManagedBean().setDistrict(new PmDistrict());
		getManagedBean().getState().setPmCountry(new PmCountry());
		getManagedBean().getDivision().setPmState(new PmState());
		getManagedBean().getDistrict().setPmState(new PmState());
		getManagedBean().getCity().setPmDistrict(new PmDistrict());

	}

	public String backToSearch() {
		reset();
		return "/xhtml/pm/location/locationMain";
	}

	public String addLocation() {
		resetLocation();
		retrievePmCountryList();
		retrievePmStateList();
		retrievePmDistrictList();

		return "/xhtml/pm/location/createLocationMain";
	}

	/**
	 * save Location details
	 * 
	 */
	public String saveLocation() {

		try {
			if (getManagedBean().getSelectedType().equalsIgnoreCase(
					ParameterConst.LOCATION_LEVEL_STATE)) {

				StateReqDTO reqDTO = new StateReqDTO();

				IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IStateSO.class);
				PmState state = this.getManagedBean().getState();

				reqDTO.setPmState(state);
				StateResDTO resDTO = stateSO.saveOrUpdateStates(reqDTO);

				if (resDTO.isSuccessful()) {
					result = resDTO.isSuccessful();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									"State submit successfully.",
									"State Name : "
											+ this.getManagedBean().getState()
													.getStateName()));

				}

			} else if (getManagedBean().getSelectedType().equalsIgnoreCase(
					ParameterConst.LOCATION_LEVEL_DIVISION)) {

				DivisionReqDTO reqDTO = new DivisionReqDTO();

				IDivisionSO divisionSO = (IDivisionSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IDivisionSO.class);

				PmDivision division = this.getManagedBean().getDivision();

				reqDTO.setPmDivision(division);
				try {
					DivisionResDTO resDTO = divisionSO
							.saveOrUpdateDivision(reqDTO);
					if (resDTO.isSuccessful()) {
						result = resDTO.isSuccessful();
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_INFO,
										"Division submit successfully.",
										"Divison Name : "
												+ this.getManagedBean()
														.getDivision()
														.getDivisionName()));

					}
				} catch (Exception e) {
					e.printStackTrace();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, e
									.toString(), "Save Location failed."));
					throw e;
				}

			} else if (getManagedBean().getSelectedType().equalsIgnoreCase(
					ParameterConst.LOCATION_LEVEL_DISTRICT)) {

				DistrictReqDTO reqDTO = new DistrictReqDTO();

				IDistrictSO districtSO = (IDistrictSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IDistrictSO.class);

				PmDistrict district = this.getManagedBean().getDistrict();

				reqDTO.setPmDistrict(district);
				try {
					DistrictResDTO resDTO = districtSO
							.saveOrUpdateDistrict(reqDTO);
					if (resDTO.isSuccessful()) {
						result = resDTO.isSuccessful();
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_INFO,
										"District submit successfully.",
										"District Name : "
												+ this.getManagedBean()
														.getDistrict()
														.getDistrictName()));

					}
				} catch (Exception e) {
					e.printStackTrace();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, e
									.toString(), "Save Location failed."));
					throw e;
				}

			} else if (getManagedBean().getSelectedType().equalsIgnoreCase(
					ParameterConst.LOCATION_LEVEL_CITY)) {

				CityReqDTO reqDTO = new CityReqDTO();

				ICitySO citySO = (ICitySO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ICitySO.class);

				PmCity city = this.getManagedBean().getCity();

				reqDTO.setPmCity(city);
				CityResDTO resDTO = citySO.saveOrUpdateCity(reqDTO);

				if (resDTO.isSuccessful()) {
					result = resDTO.isSuccessful();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									"City submit successfully.", "City Name : "
											+ this.getManagedBean().getCity()
													.getCityName()));

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							"Save Location failed."));
			throw e;
		}

		return "/xhtml/pm/location/createLocationMain";

	}

	/**
	 * Edit Location details
	 * 
	 */
	public String editLocation(String locationType, Long locationId) {

		getManagedBean().setSelectedType(locationType);

		if (locationType.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_STATE)) {
			StateReqDTO stateReqDTO = new StateReqDTO();
			stateReqDTO.setStateId(locationId);
			IStateSO stateSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
			StateResDTO resDTO = stateSO.retriveStateDetailsByID(stateReqDTO);
			PmState state = resDTO.getPmState();
			this.getManagedBean().setState(state);

		}
		if (locationType
				.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DIVISION)) {
			retrievePmStateList();
			DivisionReqDTO divisionReqDTO = new DivisionReqDTO();
			divisionReqDTO.setDivisionId(locationId);
			IDivisionSO divisionSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDivisionSO.class);
			DivisionResDTO resDTO = divisionSO
					.retrieveDivisionDtlById(divisionReqDTO);
			System.out.println("StateID:::"+resDTO.getPmDivision().getPmState().getStateId());
			this.getManagedBean().getDivision().getPmState().setStateId(resDTO.getPmDivision().getPmState().getStateId());
			this.getManagedBean().setDivision(resDTO.getPmDivision());
		}
		if (locationType
				.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DISTRICT)) {
			DistrictReqDTO districtReqDTO = new DistrictReqDTO();
			districtReqDTO.setDistrictId(locationId);
			IDistrictSO districtSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDistrictSO.class);
			DistrictResDTO resDTO = districtSO
					.retriveDistrictDtlByID(districtReqDTO);
			this.getManagedBean().setDistrict(resDTO.getPmDistrict());

		}
		if (locationType.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_CITY)) {
			CityReqDTO cityReqDTO = new CityReqDTO();
			cityReqDTO.setCityId(locationId);
			ICitySO citySO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);
			CityResDTO resDTO = citySO.retriveCityByDtlId(cityReqDTO);
			this.getManagedBean().setCity(resDTO.getPmCity());

		}

		return "/xhtml/pm/location/editLocationMain";
	}

	/**
	 * update Location details
	 * 
	 */
	public String updateLocation() {

		try {
			if (getManagedBean().getSelectedType().equalsIgnoreCase(
					ParameterConst.LOCATION_LEVEL_STATE)) {
				StateReqDTO reqDTO = new StateReqDTO();

				IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IStateSO.class);

				reqDTO.setPmState(getManagedBean().getState());

				StateResDTO resDTO = stateSO.saveOrUpdateStates(reqDTO);

				if (resDTO.isSuccessful()) {
					result = resDTO.isSuccessful();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									"State Updated Successfully.",
									"State Name : "
											+ this.getManagedBean().getState()
													.getStateName()));

				}

			} else if (getManagedBean().getSelectedType().equalsIgnoreCase(
					ParameterConst.LOCATION_LEVEL_DIVISION)) {

				DivisionReqDTO reqDTO = new DivisionReqDTO();

				IDivisionSO divisionSO = (IDivisionSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IDivisionSO.class);

				reqDTO.setPmDivision(getManagedBean().getDivision());
				DivisionResDTO resDTO = divisionSO.saveOrUpdateDivision(reqDTO);

				if (resDTO.isSuccessful()) {
					result = resDTO.isSuccessful();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									"Division updated Successfully.",
									"Divison Name : "
											+ this.getManagedBean()
													.getDivision()
													.getDivisionName()));
				}

			} else if (getManagedBean().getSelectedType().equalsIgnoreCase(
					ParameterConst.LOCATION_LEVEL_DISTRICT)) {

				DistrictReqDTO reqDTO = new DistrictReqDTO();

				IDistrictSO districtSO = (IDistrictSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IDistrictSO.class);

				reqDTO.setPmDistrict(getManagedBean().getDistrict());
				DistrictResDTO resDTO = districtSO.saveOrUpdateDistrict(reqDTO);

				if (resDTO.isSuccessful()) {
					result = resDTO.isSuccessful();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									"District updated successfully.",
									"Dirstrct Name : "
											+ this.getManagedBean()
													.getDistrict()
													.getDistrictName()));

				}

			} else if (getManagedBean().getSelectedType().equalsIgnoreCase(
					ParameterConst.LOCATION_LEVEL_CITY)) {

				CityReqDTO reqDTO = new CityReqDTO();

				ICitySO citySO = (ICitySO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ICitySO.class);
				reqDTO.setPmCity(getManagedBean().getCity());

				CityResDTO resDTO = citySO.saveOrUpdateCity(reqDTO);

				if (resDTO.isSuccessful()) {
					result = resDTO.isSuccessful();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									"City updated successfully.",
									"City Name : "
											+ this.getManagedBean().getCity()
													.getCityName()));

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							"Location Save failed."));
			throw e;
		}
		return "/xhtml/pm/location/editLocationMain";
	}

	/**
	 * search Location details
	 * 
	 */
	private class SearchLocatonPagingDataModel extends
			PagingDataModel<VPmLocation> {

		private static final long serialVersionUID = 1L;

		public SearchLocatonPagingDataModel() {

			super("searchLocation");

		}

		public ISearchResultBean<VPmLocation> pageChangeListener(
				ISearchBean<VPmLocation> searchBean) {
			ILocationSO locationSO;

			try {

				LocationReqDTO locationReqDTO = new LocationReqDTO();

				if (getManagedBean().getSelectedType() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("locationType",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getSelectedType()));
				}
				if (getManagedBean().getLocationCode() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("locationCode",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getLocationCode(), true));
				}

				if (getManagedBean().getLocationName() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("locationName",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getLocationName(), true));
				}
				if (getManagedBean().getRecordStatus() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("status",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getRecordStatus()));
				}

				locationReqDTO.setLocationSearchBean(searchBean);
				locationSO = (ILocationSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ILocationSO.class);
				LocationResDTO resDto = locationSO
						.searchLocationData(locationReqDTO);

				if (resDto.isSuccessful()) {
					return resDto.getLocationResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	private void retrievePmCountryList() {
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();
		this.getManagedBean().setCountries(resDTO.getCountryList());
	}

	private void retrievePmStateList() {
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = stateSO.retrieveStateList();

		this.getManagedBean().setStates(resDTO.getStateList());
	}

	private void retrievePmDistrictList() {
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retrieveDistrictList();
		this.getManagedBean().setDistricts(resDTO.getDistrictList());
	}

	public void retriveDivisionValues(AjaxBehaviorEvent e) {
		Long stateId = (Long) e.getComponent().getAttributes().get("value");
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setStateId(stateId);
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = stateSO.retriveStateDetailsByID(reqDTO);
		getManagedBean().setState(resDTO.getPmState());

	}

	public void retriveDistrictValues(AjaxBehaviorEvent e) {
		Long stateId = (Long) e.getComponent().getAttributes().get("value");
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setStateId(stateId);
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = stateSO.retriveStateDetailsByID(reqDTO);
		getManagedBean().setState(resDTO.getPmState());

	}

	public void retriveCityValues(AjaxBehaviorEvent e) {
		Long districtId = (Long) e.getComponent().getAttributes().get("value");
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setDistrictId(districtId);
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);
		DistrictResDTO resDTO = districtSO.retriveDistrictDtlByID(reqDTO);
		getManagedBean().setDistrict(resDTO.getPmDistrict());

	}
	public SelectItem[] getStatusOptions() {
		SelectItem[] items = new SelectItem[2];
		items[0] = new SelectItem(1, "Active");
		items[1] = new SelectItem(0, "Inactive");
		return items;
	}

	public IPagingDataModel<VPmLocation> getSearchLocation() {
		return searchLocation;
	}

	public void setSearchLocation(IPagingDataModel<VPmLocation> searchLocation) {
		this.searchLocation = searchLocation;
	}

}
