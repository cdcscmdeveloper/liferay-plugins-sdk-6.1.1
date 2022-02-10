package my.ep.web.backingbean.sm.questionnaire;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import my.ep.constant.EpPropertiesConst;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.questionnaire.QuestionnaireReqDTO;
import my.ep.dto.sm.questionnaire.QuestionnaireResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmAnswer;
import my.ep.persistence.entity.sm.SmQuestion;
import my.ep.persistence.entity.sm.SmSupplierAnswer;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.sm.questionnaire.QuestionnaireManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "questionnaireBackingBean")
@RequestScoped
public class QuestionnaireBackingBean extends
		BaseBackingBean<QuestionnaireManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1346494311859740969L;
	public static Logger log = Logger.getLogger(QuestionnaireBackingBean.class);

	public QuestionnaireBackingBean() {
		SmCommonUtil.setMDCUserId();
	}

	public void retrieveQuestionnaire() {
		QuestionnaireResDTO resDTO = SmServiceLookup.getQuestionnaireSO()
				.retrieveActiveQuestion();

		List<SmSupplierAnswer> smSuppAnswerLs = new ArrayList<SmSupplierAnswer>();
		for (SmQuestion smQues : resDTO.getSmQuestionList()) {
			SmSupplierAnswer smSuppAns = new SmSupplierAnswer();
			smSuppAns.setSmQuestion(smQues);
			smSuppAns.setSmAnswer(getApplicationTypeAnswer(smQues));
			smSuppAns.setSmAnswer(getApplicationStatus(smQues,
					smSuppAns.getSmAnswer()));
			smSuppAnswerLs.add(smSuppAns);

			if (smQues.getSmAnswerCategory().getAnswerCategoryId() == 2) {
				smSuppAns.getSmAnswer().setAnswerId(
						smQues.getSmAnswerCategory().getSmAnswers().get(0)
								.getAnswerId());
			}
		}
		this.getManagedBean().setSmSuppAnswerLs(smSuppAnswerLs);

		// retrieve MOF Info - to display MOF Account No
		retrievMOFInfo();
	}


	//<-- START #12768 -->
	/**
	 * Download Questionnaire template report in PDF
	 * @throws Exception
	 */
	public void downloadQuestionnaire()throws Exception{
		
		List<SmSupplierAnswer> smSuppAnswerLs = new ArrayList<SmSupplierAnswer>();
		smSuppAnswerLs = getManagedBean().getSmSuppAnswerLs();

		printReportVM("questionnaire","SM_QUESTIONNAIRE_FORM.pdf","/sm/questionnaire/questionnaire_form.vm",setDetail());
		
	}
	
	/**
	 * get the VelocityContext
	 * @return context
	 */
	public VelocityContext setDetail(){
	
		VelocityContext context = new VelocityContext();	
		FacesScopes face = new FacesScopes();
		
		String lblApplType = getsupplierApplTypeDesc(this.getManagedBean().getSmAppl().getApplType());

		context.put("hostUrl", getLogoHeaderPath());
		context.put("title",  ResourceBundleUtils.getMessage("lbl.sm.questionaire.answer.header",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("feedbacknote1",  ResourceBundleUtils.getMessage("lbl.sm.questionaire.feedback.note1",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("feedbacknote2",  ResourceBundleUtils.getMessage("lbl.sm.questionaire.feedback.note2",ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));

		context.put("companyName",  this.getManagedBean().getSmSupp().getCompanyName()); 
		context.put("mof", this.getManagedBean().getMofAccountNo());
		context.put("applNo",  this.getManagedBean().getSmAppl().getApplNo());
		context.put("applType",ResourceBundleUtils.getMessage(lblApplType,ResourceBundleUtils.BUNDLE_COMMON_LABELS,face.getUserLocale()));
		context.put("question", this.getManagedBean().getSmSuppAnswerLs());
		
		return context;
	}
	
	/**
	 * get the printReportVM
	 * @return String
	 */
	public void printReportVM(String tmpFolder,String fileName,String vmFile,VelocityContext context){
		String filePath = null;
		filePath = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		
		filePath = filePath + "/" + tmpFolder;
       
		File file = new File(filePath);
		file.mkdirs();
		AttachmentFormat attachmentFormat = null;
		attachmentFormat = new AttachmentDateFormat();
		String newName = attachmentFormat.format(fileName, null);
		filePath = filePath + "/"+ newName;
		OutputStream os1 = null;
		try{
			os1 = new FileOutputStream(filePath);
			DocumentGenerator.getInstance().generatePdfFromVelocity(vmFile, context, os1);		
			os1.flush();
			os1.close();
			this.getManagedBean().setDownloadfile(commonDownload(tmpFolder+"/"+newName, fileName));
		}catch(Exception e){
			log.error("File: QuestionnaireBaseBackingBean, printReportVM, Message: "+e.getMessage());
		}
	}
	
	/**
	 * get the template logo path 
	 * @return String
	 */
	private String getLogoHeaderPath(){
		//BELOW CHANGES IS TEMPORARY BECAUSE OF TECH REFRESH, CANNOT READ URL IMAGE IN VM REPORT SM
		// FUNCTION INVOLVED IS getLogoHeaderPath && getServerUrl
		log.info("DEV URL SERVER :"+getServerUrl());
		return getServerUrl();
		//return SmCommonUtil.getBasePortalUrl();
	}
	//<-- END #12768 -->

	
	public void submitQuestionnaire() {
		QuestionnaireReqDTO reqDTO = new QuestionnaireReqDTO();
		reqDTO.setSmSupplier(this.getManagedBean().getSmSupp());
		reqDTO.setSmAppl(this.getManagedBean().getSmAppl());
		reqDTO.setOriResubmitSmAppl(this.getManagedBean().getRejectedAppl());

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
		reqDTO.setAnswers(this.getManagedBean().getSmSuppAnswerLs());
		reqDTO.setUserList(useList);
		reqDTO.setToken(fs.getBPMAuthenticationCtxToken());
		// #12491
		reqDTO.setPmUser(loginUser);
		QuestionnaireResDTO resDTO = SmServiceLookup.getQuestionnaireSO()
				.questionnaireSubmission(reqDTO);
		// check if need to init task for resubmit application
		if (resDTO.isInitTask()) {
			SmApplicationUtil.initiateMofApplNewBpmTask(resDTO.getSmAppl());
		}

		// for resubmit application directly inject to task list
		if (this.getManagedBean().getRejectedAppl() != null) {
			SmCommonUtil.redirectToPage(this.getManagedBean().getTargetUrl(),
					SmCommonUtil.getMessage("lbl.sm.submit.questionaire.msg",
							null), null, null);
		} else {
			SmCommonUtil.redirectToPage(this.getManagedBean().getTargetUrl(),
					null, null, null);
		}

		renewSesionObject();
	}

	public String getsupplierApplTypeDesc(String applType) {
		return SmCommonUtil.getApplTypeDesc(applType);
	}

	public void retrievMOFInfo() {
		MofAccountReqDTO reqDTO = new MofAccountReqDTO();
		reqDTO.setSupplierId(this.getManagedBean().getSmSupp().getSupplierId());
		reqDTO.setSupplierType(this.getManagedBean().getSmAppl()
				.getSupplierType());
		MofAccountResDTO resDTO = SmServiceLookup.getMofAccountSO()
				.retriveMOFAccountBySupplierIdAndType(reqDTO);

		if (resDTO.getMofAccount() != null) {
			this.getManagedBean().setMofAccountNo(
					resDTO.getMofAccount().getMofNo());
		}
	}

	/**
	 * Default answer for Jenis Permohonan based on Application Type
	 * 
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


	//BELOW CHANGES IS TEMPORARY BECAUSE OF TECH REFRESH, CANNOT READ URL IMAGE IN VM REPORT SM
	// FUNCTION INVOLVED IS getLogoHeaderPath && getServerUrl
	/**
	 * Gets the server url.
	 *
	 * @return the server url
	 */
	private String getServerUrl() {
		ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
		PortletRequest portletRequest = (PortletRequest) extcontext.getRequest();
		HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
		String requestURL = httpServletRequest.getRequestURL().toString();
		String url = requestURL.substring(0, requestURL.length()- httpServletRequest.getRequestURI().length())+ httpServletRequest.getContextPath() + "/";

		return url;
	}

}
