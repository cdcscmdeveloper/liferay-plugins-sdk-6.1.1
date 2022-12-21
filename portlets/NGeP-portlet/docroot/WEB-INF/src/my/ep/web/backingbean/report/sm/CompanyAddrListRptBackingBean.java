package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.CompanyAddrListRptManagedBean;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "companyAddrListRptBB")
@ViewScoped
public class CompanyAddrListRptBackingBean extends
		SmReportBackingBean<CompanyAddrListRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7852663429473244581L;

	public CompanyAddrListRptBackingBean() {
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
		params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean()
				.getSupplierType());
		if (!StringUtils.isBlank(getManagedBean().getApplType())) {
			params.put(SmReportConst.PARAM_APPL_TYPE, getManagedBean()
					.getApplType());
		}
		params.put(SmReportConst.PARAM_APPL_STATUS, getManagedBean()
				.getApplStatus());
		params.put(SmReportConst.PARAM_SUPP_DOC_MODE, getManagedBean()
				.getSuppDocMode() == null ? StringUtils.EMPTY
				: getManagedBean().getSuppDocMode());
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean()
					.getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}
		generateSmReport(params);
	}

	public void resetSearch() {
		setDefaultDateFromAndDateTo();
		getManagedBean().setSupplierType("all");
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			getManagedBean().getSelectedOrgProfile().getSmSupplier()
					.setCompanyName(StringUtils.EMPTY);
		}
		getManagedBean().setApplStatus(StringUtils.EMPTY);
		getManagedBean().setApplType(StringUtils.EMPTY);
		getManagedBean().setSuppDocMode(StringUtils.EMPTY);
	}

}
