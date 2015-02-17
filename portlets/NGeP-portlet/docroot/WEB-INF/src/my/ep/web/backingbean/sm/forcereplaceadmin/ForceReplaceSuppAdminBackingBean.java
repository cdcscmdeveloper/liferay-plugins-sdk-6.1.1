package my.ep.web.backingbean.sm.forcereplaceadmin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletSession;

import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
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
import my.ep.persistence.entity.pm.common.PmRace;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.forcereplaceadmin.ForceReplaceSuppAdminManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.vo.ReactivateSupplierVO;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "forceReplaceSuppAdminBackingBean")
@RequestScoped
public class ForceReplaceSuppAdminBackingBean extends
		BaseBackingBean<ForceReplaceSuppAdminManagedBean> {
	
	
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
		initIdentificationTypes();
		initCountries();
		initStatest();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "forceReplaceAdmin.xhtml");
	}

	public void submit() {
		getManagedBean().setExistingUser(false);
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setRegNo(getManagedBean().getReactivateBasic().getRegNo());
		reqDTO.setEstDate(getManagedBean().getReactivateBasic()
				.getEstablishDate());
		getManagedBean().setSmSupplier(
				getSupplierSO()
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

	public void replaceWithNewUser() {
		getManagedBean().getNewAdmin().setIdentificationTypeId(
				getManagedBean().getReactivateBasic()
						.getNewIdentificationType());
		getManagedBean().getNewAdmin().setIdentificationNo(
				getManagedBean().getReactivateBasic().getNewIdentificationNo());
		PersonnelResDTO resDto = getPersonnelSO().replaceAdminWithNewPersonnel(
				getReqDTO());
		redirectToSuccessPage(resDto);
	}

	public void replaceWithExistingUser() {
		PersonnelResDTO resDto = getPersonnelSO().reassignNewSupplierAdmin(
				getReqDTO());
		redirectToSuccessPage(resDto);
	}

	public String cancelAddInFo() {
		getManagedBean().setExistingUser(false);
		return "forceReplaceAdmin.xhtml";
	}

	public void cancelTncPopup() {
		getManagedBean().setExistingUser(false);
		getManagedBean().setAgreeTnc(false);
		RequestContext.getCurrentInstance().execute("tncDialog.hide()");
	}

	public Date getCurrDate() {
		return new Date();
	}

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
		reqDTO.setOldAdminRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
		return reqDTO;
	}

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

		List<SmPersonnel> latestActivePersonnels = getManagedBean()
				.getSmSupplier().getSmAppl().getSmPersonnels();
		if (latestActivePersonnels != null && !latestActivePersonnels.isEmpty()) {
			for (SmPersonnel smPersonnel : latestActivePersonnels) {
				if (smPersonnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
						&& smPersonnel.getIdentificationNo() != null
						&& StringUtils.isNotBlank(smPersonnel
								.getIdentificationNo())
						&& smPersonnel.getEpRole() != null
						&& StringUtils.isNotBlank(smPersonnel.getEpRole())) {

					if (smPersonnel.getSmAppl() != null) {
						// already mof
						if (smPersonnel.getSmAppl().getApplNo() != null
								&& StringUtils.isNotBlank(smPersonnel
										.getSmAppl().getApplNo())) {
							if (smPersonnel.getEpRole().equals(
									RoleCode.MOF_SUPPLIER_USER.value())) {
								getManagedBean().setHasSupplierUser(true);
							}
						}
						// basic
						else {
							if (smPersonnel.getEpRole().equals(
									RoleCode.BASIC_SUPPLIER_USER.value())) {
								getManagedBean().setHasSupplierUser(true);
							}
						}
					}
					if (smPersonnel.getIdentificationNo().equalsIgnoreCase(
							getManagedBean().getReactivateBasic()
									.getIdentificationNo())) {
						getManagedBean().setOldAdmin(smPersonnel);
					} else if (smPersonnel.getIdentificationNo()
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
				&& getManagedBean().getNewAdmin() != null
				&& getManagedBean().getNewAdmin().getEpRole() != null
				&& StringUtils.isNotBlank(getManagedBean().getNewAdmin()
						.getEpRole())) {

			// check if new admin is not activated is not allowed
			if (getManagedBean().getNewAdmin().getEpRole() == null
					|| StringUtils.isBlank(getManagedBean().getNewAdmin()
							.getEpRole())
					|| getManagedBean().getNewAdmin().getPmUser() == null
					|| getManagedBean().getNewAdmin().getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE) {
				throw new EpException(MessageConst.ERROR_722596);
			}

			// check if replace with catalogue user not allowed if have supplier
			// user
			if (getManagedBean().getNewAdmin().getEpRole()
					.equals(RoleCode.CM_USER.value())
					&& getManagedBean().isHasSupplierUser()) {
				throw new EpException(MessageConst.ERROR_722595);
			}
		}

	}

	private void executeDecision() {
		// replace with new personnel
		if (!getManagedBean().isExistingUser()) {
			initDefaultNewAdmin();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/forcereplaceadmin/addNewAdminInfo.xhtml");
		}
		// replace with existing personnel
		else {
			RequestContext.getCurrentInstance().execute("tncDialog.show()");
		}
	}

	private void initDefaultNewAdmin() {
		getManagedBean().setNewAdmin(new SmPersonnel());
		setDefaultNewAdminPersonnelValues();
		setDefaultNewAdminAddress();
	}

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

	private void initCountries() {
		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryResDTO resDTO = countrySO.retrieveCountryList();
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

	private void initStatest() {
		getManagedBean().setPermStates(
				SmCommonUtil.getStateList(getManagedBean().getMyCountryId()));
		getManagedBean().setMailStates(
				SmCommonUtil.getStateList(getManagedBean().getMyCountryId()));
	}

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

		getManagedBean().getNewAdmin().getSmPersonnelAddresses()
				.add(getManagedBean().getPermAddr());
		getManagedBean().getNewAdmin().getSmPersonnelAddresses()
				.add(getManagedBean().getMailAddr());
	}

	private void setDefaultNewAdminPersonnelValues() {
		getManagedBean().getNewAdmin().setEpRole(
				getManagedBean().getOldAdmin().getEpRole());
		getManagedBean().getNewAdmin().setSmAppl(
				getManagedBean().getSmSupplier().getSmAppl());
		getManagedBean().getNewAdmin().setRevNo(1);
		getManagedBean().getNewAdmin().setRecordStatusFlg(
				DbValueConst.RECORD_STATUS_ACTIVE);
		getManagedBean().getNewAdmin().setBumiFlg(1);
		getManagedBean().getNewAdmin().setMuslimFlg(1);

		PmCountry country = SmCommonUtil
				.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		getManagedBean().getNewAdmin().setPmCountry(country);
		getManagedBean().getNewAdmin().setPmRace(new PmRace());
		getManagedBean().getNewAdmin().setSmPersonnelAddresses(
				new ArrayList<SmPersonnelAddress>());
	}

	private void redirectToSuccessPage(PersonnelResDTO resDto) {
		if (resDto.isSuccessful()) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_721027,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/userProfileMaintenance/outputMsg");
		} else {
			throw new EpException(MessageConst.ERROR_721113);
		}
	}

	private boolean isHavePendingTaskList() {
		boolean retVal = false;
		BpmRequestDTO reqDTO = null;
		BpmResponseDTO responseDTO = null;
		try {
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
				getManagedBean().setToken(getBpmAuthenticateToken());
			}
			reqDTO.setCtxToken(getManagedBean().getToken());
			responseDTO = getBpmSO().getWorklistItems(reqDTO);
			if (responseDTO.getWorklistVO() != null) {
				if (responseDTO.getWorklistVO().getTotal() > 0) {
					retVal = true;
				}
			}
		} finally {
			reqDTO = null;
			responseDTO = null;
		}
		return retVal;
	}

	public void assignIdentificationType(AjaxBehaviorEvent e) {
		if (getManagedBean().getReactivateBasic().getNewIdentificationType() != null) {
			getManagedBean().setIdentificationType(
					SmApplicationUtil.retrievePmParameterById(getManagedBean()
							.getReactivateBasic().getNewIdentificationType()));
		}
	}

	public IPersonnelSO getPersonnelSO() {
		return (IPersonnelSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);
	}

	public ISupplierSearchSO getSupplierSO() {
		return (ISupplierSearchSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISupplierSearchSO.class);
	}

	public IBpmSO getBpmSO() {
		return (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(
				IBpmSO.class);
	}

	private String getBpmAuthenticateToken() {
		PortletSession portletSession = (PortletSession) FacesContext
				.getCurrentInstance().getExternalContext().getSession(false);
		Object ctxToken = portletSession.getAttribute("BPM_AUTH_CONTEXT_TOKEN",
				PortletSession.APPLICATION_SCOPE);
		if (null == ctxToken) {
			String loginId = getManagedBean().getOldAdmin().getPmUser()
					.getLoginId();
			if (loginId != null) {
				ctxToken = getBpmSO().authenticate(loginId);
				portletSession.setAttribute("BPM_AUTH_CONTEXT_TOKEN", ctxToken,
						PortletSession.APPLICATION_SCOPE);
			}
		}
		return (String) ctxToken;
	}
}
