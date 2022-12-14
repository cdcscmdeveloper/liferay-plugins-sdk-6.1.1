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
import java.math.BigDecimal;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.factory.EpLogger;
import my.ep.web.constant.report.QtReportConst;
import my.ep.web.managedbean.report.qt.FinancialEvaluationCommitteeReportManagedBean;

import org.apache.log4j.Logger;

/**
 * The Class FinancialEvaluationCommitteeReportBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */

@ManagedBean(name = "qtFinancialEvaluationCommitteeReportBB")
@ViewScoped
public class FinancialEvaluationCommitteeReportBackingBean extends
QtReportBackingBean<FinancialEvaluationCommitteeReportManagedBean> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(FinancialEvaluationCommitteeReportBackingBean.class);
	
	
	public void generateReport(LinkedHashMap<String,String> params){
		log.info("FinancialEvaluationCommitteeReportBackingBean generateReport executed");
		
		//retrieveReportValueFromDb();
		log.info("generate reportCode"+getManagedBean()
				.getReportCode());
		generateSCReport(params);
	}
	
	public void resetSearch(){
		log.info("FinancialEvaluationCommitteeReportBackingBean resetSearch executed");
		this.getManagedBean().setQuotationNumber(null);
		this.getManagedBean().setQuotationId(null);
	}

	@Override
	public LinkedHashMap<String, String> getparams() {
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		BigDecimal estimatedAmount=getEstimatedAmtByQtID(this.getManagedBean().getQuotationId());
		params.put(QtReportConst.QUOTATION_TENDER_ID, this.getManagedBean().getQuotationId());
		params.put(QtReportConst.QUOTATION_TENDER_ESTIMATED_AMOUNT, estimatedAmount != null ? estimatedAmount
				.toString() : "");
		return params;
	}

}
