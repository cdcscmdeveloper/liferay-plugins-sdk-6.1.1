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
import my.ep.web.managedbean.report.qt.BriefingPaperReportManagedBean;

import org.apache.log4j.Logger;

/**
 * The Class BriefingPaperReportBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */

@ManagedBean(name = "qtBriefingReportBB")
@ViewScoped
public  class BriefingPaperReportBackingBean extends
QtReportBackingBean<BriefingPaperReportManagedBean> implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(BriefingPaperReportBackingBean.class);
	
	
	
	public void generateReport(LinkedHashMap<String, String> params){
		log.info("BriefingPaperReportBackingBean generateReport executed");	
		System.out.println("params"+params.size());
		
		//retrieveReportValueFromDb();
		
		
		generateSCReport(params);
	}
	
	public void resetSearch(){
		log.info("BriefingPaperReportBackingBean resetSearch executed");
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
