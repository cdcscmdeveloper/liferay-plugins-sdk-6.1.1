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
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
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
import my.ep.persistence.entity.pm.common.PmContact;
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
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
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
		retrievePtjGroupList();
		retrieveAgOfficeList();
		retrieveOrgProfileList();
		retrievePmCountryList();
		retrievePmStateList();
		retrievePmZoneList();
		// retrievePmFinancialOrgList();
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

		return "/xhtml/pm/organisation/organisationMain";
	}

	public void searchOrg() {

		searchOrganisation.reloadData();
	}

	public String addOrganisation() {

		return "/xhtml/pm/organisation/maintainOrganisationMain";
	}

	public void resetOrganisation() {
		getManagedBean().setOrgCode(null);
		getManagedBean().setOrgTypeCode(null);
		getManagedBean().setOrgName(null);
		getManagedBean().setOrgStatus(null);

	}

	public String saveOrganization() {
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

		PmOrgProfile orgProfile = new PmOrgProfile();

		orgProfile.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		orgProfile.setCreatedBy(getFacesScopes().getUserSessionBean()
				.getUserId());
		orgProfile.setChangedBy(getFacesScopes().getUserSessionBean()
				.getUserId());
		orgProfile.setOrgTypeCode(parameterResDTO.getPmParameter());
		orgProfile.setGovtSeller(getManagedBean().getOrgProfile()
				.isGovtSeller());
		orgProfile.setGroupOrg(getManagedBean().getOrgProfile().isGroupOrg());

		PmOrgValidity orgValidity = new PmOrgValidity();
		orgValidity.setOrgCode(getManagedBean().getOrgProfile()
				.getPmOrgValidity().getOrgCode());
		orgValidity.setOrgName(getManagedBean().getOrgProfile()
				.getPmOrgValidity().getOrgName());

		orgValidity.setPmOrgProfile(orgProfile);
		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.MINISTRY.value())) {
			orgValidity.setEffDate(getManagedBean().getOrgProfile()
					.getPmOrgValidity().getEffDate());
			orgValidity.setExpDate(getManagedBean().getOrgProfile()
					.getPmOrgValidity().getExpDate());

		}
		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.DIVISION.value())) {
			orgValidity.setEffDate(getManagedBean().getOrgProfile()
					.getPmOrgValidity().getEffDate());
			orgValidity.setExpDate(getManagedBean().getOrgProfile()
					.getPmOrgValidity().getExpDate());

		} else {
			orgValidity.setEffDate(new Date());
		}

		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.BPK_PEROLEHAN_PM_SABAH.value())|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value())) {
			orgValidity.setEffDate(new Date());
			PmZone zone = new PmZone();
			zone.setZoneId(getManagedBean().getZoneId());
			orgProfile.setPmZone(zone);

		}
		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value())) {
			orgValidity.setEffDate(getManagedBean().getCurrentDate());
			PmZone zone = new PmZone();
			zone.setZoneId(getManagedBean().getZoneId());
			orgProfile.setPmZone(zone);
		}

		PmAddressType officeAddress = getManagedBean().getPmAddressType();
		officeAddress.setAddressType(AddressType.RECEIVING_OFFICER_ADDRESS
				.value());
		List<PmAddressType> pmAddressTypes = new ArrayList<PmAddressType>();
		pmAddressTypes.add(officeAddress);
		
		/*  if (getManagedBean().getOrgType().equalsIgnoreCase(
		  OrganizationType.DIVISION.value())) {
			  List<PmAddressType> deliveryAddress = getManagedBean() .getAddressTypes();
			  log.info("DeliveryAddd : "+deliveryAddress.get(0).getPmAddress().getAddress1());
		  
		  for (PmAddressType addType : deliveryAddress) {
		  addType.setAddressType(AddressType.DELIVERY_ADDRESS.value());
		  addType.setPmOrgProfile(orgProfile); }
		  pmAddressTypes.addAll(deliveryAddress); }*/
		 

		PmContact contact = getManagedBean().getContactPerson();
		contact.setPmAddressType(officeAddress);
		List<PmContact> contactLst = new ArrayList<PmContact>();
		contactLst.add(contact);
		officeAddress.setPmContacts(contactLst);
		officeAddress.setPmOrgProfile(orgProfile);
		orgProfile.setPmAddressTypes(pmAddressTypes);

		orgProfile.setPmOrgValidity(orgValidity);
		reqDTO.setPmOrgProfile(orgProfile);

		OrgProfileResDTO resDTO = orgProfileSO.saveOrUpdateOrgProfile(reqDTO);

		if (resDTO.isSuccessful()) {
			result = resDTO.isSuccessful();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Org successfully.", "Org Name : "
									+ this.getManagedBean().getOrgProfile().getPmOrgValidity().getOrgName()));
		}

		return "/xhtml/pm/organisation/maintainOrganisationMain.xhtml";

	}

	public String editOrganization(PmOrgProfile orgProfile) {

		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);

		OrgProfileResDTO orgResDTO = orgProfileSO
				.retrieveOrgAddressByOrgId(reqDTO);
		orgProfile = orgResDTO.getPmOrgProfile();
		getManagedBean().setOrgType(
				orgProfile.getOrgTypeCode().getParameterCode());
		getManagedBean().getOrgProfile().setPmZone(orgProfile.getPmZone());
		
		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.BPK_PEROLEHAN_PM_SABAH.value())|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value())) {
			this.getManagedBean().setZoneId(orgProfile.getPmZone().getZoneId());
			
		}
		List<PmAddressType> pmAddressTypes = orgProfile.getPmAddressTypes();
		if (pmAddressTypes != null) {
			for (PmAddressType pmAddressType : pmAddressTypes) {
				if (pmAddressType.getAddressType().equalsIgnoreCase("R")) {
					this.getManagedBean().setPmAddress(
							pmAddressType.getPmAddress());

				}
				if (!pmAddressType.getPmContacts().isEmpty()) {
					this.getManagedBean().setContactPerson(
							pmAddressType.getPmContacts().get(0));
				}
			}
		}
		
		this.getManagedBean().setOrgValidity(
				orgResDTO.getPmOrgProfile().getPmOrgValidity());
		this.getManagedBean().setOrgProfile(orgResDTO.getPmOrgProfile());

		return "/xhtml/pm/organisation/maintainOrganisationMain.xhtml";

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
									getManagedBean().getOrgCode()));
				}
				if (getManagedBean().getOrgName() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmOrgValidity.orgName",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getOrgName()));
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

	public void okContacePersonAction() {

		// BO validation
		RequestContext.getCurrentInstance()
				.execute("contactPersonPopup.hide()");
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
		reqDTO.setOrgProfileId(71440L);
		OrgProfileResDTO resDTO = orgProfileSO
				.retrieveOrgProfileListByMinistry(reqDTO);

		this.getManagedBean().setOrgProfileList(resDTO.getOrgProfileList());
	}

	public void retrievePmStateList() {

		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(1l);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		List<PmState> list = resDTO.getStateList();

		this.getManagedBean().setStates(list);

	}

	private void retrievePmCountryList() {
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();
		this.getManagedBean().setCountries(resDTO.getCountryList());
	}

	public void retrievePmDistrictList(AjaxBehaviorEvent event) {
		Long stateId = (Long) event.getComponent().getAttributes().get("value");
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setStateId(stateId);
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);
		this.getManagedBean().setDistricts(resDTO.getDistrictList());
	}

	public void retrievePmCityList(AjaxBehaviorEvent event) {
		Long districtId = (Long) event.getComponent().getAttributes()
				.get("value");
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(districtId);
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICitySO.class);
		CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
		this.getManagedBean().setCitys(resDTO.getCityList());
	}

	private void retrievePmZoneList() {
		IZoneSO zoneSO = (IZoneSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IZoneSO.class);
		ZoneResDTO resDTO = zoneSO.retrieveZoneList();
		this.getManagedBean().setZones(resDTO.getZoneList());
	}

	public void getAddressSelectAll() {

		int selectedCount = 0;
		List<PmAddress> addressList = getManagedBean().getAddress();

		if (addressList != null) {
			for (PmAddress address : addressList) {
				if (address.isSelected()) {
					selectedCount++;
				}
			}
			setFlag(addressList.size() == selectedCount);
		}
	}

	public void selectAll(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();

		List<PmAddress> addressList = getManagedBean().getAddress();
		if (addressList != null) {
			for (PmAddress address : addressList) {
				if (flag) {
					address.setSelected(true);
				} else {
					address.setSelected(false);
				}
			}
		}

	}

	public void addDeliveyAddres() {
		SelectionType[] type = SelectionType.values();
		List<SelectItem> selectionTypes = new ArrayList<SelectItem>();

		for (SelectionType selectionType : type) {
			selectionTypes.add(new SelectItem(selectionType.value(),
					selectionType.name()));
		}
		this.getManagedBean().setSelections(selectionTypes);

	}

	/*
	 * public void deleteDeliveryAddres() {
	 * 
	 * List<PmAddress> addressList = getManagedBean().getAddress();
	 * List<PmAddress> selectedList = new ArrayList<PmAddress>(); for (PmAddress
	 * address : addressList) { if (address.isSelected()) {
	 * selectedList.add(address); } }
	 * 
	 * if (selectedList.size() == selectedList.size()) { selectedList.clear(); }
	 * else { for (PmAddress delvAddress : selectedList) {
	 * selectedList.remove(delvAddress); } } }
	 */
	public void addContactPerson() {
		SelectionType[] type = SelectionType.values();
		List<SelectItem> selectionTypes = new ArrayList<SelectItem>();

		for (SelectionType selectionType : type) {
			selectionTypes.add(new SelectItem(selectionType.value(),
					selectionType.name()));
		}
		this.getManagedBean().setSelections(selectionTypes);

	}

	public void addNewPtjAction() {

		List<PmOrgProfile> orgProfileList = this.getManagedBean()
				.getPmOrgProfile();
		if (orgProfileList == null) {
			orgProfileList = new ArrayList<PmOrgProfile>();
			this.getManagedBean().setPmOrgProfile(orgProfileList);
		}

		PmOrgProfile profile = new PmOrgProfile();

		profile.setPmOrgValidity(new PmOrgValidity());
		orgProfileList.add(profile);

	}

	public void deletePtjAction() {

		List<PmOrgProfile> openList = this.getManagedBean().getPmOrgProfile();

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

	public void addNewAddress() {

		List<PmAddressType> addressTypeList = this.getManagedBean()
				.getAddressTypes();
		if (addressTypeList == null) {
			addressTypeList = new ArrayList<PmAddressType>();
			this.getManagedBean().setAddressTypes(addressTypeList);
		}

		PmAddressType addressType = new PmAddressType();

		addressType.setPmAddress(new PmAddress());
		addressTypeList.add(addressType);

	}

	public void assignNewCert() {
		PmAddress address = new PmAddress();
		address.setPmState(new PmState());

		getManagedBean().setSelectedAddress(address);
		address.setPmAddressTypes(getManagedBean().getAddressTypes());

	}

	public void assignNewAddress() {
		PmAddressType addressType = new PmAddressType();
		addressType.setPmAddress(new PmAddress());
		addressType.setAddressType(AddressType.DELIVERY_ADDRESS.value());
		getManagedBean().setSelectedPmAddressType(addressType);
		addressType.setPmOrgProfile(getManagedBean().getOrgProfile());

	}

	public void deleteSelectedAdddress() {
		if (getManagedBean().getSelectedAddressTypes().size() > 0) {
			for (PmAddressType address : getManagedBean()
					.getSelectedAddressTypes()) {
				getManagedBean().getOrgProfile().getPmAddressTypes()
						.remove(address);
			}
			getManagedBean().getSelectedAddressTypes().clear();
		} else {
			System.out.println("Error");
		}
	}

	public void deleteDeliveryAddres() {

		List<PmAddressType> addressList = this.getManagedBean()
				.getAddressTypes();
		List<PmAddressType> selectedAddressList = this.getManagedBean()
				.getSelectedAddressTypes();
		if (addressList.size() == selectedAddressList.size()) {
			addressList.clear();
		} else {
			for (PmAddressType adresss : selectedAddressList) {
				addressList.remove(adresss);
			}
		}
	}

	/*
	 * public void deleteDeliveryAddres() {
	 * 
	 * List<PmAddressType> addressList = this.getManagedBean()
	 * .getAddressTypes();
	 * 
	 * if (null == addressList) { addressList = new ArrayList<PmAddressType>();
	 * }
	 * 
	 * List<PmAddressType> openSelectList = this.getManagedBean()
	 * .getSelectedAddressTypes(); if (null == openSelectList) { openSelectList
	 * = new ArrayList<PmAddressType>(); }
	 * 
	 * if (addressList.size() == openSelectList.size()) { addressList.clear(); }
	 * else { for (PmAddressType category : openSelectList)
	 * addressList.remove(category); }
	 * 
	 * 
	 * }
	 */

	public IPagingDataModel<PmOrgProfile> getSearchOrganisation() {
		return searchOrganisation;
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

	private void retrievePmFinancialOrgList() {
		IFinancialOrgSO reqSO = (IFinancialOrgSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IFinancialOrgSO.class);
		FinancialOrgResDTO resDTO = reqSO.retrieveFinancialOrgList();
		this.getManagedBean().setPmFinancialOrg(resDTO.getFinancialOrgList());
	}

	public void resetOrgProfile() {
		getManagedBean().getOrgProfile().isGovtSeller();
	}

	public void setSearchOrganisation(
			IPagingDataModel<PmOrgProfile> searchOrganisation) {
		this.searchOrganisation = searchOrganisation;
	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
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
