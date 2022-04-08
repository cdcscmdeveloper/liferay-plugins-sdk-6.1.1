/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */

package my.ep.web.backingbean.report.qt;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.dto.rpt.ReportTempInfoReqDTO;
import my.ep.biz.dto.rpt.ReportTempInfoResDTO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.rpt.IRPTRequestDtlSO;
import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.biz.service.rpt.IReportTempInfoSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.report.RPTRequestDtlReqDTO;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.rpt.ReportTempInfo;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
import my.ep.web.bean.report.ScReportFieldsSearchBean;
import my.ep.web.constant.report.QtReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.qt.QtReportManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class QtReportBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */
public abstract class QtReportBackingBean<T extends QtReportManagedBean>
		extends ReportBaseBackingBean<T> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(QtReportBackingBean.class);
	private static final String CARET_SYMBOL = "^";
	private static final String PIPE_SYMBOL = "|";
 
	private String strLanguage = ResourceBundleUtils.getMessage(
			ReportConst.LABEL_LANG, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
			new FacesScopes().getUserLocale());

	/**
	 * retrive reports dropdown data from database.
	 */
	public void reportOnChange() {
		if (!StringUtils.isBlank(getManagedBean().getReportCode())) {
			retrieveReportValueFromDb();
			this.getManagedBean().setPtjEnable(Boolean.FALSE);
			this.getManagedBean().setDeskOfficerEnable(Boolean.FALSE);
		}
	}

	/**
	 * quotationNumberValidation - Quotation number validation.
	 */
	public void quotationNumberValidation() {
		log.info("ReportBackingBean-quotationNumberValidation"
				+ this.getManagedBean().getQuotationNumber());
		if (StringUtils.isNotEmpty(this.getManagedBean().getQuotationNumber())) {
			specialCharValidation();
		} else {
			resetSearchBB();
		}
	}

	/**
	 * specialCharValidation - Special char validation.
	 */
	public void specialCharValidation() {
		log.info("ReportBackingBean-specialCharValidation"
				+ this.getManagedBean().getQuotationNumber());
		String ptString = ReportConst.NUMERIC_CHAR_PATTERN_ONLY;
		Pattern pattern = Pattern.compile(ptString);
		Matcher match;

		match = pattern.matcher(this.getManagedBean().getQuotationNumber());

		if (match.matches()) {

			getQuotationNumber();

		} else {
			resetSearchBB();
			throw new EpException(MessageConst.ERROR_REPORT_780006);
		}
	}

	/**
	 * getQuotationNumber - Gets the quotation number.
	 * 
	 * @return the quotationNumber
	 */
	public void getQuotationNumber() {
		log.info("ReportBackingBean-getQuotationNumber"
				+ this.getManagedBean().getQuotationNumber());
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		// log.info("QtNo="+this.getScQuotationNo());
		if (StringUtils.isNotEmpty(this.getManagedBean().getQuotationNumber())) {
			tenderReqDTO.setQtTndrNo(this.getManagedBean().getQuotationNumber()
					.toUpperCase());
			IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);
			QuationTenderResDTO resDTO = detailSO
					.retriveQtByQtNumber(tenderReqDTO);
			ScQt scqt = resDTO.getQtDetail();

			retrieveAgencyByQuotationNumber(scqt);

			if (scqt != null) {
				if (scqt.getQtId() != null) {
					// log.info("QtId="+scqt.getQtId());
					this.getManagedBean().setQuotationId(scqt.getQtId().toString());
				} else {
					resetSearchBB();
					throw new EpException(MessageConst.ERROR_REPORT_780005);
				}
			} else {
				resetSearchBB();
				throw new EpException(MessageConst.ERROR_REPORT_780005);
			}
		}
	}

	/**
	 * generateSCReport - Generate SOURCING(SUPPLIER SELECTION and DIRECT
	 * PURCHASE) module reports.
	 */

	public void generateSCReport(LinkedHashMap<String, String> params) {
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IRPTRequestSO.class);
		log.info("generate reportCode" + getManagedBean().getReportCode());
		log.info("generate reportName" + getManagedBean().getReportName());
		params.put(QtReportConst.PARAM_KEY_REPORT, getManagedBean()
				.getReportName());
		params.put(QtReportConst.PARAM_REPORT_CODE, getManagedBean()
				.getReportCode());
		params.put(QtReportConst.PARAM_USER_ID, getFacesScopes()
				.getUserSessionBean().getUserId().toString());

		//Bug #28440 start
		String userRoleLogin = getUserRoleLogin();
		if(userRoleLogin != null){
			params.put(QtReportConst.PARAM_LOGIN, userRoleLogin);
		}
		//Bug #28440 end
		
		RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
		reqDTO.setDefaultOutputType(this.getManagedBean()
				.getDefaultOutputType());
		reqDTO.setTempAccess(this.getManagedBean().isTempAccess());
		log.info("QtId" + this.getManagedBean().getQuotationId());
		reqDTO.setParametersMap(params);
		try {

			RPTRequestResDTO resDTO = rptRequestSO.generateReport(reqDTO);
	
			//R5 #14076 start		
			RPTRequestDtlReqDTO restDtlDTO = new RPTRequestDtlReqDTO();
			restDtlDTO.getRptRequestDtl().setRequestId(resDTO.getRequestId());
			
			if(params.get(QtReportConst.QUOTATION_TENDER_ID) != null){
				if(getManagedBean().getQuotationNumber()!= null){
					restDtlDTO.getRptRequestDtl().setDocumentNo01(getManagedBean().getQuotationNumber());
				}		
			}	
			if(restDtlDTO.getRptRequestDtl().getDocumentNo01() != null ){
				addRptRequestDetail(restDtlDTO);		
			}
			//R5 #14076 end
			
			getPopUpMsg(String.valueOf(resDTO.getRequestId()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//R5 #14076 start
	public void addRptRequestDetail(RPTRequestDtlReqDTO restDtlDTO){
		
		IRPTRequestDtlSO rptRequestDtlSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,IRPTRequestDtlSO.class);
		if(restDtlDTO.getRptRequestDtl().getCreatedBy() == null){
			restDtlDTO.getRptRequestDtl().setCreatedBy(getFacesScopes()
				.getUserSessionBean().getUserId());
		}
		rptRequestDtlSO.saveRPTRequestDtlDTO(restDtlDTO);
	}
	
	//Bug #28440 start
	public String getUserRoleLogin(){
		
		String userRoleLogin = ReportConst.EMPTY_VAL;
		
		List<SelectItem> approverNayakTarafList=retrieveApprovarNaikTarafSCS2();
		List<String> authoriztionList = retriveUserrloeByUserId();
		
		if (approverNayakTarafList.size() > 0) {
			userRoleLogin = ReportConst.APPROVERNT;
		} else if (authoriztionList.contains(ReportConst.BUDGET_APPROVER)
				|| authoriztionList.contains(ReportConst.BPK_APPROVER)
				|| authoriztionList.contains(ReportConst.COMMITTEE_APPROVER) 
				|| authoriztionList .contains(ReportConst.PUBLICATION_APPROVER)) {
			userRoleLogin = ReportConst.APPROVER;
		} else if (authoriztionList.contains(ReportConst.DESK_OFFICER)) {
			userRoleLogin = ReportConst.DESKOFFR;
		} 
		
		return userRoleLogin;
	}	
	//Bug #28440 end

	public FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public BigDecimal getEstimatedAmtByQtID(String qtId) {

		BigDecimal quotationId = new BigDecimal(qtId != null ? qtId.replaceAll(
				",", "") : null);
		BigDecimal estimatedAmount = decryptionByQtId(quotationId, this
				.getManagedBean().getReportCode());
		return estimatedAmount;

	}

	private BigDecimal decryptionByQtId(BigDecimal qtId, String reportCode) {
		log.info("decryptionByQtId");
		IQTDetailSO QtDetailsSO;

		try {
			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();

			tenderReqDTO.setQtId(qtId.longValue());
			BigDecimal estimatedAmount = QtDetailsSO
					.getdecryptedEstAmount(tenderReqDTO.getQtId());

			return estimatedAmount;

		} catch (Exception e) {
			throw (e);
		} finally {
			QtDetailsSO = null;
		}

	}

	private ReportTempInfoResDTO toSaveDecryptedAmountWithQNoAndQId(
			BigDecimal qtId, BigDecimal estimatedAmount) {
		IReportTempInfoSO reportTempSO;
		reportTempSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ReportConst.RPT_MODULE_SER_OBJ, IReportTempInfoSO.class);

		ReportTempInfo rptTmp = new ReportTempInfo();
		rptTmp.setReportCode(this.getManagedBean().getReportCode());
		rptTmp.setQtId(qtId.toString());
		rptTmp.setEstimatedAmt(estimatedAmount != null ? estimatedAmount
				.toString() : null);

		ReportTempInfoReqDTO reqDTO = new ReportTempInfoReqDTO();
		reqDTO.setReportTempInfo(rptTmp);

		ReportTempInfoResDTO resDTO = reportTempSO
				.saveDecryptedAmountWithQuotationNoAndQuotationId(reqDTO);
		reportTempSO = null;
		return resDTO;
	}

	public abstract void generateReport(LinkedHashMap<String, String> params);

	public abstract void resetSearch();

	public abstract LinkedHashMap<String, String> getparams();

	/**
	 * Generate month list.
	 * 
	 * @param language
	 *            the language
	 * @return the list
	 */
	public List<SelectItem> generateMonthList() {
		List<SelectItem> monthList = new ArrayList<SelectItem>();
		if ((strLanguage != null) && (strLanguage.equalsIgnoreCase("en"))) {

			String month = ReportConst.EMPTY_VAL;
			int monthNoInt;
			String monthNoStr = ReportConst.EMPTY_VAL;
			DateFormatSymbols dfs = new DateFormatSymbols();
			String[] months = dfs.getMonths();
			for (int i = 0; i <= 11; i++) {
				monthNoInt = i + 1;
				if (monthNoInt < 10) {
					monthNoStr = ReportConst.ZERO_VAL + monthNoInt;
				} else {
					monthNoStr = monthNoInt + ReportConst.EMPTY_VAL;
				}
				month = months[i];
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(month);
				selectItem.setValue(monthNoStr);
				monthList.add(selectItem);
			}
		} else if ((strLanguage != null)
				&& (strLanguage.equalsIgnoreCase("ms"))) {
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel("Januari");
			selectItem.setValue("01");
			monthList.add(selectItem);
			SelectItem selectItem1 = new SelectItem();
			selectItem1.setLabel("Februari");
			selectItem1.setValue("02");
			monthList.add(selectItem1);
			SelectItem selectItem2 = new SelectItem();
			selectItem2.setLabel("Mac");
			selectItem2.setValue("03");
			monthList.add(selectItem2);
			SelectItem selectItem3 = new SelectItem();
			selectItem3.setLabel("April");
			selectItem3.setValue("04");
			monthList.add(selectItem3);
			SelectItem selectItem4 = new SelectItem();
			selectItem4.setLabel("Mei");
			selectItem4.setValue("05");
			monthList.add(selectItem4);
			SelectItem selectItem5 = new SelectItem();
			selectItem5.setLabel("Jun");
			selectItem5.setValue("06");
			monthList.add(selectItem5);
			SelectItem selectItem6 = new SelectItem();
			selectItem6.setLabel("Julai");
			selectItem6.setValue("07");
			monthList.add(selectItem6);
			SelectItem selectItem7 = new SelectItem();
			selectItem7.setLabel("Ogos");
			selectItem7.setValue("08");
			monthList.add(selectItem7);
			SelectItem selectItem8 = new SelectItem();
			selectItem8.setLabel("September");
			selectItem8.setValue("09");
			monthList.add(selectItem8);
			SelectItem selectItem9 = new SelectItem();
			selectItem9.setLabel("Oktober");
			selectItem9.setValue("10");
			monthList.add(selectItem9);
			SelectItem selectItem10 = new SelectItem();
			selectItem10.setLabel("November");
			selectItem10.setValue("11");
			monthList.add(selectItem10);
			SelectItem selectItem11 = new SelectItem();
			selectItem11.setLabel("Disember");
			selectItem11.setValue("12");
			monthList.add(selectItem11);

		}
		return monthList;
	}

	/**
	 * Generate year list.
	 * 
	 * @return the list
	 */
	public List<SelectItem> generateYearList() {

		int year = Calendar.getInstance().get(Calendar.YEAR);

		List<SelectItem> yearList = new ArrayList<SelectItem>();

		for (int i = year - 7; i <= year; i++) {

			yearList.add(new SelectItem(i, String.valueOf(i)));

		}
		return yearList;
	}

	public String getStatusDesc(String label) {
		return ResourceBundleUtils.getMessage(label,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
	}
	
	public void	onSelectEffectedValues(ScReportFieldsSearchBean reportFieldBean){
		ScReportFieldsSearchBean reportFiledsBean=	onSelectEffectedCommonValues(reportFieldBean);
		this.getManagedBean().setScReportSearchBean(reportFiledsBean);
		
		
	}

	/**
	 * onSelectEffectedValues - On select effected values.S
	 */
	private ScReportFieldsSearchBean onSelectEffectedCommonValues(
			ScReportFieldsSearchBean reportFileds) {
		log.info("ReportBackingBean-onSelectEffectedValues");
		String checkBoxvalue[] = null;
		if (null != reportFileds.getAllCheckboxValue()
				&& reportFileds.getAllCheckboxValue().length > 0) {
			reportFileds.setQuotationCheckboxEditView(Boolean.TRUE);
			reportFileds.setTenderCheckboxEditView(Boolean.TRUE);
			reportFileds.setRestrictedTndrCheckboxEditView(Boolean.TRUE);
			reportFileds.setQuotationCheckboxValue(checkBoxvalue);
			reportFileds.setTenderCheckboxValue(checkBoxvalue);
			reportFileds.setRestrictedTndrCheckboxValue(checkBoxvalue);
		} else if ((null != reportFileds.getQuotationCheckboxValue() && reportFileds
				.getQuotationCheckboxValue().length > 0)
				&& (null != reportFileds.getTenderCheckboxValue() && reportFileds
						.getTenderCheckboxValue().length > 0)) {
			reportFileds.setAllCheckboxEditView(Boolean.TRUE);
			reportFileds.setRestrictedTndrCheckboxEditView(Boolean.TRUE);

		} else if ((null != reportFileds.getRestrictedTndrCheckboxValue() && reportFileds
				.getRestrictedTndrCheckboxValue().length > 0)
				&& (null != reportFileds.getTenderCheckboxValue() && reportFileds
						.getTenderCheckboxValue().length > 0)) {
			reportFileds.setAllCheckboxEditView(Boolean.TRUE);
			reportFileds.setQuotationCheckboxEditView(Boolean.TRUE);

		} else if ((null != reportFileds.getRestrictedTndrCheckboxValue() && reportFileds
				.getRestrictedTndrCheckboxValue().length > 0)
				&& (null != reportFileds.getQuotationCheckboxValue() && reportFileds
						.getQuotationCheckboxValue().length > 0)) {
			reportFileds.setAllCheckboxEditView(Boolean.TRUE);
			reportFileds.setTenderCheckboxEditView(Boolean.TRUE);

		} else {
			reportFileds.setQuotationCheckboxEditView(Boolean.FALSE);
			reportFileds.setTenderCheckboxEditView(Boolean.FALSE);
			reportFileds.setRestrictedTndrCheckboxEditView(Boolean.FALSE);
			reportFileds.setAllCheckboxEditView(Boolean.FALSE);

		}
		return reportFileds;
	}

	public List<SelectItem> reportStatusDropdownValues(String reportCode) {
		log.info("===reportCode" + reportCode);
		List<SelectItem> statusValues = new ArrayList<SelectItem>();

		statusValues.add(new SelectItem(null, getStatusDesc("lbl.txt.select")));
		statusValues.add(new SelectItem(1, getStatusDesc("lbl.rpt.all")));
		statusValues
				.add(new SelectItem(2, getStatusDesc("lbl.rpt.preparation")));
		statusValues
				.add(new SelectItem(3, getStatusDesc("lbl.rpt.publishing")));
		statusValues.add(new SelectItem(4,
				getStatusDesc("lbl.rpt.pendingEvaluation")));
		statusValues.add(new SelectItem(5,
				getStatusDesc("lbl.rpt.pendingSupplierFinalization")));
		statusValues.add(new SelectItem(6,
				getStatusDesc("lbl.rpt.pendingLOInLOA")));
		statusValues.add(new SelectItem(7,
				getStatusDesc("lbl.rpt.loaCompleted")));
		statusValues.add(new SelectItem(8,
				getStatusDesc("lbl.rpt.drpdown.cancelledLabel")));

		return statusValues;
	}

	/**
	 * getDefaultValuesFromAndToDate - Gets the default values from and to date.
	 * 
	 * @param reportCode
	 *            the report code
	 * @return the default values from and to date
	 */
	protected ScReportFieldsSearchBean getDefaultValuesFromAndToDate(
			ScReportFieldsSearchBean reportSearchbean) {
		log.info("ReportBackingBean-getDefaultValuesFromAndToDate");
		Date currentDate = new Date();
		Calendar fromDate = Calendar.getInstance();
		fromDate.set(fromDate.get(Calendar.YEAR), 00, 01, 0, 00, 0);

		reportSearchbean.setScPublishFromDate(fromDate.getTime());
		reportSearchbean.setScPublishToDate(currentDate);
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);

		reportSearchbean.setScCreatedFromYear(outputDateFormat.format(
				currentDate).substring(6, 10));
		reportSearchbean.setScCreatedToYear(outputDateFormat
				.format(currentDate).substring(6, 10));

		return reportSearchbean;

	}
	
	/**
	 * Created to date update.
	 */
	public void createdToDtUpdate(ScReportFieldsSearchBean reportSearchbean) {
		
		reportSearchbean.setScCreatedToYear(reportSearchbean.getScCreatedFromYear());
	}

	/**
	 * Created to month update.
	 */
	public void createdToMonthUpdate(ScReportFieldsSearchBean reportSearchbean) {
		reportSearchbean.setScCreatedToMonth(reportSearchbean.getScCreatedFromMonth());
	}
	
	protected String returnProcurementValue(String[] procuremntValue){
		 String value = QtReportConst.EMPTY_VAL;
		for(int i=0;i<procuremntValue.length;i++){
			value=procuremntValue[0];
			return value;
		}
		return value;
	}
	
	
	/**
	 * retrieveDescOfficer - Retrieve desc officer, based on user id and
	 * organization profile id.
	 * 
	 * @return the descOfficerList
	 */
	public List<SelectItem> retrieveDescOfficer() {
		log.info("ReportBackingBean-retrieveDescOfficer");
	List<SelectItem>	scDescOfficerList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			Long orgProfileId = null;
			/*if (StringUtils.isNotEmpty(this.getScPtj())) {
				orgProfileId = Long.parseLong(this.getScPtj());
			}*/
			List<Object[]> reqLst = reportSO.retrieveDeskOfficer( new FacesScopes().getUserSessionBean().getUserId(),
					orgProfileId);
			if (CollectionUtils.isNotEmpty(reqLst)) {
				for (Object[] object : reqLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					scDescOfficerList.add(selectItem);
				}
			}
		}
		return scDescOfficerList;
	}
	
	/**
	 * DeskOfficer DropDown Show or Hide Based on LoginUser.
	 * 
	 * 
	 * 
	 */
	public boolean retriveUserRoles() {
		log.info("retriveUserRoles" + this.getManagedBean().isDeskOfficerEnable());
		if (!this.getManagedBean().isDeskOfficerEnable()) {
			List<String> userRoles = retriveUserrloeByUserId();
			List<SelectItem> applovalListSize = retrieveApprovarNaikTarafSCS2();
			if ((null != applovalListSize && applovalListSize.size() > 0)
					|| ((null != userRoles) && userRoles.contains(ReportConst.BPK_APPROVER)
							|| userRoles.contains(ReportConst.COMMITTEE_APPROVER) || userRoles
								.contains(ReportConst.PUBLICATION_APPROVER))) {
				this.getManagedBean().setDeskOfficerEnable(Boolean.TRUE);
				return this.getManagedBean().isDeskOfficerEnable();
			}
		}
		return this.getManagedBean().isDeskOfficerEnable();
	}
	
	/**
	 * retriveUserrloeByUserId - Retrive userrole by user id, based on module
	 * name.
	 * 
	 * @param moduleName
	 *            the module name
	 * @return the authoriztionList
	 */
	public List<String> retriveUserrloeByUserId() {
		log.info("ReportBackingBean-retriveUserrloeByUserId");
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		List<String> authoriztionList=new ArrayList<String>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			// reqDTO.setUserId((long) 240500);
			reqDTO.setLanguageCode(ReportConst.ENG_LANG);
			UserResDTO userResDTO = userSO
					.retrieveUserRoleDescListByUserId(reqDTO);
			List<PmRoleDesc> roleDesc = userResDTO.getRoleDescList();
			if (CollectionUtils.isNotEmpty(roleDesc)) {
				for (PmRoleDesc pmRoleDesc : roleDesc) {
					pmRoleDesc.getPmRole().getRoleCode();
					if (!authoriztionList.contains(pmRoleDesc.getPmRole()
							.getRoleCode())) {
						authoriztionList.add(pmRoleDesc.getPmRole()
								.getRoleCode());
					}
				}
			}
		}
		return authoriztionList;
	}
	
	
	/**
	 * retrievePTJS2 - Retrieve PTJ for s2, based on user id.
	 * 
	 * @return the ptjList
	 */
	public List<SelectItem> retrievePTJList() {
		log.info("ReportBackingBean-retrievePTJS2");
	List<SelectItem>	scPtjList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> ptLst = reportSO.retrievePTJS2(new FacesScopes().getUserSessionBean().getUserId());
			if (CollectionUtils.isNotEmpty(ptLst)) {
				for (Object[] object : ptLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					scPtjList.add(selectItem);
				}
			}
		}
		return scPtjList;
	}
	/**
	 * PTJ DropDown Show or Hide Based on LoginUser.
	 * 
	 * 
	 * 
	 */
	public boolean retrivePtjRoles(){
		log.info("retrivePtjRoles"+this.getManagedBean().isPtjEnable());
		if(!this.getManagedBean().isPtjEnable()){
			List<SelectItem> applovalListSize=retrieveApprovarNaikTarafSCS2();
			if(null!=applovalListSize && applovalListSize.size()>0){
				log.info("applovalListSize"+applovalListSize.size());
				this.getManagedBean().setPtjEnable(Boolean.TRUE);
			}
		}
		return this.getManagedBean().isPtjEnable();
	}
	
	/**
	 * retrieveApprovarNaikTarafSCS2 - Retrieve approvar naik taraf for sc s2,
	 * based on user id.
	 * 
	 * @return the approverNayakTarafList
	 */
	private List<SelectItem> retrieveApprovarNaikTarafSCS2() {
		log.info("ReportBackingBean-retrieveApprovarNaikTarafSCS2");
	List<SelectItem>	approverNayakTarafList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> aprnktrfLst = reportSO
					.retrieveApprovarNaikTarafSCS2( new FacesScopes().getUserSessionBean().getUserId());
			if (CollectionUtils.isNotEmpty(aprnktrfLst)) {
				for (Object[] object : aprnktrfLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					approverNayakTarafList.add(selectItem);
				}
			}
		}
		return approverNayakTarafList;
	}

	protected void resetSearchBB() {
		this.getManagedBean().setQuotationNumber(null);
		this.getManagedBean().setQuotationId(null);
		ScReportFieldsSearchBean reportFileSearchBean = this.getManagedBean().getScReportSearchBean();
		String reportCode = this.getManagedBean().getReportCode();
		if (QtReportConst.SC_PROP_VALID_PERIOD_EXT_RPT_VAL.equals(reportCode) && reportFileSearchBean != null) {
			reportFileSearchBean.setScPtj(null);
			reportFileSearchBean.setScPtjName(null);
			if (!isDeskOfficer()) {
				reportFileSearchBean.setScDeskOfficer(null);
				reportFileSearchBean.setPtjProfile(null);
				reportFileSearchBean.setDoSelectItem(null);
			}
			reportFileSearchBean = getDefaultValuesFromAndToDate(reportFileSearchBean);
			this.getManagedBean().setScReportSearchBean(reportFileSearchBean);
		}
	}

	public void retrieveAgencyByQuotationNumber(ScQt scQt) {
		String ptjName = "";
		String ptjCode = "";
		boolean deskOfficerRoles = retriveUserRoles();
		String reportCode = this.getManagedBean().getReportCode();
		if (deskOfficerRoles && QtReportConst.SC_PROP_VALID_PERIOD_EXT_RPT_VAL.equals(reportCode)) {
			if (scQt != null && scQt.getQtId() != null) {
				Date date = retriveDatesforOrgProfile(scQt.getPublishDate(), scQt.getCreatedDate());

				log.info("date" + date);
				ptjCode = scQt.getOrgProfileId().toString();
				ptjName = retrievePTJByOrgProfileId(scQt.getOrgProfileId(), date);
			}
			this.getManagedBean().getScReportSearchBean().setScPtj(ptjCode);
			this.getManagedBean().getScReportSearchBean().setScPtjName(ptjName);
		}
	}

	public List<SelectItem> retrieveDescOfficerByAgency() {
		List<SelectItem> scDescOfficerList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			if (isDeskOfficer()) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(new FacesScopes().getUserSessionBean().getUserName());
				selectItem.setValue(new FacesScopes().getUserSessionBean().getUserId());
				getManagedBean().getScReportSearchBean().setDoSelectItem(selectItem);
				scDescOfficerList.add(selectItem);
			} else {
				ScQt scQt = findScQtByQuotationNumber();
				
				Long orgProfileId = null;
				if (scQt != null) {
					orgProfileId = scQt.getOrgProfileId();
				}
				
				List<Object[]> reqLst = getPMReportSO().retrieveDeskOfficer(
						new FacesScopes().getUserSessionBean().getUserId(), orgProfileId);
				if (CollectionUtils.isNotEmpty(reqLst)) {
					for (Object[] object : reqLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						scDescOfficerList.add(selectItem);
					}
				}
			}
		}
		return scDescOfficerList;
	}

	private Date retriveDatesforOrgProfile(Date publishDate, Date createdDate) {
		if (publishDate == null) {
			log.info("===========createdDate=====" + createdDate);
			return createdDate;
		}
		log.info("===========publishDate=====" + publishDate);
		return publishDate;
	}

	private ScQt findScQtByQuotationNumber() {
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		if (StringUtils.isNotEmpty(this.getManagedBean().getQuotationNumber())) {
			tenderReqDTO.setQtTndrNo(this.getManagedBean().getQuotationNumber().toUpperCase());

			QuationTenderResDTO resDTO = getQTDetailSO().retriveQtByQtNumber(tenderReqDTO);
			return resDTO.getQtDetail();
		}
		return null;
	}

	private IQTDetailSO getQTDetailSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
	}

	private IPMReportSO getPMReportSO() {
		return EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IPMReportSO.class);
	}
	
	/**
	 * Gets the PTJ values based on user role.
	 */
	public boolean getPtjValues() {

		if (!getManagedBean().isPtjEnable()) {
			if (isDeskOfficer()) {
				PmOrgProfile ptjProfile = null;
				try {
					ptjProfile = getOrgProfileByLoginUser();
				} catch (EpException e) {
					String msgCode = MessageConst.ERROR_770224;
					FacesContext.getCurrentInstance().addMessage(
							null,new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(msgCode, null,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())
											+ " (" + msgCode + ")"));
				}
				
				getManagedBean().getScReportSearchBean().setPtjProfile(ptjProfile);
				getManagedBean().setPtjEnable(true);
				
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(new FacesScopes().getUserSessionBean().getUserName());
				selectItem.setValue(new FacesScopes().getUserSessionBean().getUserId());
				getManagedBean().getScReportSearchBean().setScDeskOfficer(String.valueOf(new FacesScopes().getUserSessionBean().getUserId()));
				getManagedBean().getScReportSearchBean().setDoSelectItem(selectItem);
			} else if (this.getManagedBean().getScQt() == null){
				PmOrgProfile orgProfile = getOrgProfileByLoginUser();
				if(orgProfile != null){
					if (!ParameterConst.ORG_TYPE_MINISTRY.equalsIgnoreCase(orgProfile
							.getOrgTypeCode().getParameterCode())) {
						orgProfile = getMinistryOrgProfile(orgProfile);
					}
					this.getManagedBean().setScQt(new ScQt());
					
					this.getManagedBean().getScQt().setMinistryOrgProfile(orgProfile);
				}
			}
			
		}
		return getManagedBean().isPtjEnable();
	}
	
	/**
	 * Ptj dropdown list.
	 * 
	 * @return the list
	 */
	public PmOrgProfile getMinistryOrgProfile(PmOrgProfile orgProfile) {

		if (null != orgProfile) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();

			reqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveMinistryByOrgProfileId(reqDTO);

			return resDTO.getPmOrgProfile();
		}
		return null;
	}
	
	/**
	 * construct the ConditionFields based on MinistryProfile
	 * 
	 * @return orgProfileId
	 */
	public String constuctConditionFields() {
		StringBuilder conditionField = null;
		if (null != this.getManagedBean().getScQt() && null == this.getManagedBean().getScQt()
				.getMinistryOrgProfile()) {
			PmOrgProfile ministryOrg = this.getManagedBean().getScQt()
					.getMinistryOrgProfile();
			if (null != ministryOrg) {
				conditionField = new StringBuilder();
				Long ministryOrgProfileId = ministryOrg.getOrgProfileId();
				if (ParameterConst.ORG_TYPE_MINISTRY.equalsIgnoreCase(ministryOrg
						.getOrgTypeCode().getParameterCode())) {
					
					conditionField.append(OrgProfileUtil
							.getSearchBeanParentString(ParameterConst.ORG_TYPE_MINISTRY)
							+ CARET_SYMBOL + ministryOrgProfileId);
				} else {
					conditionField.append("orgProfileId" + CARET_SYMBOL + ministryOrgProfileId);
				}
				conditionField.append(PIPE_SYMBOL + "recordStatus" + CARET_SYMBOL + 
						DbValueConst.RECORD_STATUS_ACTIVE);
			}
			if (conditionField != null) {
				return conditionField.toString();
			}
		}
		
		return null;
	}
	
	
	/**
	 * @return
	 */
	public boolean isDeskOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.DESK_OFFICER.value());
	}
	
	/**
	 * @return
	 */
	private PmOrgProfile getOrgProfileByLoginUser() {
		PmOrgProfile pmOrgProfile = null;
		Long userId = new FacesScopes().getUserSessionBean().getUserId();
		
		if (userId != null) {
			IUserSO userSO = null;
			try {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserId(userId);
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

				if (resDTO.getUserOrgList() != null
						&& resDTO.getUserOrgList().size() > 0) {
					for (PmUserOrg pmUserOrg : resDTO.getUserOrgList()) {
						if (pmUserOrg.getPmOrgProfile() != null) {
							pmOrgProfile = pmUserOrg.getPmOrgProfile();
							break;
						}
					}
				}
			} finally {
				userSO = null;
			}	
		}
		
		return pmOrgProfile;
	}
}
