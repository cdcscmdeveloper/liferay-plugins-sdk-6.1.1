package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.TotalDailyApplRptManagedBean;

@ManagedBean(name = "totalDailyApplRptBB")
@ViewScoped
public class TotalDailyApplRptBackingBean extends
		SmReportBackingBean<TotalDailyApplRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 821555854203511673L;

	public TotalDailyApplRptBackingBean() {
		setDefaultDateFromAndDateTo();
	}

	public void generateReport() {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		SimpleDateFormat df = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);

		retrieveReportValueFromDb();
		params.put(SmReportConst.PARAM_DATE_FROM,
				df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO,
				df.format(getManagedBean().getDateTo()));

		generateSmReport(params);
	}

	public void resetSearch() {
		setDefaultDateFromAndDateTo();
	}

}
