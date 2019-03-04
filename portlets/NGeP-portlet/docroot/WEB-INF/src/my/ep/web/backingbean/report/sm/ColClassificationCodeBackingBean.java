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
//recommit
@ManagedBean (name="colClassificationCodeBB")
@ViewScoped
public class ColClassificationCodeBackingBean extends SmReportBackingBean<ColClassificationCodeManagedBean>{
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ColClassificationCodeBackingBean.class);
	
	public ColClassificationCodeBackingBean(){
		setDefaultDateFromAndDateTo();
	}
	
	public void generateReport(){
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		retrieveReportValueFromDb();
		
		params.put(SmReportConst.PARAM_CODE, getManagedBean().getClassificationCode());
		
		if(getManagedBean().getDateFrom() != null){
			params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
			params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
		}
		
		if(getManagedBean().getDocumentType() == null){
			params.put(ReportConst.ENHANCED_SPREAD_SHEET, getManagedBean().getDefaultOutputType());
		}else{
			params.put(ReportConst.ENHANCED_SPREAD_SHEET, getManagedBean().getDocumentType());
			getManagedBean().setDefaultOutputType(getManagedBean().getDocumentType());
		}
		
		generateSmReport(params);
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
