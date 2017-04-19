package my.ep.web.backingbean.report.sm;

import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ThresholdValueRptManagedBean;

@ManagedBean(name = "thresholdValueRptBB")
@ViewScoped
public class ThresholdValueRptBackingBean extends
		SmReportBackingBean<ThresholdValueRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3853880639379649009L;

	public ThresholdValueRptBackingBean() {
		getManagedBean().setYearList(getCommonYearList());
	}

	public void generateReport() {
		checkTwoYearsFieldDefference(getManagedBean().getYear(),
				getManagedBean().getYearTo());

		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();

		params.put(SmReportConst.PARAM_YEAR_FROM, getManagedBean().getYear());
		params.put(SmReportConst.PARAM_YEAR_TO, getManagedBean().getYearTo());

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setYear(null);
		getManagedBean().setYearTo(null);
	}

}
