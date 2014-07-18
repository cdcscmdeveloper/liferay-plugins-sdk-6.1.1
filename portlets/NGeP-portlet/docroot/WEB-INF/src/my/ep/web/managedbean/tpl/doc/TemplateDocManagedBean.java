/**
 * Copyright © 2012 Archisoft. All rights reserved.
*/
package my.ep.web.managedbean.tpl.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.ct.agreement.CtAgreementContent;
import my.ep.persistence.entity.tpl.TplCategory;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.persistence.entity.tpl.doc.TplDocSection;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.TreeNode;

/**
 * Managed Bean for Template Document.
 * 
 * @author Melinda
 * @created 15.10.2012
 * @see my.ep.web.managedbean.BaseManagedBean
 */
@ManagedBean(name = "templateDocMB")
@SessionScoped
public class TemplateDocManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private Long categoryId;
	private String processType;
	private String title;
	private String desc;
	private String templCategory;
	
	private String processValue;

	private Long impCategoryId;
	private Long docMasterId;	
	
	private TplCategory category;
	
	private TplDocMaster selectedTemplate;
	private TplDocMaster selectedImpMaster;
	private List<TplDocMaster> templateMasterList;
	private List<TplDocMaster> importMasterList;
	
	private TplDocSection selectedSection;
	private List<TplDocSection> deletedList;	
	private List<TplDocSection> fullTemplateList;
	private List<TplDocSection> selectedTemplateList;	
	private List<TplDocSection> importedSectionList;
	private List<TplDocSection> variableList;
	
	private TreeNode docSectionTN;
	private TreeNode imptSectionTN;
	private TreeNode selectedImpSection;
	private Map<TplDocSection, TreeNode> docSectionMap;
	
	private String selectedSectionTitle;
	private String selectedSectionContent;
	private String selectedSectionName;
	private String selectedOption;
	private String previewValue;
	private String flow;
	private String selectedVariable;
	private boolean showTemplateList = false;
	
	private String selectedSectionText;
	
		private List<SelectItem> templateTypeList;
	    private Map<String, String> templateTypeMap;
	
	
	private List<SelectItem> templateCategoryList; 
	
	private SelectItem[] templateProcessTypeList;
	
	//private List<TplSearch> templateDocSearch;

	// for search in data table
	private List<TplDocMaster> templateSearchList;

	private String externalPageUrl;
	private CtAgreementContent ctAgreementContent; 
		/**
	 * TemplateManagedBean constructor.
	 */	
	public TemplateDocManagedBean() {
	}

	
	public List<TplDocSection> getFullTemplateList() {
		return fullTemplateList;
	}

	public void setFullTemplateList(List<TplDocSection> fullTemplateList) {
		this.fullTemplateList = fullTemplateList;
	}

	public List<TplDocSection> getSelectedTemplateList() {
		return selectedTemplateList;
	}

	public void setSelectedTemplateList(
			List<TplDocSection> selectedTemplateList) {
		this.selectedTemplateList = selectedTemplateList;
	}

	public TplDocSection getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(TplDocSection selectedSection) {
		this.selectedSection = selectedSection;
	}

	public String getSelectedSectionTitle() {
		return selectedSectionTitle;
	}

	public void setSelectedSectionTitle(String selectedSectionTitle) {
		this.selectedSectionTitle = selectedSectionTitle;
	}

	public boolean isShowTemplateList() {
		return showTemplateList;
	}

	public void setShowTemplateList(boolean showTemplateList) {
		this.showTemplateList = showTemplateList;
	}

	public String getFlow() {
		return flow;
	}

	public void setFlow(String flow) {
		this.flow = flow;
	}

	public TplDocMaster getSelectedTemplate() {
		return selectedTemplate;
	}

	public void setSelectedTemplate(TplDocMaster selectedTemplate) {
		this.selectedTemplate = selectedTemplate;
	}

	public String getSelectedVariable() {
		return selectedVariable;
	}

	public void setSelectedVariable(String selectedVariable) {
		this.selectedVariable = selectedVariable;
	}

	public List<TplDocSection> getDeletedList() {
		return deletedList;
	}

	public void setDeletedList(List<TplDocSection> deletedList) {
		this.deletedList = deletedList;
	}

	public TreeNode getDocSectionTN() {
		return docSectionTN;
	}

	public void setDocSectionTN(TreeNode docSectionTN) {
		this.docSectionTN = docSectionTN;
	}

	public List<TplDocMaster> getTemplateMasterList() {
		return templateMasterList;
	}

	public void setTemplateMasterList(List<TplDocMaster> templateMasterList) {
		this.templateMasterList = templateMasterList;
	}

	public String getSelectedSectionName() {
		return selectedSectionName;
	}

	public void setSelectedSectionName(String selectedSectionName) {
		this.selectedSectionName = selectedSectionName;
	}

	public String getSelectedOption() {
		return selectedOption;
	}

	public void setSelectedOption(String selectedOption) {
		this.selectedOption = selectedOption;
	}

	public List<TplDocSection> getVariableList() {
		return variableList;
	}

	public void setVariableList(List<TplDocSection> variableList) {
		this.variableList = variableList;
	}

	public TplDocMaster getSelectedImpMaster() {
		return selectedImpMaster;
	}

	public void setSelectedImpMaster(TplDocMaster selectedImpMaster) {
		this.selectedImpMaster = selectedImpMaster;
	}

	public List<TplDocMaster> getImportMasterList() {
		if(importMasterList == null){
			importMasterList = new ArrayList<TplDocMaster>();
		}
		return importMasterList;
	}

	public void setImportMasterList(List<TplDocMaster> importMasterList) {
		this.importMasterList = importMasterList;
	}

	public String getPreviewValue() {
		return previewValue;
	}

	public void setPreviewValue(String previewValue) {
		this.previewValue = previewValue;
	}

	public TreeNode getImptSectionTN() {
		return imptSectionTN;
	}

	public void setImptSectionTN(TreeNode imptSectionTN) {
		this.imptSectionTN = imptSectionTN;
	}

	public TreeNode getSelectedImpSection() {
		return selectedImpSection;
	}

	public void setSelectedImpSection(TreeNode selectedImpSection) {
		this.selectedImpSection = selectedImpSection;
	}

	public Map<TplDocSection, TreeNode> getDocSectionMap() {
		return docSectionMap;
	}

	public void setDocSectionMap(Map<TplDocSection, TreeNode> docSectionMap) {
		this.docSectionMap = docSectionMap;
	}

	public List<TplDocSection> getImportedSectionList() {
		return importedSectionList;
	}

	public void setImportedSectionList(List<TplDocSection> importedSectionList) {
		this.importedSectionList = importedSectionList;
	}

	public List<SelectItem> getTemplateTypeList() {
		return templateTypeList;
	}

	public void setTemplateTypeList(List<SelectItem> templateTypeList) {
		this.templateTypeList = templateTypeList;
	}

	public Map<String, String> getTemplateTypeMap() {
		return templateTypeMap;
	}

	public void setTemplateTypeMap(Map<String, String> templateTypeMap) {
		this.templateTypeMap = templateTypeMap;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getImpCategoryId() {
		return impCategoryId;
	}

	public void setImpCategoryId(Long impCategoryId) {
		this.impCategoryId = impCategoryId;
	}

	public Long getDocMasterId() {
		return docMasterId;
	}

	public void setDocMasterId(Long docMasterId) {
		this.docMasterId = docMasterId;
	}

	public String getSelectedSectionContent() {
		return selectedSectionContent;
	}

	public void setSelectedSectionContent(String selectedSectionContent) {
		this.selectedSectionContent = selectedSectionContent;
	}

	public TplCategory getCategory() {
		return category;
	}

	public void setCategory(TplCategory category) {
		this.category = category;
	}

	public List<SelectItem> getTemplateCategoryList() {
		return templateCategoryList;
	}

	public void setTemplateCategoryList(List<SelectItem> templateCategoryList) {
		this.templateCategoryList = templateCategoryList;
	}

	
	/*public List<TplSearch> getTemplateDocSearch() {
		return templateDocSearch;
	}

	public void setTemplateDocSearch(List<TplSearch> templateDocSearch) {
		this.templateDocSearch = templateDocSearch;
	}*/


	public String getProcessType() {
		if(processType == null)
			processType = "";
		return processType;
	}


	public void setProcessType(String processType) {
		this.processType = processType;
	}


	public String getTemplCategory() {
		return templCategory;
	}


	public void setTemplCategory(String templCategory) {
		this.templCategory = templCategory;
	}


	public List<TplDocMaster> getTemplateSearchList() {
		return templateSearchList;
	}


	public void setTemplateSearchList(List<TplDocMaster> templateSearchList) {
		this.templateSearchList = templateSearchList;
	}


	public String getSelectedSectionText() {
		return selectedSectionText;
	}


	public void setSelectedSectionText(String selectedSectionText) {
		this.selectedSectionText = selectedSectionText;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}


	public SelectItem[] getTemplateProcessTypeList() {
		return templateProcessTypeList;
	}


	public void setTemplateProcessTypeList(SelectItem[] templateProcessTypeList) {
		this.templateProcessTypeList = templateProcessTypeList;
	}


	public String getProcessValue() {
		return processValue;
	}


	public void setProcessValue(String processValue) {
		this.processValue = processValue;
	}

	public String getExternalPageUrl() {
		return externalPageUrl;
	}

	public void setExternalPageUrl(String externalPageUrl) {
		this.externalPageUrl = externalPageUrl;
	}
	
	public CtAgreementContent getCtAgreementContent() {
		return ctAgreementContent;
	}

	public void setCtAgreementContent(CtAgreementContent ctAgreementContent) {
		this.ctAgreementContent = ctAgreementContent;
	}
}