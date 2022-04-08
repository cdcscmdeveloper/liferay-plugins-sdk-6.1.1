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

@ManagedBean(name="revenueCollectionRptBB")
@ViewScoped
public class RevenueCollectionRptBackingBean extends SmReportBackingBean<RevenueCollectionRptManagedBean>{
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(RevenueCollectionRptBackingBean.class);
	
	public RevenueCollectionRptBackingBean(){
		getManagedBean().setSelectedPaymentTypes(new ArrayList<String>());
		
		//Enhancement #19245
		getManagedBean().setCdcCollector(isRole(ReportConst.ROLE_CODE_COLLECTOR_CDC));
		getManagedBean().setSoftCert(SmReportConst.PAYMENT_SOFTC_CERT);
		
		setDateFromAndDateTo();
		//end of Enhancement #19245
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
			if(getManagedBean().getDefaultOutputType().equals("P")){
				params.put(ReportConst.PARAM_REPORT_OUTPUT_FORMAT, "pdf");
			}else{
				params.put(ReportConst.PARAM_REPORT_OUTPUT_FORMAT, "enhancedspreadsheet");
			}	
		}else{
			if(getManagedBean().getDocumentType().equals("P")){
				params.put(ReportConst.PARAM_REPORT_OUTPUT_FORMAT, "pdf");
			}else{
				params.put(ReportConst.PARAM_REPORT_OUTPUT_FORMAT, "enhancedspreadsheet");
			}			
			getManagedBean().setDefaultOutputType(getManagedBean().getDocumentType());
		}
		
		params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));

		//START : Enhancement #15990
		//If Document Type - Excel will by pass runJob from oracle report.
		if(getManagedBean().getDocumentType() == null){
			if("P".equals(getManagedBean().getDefaultOutputType())){
				generateSmReport(params);
			}else{
				try{
					generateSMReportInExcel(getManagedBean().getReportCode(), params);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}else{
			if("P".equals(getManagedBean().getDocumentType())){
				generateSmReport(params);
			}else{
				try{
					log.info("INSIDE ELSE [report_code] == "+getManagedBean().getReportCode());
					generateSMReportInExcel(getManagedBean().getReportCode(), params);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		//END : Enhancement #15990
	}
	
	public void resetSearch(){
		getManagedBean().setSelectedPaymentTypes(new ArrayList<String>());
		
		//Enhancement #19245
		setDateFromAndDateTo();
		//end of Enhancement #19245
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
