package my.ep.web.backingbean.ss.supplierproposal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.ss.quationtender.IQTProposalSO;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.ss.quationtender.ProposalReqDTO;
import my.ep.dto.ss.quationtender.ProposalResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtDoc;
import my.ep.persistence.entity.ss.quationtender.ScQtProposal;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalChecklistAttachment;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.managedbean.ss.supplierproposal.SPDetailManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "sPDetailBB")
@RequestScoped
public class SPDetailBackingBean extends BaseBackingBean<SPDetailManagedBean> {
	public static Logger log = EpLogger.getLogger(SPDetailBackingBean.class);

	private static final String MODULE_NAME = "ss";
	private IQTProposalSO proposalSO = null;

	public SPDetailBackingBean() {
		IQTProposalSO proposalSO = BusinessDelegate.getInstance()
				.getServiceObject(MODULE_NAME, IQTProposalSO.class);

		this.proposalSO = proposalSO;

		if (getManagedBean().getQtProposal() == null) {
			retrieveTechnicalProposalList();
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
	public void retrieveTechnicalProposalList() {
		debugLogger("retrieveTechnicalProposalList");

		IQTProposalSO proposalSO = null;

		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					MODULE_NAME, IQTProposalSO.class);

			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setQtSupplierId(1L);

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

		List<ScQtProposalChecklist> scQtProposalChecklists = new ArrayList<ScQtProposalChecklist>();

		ScQtProposal scQtProposal = getManagedBean().getQtProposal();
		List<ScQtProposalChecklist> tecQtProposalChecklists = getManagedBean()
				.getTecCheckDetailsList();
		scQtProposalChecklists.addAll(tecQtProposalChecklists);

		tecQtProposalChecklists = getManagedBean().getFinanceCheckDetailsList();
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
		resDTO = proposalSO.saveProposalDtl(reqDTO);

	}

	public void handleFileUpload(FileUploadEvent event) {
		debugLogger("handleFileUpload");
		ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
				.getQtProposalChecklist();

		UploadedFile file = event.getFile();
		String fileName = file.getFileName();
		byte[] fileBytes = file.getContents();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"The file was uploaded successfully.", null);
		FacesContext.getCurrentInstance().addMessage(null, message);

		Timestamp date = new Timestamp(System.currentTimeMillis());
		ScQtProposalChecklistAttachment attachment = scQtProposalChecklist
				.getScQtProposalChecklistAttachment();
		if (null == attachment) {
			attachment = new ScQtProposalChecklistAttachment();
			attachment.setScQtProposalChecklist(scQtProposalChecklist);
			attachment.setCreatedBy(1L);
			attachment.setCreatedDate(date);
			attachment.setChangedBy(1L);
		}

		attachment.setFileName(fileName);
		attachment.setFileDesc("File uploded for Fulfilment Req Order");
		attachment.setFilePath("E:\\upload\\" + fileName);

		FileOutputStream fos;
		try {
			fos = new FileOutputStream("E:\\upload\\" + fileName);
			fos.write(fileBytes);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		scQtProposalChecklist.setScQtProposalChecklistAttachment(attachment);

	}

	public void downloadPTJFile(ScQtProposalChecklist scQtProposalChecklist)
			throws IOException {
		debugLogger("downloadFile");
		File file = new File("E:\\upload\\" + scQtProposalChecklist.getScQtCheckList()
				.getScQtCheckListAttachment().getFileName());
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

		debugLogger("newAction");

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

		ScQtProposalChecklistAttachment checklistAttachment = scQtProposalChecklist
				.getScQtProposalChecklistAttachment();

		File file = new File("E:\\upload\\" + checklistAttachment.getFileName());
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
}
