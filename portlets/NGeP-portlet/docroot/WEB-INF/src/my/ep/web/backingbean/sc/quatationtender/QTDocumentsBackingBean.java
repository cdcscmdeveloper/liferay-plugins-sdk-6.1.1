package my.ep.web.backingbean.sc.quatationtender;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.biz.service.tpl.doc.ITemplateDocSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
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
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.persistence.entity.tpl.doc.TplDocSection;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.web.backingbean.tpl.doc.TemplateDocBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 * @author SudheerKumar
 * 
 */
@ManagedBean(name = "qTDocumentsBackingBean")
@RequestScoped
public class QTDocumentsBackingBean extends SourcingBaseBackingBean<QTDetailManagedBean> {

	private static final Logger log = EpLogger.getLogger(QTDocumentsBackingBean.class);

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

		public DocumentPagingDataModel(String componentId, QtnTenderSearchBean cloneDocumentSearchBean) {
			super(componentId);
			this.cloneDocumentSearchBean = cloneDocumentSearchBean;
		}
		
		@Override
		public ISearchResultBean<ScQt> pageChangeListener(ISearchBean<ScQt> searchBean) {
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
	private void populateCloneQTSearchCriteria(QtnTenderSearchBean cloneDocumentSearchBean, ISearchBean<ScQt> qtSearchBean) {
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
		//scQtDocReport.setScQtDocReportAttachment(new ScQtDocReportAttachment());
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
		this.getManagedBean().getCloneDocumentSearchBean().setQtTitle(null);
		this.getManagedBean().getCloneDocumentSearchBean().setQtNo(null);
		this.getManagedBean().getCloneDocumentSearchBean().setProcurementMode(null);
		this.getManagedBean().getCloneDocumentSearchBean().setFromCreatedDate(null);
		this.getManagedBean().getCloneDocumentSearchBean().setToCreatedDate(null);
		this.getManagedBean().getCloneDocumentSearchBean().setQtStatus(null);
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
				
				tplDocMaster.setDocTitle(scQtDocReport.getTitle());
				tplDocMaster.setDocName(scQtDocReport.getTitle());
				// Procurement Category
				tplDocMaster.setProcurementCategory(scQtDocReport.getScQt().getPmProcurementCategory());
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
				// Status
				PmStatus status = new PmStatus();
				status.setStatusId(10001l);
				tplDocMaster.setPmStatus(status);
				
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
				//getManagedBean().setScQtDocReportList(qtTemplateResDTO.getReports());
				for (ScQtDocReport docReport : qtTemplateResDTO.getReports()) {
					getManagedBean().setScQtDocReport(docReport);
				}
				TemplateDocReqDTO tmplateReqDTO1 = new TemplateDocReqDTO();
				Long masterId = getManagedBean().getScQtDocReport().getTplDocMaster().getDocMasterId();
				log.info("Master Id : " + masterId);
				tmplateReqDTO1.setDocMasterId(masterId);
				TemplateDocResDTO templateResDTO1 = templateSO.getTemplateMaster(tmplateReqDTO1);
				TplDocMaster docMaster = templateResDTO1.getTplDocMaster();
				
				templateDocUrl = tplDocBB.goToMaint(docMaster);
			} else {
				templateDocUrl = tplDocBB.goToMaint(scQtDocReport.getTplDocMaster()); // Need to Change
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return templateDocUrl;
	}

	public String documentBackAction() {
		debugLogger("documentBackAction");
		return "/xhtml/sc/quatationtender/quatationtendercreation.xhtml?faces-redirect=true";
	}
	
	public void downloadReport(ScQtDocReport scQtDocReport) throws IOException {
		debugLogger("downloadReport");
		ScQtDocReportAttachment scQtDocReportAttachment = scQtDocReport.getScQtDocReportAttachment();
		if (scQtDocReportAttachment == null) {
			scQtDocReportAttachment = generateDocumentAttachment(scQtDocReport);
		}
		downloadFile(scQtDocReportAttachment);
	}
	
	public void downloadAllReports() throws IOException {
		debugLogger("downloadAllReports");
		List<ScQtDocReport> scQtDocReports = getManagedBean().getScQtDocReportList();
		List<ScQtDocReportAttachment> scAttachments = new ArrayList<ScQtDocReportAttachment>();
		for (ScQtDocReport scQtDocReport : scQtDocReports) {
			ScQtDocReportAttachment attachment = scQtDocReport.getScQtDocReportAttachment();
			if(attachment == null) {
				attachment = generateDocumentAttachment(scQtDocReport);
			}
			scAttachments.add(attachment);
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
				
				if (SourcingConstants.DOC_DEF_KEY_SUPP_OFFER_STATEMENT.equals(docDefinitionKey)) {
					generateKenyataanTawaranPembekalPdfFromVelocity((ScAttachment)scQtDocReportAttachment, getManagedBean().getQt());
				} else if (SourcingConstants.DOC_DEF_KEY_RULES_REGULATION.equals(docDefinitionKey)) {
					
				} else if (SourcingConstants.DOC_DEF_KEY_CONTRACT_TERM.equals(docDefinitionKey)) {
					
				} else if (SourcingConstants.DOC_DEF_KEY_SPECIFICATION.equals(docDefinitionKey)) {
					generateSpesifikasiPdfFromVelocity((ScAttachment)scQtDocReportAttachment, getManagedBean().getQt());
				} else if (SourcingConstants.DOC_DEF_KEY_TECH_COMPLIANCE_CHECKLIST.equals(docDefinitionKey)) {
					generateSenaraiSemakUntukPematuhanTeknikalPdfFromVelocity((ScAttachment)scQtDocReportAttachment, getManagedBean().getQt());
				} else if (SourcingConstants.DOC_DEF_KEY_SAMPLE_COMPLIANCE_CHECKLIST.equals(docDefinitionKey)) {
					
				} else if (SourcingConstants.DOC_DEF_KEY_FIN_COMPLIANCE_CHECKLIST.equals(docDefinitionKey)) {
					generateSenaraiSemakUntukPematuhanKewanganPdfFromVelocity((ScAttachment)scQtDocReportAttachment, getManagedBean().getQt());
				} else if (SourcingConstants.DOC_DEF_KEY_LOA_SAMPLE.equals(docDefinitionKey)) {
					generateSampleSuratSetujuTerimaPdfFromVelocity((ScAttachment)scQtDocReportAttachment, getManagedBean().getQt());
				} else if (SourcingConstants.DOC_DEF_KEY_CONTRACT_COPY_SAMPLE.equals(docDefinitionKey)) {
					// TODO put attachment in repository folder
					//generateSamplesamplesalinankontrakPdfFromVelocity((ScAttachment)scQtDocReportAttachment, getManagedBean().getQt());
				} else if (SourcingConstants.DOC_DEF_KEY_DECLARION_SAMPLE.equals(docDefinitionKey)) {
					generateSampleSuratPdfFromVelocity((ScAttachment)scQtDocReportAttachment, getManagedBean().getQt());
				} else if (SourcingConstants.DOC_DEF_KEY_FREQUENCY_SCHEDULE.equals(docDefinitionKey)) {
					// Frequency Schedule  - Jadual Kekerapan
					generateFrequencySchedulePdfFromVelocity((ScAttachment)scQtDocReportAttachment, getManagedBean().getQt());
				} else if (SourcingConstants.DOC_DEF_KEY_PROGRESSIVE_PAYMENT.equals(docDefinitionKey)) {
					// Progressive Payment - As and When, Jadual Pembayaran Berperingkat
					generateProgressivePaymentPdfFromVelocity((ScAttachment)scQtDocReportAttachment, getManagedBean().getQt());
				} else if (SourcingConstants.DOC_DEF_KEY_DELIVERY_SERVICE_ADDRESS.equals(docDefinitionKey)) {
					generateDeliveryAddressPdfFromVelocity((ScAttachment)scQtDocReportAttachment, getManagedBean().getQt());
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			}
			scQtDocReport.setScQtDocReportAttachment(scQtDocReportAttachment);
		}
		return scQtDocReportAttachment;
	}

	/**
	 * @param scQtDocReportAttachment
	 * @param qt
	 */
	private void generateSenaraiSemakUntukPematuhanKewanganPdfFromVelocity(ScAttachment attachment, ScQt qt) throws Exception {
		attachment.setFileName("Senarai_Semak_Untuk_Pematuhan_Kewangan.pdf");
		populateAttachmentDetails(attachment);
		
		VelocityContext context = new VelocityContext();
		
		List<ScQtCheckList> uploadFinanceSpecList = new ArrayList<ScQtCheckList>();
		List<ScQtCheckList> financeSpecList = new ArrayList<ScQtCheckList>();
		IQTSpecSO specSO;
		try {
			specSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(qt.getQtId());
			reqDTO.setSpecType(QTConstants.FINANCIAL_DOC_TYPE);
			SpecificationResDTO resDto = specSO.retrieveCheckLists(reqDTO);
			financeSpecList = resDto.getSpecResultBean().getResultList();
		} finally {
			specSO = null;
		}
		if (uploadFinanceSpecList != null) {
			for (ScQtCheckList checkList : financeSpecList) {
				if (checkList.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD)) {
					uploadFinanceSpecList.add(checkList);
				}
			}
		}
		context.put("uploadFinanceSpecList", uploadFinanceSpecList);
		
		DocumentGenerator.getInstance().generatePdfFromVelocity("/sc/qtDoc/qtDoc-senarai-semak-untuk-pematuhan-Kewangan.vm", 
    			context, new FileOutputStream(attachment.getFilePath()));
	}

	/**
	 * @param scQtDocReportAttachment
	 * @param qt
	 */
	private void generateSenaraiSemakUntukPematuhanTeknikalPdfFromVelocity(ScAttachment attachment, ScQt qt) throws Exception {
		attachment.setFileName("Senarai_Semak_Untuk_Pematuhan_Teknikal.pdf");
		populateAttachmentDetails(attachment);
		
		VelocityContext context = new VelocityContext();
		
		List<ScQtCheckList> uploadTechSpecList = new ArrayList<ScQtCheckList>();
		List<ScQtCheckList> techSpecList = new ArrayList<ScQtCheckList>();
		IQTSpecSO specSO;
		try {
			specSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(qt.getQtId());
			reqDTO.setSpecType(QTConstants.TECHNICAL_DOC_TYPE);
			SpecificationResDTO resDto = specSO.retrieveCheckLists(reqDTO);
			techSpecList = resDto.getSpecResultBean().getResultList();
		} finally {
			specSO = null;
		}
		if (uploadTechSpecList != null) {
			for (ScQtCheckList checkList : techSpecList) {
				if (checkList.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD)) {
					uploadTechSpecList.add(checkList);
				}
			}
		}
		context.put("uploadTechSpecList", uploadTechSpecList);
		
		DocumentGenerator.getInstance().generatePdfFromVelocity("/sc/qtDoc/qtDoc-senarai-semak-untuk-pematuhan-teknikal.vm", 
    			context, new FileOutputStream(attachment.getFilePath()));
	}

	/**
	 * @param scQtDocReportAttachment
	 * @param qt
	 */
	private void generateDeliveryAddressPdfFromVelocity(ScAttachment attachment, ScQt qt) throws Exception {
		attachment.setFileName("Delivery_Address.pdf");
		populateAttachmentDetails(attachment);
		
		VelocityContext context = new VelocityContext();
		
		boolean isOneOff = false;
		IQTSpecSO specSo;
		List<BriefingZoneBean> deliveryAddressBean = new ArrayList<BriefingZoneBean>();
		try {
			specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(qt.getQtId());
			SpecificationResDTO resDTO = specSo.retrieveDeliverSeriviceAddress(reqDTO);
			List<TplSpecZoneDtl> zones = resDTO.getSpecZoneDtls();
			if ( zones != null && !zones.isEmpty()) {
				isOneOff = true;
				for (TplSpecZoneDtl specZoneDtl : zones) {
					reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
					reqDTO.setZoneId(specZoneDtl.getSpecZoneId());
					resDTO = specSo.retrieveDeliveryAddressDtls(reqDTO);
					BriefingZoneBean bean = new BriefingZoneBean(specZoneDtl, resDTO.getAddressResultBean());
					deliveryAddressBean.add(bean);
				}
			} else {
				//isOneOff = true;
				BriefingZoneBean bean = new BriefingZoneBean(null, resDTO.getAddressResultBean());
				deliveryAddressBean.add(bean);
			}
		} finally {
			specSo = null;
		}
		context.put("isOneOff", isOneOff);
		context.put("deliveryAddressBean", deliveryAddressBean);
		
		DocumentGenerator.getInstance().generatePdfFromVelocity("/sc/qtDoc/qtDoc-delivery-address.vm", 
    			context, new FileOutputStream(attachment.getFilePath()));
	}
	
	/**
	 * @param scQtDocReportAttachment
	 * @param qt
	 */
	private void generateFrequencySchedulePdfFromVelocity(ScAttachment attachment, ScQt qt) throws Exception {
		attachment.setFileName("Frequency_Schedule.pdf");
		populateAttachmentDetails(attachment);
		
		VelocityContext context = new VelocityContext();
		
		List<ScQtSchedule> scheduleList = new ArrayList<ScQtSchedule>();
		IQTSpecSO specSo;
		SpecificationResDTO resDTO = null;
		try {
			specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(qt.getQtId());
			resDTO = specSo.retrieveFulFilMentScheduleDtls(reqDTO);
			scheduleList = resDTO.getScheduleResults().getResultList();	
		} finally {
			specSo = null;
		}
		int totalFrequency = 0;
		if (scheduleList != null) {
			totalFrequency = scheduleList.size();
		}
		context.put("totalFrequency", totalFrequency);
		context.put("scheduleList", scheduleList);
		
		DocumentGenerator.getInstance().generatePdfFromVelocity("/sc/qtDoc/qtDoc-frequency-schedule.vm", 
    			context, new FileOutputStream(attachment.getFilePath()));
	}

	/**
	 * @param scQtDocReportAttachment
	 * @param qt
	 */
	private void generateProgressivePaymentPdfFromVelocity(ScAttachment attachment, ScQt qt) throws Exception {
		attachment.setFileName("Progressive_Payment.pdf");
		populateAttachmentDetails(attachment);
		
		VelocityContext context = new VelocityContext();
		
		List<ScQtProgressivePayment> paymentList = new ArrayList<ScQtProgressivePayment>();
		IQTSpecSO specSo;
		SpecificationResDTO resDTO = null;
		try {
			specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(qt.getQtId());
			resDTO = specSo.retrieveFulFilMentScheduleDtls(reqDTO);
			paymentList = resDTO.getProgressivePaymentResults().getResultList();
		} finally {
			specSo = null;
		}
		int totalPhase = 0;
		if (paymentList != null) {
			totalPhase = paymentList.size();
		}
		context.put("totalPhase", totalPhase);
		context.put("paymentList", paymentList);
		
		DocumentGenerator.getInstance().generatePdfFromVelocity("/sc/qtDoc/qtDoc-progressive-payment.vm", 
    			context, new FileOutputStream(attachment.getFilePath()));
	}

	/**
	 * @param scQtDocReportAttachment
	 * @param qt
	 */
	private void generateSpesifikasiPdfFromVelocity(ScAttachment attachment, ScQt qt) throws Exception {
		attachment.setFileName("Spesifikasi.pdf");
		populateAttachmentDetails(attachment);
		
		VelocityContext context = new VelocityContext();
		
		List<TplSpecQuestion> tplSpecQuestionsList = new ArrayList<TplSpecQuestion>();
		List<ScQtCheckList> techSpecList = new ArrayList<ScQtCheckList>();
		IQTSpecSO specSO;
		try {
			specSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(qt.getQtId());
			reqDTO.setSpecType(QTConstants.TECHNICAL_SPEC_TYPE);
			SpecificationResDTO resDto = specSO.retrieveSpecDetails(reqDTO);
			techSpecList = resDto.getSpecResultBean().getResultList();
		} finally {
			specSO = null;
		}
		ScQtDoc qtDoc = new ScQtDoc();
		for (ScQtCheckList scQtCheckList : techSpecList) {
			qtDoc = scQtCheckList.getScQtDoc();
		}
		TplSpecMaster master = qtDoc.getTplSpecMaster();
		
		context.put("title", master.getSpecName());
		
		StringBuilder itemTypes = new StringBuilder();
    	if (itemTypes != null) {
    		itemTypes.append(getParameterDesc(master.getItemTypeCode()));
    	}
    	context.put("itemType", itemTypes);
    	
    	StringBuilder localGoods = new StringBuilder();
    	if (localGoods != null) {
    		localGoods.append(getParameterDesc(master.getItemOriginCode()));
    	}
		context.put("localGoods", localGoods);
		
		if (tplSpecQuestionsList != null) {
			tplSpecQuestionsList = master.getTplSpecQuestions();
		}
		context.put("tplSpecQuestionsList", tplSpecQuestionsList);
		
		DocumentGenerator.getInstance().generatePdfFromVelocity("/sc/qtDoc/qtDoc-spesifikasi.vm", 
    			context, new FileOutputStream(attachment.getFilePath()));
	}

	/*private void generateSamplesamplesalinankontrakPdfFromVelocity(ScAttachment attachment, ScQt qt) throws Exception {
		attachment.setFileName("Sample_Surat_Setuju_Terima.pdf");
		populateAttachmentDetails(attachment);
		
		VelocityContext context = new VelocityContext();
		
		context.put("agency", "");
		
		DocumentGenerator.getInstance().generatePdfFromVelocity("/sc/qtDoc/qtDoc-sample-surat-setuju-terima.vm", 
    			context, new FileOutputStream(attachment.getFilePath()));
	}*/

	/**
	 * @param scQtDocReportAttachment
	 * @param qt
	 */
	private void generateSampleSuratSetujuTerimaPdfFromVelocity(ScAttachment attachment, ScQt qt) throws Exception {
		attachment.setFileName("Sample_Surat_Setuju_Terima.pdf");
		populateAttachmentDetails(attachment);
		
		PmOrgProfile pmOrgProfile = qt.getPmOrgProfile();
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat amountDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
    	VelocityContext context = new VelocityContext();
    	
    	IAddressSO addressSO = null;
    	PmAddressType pmAddressType = null;
		try {
			addressSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
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
    	context.put("title", qt.getQtTitle());
    	
    	String estimatedPrice = "";
    	if (qt.getEstimatedAmount() != null) {
    		estimatedPrice = amountDF.format(qt.getEstimatedAmount());	
    	}
    	context.put("estimatedPrice", estimatedPrice);

    	DocumentGenerator.getInstance().generatePdfFromVelocity("/sc/qtDoc/qtDoc-sample-surat-setuju-terima.vm", 
    			context, new FileOutputStream(attachment.getFilePath()));
	}

	private void generateSampleSuratPdfFromVelocity(ScAttachment attachment, ScQt qt) throws Exception {
		attachment.setFileName("Sample_Surat_Akuan_Pembida.pdf");
		populateAttachmentDetails(attachment);
		
		//CommonUtils commonUtils = new CommonUtils();
    	VelocityContext context = new VelocityContext();
    	
    	context.put("title", qt.getQtTitle());
    	context.put("qtNo", qt.getQtNo());

    	DocumentGenerator.getInstance().generatePdfFromVelocity("/sc/qtDoc/qtDoc-sample-surat-akuan-pembida.vm", 
    			context, new FileOutputStream(attachment.getFilePath()));
	}

	private void generateKenyataanTawaranPembekalPdfFromVelocity(ScAttachment attachment, ScQt qt) throws Exception {
		attachment.setFileName("Kenyataan_Tawaran_Pembekal.pdf");
		populateAttachmentDetails(attachment);
		
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
    	VelocityContext context = new VelocityContext();
		
    	context.put("ministry", qt.getPmOrgProfile().getParentOrgProfile().getOrgName());
    	context.put("agency", qt.getPmOrgProfile().getOrgName());
    	context.put("qtTitle", qt.getQtTitle());
    	
    	StringBuilder openType = new StringBuilder();
    	if (openType != null) {
    		openType.append(getParameterDesc(ParameterTypeConst.QUOTATION_TENDER_TYPE, qt.getQtType()));
    	}
    	context.put("qtType", openType);

    	List<ScQtCategory> categoryResults = new ArrayList<ScQtCategory>();
    	IQTDetailSO QtDetailsSO;
		try {
			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			tenderReqDTO.setQtdetail(this.getManagedBean().getQt());
			QuationTenderResDTO tenderResDTO = QtDetailsSO.retriveQTSuppQulCriDetails(tenderReqDTO);
			categoryResults = tenderResDTO.getCategoryResultBean().getResultList();
		} finally {
			QtDetailsSO = null;
		}
		context.put("categoryResults", categoryResults);
		
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
    	
    	boolean isZone = false;
       	IQTBriefingSO qTBriefingSO = null;
    	ScQtBsv qtBsv = null;
    	BriefingZoneBean briefingZoneBean = null;
    	List<BriefingZoneBean> zoneBeans = new ArrayList<BriefingZoneBean>();
		try {
			qTBriefingSO = (IQTBriefingSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			BriefingResDTO resDTO = qTBriefingSO.retrieveBriefingSiteVisitDtl(reqDTO);
			ScQt scQt = resDTO.getScQt();
			if (scQt.isZonal()) {
				List<TplSpecZoneDtl> zones = resDTO.getZonesList();
				if (null != zones) {
					isZone = true;
					BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
					briefingReqDTO.setQtId(getManagedBean().getQt().getQtId());
					for (TplSpecZoneDtl specZoneDtl : zones) {
						briefingReqDTO.setSpecZoneId(specZoneDtl.getSpecZoneId());
						BriefingResDTO briefingResDTO = qTBriefingSO.retrieveBriefingSiteVisitDtls(briefingReqDTO);
						ScQtBsv scQtBsv = briefingResDTO.getScQtBsv();
						briefingZoneBean = new BriefingZoneBean(specZoneDtl, briefingResDTO.getBriefingList(), briefingResDTO.getSiteVisitList(), scQtBsv);
						zoneBeans.add(briefingZoneBean);
					}
				}
			} else {
				qtBsv = resDTO.getScQtBsv();
				briefingZoneBean = new BriefingZoneBean(null, resDTO.getBriefingList(), resDTO.getSiteVisitList(), qtBsv);
				zoneBeans.add(briefingZoneBean);
			}
		} finally {
			qTBriefingSO = null;
		}
		context.put("isZone", isZone);
		context.put("zoneBeans", zoneBeans);

		boolean isOneOff = false;
    	if (qt.getFulfilmentType().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
    		isOneOff = true;
    		context.put("fulfilmentType", isOneOff);
    	} else if (qt.getFulfilmentType().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_SCHEDULED) || qt.getFulfilmentType().getParameterCode().equals(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN)) {
    		isOneOff = true;
    		context.put("fulfilmentType", isOneOff);
    	}
    	
    	DocumentGenerator.getInstance().generatePdfFromVelocity("/sc/qtDoc/qtDoc-kenyataan-tawaran-pembekal.vm", 
    			context, new FileOutputStream(attachment.getFilePath()));
	}

	private IParameterSO getParameterSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}
}
