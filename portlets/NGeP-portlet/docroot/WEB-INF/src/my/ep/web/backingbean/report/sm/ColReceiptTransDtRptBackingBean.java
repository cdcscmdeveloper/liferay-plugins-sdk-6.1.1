package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ColReceiptTransDtRptManagedBean;

@ManagedBean(name = "colReceiptTransDtRptBB")
@ViewScoped
public class ColReceiptTransDtRptBackingBean extends SmReportBackingBean<ColReceiptTransDtRptManagedBean>{
	private static final long serialVersionUID = 1L;
		
	//Enhancement #19245
	public ColReceiptTransDtRptBackingBean() {
		getManagedBean().setCdcCollector(isRole(ReportConst.ROLE_CODE_COLLECTOR_CDC));
		getManagedBean().setSoftCert(SmReportConst.PAYMENT_SOFTC_CERT);

		setDateFromAndDateTo();
	}
	//end of Enhancement #19245
	
	public void generateReport(){
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		retrieveReportValueFromDb();
		
		addCompanyNameParam(params);
		addSelectedPaymentTypeParam(params);
		
		if(StringUtils.isNotBlank(getManagedBean().getReportType())){
			params.put(SmReportConst.PARAM_RPT_TYPE, getManagedBean().getReportType());
		}
		
		if(getManagedBean().getDateFrom() != null){
			params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
			params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
		}

		generateSmReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setYear(null);
		getManagedBean().setYearTo(null);
		getManagedBean().setReportType(null);
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setSelectedPaymentTypes(new ArrayList<String>());
	
		//Enhancement #19245		
		setDateFromAndDateTo();
		//end of Enhancement #19245
	}
}
