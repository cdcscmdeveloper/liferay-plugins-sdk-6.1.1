package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.VirtualCertReleasedRptManagedBean;

@ManagedBean(name = "virtualCertReleasedRptBB")
@ViewScoped
public class VirtualCertReleasedRptBackingBean extends SmReportBackingBean<VirtualCertReleasedRptManagedBean>{
	private static final long serialVersionUID = 1L;

	public void generateReport(){
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		retrieveReportValueFromDb();

		
		addCompanyNameParam(params);
		if(StringUtils.isNotBlank(getManagedBean().getZone())){
			params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		}else{
			params.put(SmReportConst.PARAM_ZONE, "");
		}

		params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));

		generateSmReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setDateFrom(null);
		getManagedBean().setDateTo(null);
		getManagedBean().setZone("");
		getManagedBean().setSelectedOrgProfile(null);
	}
}
