package my.ep.web.backingbean.rpt;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.logging.LogKey;
import my.ep.type.report.EnumReportType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.RptConst;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

public class SmRptParam extends RptBackingBean{

	public SmRptParam(String reportId, String reportName, boolean isExcel,
			boolean isTempAccess) {
		super(reportId, reportName, isExcel, isTempAccess);
		// TODO Auto-generated constructor stub
	}

//	public void mofRegNoNumericValidation() {
//	
//		if (reportValue.equals(RptConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL)) {
//			
//			if (isEmpty(smMofRegNoFrom) || StringUtils.isEmpty(smMofRegNoTo)) {
//				
//				valuesEmpty = true;
//				throw new EpException(MessageConst.ERROR_REPORT_780035);
//				
//			} else {
//			
//				valuesEmpty = false;
//				
//			}
//		}
//		
//		String ptString1 = RptConst.NUMERIC_PATTERN_ONLY;
//		Pattern pattern1 = Pattern.compile(ptString1);
//		
//		if (!isEmpty(smMofRegNoFrom)) {
//		
//			String mofRegNoFrom = null;
//			
//			mofRegNoFrom = smMofRegNoFrom.replace(RptConst.PARAM_IFON, RptConst.EMPTY_VAL);
//			mofRegNoFrom = mofRegNoFrom.replace(RptConst.PARAM_SPACE, RptConst.EMPTY_VAL);
//	
//			Matcher match1 = pattern1.matcher(mofRegNoFrom);
//			
//			if (!match1.matches()) {
//			
//				smMofRegNoFrom = null;
//				smMofRegNoTo = null;
//				
//				throw new EpException(MessageConst.ERROR_REPORT_780018);
//			}
//		}
//	}
//	
//	public void checkOneYearDifference(Date fromDate, Date toDate) {
//		log.info("ReportBackingBean-checkOneYearDefference");
//		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
//				CommonConstants.DEFAULT_DATE_FORMAT);
//		String yearFrom = outputDateFormat.format(fromDate).substring(6, 10);
//		String yearTo = outputDateFormat.format(toDate).substring(6, 10);
//		String monthFrom = outputDateFormat.format(fromDate).substring(3, 5);
//		String monthTo = outputDateFormat.format(toDate).substring(3, 5);
//		String dayFrom = outputDateFormat.format(fromDate).substring(0, 2);
//		String dayTo = outputDateFormat.format(toDate).substring(0, 2);
//		int yearFromInt = Integer.parseInt(yearFrom);
//		int yearToInt = Integer.parseInt(yearTo);
//		int monthFromInt = Integer.parseInt(monthFrom);
//		int monthToInt = Integer.parseInt(monthTo);
//		int dayFromInt = Integer.parseInt(dayFrom);
//		int dayToInt = Integer.parseInt(dayTo);
//		if (yearToInt == yearFromInt + 1) {
//			if (monthToInt == monthFromInt) {
//				if (dayToInt > dayFromInt) {
//					throw new EpException(MessageConst.ERROR_REPORT_780011);
//				}
//			} else if (monthToInt > monthFromInt) {
//				throw new EpException(MessageConst.ERROR_REPORT_780011);
//			}
//		} else if (yearToInt > yearFromInt + 1) {
//			throw new EpException(MessageConst.ERROR_REPORT_780011);
//		}
//	}
//
//	public void checkOnlyYearFieldDifference(String yearFrom, String yearTo) {
//		log.info("ReportBackingBean-checkOnlyYearFieldDefference");
//		int yearFromInt = Integer.parseInt(yearFrom);
//		int yearToInt = Integer.parseInt(yearTo);
//		if (yearFromInt > yearToInt) {
//			throw new EpException(MessageConst.ERROR_REPORT_780015);
//		}
//	}
//	
//	public void checkTwoYearsFieldDifference(String yearFrom, String yearTo) {
//		
//		int yearFromInt = Integer.parseInt(yearFrom);
//		int yearToInt = Integer.parseInt(yearTo);
//		
//		if (yearToInt > yearFromInt + 1) {
//			throw new EpException(MessageConst.ERROR_REPORT_780017);
//		}
//	}
//	
//	public boolean isFromDateOrToDateEmpty(){
//		return fromDate == null || toDate == null;	
//	}
//	
//	public void generateReport() {
//
//		String reportArr1[] = {RptConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL,
//							  RptConst.SM_LIST_OF_DIR_OWN_CEO_COMPANY_RPT_VAL,
//							  RptConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL,
//							  RptConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL};
//							  
//		if(isMatch(reportValue, reportArr1)){
//			mofRegNoNumericValidation();
//		}
//		
//		String reportArr2[] = {	RptConst.KPPP1_PEMATUHAN_OC_RPT_VAL,
//								RptConst.KPP1_PEMATUHAN_EC_RPT_VAL,
//								RptConst.PDW_EC_RPT_VAL };
//		
//		if(!isMatch(reportValue, reportArr2)){
//			throw new EpException(MessageConst.ERROR_REPORT_780004);
//		};
//		
//		if (fromDate.after(toDate)) {
//			throw new EpException(MessageConst.ERROR_REPORT_780008);
//		}
//	
//		if (toDate.after(fromDate)) {
//			checkOneYearDifference(fromDate, toDate);
//		}
//		
//		if (ctCircularEffectiveFromDate.after(ctCircularEffectiveToDate)) {
//			throw new EpException(MessageConst.ERROR_REPORT_780008);
//		}
//		
//		if (ctCircularEffectiveFromDate.after(ctCircularEffectiveToDate)) {
//			checkOneYearDifference(ctCircularEffectiveFromDate, ctCircularEffectiveToDate);
//		}
//
//		if (scSoneCrtdFromDate.after(scSoneCrtdToDate)) {
//			throw new EpException(MessageConst.ERROR_REPORT_780008);
//		}
//
//		if (this.scSoneCrtdToDate.after(scSoneCrtdFromDate)) {
//			checkOneYearDifference(scSoneCrtdFromDate, scSoneCrtdToDate);
//		}
//		
////		if (StringUtils.isNotEmpty(this.getManagedBean().getReportValue())) {
////				rptGenBean.setReportValueIdVar(RptConst.FL_AMP_REPORT_ID_EQ
////						+ getManagedBean().getReportValue());
////			}
//
////				rptGenBean.setUserIdVar(RptConst.FL_AMP_USER_ID_EQ
////						+ new FacesScopes().getUserSessionBean().getUserId()
////								.toString());
////			}
//	
//	
//	String[] reportArr3 = {
//			RptConst.SM_COLLECTION_AND_TRANSACTION_DATE_RPT_VAL,
//			RptConst.SM_REVENUE_COLLECTION_RPT_VAL};	
//	
//	if(isMatch(reportValue, reportArr3)){
//		
//		if(isEmpty(smMofRegistrationFeeList) || 
//		   isEmpty(smProcessingFeeList)||
//		   isEmpty(smSoftCertificateFeeList)){
//		
//			throw new EpException(MessageConst.ERROR_REPORT_780027);
//		}
//	}
//		
//	if(reportValue.equals(RptConst.SM_COLLECTION_AND_TRANSACTION_DATE_RPT_VAL)){
//
//		if (isFromDateOrToDateEmpty()) {
//			throw new EpException(MessageConst.ERROR_REPORT_780034);
//		}
//	}
//	
//	if(reportValue.equals(RptConst.SM_COMPANY_ADDRESS_LIST_RPT_VAL)){
//		throw new EpException(MessageConst.ERROR_REPORT_780036);
//	}
//	
//	if(reportValue.equals(RptConst.SM_LIST_OF_PRE_EXPIRY_COMPANY_RPT_VAL)){
//		
//		if (isFromDateOrToDateEmpty()) {
//			throw new EpException(MessageConst.ERROR_REPORT_780037);
//		}
//	}
//	
//	if(reportValue.equals(RptConst.SM_LIST_OF_GST_RGSTRD_SUPPLIER)){
//		
//		if (isEmpty(smGSTRegisteredStatus)) {
//			throw new EpException(MessageConst.ERROR_REPORT_780038);
//		}
//		
//		if (isFromDateOrToDateEmpty()) {
//			throw new EpException(MessageConst.ERROR_REPORT_780039);
//		}
//	}
//
//	if(reportValue.equals(RptConst.SM_VIRTUAL_DAY_REPORT_RPT_VAL)){
//		
//		if (isFromDateOrToDateEmpty()) {
//			throw new EpException(MessageConst.ERROR_REPORT_780040);
//		}
//	}
//	
//	if (smThresholdFromYear == null || smThresholdToYear == null) {
//		throw new EpException(MessageConst.ERROR_REPORT_780016);
//	}
//	
//	checkOnlyYearFieldDifference(smThresholdFromYear, smThresholdToYear);
//	checkOnlyYearFieldDifference(smCreatedFromYear, smCreatedToYear);
//	
//	String[] reportArr4 = {RptConst.SM_SUPPLIER_PERFORMANCE,
//			RptConst.SM_THRESHOLD};
//	
//	if(isMatch(reportValue, reportArr4)){
//		checkTwoYearsFieldDifference(smThresholdFromYear, smThresholdToYear);
//	}
//	
//	if(reportValue.equals(RptConst.SM_CAT_STATIC_BY_YEAR_RPT_VAL)){
//		checkTwoYearsFieldDifference(smCreatedFromYear, smCreatedToYear);
//	}
//
//	if(reportValue.equals(RptConst.SM_REG_COMPANY_IN_MOF_RPT_VAL)){
//		
//		if(isEmpty(smMofRegNo)){
//			throw new EpException(MessageConst.ERROR_REPORT_780029);
//		}
//		
//		checkTwoYearsFieldDifference(smCreatedFromYear, smCreatedToYear);
//	}
//	
//	if(isModuleValuePP()){
//
//		if (isEmpty(ppYear)) {
//			throw new EpException(MessageConst.ERROR_REPORT_780030);
//		}
//	}
//
//	if(isModuleValuePM()){
//
//		String[] reportArr = {  RptConst.PM_LIST_OF_EXEMPTED_ITEMS_BY_SUPPLIER_RPT_VAL,
//					 			RptConst.PM_LIST_OF_EXEMPTED_ITEMS_RPT_VAL,
//					 			RptConst.PM_LIST_OF_EXEMPTED_CONTRACTED_ITEMS_RPT_VAL};
//		
//		switch(EnumReportType.valueOf(reportCode)){
//		
//			case PM_001:
//	
//				if (isEmpty(pmServiceType) && 
//					isEmpty(pmFinancialOrgName) &&
//					isEmpty(pmBusinessRegNo) &&
//					isEmpty(pmStatus)) {
//					
//					throw new EpException(
//							MessageConst.ERROR_REPORT_780003);
//				}
//				
//				break;
//
//			case PM_002:
//	
//				if (isEmpty(pmOrgCategory)) {
//					throw new EpException(MessageConst.ERROR_REPORT_780031);
//				}
//				
//				if (isEmpty(pmOrgType)) {
//					throw new EpException(MessageConst.ERROR_REPORT_780032);
//				}
//				
//				break;
//			
//			case PM_003:
//				
//				if (isEmpty(pmOrgType)) {
//					throw new EpException(MessageConst.ERROR_REPORT_780032);
//				}
//				
//				if (isEmpty(pmOrgName)) {
//					throw new EpException(MessageConst.ERROR_REPORT_780033);
//				}								
//	
//				break;
//		
//			case PM_004:
//	
//				if (isEmpty(pmOrgCode) && 
//					isEmpty(pmOrgName) && 
//					isEmpty(pmStatus)) {
//					
//					throw new EpException(MessageConst.ERROR_REPORT_780003);
//					
//				}
//				
//				break;
//			
//			}
//		}
//	}
//
//	private String getCalendarMonthNameByNo(String monthNo) {
//
//		int month = Integer.parseInt(monthNo);
//		
//		String monthString = "";
//
//		switch (month) {
//		
//		case 1:
//			monthString = "January";
//			break;
//		case 2:
//			monthString = "February";
//			break;
//		case 3:
//			monthString = "March";
//			break;
//		case 4:
//			monthString = "April";
//			break;
//		case 5:
//			monthString = "May";
//			break;
//		case 6:
//			monthString = "June";
//			break;
//		case 7:
//			monthString = "July";
//			break;
//		case 8:
//			monthString = "August";
//			break;
//		case 9:
//			monthString = "September";
//			break;
//		case 10:
//			monthString = "October";
//			break;
//		case 11:
//			monthString = "November";
//			break;
//		case 12:
//			monthString = "December";
//			break;
//		default:
//			monthString = "Invalid month";
//			break;
//		}
//		return monthString;
//	}
//	
//	public void generateSMReport() {
//
//		if(!isEmpty(urlReportJSPName)){} else {
//			throw new EpException(MessageConst.ERROR_REPORT_780007);
//		}
//		
//		HashMap<String, String> h = new HashMap<String, String>();
//
//		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
//				CommonConstants.DEFAULT_DATE_FORMAT);
//		
//		String[] reportArr1 = { RptConst.SM_CAT_STATIC_BY_YEAR_RPT_VAL,
//							    RptConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL};
//
//		String[] reportArr2 = {	RptConst.SM_SUPPLIER_PERFORMANCE,
//								RptConst.SM_THRESHOLD,
//								RptConst.SM_THRESHOLD_VALUE};
//		
//		String[] reportArr3 = { RptConst.SM_COLLECTION_AND_TRANSACTION_DATE_RPT_VAL,
//							 	RptConst.SM_COLLECTION_PAYMENT_METHODS_RPT_VAL,
//								RptConst.SM_COLLECTION_CLASSIFICATION_CODE_RPT_VAL,
//								RptConst.SM_REVENUE_COLLECTION_RPT_VAL};
//
//		String[] reportArr4 = {	ReportConst.SM_APPL_PEND_SITE_VISIT_RPT_VAL,
//								ReportConst.SM_SITE_VISIT_APPL_REQUIRE_RPT_VAL,
//								ReportConst.SM_CATEGORY_STATISTIC_RPT_VAL,
//								ReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL,
//								ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL,
//								ReportConst.SM_CATEGORY_OVERLAPPING_RPT_VAL};
//
//		String[] reportArr5 = {	ReportConst.SM_CATEGORY_STATISTIC_RPT_VAL,
//								ReportConst.SM_CATEGORY_OVERLAPPING_RPT_VAL};
//							
//		if(isMatch(reportValue, reportArr1)){
//				
//			validateDate();
//		
//			String fromMonth = getCalendarMonthNameByNo(smFromMonth);
//			String toMonth = getCalendarMonthNameByNo(smToMonth);
//
//			if(!isEmpty(reportJsp)){
//			
//				h.put(RptConst.AMP_REPORT_NAME, reportJsp);
//				h.put(RptConst.AMP_SM_YEAR_FROM_EQ, smCreatedFromYear);
//				h.put(RptConst.AMP_SM_YEAR_TO_EQ, smCreatedToYear);
//				h.put(RptConst.AMP_SM_FROM_DATE_EQ, outputDateFormat.format(fromDate));
//				h.put(RptConst.AMP_SM_TO_DATE_EQ, outputDateFormat.format(toDate));
//				h.put(RptConst.AMP_SM_REPORT_TYPE_EQ, smReportType);	
//				h.put(RptConst.FL_AMP_REPORT_ID_EQ, reportValue);
//				h.put(RptConst.AMP_SM_FROM_DATE_EQ, fromMonth);
//				h.put(RptConst.AMP_SM_TO_DATE_EQ, toMonth);
//				h.put(RptConst.AMP_SM_SUPPLIER_ZONE_NAME_EQ, smZone); 
//				h.put(RptConst.FL_AMP_USER_ID_EQ, String.valueOf(userId));	
//			
//				String reportWebUrl = "";
//			
//				for (Map.Entry<String, String> entries : h.entrySet()) {
//				 
//					 Entry<String, String> thisEntry =  entries;
//					 
//					 String key = thisEntry.getKey();
//					 String value = thisEntry.getValue();
//					 
//					 reportWebUrl +=  key + "='" + value + "'";
//				}
//				
//			} else {
//				
//				throw new EpException(MessageConst.ERROR_REPORT_780007);
//				
//			}
//			
//		} else if(isMatch(reportValue, reportArr2)){
//		
//			h.put(RptConst.AMP_SM_SUPPLIER_ZONE_NAME_EQ, smZone);
//			h.put(RptConst.AMP_SM_YEAR_FROM_EQ, smCreatedFromYear);
//			
//		} else if(isMatch(reportValue, reportArr3)){
//		
//			h.put(RptConst.AMP_SM_OCDE_TYPE, smCode);
//			h.put(RptConst.AMP_SM_PAYMENT_TYPE_METHOD_EQ, smPaymentMethod);
//			h.put(RptConst.AMP_SM_REP_TYPE_EQ, smRTReportType);
//			h.put(RptConst.AMP_SM_SUP_NAME_EQ, smSupplierName);
//			h.put(RptConst.AMP_SM_MOF_REG_FEE_TYPE_EQ, smMofRegistrationFeeList.get(0).toString());
//			h.put(RptConst.AMP_SM_PROCESSING_FEE_TYPE_EQ, smProcessingFeeList.get(0).toString());
//			h.put(RptConst.AMP_SM_SOFTCERTIFICATE_TYPE_EQ, smSoftCertificateFeeList.get(0).toString());
//
//		} 
//		
//		if( isMatch(reportValue, reportArr1) ||
//			isMatch(reportValue, reportArr2) ||
//			isMatch(reportValue, reportArr3)) {
//			
//			if(!isEmpty(smFromMonth)){
//				smFromMonth = getCalendarMonthNameByNo(smFromMonth);
//			}
//			
////			if(!isEmpty(smToMonth)){
////				String smToMonth = getCalendarMonthNameByNo(smToMonth);
////			}
//			
//			h.put(RptConst.AMP_SM_PRFL_STS_EQ, smProfileStatus);
//			h.put(RptConst.AMP_SM_GST_REGNO_EQ, smGSTRegistrationNo);
//			h.put(RptConst.AMP_SM_GST_STS_EQ, smGSTRegisteredStatus);
//			h.put(RptConst.AMP_SM_USER_NAME_EQ, smUserName);
//			h.put(RptConst.AMP_SM_REQ_ENQ_DATE_EQ, smRequestExpiryRenewalDate);
//			h.put(RptConst.AMP_SM_MOF_ACC_END_FROM_DATE_EQ, outputDateFormat.format(mofAccountEndDateFromDate));
//			h.put(RptConst.AMP_SM_MOF_ACC_END_TO_DATE_EQ, outputDateFormat.format(mofAccountEndDateToDate));
//			h.put(RptConst.AMP_SM_MAIN_CATEGORY_EQ, mainCategoryTotVar);
//			h.put(RptConst.AMP_SM_SUB_CATEGORY_EQ, smSubCategory);
//			h.put(RptConst.AMP_SM_SUB_DIV_CATEGORY_EQ, smSubCategoryDivision);
//			h.put(RptConst.AMP_SM_REG_TYPE_EQ, smRegistrationType);
//			h.put(RptConst.AMP_SM_MOF_REG_NO_EQ, smMofRegNo);
//			h.put(RptConst.AMP_SM_APPL_STATUS_EQ, smApplicationStatus);
//			h.put(RptConst.AMP_SM_PAYMENT_TO_EQ, smPaymentTo);
//			h.put(RptConst.AMP_SM_COMP_NAME_EQ, smCompanyName);
//			h.put(RptConst.AMP_SM_REG_STATUS_EQ, smRegistrationStatus);
//			h.put(RptConst.AMP_SM_STATE_NAME_EQ, smState);
//			h.put(RptConst.AMP_SM_DISTRICT_NAME_EQ, smDistrict);
//			h.put(RptConst.AMP_SM_SITE_VISIT_STATUS_EQ, smSiteVisitStatus);
//			h.put(RptConst.AMP_SM_SUPPLIER_TYPE_EQ, smSupplierType);
//			h.put(RptConst.AMP_SM_SUPPLIER_ZONE_NAME_EQ, smZone);
//			h.put(RptConst.AMP_SM_REPORT_TYPE_EQ, smReportType);
//			h.put(RptConst.AMP_SM_PAYMENT_MENTHOD_EQ, smPaymentMethod);
//			h.put(RptConst.AMP_SM_PRO_TYPE_EQ, smProcessingType);
//			h.put(RptConst.AMP_SM_RECEIPT_NO_EQ, smReceiptNo);
//			h.put(RptConst.AMP_SM_PRO_OFFICERNAME_EQ, smProcessingOfficerName);
//			h.put(RptConst.AMP_SM_APPLICATION_NO_EQ, smApplicationNo);
//			h.put(RptConst.AMP_SM_PAYMENT_TYPE_EQ, smPaymentType);
//			h.put(RptConst.AMP_SM_APPL_TYPE_EQ, smApplicationType);
//			h.put(RptConst.AMP_SM_APPL_TYPE_EQ, smSVOUser);
//			h.put(RptConst.AMP_SM_YEAR_EQ, smYearTextVar);
//			h.put(RptConst.AMP_SM_YEAR_EQ, smMonthlyRptByYear);
//				
//			String mofRegNoFrom = null;
//			String mofRegNoTo = null;
//			
//			if(smMofRegNoFrom.contains(RptConst.PARAM_IFON)){
//	
//				mofRegNoFrom = smMofRegNoFrom.replace(RptConst.PARAM_IFON, RptConst.EMPTY_VAL);
//	
//				if (mofRegNoFrom.contains(RptConst.PARAM_SPACE)) {
//					mofRegNoFrom = mofRegNoFrom.replace(RptConst.PARAM_SPACE, RptConst.EMPTY_VAL);
//				} else {
//					mofRegNoFrom = this.getSmMofRegNoFrom();
//				}
//			}
//		
//			if (smMofRegNoTo.contains(RptConst.PARAM_IFON)) {
//				
//				mofRegNoTo = smMofRegNoTo.replace(RptConst.PARAM_IFON, RptConst.EMPTY_VAL);
//				
//				if (mofRegNoTo.contains(RptConst.PARAM_SPACE)) {
//					mofRegNoTo = mofRegNoTo.replace(RptConst.PARAM_SPACE, RptConst.EMPTY_VAL);
//				}
//				
//			} else {
//				mofRegNoTo = this.getSmMofRegNoTo();
//			}
//
//			validateMofRegNo(mofRegNoFrom, mofRegNoTo);
//				
//			h.put(RptConst.AMP_SM_MOF_REG_NO_FRM_EQ, smMofRegNoFrom);
//			h.put(RptConst.AMP_SM_MOF_REG_NO_TO_EQ, smMofRegNoTo);
//			h.put(RptConst.AMP_SM_PAIDUP_CPTL_FROM_EQ, smPaidUpCapitalFrm);
//			h.put(RptConst.AMP_SM_PAIDUP_CPTL_TO_EQ, smPaidUpCapitalTo);
//			h.put(RptConst.AMP_SM_PAIDUP_CPTL_R_ACLTD_EQ, smPaidUpCapitalNaccum);
//			h.put(RptConst.AMP_SM_EP_NO_EQ, smEpNo);
//			h.put(RptConst.AMP_SM_REG_NO_EQ, smRegistrationNo);
//			h.put(RptConst.AMP_SM_SUPP_DOC_MODE_EQ, smSupportingDocumentMode);
//			h.put(RptConst.AMP_SM_SUPP_DOC_STATUS_EQ, smSupportingDocumentStatus);
//			h.put(RptConst.AMP_SM_PERSONNEL_ROLE_EQ, smPersonnelRole);
//			h.put(RptConst.AMP_SM_IDENT_TYPE_EQ, smIdentificationType);
//			h.put(RptConst.AMP_SM_IDENT_NO_EQ, smIdentificationNumber);
//			h.put(RptConst.AMP_SM_CANCEL_BY_EQ, smCancellationBy);
//			h.put(RptConst.AMP_SM_BUS_REG_TYPE_BY_EQ, smBusCompTypeRegistrationStatus);
//			h.put(RptConst.AMP_SM_BUS_TYPE_BY_EQ, smBusCompanyType);
//			h.put(RptConst.AMP_SM_BUS_TYPE_BY_EQ, smBusPaidUpCapitalFrm);
//			h.put(RptConst.AMP_SM_BUS_TYPE_BY_EQ, smBusPaidUpCapitalTo);
//		}
//		
//		if(isMatch(reportValue, reportArr4)){
//
//			StringBuffer maincategory = new StringBuffer();
//			StringBuffer subcategory = new StringBuffer();
//			StringBuffer subcategorydivision = new StringBuffer();
//			
//			maincategory.append(RptConst.AMP_SM_MAIN_CATEGORY_EQ);
//			subcategory.append(RptConst.AMP_SM_SUB_CATEGORY_EQ);
//			subcategorydivision.append(RptConst.AMP_SM_SUB_DIV_CATEGORY_EQ);
//			
//			for (SmSupplierCategory smSupplierCat : selectedCatList) {
//
//				if (smSupplierCat.getPmCategoryL1().getCategoryL1Id() != null && 
//					smSupplierCat.getPmCategoryL2().getCategoryL2Id() != null && 
//					smSupplierCat.getPmCategoryL3().getCategoryL3Id() != null) {
//							
//					maincategory.append(RptConst.KAMA + smSupplierCat.getPmCategoryL1().getCategoryL1Id());
//					subcategory.append(RptConst.KAMA + smSupplierCat.getPmCategoryL2().getCategoryL2Id());
//					subcategorydivision.append(RptConst.KAMA + smSupplierCat.getPmCategoryL3().getCategoryL3Id());
//				}
//				
//				if (reportValue.equals(RptConst.SM_CAT_STATIC_BY_STATE_RPT_VAL)) {
//							
//					if (smState == null) {
//							
//						if (smSupplierCat.getPmCategoryL1().getCategoryL1Id() == null) {
//							throw new EpException(MessageConst.ERROR_REPORT_780047);
//						}
//						
//					} else if (smSupplierCat.getPmCategoryL2().getCategoryL2Id() == null) {
//			
//						throw new EpException(MessageConst.ERROR_REPORT_780048);
//					
//					} else if (smSupplierCat.getPmCategoryL3().getCategoryL3Id() == null) {
//								
//						throw new EpException(MessageConst.ERROR_REPORT_780049);
//					}
//				}
//						
//				
//				if (reportValue.equals(RptConst.SM_CATEGORY_STATISTIC_RPT_VAL) ||
//					reportValue.equals(RptConst.SM_CATEGORY_OVERLAPPING_RPT_VAL)) {
//							
//					if (smSupplierCat.getPmCategoryL1().getCategoryL1Id() == null) {
//						throw new EpException(MessageConst.ERROR_REPORT_780047);
//					}
//				}
//				
//				urlReportJSPName = reportJsp;
//			
//			
//			if(!isEmpty(urlReportJSPName)){
//				
//				String param = RptConst.AMP_REPORT_NAME;
//				
//				for (Map.Entry<String, String> entry : h.entrySet()) {
//				    
//				}
//			}
//			
//		if(isMatch(reportValue, reportArr5)){
//			if (smSupplierCat.getPmCategoryL1().getCategoryL1Id() == null) {
//				throw new EpException(MessageConst.ERROR_REPORT_780047);
//			}
//		}
//			}
//		}
//	}
//	
//	public void validateMofRegNo(String fromMofNo, String toMofNo) {
//	
//		Long fromMofNoLng = Long.parseLong(fromMofNo);
//		Long toMofNoLng = Long.parseLong(toMofNo);
//		
//		if (fromMofNoLng > toMofNoLng) {
//			throw new EpException(MessageConst.ERROR_REPORT_780019);
//		}
//	}
//	
//	public void validateDate() {
//
//		int fromyear = 0;
//		int toyear = 0;
//		int fromMonth = 0;
//		int toMonth = 0;
//		
//		fromyear = Integer.parseInt(smCreatedFromYear);
//		toyear = Integer.parseInt(smCreatedToYear);
//
//		fromMonth = Integer.parseInt(smFromMonth);
//		toMonth = Integer.parseInt(smToMonth);
//	
//		if (fromyear == toyear) {
//			
//			if (fromMonth > toMonth) {
//				throw new EpException(MessageConst.ERROR_REPORT_780014);
//			}
//			
//		} else if (fromyear > toyear) {
//			
//			throw new EpException(MessageConst.ERROR_REPORT_780015);
//			
//		} else if (toyear > fromyear + 1) {
//			
//			throw new EpException(MessageConst.ERROR_REPORT_780011);
//			
//		}
//	}
//	
//	protected void fileGenerateReport(String reportTitle,			
//			String paramConstrString) {
//		
//		Timestamp time = DateUtils.getCurrentDate();
//		
//		StringBuilder gnrtdURL = new StringBuilder();
//
//		boolean isTempAccess = false;
//		
//		if(reportUrlValue.equals("1")){
//			gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.EXC_PREV_URL2));
//			isTempAccess = true;
//		}
//
//		if(reportUrlValue.equals("0")){
//			gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.EXC_PREV_URL));
//		}
//		
//		gnrtdURL.append(paramConstrString);
//		
//		String footerStr = "";
//		
//		boolean isExcel = false;
//		
//		if(reportValue.equals(RptConst.SM_LIST_OF_SOFTCERT_PAYMENT_RPT_VAL) ||
//		   reportValue.equals(RptConst.SM_REJECTED_SOFTCERT_RPT_VAL)){
//		
//			gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(
//					EpPropertiesConst.EXC_NEXT_URL_XLS));
//			
//			footerStr = " destype=cache desformat=enhancedspreadsheet ";
//			
//			isExcel = true;
//		
//		} else {
//			
//			gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(
//					EpPropertiesConst.EXC_NEXT_URL));
//			
//			footerStr = " destype=cache desformat=pdf ";
//		}
//
//		String reportStr = gnrtdURL.toString();
//
//		paramConstrString = paramConstrString.replace("&", " ");
//		paramConstrString = paramConstrString.replaceAll("^\\s+", "");
//		paramConstrString = paramConstrString.replaceAll("\\s+$", "");
//		paramConstrString = paramConstrString.replaceAll("(=)","$1\"");
//		paramConstrString = paramConstrString.replaceAll("( )","\"$1");
//		paramConstrString = paramConstrString + "\"";
//		
//		paramConstrString = paramConstrString.replaceAll("%20" , " ");
//
//		StringBuffer reportUrl = new StringBuffer(paramConstrString);
//		
//		PmUser user = new FacesScopes().getUserSessionBean();
//		new LogKey().setUserId(user.getUserId());
//		
//		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
//				.getServiceObject(RptConst.RPT_MODULE_SER_OBJ,
//						IRPTRequestSO.class);
//		
//		RPTRequestResDTO resDTO = rptRequestSO.generateReport(reportUrl, this.getReportValue(), reportTitle, user.getUserId(), isExcel, isTempAccess);
//		
//		requestId = String.valueOf(resDTO.getRequestId());
//		jobId = String.valueOf(resDTO.getJobId());
//		
//		RequestContext.getCurrentInstance().execute("ex.show();");
//
//	}
}
