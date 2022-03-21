package my.ep.web.backingbean.report.sm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.OrganizationType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
import my.ep.web.constant.report.PmReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.report.sm.SmReportManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.CategoryRptVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;

public abstract class SmReportBackingBean<T extends SmReportManagedBean>
		extends ReportBaseBackingBean<T> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger.getLogger(SmReportBackingBean.class);

	public void generateSmReport(LinkedHashMap<String, String> params) {
			
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IRPTRequestSO.class);
		
		params.put(SmReportConst.PARAM_KEY_REPORT, getManagedBean()
				.getReportName());
		params.put(SmReportConst.PARAM_REPORT_CODE, getManagedBean()
				.getReportCode());
		params.put(SmReportConst.PARAM_USER_ID, getFacesScopes()
				.getUserSessionBean().getUserId().toString());

		RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
		reqDTO.setDefaultOutputType(this.getManagedBean().getDefaultOutputType());
		reqDTO.setTempAccess(this.getManagedBean().isTempAccess());
		reqDTO.setParametersMap(params);
				
		try {
			RPTRequestResDTO resDTO = rptRequestSO.generateReport(reqDTO);
			getPopUpMsg(String.valueOf(resDTO.getRequestId()));
			RequestContext.getCurrentInstance().execute("rptConfirmationDlg.show()");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public void setDefaultDateFromAndDateTo() {
		if (getManagedBean().getDateFrom() == null
				|| getManagedBean().getDateTo() == null) {
			Calendar fromDate = Calendar.getInstance();
			fromDate.set(fromDate.get(Calendar.YEAR), 00, 01, 0, 00, 0);
			getManagedBean().setDateFrom(fromDate.getTime());
			getManagedBean().setDateTo(new Date());
		}
	}

	public void validateMofRegNo() {
		String ptString1 = ReportConst.NUMERIC_PATTERN_ONLY;
		Pattern pattern1 = Pattern.compile(ptString1);
		if (StringUtils.isNotEmpty(getManagedBean().getMofRegNoFrom())) {
			String mofRegNoFrom = null;
			if (getManagedBean().getMofRegNoFrom().contains(
					ReportConst.PARAM_IFON)) {
				mofRegNoFrom = getManagedBean().getMofRegNoFrom().replace(
						ReportConst.PARAM_IFON, ReportConst.EMPTY_VAL);
				if (mofRegNoFrom.contains(ReportConst.PARAM_SPACE)) {
					mofRegNoFrom = mofRegNoFrom.replace(
							ReportConst.PARAM_SPACE, ReportConst.EMPTY_VAL);
				}
			} else {
				mofRegNoFrom = getManagedBean().getMofRegNoFrom();
			}

			Matcher match1 = pattern1.matcher(mofRegNoFrom);
			if (match1.matches()) {

			} else {
				getManagedBean().setMofRegNoFrom(null);
				getManagedBean().setMofRegNoTo(null);
				throw new EpException(MessageConst.ERROR_REPORT_780018);
			}
		}
		String ptString2 = ReportConst.NUMERIC_PATTERN_ONLY;
		Pattern pattern2 = Pattern.compile(ptString2);
		if (StringUtils.isNotEmpty(getManagedBean().getMofRegNoTo())) {
			String mofRegNoTo = null;
			if (getManagedBean().getMofRegNoTo().contains(
					ReportConst.PARAM_IFON)) {
				mofRegNoTo = getManagedBean().getMofRegNoTo().replace(
						ReportConst.PARAM_IFON, ReportConst.EMPTY_VAL);
				if (mofRegNoTo.contains(ReportConst.PARAM_SPACE)) {
					mofRegNoTo = mofRegNoTo.replace(ReportConst.PARAM_SPACE,
							ReportConst.EMPTY_VAL);
				}
			} else {
				mofRegNoTo = getManagedBean().getMofRegNoTo();
			}

			Matcher match2 = pattern2.matcher(mofRegNoTo);
			if (match2.matches()) {
				// this.setSmMofRegNoTo(mofRegNoTo);
			} else {
				getManagedBean().setMofRegNoFrom(null);
				getManagedBean().setMofRegNoTo(null);
				throw new EpException(MessageConst.ERROR_REPORT_780018);
			}
		}
	}
	
	// #27397 - start
	public void validateMofRegNo(String mofNo) {
		if (StringUtils.isEmpty(mofNo)) {
			throw new EpException(MessageConst.ERROR_REPORT_780029);
		}
	}
	// #27397 - end
	
	// #21331 - start
	public void validateParameterValue(String reportCode) {
		log.info("************** SmReportBackingBean.validateParameterValue() | reportCode: " + reportCode);
		// #29712 & #30824, #32564 - start
		// SM_001
		if (StringUtils.isNotEmpty(reportCode) && (reportCode.trim().equalsIgnoreCase(ReportConst.SM_REG_COMPANY_IN_MOF_RPT_VAL))) {
			if (StringUtils.isEmpty(getManagedBean().getRegType())) {
				throw new EpException(MessageConst.ERROR_REPORT_780062);
			} else {
				if (getManagedBean().getRegType().equalsIgnoreCase(SmReportConst.SM_REG_TYPE_MOF)) {
					validateMofRegNo(getManagedBean().getMofNo());
				} else if (getManagedBean().getRegType().equalsIgnoreCase(SmReportConst.SM_REG_TYPE_BASIC)) {
					if (getManagedBean().getSelectedSupplier() == null) {
						throw new EpException(MessageConst.ERROR_REPORT_780044);
					}
				} else if (getManagedBean().getRegType().equalsIgnoreCase(SmReportConst.SM_REG_TYPE_G2G)) {
					if (getManagedBean().getSelectedOrgProfile() == null) {
						throw new EpException(MessageConst.ERROR_REPORT_780070);
					}
				}
			}
		}
		// #29712 & #30824, #32564 - end
		
		// SM_048
		if (StringUtils.isNotEmpty(reportCode) && (reportCode.trim().equalsIgnoreCase(ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL))) {
			// search date
			if (StringUtils.isEmpty(getManagedBean().getSearchDate())) {
				throw new EpException(MessageConst.ERROR_REPORT_780060);
			} else {
				if (getManagedBean().getSearchDate().equalsIgnoreCase("mofRegisteredDate")) {
					checkOneMonthDifferent(getManagedBean().getDateFrom(), getManagedBean().getDateTo(), getManagedBean().getSearchDate());
				} else {
					checkOneMonthDifferent(getManagedBean().getDateExpFrom(), getManagedBean().getDateExpTo(), getManagedBean().getSearchDate());
				}
			}
			// MOF Reg No
			if (StringUtils.isEmpty(getManagedBean().getMofRegNoFrom()) || StringUtils.isEmpty(getManagedBean().getMofRegNoTo())) {
				validateMofRegNo();
			}
		}
	}

	public void checkOneMonthDifferent(Date fromDate, Date toDate, String searchDate) {
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		String yearFrom = outputDateFormat.format(fromDate).substring(6, 10);
		String yearTo = outputDateFormat.format(toDate).substring(6, 10);
		String monthFrom = outputDateFormat.format(fromDate).substring(3, 5);
		String monthTo = outputDateFormat.format(toDate).substring(3, 5);
		String dayFrom = outputDateFormat.format(fromDate).substring(0, 2);
		String dayTo = outputDateFormat.format(toDate).substring(0, 2);
		int yearFromInt = Integer.parseInt(yearFrom);
		int yearToInt = Integer.parseInt(yearTo);
		int monthFromInt = Integer.parseInt(monthFrom);
		int monthToInt = Integer.parseInt(monthTo);
		int dayFromInt = Integer.parseInt(dayFrom);
		int dayToInt = Integer.parseInt(dayTo);

		log.info("********checkOneYearDifferent********| searchDate = " + searchDate);
		log.info("** yearFromInt=" + yearFromInt + " | yearToInt=" + yearToInt + " | monthFromInt=" + monthFromInt + " | monthToInt=" + monthToInt + " | dayFromInt=" + dayFromInt
				+ " | dayToInt=" + dayToInt);

		if (yearFromInt == yearToInt) {
			if (monthFromInt != monthToInt) {
				if (searchDate.equalsIgnoreCase("mofRegisteredDate")) {
					throw new EpException(MessageConst.ERROR_REPORT_780057);
				} else {
					throw new EpException(MessageConst.ERROR_REPORT_780058);
				}
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780061);
		}
	}
	// #21331 - end

	public void onChangeZone() {

	}

	public void onChangeRegType() {

	}

	public void onChangeState() {

	}

	public void onChangeDistrict() {

	}

	public void onChangeYear() {

	}

	public void onChangeApplProgressStatus() {

	}

	public void onChangeSuppType() {

	}

	public void onChangeBussType() {
		getManagedBean().setCapitalLabel(StringUtils.EMPTY);
		if (StringUtils.isNotEmpty(getManagedBean().getBussType())) {
			if (getManagedBean().getBussType().equals("C")
					|| getManagedBean().getBussType().equals("D")
					|| getManagedBean().getBussType().equals("E")
					|| getManagedBean().getBussType().equals("F")
					|| getManagedBean().getBussType().equals("G")
					|| getManagedBean().getBussType().equals("H")) {
				getManagedBean().setCapitalLabel(
						SmCommonUtil.getMessage("lbl.rpt.accumulatedcapital",
								null));
			} else if (getManagedBean().getBussType().equals("I")
					|| getManagedBean().getBussType().equals("J")
					|| getManagedBean().getBussType().equals("B")) {
				getManagedBean().setCapitalLabel(
						SmCommonUtil.getMessage("lbl.rpt.paidUpCapitalFrm",
								null));
			} else if (getManagedBean().getBussType().equals("K")) {
				getManagedBean().setCapitalLabel(
						SmCommonUtil.getMessage("lbl.rpt.accumulatedshare",
								null));
			}
		} else {
			getManagedBean().setCapitalLabel(
					SmCommonUtil.getMessage("lbl.rpt.paidaccumulatedshare",
							null));
		}

		if (StringUtils.isBlank(getManagedBean().getCapitalLabel())) {
			getManagedBean().setCapitalShareFrom(StringUtils.EMPTY);
			getManagedBean().setCapitalShareTo(StringUtils.EMPTY);
		}
	}
	
	// #23080 - start
	public void onChangeOrganizationType() {

	}
	// #23080 - end

	public void reportOnChange() {
		if (!StringUtils.isBlank(getManagedBean().getReportCode())) {
			retrieveReportValueFromDb();
		}
	}

	public void addCompanyNameParam(LinkedHashMap<String, String> params) {
		if (getManagedBean().getSelectedOrgProfile() != null) {
			if (getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
				params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean()
						.getSelectedOrgProfile().getSmSupplier()
						.getCompanyName());
			}
		}
	}

	public void initStates() {
		PmCountry country = SmCommonUtil
				.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		getManagedBean().setStateLst(
				SmCommonUtil.getStateList(country.getCountryId()));
	}

	public void initStatesByZone() {
		getManagedBean().setDistrictLst(new ArrayList<SelectItem>());
		if (StringUtils.isBlank(getManagedBean().getZone())) {
			initStates();
		} else {
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
			reqDTO.setZoneName(getManagedBean().getZone());
			List<PmState> stateList = SmServiceLookup.getStateSO()
					.retriveStatesListByCountryCodeWithZoneName(reqDTO)
					.getStateList();
			getManagedBean().setStateLst(
					EntityHelper.convertListToSelectItem(stateList,
							"stateName", "stateId"));
		}
	}

	public List<SelectItem> retrieveRegType(boolean showAll, boolean showUpgrade, boolean compProfile) {
		List<SelectItem> items = new ArrayList<SelectItem>();
		SelectItem item = null;
		if (showAll) {
			item = new SelectItem();
			item.setLabel(SmCommonUtil.getMessage("lbl.rpt.all", null));
			item.setValue(StringUtils.EMPTY);
			items.add(item);
		}
		if (!compProfile) {
			item = new SelectItem();
			item.setLabel(SmCommonUtil.getMessage("lbl.rpt.regBasic", null));
			item.setValue(SmReportConst.SM_REG_TYPE_BASIC);
			items.add(item);
			item = new SelectItem();
			item.setLabel(SmCommonUtil.getMessage("lbl.rpt.regMOF", null));
			item.setValue(SmReportConst.SM_REG_TYPE_MOF);
			items.add(item);
			if (showUpgrade) {
				item = new SelectItem();
				item.setLabel(SmCommonUtil.getMessage("lbl.rpt.upgradeBasicToMOF",
						null));
				item.setValue(SmReportConst.SM_REG_TYPE_UPGRADE_BASIC_TO_MOF);
				items.add(item);
			}
		} else {
			// #23080 - start
			item = new SelectItem();
			item.setLabel(SmCommonUtil.getMessage("lbl.rpt.regBasicAccount", null));
			item.setValue(SmReportConst.SM_REG_TYPE_BASIC);
			items.add(item);
			item = new SelectItem();
			item.setLabel(SmCommonUtil.getMessage("lbl.rpt.regG2GAccount", null));
			item.setValue(SmReportConst.SM_REG_TYPE_G2G);
			items.add(item);
			item = new SelectItem();
			item.setLabel(SmCommonUtil.getMessage("lbl.rpt.regMOFAccount", null));
			item.setValue(SmReportConst.SM_REG_TYPE_MOF);
			items.add(item);
			// #23080 - end
		}
		return items;
	}

	public List<SelectItem> retrieveSuppType(boolean showAll) {
		List<SelectItem> items = new ArrayList<SelectItem>();
		SelectItem item = null;
		if (showAll) {
			item = new SelectItem();
			item.setLabel(SmCommonUtil.getMessage("lbl.rpt.all", null));
			item.setValue(StringUtils.EMPTY);
			items.add(item);
		}
		item = new SelectItem();
		item.setLabel(SmCommonUtil.getMessage("lbl.rpt.consultant", null));
		item.setValue(SupplierManagementConst.SUPP_TYPE_CONSULTANT);
		items.add(item);
		item = new SelectItem();
		item.setLabel(SmCommonUtil.getMessage("lbl.rpt.contractor", null));
		item.setValue(SupplierManagementConst.SUPP_TYPE_CONTRACTOR);
		items.add(item);
		return items;
	}

	/**
	 * retrieveRegStatusList - will retrieve registration status list.
	 * 
	 * @return the registrationStatusList
	 */
	public List<SelectItem> retrieveRegStatusList() {
		List<SelectItem> registrationStatusList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);

			List<Object[]> regStatusList = reportSO
					.retrieveRegStatusList(getFacesScopes().getUserLocale()
							.getLanguage());
			if (CollectionUtils.isNotEmpty(regStatusList)) {
				for (Object[] object : regStatusList) {
					SelectItem selectItem = new SelectItem();
					selectItem.setValue(object[1].toString());
					selectItem.setLabel(object[0].toString());
					registrationStatusList.add(selectItem);
				}
			}
		}
		return registrationStatusList;
	}

	public void initBusinesTypeList() {
		getManagedBean().setBussTypeList(new ArrayList<SelectItem>());
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);

			List<Object[]> retrieveConsultantList = reportSO
					.getConsultantBusCompanyData(getFacesScopes()
							.getUserLocale().getLanguage());
			if (CollectionUtils.isNotEmpty(retrieveConsultantList)) {
				for (Object[] object : retrieveConsultantList) {
					SelectItem selectItem = new SelectItem();
					selectItem.setValue(object[1].toString());
					selectItem.setLabel(object[0].toString());
					getManagedBean().getBussTypeList().add(selectItem);
				}
			}
		}
	}

	public List<SelectItem> retrieveApplStatus() {
		if (EntityHelper
				.isCollectionEmpty(getManagedBean().getApplStatusList())
				&& null != new FacesScopes().getUserSessionBean().getUserId()) {

			getManagedBean().setApplStatusList(new ArrayList<SelectItem>());
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> applStatusList = reportSO
					.retrieveApplicationStatusList(new FacesScopes()
							.getUserLocale().getLanguage());

			if (CollectionUtils.isNotEmpty(applStatusList)) {
				for (Object[] object : applStatusList) {
					if (object[0] != null && object[1] != null) {
						SelectItem selectItem = new SelectItem();
						selectItem.setValue(object[0].toString());
						selectItem.setLabel(object[1].toString());
						getManagedBean().getApplStatusList().add(selectItem);
					}
				}
			}
		}
		return getManagedBean().getApplStatusList();
	}

	public List<SelectItem> retrieveDistrictLsByStateId(Long stateId) {
		return SmCommonUtil.getDistrictList(stateId);
	}

	public List<SelectItem> retrieveRegistrationStatusList() {
		List<PmStatusDesc> descList = new ArrayList<PmStatusDesc>();
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		descList = SmServiceLookup.getStatusSO().retrieveRegStatusList(reqDTO)
				.getStatusDescList();
		List<SelectItem> items = new ArrayList<SelectItem>();
		SelectItem item = null;
		for (PmStatusDesc sd : descList) {
			if (sd.getPmStatus() != null
					&& !StringUtils.isBlank(sd.getStatusName())) {
				item = new SelectItem();
				item.setLabel(sd.getStatusName());
				item.setValue(sd.getPmStatus().getStatusId());
				items.add(item);
			}
		}
		return items;
	}

	public void addSelectedPaymentTypeParam(LinkedHashMap<String, String> params) {
		if (getManagedBean().getSelectedPaymentTypes() != null && getManagedBean().getSelectedPaymentTypes().size() > 0) {
			for (String paymentType : getManagedBean().getSelectedPaymentTypes()) {				
				
				if (paymentType.equalsIgnoreCase(SmReportConst.PAYMENT_MOF)) {
					params.put(SmReportConst.PARAM_PAYMENT_TYPE1, paymentType);
				} else if (paymentType.equalsIgnoreCase(SmReportConst.PAYMENT_PROCESSING)) {
					params.put(SmReportConst.PARAM_PAYMENT_TYPE2, paymentType);
				} else if (paymentType.equalsIgnoreCase(SmReportConst.PAYMENT_SOFTC_CERT)) {
					params.put(SmReportConst.PARAM_PAYMENT_TYPE3, paymentType);
				}
			}
			//Enhancement #19245 - role cdc collector may view soft cert only	
		} else if (getManagedBean().getSoftCert() != null) { 
				log.info(":::addSelectedPaymentTypeParam() getManagedBean().getSoftCert(): "+ getManagedBean().getSoftCert());
				params.put(SmReportConst.PARAM_PAYMENT_TYPE3, getManagedBean().getSoftCert());
			//end of Enhancement #19245 
		}
	}

	/**
	 * Gets the payment type method list.
	 * 
	 * @return the payment type method list
	 */
	public List<SelectItem> getPaymentTypeMethodList() {
		List<SelectItem> paymentTypeMethodList = new ArrayList<SelectItem>();
		SelectItem item = new SelectItem();
		item.setValue(ReportConst.SM_REPORT_CREDIT);
		item.setLabel(ResourceBundleUtils.getMessage("lbl.rpt.credit",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes()
						.getUserLocale()));
		paymentTypeMethodList.add(item);
		item = new SelectItem();
		String fpx = ResourceBundleUtils.getMessage("lbl.rpt.fpx",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes()
						.getUserLocale());
		item.setValue(fpx);
		item.setLabel(fpx);
		paymentTypeMethodList.add(item);

		return paymentTypeMethodList;
	}

	/**
	 * retrieveProcessingOfficerList - will retrieve processing officer list.
	 * 
	 * @return the processingOfficerList
	 */
	public List<SelectItem> retrieveProcessingOfficerList() {
		List<SelectItem> processingOfficerList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);

			/*
			 * List<Object[]> regStatusList = reportSO
			 * .retrieveProcessingOfficerList(this.getSmZone());
			 */
			List<Object[]> regStatusList = reportSO
					.retrieveProcessingOfficerList(null);
			if (CollectionUtils.isNotEmpty(regStatusList)) {
				for (Object[] object : regStatusList) {
					SelectItem selectItem = new SelectItem();
					selectItem.setValue(object[1].toString());
					selectItem.setLabel(object[0].toString());
					processingOfficerList.add(selectItem);
				}
			}

		}
		return processingOfficerList;
	}

	/**
	 * Retriev category list by supplier type.
	 * 
	 * @param supplierType
	 *            the supplier type
	 * @return the list
	 */
	public List<PmCategoryL1> retrieveL1CategoryLs() {
		log.info("ReportBackingBean-retrievCategoryListBySupplierType");

		// to clear the category drop down value
		/*
		 * for (SmSupplierCategory cat : this.getSelectedCatList()) {
		 * cat.setPmCategoryL1(new PmCategoryL1()); cat.setPmCategoryL2(new
		 * PmCategoryL2()); cat.setPmCategoryL3(new PmCategoryL3()); }
		 */
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						ICategoryCodeSO.class);
		CategoryCodeResDTO catCodeResDTO = new CategoryCodeResDTO();
		if (StringUtils.isNotBlank(getManagedBean().getSupplierType())) {
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			if (StringUtils.isNotBlank(getManagedBean().getSupplierType())) {
				reqDTO.setSupplierTypeCode(getManagedBean().getSupplierType());
			}

			catCodeResDTO = reqSO.retrieveCategoryL1ListBySupplierType(reqDTO);

		} else {
			catCodeResDTO = reqSO.retrieveCategoryL1List();
		}

		return catCodeResDTO.getCategoryL1List();
	}

	/**
	 * retrieve Lvl2 Category list.
	 * 
	 * @param categoryL1Id
	 *            the category l1 id
	 * @return the categoryL2List
	 */
	public List<PmCategoryL2> retrieveL2CategoryLs(Long categoryL1Id) {
		if (categoryL1Id != null) {
			ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
					.getInstance().getServiceObject(
							ReportConst.PM_MODULE_SER_OBJ,
							ICategoryCodeSO.class);
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			reqDTO.setCategoryL1Id(categoryL1Id);
			CategoryCodeResDTO resDTO = reqSO
					.retrieveCategoryL2ListByL1Id(reqDTO);
			return resDTO.getCategoryL2List();
		}

		return null;
	}

	/**
	 * retrieve Lvl2 Category list.
	 * 
	 * @param categoryL2Id
	 *            the category l2 id
	 * @return the categoryL3List
	 */
	public List<PmCategoryL3> retrieveL3CategoryLs(Long categoryL2Id) {
		if (categoryL2Id != null) {
			ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
					.getInstance().getServiceObject(
							ReportConst.PM_MODULE_SER_OBJ,
							ICategoryCodeSO.class);
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			reqDTO.setCategoryL2Id(categoryL2Id);
			CategoryCodeResDTO resDTO = reqSO
					.retrieveCategoryL3ListByL2Id(reqDTO);
			return resDTO.getCategoryL3List();
		}

		return null;
	}

	public void addCategory() {
		if (getManagedBean().getCategoryLs() == null) {
			getManagedBean().setCategoryLs(new ArrayList<CategoryRptVO>());
		}

		if (getManagedBean().getSelectedCategoryLs() != null) {
			getManagedBean().setSelectedCategoryLs(
					new ArrayList<CategoryRptVO>());
		}

		getManagedBean().getCategoryLs().add(new CategoryRptVO());
	}

	public void removeCategory() {
		if (getManagedBean().getSelectedCategoryLs().size() > 0) {
			getManagedBean().getCategoryLs().removeAll(
					getManagedBean().getSelectedCategoryLs());
		}
	}

	public void addCategoryParam(LinkedHashMap<String, String> params) {
		List<String> lvl1Ls = new ArrayList<String>();
		List<String> lvl2Ls = new ArrayList<String>();
		List<String> lvl3Ls = new ArrayList<String>();

		if (getManagedBean().getCategoryLs() != null
				&& getManagedBean().getCategoryLs().size() > 0) {
			for (CategoryRptVO catVO : getManagedBean().getCategoryLs()) {
				if (catVO.getCatLvl1() == null) {
					throw new EpException(MessageConst.ERROR_REPORT_780047);
				}

				lvl1Ls.add(catVO.getCatLvl1().toString());
				lvl2Ls.add(catVO.getCatLvl2() == null ? "null" : catVO
						.getCatLvl2().toString());
				lvl3Ls.add(catVO.getCatLvl3() == null ? "null" : catVO
						.getCatLvl3().toString());
			}

			params.put(SmReportConst.PARAM_CAT_L1,
					StringUtils.join(lvl1Ls, ","));
			params.put(SmReportConst.PARAM_CAT_L2,
					StringUtils.join(lvl2Ls, ","));
			params.put(SmReportConst.PARAM_CAT_L3,
					StringUtils.join(lvl3Ls, ","));
		}else{
			throw new EpException(MessageConst.ERROR_REPORT_780052);
		}
	}

	public void addCatParamWithoutMainCatChecking(LinkedHashMap<String, String> params) {
		List<String> lvl1Ls = new ArrayList<String>();
		List<String> lvl2Ls = new ArrayList<String>();
		List<String> lvl3Ls = new ArrayList<String>();

		if (getManagedBean().getCategoryLs() != null
				&& getManagedBean().getCategoryLs().size() > 0) {
			for (CategoryRptVO catVO : getManagedBean().getCategoryLs()) {
				/*if (catVO.getCatLvl1() == null) {
					throw new EpException(MessageConst.ERROR_REPORT_780047);
				}
*/
				lvl1Ls.add(catVO.getCatLvl2() == null ? "" :catVO.getCatLvl1().toString());
				lvl2Ls.add(catVO.getCatLvl2() == null ? "" : catVO
						.getCatLvl2().toString());
				lvl3Ls.add(catVO.getCatLvl3() == null ? "" : catVO
						.getCatLvl3().toString());
			}

			params.put(SmReportConst.PARAM_CAT_L1,
					StringUtils.join(lvl1Ls, ","));
			params.put(SmReportConst.PARAM_CAT_L2,
					StringUtils.join(lvl2Ls, ","));
			params.put(SmReportConst.PARAM_CAT_L3,
					StringUtils.join(lvl3Ls, ","));
		}
	}
	
	public void resetCategory(){
		getManagedBean().setSelectedCategoryLs(null);
		getManagedBean().setCategoryLs(null);
	}
	
	public void setDateToByDateRange(int dateRange){
		if(dateRange != 0 && getManagedBean().getDateFrom() != null){
			Calendar calTo = Calendar.getInstance();
			Date dtFrom = getManagedBean().getDateFrom();
			calTo.setTime(dtFrom);
			calTo.add(Calendar.MONTH, dateRange);
			
			Date currentDate = new Date();
			
			if(calTo.getTime().after(currentDate)){
				getManagedBean().setDateTo(currentDate);
			}else{
				getManagedBean().setDateTo(calTo.getTime());
			}
		}
	}
	
	//Enhancement #19245
	/**
	 * To check user role 
	 * 
	 * @param roleCode
	 * @return boolean flag
	 */
	public boolean isRole(String roleCode) {
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
		reqDTO.setLanguageCode(CommonConstants.DEFAULT_LANGUAGE_CODE);
		
		UserResDTO userResDTO = userSO.retrieveUserRoleDescListByUserId(reqDTO);
		List<PmRoleDesc> roleDesc = userResDTO.getRoleDescList();

		if (null != roleDesc) {
			for (PmRoleDesc pmRoleDesc : roleDesc) {
				if (roleCode.equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {
					return true;
				}
			}
		} else {
			throw new EpException("User does not exist with the role " + roleCode);
		}
		
		return false;
	}

	public void setDateFromAndDateTo() {
		Calendar fromDate = Calendar.getInstance();
		fromDate.set(fromDate.get(Calendar.YEAR), 00, 01, 0, 00, 0);
		getManagedBean().setDateFrom(fromDate.getTime());
		getManagedBean().setDateTo(new Date());
	}
	//end of Enhancement #19245 
	
	// #23080 - start
	/**
	 * Initialized agency type
	 */
	public void retrieveAgencyTypes() {
		getManagedBean().setAgencyTypeItems(new ArrayList<SelectItem>());
		SelectItem[] items = getDropdownCodeValues("OT");
		String[] agencyType = new String[] { OrganizationType.STATUTORY_BODIES.value(), OrganizationType.STATUTORY_BODIES_STATE.value(), OrganizationType.STATE_GOVERNMENT.value(),
				OrganizationType.LOCAL_AUTHORITY.value() };
		if (items != null && items.length > 0) {
			List<SelectItem> itemList = Arrays.asList(items);
			for (SelectItem item : itemList) {
				if (item.getValue() != null && Arrays.asList(agencyType).contains(item.getValue())) {
					getManagedBean().getAgencyTypeItems().add(item);
				}
			}
		}
	}
	// #23080 - end
	
	
	
	//Start- Enhancement - #15990
	//Added By: Haidah
	//Only for Laporan Terimaan Berdasarkan Kaedah Bayaran
	public List<Object[]> generateSmCollectionReportBasedPaymentMethod(LinkedHashMap<String, String> params, String type){
		IRPTRequestSO irptRequestSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IRPTRequestSO.class);
		return irptRequestSO.generateReportSMCollectionBasedPaymentMethod(params,type);
	}
	
	//Retrieve List of Payment Type 
	public List<String> retrievePaymentType(String paramCode){
		IPMReportSO ipmReportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ, IPMReportSO.class);
		return ipmReportSO.retrieveParameterCodeNameByParamCodeParamTypeAndLanguageCode(ReportConst.MALAY_LANGUAGE_CODE, "SPI", paramCode);
	}
	
	public List<Object[]> generateSMCollectionReportBasedClassificationCode(LinkedHashMap<String, String> params, String type){
		IRPTRequestSO irptRequestSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IRPTRequestSO.class);
		return irptRequestSO.generateReportSMCollectionBasedClassificationCode(params, type);
	}
	
	public List<Object[]> generateSMCollectionReportBasedReceiptAndTransactionDate(LinkedHashMap<String, String> params, String type){
		IRPTRequestSO irptRequestSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IRPTRequestSO.class);
		return irptRequestSO.generateReportSMCollectionBasedReceiptTransactionDate(params, type);
	}
	
	public List<Object[]> generateSMListDailyReceiptReport(LinkedHashMap<String, String> params, String type){
		IRPTRequestSO irptRequestSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IRPTRequestSO.class);
		return irptRequestSO.generateReportSMListOfDailyReceipt(params, type);
	}
	
	public List<Object[]> generateSMRevenueCollectionReport(LinkedHashMap<String, String> params){
		IRPTRequestSO irptRequestSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IRPTRequestSO.class);
		return irptRequestSO.generateReportSMRevenueCollection(params);
	}
	
	public void generateSMReportInExcel(String reportCode, LinkedHashMap<String, String> params){
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IRPTRequestSO.class);
		
		params.put(SmReportConst.PARAM_KEY_REPORT, getManagedBean()
				.getReportName());
		params.put(SmReportConst.PARAM_REPORT_CODE, getManagedBean()
				.getReportCode());
		params.put(SmReportConst.PARAM_USER_ID, getFacesScopes()
				.getUserSessionBean().getUserId().toString());
		
		log.info("USER_ID === "+params.get(SmReportConst.PARAM_USER_ID));

		RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
		reqDTO.setDefaultOutputType(this.getManagedBean().getDefaultOutputType());
		reqDTO.setTempAccess(this.getManagedBean().isTempAccess());
		reqDTO.setParametersMap(params);
		
		RPTRequestResDTO resDTO = new RPTRequestResDTO();
		try{
			resDTO = rptRequestSO.generateReportSMInExcel(reqDTO);
			log.info("REQUEST ID === "+resDTO.getRequestId());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		switch(reportCode){
			case "SM_071" : //Collection Report Based On Receipt & Transaction Date
				try {
					getCollectionReportBasedOnReceiptAndTransactionDate(params,resDTO.getRequestId());
					getPopUpMsg(String.valueOf(resDTO.getRequestId()));
					RequestContext.getCurrentInstance().execute("rptConfirmationDlg.show()");
				}catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
		
			case "SM_072" : //Collection Report Based On Payment Method
				try{
					getCollectionReportBasedOnPaymentMethod(params, resDTO.getRequestId());
					getPopUpMsg(String.valueOf(resDTO.getRequestId()));
					RequestContext.getCurrentInstance().execute("rptConfirmationDlg.show()");
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			
			case "SM_073" : //Collection Report Based On Classification Code
				try{
					getCollectionReportBasedOnClassificationCode(params, resDTO.getRequestId());
					getPopUpMsg(String.valueOf(resDTO.getRequestId()));
					RequestContext.getCurrentInstance().execute("rptConfirmationDlg.show()");
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
				
			case "SM_074" : //Revenue Collection Report
				try {
					getRevenueCollectionReport(params, resDTO.getRequestId());
					getPopUpMsg(String.valueOf(resDTO.getRequestId()));
					RequestContext.getCurrentInstance().execute("rptConfirmationDlg.show()");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;
				
			case "SM_075" : //List Of Daily Receipt Report
				try{
					getListOfDailyReceiptReport(params,resDTO.getRequestId());
					getPopUpMsg(String.valueOf(resDTO.getRequestId()));
					RequestContext.getCurrentInstance().execute("rptConfirmationDlg.show()");
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
		}
	}
	
	public void getCollectionReportBasedOnPaymentMethod(LinkedHashMap<String, String> params, BigDecimal requestId) throws Exception{
		List<String> payType = new ArrayList<String>();
		List<String> payTypeDescParam = new ArrayList<String>();
		Map<Integer, Object[]> reportListMap = new TreeMap<Integer, Object[]>();
		Map<Integer, Object[]> summaryListMap = new TreeMap<Integer, Object[]>();
		List<Object[]> rsltReportList = new ArrayList<Object[]>();
		List<Object[]> rsltSummaryList = new ArrayList<Object[]>();
		BigDecimal totalAmt = new BigDecimal(0);
		BigDecimal totalGstAmt = new BigDecimal(0); 
		BigDecimal totalServiceCharge = new BigDecimal(0);
		BigDecimal totalGstServiceCharge = new BigDecimal(0);
		BigDecimal totalNetAmt = new BigDecimal(0);
		int countBil = 0;
		String payementTypeDescParam = "";
		String paymentType = "";
		String paymentMethod = "";
		String reportType = "";
		
		if("M".equalsIgnoreCase(params.get(SmReportConst.PARAM_PAYMENT_TYPE1))){//MOF Registration Fee
			payType.add("MF");
			payType.add("RF");
			payTypeDescParam.add("MF");
			payTypeDescParam.add("RF");
		}
		
		if("P".equalsIgnoreCase(params.get(SmReportConst.PARAM_PAYMENT_TYPE2))){//Processing Fee
			payType.add("PF");
			payType.add("RPF");
			payTypeDescParam.add("PF");
		}

		if("S".equalsIgnoreCase(params.get(SmReportConst.PARAM_PAYMENT_TYPE3))){//Soft Certificate Fee
			payType.add("SF");
			payTypeDescParam.add("SF");
		}
		
		for(int b=0; b<payType.size(); b++){
			if(b != payType.size()-1){
				paymentType += "'"+payType.get(b).toString() +"',";
			}else{
				paymentType += "'"+payType.get(b).toString()+"'";
			}
		}
		
		for(int c=0; c<payTypeDescParam.size(); c++){
			if(c != payTypeDescParam.size()-1){
				payementTypeDescParam += "'"+payTypeDescParam.get(c).toString() +"',";
			}else{
				payementTypeDescParam += "'"+payTypeDescParam.get(c).toString()+"'";
			}
		}
		
		params.put("PAYMENTTYPE",paymentType);
		int paymentTypeSize = payTypeDescParam.size();
		if("Credit".equalsIgnoreCase(params.get(SmReportConst.PARAM_PAYMENT_METHOD)))paymentMethod = "Kad Kredit";
		else paymentMethod = "FPX";
		
		if("Tarikh Resit".equalsIgnoreCase(params.get(SmReportConst.PARAM_RPT_TYPE)))reportType = "Tarikh Resit";
		else reportType = "Tarikh Transaksi";
		
		String paymentTypeDesc = getPaymentTypeDesc(payementTypeDescParam);
		String reportTitle = "LAPORAN TERIMAAN BERDASARKAN KAEDAH BAYARAN BAGI TEMPOH " 
				+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateFrom()) + " HINGGA "
				+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateTo())
				+ " SEPERTI PADA "+ DateUtils.dateToString("dd/MM/yyyy",new Date())
				+ " BAGI TAHUN KEWANGAN "+ DateUtils.dateToString("yyyy",new Date());
		
		String[] rowHeaderItems = {"Kod PTJ","Nama PTJ","Kaedah Bayaran","Nama Pembekal","Jenis Bayaran"};
		//Update Func By Include label RM inside column Caj Perkhidmatan & GST Caj Perkhidmatan
		ArrayList<String> columnHeaderList = new ArrayList<String>(Arrays.asList(
				"Bil", "Tarikh Transaksi", "Tarikh Resit", "No Resit","Nama Pembekal","No Permohonan","No Rujukan","Jenis Terimaan-Kod", "Jenis Terimaan-Perihal",
				"Harga (RM)","GST (RM)","Caj Perkhidmatan (RM)","GST Caj Perkhidmatan (RM)","Amaun Bersih (RM)","Jumlah Keseluruhan (RM)","Status"
		));
		String[] columnsRingkasan = {reportType,"Bil","Harga (RM)","GST (RM)","Caj Perkhidmatan (RM)","GST Caj Perkhidmatan (RM)","Amaun Bersih (RM)"};
		
		try{
			rsltReportList = generateSmCollectionReportBasedPaymentMethod(params,"L");
			rsltSummaryList = generateSmCollectionReportBasedPaymentMethod(params,"R");
			if(!rsltReportList.isEmpty()){
				for(int m=0; m<rsltReportList.size(); m++){
					Object[] row = (Object[]) rsltReportList.get(m);
					BigDecimal amount = row[7] != null ? (BigDecimal)row[7] : BigDecimal.ZERO;
					BigDecimal gstAmount = row[8] != null ? (BigDecimal)row[8] : BigDecimal.ZERO;
					BigDecimal serviceCharge = row[9] != null ? (BigDecimal)row[9] : BigDecimal.ZERO;
					BigDecimal gstRate = row[10] != null ? (BigDecimal)row[10] : BigDecimal.ZERO;
					BigDecimal gstServiceChargeAmt = serviceCharge.multiply(gstRate).setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE,BigDecimal.ROUND_HALF_UP);
					BigDecimal netPrice = amount.add(gstAmount).subtract(serviceCharge).subtract(gstServiceChargeAmt).setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE,BigDecimal.ROUND_HALF_UP);
					reportListMap.put(m, new Object[]{
						(Date)row[16],(Date)row[1],row[2].toString(),row[3].toString(),String.valueOf(row[4]),row[14].toString(),
						row[5].toString(),row[6].toString(),amount,gstAmount,serviceCharge,gstServiceChargeAmt,netPrice,netPrice,row[13].toString()
					});
				}
			}
			
			if(!rsltSummaryList.isEmpty()){
				for(int n=0; n<rsltSummaryList.size(); n++){
					Object[] row = (Object[]) rsltSummaryList.get(n);
					BigDecimal amount = row[2] != null ? (BigDecimal)row[2] : BigDecimal.ZERO;
					BigDecimal gstAmount = row[3] != null ? (BigDecimal)row[3] : BigDecimal.ZERO;
					BigDecimal serviceCharge = row[4] != null ? (BigDecimal)row[4] : BigDecimal.ZERO;
					BigDecimal gstServiceChargeAmt = row[5] != null ? (BigDecimal)row[5] : BigDecimal.ZERO;
					BigDecimal netPrice = amount.add(gstAmount).subtract(serviceCharge).subtract(gstServiceChargeAmt).setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE,BigDecimal.ROUND_HALF_UP);
					totalAmt = totalAmt.add(amount);
					totalGstAmt = totalGstAmt.add(gstAmount);
					totalServiceCharge = totalServiceCharge.add(serviceCharge); 
					totalGstServiceCharge = totalGstServiceCharge.add(gstServiceChargeAmt); 
					totalNetAmt = totalNetAmt.add(netPrice);
					countBil+= Integer.parseInt(row[1].toString());
					summaryListMap.put(n, new Object[]{
						(Date)row[0],Integer.parseInt(row[1].toString()),amount,gstAmount,serviceCharge,gstServiceChargeAmt,netPrice
					});
				}
			}
			summaryListMap.put(rsltSummaryList.size(),new Object[]{"Keseluruhan",countBil,totalAmt,totalGstAmt,totalServiceCharge,totalGstServiceCharge,totalNetAmt});
			if("Tarikh Resit".equalsIgnoreCase(params.get(SmReportConst.PARAM_RPT_TYPE)))columnHeaderList.remove(1);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//START HERE POPULATE REPORT ------ 
		Workbook wb = new XSSFWorkbook(); 
		CreationHelper createHelper = wb.getCreationHelper();
		//Only create Sheet Senarai only if search result return has records.
		if(!reportListMap.isEmpty()){
			log.info("LIST IS NOT EMPTY !!!");
			Sheet sheetList = wb.createSheet("Senarai"); 
			try{
				//Setting Report Title
				Row titleRowSheetList = sheetList.createRow(0);
				Cell cellTitleRow = titleRowSheetList.createCell(1);
			    cellTitleRow.setCellValue(reportTitle);
			    cellTitleRow.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
			    sheetList.addMergedRegion(new CellRangeAddress(0,0,1,columnHeaderList.size()-1)); 
			    
			    //Setting Row Items - Kod PTJ/Nama PTJ/Kaedah Bayaran/Nama Pembekal/Jenis Bayaran
			    for(int i = 1; i <= rowHeaderItems.length; i++) {
		        	Row itemsDtlRow = sheetList.createRow(i);
		        	Cell cellItemsDtl = itemsDtlRow.createCell(1);
		        	cellItemsDtl.setCellValue(rowHeaderItems[i-1]);
		        	cellItemsDtl.setCellStyle(titleHeaderStyle(wb,true,false,false,true));
		        	Cell cellItemsVal = itemsDtlRow.createCell(2);
		        	if(i==1)cellItemsVal.setCellValue("24010168 ");        		
		        	if(i==2)cellItemsVal.setCellValue("Unit Perolehan Kementerian Kewangan");
		        	if(i==3)cellItemsVal.setCellValue(paymentMethod); 
		        	if(i==4){
		        		if(StringUtils.isNotBlank(params.get(SmReportConst.PARAM_COMPANY_NAME))){
		        			cellItemsVal.setCellValue(params.get(SmReportConst.PARAM_COMPANY_NAME));
		        		}else{
		        			cellItemsVal.setCellValue("Semua");
		        		}
		        	}
		        	if(i==5){
		        		itemsDtlRow.setHeightInPoints((paymentTypeSize * sheetList.getDefaultRowHeightInPoints()));
		        		cellItemsVal.setCellValue(paymentTypeDesc); 
		        	}
		        	cellItemsVal.setCellStyle(titleHeaderStyle(wb,false,false,true,true));
		        	sheetList.addMergedRegion(new CellRangeAddress(i,i,2,4)); 
		        }
			    
			    //Setting Column header list
			    Row headerRowList = sheetList.createRow(sheetList.getLastRowNum()+1);
		        for(int i = 0; i < columnHeaderList.size(); i++) {
		            Cell cell = headerRowList.createCell(i);
		            cell.setCellValue(columnHeaderList.get(i).toString());
		            cell.setCellStyle(columnStyle(wb,createHelper,true,true,true,false,false,false,false));
		        }
		        
		        Set<Integer> keyIdList = reportListMap.keySet();
				int rowid = sheetList.getLastRowNum()+1;
				int countRowFpx = 1;
				String receiptNo = "";
				for (Integer key : keyIdList) {
					Row row = sheetList.createRow(rowid++);
					Object[] objectArr = reportListMap.get(key);
					int cellid = 1;
					Cell cellBil = row.createCell(0);
					if(receiptNo == ""){
						cellBil.setCellValue(countRowFpx++);
					}else if(!receiptNo.equalsIgnoreCase((String)objectArr[2])){
						cellBil.setCellValue(countRowFpx++);
					}else{
						sheetList.addMergedRegion(new CellRangeAddress(rowid-2,rowid-1,0,0)); 
					}
					cellBil.setCellStyle(columnStyle(wb,createHelper,false,false,true,false,false,false,false));
					receiptNo = (String)objectArr[2];
					
					for(int x=0; x<objectArr.length; x++){
						if(x==0 && "Tarikh Resit".equalsIgnoreCase(params.get(SmReportConst.PARAM_RPT_TYPE)))continue;
						Cell cell = row.createCell(cellid);
						if(objectArr[x] instanceof Date){
							cell.setCellValue((Date) objectArr[x]);
							cell.setCellStyle(columnStyle(wb,createHelper,false,false,true,true,false,false,false));
						}else if(objectArr[x] instanceof BigDecimal){
							cell.setCellValue(((BigDecimal) objectArr[x]).doubleValue());
							cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,false));
						}else if(objectArr[x] instanceof Double){
							cell.setCellValue(((Double) objectArr[x]).doubleValue());
							cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,false));
						}else{
							if("null".equalsIgnoreCase((String) objectArr[x]))cell.setCellValue("");
							else cell.setCellValue((String) objectArr[x]);
							cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,true,false));
						}
						cellid++;
					}
				}
				//Setting Auto filtering in each header column list
		        sheetList.setAutoFilter(new CellRangeAddress(headerRowList.getRowNum(),sheetList.getLastRowNum(),1,columnHeaderList.size()-1));
		        // Resize all columns to fit the content size
		        for(int i = 0; i < columnHeaderList.size(); i++) {
		        	if("Tarikh Resit".equalsIgnoreCase(params.get(SmReportConst.PARAM_RPT_TYPE))){
		        		if(i==0)sheetList.setColumnWidth(i, 8*256);//Col : Bil
		            	else if(i==1)sheetList.setColumnWidth(i, 17*256); //Col : Tarikh Resit
		            	else if(i==2)sheetList.setColumnWidth(i, 19*256); //Col : No. Resit
		            	else if(i==3)sheetList.setColumnWidth(i, 50*256); //Col : Nama Pembekal
		            	else if(i==4)sheetList.setColumnWidth(i, 20*256); //Col : No. Permohonan
		            	else if(i==5)sheetList.setColumnWidth(i, 20*256); //Col : No. Rujukan
		            	else if(i==6)sheetList.setColumnWidth(i, 23*256); //Col : Jenis Terimaan-Kod
		            	else if(i==7)sheetList.setColumnWidth(i, 29*256); //Col : Jenis Terimaan-Perihal
		            	else if(i==8)sheetList.setColumnWidth(i, 15*256); //Col : Harga(RM)
		            	else if(i==9)sheetList.setColumnWidth(i, 15*256); //Col : GST (RM)
		            	else if(i==10)sheetList.setColumnWidth(i, 30*256); //Col : Caj Perkhidmatan
		            	else if(i==11)sheetList.setColumnWidth(i, 30*256); //Col : GST Caj Perkhidmatan
		            	else if(i==12)sheetList.setColumnWidth(i, 28*256); //Col : Amaun Bersih
		            	else if(i==13)sheetList.setColumnWidth(i, 28*256); //Col : Jumlah Keseluruhan       
		            	else if(i==14)sheetList.setColumnWidth(i, 22*256); //Col : Status       
		        	}else{
		        		if(i==0)sheetList.setColumnWidth(i, 8*256);//Col : Bil
		            	else if(i==1)sheetList.setColumnWidth(i, 20*256); //Col : Tarikh Tarikh Transaksi
		            	else if(i==2)sheetList.setColumnWidth(i, 20*256); //Col : Tarikh Resit
		            	else if(i==3)sheetList.setColumnWidth(i, 19*256); //Col : No. Resit
		            	else if(i==4)sheetList.setColumnWidth(i, 50*256); //Col : Nama Pembekal
		            	else if(i==5)sheetList.setColumnWidth(i, 20*256); //Col : No. Permohonan
		            	else if(i==6)sheetList.setColumnWidth(i, 20*256); //Col : No. Rujukan
		            	else if(i==7)sheetList.setColumnWidth(i, 23*256); //Col : Jenis Terimaan-Kod
		            	else if(i==8)sheetList.setColumnWidth(i, 29*256); //Col : Jenis Terimaan-Perihal
		            	else if(i==9)sheetList.setColumnWidth(i, 15*256); //Col : Harga(RM)
		            	else if(i==10)sheetList.setColumnWidth(i, 15*256); //Col : GST (RM)
		            	else if(i==11)sheetList.setColumnWidth(i, 30*256); //Col : Caj Perkhidmatan
		            	else if(i==12)sheetList.setColumnWidth(i, 30*256); //Col : GST Caj Perkhidmatan
		            	else if(i==13)sheetList.setColumnWidth(i, 28*256); //Col : Amaun Bersih
		            	else if(i==14)sheetList.setColumnWidth(i, 28*256); //Col : Jumlah Keseluruhan       
		            	else if(i==15)sheetList.setColumnWidth(i, 22*256); //Col : Status       
		        	}
		        }
				log.info("DONE GENERATE SENARAI!!!");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		//SECTION SHEET (Populate) : Ringkasan
		Sheet sheetSummary = wb.createSheet("Ringkasan"); 
		try{
			//Setting Report Title
			Row titleRowSheetSummary = sheetSummary.createRow(0);
			Cell cellTitleRowSummary = titleRowSheetSummary.createCell(0);
			cellTitleRowSummary.setCellValue(reportTitle);
			cellTitleRowSummary.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
			sheetSummary.addMergedRegion(new CellRangeAddress(0,0,0,columnsRingkasan.length+1)); 
			
			Row subTitleRowSheetSummary = sheetSummary.createRow(1);
			Cell cellSubTitleRowSummary = subTitleRowSheetSummary.createCell(0);
			cellSubTitleRowSummary.setCellValue("RINGKASAN LAPORAN TERIMAAN :");
			cellSubTitleRowSummary.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
			sheetSummary.addMergedRegion(new CellRangeAddress(1,1,0,columnsRingkasan.length+1)); 
			
			//Setting Column header list
		    Row headerRowListSummary = sheetSummary.createRow(sheetSummary.getLastRowNum()+1);
	        for(int i = 0; i < columnsRingkasan.length; i++) {
	            Cell cell = headerRowListSummary.createCell(i);
	            cell.setCellValue(columnsRingkasan[i]);
	            cell.setCellStyle(columnStyle(wb,createHelper,true,true,true,false,false,false,true));
	        }
	        
	        if(!summaryListMap.isEmpty()){
	        	log.info("SUMMARY IS NOT EMPTY !!!");
				Set<Integer> keyIdSummary = summaryListMap.keySet();
				int rowid = sheetSummary.getLastRowNum()+1;
				for (Integer key : keyIdSummary) {
					Row rowSummary = sheetSummary.createRow(rowid++);
					Object[] objectArr = summaryListMap.get(key);
					for(int x=0; x<objectArr.length; x++){
						Cell cell = rowSummary.createCell(x);
						if(objectArr[x] instanceof Date){
							cell.setCellValue((Date) objectArr[x]);
							if(key == (summaryListMap.size() - 1))cell.setCellStyle(columnStyle(wb,createHelper,false,true,true,true,false,false,true));
							else cell.setCellStyle(columnStyle(wb,createHelper,false,false,true,true,false,false,true));
						}else if(objectArr[x] instanceof BigDecimal){
							cell.setCellValue(((BigDecimal) objectArr[x]).doubleValue());
							if(key == (summaryListMap.size() - 1))cell.setCellStyle(columnStyle(wb,createHelper,false,true,false,false,true,false,true));
							else cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,true));
						}else if(objectArr[x] instanceof Double){
							cell.setCellValue(((Double) objectArr[x]).doubleValue());
							if(key == (summaryListMap.size() - 1))cell.setCellStyle(columnStyle(wb,createHelper,false,true,false,false,true,false,true));
							else cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,true));
						}else if(objectArr[x] instanceof Integer){
							cell.setCellValue(((Integer) objectArr[x]).intValue());
							if(key == (summaryListMap.size() - 1))cell.setCellStyle(columnStyle(wb,createHelper,false,true,false,false,false,false,true));
							else cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,false,true));
						}else{
							cell.setCellValue((String) objectArr[x]);
							if(key == (summaryListMap.size() - 1))cell.setCellStyle(columnStyle(wb,createHelper,false,true,true,false,false,false,true));
							else cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,false,true));
						}
					}
				}
	        }
	        
	        if(rsltSummaryList.isEmpty()){
	        	Row noRecordRow = sheetSummary.createRow(sheetSummary.getLastRowNum()+1);
				Cell cellNoRecord = noRecordRow.createCell(0);
				cellNoRecord.setCellValue("Rekod tidak dijumpai");
				cellNoRecord.setCellStyle(titleHeaderStyle(wb,false,false,false,false));
				sheetSummary.addMergedRegion(new CellRangeAddress(sheetSummary.getLastRowNum(),sheetSummary.getLastRowNum(),0,columnsRingkasan.length-1)); 
	        }
	        // Resize all columns to fit the content size
	        for(int i = 0; i < columnsRingkasan.length; i++) {
	        	if(i==0)sheetSummary.setColumnWidth(i, 17*256); //Col : Tarikh Resit/Transaksi
	        	else if(i==1)sheetSummary.setColumnWidth(i, 8*256); //Col : Bil
	        	else if(i==2)sheetSummary.setColumnWidth(i, 16*256); //Col : Harga (RM)
	        	else if(i==3)sheetSummary.setColumnWidth(i, 16*256); //Col : GST (RM)
	        	else if(i==4)sheetSummary.setColumnWidth(i, 28*256); //Col : Caj Perkhidmatan (RM)
	        	else if(i==5)sheetSummary.setColumnWidth(i, 28*256); //Col : GST Caj Perkhidmatan (RM)
	        	else if(i==6)sheetSummary.setColumnWidth(i, 26*256); //Col : Amaun Bersih (RM)
	        }
	        log.info("DONE GENERATE RINGKASAN !!!");
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
	    	String path = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_SHARED_REPORT_DIR);
	    	String fullPath = path+"/"+requestId+".xlsx";
	    	//String fullPath = "D://reports//dev//"+requestId+".xlsx";
	    	log.info("FULL PATH (CREATE FILE) == "+fullPath);
	    	FileOutputStream out = new FileOutputStream(new File(fullPath));
	    	wb.write(out);
	        out.close();
	        wb.close();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    log.info("DONE GENERATE REPORT COLLECTION REPORT BASED ON Payment Method !!");
	}
	
	public void getCollectionReportBasedOnClassificationCode(LinkedHashMap<String, String> params, BigDecimal requestId)throws Exception{
		Map<Integer, Object[]> fpxList = new TreeMap<Integer, Object[]>();
		Map<Integer, Object[]> creditList = new TreeMap<Integer, Object[]>();
		Map<Integer, Object[]> summaryList = new TreeMap<Integer, Object[]>();
		List<Object[]> reportList = new ArrayList<Object[]>();
		List<Object[]> summaryReportList = new ArrayList<Object[]>();
		int fpxCount = 0, creditCount = 0; 
		int totalCount = 0;
		BigDecimal totalPrice = new BigDecimal(0);
		BigDecimal totalGst = new BigDecimal(0); 
		BigDecimal totalServiceCharge = new BigDecimal(0);
		BigDecimal totalGstServiceCharge = new BigDecimal(0);
		BigDecimal totalNetAmount = new BigDecimal(0);
		
		String titleReport = "LAPORAN TERIMAAN BERDASARKAN KOD PENJENISAN BAGI TEMPOH " 
								+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateFrom()) + " HINGGA "
								+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateTo())
								+ " SEPERTI PADA "+ DateUtils.dateToString("dd/MM/yyyy",new Date())
								+ " BAGI TAHUN KEWANGAN "+ DateUtils.dateToString("yyyy",new Date());
		
		String[] sheetName = {"Senarai_FPX", "Senarai_Kad Kredit","Ringkasan"};
		String[] rowHeaderItems = {"Kod PTJ","Nama PTJ","Kod Penjenisan","Kaedah Bayaran"};
		//Update Func By Include label RM inside column Caj Perkhidmatan & GST Caj Perkhidmatan
		String[] columnsList = {
				"Bil","Tarikh","No Resit","No Permohonan","Jenis Terimaan-Kod","Jenis Terimaan-Perihal",
				"Harga (RM)","GST (RM)","Caj Perkhidmatan (RM)","GST Caj Perkhidmatan (RM)","Amaun Bersih (RM)","Jumlah Keseluruhan (RM)","Status"
		};
		
		String[] columnsSummaryList = {
				"Kod Penjenisan","Bil","Harga (RM)","GST (RM)","Caj Perkhidmatan (RM)","GST Caj Perkhidmatan (RM)","Amaun Bersih (RM)"
		};
		
		try{
			reportList = generateSMCollectionReportBasedClassificationCode(params,"L");
			if(!reportList.isEmpty()){
				for(int m=0; m<reportList.size(); m++){
					Object[] row = (Object[]) reportList.get(m);
					BigDecimal priceAmt = row[13] != null ? (BigDecimal)row[13] : BigDecimal.ZERO;
					BigDecimal gstAmt = row[15] != null ? (BigDecimal)row[15] : BigDecimal.ZERO;
					BigDecimal serviceCharge = row[9] != null ? (BigDecimal)row[9] : BigDecimal.ZERO;
					BigDecimal gstRate = row[16] != null ? (BigDecimal)row[16] : BigDecimal.ZERO;
					BigDecimal gstServiceCharge = serviceCharge.multiply(gstRate).setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE,BigDecimal.ROUND_HALF_UP);
					BigDecimal netPrice = (priceAmt.add(gstAmt).subtract(serviceCharge).subtract(gstServiceCharge)).setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE,BigDecimal.ROUND_HALF_UP);
					if("FPX".equalsIgnoreCase(row[1].toString())){
						fpxList.put(fpxCount, new Object[] {
								(Date) row[3],row[2].toString(),row[5].toString(),row[11].toString(),row[12].toString(),
								priceAmt,gstAmt,serviceCharge,gstServiceCharge,netPrice,netPrice,row[14].toString()
						});
						fpxCount++;
					}else{
						creditList.put(creditCount, new Object[] {
								(Date) row[3],row[2].toString(),row[5].toString(),row[11].toString(),row[12].toString(),
								priceAmt,gstAmt,serviceCharge,gstServiceCharge,netPrice,netPrice,row[14].toString()
						});
						creditCount++;
					}
				}
			}			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		/*Update #15990 : Include query to get summary of the collection report based on classification code */
		summaryReportList = generateSMCollectionReportBasedClassificationCode(params,"R");
		if(!summaryReportList.isEmpty()){
			for(int x=0; x<summaryReportList.size(); x++){
				Object[] row = (Object[]) summaryReportList.get(x);
				Integer total = row[1] != null ? Integer.parseInt(row[1].toString()) : 0;
				BigDecimal priceAmt = row[2] != null ? (BigDecimal)row[2] : BigDecimal.ZERO;
				BigDecimal gstAmt = row[3] != null ? (BigDecimal)row[3] : BigDecimal.ZERO;
				BigDecimal serviceCharge = row[4] != null ? (BigDecimal)row[4] : BigDecimal.ZERO;
				BigDecimal gstServiceCharge = row[5] != null ? (BigDecimal)row[5] : BigDecimal.ZERO;
				BigDecimal netPrice = (priceAmt.add(gstAmt).subtract(serviceCharge).subtract(gstServiceCharge)).setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE,BigDecimal.ROUND_HALF_UP);
				
				summaryList.put(x, new Object[]{row[0].toString(),total,priceAmt,gstAmt, serviceCharge,gstServiceCharge,netPrice});
				totalCount+= total;
				totalPrice = totalPrice.add(priceAmt);
				totalGst = totalGst.add(gstAmt);
				totalServiceCharge = totalServiceCharge.add(serviceCharge);
				totalGstServiceCharge = totalGstServiceCharge.add(gstServiceCharge);
				totalNetAmount = totalNetAmount.add(netPrice);
			}
			
			summaryList.put(summaryReportList.size(), new Object[] {
					"Keseluruhan",totalCount,totalPrice,totalGst,totalServiceCharge,totalGstServiceCharge,totalNetAmount
			});
		}
		
		Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		
		//Only Create Sheet Senarai_FPX only if search result has return records
		if(!fpxList.isEmpty()){
			Sheet sheetFPX = wb.createSheet(sheetName[0]); 
			//Setting Report Title
			Row titleRowSheetFpx = sheetFPX.createRow(0);
			Cell cellTitleRow = titleRowSheetFpx.createCell(1);
		    cellTitleRow.setCellValue(titleReport);
		    cellTitleRow.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
		    sheetFPX.addMergedRegion(new CellRangeAddress(0,0,1,columnsList.length-1)); 
		    
		    //Setting Row Items - Kod PTJ/Nama PTJ/Kod Penjenisan/Kaedah Bayaran
		    for(int i = 1; i <= rowHeaderItems.length; i++) {
	        	Row itemsDtlRow = sheetFPX.createRow(i);
	        	Cell cellItemsDtl = itemsDtlRow.createCell(1);
	        	cellItemsDtl.setCellValue(rowHeaderItems[i-1]);
	        	cellItemsDtl.setCellStyle(titleHeaderStyle(wb,false,false,false,false));
	        	Cell cellItemsVal = itemsDtlRow.createCell(2);
	        	if(i==1)cellItemsVal.setCellValue("24010168 ");        		
	        	if(i==2)cellItemsVal.setCellValue("Unit Perolehan Kementerian Kewangan");
	        	if(i==3)cellItemsVal.setCellValue(params.get(SmReportConst.PARAM_CODE));
	        	if(i==4)cellItemsVal.setCellValue("FPX"); 
	        	cellItemsVal.setCellStyle(titleHeaderStyle(wb,false,false,false,false));
	        	sheetFPX.addMergedRegion(new CellRangeAddress(i,i,2,3)); 
	        }
		    
		    //Setting Column header list
		    Row headerRowListFpx = sheetFPX.createRow(sheetFPX.getLastRowNum()+1);
	        for(int i = 0; i < columnsList.length; i++) {
	            Cell cell = headerRowListFpx.createCell(i);
	            cell.setCellValue(columnsList[i]);
	            cell.setCellStyle(columnStyle(wb,createHelper,true,true,true,false,false,false,true));
	        }
	        
	        Set<Integer> keyIdFpx = fpxList.keySet();
			int rowid = sheetFPX.getLastRowNum()+1;
			int countRowFpx = 1;
			for (Integer key : keyIdFpx) {
				Row row = sheetFPX.createRow(rowid++);
				Object[] objectArr = fpxList.get(key);
				int cellid = 1;
				Cell cellBil = row.createCell(0);
				cellBil.setCellValue(countRowFpx++);
				cellBil.setCellStyle(columnStyle(wb,createHelper,false,false,true,false,false,false,false));
				
				for (Object obj : objectArr) {
					Cell cell = row.createCell(cellid);
					if(obj instanceof Date){
						cell.setCellValue((Date) obj);
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,true,true,false,false,false));
					}else if(obj instanceof BigDecimal){
						cell.setCellValue(((BigDecimal) obj).doubleValue());
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,false));
					}else if(obj instanceof Double){
						cell.setCellValue(((Double) obj).doubleValue());
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,false));
					}else{
						cell.setCellValue((String) obj);
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,false,false));
					}
					cellid++;
				}
			}
			
			//Setting Auto filtering in each header column list
		    sheetFPX.setAutoFilter(new CellRangeAddress(headerRowListFpx.getRowNum(),sheetFPX.getLastRowNum(),1,columnsList.length-1));
		    // Resize all columns to fit the content size
	        for(int i = 0; i < columnsList.length; i++) {
	        	if(i==0)sheetFPX.setColumnWidth(i, 8*256);//Column : Bil	
	        	else if(i==1)sheetFPX.setColumnWidth(i, 16*256);//Column : Tarikh	
	        	else if(i==2)sheetFPX.setColumnWidth(i, 20*256);//Column : No. Resit	
	        	else if(i==3)sheetFPX.setColumnWidth(i, 20*256);//Column : No. Permohonan	
	        	else if(i==4)sheetFPX.setColumnWidth(i, 24*256);//Column : Jenis Terimaan - Kod
	        	else if(i==5)sheetFPX.setColumnWidth(i, 29*256);//Column : Jenis Terimaan - Perihal
	        	else if(i==6)sheetFPX.setColumnWidth(i, 15*256);//Column : Harga (RM)
	        	else if(i==7)sheetFPX.setColumnWidth(i, 15*256);//Column : GST (RM)
	        	else if(i==8)sheetFPX.setColumnWidth(i, 27*256);//Column : Caj Perkhidmatan 
	        	else if(i==9)sheetFPX.setColumnWidth(i, 30*256);//Column : GST Caj Perkhidmatan
	        	else if(i==10)sheetFPX.setColumnWidth(i, 23*256);//Column : Amaun Bersih
	        	else if(i==11)sheetFPX.setColumnWidth(i, 29*256);//Column : Jumlah Keseluruhan
	        	else if(i==12)sheetFPX.setColumnWidth(i, 22*256);//Column : Status
	        }
			log.info("DONE GENERATE SENARAI_FPX");
		}
		
		//Only Create Sheet Senarai_Kad Kredit only if search result has return records
		if(!creditList.isEmpty()){
			log.info("CREDIT LIST IS NOT NULL !!!");
			Sheet sheetCC = wb.createSheet(sheetName[1]); 
			//Setting Report Title
			Row titleRowSheetCC = sheetCC.createRow(0);
			Cell cellTitleRowCC = titleRowSheetCC.createCell(1);
			cellTitleRowCC.setCellValue(titleReport);
			cellTitleRowCC.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
			sheetCC.addMergedRegion(new CellRangeAddress(0,0,1,columnsList.length-1)); 
			
			//Setting Row Items - Kod PTJ/Nama PTJ/Kod Penjenisan/Kaedah Bayaran
		    for(int i = 1; i <= rowHeaderItems.length; i++) {
	        	Row itemsDtlRow = sheetCC.createRow(i);
	        	Cell cellItemsDtl = itemsDtlRow.createCell(1);
	        	cellItemsDtl.setCellValue(rowHeaderItems[i-1]);
	        	cellItemsDtl.setCellStyle(titleHeaderStyle(wb,false,false,false,false));
	        	Cell cellItemsVal = itemsDtlRow.createCell(2);
	        	if(i==1)cellItemsVal.setCellValue("24010168 ");        		
	        	if(i==2)cellItemsVal.setCellValue("Unit Perolehan Kementerian Kewangan");
	        	if(i==3)cellItemsVal.setCellValue(params.get(SmReportConst.PARAM_CODE));
	        	if(i==4)cellItemsVal.setCellValue("Kad Kredit"); 
	        	cellItemsVal.setCellStyle(titleHeaderStyle(wb,false,false,false,false));
	        	sheetCC.addMergedRegion(new CellRangeAddress(i,i,2,3)); 
	        }
			
			Row headerRowListCredit = sheetCC.createRow(sheetCC.getLastRowNum()+1);
	        for(int i = 0; i < columnsList.length; i++) {
	            Cell cell = headerRowListCredit.createCell(i);
	            cell.setCellValue(columnsList[i]);
	            cell.setCellStyle(columnStyle(wb,createHelper,true,true,true,false,false,false,true));
	        }
	        
			Set<Integer> keyIdCredit = creditList.keySet();
			int rowid = sheetCC.getLastRowNum()+1;
			int countRowCC = 1;
			for (Integer key : keyIdCredit) {
				Row row = sheetCC.createRow(rowid++);
				Object[] objectArr = creditList.get(key);
				int cellid = 1;
				Cell cellBil = row.createCell(0);
				cellBil.setCellValue(countRowCC++);
				cellBil.setCellStyle(columnStyle(wb,createHelper,false,false,true,false,false,false,false));
				
				for (Object obj : objectArr) {
					Cell cell = row.createCell(cellid);
					if(obj instanceof Date){
						cell.setCellValue((Date) obj);
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,true,true,false,false,false));
					}else if(obj instanceof BigDecimal){
						cell.setCellValue(((BigDecimal) obj).doubleValue());
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,false));
					}else if(obj instanceof Double){
						cell.setCellValue(((Double) obj).doubleValue());
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,false));
					}else{
						cell.setCellValue((String) obj);
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,false,false));
					}
					cellid++;
				}
			}
			//Setting Auto filtering in each header column list
			sheetCC.setAutoFilter(new CellRangeAddress(headerRowListCredit.getRowNum(),sheetCC.getLastRowNum(),1,columnsList.length-1));
			// Resize all columns to fit the content size
	        for(int i = 0; i < columnsList.length; i++) {
	        	if(i==0)sheetCC.setColumnWidth(i, 8*256);//Column : Bil	
	        	else if(i==1)sheetCC.setColumnWidth(i, 16*256);//Column : Tarikh	
	        	else if(i==2)sheetCC.setColumnWidth(i, 20*256);//Column : No. Resit	
	        	else if(i==3)sheetCC.setColumnWidth(i, 20*256);//Column : No. Permohonan	
	        	else if(i==4)sheetCC.setColumnWidth(i, 24*256);//Column : Jenis Terimaan - Kod
	        	else if(i==5)sheetCC.setColumnWidth(i, 29*256);//Column : Jenis Terimaan - Perihal
	        	else if(i==6)sheetCC.setColumnWidth(i, 15*256);//Column : Harga (RM)
	        	else if(i==7)sheetCC.setColumnWidth(i, 15*256);//Column : GST (RM)
	        	else if(i==8)sheetCC.setColumnWidth(i, 25*256);//Column : Caj Perkhidmatan 
	        	else if(i==9)sheetCC.setColumnWidth(i, 30*256);//Column : GST Caj Perkhidmatan
	        	else if(i==10)sheetCC.setColumnWidth(i, 23*256);//Column : Amaun Bersih
	        	else if(i==11)sheetCC.setColumnWidth(i, 29*256);//Column : Jumlah Keseluruhan
	        	else if(i==12)sheetCC.setColumnWidth(i, 22*256);//Column : Status
	        }
			log.info("DONE GENERATE SENARAI_KAD KREDIT");
		}
		
		//SECTION SHEET (Populate) : Ringkasan
		Sheet sheetSummary = wb.createSheet(sheetName[2]); 
        //Setting Report Title
		Row titleRowSheetSummary = sheetSummary.createRow(0);
		Cell cellTitleRowSummary = titleRowSheetSummary.createCell(0);
		cellTitleRowSummary.setCellValue(titleReport);
		cellTitleRowSummary.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
		sheetSummary.addMergedRegion(new CellRangeAddress(0,0,0,columnsSummaryList.length+3)); 
		
		Row subTitleRowSheetSummary = sheetSummary.createRow(1);
		Cell cellSubTitleRowSummary = subTitleRowSheetSummary.createCell(0);
		cellSubTitleRowSummary.setCellValue("RINGKASAN LAPORAN TERIMAAN :");
		cellSubTitleRowSummary.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
		sheetSummary.addMergedRegion(new CellRangeAddress(1,1,0,columnsSummaryList.length+3)); 
		
		//Setting Column header list
	    Row headerRowListSummary = sheetSummary.createRow(sheetSummary.getLastRowNum()+1);
        for(int i = 0; i < columnsSummaryList.length; i++) {
            Cell cell = headerRowListSummary.createCell(i);
            cell.setCellValue(columnsSummaryList[i]);
            cell.setCellStyle(columnStyle(wb,createHelper,true,true,true,false,false,false,true));
        }
        
        if(!summaryReportList.isEmpty()){
			log.info("SUMMARY LIST IS NOT NULL !!!");
			Set<Integer> keyIdSummary = summaryList.keySet();
			int rowid = sheetSummary.getLastRowNum()+1;
			for (Integer key : keyIdSummary) {
				Row row = sheetSummary.createRow(rowid++);
				Object[] objectArr = summaryList.get(key);
				int cellid = 0;
				for (Object obj : objectArr) {
					Cell cell = row.createCell(cellid++);
					if(obj instanceof BigDecimal){
						cell.setCellValue(((BigDecimal) obj).doubleValue());
						if(key == 0)cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,true));
						else cell.setCellStyle(columnStyle(wb,createHelper,false,true,false,false,true,false,true));
					}else if(obj instanceof Integer){
						cell.setCellValue(((Integer) obj).intValue());
						if(key == 0)cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,true,true));
						else cell.setCellStyle(columnStyle(wb,createHelper,false,true,false,false,false,true,true));
					}else{
						cell.setCellValue((String) obj);
						if(key == 0)cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,false,true));
						else cell.setCellStyle(columnStyle(wb,createHelper,false,true,false,false,false,false,true));
					}
				}
			}
		}
        
        if(reportList.isEmpty()){
        	summaryList.put(0, new Object[] {
					"Keseluruhan",(fpxCount+creditCount),totalPrice,totalGst,totalServiceCharge,totalGstServiceCharge,totalNetAmount
			});
        	
        	Set<Integer> keyIdSummary = summaryList.keySet();
			int rowid = sheetSummary.getLastRowNum()+1;
			for (Integer key : keyIdSummary) {
				Row row = sheetSummary.createRow(rowid++);
				Object[] objectArr = summaryList.get(key);
				int cellid = 0;
				for (Object obj : objectArr) {
					Cell cell = row.createCell(cellid++);
					if(obj instanceof BigDecimal){
						cell.setCellValue(((BigDecimal) obj).doubleValue());
						cell.setCellStyle(columnStyle(wb,createHelper,false,true,false,false,true,false,true));
					}else if(obj instanceof Integer){
						cell.setCellValue(((Integer) obj).intValue());
						cell.setCellStyle(columnStyle(wb,createHelper,false,true,false,false,false,false,true));
					}else{
						cell.setCellValue((String) obj);
						cell.setCellStyle(columnStyle(wb,createHelper,false,true,false,false,false,false,true));
					}
				}
			}
        	
        	Row noRecordRow = sheetSummary.createRow(sheetSummary.getLastRowNum()+1);
			Cell cellNoRecord = noRecordRow.createCell(0);
			cellNoRecord.setCellValue("Rekod tidak dijumpai");
			cellNoRecord.setCellStyle(titleHeaderStyle(wb,false,false,false,false));
			sheetSummary.addMergedRegion(new CellRangeAddress(sheetSummary.getLastRowNum(),sheetSummary.getLastRowNum(),0,columnsSummaryList.length-1)); 
        }
        
        // Resize all columns to fit the content size
        for(int i = 0; i < columnsSummaryList.length; i++) {  
        	if(i==0)sheetSummary.setColumnWidth(i, 17*256);//Column : Kod Penjenisan
        	else if(i==1)sheetSummary.setColumnWidth(i, 8*256);//Column : Bil
        	else if(i==2)sheetSummary.setColumnWidth(i, 15*256);//Column : Harga
        	else if(i==3)sheetSummary.setColumnWidth(i, 15*256);//Column : GST
        	else if(i==4)sheetSummary.setColumnWidth(i, 24*256);//Column : Caj Perkhidmatan
        	else if(i==5)sheetSummary.setColumnWidth(i, 29*256);//Column : GST Caj Perkhidmatan
        	else if(i==6)sheetSummary.setColumnWidth(i, 24*256);//Column : GST Caj Perkhidmatan
        }
        try{
        	String path = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_SHARED_REPORT_DIR);
        	String fullPath = path+"/"+requestId+".xlsx";
        	//String fullPath = "D://reports//dev//"+requestId+".xlsx";
        	log.info("FULL PATH (CREATE FILE) == "+fullPath);
        	FileOutputStream out = new FileOutputStream(new File(fullPath));
        	wb.write(out);
            out.close();
            wb.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
	    log.info("DONE GENERATE Collection Report Based on Classification Code !!");
	}
	
	public void getCollectionReportBasedOnReceiptAndTransactionDate(LinkedHashMap<String, String> params, BigDecimal requestId)throws Exception{
		Map<Integer, Object[]> fpxList = new TreeMap<Integer, Object[]>();
		Map<Integer, Object[]> creditList = new TreeMap<Integer, Object[]>();
		Map<Integer, Object[]> summaryList = new TreeMap<Integer, Object[]>();
		List<Object[]> reportList = new ArrayList<Object[]>();
		List<Object[]> summaryReportList = new ArrayList<Object[]>();
		int fpxCount = 0,creditCount = 0,countBil = 0;
		BigDecimal totalPrice = new BigDecimal(0);
		BigDecimal totalGst = new BigDecimal(0);
		BigDecimal totalServiceCharge = new BigDecimal(0);
		BigDecimal totalGstServiceCharge = new BigDecimal(0);
		BigDecimal totalNetAmount = new BigDecimal(0);
		String reportType = "";
		String paymentTDesc = "";
		
		String[] sheetName = {"Senarai_FPX", "Senarai_Kad Kredit","Ringkasan"};
		String[] rowHeaderItems = {"Kod PTJ","Nama PTJ","Kaedah Bayaran","Nama Pembekal","Jenis Bayaran"};	
		//Update Func By Include label RM inside column Caj Perkhidmatan & GST Caj Perkhidmatan
		ArrayList<String> columnHeaderList = new ArrayList<String>(Arrays.asList(
				"Bil","Tarikh Transaksi","Tarikh Resit","No Resit","Nama Pembekal","No Permohonan","No Rujukan","Jenis Terimaan-Kod","Jenis Terimaan-Perihal",
				"Harga (RM)","GST (RM)","Caj Perkhidmatan (RM)","GST Caj Perkhidmatan (RM)","Amaun Bersih (RM)","Jumlah Keseluruhan (RM)","Status"
		));
		
		if("Tarikh Resit".equalsIgnoreCase(params.get(SmReportConst.PARAM_RPT_TYPE)))reportType = "Tarikh Resit";
		else reportType = "Tarikh Transaksi";
		
		String titleReport = "LAPORAN TERIMAAN BERDASARKAN "+reportType.toUpperCase()+" BAGI TEMPOH " 
				+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateFrom()) + " HINGGA "
				+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateTo())
				+ " SEPERTI PADA "+ DateUtils.dateToString("dd/MM/yyyy",new Date())
				+ " BAGI TAHUN KEWANGAN "+ DateUtils.dateToString("yyyy",new Date());

		String[] columnsSummaryList = {
				reportType,"Bil","Harga (RM)","GST (RM)","Caj Perkhidmatan (RM)","GST Caj Perkhidmatan (RM)","Amaun Bersih (RM)"
		};
		try{
			List<String> payType = new ArrayList<String>();
			List<String> paymentTypeDesc = new ArrayList<String>();
			if("M".equalsIgnoreCase(params.get(SmReportConst.PARAM_PAYMENT_TYPE1))){//MOF Registration Fee
				payType.add("MF");
				payType.add("RF");
				paymentTypeDesc.add("Bayaran Pendaftaran");
			}
			
			if("P".equalsIgnoreCase(params.get(SmReportConst.PARAM_PAYMENT_TYPE2))){//Processing Fee
				payType.add("PF");
				payType.add("RPF");
				paymentTypeDesc.add("Bayaran Proses");
			}
			
			String paymentType = "";
			if("S".equalsIgnoreCase(params.get(SmReportConst.PARAM_PAYMENT_TYPE3))){//Soft Certificate Fee
				payType.add("SF");
				paymentTypeDesc.add("Bayaran Sijil Digital");
			}
			
			for(int b=0; b<payType.size(); b++){
				if(b != payType.size()-1){
					paymentType += "'"+payType.get(b).toString() +"',";
				}else{
					paymentType += "'"+payType.get(b).toString()+"'";
				}
			}
			
			for(int x=0; x<paymentTypeDesc.size(); x++){
				if(x != (paymentTypeDesc.size() - 1)){
					paymentTDesc += paymentTypeDesc.get(x).toString()+", ";
				}else{
					paymentTDesc += paymentTypeDesc.get(x).toString();
				}
			}
			params.put("PAYMENTTYPE",paymentType);
			reportList = generateSMCollectionReportBasedReceiptAndTransactionDate(params, "L");
			summaryReportList = generateSMCollectionReportBasedReceiptAndTransactionDate(params, "R");
			if(!reportList.isEmpty()){
				for(int m=0; m<reportList.size(); m++){
					Object[] row = (Object[]) reportList.get(m);
					BigDecimal priceAmt = row[14] != null ? (BigDecimal)row[14] : BigDecimal.ZERO;
					BigDecimal gstAmt = row[15] != null ? (BigDecimal)row[15] : BigDecimal.ZERO;
					BigDecimal serviceCharge = row[10] != null ? (BigDecimal)row[10] : BigDecimal.ZERO;
					BigDecimal gstRate = row[18] != null ? (BigDecimal)row[18] : BigDecimal.ZERO;
					BigDecimal gstServiceCharge = serviceCharge.multiply(gstRate).setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE,BigDecimal.ROUND_HALF_UP);
					BigDecimal netPrice = priceAmt.add(gstAmt).subtract(serviceCharge).subtract(gstServiceCharge).setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE,BigDecimal.ROUND_HALF_UP);
					if("FPX".equalsIgnoreCase(row[1].toString())){
						fpxList.put(fpxCount, new Object[] {
							(Date) row[4],//Tarikh Transaksi
							(Date) row[3],//Tarikh Resit
							row[2].toString(),//No Resit
							row[5].toString(),//Nama Pembekal
							String.valueOf(row[6]),//No Permohonan
							row[7].toString(),//No Rujukan
							row[12].toString(),//Jenis Terimaan-Kod
							row[13].toString(),//Jenis Terimaan-Perihal
							priceAmt ,//Harga
							gstAmt,//GST
							serviceCharge, // Caj Perkhidmatan 
							gstServiceCharge,//GST Caj Perkhidmatan
							netPrice,//Amaun Bersih
							netPrice,//Jumlah Keseluruhan
							row[16].toString()//Status
						});
						fpxCount++;
					}else{
						creditList.put(creditCount, new Object[] {
							(Date) row[4],//Tarikh Transaksi
							(Date) row[3],//Tarikh Resit
							row[2].toString(),//No Resit
							row[5].toString(),//Nama Pembekal
							String.valueOf(row[6]),//No Permohonan
							row[7].toString(),//No Rujukan
							row[12].toString(),//Jenis Terimaan-Kod
							row[13].toString(),//Jenis Terimaan-Perihal
							priceAmt ,//Harga
							gstAmt,//GST
							serviceCharge, // Caj Perkhidmatan 
							gstServiceCharge,//GST Caj Perkhidmatan
							netPrice,//Amaun Bersih
							netPrice,//Jumlah Keseluruhan
							row[16].toString()//Status
						});
						creditCount++;
					}
				}
			}
			
			if(!summaryReportList.isEmpty()){
				for(int z=0; z<summaryReportList.size(); z++){
					Object[] row = (Object[]) summaryReportList.get(z);
					countBil += Integer.parseInt(row[1].toString());
					totalPrice = totalPrice.add((BigDecimal)row[2]); 
					totalGst = totalGst.add((BigDecimal)row[3]);
					totalServiceCharge = totalServiceCharge.add((BigDecimal)row[4]);
					totalGstServiceCharge = totalGstServiceCharge.add((BigDecimal)row[5]);
					
					BigDecimal netAmount = ((BigDecimal)row[2]).add((BigDecimal)row[3])
							.subtract((BigDecimal)row[4]).subtract((BigDecimal)row[5]).setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE,BigDecimal.ROUND_HALF_UP);
					summaryList.put(z, new Object[]{
						(Date) row[0],//Tarikh Resit/Transaksi
						Integer.parseInt(row[1].toString()),//Bil
						(BigDecimal) row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO ,//Harga
						(BigDecimal) row[3] != null ? (BigDecimal) row[3] : BigDecimal.ZERO ,//GST
						(BigDecimal) row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO ,//Caj Perkhidmatan
						(BigDecimal) row[5] != null ? (BigDecimal) row[5] : BigDecimal.ZERO ,//GST Caj Perkhidmatan
						netAmount //Amaun Bersih
					});
				}
				totalNetAmount = totalPrice.add(totalGst).subtract(totalServiceCharge).subtract(totalGstServiceCharge).setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE,BigDecimal.ROUND_HALF_UP);
			}
			summaryList.put(summaryReportList.size(), new Object[]{
				"Keseluruhan",countBil,totalPrice,totalGst,totalServiceCharge,totalGstServiceCharge,totalNetAmount
			});
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if("Tarikh Resit".equalsIgnoreCase(params.get(SmReportConst.PARAM_RPT_TYPE)))columnHeaderList.remove(1);
		
		Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		//Only Create  Sheet of Senarai_FPX , only if search result has return records
		if(!fpxList.isEmpty()){
			log.info("FPX LIST IS NOT EMPTY !!!");
			Sheet sheetFPX = wb.createSheet(sheetName[0]); 
			//Setting Report Title
			Row titleRowSheetFpx = sheetFPX.createRow(0);
			Cell cellTitleRow = titleRowSheetFpx.createCell(1);
		    cellTitleRow.setCellValue(titleReport);
		    cellTitleRow.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
		    sheetFPX.addMergedRegion(new CellRangeAddress(0,0,1,columnHeaderList.size()-1)); 
		    
		    //Setting Row Items - Kod PTJ/Nama PTJ/Kaedah Bayaran/Nama Pembekal/Jenis Bayaran
		    for(int i = 1; i <= rowHeaderItems.length; i++) {
	        	Row itemsDtlRow = sheetFPX.createRow(i);
	        	Cell cellItemsDtl = itemsDtlRow.createCell(1);
	        	cellItemsDtl.setCellValue(rowHeaderItems[i-1]);
	        	cellItemsDtl.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
	        	Cell cellItemsVal = itemsDtlRow.createCell(2);
	        	if(i==1)cellItemsVal.setCellValue("24010168 ");        		
	        	if(i==2)cellItemsVal.setCellValue("Unit Perolehan Kementerian Kewangan");
	        	if(i==3)cellItemsVal.setCellValue("FPX"); 
	        	if(i==4){
	        		if(StringUtils.isNotBlank(params.get(SmReportConst.PARAM_COMPANY_NAME))){
	        			cellItemsVal.setCellValue(params.get(SmReportConst.PARAM_COMPANY_NAME));
	        		}else{
	        			cellItemsVal.setCellValue("Semua");
	        		}
	        	}
	        	if(i==5)cellItemsVal.setCellValue(paymentTDesc); 
	        	cellItemsVal.setCellStyle(titleHeaderStyle(wb,false,false,false,false));
	        	sheetFPX.addMergedRegion(new CellRangeAddress(i,i,2,4)); 
	        }
		    
		    //Setting Column header list
		    Row headerRowListFpx = sheetFPX.createRow(sheetFPX.getLastRowNum()+1);
	        for(int i = 0; i < columnHeaderList.size(); i++) {
	            Cell cell = headerRowListFpx.createCell(i);
	            cell.setCellValue(columnHeaderList.get(i).toString());
	            cell.setCellStyle(columnStyle(wb,createHelper,true,true,true,false,false,false,true));
	        }
	        //Start : Populate Record
			Set<Integer> keyIdFpx = fpxList.keySet();
			int rowid = sheetFPX.getLastRowNum()+1;
			int countRowFpx = 1;
			String receiptNo = "";
			for (Integer key : keyIdFpx) {
				Row row = sheetFPX.createRow(rowid++);
				Object[] objectArr = fpxList.get(key);
				
				int cellid = 1;
				Cell cellBil = row.createCell(0);
				if(receiptNo == ""){
					cellBil.setCellValue(countRowFpx++);
				}else if(!receiptNo.equalsIgnoreCase((String)objectArr[2])){
					cellBil.setCellValue(countRowFpx++);
				}else{
					sheetFPX.addMergedRegion(new CellRangeAddress(rowid-2,rowid-1,0,0)); 
				}
				cellBil.setCellStyle(columnStyle(wb,createHelper,false,false,true,false,false,false,false));
				receiptNo = (String)objectArr[2];
				for(int x=0; x<objectArr.length; x++){
					if(x==0 && "Tarikh Resit".equalsIgnoreCase(params.get(SmReportConst.PARAM_RPT_TYPE)))continue;
					Cell cell = row.createCell(cellid);
					if(objectArr[x] instanceof Date){
						cell.setCellValue((Date) objectArr[x]);
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,true,true,false,false,false));
					}else if(objectArr[x] instanceof BigDecimal){
						cell.setCellValue(((BigDecimal) objectArr[x]).doubleValue());
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,false));
					}else if(objectArr[x] instanceof Double){
						cell.setCellValue(((Double) objectArr[x]).doubleValue());
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,false));
					}else{
						if("null".equalsIgnoreCase((String) objectArr[x]))cell.setCellValue("");
						else cell.setCellValue((String) objectArr[x]);
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,true,false));
					}
					cellid++;
				}
			}
			//Setting Auto filtering in each header column list
		    sheetFPX.setAutoFilter(new CellRangeAddress(headerRowListFpx.getRowNum(),sheetFPX.getLastRowNum(),1,columnHeaderList.size()-1));
		
		    //Resize all columns to fit the content size
	        for(int i = 0; i < columnHeaderList.size(); i++) {
	        	if("Tarikh Resit".equalsIgnoreCase(params.get(SmReportConst.PARAM_RPT_TYPE))){
	        		if(i==0)sheetFPX.setColumnWidth(i, 8*256);//Col : Bil
	            	else if(i==1)sheetFPX.setColumnWidth(i, 17*256); //Col : Tarikh Resit
	            	else if(i==2)sheetFPX.setColumnWidth(i, 19*256); //Col : No. Resit
	            	else if(i==3)sheetFPX.setColumnWidth(i, 50*256); //Col : Nama Pembekal
	            	else if(i==4)sheetFPX.setColumnWidth(i, 20*256); //Col : No. Permohonan
	            	else if(i==5)sheetFPX.setColumnWidth(i, 20*256); //Col : No. Rujukan
	            	else if(i==6)sheetFPX.setColumnWidth(i, 23*256); //Col : Jenis Terimaan-Kod
	            	else if(i==7)sheetFPX.setColumnWidth(i, 29*256); //Col : Jenis Terimaan-Perihal
	            	else if(i==8)sheetFPX.setColumnWidth(i, 15*256); //Col : Harga(RM)
	            	else if(i==9)sheetFPX.setColumnWidth(i, 15*256); //Col : GST (RM)
	            	else if(i==10)sheetFPX.setColumnWidth(i, 30*256); //Col : Caj Perkhidmatan
	            	else if(i==11)sheetFPX.setColumnWidth(i, 30*256); //Col : GST Caj Perkhidmatan
	            	else if(i==12)sheetFPX.setColumnWidth(i, 28*256); //Col : Amaun Bersih
	            	else if(i==13)sheetFPX.setColumnWidth(i, 28*256); //Col : Jumlah Keseluruhan       
	            	else if(i==14)sheetFPX.setColumnWidth(i, 22*256); //Col : Status       
	        	}else{
	        		if(i==0)sheetFPX.setColumnWidth(i, 8*256);//Col : Bil
	            	else if(i==1)sheetFPX.setColumnWidth(i, 20*256); //Col : Tarikh Tarikh Transaksi
	            	else if(i==2)sheetFPX.setColumnWidth(i, 20*256); //Col : Tarikh Resit
	            	else if(i==3)sheetFPX.setColumnWidth(i, 19*256); //Col : No. Resit
	            	else if(i==4)sheetFPX.setColumnWidth(i, 50*256); //Col : Nama Pembekal
	            	else if(i==5)sheetFPX.setColumnWidth(i, 20*256); //Col : No. Permohonan
	            	else if(i==6)sheetFPX.setColumnWidth(i, 20*256); //Col : No. Rujukan
	            	else if(i==7)sheetFPX.setColumnWidth(i, 23*256); //Col : Jenis Terimaan-Kod
	            	else if(i==8)sheetFPX.setColumnWidth(i, 29*256); //Col : Jenis Terimaan-Perihal
	            	else if(i==9)sheetFPX.setColumnWidth(i, 15*256); //Col : Harga(RM)
	            	else if(i==10)sheetFPX.setColumnWidth(i, 15*256); //Col : GST (RM)
	            	else if(i==11)sheetFPX.setColumnWidth(i, 30*256); //Col : Caj Perkhidmatan
	            	else if(i==12)sheetFPX.setColumnWidth(i, 30*256); //Col : GST Caj Perkhidmatan
	            	else if(i==13)sheetFPX.setColumnWidth(i, 28*256); //Col : Amaun Bersih
	            	else if(i==14)sheetFPX.setColumnWidth(i, 28*256); //Col : Jumlah Keseluruhan       
	            	else if(i==15)sheetFPX.setColumnWidth(i, 22*256); //Col : Status       
	        	}
	        }
		}
		
		//Only Create Sheet of Senarai_Kad Kredit , only if search result has return records
		if(!creditList.isEmpty()){
			log.info("CREDIT LIST IS NOT EMPTY !!!");
			Sheet sheetCC = wb.createSheet(sheetName[1]); 
	        //Setting Report Title
			Row titleRowSheetCC = sheetCC.createRow(0);
			Cell cellTitleRowCC = titleRowSheetCC.createCell(1);
			cellTitleRowCC.setCellValue(titleReport);
			cellTitleRowCC.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
			sheetCC.addMergedRegion(new CellRangeAddress(0,0,1,columnHeaderList.size()-1)); 
			
			//Setting Row Items - Kod PTJ/Nama PTJ/Kaedah Bayaran/Nama Pembekal/Jenis Bayaran
		    for(int i = 1; i <= rowHeaderItems.length; i++) {
	        	Row itemsDtlRow = sheetCC.createRow(i);
	        	Cell cellItemsDtl = itemsDtlRow.createCell(1);
	        	cellItemsDtl.setCellValue(rowHeaderItems[i-1]);
	        	cellItemsDtl.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
	        	Cell cellItemsVal = itemsDtlRow.createCell(2);
	        	if(i==1)cellItemsVal.setCellValue("24010168 ");        		
	        	if(i==2)cellItemsVal.setCellValue("Unit Perolehan Kementerian Kewangan");
	        	if(i==3)cellItemsVal.setCellValue("Kad Kredit"); 
	        	if(i==4){
	        		if(StringUtils.isNotBlank(params.get(SmReportConst.PARAM_COMPANY_NAME))){
	        			cellItemsVal.setCellValue(params.get(SmReportConst.PARAM_COMPANY_NAME));
	        		}else{
	        			cellItemsVal.setCellValue("Semua");
	        		}
	        	}
	        	if(i==5)cellItemsVal.setCellValue(paymentTDesc); 
	        	cellItemsVal.setCellStyle(titleHeaderStyle(wb,false,false,false,false));
	        	sheetCC.addMergedRegion(new CellRangeAddress(i,i,2,4)); 
	        }
		    
		    //Setting Column header list
		    Row headerRowListCC = sheetCC.createRow(sheetCC.getLastRowNum()+1);
	        for(int i = 0; i < columnHeaderList.size(); i++) {
	            Cell cell = headerRowListCC.createCell(i);
	            cell.setCellValue(columnHeaderList.get(i));
	            cell.setCellStyle(columnStyle(wb,createHelper,true,true,true,false,false,false,true));
	        }
	        
	        Set<Integer> keyIdCredit = creditList.keySet();
			int rowid = sheetCC.getLastRowNum()+1;
			int countRowCC = 1;
			String receiptNoCC = "";
			for (Integer key : keyIdCredit) {
				Row row = sheetCC.createRow(rowid++);
				Object[] objectArr = creditList.get(key);
				int cellid = 1;
				Cell cellBil = row.createCell(0);
				if(receiptNoCC == ""){
					cellBil.setCellValue(countRowCC++);
				}else if(!receiptNoCC.equalsIgnoreCase((String)objectArr[2])){
					cellBil.setCellValue(countRowCC++);
				}else{
					sheetCC.addMergedRegion(new CellRangeAddress(rowid-2,rowid-1,0,0)); 	
				}
				cellBil.setCellStyle(columnStyle(wb,createHelper,false,false,true,false,false,false,false));
				receiptNoCC = (String)objectArr[2];
				for (int x=0; x<objectArr.length; x++) {
					if(x==0 && "Tarikh Resit".equalsIgnoreCase(params.get(SmReportConst.PARAM_RPT_TYPE)))continue;
					Cell cell = row.createCell(cellid);
					if(objectArr[x] instanceof Date){
						cell.setCellValue((Date) objectArr[x]);
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,true,true,false,false,false));
					}else if(objectArr[x] instanceof BigDecimal){
						cell.setCellValue(((BigDecimal) objectArr[x]).doubleValue());
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,false));
					}else if(objectArr[x] instanceof Double){
						cell.setCellValue(((Double) objectArr[x]).doubleValue());
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,false));
					}else{
						if("null".equalsIgnoreCase((String) objectArr[x]))cell.setCellValue("");
						else cell.setCellValue((String) objectArr[x]);
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,true,false));
					}
					cellid++;
				}
			}

			//Setting Auto filtering in each header column list
	  		sheetCC.setAutoFilter(new CellRangeAddress(headerRowListCC.getRowNum(),sheetCC.getLastRowNum(),1,columnHeaderList.size()-1));
	  		
	  		// Resize all columns to fit the content size
	        for(int i = 0; i < columnHeaderList.size(); i++) {   
	        	if("Tarikh Resit".equalsIgnoreCase(params.get(SmReportConst.PARAM_RPT_TYPE))){
	        		if(i==0)sheetCC.setColumnWidth(i, 8*256);//Col : Bil
	            	else if(i==1)sheetCC.setColumnWidth(i, 17*256); //Col : Tarikh Resit
	            	else if(i==2)sheetCC.setColumnWidth(i, 19*256); //Col : No. Resit
	            	else if(i==3)sheetCC.setColumnWidth(i, 50*256); //Col : Nama Pembekal
	            	else if(i==4)sheetCC.setColumnWidth(i, 20*256); //Col : No. Permohonan
	            	else if(i==5)sheetCC.setColumnWidth(i, 20*256); //Col : No. Rujukan
	            	else if(i==6)sheetCC.setColumnWidth(i, 23*256); //Col : Jenis Terimaan-Kod
	            	else if(i==7)sheetCC.setColumnWidth(i, 29*256); //Col : Jenis Terimaan-Perihal
	            	else if(i==8)sheetCC.setColumnWidth(i, 15*256); //Col : Harga(RM)
	            	else if(i==9)sheetCC.setColumnWidth(i, 15*256); //Col : GST (RM)
	            	else if(i==10)sheetCC.setColumnWidth(i, 30*256); //Col : Caj Perkhidmatan
	            	else if(i==11)sheetCC.setColumnWidth(i, 30*256); //Col : GST Caj Perkhidmatan
	            	else if(i==12)sheetCC.setColumnWidth(i, 28*256); //Col : Amaun Bersih
	            	else if(i==13)sheetCC.setColumnWidth(i, 28*256); //Col : Jumlah Keseluruhan  
	            	else if(i==14)sheetCC.setColumnWidth(i, 22*256); //Col : Status 
	        	}else{
	        		if(i==0)sheetCC.setColumnWidth(i, 8*256);//Col : Bil
	        		else if(i==1)sheetCC.setColumnWidth(i, 20*256); //Col : Tarikh Transaksi
	            	else if(i==2)sheetCC.setColumnWidth(i, 20*256); //Col : Tarikh Resit
	            	else if(i==3)sheetCC.setColumnWidth(i, 19*256); //Col : No. Resit
	            	else if(i==4)sheetCC.setColumnWidth(i, 50*256); //Col : Nama Pembekal
	            	else if(i==5)sheetCC.setColumnWidth(i, 20*256); //Col : No. Permohonan
	            	else if(i==6)sheetCC.setColumnWidth(i, 20*256); //Col : No. Rujukan
	            	else if(i==7)sheetCC.setColumnWidth(i, 23*256); //Col : Jenis Terimaan-Kod
	            	else if(i==8)sheetCC.setColumnWidth(i, 29*256); //Col : Jenis Terimaan-Perihal
	            	else if(i==9)sheetCC.setColumnWidth(i, 15*256); //Col : Harga(RM)
	            	else if(i==10)sheetCC.setColumnWidth(i, 15*256); //Col : GST (RM)
	            	else if(i==11)sheetCC.setColumnWidth(i, 30*256); //Col : Caj Perkhidmatan
	            	else if(i==12)sheetCC.setColumnWidth(i, 30*256); //Col : GST Caj Perkhidmatan
	            	else if(i==13)sheetCC.setColumnWidth(i, 28*256); //Col : Amaun Bersih
	            	else if(i==14)sheetCC.setColumnWidth(i, 28*256); //Col : Jumlah Keseluruhan  
	            	else if(i==15)sheetCC.setColumnWidth(i, 22*256); //Col : Status 
	        	}
	        }
		}
		
		//SECTION SHEET (Populate) : Ringkasan
		Sheet sheetSummary = wb.createSheet(sheetName[2]); 
        //Setting Report Title
		Row titleRowSheetSummary = sheetSummary.createRow(0);
		Cell cellTitleRowSummary = titleRowSheetSummary.createCell(0);
		cellTitleRowSummary.setCellValue(titleReport);
		cellTitleRowSummary.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
		sheetSummary.addMergedRegion(new CellRangeAddress(0,0,0,columnsSummaryList.length)); 
		
		Row subTitleRowSheetSummary = sheetSummary.createRow(1);
		Cell cellSubTitleRowSummary = subTitleRowSheetSummary.createCell(0);
		cellSubTitleRowSummary.setCellValue("RINGKASAN LAPORAN TERIMAAN :");
		cellSubTitleRowSummary.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
		sheetSummary.addMergedRegion(new CellRangeAddress(1,1,0,columnsSummaryList.length)); 
		
		//Setting Column header list
	    Row headerRowListSummary = sheetSummary.createRow(sheetSummary.getLastRowNum()+1);
        for(int i = 0; i < columnsSummaryList.length; i++) {
            Cell cell = headerRowListSummary.createCell(i);
            cell.setCellValue(columnsSummaryList[i]);
            cell.setCellStyle(columnStyle(wb,createHelper,true,true,true,false,false,false,true));
        }
        
        if(!summaryList.isEmpty()){
        	Set<Integer> keyIdSummary = summaryList.keySet();
			int rowid = sheetSummary.getLastRowNum()+1;
			for (Integer key : keyIdSummary) {
				Row rowSummary = sheetSummary.createRow(rowid++);
				Object[] objectArr = summaryList.get(key);
				int cellSummaryId = 0;
				for (int x=0; x<objectArr.length; x++) {
					Cell cell = rowSummary.createCell(cellSummaryId);
					if(objectArr[x] instanceof Date){
						cell.setCellValue((Date) objectArr[x]);
						if(key == (summaryList.size() - 1))cell.setCellStyle(columnStyle(wb,createHelper,false,true,true,true,false,false,true));
						else cell.setCellStyle(columnStyle(wb,createHelper,false,false,true,true,false,false,true));
					}else if(objectArr[x] instanceof BigDecimal){
						cell.setCellValue(((BigDecimal) objectArr[x]).doubleValue());
						if(key == (summaryList.size() - 1))cell.setCellStyle(columnStyle(wb,createHelper,false,true,false,false,true,false,true));
						else cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,true));
					}else if(objectArr[x] instanceof Double){
						cell.setCellValue(((Double) objectArr[x]).doubleValue());
						if(key == (summaryList.size() - 1))cell.setCellStyle(columnStyle(wb,createHelper,false,true,false,false,true,false,true));
						else cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,true));
					}else if(objectArr[x] instanceof Integer){
						cell.setCellValue(((Integer) objectArr[x]).intValue());
						if(key == (summaryList.size() - 1))cell.setCellStyle(columnStyle(wb,createHelper,false,true,false,false,false,false,true));
						else cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,false,true));
					}else{
						cell.setCellValue((String) objectArr[x]);
						if(key == (summaryList.size() - 1))cell.setCellStyle(columnStyle(wb,createHelper,false,true,true,false,false,false,true));
						else cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,false,true));
					}
					cellSummaryId++;
				}
			}
        }
        
        if(summaryReportList.isEmpty()){
        	Row noRecordRow = sheetSummary.createRow(sheetSummary.getLastRowNum()+1);
			Cell cellNoRecord = noRecordRow.createCell(0);
			cellNoRecord.setCellValue("Rekod tidak dijumpai");
			cellNoRecord.setCellStyle(titleHeaderStyle(wb,false,false,false,false));
			sheetSummary.addMergedRegion(new CellRangeAddress(sheetSummary.getLastRowNum(),sheetSummary.getLastRowNum(),0,columnsSummaryList.length-1)); 
        }
        
        // Resize all columns to fit the content size
        for(int i = 0; i < columnsSummaryList.length; i++) {
        	if(i==0)sheetSummary.setColumnWidth(i, 20*256); //Col : Tarikh Resit/Transaksi
        	else if(i==1)sheetSummary.setColumnWidth(i, 8*256); //Col : Bil
        	else if(i==2)sheetSummary.setColumnWidth(i, 16*256); //Col : Harga (RM)
        	else if(i==3)sheetSummary.setColumnWidth(i, 16*256); //Col : GST (RM)
        	else if(i==4)sheetSummary.setColumnWidth(i, 27*256); //Col : Caj Perkhidmatan (RM)
        	else if(i==5)sheetSummary.setColumnWidth(i, 27*256); //Col : GST Caj Perkhidmatan (RM)
        	else if(i==6)sheetSummary.setColumnWidth(i, 26*256); //Col : Amaun Bersih (RM)
        }
        
        try{
	    	String path = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_SHARED_REPORT_DIR);
	    	String fullPath = path+"/"+requestId+".xlsx";
	    	//String fullPath = "D://reports//dev//"+requestId+".xlsx";
	    	log.info("FULL PATH (CREATE FILE) == "+fullPath);
	    	FileOutputStream out = new FileOutputStream(new File(fullPath));
	    	wb.write(out);
	        out.close();
	        wb.close();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    log.info("DONE GENERATE REPORT COLLECTION REPORT BASED ON RECEIPT AND TRANSACTION DATE !!");
	}
	
	public void getListOfDailyReceiptReport(LinkedHashMap<String, String> params, BigDecimal requestId)throws Exception{		
		Boolean isCollectorGov = isRole("COLLECTOR_GOV");
		params.put("IS_COLLECTOR_GOV",(isCollectorGov) ? "Y" : "N" );
		BigDecimal totalGrossAmt = new BigDecimal(0);
		int countBil = 0;
		Map<Integer, Object[]> fullList = new TreeMap<Integer, Object[]>();
		Map<Integer, Object[]> summaryList = new TreeMap<Integer, Object[]>();
		List<Object[]> reportList = new ArrayList<Object[]>();
		List<Object[]> summaryReportList = new ArrayList<Object[]>();
		String[] sheetName = {"Senarai", "Ringkasan"};
		String[] rowHeaderItems = {"Kod PTJ","Nama PTJ"};
		ArrayList<String> columnHeaderList = new ArrayList<String>(Arrays.asList(
				"Bil","Tarikh","No Resit","No Permohonan","Nama Pembekal", 
				"Kaedah Bayaran","Jenis Terimaan-Kod","Jenis Terimaan-Perihal","Amaun Kasar (RM)"
		));
		
		String titleReport = "LAPORAN RESIT BAGI TEMPOH " 
				+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateFrom()) + " HINGGA "
				+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateTo())
				+ " SEPERTI PADA "+ DateUtils.dateToString("dd/MM/yyyy",new Date())
				+ " BAGI TAHUN KEWANGAN "+ DateUtils.dateToString("yyyy",new Date());

		String[] columnsSummaryList = {"Tarikh Resit","Bil", "Amaun Kasar (RM)"};
		try{
			reportList = generateSMListDailyReceiptReport(params, "L");
			summaryReportList = generateSMListDailyReceiptReport(params, "R");
			if(!reportList.isEmpty()){
				for(int m=0; m<reportList.size(); m++){
					Object[] row = (Object[]) reportList.get(m);
					fullList.put(m, new Object[] {
						(Date) row[3],row[2].toString(),row[5].toString(),
						row[4].toString(),row[1].toString(),row[7].toString(),row[8].toString(),
						(BigDecimal) row[9] != null ? (BigDecimal) row[9] : BigDecimal.ZERO
					});
				}
			}
			
			if(!summaryReportList.isEmpty()){
				for(int n=0; n<summaryReportList.size(); n++){
					Object[] row = (Object[]) summaryReportList.get(n);
					totalGrossAmt = totalGrossAmt.add((BigDecimal) row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO);
					countBil += Integer.parseInt(row[1].toString());
					summaryList.put(n, new Object[]{
						(Date) row[0],
						Integer.parseInt(row[1].toString()),
						(BigDecimal) row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO
					});
				}
			}
			summaryList.put(summaryReportList.size(), new Object[]{"Keseluruhan",countBil,totalGrossAmt});
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		//Only create Sheet Senarai only if search result has return records
		if(!fullList.isEmpty()){
			log.info("SHEET SENARAI IS NOT EMPTY !!!");
			Sheet sheetReportList = wb.createSheet(sheetName[0]); 
			//Setting Report Title
			Row titleRowSheetList= sheetReportList.createRow(0);
			Cell cellTitleRow = titleRowSheetList.createCell(1);
		    cellTitleRow.setCellValue(titleReport);
		    cellTitleRow.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
		    sheetReportList.addMergedRegion(new CellRangeAddress(0,0,1,columnHeaderList.size()-1)); 
		    
		    //Setting Row Items - Kod PTJ/Nama PTJ
		    for(int i = 1; i <= rowHeaderItems.length; i++) {
	        	Row itemsDtlRow = sheetReportList.createRow(i);
	        	Cell cellItemsDtl = itemsDtlRow.createCell(1);
	        	cellItemsDtl.setCellValue(rowHeaderItems[i-1]);
	        	cellItemsDtl.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
	        	
	        	Cell cellItemsVal = itemsDtlRow.createCell(2);
	        	if(i==1)cellItemsVal.setCellValue("24010168 ");        		
	        	if(i==2)cellItemsVal.setCellValue("Unit Perolehan Kementerian Kewangan");
	        	cellItemsVal.setCellStyle(titleHeaderStyle(wb,false,false,false,false));
	        	sheetReportList.addMergedRegion(new CellRangeAddress(i,i,2,3)); 
	        }
		    
		    //Setting Column header list
		    Row headerRowList = sheetReportList.createRow(sheetReportList.getLastRowNum()+1);
	        for(int i = 0; i < columnHeaderList.size(); i++) {
	            Cell cell = headerRowList.createCell(i);
	            cell.setCellValue(columnHeaderList.get(i).toString());
	            cell.setCellStyle(columnStyle(wb,createHelper,true,true,true,false,false,false,false));
	        }
	        
	        Set<Integer> keyIdList = fullList.keySet();
			int rowid = sheetReportList.getLastRowNum()+1;
			int countRowList = 1;
			String receiptNo = "";
			for (Integer key : keyIdList) {
				Row row = sheetReportList.createRow(rowid++);
				Object[] objectArr = fullList.get(key);
				int cellid = 1;
				Cell cellBil = row.createCell(0);
				if(receiptNo == ""){
					cellBil.setCellValue(countRowList++);
				}else if(!receiptNo.equalsIgnoreCase((String)objectArr[1])){
					cellBil.setCellValue(countRowList++);
				}else{
					sheetReportList.addMergedRegion(new CellRangeAddress(rowid-2,rowid-1,0,0)); 
				}
				cellBil.setCellStyle(columnStyle(wb,createHelper,false,false,true,false,false,false,false));
				receiptNo = (String)objectArr[1];
				
				for (int x=0; x<objectArr.length; x++) {
					Cell cell = row.createCell(cellid);
					if(objectArr[x] instanceof Date){
						cell.setCellValue((Date) objectArr[x]);
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,true,true,false,false,false));
					}else if(objectArr[x] instanceof BigDecimal){
						cell.setCellValue(((BigDecimal) objectArr[x]).doubleValue());
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,false));
					}else if(objectArr[x] instanceof Integer){
						cell.setCellValue(((Integer) objectArr[x]).intValue());
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,true,false));
					}else{
						cell.setCellValue((String) objectArr[x]);
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,true,false));
					}
					cellid++;
				}
			}
			
			//Setting Auto filtering in each header column list
	        sheetReportList.setAutoFilter(new CellRangeAddress(headerRowList.getRowNum(),sheetReportList.getLastRowNum(),1,columnHeaderList.size()-1));
	  		// Resize all columns to fit the content size
	        for(int i = 0; i < columnHeaderList.size(); i++) {   
	    		if(i==0)sheetReportList.setColumnWidth(i, 8*256);//Col : Bil
	    		else if(i==1)sheetReportList.setColumnWidth(i, 13*256); //Col : Tarikh
	        	else if(i==2)sheetReportList.setColumnWidth(i, 19*256); //Col : No. Resit
	        	else if(i==3)sheetReportList.setColumnWidth(i, 21*256); //Col : No. Permohonan
	        	else if(i==4)sheetReportList.setColumnWidth(i, 52*256); //Col : Nama Pembekal
	        	else if(i==5)sheetReportList.setColumnWidth(i, 21*256); //Col : Kaedah Bayaran
	        	else if(i==6)sheetReportList.setColumnWidth(i, 24*256); //Col : Jenis Terimaan-Kod
	        	else if(i==7)sheetReportList.setColumnWidth(i, 29*256); //Col : Jenis Terimaan-Perihal
	        	else if(i==8)sheetReportList.setColumnWidth(i, 22*256); //Col : Amaun Kasar
	        }
		}
		
		//SECTION SHEET (Populate) : Ringkasan
		Sheet sheetSummaryList = wb.createSheet(sheetName[1]); 
        //Setting Report Title
		Row titleRowSheetSummary = sheetSummaryList.createRow(0);
		Cell cellTitleRowSummary = titleRowSheetSummary.createCell(0);
		cellTitleRowSummary.setCellValue(titleReport);
		cellTitleRowSummary.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
		sheetSummaryList.addMergedRegion(new CellRangeAddress(0,0,0,columnsSummaryList.length+8)); 
		
		Row subTitleRowSheetSummary = sheetSummaryList.createRow(1);
		Cell cellSubTitleRowSummary = subTitleRowSheetSummary.createCell(0);
		cellSubTitleRowSummary.setCellValue("RINGKASAN STATUS RESIT :");
		cellSubTitleRowSummary.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
		sheetSummaryList.addMergedRegion(new CellRangeAddress(1,1,0,columnsSummaryList.length+8)); 
		
		//Setting Column header list
	    Row headerRowListSummary = sheetSummaryList.createRow(sheetSummaryList.getLastRowNum()+1);
        for(int i = 0; i < columnsSummaryList.length; i++) {
            Cell cell = headerRowListSummary.createCell(i);
            cell.setCellValue(columnsSummaryList[i]);
            cell.setCellStyle(columnStyle(wb,createHelper,true,true,true,false,false,false,false));
        }
        
        if(!summaryList.isEmpty()){
        	log.info("SHEET RINGKASAN IS NOT EMPTY !!!");
			Set<Integer> keyIdSummary = summaryList.keySet();
			int rowid = sheetSummaryList.getLastRowNum()+1;
			for (Integer key : keyIdSummary) {
				Row row = sheetSummaryList.createRow(rowid++);
				Object[] objectArr = summaryList.get(key);
				int cellid = 0;
				for (int x=0; x<objectArr.length; x++) {
					Cell cell = row.createCell(cellid);
					if(objectArr[x] instanceof Date){
						cell.setCellValue((Date) objectArr[x]);
						if(key == summaryList.size()-1)cell.setCellStyle(columnStyle(wb,createHelper,false,true,true,true,false,false,true));
						else cell.setCellStyle(columnStyle(wb,createHelper,false,false,true,true,false,false,true));
					}else if(objectArr[x] instanceof BigDecimal){
						cell.setCellValue(((BigDecimal) objectArr[x]).doubleValue());
						if(key == summaryList.size()-1)cell.setCellStyle(columnStyle(wb,createHelper,false,true,false,false,true,false,true));
						else cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,true));
					}else if(objectArr[x] instanceof Double){
						cell.setCellValue(((Double) objectArr[x]).doubleValue());
						if(key == summaryList.size()-1)cell.setCellStyle(columnStyle(wb,createHelper,false,true,false,false,true,false,true));
						else cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,true));
					}else if(objectArr[x] instanceof Integer){
						cell.setCellValue(((Integer) objectArr[x]).intValue());
						if(key == summaryList.size()-1)cell.setCellStyle(columnStyle(wb,createHelper,false,true,false,false,false,false,true));
						else cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,false,true));
					}else{
						cell.setCellValue((String) objectArr[x]);
						if(key == summaryList.size()-1)cell.setCellStyle(columnStyle(wb,createHelper,false,true,true,false,false,false,true));
						else cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,false,true));
					}
					cellid++;
				}
			}
        }
        if(summaryReportList.isEmpty()){
        	Row noRecordRow = sheetSummaryList.createRow(sheetSummaryList.getLastRowNum()+1);
			Cell cellNoRecord = noRecordRow.createCell(0);
			cellNoRecord.setCellValue("Rekod tidak dijumpai");
			cellNoRecord.setCellStyle(titleHeaderStyle(wb,false,false,false,false));
			sheetSummaryList.addMergedRegion(new CellRangeAddress(sheetSummaryList.getLastRowNum(),sheetSummaryList.getLastRowNum(),0,columnsSummaryList.length-1)); 
        }
        
        // Resize all columns to fit the content size
        for(int i = 0; i < columnsSummaryList.length; i++) {
        	if(i==0)sheetSummaryList.setColumnWidth(i, 16*256); //Col : Tarikh 
        	else if(i==1)sheetSummaryList.setColumnWidth(i, 8*256); //Col : Bil
        	else if(i==2)sheetSummaryList.setColumnWidth(i, 23*256); //Col : Amaun Kasar (RM)
        }
        
        try{
	    	String path = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_SHARED_REPORT_DIR);
	    	String fullPath = path+"/"+requestId+".xlsx";
	    	//String fullPath = "D://reports//dev//"+requestId+".xlsx";
	    	log.info("FULL PATH (CREATE FILE) == "+fullPath);
	    	FileOutputStream out = new FileOutputStream(new File(fullPath));
	    	wb.write(out);
	        out.close();
	        wb.close();
	        
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		        
	    log.info("DONE GENERATE List of Daily Receipt Report !!");
	}
	
	public void getRevenueCollectionReport(LinkedHashMap<String, String> params, BigDecimal requestId)throws Exception{
		String paymentType = "";
		Map<Integer, Object[]> revenueCollectionList = new TreeMap<Integer, Object[]>();
		List<String> payType = new ArrayList<String>();
		if("M".equalsIgnoreCase(params.get(SmReportConst.PARAM_PAYMENT_TYPE1))){//MOF Registration Fee
			payType.add("MF");
			payType.add("RF");
		}
		
		if("P".equalsIgnoreCase(params.get(SmReportConst.PARAM_PAYMENT_TYPE2))){//Processing Fee
			payType.add("PF");
			payType.add("RPF");
		}
		
		if("S".equalsIgnoreCase(params.get(SmReportConst.PARAM_PAYMENT_TYPE3))){//Soft Certificate Fee
			payType.add("SF");
		}
		
		for(int b=0; b<payType.size(); b++){
			if(b != payType.size()-1){
				paymentType += "'"+payType.get(b).toString() +"',";
			}else{
				paymentType += "'"+payType.get(b).toString()+"'";
			}
		}
		params.put("PAYMENTTYPE",paymentType);
		int paymentTypeSize = payType.size();
		String paymentTypeDesc = getPaymentTypeDesc(paymentType);
		
		String titleReport = "LAPORAN TERIMAAN HASIL BAGI TEMPOH " 
				+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateFrom()) + " HINGGA "
				+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateTo())
				+ " SEPERTI PADA "+ DateUtils.dateToString("dd/MM/yyyy",new Date())
				+ " BAGI TAHUN KEWANGAN "+ DateUtils.dateToString("yyyy",new Date());
		
		String[] rowHeaderItems = {"Kod PTJ","Nama PTJ","Jenis Bayaran"};
		ArrayList<String> columnHeaderList = new ArrayList<String>(Arrays.asList(
				"Kod Penjenisan","Perihal","Jan (RM)","Feb (RM)","Mac (RM)","Apr (RM)","Mei (RM)","Jun (RM)",
				"Jul (RM)","Ogos (RM)","Sept (RM)","Okt (RM)","Nov (RM)","Dis (RM)","Jumlah Keseluruhan (RM)"
		));
		
		try{
			List<Object[]> reportList = generateSMRevenueCollectionReport(params);
			if(reportList != null){
				for(int m=0; m<reportList.size(); m++){
					Object[] row = (Object[]) reportList.get(m);
					BigDecimal jan = row[2] != null ? (BigDecimal)row[2] : BigDecimal.ZERO;
					BigDecimal feb = row[3] != null ? (BigDecimal)row[3] : BigDecimal.ZERO;
					BigDecimal mac = row[4] != null ? (BigDecimal)row[4] : BigDecimal.ZERO;
					BigDecimal apr = row[5] != null ? (BigDecimal)row[5] : BigDecimal.ZERO;
					BigDecimal may = row[6] != null ? (BigDecimal)row[6] : BigDecimal.ZERO;
					BigDecimal jun = row[7] != null ? (BigDecimal)row[7] : BigDecimal.ZERO;
					BigDecimal july = row[8] != null ? (BigDecimal)row[8] : BigDecimal.ZERO;
					BigDecimal aug = row[9] != null ? (BigDecimal)row[9] : BigDecimal.ZERO;
					BigDecimal sep = row[10] != null ? (BigDecimal)row[10] : BigDecimal.ZERO;
					BigDecimal oct = row[11] != null ? (BigDecimal)row[11] : BigDecimal.ZERO;
					BigDecimal nov = row[12] != null ? (BigDecimal)row[12] : BigDecimal.ZERO;
					BigDecimal dec = row[13] != null ? (BigDecimal)row[13] : BigDecimal.ZERO;
					BigDecimal totalAll = jan.add(feb).add(mac).add(apr).add(may).add(jun).add(july).add(aug).add(sep).add(oct).add(nov).add(dec);
							
					revenueCollectionList.put(m, new Object[]{
						row[0].toString(),row[1].toString(),
						(BigDecimal)row[2] != null ? (BigDecimal)row[2] : "-",
						(BigDecimal)row[3] != null ? (BigDecimal)row[3] : "-",
						(BigDecimal)row[4] != null ? (BigDecimal)row[4] : "-",
						(BigDecimal)row[5] != null ? (BigDecimal)row[5] : "-",
						(BigDecimal)row[6] != null ? (BigDecimal)row[6] : "-",
						(BigDecimal)row[7] != null ? (BigDecimal)row[7] : "-",
						(BigDecimal)row[8] != null ? (BigDecimal)row[8] : "-",
						(BigDecimal)row[9] != null ? (BigDecimal)row[9] : "-",
						(BigDecimal)row[10] != null ? (BigDecimal)row[10] : "-",
						(BigDecimal)row[11] != null ? (BigDecimal)row[11] : "-",
						(BigDecimal)row[12] != null ? (BigDecimal)row[12] : "-",
						(BigDecimal)row[13] != null ? (BigDecimal)row[13] : "-",
					    totalAll.doubleValue() != 0.00 ? totalAll : "-"
					});
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		Sheet sheetReportList = wb.createSheet("Senarai"); 
		
		//Setting Report Title
		Row titleRowSheetList= sheetReportList.createRow(0);
		Cell cellTitleRow = titleRowSheetList.createCell(0);
	    cellTitleRow.setCellValue(titleReport);
	    cellTitleRow.setCellStyle(titleHeaderStyle(wb,true,false,false,false));
	    sheetReportList.addMergedRegion(new CellRangeAddress(0,0,0,12)); 
	    
	    //Setting Row Items - Kod PTJ/Nama PTJ/Jenis Bayaran
	    for(int i = 1; i <= rowHeaderItems.length; i++) {
        	Row itemsDtlRow = sheetReportList.createRow(i);
        	Cell cellItemsDtl = itemsDtlRow.createCell(0);
        	cellItemsDtl.setCellValue(rowHeaderItems[i-1]);
        	cellItemsDtl.setCellStyle(titleHeaderStyle(wb,true,false,false,true));
        	
        	Cell cellItemsVal = itemsDtlRow.createCell(1);
        	if(i==1)cellItemsVal.setCellValue("24010168 ");        		
        	if(i==2)cellItemsVal.setCellValue("Unit Perolehan Kementerian Kewangan");
        	if(i==3){
        		itemsDtlRow.setHeightInPoints((paymentTypeSize * sheetReportList.getDefaultRowHeightInPoints()));
        		cellItemsVal.setCellValue(paymentTypeDesc);
        	}
        	cellItemsVal.setCellStyle(titleHeaderStyle(wb,false,false,true,true));
        	sheetReportList.addMergedRegion(new CellRangeAddress(i,i,1,3));
        }
	    
	    //Setting Column header list
	    Row headerRowList = sheetReportList.createRow(sheetReportList.getLastRowNum()+1);
        for(int i = 0; i < columnHeaderList.size(); i++) {
            Cell cell = headerRowList.createCell(i);
            cell.setCellValue(columnHeaderList.get(i).toString());
            cell.setCellStyle(columnStyle(wb,createHelper,true,true,true,false,false,false,true));
        }
        
        if(!revenueCollectionList.isEmpty()){
        	log.info("SHEET SENARAI IS NOT EMPTY !!!");
			Set<Integer> keyIdSummary = revenueCollectionList.keySet();
			int rowid = sheetReportList.getLastRowNum()+1;
			for (Integer key : keyIdSummary) {
				Row row = sheetReportList.createRow(rowid++);
				Object[] objectArr = revenueCollectionList.get(key);
				int cellid = 0;
				for (int x=0; x<objectArr.length; x++) {
					Cell cell = row.createCell(cellid);
					if(objectArr[x] instanceof BigDecimal){
						cell.setCellValue(((BigDecimal) objectArr[x]).doubleValue());
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,true));
					}else if(objectArr[x] instanceof Double){
						cell.setCellValue(((Double) objectArr[x]).doubleValue());
						cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,true,false,true));
					}else{
						cell.setCellValue((String) objectArr[x]);
						if(x == 0){
							cell.setCellStyle(columnStyle(wb,createHelper,false,false,true,false,false,true,true));
						}else{
							if("-".equalsIgnoreCase((String) objectArr[x]))cell.setCellStyle(columnStyle(wb,createHelper,false,false,true,false,false,true,true));
							else cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,true,true));
						}
					}
					cellid++;
				}
			}
        }else{
        	Row noRecordRow = sheetReportList.createRow(sheetReportList.getLastRowNum()+1);
			Cell cellNoRecord = noRecordRow.createCell(0);
			cellNoRecord.setCellValue("Rekod tidak dijumpai");
			cellNoRecord.setCellStyle(titleHeaderStyle(wb,false,false,false,false));
			sheetReportList.addMergedRegion(new CellRangeAddress(sheetReportList.getLastRowNum(),sheetReportList.getLastRowNum(),0,columnHeaderList.size()-1)); 
        }
        
        
        //Setting Auto filtering in each header column list
        sheetReportList.setAutoFilter(new CellRangeAddress(headerRowList.getRowNum(),sheetReportList.getLastRowNum(),headerRowList.getFirstCellNum(),columnHeaderList.size()-1));
        
        // Resize all columns to fit the content size
        for(int i = 0; i < columnHeaderList.size(); i++) {   
    		if(i==0)sheetReportList.setColumnWidth(i, 20*256);//Col : Kod Penjenisan
    		else if(i==1)sheetReportList.setColumnWidth(i, 35*256); //Col : Perihal
        	else if(i==2)sheetReportList.setColumnWidth(i, 15*256); //Col : Mothn : Jan (RM)
        	else if(i==3)sheetReportList.setColumnWidth(i, 15*256); //Col : Mothn : Feb (RM)
        	else if(i==4)sheetReportList.setColumnWidth(i, 15*256); //Col : Mothn : Mac (RM)
        	else if(i==5)sheetReportList.setColumnWidth(i, 15*256); //Col : Mothn : April (RM)
        	else if(i==6)sheetReportList.setColumnWidth(i, 15*256); //Col : Mothn : Mei (RM)
        	else if(i==7)sheetReportList.setColumnWidth(i, 15*256); //Col : Mothn : Jun (RM)
        	else if(i==8)sheetReportList.setColumnWidth(i, 15*256); //Col : Mothn : Jul (RM)
        	else if(i==9)sheetReportList.setColumnWidth(i, 15*256); //Col : Mothn : Ogos (RM)
        	else if(i==10)sheetReportList.setColumnWidth(i, 15*256); //Col : Mothn : Sep (RM)
        	else if(i==11)sheetReportList.setColumnWidth(i, 15*256); //Col : Mothn : Okt (RM)
        	else if(i==12)sheetReportList.setColumnWidth(i, 15*256); //Col : Mothn : Nov (RM)
        	else if(i==13)sheetReportList.setColumnWidth(i, 15*256); //Col : Mothn : Dis (RM)
        	else if(i==14)sheetReportList.setColumnWidth(i, 30*256); //Col : Mothn : Jumlah Keseluruhan (RM)
        }
        
        try{
	    	String path = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_SHARED_REPORT_DIR);
	    	String fullPath = path+"/"+requestId+".xlsx";
	    	//String fullPath = "D://reports//dev//"+requestId+".xlsx";
	    	log.info("FULL PATH (CREATE FILE) == "+fullPath);
	    	FileOutputStream out = new FileOutputStream(new File(fullPath));
	    	wb.write(out);
	        out.close();
	        wb.close();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    log.info("DONE GENERATE Revenue Collection Report !!!");
	}
	
	public CellStyle titleHeaderStyle(Workbook wb, Boolean isBold, Boolean isAlignCenter, Boolean isWrapTxt, Boolean isVAlignTop){
		Font titleReportFont = wb.createFont();
		if(isBold)titleReportFont.setBold(true);
		else titleReportFont.setBold(false);
		titleReportFont.setFontHeightInPoints((short)10);  
		titleReportFont.setFontName("Arial"); 
		
		CellStyle titleReportCellStyle = wb.createCellStyle();
		titleReportCellStyle.setFont(titleReportFont);
		if(isAlignCenter)titleReportCellStyle.setAlignment(HorizontalAlignment.CENTER);
		if(isWrapTxt)titleReportCellStyle.setWrapText(true);
		if(isVAlignTop)titleReportCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
		else titleReportCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		
		return titleReportCellStyle;
	}
	
	public CellStyle columnStyle(Workbook wb, 
			CreationHelper createHelper,
			Boolean isCellListHeader,
			Boolean isBold, 
			Boolean isHAlignCenter,
			Boolean isFormatDate,
			Boolean isFormatDecimalNumber,
			Boolean isWrapTxt,
			Boolean isVAlignCenter
			){
		
		Font contentFont = wb.createFont();
		
		contentFont.setFontHeightInPoints((short)10);  
		contentFont.setFontName("Arial"); 
		if(isBold)contentFont.setBold(true);
		else contentFont.setBold(false);
		
		CellStyle cellStyle = wb.createCellStyle();
		
		if(isCellListHeader){
			cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());  
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		if(isHAlignCenter)cellStyle.setAlignment(HorizontalAlignment.CENTER);
		if(isFormatDate)cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
		if(isFormatDecimalNumber)cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
		if(isWrapTxt)cellStyle.setWrapText(true);
		if(isVAlignCenter)cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		else cellStyle.setVerticalAlignment(VerticalAlignment.TOP);

		cellStyle.setFont(contentFont);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		
		return cellStyle;
	}
	
	public String getPaymentTypeDesc(String paymentType) {
		String paymentTypeDesc = "";
		List<String> paymentTypeList = retrievePaymentType(paymentType);
		if(paymentTypeList != null){
			for(int x = 0; x<paymentTypeList.size(); x++){
				if(x != paymentTypeList.size() - 1){
					paymentTypeDesc += paymentTypeList.get(x).toString()+",\n";
				}else{
					paymentTypeDesc += paymentTypeList.get(x).toString()+" ";
				}
			}
		}
		return paymentTypeDesc;
	}
	
	//End - Enhancement #15990
	
	// #28115 - start
	public void getSupplierInfo() {
		PmUser pmUser = new FacesScopes().getUserSessionBean();

		if (pmUser != null) {
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			supplierSearchReqDTO.setUserId(pmUser.getUserId());
			ISupplierSearchSO supplierSearchSO = SmServiceLookup.getSupplierSearchSO();
			SupplierSearchResDTO smResDTO = supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);

			if (smResDTO.getResultBean().getResultList().size() > 0) {
				SmSupplier supplier = (SmSupplier) smResDTO.getResultBean().getResultList().get(0);

				if (supplier != null) {
					getManagedBean().setRegNo(supplier.getRegNo());
					getManagedBean().setCompanyName(supplier.getCompanyName());

					MofAccountReqDTO reqDTO = new MofAccountReqDTO();
					reqDTO.setSupplierId(supplier.getSupplierId());
					SmMofAccount mofAccount = SmServiceLookup.getMofAccountSO().retrieveMOFAccBySupplierId(reqDTO).getMofAccount();

					if (mofAccount != null) {
						getManagedBean().setMofNo(mofAccount.getMofNo());
						getManagedBean().setSupplierType(mofAccount.getSupplierType());
//						getManagedBean().setRegType(ReportConst.RPT_REG_MOF);
						getManagedBean().setDateFrom(mofAccount.getEffDate());
					} else {
//						getManagedBean().setRegType(ReportConst.RPT_REG_BASIC);
					}
				}
			}
		}
	}
	// #28115 - end
	
	
}
