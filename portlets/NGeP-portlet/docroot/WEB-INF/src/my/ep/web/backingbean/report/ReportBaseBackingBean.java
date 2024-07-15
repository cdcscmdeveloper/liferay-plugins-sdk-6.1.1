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
package my.ep.web.backingbean.report;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.dto.rpt.PMReportReqDTO;
import my.ep.biz.dto.rpt.PMReportResDTO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.rpt.PMReport;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.rpt.ReportUtil;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportBaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

//start #27472
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
//end #27472

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

public abstract class ReportBaseBackingBean<T extends ReportBaseManagedBean> extends BaseBackingBean<T> {

	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger.getLogger(ReportBaseBackingBean.class);

	public void retrieveReportCategoryListByModuleCode(String moduleCode) {

		this.getManagedBean().setReportCategoryList(null);
		
		Long userId = getFacesScopes().getUserSessionBean().getUserId();	
		if (userId != null) {			
			IPMReportSO reportSO = null;
			
			try {
			
				reportSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
								IPMReportSO.class);
				
				String languageCode = getFacesScopes().getUserLocale()
						.getLanguage();
				PMReportReqDTO reqDTO = new PMReportReqDTO();
				reqDTO.setUserId(userId);
				reqDTO.setModuleCode(moduleCode);
				reqDTO.setLanguageCode(languageCode);
				
				PMReportResDTO resDTO = reportSO
						.retrieveCategoryListByModule(reqDTO);

				if (resDTO.isSuccessful()
						&& CollectionUtils.isNotEmpty(resDTO.getReportList())) {
					for (Object[] object : resDTO.getReportList()) {	
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1].toString());
					
						this.getManagedBean().getReportCategoryList().add(selectItem);
					}
				}		
			} catch (Exception e) {
				log.info(e.getMessage(), e);
				throw new EpException(e.getMessage());
			} finally {
				reportSO = null;
			}
		}
	}
	
	public void retrieveReportListByModuleCodeAndSubModule(String moduleCode, String subModule) {
		log.info("retrieveReportListByModuleCodeAndSubModule");

		this.getManagedBean().setReportList(null);

		Long userId = getFacesScopes().getUserSessionBean().getUserId();
		if (userId != null) {
			IPMReportSO reportSO = null;
			try {
				reportSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
								IPMReportSO.class);
				String languageCode = getFacesScopes().getUserLocale()
						.getLanguage();
				PMReportReqDTO reqDTO = new PMReportReqDTO();
				reqDTO.setUserId(userId);
				reqDTO.setModuleCode(moduleCode);
				reqDTO.setSubModule(subModule);
				reqDTO.setLanguageCode(languageCode);
				PMReportResDTO resDTO = reportSO
						.retrieveReportListByModuleAndSubModule(reqDTO);
				if (resDTO.isSuccessful()
						&& CollectionUtils.isNotEmpty(resDTO.getReportList())) {
					for (Object[] object : resDTO.getReportList()) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1].toString());
						this.getManagedBean().getReportList().add(selectItem);

					}
				}
			} catch (Exception e) {
				throw new EpException(e.getMessage());
			} finally {
				reportSO = null;
			}
		}

	}
	
	public void retrieveReportListByAuthorization(String moduleCode) {
		log.info("retrieveReportListByAuthorization");

		this.getManagedBean().setReportList(null);

		Long userId = getFacesScopes().getUserSessionBean().getUserId();
		if (userId != null) {
			IPMReportSO reportSO = null;
			try {
				reportSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
								IPMReportSO.class);
				String languageCode = getFacesScopes().getUserLocale()
						.getLanguage();
				PMReportReqDTO reqDTO = new PMReportReqDTO();
				reqDTO.setUserId(userId);
				reqDTO.setModuleCode(moduleCode);
				reqDTO.setLanguageCode(languageCode);
				PMReportResDTO resDTO = reportSO
						.retrieveReportListByModuleAndUserAuthorization(reqDTO);
				if (resDTO.isSuccessful()
						&& CollectionUtils.isNotEmpty(resDTO.getReportList())) {
					for (Object[] object : resDTO.getReportList()) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1].toString());
						this.getManagedBean().getReportList().add(selectItem);

					}
				}
			} catch (Exception e) {
				throw new EpException(e.getMessage());
			} finally {
				reportSO = null;
			}
		}

	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public void checkOneYearDifferent(Date fromDate, Date toDate) {
		log.info("********checkOneYearDifferent********");
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
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
		if (yearToInt == yearFromInt + 1) {
			if (monthToInt == monthFromInt) {
				if (dayToInt > dayFromInt) {
					throw new EpException(MessageConst.ERROR_REPORT_780011);
				}
			} else if (monthToInt > monthFromInt) {
				throw new EpException(MessageConst.ERROR_REPORT_780011);
			}
		} else if (yearToInt > yearFromInt + 1) {
			throw new EpException(MessageConst.ERROR_REPORT_780011);
		}
	}

	public void retrieveReportValueFromDb(){

		
		System.out.println("getReportCode:"+this.getManagedBean().getReportCode());
		PMReportReqDTO reqDTO = new PMReportReqDTO();
		reqDTO.setReportCode(this.getManagedBean().getReportCode());
		ReportUtil reportUtil = new ReportUtil();

		IPMReportSO reportSO = reportUtil.getPMReportSO();
		PMReportResDTO resDTO = reportSO.retriveReportByrptId(reqDTO);

		PMReport pmReport = resDTO.getPmReportDtls();

		this.getManagedBean().setDefaultOutputType(pmReport.getDefaultOutputType());
		this.getManagedBean().setReportName(pmReport.getReportFilename());
		
		this.getManagedBean().setTempAccess(pmReport.getIsTempAccess() == 1 ? true : false);
		
		String locale = getFacesScopes().getUserLocale().getLanguage();
		if(locale.equalsIgnoreCase("en")){
			this.getManagedBean().setReportTitle(pmReport.getReportTitle());
		}else{
			this.getManagedBean().setReportTitle(pmReport.getReportTitleMalay());
		}
		
	}
	
	public void getPopUpMsg(String reqNo){
		
		String msg = "";
		Locale locale = new FacesScopes().getUserLocale();

		msg = ResourceBundleUtils.getMessage("lbl.rpt.popup.instruction", new String[] { ""+reqNo} ,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		
		this.getManagedBean().setPopUpMsg(msg);
		
	}
	
	public void redirect() throws IOException {
		
		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
				.getRequestScope(WebKeys.THEME_DISPLAY);


		String commonUrl = "";
		
		try {
			commonUrl = themeDisplay.getURLCurrent().substring(
					0,
					themeDisplay.getURLCurrent().indexOf(
							themeDisplay.getLayout().getGroup()
									.getFriendlyURL())
							+ themeDisplay.getLayout().getGroup()
									.getFriendlyURL().length());
		} catch (PortalException e1) {
			e1.printStackTrace();
		} catch (SystemException e1) {
			e1.printStackTrace();
		}

		String reportsRequested = commonUrl + "/" + "report";
	    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	    externalContext.redirect(reportsRequested);
	}

	public String constructParameterString(LinkedHashMap<String, String> map){
	
		String doubleQuotes = "\"";
		
		String parameterStr = "";
		
		for (Map.Entry<String, String> entry : map.entrySet()) {
			
			String key = entry.getKey();
			String value = entry.getValue();
			
			parameterStr = parameterStr + key + "=" + doubleQuotes + value + doubleQuotes + " ";
		}
		
		//Remove last space character.
		if(map.size() > 0 ){
			parameterStr = parameterStr.substring(0, parameterStr.length()-1);
		}
		
		return parameterStr;
	}
	
	public List<String> getCommonYearList() {
		List<String> years = new ArrayList<String>();
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);

		Date currentDate = new Date();
		Integer year = null;
		String yearFrom = outputDateFormat.format(currentDate).substring(6, 10);
		if (yearFrom != null) {
			year = Integer.parseInt(yearFrom);
		}
		if (year != null) {
			for (int i = year - 7; i <= year; i++) {
				years.add(Integer.toString(i));
			}
		}
		return years;
	}
	
	public List<SelectItem> getCommonMonthList() {
		List<SelectItem> monthList = new ArrayList<SelectItem>();
		Locale locale = null;
		String lang = new FacesScopes().getUserLocale().getLanguage();
		if (lang.equalsIgnoreCase(ReportConst.ENG_LANGUAGE_CODE)) {
			locale = new Locale(lang, ReportConst.ENG_COUNTRY_CODE);
		} else if (lang.equalsIgnoreCase(ReportConst.MALAY_LANGUAGE_CODE)) {
			locale = new Locale(lang, ReportConst.MALAYSIA_COUNTRY_CODE);
		}
		String month = ReportConst.EMPTY_VAL;
		int monthNoInt;
		String monthNoStr = ReportConst.EMPTY_VAL;
		DateFormatSymbols dfs = new DateFormatSymbols(locale);
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
		return monthList;
	}
	
	/**
	 * Gets the calendar month name by no.
	 * 
	 * @param monthNo
	 *            the month no
	 * @return the calendar month name by no
	 */
	public String getCalendarMonthNameByNo(String monthNo) {

		int month = Integer.parseInt(monthNo);
		String monthString = "";

		switch (month) {
		case 1:
			monthString = "January";
			break;
		case 2:
			monthString = "February";
			break;
		case 3:
			monthString = "March";
			break;
		case 4:
			monthString = "April";
			break;
		case 5:
			monthString = "May";
			break;
		case 6:
			monthString = "June";
			break;
		case 7:
			monthString = "July";
			break;
		case 8:
			monthString = "August";
			break;
		case 9:
			monthString = "September";
			break;
		case 10:
			monthString = "October";
			break;
		case 11:
			monthString = "November";
			break;
		case 12:
			monthString = "December";
			break;
		default:
			monthString = "Invalid month";
			break;
		}
		return monthString;
	}
	
	/**
	 * checkTwoYearsFieldDefference - Check two years field defference.
	 * 
	 * @param yearFrom
	 *            the year from
	 * @param yearTo
	 *            the year to
	 */
	public void checkTwoYearsFieldDefference(String yearFrom, String yearTo) {
		log.info("ReportBackingBean-checkTwoYearsFieldDefference");
		int yearFromInt = Integer.parseInt(yearFrom);
		int yearToInt = Integer.parseInt(yearTo);
		if (yearToInt > yearFromInt + 1) {
			throw new EpException(MessageConst.ERROR_REPORT_780017);
		}
	}
	
	//start 27472 add base columstyle
	public CellStyle columnStyle(Workbook wb, 
			CreationHelper createHelper,
			Boolean isCellListHeader,
			Boolean isBold, 
			Boolean isHAlignCenter,
			Boolean isHAlignRight,
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
		if(isHAlignCenter && !isHAlignRight)cellStyle.setAlignment(HorizontalAlignment.CENTER);
		else if(isHAlignRight && !isHAlignCenter)cellStyle.setAlignment(HorizontalAlignment.RIGHT);
		else if(!isHAlignRight && !isHAlignCenter) cellStyle.setAlignment(HorizontalAlignment.LEFT);
		else cellStyle.setAlignment(HorizontalAlignment.CENTER);		
		
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
	//end 27472
	
	//30824 START
	public void setLocaleLanguageToLoginUser(){
		FacesScopes faceScope = new FacesScopes();
		Locale locale = faceScope.getUserLocale();
		String localeLanguage = locale.getLanguage();
		String dispName = faceScope.getUserScreenName();
		
		try {
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
			
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setLoginId(dispName);
			UserResDTO resDTO = userSO.retrieveUserByLoginId(reqDTO);
			
			if(resDTO.getPmUser() != null){
				reqDTO.setPmUser(resDTO.getPmUser());
				userSO.updateLocaleLanguagetoLoginUser(reqDTO,localeLanguage);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new EpException(e.getMessage());
		}
	}
	//30824 END
}
