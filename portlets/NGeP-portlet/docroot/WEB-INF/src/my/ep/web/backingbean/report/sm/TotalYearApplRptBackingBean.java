package my.ep.web.backingbean.report.sm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.TotalYearApplRptManagedBean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "totalYearApplRptBB")
@ViewScoped
public class TotalYearApplRptBackingBean extends
		SmReportBackingBean<TotalYearApplRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1467771092807434815L;
	public static Logger log = Logger
			.getLogger(TotalYearApplRptBackingBean.class);

	public TotalYearApplRptBackingBean() {
		getManagedBean().setMonthList(getCommonMonthList());
		getManagedBean().setYearList(getCommonYearList());
	}

	public void generateReport() {
		validate();
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();
		String datefrom = "01/" + getManagedBean().getMonthFrom() + "/"
				+ getManagedBean().getYear();
		String dateTo = getLastDayOfMonth();
		if (!StringUtils.isBlank(getManagedBean().getZone())) {
			params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		}
		params.put(SmReportConst.PARAM_DATE_FROM, datefrom);
		params.put(SmReportConst.PARAM_DATE_TO, dateTo);

		log.info("Param sent for report SM_009:" + getManagedBean().getZone()
				+ "||" + datefrom + "||" + dateTo);

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

	private String getLastDayOfMonth() {
		String date = "01/" + getManagedBean().getMonthTo() + "/"
				+ getManagedBean().getYearTo();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date convertedDate = dateFormat.parse(date);
			Calendar c = Calendar.getInstance();
			c.setTime(convertedDate);
			c.set(Calendar.DAY_OF_MONTH,
					c.getActualMaximum(Calendar.DAY_OF_MONTH));

			date = dateFormat.format(c.getTime());
		} catch (ParseException e) {

		}
		return date;
	}
}
