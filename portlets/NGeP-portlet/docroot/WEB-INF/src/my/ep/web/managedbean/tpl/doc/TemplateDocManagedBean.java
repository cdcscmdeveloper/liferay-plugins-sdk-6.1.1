/**
 * Copyright © 2012 Archisoft. All rights reserved.
*/
package my.ep.web.managedbean.tpl.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.tpl.doc.ITemplateDocSO;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.tpl.doc.TemplateDocReqDTO;
import my.ep.dto.tpl.doc.TemplateDocResDTO;
import my.ep.persistence.entity.ct.agreement.CtAgreementContent;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.persistence.entity.tpl.doc.TplDocSection;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

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

	private boolean showTemplateList;
	
	private Long procurementCategoryId;
	private Long processTypeId;
	private Long impCategoryId;
	private Long impProcessTypeId;
	private Long docMasterId;
	private String title;
	private String desc;
	private String impTitle;
	
	private String processType;
	private String externalPageUrl;
	private String selectedSectionTitle;
	private String selectedSectionContent;
	private String selectedSectionName;
	private String selectedSectionText;
	private String selectedOption;
	private String previewValue;
	
	private CtAgreementContent ctAgreementContent; 
	private TplDocMaster selectedTemplate;
	private TplDocMaster selectedImpMaster;
	private TplDocSection selectedSection;
	private TreeNode docSectionTN;
	private TreeNode imptSectionTN;
	private TreeNode selectedImpSection;
	
	private List<TplDocMaster> importMasterList;
	private List<TplDocSection> variableList;
	private List<TplDocSection> deletedList;
	private List<TplDocSection> importedSectionList;
	private Map<TplDocSection, TreeNode> docSectionMap;
	
	private TreeNode [] treenode;
	private boolean createNew = false;
	private boolean readOnlyFlag = false;
	private Map<String,String> docParamValues;
	
	
	private TreeNode treenodeObject;
	
	

	public TreeNode[] getTreenode() {
		return treenode;
	}

	public void setTreenode(TreeNode[] treenode) {
		this.treenode = treenode;
	}

	private IPagingDataModel<TplDocMaster> docMasterDataModel = new DocMasterPagingDataModel();
	
	private class DocMasterPagingDataModel extends PagingDataModel<TplDocMaster> {
		private static final long serialVersionUID = 1L;
		public DocMasterPagingDataModel() {
			super("tplDocMasterDt", true);
		}
		
		@Override
		public ISearchResultBean<TplDocMaster> pageChangeListener(ISearchBean<TplDocMaster> searchBean) {
			try {
				ITemplateDocSO templateDocSO = (ITemplateDocSO)BusinessDelegate.getInstance().getServiceObject("cc",ITemplateDocSO.class);
				TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();
				
				if(null != getProcurementCategoryId()){
					searchBean.getDataFilterBeans().add(new SearchFilterBean("procurementCategory.parameterId",SearchOperator.OPERATOR_EQUALS.value(), getProcurementCategoryId()));
				}
				searchBean.getDataFilterBeans().add(new SearchFilterBean("processType.parameterCode",SearchOperator.OPERATOR_EQUALS.value(),ParameterConst.TPL_DOC_PROCESS_TYPE_STANDARD));
				if(null != getTitle()){
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docTitle",SearchOperator.OPERATOR_LIKE.value(), getTitle(), true));
				}
				
				reqDTO.setSearchBean(searchBean);
				TemplateDocResDTO resDTO = templateDocSO.searchTemplateDoc(reqDTO);
				
				if(resDTO.isSuccessful()) {
					return resDTO.getDocMasterResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	/**
	 * TemplateManagedBean constructor.
	 */	
	public TemplateDocManagedBean() {
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

	public TplDocMaster getSelectedTemplate() {
		return selectedTemplate;
	}

	public void setSelectedTemplate(TplDocMaster selectedTemplate) {
		this.selectedTemplate = selectedTemplate;
	}

	public TreeNode getDocSectionTN() {
		return docSectionTN;
	}

	public void setDocSectionTN(TreeNode docSectionTN) {
		this.docSectionTN = docSectionTN;
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

	public Long getImpCategoryId() {
		return impCategoryId;
	}

	public void setImpCategoryId(Long impCategoryId) {
		this.impCategoryId = impCategoryId;
	}

	public String getSelectedSectionContent() {
		return selectedSectionContent;
	}

	public void setSelectedSectionContent(String selectedSectionContent) {
		this.selectedSectionContent = selectedSectionContent;
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

	public Long getProcurementCategoryId() {
		return procurementCategoryId;
	}

	public void setProcurementCategoryId(Long procurementCategoryId) {
		this.procurementCategoryId = procurementCategoryId;
	}

	public Long getProcessTypeId() {
		return processTypeId;
	}

	public void setProcessTypeId(Long processTypeId) {
		this.processTypeId = processTypeId;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getProcessType() {
		return processType;
	}


	public void setProcessType(String processType) {
		this.processType = processType;
	}


	public Long getImpProcessTypeId() {
		return impProcessTypeId;
	}


	public void setImpProcessTypeId(Long impProcessTypeId) {
		this.impProcessTypeId = impProcessTypeId;
	}


	public String getImpTitle() {
		return impTitle;
	}


	public void setImpTitle(String impTitle) {
		this.impTitle = impTitle;
	}

	public String getSelectedSectionText() {
		return selectedSectionText;
	}

	public void setSelectedSectionText(String selectedSectionText) {
		this.selectedSectionText = selectedSectionText;
	}

	public List<TplDocSection> getDeletedList() {
		if(deletedList==null) {
			deletedList = new ArrayList<TplDocSection>();
		}
		return deletedList;
	}

	public void setDeletedList(List<TplDocSection> deletedList) {
		this.deletedList = deletedList;
	}

	public List<TplDocSection> getImportedSectionList() {
		if(importedSectionList==null) {
			importedSectionList = new ArrayList<TplDocSection>();
		}
		return importedSectionList;
	}

	public void setImportedSectionList(List<TplDocSection> importedSectionList) {
		this.importedSectionList = importedSectionList;
	}

	public Map<TplDocSection, TreeNode> getDocSectionMap() {
		return docSectionMap;
	}

	public void setDocSectionMap(Map<TplDocSection, TreeNode> docSectionMap) {
		this.docSectionMap = docSectionMap;
	}

	public Long getDocMasterId() {
		return docMasterId;
	}

	public void setDocMasterId(Long docMasterId) {
		this.docMasterId = docMasterId;
	}

	public boolean isShowTemplateList() {
		return showTemplateList;
	}

	public void setShowTemplateList(boolean showTemplateList) {
		this.showTemplateList = showTemplateList;
	}

	public TreeNode getSelectedImpSection() {
		return selectedImpSection;
	}

	public void setSelectedImpSection(TreeNode selectedImpSection) {
		this.selectedImpSection = selectedImpSection;
	}

	public IPagingDataModel<TplDocMaster> getDocMasterDataModel() {
		return docMasterDataModel;
	}

	public void setDocMasterDataModel(IPagingDataModel<TplDocMaster> docMasterDataModel) {
		this.docMasterDataModel = docMasterDataModel;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

	public boolean isReadOnlyFlag() {
		return readOnlyFlag;
	}

	public void setReadOnlyFlag(boolean readOnlyFlag) {
		this.readOnlyFlag = readOnlyFlag;
	}

	/**
	 * @return the treenodeObject
	 */
	public TreeNode getTreenodeObject() {
		return treenodeObject;
	}

	/**
	 * @param treenodeObject the treenodeObject to set
	 */
	public void setTreenodeObject(TreeNode treenodeObject) {
		this.treenodeObject = treenodeObject;
	}

	/**
	 * @return the docParamValues
	 */
	public Map<String, String> getDocParamValues() {
		return docParamValues;
	}

	/**
	 * @param docParamValues the docParamValues to set
	 */
	public void setDocParamValues(Map<String, String> docParamValues) {
		this.docParamValues = docParamValues;
	}

	


	
	
	
	
	
	
	
}