/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.tpl.spec;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecScoring;
import my.ep.persistence.entity.tpl.spec.TplSpecTierQty;
import my.ep.web.bean.tpl.TemplateSpecBean;
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

	private String searchSpecName;
	 
	private Long searchProcurementCategoryId;
	private Long searchTemplateTypeId;
	private Long searchOrgProfileId;
	private Long searchItemTypeId;
	private Long searchSpecTypeId;
	
	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();
	
	private List<TplSpecQuestion> deletedQuestions;
	private List<TplSpecScoring> specScoringList;
	private List<TplSpecTierQty> selectedTierQtyList;
	
	public List<TplSpecQuestion> getDeletedQuestions() {
		if(deletedQuestions==null) {
			deletedQuestions = new ArrayList<TplSpecQuestion>();
		}
		return deletedQuestions;
	}
	
	public void setDeletedQuestions(List<TplSpecQuestion> deletedQuestions) {
		this.deletedQuestions = deletedQuestions;
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

	/**
	 * @return the templateSpecBean
	 */
	public TemplateSpecBean getTemplateSpecBean() {
		return templateSpecBean;
	}

	/**
	 * @param templateSpecBean the templateSpecBean to set
	 */
	public void setTemplateSpecBean(TemplateSpecBean templateSpecBean) {
		this.templateSpecBean = templateSpecBean;
	}

	/**
	 * @return the selectedTierQtyList
	 */
	public List<TplSpecTierQty> getSelectedTierQtyList() {
		return selectedTierQtyList;
	}

	/**
	 * @param selectedTierQtyList the selectedTierQtyList to set
	 */
	public void setSelectedTierQtyList(List<TplSpecTierQty> selectedTierQtyList) {
		this.selectedTierQtyList = selectedTierQtyList;
	}

}
