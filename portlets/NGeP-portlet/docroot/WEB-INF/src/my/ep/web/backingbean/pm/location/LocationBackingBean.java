package my.ep.web.backingbean.pm.location;

import java.util.ArrayList;
import java.util.List;

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
import my.ep.constant.MessageConst;
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
import my.ep.exception.EpException;
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
import my.ep.web.bean.pm.LocationSearchBean;
import my.ep.web.managedbean.pm.location.LocationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;

@ManagedBean(name = "locationBackingBean")
@ViewScoped
public class LocationBackingBean extends BaseBackingBean<LocationManagedBean> {
	private static Logger log = Logger.getLogger(LocationBackingBean.class);
	private IPagingDataModel<VPmLocation> searchLocation = new SearchLocatonPagingDataModel();

	boolean result = false;
	// Navigation Details
	public static final String NAV_TO_SEARCH = "locationMain";
	public static final String NAV_TO_MAINTAIN = "maintainLocationMain";
	// Default value
	public static final boolean SEARCH_DEFAULT = false;	

	public LocationBackingBean() {
		retrieveCountryList();

	}

	public void preLoadingLocation() {
		this.resetLocationSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	public void searchLocation() {
		searchLocation.reloadData();
	}

	public String redirectToLocation(String locationType, Long id)
			throws Exception {

		if (id != null && id > 0) {
			maintainLocation(locationType, id);
			getManagedBean().setDisabledVal(true);
		} else {
			initialLocation();
			getManagedBean().setDisabledVal(false);
		}
		return NAV_TO_MAINTAIN;
	}

	public String backToSearch() {
		getManagedBean().setSelectedType(null);

		return NAV_TO_SEARCH;
	}

	public void initialLocation() {

		getManagedBean().setState(new PmState());
		getManagedBean().setCity(new PmCity());
		getManagedBean().setDistrict(new PmDistrict());
		getManagedBean().setDivision(new PmDivision());
		getManagedBean().getState().setPmCountry(new PmCountry());		
		getManagedBean().getDivision().setPmState(new PmState());
		getManagedBean().getDistrict().setPmState(new PmState());
		getManagedBean().getDistrict().setPmDivision(new PmDivision());
		getManagedBean().getCity().setPmDistrict(new PmDistrict());
		getManagedBean().setStates(new ArrayList<PmState>());
		getManagedBean().setDivisions(new ArrayList<PmDivision>());
		getManagedBean().setDistricts(new ArrayList<PmDistrict>());
	}

	private ISearchBean<VPmLocation> getSearchBeanData(
			ISearchBean<VPmLocation> searchBean) {
		boolean isSearchable = SEARCH_DEFAULT;
		boolean isEmpty = true;
		log.info("getSearchBeanData - Start");

		LocationSearchBean lSearchBean = this.getManagedBean()
				.getLocationSearchBean();

		if (lSearchBean.getLocationType() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("locationType",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getLocationSearchBean()
									.getLocationType()));
			isEmpty = false;
		}

		if (lSearchBean.getLocationCode() != null
				&& lSearchBean.getLocationCode().trim().length() > 0) {
			log.info("+ locationCode " + lSearchBean.getLocationCode());
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("locationCode",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getLocationSearchBean()
									.getLocationCode(), true));
			isEmpty = false;
		}
		if (lSearchBean.getLocationName() != null
				&& lSearchBean.getLocationName().trim().length() > 0) {
			log.info("+ locationName " + lSearchBean.getLocationName());

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("locationName",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getLocationSearchBean()
									.getLocationName(), true));
			isEmpty = false;
		}

		// if (isEmpty && !isSearchable) {
		log.info("+ status " + lSearchBean.getRecordStatus());
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("status", SearchOperator.OPERATOR_EQUALS
						.value(), getManagedBean().getLocationSearchBean()
						.getRecordStatus()));
		// }

		log.info("getSearchBeanData - End");
		return searchBean;
	}

	/**
	 * save Location details
	 * 
	 */
	public String saveLocation() {
		log.info("Save Method");

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
									"State", this.getManagedBean().getState()
											.getStateName()
											+ " is Successfully saved"));

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
										"Division :", "Division :"
												+ this.getManagedBean()
														.getDivision()
														.getDivisionName()
												+ " is Successfully saved"));

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
										"District: ", this.getManagedBean()
												.getDistrict()
												.getDistrictName()
												+ " is Successfully saved"));

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
									"City :", this.getManagedBean().getCity()
											.getCityName()
											+ " is Successfully saved"));

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

		return NAV_TO_SEARCH;

	}

	/**
	 * Maintain Location details
	 * 
	 */
	public void maintainLocation(String locationType, Long locationId) {

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
			
			DivisionReqDTO divisionReqDTO = new DivisionReqDTO();
			divisionReqDTO.setDivisionId(locationId);
			IDivisionSO divisionSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDivisionSO.class);
			DivisionResDTO resDTO = divisionSO
					.retrieveDivisionDtlById(divisionReqDTO);
			StateReqDTO stateReqDTO = new StateReqDTO();
			stateReqDTO.setCountryId(resDTO.getPmDivision().getPmState()
					.getPmCountry().getCountryId());
			IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
					.getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IStateSO.class);
			StateResDTO stateResDTO = reqSO
					.retriveStatesListByCountryID(stateReqDTO);
			getManagedBean().setStates(stateResDTO.getStateList());
			this.getManagedBean().setDivision(resDTO.getPmDivision());
			this.getManagedBean().getState().setStateId(resDTO.getPmDivision().getPmState().getStateId());
			this.getManagedBean().getState().getPmCountry().setCountryId(resDTO.getPmDivision().getPmState().getPmCountry().getCountryId());
			
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
			//this.getManagedBean().setDistrict(resDTO.getPmDistrict());
			StateReqDTO stateReqDTO = new StateReqDTO();
			stateReqDTO.setCountryId(resDTO.getPmDistrict().getPmState()
					.getPmCountry().getCountryId());
			IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
					.getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IStateSO.class);
			StateResDTO stateResDTO = reqSO
					.retriveStatesListByCountryID(stateReqDTO);
			getManagedBean().setStates(stateResDTO.getStateList());			
			this.getManagedBean().setState(resDTO.getPmDistrict().getPmState());
			this.getManagedBean().setDistrict(resDTO.getPmDistrict());
			getManagedBean().getState().getPmCountry().setCountryId(resDTO.getPmDistrict().getPmState().getPmCountry().getCountryId());
			getManagedBean().getDistrict().getPmState().setStateId(resDTO.getPmDistrict().getPmState().getStateId());
			getManagedBean().getDistrict().setDistrictId(resDTO.getPmDistrict().getDistrictId());

		}
		if (locationType.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_CITY)) {
			
			CityReqDTO cityReqDTO = new CityReqDTO();
			cityReqDTO.setCityId(locationId);
			ICitySO citySO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);
			CityResDTO resDTO = citySO.retriveCityByDtlId(cityReqDTO);
			this.getManagedBean().setCity(resDTO.getPmCity());
			StateReqDTO stateReqDTO = new StateReqDTO();
			stateReqDTO.setCountryId(resDTO.getPmCity().getPmDistrict().getPmState().getPmCountry().getCountryId());
			IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
					.getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IStateSO.class);
			StateResDTO stateResDTO = reqSO
					.retriveStatesListByCountryID(stateReqDTO);
			getManagedBean().setStates(stateResDTO.getStateList());
			System.out.println("StateList Size::"+stateResDTO.getStateList().size());

			DistrictReqDTO districtReqDTO = new DistrictReqDTO();
			IDistrictSO districtSO = (IDistrictSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IDistrictSO.class);

			districtReqDTO.setStateId(resDTO.getPmCity().getPmDistrict()
					.getPmState().getStateId());
			DistrictResDTO districtResDTO = districtSO
					.retriveDistrictListByStateID(districtReqDTO);
			getManagedBean().setDistricts(
					districtResDTO.getDistrictList());			
			getManagedBean().getState().getPmCountry().setCountryId(resDTO.getPmCity().getPmDistrict().getPmState().getPmCountry().getCountryId());
			getManagedBean().getDistrict().getPmState().setStateId(resDTO.getPmCity().getPmDistrict().getPmState().getStateId());
			getManagedBean().getDistrict().setDistrictId(resDTO.getPmCity().getPmDistrict().getDistrictId());
			System.out.println("Country"+resDTO.getPmCity().getPmDistrict().getPmState().getStateId());
			System.out.println("State"+resDTO.getPmCity().getPmDistrict().getPmState().getPmCountry().getCountryId());
			System.out.println("District"+resDTO.getPmCity().getPmDistrict().getDistrictId());

		}

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

				ISearchBean<VPmLocation> searchBeanData = getSearchBeanData(searchBean);
				locationReqDTO.setLocationSearchBean(searchBeanData);
				locationSO = (ILocationSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ILocationSO.class);
				LocationResDTO resDTO = locationSO
						.searchLocationData(locationReqDTO);

				if (resDTO.isSuccessful()) {
					return resDTO.getLocationResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}

			return null;
		}

	}

	public void retrieveCountryList() {

		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();
		getManagedBean().setCountries(resDTO.getCountryList());
	}

	public void retrieveStateList(AjaxBehaviorEvent e) {
		Long countryIdId = (Long) e.getComponent().getAttributes().get("value");
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryIdId);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStates(resDTO.getStateList());
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

	public void retrieveDivisionList(AjaxBehaviorEvent e) {
		Long stateId = (Long) e.getComponent().getAttributes().get("value");
		DivisionReqDTO reqDTO = new DivisionReqDTO();
		reqDTO.setStateId(stateId);
		IDivisionSO reqSO = (IDivisionSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDivisionSO.class);
		DivisionResDTO resDTO = reqSO.retriveDivisionListByState(reqDTO);
		getManagedBean().setDivisions(resDTO.getDivisionList());
	}

	
	
	public void retrieveDistrictList(AjaxBehaviorEvent e) {
		Long stateId = (Long) e.getComponent().getAttributes().get("value");
		DistrictReqDTO districtReqDTO = new DistrictReqDTO();
		districtReqDTO.setStateId(stateId);
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);
		DistrictResDTO districtResDTO = districtSO
				.retriveDistrictListByStateID(districtReqDTO);
		getManagedBean().setDistricts(districtResDTO.getDistrictList());
		

	}
	

	public void retrieveCityList(AjaxBehaviorEvent e) {
		Long districtId = (Long) e.getComponent().getAttributes().get("value");
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setDistrictId(districtId);
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);
		DistrictResDTO resDTO = districtSO.retriveDistrictDtlByID(reqDTO);
		getManagedBean().setDistrict(resDTO.getPmDistrict());

	}
	

	public void updateDivisionFlag(Long stateId) {

		if (stateId != null) {
			PmState pmState = PmCommonUtil.retrievePmStateByStateId(stateId);
			this.getManagedBean().setDivisionFlag(
					(pmState != null && ParameterConst.SARAK_SK_CODE
							.equals(pmState.getStateCode())) ? false : true);
			if (pmState != null
					&& ParameterConst.SARAK_SK_CODE.equals(pmState
							.getStateCode())) {
				this.getManagedBean().setDivision(new PmDivision());

			}
		}

	}

	public List<PmDivision> getDivisionList(Long stateId) {

		return PmCommonUtil.retrievePmDivisionList(stateId);
	}	

	public SelectItem[] getStatusOptions() {
		SelectItem[] items = new SelectItem[2];
		items[0] = new SelectItem(1, "Active");
		items[1] = new SelectItem(0, "Inactive");
		return items;
	}

	/**
	 * reset search bean fields
	 */
	public void resetLocationSearchBean() {
		log.info("resetLocationSearchBean");
		this.getManagedBean().setLocationSearchBean(new LocationSearchBean());
		searchLocation = new SearchLocatonPagingDataModel();
		getManagedBean().setSelectedType(null);
	}

	public IPagingDataModel<VPmLocation> getSearchLocation() {
		return searchLocation;
	}

	public void setSearchLocation(IPagingDataModel<VPmLocation> searchLocation) {
		this.searchLocation = searchLocation;
	}

}
