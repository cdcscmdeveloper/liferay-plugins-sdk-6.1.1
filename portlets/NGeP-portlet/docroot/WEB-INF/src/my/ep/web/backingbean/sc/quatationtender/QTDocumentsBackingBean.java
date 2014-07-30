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
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.biz.service.tpl.doc.ITemplateDocSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocReqDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.specification.SpecificationReqDTO;
import my.ep.dto.sc.specification.SpecificationResDTO;
import my.ep.dto.tpl.doc.TemplateDocReqDTO;
import my.ep.dto.tpl.doc.TemplateDocResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReportAttachment;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.persistence.entity.tpl.doc.TplDocSection;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.tpl.doc.TemplateDocBackingBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.beanutils.BeanUtils;
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

	//private static final String NAV_TO_TEMPLATE_DOC_MAINTENANACE = "/xhtml/sc/quatationtender/templateDocument/templateDocMaintenance.xhtml";

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
		ScQt scQt = this.getManagedBean().getQt();
		List<ScQtDocReport> scQtDocReports = this.getManagedBean().getScQtDocReportList();
		if (scQtDocReports == null) {
			scQtDocReports = new ArrayList<ScQtDocReport>();
			scQt.setScQtDocReports(scQtDocReports);
		}
		ScQtDocReport scQtDocReport = new ScQtDocReport();
		scQtDocReport.setScQt(scQt);
		scQtDocReport.setScQtDocReportAttachment(new ScQtDocReportAttachment());
		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
			reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
			ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			scQtDocReport.setDefinationType(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}
		scQtDocReports.add(scQtDocReport);
		
		/*List<ScQtCheckList> qtCheckLists = getManagedBean().getScQtChecklists();
		if (null == qtCheckLists) {
			qtCheckLists = new ArrayList<ScQtCheckList>();
		}
		for (ScQtCheckList existedCheckList : qtCheckLists) {
			existedCheckList.setBrandNew(false);
		}
		ScQtCheckList checkList = new ScQtCheckList();
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
		reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
		ParameterResDTO resDTO = getParameterSO().retrieveParameterByTypeAndCode(reqDTO);
		checkList.setDefinationType(resDTO.getPmParameter());
		checkList.setDocCategory(null);
		checkList.setScoringReq(true);
		checkList.setChecklistSeq(1);
		checkList.setChangedBy(1L);
		checkList.setCreatedBy(1L);
		checkList.setScQt(getManagedBean().getQt());

		ParameterReqDTO reqDTO1 = new ParameterReqDTO();
		reqDTO1.setParameterId(1l);
		ParameterResDTO resDTO1 = getParameterSO().retrieveParameterById(reqDTO1);
		checkList.setAction(resDTO1.getPmParameter());
		checkList.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		IStatusSO statussO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		StatusReqDTO statusreq = new StatusReqDTO();
		statusreq.setStatusId(1L);
		StatusResDTO statusres = statussO.retrieveStatusByStatusId(statusreq);

		checkList.setPmStatus(statusres.getPmStatus());
		qtCheckLists.add(checkList);
		this.getManagedBean().setScQtChecklists(qtCheckLists);*/
	}

	public void handleFileAttachment(FileUploadEvent event) throws FileNotFoundException, IOException {
		debugLogger("handleFileAttachment");
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScQtDocReport selectedScQtDocReport = getManagedBean().getSelectedScQtDocReport();
			ScQtDocReportAttachment scQtDocReportAttachment = selectedScQtDocReport.getScQtDocReportAttachment();
			if (null == scQtDocReportAttachment) {
				scQtDocReportAttachment = new ScQtDocReportAttachment();
				scQtDocReportAttachment.setScQtDocReport(selectedScQtDocReport);
				selectedScQtDocReport.setScQtDocReportAttachment(scQtDocReportAttachment);
			}
			scQtDocReportAttachment.setDocNo(selectedScQtDocReport.getScQt().getQtNo());
			scAttachment = scQtDocReportAttachment; 
			uploadAttachment(event.getFile(), scAttachment);
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
		List<ScQtDocReport> scQtDocReports = getManagedBean().getScQtDocReportList();
		List<ScQtDocReport> selectedScQtDocReports = getManagedBean().getSelectedScQtDocReports();
		if (scQtDocReports.size() == selectedScQtDocReports.size()) {
			scQtDocReports.clear();
		} else {
			for (ScQtDocReport scQtDocReport : selectedScQtDocReports) {
				scQtDocReports.remove(scQtDocReport);
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

	public String showTemplateAction(ScQtDocReport scQtDocReport) {
		debugLogger("showTemplateAction");
		IQTTemplateSO qtTemplateSO = null;
		ITemplateDocSO templateSO = null;
		String templateDocUrl = null;
		try {
			getManagedBean().setScQtDocReport(scQtDocReport);
			TemplateDocBackingBean tplDocBB = new TemplateDocBackingBean();
			
			templateSO = (ITemplateDocSO) BusinessDelegate.getInstance().getServiceObject("cc", ITemplateDocSO.class);
			TemplateDocReqDTO tmplateReqDTO = new TemplateDocReqDTO();
			tmplateReqDTO.setDocMasterId(70l);
			TemplateDocResDTO templateResDTO = templateSO.getTemplateMaster(tmplateReqDTO);
			System.out.println("------------" + templateResDTO.getTplDocMaster());
			
			if(scQtDocReport.getTplDocMaster() == null) {
				// clone TplDocMaster Details
				TplDocMaster tplDocMaster = (TplDocMaster) BeanUtils.cloneBean(templateResDTO.getTplDocMaster());
				tplDocMaster.setDocMasterId(null);
				setUserDetails(tplDocMaster);
				
				List<TplDocSection> tplDocSectionList = new ArrayList<TplDocSection>();
				List<TplDocSection> docSectionList = new ArrayList<TplDocSection>();
				tplDocBB.getDocSectionList(1, tplDocBB.getManagedBean().getDocSectionTN(), docSectionList);
				for (int i = 0; i < docSectionList.size(); i++) {
					TplDocSection sectionOrder = docSectionList.get(i);
					sectionOrder.setSectionOrder(i);
					// clone TplDocSection Details
					TplDocSection tplDocSection1 = (TplDocSection) BeanUtils.cloneBean(sectionOrder);
					tplDocSection1.setDocSectionId(null);
					tplDocSection1.setTplDocMaster(tplDocMaster);
					setUserDetails(tplDocSection1);
					tplDocSectionList.add(tplDocSection1);
				}
				tplDocMaster.setTplDocSectionCollection(tplDocSectionList);
				tplDocMaster.setDocTitle(scQtDocReport.getTitle());
				//tplDocMaster.setCategoryId(1l);
				//tplDocMaster.setOrgProfileId(null);
				// Process Type
				ParameterReqDTO reqDTO = new ParameterReqDTO();
				reqDTO.setParameterType(ParameterTypeConst.TPL_DOC_PROCESS_TYPE);
				reqDTO.setParameterCode(ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING);
				ParameterResDTO resDTO = getParameterSO().retrieveParameterByTypeAndCode(reqDTO);
				//tplDocMaster.setProcessTypeCode(resDTO.getPmParameter());
				// Template Type
				ParameterReqDTO reqDTO1 = new ParameterReqDTO();
				reqDTO1.setParameterType(ParameterTypeConst.TEMPLATE_TYPE);
				reqDTO1.setParameterCode(ParameterConst.TEMPLATE_TYPE_MINISTRY);
				ParameterResDTO resDTO1 = getParameterSO().retrieveParameterByTypeAndCode(reqDTO1);
				//tplDocMaster.setTemplateTypeCode(resDTO1.getPmParameter());
				tplDocMaster.setDocName(scQtDocReport.getTitle());
				//tplDocMaster.setPmStatus(1l);
				
				// Saving
				qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);
				QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();
				//qtTemplateReqDTO.setScQtDoc(docs);
				QTTemplateDocResDTO qtTemplateResDTO = qtTemplateSO.saveOrUpdateTemplateDoc(qtTemplateReqDTO);
				
				// Setting the data from managedbean tpldocmaster
				//getManagedBean().setTplDocMaster(tplDocMaster);
				
				templateDocUrl = tplDocBB.goToMaint(tplDocMaster);
				//tplDocBB.createNewTemplate();
				
				//return NAV_TO_TEMPLATE_DOC_MAINTENANACE + "?faces-redirect=true";
			} else {
				templateDocUrl = tplDocBB.goToMaint(getManagedBean().getTplDocMaster()); // Need to Change
			}
			
			/*if (this.getManagedBean().getQtDoc() == null) {
				this.getManagedBean().setQtDoc(new ScQtDoc());
			}

			if (this.getManagedBean().getQtDoc().getScQtCheckList() == null) {
				this.getManagedBean().getQtDoc().setScQtCheckList(new ScQtCheckList());
			}
			this.getManagedBean().getQtDoc().setScQt(this.getManagedBean().getQt());
			this.getManagedBean().getQtDoc().getTplDocMaster().setPmStatus(1L);*/


		} catch (Exception e) {
			e.printStackTrace();
		}
		//return NAV_TO_TEMPLATE_DOC_MAINTENANACE + "?faces-redirect=true";
		return templateDocUrl;
	}

	public String documentBackAction() {
		debugLogger("documentBackAction");
		return "/xhtml/sc/quatationtender/quatationtendercreation.xhtml?faces-redirect=true";
	}
	
/*	public void saveAction() {
		debugLogger("saveAction");
		IQTTemplateSO qtTemplateSO = null;
		try {
			List<ScQtDoc> docs = new ArrayList<ScQtDoc>();
			
			ScQtDoc qtDoc = this.getManagedBean().getQtDoc();
			
			//TplDocMaster master = this.getManagedBean().getQtDoc().getTplDocMaster();
			//ScQtCheckList checkList = this.getManagedBean().getQtCheckList();
			
			TemplateDocBackingBean docBB = new TemplateDocBackingBean();
			List<TplDocSection> tplDocSectionList = new ArrayList<TplDocSection>();
			List<TplDocSection> docSectionList = new ArrayList<TplDocSection>();
			docBB.getDocSectionList(1, docBB.getManagedBean().getDocSectionTN(), docSectionList);
			for (int i = 0; i < docSectionList.size(); i++) {
				TplDocSection sectionOrder = docSectionList.get(i);
				sectionOrder.setSectionOrder(i);
				
				TplDocSection TplDocSection1 = (TplDocSection) BeanUtils
						.cloneBean(sectionOrder);
				TplDocSection1.setDocSectionId(null);
				tplDocSectionList.add(TplDocSection1);
			}
			master.setTplDocSectionCollection(tplDocSectionList);
			master.setDocTitle(checkList.getTitle());
			master.setCategoryId(1l);
			master.setOrgProfileId(null);
			// Process Type
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.TPL_DOC_PROCESS_TYPE);
			reqDTO.setParameterCode(ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING);
			ParameterResDTO resDTO = getParameterSO().retrieveParameterByTypeAndCode(reqDTO);
			master.setProcessTypeCode(resDTO.getPmParameter());
			// Template Type
			ParameterReqDTO reqDTO1 = new ParameterReqDTO();
			reqDTO1.setParameterType(ParameterTypeConst.TEMPLATE_TYPE);
			reqDTO1.setParameterCode(ParameterConst.TEMPLATE_TYPE_MINISTRY);
			ParameterResDTO resDTO1 = getParameterSO().retrieveParameterByTypeAndCode(reqDTO1);
			master.setTemplateTypeCode(resDTO1.getPmParameter());
			master.setDocName("TemplateDocument");
			master.setPmStatus(1l);
			
			qtDoc.setScQt(this.getManagedBean().getQt());
			qtDoc.setTplDocMaster(getManagedBean().getTplDocMaster());
			qtDoc.setTplSpecMaster(null);
			this.getManagedBean().getQtCheckList().setScQtDoc(qtDoc);
			qtDoc.setScQtCheckList(this.getManagedBean().getQtCheckList());
			docs.add(qtDoc);
			qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);
			QTTemplateDocReqDTO templateReqDTO = new QTTemplateDocReqDTO();
			templateReqDTO.setScQtDoc(docs);
			QTTemplateDocResDTO templateResDTO = qtTemplateSO.saveOrUpdateTemplateDoc(templateReqDTO);
			if (templateResDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.", "" ));
			}
		} finally {
			qtTemplateSO = null;
		}
	}*/
	
	public void downloadAllReports() throws IOException {
		debugLogger("downloadAllReports");
		List<ScQtDocReport> scQtDocReports = getManagedBean().getScQtDocReportList();
		List<ScQtDocReportAttachment> scAttachments = new ArrayList<ScQtDocReportAttachment>();
		for (ScQtDocReport scQtDocReport : scQtDocReports) {
			ScQtDocReportAttachment attachment = scQtDocReport.getScQtDocReportAttachment();
			if(attachment == null) {
				attachment = generateDocumentAttachment(scQtDocReport);
			}
		}
		downloadAllFiles("NGeP-QT-Report", scAttachments);
	}
	
	/**
	 * @param scQtReport
	 * @return
	 */
	private ScQtDocReportAttachment generateDocumentAttachment(ScQtDocReport scQtDocReport) {
		String definitionType = scQtDocReport.getDefinationType().getParameterCode();
		ScQtDocReportAttachment scQtDocReportAttachment = null;
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED.equals(definitionType)) {
			try {
				String docDefinitionKey = scQtDocReport.getPmDocument().getDocDefinitionKey();
				scQtDocReportAttachment = new ScQtDocReportAttachment();
				scQtDocReportAttachment.setScQtDocReport(scQtDocReport);
				if (SourcingConstants.DOC_DEF_KEY_CONTRACT_SUMMARY.equals(docDefinitionKey)) {
					/*generateContractSummaryPdfFromVelocity((ScAttachment)paperAttachment, 
							getManagedBean().getQt());*/
				} else if (SourcingConstants.DOC_DEF_KEY_AGREEMENT_LETTER.equals(docDefinitionKey)) {
					/*generateAgreementLetterPdfFromVelocity((ScAttachment)paperAttachment, 
							getManagedBean().getQt());*/
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			}
			scQtDocReport.setScQtDocReportAttachment(scQtDocReportAttachment);
		}
		return scQtDocReportAttachment;
	}

	public void downloadReport(ScQtDocReport scQtDocReport) throws IOException {
		debugLogger("downloadReport");
		ScQtDocReportAttachment scQtDocReportAttachment = scQtDocReport.getScQtDocReportAttachment();
		if (scQtDocReportAttachment == null) {
			scQtDocReportAttachment = generateDocumentAttachment(scQtDocReport);
		}
		downloadFile(scQtDocReportAttachment);
	}
	
	private IParameterSO getParameterSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}
}
