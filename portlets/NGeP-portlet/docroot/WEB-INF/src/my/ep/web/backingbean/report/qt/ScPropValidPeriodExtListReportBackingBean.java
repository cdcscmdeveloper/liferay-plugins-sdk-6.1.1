package my.ep.web.backingbean.report.qt;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.web.bean.report.ScReportFieldsSearchBean;
import my.ep.web.constant.report.QtReportConst;
import my.ep.web.managedbean.report.qt.ScPropValidPeriodExtListReportManagedBean;

import org.apache.commons.lang3.StringUtils;

@ManagedBean(name = "scPropValidPeriodExtListReportBackingBean")
@ViewScoped
public class ScPropValidPeriodExtListReportBackingBean extends QtReportBackingBean<ScPropValidPeriodExtListReportManagedBean>
		implements Serializable {
	private static final long serialVersionUID = 1L;

//	private static final Logger log = EpLogger.getLogger(ScPropValidPeriodExtReportBackingBean.class);

	@PostConstruct
	public void init() {
		this.getManagedBean().setReportCode(QtReportConst.SC_PROP_VALID_PERIOD_EXT_LIST_RPT_VAL);
		
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
		
		if (null != this.getManagedBean().getQuotationId()) {
			params.put(QtReportConst.QUOTATION_TENDER_ID, this.getManagedBean().getQuotationId());
		}
		if (StringUtils.isNotEmpty(reportFieldBean.getScStatus())) {
			params.put(QtReportConst.PARAM_DECISION_TYPE, reportFieldBean.getScStatus());
		}
		
		if (null != reportFieldBean.getPtjProfile()) {
			params.put(QtReportConst.PARAM_PROCURE_PTJ_EQ, reportFieldBean.getPtjProfile().getOrgProfileId().toString());
		} else if (StringUtils.isNotEmpty(reportFieldBean.getScPtj())) {
			params.put(QtReportConst.PARAM_PROCURE_PTJ_EQ, reportFieldBean.getScPtj());
		}

		if (StringUtils.isNotEmpty(reportFieldBean.getScDeskOfficer())) {
			params.put(QtReportConst.PARAM_DESK_OFFCR_EQ, reportFieldBean.getScDeskOfficer());
		}
		//Send title with all Uppercase
		if (StringUtils.isNotEmpty(this.getManagedBean().getQuotationTitle())) {
			params.put(QtReportConst.QUOTATION_TENDER_TITLE, this.getManagedBean().getQuotationTitle().toUpperCase());
		}

		return params;
	}
	
	/**
	 * Html error will appear if qt title AND number is not keyed in.
	 * 
	 * @return
	 */
	public boolean showErrorWhenTitleAndNumberNull() {
		ScReportFieldsSearchBean reportFieldBean = this.getManagedBean().getScReportSearchBean();
		return this.getManagedBean().getQuotationNumber() == null && StringUtils.isEmpty(this.getManagedBean().getQuotationTitle())
				&& null == reportFieldBean.getPtjProfile() && StringUtils.isEmpty(reportFieldBean.getScStatus());
	}
	
	/**
	 * Clear all Tender Title field, this enable error message to show up when user delete Tender Title
	 */
	public void clearTenderTitle() {
		if (StringUtils.isEmpty(this.getManagedBean().getQuotationTitle()) && this.getManagedBean().getQuotationId() == null) {
			this.getManagedBean().setQuotationNumber(null);
			this.getManagedBean().setQuotationTitle(null);
		}
	}
	
	/**
	 * Clear all Decision Status field, this enable error message to show up when user delete Tender Title
	 */
	public void clearDecisionStatus() {
		if (StringUtils.isEmpty(this.getManagedBean().getScReportSearchBean().getScStatus()) && this.getManagedBean().getQuotationId() == null) {
			this.getManagedBean().setQuotationNumber(null);
			this.getManagedBean().getScReportSearchBean().setScStatus(null);
		}
	}
	
}
