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
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
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
	private String searchTitle;
	private String searchSuppName;
	private String searchSuppDoRef;
	private String searchSuppInvRef;
	private Long searchStatusId;
	private Date searchContractFromDuration;
	private Date searchContractToDuration;
	
	private List<PmStatusDesc> docStatusList;
	private List<PmParameterDesc> procurementTypeList;
	private Long statusDescId;
	private Long searchProcurementTypeId;
	
	
	private FlFulfilmentRequest fulfilmentRequest;
	private PmUser pmUser;
	
	private List<VFlDocument> flDocumentList;
	
	private Boolean checkDocType =false;
	private Boolean checkPoDocType =false;
	private Boolean checkPaDocType =false;
	private Boolean checkTitle =false;
	private Boolean  checkDoInDocType=false;
	
	// getUserGroup
	private String fulfilmentRequisitioner;

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

	public Long getSearchStatusId() {
		return searchStatusId;
	}

	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
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

	/**
	 * @return the checkPaDocType
	 */
	public Boolean getCheckPaDocType() {
		return checkPaDocType;
	}

	/**
	 * @param checkPaDocType the checkPaDocType to set
	 */
	public void setCheckPaDocType(Boolean checkPaDocType) {
		this.checkPaDocType = checkPaDocType;
	}

	/**
	 * @return the checkTitle
	 */
	public Boolean getCheckTitle() {
		return checkTitle;
	}

	/**
	 * @param checkTitle the checkTitle to set
	 */
	public void setCheckTitle(Boolean checkTitle) {
		this.checkTitle = checkTitle;
	}

	/**
	 */
	public String getFulfilmentRequisitioner() {
		return fulfilmentRequisitioner;
	}

	/**
	 * @param fulfilmentRequisitioner the fulfilmentRequisitioner to set
	 */
	public void setFulfilmentRequisitioner(String fulfilmentRequisitioner) {
		this.fulfilmentRequisitioner = fulfilmentRequisitioner;
	}

	/**
	 * @return the searchContractToDuration
	 */
	public Date getSearchContractToDuration() {
		return searchContractToDuration;
	}

	/**
	 * @param searchContractToDuration the searchContractToDuration to set
	 */
	public void setSearchContractToDuration(Date searchContractToDuration) {
		this.searchContractToDuration = searchContractToDuration;
	}

	/**
	 * @return the searchContractFromDuration
	 */
	public Date getSearchContractFromDuration() {
		return searchContractFromDuration;
	}

	/**
	 * @param searchContractFromDuration the searchContractFromDuration to set
	 */
	public void setSearchContractFromDuration(Date searchContractFromDuration) {
		this.searchContractFromDuration = searchContractFromDuration;
	}

	/**
	 * @return the searchTitle
	 */
	public String getSearchTitle() {
		return searchTitle;
	}

	/**
	 * @param searchTitle the searchTitle to set
	 */
	public void setSearchTitle(String searchTitle) {
		this.searchTitle = searchTitle;
	}

	/**
	 * @return the searchSuppName
	 */
	public String getSearchSuppName() {
		return searchSuppName;
	}

	/**
	 * @param searchSuppName the searchSuppName to set
	 */
	public void setSearchSuppName(String searchSuppName) {
		this.searchSuppName = searchSuppName;
	}

	/**
	 * @return the docStatusList
	 */
	public List<PmStatusDesc> getDocStatusList() {
		return docStatusList;
	}

	/**
	 * @param docStatusList the docStatusList to set
	 */
	public void setDocStatusList(List<PmStatusDesc> docStatusList) {
		this.docStatusList = docStatusList;
	}

	/**
	 * @return the statusDescId
	 */
	public Long getStatusDescId() {
		return statusDescId;
	}

	/**
	 * @param statusDescId the statusDescId to set
	 */
	public void setStatusDescId(Long statusDescId) {
		this.statusDescId = statusDescId;
	}

	/**
	 * @return the pmUser
	 */
	public PmUser getPmUser() {
		return pmUser;
	}

	/**
	 * @param pmUser the pmUser to set
	 */
	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}

	/**
	 * @return the searchProcurementTypeId
	 */
	public Long getSearchProcurementTypeId() {
		return searchProcurementTypeId;
	}

	/**
	 * @param searchProcurementTypeId the searchProcurementTypeId to set
	 */
	public void setSearchProcurementTypeId(Long searchProcurementTypeId) {
		this.searchProcurementTypeId = searchProcurementTypeId;
	}

	/**
	 * @return the checkDoInDocType
	 */
	public Boolean getCheckDoInDocType() {
		return checkDoInDocType;
	}

	/**
	 * @param checkDoInDocType the checkDoInDocType to set
	 */
	public void setCheckDoInDocType(Boolean checkDoInDocType) {
		this.checkDoInDocType = checkDoInDocType;
	}

	/**
	 * @return the procurementTypeList
	 */
	public List<PmParameterDesc> getProcurementTypeList() {
		return procurementTypeList;
	}

	/**
	 * @param procurementTypeList the procurementTypeList to set
	 */
	public void setProcurementTypeList(List<PmParameterDesc> procurementTypeList) {
		this.procurementTypeList = procurementTypeList;
	}

	/**
	 * @return the searchSuppDoRef
	 */
	public String getSearchSuppDoRef() {
		return searchSuppDoRef;
	}

	/**
	 * @param searchSuppDoRef the searchSuppDoRef to set
	 */
	public void setSearchSuppDoRef(String searchSuppDoRef) {
		this.searchSuppDoRef = searchSuppDoRef;
	}

	/**
	 * @return the searchSuppInvRef
	 */
	public String getSearchSuppInvRef() {
		return searchSuppInvRef;
	}

	/**
	 * @param searchSuppInvRef the searchSuppInvRef to set
	 */
	public void setSearchSuppInvRef(String searchSuppInvRef) {
		this.searchSuppInvRef = searchSuppInvRef;
	}
	
}
