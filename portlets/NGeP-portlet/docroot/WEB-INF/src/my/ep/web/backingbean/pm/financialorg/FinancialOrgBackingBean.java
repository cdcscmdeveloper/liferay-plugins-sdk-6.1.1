package my.ep.web.backingbean.pm.financialorg;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
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
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialBranch;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmFinancialSvc;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.financialorg.FinancialOrgManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "financialOrgBackingBean")
@ViewScoped
public class FinancialOrgBackingBean extends
		BaseBackingBean<FinancialOrgManagedBean> {
	private static Logger log = Logger.getLogger(FinancialOrgBackingBean.class);
	boolean result = false;

	private IPagingDataModel<PmFinancialOrg> searchFinancialOrg = new FinancialOrgPagingDataModel();

	public FinancialOrgBackingBean() {
		retrieveCountryList();

		retrievePmFinancialOrgList();
	}

	public void searchFinancialOrg() {

		searchFinancialOrg.reloadData();

	}

	public String backToMain() {
		getManagedBean().setFinancialOrg(new PmFinancialOrg());
		getManagedBean().getFinancialOrg().setPmCountry(new PmCountry());
		getManagedBean().setPmFinancialAcc(new ArrayList<PmFinancialAcc>());
		getManagedBean().setPmFinancialBranch(
				new ArrayList<PmFinancialBranch>());
		getManagedBean().setPmFinancialBranchAcc(
				new ArrayList<PmFinancialAcc>());
		getManagedBean().setPmFinancialSvc(new ArrayList<PmFinancialSvc>());
		/*
		 * ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance()
		 * .getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
		 * ICountrySO.class); CountryReqDTO countryReqDTO = new CountryReqDTO();
		 * countryReqDTO.setCountryCode("MY");
		 * getManagedBean().getPmAddress().setPmCountry(
		 * countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());
		 */
		getManagedBean().setPmAddress(new PmAddress());
		getManagedBean().getPmAddress().setPmCountry(new PmCountry());
		getManagedBean().getPmAddress().setPmState(new PmState());
		getManagedBean().getPmAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getPmAddress().setPmCity(new PmCity());
		getManagedBean().getPmAddress().setPmDivision(new PmDivision());
		// getManagedBean().getPmAddress().getPmDivision().setDivisionId(1L);
		getManagedBean().setBranchAddress(new PmAddress());
		getManagedBean().getBranchAddress().setPmCountry(new PmCountry());
		// countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());
		getManagedBean().getBranchAddress().setPmState(new PmState());
		getManagedBean().getBranchAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getBranchAddress().setPmCity(new PmCity());
		getManagedBean().getBranchAddress().setPmDivision(new PmDivision());
		// getManagedBean().getBranchAddress().getPmDivision().setDivisionId(1L);
		getManagedBean().getFinancialOrg().setRecordStatusFlg(null);

		getManagedBean().setRegTypeId(null);

		return "/xhtml/pm/financialorg/financialOrgSearchMain.xhtml";

	}

	public void getGeneralInfo() {
		this.getManagedBean().setFinancialActiveIndex(0);
	}

	public void getFactringBranchInfo() {
		this.getManagedBean().setFinancialActiveIndex(1);
	}

	public String addFinancialOrg() {

		getManagedBean().setFinancialOrg(new PmFinancialOrg());
		getManagedBean().getFinancialOrg().setPmCountry(new PmCountry());
		getManagedBean().setPmFinancialAcc(new ArrayList<PmFinancialAcc>());
		getManagedBean().setPmFinancialBranch(
				new ArrayList<PmFinancialBranch>());
		getManagedBean().setPmFinancialBranchAcc(
				new ArrayList<PmFinancialAcc>());
		getManagedBean().setPmFinancialSvc(new ArrayList<PmFinancialSvc>());
		/*
		 * ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance()
		 * .getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
		 * ICountrySO.class); CountryReqDTO countryReqDTO = new CountryReqDTO();
		 * countryReqDTO.setCountryCode("MY");
		 * getManagedBean().getPmAddress().setPmCountry(
		 * countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());
		 */
		getManagedBean().setContactPerson(new PmContact());
		getManagedBean().getPmAddress().setPmCountry(new PmCountry());
		getManagedBean().getPmAddress().setPmState(new PmState());
		getManagedBean().getPmAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getPmAddress().setPmCity(new PmCity());
		getManagedBean().getPmAddress().setPmDivision(new PmDivision());
		// getManagedBean().getPmAddress().getPmDivision().setDivisionId(1L);
		getManagedBean().getBranchAddress().setPmCountry(new PmCountry());
		// countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());
		getManagedBean().getBranchAddress().setPmState(new PmState());
		getManagedBean().getBranchAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getBranchAddress().setPmCity(new PmCity());
		getManagedBean().getBranchAddress().setPmDivision(new PmDivision());
		// getManagedBean().getBranchAddress().getPmDivision().setDivisionId(1L);
		getManagedBean().getFinancialOrg().setRecordStatusFlg(null);

		getManagedBean().setRegTypeId(null);
		retrieveCountryList();

		retrievePmFinancialOrgList();
		return "/xhtml/pm/financialorg/maintainFinancialOrg.xhtml";

	}

	public void reset() {
		this.getManagedBean().setOrgName(null);
		this.getManagedBean().setRegNumber(null);
		this.getManagedBean().setServiceType(null);
		this.getManagedBean().setOrgStatus(null);

	}

	public void saveFinancialOrg() {
		log.info("Save Method");

		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();

		IFinancialOrgSO financialOrgSO = (IFinancialOrgSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IFinancialOrgSO.class);
		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryReqDTO countryReqDTO = new CountryReqDTO();
		countryReqDTO.setCountryId(getManagedBean().getFinancialOrg()
				.getPmCountry().getCountryId());
		CountryResDTO countryResDTO = countrySO
				.retrieveCountryById(countryReqDTO);
		IParameterSO parameterSO = (IParameterSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		parameterReqDTO
				.setParameterType(ParameterTypeConst.FINANCIAL_ORG_REG_TYPE);
		parameterReqDTO.setParameterCode(getManagedBean().getRegTypeId());
		ParameterResDTO parameterResDTO = parameterSO
				.retrieveParameterByTypeAndCode(parameterReqDTO);
		PmFinancialOrg financialOrg = getManagedBean().getFinancialOrg();
		financialOrg.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		financialOrg.setCreatedBy(getFacesScopes().getUserSessionBean()
				.getUserId());
		financialOrg.setChangedBy(getFacesScopes().getUserSessionBean()
				.getUserId());
		financialOrg.setCompanyTypeCode(parameterResDTO.getPmParameter());
		financialOrg.setBizRegNumber(getManagedBean().getFinancialOrg()
				.getBizRegNumber());
		financialOrg.setFinOrgName(getManagedBean().getFinancialOrg()
				.getFinOrgName());
		financialOrg.setPmCountry(countryResDTO.getPmCountry());
		for (PmFinancialSvc financialSvc : getManagedBean().getPmFinancialSvc()) {
			parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterType(ParameterTypeConst.BANK_SERVICES);
			parameterReqDTO.setParameterCode(getManagedBean()
					.getPmFinancialSvc().get(0).getServiceTypeCode()
					.getParameterCode());
			parameterResDTO = parameterSO
					.retrieveParameterByTypeAndCode(parameterReqDTO);
			financialSvc.setServiceTypeCode(parameterResDTO.getPmParameter());
			financialSvc.setPmFinancialOrg(financialOrg);
		}

		if (getManagedBean()
				.getPmFinancialSvc()
				.get(0)
				.getServiceTypeCode()
				.getParameterCode()
				.equalsIgnoreCase(
						ParameterConst.BANK_SERVICES_FACTORING_COMPANY)) {

			for (PmFinancialAcc financialAcc : getManagedBean()
					.getPmFinancialAcc()) {
				financialAcc.setPmFinancialOrg(financialOrg);
				financialAcc.setPmPaymentBankOrg(financialOrg);
			}

			List<PmOrgProfile> orgProfileList = getManagedBean()
					.getOrgProfileList();

			for (PmOrgProfile orgProfile : orgProfileList) {
				// PmOrgProfile orgPmOrgProfile = new PmOrgProfile();
				System.out.println("saving org Profile");
				orgProfile.setOrgTypeCode(parameterResDTO.getPmParameter());
				orgProfile.setPmFinancialOrg(financialOrg);

			}

			for (PmFinancialBranch financialBranchs : getManagedBean()
					.getPmFinancialBranch()) {
				PmAddressType brachAddress = getManagedBean()
						.getBranchAddressType();
				System.out.println("BranchAddress"+brachAddress.getPmAddress().getAddress1());
				brachAddress
						.setAddressType(AddressType.RECEIVING_OFFICER_ADDRESS
								.value());
				
				List<PmAddressType> branchAddressTypes = getManagedBean()
						.getBranchAddressList();
				branchAddressTypes.add(brachAddress);
				PmAddress branchAddress = getManagedBean().getBranchAddress();
				PmContact contact = getManagedBean().getBranchContactPerson();
				contact.setPmAddressType(brachAddress);
				List<PmContact> contactLst = getManagedBean()
						.getBranchContact();
				contactLst.add(contact);
				brachAddress.setPmAddress(branchAddress);
				brachAddress.setPmContacts(contactLst);
				brachAddress.setPmFinancialBranch(financialBranchs);
				financialBranchs.setPmAddressTypes(branchAddressTypes);

				for (PmFinancialAcc financialAcc : getManagedBean()
						.getPmFinancialBranchAcc()) {
					financialAcc.setPmFinancialBranch(financialBranchs);
					financialAcc.setPmPaymentBankOrg(financialOrg);

				}
				financialBranchs.setPmFinancialAccs(getManagedBean()
						.getPmFinancialBranchAcc());

				financialBranchs.setPmFinancialOrg(financialOrg);
			}
			financialOrg.setPmFinancialAccs(getManagedBean()
					.getPmFinancialAcc());
			financialOrg.setPmFinancialBranchs(getManagedBean()
					.getPmFinancialBranch());

		}
		PmAddressType addressType = getManagedBean().getPmAddressType();
		addressType.setAddressType(AddressType.RECEIVING_OFFICER_ADDRESS
				.value());
		List<PmAddressType> pmAddressTypes = getManagedBean().getAddressType();
		pmAddressTypes.add(addressType);
		PmAddress address = getManagedBean().getPmAddress();
		PmContact contact = getManagedBean().getContactPerson();
		contact.setPmAddressType(addressType);
		List<PmContact> contactLst = getManagedBean().getPmContact();
		contactLst.add(contact);
		addressType.setPmContacts(contactLst);
		addressType.setPmAddress(address);
		addressType.setPmFinancialOrg(financialOrg);
		financialOrg.setPmAddressTypes(pmAddressTypes);
		financialOrg.setPmFinancialSvcs(getManagedBean().getPmFinancialSvc());
		financialOrg.setPmOrgProfiles(getManagedBean().getOrgProfileList());

		reqDTO.setPmFinancialOrg(financialOrg);

		FinancialOrgResDTO resDTO = financialOrgSO
				.saveOrUpdateFinancialOrg(reqDTO);

		if (resDTO.isSuccessful()) {
			result = resDTO.isSuccessful();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Financial Org successfully.", "Org Name : "
									+ this.getManagedBean().getFinancialOrg()
											.getFinOrgName()));

		}
	}

	public String editFinancialOrg(PmFinancialOrg financialOrg) {
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		reqDTO.setFinancialOrgId(financialOrg.getFinancialOrgId());
		IFinancialOrgSO finSO = (IFinancialOrgSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IFinancialOrgSO.class);
		FinancialOrgResDTO resDTO = finSO
				.retrieveFinancialOrgDetailsById(reqDTO);
		financialOrg = resDTO.getPmFinancialOrg();
		getManagedBean().setRegTypeId(
				financialOrg.getCompanyTypeCode().getParameterCode());
		getManagedBean().setFinancialOrg(financialOrg);
		// financial org Address
		List<PmAddressType> finAddressTypes = resDTO.getPmFinancialOrg()
				.getPmAddressTypes();

		if (finAddressTypes != null && !finAddressTypes.isEmpty()) {
			for (PmAddressType pmAddressType : finAddressTypes) {

				if (pmAddressType.getAddressType().equalsIgnoreCase("R")) {
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
					StateResDTO stateresDTO = reqSO
							.retriveStatesListByCountryID(stateReqDTO);
					getManagedBean().setStateList(stateresDTO.getStateList());

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
					pmAddressType.setEditable(true);

					getManagedBean().setPmAddress(pmAddressType.getPmAddress());

				}
				//financial contacts 
				if (pmAddressType.getPmContacts() != null
						&& !pmAddressType.getPmContacts().isEmpty()) {
					for (PmContact contactPerson : pmAddressType
							.getPmContacts())
						getManagedBean().setContactPerson(contactPerson);
				}
			}
		}
		// financial services
		if (financialOrg.getPmFinancialSvcs() != null
				&& !financialOrg.getPmFinancialSvcs().isEmpty()) {
			getManagedBean().setPmFinancialSvc(
					financialOrg.getPmFinancialSvcs());

		}
		// financial account
		if (financialOrg.getPmFinancialAccs() != null
				&& !financialOrg.getPmFinancialAccs().isEmpty()) {
			getManagedBean().setPmFinancialAcc(
					financialOrg.getPmFinancialAccs());

		}
		// financial Branch
		if (financialOrg.getPmFinancialBranchs() != null
				&& !financialOrg.getPmFinancialBranchs().isEmpty()) {
			getManagedBean().setPmFinancialBranch(
					financialOrg.getPmFinancialBranchs());
			// financial Branch Address
			/*
			 * for (PmAddressType pmAddressType : financialOrg
			 * .getPmFinancialBranchs().get(0).getPmAddressTypes()) { if
			 * (pmAddressType.getAddressType().equalsIgnoreCase("R")) {
			 * this.getManagedBean().setBranchAddress(
			 * pmAddressType.getPmAddress());
			 * 
			 * } if (!pmAddressType.getPmContacts().isEmpty()) {
			 * this.getManagedBean().setBranchContactPerson(
			 * pmAddressType.getPmContacts().get(0)); } }
			 */
			for (PmFinancialBranch financialBranch : financialOrg.getPmFinancialBranchs()) {
				List<PmAddressType> branchAddressTypes = financialBranch.getPmAddressTypes();

				if (branchAddressTypes != null) {
					for (PmAddressType pmAddressType : branchAddressTypes) {
						

						if (pmAddressType.getAddressType().equalsIgnoreCase("R")) {
							if (pmAddressType.getPmAddress().getPmDivision() == null) {
								pmAddressType.getPmAddress().setPmDivision(
										new PmDivision());
							}
							StateReqDTO stateReqDTO = new StateReqDTO();
							stateReqDTO.setCountryId(pmAddressType.getPmAddress()
									.getPmCountry().getCountryId());
							IStateSO reqSO = (IStateSO) BusinessDelegate
									.getInstance().getServiceObject(
											ModuleCodeConst.PROFILE_MANAGEMENT,
											IStateSO.class);
							StateResDTO stateresDTO = reqSO
									.retriveStatesListByCountryID(stateReqDTO);
							getManagedBean().setStateList(
									stateresDTO.getStateList());

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
								districtReqDTO.setDivisionId(pmAddressType
										.getPmAddress().getPmDivision()
										.getDivisionId());
								DistrictResDTO districtResDTO = districtSO
										.retriveDistrictListByDivision(districtReqDTO);
								getManagedBean().setDistrictList(
										districtResDTO.getDistrictList());
							} else {

								districtReqDTO.setStateId(pmAddressType
										.getPmAddress().getPmState().getStateId());
								DistrictResDTO districtResDTO = districtSO
										.retriveDistrictListByStateID(districtReqDTO);
								getManagedBean().setDistrictList(
										districtResDTO.getDistrictList());
							}

							CityReqDTO cityReqDTO = new CityReqDTO();
							cityReqDTO.setDistrictId(pmAddressType.getPmAddress()
									.getPmDistrict().getDistrictId());
							ICitySO citySO = (ICitySO) BusinessDelegate
									.getInstance().getServiceObject(
											ModuleCodeConst.PROFILE_MANAGEMENT,
											ICitySO.class);
							CityResDTO cityResDTO = citySO
									.retriveCityListByDistrictId(cityReqDTO);
							this.getManagedBean().setCityList(
									cityResDTO.getCityList());
							pmAddressType.setEditable(true);

							getManagedBean().setBranchAddress(pmAddressType.getPmAddress());

						}
						
						// financial Branch Contacts
						
						if (pmAddressType.getPmContacts() != null
								&& !pmAddressType.getPmContacts().isEmpty()) {
							for (PmContact contactPerson : pmAddressType
									.getPmContacts())
								getManagedBean().setBranchContactPerson(contactPerson);
						}
					}
				}
			}
			
			// financial Branch Accounts
			getManagedBean().setPmFinancialBranchAcc(
					resDTO.getPmFinancialOrg().getPmFinancialBranchs().get(0)
							.getPmFinancialAccs());
		}

		getManagedBean().setFinancialOrg(resDTO.getPmFinancialOrg());
		return "/xhtml/pm/financialorg/maintainFinancialOrg.xhtml";

	}

	private class FinancialOrgPagingDataModel extends
			PagingDataModel<PmFinancialOrg> {

		private static final long serialVersionUID = 1L;

		public FinancialOrgPagingDataModel() {

			super("searchFinancialOrg");
		}

		public ISearchResultBean<PmFinancialOrg> pageChangeListener(
				ISearchBean<PmFinancialOrg> searchBean) {

			IFinancialOrgSO financialOrgSO;
			try {

				FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();

				if (getManagedBean().getOrgName() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("finOrgName",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getOrgName(), true));
				}
				if (getManagedBean().getRegNumber() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("bizRegNumber",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getRegNumber(), true));
				}

				if (getManagedBean().getServiceType() != null) {
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									"pmFinancialSvcs.serviceTypeCode.parameterCode",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getServiceType()));
				}
				if (getManagedBean().getOrgStatus() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("recordStatusFlg",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getOrgStatus()));
				}

				reqDTO.setFinancialSearchBean(searchBean);
				financialOrgSO = (IFinancialOrgSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IFinancialOrgSO.class);
				FinancialOrgResDTO resDto = financialOrgSO
						.searchFinancialOrgData(reqDTO);

				if (resDto.isSuccessful()) {
					return resDto.getFinaceialOrgResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	public void addFinancialService() {

		PmFinancialSvc pmFinancialSvc = new PmFinancialSvc();

		try {
			List<PmFinancialSvc> pmFinancialList = this.getManagedBean()
					.getPmFinancialSvc();
			if (null == pmFinancialList) {
				pmFinancialList = new ArrayList<PmFinancialSvc>();
			}
			pmFinancialSvc.setServiceTypeCode(new PmParameter());

			for (PmFinancialSvc finsvc : pmFinancialList) {
				finsvc.setBrandNew(false);
			}
			pmFinancialList.add(pmFinancialSvc);
			this.getManagedBean().setPmFinancialSvc(pmFinancialList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void deleteFinancialService() {

		List<PmFinancialSvc> cashOnHandList = this.getManagedBean()
				.getPmFinancialSvc();
		List<PmFinancialSvc> selectedFinancialSvcList = this.getManagedBean()
				.getSelectedPmFinancialSvc();
		if (cashOnHandList.size() == selectedFinancialSvcList.size()) {
			cashOnHandList.clear();
		} else {
			for (PmFinancialSvc financialSvc : selectedFinancialSvcList) {
				cashOnHandList.remove(financialSvc);
			}
		}
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

	public void addFinancialBranchAcc() {

		PmFinancialAcc pmFinancialBranchAcc = new PmFinancialAcc();

		try {
			List<PmFinancialAcc> pmFinancialBranchList = this.getManagedBean()
					.getPmFinancialBranchAcc();
			if (null == pmFinancialBranchList) {
				pmFinancialBranchList = new ArrayList<PmFinancialAcc>();
			}

			for (PmFinancialAcc financialAcc : pmFinancialBranchList) {
				financialAcc.setBrandNew(false);
			}
			pmFinancialBranchList.add(pmFinancialBranchAcc);
			this.getManagedBean()
					.setPmFinancialBranchAcc(pmFinancialBranchList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void deleteFinancialBranchAcc() {

		List<PmFinancialAcc> financialBranchAccList = this.getManagedBean()
				.getPmFinancialBranchAcc();
		List<PmFinancialAcc> selectedFinancialActList = this.getManagedBean()
				.getSelectedPmFinancialBranchAcc();
		if (financialBranchAccList.size() == selectedFinancialActList.size()) {
			financialBranchAccList.clear();
		} else {
			for (PmFinancialAcc financialAcc : selectedFinancialActList) {
				financialBranchAccList.remove(financialAcc);
			}
		}
	}

	public void addFinancialBranch() {

		PmFinancialBranch pmFinancialBranch = new PmFinancialBranch();

		try {
			List<PmFinancialBranch> pmFinancialBranchList = this
					.getManagedBean().getPmFinancialBranch();
			if (null == pmFinancialBranchList) {
				pmFinancialBranchList = new ArrayList<PmFinancialBranch>();
			}

			for (PmFinancialBranch financialBranch : pmFinancialBranchList) {
				financialBranch.setBrandNew(false);
			}
			pmFinancialBranchList.add(pmFinancialBranch);
			this.getManagedBean().setPmFinancialBranch(pmFinancialBranchList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void deleteFinancialBranch() {

		List<PmFinancialBranch> financialBranchList = this.getManagedBean()
				.getPmFinancialBranch();
		List<PmFinancialBranch> selectedFinancialBranchList = this
				.getManagedBean().getSelectedPmFinancialBranch();
		if (financialBranchList.size() == selectedFinancialBranchList.size()) {
			financialBranchList.clear();
		} else {
			for (PmFinancialBranch financialBranch : selectedFinancialBranchList) {
				financialBranchList.remove(financialBranch);
			}
		}
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

	private void retrievePmFinancialOrgList() {
		IFinancialOrgSO reqSO = (IFinancialOrgSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IFinancialOrgSO.class);
		FinancialOrgResDTO resDTO = reqSO.retrieveFinancialOrgList();
		this.getManagedBean().setPmFinancialOrg(resDTO.getFinancialOrgList());
	}

	public void okContacePersonAction() {

		RequestContext.getCurrentInstance()
				.execute("contactPersonPopup.hide()");
	}

	public void okBranchContacePersonAction() {

		RequestContext.getCurrentInstance().execute(
				"branchContactPersonPopup.hide()");
	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public IPagingDataModel<PmFinancialOrg> getSearchFinancialOrg() {
		return searchFinancialOrg;
	}

	public void setSearchFinancialOrg(
			IPagingDataModel<PmFinancialOrg> searchFinancialOrg) {
		this.searchFinancialOrg = searchFinancialOrg;
	}

	public void onChangeAction() {
		if (getManagedBean().isByFactoring()) {
			getManagedBean().setActiveIndex(0);
		}

	}
}
