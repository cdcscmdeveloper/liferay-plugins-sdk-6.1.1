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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.dto.rpt.PMReportReqDTO;
import my.ep.biz.dto.rpt.PMReportResDTO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.constant.MessageConst;
import my.ep.constants.CommonConstants;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.rpt.PMReport;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.rpt.ReportUtil;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportBaseManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

public abstract class ReportBaseBackingBean<T extends ReportBaseManagedBean> extends BaseBackingBean<T> {

	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger.getLogger(ReportBaseBackingBean.class);


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
	
	
}