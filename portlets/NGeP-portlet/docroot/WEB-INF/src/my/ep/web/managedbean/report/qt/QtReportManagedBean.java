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

package my.ep.web.managedbean.report.qt;

import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.web.bean.report.ScReportFieldsSearchBean;
import my.ep.web.managedbean.report.ReportBaseManagedBean;

/**
 * The Class QtReportManagedBean.
 * 
 * @author:Garikapati Mangaiah.
 */


public abstract class QtReportManagedBean extends ReportBaseManagedBean {
	private static final long serialVersionUID = 1L;
	
	private String quotationNumber;
	private String quotationId;
	private ScReportFieldsSearchBean scReportSearchBean=null;
	private boolean isPtjEnable=Boolean.FALSE;
	private boolean isDeskOfficerEnable=Boolean.FALSE;
	//#21692 - Start
	private ScQt scQt;
	private PmParameter decisionType;
	private String quotationTitle;
	//#21692 - End
	/**
	 * @return the quotationNumner
	 */
	public String getQuotationNumber() {
		return quotationNumber;
	}
	/**
	 * @param quotationNumner the quotationNumner to set
	 */
	public void setQuotationNumber(String quotationNumber) {
		this.quotationNumber = quotationNumber;
	}
	/**
	 * @return the quotationId
	 */
	public String getQuotationId() {
		return quotationId;
	}
	/**
	 * @param quotationId the quotationId to set
	 */
	public void setQuotationId(String quotationId) {
		this.quotationId = quotationId;
	}
	/**
	 * @return the scReportSearchBean
	 */
	public ScReportFieldsSearchBean getScReportSearchBean() {
		return scReportSearchBean;
	}
	/**
	 * @param scReportSearchBean the scReportSearchBean to set
	 */
	public void setScReportSearchBean(ScReportFieldsSearchBean scReportSearchBean) {
		this.scReportSearchBean = scReportSearchBean;
	}
	/**
	 * @return the isPtjEnable
	 */
	public boolean isPtjEnable() {
		return isPtjEnable;
	}
	/**
	 * @param isPtjEnable the isPtjEnable to set
	 */
	public void setPtjEnable(boolean isPtjEnable) {
		this.isPtjEnable = isPtjEnable;
	}
	/**
	 * @return the isDeskOfficerEnable
	 */
	public boolean isDeskOfficerEnable() {
		return isDeskOfficerEnable;
	}
	/**
	 * @param isDeskOfficerEnable the isDeskOfficerEnable to set
	 */
	public void setDeskOfficerEnable(boolean isDeskOfficerEnable) {
		this.isDeskOfficerEnable = isDeskOfficerEnable;
	}
	
	//#21692 - Start
	/**
	 * @return the decisionType
	 */
	public PmParameter getDecisionType() {
		return decisionType;
	}
	/**
	 * @param decisionType the decisionType to set
	 */
	public void setDecisionType(PmParameter decisionType) {
		this.decisionType = decisionType;
	}
	//#21692 - End
	
	//#21692 - Start
	/**
	 * @return the quotationTitle
	 */
	public String getQuotationTitle() {
		return quotationTitle;
	}
	/**
	 * @param quotationTitle the quotationTitle to set
	 */
	public void setQuotationTitle(String quotationTitle) {
		this.quotationTitle = quotationTitle;
	}
	//#21692 - End
	
	//#21692 - Start
	/**
	 * @return the scQt
	 */
	public ScQt getScQt() {
		return scQt;
	}
	/**
	 * @param scQt the scQt to set
	 */
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}
	//#21692 - End
	

}
