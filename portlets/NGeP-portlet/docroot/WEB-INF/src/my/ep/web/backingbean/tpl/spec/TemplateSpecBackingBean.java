/**
 * 
 */
package my.ep.web.backingbean.tpl.spec;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.persistence.entity.tpl.TplCategory;
import my.ep.persistence.entity.tpl.spec.TplOptionChoice;
import my.ep.persistence.entity.tpl.spec.TplOptionGroup;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecScoring;
import my.ep.persistence.logging.LogKey;
import my.ep.type.tpl.doc.AnswerType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.tpl.spec.TemplateSpecManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

/**
 * @author kian seng
 *
 */
@ManagedBean(name="templateSpecBackingBean")
//@RequestScoped
@ViewScoped
public class TemplateSpecBackingBean extends BaseBackingBean<TemplateSpecManagedBean> {

	private static Logger log = Logger.getLogger(TemplateSpecBackingBean.class);

//	public static void main(String[] args) {
//		ITemplateSpecSO templateSpecSO = null;
//		try {
//			templateSpecSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject(ITemplateSpecSO.class);
//
//			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
//			reqDTO.setTableSeq(1L);
//			reqDTO.setMasterId(3L);
//			TemplateSpecResDTO resDTO = templateSpecSO.findQuestionsByMasterId(reqDTO);
//			for(int i = 1; i <= resDTO.getQuestionMap().size(); i++) {
//				List<TplSpecQuestion> questionList = resDTO.getQuestionMap().get(String.valueOf(i));
//				for(TplSpecQuestion question : questionList) {
//					System.out.println("question: " + question);
//				}
//			}
//
//		} finally {
//			templateSpecSO = null;
//		}
//	}
	public TemplateSpecBackingBean(){
		  if(MDC.get(LogKey.USER_ID)==null) {
			    MDC.put(LogKey.USER_ID, 1L);
			   }
	}
	
	public String findQuestionsBySectionId() {
		ITemplateSpecSO templateSpecSO = null;
		TplSpecMaster specMaster = this.getManagedBean().getSelectedTemplate();
		try {

			templateSpecSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject(ITemplateSpecSO.class);

			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			System.out.println("specMaster.getSpecMasterId(): "+specMaster.getSpecMasterId());
			reqDTO.setMasterId(specMaster.getSpecMasterId());
			if(this.getManagedBean().getOptionMap().size() <= 0) {
				reqDTO.setFetchOptions(true);
			}

			if(StringUtils.isNotBlank(this.getSupplierFlag()) && this.getSupplierFlag().equals("Y")) {
				reqDTO.setSupplierId(Long.valueOf(new FacesScopes().getRequest().getRemoteUser()));
			}

			TemplateSpecResDTO resDTO = templateSpecSO.findQuestionsByMasterId(reqDTO);

			if(resDTO.isSuccessful()) {
				this.getManagedBean().setSpecMaster(resDTO.getSpecMaster());

				this.getManagedBean().setQuestionListMap(resDTO.getQuestionMap());
				for(int i = 1; i <= resDTO.getQuestionMap().size(); i++) {
					List<TplSpecQuestion> questionList = resDTO.getQuestionMap().get(String.valueOf(i));
					if(questionList != null && questionList.size() > 0) {					

						TplSpecQuestion headerBean = questionList.get(0);
						System.out.println("headerBean.getQuestionColumnNo(): "+headerBean.getQuestionColumnNo());
						int colspan = headerBean.getQuestionColumnNo();
						if(headerBean.isAnswer() == true) {
							colspan++;
						}
						if(headerBean.isLastAnswer() == true) {
							colspan++;
						}
						this.getManagedBean().getColnoMap().put("sec" + i, headerBean.getQuestionColumnNo());
						this.getManagedBean().getColspanMap().put("sec" + i, colspan);
					}
				}

				if(this.getManagedBean().getOptionMap().size() <= 0) {
					if(resDTO.getOptionGroupList() != null && !resDTO.getOptionGroupList().isEmpty()) {
						this.getManagedBean().setOptionGroupList(resDTO.getOptionGroupList());
						for(TplOptionGroup optionGroup : resDTO.getOptionGroupList()) {
							ArrayList<SelectItem> itemList = new ArrayList<SelectItem>();
							for(TplOptionChoice optionChoice : optionGroup.getTplOptionChoices()) {
								itemList.add(new SelectItem(optionChoice.getOptionChoiceId(), optionChoice.getChoiceName()));
							}
							this.getManagedBean().getOptionMap().put(Long.valueOf(optionGroup.getOptionGroupId()), itemList);
						}
					}
				}
			}
		} finally {
			templateSpecSO = null;
		}
		return "maintenance";
	}

	public void addQuestion(Integer sectionId,Integer rowId, Integer questionType ) {
		log.debug("In addQuestion");
		List<TplSpecQuestion> questionList = this.getManagedBean().getQuestionListMap().get((String.valueOf(sectionId)));
		this.addQuestion2(questionList, rowId, sectionId, questionType);
		log.debug("Exit addQuestion");
	}

	public void addQuestion2(List<TplSpecQuestion> questionList, Integer row, Integer section, Integer questionType) {

		TplSpecQuestion newQuestion = new TplSpecQuestion();
		newQuestion.setQuestionType(questionType);
		newQuestion.setRecordStatus(true);
		newQuestion.setBrandNew(true);
		newQuestion.setCreatedBy(1l);
		newQuestion.setTableSeq(section);
		newQuestion.setCreatedDate(new Timestamp(System.currentTimeMillis()));

		// adding default option
		newQuestion.setAnswerType(AnswerType.TEXT.value());
		newQuestion.setTplOptionGroup(this.getManagedBean().getOptionGroupList().get(0));
		questionList.add(row + 1, newQuestion);
		for(int i = row + 1; i < questionList.size(); i++) {
			TplSpecQuestion question = questionList.get(i);
			question.setQuestionOrder(i);
		}
	}

	Long questionId;
	String tableId;
	Integer rowId;
	Integer sectionId;
	Integer questionType;
	Integer numberOfColumns;
	boolean answer;
	boolean lastAnswer;
	String supplierFlag;
	Integer inputType;
	Long optionGroupId;

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public Integer getRowId() {
		return rowId;
	}

	public void setRowId(Integer rowId) {
		this.rowId = rowId;
	}

	public Integer getSectionId() {
		return sectionId;
	}

	public void setTableId(Integer sectionId) {
		this.sectionId = sectionId;
	}

	public Integer getQuestionType() {
		return questionType;
	}

	public void setQuestionType(Integer questionType) {
		this.questionType = questionType;
	}

	public Integer getNumberOfColumns() {
		return numberOfColumns;
	}

	public void setNumberOfColumns(Integer numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}


	public Long getOptionGroupId() {
		return optionGroupId;
	}

	public void setOptionGroupId(Long optionGroupId) {
		this.optionGroupId = optionGroupId;
	}

	public Integer getAnswerType() {
		return inputType;
	}

	public void setAnswerType(Integer inputType) {
		this.inputType = inputType;
	}

	public String getSupplierFlag() {
		return supplierFlag;
	}

	public void setSupplierFlag(String supplierFlag) {
		this.supplierFlag = supplierFlag;
	}

	public boolean isAnswer() {
		return answer;
	}

	public void setAnswer(boolean answer) {
		this.answer = answer;
	}

	public boolean isLastAnswer() {
		return lastAnswer;
	}

	public void setLastAnswer(boolean lastAnswer) {
		this.lastAnswer = lastAnswer;
	}

	/**
	 * save the current template spec
	 */
	public void saveAction() {
		ITemplateSpecSO templateSpecSO = null;
		try {
			
			templateSpecSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject(ITemplateSpecSO.class);

			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			
//			new LogKey(new FacesScopes().getUserScreenName());
			this.getManagedBean().getQuestionListMap().put("0", this.getManagedBean().getDeletedQuestions());
			reqDTO.setQuestionListMap(this.getManagedBean().getQuestionListMap());
			reqDTO.setSpecMaster(this.getManagedBean().getSelectedTemplate());
			reqDTO.setDeleteQuestionSeqList(getManagedBean().getDeleteQuestionSeqList());
			reqDTO.setSpecScoringList(this.getManagedBean().getSelectedSpecQuestion().getTplSpecScorings());
			TemplateSpecResDTO resDTO = templateSpecSO.saveTemplateSpec(reqDTO);		

			if(resDTO.isSuccessful()) {
				this.renewSesionObject();
				this.getManagedBean().setSpecMasterId(resDTO.getSpecMasterId());
				this.getManagedBean().setSelectedTemplate(resDTO.getSpecMaster());
				findQuestionsBySectionId();
			}
		} finally {
			templateSpecSO = null;
		}
	}

	public void saveAnswerAction() {
		ITemplateSpecSO templateSpecSO = null;
		FacesScopes facesScopes = new FacesScopes();
		try {
			templateSpecSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject(ITemplateSpecSO.class);

			List<TplSpecAnswer> answerList = new ArrayList<TplSpecAnswer>();
			for(int i = 1; i <= this.getManagedBean().getQuestionListMap().size(); i++) {
				List<TplSpecQuestion> questionList = this.getManagedBean().getQuestionListMap().get(String.valueOf(i));
				for(TplSpecQuestion question : questionList) {
					if(question.getTplSpecAnswer() != null) {

						if(question.getTplSpecAnswer().isBrandNew()) {
							question.getTplSpecAnswer().setRecordStatus(true);
							question.getTplSpecAnswer().setCreatedBy(1l);
							question.getTplSpecAnswer().setSupplierId(Long.valueOf(facesScopes.getRequest().getRemoteUser()));
							question.getTplSpecAnswer().setTplSpecQuestion(question);
						}

						answerList.add(question.getTplSpecAnswer());
					}
				}
			}

			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			reqDTO.setMasterId(this.getManagedBean().getSpecMasterId());
			reqDTO.setSupplierId(Long.valueOf(facesScopes.getRequest().getRemoteUser()));
			reqDTO.setAnswerList(answerList);

			TemplateSpecResDTO resDTO = templateSpecSO.saveTemplateSpecAnswer(reqDTO);

			if(resDTO.isSuccessful()) {
				this.getManagedBean().setQuestionListMap(resDTO.getQuestionMap());

				for(int i = 0; i < resDTO.getQuestionMap().size(); i++) {
					List<TplSpecQuestion> questionList = resDTO.getQuestionMap().get(String.valueOf(i + 1));

					if(questionList != null && questionList.size() > 0) {					

						TplSpecQuestion headerBean = questionList.get(0);					

						int colspan = headerBean.getQuestionColumnNo();
						if(headerBean.isAnswer() == true) {
							colspan++;
						}
						if(headerBean.isLastAnswer() == true) {
							colspan++;
						}
						this.getManagedBean().getColnoMap().put("sec" + (i+1), headerBean.getQuestionColumnNo());
						this.getManagedBean().getColspanMap().put("sec" + (i+1), colspan);
					}
				}
			}

		} finally {
			templateSpecSO = null;
		}
	}

	public void createTemplateTbl() {
		List<TplSpecQuestion> questionList = new ArrayList<TplSpecQuestion>();
		if(this.getManagedBean().getQuestionListMap() == null){
			this.getManagedBean().setQuestionListMap(new HashMap<String, List<TplSpecQuestion>>());
		}

		Integer newSectionId = this.getManagedBean().getQuestionListMap().size() + 1;

		int colspan = numberOfColumns;

		TplSpecQuestion header = new TplSpecQuestion();
		header.setQuestionType(1);
		header.setRecordStatus(true);
		header.setBrandNew(true);
		header.setTableSeq(newSectionId);
		header.setQuestionColumnNo(numberOfColumns);
		header.setQuestionOrder(0);

		// set default text
		header.setQuestionNumber("[No.]");
		header.setQuestion1("[Question 1]");
		header.setQuestion2("[Question 2]");
		header.setQuestion3("[Question 3]");
		header.setQuestion4("[Question 4]");
		header.setQuestion5("[Question 5]");
		header.setAnswerHeader("[Answer]");
		header.setLastAnswerHeader("[Remarks]");		

		if(this.answer) {
			header.setAnswer(true);
			colspan++;
		}
		if(this.lastAnswer) {
			header.setLastAnswer(true);
			colspan++;
		}

		questionList.add(header);

		this.getManagedBean().getColnoMap().put("sec" + newSectionId, numberOfColumns);
		this.getManagedBean().getColspanMap().put("sec" + newSectionId, colspan);
		this.getManagedBean().getQuestionListMap().put(String.valueOf(newSectionId), questionList);
	}

	public void deleteQuestion() {
		System.out.println("this.getManagedBean().getTableIndexToDelete(): "+this.getManagedBean().getTableIndexToDelete());
		List<TplSpecQuestion> dtValue = this.getManagedBean().getQuestionListMap().get(this.getManagedBean().getTableIndexToDelete());

		if(dtValue != null & !dtValue.isEmpty()) {
			TplSpecQuestion questionToDelete = dtValue.get(this.getManagedBean().getRowToDelete());
			if(questionToDelete.getQuestionType() == 1) {
				for(TplSpecQuestion question : dtValue) {
					question.setDeleted(true);
					this.getManagedBean().getDeletedQuestions().add(question);
				}

				dtValue.clear();	
				this.getManagedBean().setTableIdToDelete(null);
				this.getManagedBean().setRowToDelete(null);


				for(int i = Integer.valueOf(this.getManagedBean().getTableIndexToDelete()) + 1; i <= this.getManagedBean().getQuestionListMap().size(); i++) {

					String prevKey = String.valueOf(i - 1);
					String currentKey = String.valueOf(i);

					List<TplSpecQuestion> questions = this.getManagedBean().getQuestionListMap().get(currentKey);
					for(TplSpecQuestion question : questions) {
						question.setTableSeq(i - 1);
					}

					this.getManagedBean().getQuestionListMap().put(prevKey, questions);
				}
				this.getManagedBean().getQuestionListMap().remove(String.valueOf(this.getManagedBean().getQuestionListMap().size()));

			}

			questionToDelete.setDeleted(true);
			this.getManagedBean().getDeletedQuestions().add(questionToDelete);

			if(questionToDelete.getQuestionType() == 2) {
				for(int i = this.getManagedBean().getRowToDelete() + 1; i < dtValue.size(); i++) {
					TplSpecQuestion childToDelete = dtValue.get(i);
					if(childToDelete.getQuestionType() == 2) {
						break;
					}
					childToDelete.setDeleted(true);
					this.getManagedBean().getDeletedQuestions().add(childToDelete);
					dtValue.remove(childToDelete);
					i--;
				}
			}

			dtValue.remove(questionToDelete);
			log.debug("this.getManagedBean().getDeletedQuestions(): " + this.getManagedBean().getDeletedQuestions());
			System.out.println(">>>>>>>>>>>>>>>>>>>> this.getManagedBean().getDeletedQuestions().size(): " + this.getManagedBean().getDeletedQuestions().size());
			this.getManagedBean().setTableIdToDelete(null);
			this.getManagedBean().setRowToDelete(null);
		}
	}

	public void changeInput(Integer sectionId,Integer rowId, Integer inputType,Long optionGroupId ) {
		log.debug("In changeInput");
		//		FacesContext context = FacesContext.getCurrentInstance();
		//		Map map = context.getExternalContext().getRequestParameterMap();
		//		String sectionId = (String)map.get("sectionId");
		//		String rowId = (String)map.get("rowId");
		//		String inputType = (String)map.get("inputType");
		//		String optionGroup = (String)map.get("optionGroup");
		log.info("this.getSectionId(): " + sectionId);
		log.info("this.getRowId(): " +rowId);
		log.info("this.getAnswerType(): " + this.getAnswerType());
		log.info("this.getOptionGroupId(): " + this.getOptionGroupId());

		List<TplSpecQuestion> questionList = this.getManagedBean().getQuestionListMap().get(String.valueOf(this.getSectionId()));
		TplSpecQuestion question = questionList.get(this.getRowId());
		question.setAnswerType(this.getAnswerType());

		if(this.getOptionGroupId() != null) {
			question.setTplOptionGroup(this.getManagedBean().getOptionGroupList().get(0));
		}

		log.debug("Exit changeInput");
	}

	public void preview() {
		log.debug("In preview.");
		this.getManagedBean().setPreviewUrl("/xhtml/tpl/templatespec-answer/maintenance.xhtml");
	}

	public void postPreview() {
		log.debug("In postPreview.");
		this.getManagedBean().setPreviewUrl(null);		
	}

	public String newAction() {

		//TODO: retrieve optionGroup is not yet loaded into memory 
		if(this.getManagedBean().getQuestionListMap() != null) {
			this.getManagedBean().getQuestionListMap().clear();
		} else {
			this.getManagedBean().setQuestionListMap(new HashMap<String, List<TplSpecQuestion>>());
		}

		TplSpecMaster specMaster = new TplSpecMaster();
		specMaster.setRecordStatus(true);
		specMaster.setBrandNew(true);
		specMaster.setCreatedBy(1l);
		specMaster.setTemplateType("MA");
		specMaster.setTplCategory(new TplCategory());

		this.getManagedBean().setSpecMaster(specMaster);
		this.getManagedBean().getColnoMap().clear();
		this.getManagedBean().getColspanMap().clear();
		this.getManagedBean().setSpecMasterId(null);

		return "maintenance"; 
	}

	public List<SelectItem> getTemplateCategoryList() {
		List<SelectItem> templateCategoryList = new ArrayList<SelectItem>();

		ITemplateSpecSO templateSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject(ITemplateSpecSO.class);	
		TemplateSpecResDTO resDTO = templateSO.getTemplateCategoryList(null);
		if(resDTO.getCategoryList() != null && !resDTO.getCategoryList().isEmpty()){
			templateCategoryList.add(new SelectItem("", "- Select One -"));
			List<TplCategory> categoryListDB = resDTO.getCategoryList();
			for(TplCategory obj : categoryListDB){
				templateCategoryList.add(new SelectItem(obj.getCategoryId(), obj.getCategoryDesc()));
			}
		}		

		return templateCategoryList;
	}

	public void searchResult(){

		if(getManagedBean().getCategoryId() != null && !getManagedBean().getCategoryId().equals("")){
			ITemplateSpecSO templateSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject(ITemplateSpecSO.class);

			log.info("getManagedBean().getTemplateCategoryID() === " + getManagedBean().getCategoryId());

			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			reqDTO.setCategoryId(getManagedBean().getCategoryId());
			reqDTO.setName(null);
			reqDTO.setType(null);
			reqDTO.setDesc(null);

			log.info("======== A ========= ");

			TemplateSpecResDTO resDTO = templateSO.getTemplateMasterList(reqDTO);

			log.info("======== B ========= ");

			if(resDTO.getSpecMasterList() != null && !resDTO.getSpecMasterList().isEmpty()){	
				Long templateMasterId = null;
				List<TplSpecMaster> docMasterListDB = resDTO.getSpecMasterList();
				this.getManagedBean().setTemplateSpecMasterList(docMasterListDB);
				for(TplSpecMaster masterId : docMasterListDB){
					templateMasterId = masterId.getSpecMasterId();
				}
				getManagedBean().setSpecMasterId(templateMasterId);
				getManagedBean().setShowTemplateList(true);
			}	
		}
	}
	public String createNewTemplate(){
		
		if(this.getManagedBean().getQuestionListMap() != null) {
			this.getManagedBean().getQuestionListMap().clear();
		} else {
			this.getManagedBean().setQuestionListMap(new HashMap<String, List<TplSpecQuestion>>());
		}
//		FacesScopes facesScopes = new FacesScopes();
		TplSpecMaster specMaster = new TplSpecMaster();
		specMaster.setBrandNew(Boolean.TRUE);
//		specMaster.setCreatedBy(1l);
//		specMaster.setCreatedDate(new Timestamp(System.currentTimeMillis()));
//		specMaster.setRecordStatus(true);
		specMaster.setTemplateType("M");
		
		this.getManagedBean().setSelectedTemplate(specMaster);
//		this.getManagedBean().setSpecMaster(specMaster);
		this.getManagedBean().getSelectedTemplate().setTplCategory(new TplCategory());
		this.getManagedBean().getColnoMap().clear();
		this.getManagedBean().getColspanMap().clear();
		
		ITemplateSpecSO templateSpecSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject(ITemplateSpecSO.class);
		
		TemplateSpecResDTO resDTO = templateSpecSO.findAllOptionGroup(null);
		this.getManagedBean().setOptionGroupList(resDTO.getOptionGroupList());
		return "maintenance";
	}

	public void addColumn(Integer tableColumn, Integer index){
		System.out.println("table column: "+tableColumn + " index: "+ index);
		if(getManagedBean().getColnoMap().get("sec" + index) < 5){
			if(tableColumn == 1 ){
				System.out.println(" == 1 ");
				List<TplSpecQuestion> questionList = getManagedBean().getQuestionListMap().get(String.valueOf(index));
				TplSpecQuestion headerBean = new TplSpecQuestion();
				for(int i = 0;i < questionList.size();i++){
					if(questionList != null && questionList.size() > 0) {					

						headerBean = questionList.get(i);
						if(headerBean.getQuestionType() != 2){
							headerBean.setQuestion5(headerBean.getQuestion4());
							headerBean.setQuestion4(headerBean.getQuestion3());
							headerBean.setQuestion3(headerBean.getQuestion2());
							headerBean.setQuestion2(headerBean.getQuestion1());
							headerBean.setQuestion1(null);
						}
					}
				}
			}
			else if(tableColumn == 2 ){
				System.out.println(" == 2 ");
				List<TplSpecQuestion> questionList = getManagedBean().getQuestionListMap().get(String.valueOf(index));
				TplSpecQuestion headerBean = new TplSpecQuestion();
				for(int i = 0;i < questionList.size();i++){
					if(questionList != null && questionList.size() > 0) {					

						headerBean = questionList.get(i);
						if(headerBean.getQuestionType() != 2){
							headerBean.setQuestion5(headerBean.getQuestion4());
							headerBean.setQuestion4(headerBean.getQuestion3());
							headerBean.setQuestion3(headerBean.getQuestion2());
							headerBean.setQuestion2(null);
						}
					}
				}
			}
			else if(tableColumn == 3){
				System.out.println(" == 3 ");
				List<TplSpecQuestion> questionList = getManagedBean().getQuestionListMap().get(String.valueOf(index));
				TplSpecQuestion headerBean = new TplSpecQuestion();
				for(int i = 0;i < questionList.size();i++){
					if(questionList != null && questionList.size() > 0) {					

						headerBean = questionList.get(i);
						if(headerBean.getQuestionType() != 2){
							headerBean.setQuestion5(headerBean.getQuestion4());
							headerBean.setQuestion4(headerBean.getQuestion3());
							headerBean.setQuestion3(null);
						}
					}
				}
			}
			else if(tableColumn == 4){
				System.out.println(" == 4 ");
				List<TplSpecQuestion> questionList = getManagedBean().getQuestionListMap().get(String.valueOf(index));
				TplSpecQuestion headerBean = new TplSpecQuestion();
				for(int i = 0;i < questionList.size();i++){
					if(questionList != null && questionList.size() > 0) {					

						headerBean = questionList.get(i);
						if(headerBean.getQuestionType() != 2){
							headerBean.setQuestion5(headerBean.getQuestion4());
							headerBean.setQuestion4(null);
						}
					}
				}
			}
			else if(tableColumn == 5){
				System.out.println(" == 5 ");
				List<TplSpecQuestion> questionList = getManagedBean().getQuestionListMap().get(String.valueOf(index));
				TplSpecQuestion headerBean = new TplSpecQuestion();
				for(int i = 0;i < questionList.size();i++){
					if(questionList != null && questionList.size() > 0) {					

						headerBean = questionList.get(i);
						if(headerBean.getQuestionType() != 2){
							headerBean.setQuestion5(null);
						}
					}
				}
			}
			this.getManagedBean().getColnoMap().put("sec" + index, getManagedBean().getColnoMap().get("sec" + index) + 1);
			this.getManagedBean().getColspanMap().put("sec" + index, getManagedBean().getColspanMap().get("sec" + index) + 1);
		}
	}

	public void deleteColumn(Integer tableColumn, Integer index){
		System.out.println("table column: "+tableColumn + " index: "+ index);
		if(tableColumn == 1 ){
			System.out.println(" == 1 ");
			List<TplSpecQuestion> questionList = getManagedBean().getQuestionListMap().get(String.valueOf(index));
			TplSpecQuestion headerBean = new TplSpecQuestion();
			for(int i = 0;i < questionList.size();i++){
				if(questionList != null && questionList.size() > 0) {					

					headerBean = questionList.get(i);
					if(headerBean.getQuestionType() != 2){

						headerBean.setQuestion1(headerBean.getQuestion2());
						headerBean.setQuestion2(headerBean.getQuestion3());
						headerBean.setQuestion3(headerBean.getQuestion4());
						headerBean.setQuestion4(headerBean.getQuestion5());

					}
				}
			}
		}
		else if(tableColumn == 2){
			System.out.println(" == 2 ");
			List<TplSpecQuestion> questionList = getManagedBean().getQuestionListMap().get(String.valueOf(index));
			TplSpecQuestion headerBean = new TplSpecQuestion();
			for(int i = 0;i < questionList.size();i++){
				if(questionList != null && questionList.size() > 0) {					

					headerBean = questionList.get(i);
					if(headerBean.getQuestionType() != 2){
						headerBean.setQuestion2(headerBean.getQuestion3());
						headerBean.setQuestion3(headerBean.getQuestion4());
						headerBean.setQuestion4(headerBean.getQuestion5());
					}
				}
			}
		}
		else if(tableColumn == 3){
			System.out.println(" == 3 ");
			List<TplSpecQuestion> questionList = getManagedBean().getQuestionListMap().get(String.valueOf(index));
			TplSpecQuestion headerBean = new TplSpecQuestion();
			for(int i = 0;i < questionList.size();i++){
				if(questionList != null && questionList.size() > 0) {					

					headerBean = questionList.get(i);
					if(headerBean.getQuestionType() != 2){
						headerBean.setQuestion3(headerBean.getQuestion4());
						headerBean.setQuestion4(headerBean.getQuestion5());
					}
				}
			}
		}
		else if(tableColumn == 4){
			System.out.println(" == 4 ");
			List<TplSpecQuestion> questionList = getManagedBean().getQuestionListMap().get(String.valueOf(index));
			TplSpecQuestion headerBean = new TplSpecQuestion();
			for(int i = 0;i < questionList.size();i++){
				if(questionList != null && questionList.size() > 0) {					

					headerBean = questionList.get(i);
					if(headerBean.getQuestionType() != 2){
						headerBean.setQuestion4(headerBean.getQuestion5());
					}
				}
			}
		}
		else if(tableColumn == 5){
			System.out.println(" == 5 ");
			List<TplSpecQuestion> questionList = getManagedBean().getQuestionListMap().get(String.valueOf(index));
			TplSpecQuestion headerBean = new TplSpecQuestion();
			for(int i = 0;i < questionList.size();i++){
				if(questionList != null && questionList.size() > 0) {					

					headerBean = questionList.get(i);
					if(headerBean.getQuestionType() != 2){
						headerBean.setQuestion5(null);
					}
				}
			}
		}
		this.getManagedBean().getColnoMap().put("sec" + index, getManagedBean().getColnoMap().get("sec" + index) - 1);
		this.getManagedBean().getColspanMap().put("sec" + index, getManagedBean().getColspanMap().get("sec" + index) - 1);
	}

	public void removeAnswerFlg(Integer index,Integer answerFlg){
		List<TplSpecQuestion> questionList = getManagedBean().getQuestionListMap().get(String.valueOf(index));
		TplSpecQuestion header = questionList.get(0);
		header.setAnswer(true);
		if(answerFlg == 1){
			header.setAnswer(false);

			this.getManagedBean().getColnoMap().put("sec" + index, header.getQuestionColumnNo());
			this.getManagedBean().getColspanMap().put("sec" + index, getManagedBean().getColspanMap().get("sec" + index) - 1);
		}else if(answerFlg == 2){
			header.setLastAnswer(false);

			this.getManagedBean().getColnoMap().put("sec" + index, header.getQuestionColumnNo());
			this.getManagedBean().getColspanMap().put("sec" + index, getManagedBean().getColspanMap().get("sec" + index) - 1);
		}
	}

	public void addAnswerFlg(Integer index,Integer answerFlg){
		System.out.println("addAnswerFlg: "+index +"--"+answerFlg);
		List<TplSpecQuestion> questionList = getManagedBean().getQuestionListMap().get(String.valueOf(index));
		System.out.println("questionList: "+questionList.size());
		TplSpecQuestion header = questionList.get(0);
		if(answerFlg == 1){
			header.setAnswer(true);
			System.out.println("getColnoMap: "+this.getManagedBean().getColnoMap().size());
			System.out.println("getColspanMap: "+this.getManagedBean().getColspanMap().size());
			System.out.println("header.getQuestionColumnNo(): "+header.getQuestionColumnNo());
			this.getManagedBean().getColnoMap().put("sec" + index, header.getQuestionColumnNo());
			this.getManagedBean().getColspanMap().put("sec" + index, getManagedBean().getColspanMap().get("sec" + index) + 1);
			System.out.println("after getColnoMap: "+this.getManagedBean().getColnoMap().size());
			System.out.println("after getColspanMap: "+this.getManagedBean().getColspanMap().size());
		}
		else if (answerFlg == 2){
			header.setLastAnswer(true);
			this.getManagedBean().getColnoMap().put("sec" + index, header.getQuestionColumnNo());
			this.getManagedBean().getColspanMap().put("sec" + index, getManagedBean().getColspanMap().get("sec" + index) + 1);
		}
	}
	
	public SelectItem[] getAnswerTypes() {
	     SelectItem[] items = new SelectItem[AnswerType.values().length];
	     int i = 0;
	     for(AnswerType g: AnswerType.values()) {
	       items[i++] = new SelectItem(g.value(), g.name());
	     }
	     return items;
	 }
	
	public void searchOptionChoiceList(){
		TplOptionGroup optionGroup = getManagedBean().getSelectedAnsGroup();
		ITemplateSpecSO templateSpecSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject(ITemplateSpecSO.class);
		TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
		reqDTO.setOptionGroupId(optionGroup.getOptionGroupId());

		TemplateSpecResDTO resDTO = templateSpecSO.findGroupSeqByGroupName(reqDTO);
			getManagedBean().setAnsOptionChoiceList2(resDTO.getChoiceList());
			getManagedBean().getSelectedSpecQuestion().setTplOptionGroup(optionGroup);
			
			ArrayList<SelectItem> itemList = new ArrayList<SelectItem>();
			for(TplOptionChoice optionChoice : resDTO.getChoiceList()) {
				itemList.add(new SelectItem(optionChoice.getOptionChoiceId(), optionChoice.getChoiceName()));
			}
			this.getManagedBean().getOptionMap().put(Long.valueOf(optionGroup.getOptionGroupId()), itemList);
		
	}
	
	public void searchOptionGroupList(){
		String optionGroupName = getManagedBean().getOptionGroupName();
		ITemplateSpecSO templateSpecSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject(ITemplateSpecSO.class);
		TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
		reqDTO.setOptionGroupName(optionGroupName);
		
		TemplateSpecResDTO resDTO = templateSpecSO.findAllByGroupName(reqDTO);
		getManagedBean().setAnsOptionGroupList(resDTO.getOptionGroupList());
	}
	
	public void deleteOptChoice(){
		
		getManagedBean().getNewOptChoiceList().remove(getManagedBean().getSelectedChoice());
	}
	
	public void addNewChoiceRow(){
		TplOptionChoice newOptChoice = new TplOptionChoice();
		newOptChoice.setChoiceName("");
		newOptChoice.setBrandNew(true);
		if(getManagedBean().getNewOptChoiceList() == null){
			getManagedBean().setNewOptChoiceList(new ArrayList<TplOptionChoice>()); 
		}
		getManagedBean().getNewOptChoiceList().add(newOptChoice);
	}
	
	public void saveNewOptChoice(){
		ITemplateSpecSO templateSpecSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject(ITemplateSpecSO.class);
		TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
		TplOptionGroup optGroup = new TplOptionGroup();
		optGroup.setGroupName(getManagedBean().getNewOptGroupName());
//		optGroup.setTplOptionChoices(getManagedBean().getNewOptChoiceList());
		optGroup.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		optGroup.setRecordStatus(true);
		optGroup.setCreatedBy(1l);
		
		reqDTO.setOptionGroup(optGroup);
		reqDTO.setOptionChoiceList(getManagedBean().getNewOptChoiceList());
		TemplateSpecResDTO resDTO = templateSpecSO.saveOptionGroup(reqDTO);
		getManagedBean().getSelectedSpecQuestion().setTplOptionGroup(resDTO.getOptionGroup());
		
		getManagedBean().setAnsOptionChoiceList2(resDTO.getOptionGroup().getTplOptionChoices());
		
		ArrayList<SelectItem> itemList = new ArrayList<SelectItem>();
		for(TplOptionChoice optionChoice : getManagedBean().getNewOptChoiceList()) {
			itemList.add(new SelectItem(optionChoice.getOptionChoiceId(), optionChoice.getChoiceName()));
		}
		this.getManagedBean().getOptionMap().put(Long.valueOf(resDTO.getOptionGroup().getOptionGroupId()), itemList);
	
	}
	
	public void deleteScoringOption(){
		getManagedBean().getSpecScoringList().remove(getManagedBean().getSelectedScore());
	}
	
	public void addScoringValue(){
		TplSpecScoring specScore = new TplSpecScoring();
		specScore.setRangeFrom(0);
		specScore.setRangeTo(0);
		specScore.setScore(0);
		specScore.setTplSpecQuestion(getManagedBean().getSelectedSpecQuestion());
		specScore.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		specScore.setRecordStatus(true);
		specScore.setCreatedBy(1l);
//		if(getManagedBean().getSelectedSpecQuestion().getTplSpecScorings() == null){
//			getManagedBean().getSelectedSpecQuestion().setTplSpecScorings(new ArrayList<TplSpecScoring>()); 
//		}
//		getManagedBean().getSelectedSpecQuestion().getTplSpecScorings().add(specScore);
		
		if(getManagedBean().getSpecScoringList() == null){
			this.getManagedBean().setSpecScoringList(new ArrayList<TplSpecScoring>());
		}
		getManagedBean().getSpecScoringList().add(specScore);
	}
	
	public void renumberingQuestionTable(){
		int sectionNumber = 0;
		int level3Number = 0;
		// all table
		if(!getManagedBean().getQuestionListMap().isEmpty()){
			for(int x = 0; x < getManagedBean().getQuestionListMap().size(); x++) {
				boolean question2isSection = false;
				sectionNumber = 1;
				List<TplSpecQuestion> questionList = getManagedBean().getQuestionListMap().get(String.valueOf(x+1));
				// 1 table
				for(int y = 0; y < questionList.size(); y++) {
					TplSpecQuestion question = questionList.get(y);
					switch(question.getQuestionOrder()) {

					case 0: question.setQuestionNumber(String.valueOf(x+1)); break;
					case 1: question.setQuestionNumber(String.valueOf((x+1)+"."+sectionNumber));
					if(question.getQuestionType()==2) {
						question2isSection = true;
					}
					level3Number = 0;
					break;
					default: if(question2isSection) {
						if(question.getQuestionType()==3) {
							level3Number++;
							question.setQuestionNumber(String.valueOf((x+1)+"."+sectionNumber+"."+level3Number));
						}
						if(question.getQuestionType()==2) {
							sectionNumber++;
							question.setQuestionNumber(String.valueOf((x+1)+"."+sectionNumber));
							level3Number = 0;
						}									
					} else {
						sectionNumber++;
						question.setQuestionNumber(String.valueOf((x+1)+"."+sectionNumber));
					}

					}
				}
			}
		}
	}
	
	public void storeChoiceId(){
//		if(!getManagedBean().isCheckScore() && getManagedBean().getSelectedSpecQuestion().getAnswerType().intValue()!=getManagedBean().getSelectedAnswerType().intValue()) {
//			getManagedBean().getSpecScoringList().clear();
//		}
		if(getManagedBean().isCheckScore() && getManagedBean().getSelectedSpecQuestion().getAnswerType().intValue()!=getManagedBean().getSelectedAnswerType().intValue()) {
			getManagedBean().getDeleteQuestionSeqList().add(getManagedBean().getSelectedSpecQuestion().getSpecQuestionId()); // change to list
		}
		if(getManagedBean().getSelectedSpecQuestion().getTplOptionGroup() != null){
			getManagedBean().getSelectedSpecQuestion().getTplOptionGroup().setTplOptionChoices(getManagedBean().getAnsOptionChoiceList2());
			for(TplOptionChoice choice : getManagedBean().getSelectedSpecQuestion().getTplOptionGroup().getTplOptionChoices()){
				System.out.println("choice.getChoiceName(): "+choice.getChoiceName());
			}
		}
		getManagedBean().getSelectedSpecQuestion().setAnswerType(getManagedBean().getSelectedAnswerType());
		System.out.println("getManagedBean().getSelectedAnswerType(): "+getManagedBean().getSelectedSpecQuestion().getAnswerType());
		getManagedBean().getSelectedSpecQuestion().setScoring(getManagedBean().isCheckScore());
		
		if(getManagedBean().getSelectedSpecQuestion().getAnswerType() == 3){
			if(getManagedBean().isCheckScore()){
				getManagedBean().getSelectedSpecQuestion().getTplSpecScorings().clear();
				for(TplOptionChoice optChoice : getManagedBean().getAnsOptionChoiceList2()){
					TplSpecScoring specScore = new TplSpecScoring();
					specScore.setSpecScoringId(optChoice.getOptionChoiceId());
					specScore.setScore(optChoice.getScore());
					specScore.setTplSpecQuestion(getManagedBean().getSelectedSpecQuestion());
					specScore.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					specScore.setCreatedBy(1l);
					specScore.setRecordStatus(true);
					getManagedBean().getSelectedSpecQuestion().getTplSpecScorings().add(specScore);
				}
			}
		}else if(getManagedBean().getSelectedSpecQuestion().getAnswerType() == 2){
//			getManagedBean().getSelectedSpecQuestion().getTplSpecScorings().clear();
			getManagedBean().getSelectedSpecQuestion().setTplOptionGroup(null);
			getManagedBean().getSelectedSpecQuestion().setTplSpecScorings(getManagedBean().getSpecScoringList());
		}else if(getManagedBean().getSelectedSpecQuestion().getAnswerType() == 1){
			getManagedBean().getSelectedSpecQuestion().setTplOptionGroup(null);
			getManagedBean().getSelectedSpecQuestion().setTplSpecScorings(null);
		}
	}
	
	public void storeAnswerData(){
		System.out.println("getManagedBean().getSelectedSpecQuestion().getAnswerType(): "+getManagedBean().getSelectedSpecQuestion().getAnswerType());
		getManagedBean().setSelectedAnswerType(getManagedBean().getSelectedSpecQuestion().getAnswerType());
		getManagedBean().setCheckScore(getManagedBean().getSelectedSpecQuestion().isScoring());
		if(getManagedBean().getSelectedSpecQuestion().getTplOptionGroup() != null){
			getManagedBean().setAnsOptionChoiceList2(getManagedBean().getSelectedSpecQuestion().getTplOptionGroup().getTplOptionChoices());
		}
		if(getManagedBean().getSelectedSpecQuestion().getTplSpecScorings() != null){
			getManagedBean().setSpecScoringList(getManagedBean().getSelectedSpecQuestion().getTplSpecScorings());
		}
		
		
	}
//	
//	public void storeAnswerData(TplSpecQuestion selectedQuestion){
//		getManagedBean().setSelectedAnswerType(selectedQuestion.getAnswerType());
//		getManagedBean().setCheckScore(selectedQuestion.isScoring());
//		if(selectedQuestion.getTplOptionGroup() != null){
//			getManagedBean().setAnsOptionChoiceList2(selectedQuestion.getTplOptionGroup().getTplOptionChoices());
//		}
//		if(selectedQuestion.getTplSpecScorings() != null){
//			getManagedBean().setSpecScoringList(selectedQuestion.getTplSpecScorings());
//		}
//		
//		
//	}
	
//	public void storeAnswerData(ActionEvent event){
//		TplSpecQuestion selectedQuestion = (TplSpecQuestion)event.getComponent().getAttributes().get("selectedQuestion");
//		System.out.println("storeAnswerData selected Ques: "+selectedQuestion);
//		getManagedBean().setSelectedAnswerType(selectedQuestion.getAnswerType());
//		getManagedBean().setCheckScore(selectedQuestion.isScoring());
//		if(selectedQuestion.getTplOptionGroup() != null){
//			getManagedBean().setAnsOptionChoiceList2(selectedQuestion.getTplOptionGroup().getTplOptionChoices());
//		}
//		if(selectedQuestion.getTplSpecScorings() != null){
//			getManagedBean().setSpecScoringList(selectedQuestion.getTplSpecScorings());
//		}
//	}
	
	public void clearAnswerTable(){
		if(getManagedBean().getSelectedSpecQuestion().getAnswerType().intValue()!=getManagedBean().getSelectedAnswerType().intValue()){
//			getManagedBean().getSpecScoringList().clear();
			getManagedBean().setCheckScore(false);
		}
	}
	
	public void generateExcel(){
		TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
		TplSpecMaster specMaster = this.getManagedBean().getSelectedTemplate();
		reqDTO.setMasterId(specMaster.getSpecMasterId());
		ITemplateSpecSO templateSpecSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject(ITemplateSpecSO.class);
		System.out.println("generate Excel");
		templateSpecSO.generateExcel(reqDTO);
	}
}

