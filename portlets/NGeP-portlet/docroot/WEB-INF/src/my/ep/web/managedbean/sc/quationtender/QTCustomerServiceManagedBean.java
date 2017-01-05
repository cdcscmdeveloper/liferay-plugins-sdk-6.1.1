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
package my.ep.web.managedbean.sc.quationtender;


import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.sc.quatationtender.QTCustomerServiceSearchBean;
import my.ep.web.bean.sc.quatationtender.SourcingCustomerServiceBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * The Class DPCustomerServiceManagedBean.
 * 
 * @author Garikapati Mangaiah.
 */
@ManagedBean(name="qtCustomerServiceManagedBean")
@SessionScoped
public class QTCustomerServiceManagedBean extends BaseManagedBean{
private static Logger log = Logger.getLogger(QTCustomerServiceManagedBean.class);

private static final long serialVersionUID = 1L;

private QTCustomerServiceSearchBean qtCustomerServiceSearchBean;
private String viewOriginalUrl;
private ScQt customerScQt;
private String ministryName;
private String kampulanPtjName;
private String ptjCode;
private String ptjName;
private String officerName;
private List<SourcingCustomerServiceBean> customerServiceEntity;
private List<ScBidSchedule> bidScheduleList;
private List<ScQtBsvDtl> briefingDtlsList;
private List<ScQtBsvDtl> siteVisitDtlsList;
private Long totalProposalSubmitCount;
private IPagingDataModel<ScQt> qtCustomerServiceDataModel;

/**
 * Instantiates a new DP CustomerService managed bean.
 */
public QTCustomerServiceManagedBean(){
	log.info("inside ");

	qtCustomerServiceSearchBean = new QTCustomerServiceSearchBean();
}

/**
 * @return the qtCustomerServiceSearchBean
 */
public QTCustomerServiceSearchBean getQtCustomerServiceSearchBean() {
	return qtCustomerServiceSearchBean;
}

/**
 * @param qtCustomerServiceSearchBean the qtCustomerServiceSearchBean to set
 */
public void setQtCustomerServiceSearchBean(
		QTCustomerServiceSearchBean qtCustomerServiceSearchBean) {
	this.qtCustomerServiceSearchBean = qtCustomerServiceSearchBean;
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
 * @return the customerScQt
 */
public ScQt getCustomerScQt() {
	return customerScQt;
}

/**
 * @param customerScQt the customerScQt to set
 */
public void setCustomerScQt(ScQt customerScQt) {
	this.customerScQt = customerScQt;
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
 * @return the ptjCode
 */
public String getPtjCode() {
	return ptjCode;
}

/**
 * @param ptjCode the ptjCode to set
 */
public void setPtjCode(String ptjCode) {
	this.ptjCode = ptjCode;
}

/**
 * @return the ptjName
 */
public String getPtjName() {
	return ptjName;
}

/**
 * @param ptjName the ptjName to set
 */
public void setPtjName(String ptjName) {
	this.ptjName = ptjName;
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
 * @return the bidScheduleList
 */
public List<ScBidSchedule> getBidScheduleList() {
	return bidScheduleList;
}

/**
 * @param bidScheduleList the bidScheduleList to set
 */
public void setBidScheduleList(List<ScBidSchedule> bidScheduleList) {
	this.bidScheduleList = bidScheduleList;
}

/**
 * @return the briefingDtlsList
 */
public List<ScQtBsvDtl> getBriefingDtlsList() {
	return briefingDtlsList;
}

/**
 * @param briefingDtlsList the briefingDtlsList to set
 */
public void setBriefingDtlsList(List<ScQtBsvDtl> briefingDtlsList) {
	this.briefingDtlsList = briefingDtlsList;
}

/**
 * @return the siteVisitDtlsList
 */
public List<ScQtBsvDtl> getSiteVisitDtlsList() {
	return siteVisitDtlsList;
}

/**
 * @param siteVisitDtlsList the siteVisitDtlsList to set
 */
public void setSiteVisitDtlsList(List<ScQtBsvDtl> siteVisitDtlsList) {
	this.siteVisitDtlsList = siteVisitDtlsList;
}

/**
 * @return the totalProposalSubmitCount
 */
public Long getTotalProposalSubmitCount() {
	return totalProposalSubmitCount;
}

/**
 * @param totalProposalSubmitCount the totalProposalSubmitCount to set
 */
public void setTotalProposalSubmitCount(Long totalProposalSubmitCount) {
	this.totalProposalSubmitCount = totalProposalSubmitCount;
}

/**
 * @return the qtCustomerServiceDataModel
 */
public IPagingDataModel<ScQt> getQtCustomerServiceDataModel() {
	return qtCustomerServiceDataModel;
}

/**
 * @param qtCustomerServiceDataModel the qtCustomerServiceDataModel to set
 */
public void setQtCustomerServiceDataModel(
		IPagingDataModel<ScQt> qtCustomerServiceDataModel) {
	this.qtCustomerServiceDataModel = qtCustomerServiceDataModel;
}




}
