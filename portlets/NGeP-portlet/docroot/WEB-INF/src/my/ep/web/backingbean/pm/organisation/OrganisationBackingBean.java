package my.ep.web.backingbean.pm.organisation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.agoffice.IAgOfficeSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.ptjgroup.PtjGroupResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.zone.ZoneResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmProfileGroup;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.OrganisationSearchBean;
import my.ep.web.managedbean.pm.organisation.OrganisationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

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
	// Default value
	public static final boolean SEARCH_DEFAULT = false;
	// Tab values
	public static final int PROFILE_DETAIL = 0;
	public static final int GOV_SELLER = 1;
	public static final int RESPONSIBLE_PROFILE = 2;
	public static final String NAV_TO_SEARCH = "organisationMain";
	public static final String NAV_TO_MAINTAIN = "maintainOrganisation";

	public OrganisationBackingBean() {

	}

	public void preLoadingOrg() {

		this.resetOrganisationSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	public String redirectToOrganisation(Long id) throws Exception {
		retrieveCountryList();
		retrieveFinancialOrgList();

		if (id != null && id > 0) {
			maintainOrganisation(id);

		} else {
			initialOrganisation();

		}
		getManagedBean().setShowParentMsg(true);
		return NAV_TO_MAINTAIN;
	}

	public int getProfileDetails() {
		return PROFILE_DETAIL;
	}

	public int getGovernmentSeller() {
		return GOV_SELLER;
	}

	public int getResponsibleProfile() {
		return RESPONSIBLE_PROFILE;
	}

	public String backToSearch() {

		return NAV_TO_SEARCH;
	}

	public void initialOrganisation() {
		this.getManagedBean().setSelectedTabIndex(PROFILE_DETAIL);
		getManagedBean().setOrgProfile(new PmOrgProfile());

		retrievePmZoneList();
		getManagedBean().setOrgProfile(new PmOrgProfile());
		getManagedBean().setOrgValidity(new PmOrgValidity());
		getManagedBean().setOfficeAddress(new PmAddress());
		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryReqDTO countryReqDTO = new CountryReqDTO();
		countryReqDTO.setCountryCode("MY");
		getManagedBean().getOfficeAddress().setPmCountry(
				countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());

		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(getManagedBean().getOfficeAddress().getPmCountry()
				.getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStateList(resDTO.getStateList());
		getManagedBean().getOfficeAddress().setPmCountry(new PmCountry());
		getManagedBean().getOfficeAddress().setPmState(new PmState());
		getManagedBean().getOfficeAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getOfficeAddress().setPmCity(new PmCity());
		getManagedBean().setContactList(new ArrayList<PmContact>());
		getManagedBean().setOfficeContactPerson(new PmContact());
		getManagedBean().getOrgProfile().setPmZone(new PmZone());
		getManagedBean().setOrgType(null);
		getManagedBean().getOrgProfile().setRecordStatusFlg(null);
		getManagedBean().setPmFinancialAcc(new ArrayList<PmFinancialAcc>());
		getManagedBean().setProfileGroup(new ArrayList<PmProfileGroup>());

	}

	public void searchOrganisation() {

		searchOrganisation.reloadData();
	}

	private ISearchBean<PmOrgProfile> getSearchBeanData(
			ISearchBean<PmOrgProfile> searchBean) {
		log.info("getSearchBeanData - Start");
		OrganisationSearchBean oSearchBean = this.getManagedBean()
				.getOrganisationSearchBean();
		boolean isSearchable = SEARCH_DEFAULT;
		boolean isEmpty = true;

		if (oSearchBean.getOrgTypeCode() != null
				&& oSearchBean.getOrgTypeCode().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("orgTypeCode.parameterId",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getOrganisationSearchBean()
									.getOrgTypeCode()));
			isEmpty = false;
		}

		if (oSearchBean.getOrgCode() != null
				&& oSearchBean.getOrgCode().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmOrgValidity.orgCode",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getOrganisationSearchBean()
									.getOrgCode(), true));
			isEmpty = false;
		}
		if (oSearchBean.getOrgName() != null
				&& oSearchBean.getOrgName().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmOrgValidity.orgName",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getOrganisationSearchBean()
									.getOrgName(), true));
			isEmpty = false;

		}
		// if (isEmpty && !isSearchable) {
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS.value(), oSearchBean
								.getOrgStatus()));

		// }
		log.info("getSearchBeanData - End");
		return searchBean;
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
				ISearchBean<PmOrgProfile> searchBeanData = getSearchBeanData(searchBean);
				OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();

				orgProfileReqDTO.setOrgProfileSearchBean(searchBeanData);
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

	public String maintainOrganisation(Long id) {
		this.getManagedBean().setSelectedTabIndex(PROFILE_DETAIL);
		retrieveSupplierUserList(id);

		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(id);
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);

		OrgProfileResDTO orgResDTO = orgProfileSO
				.retrieveOrgDetailsByOrgId(reqDTO);
		PmOrgProfile orgProfile = orgResDTO.getPmOrgProfile();

		getManagedBean().setOrgType(
				orgProfile.getOrgTypeCode().getParameterCode());
		// BPK Sabah sarawak
		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.BPK_PEROLEHAN_PM_SABAH.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value())) {
			retrievePmZoneList();
			getManagedBean().setZoneId(orgProfile.getPmZone().getZoneId());

		}
		// PTJ
		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.DIVISION.value())) {
			retrieveAgOfficeList();
			retrievePtjGroupList();
			retrieveOrgProfileList(orgProfile.getParentOrgProfile()
					.getOrgProfileId());
			if (orgProfile.getPmPtjGroup().getPtjGroupId() != null) {
				getManagedBean().setPtjGroupId(
						orgProfile.getPmPtjGroup().getPtjGroupId());
			}
			if (orgProfile.getPmAgOffice().getAgOfficeId() != null) {
				getManagedBean().setAgOfficeId(
						orgProfile.getPmAgOffice().getAgOfficeId());
			}
			if (orgProfile.getParentOrgProfile().getOrgProfileId() != null) {
				getManagedBean().setParentOrgProfileId(
						orgProfile.getParentOrgProfile().getOrgProfileId());
			}
			
		//	this.getManagedBean().setProfileGroup(orgProfile.getAuthorizedProfileGroups());
		//	this.getManagedBean().setSelectedProfileGroup(orgProfile.getAuthorizedProfileGroups().get(0));
			
			// Org Responsble
						/*List<PmProfileGroup> profileGroup = orgProfile
								.getAuthorizedProfileGroups();
						if (profileGroup != null && !profileGroup.isEmpty()) {
							for (PmProfileGroup profileGrp : profileGroup) {
								getManagedBean().setAuthOrgProfile(profileGrp.getAuthorizedOrgProfile());
							}
							
						}*/
		}
		List<PmAddressType> pmAddressTypes = orgProfile.getPmAddressTypes();
		boolean addrExist = false;
		if (pmAddressTypes != null && !pmAddressTypes.isEmpty()) {
			for (PmAddressType pmAddressType : pmAddressTypes) {

				if (pmAddressType.getAddressType().equalsIgnoreCase(
						AddressType.BILLING_ADDRESS.value())) {
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

					
					this.getManagedBean().setOfficeAddress(
							pmAddressType.getPmAddress());
					addrExist = true;
					}
				if (pmAddressType.getAddressType().equalsIgnoreCase(
						AddressType.DELIVERY_ADDRESS.value())) {
					/*
					 * this.getManagedBean().setDeliveryAddress(pmAddressType.
					 * getPmAddress()); List<PmAddress> pmAddress = new
					 * ArrayList<PmAddress>();
					 * pmAddress.add(pmAddressType.getPmAddress());
					 * this.getManagedBean().setDeliveryAddresList(pmAddress);
					 * System.out.println("Delivery Address" +
					 * pmAddressType.getPmAddress().getAddress1());
					 */

				}

				if (!pmAddressType.getPmContacts().isEmpty()) {
					this.getManagedBean().setOfficeContactPerson(
							pmAddressType.getPmContacts().get(0));

					// pmAddressType.setPmContacts(pmAddressType.getPmContacts());
				} else {

					List<PmContact> contactLst = getManagedBean()
							.getContactList();
					getManagedBean().setOfficeContactPerson(new PmContact());
					PmContact contact = getManagedBean()
							.getOfficeContactPerson();
					contact.setPmAddressType(pmAddressType);
					contactLst.add(contact);
					pmAddressType.setPmContacts(contactLst);

				}

			}

		}
		if (!addrExist) {
			PmAddressType addressType = getManagedBean().getPmAddressType();
			pmAddressTypes.add(addressType);
			addressType.setAddressType(AddressType.BILLING_ADDRESS.value());
			getManagedBean().setOfficeAddress(setDefaultAddr());
			PmAddress address = getManagedBean().getOfficeAddress();
			addressType.setPmAddress(address);
			List<PmContact> contactLst = getManagedBean().getContactList();
			getManagedBean().setOfficeContactPerson(new PmContact());
			PmContact contact = getManagedBean().getOfficeContactPerson();
			contact.setPmAddressType(addressType);
			contactLst.add(contact);
			addressType.setPmContacts(contactLst);
			addressType.setPmOrgProfile(orgProfile);
			orgProfile.setPmAddressTypes(pmAddressTypes);

		}
		this.getManagedBean().setOrgValidity(
				orgResDTO.getPmOrgProfile().getPmOrgValidity());
		this.getManagedBean().setOrgProfile(orgResDTO.getPmOrgProfile());

		return NAV_TO_SEARCH;

	}

	public String saveOrganization() throws Exception {

		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();

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
		PmOrgValidity orgValidity = this.getManagedBean().getOrgValidity();
		List<PmProfileGroup> profileGroup = new ArrayList<PmProfileGroup>();
		if (orgProfile.isBrandNew()) {
			orgProfile.setOrgTypeCode(parameterResDTO.getPmParameter());

			orgValidity.setEffDate(new Date());
			// orgValidity.setExpDate(new Date());

			if (parameterResDTO
					.getPmParameter()
					.getParameterCode()
					.equalsIgnoreCase(
							OrganizationType.BPK_PEROLEHAN_PM_SABAH.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value())) {

				PmZone zone = getManagedBean().getZone();
				zone.setZoneId(getManagedBean().getZoneId());
				orgProfile.setPmZone(zone);

			}
			PmAddressType addressType = this.getManagedBean()
					.getPmAddressType();
			addressType.setAddressType(AddressType.BILLING_ADDRESS.value());
			List<PmAddressType> pmAddressTypes = this.getManagedBean()
					.getOfficeAddressTypeList();
			pmAddressTypes.add(addressType);
			PmAddress address = this.getManagedBean().getOfficeAddress();
			PmContact contact = this.getManagedBean().getOfficeContactPerson();
			contact.setPmAddressType(addressType);
			List<PmContact> contactLst = this.getManagedBean().getContactList();
			contactLst.add(contact);
			addressType.setPmContacts(contactLst);
			addressType.setPmAddress(address);
			addressType.setPmOrgProfile(orgProfile);
			orgProfile.setPmAddressTypes(pmAddressTypes);
			

			orgValidity.setPmOrgProfile(orgProfile);
		}
		if (getManagedBean().getOrgProfile().isGovtSeller()) {
			IRegistrationSO registrationSO = (IRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT,
							IRegistrationSO.class);
			RegistrationReqDTO regReqDTO = new RegistrationReqDTO();
			PmFinancialOrg bank = new PmFinancialOrg();
			bank.setFinancialOrgId(1L);
			regReqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
			PmFinancialOrg financialOrg = getManagedBean()
					.getFinancialOrg();

			if (!getManagedBean().getPmFinancialAcc().isEmpty()) {
				for (PmFinancialAcc financialAcc : this.getManagedBean()
						.getPmFinancialAcc()) {
					financialAcc.setPmFinancialOrg(financialOrg);
					regReqDTO.setBankAccountNo(financialAcc.getAccountNo());

				}
			}
			RegistrationResDTO regResDTO = registrationSO
					.createPTJSupplier(regReqDTO);
			regResDTO.setSuccessful(true);

		}
		if (parameterResDTO.getPmParameter().getParameterCode()
				.equalsIgnoreCase(OrganizationType.DIVISION.value())) {
			orgProfile.getParentOrgProfile().setOrgProfileId(
					getManagedBean().getParentOrgProfileId());
			if (getManagedBean().getOrgProfile().isGroupOrg()) {

				List<PmProfileGroup> profileGrp = getManagedBean()
						.getProfileGroup();
				PmOrgProfile pmorg = getManagedBean().getOrgProfile();

				for (PmProfileGroup proGroup : profileGrp) {
					proGroup.setMainOrgProfile(orgProfile);
					proGroup.setAuthorizedOrgProfile(pmorg);
				}
				profileGroup.addAll(profileGrp);
				orgProfile.setMainProfileGroups(profileGroup);
			}
		}
		
		orgProfile.setPmOrgValidity(orgValidity);
		reqDTO.setPmOrgProfile(orgProfile);

		OrgProfileResDTO resDTO = orgProfileSO.saveOrUpdateOrgProfile(reqDTO);

		if (resDTO.isSuccessful()) {
			result = resDTO.isSuccessful();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Org Name : ",
							this.getManagedBean().getOrgValidity().getOrgName()
									+ "Submit successfully."));
		}

		return NAV_TO_SEARCH;

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

		// getManagedBean().getOfficeAddress().setPmDivision(new PmDivision());

		IParameterSO parameterSO = (IParameterSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterId(getManagedBean().getOrgTypeId());

		ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);

		this.getManagedBean().setOrgType(
				resDTO.getPmParameter().getParameterCode());

	}

	public void addContacePerson() {

		RequestContext.getCurrentInstance()
				.execute("contactPersonPopup.show()");
		getManagedBean().setShowParentMsg(false);
	}

	public void okContacePerson() {

		RequestContext.getCurrentInstance()
				.execute("contactPersonPopup.hide()");
		getManagedBean().setShowParentMsg(true);
	}

	public void closeContacePerson() {

		RequestContext.getCurrentInstance()
				.execute("contactPersonPopup.hide()");
		getManagedBean().setShowParentMsg(true);
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

		PmAddressType deliveryAddr = null;
		PmAddress manualAddr = new PmAddress();
		manualAddr = getManagedBean().getPmAddressType().getPmAddress();
		deliveryAddr = new PmAddressType();
		// deliveryAddr.setPmOrgProfile(getManagedBean().getOrgProfile());
		deliveryAddr.setPmAddress(manualAddr);

		getManagedBean().setSelectedDeliveryAddress(deliveryAddr);
		getManagedBean().getOrgProfile().getPmAddressTypes().add(deliveryAddr);
		// selectAddrEvent();

	}

	public void addDeliveryAddr() {

		getManagedBean().setDeliveryAddress(new PmAddressType());
		getManagedBean().getDeliveryAddress().setAddressTypeId(1L);
		getManagedBean().getDeliveryAddress().setPmAddress(new PmAddress());
		// default to Malaysia
		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryReqDTO countryReqDTO = new CountryReqDTO();
		countryReqDTO.setCountryCode("MY");
		getManagedBean()
				.getDeliveryAddress()
				.getPmAddress()
				.setPmCountry(
						countrySO.retrieveCountryByCode(countryReqDTO)
								.getPmCountry());
		getManagedBean().getDeliveryAddress().getPmAddress()
				.setPmState(new PmState());
		getManagedBean().getDeliveryAddress().getPmAddress()
				.setPmDistrict(new PmDistrict());
		getManagedBean().getDeliveryAddress().getPmAddress()
				.setPmCity(new PmCity());
		getManagedBean().getDeliveryAddress().getPmAddress()
				.setPmDivision(new PmDivision());

		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(getManagedBean().getDeliveryAddress()
				.getPmAddress().getPmCountry().getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStateList(resDTO.getStateList());

		if (getManagedBean().getAddressTypesList() == null) {
			AddressReqDTO addReqDTO = new AddressReqDTO();

			addReqDTO.setAddressType(AddressType.DELIVERY_ADDRESS.value());
			IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IAddressSO.class);
			AddressResDTO addResDTO = addressSO
					.retrieveAddressTypeList(addReqDTO);
			getManagedBean()
					.setAddressTypesList(addResDTO.getAddressTypeList());
		}
		if (getManagedBean().getManualAddressMap() == null
				|| getManagedBean().getManualAddressMap().size() == 0) {
			setManualAddressList();
		}

	}
	public void editDeliveryAddress(PmAddress address) {

		
		System.out.println("Address Type" + address.getAddress1());
		//this.getManagedBean().setDeliveryAddress(address);
		
		StateReqDTO stateReqDTO = new StateReqDTO();
		System.out.println("CountRy ID:"
				+ address.getPmCountry().getCountryId());
		stateReqDTO.setCountryId(address.getPmCountry().getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(stateReqDTO);
		getManagedBean().setStateList(resDTO.getStateList());

		DistrictReqDTO districtReqDTO = new DistrictReqDTO();
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);

		districtReqDTO.setStateId(address.getPmState().getStateId());
		DistrictResDTO districtResDTO = districtSO
				.retriveDistrictListByStateID(districtReqDTO);
		getManagedBean().setDistrictList(districtResDTO.getDistrictList());

		CityReqDTO cityReqDTO = new CityReqDTO();
		cityReqDTO.setDistrictId(address.getPmDistrict().getDistrictId());
		ICitySO citySO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICitySO.class);
		CityResDTO cityResDTO = citySO.retriveCityListByDistrictId(cityReqDTO);
		this.getManagedBean().setCityList(cityResDTO.getCityList());
		// pmAddressType.setEditable(true);
		System.out.println("AddressSize" + address.getAddress1());

	}
	private void setManualAddressList() {
		Map<Long, List<PmAddressType>> addressTypeMap = new HashMap<Long, List<PmAddressType>>();
		if (getManagedBean().getAddressTypesList() == null
				|| getManagedBean().getAddressTypesList().size() == 0) {
			orgDeliveryAddrList();
		}
		for (PmAddressType addressType : getManagedBean().getAddressTypesList()) {
			Long key = addressType.getPmAddress().getPmDistrict()
					.getDistrictId();
			if (!addressTypeMap.containsKey(key)) {
				addressTypeMap.put(key, new ArrayList<PmAddressType>());
			}
			addressTypeMap.get(key).add(addressType);
		}
		getManagedBean().setManualAddressMap(addressTypeMap);
	}

	public void orgDeliveryAddrList() {
		AddressReqDTO reqDTO = new AddressReqDTO();
		// reqDTO.setOrgProfileId(getManagedBean().getFulfilmentRequest().getPreparedOrgProfile().getOrgProfileId());
		reqDTO.setAddressType(AddressType.DELIVERY_ADDRESS.value());
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IAddressSO.class);
		AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
		getManagedBean().setAddressTypesList(resDTO.getAddressTypeList());

	}

	public void deleteSelectedAdddress() {

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
				ParameterTypeConst.ORGANISATION_TYPES, searchBean);
		return items;
	}

	public SelectItem[] getNewOrgTypeDropdownValues(String itemType) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add("SYS");
		recommendList.add("FJA");
		recommendList.add("FFC");
		recommendList.add("SUP");
		recommendList.add("OTH");
		recommendList.add("FPJ");
		recommendList.add("SPT");

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		searchBean.getSortColumns();

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.ORGANISATION_TYPES, searchBean);
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

	private void retrieveOrgProfileList(Long id) {
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(id);
		OrgProfileResDTO resDTO = orgProfileSO
				.retrieveOrgProfileListByMinistry(reqDTO);

		this.getManagedBean().setOrgProfileList(resDTO.getOrgProfileList());
	}

	private void retrieveFinancialOrgList() {
		IFinancialOrgSO financialOrgSO = (IFinancialOrgSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IFinancialOrgSO.class);
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PAYMENT_BANK);

		FinancialOrgResDTO resDTO = financialOrgSO
				.retrieveFinancialOrgListByServiceType(reqDTO);

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

	private PmAddress setDefaultAddr() {
		PmAddress addr = new PmAddress();
		addr.setPmCity(new PmCity());
		addr.setPmCountry(new PmCountry());
		addr.setPmState(new PmState());
		addr.setPmDistrict(new PmDistrict());
		return addr;
	}

	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getStatusOptionsDefault();
	}

	public void updateOrgCode(PmOrgProfile pmOrgProfile, int index) {

		boolean isValid = true;
		if (pmOrgProfile != null && pmOrgProfile.getOrgProfileId() != null) {
			if (this.getManagedBean().getSelectedProfileGroups() != null
					&& !this.getManagedBean().getSelectedProfileGroups()
							.isEmpty()) {
				int i = 0;
				for (PmProfileGroup pmUserOrg : this.getManagedBean()
						.getSelectedProfileGroups()) {
					if (pmUserOrg.getMainOrgProfile().getOrgProfileId()
							.equals(pmOrgProfile.getOrgProfileId())
							&& i != index) {
						isValid = false;
						break;
					}
					i++;
				}
				if (!isValid) {
					this.getManagedBean().getSelectedProfileGroups().get(index)
							.getAuthorizedOrgProfile()
							.setPmOrgValidity(new PmOrgValidity());
					throw new EpException(MessageConst.ERROR_770019);
				}

			}
		}
	}

	public void addOrgUnit() {

		PmProfileGroup pmPtjGroupList = new PmProfileGroup();

		try {
			List<PmProfileGroup> ptjList = this.getManagedBean()
					.getProfileGroup();
			if (null == ptjList) {
				ptjList = new ArrayList<PmProfileGroup>();
			}

			for (PmProfileGroup pmPtj : ptjList) {
				pmPtj.setBrandNew(false);
			}
			ptjList.add(pmPtjGroupList);
			this.getManagedBean().setProfileGroup(ptjList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}

	}

	public void deleteOrgUnit(PmProfileGroup pmProfileGroup) {

		if (pmProfileGroup != null && pmProfileGroup.isBrandNew()) {
			if (this.getManagedBean().getProfileGroup() == null) {
				this.getManagedBean().setProfileGroup(
						new ArrayList<PmProfileGroup>());
			}
			this.getManagedBean().getProfileGroup().remove(pmProfileGroup);
		}
	}

	private void retrieveSupplierUserList(Long id) {
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setOrgProfileId(id);

		UserResDTO resDTO = userSO.retrieveUserListByOrgProfileId(reqDTO);

		this.getManagedBean().setSupplierUserList(resDTO.getUserList());
	}

	public void addUser() {

		if (getManagedBean().getUser() == null) {
			this.getManagedBean().setUser(new PmUser());
		}
		if (this.getManagedBean().getSupplierUsers() != null
				&& !this.getManagedBean().getSupplierUsers().isEmpty()) {
			this.getManagedBean().setSupplierUsers(null);
		}

	}

	public void deleteUser() {

		List<PmUser> userList = getManagedBean().getSelectedSupplierUsers();
		List<PmUser> selectedUserList = getManagedBean().getSelectedUsers();
		for (PmUser usrList : userList) {
			if (usrList.isSelected()) {
				selectedUserList.add(usrList);
			}
		}
		if (userList.size() == selectedUserList.size()) {
			userList.clear();
		} else {
			for (PmUser user : selectedUserList) {
				userList.remove(user);
			}
		}
	}

	public void conformUsers() {

		List<PmUser> selectedUserList = getManagedBean()
				.getSelectedSupplierUsers();
		this.getManagedBean().setSupplierUsers(selectedUserList);

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
	 * reset search bean fields
	 */
	public void resetOrganisationSearchBean() {
		log.info("resetOrgSearchBean");
		this.getManagedBean().setOrganisationSearchBean(
				new OrganisationSearchBean());
		searchOrganisation = new SearchOrgPagingDataModel();
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
