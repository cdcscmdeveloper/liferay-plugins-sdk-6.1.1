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
package my.ep.web.managedbean.report.dp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class LetterOfAcceptanceContentRptManagedBean.
 *
 * @author Venkat Chanumolu
 * @since 08.06.2017
 */
@ManagedBean(name="loaContentRptManagedBean")
@SessionScoped
public class LetterOfAcceptanceContentRptManagedBean extends DpReportManagedBean{
	private static final long serialVersionUID = 1L;
	
	/** The sc loa. */
	private ScLoa scLoa;
	/** The loa report document list. */
	private List<ScLoaReport> loaReportDocumentList;
	/** The sc qt. */
	private ScQt scQt;
	
	/** The supp loi loa map. */
	private Map<Long, List<ScLoiLoa>> suppLoiLoaMap;
	/**
	 * @return the scLoa
	 */
	public ScLoa getScLoa() {
		return scLoa;
	}
	/**
	 * @param scLoa the scLoa to set
	 */
	public void setScLoa(ScLoa scLoa) {
		this.scLoa = scLoa;
	}
	/**
	 * @return the loaReportDocumentList
	 */
	public List<ScLoaReport> getLoaReportDocumentList() {
		return loaReportDocumentList;
	}
	/**
	 * @param loaReportDocumentList the loaReportDocumentList to set
	 */
	public void setLoaReportDocumentList(List<ScLoaReport> loaReportDocumentList) {
		this.loaReportDocumentList = loaReportDocumentList;
	}
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
	/**
	 * @return the suppLoiLoaMap
	 */
	public Map<Long, List<ScLoiLoa>> getSuppLoiLoaMap() {
		return suppLoiLoaMap;
	}
	/**
	 * @param suppLoiLoaMap the suppLoiLoaMap to set
	 */
	public void setSuppLoiLoaMap(Map<Long, List<ScLoiLoa>> suppLoiLoaMap) {
		this.suppLoiLoaMap = suppLoiLoaMap;
	}
	
	
	
}
