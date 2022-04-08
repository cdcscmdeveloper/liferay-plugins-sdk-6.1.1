package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ColClassificationCodeManagedBean;
import my.ep.web.util.pm.helper.PmCommonUtil;

@ManagedBean (name="colClassificationCodeBB")
@ViewScoped
public class ColClassificationCodeBackingBean extends SmReportBackingBean<ColClassificationCodeManagedBean>{
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ColClassificationCodeBackingBean.class);
	
	public ColClassificationCodeBackingBean(){
		setDefaultDateFromAndDateTo();
	}
	
	public void generateReport(){
		
		log.info("INSIDE METHOD generateReport !!!");
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		retrieveReportValueFromDb();
		
		params.put(SmReportConst.PARAM_CODE, getManagedBean().getClassificationCode());
		
		if(getManagedBean().getDateFrom() != null){
			params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
			params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
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
		
		//START : Enhancement #15990
		//If Document Type - Excel will by pass runJob from oracle report.
		if(getManagedBean().getDocumentType() == null){
			log.info("INSIDE HERE DOCUMENT TYPE IS NULL");
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
			log.info("INSIDE HERE DOCUMENT TYPE IS NOT NULL");
			
			if("P".equals(getManagedBean().getDocumentType())){
				generateSmReport(params);
			}else{
				try{
					log.info("INSIDE ELSE [report_code] == "+getManagedBean().getReportCode());
					generateSMReportInExcel(getManagedBean().getReportCode(), params);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		//END : Enhancement #15990
	}
	
	public void resetSearch(){
		getManagedBean().setDateFrom(null);
		getManagedBean().setDateTo(null);
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
