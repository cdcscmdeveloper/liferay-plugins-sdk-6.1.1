/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.tpl.spec;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecScoring;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.tpl.spec.TemplateSpecManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Template Specification Backing Bean
 * 
 * @author kian seng
 * @since 24/4/2013
 */
@ManagedBean(name="templateSpecBackingBean")
@RequestScoped
public class TemplateSpecBackingBean extends BaseBackingBean<TemplateSpecManagedBean> {

	private static Logger log = Logger.getLogger(TemplateSpecBackingBean.class);
	
	public String retrieveTemplateSpecMaster() {
		ITemplateSpecSO templateSpecSO = null;
		TplSpecMaster specMaster = this.getManagedBean().getSpecMaster();
		
		/*IRegistrationSO registrationSO = BusinessDelegate.getInstance().getServiceObject("sm", IRegistrationSO.class);
		RegistrationReqDTO registrationReqDTO = new RegistrationReqDTO();
		registrationReqDTO.setSupplierId(1L);
		RegistrationResDTO registrationResDTO = registrationSO.retrieveSuppOnlineAcc(registrationReqDTO);
		getManagedBean().setSupplier(registrationResDTO.getSmSupp());*/
		
		try {
			templateSpecSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject("cc",ITemplateSpecSO.class);
			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			reqDTO.setSpecMaster(specMaster);
			reqDTO.setContentType(getManagedBean().getContentType());
			if (!TemplateSpecConst.CONTENT_TYPE_QUESTION.equals(getManagedBean().getContentType())) {
				SmSupplier smSupplier = this.getManagedBean().getSupplier();
				
				reqDTO.setSupplier(smSupplier);
			}

			TemplateSpecResDTO resDTO = templateSpecSO.getTemplateMaster(reqDTO);
			if(resDTO.isSuccessful()) {
				getManagedBean().setSpecMaster(resDTO.getSpecMaster());
				
				if (!TemplateSpecConst.CONTENT_TYPE_QUESTION.equals(getManagedBean().getContentType())) {
					populateSpecAnswer(getManagedBean().getSpecMaster().getTplSpecQuestions());
				}
				
				addQuestion(-1, TemplateSpecConst.QUESTION_TYPE_HEADER);
				getManagedBean().setSelectedSpecQuestion(new TplSpecQuestion());
			}
		} finally {
			templateSpecSO = null;
		}
		return "maintenance";
	}

	public void saveAction() {
		log.debug("TemplateSpecBackingBean.saveAction()");
		ITemplateSpecSO templateSpecSO = null;
		try {
			templateSpecSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject("cc",ITemplateSpecSO.class);
			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			
			// Remove header row
			getManagedBean().getSpecMaster().getTplSpecQuestions().remove(0);
			
			reqDTO.setSpecMaster(this.getManagedBean().getSpecMaster());
			reqDTO.setDeletedQuestionList(this.getManagedBean().getDeletedQuestions());
			TemplateSpecResDTO resDTO = templateSpecSO.saveTemplateSpec(reqDTO);		

			if(resDTO.isSuccessful()) {
				this.renewSesionObject();
				this.getManagedBean().setSpecMaster(resDTO.getSpecMaster());
				retrieveTemplateSpecMaster();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Record save successfully", "Record ID: "+resDTO.getSpecMaster().getSpecMasterId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			templateSpecSO = null;
		}
	}

	public void saveAnswerAction() {
		ITemplateSpecSO templateSpecSO = null;
		try {
			templateSpecSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject("cc",ITemplateSpecSO.class);

			List<TplSpecAnswer> answerList = new ArrayList<TplSpecAnswer>();
			for(TplSpecQuestion question : this.getManagedBean().getSpecMaster().getTplSpecQuestions()) {
				if(question.getTplSpecAnswer() != null) {
					answerList.add(question.getTplSpecAnswer());
				}
			}
			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			reqDTO.setAnswerList(answerList);

			TemplateSpecResDTO resDTO = templateSpecSO.saveTemplateSpecAnswer(reqDTO);

			if(resDTO.isSuccessful()) {
				retrieveTemplateSpecMaster();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Record save successfully", "Record ID: "+getManagedBean().getSpecMaster().getSpecMasterId()));
			}

		} finally {
			templateSpecSO = null;
		}
	}

	public void deleteQuestion() {
		List<TplSpecQuestion> specQuestionList = getManagedBean().getSpecMaster().getTplSpecQuestions();
		TplSpecQuestion questionToDelete = specQuestionList.get(getManagedBean().getSelectedIndex());

		if(questionToDelete.getQuestionType().equalsIgnoreCase(TemplateSpecConst.QUESTION_TYPE_ITEM)) {
			for(int i = getManagedBean().getSelectedIndex() + 1; i < specQuestionList.size(); i++) {
				TplSpecQuestion childToDelete = specQuestionList.get(i);
				if(childToDelete.getQuestionType().equalsIgnoreCase(TemplateSpecConst.QUESTION_TYPE_ITEM)) {
					break;
				}
				if(!childToDelete.isBrandNew()) {
					childToDelete.setDeleted(true);
					getManagedBean().getDeletedQuestions().add(childToDelete);
				}
				specQuestionList.remove(childToDelete);
				i--;
			}
		} 
		if(!questionToDelete.isBrandNew()) {
			questionToDelete.setDeleted(true);
			this.getManagedBean().getDeletedQuestions().add(questionToDelete);
		}
		specQuestionList.remove(questionToDelete);
		
		this.getManagedBean().setSelectedIndex(-1);
	}

	public String newAction() {
		TplSpecMaster specMaster = new TplSpecMaster();
		
		specMaster.setProcurementCategory(new PmParameter());
		specMaster.setItemTypeCode(new PmParameter());
		specMaster.setTemplateTypeCode(new PmParameter());
		specMaster.setSpecTypeCode(new PmParameter());
		specMaster.setOrgProfile(new PmOrgProfile());
		
		getManagedBean().setSpecMaster(specMaster);
		getManagedBean().setSelectedSpecQuestion(new TplSpecQuestion());
		
		return "maintenance"; 
	}

	public void searchResult(){
		ITemplateSpecSO templateSpecSO = (ITemplateSpecSO)BusinessDelegate.getInstance().getServiceObject("cc",ITemplateSpecSO.class);
		ISearchBean<TplSpecMaster> searchBean = new SearchBean<TplSpecMaster>();
		SearchFilterBean equalFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
		SearchFilterBean likeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
		
		if(getManagedBean().getSearchProcurementCategoryId()!=null) {
			equalFilterBean.setSearchField("procurementCategory.parameterId");
			equalFilterBean.setSearchValue(getManagedBean().getSearchProcurementCategoryId());
			searchBean.getDataFilterBeans().add(equalFilterBean);
		}
		if(getManagedBean().getSearchTemplateTypeId()!=null) {
			equalFilterBean.setSearchField("templateTypeCode.parameterId");
			equalFilterBean.setSearchValue(getManagedBean().getSearchTemplateTypeId());
			searchBean.getDataFilterBeans().add(equalFilterBean);
		}
		if(getManagedBean().getSearchOrgProfileId()!=null) {
			equalFilterBean.setSearchField("orgProfile.orgProfileId");
			equalFilterBean.setSearchValue(getManagedBean().getSearchOrgProfileId());
			searchBean.getDataFilterBeans().add(equalFilterBean);
		}		
		if(getManagedBean().getSearchItemTypeId()!=null) {
			equalFilterBean.setSearchField("itemTypeCode.parameterId");
			equalFilterBean.setSearchValue(getManagedBean().getSearchItemTypeId());
			searchBean.getDataFilterBeans().add(equalFilterBean);
		}
		if(getManagedBean().getSearchSpecTypeId()!=null) {
			equalFilterBean.setSearchField("specTypeCode.parameterId");
			equalFilterBean.setSearchValue(getManagedBean().getSearchSpecTypeId());
			searchBean.getDataFilterBeans().add(equalFilterBean);
		}			
		if(getManagedBean().getSearchSpecName()!=null) {
			likeFilterBean.setSearchField("specName");
			likeFilterBean.setSearchValue(getManagedBean().getSearchSpecName());
			searchBean.getDataFilterBeans().add(likeFilterBean);
		}		
		
		TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
		reqDTO.setSearchBean(searchBean);
		TemplateSpecResDTO resDTO = templateSpecSO.searchTemplateMaster(reqDTO);
		if(resDTO.isSuccessful()) {
			getManagedBean().setSpecMasterList(resDTO.getSearchResultBean().getResultList());
		}
	}
	
	public void addScoringValue(){
		TplSpecScoring specScore = new TplSpecScoring();
		specScore.setRangeFrom(0);
		specScore.setRangeTo(0);
		specScore.setScore(0);
		specScore.setTplSpecQuestion(getManagedBean().getSelectedSpecQuestion());
		getManagedBean().getSpecScoringList().add(specScore);
	}
	
	public void renumberingQuestionTable(){
		int level2Number = 0;
		int sectionNumber = 0;
		String numbering;

		if(!getManagedBean().getSpecMaster().getTplSpecQuestions().isEmpty()){
			boolean firstRowisSection = false;
		
			for(int x = 0; x < getManagedBean().getSpecMaster().getTplSpecQuestions().size(); x++) {
				numbering = "";
				TplSpecQuestion question = getManagedBean().getSpecMaster().getTplSpecQuestions().get(x);
				if(question.getQuestionType().equalsIgnoreCase(TemplateSpecConst.QUESTION_TYPE_ITEM)) {
					if(x==0) {
						firstRowisSection = true;
					}
					sectionNumber++;
					question.setQuestionNumber(sectionNumber+"");
				}
				if(question.getQuestionType().equalsIgnoreCase(TemplateSpecConst.QUESTION_TYPE_ITEM_SPEC)) {
					level2Number++;
					numbering = firstRowisSection?sectionNumber+"."+level2Number:level2Number+"";
					question.setQuestionNumber(numbering);
				}
			}
		}
	}
	
	public void storeAnswerSetting(ActionEvent event) {
		log.info("storeAnswerSetting()");
	}

	public void renderTable(ActionEvent event) {
		log.info("renderTable()");
		List<TplSpecQuestion> questionList = this.getManagedBean().getSpecMaster().getTplSpecQuestions();
		if(questionList.size()==0) {
			addQuestion(-1, TemplateSpecConst.QUESTION_TYPE_HEADER);
		}
	}

	public void addQuestion(Integer rowId, String questionType) {
		List<TplSpecQuestion> questionList = this.getManagedBean().getSpecMaster().getTplSpecQuestions();
		this.addQuestion(questionList, rowId, questionType);
	}

	private void addQuestion(List<TplSpecQuestion> questionList, Integer row, String questionType) {
		IParameterSO parameterSO = null;
		try {
			TplSpecQuestion newQuestion = new TplSpecQuestion();
			newQuestion.setQuestionType(questionType);
			
			parameterSO = BusinessDelegate.getInstance().getServiceObject("pm", IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.ANSWER_TYPE);
			reqDTO.setParameterCode(ParameterConst.ANSWER_TYPE_TEXT);
			ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			newQuestion.setAnswerTypeCode(resDTO.getPmParameter());
			
			PmUom uom = new PmUom();
			newQuestion.setClUom(uom);
			questionList.add(row + 1, newQuestion);
			for(int i = row + 1; i < questionList.size(); i++) {
				TplSpecQuestion question = questionList.get(i);
				question.setQuestionOrder(i);
			}
			
			newQuestion.setClAnswerRequired(true);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			parameterSO = null;
		}
	}
	
	public SelectItem[] getUomList() {
	    SelectItem[] items = null;
	    
		IUomSO uomSO = null;
		try {
			uomSO = (IUomSO)BusinessDelegate.getInstance().getServiceObject("pm",IUomSO.class);
			UomResDTO resDTO = uomSO.retrieveUomList();
			items = new SelectItem[resDTO.getPmUomList().size()+1];
			items[0] = new SelectItem(null, "- Select One -");
			int i = 1;
			for(PmUom uom : resDTO.getPmUomList()) {
				items[i++] = new SelectItem(uom.getUomId(), uom.getUomCode()+" "+uom.getUomName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			uomSO = null;
		}
	    return items;
	}
	
	public SelectItem[] getMinistryList() {
	    SelectItem[] items = null;
	    
		IOrgProfileSO orgProfileSO = null;
		try {
			orgProfileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
			
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(ParameterConst.ORG_TYPE_MINISTRY);
			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
			
			items = new SelectItem[resDTO.getOrgProfileList().size()+1];
			items[0] = new SelectItem(null, "- Select One -");
			int i = 1;
			for(PmOrgProfile orgProfile : resDTO.getOrgProfileList()) {
				items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			orgProfileSO = null;
		}
	    return items;
	}
	
	public void answerTypeChange(TplSpecQuestion specQuestion) {
		IParameterSO parameterSO = null;
		try {
			Long parameterId = specQuestion.getAnswerTypeCode().getParameterId();
			
			parameterSO = BusinessDelegate.getInstance().getServiceObject("pm", IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			specQuestion.setAnswerTypeCode(resDTO.getPmParameter());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			parameterSO = null;
		}
		
	}
	
	public void calcTotalAmount(TplSpecAnswer specAnswer, boolean isItemTypeService) {
		TplSpecQuestion specQuestion = specAnswer.getTplSpecQuestion();
		TplSpecMaster specMaster = specQuestion.getTplSpecMaster();
		BigDecimal quantity = specQuestion.getClQuantity();
		BigDecimal ratePerUom = specAnswer.getRatePerUom();
		
		BigDecimal totalPrice = quantity;
		
		if (ratePerUom != null) {
			totalPrice = totalPrice.multiply(ratePerUom);	
		}
		
		if (isItemTypeService) {
			Integer uomFrequency = specQuestion.getClUomFrequency();
			Integer uomPerDay = specQuestion.getClUomPerDay();
			Integer uomPerMonth = specQuestion.getClUomPerMonth();
			Integer monthPerYear = specQuestion.getClMonthPerYear();
			int noOfYear = specMaster.getNoOfYear();
			
			if (uomFrequency != null) {
				totalPrice = totalPrice.multiply(BigDecimal.valueOf(uomFrequency));
			}
			
			if (uomPerDay != null) {
				totalPrice = totalPrice.multiply(BigDecimal.valueOf(uomPerDay));
			}
			
			if (uomPerMonth != null) {
				totalPrice = totalPrice.multiply(BigDecimal.valueOf(uomPerMonth));
			}
			
			if (monthPerYear != null) {
				totalPrice = totalPrice.multiply(BigDecimal.valueOf(monthPerYear));
			}
			
			specAnswer.setAmountPerYear(totalPrice);
			
			if (noOfYear > 0) {
				totalPrice = totalPrice.multiply(BigDecimal.valueOf(noOfYear));
			}
		}
		specAnswer.setAnswerNumeric(totalPrice);
	}
	
	private void populateSpecAnswer(List<TplSpecQuestion> questions) {		
		TplSpecAnswer tplSpecAnswer = null;
		
		for (TplSpecQuestion question : questions) {
			if (question.getTplSpecAnswers() != null && question.getTplSpecAnswers().size() > 0) {
				question.setTplSpecAnswer(question.getTplSpecAnswers().iterator().next());
			} else if (TemplateSpecConst.CONTENT_TYPE_ANSWER.equals(getManagedBean().getContentType())) {
				tplSpecAnswer = new TplSpecAnswer();
				tplSpecAnswer.setSupplier(getManagedBean().getSupplier());
				tplSpecAnswer.setTplSpecQuestion(question);
				
				question.setTplSpecAnswer(tplSpecAnswer);
			}
		}
	}
	
}

