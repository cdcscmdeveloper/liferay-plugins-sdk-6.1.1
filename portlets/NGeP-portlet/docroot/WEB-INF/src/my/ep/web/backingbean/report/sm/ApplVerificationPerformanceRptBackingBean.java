package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import my.ep.constants.CommonConstants;
import my.ep.factory.EpLogger;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ApplVerificationPerformanceRptManagedBean;

@ManagedBean(name = "applVerificationPerformanceRptBB")
@ViewScoped
public class ApplVerificationPerformanceRptBackingBean extends SmReportBackingBean<ApplVerificationPerformanceRptManagedBean>{
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger.getLogger(ApplVerificationPerformanceRptBackingBean.class);

	public void generateReport(){
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		log.info("********Report Code*************" + getManagedBean().getReportCode());
		
		retrieveReportValueFromDb();
		
		params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
		params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean().getSupplierType());

		for (Map.Entry<String, String> entry : params.entrySet()) {
			log.info("Key : " + entry.getKey() + " Value : " + entry.getValue());
		}
		
		generateSmReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setSupplierType("all");
		getManagedBean().setDateFrom(null);
		getManagedBean().setDateTo(null);
	}
}
