package my.ep.web.backingbean.report.qt;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.factory.EpLogger;
import my.ep.web.bean.report.ScReportFieldsSearchBean;
import my.ep.web.constant.report.QtReportConst;
import my.ep.web.managedbean.report.qt.BSVReportManagedBean;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
/**
 * The Class BSVReportBackingBean.
 * 
 * @author Venkat Chanumolu.
 */
@ManagedBean(name = "qtBsvReportBB")
@ViewScoped
public class BSVReportBackingBean extends QtReportBackingBean<BSVReportManagedBean> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(BSVReportBackingBean.class);
	
	@PostConstruct
	public void init(){
		log.info("=====init executed for BSVReportBackingBean====");
		ScReportFieldsSearchBean reportFileSearchBean = this.getManagedBean().getScReportSearchBean();
		if(null==reportFileSearchBean){
			log.info("=====init  BSVReportBackingBean null value====");
			reportFileSearchBean = new ScReportFieldsSearchBean();
			reportFileSearchBean = getDefaultValuesFromAndToDate(reportFileSearchBean);
			this.getManagedBean().setScReportSearchBean(reportFileSearchBean);
		}
	}

	@Override
	public void generateReport(LinkedHashMap<String, String> params) {
		log.info("BSVReportBackingBean generateReport executed");	
		System.out.println("params"+params.size());
		generateSCReport(params);
	}

	@Override
	public void resetSearch() {
		log.info("BSVReportBackingBean resetSearch executed");
		this.getManagedBean().setQuotationNumber(null);
		this.getManagedBean().setQuotationId(null);
		ScReportFieldsSearchBean reportFileSearchBean=this.getManagedBean().getScReportSearchBean();
		reportFileSearchBean=new ScReportFieldsSearchBean();
		reportFileSearchBean=getDefaultValuesFromAndToDate(reportFileSearchBean);
		this.getManagedBean().setScReportSearchBean(reportFileSearchBean);
	}

	@Override
	public LinkedHashMap<String, String> getparams() {
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		ScReportFieldsSearchBean reportFieldBean=this.getManagedBean().getScReportSearchBean();
		
		StringBuilder procureMethod = new StringBuilder();
		if ( null!=reportFieldBean.getAllCheckboxValue() && reportFieldBean.getAllCheckboxValue().length > 0) {
			 params.put(QtReportConst.PARAM_PROCURE_METHOD_EQ, returnProcurementValue(reportFieldBean.getAllCheckboxValue()));
		} else {
			if (null!=reportFieldBean.getQuotationCheckboxValue()  && reportFieldBean.getQuotationCheckboxValue().length > 0) {
				procureMethod.append(returnProcurementValue(reportFieldBean.getQuotationCheckboxValue()));
			}
			if (null!=reportFieldBean.getTenderCheckboxValue() && reportFieldBean.getTenderCheckboxValue().length > 0) {
				procureMethod.append(returnProcurementValue(reportFieldBean.getTenderCheckboxValue()));
			}
			if (null!=reportFieldBean.getRestrictedTndrCheckboxValue() && reportFieldBean.getRestrictedTndrCheckboxValue().length > 0) {
				procureMethod.append(returnProcurementValue(reportFieldBean.getRestrictedTndrCheckboxValue()));
			}
			if ((procureMethod.length() > 0)) {		
				 params.put(QtReportConst.PARAM_PROCURE_METHOD_EQ, procureMethod.toString());
			}
		}
		
		if (StringUtils.isNotEmpty(this.getManagedBean().getQuotationNumber())) {
			//quotationNumberValidation();
			params.put(QtReportConst.QUOTATION_TENDER_ID, this.getManagedBean().getQuotationId());
		}
		if (StringUtils.isNotEmpty(reportFieldBean.getScStatus())) {
			params.put(QtReportConst.PARAM_PROCURE_STATUS_EQ, reportFieldBean.getScStatus());
		}
		
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
				QtReportConst.URL_RPT_DATE_ANOTHER_FORMAT);
		if (StringUtils.isNotEmpty(reportFieldBean.getScCreatedToMonth())
				&& StringUtils.isNotEmpty(reportFieldBean
						.getScCreatedFromMonth())) {
			StringBuilder createFromDate=new StringBuilder();
			createFromDate.append(reportFieldBean
						.getScCreatedFromMonth()).append(reportFieldBean.getScCreatedFromYear());
			params.put(QtReportConst.PARAM_CREAT_FRM_DT_EQ, createFromDate.toString());
			log.info("createFromDate"+createFromDate.toString());
			StringBuilder createToDate=new StringBuilder();
			createToDate.append(reportFieldBean
						.getScCreatedToMonth()).append(reportFieldBean.getScCreatedToYear());
			params.put(QtReportConst.PARAM_CREAT_TO_DT_EQ, createToDate.toString());
			log.info("createToDate"+createToDate.toString());
			
		} else {
			params.put(QtReportConst.PARAM_CREAT_FRM_DT_EQ, reportFieldBean.getScCreatedFromYear());
			params.put(QtReportConst.PARAM_CREAT_TO_DT_EQ, reportFieldBean.getScCreatedToYear());
			
		}
		
		if(null!=reportFieldBean.getScPublishFromDate() && reportFieldBean.getScPublishFromDate().toString() != QtReportConst.EMPTY_VAL){
			params.put(QtReportConst.PARAM_PUB_FRM_DT_EQ,  outputDateFormat.format(reportFieldBean.getScPublishFromDate()));	
		}
       if(null!=reportFieldBean.getScPublishToDate() && reportFieldBean.getScPublishToDate().toString() != QtReportConst.EMPTY_VAL){
    	   params.put(QtReportConst.PARAM_PUB_TO_DT_EQ, outputDateFormat.format(reportFieldBean.getScPublishToDate()));	
		}
		if(StringUtils.isNotEmpty(reportFieldBean.getScDeskOfficer())){
			params.put(QtReportConst.PARAM_DESK_OFFCR_EQ, reportFieldBean.getScDeskOfficer());	
		}
		if(StringUtils.isNotEmpty(reportFieldBean.getScPtj())){
			params.put(QtReportConst.PARAM_PROCURE_PTJ_EQ, reportFieldBean.getScPtj());	
		}
		return params;
	}

}
