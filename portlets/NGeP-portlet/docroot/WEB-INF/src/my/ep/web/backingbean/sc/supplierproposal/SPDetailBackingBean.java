package my.ep.web.backingbean.sc.supplierproposal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.ProposalResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklistAttachment;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.sc.Data.SC_SiteVisitProposal_Data.SC_SiteVisitProposal_DataType;
import my.ep.sc.bpmnProcess.ProposalPreparation.ProposalPreparationPortType;
import my.ep.sc.bpmnProcess.ProposalPreparation.ProposalPreparationServiceLocator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.managedbean.sc.supplierproposal.SPDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "sPDetailBackingBean")
@RequestScoped
public class SPDetailBackingBean extends
		SourcingBaseBackingBean<SPDetailManagedBean> {
	public static Logger log = EpLogger.getLogger(SPDetailBackingBean.class);

	private static final String MODULE_NAME = "ss";
	private IQTProposalSO proposalSO = null;

	public SPDetailBackingBean() {
		IQTProposalSO proposalSO = BusinessDelegate.getInstance()
				.getServiceObject(MODULE_NAME, IQTProposalSO.class);

		this.proposalSO = proposalSO;

		if (getManagedBean().getQtProposal() == null) {
			retrieveTechnicalProposalList(3l);
		}

		log.info("inside constructor");

		TemplateSpecBackingBean tmpSpecBB = new TemplateSpecBackingBean();
		tmpSpecBB.getManagedBean().setSelectedSpecQuestion(
				new TplSpecQuestion());

	}

	@PostConstruct
	public void init() {
	}

	/**
	 * retrieving Technical Proposal CheckList
	 */
	public void retrieveTechnicalProposalList(Long supplierId) {
		debugLogger("retrieveTechnicalProposalList");

		IQTProposalSO proposalSO = null;

		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					MODULE_NAME, IQTProposalSO.class);

			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setQtSupplierId(supplierId);

			ProposalResDTO resDTO = proposalSO
					.retrieveProposalDtlByQtSupplierId(reqDTO);

			// Technical Check List
			List<ScQtProposalChecklist> tecCheckDetailsList = resDTO
					.getTecChecklistResultBean().getResultList();
			System.out.println("Technical list: " + tecCheckDetailsList.size());
			// Financial Check List
			List<ScQtProposalChecklist> finCheckDetailsList = resDTO
					.getFinChecklistResultBean().getResultList();
			System.out.println("Financial list: " + finCheckDetailsList.size());

			this.getManagedBean().setQtProposal(resDTO.getScQtProposal());
			this.getManagedBean().setTecCheckDetailsList(tecCheckDetailsList);
			this.getManagedBean().setFinanceCheckDetailsList(
					finCheckDetailsList);
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));
		}
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * Method to save the SP details
	 */
	public void saveSPDetailsAction() {
		debugLogger("saveSPDetailsAction");

		try {
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
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
	}

	public void handleFileUpload(FileUploadEvent event) {
		debugLogger("handleFileUpload");
		ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
				.getQtProposalChecklist();

		ScQtProposalChecklistAttachment attachment = scQtProposalChecklist
				.getScQtProposalChecklistAttachment();
		if (null == attachment) {
			attachment = new ScQtProposalChecklistAttachment();
			attachment.setScQtProposalChecklist(scQtProposalChecklist);
		}
		uploadAttachment(event.getFile(), attachment);
		scQtProposalChecklist.setScQtProposalChecklistAttachment(attachment);

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
		TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();

		templateSpecBackingBean.getManagedBean().setSpecMaster(specMaster);
		templateSpecBackingBean.getManagedBean().setContentType(
				TemplateSpecConst.CONTENT_TYPE_ANSWER);

		Long userId = new FacesScopes().getUserSessionBean().getUserId();
		/* Long userId = 1L; */
		IRegistrationSO registrationSO = BusinessDelegate.getInstance()
				.getServiceObject("sm", IRegistrationSO.class);
		RegistrationReqDTO registrationReqDTO = new RegistrationReqDTO();
		registrationReqDTO.setSupplierId(userId);
		RegistrationResDTO registrationResDTO = registrationSO
				.retrieveSuppOnlineAcc(registrationReqDTO);
		templateSpecBackingBean.getManagedBean().setSupplier(
				registrationResDTO.getSmSupp());

		templateSpecBackingBean.retrieveTemplateSpecMaster();
	}

	public void downloadSPFile(ScQtProposalChecklist scQtProposalChecklist)
			throws Exception {
		debugLogger("downloadSPFile");
		ScQtProposalChecklistAttachment checklistAttachment = scQtProposalChecklist
				.getScQtProposalChecklistAttachment();
		System.out.println("File Name : " + checklistAttachment.getFilePath());

		File file = new File(checklistAttachment.getFilePath());
		InputStream input = new FileInputStream(file);

		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);
		res.setHeader("Content-Disposition", "attachment; filename=\""
				+ checklistAttachment.getFileName() + "\"");
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();
		if (checklistAttachment.getFilePath() != null) {

			try {
				URLConnection uc = new URL(checklistAttachment.getFilePath())
						.openConnection();
				input = new BufferedInputStream(uc.getInputStream());
			} catch (Exception e) {
				// Cannot open connection, maybe local file
				input = new FileInputStream(checklistAttachment.getFilePath());
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

	}

	public String createProposal() {
		ProposalPreparationServiceLocator locator = new ProposalPreparationServiceLocator();
		// locator.setSiteVisitPortEndpointAddress("http://192.168.120.17:8001/soa-infra/services/default/Sourcing/SiteVisit.service");

		SC_SiteVisitProposal_DataType dataType = new SC_SiteVisitProposal_DataType();
		System.out.println(dataType);
		String qtno = this.getManagedBean().getSupplierId();

		try {
			if (qtno == null) {
				throw new EpException("");
			} else {
				dataType.setQt_id(new Integer(qtno));

			}
			dataType.setSupplier(new FacesScopes().getUserScreenName());
			//Calendar c = Calendar.getInstance(); // gives u calendar with
			Calendar calendar = Calendar.getInstance();

			// Set the time for the notification to occur.
			calendar.set(Calendar.YEAR, 2013);
			calendar.set(Calendar.MONTH, 12);
			calendar.set(Calendar.DAY_OF_MONTH, 13);
			calendar.set(Calendar.HOUR_OF_DAY, 10);
			calendar.set(Calendar.MINUTE, 45);
			calendar.set(Calendar.SECOND, 0); // current time

			dataType.setClosing_time(calendar);
			dataType.setApproval(true);

			ProposalPreparationPortType port = locator
					.getProposalPreparationPort();

			String docnumber = "123we34";
			port.start(dataType, docnumber);

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/shared/acknowledge.xhtml");
		} catch (ServiceException e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Unable to process.", ""));
		} catch (RemoteException e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Unable to process.", ""));
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Unable to process.", ""));
		}
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
		return EpEjbBusinessDelegate.getInstance().getServiceObject(
				QTConstants.SS_MODULE, IQTProposalSO.class);
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
}
