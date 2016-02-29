package my.ep.web.util.pm.helper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.role.IRoleSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.confgeneral.IConfigGeneralSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.language.ILanguageSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.salutation.ISalutationSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.userattachment.IUserAttachmentSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.role.RoleReqDTO;
import my.ep.dto.pm.authorization.role.RoleResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.authorization.userorg.UserOrgReqDTO;
import my.ep.dto.pm.authorization.userorg.UserOrgResDTO;
import my.ep.dto.pm.common.PmTrackingDairyVO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.confgeneral.ConfigGeneralReqDTO;
import my.ep.dto.pm.common.confgeneral.ConfigGeneralResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.dto.pm.common.language.LanguageResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.salutation.SalutationReqDTO;
import my.ep.dto.pm.common.salutation.SalutationResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.userattachment.UserAttachmentReqDTO;
import my.ep.dto.pm.common.userattachment.UserAttachmentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserAttachment;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCertSignatory;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmConfigGeneral;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmLanguage;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.fee.settings.PmFeeExemption;
import my.ep.persistence.entity.pm.general.settings.gst.PmDesignatedArea;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRule;
import my.ep.persistence.entity.pm.general.settings.threshold.PmThreshold;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.id.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * CommonUtil for Profile Management
 * 
 * @author RAIN POH
 * @since 8/7/2013
 * 
 */
public class PmCommonUtil {
	private static Logger log = Logger.getLogger(PmCommonUtil.class);
	private static final String FILE_DATETIME_FORMAT = "ddMMyyyyHHmmss";

	/**
	 * Check date overlapping
	 * 
	 * @param startA
	 * @param endA
	 * @param startB
	 * @param endB
	 * @param allowedStartEndSameDate
	 * @return boolean
	 */
	public static boolean dateOverlapping(Date startA, Date endA, Date startB,
			Date endB, boolean allowedStartEndSameDate) {

		boolean isCurrentPairOverlap = false;
		boolean isStartABeforeEndB = false;
		boolean isEndAAfterStartB = false;
		// cannot overlap
		if (allowedStartEndSameDate) {
			isStartABeforeEndB = ((startA.compareTo(endB)) < 0);
			isEndAAfterStartB = ((endA.compareTo(startB)) > 0);
		} else {
			isStartABeforeEndB = ((startA.compareTo(endB)) < 0 || startA
					.equals(endB));
			isEndAAfterStartB = ((endA.compareTo(startB)) > 0 || endA
					.equals(startB));
		}
		isCurrentPairOverlap = isStartABeforeEndB && isEndAAfterStartB;

		return isCurrentPairOverlap;
	}

	public static boolean checkRoleRestriction(String roleCode,
			List<PmUserRole> userRoles) {
		log.info("checkRoleRestriction");
		List<String> checkRoles = new ArrayList<String>();
		IRoleSO roleSO = null;
		boolean restrictedRole = false;
		try {
			for (PmUserRole pmUserRole : userRoles) {
				checkRoles.add(pmUserRole.getPmRole().getRoleCode());
			}

			RoleReqDTO roleReqDTO = new RoleReqDTO();
			roleReqDTO.setRoleCode(roleCode);
			roleReqDTO.setRestrictedRoles(checkRoles);
			roleReqDTO.setModuleCode(ModuleCodeConst.PROFILE_MANAGEMENT);
			roleSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRoleSO.class);
			RoleResDTO resDto = roleSO.checkRoleRestriction(roleReqDTO);
			restrictedRole = resDto.isRestrictedRole();
		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770137,
					new String[] { e.getMessage() });
		} finally {
			roleSO = null;
		}
		return restrictedRole;
	}

	/**
	 * Gets the message.
	 * 
	 * @param key
	 *            - String key
	 * @param params
	 *            - String[]
	 * @return the message
	 */
	public static String getMessage(String key, String[] params) {
		Locale locale = new FacesScopes().getUserLocale();
		String msg = "";

		if (params != null) {
			msg = ResourceBundleUtils.getMessage(key, params,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		} else {
			msg = ResourceBundleUtils.getMessage(key,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		}

		return msg;
	}
	
	/**
	 * get record status options
	 * 
	 * @return SelectItem[]
	 */
	public static SelectItem[] getStatusOptionsDefault() {
		SelectItem[] items = new SelectItem[2];
		items[0] = new SelectItem(DbValueConst.RECORD_STATUS_ACTIVE,
				ResourceBundleUtils.getMessage("lbl.pm.active",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items[1] = new SelectItem(DbValueConst.RECORD_STATUS_DELETED,
				ResourceBundleUtils.getMessage("lbl.pm.inactive",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		return items;
	}

	/**
	 * get record status options active 1 inactive 0
	 * 
	 * @return SelectItem[]
	 */
	public static SelectItem[] getRecordStatusOptions() {
		SelectItem[] items = new SelectItem[2];
		items[0] = new SelectItem(DbValueConst.RECORD_STATUS_ACTIVE,
				ResourceBundleUtils.getMessage("lbl.pm.active",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items[1] = new SelectItem(DbValueConst.RECORD_STATUS_SUSPENDED,
				ResourceBundleUtils.getMessage("lbl.pm.inactive",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		return items;
	}

	/**
	 * Get Role description by role code.
	 * 
	 * @param roleCode
	 *            the role code.
	 * @return role description
	 */
	public static String getRoleDescByRoleCode(String roleCode) {
		String roleDesc = null;

		if (roleCode != null) {
			// Get the role description.
			PmRoleDesc pmRoleDesc = RoleHelper.getInstance().getRoleDescByCode(
					roleCode);

			if (pmRoleDesc != null) {
				roleDesc = pmRoleDesc.getRoleName();
			}
		}

		return roleDesc;
	}
	
	/**
	 * Get Role description by role code and Language code.
	 * 
	 * @param roleCode
	 *            the role code.
	 * @param languageCode
	 *            the language code.
	 * @return role description
	 */
	public static String getRoleDescByRoleCodeAndLanguage(String roleCode, String languageCode) {
		String roleDesc = null;

		if (roleCode != null) {
			// Get the role description.
			PmRoleDesc pmRoleDesc = RoleHelper.getInstance().getRoleDescByCodeAndLanguage(
					roleCode, languageCode);

			if (pmRoleDesc != null) {
				roleDesc = pmRoleDesc.getRoleName();
			}
		}

		return roleDesc;
	}
	
	/**
	 * initialize a complete Pm Address
	 */
	public static PmAddress getNewPmAddress() {
		PmCountry defaultCountry = PmCommonUtil
				.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		PmAddress pmAddress = new PmAddress();
		pmAddress.setPmCountry(defaultCountry != null ? defaultCountry
				: new PmCountry());
		pmAddress.setPmState(new PmState());
		pmAddress.setPmDistrict(new PmDistrict());
		pmAddress.setPmCity(new PmCity());
		pmAddress.setPmDivision(new PmDivision());
		return pmAddress;
	}

	/**
	 * retrieve pmUser without org info by id
	 * 
	 * @param id
	 * @return PmUser
	 */
	public static PmUser retrieveUserWithoutOrgInfoById(Long id) {
		PmUser pmUser = new PmUser();
		IUserSO userSO = null;
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = null;
			reqDTO.setUserId(id);
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDTO = userSO.retrieveUserProfileByUserId(reqDTO);
			if (resDTO.isSuccessful()) {
				pmUser = resDTO.getPmUser();
			}
		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770053, new String[] {
					id.toString(), e.getMessage() });
		} finally {
			userSO = null;
		}
		return pmUser;
	}

	/**
	 * set default notification mode and preferences
	 * 
	 * @param pmUser
	 */
	public static void loadPmUserPreference(PmUser pmUser) {
		IConfigGeneralSO configGeneralSO = null;
		try {
			configGeneralSO = (IConfigGeneralSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IConfigGeneralSO.class);
			ConfigGeneralReqDTO configGeneralReqDTO = new ConfigGeneralReqDTO();
			ConfigGeneralResDTO configGeneralResDTO = configGeneralSO
					.getConfigGeneral(configGeneralReqDTO);
			PmConfigGeneral configGeneral = null;
			if (configGeneralResDTO != null
					&& configGeneralResDTO.isSuccessful()) {
				configGeneral = configGeneralResDTO.getPmConfigGeneral();
			}
			if (configGeneral == null) {
				throw new EpException(MessageConst.ERROR_770052);
			}
			if (pmUser.getPreferredLanguage() == null) {
				pmUser.setPreferredLanguage(configGeneral
						.getPreferredLanguage());
			}
			// if(pmUser.getNotifyRemovalDay()==null){
			// pmUser.setNotifyRemovalDay(configGeneral.getNotifyFreq());
			// }
		} finally {
			configGeneralSO = null;
		}
	}

	/**
	 * get YES & NO options //TODO: to load dynamically
	 * 
	 * @return SelectItem[]
	 */
	public static SelectItem[] getYesNoOptions() {
		SelectItem[] items = new SelectItem[3];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[1] = new SelectItem(1, ResourceBundleUtils.getMessage(
				"lbl.txt.yes", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[2] = new SelectItem(0, ResourceBundleUtils.getMessage(
				"lbl.txt.no", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		return items;
	}

	/**
	 * retrieve all languages from pm
	 * 
	 * @param paramType
	 * @return
	 */
	public static List<PmLanguage> retrieveLanguageObjs() {
		log.info("retrieveLanguageObjs");
		List<PmLanguage> languages = new ArrayList<PmLanguage>();
		ILanguageSO languageSO = null;
		try {
			languageSO = (ILanguageSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ILanguageSO.class);
			LanguageResDTO languagecResDTO = languageSO.retrieveLanguages();
			languages = languagecResDTO.getLanguageList();

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770057);
		} finally {
			languageSO = null;
		}
		return languages;
	}

	/**
	 * get YES & NO options //TODO: to load dynamically
	 * 
	 * @return SelectItem[]
	 */
	public static SelectItem[] getYesNoBooleanOptions() {
		SelectItem[] items = new SelectItem[2];
		items[0] = new SelectItem(true, ResourceBundleUtils.getMessage(
				"lbl.txt.yes", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[1] = new SelectItem(false, ResourceBundleUtils.getMessage(
				"lbl.txt.no", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		return items;
	}

	/**
	 * get record status options //TODO: to load dynamically
	 * 
	 * @return SelectItem[]
	 */
	public static SelectItem[] getStatusOptions() {
		SelectItem[] items = new SelectItem[3];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[1] = new SelectItem(DbValueConst.RECORD_STATUS_ACTIVE,
				ResourceBundleUtils.getMessage("lbl.pm.active",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items[2] = new SelectItem(DbValueConst.RECORD_STATUS_DELETED,
				ResourceBundleUtils.getMessage("lbl.pm.inactive",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		return items;
	}

	/**
	 * get record status options //TODO: to load dynamically
	 * 
	 * @return SelectItem[]
	 */
	public static SelectItem[] getCommonStatusOptions() {
		SelectItem[] items = new SelectItem[3];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[1] = new SelectItem(DbValueConst.RECORD_STATUS_ACTIVE,
				ResourceBundleUtils.getMessage("lbl.pm.active",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items[2] = new SelectItem(DbValueConst.RECORD_STATUS_SUSPENDED,
				ResourceBundleUtils.getMessage("lbl.pm.inactive",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		return items;
	}

	/**
	 * retrieve the default PmCountry as per configured
	 * 
	 * @param countryCode
	 * @return PmCountry
	 */
	public static PmCountry retrieveCountryByCountryCode(String countryCode) {
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode(countryCode);

		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);

		return resDTO.getPmCountry();
	}

	/**
	 * retrieve all countries from Pm Country
	 * 
	 * @return List<PmCountry>
	 */
	public static List<PmCountry> retrievePmCountryList() {
		ICountrySO reqSO = null;
		List<PmCountry> resultList = null;
		try {
			reqSO = (ICountrySO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICountrySO.class);
			CountryResDTO resDTO = reqSO.retrieveCountryList();
			resultList = resDTO.getCountryList();
		} finally {
			reqSO = null;
		}
		return resultList;
	}

	/**
	 * retrieve state list from Pm State by country id
	 * 
	 * @param countryId
	 * @return List<PmState>
	 */
	public static List<PmState> retrievePmStateList(Long countryId) {
		IStateSO reqSO = null;
		List<PmState> resultList = null;
		try {
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(countryId);
			reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
			StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
			resultList = resDTO.getStateList();
		} finally {
			reqSO = null;
		}
		return resultList;
	}
	/**
	 * retrieve state list from Pm State by country id
	 * 
	 * @param countryId
	 * @return List<PmState>
	 */
	public static List<PmState> retrievePmStateListForCalendar(Long countryId) {
		IStateSO reqSO = null;
		List<PmState> resultList = null;
		try {
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(countryId);
			reqDTO.setCalendarOnFlag(DbValueConst.IS_CALENDAR_ON);
			reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
			StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
			resultList = resDTO.getStateList();
		} finally {
			reqSO = null;
		}
		return resultList;
	}
	/**
	 * retrieve division list from Pm Division by state id
	 * 
	 * @param stateId
	 * @return List<PmDivision>
	 */
	public static List<PmDivision> retrievePmDivisionList(Long stateId) {
		IDivisionSO reqSO = null;
		List<PmDivision> resultList = null;
		try {
			DivisionReqDTO reqDTO = new DivisionReqDTO();
			reqDTO.setStateId(stateId);
			reqSO = (IDivisionSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDivisionSO.class);
			DivisionResDTO resDTO = reqSO.retriveDivisionListByState(reqDTO);
			resultList = resDTO.getDivisionList();
		} finally {
			reqSO = null;
		}
		return resultList;
	}
	
	/**
	 * retrieve district list from Pm District by division id
	 * 
	 * @param stateId
	 * @return List<PmDistrict>
	 */
	public static List<PmDistrict> retrievePmDistrictListByDivisionId(Long divisionId) {
		IDistrictSO reqSO = null;
		List<PmDistrict> resultList = null;
		try {
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			reqDTO.setDivisionId(divisionId);
			reqSO = (IDistrictSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDistrictSO.class);
			DistrictResDTO resDTO = reqSO.retriveDistrictListByDivision(reqDTO);
			resultList = resDTO.getDistrictList();
		} finally {
			reqSO = null;
		}
		return resultList;
	}

	/**
	 * retrieve district list from Pm District by state id
	 * 
	 * @param stateId
	 * @return List<PmDistrict>
	 */
	public static List<PmDistrict> retrievePmDistrictList(Long stateId) {
		IDistrictSO reqSO = null;
		List<PmDistrict> resultList = null;
		try {
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			reqDTO.setStateId(stateId);
			reqSO = (IDistrictSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDistrictSO.class);
			DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);
			resultList = resDTO.getDistrictList();
		} finally {
			reqSO = null;
		}
		return resultList;
	}

	/**
	 * retrieve city list from Pm City by district id
	 * 
	 * @param districtId
	 * @return List<PmCity>
	 */
	public static List<PmCity> retrievePmCityList(Long districtId) {
		ICitySO reqSO = null;
		List<PmCity> resultList = null;
		try {
			CityReqDTO reqDTO = new CityReqDTO();
			reqDTO.setDistrictId(districtId);
			reqSO = (ICitySO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);
			CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
			resultList = resDTO.getCityList();
		} finally {
			reqSO = null;
		}
		return resultList;
	}

	/**
	 * retrieve state by state id
	 * 
	 * @param stateId
	 * @return PmState
	 */
	public static PmState retrievePmStateByStateId(Long stateId) {
		IStateSO reqSO = null;
		PmState pmState = null;
		try {
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setStateId(stateId);
			reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
			StateResDTO resDTO = reqSO.retriveStateByID(reqDTO);
			pmState = resDTO.getPmState();
		} finally {
			reqSO = null;
		}
		return pmState;
	}

	/**
	 * retrieve all salutation from Pm Salutation
	 * 
	 * @return List<PmSalutation>
	 */
	public static List<PmSalutation> retrievePmSalutationList() {
		ISalutationSO reqSO = null;
		List<PmSalutation> resultList = null;
		try {
			SalutationReqDTO reqDTO = new SalutationReqDTO();
			reqSO = (ISalutationSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ISalutationSO.class);
			ISearchBean<PmSalutation> searchBean = new SearchBean<PmSalutation>();

			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("recordStatusFlg");
			bean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
			searchBean.getDataFilterBeans().add(bean);
			searchBean.getSortColumns().add(
					new SearchOrderBean("salutationName"));

			reqDTO.setSalutationSearchBean(searchBean);
			SalutationResDTO resDTO = reqSO.retrieveSalutationBySearch(reqDTO);
			resultList = resDTO.getSalutationList();
		} finally {
			reqSO = null;
		}
		return resultList;
	}

	/**
	 * retrieve filtered parameter list
	 * 
	 * @param paramType
	 * @param notInParam
	 * @param inParam
	 * @returnList<PmParameterDesc>
	 */
	public static List<PmParameterDesc> retrieveFilteredParamDesc(
			String paramType, List<String> notInParam, List<String> inParam) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		if (notInParam != null && !notInParam.isEmpty()) {
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_NOT_IN.value());
			bean.setSearchField("pmParameter.parameterCode");
			bean.setSearchValue(notInParam);
			searchBean.getDataFilterBeans().add(bean);
		}
		if (inParam != null && !inParam.isEmpty()) {
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
			bean.setSearchField("pmParameter.parameterCode");
			bean.setSearchValue(inParam);
			searchBean.getDataFilterBeans().add(bean);
		}

		return ParameterHelper.getInstance().getParameterDescListBySearchBean(
				paramType, searchBean);
	}

	/**
	 * retrieve pmUser by id
	 * 
	 * @param id
	 * @return PmUser
	 */
	public static PmUser retrieveUserById(Long id, boolean ignoreStatus) {
		PmUser pmUser = new PmUser();
		IUserSO userSO = null;
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = null;
			reqDTO.setUserId(id);
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			if (ignoreStatus) {
				reqDTO.setFetchAddress(true);
				resDTO = userSO
						.retrieveUserByLoginIdOrUserIdIgnoreStatus(reqDTO);
			} else {
				resDTO = userSO.retrieveUserProfileByUserId(reqDTO);
			}

			if (resDTO.isSuccessful()) {
				pmUser = resDTO.getPmUser();
			}
		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770053, new String[] {
					id.toString(), e.getMessage() });
		} finally {
			userSO = null;
		}
		return pmUser;
	}

	/**
	 * retrieve active PmUserOrgs
	 * 
	 * @param id
	 * @param isDetail
	 * @return List<PmUserOrg>
	 */
	public static List<PmUserOrg> retrieveActiveOrg(Long id) {
		List<PmUserOrg> pmUserOrgs = new ArrayList<PmUserOrg>();
		IUserSO userSO = null;
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = null;
			reqDTO.setUserId(id);
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			resDTO = userSO.retrieveUserOrgDtlListByUserId(reqDTO);

			if (resDTO.isSuccessful()) {
				pmUserOrgs = resDTO.getUserOrgList();
			}

		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770058, new String[] {
					id.toString(), e.getMessage() });
		} finally {
			userSO = null;
		}
		return pmUserOrgs;
	}

	/**
	 * retrieve active PmUserOrgs
	 * 
	 * @param id
	 * @param isDetail
	 * @return List<PmUserOrg>
	 */
	public static List<PmUserOrg> retrieveAllUserOrg(Long id) {
		List<PmUserOrg> pmUserOrgs = new ArrayList<PmUserOrg>();
		IUserSO userSO = null;
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = null;
			reqDTO.setUserId(id);
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			resDTO = userSO.retrieveAllUserOrgDtlListByUserId(reqDTO);

			if (resDTO.isSuccessful()) {
				pmUserOrgs = resDTO.getUserOrgList();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770058, new String[] {
					id.toString(), e.getMessage() });
		} finally {
			userSO = null;
		}
		return pmUserOrgs;
	}

	/**
	 * retrieve PmUserOrg
	 * 
	 * @param userId
	 * @param orgId
	 * @return PmUserOrg
	 */
	public static PmUserOrg retrieveUserOrgByUserIdAndOrgId(Long id,
			Long userOrgId) {
		PmUserOrg pmUserOrg = null;
		IUserSO userSO = null;
		try {
			UserOrgReqDTO reqDTO = new UserOrgReqDTO();
			UserOrgResDTO resDTO = null;
			reqDTO.setUserId(id);
			reqDTO.setUserOrgId(userOrgId);
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			resDTO = userSO.retrieveUserOrgDtByUserIdAndOrgId(reqDTO);

			if (resDTO.isSuccessful()) {
				pmUserOrg = resDTO.getPmUserOrg();
			}

		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770058, new String[] {
					id.toString(), e.getMessage() });
		} finally {
			userSO = null;
		}
		return pmUserOrg;
	}

	/**
	 * retrieve active PmUserOrgs without role
	 * 
	 * @param id
	 * @param isFactoring
	 * @return List<PmUserOrg>
	 */
	public static List<PmUserOrg> retrieveActiveOrgWithoutRole(Long id,
			boolean isFactoring) {
		List<PmUserOrg> pmUserOrgs = new ArrayList<PmUserOrg>();
		IUserSO userSO = null;
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = null;
			reqDTO.setUserId(id);
			reqDTO.setFactoringUser(isFactoring);
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

			if (resDTO.isSuccessful()) {
				pmUserOrgs = resDTO.getUserOrgList();
			}

		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770058, new String[] {
					id.toString(), e.getMessage() });
		} finally {
			userSO = null;
		}
		return pmUserOrgs;
	}

	/**
	 * retrieve address type list by orgProfileId
	 * 
	 * @param orgProfileId
	 * @return List<PmAddressType>
	 */
	public static List<PmAddressType> retrieveAddressTypesByOrgId(
			Long orgProfileId, String addressType) {
		List<PmAddressType> pmAddressTypes = new ArrayList<PmAddressType>();
		IAddressSO addressSO = null;
		try {
			AddressReqDTO reqDTO = new AddressReqDTO();
			AddressResDTO resDTO = null;
			if (orgProfileId != null) {
				reqDTO.setOrgProfileId(orgProfileId);
			}
			if (addressType != null) {
				reqDTO.setAddressType(addressType);
			}
			addressSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if (resDTO.isSuccessful()) {
				pmAddressTypes = resDTO.getAddressTypeList();
			}

		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770147,
					new String[] { e.getMessage() });

		} finally {
			addressSO = null;
		}
		return pmAddressTypes;
	}

	/**
	 * retrieve address type list by user id
	 * 
	 * @param userId
	 * @return List<PmAddressType>
	 */
	public static List<PmAddressType> retrieveAddressTypesByUserIdOrAddressType(
			Long userId, String addressType) {
		List<PmAddressType> pmAddressTypes = new ArrayList<PmAddressType>();
		IAddressSO addressSO = null;
		try {
			AddressReqDTO reqDTO = new AddressReqDTO();
			AddressResDTO resDTO = null;
			if (userId != null) {
				reqDTO.setUserId(userId);
			}
			if (addressType != null) {
				reqDTO.setAddressType(addressType);
			}
			addressSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if (resDTO.isSuccessful()) {
				pmAddressTypes = resDTO.getAddressTypeList();
			}

		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770060, new String[] {
					userId.toString(), e.getMessage() }); // TODO

		} finally {
			addressSO = null;
		}
		return pmAddressTypes;
	}

	/**
	 * Get full attachment upload path
	 * 
	 * @param loginId
	 * @param fileName
	 * @return String
	 */
	public static String getUploadPath(String loginId, String fileName) {
		// e.g: base/PM/LoginID/files
		String docRepoBaseDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		StringBuilder filePath = new StringBuilder(docRepoBaseDir);
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(ModuleCodeConst.PROFILE_MANAGEMENT.toUpperCase());
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(loginId);
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(fileName);
		return filePath.toString();
	}

	/**
	 * Get attachment upload path
	 * 
	 * @return String
	 */
	public static String getAttachmentPath(String fullPath) {
		String path = "";
		if (!StringUtils.isBlank(fullPath)) {
			int index = fullPath.lastIndexOf("/");
			path = fullPath.substring(0, index);
		}
		return path;
	}

	public static void commonUploadFile(FileUploadEvent event, String path) {
		File dir = new File(path);
		log.info(" dir.exists()" + dir.exists());

		if (dir.exists()) {
		} else {
			dir.mkdirs();
		}

		String uploadDir = "";

		UploadedFile file = event.getFile();

		uploadDir = path + CommonConstants.DOC_REPO_PATH_SEPARATOR
				+ file.getFileName();

		genFile(uploadDir, file.getContents());

	}

	public static void genFile(String uploadDir, byte[] content) {

		try {

			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
					new FileOutputStream(uploadDir));

			bufferedOutputStream.write(content);

			if (bufferedOutputStream != null) {
				bufferedOutputStream.flush();
				bufferedOutputStream.close();
			}

		} catch (Exception e) {
			log.error(e);
		}

	}

	/**
	 * Retrieve user name
	 * 
	 * @param userId
	 * @return
	 */
	public static String retriveUserName(Long userId) {
		IUserSO userSo = null;
		String userName = "";
		try {
			if (userId != null) {
				userSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserReqDTO reqDTo = new UserReqDTO();
				reqDTo.setUserId(userId);
				UserResDTO resDTO = userSo.retrieveUserByUserId(reqDTo);
				if (resDTO.isSuccessful() == true) {
					userName = resDTO.getPmUser() != null ? resDTO.getPmUser()
							.getUserName() : "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userSo = null;
		}

		return userName;
	}

	public static void deleteUploadFile(String path) {
		File dir = new File(path);
		System.out.println(path);
		log.info(" dir.exists()" + dir.exists());
		if (dir.exists()) {
			dir.delete();
		} else {
			throw new EpException(MessageConst.ERROR_770056);
		}
	}

	/**
	 * Delete attachment status
	 * 
	 * @return boolean
	 */
	public static boolean deleteUserAttachment(
			List<PmUserAttachment> pmUserAttachments) {
		boolean isSuccess = false;
		UserAttachmentReqDTO reqDTO = null;
		IUserAttachmentSO reqSO = null;
		UserAttachmentResDTO resDTO = null;
		try {
			if (pmUserAttachments != null && !pmUserAttachments.isEmpty()) {
				reqDTO = new UserAttachmentReqDTO();
				reqDTO.setPmUserAttachments(pmUserAttachments);
				reqSO = (IUserAttachmentSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IUserAttachmentSO.class);
				resDTO = reqSO.deleteUserAttachment(reqDTO);
			}
			isSuccess = resDTO != null ? resDTO.isSuccessful() : false;
		} finally {
			reqSO = null;
		}
		return isSuccess;
	}

	/**
	 * retrieve parameter obj
	 * 
	 * @param pmParameter
	 */

	public static PmParameter retrievePmParameter(String paramType,
			String paramCode) {
		IParameterSO parameterSO = null;
		PmParameter pmParameter = null;
		try {
			parameterSO = (IParameterSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IParameterSO.class);
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterType(paramType);
			parameterReqDTO.setParameterCode(paramCode);
			ParameterResDTO parameterResDTO = parameterSO
					.retrieveParameterByTypeAndCode(parameterReqDTO);
			pmParameter = parameterResDTO.getPmParameter();

		} finally {
			parameterSO = null;
		}
		return pmParameter;
	}

	/**
	 * Generate current date for LOV date format
	 * 
	 * @param date
	 * @return String
	 */
	public static String getCurrentDateStr(Date date) {
		if (date != null) {
			return new SimpleDateFormat("yyyy-MM-dd").format(date);
		} else {
			return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		}

	}

	/**
	 * retrieve status by status id
	 * 
	 * @param statusId
	 * @return PmStatus
	 */
	public static PmStatus getPmStatusById(Long statusId) {
		PmStatus pmStatus = new PmStatus();
		StatusReqDTO reqDTO = new StatusReqDTO();
		IStatusSO statusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		reqDTO.setStatusId(statusId);
		StatusResDTO resDTO = statusSO.retrieveStatusByStatusId(reqDTO);
		if (resDTO.isSuccessful()) {
			pmStatus = resDTO.getPmStatus();
		}
		return pmStatus;
	}

	/**
	 * Retrieve user name
	 * 
	 * @param userIds
	 * @return List<PmUser>
	 */
	public static List<PmUser> retriveUsersByIds(List<Long> userIds) {
		List<PmUser> pmUsers = new ArrayList<PmUser>();

		try {
			if (userIds != null && !userIds.isEmpty()) {
				ISearchBean<PmUser> searchBean = new SearchBean<PmUser>();

				// active user
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchField("recordStatusFlg");
				bean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
				searchBean.getDataFilterBeans().add(bean);

				bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
				bean.setSearchField("userId");
				bean.setSearchValue(userIds);
				searchBean.getDataFilterBeans().add(bean);

				pmUsers = searchUsersBySearchBean(searchBean);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pmUsers;
	}

	/**
	 * search user by search bean
	 * 
	 * @param userIds
	 * @return List<PmUser>
	 */
	public static List<PmUser> searchUsersBySearchBean(
			ISearchBean<PmUser> searchBean) {
		List<PmUser> pmUsers = new ArrayList<PmUser>();
		IUserSO userSO = null;
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = new UserResDTO();
			reqDTO.setUserSearchBean(searchBean);
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDTO = userSO.searchUserProfile(reqDTO);

			if (resDTO.isSuccessful() && resDTO.getUserResultBean() != null
					&& resDTO.getUserResultBean().getResultList() != null) {
				pmUsers = resDTO.getUserResultBean().getResultList();
			}
		} finally {
			userSO = null;
		}
		return pmUsers;
	}

	/**
	 * retrieve the Ministry by ptj org profile id
	 * 
	 * @param Long
	 *            orgProfileId
	 * @return PmOrgProfile
	 */
	public static PmOrgProfile retrieveMinistryByOrgProfileId(Long orgProfileId) {
		IOrgProfileSO orgProfileSO = null;
		PmOrgProfile ministryProfile = null;
		try {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			OrgProfileResDTO resDTO = new OrgProfileResDTO();
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			reqDTO.setOrgProfileId(orgProfileId);
			resDTO = orgProfileSO.retrieveMinistryByOrgProfileId(reqDTO);
			ministryProfile = resDTO.getPmOrgProfile();
		} finally {
			orgProfileSO = null;
		}
		return ministryProfile;
	}

	/**
	 * retrieve the Pegawai Pengawal by ptj org profile id
	 * 
	 * @param Long
	 *            orgProfileId
	 * @return PmOrgProfile
	 */
	public static PmOrgProfile retrievePegawaiPengawalByOrgProfileId(
			Long orgProfileId) {
		IOrgProfileSO orgProfileSO = null;
		PmOrgProfile jabatanProfile = null;
		try {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			OrgProfileResDTO resDTO = new OrgProfileResDTO();
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			reqDTO.setOrgProfileId(orgProfileId);
			resDTO = orgProfileSO.retrievePegawaiPengawalByOrgProfileId(reqDTO);
			jabatanProfile = resDTO.getPmOrgProfile();
		} finally {
			orgProfileSO = null;
		}
		return jabatanProfile;
	}

	/**
	 * retrieve the Jabatan by ptj org profile id
	 * 
	 * @param Long
	 *            orgProfileId
	 * @return PmOrgProfile
	 */
	public static PmOrgProfile retrieveJabatanByOrgProfileId(Long orgProfileId) {
		IOrgProfileSO orgProfileSO = null;
		PmOrgProfile jabatanProfile = null;
		try {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			OrgProfileResDTO resDTO = new OrgProfileResDTO();
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			reqDTO.setOrgProfileId(orgProfileId);
			resDTO = orgProfileSO.retrieveJabatanByOrgProfileId(reqDTO);
			jabatanProfile = resDTO.getPmOrgProfile();
		} finally {
			orgProfileSO = null;
		}
		return jabatanProfile;
	}

	/**
	 * retrieve challenge question list
	 * 
	 * @param PmUser
	 * @return List<String>
	 */
	public static SelectItem[] retrieveChallengeQAList(PmUser pmUser) {
		List<String> challengeQAList = null;
		IUserSO userSO = null;
		SelectItem[] items = null;
		if (pmUser == null || pmUser.getLoginId() == null) {
			throw new EpException("User login id is invalid.");
		}
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = null;
			reqDTO.setLoginId(pmUser.getLoginId());
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDTO = userSO.getChallengeQAList(reqDTO);
			if (resDTO.isSuccessful()) {
				challengeQAList = resDTO.getOimChallengeQAs();
				items = new SelectItem[challengeQAList.size()];
				for (int i = 0; i < challengeQAList.size(); i++) {
					String value = challengeQAList.get(i);
					items[i] = new SelectItem(value, value);
				}
			}
		} catch (Exception e) {
			throw new EpException("Retrive Challenge QA error");
		} finally {
			userSO = null;
		}
		return items;
	}

	public static PmTrackingDairyVO assignTrackingDairyValues(PmUser pmUser,
			String actionDesc, PmStatus status) {
		PmTrackingDairyVO trckVO = new PmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());
		trckVO.setDocId(pmUser.getUserId());
		trckVO.setDocType(ParameterConst.USER_PROFILE_PARAM_CODE);
		trckVO.setDocNo(pmUser.getUserId() != null ? pmUser.getUserId().toString() : null);
		trckVO.setGroupId(null);
		trckVO.setStatus(status);
		trckVO.setGroupDocType(null);
		return trckVO;
	}

	public static PmTrackingDairyVO assignTrackingDairyForRuleConfig(
			PmConfigRule rule, String actionDesc, PmStatus status) {
		PmTrackingDairyVO trckVO = new PmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());
		trckVO.setDocId(rule.getRuleTypeId());
		trckVO.setDocType(ParameterConst.RULE_CONFIG_PARAM_CODE);
		trckVO.setDocNo(rule.getRuleType());
		trckVO.setGroupId(null);
		trckVO.setStatus(status);
		trckVO.setGroupDocType(null);
		return trckVO;
	}

	public static PmTrackingDairyVO assignTrackingDairyForSignatory(
			PmCertSignatory pmcertSignatory, String actionDesc, PmStatus status) {
		PmTrackingDairyVO trckVO = new PmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());
		trckVO.setDocId(pmcertSignatory.getPmZone().getZoneId());
		trckVO.setDocType(ParameterConst.VIRTUAL_CERTIFICATE_PARAM_CODE);
		trckVO.setDocNo(pmcertSignatory.getPmZone().getZoneId().toString());
		trckVO.setGroupId(null);
		trckVO.setStatus(status);
		trckVO.setGroupDocType(null);
		return trckVO;
	}

	public static PmTrackingDairyVO assignTrackingDairyForFeeExemption(
			PmFeeExemption fee, String actionDesc, PmStatus status) {
		PmTrackingDairyVO trckVO = new PmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());
		trckVO.setDocId(fee.getFeeExemptionId());
		trckVO.setDocType(ParameterConst.FEE_EXEMPTION_PARAM_CODE);
		trckVO.setDocNo(fee.getFeeExemptionId().toString());
		trckVO.setGroupId(null);
		trckVO.setStatus(status);
		trckVO.setGroupDocType(null);
		return trckVO;
	}

	public static PmTrackingDairyVO assignTrackingDairyForUnplannedThreshold(
			PmThreshold threshold, String actionDesc, PmStatus status) {
		PmTrackingDairyVO trckVO = new PmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());
		trckVO.setDocId(Long.valueOf(threshold.getThresholdId()));
		trckVO.setDocType(ParameterConst.UNPLANDED_THRESHOLD_PARAM_CODE);
		trckVO.setDocNo(threshold.getThresholdId().toString());
		trckVO.setGroupId(null);
		trckVO.setStatus(status);
		trckVO.setGroupDocType(null);
		return trckVO;
	}

	public static PmTrackingDairyVO assignTrackingDairyForBudgetThreshold(
			PmThreshold threshold, String actionDesc, PmStatus status) {
		PmTrackingDairyVO trckVO = new PmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());
		trckVO.setDocId(Long.valueOf(threshold.getThresholdId()));
		trckVO.setDocType(ParameterConst.BUDGET_VS_SPENT_THRESHOLD_PARAM_CODE);
		trckVO.setDocNo(threshold.getThresholdId().toString());
		trckVO.setGroupId(null);
		trckVO.setStatus(status);
		trckVO.setGroupDocType(null);
		return trckVO;
	}

	public static PmTrackingDairyVO assignTrackingDairyForPmCategoryL1(
			Long pmCategoryL1Id, String categoryL1Code, PmStatus status,
			String actionDesc, String docType) {

		PmTrackingDairyVO trckVO = new PmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());
		trckVO.setDocId(pmCategoryL1Id);
		trckVO.setDocNo(categoryL1Code);
		trckVO.setDocType(docType);
		trckVO.setStatus(status);
		trckVO.setGroupId(null);
		trckVO.setGroupDocType(null);
		return trckVO;
	}

	public static PmTrackingDairyVO assignTrackingDairyForPmCategoryL2(
			Long pmCategoryL2Id, String pmCategoryL2Code, Long pmCategoryL1Id,
			PmStatus status, String actionDesc, String docType,
			String groupDocType) {

		PmTrackingDairyVO trckVO = new PmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());
		trckVO.setDocId(pmCategoryL2Id);
		trckVO.setDocNo(pmCategoryL2Code);
		trckVO.setDocType(docType);
		trckVO.setStatus(status);
		trckVO.setGroupId(pmCategoryL1Id);
		trckVO.setGroupDocType(groupDocType);
		return trckVO;
	}

	public static PmTrackingDairyVO assignTrackingDairyForPmCategoryL3(
			Long pmCategoryL3Id, Long pmCategoryL2Id, String categoryL3Code,
			PmStatus status, String actionDesc, String docType,
			String groupDocType) {

		PmTrackingDairyVO trckVO = new PmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());
		trckVO.setDocId(pmCategoryL3Id);
		trckVO.setDocNo(categoryL3Code);
		trckVO.setDocType(docType);
		trckVO.setStatus(status);
		trckVO.setGroupId(pmCategoryL2Id);
		trckVO.setGroupDocType(groupDocType);
		return trckVO;
	}

	public static PmTrackingDairyVO assignTrackingDairyForPmBasicCategory(
			Long pmCategoryL3Id, Long pmCategoryL2Id, String categoryL3Code,
			PmStatus status, String actionDesc, String docType,
			String groupDocType) {

		PmTrackingDairyVO trckVO = new PmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());
		trckVO.setDocId(pmCategoryL3Id);
		trckVO.setDocNo(categoryL3Code);
		trckVO.setDocType(docType);
		trckVO.setStatus(status);
		trckVO.setGroupId(pmCategoryL2Id);
		trckVO.setGroupDocType(groupDocType);
		return trckVO;
	}

	public static PmTrackingDairyVO assignTrackingDairyForEducation(
			Long eduFiledId, String eduFieldCode, PmStatus status,
			String actionDesc) {

		PmTrackingDairyVO trckVO = new PmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());
		trckVO.setDocId(eduFiledId);
		trckVO.setDocNo(eduFieldCode);
		trckVO.setDocType(ParameterConst.EDU_PARAM_CODE);
		trckVO.setStatus(status);
		trckVO.setGroupId(null);
		trckVO.setGroupDocType(null);
		return trckVO;
	}

	public static PmTrackingDairyVO assignTrackingDairyForUOM(Long eduFiledId,
			String eduFieldCode, PmStatus status, String actionDesc) {

		PmTrackingDairyVO trckVO = new PmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());
		trckVO.setDocId(eduFiledId);
		trckVO.setDocNo(eduFieldCode);
		trckVO.setDocType(ParameterConst.UOM_PARAM_CODE);
		trckVO.setStatus(status);
		trckVO.setGroupId(null);
		trckVO.setGroupDocType(null);
		return trckVO;
	}

	public static PmTrackingDairyVO assignTrackingDairyForProfessional(
			Long certBodyId, String certBodyCode, PmStatus status,
			String actionDesc) {

		PmTrackingDairyVO trckVO = new PmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());
		trckVO.setDocId(certBodyId);
		trckVO.setDocNo(certBodyCode);
		trckVO.setDocType(ParameterConst.PROFESSIONAL_CERT_BODY);
		trckVO.setStatus(status);
		trckVO.setGroupId(null);
		trckVO.setGroupDocType(null);
		return trckVO;
	}
	
	public static PmTrackingDairyVO assignTrackingDairyForSupportingDocument(Long docId,String docCode,PmStatus status,String actionDesc) {

		PmTrackingDairyVO trckVO = new PmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());
		trckVO.setDocId(docId);
		trckVO.setDocNo(docCode);
		trckVO.setDocType(ParameterConst.SUPPORTING_DOC_QUOTATION_TENDER);
		trckVO.setStatus(status);
		trckVO.setGroupId(null);
		trckVO.setGroupDocType(null);
		return trckVO;
	}
	public static PmTrackingDairyVO assignTrackingDairyForDesignatedArea(
			PmDesignatedArea da, String actionDesc, PmStatus status) {
		PmTrackingDairyVO trckVO = new PmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());
		trckVO.setDocId(da.getDesignatedAreaId());
		trckVO.setDocType(ParameterConst.GST_PARAM_CODE);
		trckVO.setDocNo(da.getDesignatedAreaId() != null ? da.getDesignatedAreaId().toString() : null);
		trckVO.setGroupId(null);
		trckVO.setStatus(status);
		trckVO.setGroupDocType(null);
		return trckVO;
	}
	public static String getUserRoleValue() {
		FacesScopes sc = new FacesScopes();
		if (sc.hasRoleUser(RoleCode.EP_ADMIN.value())) {
			return RoleCode.EP_ADMIN.value();
		} else if (sc.hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
			return RoleCode.MINISTRY_ADMIN.value();
		} else if (sc.hasRoleUser(RoleCode.PTJ_ADMIN.value())) {
			return RoleCode.PTJ_ADMIN.value();
		} else if (sc.hasRoleUser(RoleCode.JABATAN_ADMIN.value())) {
			return RoleCode.JABATAN_ADMIN.value();
		} else if (sc.hasRoleUser(RoleCode.EP_COMMITTEE.value())) {
			return RoleCode.EP_COMMITTEE.value();
		} else if (sc.hasRoleUser(RoleCode.BPK_OFFICER.value())) {
			return RoleCode.BPK_OFFICER.value();
		} else if (sc.hasRoleUser(RoleCode.UPS_ADMIN.value())) {
			return RoleCode.UPS_ADMIN.value();
		} else if (sc.hasRoleUser(RoleCode.FACTORING_ADMIN.value())) {
			return RoleCode.FACTORING_ADMIN.value();
		} else if (sc.hasRoleUser(RoleCode.FPB_CHAIRPERSON.value())) {
			return RoleCode.FPB_CHAIRPERSON.value();
		}
		return null;
	}
	
	public static PmDistrict retrievePmDistById(Long distId) {
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setDistrictId(distId);
		DistrictResDTO resDTO = reqSO.retriveDistrictByID(reqDTO);

		return resDTO.getPmDistrict();
	}
	
	public static PmCity retrievePmCityById(Long cityId) {
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICitySO.class);
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setCityId(cityId);
		CityResDTO resDTO = reqSO.retriveCityById(reqDTO);

		return resDTO.getPmCity();
	}
	
	public static PmDivision retrievePmDivisionById(Long divisionId) {
		IDivisionSO reqSO = (IDivisionSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDivisionSO.class);
		DivisionReqDTO reqDTO = new DivisionReqDTO();
		reqDTO.setDivisionId(divisionId);
		DivisionResDTO resDTO = reqSO.retrieveDivisionById(reqDTO);

		return resDTO.getPmDivision();
	}
	
	// docuRepo/dev/ngep/2016/PM/AccAppl/loginId/timemilis/fileName
	public static String getUploadFilePath(String path,String loginId, String fileName) {
		String docRepoBaseDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		StringBuilder filePath = new StringBuilder(docRepoBaseDir);
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(Calendar.getInstance().get(Calendar.YEAR));
		filePath.append(path);
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(loginId);
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(System.currentTimeMillis());
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(fileName);
		return filePath.toString();
	}
	
	/**
	 * Hash string.
	 * 
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String hashString(String str) {

		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException ex) {
			log.info(ex.getMessage());
			// return;
		}
		md.update(str.getBytes());
		byte[] shaDig = md.digest();
		return new String(Hex.encodeHex(shaDig));
	}
	
	/**
	 * Redirect to page with message as optional.
	 * 
	 * @param page
	 *            - String
	 * @param msgKeyId
	 *            - String
	 */
	public static void redirectToPage(String page, String msgKeyId,
			String[] params) {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (StringUtils.isNotBlank(msgKeyId)) {
			if (params != null && params.length > 0) {
				fc.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(msgKeyId, params,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			} else {
				fc.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(msgKeyId,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
		}
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, page);
	}
	
	/**
	 * retrieve pmUser by loginId
	 * 
	 * @param loginId
	 * @return PmUser
	 */
	public static PmUser retrieveUserByLoginId(String loginId) {
		PmUser pmUser = new PmUser();
		IUserSO userSO = null;
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = null;
			reqDTO.setLoginId(loginId);
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			
			resDTO = userSO
					.retrieveUserByLoginIdOrUserIdIgnoreStatus(reqDTO);
			

			if (resDTO.isSuccessful()) {
				pmUser = resDTO.getPmUser();
			}
		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770053, new String[] {
					loginId.toString(), e.getMessage() });
		} finally {
			userSO = null;
		}
		return pmUser;
	}
	
	/**
	 * Get Liferay Portal Base URL
	 * 
	 * @return String
	 */
	public static String getBasePortalUrl() {
		FacesContext fCtx = FacesContext.getCurrentInstance();
		PortletRequest req = (PortletRequest) fCtx.getExternalContext()
				.getRequest();
		ThemeDisplay themeDisplay = (ThemeDisplay) req
				.getAttribute(WebKeys.THEME_DISPLAY);
		return themeDisplay.getURLPortal();
	}
	
	
	
	/**
	 * retrieve pmUser by idNo
	 * 
	 * @param idNo
	 * @return PmUser
	 */
	public static PmUser retrieveUserByIdNo(String idNo) {
		PmUser pmUser = new PmUser();
		IUserSO userSO = null;
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = null;
			reqDTO.setIdentificationNo(idNo);
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			
			resDTO = userSO
					.retriveUserProfileByIdNo(reqDTO);
			

			if (resDTO.isSuccessful()) {
				pmUser = resDTO.getPmUser();
			}
		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770053, new String[] {
					idNo.toString(), e.getMessage() });
		} finally {
			userSO = null;
		}
		return pmUser;
	}
	
}
