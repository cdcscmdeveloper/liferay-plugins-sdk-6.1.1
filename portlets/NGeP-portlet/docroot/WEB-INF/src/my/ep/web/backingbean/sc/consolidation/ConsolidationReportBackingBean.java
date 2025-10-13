/**
 * Copyright � 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.consolidation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date; //#38151
import java.sql.Timestamp; //#38151
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.bean.sc.QtEvaluationResultDtlBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO; //#38151
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.quationtender.EvaluationReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO; //#38151
import my.ep.dto.sc.quationtender.SpecificationReqDTO; //#38151
import my.ep.dto.sc.quationtender.SpecificationResDTO; //#38151
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAppVersion; //#38151
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendationUlasanRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreSetting; //#38151
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalItem;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
// #31443 Start
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneQty;
// #31443 End
import my.ep.type.aut.SearchOperator;
import my.ep.util.CustomUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.approval.ApprovalBackingBean;
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.sc.supplierfinalization.SupplierFinalizationBackingBean;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.WeightageScoreBean; //#38151
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.key.sc.supplierfinalization.MapKey;
import my.ep.web.managedbean.sc.consolidation.ConsolidationReportManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;

/**
 * ConsolidationReport Detail Backing Bean.
 * 
 * @author Praneeth
 * 
 * @version 1.1
 * @since 14.01.2016
 */
@ManagedBean(name = "consolidationReportBackingBean")
@RequestScoped
public class ConsolidationReportBackingBean extends
		SourcingBaseBackingBean<ConsolidationReportManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger
			.getLogger(ConsolidationReportBackingBean.class);

	private final static int BRIEFING_PAPER_TAB_INDEX = 3;
	private final static int PACKAGE_TYPE_TAB_INDEX = 1;
	private final static int ITEMIZED_TYPE_TAB_INDEX = 2;
	private final static int EVALUATION_REPORT_TAB_INDEX = 0;

	private final static Long DEFAULT_ITEM = 0L;
	private final static Long DEFAULT_ZONE = 0L;

	/** Default constructor */
	public ConsolidationReportBackingBean() {
	}

	/** Public Methods **/
	public void preRenderView() {
		FacesContext fc = FacesContext.getCurrentInstance();

		try {
			SCEvaluationDataType dataType = getBpmTaskDetails(
					SCEvaluationDataType.class, null);
			Long qtId = Long.valueOf(dataType.getQtId());
			getManagedBean().setQtId(qtId);
			getManagedBean().setCommitteeType(dataType.getCommitteeType());
			getManagedBean().setPayLoad(dataType);
			getManagedBean().setActiveIndex(0);

			loadInitialData();
			// #18685 - Start.
			getManagedBean().setDocNo(getManagedBean().getScQt().getQtNo());
			getManagedBean().setAllSupplierDisagreeEPV(allSupplierDisagreeEPV(qtId));//#21692
			// #18685 - End.
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							QTConstants.DO_CONSOLIDATION_REPORT_PAGE);
		} catch (Exception e) {
			handleException(e);
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);
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
			navigateToEvaluationReport();
			break;
		case 1:
			navigateToPackagewiseTab();
			break;
		case 2:
			navigateToItemizedTab();
			break;
		case 3:
			navigateToBriefingPaperTab();
			break;
		}
		onTabMenuChangeAction(tabIndex, true);
	}

	private void onTabMenuChangeAction(int tabIndex, boolean isEventTriggered) {

		if (isEventTriggered) {
			ScQt scQt = this.getManagedBean().getScQt();
			if (scQt.isZonal()
					&& CollectionUtils.isNotEmpty(this.getManagedBean()
							.getZoneDetails())) {
				this.getManagedBean().setSelectedZone(
						this.getManagedBean().getZoneDetails().get(0));
				this.getManagedBean().setZoneActiveIndex(0);
			}
		}
		switch (tabIndex) {
		case 0:
			navigateToEvaluationReport();
			break;
		case 1:
			navigateToPackagewiseTab();
			break;
		case 2:
			navigateToItemizedTab();
			break;
		case 3: 
			navigateToBriefingPaperTab();
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
		if (selectedZoneTabIndex == getManagedBean().getZoneActiveIndex()) {
			return;
		}

		TplSpecZoneDtl selectedZone = getManagedBean().getZoneDetails().get(
				selectedZoneTabIndex);
		getManagedBean().setSelectedZone(selectedZone);
		getManagedBean().setZoneActiveIndex(selectedZoneTabIndex);

		if (!getManagedBean().isItemized()) {
			navigateToPackagewiseTab();
		} else {
			getManagedBean().setQualifiedSuppliersDataModel(null);
			getManagedBean().setDisQualifiedSuppliersDataModel(null);// #28777&#29729
			populateScQtRecommendsByZone();
		}
	}

	/**
	 * Method to retrieve Board of Directors Details pop up in Consolidation of
	 * Report
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
			reqDTO.setSupplierType(ParameterConst.SUPPLIER_TYPE_CONTRACTOR);

			personnelSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);
			PersonnelResDTO resDTO = personnelSO
					.retrieveBordOfDirectors(reqDTO);
			if (resDTO.isSuccessful()) {
				boardofDirectors = resDTO.getSmPersonnelList();
				if (CollectionUtils.isNotEmpty(boardofDirectors)) {
					for (SmPersonnel smPersonnel : boardofDirectors) {
						SmMofAccount smMofAccount = retriveMofAccountBySupplier(smPersonnel
								.getSmAppl().getSmSupplier());
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
		getManagedBean().setSelectedItem(scQtRecommend);

		Long selectedZoneId = null;
		ScQt scQt = getManagedBean().getScQt();
		if (scQt.isZonal()) {
			selectedZoneId = getManagedBean().getSelectedZone().getSpecZoneId();
		}
		getManagedBean().setQualifiedSuppliersDataModel(
				new QualifiedSuppliersPagingDataModel("qualifiedSuppliers",
						scQt.getQtId(),
						SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM,
						selectedZoneId, scQtRecommend)); 
		
		// #28777 & #29729 - Start
		Long qtId = this.getManagedBean().getScQt().getQtId();
		List<Long> quaQtSupplierIdList = retrieveZoneQtSuppliersIdsByQtIdAndSpecZoneId(true, qtId, selectedZoneId);
		if (CollectionUtils.isEmpty(quaQtSupplierIdList) && selectedZoneId != null) {
			getManagedBean().setQualifiedSuppliersDataModel(null);
		}

		getManagedBean().setDisQualifiedSuppliersDataModel(
				new DisqualifiedSuppliersPagingDataModel("disQualifiedSuppliers", scQt.getQtId(),
						SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM, selectedZoneId, scQtRecommend));
		// #28777 & #29729 - End
	}

	/**
	 * Update the latest rank and remarks in the cached list
	 */
	public void updateCachedData(ScQtEvaluationResultDtl selectedSupplier) {
		if (selectedSupplier == null) {
			return;
		}

		Long selectedZoneId = DEFAULT_ZONE;
		if (getManagedBean().getScQt().isZonal()) {
			selectedZoneId = getManagedBean().getSelectedZone().getSpecZoneId();
		}

		Long selectedItemId = DEFAULT_ITEM;
		if (getManagedBean().isItemized()) {
			selectedItemId = getManagedBean().getSelectedItem()
					.getRequestItem().getRequestNoteItemId();
		}

		List<QtEvaluationResultDtlBean> cachedSuppliersList = getCachedSuppliersFromMap(
				selectedZoneId, selectedItemId);

		QtEvaluationResultDtlBean doRecommendationBean = getCachedBeanFromList(
				cachedSuppliersList,
				selectedSupplier.getEvaluationResultDtlId());

		if (doRecommendationBean == null) {
			doRecommendationBean = new QtEvaluationResultDtlBean();
			doRecommendationBean.setQtEvalResDtlId(selectedSupplier
					.getEvaluationResultDtlId());
			doRecommendationBean.setEvaluationRank(selectedSupplier
					.getEvalRank());
			doRecommendationBean.setDeskOfficerRank(selectedSupplier
					.getDeskOfficerRank());
			doRecommendationBean.setDeskOfficerRemark(selectedSupplier
					.getDeskOfficerRemark());

			cachedSuppliersList.add(doRecommendationBean);
		} else {
			doRecommendationBean.setDeskOfficerRank(selectedSupplier
					.getDeskOfficerRank());
			doRecommendationBean.setDeskOfficerRemark(selectedSupplier
					.getDeskOfficerRemark());
		}
	}

	/**
	 * Add ScQtFinalizationReport
	 */
	public void addFinalizationReportAction() {
		debugLogger("addScQtFinalizationReport");

		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();

		List<ScQtFinalizationReport> scQtFinalizationReports = getManagedBean()
				.getScQtFinalization().getScQtFinalizationReports();
		if (scQtFinalizationReports == null) {
			scQtFinalizationReports = new ArrayList<ScQtFinalizationReport>();
			scQtFinalization
					.setScQtFinalizationReports(scQtFinalizationReports);
		}

		ScQtFinalizationReport report = new ScQtFinalizationReport();
		report.setScQtFinalization(scQtFinalization);

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
			report.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_BRIEFING_PAPER); // NR
		} finally {
			parameterSO = null;
		}

		scQtFinalizationReports.add(report);
	}

	/**
	 * Delete ScQtFinalizationReport
	 */
	public void deleteFinalizationReportAction() {
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		List<ScQtFinalizationReport> selectedFinalizationReports = getManagedBean()
				.getSelectedFinalizationReports();
		List<ScQtFinalizationReport> deletedFinalizationReports = getManagedBean()
				.getDeletedFinalizationReports();
		if (deletedFinalizationReports == null) {
			deletedFinalizationReports = new ArrayList<ScQtFinalizationReport>();
			getManagedBean().setDeletedFinalizationReports(
					deletedFinalizationReports);
		}
		if (CollectionUtils.isNotEmpty(selectedFinalizationReports)) {
			for (ScQtFinalizationReport report : selectedFinalizationReports) {
				if (!report.isBrandNew()) {
					report.setDeleted(true);
					deletedFinalizationReports.add(report);
				}
			}
			deleteRowsAction(scQtFinalization.getScQtFinalizationReports(),
					selectedFinalizationReports);
		} else {
			throw new EpException(MessageConst.ERROR_760158);
		}
		getManagedBean().setSelectedFinalizationReports(null);
	}

	/**
	 * Rowselect event to select Supplier Finalization Report under Briefing
	 * Paper tab
	 * 
	 * @param ScQtFinalizationReport
	 *            the ScQtFinalizationReport to set
	 */
	public void selectReportEvent(SelectEvent event) {
		debugLogger("selectReportEvent - Row Select");

		ScQtFinalizationReport report = (ScQtFinalizationReport) event
				.getObject();
		report.setSelected(true);

		selectReportEvent(report);
	}

	/**
	 * Checkbox event to select Supplier Finalization Report under Briefing
	 * Paper tab
	 * 
	 * @param ScQtFinalizationReport
	 *            the ScQtFinalizationReport to set
	 */
	public void selectReportEvent(ScQtFinalizationReport report) {
		debugLogger("selectReportEvent - Checkbox select");

		List<ScQtFinalizationReport> selectedFinalizationReports = getManagedBean()
				.getSelectedFinalizationReports();
		if (selectedFinalizationReports == null) {
			selectedFinalizationReports = new ArrayList<ScQtFinalizationReport>();
			getManagedBean().setSelectedFinalizationReports(
					selectedFinalizationReports);
		}
		if (report.isSelected()) {
			selectedFinalizationReports.add(report);
		} else {
			selectedFinalizationReports.remove(report);
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
			ScQtFinalizationReport scQtFinalizationReport = this
					.getManagedBean().getSelectedFinalizationReport();
			ScQtFinalizationReportAttachment reportAttachment = scQtFinalizationReport
					.getScQtFinalizationReportAttachment();
			if (null == reportAttachment) {
				reportAttachment = new ScQtFinalizationReportAttachment();
				reportAttachment
						.setScQtFinalizationReport(scQtFinalizationReport);
				reportAttachment.setDocNo(getManagedBean().getScQt().getQtNo());
				scQtFinalizationReport
						.setScQtFinalizationReportAttachment(reportAttachment);
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

		ScQtFinalizationReportAttachment reportAttachment = scQtFinalizationReport
				.getScQtFinalizationReportAttachment();
		String definitionType = scQtFinalizationReport.getDefinationType().getParameterCode();
		String docDefinitionKey = null;
		if (!SourcingConstants.DEMO_EVALUATION_REPORT.equalsIgnoreCase(scQtFinalizationReport.getTitle())) {//#28784
			docDefinitionKey = scQtFinalizationReport.getPmDocument().getDocDefinitionKey();
		}
		// codes fixed related to 25619 issue.
		if (reportAttachment == null
				|| (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED.equals(definitionType) && SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT
						.equals(docDefinitionKey))) {
			reportAttachment = generateDocumentAttachment(scQtFinalizationReport);
			// scQtFinalizationReport.setScQtFinalizationReportAttachment(reportAttachment);
		}
		downloadFile(reportAttachment);
	}
	
	// #18685 - Start
	/**
	 * @throws Exception
	 */
	// 38523 - start
	public void downloadAllBriefingPaperReports(boolean skipDownload) throws Exception {
		// #18685 - Start.
		debugLogger("downloadAllReports");
		List<ScQtFinalizationReportAttachment> scAttachments = new ArrayList<>();
		List<ScQtFinalizationReport> scQtFinalizationReports = briefingPaperDisplayFilter(getManagedBean()
				.getScQtFinalization().getScQtFinalizationReports());
		for (ScQtFinalizationReport scQtFinalizationReport : scQtFinalizationReports) {
			ScQtFinalizationReportAttachment attachment;
			if (scQtFinalizationReport.getTitle().contains(QTConstants.QT_REPORT_BRIEFING_PAPER)) {
				attachment = retrieveBriefingPaperAttachment(scQtFinalizationReport, skipDownload);
			} else {
				attachment = scQtFinalizationReport.getScQtFinalizationReportAttachment();
			}
			if (null != attachment) {
				scAttachments.add(attachment);
			}
		}

		if (CollectionUtils.isNotEmpty(this.getManagedBean().getNewBriefingPaperReportList())) {
			// 38523 - end
			for (ScQtFinalizationReport uploadedSfReport : this.getManagedBean().getNewBriefingPaperReportList()) {
				if (uploadedSfReport.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD)
						&& null != uploadedSfReport.getScQtFinalizationReportAttachment()) {
					scAttachments.add(uploadedSfReport.getScQtFinalizationReportAttachment());
				}
			}
		}
		
		// Generate all the reports before submission
		if (!skipDownload) {// 38523
			downloadAllFiles("NGeP-Finalization-Report", scAttachments);
		}
		// #18685 - End.
	}
	
	/**
	 * @param scQtFinalizationReport
	 * @throws Exception
	 */
	public void downloadBriefingPaperReport(ScQtFinalizationReport scQtFinalizationReport) throws Exception {
		// 38523 - start
		downloadFile(retrieveBriefingPaperAttachment(scQtFinalizationReport, false));
		// 38523 - end
	}

	// 38523 - start
	private ScQtFinalizationReportAttachment retrieveBriefingPaperAttachment(
			ScQtFinalizationReport scQtFinalizationReport, boolean directDownload) throws Exception {
		ScQtFinalizationReport latestBriefingPaper = null;
		for (ScQtFinalizationReport report : getManagedBean().getFinalizationReports()) {
			boolean isLatest = latestBriefingPaper == null
					|| (report.getCreatedDate() != null && report.getCreatedDate().after(
							latestBriefingPaper.getCreatedDate()));
			if (report.getTitle().contains(QTConstants.QT_REPORT_BRIEFING_PAPER) && isLatest) {
				latestBriefingPaper = report;
			}
		}
		boolean isGenerateReport = false;
		if (latestBriefingPaper != null) {
			isGenerateReport = latestBriefingPaper.getTitle().equals(scQtFinalizationReport.getTitle());
		}
		ScQtFinalizationReportAttachment reportAttachment = scQtFinalizationReport
				.getScQtFinalizationReportAttachment();

		List<Long> allowedStatusIds = Arrays.asList(QTStatusConst.QT_CONSOLIDATION_REPORT,
				QTStatusConst.QT_PENDING_RECOMMENDATION_REVISE_BRIEFING_PAPER);

		if ((allowedStatusIds.contains(getManagedBean().getCurrentStatusId()) && isGenerateReport)
				|| null == reportAttachment) {
			reportAttachment = generateDocumentAttachment(scQtFinalizationReport);
			scQtFinalizationReport.setScQtFinalizationReportAttachment(reportAttachment);
		}
		return reportAttachment;
	}
	// 38523 - end
	
	/**
	 * To download all reports under Evaluation Report tab.
	 * 
	 * @throws Exception
	 *             the exception that would be thrown in case of any error while
	 *             downloading the report(s)
	 */
	public void downloadAllEvaluationReports() throws Exception {
		debugLogger("downloadAllEvaluationReports");

		List<ScQtFinalizationReport> scQtFinalizationReports = getManagedBean().getEvaluationReportList();
		// 37622 - start
		List<ScQtFinalizationReportAttachment> scAttachments = new ArrayList<>();
		for (ScQtFinalizationReport report : scQtFinalizationReports) {
			ScQtFinalizationReportAttachment attachment = report.getScQtFinalizationReportAttachment();
			if (attachment == null) {
				attachment = generateDocumentAttachment(report);
				report.setScQtFinalizationReportAttachment(attachment);
			}
			if (attachment != null) {
				attachment.setTempFileName(report.getTitle());
			}
			scAttachments.add(attachment);
		}
		// 37622 - end

		// Generate all the reports before submission
		if (!getManagedBean().isSubmit()) {
			downloadAllFiles("NGeP-Evaluation-Report", scAttachments);
		}
	}
	
	/**
	 * save consolidationReport Details.
	 * 
	 * 
	 */
	public void saveAction() {
		SuppFinalizationReqDTO reqDTO = prepareDataToSave();
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;

		try {
			// #18685 - Start.
			resetUnSavedReportContent(this.getManagedBean().getNewBriefingPaperReportList());
			// #18685 - End.
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			// #18685 - Start
			if (CollectionUtils.isNotEmpty(getManagedBean().getNewBriefingPaperReportList())
					&& CollectionUtils.isNotEmpty(getManagedBean().getSfBriefingPaperReportList())) {
				getManagedBean().getNewBriefingPaperReportList().addAll(
						getManagedBean().getSfBriefingPaperReportList());
			}
			// #18685 - End
			
			// #18685 - Start.
			reqDTO.setAddNewBriefingPaperReportList(getManagedBean().getNewBriefingPaperReportList());
			if (CollectionUtils.isNotEmpty(getManagedBean().getFinalizationItemizedRemarks())) {
				reqDTO.setFinalizationItemizedRemarks(getManagedBean().getFinalizationItemizedRemarks());
			}
	         // #18685 - End.
			
			if (CollectionUtils.isNotEmpty(getManagedBean().getFinalizationUlasanremarks())) {
              for (ScQtRecommendationUlasanRemarks remark : getManagedBean().getFinalizationUlasanremarks()) {
                  if (StringUtils.isNotEmpty(remark.getRemark())) {
                      remark.setSourceName(CommonConstants.BRIEFING_PAPER);
                  }
              }
			}
			// #18685 - End 
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.saveConsolidationReport(reqDTO);
			//saveEvaluationReportsDetaills();
			
            // #18685 - Start
            /*for (ScQtFinalizationRemarks updatedRemark : resDTO.getScQtFinalization().getScQtFinalizationRemarks()) {
              for (ScQtFinalizationRemarks remark : getManagedBean().getFinalizationItemizedRemarks()) {
                if (updatedRemark.getRemark().equalsIgnoreCase(remark.getRemark()) && CommonConstants.ITEMIZED.equals(remark.getSourceName())) {
                    getManagedBean().getFinalizationItemizedRemarks().set(0, updatedRemark);
                }
              }
            }*/
            // #18685 - End
            
			if (resDTO.isSuccessful()) {
				renewSessionAndRetrieve(getManagedBean().getActiveIndex());

				String qtNo = getManagedBean().getScQt().getQtNo().toString();
				successMessage(MessageConst.INFO_700004, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION, qtNo, Boolean.FALSE);
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	/**
	 * submit Action Details.
	 * 
	 * 
	 */
	public void submitAction() {
		// Validate Desk officer ranking
		// #30874 - Start.
		//#32401 - start comment to enable validation for pending recommendation (revise)
		//28834 start
		  /*if (null != getManagedBean().getCurrentStatusId() 
					&& (getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.QT_PENDING_RECOMMENDATION_REVISE_BRIEFING_PAPER.longValue())) {
				
			} else {
			     validateConsolidationReport();    
					
			}*/
		//28834 end
		//#32401 - end comment
				
		//#32401 - start replace comment above with this.
			validateConsolidationReport();
		//#32401 - end
	   
	    // #30874 - End.
		RequestContext.getCurrentInstance().execute("submissionPopup.show()");

	}
	
	//haziq -Bug #31364 - start
	public void preSubmitConsolidationReport() {
		IQTSuppFinalizationSO qtSuppFinalizationSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSuppFinalizationSO.class);
	
		SuppFinalizationReqDTO  reqDTO  = new SuppFinalizationReqDTO ();
	
		reqDTO.setScQt(this.getManagedBean().getScQt());
		System.out.println("getFinalizationCommitte " + this.getManagedBean().getScQt().getFinalizationCommitte() );
		System.out.println("getFinalizationSecratery " + this.getManagedBean().getScQt().getFinalizationSecratery() );
	
		
		SuppFinalizationResDTO  suppFinalizationResDTO = qtSuppFinalizationSO.checkNonActiveComAndsetNull(reqDTO, false);
		
		if (suppFinalizationResDTO.isSetNull()){
			this.getManagedBean().setSetNull(true);
			//this.getManagedBean().setScQt(suppFinalizationResDTO.getScQt());
			RequestContext.getCurrentInstance().execute("submissionPopupAndNewCom.show()");
		}
		else{
			this.getManagedBean().setSetNull(false);
			submitConsolidationReport();
		}
	}
	//haziq -Bug #31364 - End

	/**
	 * submit consolidationReport Details.
	 * 
	 * 
	 */
	//28834 starts
	public void submitConsolidationReport() {

		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		SuppFinalizationReqDTO reqDTO = prepareDataToSave();

		try {
			// #27502 -- Start
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, //haziq -Bug #31364
					IQTSuppFinalizationSO.class);	
			ScQt scQt = this.getManagedBean().getScQt();
			
			if  (this.getManagedBean().isSetNull()){ ////haziq -Bug #31364
				reqDTO.setScQt(scQt);	
				SuppFinalizationResDTO  suppFinalizationResDTO = qtSuppFinalizationSO.checkNonActiveComAndsetNull(reqDTO, true);
				scQt = suppFinalizationResDTO.getScQt();
			}
			
			QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
			qtBackingBean.getManagedBean().setQt(null);//#38191
			qtBackingBean.retrieveQuationTenderDetails(scQt.getQtId());

			validateEvaluationReports(SourcingConstants.CONSOLIDATION_TASK);


			// BPM Params
			FacesScopes facesScopes = new FacesScopes();
			reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setOutcome(getManagedBean().getOutcome());
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setPmUser(facesScopes.getUserSessionBean());
			// reqDTO.getScQtFinalization().setBriefingPaperUploaded(false);
			// #18685 - Start
			if (CollectionUtils.isNotEmpty(getManagedBean().getNewBriefingPaperReportList())
					&& CollectionUtils.isNotEmpty(getManagedBean().getSfBriefingPaperReportList())) {
				getManagedBean().getNewBriefingPaperReportList().addAll(
						getManagedBean().getSfBriefingPaperReportList());
			}
			
			for (ScQtFinalizationReport scQtFinalizationReport : getManagedBean().getNewBriefingPaperReportList()) {
				scQtFinalizationReport.setPreviousReportContent(scQtFinalizationReport.getReportContent());
			}
			// #18685 - End
			
			// #18685 - Start.
			reqDTO.setAddNewBriefingPaperReportList(getManagedBean().getNewBriefingPaperReportList());
			if (CollectionUtils.isNotEmpty(getManagedBean().getFinalizationItemizedRemarks())) {
              reqDTO.setFinalizationItemizedRemarks(getManagedBean().getFinalizationItemizedRemarks());
            }
	         // #18685 - End.
			//this.downloadAllBriefingPaperReports();

			//#27502 -- Start
			reqDTO.setScQt(qtBackingBean.getManagedBean().getQt());
			//#27502 -- End
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.submitConsolidationReport(reqDTO);
			if (resDTO.isSuccessful()) {

				String submitMessage = ResourceBundleUtils.getMessage(
						"lbl.sc.consolidation.report.submit.message",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale());
				String qtParam = getParameterDesc(
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION)
						+ " " + getManagedBean().getScQt().getQtNo();

				commonMessageAcknowledmentPage(MessageConst.INFO_700070,
						submitMessage, qtParam);
			}

		}finally {
			qtSuppFinalizationSO = null;
		}
	}
	//28834 ends

	/** End of Public Methods **/

	/** Private Methods **/

	private void loadInitialData() {
		retrieveQTDetails();
		retrieveSupplierFinalizationDtls();
		retrieveBriefingPaper();
		// #18685 - Start.
		//initializeFinalizationUlasanRemarks();
        // #18685 - End.
		//#21695 Farid start
		retrieveSupplierOfferOrNotOffer();
		//#21695 end
	}

	private void setZoneAndTabIndexOnException(boolean isItemized,
			TplSpecZoneDtl zoneDtl) {
		List<TplSpecZoneDtl> specZoneDtls = this.getManagedBean()
				.getZoneDetails();
		if (zoneDtl != null && CollectionUtils.isNotEmpty(specZoneDtls)) {
			zoneDtl = getManagedBean().getZoneMap()
					.get(zoneDtl.getSpecZoneId());
			getManagedBean().setSelectedZone(zoneDtl);
			getManagedBean().setZoneActiveIndex(zoneDtl.getZoneTabIndex());
		}

		if (isItemized) {
			List<ScQtRecommend> itemList = null;
			if (zoneDtl != null) {
				Map<Long, List<ScQtRecommend>> itemsByZone = getManagedBean()
						.getScQtFinalization().getScQtRecommendsByZone();
				itemList = itemsByZone.get(zoneDtl.getSpecZoneId());
			} else {
				itemList = getManagedBean().getScQtFinalization()
						.getScQtRecommends();
			}
			//#32398 - Start
			sortingItemizedFollowedOrderByQtId(itemList,this.getManagedBean().getScQt());
			//#32398 - End
			getManagedBean().setItemsList(itemList);
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
		SCEvaluationDataType dataType = (SCEvaluationDataType) getManagedBean()
				.getPayLoad();
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
		// #18685 - Start.
		getManagedBean().setDocNo(getManagedBean().getScQt().getQtNo());
		// #18685 - End.

		onTabMenuChangeAction(activeIndex, false);
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

			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.retrieveQTDtls(reqDTO);
			ScQt scQt = resDTO.getScQt();
			populateParentOrgProfile(scQt);
			getManagedBean().setScQt(scQt);
			evaluateWIP(scQt);//34865,34866,34867 - 29021 WIP	
			checkWeightage35384WIP(); //#38151

			boolean isMedicalProcCategory = isMultipleProposalCategory(scQt,
					null);
			if (isMedicalProcCategory) {
				if (SourcingConstants.EVALUATE_BY_BOTH_PACKAGE_ITEM
						.equalsIgnoreCase(this.getManagedBean().getScQt()
								.getEvaluateBy())) {
					this.getManagedBean().getScQt()
							.setEvaluateBy(SourcingConstants.EVALUATE_BY__ITEM);
				}
			}
			// getManagedBean().setProcCategoryMedicine(isMedicalProcCategory);

			List<TplSpecZoneDtl> zoneDetails = resDTO.getZoneDetails();
			getManagedBean().setZoneDetails(zoneDetails);
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
//#21695 Farid Start -(19/5/2022 update whole method)
	private void retrieveSupplierOfferOrNotOffer() {
		ScQt scQt = getManagedBean().getScQt();
		IQTEvaluationSO evaluationSO = null;

		try {
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			ISearchBean<Object[]> searchBean = new SearchBean<Object[]>();
			reqDTO.setNotOfferSupplierSearchBean(searchBean);
			boolean isOfferAll = false;

			if (getManagedBean().getScQt().getEvaluateBy()
					.equalsIgnoreCase(SourcingConstants.EVALUATE_BY__ITEM)) {
				isOfferAll = true;
			}
			else if (getManagedBean()
					.getScQt()
					.getEvaluateBy()
					.equalsIgnoreCase(
							SourcingConstants.EVALUATE_BY_BOTH_PACKAGE_ITEM)) {

				int check = evaluationSO
						.retrieveOfferOrNotOfferSuppliers(reqDTO);
				if (check == 0) {
					isOfferAll = true;
				}
			}
			getManagedBean().setOfferAll(isOfferAll);
		} finally {
			evaluationSO = null;
		}
	}
//#21695 Farid end -(19/5/2022 update whole method)
	
	private void retrieveSupplierFinalizationDtls() {
		ScQt scQt = getManagedBean().getScQt();
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;

		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			PmUser user = new FacesScopes().getUserSessionBean();
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setScQt(scQt);
			reqDTO.setLoggedInUserId(user.getUserId());

			List<TplSpecZoneDtl> zoneDetails = getManagedBean()
					.getZoneDetails();
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
			}

			if (scQt.isZonal()) {
				createZoneMenuModel();
				populateScQtRecommendsByZone();
			} else {
				//getManagedBean().setItemsList(
				//		scQtFinalization.getScQtRecommends());
				//#32398 - Start
				getManagedBean().setItemsList(
						sortingItemizedFollowedOrderByQtId(
								scQtFinalization.getScQtRecommends(),scQt));
				//#32398 - End
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
			 //28834 start
			reqDTO.setCommitteeType(getManagedBean().getCommitteeType());
			//28834 ends
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.retrieveFinalizationReports(reqDTO);
			ScQtFinalization _scQtFinalization = resDTO.getScQtFinalization();

			List<ScQtFinalizationReport> finalizationReports = _scQtFinalization
					.getScQtFinalizationReports();
			// 18685 - START.
			List<String> reportDocDefList = new ArrayList<>();
			reportDocDefList.add(SourcingConstants.DOC_DEF_KEY_LATAR_BELAKANG_REPORT);
			reportDocDefList.add(SourcingConstants.DOC_DEF_KEY_TUJUAN_REPORT);
			reportDocDefList.add(SourcingConstants.DOC_DEF_KEY_METODOLOGI_PENILAIAN_REPORT);
			reportDocDefList.add(SourcingConstants.DOC_DEF_KEY_UDPUP_REPORT);
			checkKertasTaklimatReports(finalizationReports, reportDocDefList, SourcingConstants.CONSOLIDATION_TASK);
			scQtFinalization.setScQtFinalizationReports(finalizationReports);

			if (finalizationReports != null) {
				populateEvolutionReports(finalizationReports, getManagedBean().getScQt());
				this.getManagedBean().setFinalizationReports(finalizationReports);
				if (CollectionUtils.isEmpty(getManagedBean().getNewBriefingPaperReportList())) {
					saveNewBriefingPaperReportList(reportDocDefList, _scQtFinalization, qtId);
				}
			}
			
			//#28784
			if(this.getManagedBean().getScQt().isDemoRequired()) {
				this.getManagedBean().setDemoReq(true);
			}
			// 18685 - END.
			//retrieveReportTabDtl(getManagedBean().getScQt(), "KT", SourcingConstants.CONSOLIDATION_TASK);
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	private void calculateOfferedPrice(ScQtEvaluationResultDtl evalResultDtl,
			ScQtRecommend scQtRecommend
			// #21695 - #32446 - Start.
			, Long zoneId
			// #21695 - #32446 - End.
			) {
		ScRequestItem requestItem = scQtRecommend.getRequestItem();
		ScQtProposalItem proposalItem = fetchQtProposalItem(scQtRecommend,
				evalResultDtl
				// #21695 - #32446 - Start.
				, zoneId
				// #21695 - #32446 - End.
				);
		if (proposalItem != null) {
			BigDecimal offeredPrice = TemplateSpecHelper.getInstance()
					.calculateTotalAmount(requestItem.getTplSpecQuestion(),
							proposalItem.getOfferPrice(),
							scQtRecommend.getOrderQty());

			evalResultDtl.getQtEvaluationResult().setOfferedPrice(offeredPrice);
		}
	}

	private ScQtProposalItem fetchQtProposalItem(ScQtRecommend scQtRecommend,
			ScQtEvaluationResultDtl recommendedSupplier
			// #21695 - #32446 - Start.
			, Long zoneId
			// #21695 - #32446 - End.
			) {
		ScQtProposalItem qtProposalItem = null;
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			// #21695 - #32446 - Start.
			if (zoneId != null && zoneId !=0) {// #21695 - #32856 - Start.
				reqDTO.setSpecZoneId(zoneId);
			}
			// #21695 - #32446 - End.
			reqDTO.setScQtProposalAmount(recommendedSupplier
					.getProposalAmount());
			reqDTO.setRequestItemId(scQtRecommend.getRequestItem()
					.getRequestNoteItemId());
			boolean isPriceTypeTier = isItemPriceTypeTariffOrTier(scQtRecommend);
			reqDTO.setTierPricing(isPriceTypeTier);

			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.getQtProposalItem(reqDTO);
			qtProposalItem = resDTO.getScQtProposalItem();
		} finally {
			qtSuppFinalizationSO = null;
		}

		return qtProposalItem;
	}

	private boolean isItemPriceTypeTariffOrTier(ScQtRecommend scQtRecommend) {
		TplSpecQuestion specQuestion = scQtRecommend.getRequestItem()
				.getTplSpecQuestion();
		boolean isPriceTypeTariffOrTier = false;

		if (ParameterConst.PRICE_TYPE_TARIFF.equals(scQtRecommend
				.getRequestItem().getTplSpecQuestion().getClPriceTypeCode())) {
			isPriceTypeTariffOrTier = true;
		} else {
			isPriceTypeTariffOrTier = TemplateSpecHelper.getInstance()
					.isPriceTypeTier(
							specQuestion.getClPriceTypeCode()
									.getParameterCode());
		}

		return isPriceTypeTariffOrTier;
	}

	private void createZoneMenuModel() {
		List<TplSpecZoneDtl> zoneDtls = getManagedBean().getZoneDetails();
		if (CollectionUtils.isNotEmpty(zoneDtls)) {
			Map<Long, TplSpecZoneDtl> zoneMap = new HashMap<Long, TplSpecZoneDtl>();

			// Dynamically generating the tabs
			MenuModel zoneMenuModel = new DefaultMenuModel();
			if (CollectionUtils.isNotEmpty(zoneDtls)) {
				int zoneTabIndex = 0;
				for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
					specZoneDtl.setZoneTabIndex(zoneTabIndex);

					MenuItem zoneItem = new MenuItem();
					zoneItem.setValue(specZoneDtl.getZoneName().toUpperCase());
					zoneItem.setTransient(true);
					zoneItem.setPartialSubmit(true);
					zoneItem.setProcess("@this");
					zoneItem.setUpdate("@this");
					zoneItem.setOnstart("blockWidget.show();");
					zoneItem.setOncomplete("onZoneTabChangeAction([ {name : 'selectedZoneTabIndex', "
							+ "value : "
							+ zoneTabIndex
							+ "}]); blockWidget.hide();");
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
		Boolean isScQtRecommendByZoneNull = false;//#29466
		if (scQtFinalization != null) {
			Map<Long, List<ScQtRecommend>> scQtRecommendsByZone = scQtFinalization
					.getScQtRecommendsByZone();
			log.debug("scQtFinalization.getScQtRecommendsByZone() : "
					+ scQtRecommendsByZone);
			
			//#29466 - START
			/*Checking if each QtRecommendId() from scQtRecommendsByZone
			  for that selected zone is null
			*/
			for(ScQtRecommend scQtRecommendByZone: scQtRecommendsByZone.get(selectedZone.getSpecZoneId())){
				if (scQtRecommendByZone.getQtRecommendId() == null){
					isScQtRecommendByZoneNull = true;
					break;
				}
			}		
			
			/*If at least one QtRecommendId() from scQtRecommendsByZone is null, 
			  will be replaced and added from each scQtFinalization.getScQtRecommends() 
			  for that same selected zone
			*/
			if(isScQtRecommendByZoneNull) {
				log.info("Replacing null recommendId for zone: " + selectedZone.getSpecZoneId());
				scQtRecommendsByZone.get(selectedZone.getSpecZoneId()).clear();
				for(ScQtRecommend qtRecommend: scQtFinalization.getScQtRecommends()){
					if(selectedZone.getSpecZoneId().equals(qtRecommend.getTplSpecZoneDtl().getSpecZoneId())){
						scQtRecommendsByZone.get(selectedZone.getSpecZoneId()).add(qtRecommend);
					}
				}
			}
			//#29466 - END
			
			//#32092 - Start.
			//#32009 - related to #21695 - start
			if(scQtRecommendsByZone.size() > 0){
				Iterator<List<ScQtRecommend>> iterator = scQtRecommendsByZone.values().iterator();
				while (iterator.hasNext()) {
					List<ScQtRecommend> qtrecommendList = iterator.next();
					List<ScQtRecommend> toberemoveList = new ArrayList<ScQtRecommend>();
					if (CollectionUtils.isNotEmpty(qtrecommendList)) {
						//#32243 - add zone condition for item qty = 0 - start
						for (ScQtRecommend qtrecommend : qtrecommendList) {
							if(qtrecommend.getTplSpecZoneDtl().getSpecZoneId().equals(selectedZone.getSpecZoneId())
									&& qtrecommend.getOrderQty().compareTo(BigDecimal.ZERO) == 0){
								toberemoveList.add(qtrecommend);
							}
						}
						if(CollectionUtils.isNotEmpty(toberemoveList)){
							qtrecommendList.removeAll(toberemoveList);	
							
						}
						//#32243 - add zone condition for item qty = 0 - end
					}
				}
				/*for(List<ScQtRecommend> scQtRecommend: scQtRecommendsByZone.values()){
					for(ScQtRecommend qtrecommend: scQtRecommend){
						if(qtrecommend.getOrderQty().compareTo(BigDecimal.ZERO) == 0){
							scQtRecommend.remove(qtrecommend);
						}
					}	
				}*/
			}
			//#32009 - related to #21695 - end
			//#32092 - End.
			if (selectedZone != null && scQtRecommendsByZone != null
					&& scQtRecommendsByZone.size() > 0) {
				//getManagedBean().setItemsList(
				//		scQtRecommendsByZone.get(selectedZone.getSpecZoneId()));
				//#32398 - Start
				getManagedBean().setItemsList(
						sortingItemizedFollowedOrderByQtId(
								scQtRecommendsByZone.get(selectedZone.getSpecZoneId()),this.getManagedBean().getScQt()));
				//#32398 - End
			}
		}
	}

	private SuppFinalizationResDTO getQualifiedSuppliersWithDORank(
			TplSpecZoneDtl specZone, ScQtRecommend scQtRecommend) {
		SuppFinalizationResDTO resDTO = null;
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(getManagedBean().getScQt().getQtId());
//			reqDTO.setDoRankRequired(true); // 26247
			reqDTO.setCountOfQualiedSuppliersReq(true);

			if (specZone != null) {
				reqDTO.setSpecZoneId(specZone.getSpecZoneId());
			}
			if (scQtRecommend != null) {
				reqDTO.setItemize(true);
				reqDTO.setRequestItemId(scQtRecommend.getRequestItem()
						.getRequestNoteItemId());
			}

			resDTO = qtSuppFinalizationSO
					.retrieveQtEvaluationResultDtls(reqDTO);

			if (resDTO.isSuccessful()) {
				return resDTO;
			}
		} finally {
			qtSuppFinalizationSO = null;
		}

		return null;
	}

	private void initializeRemarks() {
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		
		// #18685 - Start
		if (null != scQtFinalization && CollectionUtils.isNotEmpty(scQtFinalization.getScQtFinalizationRemarks())) {
			Iterator<ScQtFinalizationRemarks> iterator = scQtFinalization.getScQtFinalizationRemarks().iterator();
			while (iterator.hasNext()) {
				ScQtFinalizationRemarks remark = iterator.next();
				if (CommonConstants.ITEMIZED.equals(remark.getSourceName())) {
					// #18685 - Start
					if (null == getManagedBean().getFinalizationItemizedRemarks()) {
						List<ScQtFinalizationRemarks> itemizedRemarks = new ArrayList<>();
						getManagedBean().setFinalizationItemizedRemarks(itemizedRemarks);
					}
					getManagedBean().getFinalizationItemizedRemarks().add(remark);
					iterator.remove();
					// #18685 - End
				}
			}
		}
		

		// Retrieve finalization remarks for Package Wise
		List<ScQtFinalizationRemarks> remarks = initializeRemarks(
				scQtFinalization, getManagedBean().getScQt()
						.getCurrentWorkFlowStatus(),
				scQtFinalization.getScQtFinalizationRemarks(),
				ScQtFinalizationRemarks.class);
		if (null != remarks && remarks.size() > 0) {
			for (ScQtFinalizationRemarks remark : remarks) {
				// #18685 - Start
				if (StringUtils.isEmpty(remark.getSourceName())) {
					remark.setSourceName(CommonConstants.PACKAGE_WISE);
				}
				// #18685 - End
			}
		}
		scQtFinalization.setScQtFinalizationRemarks(remarks);
		// Finalization Remarks list
		getManagedBean().setFinalizationRemars(remarks);
		
		// Retrieve finalization remarks for Itemized
		List<ScQtFinalizationRemarks> itemizedRemarks = initializeRemarks(
				scQtFinalization, getManagedBean().getScQt()
						.getCurrentWorkFlowStatus(),
						getManagedBean().getFinalizationItemizedRemarks(),
				ScQtFinalizationRemarks.class);
		if (null != itemizedRemarks && itemizedRemarks.size() > 0) {
			for (ScQtFinalizationRemarks remark : itemizedRemarks) {
				// #18685 - Start
				if (StringUtils.isEmpty(remark.getSourceName())) {
					remark.setSourceName(CommonConstants.ITEMIZED);
				}
				// #18685 - End
			}
		}
		//scQtFinalization.getScQtFinalizationRemarks().addAll(itemizedRemarks);
		getManagedBean().setFinalizationItemizedRemarks(itemizedRemarks);
		// #18685 - End
	}

	private void finalizeRemarks() {
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		if (scQtFinalization != null) {
			List<ScQtFinalizationRemarks> remarksList = super
					.finalizeRemarks(getManagedBean().getFinalizationRemars());
			scQtFinalization.setScQtFinalizationRemarks(remarksList);
			setUserDetails(scQtFinalization);
		}
	}

	private void validateConsolidationReport() {
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		ScQtFinalizationReport kertasTaklimatReport = null;
		if (CollectionUtils.isNotEmpty(scQtFinalization
				.getScQtFinalizationReports())) {
			for (ScQtFinalizationReport report : scQtFinalization
					.getScQtFinalizationReports()) {
				if (report.getPmDocument() != null
						&& SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT
								.equals(report.getPmDocument()
										.getDocDefinitionKey())) {
					kertasTaklimatReport = report;
				}
				if (report.getScQtFinalizationReportAttachment() == null
						&& ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
								.equals(report.getDefinationType()
										.getParameterCode())) {
					navigateToBriefingPaperTab();
					throw new EpException(MessageConst.ERROR_760415,
							new String[] { report.getTitle() });
				}
			}
		}

		boolean isZonal = getManagedBean().getScQt().isZonal();
		Map<Long, List<ScQtRecommend>> itemsByZone = scQtFinalization
				.getScQtRecommendsByZone();

		if (isZonal) {
			for (TplSpecZoneDtl specZoneDtl : getManagedBean().getZoneDetails()) {
				Long specZoneId = specZoneDtl.getSpecZoneId();
				if(itemsByZone!= null && CollectionUtils.isNotEmpty(itemsByZone.get(specZoneId))){
					validateDORanking(specZoneDtl, itemsByZone.get(specZoneId));
				}
			}
		} else {
			validateDORanking(null, scQtFinalization.getScQtRecommends());
		}
		// Commented below code for #18685.
		/*if (!scQtFinalization.isBriefingPaperUploaded()
				&& (kertasTaklimatReport != null && kertasTaklimatReport
						.getScQtFinalizationReportAttachment() == null)) {
			navigateToBriefingPaperTab();
			throw new EpException(MessageConst.ERROR_760452);
		}else{
			scQtFinalization.setBriefingPaperUploaded(true);
		}*/
	}

	private void validateDORanking(TplSpecZoneDtl specZoneDtl,
			List<ScQtRecommend> items) {
		List<ScQtEvaluationResultDtl> qualifiedSuppliers = null;
		List<QtEvaluationResultDtlBean> qualifiedSupplierBeans = null;
		// #31443 Farid Start 
		ScQt scQt = getManagedBean().getScQt();
		// #31443 Farid End
		SuppFinalizationResDTO resDTO = getQualifiedSuppliersWithDORank(
				specZoneDtl, null);

		int totalQualifiedSuppliers = 0;
		if (resDTO != null) {
			qualifiedSuppliers = resDTO.getQualifiedSupplierDetails();
			totalQualifiedSuppliers = resDTO.getTotalQualifiedSuppliers();
		}
		
		// #28777 & #29729 - Start
		if (scQt.isZonal() && specZoneDtl != null
				&& checkListZonesDisqualifiedSupplierBranchDeleted(scQt.getQtId(), specZoneDtl.getSpecZoneId())
				&& resDTO != null) {
			qualifiedSuppliers = resDTO.getListQualifiedSuppliersBranchDeleted();
			totalQualifiedSuppliers = resDTO.getTotalQualifiedSuppliersBranchDeleted();
		}
		if (!scQt.isZonal() && checkListDisqualifiedSupplierBranchDeleted(scQt.getQtId()) && resDTO != null) {
			qualifiedSuppliers = resDTO.getListQualifiedSuppliersBranchDeleted();
			totalQualifiedSuppliers = resDTO.getTotalQualifiedSuppliersBranchDeleted();
		}

		// #28777 & #29729 - End
		
		// 26247 - start
		int countDisabled = countSupplierWithDisciplinaryAction(qualifiedSuppliers);
		totalQualifiedSuppliers = totalQualifiedSuppliers - countDisabled;
		// 26247 - end
		// farid start bug #31556 related to #21695
		// exclude validation ranking if has not offer
				if(scQt!=null && !scQt.isPartial()){
					// Package wise validation
					if (SourcingConstants.EVALUATE_BY_BOTH_PACKAGE_ITEM
							.equalsIgnoreCase(this.getManagedBean().getScQt()
									.getEvaluateBy())) {
						qualifiedSupplierBeans = getQtEvaluationResultDtlBeansWithRank(
								specZoneDtl, null, qualifiedSuppliers);
						validateRanking(qualifiedSupplierBeans, false, null, specZoneDtl,
								totalQualifiedSuppliers);
					}
					// End of // Package wise validation
				}else if(scQt!=null && scQt.isPartial()){
					excludeValidatePartialRanking(specZoneDtl,items);
				}
		// farid End bug #31556 related to #21695
		// exclude validation ranking if has not offer	
				
		// Item wise validation
		List<ScQtEvaluationResultDtl> itemQualifiedSuppliers = null;
		if (CollectionUtils.isNotEmpty(items)) {
			for (ScQtRecommend item : items) {
				totalQualifiedSuppliers = 0;
				resDTO = getQualifiedSuppliersWithDORank(specZoneDtl, item);

				if (resDTO != null) {
					itemQualifiedSuppliers = resDTO
							.getQualifiedSupplierDetails();
					totalQualifiedSuppliers = resDTO
							.getTotalQualifiedSuppliers();
				}
				
				// #28777 & #29729 - Start
				if (scQt.isZonal() && specZoneDtl != null
						&& checkListZonesDisqualifiedSupplierBranchDeleted(scQt.getQtId(), specZoneDtl.getSpecZoneId())
						&& resDTO != null) {
					itemQualifiedSuppliers = resDTO.getListQualifiedSuppliersBranchDeleted();
					totalQualifiedSuppliers = resDTO.getTotalQualifiedSuppliersBranchDeleted();
				}
				if (!scQt.isZonal() && checkListDisqualifiedSupplierBranchDeleted(scQt.getQtId()) && resDTO != null) {
					itemQualifiedSuppliers = resDTO.getListQualifiedSuppliersBranchDeleted();
					totalQualifiedSuppliers = resDTO.getTotalQualifiedSuppliersBranchDeleted();
				}
				// #28777 & #29729 - End
				
				// 26247 - start
				int countItemDisabled = countSupplierWithDisciplinaryAction(qualifiedSuppliers);
				totalQualifiedSuppliers = totalQualifiedSuppliers - countItemDisabled;
				// 26247 - end
				
				qualifiedSupplierBeans = getQtEvaluationResultDtlBeansWithRank(
						specZoneDtl, item.getRequestItem(),
						itemQualifiedSuppliers);
				// #31443 Start -exclude validate if quantity 0					
				if(scQt!=null && !scQt.isPartial() && item.getOrderQty() != null && item.getOrderQty().compareTo(BigDecimal.ZERO) > 0){
					validateRanking(qualifiedSupplierBeans, true, item,
							specZoneDtl, totalQualifiedSuppliers);
				}
				else if(scQt!=null && scQt.isPartial()){
					excludeValidatePartialRanking(specZoneDtl,items);
				}
				// #31443 End 	
			}
		}

		// End of item wise validation
	}
	
	// 26247 - start
	private int countSupplierWithDisciplinaryAction(List<ScQtEvaluationResultDtl> evaluationResultDtls){
		int countItemDisabled = 0;
		if (CollectionUtils.isNotEmpty(evaluationResultDtls)) {
			for (ScQtEvaluationResultDtl resultDtl : evaluationResultDtls) {
				ScQtEvaluationResult scQtEvaluationResult = resultDtl.getQtEvaluationResult();
				ScQtProposal scQtProposal = scQtEvaluationResult.getScQtProposal();
				if (this.disabledQTProcess(scQtProposal.getScQtSupplier().getSmSupplier())) {
					countItemDisabled++;
				}
			}
		}
		return countItemDisabled;
	}
	// 26247 - end

	private void validateRanking(
			List<QtEvaluationResultDtlBean> qualifiedSupplierBeans,
			boolean isItemized, ScQtRecommend item, TplSpecZoneDtl specZoneDtl,
			int totalQualifiedSuppliers) {
		int totalRanksCount = 0;
		int minRankCount = totalQualifiedSuppliers;
		if (totalQualifiedSuppliers == 2) {
			minRankCount = 1;
		} else if (totalQualifiedSuppliers > 2) {
			minRankCount = 3;
		}

		int currentRank = 0;
		EpException epException = null;
		String expMsgForItem = StringUtils.EMPTY;

		if (isItemized) {
			expMsgForItem = "- Item : " + item.getRequestItem().getItemName();
		}

		if (CollectionUtils.isNotEmpty(qualifiedSupplierBeans)) {

			// Sort the suppliers as per the rank given by the desk officer
			// 26247 - start
			CustomUtils.sort(qualifiedSupplierBeans, new Comparator<QtEvaluationResultDtlBean>() {
				@Override
				public int compare(QtEvaluationResultDtlBean supplier1, QtEvaluationResultDtlBean supplier2) {
					BigDecimal rank1 = supplier1.getDeskOfficerRank();
					BigDecimal rank2 = supplier2.getDeskOfficerRank();
					if (rank1 == null && rank2 == null) {
						return 0;
					}
					if (rank1 == null) {
						return -1;
					}
					if (rank2 == null) {
						return 1;
					}
					return rank1.compareTo(rank2);
				}
			});
			// 26247 - end

			for (QtEvaluationResultDtlBean qualifiedSupp : qualifiedSupplierBeans) {
				BigDecimal deskOfficerRank = qualifiedSupp.getDeskOfficerRank();

				if (deskOfficerRank != null) {
					int doRank = deskOfficerRank.intValue();
					int evaluationRank = qualifiedSupp.getEvaluationRank();
					if (evaluationRank != doRank
							&& StringUtils.isBlank(qualifiedSupp
									.getDeskOfficerRemark())) {
						epException = new EpException(
								MessageConst.ERROR_760431,
								new String[] { expMsgForItem });
					}

					if (epException == null) {
						if (doRank == currentRank) {
							epException = new EpException(
									MessageConst.ERROR_760381, new String[] {
											String.valueOf(currentRank),
											expMsgForItem });
						} else if (doRank > totalQualifiedSuppliers) {
							epException = new EpException(
									MessageConst.ERROR_760382,
									new String[] {
											String.valueOf(totalQualifiedSuppliers),
											expMsgForItem });
						} else if (doRank != (currentRank + 1)) {
							if (currentRank == 0) {
								epException = new EpException(
										MessageConst.ERROR_760427,
										new String[] { expMsgForItem });
							} else {
								epException = new EpException(
										MessageConst.ERROR_760383,
										new String[] {
												String.valueOf(currentRank),
												expMsgForItem });
							}
						}
					}
					if (epException != null) {
						setZoneAndTabIndexOnException(isItemized, specZoneDtl);
						throw epException;
					}

					currentRank = doRank;
					++totalRanksCount;
				}
			}
		}

		if (totalRanksCount < minRankCount) {
			setZoneAndTabIndexOnException(isItemized, specZoneDtl);
			throw new EpException(MessageConst.ERROR_760384, new String[] {
					String.valueOf(minRankCount), expMsgForItem });
		}
	}

	private List<QtEvaluationResultDtlBean> getQtEvaluationResultDtlBeansWithRank(
			TplSpecZoneDtl specZoneDtl, ScRequestItem requestItem,
			List<ScQtEvaluationResultDtl> qualifiedSuppliers) {
		Long zoneId = null;
		if (specZoneDtl != null) {
			zoneId = specZoneDtl.getSpecZoneId();
		} else {
			zoneId = DEFAULT_ZONE;
		}

		Long itemId = null;
		if (requestItem != null) {
			itemId = requestItem.getRequestNoteItemId();
		} else {
			itemId = DEFAULT_ITEM;
		}

		List<QtEvaluationResultDtlBean> beanList = null;
		List<QtEvaluationResultDtlBean> cachedSuppliers = getCachedSuppliersFromMap(
				zoneId, itemId);

		if (CollectionUtils.isNotEmpty(qualifiedSuppliers)) {
			QtEvaluationResultDtlBean resultDtlBean = null;
			beanList = new ArrayList<QtEvaluationResultDtlBean>();

			for (ScQtEvaluationResultDtl evalResDtl : qualifiedSuppliers) {
				resultDtlBean = getCachedBeanFromList(cachedSuppliers,
						evalResDtl.getEvaluationResultDtlId());
				if (resultDtlBean == null) {
					resultDtlBean = new QtEvaluationResultDtlBean();
					resultDtlBean.setQtEvalResDtlId(evalResDtl
							.getEvaluationResultDtlId());
					resultDtlBean.setEvaluationRank(evalResDtl.getEvalRank());
					setRankNull(evalResDtl);//#21692
					resultDtlBean.setDeskOfficerRank(evalResDtl
							.getDeskOfficerRank());
					resultDtlBean.setDeskOfficerRemark(evalResDtl
							.getDeskOfficerRemark());
				}
				beanList.add(resultDtlBean);
			}
		} else {
			beanList = cachedSuppliers;
		}

		return beanList;
	}

	private SuppFinalizationReqDTO prepareDataToSave() {
		SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
		reqDTO.setScQtFinalization(getManagedBean().getScQtFinalization());
		reqDTO.setRemovedFinalizationReports(getManagedBean()
				.getDeletedFinalizationReports());

		List<QtEvaluationResultDtlBean> cachedSuppliers = new ArrayList<QtEvaluationResultDtlBean>();
		if (MapUtils.isNotEmpty(getManagedBean().getQualifiedSuppliersCache())) {
			for (List<QtEvaluationResultDtlBean> supplierList : getManagedBean()
					.getQualifiedSuppliersCache().values()) {
				if (CollectionUtils.isNotEmpty(supplierList)) {
					cachedSuppliers.addAll(supplierList);
				}
			}
		}
		reqDTO.setCachedSupplierBeans(cachedSuppliers);

		finalizeRemarks();
		finalizeUlasanremarks();
		return reqDTO;
	}

	private ScQtFinalizationReportAttachment generateDocumentAttachment(
			ScQtFinalizationReport scQtFinalizationReport) throws Exception {
		String definitionType = scQtFinalizationReport.getDefinationType()
				.getParameterCode();
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();

		ScQtFinalizationReportAttachment reportAttachment = null;
		// Commented this condition for #18685.
		/*if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
				.equals(definitionType)) {*/
			String docDefinitionKey = scQtFinalizationReport.getPmDocument()
					.getDocDefinitionKey();
			ScQt scQt = getManagedBean().getScQt();
			reportAttachment = new ScQtFinalizationReportAttachment();
			reportAttachment.setScQtFinalizationReport(scQtFinalizationReport);
			reportAttachment.setDocNo(scQtFinalization.getScQt().getQtNo());
			StringBuilder filename = new StringBuilder();
			// replace white space due to firefox
			filename.append(scQtFinalizationReport
					.getTitle()
					.replaceAll(SourcingConstants.REG_EXP_WHITE_SPACE,
							SourcingConstants.UNDER_SCORE)
					.replaceAll(SourcingConstants.REG_EXP_BACK_SLASH,
							SourcingConstants.UNDER_SCORE));

			if (SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT
					.equals(docDefinitionKey)) {
				filename.append(".rtf");
				reportAttachment.setFileName(filename.toString());
				generateKertasTaklimatPdf(scQt, reportAttachment);
				// scQtFinalizationReport.setScQtFinalizationReportAttachment(reportAttachment);
			} else {
				debugLogger("No document type exists with the key "
						+ docDefinitionKey);
				reportAttachment = null;
			}

		/*}*/
		return reportAttachment;
	}

	private void generateKertasTaklimatPdf(ScQt qt,
			ScQtFinalizationReportAttachment attachment) throws Exception {
		debugLogger("generateKertasTaklimatPdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		// #35015 4ser -Start
		IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

		long qtId = qt.getQtId();
		EvaluationReqDTO req4SerDTO = new EvaluationReqDTO();
		req4SerDTO.setQtId(qtId);
		int wipstatus = evaluationSO.checkWipScoreSetting(req4SerDTO);
		String reportName = "";
		if (wipstatus <= ParameterConst.FLAG_WIP_STATUS_3SERANGKAI_BEFORE) {
			reportName = ReportConst.SC_SUPPLIER_FINALIZATION_RPT_WIP_JSP;
		} else {
			reportName = ReportConst.SC_SUPPLIER_FINALIZATION_RPT_JSP;
		}
		// #35015 4ser -End

		String estimatedAmountStr = null;

		if (qt.getEstimatedAmount() != null) {
			estimatedAmountStr = qt.getEstimatedAmount().toString();
		} else {
			IQTDetailSO qtDetailSO = null;
			try {
				qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				BigDecimal estimatedAmount = qtDetailSO
						.getdecryptedEstAmount(qtId);
				estimatedAmountStr = estimatedAmount.toPlainString();
			} finally {
				qtDetailSO = null;
			}
		}
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
				.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_ESTIMATED_AMOUNT, estimatedAmountStr);
		paramMap.put(ReportConst.PARAM_REPORT_BUFFERS, String.valueOf(ReportConst.BUFFER_SIZE)); //Bug #33714
		StringBuilder ulasan = new StringBuilder();
		List<ScQtRecommendationUlasanRemarks> ulasanRemarks = this
				.getManagedBean().getFinalizationUlasanremarks();
		if (CollectionUtils.isNotEmpty(ulasanRemarks)) {
			log.info("ulasanremarknotempty");
			for (ScQtRecommendationUlasanRemarks remark : ulasanRemarks) {
				if (StringUtils.isNotBlank(remark.getRemark())) {
					log.info("ulasanremark" + remark.getRemark());
					ulasan.append(remark.getRemark() + "\n");
				}
			}
		}
		paramMap.put(ReportConst.PARAM_REMARK,
				ulasan.length() == 0 ? StringUtils.EMPTY : ulasan.toString());
		paramMap.put(ReportConst.PARAM_REPORT_OUTPUT_FORMAT, "rtf");
		// #18685 - Start
		paramMap.put("flowType", "tasklist");
		// #18685 - End
		generatePdfFromOracle(reportName, paramMap, attachment, false);
	}

	private void navigateToBriefingPaperTab() {
		getManagedBean().setActiveIndex(BRIEFING_PAPER_TAB_INDEX);
	}

	private void navigateToItemizedTab() {
		getManagedBean().setItemized(true);
		getManagedBean().setActiveIndex(ITEMIZED_TYPE_TAB_INDEX);
		getManagedBean().setQualifiedSuppliersDataModel(null);
		getManagedBean().setDisQualifiedSuppliersDataModel(null);// #28777&#29729
		//#21695 START - Fix bug reload data changing tab package and item
		if(getManagedBean().getScQt().isZonal()){
			TplSpecZoneDtl selectedZone = getManagedBean().getZoneDetails().get(0);
			getManagedBean().setSelectedZone(selectedZone);
			getManagedBean().setZoneActiveIndex(0);
			populateScQtRecommendsByZone();
		}
		// #21695 END
		}

	private void navigateToPackagewiseTab() {
		getManagedBean().setItemized(false);

		Long selectedZoneId = null;
		if (getManagedBean().getScQt().isZonal()) {
			selectedZoneId = getManagedBean().getSelectedZone().getSpecZoneId();
		}

		getManagedBean().setQualifiedSuppliersDataModel(
				new QualifiedSuppliersPagingDataModel("qualifiedSuppliers",
						getManagedBean().getScQt().getQtId(),
						SourcingConstants.EVAL_STAGE_RECOMMENDATION_PKG,
						selectedZoneId, null));
		
		// #28777&#29729 - Start
		Long qtId = this.getManagedBean().getScQt().getQtId();
		List<Long> quaQtSupplierIdList = retrieveZoneQtSuppliersIdsByQtIdAndSpecZoneId(true, qtId, selectedZoneId);
		if (CollectionUtils.isEmpty(quaQtSupplierIdList) && selectedZoneId != null) {
			getManagedBean().setQualifiedSuppliersDataModel(null);
		}

		getManagedBean().setDisQualifiedSuppliersDataModel(
				new DisqualifiedSuppliersPagingDataModel("disqualifiedSuppliers", qtId,
						SourcingConstants.EVAL_STAGE_RECOMMENDATION_PKG, selectedZoneId, null));
		// #28777&#29729 - End
	
		getManagedBean().setActiveIndex(PACKAGE_TYPE_TAB_INDEX);
	}

	private void commonMessageAcknowledmentPage(String messageId,
			String variable1, String variable2) {
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(messageId, new String[] { variable1,
								variable2 },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

		// clear data
		super.renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
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
	 * Get briefingPaerTabIndex.
	 * 
	 * @return the BRIEFING_PAPER_TAB_INDEX
	 */
	public static int getBRIEFING_PAPER_TAB_INDEX() {
		return BRIEFING_PAPER_TAB_INDEX;
	}

	/**
	 * Get ItemizedTypeTabIndex.
	 * 
	 * @return the ITEMIZED_TYPE_TAB_INDEX
	 */
	public static int getITEMIZED_TYPE_TAB_INDEX() {
		return ITEMIZED_TYPE_TAB_INDEX;
	}

	/**
	 * Get PackageTypeTabIndex.
	 * 
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
		 * @param totalCompliant
		 *            the totalCompliant to set
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
		 * @param totalNotCompliant
		 *            the totalNotCompliant to set
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
		 * @param totalSubmitted
		 *            the totalSubmitted to set
		 */
		public void setTotalSubmitted(int totalSubmitted) {
			this.totalSubmitted = totalSubmitted;
		}
	}

	/**
	 * Qualified Suppliers Paging Data Model
	 * 
	 * @author Praneeth
	 * @since 04.08.2016
	 */
	private class QualifiedSuppliersPagingDataModel extends
			PagingDataModel<ScQtEvaluationResultDtl> {

		private static final long serialVersionUID = 1L;

		private Long qtId;
		private String evaluationStage;
		private Long selectedZoneId;
		private ScQtRecommend selectedItem;

		/**
		 * Constructor for Summary Supplier Paging Data Model
		 */
		public QualifiedSuppliersPagingDataModel(String componentId, Long qtId,
				String evaluationStage, Long selectedZoneId,
				ScQtRecommend selectedItem) {
			super(componentId);
			this.qtId = qtId;
			this.evaluationStage = evaluationStage;
			this.selectedZoneId = selectedZoneId;
			this.selectedItem = selectedItem;

			this.resetData();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQtEvaluationResultDtl> pageChangeListener(
				ISearchBean<ScQtEvaluationResultDtl> searchBean) {
			ISearchResultBean<ScQtEvaluationResultDtl> resultBean = null;
			boolean flagQualifiedSupplier = Boolean.TRUE;
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			resultBean = commonQualifiedSuppliersPagingDataModel(searchBean, resultBean, qtId, evaluationStage,
					selectedZoneId, selectedItem, flagQualifiedSupplier);// #28777&#29729
			return resultBean;
		}	
	}
	
	// #28777 & #29729 - Start
	/**
	 * Disqualified Suppliers Paging Data Model
	 */
	private class DisqualifiedSuppliersPagingDataModel extends PagingDataModel<ScQtEvaluationResultDtl> {

		private static final long serialVersionUID = 1L;
		private Long qtId;
		private String evaluationStage;
		private Long selectedZoneId;
		private ScQtRecommend selectedItem;

		/**
		 * Constructor for Summary Supplier Paging Data Model
		 */
		public DisqualifiedSuppliersPagingDataModel(String componentId, Long qtId, String evaluationStage,
				Long selectedZoneId, ScQtRecommend selectedItem) {
			super(componentId);
			this.qtId = qtId;
			this.evaluationStage = evaluationStage;
			this.selectedZoneId = selectedZoneId;
			this.selectedItem = selectedItem;

			this.resetData();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQtEvaluationResultDtl> pageChangeListener(
				ISearchBean<ScQtEvaluationResultDtl> searchBean) {
			ISearchResultBean<ScQtEvaluationResultDtl> resultBean = null;
			boolean flagQualifiedSupplier = Boolean.FALSE;
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			resultBean = commonQualifiedSuppliersPagingDataModel(searchBean, resultBean, qtId, evaluationStage,
					selectedZoneId, selectedItem, flagQualifiedSupplier);
			return resultBean;
		}
	}

	// #28777 & #29729 - End
	
	/**
	 * @param searchBean
	 * @param resultBean
	 * @return
	 */
	private ISearchResultBean<ScQtEvaluationResultDtl> commonQualifiedSuppliersPagingDataModel(
			ISearchBean<ScQtEvaluationResultDtl> searchBean, ISearchResultBean<ScQtEvaluationResultDtl> resultBean,
			Long qtId, String evaluationStage, Long selectedZoneId, ScQtRecommend selectedItem,
			boolean flagQualifiedSupplier) {// #28777 & #29729
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("qtEvaluationResult.evaluationStage",
						SearchOperator.OPERATOR_EQUALS.value(),
						evaluationStage));
		
		// #28777 & #29729 - Start
		if (flagQualifiedSupplier) {
			List<Long> quaQtSupplierIdList = retrieveZoneQtSuppliersIdsByQtIdAndSpecZoneId(true, qtId, selectedZoneId);
			if (CollectionUtils.isNotEmpty(quaQtSupplierIdList) && selectedZoneId != null) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("qtEvaluationResult.scQtProposal.scQtSupplier.qtSupplierId",
								SearchOperator.OPERATOR_IN.value(), quaQtSupplierIdList));
			} else {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("qtEvaluationResult.scQtProposal.scQtSupplier.recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(), DbValueConst.RECORD_STATUS_ACTIVE));
			}
		} else {
			List<Long> disQtSupplierIdList = retrieveZoneQtSuppliersIdsByQtIdAndSpecZoneId(false, qtId, selectedZoneId);
			if (CollectionUtils.isNotEmpty(disQtSupplierIdList) && selectedZoneId != null) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("qtEvaluationResult.scQtProposal.scQtSupplier.qtSupplierId",
								SearchOperator.OPERATOR_IN.value(), disQtSupplierIdList));
			} else {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("qtEvaluationResult.scQtProposal.scQtSupplier.recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(), SourcingConstants.RECORD_STATUS_DISQUALIFIED));
			}
		}
		// #28777 & #29729 - End

		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setSearchBean(searchBean);
			reqDTO.setQtId(qtId);
			if (!DEFAULT_ZONE.equals(selectedZoneId)) {
				reqDTO.setSpecZoneId(selectedZoneId);
			}
			Long selectedItemId = null;
			if (null != selectedItem) {
				selectedItemId = selectedItem.getRequestItem()
						.getRequestNoteItemId();
				reqDTO.setRequestItemId(selectedItemId);
			}
			/*
			 * Development 32311: SLA LOG START
			 */
			long startTime = System.currentTimeMillis();
			/*
			 * BUG 32373 #83
			 */
			reqDTO.setEvaluationStage(evaluationStage); //32373 #83
			/*
			 * BUG 32373 #83
			 */
			//#36239 4serangkai -adding to check zonal
			if(null != selectedZoneId){
				reqDTO.setZonal(true);
			}
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.retrieveQualifiedSuppliersInfoByEvalStage(reqDTO);
			SLAUtils.getInstance().logSQLPerformanceInfo("QT","32311(B)","QTSuppFinalizationSO.retrieveQualifiedSuppliersInfoByEvalStage", startTime,"#32311");
			/*
			 * Development 32311: SLA LOG END
			 */
			if (resDTO.isSuccessful()) {
				resultBean = resDTO.getQualifiedSuppliersByEvalStage();
				List<ScQtEvaluationResultDtl> disqualifiedSuppliers = resultBean
						.getResultList();
				Long zoneId = (selectedZoneId == null ? DEFAULT_ZONE
						: selectedZoneId);
				Long itemId = (selectedItemId == null ? DEFAULT_ITEM
						: selectedItemId);
				List<QtEvaluationResultDtlBean> cachedSuppliers = getCachedSuppliersFromMap(
						zoneId, itemId);

				if (CollectionUtils.isNotEmpty(disqualifiedSuppliers)) {
					for (ScQtEvaluationResultDtl qualSupplier : disqualifiedSuppliers) {
						if (selectedItem != null) {
							calculateOfferedPrice(qualSupplier,
									selectedItem
									// #21695 - #32446 - Start.
									, zoneId
									// #21695 - #32446 - End.
									);
						}
						if (CollectionUtils.isNotEmpty(cachedSuppliers)) {
							QtEvaluationResultDtlBean recommendationBean = getCachedBeanFromList(
									cachedSuppliers,
									qualSupplier.getEvaluationResultDtlId());
							if (recommendationBean != null) {
								qualSupplier
										.setDeskOfficerRank(recommendationBean
												.getDeskOfficerRank());
								qualSupplier
										.setDeskOfficerRemark(recommendationBean
												.getDeskOfficerRemark());
							}
						}
						setRankNull(qualSupplier);//#21692
					}
					
				}
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
		return resultBean;
	}

	private List<QtEvaluationResultDtlBean> getCachedSuppliersFromMap(
			Long zoneId, Long itemId) {
		if (zoneId == null && itemId == null) {
			return null;
		}

		List<QtEvaluationResultDtlBean> cachedSuppliersList = null;
		Map<MapKey, List<QtEvaluationResultDtlBean>> cachedSupplierBeans = getManagedBean()
				.getQualifiedSuppliersCache();
		MapKey mapKey = new MapKey(zoneId, itemId);

		if (cachedSupplierBeans == null) {
			cachedSupplierBeans = new HashMap<MapKey, List<QtEvaluationResultDtlBean>>();
			cachedSuppliersList = new ArrayList<QtEvaluationResultDtlBean>();
			cachedSupplierBeans.put(mapKey, cachedSuppliersList);

			getManagedBean().setQualifiedSuppliersCache(cachedSupplierBeans);
		} else if (cachedSupplierBeans.containsKey(mapKey)) {
			cachedSuppliersList = cachedSupplierBeans.get(mapKey);
		} else {
			cachedSuppliersList = new ArrayList<QtEvaluationResultDtlBean>();
			cachedSupplierBeans.put(mapKey, cachedSuppliersList);
		}

		return cachedSuppliersList;
	}

	private QtEvaluationResultDtlBean getCachedBeanFromList(
			List<QtEvaluationResultDtlBean> beanList, Long evaluationResultDtlId) {
		if (CollectionUtils.isNotEmpty(beanList)) {
			QtEvaluationResultDtlBean requiredBean = new QtEvaluationResultDtlBean();
			requiredBean.setQtEvalResDtlId(evaluationResultDtlId);

			if (beanList.contains(requiredBean)) {
				return beanList.get(beanList.indexOf(requiredBean));
			}
		}

		return null;
	}

	/**
	 * @method returns eval Type Item.
	 */
	public String evalTypeItem() {
		return SourcingConstants.EVALUATE_BY__ITEM;
	}

	/**
	 * Retrieve Qualified suppliers by evaluation stage
	 */
	//#38151 - Start
	public void retrieveQualifiedSuppliersInfoByEvalStage() {
	    retrieveQualifiedSuppliersInfoByEvalStage(null);
	}
	
	/**
	 * Retrieve Qualified suppliers by evaluation stage
	 */
	public void retrieveQualifiedSuppliersInfoByEvalStage(String tecfin) {//#38151
		//#38151 - Start
		String evalStageId = null;
		if(tecfin == null){
			 evalStageId = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap()
					.get("evaluationStageId");
		}else{
			evalStageId = tecfin;
		}
		//#38151 - End
		log.info("evalStageId" + evalStageId);
		int evaluationStageId = 0;
		if (evalStageId != null) {
			evaluationStageId = Integer.parseInt(evalStageId);
		}
		SupplierFinalizationBackingBean supplierFinalizationBB = new SupplierFinalizationBackingBean();
		supplierFinalizationBB.getManagedBean().setScQt(
				this.getManagedBean().getScQt());
		supplierFinalizationBB.getManagedBean().setItemize(Boolean.FALSE);
		supplierFinalizationBB.getManagedBean().setItemRowSelect(Boolean.FALSE);
		// supplierFinalizationBB.getManagedBean().setQualifiedSuppliersByEvalStage(null);
		retrievescoresettings(supplierFinalizationBB.getManagedBean().getScQt()); //#38151
		supplierFinalizationBB.getManagedBean().getScQt().setScQtScoreSettings(this.getManagedBean().getScQt().getScQtScoreSettings()); //#38151
		if (this.getManagedBean().getActiveIndex() == PACKAGE_TYPE_TAB_INDEX) {
			log.info("PACKAGE_TYPE_TAB_INDEX"
					+ this.getManagedBean().getActiveIndex());
			this.getManagedBean().setSelectedItem(null);
		} else if (this.getManagedBean().getActiveIndex() == ITEMIZED_TYPE_TAB_INDEX) {
			log.info("ITEMIZED_TYPE_TAB_INDEX"
					+ this.getManagedBean().getActiveIndex());
			//#21695 tec by itemize result pop up farid - start
			if (evaluationStageId == 2 || evaluationStageId == 1) {
			//#21695 tec by itemize result pop up farid - end
				supplierFinalizationBB.getManagedBean()
						.setItemize(Boolean.TRUE);
				if (null != this.getManagedBean().getSelectedItem()) {
					log.info("getSelectedItem"
							+ this.getManagedBean().getSelectedItem());
					supplierFinalizationBB.getManagedBean().setSelectedItem(
							this.getManagedBean().getSelectedItem());
					supplierFinalizationBB.getManagedBean().setItemRowSelect(
							Boolean.TRUE);
				}
			}

			log.info("setItemize"
					+ supplierFinalizationBB.getManagedBean().isItemize());
		}
		
		
		TplSpecZoneDtl specZoneDtl = this.getManagedBean().getSelectedZone();
		log.info("specZoneDtl" + specZoneDtl);
		//#21695 tec by itemize result pop up farid - start
		if(evaluationStageId==2 || evaluationStageId == 1){ //#26440 Start
		//#21695 tec by itemize result pop up farid - end
		supplierFinalizationBB.getManagedBean().setCurrentZone(specZoneDtl);
		} //#26440 End
		supplierFinalizationBB.getManagedBean().setCheckCategoryCodeQt(this.getManagedBean().getScQt());//#29466
		supplierFinalizationBB.retrieveQualifiedSuppliersInfoByEvalStage();
		this.getManagedBean().setSuppFinalBean(supplierFinalizationBB); //#38151

	}

	public void handleKertasFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {

		debugLogger("handleKertasFileUpload");
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScQtFinalizationReport scQtFinalizationReport = this
					.getManagedBean().getSelectedFinalizationReport();
			if (scQtFinalizationReport == null) {
				List<ScQtFinalizationReport> reports = this.getManagedBean()
						.getFinalizationReports();
				for (ScQtFinalizationReport report : reports) {
					if (SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT
							.equals(report.getPmDocument()
									.getDocDefinitionKey())) {
						scQtFinalizationReport = report;
					}
				}
			}
			ScQtFinalizationReportAttachment reportAttachment = scQtFinalizationReport
					.getScQtFinalizationReportAttachment();
			if (null == reportAttachment) {
				reportAttachment = new ScQtFinalizationReportAttachment();
				reportAttachment
						.setScQtFinalizationReport(scQtFinalizationReport);
				reportAttachment.setDocNo(getManagedBean().getScQt().getQtNo());
				reportAttachment.setFileName(event.getFile().getFileName());
				log.info("$$$$$ File Name " + event.getFile().getFileName());
				scQtFinalizationReport
						.setScQtFinalizationReportAttachment(reportAttachment);
				populateAttachmentDetails(reportAttachment);
			}
			reportAttachment.setScQtFinalizationReport(scQtFinalizationReport);
			scAttachment = reportAttachment;
			uploadAttachment(event.getFile(), scAttachment);
			this.getManagedBean().getScQtFinalization()
					.setBriefingPaperUploaded(true);
		} else {
			log.debug("$$$$$ File from event is null @ "
					+ DateUtils.getCurrentDate());
			log.debug("$$$$$ Qt id is: "
					+ this.getManagedBean().getScQtFinalization().getScQt()
							.getQtId());
		}
	}

	public void deleteKertasAttachment(BaseEntity delEntity) {

		ScQtFinalizationReport scQtFinalizationReport = this.getManagedBean()
				.getSelectedFinalizationReport();
		scQtFinalizationReport.setScQtFinalizationReportAttachment(null);
		this.getManagedBean().getScQtFinalization()
				.setBriefingPaperUploaded(false);

	}

	private void initializeFinalizationUlasanRemarks() {
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();

		// Retrieve finalization endorsement remarks
		List<ScQtRecommendationUlasanRemarks> remarks = initializeRemarks(
				scQtFinalization, scQtFinalization.getCurrentStatus(),
				scQtFinalization.getScQtRecommendationUlasanRemarks(),
				ScQtRecommendationUlasanRemarks.class);
		scQtFinalization.setScQtRecommendationUlasanRemarks(remarks);

		// Finalization Ulasan Remarks list
		getManagedBean().setFinalizationUlasanremarks(remarks);
	}

	private void finalizeUlasanremarks() {
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		List<ScQtRecommendationUlasanRemarks> ulasanRemarks = this
				.getManagedBean().getFinalizationUlasanremarks();
		if (CollectionUtils.isNotEmpty(ulasanRemarks)) {
			List<ScQtRecommendationUlasanRemarks> remarksList = super
					.finalizeRemarks(ulasanRemarks);
			scQtFinalization.setScQtRecommendationUlasanRemarks(remarksList);
			setUserDetails(scQtFinalization);
		}
	}

	public void addDocumentList(ScQt scqt) {
		log.debug("addDocumentList");
		List<ScQtFinalizationReport> newBPReportList = this.getManagedBean()
				.getNewBriefingPaperReportList();
		if (newBPReportList == null) {
			newBPReportList = new ArrayList<ScQtFinalizationReport>();
			//scQt.setScQtE(scQtDocReports);
		}
		ScQtFinalizationReport report = new ScQtFinalizationReport();
		report.setDocCategory(SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT);
		ScQtFinalization scQtFinalization = this.getManagedBean()
				.getScQtFinalization();
		if (scQtFinalization == null) {
			scQtFinalization = new ScQtFinalization();
		}
		report.setScQtFinalization(scQtFinalization);
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
			report.setDefinationType(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}
		newBPReportList.add(report);
	}
	
	/**
	 * @return the evaluationReportTabIndex
	 */
	public static int getEvaluationReportTabIndex() {
		return EVALUATION_REPORT_TAB_INDEX;
	}
	
	private void navigateToEvaluationReport() {
		getManagedBean().setActiveIndex(EVALUATION_REPORT_TAB_INDEX);
	}
	
	// #28777 & #29729 - Start
	/**
	 * Method to display list of supplier branch had deleted or not with related
	 * current QT
	 * 
	 * @param supplierId
	 **/
	public void showSupplierBranchList(Long supplierId) {

		ISupplierSearchSO supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);

		SupplierSearchReqDTO suppSearchDeletedReqDTO = new SupplierSearchReqDTO();
		suppSearchDeletedReqDTO.setSupplierId(supplierId);
		SupplierSearchResDTO suppSearchDeletedResDTO = supplierSearchSO
				.retrieveSmSupplierBranchDeletedBySupplier(suppSearchDeletedReqDTO);
		List<SmSupplierBranch> supplierBranchListDeleted = suppSearchDeletedResDTO.getSmSupplierBranchList();

		for (SmSupplierBranch supplierBranch : supplierBranchListDeleted) {
			int statusFlag = supplierBranch.getRecordStatusFlg();
			if (statusFlag == DbValueConst.RECORD_STATUS_DELETED) {
				supplierBranch.setQtRelatedSupplierBranch(DbValueConst.FLAG_YES);
			} else {
				supplierBranch.setQtRelatedSupplierBranch(DbValueConst.FLAG_NO);
			}
		}

		if (CollectionUtils.isEmpty(supplierBranchListDeleted)) {
			getManagedBean().setSupplierBranchList(listSupplierBranchList(supplierId));
		} else {
			supplierBranchListDeleted.addAll(listSupplierBranchList(supplierId));
			getManagedBean().setSupplierBranchList(supplierBranchListDeleted);
		}
	}

	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to display list of supplier branch
	 * 
	 * @param supplierId
	 **/
	private List<SmSupplierBranch> listSupplierBranchList(Long supplierId) {

		ISupplierSearchSO supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);

		SupplierSearchReqDTO suppSearchReqDTO = new SupplierSearchReqDTO();
		suppSearchReqDTO.setSupplierId(supplierId);
		SupplierSearchResDTO suppSearchResDTO = supplierSearchSO.retrieveSmSupplierBranchBySupplier(suppSearchReqDTO);
		return suppSearchResDTO.getSmSupplierBranchList();
	}

	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to display instruction when had supplier deleted branch
	 * 
	 * @param qtId
	 * @return true or false
	 **/
	public Boolean checkInstructionSupplierBranchDeleted(Long qtId) {
		ISourcingIntegrationSO sourcingIntegrationSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
		List<TplSpecZoneDtl> zoneDetails = this.getManagedBean().getZoneDetails();
		int zoneActiveIndex = this.getManagedBean().getZoneActiveIndex();
		boolean isZonal = this.getManagedBean().getScQt().isZonal();

		if (isZonal && CollectionUtils.isNotEmpty(zoneDetails) && zoneActiveIndex != -1
				&& zoneDetails.get(zoneActiveIndex).isBranchDeactivated()) {
			return true;
		}

		if (!isZonal && sourcingIntegrationSO.checkInstructionDisqualifiedSupplierBranchDeleted(qtId)) {
			return true;
		}

		return false;
	}

	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to check had disqualified supplier list
	 * 
	 * @param qtId
	 * @return true or false
	 **/
	public Boolean checkListDisqualifiedSupplierBranchDeleted(Long qtId) {
		ISourcingIntegrationSO sourcingIntegrationSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
		List<TplSpecZoneDtl> zoneDetails = this.getManagedBean().getZoneDetails();
		int zoneActiveIndex = this.getManagedBean().getZoneActiveIndex();
		boolean isZonal = this.getManagedBean().getScQt().isZonal();

		if (isZonal && CollectionUtils.isNotEmpty(zoneDetails) && zoneActiveIndex != -1
				&& zoneDetails.get(zoneActiveIndex).isBranchDeactivated()) {
			return true;
		}

		if (!isZonal && sourcingIntegrationSO.checkListDisqualifiedSupplierBranchDeleted(qtId)) {
			return true;
		}

		return false;
	}

	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to check list had disqualified zones
	 * 
	 * @param qtId
	 * @param specZoneId
	 * @return true or false
	 **/
	public Boolean checkListZonesDisqualifiedSupplierBranchDeleted(Long qtId, Long specZoneId) {
		ISourcingIntegrationSO sourcingIntegrationSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
		return sourcingIntegrationSO.checkListZonesDisqualifiedSupplierBranchDeleted(qtId, specZoneId);
	}

	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to retrieve qualified or disqualified zone for QtSuppliersId
	 * 
	 * @param isQualified
	 * @param qtId
	 * @param specZoneId
	 * @return List<Long>
	 **/
	public List<Long> retrieveZoneQtSuppliersIdsByQtIdAndSpecZoneId(boolean isQualified, Long qtId, Long specZoneId) {
		ISourcingIntegrationSO sourcingIntegrationSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
		return sourcingIntegrationSO.retrieveZoneQtSuppliersIdsByQtIdAndSpecZoneId(isQualified, qtId, specZoneId);
	}

	// #28777 & #29729 - End
	
	//#32398 - Start
	private List<ScQtRecommend> sortingItemizedFollowedOrderByQtId(List<ScQtRecommend> oldRequestItemsRecommend,ScQt scQt){
		
		SuppFinalizationReqDTO reqDTORecommend = new SuppFinalizationReqDTO();
		reqDTORecommend.setQtId(scQt.getQtId());
		List<ScQtRecommend> newRequestItemsRecommend = findScRequestItemizedByReqDTO(reqDTORecommend);
		final List<Long> sortingOrderRecommendIds = new ArrayList<>();
		
		if(CollectionUtils.isNotEmpty(newRequestItemsRecommend)){
			for(ScQtRecommend recommendIds :newRequestItemsRecommend){
				sortingOrderRecommendIds.add(recommendIds.getQtRecommendId());
			}
		}
		
		Collections.sort(oldRequestItemsRecommend, new Comparator<ScQtRecommend>() {
			public int compare(ScQtRecommend o1, ScQtRecommend o2) {
						return Integer.valueOf(
								sortingOrderRecommendIds.indexOf(o1.getQtRecommendId().longValue())).compareTo(
								Integer.valueOf(sortingOrderRecommendIds.indexOf(o2.getQtRecommendId().longValue())));
			}
		});	
		
		return oldRequestItemsRecommend;
	}
	
	public List<ScQtRecommend> findScRequestItemizedByReqDTO(SuppFinalizationReqDTO reqDTO) {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);	
			return qtSuppFinalizationSO.findScRequestItemizedByQtId(reqDTO).getScQtRecommendList();
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	//#32398 - End
		
	//#31443 here to exclude validation for not offer entry supplier ranking
		@SuppressWarnings("unused")
		private void excludeValidatePartialRanking(TplSpecZoneDtl specZoneDtl,
				List<ScQtRecommend> items) {
			List<ScQtEvaluationResultDtl> qualifiedSuppliers = null;
			List<QtEvaluationResultDtlBean> qualifiedSupplierBeans = null;
			ScQt scQt = getManagedBean().getScQt();
			SuppFinalizationResDTO resDTO = getQualifiedSuppliersWithDORank(
					specZoneDtl, null);

			int totalQualifiedSuppliers = 0;
			if (resDTO != null) {
				qualifiedSuppliers = resDTO.getQualifiedSupplierDetails();
				totalQualifiedSuppliers = resDTO.getTotalQualifiedSuppliers();
				// 26247 - start
				int countDisabled = countSupplierWithDisciplinaryAction(qualifiedSuppliers);
				totalQualifiedSuppliers = totalQualifiedSuppliers - countDisabled;
				// 26247 - end
			}

		// farid start bug #31556 related to #21695
		// exclude validation ranking if has not offer
		// Package wise validation for partial offer
//-------------> HERE #31556 & #31555 related to #21695 Farid START				
		if (SourcingConstants.EVALUATE_BY_BOTH_PACKAGE_ITEM
				.equalsIgnoreCase(this.getManagedBean().getScQt()
						.getEvaluateBy()) && !this.getManagedBean().isOfferAll()) {
			qualifiedSupplierBeans = getQtEvaluationResultDtlBeansWithRank(
					specZoneDtl, null, qualifiedSuppliers);
			validateRankingPO(qualifiedSupplierBeans, false, null, specZoneDtl,
					totalQualifiedSuppliers);
			
			//#21695 Fix bug 32401 add param start
			//#32401 start
			PackageWiseRemarkMandatory(qualifiedSupplierBeans,totalQualifiedSuppliers, specZoneDtl);
			//#32401 end
			//#21695 Fix bug 32401 add param end
		}
//-------------> HERE #31556 & #31555 related to #21695 Farid END			
		// farid start bug #31556 related to #21695
		// exclude validation ranking if has not offer
		// End of // Package wise validation for partial offer

			// Item wise validation
			List<ScQtEvaluationResultDtl> itemQualifiedSuppliers = null;
			if (CollectionUtils.isNotEmpty(items)) {
				for (ScQtRecommend item : items) {
					totalQualifiedSuppliers = 0;
					resDTO = getQualifiedSuppliersWithDORank(specZoneDtl, item);

					if (resDTO != null) {
						itemQualifiedSuppliers = resDTO
								.getQualifiedSupplierDetails();
						totalQualifiedSuppliers = resDTO
								.getTotalQualifiedSuppliers();
						// 26247 - start
						int countItemDisabled = countSupplierWithDisciplinaryAction(itemQualifiedSuppliers);
						totalQualifiedSuppliers = totalQualifiedSuppliers - countItemDisabled;
						// 26247 - end
					}
					qualifiedSupplierBeans = getQtEvaluationResultDtlBeansWithRank(
							specZoneDtl, item.getRequestItem(),
							itemQualifiedSuppliers);
		// #31443 Start -exclude validate if quantity 0					
//-------------> HERE #31556 & #31555 related to #21695 Farid START
					 if(specZoneDtl != null){
						for(TplSpecZoneQty finditemOfferZone : item.getRequestItem().getTplSpecQuestion().getTplSpecZoneQties()){
							if(finditemOfferZone != null && finditemOfferZone.getTplSpecZoneOffer() != null && !finditemOfferZone.getTplSpecZoneOffer().isOffer()
									&& item.getOrderQty() != null && item.getOrderQty().compareTo(BigDecimal.ZERO) > 0){
								log.info("validate zonal offer");
								validateRankingPO(qualifiedSupplierBeans, true, item,
										specZoneDtl, totalQualifiedSuppliers);
							}
						}
					}
//-------------> HERE #31556 & #31555 related to #21695 Farid END					 
//-------------> HERE #31556 & #31555 related to #21695 Farid START				 
					else {
						for(TplSpecAnswer finditemOfferNonZonal : item.getRequestItem().getTplSpecQuestion().getTplSpecAnswers()){
							BigDecimal rpUOM=finditemOfferNonZonal!=null && finditemOfferNonZonal.getRatePerUom()!=null ? finditemOfferNonZonal.getRatePerUom() : BigDecimal.ZERO;//35329-start
							BigDecimal quantity=item.getRequestItem()!=null && item.getRequestItem().getQuantity()!=null ? item.getRequestItem().getQuantity() : BigDecimal.ZERO;
							if (rpUOM.compareTo(BigDecimal.ZERO) > 0
									&& quantity.compareTo(BigDecimal.ZERO) > 0) {//35329-End
								log.info("validate nonzonal offer");
								validateRankingPO(qualifiedSupplierBeans, true, item,
										specZoneDtl, totalQualifiedSuppliers);
							}
						}
					}
//-------------> HERE #31556 & #31555 related to #21695 Farid END	
					 
				}
				//#32401 start
				ItemWiseRemarkMandatory(qualifiedSupplierBeans,totalQualifiedSuppliers, specZoneDtl);
				//#32401 end
			}

			// End of item wise validation
		}
//#31556 & #31555 related to #21695 Farid END				
//-------------> HERE #31556 & #31555 related to #21695 Farid START		
		private void validateRankingPO(
				List<QtEvaluationResultDtlBean> qualifiedSupplierBeans,
				boolean isItemized, ScQtRecommend item, TplSpecZoneDtl specZoneDtl,
				int totalQualifiedSuppliers) {
			int totalRanksCount = 0;
			int minRankCount = qualifiedSupplierBeans.size();
//#21695 Fix Bug validation - START			
			//#32401 - start
			
			 if (totalQualifiedSuppliers == 1) {
				minRankCount = 0;
			}else if (totalQualifiedSuppliers == 2) {
				minRankCount = 0;
			} else if (totalQualifiedSuppliers >= 3) {
				minRankCount = 3;
			}
			//#32401 - end
			//#21695 Fix Bug validation - END	
			int currentRank = 0;
			EpException epException = null;
			String expMsgForItem = StringUtils.EMPTY;

			if (isItemized) {
				expMsgForItem = "- Item : " + item.getRequestItem().getItemName();
			}

			if (CollectionUtils.isNotEmpty(qualifiedSupplierBeans)) {

			// Sort the suppliers as per the rank given by the desk officer
			// 26247 - start
			CustomUtils.sort(qualifiedSupplierBeans, new Comparator<QtEvaluationResultDtlBean>() {
				@Override
				public int compare(QtEvaluationResultDtlBean supplier1, QtEvaluationResultDtlBean supplier2) {
					BigDecimal rank1 = supplier1.getDeskOfficerRank();
					BigDecimal rank2 = supplier2.getDeskOfficerRank();
					if (rank1 == null && rank2 == null) {
						return 0;
					}
					if (rank1 == null) {
						return -1;
					}
					if (rank2 == null) {
						return 1;
					}
					return rank1.compareTo(rank2);
				}
			});
			// 26247 - end

				for (QtEvaluationResultDtlBean qualifiedSupp : qualifiedSupplierBeans) {
					BigDecimal deskOfficerRank = qualifiedSupp.getDeskOfficerRank();

					if (deskOfficerRank != null) {
						int doRank = deskOfficerRank.intValue();
						int evaluationRank = qualifiedSupp.getEvaluationRank();
						if (evaluationRank != doRank
								&& StringUtils.isBlank(qualifiedSupp
										.getDeskOfficerRemark())) {
							epException = new EpException(
									MessageConst.ERROR_760431,
									new String[] { expMsgForItem });
						}

						if (epException == null) {
							if (doRank == currentRank) {
								epException = new EpException(
										MessageConst.ERROR_760381, new String[] {
												String.valueOf(currentRank),
												expMsgForItem });
							} else if (doRank > totalQualifiedSuppliers) {
								epException = new EpException(
										MessageConst.ERROR_760382,
										new String[] {
												String.valueOf(totalQualifiedSuppliers),
												expMsgForItem });
							} else if (doRank != (currentRank + 1)) {
								if (currentRank == 0) {
									epException = new EpException(
											MessageConst.ERROR_760427,
											new String[] { expMsgForItem });
								} else {
									epException = new EpException(
											MessageConst.ERROR_760383,
											new String[] {
													String.valueOf(currentRank),
													expMsgForItem });
								}
							}
						}
						if (epException != null) {
							setZoneAndTabIndexOnException(isItemized, specZoneDtl);
							throw epException;
						}
						

						currentRank = doRank;
						++totalRanksCount;
					}
				}
			}

			//#32401 - start
			if (totalRanksCount < minRankCount ) {
				setZoneAndTabIndexOnException(isItemized, specZoneDtl);
				throw new EpException(MessageConst.ERROR_760384, new String[] {
						String.valueOf(minRankCount), expMsgForItem });
			}
			//#21695 Fix Bug validation - START	
			else if (totalRanksCount == 0 && minRankCount == 0){
				if(CollectionUtils.isNotEmpty(qualifiedSupplierBeans)){
					throw new EpException(MessageConst.ERROR_760384, new String[] {
							String.valueOf(minRankCount), expMsgForItem });
				}
			}
			//#21695 Fix Bug validation - End	
		}
//-------------> HERE #31556 & #31555 related to #21695 Farid END	

		//#21695 Fix bug 32401 add qualifiedSupplierBeans start
		public void PackageWiseRemarkMandatory(List<QtEvaluationResultDtlBean> qualifiedSupplierBeans,int totalQualifiedSuppliers, TplSpecZoneDtl specZoneDtl){
			if (totalQualifiedSuppliers == 0) {
				
				if (getManagedBean().getFinalizationRemars() != null){
					for (ScQtFinalizationRemarks qtFinalizationRemarks: getManagedBean().getFinalizationRemars()){
						if (qtFinalizationRemarks.getRemark() == null || StringUtils.isEmpty(qtFinalizationRemarks.getRemark())){
							//set false for packagewise
							if(!qualifiedSupplierBeans.isEmpty()){
								setZoneAndTabIndexOnException(false, specZoneDtl);
								throw new EpException(MessageConst.ERROR_775165);
							}
							
						}
					}
				}
			}
		}
		
		public void ItemWiseRemarkMandatory(List<QtEvaluationResultDtlBean> qualifiedSupplierBeans,int totalQualifiedSuppliers, TplSpecZoneDtl specZoneDtl){
			if (totalQualifiedSuppliers == 0) {
				if (getManagedBean().getFinalizationItemizedRemarks() != null){
					for (ScQtFinalizationRemarks finalizationRemarks: getManagedBean().getFinalizationItemizedRemarks()){
						if (finalizationRemarks.getRemark() == null || StringUtils.isEmpty(finalizationRemarks.getRemark())){
							//set true for itemized tab
							if(!qualifiedSupplierBeans.isEmpty()){
							setZoneAndTabIndexOnException(true, specZoneDtl);
							throw new EpException(MessageConst.ERROR_775165);
							}
						}
					}
				}
			}	
		}
		//#32401 - end
		//#21695 Fix bug 32401 add qualifiedSupplierBeans end	
		
		//#21692 - Start
		/**
		 * Created to make sure new instructions in Consolidation Report status
		 * start with 4 and in sequence.
		 * 
		 * Each TRUE condition is a + 1, meaning, first TRUE condition is 4, second is 5
		 * and so on.
		 * 
		 * @param qtId
		 * @return
		 */
		public List<String> qualifiedSupplierInstructionList(Long qtId) {
			Locale locale = new FacesScopes().getUserLocale();
			List<String> instructionList = new ArrayList<>();
			int condition = SourcingConstants.DEFAULT_INSTRUCTIONS_MESSAGE_NUMBERING;//Initial instruction number, after 3 default (as-is) instructions
			
			//#28777#29729 condition
			if (checkInstructionSupplierBranchDeleted(qtId)) {
				condition = condition + 1;
			}
			
			//#21692 condition, should be 5th instruction if above condition is TRUE, else 4th
			if (new ApprovalBackingBean().isEPVApprovalRequestAccepted(qtId)) {
				condition = condition + 1;
				instructionList.add(SourcingConstants.BREAK_LINE + condition + SourcingConstants.DOT
						+ ResourceBundleUtils.getMessage("lbl.sc.qt.pending.rec.instruction", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			}
			
			//Will be the list of instruction(s) above, can be empty if no condition met.
			return instructionList;
		}
		//#21692 - End
		
		//#21692 - Start
		/**
		 * @return String Warning message for no supplier accept Extend Proposal Validity
		 */
		public String getNoSupplierWarningMessage() {
			return MessageUtils.getMessage(MessageConst.WARN_7602693, MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale());
		}
		//#21692 - End
		
		//#21692 - Start
		private boolean allSupplierDisagreeEPV(Long qtId) {		
			IQTDetailSO qtDetailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			return qtDetailSO.allSupplierDisagreeEPV(qtId);
		}
		//#21692 - End
		
	// #21692 - Start
	private void setRankNull(ScQtEvaluationResultDtl qualSupplier) {
		boolean disagreeProposalValidityExtension = qualSupplier.getQtEvaluationResult().getScQtProposal()
				.getIsAgreeQtExtendPeriod() != null
				&& (qualSupplier.getQtEvaluationResult().getScQtProposal().getIsAgreeQtExtendPeriod() == SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_NOT_QUALIFIED || qualSupplier
						.getQtEvaluationResult().getScQtProposal().getIsAgreeQtExtendPeriod() == SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_REJECTED);
		// 26247 - start
		SmSupplier supplier = qualSupplier.getQtEvaluationResult().getScQtProposal().getScQtSupplier().getSmSupplier();
		if (disagreeProposalValidityExtension || this.disabledQTProcess(supplier)) {
			qualSupplier.setDeskOfficerRank(null);
			updateCachedData(qualSupplier);
		}
		// 26247 - end
	}
	// #21692 - End
		
		//#38151 - Start
		public void qualifiedSupplierListPopupScore(Long supplierId) {
			IQTSuppFinalizationSO qtSuppFinalizationSO = null;

			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSuppFinalizationSO.class);
			retrieveQualifiedSuppliersInfoByEvalStage("1");
			log.info("===========================================" + supplierId);
			this.getManagedBean().getQualifiedSuppliersDataModel().getList();
			BigDecimal totalScoreTecEval = null;
			BigDecimal totalScoreFinEval = null;
			BigDecimal scoresettsTec = null;
			BigDecimal scoresettsFin = null;
			BigDecimal recommendationTechnicalScore = null;
			BigDecimal recommendationFinancialScore = null;
			BigDecimal recommendationTotalScore = null;
			BigDecimal percentage = new BigDecimal(SourcingConstants.IS_ONE_HUNDRED_PERCENT);
			Long qtId = this.getManagedBean().getScQt().getQtId();
			String currentEvalStage = null;
			String evalstageT = null;
			String evalstageF = null;
			Long requestNoteItemId = null;
			Long selectedZoneId = null;

			for (ScQtEvaluationResultDtl qualifiedSuppDM : this.getManagedBean().getQualifiedSuppliersDataModel().getList()) {
				currentEvalStage = qualifiedSuppDM.getQtEvaluationResult().getEvaluationStage();
//				recommendationTotalScore = qualifiedSuppDM.getTotalScore();
				qualifiedSuppDM.getQtEvaluationResult().getEvaluationResultDtls();
				if (null != this.getManagedBean().getSelectedItem()) {
					this.getManagedBean().getSelectedItem().getRequestItem().getRequestNoteItemId();
				}
				if (null != this.getManagedBean().getSelectedZone()) {
					selectedZoneId = this.getManagedBean().getSelectedZone().getSpecZoneId();
				}
				break;
			}
			if (currentEvalStage.equalsIgnoreCase(SourcingConstants.EVAL_STAGE_RECOMMENDATION_PKG)) {
				evalstageT = SourcingConstants.EVAL_STAGE_TECHNICAL;
				evalstageF = SourcingConstants.EVAL_STAGE_FINANCIAL_PKG;
			} else if (currentEvalStage.equalsIgnoreCase(SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM)) {
				//if WIP cannot use Evalstage TI have to change T, 4serangkai deployment only can use TI
				//evalstageT = SourcingConstants.EVAL_STAGE_TECHNICAL;
				evalstageT = SourcingConstants.EVAL_STAGE_TECHNICAL_ITEM;
				evalstageF = SourcingConstants.EVAL_STAGE_FINANCIAL_ITM;
			}

			List<Object[]> findListT = qtSuppFinalizationSO.retrieveQtEvaluationResultDtlsTFscore(evalstageT, qtId, null);
			for (Object[] row : findListT) {
				BigDecimal suppId = (BigDecimal) row[0];
				//BigDecimal supplierNo = (BigDecimal) row[1];
				BigDecimal supplierTotalScore = (BigDecimal) row[3];
				String evalStage = row[4].toString();
				BigDecimal reqItemId = (BigDecimal) row[6];
				// Handle each row
				if (supplierId == suppId.longValue() && evalStage.equalsIgnoreCase(evalstageT)
						&& (requestNoteItemId == null || requestNoteItemId == reqItemId.longValue())) {
					totalScoreTecEval = supplierTotalScore;
					break;
				}
			}

			for (ScQtScoreSetting scoresett : this.getManagedBean().getSuppFinalBean().getManagedBean().getScQt()
					.getScQtScoreSettings()) {
				if (this.getManagedBean().getActiveIndex() == PACKAGE_TYPE_TAB_INDEX
						&& scoresett.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_PACKAGE) {
					scoresettsTec = scoresett.getOverallTechnical();
					scoresettsFin = scoresett.getOverallFin();

					if (null != scoresett.getOverallOtherTechnical() || null != scoresett.getOverallOtherFin()) {
						scoresettsTec = scoresett.getOverallTechnical().add(scoresett.getOverallOtherTechnical());
						scoresettsFin = scoresett.getOverallFin().add(scoresett.getOverallOtherFin());
					}
					break;
				} else if (this.getManagedBean().getActiveIndex() == ITEMIZED_TYPE_TAB_INDEX
						&& scoresett.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_ITEM) {
					scoresettsTec = scoresett.getOverallTechnical();
					scoresettsFin = scoresett.getOverallFin();

					if (null != scoresett.getOverallOtherTechnical() || null != scoresett.getOverallOtherFin()) {
						scoresettsTec = scoresett.getOverallTechnical().add(scoresett.getOverallOtherTechnical());
						scoresettsFin = scoresett.getOverallFin().add(scoresett.getOverallOtherFin());
					}
					break;
				}
			}
			this.getManagedBean().setOverallTechnicalRecommendationPercent(scoresettsTec.longValue());
			this.getManagedBean().setOverallFinancialRecommendationPercent(scoresettsFin.longValue());

			// for(ScQtEvaluationResultDtl testting :
			// this.getManagedBean().getQualifiedSuppliersDataModel().getList()){
			// if(testting.getQtEvaluationResult().getEvaluationStage() != null){
			// totalScoreTecEval = testting.getTotalScore();
			// break;
			// }
			// }

			retrieveQualifiedSuppliersInfoByEvalStage("2");
			List<Object[]> findListF = qtSuppFinalizationSO.retrieveQtEvaluationResultDtlsTFscore(evalstageF, qtId,
					selectedZoneId);

			for (Object[] row : findListF) {
				BigDecimal suppId = (BigDecimal) row[0];
				//BigDecimal supplierNo = (BigDecimal) row[1];
				BigDecimal supplierTotalScore = (BigDecimal) row[3];
				String evalStage = row[4].toString();
				BigDecimal reqItemId = (BigDecimal) row[6];
				// Handle each row

				if (supplierId == suppId.longValue() && evalStage.equalsIgnoreCase(evalstageF)
						&& (requestNoteItemId == null || requestNoteItemId == reqItemId.longValue())) {
					totalScoreFinEval = supplierTotalScore;
					break;
				}
			}

			recommendationTechnicalScore = totalScoreTecEval.multiply(scoresettsTec).divide(percentage,
					SourcingConstants.DECIMAL_PRECISION, BigDecimal.ROUND_HALF_UP);

			recommendationFinancialScore = totalScoreFinEval.multiply(scoresettsFin).divide(percentage,
					SourcingConstants.DECIMAL_PRECISION, BigDecimal.ROUND_HALF_UP);
			
			recommendationTotalScore = recommendationTechnicalScore.add(recommendationFinancialScore);

			WeightageScoreBean bean = new WeightageScoreBean(totalScoreTecEval.doubleValue(),
					totalScoreFinEval.doubleValue(), recommendationTechnicalScore.doubleValue(),
					recommendationFinancialScore.doubleValue(), recommendationTotalScore.doubleValue());

			List<WeightageScoreBean> listlist = new ArrayList<>();
			listlist.add(bean);

			this.getManagedBean().setWeightageScoreBeans(listlist);

			RequestContext.getCurrentInstance().execute("preferenceMarginSuppDetailstestPopup.show()");

		}
			
		public void retrievescoresettings(ScQt scqt) {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setScQt(scqt);
			SpecificationResDTO resDTO = null;
			IQTSpecSO specSo = null;
			try {
				List<Integer> settinglist = new ArrayList<Integer>();
				settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_PACKAGE);
				settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_ITEM);

				reqDTO.setSettingTypes(settinglist);
				specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
				resDTO = specSo.retrieveScoreSettingDtls(reqDTO);
				this.getManagedBean().getScQt().setScQtScoreSettings(resDTO.getScoreSettingResultBean().getResultList());
			} catch (Exception e) {
				handleException(e);
			}
		}
			//#38151 - End
		
		//#38151 Start
		private void checkWeightage35384WIP() {
			IQTDetailSO qtDetailsSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTDetailSO.class);
			ScQt qt = this.getManagedBean().getScQt();
			Date publishDate = qt.getPublishDate();
			Date createdDate = qt.getCreatedDate();
			
			if (null == publishDate) {
				QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
				qtReqDTO.setRedmineNo(QTConstants.APP_VERSION_REDMINE_NO_38151);
				qtReqDTO.setPhase(QTConstants.APP_VERSION_REDMINE_NO_38151_PHASE);

				PmAppVersion appVerDtls = qtDetailsSO.getAppVersionDetailsWithRedmineNo(qtReqDTO);

				Date effDate = appVerDtls.getEffDate();
				Timestamp effTimestamp = new Timestamp(effDate.getTime());
				
				if (createdDate.before(effTimestamp)) {
					this.getManagedBean().setWeightage35384WIP(true);
				}
			}else{
				QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
				qtReqDTO.setRedmineNo(QTConstants.APP_VERSION_REDMINE_NO_38151);
				qtReqDTO.setPhase(QTConstants.APP_VERSION_REDMINE_NO_38151_PHASE);

				PmAppVersion appVerDtls = qtDetailsSO.getAppVersionDetailsWithRedmineNo(qtReqDTO);

				Date effDate = appVerDtls.getEffDate();

				if (publishDate.before(effDate)) {
					this.getManagedBean().setWeightage35384WIP(true);
				}
			}
		}
		//#38151 End
}
