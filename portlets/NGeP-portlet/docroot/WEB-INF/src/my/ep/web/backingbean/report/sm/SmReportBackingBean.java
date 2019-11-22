package my.ep.web.backingbean.report.sm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.rpt.IRPTRequestSO;
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
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.type.pm.OrganizationType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
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
	
	
	
	//Start- #15990
	//Added By: Haidah
	
	//Only for Laporan Terimaan Berdasarkan Kaedah Bayaran
	public List<Object[]> generateSmCollectionReportBasedPaymentMethod(LinkedHashMap<String, String> params, String type){
		IRPTRequestSO irptRequestSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IRPTRequestSO.class);
		log.info("INSIDE generateSmReportContent() !!!!");
		return irptRequestSO.generateReportSMCollectionBasedPaymentMethod(params,type);
	}
	
	//Retrieve List of Payment Type 
	public List<String> retrievePaymentType(String paramCode){
		IPMReportSO ipmReportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ, IPMReportSO.class);
		return ipmReportSO.retrieveParameterCodeNameByParamCodeParamTypeAndLanguageCode(ReportConst.MALAY_LANGUAGE_CODE, "SPI", paramCode);
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
			case "SM_072" : //Collection Report Based On Payment Method
				try{
					getCollectionReportBasedOnPaymentMethod(params, resDTO.getRequestId());
					getPopUpMsg(String.valueOf(resDTO.getRequestId()));
					RequestContext.getCurrentInstance().execute("rptConfirmationDlg.show()");
				}catch(Exception e){
					e.printStackTrace();
				}
				
			break;
		}
	}
	
	public void getCollectionReportBasedOnPaymentMethod(LinkedHashMap<String, String> params, BigDecimal requestId) throws FileNotFoundException, IOException{
		log.info("INSIDE generateCollectionReportBasedOnPaymentMethod (REQUEST_ID) === >>>> "+requestId);
		
		List<String> payType = new ArrayList<String>();
		if("M".equalsIgnoreCase(params.get(SmReportConst.PARAM_PAYMENT_TYPE1))){//MOF Registration Fee
			payType.add("MF");
			payType.add("RF");
		}
		
		if("P".equalsIgnoreCase(params.get(SmReportConst.PARAM_PAYMENT_TYPE2))){//Processing Fee
			payType.add("PF");
			payType.add("RPF");
		}
		
		String paymentType = "";
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
		
		List<String> paymentTypeList = retrievePaymentType(paymentType);
		String paymentTypeDesc = "";
		if(paymentTypeList != null){
			for(int x = 0; x<paymentTypeList.size(); x++){
				if(x != paymentTypeList.size() - 1){
					paymentTypeDesc += paymentTypeList.get(x).toString()+",\n";
				}else{
					paymentTypeDesc += paymentTypeList.get(x).toString()+" ";
				}
			}
		}
		
		String[] reportTitleList = {"Laporan Terimaan Berdasarkan Kaedah Bayaran Bagi Tempoh","Ringkasan Laporan Terimaan Berdasarkan Kaedah Bayaran Bagi Tempoh"};
		String[] sheetFileName = {"Senarai", "Ringkasan"};
		String[] rowItems = {"Kod PTJ","Nama PTJ","Kaedah Bayaran","Nama Pembekal","Jenis Bayaran"};
		String name = "";
		String paymentMethod = "";
		
		String[] columnsLaporan = {
				"Bil", 
				"Tarikh", 
				"No Resit", 
				"Nama Pembekal",
				"No Permohonan", 
				"Jenis Terimaan-Kod",  
				"Jenis Terimaan-Perihal",
				"Harga (RM)",
				"GST (RM)",
				"Caj Perkhidmatan",
				"GST Caj Perkhidmatan",
				"Amaun Bersih (RM)",
				"Jumlah Keseluruhan (RM)",
				"Status"
		};
		
		String[] columnsRingkasan = {
				"Tarikh Resit",
				"  Bil  ", 
				"Harga (RM)",
				"GST (RM)",
				"Caj Perkhidmatan (RM)",
				"GST Caj Perkhidmatan (RM)",
				"Amaun Bersih (RM)"
		};
		
		if("Credit".equalsIgnoreCase(params.get(SmReportConst.PARAM_PAYMENT_METHOD))){
			name = "CC";
			paymentMethod = "Kad Kredit";
			
		}else{
			name = "FPX";
			paymentMethod = "FPX";
		}
				
		//START HERE POPULATE REPORT ------ 
		Workbook wb = new HSSFWorkbook(); 
		// Creating Sheets using sheet object 
		Sheet sheetLaporan = wb.createSheet(sheetFileName[0]+" "+name); 
		Sheet sheetRingkasan = wb.createSheet(sheetFileName[1]+" "+name); 
        
        Font titleReportFont = wb.createFont();
		titleReportFont.setBold(true);
		
		CellStyle titleReportCellStyle = wb.createCellStyle();
		titleReportCellStyle.setFont(titleReportFont);
		
		CellStyle headerCellStyle = wb.createCellStyle();
		headerCellStyle.setFont(titleReportFont);
		headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());  
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		
		CellStyle itemListStyle = wb.createCellStyle();
		itemListStyle.setBorderTop(BorderStyle.THIN);
		itemListStyle.setBorderBottom(BorderStyle.THIN);
		itemListStyle.setBorderLeft(BorderStyle.THIN);
		itemListStyle.setBorderRight(BorderStyle.THIN);
		
		CellStyle itemListStyle_2 = wb.createCellStyle();
		itemListStyle_2.setBorderTop(BorderStyle.THIN);
		itemListStyle_2.setBorderBottom(BorderStyle.THIN);
		itemListStyle_2.setBorderLeft(BorderStyle.THIN);
		itemListStyle_2.setBorderRight(BorderStyle.THIN);
		itemListStyle_2.setAlignment(HorizontalAlignment.CENTER);
		
		CellStyle wrapTextStyle = wb.createCellStyle();
		wrapTextStyle.setWrapText(true);
		wrapTextStyle.setVerticalAlignment(VerticalAlignment.TOP);
		
		
		CellStyle currencyStyle = wb.createCellStyle();
		currencyStyle.setBorderTop(BorderStyle.THIN);
		currencyStyle.setBorderBottom(BorderStyle.THIN);
		currencyStyle.setBorderLeft(BorderStyle.THIN);
		currencyStyle.setBorderRight(BorderStyle.THIN);
		currencyStyle.setAlignment(HorizontalAlignment.RIGHT);
		DataFormat datafrmt = wb.createDataFormat();
		currencyStyle.setDataFormat(datafrmt.getFormat("###,###,##0.00")); 
		
        
        Row titleRow = sheetLaporan.createRow(0);
       
        Cell cellTitleRow = titleRow.createCell(1);
        cellTitleRow.setCellValue(reportTitleList[0] + " "
        		+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateFrom()) 
        		+ " hingga "
        		+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateTo())
        		+" seperti pada "
        		+ DateUtils.dateToString("dd/MM/yyyy",new Date())
        		+" bagi tahun kewangan "
        		+ DateUtils.dateToString("yyyy",new Date())
        );
        cellTitleRow.setCellStyle(titleReportCellStyle);
	    
        sheetLaporan.addMergedRegion(new CellRangeAddress(0,0,1,columnsLaporan.length)); 
        
        //Sheet 1:
        for(int i = 1; i <= rowItems.length; i++) {
        	Row itemsDtlRow = sheetLaporan.createRow(i);
        	Cell cellItemsDtl = itemsDtlRow.createCell(1);
        	cellItemsDtl.setCellValue(rowItems[i-1]);
        	if(i==5){
        		cellItemsDtl.setCellStyle(wrapTextStyle);
        	}
        	
        	Cell cellItemsVal = itemsDtlRow.createCell(2);
        	if(i==1){
        		cellItemsVal.setCellValue("24010168");        		
        	}
        	
        	if(i==2){
        		cellItemsVal.setCellValue("Unit Perolehan Kementerian Kewangan");
        	}
        	
        	if(i==3){
        		cellItemsVal.setCellValue(paymentMethod);
        	}
        	
        	if(i==4){
        		if(StringUtils.isNotBlank(params.get(SmReportConst.PARAM_COMPANY_NAME))){
        			cellItemsVal.setCellValue(params.get(SmReportConst.PARAM_COMPANY_NAME));
        		}else{
        			cellItemsVal.setCellValue("SEMUA");
        		}
        	}
        	
        	if(i==5){
        		//sheetLaporan.addMergedRegion(new CellRangeAddress(itemsDtlRow.getRowNum(),itemsDtlRow.getRowNum(),2,3));
        		cellItemsVal.setCellValue(paymentTypeDesc);
        		cellItemsVal.setCellStyle(wrapTextStyle);
        	}
        }
        
        //Row Senarai Laporan Terimaan
        Row headerRowListLaporan = sheetLaporan.createRow(sheetLaporan.getLastRowNum()+2);
        
        for(int i = 0; i < columnsLaporan.length; i++) {
            Cell cell = headerRowListLaporan.createCell(i);
            cell.setCellValue(columnsLaporan[i]);
            cell.setCellStyle(headerCellStyle);
        }
        
        try{
        	//Query-Paparan Laporan Terimaan Berdasarkan Kaedah Bayaran
			List<Object[]> reportList = generateSmCollectionReportBasedPaymentMethod(params,"L");
			log.info("SIZE LIST === >>> "+reportList.size());
			if(reportList != null){
				int rowCount = 1;
				for(int i = 0; i<reportList.size(); i++){
					Row itemLaporanTerimaan = sheetLaporan.createRow(sheetLaporan.getLastRowNum()+1);
					Object[] row = (Object[]) reportList.get(i);
					String applNo = "";
					String kod = "";
					String perihal = "";
					int rowCellCount = 0;					
					for(int a=0; a<row.length; a++){
						double amount = row[7] != null ? Double.parseDouble(String.valueOf(row[7])) : 0.00;
						double gstAmount = row[8] != null ? Double.parseDouble(String.valueOf(row[8])) : 0.00 ;
						double cajPerkhidmatan = row[9] != null ? Double.parseDouble(String.valueOf(row[9])) : 0.00;
						double gstRate = row[10] != null ? Double.parseDouble(String.valueOf(row[10])) : 0.00;
						double gstServiceChargeAmt = cajPerkhidmatan * gstRate;
						double netPrice = amount + gstAmount - cajPerkhidmatan - gstServiceChargeAmt;
						Cell cell;
						if(a==0){
							cell = itemLaporanTerimaan.createCell(rowCellCount);
				            cell.setCellValue(rowCount);
				            cell.setCellStyle(itemListStyle);
				            rowCellCount++;
						}else if(a==1){ //Column : Tarikh
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(String.valueOf(row[1]));
							cell.setCellStyle(itemListStyle_2);
				            rowCellCount++;
						}else if(a==2) { // Column : No Resit
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(String.valueOf(row[2]));
							cell.setCellStyle(itemListStyle);
				            rowCellCount++;
						}else if(a==3){ // Column : Nama Pembekal
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue( String.valueOf(row[3]));
							cell.setCellStyle(itemListStyle);
				            rowCellCount++;
						}else if(a==4){ // Column : No Permohonan 
							applNo = String.valueOf(row[4]);
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(StringUtils.isNotEmpty(applNo) ? applNo : "isemptyla");
							cell.setCellStyle(itemListStyle);
				            rowCellCount++;
						}else if(a==5){// Column : Kod
							kod = String.valueOf(row[5]);
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(kod != null ? kod : "");
							cell.setCellStyle(itemListStyle);
				            rowCellCount++;
						}else if(a==6){// Column : Perihal
							perihal = String.valueOf(row[6]);							
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(StringUtils.isNotBlank(perihal) ? perihal : "");
							cell.setCellStyle(itemListStyle);
				            rowCellCount++;
						}else if(a==7){//Column : Harga (RM)
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(amount);
							cell.setCellStyle(currencyStyle);
				            rowCellCount++;
						}else if(a==8){//Column : GST (RM)
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(gstAmount);
							cell.setCellStyle(currencyStyle);
				            rowCellCount++;
						}else if(a==9){//Column : Caj Perkhidmatan
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(cajPerkhidmatan);
							cell.setCellStyle(currencyStyle);
				            rowCellCount++;
						}else if(a==10){//Column : GST Caj Perkhidmatan
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(gstServiceChargeAmt);
							cell.setCellStyle(currencyStyle);
				            rowCellCount++;
						}else if(a==11){//Column : Amaun Bersih (RM)
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(netPrice);
							cell.setCellStyle(currencyStyle);
				            rowCellCount++;
						}else if(a==12){//Column : Jumlah Keseluruhan (RM)
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(netPrice);
							cell.setCellStyle(currencyStyle);
				            rowCellCount++;
						}else if(a==13) {//Status
							cell = itemLaporanTerimaan.createCell(rowCellCount);
							cell.setCellValue(String.valueOf(row[13]));
							cell.setCellStyle(itemListStyle);
						}						
					}
					rowCount++;
				}
			}
			
		}catch(Exception e){
			log.info("ADA ERROR LAA SHEET LAPORAN !!!! ");
			e.printStackTrace();
		}
        
        sheetLaporan.setAutoFilter(new CellRangeAddress(headerRowListLaporan.getRowNum(),sheetLaporan.getLastRowNum(),headerRowListLaporan.getFirstCellNum(),columnsLaporan.length-1));
        
        // Resize all columns to fit the content size
        for(int i = 0; i < columnsLaporan.length; i++) {
        	sheetLaporan.autoSizeColumn(i);
        }
        
        //Second Sheet
        try{
        	//Query-Paparan Ringkasan Terimaan Berdasarkan Kaedah Bayaran
			List<Object[]> reportRingkasanList = generateSmCollectionReportBasedPaymentMethod(params,"R");
			log.info("SIZE REPORT RINGKASAN TERIMAAN LIST === >>> "+reportRingkasanList.size());
			
			Row titleRowRingkasan = sheetRingkasan.createRow(0);
	        Cell cellTitleRingkasan = titleRowRingkasan.createCell(0);
	        cellTitleRingkasan.setCellValue(reportTitleList[1]+ " "
	        		+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateFrom()) 
	        		+ " hingga "
	        		+ DateUtils.dateToString("dd/MM/yyyy",getManagedBean().getDateTo())
	        		+" seperti pada "
	        		+ DateUtils.dateToString("dd/MM/yyyy",new Date())
	        		+" bagi tahun kewangan "
	        		+ DateUtils.dateToString("yyyy",new Date()));
	        cellTitleRingkasan.setCellStyle(titleReportCellStyle);
	        
	        sheetRingkasan.addMergedRegion(new CellRangeAddress(0,0,0,columnsLaporan.length-1)); 
	        
	        Row headerRowRingkasan = sheetRingkasan.createRow(2);
	        // Create cells
	        for(int i = 0; i < columnsRingkasan.length; i++) {
	            Cell cell = headerRowRingkasan.createCell(i);
	            cell.setCellValue(columnsRingkasan[i]);
	            cell.setCellStyle(headerCellStyle);
	        }
	        
	        int totalCount = 0;
	        double totalAmt = 0.00;
        	double totalGstAmt = 0.00;
        	double totalServiceCaj = 0.00;
        	double totalNetAmt = 0.00;
        	
	        if(reportRingkasanList != null){
	        	for(int m=0; m<reportRingkasanList.size(); m++){
	        		Row itemRingkasanTerimaan = sheetRingkasan.createRow(sheetRingkasan.getLastRowNum()+1);
					Object[] rowRingkasan = (Object[]) reportRingkasanList.get(m);
					int rowCellRingkasanCount = 0;
					for(int x=0; x<rowRingkasan.length+1; x++){
						double amt = rowRingkasan[2] != null ? Double.parseDouble(String.valueOf(rowRingkasan[2])) : 0.00;
						double gstAmt = rowRingkasan[3] != null ? Double.parseDouble(String.valueOf(rowRingkasan[3])) : 0.00;
						double serviceCaj = rowRingkasan[4] != null ? Double.parseDouble(String.valueOf(rowRingkasan[4])) : 0.00;
						double netAmt = amt + gstAmt - serviceCaj;
						Cell cellRingkasan;
						if(x==0){
							cellRingkasan = itemRingkasanTerimaan.createCell(rowCellRingkasanCount);
							cellRingkasan.setCellValue(String.valueOf(rowRingkasan[x]));
							cellRingkasan.setCellStyle(itemListStyle_2);
							rowCellRingkasanCount++;
						}else if(x==1){
							cellRingkasan = itemRingkasanTerimaan.createCell(rowCellRingkasanCount);
							cellRingkasan.setCellValue(Integer.parseInt(String.valueOf(rowRingkasan[x])));
							cellRingkasan.setCellStyle(itemListStyle);
							rowCellRingkasanCount++;
							totalCount += Integer.parseInt(String.valueOf(rowRingkasan[x]));
						}else if(x==2){
							cellRingkasan = itemRingkasanTerimaan.createCell(rowCellRingkasanCount);
							cellRingkasan.setCellValue(amt);
							cellRingkasan.setCellStyle(currencyStyle);
							rowCellRingkasanCount++;
							totalAmt += amt;
						}else if(x==3){
							cellRingkasan = itemRingkasanTerimaan.createCell(rowCellRingkasanCount);
							cellRingkasan.setCellValue(gstAmt);
							cellRingkasan.setCellStyle(currencyStyle);
							rowCellRingkasanCount++;
							totalGstAmt += gstAmt;
						}else if(x==4){
							cellRingkasan = itemRingkasanTerimaan.createCell(rowCellRingkasanCount);
							cellRingkasan.setCellValue(serviceCaj);
							cellRingkasan.setCellStyle(currencyStyle);
							rowCellRingkasanCount++;
							totalServiceCaj += serviceCaj;
						}else if(x==5){
							cellRingkasan = itemRingkasanTerimaan.createCell(rowCellRingkasanCount);
							cellRingkasan.setCellValue(0.00);
							cellRingkasan.setCellStyle(currencyStyle);
							rowCellRingkasanCount++;
						}else if(x==6){
							cellRingkasan = itemRingkasanTerimaan.createCell(rowCellRingkasanCount);
							cellRingkasan.setCellValue(netAmt);
							cellRingkasan.setCellStyle(currencyStyle);
							rowCellRingkasanCount++;
							totalNetAmt += netAmt;
						}
					}
					
	        	}
	        }
	        
	        //Row : Display Sum of each amount column (Harga/GST/Caj Perkhidmatan/GST Caj Perkhidmatan/Amaun Bersih)
	        Row rowSum = sheetRingkasan.createRow(sheetRingkasan.getLastRowNum()+1);
	        for(int x=0; x<columnsRingkasan.length; x++){
	        	Cell cellSumRingkasan = rowSum.createCell(x);;
	        	if(x==0){
	        		cellSumRingkasan.setCellValue("Keseluruhan");
	        		cellSumRingkasan.setCellStyle(itemListStyle);
	        	}else if(x==1){
	        		cellSumRingkasan.setCellValue(totalCount);
	        		cellSumRingkasan.setCellStyle(itemListStyle);
	        	}else if(x==2){
	        		cellSumRingkasan.setCellValue(totalAmt);
	        		cellSumRingkasan.setCellStyle(currencyStyle);
	        	}else if(x==3){
	        		cellSumRingkasan.setCellValue(totalGstAmt);
	        		cellSumRingkasan.setCellStyle(currencyStyle);
	        	}else if(x==4){
	        		cellSumRingkasan.setCellValue(totalServiceCaj);
	        		cellSumRingkasan.setCellStyle(currencyStyle);
	        	}else if(x==5){
	        		cellSumRingkasan.setCellValue(0.00);
	        		cellSumRingkasan.setCellStyle(currencyStyle);
	        	}else if(x==6){
	        		cellSumRingkasan.setCellValue(totalNetAmt);
	        		cellSumRingkasan.setCellStyle(currencyStyle);
	        	}
	        }
	        
	        // Resize all columns to fit the content size
	        for(int i = 0; i < columnsRingkasan.length; i++) {
	        	sheetRingkasan.autoSizeColumn(i);
	        }
	        			
        }catch(Exception e){
        	log.info("ADA ERROR LAA SHEET RINGKASAN !!!! ");
			e.printStackTrace();
        }
        
        
        try{
        	String path = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_SHARED_REPORT_DIR);
        	String fullPath = path+"/"+requestId+".xls";
        	//String fullPath = "D://reports//dev//"+requestId+".xls";
        	log.info("FULL PATH (CREATE FILE) == "+fullPath);
        	FileOutputStream out = new FileOutputStream(new File(fullPath));
        	wb.write(out);
            out.close();
            
        }catch(Exception e){
        	e.printStackTrace();
        }
		
	}
	//End - Enhancement #15990
	
	
}
