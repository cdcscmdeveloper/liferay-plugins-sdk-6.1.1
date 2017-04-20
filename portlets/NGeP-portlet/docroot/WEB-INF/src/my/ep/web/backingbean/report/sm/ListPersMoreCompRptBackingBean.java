package my.ep.web.backingbean.report.sm;

import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ListPersMoreCompRptManagedBean;

@ManagedBean(name = "listPersMoreCompRptBB")
@ViewScoped
public class ListPersMoreCompRptBackingBean extends
		SmReportBackingBean<ListPersMoreCompRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5414529903281661055L;

	public ListPersMoreCompRptBackingBean() {
		setDefaultDateFromAndDateTo();
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
		params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean()
				.getSelectedOrgProfile().getSmSupplier().getCompanyName());
		params.put(SmReportConst.PARAM_IDENT_NO, getManagedBean()
				.getIdentificationNo());
		params.put(SmReportConst.PARAM_MOF_REG_NO_FROM, getManagedBean()
				.getMofRegNoFrom());
		params.put(SmReportConst.PARAM_MOF_REG_NO_TO, getManagedBean()
				.getMofRegNoTo());
		params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setSupplierType(StringUtils.EMPTY);
		getManagedBean().setMofRegNoFrom(StringUtils.EMPTY);
		getManagedBean().setMofRegNoTo(StringUtils.EMPTY);
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			getManagedBean().getSelectedOrgProfile().getSmSupplier()
					.setCompanyName(StringUtils.EMPTY);
		}
		getManagedBean().setIdentificationNo(StringUtils.EMPTY);
		getManagedBean().setZone(StringUtils.EMPTY);
	}

}
