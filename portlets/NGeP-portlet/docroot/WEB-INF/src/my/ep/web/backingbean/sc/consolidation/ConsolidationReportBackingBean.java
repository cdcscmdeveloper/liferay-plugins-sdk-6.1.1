/**
 * Copyright � 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.consolidation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
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
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sc.requestitem.RequestItemReqDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendationUlasanRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalItem;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
// #31443 Start
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneQty;
// #31443 End
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.sc.supplierfinalization.SupplierFinalizationBackingBean;
import my.ep.web.bean.paginator.PagingDataModel;
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
		String definitionType = scQtFinalizationReport.getDefinationType()
				.getParameterCode();
		String docDefinitionKey = scQtFinalizationReport.getPmDocument()
				.getDocDefinitionKey();
       //codes fixed related to 25619 issue.
		if (reportAttachment == null
				|| (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
						.equals(definitionType) && SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT
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
	public void downloadAllBriefingPaperReports() throws Exception {
		
		// #18685 - Start.
		debugLogger("downloadAllReports");
		// #28834 - Start.
		List<ScQtFinalizationReport> briefingPaperList = null;
		List<ScQtFinalizationReport> decisionPaperList = null;
		for (ScQtFinalizationReport report: getManagedBean().getScQtFinalization().getScQtFinalizationReports()) {
			  if (report.getTitle().contains("Kertas Taklimat")) {
				  if (briefingPaperList == null) {
					  briefingPaperList = new ArrayList<>();
				  }
				  briefingPaperList.add(report);
			  } else {
				  if (decisionPaperList == null) {
					  decisionPaperList = new ArrayList<>();
				  }
				  decisionPaperList.add(report);
			  }
		}
		
		List<ScQtFinalizationReportAttachment> scAttachments = new ArrayList<ScQtFinalizationReportAttachment>();

		if (CollectionUtils.isNotEmpty(briefingPaperList)
				&& briefingPaperList.size() > 0) {
			for (ScQtFinalizationReport finalizationReport : briefingPaperList) {
				ScQtFinalizationReportAttachment attachment = finalizationReport.getScQtFinalizationReportAttachment();
				if (finalizationReport.getTitle().equals(briefingPaperList.get(briefingPaperList.size() -1).getTitle()) || attachment == null) {
					attachment = generateDocumentAttachment(finalizationReport);
					finalizationReport.setScQtFinalizationReportAttachment(attachment);
				}
				scAttachments.add(attachment);
			}
		}
		
		if (CollectionUtils.isNotEmpty(decisionPaperList)
				&& decisionPaperList.size() > 0) {
			for (ScQtFinalizationReport finalizationReport : decisionPaperList) {
				ScQtFinalizationReportAttachment attachment = finalizationReport.getScQtFinalizationReportAttachment();
				if (null != attachment) {
					scAttachments.add(attachment);
				}
			}
		}
		// #28834 - End.
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getNewBriefingPaperReportList())
				&& this.getManagedBean().getNewBriefingPaperReportList().size() > 0) {
			for (ScQtFinalizationReport uploadedSfReport : this.getManagedBean().getNewBriefingPaperReportList()) {
				if (uploadedSfReport.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD)
						&& null != uploadedSfReport.getScQtFinalizationReportAttachment()) {
					scAttachments.add(uploadedSfReport.getScQtFinalizationReportAttachment());
				}
			}
		}
		
		// Generate all the reports before submission
		if (!getManagedBean().isSubmit()) {
			downloadAllFiles("NGeP-Finalization-Report", scAttachments);
		}
		// #18685 - End.
	}
	
	/**
	 * @param scQtFinalizationReport
	 * @throws Exception
	 */
	public void downloadBriefingPaperReport(ScQtFinalizationReport scQtFinalizationReport) throws Exception {
		//28834 starts.
		ScQtFinalizationReportAttachment reportAttachment = null;
        boolean latestBriefingPaper = false;
        // #28834 - Start.
        List<ScQtFinalizationReport> briefingPaperList = null;
		for (ScQtFinalizationReport report: getManagedBean().getFinalizationReports()) {
			  if (report.getTitle().contains("Kertas Taklimat")) {
				  if (briefingPaperList == null) {
					  briefingPaperList = new ArrayList<>();
				  }
				  briefingPaperList.add(report);
			  }
		}
        if (CollectionUtils.isNotEmpty(briefingPaperList) && briefingPaperList.size() > 0) {
          ScQtFinalizationReport latestScQtFinalizationReport = briefingPaperList.get(briefingPaperList.size() -1);
          if (null != latestScQtFinalizationReport && latestScQtFinalizationReport.getTitle().equals(scQtFinalizationReport.getTitle())) {
            latestBriefingPaper = true;
          }
        }
        // #28834 - End.
		if (null != getManagedBean().getCurrentStatusId() 
				&& (getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.QT_CONSOLIDATION_REPORT.longValue() ||
						getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.QT_PENDING_RECOMMENDATION_REVISE_BRIEFING_PAPER.longValue()) && latestBriefingPaper) {
			reportAttachment = generateDocumentAttachment(scQtFinalizationReport);
			scQtFinalizationReport.setScQtFinalizationReportAttachment(reportAttachment);
			//28834 ends.
		} else {
			reportAttachment = scQtFinalizationReport.getScQtFinalizationReportAttachment();

			if (reportAttachment == null) {
				reportAttachment = generateDocumentAttachment(scQtFinalizationReport);
				scQtFinalizationReport.setScQtFinalizationReportAttachment(reportAttachment);
			}
		}
		downloadFile(reportAttachment);
	}
	// #18685 - End
	
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
		List<ScQtFinalizationReportAttachment> scAttachments = new ArrayList<ScQtFinalizationReportAttachment>();

		for (int i = 0; i < scQtFinalizationReports.size(); i++) {
			ScQtFinalizationReportAttachment attachment = scQtFinalizationReports
					.get(i).getScQtFinalizationReportAttachment();
			if (attachment == null) {
				attachment = generateDocumentAttachment(scQtFinalizationReports
						.get(i));
				scQtFinalizationReports.get(i)
						.setScQtFinalizationReportAttachment(attachment);
			}
			scAttachments.add(attachment);
		}

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
		//28834 start
		if (null != getManagedBean().getCurrentStatusId() 
				&& (getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.QT_PENDING_RECOMMENDATION_REVISE_BRIEFING_PAPER.longValue())) {
			
		} else {
		         validateConsolidationReport();
			
		}
		//28834 end
	   
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
//#21695 Farid Start
	private void retrieveSupplierOfferOrNotOffer() {
		ScQt scQt = getManagedBean().getScQt();
		IQTEvaluationSO evaluationSO = null;

		try {
			
			evaluationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION,
					IQTEvaluationSO.class);

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			ISearchBean<Object[]> searchBean = new SearchBean<Object[]>(); 
			reqDTO.setNotOfferSupplierSearchBean(searchBean);
			boolean isOfferAll = false;	 
			
			
			if (getManagedBean().getScQt()
					.getEvaluateBy().equalsIgnoreCase(SourcingConstants.EVALUATE_BY__ITEM)){
				isOfferAll = true;	 
			}
//#21695 Farid Start 18/8/2020			
			else if (getManagedBean().getScQt()
					.getEvaluateBy().equalsIgnoreCase(SourcingConstants.EVALUATE_BY_BOTH_PACKAGE_ITEM)){
	
				 List <Object[]> allSuppliers = evaluationSO.retrieveOfferOrNotOfferSuppliers(reqDTO)
							.getOfferOrNotOfferSupplierResultBean().getResultList();
						 
				 List <Object[]> suppliersNotOffer = evaluationSO.retrieveSummaryNotOfferSuppliers(reqDTO)
								.getNotOfferSupplierResultBean().getResultList();
					
				 log.info("allSuppliers size " +  allSuppliers.size());
				 log.info("suppliersNotOffer size " +  suppliersNotOffer.size());
						
				 if ( allSuppliers.size() == suppliersNotOffer.size() && allSuppliers.size() != 0 && suppliersNotOffer.size() != 0 ){
						isOfferAll = true;	 
					}
			}
			
//#21695 Farid End 18/8/2020
			 
//             for (  Object[] paramsObjallSupplier : allSuppliers){
//	              Long supplierId = ((BigDecimal) paramsObjallSupplier[0]).longValue();
//	              
//	              for (  Object[] paramsObjsuppliersNotOffer : suppliersNotOffer){
//	            	  
//		              Long notOfferSupplierId = ((BigDecimal) paramsObjsuppliersNotOffer[0]).longValue();
//		              
//		              if (supplierId == notOfferSupplierId){
//		            	  
//		            	  isAllOffer = false;
//		            	  
//		              }
//				 }
//	         
//			 }
			
			 getManagedBean().setOfferAll(isOfferAll);
		} finally {
			evaluationSO = null;
		}
	}
//#21695 Farid end
	
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
			// 18685 - END.
			//retrieveReportTabDtl(getManagedBean().getScQt(), "KT", SourcingConstants.CONSOLIDATION_TASK);
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	private void calculateOfferedPrice(ScQtEvaluationResultDtl evalResultDtl,
			ScQtRecommend scQtRecommend) {
		ScRequestItem requestItem = scQtRecommend.getRequestItem();
		ScQtProposalItem proposalItem = fetchQtProposalItem(scQtRecommend,
				evalResultDtl);
		if (proposalItem != null) {
			BigDecimal offeredPrice = TemplateSpecHelper.getInstance()
					.calculateTotalAmount(requestItem.getTplSpecQuestion(),
							proposalItem.getOfferPrice(),
							scQtRecommend.getOrderQty());

			evalResultDtl.getQtEvaluationResult().setOfferedPrice(offeredPrice);
		}
	}

	private ScQtProposalItem fetchQtProposalItem(ScQtRecommend scQtRecommend,
			ScQtEvaluationResultDtl recommendedSupplier) {
		ScQtProposalItem qtProposalItem = null;
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			if (scQtRecommend.getTplSpecZoneDtl() != null) {
				reqDTO.setSpecZoneId(scQtRecommend.getTplSpecZoneDtl()
						.getSpecZoneId());
			}
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
		if (scQtFinalization != null) {
			Map<Long, List<ScQtRecommend>> scQtRecommendsByZone = scQtFinalization
					.getScQtRecommendsByZone();
			log.debug("scQtFinalization.getScQtRecommendsByZone() : "
					+ scQtRecommendsByZone);
			//#32092 - Start.
			//#32009 - related to #21695 - start
			if(scQtRecommendsByZone.size() > 0){
				Iterator<List<ScQtRecommend>> iterator = scQtRecommendsByZone.values().iterator();
				while (iterator.hasNext()) {
					List<ScQtRecommend> qtrecommendList = iterator.next();
					if (CollectionUtils.isNotEmpty(qtrecommendList)) {
						for (ScQtRecommend qtrecommend : qtrecommendList) {
							if(qtrecommend.getOrderQty().compareTo(BigDecimal.ZERO) == 0){
								iterator.remove();
							}
						}
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
				getManagedBean().setItemsList(
						scQtRecommendsByZone.get(selectedZone.getSpecZoneId()));
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
			reqDTO.setDoRankRequired(true);
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
			Collections.sort(qualifiedSupplierBeans,
					new Comparator<QtEvaluationResultDtlBean>() {
						public int compare(QtEvaluationResultDtlBean supplier1,
								QtEvaluationResultDtlBean supplier2) {
							if (supplier1.getDeskOfficerRank() != null
									&& supplier2.getDeskOfficerRank() != null) {

								// Ascending order
								return supplier1.getDeskOfficerRank()
										.intValue()
										- supplier2.getDeskOfficerRank()
												.intValue();
							}
							return -1;
						}
					});

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
		long qtId = qt.getQtId();
		String reportName = ReportConst.SC_SUPPLIER_FINALIZATION_RPT_JSP;

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

			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("qtEvaluationResult.evaluationStage",
							SearchOperator.OPERATOR_EQUALS.value(),
							evaluationStage));

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

				SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
						.retrieveQualifiedSuppliersInfoByEvalStage(reqDTO);
				if (resDTO.isSuccessful()) {
					resultBean = resDTO.getQualifiedSuppliersByEvalStage();
					List<ScQtEvaluationResultDtl> qualifiedSuppliers = resultBean
							.getResultList();
					Long zoneId = (selectedZoneId == null ? DEFAULT_ZONE
							: selectedZoneId);
					Long itemId = (selectedItemId == null ? DEFAULT_ITEM
							: selectedItemId);
					List<QtEvaluationResultDtlBean> cachedSuppliers = getCachedSuppliersFromMap(
							zoneId, itemId);

					if (CollectionUtils.isNotEmpty(qualifiedSuppliers)) {
						for (ScQtEvaluationResultDtl qualSupplier : qualifiedSuppliers) {
							if (selectedItem != null) {
								calculateOfferedPrice(qualSupplier,
										selectedItem);
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
						}
						getManagedBean().setMaxRank(
								String.valueOf(resultBean.getTotalRecord()));
					}
				}
			} finally {
				qtSuppFinalizationSO = null;
			}

			return resultBean;
		}
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
	public void retrieveQualifiedSuppliersInfoByEvalStage() {
		String evalStageId = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap()
				.get("evaluationStageId");
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
		if (this.getManagedBean().getActiveIndex() == PACKAGE_TYPE_TAB_INDEX) {
			log.info("PACKAGE_TYPE_TAB_INDEX"
					+ this.getManagedBean().getActiveIndex());
			this.getManagedBean().setSelectedItem(null);
		} else if (this.getManagedBean().getActiveIndex() == ITEMIZED_TYPE_TAB_INDEX) {
			log.info("ITEMIZED_TYPE_TAB_INDEX"
					+ this.getManagedBean().getActiveIndex());
			if (evaluationStageId == 2) {
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
		if(evaluationStageId==2){ //#26440 Start
		supplierFinalizationBB.getManagedBean().setCurrentZone(specZoneDtl);
		} //#26440 End
		supplierFinalizationBB.retrieveQualifiedSuppliersInfoByEvalStage();

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
	
	//#27927 - Start
	/**
	* Method to display list of supplier branch had deleted or not with related current QT
	**/
    public void showSupplierBranchList(Long supplierId) {
		ScQt scQt = this.getManagedBean().getScQt();
		ScQtSupplierCriteria qtSupplierCriteria = scQt.getScQtSupplierCriteria();
		List<SmSupplierBranch> supplierBranchListDeleted = new ArrayList<>();
		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
		IQTDetailSO QtDetailsSO = (IQTDetailSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
	
	    SupplierSearchReqDTO suppSearchDeletedReqDTO = new SupplierSearchReqDTO();
	    suppSearchDeletedReqDTO.setSupplierId(supplierId);
    	SupplierSearchResDTO suppSearchDeletedResDTO = supplierSearchSO.retrieveSmSupplierBranchDeletedBySupplier(suppSearchDeletedReqDTO);
		supplierBranchListDeleted = suppSearchDeletedResDTO.getSmSupplierBranchList();
		
		if(qtSupplierCriteria.isAllState()){
    		for(SmSupplierBranch supplierBranch : supplierBranchListDeleted) {
    	        supplierBranch.setQtRelatedSupplierBranch(true);
    		}
  		}else{
  			
    			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
	    		tenderReqDTO.setQtdetail(scQt);
	    		QuationTenderResDTO tenderResDTO = QtDetailsSO.retriveQTSuppQulCriDetails(tenderReqDTO);
	    		if(tenderResDTO != null && tenderResDTO.getLocationDetails()!= null){
	    			for(TplSpecLoc locality : tenderResDTO.getLocationDetails()){
		    			for(SmSupplierBranch  supplierBranch : supplierBranchListDeleted){
							if(locality.getPmState().getStateId() != null && supplierBranch.getSmAddress().getPmState().getStateId() != null){
								if(locality.getPmState().getStateCode().equals(supplierBranch.getSmAddress().getPmState().getStateCode())){
		    						supplierBranch.setQtRelatedSupplierBranch(true);
		    					}
		    			    }
		    			}
	    			}
	    		}
  		}
		
		if(CollectionUtils.isEmpty(supplierBranchListDeleted)){
    		getManagedBean().setSupplierBranchList(listSupplierBranchList(supplierId,scQt));
    	}else{
    		supplierBranchListDeleted.addAll(listSupplierBranchList(supplierId,scQt));
    		getManagedBean().setSupplierBranchList(supplierBranchListDeleted);
    	}
		
	}
	
	private List<SmSupplierBranch> listSupplierBranchList(Long supplierId,ScQt scQt){
		
		List<SmSupplierBranch> supplierBranchList = new ArrayList<>();
		ScQtSupplierCriteria qtSupplierCriteria = scQt.getScQtSupplierCriteria();
		
		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
		IQTDetailSO QtDetailsSO = (IQTDetailSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		
		SupplierSearchReqDTO suppSearchReqDTO = new SupplierSearchReqDTO();
		suppSearchReqDTO.setSupplierId(supplierId);
    	SupplierSearchResDTO suppSearchResDTO = supplierSearchSO.retrieveSmSupplierBranchBySupplier(suppSearchReqDTO);
    	supplierBranchList = suppSearchResDTO.getSmSupplierBranchList();
    	
    	if(qtSupplierCriteria.isAllState()){
    		for(SmSupplierBranch supplierBranch : supplierBranchList) {
    	        supplierBranch.setQtRelatedSupplierBranch(true);
    		}
  		}else{
  			
    			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
	    		tenderReqDTO.setQtdetail(scQt);
	    		QuationTenderResDTO tenderResDTO = QtDetailsSO.retriveQTSuppQulCriDetails(tenderReqDTO);
	    		if(tenderResDTO != null && tenderResDTO.getLocationDetails()!= null){
	    			for(TplSpecLoc locality : tenderResDTO.getLocationDetails()){
		    			for(SmSupplierBranch  supplierBranch : supplierBranchList){
							if(locality.getPmState().getStateId() != null && supplierBranch.getSmAddress().getPmState().getStateId() != null){
								if(locality.getPmState().getStateCode().equals(supplierBranch.getSmAddress().getPmState().getStateCode())){
		    						supplierBranch.setQtRelatedSupplierBranch(true);
		    					}
		    			    }
		    			}
	    			}
	    		}
  		}
    	  
    	return supplierBranchList;
	}
	//#27927 - End
	
		
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
					}
					qualifiedSupplierBeans = getQtEvaluationResultDtlBeansWithRank(
							specZoneDtl, item.getRequestItem(),
							itemQualifiedSuppliers);
		// #31443 Start -exclude validate if quantity 0					
//-------------> HERE #31556 & #31555 related to #21695 Farid START	
					 if(specZoneDtl != null){
						for(TplSpecZoneQty finditemOfferZone : item.getRequestItem().getTplSpecQuestion().getTplSpecZoneQties()){
							if(finditemOfferZone != null && !finditemOfferZone.getTplSpecZoneOffer().isOffer()
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
							if(finditemOfferNonZonal != null && finditemOfferNonZonal.getRatePerUom().compareTo(BigDecimal.ZERO) > 0
									&& item.getRequestItem().getQuantity() != null 
										&& item.getRequestItem().getQuantity().compareTo(BigDecimal.ZERO) > 0){
								log.info("validate nonzonal offer");
								validateRankingPO(qualifiedSupplierBeans, true, item,
										specZoneDtl, totalQualifiedSuppliers);
							}
						}
					}
//-------------> HERE #31556 & #31555 related to #21695 Farid END				
				}
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
			if (qualifiedSupplierBeans.size() == 1) {
				minRankCount = 1;
			}else if (qualifiedSupplierBeans.size() == 2) {
				minRankCount = 1;
			} else if (qualifiedSupplierBeans.size() > 2) {
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
				Collections.sort(qualifiedSupplierBeans,
						new Comparator<QtEvaluationResultDtlBean>() {
							public int compare(QtEvaluationResultDtlBean supplier1,
									QtEvaluationResultDtlBean supplier2) {
								if (supplier1.getDeskOfficerRank() != null
										&& supplier2.getDeskOfficerRank() != null) {

									// Ascending order
									return supplier1.getDeskOfficerRank()
											.intValue()
											- supplier2.getDeskOfficerRank()
													.intValue();
								}
								return -1;
							}
						});

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
		
//-------------> HERE #31556 & #31555 related to #21695 Farid END				
}
