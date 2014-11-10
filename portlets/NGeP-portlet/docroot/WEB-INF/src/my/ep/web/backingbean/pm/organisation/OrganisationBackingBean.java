package my.ep.web.backingbean.pm.organisation;

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
import my.ep.biz.service.pm.authorization.user.IUserSO;
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
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.ptjgroup.PtjGroupResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
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
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmPtjGroup;
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

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

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

	public OrganisationBackingBean() {
		retrieveCountryList();
		retrieveAgOfficeList();
		retrievePtjGroupList();
		retrieveOrgProfileList();
		retrieveFinancialOrgList();

	}

	public void preLoading() {
		log.info("preLoading");
		// load critical mappers

		this.resetOrganisationSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_SEARCH);
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
		// getManagedBean().getOfficeAddress().setPmDivision(new PmDivision());
		// getManagedBean().getOfficeAddress().getPmDivision().setDivisionId(1L);
		getManagedBean().setDeliveryAddress(new PmAddress());
		getManagedBean().getDeliveryAddress().setPmCountry(new PmCountry());
		getManagedBean().setDeliveryContactPerson(new PmContact());
		getManagedBean().getDeliveryAddress().setPmState(new PmState());
		getManagedBean().getDeliveryAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getDeliveryAddress().setPmCity(new PmCity());
		// getManagedBean().getDeliveryAddress().setPmDivision(new
		// PmDivision());
		getManagedBean().setOfficeContactPerson(new PmContact());
		getManagedBean().getOrgProfile().setPmZone(new PmZone());
		getManagedBean().setOrgType(null);
		getManagedBean().getOrgProfile().setRecordStatusFlg(null);
		getManagedBean().setPmFinancialAcc(new ArrayList<PmFinancialAcc>());
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
					new SearchFilterBean("orgTypeCode.parameterCode",
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
		if(isEmpty && !isSearchable){
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmOrgValidity.recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getOrganisationSearchBean()
									.getOrgStatus()));
		}

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

	public String editOrganization(PmOrgProfile orgProfile) {
		this.getManagedBean().setSelectedTabIndex(PROFILE_DETAIL);
		retrieveSupplierUserList(orgProfile);
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

		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.BPK_PEROLEHAN_PM_SABAH.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value())) {
			retrievePmZoneList();
			getManagedBean().setZoneId(orgProfile.getPmZone().getZoneId());

		}
		List<PmAddressType> pmAddressTypes = orgProfile.getPmAddressTypes();
		boolean addrExist = false;
		if (pmAddressTypes != null && !pmAddressTypes.isEmpty()) {
			for (PmAddressType pmAddressType : pmAddressTypes) {
				System.out.println("AddressType Id"
						+ pmAddressType.getPmAddress().getAddress1());
				if (pmAddressType.getAddressType().equalsIgnoreCase(
						AddressType.RECEIVING_OFFICER_ADDRESS.value())) {

					retrieveCountryList();

					/*
					 * if (pmAddressType.getPmAddress().getPmDivision() == null)
					 * { pmAddressType.getPmAddress().setPmDivision( new
					 * PmDivision()); }
					 */

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

					/*
					 * if (pmAddressType.getPmAddress().getPmDivision()
					 * .getDivisionId() != null) { DivisionReqDTO divReqDTO =
					 * new DivisionReqDTO();
					 * divReqDTO.setStateId(pmAddressType.getPmAddress()
					 * .getPmState().getStateId()); IDivisionSO divisionSO =
					 * (IDivisionSO) BusinessDelegate
					 * .getInstance().getServiceObject(
					 * ModuleCodeConst.PROFILE_MANAGEMENT, IDivisionSO.class);
					 * DivisionResDTO divResDTO = divisionSO
					 * .retriveDivisionListByState(divReqDTO);
					 * getManagedBean().setDivisionList(
					 * divResDTO.getDivisionList()); districtReqDTO
					 * .setDivisionId(pmAddressType.getPmAddress()
					 * .getPmDivision().getDivisionId()); DistrictResDTO
					 * districtResDTO = districtSO
					 * .retriveDistrictListByDivision(districtReqDTO);
					 * getManagedBean().setDistrictList(
					 * districtResDTO.getDistrictList()); } else {
					 * districtReqDTO.setStateId(pmAddressType.getPmAddress()
					 * .getPmState().getStateId()); DistrictResDTO
					 * districtResDTO = districtSO
					 * .retriveDistrictListByStateID(districtReqDTO);
					 * getManagedBean().setDistrictList(
					 * districtResDTO.getDistrictList()); }
					 */

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

					// pmAddressType.setEditable(true);
					this.getManagedBean().setOfficeAddress(
							pmAddressType.getPmAddress());
					addrExist = true;
					this.getManagedBean().setDeliveryAddress(
							pmAddressType.getPmAddress());

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
			addressType.setAddressType(AddressType.RECEIVING_OFFICER_ADDRESS
					.value());
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
			getManagedBean().setDeliveryAddress(setDefaultAddr());
		}
		this.getManagedBean().setOrgValidity(
				orgResDTO.getPmOrgProfile().getPmOrgValidity());
		this.getManagedBean().setOrgProfile(orgResDTO.getPmOrgProfile());

		return "/xhtml/pm/organisation/maintainOrganisation.xhtml";

	}

	public void editDeliveryAddress(PmAddress address) {

		retrieveCountryList();
		System.out.println("Address Type" + address.getAddress1());

		StateReqDTO stateReqDTO = new StateReqDTO();
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

	public String saveOrganization() throws Exception {

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
		PmOrgValidity orgValidity = this.getManagedBean().getOrgValidity();
		if (orgProfile.isBrandNew()) {
			orgProfile.setOrgTypeCode(parameterResDTO.getPmParameter());

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
				IRegistrationSO registrationSO = (IRegistrationSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IRegistrationSO.class);
				RegistrationReqDTO regReqDTO = new RegistrationReqDTO();
				regReqDTO.setBank(getManagedBean().getOrgProfile()
						.getPmFinancialOrg());
				regReqDTO.setBankAccountNo(getManagedBean().getPmFinancialAcc()
						.get(0).getAccountNo());

				RegistrationResDTO regResDTO = registrationSO
						.createPTJSupplier(regReqDTO);
				regResDTO.setSuccessful(true);

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
			PmAddressType addressType = this.getManagedBean()
					.getPmAddressType();
			addressType.setAddressType(AddressType.RECEIVING_OFFICER_ADDRESS
					.value());
			List<PmAddressType> pmAddressTypes = this.getManagedBean()
					.getOfficeAddressTypeList();
			pmAddressTypes.add(addressType);
			PmAddress address = this.getManagedBean().getOfficeAddress();
			PmContact contact = this.getManagedBean().getOfficeContactPerson();
			contact.setPmAddressType(addressType);
			List<PmContact> contactLst = this.getManagedBean().getContactList();
			contactLst.add(contact);
			System.out.println("ContactList" + contactLst.size());
			addressType.setPmContacts(contactLst);
			addressType.setPmAddress(address);
			addressType.setPmOrgProfile(orgProfile);
			orgProfile.setPmAddressTypes(pmAddressTypes);
			// orgValidity.setEffDate(new Date());

			/*
			 * PmAddressType addressType = getManagedBean().getPmAddressType();
			 * addressType.setAddressType(AddressType.RECEIVING_OFFICER_ADDRESS
			 * .value()); List<PmAddressType> pmAddressTypes = getManagedBean()
			 * .getOfficeAddressTypeList(); pmAddressTypes.add(addressType);
			 * 
			 * PmAddress address = getManagedBean().getOfficeAddress();
			 * PmContact contact = getManagedBean().getOfficeContactPerson();
			 * contact.setPmAddressType(addressType); List<PmContact> contactLst
			 * = this.getManagedBean().getContactList();
			 * contactLst.add(contact); addressType.setPmAddress(address);
			 * addressType.setPmContacts(contactLst);
			 * addressType.setPmOrgProfile(orgProfile);
			 * orgProfile.setPmAddressTypes(pmAddressTypes);
			 * orgValidity.setPmOrgProfile(orgProfile);
			 * orgProfile.setPmOrgValidity(orgValidity);
			 */
			System.out.println("ContactName"
					+ getManagedBean().getOfficeContactPerson()
							.getContactName());
			System.out.println("Contttt::"
					+ getManagedBean().getContactList().size());
		}
		reqDTO.setPmOrgProfile(orgProfile);

		OrgProfileResDTO resDTO = orgProfileSO.saveOrUpdateOrgProfile(reqDTO);
		if (getManagedBean().getOrgProfile().isGovtSeller()) {

			IRegistrationSO registrationSO = (IRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT,
							IRegistrationSO.class);
			RegistrationReqDTO regReqDTO = new RegistrationReqDTO();
			PmFinancialOrg bank = new PmFinancialOrg();
			bank.setFinancialOrgId(1L);
			regReqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
			PmFinancialOrg financialOrg = getManagedBean().getFinancialOrg();

			if (!getManagedBean().getPmFinancialAcc().isEmpty()) {
				for (PmFinancialAcc financialAcc : this.getManagedBean()
						.getPmFinancialAcc()) {

					System.out.println("Account No::"
							+ financialAcc.getAccountNo());
					financialAcc.setPmFinancialOrg(financialOrg);

				}
			}

			regReqDTO.setBank(bank);
			regReqDTO.setBankAccountNo("1134567");
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(2144L);

			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IUserSO.class);
			userReqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
			System.out.println("Org Profile ID" + orgProfile.getOrgProfileId());

			UserResDTO userResDTO = userSO
					.retrieveUserListByOrgProfileId(userReqDTO);

			regReqDTO.setPmUsers(userResDTO.getUserList());

			/*
			 * RegistrationResDTO regResDTO = registrationSO
			 * .createPTJSupplier(regReqDTO); regResDTO.setSuccessful(true);
			 */

		}
		if (getManagedBean().getOrgProfile().isGroupOrg()) {

			System.out.println("Is Gove Seller ");

		}

		if (resDTO.isSuccessful()) {
			result = resDTO.isSuccessful();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Org Name : ",
							this.getManagedBean().getOrgValidity().getOrgName()
									+ "Submit successfully."));
		}

		return "/xhtml/pm/organisation/organisationMain";

	}

	public String addOrganisation() {
		this.getManagedBean().setSelectedTabIndex(PROFILE_DETAIL);
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
		// getManagedBean().getOfficeAddress().setPmDivision(new PmDivision());

	}

	public void okContacePersonAction() {

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

	private void retrieveOrgProfileList() {
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(1L);
		OrgProfileResDTO resDTO = orgProfileSO
				.retrieveOrgProfileListByMinistry(reqDTO);

		this.getManagedBean().setOrgProfileList(resDTO.getOrgProfileList());
	}

	private void retrieveFinancialOrgList() {
		IFinancialOrgSO finOrgProfileSO = (IFinancialOrgSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IFinancialOrgSO.class);

		FinancialOrgResDTO resDTO = finOrgProfileSO.retrieveFinancialOrgList();

		this.getManagedBean().setPmFinancialOrg(resDTO.getFinancialOrgList());
	}

	private void retrieveSupplierUserList(PmOrgProfile orgProfile) {
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setOrgProfileId(orgProfile.getOrgProfileId());

		UserResDTO resDTO = userSO.retrieveUserListByOrgProfileId(reqDTO);

		this.getManagedBean().setSupplierUsers(
				new DualListModel(resDTO.getUserList(), this.getManagedBean()
						.getSelectedUsers()));
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
		PmPtjGroup pmPtjGroupList = new PmPtjGroup();

		try {
			List<PmPtjGroup> ptjList = this.getManagedBean().getPtjGroupList();
			if (null == ptjList) {
				ptjList = new ArrayList<PmPtjGroup>();
			}

			for (PmPtjGroup pmPtj : ptjList) {
				pmPtj.setBrandNew(false);
			}
			ptjList.add(pmPtjGroupList);
			this.getManagedBean().setPtjGroupList(ptjList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}

	}

	public void deletePtjAction() {

		List<PmPtjGroup> openList = this.getManagedBean().getPtjGroupList();

		if (null == openList) {
			openList = new ArrayList<PmPtjGroup>();
		}

		List<PmPtjGroup> openSelectList = this.getManagedBean()
				.getSelectedPtjGroup();
		if (null == openSelectList) {
			openSelectList = new ArrayList<PmPtjGroup>();
		}

		if (openList.size() == openSelectList.size()) {
			openList.clear();
		} else {
			for (PmPtjGroup category : openSelectList)
				openList.remove(category);
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
		SelectItem[] items = new SelectItem[2];
		items[0] = new SelectItem(1, "Active");
		items[1] = new SelectItem(0, "Inactive");
		return items;
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
