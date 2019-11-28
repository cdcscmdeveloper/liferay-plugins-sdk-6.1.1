package my.ep.web.backingbean.report.qt;

import java.io.Serializable;
import java.util.LinkedHashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.factory.EpLogger;
import my.ep.web.bean.report.ScReportFieldsSearchBean;
import my.ep.web.constant.report.QtReportConst;
import my.ep.web.managedbean.report.qt.ScPropValidPeriodExtReportManagedBean;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "scPropValidPeriodExtReportBackingBean")
@ViewScoped
public class ScPropValidPeriodExtReportBackingBean extends QtReportBackingBean<ScPropValidPeriodExtReportManagedBean>
		implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger.getLogger(ScPropValidPeriodExtReportBackingBean.class);

	@PostConstruct
	public void init() {
		log.info("=====init executed for ScPropValidPeriodExtReportBackingBean====");
		this.getManagedBean().setReportCode(QtReportConst.SC_PROP_VALID_PERIOD_EXT_RPT_VAL);
		ScReportFieldsSearchBean reportFileSearchBean = this.getManagedBean().getScReportSearchBean();
		if (null == reportFileSearchBean) {
			log.info("=====init  ScPropValidPeriodExtReportBackingBean null value====");
			reportFileSearchBean = new ScReportFieldsSearchBean();
			reportFileSearchBean = getDefaultValuesFromAndToDate(reportFileSearchBean);
			this.getManagedBean().setScReportSearchBean(reportFileSearchBean);
		}
	}

	@Override
	public void generateReport(LinkedHashMap<String, String> params) {
		log.info("ScPropValidPeriodExtReportBackingBean generateReport executed");
		generateSCReport(params);

		log.info("ScPropValidPeriodExtReportBackingBean generateReport ended");
	}

	@Override
	public void resetSearch() {
		log.info("ScPropValidPeriodExtReportBackingBean resetSearch executed");
		resetSearchBB();
	}

	@Override
	public LinkedHashMap<String, String> getparams() {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		ScReportFieldsSearchBean reportFieldBean = this.getManagedBean().getScReportSearchBean();

		params.put(QtReportConst.QUOTATION_TENDER_ID, this.getManagedBean().getQuotationId());
		params.put(QtReportConst.PARAM_SUBMIT_FR_DT, reportFieldBean.getScPublishFromDate().toString());
		params.put(QtReportConst.PARAM_SUBMIT_TO_DT, reportFieldBean.getScPublishToDate().toString());
		
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
