package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.SuppContPersBankBranchRptManagedBean;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "suppContPersBankBranchRptBB")
@ViewScoped
public class SuppContPersBankBranchRptBackingBean extends
		SmReportBackingBean<SuppContPersBankBranchRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8780162065038924664L;

	public SuppContPersBankBranchRptBackingBean() {
		setDefaultDateFromAndDateTo();
		//#28115 - start
		getSupplierInfo();
		//#28115 - end
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
		params.put(SmReportConst.PARAM_REG_NO, getManagedBean().getRegNo());
		if (!StringUtils.isBlank(getManagedBean().getSupplierType())) {
			params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean()
					.getSupplierType());
		}
		if (!StringUtils.isBlank(getManagedBean().getRegType())) {
			params.put(SmReportConst.PARAM_REG_TYPE, getManagedBean()
					.getRegType());
		} else {
			//#28115 - start
			params.put(SmReportConst.PARAM_REG_TYPE, ReportConst.SUPPL_ALL_TYPE);
			//#28115 - end
		}
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean()
					.getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}
		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setZone(StringUtils.EMPTY);
		getManagedBean().setSupplierType("all");
		getManagedBean().setRegNo(StringUtils.EMPTY);
		getManagedBean().setRegType(StringUtils.EMPTY);
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			getManagedBean().getSelectedOrgProfile().getSmSupplier()
					.setCompanyName(StringUtils.EMPTY);
		}
		setDefaultDateFromAndDateTo();
	}
}
