package my.ep.web.backingbean.sm.questionnaire;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.questionnaire.IQuestionnaireSO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.questionnaire.QuestionnaireReqDTO;
import my.ep.dto.sm.questionnaire.QuestionnaireResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmAnswer;
import my.ep.persistence.entity.sm.SmQuestion;
import my.ep.persistence.entity.sm.SmSupplierAnswer;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.questionnaire.QuestionnaireManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.log4j.Logger;

@ManagedBean(name = "questionnaireBackingBean")
@RequestScoped
public class QuestionnaireBackingBean extends BaseBackingBean<QuestionnaireManagedBean> {
	public static Logger log = Logger.getLogger(QuestionnaireBackingBean.class);

	public QuestionnaireBackingBean() {
		SmCommonUtil.setMDCUserId();
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

		List<SmSupplierAnswer> smSuppAnswerLs = new ArrayList<SmSupplierAnswer>();
		for (SmQuestion smQues : resDTO.getSmQuestionList()) {
			SmSupplierAnswer smSuppAns = new SmSupplierAnswer();
			smSuppAns.setSmAppl(this.getManagedBean().getSmAppl());
			smSuppAns.setSmQuestion(smQues);
			smSuppAns.setSmAnswer(getApplicationTypeAnswer(smQues));
			smSuppAns.setSmAnswer(getApplicationStatus(smQues,smSuppAns.getSmAnswer()));
			smSuppAnswerLs.add(smSuppAns);
			
			if(smQues.getSmAnswerCategory().getAnswerCategoryId() == 2){
				smSuppAns.getSmAnswer().setAnswerId(smQues.getSmAnswerCategory().getSmAnswers().get(0).getAnswerId());
			}
			// /smSuppAns.setSupplierAnswerId(supplierAnswerId)
		}
		this.getManagedBean().setSmSuppAnswerLs(smSuppAnswerLs);
		
		//retrieve MOF Info - to display MOF Account No
		retrievMOFInfo();
	}

	public void submitQuestionnaire() {
		log.info("***********submitQuestionnaire Start**************");
		log.info("***********SM SUPPLIER ID******"
				+ this.getManagedBean().getSmSupp().getSupplierId());

		QuestionnaireReqDTO reqDTO = new QuestionnaireReqDTO();

		this.getManagedBean()
				.getSmAppl()
				.setSmSupplierAnswers(this.getManagedBean().getSmSuppAnswerLs());
		reqDTO.setSmSupplier(this.getManagedBean().getSmSupp());
		reqDTO.setSmAppl(this.getManagedBean().getSmAppl());
		reqDTO.setOriResubmitSmAppl(this.getManagedBean().getRejectedAppl());
		IQuestionnaireSO reqSO = (IQuestionnaireSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IQuestionnaireSO.class);

		for (SmSupplierAnswer suppAns : this.getManagedBean()
				.getSmSuppAnswerLs()) {

			log.info("Desc >>>>>"
					+ suppAns.getSmQuestion().getSmAnswerCategory()
							.getAnswerCategoryDesc());
			log.info("Answer Id>>>>>" + suppAns.getSmAnswer().getAnswerId());
		}

		FacesScopes fs = new FacesScopes();
		List<String> useList = new ArrayList<String>();
		PmUser loginUser = fs.getUserSessionBean();
		useList.add(loginUser.getLoginId());
		reqDTO.setUserList(useList);
		reqDTO.setToken(fs.getBPMAuthenticationCtxToken());
		reqDTO.setResetLatestResubmitAppl(getManagedBean().isResetLatestResubmitAppl());

		reqSO.questionnaireSubmission(reqDTO);
		
		//for resubmit application directly inject to task list
		if (this.getManagedBean().getRejectedAppl() != null) {
			SmCommonUtil.redirectToPage(this.getManagedBean().getTargetUrl(),
					SmCommonUtil.getMessage("lbl.sm.submit.questionaire.msg",
							null), null);
		} else {
			SmCommonUtil.redirectToPage(this.getManagedBean().getTargetUrl(),
					null, null);
		}

		renewSesionObject();
	}
	
	public String getsupplierApplTypeDesc(String applType){
		return SmCommonUtil.getApplTypeDesc(applType);
	}
	
	public void retrievMOFInfo(){
		MofAccountReqDTO reqDTO = new MofAccountReqDTO();
		
		IMofAccountSO mofSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject("sm", IMofAccountSO.class);
		reqDTO.setSupplierId(this.getManagedBean().getSmSupp().getSupplierId());
		reqDTO.setSupplierType(this.getManagedBean().getSmAppl().getSupplierType());
		MofAccountResDTO resDTO = mofSO.retriveMOFAccountBySupplierIdAndType(reqDTO);
		
		if(resDTO.getMofAccount() != null){
			this.getManagedBean().setMofAccountNo(resDTO.getMofAccount().getMofNo());
		}
	}
	
	/**
	 * Default answer for Jenis Permohonan based on Application Type
	 * @param smQues
	 * @return SmAnswer
	 */
	private SmAnswer getApplicationTypeAnswer(SmQuestion smQues) {
		SmAnswer smAnswer = new SmAnswer();
		if (smQues.getSmAnswerCategory().getAnswerCategoryId() == 5) {
			for (SmAnswer smAnswerTmp : smQues.getSmAnswerCategory()
					.getSmAnswers()) {
				if (smAnswerTmp.getAnswerValue().equals(
						getManagedBean().getSmAppl().getApplType())) {
					smAnswer = smAnswerTmp;
					break;
				}
			}
		}
		return smAnswer;
	}

	/**
	 * Default answer for Status Permohonan based on SmWorkFlowStatus
	 * 
	 * @param smQues
	 * @param smAnswer
	 * @return SmAnswer
	 */
	private SmAnswer getApplicationStatus(SmQuestion smQues, SmAnswer smAnswer) {
		String answerStatus = "Ditolak";
		if (smQues.getSmAnswerCategory().getAnswerCategoryId() == 4) {
			SmWorkflowStatus sts = SmCommonUtil
					.getCurrentActiveWokFlowStatus(getManagedBean().getSmAppl()
							.getApplId());

			if (sts != null
					&& sts.getPmStatus() != null
					&& sts.getPmStatus().getStatusId() != null
					&& (sts.getPmStatus()
							.getStatusId()
							.compareTo(
									SupplierManagementConst.PM_STATUS_MOF_ACCT_REG_APPROVED) == 0
							|| sts.getPmStatus()
									.getStatusId()
									.compareTo(
											SupplierManagementConst.PM_STATUS_MOF_ACCT_MAINTAIN_APPROVED) == 0
							|| sts.getPmStatus()
									.getStatusId()
									.compareTo(
											SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT_AFTER_APPROVED) == 0 || sts
							.getPmStatus()
							.getStatusId()
							.compareTo(
									SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT_REPORT_AFTER_APPROVED) == 0)) {
				answerStatus = "Diluluskan";
			}
			for (SmAnswer smAnswerTmp : smQues.getSmAnswerCategory()
					.getSmAnswers()) {
				if (smAnswerTmp.getAnswerValue().equals(answerStatus)) {
					smAnswer = smAnswerTmp;
					break;
				}
			}
		}
		return smAnswer;
	}
	
}
