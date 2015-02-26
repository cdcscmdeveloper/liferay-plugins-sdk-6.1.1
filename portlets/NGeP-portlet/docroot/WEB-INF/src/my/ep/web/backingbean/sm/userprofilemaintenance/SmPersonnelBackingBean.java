package my.ep.web.backingbean.sm.userprofilemaintenance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.common.myidentity.IMyIdentitySO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.common.myidentity.MyIdentityReqDTO;
import my.ep.dto.sm.common.myidentity.MyIdentityResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.userprofilemaintenance.SmPersonnelManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "smPersonnelBackingBean")
@RequestScoped
public class SmPersonnelBackingBean extends
		BaseBackingBean<SmPersonnelManagedBean> {

	private static Logger log = Logger.getLogger(SmPersonnelBackingBean.class);

	public SmPersonnelBackingBean() {
		SmCommonUtil.setMDCUserId();
	}

	public void preRenderSmPersonnel() {
		SmSupplier smSupplier = SmApplicationUtil.getCurrentSupplier();
		getManagedBean().setSmSupplier(smSupplier);
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode("MY");
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);
		getManagedBean().setDefaultCountryId(
				resDTO.getPmCountry().getCountryId());

		initAuthorizedPersonnels(smSupplier);
		getManagedBean().setDisableAddNewUser(validatePersonLimit(smSupplier));
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "userProfileSearch.xhtml");
	}

	public void preRenderUpdateOwnProfilePage() {
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode("MY");
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);
		getManagedBean().setDefaultCountryId(
				resDTO.getPmCountry().getCountryId());
		initUserPersonnelInfo();
		buildRoleList();
		getManagedBean().setIdTypes(getPmIdTypeDropdownValues());
		SelectItem role = new SelectItem();
		role.setLabel("Supplier Admin");
		role.setValue(RoleCode.BASIC_SUPPLIER_ADMIN.value());
		getManagedBean().getRoleList().add(role);

		fetchUpdateSmPersonnelAddresses(getManagedBean().getUpdateSmPersonnel());

		if (getManagedBean().getUpdateSmPersonnelMailingAddress()
				.getSmAddress() != null)
			getManagedBean().setTempAddress(
					getManagedBean().getUpdateSmPersonnelMailingAddress()
							.getSmAddress());

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "updateUserProfile.xhtml");
	}

	public void preRenderDeactivateSuppAdmin() {
		boolean isHasSupplierUser = false;
		SmSupplier smSupplier = SmApplicationUtil.getCurrentSupplier();
		SmPersonnel adminPersonnel = null;
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(smSupplier.getSupplierId());
		reqDTO.setApplId(smSupplier.getSmAppl().getApplId());
		reqDTO.setHasActiveAppl(false);
		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IPersonnelSO.class);

		PersonnelResDTO resDTO = smPersonnelSO
				.retrieveAuthorizedPersonnelsBySuppIdAndApplId(reqDTO);

		getManagedBean().setSmPersonnelList(new ArrayList<SmPersonnel>());

		String redirectPage = SmApplicationUtil.getApplicationErrorPage();
		FacesContext fc = FacesContext.getCurrentInstance();

		getManagedBean().setNoSupplierUserMsg("");

		if (isHavePendingTaskList()) {
			setDisallowedDeactivateSuppAdminMsg(fc);
		} else {
			redirectPage = "adminReAssign.xhtml";
			if (resDTO.getSmPersonnelList() != null
					&& !resDTO.getSmPersonnelList().isEmpty()) {
				List<SmPersonnel> authorizedPersonnelList = resDTO
						.getSmPersonnelList();
				for (SmPersonnel smPersonnelTemp : authorizedPersonnelList) {
					if (smPersonnelTemp.getEpRole().equals(
							RoleCode.BASIC_SUPPLIER_ADMIN.value())
							|| smPersonnelTemp.getEpRole().equals(
									RoleCode.MOF_SUPPLIER_ADMIN.value())) {
						adminPersonnel = smPersonnelTemp;
					} else if (smPersonnelTemp.getEpRole().equals(
							RoleCode.BASIC_SUPPLIER_USER.value())
							|| smPersonnelTemp.getEpRole().equals(
									RoleCode.MOF_SUPPLIER_USER.value())) {
						isHasSupplierUser = true;
					}
				}
				authorizedPersonnelList.remove(adminPersonnel);
				getManagedBean().setSmPersonnelList(authorizedPersonnelList);
				getManagedBean().setUpdateSmPersonnel(adminPersonnel);
				SmCommonUtil.setRoleName(smSupplier.getSmAppl()
						.getSupplierType(), getManagedBean()
						.getSmPersonnelList());
				if (!isHasSupplierUser) {
					constructNoSupplierUserToDeactivateMsg(smSupplier);
				}
			} else {
				getManagedBean().setSmPersonnelList(
						new ArrayList<SmPersonnel>());
				constructNoSupplierUserToDeactivateMsg(smSupplier);
			}
		}

		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, redirectPage);

	}

	public void resetSmPersonnelSearch() {
		this.getManagedBean().setSearchCompanyRole(null);
		this.getManagedBean().setSearchEPRole(null);
		this.getManagedBean().setSearchUsername(null);
	}

	private void buildRoleList() {
		List<SelectItem> roles = new ArrayList<SelectItem>();

		SelectItem role = new SelectItem();
		role.setLabel(SmCommonUtil.getMessage(
				"lbl.sm.person.composition.suppUser", null));
		role.setValue(RoleCode.BASIC_SUPPLIER_USER.value());
		if (!getManagedBean().isDisableRole()) {
			roles.add(role);
		}
		role = new SelectItem();
		role.setLabel(SmCommonUtil.getMessage(
				"lbl.sm.person.composition.catUser", null));
		role.setValue(RoleCode.CM_USER.value());
		roles.add(role);

		getManagedBean().setRoleList(roles);

		if (getManagedBean().getBasicSmPersonnel() != null
				&& getManagedBean().getBasicSmPersonnel().getEpRole() != null
				&& getManagedBean().getBasicSmPersonnel().getEpRole()
						.equals(RoleCode.BASIC_SUPPLIER_ADMIN.value())
				&& getManagedBean().isEditMode()) {
			SelectItem adminRole = new SelectItem();
			adminRole.setLabel(SmCommonUtil.getMessage(
					"lbl.sm.person.composition.suppAdmin", null));
			adminRole.setValue(RoleCode.BASIC_SUPPLIER_ADMIN.value());
			getManagedBean().getRoleList().add(adminRole);
		}
	}

	private boolean validatePersonLimit(SmSupplier smSupplier) {
		boolean validPerCount = false;

		// disabled add button if business type is individual (A)
		if (smSupplier != null
				&& smSupplier.getBusinessType().equalsIgnoreCase("A")) {
			validPerCount = true;
		} else {
			// disabled if no authorized personnel means application not in
			// Basic or Draft MOF
			if (getManagedBean().getSmPersonnelList() == null
					|| getManagedBean().getSmPersonnelList().isEmpty()) {
				validPerCount = true;
			} else {
				// disabled add button if authorized personnel that activated
				// already 3
				int count = 0;
				for (SmPersonnel smPersonnel : getManagedBean()
						.getSmPersonnelList()) {
					if (smPersonnel.getEpRole() != null
							&& StringUtils.isNotBlank(smPersonnel.getEpRole())) {
						count++;
					}
				}
				if (count >= 3) {
					validPerCount = true;
				}
			}

		}
		return validPerCount;
	}

	public String navigateToAddNewUserPage() {
		getManagedBean().setEditMode(false);
		getManagedBean().setDeactivatePersonnel(false);
		getManagedBean().setIdTypes(getPmIdTypeDropdownValues());
		removeEPRole();
		buildRoleList();

		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryId(getManagedBean().getDefaultCountryId());
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryById(reqDTO);

		// getManagedBean().setDisableRole(false);
		SmPersonnel smPersonnel = new SmPersonnel();
		getManagedBean().setAccountStatus(1);
		smPersonnel.setPmCountry(resDTO.getPmCountry());

		resDTO = reqSO.retrieveCountryById(reqDTO);
		SmAddress mailAddress = new SmAddress();
		mailAddress.setPmCountry(resDTO.getPmCountry());
		mailAddress.setPmState(new PmState());
		mailAddress.setPmDistrict(new PmDistrict());
		mailAddress.setPmCity(new PmCity());

		SmPersonnelAddress mailPersonnelAddress = new SmPersonnelAddress();
		mailPersonnelAddress.setAddressType(SupplierManagementConst.MAIL_ADD);
		mailPersonnelAddress.setSmAddress(mailAddress);
		mailPersonnelAddress.setSmPersonnel(smPersonnel);

		resDTO = reqSO.retrieveCountryById(reqDTO);
		SmAddress permAddress = new SmAddress();
		permAddress.setPmCountry(resDTO.getPmCountry());
		permAddress.setPmState(new PmState());
		permAddress.setPmDistrict(new PmDistrict());
		permAddress.setPmCity(new PmCity());

		SmPersonnelAddress permPersonnelAddress = new SmPersonnelAddress();
		permPersonnelAddress.setAddressType(SupplierManagementConst.PERM_ADD);
		permPersonnelAddress.setSmAddress(permAddress);
		permPersonnelAddress.setSmPersonnel(smPersonnel);

		getManagedBean()
				.setBasicSmPersonnelMailingAddress(mailPersonnelAddress);
		getManagedBean().setBasicSmPersonnelPermanentAddress(
				permPersonnelAddress);
		smPersonnel
				.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		smPersonnel.getSmPersonnelAddresses().add(
				getManagedBean().getBasicSmPersonnelPermanentAddress());
		smPersonnel.getSmPersonnelAddresses().add(
				getManagedBean().getBasicSmPersonnelMailingAddress());
		getManagedBean().setBasicSmPersonnel(smPersonnel);
		getCountryList();
		updateMailAddLists(getManagedBean().getDefaultCountryId().toString(),
				"", "");
		updatePermAddLists(getManagedBean().getDefaultCountryId().toString(),
				"", "");
		return "basicUserProfile.xhtml";
	}

	public void initUserPersonnelInfo() {
		FacesScopes facesScopes = new FacesScopes();
		PmUser usr = facesScopes.getUserSessionBean();
		getPersonnelByPmUser(usr);
	}

	private void initAuthorizedPersonnels(SmSupplier smSupplier) {
		getManagedBean().setSmPersonnelList(new ArrayList<SmPersonnel>());
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(smSupplier.getSupplierId());

		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IPersonnelSO.class);
		int countActiveAppl = smPersonnelSO
				.countSupplierWithActiveApplBySupplierId(reqDTO);
		PersonnelResDTO resDTO = null;
		if (countActiveAppl != 0) {
			// isActiveAppl eq 1 means already in Upgrade Mof Draft
			reqDTO.setHasActiveAppl(true);
			getManagedBean().setUpgradeMofDraftAppl(true);
		} else {
			// isActiveAppl eq 0 means still in Basic
			reqDTO.setHasActiveAppl(false);
			getManagedBean().setUpgradeMofDraftAppl(false);
		}

		// set latest active smAppl
		getManagedBean().setCurrSmAppl(getCurrActiveSmAppl(smSupplier));

		// set basic SmAppl
		getManagedBean().setBasicSmAppl(smSupplier.getSmAppl());

		resDTO = smPersonnelSO
				.retrieveAuthorizedPersonnelsForLatestActiveAppl(reqDTO);
		if (resDTO.getSmPersonnelList() != null
				&& !resDTO.getSmPersonnelList().isEmpty()) {
			getManagedBean().setSmPersonnelList(resDTO.getSmPersonnelList());
		}

		// set prev epRole
		if (getManagedBean().getSmPersonnelList() != null
				&& !getManagedBean().getSmPersonnelList().isEmpty()) {
			for (SmPersonnel smPersonnel : getManagedBean()
					.getSmPersonnelList()) {
				setPrevEpRole(smPersonnel);
			}
		}

		getManagedBean().setDeactivatePersonnel(false);

	}

	public String navigateToUpdateUserPage(String personnelId) {
		getManagedBean().setEditMode(true);
		getManagedBean().setIdTypes(getPmIdTypeDropdownValues());
		getPersonnelInfoById(personnelId);
		buildRoleList();
		validateAccountStatus();
		setDisabledActivateDeactivateRadioButton();
		setPersonnelViewOnlyData(getManagedBean().getBasicSmPersonnel());
		setDeactivateEpRole(getManagedBean().getBasicSmPersonnel());
		if (getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress() != null)
			getManagedBean().setTempAddress(
					getManagedBean().getBasicSmPersonnelMailingAddress()
							.getSmAddress());
		if (getManagedBean().isViewOnlyPersonnel()) {
			return "basicUserProfileViewOnly.xhtml";
		} else {
			return "basicUserProfile.xhtml";
		}
	}

	public String cancel() {
		return "userProfileSearch.xhtml";

	}

	public void confirmCreateBasicApplication() {

	}

	public void initializeLists() {
		getCountryList();

		if (getManagedBean().getBasicSmPersonnelMailingAddress() != null
				&& getManagedBean().getBasicSmPersonnelMailingAddress()
						.getSmAddress() != null) {
			SmAddress address = getManagedBean()
					.getBasicSmPersonnelMailingAddress().getSmAddress();
			if (address.getAddress1() != null) {
				updateMailAddLists(address.getPmCountry().getCountryId()
						.toString(), address.getPmState().getStateId()
						.toString(), address.getPmDistrict().getDistrictId()
						.toString());
			}
		} else {
			SmAddress s1 = new SmAddress();

			/* Construct Mailing Address Object */
			ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICountrySO.class);
			CountryReqDTO reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			CountryResDTO resCountryDTO = countrySO
					.retrieveCountryById(reqCountryDTO);
			s1.setPmCountry(resCountryDTO.getPmCountry());
			s1.setPmState(new PmState());
			s1.setPmDistrict(new PmDistrict());
			s1.setPmCity(new PmCity());

			SmPersonnelAddress mailPersonnelAddress = new SmPersonnelAddress();
			mailPersonnelAddress
					.setAddressType(SupplierManagementConst.MAIL_ADD);
			mailPersonnelAddress.setSmAddress(s1);
			mailPersonnelAddress.setSmPersonnel(getManagedBean()
					.getBasicSmPersonnel());
			getManagedBean().setBasicSmPersonnelMailingAddress(
					mailPersonnelAddress);
			getManagedBean().getBasicSmPersonnel().getSmPersonnelAddresses()
					.add(getManagedBean().getBasicSmPersonnelMailingAddress());
			updateMailAddLists(getManagedBean().getDefaultCountryId()
					.toString(), "", "");
		}

		if (getManagedBean().getBasicSmPersonnelPermanentAddress() != null
				&& getManagedBean().getBasicSmPersonnelPermanentAddress()
						.getSmAddress() != null) {
			SmAddress address = getManagedBean()
					.getBasicSmPersonnelPermanentAddress().getSmAddress();
			if (address.getAddress1() != null) {
				updatePermAddLists(address.getPmCountry().getCountryId()
						.toString(), address.getPmState().getStateId()
						.toString(), address.getPmDistrict().getDistrictId()
						.toString());
			}
		} else {
			SmAddress s1 = new SmAddress();

			/* Construct Permanent Address Object */
			ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICountrySO.class);
			CountryReqDTO reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			CountryResDTO resCountryDTO = countrySO
					.retrieveCountryById(reqCountryDTO);
			s1.setPmCountry(resCountryDTO.getPmCountry());
			s1.setPmState(new PmState());
			s1.setPmDistrict(new PmDistrict());
			s1.setPmCity(new PmCity());

			SmPersonnelAddress permPersonnelAddress = new SmPersonnelAddress();
			permPersonnelAddress
					.setAddressType(SupplierManagementConst.PERM_ADD);
			permPersonnelAddress.setSmAddress(s1);
			permPersonnelAddress.setSmPersonnel(getManagedBean()
					.getBasicSmPersonnel());
			getManagedBean().setBasicSmPersonnelPermanentAddress(
					permPersonnelAddress);
			getManagedBean()
					.getBasicSmPersonnel()
					.getSmPersonnelAddresses()
					.add(getManagedBean().getBasicSmPersonnelPermanentAddress());
			updatePermAddLists(getManagedBean().getDefaultCountryId()
					.toString(), "", "");
		}
	}

	public void initializeUpdateLists() {
		getCountryList();

		if (getManagedBean().getUpdateSmPersonnel().getSmPersonnelAddresses() != null
				&& getManagedBean().getUpdateSmPersonnel()
						.getSmPersonnelAddresses().size() > 0) {
			SmAddress address = new SmAddress();
			if (getManagedBean().getUpdateSmPersonnelMailingAddress() != null) {
				address = getManagedBean().getUpdateSmPersonnelMailingAddress()
						.getSmAddress();
				if (address.getAddress1() != null) {
					updateMailAddLists(address.getPmCountry().getCountryId()
							.toString(), address.getPmState().getStateId()
							.toString(), address.getPmDistrict()
							.getDistrictId().toString());
				}
			} else {
				SmAddress s1 = new SmAddress();

				/* Construct Mailing Address Object */
				ICountrySO countrySO = (ICountrySO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								ICountrySO.class);
				CountryReqDTO reqCountryDTO = new CountryReqDTO();
				reqCountryDTO.setCountryId(getManagedBean()
						.getDefaultCountryId());
				CountryResDTO resCountryDTO = countrySO
						.retrieveCountryById(reqCountryDTO);
				s1.setPmCountry(resCountryDTO.getPmCountry());
				s1.setPmState(new PmState());
				s1.setPmDistrict(new PmDistrict());
				s1.setPmCity(new PmCity());

				SmPersonnelAddress mailPersonnelAddress = new SmPersonnelAddress();
				mailPersonnelAddress
						.setAddressType(SupplierManagementConst.MAIL_ADD);
				mailPersonnelAddress.setSmAddress(s1);
				mailPersonnelAddress.setSmPersonnel(getManagedBean()
						.getUpdateSmPersonnel());
				getManagedBean().setUpdateSmPersonnelMailingAddress(
						mailPersonnelAddress);
				getManagedBean()
						.getUpdateSmPersonnel()
						.getSmPersonnelAddresses()
						.add(getManagedBean()
								.getUpdateSmPersonnelMailingAddress());
				updateMailAddLists(getManagedBean().getDefaultCountryId()
						.toString(), "", "");
			}
			if (getManagedBean().getUpdateSmPersonnelPermanentAddress() != null) {
				address = getManagedBean()
						.getUpdateSmPersonnelPermanentAddress().getSmAddress();
				if (address.getAddress1() != null) {
					updatePermAddLists(address.getPmCountry().getCountryId()
							.toString(), address.getPmState().getStateId()
							.toString(), address.getPmDistrict()
							.getDistrictId().toString());
				}
			} else {
				SmAddress s1 = new SmAddress();

				/* Construct Permanent Address Object */
				ICountrySO countrySO = (ICountrySO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								ICountrySO.class);
				CountryReqDTO reqCountryDTO = new CountryReqDTO();
				reqCountryDTO.setCountryId(getManagedBean()
						.getDefaultCountryId());
				CountryResDTO resCountryDTO = countrySO
						.retrieveCountryById(reqCountryDTO);
				s1.setPmCountry(resCountryDTO.getPmCountry());
				s1.setPmState(new PmState());
				s1.setPmDistrict(new PmDistrict());
				s1.setPmCity(new PmCity());

				SmPersonnelAddress permPersonnelAddress = new SmPersonnelAddress();
				permPersonnelAddress
						.setAddressType(SupplierManagementConst.PERM_ADD);
				permPersonnelAddress.setSmAddress(s1);
				permPersonnelAddress.setSmPersonnel(getManagedBean()
						.getUpdateSmPersonnel());
				getManagedBean().setUpdateSmPersonnelPermanentAddress(
						permPersonnelAddress);
				getManagedBean()
						.getUpdateSmPersonnel()
						.getSmPersonnelAddresses()
						.add(getManagedBean()
								.getUpdateSmPersonnelPermanentAddress());
				updatePermAddLists(getManagedBean().getDefaultCountryId()
						.toString(), "", "");
			}
		}
	}

	public void getCountryList() {

		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryReqDTO reqDTO = new CountryReqDTO();

		CountryResDTO resDTO = countrySO.retrieveCountryList();

		List<SelectItem> countryList1 = new ArrayList<SelectItem>();
		List<SelectItem> countryList2 = new ArrayList<SelectItem>();

		for (PmCountry country : resDTO.getCountryList()) {
			SelectItem si = new SelectItem();
			si.setLabel(country.getCountryName());
			si.setValue(country.getCountryId());
			countryList1.add(si);

			si = new SelectItem();
			si.setLabel(country.getCountryName());
			si.setValue(country.getCountryId());
			countryList2.add(si);
		}

		getManagedBean().setCountryList(countryList1);

		getManagedBean().setMailAddCountryList(countryList1);

		getManagedBean().setPermAddCountryList(countryList2);

	}

	public void updateMailAddLists(String countryId, String stateId,
			String districtId) {
		if (!countryId.isEmpty() || countryId.equalsIgnoreCase("null")) {
			getManagedBean().setMailAddStateList(
					SmCommonUtil.getStateList(Long.valueOf(countryId)));
			getManagedBean().setMailAddDistrictList(null);
			getManagedBean().setMailAddCityList(null);
		}
		if (!stateId.isEmpty() || stateId.equalsIgnoreCase("null")) {
			getManagedBean().setMailAddDistrictList(
					SmCommonUtil.getDistrictList(Long.valueOf(stateId)));
			getManagedBean().setMailAddCityList(null);
		}
		if (!districtId.isEmpty() || districtId.equalsIgnoreCase("null")) {
			getManagedBean().setMailAddCityList(
					SmCommonUtil.getCityList(Long.valueOf(districtId)));
		}
	}

	public void updatePermAddLists(String countryId, String stateId,
			String districtId) {
		if (!countryId.isEmpty() || countryId.equalsIgnoreCase("null")) {
			getManagedBean().setPermAddStateList(
					SmCommonUtil.getStateList(Long.valueOf(countryId)));
			getManagedBean().setPermAddDistrictList(null);
			getManagedBean().setPermAddCityList(null);
		}
		if (!stateId.isEmpty() || stateId.equalsIgnoreCase("null")) {
			getManagedBean().setPermAddDistrictList(
					SmCommonUtil.getDistrictList(Long.valueOf(stateId)));
			getManagedBean().setPermAddCityList(null);
		}
		if (!districtId.isEmpty() || districtId.equalsIgnoreCase("null")) {
			getManagedBean().setPermAddCityList(
					SmCommonUtil.getCityList(Long.valueOf(districtId)));
		}
	}

	public void setUpdateMailingAddrSameAsPerm() {
		if (getManagedBean().getUpdateSmPersonnelMailingAddress()
				.isSameAsPerm()) {
			SmAddress address = cloneAddress(getManagedBean()
					.getUpdateSmPersonnelPermanentAddress().getSmAddress());
			this.getManagedBean().getUpdateSmPersonnelMailingAddress()
					.setSmAddress(address);
			updateMailAddLists(this.getManagedBean()
					.getUpdateSmPersonnelMailingAddress().getSmAddress()
					.getPmCountry().getCountryId().toString(), this
					.getManagedBean().getUpdateSmPersonnelMailingAddress()
					.getSmAddress().getPmState().getStateId().toString(), this
					.getManagedBean().getUpdateSmPersonnelMailingAddress()
					.getSmAddress().getPmDistrict().getDistrictId().toString());
		} else {
			if (getManagedBean().getTempAddress() != null) {
				getManagedBean().getUpdateSmPersonnelMailingAddress()
						.setSmAddress(getManagedBean().getTempAddress());
			} else {
				getManagedBean().getUpdateSmPersonnelMailingAddress()
						.setSmAddress(setDefaultAddr());
			}
		}

	}

	private SmAddress cloneAddress(SmAddress refAddress) {
		SmAddress address = new SmAddress();
		if (refAddress.getAddress1() != null)
			address.setAddress1(new String(refAddress.getAddress1().toString()));
		if (refAddress.getAddress2() != null)
			address.setAddress2(new String(refAddress.getAddress2().toString()));
		if (refAddress.getAddress3() != null)
			address.setAddress3(new String(refAddress.getAddress3().toString()));
		if (refAddress.getAddress4() != null)
			address.setAddress4(new String(refAddress.getAddress4().toString()));
		if (refAddress.getPostcode() != null)
			address.setPostcode(new String(refAddress.getPostcode().toString()));

		/**** Cloning the address ******/
		PmCountry country = new PmCountry();
		PmState state = new PmState();
		PmDistrict district = new PmDistrict();
		PmCity city = new PmCity();

		country.setCountryId(refAddress.getPmCountry().getCountryId());
		address.setPmCountry(country);

		state.setStateId(refAddress.getPmState().getStateId());
		address.setPmState(state);

		district.setDistrictId(refAddress.getPmDistrict().getDistrictId());
		address.setPmDistrict(district);

		city.setCityId(refAddress.getPmCity().getCityId());
		address.setPmCity(city);

		return address;
	}

	private SmAddress setDefaultAddr() {
		SmAddress addr = new SmAddress();
		PmCountry cntry = new PmCountry();
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode("MY");
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);
		cntry = resDTO.getPmCountry();
		addr.setPmCity(new PmCity());
		addr.setPmCountry(cntry);
		addr.setPmState(new PmState());
		addr.setPmDistrict(new PmDistrict());

		return addr;
	}

	public void getPersonnelInfoById(String personnelId) {
		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();

		reqDTO.setPersonnelId(Long.valueOf(personnelId));

		PersonnelResDTO resDTO = smPersonnelSO.retrievePersonnelById(reqDTO);

		SmPersonnel smPersonnel = resDTO.getSmPersonnel();

		this.getManagedBean().setBasicSmPersonnel(smPersonnel);

		setPersonnelViewOnlyMode(getManagedBean().getBasicSmPersonnel());

		if (getManagedBean().isViewOnlyPersonnel()) {
			setPersonnelViewOnlyAddresses(smPersonnel);
		} else {
			List<SmPersonnelAddress> smPAList = new ArrayList<SmPersonnelAddress>();

			if (smPersonnel.getSmPersonnelAddresses() != null
					&& smPersonnel.getSmPersonnelAddresses().size() > 0) {

				smPersonnel.getSmPersonnelAddresses().size();
				smPAList.addAll(smPersonnel.getSmPersonnelAddresses());
				for (SmPersonnelAddress smPA : smPAList) {
					SmAddress address = new SmAddress();
					address.setPmCountry(new PmCountry());
					address.setPmState(new PmState());
					address.setPmDistrict(new PmDistrict());
					address.setPmCity(new PmCity());
					if (smPA.getAddressType().equalsIgnoreCase(
							SupplierManagementConst.MAIL_ADD)) {
						address.setAddress1(smPA.getSmAddress().getAddress1());
						address.setAddress2(smPA.getSmAddress().getAddress2());
						address.setAddress3(smPA.getSmAddress().getAddress3());
						address.setAddress4(smPA.getSmAddress().getAddress4());
						address.getPmCountry().setCountryId(
								smPA.getSmAddress().getPmCountry()
										.getCountryId());
						address.getPmState().setStateId(
								smPA.getSmAddress().getPmState().getStateId());
						address.getPmDistrict().setDistrictId(
								smPA.getSmAddress().getPmDistrict()
										.getDistrictId());
						address.getPmCity().setCityId(
								smPA.getSmAddress().getPmCity().getCityId());
						address.setPostcode(smPA.getSmAddress().getPostcode());
						smPA.setSmAddress(address);
						this.getManagedBean()
								.setBasicSmPersonnelMailingAddress(smPA);
					} else if (smPA.getAddressType().equalsIgnoreCase(
							SupplierManagementConst.PERM_ADD)) {
						address.setAddress1(smPA.getSmAddress().getAddress1());
						address.setAddress2(smPA.getSmAddress().getAddress2());
						address.setAddress3(smPA.getSmAddress().getAddress3());
						address.setAddress4(smPA.getSmAddress().getAddress4());
						address.getPmCountry().setCountryId(
								smPA.getSmAddress().getPmCountry()
										.getCountryId());
						address.getPmState().setStateId(
								smPA.getSmAddress().getPmState().getStateId());
						address.getPmDistrict().setDistrictId(
								smPA.getSmAddress().getPmDistrict()
										.getDistrictId());
						address.getPmCity().setCityId(
								smPA.getSmAddress().getPmCity().getCityId());
						address.setPostcode(smPA.getSmAddress().getPostcode());
						smPA.setSmAddress(address);
						this.getManagedBean()
								.setBasicSmPersonnelPermanentAddress(smPA);
					}
				}
				initializeLists();
			} else {

				SmAddress s1 = new SmAddress();
				SmAddress s2 = new SmAddress();

				/* Construct Mailing Address Object */
				ICountrySO countrySO = (ICountrySO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								ICountrySO.class);
				CountryReqDTO reqCountryDTO = new CountryReqDTO();
				reqCountryDTO.setCountryId(getManagedBean()
						.getDefaultCountryId());
				CountryResDTO resCountryDTO = countrySO
						.retrieveCountryById(reqCountryDTO);
				s1.setPmCountry(resCountryDTO.getPmCountry());
				s1.setPmState(new PmState());
				s1.setPmDistrict(new PmDistrict());
				s1.setPmCity(new PmCity());

				/* Construct Permanent Address Object */
				reqCountryDTO = new CountryReqDTO();
				reqCountryDTO.setCountryId(getManagedBean()
						.getDefaultCountryId());
				resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
				s2.setPmCountry(resCountryDTO.getPmCountry());
				s2.setPmState(new PmState());
				s2.setPmDistrict(new PmDistrict());
				s2.setPmCity(new PmCity());

				List<SmPersonnelAddress> smPersonnelAddresses = new ArrayList<SmPersonnelAddress>();
				SmPersonnelAddress permPersonnelAddress = new SmPersonnelAddress();
				permPersonnelAddress
						.setAddressType(SupplierManagementConst.PERM_ADD);
				permPersonnelAddress.setSmAddress(s2);
				permPersonnelAddress.setSmPersonnel(smPersonnel);
				getManagedBean().setBasicSmPersonnelPermanentAddress(
						permPersonnelAddress);
				SmPersonnelAddress mailPersonnelAddress = new SmPersonnelAddress();
				mailPersonnelAddress
						.setAddressType(SupplierManagementConst.MAIL_ADD);
				mailPersonnelAddress.setSmAddress(s1);
				mailPersonnelAddress.setSmPersonnel(smPersonnel);
				getManagedBean().setBasicSmPersonnelMailingAddress(
						mailPersonnelAddress);
				smPersonnelAddresses.add(getManagedBean()
						.getBasicSmPersonnelPermanentAddress());
				smPersonnelAddresses.add(getManagedBean()
						.getBasicSmPersonnelMailingAddress());
				smPersonnel.setSmPersonnelAddresses(smPersonnelAddresses);

				getCountryList();

				reqCountryDTO = new CountryReqDTO();
				reqCountryDTO.setCountryId(getManagedBean()
						.getDefaultCountryId());
				resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
				smPersonnel.setPmCountry(resCountryDTO.getPmCountry());
			}
		}

	}

	public void getPersonnelByPmUser(PmUser usr) {
		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setUserId(Long.valueOf(usr.getUserId()));
		PersonnelResDTO resDTO = smPersonnelSO
				.retrievePersonnelByUserId(reqDTO);
		SmPersonnel smPersonnel = resDTO.getSmPersonnel();
		this.getManagedBean().setUpdateSmPersonnel(smPersonnel);
	}

	public void fetchUpdateSmPersonnelAddresses(SmPersonnel smPersonnel) {
		if (smPersonnel.getSmPersonnelAddresses() != null
				&& smPersonnel.getSmPersonnelAddresses().size() > 0) {
			List<SmPersonnelAddress> smPAList = new ArrayList<SmPersonnelAddress>();

			smPersonnel.getSmPersonnelAddresses().size();
			smPAList.addAll(smPersonnel.getSmPersonnelAddresses());
			for (SmPersonnelAddress smPA : smPAList) {
				SmAddress address = new SmAddress();
				address.setPmCountry(new PmCountry());
				address.setPmState(new PmState());
				address.setPmDistrict(new PmDistrict());
				address.setPmCity(new PmCity());
				if (smPA.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.MAIL_ADD)) {
					address.setAddress1(smPA.getSmAddress().getAddress1());
					address.setAddress2(smPA.getSmAddress().getAddress2());
					address.setAddress3(smPA.getSmAddress().getAddress3());
					address.setAddress4(smPA.getSmAddress().getAddress4());
					address.getPmCountry().setCountryId(
							smPA.getSmAddress().getPmCountry().getCountryId());
					address.getPmState().setStateId(
							smPA.getSmAddress().getPmState().getStateId());
					address.getPmDistrict()
							.setDistrictId(
									smPA.getSmAddress().getPmDistrict()
											.getDistrictId());
					address.getPmCity().setCityId(
							smPA.getSmAddress().getPmCity().getCityId());
					address.setPostcode(smPA.getSmAddress().getPostcode());
					smPA.setSmAddress(address);
					this.getManagedBean().setUpdateSmPersonnelMailingAddress(
							smPA);
				} else if (smPA.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.PERM_ADD)) {
					address.setAddress1(smPA.getSmAddress().getAddress1());
					address.setAddress2(smPA.getSmAddress().getAddress2());
					address.setAddress3(smPA.getSmAddress().getAddress3());
					address.setAddress4(smPA.getSmAddress().getAddress4());
					address.getPmCountry().setCountryId(
							smPA.getSmAddress().getPmCountry().getCountryId());
					address.getPmState().setStateId(
							smPA.getSmAddress().getPmState().getStateId());
					address.getPmDistrict()
							.setDistrictId(
									smPA.getSmAddress().getPmDistrict()
											.getDistrictId());
					address.getPmCity().setCityId(
							smPA.getSmAddress().getPmCity().getCityId());
					address.setPostcode(smPA.getSmAddress().getPostcode());
					smPA.setSmAddress(address);
					this.getManagedBean().setUpdateSmPersonnelPermanentAddress(
							smPA);
				}
			}
			initializeUpdateLists();
		} else {

			SmAddress s1 = new SmAddress();
			SmAddress s2 = new SmAddress();

			/* Construct Mailing Address Object */
			ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICountrySO.class);
			CountryReqDTO reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			CountryResDTO resCountryDTO = countrySO
					.retrieveCountryById(reqCountryDTO);
			s1.setPmCountry(resCountryDTO.getPmCountry());
			s1.setPmState(new PmState());
			s1.setPmDistrict(new PmDistrict());
			s1.setPmCity(new PmCity());

			/* Construct Permanent Address Object */
			reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
			s2.setPmCountry(resCountryDTO.getPmCountry());
			s2.setPmState(new PmState());
			s2.setPmDistrict(new PmDistrict());
			s2.setPmCity(new PmCity());

			List<SmPersonnelAddress> smPersonnelAddresses = new ArrayList<SmPersonnelAddress>();
			SmPersonnelAddress permPersonnelAddress = new SmPersonnelAddress();
			permPersonnelAddress
					.setAddressType(SupplierManagementConst.PERM_ADD);
			permPersonnelAddress.setSmAddress(s1);
			permPersonnelAddress.setSmPersonnel(smPersonnel);
			getManagedBean().setUpdateSmPersonnelPermanentAddress(
					permPersonnelAddress);
			SmPersonnelAddress mailPersonnelAddress = new SmPersonnelAddress();
			mailPersonnelAddress
					.setAddressType(SupplierManagementConst.MAIL_ADD);
			mailPersonnelAddress.setSmAddress(s1);
			mailPersonnelAddress.setSmPersonnel(smPersonnel);
			getManagedBean().setUpdateSmPersonnelMailingAddress(
					mailPersonnelAddress);
			smPersonnelAddresses.add(getManagedBean()
					.getUpdateSmPersonnelPermanentAddress());
			smPersonnelAddresses.add(getManagedBean()
					.getUpdateSmPersonnelMailingAddress());
			smPersonnel.setSmPersonnelAddresses(smPersonnelAddresses);

			getCountryList();

			reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
			smPersonnel.setPmCountry(resCountryDTO.getPmCountry());

			updatePermAddLists(getManagedBean()
					.getUpdateSmPersonnelPermanentAddress().getSmAddress()
					.getPmCountry().getCountryId().toString(), "", "");
			updateMailAddLists(getManagedBean()
					.getUpdateSmPersonnelMailingAddress().getSmAddress()
					.getPmCountry().getCountryId().toString(), "", "");
		}
	}

	public void save() throws Exception {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (getManagedBean().getBasicSmPersonnel().getPersonnelId() == null) {
			getManagedBean().setBasicSmPersonnel(
					saveNewUser(getManagedBean().getBasicSmPersonnel(), fc));
			initAuthorizedPersonnels(getManagedBean().getSmSupplier());
			getManagedBean().setDisableAddNewUser(
					validatePersonLimit(getManagedBean().getSmSupplier()));
		} else {
			if (getManagedBean().getAccountStatus() == DbValueConst.RECORD_STATUS_ACTIVE) {
				updateUser(getManagedBean().getBasicSmPersonnel(), fc);
				initAuthorizedPersonnels(getManagedBean().getSmSupplier());
				getManagedBean().setDisableAddNewUser(
						validatePersonLimit(getManagedBean().getSmSupplier()));
			} else if (getManagedBean().getAccountStatus() == DbValueConst.RECORD_STATUS_SUSPENDED) {
				if (!getManagedBean().isDeactivatePersonnel()) {
					String[] params = { getManagedBean().getBasicSmPersonnel()
							.getName() };
					getManagedBean().setDeactivateTerminateConfirmMsg(
							SmCommonUtil.getMessage(
									"lbl.sm.userprofile.deactivate.personnel",
									params));
					RequestContext.getCurrentInstance().execute(
							"confirmDialog.show()");
				} else {
					getManagedBean().getBasicSmPersonnel().setEpRole(null);
					updateUser(getManagedBean().getBasicSmPersonnel(), fc);
					initAuthorizedPersonnels(getManagedBean().getSmSupplier());
					getManagedBean().setDisableAddNewUser(
							validatePersonLimit(getManagedBean()
									.getSmSupplier()));
				}
			} else if (getManagedBean().getAccountStatus() == DbValueConst.RECORD_STATUS_DELETED) {
				String[] params = { getManagedBean().getBasicSmPersonnel()
						.getName() };
				getManagedBean().setDeactivateTerminateConfirmMsg(
						SmCommonUtil.getMessage(
								"lbl.sm.userprofile.terminate.personnel",
								params));
				RequestContext.getCurrentInstance().execute(
						"confirmDialog.show()");
			}
		}
	}

	public void deactivateOrTerminate() throws Exception {
		FacesContext fc = FacesContext.getCurrentInstance();
		updateUser(getManagedBean().getBasicSmPersonnel(), fc);
		initAuthorizedPersonnels(getManagedBean().getSmSupplier());
		getManagedBean().setDisableAddNewUser(
				validatePersonLimit(getManagedBean().getSmSupplier()));
		getManagedBean().setDeactivateTerminateConfirmMsg("");
	}

	public void updateUser(SmPersonnel smPersonnel, FacesContext fc)
			throws Exception {
		PersonnelResDTO resDTO = updatePersonnel(smPersonnel);
		log.info("Result:" + resDTO.isSuccessful());
		if (resDTO.isSuccessful()) {
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_100002,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "userProfileSearch.xhtml");
			getManagedBean().setBasicSmPersonnel(resDTO.getSmPersonnel());
		}
	}

	public SmPersonnel saveNewUser(SmPersonnel smPersonnel, FacesContext fc) {

		validatePersonnelBeforeSave(smPersonnel);
		setAuthorizedFlag(smPersonnel);
		setPersonnelAddress(smPersonnel);

		if (smPersonnel.getIdentificationType().getParameterCode().equals("I")) {
			myIdentityVerification(smPersonnel);
		}

		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryReqDTO reqCountryDTO = new CountryReqDTO();
		reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
		CountryResDTO resCountryDTO = countrySO
				.retrieveCountryById(reqCountryDTO);
		PmCountry country = resCountryDTO.getPmCountry();
		smPersonnel.setPmCountry(country);

		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();

		FacesScopes fs = new FacesScopes();
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		reqDTO.setSmPersonnel(smPersonnel);
		reqDTO.setHasActiveAppl(getManagedBean().isUpgradeMofDraftAppl());
		reqDTO.setApplId(getManagedBean().getBasicSmAppl().getApplId());
		reqDTO.setBasicSmAppl(getManagedBean().getBasicSmAppl());
		if (reqDTO.isHasActiveAppl()) {
			reqDTO.setDraftApplId(getManagedBean().getCurrSmAppl().getApplId());
		} else {
			reqDTO.setDraftApplId(null);
		}

		PersonnelResDTO resDTO = smPersonnelSO.savePersonnelRecord(reqDTO);

		if (resDTO.isSuccessful()) {
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_721014,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "userProfileSearch.xhtml");
			return resDTO.getSmPersonnel();
		} else
			return null;
	}

	private PersonnelResDTO updatePersonnel(SmPersonnel smPersonnel)
			throws Exception {
		setAuthorizedFlag(smPersonnel);
		if (getManagedBean().getBasicSmPersonnelMailingAddress().isSameAsPerm()) {
			SmAddress address = cloneAddress(getManagedBean()
					.getBasicSmPersonnelPermanentAddress().getSmAddress());
			this.getManagedBean().getBasicSmPersonnelMailingAddress()
					.setSmAddress(address);
		}
		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		FacesScopes fs = new FacesScopes();
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		reqDTO.setSmPersonnel(smPersonnel);
		reqDTO.setAccountStatus(getManagedBean().getAccountStatus());
		reqDTO.setHasActiveAppl(getManagedBean().isUpgradeMofDraftAppl());
		reqDTO.setBasicSmAppl(getManagedBean().getBasicSmAppl());
		reqDTO.setSmSupplierId(getManagedBean().getSmSupplier().getSupplierId());
		if (reqDTO.isHasActiveAppl()) {
			reqDTO.setDraftApplId(getManagedBean().getCurrSmAppl().getApplId());
		} else {
			reqDTO.setDraftApplId(null);
		}
		return smPersonnelSO.updatePersonnelAccountStatusOrEpRole(reqDTO);
	}

	private void setPersonnelAddress(SmPersonnel smPersonnel) {

		SmAddress s1 = getManagedBean().getBasicSmPersonnelPermanentAddress()
				.getSmAddress();

		/* Construct Mailing Address Object */

		PmCity city = s1.getPmCity();
		PmDistrict district = s1.getPmDistrict();
		PmState state = s1.getPmState();
		PmCountry country = s1.getPmCountry();

		Long cityId = city.getCityId();
		Long districtId = district.getDistrictId();
		Long stateId = state.getStateId();
		Long countryId = country.getCountryId();

		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryReqDTO reqCountryDTO = new CountryReqDTO();
		reqCountryDTO.setCountryId(countryId);
		CountryResDTO resCountryDTO = countrySO
				.retrieveCountryById(reqCountryDTO);
		PmCountry newCountry = resCountryDTO.getPmCountry();

		s1.setPmCountry(newCountry);

		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateReqDTO reqStateDTO = new StateReqDTO();
		reqStateDTO.setStateId(stateId);
		StateResDTO resStateDTO = stateSO.retriveStateByID(reqStateDTO);
		PmState newState = resStateDTO.getPmState();

		s1.setPmState(newState);

		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);
		DistrictReqDTO reqDistrictDTO = new DistrictReqDTO();
		reqDistrictDTO.setDistrictId(districtId);
		DistrictResDTO resDistrictDTO = districtSO
				.retriveDistrictByID(reqDistrictDTO);
		PmDistrict newDistrict = resDistrictDTO.getPmDistrict();

		s1.setPmDistrict(newDistrict);

		ICitySO citySO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICitySO.class);
		CityReqDTO reqCityDTO = new CityReqDTO();
		reqCityDTO.setCityId(cityId);
		CityResDTO resCityDTO = citySO.retriveCityById(reqCityDTO);
		PmCity newCity = resCityDTO.getPmCity();

		s1.setPmCity(newCity);

		if (getManagedBean().getBasicSmPersonnelMailingAddress().isSameAsPerm()) {
			SmAddress address = cloneAddress(getManagedBean()
					.getBasicSmPersonnelPermanentAddress().getSmAddress());
			this.getManagedBean().getBasicSmPersonnelMailingAddress()
					.setSmAddress(address);
		} else {
			SmAddress s2 = getManagedBean().getBasicSmPersonnelMailingAddress()
					.getSmAddress();

			/* Construct Permanent Address Object */

			city = s2.getPmCity();
			district = s2.getPmDistrict();
			state = s2.getPmState();
			country = s2.getPmCountry();

			cityId = city.getCityId();
			districtId = district.getDistrictId();
			stateId = state.getStateId();
			countryId = country.getCountryId();

			reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(countryId);
			resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
			newCountry = resCountryDTO.getPmCountry();

			s2.setPmCountry(newCountry);

			reqStateDTO = new StateReqDTO();
			reqStateDTO.setStateId(stateId);
			resStateDTO = stateSO.retriveStateByID(reqStateDTO);
			newState = resStateDTO.getPmState();

			s2.setPmState(newState);

			reqDistrictDTO = new DistrictReqDTO();
			reqDistrictDTO.setDistrictId(districtId);
			resDistrictDTO = districtSO.retriveDistrictByID(reqDistrictDTO);
			newDistrict = resDistrictDTO.getPmDistrict();

			s2.setPmDistrict(newDistrict);

			reqCityDTO = new CityReqDTO();
			reqCityDTO.setCityId(cityId);
			resCityDTO = citySO.retriveCityById(reqCityDTO);
			newCity = resCityDTO.getPmCity();

			s2.setPmCity(newCity);
		}

		List<SmPersonnelAddress> smPersonnelAddresses = new ArrayList<SmPersonnelAddress>();

		smPersonnelAddresses.add(getManagedBean()
				.getBasicSmPersonnelPermanentAddress());
		smPersonnelAddresses.add(getManagedBean()
				.getBasicSmPersonnelMailingAddress());

		smPersonnel.setSmPersonnelAddresses(smPersonnelAddresses);

	}

	private void validatePersonnelBeforeSave(SmPersonnel smPersonnel) {

		if (smPersonnel.getIdentificationType().getParameterCode()
				.equalsIgnoreCase("I")) {
			if (smPersonnel.getIdentificationNo().length() != 12) {
				throw new EpException(MessageConst.ERROR_722532,
						new String[] { smPersonnel.getIdentificationNo() });
			}
		}

		for (SmPersonnel per : getManagedBean().getSmPersonnelList()) {
			if (smPersonnel.getIdentificationNo() != null
					&& smPersonnel.getIdentificationNo().equals(
							per.getIdentificationNo())) {
				throw new EpException(MessageConst.ERROR_722524,
						new String[] { smPersonnel.getIdentificationNo() });

			}
		}

		if (getManagedBean().isUpgradeMofDraftAppl()) {
			IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT,
							IPersonnelSO.class);
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setIdentificationNo(smPersonnel.getIdentificationNo());
			reqDTO.setSmSupplierId(getManagedBean().getSmSupplier()
					.getSupplierId());

			PersonnelResDTO resDTO = smPersonnelSO
					.retrieveDraftPersonnelByIdentificationNoAndSupplierId(reqDTO);
			if (resDTO.getSmPersonnel() != null) {
				throw new EpException(MessageConst.ERROR_722589);
			}
		}
	}

	public void myIdentityVerification(SmPersonnel per) {
		String postCode = "";
		for (SmPersonnelAddress addr : per.getSmPersonnelAddresses()) {
			if (addr.getAddressType() != null
					&& addr.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)
					&& addr.getSmAddress() != null) {
				postCode = addr.getSmAddress().getPostcode();
			}
		}
		IMyIdentitySO myIdentitySO = (IMyIdentitySO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IMyIdentitySO.class);
		MyIdentityReqDTO reqDTO = new MyIdentityReqDTO();
		reqDTO.setName(per.getName());
		reqDTO.setNewIcNo(per.getIdentificationNo());
		reqDTO.setType("Online");
		reqDTO.setPostCode(postCode);

		MyIdentityResDTO resDTO = null;
		StringBuffer sb = new StringBuffer();
		sb.append("MessageConst.ERROR_MYIDENTITY_");

		try {
			resDTO = myIdentitySO.myIdentityVerification(reqDTO);
			// StringBuffer sb = new StringBuffer();
			if (!resDTO.getStatusCode().equalsIgnoreCase(
					SupplierManagementConst.SM_MYIDENTITY_STATUS_CODE_VALID)) {
				sb.append(resDTO.getStatusCode());
				throw new EpException(sb.toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new EpException(e.getMessage());
		}

	}

	public String getCompanyId() {
		return EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.PORTAL_COMPANY_ID);
	}

	public boolean isExistEmailInPmUser(String email) {
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setEmail(email);

		UserResDTO resDTO = userSO.retrieveUserByEmail(reqDTO);

		if (resDTO.getUserList().size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isExistUserByIdentificationNo(String idNo) {
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setLoginId(idNo);
		UserResDTO resDTO = userSO.retrieveUserByLoginId(reqDTO);

		if (resDTO.getPmUser() != null) {
			return true;
		} else {
			return false;
		}
	}

	public String update(SmPersonnel smPersonnel) {

		setUpdateMailingAddrSameAsPerm();
		// boolean valid = true;
		try {
			// if(valid){

			IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT,
							IPersonnelSO.class);
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();

			reqDTO.setSmPersonnel(smPersonnel);

			PersonnelResDTO resDTO = smPersonnelSO
					.updatePersonnelRecord(reqDTO);

			if (resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_100002,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return "/xhtml/sm/userProfileMaintenance/outputMsg";

			} else
				return null;
			// } else {
			// return null;
			// }
		} finally {
			// do nothing
		}
	}

	public void assignAccountPrivilege(AjaxBehaviorEvent e) {
		if (getManagedBean().getBasicSmPersonnel().getAuthorizedFlg() == 0) {
			getManagedBean().getBasicSmPersonnel().setSoftcertFlg(0);
			getManagedBean().getBasicSmPersonnel().setContractSignerFlg(0);
		}

		if (getManagedBean().getBasicSmPersonnel().getSoftcertFlg() == 0) {
			getManagedBean().getBasicSmPersonnel().setContractSignerFlg(0);
		}
	}

	public void removeEPRole() {
		int smUserListSize = 0;
		if (getManagedBean().getSmPersonnelDataModel2().getList() != null)
			smUserListSize = getManagedBean().getSmPersonnelDataModel2()
					.getList().size();
		if (smUserListSize < 5)
			getManagedBean().setDisableRole(false);
		else
			getManagedBean().setDisableRole(true);
	}

	public void assignPersonIdentType(AjaxBehaviorEvent e) {
		if (getManagedBean().getBasicSmPersonnel().getIdentificationTypeId() != null) {
			getManagedBean().getBasicSmPersonnel().setIdentificationType(
					retrievePmParameterById(getManagedBean()
							.getBasicSmPersonnel().getIdentificationTypeId()));
			if (getManagedBean().getBasicSmPersonnel().getIdentificationType() == null) {
				getManagedBean().getBasicSmPersonnel().setIdentificationType(
						new PmParameter());
			}
		} else {
			getManagedBean().getBasicSmPersonnel().setIdentificationType(
					new PmParameter());
		}
	}

	private PmParameter retrievePmParameterById(Long paramId) {
		IParameterSO reqSO = (IParameterSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterId(paramId);
		ParameterResDTO resDTO = reqSO.retrieveParameterById(reqDTO);

		return resDTO.getPmParameter();
	}

	public void validateAccountStatus() {
		if (getManagedBean().getBasicSmPersonnel().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
				&& getManagedBean().getBasicSmPersonnel().getPmUser() != null
				&& StringUtils.isNotBlank(getManagedBean()
						.getBasicSmPersonnel().getEpRole())) {
			getManagedBean()
					.setAccountStatus(DbValueConst.RECORD_STATUS_ACTIVE);
		} else if (getManagedBean().getBasicSmPersonnel().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
				&& getManagedBean().getBasicSmPersonnel().getPmUser() != null
				&& StringUtils.isBlank(getManagedBean().getBasicSmPersonnel()
						.getEpRole())) {
			getManagedBean().setAccountStatus(
					DbValueConst.RECORD_STATUS_SUSPENDED);
		}
	}

	/**
	 * retrieve identity type from PM
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getPmIdTypeDropdownValues() {
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescList(ParameterTypeConst.IDENTIFICATION_TYPES);
		Map<Long, Object> identityType = new HashMap<Long, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			identityType.put(pmDesc.getPmParameter().getParameterId(),
					pmDesc.getPmParameter());
		}

		List<SelectItem> itemList = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownValues(ParameterTypeConst.IDENTIFICATION_TYPES);

		for (SelectItem selectItem : items) {

			if (selectItem.getValue() == null) {
				itemList.add(selectItem);
			} else {
				PmParameter pmParameter = (PmParameter) identityType
						.get(selectItem.getValue());
				if (!pmParameter.getParameterCode().equals("A")) {
					itemList.add(selectItem);
				}
			}

		}

		return itemList.toArray(new SelectItem[itemList.size()]);

	}

	public void assignVerifyIdentType(AjaxBehaviorEvent e) {
		if (getManagedBean().getAdminVerifyIdType().getParameterId() != null) {
			getManagedBean().setAdminVerifyIdType(
					retrievePmParameterById(getManagedBean()
							.getAdminVerifyIdType().getParameterId()));
			if (getManagedBean().getAdminVerifyIdType() == null) {
				getManagedBean().setAdminVerifyIdType(new PmParameter());
			}
		} else {
			getManagedBean().setAdminVerifyIdType(new PmParameter());
		}
	}

	public void confirmDeactivateAdmin() {
		if (getManagedBean().getSelectedPersonnelToReassign() == null) {
			throw new EpException(MessageConst.ERROR_721111);
		} else {
			RequestContext.getCurrentInstance().execute("confirmDialog.show()");
		}
	}

	public void deactivateAdmin() {
		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
		reqDTO.setAdminPersonnel(getManagedBean().getUpdateSmPersonnel());
		reqDTO.setSmPersonnel(getManagedBean().getSelectedPersonnelToReassign());
		reqDTO.setSmSupplierId(SmApplicationUtil.getCurrentSupplier()
				.getSupplierId());
		reqDTO.setOldAdminRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);

		PersonnelResDTO resDTO = new PersonnelResDTO();
		resDTO = smPersonnelSO.reassignNewSupplierAdmin(reqDTO);

		if (resDTO.isSuccessful()) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_721027,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,SmApplicationUtil.getApplicationErrorPage());
		} else {
			throw new EpException(MessageConst.ERROR_721112);
		}
	}

	public void onChangeAccntStatus(AjaxBehaviorEvent e) {

	}

	private SmAppl getCurrActiveSmAppl(SmSupplier smSupplier) {
		if (getManagedBean().isUpgradeMofDraftAppl()) {
			MofRegistrationReqDTO mofRegistrationReqDTO = new MofRegistrationReqDTO();
			mofRegistrationReqDTO.setSupplierId(smSupplier.getSupplierId());

			IMofRegistrationSO reqSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT,
							IMofRegistrationSO.class);
			return reqSO.retrieveActiveSmApplBySupp(mofRegistrationReqDTO)
					.getSmAppl();
		} else {
			return smSupplier.getSmAppl();
		}
	}

	private void constructNoSupplierUserToDeactivateMsg(SmSupplier smSupplier) {
		if (smSupplier.getSmAppl().getApplType()
				.equals(ParameterConst.BASIC_ACCOUNT)) {
			getManagedBean()
					.setNoSupplierUserMsg(
							SmCommonUtil
									.getMessage(
											"lbl.sm.deactivate.nosupplieruser.basic.msg",
											null));
		} else {
			getManagedBean().setNoSupplierUserMsg(
					SmCommonUtil.getMessage(
							"lbl.sm.deactivate.nosupplieruser.mof.msg", null));
		}
	}

	private void setAuthorizedFlag(SmPersonnel smPersonnel) {
		if (smPersonnel != null && smPersonnel.getEpRole() != null
				&& StringUtils.isNotBlank(smPersonnel.getEpRole())) {
			if (smPersonnel.getEpRole().equals(RoleCode.CM_USER.value())) {
				smPersonnel.setSoftcertFlg(0);
				smPersonnel.setAuthorizedFlg(Integer
						.parseInt(ParameterConst.FLAG_YES));
			} else {
				smPersonnel.setSoftcertFlg(2);
				smPersonnel.setAuthorizedFlg(Integer
						.parseInt(ParameterConst.FLAG_YES));
			}
		}
	}

	private void setDisallowedDeactivateSuppAdminMsg(FacesContext fc) {
		fc.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_721213,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
	}

	private void setDisabledActivateDeactivateRadioButton() {
		getManagedBean().setDisabledActivateAndDeactivate(false);
		if (getManagedBean().getBasicSmPersonnel() != null
				&& getManagedBean().getBasicSmPersonnel().getEpRole() != null) {
			if (getManagedBean().getBasicSmPersonnel().getEpRole()
					.equals(RoleCode.BASIC_SUPPLIER_ADMIN.value())
					|| getManagedBean().isUpgradeMofDraftAppl()) {
				getManagedBean().setDisabledActivateAndDeactivate(true);
			}
		}
		if (getManagedBean().isDisableAddNewUser()
				&& getManagedBean().getAccountStatus() == DbValueConst.RECORD_STATUS_SUSPENDED) {
			getManagedBean().setDisabledActivateAndDeactivate(true);
		}
	}

	private void setPersonnelViewOnlyMode(SmPersonnel smPersonnel) {
		getManagedBean().setViewOnlyPersonnel(false);
		if (getManagedBean().isEditMode() && smPersonnel.getPmUser() == null
				|| smPersonnel.getPmUser().getUserId() == null) {
			{
				getManagedBean().setViewOnlyPersonnel(true);
			}
		}
	}

	private void setPersonnelViewOnlyData(SmPersonnel smPersonnel) {
		if (getManagedBean().isViewOnlyPersonnel()) {
			getManagedBean()
					.setSelectedPersonnelIdentificationType(
							SmCommonUtil
									.getParamDescriptionByLanguageAndPmParameter(smPersonnel
											.getIdentificationType()));
			for (SelectItem item : getManagedBean().getRoleList()) {
				if (smPersonnel.getEpRole().equals(item.getValue())) {
					smPersonnel.setEpRole(item.getLabel());
					break;
				}
			}
		}
	}

	private void setPersonnelViewOnlyAddresses(SmPersonnel smPersonnel) {
		if (getManagedBean().isViewOnlyPersonnel()) {
			if (smPersonnel.getSmPersonnelAddresses() != null
					&& smPersonnel.getSmPersonnelAddresses().size() > 0) {
				for (SmPersonnelAddress personnelAddress : smPersonnel
						.getSmPersonnelAddresses()) {
					if (personnelAddress
							.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)) {
						if (personnelAddress.getSmAddress() != null) {
							getManagedBean()
									.setBasicSmPersonnelPermanentAddress(
											personnelAddress);
						}
					} else if (personnelAddress
							.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)) {
						if (personnelAddress.getSmAddress() != null) {
							getManagedBean().setBasicSmPersonnelMailingAddress(
									personnelAddress);
						}
					}
				}
			}
		}
	}

	private boolean isHavePendingTaskList() {
		boolean retVal = false;
		IBpmSO bpmSO = null;
		BpmRequestDTO reqDTO = null;
		BpmResponseDTO responseDTO = null;
		try {
			bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			reqDTO = new BpmRequestDTO();
			reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
			reqDTO.setOffset(1);
			reqDTO.setState("ASSIGNED");
			reqDTO.setDocType("");

			reqDTO.setViewBy("");
			reqDTO.setAssignment("");
			// get bpm token only if not yet authenticate
			if (getManagedBean().getToken() == null
					|| StringUtils.isBlank(getManagedBean().getToken())) {
				FacesScopes face = new FacesScopes();
				getManagedBean().setToken(face.getBPMAuthenticationCtxToken());
			}
			reqDTO.setCtxToken(getManagedBean().getToken());
			responseDTO = bpmSO.getWorklistItems(reqDTO);
			if (responseDTO.getWorklistVO() != null) {
				if (responseDTO.getWorklistVO().getTotal() > 0) {
					retVal = true;
				}
			}
		} finally {
			bpmSO = null;
			reqDTO = null;
			responseDTO = null;
		}
		return retVal;
	}

	private void setDeactivateEpRole(SmPersonnel personnel) {
		getManagedBean().setDeactivatePersonnel(false);
		if (personnel != null && personnel.getEpRole() == null
				&& StringUtils.isBlank(personnel.getEpRole())
				&& personnel.getPmUser() != null
				&& personnel.getPmUser().getPmUserOrgs() != null
				&& !personnel.getPmUser().getPmUserOrgs().isEmpty()) {
			getManagedBean().setDeactivatePersonnel(true);
			personnel.setEpRole(getDeactivateEpRole(personnel));
		}
	}

	private void setPrevEpRole(SmPersonnel personnel) {
		if (personnel.getEpRole() == null) {
			personnel.setPrevEpRole(getDeactivateEpRole(personnel));
		} else {
			personnel.setPrevEpRole(personnel.getEpRole());
		}
	}

	private String getDeactivateEpRole(SmPersonnel personnel) {
		String epRole = "";
		if (personnel != null && personnel.getEpRole() == null
				&& StringUtils.isBlank(personnel.getEpRole())
				&& personnel.getPmUser() != null
				&& personnel.getPmUser().getPmUserOrgs() != null
				&& !personnel.getPmUser().getPmUserOrgs().isEmpty()) {
			List<PmUserOrg> pmUserOrgs = personnel.getPmUser().getPmUserOrgs();
			PmUserOrg pmUserOrg = new PmUserOrg();
			for (PmUserOrg org : pmUserOrgs) {
				if (org.getPmOrgProfile().getOrgTypeCode().getParameterCode()
						.equals(ParameterConst.ORG_TYPE_SUPPLIER)) {
					pmUserOrg = org;
					break;
				}
			}
			if (pmUserOrg != null && pmUserOrg.getPmUserRoles() != null
					&& !pmUserOrg.getPmUserRoles().isEmpty()) {
				for (PmUserRole pmUserRole : pmUserOrg.getPmUserRoles()) {
					if (pmUserRole.getPmRole().getRoleCode()
							.equals(RoleCode.BASIC_SUPPLIER_USER.value())
							|| pmUserRole
									.getPmRole()
									.getRoleCode()
									.equals(RoleCode.BASIC_SUPPLIER_ADMIN
											.value())
							|| pmUserRole.getPmRole().getRoleCode()
									.equals(RoleCode.CM_USER.value())) {
						epRole = pmUserRole.getPmRole().getRoleCode();
						break;
					}
				}
			}
		}
		return epRole;
	}
}
