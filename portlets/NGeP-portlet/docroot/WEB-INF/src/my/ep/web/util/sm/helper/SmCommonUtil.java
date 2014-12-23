package my.ep.web.util.sm.helper;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import my.ep.biz.service.pm.common.businessnature.IBusinessNatureSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.revision.ISmApplRevisionSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.businessnature.BusinessNatureReqDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionResDTO;
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
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.type.sm.ApplType;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.vo.SmPersonnelSummaryCountVO;
import my.ep.web.vo.SmPersonnleCalcVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.id.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SmCommonUtil {
	private static Logger log = Logger.getLogger(SmCommonUtil.class);

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

	public static PmCountry retrieveCountryByCountryCode(String countryCode) {
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode(countryCode);

		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);

		return resDTO.getPmCountry();
	}

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

	public static Map<Long, PmStatusDesc> converPmStatusListToMap(
			List<PmStatusDesc> pmStatusDescLs) {
		Map<Long, PmStatusDesc> statDescMap = new HashMap<Long, PmStatusDesc>();
		for (PmStatusDesc pmStatDesc : pmStatusDescLs) {
			statDescMap.put(pmStatDesc.getPmStatus().getStatusId(), pmStatDesc);
		}
		return statDescMap;
	}

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

	public static PmBusinessNature retrieveBusinessNatureById(Long bizId) {
		BusinessNatureReqDTO reqDTO = new BusinessNatureReqDTO();
		reqDTO.setBusinessNatureId(bizId);

		IBusinessNatureSO reqSO = (IBusinessNatureSO) BusinessDelegate
				.getInstance().getServiceObject("pm", IBusinessNatureSO.class);
		BusinessNatureResDTO resDTO = reqSO
				.retrieveBusinessNatureDtlById(reqDTO);

		return resDTO.getBusinessNature();
	}

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

	private static PmCountry retrievePmCountryById(Long countryId) {
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICountrySO.class);
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryId(countryId);
		CountryResDTO resDTO = reqSO.retrieveCountryById(reqDTO);

		return resDTO.getPmCountry();
	}

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

	public static SmPersonnleCalcVO resetAllPersonnelCount(
			List<SmStaffTotal> stfTotalList, SmPersonnleCalcVO persCalcVo) {
		return assignEnteredStaffValues(stfTotalList, persCalcVo);
	}

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

	private static SmPersonnleCalcVO setBumiStatus(
			SmPersonnleCalcVO persCalcVo, BigDecimal bumiPercentage) {
		persCalcVo.setBumiStatus(false);
		if (bumiPercentage != null
				&& bumiPercentage.compareTo(new BigDecimal(50)) == 1) {
			persCalcVo.setBumiStatus(true);
		}
		return persCalcVo;
	}

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
}
