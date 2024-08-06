package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.DailyRegApplRptManagedBean;

@ManagedBean(name = "dailyRegApplRptBB")
@ViewScoped
public class DailyRegApplRptBackingBean extends
		SmReportBackingBean<DailyRegApplRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3935918416515852752L;

	public DailyRegApplRptBackingBean() {
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
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean()
					.getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}
		params.put(SmReportConst.PARAM_REG_TYPE, getManagedBean().getRegType());
		params.put(SmReportConst.PARAM_REG_NO, getManagedBean().getRegNo());

		//#7754 - Start
		params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone() == null ? StringUtils.EMPTY : getManagedBean()
				.getZone());		
		//#7754 - End

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setRegType(SmReportConst.SM_REG_TYPE_BASIC);
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			getManagedBean().getSelectedOrgProfile().getSmSupplier()
					.setCompanyName(StringUtils.EMPTY);
		}
		getManagedBean().setRegNo(StringUtils.EMPTY);
		setDefaultDateFromAndDateTo();
		//#7754 - Start
		getManagedBean().setZone(null);
		//#7754 - End
	}

}
