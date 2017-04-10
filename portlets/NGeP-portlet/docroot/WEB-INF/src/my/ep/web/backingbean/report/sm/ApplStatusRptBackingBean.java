package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ApplStatusRptManagedBean;

@ManagedBean(name = "applStatusRptBB")
@ViewScoped
public class ApplStatusRptBackingBean extends
		SmReportBackingBean<ApplStatusRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3440385149367317737L;

	public ApplStatusRptBackingBean() {
		if (getManagedBean().getDateFrom() == null
				|| getManagedBean().getDateTo() == null) {
			setDefaultDateFromAndDateTo();
		}
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
		params.put(SmReportConst.PARAM_MOF_NO, getManagedBean().getMofNo());
		params.put(SmReportConst.PARAM_APPL_NO, getManagedBean().getApplNo());
		params.put(SmReportConst.PARAM_REG_NO, getManagedBean().getRegNo());
		params.put(SmReportConst.PARAM_APPL_TYPE, getManagedBean()
				.getApplType());

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setMofNo(StringUtils.EMPTY);
		getManagedBean().setApplNo(StringUtils.EMPTY);
		getManagedBean().setApplType(StringUtils.EMPTY);
		getManagedBean().setRegNo(StringUtils.EMPTY);
		setDefaultDateFromAndDateTo();
	}

}
