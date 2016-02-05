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

import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
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
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.userprofilemaintenance.SmPersonnelManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "smPersonnelBackingBean")
@RequestScoped
public class SmPersonnelBackingBean extends
		BaseBackingBean<SmPersonnelManagedBean> {

	private static Logger log = Logger.getLogger(SmPersonnelBackingBean.class);

	/**
	 * Instantiate SmPersonnelBackingBean and set MDC user id
	 */
	public SmPersonnelBackingBean() {
		SmCommonUtil.setMDCUserId();
	}

	/**
	 * Pre-render and redirect to Supplier User Maintenance screen
	 */
	public void preRenderSmPersonnel() {
		SmSupplier smSupplier = SmApplicationUtil.getCurrentSupplier();
		getManagedBean().setSmSupplier(smSupplier);
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode("MY");
		CountryResDTO resDTO = SmServiceLookup.getCountrySO()
				.retrieveCountryByCode(reqDTO);
		getManagedBean().setDefaultCountryId(
				resDTO.getPmCountry().getCountryId());

		initAuthorizedPersonnels(smSupplier);
		getManagedBean().setDisableAddNewUser(validatePersonLimit(smSupplier));
		getManagedBean().setSalutationItems(SmCommonUtil.getSalutationItems());
		SmCommonUtil.redirectToPage("userProfileSearch.xhtml", "", null);
	}

	/**
	 * Pre - render and redirect to Supplier User Profile screen
	 */
	public void preRenderUpdateOwnProfilePage() {
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode("MY");
		CountryResDTO resDTO = SmServiceLookup.getCountrySO()
				.retrieveCountryByCode(reqDTO);
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

		SmCommonUtil.redirectToPage("updateUserProfile.xhtml", "", null);
	}

	/**
	 * Pre - render and redirect to Deactivate Supplier Admin screen
	 */
	public void preRenderDeactivateSuppAdmin() {
		boolean isHasSupplierUser = false;
		SmSupplier smSupplier = SmApplicationUtil.getCurrentSupplier();
		SmPersonnel adminPersonnel = null;
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(smSupplier.getSupplierId());
		reqDTO.setApplId(smSupplier.getSmAppl().getApplId());
		reqDTO.setHasActiveAppl(false);

		PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO()
				.retrieveAuthorizedPersonnelsBySuppIdAndApplId(reqDTO);

		getManagedBean().setSmPersonnelList(new ArrayList<SmPersonnel>());

		String redirectPage = SmApplicationUtil.getApplicationErrorPage();

		getManagedBean().setNoSupplierUserMsg("");

		if (isHavePendingTaskList(smSupplier)) {
			SmCommonUtil.redirectToPage(redirectPage, MessageConst.INFO_721213,
					null);
		} else {
			redirectPage = "adminReAssign.xhtml";
			if (resDTO.getSmPersonnelList() != null
					&& !resDTO.getSmPersonnelList().isEmpty()) {
				List<SmPersonnel> authorizedPersonnelList = resDTO
						.getSmPersonnelList();
				for (SmPersonnel smPersonnelTemp : authorizedPersonnelList) {
					if (smPersonnelTemp.getEpRole().equals(
							RoleCode.G2G_SUPPLIER_ADMIN.value())
							|| smPersonnelTemp.getEpRole().equals(
									RoleCode.BASIC_SUPPLIER_ADMIN.value())
							|| smPersonnelTemp.getEpRole().equals(
									RoleCode.MOF_SUPPLIER_ADMIN.value())) {
						adminPersonnel = smPersonnelTemp;
					} else if (smPersonnelTemp.getEpRole().equals(
							RoleCode.G2G_SUPPLIER_USER.value())
							|| smPersonnelTemp.getEpRole().equals(
									RoleCode.BASIC_SUPPLIER_USER.value())
							|| smPersonnelTemp.getEpRole().equals(
									RoleCode.MOF_SUPPLIER_USER.value())
							|| smPersonnelTemp.getEpRole().equals(
									RoleCode.FL_USER.value())) {
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

			SmCommonUtil.redirectToPage(redirectPage, "", null);
		}

	}

	/**
	 * reset search value
	 */
	public void resetSmPersonnelSearch() {
		this.getManagedBean().setSearchCompanyRole(null);
		this.getManagedBean().setSearchEPRole(null);
		this.getManagedBean().setSearchUsername(null);
	}

	/**
	 * construct valid roles
	 */
	private void buildRoleList() {
		boolean isGovtSeller = (StringUtils.isNotBlank(getManagedBean()
				.getSmSupplier().getBusinessType()) && getManagedBean()
				.getSmSupplier()
				.getBusinessType()
				.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_GOVT_SELLER)) ? true
				: false;
		List<SelectItem> roles = new ArrayList<SelectItem>();

		SelectItem role = new SelectItem();
		role.setValue(isGovtSeller ? RoleCode.G2G_SUPPLIER_USER.value()
				: RoleCode.BASIC_SUPPLIER_USER.value());
		role.setLabel(getEpRoleDescByRoleCode(role.getValue().toString()));
		if (!getManagedBean().isDisableRole()) {
			roles.add(role);
		}

		role = new SelectItem();
		role.setValue(RoleCode.CM_USER.value());
		role.setLabel(getEpRoleDescByRoleCode(role.getValue().toString()));
		roles.add(role);

		getManagedBean().setRoleList(roles);

		if (getManagedBean().getBasicSmPersonnel() != null
				&& getManagedBean().getBasicSmPersonnel().getEpRole() != null
				&& (getManagedBean().getBasicSmPersonnel().getEpRole()
						.equals(RoleCode.BASIC_SUPPLIER_ADMIN.value()) || getManagedBean()
						.getBasicSmPersonnel().getEpRole()
						.equals(RoleCode.G2G_SUPPLIER_ADMIN.value()))
				&& getManagedBean().isEditMode()) {
			SelectItem adminRole = new SelectItem();
			adminRole.setValue(isGovtSeller ? RoleCode.G2G_SUPPLIER_ADMIN
					.value() : RoleCode.BASIC_SUPPLIER_ADMIN.value());
			adminRole.setLabel(getEpRoleDescByRoleCode(adminRole.getValue()
					.toString()));
			getManagedBean().getRoleList().add(adminRole);
		}
	}

	/**
	 * Validate whether need to disable add personnel button in Supplier User
	 * Maintenance screen
	 * 
	 * @param SmSupplier
	 *            smSupplier
	 * @return boolean
	 */
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
				if (count >= SmRuleHelper.getInstance()
						.getMaxBasicAuthorizedUser()) {
					validPerCount = true;
				}
			}

		}
		return validPerCount;
	}

	/**
	 * Redirect to add new personnel screen in Supplier User Maintenance
	 * 
	 * @return String page
	 */
	public String navigateToAddNewUserPage() {
		getManagedBean().setEditMode(false);
		getManagedBean().setDeactivatePersonnel(false);
		getManagedBean().setIdTypes(getPmIdTypeDropdownValues());
		removeEPRole();
		buildRoleList();

		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryId(getManagedBean().getDefaultCountryId());

		CountryResDTO resDTO = SmServiceLookup.getCountrySO()
				.retrieveCountryById(reqDTO);

		// getManagedBean().setDisableRole(false);
		SmPersonnel smPersonnel = new SmPersonnel();
		getManagedBean().setAccountStatus(1);
		smPersonnel.setPmCountry(resDTO.getPmCountry());
		smPersonnel.setTitle(new PmSalutation());

		resDTO = SmServiceLookup.getCountrySO().retrieveCountryById(reqDTO);
		SmAddress mailAddress = new SmAddress();
		mailAddress.setPmCountry(resDTO.getPmCountry());
		mailAddress.setPmState(new PmState());
		mailAddress.setPmDistrict(new PmDistrict());
		mailAddress.setPmCity(new PmCity());

		SmPersonnelAddress mailPersonnelAddress = new SmPersonnelAddress();
		mailPersonnelAddress.setAddressType(SupplierManagementConst.MAIL_ADD);
		mailPersonnelAddress.setSmAddress(mailAddress);
		mailPersonnelAddress.setSmPersonnel(smPersonnel);

		resDTO = SmServiceLookup.getCountrySO().retrieveCountryById(reqDTO);
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

	/**
	 * Initialize personnel by login id
	 */
	public void initUserPersonnelInfo() {
		FacesScopes facesScopes = new FacesScopes();
		PmUser usr = facesScopes.getUserSessionBean();
		getPersonnelByPmUser(usr);
	}

	/**
	 * Initialize authorized personnel by supplier
	 * 
	 * @param SmSupplier
	 *            smSupplier
	 */
	private void initAuthorizedPersonnels(SmSupplier smSupplier) {
		getManagedBean().setSmPersonnelList(new ArrayList<SmPersonnel>());
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(smSupplier.getSupplierId());

		int countActiveAppl = SmServiceLookup.getPersonnelSO()
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

		resDTO = SmServiceLookup.getPersonnelSO()
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

	/**
	 * Redirect to update personnel screen in Supplier User Maintenance
	 * 
	 * @param String
	 *            personnelId
	 * @return String page
	 */
	public String navigateToUpdateUserPage(String personnelId) {
		getManagedBean().setEditMode(true);
		getManagedBean().setIdTypes(getPmIdTypeDropdownValues());
		getPersonnelInfoById(personnelId);
		buildRoleList();
		validateAccountStatus();
		setDisabledActivateDeactivateRadioButton();
		setPersonnelViewOnlyData(getManagedBean().getBasicSmPersonnel());
		setDeactivateEpRole(getManagedBean().getBasicSmPersonnel());
		getManagedBean().getBasicSmPersonnel().setPrevSoftcertFlg(
				getManagedBean().getBasicSmPersonnel().getSoftcertFlg());
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

	/**
	 * Redirect to first screen in Supplier User Maintenance
	 * 
	 * @return String page
	 */
	public String cancel() {
		return "userProfileSearch.xhtml";

	}

	public void confirmCreateBasicApplication() {

	}

	/**
	 * On change role selection if Catalogue User set contract signer and
	 * softcert flag to false
	 */
	public void onChangeRole() {
		if (StringUtils.isNotBlank(getManagedBean().getBasicSmPersonnel()
				.getEpRole())) {
			if (getManagedBean().getBasicSmPersonnel().getEpRole()
					.equals(RoleCode.CM_USER.value())
					|| getManagedBean()
							.getBasicSmPersonnel()
							.getSmAppl()
							.getSupplierType()
							.equals(SupplierManagementConst.SUPP_TYPE_CONSULTANT)) {
				if (getManagedBean().getBasicSmPersonnel().getPrevSoftcertFlg() != SupplierManagementConst.SOFT_CERT_ISSUED) {
					getManagedBean().getBasicSmPersonnel().setSoftcertFlg(
							DbValueConst.RECORD_STATUS_SUSPENDED);
				}
				getManagedBean().getBasicSmPersonnel().setContractSignerFlg(
						DbValueConst.RECORD_STATUS_SUSPENDED);
			}
		}
	}

	/**
	 * Initialize personnel addresses
	 */
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
			CountryReqDTO reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			CountryResDTO resCountryDTO = SmServiceLookup.getCountrySO()
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
			CountryReqDTO reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			CountryResDTO resCountryDTO = SmServiceLookup.getCountrySO()
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

	/**
	 * Initialize updated personnel addresses
	 */
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
				CountryReqDTO reqCountryDTO = new CountryReqDTO();
				reqCountryDTO.setCountryId(getManagedBean()
						.getDefaultCountryId());
				CountryResDTO resCountryDTO = SmServiceLookup.getCountrySO()
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
				CountryReqDTO reqCountryDTO = new CountryReqDTO();
				reqCountryDTO.setCountryId(getManagedBean()
						.getDefaultCountryId());
				CountryResDTO resCountryDTO = SmServiceLookup.getCountrySO()
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

	/**
	 * Get list of country
	 */
	public void getCountryList() {

		CountryResDTO resDTO = SmServiceLookup.getCountrySO()
				.retrieveCountryList();

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

	/**
	 * Update mail address for state,district and city list based on dropdown
	 * selection
	 * 
	 * @param String
	 *            countryId
	 * @param String
	 *            stateId
	 * @param String
	 *            districtId
	 */
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

	/**
	 * Update permanent address for state,district and city list based on
	 * dropdown selection
	 * 
	 * @param String
	 *            countryId
	 * @param String
	 *            stateId
	 * @param String
	 *            districtId
	 */
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

	/**
	 * Update mail address if tick same as permanent address
	 */
	public void setUpdateMailingAddrSameAsPerm() {
		if (getManagedBean().getUpdateSmPersonnelMailingAddress()
				.isSameAsPerm()) {
			SmAddress address = SmCommonUtil.cloneAddress(getManagedBean()
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
						.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
			}
		}

	}

	/**
	 * Get personnel by personnel id
	 * 
	 * @param String
	 *            personnelId
	 */
	public void getPersonnelInfoById(String personnelId) {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setPersonnelId(Long.valueOf(personnelId));
		PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO()
				.retrievePersonnelById(reqDTO);
		SmPersonnel smPersonnel = resDTO.getSmPersonnel();
		this.getManagedBean().setBasicSmPersonnel(smPersonnel);
		setPersonnelViewOnlyMode(getManagedBean().getBasicSmPersonnel());
		if (smPersonnel.getTitle() == null) {
			smPersonnel.setTitle(new PmSalutation());
		}

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
				CountryReqDTO reqCountryDTO = new CountryReqDTO();
				reqCountryDTO.setCountryId(getManagedBean()
						.getDefaultCountryId());
				CountryResDTO resCountryDTO = SmServiceLookup.getCountrySO()
						.retrieveCountryById(reqCountryDTO);
				s1.setPmCountry(resCountryDTO.getPmCountry());
				s1.setPmState(new PmState());
				s1.setPmDistrict(new PmDistrict());
				s1.setPmCity(new PmCity());

				/* Construct Permanent Address Object */
				reqCountryDTO = new CountryReqDTO();
				reqCountryDTO.setCountryId(getManagedBean()
						.getDefaultCountryId());
				resCountryDTO = SmServiceLookup.getCountrySO()
						.retrieveCountryById(reqCountryDTO);
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
				resCountryDTO = SmServiceLookup.getCountrySO()
						.retrieveCountryById(reqCountryDTO);
				smPersonnel.setPmCountry(resCountryDTO.getPmCountry());
			}
		}
		setPrevEpRole(smPersonnel);

	}

	/**
	 * Get personnel by PmUser
	 * 
	 * @param PmUser
	 *            usr
	 */
	public void getPersonnelByPmUser(PmUser usr) {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setUserId(Long.valueOf(usr.getUserId()));
		PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO()
				.retrievePersonnelByUserId(reqDTO);
		SmPersonnel smPersonnel = resDTO.getSmPersonnel();
		this.getManagedBean().setUpdateSmPersonnel(smPersonnel);
	}

	/**
	 * Fetch updated personnel addresses (mail and permanent)
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 */
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
			CountryReqDTO reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			CountryResDTO resCountryDTO = SmServiceLookup.getCountrySO()
					.retrieveCountryById(reqCountryDTO);
			s1.setPmCountry(resCountryDTO.getPmCountry());
			s1.setPmState(new PmState());
			s1.setPmDistrict(new PmDistrict());
			s1.setPmCity(new PmCity());

			/* Construct Permanent Address Object */
			reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			resCountryDTO = SmServiceLookup.getCountrySO().retrieveCountryById(
					reqCountryDTO);
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
			resCountryDTO = SmServiceLookup.getCountrySO().retrieveCountryById(
					reqCountryDTO);
			smPersonnel.setPmCountry(resCountryDTO.getPmCountry());

			updatePermAddLists(getManagedBean()
					.getUpdateSmPersonnelPermanentAddress().getSmAddress()
					.getPmCountry().getCountryId().toString(), "", "");
			updateMailAddLists(getManagedBean()
					.getUpdateSmPersonnelMailingAddress().getSmAddress()
					.getPmCountry().getCountryId().toString(), "", "");
		}
	}

	/**
	 * Save or update personnel in Supplier User Maintenance
	 * 
	 * @throws Exception
	 */
	public void save() throws Exception {
		if (getManagedBean().getBasicSmPersonnel() != null) {
			SmCommonUtil.checkEmailDomainWithExceptionMsg(getManagedBean()
					.getBasicSmPersonnel().getName(), getManagedBean()
					.getBasicSmPersonnel().getEmail());
		}
		if (getManagedBean().getBasicSmPersonnel().getPersonnelId() == null) {
			getManagedBean().setBasicSmPersonnel(
					saveNewUser(getManagedBean().getBasicSmPersonnel()));
			initAuthorizedPersonnels(getManagedBean().getSmSupplier());
			getManagedBean().setDisableAddNewUser(
					validatePersonLimit(getManagedBean().getSmSupplier()));
		} else {
			if (getManagedBean().getAccountStatus() == DbValueConst.RECORD_STATUS_ACTIVE) {
				updateUser(getManagedBean().getBasicSmPersonnel());
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
					updateUser(getManagedBean().getBasicSmPersonnel());
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

	/**
	 * Deactivate or terminate authorized personnel
	 */
	public void deactivateOrTerminate() {
		try {
			updateUser(getManagedBean().getBasicSmPersonnel());
			initAuthorizedPersonnels(getManagedBean().getSmSupplier());
			getManagedBean().setDisableAddNewUser(
					validatePersonLimit(getManagedBean().getSmSupplier()));
			getManagedBean().setDeactivateTerminateConfirmMsg("");
		} catch (Exception ex) {
			throw new EpException(ex.getMessage());
		}
	}

	/**
	 * Update existing personnel
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 * @throws Exception
	 */
	public void updateUser(SmPersonnel smPersonnel) throws Exception {
		if (smPersonnel.getTitle() != null
				&& smPersonnel.getTitle().getSalutationId() == null) {
			smPersonnel.setTitle(null);
		}
		PersonnelResDTO resDTO = updatePersonnel(smPersonnel);
		log.info("Result:" + resDTO.isSuccessful());
		if (resDTO.isSuccessful()) {
			SmCommonUtil.redirectToPage("userProfileSearch.xhtml",
					MessageConst.INFO_100002, null);
			getManagedBean().setBasicSmPersonnel(resDTO.getSmPersonnel());
		}
	}

	/**
	 * Save new personnel record
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 * @return
	 */
	public SmPersonnel saveNewUser(SmPersonnel smPersonnel) {

		validatePersonnelBeforeSave(smPersonnel);
		setAuthorizedFlag(smPersonnel);
		SmCommonUtil.setNewPersonnelPermAndMailAddress(smPersonnel,
				getManagedBean().getBasicSmPersonnelMailingAddress(),
				getManagedBean().getBasicSmPersonnelPermanentAddress());

		if (smPersonnel.getIdentificationType().getParameterCode().equals("I")) {
			myIdentityVerification(smPersonnel);
		}

		CountryReqDTO reqCountryDTO = new CountryReqDTO();
		reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
		CountryResDTO resCountryDTO = SmServiceLookup.getCountrySO()
				.retrieveCountryById(reqCountryDTO);
		PmCountry country = resCountryDTO.getPmCountry();
		smPersonnel.setPmCountry(country);

		PersonnelReqDTO reqDTO = new PersonnelReqDTO();

		FacesScopes fs = new FacesScopes();
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		reqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
		reqDTO.setSmPersonnel(smPersonnel);
		reqDTO.setHasActiveAppl(getManagedBean().isUpgradeMofDraftAppl());
		reqDTO.setApplId(getManagedBean().getBasicSmAppl().getApplId());
		reqDTO.setBasicSmAppl(getManagedBean().getBasicSmAppl());
		if (reqDTO.isHasActiveAppl()) {
			reqDTO.setDraftApplId(getManagedBean().getCurrSmAppl().getApplId());
		} else {
			reqDTO.setDraftApplId(null);
		}

		PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO()
				.savePersonnelRecord(reqDTO);

		if (resDTO.isSuccessful()) {
			SmCommonUtil.redirectToPage("userProfileSearch.xhtml",
					MessageConst.INFO_721014,
					new String[] {
							smPersonnel.getEmail(),
							SmRuleHelper.getInstance().getEmailActivationDays()
									+ "" });
			return resDTO.getSmPersonnel();
		} else
			return null;
	}

	/**
	 * Update existing personnel record in Supplier User Maintenance
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 * @return
	 * @throws Exception
	 */
	private PersonnelResDTO updatePersonnel(SmPersonnel smPersonnel)
			throws Exception {
		setAuthorizedFlag(smPersonnel);
		if (getManagedBean().getBasicSmPersonnelMailingAddress().isSameAsPerm()) {
			SmAddress address = SmCommonUtil.cloneAddress(getManagedBean()
					.getBasicSmPersonnelPermanentAddress().getSmAddress());
			this.getManagedBean().getBasicSmPersonnelMailingAddress()
					.setSmAddress(address);
		}
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
		return SmServiceLookup.getPersonnelSO()
				.updatePersonnelAccountStatusOrEpRole(reqDTO);
	}

	/**
	 * Validate personnel before save in Supplier User Maintenance
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 */
	private void validatePersonnelBeforeSave(SmPersonnel smPersonnel) {

		if (smPersonnel.getTitle() != null
				&& smPersonnel.getTitle().getSalutationId() == null) {
			smPersonnel.setTitle(null);
		}
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
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setIdentificationNo(smPersonnel.getIdentificationNo());
			reqDTO.setSmSupplierId(getManagedBean().getSmSupplier()
					.getSupplierId());

			PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO()
					.retrieveDraftPersonnelByIdentificationNoAndSupplierId(
							reqDTO);
			if (resDTO.getSmPersonnel() != null) {
				throw new EpException(MessageConst.ERROR_722589);
			}
		}
	}

	/**
	 * Verify personnel with my identity
	 * 
	 * @param SmPersonnel
	 *            per
	 */
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
		MyIdentityReqDTO reqDTO = new MyIdentityReqDTO();
		reqDTO.setName(per.getName());
		reqDTO.setNewIcNo(per.getIdentificationNo());
		reqDTO.setType("Online");
		reqDTO.setPostCode(postCode);

		MyIdentityResDTO resDTO = null;
		StringBuffer sb = new StringBuffer();
		sb.append("MessageConst.ERROR_MYIDENTITY_");

		try {
			resDTO = SmServiceLookup.getMyIdentitySO().myIdentityVerification(
					reqDTO);
			if (!resDTO.getStatusCode().equalsIgnoreCase(
					SupplierManagementConst.SM_MYIDENTITY_STATUS_CODE_VALID)) {
				sb.append(resDTO.getStatusCode());
				throw new EpException(sb.toString());
			}
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		}

	}

	/**
	 * Get portal company id
	 * 
	 * @return String portalCompanyId
	 */
	public String getCompanyId() {
		return EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.PORTAL_COMPANY_ID);
	}

	/**
	 * Check if email already exist in Pm
	 * 
	 * @param String
	 *            email
	 * @return boolean
	 */
	public boolean isExistEmailInPmUser(String email) {
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setEmail(email);

		UserResDTO resDTO = SmServiceLookup.getUserSO().retrieveUserByEmail(
				reqDTO);

		if (resDTO.getUserList().size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check if identification no already exist in Pm
	 * 
	 * @param String
	 *            idNo
	 * @return boolean
	 */
	public boolean isExistUserByIdentificationNo(String idNo) {
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setLoginId(idNo);
		UserResDTO resDTO = SmServiceLookup.getUserSO().retrieveUserByLoginId(
				reqDTO);

		if (resDTO.getPmUser() != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Update personnel and redirect to success page
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 * @return
	 */
	public String update(SmPersonnel smPersonnel) {
		setUpdateMailingAddrSameAsPerm();
		try {
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setSmPersonnel(smPersonnel);
			PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO()
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
		} finally {

		}
	}

	/**
	 * Set personnel authorization
	 * 
	 * @param AjaxBehaviorEvent
	 *            e
	 */
	public void assignAccountPrivilege(AjaxBehaviorEvent e) {
		if (getManagedBean().getBasicSmPersonnel().getAuthorizedFlg() == 0) {
			getManagedBean().getBasicSmPersonnel().setSoftcertFlg(0);
			getManagedBean().getBasicSmPersonnel().setContractSignerFlg(0);
		}

		if (getManagedBean().getBasicSmPersonnel().getSoftcertFlg() == 0) {
			getManagedBean().getBasicSmPersonnel().setContractSignerFlg(0);
		}
	}

	/**
	 * Remove role from current role list
	 */
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

	/**
	 * Set personnel identification type
	 * 
	 * @param AjaxBehaviorEvent
	 *            e
	 */
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

	/**
	 * Retrieve parameter by parameter id
	 * 
	 * @param Long
	 *            paramId
	 * @return PmParameter
	 */
	private PmParameter retrievePmParameterById(Long paramId) {
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterId(paramId);
		ParameterResDTO resDTO = SmServiceLookup.getParameterSO()
				.retrieveParameterById(reqDTO);

		return resDTO.getPmParameter();
	}

	/**
	 * Set personnel account status
	 */
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

	/**
	 * Set personnel verification type
	 * 
	 * @param AjaxBehaviorEvent
	 *            e
	 */
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

	/**
	 * Show deactivate admin confirmation dialog box
	 */
	public void confirmDeactivateAdmin() {
		if (getManagedBean().getSelectedPersonnelToReassign() == null) {
			throw new EpException(MessageConst.ERROR_721111);
		} else {
			RequestContext.getCurrentInstance().execute("confirmDialog.show()");
		}
	}

	/**
	 * Process deactivate old admin and replace with new admin
	 */
	public void deactivateAdmin() {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		FacesScopes fs = new FacesScopes();
		reqDTO.setLanguage(fs.getUserLocale().getLanguage());
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		reqDTO.setAdminPersonnel(getManagedBean().getUpdateSmPersonnel());
		reqDTO.setSmPersonnel(getManagedBean().getSelectedPersonnelToReassign());
		reqDTO.setSmSupplierId(SmApplicationUtil.getCurrentSupplier()
				.getSupplierId());
		reqDTO.setOldAdminRecordStatusFlg(getManagedBean()
				.getUpdateSmPersonnel().getEpRole()
				.equals(RoleCode.MOF_SUPPLIER_ADMIN.value()) ? DbValueConst.RECORD_STATUS_ACTIVE
				: DbValueConst.RECORD_STATUS_DELETED);

		PersonnelResDTO resDTO = new PersonnelResDTO();
		resDTO = SmServiceLookup.getPersonnelSO().reassignSupplierAdmin(reqDTO);

		if (resDTO.isSuccessful()) {
			SmCommonUtil
					.callDeactivationSuccessDialog(MessageConst.INFO_721027);
		} else {
			throw new EpException(MessageConst.ERROR_721112);
		}
	}

	public void onChangeAccntStatus(AjaxBehaviorEvent e) {

	}

	/**
	 * Redirect to liferay logout url
	 */
	public void proceedToLogout() {
		SmCommonUtil.redirectToExternalUrl(getManagedBean().getLogoutUrl());
	}

	/**
	 * Get current active application by supplier
	 * 
	 * @param SmSupplier
	 *            smSupplier
	 * @return
	 */
	private SmAppl getCurrActiveSmAppl(SmSupplier smSupplier) {
		if (getManagedBean().isUpgradeMofDraftAppl()) {
			MofRegistrationReqDTO mofRegistrationReqDTO = new MofRegistrationReqDTO();
			mofRegistrationReqDTO.setSupplierId(smSupplier.getSupplierId());
			return SmServiceLookup.getMofRegistrationSO()
					.retrieveActiveSmApplBySupp(mofRegistrationReqDTO)
					.getSmAppl();
		} else {
			return smSupplier.getSmAppl();
		}
	}

	/**
	 * Construct message for deactivate supplier admin
	 * 
	 * @param SmSupplier
	 *            smSupplier
	 */
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

	/**
	 * Set personnel authorized flag
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 */
	private void setAuthorizedFlag(SmPersonnel smPersonnel) {
		if (smPersonnel != null && smPersonnel.getEpRole() != null
				&& StringUtils.isNotBlank(smPersonnel.getEpRole())) {
			if (smPersonnel.getEpRole().equals(RoleCode.CM_USER.value())) {
				smPersonnel.setSoftcertFlg(0);
				smPersonnel.setAuthorizedFlg(Integer
						.parseInt(ParameterConst.FLAG_YES));
			} else if (smPersonnel.getEpRole().equals(
					RoleCode.BASIC_SUPPLIER_USER.value())) {
				if (smPersonnel.getSoftcertFlg() == 0) {
					smPersonnel
							.setSoftcertFlg(SupplierManagementConst.SOFT_CERT_PENDING_REQ);
				}
				smPersonnel.setAuthorizedFlg(Integer
						.parseInt(ParameterConst.FLAG_YES));
			}
		}
	}

	/**
	 * Set to disable or enable active or deactivate radio button
	 */
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

	/**
	 * Set whether the personnel can be edited or only for view
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 */
	private void setPersonnelViewOnlyMode(SmPersonnel smPersonnel) {
		getManagedBean().setViewOnlyPersonnel(false);
		if (getManagedBean().isEditMode() && smPersonnel.getPmUser() == null
				|| smPersonnel.getPmUser().getUserId() == null) {
			{
				getManagedBean().setViewOnlyPersonnel(true);
			}
		}
	}

	/**
	 * Set personnel view only mode identification type and role
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 */
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

	/**
	 * Set personnel view only mode for addresses
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 */
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

	/**
	 * Check whether the login user have active application that still running
	 * 
	 * @return boolean
	 */
	private boolean isHavePendingTaskList(SmSupplier supplier) {
		boolean retVal = SmApplicationUtil.checkActiveSmApplForSupp(supplier);
		return retVal;
	}

	/**
	 * Set whether the personnel is deactivated or not
	 * 
	 * @param SmPersonnel
	 *            personnel
	 */
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

	/**
	 * Set personnel previous role
	 * 
	 * @param SmPersonnel
	 *            personnel
	 */
	private void setPrevEpRole(SmPersonnel personnel) {
		if (personnel.getEpRole() == null) {
			personnel.setPrevEpRole(getDeactivateEpRole(personnel));
		} else {
			personnel.setPrevEpRole(personnel.getEpRole());
		}
	}

	/**
	 * Get ep role for deactivated personnel
	 * 
	 * @param SmPersonnel
	 *            personnel
	 * @return String
	 */
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
