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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.web.constant.report.QtReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.qt.TechnicalEvaluationCommitteeReportManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class TechnicalEvaluationCommitteeReportBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */

@ManagedBean(name = "qtTechnicalEvaluatonReportBB")
@ViewScoped
public class TechnicalEvaluationCommitteeReportBackingBean  extends
QtReportBackingBean<TechnicalEvaluationCommitteeReportManagedBean> implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(TechnicalEvaluationCommitteeReportBackingBean.class);
	
	
	public void generateReport(LinkedHashMap<String,String> params){
		log.info("TechnicalEvaluationCommitteeReportBackingBean generateReport executed");	
		
		log.info("TechnicalEvaluationCommitteeReportBackingBean generateReport ended");	
		generateSCReport(params);
	}
	
	//#25651 - Start
	/**
	 * Gets the source integration so.
	 * @return the getSourcingIntegrationSO
	 */
	private ISourcingIntegrationSO getSourcingIntegrationSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
	}
	//#25651 - End
	
	// #25651 - Start
	/*
	 * To validate quotation or tender number and reset search
	 */
	public void validateAndGetScReports() {

		String qtNumber = this.getManagedBean().getQuotationNumber();
		if (StringUtils.isNotEmpty(qtNumber)) {
			quotationNumberValidation();
			this.getScReports(qtNumber);
		} else {
			this.resetSearch();
		}

	}

	// #25651 - End

	//#25651 - Start
	/*
	 * getScReports - Gets the ScReports for opening committee.
	 */
    public void getScReports(String qtNumber) {

        boolean isAllReportExisting = false;
		List<ScQtFinalizationReport> scQtFinalizationReports;
		if (StringUtils.isNotEmpty(qtNumber) && qtNumber.startsWith(ReportConst.QT_VALUE)) {
			
			List<ScQtFinalizationReport> reports = getSourcingIntegrationSO().retriveFinalizationScReport(qtNumber,
					SourcingConstants.DOC_DEF_KEY_TECHNICAL_EVAALUATION_REPORT);
	
			if (CollectionUtils.isNotEmpty(reports)) {
				isAllReportExisting = true;
				scQtFinalizationReports = reports;
			} else {
				isAllReportExisting = false;
				scQtFinalizationReports = new ArrayList<>();
			}
	
		} else {
			isAllReportExisting = false;
			scQtFinalizationReports = new ArrayList<>();
		}
	
		this.getManagedBean().setAllReportExisting(isAllReportExisting);
		this.getManagedBean().setQtReportDocumentList(scQtFinalizationReports);
    }
	//#25651 - End
	
	public void resetSearch(){
		log.info("TechnicalEvaluationCommitteeReportBackingBean resetSearch executed");
		this.getManagedBean().setQuotationNumber(null);
		this.getManagedBean().setQuotationId(null);
		this.getManagedBean().setQtReportDocumentList(new ArrayList<ScQtFinalizationReport>()); //#25651
	}

	@Override
	public LinkedHashMap<String, String> getparams() {
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		//BigDecimal estimatedAmount=getEstimatedAmtByQtID(this.getManagedBean().getQuotationId());
		params.put(QtReportConst.QUOTATION_TENDER_ID, this.getManagedBean().getQuotationId());
		return params;
	}

}
