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
		searchFinancialOrg.reloadData(	}
	public String backToMain() {
		getManagedBean().setServiceType(null);
		return NAV_TO_SEARCH;
);

	}

	public void initialFinOrg() {
		getManagedBean().setServiceType(null);
		this.getManagedBean(	public String redirectToFinOrg(Long id) throws Exception {
		retrieveCountryList();
		retrieveBranchCountryList();
		retrievePmFinancialOrgList();

		if (id != null && id > 0) {
			maintainFinancialOrg(id);

		} else {
			initialFinOrg();
).setSelectedTabIndex(GENERRAL);
		getManagedBean().setFinancialOrg(new PmFinancialOrg());
		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		Co		}
		getManagedBean().setShowParentMsg(true);
		return NAV_TO_MAINTAIN;
	}
untryReqDTO countryReqDTO = new CountryReqDTO();
		countryReqDTO.setCountryCode("MY");
		getManagedBean().getFinancialOrg().setPmCountry(
				countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());

		getManagedBean().setPmFinancialAcc(new ArrayList<PmFina		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICountrySO.class);
ayList<PmFinancialAcc>());
		getManagedBean().setPmFinancialSvc(new ArrayList<		getManagedBean().getFinancialOrg().setPmCountry(countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());
Address());
		getManagedBean().setContactPerson(new PmContact()		getManagedBean().setPmFinancialBranch(new ArrayList<PmFinancialBranch>());
		getManagedBean().setPmFinancialBranchAcc(new ArrayList<PmFinancialAcc>());
eqDTO.setCountryId(getManagedBean().getPmAddress().getPmCoun		//Financial Org Address
nessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PRO		getManagedBean().getPmAddress().setPmCountry(countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());
agedBean().setStateList(resDTO.getSta		reqDTO.setCountryId(getManagedBean().getPmAddress().getPmCountry().getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStateSO.class);
ess(new PmAddress());
		getManagedBean().getBranchAddress().setPmCountry(
				countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());

		getManagedBean().getBranchAddress().setPmState(new PmState());
		getManagedBean().getBranchAddress().setPmDistrict(new PmDistrict());
		getManag		//Branch Address
an().getBranchAddress().setPmCity(new PmCity());

	}

	public Strin		getManagedBean().setBranchContactPerson(new PmContact());
		getManagedBean().getBranchAddress().setPmCountry(countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());
		getManagedBean().setBranchStateList(resDTO.getStateList());		
untryList();
		retrievePmFinancialOrgList();

		if (id != null && id > 0) {
			maintainFinancialOrg(id);

		} else {
			initialFinOrg();

		}
		getManagedBean().setShowParentMsg(true);
		return NAV_TO_MAINTAIN;
	}

	IFinancialOrgSO financialOrgSO = (IFinancialOrgSO) BusinessDelegate
			.getInstance().getServiceO


	public void saveFinancialOrg() {
		log.info("Save Method");

		FinancialOrgReqDTO reqDTO = new 	
Finan	public String saveFinancialOrg() {
Org.isBrandNew()) {

			for 		PmFinancialOrg financialOrg = this.getManagedBean().getFinancialOrg();
etPmFinancialSvc()) {			
				
					parameterReqDTO.setParameterType(ParameterTypeConst.BANK_SERVICES);
					p					.getPmFinancialSvc()) {
				parameterReqDTO
						.setParameterType(ParameterTypeConst.BANK_SERVICES);
				parameterReqDTO.setParameterId(financialSvc
						.getServiceTypeCode().getParameterId());
				ParameterResDTO parameterResDTO = parameterSO
						.retrieveParameterById(parameterReqDTO);
				financialSvc.setServiceTypeCode(parameterResDTO
						.getPmParameter());
	.getPmAddressType();
			addressType.setAddressType(AddressType.BILLING_ADDRESS.value());
			List<PmAddressType> pmAddressTypes = this.getManagedBean()
					.getAddressType();
			pmAddressTypes.add(addressType);
			PmAddress address = this.getManagedBean().getPmAddress();
			PmContact contact = this.getManagedBean().getContactPerson();
			contact.s				if (parameterResDTO.getPmParameter().getParameterCode().toString()
						.trim().contains(ParameterConst.BANK_SERVICES_FACTORING_COMPANY)) {
					
					savefactoringDetails(financialOrg);

				}
			}
			
etPmAddressType(addressType);
			List<PmContact> contactLst = this.getManagedBean().getPmContact();
			contactLst.add(contact);
			addressType.setPmContacts(contactLst);
			addressType.setPmAddress(address);
			addressType.setPmFinancialOrg(financialOrg);
			financialOrg.setPmAddressTypes(pmAddressTypes);
					addressType.setPmFinancialOrg(financialOrg);			
cs(this.getManagedBean()
				.getPmFinancialSvc());
	
TOnancialOrg(financialOrg);

		FinancialOrgResDTO resDTO = financialOrgSO
						FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
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

	public void maintainFinancialOrg(Long id) {
		this.getManagedBean().setSelectedTabIndex(GENERRAL);
		FinancialOrgReqDTO reqDTO = new FinancialO		return NAV_TO_SEARCH;
rgReqDTO();
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
		
		for (PmFinancialSvc financialSvc : getManagedBean()
				.getPmFinancialSvc()) {			
			
				parameterReqDTO.setParameterType(ParameterTypeConst.BANK_SERVICES);
				para
		for (PmFinancialSvc financialSvc : getManagedBean().getPmFinancialSvc()) {

			parameterReqDTO.setParameterType(ParameterTypeConst.BANK_SERVICES);
			parameterReqDTO.setParameterId(financialSvc.getServiceTypeCode()
					.getParameterId());
			ParameterResDTO parameterResDTO = parameterSO
					.retrieveParameterById(parameterReqDTO);
			financialSvc.setServiceTypeCode(parameterResDTO.getPmParameter());
			if (parameterResDTO.getPmParameter().getParameterCode().toString()
					.trim().contains(ParameterConst.BANK_SERVICES_FACTORING_COMPANY)) {
				getManagedBean().setServiceType(
						financialSvc.getServiceTypeCode().getParameterCode());

			}
ypes != null && !finAddressTypes.isEmpty()) {
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
					

					getManagedBean().setPmAddress(pmAddressType.getPmAddress());
					addrExist = true;
				}
				// financial contacts
				if (pmAddressType.getPmContacts() != null					System.out.println("Main Addresss"+pmAddressType.getPmAddress().getAddress1());
					System.out.println("Main City"+pmAddressType.getPmAddress().getPmCity().getCityName());
& !pmAddressType.getPmContacts().isEmpty()) {
					for (PmContact contactPerson : pmAddressType
							.getPmContacts())
						getManagedBean().setContactPerson(contactPerson);
				}
				else {

					List<PmContact> contactLst = getManagedBean()
							.getPmContact();
					getManagedBean().setContactPerson(new PmContact());
					PmContact contact = getManagedBean()
							.getContactPerson();
					contact.setPmAddressType(pmAddressType);
					contactLst.add(contact);
					pmAddressType.setPmContacts(contactLst)				} else {
	}
		if (!addrExist) {
			PmAddressType addressType = getManagedBean().getPmAddressType();
			finAddressTypes.add(addressType);
					PmContact contact = getManagedBean().getContactPerson();
);
			getManagedBean().setPmAddress(setDefaultAddr());
			PmAddress address = getManagedBean().getPmAddress();
			addressType.setPmAddress(address);
			List<PmContact> contactLst = getManagedBean().getPmContact();
			getManagedBean().setContactPerson(new PmCo			addressType.setAddressType(AddressType.BILLING_ADDRESS.value());
ontact.setPmAddressType(addressType);
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
		

		getManagedBean().setFinancialOrg(resDTO.getPmFinancialOrg());

	}

	public void addFinanciaervice() {
		
		if(this.getManagedBean().getPmFinancialSvc()!=null){
			if(this.getManagedBean().getPmFinancialSvc().size()>3){
				throw new EpException("Exceed the limit");
			}
		}	

		PmFinancialSvc pmFinancial		// financial account
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
							System.out.println("Branch Addresss"+branchAddressType.getPmAddress().getAddress1());
							System.out.println("BranchCity"+branchAddressType.getPmAddress().getPmCity().getCityName());
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
= new PmFinancialSvc();

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

		if (financialSvc != null && financialSvc.isBrandNew()) {
			if (this.getManagedBean().getSelectedPmFinancialSvc() == null) {

				this.getManagedBean().setSelectedPmFinancialSvc(
						new ArrayList<PmFinancialSvc>());
			}

			this.getManagedBean().getSelectedPmFinancialSvc()
					.remove(financialSvc);
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
		
		System.out.println("inside addFinancialBranchAcc ");
		if(null==getManagedBean().getPmFinancialBranch()){
			throw new EpException("Please select atleast one branch to proceed.");
		}else if(getManagedBean().getPmFinancialBranch().size()==0){
			throw new EpException("Please select atleast one branch to proceed.");
		}
		else{
			System.out.println("getPmFinancialBranch :"+getManagedBean().getPmFinancialBranch().size());
			
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
				.getMa			pmFinancialSvc.setServiceTypeCode(new PmParameter());
ranchList.size() == selectedFinancialBranchList.size()) {
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
			getManagedBean().setServiceType(parameterResDTO
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

	private void retrievePmFinancialOrgList() {
		IFinancialOrgSO reqSO = (IFinancialOrgSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IFinancialOrgSO.class);
		FinancialOrgResDTO resDTO = reqSO.retrieveFinancialOrgList();
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

		RequestContext.getCurrentInstance()
				.execute("branchContactPersonPopup.show()");
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
