package my.ep.web.backingbean.sc.quatationtender;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
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
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.biz.service.tpl.doc.ITemplateDocSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocReqDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.tpl.doc.TemplateDocReqDTO;
import my.ep.dto.tpl.doc.TemplateDocResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
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
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
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
		
		// PagingDataModel for Clone Quotation Tender List
		IPagingDataModel<ScQt> cloneSearchDocumentDataModel = getManagedBean().getCloneDocumentDataModel();
		
		if (cloneSearchDocumentDataModel == null) {
			getManagedBean().setCloneDocumentDataModel(new DocumentPagingDataModel("clonesearchboid", getManagedBean().getCloneDocumentSearchBean()));
		}

	}
	
	private class DocumentPagingDataModel extends PagingDataModel<ScQt> {
		private static final long serialVersionUID = 1L;

		private QtnTenderSearchBean cloneDocumentSearchBean;

		public DocumentPagingDataModel(String componentId,
				QtnTenderSearchBean cloneDocumentSearchBean) {
			super(componentId);
			this.cloneDocumentSearchBean = cloneDocumentSearchBean;
		}
		
		@Override
		public ISearchResultBean<ScQt> pageChangeListener(
				ISearchBean<ScQt> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				populateCloneQTSearchCriteria(cloneDocumentSearchBean, searchBean);
				reqDTO.setQtSearchBean(searchBean);
				QuationTenderResDTO resDTO = detailSO.getQuationTenders(reqDTO);
				return resDTO.getQtResultBean();
			} finally {
				detailSO = null;
			}
		}
	}

	/**
	 * @param cloneBusinessOwnerSearchBean
	 * @param searchBean
	 */
	private void populateCloneQTSearchCriteria(
			QtnTenderSearchBean cloneDocumentSearchBean,
			ISearchBean<ScQt> qtSearchBean) {
		if (cloneDocumentSearchBean != null) {
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			
			String qtTitle = cloneDocumentSearchBean.getQtTitle();
			if (!StringUtils.isBlank(qtTitle)) {
				qtSearchBean.getDataFilterBeans().add(new SearchFilterBean(QTConstants.QT_TITILE, SearchOperator.OPERATOR_LIKE.value(), qtTitle, true));
			}

			String qtNumber = cloneDocumentSearchBean.getQtNo();
			if (!StringUtils.isBlank(qtNumber)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_NUMBER);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtNumber);
				searchFilterBean.add(bean);
			}
			
			String procurementMode = cloneDocumentSearchBean.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				qtSearchBean.getDataFilterBeans().add(new SearchFilterBean("procurementMode.parameterCode", SearchOperator.OPERATOR_EQUALS.value(), procurementMode));
			}

			Date fromCreatedDate = cloneDocumentSearchBean.getFromCreatedDate();
			Date toCreatedDate = cloneDocumentSearchBean.getToCreatedDate();
			if (null != fromCreatedDate || toCreatedDate != null) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(fromCreatedDate, toCreatedDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			
			String status = cloneDocumentSearchBean.getQtStatus();
			if (!StringUtils.isBlank(status)) {				
				qtSearchBean.getDataFilterBeans().add(new SearchFilterBean("scQtStatus.pmStatus.statusId", SearchOperator.OPERATOR_EQUALS.value(), status));				
				qtSearchBean.getDataFilterBeans().add(new SearchFilterBean("scQtStatus.current", SearchOperator.OPERATOR_EQUALS.value(), true));
			}
			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}

	public void cloneSearchDocumentAction() {
		getManagedBean().getCloneDocumentDataModel().reloadData();
	}
	
	public void cloneDocumentPopupAction() {
		debugLogger("cloneDocumentPopupAction");
		getManagedBean().getCloneDocumentSearchBean().setQtTitle(null);
		getManagedBean().getCloneDocumentSearchBean().setQtNo(null);
		getManagedBean().getCloneDocumentSearchBean().setProcurementMode(null);
		getManagedBean().getCloneDocumentSearchBean().setFromCreatedDate(null);
		getManagedBean().getCloneDocumentSearchBean().setToCreatedDate(null);
		getManagedBean().getCloneDocumentSearchBean().setQtStatus(null);
		getManagedBean().setCloneCommitteMemebers(null);
		getManagedBean().setCloneBusinessTenderList(null);
	}

	public SelectItem[] getProcurementModeDropdownValues(){
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO);
		recommendList.add(ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE);
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		SelectItem[] items = getDropdownCodeValuesBySearchBean(ParameterTypeConst.SC_PROCUREMENT_MODE, searchBean);
		return items;
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
		scQtDocReport.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_QUOTATION_TENDER_DOCUMENTS);
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
			} else {
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
		ScQt scQt = (ScQt) event.getObject();
		IQTDetailSO detailSO;
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		QuationTenderResDTO resDTO = null;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			reqDTO.setQtId(scQt.getQtId());
			resDTO = detailSO.retriveQTDocDetails(reqDTO);
			List<ScQtDocReport> scQtDocReports = resDTO.getReports();
			this.getManagedBean().setCloneScQtDocReports(scQtDocReports);
		} finally {
			detailSO = null;
		}
	}

	public void addDocumentCloneAction() {
		debugLogger("addDocumentCloneAction");
		List<ScQtDocReport> reportList = getManagedBean().getScQtDocReportList();
		if (null == reportList) {
			reportList = new ArrayList<ScQtDocReport>();
		}
		List<ScQtDocReport> scQtDocReports = this.getManagedBean().getSelectedCloneScQtDocReports();
		for (ScQtDocReport docReport : scQtDocReports) {
			ScQtDocReport qtDocReport = null;
			try {
				qtDocReport = (ScQtDocReport) BeanUtils.cloneBean(docReport);
			} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
			if (null != qtDocReport) {
				qtDocReport.setQtReportId(null);
				qtDocReport.setDocId(null);
				setUserDetails(qtDocReport);
				qtDocReport.setChangedDate(null);
				reportList.add(qtDocReport);
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

		PortletResponse portalresponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil.getHttpServletResponse(portalresponse);
		response.setContentType("Content-type: text/zip");
		response.setHeader("Content-Disposition", "attachment; filename=Ngep.zip");

		// ServletOutputStream out = response.getOutputStream();
		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
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
			tmplateReqDTO.setDocMasterId(6l);
			TemplateDocResDTO templateResDTO = templateSO.getTemplateMaster(tmplateReqDTO);
			log.info("------------" + templateResDTO.getTplDocMaster());
			TplDocMaster docMasters = templateResDTO.getTplDocMaster();
			if(scQtDocReport.getTplDocMaster() == null) {
				
				// clone TplDocMaster Details
				TplDocMaster tplDocMaster = (TplDocMaster) BeanUtils.cloneBean(docMasters);
				tplDocMaster.setDocMasterId(null);
				tplDocMaster.setChangedDate(null);
				tplDocMaster.setTplDocSections(new ArrayList<TplDocSection>());
				setUserDetails(tplDocMaster);

				//clone tplDocSection Details
				List<TplDocSection> docSections = docMasters.getTplDocSections();
				for (TplDocSection tplDocSection : docSections) {
					TplDocSection section = (TplDocSection) BeanUtils.cloneBean(tplDocSection);
					section.setDocSectionId(null);
					section.setChangedDate(null);
					section.setTplDocMaster(tplDocMaster);
					setUserDetails(section);
					tplDocMaster.getTplDocSections().add(section);
				}
				
				tplDocMaster.setTplDocSectionCollection(null);
				
				/*List<TplDocSection> tplDocSectionList = new ArrayList<TplDocSection>();
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
				tplDocMaster.setTplDocSectionCollection(tplDocSectionList);*/
				
				tplDocMaster.setDocTitle(scQtDocReport.getTitle());
				// Procurement Category
				ParameterReqDTO reqDTO2 = new ParameterReqDTO();
				reqDTO2.setParameterType(ParameterTypeConst.PROCUREMENT_CATEGORY);
				reqDTO2.setParameterCode(ParameterConst.PROCUREMENT_CATEGORY_OTHERS);
				ParameterResDTO resDTO2 = getParameterSO().retrieveParameterByTypeAndCode(reqDTO2);
				tplDocMaster.setProcurementCategory(resDTO2.getPmParameter());
				tplDocMaster.setOrgProfile(null);
				// Process Type
				ParameterReqDTO reqDTO = new ParameterReqDTO();
				reqDTO.setParameterType(ParameterTypeConst.TPL_DOC_PROCESS_TYPE);
				reqDTO.setParameterCode(ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING);
				ParameterResDTO resDTO = getParameterSO().retrieveParameterByTypeAndCode(reqDTO);
				tplDocMaster.setProcessType(resDTO.getPmParameter());
				// Template Type
				ParameterReqDTO reqDTO1 = new ParameterReqDTO();
				reqDTO1.setParameterType(ParameterTypeConst.TEMPLATE_TYPE);
				reqDTO1.setParameterCode(ParameterConst.TEMPLATE_TYPE_MINISTRY);
				ParameterResDTO resDTO1 = getParameterSO().retrieveParameterByTypeAndCode(reqDTO1);
				tplDocMaster.setTemplateType(resDTO1.getPmParameter());
				tplDocMaster.setDocName(scQtDocReport.getTitle());
				
				PmStatus status = new PmStatus();
				status.setStatusId(QTStatusConst.QT_PENDING_APPOINT_BO);
				tplDocMaster.setPmStatus(status);
				//tplDocMaster.setPmStatus(1);
				
				List<ScQtDocReport> reports = new ArrayList<ScQtDocReport>();
				ScQtDocReport qtDocReport = getManagedBean().getScQtDocReport();
				qtDocReport.setTplDocMaster(tplDocMaster);
				reports.add(qtDocReport);
				
				// Saving
				qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);
				QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();
				qtTemplateReqDTO.setReports(reports);
				QTTemplateDocResDTO qtTemplateResDTO = qtTemplateSO.saveQtReports(qtTemplateReqDTO);
				if (qtTemplateResDTO.isSuccessful()) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.", "" ));
				}
				TemplateDocResDTO templateResDTO1 = templateSO.getTemplateMaster(tmplateReqDTO);
				TplDocMaster docMaster = templateResDTO1.getTplDocMaster();
				
				templateDocUrl = tplDocBB.goToMaint(docMaster);
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
