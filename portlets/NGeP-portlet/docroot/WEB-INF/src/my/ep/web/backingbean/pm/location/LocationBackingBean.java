package my.ep.web.backingbean.pm.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.audit.IAuditPMSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.location.ILocationSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
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
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.VPmLocation;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.mapbeans.AuditPMLocationBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.LocationSearchBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.pm.location.LocationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
//Enhancement29642 - Start
//Enhancement29642 - End
//Enhancement29642 - Start
//Enhancement29642 - End

@ManagedBean(name = "locationBackingBean")
@ViewScoped
public class LocationBackingBean extends BaseBackingBean<LocationManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(LocationBackingBean.class);
	private IPagingDataModel<VPmLocation> searchLocation = new SearchLocatonPagingDataModel();

	// Navigation Details
	public static final String NAV_TO_SEARCH = "locationMain";
	public static final String NAV_TO_MAINTAIN = "maintainLocationMain";
	// Default value
	public static final boolean SEARCH_DEFAULT = false;

	public LocationBackingBean() {

	}

	// #32753 - START
	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	// #32753 - END

	public void preLoadingLocation() {
		this.resetLocationSearchBean();
		log.info("preLoading Location");
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	public void searchLocation() {
		this.getManagedBean().setLazy(true);
		// Enhancement29642 - start
		if (this.getManagedBean().getLocationSearchBean().getLocationType() == null) {
			throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
					"lbl.pm.location.type", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });

		}
		// Enhancement29642 - end
		searchLocation.reloadData();
	}

	public String redirectToLocation(String locationType, Long id) throws Exception {
		retrieveCountryList();

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
		// getManagedBean().setLazy(true);

		return NAV_TO_SEARCH;
	}

	public void initialLocation() {
		PmCountry defaultCountry = PmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);

		getManagedBean().setState(new PmState());
		getManagedBean().setDistrict(new PmDistrict());
		getManagedBean().setDivision(new PmDivision());
		getManagedBean().getState().setPmCountry(defaultCountry != null ? defaultCountry : new PmCountry());
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(getManagedBean().getState().getPmCountry().getCountryId());
		if (null != getManagedBean().getSelectedType())
			if (getManagedBean().getSelectedType().equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DIVISION))
				reqDTO.setIsAdoptDivision(true);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStates(resDTO.getStateList());
		getManagedBean().getDivision().setPmState(new PmState());
		getManagedBean().getDivision().getPmState().setPmCountry(defaultCountry != null ? defaultCountry : new PmCountry());
		getManagedBean().getDistrict().setPmState(new PmState());
		getManagedBean().getDistrict().getPmState().setPmCountry(defaultCountry != null ? defaultCountry : new PmCountry());
		getManagedBean().setCity(new PmCity());
		getManagedBean().getDistrict().setPmDivision(new PmDivision());
		getManagedBean().getCity().setPmDistrict(new PmDistrict());
		getManagedBean().setDistricts(new ArrayList<PmDistrict>());
	}

	private ISearchBean<VPmLocation> getSearchBeanData(ISearchBean<VPmLocation> searchBean) {
		// boolean isSearchable = SEARCH_DEFAULT;
		// boolean isEmpty = true;

		LocationSearchBean lSearchBean = this.getManagedBean().getLocationSearchBean();

		if (lSearchBean.getCountryId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("countryId", SearchOperator.OPERATOR_EQUALS.value(), lSearchBean.getCountryId()));
		}

		if (lSearchBean.getLocationType() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("locationType", SearchOperator.OPERATOR_EQUALS.value(), lSearchBean.getLocationType()));
		}

		if (lSearchBean.getLocationCode() != null && lSearchBean.getLocationCode().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("locationCode", SearchOperator.OPERATOR_LIKE.value(), lSearchBean.getLocationCode(),
							true));
		}
		if (lSearchBean.getLocationName() != null && lSearchBean.getLocationName().trim().length() > 0) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("locationName", SearchOperator.OPERATOR_LIKE.value(), getManagedBean()
							.getLocationSearchBean().getLocationName(), true));
		}
		// Enhancement29642 - Start
		if (lSearchBean.getAdditionalLocationName() != null && lSearchBean.getAdditionalLocationName().trim().length() > 0) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("locationName", SearchOperator.OPERATOR_LIKE.value(), getManagedBean()
							.getLocationSearchBean().getAdditionalLocationName(), true));
		}
		// Enhancement29642 - End
		if (lSearchBean.getRecordStatus() != null && lSearchBean.getRecordStatus().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("status", SearchOperator.OPERATOR_EQUALS.value(), lSearchBean.getRecordStatus()));
		} else {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("status", SearchOperator.OPERATOR_IN.value(), new ArrayList<String>(Arrays.asList(
							ParameterConst.RECORD_STATUS_INACTIVE, ParameterConst.RECORD_STATUS_ACTIVE))));

		}
		// Enhancement29642 - Start
		if (lSearchBean.getAdditionalLocationType() != null && lSearchBean.getRecordAdditional().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("additionalLocationType", SearchOperator.OPERATOR_EQUALS.value(), lSearchBean
							.getAdditionalLocationType()));
		} else {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("locationType", SearchOperator.OPERATOR_IN.value(), new ArrayList<String>(Arrays.asList(
							ParameterConst.LOCATION_LEVEL_STATE, ParameterConst.LOCATION_LEVEL_DIVISION,
							ParameterConst.LOCATION_LEVEL_DISTRICT, ParameterConst.LOCATION_LEVEL_CITY))));
		}
		// Enhancement29642 - End
		return searchBean;

	}

	/**
	 * save Location details
	 * 
	 */
	public String saveLocation() {
		log.info("saveLocation");

		// #32753 - START
		Long loginSessionUserId = 1L;
		IUserSO userSO = null;
		userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setLoginId(getFacesScopes().getUserScreenName());
		PmUser pmUser = userSO.retrieveUserByLoginId(userReqDTO).getPmUser();
		if (pmUser == null) {
			loginSessionUserId = getFacesScopes().getUserSessionBean().getUserId();
		} else {
			loginSessionUserId = pmUser.getUserId();
		}
		new LogKey(loginSessionUserId);
		log.info("32753 UserId: " + loginSessionUserId);
		// #32753 - END
		/** AUDIT 28895_35509 START **/
		IAuditPMSO pmauditSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IAuditPMSO.class);
		AuditPMBackingBeanTemplate auTemplate = new AuditPMLocationBackingBean();
		/** AUDIT 28895_35509 START END **/
		String message = null;
		String[] messageParam = null;
		Long locationId = null;
		Long locationMsgId = null; //#34445
		
		try { //#34445

			IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);

			if (ParameterConst.LOCATION_LEVEL_STATE.equals(getManagedBean().getSelectedType())) {
				StateReqDTO reqDTO = new StateReqDTO();
				PmState state = this.getManagedBean().getState();
				state.setStateCode(state.getStateCode().toUpperCase());
				state.setStateName(state.getStateName().trim());

				// #32753 - START
				if (!state.isBrandNew()) {
					state.setChangedBy(loginSessionUserId);
				} else {
					state.setChangedBy(loginSessionUserId);
					state.setCreatedBy(loginSessionUserId);
				}
				// #32753 - END

				reqDTO.setPmState(state);
				locationMsgId = getManagedBean().getState().getStateId(); // #34445
				/************ AUDIT #28895_35509 START *************/
				reqDTO.setStateId(getManagedBean().getState().getStateId());
				if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF) && getManagedBean().getState().getStateId() != null) {
					getManagedBean().setStateOldVal(stateSO.retriveStateDetailsByID(reqDTO));
				}
				/************ END AUDIT #28895_35509 END *************/
				StateResDTO resDTO = stateSO.saveOrUpdateStates(reqDTO);

				if (resDTO.isSuccessful()) {
					message = getManagedBean().getState().isBrandNew() ? MessageConst.ERR_900016
							: MessageConst.ERR_900017;
					messageParam = new String[] {
							ResourceBundleUtils.getMessage("lbl.pm.state", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale()), this.getManagedBean().getState().getStateName() };
					locationId = getManagedBean().getState().getStateId();
					/************ AUDIT #28895_35509 START *************/

					if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF)) {
						getManagedBean().setResState(resDTO.getPmState());
						auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_LOCDTL);
					}
					/************ END AUDIT #28895_35509 END *************/
				}
			} else if (ParameterConst.LOCATION_LEVEL_DIVISION.equals(getManagedBean().getSelectedType())) {
				DivisionReqDTO reqDTO = new DivisionReqDTO();
				IDivisionSO divisionSO = (IDivisionSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IDivisionSO.class);

				PmDivision division = this.getManagedBean().getDivision();
				division.setDivisionCode(division.getDivisionCode().toUpperCase());
				division.setDivisionName(division.getDivisionName().trim());

				// #32753 - START
				if (!division.isBrandNew()) {
					division.setChangedBy(loginSessionUserId);
				} else {
					division.setChangedBy(loginSessionUserId);
					division.setCreatedBy(loginSessionUserId);
				}
				// #32753 - END

				reqDTO.setPmDivision(division);
				reqDTO.setStateId(division.getPmState().getStateId());
				locationMsgId = getManagedBean().getDivision().getDivisionId(); // #34445
				/************ AUDIT #28895_35509 START *************/
				if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF)
						&& getManagedBean().getDivision().getDivisionId() != null) {
					reqDTO.setDivisionId(getManagedBean().getDivision().getDivisionId());
					getManagedBean().setDivisionOldVal(divisionSO.retrieveDivisionById(reqDTO));
				}
				/************ END AUDIT #28895_35509 END *************/
				DivisionResDTO resDTO = divisionSO.saveOrUpdateDivision(reqDTO);
				if (resDTO.isSuccessful()) {
					message = getManagedBean().getDivision().isBrandNew() ? MessageConst.ERR_900016
							: MessageConst.ERR_900017;
					messageParam = new String[] {
							ResourceBundleUtils.getMessage("lbl.pm.division", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale()),
							this.getManagedBean().getDivision().getDivisionName() };
					locationId = getManagedBean().getDivision().getDivisionId();
					/************ AUDIT #28895_35509 START *************/
					if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF)) {
						StateResDTO stateResDTO = stateSO.retrieveStateByStateId(reqDTO.getStateId());
						getManagedBean().setState(stateResDTO.getPmState());
						getManagedBean().setResDivision(resDTO.getPmDivision());
						auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_LOCDTL);
					}
					/************ END AUDIT #28895_35509 END *************/
				}
			} else if (ParameterConst.LOCATION_LEVEL_DISTRICT.equals(getManagedBean().getSelectedType())) {
				DistrictReqDTO reqDTO = new DistrictReqDTO();
				IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);

				PmDistrict district = this.getManagedBean().getDistrict();
				district.setDistrictCode(district.getDistrictCode().toUpperCase());
				district.setDistrictName(district.getDistrictName().trim());
				if (!this.getManagedBean().getDivisionBox()) {
					if (null != district.getPmDivision()) {
						if (district.getPmDivision().getDivisionId() == null) {
							throw new EpException(MessageConst.ERR_900014);
						}
					}
				} else {
					district.setPmDivision(null);
				}
				// #32753 - START
				if (!district.isBrandNew()) {
					district.setChangedBy(loginSessionUserId);
				} else {
					district.setChangedBy(loginSessionUserId);
					district.setCreatedBy(loginSessionUserId);
				}
				// #32753 - END

				reqDTO.setPmDistrict(district);
				reqDTO.setStateId(district.getPmState().getStateId());
				reqDTO.setDivisionId(null != district.getPmDivision() ? district.getPmDivision().getDivisionId() : null);
				locationMsgId = getManagedBean().getDistrict().getDistrictId(); // #34445
				/************ AUDIT #28895_35509 *************/
				reqDTO.setDistrictId(getManagedBean().getDistrict().getDistrictId());
				if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF)
						&& getManagedBean().getDistrict().getDistrictId() != null) {
					getManagedBean().setDistrictOldVal(districtSO.retriveDistrictByID(reqDTO));
				}
				/************ END AUDIT #28895_35509 *************/
				DistrictResDTO resDTO = districtSO.saveOrUpdateDistrict(reqDTO);
				if (resDTO.isSuccessful()) {
					message = getManagedBean().getDistrict().isBrandNew() ? MessageConst.ERR_900016
							: MessageConst.ERR_900017;
					messageParam = new String[] {
							ResourceBundleUtils.getMessage("lbl.pm.district", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale()),
							this.getManagedBean().getDistrict().getDistrictName() };
					locationId = getManagedBean().getDistrict().getDistrictId();
					/************ AUDIT #28895_35509 *************/
					if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF)) {
						getManagedBean().setResDistrict(resDTO.getPmDistrict());
						auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_LOCDTL);
					}
					/************ END AUDIT #28895_35509 *************/
				}
			} else if (ParameterConst.LOCATION_LEVEL_CITY.equals(getManagedBean().getSelectedType())) {
				CityReqDTO reqDTO = new CityReqDTO();
				ICitySO citySO = (ICitySO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);

				PmCity city = this.getManagedBean().getCity();
				city.setCityCode(city.getCityCode().toUpperCase());
				city.setCityName(city.getCityName().trim());

				// #32753 - START
				if (!city.isBrandNew()) {
					city.setChangedBy(loginSessionUserId);
				} else {
					city.setChangedBy(loginSessionUserId);
					city.setCreatedBy(loginSessionUserId);
				}
				// #32753 - END

				reqDTO.setPmCity(city);
				reqDTO.setDistrictId(city.getPmDistrict().getDistrictId());
				locationMsgId = getManagedBean().getCity().getCityId(); // #34445
				/************ AUDIT #28895_35509 *************/
				reqDTO.setCityId(getManagedBean().getCity().getCityId());
				if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF) && getManagedBean().getCity().getCityId() != null) {
					getManagedBean().setCityOldVal(citySO.retriveCityById(reqDTO));
				}
				/************ END AUDIT #28895_35509 *************/
				CityResDTO resDTO = citySO.saveOrUpdateCity(reqDTO);
				if (resDTO.isSuccessful()) {
					message = getManagedBean().getCity().isBrandNew() ? MessageConst.ERR_900016
							: MessageConst.ERR_900017;
					messageParam = new String[] {
							ResourceBundleUtils.getMessage("lbl.pm.city", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale()), this.getManagedBean().getCity().getCityName() };
					locationId = getManagedBean().getCity().getCityId();
					/************ AUDIT #28895_35509 *************/
					if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF)) {
						getManagedBean().setResCity(resDTO.getPmCity());
						auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_LOCDTL);
					}
					/************ END AUDIT #28895_35509 *************/
				}

			}
			if (message != null && messageParam != null) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(message,
								messageParam, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}

			if (getManagedBean().isDisabledVal()) {
				if (locationId != null && getManagedBean().getSelectedType() != null) {
					maintainLocation(getManagedBean().getSelectedType(), locationId);
				}
				return NAV_TO_MAINTAIN;
			} else {
				this.getManagedBean().setSelectedType(null);
				return NAV_TO_SEARCH;
			}

		//#34445 Start
		} catch (EpException e) {
			handleException(e);
			if (locationMsgId != null && getManagedBean().getSelectedType() != null) {
				maintainLocation(getManagedBean().getSelectedType(), locationMsgId);
			}
			return NAV_TO_MAINTAIN;
		}
		//#34445 End
	}

	/** #35509 getCountryAuditVal() START **/
	private void getCountryAuditVal(Long selectedCountryId) {
		retrieveCountryList();
		Map<Long, PmCountry> countryMap = new HashMap<>();
		for (PmCountry country : getManagedBean().getCountries()) {
			countryMap.put(country.getCountryId(), country);
		}

		PmCountry selectedCountry = countryMap.get(selectedCountryId);
		if (selectedCountry != null) {
			getManagedBean().setResCountry(selectedCountry);
		}
	}

	/** #35509 getCountryAuditVal() END **/

	/**
	 * Maintain Location details
	 * 
	 */
	public void maintainLocation(String locationType, Long locationId) {

		getManagedBean().setSelectedType(locationType);

		if (locationType.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_STATE)) {
			StateReqDTO stateReqDTO = new StateReqDTO();
			stateReqDTO.setStateId(locationId);
			IStateSO stateSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
			StateResDTO resDTO = stateSO.retriveStateDetailsByID(stateReqDTO);
			PmState state = resDTO.getPmState();
			this.getManagedBean().setState(state);

		}
		if (locationType.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DIVISION)) {

			DivisionReqDTO divisionReqDTO = new DivisionReqDTO();
			divisionReqDTO.setDivisionId(locationId);
			IDivisionSO divisionSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IDivisionSO.class);
			DivisionResDTO resDTO = divisionSO.retrieveDivisionDtlById(divisionReqDTO);
			StateReqDTO stateReqDTO = new StateReqDTO();
			stateReqDTO.setCountryId(resDTO.getPmDivision().getPmState().getPmCountry().getCountryId());
			IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IStateSO.class);
			StateResDTO stateResDTO = reqSO.retriveStatesListByCountryID(stateReqDTO);
			getManagedBean().setStates(stateResDTO.getStateList());
			this.getManagedBean().setDivision(resDTO.getPmDivision());
			this.getManagedBean().getState().setStateId(resDTO.getPmDivision().getPmState().getStateId());
			this.getManagedBean().getState().getPmCountry()
					.setCountryId(resDTO.getPmDivision().getPmState().getPmCountry().getCountryId());

		}
		if (locationType.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DISTRICT)) {

			DistrictReqDTO districtReqDTO = new DistrictReqDTO();
			districtReqDTO.setDistrictId(locationId);
			IDistrictSO districtSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IDistrictSO.class);
			DistrictResDTO resDTO = districtSO.retriveDistrictDtlByID(districtReqDTO);

			StateReqDTO stateReqDTO = new StateReqDTO();
			stateReqDTO.setCountryId(resDTO.getPmDistrict().getPmState().getPmCountry().getCountryId());
			IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IStateSO.class);
			StateResDTO stateResDTO = reqSO.retriveStatesListByCountryID(stateReqDTO);
			getManagedBean().setStates(stateResDTO.getStateList());
			this.getManagedBean().setState(resDTO.getPmDistrict().getPmState());
			this.getManagedBean().setDistrict(resDTO.getPmDistrict());
			getManagedBean().getState().getPmCountry()
					.setCountryId(resDTO.getPmDistrict().getPmState().getPmCountry().getCountryId());
			getManagedBean().getDistrict().getPmState().setStateId(resDTO.getPmDistrict().getPmState().getStateId());

			if (resDTO.getPmDistrict().getPmDivision() != null) {
				getManagedBean().setDivisions(getDivisionList(resDTO.getPmDistrict().getPmState().getStateId()));
				getManagedBean().getDistrict().getPmDivision()
						.setDivisionId(resDTO.getPmDistrict().getPmDivision().getDivisionId());

			}

		}
		if (locationType.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_CITY)) {

			CityReqDTO cityReqDTO = new CityReqDTO();
			cityReqDTO.setCityId(locationId);
			ICitySO citySO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);
			CityResDTO resDTO = citySO.retriveCityByDtlId(cityReqDTO);
			this.getManagedBean().setCity(resDTO.getPmCity());
			StateReqDTO stateReqDTO = new StateReqDTO();
			stateReqDTO.setCountryId(resDTO.getPmCity().getPmDistrict().getPmState().getPmCountry().getCountryId());
			IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IStateSO.class);
			StateResDTO stateResDTO = reqSO.retriveStatesListByCountryID(stateReqDTO);
			getManagedBean().setStates(stateResDTO.getStateList());
			DistrictReqDTO districtReqDTO = new DistrictReqDTO();
			IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
			districtReqDTO.setStateId(resDTO.getPmCity().getPmDistrict().getPmState().getStateId());
			DistrictResDTO districtResDTO = districtSO.retriveDistrictListByStateID(districtReqDTO);
			getManagedBean().setDistricts(districtResDTO.getDistrictList());
			getManagedBean().getState().getPmCountry()
					.setCountryId(resDTO.getPmCity().getPmDistrict().getPmState().getPmCountry().getCountryId());
			getManagedBean().getDistrict().getPmState().setStateId(resDTO.getPmCity().getPmDistrict().getPmState().getStateId());
			getManagedBean().getDistrict().setDistrictId(resDTO.getPmCity().getPmDistrict().getDistrictId());

		}

	}

	/**
	 * search Location details
	 * 
	 */
	private class SearchLocatonPagingDataModel extends PagingDataModel<VPmLocation> {

		private static final long serialVersionUID = 1L;

		public SearchLocatonPagingDataModel() {

			super("searchLocations", getManagedBean().isLazy());

		}

		public ISearchResultBean<VPmLocation> pageChangeListener(ISearchBean<VPmLocation> searchBean) {
			ILocationSO locationSO;

			try {
				LocationReqDTO locationReqDTO = new LocationReqDTO();

				ISearchBean<VPmLocation> searchBeanData = getSearchBeanData(searchBean);
				locationReqDTO.setLocationSearchBean(searchBeanData);
				locationSO = (ILocationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ILocationSO.class);
				LocationResDTO resDTO = locationSO.searchLocationData(locationReqDTO);

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

		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();
		getManagedBean().setCountries(resDTO.getCountryList());
	}

	public void retrieveStateList(AjaxBehaviorEvent e) {
		Long countryId = (Long) e.getComponent().getAttributes().get("value");
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryId);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStates(resDTO.getStateList());

		/* #35509 PM Start */
		getCountryAuditVal(countryId);
		/* #35509 PM End */
	}

	public void retrieveDivisionList(AjaxBehaviorEvent e) {
		Long stateId = (Long) e.getComponent().getAttributes().get("value");

		StateReqDTO stateReqDTO = new StateReqDTO();
		stateReqDTO.setStateId(stateId);
		IStateSO stateReqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IStateSO.class);
		StateResDTO stateResDTO = stateReqSO.retriveStateByID(stateReqDTO);
		PmState pmState = null;
		if (null != stateResDTO.getPmState()) {
			pmState = stateResDTO.getPmState();
		}

		DivisionReqDTO reqDTO = new DivisionReqDTO();

		reqDTO.setStateId(stateId);
		IDivisionSO reqSO = (IDivisionSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IDivisionSO.class);
		DivisionResDTO resDTO = reqSO.retriveDivisionListByState(reqDTO);
		getManagedBean().setDivisions(resDTO.getDivisionList());

		if (pmState != null) {
			if (pmState.getAdoptDivision()) {
				getManagedBean().setDivisionBox(false);
			} else {
				getManagedBean().getDistrict().getPmDivision().setDivisionId(null);
				getManagedBean().setDivisionBox(true);
			}
		}

	}

	public void retrieveDistrictList(AjaxBehaviorEvent e) {
		Long stateId = (Long) e.getComponent().getAttributes().get("value");
		DistrictReqDTO districtReqDTO = new DistrictReqDTO();
		districtReqDTO.setStateId(stateId);
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
		DistrictResDTO districtResDTO = districtSO.retriveDistrictListByStateID(districtReqDTO);
		getManagedBean().setDistricts(districtResDTO.getDistrictList());

		/* #35509 PM Start */
		Map<Long, PmState> stateMap = new HashMap<>();
		for (PmState state : getManagedBean().getStates()) {
			stateMap.put(state.getStateId(), state);
		}

		PmState selectedSate = stateMap.get(stateId);
		if (selectedSate != null) {
			getManagedBean().setResState(selectedSate);
		}
		/* #35509 PM End */
	}

	/*
	 * public void updateDivisionFlag(Long stateId) {
	 * 
	 * if (stateId != null) { PmState pmState =
	 * PmCommonUtil.retrievePmStateByStateId(stateId);
	 * this.getManagedBean().setDivisionFlag( (pmState != null &&
	 * ParameterConst.SARAK_SK_CODE .equals(pmState.getStateCode())) ? false :
	 * true); if (pmState != null && ParameterConst.SARAK_SK_CODE.equals(pmState
	 * .getStateCode())) { this.getManagedBean().setDivision(new PmDivision());
	 * 
	 * } }
	 * 
	 * }
	 */

	public List<PmDivision> getDivisionList(Long stateId) {

		return PmCommonUtil.retrievePmDivisionList(stateId);
	}

	public SelectItem[] getStatusOptions() {

		return PmCommonUtil.getCommonStatusOptions();
	}

	// Enhancement29642 - start
	public SelectItem[] getDropdownCodeValuesAdditionalLocation(String parameterType, String parameterCode) {

		ISearchBean<PmParameterDesc> searchBean = super.createSearchBean(PmParameterDesc.class,
				"tdDropdownCodeValuesAdditionalLocation");

		if (parameterCode.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DIVISION)) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_EQUALS.value(),
							ParameterConst.LOCATION_LEVEL_STATE));

		} else if (parameterCode.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DISTRICT)) {

			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
			bean.setSearchField("pmParameter.parameterCode");
			bean.setSearchValue(new ArrayList<String>(Arrays.asList(new String[] { ParameterConst.LOCATION_LEVEL_STATE,
					ParameterConst.LOCATION_LEVEL_DIVISION })));

			searchBean.getDataFilterBeans().add(bean);

		} else if (parameterCode.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_CITY)) {

			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
			bean.setSearchField("pmParameter.parameterCode");
			bean.setSearchValue(new ArrayList<String>(Arrays.asList(new String[] { ParameterConst.LOCATION_LEVEL_STATE,
					ParameterConst.LOCATION_LEVEL_DIVISION, ParameterConst.LOCATION_LEVEL_DISTRICT })));

			searchBean.getDataFilterBeans().add(bean);
		}

		return getDropdownCodeValuesBySearchBeanAddLoc(parameterType, searchBean);
	}

	public SelectItem[] getDropdownCodeValuesBySearchBeanAddLoc(String parameterType, ISearchBean<PmParameterDesc> searchBean) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {

			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().getParameterDescListBySearchBean(parameterType,
					searchBean);

			if (parameterType.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_STATE)) {
				items = new SelectItem[parameterDescs.size()];
			} else {
				items = new SelectItem[parameterDescs.size() + 1];
			}

			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
			int i = 1;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				items[i++] = new SelectItem(parameterDesc.getPmParameter().getParameterCode(), parameterDesc.getCodeName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}

		return items;
	}

	public void changeLocationType() {
		log.info("changeLocationType");
		this.getManagedBean().getLocationSearchBean().setRecordAdditional(null);
	}

	// Enhancement29642 - end

	/**
	 * reset search bean fields
	 */
	public void resetLocationSearchBean() {
		log.info("resetLocationSearchBean");
		getManagedBean().setLazy(false);
		LocationSearchBean searchBean = new LocationSearchBean();
		searchBean.setCountryId(PmCommonUtil.retrieveCountryByCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE).getCountryId());
		this.getManagedBean().setLocationSearchBean(searchBean);
		searchLocation = new SearchLocatonPagingDataModel();
		getManagedBean().setSelectedType(null);
	}

	public IPagingDataModel<VPmLocation> getSearchLocation() {
		return searchLocation;
	}

	public void setSearchLocation(IPagingDataModel<VPmLocation> searchLocation) {
		this.searchLocation = searchLocation;
	}

	/**
	 * get all country list
	 * 
	 * @return List<PmCountry>
	 */
	public List<PmCountry> getCountryList() {
		log.info("getCountryList");
		return PmCommonUtil.retrievePmCountryList();
	}

}
