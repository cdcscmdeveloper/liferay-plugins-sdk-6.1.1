package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.constants.CommonConstants;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.DailyApplSubmitRptManagedBean;
import my.ep.web.util.FacesScopes;

@ManagedBean(name = "dailyApplSubmitRptBB")
@ViewScoped
public class DailyApplSubmitRptBackingBean extends
		SmReportBackingBean<DailyApplSubmitRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5599569176798032965L;

	public DailyApplSubmitRptBackingBean() {
		initApplStatuses();
		initStates();
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
		params.put(SmReportConst.PARAM_REG_TYPE, getManagedBean().getRegType());
		params.put(SmReportConst.PARAM_STATE, getManagedBean().getState());
		params.put(SmReportConst.PARAM_APPL_STATUS, getManagedBean()
				.getApplStatus());

		generateSmReport(params);
	}

	public void resetSearch() {
		getManagedBean().setApplStatus(StringUtils.EMPTY);
		getManagedBean().setRegType("all");
		getManagedBean().setState(StringUtils.EMPTY);
		setDefaultDateFromAndDateTo();
	}

	public void initApplStatuses() {
		getManagedBean().setApplStatusList(new ArrayList<SelectItem>());
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {

			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> applStatusList = reportSO
					.retrieveApplicationStatusList(new FacesScopes()
							.getUserLocale().getLanguage());

			if (CollectionUtils.isNotEmpty(applStatusList)) {
				for (Object[] object : applStatusList) {
					SelectItem selectItem = new SelectItem();
					selectItem.setValue(object[0].toString());
					selectItem.setLabel(object[1].toString());
					getManagedBean().getApplStatusList().add(selectItem);
				}
			}
		}

	}

}
