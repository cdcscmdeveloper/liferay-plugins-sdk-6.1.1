package my.ep.web.backingbean.sm.questionnaire;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.questionnaire.IQuestionnaireSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.questionnaire.QuestionnaireReqDTO;
import my.ep.dto.sm.questionnaire.QuestionnaireResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAnswer;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmQuestion;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierAnswer;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.questionnaire.QuestionnaireManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.primefaces.model.StreamedContent;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.liferay.portal.util.PortalUtil;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

@ManagedBean(name = "questionnaireBackingBean")
@RequestScoped
public class QuestionnaireBackingBean extends BaseBackingBean<QuestionnaireManagedBean> {
	public static Logger log = Logger.getLogger(QuestionnaireBackingBean.class);

	public QuestionnaireBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
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

	public void submitQuestionnaire() {
		log.info("***********submitQuestionnaire Start**************");
		// this.getManagedBean().getSmSupp().setSmSupplierAnswers(this.getManagedBean().getSmSuppAnswerLs());
		QuestionnaireReqDTO reqDTO = new QuestionnaireReqDTO();
		// for(SmSupplierAnswer sa : this.getManagedBean().getSmSuppAnswerLs()){
		// log.info("****88888888888888888*****" +
		// sa.getSmAnswer().getAnswerId());
		// }
		log.info("****88888888888888888*****");
		reqDTO.setSmSuppAns(this.getManagedBean().getSmSuppAnswerLs());
		reqDTO.setSmAppl(this.getManagedBean().getSmAppl());
		IQuestionnaireSO reqSO = (IQuestionnaireSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IQuestionnaireSO.class);
		QuestionnaireResDTO resDTO = reqSO.questionnaireSubmission(reqDTO);

		//return "viewVirtualCert";
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, this.getManagedBean().getTargetUrl());

	}
	
	
	
	private SmSupplier getSmSupplier() {
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		supplierSearchReqDTO.setUserId(pmUser.getUserId());

		ISupplierSearchSO supplierSearchSO= null;
		SmSupplier smSupp = null;
		try {
			supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
//			SupplierSearchResDTO smResDTO = supplierSearchSO.retrieveSupplierForVirtualCertification(supplierSearchReqDTO);
			SupplierSearchResDTO smResDTO = supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
			smSupp = (SmSupplier)smResDTO.getResultBean().getResultList().get(0);
		} finally {
			supplierSearchSO = null;
		}
		return smSupp;
	}
	
	public void validateQuestionnaire(Long applId, String targetUrl){
		System.out.println("********QuestionnaireBackingBean Start******");
		if(applId != null && targetUrl != null){
			//try{
				SmSupplier supplier = getSmSupplier();
				System.out.println("********QuestionnaireBackingBean isQuestionnaire******" + supplier.getSmAppl().isQuestionnaire());
				if(!supplier.getSmAppl().isQuestionnaire()){
					this.getManagedBean().setTargetUrl(targetUrl);
					this.getManagedBean().setSmSupp(supplier);
					this.getManagedBean().setSmAppl(supplier.getSmAppl());
					System.out.println("********QuestionnaireBackingBean******" + applId);
					//System.out.println("********QuestionnaireBackingBean type******" + type);

					retrieveQuestionnaire();
					//this.getManagedBean().setTargetUrl(targetUrl);
					this.getManagedBean().setSmSupp(supplier);
					SmMofAccount mofAcc = retrieveLatestMofAccount();
					this.getManagedBean().setSmMofAccount(mofAcc);
					FacesContext fc = FacesContext.getCurrentInstance();
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/questionnaire/questionnaireView.xhtml");

					return;
					
				}
				else{
					FacesContext fc = FacesContext.getCurrentInstance();
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null,targetUrl);
				}
//			}catch(Exception ex){
//				System.out.println("Exception>>>>" + ex.getMessage());
//			}
		}
	}
	
	public SmMofAccount retrieveLatestMofAccount(){
		SmMofAccount smMofAcc = new SmMofAccount();
		try {
			IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
	
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			MofRegistrationResDTO resDto = new MofRegistrationResDTO();
			
			reqDto.setSupplierId(getSmSupplier().getSupplierId());
			System.out.println("*********Supplier ID>" + reqDto.getSupplierId());
			resDto = mofRegistrationSO.retrieveSupplierProfile(reqDto);
			SmSupplier supplier = resDto.getSmSupplier();
			Date now = new Date();
			
			for(SmMofAccount mofAcc : supplier.getSmMofAccounts()){
				System.out.println("11111111111111111" + mofAcc);
				if(mofAcc.getEffDate().before(now) && mofAcc.getExpDate().after(now)){
					System.out.println("***mofAcc.getSupplierType()****" + mofAcc.getSupplierType());
					if(mofAcc.getSupplierType().equalsIgnoreCase(supplier.getSmAppl().getSupplierType())){
						smMofAcc = mofAcc;
					}
				}
			}
			
		}catch(Exception ex){
			System.out.println("**********retrieveLatestMofAccount exception>>>>>>" + ex.getMessage());
		}
		
		return smMofAcc;
	}
}
