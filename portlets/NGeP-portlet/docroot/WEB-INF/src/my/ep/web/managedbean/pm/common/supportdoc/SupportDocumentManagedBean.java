/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.common.supportdoc;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmDocumentRestr;
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
	private List <PmDocumentRestr> deleteAppTypeRestrictions;
	private List <PmDocumentRestr> deleteComTypeRestrictions;
	private List <PmDocumentRestr> deleteSuppTypeRestrictions;
	private List <PmDocumentRestr> deleteCategoryRestrictions;
	
	
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
	
	

	
	
}