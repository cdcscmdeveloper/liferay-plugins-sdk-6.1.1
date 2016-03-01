/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.sc.quatationtender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.biz.service.tpl.doc.ITemplateDocSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocReqDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SpecificationReqDTO;
import my.ep.dto.sc.quationtender.SpecificationResDTO;
import my.ep.dto.tpl.doc.TemplateDocReqDTO;
import my.ep.dto.tpl.doc.TemplateDocResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScReport;
import my.ep.persistence.entity.sc.loaloi.ScLoaReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklistAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.persistence.entity.tpl.doc.TplDocSection;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.supplierfinalization.SupplierFinalizationBackingBean;
import my.ep.web.backingbean.tpl.doc.TemplateDocBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentDefaultFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class QTDocumentsBackingBean.
 * 
 * @author SudheerKumar
 */
@ManagedBean(name = "qTDocumentsBackingBean")
@RequestScoped
public class QTDocumentsBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(QTDocumentsBackingBean.class);

	/** The Constant DOCUMENT_PAGE. */
	private static final String DOCUMENT_PAGE = "/xhtml/sc/quatationtender/quatationtendercreation.xhtml?faces-redirect=true";
	private static final Locale myLocale = new Locale(SourcingConstants.MALAY_LANGUAGE_CODE,
				  SourcingConstants.MALAYSIA_COUNTRY_CODE);

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/** The select all. */
	private boolean selectAll;

	private boolean readOnly;
	
	/**
	 * Instantiates a new QT documents backing bean.
	 */
	public QTDocumentsBackingBean() {
		// PagingDataModel for Clone Quotation Tender List
	}

	/**
	 * The Class DocumentPagingDataModel.
	 */
	private class DocumentPagingDataModel extends PagingDataModel<ScQt> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The clone document search bean. */
		private QtnTenderSearchBean cloneDocumentSearchBean;

		/**
		 * Instantiates a new document paging data model.
		 * 
		 * @param componentId
		 *            the component id
		 * @param cloneDocumentSearchBean
		 *            the clone document search bean
		 */
		public DocumentPagingDataModel(String componentId,
				QtnTenderSearchBean cloneDocumentSearchBean) {
			super(componentId, false);
			this.cloneDocumentSearchBean = cloneDocumentSearchBean;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQt> pageChangeListener(
				ISearchBean<ScQt> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				populateCloneQTSearchCriteria(cloneDocumentSearchBean,
						searchBean);
				reqDTO.setQtSearchBean(searchBean);
				QuationTenderResDTO resDTO = detailSO.retriveQuationTenderDetailsBySearchCriteria(reqDTO);
				return resDTO.getQtResultBean();
			} finally {
				detailSO = null;
			}
		}
	}

	/**
	 * Populate clone qt search criteria.
	 * 
	 * @param cloneDocumentSearchBean
	 *            the clone document search bean
	 * @param qtSearchBean
	 *            the qt search bean
	 */
	private void populateCloneQTSearchCriteria(
			QtnTenderSearchBean cloneDocumentSearchBean,
			ISearchBean<ScQt> qtSearchBean) {
		if (cloneDocumentSearchBean != null) {
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();

			String qtTitle = cloneDocumentSearchBean.getQtTitle();
			if (!StringUtils.isBlank(qtTitle)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.QT_TITILE,
								SearchOperator.OPERATOR_LIKE.value(), qtTitle,
								true));
			}

			String qtNumber = cloneDocumentSearchBean.getQtNo();
			if (!StringUtils.isBlank(qtNumber)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_NUMBER);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtNumber);
				searchFilterBean.add(bean);
			}

			String procurementMode = cloneDocumentSearchBean
					.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("procurementMode.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								procurementMode));
			}

			Date fromCreatedDate = cloneDocumentSearchBean.getFromCreatedDate();
			Date toCreatedDate = cloneDocumentSearchBean.getToCreatedDate();
			if (null != fromCreatedDate || toCreatedDate != null) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromCreatedDate, toCreatedDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			Date fromPublishDate = cloneDocumentSearchBean.getFromPublishDate();
			Date toPublishDate = cloneDocumentSearchBean.getToPublishDate();
			if (null != fromPublishDate || null != toPublishDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.PUBLISH_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromPublishDate, toPublishDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			Date fromClosingDate = cloneDocumentSearchBean.getFromClosingDate();
			Date toClosingDate = cloneDocumentSearchBean.getToClosingDate();
			if (null != fromClosingDate || null != toClosingDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CLOSED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromClosingDate, toClosingDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			String status = cloneDocumentSearchBean.getQtStatus();
			if (!StringUtils.isBlank(status)) {
				qtSearchBean
						.getDataFilterBeans()
						.add(new SearchFilterBean(
								"scQtStatus.pmStatus.statusId",
								SearchOperator.OPERATOR_EQUALS.value(), status));
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtStatus.current",
								SearchOperator.OPERATOR_EQUALS.value(), true));
			}
			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}

	/**
	 * Clone search document action.
	 */
	public void cloneSearchDocumentAction() {
		debugLogger("cloneSearchDocumentAction");
		getManagedBean().setCloneDocumentSelectQt(null);
		getManagedBean().getCloneDocumentDataModel().reloadData();
	}

	/**
	 * Clone document popup action.
	 */
	public void cloneDocumentPopupAction() {
		debugLogger("cloneDocumentPopupAction");
		getManagedBean().setCloneDocumentSelectQt(null);
		
		IPagingDataModel<ScQt> cloneSearchDocumentDataModel = getManagedBean()
				.getCloneDocumentDataModel();

		if (cloneSearchDocumentDataModel == null) {
			getManagedBean().setCloneDocumentDataModel(
					new DocumentPagingDataModel("clonesearchdocid",
							getManagedBean().getCloneDocumentSearchBean()));
		}
		getManagedBean().getCloneDocumentDataModel().resetData();
		
		getManagedBean().getCloneDocumentSearchBean().setQtTitle(null);
		getManagedBean().getCloneDocumentSearchBean().setQtNo(null);
		getManagedBean().getCloneDocumentSearchBean().setProcurementMode(null);
		getManagedBean().getCloneDocumentSearchBean().setFromCreatedDate(null);
		getManagedBean().getCloneDocumentSearchBean().setToCreatedDate(null);
		getManagedBean().getCloneDocumentSearchBean().setQtStatus(null);
		getManagedBean().setCloneScQtDocReports(null);
	}

	/**
	 * Gets the procurement mode dropdown values.
	 * 
	 * @return the procurement mode dropdown values
	 */
	public SelectItem[] getProcurementModeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO);
		recommendList.add(ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE);
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.SC_PROCUREMENT_MODE, searchBean);
		return items;
	}

	/**
	 * To add empty row to document data table.
	 */
	public void addDocumentList() {
		debugLogger("addDocumentList");
		ScQt scQt = this.getManagedBean().getQt();
		List<ScQtDocReport> scQtDocReports = this.getManagedBean()
				.getScQtDocReportList();
		if (scQtDocReports == null) {
			scQtDocReports = new ArrayList<ScQtDocReport>();
			scQt.setScQtDocReports(scQtDocReports);
		}
		ScQtDocReport scQtDocReport = new ScQtDocReport();
		scQtDocReport
				.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_QUOTATION_TENDER_DOCUMENTS);
		scQtDocReport.setScQt(scQt);
		// scQtDocReport.setScQtDocReportAttachment(new
		// ScQtDocReportAttachment());
		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
			reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
			ParameterResDTO resDTO = parameterSO
					.retrieveParameterByTypeAndCode(reqDTO);
			scQtDocReport.setDefinationType(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}
		scQtDocReports.add(scQtDocReport);
	}

	/**
	 * To remove documents from data table
	 */
	public void deleteDocumentList() {
		debugLogger("deleteDocumentList");
		List<ScQtDocReport> docReportsList = this.getManagedBean()
				.getScQtDocReportList();
		List<ScQtDocReport> selectedReportsList = this.getManagedBean()
				.getSelectedScQtDocReports();
		List<ScQtDocReport> removeDocumentList = this.getManagedBean()
				.getRemoveDocReports();
		if (null == removeDocumentList) {
			removeDocumentList = new ArrayList<ScQtDocReport>();
		}
		removeDocumentList.addAll(selectedReportsList);
		if (docReportsList.size() == selectedReportsList.size()) {
			docReportsList.clear();
		} else {
			for (ScQtDocReport report : selectedReportsList) {
				report.setDeleted(true);
				docReportsList.remove(report);
			}
		}
		if (null != removeDocumentList && !removeDocumentList.isEmpty()) {
			for (int i = 0; i < removeDocumentList.size(); i++) {
				if (null == removeDocumentList.get(i).getDocId()) {
					removeDocumentList.remove(i);
				}
			}
		}
		this.getManagedBean().setScQtDocReportList(docReportsList);
		this.getManagedBean().setRemoveDocReports(removeDocumentList);
	}

	/**
	 * Add attachment to list.
	 * 
	 * @param event
	 *            File upload event instance
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void handleFileAttachment(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		debugLogger("handleFileAttachment");
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScQtDocReport selectedScQtDocReport = getManagedBean()
					.getSelectedScQtDocReport();
			ScQtDocReportAttachment scQtDocReportAttachment = selectedScQtDocReport
					.getScQtDocReportAttachment();
			if (null == scQtDocReportAttachment) {
				scQtDocReportAttachment = new ScQtDocReportAttachment();
				scQtDocReportAttachment.setScQtDocReport(selectedScQtDocReport);
				selectedScQtDocReport
						.setScQtDocReportAttachment(scQtDocReportAttachment);
			} else {
				scQtDocReportAttachment.setScQtDocReport(selectedScQtDocReport);
				selectedScQtDocReport
						.setScQtDocReportAttachment(scQtDocReportAttachment);
			}
			scQtDocReportAttachment.setDocNo(selectedScQtDocReport.getScQt()
					.getQtNo());
			scAttachment = scQtDocReportAttachment;
			uploadAttachment(event.getFile(), scAttachment);
			selectedScQtDocReport
					.setScQtDocReportAttachment(scQtDocReportAttachment);
		}
	}

	/**
	 * On row scQtDocReports in clone action.
	 * 
	 * @param event
	 *            the event
	 */
	public void onRowCloneAction(SelectEvent event) {
		debugLogger("onRowCloneAction");
		ScQt scQt = (ScQt) event.getObject();
		this.getManagedBean().setCloneDocumentSelectQt(scQt);
		IQTDetailSO detailSO;
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		QuationTenderResDTO resDTO = null;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
			resDTO = detailSO.retriveCloneQTDocDetails(reqDTO);
			List<ScQtDocReport> scQtDocReports = resDTO.getReports();
			this.getManagedBean().setCloneScQtDocReports(scQtDocReports);
		} finally {
			detailSO = null;
		}
	}

	/**
	 * Adds the document clone action.
	 */
	public void addDocumentCloneAction() {
		debugLogger("addDocumentCloneAction");
		List<ScQtDocReport> reportList = getManagedBean()
				.getScQtDocReportList();
		if (null == reportList) {
			reportList = new ArrayList<ScQtDocReport>();
		}
		List<ScQtDocReport> scQtDocReports = this.getManagedBean()
				.getSelectedCloneScQtDocReports();
		if (!this.getManagedBean().getSelectedCloneScQtDocReports().isEmpty()) {
			for (ScQtDocReport docReport : scQtDocReports) {
				ScQtDocReport qtDocReport = null;
				try {
					qtDocReport = (ScQtDocReport) BeanUtils
							.cloneBean(docReport);
				} catch (IllegalAccessException | InstantiationException
						| InvocationTargetException | NoSuchMethodException e) {
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
		} else {
			throw new EpException(MessageConst.ERROR_760141);
		}
		RequestContext.getCurrentInstance().execute("screen2.hide()");
	}

	/**
	 * determineIcon for displaying image icon in pid.
	 * 
	 * @param attachment
	 *            the attachment
	 * @return the string
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

	/**
	 * Reset document search bean.Event handle to clear all the document search
	 * data.
	 */
	public void resetClone() {
		debugLogger("resetClone");
		this.getManagedBean().getCloneDocumentSearchBean().setQtTitle(null);
		this.getManagedBean().getCloneDocumentSearchBean().setQtNo(null);
		this.getManagedBean().getCloneDocumentSearchBean().setProcurementMode(null);
		this.getManagedBean().getCloneDocumentSearchBean().setFromCreatedDate(null);
		this.getManagedBean().getCloneDocumentSearchBean().setToCreatedDate(null);
		this.getManagedBean().getCloneDocumentSearchBean().setQtStatus(null);
		this.getManagedBean().getCloneDocumentSearchBean().setFromPublishDate(null);
		this.getManagedBean().getCloneDocumentSearchBean().setToPublishDate(null);
		this.getManagedBean().getCloneDocumentSearchBean().setFromClosingDate(null);
		this.getManagedBean().getCloneDocumentSearchBean().setToClosingDate(null);
		this.getManagedBean().setCloneDocumentSelectQt(null);
	}

	/**
	 * Show template action.
	 * 
	 * @param scQtDocReport
	 *            the sc qt doc report
	 * @return the string
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 */
	public String showTemplateAction(int index, ScQtDocReport scQtDocReport)
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		debugLogger("showTemplateAction");

		String templateDocUrl = null;
		try {
			TemplateDocBackingBean tplDocBB = new TemplateDocBackingBean();
			tplDocBB.getManagedBean().setExternalPageUrl(DOCUMENT_PAGE);

			ScQtDocReport qtDocReport = retriveRulesAndRegulationReport(scQtDocReport);
			if (qtDocReport != null) {
				scQtDocReport = qtDocReport;
			} else {
				// save list of documents if its not yet save
				scQtDocReport = saveScQtReportList(scQtDocReport);
			}

			this.getManagedBean().setScQtDocReport(scQtDocReport);
			TplDocMaster docMaster = scQtDocReport.getTplDocMaster();
			if (docMaster == null) {
				// clone standard tplDocMaster
				TplDocMaster tplDocMaster = cloneTplDocMasterDtls(scQtDocReport);
				scQtDocReport.setTplDocMaster(tplDocMaster);
				// save clone to db
				docMaster = saveClonedTplDocMaster(scQtDocReport);
			}

			this.getManagedBean().getScQtDocReportList().remove(index);
			this.getManagedBean().getScQtDocReportList()
					.add(index, this.getManagedBean().getScQtDocReport());
			
			templateDocUrl = tplDocBB.goToMaint(docMaster,this.isReadOnly());

		} finally {
		}
		return templateDocUrl;
	}

	// retrieve the scQtDocReport for Syarat-syarat Am/ Arahan/Syarat-syarat
	// Khas
	/**
	 * Retrive rules and regulation report.
	 * 
	 * @param scQtDocReport
	 *            the sc qt doc report
	 * @return ScQtDocReport
	 */
	private ScQtDocReport retriveRulesAndRegulationReport(
			ScQtDocReport scQtDocReport) {
		debugLogger("retriveRulesAndRegulationReport");
		IQTDetailSO qTDetailSO = null;
		try {
			qTDetailSO = (IQTDetailSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setScReport(scQtDocReport);
			reqDTO.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_QUOTATION_TENDER_DOCUMENTS);
			reqDTO.setDocDefKey(SourcingConstants.DOC_DEF_KEY_RULES_REGULATION);

			QuationTenderResDTO resDTO = qTDetailSO
					.retriveScQtReportByDefKey(reqDTO);
			ScQtDocReport qtDocReport = (ScQtDocReport) resDTO.getScReport();
			return qtDocReport;
		} finally {
			qTDetailSO = null;
		}
	}

	/**
	 * Save to scQtDocReport.
	 * 
	 * @param scQtDocReport
	 *            the sc qt doc report
	 * @return scQtDocReport
	 */
	private ScQtDocReport saveScQtReportList(ScQtDocReport scQtDocReport) {
		debugLogger("saveScQtReportList");
		QTDetailBackingBean qTDetailBackingBean = new QTDetailBackingBean();
		if (scQtDocReport.getQtReportId() == null) {
			qTDetailBackingBean.validateQtDetails();
			ScQtDocReport qtDocReport = retriveRulesAndRegulationReport(scQtDocReport);
			return qtDocReport;
		} else {
			return null;
		}
	}

	/**
	 * Save cloned tpl doc master.
	 * 
	 * @param scQtDocReport
	 *            the sc qt doc report
	 * @return scQtDocReport
	 */
	private TplDocMaster saveClonedTplDocMaster(ScQtDocReport scQtDocReport) {
		debugLogger("saveClonedTplDocMaster");
		IQTTemplateSO qtTemplateSO = null;
		try {
			qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);

			QTTemplateDocReqDTO reqDTO = new QTTemplateDocReqDTO();
			reqDTO.setScQtDocReport(scQtDocReport);

			QTTemplateDocResDTO resDTO = qtTemplateSO.saveQtReports(reqDTO);
			this.getManagedBean().setScQtDocReport(resDTO.getScQtDocReport());
			List<ScQtDocReport> docReportList = getManagedBean()
					.getScQtDocReportList();
			if (CollectionUtils.isNotEmpty(docReportList)) {
				for (int i = 0; i < docReportList.size(); i++) {
					docReportList.get(i).getQtReportId();
					if (resDTO.getScQtDocReport().getQtReportId()
							.equals(docReportList.get(i).getQtReportId())) {
						docReportList.remove(i);
						docReportList.add(i, resDTO.getScQtDocReport());
						i--;
						break;
					}
				}
			}
			getManagedBean().setScQtDocReportList(docReportList);
			TplDocMaster tplDocMaster = getManagedBean().getScQtDocReport()
					.getTplDocMaster();
			return tplDocMaster;
		} finally {
			qtTemplateSO = null;
		}
	}

	/**
	 * Clone tpl doc master dtls.
	 * 
	 * @param scQtDocReport
	 *            the sc qt doc report
	 * @return the tpl doc master
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 */
	private TplDocMaster cloneTplDocMasterDtls(ScQtDocReport scQtDocReport)
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		debugLogger("cloneTplDocMasterDtls");

		ScQt scQt = scQtDocReport.getScQt();
		Long tplMasterId = retrieveStandardTplDocMaster(scQt).getDocMasterId();
		TplDocMaster templateDocMaster = retrieveTplDocMasterById(tplMasterId);

		// clone TplDocMaster Details
		TplDocMaster tplDocMaster = (TplDocMaster) templateDocMaster
				.cloneEntity();
		tplDocMaster.setDocMasterId(null);
		tplDocMaster.setOrgProfile(null);
		tplDocMaster.setTplDocSections(null);
		// tplDocMaster.setTplDocSectionCollection(null);
		tplDocMaster.setDocTitle(scQtDocReport.getTitle());
		tplDocMaster.setDocName(scQtDocReport.getTitle());

		setUserDetails(tplDocMaster);

		// clone tplDocSection Details
		/*
		 * tplDocMaster.setTplDocSections(new ArrayList<TplDocSection>());
		 * List<TplDocSection> docSections =
		 * templateDocMaster.getTplDocSections(); for (TplDocSection
		 * tplDocSection : docSections) { TplDocSection section =
		 * (TplDocSection) tplDocSection.cloneEntity();
		 * section.setDocSectionId(null); section.setParent(null); //
		 * Temporarily am setting null, need to confirm once
		 * section.setTplDocMaster(tplDocMaster);
		 * 
		 * setUserDetails(section);
		 * tplDocMaster.getTplDocSections().add(section); }
		 */

		tplDocMaster.setTplDocSectionCollection(new ArrayList<TplDocSection>());
		List<TplDocSection> docSectionCOllection = templateDocMaster
				.getTplDocSectionCollection();
		for (TplDocSection tplDocSection : docSectionCOllection) {
			TplDocSection section = (TplDocSection) tplDocSection.cloneEntity();
			section.setDocSectionId(null);
			section.setParent(null); // Temporarily am setting null, need to
										// confirm once
			section.setTplDocMaster(tplDocMaster);

			setUserDetails(section);
			tplDocMaster.getTplDocSectionCollection().add(section);
		}

		// Procurement Category
		PmParameter procurementCategory = scQtDocReport.getScQt()
				.getPmProcurementCategory();
		tplDocMaster.setProcurementCategory(procurementCategory);

		// Process Type
		PmParameter processType = getPmParameter(
				ParameterTypeConst.TPL_DOC_PROCESS_TYPE,
				ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING);
		tplDocMaster.setProcessType(processType);

		// Template Type
		PmParameter templateType = getPmParameter(
				ParameterTypeConst.TEMPLATE_TYPE,
				ParameterConst.TEMPLATE_TYPE_MINISTRY);
		tplDocMaster.setTemplateType(templateType);

		// Item Type - Service & Product
		PmParameter itemType = null;
		if (procurementCategory.getParameterCode().equals(
				ParameterConst.PROCUREMENT_CATEGORY_BAHAN_MENTAH)
				|| procurementCategory
						.getParameterCode()
						.equals(ParameterConst.PROCUREMENT_CATEGORY_KAWALAN_KESELAMATAN)
				|| procurementCategory.getParameterCode().equals(
						ParameterConst.PROCUREMENT_CATEGORY_PERKHIDMATAN)
				|| procurementCategory.getParameterCode().equals(
						ParameterConst.PROCUREMENT_CATEGORY_MTO)
				|| procurementCategory
						.getParameterCode()
						.equals(ParameterConst.PROCUREMENT_CATEGORY_PERKHIDMATAN_PEMINDAHAN_BARANG)
				|| procurementCategory.getParameterCode().equals(
						ParameterConst.PROCUREMENT_CATEGORY_HIRE_PURCHASE)) {
			itemType = getPmParameter(ParameterTypeConst.SC_ITEM_TYPE,
					ParameterConst.ITEM_TYPE_SERVICE);
		} else if (procurementCategory.getParameterCode().equals(
				ParameterConst.PROCUREMENT_CATEGORY_PERALATAN_MAKMAL)
				|| procurementCategory
						.getParameterCode()
						.equals(ParameterConst.PROCUREMENT_CATEGORY_UBATAN_DAN_BAHAN_KIMIA)
				|| procurementCategory.getParameterCode().equals(
						ParameterConst.PROCUREMENT_CATEGORY_ICT)
				|| procurementCategory.getParameterCode().equals(
						ParameterConst.PROCUREMENT_CATEGORY_OTHERS)
				|| procurementCategory.getParameterCode().equals(
						ParameterConst.PROCUREMENT_CATEGORY_PERALATAN_PEJABAT)) {
			itemType = getPmParameter(ParameterTypeConst.SC_ITEM_TYPE,
					ParameterConst.ITEM_TYPE_PRODUCT);
		}
		tplDocMaster.setItemTypeId(itemType);

		// Status
		PmStatus status = new PmStatus();
		status.setStatusId(QTStatusConst.DOC_TEMPLATE_DEFAULT);
		tplDocMaster.setPmStatus(status);

		tplDocMaster.setScReport(scQtDocReport);

		return tplDocMaster;
	}

	/**
	 * Retrieve standard tpl doc master.
	 * 
	 * @param scQt
	 *            the scqt
	 * @return TplDocMaster
	 */
	public TplDocMaster retrieveStandardTplDocMaster(ScQt scQt) {
		debugLogger("retrieveStandardTplDocMaster");
		ITemplateDocSO templateDocSO = null;
		try {
			templateDocSO = (ITemplateDocSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
							ITemplateDocSO.class);
			TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();

			reqDTO.setCategoryId(scQt.getPmProcurementCategory()
					.getParameterId());
			PmParameter processType = getPmParameter(
					ParameterTypeConst.TPL_DOC_PROCESS_TYPE,
					ParameterConst.TPL_DOC_PROCESS_TYPE_STANDARD);
			reqDTO.setProcessTypeId(processType.getParameterId());
			reqDTO.setTitle(SourcingConstants.DOCTITLE_QT);

			TemplateDocResDTO resDTO = templateDocSO
					.getTemplateMasterList(reqDTO);
			List<TplDocMaster> docMasterList = resDTO.getDocMasterList();

			TplDocMaster docMaster = null;
			if (docMasterList != null && docMasterList.size() > 0) {
				docMaster = docMasterList.get(0);
				return docMaster;
			} else {
				throw new EpException(MessageConst.ERROR_760171);
			}
		} finally {
			templateDocSO = null;
		}
	}

	/**
	 * Retrieve tpl doc master by master id.
	 * 
	 * @param masterId
	 *            the master id
	 * @return TplDocMaster
	 */
	private TplDocMaster retrieveTplDocMasterById(Long masterId) {
		debugLogger("retrieveTplDocMasterById");
		IQTTemplateSO qtTemplateSO = null;
		try {
			qtTemplateSO = (IQTTemplateSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTTemplateSO.class);

			QTTemplateDocReqDTO reqDTO = new QTTemplateDocReqDTO();
			reqDTO.setMasterId(masterId);

			QTTemplateDocResDTO resDTO = qtTemplateSO.getTemplateMaster(reqDTO);
			TplDocMaster docMaster = resDTO.getTplDocMaster();

			return docMaster;
		} finally {
			qtTemplateSO = null;
		}
	}

	/**
	 * Gets the pm parameter.
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @param parameterCode
	 *            the parameter code
	 * @return the pm parameter
	 */
	private PmParameter getPmParameter(String parameterType,
			String parameterCode) {
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterType(parameterType);
		reqDTO.setParameterCode(parameterCode);

		ParameterResDTO resDTO = getParameterSO()
				.retrieveParameterByTypeAndCode(reqDTO);
		return resDTO.getPmParameter();
	}

	/**
	 * Template Document Page to Documents list page.
	 * 
	 * @return the string
	 */
	public String documentBackAction() {
		debugLogger("documentBackAction");
		return "/xhtml/sc/quatationtender/quatationtendercreation.xhtml?faces-redirect=true";
	}

	/**
	 * Download report.
	 * 
	 * @param scQtDocReport
	 *            the sc qt doc report
	 * @throws Exception
	 *             the exception
	 */
	public void downloadReport(ScQtDocReport scQtDocReport) throws Exception {
		debugLogger("downloadReport");
		ScQtDocReportAttachment scQtDocReportAttachment = scQtDocReport
				.getScQtDocReportAttachment();
		if (!scQtDocReport.getDefinationType().getParameterCode().equalsIgnoreCase(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD)) {
			scQtDocReportAttachment = generateDocumentAttachment(scQtDocReport);
			scQtDocReport.setScQtDocReportAttachment(scQtDocReportAttachment);
		} 
		downloadFile(scQtDocReportAttachment);
	}

	/**
	 * Download all reports.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void downloadAllReports() throws Exception {
		debugLogger("downloadAllReports");
		List<ScQtDocReport> scQtDocReports = getManagedBean()
				.getScQtDocReportList();
		List<ScQtDocReportAttachment> scAttachments = new ArrayList<ScQtDocReportAttachment>();
		for (int i = 0; i < scQtDocReports.size(); i++) {
			ScQtDocReportAttachment attachment = scQtDocReports.get(i)
					.getScQtDocReportAttachment();
			if (attachment == null) {
				attachment = generateDocumentAttachment(scQtDocReports.get(i));
				scQtDocReports.get(i).setScQtDocReportAttachment(attachment);
			}
			scAttachments.add(attachment);
		}
		downloadAllFiles("NGeP-QT-Report", scAttachments);
	}

	/**
	 * Generate document attachment.generate all document report.
	 * 
	 * @param scQtDocReport
	 *            the sc qt doc report
	 * @return ScQtDocReportAttachment
	 * @throws Exception
	 *             the exception
	 */
	public ScQtDocReportAttachment generateDocumentAttachment(
			ScQtDocReport scQtDocReport) throws Exception {
		String definitionType = scQtDocReport.getDefinationType()
				.getParameterCode();
		ScQtDocReportAttachment scQtDocReportAttachment = null;
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
				.equals(definitionType)) {
			String docDefinitionKey = scQtDocReport.getPmDocument()
					.getDocDefinitionKey();
			scQtDocReportAttachment = new ScQtDocReportAttachment();
			scQtDocReportAttachment.setScQtDocReport(scQtDocReport);
			scQtDocReportAttachment.setDocNo(scQtDocReport.getScQt().getQtNo());
			StringBuilder filename = new StringBuilder();
			// replace white space due to firefox
			filename.append(scQtDocReport.getTitle().replaceAll("\\s", "_"));

			if (SourcingConstants.DOC_DEF_KEY_SUPP_OFFER_STATEMENT
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtDocReportAttachment.setFileName(filename.toString());
				generateKenyataanTawaranPembekalPdfFromVelocity(
						(ScAttachment) scQtDocReportAttachment,
						getManagedBean().getQt());
			} else if (SourcingConstants.DOC_DEF_KEY_CONTRACT_TERM
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtDocReportAttachment.setFileName(filename.toString());
				generateTermaKontrakPdfFromVelocity(
						(ScAttachment) scQtDocReportAttachment,
						getManagedBean().getQt());
			} else if (SourcingConstants.DOC_DEF_KEY_SPECIFICATION
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtDocReportAttachment.setFileName(filename.toString());
				generateSpesifikasiPdfFromVelocity(
						(ScAttachment) scQtDocReportAttachment,
						getManagedBean().getQt());
			} else if (SourcingConstants.DOC_DEF_KEY_TECH_COMPLIANCE_CHECKLIST
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtDocReportAttachment.setFileName(filename.toString());
				generateSenaraiSemakUntukPematuhanTeknikalPdfFromVelocity(
						(ScAttachment) scQtDocReportAttachment,
						getManagedBean().getQt());
			} else if (SourcingConstants.DOC_DEF_KEY_SAMPLE_COMPLIANCE_CHECKLIST
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtDocReportAttachment.setFileName(filename.toString());
				generateSampleComplianceChecklistPdfFromVelocity(
						(ScAttachment) scQtDocReportAttachment,
						getManagedBean().getQt());
			} else if (SourcingConstants.DOC_DEF_KEY_FIN_COMPLIANCE_CHECKLIST
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtDocReportAttachment.setFileName(filename.toString());
				generateSenaraiSemakUntukPematuhanKewanganPdfFromVelocity(
						(ScAttachment) scQtDocReportAttachment,
						getManagedBean().getQt());
			} else if (SourcingConstants.DOC_DEF_KEY_LOA_SAMPLE
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtDocReportAttachment.setFileName(filename.toString());
				generateSampleSuratSetujuTerimaPdfFromVelocity(
						(ScAttachment) scQtDocReportAttachment,
						getManagedBean().getQt());
			} else if (SourcingConstants.DOC_DEF_KEY_CONTRACT_COPY_SAMPLE
					.equals(docDefinitionKey)) {
				// TODO put attachment in repository folder
				generateSamplesamplesalinankontrakPdfFromVelocity(
						(ScAttachment) scQtDocReportAttachment,
						getManagedBean().getQt());
			} else if (SourcingConstants.DOC_DEF_KEY_DECLARION_SAMPLE
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtDocReportAttachment.setFileName(filename.toString());
				generateSampleSuratPdfFromVelocity(
						(ScAttachment) scQtDocReportAttachment,
						getManagedBean().getQt());
			} else if (SourcingConstants.DOC_DEF_KEY_SCOPE_SERVICES
					.equals(docDefinitionKey)
					&& (QTConstants.EVALUATION_TWO_TIER
							.equalsIgnoreCase(getManagedBean().getQt()
									.getEvaluationType()) || (QTConstants.EVALUATION_ONE_TIER
							.equalsIgnoreCase(getManagedBean().getQt()
									.getEvaluationType()) && getManagedBean()
							.getQt().isTechEvaluationReq()))) {
				scQtDocReportAttachment = generateSkopPerkhidmatanPdfFromVelocity(
						(ScAttachment) scQtDocReportAttachment,
						getManagedBean().getQt(), docDefinitionKey,
						scQtDocReport);

			} else if (SourcingConstants.DOC_DEF_KEY_FREQUENCY_SCHEDULE
					.equals(docDefinitionKey)) {
				// Frequency Schedule - Jadual Kekerapan
				filename.append(".pdf");
				scQtDocReportAttachment.setFileName(filename.toString());
				generateFrequencySchedulePdfFromVelocity(
						(ScAttachment) scQtDocReportAttachment,
						getManagedBean().getQt());
			} else if (SourcingConstants.DOC_DEF_KEY_PROGRESSIVE_PAYMENT
					.equals(docDefinitionKey)) {
				// Progressive Payment - As and When, Jadual Pembayaran
				// Berperingkat
				filename.append(".pdf");
				scQtDocReportAttachment.setFileName(filename.toString());
				generateProgressivePaymentPdfFromVelocity(
						(ScAttachment) scQtDocReportAttachment,
						getManagedBean().getQt());
			} else if (SourcingConstants.DOC_DEF_KEY_DELIVERY_SERVICE_ADDRESS
					.equals(docDefinitionKey)) {
				generateDeliveryAddressPdfFromVelocity(
						(ScAttachment) scQtDocReportAttachment,
						getManagedBean().getQt());
			} else {
				debugLogger("No document type is exists with the key "
						+ docDefinitionKey);
				scQtDocReportAttachment = null;
			}
			scQtDocReport.setScQtDocReportAttachment(scQtDocReportAttachment);
		} else {
			if (null != scQtDocReport.getPmDocument()) {
				String docDefinitionKey = scQtDocReport.getPmDocument()
						.getDocDefinitionKey();
				scQtDocReportAttachment = new ScQtDocReportAttachment();
				scQtDocReportAttachment.setScQtDocReport(scQtDocReport);
				scQtDocReportAttachment.setDocNo(scQtDocReport.getScQt()
						.getQtNo());

				if (SourcingConstants.DOC_DEF_KEY_RULES_REGULATION
						.equals(docDefinitionKey)) {
					generateSyaratSyaratAmPdfFromVelocity(
							(ScAttachment) scQtDocReportAttachment,
							scQtDocReport);
				} else {
					debugLogger("No document type is exists with the key "
							+ docDefinitionKey);
					scQtDocReportAttachment = null;
				}
			} else {

				scQtDocReportAttachment = null;
			}
			scQtDocReport.setScQtDocReportAttachment(scQtDocReportAttachment);
		}
		return scQtDocReportAttachment;
	}

	/**
	 * Generate syarat syarat am pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scQtDocReport
	 *            the sc qt doc report
	 * @throws Exception
	 *             the exception
	 */
	private void generateSyaratSyaratAmPdfFromVelocity(ScAttachment attachment,
			ScQtDocReport scQtDocReport) throws Exception {
		debugLogger("generateSyaratSyaratAmPdfFromVelocity");
		attachment.setFileName(SourcingConstants.DOCNAME_QT_SYARAT_AM);

		String html = generateSyaratSyaratAmFromVelocity(scQtDocReport,
				getManagedBean().getQt());
		populateAttachmentDetails(attachment);

		DocumentGenerator.getInstance().generatePdf(
				new FileOutputStream(getActualFilePath(attachment)), html);
	}

	/**
	 * Generate syarat syarat am from velocity.
	 * 
	 * @param scQtDocReport
	 *            the sc qt doc report
	 * @param scQt
	 *            the sc qt
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	private String generateSyaratSyaratAmFromVelocity(
			ScQtDocReport scQtDocReport, ScQt scQt) throws Exception {
		debugLogger("generateSyaratSyaratAmFromVelocity");

		TemplateDocBackingBean backingBean = new TemplateDocBackingBean();
		ScQtDocReport qtDocReport = retriveRulesAndRegulationReport(scQtDocReport);
		if (qtDocReport != null) {
			scQtDocReport = qtDocReport;
		} else {
			// save list of documents if its not yet save
			scQtDocReport = saveScQtReportList(scQtDocReport);
		}

		TplDocMaster docMaster = scQtDocReport.getTplDocMaster();
		if (docMaster == null) {
			// clone standard tplDocMaster
			TplDocMaster tplDocMaster = cloneTplDocMasterDtls(scQtDocReport);
			scQtDocReport.setTplDocMaster(tplDocMaster);

			// save clone to db
			docMaster = saveClonedTplDocMaster(scQtDocReport);
		}
		backingBean.goToMaint(docMaster);
		backingBean.previewAction();
		String html = backingBean.getManagedBean().getPreviewValue();

		PmOrgProfile pmOrgProfile = scQt.getPmOrgProfile();

		IAddressSO addressSO = null;
		PmAddressType pmAddressType = null;
		try {
			addressSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setOrgProfileId(pmOrgProfile.getOrgProfileId());
			reqDTO.setAddressType(AddressType.CONTACT_ADDRESS.value());
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if (resDTO.getAddressTypeList().size() > 0) {
				pmAddressType = resDTO.getAddressTypeList().get(0);
			}
		} finally {
			addressSO = null;
		}

		String agencyAddress = "";
		String agencyPhoneNo = "";
		String agencyFaxNo = "";
		if (pmAddressType != null) {
			PmAddress pmAddress = pmAddressType.getPmAddress();
			agencyAddress = getFullAddress(pmAddress);
			agencyAddress = agencyAddress.replaceAll("\r\n", "<br />");
			agencyPhoneNo = pmAddress.getPhoneNo();
			agencyFaxNo = pmAddress.getFaxNo();
		}

		FacesScopes facesScopes = new FacesScopes();
		String officerName = facesScopes.getUserSessionBean().getUserName();

		html = html.replaceAll("&lt;&lt;Agency Name&gt;&gt;",
				pmOrgProfile.getOrgName());
		html = html.replaceAll("&lt;&lt;Agency name&gt;&gt;",
				pmOrgProfile.getOrgName());
		html = html.replaceAll("&lt;&lt;Department Name&gt;&gt;",
				pmOrgProfile.getOrgName());
		html = html.replaceAll("&lt;&lt;Agency Address&gt;&gt;", agencyAddress);
		html = html.replaceAll("&lt;&lt;Officer Name&gt;&gt;", officerName);
		html = html.replaceAll("&lt;&lt;Tel No&gt;&gt;", agencyPhoneNo);
		html = html.replaceAll("&lt;&lt;Faks No&gt;&gt;", agencyFaxNo);
		html = html.replaceAll("&lt;&lt;Validity Period&gt;&gt;",
				Integer.toString(scQt.getQtValidityPeriod()));
		return html;
	}

	/**
	 * Generate skop perkhidmatan pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private ScQtDocReportAttachment generateSkopPerkhidmatanPdfFromVelocity(
			ScAttachment attachment, ScQt qt, String docDefinitionKey,
			ScQtDocReport scQtDocReport) throws Exception {
		debugLogger("generateSkopPerkhidmatanPdfFromVelocity");
		ScQtDocReportAttachment docReportAttachment = new ScQtDocReportAttachment();
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			reqDTO.setScQt(qt);
			reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
			resDTO = specSo.retrieveTechnicalService(reqDTO);
			if (isAtleastOneSeriveReq(resDTO.getTechnicalCheckList())) {
				if (null != resDTO.getTechnicalCheckList()) {
					for (ScQtCheckList checklist : resDTO
							.getTechnicalCheckList()) {
						if (null != checklist.getPmDocument()) {
							if (SourcingConstants.DOC_DEF_KEY_SERVICE_SCOPE
									.equalsIgnoreCase(checklist.getPmDocument()
											.getDocDefinitionKey())) {
								// localcheckList.add(checklist);
								IQTDetailSO qtDetailSO = null;
								QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
								qtDetailSO = BusinessDelegate
										.getInstance()
										.getServiceObject(
												ModuleCodeConst.SUPPLIER_SELECTION,
												IQTDetailSO.class);
								quationTenderReqDTO.setDocCategory(checklist
										.getDocCategory());
								quationTenderReqDTO
										.setDocDefKey(docDefinitionKey);
								quationTenderReqDTO.setScQtCheckList(checklist);
								QuationTenderResDTO qtResDTO = qtDetailSO
										.retriveScQtReportByDefKey(quationTenderReqDTO);

								ScQtCheckList qtCheckList = qtResDTO
										.getScQtCheckList();

								if (qtCheckList != null
										&& qtCheckList
												.getScQtCheckListAttachment() != null) {
									attachment = qtCheckList
											.getScQtCheckListAttachment();
								}
								if (attachment != null) {
									docReportAttachment
											.setScQtDocReport(scQtDocReport);
									docReportAttachment.setFileName(attachment
											.getFileName());
									docReportAttachment.setDocNo(qt.getQtNo());
									
								 /* Copy doc with content from QCL folder to QD folder
								  * inFile = new FileInputStream(getActualFilePath(attachment));
									populateAttachmentDetails(docReportAttachment);
									 outFile = new FileOutputStream(
											getActualFilePath(docReportAttachment));
									int i=0;  
									while((i=inFile.read())!=-1){  
										outFile.write((byte)i);  
									} 
									log.info("Successfully wriiten into file");*/
									
									docReportAttachment.setFilePath(attachment.getFilePath());
									Timestamp date = new Timestamp(System.currentTimeMillis());
									PmUser createdUser = new FacesScopes().getUserSessionBean();
									docReportAttachment.setCreatedBy(createdUser.getUserId());
									docReportAttachment.setCreatedDate(date);
									docReportAttachment.setChangedBy(createdUser.getUserId());
									docReportAttachment.setCreatedUser(createdUser);
									
									scQtDocReport
											.setScQtDocReportAttachment(docReportAttachment);
								}
								break;
							} else {
								log.info("Testing");
							}
						} else {
							log.info("Testings");
						}
					}
				} else {
					log.info("Testingss");
				}
			}
		} finally {
			specSo = null;
		}
		return docReportAttachment;
	}

	/**
	 * @param technicalCheckList
	 * @return
	 */
	private boolean isAtleastOneSeriveReq(List<ScQtCheckList> checklist) {
		int i = 0;
		if (null != checklist && !checklist.isEmpty()) {
			for (ScQtCheckList specChecklist : checklist) {
				if (QTConstants.TEMPLETE_SPEC_CODE
						.equalsIgnoreCase(specChecklist.getDefinationType()
								.getParameterCode())) {
					if (null != specChecklist.getScQtDoc()) {
						ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate
								.getInstance().getServiceObject(
										ModuleCodeConst.COMMON_COMPONENT,
										ITemplateSpecSO.class);
						TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
						specReqDTO.setSpecMaster(specChecklist.getScQtDoc()
								.getTplSpecMaster());
						specReqDTO
								.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
						TemplateSpecResDTO resDTO = templateSpecSO
								.getTemplateMaster(specReqDTO);
						if (resDTO
								.getSpecMaster()
								.getItemTypeCode()
								.getParameterCode()
								.equalsIgnoreCase(
										ParameterConst.ITEM_TYPE_SERVICE)) {
							i++;
							if (i >= 1) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private void generateSampleComplianceChecklistPdfFromVelocity(
			ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generateSampleComplianceChecklistPdfFromVelocity");
		populateAttachmentDetails(attachment);

		VelocityContext context = new VelocityContext();

		context.put("ptj", qt.getPmOrgProfile().getOrgName());
		String title = qt.getQtTitle();
		if (title == null) {
			title = "";
		}
		context.put("title", title.replaceAll("&", "&#38;"));
		context.put("qtNo", qt.getQtNo());

		List<TplSpecMaster> masters = new ArrayList<TplSpecMaster>();
		IQTSpecSO specSO;
		try {
			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(qt.getQtId());
			reqDTO.setSpecType(QTConstants.TECHNICAL_SPEC_TYPE);
			reqDTO.setPrepareSpec(true);
			SpecificationResDTO resDto = specSO.retrieveSpecDetails(reqDTO);
			for (ScQtCheckList scQtCheckList : resDto.getSpecResultBean()
					.getResultList()) {
				List<TplSpecQuestion> newTplSpecQuestionsList = new ArrayList<TplSpecQuestion>();
				TplSpecMaster master = null;
				master = scQtCheckList.getScQtDoc().getTplSpecMaster();
				for (TplSpecQuestion question : master.getTplSpecQuestions()) {

					if (question.getClItem() == null) {
						question.setClItem("");
					}
					newTplSpecQuestionsList.add(question);
				}
				master.setTplSpecQuestions(newTplSpecQuestionsList);
				masters.add(master);
				context.put("masters", masters);
			}
		} finally {
			specSO = null;
		}

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_QT_SAMPLE_COMPLIANCE_CHECKLIST,
				context, new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate terma kontrak pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateTermaKontrakPdfFromVelocity(ScAttachment attachment,
			ScQt qt) throws Exception {
		populateAttachmentDetails(attachment);

		VelocityContext context = new VelocityContext();
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat amountDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));

		String deductionAmount = "";
		List<String> deductionTypes = new ArrayList<String>();
		// StringBuilder deductionTypes = new StringBuilder();
		List<ScQtDeduction> scQtDeductions = qt.getScQtDeductions();
		if (scQtDeductions != null) {
			for (ScQtDeduction scQtDeduction : scQtDeductions) {
				if (scQtDeduction.getDeductionAmount() != null) {
					deductionAmount = amountDF.format(scQtDeduction
							.getDeductionAmount());
				}
				/*
				 * if (deductionTypes.size() > 0) { deductionTypes.add(","); }
				 */
				deductionTypes.add(getParameterDesc(scQtDeduction
						.getDeductionType()));
				// deductionTypes.append(getParameterDesc(scQtDeduction.getDeductionType()));
			}
		}
		context.put("deductionAmount", deductionAmount);
		context.put("validityPeriod", qt.getQtValidityPeriod());
		context.put("contractPeriod", qt.getContractDuration());
		// context.put("deductionTypes", deductionTypes.toString());
		boolean deductionType = false;
		if (deductionTypes.contains("Penalty")) {
			deductionType = true;
			context.put("penalty", deductionType);
		}
		if (deductionTypes.contains("Royalty Fee")) {
			deductionType = true;
			context.put("royaltyFee", deductionType);
		}
		if (deductionTypes.contains("Fixed Deduction")) {
			deductionType = true;
			context.put("fixedDeduction", deductionType);
		}
		if (deductionTypes.contains("Non Fixed Deduction")) {
			deductionType = true;
			context.put("nonFixedDeduction", deductionType);
		}

		boolean isOneOff = false;
		if (qt.getFulfilmentType().getParameterCode()
				.equals(ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
			isOneOff = true;
			context.put("fulfilmentType", isOneOff);
		} else if (qt.getFulfilmentType().getParameterCode()
				.equals(ParameterConst.FULFILMENT_TYPE_SCHEDULED)
				|| qt.getFulfilmentType().getParameterCode()
						.equals(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN)) {
			context.put("fulfilmentType", isOneOff);
		}

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_QT_CONTRACT_TERM, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate senarai semak untuk pematuhan kewangan pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateSenaraiSemakUntukPematuhanKewanganPdfFromVelocity(
			ScAttachment attachment, ScQt qt) throws Exception {
		populateAttachmentDetails(attachment);

		VelocityContext context = new VelocityContext();

		FacesScopes fs = new FacesScopes();
		String hostUrl = fs.getRequest().getServerName() + ":"
				+ fs.getRequest().getServerPort();
		context.put("hostUrl", hostUrl);

		boolean isFinanceOff = false;
		List<ScQtCheckList> uploadFinanceSpecList = new ArrayList<ScQtCheckList>();
		List<ScQtCheckList> financeSpecList = new ArrayList<ScQtCheckList>();
		IQTSpecSO specSO;
		try {
			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setScQt(qt);
			reqDTO.setSpecType(QTConstants.FINANCIAL_DOC_TYPE);
			SpecificationResDTO resDto = specSO.retrieveCheckLists(reqDTO);
			financeSpecList = resDto.getFinancialCheckLists();
			for (ScQtCheckList qtCheckList : financeSpecList) {
				if (qtCheckList.getDefinationType().getParameterCode()
						.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD)
						|| qtCheckList
								.getDefinationType()
								.getParameterCode()
								.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION)) {
					if (!qtCheckList.getAction().getParameterCode()
							.equals(ParameterConst.CHECKLIST_ACTION_VIEW)) {
						uploadFinanceSpecList.add(qtCheckList);
						isFinanceOff = true;
					}
				}
			}
			context.put("isFinanceOff", isFinanceOff);
			context.put("uploadFinanceSpecList", uploadFinanceSpecList);
			context.put("bean", this);
		} finally {
			specSO = null;
		}

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_QT_FIN_COMPLIANCE_CHECKLIST, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate senarai semak untuk pematuhan teknikal pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateSenaraiSemakUntukPematuhanTeknikalPdfFromVelocity(
			ScAttachment attachment, ScQt qt) throws Exception {
		populateAttachmentDetails(attachment);

		VelocityContext context = new VelocityContext();

		FacesScopes fs = new FacesScopes();
		String hostUrl = fs.getRequest().getServerName() + ":"
				+ fs.getRequest().getServerPort();
		context.put("hostUrl", hostUrl);

		boolean isTechOff = false;
		List<ScQtCheckList> uploadTechSpecList = new ArrayList<ScQtCheckList>();
		List<ScQtCheckList> techSpecList = new ArrayList<ScQtCheckList>();

		if (qt.isTechEvaluationReq()) {
			IQTSpecSO specSO;
			try {
				specSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				reqDTO.setScQt(qt);
				reqDTO.setSpecType(QTConstants.TECHNICAL_DOC_TYPE);
				SpecificationResDTO resDto = specSO.retrieveCheckLists(reqDTO);
				techSpecList = resDto.getTechnicalCheckList();
				for (ScQtCheckList qtCheckList : techSpecList) {
					if (qtCheckList
							.getDefinationType()
							.getParameterCode()
							.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD)
							|| qtCheckList
									.getDefinationType()
									.getParameterCode()
									.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION)) {
						if (!qtCheckList.getAction().getParameterCode()
								.equals(ParameterConst.CHECKLIST_ACTION_VIEW)) {
							uploadTechSpecList.add(qtCheckList);
							isTechOff = true;
						}
					}
				}
			} finally {
				specSO = null;
			}
		}

		context.put("isTechOff", isTechOff);
		context.put("uploadTechSpecList", uploadTechSpecList);
		context.put("bean", this);

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_QT_TECH_COMPLIANCE_CHECKLIST,
				context, new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate delivery address pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateDeliveryAddressPdfFromVelocity(
			ScAttachment attachment, ScQt qt) throws Exception {
		attachment
				.setFileName(SourcingConstants.DOCNAME_QT_DELIVERY_SERVICE_ADDRESS);
		populateAttachmentDetails(attachment);

		VelocityContext context = new VelocityContext();

		boolean isOneOff = false;
		IQTSpecSO specSo;
		List<BriefingZoneBean> deliveryAddressBean = new ArrayList<BriefingZoneBean>();
		try {
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(qt.getQtId());
			SpecificationResDTO resDTO = specSo
					.retrieveDeliverSeriviceAddress(reqDTO);
			List<TplSpecZoneDtl> zones = resDTO.getSpecZoneDtls();
			if (zones != null && !zones.isEmpty()) {
				isOneOff = true;
				for (TplSpecZoneDtl specZoneDtl : zones) {
					reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
					reqDTO.setZoneId(specZoneDtl.getSpecZoneId());
					resDTO = specSo.retrieveDeliveryAddressDtls(reqDTO);
					BriefingZoneBean bean = new BriefingZoneBean(specZoneDtl,
							resDTO.getAddressResultBean());
					deliveryAddressBean.add(bean);
				}
			} else {
				// isOneOff = true;
				BriefingZoneBean bean = new BriefingZoneBean(null,
						resDTO.getAddressResultBean());
				deliveryAddressBean.add(bean);
			}
		} finally {
			specSo = null;
		}
		context.put("isOneOff", isOneOff);
		context.put("deliveryAddressBean", deliveryAddressBean);

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_QT_DELIVERY_ADDRESS, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate frequency schedule pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateFrequencySchedulePdfFromVelocity(
			ScAttachment attachment, ScQt qt) throws Exception {
		populateAttachmentDetails(attachment);

		VelocityContext context = new VelocityContext();
		SimpleDateFormat dateSDF = new SimpleDateFormat("MMM YYYY");
		context.put("dateSDF", dateSDF);

		List<ScQtSchedule> scheduleList = new ArrayList<ScQtSchedule>();
		IQTSpecSO specSo;
		SpecificationResDTO resDTO = null;
		try {
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(qt.getQtId());
			resDTO = specSo.retrieveFulFilMentScheduleDtls(reqDTO);
			if (resDTO.getScheduleResults() != null
					&& !resDTO.getScheduleResults().getResultList().isEmpty()) {
				scheduleList = resDTO.getScheduleResults().getResultList();
				//Sorting
				Collections.sort(scheduleList, new Comparator<ScQtSchedule>() {

					@Override
					public int compare(ScQtSchedule qtSchedule1, ScQtSchedule qtSchedule2) {
						return qtSchedule1.getScheduleNo().compareTo(qtSchedule2.getScheduleNo());
					}
					
				});
				for (ScQtSchedule qtSchedule : scheduleList) {
					String startMonth = "";
					String endMonth = "";
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							CommonConstants.DEFAULT_DATE_FORMAT);
					int months;
					Date startDate = null;
					Date endDate = null;
					String fromdate = dateFormat.format(qtSchedule
							.getFromDate());
					String todate = dateFormat.format(qtSchedule
							.getToDate());
					String startdate = fromdate.substring(3, 5);
					String startYear = fromdate.substring(6,
							fromdate.length());
					String fromDay = fromdate.substring(0, 2);

					String enddate = todate.substring(3, 5);
					String endYear = todate.substring(6,
							todate.length());
					String toDay = todate.substring(0, 2);

					startMonth = fulfilmentMonthlyDtls(startdate);
					endMonth = fulfilmentMonthlyDtls(enddate);
					log.info("startMonth : " + startMonth);
					log.info("endMonth : " + endMonth);

					int sYear = Integer.parseInt(startYear);
					int eYear = Integer.parseInt(endYear);

					String fromDate = fromDay + "/" + startdate + "/"
							+ sYear;
					String toDate = toDay + "/" + enddate + "/" + eYear;

					startDate = dateFormat.parse(fromDate);
					endDate = dateFormat.parse(toDate);

					months = DateUtils.getMonths(startDate, endDate);
					qtSchedule.setTotalMonths(new BigDecimal(months));
				}
			}
		} finally {
			specSo = null;
		}
		int totalFrequency = 0;
		if (scheduleList != null) {
			totalFrequency = scheduleList.size();
		}
		context.put("totalFrequency", totalFrequency);
		context.put("scheduleList", scheduleList);
		context.put("bean", this);

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_QT_FREQUENCY_SCHEDULE, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	private String fulfilmentMonthlyDtls(String startdate) {
		String startMonth = "";
		try {
			if (startdate.equalsIgnoreCase("01")) {
				startMonth = "0";
			} else if (startdate.equalsIgnoreCase("02")) {
				startMonth = "1";
			} else if (startdate.equalsIgnoreCase("03")) {
				startMonth = "2";
			} else if (startdate.equalsIgnoreCase("04")) {
				startMonth = "3";
			} else if (startdate.equalsIgnoreCase("05")) {
				startMonth = "4";
			} else if (startdate.equalsIgnoreCase("06")) {
				startMonth = "5";
			}

			else if (startdate.equalsIgnoreCase("07")) {
				startMonth = "6";
			} else if (startdate.equalsIgnoreCase("08")) {
				startMonth = "7";
			} else if (startdate.equalsIgnoreCase("09")) {
				startMonth = "8";
			} else if (startdate.equalsIgnoreCase("10")) {
				startMonth = "9";
			} else if (startdate.equalsIgnoreCase("11")) {
				startMonth = "10";
			} else if (startdate.equalsIgnoreCase("12")) {
				startMonth = "11";
			}

		} finally {

		}
		return startMonth;
	}
	
	/**
	 * Generate progressive payment pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateProgressivePaymentPdfFromVelocity(
			ScAttachment attachment, ScQt qt) throws Exception {
		populateAttachmentDetails(attachment);

		VelocityContext context = new VelocityContext();

		List<ScQtProgressivePayment> paymentList = new ArrayList<ScQtProgressivePayment>();
		IQTSpecSO specSo;
		SpecificationResDTO resDTO = null;
		try {
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(qt.getQtId());
			resDTO = specSo.retrieveFulFilMentScheduleDtls(reqDTO);
			if (resDTO.getProgressivePaymentResults() != null
					&& !resDTO.getProgressivePaymentResults().getResultList()
							.isEmpty()) {
				paymentList = resDTO.getProgressivePaymentResults()
						.getResultList();
			}
		} finally {
			specSo = null;
		}
		int totalPhase = 0;
		if (paymentList != null) {
			totalPhase = paymentList.size();
		}
		context.put("totalPhase", totalPhase);
		context.put("paymentList", paymentList);

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_QT_PROGRASSIVE_PAYMENT, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}
	
	/**
	 * Get Parameter description by parameter for report.
	 * 
	 * @param parameter
	 *            the PmParameter instance.
	 * @return parameter description
	 */
	public String getParameterDescForReport(PmParameter parameter) {
		if (parameter != null
				&& StringUtils.isNotBlank(parameter.getParameterType())) {
			return getParameterDesc(
					parameter,
					myLocale);
		} else {
			return null;
		}
	}

	/**
	 * Generate spesifikasi pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateSpesifikasiPdfFromVelocity(ScAttachment attachment,
			ScQt qt) throws Exception {
		populateAttachmentDetails(attachment);

		VelocityContext context = new VelocityContext();
		List<TplSpecMaster> masters = new ArrayList<TplSpecMaster>();
		IQTSpecSO specSO;
		try {
			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(qt.getQtId());
			reqDTO.setSpecType(QTConstants.TECHNICAL_SPEC_TYPE);
			reqDTO.setPrepareSpec(true);
			SpecificationResDTO resDto = specSO.retrieveSpecDetails(reqDTO);
			for (ScQtCheckList scQtCheckList : resDto.getSpecResultBean()
					.getResultList()) {
				List<TplSpecQuestion> newTplSpecQuestionsList = new ArrayList<TplSpecQuestion>();
				TplSpecMaster master = null;
				master = scQtCheckList.getScQtDoc().getTplSpecMaster();
				
				for (TplSpecQuestion question : master.getTplSpecQuestions()) {
					TplSpecQuestion parentQuestion = question
							.getParentSpecQuestion();

					if (question.getClItem() == null) {
						question.setClItem("");
					}
					if (question.getClSpecDetail() == null) {
						question.setClSpecDetail("");
					}
					if (question.getClUom() != null) {
						if (question.getClUom().getUomName() == null) {
							question.getClUom().setUomName("");
						}
					} else {
						question.setClUom(new PmUom());
						question.getClUom().setUomName("");
					}
					if (question.getClPriceTypeCode() == null) {
						question.setClPriceTypeCode(new PmParameter());
						question.getClPriceTypeCode().setParameterCode("");
					} else {
						if (question.getClPriceTypeCode().getParameterCode() == null) {
							question.getClPriceTypeCode().setParameterCode("");
						}
					}
					if (question.getClQuantity() == null) {
						question.setClQuantity(new BigDecimal(0));
					}

					if (question.isClAnswerRequired()) {
						question.setClAnswerRequired(true);
					}
					if (question.getClReviewRemark() == null) {
						question.setClReviewRemark("");
					}
					if (parentQuestion != null) {
						int specCount = parentQuestion.getSpecCount();
						if (specCount == 0) {
							specCount = 1;
						}
						parentQuestion.setSpecCount(specCount + 1);
					}
					newTplSpecQuestionsList.add(question);
				}
				master.setTplSpecQuestions(newTplSpecQuestionsList);
				masters.add(master);

				context.put("masters", masters);
				context.put("bean", this);
			}
		} finally {
			specSO = null;
		}
		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_QT_SPECIFICATION, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate samplesalinankontrak pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateSamplesamplesalinankontrakPdfFromVelocity(
			ScAttachment attachment, ScQt qt) throws Exception {
		debugLogger("generateSamplesamplesalinankontrakPdfFromVelocity");
		attachment.setFileName("Sampel_Kontrak_Umum.docx");

		Timestamp date = new Timestamp(System.currentTimeMillis());
		PmUser createdUser = new FacesScopes().getUserSessionBean();

		attachment.setCreatedBy(createdUser.getUserId());
		attachment.setCreatedDate(date);
		attachment.setChangedBy(createdUser.getUserId());
		attachment.setCreatedUser(createdUser);

		String docRepoSharedDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_SHARED_DIR);

		StringBuilder filePath = new StringBuilder(docRepoSharedDir);
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase());
		attachment.setFilePath(filePath.toString());

		File theDir = new File(getActualFilePath(attachment));
		theDir.mkdirs();

		AttachmentFormat scFormat = getAttachFormat(attachment);
		String formatedFileName = scFormat.format(attachment.getFileName(),
				attachment.getPrefixFileName());

		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(formatedFileName);
		attachment.setFilePath(filePath.toString());
	}

	/**
	 * Gets the attach format.
	 * 
	 * @param scAttachment
	 *            the sc attachment
	 * @return the attach format
	 */
	private AttachmentFormat getAttachFormat(ScAttachment scAttachment) {
		if (scAttachment instanceof ScLoaReportAttachment
				|| scAttachment instanceof ScQtCheckListAttachment
				|| scAttachment instanceof ScQtProposalChecklistAttachment
				|| scAttachment instanceof ScQtFinalizationAttachment) {

			return new AttachmentDateFormat();
		} else {

			return new AttachmentDefaultFormat();
		}
	}

	/**
	 * Generate sample surat setuju terima pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateSampleSuratSetujuTerimaPdfFromVelocity(
			ScAttachment attachment, ScQt qt) throws Exception {
		populateAttachmentDetails(attachment);

		PmOrgProfile pmOrgProfile = qt.getPmOrgProfile();
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat amountDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		VelocityContext context = new VelocityContext();

		FacesScopes fs = new FacesScopes();
		String hostUrl = fs.getRequest().getServerName() + ":"
				+ fs.getRequest().getServerPort();
		context.put("hostUrl", hostUrl);

		IAddressSO addressSO = null;
		PmAddressType pmAddressType = null;
		try {
			addressSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setOrgProfileId(pmOrgProfile.getOrgProfileId());
			reqDTO.setAddressType(AddressType.CONTACT_ADDRESS.value());
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if (resDTO.getAddressTypeList().size() > 0) {
				pmAddressType = resDTO.getAddressTypeList().get(0);
			}
		} finally {
			addressSO = null;
		}
		context.put("agency", pmOrgProfile.getOrgName());
		context.put("ministry", pmOrgProfile.getParentOrgProfile().getOrgName());

		String agencyAddress = "";
		String agencyPhoneNo = "";
		String agencyFaxNo = "";
		if (pmAddressType != null) {
			PmAddress pmAddress = pmAddressType.getPmAddress();
			agencyAddress = getFullAddress(pmAddress);
			agencyAddress = agencyAddress.replaceAll("\r\n", "<br />");
			agencyPhoneNo = pmAddress.getPhoneNo();
			agencyFaxNo = pmAddress.getFaxNo();
		}
		context.put("agencyAddress", agencyAddress);
		context.put("agencyPhoneNo", agencyPhoneNo);
		context.put("agencyFaxNo", agencyFaxNo);
		context.put("fileNo", qt.getFileNo());

		StringBuilder procurementMode = new StringBuilder();
		if (procurementMode != null) {
			procurementMode.append(getParameterDesc(qt.getProcurementMode()));
		}
		context.put("procurementMode", procurementMode);
		String title = qt.getQtTitle();
		if (title == null) {
			title = "";
		}
		context.put("title", title.replaceAll("&", "&#38;"));

		String estimatedPrice = "";
		if (qt.getEstimatedAmount() != null) {
			estimatedPrice = amountDF.format(qt.getEstimatedAmount());
		}
		context.put("estimatedPrice", estimatedPrice);
		boolean isEstimate = false;
		if (qt.getEstimatedAmount().intValue() >= 200000) {
			isEstimate = true;
		}
		context.put("isEstimate", isEstimate);

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_QT_LOA_SAMPLE, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate sample surat pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateSampleSuratPdfFromVelocity(ScAttachment attachment,
			ScQt qt) throws Exception {
		populateAttachmentDetails(attachment);

		// CommonUtils commonUtils = new CommonUtils();
		VelocityContext context = new VelocityContext();

		String title = qt.getQtTitle();
		if (title == null) {
			title = "";
		}
		context.put("title", title.replaceAll("&", "&#38;"));
		context.put("qtNo", qt.getQtNo());

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_QT_INTEGRITY_PACT_SAMPLE, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Generate kenyataan tawaran pembekal pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param qt
	 *            the qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateKenyataanTawaranPembekalPdfFromVelocity(
			ScAttachment attachment, ScQt qt) throws Exception {
		populateAttachmentDetails(attachment);

		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
						.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		
		DecimalFormat amountDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		VelocityContext context = new VelocityContext();

		FacesScopes fs = new FacesScopes();
		String hostUrl = fs.getRequest().getServerName() + ":"
				+ fs.getRequest().getServerPort();
		context.put("hostUrl", hostUrl);

		context.put("commonUtils", commonUtils);

		String ministryOrgName = null;
		if (qt.getMinistryOrgProfile() != null) {
			ministryOrgName = qt.getMinistryOrgProfile().getOrgName();
		} else {
			ministryOrgName = StringUtils.EMPTY;
		}
		context.put("ministry", ministryOrgName);
		// context.put("ministry",
		// qt.getPmOrgProfile().getParentOrgProfile().getOrgName());
		context.put("ptj", qt.getPmOrgProfile().getOrgName());
		String title = qt.getQtTitle();
		if (title == null) {
			title = "";
		}
		context.put("qtTitle", title.replaceAll("&", "&#38;"));

		StringBuilder openType = new StringBuilder();
		if (openType != null) {
			openType.append(getParameterDesc(
					ParameterTypeConst.QUOTATION_TENDER_TYPE, qt.getQtType()));
		}
		context.put("qtType", openType);

		context.put("panel", qt.isPanel());

		List<ScQtCategory> categoryResults1 = new ArrayList<ScQtCategory>();
		List<ScQtCategory> categoryResults = new ArrayList<ScQtCategory>();
		IQTDetailSO QtDetailsSO;
		try {
			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			tenderReqDTO.setQtdetail(this.getManagedBean().getQt());
			QuationTenderResDTO tenderResDTO = QtDetailsSO
					.retriveQTSuppQulCriDetails(tenderReqDTO);
			categoryResults = tenderResDTO.getCategories()
					;
			if (null != tenderResDTO.getLocationDetails()) {
				context.put("locality", tenderResDTO.getLocationDetails());
			}
		} finally {
			QtDetailsSO = null;
		}
		if (categoryResults != null) {
			for (ScQtCategory scQtCategory : categoryResults) {
				ScQtCategory qtCategory = null;
				try {
					qtCategory = (ScQtCategory) BeanUtils
							.cloneBean(scQtCategory);
				} catch (IllegalAccessException | InstantiationException
						| InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
				categoryResults1.add(qtCategory);
			}
		}
		context.put("categoryResults1", categoryResults1);

		String publishDate = "";
		if (qt.getPublishDate() != null) {
			publishDate = dateSDF.format(qt.getPublishDate());
		}
		context.put("publishDate", publishDate);

		String closingDate = "";
		if (qt.getClosingDate() != null) {
			closingDate = dateSDF.format(qt.getClosingDate());
		}
		context.put("closingDate", closingDate);
		context.put("validityPeriod", qt.getQtValidityPeriod());

		String locationLevel = null;
		boolean isLocLevel = false;
		boolean isSubLocLevel = false;
		if (qt.getZoneLocationLevel() != null) {
			if (ParameterConst.LOCATION_LEVEL_STATE.equals(qt
					.getZoneLocationLevel().getParameterCode())) {
				isLocLevel = true;
				locationLevel = "State";
			} else if (ParameterConst.LOCATION_LEVEL_DIVISION.equals(qt
					.getZoneLocationLevel().getParameterCode())) {
				isLocLevel = true;
				locationLevel = "Division";
			} else if (ParameterConst.LOCATION_LEVEL_DISTRICT.equals(qt
					.getZoneLocationLevel().getParameterCode())) {
				isLocLevel = true;
				locationLevel = "District";
			}
			context.put("locLevel", isLocLevel);
		} else {
			ScQtSupplierCriteria criteria = qt.getScQtSupplierCriteria();
			if (criteria.getLocationLevel() != null) {
				if (ParameterConst.LOCATION_LEVEL_STATE.equals(criteria
						.getLocationLevel().getParameterCode())) {
					isSubLocLevel = true;
					locationLevel = "State";
				} else if (ParameterConst.LOCATION_LEVEL_DISTRICT
						.equals(criteria.getLocationLevel().getParameterCode())) {
					isSubLocLevel = true;
					locationLevel = "District";
				}
				context.put("subLocLevel", isSubLocLevel);
			} else {
				context.put("subLocLevel", isSubLocLevel);
			}
			context.put("locLevel", isLocLevel);
		}
		context.put("locationLevel", locationLevel);

		IQTDetailSO qtDtlSO;
		try {
			qtDtlSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
			qtReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			qtReqDTO.setBdgDtlsReq(true);
			QuationTenderResDTO qtResDTO = new QuationTenderResDTO();
			qtResDTO = qtDtlSO.retriveQTDetails(qtReqDTO);
			if (this.getManagedBean().getQt().isZonal()) {
				context.put("zoneLocality", qtResDTO.getZoneDetails());
			}
		} finally {
			QtDetailsSO = null;
		}

		context.put("bsvReq", qt.isBsvRequired());
		boolean isZone = false;
		List<BriefingZoneBean> zoneBeans = new ArrayList<BriefingZoneBean>();
		if (qt.isBsvRequired()) {
			QTDetailBackingBean backingBean = new QTDetailBackingBean();
			backingBean.retrieveBriefingSitevisitTabDtl();
			List<BriefingZoneBean> briefingZoneBean = this.getManagedBean()
					.getBriefingZoneBean();
			if (briefingZoneBean != null && !briefingZoneBean.isEmpty()) {
				isZone = true;
				for (BriefingZoneBean zoneBean : this.getManagedBean()
						.getBriefingZoneBean()) {
					zoneBeans.add(zoneBean);
				}
			} else {
				BriefingZoneBean bsvDtlBean = this.getManagedBean()
						.getBsvDtlBean();
				if (null == bsvDtlBean) {
					bsvDtlBean = this.getManagedBean().getBsvDtlBean();
				}
				zoneBeans.add(bsvDtlBean);
			}
			context.put("isZone", isZone);
			context.put("zoneBeans", zoneBeans);
		}

		boolean isOneOff = false;
		if (qt.getFulfilmentType().getParameterCode()
				.equals(ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
			isOneOff = true;
			context.put("fulfilmentType", isOneOff);
		} else if (qt.getFulfilmentType().getParameterCode()
				.equals(ParameterConst.FULFILMENT_TYPE_SCHEDULED)
				|| qt.getFulfilmentType().getParameterCode()
						.equals(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN)) {
			context.put("fulfilmentType", isOneOff);
		}
				
		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_QT_SUPP_OFFER_STATEMENT, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Gets the parameter so.
	 * 
	 * @return the parameter so
	 */
	private IParameterSO getParameterSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}

	/**
	 * Checks if is select all.
	 * 
	 * @return true, if is select all
	 */
	public boolean isSelectAll() {
		return selectAll;
	}

	/**
	 * Sets the select all.
	 * 
	 * @param selectAll
	 *            the new select all
	 */
	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Download all reports.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void createDocumentAttachments() throws Exception {
		debugLogger("createDocumentAttachment");
		List<ScQtDocReport> scQtDocReports = getManagedBean()
				.getScQtDocReportList();

		for (int i = 0; i < scQtDocReports.size(); i++) {
			ScQtDocReportAttachment docReportAttachment = scQtDocReports.get(i)
					.getScQtDocReportAttachment();
			if (docReportAttachment == null) {
				docReportAttachment = generateDocumentAttachment(scQtDocReports.get(i));
				scQtDocReports.get(i).setScQtDocReportAttachment(docReportAttachment);
			}
		}

	}

	/**
	 * Download all reports.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void reGenerateDocuments(Long qtId, Long statusId,
			boolean isPcbProcess) {
		debugLogger("reGenerateDocuments");
		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		quationTenderReqDTO.setQtId(qtId);
		quationTenderReqDTO.setStatusId(statusId);
		quationTenderReqDTO.setPcbProcess(isPcbProcess);
		List<ScReport> reports = detailSO.retriveRegenerateDocumentsByQt(
				quationTenderReqDTO).getRegenerateReports();

		SupplierFinalizationBackingBean finalizationBackingBean = new SupplierFinalizationBackingBean();
		for (int i = 0; i < reports.size(); i++) {

			try {
				if (ParameterConst.DOCUMENT_CATEGORY_QUOTATION_TENDER_DOCUMENTS
						.equalsIgnoreCase(reports.get(i).getDocCategory())) {
					generateDocumentAttachment((ScQtDocReport) reports.get(i));
				} else if (ParameterConst.DOCUMENT_CATEGORY_BRIEFING_PAPER
						.equalsIgnoreCase(reports.get(i).getDocCategory())) {
					finalizationBackingBean
							.generateDocumentAttachment((ScQtFinalizationReport) reports
									.get(i));
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			}

		}

	}

}
