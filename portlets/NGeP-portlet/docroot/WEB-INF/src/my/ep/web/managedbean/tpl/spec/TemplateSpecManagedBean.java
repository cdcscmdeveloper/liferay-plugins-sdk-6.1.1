/**
 * 
 */
package my.ep.web.managedbean.tpl.spec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.type.tpl.doc.AnswerType;
import my.ep.persistence.entity.tpl.spec.TplOptionChoice;
import my.ep.persistence.entity.tpl.spec.TplOptionGroup;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecScoring;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
//import org.richfaces.component.UIDataTable;
//import org.richfaces.component.UIExtendedDataTable;

/**
 * @author kian seng
 *
 */

@ManagedBean(name="templateSpecMB")
@SessionScoped
public class TemplateSpecManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private Map<String, String> selectedIdxMap = new HashMap<String, String>();
	private Map<String, Integer> colspanMap = new HashMap<String, Integer>();
	private Map<String, Integer> colnoMap = new HashMap<String, Integer>(); //defines number of columns
	
	private Map<String, List<TplSpecQuestion>> questionListMap;// = new HashMap<String, List<TplSpecQuestion>>(); //define number of tables
	private List<TplSpecQuestion> deletedQuestions = new ArrayList<TplSpecQuestion>();
	
	private String tableIdToDelete;
	private String tableIndexToDelete;
	private Integer rowToDelete;
	
	private Long specMasterId;
	private Map<Long, List<SelectItem>> optionMap = new HashMap<Long, List<SelectItem>>();
	private List<TplOptionGroup> optionGroupList = new ArrayList<TplOptionGroup>();
	private TplSpecMaster specMaster;
	
	private Long categoryId;
	private TplSpecMaster selectedTemplate;
	private List<TplSpecMaster> templateSpecMasterList;
	private boolean showTemplateList = false;
	private TplSpecMaster selectedRow;
	private Integer selectedAnswerType;
	private String optionGroupName;
	private List<TplOptionGroup> ansOptionGroupList;
	private TplOptionGroup selectedAnsGroup;
	private String newOptGroupName;
	private TplOptionChoice selectedChoice;
	private List<TplOptionChoice> newOptChoiceList;
	private TplSpecQuestion selectedSpecQuestion;
	private String scoreWeight;
	private List<TplSpecScoring> specScoringList; 
	private TplSpecScoring selectedScore;
	private TplSpecScoring specScore;
	private boolean checkScore;
	private List<Long> deleteQuestionSeqList;
	private List<TplOptionChoice> ansOptionChoiceList2;
	private List<TplOptionChoice> ansOptionChoiceList;
		
	public Map<String, String> getSelectedIdxMap() {
		return selectedIdxMap;
	}

	public void setSelectedIdxMap(Map<String, String> selectedIdxMap) {
		this.selectedIdxMap = selectedIdxMap;
	}

	public Map<String, Integer> getColspanMap() {
		return colspanMap;
	}

	public void setColspanMap(Map<String, Integer> colspanMap) {
		this.colspanMap = colspanMap;
	}

	public Map<String, Integer> getColnoMap() {
		return colnoMap;
	}

	public void setColnoMap(Map<String, Integer> colnoMap) {
		this.colnoMap = colnoMap;
	}

	public Map<String, List<TplSpecQuestion>> getQuestionListMap() {
		return questionListMap;
	}

	public void setQuestionListMap(
			Map<String, List<TplSpecQuestion>> questionListMap) {
		this.questionListMap = questionListMap;
	}

	public List<TplSpecQuestion> getDeletedQuestions() {
		return deletedQuestions;
	}

	public void setDeletedQuestions(List<TplSpecQuestion> deletedQuestions) {
		this.deletedQuestions = deletedQuestions;
	}

	public String getTableIdToDelete() {
		return tableIdToDelete;
	}

	public void setTableIdToDelete(String tableIdToDelete) {
		this.tableIdToDelete = tableIdToDelete;
	}

	public Integer getRowToDelete() {
		return rowToDelete;
	}

	public void setRowToDelete(Integer rowToDelete) {
		this.rowToDelete = rowToDelete;
	}

	public String getTableIndexToDelete() {
		return tableIndexToDelete;
	}

	public void setTableIndexToDelete(String tableIndexToDelete) {
		this.tableIndexToDelete = tableIndexToDelete;
	}

	public Map<Long, List<SelectItem>> getOptionMap() {
		return optionMap;
	}

	public void setOptionMap(Map<Long, List<SelectItem>> optionMap) {
		this.optionMap = optionMap;
	}

	public List<TplOptionGroup> getOptionGroupList() {
		return optionGroupList;
	}

	public void setOptionGroupList(List<TplOptionGroup> optionGroupList) {
		this.optionGroupList = optionGroupList;
	}
	
	String previewUrl;

	public String getPreviewUrl() {
		return previewUrl;
	}

	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}

	public TplSpecMaster getSpecMaster() {
		return specMaster;
	}

	public void setSpecMaster(TplSpecMaster specMaster) {
		this.specMaster = specMaster;
	}
	
	private String pageUrl;

	public String getPageUrl() {
		String loadFirstPage = new FacesScopes().getRequest().getParameter("loadFirstPage");
		if(loadFirstPage == null) {
			pageUrl = "search.xhtml";
		}
//		if(pageUrl == null) {
//			pageUrl = "search.xhtml";
//		}
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public List<TplSpecMaster> getTemplateSpecMasterList() {
		return templateSpecMasterList;
	}

	public void setTemplateSpecMasterList(List<TplSpecMaster> templateSpecMasterList) {
		this.templateSpecMasterList = templateSpecMasterList;
	}

	public boolean isShowTemplateList() {
		return showTemplateList;
	}

	public void setShowTemplateList(boolean showTemplateList) {
		this.showTemplateList = showTemplateList;
	}

	public TplSpecMaster getSelectedTemplate() {
		return selectedTemplate;
	}

	public void setSelectedTemplate(TplSpecMaster selectedTemplate) {
		this.selectedTemplate = selectedTemplate;
	}

	public TplSpecMaster getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(TplSpecMaster selectedRow) {
		this.selectedRow = selectedRow;
	}

	public Integer getSelectedAnswerType() {
		return selectedAnswerType;
	}

	public void setSelectedAnswerType(Integer selectedAnswerType) {
		this.selectedAnswerType = selectedAnswerType;
	}

	public String getOptionGroupName() {
		return optionGroupName;
	}

	public void setOptionGroupName(String optionGroupName) {
		this.optionGroupName = optionGroupName;
	}

	public List<TplOptionGroup> getAnsOptionGroupList() {
		return ansOptionGroupList;
	}

	public void setAnsOptionGroupList(List<TplOptionGroup> ansOptionGroupList) {
		this.ansOptionGroupList = ansOptionGroupList;
	}

	public TplOptionGroup getSelectedAnsGroup() {
		return selectedAnsGroup;
	}

	public void setSelectedAnsGroup(TplOptionGroup selectedAnsGroup) {
		this.selectedAnsGroup = selectedAnsGroup;
	}

	public String getNewOptGroupName() {
		return newOptGroupName;
	}

	public void setNewOptGroupName(String newOptGroupName) {
		this.newOptGroupName = newOptGroupName;
	}

	public TplOptionChoice getSelectedChoice() {
		return selectedChoice;
	}

	public void setSelectedChoice(TplOptionChoice selectedChoice) {
		this.selectedChoice = selectedChoice;
	}

	public List<TplOptionChoice> getNewOptChoiceList() {
		return newOptChoiceList;
	}

	public void setNewOptChoiceList(List<TplOptionChoice> newOptChoiceList) {
		this.newOptChoiceList = newOptChoiceList;
	}

	public TplSpecQuestion getSelectedSpecQuestion() {
		if(selectedSpecQuestion == null){
			selectedSpecQuestion = new TplSpecQuestion();
		}
		if(selectedSpecQuestion.getAnswerType() == null){
			selectedSpecQuestion.setAnswerType(AnswerType.TEXT.value());
		}
		return selectedSpecQuestion;
	}

	public void setSelectedSpecQuestion(TplSpecQuestion selectedSpecQuestion) {
		System.out.println("selectedSpecQuestion.getAnswerType(): "+selectedSpecQuestion.getAnswerType());
		this.selectedSpecQuestion = selectedSpecQuestion;
	}

	public String getScoreWeight() {
		return scoreWeight;
	}

	public void setScoreWeight(String scoreWeight) {
		this.scoreWeight = scoreWeight;
	}

	public List<TplSpecScoring> getSpecScoringList() {
		return specScoringList;
	}

	public void setSpecScoringList(List<TplSpecScoring> specScoringList) {
		this.specScoringList = specScoringList;
	}

	public TplSpecScoring getSelectedScore() {
		return selectedScore;
	}

	public void setSelectedScore(TplSpecScoring selectedScore) {
		this.selectedScore = selectedScore;
	}

	public TplSpecScoring getSpecScore() {
		return specScore;
	}

	public void setSpecScore(TplSpecScoring specScore) {
		this.specScore = specScore;
	}

	public boolean isCheckScore() {
		return checkScore;
	}

	public void setCheckScore(boolean checkScore) {
		this.checkScore = checkScore;
	}

	public List<Long> getDeleteQuestionSeqList() {
		if(deleteQuestionSeqList == null){
			deleteQuestionSeqList = new ArrayList<Long>();
		}
		return deleteQuestionSeqList;
	}

	public void setDeleteQuestionSeqList(List<Long> deleteQuestionSeqList) {
		this.deleteQuestionSeqList = deleteQuestionSeqList;
	}

	public Long getSpecMasterId() {
		return specMasterId;
	}

	public void setSpecMasterId(Long specMasterId) {
		this.specMasterId = specMasterId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public List<TplOptionChoice> getAnsOptionChoiceList2() {
		return ansOptionChoiceList2;
	}

	public void setAnsOptionChoiceList2(List<TplOptionChoice> ansOptionChoiceList2) {
		this.ansOptionChoiceList2 = ansOptionChoiceList2;
	}

	public List<TplOptionChoice> getAnsOptionChoiceList() {
		return ansOptionChoiceList;
	}

	public void setAnsOptionChoiceList(List<TplOptionChoice> ansOptionChoiceList) {
		this.ansOptionChoiceList = ansOptionChoiceList;
	}

}
