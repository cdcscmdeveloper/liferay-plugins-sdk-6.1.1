package my.ep.web.backingbean.sm.questionnaire;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.questionnaire.IQuestionnaireSO;
import my.ep.dto.sm.questionnaire.QuestionnaireReqDTO;
import my.ep.dto.sm.questionnaire.QuestionnaireResDTO;
import my.ep.persistence.entity.sm.SmAnswer;
import my.ep.persistence.entity.sm.SmQuestion;
import my.ep.persistence.entity.sm.SmSupplierAnswer;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.questionnaire.QuestionnaireManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.log4j.Logger;

@ManagedBean(name = "questionnaireBackingBean")
@RequestScoped
public class QuestionnaireBackingBean extends BaseBackingBean<QuestionnaireManagedBean> {
	public static Logger log = Logger.getLogger(QuestionnaireBackingBean.class);

	public QuestionnaireBackingBean() {
//		if (MDC.get(LogKey.USER_ID) == null) {
//			MDC.put(LogKey.USER_ID, 1L);
//		}
	}

	/*
	public void retrieveSupplierInfo() {
		log.info("***********retrieveSupplierInfo**************");
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		reqDTO.setSupplierId(27);
		IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IRegistrationSO.class);
		RegistrationResDTO resDTO = reqSO.retrieveSuppOnlineAcc(reqDTO);
		log.info("***********retrieveSupplierInfo**************"
				+ resDTO.getSmSupp().getRegNo());
		this.getManagedBean().setSmSupp(resDTO.getSmSupp());
		resDTO.getSmSupp().getSmAppl().getApplType();
	}*/

	public void retrieveQuestionnaire() {
		// this.test();
		log.info("***********retrieveQuestionnaire**************");
		//QuestionnaireReqDTO reqDTO = new QuestionnaireReqDTO();
		IQuestionnaireSO reqSO = (IQuestionnaireSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IQuestionnaireSO.class);
		QuestionnaireResDTO resDTO = reqSO.retrieveActiveQuestion();

		// this.getManagedBean().setSmQuestionLs(resDTO.getSmQuestionList());

		List<SmSupplierAnswer> smSuppAnswerLs = new ArrayList<SmSupplierAnswer>();
		for (SmQuestion smQues : resDTO.getSmQuestionList()) {
			SmSupplierAnswer smSuppAns = new SmSupplierAnswer();
			smSuppAns.setSmSupplier(this.getManagedBean().getSmSupp());
			smSuppAns.setSmQuestion(smQues);
			smSuppAns.setSmAnswer(new SmAnswer());
			smSuppAnswerLs.add(smSuppAns);
			
			if(smQues.getSmAnswerCategory().getAnswerCategoryId() == 2){
				smSuppAns.getSmAnswer().setAnswerId(smQues.getSmAnswerCategory().getSmAnswers().get(0).getAnswerId());
			}
			// /smSuppAns.setSupplierAnswerId(supplierAnswerId)
		}

		this.getManagedBean().setSmSuppAnswerLs(smSuppAnswerLs);
	}

	public void submitQuestionnaire() {
		log.info("***********submitQuestionnaire Start**************");
		log.info("***********SM SUPPLIER ID******" + this.getManagedBean().getSmSupp().getSupplierId());

		QuestionnaireReqDTO reqDTO = new QuestionnaireReqDTO();

		this.getManagedBean().getSmSupp().setSmSupplierAnswers(new ArrayList<SmSupplierAnswer>());
		this.getManagedBean().getSmSupp().setSmSupplierAnswers(this.getManagedBean().getSmSuppAnswerLs());
		reqDTO.setSmSupplier(this.getManagedBean().getSmSupp());
		reqDTO.setSmAppl(this.getManagedBean().getSmAppl());
		IQuestionnaireSO reqSO = (IQuestionnaireSO) BusinessDelegate.getInstance().getServiceObject("sm", IQuestionnaireSO.class);
	
		for(SmSupplierAnswer suppAns : this.getManagedBean().getSmSuppAnswerLs()){
			
			log.info("Desc >>>>>" + suppAns.getSmQuestion().getSmAnswerCategory().getAnswerCategoryDesc());
			log.info("Answer Id>>>>>" + suppAns.getSmAnswer().getAnswerId());
		}
		
		QuestionnaireResDTO resDTO = reqSO.questionnaireSubmission(reqDTO);

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, this.getManagedBean().getTargetUrl());
		
		renewSesionObject();
	}
	
	public String getsupplierApplTypeDesc(String applType){
		return SmCommonUtil.getApplTypeDesc(applType);
	}
	
}
