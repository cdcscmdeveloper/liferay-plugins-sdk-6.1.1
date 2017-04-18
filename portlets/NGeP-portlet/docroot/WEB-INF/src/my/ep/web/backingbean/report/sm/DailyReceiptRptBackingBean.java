package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.DailyReceiptRptManagedBean;

@ManagedBean(name="dailyReceiptRptBB")
@ViewScoped
public class DailyReceiptRptBackingBean extends SmReportBackingBean<DailyReceiptRptManagedBean>{
	private static final long serialVersionUID = 1L;
	
	public void generateReport(){
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		retrieveReportValueFromDb();
		
		if(StringUtils.isNotBlank(getManagedBean().getApplNo())){
			params.put(SmReportConst.PARAM_APPL_NO, getManagedBean().getApplNo());
		}
		
		if(StringUtils.isNotBlank(getManagedBean().getReceiptNo())){
			params.put(SmReportConst.PARAM_RECEIPT_NO, getManagedBean().getReceiptNo());
		}
		
		params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
		generateSmReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setApplNo(null);
		getManagedBean().setYear(null);
		getManagedBean().setYearTo(null);
		getManagedBean().setReceiptNo(null);
	}
}
