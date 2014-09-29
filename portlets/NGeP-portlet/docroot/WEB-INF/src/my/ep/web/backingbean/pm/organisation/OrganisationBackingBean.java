package my.ep.web.backingbean.pm.organisation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.ptjgroup.IPtjGroupSO;
import my.ep.biz.service.pm.common.agoffice.IAgOfficeSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.ptjgroup.PtjGroupResDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.zone.ZoneResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.organisation.OrganisationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "organisationBackingBean")
@ViewScoped
public class OrganisationBackingBean extends
		BaseBackingBean<OrganisationManagedBean> {
	private static Logger log = Logger.getLogger(OrganisationBackingBean.class);

	private IPagingDataModel<PmOrgProfile> searchOrganisation = new SearchOrgPagingDataModel();
	private boolean flag;
	boolean result = false;

	public OrganisationBackingBean() {
		retrieveCountryList();
		retrieveAgOfficeList();
		retrievePtjGroupList();
		retrieveOrgProfileList();
		retrieveFinancialOrgList();

		// retrievePmDistrictList();
		if (null == getManagedBean().getOfficeAddress()) {
			getManagedBean().setOfficeAddress(new PmAddress());
		}
		if (null == getManagedBean().getOfficeAddress().getPmCountry()) {
			getManagedBean().getOfficeAddress().setPmCountry(new PmCountry());
			// getManagedBean().getOfficeAddress().getPmCountry().setCountryId(1L);
		}
	}

	public void getProfileDetails() {
		this.getManagedBean().setOrganisationActiveIndex(0);
	}

	public void getGovernmentSeller() {
		this.getManagedBean().setOrganisationActiveIndex(1);
	}

	public void getResponsibleProfile() {
		this.getManagedBean().setOrganisationActiveIndex(2);
	}

	public String backToSearch() {
		reset();

		return "/xhtml/pm/organisation/organisationMain";
	}

	public void reset() {
		getManagedBean().setOrgProfile(new PmOrgProfile());
		getManagedBean().setOrgValidity(new PmOrgValidity());
		getManagedBean().setOfficeAddress(new PmAddress());
		getManagedBean().getOfficeAddress().setPmCountry(new PmCountry());
		getManagedBean().getOfficeAddress().setPmState(new PmState());
		getManagedBean().getOfficeAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getOfficeAddress().setPmCity(new PmCity());
		getManagedBean().getOfficeAddress().setPmDivision(new PmDivision());
		getManagedBean().getOfficeAddress().getPmDivision().setDivisionId(1L);
		getManagedBean().setDeliveryAddress(new PmAddress());
		getManagedBean().getDeliveryAddress().setPmCountry(new PmCountry());
		getManagedBean().setDeliveryContactPerson(new PmContact());
		getManagedBean().getDeliveryAddress().setPmState(new PmState());
		getManagedBean().getDeliveryAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getDeliveryAddress().setPmCity(new PmCity());
		getManagedBean().getDeliveryAddress().setPmDivision(new PmDivision());
		getManagedBean().setOfficeContactPerson(new PmContact());
		getManagedBean().getOrgProfile().setPmZone(new PmZone());
		getManagedBean().setOrgType(null);
		getManagedBean().getOrgProfile().setRecordStatusFlg(null);
	}

	public void searchOrg() {

		searchOrganisation.reloadData();
	}

	public void resetOrganisation() {
		getManagedBean().setOrgCode(null);
		getManagedBean().setOrgTypeCode(null);
		getManagedBean().setOrgName(null);
		getManagedBean().setOrgStatus(null);

	}

	private class SearchOrgPagingDataModel extends
			PagingDataModel<PmOrgProfile> {

		private static final long serialVersionUID = 1L;

		public SearchOrgPagingDataModel() {

			super("searchOrganisation");

		}

		public ISearchResultBean<PmOrgProfile> pageChangeListener(
				ISearchBean<PmOrgProfile> searchBean) {
			IOrgProfileSO orgProfileSO;
			try {

				OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();

				if (getManagedBean().getOrgTypeCode() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("orgTypeCode.parameterCode",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getOrgTypeCode()));
				}
				if (getManagedBean().getOrgCode() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmOrgValidity.orgCode",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getOrgCode(), true));
				}
				if (getManagedBean().getOrgName() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmOrgValidity.orgName",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getOrgName(), true));
				}
				if (getManagedBean().getOrgStatus() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"pmOrgValidity.recordStatusFlg",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getOrgStatus()));
				}

				orgProfileReqDTO.setOrgProfileSearchBean(searchBean);
				orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileResDTO resDto = orgProfileSO
						.searchOrgProfile(orgProfileReqDTO);

				if (resDto.isSuccessful()) {
					return resDto.getOrgProfileResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	public String editOrganization(PmOrgProfile orgProfile) {
		// getManagedBean().setOrganisationActiveIndex(1);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);

		OrgProfileResDTO orgResDTO = orgProfileSO
				.retrieveOrgDetailsByOrgId(reqDTO);
		orgProfile = orgResDTO.getPmOrgProfile();
		getManagedBean().setOrgType(
				orgProfile.getOrgTypeCode().getParameterCode());
		// getManagedBean().getOrgProfile().setPmZone(orgProfile.getPmZone());

		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.BPK_PEROLEHAN_PM_SABAH.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value())) {
			retrievePmZoneList();
			getManagedBean().setZoneId(orgProfile.getPmZone().getZoneId());

		}

		List<PmAddressType> pmAddressTypes = orgProfile.getPmAddressTypes();
		if (pmAddressTypes != null && !pmAddressTypes.isEmpty()) {
			for (PmAddressType pmAddressType : pmAddressTypes) {
				if (pmAddressType.getAddressType().equalsIgnoreCase("R")) {

					retrieveCountryList();

					if (pmAddressType.getPmAddress().getPmDivision() == null) {
						pmAddressType.getPmAddress().setPmDivision(
								new PmDivision());
					}

					StateReqDTO stateReqDTO = new StateReqDTO();
					stateReqDTO.setCountryId(pmAddressType.getPmAddress()
							.getPmCountry().getCountryId());
					IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IStateSO.class);
					StateResDTO resDTO = reqSO
							.retriveStatesListByCountryID(stateReqDTO);
					getManagedBean().setStateList(resDTO.getStateList());

					DistrictReqDTO districtReqDTO = new DistrictReqDTO();
					IDistrictSO districtSO = (IDistrictSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IDistrictSO.class);
					if (pmAddressType.getPmAddress().getPmDivision()
							.getDivisionId() != null) {
						DivisionReqDTO divReqDTO = new DivisionReqDTO();
						divReqDTO.setStateId(pmAddressType.getPmAddress()
								.getPmState().getStateId());
						IDivisionSO divisionSO = (IDivisionSO) BusinessDelegate
								.getInstance().getServiceObject(
										ModuleCodeConst.PROFILE_MANAGEMENT,
										IDivisionSO.class);
						DivisionResDTO divResDTO = divisionSO
								.retriveDivisionListByState(divReqDTO);
						getManagedBean().setDivisionList(
								divResDTO.getDivisionList());
						districtReqDTO
								.setDivisionId(pmAddressType.getPmAddress()
										.getPmDivision().getDivisionId());
						DistrictResDTO districtResDTO = districtSO
								.retriveDistrictListByDivision(districtReqDTO);
						getManagedBean().setDistrictList(
								districtResDTO.getDistrictList());
					} else {
						districtReqDTO.setStateId(pmAddressType.getPmAddress()
								.getPmState().getStateId());
						DistrictResDTO districtResDTO = districtSO
								.retriveDistrictListByStateID(districtReqDTO);
						getManagedBean().setDistrictList(
								districtResDTO.getDistrictList());
					}

					/*
					 * districtReqDTO.setStateId(pmAddressType.getPmAddress()
					 * .getPmState().getStateId()); DistrictResDTO
					 * districtResDTO = districtSO
					 * .retriveDistrictListByStateID(districtReqDTO);
					 * getManagedBean().setDistrictList(
					 * districtResDTO.getDistrictList());
					 */

					CityReqDTO cityReqDTO = new CityReqDTO();
					cityReqDTO.setDistrictId(pmAddressType.getPmAddress()
							.getPmDistrict().getDistrictId());
					ICitySO citySO = (ICitySO) BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									ICitySO.class);
					CityResDTO cityResDTO = citySO
							.retriveCityListByDistrictId(cityReqDTO);
					this.getManagedBean().setCityList(cityResDTO.getCityList());

					//pmAddressType.setEditable(true);
					this.getManagedBean().setOfficeAddress(
							pmAddressType.getPmAddress());

				} else if (pmAddressType.getAddressType().equalsIgnoreCase("D")) {

					retrieveCountryList();

					StateReqDTO stateReqDTO = new StateReqDTO();
					stateReqDTO.setCountryId(pmAddressType.getPmAddress()
							.getPmCountry().getCountryId());
					IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IStateSO.class);
					StateResDTO resDTO = reqSO
							.retriveStatesListByCountryID(stateReqDTO);
					getManagedBean().setStateList(resDTO.getStateList());

					DistrictReqDTO districtReqDTO = new DistrictReqDTO();
					IDistrictSO districtSO = (IDistrictSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IDistrictSO.class);

					districtReqDTO.setStateId(pmAddressType.getPmAddress()
							.getPmState().getStateId());
					DistrictResDTO districtResDTO = districtSO
							.retriveDistrictListByStateID(districtReqDTO);
					getManagedBean().setDistrictList(
							districtResDTO.getDistrictList());

					CityReqDTO cityReqDTO = new CityReqDTO();
					cityReqDTO.setDistrictId(pmAddressType.getPmAddress()
							.getPmDistrict().getDistrictId());
					ICitySO citySO = (ICitySO) BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									ICitySO.class);
					CityResDTO cityResDTO = citySO
							.retriveCityListByDistrictId(cityReqDTO);
					this.getManagedBean().setCityList(cityResDTO.getCityList());
//pmAddressType.setEditable(true);
					this.getManagedBean().setDeliveryAddress(
							pmAddressType.getPmAddress());

				}
				if (!pmAddressType.getPmContacts().isEmpty()) {
					this.getManagedBean().setOfficeContactPerson(
							pmAddressType.getPmContacts().get(0));
				}
			}
		}

		this.getManagedBean().setOrgValidity(
				orgResDTO.getPmOrgProfile().getPmOrgValidity());
		this.getManagedBean().setOrgProfile(orgResDTO.getPmOrgProfile());

		return "/xhtml/pm/organisation/maintainOrganisation.xhtml";

	}

	public String saveOrganization() {

		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		log.info("Org Type  ::" + getManagedBean().getOrgType());

		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);

		IParameterSO parameterSO = (IParameterSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		parameterReqDTO.setParameterType(ParameterTypeConst.ORGANISATION_TYPES);
		parameterReqDTO.setParameterCode(getManagedBean().getOrgType());
		ParameterResDTO parameterResDTO = parameterSO
				.retrieveParameterByTypeAndCode(parameterReqDTO);

		PmOrgProfile orgProfile = this.getManagedBean().getOrgProfile();

		orgProfile.setOrgTypeCode(parameterResDTO.getPmParameter());		
		PmOrgValidity orgValidity = this.getManagedBean().getOrgValidity();
		
		orgValidity.setEffDate(new Date());
		orgValidity.setPmOrgProfile(orgProfile);
		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.MINISTRY.value())) {
			orgValidity.setEffDate(getManagedBean().getOrgValidity()
					.getEffDate());
			orgValidity.setExpDate(getManagedBean().getOrgValidity()
					.getExpDate());

		}
		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.DIVISION.value())) {
			orgValidity.setEffDate(getManagedBean().getOrgValidity()
					.getEffDate());
			orgValidity.setExpDate(getManagedBean().getOrgValidity()
					.getExpDate());

		} else {
			orgValidity.setEffDate(new Date());
		}

		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.BPK_PEROLEHAN_PM_SABAH.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value())) {
			orgValidity.setEffDate(new Date());
			PmZone zone = getManagedBean().getZone();
			zone.setZoneId(getManagedBean().getZoneId());
			orgProfile.setPmZone(zone);

		}

		/*PmAddressType addressType = getManagedBean().getPmAddressType();
		addressType.setAddressType(AddressType.RECEIVING_OFFICER_ADDRESS
				.value());
		List<PmAddressType> pmAddressTypes = getManagedBean()
				.getOfficeAddressTypeList();
		pmAddressTypes.add(addressType);

		PmAddress address = getManagedBean().getOfficeAddress();
		PmContact contact = getManagedBean().getOfficeContactPerson();
		contact.setPmAddressType(addressType);
		List<PmContact> contactLst = getManagedBean().getContactList();
		contactLst.add(contact);
		addressType.setPmAddress(address);
		addressType.setPmContacts(contactLst);
		addressType.setPmOrgProfile(orgProfile);
		orgProfile.setPmAddressTypes(pmAddressTypes);
*/
		/*
		 * PmAddressType addressType = getManagedBean().getPmAddressType();
		 * addressType.setAddressType(AddressType.RECEIVING_OFFICER_ADDRESS
		 * .value()); List<PmAddressType> pmAddressTypes = new
		 * ArrayList<PmAddressType>(); pmAddressTypes.add(addressType);
		 * 
		 * if (getManagedBean().getOrgType().equalsIgnoreCase(
		 * OrganizationType.DIVISION.value())) { List<PmAddressType>
		 * deliveryAddress = getManagedBean() .getDeliveryAddressTypeList();
		 * 
		 * 
		 * for (PmAddressType addType : deliveryAddress) {
		 * addType.setAddressType(AddressType.DELIVERY_ADDRESS.value());
		 * addType.setPmOrgProfile(orgProfile); }
		 * pmAddressTypes.addAll(deliveryAddress); } PmAddress address =
		 * getManagedBean().getOfficeAddress(); PmContact contact =
		 * getManagedBean().getOfficeContactPerson();
		 * contact.setPmAddressType(addressType); List<PmContact> contactLst =
		 * new ArrayList<PmContact>(); contactLst.add(contact);
		 * addressType.setPmAddress(address);
		 * addressType.setPmContacts(contactLst);
		 * addressType.setPmOrgProfile(orgProfile);
		 * orgProfile.setPmAddressTypes(pmAddressTypes);
		 */

		orgProfile.setPmOrgValidity(orgValidity);
		reqDTO.setPmOrgProfile(orgProfile);

		OrgProfileResDTO resDTO = orgProfileSO.saveOrUpdateOrgProfile(reqDTO);

		if (resDTO.isSuccessful()) {
			result = resDTO.isSuccessful();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Org successfully.", "Org Name : "
									+ this.getManagedBean().getOrgValidity()
											.getOrgName()));
		}

		return "/xhtml/pm/organisation/maintainOrganisation.xhtml";

	}

	public String addOrganisation() {
		if (getManagedBean().getOrgProfile().getOrgProfileId() == null) {
			getManagedBean().setOrgProfile(new PmOrgProfile());
		}
		retrieveCountryList();
		retrievePmZoneList();
		
		reset();

		return "/xhtml/pm/organisation/maintainOrganisation";
	}

	public void resetOrgProfile() {
		getManagedBean().setOrgProfile(new PmOrgProfile());
		getManagedBean().setOrgValidity(new PmOrgValidity());
		getManagedBean().setOfficeAddress(new PmAddress());
		getManagedBean().setOfficeContactPerson(new PmContact());
		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryReqDTO countryReqDTO = new CountryReqDTO();
		countryReqDTO.setCountryCode("MY");
		getManagedBean().getOfficeAddress().setPmCountry(
				countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());
		getManagedBean().getOfficeAddress().setPmState(new PmState());
		getManagedBean().getOfficeAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getOfficeAddress().setPmCity(new PmCity());
		getManagedBean().getOfficeAddress().setPmDivision(new PmDivision());

	}

	public void okContacePersonAction() {

		// BO validation
		RequestContext.getCurrentInstance()
				.execute("contactPersonPopup.hide()");
	}

	private void retrievePmZoneList() {
		IZoneSO zoneSO = (IZoneSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IZoneSO.class);
		ZoneResDTO resDTO = zoneSO.retrieveZoneList();
		this.getManagedBean().setZoneList(resDTO.getZoneList());
	}

	public void retrieveCountryList() {

		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();
		getManagedBean().setCountryList(resDTO.getCountryList());
	}

	public void retrieveStateList(AjaxBehaviorEvent e) {
		Long countryIdId = (Long) e.getComponent().getAttributes().get("value");
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryIdId);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStateList(resDTO.getStateList());
	}

	public void retrieveDivisionList(AjaxBehaviorEvent e) {
		Long stateId = (Long) e.getComponent().getAttributes().get("value");
		DivisionReqDTO reqDTO = new DivisionReqDTO();
		reqDTO.setStateId(stateId);
		IDivisionSO reqSO = (IDivisionSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDivisionSO.class);
		DivisionResDTO resDTO = reqSO.retriveDivisionListByState(reqDTO);
		getManagedBean().setDivisionList(resDTO.getDivisionList());

		if (resDTO.getDivisionList().size() == 0) {
			DistrictReqDTO districtReqDTO = new DistrictReqDTO();
			districtReqDTO.setStateId(stateId);
			IDistrictSO districtSO = (IDistrictSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IDistrictSO.class);
			DistrictResDTO districtResDTO = districtSO
					.retriveDistrictListByStateID(districtReqDTO);
			getManagedBean().setDistrictList(districtResDTO.getDistrictList());
		}
	}

	public void retrieveDistrictList(AjaxBehaviorEvent e) {
		Long divisionId = (Long) e.getComponent().getAttributes().get("value");
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setDivisionId(divisionId);
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByDivision(reqDTO);
		getManagedBean().setDistrictList(resDTO.getDistrictList());

	}

	public void retrieveCityList(AjaxBehaviorEvent e) {
		Long districtId = (Long) e.getComponent().getAttributes().get("value");
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(districtId);
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICitySO.class);
		CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
		this.getManagedBean().setCityList(resDTO.getCityList());
	}

	public void assignNewAddress() {
		PmAddressType addressType = new PmAddressType();
		addressType.setPmAddress(new PmAddress());
		addressType.setAddressType(AddressType.DELIVERY_ADDRESS.value());
		getManagedBean().setSelectedDeliveryAddressType(addressType);
		addressType.setPmOrgProfile(getManagedBean().getOrgProfile());

	}

	public void deleteSelectedAdddress() {
		if (getManagedBean().getSelectedDelAddressTypes().size() > 0) {
			for (PmAddressType address : getManagedBean()
					.getSelectedDelAddressTypes()) {
				getManagedBean().getOrgProfile().getPmAddressTypes()
						.remove(address);
			}
			getManagedBean().getSelectedDelAddressTypes().clear();
		} else {
			System.out.println("Error");
		}
	}

	public SelectItem[] getOrgTypeDropdownValues(String itemType) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add("SYS");
		recommendList.add("FJA");
		recommendList.add("FFC");
		recommendList.add("SUP");
		recommendList.add("OTH");

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		searchBean.getSortColumns();

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.RECORD_STATUS, searchBean);
		return items;
	}

	private void retrievePtjGroupList() {
		IPtjGroupSO ptjGroupSO = (IPtjGroupSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IPtjGroupSO.class);
		PtjGroupResDTO resDTO = ptjGroupSO.retrievePtjGroupList();
		this.getManagedBean().setPtjGroup(resDTO.getPtjGroup());
	}

	private void retrieveAgOfficeList() {
		IAgOfficeSO agOfficeSO = (IAgOfficeSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IAgOfficeSO.class);
		AgOfficeResDTO resDTO = agOfficeSO.retrieveAgOfficeList();

		this.getManagedBean().setAgOffice(resDTO.getAgOfficeList());
	}

	private void retrieveOrgProfileList() {
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(1L);
		OrgProfileResDTO resDTO = orgProfileSO
				.retrieveOrgProfileListByMinistry(reqDTO);

		this.getManagedBean().setPmOrgProfile(resDTO.getOrgProfileList());
	}

	private void retrieveFinancialOrgList() {
		IFinancialOrgSO finOrgProfileSO = (IFinancialOrgSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IFinancialOrgSO.class);

		FinancialOrgResDTO resDTO = finOrgProfileSO.retrieveFinancialOrgList();

		this.getManagedBean().setPmFinancialOrg(resDTO.getFinancialOrgList());
	}

	public void addFinancialAcc() {

		PmFinancialAcc pmFinancialAcc = new PmFinancialAcc();

		try {
			List<PmFinancialAcc> pmFinancialList = this.getManagedBean()
					.getPmFinancialAcc();
			if (null == pmFinancialList) {
				pmFinancialList = new ArrayList<PmFinancialAcc>();
			}

			for (PmFinancialAcc financialAcc : pmFinancialList) {
				financialAcc.setBrandNew(false);
			}
			pmFinancialList.add(pmFinancialAcc);
			this.getManagedBean().setPmFinancialAcc(pmFinancialList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void deleteFinancialAcc() {

		List<PmFinancialAcc> financialAccList = this.getManagedBean()
				.getPmFinancialAcc();
		List<PmFinancialAcc> selectedFinancialActList = this.getManagedBean()
				.getSelectedPmFinancialAcc();
		if (financialAccList.size() == selectedFinancialActList.size()) {
			financialAccList.clear();
		} else {
			for (PmFinancialAcc financialAcc : selectedFinancialActList) {
				financialAccList.remove(financialAcc);
			}
		}
	}
	public void addNewPtjAction() {

		List<PmOrgProfile> orgProfileList = this.getManagedBean().getOrgProfileList();
		if (orgProfileList == null) {
			orgProfileList = new ArrayList<PmOrgProfile>();
			this.getManagedBean().setOrgProfileList(orgProfileList);
		}

		PmOrgProfile profile = new PmOrgProfile();

		profile.setPmOrgValidity(new PmOrgValidity());
		orgProfileList.add(profile);

	}

	public void deletePtjAction() {

		List<PmOrgProfile> openList = this.getManagedBean().getOrgProfileList();

		if (null == openList) {
			openList = new ArrayList<PmOrgProfile>();
		}

		List<PmOrgProfile> openSelectList = this.getManagedBean()
				.getSelectedPmOrgProfile();
		if (null == openSelectList) {
			openSelectList = new ArrayList<PmOrgProfile>();
		}

		if (openList.size() == openSelectList.size()) {
			openList.clear();
		} else {
			for (PmOrgProfile category : openSelectList)
				openList.remove(category);
		}

	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	/**
	 * @return the searchOrganisation
	 */
	public IPagingDataModel<PmOrgProfile> getSearchOrganisation() {
		return searchOrganisation;
	}

	/**
	 * @param searchOrganisation
	 *            the searchOrganisation to set
	 */
	public void setSearchOrganisation(
			IPagingDataModel<PmOrgProfile> searchOrganisation) {
		this.searchOrganisation = searchOrganisation;
	}

	/**
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

}
