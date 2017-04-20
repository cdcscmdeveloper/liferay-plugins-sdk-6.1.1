package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ColClassificationCodeManagedBean;

@ManagedBean (name="colClassificationCodeBB")
@ViewScoped
public class ColClassificationCodeBackingBean extends SmReportBackingBean<ColClassificationCodeManagedBean>{
	private static final long serialVersionUID = 1L;
	
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
		
		generateSmReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setDateFrom(null);
		getManagedBean().setDateTo(null);
	}
}
