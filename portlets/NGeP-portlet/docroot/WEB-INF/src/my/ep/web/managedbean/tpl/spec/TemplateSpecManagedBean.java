/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.tpl.spec;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecScoring;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Template Specification Managed Bean
 * 
 * @author kian seng
 * @since 24/4/2013
 */

@ManagedBean(name="templateSpecMB")
@SessionScoped
public class TemplateSpecManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private int selectedIndex; 
	private String searchSpecName;
	 
	private Long searchProcurementCategoryId;
	private Long searchTemplateTypeId;
	private Long searchOrgProfileId;
	private Long searchItemTypeId;
	private Long searchSpecTypeId;	
	private TplSpecMaster specMaster;
	private TplSpecQuestion selectedSpecQuestion;
	private SmSupplier supplier;
	
	private List<TplSpecMaster> specMasterList;
	private List<TplSpecQuestion> deletedQuestions;
	private List<TplSpecScoring> specScoringList;
	
	public TplSpecMaster getSpecMaster() {
		return specMaster;
	}
	
	public void setSpecMaster(TplSpecMaster specMaster) {
		this.specMaster = specMaster;
	}
	
	public TplSpecQuestion getSelectedSpecQuestion() {
		return selectedSpecQuestion;
	}
	
	public void setSelectedSpecQuestion(TplSpecQuestion selectedSpecQuestion) {
		this.selectedSpecQuestion = selectedSpecQuestion;
	}
	
	public List<TplSpecQuestion> getDeletedQuestions() {
		if(deletedQuestions==null) {
			deletedQuestions = new ArrayList<TplSpecQuestion>();
		}
		return deletedQuestions;
	}
	
	public void setDeletedQuestions(List<TplSpecQuestion> deletedQuestions) {
		this.deletedQuestions = deletedQuestions;
	}

	public SmSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(SmSupplier supplier) {
		this.supplier = supplier;
	}

	public String getSearchSpecName() {
		return searchSpecName;
	}

	public void setSearchSpecName(String searchSpecName) {
		this.searchSpecName = searchSpecName;
	}

	public Long getSearchTemplateTypeId() {
		return searchTemplateTypeId;
	}

	public void setSearchTemplateTypeId(Long searchTemplateTypeId) {
		this.searchTemplateTypeId = searchTemplateTypeId;
	}

	public Long getSearchOrgProfileId() {
		return searchOrgProfileId;
	}

	public void setSearchOrgProfileId(Long searchOrgProfileId) {
		this.searchOrgProfileId = searchOrgProfileId;
	}

	public Long getSearchItemTypeId() {
		return searchItemTypeId;
	}

	public void setSearchItemTypeId(Long searchItemTypeId) {
		this.searchItemTypeId = searchItemTypeId;
	}

	public Long getSearchSpecTypeId() {
		return searchSpecTypeId;
	}

	public void setSearchSpecTypeId(Long searchSpecTypeId) {
		this.searchSpecTypeId = searchSpecTypeId;
	}

	public List<TplSpecMaster> getSpecMasterList() {
		return specMasterList;
	}

	public void setSpecMasterList(List<TplSpecMaster> specMasterList) {
		this.specMasterList = specMasterList;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public List<TplSpecScoring> getSpecScoringList() {
		if(specScoringList==null) {
			specScoringList = new ArrayList<TplSpecScoring>();
		}
		return specScoringList;
	}

	public void setSpecScoringList(List<TplSpecScoring> specScoringList) {
		this.specScoringList = specScoringList;
	}

	public Long getSearchProcurementCategoryId() {
		return searchProcurementCategoryId;
	}

	public void setSearchProcurementCategoryId(
			Long searchProcurementCategoryId) {
		this.searchProcurementCategoryId = searchProcurementCategoryId;
	}

}
