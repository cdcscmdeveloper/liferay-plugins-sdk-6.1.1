package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constant.MessageConst;
import my.ep.constants.CommonConstants;
import my.ep.exception.EpException;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.CompDirOwnerMngEquiRptManagedBean;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "compSortedAlphabetRptBB")
@ViewScoped
public class CompSortedAlphabetRptBackingBean extends
		SmReportBackingBean<CompDirOwnerMngEquiRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6424670319897936467L;

	public CompSortedAlphabetRptBackingBean() {
		setDefaultDateFromAndDateTo();
		if (getManagedBean().getDateExpFrom() == null
				|| getManagedBean().getDateExpTo() == null) {
			Calendar fromDate = Calendar.getInstance();
			fromDate.set(fromDate.get(Calendar.YEAR), 00, 01, 0, 00, 0);
			getManagedBean().setDateExpFrom(fromDate.getTime());
			getManagedBean().setDateExpTo(new Date());
		}
	}

	public void generateReport() {
		if (StringUtils.isEmpty(getManagedBean().getMofRegNoFrom())
				|| StringUtils.isEmpty(getManagedBean().getMofRegNoTo())) {
			throw new EpException(MessageConst.ERROR_REPORT_780035);
		}
		validateMofRegNo();
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		SimpleDateFormat df = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);

		retrieveReportValueFromDb();
		params.put(SmReportConst.PARAM_DATE_FROM,
				df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO,
				df.format(getManagedBean().getDateTo()));
		params.put(SmReportConst.PARAM_DATE_EXP_FROM,
				df.format(getManagedBean().getDateExpFrom()));
		params.put(SmReportConst.PARAM_DATE_EXP_TO,
				df.format(getManagedBean().getDateExpTo()));
		params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean()
				.getSupplierType());
		params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean()
				.getSelectedOrgProfile().getSmSupplier().getCompanyName());
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
		setDefaultDateFromAndDateTo();
		if (getManagedBean().getDateExpFrom() == null
				|| getManagedBean().getDateExpTo() == null) {
			Calendar fromDate = Calendar.getInstance();
			fromDate.set(fromDate.get(Calendar.YEAR), 00, 01, 0, 00, 0);
			getManagedBean().setDateExpFrom(fromDate.getTime());
			getManagedBean().setDateExpTo(new Date());
		}
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			getManagedBean().getSelectedOrgProfile().getSmSupplier()
					.setCompanyName(StringUtils.EMPTY);
		}
	}

}
