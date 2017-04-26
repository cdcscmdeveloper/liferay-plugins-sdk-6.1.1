package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.OfflineSuppDocPendSubRptManagedBean;

@ManagedBean(name = "offlineSuppDocPendSubRptBB")
@ViewScoped
public class OfflineSuppDocPendSubRptBackingBean extends
		SmReportBackingBean<OfflineSuppDocPendSubRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7884411724614382877L;

	public OfflineSuppDocPendSubRptBackingBean() {
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
		params.put(SmReportConst.PARAM_ZONE,
				getManagedBean().getZone() == null ? StringUtils.EMPTY
						: getManagedBean().getZone());
		params.put(SmReportConst.PARAM_APPL_NO, getManagedBean().getApplNo());
		params.put(SmReportConst.PARAM_EP_NO, getManagedBean().getEpNo());
		params.put(SmReportConst.PARAM_REG_NO, getManagedBean().getRegNo());
		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setZone(StringUtils.EMPTY);
		getManagedBean().setApplNo(StringUtils.EMPTY);
		getManagedBean().setEpNo(StringUtils.EMPTY);
		getManagedBean().setRegNo(StringUtils.EMPTY);
		setDefaultDateFromAndDateTo();
	}

}
