/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.doc;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.VFlDocument;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Fulfillment document
 * 
 * @author Giribabu
 * @since 2/11/2013
 */
@ManagedBean(name = "flDocumentManagedBean")
@SessionScoped
public class FlDocumentManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private String tempLoginId;
	private String tempDocType;
	private String searchType = "D"; // Define or Advanced
	private String searchOrderType; 
	private String searchDocType;
	private String searchDocNo;
	private String searchContractNo;
	private String searchCircularNo;
	private String searchLoaNo;
	private Date searchStartDate;
	private Date searchEndDate;
	private String searchProcurementType;
	private Long searchStatusId; 
	private FlFulfilmentRequest fulfilmentRequest;
	
	private List<VFlDocument> flDocumentList;
	
	private Boolean checkDocType =false;
	private Boolean checkPoDocType =false;

	public String getTempLoginId() {
		return tempLoginId;
	}

	public void setTempLoginId(String tempLoginId) {
		this.tempLoginId = tempLoginId;
	}

	public String getTempDocType() {
		return tempDocType;
	}

	public void setTempDocType(String tempDocType) {
		this.tempDocType = tempDocType;
	}

	public List<VFlDocument> getFlDocumentList() {
		return flDocumentList;
	}

	public void setFlDocumentList(List<VFlDocument> flDocumentList) {
		this.flDocumentList = flDocumentList;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getSearchOrderType() {
		return searchOrderType;
	}

	public void setSearchOrderType(String searchOrderType) {
		this.searchOrderType = searchOrderType;
	}

	public String getSearchDocType() {
		return searchDocType;
	}

	public void setSearchDocType(String searchDocType) {
		this.searchDocType = searchDocType;
	}

	public String getSearchDocNo() {
		return searchDocNo;
	}

	public void setSearchDocNo(String searchDocNo) {
		this.searchDocNo = searchDocNo;
	}

	public String getSearchContractNo() {
		return searchContractNo;
	}

	public void setSearchContractNo(String searchContractNo) {
		this.searchContractNo = searchContractNo;
	}

	public String getSearchCircularNo() {
		return searchCircularNo;
	}

	public void setSearchCircularNo(String searchCircularNo) {
		this.searchCircularNo = searchCircularNo;
	}

	public String getSearchLoaNo() {
		return searchLoaNo;
	}

	public void setSearchLoaNo(String searchLoaNo) {
		this.searchLoaNo = searchLoaNo;
	}

	public Date getSearchStartDate() {
		return searchStartDate;
	}

	public void setSearchStartDate(Date searchStartDate) {
		this.searchStartDate = searchStartDate;
	}

	public Date getSearchEndDate() {
		return searchEndDate;
	}

	public void setSearchEndDate(Date searchEndDate) {
		this.searchEndDate = searchEndDate;
	}

	public Long getSearchStatusId() {
		return searchStatusId;
	}

	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
	}

	public String getSearchProcurementType() {
		return searchProcurementType;
	}

	public void setSearchProcurementType(String searchProcurementType) {
		this.searchProcurementType = searchProcurementType;
	}

	/**
	 * @return the fulfilmentRequest
	 */
	public FlFulfilmentRequest getFulfilmentRequest() {
		return fulfilmentRequest;
	}

	/**
	 * @param fulfilmentRequest the fulfilmentRequest to set
	 */
	public void setFulfilmentRequest(FlFulfilmentRequest fulfilmentRequest) {
		this.fulfilmentRequest = fulfilmentRequest;
	}

	/**
	 * @return the checkDocType
	 */
	public Boolean getCheckDocType() {
		return checkDocType;
	}

	/**
	 * @param checkDocType the checkDocType to set
	 */
	public void setCheckDocType(Boolean checkDocType) {
		this.checkDocType = checkDocType;
	}

	/**
	 * @return the checkPoDocType
	 */
	public Boolean getCheckPoDocType() {
		return checkPoDocType;
	}

	/**
	 * @param checkPoDocType the checkPoDocType to set
	 */
	public void setCheckPoDocType(Boolean checkPoDocType) {
		this.checkPoDocType = checkPoDocType;
	}
	
}
