package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import my.ep.constants.CommonConstants;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.RevenueCollectionRptManagedBean;
import my.ep.web.util.pm.helper.PmCommonUtil;
//recommit
@ManagedBean(name="revenueCollectionRptBB")
@ViewScoped
public class RevenueCollectionRptBackingBean extends SmReportBackingBean<RevenueCollectionRptManagedBean>{
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(RevenueCollectionRptBackingBean.class);
	
	public RevenueCollectionRptBackingBean(){
		getManagedBean().setSelectedPaymentTypes(new ArrayList<String>());
		setDefaultDateFromAndDateTo();
		getManagedBean().setCdcCollector(isRole(ReportConst.ROLE_CODE_COLLECTOR_CDC));
		getManagedBean().setSoftCert(SmReportConst.PAYMENT_SOFTC_CERT);
	}
	
	public void generateReport(){
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		retrieveReportValueFromDb();
		addSelectedPaymentTypeParam(params);
/*		for(String paymentType : getManagedBean().getSelectedPaymentTypes()){
			if(paymentType.equalsIgnoreCase(SmReportConst.PAYMENT_MOF)){
				params.put(SmReportConst.PARAM_PAYMENT_TYPE1, paymentType);
			}else if(paymentType.equalsIgnoreCase(SmReportConst.PAYMENT_PROCESSING)){
				params.put(SmReportConst.PARAM_PAYMENT_TYPE2, paymentType);
			}else if(paymentType.equalsIgnoreCase(SmReportConst.PAYMENT_SOFTC_CERT)){
				params.put(SmReportConst.PARAM_PAYMENT_TYPE3, paymentType);
			}
		}*/

		if(getManagedBean().getDocumentType() == null){
			params.put(ReportConst.ENHANCED_SPREAD_SHEET, getManagedBean().getDefaultOutputType());
		}else{
			params.put(ReportConst.ENHANCED_SPREAD_SHEET, getManagedBean().getDocumentType());
			getManagedBean().setDefaultOutputType(getManagedBean().getDocumentType());
		}
		
		params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));

		generateSmReport(params);
	}
	
	public void resetSearch(){
		getManagedBean().setSelectedPaymentTypes(new ArrayList<String>());
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
