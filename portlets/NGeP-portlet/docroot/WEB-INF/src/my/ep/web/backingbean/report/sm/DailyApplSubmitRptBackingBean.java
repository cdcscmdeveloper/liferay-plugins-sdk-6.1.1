package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.DailyApplSubmitRptManagedBean;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "dailyApplSubmitRptBB")
@ViewScoped
public class DailyApplSubmitRptBackingBean extends
		SmReportBackingBean<DailyApplSubmitRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5599569176798032965L;

	public DailyApplSubmitRptBackingBean() {
		//#7754_#35984 - Start
		getZoneByUser();
		initStatesByZone();
		//#7754_#35984 - End
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
		if (!StringUtils.isBlank(getManagedBean().getRegType())) {
			params.put(SmReportConst.PARAM_REG_TYPE, getManagedBean()
					.getRegType());
		}
		if (!StringUtils.isBlank(getManagedBean().getState())) {
			params.put(SmReportConst.PARAM_STATE, getManagedBean().getState());
		}
		if (!StringUtils.isBlank(getManagedBean().getApplStatus())) {
			params.put(SmReportConst.PARAM_APPL_STATUS, getManagedBean()
					.getApplStatus());
		}

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setApplStatus(StringUtils.EMPTY);
		getManagedBean().setRegType("all");
		getManagedBean().setState(StringUtils.EMPTY);
		setDefaultDateFromAndDateTo();
	}

}
