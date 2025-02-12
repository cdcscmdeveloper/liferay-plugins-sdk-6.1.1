package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constants.CommonConstants;
import my.ep.type.pm.RoleCode;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.SmRevisitRptManagedBean;

@ManagedBean(name = "smRevisitRptBB")
@ViewScoped
public class SmRevisitRptBackingBean extends SmReportBackingBean<SmRevisitRptManagedBean> {
	private static final long serialVersionUID = 1L;

	public SmRevisitRptBackingBean() {
		this.getManagedBean().setSvoRoleCode(RoleCode.UPS_SVO.value());
		resetSearch();
	}

	public void generateReport() {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put(SmReportConst.PARAM_ZONE, this.getManagedBean().getZone());
		if (null != this.getManagedBean().getSelectedSupplier()) {
			params.put(SmReportConst.PARAM_SUPPLIER_ID, String.valueOf(this.getManagedBean().getSelectedSupplier().getSupplierId()));
		}
		if (null != this.getManagedBean().getRevisitOfficer()) {
			params.put(SmReportConst.PARAM_SVO_USER, String.valueOf(this.getManagedBean().getRevisitOfficer().getUserId()));
		}
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		params.put(SmReportConst.PARAM_VISIT_DATE_FROM, df.format(this.getManagedBean().getVisitDateFrom()));
		params.put(SmReportConst.PARAM_VISIT_DATE_TO, df.format(this.getManagedBean().getVisitDateTo()));
		generateSmReport(params);
	}

	public void resetSearch() {
		this.getManagedBean().setZone(null);
		this.getManagedBean().setRevisitOfficer(null);
		this.getManagedBean().setSelectedSupplier(null);
		Calendar fromVisitDate = Calendar.getInstance();
		fromVisitDate.set(fromVisitDate.get(Calendar.YEAR), 00, 01, 0, 00, 0);
		this.getManagedBean().setVisitDateFrom(fromVisitDate.getTime());
		this.getManagedBean().setVisitDateTo(new Date());
	}
}
