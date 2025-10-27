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

/**
 * @author firdaus.omar 
 * Backing bean for the revisit report functionality.
 */
@ManagedBean(name = "smRevisitRptBB")
@ViewScoped
public class SmRevisitRptBackingBean extends SmReportBackingBean<SmRevisitRptManagedBean> {
	// 34031#note-71 : Issue 4
	private static final long serialVersionUID = 5315921064723862293L;;

	/**
	 * Constructor for the SmRevisitRptBackingBean class.
	 */
	public SmRevisitRptBackingBean() {
		this.getManagedBean().setSvoRoleCode(RoleCode.UPS_SVO.value());
		resetSearch();
	}

	/**
	 * Generates a report based on the current search parameters.
	 */
	public void generateReport() {
		LinkedHashMap<String, String> params = new LinkedHashMap<>();
		retrieveReportValueFromDb();
		params.put(SmReportConst.PARAM_ZONE, this.getManagedBean().getZone());
		if (null != this.getManagedBean().getSelectedOrgProfile()) {
			params.put(SmReportConst.PARAM_SUPPLIER_ID, String.valueOf(this.getManagedBean().getSelectedOrgProfile().getSmSupplier().getSupplierId()));
		}
		if (null != this.getManagedBean().getRevisitOfficer()) {
			params.put(SmReportConst.PARAM_SVO_USER, String.valueOf(this.getManagedBean().getRevisitOfficer().getUserId()));
		}
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		params.put(SmReportConst.PARAM_VISIT_DATE_FROM, df.format(this.getManagedBean().getVisitDateFrom()));
		params.put(SmReportConst.PARAM_VISIT_DATE_TO, df.format(this.getManagedBean().getVisitDateTo()));
		generateSmReport(params);
	}

	/**
	 * Resets the search parameters to their default values.
	 */
	public void resetSearch() {
		this.getManagedBean().setZone(null);
		this.getManagedBean().setRevisitOfficer(null);
		this.getManagedBean().setSelectedOrgProfile(null);
		Calendar fromVisitDate = Calendar.getInstance();
		fromVisitDate.set(fromVisitDate.get(Calendar.YEAR), 00, 01, 0, 00, 0);
		this.getManagedBean().setVisitDateFrom(fromVisitDate.getTime());
		this.getManagedBean().setVisitDateTo(new Date());
	}
}
