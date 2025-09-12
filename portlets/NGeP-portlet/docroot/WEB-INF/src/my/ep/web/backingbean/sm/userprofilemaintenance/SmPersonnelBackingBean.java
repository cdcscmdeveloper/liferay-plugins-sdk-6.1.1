package my.ep.web.backingbean.sm.userprofilemaintenance;

import java.sql.Timestamp;
import java.util.ArrayList;
// 9569 start
import java.util.Collections;
import java.util.Comparator;
//9569 end

//start #12981 - no 2, comment #189
import java.util.Arrays;
//start #12981 - no 2, comment #189

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.bpm.vo.WorklistItemVO;
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
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmDigiCert;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSoftcertRequest;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.userprofilemaintenance.SmPersonnelManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SmAuthorizedPersonnelCountVO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
//#28895_35501 Start - AUDIT TRAIL SM
import my.ep.biz.service.IAuditTrailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.web.backingbean.sm.audit.AuditSupplierManagementAdminInformationBackingBean;
import my.ep.web.backingbean.sm.audit.AuditSupplierManagementBackingBeanTemplate;
import my.ep.web.backingbean.sm.audit.AuditSupplierManagementPersonnelBackingBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
//#28895_35501 End


@ManagedBean(name = "smPersonnelBackingBean")
@RequestScoped
public class SmPersonnelBackingBean extends
		BaseBackingBean<SmPersonnelManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8285824019929910081L;
	private static Logger log = Logger.getLogger(SmPersonnelBackingBean.class);
	private boolean showInfo = false;  //#30053

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
		getManagedBean().setMaxUserLimit(
				SmRuleHelper.getInstance().getMaxBasicAuthorizedUser());
		SmSupplier smSupplier = SmApplicationUtil.getCurrentSupplier();
		getManagedBean().setSmSupplier(smSupplier);
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode("MY");
		CountryResDTO resDTO = SmServiceLookup.getCountrySO()
				.retrieveCountryByCode(reqDTO);
		getManagedBean().setDefaultCountryId(
				resDTO.getPmCountry().getCountryId());

		initAuthorizedPersonnels(smSupplier);
		resetUserLimitMsg();
		getManagedBean().setDisableAddNewUser(validatePersonLimit(smSupplier));
		getManagedBean().setSalutationItems(SmCommonUtil.getSalutationItems());
		SmCommonUtil.redirectToPage("userProfileSearch.xhtml", "", null, null);
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
		// #9569 - start
		getManagedBean().setSupplierType(getManagedBean().getSmSupplier().getSmAppl().getSupplierType());
		// #9569 - end
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

		SmCommonUtil.redirectToPage("updateUserProfile.xhtml", "", null, null);
	}

	/**
	 * Pre - render and redirect to Deactivate Supplier Admin screen
	 */
	public void preRenderDeactivateSuppAdmin() {
		renewSesionObject();
		boolean isHasSupplierUser = false;
		SmSupplier smSupplier = SmApplicationUtil.getCurrentSupplier();
		// #28895_35501 - AUDIT TRAIL SM
		copyOldData(smSupplier);
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
		
		int countActiveAppl = SmServiceLookup.getPersonnelSO()
				.countSupplierWithActiveApplBySupplierId(reqDTO);
		
		if (countActiveAppl > 0) {
			getManagedBean().setHasActiveAppl(true);
		}

		redirectPage = "adminReAssign.xhtml";
		if (!EntityHelper.isCollectionEmpty(resDTO.getSmPersonnelList())) {
			List<SmPersonnel> authorizedPersonnelList = resDTO
					.getSmPersonnelList();
			for (SmPersonnel smPersonnelTemp : authorizedPersonnelList) {
				if (my.ep.constant.sm.SupplierManagementConst.SUPPLIER_ADMIN_ROLES
						.contains(smPersonnelTemp.getEpRole())) {
					adminPersonnel = smPersonnelTemp;
				} else if (my.ep.constant.sm.SupplierManagementConst.SUPPLIER_USER_ROLES
						.contains(smPersonnelTemp.getEpRole())) {
					isHasSupplierUser = true;
				}
			}
			authorizedPersonnelList.remove(adminPersonnel);
			getManagedBean().setSmPersonnelList(authorizedPersonnelList);
			getManagedBean().setUpdateSmPersonnel(adminPersonnel);
			SmCommonUtil.setRoleName(smSupplier.getSmAppl().getSupplierType(),
					getManagedBean().getSmPersonnelList());
			//start - #30053
			if(smSupplier.getSmAppl().getSupplierType().equals(SupplierManagementConst.STYPE_CONTRACTOR)){
				showInfo = true;
			}
			//end - #30053
			if (!isHasSupplierUser) {
				constructNoSupplierUserToDeactivateMsg(smSupplier);
			} else {
				getManagedBean().setTaskList(
						SmCommonUtil.getBpmTaskListByLoginId(adminPersonnel
								.getPmUser().getLoginId()));
			}
		} else {
			getManagedBean().setSmPersonnelList(new ArrayList<SmPersonnel>());
			constructNoSupplierUserToDeactivateMsg(smSupplier);
		}

		SmCommonUtil.redirectToPage(redirectPage, "", null, null);

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
		// #31710 - start
		if (!(getManagedBean().getSupplierType().isEmpty())
				&& !(getManagedBean().getSupplierType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_CONSULTANT))) {
			role = new SelectItem();
			role.setValue(RoleCode.CM_USER.value());
			role.setLabel(getEpRoleDescByRoleCode(role.getValue().toString()));
			roles.add(role);
		}
		// #31710 - end
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
				if (count >= getManagedBean().getMaxUserLimit()) {
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
		// #32515 - start
		getManagedBean().setSupplierType(getManagedBean().getSmSupplier().getSmAppl().getSupplierType());
		// #32515 - end
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
		PmParameter identificationType = ParameterHelper.getInstance()
				.getParameterByTypeAndCode(
						ParameterTypeConst.IDENTIFICATION_TYPES,
						ParameterConst.IDENTIFICATION_TYPES_IC);
		smPersonnel
				.setIdentificationTypeId(identificationType.getParameterId());
		smPersonnel.setIdentificationType(identificationType);

		resDTO = SmServiceLookup.getCountrySO().retrieveCountryById(reqDTO);
		SmAddress mailAddress = new SmAddress();
		mailAddress.setPmCountry(resDTO.getPmCountry());
		mailAddress.setPmState(new PmState());
		//#13320 - added division
		mailAddress.setPmDivision(new PmDivision());
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
		//#13320 - added division
		permAddress.setPmDivision(new PmDivision());
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
		//#13320 - added division - start
		updateMailAddLists(getManagedBean().getDefaultCountryId().toString(),
				"", "", "");
		updatePermAddLists(getManagedBean().getDefaultCountryId().toString(),
				"", "", "");
		//#13320 - added division - end
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
		getManagedBean().setUserAddConfirmationMsg(
				SmCommonUtil
						.getMessage("lbl.sm.userprofile.search.userscount",
								new String[] { getManagedBean()
										.getMaxUserLimit() + "" }));
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
		// #29576 - start
		reqDTO.setAppl(getManagedBean().getCurrSmAppl());
		// #29576 - end

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
			int balanceUser = getManagedBean().getMaxUserLimit()
					- getManagedBean().getSmPersonnelList().size();
			getManagedBean().setUserAddConfirmationMsg(
					SmCommonUtil.getMessage(
							"lbl.sm.userprofile.search.userscount",
							new String[] { balanceUser + "" }));
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
		// #32515 - start
		getManagedBean().setSupplierType(getManagedBean().getSmSupplier().getSmAppl().getSupplierType());
		getManagedBean().setEpRoleTrans(getManagedBean().getBasicSmPersonnel().getEpRole());
		// #32515 - end
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

			// if personnel is Catalogue User
			if (getManagedBean().getBasicSmPersonnel().getEpRole()
					.equals(RoleCode.CM_USER.value())) {

				// if latest softcert flag not yet issued, then set flag no
				if (getManagedBean().getBasicSmPersonnel().getPrevSoftcertFlg() != SupplierManagementConst.SOFT_CERT_ISSUED) {
					getManagedBean().getBasicSmPersonnel().setSoftcertFlg(
							Integer.parseInt(ParameterConst.FLAG_NO));
				} else {
					getManagedBean().getBasicSmPersonnel().setSoftcertFlg(
							getManagedBean().getBasicSmPersonnel()
									.getPrevSoftcertFlg());
				}

				// set contract signer flag to no
				getManagedBean().getBasicSmPersonnel().setContractSignerFlg(
						Integer.parseInt(ParameterConst.FLAG_NO));

				//#9569 - Start
				//Since for MOF appliation, CM_USER Must be an authorized personnel.
				// set authorized flag to no
				/*getManagedBean().getBasicSmPersonnel().setAuthorizedFlg(
						Integer.parseInt(ParameterConst.FLAG_NO));*/
				
				getManagedBean().getBasicSmPersonnel().setAuthorizedFlg(
						Integer.parseInt(ParameterConst.FLAG_YES));
				//#9569 - End 
			} else {
				// newly added personnel, set softcert flag to pending request
				if (getManagedBean().getBasicSmPersonnel().getPersonnelId() == null) {
					getManagedBean().getBasicSmPersonnel().setSoftcertFlg(
							SupplierManagementConst.SOFT_CERT_PENDING_REQ);

				} else {
					// if latest softcert flag is 0, set to pending request
					if (getManagedBean().getBasicSmPersonnel()
							.getPrevSoftcertFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
						getManagedBean().getBasicSmPersonnel().setSoftcertFlg(
								SupplierManagementConst.SOFT_CERT_PENDING_REQ);
					}
					// if latest softcert flag is not 0, set to latest softcert
					// flag
					else {
						getManagedBean().getBasicSmPersonnel().setSoftcertFlg(
								getManagedBean().getBasicSmPersonnel()
										.getPrevSoftcertFlg());
					}

				}
				// set authorized flag to yes
				getManagedBean().getBasicSmPersonnel().setAuthorizedFlg(
						Integer.parseInt(ParameterConst.FLAG_YES));
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
			//#13320 - added division
			if (address.getAddress1() != null) {
				updateMailAddLists(address.getPmCountry().getCountryId()
						.toString(), address.getPmState().getStateId()
						.toString(), address.getPmDivision(), address.getPmDistrict().getDistrictId()
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
			//#13320 - added division
			s1.setPmDivision(new PmDivision());
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
			//#13320 - added division
			updateMailAddLists(getManagedBean().getDefaultCountryId()
					.toString(), "", "", "");
		}

		if (getManagedBean().getBasicSmPersonnelPermanentAddress() != null
				&& getManagedBean().getBasicSmPersonnelPermanentAddress()
						.getSmAddress() != null) {
			SmAddress address = getManagedBean()
					.getBasicSmPersonnelPermanentAddress().getSmAddress();
			if (address.getAddress1() != null) {
				//#13320 - added division
				updatePermAddLists(address.getPmCountry().getCountryId()
						.toString(), address.getPmState().getStateId()
						.toString(), address.getPmDivision(), address.getPmDistrict().getDistrictId()
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
			//#13320 - added division
			s1.setPmDivision(new PmDivision());
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
			//#13320 - added division
			updatePermAddLists(getManagedBean().getDefaultCountryId()
					.toString(), "", "", "");
		}
		
		//9569 - Start
		//Residential
		if (getManagedBean().getBasicSmPersonnelResidentialAddress() != null
				&& getManagedBean().getBasicSmPersonnelResidentialAddress()
						.getSmAddress() != null) {
			SmAddress address = getManagedBean()
					.getBasicSmPersonnelResidentialAddress().getSmAddress();
			
			if (address.getAddress1() != null) {
				updateResAddLists(address.getPmCountry().getCountryId().toString(), address.getPmState().getStateId()
						.toString(), address.getPmDivision(), address.getPmDistrict().getDistrictId()
						.toString());
			}
		} else {
			SmAddress s1 = new SmAddress();

			/* Construct Residential Address Object */
			CountryReqDTO reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			CountryResDTO resCountryDTO = SmServiceLookup.getCountrySO()
					.retrieveCountryById(reqCountryDTO);
			s1.setPmCountry(resCountryDTO.getPmCountry());
			s1.setPmState(new PmState());
			s1.setPmDivision(new PmDivision());
			s1.setPmDistrict(new PmDistrict());
			s1.setPmCity(new PmCity());

			SmPersonnelAddress resPersonnelAddress = new SmPersonnelAddress();
			resPersonnelAddress
					.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES);
			resPersonnelAddress.setSmAddress(s1);
			resPersonnelAddress.setSmPersonnel(getManagedBean()
					.getBasicSmPersonnel());
			getManagedBean().setBasicSmPersonnelResidentialAddress(
					resPersonnelAddress);
			getManagedBean().getBasicSmPersonnel().getSmPersonnelAddresses()
					.add(getManagedBean().getBasicSmPersonnelResidentialAddress());
			
			updateResAddLists(getManagedBean().getDefaultCountryId().toString(), "", "", "");
		}
		
		//Office
		if (getManagedBean().getBasicSmPersonnelOfficeAddress() != null
				&& getManagedBean().getBasicSmPersonnelOfficeAddress()
						.getSmAddress() != null) {
			SmAddress address = getManagedBean()
					.getBasicSmPersonnelOfficeAddress().getSmAddress();
			
			if (address.getAddress1() != null) {
				updateOffAddLists(address.getPmCountry().getCountryId().toString(), address.getPmState().getStateId()
						.toString(), address.getPmDivision(), address.getPmDistrict().getDistrictId()
						.toString());
			}
		} else {
			SmAddress s1 = new SmAddress();

			/* Construct Office Address Object */
			CountryReqDTO reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			CountryResDTO resCountryDTO = SmServiceLookup.getCountrySO()
					.retrieveCountryById(reqCountryDTO);
			s1.setPmCountry(resCountryDTO.getPmCountry());
			s1.setPmState(new PmState());
			s1.setPmDivision(new PmDivision());
			s1.setPmDistrict(new PmDistrict());
			s1.setPmCity(new PmCity());

			SmPersonnelAddress offPersonnelAddress = new SmPersonnelAddress();
			offPersonnelAddress
					.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE);
			offPersonnelAddress.setSmAddress(s1);
			offPersonnelAddress.setSmPersonnel(getManagedBean()
					.getBasicSmPersonnel());
			getManagedBean().setBasicSmPersonnelOfficeAddress(
					offPersonnelAddress);
			getManagedBean().getBasicSmPersonnel().getSmPersonnelAddresses()
					.add(getManagedBean().getBasicSmPersonnelOfficeAddress());
			
			updateOffAddLists(getManagedBean().getDefaultCountryId().toString(), "", "", "");
		}
		//9569 - End
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
					//#13320 - added division
					updateMailAddLists(address.getPmCountry().getCountryId()
							.toString(), address.getPmState().getStateId()
							.toString(), address.getPmDivision(), address.getPmDistrict()
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
				//#13320 - added division
				s1.setPmDivision(new PmDivision());
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
				//#13320 - added division
				updateMailAddLists(getManagedBean().getDefaultCountryId()
						.toString(), "", "", "");
			}
			if (getManagedBean().getUpdateSmPersonnelPermanentAddress() != null) {
				address = getManagedBean()
						.getUpdateSmPersonnelPermanentAddress().getSmAddress();
				if (address.getAddress1() != null) {
					//#13320 - added division
					updatePermAddLists(address.getPmCountry().getCountryId()
							.toString(), address.getPmState().getStateId()
							.toString(), address.getPmDivision(), address.getPmDistrict()
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
				//#13320 - added division
				s1.setPmDivision(new PmDivision());
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
				//#13320 - added division
				updatePermAddLists(getManagedBean().getDefaultCountryId()
						.toString(), "", "", "");
			}
		}
	}
	//9569 - Start
	public void onTickSameAsPermForMail(SmAddress srcAddr,SmPersonnelAddress destAddr){
		 Long sourceCountry = srcAddr.getPmCountry().getCountryId();
		 Long sourceState =  srcAddr.getPmState().getStateId();
		 PmDivision sourceDivision = srcAddr.getPmDivision();
		 Long sourceDistrict = srcAddr.getPmDistrict().getDistrictId();
		 Long sourceCity = srcAddr.getPmCity().getCityId();
		 
		if(destAddr.isSameAsPerm() == true){
			SmAddress destiAddress = destAddr.getSmAddress();
			Long destiCountry = destAddr.getSmAddress().getPmCountry().getCountryId();
			Long destiState = destAddr.getSmAddress().getPmState().getStateId();
			PmDivision destiDivision = destAddr.getSmAddress().getPmDivision();
			Long destiDistrict = destAddr.getSmAddress().getPmDistrict().getDistrictId();
			Long destiCity = destAddr.getSmAddress().getPmCity().getCityId();
			
			if(destiAddress.getAddress1() != null){
				getManagedBean().getBasicSmPersonnelPrevAddress1().setSmAddress(destiAddress);
			}else{
				getManagedBean().getBasicSmPersonnelPrevAddress1().setSmAddress(new SmAddress());
			}
			
			if(destiCountry !=null){
				getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress().getPmCountry().setCountryId(destiCountry);
				getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress().getPmState().setStateId(destiState);
				
				if(destiDivision.getDivisionId() !=null){
					getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress().setPmDivision(destiDivision);
				}else{
					getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress().setPmDivision(new PmDivision());
				}
				
				getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress().getPmDistrict().setDistrictId(destiDistrict);
				getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress().getPmCity().setCityId(destiCity);
			}else{
				getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress().setPmCountry(new PmCountry());
				getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress().setPmState(new PmState());
				getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress().setPmDivision(new PmDivision());
				getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress().setPmDistrict(new PmDistrict());
				getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress().setPmCity(new PmCity());		
			}

			destAddr.setSmAddress(srcAddr);
			updateMailAddLists(sourceCountry.toString(), sourceState.toString(), sourceDivision, sourceDistrict.toString());
		}else{
			SmAddress prevAddr = getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress();
			Long prevCountry = getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress().getPmCountry().getCountryId();
			Long prevState = getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress().getPmState().getStateId();
			PmDivision prevDivision = getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress().getPmDivision();
			Long prevDistrict = getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress().getPmDistrict().getDistrictId();
			Long prevCity = getManagedBean().getBasicSmPersonnelPrevAddress1().getSmAddress().getPmCity().getCityId();
			
		if(prevAddr.getAddress1() == null && prevCountry == null){
				destAddr.setSmAddress(new SmAddress());
				destAddr.getSmAddress().setPmCountry(new PmCountry());
				destAddr.getSmAddress().setPmState(new PmState());
				destAddr.getSmAddress().setPmDivision(new PmDivision());
				destAddr.getSmAddress().setPmDistrict(new PmDistrict());
				destAddr.getSmAddress().setPmCity(new PmCity());			
			}else{	
					destAddr.setSmAddress(prevAddr);
					updateMailAddLists(prevCountry.toString(), prevState.toString(), prevDivision, prevDistrict.toString());
			}
		}
	}

	public void onTickSameAsPermForRes(SmAddress srcAddr,SmPersonnelAddress destAddr){
		 Long sourceCountry = srcAddr.getPmCountry().getCountryId();
		 Long sourceState =  srcAddr.getPmState().getStateId();
		 PmDivision sourceDivision = srcAddr.getPmDivision();
		 Long sourceDistrict = srcAddr.getPmDistrict().getDistrictId();
		 Long sourceCity = srcAddr.getPmCity().getCityId();
		 
		if(destAddr.isSameAsPerm() == true){
			SmAddress destiAddress = destAddr.getSmAddress();
			Long destiCountry = destAddr.getSmAddress().getPmCountry().getCountryId();
			Long destiState = destAddr.getSmAddress().getPmState().getStateId();
			PmDivision destiDivision = destAddr.getSmAddress().getPmDivision();
			Long destiDistrict = destAddr.getSmAddress().getPmDistrict().getDistrictId();
			Long destiCity = destAddr.getSmAddress().getPmCity().getCityId();
			
			if(destiAddress.getAddress1() != null){
				getManagedBean().getBasicSmPersonnelPrevAddress2().setSmAddress(destiAddress);
			}else{
				getManagedBean().getBasicSmPersonnelPrevAddress2().setSmAddress(new SmAddress());
			}
			
			if(destiCountry !=null){
				getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress().getPmCountry().setCountryId(destiCountry);
				getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress().getPmState().setStateId(destiState);
				
				if(destiDivision.getDivisionId() !=null){
					getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress().setPmDivision(destiDivision);
				}else{
					getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress().setPmDivision(new PmDivision());
				}
				
				getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress().getPmDistrict().setDistrictId(destiDistrict);
				getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress().getPmCity().setCityId(destiCity);
			}else{
				getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress().setPmCountry(new PmCountry());
				getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress().setPmState(new PmState());
				getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress().setPmDivision(new PmDivision());
				getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress().setPmDistrict(new PmDistrict());
				getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress().setPmCity(new PmCity());		
			}

			destAddr.setSmAddress(srcAddr);
			updateResAddLists(sourceCountry.toString(), sourceState.toString(), sourceDivision, sourceDistrict.toString());
		}else{
			SmAddress prevAddr = getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress();
			Long prevCountry = getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress().getPmCountry().getCountryId();
			Long prevState = getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress().getPmState().getStateId();
			PmDivision prevDivision = getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress().getPmDivision();
			Long prevDistrict = getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress().getPmDistrict().getDistrictId();
			Long prevCity = getManagedBean().getBasicSmPersonnelPrevAddress2().getSmAddress().getPmCity().getCityId();
			
		if(prevAddr.getAddress1() == null && prevCountry == null){
				destAddr.setSmAddress(new SmAddress());
				destAddr.getSmAddress().setPmCountry(new PmCountry());
				destAddr.getSmAddress().setPmState(new PmState());
				destAddr.getSmAddress().setPmDivision(new PmDivision());
				destAddr.getSmAddress().setPmDistrict(new PmDistrict());
				destAddr.getSmAddress().setPmCity(new PmCity());			
			}else{	
				
					destAddr.setSmAddress(prevAddr);
					updateResAddLists(prevCountry.toString(), prevState.toString(), prevDivision, prevDistrict.toString());
				
			}
		}
	}
	
	public void onTickOfficeSameAsCompany(){
		SmAddress compAddr = this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress();
		Long compCountry = this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmCountry().getCountryId();
		Long compState = this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmState().getStateId();
		PmDivision compDivision = this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmDivision();
		Long compDistrict = this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmDistrict().getDistrictId();
		Long compCity = this.getManagedBean().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmCity().getCityId();
		
		if(getManagedBean().getBasicSmPersonnelOfficeAddress().isSameAsComp() == true){
			SmAddress offAddr = getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress();
			Long offCountry = getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress().getPmCountry().getCountryId();
			Long offState = getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress().getPmState().getStateId();
			PmDivision offDivision = getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress().getPmDivision();
			Long offDistrict = getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress().getPmDistrict().getDistrictId();
			Long offCity = getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress().getPmCity().getCityId();
			
			if(offAddr.getAddress1() != null){
				getManagedBean().getBasicSmPersonnelPrevAddress().setSmAddress(offAddr);
			}else{
				getManagedBean().getBasicSmPersonnelPrevAddress().setSmAddress(new SmAddress());
			}
			
			if(offCountry !=null){
				getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress().getPmCountry().setCountryId(offCountry);
				getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress().getPmState().setStateId(offState);
				
				if(offDivision.getDivisionId() !=null){
					getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress().setPmDivision(offDivision);
				}else{
					getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress().setPmDivision(new PmDivision());
				}
				
				getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress().getPmDistrict().setDistrictId(offDistrict);
				getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress().getPmCity().setCityId(offCity);
			}else{
				getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress().setPmCountry(new PmCountry());
				getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress().setPmState(new PmState());
				getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress().setPmDivision(new PmDivision());
				getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress().setPmDistrict(new PmDistrict());
				getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress().setPmCity(new PmCity());		
			}
			
			getManagedBean().getBasicSmPersonnelOfficeAddress().setSmAddress(compAddr);
			updateOffAddLists(compCountry.toString(), compState.toString(), compDivision, compDistrict.toString());
			
		}else{
			SmAddress prevAddr = getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress();
			Long prevCountry = getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress().getPmCountry().getCountryId();
			Long prevState = getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress().getPmState().getStateId();
			PmDivision prevDivision = getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress().getPmDivision();
			Long prevDistrict = getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress().getPmDistrict().getDistrictId();
			Long prevCity = getManagedBean().getBasicSmPersonnelPrevAddress().getSmAddress().getPmCity().getCityId();
			
			/*getManagedBean().getBasicSmPersonnelOfficeAddress().setSmAddress(prevAddr);
			updateOffAddLists(prevCountry.toString(), prevState.toString(), prevDivision, prevDistrict.toString());*/
			log.info("This is the previous Address 1 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + prevAddr.getAddress1());
			log.info("This is the previous Address Country ID >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + prevCountry);
			
		if(prevAddr.getAddress1() == null && prevCountry == null){
			   // this.getManagedBean().setBasicSmPersonnelOfficeAddress(new SmPersonnelAddress());
				getManagedBean().getBasicSmPersonnelOfficeAddress().setSmAddress(new SmAddress());
				log.info("This is the previous Address Country ID >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + prevCountry);
				getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress().setPmCountry(new PmCountry());
				getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress().setPmState(new PmState());
				getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress().setPmDivision(new PmDivision());
				getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress().setPmDistrict(new PmDistrict());
				getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress().setPmCity(new PmCity());		
				
			}else{
				getManagedBean().getBasicSmPersonnelOfficeAddress().setSmAddress(prevAddr);
				updateOffAddLists(prevCountry.toString(), prevState.toString(), prevDivision, prevDistrict.toString());
			}
		}
	}
	//9569 - End
	public void onTickSameAsAddress(SmAddress srcAddr,
			SmPersonnelAddress destAddr) {
		
		if(!destAddr.isSameAsPerm()) return;
		if (srcAddr == null || StringUtils.isBlank(srcAddr.getAddress1())
				|| srcAddr.getPmState() == null
				|| srcAddr.getPmState().getStateId() == null
				|| srcAddr.getPmDistrict() == null
				|| srcAddr.getPmDistrict().getDistrictId() == null
				|| srcAddr.getPmCity() == null
				|| srcAddr.getPmCity().getCityId() == null
				|| StringUtils.isBlank(srcAddr.getPostcode())) {
			String addrType = SmCommonUtil.getMessage(
					"lbl.sm.permenantaddress", null);
			destAddr.setSameAsPerm(false);
			throw new EpException(MessageConst.ERROR_722656,
					new String[] { addrType });
		} else {
			//#13320 - added division - start
			if(srcAddr.getPmDivision() != null){
				srcAddr.setDivisionId(srcAddr.getPmDivision().getDivisionId());
				getManagedBean().setMailDivisionId(srcAddr.getDivisionId());	
			}
			
			destAddr.setSmAddress(SmApplicationUtil.cloneAsPermenantAddr(
					srcAddr,
					destAddr.getSmAddress(),
					destAddr,
					SmCommonUtil.getInstance().getCountryById(
							getManagedBean().getMyCountryId())));
			
			updateMailAddLists(destAddr.getSmAddress().getPmCountry()
					.getCountryId().toString(), destAddr.getSmAddress()
					.getPmState().getStateId().toString(), destAddr
					.getSmAddress().getPmDivision(), destAddr
					.getSmAddress().getPmDistrict().getDistrictId().toString());
			//#13320 - added division - end
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
		//9569 -Start
		List<SelectItem> countryList3 = new ArrayList<SelectItem>();
		List<SelectItem> countryList4 = new ArrayList<SelectItem>();
		//9569 - End

		for (PmCountry country : resDTO.getCountryList()) {
			SelectItem si = new SelectItem();
			si.setLabel(country.getCountryName());
			si.setValue(country.getCountryId());
			countryList1.add(si);

			si = new SelectItem();
			si.setLabel(country.getCountryName());
			si.setValue(country.getCountryId());
			countryList2.add(si);
			
			//9569 - Start
			si = new SelectItem();
			si.setLabel(country.getCountryName());
			si.setValue(country.getCountryId());
			countryList3.add(si);

			si = new SelectItem();
			si.setLabel(country.getCountryName());
			si.setValue(country.getCountryId());
			countryList4.add(si);
			//9569 - End
		}

		getManagedBean().setCountryList(countryList1);

		getManagedBean().setMailAddCountryList(countryList1);

		getManagedBean().setPermAddCountryList(countryList2);
		
		//9569 - Start
		getManagedBean().setResAddCountryList(countryList3);
		
		getManagedBean().setOffAddCountryList(countryList4);
		//9569 - End
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
	public void updateMailAddLists(String countryId, String stateId, String divisionId,
			String districtId) {
		//9569 - Start
		if (this.getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress().getPmDivision() == null){
			this.getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress().setPmDivision(new PmDivision());
		}
		if (!countryId.isEmpty() || countryId.equalsIgnoreCase("null")) {
			getManagedBean().setMailAddStateList(
					SmCommonUtil.getStateList(Long.valueOf(countryId)));
			getManagedBean().setMailAddDistrictList(null);
			getManagedBean().setMailAddDivisionList(null);
			//9569 - End
			getManagedBean().setMailAddCityList(null);
			//#33592
			getManagedBean().setMailDivisionBox(false);
		}
		
		//#13320 - added division - start
		if(!stateId.isEmpty() || stateId.equalsIgnoreCase("null")) {
			PmState state = SmCommonUtil.getInstance().getStateById(Long.valueOf(stateId));
			getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress().
				setPmState(state);
			getManagedBean().setMailAddCityList(null);
			getManagedBean().setMailAddDivisionList(null);
			//#33592 start
			getManagedBean().setMailDivisionBox(false);
			if (state != null) {
				if (state.getStateCode().equals(SupplierManagementConst.SARAWAK_CODE)) {
					List<SelectItem> divisionLs = SmCommonUtil.getDivisionList(state.getStateId());
					this.getManagedBean().setMailAddDivisionList(divisionLs);
					this.getManagedBean().setMailAddDistrictList(SmCommonUtil.getDistrictListByID(getManagedBean().getDivisionId()));
					getManagedBean().setMailDivisionBox(true);
				} else {
					this.getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress().setPmDistrict(new PmDistrict());
					getManagedBean().setMailAddDistrictList(SmCommonUtil.getDistrictList(Long.valueOf(stateId)));
				}//#33592- end
			}
		}
				
		if((!divisionId.isEmpty() || divisionId.equalsIgnoreCase("null"))
			&& (getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress().getPmState()
					.getStateCode().equals(SupplierManagementConst.SARAWAK_CODE))){

			getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress().
				setPmDivision(SmCommonUtil.getInstance().getDivisionByUsingId(Long.valueOf(divisionId)));
			getManagedBean().setMailAddDistrictList(
				SmCommonUtil.getDistrictListByID(Long.valueOf(divisionId)));
			getManagedBean().getBasicSmPersonnelMailingAddress()
				.getSmAddress().setDivisionId(Long.valueOf(divisionId));
			getManagedBean().setMailAddCityList(null);
		}
		//#13320 - added division - end
		
		if (!districtId.isEmpty() || districtId.equalsIgnoreCase("null")) {
			getManagedBean()
					.getBasicSmPersonnelMailingAddress()
					.getSmAddress()
					.setPmDistrict(
							SmCommonUtil.getInstance().getDistrictById(
									Long.valueOf(districtId)));
			getManagedBean().setMailAddCityList(
					SmCommonUtil.getCityList(Long.valueOf(districtId)));
		}
	}

	//9569 - Start
	/**
	 * Update Residential address for state,district and city list based on drop
	 * down selection
	 * 
	 * @param String
	 *            countryId
	 * @param String
	 *            stateId
	 * @param String
	 *            districtId
	 */
	public void updateResAddLists(String countryId, String stateId, String divisionId,
			String districtId) {
		//#9569 - Start
		if (this.getManagedBean().getBasicSmPersonnelResidentialAddress().getSmAddress().getPmDivision() == null){
			this.getManagedBean().getBasicSmPersonnelResidentialAddress().getSmAddress().setPmDivision(new PmDivision());
		}

		if (!countryId.isEmpty() || countryId.equalsIgnoreCase("null")) {
			getManagedBean().setResAddStateList(SmCommonUtil.getStateList(Long.valueOf(countryId)));
			getManagedBean().setResAddDivisionList(null);
			//9569 - End
			getManagedBean().setResAddDistrictList(null);
			getManagedBean().setResAddCityList(null);
		}
		
		if(!stateId.isEmpty() || stateId.equalsIgnoreCase("null")) {
			PmState state = SmCommonUtil.getInstance().getStateById(Long.valueOf(stateId));
			getManagedBean().getBasicSmPersonnelResidentialAddress().getSmAddress().
				setPmState(state);
			getManagedBean().setResAddDivisionList(null);
			getManagedBean().setResAddCityList(null);
			//getManagedBean().setResDivisionBox(!state.getAdoptDivision());
			
			if(state.getStateCode().equals(SupplierManagementConst.SARAWAK_CODE)){				
				List<SelectItem> divisionLs = SmCommonUtil.getDivisionList(state.getStateId());
				this.getManagedBean().setResAddDivisionList(divisionLs);
				getManagedBean().setResAddDistrictList(null);
			
			} else {
				getManagedBean().setResAddDistrictList(
						SmCommonUtil.getDistrictList(Long.valueOf(stateId)));
			}
		}
				
		if((!divisionId.isEmpty() || divisionId.equalsIgnoreCase("null"))
			&& (getManagedBean().getBasicSmPersonnelResidentialAddress().getSmAddress().getPmState()
					.getStateCode().equals(SupplierManagementConst.SARAWAK_CODE))){

			getManagedBean().getBasicSmPersonnelResidentialAddress().getSmAddress().
				setPmDivision(SmCommonUtil.getInstance().getDivisionByUsingId(Long.valueOf(divisionId)));
			getManagedBean().setResAddDistrictList(
				SmCommonUtil.getDistrictListByID(Long.valueOf(divisionId)));
			getManagedBean().getBasicSmPersonnelResidentialAddress()
				.getSmAddress().setDivisionId(Long.valueOf(divisionId));
			getManagedBean().setResAddCityList(null);
		}
		//#13320 - added division - end
		
		if (!districtId.isEmpty() || districtId.equalsIgnoreCase("null")) {
			getManagedBean()
					.getBasicSmPersonnelResidentialAddress()
					.getSmAddress()
					.setPmDistrict(
							SmCommonUtil.getInstance().getDistrictById(
									Long.valueOf(districtId)));
			getManagedBean().setResAddCityList(
					SmCommonUtil.getCityList(Long.valueOf(districtId)));
		}
	}
	
	/**
	 * Update Office address for state,district and city list based on drop down
	 * selection
	 * 
	 * @param String
	 *            countryId
	 * @param String
	 *            stateId
	 * @param String
	 *            districtId
	 */
	public void updateOffAddLists(String countryId, String stateId, String divisionId,
			String districtId) {
		//9569 - Start
		if (this.getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress().getPmDivision() == null){
			this.getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress().setPmDivision(new PmDivision());
		}
		
		if (!countryId.isEmpty() || countryId.equalsIgnoreCase("null")) {
			getManagedBean().setOffAddStateList(
					SmCommonUtil.getStateList(Long.valueOf(countryId)));
			getManagedBean().setOffAddDivisionList(null);
			//9569 - End
			getManagedBean().setOffAddDistrictList(null);
			getManagedBean().setOffAddCityList(null);
		}
		
		//#13320 - added division - start
		if(!stateId.isEmpty() || stateId.equalsIgnoreCase("null")) {
			PmState state = SmCommonUtil.getInstance().getStateById(Long.valueOf(stateId));
			getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress().
				setPmState(state);
			getManagedBean().setOffAddDivisionList(null);
			getManagedBean().setOffAddCityList(null);
			//getManagedBean().setOffDivisionBox(!state.getAdoptDivision());
			
			if(state.getStateCode().equals(SupplierManagementConst.SARAWAK_CODE)){				
				List<SelectItem> divisionLs = SmCommonUtil.getDivisionList(state.getStateId());
				this.getManagedBean().setOffAddDivisionList(divisionLs);
				getManagedBean().setOffAddDistrictList(null);
			
			} else {
				getManagedBean().setOffAddDistrictList(
						SmCommonUtil.getDistrictList(Long.valueOf(stateId)));
			}
		}
				
		if((!divisionId.isEmpty() || divisionId.equalsIgnoreCase("null"))
			&& (getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress().getPmState()
					.getStateCode().equals(SupplierManagementConst.SARAWAK_CODE))){

			getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress().
				setPmDivision(SmCommonUtil.getInstance().getDivisionByUsingId(Long.valueOf(divisionId)));
			getManagedBean().setOffAddDistrictList(
				SmCommonUtil.getDistrictListByID(Long.valueOf(divisionId)));
			getManagedBean().getBasicSmPersonnelOfficeAddress()
				.getSmAddress().setDivisionId(Long.valueOf(divisionId));
			getManagedBean().setOffAddCityList(null);
		}
		//#13320 - added division - end
		
		if (!districtId.isEmpty() || districtId.equalsIgnoreCase("null")) {
			getManagedBean()
					.getBasicSmPersonnelOfficeAddress()
					.getSmAddress()
					.setPmDistrict(
							SmCommonUtil.getInstance().getDistrictById(
									Long.valueOf(districtId)));
			getManagedBean().setOffAddCityList(
					SmCommonUtil.getCityList(Long.valueOf(districtId)));
		}
	}
	//9569 - End
	
	/**
	 * Update permanent address for state,district and city list based on drop
	 * down selection
	 * 
	 * @param String
	 *            countryId
	 * @param String
	 *            stateId
	 * @param String
	 *            districtId
	 */
	public void updatePermAddLists(String countryId, String stateId, String divisionId,
			String districtId) {
		
		//9569 - Start
		if (this.getManagedBean().getBasicSmPersonnelPermanentAddress().getSmAddress().getPmDivision() == null){
			this.getManagedBean().getBasicSmPersonnelPermanentAddress().getSmAddress().setPmDivision(new PmDivision());
		}
		
		if (!countryId.isEmpty() || countryId.equalsIgnoreCase("null")) {
			getManagedBean().setPermAddStateList(
					SmCommonUtil.getStateList(Long.valueOf(countryId)));	
			getManagedBean().setPermAddDivisionList(null);
			//9569 - End
			getManagedBean().setPermAddDistrictList(null);
			getManagedBean().setPermAddCityList(null);
			//#33592
			getManagedBean().setDivisionBox(false);
		}
		//#13320 - added division
		if(!stateId.isEmpty() || stateId.equalsIgnoreCase("null")) {
			PmState state = SmCommonUtil.getInstance().getStateById(Long.valueOf(stateId));
			getManagedBean().getBasicSmPersonnelPermanentAddress().getSmAddress().
				setPmState(state);
			getManagedBean().setPermAddDivisionList(null);
			getManagedBean().setPermAddCityList(null);
			//#33592 -start
			getManagedBean().setDivisionBox(false);
			if (state != null) {
				if (state.getStateCode().equals(SupplierManagementConst.SARAWAK_CODE)) {
					List<SelectItem> divisionLs = SmCommonUtil.getDivisionList(state.getStateId());
					this.getManagedBean().setPermAddDivisionList(divisionLs);
					this.getManagedBean().setPermAddDistrictList(
							SmCommonUtil.getDistrictListByID(this.getManagedBean().getDivisionId())); 
					getManagedBean().setDivisionBox(true);
				}else {
					this.getManagedBean().getBasicSmPersonnelPermanentAddress().getSmAddress().setPmDistrict(new PmDistrict());
					getManagedBean().setPermAddDistrictList(SmCommonUtil.getDistrictList(Long.valueOf(stateId)));
				}//#33592-end
			}
		}
		
		if(!divisionId.isEmpty() || divisionId.equalsIgnoreCase("null")){
			getManagedBean().getBasicSmPersonnelPermanentAddress().getSmAddress().
				setPmDivision(SmCommonUtil.getInstance().getDivisionByUsingId(Long.valueOf(divisionId)));
			getManagedBean().setPermAddDistrictList(
				SmCommonUtil.getDistrictListByID(Long.valueOf(divisionId)));
			getManagedBean().getBasicSmPersonnelPermanentAddress()
				.getSmAddress().setDivisionId(Long.valueOf(divisionId));
			getManagedBean().setPermAddCityList(null);
		}

		//#13320 - added division - end
		if (!districtId.isEmpty() || districtId.equalsIgnoreCase("null")) {
			getManagedBean()
					.getBasicSmPersonnelPermanentAddress()
					.getSmAddress()
					.setPmDistrict(
							SmCommonUtil.getInstance().getDistrictById(
									Long.valueOf(districtId)));
			getManagedBean().setPermAddCityList(
					SmCommonUtil.getCityList(Long.valueOf(districtId)));
		}
		//#13320 - added division
		if(stateId.isEmpty() ^ districtId.isEmpty())
			getManagedBean().getBasicSmPersonnelPermanentAddress().getSmAddress().getPmCity().setCityId(null);
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
			//#13320 - added division
			updateMailAddLists(this.getManagedBean()
					.getUpdateSmPersonnelMailingAddress().getSmAddress()
					.getPmCountry().getCountryId().toString(), this
					.getManagedBean().getUpdateSmPersonnelMailingAddress()
					.getSmAddress().getPmState().getStateId().toString(), this
					.getManagedBean().getUpdateSmPersonnelMailingAddress()
					.getSmAddress().getPmDivision(), this
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
		//start searching for user role history for supplier maintenance
		getManagedBean().setPersonnelHistoryList(null);
		Long userId = null;
		if(smPersonnel.getPmUser() != null){
			userId = smPersonnel.getPmUser().getUserId();
			PersonnelReqDTO reqDTO1 = new PersonnelReqDTO();
			reqDTO1.setPmUserId(userId);
			PersonnelResDTO resDTO1 = SmServiceLookup.getPersonnelSO().retrieveUserRolesHistoryByUserOrgID(reqDTO1, smPersonnel);
			getManagedBean().setPersonnelHistoryList(resDTO1.getPersonnelHistory());
		}
		//End searching for user role history	
		
		this.getManagedBean().setBasicSmPersonnel(smPersonnel);
		setPersonnelViewOnlyMode(getManagedBean().getBasicSmPersonnel());
		//9569 - Start
		SmPersonnelAddress mail = new SmPersonnelAddress();
		mail.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL);
		mail.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
		mail.getSmAddress().setPmCountry(new PmCountry());
		mail.setSameAsPerm(false);
		mail.setSmPersonnel(smPersonnel);
		getManagedBean().setBasicSmPersonnelMailingAddress(mail);
		
		SmPersonnelAddress off = new SmPersonnelAddress();
		off.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE);
		off.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
		off.getSmAddress().setPmCountry(new PmCountry());
		off.setSameAsPerm(false);
		off.setSmPersonnel(smPersonnel);
		getManagedBean().setBasicSmPersonnelOfficeAddress(off);
		
		SmPersonnelAddress res = new SmPersonnelAddress();
		res.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES);
		res.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
		res.getSmAddress().setPmCountry(new PmCountry());
		res.setSameAsPerm(false);
		res.setSmPersonnel(smPersonnel);
		getManagedBean().setBasicSmPersonnelResidentialAddress(res);
		//9569 - End
		if (smPersonnel.getTitle() == null) {
			smPersonnel.setTitle(new PmSalutation());
		}

		if (getManagedBean().isViewOnlyPersonnel()) {
			setPersonnelViewOnlyAddresses(smPersonnel);
		} else {
			//9569 - Start
			//List<SmPersonnelAddress> smPAList = new ArrayList<SmPersonnelAddress>();
				//smPersonnel.getSmPersonnelAddresses().size();
				//smPAList.addAll(smPersonnel.getSmPersonnelAddresses());
				
				if (smPersonnel.getSmPersonnelAddresses() != null
					      && smPersonnel.getSmPersonnelAddresses().size() > 0){
			
				for (SmPersonnelAddress smPA : smPersonnel.getSmPersonnelAddresses()) {
					SmAddress address = new SmAddress();
					address.setPmCountry(new PmCountry());
					address.setPmState(new PmState());
					address.setPmDistrict(new PmDistrict());
					address.setPmCity(new PmCity());
					//13320 - division
					address.setPmDivision(new PmDivision());
					if (smPA.getAddressType().equalsIgnoreCase(
							SupplierManagementConst.MAIL_ADD)) {
						//9569 - Start
						if(smPA.getSmAddress() != null && smPA.getSmAddress().getAddressId() != null){
						address.setAddress1(smPA.getSmAddress().getAddress1());
						address.setAddress2(smPA.getSmAddress().getAddress2());
						address.setAddress3(smPA.getSmAddress().getAddress3());
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
						//#13320 - added division - start
						if(smPA.getSmAddress().getPmDivision() != null){
							Long divId = smPA.getSmAddress().getPmDivision().getDivisionId();
							address.getPmDivision().setDivisionId(divId);
							address.setDivisionId(divId);
							getManagedBean().setMailDivisionId(divId);
						}
						//#13320 - added division - end
						address.setPostcode(smPA.getSmAddress().getPostcode());
						smPA.setSmAddress(address);
						this.getManagedBean()
								.setBasicSmPersonnelMailingAddress(smPA);
						}
					} else if (smPA.getAddressType().equalsIgnoreCase(
							SupplierManagementConst.PERM_ADD)) {
						if(smPA.getSmAddress() != null && smPA.getSmAddress().getAddressId() != null){
						address.setAddress1(smPA.getSmAddress().getAddress1());
						address.setAddress2(smPA.getSmAddress().getAddress2());
						address.setAddress3(smPA.getSmAddress().getAddress3());
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
						//#13320 - added division - start
						if(smPA.getSmAddress().getPmDivision() != null){
							Long divId = smPA.getSmAddress().getPmDivision().getDivisionId();
							address.getPmDivision().setDivisionId(divId);
							address.setDivisionId(divId);
							getManagedBean().setDivisionId(divId);
						}
						//#13320 - added division - end
						address.setPostcode(smPA.getSmAddress().getPostcode());
						smPA.setSmAddress(address);
						this.getManagedBean()
								.setBasicSmPersonnelPermanentAddress(smPA);
						}
					}
					//#9569 - Start
					else if (smPA.getAddressType().equalsIgnoreCase(
							SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
						if(smPA.getSmAddress() != null && smPA.getSmAddress().getAddressId() != null){
						address.setAddress1(smPA.getSmAddress().getAddress1());
						address.setAddress2(smPA.getSmAddress().getAddress2());
						address.setAddress3(smPA.getSmAddress().getAddress3());
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
						if(smPA.getSmAddress().getPmDivision() != null){
							Long divId = smPA.getSmAddress().getPmDivision().getDivisionId();
							address.getPmDivision().setDivisionId(divId);
							address.setDivisionId(divId);
							getManagedBean().setResDivisionId(divId);
						}
						address.setPostcode(smPA.getSmAddress().getPostcode());
						smPA.setSmAddress(address);
						this.getManagedBean()
								.setBasicSmPersonnelResidentialAddress(smPA);
						}
					}else if (smPA.getAddressType().equalsIgnoreCase(
							SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
						if(smPA.getSmAddress() != null && smPA.getSmAddress().getAddressId() != null){
						address.setAddress1(smPA.getSmAddress().getAddress1());
						address.setAddress2(smPA.getSmAddress().getAddress2());
						address.setAddress3(smPA.getSmAddress().getAddress3());
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
						if(smPA.getSmAddress().getPmDivision() != null){
							Long divId = smPA.getSmAddress().getPmDivision().getDivisionId();
							address.getPmDivision().setDivisionId(divId);
							address.setDivisionId(divId);
							getManagedBean().setOffDivisionId(divId);
						}
						address.setPostcode(smPA.getSmAddress().getPostcode());
						smPA.setSmAddress(address);
						this.getManagedBean()
								.setBasicSmPersonnelOfficeAddress(smPA);
						}
					}
					//#9569 - End
				}
				initializeLists();
			} else {

				SmAddress s1 = new SmAddress();
				SmAddress s2 = new SmAddress();
				//9569 - Start
				SmAddress s3 = new SmAddress(); //residential Address
				SmAddress s4 = new SmAddress(); // Office Address
				//9569 - End

				/* Construct Mailing Address Object */
				CountryReqDTO reqCountryDTO = new CountryReqDTO();
				reqCountryDTO.setCountryId(getManagedBean()
						.getDefaultCountryId());
				CountryResDTO resCountryDTO = SmServiceLookup.getCountrySO()
						.retrieveCountryById(reqCountryDTO);
				s1.setPmCountry(resCountryDTO.getPmCountry());
				s1.setPmState(new PmState());
				//#13320 - added division
				s1.setPmDivision(new PmDivision());
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
				//#13320 - added divisions
				s2.setPmDivision(new PmDivision());
				s2.setPmDistrict(new PmDistrict());
				s2.setPmCity(new PmCity());
				
				//9569 - Start
				/* Construct Residential Address Object */
				reqCountryDTO = new CountryReqDTO();
				reqCountryDTO.setCountryId(getManagedBean()
						.getDefaultCountryId());
				resCountryDTO = SmServiceLookup.getCountrySO()
						.retrieveCountryById(reqCountryDTO);
				s3.setPmCountry(resCountryDTO.getPmCountry());
				s3.setPmState(new PmState());
				s3.setPmDivision(new PmDivision());
				s3.setPmDistrict(new PmDistrict());
				s3.setPmCity(new PmCity());
				
				/* Construct Office Address Object */
				reqCountryDTO = new CountryReqDTO();
				reqCountryDTO.setCountryId(getManagedBean()
						.getDefaultCountryId());
				resCountryDTO = SmServiceLookup.getCountrySO()
						.retrieveCountryById(reqCountryDTO);
				s4.setPmCountry(resCountryDTO.getPmCountry());
				s4.setPmState(new PmState());
				s4.setPmDivision(new PmDivision());
				s4.setPmDistrict(new PmDistrict());
				s4.setPmCity(new PmCity());
				//9569 - End
				
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
				//9569 - Start
				//res address
				SmPersonnelAddress resPersonnelAddress = new SmPersonnelAddress();
				resPersonnelAddress
						.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES);
				resPersonnelAddress.setSmAddress(s3);
				resPersonnelAddress.setSmPersonnel(smPersonnel);
				getManagedBean().setBasicSmPersonnelResidentialAddress(
						resPersonnelAddress);
				//res address end
				//office address
				SmPersonnelAddress offPersonnelAddress = new SmPersonnelAddress();
				offPersonnelAddress
						.setAddressType(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE);
				offPersonnelAddress.setSmAddress(s4);
				offPersonnelAddress.setSmPersonnel(smPersonnel);
				getManagedBean().setBasicSmPersonnelOfficeAddress(
						offPersonnelAddress);
				//office address end
				//9569 - End
				smPersonnelAddresses.add(getManagedBean()
						.getBasicSmPersonnelPermanentAddress());
				smPersonnelAddresses.add(getManagedBean()
						.getBasicSmPersonnelMailingAddress());
				//9569 - Start
				smPersonnelAddresses.add(getManagedBean()
						.getBasicSmPersonnelResidentialAddress());
				smPersonnelAddresses.add(getManagedBean()
						.getBasicSmPersonnelOfficeAddress());
				//9569 - End
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
				//#13320 - added division
				address.setPmDivision(new PmDivision());
				address.setPmCity(new PmCity());
				if (smPA.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.MAIL_ADD)) {
					address.setAddress1(smPA.getSmAddress().getAddress1());
					address.setAddress2(smPA.getSmAddress().getAddress2());
					address.setAddress3(smPA.getSmAddress().getAddress3());
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
					//#13320 - added division - start
					if(smPA.getSmAddress().getPmDivision() != null){
						address.getPmDivision().setDivisionId(
								smPA.getSmAddress().getPmDivision().getDivisionId());
						address.setDivisionId(smPA.getSmAddress().getDivisionId());
					}
					//#13320 - added division - end
					address.setPostcode(smPA.getSmAddress().getPostcode());
					smPA.setSmAddress(address);
					this.getManagedBean().setUpdateSmPersonnelMailingAddress(
							smPA);
				} else if (smPA.getAddressType().equalsIgnoreCase(
						SupplierManagementConst.PERM_ADD)) {
					address.setAddress1(smPA.getSmAddress().getAddress1());
					address.setAddress2(smPA.getSmAddress().getAddress2());
					address.setAddress3(smPA.getSmAddress().getAddress3());
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
					//#13320 - added division
					if(smPA.getSmAddress().getPmDivision() != null){
						address.getPmDivision().setDivisionId(
								smPA.getSmAddress().getPmDivision().getDivisionId());
						address.setDivisionId(smPA.getSmAddress().getDivisionId());
					}
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
			//#13320 - added division
			s1.setPmDivision(new PmDivision());
			s1.setPmDistrict(new PmDistrict());
			s1.setPmCity(new PmCity());

			/* Construct Permanent Address Object */
			reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			resCountryDTO = SmServiceLookup.getCountrySO().retrieveCountryById(
					reqCountryDTO);
			s2.setPmCountry(resCountryDTO.getPmCountry());
			s2.setPmState(new PmState());
			//#13320 - added division
			s1.setPmDivision(new PmDivision());
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

			//#13320 - added division
			updatePermAddLists(getManagedBean()
					.getUpdateSmPersonnelPermanentAddress().getSmAddress()
					.getPmCountry().getCountryId().toString(), "", "", "");
			updateMailAddLists(getManagedBean()
					.getUpdateSmPersonnelMailingAddress().getSmAddress()
					.getPmCountry().getCountryId().toString(), "", "", "");
		}
	}

	/**
	 * Save or update personnel in Supplier User Maintenance
	 * 
	 * @throws Exception
	 */
	public void save() {
		if (getManagedBean().getBasicSmPersonnel() != null && !(getManagedBean().getAccountStatus() == DbValueConst.RECORD_STATUS_ACTIVE)) {
			//#26050 - start
			validatePendingTask(getManagedBean().getBasicSmPersonnel());
			//#26050 - end
			SmCommonUtil.checkEmailDomainWithExceptionMsg(getManagedBean()
					.getBasicSmPersonnel().getName(), getManagedBean()
					.getBasicSmPersonnel().getEmail());
		}
		//32515 START
		//Update Role
		if (getManagedBean().getBasicSmPersonnel() != null && getManagedBean().getBasicSmPersonnel().getPersonnelId() != null && getManagedBean().getBasicSmPersonnel().getEpRole() != null &&
				getManagedBean().getEpRoleTrans() != null && !getManagedBean().getEpRoleTrans().equals(getManagedBean().getBasicSmPersonnel().getEpRole()) &&
				getManagedBean().getAccountStatus() == DbValueConst.RECORD_STATUS_ACTIVE) {
			
			// #28895_35501 Start - AUDIT TRAIL SM
			getManagedBean().setOldRole(getManagedBean().getEpRoleTrans());
			getManagedBean().setNewRole(getManagedBean().getBasicSmPersonnel().getEpRole());
			// #28895_35501 End
			String name = getManagedBean().getBasicSmPersonnel().getName();
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setSmSupplierId(getManagedBean().getSmSupplier().getSupplierId());//#33592-start
			reqDTO.setSmSupplier(getManagedBean().getSmSupplier());
			FacesScopes facesScopes = new FacesScopes();
			PmUser usr = facesScopes.getUserSessionBean();
			getPersonnelByPmUser(usr);
			reqDTO.setUserId(usr.getUserId());//#33592-end
			SmSoftcertRequest softcertRequest = SmServiceLookup.getPersonnelSO().retrievePersonnelSoftcertRequestBySupplierIdAndUserId(reqDTO).getSoftcertRequest();
			if (softcertRequest != null && softcertRequest.getPmDigiCert() != null){//#33592-start
				if (validateSoftcertExpired(softcertRequest)) {
					throw new EpException(MessageConst.ERROR_770252, new String[] { name });
				}else if(getManagedBean().getBasicSmPersonnel().getPmUser() != null && PmCommonUtil.isAnyPendingTask(getManagedBean().getBasicSmPersonnel().getPmUser().getLoginId())){
					throw new EpException(MessageConst.ERROR_770251, new String[] { name });
				}//#33592- end
			}
		}
		//32515 END
		//Start 13320
		// Start - 35178
		if (!getManagedBean().getBasicSmPersonnel().getSmPersonnelAddresses().isEmpty()) {
			// Start 13320
			for (SmPersonnelAddress per : getManagedBean().getBasicSmPersonnel().getSmPersonnelAddresses()) {
				// #33592-start
				if ((per.getSmAddress() != null)
						&& (per.getSmAddress().getPmState().getStateCode() != null && !per.getSmAddress().getPmState()
								.getStateCode().equals(SupplierManagementConst.SARAWAK_CODE))) {
					per.getSmAddress().setPmDivision(null);
				}// #33592-end
			}
			// End 13320
		}
		// End - 35178
		if (getManagedBean().getBasicSmPersonnel().getPersonnelId() == null) {
			//#9569 - Start
			getManagedBean().getBasicSmPersonnel().setFromBasicUserMaintenane(true);
			//#9569 - End
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
		resetUserLimitMsg();
	}
	
	//#26050 - start
	public void validatePendingTask(SmPersonnel smPersonnel) {
		if (getManagedBean().getBasicSmPersonnel().getPmUser() != null && PmCommonUtil.isAnyPendingTask(getManagedBean().getBasicSmPersonnel().getPmUser().getLoginId())
				|| smPersonnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING) {
			throw new EpException(MessageConst.ERROR_770231);
		}
	}
	//#26050 - end

	/**
	 * Deactivate or terminate authorized personnel
	 */
	public void deactivateOrTerminate() {
		updateUser(getManagedBean().getBasicSmPersonnel());
		initAuthorizedPersonnels(getManagedBean().getSmSupplier());
		getManagedBean().setDisableAddNewUser(
				validatePersonLimit(getManagedBean().getSmSupplier()));
		getManagedBean().setDeactivateTerminateConfirmMsg("");
	}

	/**
	 * Update existing personnel
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 * @throws Exception
	 */
	public void updateUser(SmPersonnel smPersonnel) {
		try {
			//start #12981 - no 2, comment #189
			List<Integer> statusAllowedToRequest = Arrays.asList(
					SupplierManagementConst.SOFT_CERT_ISSUED,
					SupplierManagementConst.IS_SOFTCERT_REQ,
					SupplierManagementConst.SOFT_CERT_PROCESSING,
					SupplierManagementConst.SOFT_CERT_PENDING_REQ,
					SupplierManagementConst.SOFT_CERT_REJECTED,
					SupplierManagementConst.SOFT_CERT_REQ_FAILED,
					SupplierManagementConst.SOFT_CERT_READY_FOR_RENEWAL);
			
			if (statusAllowedToRequest.contains(smPersonnel.getSoftcertFlg())) {
				smPersonnel.setAuthorizedFlg(1);
			}
			//end #12981 - no 2, comment #189
			
			if (smPersonnel.getTitle() != null
					&& smPersonnel.getTitle().getSalutationId() == null) {
				smPersonnel.setTitle(null);
			}
			PersonnelResDTO resDTO = updatePersonnel(smPersonnel);
			log.info("Result:" + resDTO.isSuccessful());
			if (resDTO.isSuccessful()) {
				// #28895_35501 Start - AUDIT TRAIL SM
				IAuditTrailSO auditSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT, IAuditTrailSO.class);
				if (auditSo.isAuditConfig(AuditSupplierManagementConst.AU_SM_CONF)) {
					AuditSupplierManagementBackingBeanTemplate auditSmBackingBean = new AuditSupplierManagementPersonnelBackingBean();
					auditSmBackingBean.saveAuditData(getManagedBean(), getUserIdSessionFromLoginId());
				}
				// #28895_35501 End
				SmCommonUtil.redirectToPage("userProfileSearch.xhtml",
						MessageConst.INFO_100002, null, null);
				getManagedBean().setBasicSmPersonnel(resDTO.getSmPersonnel());
			} else {
				throw new EpException(MessageConst.ERROR_722632);
			}
		} catch (Exception ex) {
			throw new EpException(MessageConst.ERROR_722632);
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
		SmPersonnel personnel = resDTO.getSmPersonnel();

		if (resDTO.isSuccessful()) {
			SmCommonUtil.redirectToPage("userProfileSearch.xhtml",
					MessageConst.INFO_721014,
					new String[] {
							smPersonnel.getEmail(),
							SmRuleHelper.getInstance().getEmailActivationDays()
									+ "" }, null);
		} else {
			throw new EpException(MessageConst.ERROR_722633);
		}
		return personnel;
	}

	/**
	 * Update existing personnel record in Supplier User Maintenance
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 * @return PersonnelResDTO
	 */
	private PersonnelResDTO updatePersonnel(SmPersonnel smPersonnel) {
		SmCommonUtil.setNewPersonnelPermAndMailAddress(smPersonnel,
				getManagedBean().getBasicSmPersonnelMailingAddress(),
				getManagedBean().getBasicSmPersonnelPermanentAddress());
		
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
		if (reqDTO.getAccountStatus() == DbValueConst.RECORD_STATUS_SUSPENDED
				|| reqDTO.getAccountStatus() == DbValueConst.RECORD_STATUS_DELETED) {
			reqDTO.setToken(fs.getBPMAuthenticationCtxToken());
		}
		if (reqDTO.isHasActiveAppl()) {
			reqDTO.setDraftApplId(getManagedBean().getCurrSmAppl().getApplId());
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

		SmCommonUtil.getInstance().verifyWithMyIdentity(per.getName(),
				per.getIdentificationNo(), postCode, null);

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
		FacesScopes fs = new FacesScopes();
		if (getManagedBean().getSelectedPersonnelToReassign() == null) {
			throw new EpException(MessageConst.ERROR_721111);
		} else {
			// #26050 - start
			if (getManagedBean().isHasActiveAppl() || !(getManagedBean().getTaskList().isEmpty()) && getManagedBean().getTaskList() != null
					&& getManagedBean().getTaskList().size() > 0) {
				throw new EpException(MessageConst.ERROR_770228, new String[] { fs.getUserSessionBean().getUserName() });
			}
			// #26050 - end
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
		reqDTO.setTaskList(getTaskList());
		reqDTO.setOldAdminRecordStatusFlg(getManagedBean()
				.getUpdateSmPersonnel().getEpRole()
				.equals(RoleCode.MOF_SUPPLIER_ADMIN.value()) ? DbValueConst.RECORD_STATUS_ACTIVE
				: DbValueConst.RECORD_STATUS_DELETED);
//		if (!EntityHelper.isCollectionEmpty(reqDTO.getTaskList())) {
//			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
//		}
		// [#26050 - start]
		if (EntityHelper.isCollectionEmpty(reqDTO.getTaskList())) {
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
		} else {
			throw new EpException(MessageConst.ERROR_770167);
		}
		// [#26050 - end]
		
		reqDTO.setActionType(my.ep.constant.sm.SupplierManagementConst.REPLACE_SUPP_ADMIN);

		PersonnelResDTO resDTO = new PersonnelResDTO();
		resDTO = SmServiceLookup.getPersonnelSO().reassignSupplierAdmin(reqDTO);

		if (resDTO.isSuccessful()) {
			// #28895_35501 Start - AUDIT TRAIL SM
			IAuditTrailSO auditSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT, IAuditTrailSO.class);
			if (auditSo.isAuditConfig(AuditSupplierManagementConst.AU_SM_CONF)) {
				AuditSupplierManagementBackingBeanTemplate auditSmBackingBean = new AuditSupplierManagementAdminInformationBackingBean();
				auditSmBackingBean.saveAuditData(getManagedBean(), getUserIdSessionFromLoginId());
			}
			// #28895_35501 End
			SmCommonUtil
					.callDeactivationSuccessDialog(MessageConst.INFO_721027);
		} else {
			throw new EpException(MessageConst.ERROR_721112);
		}
	}

	public void onChangeAccntStatus(AjaxBehaviorEvent e) {

	}

	private List<String> getTaskList() {
		List<String> taskList = new ArrayList<String>();
		if (!EntityHelper.isCollectionEmpty(getManagedBean().getTaskList())) {
			for (WorklistItemVO task : getManagedBean().getTaskList()) {
				taskList.add(task.getTaskId());
			}
		}
		return taskList;
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
	 * Set to disable or enable active or deactivate radio button
	 */
	private void setDisabledActivateDeactivateRadioButton() {
		getManagedBean().setDisabledActivateAndDeactivate(false);
		if (getManagedBean().getBasicSmPersonnel() != null
				&& getManagedBean().getBasicSmPersonnel().getEpRole() != null) {
			if (my.ep.constant.sm.SupplierManagementConst.SUPPLIER_ADMIN_ROLES
					.contains(getManagedBean().getBasicSmPersonnel()
							.getEpRole())
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
						}//START 9569
					} else if (personnelAddress
							.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES)) {
						if (personnelAddress.getSmAddress() != null) {
							getManagedBean().setBasicSmPersonnelResidentialAddress(
									personnelAddress);
						}
					} else if (personnelAddress
							.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE)) {
						if (personnelAddress.getSmAddress() != null) {
							getManagedBean().setBasicSmPersonnelOfficeAddress(
									personnelAddress);
						}
					}//END 9569
				}
			}
		}
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

	private void resetUserLimitMsg() {
		// reset user limit message
		if (!EntityHelper.isCollectionEmpty(getManagedBean()
				.getSmPersonnelList())) {
			int balanceUser = getManagedBean().getMaxUserLimit()
					- getManagedBean().getSmPersonnelList().size();

			if (balanceUser > 0) {
				getManagedBean().setUserLimitTooltips(
						SmCommonUtil.getMessage(
								"lbl.sm.userprofile.search.userlimit",
								new String[] { balanceUser + "" }));
			} else {
				getManagedBean()
						.setUserLimitTooltips(
								SmCommonUtil
										.getMessage(
												"lbl.sm.userprofile.search.userlimit.max",
												null));
			}
		}
	}
	
	//#13320 - added division - start
	private void updatePermAddLists(String countryId, String stateId, PmDivision division,
			String districtId){
		String divisionId = "";
		if(division != null && division.getDivisionId() != null)
			divisionId = division.getDivisionId().toString();
		updatePermAddLists(countryId, stateId, divisionId, districtId);
	}
	
	private void updateMailAddLists(String countryId, String stateId, PmDivision division,
			String districtId){
		String divisionId = "";
		if(division != null && division.getDivisionId() != null)
			divisionId = division.getDivisionId().toString();
		updateMailAddLists(countryId, stateId, divisionId, districtId);
	}
	//#13320 - added division - end
	
	//9569 - Start
	//Residential
	private void updateResAddLists(String countryId, String stateId, PmDivision division,
			String districtId){
		String divisionId = "";
		if(division != null && division.getDivisionId() != null)
			divisionId = division.getDivisionId().toString();
		updateResAddLists(countryId, stateId, divisionId, districtId);
	}
	//Office
	private void updateOffAddLists(String countryId, String stateId, PmDivision division,
			String districtId){
		String divisionId = "";
		if(division != null && division.getDivisionId() != null)
			divisionId = division.getDivisionId().toString();
		updateOffAddLists(countryId, stateId, divisionId, districtId);
	}
	//9569 - End

	// 9569 start
	/**
	 * Pre render for Supplier User Maintainance
	 */
	public void preRenderSmUserMaintenance() {
		FacesContext fc = FacesContext.getCurrentInstance();
		boolean allowAccess = true;
		SmSupplier smSupplier = SmApplicationUtil.getCurrentSupplier();
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		supplierSearchReqDTO.setSupplierId(smSupplier.getSupplierId());
		supplierSearchReqDTO.setRegNo(smSupplier.getRegNo());
		SupplierSearchResDTO searchResDTO = SmServiceLookup.getSupplierSearchSO().retriveSupplierById(supplierSearchReqDTO);
		
		smSupplier = searchResDTO.getSmSupplier();
		if(searchResDTO.getSmSupplier() != null && searchResDTO.getSmSupplier().getSmAppl().getSmCompanyBasic() != null){
			if(!searchResDTO.getSmSupplier().getSmAppl().getSupplierType().equals(SupplierManagementConst.STYPE_CONTRACTOR)){
					allowAccess = false;
					// SmCommonUtil.redirectToPage("supplierUserMaintenance.xhtml",
					// "", null, null);
					// throw new EpException(MessageConst.ERROR_722684);
			}else if(searchResDTO.getSmSupplier().getSmAppl().getSmCompanyBasic()!= null &&!searchResDTO.getSmSupplier().getSmAppl().getSmCompanyBasic().isWithFederal()){
				allowAccess = false;
			//	SmCommonUtil.redirectToPage("supplierUserMaintenance.xhtml", "", null, null);
				//throw new EpException(MessageConst.ERROR_722684);
			}
		}
		getManagedBean().setAllowAccess(allowAccess);
		if(!allowAccess){
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_722684,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/xhtml/sm/userProfileMaintenance/supplierUserMaintenance.xhtml");
		}else{
			getManagedBean().setSmSupplier(smSupplier);
			getManagedBean().setSmAppl(smSupplier.getSmAppl());
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
			List<SmPersonnel> smPersonnelList = SmServiceLookup.getPersonnelSO().retrieveMOFPersonnelListByApplId(reqDTO).getSmPersonnelList();
			
			//to get role for deleted personnel
			if (smPersonnelList != null && !smPersonnelList.isEmpty()) {
				for (SmPersonnel smPersonnel : smPersonnelList) {
					if(smPersonnel.getEpRole() == ""){
						smPersonnel.setEpRole(null);
					}
					
					if(smPersonnel.getEpRole() == null && smPersonnel.getPmUser() != null || smPersonnel.getEpRole() == "" && smPersonnel.getPmUser() != null){			
						Long userId = smPersonnel.getPmUser().getUserId();
						log.info("This is the user id == " + userId);
						String epRole = setPrevEpRoleSuppMain(userId);
						smPersonnel.setPrevEpRole(epRole);
					}else {
						smPersonnel.setPrevEpRole(smPersonnel.getEpRole());
					}
				}
			}
			
			sortPersonnelByEpRole(smPersonnelList);
			getManagedBean().setSmPersonnelList(smPersonnelList);
			countAuthUserWithRole(smPersonnelList);
			countContractSigner(smPersonnelList);
			epRoleList(null,2);
			getManagedBean().setCountryList(SmCommonUtil.getCountryList());
			getManagedBean().setSalutationItems(SmCommonUtil.getSalutationItems());
			getManagedBean().setRaceSelectItems(SmApplicationUtil.getRaceSelectItems());
			SmCommonUtil.redirectToPage("supplierUserMaintenance.xhtml", "", null, null);
		}
		
	}

	/**
	 * Generate Ep Role list for drop down
	 * 
	 * @param userAuth
	 */
	private void epRoleList(SmPersonnel personnel,int userAuth) {
		List<SelectItem> roles = new ArrayList<SelectItem>();
		if (userAuth == 2) {
			// all role
			SelectItem role = new SelectItem();
			role.setValue(RoleCode.MOF_SUPPLIER_ADMIN.value());
			role.setLabel(getEpRoleDescByRoleCode(role.getValue().toString()));
			roles.add(role);
			
			role = new SelectItem();
			role.setValue(RoleCode.MOF_SUPPLIER_USER.value());
			role.setLabel(getEpRoleDescByRoleCode(role.getValue().toString()));
			roles.add(role);

			role = new SelectItem();
			role.setValue(RoleCode.CM_USER.value());
			role.setLabel(getEpRoleDescByRoleCode(role.getValue().toString()));
			roles.add(role);

			role = new SelectItem();
			role.setValue(RoleCode.FL_USER.value());
			role.setLabel(getEpRoleDescByRoleCode(role.getValue().toString()));
			roles.add(role);

			getManagedBean().setRoleList(roles);
		} else if (userAuth == 1) {
			// authorized user role
			SelectItem role = new SelectItem();
			role.setValue(RoleCode.MOF_SUPPLIER_USER.value());
			role.setLabel(getEpRoleDescByRoleCode(role.getValue().toString()));
			roles.add(role);

			role = new SelectItem();
			role.setValue(RoleCode.CM_USER.value());
			role.setLabel(getEpRoleDescByRoleCode(role.getValue().toString()));
			roles.add(role);

			getManagedBean().setRoleList(roles);
		} else if (userAuth == 0) {
			// non-authorized user role
			SelectItem role = new SelectItem();
			role.setValue(RoleCode.FL_USER.value());
			role.setLabel(getEpRoleDescByRoleCode(role.getValue().toString()));
			roles.add(role);

			getManagedBean().setRoleList(roles);
		}else if(userAuth == 3){

			SelectItem role = new SelectItem();
			role.setValue(RoleCode.MOF_SUPPLIER_USER.value());
			role.setLabel(getEpRoleDescByRoleCode(role.getValue().toString()));
			roles.add(role);

			role = new SelectItem();
			role.setValue(RoleCode.CM_USER.value());
			role.setLabel(getEpRoleDescByRoleCode(role.getValue().toString()));
			roles.add(role);
			if(getManagedBean().getAuthUserCount()>9){
				role = new SelectItem();
				role.setValue(RoleCode.FL_USER.value());
				role.setLabel(getEpRoleDescByRoleCode(role.getValue().toString()));
				roles.add(role);
			}
			getManagedBean().setRoleList(roles);
		}
	}

	/**
	 * Reset search field
	 */
	public void resetMOFSmPersonnel() {
		this.getManagedBean().setSpName(null);
		this.getManagedBean().setSpCountryId(null);
		this.getManagedBean().setSpSoftcertFlg(0);
		this.getManagedBean().setSpRecordStatus(0);
		this.getManagedBean().setSpEpRole(null);
		this.getManagedBean().setSpIsAuthorize(0);
		this.getManagedBean().setSpIEpRoleStatus(0);
	}

	/**
	 * Search personnel based on criteria
	 */
	public void searchMOFSmPersonnel() {
		//since default value in drop down is defaulted by "0" to get "not required" data need to be in "0"
		//therefore, in backingBean "select one" is actually equals to 99
		if(getManagedBean().getSpSoftcertFlg() == 0){
			getManagedBean().setSpSoftcertFlg(99);
		}else if(getManagedBean().getSpSoftcertFlg() == 99){
			getManagedBean().setSpSoftcertFlg(0);
		}
		SmSupplier smSupplier = SmApplicationUtil.getCurrentSupplier();
		getManagedBean().setSmSupplier(smSupplier);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(smSupplier.getSupplierId());
		reqDTO.setApplId(smSupplier.getSmAppl().getApplId());
		reqDTO.setHasActiveAppl(false);
		if (getManagedBean().getSpName().isEmpty() && getManagedBean().getSpCountryId() == null
				&& getManagedBean().getSpSoftcertFlg() == 99 && getManagedBean().getSpRecordStatus() == 0
				&& getManagedBean().getSpEpRole() == null && getManagedBean().getSpIsAuthorize() == 0
				&& getManagedBean().getSpIEpRoleStatus() == 0) {
			reqDTO.setSmPersonnel(null);
		} else {
			reqDTO.setSmPersonnel(new SmPersonnel());
			reqDTO.getSmPersonnel().setPmCountry(new PmCountry());
			if (!getManagedBean().getSpName().isEmpty()) {
				reqDTO.getSmPersonnel().setName(this.getManagedBean().getSpName());
			}

			if (getManagedBean().getSpCountryId() != null) {
				reqDTO.getSmPersonnel().getPmCountry().setCountryId(this.getManagedBean().getSpCountryId());
			}
			
			if(getManagedBean().getSpRecordStatus() == 0){
				reqDTO.getSmPersonnel().setRecordStatusFlg(99);
			}else{
				reqDTO.getSmPersonnel().setRecordStatusFlg(this.getManagedBean().getSpRecordStatus());
			}
			
			//as 29/7/2021 - Start
			//Since default value in 0, this step required for is authorized
			if(getManagedBean().getSpIsAuthorize() == 0){
				reqDTO.getSmPersonnel().setAuthorizedFlg(99);
			}else{
				if(getManagedBean().getSpIsAuthorize() == 1){
					reqDTO.getSmPersonnel().setAuthorizedFlg(this.getManagedBean().getSpIsAuthorize());	
				}else if(getManagedBean().getSpIsAuthorize() == 2){
					reqDTO.getSmPersonnel().setAuthorizedFlg(0);
				}			
			}
			//as 29/7/2021 - End
			
			if (getManagedBean().getSpSoftcertFlg() == 99) {
				reqDTO.getSmPersonnel().setSoftcertFlg(99);
			} else if (getManagedBean().getSpSoftcertFlg() != 99) {
				reqDTO.getSmPersonnel().setSoftcertFlg(this.getManagedBean().getSpSoftcertFlg());
			}
		}
		List<SmPersonnel> smPersonnelList = SmServiceLookup.getPersonnelSO().retrieveMOFPersonnelListByApplId(reqDTO)
				.getSmPersonnelList();
		
		//for deleted personnel epRole
		//to get role for deleted personnel
		if (smPersonnelList != null && !smPersonnelList.isEmpty()) {
			for (SmPersonnel smPersonnel : smPersonnelList) {
				if(smPersonnel.getEpRole() == ""){
					smPersonnel.setEpRole(null);
				}
				
				if(smPersonnel.getEpRole() == null && smPersonnel.getPmUser() != null || smPersonnel.getEpRole() == "" && smPersonnel.getPmUser() != null){			
					Long userId = smPersonnel.getPmUser().getUserId();
					log.info("This is the user id == " + userId);
					String epRole = setPrevEpRoleSuppMain(userId);
					smPersonnel.setPrevEpRole(epRole);
				}else {
					smPersonnel.setPrevEpRole(smPersonnel.getEpRole());
				}
			}
		}
		
		//start removing by using list
		if(smPersonnelList != null && !smPersonnelList.isEmpty() && smPersonnelList.size() > 0){
			ListIterator<SmPersonnel> itr = smPersonnelList.listIterator();
			
			while(itr.hasNext()){
				SmPersonnel personnel = itr.next();		
				//for searching by epRole
				if(getManagedBean().getSpEpRole() != null && getManagedBean().getSpEpRole() != ""){
					if(personnel.getPrevEpRole() != null && personnel.getPrevEpRole() != ""){
						if(getManagedBean().getSpEpRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.value())){
							if(!personnel.getPrevEpRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.value())){
								itr.remove();
								continue;
							}	
						}else if(getManagedBean().getSpEpRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value())){
							if(!personnel.getPrevEpRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value())){
								itr.remove();
								continue;
							}
						}else if(getManagedBean().getSpEpRole().equalsIgnoreCase(RoleCode.CM_USER.value())){
							if(!personnel.getPrevEpRole().equalsIgnoreCase(RoleCode.CM_USER.value())){
								itr.remove();
								continue;
							}	
						}else if(getManagedBean().getSpEpRole().equalsIgnoreCase(RoleCode.FL_USER.value())){
							if(!personnel.getPrevEpRole().equalsIgnoreCase(RoleCode.FL_USER.value())){
								itr.remove();
								continue;
							}
						}
					}else{
						itr.remove();
						continue;
					}
				}
				//For searching epRoleStatus
				if(getManagedBean().getSpIEpRoleStatus() != 0){
					if(getManagedBean().getSpIEpRoleStatus() == 1){
						if(!(personnel.getEpRole() != null && personnel.getPmUser() != null)){
							itr.remove();
							continue;
						}
					}else if(getManagedBean().getSpIEpRoleStatus() == 2){
						if(!(personnel.getPmUser() != null && personnel.getEpRole() == null)){
							itr.remove();
							continue;
						}
					}
				}
			}
		}
		
		sortPersonnelByEpRole(smPersonnelList);
		getManagedBean().setSmPersonnelList(smPersonnelList);
		//change back value for the softcert flag
		if(getManagedBean().getSpSoftcertFlg() == 0){
			getManagedBean().setSpSoftcertFlg(99);
		}else if(getManagedBean().getSpSoftcertFlg() == 99){
			getManagedBean().setSpSoftcertFlg(0);
		}
	}
	
	/**
	 * Navigate to personnel edit page
	 * 
	 * @param personnelId
	 * @return
	 */
	public String navigateToUpdateMOFUserPage(String personnelId) {
		FacesScopes sc = new FacesScopes();
		getManagedBean().setEditMode(true);
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode("MY");
		CountryResDTO resDTO = SmServiceLookup.getCountrySO()
				.retrieveCountryByCode(reqDTO);
		getManagedBean().setDefaultCountryId(
				resDTO.getPmCountry().getCountryId());
		//9569 - Start
		resetAll();
		//9569 - End
		getPersonnelInfoById(personnelId);
		getManagedBean().setEpRoleTrans(getManagedBean().getBasicSmPersonnel().getEpRole());
		getManagedBean().setRaceSelectItems(SmApplicationUtil.getRaceSelectItems());
		//set role checking
		SmAuthorizedPersonnelCountVO countVO = SmApplicationUtil.getSmAuthorizedPersonnelCountVO(getManagedBean().getSmAppl());
		getManagedBean().setAuthVO(countVO);
		getManagedBean().setValidSmUserLimit(countVO.isReachAuthorizedPersonnelLimit());
		buildRoleList(getManagedBean().getBasicSmPersonnel());
		//epRoleList(getManagedBean().getBasicSmPersonnel(),getManagedBean().getBasicSmPersonnel().getAuthorizedFlg());
		initializeLists();
		log.info(getManagedBean().getBasicSmPersonnel().getTitle().getSalutationName());
		/*if(getManagedBean().getBasicSmPersonnel().getEpRole() != null && getManagedBean().getBasicSmPersonnel().getEpRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.value())){
			epRoleList(getManagedBean().getBasicSmPersonnel(),2);
		}*/
		getManagedBean().setDisableRoleSelection(disableEpRoleDropDown(getManagedBean()
				.getBasicSmPersonnel()));
		return "mofUserProfile.xhtml";
	}
	
	//9569 - Start
	public void resetAll(){
		this.getManagedBean().setBasicSmPersonnelPermanentAddress(new SmPersonnelAddress());
		this.getManagedBean().setBasicSmPersonnelMailingAddress(new SmPersonnelAddress());
		this.getManagedBean().setBasicSmPersonnelResidentialAddress(new SmPersonnelAddress());
		this.getManagedBean().setBasicSmPersonnelOfficeAddress(new SmPersonnelAddress());
	}
	//9569 - End

	/**
	 * Back to search page
	 */
	public void cancelUserMOFUpdate() {
		preRenderSmUserMaintenance();
		//return "supplierUserMaintenance.xhtml";
	}

	/**
	 * Save personnel update
	 */
	public void setAddress(List<SmPersonnelAddress> smPersonnelAddresses,SmPersonnelAddress personnelAddress){
		SmPersonnelAddress personnelAdd = (SmPersonnelAddress) EntityHelper.getEntityBySinglePropertyFromList(personnelAddress.getPersonnelAddressId(),smPersonnelAddresses,"personnelAddressId");
		if(personnelAdd == null){
			smPersonnelAddresses.add(personnelAddress);
		}
	}
	
	/**
	 * Set personnel previous role
	 * 
	 * @param SmPersonnel
	 *            personnel
	 */
	private String setPrevEpRoleSuppMain(Long userId) {
		String epRole = "";
		PersonnelReqDTO personnelReqDTO = new PersonnelReqDTO();
		personnelReqDTO.setPmUserId(userId);
		PersonnelResDTO personnelResDTO = SmServiceLookup.getPersonnelSO().retrieveUserRolesForDeactivatedPersonnel(personnelReqDTO);
		if (!personnelResDTO.isSuccessful()) {
			throw new EpException(MessageConst.ERROR_721108);
		}
		epRole = personnelResDTO.getDeletedRole();
		return epRole;
	}
	
	public void saveUserMOFUpdate() {
		SmPersonnel personnel = getManagedBean().getBasicSmPersonnel();
		
		//#9569 - Start
		//to validate if user that admin want to delete or change role doesn't have any pending task - Start
		//Cannot delete eP Role for {0} as there are pending tasks that belongs to this user.
		if(personnel.getPmUser() != null && personnel.getEpRole() != null){
			if(getManagedBean().getEpRoleTrans() != null && getManagedBean().getEpRoleTrans().equalsIgnoreCase(SupplierManagementConst.EPROLE_DELETED)){
				if (personnel.getPmUser() != null && PmCommonUtil.isAnyPendingTask(personnel.getPmUser().getLoginId())
						|| personnel.getPmUser() != null
						&& personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING
						|| personnel.getPmUser() != null
						&& personnel.getSoftcertFlg() == SupplierManagementConst.IS_SOFTCERT_REQ) {
					String errMsg = personnel.getName();
					throw new EpException(MessageConst.ERROR_770950, new String[] { errMsg });
				}
			} else if (getManagedBean().getEpRoleTrans() != null
					&& !personnel.getEpRole().equalsIgnoreCase(getManagedBean().getEpRoleTrans())
					&& !getManagedBean().getEpRoleTrans().equalsIgnoreCase(SupplierManagementConst.EPROLE_DELETED)) {
				if (personnel.getPmUser() != null && PmCommonUtil.isAnyPendingTask(personnel.getPmUser().getLoginId())
						|| personnel.getPmUser() != null
						&& personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING
						|| personnel.getPmUser() != null
						&& personnel.getSoftcertFlg() == SupplierManagementConst.IS_SOFTCERT_REQ) {
					String errMsg = personnel.getName();
					throw new EpException(MessageConst.ERROR_770949, new String[] { errMsg });
				}
			}	
		}
		//to validate if user that admin want to delete or change role doesn't have any pending task - End
		//#9569 - End
		
		try {
			
			// to check if selected role is deleted or not
			boolean ePDeleted = false;
			// to check for the ep status
			boolean ePStatus = false;
			//to send status to SO
			String status = null;
			
			// This is to save all the address
			SmPersonnelAddress personnelAddressPer = getManagedBean().getBasicSmPersonnelPermanentAddress();
			SmPersonnelAddress personnelAddress = getManagedBean().getBasicSmPersonnelMailingAddress();
			SmPersonnelAddress personnelAddress1 = getManagedBean().getBasicSmPersonnelResidentialAddress();
			SmPersonnelAddress personnelAddress2 = getManagedBean().getBasicSmPersonnelOfficeAddress();

			setAddress(personnel.getSmPersonnelAddresses(), personnelAddressPer);
			setAddress(personnel.getSmPersonnelAddresses(), personnelAddress);

			// To check if the address not empty
			if (getManagedBean().getBasicSmPersonnelResidentialAddress().getSmAddress().getAddress1() != null) {
				setAddress(personnel.getSmPersonnelAddresses(), personnelAddress1);
			}
			if (getManagedBean().getBasicSmPersonnelOfficeAddress().getSmAddress().getAddress1() != null) {
				setAddress(personnel.getSmPersonnelAddresses(), personnelAddress2);
			}

			for (SmPersonnelAddress per : personnel.getSmPersonnelAddresses()) {
				if (!per.getSmAddress().getPmState().getStateCode().equals(SupplierManagementConst.SARAWAK_CODE)) {
					per.getSmAddress().setPmDivision(null);
				}
			}
			PersonnelReqDTO reqDTO2 = new PersonnelReqDTO();
			reqDTO2.setSmPersonnel(personnel);

			// start updating role
			// SmPersonnel personnel1 = getManagedBean().getBasicSmPersonnel();
			if (personnel.getEpRole() == null) {
				personnel.setEpRole("");
			}

			int recheckedSoftcert = 0;
			if (personnel.getPmUser() != null && (personnel.getEpRole() == null ||personnel.getEpRole() == "" ) && personnel.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
				ePStatus = true;
				recheckedSoftcert = personnel.getSoftcertFlg();
			}
			
			//The whole changing delete and changing role process start here
			if (getManagedBean().getEpRoleTrans() != null
					&& !personnel.getEpRole().equalsIgnoreCase(getManagedBean().getEpRoleTrans())) {
				// #28895_35501 Start - AUDIT TRAIL SM
				getManagedBean().setOldRole(personnel.getEpRole());
				getManagedBean().setNewRole(getManagedBean().getEpRoleTrans());
				// #28895_35501 End
				//If user has a pmUser or active role
				if (personnel.getPmUser() != null) {
					//If supplier admin want to delete the role
					if (getManagedBean().getEpRoleTrans().equalsIgnoreCase(SupplierManagementConst.EPROLE_DELETED)) {
						PersonnelReqDTO reqDTO = new PersonnelReqDTO();
						reqDTO.setRdeleted(true);
						reqDTO.setSmPersonnel(personnel);
						reqDTO.getSmPersonnel().setPrevEpRole(personnel.getEpRole());
						PersonnelResDTO personnelRes = SmServiceLookup.getPersonnelSO().updateOrDeletedRole(reqDTO, status);
						if(!personnelRes.isSuccessful()){
							log.info("Unsuccessful to delete personnel role");
							throw new EpException(MessageConst.ERROR_722632);
						}
					} 
					//If supplier admin want to change the role to supplier user or fullfillment user
					else if (getManagedBean().getEpRoleTrans().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value()) && !ePStatus
							|| getManagedBean().getEpRoleTrans().equalsIgnoreCase(RoleCode.FL_USER.value())
							&& !ePStatus) {

						int softcert = checkSoftCert();
						if(softcert == 0){
							softcert = 4;
						}
						
						PersonnelReqDTO reqDTO1 = new PersonnelReqDTO();
						PersonnelResDTO personnelRes = new PersonnelResDTO();

						personnel.setSoftcertFlg(softcert);
						reqDTO1.setSmPersonnel(personnel);
						reqDTO1.setNewEpRole(getManagedBean().getEpRoleTrans());
						personnelRes = SmServiceLookup.getPersonnelSO().updateMofPersonnelAndPmRole(reqDTO1);
						if (!personnelRes.isSuccessful()) {
							throw new EpException(MessageConst.ERROR_722632);
						}
					} 
					//if supplier admin want to change the role to catalog user
					else if (getManagedBean().getEpRoleTrans().equalsIgnoreCase(RoleCode.CM_USER.value()) && !ePStatus){
						PersonnelReqDTO reqDTO1 = new PersonnelReqDTO();
						PersonnelResDTO personnelRes = new PersonnelResDTO();
						
						if (personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PENDING_REQ
								|| personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REJECTED) {
							personnel.setSoftcertFlg(SupplierManagementConst.RECORD_STATUS_SUSPENDED);
						}
						personnel.setContractSignerFlg(Integer.parseInt(ParameterConst.FLAG_NO));
						reqDTO1.setSmPersonnel(personnel);
						reqDTO1.setNewEpRole(getManagedBean().getEpRoleTrans());
						personnelRes = SmServiceLookup.getPersonnelSO().updateMofPersonnelAndPmRole(reqDTO1);
						if (!personnelRes.isSuccessful()) {
							throw new EpException(MessageConst.ERROR_722632);
						}
					}
					
					//reactivating from deleted, and assign MOF Supplier User
					else if (getManagedBean().getEpRoleTrans().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value())
							&& ePStatus) {

						PersonnelReqDTO reqDTO = new PersonnelReqDTO();
						reqDTO.setReactive(true);
						personnel.setEpRole(getManagedBean().getEpRoleTrans());
						reqDTO.setSmPersonnel(personnel);
						PersonnelResDTO personnelRes = SmServiceLookup.getPersonnelSO().updateOrDeletedRole(reqDTO,status);
						
						if(!personnelRes.isSuccessful()){
							throw new EpException(MessageConst.ERROR_722632);
						}
					}
					
					//reactivating from deleted, and assign catalog user
					else if (getManagedBean().getEpRoleTrans().equalsIgnoreCase(RoleCode.CM_USER.value()) && ePStatus) {
						PersonnelReqDTO reqDTO = new PersonnelReqDTO();
						reqDTO.setReactive(true);
						personnel.setContractSignerFlg(Integer.parseInt(ParameterConst.FLAG_NO));
						personnel.setEpRole(getManagedBean().getEpRoleTrans());
						reqDTO.setSmPersonnel(personnel);
						PersonnelResDTO personnelRes = SmServiceLookup.getPersonnelSO().updateOrDeletedRole(reqDTO, status);
						
						if(!personnelRes.isSuccessful()){
							throw new EpException(MessageConst.ERROR_722632);
						}
					}		
					//reactivating from deleted, and assign fullfillment user
					else if (getManagedBean().getEpRoleTrans().equalsIgnoreCase(RoleCode.FL_USER.value()) && ePStatus) {
						PersonnelReqDTO reqDTO = new PersonnelReqDTO();
						reqDTO.setReactive(true);
						personnel.setEpRole(getManagedBean().getEpRoleTrans());
						reqDTO.setSmPersonnel(personnel);
						PersonnelResDTO personnelRes = SmServiceLookup.getPersonnelSO().updateOrDeletedRole(reqDTO, status);
						
						if(!personnelRes.isSuccessful()){
							throw new EpException(MessageConst.ERROR_722632);
						}
					}
				} else {
					
					PersonnelReqDTO reqDTO1 = new PersonnelReqDTO();
					PersonnelResDTO resDTO1 = new PersonnelResDTO();
					personnel.setActivationDate(new Timestamp(System.currentTimeMillis())); //32040
					/*personnel.setCreatedDate(new Timestamp(System.currentTimeMillis()));*/
					
					//admin assign new roles to personnel (supplier user & fullfillment user)
					if (getManagedBean().getEpRoleTrans().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value()) && !ePStatus
							|| getManagedBean().getEpRoleTrans().equalsIgnoreCase(RoleCode.FL_USER.value()) && !ePStatus) {
						
						personnel.setEpRole(getManagedBean().getEpRoleTrans());
						personnel.setRecordStatusFlg(DbValueConst.RECORD_STATUS_PENDING_ACTIVATION);
						personnel.setSoftcertFlg(SupplierManagementConst.SOFT_CERT_PENDING_REQ);
					}
					
					//admin assign new roles to personnel (Catalog User)
					else if(getManagedBean().getEpRoleTrans().equalsIgnoreCase(RoleCode.CM_USER.value()) && !ePStatus) {
						
						personnel.setEpRole(getManagedBean().getEpRoleTrans());
						personnel.setRecordStatusFlg(DbValueConst.RECORD_STATUS_PENDING_ACTIVATION);
						personnel.setSoftcertFlg(SupplierManagementConst.RECORD_STATUS_SUSPENDED);
					}
					
					//just delete, personnel didn't yet activate the account
					else if (getManagedBean().getEpRoleTrans().equalsIgnoreCase(
							SupplierManagementConst.EPROLE_DELETED)) {

						personnel.setEpRole(null);
						personnel.setSoftcertFlg(SupplierManagementConst.RECORD_STATUS_SUSPENDED);
						personnel.setContractSignerFlg(Integer.parseInt(ParameterConst.FLAG_NO));
						personnel.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);

					}
					
					if (personnel.getEpRole() != null) {
						List<SmPersonnel> smPersonnelList = new ArrayList<SmPersonnel>();
						smPersonnelList.add(personnel);
						reqDTO1.setSmPersonnelList(smPersonnelList);
						reqDTO1.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
						resDTO1 = SmServiceLookup.getPersonnelSO().sendPersonnelActivationEmail(reqDTO1);
						reqDTO2.setSmPersonnel(personnel);
						log.info("End first update------------------------------------------------------");
						SmServiceLookup.getPersonnelSO().updatePersonnelRecord(reqDTO2);
						log.info("End second update------------------------------------------------------");
						if (!resDTO1.isSuccessful()) {
							throw new EpException(MessageConst.ERROR_722632);
						}
					} else {
						reqDTO2.setSmPersonnel(personnel);
						resDTO1 = SmServiceLookup.getPersonnelSO().updatePersonnelRecord(reqDTO2);
						if (!resDTO1.isSuccessful()) {
							throw new EpException(MessageConst.ERROR_722632);
						}
					}
				}
			} else {
				PersonnelResDTO resDTO2 = SmServiceLookup.getPersonnelSO().updatePersonnelRecord(reqDTO2);
				if (!resDTO2.isSuccessful()) {
					throw new EpException(MessageConst.ERROR_722632);
				}
			}
			// 9569
			// Make sure table is updated
			// #28895_35501 Start - AUDIT TRAIL SM
			IAuditTrailSO auditSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT, IAuditTrailSO.class);
			if (auditSo.isAuditConfig(AuditSupplierManagementConst.AU_SM_CONF)) {
				AuditSupplierManagementBackingBeanTemplate auditSmBackingBean = new AuditSupplierManagementPersonnelBackingBean();
				auditSmBackingBean.saveAuditData(getManagedBean(), getUserIdSessionFromLoginId());
			}
			// #28895_35501 End
			PersonnelReqDTO reqDTO3 = new PersonnelReqDTO();
			reqDTO3.setApplId(getManagedBean().getSmAppl().getApplId());
			List<SmPersonnel> smPersonnelList2 = SmServiceLookup.getPersonnelSO().retrieveMOFPersonnelListByApplId(reqDTO3).getSmPersonnelList();
			//to get role for deleted personnel
			if (smPersonnelList2 != null && !smPersonnelList2.isEmpty()) {
				for (SmPersonnel smPersonnel : smPersonnelList2) {
					if(smPersonnel.getEpRole() == ""){
						smPersonnel.setEpRole(null);
					}
					
					if((smPersonnel.getEpRole() == null ||smPersonnel.getEpRole() == "") && smPersonnel.getPmUser() != null){			
						Long userId = smPersonnel.getPmUser().getUserId();
						log.info("This is the user id == " + userId);
						String epRole = setPrevEpRoleSuppMain(userId);
						smPersonnel.setPrevEpRole(epRole);
					}else {
						smPersonnel.setPrevEpRole(smPersonnel.getEpRole());
					}
				}
			}
			sortPersonnelByEpRole(smPersonnelList2);
			getManagedBean().setSmPersonnelList(smPersonnelList2);
			countAuthUserWithRole(smPersonnelList2);
			countContractSigner(smPersonnelList2);
			epRoleList(null,2);
		    SmCommonUtil.redirectToPage("supplierUserMaintenance.xhtml", MessageConst.INFO_100002, null, null);

		  }catch (Exception ex){
		      throw new EpException(MessageConst.ERROR_722632);
		  }
		}
	
	public int checkSoftCert(){
		int value = 4;
		PersonnelReqDTO reqDTO1 = new PersonnelReqDTO();
		reqDTO1.setSmSupplierId(getManagedBean().getBasicSmPersonnel().getSmAppl().getSmSupplier().getSupplierId());
		reqDTO1.setSmSupplier(getManagedBean().getBasicSmPersonnel().getSmAppl().getSmSupplier());
		reqDTO1.setUserId(getManagedBean().getBasicSmPersonnel().getPmUser().getUserId());
		SmSoftcertRequest softcertRequest = SmServiceLookup.getPersonnelSO().retrievePersonnelSoftcertRequestBySupplierIdAndUserId(reqDTO1).getSoftcertRequest();
		
		if(softcertRequest != null){
			if(softcertRequest.getPmDigiCert() != null && softcertRequest.getPmDigiCert().getValidTo() != null){
				Calendar renewDate = Calendar.getInstance();
				renewDate.setTime(softcertRequest.getPmDigiCert().getValidTo());
				renewDate.add(Calendar.MONTH, -SmServiceLookup.getSmRuleSO().retrieveSoftCertRenewalPeriod().getSoftCertRenewalMonth());
				if (DateUtils.isDateGreaterOrEqual(new Date(), renewDate.getTime())) {
					value = SupplierManagementConst.SOFT_CERT_ISSUED;
					//temp solution to handle with softcert management after payment
				}else if (softcertRequest != null && softcertRequest.getPmDigiCert() != null) {
					value = SupplierManagementConst.SOFT_CERT_ISSUED;
				}	
			}else if (softcertRequest != null && softcertRequest.getPmDigiCert() == null
					&& softcertRequest.getReasonCode() != null){			
				value = SupplierManagementConst.SOFT_CERT_REJECTED;
			}else if (softcertRequest != null && softcertRequest.getPmDigiCert() == null){
				value = SupplierManagementConst.SOFT_CERT_PROCESSING;
			}	
		}else{
			value = getManagedBean().getBasicSmPersonnel().getSoftcertFlg(); 
		}
		return value;	
	}
//9569 - End
	/**
	 * Sort personnel list based Ep Role
	 * 
	 * @param smPersonnelList
	 */
	public static void sortPersonnelByEpRole(List<SmPersonnel> smPersonnelList) {
		if (smPersonnelList != null) {
			for (SmPersonnel per : smPersonnelList) {
				if (per.getPrevEpRole() != null) {
					if (per.getPrevEpRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
						per.setSort(1);
					} else if (per.getPrevEpRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value()) && !(per.getEpRole() == null && per.getPmUser() != null)) {
						per.setSort(2);
					} else if (per.getPrevEpRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value())){
						per.setSort(3);					
					} else if (per.getPrevEpRole().equalsIgnoreCase(RoleCode.CM_USER.value()) && !(per.getEpRole() == null && per.getPmUser() != null)) {
						per.setSort(4);
					} else if (per.getPrevEpRole().equalsIgnoreCase(RoleCode.CM_USER.value())){
						per.setSort(5);
					} else if (per.getPrevEpRole().equalsIgnoreCase(RoleCode.FL_USER.value()) && !(per.getEpRole() == null && per.getPmUser() != null)) {
						per.setSort(6);
					} else if (per.getPrevEpRole().equalsIgnoreCase(RoleCode.FL_USER.value())) {
						per.setSort(7);
					} else{
						per.setSort(8);
					}
				} else {
					per.setSort(8);
				}
			}
			// sort by name
			Collections.sort(smPersonnelList,
					new Comparator<SmPersonnel>() {
						@Override
						public int compare(final SmPersonnel s1,
								final SmPersonnel s2) {
							return (s1.getName()).compareTo(s2.getName());
						}
					});

			// sort by sort
			Collections.sort(smPersonnelList,
					new Comparator<SmPersonnel>() {
						@Override
						public int compare(final SmPersonnel s1,
								final SmPersonnel s2) {
							return (s2.getSort() > s1.getSort() ? -1 : (s1
									.getSort() == s2.getSort() ? 0 : 1));
						}
					});
			
			/*// sort by name
				Collections.sort(smPersonnelList, new Comparator<SmPersonnel>() {
					@Override
					public int compare(final SmPersonnel s1, final SmPersonnel s2) {
						return (s1.getName()).compareTo(s2.getName());
					}
				});
				// sort by sort
				Collections.sort(smPersonnelList, new Comparator<SmPersonnel>() {
					@Override
					public int compare(final SmPersonnel s1, final SmPersonnel s2) {
						return (s2.getSort() > s1.getSort() ? -1 : (s1.getSort() == s2.getSort() ? 0 : 1));
					}
				});*/

		}
	}

	/**
	 * Count authorized user
	 * 
	 * @param smPersonnelList
	 */
	public void countAuthUserWithRole(List<SmPersonnel> smPersonnelList) {
		if (smPersonnelList != null) {
			int count = 0;
			for (SmPersonnel per : smPersonnelList) {
				if (per.getAuthorizedFlg() == 1 && per.getPmUser() != null) {
					count++;
				}
			}
			getManagedBean().setAuthUserCount(count);
		}
	}

	/**
	 * Count authorized user
	 * 
	 * @param smPersonnelList
	 */
	public void countContractSigner(List<SmPersonnel> smPersonnelList) {
		if (smPersonnelList != null) {
			int count = 0;
			for (SmPersonnel per : smPersonnelList) {
				if (per.getContractSignerFlg() == 1) {
					count++;
				}
			}
			getManagedBean().setContractSignerCount(count);
		}
	}
	
	/**
	 * To check whether need to disable ep role drop down
	 * 
	 * @param smPersonnel
	 * @return boolean
	 */
	public static boolean disableEpRoleDropDown(SmPersonnel smPersonnel) {
		boolean retVal = false;

		// disabled drop down for Admin
		if (!StringUtils.isBlank(smPersonnel.getEpRole())
				&& (smPersonnel.getEpRole().equals(
						RoleCode.MOF_SUPPLIER_ADMIN.value()))) {
			retVal = true;
		}
		return retVal;
	}
	
	public void buildRoleList(SmPersonnel personnel) {
		getManagedBean().setRoleList(
				SmApplicationUtil.getPersonnelRoleItems(personnel,
						getManagedBean().getSmAppl(), getManagedBean()
								.getAuthVO()));
	}

	//start - #30053
	public boolean isShowInfo() {
		return showInfo;
	}

	public void setShowInfo(boolean showInfo) {
		this.showInfo = showInfo;
	}
	//end - #30053
	// 9569 end
	
	//START 30053
	public boolean disableRadio(SmPersonnel personnel){
		boolean disableRadio  = false;
		try{
			if(personnel.getSmAppl()!= null && SupplierManagementConst.SUPP_TYPE_CONSULTANT.equalsIgnoreCase(personnel.getSmAppl().getSupplierType())){
				disableRadio = false;
			}else if(RoleCode.CM_USER.value().equals(personnel.getEpRole()) || (personnel.getSoftcertFlg() != 0 && personnel.getSoftcertFlg()!= 1)){
				disableRadio = true;
			}
		}catch(Exception e){
			log.info(e.getMessage());
		}
		return disableRadio;
	}
	//END 30053
	
	//9569
	public static boolean personnelToDeleteRole(SmPersonnel basicSmPersonnel) {
		boolean retVal = false;
		if(basicSmPersonnel.getEpRole()!=null && basicSmPersonnel.getAuthorizedFlg()==1 &&
				!basicSmPersonnel.getEpRole().equalsIgnoreCase(SupplierManagementConst.MOF_SUPPLIER_ADMIN)){
			retVal = true;
		}
		return retVal;
	}
	
	//32515 START	
	public Boolean validateSoftcertExpired(SmSoftcertRequest softcertRequest){
		Boolean valid = false;
		if(softcertRequest != null){
			Date currentDate = new Date();
			if(currentDate.before(softcertRequest.getPmDigiCert().getValidTo())){
				valid = true;
			}
		}
		return valid;
	}
	//32515 END
	// #28895_35501 Start - AUDIT TRAIL SM
	private void copyOldData(SmSupplier smSupplier) {
		getManagedBean().setOldSmSupplier(smSupplier);
		String oldRole = null;
		FacesScopes facesScope = new FacesScopes();
		if (facesScope.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
			oldRole = RoleCode.BASIC_SUPPLIER_ADMIN.value();
		} else if (facesScope.hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())) {
			oldRole = RoleCode.G2G_SUPPLIER_ADMIN.value();
		} else if (facesScope.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
			oldRole = RoleCode.MOF_SUPPLIER_ADMIN.value();
		}
		getManagedBean().setOldRole(oldRole);
		PmOrgProfile orgProfile = new PmOrgProfile();
		if (getUserIdSessionFromLoginId() != null) {
			List<PmUserOrg> orgList = PmCommonUtil.retrieveActiveOrg(getUserIdSessionFromLoginId());
			if (!orgList.isEmpty()) {
				orgProfile = orgList.get(0).getPmOrgProfile();
			}
		}
		getManagedBean().setOldOrg(orgProfile);
	}
	// #28895_35501 End

}
