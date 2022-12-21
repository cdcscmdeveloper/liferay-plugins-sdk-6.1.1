package my.ep.web.backingbean.report.sm;

import java.util.Calendar;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.MonthlyRptManagedBean;

@ManagedBean(name = "monthlyRptBB")
@ViewScoped
public class MonthlyRptBackingBean extends
		SmReportBackingBean<MonthlyRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9110279352996690244L;

	public MonthlyRptBackingBean() {
		getManagedBean().setYearList(getCommonYearList());

	}

	public void generateReport() {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

		retrieveReportValueFromDb();
		params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		params.put(SmReportConst.PARAM_YEAR, getManagedBean().getYear());
		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setZone("");
		getManagedBean()
				.setYear(Calendar.getInstance().get(Calendar.YEAR) + "");
		setDefaultDateFromAndDateTo();
	}

}
