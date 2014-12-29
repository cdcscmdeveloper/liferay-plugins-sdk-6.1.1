package my.ep.web.backingbean.pm.financialorg;

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
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.constant.MessageConst;
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
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialBranch;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmFinancialSvc;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.FinancialOrgSearchBean;
import my.ep.web.managedbean.pm.financialorg.FinancialOrgManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "financialOrgBackingBean")
@ViewScoped
public class FinancialOrgBackingBean extends
		BaseBackingBean<FinancialOrgManagedBean> {
	private static Logger log = Logger.getLogger(FinancialOrgBackingBean.class);
	boolean result = false;
	// Tab values
	public static final int GENERRAL = 0;
	public static final int FACTRING_BRANCH = 1;

	// Default value
	public static final boolean SEARCH_DEFAULT = false;
	// Navigation Details
	public static final String NAV_TO_SEARCH = "financialOrgSearchMain";
	public static final String NAV_TO_MAINTAIN = "maintainFinancialOrg";
	private IPagingDataModel<PmFinancialOrg> searchFinancialOrg = new FinancialOrgPagingDataModel();

	public FinancialOrgBackingBean() {

	}

	public void preLoadingFinOrg() {
		log.info("preLoading");
		this.resetFinancialOrgSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	public int getGeneralInfoTab() {
		return GENERRAL;
	}

	public int getFactringBranchInfoTab() {
		return FACTRING_BRANCH;
	}

	private ISearchBean<PmFinancialOrg> getSearchBeanData(
			ISearchBean<PmFinancialOrg> searchBean) {
		log.info("getSearchBeanData - Start");
		FinancialOrgSearchBean fSearchBean = this.getManagedBean()
				.getFinancialOrgSearchBean();
		boolean isSearchable = SEARCH_DEFAULT;
		boolean isEmpty = true;
		if (fSearchBean.getOrgName() != null
				&& fSearchBean.getOrgName().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("finOrgName",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getFinancialOrgSearchBean()
									.getOrgName(), true));
			isEmpty = false;
		}

		if (fSearchBean.getServiceType() != null
				&& fSearchBean.getServiceType().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmFinancialSvcs.serviceTypeCode.parameterCode",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getFinancialOrgSearchBean()
									.getServiceType()));
			isEmpty = false;
		}
		if (isEmpty && !isSearchable) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getFinancialOrgSearchBean()
									.getOrgStatus()));
		}
		if (fSearchBean.getRegNumber() != null
				&& fSearchBean.getRegNumber().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("bizRegNumber",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getFinancialOrgSearchBean()
									.getRegNumber(), true));
			isEmpty = false;
		}

		log.info("getSearchBeanData - End");
		return searchBean;
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
				ISearchBean<PmFinancialOrg> searchBeanData = getSearchBeanData(searchBean);
				reqDTO.setFinancialSearchBean(searchBeanData);
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

	public void searchFinancialOrg() {
		searchFinancialOrg.reloadData();
	}
	public String backToMain() {
		getManagedBean().setServiceTypes(null);
		initialFinOrg();
		return NAV_TO_SEARCH;

	}
	public String redirectToFinOrg(Long id) throws Exception {
		retrieveCountryList();
		retrieveBranchCountryList();
		retrievePmFinancialOrgList();

		if (id != null && id > 0) {
			maintainFinancialOrg(id);

		} else {
			initialFinOrg();

		}
		getManagedBean().setShowParentMsg(true);
		return NAV_TO_MAINTAIN;
	}
	public void initialFinOrg() {
		getManagedBean().setServiceTypes(null);
		this.getManagedBean().setSelectedTabIndex(GENERRAL);
		getManagedBean().setFinancialOrg(new PmFinancialOrg());
		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICountrySO.class);
		CountryReqDTO countryReqDTO = new CountryReqDTO();
		countryReqDTO.setCountryCode("MY");
		getManagedBean().getFinancialOrg().setPmCountry(countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());
		getManagedBean().setPmFinancialAcc(new ArrayList<PmFinancialAcc>());
		getManagedBean().setPmFinancialBranch(new ArrayList<PmFinancialBranch>());
		getManagedBean().setPmFinancialBranchAcc(new ArrayList<PmFinancialAcc>());
		getManagedBean().setPmFinancialSvc(new ArrayList<PmFinancialSvc>());
		//Financial Org Address
		getManagedBean().setPmAddress(new PmAddress());
		getManagedBean().setContactPerson(new PmContact());
		getManagedBean().getPmAddress().setPmCountry(countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(getManagedBean().getPmAddress().getPmCountry().getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStateList(resDTO.getStateList());
		getManagedBean().getPmAddress().setPmState(new PmState());
		getManagedBean().getPmAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getPmAddress().setPmCity(new PmCity());
		//Branch Address
		getManagedBean().setBranchAddress(new PmAddress());
		getManagedBean().setBranchContactPerson(new PmContact());
		getManagedBean().getBranchAddress().setPmCountry(countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());
		getManagedBean().setBranchStateList(resDTO.getStateList());		
		getManagedBean().getBranchAddress().setPmState(new PmState());
		getManagedBean().getBranchAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getBranchAddress().setPmCity(new PmCity());

	}

	IFinancialOrgSO financialOrgSO = (IFinancialOrgSO) BusinessDelegate
			.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IFinancialOrgSO.class);


	IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IParameterSO.class);
	ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
	

	public String saveFinancialOrg() {
		log.info("Save Method");
		PmFinancialOrg financialOrg = this.getManagedBean().getFinancialOrg();
		if (financialOrg.isBrandNew()) {
			
			if(this.getManagedBean().getPmFinancialSvc()!=null&& this.getManagedBean().getPmFinancialSvc().isEmpty()){
				throw new EpException(MessageConst.ERROR_775103);
			}

			for (PmFinancialSvc financialSvc : this.getManagedBean()
					.getPmFinancialSvc()) {

				parameterReqDTO
						.setParameterType(ParameterTypeConst.BANK_SERVICES);
				parameterReqDTO.setParameterId(financialSvc
						.getServiceTypeCode().getParameterId());
				ParameterResDTO parameterResDTO = parameterSO
						.retrieveParameterById(parameterReqDTO);
				financialSvc.setServiceTypeCode(parameterResDTO
						.getPmParameter());
				financialSvc.setPmFinancialOrg(financialOrg);
				if (parameterResDTO.getPmParameter().getParameterCode().toString()
						.trim().contains(ParameterConst.BANK_SERVICES_FACTORING_COMPANY)) {
					
					saveFactoringDetails(financialOrg);

				}
			}
			
			
			PmAddressType addressType = this.getManagedBean()
					.getPmAddressType();
			addressType.setAddressType(AddressType.BILLING_ADDRESS.value());
			List<PmAddressType> pmAddressTypes = this.getManagedBean()
					.getAddressType();
			pmAddressTypes.add(addressType);
			PmAddress address = this.getManagedBean().getPmAddress();
			PmContact contact = this.getManagedBean().getContactPerson();
			contact.setPmAddressType(addressType);
			List<PmContact> contactLst = this.getManagedBean().getPmContact();
			contactLst.add(contact);
			addressType.setPmContacts(contactLst);
			addressType.setPmAddress(address);
			addressType.setPmFinancialOrg(financialOrg);			
			financialOrg.setPmAddressTypes(pmAddressTypes);

		}

		financialOrg.setPmFinancialSvcs(this.getManagedBean()
				.getPmFinancialSvc());
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		reqDTO.setPmFinancialOrg(financialOrg);
		try{	

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
		catch (Exception e) {
			e.printStackTrace();
			
			throw e;
		}
		return NAV_TO_SEARCH;
	}

	public void maintainFinancialOrg(Long id) {
		this.getManagedBean().setSelectedTabIndex(GENERRAL);
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		reqDTO.setFinancialOrgId(id);
		IFinancialOrgSO finSO = (IFinancialOrgSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IFinancialOrgSO.class);
		FinancialOrgResDTO resDTO = finSO
				.retrieveFinancialOrgDetailsById(reqDTO);
		PmFinancialOrg financialOrg = resDTO.getPmFinancialOrg();

		getManagedBean().setFinancialOrg(financialOrg);
		getManagedBean().setPmFinancialSvc(financialOrg.getPmFinancialSvcs());

		for (PmFinancialSvc financialSvc : getManagedBean().getPmFinancialSvc()) {

			parameterReqDTO.setParameterType(ParameterTypeConst.BANK_SERVICES);
			parameterReqDTO.setParameterId(financialSvc.getServiceTypeCode()
					.getParameterId());
			ParameterResDTO parameterResDTO = parameterSO
					.retrieveParameterById(parameterReqDTO);
			financialSvc.setServiceTypeCode(parameterResDTO.getPmParameter());
			/*if (parameterResDTO.getPmParameter().getParameterCode().toString()
					.trim().contains(ParameterConst.BANK_SERVICES_FACTORING_COMPANY)) {
				
				 getManagedBean().getServiceTypes().add(financialSvc.getServiceTypeCode().getParameterCode());
				// getManagedBean().setServiceType(financialSvc.getServiceTypeCode().getParameterCode());
			}*/
			if(getManagedBean().getServiceTypes()== null){
				   getManagedBean().setServiceTypes(new ArrayList<String>());			   
			   }
			
			 getManagedBean().setServiceType(financialSvc.getServiceTypeCode().getParameterCode());
			 getManagedBean().getServiceTypes().add(getManagedBean().getServiceType());
		}
		// financial org Address
		List<PmAddressType> finAddressTypes = resDTO.getPmFinancialOrg()
				.getPmAddressTypes();

		boolean addrExist = false;
		if (finAddressTypes != null && !finAddressTypes.isEmpty()) {
			for (PmAddressType pmAddressType : finAddressTypes) {
				if (pmAddressType.getAddressType().equalsIgnoreCase(
						AddressType.BILLING_ADDRESS.value())) {

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
					districtReqDTO.setStateId(pmAddressType.getPmAddress()
							.getPmState().getStateId());
					DistrictResDTO districtResDTO = districtSO
							.retriveDistrictListByStateID(districtReqDTO);
					getManagedBean().setDistrictList(
							districtResDTO.getDistrictList());
					// }

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
					System.out.println("Main Addresss"+pmAddressType.getPmAddress().getAddress1());
					System.out.println("Main City"+pmAddressType.getPmAddress().getPmCity().getCityName());
					getManagedBean().setPmAddress(pmAddressType.getPmAddress());
					addrExist = true;
				}
				// financial contacts
				if (pmAddressType.getPmContacts() != null
						&& !pmAddressType.getPmContacts().isEmpty()) {
					for (PmContact contactPerson : pmAddressType
							.getPmContacts())
						getManagedBean().setContactPerson(contactPerson);
				} else {

					List<PmContact> contactLst = getManagedBean()
							.getPmContact();
					getManagedBean().setContactPerson(new PmContact());
					PmContact contact = getManagedBean().getContactPerson();
					contact.setPmAddressType(pmAddressType);
					contactLst.add(contact);
					pmAddressType.setPmContacts(contactLst);

				}
			}
		}
		if (!addrExist) {
			PmAddressType addressType = getManagedBean().getPmAddressType();
			finAddressTypes.add(addressType);
			addressType.setAddressType(AddressType.BILLING_ADDRESS.value());
			getManagedBean().setPmAddress(setDefaultAddr());
			PmAddress address = getManagedBean().getPmAddress();
			addressType.setPmAddress(address);
			List<PmContact> contactLst = getManagedBean().getPmContact();
			getManagedBean().setContactPerson(new PmContact());
			PmContact contact = getManagedBean().getContactPerson();
			contact.setPmAddressType(addressType);
			contactLst.add(contact);
			addressType.setPmContacts(contactLst);
			addressType.setPmFinancialOrg(financialOrg);
			financialOrg.setPmAddressTypes(finAddressTypes);
		}
		// financial services
		if (financialOrg.getPmFinancialSvcs() != null
				&& !financialOrg.getPmFinancialSvcs().isEmpty()) {
			this.getManagedBean().setPmFinancialSvc(
					financialOrg.getPmFinancialSvcs());

		}
		// financial account
		if (financialOrg.getPmFinancialAccs() != null
				&& !financialOrg.getPmFinancialAccs().isEmpty()) {
			this.getManagedBean().setPmFinancialAcc(
					financialOrg.getPmFinancialAccs());

		}
		// financial Branch
		if (financialOrg.getPmFinancialBranchs() != null
				&& !financialOrg.getPmFinancialBranchs().isEmpty()) {
			getManagedBean().setPmFinancialBranch(
					financialOrg.getPmFinancialBranchs());
			for (PmFinancialBranch financialBranch : financialOrg
					.getPmFinancialBranchs()) {
				// financial Branch Address
				List<PmAddressType> branchAddressTypes = financialBranch
						.getPmAddressTypes();

				boolean branchaddrExist = false;
				if (branchAddressTypes != null && !branchAddressTypes.isEmpty()) {
					for (PmAddressType branchAddressType : branchAddressTypes) {
						if (branchAddressType.getAddressType()
								.equalsIgnoreCase(
										AddressType.BILLING_ADDRESS
												.value())) {

							StateReqDTO stateReqDTO = new StateReqDTO();
							stateReqDTO.setCountryId(branchAddressType
									.getPmAddress().getPmCountry()
									.getCountryId());
							IStateSO reqSO = (IStateSO) BusinessDelegate
									.getInstance().getServiceObject(
											ModuleCodeConst.PROFILE_MANAGEMENT,
											IStateSO.class);
							StateResDTO stateresDTO = reqSO
									.retriveStatesListByCountryID(stateReqDTO);
							getManagedBean().setBranchStateList(
									stateresDTO.getStateList());

							DistrictReqDTO districtReqDTO = new DistrictReqDTO();
							IDistrictSO districtSO = (IDistrictSO) BusinessDelegate
									.getInstance().getServiceObject(
											ModuleCodeConst.PROFILE_MANAGEMENT,
											IDistrictSO.class);
							districtReqDTO.setStateId(branchAddressType
									.getPmAddress().getPmState().getStateId());
							DistrictResDTO districtResDTO = districtSO
									.retriveDistrictListByStateID(districtReqDTO);
							getManagedBean().setBranchDistrictList(
									districtResDTO.getDistrictList());
							// }

							CityReqDTO cityReqDTO = new CityReqDTO();
							cityReqDTO.setDistrictId(branchAddressType
									.getPmAddress().getPmDistrict()
									.getDistrictId());
							ICitySO citySO = (ICitySO) BusinessDelegate
									.getInstance().getServiceObject(
											ModuleCodeConst.PROFILE_MANAGEMENT,
											ICitySO.class);
							CityResDTO cityResDTO = citySO
									.retriveCityListByDistrictId(cityReqDTO);
							this.getManagedBean().setBranchCityList(
									cityResDTO.getCityList());							

							getManagedBean().setBranchAddress(
									branchAddressType.getPmAddress());
							
							branchaddrExist = true;
						}
						// financial Branch contacts
						if (branchAddressType.getPmContacts() != null
								&& !branchAddressType.getPmContacts().isEmpty()) {
							for (PmContact contactPerson : branchAddressType
									.getPmContacts())
								getManagedBean().setBranchContactPerson(
										contactPerson);
						}
					}
				}
				if (!branchaddrExist) {

					PmAddressType addressType = getManagedBean()
							.getBranchAddressType();
					branchAddressTypes.add(addressType);
					addressType
							.setAddressType(AddressType.BILLING_ADDRESS
									.value());
					getManagedBean().setBranchAddress(setDefaultAddr());
					PmAddress address = getManagedBean().getBranchAddress();
					addressType.setPmAddress(address);
					List<PmContact> contactLst = getManagedBean()
							.getBranchContact();
					getManagedBean().setBranchContactPerson(new PmContact());
					PmContact contact = getManagedBean()
							.getBranchContactPerson();
					contact.setPmAddressType(addressType);
					contactLst.add(contact);
					addressType.setPmContacts(contactLst);

					addressType.setPmFinancialBranch(financialBranch);
					financialBranch.setPmAddressTypes(finAddressTypes);

					getManagedBean().setBranchAddress(setDefaultAddr());
				}

				// financial Branch Accounts
				getManagedBean().setPmFinancialBranchAcc(
						financialBranch.getPmFinancialAccs());
			}
		}

		getManagedBean().setFinancialOrg(resDTO.getPmFinancialOrg());

	}
	/*public void addFinancialService() {
		if(this.getManagedBean().getPmFinancialSvc()!=null){
			if(this.getManagedBean().getPmFinancialSvc().size()>3){
				throw new EpException("Exceed the limit");
			}
		}
    try {
			List<PmFinancialSvc> pmFinancialList = this.getManagedBean()
					.getPmFinancialSvc();
			if (null == pmFinancialList) {
				pmFinancialList = new ArrayList<PmFinancialSvc>();
			}
			this.getManagedBean().getFinancialSvc().setServiceTypeCode(new PmParameter());
			pmFinancialList.add(this.getManagedBean().getFinancialSvc());
			this.getManagedBean().setPmFinancialSvc(pmFinancialList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}*/
	public void addFinancialService() {
		if(this.getManagedBean().getPmFinancialSvc()!=null){
			if(this.getManagedBean().getPmFinancialSvc().size()>3){
				throw new EpException("Exceed the limit");
			}
		}	
		PmFinancialSvc pmFinancialSvc = new PmFinancialSvc();

		try {
			List<PmFinancialSvc> pmFinancialList = this.getManagedBean()
					.getPmFinancialSvc();
			if (null == pmFinancialList) {
				pmFinancialList = new ArrayList<PmFinancialSvc>();
			}
			pmFinancialSvc.setServiceTypeCode(new PmParameter());
			pmFinancialList.add(pmFinancialSvc);
			this.getManagedBean().setPmFinancialSvc(pmFinancialList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void deleteFinSvc(PmFinancialSvc financialSvc) {
		if(this.getManagedBean().getSelectedPmFinancialSvc()!=null && !this.getManagedBean().getSelectedPmFinancialSvc().isEmpty()){

	
			if (this.getManagedBean().getSelectedPmFinancialSvc() == null) {

				this.getManagedBean().setSelectedPmFinancialSvc(
						new ArrayList<PmFinancialSvc>());
			}

			this.getManagedBean().getSelectedPmFinancialSvc()
					.remove(financialSvc);
		}
		
		else{
			//need to change exceptions
			throw new EpException(MessageConst.ERROR_770022);
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

	/*public void addFinancialAcc() {

		try {
			List<PmFinancialAcc> pmFinancialList = this.getManagedBean().getPmFinancialAcc();
			if (null == pmFinancialList) {
				pmFinancialList = new ArrayList<PmFinancialAcc>();
			}

			this.getManagedBean().getPmFinancialAcc().add(new PmFinancialAcc());
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}*/
	public void addFinancialAcc() {

		try {
			
			if (this.getManagedBean().getPmFinancialAcc() == null) {
				this.getManagedBean().setPmFinancialAcc(new ArrayList<PmFinancialAcc>());
			}
			PmFinancialAcc finAcc = new PmFinancialAcc();
			finAcc.setPmPaymentBankOrg(new PmFinancialOrg());
			//finAcc.setPmPaymentBankOrg(getManagedBean().getFinancialOrg());
			int index = this.getManagedBean().getPmFinancialAcc().size();
			this.getManagedBean().getPmFinancialAcc().add(index, finAcc);
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
		
		
		if(null==getManagedBean().getPmFinancialBranch()){
			throw new EpException("Please select atleast one branch to proceed.");
		}else if(getManagedBean().getPmFinancialBranch().size()==0){
			throw new EpException("Please select atleast one branch to proceed.");
		}
		else{
			
			try {
				if (this.getManagedBean().getPmFinancialBranchAcc() == null) {
					this.getManagedBean().setPmFinancialBranchAcc(new ArrayList<PmFinancialAcc>());
				}
				PmFinancialAcc finAcc = new PmFinancialAcc();
				finAcc.setPmPaymentBankOrg(new PmFinancialOrg());
				//finAcc.setPmFinancialBranch(getManagedBean().getFinancialBranch());
				int index = this.getManagedBean().getPmFinancialBranchAcc().size();
				this.getManagedBean().getPmFinancialBranchAcc().add(index, finAcc);
			} catch (Exception e) {
				throw new IllegalArgumentException();
			}
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
		try {
			if (this.getManagedBean().getPmFinancialBranch() == null) {
				this.getManagedBean().setPmFinancialBranch(new ArrayList<PmFinancialBranch>());
			}
			PmFinancialBranch finBranch = new PmFinancialBranch();
			finBranch.setPmFinancialOrg(getManagedBean().getFinancialOrg());			
			finBranch.setPmAddressTypes(new ArrayList<PmAddressType>());
			int index = this.getManagedBean().getPmFinancialAcc().size();
			this.getManagedBean().getPmFinancialBranch().add(index, finBranch);
			///
			/*List<PmFinancialBranch> branchLisst = this.getManagedBean().getPmFinancialBranch();
			if (null == branchLisst) {
				branchLisst = new ArrayList<PmFinancialBranch>();
			}

			this.getManagedBean().getPmFinancialBranch().add(new PmFinancialBranch());*/
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}

	}
	public void deleteFinancialBranch(PmFinancialBranch financialBranch) {

		if (financialBranch != null && financialBranch.isBrandNew()) {
			if (this.getManagedBean().getPmFinancialBranch() == null) {
				this.getManagedBean().setPmFinancialBranch(
						new ArrayList<PmFinancialBranch>());
			}
			this.getManagedBean().getPmFinancialBranch().remove(financialBranch);
		}
	}	
	public void showBranchAddress(){
		PmFinancialBranch financialBranch = getManagedBean().getSelectedPmFinancialBranch();
		List<PmAddressType> pmAddressTypeLst = getManagedBean().getAddressType();
		financialBranch.setPmAddressTypes(pmAddressTypeLst);
		this.getManagedBean().setBranchAddressreq(true);
		
		System.out.println("Show Address");
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

	/*public void updateService(AjaxBehaviorEvent e) {
		Long finServiceId = (Long) e.getComponent().getAttributes()
				.get("value");
		for (PmFinancialSvc financialService : getManagedBean()
				.getPmFinancialSvc()) {
			parameterReqDTO.setParameterId(finServiceId);
			log.info("Parameter ID"
					+ financialService.getServiceTypeCode().getParameterId());
			ParameterResDTO parameterResDTO = parameterSO
					.retrieveParameterById(parameterReqDTO);
			getManagedBean().setServiceType(
					parameterResDTO.getPmParameter().getParameterCode());

		}
	}*/
	public void updateService(AjaxBehaviorEvent e){
		  
		  Long finServiceId =(Long) e.getComponent().getAttributes().get("value");
		  System.out.println("finServiceId :"+finServiceId);
		  int i =0;
		  for(PmFinancialSvc financialService:getManagedBean().getPmFinancialSvc()){
		   parameterReqDTO.setParameterId(finServiceId);		   
		   System.out.println("Parameter ID"+financialService.getServiceTypeCode().getParameterId());
		   if(finServiceId==financialService.getServiceTypeCode().getParameterId()){
		    i++;
		    System.out.println("i value :"+i);
		    if(i>1){
		    throw new EpException("Selection already exists.");}
		   }
		   ParameterResDTO parameterResDTO = parameterSO
		     .retrieveParameterById(parameterReqDTO);
		   if(getManagedBean().getServiceTypes()== null){
			   getManagedBean().setServiceTypes(new ArrayList<String>());			   
		   }
		   getManagedBean().getServiceTypes().add(parameterResDTO
				     .getPmParameter().getParameterCode());
		  
		  }
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
	//Branch Address
	public void retrieveBranchCountryList() {

		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();
		getManagedBean().setBranchCountryList(resDTO.getCountryList());
	}

	public void retrieveBranchStateList(AjaxBehaviorEvent e) {
		Long countryIdId = (Long) e.getComponent().getAttributes().get("value");
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryIdId);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setBranchStateList(resDTO.getStateList());
	}

	

	public void retrieveBranchDivisionList(AjaxBehaviorEvent e) {
		Long stateId = (Long) e.getComponent().getAttributes().get("value");
		DivisionReqDTO reqDTO = new DivisionReqDTO();
		reqDTO.setStateId(stateId);
		IDivisionSO reqSO = (IDivisionSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDivisionSO.class);
		DivisionResDTO resDTO = reqSO.retriveDivisionListByState(reqDTO);
		getManagedBean().setBranchDivisionList(resDTO.getDivisionList());

		if (resDTO.getDivisionList().size() == 0) {
			DistrictReqDTO districtReqDTO = new DistrictReqDTO();
			districtReqDTO.setStateId(stateId);
			IDistrictSO districtSO = (IDistrictSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IDistrictSO.class);
			DistrictResDTO districtResDTO = districtSO
					.retriveDistrictListByStateID(districtReqDTO);
			getManagedBean().setBranchDistrictList(districtResDTO.getDistrictList());
		}
	}

	public void retrieveBranchDistrictList(AjaxBehaviorEvent e) {
		Long divisionId = (Long) e.getComponent().getAttributes().get("value");
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setDivisionId(divisionId);
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByDivision(reqDTO);
		getManagedBean().setBranchDistrictList(resDTO.getDistrictList());

	}

	public void retrieveBranchCityList(AjaxBehaviorEvent e) {
		Long districtId = (Long) e.getComponent().getAttributes().get("value");
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(districtId);
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICitySO.class);
		CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
		this.getManagedBean().setBranchCityList(resDTO.getCityList());
	}

	private void retrievePmFinancialOrgList() {
		IFinancialOrgSO reqSO = (IFinancialOrgSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IFinancialOrgSO.class);
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
		FinancialOrgResDTO resDTO = reqSO.retrieveFinancialOrgListByServiceType(reqDTO);
		this.getManagedBean().setPmFinancialOrg(resDTO.getFinancialOrgList());
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

	public void addBranchContacePerson() {

		RequestContext.getCurrentInstance().execute(
				"branchContactPersonPopup.show()");
		getManagedBean().setShowParentMsg(false);
	}

	public void okBranchContacePersonAction() {

		RequestContext.getCurrentInstance().execute(
				"branchContactPersonPopup.hide()");
		getManagedBean().setShowParentMsg(false);
	}

	public void closeBranchContacePerson() {

		RequestContext.getCurrentInstance().execute(
				"branchContactPersonPopup.hide()");
		getManagedBean().setShowParentMsg(true);
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

	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getStatusOptionsDefault();
	}

	/**
	 * reset search bean fields
	 */
	public void resetFinancialOrgSearchBean() {
		log.info("resetFinancialOrgSearchBean");
		this.getManagedBean().setFinancialOrgSearchBean(
				new FinancialOrgSearchBean());
		searchFinancialOrg = new FinancialOrgPagingDataModel();
	}

	private PmAddress setDefaultAddr() {
		PmAddress addr = new PmAddress();
		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryReqDTO countryReqDTO = new CountryReqDTO();
		countryReqDTO.setCountryCode("MY");
		addr.setPmCountry(countrySO.retrieveCountryByCode(countryReqDTO)
				.getPmCountry());
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(getManagedBean().getPmAddress().getPmCountry()
				.getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStateList(resDTO.getStateList());
		addr.setPmState(new PmState());
		addr.setPmDistrict(new PmDistrict());
		addr.setPmCity(new PmCity());
		return addr;
	}
	public void saveFactoringDetails(PmFinancialOrg financialOrg){
		
		// Factoring company
		List<PmOrgProfile> orgProfileList = getManagedBean().getOrgProfileList();
		PmOrgProfile orgProfile = new PmOrgProfile();
		parameterReqDTO.setParameterType(ParameterTypeConst.ORGANISATION_TYPES);
		parameterReqDTO.setParameterCode(OrganizationType.FACTORING_COMPANY.value());
		ParameterResDTO parameterResDTO = parameterSO
				.retrieveParameterByTypeAndCode(parameterReqDTO);
		orgProfile.setOrgTypeCode(parameterResDTO.getPmParameter());
		orgProfile.setPmFinancialOrg(financialOrg);	
		orgProfileList.add(orgProfile);
		financialOrg.setPmOrgProfiles(orgProfileList);
		
		//factoring Account Details
		for (PmFinancialAcc financialAcc : this.getManagedBean()
				.getPmFinancialAcc()) {
			
			financialAcc.setPmPaymentBankOrg(financialAcc.getPmPaymentBankOrg());
			financialAcc.setPmFinancialOrg(financialOrg);
			log.info("Financial Account no"+financialAcc.getAccountNo());
		}
		//Factoring Bank Detials
          for (PmFinancialBranch financialBranchs : this.getManagedBean()
				.getPmFinancialBranch()) {
            //Factoring Branch Account Detials
			for (PmFinancialAcc financialBranchAcc : this.getManagedBean()
					.getPmFinancialBranchAcc()) {
				financialBranchAcc.setPmFinancialBranch(financialBranchs);
				financialBranchAcc.setPmPaymentBankOrg(financialBranchAcc.getPmPaymentBankOrg());
				log.info("Financial Branch Account no"+financialBranchAcc.getAccountNo());

			}
			
			financialBranchs.setPmFinancialAccs(getManagedBean()
					.getPmFinancialBranchAcc());

			financialBranchs.setPmFinancialOrg(financialOrg);
			// Factoring Branch Address
			PmAddressType brachAddress = getManagedBean()
					.getBranchAddressType();			
			brachAddress
					.setAddressType(AddressType.BILLING_ADDRESS
							.value());

			List<PmAddressType> branchAddressTypes = getManagedBean()
					.getBranchAddressList();
			branchAddressTypes.add(brachAddress);
			PmAddress branchAddress = getManagedBean()
					.getBranchAddress();
			PmContact contact = getManagedBean()
					.getBranchContactPerson();
			contact.setPmAddressType(brachAddress);
			List<PmContact> branchcontactLst = getManagedBean()
					.getBranchContact();
			branchcontactLst.add(contact);
			brachAddress.setPmAddress(branchAddress);
			brachAddress.setPmContacts(branchcontactLst);
			brachAddress.setPmFinancialBranch(financialBranchs);
			financialBranchs.setPmAddressTypes(branchAddressTypes);			
		}
		
		financialOrg.setPmFinancialAccs(this.getManagedBean()
				.getPmFinancialAcc());
		financialOrg.setPmFinancialBranchs(this.getManagedBean()
				.getPmFinancialBranch());

	}
	

	public SelectItem[] getServieTypeDropdownValues(String serviceType) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.BANK_SERVICES_PAYMENT_BANK);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		searchBean.getSortColumns();

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.BANK_SERVICES, searchBean);
		return items;
	}

}
