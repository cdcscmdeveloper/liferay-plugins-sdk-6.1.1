package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ApplPendingSiteVisitRptManagedBean;

@ManagedBean (name="applPendingSiteVisitRptBB")
@ViewScoped
public class ApplPendingSiteVisitRptBackingBean extends SmReportBackingBean<ApplPendingSiteVisitRptManagedBean>{
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(ApplPendingSiteVisitRptBackingBean.class);
	
	public ApplPendingSiteVisitRptBackingBean(){
		setDefaultDateFromAndDateTo();
	}
	
	public void generateReport(){
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		retrieveReportValueFromDb();
		
		addCompanyNameParam(params);
		params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
		
		generateSmReport(params);
	}
	
	public void resetSearch(){
		setDefaultDateFromAndDateTo();
		getManagedBean().setSelectedOrgProfile(null);	
		resetCategory();
	}

}
