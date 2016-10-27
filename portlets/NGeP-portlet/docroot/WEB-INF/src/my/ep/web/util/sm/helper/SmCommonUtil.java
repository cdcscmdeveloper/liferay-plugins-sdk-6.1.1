package my.ep.web.util.sm.helper;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.id.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.eclipse.persistence.exceptions.ValidationException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.theme.ThemeDisplay;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.pm.common.businessnature.IBusinessNatureSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.common.myidentity.IMyIdentitySO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.sm.revision.ISmApplRevisionSO;
import my.ep.biz.service.sm.sdoreview.ISdoReviewSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureReqDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
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
import my.ep.dto.sm.common.myidentity.MyIdentityReqDTO;
import my.ep.dto.sm.common.myidentity.MyIdentityResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.SmTrackingDairyVO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.sdoreview.SdoReviewReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmMachine;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmRace;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplRejectReason;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmGstSupplierDoc;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSoftcertRequest;
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.persistence.logging.LogKey;
import my.ep.type.sm.ApplType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.constant.PagingConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.vo.SmPersonnelSummaryCountVO;
import my.ep.web.vo.SmPersonnleCalcVO;

// TODO: Auto-generated Javadoc
public class SmCommonUtil {
	private static Logger log = Logger.getLogger(SmCommonUtil.class);
	private static SmCommonUtil instance;
	private static final BigDecimal percent = new BigDecimal(100);

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
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		ParameterResDTO resDTO = reqSO.retrieveParameterDescListByType(reqDTO);

		return resDTO.getParameterDescList();
	}

	/**
	 * Retrieve parameter description list by param type,param code and language
	 * code.
	 * 
	 * @param String
	 *            paramType
	 * @param String
	 *            lang
	 * @param String
	 *            paramCode
	 * @return PmParameterDesc
	 */
	public static PmParameterDesc retrieveParameterDescByTypeAndCodeAndLang(
			String paramType, String paramCode) {
		return retrieveParameterDescByTypeAndCodeAndLang(paramType, paramCode,
				new FacesScopes().getUserLocale().getLanguage());
	}

	/**
	 * Retrieve parameter description list by param type,param code and language
	 * code
	 * 
	 * @param paramType
	 * @param paramCode
	 * @param lang
	 * @return
	 */
	public static PmParameterDesc retrieveParameterDescByTypeAndCodeAndLang(
			String paramType, String paramCode, String lang) {
		PmParameterDesc retVal = null;
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterType(paramType);
		reqDTO.setLanguageCode(lang);
		reqDTO.setParameterCode(paramCode);
		IParameterSO reqSO = (IParameterSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		ParameterResDTO resDTO = reqSO.retrieveParameterDescListByType(reqDTO);
		if (!EntityHelper.isCollectionEmpty(resDTO.getParameterDescList())) {
			retVal = resDTO.getParameterDescList().get(0);
		}
		return retVal;
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
	 * Retrieve parameter by parameter ID
	 * 
	 * @param Long
	 *            parameterId
	 * @return PmParameter
	 */
	public static PmParameter retrieveParameterById(Long parameterId) {
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterId(parameterId);
		ParameterResDTO resDTO = SmServiceLookup.getParameterSO()
				.retrieveParameterById(reqDTO);
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
	public static PmCountry retrievePmCountryById(Long countryId) {
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

		StringBuilder sb = new StringBuilder();
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
		calculateStaffCompositionPercentage(persCalcVo);
	}

	private static void calculateStaffCompositionPercentage(
			SmPersonnleCalcVO personVO) {

		// bumi owner
		personVO.setEquiOwnrBumiPerc(getPercentage(
				personVO.getEquiOwnrBumiCnt(),
				personVO.getEquityOwnerTotalCnt()));

		// non-bumi owner
		personVO.setEquiOwnrNonBumiPerc(getPercentage(
				personVO.getEquiOwnrNonBumiCnt(),
				personVO.getEquityOwnerTotalCnt()));

		// foreign owner
		personVO.setEquiOwnrFrgPerc(getPercentage(personVO.getEquiOwnrFrgCnt(),
				personVO.getEquityOwnerTotalCnt()));

		// bumi director
		personVO.setBodBumiPerc(getPercentage(personVO.getBodBumiCnt(),
				personVO.getBodTotalCnt()));

		// non-bumi director
		personVO.setBodNonBumiPerc(getPercentage(personVO.getBodNonBumiCnt(),
				personVO.getBodTotalCnt()));

		// foreign director
		personVO.setBodFrgPerc(getPercentage(personVO.getBodFrgCnt(),
				personVO.getBodTotalCnt()));

		// bumi higher management
		personVO.setHghMngBumiPerc(getPercentage(personVO.getHghMngBumiCnt(),
				personVO.getHghrMngmtTotalCnt()));

		// non bumi higher management
		personVO.setHghMngNonBumiPerc(getPercentage(
				personVO.getHghMngNonBumiCnt(), personVO.getHghrMngmtTotalCnt()));

		// foreign higher management
		personVO.setHghMngFrgPerc(getPercentage(personVO.getHghMngFrgCnt(),
				personVO.getHghrMngmtTotalCnt()));

		// bumi management
		personVO.setMngtBumiPerc(getPercentage(personVO.getMngtBumiCnt(),
				personVO.getMngmtTotalCnt()));

		// non bumi management
		personVO.setMngtNonBumiPerc(getPercentage(personVO.getMngtNonBumiCnt(),
				personVO.getMngmtTotalCnt()));

		// foreign management
		personVO.setMngtFrgPerc(getPercentage(personVO.getMngtFrgCnt(),
				personVO.getMngmtTotalCnt()));

		// bumi staff
		personVO.setStfBumiEnterPerc(getPercentage(
				personVO.getStfBumiEnterCnt(), personVO.getStfTotalCnt()));

		// non-bumi staff
		personVO.setStfNonBumiEnterPerc(getPercentage(
				personVO.getStfNonBumiEnterCnt(), personVO.getStfTotalCnt()));

		// foreign staff
		personVO.setStfFrgEnterPerc(getPercentage(personVO.getStfFrgEnterCnt(),
				personVO.getStfTotalCnt()));

		// bumi actual staff
		personVO.setStfBumiActPerc(getPercentage(personVO.getStfBumiActCnt(),
				personVO.getStfActualTotalCnt()));

		// non bumi actual staff
		personVO.setStfNonBumiActPerc(getPercentage(
				personVO.getStfNonBumiActCnt(), personVO.getStfActualTotalCnt()));

		// foreign actual staff
		personVO.setStfFrgActPerc(getPercentage(personVO.getStfFrgActCnt(),
				personVO.getStfActualTotalCnt()));

		// prof bumi
		personVO.setProfBumiEnterPerc(getPercentage(
				personVO.getProfBumiEnterCnt(), personVO.getProfTotalCnt()));

		// prof non bumi
		personVO.setProfNonBumiEnterPerc(getPercentage(
				personVO.getProfNonBumiEnterCnt(), personVO.getProfTotalCnt()));

		// prof foreign
		personVO.setProfFrgEnterPerc(getPercentage(
				personVO.getProfFrgEnterCnt(), personVO.getProfTotalCnt()));

		// prof actual bumi
		personVO.setProfBumiActPerc(getPercentage(personVO.getProfBumiActCnt(),
				personVO.getProfActualTotalCnt()));

		// prof actual non bumi
		personVO.setProfNonBumiActPerc(getPercentage(
				personVO.getProfNonBumiActCnt(),
				personVO.getProfActualTotalCnt()));

		// prof actual foreign
		personVO.setProfFrgActPerc(getPercentage(personVO.getProfFrgActCnt(),
				personVO.getProfActualTotalCnt()));

		// semi prof bumi
		personVO.setSemiBumiEnterPerc(getPercentage(
				personVO.getSemiBumiEnterCnt(), personVO.getSemiTotalCnt()));

		// semi prof non bumi
		personVO.setSemiNonBumiEnterPerc(getPercentage(
				personVO.getSemiNonBumiEnterCnt(), personVO.getSemiTotalCnt()));

		// semi prof foreign
		personVO.setSemiFrgEnterPerc(getPercentage(
				personVO.getSemiFrgEnterCnt(), personVO.getSemiTotalCnt()));

		// semi prof actual bumi
		personVO.setSemiBumiActPerc(getPercentage(personVO.getSemiBumiActCnt(),
				personVO.getSemiActualTotalCnt()));

		// semi prof actual non bumi
		personVO.setSemiNonBumiActPerc(getPercentage(
				personVO.getSemiNonBumiActCnt(),
				personVO.getSemiActualTotalCnt()));

		// semi prof actual foreign
		personVO.setSemiFrgActPerc(getPercentage(personVO.getSemiFrgActCnt(),
				personVO.getSemiActualTotalCnt()));

	}

	private static BigDecimal getPercentage(int param, int total) {
		BigDecimal retVal = BigDecimal.ZERO;
		if (total != 0) {
			try {
				retVal = new BigDecimal(param).divide(new BigDecimal(total))
						.multiply(percent);
				retVal = retVal.setScale(2, RoundingMode.CEILING);
			} catch (ArithmeticException e) {
				log.info("FAILED GET PERCENTAG:" + e.getMessage());
			}
		}
		return retVal;
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
		StringBuilder sb = new StringBuilder();
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
	 * Get message from Message Bundle
	 * 
	 * @param String
	 *            msgKeyId
	 * @param msgKeyId
	 *            params
	 * @return String
	 */
	public static String getOutputMsgBundle(String msgKeyId, String[] params) {
		Locale locale = new FacesScopes().getUserLocale();
		String msg = "";
		if (params != null) {
			msg = MessageUtils.getMessage(msgKeyId, params,
					MessageUtils.BUNDLE_COMMON_MESSAGES, locale);
		} else {
			msg = MessageUtils.getMessage(msgKeyId,
					MessageUtils.BUNDLE_COMMON_MESSAGES, locale);
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
		if (compBasic.getPaidUpCapital() != null
				&& compBasic.getPaidUpCapital().compareTo(new BigDecimal(0)) > 0) {
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

	public static String getSerializedFileFullDirByApplNo(String applNo,
			String serType) {
		String retVal = StringUtils.EMPTY;
		boolean exist = isSerFileExist(applNo, serType);
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
		log.info("File Name:" + fileName);
		log.info("SER type:" + serType);
		log.info("Directory:" + baseDir + smRevisionDir);
		log.info("Is File Exist:" + exist);
		if (exist) {
			retVal = baseDir + smRevisionDir + fileName;
		}
		return retVal;
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
	 * Check if combination of business regNo,business type and authority Id
	 * already exist
	 * 
	 * @param String
	 *            regNo
	 * @param String
	 *            businessType
	 * @param Long
	 *            localAuthorityId
	 * @return boolean
	 */
	public static boolean isRegNoAlreadyExist(String regNo,
			String businessType, Long localAuthorityId) {
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setRegNo(regNo);
		reqDTO.setBussinessType(businessType);
		reqDTO.setLocalAuthorityId(localAuthorityId);
		return SmServiceLookup.getSupplierSearchSO()
				.checkIsSupplierRegNoAlreadyExist(reqDTO).isRegNoAlreadyExist();
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
	 * Get SmAddress by addressId
	 * 
	 * @param Long
	 *            smAddressId
	 * @return SmAddress
	 */
	public SmAddress getSmAddressById(Long smAddressId) {
		SmAddress address = null;
		if (smAddressId != null) {
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setSmAddressId(smAddressId);
			address = SmServiceLookup.getMofRegistrationSO()
					.retrieveSmAddressById(reqDTO).getSmAddress();
		}
		return address;
	}

	/**
	 * Get PmMachine by machineId
	 * 
	 * @param Long
	 *            machineId
	 * @return PmMachine
	 */
	public PmMachine getMachineById(Long machineId) {
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setPmMachineId(machineId);
		return SmServiceLookup.getCategoryCodeSO().retrievePmMachine(reqDTO)
				.getPmMachine();
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
		StringBuilder sb = null;
		for (SmPersonnel smPersonnel : smPersonnels) {
			smPersonnel.setRoleName("");
			sb = new StringBuilder();
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
			userId = user.getUserId() != null ? user.getUserId() : 1L;
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
	public static List<WorklistItemVO> getBpmTaskListByAuthToken(Object token) {
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
	 * Get BPM assigned task by doc number
	 * 
	 * @param String
	 *            docNo
	 * @return WorklistItemVO
	 */
	public static WorklistItemVO getBpmAssignedTaskByDocNo(String docNo) {
		WorklistItemVO retVal = null;
		BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
		bpmReqDTO.setDocNumber(docNo);
		bpmReqDTO.setState("ASSIGNED");
		BpmResponseDTO bpmResDTO = (BpmResponseDTO) SmServiceLookup.getBpmSO()
				.getTasks(bpmReqDTO);
		if (bpmResDTO.getWorklistVO() != null) {
			System.out.println("bpmResDTO.getWorklistVO().getTotal(): "
					+ bpmResDTO.getWorklistVO().getTotal());
			for (WorklistItemVO item : bpmResDTO.getWorklistVO()
					.getWorklistItem()) {
				System.out.println("item.getAssignees(): "
						+ item.getAssignees());
			}
		}

		if (bpmResDTO.getWorklistVO() != null
				&& !EntityHelper.isCollectionEmpty(bpmResDTO.getWorklistVO()
						.getWorklistItem())) {
			for (WorklistItemVO item : bpmResDTO.getWorklistVO()
					.getWorklistItem()) {
				if (item.getState().equals("ASSIGNED")) {
					retVal = item;
					break;
				}
			}
		}
		return retVal;
	}

	/**
	 * Get bpm tasklist by login Id
	 * 
	 * @param String
	 *            loginId
	 * @return List<WorklistItemVO>
	 */
	public static List<WorklistItemVO> getBpmTaskListByLoginId(String loginId) {
		List<WorklistItemVO> taskList = new ArrayList<WorklistItemVO>();
		BpmResponseDTO responseDTO = null;
		BpmRequestDTO reqDTO = null;
		reqDTO = new BpmRequestDTO();
		reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
		reqDTO.setOffset(0);
		reqDTO.setUserId(loginId);
		responseDTO = SmServiceLookup.getBpmSO().getReassignedItems(reqDTO);
		if (responseDTO.getWorklistVO() != null
				&& !EntityHelper.isCollectionEmpty(responseDTO.getWorklistVO()
						.getWorklistItem())) {
			taskList = responseDTO.getWorklistVO().getWorklistItem();
		}
		return taskList;
	}

	public static WorklistItemVO getSingleBpmWorkListByTaskTitleAndToken(
			String taskTitle, Object token) {
		WorklistItemVO retVal = null;
		List<WorklistItemVO> taskList = SmCommonUtil
				.getBpmTaskListByAuthToken(token);
		if (taskList != null && !taskList.isEmpty()) {
			for (WorklistItemVO worklistItemVO : taskList) {
				if (worklistItemVO.getTitle().equalsIgnoreCase(taskTitle)) {
					retVal = worklistItemVO;
					break;
				}
			}
		}
		return retVal;
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

		permanent.setPmCountry(SmCommonUtil.getInstance().getCountryById(
				permanent.getPmCountry().getCountryId()));
		permanent.setPmState(SmCommonUtil.getInstance().getStateById(
				permanent.getPmState().getStateId()));
		permanent.setPmDistrict(SmCommonUtil.getInstance().getDistrictById(
				permanent.getPmDistrict().getDistrictId()));
		permanent.setPmCity(SmCommonUtil.getInstance().getCityById(
				permanent.getPmCity().getCityId()));

		if (mailAddress.isSameAsPerm()) {
			SmAddress address = cloneAddress(permAddress.getSmAddress());
			mailAddress.setSmAddress(address);
		} else {
			SmAddress s2 = mailAddress.getSmAddress();
			s2.setPmCountry(SmCommonUtil.getInstance().getCountryById(
					s2.getPmCountry().getCountryId()));
			s2.setPmState(SmCommonUtil.getInstance().getStateById(
					s2.getPmState().getStateId()));
			s2.setPmDistrict(SmCommonUtil.getInstance().getDistrictById(
					s2.getPmDistrict().getDistrictId()));
			s2.setPmCity(SmCommonUtil.getInstance().getCityById(
					s2.getPmCity().getCityId()));
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
	 * Check if site visit after approval application
	 * 
	 * @param SmAppl
	 *            smAppl
	 * @return boolean
	 */
	public static boolean isSiteVisitAfterApproved(SmAppl smAppl) {
		boolean retVal = false;
		SmWorkflowStatus wf = SmCommonUtil.getCurrentActiveWokFlowStatus(smAppl
				.getApplId());
		if (wf != null
				&& wf.getPmStatus() != null
				&& wf.getPmStatus().getStatusId() != null
				&& (wf.getPmStatus()
						.getStatusId()
						.compareTo(
								SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT_AFTER_APPROVED) == 0 || wf
						.getPmStatus()
						.getStatusId()
						.compareTo(
								SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT_REPORT_AFTER_APPROVED) == 0)) {
			retVal = true;

		}
		return retVal;
	}

	/**
	 * Get the latest GST rate from Pm Config Rule Value
	 * 
	 * @return BigDecimal
	 */
	public static BigDecimal getGstCurrentRate() {
		return SmServiceLookup.getSmRuleSO().retrieveGstCurrentRate()
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
		if (StringUtils.isNotBlank(currentUrl)
				&& StringUtils.isNotBlank(layout.getFriendlyURL())) {
			currentUrl = currentUrl.substring(0,
					currentUrl.indexOf(layout.getFriendlyURL()));
		}

		String url = portalUrl + currentUrl;
		return url;
	}

	/**
	 * Get liferay logout url
	 */
	public static String getLogoutUrl() {
		String logoutcookie = "";
		try {
			FacesScopes fs = new FacesScopes();
			FacesContext fCtx = FacesContext.getCurrentInstance();
			PortletRequest req = (PortletRequest) fCtx.getExternalContext()
					.getRequest();
			PortletConfig portletConfig = (PortletConfig) req
					.getAttribute("javax.portlet.config");
			ResourceBundle ePRes = portletConfig.getResourceBundle(fs
					.getUserLocale());
			ThemeDisplay themeDisplay = (ThemeDisplay) req
					.getAttribute(WebKeys.THEME_DISPLAY);
			String currentPortalUrl = themeDisplay.getPortalURL();
			String ssoLogoutUrl = ePRes.getString("hook.sso.logout.url");
			logoutcookie = ssoLogoutUrl + currentPortalUrl + "/c/portal/logout";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>LOGOUT URL:" + logoutcookie);
		return logoutcookie;
	}

	/**
	 * Call deactivation account message dialog
	 * 
	 * @param String
	 *            msgKey
	 */
	public static void callDeactivationSuccessDialog(String msgKey) {
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(msgKey,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
		RequestContext.getCurrentInstance()
				.execute("deactivationDialog.show()");
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
	 * Get application portal base URL from properties
	 * 
	 * @return String
	 */
	public static String getBasePortalAppUrlFromProperties() {
		return EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.PORTAL_APPLICATION_URL);
	}

	/**
	 * Show faces message based on severity level and component id (optional).
	 * 
	 * @param FacesContext
	 *            fc
	 * @param Severity
	 *            severity
	 * @param String
	 *            msg
	 * @param String
	 *            componentId
	 */
	public static void showFacesMsg(FacesContext fc, Severity severity,
			String msg, String componentId) {
		fc.addMessage(componentId, new FacesMessage(severity, null, msg));
	}

	/**
	 * Get states based on Country id.
	 * 
	 * @param countryId
	 *            - Long
	 * @return List<SelectItem> - contains the list of states
	 */
	public static List<SelectItem> getCountryList() {
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();
		List<SelectItem> countryList = new ArrayList<SelectItem>();
		for (PmCountry country : resDTO.getCountryList()) {
			SelectItem si = new SelectItem();
			si.setLabel(country.getCountryName());
			si.setValue(country.getCountryId());
			countryList.add(si);
		}
		return countryList;
	}

	/**
	 * Get salutation for select item drop down
	 * 
	 * @return List<SelectItem>
	 */
	public static List<SelectItem> getSalutationItems() {
		List<PmSalutation> salutationList = PmCommonUtil
				.retrievePmSalutationList();
		List<SelectItem> salutationSelectItems = new ArrayList<SelectItem>();
		if (!SmCommonUtil.isCollectionEmpty(salutationList)) {
			for (PmSalutation salutation : salutationList) {
				SelectItem item = new SelectItem();
				item.setLabel(salutation.getSalutationName());
				item.setValue(salutation.getSalutationId());
				salutationSelectItems.add(item);
			}
		}
		return salutationSelectItems;
	}

	/**
	 * Get supplier bank by revision no and account no
	 * 
	 * @param List
	 *            <SmSupplierBank> mainList
	 * @param SmSupplierBank
	 *            supplierBankParam
	 * @param int revNo
	 * @return SmSupplierBank
	 */
	public static SmSupplierBank getSupplierBankByRevNoAndAccNo(
			List<SmSupplierBank> mainList, SmSupplierBank param, int revNo) {
		SmSupplierBank retVal = param;

		// main list loop by accNo and recordStatus and revNo
		for (SmSupplierBank tmp : mainList) {
			if (tmp.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED
					&& StringUtils.isNotBlank(tmp.getAccountNo())
					&& tmp.getAccountNo().equals(param.getAccountNo())
					&& StringUtils.isNotBlank(tmp.getAccountPurpose())
					&& tmp.getAccountPurpose()
							.equals(param.getAccountPurpose())) {

				if (tmp.getRevNo() == revNo) {
					retVal = tmp;
					break;
				}
			}
		}

		return retVal;
	}

	public static SmStaffTotal getStaffTotalByRevNoAndCategory(
			List<SmStaffTotal> list, SmStaffTotal param, int revNo) {
		SmStaffTotal retVal = param;

		for (SmStaffTotal tmp : list) {
			if (tmp.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED
					&& StringUtils.isNotBlank(tmp.getStaffCategory())
					&& tmp.getStaffCategory().equals(param.getStaffCategory())
					&& StringUtils.isNotBlank(tmp.getStaffCategory())
					&& tmp.getStaffCategory().equals(param.getStaffCategory())) {

				if (tmp.getRevNo().compareTo(new BigDecimal(revNo)) == 0) {
					retVal = tmp;
					break;
				}
			}
		}
		return retVal;
	}

	/**
	 * Get company basic by revision no
	 * 
	 * @param List
	 *            <SmCompanyBasic> mainList
	 * @param int revNo
	 * @return SmCompanyBasic
	 */
	public static SmCompanyBasic getCompanyBasicByRevNo(
			List<SmCompanyBasic> mainList, int revNo) {
		SmCompanyBasic retValCompanyBasic = null;

		for (SmCompanyBasic companyBasic : mainList) {
			if (companyBasic.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
				if (companyBasic.getRevNo() == revNo) {
					retValCompanyBasic = companyBasic;
					break;
				} else {
					retValCompanyBasic = companyBasic;
				}
			}
		}
		return retValCompanyBasic;
	}

	/**
	 * Get personnel by identification no and revision no
	 * 
	 * @param List
	 *            <SmPersonnel> mainList
	 * @param SmPersonnel
	 *            smPersonnelParam
	 * @param int revNo
	 * @return SmPersonnel
	 */
	public static SmPersonnel getSmPersonnelByRevNoAndIdentificationNo(
			List<SmPersonnel> mainList, SmPersonnel param, int revNo) {
		SmPersonnel retVal = param;

		// main list loop by identificationNo and recordStatus and revNo
		for (SmPersonnel tmp : mainList) {
			if (tmp.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED
					&& StringUtils.isNotBlank(tmp.getIdentificationNo())
					&& tmp.getIdentificationNo().equals(
							param.getIdentificationNo())) {
				if (tmp.getRevNo() == revNo) {
					retVal = tmp;
					break;
				}
			}
		}

		return retVal;
	}

	/**
	 * Get check signatory by identification no and revision no
	 * 
	 * @param List
	 *            <SmChequeSignatory> mainList
	 * @param SmChequeSignatory
	 *            param
	 * @param int revNo
	 * @return SmChequeSignatory
	 */
	public static SmChequeSignatory getSmChequeSignatoryByRevNoAndIdentificationNo(
			List<SmChequeSignatory> mainList, SmChequeSignatory param, int revNo) {
		SmChequeSignatory retVal = param;

		// main list loop by identificationNo and recordStatus and revNo
		for (SmChequeSignatory tmp : mainList) {
			if (tmp.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED
					&& tmp.getSmPersonnel() != null
					&& StringUtils.isNotBlank(tmp.getSmPersonnel()
							.getIdentificationNo())
					&& tmp.getSmPersonnel()
							.getIdentificationNo()
							.equals(param.getSmPersonnel()
									.getIdentificationNo())) {
				if (tmp.getSmPersonnel().getRevNo() == revNo) {
					retVal = tmp;
					break;
				}
			}
		}

		return retVal;
	}

	/**
	 * Get associate company by registration no and revision no
	 * 
	 * @param List
	 *            <SmAssociateCompany> mainList
	 * @param SmAssociateCompany
	 *            smAssociateCompanyParam
	 * @param int revNo
	 * @return SmAssociateCompany
	 */
	public static SmAssociateCompany getSmAssociateCompanyByRevNoAndRegNo(
			List<SmAssociateCompany> mainList, SmAssociateCompany param,
			int revNo) {
		SmAssociateCompany retVal = param;

		// main list loop by regNo and recordStatus and revNo
		for (SmAssociateCompany tmp : mainList) {
			if (tmp.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED
					&& StringUtils.isNotBlank(tmp.getRegNo())
					&& tmp.getRegNo().equals(param.getRegNo())) {
				if (tmp.getRevNo() == revNo) {
					retVal = tmp;
					break;
				}
			}
		}

		return retVal;
	}

	/**
	 * Get category code by level 3 code and revision no
	 * 
	 * @param List
	 *            <SmSupplierCategory> mainList
	 * @param SmSupplierCategory
	 *            param
	 * @param int revNo
	 * @return SmSupplierCategory
	 */
	public static SmSupplierCategory getSmSupplierCategoryByRevNoAndCodeLvl(
			List<SmSupplierCategory> mainList, SmSupplierCategory param,
			int revNo) {
		SmSupplierCategory retVal = param;

		// main list loop by regNo and recordStatus and revNo
		for (SmSupplierCategory tmp : mainList) {
			if (tmp.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED
					&& (tmp.getPmCategoryL3() != null && tmp
							.getPmCategoryL3()
							.getCategoryL3Id()
							.compareTo(
									param.getPmCategoryL3().getCategoryL3Id()) == 0)) {
				if (tmp.getRevNo() == revNo) {
					retVal = tmp;
					break;
				}
			}
		}

		return retVal;
	}

	/**
	 * Get supplier branch by name and revision no
	 * 
	 * @param List
	 *            <SmSupplierBranch> mainList
	 * @param SmSupplierBranch
	 *            param
	 * @param int revNo
	 * @return SmSupplierBranch
	 */
	public static SmSupplierBranch getSmSupplierBranchByRevNoAndName(
			List<SmSupplierBranch> mainList, SmSupplierBranch param, int revNo) {
		SmSupplierBranch retVal = param;

		// main list loop by name,address and revNo
		for (SmSupplierBranch tmp : mainList) {
			if (tmp.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED
					&& StringUtils.isNotBlank(tmp.getBranchName())
					&& tmp.getBranchName().equals(param.getBranchName())) {
				if (tmp.getSmAddress() != null
						&& param.getSmAddress() != null
						&& !isAddressHasDifferences(param.getSmAddress(),
								tmp.getSmAddress()) && tmp.getRevNo() == revNo) {
					retVal = tmp;
					break;
				}
			}
		}

		return retVal;
	}

	/**
	 * post Back to payment gatewway
	 * 
	 * @param httpServletRequest
	 * @param isParamRequired
	 * @param merchantId
	 * @param molIpnUrl
	 * @throws Exception
	 */
	public static void paymentCurlIPN(HttpServletRequest httpServletRequest,
			boolean isParamRequire, String merchantId, String molIPNUrl)
			throws Exception {
		Enumeration<String> resParameters = httpServletRequest
				.getParameterNames();
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		StringBuilder strParams = new StringBuilder();

		if (isParamRequire) {
			while (resParameters.hasMoreElements()) {
				String paramName = (String) resParameters.nextElement();
				// params.add(new BasicNameValuePair(paramName,
				// httpServletRequest.getParameter(paramName)));
				strParams.append(paramName + "="
						+ httpServletRequest.getParameter(paramName));
				strParams.append("&");
				/*
				 * log.info(paramName + " - " +
				 * httpServletRequest.getParameter(paramName));
				 */
			}
		}
		strParams.append("treq=1");
		strParams.append("&");
		strParams.append("sa=" + merchantId);
		log.info("StrParams>>>>>>" + strParams);
		// URL url = null;
		// BufferedReader reader = null;

		try {
			// create the HttpURLConnection
			URL url = new URL(molIPNUrl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			// just want to do an HTTP POST here
			connection.setRequestMethod("POST");

			// uncomment this if you want to write output to this url
			connection.setDoOutput(true);

			OutputStream os = connection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					os, "UTF-8"));
			writer.write(strParams.toString());
			writer.flush();
			writer.close();
			os.close();
			// give it 15 seconds to respond

			connection.setReadTimeout(15 * 1000);
			connection.connect();

			StringBuilder stringBuilder; // read the output from the server
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			stringBuilder = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			log.info(">>>" + stringBuilder.toString());

		} catch (Exception e) {
			e.printStackTrace();
			// throw e;
		}
	}

	/**
	 * Get application that still active by supplier
	 * 
	 * @param SmSupplier
	 *            supplier
	 * @return SmAppl
	 */
	public static SmAppl getActiveApplBySupplier(SmSupplier supplier) {
		SmAppl activeAppl = null;
		if (supplier != null) {
			MofRegistrationReqDTO mofRegistrationReqDTO = new MofRegistrationReqDTO();
			mofRegistrationReqDTO.setSupplierId(supplier.getSupplierId());
			activeAppl = SmServiceLookup.getMofRegistrationSO()
					.retrieveActiveSmApplBySupp(mofRegistrationReqDTO)
					.getSmAppl();
		}
		return activeAppl;
	}

	/**
	 * Check whether soft cert is ready for renewal
	 * 
	 * @param Long
	 *            supplierId
	 * @param SmPersonnel
	 *            personnel
	 * @return boolean
	 */
	public static boolean isSoftcertReadyForRenewal(Long supplierId,
			SmPersonnel personnel) {
		boolean retVal = false;
		if (supplierId != null
				&& personnel != null
				&& personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_ISSUED
				&& personnel.getPmUser() != null
				&& personnel.getAuthorizedFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setSmSupplierId(supplierId);
			reqDTO.setUserId(personnel.getPmUser().getUserId());
			SmSoftcertRequest softcertRequest = SmServiceLookup
					.getPersonnelSO()
					.retrievePersonnelSoftcertRequestBySupplierIdAndUserId(
							reqDTO).getSoftcertRequest();

			if (softcertRequest != null
					&& softcertRequest.getPmDigiCert() != null
					&& softcertRequest.getPmDigiCert().getValidTo() != null) {
				Calendar renewDate = Calendar.getInstance();
				renewDate.setTime(softcertRequest.getPmDigiCert().getValidTo());
				renewDate.add(Calendar.MONTH, -3);
				if (DateUtils.isDateGreaterOrEqual(new Date(),
						renewDate.getTime())) {
					retVal = true;
				}
			}

		}
		return retVal;
	}

	/**
	 * Get user by userId
	 * 
	 * @param userId
	 * @return PmUser
	 */
	public static PmUser getUserByUserId(Long userId) {
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(userId);
		UserResDTO resDTO = SmServiceLookup.getUserSO().retrieveUserByUserId(
				reqDTO);
		return resDTO.getPmUser();
	}

	/**
	 * Get drop down value for display purpose by code
	 * 
	 * @param String
	 *            parameterType
	 * @param String
	 *            parameterCode
	 * @return String
	 */
	public static String getDropDownViewOnlyValueByCode(String parameterType,
			String parameterCode) {
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescListBySearchBean(parameterType, null);
		String retVal = "";
		for (PmParameterDesc parameterDesc : parameterDescs) {

			if (parameterCode.equals(parameterDesc.getPmParameter()
					.getParameterCode())) {
				retVal = parameterDesc.getCodeName();
			}
		}

		return retVal;
	}

	/**
	 * Get SmMofAccount by Supplier Id and Supplier Type
	 * 
	 * @param Long
	 *            supplierId
	 * @param String
	 *            suppType
	 * @return SmMofAccount
	 */
	public static SmMofAccount getMofAccountBySuppIdAndSuppType(
			Long supplierId, String suppType) {
		SmMofAccount mofAcc = null;
		if (supplierId != null && StringUtils.isNotBlank(suppType)) {
			MofAccountReqDTO reqDTO = new MofAccountReqDTO();
			reqDTO.setSupplierType(suppType);
			reqDTO.setSupplierId(supplierId);
			mofAcc = SmServiceLookup.getMofAccountSO()
					.retriveMOFAccountBySupplierIdAndType(reqDTO)
					.getMofAccount();
		}
		return mofAcc;
	}

	/**
	 * Get latest revision company basic by application id
	 * 
	 * @param Long
	 *            applId
	 * @return SmCompanyBasic
	 */
	public static SmCompanyBasic getLatestRevCompBasicByApplId(Long applId) {
		SmCompanyBasic activeCompanyBasic = new SmCompanyBasic();
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplId(applId);
		List<SmCompanyBasic> compBasicList = SmServiceLookup
				.getMofRegistrationSO().retrieveSmCompanyBasicByApplId(reqDTO)
				.getCompanyBasicList();
		activeCompanyBasic = SmCommonUtil.getCompanyBasicByRevNo(compBasicList,
				2);

		if (activeCompanyBasic.getTotalMember() != null
				&& activeCompanyBasic.getTotalMember().compareTo(
						BigDecimal.ZERO) != 0) {
			activeCompanyBasic.setTotalMember(activeCompanyBasic
					.getTotalMember().setScale(0, BigDecimal.ROUND_DOWN));
		}
		return activeCompanyBasic;
	}

	/**
	 * @param Long
	 *            supplierId
	 * @param Long
	 *            branchCode
	 * @return SmGstSupplier
	 */
	public static SmGstSupplier getSmGstSupplierBySupplierIdAndBranchCode(
			Long supplierId, Long branchCode, String applNo, boolean isCurrent) {
		SupplierGstReqDTO gstReqDTO = new SupplierGstReqDTO();
		gstReqDTO.setSupplierId(supplierId);
		gstReqDTO.setBranchCode(branchCode);
		gstReqDTO.setApplNo(applNo);
		gstReqDTO.setCurrent(isCurrent);
		return SmServiceLookup
				.getSupplierGstSO()
				.retrieveGstInfoBySupplierIdAndBranchCodeAndIsCurrent(gstReqDTO)
				.getSmGstSupplier();
	}

	/**
	 * Remove not uploaded gst supporting doc file from list
	 * 
	 * @param SmGstSupplier
	 *            gst
	 */
	public static void removeNotUploadedGstSuppDoc(SmGstSupplier gst) {
		if (gst != null && gst.getSmGstSupplierDocs() != null) {
			Iterator<SmGstSupplierDoc> iter = gst.getSmGstSupplierDocs()
					.iterator();
			while (iter.hasNext()) {
				SmGstSupplierDoc doc = iter.next();
				if (StringUtils.isBlank(doc.getFileName())
						|| StringUtils.isBlank(doc.getFilePath())) {
					iter.remove();
				}
			}
		}
	}

	/**
	 * Copy HQ GST information to Branch
	 * 
	 * @param SmGstSupplier
	 *            hqGst
	 * @param SmGstSupplier
	 *            branchGst
	 * @param boolean isSameWithHq
	 * @param Long
	 *            branchCode
	 */
	public static void copyHqGstToBranch(SmGstSupplier hqGst,
			SmGstSupplier branchGst, boolean isSameWithHq, Long branchCode) {
		if (hqGst != null) {
			if (isSameWithHq) {
				branchGst.setGstRegNo(hqGst.getGstRegNo());
				branchGst.setGstEffDate(hqGst.getGstEffDate());
				branchGst.setGstEndDate(hqGst.getGstEndDate());
				branchGst.setIsCurrent(hqGst.getIsCurrent());
			} else {
				branchGst.setGstRegNo(null);
				branchGst.setGstEffDate(null);
				branchGst.setGstEndDate(null);
				branchGst.setIsCurrent(false);
			}
			branchGst.setBranchCode(branchCode);
			branchGst.setSmGstSupplierDocs(new ArrayList<SmGstSupplierDoc>());
			copyHqSuppDocToBranch(hqGst, branchGst, isSameWithHq);
		}
	}

	/**
	 * Reset GST effective date to first of month
	 * 
	 * @param SmGstSupplier
	 *            gst
	 */
	public static void resetGstEffectiveDate(SmGstSupplier gst) {
		Calendar c = Calendar.getInstance();
		c.setTime(gst.getGstEffDate());
		c.set(Calendar.DATE, 1);
		gst.setGstEffDate(c.getTime());
	}

	private static void copyHqSuppDocToBranch(SmGstSupplier hqGst,
			SmGstSupplier branchGst, boolean isSameWithHq) {
		if (isSameWithHq) {
			for (SmGstSupplierDoc brDoc : branchGst.getSmGstSupplierDocs()) {
				for (SmGstSupplierDoc hqDoc : hqGst.getSmGstSupplierDocs()) {
					if (brDoc.getPmDocument() != null
							&& brDoc.getPmDocument()
									.getDocumentId()
									.compareTo(
											hqDoc.getPmDocument()
													.getDocumentId()) == 0) {
						brDoc.setFileName(hqDoc.getFileName());
						brDoc.setFilePath(hqDoc.getFilePath());
					}
				}
			}
		} else {
			if (branchGst.getSmGstSupplierDocs() == null
					|| branchGst.getSmGstSupplierDocs().isEmpty()) {
				SmApplicationUtil.initGstSuppDocs(branchGst);
			}
		}
	}

	public static void processUpload(String uploadDir, FileUploadEvent event,
			String fileName) {

		String finalDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		finalDir += "/" + uploadDir;

		File dir = new File(finalDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		try {
			fileName = StringUtils.replace(fileName, " ", "-");

			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
					new FileOutputStream(finalDir + "/" + fileName));

			bufferedOutputStream.write(event.getFile().getContents());

			if (bufferedOutputStream != null) {
				bufferedOutputStream.flush();
				bufferedOutputStream.close();
			}

		} catch (Exception e) {
			throw new EpException("ERROR PROCESSING UPLOADED FILE:"
					+ e.getMessage());
		}

	}

	public static String getUploadFilePath(String path) {
		return Calendar.getInstance().get(Calendar.YEAR) + path;
	}

	/**
	 * Return softcert doc no
	 * 
	 * @param SmAppl
	 *            latestAppl
	 * @return String
	 */
	public static String getSoftcertDocNo(SmAppl latestAppl) {
		if (latestAppl == null) {
			throw new EpException("Not valid login user!!");
		}
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setAppl(latestAppl);
		return SmServiceLookup.getPersonnelSO().getSoftcertDocNo(reqDTO)
				.getSoftcertDocNo();
	}

	/**
	 * Set rejected softcert reason
	 * 
	 * @param SmPersonnel
	 *            personnel
	 * @param Long
	 *            supplierId
	 */
	public static void setRejectedPersonnelSoftcert(SmPersonnel personnel,
			Long supplierId) {
		if (supplierId != null
				&& personnel != null
				&& personnel.getPmUser() != null
				&& personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REJECTED) {
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setSmSupplierId(supplierId);
			reqDTO.setUserId(personnel.getPmUser().getUserId());
			SmSoftcertRequest softcertRequest = SmServiceLookup
					.getPersonnelSO()
					.retrievePersonnelSoftcertRequestBySupplierIdAndUserId(
							reqDTO).getSoftcertRequest();
			if (softcertRequest != null
					&& StringUtils.isNotBlank(softcertRequest.getReasonCode())) {
				if (softcertRequest.getReasonCode().equals(
						SupplierManagementConst.REJECTED_WITH_BLACKLIST)) {
					personnel.setSoftcertRejectReason(SmCommonUtil.getMessage(
							"lbl.sm.softcert.rejected.blacklist", null));
				} else if (softcertRequest.getReasonCode().equals(
						SupplierManagementConst.REJECTED_WITH_FRAUD)) {
					personnel.setSoftcertRejectReason(SmCommonUtil.getMessage(
							"lbl.sm.softcert.rejected.fraud", null));
				} else if (softcertRequest
						.getReasonCode()
						.equals(SupplierManagementConst.REJECTED_WITH_INCOMPLETE_SUPPDOC)) {
					personnel
							.setSoftcertRejectReason(SmCommonUtil
									.getMessage(
											"lbl.sm.softcert.rejected.incomplete.suppdoc",
											null));
				} else if (softcertRequest
						.getReasonCode()
						.equals(SupplierManagementConst.REJECTED_WITH_NO_CONFIG_CHALLENGE_RESPONSE)) {
					personnel
							.setSoftcertRejectReason(SmCommonUtil
									.getMessage(
											"lbl.sm.softcert.rejected.noconfig.challresp",
											null));
				}
			}
		}
	}

	public static String getOwnershipTypeLabel(String ownershipType) {
		if (StringUtils.isBlank(ownershipType)) {
			return getMessage("lbl.sm.no.available", null);
		} else {
			if (ownershipType.equalsIgnoreCase("O")) {
				return getMessage("lbl.sm.facility.own", null);
			} else {
				return getMessage("lbl.sm.facility.rent", null);
			}
		}
	}

	public static String getFacTypeLabel(String facType) {
		if (StringUtils.isBlank(facType)) {
			return getMessage("lbl.sm.no.available", null);
		} else {
			if (facType.equalsIgnoreCase("H")) {
				return getMessage("lbl.sm.facility.facilityType.home.office",
						null);
			} else {
				return getMessage(
						"lbl.sm.facility.facilityType.business.office", null);
			}
		}
	}

	public static String getPriceAmountFormat(BigDecimal priceAmount) {
		String retVal = "";
		if (priceAmount != null) {
			priceAmount = priceAmount.setScale(2, RoundingMode.CEILING);
			retVal = priceAmount.toString();
		}
		return retVal;
	}

	public static String getDisplayDateFormat(Date date) {
		String retVal = StringUtils.EMPTY;
		if (date != null) {
			retVal = DateUtils.dateToString("dd/MM/yyyy", date);
		}
		return retVal;
	}

	public PmCountry getCountryById(Long countryId) {
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryId(countryId);
		return SmServiceLookup.getCountrySO().retrieveCountryById(reqDTO)
				.getPmCountry();
	}

	public PmState getStateById(Long stateId) {
		if (stateId != null) {
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setStateId(stateId);
			return SmServiceLookup.getStateSO().retriveStateByID(reqDTO)
					.getPmState();
		} else {
			return null;
		}
	}

	public PmDistrict getDistrictById(Long districtId) {
		if (districtId != null) {
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			reqDTO.setDistrictId(districtId);
			return SmServiceLookup.getDistrictSO().retriveDistrictByID(reqDTO)
					.getPmDistrict();
		} else {
			return null;
		}
	}

	public PmCity getCityById(Long cityId) {
		if (cityId != null) {
			CityReqDTO reqDTO = new CityReqDTO();
			reqDTO.setCityId(cityId);
			return SmServiceLookup.getCitySO().retriveCityById(reqDTO)
					.getPmCity();
		} else {
			return null;
		}
	}

	public static String getCountryLabelById(Long countryId) {
		String name = "";
		if (countryId != null) {
			PmCountry country = getInstance().getCountryById(countryId);
			if (country != null) {
				name = country.getCountryName();
			}
		}
		return name;
	}

	public static String getStateLabelById(Long stateId) {
		String name = "";
		if (stateId != null) {
			PmState state = getInstance().getStateById(stateId);
			if (state != null) {
				name = state.getStateName();
			}
		}
		return name;
	}

	public static String getDistrictLabelById(Long districtId) {
		String name = "";
		if (districtId != null) {
			PmDistrict district = getInstance().getDistrictById(districtId);
			if (district != null) {
				name = district.getDistrictName();
			}
		}
		return name;
	}

	public static String getCityLabelById(Long cityId) {
		String name = "";
		if (cityId != null) {
			PmCity city = getInstance().getCityById(cityId);
			if (city != null) {
				name = city.getCityName();
			}
		}
		return name;
	}

	public static String getRaceByIdFromSelectItems(Long raceId) {
		String retVal = "";
		for (SelectItem tmp : SmApplicationUtil.getRaceSelectItems()) {
			if (tmp.getValue().equals(raceId)) {
				retVal = tmp.getLabel();
				break;
			}
		}
		return retVal;
	}

	public static PmRace getPmRaceById(Long raceId) {
		PmRace retVal = null;
		if (raceId != null) {
			for (PmRace race : SmApplicationUtil.retrievePmRaceList()) {
				if (race.getRaceId().equals(raceId)) {
					retVal = race;
					break;
				}
			}
		}
		return retVal;
	}

	public static boolean isCollectionEmpty(Collection<?> value) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		return false;
	}

	public static void checkEmailDomainWithExceptionMsg(String emailOwner,
			String email) {
		// if (StringUtils.isNotBlank(email) && !isValidEmailDomain(email)) {
		// throw new EpException(MessageConst.ERROR_721115, new String[] {
		// email, emailOwner });
		// }
	}

	public static boolean isValidEmailDomain(String email) {
		boolean retVal = false;
		String[] hostNameArr = StringUtils.isNotBlank(email) ? email.split("@")
				: new String[] {};
		try {
			if (hostNameArr != null && hostNameArr.length > 1) {
				Hashtable env = new Hashtable();
				env.put("java.naming.factory.initial",
						"com.sun.jndi.dns.DnsContextFactory");
				DirContext ictx = new InitialDirContext(env);
				Attributes attrs = ictx.getAttributes(hostNameArr[1],
						new String[] { "NS" });
				Attribute attr = attrs.get("NS");
				ArrayList res = new ArrayList();

				if (attr != null) {
					NamingEnumeration en = attr.getAll();
					while (en.hasMore()) {
						String x = (String) en.next();
						if (x.endsWith("."))
							x = x.substring(0, x.length() - 1);
						res.add(x);
					}
				}

				if (!isCollectionEmpty(res)) {
					log.info("Email " + email + " name servers:" + res);
					retVal = true;
				} else {
					log.info("Email " + email
							+ "dont have any name servers exist!!!");
				}
			}
		} catch (Exception e) {
			log.info(">>>>>>>>>>>>>>ERROR EMAIL LOOKUP[" + email + "]"
					+ e.getMessage() + "<<<<<<<<<<<<<");
		}
		return retVal;
	}

	/**
	 * Get business type select items
	 * 
	 * @param String
	 *            businessType
	 * @param String
	 *            suppType
	 * @param String
	 *            applType
	 * @return
	 */
	public List<SelectItem> getBusinessTypeItems(String businessType,
			String suppType, String applType) {
		List<SelectItem> itemList = new ArrayList<SelectItem>();
		try {
			SelectItem[] items = getDropdownCodeValuesBySearchBean(
					SupplierManagementConst.SUPPLIER_BUSINESS_TYPE, null);

			for (SelectItem selectItem : items) {
				if (StringUtils.isNotBlank(businessType)) {
					if (selectItem.getValue() != null) {
						if (businessType
								.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
								|| businessType
										.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)) {

							if (selectItem
									.getValue()
									.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
									|| selectItem
											.getValue()
											.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)) {
								itemList.add(selectItem);
							}
						} else if (businessType
								.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)
								|| businessType
										.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK)) {
							if (selectItem
									.getValue()
									.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)
									|| selectItem
											.getValue()
											.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK)) {
								itemList.add(selectItem);
							}
						} else if (businessType
								.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)
								|| businessType
										.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)) {
							if (selectItem
									.getValue()
									.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)
									|| selectItem
											.getValue()
											.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)) {
								itemList.add(selectItem);
							}
						} else if (businessType
								.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD)
								|| businessType
										.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD)) {
							if (selectItem
									.getValue()
									.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD)
									|| selectItem
											.getValue()
											.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD)) {
								itemList.add(selectItem);
							}
						}
					}
				} else {
					if (applType.equals(SupplierManagementConst.ACC_BASIC)) {
						itemList.add(selectItem);
					} else if (applType
							.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)
							&& suppType != null) {
						if (suppType
								.equals(SupplierManagementConst.STYPE_CONTRACTOR)) {
							if (selectItem.getValue() == null
									|| !selectItem
											.getValue()
											.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL)) {
								itemList.add(selectItem);
							}
						} else if (suppType
								.equals(SupplierManagementConst.STYPE_CONSULTANT)) {
							if (selectItem.getValue() == null
									|| (!selectItem
											.getValue()
											.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN)
											&& !selectItem
													.getValue()
													.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI)
											&& !selectItem
													.getValue()
													.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN) && !selectItem
											.getValue()
											.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL))) {
								itemList.add(selectItem);
							}
						}
					} else if (suppType == null
							&& applType
									.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
						if (selectItem.getValue() == null) {
							itemList.add(selectItem);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_721010, new String[] {
					e.toString(), e.getMessage() });

		}
		return itemList;
	}

	/**
	 * Get dropdown code values by search bean
	 * 
	 * @param String
	 *            parameterType
	 * @param ISearchBean
	 *            <PmParameterDesc> searchBean
	 * @return SelectItem[]
	 */
	public SelectItem[] getDropdownCodeValuesBySearchBean(String parameterType,
			ISearchBean<PmParameterDesc> searchBean) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper
					.getInstance().getParameterDescListBySearchBean(
							parameterType, searchBean);

			items = new SelectItem[parameterDescs.size() + 1];

			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				items[i++] = new SelectItem(parameterDesc.getPmParameter()
						.getParameterCode(), parameterDesc.getCodeName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}

	/**
	 * Get machine items by category type code except for type printing
	 * 
	 * @param String
	 *            catTypeCode
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getMachineItemsByCatTypeCode(String catTypeCode) {
		List<SelectItem> machineItems = new ArrayList<SelectItem>();
		String[] categoryInMachineSection = new String[] {
				ParameterConst.CATEGORY_TYPE_DOCKYARD,
				ParameterConst.CATEGORY_TYPE_TRAVEL,
				ParameterConst.CATEGORY_TYPE_WORKSHOP };
		if (StringUtils.isNotBlank(catTypeCode)
				&& Arrays.asList(categoryInMachineSection)
						.contains(catTypeCode)) {
			PmParameter categoryType = new PmParameter();
			categoryType.setParameterCode(catTypeCode);
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			reqDTO.setCategoryTypeCode(categoryType);
			List<PmMachine> machineList = SmServiceLookup.getCategoryCodeSO()
					.retrievePmMachineByParameterCategoryType(reqDTO)
					.getMachineList();
			if (!SmCommonUtil.isCollectionEmpty(machineList)) {
				SelectItem machineItem = null;
				for (PmMachine machine : machineList) {
					machineItem = new SelectItem();
					machineItem.setValue(machine.getMachineId());
					machineItem.setLabel(machine.getMachineDesc());
					machineItems.add(machineItem);
				}
			}
		}
		return machineItems;
	}

	/**
	 * Retrieve machine list for printing category type
	 * 
	 * @param PmCategoryL3
	 *            categoryCode
	 * @return List<SelectItem>
	 */
	public static List<SelectItem> getPrintingMachineItemsByCategoryCode(
			PmCategoryL3 categoryCode) {
		List<PmMachine> machines = new ArrayList<PmMachine>();
		List<SelectItem> machineItems = new ArrayList<SelectItem>();
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		if (categoryCode.getCategoryTypeCode() != null
				&& categoryCode.getCategoryTypeCode().getParameterCode()
						.equals(ParameterConst.CATEGORY_TYPE_PRINTING)
				&& categoryCode != null
				&& categoryCode.getPmCategoryL2() != null
				&& categoryCode.getPmCategoryL2().getPmCategoryL1() != null) {
			reqDTO.setCategoryL1Id(categoryCode.getPmCategoryL2()
					.getPmCategoryL1().getCategoryL1Id());
			reqDTO.setCategoryL2Id(categoryCode.getPmCategoryL2()
					.getCategoryL2Id());
			reqDTO.setCategoryL3Id(categoryCode.getCategoryL3Id());
			machines = SmServiceLookup.getCategoryCodeSO()
					.retrievePmMachineByCategoryCodeId(reqDTO).getMachineList();
			if (!EntityHelper.isCollectionEmpty(machines)) {
				for (PmMachine mac : machines) {
					SelectItem macItem = new SelectItem();
					macItem.setLabel(mac.getMachineDesc());
					macItem.setValue(mac.getMachineId());
					machineItems.add(macItem);
				}
			}
		}
		return machineItems;
	}

	public static boolean isSmObjectPropsDifference(Object latest,
			Object current, String[] excludeProps) {
		boolean retVal = false;
		if (latest != null
				&& current != null
				&& (current.getClass().getName().equals(latest.getClass()
						.getName()))) {
			Class<?> object = latest.getClass();
			List<String> excludeList = new ArrayList<String>();
			excludeList.add("serialVersionUID");
			excludeList.add("changedFieldStatus");
			excludeList.add("createdDate");
			excludeList.add("createdBy");
			excludeList.add("changedDate");
			excludeList.add("changedBy");
			excludeList.add("revNo");
			excludeList.add("smAppl");
			excludeList.add("smAddress");
			if (excludeProps != null && excludeProps.length > 0) {
				excludeList.addAll(Arrays.asList(excludeProps));
			}
			Field[] attributes = object.getDeclaredFields();
			Object latestVal = null;
			Object currVal = null;
			for (Field field : attributes) {
				if (!excludeList.contains(field.getName())) {
					if (field.getGenericType() instanceof ParameterizedType == false) {
						try {
							latestVal = PropertyUtils.getProperty(latest,
									field.getName());
							currVal = PropertyUtils.getProperty(current,
									field.getName());
							if (BigDecimal.class.equals(field.getType())) {
								if (currVal != null) {
									currVal = new BigDecimal(currVal.toString())
											.setScale(2, BigDecimal.ROUND_UP);
								}
								if (latestVal != null) {
									latestVal = new BigDecimal(
											latestVal.toString()).setScale(2,
											BigDecimal.ROUND_UP);
								}
							}
							if (latestVal == null
									|| StringUtils
											.isBlank(latestVal.toString())) {
								if (currVal != null
										&& StringUtils.isNotBlank(currVal
												.toString())) {
									retVal = true;
									break;
								}
							} else {
								if (!latestVal.equals(currVal)) {
									retVal = true;
									break;
								}
							}
						} catch (IllegalAccessException
								| InvocationTargetException
								| NoSuchMethodException e) {
							log.info(e.getMessage());
						}
					}
				}
			}
		}
		return retVal;
	}

	/**
	 * Get the instance of smCommonUtil
	 * 
	 * @return SmCommonUtil
	 */
	public static SmCommonUtil getInstance() {
		if (instance == null) {
			try {
				instance = new SmCommonUtil();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return instance;

	}

	/**
	 * Retrieve PmStatus By Id
	 */
	public PmStatus retrievePmStatusById(Long statusId) {
		StatusReqDTO reqDTO = new StatusReqDTO();
		IStatusSO reqSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		reqDTO.setStatusId(statusId);
		StatusResDTO resDTO = reqSO.retrieveStatusByStatusId(reqDTO);

		return resDTO.getPmStatus();
	}

	/**
	 * Get personnel latest active role in ePerolehan
	 * 
	 * @param SmPersonnel
	 *            personnel
	 * @return String
	 */
	public static String getPersonnelActiveRole(SmPersonnel personnel) {
		String retVal = "";
		if (personnel.getPmUser() != null
				&& personnel.getPmUser().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
			PmUserOrg userOrg = getSupplierPmUserOrg(personnel);
			if (userOrg != null
					&& userOrg.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
					&& !EntityHelper
							.isCollectionEmpty(userOrg.getPmUserRoles())) {
				for (PmUserRole userRole : userOrg.getPmUserRoles()) {
					if (userRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
						retVal = userRole.getPmRole().getRoleCode();
						break;
					}
				}
			}
		}
		return retVal;
	}

	/**
	 * Get local authority select items by business type and address
	 * 
	 * @param String
	 *            businessType
	 * @param SmAddress
	 *            smAddress
	 * @return List<SelectItem>
	 */
	public static List<SelectItem> getLocalAuthorityItems(String businessType,
			SmAddress smAddress) {
		List<SelectItem> items = new ArrayList<SelectItem>();
		SelectItem item = null;
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		if (SupplierManagementConst.LOCAL_AUTH_BUSINESS_TYPE
				.contains(businessType)) {
			if (smAddress != null && smAddress.getPmState() != null
					&& smAddress.getPmState().getStateId() != null) {
				if (smAddress.getPmDistrict() != null
						&& smAddress.getPmDistrict().getDistrictId() != null
						&& getInstance()
								.getDistrictById(
										smAddress.getPmDistrict()
												.getDistrictId())
								.getDistrictCode()
								.equals(SupplierManagementConst.KUCHING_DISTRICT_CODE)) {
					items.add(getLhdnSelectItem());
				} else {
					reqDTO.setSmAddress(smAddress);
					List<PmOrgProfile> orgProfileList = SmServiceLookup
							.getRegistrationSO()
							.retrieveLocalAuthorityOrgProfileByAddress(reqDTO)
							.getOrgProfileList();
					if (!EntityHelper.isCollectionEmpty(orgProfileList)) {
						item = new SelectItem();
						item.setLabel(SmCommonUtil.getMessage("lbl.txt.select",
								null));
						item.setValue(null);
						items.add(item);
						for (PmOrgProfile orgProfile : orgProfileList) {
							item = new SelectItem();
							item.setLabel(orgProfile.getPmOrgValidity()
									.getOrgName());
							item.setValue(orgProfile.getOrgProfileId());
							items.add(item);
						}
					} else {
						items.add(getLhdnSelectItem());
					}
				}
			}
		} else {
			items.add(getSsmSelectItem());
		}
		return items;
	}

	/**
	 * Check if application is physical counsultant
	 * 
	 * @param SmAppl
	 *            appl
	 * @return boolean
	 */
	public static boolean isApplPhysicalConsultant(SmAppl appl) {
		boolean retVal = false;
		if (appl.getSupplierType().equals("J")) {
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setApplId(appl.getApplId());
			reqDTO.setCategoryCode(my.ep.constant.sm.SupplierManagementConst.PHYSICAL_SERVICES_CAT_CODE);
			int count = SmServiceLookup.getMofRegistrationSO()
					.countSuppCatByApplIdAndCatCode(reqDTO).getCount();
			if (count > 0) {
				retVal = true;
			}
		}
		return retVal;
	}

	/**
	 * Get BPM task details by doc number
	 * 
	 * @param String
	 *            taskId
	 * @return BpmResponseDTO
	 */
	public static BpmResponseDTO getBpmTaskDetails(String docNo) {
		BpmResponseDTO resDTO = null;
		String taskId = StringUtils.EMPTY;
		Object token = new FacesScopes().getBPMAuthenticationCtxToken();
		try {
			BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
			bpmReqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
			bpmReqDTO.setOffset(0);
			bpmReqDTO.setState("ASSIGNED");
			bpmReqDTO.setDocType("");
			bpmReqDTO.setViewBy("ALL");
			bpmReqDTO.setAssignment("ALL");
			bpmReqDTO.setCtxToken(token);
			resDTO = SmServiceLookup.getBpmSO().getWorklistItems(bpmReqDTO);
			if (resDTO.getWorklistVO() != null
					&& !EntityHelper.isCollectionEmpty(resDTO.getWorklistVO()
							.getWorklistItem())) {
				for (WorklistItemVO item : resDTO.getWorklistVO()
						.getWorklistItem()) {
					if (item.getDocNumber().equals(docNo)) {
						taskId = item.getTaskId();
					}
					break;
				}
			}
			SdoReviewReqDTO reqDTO = new SdoReviewReqDTO();
			reqDTO.setTaskId(taskId);
			reqDTO.setToken(token);
			ISdoReviewSO reqSO = (ISdoReviewSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", ISdoReviewSO.class);
			resDTO = (BpmResponseDTO) reqSO.getBPMTaskDetails(reqDTO);
			resDTO.setTaskId(taskId);
		} catch (Exception ex) {

		}
		return resDTO;
	}

	public static boolean isBetweenDuration(int minParam, int maxParam,
			int minCompare, int maxCompare) {
		boolean retVal = false;

		List<Integer> paramDuration = new ArrayList<Integer>();
		List<Integer> compareDuration = new ArrayList<Integer>();

		for (int i = minParam; i <= maxParam; i++) {
			paramDuration.add(i);
		}

		for (int i = minCompare; i <= maxCompare; i++) {
			compareDuration.add(i);
		}

		for (Integer param : paramDuration) {
			for (Integer compare : compareDuration) {
				if (param.equals(compare)) {
					retVal = true;
					break;
				}
			}
		}

		return retVal;
	}

	/**
	 * Verify identification no with MyIdentity
	 * 
	 * @param name
	 * @param identificationNo
	 * @param postCode
	 */
	public void verifyWithMyIdentity(String name, String identificationNo,
			String postCode, String type) {
		MyIdentityReqDTO reqDTO = new MyIdentityReqDTO();
		IMyIdentitySO reqSO = BusinessDelegate.getInstance().getServiceObject(
				"sm", IMyIdentitySO.class);

		if (type != null
				&& type.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_COMPLAIN)) {
			reqDTO.setType(my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_COMPLAIN);
		} else {
			reqDTO.setType(my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_ONLINE);
		}

		reqDTO.setNewIcNo(identificationNo);
		reqDTO.setPostCode(postCode);
		reqDTO.setName(name);

		try {
			MyIdentityResDTO resDTO = reqSO.myIdentityVerification(reqDTO);
			log.info("verifyWithMyIdentity>>>>" + resDTO.getSeverity()
					+ ">>code>>" + resDTO.getStatusCode());
			if (!resDTO
					.getSeverity()
					.equalsIgnoreCase(
							my.ep.constant.sm.SupplierManagementConst.MYIDENTITY_SEVERITY_SUCC)) {
				switch (resDTO.getStatusCode()) {
				case "70019":
					throw new EpException(MessageConst.ERROR_MYIDENTITY_70019);
				case "70020":
					throw new EpException(MessageConst.ERROR_MYIDENTITY_70020);
				case "70031":
					throw new EpException(MessageConst.ERROR_MYIDENTITY_70031);
/*				case "70032":
					throw new EpException(MessageConst.ERROR_MYIDENTITY_70032);*/
				case "70004":
					throw new EpException(MessageConst.ERROR_MYIDENTITY_70004);
				case "70005":
					throw new EpException(MessageConst.ERROR_MYIDENTITY_70004);
				}
			}
		} catch (Exception ex) {
			String message = MessageUtils.getMessage(ex.getMessage(),
					MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale());
			if (message != null) {
				throw new EpException(message + "(ERR-729999)");
			} else {
				throw new EpException(ex.getMessage() + "(ERR-729999)");
			}
		}
	}
	
	/**
	 * verify verify Owner With My Identity
	 * @param name
	 * @param identificationNo
	 * @param postCode
	 * @param type
	 */
	public void verifyOwnerWithMyIdentity(String name, String identificationNo,
			String postCode, String type){
		MyIdentityReqDTO reqDTO = new MyIdentityReqDTO();
		IMyIdentitySO reqSO = BusinessDelegate.getInstance().getServiceObject(
				"sm", IMyIdentitySO.class);

		if (type != null
				&& type.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_COMPLAIN)) {
			reqDTO.setType(my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_COMPLAIN);
		} else {
			reqDTO.setType(my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_ONLINE);
		}

		reqDTO.setNewIcNo(identificationNo);
		reqDTO.setPostCode(postCode);
		reqDTO.setName(name);

		try {
			MyIdentityResDTO resDTO = reqSO.myIdentityVerification(reqDTO);
			log.info("verifyWithMyIdentity>>>>" + resDTO.getSeverity()
					+ ">>code>>" + resDTO.getStatusCode());
			if (!resDTO
					.getSeverity()
					.equalsIgnoreCase(
							my.ep.constant.sm.SupplierManagementConst.MYIDENTITY_SEVERITY_SUCC)) {
				switch (resDTO.getStatusCode()) {
				case "70019":
					throw new EpException(MessageConst.ERROR_MYIDENTITY_70019);
				case "70020":
					throw new EpException(MessageConst.ERROR_MYIDENTITY_70020);
				case "70031":
					throw new EpException(MessageConst.ERROR_MYIDENTITY_70031);
				case "70032":
					throw new EpException(MessageConst.ERROR_MYIDENTITY_70032);
				case "70004":
					throw new EpException(MessageConst.ERROR_MYIDENTITY_70004);
				case "70005":
					throw new EpException(MessageConst.ERROR_MYIDENTITY_70004);
				}
			}
		} catch (Exception ex) {
			String message = MessageUtils.getMessage(ex.getMessage(),
					MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale());
			if (message != null) {
				throw new EpException(message + "(ERR-729999)");
			} else {
				throw new EpException(ex.getMessage() + "(ERR-729999)");
			}
		}
	}

	/**
	 * Set default personnel identification type based on selected nationality
	 * 
	 * @param SmPersonnel
	 *            per
	 */
	public static void setDefaultPersonnelNationality(SmPersonnel per) {
		if (per.getPmCountry() != null) {
			if (per.getPmCountry().getCountryCode()
					.equals(SupplierManagementConst.MALAYSIA_CODE)) {
				PmParameter ic = retrieveParameterByTypeAndCode(
						ParameterTypeConst.IDENTIFICATION_TYPES,
						SupplierManagementConst.NEW_IC, StringUtils.EMPTY);
				per.setIdentificationTypeId(ic.getParameterId());
				per.setIdentificationType(ic);
			} else {
				PmParameter passport = retrieveParameterByTypeAndCode(
						ParameterTypeConst.IDENTIFICATION_TYPES, "P",
						StringUtils.EMPTY);
				per.setIdentificationTypeId(passport.getParameterId());
				per.setIdentificationType(passport);
			}
		}
	}

	/**
	 * Validate if GST registration no. entered already exist
	 * 
	 * @param String
	 *            bussRegNo
	 * @param String
	 *            gstRegNo
	 */
	public static void validateGstRegNoUniqueness(String bussRegNo,
			String gstRegNo) {
		if (!StringUtils.isBlank(gstRegNo)) {
			SupplierGstReqDTO reqDTO = new SupplierGstReqDTO();
			reqDTO.setBussRegNo(bussRegNo);
			reqDTO.setGstRegNo(gstRegNo);
			if (SmServiceLookup.getSupplierGstSO()
					.checkIsGstRegNoAlreadyExist(reqDTO)
					.isGstRegNoAlreadyExist()) {
				throw new EpException(MessageConst.ERROR_722641,
						new String[] { gstRegNo });
			}
		}
	}

	/**
	 * Get status description by status ID
	 * 
	 * @param Long
	 *            statusId
	 * @return PmStatusDesc
	 */
	public static PmStatusDesc getStatusDescByStatusId(Long statusId) {
		PmStatusDesc retVal = null;
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(statusId);
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		StatusResDTO resDTO = SmServiceLookup.getStatusSO()
				.retrieveStatusDescById(reqDTO);
		if (resDTO != null && resDTO.getPmStatusDesc() != null) {
			retVal = resDTO.getPmStatusDesc();
		}
		return retVal;
	}

	/**
	 * Generate velocity template to PDF
	 * 
	 * @param svTask
	 * @param templateType
	 * @return String
	 * @throws Exception
	 */
	public static String generateToPdf(String vm, VelocityContext context,
			String fileNamePath) throws Exception {
		File tempFile1 = new File(fileNamePath);
		OutputStream os1 = new FileOutputStream(tempFile1);
		DocumentGenerator.getInstance().generatePdfFromVelocity(vm, context,
				os1);
		os1.flush();
		os1.close();
		return fileNamePath;
	}

	public static void combinePdf(List<File> pdfs, String fileName)
			throws Exception {
		List<InputStream> list = new ArrayList<InputStream>();
		for (File pdf : pdfs) {
			list.add(new FileInputStream(pdf));
		}

		String latestFile = fileName;
		OutputStream combined = new FileOutputStream(new File(latestFile));

		doMerge(list, combined);

		combined.flush();
		combined.close();
	}

	public static void doMerge(List<InputStream> list, OutputStream outputStream)
			throws DocumentException, IOException {
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		document.open();
		PdfContentByte cb = writer.getDirectContent();

		for (InputStream in : list) {
			PdfReader reader = new PdfReader(in);
			for (int i = 1; i <= reader.getNumberOfPages(); i++) {
				document.newPage();
				// import the page from source pdf
				PdfImportedPage page = writer.getImportedPage(reader, i);
				// add the page to the destination pdf
				cb.addTemplate(page, 0, 0);
			}
			in.close();
		}

		outputStream.flush();
		document.close();
		outputStream.close();
	}

	/**
	 * Get as PDF type stream content
	 * 
	 * @param String
	 *            fullPathName
	 * @return StreamedContent
	 * @throws IOException
	 */
	public static StreamedContent getPdfFileStreamContentByActualPath(
			String path) throws IOException {
		String filePath = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		filePath += "/" + path;
		File file = new File(filePath);
		ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
		FileInputStream in = new FileInputStream(file);
		byte[] buf = new byte[512];
		int len;
		while (true) {
			len = in.read(buf);
			if (len == -1) {
				break;
			}
			tmpOut.write(buf, 0, len);
		}
		in.close();
		tmpOut.close();
		ByteBuffer bb = ByteBuffer.wrap(tmpOut.toByteArray(), 0, tmpOut.size());

		ByteArrayInputStream stream = new ByteArrayInputStream(bb.array());

		return new DefaultStreamedContent(stream, "application/pdf");
	}

	public static StreamedContent getFileStreamContentByTypeAndPath(
			String path, String type) throws IOException {
		PortletContext context = (PortletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		String filePath = context.getRealPath("/xhtml" + path);
		File file = new File(filePath);
		ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
		FileInputStream in = new FileInputStream(file);
		byte[] buf = new byte[512];
		int len;
		while (true) {
			len = in.read(buf);
			if (len == -1) {
				break;
			}
			tmpOut.write(buf, 0, len);
		}
		in.close();
		tmpOut.close();
		ByteBuffer bb = ByteBuffer.wrap(tmpOut.toByteArray(), 0, tmpOut.size());

		ByteArrayInputStream stream = new ByteArrayInputStream(bb.array());

		return new DefaultStreamedContent(stream, type);
	}

	/**
	 * Check if current address has different compare with latest address
	 * 
	 * @param SmAddress
	 *            latest
	 * @param SmAddress
	 *            current
	 * @return boolean
	 */
	public static boolean isAddressHasDifferences(SmAddress latest,
			SmAddress current) {
		boolean retVal = false;
		if ((latest == null && current != null)
				|| (latest != null && current == null)) {
			retVal = true;
		} else if (isHasDifferences(latest, current, new String[] {
				"countryId", "stateId", "districtId", "cityId", "addressId",
				"pmCountry", "pmState", "pmCity", "pmDistrict" })) {
			retVal = true;
		} else {
			if ((latest.getPmCountry() != null && current.getPmCountry() == null)
					|| (latest.getPmCountry() == null && current.getPmCountry() != null)
					|| (latest.getPmCountry() != null
							&& current.getPmCountry() != null && latest
							.getPmCountry().getCountryId()
							.compareTo(current.getPmCountry().getCountryId()) != 0)) {
				retVal = true;
			} else if ((latest.getPmState() != null && current.getPmState() == null)
					|| (latest.getPmState() == null && current.getPmState() != null)
					|| (latest.getPmState() != null
							&& current.getPmState() != null && latest
							.getPmState().getStateId()
							.compareTo(current.getPmState().getStateId()) != 0)) {
				retVal = true;
			} else if ((latest.getPmCity() != null && current.getPmCity() == null)
					|| (latest.getPmCity() == null && current.getPmCity() != null)
					|| (latest.getPmCity() != null
							&& current.getPmCity() != null && latest
							.getPmCity().getCityId()
							.compareTo(current.getPmCity().getCityId()) != 0)) {
				retVal = true;
			} else if ((latest.getPmDistrict() != null && current
					.getPmDistrict() == null)
					|| (latest.getPmDistrict() == null && current
							.getPmDistrict() != null)
					|| (latest.getPmDistrict() != null
							&& current.getPmDistrict() != null && latest
							.getPmDistrict().getDistrictId()
							.compareTo(current.getPmDistrict().getDistrictId()) != 0)) {
				retVal = true;
			}
		}
		return retVal;
	}

	public static boolean isSupplierOwnerIndividualAcc(
			String identificationType, String identificationNo, String bussType) {
		boolean retVal = false;
		if (identificationType != null) {
			RegistrationReqDTO reqDTO = new RegistrationReqDTO();
			reqDTO.setIdentificationType(identificationType);
			reqDTO.setIdentificationNo(identificationNo);
			if (bussType != null) {
				reqDTO.setBusinessType(bussType);
			}
			IRegistrationSO reqSO = SmServiceLookup.getRegistrationSO();
			RegistrationResDTO resDTO = reqSO
					.retrieveActiveSupplierByOwnerIDAndBusType(reqDTO);

			if (resDTO.getSmSuppliers().size() > 0) {
				retVal = true;
			}
		}
		return retVal;
	}

	/**
	 * Get user zone ID by login ID and eP Role
	 * 
	 * @param String
	 *            loginId
	 * @param String
	 *            epRole
	 * @return Long
	 */
	public static Long getUserZoneIdByLoginIdAndEpRole(String loginId,
			String epRole) {
		if (StringUtils.isBlank(loginId) || StringUtils.isBlank(epRole)) {
			return null;
		} else {
			SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
			reqDTO.setLoginId(loginId);
			reqDTO.setEpRole(epRole);
			return SmServiceLookup.getSupplierDataMaintenanceSO()
					.getUserZoneIdByLoginIdAndEpRole(reqDTO).getZoneId();
		}
	}

	/**
	 * Get user roles by user ID
	 * 
	 * @param Long
	 *            userId
	 * @return List<PmUserRole>
	 */
	public static List<PmUserRole> getUserRolesByUserId(Long userId) {
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserIds(new ArrayList<Long>());
		reqDTO.getUserIds().add(userId);
		return SmServiceLookup.getUserSO()
				.retrieveUserRoleListByUserIds(reqDTO).getUserRoleList();
	}

	/**
	 * Get latest rejected application by application type and supplier
	 * 
	 * @param SmSupplier
	 *            supplier
	 * @param String
	 *            applType
	 * @return SmAppl
	 */
	public static SmAppl getLatestRejectedApplBySupplier(SmSupplier supplier) {
		SmAppl retVal = null;
		StringBuilder sb = new StringBuilder();
		sb.append("(")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER)
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER)
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM)
				.append(")");
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(supplier.getSupplierId());
		reqDTO.setWorkflowStatus(sb.toString());
		Long applId = SmServiceLookup.getMofRegistrationSO()
				.retriveApplIdForLastRejectedApplicationByApplType(reqDTO)
				.getApplId();
		if (applId != null) {
			reqDTO = new MofRegistrationReqDTO();
			reqDTO.setApplId(applId);
			retVal = SmServiceLookup.getMofRegistrationSO()
					.retrieveSmApplicationDetailsById(reqDTO).getSmAppl();
		}
		return retVal;
	}

	private static SelectItem getLhdnSelectItem() {
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		reqDTO.setOrgCode(my.ep.constant.sm.SupplierManagementConst.LHDN_ORG_PROFILE_CODE);
		PmOrgProfile orgProfile = SmServiceLookup.getRegistrationSO()
				.retrieveOrgProfileByOrgCode(reqDTO).getOrgProfile();
		SelectItem item = new SelectItem();
		item.setLabel(orgProfile.getPmOrgValidity().getOrgName());
		item.setValue(orgProfile.getOrgProfileId());
		return item;
	}

	private static SelectItem getSsmSelectItem() {
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		reqDTO.setOrgCode(my.ep.constant.sm.SupplierManagementConst.SSM_ORG_PROFILE_CODE);
		PmOrgProfile orgProfile = SmServiceLookup.getRegistrationSO()
				.retrieveOrgProfileByOrgCode(reqDTO).getOrgProfile();
		SelectItem item = new SelectItem();
		item.setLabel(orgProfile.getPmOrgValidity().getOrgName());
		item.setValue(orgProfile.getOrgProfileId());
		return item;
	}

	private static PmUserOrg getSupplierPmUserOrg(SmPersonnel personnel) {
		if (personnel != null
				&& personnel.getPmUser() != null
				&& !EntityHelper.isCollectionEmpty(personnel.getPmUser()
						.getPmUserOrgs())) {
			PmUser pmUser = personnel.getPmUser();
			List<PmUserOrg> pmUserOrgs = pmUser.getPmUserOrgs();
			PmUserOrg pmUserOrg = new PmUserOrg();
			for (PmUserOrg org : pmUserOrgs) {
				if (org.getPmOrgProfile().getOrgTypeCode().getParameterCode()
						.equals(ParameterConst.ORG_TYPE_SUPPLIER)
						|| (org.getPmOrgProfile().isGovtSeller() && (org
								.getPmOrgProfile()
								.getOrgTypeCode()
								.getParameterCode()
								.equals(ParameterConst.ORG_TYPE_LOCAL_AUTHORITY)
								|| org.getPmOrgProfile()
										.getOrgTypeCode()
										.getParameterCode()
										.equals(ParameterConst.ORG_TYPE_STATE_GOVT) || org
								.getPmOrgProfile().getOrgTypeCode()
								.getParameterCode()
								.equals(ParameterConst.ORG_TYPE_STATUTORY_BODY)))) {
					pmUserOrg = org;
					break;
				}
			}
			return pmUserOrg;
		} else {
			return null;
		}
	}

	/**
	 * Check if latest object has difference compare with latest object
	 * 
	 * @param Object
	 *            latest
	 * @param Object
	 *            current
	 * @param String
	 *            [] excludeProps
	 * @return boolean
	 */
	public static boolean isHasDifferences(Object latest, Object current,
			String[] excludeProps) {
		return isSmObjectPropsDifference(latest, current, excludeProps);
	}

	/**
	 * Get HTML reject reasons for display purpose
	 * 
	 * @param SmAppl
	 *            smAppl
	 * @return String
	 */
	public static String getHtmlApplicationRejectReasons(SmAppl smAppl) {
		String langCode = new FacesScopes().getUserLocale().getLanguage();
		StringBuilder sb = new StringBuilder();
		int i = 1;
		if (smAppl != null
				&& !EntityHelper.isCollectionEmpty(smAppl
						.getSmApplRejectReasons())) {
			for (SmApplRejectReason rej : smAppl.getSmApplRejectReasons()) {
				if (rej.getPmReason() != null
						&& rej.getPmReason().getPmReasonDescs() != null) {
					try {
						for (PmReasonDesc desc : rej.getPmReason()
								.getPmReasonDescs()) {
							if (desc.getPmLanguage() != null
									&& desc.getPmLanguage().getLanguageCode()
											.equalsIgnoreCase(langCode)) {
								sb.append(i + ") " + desc.getReasonDesc())
										.append("<br />");
								i++;
							}
						}
					} catch (ValidationException ex) {
						log.debug(">>>>ERROR INITIALIZE REJECT REASON:"
								+ ex.getMessage() + "<<<<<<<");
					}
				}
			}
		}
		return sb.toString();
	}

	public String getPaymentGatewayVKeyByBillType(String billType) {
		String vKey = null;

		if (billType
				.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_SOFTCERT)) {
			vKey = EpPropertiesUtils.getInstance().getProperty(
					"molpay.cdc.vkey");
		} else {
			vKey = EpPropertiesUtils.getInstance().getProperty(
					"molpay.gov.vkey");
		}
		return vKey;
	}

	public String getPaymentGatewayMerchantIdByBillType(String billType) {
		String vCode = null;

		if (billType
				.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.BILL_TYPE_SOFTCERT)) {
			vCode = EpPropertiesUtils.getInstance().getProperty(
					"molpay.cdc.merchantid");
		} else {
			vCode = EpPropertiesUtils.getInstance().getProperty(
					"molpay.gov.merchantid");
		}

		return vCode;
	}
}
