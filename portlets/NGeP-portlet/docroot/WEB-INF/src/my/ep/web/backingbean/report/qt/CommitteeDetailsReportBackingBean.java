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
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.factory.EpLogger;
import my.ep.web.constant.report.QtReportConst;
import my.ep.web.managedbean.report.qt.CommitteeDetailsReportManagedBean;

import org.apache.log4j.Logger;

/**
 * The Class CommitteeDetailsReportBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */

@ManagedBean(name = "qtCommitteeDetailsReportBB")
@ViewScoped
public class CommitteeDetailsReportBackingBean extends
QtReportBackingBean<CommitteeDetailsReportManagedBean> implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(CommitteeDetailsReportBackingBean.class);
	
	
	public void generateReport(LinkedHashMap<String,String> params){
		log.info("CommitteeDetailsReportBackingBean generateReport executed");	
		
		
		generateSCReport(params);
	}
	
	public void resetSearch(){
		log.info("CommitteeDetailsReportBackingBean resetSearch executed");
		this.getManagedBean().setQuotationNumber(null);
		this.getManagedBean().setQuotationId(null);
	}

	@Override
	public LinkedHashMap<String, String> getparams() {
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		//BigDecimal estimatedAmount=getEstimatedAmtByQtID(this.getManagedBean().getQuotationId());
		params.put(QtReportConst.QUOTATION_TENDER_ID, this.getManagedBean().getQuotationId());
		return params;
	}

}
