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
package my.ep.web.managedbean.sc.dpcustomerservice;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.backingbean.sc.directpurchase.DpCustomerServiceSearchBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.sc.quatationtender.SourcingCustomerServiceBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * The Class DPCustomerServiceBackingBean.
 * 
 * @author Garikapati Mangaiah.
 */
@ManagedBean(name="dpCustomerServiceManagedBean")
@SessionScoped
public class DPCustomerServiceManagedBean extends BaseManagedBean{
	private static Logger log = Logger.getLogger(DPCustomerServiceManagedBean.class);

private static final long serialVersionUID = 1L;

private DpCustomerServiceSearchBean dpCustServiceSearchBean;
private String dpDcoumentDescription;
private String viewOriginalUrl;
private String ministryName;
private String kampulanPtjName;
private String doctype;
private Long statusId;
private String docNumber;
private String docTitle;
private String officerName;
private List<SourcingCustomerServiceBean> customerServiceEntity;
private IPagingDataModel<Object[]> dpCustomerServiceSearchModel;


/**
 * Instantiates a new DP CustomerService managed bean.
 */
public DPCustomerServiceManagedBean(){
	log.info("inside ");

	dpCustServiceSearchBean = new DpCustomerServiceSearchBean();
}

/**
 * @return the dpCustServiceSearchBean
 */
public DpCustomerServiceSearchBean getDpCustServiceSearchBean() {
	return dpCustServiceSearchBean;
}

/**
 * @param dpCustServiceSearchBean the dpCustServiceSearchBean to set
 */
public void setDpCustServiceSearchBean(
		DpCustomerServiceSearchBean dpCustServiceSearchBean) {
	this.dpCustServiceSearchBean = dpCustServiceSearchBean;
}

/**
 * @return the dpDcoumentDescription
 */
public String getDpDcoumentDescription() {
	return dpDcoumentDescription;
}

/**
 * @param dpDcoumentDescription the dpDcoumentDescription to set
 */
public void setDpDcoumentDescription(String dpDcoumentDescription) {
	this.dpDcoumentDescription = dpDcoumentDescription;
}

/**
 * @return the viewOriginalUrl
 */
public String getViewOriginalUrl() {
	return viewOriginalUrl;
}

/**
 * @param viewOriginalUrl the viewOriginalUrl to set
 */
public void setViewOriginalUrl(String viewOriginalUrl) {
	this.viewOriginalUrl = viewOriginalUrl;
}

/**
 * @return the ministryName
 */
public String getMinistryName() {
	return ministryName;
}

/**
 * @param ministryName the ministryName to set
 */
public void setMinistryName(String ministryName) {
	this.ministryName = ministryName;
}

/**
 * @return the kampulanPtjName
 */
public String getKampulanPtjName() {
	return kampulanPtjName;
}

/**
 * @param kampulanPtjName the kampulanPtjName to set
 */
public void setKampulanPtjName(String kampulanPtjName) {
	this.kampulanPtjName = kampulanPtjName;
}

/**
 * @return the doctype
 */
public String getDoctype() {
	return doctype;
}

/**
 * @param doctype the doctype to set
 */
public void setDoctype(String doctype) {
	this.doctype = doctype;
}

/**
 * @return the statusId
 */
public Long getStatusId() {
	return statusId;
}

/**
 * @param statusId the statusId to set
 */
public void setStatusId(Long statusId) {
	this.statusId = statusId;
}

/**
 * @return the docNumber
 */
public String getDocNumber() {
	return docNumber;
}

/**
 * @param docNumber the docNumber to set
 */
public void setDocNumber(String docNumber) {
	this.docNumber = docNumber;
}

/**
 * @return the docTitle
 */
public String getDocTitle() {
	return docTitle;
}

/**
 * @param docTitle the docTitle to set
 */
public void setDocTitle(String docTitle) {
	this.docTitle = docTitle;
}

/**
 * @return the officerName
 */
public String getOfficerName() {
	return officerName;
}

/**
 * @param officerName the officerName to set
 */
public void setOfficerName(String officerName) {
	this.officerName = officerName;
}

/**
 * @return the customerServiceEntity
 */
public List<SourcingCustomerServiceBean> getCustomerServiceEntity() {
	return customerServiceEntity;
}

/**
 * @param customerServiceEntity the customerServiceEntity to set
 */
public void setCustomerServiceEntity(
		List<SourcingCustomerServiceBean> customerServiceEntity) {
	this.customerServiceEntity = customerServiceEntity;
}

/**
 * @return the dpCustomerServiceSearchModel
 */
public IPagingDataModel<Object[]> getDpCustomerServiceSearchModel() {
	return dpCustomerServiceSearchModel;
}

/**
 * @param dpCustomerServiceSearchModel the dpCustomerServiceSearchModel to set
 */
public void setDpCustomerServiceSearchModel(
		IPagingDataModel<Object[]> dpCustomerServiceSearchModel) {
	this.dpCustomerServiceSearchModel = dpCustomerServiceSearchModel;
}




}
