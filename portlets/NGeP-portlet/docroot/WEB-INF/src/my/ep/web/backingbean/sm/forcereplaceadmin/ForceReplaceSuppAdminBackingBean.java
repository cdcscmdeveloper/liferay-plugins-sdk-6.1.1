package my.ep.web.backingbean.sm.forcereplaceadmin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.forcereplaceadmin.ForceReplaceSuppAdminManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.ReactivateSupplierVO;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "forceReplaceSuppAdminBackingBean")
@RequestScoped
public class ForceReplaceSuppAdminBackingBean extends
		BaseBackingBean<ForceReplaceSuppAdminManagedBean> {

	/**
	 * Pre - render and redirect to Force Replace Supplier Admin page
	 */
	public void preRenderForceReplaceAdmin() {
		renewSesionObject();
		getManagedBean().setMyCountryId(SmCommonUtil.getMyCountryId());
		getManagedBean().setPermStates(new ArrayList<SelectItem>());
		getManagedBean().setPermDistricts(new ArrayList<SelectItem>());
		getManagedBean().setPermCities(new ArrayList<SelectItem>());
		getManagedBean().setMailStates(new ArrayList<SelectItem>());
		getManagedBean().setMailDistricts(new ArrayList<SelectItem>());
		getManagedBean().setMailCities(new ArrayList<SelectItem>());
		getManagedBean().setHasSupplierUser(false);
		getManagedBean().setReactivateBasic(new ReactivateSupplierVO());
		getManagedBean().setSmSupplier(new SmSupplier());
		initIdentificationTypes();
		initCountries();
		initStates();
		SmCommonUtil.redirectToPage("forceReplaceAdmin.xhtml", "", null);
	}

	/**
	 * Submit force replace and display pop up (for existing personnel) or
	 * redirect to new personnel record page
	 */
	public void submit() {
		getManagedBean().setExistingUser(false);
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setRegNo(getManagedBean().getReactivateBasic().getRegNo());
		reqDTO.setEstDate(getManagedBean().getReactivateBasic()
				.getEstablishDate());
		getManagedBean().setSmSupplier(
				SmServiceLookup
						.getSupplierSearchSO()
						.retrieveSuppProfileAndPersonnelsByBussRegNoAndEstDate(
								reqDTO).getSmSupplier());

		// Company not exist
		if (getManagedBean().getSmSupplier() == null
				|| getManagedBean().getSmSupplier().getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE) {
			throw new EpException(MessageConst.ERROR_722593);
		} else {
			validate();
			executeDecision();
		}
	}

	/**
	 * Replace admin with new personnel and redirect to success page
	 */
	public void replaceWithNewUser() {
		getManagedBean().getNewAdmin().setIdentificationTypeId(
				getManagedBean().getReactivateBasic()
						.getNewIdentificationType());
		getManagedBean().getNewAdmin().setIdentificationNo(
				getManagedBean().getReactivateBasic().getNewIdentificationNo());
		SmCommonUtil.setNewPersonnelPermAndMailAddress(getManagedBean()
				.getNewAdmin(), getManagedBean().getMailAddr(),
				getManagedBean().getPermAddr());
		PersonnelResDTO resDto = SmServiceLookup.getPersonnelSO()
				.replaceAdminWithNewPersonnel(getReqDTO());
		redirectToSuccessPage(resDto);
	}

	/**
	 * Replace admin with existing personnel and redirect to success page
	 */
	public void replaceWithExistingUser() {
		PersonnelResDTO resDto = SmServiceLookup.getPersonnelSO()
				.reassignSupplierAdmin(getReqDTO());
		redirectToSuccessPage(resDto);
	}

	/**
	 * Redirect back to Force Replace Admin first page from add new personnel
	 * information
	 * 
	 * @return String
	 */
	public String back() {
		getManagedBean().setAgreeTnc(false);
		getManagedBean().setExistingUser(false);
		getManagedBean().getReactivateBasic().setShowEmailInput(false);
		getManagedBean().setNewAdmin(null);
		getManagedBean().setOldAdmin(null);
		return "forceReplaceAdmin.xhtml";
	}

	/**
	 * Close terms and condition pop up
	 */
	public void cancelTncPopup() {
		getManagedBean().setExistingUser(false);
		getManagedBean().setAgreeTnc(false);
		RequestContext.getCurrentInstance().execute("tncDialog.hide()");
	}

	/**
	 * Get current date
	 * 
	 * @return Date
	 */
	public Date getCurrDate() {
		return new Date();
	}

	/**
	 * Show or hide email input after click submit
	 */
	private void setShowOrHideEmail() {
		getManagedBean().getReactivateBasic().setShowEmailInput(false);
		if (getManagedBean().getNewAdmin() != null
				&& StringUtils.isBlank(getManagedBean().getNewAdmin()
						.getEmail())
				|| getManagedBean().getNewAdmin().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION) {
			getManagedBean().getReactivateBasic().setShowEmailInput(true);
		}
	}

	/**
	 * Get constructed DTO
	 * 
	 * @return PersonnelReqDTO
	 */
	private PersonnelReqDTO getReqDTO() {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(getManagedBean().getSmSupplier().getSupplierId());
		reqDTO.setAdminPersonnel(getManagedBean().getOldAdmin());
		reqDTO.setSmPersonnel(getManagedBean().getNewAdmin());
		reqDTO.setApplId(getManagedBean().getSmSupplier().getSmAppl()
				.getApplId());
		FacesScopes fs = new FacesScopes();
		reqDTO.setLanguage(fs.getUserLocale().getLanguage());
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		reqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
		reqDTO.setOldAdminRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
		return reqDTO;
	}

	/**
	 * Validate before proceed to force replace admin
	 */
	private void validate() {

		// check if new admin identificationNo same as existing admin
		// identificationNo
		if (getManagedBean()
				.getReactivateBasic()
				.getIdentificationNo()
				.equals(getManagedBean().getReactivateBasic()
						.getNewIdentificationNo())) {
			throw new EpException(MessageConst.ERROR_722600);
		}

		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(getManagedBean().getSmSupplier().getSupplierId());
		List<SmPersonnel> latestActivePersonnels = SmServiceLookup
				.getPersonnelSO()
				.retrieveLatestApprovedPersonnelBySupplierId(reqDTO)
				.getSmPersonnelList();

		if (!SmCommonUtil.isCollectionEmpty(latestActivePersonnels)) {
			for (SmPersonnel smPersonnel : latestActivePersonnels) {
				if (smPersonnel.getIdentificationNo() != null
						&& StringUtils.isNotBlank(smPersonnel
								.getIdentificationNo())
						&& smPersonnel.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {

					if (smPersonnel.getSmAppl() != null) {
						// already mof
						if (StringUtils.isNotBlank(smPersonnel.getSmAppl()
								.getApplType())
								&& !smPersonnel
										.getSmAppl()
										.getApplType()
										.equals(SupplierManagementConst.ACC_BASIC)) {
							// check if already had active mof supplier user
							// account
							if (StringUtils.isNotBlank(smPersonnel.getEpRole())
									&& smPersonnel.getEpRole().equals(
											RoleCode.MOF_SUPPLIER_USER.value())
									&& smPersonnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
								getManagedBean().setHasSupplierUser(true);
							}
						}
						// basic
						else {
							// check if already had active basic supplier user
							// account
							if (StringUtils.isNotBlank(smPersonnel.getEpRole())
									&& smPersonnel.getEpRole().equals(
											RoleCode.BASIC_SUPPLIER_USER
													.value())
									&& smPersonnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
								getManagedBean().setHasSupplierUser(true);
							}
						}
					}
					// set old admin
					if (smPersonnel.getIdentificationNo().equalsIgnoreCase(
							getManagedBean().getReactivateBasic()
									.getIdentificationNo())) {
						getManagedBean().setOldAdmin(smPersonnel);
					}
					// set new admin
					else if (smPersonnel.getIdentificationNo()
							.equalsIgnoreCase(
									getManagedBean().getReactivateBasic()
											.getNewIdentificationNo())) {
						getManagedBean().setNewAdmin(smPersonnel);
						getManagedBean().setExistingUser(true);
					}
				}
			}
		}

		// old admin not exist
		if (getManagedBean().getOldAdmin() == null) {
			throw new EpException(MessageConst.ERROR_722594);
		}

		// existing new admin
		if (getManagedBean().isExistingUser()
				&& getManagedBean().getNewAdmin() != null) {

			// check if replace with catalog user not allowed if have active
			// supplier
			// user
			if (StringUtils.isNotBlank(getManagedBean().getNewAdmin()
					.getEpRole())
					&& getManagedBean().getNewAdmin().getEpRole()
							.equals(RoleCode.CM_USER.value())
					&& getManagedBean().isHasSupplierUser()) {
				throw new EpException(MessageConst.ERROR_722595);
			}

			// check if replace with pending activation user not allowed if have
			// active supplier user
			if (getManagedBean().getNewAdmin().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION
					&& getManagedBean().isHasSupplierUser()) {
				throw new EpException(MessageConst.ERROR_722629);
			}

			// check if replace with user that don't have role in ep not allowed
			// if have active supplier user
			if (StringUtils.isBlank(getManagedBean().getNewAdmin().getEpRole())
					&& getManagedBean().isHasSupplierUser()) {
				throw new EpException(MessageConst.ERROR_722630);
			}
		}

	}

	/**
	 * Execute based on decision for existing personnel call terms and condition
	 * dialog pop up and for new personnel redirect to new personnel info page
	 */
	private void executeDecision() {
		// replace with new personnel
		if (!getManagedBean().isExistingUser()) {
			initDefaultNewAdmin();
			SmCommonUtil.redirectToPage(
					"/xhtml/sm/forcereplaceadmin/addNewAdminInfo.xhtml", "",
					null);
		}
		// replace with existing personnel
		else {
			setShowOrHideEmail();
			SmCommonUtil.redirectToPage(
					"/xhtml/sm/forcereplaceadmin/addExistingAdminInfo.xhtml",
					"", null);
		}
	}

	/**
	 * Initialize new admin values
	 */
	private void initDefaultNewAdmin() {
		getManagedBean().setNewAdmin(new SmPersonnel());
		setDefaultNewAdminPersonnelValues();
		setDefaultNewAdminAddress();
	}

	/**
	 * Initialize list of identification types
	 */
	private void initIdentificationTypes() {
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescList(ParameterTypeConst.IDENTIFICATION_TYPES);
		Map<Long, Object> identityType = new HashMap<Long, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			identityType.put(pmDesc.getPmParameter().getParameterId(),
					pmDesc.getPmParameter());
		}
		getManagedBean().setIdentificationTypes(new ArrayList<SelectItem>());
		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.IDENTIFICATION_TYPES, null);

		for (SelectItem selectItem : items) {
			if (selectItem.getValue() == null) {
				getManagedBean().getIdentificationTypes().add(selectItem);
			} else {
				PmParameter pmParameter = (PmParameter) identityType
						.get(selectItem.getValue());
				if (!pmParameter.getParameterCode().equals("A")) {
					getManagedBean().getIdentificationTypes().add(selectItem);
				}
			}
		}
	}

	/**
	 * Initialize list of country
	 */
	private void initCountries() {
		CountryResDTO resDTO = SmServiceLookup.getCountrySO()
				.retrieveCountryList();
		List<SelectItem> countryList = new ArrayList<SelectItem>();
		SelectItem si = null;
		for (PmCountry country : resDTO.getCountryList()) {
			si = new SelectItem();
			si.setLabel(country.getCountryName());
			si.setValue(country.getCountryId());
			countryList.add(si);
		}
		getManagedBean().setNationalities(countryList);
		getManagedBean().setPermCountries(countryList);
		getManagedBean().setMailCountries(countryList);
	}

	/**
	 * Initialize list of state
	 */
	private void initStates() {
		getManagedBean().setPermStates(
				SmCommonUtil.getStateList(getManagedBean().getMyCountryId()));
		getManagedBean().setMailStates(
				SmCommonUtil.getStateList(getManagedBean().getMyCountryId()));
	}

	/**
	 * Update mail address country, state and district dropdown list
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
			getManagedBean().setMailStates(
					SmCommonUtil.getStateList(Long.valueOf(countryId)));
			getManagedBean().setMailDistricts(null);
			getManagedBean().setMailCities(null);
		}
		if (!stateId.isEmpty() || stateId.equalsIgnoreCase("null")) {
			getManagedBean().setMailDistricts(
					SmCommonUtil.getDistrictList(Long.valueOf(stateId)));
			getManagedBean().setMailCities(null);
		}
		if (!districtId.isEmpty() || districtId.equalsIgnoreCase("null")) {
			getManagedBean().setMailCities(
					SmCommonUtil.getCityList(Long.valueOf(districtId)));
		}
	}

	/**
	 * Update permanent address country, state and district dropdown list
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
			getManagedBean().setPermStates(
					SmCommonUtil.getStateList(Long.valueOf(countryId)));
			getManagedBean().setPermDistricts(null);
			getManagedBean().setPermCities(null);
		}
		if (!stateId.isEmpty() || stateId.equalsIgnoreCase("null")) {
			getManagedBean().setPermDistricts(
					SmCommonUtil.getDistrictList(Long.valueOf(stateId)));
			getManagedBean().setPermCities(null);
		}
		if (!districtId.isEmpty() || districtId.equalsIgnoreCase("null")) {
			getManagedBean().setPermCities(
					SmCommonUtil.getCityList(Long.valueOf(districtId)));
		}
	}

	/**
	 * Set default permanent and mail address to Malaysia
	 */
	private void setDefaultNewAdminAddress() {
		PmCountry countryMy = SmCommonUtil
				.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		SmPersonnelAddress perm = new SmPersonnelAddress();
		perm.setAddressType(SupplierManagementConst.PERM_ADD);
		perm.setSmAddress(SmApplicationUtil.setDefaultAddr(countryMy));
		perm.setSmPersonnel(getManagedBean().getNewAdmin());
		perm.setSameAsPerm(false);

		SmPersonnelAddress mail = new SmPersonnelAddress();
		mail.setAddressType(SupplierManagementConst.MAIL_ADD);
		mail.setSmAddress(SmApplicationUtil.setDefaultAddr(countryMy));
		mail.setSmPersonnel(getManagedBean().getNewAdmin());
		mail.setSameAsPerm(false);

		getManagedBean().setPermAddr(perm);
		getManagedBean().setMailAddr(mail);
	}

	/**
	 * Set default new personnel values
	 */
	private void setDefaultNewAdminPersonnelValues() {
		getManagedBean().getNewAdmin().setEpRole(
				getManagedBean().getOldAdmin().getEpRole());
		getManagedBean().getNewAdmin().setSmAppl(
				getManagedBean().getSmSupplier().getSmAppl());
		getManagedBean().getNewAdmin().setRevNo(1);
		getManagedBean().getNewAdmin().setBumiFlg(1);
		getManagedBean().getNewAdmin().setMuslimFlg(1);
		getManagedBean().getNewAdmin().setRecordStatusFlg(
				DbValueConst.RECORD_STATUS_PENDING_ACTIVATION);
		getManagedBean().getNewAdmin().setAuthorizedFlg(
				DbValueConst.RECORD_STATUS_ACTIVE);
		getManagedBean().getNewAdmin().setContractSignerFlg(
				DbValueConst.RECORD_STATUS_ACTIVE);

		PmCountry country = SmCommonUtil
				.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		getManagedBean().getNewAdmin().setPmCountry(country);
	}

	/**
	 * Redirect to success page
	 * 
	 * @param PersonnelResDTO
	 *            resDto
	 */
	private void redirectToSuccessPage(PersonnelResDTO resDTO) {
		if (resDTO.isSuccessful()) {
			if (getManagedBean().getNewAdmin().getPmUser() == null) {
				SmCommonUtil.redirectToPage(
						SmApplicationUtil.getApplicationErrorPage(),
						MessageConst.INFO_721037, new String[] {
								getManagedBean().getNewAdmin().getEmail(),
								SmRuleHelper.getInstance()
										.getEmailActivationDays() + "" });
			} else {
				if (getManagedBean().getReactivateBasic().isShowEmailInput()) {
					SmCommonUtil.redirectToPage(
							SmApplicationUtil.getApplicationErrorPage(),
							MessageConst.INFO_721037, new String[] {
									getManagedBean().getNewAdmin().getEmail(),
									SmRuleHelper.getInstance()
											.getEmailActivationDays() + "" });
				} else {
					SmCommonUtil.redirectToPage(
							SmApplicationUtil.getApplicationErrorPage(),
							MessageConst.INFO_721036, null);
				}
			}
		} else {
			throw new EpException(MessageConst.ERROR_721113);
		}
	}

	/**
	 * Set identification type
	 * 
	 * @param AjaxBehaviorEvent
	 *            e
	 */
	public void assignIdentificationType(AjaxBehaviorEvent e) {
		if (getManagedBean().getReactivateBasic().getNewIdentificationType() != null) {
			getManagedBean().setIdentificationType(
					SmApplicationUtil.retrievePmParameterById(getManagedBean()
							.getReactivateBasic().getNewIdentificationType()));
		}
	}

	/**
	 * Redirect to Terms And Condition Details Page
	 */
	public void viewTnc() {
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/forcereplaceadmin/tncDetails.xhtml", "", null);
	}

}
