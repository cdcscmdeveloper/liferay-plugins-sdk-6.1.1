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
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.biz.service.pm.fee.settings.IFeeExemptionSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
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
import my.ep.dto.pm.fee.settings.FeeExemptionReqDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
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
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.pm.fee.settings.PmFeeExemption;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.OrganisationSearchBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.pm.organisation.OrganisationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang.StringUtils;
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
		log.info("..............redirectToOrganisation.......id=" + id);
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
		this.getManagedBean().setOrgTypeId(null);
		this.getManagedBean().setSelectedTabIndex(PROFILE_DETAIL);		
		getManagedBean().setOrgProfile(new PmOrgProfile());		
		getManagedBean().getOrgProfile().setPmOrgValidity(new PmOrgValidity());
		getManagedBean().setOfficeAddress(setDefaultAddr());		
		getManagedBean().setContactList(new ArrayList<PmContact>());
		getManagedBean().setOfficeContactPerson(new PmContact());
		//getManagedBean().getOrgProfile().setPmZone(new PmZone());
		getManagedBean().setOrgType(null);		
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
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS.value(), oSearchBean
								.getOrgStatus()));

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
		log.info(".......maintainOrganisation......");
		// clear list's previous value
		this.getManagedBean().setSmSupplierBanks(
				new ArrayList<SmSupplierBank>());
		this.getManagedBean().setSmPersonnels(new ArrayList<SmPersonnel>());
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

		getManagedBean().setOrgTypeId(
				orgProfile.getOrgTypeCode().getParameterId());
		log.info("orgProfile.getOrgTypeCode().getParameterCode()="
				+ orgProfile.getOrgTypeCode().getParameterCode());
		// BPK Sabah sarawak
		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.BPK_PEROLEHAN_PM_SABAH.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value())) {
			retrievePmZoneList();			
			getManagedBean().getOrgProfile().setPmZone(orgProfile.getPmZone()==null ? new PmZone() : orgProfile.getPmZone());

		}
		// PTJ
		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.DIVISION.value())) {
			retrieveOrgProfileList();
			orgDeliveryAddrList(orgProfile);
			this.getManagedBean().setProfileGroup(
					orgProfile.getMainProfileGroups());

		}
		// PTJ, GLC, LAT, STG and STB

		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.DIVISION.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.GOVERNMENT_LINK_COMPANIES.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.STATUTORY_BODIES.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.STATE_GOVERNMENT.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.LOCAL_AUTHORITY.value())) {
			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT,
							ISupplierSearchSO.class);
			SupplierSearchReqDTO supReqDTO = new SupplierSearchReqDTO();
			SupplierSearchResDTO supResDTO;
			log.info("Org Profile ID-----"+orgProfile.getOrgProfileId());
			supReqDTO.setSupplierId(orgProfile.getOrgProfileId());
			supResDTO = supplierSearchSO.retriveSupplierById(supReqDTO);
			if (supResDTO.getSmSupplier() != null) {
				supResDTO = supplierSearchSO
						.retriveSuppProfileAndPersonnelBySuppId(supReqDTO);
				SmSupplier supplier = supResDTO.getSmSupplier();
				if (supplier != null) {
					this.getManagedBean().setSmSupplierBanks(
							supplier.getSmAppl().getSmSupplierBanks());
					this.getManagedBean().setSmPersonnels(
							supplier.getSmAppl().getSmPersonnels());
				}
			}
		}
		List<PmAddressType> pmAddressTypes = orgProfile.getPmAddressTypes();
		boolean addrExist = false;
		if (pmAddressTypes != null && !pmAddressTypes.isEmpty()) {
			for (PmAddressType pmAddressType : pmAddressTypes) {
				if (pmAddressType.getAddressType().equalsIgnoreCase(
						AddressType.BILLING_ADDRESS.value())) {
					this.getManagedBean().setOfficeAddress((pmAddressType!=null) ? pmAddressType.getPmAddress() : setDefaultAddr());
					getManagedBean().setOfficeAddress(pmAddressType.getPmAddress());
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
				
			    if (pmAddressType.getPmContacts() != null && !pmAddressType.getPmContacts().isEmpty()) {
					for (PmContact contactPerson : pmAddressType.getPmContacts())
						getManagedBean().setOfficeContactPerson(contactPerson);
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
		
		getManagedBean().setOrgProfile(orgResDTO.getPmOrgProfile());

		return NAV_TO_SEARCH;

	}

	public String saveOrganization() throws Exception {
		// check govSeller
		log.info("................this.getManagedBean().getSmSupplierBanks()="
				+ this.getManagedBean().getSmSupplierBanks());
		if (this.getManagedBean().getOrgProfile().isGovtSeller()) {
			List<SmSupplierBank> tempSupplierBankList = new ArrayList<SmSupplierBank>();
			if (this.getManagedBean().getSmSupplierBanks() == null) {
				throw new EpException(MessageConst.ERROR_775101);
			} else {
				// filter list for active records and check again

				for (SmSupplierBank supplierBank : this.getManagedBean()
						.getSmSupplierBanks()) {
					log.info("..............supplierBank.getRecordStatusFlg="
							+ supplierBank.getRecordStatusFlg());
					log.info("..............supplierBank.isRecordStatus="
							+ supplierBank.isRecordStatus());
					if (supplierBank.getRecordStatusFlg() == 1) {
						tempSupplierBankList.add(supplierBank);
					}
				}
				log.info(".................tempSupplierBankList.size="
						+ tempSupplierBankList.size());
				if (tempSupplierBankList.isEmpty()) {
					throw new EpException(MessageConst.ERROR_775101);
				}

			}
			if (this.getManagedBean().getSmPersonnels() == null) {
				throw new EpException(MessageConst.ERROR_775102);
			} else {
				// filter list for active records and check again
				List<SmPersonnel> tempPersonnelList = new ArrayList<SmPersonnel>();
				for (SmPersonnel personnel : this.getManagedBean()
						.getSmPersonnels()) {
					if (personnel.getRecordStatusFlg() == 1) {
						tempPersonnelList.add(personnel);
					}
				}
				if (tempPersonnelList.isEmpty()) {
					throw new EpException(MessageConst.ERROR_775102);
				}
			}
		}
		// check group org
		if (this.getManagedBean().getOrgProfile().isGroupOrg()) {
			if (this.getManagedBean().getProfileGroup() == null
					|| this.getManagedBean().getProfileGroup().isEmpty()) {
				throw new EpException(MessageConst.ERROR_775103);
			}
		}

		IFinancialOrgSO financialOrgSO = (IFinancialOrgSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IFinancialOrgSO.class);

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

		PmOrgProfile orgProfile = getManagedBean().getOrgProfile();
		PmOrgValidity orgValidity = getManagedBean().getOrgProfile().getPmOrgValidity();
		
		List<PmProfileGroup> profileGroup = new ArrayList<PmProfileGroup>();
		if (orgProfile.isBrandNew()) {
			orgProfile.setOrgTypeCode(parameterResDTO.getPmParameter());
			orgValidity.setEffDate(new Date());			
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
			orgProfile.setPmOrgValidity(orgValidity);
		}	
		if (parameterResDTO.getPmParameter().getParameterCode().equalsIgnoreCase(
				OrganizationType.BPK_PEROLEHAN_PM_SABAH.value())
		|| getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value())) {				
	    orgProfile.setPmZone(getManagedBean().getOrgProfile().getPmZone());
       }
		
		if (getManagedBean().getOrgProfile().isGovtSeller()) {
			IRegistrationSO registrationSO = (IRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT,
							IRegistrationSO.class);

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT,
							ISupplierSearchSO.class);
			SupplierSearchReqDTO supReqDTO = new SupplierSearchReqDTO();
			SupplierSearchResDTO supResDTO;
			supReqDTO.setSupplierId(orgProfile.getOrgProfileId());
			supResDTO = supplierSearchSO.retriveSupplierById(supReqDTO);
			// new Supplier
			if (supResDTO.getSmSupplier() == null) {
				log.info("Create new Supplier");
				RegistrationReqDTO regReqDTO = new RegistrationReqDTO();
				regReqDTO.setPmUsers(getManagedBean().getSupplierUsers());
             
				regReqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
				if (!getManagedBean().getSmSupplierBanks().isEmpty()) {
                 for(SmSupplierBank supplierBank:getManagedBean().getSmSupplierBanks()){
                	 FinancialOrgReqDTO finReqDTO = new FinancialOrgReqDTO();
						finReqDTO.setFinancialOrgId(supplierBank.getPmFinancialOrg().getFinancialOrgId());

						FinancialOrgResDTO finResDTO = financialOrgSO
								.retrieveFinancialOrgById(finReqDTO);
						PmFinancialOrg pmFinancialOrg = finResDTO
								.getPmFinancialOrg();               
                	 regReqDTO.setBankAccountNo(supplierBank.getAccountNo());
                	 regReqDTO.setBank(pmFinancialOrg);
                }
				}
				RegistrationResDTO regResDTO = registrationSO
						.createPTJSupplier(regReqDTO);
				regResDTO.setSuccessful(true);
				//Update User Role
				for(PmUser user:getManagedBean().getSupplierUsers()){
				IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IUserSO.class);
				UserReqDTO userReqDTO = new UserReqDTO();
				userReqDTO.setPmUser(user);
				userReqDTO.isSupplierUser();
				UserResDTO userResDTO= userSO.saveUser(userReqDTO);
				userResDTO.setSuccessful(true);
				}
				//Fee Exemption
				IFeeExemptionSO exemptionSO = (IFeeExemptionSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IFeeExemptionSO.class);
				FeeExemptionReqDTO feeReqDTO = new FeeExemptionReqDTO();
				PmFeeExemption feeEx = new PmFeeExemption();				
				feeEx.setOrgProfileId(orgProfile);
				feeReqDTO.setFeeExemption(feeEx);
				FeeExemptionResDTO feeResDTO = exemptionSO.insertFeeExemption(feeReqDTO);
				feeResDTO.setSuccessful(true);

			}
			if (supResDTO.getSmSupplier() != null) {
				log.info("Update Supplier");
				supResDTO = supplierSearchSO
						.retriveSuppProfileAndPersonnelBySuppId(supReqDTO);
				SmSupplier supplier = supResDTO.getSmSupplier();
				if (supplier != null) {
					RegistrationReqDTO regReqDTO = new RegistrationReqDTO();
					// SmPersonnel smPersonnel = new SmPersonnel();

					supplier.getSmAppl().getSmPersonnels()
							.addAll(getManagedBean().getSmPersonnels());

					regReqDTO.setSmSupp(supplier);
					RegistrationResDTO regResDTO = registrationSO
							.updatePTJSupplier(regReqDTO);
					regResDTO.setSuccessful(true);
				}
			}
		}
		
		if (parameterResDTO.getPmParameter().getParameterCode()
				.equalsIgnoreCase(OrganizationType.DIVISION.value())) {
			getManagedBean().getAddressTypesList().addAll(orgProfile.getPmAddressTypes());
			orgProfile.setPmAddressTypes(getManagedBean().getAddressTypesList());
			
			if (getManagedBean().getOrgProfile().isGroupOrg()) {
				
				List<PmProfileGroup> profileGrp = getManagedBean()
						.getProfileGroup();
				for (PmProfileGroup proGroup : profileGrp) {
					if(proGroup.isBrandNew()){
						proGroup.setMainOrgProfile(orgProfile);
					}
				}
				profileGroup.addAll(profileGrp);
				orgProfile.setMainProfileGroups(profileGroup);
				
			}
			else{
				orgProfile.setMainProfileGroups(null);				
			}
		}
		validateOrgProfiel(orgProfile);
		validateOrgAddress(this.getManagedBean().getOfficeAddress());
		validateContactPerson(this.getManagedBean().getOfficeContactPerson());
		reqDTO.setPmOrgProfile(orgProfile);

		OrgProfileResDTO resDTO = orgProfileSO.saveOrUpdateOrgProfile(reqDTO);
		reqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
		OrgProfileResDTO resDTO1 = orgProfileSO
				.retrieveOrgProfileDtlByProfileId(reqDTO);
		if (resDTO1 != null && resDTO1.getPmOrgProfile() != null) {
			getManagedBean().setOrgProfile(resDTO1.getPmOrgProfile());
		}

		if (resDTO.isSuccessful()) {
			result = resDTO.isSuccessful();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Org Name : ",
							this.getManagedBean().getOrgProfile().getOrgName()
									+ "Submit successfully."));
		}

		return NAV_TO_SEARCH;

	}
	private void validateOrgProfiel(PmOrgProfile orgProfile){
		
		if(StringUtils.isBlank(orgProfile.getPmOrgValidity().getOrgCode())){
			throw new EpException(MessageConst.ERROR_775105);
		}		
		if(StringUtils.isBlank(orgProfile.getPmOrgValidity().getOrgName())){
			throw new EpException(MessageConst.ERROR_775106);
		}

	}
     private void validateContactPerson(PmContact contactPerson){
		
		if(StringUtils.isBlank(contactPerson.getContactName())){
			throw new EpException(MessageConst.ERROR_775113);
		}		
		if(StringUtils.isBlank(contactPerson.getEmail())){
			throw new EpException(MessageConst.ERROR_775114);
		}

	}
	private boolean validateOrgAddress(PmAddress pmAddress){
		
		boolean addressEntered = false;
	
			//address is optional
			if(pmAddress.getPmCountry().getCountryId()!=null || !StringUtils.isBlank(pmAddress.getAddress1()) ||
					!StringUtils.isBlank(pmAddress.getPostcode()) || pmAddress.getPmState().getStateId()!=null ||
					pmAddress.getPmDistrict().getDistrictId()!=null
					){
				addressEntered = true;
			}
			if(addressEntered){
				if(pmAddress.getPmCountry().getCountryId()==null){
					throw new EpException(MessageConst.ERROR_775107);
				}
		
				if(StringUtils.isBlank(pmAddress.getAddress1())){
					throw new EpException(MessageConst.ERROR_775108);
				}
				
				if(StringUtils.isBlank(pmAddress.getPostcode())){
					throw new EpException(MessageConst.ERROR_775109);
				}
				
				if(pmAddress.getPmState().getStateId()==null){
					throw new EpException(MessageConst.ERROR_775110);
				}
		
				if(pmAddress.getPmDistrict().getDistrictId()==null){
					throw new EpException(MessageConst.ERROR_775111);
				}	
				if(pmAddress.getPmCity().getCityId()==null){
					throw new EpException(MessageConst.ERROR_775112);
				}	
			}
		
		return addressEntered;
	}

	public void resetOrgProfile() {
		getManagedBean().setOrgProfile(new PmOrgProfile());	
		getManagedBean().getOrgProfile().setPmOrgValidity(new PmOrgValidity());
		//getManagedBean().getOrgProfile().setPmZone(new PmZone());
		getManagedBean().setOfficeAddress(setDefaultAddr());
		getManagedBean().setOfficeContactPerson(new PmContact());
		
		  IParameterSO parameterSO = (IParameterSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterId(getManagedBean().getOrgTypeId());
		ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
		this.getManagedBean().setOrgType(
				resDTO.getPmParameter().getParameterCode());
		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.BPK_PEROLEHAN_PM_SABAH.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value())) {
			retrievePmZoneList();			
			getManagedBean().getOrgProfile().setPmZone(new PmZone());

		}
		OrgProfileReqDTO orgReqDTO = new OrgProfileReqDTO();
		orgReqDTO.setOrgType(getManagedBean().getOrgType());
		
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileResDTO orgResDTO = orgProfileSO.retrieveOrgProfileByOrgType(orgReqDTO);
		orgResDTO.setSuccessful(true);			
		
		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.BPK_PENDAFTARAN_HQ.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PENDAFTARAN_SABAH.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PENDAFTARAN_SARAWAK.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PEROLEHAN_HQ.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PEROLEHAN_PM_SABAH.value())
				||getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value())) {
			throw new EpException(MessageConst.ERROR_775104);
			
		}

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
	public void retrieveDeliveryCountryList() {

		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();
		getManagedBean().setDeliveryCountryList(resDTO.getCountryList());
	}

	public void retrieveDeliveryStateList(AjaxBehaviorEvent e) {
		Long countryIdId = (Long) e.getComponent().getAttributes().get("value");
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryIdId);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setDeliveryStateList(resDTO.getStateList());
	}
	public void retrieveDeliveryDivisionList(AjaxBehaviorEvent e) {
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
			getManagedBean().setDeliveryDistrictList(districtResDTO.getDistrictList());
		}
	}

	

	public void retrieveDeliveryDistrictList(AjaxBehaviorEvent e) {
		Long divisionId = (Long) e.getComponent().getAttributes().get("value");
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setDivisionId(divisionId);
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByDivision(reqDTO);
		getManagedBean().setDeliveryDistrictList(resDTO.getDistrictList());

	}

	public void retrieveDeliveryCityList(AjaxBehaviorEvent e) {
		Long districtId = (Long) e.getComponent().getAttributes().get("value");
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(districtId);
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICitySO.class);
		CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
		this.getManagedBean().setDeliveryCityList(resDTO.getCityList());
	}
	public void addDeliveryAddr() {
	log.info("Add Delivery Address");
		retrieveDeliveryCountryList();
		
		PmAddress address = new PmAddress();
		address.setPmCountry(new PmCountry());
		address.setPmState(new PmState());
		address.setPmDistrict(new PmDistrict());
		address.setPmCity(new PmCity());
		
		PmAddressType addressType = new PmAddressType();
		addressType.setAddressType(AddressType.DELIVERY_ADDRESS.value());
		addressType.setPmAddress(address);
		
		getManagedBean().setDeliveryAddress(addressType);		
				
		// default to Malaysia
		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICountrySO.class);
		CountryReqDTO countryReqDTO = new CountryReqDTO();
		countryReqDTO.setCountryCode("MY");		
		getManagedBean().getDeliveryAddress().getPmAddress().setPmCountry(countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());	
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(getManagedBean().getDeliveryAddress().getPmAddress().getPmCountry().getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);		
		getManagedBean().setDeliveryStateList(resDTO.getStateList());		
		getManagedBean().getDeliveryAddress().setAddressType(AddressType.DELIVERY_ADDRESS.value());		

	}
	public void updateDeliveryAddrDt() {	
		
		PmAddressType deliveryAddr = getManagedBean().getDeliveryAddress();
		PmAddress manualAddr = deliveryAddr.getPmAddress();	
		deliveryAddr.setAddressType(AddressType.DELIVERY_ADDRESS.value());
		deliveryAddr.setPmOrgProfile(getManagedBean().getOrgProfile());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setStateId(manualAddr.getPmState().getStateId());
		StateResDTO resDTO = reqSO.retriveStateByID(reqDTO);
		manualAddr.setPmState(resDTO.getPmState());	
		
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
		DistrictReqDTO districtReqDTO = new DistrictReqDTO();
		districtReqDTO.setDistrictId(manualAddr.getPmDistrict().getDistrictId());
		DistrictResDTO districtResDTO = districtSO.retriveDistrictByID(districtReqDTO);		
		manualAddr.setPmDistrict(districtResDTO.getPmDistrict());	
		
		ICitySO citySO = (ICitySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);
		CityReqDTO cityReqDTO = new CityReqDTO();
		cityReqDTO.setCityId(manualAddr.getPmCity().getCityId());
		CityResDTO cityResDTO = citySO.retriveCityById(cityReqDTO);
		manualAddr.setPmCity(cityResDTO.getPmCity());	
		if(manualAddr.isBrandNew() && !manualAddr.isModified()){
			getManagedBean().getAddressTypesList().add(deliveryAddr);	
		}else{
			for(int i =0; i < getManagedBean().getAddressTypesList().size(); i++){
				PmAddressType adressType = getManagedBean().getAddressTypesList().get(i);
				PmAddress address = adressType.getPmAddress();
				if( (!manualAddr.isBrandNew()) && address.getAddressId() != null && address.getAddressId().equals(manualAddr.getAddressId())){					
					getManagedBean().getAddressTypesList().set(i, adressType);
				}else if(address.getAddressName().equals(manualAddr.getAddressName())){
					getManagedBean().getAddressTypesList().set(i, adressType);
				}
			}
		}
			
	}
	
     public void editDeliveryAddress(PmAddressType address) {
    	 retrieveDeliveryCountryList();	
	    getManagedBean().setDeliveryAddress(address);	
		getManagedBean().setPmAddress(address.getPmAddress());		
		getManagedBean().getDeliveryAddress().getPmAddress().getPmCountry().setCountryId(address.getPmAddress().getPmCountry().getCountryId());
	    StateReqDTO stateReqDTO = new StateReqDTO();		
		stateReqDTO.setCountryId(address.getPmAddress().getPmCountry().getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(stateReqDTO);
		getManagedBean().setDeliveryStateList(resDTO.getStateList());

		DistrictReqDTO districtReqDTO = new DistrictReqDTO();
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);

		districtReqDTO.setStateId(address.getPmAddress().getPmState().getStateId());
		DistrictResDTO districtResDTO = districtSO
				.retriveDistrictListByStateID(districtReqDTO);
		getManagedBean().setDeliveryDistrictList(districtResDTO.getDistrictList());

		CityReqDTO cityReqDTO = new CityReqDTO();
		cityReqDTO.setDistrictId(address.getPmAddress().getPmDistrict().getDistrictId());
		ICitySO citySO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICitySO.class);
		CityResDTO cityResDTO = citySO.retriveCityListByDistrictId(cityReqDTO);
		this.getManagedBean().setDeliveryCityList(cityResDTO.getCityList());
		
		List<SelectItem> manualAddrList = new ArrayList<SelectItem>();
		if(getManagedBean().getManualAddressList()!=null) {
			for (PmAddressType obj : getManagedBean().getManualAddressList()) {
				manualAddrList.add(new SelectItem(obj.getAddressTypeId(), obj.getPmAddress().getAddressName()));
			}
		}
		 if (address.getPmContacts() != null && !address.getPmContacts().isEmpty()) {
				for (PmContact contactPerson : address.getPmContacts())
					getManagedBean().setDeliveryContactPerson(contactPerson);
			}
			else {

				List<PmContact> contactLst = getManagedBean()
						.getDeliveryContactList();
				getManagedBean().setDeliveryContactPerson(new PmContact());
				PmContact contact = getManagedBean()
						.getDeliveryContactPerson();
				contact.setPmAddressType(address);
				contactLst.add(contact);
				address.setPmContacts(this.getManagedBean().getContactList());			
				}		
		 if(address.getPmAddress().isBrandNew()){
			 address.getPmAddress().setModified(true);
		 }
		 address.setEditable(true);
	}
    public void orgDeliveryAddrList(PmOrgProfile orgProfile) {
		AddressReqDTO reqDTO = new AddressReqDTO();
		log.info("ORG PROFILE ID"+orgProfile.getOrgProfileId());
		reqDTO.setOrgProfileId(orgProfile.getOrgProfileId());		
		reqDTO.setAddressType(AddressType.DELIVERY_ADDRESS.value());
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IAddressSO.class);
		AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);		
		log.info("Address List"+resDTO.getAddressTypeList().size());
		getManagedBean().setAddressTypesList(resDTO.getAddressTypeList());		

	}

	public SelectItem[] getOrgTypeDropdownValues(String itemType) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(OrganizationType.EP_PROFILE.value());
		recommendList.add(OrganizationType.DEPARTMENT.value());
		recommendList.add(OrganizationType.FACTORING_COMPANY.value());
		recommendList.add(OrganizationType.SUPPLIER.value());
		recommendList.add(OrganizationType.OTHERS.value());

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
		recommendList.add(OrganizationType.EP_PROFILE.value());
		recommendList.add(OrganizationType.DEPARTMENT.value());
		recommendList.add(OrganizationType.FACTORING_COMPANY.value());
		recommendList.add(OrganizationType.SUPPLIER.value());
		recommendList.add(OrganizationType.OTHERS.value());		
		recommendList.add(OrganizationType.DIVISION.value());
		recommendList.add(OrganizationType.SUPPORT.value());
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		searchBean.getSortColumns();

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.ORGANISATION_TYPES, searchBean);
		return items;
	}

	public SelectItem[] getAllOrgTypeDropdownValues(String itemType) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

		searchBean.getSortColumns();

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.ORGANISATION_TYPES, searchBean);
		return items;
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

	public void addSupplierBank() {

		try {

			if (this.getManagedBean().getSmSupplierBanks() == null) {
				this.getManagedBean().setSmSupplierBanks(
						new ArrayList<SmSupplierBank>());
			}
			SmSupplierBank newSupplierBank = new SmSupplierBank();
			newSupplierBank.setPmFinancialOrg(new PmFinancialOrg());
			int index = this.getManagedBean().getSmSupplierBanks().size();
			this.getManagedBean().getSmSupplierBanks()
					.add(index, newSupplierBank);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void deleteSupplierBank(int index) {
		SmSupplierBank smSupplierBank = this.getManagedBean()
				.getSmSupplierBanks().get(index);
		smSupplierBank.setDeleted(true);
		this.getManagedBean().getDeletedSupplierBankList().add(smSupplierBank);
		// should added to original list before save																			
		this.getManagedBean().getSmSupplierBanks().remove(smSupplierBank);

	}

	private void retrieveOrgProfileList() {
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		OrgProfileResDTO resDTO = orgProfileSO
				.retrieveOrgProfileListByMinistry(reqDTO);
		this.getManagedBean().setOrgProfileList(resDTO.getOrgProfileList());
	}
	private PmAddress setDefaultAddr() {
		PmAddress addr = new PmAddress();
		PmCountry defaultCountry = PmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);StateReqDTO reqDTO = new StateReqDTO();
		addr.setPmCountry(defaultCountry);
		reqDTO.setCountryId(defaultCountry.getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStateList(resDTO.getStateList());
		addr.setPmCity(new PmCity());	
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
     try {
			List<PmProfileGroup> ptjList = this.getManagedBean()
					.getProfileGroup();
			if (null == ptjList) {
				ptjList = new ArrayList<PmProfileGroup>();
			}		
			this.getManagedBean().getProfileGroup().add(new PmProfileGroup());
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

	public void addPersonnel() {

		if (this.getManagedBean().getSmPersonnels() == null) {
			this.getManagedBean().setSmPersonnels(new ArrayList<SmPersonnel>());
		}
		if (this.getManagedBean().getSelectedSupplierUsers() != null) {
			this.getManagedBean().getSelectedSupplierUsers().clear();
		}
		// int index = this.getManagedBean().getSmPersonnels().size();
		// this.getManagedBean().getSmPersonnels().add(index , new
		// SmPersonnel());

	}

	public void deletePersonnel(int index) {

		SmPersonnel smPersonnel = this.getManagedBean().getSmPersonnels()
				.get(index);
		smPersonnel.setDeleted(true);
		// this.getManagedBean().getDeletedSmPersonnels()
		// .add(smPersonnel);// should added to original list before
		// // save
		this.getManagedBean().getSmPersonnels().remove(smPersonnel);
	}

	public void conformUsers() {

		List<PmUser> selectedUserList = getManagedBean()
				.getSelectedSupplierUsers();
		List<SmPersonnel> addedPersonnelList = new ArrayList<SmPersonnel>();
		boolean redaundant;
		for (PmUser selectedUser : selectedUserList) {
			redaundant = false;
			for (SmPersonnel smPersonnel : this.getManagedBean()
					.getSmPersonnels()) {
				if (smPersonnel.getPmUser().getUserId()
						.equals(selectedUser.getUserId())) {
					redaundant = true;
					break;
				}
			}
			if (!redaundant) {
				SmPersonnel newSmPersonnel = new SmPersonnel();
				newSmPersonnel.setPmUser(selectedUser);
				addedPersonnelList.add(newSmPersonnel);
			}
		}
		this.getManagedBean().getSmPersonnels().addAll(addedPersonnelList);
		
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
		this.getManagedBean().setOrganisationSearchBean(null);
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
