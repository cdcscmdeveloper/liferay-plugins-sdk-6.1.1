package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.SupplierStatCategoryRptManagedBean;

@ManagedBean(name = "supplierStatCategoryRptBB")
@ViewScoped
public class SupplierStatCategoryRptBackingBean extends
		SmReportBackingBean<SupplierStatCategoryRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2045818467484539919L;

	public SupplierStatCategoryRptBackingBean() {
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
		params.put(SmReportConst.PARAM_ZONE,
				getManagedBean().getZone() == null ? StringUtils.EMPTY
						: getManagedBean().getZone());

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setZone(StringUtils.EMPTY);
		setDefaultDateFromAndDateTo();
	}

}
