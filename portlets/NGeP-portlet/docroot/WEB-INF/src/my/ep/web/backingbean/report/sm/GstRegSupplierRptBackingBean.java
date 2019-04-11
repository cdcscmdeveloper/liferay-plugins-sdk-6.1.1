package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.GstRegSupplierRptManagedBean;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "gstRegSupplierRptBB")
@ViewScoped
public class GstRegSupplierRptBackingBean extends
		SmReportBackingBean<GstRegSupplierRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4204281795015981972L;

	public GstRegSupplierRptBackingBean() {
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
		if (!StringUtils.isBlank(getManagedBean().getSupplierType())) {
			params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean()
					.getSupplierType());
		}
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean()
					.getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}
		if (!StringUtils.isBlank(getManagedBean().getRegType())) {
			params.put(SmReportConst.PARAM_REG_TYPE, getManagedBean()
					.getRegType());
		}
		if (!StringUtils.isBlank(getManagedBean().getGstRegStatus())) {
			params.put(SmReportConst.PARAM_GST_STATUS, getManagedBean()
					.getGstRegStatus());
		}
		params.put(SmReportConst.PARAM_REG_NO, getManagedBean().getRegNo());
		params.put(SmReportConst.PARAM_GST_REG_NO, getManagedBean()
				.getGstRegNo());

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setZone(StringUtils.EMPTY);
		getManagedBean().setSupplierType("all");
		getManagedBean().setRegType("all");
		getManagedBean().setRegNo(StringUtils.EMPTY);
		getManagedBean().setGstRegNo(StringUtils.EMPTY);
		getManagedBean().setGstRegStatus(StringUtils.EMPTY);
		setDefaultDateFromAndDateTo();
	}

}
