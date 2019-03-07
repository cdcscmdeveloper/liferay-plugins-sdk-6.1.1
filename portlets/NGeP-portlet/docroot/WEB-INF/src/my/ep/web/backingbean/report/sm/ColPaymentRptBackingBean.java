package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ColPaymentRptManagedBean;
import my.ep.web.util.pm.helper.PmCommonUtil;
//recommit
@ManagedBean(name = "colPaymentRptBB")
@ViewScoped
public class ColPaymentRptBackingBean extends SmReportBackingBean<ColPaymentRptManagedBean>{
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ColPaymentRptBackingBean.class);
	
	public ColPaymentRptBackingBean(){
		getManagedBean().setPaymentTypeMethodLs(getPaymentTypeMethodList());
		getManagedBean().setCdcCollector(isRole(ReportConst.ROLE_CODE_COLLECTOR_CDC));
		getManagedBean().setSoftCert(SmReportConst.PAYMENT_SOFTC_CERT);
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
		
		generateSmReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setDateFrom(null);
		getManagedBean().setDateTo(null);
		getManagedBean().setReportType(null);
		getManagedBean().setPaymentMethod(null);
		getManagedBean().setSelectedPaymentTypes(new ArrayList<String>());
		getManagedBean().setSelectedOrgProfile(null);
	}
	
	public static SelectItem[] getDocumentTypeOptions() {
		log.info("getReportTypeOptions");
		return PmCommonUtil.getDocumentTypeOption();
	}
}
