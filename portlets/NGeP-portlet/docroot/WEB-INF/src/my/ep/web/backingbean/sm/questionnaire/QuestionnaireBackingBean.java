package my.ep.web.backingbean.sm.questionnaire;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sm.questionnaire.IQuestionnaireSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.dto.sm.questionnaire.QuestionnaireReqDTO;
import my.ep.dto.sm.questionnaire.QuestionnaireResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.persistence.entity.sm.SmAnswer;
import my.ep.persistence.entity.sm.SmQuestion;
import my.ep.persistence.entity.sm.SmSupplierAnswer;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.questionnaire.QuestionnaireManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

@ManagedBean(name = "questionnaireBackingBean")
@RequestScoped
public class QuestionnaireBackingBean extends
		BaseBackingBean<QuestionnaireManagedBean> {
	public static Logger log = Logger.getLogger(QuestionnaireBackingBean.class);

	public QuestionnaireBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
	}

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
	}

	public void retrieveQuestionnaire() {
		this.retrieveSupplierInfo();
		// this.test();
		log.info("***********retrieveQuestionnaire**************");
		QuestionnaireReqDTO reqDTO = new QuestionnaireReqDTO();
		IQuestionnaireSO reqSO = (IQuestionnaireSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IQuestionnaireSO.class);
		QuestionnaireResDTO resDTO = reqSO.retrieveActiveQuestion();

		// this.getManagedBean().setSmQuestionLs(resDTO.getSmQuestionList());

		List<SmSupplierAnswer> smSuppAnswerLs = new ArrayList<SmSupplierAnswer>();
		for (SmQuestion smQues : resDTO.getSmQuestionList()) {
			SmSupplierAnswer smSuppAns = new SmSupplierAnswer();
			//smSuppAns.setSmSupplier(this.getManagedBean().getSmSupp());
			smSuppAns.setSmQuestion(smQues);
			smSuppAns.setSmAnswer(new SmAnswer());
			smSuppAnswerLs.add(smSuppAns);
			// /smSuppAns.setSupplierAnswerId(supplierAnswerId)
		}

		this.getManagedBean().setSmSuppAnswerLs(smSuppAnswerLs);
	}

	public String submitQuestionnaire() {
		log.info("***********submitQuestionnaire Start**************");
		// this.getManagedBean().getSmSupp().setSmSupplierAnswers(this.getManagedBean().getSmSuppAnswerLs());
		QuestionnaireReqDTO reqDTO = new QuestionnaireReqDTO();
		// for(SmSupplierAnswer sa : this.getManagedBean().getSmSuppAnswerLs()){
		// log.info("****88888888888888888*****" +
		// sa.getSmAnswer().getAnswerId());
		// }
		log.info("****88888888888888888*****");
		reqDTO.setSmSuppAns(this.getManagedBean().getSmSuppAnswerLs());
		IQuestionnaireSO reqSO = (IQuestionnaireSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IQuestionnaireSO.class);
		QuestionnaireResDTO resDTO = reqSO.questionnaireSubmission(reqDTO);

		return "viewVirtualCert";

	}
}
