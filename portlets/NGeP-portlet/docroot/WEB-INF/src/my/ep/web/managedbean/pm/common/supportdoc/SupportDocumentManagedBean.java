/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.common.supportdoc;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmDocumentRestr;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.web.bean.pm.SupportDocumentSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Support Document
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
@ManagedBean(name = "supportDocManagedBean")
@SessionScoped
public class SupportDocumentManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	
	private SupportDocumentSearchBean supportDocSearchBean;
	private PmDocument selectedDocument;
	private boolean checkList;
	private List <PmDocumentRestr> appTypeRestrictions;
	private List <PmDocumentRestr> comTypeRestrictions;
	private List <PmDocumentRestr> suppTypeRestrictions;
	private List <PmDocumentRestr> categoryRestrictions;
	private List <PmDocumentRestr> certBodyRestrictions;
	private List <PmDocumentRestr> checkListRestrictions;
	private List <PmDocumentRestr> deleteAppTypeRestrictions;
	private List <PmDocumentRestr> deleteComTypeRestrictions;
	private List <PmDocumentRestr> deleteSuppTypeRestrictions;
	private List <PmDocumentRestr> deleteCategoryRestrictions;
	private List <PmDocumentRestr> deleteCertBodyRestrictions;
	private List <PmDocumentRestr> deleteCheckListRestrictions;
	private List <PmDocumentRestr> deleteRestrictions;
	private SelectItem[] supplierActionItems;
	private SelectItem[] docDefinitionOptions;
	private PmCategoryL3 selectedCategoryL3;
	private List <PmCertBody> selectedCertBodies;
	private int selectedTabIndex;
	private PmParameter supplierDocParameter;
	private Integer deleteRowIndex;
	private PmDocumentRestr selectedAppTypeRestr;
	private boolean isCompanyInfoChanged;
	private boolean isAsscProjChanged;
	private boolean isPersonnelBranchChanged;
	private boolean isCapitalEquityChanged;
	private boolean isBankChanged;
	/************ AUDIT #28895_35509 *************/
	private PmDocument selectedDocumentOld;
	private DocumentReqDTO reqDTO;
	private DocumentResDTO resDTO;
	private List<PmDocumentRestr> pmDocumentRestrList;
	private List<PmDocumentRestr> deletedPmDocumentRestrList;
	private List<PmDocumentRestr> updatedPmDocumentRestrList;
	private List<PmDocumentRestr> mandatoryUpdatedPmDocumentRestrList;
	private List<PmDocumentRestr> deleteAppTypeRestrictionsAuditList;
	private List<PmDocumentRestr> deleteComTypeRestrictionsAudit;
	private List<PmDocumentRestr> deleteSuppTypeRestrictionsAudit;
	private List<PmDocumentRestr> deleteCategoryRestrictionsAudit;
	private List<PmDocumentRestr> deleteCertBodyRestrictionsAudit;
	boolean updated;
	private List<PmDocumentRestr> appTypeRestrictionsOld;
	private List<PmDocumentRestr> comTypeRestrictionsOld;
	private List<PmDocumentRestr> suppTypeRestrictionsOld;
	private List<PmDocumentRestr> categoryRestrictionsOld;
	private List<PmDocumentRestr> certBodyRestrictionsOld;
	private List<PmDocumentRestr> appTypeRestrictionsNew = new ArrayList<>();
	private List<PmDocumentRestr> comTypeRestrictionsNew = new ArrayList<>();
	private List<PmDocumentRestr> suppTypeRestrictionsNew = new ArrayList<>();
	private List<PmDocumentRestr> categoryRestrictionsNew = new ArrayList<>();
	private List<PmDocumentRestr> certBodyRestrictionsNew = new ArrayList<>();
	private List<PmDocumentRestr> appTypeRestrictionsUpdated = new ArrayList<>();
	private List<PmDocumentRestr> newCompanyCodeTypeAuditList = new ArrayList<>();
	private List<PmDocumentRestr> newCategoryL3AuditList = new ArrayList<>();
	private boolean deletedAuditList;
	private PmZone auditPmZone;

	/************ AUDIT #28895_35509 *************/
	
	public SupportDocumentManagedBean(){}

	/**
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	/**
	 * @param selectedTabIndex the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	/**
	 * @return the appTypeRestrictions
	 */
	public List<PmDocumentRestr> getAppTypeRestrictions() {
		return appTypeRestrictions;
	}

	/**
	 * @param appTypeRestrictions the appTypeRestrictions to set
	 */
	public void setAppTypeRestrictions(List<PmDocumentRestr> appTypeRestrictions) {
		this.appTypeRestrictions = appTypeRestrictions;
	}

	/**
	 * @return the comTypeRestrictions
	 */
	public List<PmDocumentRestr> getComTypeRestrictions() {
		return comTypeRestrictions;
	}

	/**
	 * @param comTypeRestrictions the comTypeRestrictions to set
	 */
	public void setComTypeRestrictions(List<PmDocumentRestr> comTypeRestrictions) {
		this.comTypeRestrictions = comTypeRestrictions;
	}

	/**
	 * @return the suppTypeRestrictions
	 */
	public List<PmDocumentRestr> getSuppTypeRestrictions() {
		return suppTypeRestrictions;
	}

	/**
	 * @param suppTypeRestrictions the suppTypeRestrictions to set
	 */
	public void setSuppTypeRestrictions(List<PmDocumentRestr> suppTypeRestrictions) {
		this.suppTypeRestrictions = suppTypeRestrictions;
	}

	/**
	 * @return the categoryRestrictions
	 */
	public List<PmDocumentRestr> getCategoryRestrictions() {
		return categoryRestrictions;
	}

	/**
	 * @param categoryRestrictions the categoryRestrictions to set
	 */
	public void setCategoryRestrictions(List<PmDocumentRestr> categoryRestrictions) {
		this.categoryRestrictions = categoryRestrictions;
	}

	/**
	 * @return the checkList
	 */
	public boolean isCheckList() {
		return checkList;
	}

	/**
	 * @param checkList the checkList to set
	 */
	public void setCheckList(boolean checkList) {
		this.checkList = checkList;
	}

	/**
	 * @return the supportDocSearchBean
	 */
	public SupportDocumentSearchBean getSupportDocSearchBean() {
		if(supportDocSearchBean==null)
			supportDocSearchBean = new SupportDocumentSearchBean();
		return supportDocSearchBean;
	}

	/**
	 * @param supportDocSearchBean the supportDocSearchBean to set
	 */
	public void setSupportDocSearchBean(
			SupportDocumentSearchBean supportDocSearchBean) {
		this.supportDocSearchBean = supportDocSearchBean;
	}

	/**
	 * @return the selectedDocument
	 */
	public PmDocument getSelectedDocument() {
		return selectedDocument;
	}

	/**
	 * @param selectedDocument the selectedDocument to set
	 */
	public void setSelectedDocument(PmDocument selectedDocument) {
		this.selectedDocument = selectedDocument;
	}

	/**
	 * @return the deleteAppTypeRestrictions
	 */
	public List<PmDocumentRestr> getDeleteAppTypeRestrictions() {
		return deleteAppTypeRestrictions;
	}

	/**
	 * @param deleteAppTypeRestrictions the deleteAppTypeRestrictions to set
	 */
	public void setDeleteAppTypeRestrictions(
			List<PmDocumentRestr> deleteAppTypeRestrictions) {
		this.deleteAppTypeRestrictions = deleteAppTypeRestrictions;
	}

	/**
	 * @return the deleteComTypeRestrictions
	 */
	public List<PmDocumentRestr> getDeleteComTypeRestrictions() {
		return deleteComTypeRestrictions;
	}

	/**
	 * @param deleteComTypeRestrictions the deleteComTypeRestrictions to set
	 */
	public void setDeleteComTypeRestrictions(
			List<PmDocumentRestr> deleteComTypeRestrictions) {
		this.deleteComTypeRestrictions = deleteComTypeRestrictions;
	}

	/**
	 * @return the deleteSuppTypeRestrictions
	 */
	public List<PmDocumentRestr> getDeleteSuppTypeRestrictions() {
		return deleteSuppTypeRestrictions;
	}

	/**
	 * @param deleteSuppTypeRestrictions the deleteSuppTypeRestrictions to set
	 */
	public void setDeleteSuppTypeRestrictions(
			List<PmDocumentRestr> deleteSuppTypeRestrictions) {
		this.deleteSuppTypeRestrictions = deleteSuppTypeRestrictions;
	}

	/**
	 * @return the deleteCategoryRestrictions
	 */
	public List<PmDocumentRestr> getDeleteCategoryRestrictions() {
		return deleteCategoryRestrictions;
	}

	/**
	 * @param deleteCategoryRestrictions the deleteCategoryRestrictions to set
	 */
	public void setDeleteCategoryRestrictions(
			List<PmDocumentRestr> deleteCategoryRestrictions) {
		this.deleteCategoryRestrictions = deleteCategoryRestrictions;
	}

	/**
	 * @return the selectedCategoryL3
	 */
	public PmCategoryL3 getSelectedCategoryL3() {
		return selectedCategoryL3;
	}

	/**
	 * @return the checkListRestrictions
	 */
	public List<PmDocumentRestr> getCheckListRestrictions() {
		return checkListRestrictions;
	}

	/**
	 * @param checkListRestrictions the checkListRestrictions to set
	 */
	public void setCheckListRestrictions(List<PmDocumentRestr> checkListRestrictions) {
		this.checkListRestrictions = checkListRestrictions;
	}

	/**
	 * @return the deleteCheckListRestrictions
	 */
	public List<PmDocumentRestr> getDeleteCheckListRestrictions() {
		return deleteCheckListRestrictions;
	}

	/**
	 * @param deleteCheckListRestrictions the deleteCheckListRestrictions to set
	 */
	public void setDeleteCheckListRestrictions(
			List<PmDocumentRestr> deleteCheckListRestrictions) {
		this.deleteCheckListRestrictions = deleteCheckListRestrictions;
	}

	/**
	 * @param selectedCategoryL3 the selectedCategoryL3 to set
	 */
	public void setSelectedCategoryL3(PmCategoryL3 selectedCategoryL3) {
		this.selectedCategoryL3 = selectedCategoryL3;
	}

	public List <PmDocumentRestr> getCertBodyRestrictions() {
		return certBodyRestrictions;
	}

	public void setCertBodyRestrictions(List <PmDocumentRestr> certBodyRestrictions) {
		this.certBodyRestrictions = certBodyRestrictions;
	}

	public List <PmDocumentRestr> getDeleteCertBodyRestrictions() {
		return deleteCertBodyRestrictions;
	}

	public void setDeleteCertBodyRestrictions(
			List <PmDocumentRestr> deleteCertBodyRestrictions) {
		this.deleteCertBodyRestrictions = deleteCertBodyRestrictions;
	}

	public List <PmCertBody> getSelectedCertBodies() {
		return selectedCertBodies;
	}

	public void setSelectedCertBodies(List <PmCertBody> selectedCertBodies) {
		this.selectedCertBodies = selectedCertBodies;
	}

	public List <PmDocumentRestr> getDeleteRestrictions() {
		return deleteRestrictions;
	}

	public void setDeleteRestrictions(List <PmDocumentRestr> deleteRestrictions) {
		this.deleteRestrictions = deleteRestrictions;
	}

	/**
	 * @return the supplierActionItems
	 */
	public SelectItem[] getSupplierActionItems() {
		return supplierActionItems;
	}

	/**
	 * @param supplierActionItems the supplierActionItems to set
	 */
	public void setSupplierActionItems(SelectItem[] supplierActionItems) {
		this.supplierActionItems = supplierActionItems;
	}

	/**
	 * @return the docDefinitionOptions
	 */
	public SelectItem[] getDocDefinitionOptions() {
		return docDefinitionOptions;
	}

	/**
	 * @param docDefinitionOptions the docDefinitionOptions to set
	 */
	public void setDocDefinitionOptions(SelectItem[] docDefinitionOptions) {
		this.docDefinitionOptions = docDefinitionOptions;
	}

	/**
	 * @return the deleteRowIndex
	 */
	public Integer getDeleteRowIndex() {
		return deleteRowIndex;
	}

	/**
	 * @param deleteRowIndex the deleteRowIndex to set
	 */
	public void setDeleteRowIndex(Integer deleteRowIndex) {
		this.deleteRowIndex = deleteRowIndex;
	}



	/**
	 * @return the supplierDocParameter
	 */
	public PmParameter getSupplierDocParameter() {
		return supplierDocParameter;
	}



	/**
	 * @param supplierDocParameter the supplierDocParameter to set
	 */
	public void setSupplierDocParameter(PmParameter supplierDocParameter) {
		this.supplierDocParameter = supplierDocParameter;
	}

	/**
	 * @return the selectedAppTypeRestr
	 */
	public PmDocumentRestr getSelectedAppTypeRestr() {
		return selectedAppTypeRestr;
	}

	/**
	 * @param selectedAppTypeRestr the selectedAppTypeRestr to set
	 */
	public void setSelectedAppTypeRestr(PmDocumentRestr selectedAppTypeRestr) {
		this.selectedAppTypeRestr = selectedAppTypeRestr;
	}

	/**
	 * @return the isCompanyInfoChanged
	 */
	public boolean isCompanyInfoChanged() {
		return isCompanyInfoChanged;
	}

	/**
	 * @param isCompanyInfoChanged the isCompanyInfoChanged to set
	 */
	public void setCompanyInfoChanged(boolean isCompanyInfoChanged) {
		this.isCompanyInfoChanged = isCompanyInfoChanged;
	}

	/**
	 * @return the isAsscProjChanged
	 */
	public boolean isAsscProjChanged() {
		return isAsscProjChanged;
	}

	/**
	 * @param isAsscProjChanged the isAsscProjChanged to set
	 */
	public void setAsscProjChanged(boolean isAsscProjChanged) {
		this.isAsscProjChanged = isAsscProjChanged;
	}

	/**
	 * @return the isPersonnelBranchChanged
	 */
	public boolean isPersonnelBranchChanged() {
		return isPersonnelBranchChanged;
	}

	/**
	 * @param isPersonnelBranchChanged the isPersonnelBranchChanged to set
	 */
	public void setPersonnelBranchChanged(boolean isPersonnelBranchChanged) {
		this.isPersonnelBranchChanged = isPersonnelBranchChanged;
	}

	/**
	 * @return the isCapitalEquityChanged
	 */
	public boolean isCapitalEquityChanged() {
		return isCapitalEquityChanged;
	}

	/**
	 * @param isCapitalEquityChanged the isCapitalEquityChanged to set
	 */
	public void setCapitalEquityChanged(boolean isCapitalEquityChanged) {
		this.isCapitalEquityChanged = isCapitalEquityChanged;
	}

	/**
	 * @return the isBankChanged
	 */
	public boolean isBankChanged() {
		return isBankChanged;
	}

	/**
	 * @param isBankChanged the isBankChanged to set
	 */
	public void setBankChanged(boolean isBankChanged) {
		this.isBankChanged = isBankChanged;
	}

	/************ AUDIT #28895_35509 *************/
	public PmDocument getSelectedDocumentOld() {
		return selectedDocumentOld;
	}

	public void setSelectedDocumentOld(PmDocument selectedDocumentOld) {
		this.selectedDocumentOld = selectedDocumentOld;
	}

	public DocumentReqDTO getReqDTO() {
		return reqDTO;
	}

	public void setReqDTO(DocumentReqDTO reqDTO) {
		this.reqDTO = reqDTO;
	}

	public DocumentResDTO getResDTO() {
		return resDTO;
	}

	public void setResDTO(DocumentResDTO resDTO) {
		this.resDTO = resDTO;
	}

	public List<PmDocumentRestr> getPmDocumentRestrList() {
		return pmDocumentRestrList;
	}

	public void setPmDocumentRestrList(List<PmDocumentRestr> pmDocumentRestrList) {
		this.pmDocumentRestrList = pmDocumentRestrList;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public List<PmDocumentRestr> getDeletedPmDocumentRestrList() {
		return deletedPmDocumentRestrList;
	}

	public void setDeletedPmDocumentRestrList(List<PmDocumentRestr> deletedPmDocumentRestrList) {
		this.deletedPmDocumentRestrList = deletedPmDocumentRestrList;
	}

	public List<PmDocumentRestr> getUpdatedPmDocumentRestrList() {
		return updatedPmDocumentRestrList;
	}

	public void setUpdatedPmDocumentRestrList(List<PmDocumentRestr> updatedPmDocumentRestrList) {
		this.updatedPmDocumentRestrList = updatedPmDocumentRestrList;
	}

	public List<PmDocumentRestr> getMandatoryUpdatedPmDocumentRestrList() {
		return mandatoryUpdatedPmDocumentRestrList;
	}

	public void setMandatoryUpdatedPmDocumentRestrList(List<PmDocumentRestr> mandatoryUpdatedPmDocumentRestrList) {
		this.mandatoryUpdatedPmDocumentRestrList = mandatoryUpdatedPmDocumentRestrList;
	}

	public List<PmDocumentRestr> getAppTypeRestrictionsOld() {
		return appTypeRestrictionsOld;
	}

	public void setAppTypeRestrictionsOld(List<PmDocumentRestr> appTypeRestrictionsOld) {
		this.appTypeRestrictionsOld = appTypeRestrictionsOld;
	}

	public List<PmDocumentRestr> getComTypeRestrictionsOld() {
		return comTypeRestrictionsOld;
	}

	public void setComTypeRestrictionsOld(List<PmDocumentRestr> comTypeRestrictionsOld) {
		this.comTypeRestrictionsOld = comTypeRestrictionsOld;
	}

	public List<PmDocumentRestr> getSuppTypeRestrictionsOld() {
		return suppTypeRestrictionsOld;
	}

	public void setSuppTypeRestrictionsOld(List<PmDocumentRestr> suppTypeRestrictionsOld) {
		this.suppTypeRestrictionsOld = suppTypeRestrictionsOld;
	}

	public List<PmDocumentRestr> getCategoryRestrictionsOld() {
		return categoryRestrictionsOld;
	}

	public void setCategoryRestrictionsOld(List<PmDocumentRestr> categoryRestrictionsOld) {
		this.categoryRestrictionsOld = categoryRestrictionsOld;
	}

	public List<PmDocumentRestr> getCertBodyRestrictionsOld() {
		return certBodyRestrictionsOld;
	}

	public void setCertBodyRestrictionsOld(List<PmDocumentRestr> certBodyRestrictionsOld) {
		this.certBodyRestrictionsOld = certBodyRestrictionsOld;
	}

	public List<PmDocumentRestr> getDeleteAppTypeRestrictionsAuditList() {
		return deleteAppTypeRestrictionsAuditList;
	}

	public void setDeleteAppTypeRestrictionsAuditList(List<PmDocumentRestr> deleteAppTypeRestrictionsAuditList) {
		this.deleteAppTypeRestrictionsAuditList = deleteAppTypeRestrictionsAuditList;
	}

	public List<PmDocumentRestr> getDeleteComTypeRestrictionsAudit() {
		return deleteComTypeRestrictionsAudit;
	}

	public void setDeleteComTypeRestrictionsAudit(List<PmDocumentRestr> deleteComTypeRestrictionsAudit) {
		this.deleteComTypeRestrictionsAudit = deleteComTypeRestrictionsAudit;
	}

	public List<PmDocumentRestr> getDeleteSuppTypeRestrictionsAudit() {
		return deleteSuppTypeRestrictionsAudit;
	}

	public void setDeleteSuppTypeRestrictionsAudit(List<PmDocumentRestr> deleteSuppTypeRestrictionsAudit) {
		this.deleteSuppTypeRestrictionsAudit = deleteSuppTypeRestrictionsAudit;
	}

	public List<PmDocumentRestr> getDeleteCategoryRestrictionsAudit() {
		return deleteCategoryRestrictionsAudit;
	}

	public void setDeleteCategoryRestrictionsAudit(List<PmDocumentRestr> deleteCategoryRestrictionsAudit) {
		this.deleteCategoryRestrictionsAudit = deleteCategoryRestrictionsAudit;
	}

	public List<PmDocumentRestr> getDeleteCertBodyRestrictionsAudit() {
		return deleteCertBodyRestrictionsAudit;
	}

	public void setDeleteCertBodyRestrictionsAudit(List<PmDocumentRestr> deleteCertBodyRestrictionsAudit) {
		this.deleteCertBodyRestrictionsAudit = deleteCertBodyRestrictionsAudit;
	}

	public boolean isDeletedAuditList() {
		return deletedAuditList;
	}

	public void setDeletedAuditList(boolean deletedAuditList) {
		this.deletedAuditList = deletedAuditList;
	}

	public List<PmDocumentRestr> getAppTypeRestrictionsNew() {
		return appTypeRestrictionsNew;
	}

	public void setAppTypeRestrictionsNew(List<PmDocumentRestr> appTypeRestrictionsNew) {
		this.appTypeRestrictionsNew = appTypeRestrictionsNew;
	}

	public List<PmDocumentRestr> getComTypeRestrictionsNew() {
		return comTypeRestrictionsNew;
	}

	public void setComTypeRestrictionsNew(List<PmDocumentRestr> comTypeRestrictionsNew) {
		this.comTypeRestrictionsNew = comTypeRestrictionsNew;
	}

	public List<PmDocumentRestr> getSuppTypeRestrictionsNew() {
		return suppTypeRestrictionsNew;
	}

	public void setSuppTypeRestrictionsNew(List<PmDocumentRestr> suppTypeRestrictionsNew) {
		this.suppTypeRestrictionsNew = suppTypeRestrictionsNew;
	}

	public List<PmDocumentRestr> getCategoryRestrictionsNew() {
		return categoryRestrictionsNew;
	}

	public void setCategoryRestrictionsNew(List<PmDocumentRestr> categoryRestrictionsNew) {
		this.categoryRestrictionsNew = categoryRestrictionsNew;
	}

	public List<PmDocumentRestr> getCertBodyRestrictionsNew() {
		return certBodyRestrictionsNew;
	}

	public void setCertBodyRestrictionsNew(List<PmDocumentRestr> certBodyRestrictionsNew) {
		this.certBodyRestrictionsNew = certBodyRestrictionsNew;
	}

	public List<PmDocumentRestr> getAppTypeRestrictionsUpdated() {
		return appTypeRestrictionsUpdated;
	}

	public void setAppTypeRestrictionsUpdated(List<PmDocumentRestr> appTypeRestrictionsUpdated) {
		this.appTypeRestrictionsUpdated = appTypeRestrictionsUpdated;
	}

	public List<PmDocumentRestr> getNewCompanyCodeTypeAuditList() {
		return newCompanyCodeTypeAuditList;
	}

	public void setNewCompanyCodeTypeAuditList(List<PmDocumentRestr> newCompanyCodeTypeAuditList) {
		this.newCompanyCodeTypeAuditList = newCompanyCodeTypeAuditList;
	}

	public List<PmDocumentRestr> getNewCategoryL3AuditList() {
		return newCategoryL3AuditList;
	}

	public void setNewCategoryL3AuditList(List<PmDocumentRestr> newCategoryL3AuditList) {
		this.newCategoryL3AuditList = newCategoryL3AuditList;
	}

	/**
	 * @return the auditPmZone
	 */
	public PmZone getAuditPmZone() {
		return auditPmZone;
	}

	/**
	 * @param auditPmZone the auditPmZone to set
	 */
	public void setAuditPmZone(PmZone auditPmZone) {
		this.auditPmZone = auditPmZone;
	}
	/************ AUDIT #28895_35509 *************/
}