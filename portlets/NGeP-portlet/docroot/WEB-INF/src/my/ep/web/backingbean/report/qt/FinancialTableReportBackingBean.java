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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.factory.EpLogger;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.bean.report.ScReportFieldsSearchBean;
import my.ep.web.constant.report.QtReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.qt.FinancialTableReportManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.SchemaField;

/**
 * The Class FinancialTableReportBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */

@ManagedBean(name = "qtFinancialTableReportBB")
@ViewScoped
public class FinancialTableReportBackingBean extends
QtReportBackingBean<FinancialTableReportManagedBean> implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(FinancialTableReportBackingBean.class);
	private String strLanguage = ResourceBundleUtils.getMessage(
			ReportConst.LABEL_LANG, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
			new FacesScopes().getUserLocale());
	
	public FinancialTableReportBackingBean(){
		
	}
	
	@PostConstruct
	public void init(){
		log.info("=====init executed FinancialTableReportBackingBean====");
		ScReportFieldsSearchBean reportFileSearchBean=this.getManagedBean().getScReportSearchBean();
		if(null==reportFileSearchBean){
			log.info("=====init  FinancialTableReportBackingBean null value====");
			reportFileSearchBean=new ScReportFieldsSearchBean();
			reportFileSearchBean=getDefaultValuesFromAndToDate(reportFileSearchBean);
			this.getManagedBean().setScReportSearchBean(reportFileSearchBean);
		}
	}
	
	@Override
	public void generateReport(LinkedHashMap<String, String> params) {
		log.info("=====FinancialTableReportBackingBean generateReport====");
		for(Entry<String, String> entry:params.entrySet()){
			 System.out.println(entry.getKey() + "/" + entry.getValue());
		}
		generateSCReport(params);
	}
	@Override
	public void resetSearch() {
		// TODO Auto-generated method stub
		this.getManagedBean().setQuotationNumber(null);
		this.getManagedBean().setQuotationId(null);
		ScReportFieldsSearchBean reportFileSearchBean=this.getManagedBean().getScReportSearchBean();
		reportFileSearchBean=new ScReportFieldsSearchBean();
		reportFileSearchBean=getDefaultValuesFromAndToDate(reportFileSearchBean);
		this.getManagedBean().setScReportSearchBean(reportFileSearchBean);
		
	}
	@Override
	public LinkedHashMap<String, String> getparams() {
		LinkedHashMap<String, String> params=new LinkedHashMap<>();
		ScReportFieldsSearchBean reportFieldBean=this.getManagedBean().getScReportSearchBean();
		//Disable #29580
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
			params.put(QtReportConst.QUOTATION_TENDER_ID, this.getManagedBean().getQuotationId());
		}
		//Disable #29580
		if(StringUtils.isNotEmpty(reportFieldBean.getScDeskOfficer())){
			params.put(QtReportConst.PARAM_DESK_OFFCR_EQ, reportFieldBean.getScDeskOfficer());	
		}
		if(StringUtils.isNotEmpty(reportFieldBean.getScPtj())){
			params.put(QtReportConst.PARAM_PROCURE_PTJ_EQ, reportFieldBean.getScPtj());	
		}
		//#29580 -start
		if (StringUtils.isNotEmpty(this.getManagedBean().getQuotationTitle())) { 
			params.put(QtReportConst.QUOTATION_TENDER_TITLE, this.getManagedBean().getQuotationTitle());
		}
		//#29580 -end
		return params;
	}

	




}
