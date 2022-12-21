package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.QuestionRespSummaryRptManagedBean;

@ManagedBean(name = "questionRespSummaryRptBB")
@ViewScoped
public class QuestionRespSummaryRptBackingBean extends
		SmReportBackingBean<QuestionRespSummaryRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8037590351466912712L;

	public QuestionRespSummaryRptBackingBean() {
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
		params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean()
				.getSupplierType());
		params.put(SmReportConst.PARAM_REPORT_TYPE, getManagedBean()
				.getReportType());
		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setZone(StringUtils.EMPTY);
		getManagedBean().setSupplierType("all");
		getManagedBean().setReportType(StringUtils.EMPTY);
		setDefaultDateFromAndDateTo();
	}

}
