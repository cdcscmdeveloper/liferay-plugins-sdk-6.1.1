/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.report.qt;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.factory.EpLogger;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.bean.report.ScReportFieldsSearchBean;
import my.ep.web.constant.report.QtReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.qt.LetterOfAcceptanceReportManagedBean;
import my.ep.web.util.FacesScopes;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


/**
 * The Class LetterOfAcceptanceReportBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */

@ManagedBean(name = "qtLetterOfAcceptanceReportBB")
@ViewScoped
public class LetterOfAcceptanceReportBackingBean extends
QtReportBackingBean< LetterOfAcceptanceReportManagedBean> implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(LetterOfAcceptanceReportBackingBean.class);
	private String strLanguage = ResourceBundleUtils.getMessage(
			ReportConst.LABEL_LANG, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
			new FacesScopes().getUserLocale());
	
	public LetterOfAcceptanceReportBackingBean(){
		
	}
	
	@PostConstruct
	public void init(){
		log.info("=====init executed  LetterOfAcceptanceReportBackingBean====");
		ScReportFieldsSearchBean reportFileSearchBean=this.getManagedBean().getScReportSearchBean();
		if(null==reportFileSearchBean){
			log.info("=====init  LetterOfAcceptanceReportBackingBean null value====");
			reportFileSearchBean=new ScReportFieldsSearchBean();
			reportFileSearchBean=getDefaultValuesFromAndToDate(reportFileSearchBean);
			this.getManagedBean().setScReportSearchBean(reportFileSearchBean);
		}
	}

	@Override
	public void generateReport(LinkedHashMap<String, String> params) {
		log.info("===== LetterOfAcceptanceReportBackingBean generateReport====");
		for(Entry<String, String> entry:params.entrySet()){
			 System.out.println(entry.getKey() + "/" + entry.getValue());
		}
		generateSCReport(params);
		
	}

	@Override
	public void resetSearch() {
		this.getManagedBean().setQuotationNumber(null);
		this.getManagedBean().setQuotationId(null);
		ScReportFieldsSearchBean reportFileSearchBean=this.getManagedBean().getScReportSearchBean();
		reportFileSearchBean=new ScReportFieldsSearchBean();
		reportFileSearchBean=getDefaultValuesFromAndToDate(reportFileSearchBean);
		this.getManagedBean().setScReportSearchBean(reportFileSearchBean);
		
	}

	@Override
	public LinkedHashMap<String, String> getparams() {
		LinkedHashMap<String, String> params=new LinkedHashMap<String,String>();
		ScReportFieldsSearchBean reportFieldBean=this.getManagedBean().getScReportSearchBean();
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
		/*if (StringUtils.isNotEmpty(this.getManagedBean().getQuotationNumner())) {
			//quotationNumberValidation();
			params.put(QtReportConst.QUOTATION_TENDER_ID, this.getManagedBean().getQuotationId());
		}*/
		// 29580 - Start (Harun) Change option from single status to multiple 
		/*if (StringUtils.isNotEmpty(reportFieldBean.getScStatus())) {
			params.put(QtReportConst.PARAM_PROCURE_STATUS_EQ, reportFieldBean.getScStatus());
		}*/
		if(CollectionUtils.isNotEmpty(reportFieldBean.getScStatusManyMenu())){
			List<String> conversionList = new ArrayList<>();
			
			for (String strManyMenu: reportFieldBean.getScStatusManyMenu()){
				conversionList.add(getConversionParam(strManyMenu));
			}
			String status = StringUtils.join(conversionList,",");
			params.put(QtReportConst.PARAM_PROCURE_STATUS_EQ, status.replaceAll(",$",""));
		}
		// 29580 - End (Harun)
		if(StringUtils.isNotEmpty(reportFieldBean.getScDeskOfficer())){
			params.put(QtReportConst.PARAM_DESK_OFFCR_EQ, reportFieldBean.getScDeskOfficer());	
		}
		if(StringUtils.isNotEmpty(reportFieldBean.getScPtj())){
			params.put(QtReportConst.PARAM_PROCURE_PTJ_EQ, reportFieldBean.getScPtj());	
		}
		return params;
	}
	
	// 29580 - Start (Harun) Change option from single status to multiple 
	public String getConversionParam(String status) {
		
		HashMap<String, String> status_map = new HashMap<String, String>();
		status_map.put(getStatusDesc(QtReportConst.LABEL_ALL), QtReportConst.STATUS_ALL);
		status_map.put(getStatusDesc(QtReportConst.LABEL_PREPARATION), QtReportConst.STATUS_PREPARATION);
		status_map.put(getStatusDesc(QtReportConst.LABEL_PUBLISHING), QtReportConst.STATUS_PUBLISHING);
		status_map.put(getStatusDesc(QtReportConst.LABEL_PENDING_EVALUATION), QtReportConst.STATUS_PENDING_EVALUATION);
		status_map.put(getStatusDesc(QtReportConst.LABEL_PENDING_SUPPLIER_FINALIZATION), QtReportConst.STATUS_PENDING_SUPPLIER_FINALIZATION);
		status_map.put(getStatusDesc(QtReportConst.LABEL_PENDING_LOILOA), QtReportConst.STATUS_PENDING_LOILOA);
		status_map.put(getStatusDesc(QtReportConst.LABEL_LOA_COMPLETED), QtReportConst.STATUS_LOA_COMPLETED);
		status_map.put(getStatusDesc(QtReportConst.LABEL_CANCELLED), QtReportConst.STATUS_CANCELLED);
		
		return status_map.get(status);
				
	}
	
	public String getStatusDesc(String label) {
		return ResourceBundleUtils.getMessage(label,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
	}
	// 29580 - End (Harun)
	

}
