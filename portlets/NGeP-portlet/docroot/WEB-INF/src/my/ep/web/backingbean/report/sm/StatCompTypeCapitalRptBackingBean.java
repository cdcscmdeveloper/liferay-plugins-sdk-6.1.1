package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import my.ep.constant.MessageConst;
import my.ep.constants.CommonConstants;
import my.ep.exception.EpException;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.StatCompTypeCapitalRptManagedBean;

@ManagedBean(name = "statCompTypeCapitalRptBB")
@ViewScoped
public class StatCompTypeCapitalRptBackingBean extends
		SmReportBackingBean<StatCompTypeCapitalRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 932502332895436850L;

	public StatCompTypeCapitalRptBackingBean() {
		setDefaultDateFromAndDateTo();
		initBusinesTypeList();
	}

	public void generateReport() {
		if (StringUtils.isEmpty(getManagedBean().getMofRegNoFrom())
				|| StringUtils.isEmpty(getManagedBean().getMofRegNoTo())) {
			throw new EpException(MessageConst.ERROR_REPORT_780035);
		}
		validateMofRegNo();
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		SimpleDateFormat df = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);

		retrieveReportValueFromDb();
		params.put(SmReportConst.PARAM_DATE_FROM,
				df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO,
				df.format(getManagedBean().getDateTo()));
		params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean()
				.getSupplierType());
		params.put(SmReportConst.PARAM_BUSS_TYPE, getManagedBean()
				.getBussType());

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setZone(StringUtils.EMPTY);
		getManagedBean().setSupplierType("all");
		getManagedBean().setBussType(StringUtils.EMPTY);
		setDefaultDateFromAndDateTo();
	}

}
