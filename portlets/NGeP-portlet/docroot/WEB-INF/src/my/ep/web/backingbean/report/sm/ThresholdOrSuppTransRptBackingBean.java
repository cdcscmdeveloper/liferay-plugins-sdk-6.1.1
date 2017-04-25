package my.ep.web.backingbean.report.sm;

import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ThresholdOrSuppTransRptManagedBean;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "thresholdOrSuppTransRptBB")
@ViewScoped
public class ThresholdOrSuppTransRptBackingBean extends
		SmReportBackingBean<ThresholdOrSuppTransRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1006125624653562063L;

	public ThresholdOrSuppTransRptBackingBean() {
		getManagedBean().setYearList(getCommonYearList());
	}

	public void generateReport() {
		checkTwoYearsFieldDefference(getManagedBean().getYear(),
				getManagedBean().getYearTo());
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();
		params.put(SmReportConst.PARAM_YEAR_FROM, getManagedBean().getYear());
		params.put(SmReportConst.PARAM_YEAR_TO, getManagedBean().getYearTo());
		params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean()
				.getSupplierType());
		params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean()
				.getSelectedOrgProfile().getSmSupplier().getCompanyName());
		params.put(SmReportConst.PARAM_MOF_NO, getManagedBean().getMofNo());
		params.put(SmReportConst.PARAM_REG_TYPE, getManagedBean().getRegType());
		params.put(SmReportConst.PARAM_REG_NO, getManagedBean().getRegNo());

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setZone(StringUtils.EMPTY);
		getManagedBean().setSupplierType("all");
		getManagedBean().setYear(null);
		getManagedBean().setYearTo(null);
		getManagedBean().setRegNo(StringUtils.EMPTY);
		getManagedBean().setRegType(StringUtils.EMPTY);
		getManagedBean().setMofNo(StringUtils.EMPTY);
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			getManagedBean().getSelectedOrgProfile().getSmSupplier()
					.setCompanyName(StringUtils.EMPTY);
		}
	}

}
