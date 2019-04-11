package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.DailyReceivedRptManagedBean;

@ManagedBean(name = "dailyReceivedRptBB")
@ViewScoped
public class DailyReceivedRptBackingBean extends
		SmReportBackingBean<DailyReceivedRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8499640157757275310L;

	public DailyReceivedRptBackingBean() {
		setDefaultDateFromAndDateTo();
	}

	public void generateReport() {
		SimpleDateFormat df = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();
		params.put(SmReportConst.PARAM_DATE_FROM,
				df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO,
				df.format(getManagedBean().getDateTo()));
		params.put(SmReportConst.PARAM_PAYMENT_TYPE, getManagedBean()
				.getPaymentType());
		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setPaymentType(StringUtils.EMPTY);
		setDefaultDateFromAndDateTo();
	}

}
