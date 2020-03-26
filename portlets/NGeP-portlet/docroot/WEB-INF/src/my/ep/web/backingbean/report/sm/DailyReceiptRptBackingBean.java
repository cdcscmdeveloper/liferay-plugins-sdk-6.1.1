package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.DailyReceiptRptManagedBean;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name="dailyReceiptRptBB")
@ViewScoped
public class DailyReceiptRptBackingBean extends SmReportBackingBean<DailyReceiptRptManagedBean>{
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(DailyReceiptRptBackingBean.class);
	
	public DailyReceiptRptBackingBean() {
		setDateFromAndDateTo();
	}
	
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
		
		params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
		
		//START : Enhancement #15990
		//If Document Type - Excel will by pass runJob from oracle report.
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
		//END : Enhancement #15990
	}
	
	public void resetSearch(){
		getManagedBean().setApplNo(null);
		getManagedBean().setYear(null);
		getManagedBean().setYearTo(null);
		getManagedBean().setReceiptNo(null);
		
		setDateFromAndDateTo();
	}
	
	/**
	 * document type options
	 * @return SelectItem[]
	 */
	public static SelectItem[] getDocumentTypeOptions() {
		log.info("getReportTypeOptions");
		return PmCommonUtil.getDocumentTypeOption();
	}
}
