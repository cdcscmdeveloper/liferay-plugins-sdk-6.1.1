package my.ep.web.backingbean.report.sm;

import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.TotalYearApplRptManagedBean;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "totalYearApplRptBB")
@ViewScoped
public class TotalYearApplRptBackingBean extends
		SmReportBackingBean<TotalYearApplRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1467771092807434815L;

	public TotalYearApplRptBackingBean() {
		getManagedBean().setMonthList(getCommonMonthList());
		getManagedBean().setYearList(getCommonYearList());
	}

	public void generateReport() {
		validate();
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();
		params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		params.put(SmReportConst.PARAM_YEAR_FROM, getManagedBean().getYear());
		params.put(SmReportConst.PARAM_YEAR_TO, getManagedBean().getYearTo());
		params.put(SmReportConst.PARAM_DATE_FROM,
				getCalendarMonthNameByNo(getManagedBean().getMonthFrom()));
		params.put(SmReportConst.PARAM_DATE_TO,
				getCalendarMonthNameByNo(getManagedBean().getMonthTo()));

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setZone(StringUtils.EMPTY);
		getManagedBean().setMonthFrom("1");
		getManagedBean().setMonthTo("1");
		getManagedBean().setYear(getManagedBean().getYearList().get(0));
		getManagedBean().setYearTo(getManagedBean().getYearList().get(0));
	}

	private void validate() {
		String monthFrom = getManagedBean().getMonthFrom().length() == 1 ? "0"
				+ getManagedBean().getMonthFrom() : getManagedBean()
				.getMonthFrom();
		String monthTo = getManagedBean().getMonthTo().length() == 1 ? "0"
				+ getManagedBean().getMonthTo() : getManagedBean().getMonthTo();
		int from = Integer.parseInt(getManagedBean().getYear() + monthFrom);
		int to = Integer.parseInt(getManagedBean().getYearTo() + monthTo);
		if (to < from) {
			throw new EpException(MessageConst.ERROR_REPORT_780050);
		}
	}
}
