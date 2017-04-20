package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import my.ep.constant.MessageConst;
import my.ep.constants.CommonConstants;
import my.ep.exception.EpException;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.CompDirOwnerMngEquiRptManagedBean;

@ManagedBean(name = "compDirOwnerMngEquiRptBB")
@ViewScoped
public class CompDirOwnerMngEquiRptBackingBean extends
		SmReportBackingBean<CompDirOwnerMngEquiRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6634506001926215613L;

	public CompDirOwnerMngEquiRptBackingBean() {
		setDefaultDateFromAndDateTo();
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
		params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean()
				.getSupplierType());
		params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean()
				.getSelectedOrgProfile().getSmSupplier().getCompanyName());
		params.put(SmReportConst.PARAM_ROLE, getManagedBean().getRole());
		params.put(SmReportConst.PARAM_MOF_REG_NO_FROM, getManagedBean()
				.getMofRegNoFrom());
		params.put(SmReportConst.PARAM_MOF_REG_NO_TO, getManagedBean()
				.getMofRegNoTo());

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setZone(StringUtils.EMPTY);
		getManagedBean().setSupplierType("all");
		getManagedBean().setRole(StringUtils.EMPTY);
		getManagedBean().setMofRegNoFrom(StringUtils.EMPTY);
		getManagedBean().setMofRegNoTo(StringUtils.EMPTY);
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			getManagedBean().getSelectedOrgProfile().getSmSupplier()
					.setCompanyName(StringUtils.EMPTY);
		}
		setDefaultDateFromAndDateTo();
	}

}
