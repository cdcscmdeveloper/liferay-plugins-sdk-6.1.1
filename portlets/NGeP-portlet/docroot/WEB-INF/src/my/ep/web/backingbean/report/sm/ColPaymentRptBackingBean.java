package my.ep.web.backingbean.report.sm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
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
import org.apache.poi.xwpf.usermodel.VerticalAlign;

import com.liferay.portal.util.PortalUtil;

import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ColPaymentRptManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.pm.helper.PmCommonUtil;

@ManagedBean(name = "colPaymentRptBB")
@ViewScoped
public class ColPaymentRptBackingBean extends SmReportBackingBean<ColPaymentRptManagedBean>{
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ColPaymentRptBackingBean.class);
	
	public ColPaymentRptBackingBean(){
		getManagedBean().setPaymentTypeMethodLs(getPaymentTypeMethodList());
		
		//Enhancement #19245
		getManagedBean().setCdcCollector(isRole(ReportConst.ROLE_CODE_COLLECTOR_CDC));
		getManagedBean().setSoftCert(SmReportConst.PAYMENT_SOFTC_CERT);
		
		setDateFromAndDateTo();
		//end of Enhancement #19245
	}
	
	public void generateReport() {
		SimpleDateFormat df = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();

		addCompanyNameParam(params);
		addSelectedPaymentTypeParam(params);

		if (StringUtils.isNotBlank(getManagedBean().getReportType())) {
			params.put(SmReportConst.PARAM_RPT_TYPE, getManagedBean()
					.getReportType());
		}

		if (getManagedBean().getDateFrom() != null) {
			params.put(SmReportConst.PARAM_DATE_FROM,
					df.format(getManagedBean().getDateFrom()));
			params.put(SmReportConst.PARAM_DATE_TO,
					df.format(getManagedBean().getDateTo()));
		}

		if (!StringUtils.isBlank(getManagedBean().getPaymentMethod())) {
			params.put(SmReportConst.PARAM_PAYMENT_METHOD,
					StringUtils.upperCase(getManagedBean().getPaymentMethod()));
		}

		if(getManagedBean().getDocumentType() == null){
			if(getManagedBean().getDefaultOutputType().equals("P")){
				params.put(ReportConst.PARAM_REPORT_OUTPUT_FORMAT, "pdf");
			}else{
				params.put(ReportConst.PARAM_REPORT_OUTPUT_FORMAT, "enhancedspreadsheet");
			}	
		}else{
			if(getManagedBean().getDocumentType().equals("P")){
				params.put(ReportConst.PARAM_REPORT_OUTPUT_FORMAT, "pdf");
			}else{
				params.put(ReportConst.PARAM_REPORT_OUTPUT_FORMAT, "enhancedspreadsheet");
			}			
			getManagedBean().setDefaultOutputType(getManagedBean().getDocumentType());
		}
		
		//Enhancement #15990
		//If Document Type - Excel will bypass runJob from oracle report.
		if(getManagedBean().getDocumentType() == null){
			if("P".equals(getManagedBean().getDefaultOutputType())){
				generateSmReport(params);
			}else{
				try{
					generateSMReportInExcel(getManagedBean().getReportCode(), params);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}else{
			if("P".equals(getManagedBean().getDocumentType())){
				generateSmReport(params);
			}else{
				try{
					generateSMReportInExcel(getManagedBean().getReportCode(), params);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}		
	}
	
	public void resetSearch(){
		getManagedBean().setReportType(null);
		getManagedBean().setPaymentMethod(null);
		getManagedBean().setSelectedPaymentTypes(new ArrayList<String>());
		getManagedBean().setSelectedOrgProfile(null);
		
		//Enhancement #19245
		setDateFromAndDateTo();
		//end of Enhancement #19245
	}
	
	public static SelectItem[] getDocumentTypeOptions() {
		log.info("getReportTypeOptions");
		return PmCommonUtil.getDocumentTypeOption();
	}
}
