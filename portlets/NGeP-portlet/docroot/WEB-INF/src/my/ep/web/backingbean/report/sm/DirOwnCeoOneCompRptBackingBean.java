package my.ep.web.backingbean.report.sm;

import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.DirOwnCeoOneCompRptManagedBean;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "dirOwnCeoOneCompRptBB")
@ViewScoped
public class DirOwnCeoOneCompRptBackingBean extends
		SmReportBackingBean<DirOwnCeoOneCompRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6988130580818997228L;

	public DirOwnCeoOneCompRptBackingBean() {
		setDefaultDateFromAndDateTo();
		getManagedBean().setMofRegNoFrom(SmReportConst.SUPPL_CONSULT_MOF_NO);
		getManagedBean().setMofRegNoTo(SmReportConst.SUPPL_CONSULT_MOF_NO);
	}

	public void generateReport() {
		if (StringUtils.isEmpty(getManagedBean().getMofRegNoFrom())
				|| StringUtils.isEmpty(getManagedBean().getMofRegNoTo())) {
			throw new EpException(MessageConst.ERROR_REPORT_780035);
		}
		validateMofRegNo();
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

		retrieveReportValueFromDb();
		params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean()
				.getSupplierType());
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean()
					.getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}
		params.put(SmReportConst.PARAM_IDENT_NO, getManagedBean()
				.getIdentificationNo());
		params.put(SmReportConst.PARAM_MOF_REG_NO_FROM, getManagedBean()
				.getMofRegNoFrom());
		params.put(SmReportConst.PARAM_MOF_REG_NO_TO, getManagedBean()
				.getMofRegNoTo());

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setMofRegNoFrom(SmReportConst.SUPPL_CONSULT_MOF_NO);
		getManagedBean().setMofRegNoTo(SmReportConst.SUPPL_CONSULT_MOF_NO);
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			getManagedBean().getSelectedOrgProfile().getSmSupplier()
					.setCompanyName(StringUtils.EMPTY);
		}
		getManagedBean().setIdentificationNo(StringUtils.EMPTY);
	}

}
