package my.ep.web.backingbean.sc.consolidation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.sc.SupplierQualificationBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.dto.sc.quationtender.EvaluationReqDTO;
import my.ep.dto.sc.quationtender.EvaluationResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.NoticeBoardReqDTO;
import my.ep.dto.sc.quationtender.NoticeBoardResDTO;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.ProposalResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleItem;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleLable;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.ConsolidationReportManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;


/**
 * @author Praneeth
 * 
 * @version 1.1
 * @since 14/01/2016
 */
@ManagedBean(name="consolidationReportBackingBean")
@RequestScoped
public class ConsolidationReportBackingBean extends 
					SourcingBaseBackingBean<ConsolidationReportManagedBean> {

	private static final Logger log = EpLogger
			.getLogger(ConsolidationReportBackingBean.class);
	
	private final static int BRIEFING_PAPER_TAB_INDEX = 0;
	private final static int PACKAGE_TYPE_TAB_INDEX = 1;
	private final static int ITEMIZED_TYPE_TAB_INDEX = 2;
	
	/** Default constructor */
	public ConsolidationReportBackingBean() {}
	
	
	/** Public Methods **/
	public void preRenderView() {
		try {
			SCEvaluationDataType dataType 
					= getBpmTaskDetails(SCEvaluationDataType.class, null);
			Long qtId = Long.valueOf(dataType.getQtId());
			getManagedBean().setQtId(qtId);
			getManagedBean().setCommitteeType(
						dataType.getCommitteeType());
			getManagedBean().setPayLoad(dataType);
			getManagedBean().setActiveIndex(0);
			
			loadInitialData();
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							QTConstants.DO_CONSOLIDATION_REPORT_PAGE);
		} catch (Exception e) {
			handleException(e);
		}
	}
	
	/**
	 * To navigate to the selected tab
	 * 
	 * @param tabIndex
	 *            the index of the selected tab
	 */
	public void onTabMenuChangeAction(int tabIndex) {
		switch (tabIndex) {
			case 0:
				navigateToBriefingPaperTab();
				break;
			case 1:
				navigateToPackagewiseTab();
				break;
			case 2:
				navigateToItemizedTab();
				break;
		}
	}	
	
	/**
	 * To navigate to the selected zone tab, under Supplier Finalization tab
	 * 
	 * @param zoneTabIndex
	 *            the selected zone tab index
	 */
	public void onZoneTabChangeAction() {
		Integer selectedZoneTabIndex = Integer.valueOf(FacesContext
				.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("selectedZoneTabIndex"));
		if (selectedZoneTabIndex == 
				getManagedBean().getZoneActiveIndex()) {
			return;
		}
		
		List<ScQtEvaluationResultDtl> qualifiedSuppliers = null;
		TplSpecZoneDtl selectedZone = getManagedBean().getZoneDetails()
													  .get(selectedZoneTabIndex);
		getManagedBean().setSelectedZone(selectedZone);
		getManagedBean().setZoneActiveIndex(selectedZoneTabIndex);
		
		Long selectedZoneId = selectedZone.getSpecZoneId();
		
		if (!getManagedBean().isItemized()) {
			Map<Long, List<ScQtEvaluationResultDtl>> qualifiedSupppliersByZone 
								= getManagedBean().getQualifiedSupppliersByZone();
			
			if (MapUtils.isNotEmpty(qualifiedSupppliersByZone)) {
				if (qualifiedSupppliersByZone
						.containsKey(selectedZone.getSpecZoneId())) {
					qualifiedSuppliers = qualifiedSupppliersByZone
												  .get(selectedZoneId);
				} else {
					qualifiedSuppliers = null;
				}
			} else {
				qualifiedSuppliers = null;
			}
			if (qualifiedSuppliers != null) {
				getManagedBean().setMaxRank(
						String.valueOf(qualifiedSuppliers.size()));		
			}
			getManagedBean().setQualifiedSuppliers(qualifiedSuppliers);
		} else {
			populateScQtRecommendsByZone();
			getManagedBean().setQualifiedSuppliers(qualifiedSuppliers);
		}
	}	
	
	/**
	 * Method to retrieve Board of Directors Details pop up in Consolidation of Report
	 */
	public void showBoardofDirectors(SmSupplier selectedSupplier) {
		debugLogger("showBoardofDirectors");

		if (selectedSupplier == null) {
			return;
		}
		log.info("Supplier Name :: " + selectedSupplier.getCompanyName());

		List<SmPersonnel> boardofDirectors = null;
		IPersonnelSO personnelSO = null;
		try {
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setSmSupplierId(selectedSupplier.getSupplierId());
			
			personnelSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
							IPersonnelSO.class);
			PersonnelResDTO resDTO = personnelSO
					.retrieveBordOfDirectors(reqDTO);
			if (resDTO.isSuccessful()) {
				boardofDirectors = resDTO.getSmPersonnelList();
				if (CollectionUtils.isNotEmpty(boardofDirectors)) {
					for (SmPersonnel smPersonnel : boardofDirectors) {
						SmMofAccount smMofAccount = retriveMofAccountBySupplier(
								smPersonnel.getSmAppl().getSmSupplier());
						smPersonnel.getSmAppl().getSmSupplier()
								   .setSmMofAccount(smMofAccount);
					}
				}
			}
		} finally {
			personnelSO = null;
		}

		getManagedBean().setBoardofDirectors(boardofDirectors);
	}	
	
	/**
	 * Event to select an item where Recommend Type is Itemized under Supplier
	 * Finalization tab
	 * 
	 * @param scQtRecommend
	 *            the scQtRecommend to set
	 */
	public void selectItemEvent(SelectEvent event) {
		debugLogger("selectItemEvent");

		ScQtRecommend scQtRecommend = (ScQtRecommend) event.getObject();	
		List<ScQtEvaluationResultDtl> qualifiedSuppliers 
						= scQtRecommend.getShortListedSupplierList();
		if (CollectionUtils.isEmpty(qualifiedSuppliers)) {
			SuppFinalizationResDTO resDTO 
					= retrieveQualifiedSuppliersByItem(scQtRecommend);
			qualifiedSuppliers = resDTO.getQualifiedByItemSuppliers();
			scQtRecommend.setShortListedSupplierList(qualifiedSuppliers);
		}
		if (qualifiedSuppliers != null) {
			getManagedBean().setMaxRank(
					String.valueOf(qualifiedSuppliers.size()));
		}
		getManagedBean().setQualifiedSuppliers(qualifiedSuppliers);
	}
	
	/**
	 * Add ScQtFinalizationReport
	 */
	public void addFinalizationReportAction() {
		debugLogger("addScQtFinalizationReport");

		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		
		List<ScQtFinalizationReport> scQtFinalizationReports 
				= getManagedBean().getScQtFinalization().getScQtFinalizationReports();	
		if (scQtFinalizationReports == null) {
			scQtFinalizationReports = new ArrayList<ScQtFinalizationReport>();
			scQtFinalization.setScQtFinalizationReports(
									scQtFinalizationReports);
		}

		ScQtFinalizationReport report = new ScQtFinalizationReport();
		report.setScQtFinalization(scQtFinalization);
		report.setScQtFinalizationReportAttachment(
					new ScQtFinalizationReportAttachment());

		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
			reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
			ParameterResDTO resDTO = parameterSO
					.retrieveParameterByTypeAndCode(reqDTO);
			report.setDefinationType(resDTO.getPmParameter());
			report.setDocCategory(
					ParameterConst.DOCUMENT_CATEGORY_BRIEFING_PAPER); // NR
		} finally {
			parameterSO = null;
		}

		scQtFinalizationReports.add(report);
	}
	
	/**
	 * Delete ScQtFinalizationReport
	 */
	public void deleteFinalizationReportAction() {
		ScQtFinalization scQtFinalization 
					= getManagedBean().getScQtFinalization();
		List<ScQtFinalizationReport> selectedFinalizationReports
					= getManagedBean().getSelectedFinalizationReports();

		if (CollectionUtils.isNotEmpty(selectedFinalizationReports)) {
			deleteRowsAction(scQtFinalization.getScQtFinalizationReports(), 
							 selectedFinalizationReports);
		} else {
			throw new EpException(MessageConst.ERROR_760158);
		}
	}
	
	/**
	 * Handle file upload.
	 * 
	 * @param event
	 *            the event
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		debugLogger("handleFileUpload");
		if (event.getFile() != null) {
			// String attachmentDocType =
			// getManagedBean().getAttachmentDocType();
			ScAttachment scAttachment = null;
			ScQtFinalizationReport scQtFinalizationReport 
					= this.getManagedBean().getSelectedFinalizationReport();
			ScQtFinalizationReportAttachment reportAttachment 
					= scQtFinalizationReport.getScQtFinalizationReportAttachment();
			if (null == reportAttachment) {
				reportAttachment = new ScQtFinalizationReportAttachment();
				reportAttachment.setScQtFinalizationReport(scQtFinalizationReport);
				scQtFinalizationReport.setScQtFinalizationReportAttachment(reportAttachment);
			}
			reportAttachment.setScQtFinalizationReport(scQtFinalizationReport);
			scAttachment = reportAttachment;

			uploadAttachment(event.getFile(), scAttachment);
		}
	}	
	
	/**
	 * To download a report under Briefing Paper/Kertas Taklimat tab
	 * 
	 * @param scQtFinalizationReport
	 *            selected report which would be downloaded
	 * @throws Exception
	 *             the exception that would be thrown in case of any error while
	 *             downloading the report
	 */
	public void downloadReport(ScQtFinalizationReport scQtFinalizationReport)
			throws Exception {
		debugLogger("downloadReport");
		
		ScQtFinalizationReportAttachment reportAttachment 
				= scQtFinalizationReport.getScQtFinalizationReportAttachment();
		boolean isEvaluationReport = false;
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
				.equals(scQtFinalizationReport.getDefinationType().getParameterCode())) {
			String docDefinitionKey = scQtFinalizationReport.getPmDocument()
											.getDocDefinitionKey();
			String docType = null;
			if (SourcingConstants.DOC_DEF_KEY_EVALUATION_REPORT
								.equals(docDefinitionKey)) { 
				isEvaluationReport = true;
				docType = QTConstants.EVALUATION_REPORT;
			} else if (SourcingConstants.DOC_DEF_KEY_OPENNING_REPORT
								.equals(docDefinitionKey)) {
				isEvaluationReport = true;
				docType = QTConstants.OPENING_COMMITTEE_REPORT;
			} else if (SourcingConstants.DOC_DEF_KEY_TECHNICAL_EVAALUATION_REPORT
								.equals(docDefinitionKey)) {
				isEvaluationReport = true;
				docType = QTConstants.TECHNICAL_EVALUATION_REPORT;
			} else if (SourcingConstants.DOC_DEF_KEY_FINANCIAL_EVAALUATION_REPORT
								.equals(docDefinitionKey)) {
				isEvaluationReport = true;
				docType = QTConstants.FINANCIAL_EVALUATION_REPORT;
			}
			
			if (reportAttachment != null && isEvaluationReport) {
				reportAttachment.setDocType(docType);
				String filePath = reportAttachment.getFilePath();
				if (StringUtils.isNotBlank(filePath)) {
					reportAttachment.setFilePath(filePath.replaceAll("KTR", docType));
				}
			}
		}		
		
		if ((QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION
				.equals(getManagedBean().getCurrentStatusId()) || 
					QTStatusConst.SF_ESCALATION_TO_BPK
						.equals(getManagedBean().getCurrentStatusId()) || 
							reportAttachment == null) && !isEvaluationReport) {
			reportAttachment = generateDocumentAttachment(scQtFinalizationReport);
			scQtFinalizationReport
					.setScQtFinalizationReportAttachment(reportAttachment);
		}
		downloadFile(reportAttachment);
	}

	/**
	 * To download all reports under Briefing Paper/Kertas Taklimat tab
	 * 
	 * @throws Exception
	 *             the exception that would be thrown in case of any error while
	 *             downloading the report(s)
	 */
	public void downloadAllReports() throws Exception {
		debugLogger("downloadAllReports");

		List<ScQtFinalizationReport> scQtFinalizationReports = getManagedBean()
				.getScQtFinalization().getScQtFinalizationReports();
		List<ScQtFinalizationReportAttachment> scAttachments = new ArrayList<ScQtFinalizationReportAttachment>();

		for (ScQtFinalizationReport report : scQtFinalizationReports) {
			ScQtFinalizationReportAttachment attachment = report
					.getScQtFinalizationReportAttachment();
			boolean isEvaluationReport = false;
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
					.equals(report.getDefinationType().getParameterCode())) {
				String docDefinitionKey = report.getPmDocument()
												.getDocDefinitionKey();
				String docType = null;
				if (SourcingConstants.DOC_DEF_KEY_EVALUATION_REPORT
						.equals(docDefinitionKey)) { 
					isEvaluationReport = true;
					docType = QTConstants.EVALUATION_REPORT;
				} else if (SourcingConstants.DOC_DEF_KEY_OPENNING_REPORT
									.equals(docDefinitionKey)) {
					isEvaluationReport = true;
					docType = QTConstants.OPENING_COMMITTEE_REPORT;
				} else if (SourcingConstants.DOC_DEF_KEY_TECHNICAL_EVAALUATION_REPORT
									.equals(docDefinitionKey)) {
					isEvaluationReport = true;
					docType = QTConstants.TECHNICAL_EVALUATION_REPORT;
				} else if (SourcingConstants.DOC_DEF_KEY_FINANCIAL_EVAALUATION_REPORT
									.equals(docDefinitionKey)) {
					isEvaluationReport = true;
					docType = QTConstants.FINANCIAL_EVALUATION_REPORT;
				}
				
				if (attachment != null && isEvaluationReport) {
					attachment.setDocType(docType);
					String filePath = attachment.getFilePath();
					if (StringUtils.isNotBlank(filePath)) {
						attachment.setFilePath(filePath.replaceAll("KTR", docType));
					}
				}
			}
			if ((QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION
					.equals(getManagedBean().getCurrentStatusId()) ||
						QTStatusConst.SF_ESCALATION_TO_BPK
							.equals(getManagedBean().getCurrentStatusId()) ||
								attachment == null) && !isEvaluationReport) {
				attachment = generateDocumentAttachment(report);
				report.setScQtFinalizationReportAttachment(attachment);
			}
			scAttachments.add(attachment);
		}
		
		// Generate all the reports before submission
		if (!getManagedBean().isSubmit()) {
			downloadAllFiles("NGeP-Finalization-Report", scAttachments);
		}
	}	

	public void saveAction() {
		// Validate Desk officer ranking
		validateConsolidationReport();
		
		SuppFinalizationReqDTO reqDTO = prepareDataToSave();
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		
		try {
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			SuppFinalizationResDTO resDTO 
					= qtSuppFinalizationSO.saveConsolidationReport(reqDTO);
			
			if (resDTO.isSuccessful()) {
				renewSessionAndRetrieve(
						getManagedBean().getActiveIndex());
				
				String qtNo = getManagedBean().getScQt().getQtNo().toString();
				String desc = getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION);
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(MessageConst.INFO_700004,
										new String[] { desc, qtNo },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	public void submitAction() {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		SuppFinalizationReqDTO reqDTO = prepareDataToSave();
		
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);

			// BPM Params
			reqDTO.setToken((new FacesScopes()).getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setOutcome(getManagedBean().getOutcome());
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			
			SuppFinalizationResDTO resDTO 
					= qtSuppFinalizationSO.submitConsolidationReport(reqDTO);
			if (resDTO.isSuccessful()) {
				String variable1 = getParameterDesc(
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION)
						+ " " + getManagedBean().getScQt().getQtNo();
				String variable2 = getMessageCommon("lbl.sc.consolidation.report");

				commonMessageAcknowledmentPage(MessageConst.INFO_700026,
						variable1, variable2);
			}			
			
		} finally {
			qtSuppFinalizationSO = null;
		}		
	}
	
	/** End of Public Methods **/
	
	
	/** Private Methods **/
	
	private void loadInitialData() {
		retrieveQTDetails();
		retrieveSupplierFinalizationDtls();
		retrieveBriefingPaper();
	}
	
	private void setZoneAndTabIndexOnException(boolean isItemized, TplSpecZoneDtl zoneDtl) {
		List<TplSpecZoneDtl> specZoneDtls 
					= this.getManagedBean().getZoneDetails();
		if (zoneDtl != null && 
				CollectionUtils.isNotEmpty(specZoneDtls)) {
			zoneDtl = getManagedBean().getZoneMap().get(zoneDtl.getSpecZoneId());
			getManagedBean().setSelectedZone(zoneDtl);
			getManagedBean().setZoneActiveIndex(
						zoneDtl.getZoneTabIndex());
		}
		if (isItemized) {
			navigateToItemizedTab();
		} else {
			navigateToPackagewiseTab();
		}
	}	
	
	private void renewSessionAndRetrieve(int activeIndex) {
		Long qtId = getManagedBean().getScQt().getQtId();
		String taskId = getManagedBean().getTaskId();
		String taskName = getManagedBean().getTaskName();
		SCEvaluationDataType dataType = (SCEvaluationDataType) 
							getManagedBean().getPayLoad();
		String committeeType = getManagedBean().getCommitteeType();
		Long currentStatusId = getManagedBean().getCurrentStatusId();
		String[] outcomes = getManagedBean().getOutComes();
		int zoneActiveIndex = getManagedBean().getZoneActiveIndex();
		TplSpecZoneDtl selectedZone = getManagedBean().getSelectedZone();

		renewSesionObject();

		getManagedBean().setQtId(qtId);
		getManagedBean().setSelectedZone(selectedZone);
		getManagedBean().setZoneActiveIndex(zoneActiveIndex);
		getManagedBean().setTaskId(taskId);
		getManagedBean().setTaskName(taskName);
		getManagedBean().setOutComes(outcomes);
		getManagedBean().setPayLoad(dataType);
		getManagedBean().setCommitteeType(committeeType);
		getManagedBean().setCurrentStatusId(currentStatusId);
		getManagedBean().setZoneActiveIndex(
				selectedZone != null ? selectedZone.getZoneTabIndex() : 0);

		loadInitialData();

		switch (activeIndex) {
			case 0:
				navigateToBriefingPaperTab();
				break;
			case 1:
				navigateToItemizedTab();
				break;
			case 2:
				navigateToPackagewiseTab();
		}
	}	
	
	/**
	 * Loads ScQt details corresponding to the selected quotation/tender and
	 * sets the data in the managed bean associated with the current user
	 * session
	 * 
	 * @param qtId
	 *            The id of the quotation/tender to be loaded
	 */
	private void retrieveQTDetails() {
		Long qtId = getManagedBean().getQtId();
		IQTSuppFinalizationSO qtSuppFinalizationSO;
		
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(qtId);

			SuppFinalizationResDTO resDTO 
						= qtSuppFinalizationSO.retrieveQTDtls(reqDTO);
			ScQt scQt = resDTO.getScQt();
			populateParentOrgProfile(scQt);
			getManagedBean().setScQt(scQt);

			List<TplSpecZoneDtl> zoneDetails = resDTO.getZoneDetails();
			getManagedBean().setZoneDetails(zoneDetails);
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	private void retrieveSupplierFinalizationDtls() {
		ScQt scQt = getManagedBean().getScQt();
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			boolean isZonal = scQt.isZonal();
			PmUser user = new FacesScopes().getUserSessionBean();
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setScQt(scQt);
			reqDTO.setLoggedInUserId(user.getUserId());

			List<TplSpecZoneDtl> zoneDetails 
						= getManagedBean().getZoneDetails();
			reqDTO.setZoneDetails(zoneDetails);

			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.retrieveSupplierFinalizationDtls(reqDTO);
			ScQtFinalization scQtFinalization = resDTO.getScQtFinalization();

			if (CollectionUtils.isNotEmpty(zoneDetails)
					&& getManagedBean().getSelectedZone() == null) {
				getManagedBean().setSelectedZone(zoneDetails.get(0));
			}

			if (scQtFinalization != null) {
				getManagedBean().setScQtFinalization(scQtFinalization);
				
				List<ScQtEvaluationResultDtl> toBeSavedSuppliers 
							= new ArrayList<ScQtEvaluationResultDtl>();
				if (isZonal) {
					Map<Long, List<ScQtEvaluationResultDtl>> 
							qualifiedSupppliersByZone = resDTO.getQualifiedByPackageMap();
					getManagedBean().setQualifiedSupppliersByZone(
										qualifiedSupppliersByZone);
					if (MapUtils.isNotEmpty(qualifiedSupppliersByZone)) {
						for (List<ScQtEvaluationResultDtl> suppliers
									: qualifiedSupppliersByZone.values()) {
							if (CollectionUtils.isNotEmpty(suppliers)) {
								toBeSavedSuppliers.addAll(suppliers);
							}
						}
					}
				} else {
					List<ScQtEvaluationResultDtl> shortlistedPackageList 
								= resDTO.getQualifiedByPackageSuppliers();
					getManagedBean().setPackageQualifiedSuppliers(
												shortlistedPackageList);
					if (CollectionUtils.isNotEmpty(shortlistedPackageList)) {
						toBeSavedSuppliers.addAll(shortlistedPackageList);
					}
				}
				getManagedBean().setToBeSavedQualifiedSuppliers(toBeSavedSuppliers);
			}
			
			if (scQt.isZonal()) {
				createZoneMenuModel();
				populateScQtRecommendsByZone();
			} else {
				getManagedBean().setItemsList(
						scQtFinalization.getScQtRecommends());
			}		
			
			initializeRemarks();
		} finally {
			qtSuppFinalizationSO = null;
		}
	}	
	
	/**
	 * To retrieve the document list in PMDocument for Briefing Paper/Kertas
	 * Taklimat
	 */
	private void retrieveBriefingPaper() {
		IQTSuppFinalizationSO qtSuppFinalizationSO;
		
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			Long qtId = getManagedBean().getScQt().getQtId();

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			ScQtFinalization scQtFinalization = this.getManagedBean()
					.getScQtFinalization();
			if (scQtFinalization == null) {
				scQtFinalization = new ScQtFinalization();
			}
			reqDTO.setQtId(qtId);
			reqDTO.setScQtFinalization(scQtFinalization);

			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.retrieveFinalizationReports(reqDTO);
			ScQtFinalization _scQtFinalization = resDTO.getScQtFinalization();
			
			List<ScQtFinalizationReport> finalizationReports 
								= _scQtFinalization.getScQtFinalizationReports();
			if (CollectionUtils.isNotEmpty(
								finalizationReports)) {
				for (ScQtFinalizationReport report 
									: finalizationReports) {
					String docDefinitionKey = report.getPmDocument().getDocDefinitionKey();					
					if (SourcingConstants.DOC_DEF_KEY_COVER
											.equals(docDefinitionKey)) {
						finalizationReports.remove(report);
						break;
					}
				}
			}

			scQtFinalization.setScQtFinalizationReports(finalizationReports);
			if (finalizationReports != null) {
				this.getManagedBean().setFinalizationReports(finalizationReports);
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}	
	
	private SuppFinalizationResDTO retrieveQualifiedSuppliersByItem(
											ScQtRecommend scQtRecommend) {
		SuppFinalizationResDTO resDTO = new SuppFinalizationResDTO();
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(getManagedBean().getScQt().getQtId());
			reqDTO.setScQtFinalization(getManagedBean().getScQtFinalization());
			
			if (scQtRecommend.getTplSpecZoneDtl() != null) {
				reqDTO.setQtZoneCriteriaId(scQtRecommend
						.getTplSpecZoneDtl().getSpecZoneId());
			}
			reqDTO.setRequestItemId(scQtRecommend
					.getRequestItem().getRequestNoteItemId());
			reqDTO.setRecommendType(
					SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM);
			
			resDTO = qtSuppFinalizationSO.retrieveEvaluationResultsByItem(reqDTO);
			if (CollectionUtils.isNotEmpty(
					resDTO.getQualifiedByItemSuppliers())) {
				getManagedBean().getToBeSavedQualifiedSuppliers()
								.addAll(resDTO.getQualifiedByItemSuppliers());
			}			
		} finally {
			qtSuppFinalizationSO = null;
		}

		return resDTO;
	}	
	
	private void createZoneMenuModel() {
		List<TplSpecZoneDtl> zoneDtls 
					= getManagedBean().getZoneDetails();
		if (CollectionUtils.isNotEmpty(zoneDtls)) {
			Map<Long,TplSpecZoneDtl> zoneMap = new HashMap<Long,TplSpecZoneDtl>();
			
			// Dynamically generating the tabs
			MenuModel zoneMenuModel = new DefaultMenuModel();
			if (CollectionUtils.isNotEmpty(zoneDtls)) {
				int zoneTabIndex = 0;
				for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
					specZoneDtl.setZoneTabIndex(zoneTabIndex);

					MenuItem zoneItem = new MenuItem();
					zoneItem.setValue(specZoneDtl.getZoneName().toUpperCase());
					//zoneItem.setAjax(true);
					zoneItem.setTransient(true);
					zoneItem.setPartialSubmit(true);
					zoneItem.setProcess("@this");
					zoneItem.setUpdate("@this");
					zoneItem.setOnstart("blockWidget.show();");
					zoneItem.setOncomplete("onZoneTabChangeAction([ {name : 'selectedZoneTabIndex', " +
							"value : " + zoneTabIndex + "}]); blockWidget.hide();");
					zoneMenuModel.addMenuItem(zoneItem);
					++zoneTabIndex;
					zoneMap.put(specZoneDtl.getSpecZoneId(), specZoneDtl);
				}
				getManagedBean().setZoneMap(zoneMap);
			}
			
			getManagedBean().setZoneMenuModel(zoneMenuModel);
		}
	}	
	
	private void populateScQtRecommendsByZone() {
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		TplSpecZoneDtl selectedZone = getManagedBean().getSelectedZone();
		if (selectedZone != null) {
			getManagedBean().setItemsList(
					scQtFinalization.getScQtRecommendsByZone()
							.get(selectedZone.getSpecZoneId()));
		}
	}	
	
	private List<ScQtEvaluationResultDtl> getShortlistedSuppliersByZone(TplSpecZoneDtl specZone) {
		Map<Long, List<ScQtEvaluationResultDtl>> qualifiedSuppliersByZone 
							= getManagedBean().getQualifiedSupppliersByZone();
		if (MapUtils.isNotEmpty(qualifiedSuppliersByZone)) {
			Long specZoneId = null;
			if (specZone == null) {
				specZoneId = (Long) (qualifiedSuppliersByZone
						.keySet().toArray())[0];
			} else {
				specZoneId = specZone.getSpecZoneId();
			}
			return qualifiedSuppliersByZone
						.get(specZoneId);
		}
		
		return null;
	}
	
	private void initializeRemarks() {
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();

		// Retrieve committee remarks
		List<ScQtFinalizationRemarks> remarks = initializeRemarks(scQtFinalization,
				null, scQtFinalization.getScQtFinalizationRemarks(), ScQtFinalizationRemarks.class);
		scQtFinalization.setScQtFinalizationRemarks(remarks);

		// Committee Remarks list
		getManagedBean().setFinalizationRemars(remarks);		
	}	
	
	private void finalizeRemarks() {
		ScQtFinalization scQtFinalization 
							= getManagedBean().getScQtFinalization();
		if (scQtFinalization != null) {
			List<ScQtFinalizationRemarks> remarksList = super.finalizeRemarks(
					getManagedBean().getFinalizationRemars());
			scQtFinalization.setScQtFinalizationRemarks(remarksList);
			setUserDetails(scQtFinalization);
		}
	}
	
	private void validateConsolidationReport() {
		boolean isZonal = getManagedBean().getScQt().isZonal();
		ScQtFinalization scQtFinalization 
					= getManagedBean().getScQtFinalization();
		List<ScQtEvaluationResultDtl> packageQualifiedSuppliers 
					= getManagedBean().getPackageQualifiedSuppliers();
		Map<Long,List<ScQtRecommend>> itemsByZone 
					= scQtFinalization.getScQtRecommendsByZone();
		Map<Long,List<ScQtEvaluationResultDtl>> qualifiedSuppliersByZonalPackage 
					= getManagedBean().getQualifiedSupppliersByZone();
		
		if (isZonal) {
			for (TplSpecZoneDtl specZoneDtl 
						: getManagedBean().getZoneDetails()) {
				Long specZoneId = specZoneDtl.getSpecZoneId();
				packageQualifiedSuppliers 
						= qualifiedSuppliersByZonalPackage.get(specZoneId);
				validateDORanking(specZoneDtl, itemsByZone.get(specZoneId),
								  packageQualifiedSuppliers);				
			}
		} else {
			validateDORanking(null, scQtFinalization.getScQtRecommends(),
							  packageQualifiedSuppliers);
		}
	}
	
	private void validateDORanking(TplSpecZoneDtl specZoneDtl, List<ScQtRecommend> items, 
								   List<ScQtEvaluationResultDtl> packageQualifiedSuppliers) {
		if (CollectionUtils.isNotEmpty(
				packageQualifiedSuppliers)) {
			validateRanking(null, packageQualifiedSuppliers, 
							  false, specZoneDtl);
		}
		
		List<ScQtEvaluationResultDtl> itemQualifiedSuppliers = null;
		if (CollectionUtils.isNotEmpty(items)) {
			for (ScQtRecommend item : items) {
				itemQualifiedSuppliers = item.getShortListedSupplierList();
				if (CollectionUtils.isEmpty(
							itemQualifiedSuppliers)) {
					SuppFinalizationResDTO resDTO 
							= retrieveQualifiedSuppliersByItem(item);
					itemQualifiedSuppliers = resDTO.getQualifiedByItemSuppliers(); 
				}
				if (CollectionUtils.isNotEmpty(
							itemQualifiedSuppliers)) {
					validateRanking(item, itemQualifiedSuppliers, 
									  true, specZoneDtl);
				}
			}
		}
	}
	
	private void validateRanking(ScQtRecommend item, List<ScQtEvaluationResultDtl> qualifiedSuppliers, 
								   boolean isItemized, TplSpecZoneDtl specZoneDtl) {
		int totalRanksCount = 0;
		int totalQualifiedSuppliers = qualifiedSuppliers.size();
		int minRankCount = totalQualifiedSuppliers;
		if (totalQualifiedSuppliers == 2) {
			minRankCount = 1;
		} else if (totalQualifiedSuppliers > 2) {
			minRankCount = 3;
		}
		
		// Sort the suppliers as per the rank given by the desk officer
		Collections.sort(qualifiedSuppliers, 
							new Comparator<ScQtEvaluationResultDtl>() {
								public int compare(ScQtEvaluationResultDtl supplier1, 
										ScQtEvaluationResultDtl supplier2) {
									if (supplier1.getDeskOfficerRank() != null && 
											supplier2.getDeskOfficerRank() != null) {
										
										// Ascending order
										return supplier1.getDeskOfficerRank().intValue() - 
													supplier2.getDeskOfficerRank().intValue();
									}
									return -1;
								}
							});
		
		int currentRank = 0;
		for (ScQtEvaluationResultDtl qualifiedSupp 
								: qualifiedSuppliers) {
			BigDecimal deskOfficerRank = qualifiedSupp.getDeskOfficerRank();
			if (deskOfficerRank != null) {
				int doRank = deskOfficerRank.intValue();
				if (doRank == currentRank) { 
					setZoneAndTabIndexOnException(isItemized, specZoneDtl);
					throw new EpException(MessageConst.ERROR_760381, 
								new String[] {String.valueOf(currentRank)});
				} else if (doRank > totalQualifiedSuppliers) {
					setZoneAndTabIndexOnException(isItemized, specZoneDtl);
					throw new EpException(MessageConst.ERROR_760382, 
								new String[] {String.valueOf(totalQualifiedSuppliers)});
				} else if (doRank != (currentRank + 1)) {
					setZoneAndTabIndexOnException(isItemized, specZoneDtl);
					throw new EpException(MessageConst.ERROR_760383, 
								new String[] {String.valueOf(currentRank)});
				}
				currentRank = doRank;
				++totalRanksCount;
			}
		}
		
		if (totalRanksCount < minRankCount) {
			setZoneAndTabIndexOnException(isItemized, specZoneDtl);
			throw new EpException(MessageConst.ERROR_760384, 
						new String[] {String.valueOf(minRankCount)});
		}
	}
	
	private SuppFinalizationReqDTO prepareDataToSave() {
		SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
		reqDTO.setScQtFinalization(
				getManagedBean().getScQtFinalization());
		reqDTO.setQualifiedSuppliers(
				getManagedBean().getToBeSavedQualifiedSuppliers());
		finalizeRemarks();
		
		return reqDTO;
	}
	
	private ScQtFinalizationReportAttachment generateDocumentAttachment(
			ScQtFinalizationReport scQtFinalizationReport) throws Exception {
		String definitionType = scQtFinalizationReport.getDefinationType()
				.getParameterCode();
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();

		ScQtFinalizationReportAttachment reportAttachment = null;
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
				.equals(definitionType)) {
			String docDefinitionKey = scQtFinalizationReport.getPmDocument()
					.getDocDefinitionKey();
			reportAttachment = new ScQtFinalizationReportAttachment();
			reportAttachment.setScQtFinalizationReport(scQtFinalizationReport);
			reportAttachment.setDocNo(scQtFinalization.getScQt().getQtNo());
			ScAttachment attachment = (ScAttachment) reportAttachment;
			StringBuilder filename = new StringBuilder();
			// replace white space due to firefox
			filename.append(scQtFinalizationReport.getTitle().replaceAll("\\s",
					"_"));

			if (SourcingConstants.DOC_DEF_KEY_COVER.equals(docDefinitionKey)) {
				filename.append(".pdf");
				reportAttachment.setFileName(filename.toString());
				generateCoverPdf(attachment, getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_OFFERED_QT_DTLS
					.equals(docDefinitionKey)) {
				generateOfferedAndQTDtlsPdf(attachment, getManagedBean()
						.getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_ANALYSIS_EVALUATION_REPORT
							.equals(docDefinitionKey)) {
				filename.append(".pdf");
				reportAttachment.setFileName(filename.toString());
				generateAnalysisEvaluationReportPdf(attachment,
						getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_NOTICE_BOARD
					.equals(docDefinitionKey)) {
				generateNoticeBoardPdf(attachment, getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_PRICE_SCHEDULE
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				reportAttachment.setFileName(filename.toString());
				generatePriceSchedulePdf(attachment, getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_EVALUATION_REPORT
					.equals(docDefinitionKey)) {
				filename.append(".pdf");
				reportAttachment.setFileName(filename.toString());
				populateAttachmentDetails(reportAttachment);

			} 
			scQtFinalizationReport
					.setScQtFinalizationReportAttachment(reportAttachment);
		}
		return reportAttachment;
	}	
	
	private void generateCoverPdf(ScAttachment attachment, ScQt scQt)
			throws Exception {
		debugLogger("generateCoverPdf");
		// attachment.setFileName("finalization-Cover.pdf");
		populateAttachmentDetails(attachment);

		// ScQt scQt = scQtFinalization.getScQt();
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
						.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		VelocityContext context = new VelocityContext();

		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		log.info("Current Year : " + year);

		context.put("qtNo", scQt.getQtNo());
		context.put("year", year);
		
		context.put("ptj", scQt.getPmOrgProfile().getOrgName());
		context.put("title", scQt.getQtTitle());

		context.put("procurementMode", scQt.getProcurementMode()
				.getParameterCode());

		String committeeType = this.getManagedBean().getCommitteeType();
		log.info("committeeType : " + committeeType);
		StringBuilder commType = new StringBuilder();
		if (commType != null) {
			commType.append(getParameterDesc(
					ParameterTypeConst.SC_COMMITTEE_TYPE, committeeType));
		}
		context.put("commType", commType);

		context.put("scQtMeetings", null);
		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_FZ_COVER, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}	
	

	private void generateOfferedAndQTDtlsPdf(ScAttachment attachment, ScQt scQt)
			throws Exception {
		debugLogger("generateOfferedAndQTDtlsPdf");

		attachment
				.setFileName("Keterangan_Tawaran_dan_Maklumat_Tender_Sebut_Harga.pdf");
		populateAttachmentDetails(attachment);

		// ScQt scQt = scQtFinalization.getScQt();
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
						.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		// DecimalFormat amountDF = new
		// DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		VelocityContext context = new VelocityContext();

		String committeeType = this.getManagedBean().getCommitteeType();
		log.info("committeeType : " + committeeType);
		StringBuilder commType = new StringBuilder();
		if (commType != null) {
			commType.append(getParameterDesc(
					ParameterTypeConst.SC_COMMITTEE_TYPE, committeeType));
		}
		context.put("commType", commType);
		context.put("ptj", scQt.getPmOrgProfile().getOrgName());
		context.put("title", scQt.getQtTitle());
		context.put("estimatedAmount", scQt.getEstimatedAmount());
		context.put("procurementMode", scQt.getProcurementMode().getParameterCode());
		context.put("fulfilmentType", scQt.getFulfilmentType().getParameterCode());

		boolean planned = false;
		if (scQt.isPlanned()) {
			planned = true;
			if (scQt.getScQtGosodoBudgets() != null) {
				context.put("budgetList", scQt.getScQtGosodoBudgets());
			}
		}
		context.put("isPlanned", planned);
		
		StringBuilder contractDuration = new StringBuilder();
		if (scQt.getContractDuration() != null) {
			contractDuration.append(scQt.getContractDuration() + " " + getMessageCommon("lbl.sc.sp.month")); 
		}
		context.put("contractDuration", contractDuration);
		
		boolean isSample = false;
		if (scQt.isSampleReq()) {
			isSample = true;
		}
		context.put("isSample", isSample);
		if (isSample) {
			String sampleClosingDate = "";
			if (scQt.getSampleClosingDate() != null) {
				sampleClosingDate = dateSDF.format(scQt.getSampleClosingDate());
			}
			context.put("sampleClosingDate", sampleClosingDate);
			
			IQTEvaluationSO evaluationSo = null;
			try {
				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				evaluationSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				EvaluationResDTO resDTO = evaluationSo
						.retrieveSampleLableDtls(reqDTO);	
				
				Map<String,SampleLabelDTO> sampleLabelsMap = null;
				
				List<ScQtSampleLable> scQtSampleLabelList = 
							resDTO.getLableResultBean().getResultList();
				if (CollectionUtils.isNotEmpty(scQtSampleLabelList)) {
					sampleLabelsMap = new HashMap<String,SampleLabelDTO>();
				}
				for (ScQtSampleLable scQtSampleLabel 
										: scQtSampleLabelList) {
					reqDTO.setLableId(scQtSampleLabel.getSampleLableId());
					resDTO = evaluationSo.retrieveSampleItemDtlsByLable(reqDTO);
					List<ScQtSampleItem> sampleItemsList = resDTO.getItemResultBean().getResultList();
					if (CollectionUtils.isNotEmpty(sampleItemsList)) {
						for (ScQtSampleItem scQtSampleItem : 
													sampleItemsList) {
							String itemName = scQtSampleItem.getScRequestItem().getItemName();
							if (sampleLabelsMap.containsKey(itemName)) {
								SampleLabelDTO sampleLabelDTO = sampleLabelsMap.get(itemName);
								String submissionStatusCode
												= scQtSampleItem.getSubmissionStatus().getParameterCode();
								if (!ParameterConst.SAMPLE_SUBMISSION_STATUS_NOT_SUBMIT
										.equals(submissionStatusCode)) {
									sampleLabelDTO.setTotalSubmitted(
											sampleLabelDTO.getTotalSubmitted() + 1);									
									if (ParameterConst.SAMPLE_SUBMISSION_STATUS_COMPLIANCE
											.equals(submissionStatusCode)) {
										sampleLabelDTO.setTotalCompliant(
												sampleLabelDTO.getTotalCompliant() + 1);										
									} else {
										sampleLabelDTO.setTotalNotCompliant(
												sampleLabelDTO.getTotalNotCompliant() + 1);										
									}
								} 
							} else {
								SampleLabelDTO sampleLabelDTO = new SampleLabelDTO();
								String submissionStatusCode
											= scQtSampleItem.getSubmissionStatus().getParameterCode();
								if (!ParameterConst.SAMPLE_SUBMISSION_STATUS_NOT_SUBMIT
										.equals(submissionStatusCode)) {
									sampleLabelDTO.setTotalSubmitted(1);									
									if (ParameterConst.SAMPLE_SUBMISSION_STATUS_COMPLIANCE
											.equals(submissionStatusCode)) {
										sampleLabelDTO.setTotalCompliant(1);										
									} else {
										sampleLabelDTO.setTotalNotCompliant(1);										
									}
								}
								sampleLabelsMap.put(itemName, sampleLabelDTO);
							}
						}
					}
				}
				context.put("sampleLabelsMap", sampleLabelsMap);
			} finally {
				evaluationSo = null;
			}
		}

		String publishDate = "";
		if (scQt.getPublishDate() != null) {
			publishDate = dateSDF.format(scQt.getPublishDate());
		}
		context.put("publishDate", publishDate);

		String closingDate = "";
		if (scQt.getClosingDate() != null) {
			closingDate = dateSDF.format(scQt.getClosingDate());
		}
		context.put("closingDate", closingDate);
		
		context.put("isBsvRequired", scQt.isBsvRequired());
		context.put("isZone", scQt.isZonal());
		
		List<TplSpecZoneDtl> zoneDetails = this.getManagedBean().getZoneDetails();
		context.put("zoneDetails", zoneDetails);

		// get Total Suppliers based on Zone
		Object[] object = null;
		BriefingZoneBean briefingZoneBean = null;
		Map<Long,BriefingZoneBean> zoneBeansMap 
					= new HashMap<Long,BriefingZoneBean>();
		IQTBriefingSO qTBriefingSO;
		if (scQt.isBsvRequired()) {
			try {
				qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
				String languageCode = new FacesScopes().getUserLocale()
						.getLanguage();
				BriefingReqDTO reqDTO = new BriefingReqDTO();
				reqDTO.setQtId(scQt.getQtId());
				reqDTO.setLanguageCode(languageCode);
				BriefingResDTO resDTO = qTBriefingSO
						.retrieveBriefingSiteVisitDtl(reqDTO);
				if (scQt.isZonal()) {
					List<TplSpecZoneDtl> zoneDtls = resDTO.getZonesList();
					if (null != zoneDtls) {
						context.put("zones", zoneDtls);
						for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
//							List<BriefingZoneBean> zoneBeans = new ArrayList<BriefingZoneBean>();
							reqDTO.setSpecZoneId(specZoneDtl.getSpecZoneId());
							BriefingResDTO briefingResDTO = qTBriefingSO
									.getTotalAttendedSupplier(reqDTO);
							object = briefingResDTO.getTotalAttendedSupplier();
							ScQtBsv scQtBsv = briefingResDTO.getScQtBsv();
							briefingZoneBean = new BriefingZoneBean(specZoneDtl,
									briefingResDTO.getBriefingList(),
									briefingResDTO.getSiteVisitList(), scQtBsv);
							
							int totalAttendedSuppliers = 0;
							if (object != null) {
								Long totalAttended = (Long) object[1];
								if (totalAttended != null) {
									totalAttendedSuppliers = totalAttended.intValue();
								}
							}
							briefingZoneBean.setTotalSuppliersAttended(
													totalAttendedSuppliers);
							zoneBeansMap.put(specZoneDtl.getSpecZoneId(),briefingZoneBean);
						}
					}
					context.put("zoneBeansMap", zoneBeansMap);					
				} else {
					BriefingResDTO briefingResDTO = qTBriefingSO
							.getTotalAttendedSupplier(reqDTO);
					object = briefingResDTO.getTotalAttendedSupplier();
				}
			} finally {
				qTBriefingSO = null;
			}
		} 
		String supplierSize = "";
		if (object != null) {
			Long totalAttended = (Long) object[1];
			if (totalAttended != null) {
				supplierSize = totalAttended.toString();
			}
		}
		context.put("supplierSize", supplierSize);		

		Integer totalQualifiedSuppliers = null;
		Map<Long,Integer> totalQualifiedSuppliersByZone = null;
		
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			
			if (scQt.isZonal()) {
				List<TplSpecZoneDtl> zoneDtls 
							= this.getManagedBean().getZoneDetails();
				if (null != zoneDtls) {
					totalQualifiedSuppliersByZone = 
									new HashMap<Long,Integer>();
					for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
						reqDTO.setSpecZoneId(specZoneDtl.getSpecZoneId());
						SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
								.getCountOfTotalQualifiedSuppliersByCriteria(reqDTO);
						totalQualifiedSuppliers 
							= resDTO.getTotalQualifiedSuppliers();
						
						if (totalQualifiedSuppliers == null) {
							totalQualifiedSuppliers = 0;
						}						
						totalQualifiedSuppliersByZone.put(
								specZoneDtl.getSpecZoneId(), 
								totalQualifiedSuppliers);
					}
					context.put("totalQualifiedSuppliersByZone", totalQualifiedSuppliersByZone);
				}
			} else {
				SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
						.getCountOfTotalQualifiedSuppliersByCriteria(reqDTO);
				totalQualifiedSuppliers = resDTO.getTotalQualifiedSuppliers();
				
				if (totalQualifiedSuppliers == null) {
					totalQualifiedSuppliers = 0;
				}
				context.put("totalQualifiedSuppliers", totalQualifiedSuppliers);				
			}
		} finally {
			qTBriefingSO = null;
		}

		// QTProposalSO.retrieveTotalSubmittedProposal
		Long totalSuppliers;
		Map<Long,Long> totalSuppliersSubmittedProposalByZone = null;
		IQTProposalSO qTProposalSO;
		try {
			qTProposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO proposalReqDTO = new ProposalReqDTO();
			proposalReqDTO.setQtId(scQt.getQtId());
			if (scQt.isZonal() && 
					CollectionUtils.isNotEmpty(zoneDetails)) {
				totalSuppliersSubmittedProposalByZone 
								= new HashMap<Long,Long>();
				for (TplSpecZoneDtl zoneDtl : zoneDetails) {
					proposalReqDTO.setSpecZoneId(zoneDtl.getSpecZoneId());
					ProposalResDTO proposalResDTO = qTProposalSO
							.getTotalSubmittedProposal(proposalReqDTO);
					totalSuppliers = proposalResDTO.getTotalSubmitted();
					totalSuppliersSubmittedProposalByZone.put(
							zoneDtl.getSpecZoneId(),totalSuppliers);
				}
				context.put("totalSuppliersSubmittedProposalByZone", 
									totalSuppliersSubmittedProposalByZone);				
			} else {
				ProposalResDTO proposalResDTO = qTProposalSO
						.getTotalSubmittedProposal(proposalReqDTO);
				totalSuppliers = proposalResDTO.getTotalSubmitted();
				context.put("totalSuppliersSubmittedProposal", totalSuppliers);
			}

		} finally {
			qTProposalSO = null;
		}

		Integer validityDays = scQt.getQtValidityPeriod();
		context.put("validityDays", validityDays);
		
		String proposalValidityDate = null;
		if (scQt.getProposalValidityEndDate() != null) {
			proposalValidityDate = dateSDF.format(scQt
					.getProposalValidityEndDate());
		}
		context.put("proposalValidityDate", proposalValidityDate);
		
		StringBuilder openType = new StringBuilder();
		if (openType != null) {
			openType.append(getParameterDesc(
					ParameterTypeConst.QUOTATION_TENDER_TYPE, scQt.getQtType()));
		}
		context.put("openType", openType);
		context.put("openFor", scQt.getOpenFor());

		String existingContract = scQt.getExistingContractNo();
		if (scQt.getExistingContractNo() == null) {
			existingContract = "";
		} else {
			IContractSO contractSO;
			try {
				contractSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				ContractReqDTO contractReqDTO = new ContractReqDTO();
				contractReqDTO.setContractNo(scQt.getExistingContractNo());
				ContractResDTO contractResDTO = contractSO
						.retriveContractInfoByContractNo(contractReqDTO);
				if (contractResDTO.getContract() != null) {
					CtContract contract = contractResDTO.getContract();
					context.put("contract", contract);
				}
			} finally {
				contractSO = null;
			}
		}
		context.put("existingContract", existingContract);
		context.put("initialDecisionFwdToBPK", false);

		List<ScQtCommitteeActivity> committeeActivities = new ArrayList<ScQtCommitteeActivity>();
		List<ScQtCommitteeActivity> committeeActivitiesList = new ArrayList<ScQtCommitteeActivity>();
		ScQtCommittee scQtCommittee = new ScQtCommittee();
		IQTMeetingSO meetingSO = null;
		MeetingResDTO resDTO = null;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setCommitteType(committeeType);
			resDTO = meetingSO.retrieveMeetingDls(reqDTO);
			scQtCommittee = resDTO.getQtCommittee();
			if (null == scQtCommittee) {
				scQtCommittee = new ScQtCommittee();
			}
			if (null != scQtCommittee) {
				committeeActivitiesList = scQtCommittee.getCommitteeActivities();
				
				ScCommittee sccommittee = resDTO.getQtCommittee()
						.getScCommittee();
				committeeActivities = new ArrayList<ScQtCommitteeActivity>();
				if (CollectionUtils.isNotEmpty(sccommittee.getScCommitteeMembers())) {
					for (ScCommitteeMember scCommitteeMember : sccommittee
							.getScCommitteeMembers()) {
						ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
						activity.setScQtCommittee(this.getManagedBean()
								.getScQtCommittee());
						activity.setScCommitteeMember(scCommitteeMember);
						setDefaultValueForCommitteeActivityIfNull(activity);
						committeeActivities.add(activity);
					}
				}
				Collections.sort(committeeActivities);
			}
		} finally {
			meetingSO = null;
		}
		context.put("committeeActivities", committeeActivities);
		
		context.put("bean", this);
		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_FZ_OFFERED_QT_DTLS, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}
	
	private void generateAnalysisEvaluationReportPdf(ScAttachment attachment,
			ScQt scQt) throws Exception {
		// attachment.setFileName("AnalysisOfEvaluation.pdf");
		populateAttachmentDetails(attachment);

		if (!getManagedBean().isSubmit()) {
			FacesScopes facesScopes = new FacesScopes();
			long qtId = scQt.getQtId();
			String reportId = "";
			String reportName = ReportConst.SC_FINANCE_ANALYSIS_EVALUATION_RPT_JSP;
			reportId = "SC_037";
	
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
			paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
					.getUserSessionBean().getUserId() + "");
			paramMap.put(ReportConst.PARAM_KEY_REPORT_ID, reportId + "");
	
			generatePdfFromOracle(reportName, paramMap, attachment, false);
		}
	}
	
	private void generateNoticeBoardPdf(ScAttachment attachment, ScQt scQt)
			throws Exception {
		debugLogger("generateNoticeBoardPdf");

		attachment.setFileName("Notice_Board.pdf");
		populateAttachmentDetails(attachment);

		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
						.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		DecimalFormat amountDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		VelocityContext context = new VelocityContext();
		
		context.put("dateSDF", dateSDF);

		context.put("title", scQt.getQtTitle());
		context.put("qtNo", scQt.getQtNo());
		context.put("zonal", scQt.isZonal());
		
		String ministryOrgName = null;
		if (scQt.getMinistryOrgProfile() != null) {
			ministryOrgName = scQt.getMinistryOrgProfile().getOrgName();
		} else {
			ministryOrgName = StringUtils.EMPTY;
		}
		context.put("ministry", ministryOrgName);		
		
		context.put("ptj", scQt.getPmOrgProfile().getOrgName());
		String publishDate = "";
		if (scQt.getPublishDate() != null) {
			publishDate = dateSDF.format(scQt.getPublishDate());
		}
		context.put("publishDate", publishDate);
		context.put("publishPeriod", scQt.getPublishPeriod());
		String closingDate = "";
		if (scQt.getClosingDate() != null) {
			closingDate = dateSDF.format(scQt.getClosingDate());
		}
		context.put("closingDate", closingDate);
		context.put("tier", scQt.getEvaluationType());
		
		Integer validityDays = scQt.getQtValidityPeriod();
		context.put("validityDays", validityDays);
		
		String proposalValidityDate = null;
		if (scQt.getProposalValidityEndDate() != null) {
			proposalValidityDate = dateSDF.format(scQt
					.getProposalValidityEndDate());
		}
		context.put("proposalValidityDate", proposalValidityDate);	

		context.put("openFor", scQt.getOpenFor());

		String paidupcapital = "";
		if (scQt.getScQtSupplierCriteria() != null) {
			if (scQt.getScQtSupplierCriteria().getMinPaidUpCapital() != null) {
				paidupcapital = amountDF.format(scQt.getScQtSupplierCriteria()
						.getMinPaidUpCapital());
			}
		}
		context.put("paidupcapital", paidupcapital);

		context.put("categoryList", scQt.getScQtCategorys());
		context.put("bean", this);
		
		boolean isPanel = false;
		if (scQt.isPanel()) {
			isPanel = true;
			List<CtPanelItem> panelList = new ArrayList<CtPanelItem>();
			panelList.add(scQt.getScQtSupplierCriteria().getCtPanelItem());
			context.put("ctCircularVer", scQt.getScQtSupplierCriteria().getCtPanelItem().getCtCircularVer());
			context.put("panelList", panelList);
			context.put("bean", this);
		} else {
			
		}
		context.put("panel", isPanel);
		
		String locationDistrict = "District";
		String locationLevel = null;
		boolean isLocLevel = false;
		boolean isSubLocLevel = false;
		if (scQt.isZonal() && scQt.getZoneLocationLevel() != null) {
			if (ParameterConst.LOCATION_LEVEL_STATE.equals(scQt
					.getZoneLocationLevel().getParameterCode())) {
				isLocLevel = true;
				locationLevel = "State";
			} else if (ParameterConst.LOCATION_LEVEL_DIVISION.equals(scQt
					.getZoneLocationLevel().getParameterCode())) {
				isLocLevel = true;
				locationLevel = "Division";
			} else if (ParameterConst.LOCATION_LEVEL_DISTRICT.equals(scQt
					.getZoneLocationLevel().getParameterCode())) {
				isLocLevel = true;
				locationLevel = locationDistrict;
			}
			context.put("zoneLocLevel", isLocLevel);
			context.put("zoneLocationLevel", locationLevel);
			
			IQTDetailSO qtDtlSO;
			try {
				qtDtlSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
				qtReqDTO.setDocType(QTConstants.DOCTYPE_QT);
				qtReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				qtReqDTO.setBdgDtlsReq(true);
				QuationTenderResDTO qtResDTO = new QuationTenderResDTO();
				qtResDTO = qtDtlSO.retriveQTDetails(qtReqDTO);

				Map<TplSpecZoneDtl, Map<PmState,List<PmDistrict>>> zoneLocationMap = 
							new HashMap<TplSpecZoneDtl, Map<PmState,List<PmDistrict>>>();
				if (CollectionUtils.isNotEmpty(
							qtResDTO.getZoneDetails())) {
					for (TplSpecZoneDtl zoneDtl 
								: qtResDTO.getZoneDetails()) {
						if (CollectionUtils.isNotEmpty(
								zoneDtl.getTplSpecZoneLocs())) {
							Map<PmState,List<PmDistrict>> stateAndDistrict
									= new HashMap<PmState,List<PmDistrict>>();					
							for (TplSpecZoneLoc specZoneLoc 
										: zoneDtl.getTplSpecZoneLocs()) {
								PmState state = specZoneLoc.getPmState();
								
								if (locationDistrict.equals(locationLevel)) {
									List<PmDistrict> districtList 
										= stateAndDistrict.get(state);
									if (districtList == null) {
										districtList = new ArrayList<PmDistrict>();
									}
									districtList.add(specZoneLoc.getPmDistrict());
									stateAndDistrict.put(state, districtList);
								} else {
									stateAndDistrict.put(state, null);
								}
							}
							zoneLocationMap.put(zoneDtl, stateAndDistrict);
						}
						
					}
				}
				context.put("zoneLocality", zoneLocationMap);
			} finally {
				qtDtlSO = null;
			}			
		}
		
		locationLevel = null;
		isLocLevel = false;
		boolean isAllState = false;
		ScQtSupplierCriteria criteria = scQt.getScQtSupplierCriteria();
		
		if (criteria != null) {
			isAllState = criteria.isAllState();
			
			if (criteria.getLocationLevel() != null) {
				if (ParameterConst.LOCATION_LEVEL_STATE.equals(criteria
						.getLocationLevel().getParameterCode())) {
					isSubLocLevel = true;
					locationLevel = "State";
				} else if (ParameterConst.LOCATION_LEVEL_DISTRICT
						.equals(criteria.getLocationLevel()
								.getParameterCode())) {
					isSubLocLevel = true;
					locationLevel = locationDistrict;
				}
				context.put("subLocLevel", isSubLocLevel);
			} else {
				context.put("subLocLevel", isSubLocLevel);
			}
		}
		context.put("locLevel", isLocLevel);
		context.put("isAllState", isAllState);
		context.put("locationLevel", locationLevel);	
		
		IQTDetailSO qtDetailsSO;
		try {
			qtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			tenderReqDTO.setQtdetail(this.getManagedBean().getScQt());
			QuationTenderResDTO tenderResDTO = qtDetailsSO
					.retriveQTSuppQulCriDetails(tenderReqDTO);
			Map<PmState,List<PmDistrict>> locationMap = 
							new HashMap<PmState,List<PmDistrict>>();
			if (CollectionUtils.isNotEmpty(
					tenderResDTO.getLocationDetails())) {
				for (TplSpecLoc specLoc 
							: tenderResDTO.getLocationDetails()) {
					PmState state = specLoc.getPmState();
					List<PmDistrict> districtList = null;
					
					if (locationDistrict.equals(locationLevel)) {
						districtList = locationMap.get(state);
						
						if (districtList == null) {
							districtList = new ArrayList<PmDistrict>();
						}
						districtList.add(specLoc.getPmDistrict());
					}
					locationMap.put(state, districtList);
				}
				context.put("locality", tenderResDTO.getLocationDetails());				
			}			
		} finally {
			qtDetailsSO = null;
		}		

		boolean isbsv = false;
		if (scQt.isBsvRequired()) {
			isbsv = true;
			boolean isZone = false;
			IQTBriefingSO qTBriefingSO = null;
			ScQtBsv qtBsv = null;
			BriefingZoneBean briefingZoneBean = null;
			List<BriefingZoneBean> zoneBeans = new ArrayList<BriefingZoneBean>();
			try {
				qTBriefingSO = (IQTBriefingSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTBriefingSO.class);
				BriefingReqDTO reqDTO = new BriefingReqDTO();
				reqDTO.setScQt(this.getManagedBean().getScQt());
				BriefingResDTO resDTO = qTBriefingSO
						.retrieveBriefingSiteVisitDtl(reqDTO);
				ScQt qt = resDTO.getScQt();
				if (qt.isZonal()) {
					List<TplSpecZoneDtl> zones = resDTO.getZonesList();
					if (null != zones) {
						isZone = true;
						BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
						briefingReqDTO.setQtId(getManagedBean().getScQt()
								.getQtId());
						for (TplSpecZoneDtl specZoneDtl : zones) {
							briefingReqDTO.setSpecZoneId(specZoneDtl
									.getSpecZoneId());
							BriefingResDTO briefingResDTO = qTBriefingSO
									.retrieveBriefingSiteVisitDtls(briefingReqDTO);
							ScQtBsv scQtBsv = briefingResDTO.getScQtBsv();
							briefingZoneBean = new BriefingZoneBean(
									specZoneDtl,
									briefingResDTO.getBriefingList(),
									briefingResDTO.getSiteVisitList(), scQtBsv);
							zoneBeans.add(briefingZoneBean);
						}
					}
				} else {
					qtBsv = resDTO.getScQtBsv();
					briefingZoneBean = new BriefingZoneBean(null,
							resDTO.getBriefingList(),
							resDTO.getSiteVisitList(), qtBsv);
					zoneBeans.add(briefingZoneBean);
				}
			} finally {
				qTBriefingSO = null;
			}
			context.put("isbsv", isbsv);
			context.put("isZone", isZone);
			context.put("zoneBeans", zoneBeans);
		} else {
			isbsv = false;
			context.put("isbsv", isbsv);
		}

		List<ScQtContactPerson> contactPersons = new ArrayList<ScQtContactPerson>();
		List<ScQtContactPerson> contactPersonsList = new ArrayList<ScQtContactPerson>();
		IQTNoticeBoardSO qtNoticeBoardSO = null;
		NoticeBoardReqDTO noticeBoardReqDTO = new NoticeBoardReqDTO();
		NoticeBoardResDTO noticeBoardResDTO = new NoticeBoardResDTO();
		try {
			qtNoticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);
			noticeBoardReqDTO.setQtId(scQt.getQtId());
			noticeBoardResDTO = qtNoticeBoardSO
					.retrieveQtNoticeDtl(noticeBoardReqDTO);
			if (noticeBoardResDTO.getContactPersonResultBean().getResultList() != null) {
				contactPersons = noticeBoardResDTO.getContactPersonResultBean()
						.getResultList();
			}
		} finally {
			qtNoticeBoardSO = null;
		}
		for (ScQtContactPerson contactPerson : contactPersons) {
			if (contactPerson.getPhoneNo() == null) {
				contactPerson.setPhoneNo("");
			}
			if (contactPerson.getFaxNo() == null) {
				contactPerson.setFaxNo("");
			}
			if (contactPerson.getEmail() == null) {
				contactPerson.setEmail("");
			}
			contactPersonsList.add(contactPerson);
		}
		context.put("contactPersons", contactPersonsList);
		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_FZ_NOTICE_BOARD, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	private void generatePriceSchedulePdf(ScAttachment attachment, ScQt scQt)
			throws Exception {
		debugLogger("generatePriceSchedulePdf");

		// attachment.setFileName("finalization-priceSchedule.pdf");
		populateAttachmentDetails(attachment);

		VelocityContext context = new VelocityContext();

		List<TplSpecZoneDtl> list = new ArrayList<TplSpecZoneDtl>();
		List<SupplierQualificationBean> beans = new ArrayList<SupplierQualificationBean>();
		boolean isZone = false;
		IQTNoticeBoardSO qtNoticeBoardSO = null;
		NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
		
		try {
			qtNoticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);
			reqDTO.setQtId(scQt.getQtId());
			NoticeBoardResDTO resDTO = qtNoticeBoardSO
					.retrieveOfferedPriceDtls(reqDTO);
			
			if (resDTO.getScQt().isZonal()) {
				Map<Long,List<SupplierQualificationBean>>
						zonalQualifiedSuppliersMap = new HashMap<Long,List<SupplierQualificationBean>>();
				List<TplSpecZoneDtl> zones = resDTO.getZones();
				if (null != zones) {
					isZone = true;
					NoticeBoardReqDTO boardReqDTO = new NoticeBoardReqDTO();
					boardReqDTO.setQtId(scQt.getQtId());
					list.addAll(resDTO.getZones());
					context.put("zones", list);
					for (TplSpecZoneDtl zoneDtl : zones) {
						boardReqDTO.setZoneId(zoneDtl.getSpecZoneId());
						NoticeBoardResDTO boardResDTO = qtNoticeBoardSO
								.retrieveOfferedPriceDtlsByZone(boardReqDTO);
						zonalQualifiedSuppliersMap.put(zoneDtl.getSpecZoneId(), 
								boardResDTO.getQualificationBean());
					}
					context.put("zones", list);
					context.put("supplierBeansMap", zonalQualifiedSuppliersMap);
				}
			} else {
				beans.addAll(resDTO.getQualificationBean());
				context.put("supplierBeans", beans);
			}
		} finally {
			qtNoticeBoardSO = null;
		}
		context.put("isZone", isZone);
		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_FZ_PRICE_SCHEDULE, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}	
	
	private void setDefaultValueForCommitteeActivityIfNull(
					ScQtCommitteeActivity committeeActivity) {
		if (committeeActivity.getScCommitteeMember() != null) {
			if (committeeActivity.getScCommitteeMember()
					.getMemberName() == null) {
				committeeActivity.getScCommitteeMember()
						.setMemberName("");
			}
		} else {
			committeeActivity
					.setScCommitteeMember(new ScCommitteeMember());
			committeeActivity.getScCommitteeMember()
					.setMemberName("");
		}
		if (committeeActivity.getScCommitteeMember() != null) {
			if (committeeActivity.getScCommitteeMember()
					.getDesignation() == null) {
				committeeActivity.getScCommitteeMember()
						.setDesignation("");
			}
		} else {
			committeeActivity
					.setScCommitteeMember(new ScCommitteeMember());
			committeeActivity.getScCommitteeMember()
					.setDesignation("");
		}
		if (committeeActivity.getScCommitteeMember() != null) {
			if (committeeActivity.getScCommitteeMember()
					.getGrade() == null) {
				committeeActivity.getScCommitteeMember()
						.setGrade("");
			}
		} else {
			committeeActivity
					.setScCommitteeMember(new ScCommitteeMember());
			committeeActivity.getScCommitteeMember().setGrade(
					"");
		}
		if (committeeActivity.getScCommitteeMember() != null) {
			if (committeeActivity.getScCommitteeMember()
					.getIcPassport() == null) {
				committeeActivity.getScCommitteeMember()
						.setIcPassport("");
			}
		} else {
			committeeActivity
					.setScCommitteeMember(new ScCommitteeMember());
			committeeActivity.getScCommitteeMember()
					.setIcPassport("");
		}		
	}	
	
	private void navigateToBriefingPaperTab() {
		getManagedBean().setActiveIndex(BRIEFING_PAPER_TAB_INDEX);
	}
	
	private void navigateToItemizedTab() {
		getManagedBean().setItemized(true);
		getManagedBean().setActiveIndex(ITEMIZED_TYPE_TAB_INDEX);
		getManagedBean().setQualifiedSuppliers(null);
	}
	
	private void navigateToPackagewiseTab() {
		getManagedBean().setItemized(false);
		
		List<ScQtEvaluationResultDtl> qualifiedSuppliers = null;
		if (getManagedBean().getScQt().isZonal()) {
			qualifiedSuppliers = getShortlistedSuppliersByZone(
										getManagedBean().getSelectedZone());
		} else {
			qualifiedSuppliers = getManagedBean().getPackageQualifiedSuppliers();
		}
		if (qualifiedSuppliers != null) {
			getManagedBean().setMaxRank(
					String.valueOf(qualifiedSuppliers.size()));		
		}
		getManagedBean().setQualifiedSuppliers(qualifiedSuppliers);
		getManagedBean().setActiveIndex(PACKAGE_TYPE_TAB_INDEX);
	}
	
	private void commonMessageAcknowledmentPage(String messageId, String variable1, String variable2){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
			FacesMessage.SEVERITY_INFO, 
			null,
			MessageUtils.getMessage(messageId, 
					new String[]{variable1, variable2}, 
					MessageUtils.BUNDLE_COMMON_MESSAGES, 
					new FacesScopes().getUserLocale())));
		 // clear data
		super.renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
	}	
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}


	/**
	 * @return the log
	 */
	public static Logger getLog() {
		return log;
	}

	/**
	 * @return the BRIEFING_PAPER_TAB_INDEX
	 */
	public static int getBRIEFING_PAPER_TAB_INDEX() {
		return BRIEFING_PAPER_TAB_INDEX;
	}

	/**
	 * @return the ITEMIZED_TYPE_TAB_INDEX
	 */
	public static int getITEMIZED_TYPE_TAB_INDEX() {
		return ITEMIZED_TYPE_TAB_INDEX;
	}

	/**
	 * @return the PACKAGE_TYPE_TAB_INDEX
	 */
	public static int getPACKAGE_TYPE_TAB_INDEX() {
		return PACKAGE_TYPE_TAB_INDEX;
	}
	
	/** End of Private Methods **/
	
	
	public class SampleLabelDTO {
		private int totalCompliant;
		private int totalNotCompliant;
		private int totalSubmitted;
		
		public SampleLabelDTO() {
		}

		/**
		 * @return the totalCompliant
		 */
		public int getTotalCompliant() {
			return totalCompliant;
		}

		/**
		 * @param totalCompliant the totalCompliant to set
		 */
		public void setTotalCompliant(int totalCompliant) {
			this.totalCompliant = totalCompliant;
		}

		/**
		 * @return the totalNotCompliant
		 */
		public int getTotalNotCompliant() {
			return totalNotCompliant;
		}

		/**
		 * @param totalNotCompliant the totalNotCompliant to set
		 */
		public void setTotalNotCompliant(int totalNotCompliant) {
			this.totalNotCompliant = totalNotCompliant;
		}

		/**
		 * @return the totalSubmitted
		 */
		public int getTotalSubmitted() {
			return totalSubmitted;
		}

		/**
		 * @param totalSubmitted the totalSubmitted to set
		 */
		public void setTotalSubmitted(int totalSubmitted) {
			this.totalSubmitted = totalSubmitted;
		}

	}	

}
