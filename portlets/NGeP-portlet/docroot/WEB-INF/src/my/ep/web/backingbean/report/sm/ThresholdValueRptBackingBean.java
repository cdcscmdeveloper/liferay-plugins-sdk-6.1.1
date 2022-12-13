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
		getManagedBean().setYear(getManagedBean().getCurrYear());
		getManagedBean().setYearTo(getManagedBean().getCurrYear());
	}

	public void generateReport() {
		checkTwoYearsFieldDefference(getManagedBean().getYear(),
				getManagedBean().getYearTo());

		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();

		params.put(SmReportConst.PARAM_FROM_YEAR, getManagedBean().getYear());
		params.put(SmReportConst.PARAM_TO_YEAR, getManagedBean().getYearTo());
		
		//#7754 - Start
		params.put(SmReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId().toString());
		//#7754 - End

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setYear(getManagedBean().getCurrYear());
		getManagedBean().setYearTo(getManagedBean().getCurrYear());
	}

}
