/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.tpl.spec;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
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
	
	private List<TplSpecQuestion> tplSpecQuestions;
	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();
	
	public TemplateSpecManagedBean() {
	}

	/**
	 * @return the searchSpecName
	 */
	public String getSearchSpecName() {
		return searchSpecName;
	}

	/**
	 * @param searchSpecName the searchSpecName to set
	 */
	public void setSearchSpecName(String searchSpecName) {
		this.searchSpecName = searchSpecName;
	}

	/**
	 * @return the searchProcurementCategoryId
	 */
	public Long getSearchProcurementCategoryId() {
		return searchProcurementCategoryId;
	}

	/**
	 * @param searchProcurementCategoryId the searchProcurementCategoryId to set
	 */
	public void setSearchProcurementCategoryId(Long searchProcurementCategoryId) {
		this.searchProcurementCategoryId = searchProcurementCategoryId;
	}

	/**
	 * @return the searchTemplateTypeId
	 */
	public Long getSearchTemplateTypeId() {
		return searchTemplateTypeId;
	}

	/**
	 * @param searchTemplateTypeId the searchTemplateTypeId to set
	 */
	public void setSearchTemplateTypeId(Long searchTemplateTypeId) {
		this.searchTemplateTypeId = searchTemplateTypeId;
	}

	/**
	 * @return the searchOrgProfileId
	 */
	public Long getSearchOrgProfileId() {
		return searchOrgProfileId;
	}

	/**
	 * @param searchOrgProfileId the searchOrgProfileId to set
	 */
	public void setSearchOrgProfileId(Long searchOrgProfileId) {
		this.searchOrgProfileId = searchOrgProfileId;
	}

	/**
	 * @return the searchItemTypeId
	 */
	public Long getSearchItemTypeId() {
		return searchItemTypeId;
	}

	/**
	 * @param searchItemTypeId the searchItemTypeId to set
	 */
	public void setSearchItemTypeId(Long searchItemTypeId) {
		this.searchItemTypeId = searchItemTypeId;
	}

	/**
	 * @return the searchSpecTypeId
	 */
	public Long getSearchSpecTypeId() {
		return searchSpecTypeId;
	}

	/**
	 * @param searchSpecTypeId the searchSpecTypeId to set
	 */
	public void setSearchSpecTypeId(Long searchSpecTypeId) {
		this.searchSpecTypeId = searchSpecTypeId;
	}

	/**
	 * @return the tplSpecQuestions
	 */
	public List<TplSpecQuestion> getTplSpecQuestions() {
		return tplSpecQuestions;
	}

	/**
	 * @param tplSpecQuestions the tplSpecQuestions to set
	 */
	public void setTplSpecQuestions(List<TplSpecQuestion> tplSpecQuestions) {
		this.tplSpecQuestions = tplSpecQuestions;
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

}
