package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ListOfApplicationRptManagedBean;

@ManagedBean(name = "listOfApplicationRptBB")
@ViewScoped
public class ListOfApplicationRptBackingBean extends
		SmReportBackingBean<ListOfApplicationRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7924213865906825877L;

	public ListOfApplicationRptBackingBean() {
		setDefaultDateFromAndDateTo();
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
		//#32521 - START
		if (!StringUtils.isBlank(getManagedBean().getZone())) {
			params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone());
		}
		if (!StringUtils.isBlank(getManagedBean().getApplType())) {
			params.put(SmReportConst.PARAM_APPL_TYPE, getManagedBean()
					.getApplType());
		}
		//#32521 - END
		if (!StringUtils.isBlank(getManagedBean().getSupplierType())) {
			params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean()
					.getSupplierType());
		}
		if (!StringUtils.isBlank(getManagedBean().getApplNo())) {
			params.put(SmReportConst.PARAM_APPL_NO, getManagedBean()
					.getApplNo());
		}
		if (!StringUtils.isBlank(getManagedBean().getApplStatus())) {
			params.put(SmReportConst.PARAM_APPL_STATUS, getManagedBean()
					.getApplStatus());
		}
		if (!StringUtils.isBlank(getManagedBean().getEpNo())) {
			params.put(SmReportConst.PARAM_EP_NO, getManagedBean().getEpNo());
		}
		if (!StringUtils.isBlank(getManagedBean().getRegNo())) {
			params.put(SmReportConst.PARAM_REG_NO, getManagedBean().getRegNo());
		}
		if (!StringUtils.isBlank(getManagedBean().getSuppDocMode())) {
			params.put(SmReportConst.PARAM_SUPP_DOC_MODE, getManagedBean()
					.getSuppDocMode());
		}
		if (!StringUtils.isBlank(getManagedBean().getSuppDocStatus())) {
			params.put(SmReportConst.PARAM_SUPP_DOC_STATUS, getManagedBean()
					.getSuppDocStatus());
		}
		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setZone(StringUtils.EMPTY);
		getManagedBean().setSupplierType("all");
		getManagedBean().setApplNo(StringUtils.EMPTY);
		getManagedBean().setApplStatus(StringUtils.EMPTY);
		getManagedBean().setEpNo(StringUtils.EMPTY);
		getManagedBean().setRegNo(StringUtils.EMPTY);
		getManagedBean().setSuppDocMode(StringUtils.EMPTY);
		getManagedBean().setSuppDocStatus(StringUtils.EMPTY);
		//#32521 - START
		getManagedBean().setApplType(null);
		//#32521 - END

		setDefaultDateFromAndDateTo();
	}

}
