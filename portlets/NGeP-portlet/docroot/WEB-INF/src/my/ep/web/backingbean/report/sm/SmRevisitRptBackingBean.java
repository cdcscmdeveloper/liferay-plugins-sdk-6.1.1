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
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.report.sm.SmRevisitRptManagedBean;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

/**
 * @author firdaus.omar 
 * Backing bean for the revisit report functionality.
 */
@ManagedBean(name = "smRevisitRptBB")
@ViewScoped
public class SmRevisitRptBackingBean extends SmReportBackingBean<SmRevisitRptManagedBean> {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for the SmRevisitRptBackingBean class.
	 */
	public SmRevisitRptBackingBean() {
		this.getManagedBean().setSvoRoleCode(RoleCode.UPS_SVO.value());
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

	/**
	 * Construct Site Visit Officer search condition.
	 */
	public String getConditionFieldsSVO() {
		StringBuilder sb = new StringBuilder();

		String role = "pmUserOrgs.pmUserRoles.pmRole.roleCode^";
		sb.append(role + this.getManagedBean().getSvoRoleCode());

		String searchZone = "|pmUserOrgs.pmOrgProfile.pmZone.zoneId^";
		String zone = getManagedBean().getZone();

		if (StringUtils.isBlank(zone)) {
			return sb.toString();
		}

		if (zone.equalsIgnoreCase(SupplierManagementConst.SM_STATE_NAME_SEMENANJUNG)) {
			sb.append(searchZone + "1");
		} else if (zone.equalsIgnoreCase(SupplierManagementConst.SM_STATE_NAME_SABAH)) {
			sb.append(searchZone + "2");
		} else if (zone.equalsIgnoreCase(SupplierManagementConst.SM_STATE_NAME_SARAWAK)) {
			sb.append(searchZone + "3");
		}

		return sb.toString();
	}

	/**
	 * Zone change listener.
	 */
	public void onChangeZone() {
		this.getManagedBean().setRevisitOfficer(null);
		getConditionFieldsSVO();
		RequestContext.getCurrentInstance().update(":form");
	}
}
