package my.ep.web.util.sm.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.biz.service.pm.common.businessnature.IBusinessNatureSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.revision.ISmApplRevisionSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.businessnature.BusinessNatureReqDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureResDTO;
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
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.mofRegistration.SmTrackingDairyVO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.persistence.logging.LogKey;
import my.ep.type.sm.ApplType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.constant.PagingConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.vo.SmPersonnelSummaryCountVO;
import my.ep.web.vo.SmPersonnleCalcVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.id.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.context.RequestContext;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.theme.ThemeDisplay;

// TODO: Auto-generated Javadoc
public class SmCommonUtil {
	private static Logger log = Logger.getLogger(SmCommonUtil.class);

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
	 * Md5.
	 * 
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String md5(String str) {

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			log.info(ex.getMessage());
			// return;
		}
		md.update(str.getBytes());
		byte[] md5Dig = md.digest();

		return new String(Hex.encodeHex(md5Dig));
	}

	/**
	 * Retrieve parameter description list by param type and language code.
	 * 
	 * @param paramType
	 *            -String param type
	 * @param lang
	 *            -String lang
	 * @return List<PmParameterDesc>
	 */
	public static List<PmParameterDesc> retrieveParameterDescList(
			String paramType, String lang) {
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterType(paramType);
		reqDTO.setLanguageCode(lang);
		IParameterSO reqSO = (IParameterSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IParameterSO.class);
		ParameterResDTO resDTO = reqSO.retrieveParameterDescListByType(reqDTO);

		return resDTO.getParameterDescList();
	}

	/**
	 * Retrieve country by country code.
	 * 
	 * @param countryCode
	 *            -String country code
	 * @return PmCountry object
	 */
	public static PmCountry retrieveCountryByCountryCode(String countryCode) {
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode(countryCode);

		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);

		return resDTO.getPmCountry();
	}

	/**
	 * Retrieve pm status list based in module code,document type, language.
	 * 
	 * @param moduleCode
	 *            - String moduleCode
	 * @param docType
	 *            -String docType
	 * @param lang
	 *            -String lang
	 * @return List<PmStatusDesc>
	 */
	public static List<PmStatusDesc> retrievePmStatusList(String moduleCode,
			String docType, String lang) {
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setDocType(docType);
		reqDTO.setModuleCode(moduleCode);
		reqDTO.setLanguageCode(lang);
		IStatusSO reqSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		StatusResDTO resDTO = reqSO.retrieveStatusDescList(reqDTO);

		return resDTO.getStatusDescList();
	}

	/**
	 * Convert pm status list to map.
	 * 
	 * @param pmStatusDescLs
	 *            - List<PmStatusDesc>
	 * @return Map<Long, PmStatusDesc>
	 */
	public static Map<Long, PmStatusDesc> converPmStatusListToMap(
			List<PmStatusDesc> pmStatusDescLs) {
		Map<Long, PmStatusDesc> statDescMap = new HashMap<Long, PmStatusDesc>();
		for (PmStatusDesc pmStatDesc : pmStatusDescLs) {
			statDescMap.put(pmStatDesc.getPmStatus().getStatusId(), pmStatDesc);
		}
		return statDescMap;
	}

	/**
	 * Retrieve parameter by parameter type ,parameter code and language.
	 * 
	 * @param paramType
	 *            -String param type
	 * @param paramCode
	 *            -String param code
	 * @param lang
	 *            -String lang
	 * @return PmParameter object
	 */
	public static PmParameter retrieveParameterByTypeAndCode(String paramType,
			String paramCode, String lang) {
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterType(paramType);
		reqDTO.setParameterCode(paramCode);
		reqDTO.setLanguageCode(lang);
		IParameterSO reqSO = (IParameterSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IParameterSO.class);
		ParameterResDTO resDTO = reqSO.retrieveParameterByTypeAndCode(reqDTO);

		return resDTO.getPmParameter();
	}

	/**
	 * Gets the parameter description by language and pm parameter.
	 * 
	 * @param pmParameter
	 *            - PmParameter object
	 * @return the param description string by language and pm parameter
	 */
	public static String getParamDescriptionByLanguageAndPmParameter(
			PmParameter pmParameter) {
		String desc = "";
		if (pmParameter != null) {
			List<PmParameterDesc> parameterDescs = ParameterHelper
					.getInstance().getParameterDescList(
							pmParameter.getParameterType());
			if (parameterDescs != null) {
				for (PmParameterDesc paramDesc : parameterDescs) {
					if (paramDesc.getPmParameter() != null
							&& paramDesc.getPmParameter().getParameterId()
									.compareTo(pmParameter.getParameterId()) == 0) {
						desc = paramDesc.getCodeName();
						break;
					}
				}
			}
		}
		return desc;

	}

	/**
	 * Retrieve business nature by id.
	 * 
	 * @param bizId
	 *            - Long biz id
	 * @return PmBusinessNature object
	 */
	public static PmBusinessNature retrieveBusinessNatureById(Long bizId) {
		BusinessNatureReqDTO reqDTO = new BusinessNatureReqDTO();
		reqDTO.setBusinessNatureId(bizId);

		IBusinessNatureSO reqSO = (IBusinessNatureSO) BusinessDelegate
				.getInstance().getServiceObject("pm", IBusinessNatureSO.class);
		BusinessNatureResDTO resDTO = reqSO
				.retrieveBusinessNatureDtlById(reqDTO);

		return resDTO.getBusinessNature();
	}

	/**
	 * Gets the application type description.
	 * 
	 * @param applType
	 *            - String applType
	 * @return the application type description
	 */
	public static String getApplTypeDesc(String applType) {
		log.info("--------getApplTypeDesc start--------" + applType);
		String apType = "";

		switch (applType) {
		case "O":
			apType = ApplType.O.value();
			break;

		case "N":
			apType = ApplType.N.value();
			break;

		case "B":
			apType = ApplType.B.value();
			break;

		case "A":
			apType = ApplType.A.value();
			break;

		case "U":
			apType = ApplType.U.value();
			break;

		case "R":
			apType = ApplType.R.value();
			break;

		case "C":
			apType = ApplType.C.value();
			break;
		}

		log.info("--------getApplTypeDesc--------" + apType);

		return apType;
	}

	/**
	 * Calculate sum of personnel list count in the supplier .
	 * 
	 * @param personnelList
	 *            - List<SmPersonnel> p
	 * @param stfTotals
	 *            - List<SmStaffTotal>
	 * @param supplierType
	 *            - String supplierType
	 * @return SmPersonnleCalcVO object setting all the counts
	 */
	public static SmPersonnleCalcVO calcPersonnelListCount(
			List<SmPersonnel> personnelList, List<SmStaffTotal> stfTotals,
			String supplierType) {
		log.info("********* Staff calculation from Util class ********* ");
		SmPersonnleCalcVO personVO = new SmPersonnleCalcVO();
		personVO = resetAllPersonnelCount(stfTotals, personVO);
		// setValidSmUserLimit(validatePersonLimit());
		Locale locale = new FacesScopes().getUserLocale();
		for (SmPersonnel per : personnelList) {
			personVO = calcSumUpByPersonnel(per, personVO, supplierType, locale);
		}
		calcuteStaffSubTotalSummary(personVO);
		log.info("********* END calcPersonnelListCount ********* ");
		return personVO;
	}

	/**
	 * Retrieve PmCountry by id.
	 * 
	 * @param countryId
	 *            - Long country id
	 * @return PmCountry object
	 */
	private static PmCountry retrievePmCountryById(Long countryId) {
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICountrySO.class);
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryId(countryId);
		CountryResDTO resDTO = reqSO.retrieveCountryById(reqDTO);

		return resDTO.getPmCountry();
	}

	/**
	 * Calculate sum up by personnel.Update the SmPersonnleCalcVO
	 * information/counts into relevant variable like
	 * :equiOwnrNonBumiCnt,equiOwnrBumiCnt..etc, based on the personnel passed.
	 * 
	 * @param per
	 *            - SmPersonnel per
	 * @param persCalcVo
	 *            SmPersonnleCalcVO persCalcVo
	 * @param supplierType
	 *            - String supplierType
	 * @param locale
	 *            - Locale locale
	 * @return SmPersonnleCalcVO object
	 */
	private static SmPersonnleCalcVO calcSumUpByPersonnel(SmPersonnel per,
			SmPersonnleCalcVO persCalcVo, String supplierType, Locale locale) {
		per.setRoleName("");
		if (per.getPmCountry() != null
				&& per.getPmCountry().getCountryId() != null) {
			if (per.getPmCountry().getCountryCode() == null
					|| (per.getPmCountry().getCountryCode() != null && per
							.getPmCountry().getCountryCode()
							.equalsIgnoreCase(""))) {
				per.setPmCountry(retrievePmCountryById(per.getPmCountry()
						.getCountryId()));
			}
		}

		StringBuffer sb = new StringBuffer();
		if (per.isEquityOwner()) {
			sb.append(
					ResourceBundleUtils.getMessage(
							SupplierManagementConst.PERSONNEL_EQUI_OWNER,
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale))
					.append(",");
			setPersonnelCountSummary(per, persCalcVo.getEquiOwnrBumiCnt(),
					persCalcVo.getEquiOwnrNonBumiCnt(),
					persCalcVo.getEquiOwnrFrgCnt(), persCalcVo);
			persCalcVo.setEquiOwnrBumiCnt(persCalcVo.getSummaryCountVO()
					.getBumiCount());
			persCalcVo.setEquiOwnrNonBumiCnt(persCalcVo.getSummaryCountVO()
					.getNonBumiCount());
			persCalcVo.setEquiOwnrFrgCnt(persCalcVo.getSummaryCountVO()
					.getFrgCount());
		}
		if (per.isDirector()) {
			// setDirectorCount(per);
			sb.append(
					ResourceBundleUtils.getMessage(
							SupplierManagementConst.PERSONNEL_DIR_ROLENAME,
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale))
					.append(",");
			setPersonnelCountSummary(per, persCalcVo.getBodBumiCnt(),
					persCalcVo.getBodNonBumiCnt(), persCalcVo.getBodFrgCnt(),
					persCalcVo);
			persCalcVo.setBodBumiCnt(persCalcVo.getSummaryCountVO()
					.getBumiCount());
			persCalcVo.setBodNonBumiCnt(persCalcVo.getSummaryCountVO()
					.getNonBumiCount());
			persCalcVo.setBodFrgCnt(persCalcVo.getSummaryCountVO()
					.getFrgCount());
		}
		if (per.isHigherManagement()) {
			// setHigherMngmtCount(per);
			sb.append(
					ResourceBundleUtils.getMessage(
							SupplierManagementConst.PERSONNEL_HGHR_ROLENAME,
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale))
					.append(",");
			setPersonnelCountSummary(per, persCalcVo.getHghMngBumiCnt(),
					persCalcVo.getHghMngNonBumiCnt(),
					persCalcVo.getHghMngFrgCnt(), persCalcVo);
			persCalcVo.setHghMngBumiCnt(persCalcVo.getSummaryCountVO()
					.getBumiCount());
			persCalcVo.setHghMngNonBumiCnt(persCalcVo.getSummaryCountVO()
					.getNonBumiCount());
			persCalcVo.setHghMngFrgCnt(persCalcVo.getSummaryCountVO()
					.getFrgCount());
		}
		if (per.isClerical()) {
			// staff
			// setStaffEnteredCount(per);
			if (supplierType
					.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
				sb.append(
						ResourceBundleUtils
								.getMessage(
										SupplierManagementConst.PERSONNEL_CLERICAL_STAFF_ROLENAME,
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										locale)).append(",");
			} else {
				sb.append(
						ResourceBundleUtils
								.getMessage(
										SupplierManagementConst.PERSONNEL_STAFF_ROLENAME,
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										locale)).append(",");
			}

			setPersonnelCountSummary(per, persCalcVo.getStfBumiEnterCnt(),
					persCalcVo.getStfNonBumiEnterCnt(),
					persCalcVo.getStfFrgEnterCnt(), persCalcVo);
			persCalcVo.setStfBumiEnterCnt(persCalcVo.getSummaryCountVO()
					.getBumiCount());
			persCalcVo.setStfNonBumiEnterCnt(persCalcVo.getSummaryCountVO()
					.getNonBumiCount());
			persCalcVo.setStfFrgEnterCnt(persCalcVo.getSummaryCountVO()
					.getFrgCount());
		}
		if (per.isManagement()) {
			// management
			// setManagementCount(per);
			sb.append(
					ResourceBundleUtils
							.getMessage(
									SupplierManagementConst.PERSONNEL_MANAGEMENT_ROLENAME,
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									locale)).append(",");
			setPersonnelCountSummary(per, persCalcVo.getMngtBumiCnt(),
					persCalcVo.getMngtNonBumiCnt(), persCalcVo.getMngtFrgCnt(),
					persCalcVo);
			persCalcVo.setMngtBumiCnt(persCalcVo.getSummaryCountVO()
					.getBumiCount());
			persCalcVo.setMngtNonBumiCnt(persCalcVo.getSummaryCountVO()
					.getNonBumiCount());
			persCalcVo.setMngtFrgCnt(persCalcVo.getSummaryCountVO()
					.getFrgCount());
		}
		if (supplierType
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
			if (per.isProf()) {
				// prof
				// setProfEnteredCount(per);
				sb.append(
						ResourceBundleUtils
								.getMessage(
										SupplierManagementConst.PERSONNEL_PROF_STAFF_ROLENAME,
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										locale)).append(",");
				setPersonnelCountSummary(per, persCalcVo.getProfBumiEnterCnt(),
						persCalcVo.getProfNonBumiEnterCnt(),
						persCalcVo.getProfFrgEnterCnt(), persCalcVo);
				persCalcVo.setProfBumiEnterCnt(persCalcVo.getSummaryCountVO()
						.getBumiCount());
				persCalcVo.setProfNonBumiEnterCnt(persCalcVo
						.getSummaryCountVO().getNonBumiCount());
				persCalcVo.setProfFrgEnterCnt(persCalcVo.getSummaryCountVO()
						.getFrgCount());

			}
			if (per.isSemiProf()) {
				// semi
				// setSemiProfEnteredCount(per);
				sb.append(
						ResourceBundleUtils
								.getMessage(
										SupplierManagementConst.PERSONNEL_SEMIPROF_STAFF_ROLENAME,
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										locale)).append(",");
				setPersonnelCountSummary(per, persCalcVo.getSemiBumiEnterCnt(),
						persCalcVo.getSemiNonBumiEnterCnt(),
						persCalcVo.getSemiFrgEnterCnt(), persCalcVo);
				persCalcVo.setSemiBumiEnterCnt(persCalcVo.getSummaryCountVO()
						.getBumiCount());
				persCalcVo.setSemiNonBumiEnterCnt(persCalcVo
						.getSummaryCountVO().getNonBumiCount());
				persCalcVo.setSemiFrgEnterCnt(persCalcVo.getSummaryCountVO()
						.getFrgCount());
			}
		}
		if (!sb.toString().isEmpty() && sb.toString().length() > 0) {
			per.setRoleName(sb.toString().substring(0,
					sb.toString().length() - 1));
		}
		sb = null;
		return persCalcVo;

	}

	/**
	 * Calcute staff sub total summary.
	 * 
	 * @param persCalcVo
	 *            -SmPersonnleCalcVO
	 */
	private static void calcuteStaffSubTotalSummary(SmPersonnleCalcVO persCalcVo) {
		persCalcVo.setEquityOwnerTotalCnt(persCalcVo.getEquiOwnrBumiCnt()
				+ persCalcVo.getEquiOwnrNonBumiCnt()
				+ persCalcVo.getEquiOwnrFrgCnt());
		persCalcVo.setBodTotalCnt(persCalcVo.getBodBumiCnt()
				+ persCalcVo.getBodNonBumiCnt() + persCalcVo.getBodFrgCnt());
		persCalcVo.setHghrMngmtTotalCnt(persCalcVo.getHghMngBumiCnt()
				+ persCalcVo.getHghMngNonBumiCnt()
				+ persCalcVo.getHghMngFrgCnt());
		persCalcVo.setMngmtTotalCnt(persCalcVo.getMngtBumiCnt()
				+ persCalcVo.getMngtNonBumiCnt() + persCalcVo.getMngtFrgCnt());
		persCalcVo.setStfTotalCnt(persCalcVo.getStfBumiEnterCnt()
				+ persCalcVo.getStfNonBumiEnterCnt()
				+ persCalcVo.getStfFrgEnterCnt());
		persCalcVo.setSemiTotalCnt(persCalcVo.getSemiBumiEnterCnt()
				+ persCalcVo.getSemiNonBumiEnterCnt()
				+ persCalcVo.getSemiFrgEnterCnt());
		persCalcVo.setProfTotalCnt(persCalcVo.getProfBumiEnterCnt()
				+ persCalcVo.getProfNonBumiEnterCnt()
				+ persCalcVo.getProfFrgEnterCnt());
	}

	/**
	 * Sets the personnel count summary.
	 * 
	 * @param per
	 *            the per
	 * @param bumiCount
	 *            the bumi count
	 * @param nonBumCount
	 *            the non bum count
	 * @param frgCount
	 *            the frg count
	 * @param persCalcVo
	 *            the pers calc vo
	 */
	private static void setPersonnelCountSummary(SmPersonnel per,
			int bumiCount, int nonBumCount, int frgCount,
			SmPersonnleCalcVO persCalcVo) {
		persCalcVo.setSummaryCountVO(new SmPersonnelSummaryCountVO());

		if (per.getPmCountry() != null
				&& per.getPmCountry().getCountryCode() != null
				&& per.getPmCountry().getCountryCode()
						.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)) {
			if (per.getBumiFlg() == 1) {
				bumiCount = bumiCount + 1;
			} else {
				nonBumCount = nonBumCount + 1;
			}
		} else {
			frgCount = frgCount + 1;
		}
		persCalcVo.getSummaryCountVO().setBumiCount(bumiCount);
		persCalcVo.getSummaryCountVO().setNonBumiCount(nonBumCount);
		persCalcVo.getSummaryCountVO().setFrgCount(frgCount);
	}

	/**
	 * Reset all personnel counts to zero.
	 * 
	 * @param stfTotalList
	 *            - List<SmStaffTotal>
	 * @param persCalcVo
	 *            - SmPersonnleCalcVO
	 * @return SmPersonnleCalcVO object
	 */
	public static SmPersonnleCalcVO resetAllPersonnelCount(
			List<SmStaffTotal> stfTotalList, SmPersonnleCalcVO persCalcVo) {
		return assignEnteredStaffValues(stfTotalList, persCalcVo);
	}

	/**
	 * Assign entered staff values or counts to the SmPersonnleCalcVO.
	 * 
	 * @param stfTotalList
	 *            - List<SmStaffTotal>
	 * @param persCalcVo
	 *            - SmPersonnleCalcVO
	 * @return SmPersonnleCalcVO object
	 */
	public static SmPersonnleCalcVO assignEnteredStaffValues(
			List<SmStaffTotal> stfTotalList, SmPersonnleCalcVO persCalcVo) {
		for (SmStaffTotal stf : stfTotalList) {
			if (stf.getStaffCategory().equalsIgnoreCase(
					SupplierManagementConst.STAFF_TYPE_CONTRACTOR)
					|| stf.getStaffCategory().equalsIgnoreCase(
							SupplierManagementConst.STAFF_TYPE_CLERICAL)) {
				persCalcVo.setStfBumiActCnt(stf.getTotalBumi().intValue());
				persCalcVo.setStfFrgActCnt(stf.getTotalForeigner().intValue());
				persCalcVo.setStfNonBumiActCnt(stf.getTotalNonBumi()
						.intValueExact());

				persCalcVo.setStfActualTotalCnt(persCalcVo.getStfBumiActCnt()
						+ persCalcVo.getStfFrgActCnt()
						+ persCalcVo.getStfNonBumiActCnt());
			} else if (stf.getStaffCategory().equalsIgnoreCase(
					SupplierManagementConst.STAFF_TYPE_PROFESSIONAL)) {
				persCalcVo.setProfBumiActCnt(stf.getTotalBumi().intValue());
				persCalcVo.setProfFrgActCnt(stf.getTotalForeigner().intValue());
				persCalcVo.setProfNonBumiActCnt(stf.getTotalNonBumi()
						.intValue());

				persCalcVo.setProfActualTotalCnt(persCalcVo.getProfBumiActCnt()
						+ persCalcVo.getProfNonBumiActCnt()
						+ persCalcVo.getProfFrgActCnt());
			} else if (stf.getStaffCategory().equalsIgnoreCase(
					SupplierManagementConst.STAFF_TYPE_SEMIPROF)) {
				persCalcVo.setSemiBumiActCnt(stf.getTotalBumi().intValue());
				persCalcVo.setSemiFrgActCnt(stf.getTotalForeigner().intValue());
				persCalcVo.setSemiNonBumiActCnt(stf.getTotalNonBumi()
						.intValue());

				persCalcVo.setSemiActualTotalCnt(persCalcVo.getSemiBumiActCnt()
						+ persCalcVo.getSemiFrgActCnt()
						+ persCalcVo.getSemiNonBumiActCnt());
			}
		}
		return persCalcVo;
	}

	/**
	 * Calculate bumi percentage totals for the personnels.
	 * 
	 * @param persCalcVo
	 *            - SmPersonnleCalcVO
	 * @param personnelList
	 *            - List<SmPersonnel>
	 * @return SmPersonnleCalcVO object
	 */
	public static SmPersonnleCalcVO calculateBumiPercentageTotals(
			SmPersonnleCalcVO persCalcVo, List<SmPersonnel> personnelList) {
		calcuteStaffSubTotalSummary(persCalcVo);
		persCalcVo.setTotalStaffEntered(persCalcVo.getBodTotalCnt()
				+ persCalcVo.getHghrMngmtTotalCnt()
				+ persCalcVo.getMngmtTotalCnt() + persCalcVo.getStfTotalCnt()
				+ persCalcVo.getSemiTotalCnt() + persCalcVo.getProfTotalCnt());
		persCalcVo.setTotalKeyRolesEntered(persCalcVo.getBodTotalCnt()
				+ persCalcVo.getHghrMngmtTotalCnt()
				+ persCalcVo.getMngmtTotalCnt());

		int stfBumiTotal = persCalcVo.getBodBumiCnt()
				+ persCalcVo.getHghMngBumiCnt() + persCalcVo.getMngtBumiCnt()
				+ persCalcVo.getStfBumiEnterCnt()
				+ persCalcVo.getSemiBumiEnterCnt()
				+ persCalcVo.getProfBumiEnterCnt();

		BigDecimal mul = new BigDecimal(100);
		BigDecimal halfBumi = new BigDecimal(51);
		if (persCalcVo.getTotalStaffEntered() > 0) {
			BigDecimal stffBumiPer = new BigDecimal(stfBumiTotal
					/ persCalcVo.getTotalStaffEntered()).multiply(mul)
					.setScale(2, RoundingMode.HALF_UP);
			persCalcVo.setTotalStaffBumiPerc(stffBumiPer);
		}
		if (persCalcVo.getTotalStaffBumiPerc().compareTo(mul) == 0) {
			persCalcVo.setFullBumiStatus(true);
		} else if (persCalcVo.getTotalStaffBumiPerc().compareTo(halfBumi) >= 0
				&& persCalcVo.getTotalStaffBumiPerc().compareTo(mul) < 0) {
			int keyRoleBumiTotal = persCalcVo.getBodBumiCnt()
					+ persCalcVo.getHghMngBumiCnt()
					+ persCalcVo.getMngtBumiCnt();
			if (persCalcVo.getTotalKeyRolesEntered() > 0
					&& persCalcVo.getTotalStaffBumiPerc().compareTo(mul) != 0) {
				BigDecimal keyRoleBumiPer = new BigDecimal(keyRoleBumiTotal
						/ persCalcVo.getTotalKeyRolesEntered()).multiply(mul)
						.setScale(2, RoundingMode.HALF_UP);
				persCalcVo.setTotalKeyRoleBumiPerc(keyRoleBumiPer);
				if (persCalcVo.getTotalKeyRoleBumiPerc().compareTo(halfBumi) >= 0
						&& persCalcVo.getTotalKeyRoleBumiPerc().compareTo(mul) < 0) {
					for (SmPersonnel per : personnelList) {
						if (per.isEquityOwner() && per.getBumiFlg() == 1) {
							persCalcVo.setTotalShrBumiShrHldrPerc(persCalcVo
									.getTotalShrBumiShrHldrPerc().add(
											per.getSharePercentage()));
						}
					}
					if (persCalcVo.getTotalShrBumiShrHldrPerc().compareTo(
							halfBumi) >= 0
							&& persCalcVo.getTotalShrBumiShrHldrPerc()
									.compareTo(mul) < 0) {
						persCalcVo.setHalfBumiStatus(true);
					}

				}
			}
		}
		return persCalcVo;
	}

	/**
	 * Display bumi recommend note.
	 * 
	 * @param smSupplierBankList
	 *            - List<SmSupplierBank>
	 * @param persCalcVo
	 *            - SmPersonnleCalcVO
	 * @param outputPage
	 *            - String output page
	 * @param applType
	 *            - String application type
	 * @param supplType
	 *            - String Supplier type
	 * @param bumiPercentage
	 *            - BigDecimal
	 * @param supplUser
	 *            - boolean true or false
	 * @return String - bumi recommendation note
	 */
	public static String displayBumiRecommendNote(
			List<SmSupplierBank> smSupplierBankList,
			SmPersonnleCalcVO persCalcVo, String outputPage, String applType,
			String supplType, BigDecimal bumiPercentage, boolean supplUser) {

		if (supplType.equals("K")
				&& (applType
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF) || applType
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI))) {
			if (persCalcVo == null) {
				persCalcVo = new SmPersonnleCalcVO();
			}
			persCalcVo.setBumiRecommendNote("");
			setBumiStatus(persCalcVo, bumiPercentage);
			setAllBankSignatoriesNonBumi(persCalcVo, smSupplierBankList);

			if (persCalcVo.isBumiStatus()) {
				if (StringUtils.isEmpty(persCalcVo.getNonBumiSignatoryBank())) {
					if (supplUser) {
						persCalcVo.setBumiRecommendNote(getMessage(
								"lbl.sm.recommend.supplier.bumi", null));
					} else {
						persCalcVo.setBumiRecommendNote(getMessage(
								"lbl.sm.recommend.poapprover.bumi", null));
					}
				} else {
					if (supplUser) {
						persCalcVo
								.setBumiRecommendNote(getMessage(
										"lbl.sm.recommend.supplier.sign.nonbumi",
										null));
					} else {
						String[] params = { "" };
						params[0] = persCalcVo.getNonBumiSignatoryBank();
						persCalcVo.setBumiRecommendNote(getMessage(
								"lbl.sm.recommend.poapprover.sign.nonbumi",
								params));
					}
				}
			} else {
				if (supplUser) {
					persCalcVo.setBumiRecommendNote(getMessage(
							"lbl.sm.recommend.supplier.nonbumi", null));
				} else {
					persCalcVo.setBumiRecommendNote(getMessage(
							"lbl.sm.recommend.poapprover.nonbumi", null));
				}
			}
		}

		return persCalcVo.getBumiRecommendNote();
	}

	/**
	 * Sets the all bank signatories non bumi.
	 * 
	 * @param persCalcVo
	 *            the pers calc vo
	 * @param smSupplierBankList
	 *            the sm supplier bank list
	 */
	private static void setAllBankSignatoriesNonBumi(
			SmPersonnleCalcVO persCalcVo,
			List<SmSupplierBank> smSupplierBankList) {
		StringBuffer sb = new StringBuffer();
		String bankSignatoryNonBumi = "";

		if (smSupplierBankList != null
				&& CollectionUtils.isNotEmpty(smSupplierBankList)) {
			for (SmSupplierBank smSupplierBank : smSupplierBankList) {
				bankSignatoryNonBumi = getBankSignatoryNonBumi(smSupplierBank);
				if (!StringUtils.isEmpty(bankSignatoryNonBumi)) {
					sb.append(bankSignatoryNonBumi).append(",");
				}
			}
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.lastIndexOf(","));
				bankSignatoryNonBumi = sb.toString();
			}
		}

		persCalcVo.setNonBumiSignatoryBank(bankSignatoryNonBumi);
	}

	/**
	 * Gets the bank signatory non bumi.
	 * 
	 * @param smSupplierBank
	 *            the sm supplier bank
	 * @return the bank signatory non bumi
	 */
	private static String getBankSignatoryNonBumi(SmSupplierBank smSupplierBank) {
		String bankRetVal = "";

		if (smSupplierBank.getSmChequeSignatories() != null
				&& CollectionUtils.isNotEmpty(smSupplierBank
						.getSmChequeSignatories())) {
			for (SmChequeSignatory sign : smSupplierBank
					.getSmChequeSignatories()) {
				if (sign.getSmPersonnel() != null
						&& sign.getSmPersonnel().getBumiFlg() == 0) {
					bankRetVal = smSupplierBank.getPmFinancialOrg() == null ? ""
							: smSupplierBank.getPmFinancialOrg()
									.getFinOrgName();
					break;
				}
			}
		}

		return bankRetVal;
	}

	/**
	 * Sets the bumi status.
	 * 
	 * @param persCalcVo
	 *            the pers calc vo
	 * @param bumiPercentage
	 *            the bumi percentage
	 * @return the sm personnle calc vo
	 */
	private static SmPersonnleCalcVO setBumiStatus(
			SmPersonnleCalcVO persCalcVo, BigDecimal bumiPercentage) {
		persCalcVo.setBumiStatus(false);
		if (bumiPercentage != null
				&& bumiPercentage.compareTo(new BigDecimal(50)) == 1) {
			persCalcVo.setBumiStatus(true);
		}
		return persCalcVo;
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
	 * Calculate sm application paid up capital sum.
	 * 
	 * @param compBasic
	 *            - SmCompanyBasic
	 * @param businessTypeVal
	 *            - String
	 */
	public static void calculateSmApplicationPaidUpCapitalSum(
			SmCompanyBasic compBasic, String businessTypeVal) {
		boolean llpOrRoc = false;
		if (compBasic.getBumiEquity() != null) {
			compBasic.setBumiEquity(compBasic.getBumiEquity().setScale(2,
					RoundingMode.HALF_UP));
		} else {
			compBasic.setBumiEquity(new BigDecimal(0));
		}
		if (compBasic.getNonBumiEquity() != null) {
			compBasic.setNonBumiEquity(compBasic.getNonBumiEquity().setScale(2,
					RoundingMode.HALF_UP));
		} else {
			compBasic.setNonBumiEquity(new BigDecimal(0));
		}
		if (compBasic.getForeignerEquity() != null) {
			compBasic.setForeignerEquity(compBasic.getForeignerEquity()
					.setScale(2, RoundingMode.HALF_UP));
		} else {
			compBasic.setForeignerEquity(new BigDecimal(0));
		}
		if (businessTypeVal
				.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT)
				|| businessTypeVal
						.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP_DEFAULT)) {
			llpOrRoc = true;
		}
		if (compBasic.getGovtEquity() != null && llpOrRoc) {
			compBasic.setGovtEquity(compBasic.getGovtEquity().setScale(2,
					RoundingMode.HALF_UP));
		} else {
			compBasic.setGovtEquity(new BigDecimal(0));
		}
		if (compBasic.getGovtUnitrustEquity() != null && llpOrRoc) {
			compBasic.setGovtUnitrustEquity(compBasic.getGovtUnitrustEquity()
					.setScale(2, RoundingMode.HALF_UP));
		} else {
			compBasic.setGovtUnitrustEquity(new BigDecimal(0));
		}
		if (compBasic.getForeignCompanyEquity() != null && llpOrRoc) {
			compBasic.setForeignCompanyEquity(compBasic
					.getForeignCompanyEquity()
					.setScale(2, RoundingMode.HALF_UP));
		} else {
			compBasic.setForeignCompanyEquity(new BigDecimal(0));
		}
		if (compBasic.getLocalCompanyEquity() != null && llpOrRoc) {
			compBasic.setLocalCompanyEquity(compBasic.getLocalCompanyEquity()
					.setScale(2, RoundingMode.HALF_UP));
		} else {
			compBasic.setLocalCompanyEquity(new BigDecimal(0));
		}
		if (llpOrRoc) {
			compBasic.setCompaniesEquityTotal(compBasic.getLocalCompanyEquity()
					.add(compBasic.getForeignCompanyEquity())
					.add(compBasic.getGovtUnitrustEquity())
					.add(compBasic.getGovtEquity())
					.setScale(2, RoundingMode.HALF_UP));
		} else {
			compBasic.setCompaniesEquityTotal(new BigDecimal(0));
		}
		if (compBasic.getAuthorizedCapital() != null) {
			compBasic.setAuthorizedCapital(compBasic.getAuthorizedCapital()
					.setScale(2, RoundingMode.HALF_UP));
		} else {
			compBasic.setAuthorizedCapital(new BigDecimal(0));
		}
		if (compBasic.getTotalShare() != null) {
			compBasic.setTotalShare(compBasic.getTotalShare().setScale(2,
					RoundingMode.HALF_UP));
		} else {
			compBasic.setTotalShare(new BigDecimal(0));
		}
		if (compBasic.getTotalMember() != null) {
			compBasic.setTotalMember(compBasic.getTotalMember());
		} else {
			compBasic.setTotalMember(new BigDecimal(0));
		}
		compBasic.setPaidUpCapital(compBasic.getBumiEquity()
				.add(compBasic.getNonBumiEquity())
				.add(compBasic.getLocalCompanyEquity())
				.add(compBasic.getForeignCompanyEquity())
				.add(compBasic.getForeignerEquity())
				.add(compBasic.getGovtEquity())
				.add(compBasic.getGovtUnitrustEquity())
				.setScale(2, RoundingMode.HALF_UP));
		log.info("**** Paid up capital ****** " + compBasic.getPaidUpCapital());
	}

	/**
	 * Calculate sm application paid up capital percent.
	 * 
	 * @param compBasic
	 *            - SmCompanyBasic
	 */
	public static void calculateSmApplicationPaidUpCapitalPercent(
			SmCompanyBasic compBasic) {
		BigDecimal mul = new BigDecimal(100);
		log.info(" *** Intial Calc ***** ");
		if (compBasic.getPaidUpCapital().compareTo(new BigDecimal(0)) > 0) {
			compBasic.setBumiPercent(compBasic
					.getBumiEquity()
					.divide(compBasic.getPaidUpCapital(), 4,
							RoundingMode.HALF_UP).multiply(mul)
					.setScale(2, RoundingMode.HALF_UP));
			compBasic.setBumiPercentage(compBasic
					.getBumiEquity()
					.divide(compBasic.getPaidUpCapital(), 4,
							RoundingMode.HALF_UP).multiply(mul)
					.setScale(2, RoundingMode.HALF_UP));
			log.debug("******* First Value of  setBumiPercent "
					+ compBasic.getBumiPercent());
			compBasic.setNonBumiPercent(compBasic
					.getNonBumiEquity()
					.divide(compBasic.getPaidUpCapital(), 4,
							RoundingMode.HALF_UP).multiply(mul)
					.setScale(2, RoundingMode.HALF_UP));
			compBasic.setNonBumiPercentage(compBasic
					.getNonBumiEquity()
					.divide(compBasic.getPaidUpCapital(), 4,
							RoundingMode.HALF_UP).multiply(mul)
					.setScale(2, RoundingMode.HALF_UP));
			compBasic.setForiegnerPercent(compBasic
					.getForeignerEquity()
					.divide(compBasic.getPaidUpCapital(), 4,
							RoundingMode.HALF_UP).multiply(mul)
					.setScale(2, RoundingMode.HALF_UP));
			compBasic.setGovtUTPercent(compBasic
					.getGovtUnitrustEquity()
					.divide(compBasic.getPaidUpCapital(), 4,
							RoundingMode.HALF_UP).multiply(mul)
					.setScale(2, RoundingMode.HALF_UP));
			compBasic.setGovtEquityPercent(compBasic
					.getGovtEquity()
					.divide(compBasic.getPaidUpCapital(), 4,
							RoundingMode.HALF_UP).multiply(mul)
					.setScale(2, RoundingMode.HALF_UP));
			compBasic.setForiegnCompPercent(compBasic
					.getForeignCompanyEquity()
					.divide(compBasic.getPaidUpCapital(), 4,
							RoundingMode.HALF_UP).multiply(mul)
					.setScale(2, RoundingMode.HALF_UP));
			compBasic.setLocalCompPercent(compBasic
					.getLocalCompanyEquity()
					.divide(compBasic.getPaidUpCapital(), 4,
							RoundingMode.HALF_UP).multiply(mul)
					.setScale(2, RoundingMode.HALF_UP));
		}

	}

	/**
	 * Calculate all equity owner percent.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean
	 */
	public static void calcAllEquityOwnerPercent(
			SmApplicationManagedBean managedBean) {
		BigDecimal mul = new BigDecimal(100);
		for (SmPersonnel person : managedBean.getSmapplShareHolders()) {
			if (person.getShareAmt() != null
					&& managedBean.getSmAppl().getSmCompanyBasic()
							.getPaidUpCapital() != null
					&& managedBean.getSmAppl().getSmCompanyBasic()
							.getPaidUpCapital().compareTo(new BigDecimal(0)) > 0) {
				person.setSharePercentage(person
						.getShareAmt()
						.divide(managedBean.getSmAppl().getSmCompanyBasic()
								.getPaidUpCapital(), 4, RoundingMode.HALF_UP)
						.multiply(mul).setScale(2, RoundingMode.HALF_UP));
			} else {
				person.setShareAmt(new BigDecimal(0).setScale(2,
						RoundingMode.HALF_UP));
				person.setSharePercentage(new BigDecimal(0).setScale(2,
						RoundingMode.HALF_UP));
			}
		}

	}

	/**
	 * Gets the sm bpm task title.
	 * 
	 * @param bpmOriginalTaskTitle
	 *            the bpm original task title
	 * @param bpmProcessId
	 *            the bpm process id
	 * @return the sm bpm task title
	 */
	public static String getSmBpmTaskTitle(String bpmOriginalTaskTitle,
			String bpmProcessId) {
		String retVal = bpmOriginalTaskTitle == null ? ""
				: bpmOriginalTaskTitle;

		switch (retVal) {
		case "Complete Site Visit Report":
			retVal = getMessage("lbl.sm.bpmtitle.prepare.site.visit.report",
					null);
			break;

		case "Assign Site Visit":
			retVal = getMessage("lbl.sm.bpmtitle.assign.sitevisit", null);
			break;

		case "Verify Supporting Documents":
			retVal = getMessage("lbl.sm.bpmtitle.verify.suppdoc", null);
			break;

		case "Review Application (Special Category)":
			retVal = getMessage("lbl.sm.bpmtitle.review.special.category", null);
			break;

		case "Review Cancellation":
			retVal = getMessage("lbl.sm.bpmtitle.review.cancelmof", null);
			break;

		// case "Review application (Escalated)":
		// retVal = getMessage("lbl.sm.bpmtitle.reviewappl.escalated", null);
		// break;

		case "Make Payment":
			retVal = getMessage("lbl.sm.bpmtitle.payment.reg.fee", null);
			break;

		// case "Review Application":
		// if(bpmProcessId.equalsIgnoreCase("MOF Supplier Registration")){
		// retVal = getMessage("lbl.sm.bpmtitle.reviewappl.newmof", null);
		// }
		// break;

		}

		return retVal;
	}

	/**
	 * Checks if the serialized file exist or not.
	 * 
	 * @param applNo
	 *            - String applNo
	 * @param serType
	 *            - String serialized file type
	 * @return true, if is ser file exist
	 */
	public static boolean isSerFileExist(String applNo, String serType) {
		boolean exist = true;
		// Check if approved serialized file exists for latest approved
		// application, if exists - do nothing, else, generate
		String fileName = applNo + "_" + serType + ".ser";
		String baseDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)
				+ File.separator
				+ Calendar.getInstance().get(Calendar.YEAR)
				+ File.separator;
		String smRevisionDir = "SM" + File.separator + applNo + File.separator
				+ "Revision" + File.separator;
		exist = new File(baseDir + smRevisionDir, fileName).exists();
		log.info("File Name:" + fileName);
		log.info("SER type:" + serType);
		log.info("Directory:" + baseDir + smRevisionDir);
		log.info("Is File Exist:" + exist);
		return exist;
	}

	/**
	 * Creates the serialized file.
	 * 
	 * @param applNo
	 *            - String applNo
	 * @param serType
	 *            - String serialized file type
	 */
	public static void createSerFile(SmAppl smAppl, String serType) {
		ISmApplRevisionSO applRevisionSO = null;
		try {
			log.info("start creating SER file for appNo:" + smAppl.getApplNo()
					+ "  serType:" + serType);
			applRevisionSO = (ISmApplRevisionSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
							ISmApplRevisionSO.class);

			SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
			reqDTO.setSmAppl(smAppl);
			if (serType.equalsIgnoreCase("SUP")) {
				reqDTO.setType("supplier");
			} else {
				reqDTO.setType("approved");
			}
			applRevisionSO.applicationHist(reqDTO);
		} catch (Exception ex) {
			log.info("Failed to create SER file  [" + ex.getMessage() + "]");
		} finally {
			applRevisionSO = null;
		}
	}

	/**
	 * Assigns the default address for the given address parameter.
	 * 
	 * @param defaultAddr
	 *            - SmAddress defaultAddr
	 * @return SmAddress object
	 */
	public static SmAddress getDefaultAddress(SmAddress defaultAddr) {
		defaultAddr = new SmAddress();
		defaultAddr.setPmCity(new PmCity());
		defaultAddr.setPmDistrict(new PmDistrict());
		// defaultAddr.setPmCountry(new PmCountry());
		defaultAddr
				.setPmCountry(SmCommonUtil
						.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE));
		// defaultAddr.getPmCountry().setCountryId(1l);
		defaultAddr.setPmState(new PmState());
		return defaultAddr;
	}

	/**
	 * Gets the country id for Malaysia.
	 * 
	 * @return Long - country Id
	 */
	public static Long getMyCountryId() {
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICountrySO.class);
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);
		return resDTO.getPmCountry().getCountryId();
	}

	/**
	 * Close block ui.
	 */
	public static void closeBlockUI() {
		RequestContext.getCurrentInstance().execute("blockWidget.hide()");
	}

	/**
	 * Checks if the given personnel created in previously approved application
	 * of the supplier.
	 * 
	 * @param smAppl
	 *            - SmAppl
	 * @param smPersonnel
	 *            - SmPersonnel
	 * @return true, if the personnel was created in previous approved
	 *         application
	 */
	public static boolean isPersonnelCreatedInPreviousApprovedAppl(
			SmAppl smAppl, SmPersonnel smPersonnel) {
		IPersonnelSO reqSO = (IPersonnelSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IPersonnelSO.class);
		boolean retVal = false;
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		String prevApplNo = smAppl.getApprovedSerFile() == null ? "" : smAppl
				.getApprovedSerFile().substring(
						smAppl.getApprovedSerFile().lastIndexOf('/') + 1,
						smAppl.getApprovedSerFile().indexOf("_APP"));
		reqDTO.setPrevApprovedApplNo(prevApplNo);
		if (smPersonnel.getPersonnelId() != null
				&& StringUtils.isNotBlank(prevApplNo)) {
			reqDTO.setPersonnelId(smPersonnel.getPersonnelId());
			retVal = reqSO
					.isPersonnelCreatedInPreviousApprovedApplication(reqDTO);
		}
		return retVal;
	}

	/**
	 * Checks if the given personnel is already deactivated.
	 * 
	 * @param smPersonnel
	 *            - SMmPersonnel
	 * @return true, if personnel is already deactivated
	 */
	public static boolean isPersonnelAlreadyDeactivate(SmPersonnel smPersonnel) {
		boolean retVal = false;
		IPersonnelSO reqSO = (IPersonnelSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setPersonnelId(smPersonnel.getPersonnelId());
		retVal = reqSO.isPersonnelAlreadyDeactivate(reqDTO);
		return retVal;
	}

	/**
	 * Format the given number to two decimal point.
	 * 
	 * @param amount
	 *            - BigDecimal
	 * @return String - formatted number
	 */
	public static String formatToTwoDecimalPoint(BigDecimal amount) {
		DecimalFormat formatter = new DecimalFormat("###,##0.00");
		return formatter.format(amount);
	}

	/**
	 * Create a default TrackingDiaryVO object.
	 * 
	 * @param actionDesc
	 *            - String
	 * @param applId
	 *            - Long
	 * @param applNo
	 *            - String
	 * @param userRole
	 *            - String
	 * @return SmTrackingDairyVO object
	 */
	public static SmTrackingDairyVO assignTrackingDiaryValues(
			String actionDesc, Long applId, String applNo, String userRole) {
		SmTrackingDairyVO trckVO = new SmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(userRole);
		trckVO.setDocId(applId);
		trckVO.setDocNo(applNo);
		trckVO.setDocType("SR");
		trckVO.setGroupId(null);
		trckVO.setStatus(null);
		trckVO.setGroupDocType(null);

		return trckVO;
	}

	/**
	 * Get states based on Country id.
	 * 
	 * @param countryId
	 *            - Long
	 * @return List<SelectItem> - contains the list of states
	 */
	public static List<SelectItem> getStateList(Long countryId) {
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryId);
		StateResDTO resDTO = stateSO.retriveStatesListByCountryID(reqDTO);
		List<SelectItem> stateList = new ArrayList<SelectItem>();
		for (PmState state : resDTO.getStateList()) {
			SelectItem si = new SelectItem();
			si.setLabel(state.getStateName());
			si.setValue(state.getStateId());
			stateList.add(si);
		}
		return stateList;

	}

	/**
	 * Get districts based on state id.
	 * 
	 * @param stateId
	 *            - Long
	 * @return List<SelectItem> - contains the list of states
	 */
	public static List<SelectItem> getDistrictList(Long stateId) {
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setStateId(stateId);
		DistrictResDTO resDTO = districtSO.retriveDistrictListByStateID(reqDTO);
		List<SelectItem> districtList = new ArrayList<SelectItem>();
		for (PmDistrict district : resDTO.getDistrictList()) {
			SelectItem si = new SelectItem();
			si.setLabel(district.getDistrictName());
			si.setValue(district.getDistrictId());
			districtList.add(si);
		}
		return districtList;
	}

	/**
	 * Get cities based on district id.
	 * 
	 * @param districtId
	 *            - Long
	 * @return List<SelectItem> - contains the list of cities
	 */
	public static List<SelectItem> getCityList(Long districtId) {
		ICitySO citySO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICitySO.class);
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(districtId);
		CityResDTO resDTO = citySO.retriveCityListByDistrictId(reqDTO);
		List<SelectItem> cityList = new ArrayList<SelectItem>();
		for (PmCity city : resDTO.getCityList()) {
			SelectItem si = new SelectItem();
			si.setLabel(city.getCityName());
			si.setValue(city.getCityId());
			cityList.add(si);
		}
		return cityList;
	}

	/**
	 * Set personnel role name for display purposes based on properties.
	 * 
	 * @param supplierType
	 *            - String
	 * @param smPersonnels
	 *            - List<SmPersonnel>
	 */
	public static void setRoleName(String supplierType,
			List<SmPersonnel> smPersonnels) {
		StringBuffer sb = null;
		for (SmPersonnel smPersonnel : smPersonnels) {
			smPersonnel.setRoleName("");
			sb = new StringBuffer();
			if (smPersonnel.isEquityOwner()) {
				sb.append(
						SmCommonUtil.getMessage(
								SupplierManagementConst.PERSONNEL_EQUI_OWNER,
								null)).append(",");
			}
			if (smPersonnel.isDirector()) {
				sb.append(
						SmCommonUtil.getMessage(
								SupplierManagementConst.PERSONNEL_DIR_ROLENAME,
								null)).append(",");
			}
			if (smPersonnel.isHigherManagement()) {
				sb.append(
						SmCommonUtil
								.getMessage(
										SupplierManagementConst.PERSONNEL_HGHR_ROLENAME,
										null)).append(",");
			}
			if (smPersonnel.isClerical()) {
				if (supplierType
						.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
					sb.append(
							SmCommonUtil
									.getMessage(
											SupplierManagementConst.PERSONNEL_CLERICAL_STAFF_ROLENAME,
											null)).append(",");
				} else {
					sb.append(
							SmCommonUtil
									.getMessage(
											SupplierManagementConst.PERSONNEL_STAFF_ROLENAME,
											null)).append(",");
				}
			}
			if (smPersonnel.isManagement()) {
				sb.append(
						SmCommonUtil
								.getMessage(
										SupplierManagementConst.PERSONNEL_MANAGEMENT_ROLENAME,
										null)).append(",");
			}
			if (supplierType
					.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
				if (smPersonnel.isProf()) {
					sb.append(
							SmCommonUtil
									.getMessage(
											SupplierManagementConst.PERSONNEL_PROF_STAFF_ROLENAME,
											null)).append(",");

				}
				if (smPersonnel.isSemiProf()) {
					sb.append(
							SmCommonUtil
									.getMessage(
											SupplierManagementConst.PERSONNEL_SEMIPROF_STAFF_ROLENAME,
											null)).append(",");
				}
			}
			if (!sb.toString().isEmpty() && sb.toString().length() > 0) {
				smPersonnel.setRoleName(sb.toString().substring(0,
						sb.toString().length() - 1));
			}
		}
	}

	/**
	 * Set MDC user_id value to be used in BaseEntity for auditing purposes.
	 */
	public static void setMDCUserId() {
		Long userId = 1L;
		PmUser user = new FacesScopes().getUserSessionBean();
		if (user != null) {
			userId = user.getUserId();
		}
		new LogKey(userId);
	}

	/**
	 * Get Bpm Task List by token.
	 * 
	 * @param token
	 *            - String
	 * @return List<WorklistItemVO>
	 */
	public static List<WorklistItemVO> getBpmTaskListByAuthToken(String token) {
		IBpmSO bpmSO = null;
		BpmRequestDTO reqDTO = null;
		BpmResponseDTO responseDTO = null;
		List<WorklistItemVO> taskList = new ArrayList<WorklistItemVO>();
		try {
			bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			reqDTO = new BpmRequestDTO();
			reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
			reqDTO.setOffset(0);
			reqDTO.setState("ASSIGNED");
			reqDTO.setDocType("");

			reqDTO.setViewBy("");
			reqDTO.setAssignment("ALL");
			reqDTO.setCtxToken(token);
			responseDTO = bpmSO.getWorklistItems(reqDTO);
			if (responseDTO.getWorklistVO() != null
					&& responseDTO.getWorklistVO().getWorklistItem() != null) {
				taskList = responseDTO.getWorklistVO().getWorklistItem();
			}
		} finally {
			bpmSO = null;
			reqDTO = null;
			responseDTO = null;
		}
		return taskList;
	}

	/**
	 * Redirect to page with message as optional.
	 * 
	 * @param page
	 *            - String
	 * @param msgKeyId
	 *            - String
	 */
	public static void redirectToPage(String page, String msgKeyId) {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (StringUtils.isNotBlank(msgKeyId)) {
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(msgKeyId,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		}
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, page);
	}

	/**
	 * Redirect to external URL
	 * 
	 * @param url
	 *            -String (mandatory)
	 */
	public static void redirectToExternalUrl(String url) {
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		try {
			externalContext.redirect(url);
		} catch (IOException e) {
			throw new EpException(e.getMessage());
		}
	}

	/**
	 * Generate velocity to PDF
	 * 
	 * @param velocityFilePath
	 * @param outputPdfPath
	 * @param context
	 * @throws Exception
	 */
	public static void generateVelocityToPdf(String velocityFilePath,
			String outputPdfPath, VelocityContext context) throws Exception {
		File tempFile1 = new File(outputPdfPath);
		OutputStream os1 = new FileOutputStream(tempFile1);
		DocumentGenerator.getInstance().generatePdfFromVelocity(
				velocityFilePath, context, os1);
		os1.flush();
		os1.close();
	}

	/**
	 * Return base directory
	 * 
	 * @return String
	 */
	public static String getBaseDir() {
		return EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
	}

	/**
	 * Set newly added personnel permanent and mail address
	 * 
	 * @param smPersonnel
	 * @param mailAddress
	 * @param permAddress
	 */
	public static void setNewPersonnelPermAndMailAddress(
			SmPersonnel smPersonnel, SmPersonnelAddress mailAddress,
			SmPersonnelAddress permAddress) {
		permAddress.setSmPersonnel(smPersonnel);
		mailAddress.setSmPersonnel(smPersonnel);
		SmAddress permanent = permAddress.getSmAddress();

		PmCity city = permanent.getPmCity();
		PmDistrict district = permanent.getPmDistrict();
		PmState state = permanent.getPmState();
		PmCountry country = permanent.getPmCountry();

		Long cityId = city.getCityId();
		Long districtId = district.getDistrictId();
		Long stateId = state.getStateId();
		Long countryId = country.getCountryId();

		CountryReqDTO reqCountryDTO = new CountryReqDTO();
		reqCountryDTO.setCountryId(countryId);
		CountryResDTO resCountryDTO = SmServiceLookup.getCountrySO()
				.retrieveCountryById(reqCountryDTO);
		PmCountry newCountry = resCountryDTO.getPmCountry();

		permanent.setPmCountry(newCountry);

		StateReqDTO reqStateDTO = new StateReqDTO();
		reqStateDTO.setStateId(stateId);
		StateResDTO resStateDTO = SmServiceLookup.getStateSO()
				.retriveStateByID(reqStateDTO);
		PmState newState = resStateDTO.getPmState();

		permanent.setPmState(newState);

		DistrictReqDTO reqDistrictDTO = new DistrictReqDTO();
		reqDistrictDTO.setDistrictId(districtId);
		DistrictResDTO resDistrictDTO = SmServiceLookup.getDistrictSO()
				.retriveDistrictByID(reqDistrictDTO);
		PmDistrict newDistrict = resDistrictDTO.getPmDistrict();

		permanent.setPmDistrict(newDistrict);

		CityReqDTO reqCityDTO = new CityReqDTO();
		reqCityDTO.setCityId(cityId);
		CityResDTO resCityDTO = SmServiceLookup.getCitySO().retriveCityById(
				reqCityDTO);
		PmCity newCity = resCityDTO.getPmCity();

		permanent.setPmCity(newCity);

		if (mailAddress.isSameAsPerm()) {
			SmAddress address = cloneAddress(permAddress.getSmAddress());
			mailAddress.setSmAddress(address);
		} else {
			SmAddress s2 = mailAddress.getSmAddress();

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
			resCountryDTO = SmServiceLookup.getCountrySO().retrieveCountryById(
					reqCountryDTO);
			newCountry = resCountryDTO.getPmCountry();

			s2.setPmCountry(newCountry);

			reqStateDTO = new StateReqDTO();
			reqStateDTO.setStateId(stateId);
			resStateDTO = SmServiceLookup.getStateSO().retriveStateByID(
					reqStateDTO);
			newState = resStateDTO.getPmState();

			s2.setPmState(newState);

			reqDistrictDTO = new DistrictReqDTO();
			reqDistrictDTO.setDistrictId(districtId);
			resDistrictDTO = SmServiceLookup.getDistrictSO()
					.retriveDistrictByID(reqDistrictDTO);
			newDistrict = resDistrictDTO.getPmDistrict();

			s2.setPmDistrict(newDistrict);

			reqCityDTO = new CityReqDTO();
			reqCityDTO.setCityId(cityId);
			resCityDTO = SmServiceLookup.getCitySO()
					.retriveCityById(reqCityDTO);
			newCity = resCityDTO.getPmCity();

			s2.setPmCity(newCity);
		}

		List<SmPersonnelAddress> smPersonnelAddresses = new ArrayList<SmPersonnelAddress>();
		smPersonnelAddresses.add(permAddress);
		smPersonnelAddresses.add(mailAddress);
		smPersonnel.setSmPersonnelAddresses(smPersonnelAddresses);
	}

	/**
	 * Copy address to new address object
	 * 
	 * @param SmAddress
	 *            refAddress
	 * @return SmAddress
	 */
	public static SmAddress cloneAddress(SmAddress refAddress) {
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

	public static boolean validateGstRegNo(String value) {
		String gstNoPattern = "\\d+";
		Pattern pattern;
		Matcher matcher;

		pattern = Pattern.compile(gstNoPattern);

		if (value != null) {
			String gstNo = value.toString();

			if (gstNo.length() > 0) {
				if (gstNo.length() != 12) {
					return false;
				}

				matcher = pattern.matcher(value.toString());
				if (!matcher.matches()) {
					return false;
				}

				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the current active work flow status.
	 * 
	 * @param applId
	 *            - the appl id
	 * @return the SmWorkflowStatus
	 */
	public static SmWorkflowStatus getCurrentActiveWokFlowStatus(Long applId) {
		SmWorkFlowStatusReqDTO reqDTO = new SmWorkFlowStatusReqDTO();
		reqDTO.setApplId(applId);
		SmWorkFlowStatusResDTO resDTO = SmServiceLookup.getSmWorkFlowStatusSO()
				.retrieveCurrentWorkFlowStatusByDocId(reqDTO);
		if (resDTO != null && resDTO.getWorkflowStatus() != null) {
			return resDTO.getWorkflowStatus();
		} else {
			return null;
		}
	}

	/**
	 * Get the latest GST rate from Pm Config Rule Value
	 * 
	 * @return BigDecimal
	 */
	public static BigDecimal getGstCurrentRate() {
		return SmServiceLookup.getPaymentSO().retrieveGstCurrentRate()
				.getGstCurrRate();
	}

	/**
	 * Get Basic Personnel(s) by supplier id
	 * 
	 * @param Long
	 *            supplierId
	 * @return List<SmPersonnel>
	 */
	public static List<SmPersonnel> getBasicPersonnelsBySupplierId(
			Long supplierId) {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(supplierId);
		return SmServiceLookup.getPersonnelSO()
				.retrieveBasicPersonnelsBySuppId(reqDTO).getSmPersonnelList();
	}

	/**
	 * Get EPerolehan based url
	 * 
	 * @return String
	 */
	public static String getEpBaseUrl() {
		FacesContext fCtx = FacesContext.getCurrentInstance();
		PortletRequest req = (PortletRequest) fCtx.getExternalContext()
				.getRequest();
		ThemeDisplay themeDisplay = (ThemeDisplay) req
				.getAttribute(WebKeys.THEME_DISPLAY);
		Layout layout = themeDisplay.getLayout();

		String portalUrl = themeDisplay.getURLPortal();
		String currentUrl = themeDisplay.getURLCurrent();
		currentUrl = currentUrl.substring(0,
				currentUrl.indexOf(layout.getFriendlyURL()));

		String url = portalUrl + currentUrl;
		return url;
	}

	/**
	 * Redirect to liferay logout url
	 */
	public static void redirectToLogoutUrl() {
		FacesContext fCtx = FacesContext.getCurrentInstance();
		PortletRequest req = (PortletRequest) fCtx.getExternalContext()
				.getRequest();
		ThemeDisplay themeDisplay = (ThemeDisplay) req
				.getAttribute(WebKeys.THEME_DISPLAY);

		String portalUrl = themeDisplay.getURLPortal();
		String logOutUrl = portalUrl + "/c/portal/logout";
		System.out.println(">>>>>>>>>>>>logout url default:" + logOutUrl
				+ "<<<<<<<<<<<<<<");
		log.info(">>>>>>>>>>>>logout url default:" + logOutUrl
				+ "<<<<<<<<<<<<<<");
		redirectToExternalUrl(logOutUrl);
	}
}
