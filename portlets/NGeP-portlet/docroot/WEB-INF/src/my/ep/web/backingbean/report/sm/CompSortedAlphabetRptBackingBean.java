package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constants.CommonConstants;
import my.ep.factory.EpLogger;
import my.ep.web.backingbean.report.ReportBaseBackingBean;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.CompSortedAlphabetRptManagedBean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "compSortedAlphabetRptBB")
@ViewScoped
public class CompSortedAlphabetRptBackingBean extends
		SmReportBackingBean<CompSortedAlphabetRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6424670319897936467L;
	
	private static final Logger log = EpLogger.getLogger(ReportBaseBackingBean.class);

	public void generateReport() {
		validateParameterValue(getManagedBean().getReportCode());
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		SimpleDateFormat df = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);

		retrieveReportValueFromDb();
		
		// #21331 - start
		if (getManagedBean().getSearchDate().equalsIgnoreCase("mofRegisteredDate")) {
			if (getManagedBean().getDateFrom() != null && getManagedBean().getDateTo() != null) {
				params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
				params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
			}
		} else if (getManagedBean().getSearchDate().equalsIgnoreCase("mofEndDate")) {
			if (getManagedBean().getDateExpFrom() != null && getManagedBean().getDateExpTo() != null) {
			}
			if (getManagedBean().getDateExpFrom() != null) {
				params.put(SmReportConst.PARAM_DATE_EXP_FROM,
						df.format(getManagedBean().getDateExpFrom()));
			}
			if (getManagedBean().getDateExpTo() != null) {
				params.put(SmReportConst.PARAM_DATE_EXP_TO,
						df.format(getManagedBean().getDateExpTo()));
			}
		}
		// #21331 - end
		
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
		params.put(SmReportConst.PARAM_MOF_REG_NO_FROM, getManagedBean()
				.getMofRegNoFrom());
		params.put(SmReportConst.PARAM_MOF_REG_NO_TO, getManagedBean()
				.getMofRegNoTo());

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setZone(StringUtils.EMPTY);
		getManagedBean().setSupplierType("all");
		getManagedBean().setMofRegNoFrom(StringUtils.EMPTY);
		getManagedBean().setMofRegNoTo(StringUtils.EMPTY);
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			getManagedBean().getSelectedOrgProfile().getSmSupplier()
					.setCompanyName(StringUtils.EMPTY);
		}
		// #21331 - start
		getManagedBean().setSearchDate(null);
		getManagedBean().setDateExpFrom(null);
		getManagedBean().setDateExpTo(null);
		getManagedBean().setDateFrom(null);
		getManagedBean().setDateTo(null);
		// #21331 - end
	}

}
