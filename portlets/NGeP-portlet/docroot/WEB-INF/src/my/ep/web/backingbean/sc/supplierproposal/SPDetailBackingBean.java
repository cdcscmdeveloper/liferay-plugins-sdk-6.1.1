package my.ep.web.backingbean.sc.supplierproposal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.ProposalResDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocReqDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklistAttachment;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPExperience;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPExperienceNCheckList;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.sc.supplierproposal.SPDetailManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "supplierProposalDetailBackingBean")
@RequestScoped
public class SPDetailBackingBean extends
		SourcingBaseBackingBean<SPDetailManagedBean> {

	// Technical Proposal
	private static final String NAV_TO_TECHNICAL_STANDARD_PRODUCT_PAGE = "/xhtml/sc/supplierproposal/technicalSpecification.xhtml";
	private static final String NAV_TO_NON_FEDERAL_GOVT_PAGE = "/xhtml/sc/supplierproposal/technicalNonFederalGovt.xhtml";
	private static final String NAV_TO_FEDERAL_GOVT_PAGE = "/xhtml/sc/supplierproposal/technicalFederalGovt.xhtml";
	private static final String NAV_TO_TECHNICAL_EMPL_LIST_PAGE = "/xhtml/sc/supplierproposal/technicalEmployeeList.xhtml";
	private static final String NAV_TO_NO_OF_EMPLOYEE__PAGE = "/xhtml/sc/supplierproposal/technicalNoOfEmployee.xhtml";
	private static final String NAV_TO_SAMPLING_ADDRESS_PAGE = "/xhtml/sc/supplierproposal/technicalSamplingAddress.xhtml";

	// Financial Proposal
	private static final String NAV_TO_FINANCE_STATNDARD_PRODUCT_PAGE = "/xhtml/sc/supplierproposal/financialSpecification.xhtml";
	private static final String NAV_TO_SUPPLIER_DETAILS_PAGE = "/xhtml/sc/supplierproposal/financialSupplierDetails.xhtml";
	private static final String NAV_TO_CREDIT_FACILITIES_PAGE = "/xhtml/sc/supplierproposal/financialCreditFacilities.xhtml";
	private static final String NAV_TO_GOVT_COMPANY_EXPERIENCE_PAGE = "/xhtml/sc/supplierproposal/financialFederalGovt.xhtml";
	private static final String NAV_TO_NON_GOVT_COMPANY_EXPERIENCE_PAGE = "/xhtml/sc/supplierproposal/financialNonFederalGovt.xhtml";
	private static final String NAV_TO_FINANCE_BANK_STATEMENT_PAGE = "/xhtml/sc/supplierproposal/financialBankStatement.xhtml";

	public static Logger log = EpLogger.getLogger(SPDetailBackingBean.class);

	public SPDetailBackingBean() {
		log.info("inside constructor");

		getManagedBean().getTemplateSpecBean().setSelectedSpecQuestion(
				new TplSpecQuestion());
	}

	@PostConstruct
	public void init() {
		try {
			if (this.getManagedBean().getTecCheckDetailsList() == null) {
				retrieveTechnicalProposalList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * retrieving Technical Proposal CheckList
	 */
	public void retrieveTechnicalProposalList() {
		debugLogger("retrieveTechnicalProposalList");
		Long supplierId = 1L;
		this.getManagedBean().setPrepareActiveIndex(0);
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setQtSupplierId(supplierId);

			ProposalResDTO resDTO = proposalSO
					.retrieveProposalDtlByQtSupplierId(reqDTO);

			// Technical Check List
			List<ScQtProposalChecklist> tecCheckDetailsList = resDTO
					.getTecChecklistResultBean().getResultList();
			log.info("Technical list: " + tecCheckDetailsList.size());

			this.getManagedBean().setQt(resDTO.getScQt());
			// this.getManagedBean().setQtProposal(resDTO.getScQtProposal());
			this.getManagedBean().setTecCheckDetailsList(tecCheckDetailsList);
		} finally {
			proposalSO = null;
		}
	}

	/**
	 * retrieving Financial Proposal CheckList
	 */
	public void retrieveFinancialProposalList() {
		debugLogger("retrieveFinancialProposalList");
		Long supplierId = 1L;
		this.getManagedBean().setPrepareActiveIndex(1);
		IQTProposalSO proposalSO = null;

		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setQtSupplierId(supplierId);

			ProposalResDTO resDTO = proposalSO
					.retrieveProposalDtlByQtSupplierId(reqDTO);

			// Financial Check List
			List<ScQtProposalChecklist> finCheckDetailsList = resDTO
					.getFinChecklistResultBean().getResultList();
			log.info("Financial list: " + finCheckDetailsList.size());

			this.getManagedBean().setQt(resDTO.getScQt());
			// this.getManagedBean().setQtProposal(resDTO.getScQtProposal());
			this.getManagedBean().setFinanceCheckDetailsList(
					finCheckDetailsList);
		} finally {
			proposalSO = null;
		}
	}

	public void retrieveDeclarationList() {
		debugLogger("retrieveDeclarationList");
		this.getManagedBean().setPrepareActiveIndex(2);
	}

	/**
	 * Method to save the SP details
	 */
	public void saveSPDetailsAction() {
		debugLogger("saveSPDetailsAction");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			List<ScQtProposalChecklist> scQtProposalChecklists = new ArrayList<ScQtProposalChecklist>();

			ScQtProposal scQtProposal = getManagedBean().getQtProposal();
			List<ScQtProposalChecklist> tecQtProposalChecklists = getManagedBean()
					.getTecCheckDetailsList();
			scQtProposalChecklists.addAll(tecQtProposalChecklists);

			tecQtProposalChecklists = getManagedBean()
					.getFinanceCheckDetailsList();
			scQtProposalChecklists.addAll(tecQtProposalChecklists);

			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setQtSupplierId(1L);

			ProposalResDTO resDTO = new ProposalResDTO();

			// Update Changes
			for (ScQtProposalChecklist proposalChecklist : scQtProposalChecklists) {
				proposalChecklist.setSubmitted(true);
			}
			scQtProposal.setScQtProposalChecklists(scQtProposalChecklists);

			reqDTO.setScQtProposal(scQtProposal);
			resDTO = proposalSO.saveQTDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "QT Proposal No. : "
						+ scQtProposal.getQtProposalId();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", message));
			} else {
				throw new EpException("Failed.");
			}
		} finally {
			proposalSO = null;
		}
	}

	public void savePRPExperinceInfo() {
		debugLogger("savePRPExperinceInfo");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			
			List<ScQtPRPExperienceNCheckList> experienceNCheckList = new ArrayList<ScQtPRPExperienceNCheckList>();
			
			//ScQtPRPExperience scQtPRPExperience = new ScQtPRPExperience();
			
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			
			ProposalResDTO resDTO = new ProposalResDTO();
			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean().getQtProposalChecklist();
			
			List<ScQtPRPExperience> experiences = this.getManagedBean().getScQtPRPExperiences();
			for (ScQtPRPExperience prpExperience : experiences) {
				ScQtPRPExperienceNCheckList checkList = new ScQtPRPExperienceNCheckList();
				checkList.setScQtProposalChecklist(scQtProposalChecklist);
				checkList.setScQtPRPExperience(prpExperience);
				experienceNCheckList.add(checkList);
			}		
					
			reqDTO.setExperienceNCheckList(experienceNCheckList);
			resDTO = proposalSO.savePRPExperinceInfo(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
			} else {
				throw new EpException("Failed.");
			}
		} finally {
			proposalSO = null;
		}
	}
	
	public QTTemplateDocResDTO saveFinancialTempleateSpecDtls() {
		IQTTemplateSO qtTemplateSO = null;
		IStatusSO statussO = null;
		IOrgProfileSO ProfileSo = null;
		QTTemplateDocReqDTO docReqDTO = new QTTemplateDocReqDTO();
		QTTemplateDocResDTO resDTO = new QTTemplateDocResDTO();
		try {
			ScQtDoc scQtDoc = this.getManagedBean().getQtProposalChecklist().getScQtCheckList().getScQtDoc();
			TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();
		} finally {
			qtTemplateSO = null;
		}
		return resDTO;
	}

	public void handleFileUpload(FileUploadEvent event) {
		debugLogger("handleFileUpload");
		FacesScopes facesScopes = new FacesScopes();
		if (event.getFile() != null) {
			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();
			ScQtProposalChecklistAttachment attachment = scQtProposalChecklist
					.getScQtProposalChecklistAttachment();
			if (null == attachment) {
				attachment = new ScQtProposalChecklistAttachment();
				attachment.setScQtProposalChecklist(scQtProposalChecklist);
			}
			attachment.setUser(facesScopes.getUserSessionBean());
			attachment
					.setChangedDate(new Timestamp(System.currentTimeMillis()));
			uploadAttachment(event.getFile(), attachment);
			scQtProposalChecklist
					.setScQtProposalChecklistAttachment(attachment);
		}
	}

	public void downloadPTJFile(ScQtProposalChecklist scQtProposalChecklist)
			throws IOException {
		debugLogger("downloadPTJFile");
		File file = new File(scQtProposalChecklist.getScQtCheckList()
				.getScQtCheckListAttachment().getFilePath());
		InputStream input = new FileInputStream(file);

		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);
		res.setHeader("Content-Disposition", "attachment; filename=\""
				+ scQtProposalChecklist.getScQtCheckList()
						.getScQtCheckListAttachment().getFileName() + "\"");
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();
		if (scQtProposalChecklist.getScQtCheckList()
				.getScQtCheckListAttachment().getFilePath() != null) {
			try {
				URLConnection uc = new URL(scQtProposalChecklist
						.getScQtCheckList().getScQtCheckListAttachment()
						.getFilePath()).openConnection();
				input = new BufferedInputStream(uc.getInputStream());
			} catch (Exception e) {
				// Cannot open connection, maybe local file
				input = new FileInputStream(scQtProposalChecklist
						.getScQtCheckList().getScQtCheckListAttachment()
						.getFilePath());
			}
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				buffer = new byte[4096];
			}
			input.close();
		} else {
			// out.write(attachment.getFileByte());
		}
		out.flush();
		out.close();
		FacesContext.getCurrentInstance().responseComplete();
	}

	public void onlineAction(ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("onlineAction");

		ScQtCheckList scQtCheckList = scQtProposalChecklist.getScQtCheckList();

		ScQtDoc scQtDoc = scQtCheckList.getScQtDoc();

		TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
		TemplateSpecBean templateSpecBean = getManagedBean()
				.getTemplateSpecBean();
		TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();

		templateSpecBean.setSpecMaster(specMaster);
		templateSpecBean.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);

		Long userId = new FacesScopes().getUserSessionBean().getUserId();
		/* Long userId = 1L; */
		IRegistrationSO registrationSO = BusinessDelegate.getInstance()
				.getServiceObject("sm", IRegistrationSO.class);
		RegistrationReqDTO registrationReqDTO = new RegistrationReqDTO();
		registrationReqDTO.setSupplierId(userId);
		RegistrationResDTO registrationResDTO = registrationSO
				.retrieveSuppOnlineAcc(registrationReqDTO);
		templateSpecBean.setSupplier(registrationResDTO.getSmSupp());

		templateSpecBackingBean.retrieveTemplateSpecMaster(templateSpecBean);
	}

	public void downloadSPFile(ScQtProposalChecklist scQtProposalChecklist)
			throws Exception {
		debugLogger("downloadSPFile");
		/*
		 * ScQtProposalChecklistAttachment checklistAttachment =
		 * scQtProposalChecklist .getScQtProposalChecklistAttachment();
		 * System.out.println("File Name : " +
		 * checklistAttachment.getFilePath());
		 * 
		 * File file = new File(checklistAttachment.getFilePath()); InputStream
		 * input = new FileInputStream(file);
		 * 
		 * PortletResponse portletResponse = (PortletResponse) FacesContext
		 * .getCurrentInstance().getExternalContext().getResponse();
		 * HttpServletResponse res = com.liferay.portal.util.PortalUtil
		 * .getHttpServletResponse(portletResponse);
		 * res.setHeader("Content-Disposition", "attachment; filename=\"" +
		 * checklistAttachment.getFileName() + "\"");
		 * res.setHeader("Content-Transfer-Encoding", "binary");
		 * res.setContentType("application/octet-stream"); res.flushBuffer();
		 * OutputStream out = res.getOutputStream(); if
		 * (checklistAttachment.getFilePath() != null) {
		 * 
		 * try { URLConnection uc = new URL(checklistAttachment.getFilePath())
		 * .openConnection(); input = new
		 * BufferedInputStream(uc.getInputStream()); } catch (Exception e) { //
		 * Cannot open connection, maybe local file input = new
		 * FileInputStream(checklistAttachment.getFilePath()); } byte[] buffer =
		 * new byte[4096]; int bytesRead; while ((bytesRead =
		 * input.read(buffer)) != -1) { out.write(buffer, 0, bytesRead); buffer
		 * = new byte[4096]; } input.close(); } else { //
		 * out.write(attachment.getFileByte()); } out.flush(); out.close();
		 */

	}

	public String createProposal() {

		return null;
	}

	public String updateBpmTask(String action) {

		ProposalReqDTO prReqDTO = constructReqDTO();
		prReqDTO.setOutcome(action);
		prReqDTO.setTaskId(this.getManagedBean().getTaskId());
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		prReqDTO.setToken(ctx);
		prReqDTO.setPayLoad(this.getManagedBean().getProposalData());
		IQTProposalSO proSo = getProposalSO();
		try {
			ProposalResDTO resDTO = proSo.updateBPMAction(prReqDTO);
			if (resDTO.isSuccessful()) {

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", " Type. : "

						));

				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								"/xhtml/shared/acknowledge.xhtml");
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sorry We are unable to Process", " Type. : "

					));
		}
		return null;

	}

	private IQTProposalSO getProposalSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
	}

	private ProposalReqDTO constructReqDTO() {
		List<ScQtProposalChecklist> scQtProposalChecklists = new ArrayList<ScQtProposalChecklist>();

		ScQtProposal scQtProposal = getManagedBean().getQtProposal();
		List<ScQtProposalChecklist> tecQtProposalChecklists = getManagedBean()
				.getTecCheckDetailsList();
		scQtProposalChecklists.addAll(tecQtProposalChecklists);

		tecQtProposalChecklists = getManagedBean().getFinanceCheckDetailsList();
		scQtProposalChecklists.addAll(tecQtProposalChecklists);

		ProposalReqDTO reqDTO = new ProposalReqDTO();
		reqDTO.setQtSupplierId(1L);

		// Update Changes
		for (ScQtProposalChecklist proposalChecklist : scQtProposalChecklists) {
			proposalChecklist.setSubmitted(true);
		}
		scQtProposal.setScQtProposalChecklists(scQtProposalChecklists);

		reqDTO.setScQtProposal(scQtProposal);
		return reqDTO;
	}

	public String prepareTechnicalAction(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("prepareTechnicalAction");

		if ("TS".equalsIgnoreCase(scQtProposalChecklist.getScQtCheckList()
				.getDefinationType().getParameterCode())) {

			ScQtCheckList scQtCheckList = scQtProposalChecklist
					.getScQtCheckList();
			this.getManagedBean().setScQtCheckList(scQtCheckList);

			ScQtDoc scQtDoc = scQtCheckList.getScQtDoc();
			try {
				TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
				TemplateSpecBean templateSpecBean = getManagedBean()
						.getTemplateSpecBean();
				TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();

				templateSpecBean.setSpecMaster(specMaster);
				templateSpecBean
						.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);
				templateSpecBean.setSupplier(scQtProposalChecklist
						.getScQtProposal().getScQtSupplier().getSmSupplier());
				templateSpecBackingBean
						.retrieveTemplateSpecMaster(templateSpecBean);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return NAV_TO_TECHNICAL_STANDARD_PRODUCT_PAGE
					+ "?faces-redirect=true";
		} else {
			if ("TN".equalsIgnoreCase(scQtProposalChecklist.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey())) {
				ScQtCheckList scQtCheckList = scQtProposalChecklist
						.getScQtCheckList();
				this.getManagedBean().setScQtCheckList(scQtCheckList);

				return NAV_TO_NON_FEDERAL_GOVT_PAGE + "?faces-redirect=true";
			} else if ("TL".equalsIgnoreCase(scQtProposalChecklist
					.getScQtCheckList().getPmDocument().getDocDefinitionKey())) {

				return NAV_TO_TECHNICAL_EMPL_LIST_PAGE + "?faces-redirect=true";
			} else if ("NE".equalsIgnoreCase(scQtProposalChecklist
					.getScQtCheckList().getPmDocument().getDocDefinitionKey())) {

				return NAV_TO_NO_OF_EMPLOYEE__PAGE + "?faces-redirect=true";
			}

		}
		return null;
	}

	public String prepareTechnicalViewAction(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("prepareTechnicalViewAction");
		if ("SP".equalsIgnoreCase(scQtProposalChecklist.getScQtCheckList()
				.getDefinationType().getParameterCode())) {
			ScQtCheckList scQtCheckList = scQtProposalChecklist
					.getScQtCheckList();
			this.getManagedBean().setScQtCheckList(scQtCheckList);

			return NAV_TO_SAMPLING_ADDRESS_PAGE + "?faces-redirect=true";
		} else {
			if ("TG".equalsIgnoreCase(scQtProposalChecklist.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey())) {
				ScQtCheckList scQtCheckList = scQtProposalChecklist
						.getScQtCheckList();
				this.getManagedBean().setScQtCheckList(scQtCheckList);

				return NAV_TO_FEDERAL_GOVT_PAGE + "?faces-redirect=true";
			}
		}
		return null;
	}

	public String prepareFinancialAction(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("prepareFinancialAction");
		IQTProposalSO proposalSO = null;
		ProposalResDTO resDTO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			if ("TS".equalsIgnoreCase(scQtProposalChecklist.getScQtCheckList()
					.getDefinationType().getParameterCode())) {
				this.getManagedBean().setQtProposalChecklist(scQtProposalChecklist);
				ScQtCheckList scQtCheckList = scQtProposalChecklist
						.getScQtCheckList();
				this.getManagedBean().setScQtCheckList(scQtCheckList);

				ScQtDoc scQtDoc = scQtCheckList.getScQtDoc();
				try {
					TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
					TemplateSpecBean templateSpecBean = getManagedBean()
							.getTemplateSpecBean();
					TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();

					templateSpecBean.setSpecMaster(specMaster);
					templateSpecBean
							.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);
					templateSpecBean.setSupplier(scQtProposalChecklist
							.getScQtProposal().getScQtSupplier()
							.getSmSupplier());
					templateSpecBackingBean
							.retrieveTemplateSpecMaster(templateSpecBean);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return NAV_TO_FINANCE_STATNDARD_PRODUCT_PAGE
						+ "?faces-redirect=true";
			} else {
				if ("FN".equalsIgnoreCase(scQtProposalChecklist
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {
					this.getManagedBean().setQtProposalChecklist(scQtProposalChecklist);
					ScQtCheckList scQtCheckList = scQtProposalChecklist
							.getScQtCheckList();
					this.getManagedBean().setScQtCheckList(scQtCheckList);
					reqDTO.setQtCheckListId(scQtCheckList.getQtChecklistId());
					reqDTO.setDocDefinationKey(scQtCheckList.getPmDocument()
							.getDocDefinitionKey());

					resDTO = proposalSO.retrieveNonGovCompanyExpDtls(reqDTO);

					System.out.println(" ************ "
							+ resDTO.getPrpExperience());
					this.getManagedBean().setScQtPRPExperiences(
							resDTO.getPrpExperience());

					return NAV_TO_NON_GOVT_COMPANY_EXPERIENCE_PAGE
							+ "?faces-redirect=true";
				} else if ("CF".equalsIgnoreCase(scQtProposalChecklist
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {
					ScQtCheckList scQtCheckList = scQtProposalChecklist
							.getScQtCheckList();
					this.getManagedBean().setScQtCheckList(scQtCheckList);

					return NAV_TO_CREDIT_FACILITIES_PAGE
							+ "?faces-redirect=true";
				} else if ("CH".equalsIgnoreCase(scQtProposalChecklist
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {

				} else if ("BS".equalsIgnoreCase(scQtProposalChecklist
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {
					ScQtCheckList scQtCheckList = scQtProposalChecklist
							.getScQtCheckList();
					this.getManagedBean().setScQtCheckList(scQtCheckList);

					return NAV_TO_FINANCE_BANK_STATEMENT_PAGE
							+ "?faces-redirect=true";
				} else if ("AL".equalsIgnoreCase(scQtProposalChecklist
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {

				} else if ("SD".equalsIgnoreCase(scQtProposalChecklist
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {
					ScQtCheckList scQtCheckList = scQtProposalChecklist
							.getScQtCheckList();
					this.getManagedBean().setScQtCheckList(scQtCheckList);

					return NAV_TO_SUPPLIER_DETAILS_PAGE
							+ "?faces-redirect=true";
				}
			}
		} finally {
			proposalSO = null;
		}
		return null;
	}

	public String prepareFinancialViewAction(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("prepareFinancialViewAction");
		if ("SP".equalsIgnoreCase(scQtProposalChecklist.getScQtCheckList()
				.getDefinationType().getParameterCode())) {
			ScQtCheckList scQtCheckList = scQtProposalChecklist
					.getScQtCheckList();
			this.getManagedBean().setScQtCheckList(scQtCheckList);

			return null;
		} else {
			if ("FG".equalsIgnoreCase(scQtProposalChecklist.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey())) {
				ScQtCheckList scQtCheckList = scQtProposalChecklist
						.getScQtCheckList();
				this.getManagedBean().setScQtCheckList(scQtCheckList);

				return NAV_TO_GOVT_COMPANY_EXPERIENCE_PAGE
						+ "?faces-redirect=true";
			} else if ("SD".equalsIgnoreCase(scQtProposalChecklist
					.getScQtCheckList().getPmDocument().getDocDefinitionKey())) {
				ScQtCheckList scQtCheckList = scQtProposalChecklist
						.getScQtCheckList();
				this.getManagedBean().setScQtCheckList(scQtCheckList);

				return NAV_TO_SUPPLIER_DETAILS_PAGE + "?faces-redirect=true";
			}
		}

		return null;
	}

	public void addNonFederalGovtExperience() {
		debugLogger("addNonFederalGovtExperience");
		ScQtPRPExperience prpExperience = new ScQtPRPExperience();
		try {
			List<ScQtPRPExperience> experienceList = this.getManagedBean()
					.getScQtPRPExperiences();
			if (null == experienceList) {
				experienceList = new ArrayList<ScQtPRPExperience>();
			}
			for (ScQtPRPExperience scQtPRPExperience : experienceList) {
				scQtPRPExperience.setBrandNew(false);
			}
			experienceList.add(prpExperience);
			this.getManagedBean().setScQtPRPExperiences(experienceList);
			System.out.println("List Size : "
					+ getManagedBean().getScQtPRPExperiences().size());
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void deleteNonFederalGovtExperience() {
		debugLogger("deleteNonFederalGovtExperience");
		List<ScQtPRPExperience> experienceList = this.getManagedBean()
				.getScQtPRPExperiences();
		List<ScQtPRPExperience> selectedExperienceList = this.getManagedBean()
				.getSelectedPRPExperiences();
		if (experienceList.size() == selectedExperienceList.size()) {
			experienceList.clear();
		} else {
			for (ScQtPRPExperience scQtPRPExperience : selectedExperienceList) {
				experienceList.remove(scQtPRPExperience);
			}
		}
	}

	public String supplierProposalBackAction() {
		debugLogger("supplierProposalBackAction");
		return "/xhtml/sc/supplierproposal/supplierProposalMain.xhtml?faces-redirect=true";
	}

	public String prepareNonFederalGovtAction(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("prepareNonFederalGovtAction");
		ScQtCheckList scQtCheckList = scQtProposalChecklist.getScQtCheckList();
		this.getManagedBean().setScQtCheckList(scQtCheckList);

		return NAV_TO_NON_FEDERAL_GOVT_PAGE + "?faces-redirect=true";
	}

	public String prepareFederalGovtAction(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("prepareFederalGovtAction");
		ScQtCheckList scQtCheckList = scQtProposalChecklist.getScQtCheckList();
		this.getManagedBean().setScQtCheckList(scQtCheckList);

		return NAV_TO_FEDERAL_GOVT_PAGE + "?faces-redirect=true";
	}

	public String prepareSupplierDetailsAction(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("prepareSupplierDetailsAction");
		ScQtCheckList scQtCheckList = scQtProposalChecklist.getScQtCheckList();
		this.getManagedBean().setScQtCheckList(scQtCheckList);

		return NAV_TO_SUPPLIER_DETAILS_PAGE + "?faces-redirect=true";
	}

	public String prepareCreditFacilitiesAction(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("prepareCreditFacilitiesAction");
		ScQtCheckList scQtCheckList = scQtProposalChecklist.getScQtCheckList();
		this.getManagedBean().setScQtCheckList(scQtCheckList);

		return NAV_TO_CREDIT_FACILITIES_PAGE + "?faces-redirect=true";
	}

	public String prepareCompanyExperienceGovtAction(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("prepareCompanyExperienceGovtAction");
		ScQtCheckList scQtCheckList = scQtProposalChecklist.getScQtCheckList();
		this.getManagedBean().setScQtCheckList(scQtCheckList);

		return NAV_TO_GOVT_COMPANY_EXPERIENCE_PAGE + "?faces-redirect=true";
	}
}
