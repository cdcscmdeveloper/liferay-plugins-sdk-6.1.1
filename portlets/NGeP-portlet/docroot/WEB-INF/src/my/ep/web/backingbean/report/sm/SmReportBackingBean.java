package my.ep.web.backingbean.report.sm;

import java.util.ArrayList;
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
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.report.sm.SmReportManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.CategoryRptVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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

	public List<SelectItem> retrieveRegType(boolean showAll, boolean showUpgrade) {
		List<SelectItem> items = new ArrayList<SelectItem>();
		SelectItem item = null;
		if (showAll) {
			item = new SelectItem();
			item.setLabel(SmCommonUtil.getMessage("lbl.rpt.all", null));
			item.setValue(StringUtils.EMPTY);
			items.add(item);
		}
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
	
}
