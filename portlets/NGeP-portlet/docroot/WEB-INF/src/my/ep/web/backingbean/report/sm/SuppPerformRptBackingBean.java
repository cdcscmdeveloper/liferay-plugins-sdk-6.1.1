package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.SuppPerformRptManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "suppPerformRptBB")
@ViewScoped
public class SuppPerformRptBackingBean extends
		SmReportBackingBean<SuppPerformRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7400800649761552705L;

	public SuppPerformRptBackingBean() {
		getManagedBean().setYearList(getYearList());
		setDefaultDateFromAndDateTo();
		onChangeRegType();
	}

	public void generateReport() {
		checkTwoYearsFieldDefference(getManagedBean().getYear(),
				getManagedBean().getYearTo());
		SimpleDateFormat df = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();
		params.put(SmReportConst.PARAM_DATE_FROM,
				df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO,
				df.format(getManagedBean().getDateTo()));
		if (getManagedBean().getSelectedOrgProfile() != null) {
			if (getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
				params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean()
						.getSelectedOrgProfile().getSmSupplier()
						.getCompanyName());
			}
		}
		params.put(SmReportConst.PARAM_MOF_NO, getManagedBean().getMofNo());
		if (!StringUtils.isBlank(getManagedBean().getRegType())) {
			params.put(SmReportConst.PARAM_REG_TYPE, getManagedBean()
					.getRegType());
		}
		params.put(SmReportConst.PARAM_EP_NO, getManagedBean().getEpNo());
		params.put(SmReportConst.PARAM_YEAR_FROM, getManagedBean().getYear());
		params.put(SmReportConst.PARAM_YEAR_TO, getManagedBean().getYearTo());
		params.put(SmReportConst.PARAM_YEAR,
				Calendar.getInstance().get(Calendar.YEAR) + "");
		generateSmReport(params);
	}

	public void resetSearch() {
		if (getManagedBean().getSelectedOrgProfile() != null) {
			if (getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
				getManagedBean().getSelectedOrgProfile().getSmSupplier()
						.setCompanyName(StringUtils.EMPTY);
			}
		}
		getManagedBean().setMofNo(StringUtils.EMPTY);
		getManagedBean().setRegType(StringUtils.EMPTY);
		getManagedBean().setEpNo(StringUtils.EMPTY);
		setDefaultDateFromAndDateTo();
	}

	public void onChangeRegType() {
		String label = SmCommonUtil.getMessage("lbl.rpt.mofregistereddatefrom",
				null);

		if (StringUtils.isBlank(getManagedBean().getRegType())) {
			label = SmCommonUtil.getMessage("lbl.rpt.allregistereddatefrom",
					null);
		} else if (getManagedBean().getRegType().equals(
				SmReportConst.SM_REG_TYPE_BASIC)) {
			label = SmCommonUtil.getMessage("lbl.rpt.registereddatefrom", null);
		}
		getManagedBean().setRegDateLabel(label);
	}

	private List<String> getYearList() {
		List<String> years = new ArrayList<String>();
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);

		Date currentDate = new Date();
		Integer year = null;
		String yearFrom = outputDateFormat.format(currentDate).substring(6, 10);
		if (yearFrom != null) {
			year = Integer.parseInt(yearFrom);
		}
		if (year != null) {
			while (year >= 2018) {
				years.add(Integer.toString(year));
				year--;
			}
		}
		return years;
	}

}
