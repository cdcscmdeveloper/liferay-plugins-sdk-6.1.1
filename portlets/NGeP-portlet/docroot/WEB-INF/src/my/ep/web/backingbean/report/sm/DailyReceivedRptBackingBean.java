package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.DailyReceivedRptManagedBean;
import my.ep.web.util.pm.helper.PmCommonUtil;

@ManagedBean(name = "dailyReceivedRptBB")
@ViewScoped
public class DailyReceivedRptBackingBean extends
		SmReportBackingBean<DailyReceivedRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8499640157757275310L;
	private static Logger log = Logger.getLogger(RevenueCollectionRptBackingBean.class);

	public DailyReceivedRptBackingBean() {
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
		params.put(SmReportConst.PARAM_PAYMENT_TYPE, getManagedBean()
				.getPaymentType());
		generateSmReport(params);
		
		if(getManagedBean().getDocumentType() == null){
			params.put(ReportConst.ENHANCED_SPREAD_SHEET, getManagedBean().getDefaultOutputType());
		}else{
			params.put(ReportConst.ENHANCED_SPREAD_SHEET, getManagedBean().getDocumentType());
			getManagedBean().setDefaultOutputType(getManagedBean().getDocumentType());
		}
	}

	public void resetSearch() {
		getManagedBean().setPaymentType(StringUtils.EMPTY);
		setDefaultDateFromAndDateTo();
	}

	/**
	 * document type options
	 * @return SelectItem[]
	 */
	public static SelectItem[] getDocumentTypeOptions() {
		log.info("getReportTypeOptions");
		return PmCommonUtil.getDocumentTypeOption();
	}
}
