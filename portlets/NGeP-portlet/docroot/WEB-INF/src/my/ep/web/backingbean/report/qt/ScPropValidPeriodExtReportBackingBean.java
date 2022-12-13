package my.ep.web.backingbean.report.qt;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.web.bean.report.ScReportFieldsSearchBean;
import my.ep.web.constant.report.QtReportConst;
import my.ep.web.managedbean.report.qt.ScPropValidPeriodExtReportManagedBean;

import org.apache.commons.lang3.StringUtils;

@ManagedBean(name = "scPropValidPeriodExtReportBackingBean")
@ViewScoped
public class ScPropValidPeriodExtReportBackingBean extends QtReportBackingBean<ScPropValidPeriodExtReportManagedBean>
		implements Serializable {
	private static final long serialVersionUID = 1L;

//	private static final Logger log = EpLogger.getLogger(ScPropValidPeriodExtReportBackingBean.class);

	@PostConstruct
	public void init() {
		this.getManagedBean().setReportCode(QtReportConst.SC_PROP_VALID_PERIOD_EXT_RPT_VAL);
		
		ScReportFieldsSearchBean reportFileSearchBean = this.getManagedBean().getScReportSearchBean();
		
		if (null == reportFileSearchBean) {
			reportFileSearchBean = new ScReportFieldsSearchBean();
			reportFileSearchBean = getDefaultValuesFromAndToDate(reportFileSearchBean);
			
			this.getManagedBean().setScReportSearchBean(reportFileSearchBean);
		}
	}

	@Override
	public void generateReport(LinkedHashMap<String, String> params) {
		generateSCReport(params);
	}

	@Override
	public void resetSearch() {
		resetSearchBB();
	}

	@Override
	public LinkedHashMap<String, String> getparams() {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		ScReportFieldsSearchBean reportFieldBean = this.getManagedBean().getScReportSearchBean();

		//report change date strformat -start-
		String fromDate = new SimpleDateFormat("dd/MM/yyyy").format(reportFieldBean.getScPublishFromDate());
		String toDate = new SimpleDateFormat("dd/MM/yyyy").format(reportFieldBean.getScPublishToDate());
		
		params.put(QtReportConst.PARAM_SUBMIT_FR_DT, fromDate);
		params.put(QtReportConst.PARAM_SUBMIT_TO_DT, toDate);
		//report change date strformat -end-
		
		params.put(QtReportConst.QUOTATION_TENDER_ID, this.getManagedBean().getQuotationId());
		
		if (null != reportFieldBean.getPtjProfile()) {
			params.put(QtReportConst.PARAM_PROCURE_PTJ_EQ, reportFieldBean.getPtjProfile().getOrgCode());
		} else if (StringUtils.isNotEmpty(reportFieldBean.getScPtj())) {
			params.put(QtReportConst.PARAM_PROCURE_PTJ_EQ, reportFieldBean.getScPtj());
		}

		if (StringUtils.isNotEmpty(reportFieldBean.getScDeskOfficer())) {
			params.put(QtReportConst.PARAM_DESK_OFFCR_EQ, reportFieldBean.getScDeskOfficer());
		}

		return params;
	}
	
}
