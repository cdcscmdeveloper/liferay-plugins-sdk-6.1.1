/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.common.supportdoc;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmDocumentRestr;
import my.ep.persistence.entity.pm.common.PmParameter;
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
	
	
	public PmParameter getSupplierDocParameter() {
		return supplierDocParameter;
	}

	public void setSupplierDocParameter(PmParameter supplierDocParameter) {
		this.supplierDocParameter = supplierDocParameter;
	}

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

	
}