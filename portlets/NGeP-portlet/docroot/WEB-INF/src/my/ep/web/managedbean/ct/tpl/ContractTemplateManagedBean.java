/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.tpl;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.persistence.entity.ct.template.CtTemplate;
import my.ep.type.aut.SearchOperator;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean For Contract Template
 * 
 * @author Alam
 * @since 23/9/2013	
 * 
 */

@ManagedBean
@SessionScoped
public class ContractTemplateManagedBean extends BaseManagedBean{

	private static final long serialVersionUID = 1L;
	
	private int selectedIndex;
	private CtTemplate ctTemplate = new CtTemplate();;
	private List<CtTemplate> ctTemplatesList;
	private SearchFilterBean searchFilterBean;
	 private String editorValue;
   
	public ContractTemplateManagedBean(){
    	searchFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_STARTS_WITH.value());
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	
	public SearchFilterBean getSearchFilterBean() {
		return searchFilterBean;
	}

	public void setSearchFilterBean(SearchFilterBean searchFilterBean) {
		this.searchFilterBean = searchFilterBean;
	}

	public List<CtTemplate> getCtTemplatesList() {
		return ctTemplatesList;
	}

	public void setCtTemplatesList(List<CtTemplate> ctTemplatesList) {
		this.ctTemplatesList = ctTemplatesList;
	}
	
	public CtTemplate getCtTemplate() {
		return ctTemplate;
	}

	public void setCtTemplate(CtTemplate ctTemplate) {
		this.ctTemplate = ctTemplate;
	}

	public String getEditorValue() {
		return editorValue;
	}

	public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
	}
	
}
