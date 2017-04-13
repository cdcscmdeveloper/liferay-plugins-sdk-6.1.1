package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ExpiredCompanyRptManagedBean;

@ManagedBean(name = "expiredCompanyRptBB")
@ViewScoped
public class ExpiredCompanyRptBackingBean extends SmReportBackingBean<ExpiredCompanyRptManagedBean>{
	private static final long serialVersionUID = 1L;

	public ExpiredCompanyRptBackingBean(){
		if (getManagedBean().getDateFrom() == null
				|| getManagedBean().getDateTo() == null) {
			setDefaultDateFromAndDateTo();
		}
	}
	
	public void generateReport(){
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		retrieveReportValueFromDb();

		addCompanyNameParam(params);
		
		if(StringUtils.isNotBlank(getManagedBean().getApplType())){
			params.put(SmReportConst.PARAM_APPL_TYPE,getManagedBean().getApplType());
		}

		if(StringUtils.isNotBlank(getManagedBean().getDateFrom().toString())){
			checkOneYearDifferent(getManagedBean().getDateFrom(),getManagedBean().getDateTo());
			
			params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
			params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
		}
		

		generateSmReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setApplType(null);
		setDefaultDateFromAndDateTo();
	}
}
