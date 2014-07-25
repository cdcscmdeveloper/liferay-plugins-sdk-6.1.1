package my.ep.web.backingbean.sc.quatationtender;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocReqDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.specification.SpecificationReqDTO;
import my.ep.dto.sc.specification.SpecificationResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.tpl.doc.TemplateDocBackingBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

import com.liferay.portal.util.PortalUtil;

/**
 * @author SudheerKumar
 * 
 */
@ManagedBean(name = "qTDocumentsBackingBean")
@RequestScoped
public class QTDocumentsBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	private static final String NAV_TO_TEMPLATE_DOC_MAINTENANACE = "/xhtml/sc/quatationtender/templateDocument/templateDocMaintenance.xhtml";

	private static final Logger log = EpLogger
			.getLogger(QTDocumentsBackingBean.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public QTDocumentsBackingBean() {
	}

	public void addDocumentList() {
		debugLogger("addDocumentList");
		List<ScQtCheckList> qtCheckLists = getManagedBean().getScQtChecklists();
		if (null == qtCheckLists) {
			qtCheckLists = new ArrayList<ScQtCheckList>();
		}
		for (ScQtCheckList existedCheckList : qtCheckLists) {
			existedCheckList.setBrandNew(false);
		}
		ScQtCheckList checkList = new ScQtCheckList();
		PmParameter parameter = new PmParameter();
		parameter.setParameterId(184l);
		checkList.setDefinationType(parameter);
		checkList.setDocCategory(null);
		checkList.setScoringReq(true);
		checkList.setChecklistSeq(1);
		checkList.setChangedBy(1L);
		checkList.setCreatedBy(1L);
		checkList.setScQt(getManagedBean().getQt());

		IParameterSO parameterSO = (IParameterSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterId(1l);
		ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
		checkList.setAction(resDTO.getPmParameter());
		checkList.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		IStatusSO statussO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

		StatusReqDTO statusreq = new StatusReqDTO();
		statusreq.setStatusId(1L);
		StatusResDTO statusres = statussO.retrieveStatusByStatusId(statusreq);

		checkList.setPmStatus(statusres.getPmStatus());
		qtCheckLists.add(checkList);
		this.getManagedBean().setScQtChecklists(qtCheckLists);
	}

	public void handleFileAttachment(FileUploadEvent event) throws FileNotFoundException, IOException {
		debugLogger("handleFileAttachment");
		FacesScopes facesScopes = new FacesScopes();
		if (event.getFile() != null) {
			ScQtCheckList scQtCheckList = this.getManagedBean()
					.getScQtChecklist();
			ScQtCheckListAttachment attachment = getManagedBean()
					.getScQtChecklist().getScQtCheckListAttachment();
			if (null == attachment) {
				attachment = new ScQtCheckListAttachment();
				attachment.setScQtCheckList(scQtCheckList);
			}
			attachment.setDocNo(scQtCheckList.getScQt().getQtNo());
			attachment.setUser(facesScopes.getUserSessionBean());
			attachment
					.setChangedDate(new Timestamp(System.currentTimeMillis()));
			uploadAttachment(event.getFile(), attachment);
			scQtCheckList.setScQtCheckListAttachment(attachment);
		}
	}

	public void documentCloneSearchAction() {
		try {
			QtnTenderSearchBean qtnTenderSearchBean = this.getManagedBean()
					.getCloneQtnTenderSearchBean();
			List<ScQt> cloneSearchQt = retriveQtCommittes(qtnTenderSearchBean);

			this.getManagedBean().setDocumentCloneTenderList(cloneSearchQt);
		} catch (Exception e) {

		}
	}

	/**
	 * @param qtnTenderSearchBean
	 * @return
	 */
	private List<ScQt> retriveQtCommittes(
			QtnTenderSearchBean qtnTenderSearchBean) {
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		if (qtnTenderSearchBean != null) {
			SearchBean<ScQt> qtSearchBean = new SearchBean<ScQt>();
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();

			String qtTitle = qtnTenderSearchBean.getQtTitle();
			if (!StringUtils.isBlank(qtTitle)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_TITILE);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtTitle);
				searchFilterBean.add(bean);
			}

			String qtNumber = qtnTenderSearchBean.getQtNo();
			if (!StringUtils.isBlank(qtNumber)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_NUMBER);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtNumber);
				searchFilterBean.add(bean);
			}

			String procurementMode = qtnTenderSearchBean.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setAndOr(SearchCondition.CONDITION_OR.value());
				bean.setSearchField(QTConstants.PROCUREMENT_MODE);
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(procurementMode);
				searchFilterBean.add(bean);
			}

			Date fromCreatedDate = qtnTenderSearchBean.getFromCreatedDate();
			Date toCreatedDate = qtnTenderSearchBean.getToCreatedDate();
			if (null != fromCreatedDate || toCreatedDate != null) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());

				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromCreatedDate, toCreatedDate);

				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}

			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
			reqDTO.setQtSearchBean(qtSearchBean);

		}
		QuationTenderResDTO dto = null;
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			dto = detailSO.getQuationTenders(reqDTO);
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));

		} finally {
			detailSO = null;
		}

		return null != dto.getQtResultBean().getResultList() ? dto
				.getQtResultBean().getResultList() : new ArrayList<ScQt>();
	}

	public void onRowCloneAction(SelectEvent event) {
		//ScQt scQt = (ScQt) event.getObject();
		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
		SpecificationResDTO specResDTO = null;
		try {
			IQTSpecSO iqtSpecSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSpecSO.class);
			//specReqDTO.setQtId(scQt.getQtId());
			//specReqDTO.setSpecType(null);
			specReqDTO.setCheckListTypeCode(ParameterConst.DOCUMENT_CATEGORY_QUOTATION_TENDER_DOCUMENTS);
			specReqDTO.setProcurementCatCode(this.getManagedBean().getQt().getPmProcurementCategory().getParameterCode());
			specResDTO = iqtSpecSO.retrieveCheckLists(specReqDTO);
			if (specResDTO.getSpecResultBean() == null) {
				List<ScQtCheckList> checkListList = new ArrayList<ScQtCheckList>();
				for (PmDocument pmDoc : specResDTO.getDocumentResultBean()) {
					ScQtCheckList checkList = new ScQtCheckList();
					checkList.setTitle(pmDoc.getDocName());
					checkList.setDefinationType(pmDoc
							.getDocDefinitionTypeCode());
					checkList.setManditory(true);
					checkList.setAction(pmDoc.getChecklistActionCode());
					checkListList.add(checkList);
				}
				this.getManagedBean().setQtCheckLists(checkListList);
			} else {
				getManagedBean().setQtCheckLists(
						specResDTO.getSpecResultBean().getResultList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addDocumentCloneAction() {
		debugLogger("addDocumentCloneAction");

		if (getManagedBean().getScQtChecklists() == null) {
			getManagedBean().setScQtChecklists(
					getManagedBean().getSelectedQtCheckLists());
		} else {
			getManagedBean().getScQtChecklists().addAll(
					getManagedBean().getSelectedQtCheckLists());
		}
	}

	/*public void standardListAction() {

		List<ScQtCheckList> checkLists = getManagedBean().getScQtChecklists();

		if (null == checkLists) {
			checkLists = new ArrayList<ScQtCheckList>();
		}

		for (PmReportSection pmReportSection : getManagedBean()
				.getSelectedPmReportSectionList()) {
			ScQtCheckList qtCheckList = new ScQtCheckList();
			qtCheckList.setTitle(pmReportSection.getPmCheckList()
					.getCheckListName());
			qtCheckList.setCheckStatus(true);
			checkLists.add(qtCheckList);
		}
		getManagedBean().setScQtChecklists(checkLists);
	}*/

	public void deleteUploadFile() {
		debugLogger("deleteUploadFile");
		getManagedBean().getChecklistAttachment().clear();
	}

	public void deleteDocument() {
		debugLogger("deleteDocument");
		List<ScQtCheckList> checkLists = getManagedBean().getScQtChecklists();
		List<ScQtCheckList> selectedCheckLists = getManagedBean()
				.getSelectedCheckLists();
		if (checkLists.size() == selectedCheckLists.size()) {
			checkLists.clear();
		} else {
			for (ScQtCheckList scQtCheckList : selectedCheckLists) {
				checkLists.remove(scQtCheckList);
			}
		}

	}

	public void documentDownLoadZipFile(List<ScQtCheckList> checkLists)
			throws IOException {
		debugLogger("documentDownLoadZipFile");

		checkLists = getManagedBean().getScQtChecklists();
		List<File> files = new ArrayList<File>();
		ScQtCheckListAttachment attachment = new ScQtCheckListAttachment();
		for (ScQtCheckList scQtCheckList : checkLists) {
			attachment = scQtCheckList.getScQtCheckListAttachment();
			if (null != attachment) {
				files.add(new File(attachment.getFilePath()));
			}
		}

		PortletResponse portalresponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil
				.getHttpServletResponse(portalresponse);
		response.setContentType("Content-type: text/zip");
		response.setHeader("Content-Disposition",
				"attachment; filename=Ngep.zip");

		// ServletOutputStream out = response.getOutputStream();
		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(
				response.getOutputStream()));
		for (File file : files) {
			zos.putNextEntry(new ZipEntry(file.getName()));

			// Get the file
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
			} catch (FileNotFoundException fnfe) {
				zos.write(("ERROR: Could not find file " + file.getName())
						.getBytes());
				zos.closeEntry();
				continue;
			}
			BufferedInputStream fif = new BufferedInputStream(fis);
			// Write the contents of the file
			int data = 0;
			while ((data = fif.read()) != -1) {
				zos.write(data);
			}
			fif.close();
			zos.closeEntry();
		}
		zos.close();
		response.flushBuffer();
		FacesContext.getCurrentInstance().responseComplete();
	}

	public void downloadDocumentFile(ScQtCheckList scQtCheckList)
			throws IOException {
		debugLogger("downloadDocumentFile");
		
		File file = new File(scQtCheckList.getScQtCheckListAttachment()
				.getFilePath());
		InputStream input = new FileInputStream(file);

		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);
		res.setHeader("Content-Disposition", "attachment; filename=\""
				+ scQtCheckList.getScQtCheckListAttachment().getFileName()
				+ "\"");
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();
		if (scQtCheckList.getScQtCheckListAttachment().getFilePath() != null) {
			try {
				URLConnection uc = new URL(scQtCheckList
						.getScQtCheckListAttachment().getFilePath())
						.openConnection();
				input = new BufferedInputStream(uc.getInputStream());
			} catch (Exception e) {
				// Cannot open connection, maybe local file
				input = new FileInputStream(scQtCheckList
						.getScQtCheckListAttachment().getFilePath());
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

	/**
	 * determineIcon for displaying image icon in pid
	 */
	public String determineIcon(ScQtCheckListAttachment attachment) {
		String imageFileName = "icon_file_default.png";
		String ext = "";

		if (attachment != null) {
			String fileName = attachment.getFileName();

			if (fileName != null) {
				int mid = fileName.lastIndexOf(".");
				ext = fileName.substring(mid + 1, fileName.length());
			}

			if ("doc".equals(ext) || "docx".equals(ext)) {
				imageFileName = "icon_doc.png";
			} else if ("pdf".equals(ext)) {
				imageFileName = "icon_pdf.png";
			} else if ("xls".equals(ext) || "xlsx".equals(ext)
					|| "csv".equals(ext)) {
				imageFileName = "icon_exel.png";
			} else if ("jpg".equals(ext) || "bmp".equals(ext)
					|| "png".equals(ext) || "gif".equals(ext)) {
				imageFileName = "icon_image.png";
			} else if ("txt".equals(ext)) {
				imageFileName = "icon_txt.png";
			}
		}
		return imageFileName;
	}

	public void resetClone() {
		debugLogger("resetClone");
		this.getManagedBean().setTitle(null);
		this.getManagedBean().setQtNo(null);
		this.getManagedBean().setProcurementMode(null);
		this.getManagedBean().setCreatedDtFrom(null);
		this.getManagedBean().setCreatedDtTo(null);
		this.getManagedBean().setStatus(null);
	}

	public String showTemplateAction(ScQtCheckList scQtCheckList) {
		debugLogger("showTemplateAction");
		try {
			getManagedBean().setQtCheckList(scQtCheckList);
			log.info("Title Name : " + getManagedBean().getQtCheckList().getTitle());
			TemplateDocBackingBean docBB = new TemplateDocBackingBean();
			docBB.createNewTemplate();

			if (this.getManagedBean().getQtDoc() == null) {
				this.getManagedBean().setQtDoc(new ScQtDoc());
			}

			//temporarily Comment by fara
			/*this.getManagedBean()
					.getQtDoc()
					.setTplDocMaster(
							docBB.getManagedBean().getSelectedTemplate());*/

			if (this.getManagedBean().getQtDoc().getScQtCheckList() == null) {
				this.getManagedBean().getQtDoc()
						.setScQtCheckList(new ScQtCheckList());
			}
			this.getManagedBean().getQtDoc()
					.setScQt(this.getManagedBean().getQt());
			//temporarily Comment by fara
			//this.getManagedBean().getQtDoc().getTplDocMaster().setPmStatus(1L);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return NAV_TO_TEMPLATE_DOC_MAINTENANACE + "?faces-redirect=true";
	}

	public String documentBackAction() {
		debugLogger("documentBackAction");
		return "/xhtml/sc/quatationtender/quatationtendercreation.xhtml?faces-redirect=true";
	}
	
	public void saveAction() {
		debugLogger("saveAction");
		IQTTemplateSO qtTemplateSO = null;
		try {
			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();
			//List<ScQtDoc> qtDocList = this.getManagedBean().getScQtDocs();
			ScQtDoc qtDoc = this.getManagedBean().getQtDoc();
			ScQtCheckList checklist = this.getManagedBean().getQtDoc()
					.getScQtCheckList();
			checklist.setTitle("ssqqqq");
			qtDoc.setScQtCheckList(checklist);
			
			//temporarily Comment by fara
			/*qtDoc.getTplDocMaster().setCategoryId(1l);
			PmParameter processType = new PmParameter();
			processType.setParameterId(1145l);
			qtDoc.getTplDocMaster().setProcessTypeCode(processType);
			PmParameter templateType = new PmParameter();
			templateType.setParameterId(124l);
			qtDoc.getTplDocMaster().setTemplateTypeCode(templateType);
			qtDoc.getTplDocMaster().setDocName("sssss");
			qtDoc.getTplDocMaster().setTemplateStatus('F');
			qtDoc.getTplDocMaster().setPmStatus(1l);*/
			List<ScQtDoc> docs = new ArrayList<ScQtDoc>();
			docs.add(qtDoc);
			qtTemplateReqDTO.setScQtDoc(docs);
			qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);

			QTTemplateDocResDTO resDTO = qtTemplateSO
					.saveOrUpdateTemplateDoc(qtTemplateReqDTO);
			if (resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", ""

						));
			} else {
				throw new EpException("Failed.");
			}
		} finally {
			qtTemplateSO = null;
		}
		
	}
}
