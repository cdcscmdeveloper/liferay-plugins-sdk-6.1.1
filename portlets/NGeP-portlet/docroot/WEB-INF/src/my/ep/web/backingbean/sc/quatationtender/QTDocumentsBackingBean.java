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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.sc.QtAmendMentBean;
import my.ep.biz.bean.sc.QtAmendMentValueBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.document.IQTDocumentSO;
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
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.QTFieldConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateDocConst;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.document.QTDocumentReqDTO;
import my.ep.dto.sc.document.QTDocumentResDTO;
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
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.common.ScAddress;	// Bug #36702@Syazani 28/11/2024
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScReport;
import my.ep.persistence.entity.sc.loaloi.ScLoaReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;	// Bug #36702@Syazani 28/11/2024
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtDeliveryLoc;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklistAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.persistence.entity.tpl.doc.TplDocSection;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.util.CustomUtils;	// Bug #36702@Syazani 28/11/2024
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.ResourceBundleUtils;
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
import my.ep.web.managedbean.sc.supplierfinalization.SupplierFinalizationManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.VmUtil;	// Bug #36702@Syazani 28/11/2024
import my.ep.web.util.template.DocumentGenerator;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 * The Class QTDocumentsBackingBean.
 * 
 * @author SudheerKumar
 */
@ManagedBean(name = "qTDocumentsBackingBean")
@RequestScoped
public class QTDocumentsBackingBean extends SourcingBaseBackingBean<QTDetailManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = EpLogger.getLogger(QTDocumentsBackingBean.class);

	/** The Constant DOCUMENT_PAGE. */
	private static final String DOCUMENT_PAGE = "/xhtml/sc/quatationtender/quatationtendercreation.xhtml";
	private static final Locale myLocale = new Locale(SourcingConstants.MALAY_LANGUAGE_CODE, SourcingConstants.MALAYSIA_COUNTRY_CODE);
	
	
	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		
		ScQt scQt = this.getManagedBean().getQt();
		if(scQt != null){
			message += " @qtNo = "+scQt.getQtNo();
		}
		if (log.isDebugEnabled()) {
			log.debug(message);
		} 
		/*else {
			log.info(message);
		}*/
		
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
			} else {
				// Filter By - procurementMode
				List<String> procurementModes = new ArrayList<String>();
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_QUOTATION);
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_TENDER);
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"procurementMode.parameterCode",
								SearchOperator.OPERATOR_IN.value(), 
								procurementModes));
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
		getManagedBean().setSelectedCloneScQtDocReports(null);
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
		recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE);
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
		if (null == selectedReportsList || selectedReportsList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_760098);
		}
		
			for (ScQtDocReport report : selectedReportsList) {
				report.setDeleted(true);
				removeDocumentList.add(report);
				docReportsList.remove(report);
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
			List<ScQtDocReport> scQtDocReports = getManagedBean()
					.getScQtDocReportList();
			int indexOfReport = scQtDocReports.indexOf(selectedScQtDocReport);
			scQtDocReportAttachment.setDocNo(selectedScQtDocReport.getScQt()
					.getQtNo());
			scAttachment = scQtDocReportAttachment;
			uploadAttachment(event.getFile(), scAttachment);
			String fileName = scAttachment.getFileName();
			if (indexOfReport >= 0 && fileName != null) {
				scQtDocReportAttachment.setFileName(indexOfReport + SourcingConstants.UNDER_SCORE
						+ fileName);
			}
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
		IQTDocumentSO qtDocumentSO;
		QTDocumentReqDTO reqDTO = new QTDocumentReqDTO();
		QTDocumentResDTO resDTO = null;
		try {
			qtDocumentSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDocumentSO.class);
			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
			resDTO = qtDocumentSO.retriveCloneQTDocDetails(reqDTO);
			List<ScQtDocReport> scQtDocReports = resDTO.getQtDocReports();
			this.getManagedBean().setCloneScQtDocReports(scQtDocReports);
		} finally {
			qtDocumentSO = null;
		}
	}

	/**
	 * Adds the document clone action.
	 */
	public void addDocumentCloneAction() {
		debugLogger("addDocumentCloneAction");
		ScQt scQt = this.getManagedBean().getQt();
		List<ScQtDocReport> reportList = getManagedBean()
				.getScQtDocReportList();
		if (null == reportList) {
			reportList = new ArrayList<ScQtDocReport>();
		}
		List<String> existedDocumets=new ArrayList<String>();
		ScQtDocReport templetDoc=null;
		if(CollectionUtils.isNotEmpty(reportList)){
			for(ScQtDocReport docReport:reportList){
				//String docDefinitionKey = docReport.getPmDocument()!=null ?docReport.getPmDocument().getDocDefinitionKey() : docReport.getTitle();
				if(null==docReport.getPmDocument()){
				existedDocumets.add(docReport.getTitle());
				}
				else{
				String docDefinitionKey=	docReport.getPmDocument().getDocDefinitionKey();
					if (SourcingConstants.DOC_DEF_KEY_RULES_REGULATION
							.equals(docDefinitionKey)) {
					templetDoc=docReport;
					}
				}
			}
			
		}
		List<ScQtDocReport> scQtDocReports = this.getManagedBean()
				.getSelectedCloneScQtDocReports();
		if (!this.getManagedBean().getSelectedCloneScQtDocReports().isEmpty()) {
			for (ScQtDocReport docReport : scQtDocReports) {
				ScQtDocReport qtDocReport = null;
				ScQtDocReportAttachment qtDocReportAttachment=null;
				try {
					if (!existedDocumets
							.contains(
									 docReport.getTitle())) {
						qtDocReport = (ScQtDocReport) BeanUtils
								.cloneBean(docReport);
					}
				} catch (IllegalAccessException | InstantiationException
						| InvocationTargetException | NoSuchMethodException e) {
					log.warn(e.getMessage(), e);
				}
				if (null != qtDocReport) {
					qtDocReport.setQtReportId(null);
					qtDocReport.setScQt(scQt);
					qtDocReport.setDocId(String.valueOf(qtDocReport.getScQt().getQtId()));
					setUserDetails(qtDocReport);
					qtDocReport.setChangedDate(null);
					
					if (docReport.getScQtDocReportAttachment() != null) {
						try {
							 qtDocReportAttachment = (ScQtDocReportAttachment)BeanUtils.cloneBean(docReport.getScQtDocReportAttachment());
							if (qtDocReportAttachment != null) {
								qtDocReportAttachment.setAttachmentId(null);
								qtDocReportAttachment.setScQtDocReport(qtDocReport);
								setUserDetails(qtDocReportAttachment);
								qtDocReportAttachment.setChangedDate(null);
								
								qtDocReport.setScQtDocReportAttachment(qtDocReportAttachment);
							}
						} catch (IllegalAccessException | InstantiationException
								| InvocationTargetException | NoSuchMethodException e) {
							log.warn(e.getMessage(), e);
						}	
					}
					if (docReport.getTplDocMaster() != null) {
						IQTTemplateSO qtTemplateSO = null;
						try {
							qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);
					if(null!=templetDoc){
					reportList.remove(templetDoc);
					templetDoc=retriveRulesAndRegulationReport(templetDoc);
					TplDocMaster master=	retrieveTplDocMasterById(docReport.getTplDocMaster().getDocMasterId());
					master=cloneDocMasterDocSectionDetails(master, templetDoc);
					templetDoc.setTplDocMaster(master);
						QTTemplateDocReqDTO reqDTO = new QTTemplateDocReqDTO();
						reqDTO.setScQtDocReport(templetDoc);
						QTTemplateDocResDTO resDTO = qtTemplateSO.saveQtReports(reqDTO);
						qtDocReport=resDTO.getScQtDocReport();
					}else{
						TplDocMaster master=	retrieveTplDocMasterById(docReport.getTplDocMaster().getDocMasterId());
						master=cloneDocMasterDocSectionDetails(master, qtDocReport);
						qtDocReport.setTplDocMaster(master);
							QTTemplateDocReqDTO reqDTO = new QTTemplateDocReqDTO();
							reqDTO.setScQtDocReport(qtDocReport);
							QTTemplateDocResDTO resDTO = qtTemplateSO.saveQtReports(reqDTO);
							qtDocReport=resDTO.getScQtDocReport();
						
					}
						
					}finally{
						qtTemplateSO = null;
					}
						
					}
					reportList.add(qtDocReport);
					
				}
			}
			 getManagedBean()
				.setScQtDocReportList(reportList);
		} else {
			throw new EpException(MessageConst.ERROR_760141);
		}
		existedDocumets=null;
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
			ScQt scQt = getManagedBean().getQt();
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
			retriveTemplateDocParams(tplDocBB, scQt);
			this.getManagedBean().getScQtDocReportList().remove(index);
			this.getManagedBean().getScQtDocReportList()
					.add(index, this.getManagedBean().getScQtDocReport());
			
			templateDocUrl = tplDocBB.goToMaint(docMaster,this.isReadOnly());
			
			Long currentStatusId = scQt.getQtStatus().getStatusId();
			if (!this.isReadOnly() && super.isQTStatusGreaterThanOrEquals(currentStatusId, QTStatusConst.QT_PENDING_PUBLICATION)) {
				QtAmendMentBean amendMentBean = this.getManagedBean()
						.getAmendMentBean();
				if (null == amendMentBean) {
					amendMentBean = new QtAmendMentBean();	
					this.getManagedBean().setAmendMentBean(amendMentBean);
				}
				String key = QTFieldConst.FIELD_QT_DOCUMENTS;
				QtAmendMentValueBean valueBean = amendMentBean.getValueMap().get(key);
				if (valueBean == null) {
					// set the current html content into old value.
					tplDocBB.previewAction();
					valueBean = new QtAmendMentValueBean();
					valueBean.setOldValue(tplDocBB.getManagedBean().getPreviewValue());
					
					amendMentBean.getValueMap().put(key, valueBean);
				}
			}

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
		IQTDocumentSO qtDocumentSO = null;
		try {
			qtDocumentSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDocumentSO.class);

			QTDocumentReqDTO reqDTO = new QTDocumentReqDTO();
			reqDTO.setScReport(scQtDocReport);
			reqDTO.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_QUOTATION_TENDER_DOCUMENTS);
			reqDTO.setDocDefKey(SourcingConstants.DOC_DEF_KEY_RULES_REGULATION);

			QTDocumentResDTO resDTO = qtDocumentSO
					.retriveScQtReportByDefKey(reqDTO);
			ScQtDocReport qtDocReport = (ScQtDocReport) resDTO.getScReport();
			return qtDocReport;
		} finally {
			qtDocumentSO = null;
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
		log.debug("==========doc master id===="+tplMasterId);
		TplDocMaster templateDocMaster = retrieveTplDocMasterById(tplMasterId);
		return cloneDocMasterDocSectionDetails(templateDocMaster,scQtDocReport);
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
		IQTDetailSO detailsSO=null;
		try {
			templateDocSO = (ITemplateDocSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
							ITemplateDocSO.class);
			detailsSO =  BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);
			TemplateDocReqDTO reqDTO = new TemplateDocReqDTO();
			PmParameter parameter=getParameterByTypeAndCode(ParameterTypeConst.PROCUREMENT_CATEGORY,ParameterConst.PROCUREMENT_CATEGORY_OTHERS);
			log.debug("========parametrid===="+parameter.getParameterId());
			reqDTO.setCategoryId(parameter.getParameterId());
			PmParameter processType = getPmParameter(
					ParameterTypeConst.TPL_DOC_PROCESS_TYPE,
					ParameterConst.TPL_DOC_PROCESS_TYPE_STANDARD);
			reqDTO.setProcessTypeId(processType.getParameterId());
			PmParameter quotationOrTender=new PmParameter();
			quotationOrTender.setParameterType(ParameterTypeConst.SC_PROCUREMENT_MODE);
			QuationTenderReqDTO quotationReqDTO=new QuationTenderReqDTO();
			quotationReqDTO.setQtId(scQt.getQtId());
			QuationTenderResDTO resDTo=detailsSO.retriveQtById(quotationReqDTO);
			quotationOrTender.setParameterCode(resDTo.getQtDetail().getProcurementMode().getParameterCode());
			Locale locale =myLocale;
			//locale = myLocale;
			String language=locale.getLanguage();
			log.debug("=====display language====="+language);
			String paramDesc=getParameterDesc(quotationOrTender,locale);
			//log.info("=====param quotation or tenderdesc====="+paramDesc);
			reqDTO.setTitle(TemplateDocConst.PARAM_DOCTITLE_QT+TemplateDocConst.PARAM_SPACE+TemplateDocConst.PARAM_HIGH_FUN+TemplateDocConst.PARAM_SPACE+paramDesc);
			log.debug("====reqDTO.setTitle====="+reqDTO.getTitle());  
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
			detailsSO=null;
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
			scQtDocReportAttachment = generateDocumentAttachment(scQtDocReport,null);
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
			
			ScQtDocReport docReports = scQtDocReports.get(i);
			ScQtDocReportAttachment attachment = scQtDocReports.get(i)
					.getScQtDocReportAttachment();
			Long currentStatusId = this.getManagedBean().getCurrentStatusId();
			
			if(currentStatusId != null && !isQTStatusPublished(currentStatusId)){
				attachment = generateDocumentAttachment(scQtDocReports.get(i),getPrefixFileName((i+1)));
				scQtDocReports.get(i).setScQtDocReportAttachment(attachment);
				//scQtDocReports.get(i).setScQtDocReportAttachment(
						//generateDocumentAttachment(scQtDocReports.get(i),getPrefixFileName((i + 1))));
			} else {
				if (attachment == null) {
					attachment = generateDocumentAttachment(scQtDocReports.get(i),getPrefixFileName((i+1)));
					scQtDocReports.get(i).setScQtDocReportAttachment(attachment);
				}
			}
			
			
			String docDefinitionKey = docReports.getPmDocument()!=null ?docReports.getPmDocument().getDocDefinitionKey() : " ";
					
			if (attachment != null && SourcingConstants.DOC_DEF_KEY_CONTRACT_COPY_SAMPLE
					.equals(docDefinitionKey)) {
				String custFileName = getDocTitleAsFileName((i + 1),
						docReports.getTitle(), attachment.getFilePath());
				if (custFileName != null) {
					attachment.setFileName(custFileName);
				}
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
			ScQtDocReport scQtDocReport,String prefixFileName) throws Exception {
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
			if(prefixFileName !=null){
				filename.append(prefixFileName).append(SourcingConstants.UNDER_SCORE);
			}
			// replace white space due to firefox
			filename.append(scQtDocReport.getTitle()
					.replaceAll(SourcingConstants.REG_EXP_WHITE_SPACE,SourcingConstants.UNDER_SCORE)
					.replaceAll(SourcingConstants.REG_EXP_BACK_SLASH,SourcingConstants.UNDER_SCORE));

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
				generateSamplesamplesalinankontrakPdfFromVelocity(
						(ScAttachment) scQtDocReportAttachment,
						getManagedBean().getQt(),prefixFileName);
			} else if (SourcingConstants.DOC_DEF_KEY_DECLARION_SAMPLE
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				scQtDocReportAttachment.setFileName(filename.toString());
				generateSampleSuratPdfFromVelocity(
						(ScAttachment) scQtDocReportAttachment,
						getManagedBean().getQt());
			} else if (SourcingConstants.DOC_DEF_KEY_SCOPE_SERVICES
					.equals(docDefinitionKey)
					&& (ParameterConst.EVALUATION_TYPES_TWO_TIER
							.equalsIgnoreCase(getManagedBean().getQt()
									.getEvaluationType()) || (ParameterConst.EVALUATION_TYPES_ONE_TIER
							.equalsIgnoreCase(getManagedBean().getQt()
									.getEvaluationType()) && getManagedBean()
							.getQt().isTechEvaluationReq()))) {
				scQtDocReportAttachment = generateSkopPerkhidmatanPdfFromVelocity(
						(ScAttachment) scQtDocReportAttachment,
						getManagedBean().getQt(), docDefinitionKey,
						scQtDocReport,prefixFileName);

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
				StringBuilder fileName = new StringBuilder();
				if(prefixFileName != null){
					fileName.append(prefixFileName).append(SourcingConstants.UNDER_SCORE);
				}
				fileName.append(SourcingConstants.DOCNAME_QT_DELIVERY_SERVICE_ADDRESS);
				scQtDocReportAttachment.setFileName(fileName.toString());
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
					StringBuilder fileName = new StringBuilder();
					if (prefixFileName != null) {
						fileName.append(prefixFileName).append(SourcingConstants.UNDER_SCORE);
					}
					fileName.append(SourcingConstants.DOCNAME_QT_SYARAT_AM);
					
					scQtDocReportAttachment.setFileName(fileName.toString());
					generateSyaratSyaratAmPdfFromVelocity(
							(ScAttachment) scQtDocReportAttachment,
							scQtDocReport);
				} else {
					debugLogger("No document type is exists with the key "
							+ docDefinitionKey);
					scQtDocReportAttachment = null;
				}
			} else {
				if(null!=scQtDocReport.getScQtDocReportAttachment()){
					StringBuilder fileName = new StringBuilder();
					if (prefixFileName != null) {
						fileName.append(prefixFileName).append(SourcingConstants.UNDER_SCORE);
					}
					fileName.append(scQtDocReport.getScQtDocReportAttachment().getFileName().toString());
					scQtDocReport.getScQtDocReportAttachment().setFileName(fileName.toString());
					scQtDocReportAttachment	=scQtDocReport.getScQtDocReportAttachment();
				}
				else{
				scQtDocReportAttachment = null;
				}
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
		retriveTemplateDocParams(backingBean,scQt);
		backingBean.previewAction();
		String html = backingBean.getManagedBean().getPreviewValue();
		html=html.replaceAll(TemplateDocConst.PARAM_BREAK_LINE, TemplateDocConst.PARAM_SPACE);
		html=html.replaceAll(TemplateDocConst.PARAM_PREFIX_POSTFIX, TemplateDocConst.PARAM_SPACE);
		/*PmOrgProfile pmOrgProfile = scQt.getPmOrgProfile();

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
			agencyAddress = agencyAddress.replaceAll(TemplateDocConst.NEW_LINE_CHARACTER, TemplateDocConst.HTML_NEW_LINE_CHARACTER);
			agencyPhoneNo = pmAddress.getPhoneNo();
			agencyFaxNo = pmAddress.getFaxNo();
		}

		FacesScopes facesScopes = new FacesScopes();
		String officerName = facesScopes.getUserSessionBean().getUserName();
		log.info("======currentstatusid generateSyaratSyaratAmFromVelocity===="+this.getManagedBean().getCurrentStatusId());
		String ptjName=" ";
		if(null!=this.getManagedBean().getCurrentStatusId()){
		 ptjName=retrievePTJByOrgProfileId(scQt.getPmOrgProfile().getOrgProfileId(),isQTStatusPublished(this.getManagedBean().getCurrentStatusId())?scQt.getPublishDate() : scQt.getCreatedDate());
		}else{
			ptjName=scQt.getPmOrgProfile().getOrgName();
		}
		log.info("=======ptjName====="+ptjName);

		html = html.replaceAll("&lt;&lt;Agency Name&gt;&gt;",
				ptjName);
		html = html.replaceAll("&lt;&lt;Agency name&gt;&gt;",
				ptjName);
		html = html.replaceAll("&lt;&lt;Department Name&gt;&gt;",
				ptjName);
		html = html.replaceAll("&lt;&lt;Agency Address&gt;&gt;", agencyAddress);
		html = html.replaceAll("&lt;&lt;Officer Name&gt;&gt;", officerName);
		html = html.replaceAll("&lt;&lt;Tel No&gt;&gt;", agencyPhoneNo);
		html = html.replaceAll("&lt;&lt;Faks No&gt;&gt;", agencyFaxNo);
		html = html.replaceAll("&lt;&lt;Validity Period&gt;&gt;",
				scQt.getQtValidityPeriod() != null ? Integer.toString(scQt.getQtValidityPeriod()) : "");*/
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
			ScQtDocReport scQtDocReport,String prefixFileName) throws Exception {
		debugLogger("generateSkopPerkhidmatanPdfFromVelocity");
	
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;
		ScQtDocReportAttachment docReportAttachment = null;
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
								IQTDocumentSO qtDocumentSO = null;
								QTDocumentReqDTO quationTenderReqDTO = new QTDocumentReqDTO();
								qtDocumentSO = BusinessDelegate
										.getInstance()
										.getServiceObject(
												ModuleCodeConst.SUPPLIER_SELECTION,
												IQTDocumentSO.class);
								quationTenderReqDTO.setDocCategory(checklist
										.getDocCategory());
								quationTenderReqDTO
										.setDocDefKey(docDefinitionKey);
								quationTenderReqDTO.setScQtCheckList(checklist);
								QTDocumentResDTO qtResDTO = qtDocumentSO
										.retriveScQtReportByDefKey(quationTenderReqDTO);

								ScQtCheckList qtCheckList = qtResDTO
										.getScQtCheckList();

								if (qtCheckList != null
										&& qtCheckList
												.getScQtCheckListAttachment() != null) {
									attachment = qtCheckList
											.getScQtCheckListAttachment();
								}
								if (attachment != null && attachment
										.getFileName() != null) {
									 docReportAttachment = new ScQtDocReportAttachment();
									docReportAttachment
											.setScQtDocReport(scQtDocReport);
									StringBuilder fileName = new StringBuilder();
									if(prefixFileName != null){
										fileName.append(prefixFileName).append(SourcingConstants.UNDER_SCORE);
									}
									fileName.append(attachment.getFileName());
											
									docReportAttachment.setFileName(fileName.toString());
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
								log.debug("Testing");
							}
						} else {
							log.debug("Testings");
						}
					}
				} else {
					log.debug("Testingss");
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
		log.debug("======currentstatusid generateSampleComplianceChecklistPdfFromVelocity===="+this.getManagedBean().getCurrentStatusId());
		String ptjName=" ";
		if(null!=this.getManagedBean().getCurrentStatusId()){
		 ptjName=retrievePTJByOrgProfileId(qt.getPmOrgProfile().getOrgProfileId(),isQTStatusPublished(this.getManagedBean().getCurrentStatusId())?qt.getPublishDate() : qt.getCreatedDate());
		}else{
			ptjName=qt.getPmOrgProfile().getOrgName();
		}
		log.debug("=======ptjName====="+ptjName);
		context.put("ptj", ptjName);
		String title = qt.getQtTitle();
		if (title == null) {
			title = "";
		}
		title=title.replaceAll(QTConstants.NEW_LINE_CHARACTER,TemplateDocConst.PARAM_SPACE);
		title=title.replaceAll(QTConstants.AMBRA_SENT,TemplateDocConst.PARAM_SPACE);
		title=title.replaceAll(QTConstants.LESS_THEN,TemplateDocConst.PARAM_SPACE);
		title=title.replaceAll(QTConstants.GREATER_THEN,TemplateDocConst.PARAM_SPACE);
		context.put("title", title);
		context.put("qtNo", qt.getQtNo());

		List<TplSpecMaster> masters = new ArrayList<TplSpecMaster>();
		IQTSpecSO specSO;
		try {
			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(qt.getQtId());
			reqDTO.setSpecType(ParameterConst.SPECIFICATION_TYPE_TECHNICAL);
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
		String fulfilmentType = qt.getFulfilmentType() != null ? qt
				.getFulfilmentType().getParameterCode() : null;
		if (ParameterConst.FULFILMENT_TYPE_ONE_OFF.equals(fulfilmentType)) {
			isOneOff = true;
			context.put("fulfilmentType", isOneOff);
		} else if (ParameterConst.FULFILMENT_TYPE_SCHEDULED
				.equals(fulfilmentType)
				|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
						.equals(fulfilmentType)) {
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
			SpecificationResDTO resDto = specSO.retrieveCheckListsForReports(reqDTO);
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
								
						//start Bug #27158
						String title = qtCheckList.getTitle();
						
						if (title == null) {
							title = "";
						}
						
						title = title.replaceAll("\r\n", "<br />");
						title = title.replaceAll("<", "&#60;");
						title = title.replaceAll(">", "&#62;");
						title = title.replaceAll("\u2264", "&#60;=");//symbol less then equal
						title = title.replaceAll("\u2265", "&#62;=");//symbol greater then equal	
						
						qtCheckList.setTitle(title);
						//end Bug #27158
						
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
				SpecificationResDTO resDto = specSO.retrieveCheckListsForReports(reqDTO);
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
											
							//start Bug #27158
							String title = qtCheckList.getTitle();
							
							if (title == null) {
								title = "";
							}
							
							title = title.replaceAll("\r\n", "<br />");
							title = title.replaceAll("<", "&#60;");
							title = title.replaceAll(">", "&#62;");
							title = title.replaceAll("\u2264", "&#60;=");//symbol less then equal
							title = title.replaceAll("\u2265", "&#62;=");//symbol greater then equal	
							
							qtCheckList.setTitle(title);
							//end Bug #27158
							
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
				Date date=null;
				if(null!=this.getManagedBean().getCurrentStatusId()){
				boolean value=isQTStatusPublished(this.getManagedBean().getCurrentStatusId());
				date=value?qt.getPublishDate():qt.getCreatedDate();
				}
				else{
					date=qt.getCreatedDate();
				}
				PmOrgProfile changedOrgprofile=retriveOrgprofileByIdAndDate(qt.getPmOrgProfile().getOrgProfileId(),date);
				log.debug("=========changed orgprofile===="+changedOrgprofile.getOrgName());
				isOneOff = true;
				for (TplSpecZoneDtl specZoneDtl : zones) {
					reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
					reqDTO.setZoneId(specZoneDtl.getSpecZoneId());
					resDTO = specSo.retrieveDeliveryAddressDtls(reqDTO);
					for(ScQtDeliveryLoc deliveryLoc:resDTO.getAddressResultBean()){
						deliveryLoc.getPmAddressType().setPmOrgProfile(changedOrgprofile);
					}
					BriefingZoneBean bean = new BriefingZoneBean(specZoneDtl,
							resDTO.getAddressResultBean());
					deliveryAddressBean.add(bean);
				}
			} else {
				// isOneOff = true;
				Date date=null;
				if(null!=this.getManagedBean().getCurrentStatusId()){
				boolean value=isQTStatusPublished(this.getManagedBean().getCurrentStatusId());
				date=value?qt.getPublishDate():qt.getCreatedDate();
				}
				else{
					date=qt.getCreatedDate();
				}
				PmOrgProfile changedOrgprofile=retriveOrgprofileByIdAndDate(qt.getPmOrgProfile().getOrgProfileId(),date);
				log.debug("=========changed orgprofile===="+changedOrgprofile.getOrgName());
				for(ScQtDeliveryLoc deliveryLoc:resDTO.getAddressResultBean()){
					//changedOrgprofile.setOrgName("manga");
					deliveryLoc.getPmAddressType().setPmOrgProfile(changedOrgprofile);
				}
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
					log.debug("startMonth : " + startMonth);
					log.debug("endMonth : " + endMonth);

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
	private void generateSpesifikasiPdfFromVelocity(ScAttachment attachment, ScQt qt) throws Exception {
		populateAttachmentDetails(attachment);
		log.debug("start generateSpesifikasiPdfFromVelocity");
		VelocityContext context = new VelocityContext();
		List<TplSpecMaster> masters = new ArrayList<TplSpecMaster>();
		IQTSpecSO specSO;
		try {
			specSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(qt.getQtId());
			reqDTO.setSpecType(ParameterConst.SPECIFICATION_TYPE_TECHNICAL);
			reqDTO.setPrepareSpec(true);
			SpecificationResDTO resDto = specSO.retrieveSpecDetails(reqDTO);
			for (ScQtCheckList scQtCheckList : resDto.getSpecResultBean().getResultList()) {
				List<TplSpecQuestion> newTplSpecQuestionsList = new ArrayList<TplSpecQuestion>();
				TplSpecMaster master = null;
				master = scQtCheckList.getScQtDoc().getTplSpecMaster();

				for (TplSpecQuestion question : master.getTplSpecQuestions()) {
					TplSpecQuestion parentQuestion = question.getParentSpecQuestion();
					if (null != master.getSpecName()) {
						String specTitle = master.getSpecName();
						specTitle = specTitle.replaceAll(QTConstants.NEW_LINE_CHARACTER, TemplateDocConst.PARAM_SPACE);
						specTitle = specTitle.replaceAll(QTConstants.AMBRA_SENT, TemplateDocConst.PARAM_SPACE);
						specTitle = specTitle.replaceAll(QTConstants.LESS_THEN, TemplateDocConst.PARAM_SPACE);
						specTitle = specTitle.replaceAll(QTConstants.GREATER_THEN, TemplateDocConst.PARAM_SPACE);
						master.setSpecName(specTitle);

					}
					if (question.getClItem() == null) {
						question.setClItem("");
					} else {
						// Bug #36702@Syazani 28/11/2024 -- Start
						String clItem = VmUtil.getInstance().splitText(question.getClItem(), QTConstants.QT_VM_SPLIT_MAXLENGTH);
						// Bug #36702@Syazani 28/11/2024 -- End
						clItem = clItem.replaceAll(QTConstants.NEW_LINE_CHARACTER, TemplateDocConst.PARAM_SPACE);
						clItem = clItem.replaceAll(QTConstants.AMBRA_SENT, TemplateDocConst.PARAM_SPACE);
						clItem = clItem.replaceAll(QTConstants.LESS_THEN, TemplateDocConst.PARAM_SPACE);
						clItem = clItem.replaceAll(QTConstants.GREATER_THEN, TemplateDocConst.PARAM_SPACE);
						question.setClItem(clItem);
					}
					if (question.getClSpecDetail() == null) {
						question.setClSpecDetail("");
					} else {
						// Bug #36702@Syazani 28/11/2024 -- Start
						String clSpecDetail = VmUtil.getInstance().splitText(question.getClSpecDetail(), QTConstants.QT_VM_SPLIT_MAXLENGTH);
						// Bug #36702@Syazani 28/11/2024 -- End
						clSpecDetail = clSpecDetail.replaceAll(QTConstants.NEW_LINE_CHARACTER, TemplateDocConst.PARAM_SPACE);
						// end Bug #28274
						clSpecDetail = clSpecDetail.replaceAll(QTConstants.AMBRA_SENT, TemplateDocConst.PARAM_SPACE);
						clSpecDetail = clSpecDetail.replaceAll(QTConstants.LESS_THEN, TemplateDocConst.PARAM_SPACE);
						clSpecDetail = clSpecDetail.replaceAll(QTConstants.GREATER_THEN, TemplateDocConst.PARAM_SPACE);
						question.setClSpecDetail(clSpecDetail);
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
		DocumentGenerator.getInstance().generatePdfFromVelocity(SourcingConstants.DOCPATH_QT_SPECIFICATION, context, new FileOutputStream(getActualFilePath(attachment)));
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
			ScAttachment attachment, ScQt qt, String prefixFileName) throws Exception {
		debugLogger("generateSamplesamplesalinankontrakPdfFromVelocity");
		
		StringBuilder fileName = new StringBuilder();
		if(prefixFileName != null){
			fileName.append(prefixFileName).append(SourcingConstants.UNDER_SCORE);
		}
		fileName.append(SourcingConstants.DOCNAME_QT_SMPLE_CONTRACT);
		attachment.setFileName(fileName.toString());

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
		String formatedFileName = scFormat.format(SourcingConstants.DOCNAME_QT_SMPLE_CONTRACT,
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

		VelocityContext context = new VelocityContext();

		FacesScopes fs = new FacesScopes();
		String hostUrl = fs.getRequest().getServerName() + ":"
				+ fs.getRequest().getServerPort();
		context.put("hostUrl", hostUrl);

		log.debug("======currentstatusid generateSampleSuratSetujuTerimaPdfFromVelocity===="+this.getManagedBean().getCurrentStatusId());
		
		Locale myLocale = new Locale(CommonConstants.LANGUAGE_CODE_MALAY, CommonConstants.DEFAULT_COUNTRY_CODE);
		
		String procurementMode = "";
		
		if(qt.getProcurementMode().getParameterCode().equals(ParameterConst.PROCUREMENT_MODE_TENDER)){
			procurementMode = ResourceBundleUtils.getMessage("lbl.rpt.drpdown.tenderLabel",ResourceBundleUtils.BUNDLE_COMMON_LABELS, myLocale);
		}else{
			procurementMode = ResourceBundleUtils.getMessage("lbl.rpt.drpdown.quotationLabel",ResourceBundleUtils.BUNDLE_COMMON_LABELS, myLocale);
		}
		
		context.put("procurementMode", procurementMode);
		context.put("qtNo", qt.getQtNo());	
		
		String title = qt.getQtTitle();
		if (title == null) {
			title = "";
		}
		
		title=title.replaceAll(QTConstants.NEW_LINE_CHARACTER,TemplateDocConst.PARAM_SPACE);
		title=title.replaceAll(QTConstants.AMBRA_SENT,TemplateDocConst.PARAM_SPACE);
		title=title.replaceAll(QTConstants.LESS_THEN,TemplateDocConst.PARAM_SPACE);
		title=title.replaceAll(QTConstants.GREATER_THEN,TemplateDocConst.PARAM_SPACE);
		context.put("title", title);

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
		title=title.replaceAll(QTConstants.NEW_LINE_CHARACTER,TemplateDocConst.PARAM_SPACE);
		title=title.replaceAll(QTConstants.AMBRA_SENT,TemplateDocConst.PARAM_SPACE);
		title=title.replaceAll(QTConstants.LESS_THEN,TemplateDocConst.PARAM_SPACE);
		title=title.replaceAll(QTConstants.GREATER_THEN,TemplateDocConst.PARAM_SPACE);

		context.put("title", title); // #32648 remove uppercase
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
		VelocityContext context = new VelocityContext();

		FacesScopes fs = new FacesScopes();
		String hostUrl = fs.getRequest().getServerName() + ":"
				+ fs.getRequest().getServerPort();
		boolean isLocLevelDistrictContainsDivision = false;	// Bug #38731@Syazani 20/08/2025
		context.put("hostUrl", hostUrl);

		context.put("commonUtils", commonUtils);
		context.put("AddressUtils", new AddressUtils());
		context.put("QTStatusConst", new QTStatusConst());

		String ministryOrgName = null;
		if (qt.getMinistryOrgProfile() != null) {
			ministryOrgName = qt.getMinistryOrgProfile().getOrgName();
		} else {
			ministryOrgName = StringUtils.EMPTY;
		}
		context.put("ministry", ministryOrgName);
		// context.put("ministry",
		// qt.getPmOrgProfile().getParentOrgProfile().getOrgName());
		log.debug("======currentstatusid generateKenyataanTawaranPembekalPdfFromVelocity===="+this.getManagedBean().getCurrentStatusId());
		String ptjName=" ";
		if(null!=this.getManagedBean().getCurrentStatusId()){
		 ptjName=retrievePTJByOrgProfileId(qt.getPmOrgProfile().getOrgProfileId(),isQTStatusPublished(this.getManagedBean().getCurrentStatusId())?qt.getPublishDate() : qt.getCreatedDate());
		}else{
			ptjName=qt.getPmOrgProfile().getOrgName();
		}
		log.debug("=======ptjName====="+ptjName);
		context.put("ptj", ptjName);
		String title = qt.getQtTitle();
		if (title == null) {
			title = "";
		}
		
		//replace all special characters
		//title = title.replaceAll("\\W", ""); 
		title=title.replaceAll(QTConstants.NEW_LINE_CHARACTER, TemplateDocConst.PARAM_SPACE);
		title=title.replaceAll(QTConstants.AMBRA_SENT, TemplateDocConst.PARAM_SPACE);
		title=title.replaceAll(QTConstants.LESS_THEN,TemplateDocConst.PARAM_SPACE);
		title=title.replaceAll(QTConstants.GREATER_THEN, TemplateDocConst.PARAM_SPACE);
		
		context.put("qtTitle", title);

		String  openType = null;
		
		if (ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER
				.equalsIgnoreCase(qt.getQtType())) {
			openType = "Tender Terhad";
		} else {
			openType = "Terbuka";
		}
		context.put("qtType", openType);

		context.put("panel", qt.isPanel());

		List<ScQtCategory> categoryResults1 = new ArrayList<ScQtCategory>();
		List<ScQtCategory> categoryResults = new ArrayList<ScQtCategory>();
		List<TplSpecLoc> locationDetails;	// Bug #38731@Syazani 20/08/2025
		IQTDetailSO QtDetailsSO;
		QuationTenderResDTO tenderResDTO;	// Bug #38731@Syazani 20/08/2025
		try {
			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			tenderReqDTO.setQtdetail(this.getManagedBean().getQt());
			tenderResDTO = QtDetailsSO.retriveQTSuppQulCriDetails(tenderReqDTO);	// Bug #38731@Syazani 20/08/2025
			categoryResults = tenderResDTO.getCategories();
			// Bug #38731@Syazani 20/08/2025 -- Start
			locationDetails = CustomUtils.getInstance().deepCopyArrayList(tenderResDTO.getLocationDetails());
			if (null != locationDetails) {
				context.put("locality", locationDetails);
				// Bug #38731@Syazani 20/08/2025 -- End
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

		// #37613 - Start
		DecimalFormat dformat = new DecimalFormat(
				commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		context.put("estimatedAmount", dformat.format(qt.getEstimatedAmount()));
		// #37613 - End

		String locationLevel = null;
		boolean isLocLevel = false;
		boolean isSubLocLevel = false;
		//20200724@asyrafhaziq - Bug #28645 - Start
		if (qt.getZoneLocationLevel() != null &&  qt.isZonal()) {
		//20200724@asyrafhaziq - Bug #28645 - End
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
					// Bug #38731@Syazani 20/08/2025 -- Start
				} else if (ParameterConst.LOCATION_LEVEL_DIVISION
						.equals(criteria.getLocationLevel().getParameterCode())) {
					isSubLocLevel = true;
					locationLevel = "Division";
				}
				// Bug #38731@Syazani 20/08/2025 -- End
				context.put("subLocLevel", isSubLocLevel);
			} else {
				context.put("subLocLevel", isSubLocLevel);
			}
			context.put("locLevel", isLocLevel);
		}
		context.put("locationLevel", locationLevel);

		IQTDetailSO qtDtlSO;
		List<TplSpecZoneDtl> specZoneDtls = null;	// Bug #38731@Syazani 20/08/2025
		try {
			qtDtlSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
			qtReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			qtReqDTO.setBdgDtlsReq(true);
			QuationTenderResDTO qtResDTO = new QuationTenderResDTO();
			qtResDTO = qtDtlSO.retriveQTDetails(qtReqDTO);
			ScQt scQt = qtResDTO.getQtDetail();
			if (this.getManagedBean().getQt().isZonal()) {
				if (scQt.getScQtSupplierCriteria() != null &&
						scQt.getScQtSupplierCriteria().getQtZoneCriterias() != null) {
					specZoneDtls = CustomUtils.getInstance().deepCopyArrayList(scQt.getScQtSupplierCriteria().getQtZoneCriterias().getTplSpecZoneDtls());	// Bug #38731@Syazani 20/08/2025
					context.put("zoneLocality", specZoneDtls);
				}
			}
		} finally {
			QtDetailsSO = null;
		}
		
		// Bug #38731@Syazani 20/08/2025 -- Start
		if (locationLevel.equalsIgnoreCase("District")) {
			isLocLevelDistrictContainsDivision = isLocationContainsDivision(specZoneDtls, locationDetails, context);
		}
		context.put("isLocLevelDistrictContainsDivision", isLocLevelDistrictContainsDivision);
		// Bug #38731@Syazani 20/08/2025 -- End

		context.put("bsvReq", qt.isBsvRequired());
		boolean isZone = false;
		List<BriefingZoneBean> zoneBeans = new ArrayList<BriefingZoneBean>();
		if (qt.isBsvRequired()) {
			QTDetailBackingBean backingBean = new QTDetailBackingBean();
			backingBean.retrieveBriefingSitevisitTabDtl();
			// Bug #36702@Syazani 28/11/2024 -- Start
			List<BriefingZoneBean> briefingZoneBean = CustomUtils.getInstance().deepCopyArrayList(
					this.getManagedBean().getBriefingZoneBean());
			if (!CollectionUtils.isEmpty(briefingZoneBean)) {
				isZone = true;
				for (BriefingZoneBean zoneBean : briefingZoneBean) {
					splitBriefingZoneBeanText(zoneBean);
					// Bug #36702@Syazani 28/11/2024 -- End
					zoneBeans.add(zoneBean);
				}
			} else {
				// Bug #36702@Syazani 28/11/2024 -- Start
				BriefingZoneBean bsvDtlBean = new BriefingZoneBean(this.getManagedBean()
						.getBsvDtlBean());
				splitBriefingZoneBeanText(bsvDtlBean);
				// Bug #36702@Syazani 28/11/2024 -- End
				zoneBeans.add(bsvDtlBean);
			}
			context.put("isZone", isZone);
			context.put("zoneBeans", zoneBeans);
		}

		boolean isOneOff = false;
		String fulfilmentType = qt.getFulfilmentType() != null ? qt
				.getFulfilmentType().getParameterCode() : null;
		if (ParameterConst.FULFILMENT_TYPE_ONE_OFF.equals(fulfilmentType)) {
			isOneOff = true;
			context.put("fulfilmentType", isOneOff);
		} else if (ParameterConst.FULFILMENT_TYPE_SCHEDULED
				.equals(fulfilmentType)
				|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
						.equals(fulfilmentType)) {

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
	 * Get the readOnly.
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * Set the readOnly.
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
			ScQtDocReportAttachment	attachment = generateDocumentAttachment(scQtDocReports.get(i),getPrefixFileName((i+1)));
			scQtDocReports.get(i).setScQtDocReportAttachment(attachment);
			//scQtDocReports.get(i).setScQtDocReportAttachment(
					//generateDocumentAttachment(scQtDocReports.get(i),getPrefixFileName((i + 1))));
			
		}

	}

	/**
	 * Download all reports.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void reGenerateDocuments(Long qtId, String... qtFields) {
		debugLogger("reGenerateDocuments-->qtId: " + qtId + ", qtFields: " + StringUtils.join(qtFields, "|"));
		
		IQTDocumentSO qtDocumentSO;
		try {
			qtDocumentSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDocumentSO.class);
			
			QTDocumentReqDTO quationTenderReqDTO = new QTDocumentReqDTO();
			quationTenderReqDTO.setQtId(qtId);
			quationTenderReqDTO.setQtFields(qtFields);
			List<ScReport> reports = qtDocumentSO.retriveRegenDocumentListByQTField(
					quationTenderReqDTO).getScReports();

			SupplierFinalizationBackingBean finalizationBackingBean = new SupplierFinalizationBackingBean();
			SupplierFinalizationManagedBean finalizationManagedBean = finalizationBackingBean.getManagedBean();
			AttachmentDateFormat attachmentDateFormat = null;
			for (int i = 0; i < reports.size(); i++) {
				ScReport report = reports.get(i);
				try {
					if (ParameterConst.DOCUMENT_CATEGORY_QUOTATION_TENDER_DOCUMENTS
							.equalsIgnoreCase(report.getDocCategory())) {
						if (report instanceof ScQtDocReport) {
							ScQtDocReport qtDocReport = (ScQtDocReport)report;
							ScQtDocReportAttachment qtDocReportAttachment = qtDocReport.getScQtDocReportAttachment();
							
							// rename the original file before regenerate the documents.
							if (attachmentDateFormat == null) {
								attachmentDateFormat = new AttachmentDateFormat();
							}
							backupExistingFile(qtDocReportAttachment, attachmentDateFormat);
							
							qtDocReportAttachment = generateDocumentAttachment(qtDocReport, 
									qtDocReportAttachment.getFileName().substring(0, 2));
							qtDocReport.setScQtDocReportAttachment(qtDocReportAttachment);
						}
						
					} else if (ParameterConst.DOCUMENT_CATEGORY_BRIEFING_PAPER
							.equalsIgnoreCase(report.getDocCategory())) {
						if (report instanceof ScQtFinalizationReport) {
							ScQtFinalizationReport finalizationReport = (ScQtFinalizationReport)report;
							finalizationManagedBean.setScQtFinalization(finalizationReport.getScQtFinalization());
							finalizationManagedBean.setScQt(finalizationReport.getScQtFinalization().getScQt());
							
							ScQtFinalizationReportAttachment reportAttachment = finalizationBackingBean
									.generateDocumentAttachment(finalizationReport, true);	
							finalizationReport.setScQtFinalizationReportAttachment(reportAttachment);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					throw new EpException(e.getMessage());
				}
			}
			
			quationTenderReqDTO.setScReports(reports);
			qtDocumentSO.saveReportAndAttachment(quationTenderReqDTO);
			
		} finally {
			qtDocumentSO = null;
		}
		
	}
	
	private void backupExistingFile(ScAttachment attachment, AttachmentDateFormat attachmentDateFormat) {
		if (attachment != null) {
			String oldFilePath = getActualFilePath(attachment);
			File oldFile = new File(oldFilePath);
			String fileName = attachment.getFileName();
			String newFilePath = oldFilePath.replaceAll(
					fileName, 
					attachmentDateFormat.format(fileName, null));
			
			oldFile.renameTo(new File(newFilePath));	
		}
	}
	
	private String getPrefixFileName(int increment) {
		
		return (String) (increment <= 9 ? "0" + (increment) : Integer
				.toString(increment));
	}
	
	private PmOrgProfile retriveOrgprofileByIdAndDate(Long orgProfileId,
			Date date) {
		IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(orgProfileId);
		reqDTO.setCreateDate(date);
		PmOrgProfile orgProfile = profileSO.retrieveOrgProfileByProfileId(
				reqDTO).getPmOrgProfile();
		return orgProfile;

	}

	private void retriveTemplateDocParams(
			TemplateDocBackingBean templateDocBackingBean, ScQt scQt) {

		IAddressSO addressSO = null;
		PmAddressType pmAddressType = null;
		IUserSO userSO = null;
		try {
			addressSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			AddressReqDTO reqDTO = new AddressReqDTO();
			PmOrgProfile pmOrgProfile = scQt.getOwnerOrgProfile();
			reqDTO.setOrgProfileId(pmOrgProfile.getOrgProfileId());
			reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if (resDTO.getAddressTypeList().size() > 0) {
				for(PmAddressType addressType: resDTO.getAddressTypeList()){
					if(addressType.isRecordStatus()){
						pmAddressType = addressType;
						break;
					}
					
				}
			}
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			

			// Get Desk Officer User
			UserReqDTO userreqDTO = new UserReqDTO();
			userreqDTO.setUserId(scQt.getCreatedBy());
			PmUser doUser = userSO.retrieveUserByUserId(userreqDTO).getPmUser();
			String agencyAddress = "";
			if (pmAddressType != null) {
				PmAddress pmAddress = pmAddressType.getPmAddress();
				agencyAddress = getFullAddress(pmAddress);
				agencyAddress = agencyAddress.replaceAll(
						TemplateDocConst.PARAM_NEW_LINE_CHARACTER,
						TemplateDocConst.PARAM_HTML_NEW_LINE_CHARACTER);
				//agencyPhoneNo = pmAddress.getPhoneNo();
				//agencyFaxNo = pmAddress.getFaxNo();
			}

			//FacesScopes facesScopes = new FacesScopes();
			//String officerName = facesScopes.getUserSessionBean().getUserName();
			log.debug("======currentstatusid generateSyaratSyaratAmFromVelocity===="
					+ this.getManagedBean().getCurrentStatusId());
			String ptjName = " ";
			if (null != this.getManagedBean().getCurrentStatusId()) {
				ptjName = retrievePTJByOrgProfileId(scQt.getPmOrgProfile()
						.getOrgProfileId(),
						isQTStatusPublished(this.getManagedBean()
								.getCurrentStatusId()) ? scQt.getPublishDate()
								: scQt.getCreatedDate());
			} else {
				ptjName = scQt.getPmOrgProfile().getOrgName();
			}
			log.debug("=======ptjName=====" + ptjName);
			Map<String, String> docParamValues = templateDocBackingBean
					.getManagedBean().getDocParamValues();
			if (null == docParamValues) {
				docParamValues = new HashMap<String, String>();
			}
			docParamValues.put(TemplateDocConst.PARAM_AGENCY_NAME, ptjName);
			docParamValues.put(TemplateDocConst.PARAM_AGENCY_ADDRESS,
					agencyAddress);
			//20200921@asyrafhaziq - 29076 - Start
			docParamValues
					.put(TemplateDocConst.PARAM_OFFICER_NAME, doUser!=null && doUser.getUserName()!=null  ?doUser.getUserName() :" ");
			docParamValues.put(TemplateDocConst.PARAM_TELEPHONE_NUM,
					doUser!=null && doUser.getPhoneNo()!=null ?doUser.getPhoneNo() :" ");
			docParamValues.put(TemplateDocConst.PARAM_AGENCY_EMAIL, doUser!=null && doUser.getEmail()!=null ?doUser.getEmail() :" ");
			// docParamValues.put(TemplateDocConst.COMPANY_NAME, ptjName);
			docParamValues.put(TemplateDocConst.PARAM_FAX_NUM, doUser!=null && doUser.getFaxNo()!=null ?doUser.getFaxNo() :" ");
			//20200921@asyrafhaziq - 29076 - End	
			docParamValues.put(
					TemplateDocConst.PARAM_VALIDITY_PERIOD,
					scQt.getQtValidityPeriod() != null ? Integer.toString(scQt
							.getQtValidityPeriod()) : "");
			docParamValues.put(TemplateDocConst.PARAM_DEPARTMENT_NAME, ptjName);
			BigDecimal contarctAmount = returnContractAmount();
			docParamValues.put(TemplateDocConst.PARAM_CONTRACT_AMOUNT,
					contarctAmount.toString());
			docParamValues.put(TemplateDocConst.PARAM_QUOTATION_TENDER, getParameterDesc(scQt.getProcurementMode(), myLocale));
			if(ParameterConst.FULFILMENT_TYPE_ONE_OFF.equals(scQt.getFulfilmentType().getParameterCode())){
				docParamValues.put(TemplateDocConst.PARAM_FULFILMENT_TYPE, getParameterDesc(scQt.getFulfilmentType(), myLocale));
				docParamValues.put(TemplateDocConst.PARAM_FULFILMENT_CONTENT, TemplateDocConst.PARAM_FULFILMENT_ONE_OFF_DATA);
			}else{
				docParamValues.put(TemplateDocConst.PARAM_FULFILMENT_TYPE, getParameterDesc(scQt.getFulfilmentType(), myLocale));
				docParamValues.put(TemplateDocConst.PARAM_FULFILMENT_CONTENT, TemplateDocConst.PARAM_FULFILMENT_PERIODIC_DATA);
			}
			docParamValues.put(TemplateDocConst.PARAM_CONTRACT_PERIOD, scQt.getContractDuration() != null ? Integer.toString(scQt
					.getContractDuration()) : "");
			docParamValues.put(TemplateDocConst.PARAM_MINISTRY_NAME, retrieveMinistryByOrgProfileId(scQt.getPmOrgProfile()
						.getOrgProfileId()));
			QTDetailBackingBean detailBean=new QTDetailBackingBean();
			detailBean.retrieveDeductionPaymentTabDtl();
			if(CollectionUtils.isNotEmpty(this.getManagedBean().getScQtDeductionList())){
				StringBuilder paymentDeductions=new StringBuilder();
				//paymentDeductions.append(TemplateDocConst.PARAM_PAYMENT_DEDUCTION__APPLIABLE) //#34873
				paymentDeductions.append(TemplateDocConst.PARAM_HTML_NEW_LINE_CHARACTER) //#34873
				.append("<table>");
				for(int i=0;i<this.getManagedBean().getScQtDeductionList().size();i++){
				ScQtDeduction deduction=this.getManagedBean().getScQtDeductionList().get(i);
				int value=i+1;
				paymentDeductions.append("<tr><td>")
				.append("("+value +")"+getParameterDesc(deduction.getDeductionType(), myLocale))
				.append("</td></tr>");
				for(ScQtDeductionClause clause:deduction.getScQtDeductionClauses()){
					paymentDeductions.append("<tr><td>")
					.append(clause.getClauseDesc())
					.append("</td></tr>");
				}
				}
				paymentDeductions.append("</table>");
				log.debug("PaymentDeductionString"+ paymentDeductions.toString());
				docParamValues.put(TemplateDocConst.PARAM_PAYMENT_DEDUCTION, paymentDeductions.toString());	
			}else{
				docParamValues.put(TemplateDocConst.PARAM_PAYMENT_DEDUCTION, TemplateDocConst.PARAM_PAYMENT_DEDUCTION_NOT_APPLIABLE);	
			}
			   
			// docParamValues.put(TemplateDocConst.PARAM_BREAK_LINE,
			// TemplateDocConst.PARAM_SPACE);
			// docParamValues.put(TemplateDocConst.PARAM_PREFIX_POSTFIX,
			// TemplateDocConst.PARAM_SPACE);
			PmConfigRuleValue configRuleValue = calculateBondPercentage();
			if (null != configRuleValue) {
				log.debug("configRuleValue.getFromValue()"
						+ configRuleValue.getFromValue());
				log.debug("configRuleValue.getToValue()"
						+ configRuleValue.getToValue());
				docParamValues
						.put(TemplateDocConst.PARAM_PERFORMENCE_BOND_FROM,
								configRuleValue.getFromValue() != null ? configRuleValue
										.getFromValue().toString()
										: TemplateDocConst.PARAM_PERFORMENCE_BOND_FROM_AMOUNT);
				docParamValues
						.put(TemplateDocConst.PARAM_PERFORMENCE_BOND_TO,
								configRuleValue.getToValue() != null ? configRuleValue
										.getToValue().toString()
										: TemplateDocConst.PARAM_PERFORMENCE_BOND_TO_AMOUNT);
				docParamValues
						.put(TemplateDocConst.PARAM_PERFORMENCE_BOND_MAX,
								configRuleValue.getToValue() != null ? configRuleValue
										.getToValue().toString()
										: TemplateDocConst.PARAM_PERFORMENCE_BOND_TO_AMOUNT);
			}
			templateDocBackingBean.getManagedBean().setDocParamValues(
					docParamValues);
			/*
			 * html = html.replaceAll("&lt;&lt;Agency Name&gt;&gt;", ptjName);
			 * html = html.replaceAll("&lt;&lt;Agency name&gt;&gt;", ptjName);
			 * html = html.replaceAll("&lt;&lt;Department Name&gt;&gt;",
			 * ptjName); html =
			 * html.replaceAll("&lt;&lt;Agency Address&gt;&gt;", agencyAddress);
			 * html = html.replaceAll("&lt;&lt;Officer Name&gt;&gt;",
			 * officerName); html = html.replaceAll("&lt;&lt;Tel No&gt;&gt;",
			 * agencyPhoneNo); html = html.replaceAll("&lt;&lt;Faks No&gt;&gt;",
			 * agencyFaxNo); html =
			 * html.replaceAll("&lt;&lt;Validity Period&gt;&gt;",
			 * scQt.getQtValidityPeriod() != null ?
			 * Integer.toString(scQt.getQtValidityPeriod()) : "");
			 */
		} finally {
			addressSO = null;
			userSO = null;
		}
	}

	private BigDecimal returnContractAmount() {
		log.debug("start returnContractAmount");
		IRuleSO ruleSO = null;
		BigDecimal contractAmount = new BigDecimal(0);
		try {
			ruleSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);

			RuleReqDTO reqDTO = new RuleReqDTO();
			reqDTO.setRuleType(RuleTypeConst.ONE_TIER_EVALUATION_THRESHOLD);

			List<PmConfigRuleValue> configRuleValues = ruleSO
					.getRuleValuesByTypeInCurrentDate(reqDTO)
					.getRuleValueList();
			if (configRuleValues != null && configRuleValues.size() > 0) {
				PmConfigRuleValue configRuleValue = configRuleValues.get(0);

				if (configRuleValue != null
						&& configRuleValue.getRuleValue() != null) {
					contractAmount = new BigDecimal(
							configRuleValue.getRuleValue());
				}
			} else {
				contractAmount = new BigDecimal(
						TemplateDocConst.PARAM_CONTRACT_AMOUNT_VALUE);
			}
			log.debug("contractAmount" + contractAmount);
			return contractAmount;

		} finally {
			ruleSO = null;
		}
	}

	/**
	 * Calculate the performance bond percentage
	 * 
	 * @param amountForBondPercentage
	 *            amount for performance bond percentage
	 * 
	 * @return BigDecimal bondPercentage
	 */
	private PmConfigRuleValue calculateBondPercentage() {
		log.debug("start calculateBondPercentage");
		IRuleSO ruleSO = null;
		PmConfigRuleValue configRuleValue = null;
		try {
			ruleSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);

			RuleReqDTO reqDTO = new RuleReqDTO();
			reqDTO.setRuleType(RuleTypeConst.BOND_PERCENTAGE);

			List<PmConfigRuleValue> configRuleValues = ruleSO
					.getRuleValuesByTypeInCurrentDate(reqDTO)
					.getRuleValueList();
			if (configRuleValues != null && configRuleValues.size() > 0) {
				configRuleValue = configRuleValues.get(0);

			} else {
				configRuleValue = new PmConfigRuleValue();
				configRuleValue.setFromValue(new BigDecimal(
						TemplateDocConst.PARAM_PERFORMENCE_BOND_FROM_AMOUNT));
				configRuleValue.setToValue(new BigDecimal(
						TemplateDocConst.PARAM_PERFORMENCE_BOND_TO_AMOUNT));
			}

		} finally {
			ruleSO = null;
		}
		return configRuleValue;
	}
	
	private TplDocMaster cloneDocMasterDocSectionDetails(TplDocMaster templateDocMaster,ScQtDocReport scQtDocReport){
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
				Map<TplDocSection, TplDocSection> originalToCloneMap = new HashMap<>(); //#34873
				List<TplDocSection> docSectionCOllection = templateDocMaster
						.getTplDocSectionCollection();
				for (TplDocSection tplDocSection : docSectionCOllection) {
					TplDocSection section = (TplDocSection) tplDocSection.cloneEntity();
					//log.info("===doc section name"+section.getSectionTitle()+"section content"+section.getSectionContent());
					if(TemplateDocConst.PARAM_DOC_SECTION_HEADER_TITLE.equalsIgnoreCase(section.getSectionTitle())){
						String title=TemplateDocConst.PARAM_DOC_SECTION_HEADER_TITLE+TemplateDocConst.PARAM_SPACE+getParameterDesc(this.getManagedBean().getQt().getProcurementMode(), myLocale).toUpperCase();
						section.setSectionName(title);
						section.setSectionTitle(title);
					}
					section.setDocSectionId(null);
					//#34873
					//section.setParent(null); // Temporarily am setting null, need to
												// confirm once
					section.setTplDocMaster(tplDocMaster);

					setUserDetails(section);
					tplDocMaster.getTplDocSectionCollection().add(section);
					originalToCloneMap.put(tplDocSection, section); //#34873
				}
				
				//#34873 - start
				for (TplDocSection section : tplDocMaster.getTplDocSectionCollection()) {
			        TplDocSection originalParent = section.getParent();
			        if (originalParent != null) {
			            TplDocSection cloneParent = originalToCloneMap.get(originalParent);
			            section.setParent(cloneParent);
			        }
			    }
				//#34873 - end

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
				tplDocMaster.setPmStatus(getPmStatusByStatusId(QTStatusConst.DOC_TEMPLATE_DEFAULT));

				tplDocMaster.setScReport(scQtDocReport);
				
				return tplDocMaster;
	}
	
	// Bug #36702@Syazani 28/11/2024 -- Start
	private void splitBriefingZoneBeanText(BriefingZoneBean zoneBean) {
		List<ScQtBsvDtl> briefingLst = zoneBean.getBriefingList();
		ScAddress scAddress;
		if (!CollectionUtils.isEmpty(briefingLst)) {
			for (ScQtBsvDtl sqbd : briefingLst) {
				sqbd.setBsvDesc(VmUtil.getInstance().splitText(sqbd.getBsvDesc(), QTConstants.QT_VM_SPLIT_MAXLENGTH));
				if (null != sqbd.getAddress()) {
					scAddress = sqbd.getAddress();
					scAddress.setAddress1(VmUtil.getInstance().splitText(scAddress.getAddress1(),
							QTConstants.QT_VM_SPLIT_MAXLENGTH));
					scAddress.setAddress2(VmUtil.getInstance().splitText(scAddress.getAddress2(),
							QTConstants.QT_VM_SPLIT_MAXLENGTH));
					scAddress.setAddress3(VmUtil.getInstance().splitText(scAddress.getAddress3(),
							QTConstants.QT_VM_SPLIT_MAXLENGTH));
				}
			}
		}
	}
	// Bug #36702@Syazani 28/11/2024 -- End
	
	// Bug #38731@Syazani 20/08/2025 -- Start
	private boolean isLocationContainsDivision(List<TplSpecZoneDtl> specZoneDtls, List<TplSpecLoc> locationDetails,
			VelocityContext context) {
		boolean isLocationContainsDivision = false;
		if (!CollectionUtils.isEmpty(specZoneDtls)) {
			for (TplSpecZoneDtl tplSpecZoneDtl : specZoneDtls) {
				if (!CollectionUtils.isEmpty(tplSpecZoneDtl.getTplSpecZoneLocs()) && !isLocationContainsDivision) {
					tplSpecZoneDtl.setTplSpecZoneLocs(removeDuplicatebyDivisionIDbyTplSpecZoneLoc(tplSpecZoneDtl
							.getTplSpecZoneLocs()));
					isLocationContainsDivision = loopFindisLocationContainsPmDivision(tplSpecZoneDtl);
				} else if (isLocationContainsDivision) {
					break;
				}
			}
		}

		if (!CollectionUtils.isEmpty(locationDetails)) {
			List<TplSpecLoc> locationDetailsNew = removeDuplicatebyDivisionIDbyTplSpecLoc(locationDetails);
			context.put("locality", locationDetailsNew);
			for (TplSpecLoc tplSpecLoc : locationDetailsNew) {
				if (null != tplSpecLoc.getPmDivision()) {
					isLocationContainsDivision = true;
					break;
				}
			}
		}

		return isLocationContainsDivision;
	}

	private boolean loopFindisLocationContainsPmDivision(TplSpecZoneDtl tplSpecZoneDtl) {
		for (TplSpecZoneLoc tplSpecZoneLoc : tplSpecZoneDtl.getTplSpecZoneLocs()) {
			if (null != tplSpecZoneLoc.getPmDivision()) {
				return true;
			}
		}
		return false;
	}

	private List<TplSpecZoneLoc> removeDuplicatebyDivisionIDbyTplSpecZoneLoc(List<TplSpecZoneLoc> tplSpecZoneLocLst) {
		Map<String, TplSpecZoneLoc> map = new LinkedHashMap<>();
		for (TplSpecZoneLoc tplSpecZoneLoc : tplSpecZoneLocLst) {
			if (null != tplSpecZoneLoc.getPmDivision()) {
				if (!map.containsKey("D" + tplSpecZoneLoc.getPmDivision().getDivisionId())) {
					TplSpecZoneLoc tplSpecZoneLocNew = tplSpecZoneLoc;
					List<PmDistrict> pmDistrictLst = new ArrayList<>();
					pmDistrictLst.add(tplSpecZoneLoc.getPmDistrict());
					tplSpecZoneLoc.getPmDivision().setPmDistricts(pmDistrictLst);
					map.put("D" + tplSpecZoneLoc.getPmDivision().getDivisionId(), tplSpecZoneLocNew);
				} else {
					TplSpecZoneLoc tplSpecZoneLocNew = map.get("D" + tplSpecZoneLoc.getPmDivision().getDivisionId());
					List<PmDistrict> pmDistrictLst = tplSpecZoneLocNew.getPmDivision().getPmDistricts();
					pmDistrictLst.add(tplSpecZoneLoc.getPmDistrict());
					tplSpecZoneLoc.getPmDivision().setPmDistricts(pmDistrictLst);
					map.put("D" + tplSpecZoneLoc.getPmDivision().getDivisionId(), tplSpecZoneLocNew);
				}
			} else {
				map.put("S" + tplSpecZoneLoc.getSpecLocId(), tplSpecZoneLoc);
			}
		}

		return new ArrayList<>(map.values());
	}

	private List<TplSpecLoc> removeDuplicatebyDivisionIDbyTplSpecLoc(List<TplSpecLoc> tplSpecLocLst) {
		Map<String, TplSpecLoc> map = new LinkedHashMap<>();
		for (TplSpecLoc tplSpecLoc : tplSpecLocLst) {
			if (null != tplSpecLoc.getPmDivision()) {
				if (!map.containsKey("D" + tplSpecLoc.getPmDivision().getDivisionId())) {
					TplSpecLoc tplSpecLocNew = tplSpecLoc;
					List<PmDistrict> pmDistrictLst = new ArrayList<>();
					pmDistrictLst.add(tplSpecLoc.getPmDistrict());
					tplSpecLoc.getPmDivision().setPmDistricts(pmDistrictLst);
					map.put("D" + tplSpecLoc.getPmDivision().getDivisionId(), tplSpecLocNew);
				} else {
					TplSpecLoc tplSpecLocNew = map.get("D" + tplSpecLoc.getPmDivision().getDivisionId());
					List<PmDistrict> pmDistrictLst = tplSpecLocNew.getPmDivision().getPmDistricts();
					pmDistrictLst.add(tplSpecLoc.getPmDistrict());
					tplSpecLoc.getPmDivision().setPmDistricts(pmDistrictLst);
					map.put("D" + tplSpecLoc.getPmDivision().getDivisionId(), tplSpecLocNew);
				}
			} else {
				map.put("S" + tplSpecLoc.getSpecLocId(), tplSpecLoc);
			}
		}

		return new ArrayList<>(map.values());
	}
	// Bug #38731@Syazani 20/08/2025 -- End

}
