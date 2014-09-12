package my.ep.web.util.sm.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.constant.MessageConst;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.type.sm.ApplType;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.SmPersonnelSummaryCountVO;
import my.ep.web.vo.SmPersonnleCalcVO;

import org.apache.commons.id.Hex;
import org.apache.log4j.Logger;

public class SmCommonUtil {
	private static Logger log = Logger.getLogger(SmCommonUtil.class);

	public static String hashString(String str) {

		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException ex) {
			System.out.println(ex.getMessage());
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
			System.out.println(ex.getMessage());
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

	public static String getApplTypeDesc(String applType) {
		System.out.println("--------getApplTypeDesc start--------" + applType);
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

		System.out.println("--------getApplTypeDesc--------" + apType);

		return apType;
	}

	public static SmPersonnleCalcVO calcPersonnelListCount(
			List<SmPersonnel> personnelList, List<SmStaffTotal> stfTotals,
			String supplierType) {
		System.out
				.println("********* Staff calculation from Util class ********* ");
		SmPersonnleCalcVO personVO = new SmPersonnleCalcVO();
		personVO = resetAllPersonnelCount(stfTotals, personVO);
		// setValidSmUserLimit(validatePersonLimit());
		for (SmPersonnel per : personnelList) {
			personVO = calcSumUpByPersonnel(per, personVO, supplierType);
		}
		calcuteStaffSubTotalSummary(personVO);
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
			SmPersonnleCalcVO persCalcVo, String supplierType) {
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
			sb.append(SupplierManagementConst.PERSONNEL_EQUI_OWNER).append(",");
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
			sb.append(SupplierManagementConst.PERSONNEL_DIR_ROLENAME).append(
					",");
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
			sb.append(SupplierManagementConst.PERSONNEL_HGHR_ROLENAME).append(
					",");
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
						SupplierManagementConst.PERSONNEL_CLERICAL_STAFF_ROLENAME)
						.append(",");
			} else {
				sb.append(SupplierManagementConst.PERSONNEL_STAFF_ROLENAME)
						.append(",");
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
			sb.append(SupplierManagementConst.PERSONNEL_MANAGEMENT_ROLENAME)
					.append(",");
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
				sb.append(SupplierManagementConst.PERSONNEL_PROF_STAFF_ROLENAME)
						.append(",");
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
						SupplierManagementConst.PERSONNEL_SEMIPROF_STAFF_ROLENAME)
						.append(",");
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
		System.out.println("**** Paid up capital ****** "
				+ compBasic.getPaidUpCapital());
	}

	public static void calculateSmApplicationPaidUpCapitalPercent(
			SmCompanyBasic compBasic) {
		BigDecimal mul = new BigDecimal(100);
		System.out.println(" *** Intial Calc ***** ");
		if (compBasic.getPaidUpCapital().compareTo(new BigDecimal(0)) > 0) {
			compBasic.setBumiPercent(compBasic
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
}
