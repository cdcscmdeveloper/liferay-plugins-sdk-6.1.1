/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.consolidation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.bean.sc.QtEvaluationResultDtlBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
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
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalItem;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.key.sc.supplierfinalization.MapKey;
import my.ep.web.managedbean.sc.consolidation.ConsolidationReportManagedBean;
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
 * ConsolidationReport  Detail Backing Bean.
 * 
 * @author Praneeth
 * 
 * @version 1.1
 * @since 14.01.2016
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
	
	private final static Long DEFAULT_ITEM = 0L;
	private final static Long DEFAULT_ZONE = 0L;
	
	/** Default constructor */
	public ConsolidationReportBackingBean() {}
	
	
	/** Public Methods **/
	public void preRenderView() {
		FacesContext fc = FacesContext.getCurrentInstance();
		
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
			
			fc.getApplication().getNavigationHandler()
			  .handleNavigation(fc, null,
					QTConstants.DO_CONSOLIDATION_REPORT_PAGE); 
		} catch (Exception e) {
			handleException(e);
			fc.getApplication().getNavigationHandler()
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
				navigateToBriefingPaperTab();
				break;
			case 1:
				navigateToPackagewiseTab();
				break;
			case 2:
				navigateToItemizedTab();
				break;
		}
		onTabMenuChangeAction(tabIndex,true);
	}	
	private void onTabMenuChangeAction(int tabIndex,boolean isEventTriggered) {
		
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
		
		TplSpecZoneDtl selectedZone = getManagedBean().getZoneDetails()
													  .get(selectedZoneTabIndex);
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
		getManagedBean().setSelectedItem(scQtRecommend);
		
		Long selectedZoneId = null;
		ScQt scQt = getManagedBean().getScQt();
		if (scQt.isZonal()) {
			selectedZoneId = getManagedBean()
								.getSelectedZone().getSpecZoneId();
		}
		getManagedBean().setQualifiedSuppliersDataModel(
				new QualifiedSuppliersPagingDataModel("qualifiedSuppliers",
						scQt.getQtId(),
						SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM,
						selectedZoneId, scQtRecommend)
		);
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
		
		List<QtEvaluationResultDtlBean> cachedSuppliersList = 
				getCachedSuppliersFromMap(selectedZoneId, selectedItemId);
		
		QtEvaluationResultDtlBean doRecommendationBean = getCachedBeanFromList(
				cachedSuppliersList, selectedSupplier.getEvaluationResultDtlId());
		
		if (doRecommendationBean == null) {
			doRecommendationBean = new QtEvaluationResultDtlBean();
			doRecommendationBean.setQtEvalResDtlId(
					selectedSupplier.getEvaluationResultDtlId());
			doRecommendationBean.setEvaluationRank(
					selectedSupplier.getEvalRank());			
			doRecommendationBean.setDeskOfficerRank(
					selectedSupplier.getDeskOfficerRank());
			doRecommendationBean.setDeskOfficerRemark(
					selectedSupplier.getDeskOfficerRemark());

			cachedSuppliersList.add(doRecommendationBean);
		} else {
			doRecommendationBean.setDeskOfficerRank(
					selectedSupplier.getDeskOfficerRank());
			doRecommendationBean.setDeskOfficerRemark(
					selectedSupplier.getDeskOfficerRemark());
		}		
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
//		report.setScQtFinalizationReportAttachment(
//					new ScQtFinalizationReportAttachment());

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
		List<ScQtFinalizationReport> deletedFinalizationReports
					= getManagedBean().getDeletedFinalizationReports();
		if (deletedFinalizationReports == null) {
			deletedFinalizationReports = new ArrayList<ScQtFinalizationReport>();
			getManagedBean().setDeletedFinalizationReports(
								 deletedFinalizationReports);
		}
		if (CollectionUtils.isNotEmpty(selectedFinalizationReports)) {
			for (ScQtFinalizationReport report 
						: selectedFinalizationReports) {
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
	 * Rowselect event to select Supplier Finalization Report under Briefing Paper tab
	 * 
	 * @param ScQtFinalizationReport
	 *            the ScQtFinalizationReport to set
	 */
	public void selectReportEvent(SelectEvent event) {
		debugLogger("selectReportEvent - Row Select");

		ScQtFinalizationReport report = (ScQtFinalizationReport) event.getObject();
		report.setSelected(true);
		
		selectReportEvent(report);
	}		
	
	/**
	 * Checkbox event to select Supplier Finalization Report under Briefing Paper tab
	 * 
	 * @param ScQtFinalizationReport
	 *            the ScQtFinalizationReport to set
	 */
	public void selectReportEvent(ScQtFinalizationReport report) {
		debugLogger("selectReportEvent - Checkbox select");

		List<ScQtFinalizationReport> selectedFinalizationReports
					= getManagedBean().getSelectedFinalizationReports();
		if (selectedFinalizationReports == null) {
			selectedFinalizationReports 
					= new ArrayList<ScQtFinalizationReport>();
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
			ScQtFinalizationReport scQtFinalizationReport 
					= this.getManagedBean().getSelectedFinalizationReport();
			ScQtFinalizationReportAttachment reportAttachment 
					= scQtFinalizationReport.getScQtFinalizationReportAttachment();
			if (null == reportAttachment) {
				reportAttachment = new ScQtFinalizationReportAttachment();
				reportAttachment.setScQtFinalizationReport(scQtFinalizationReport);
				reportAttachment.setDocNo(getManagedBean().getScQt().getQtNo());
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
		
		if (reportAttachment == null) {
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

		for (int i = 0; i < scQtFinalizationReports.size(); i++) {
			ScQtFinalizationReportAttachment attachment = scQtFinalizationReports.get(i)
					.getScQtFinalizationReportAttachment();
			if (attachment == null) {
				attachment = generateDocumentAttachment(scQtFinalizationReports.get(i));
				scQtFinalizationReports.get(i).setScQtFinalizationReportAttachment(attachment);
			}
			scAttachments.add(attachment);
		}
		
		// Generate all the reports before submission
		if (!getManagedBean().isSubmit()) {
			downloadAllFiles("NGeP-Finalization-Report", scAttachments);
		}
	}	

	/**
	 *save consolidationReport Details.
	 * 
	 *
	 */
	public void saveAction() {
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
	
	/**
	 *submit Action Details.
	 * 
	 *
	 */
	public void submitAction() {
		// Validate Desk officer ranking
		validateConsolidationReport();
		RequestContext.getCurrentInstance().execute("submissionPopup.show()");
			
	}
	
	/**
	 *submit consolidationReport Details.
	 * 
	 *
	 */
	public void submitConsolidationReport(){
		
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		SuppFinalizationReqDTO reqDTO = prepareDataToSave();
		
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);

			// BPM Params
			FacesScopes facesScopes = new FacesScopes();
			reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setOutcome(getManagedBean().getOutcome());
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setPmUser(facesScopes.getUserSessionBean());
			
			SuppFinalizationResDTO resDTO 
					= qtSuppFinalizationSO.submitConsolidationReport(reqDTO);
			if (resDTO.isSuccessful()) {
				String qtParam = getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES,
													ParameterConst.DOCTYPE_QUOTATION)
						+ " " + getManagedBean().getScQt().getQtNo();
				String statusParam = getStatusDescById(
									getManagedBean().getCurrentStatusId());

				commonMessageAcknowledmentPage(MessageConst.INFO_700026,
											   qtParam, statusParam);
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
		List<TplSpecZoneDtl> specZoneDtls = this.getManagedBean().getZoneDetails();
		if (zoneDtl != null && 
				CollectionUtils.isNotEmpty(specZoneDtls)) {
			zoneDtl = getManagedBean().getZoneMap().get(zoneDtl.getSpecZoneId());
			getManagedBean().setSelectedZone(zoneDtl);
			getManagedBean().setZoneActiveIndex(
						zoneDtl.getZoneTabIndex());
		}
		
		if (isItemized) {
			List<ScQtRecommend> itemList = null;
			if (zoneDtl != null) {
				Map<Long,List<ScQtRecommend>> itemsByZone =
						getManagedBean().getScQtFinalization().getScQtRecommendsByZone();
				itemList = itemsByZone.get(zoneDtl.getSpecZoneId());
			} else {
				itemList = getManagedBean().getScQtFinalization().getScQtRecommends();
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

		onTabMenuChangeAction(activeIndex,false);
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
			
			boolean isMedicalProcCategory = isMultipleProposalCategory(
					scQt, null);
			getManagedBean().setProcCategoryMedicine(isMedicalProcCategory);

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
			scQtFinalization.setScQtFinalizationReports(finalizationReports);
			// Sort the reports as per the last updated date (in ascending order)
			
			Collections.sort(finalizationReports, 
								new Comparator<ScQtFinalizationReport>() {
									public int compare(ScQtFinalizationReport report1, 
											ScQtFinalizationReport report2) {
										if (report1.getChangedDate() != null && 
												report2.getChangedDate() != null) {
											
											// Ascending order
											if (report1.getChangedDate().getTime() == 
													report2.getChangedDate().getTime()) {
												return 0;
											} else if (report1.getChangedDate().getTime() > 
													report2.getChangedDate().getTime()) {
												return 1;
											} else {
												return -1;
											}
										}
										return -1;
									}
								});
			if (finalizationReports != null) {
				this.getManagedBean().setFinalizationReports(finalizationReports);
			}
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
						proposalItem.getOfferPrice(), scQtRecommend.getOrderQty());

			evalResultDtl.getQtEvaluationResult()
						 .setOfferedPrice(offeredPrice);
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
				reqDTO.setSpecZoneId(scQtRecommend.getTplSpecZoneDtl().getSpecZoneId());
			}
			reqDTO.setScQtProposalAmount(recommendedSupplier.getProposalAmount());
			reqDTO.setRequestItemId(scQtRecommend.getRequestItem()
					.getRequestNoteItemId());
			boolean isPriceTypeTier = isItemPriceTypeTariffOrTier(scQtRecommend);
			reqDTO.setTierPricing(isPriceTypeTier);			
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.getQtProposalItem(reqDTO);
			qtProposalItem = resDTO.getScQtProposalItem();
		} finally {
			qtSuppFinalizationSO = null;
		}
		
		return qtProposalItem;
	}	
	
	private boolean isItemPriceTypeTariffOrTier(ScQtRecommend scQtRecommend) {
		TplSpecQuestion specQuestion = scQtRecommend.getRequestItem().getTplSpecQuestion();
		boolean isPriceTypeTariffOrTier = false;
		
		if (ParameterConst.PRICE_TYPE_TARIFF.equals(
				scQtRecommend.getRequestItem().getTplSpecQuestion().getClPriceTypeCode())) {
			isPriceTypeTariffOrTier = true;
		} else {
			isPriceTypeTariffOrTier = TemplateSpecHelper.getInstance().isPriceTypeTier(
					specQuestion.getClPriceTypeCode().getParameterCode());
		}
		
		return isPriceTypeTariffOrTier;
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
		
		ScQtFinalization scQtFinalization = 
				getManagedBean().getScQtFinalization();
		TplSpecZoneDtl selectedZone = getManagedBean().getSelectedZone();
		if (scQtFinalization != null) {
			Map<Long, List<ScQtRecommend>> scQtRecommendsByZone = scQtFinalization
					.getScQtRecommendsByZone();
			log.info("scQtFinalization.getScQtRecommendsByZone() : "
					+ scQtRecommendsByZone);

			if (selectedZone != null && scQtRecommendsByZone != null
					&& scQtRecommendsByZone.size() > 0) {
				getManagedBean().setItemsList(
						scQtRecommendsByZone.get(
								selectedZone.getSpecZoneId()));
			}
		}
	}	
	
	private SuppFinalizationResDTO getQualifiedSuppliersWithDORank(TplSpecZoneDtl specZone, 
																   ScQtRecommend scQtRecommend) {
		SuppFinalizationResDTO resDTO = null;
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(getManagedBean().getScQt().getQtId());
			reqDTO.setDoRankRequired(true);
			reqDTO.setCountOfQualiedSuppliersReq(true);
			
			if (specZone != null) {
				reqDTO.setSpecZoneId(specZone.getSpecZoneId());
			}
			if (scQtRecommend != null) {
				reqDTO.setItemize(true);
				reqDTO.setRequestItemId(scQtRecommend
						.getRequestItem().getRequestNoteItemId());
			}
			
			resDTO = qtSuppFinalizationSO.retrieveQtEvaluationResultDtls(reqDTO);
			
			if (resDTO.isSuccessful()) {
				return resDTO;
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
		
		return null;
	}
	
	private void initializeRemarks() {
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();

		// Retrieve finalization remarks
		List<ScQtFinalizationRemarks> remarks = initializeRemarks(scQtFinalization,
				getManagedBean().getScQt().getCurrentWorkFlowStatus(), 
				scQtFinalization.getScQtFinalizationRemarks(), 
				ScQtFinalizationRemarks.class);
		scQtFinalization.setScQtFinalizationRemarks(remarks);

		// Finalization Remarks list
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
		ScQtFinalization scQtFinalization 
					= getManagedBean().getScQtFinalization();
		if (CollectionUtils.isNotEmpty(
				scQtFinalization.getScQtFinalizationReports())) {
			for (ScQtFinalizationReport report 
						: scQtFinalization.getScQtFinalizationReports()) {
				if (report.getScQtFinalizationReportAttachment() == null && 
						ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
							.equals(report.getDefinationType().getParameterCode())) {
					navigateToBriefingPaperTab();
					throw new EpException(MessageConst.ERROR_760415, 
										  new String[] { report.getTitle() });
				}
			}
		}
		
		boolean isZonal = getManagedBean().getScQt().isZonal();
		Map<Long,List<ScQtRecommend>> itemsByZone = scQtFinalization.getScQtRecommendsByZone();
		
		if (isZonal) {
			for (TplSpecZoneDtl specZoneDtl 
						: getManagedBean().getZoneDetails()) {
				Long specZoneId = specZoneDtl.getSpecZoneId();
				validateDORanking(specZoneDtl, itemsByZone.get(specZoneId));		
			}
		} else {
			validateDORanking(null, scQtFinalization.getScQtRecommends());
		}
	}
	
	private void validateDORanking(TplSpecZoneDtl specZoneDtl, List<ScQtRecommend> items) {
		List<ScQtEvaluationResultDtl> qualifiedSuppliers = null;
		List<QtEvaluationResultDtlBean> qualifiedSupplierBeans = null;
		SuppFinalizationResDTO resDTO = getQualifiedSuppliersWithDORank(specZoneDtl, null);	
		
		int totalQualifiedSuppliers = 0;
		if (resDTO != null) {
			qualifiedSuppliers = resDTO.getQualifiedSupplierDetails();
			totalQualifiedSuppliers = resDTO.getTotalQualifiedSuppliers();
		}
		
		// Package wise validation
		if (!getManagedBean().isProcCategoryMedicine()) {
			qualifiedSupplierBeans = 
					getQtEvaluationResultDtlBeansWithRank(specZoneDtl, null, qualifiedSuppliers);
			validateRanking(qualifiedSupplierBeans, false, null, 
							specZoneDtl, totalQualifiedSuppliers);	
		}		
		// End of // Package wise validation
		
		
		// Item wise validation
		List<ScQtEvaluationResultDtl> itemQualifiedSuppliers = null;
		if (CollectionUtils.isNotEmpty(items)) {
			for (ScQtRecommend item : items) {
				totalQualifiedSuppliers = 0;
				resDTO = getQualifiedSuppliersWithDORank(specZoneDtl, item);
				
				if (resDTO != null) {
					itemQualifiedSuppliers = resDTO.getQualifiedSupplierDetails();
					totalQualifiedSuppliers = resDTO.getTotalQualifiedSuppliers();
				}
				qualifiedSupplierBeans = getQtEvaluationResultDtlBeansWithRank(specZoneDtl,
											 item.getRequestItem(), itemQualifiedSuppliers);
				validateRanking(qualifiedSupplierBeans, true, item, 
								specZoneDtl, totalQualifiedSuppliers);
			}
		}
		
		// End of item wise validation
	}
	
	private void validateRanking(List<QtEvaluationResultDtlBean> qualifiedSupplierBeans, 
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
										if (supplier1.getDeskOfficerRank() != null && 
												supplier2.getDeskOfficerRank() != null) {
											
											// Ascending order
											return supplier1.getDeskOfficerRank().intValue() - 
														supplier2.getDeskOfficerRank().intValue();
										}
										return -1;
									}
								});
			
			for (QtEvaluationResultDtlBean qualifiedSupp 
									: qualifiedSupplierBeans) {
				BigDecimal deskOfficerRank = qualifiedSupp.getDeskOfficerRank();
				
				if (deskOfficerRank != null) {
					int doRank = deskOfficerRank.intValue();
					int evaluationRank = qualifiedSupp.getEvaluationRank();
					if (evaluationRank != doRank && 
							StringUtils.isBlank(qualifiedSupp.getDeskOfficerRemark())) {
						epException = new EpException(MessageConst.ERROR_760431, 
													  new String[] { expMsgForItem });
					}
					
					if (epException == null) {
						if (doRank == currentRank) { 
							epException = new EpException(MessageConst.ERROR_760381, 
										new String[] {String.valueOf(currentRank), expMsgForItem});
						} else if (doRank > totalQualifiedSuppliers) {
							epException = new EpException(MessageConst.ERROR_760382, 
										new String[] {String.valueOf(totalQualifiedSuppliers), expMsgForItem});
						} else if (doRank != (currentRank + 1)) {
							if (currentRank == 0) {
								epException = new EpException(MessageConst.ERROR_760427, 
															  new String[] {expMsgForItem});						
							} else {
								epException = new EpException(MessageConst.ERROR_760383, 
										new String[] {String.valueOf(currentRank), expMsgForItem});
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
			throw new EpException(MessageConst.ERROR_760384, 
						new String[] {String.valueOf(minRankCount), expMsgForItem});
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
		List<QtEvaluationResultDtlBean> cachedSuppliers = 
						getCachedSuppliersFromMap(zoneId, itemId);
		
		if (CollectionUtils.isNotEmpty(qualifiedSuppliers)) {
			QtEvaluationResultDtlBean resultDtlBean = null;
			beanList = new ArrayList<QtEvaluationResultDtlBean>();
			
			for (ScQtEvaluationResultDtl evalResDtl 
							   : qualifiedSuppliers) {
				resultDtlBean = getCachedBeanFromList(cachedSuppliers, 
									evalResDtl.getEvaluationResultDtlId());
				if (resultDtlBean == null) {
					resultDtlBean = new QtEvaluationResultDtlBean();
					resultDtlBean.setQtEvalResDtlId(
							evalResDtl.getEvaluationResultDtlId());
					resultDtlBean.setEvaluationRank(
							evalResDtl.getEvalRank());
					resultDtlBean.setDeskOfficerRank(
							evalResDtl.getDeskOfficerRank());					
					resultDtlBean.setDeskOfficerRemark(
							evalResDtl.getDeskOfficerRemark());
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
		reqDTO.setScQtFinalization(
				getManagedBean().getScQtFinalization());
		reqDTO.setRemovedFinalizationReports(
				getManagedBean().getDeletedFinalizationReports());
		
		List<QtEvaluationResultDtlBean> cachedSuppliers = 
					new ArrayList<QtEvaluationResultDtlBean>();
		if (MapUtils.isNotEmpty(
				getManagedBean().getQualifiedSuppliersCache())) {
			for (List<QtEvaluationResultDtlBean> supplierList 
						: getManagedBean().getQualifiedSuppliersCache().values()) {
				if (CollectionUtils.isNotEmpty(supplierList)) {
					cachedSuppliers.addAll(supplierList);
				}
			}
		}
		reqDTO.setCachedSupplierBeans(cachedSuppliers);

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
			ScQt scQt = getManagedBean().getScQt();
			reportAttachment = new ScQtFinalizationReportAttachment();
			reportAttachment.setScQtFinalizationReport(scQtFinalizationReport);
			reportAttachment.setDocNo(scQtFinalization.getScQt().getQtNo());
			StringBuilder filename = new StringBuilder();
			// replace white space due to firefox
			filename.append(scQtFinalizationReport.getTitle().replaceAll("\\s",
					"_"));
			
			if (SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT.equals(docDefinitionKey)) {
				filename.append(".pdf");
				reportAttachment.setFileName(filename.toString());			
				generateKertasTaklimatPdf(scQt, reportAttachment);
				scQtFinalizationReport
						.setScQtFinalizationReportAttachment(reportAttachment);
			} else {
				debugLogger("No document type exists with the key " + docDefinitionKey);
				reportAttachment = null;
			}

			// Redmine SIR#7782
//			if (SourcingConstants.DOC_DEF_KEY_COVER.equals(docDefinitionKey)) {
//				filename.append(".pdf");
//				reportAttachment.setFileName(filename.toString());
//				generateCoverPdf(attachment, getManagedBean().getScQt());
//
//			} else if (SourcingConstants.DOC_DEF_KEY_OFFERED_QT_DTLS
//					.equals(docDefinitionKey)) {
//				generateOfferedAndQTDtlsPdf(attachment, getManagedBean()
//						.getScQt());
//
//			} else if (SourcingConstants.DOC_DEF_KEY_ANALYSIS_EVALUATION_REPORT
//							.equals(docDefinitionKey)) {
//				filename.append(".pdf");
//				reportAttachment.setFileName(filename.toString());
//				generateAnalysisEvaluationReportPdf(attachment,
//						getManagedBean().getScQt());
//
//			} else if (SourcingConstants.DOC_DEF_KEY_NOTICE_BOARD
//					.equals(docDefinitionKey)) {
//				generateNoticeBoardPdf(attachment, getManagedBean().getScQt());
//
//			} else if (SourcingConstants.DOC_DEF_KEY_PRICE_SCHEDULE
//					.equals(docDefinitionKey)) {
//				filename.append(".pdf");
//				reportAttachment.setFileName(filename.toString());
//				generatePriceSchedulePdf(attachment, getManagedBean().getScQt());
//
//			} else if (SourcingConstants.DOC_DEF_KEY_EVALUATION_REPORT
//					.equals(docDefinitionKey)) {
//				filename.append(".pdf");
//				reportAttachment.setFileName(filename.toString());
//				populateAttachmentDetails(reportAttachment);
//
//			} 
			
			
		}
		return reportAttachment;
	}	
	
	private void generateKertasTaklimatPdf(ScQt qt, ScQtFinalizationReportAttachment attachment) 
										   throws Exception {
		debugLogger("generateKertasTaklimatPdf");
		populateAttachmentDetails(attachment);

		FacesScopes facesScopes = new FacesScopes();
		long qtId = qt.getQtId();
		String reportName = ReportConst.SC_SUPPLIER_FINALIZATION_RPT_JSP;
		
		String estimatedAmountStr = null;
		IQTDetailSO qtDetailSO = null;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			BigDecimal estimatedAmount = qtDetailSO.getdecryptedEstAmount(qtId);
			estimatedAmountStr = estimatedAmount.toPlainString();
		} finally {
			qtDetailSO = null;
		}		

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, facesScopes
				.getUserSessionBean().getUserId() + "");
		paramMap.put(ReportConst.PARAM_ESTIMATED_AMOUNT, estimatedAmountStr);

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
				new QualifiedSuppliersPagingDataModel(
						"qualifiedSuppliers", getManagedBean().getScQt().getQtId(),
						SourcingConstants.EVAL_STAGE_RECOMMENDATION_PKG,
						selectedZoneId, null)
		);			
		
		getManagedBean().setActiveIndex(PACKAGE_TYPE_TAB_INDEX);
	}
	
	private void commonMessageAcknowledmentPage(String messageId, String variable1, String variable2){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
			FacesMessage.SEVERITY_INFO, 
			null, MessageUtils.getMessage(messageId, 
					new String[]{variable1, variable2}, 
					MessageUtils.BUNDLE_COMMON_MESSAGES, 
					new FacesScopes().getUserLocale())));
		
		// clear data
		super.renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
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
	 * @return the BRIEFING_PAPER_TAB_INDEX
	 */
	public static int getBRIEFING_PAPER_TAB_INDEX() {
		return BRIEFING_PAPER_TAB_INDEX;
	}

	/**
	 * Get ItemizedTypeTabIndex.
	 * @return the ITEMIZED_TYPE_TAB_INDEX
	 */
	public static int getITEMIZED_TYPE_TAB_INDEX() {
		return ITEMIZED_TYPE_TAB_INDEX;
	}

	/**
	 * Get PackageTypeTabIndex.
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
		public QualifiedSuppliersPagingDataModel(String componentId,
					            Long qtId, String evaluationStage, 
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
			
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"qtEvaluationResult.scQtProposal.scQtSupplier.scQt.qtId",
					SearchOperator.OPERATOR_EQUALS.value(), qtId));
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("qtEvaluationResult.evaluationStage",
							SearchOperator.OPERATOR_EQUALS.value(), evaluationStage));		
			
			if (null != selectedZoneId) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("tplSpecZoneDtl.specZoneId",
								SearchOperator.OPERATOR_EQUALS.value(), selectedZoneId));
			} 
			
			Long selectedItemId = null;
			if (null != selectedItem) {
				selectedItemId = selectedItem.getRequestItem().getRequestNoteItemId();
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("scRequestItem.requestNoteItemId",
								SearchOperator.OPERATOR_EQUALS.value(), 
								selectedItem.getRequestItem().getRequestNoteItemId()));				
			} 
			
			IQTSuppFinalizationSO qtSuppFinalizationSO = null;
			try {
				qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);
				
				SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
				reqDTO.setSearchBean(searchBean);
				
				if (!DEFAULT_ZONE.equals(selectedZoneId)) {
					reqDTO.setSpecZoneId(selectedZoneId);
				}
				
				SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
							.retrieveQualifiedSuppliersInfoByEvalStage(reqDTO);
				if (resDTO.isSuccessful()) {
					resultBean = resDTO.getQualifiedSuppliersByEvalStage();
					List<ScQtEvaluationResultDtl> qualifiedSuppliers = 
														resultBean.getResultList();
					Long zoneId = (selectedZoneId == null ? DEFAULT_ZONE : selectedZoneId);
					Long itemId = (selectedItemId == null ? DEFAULT_ITEM : selectedItemId);					
					List<QtEvaluationResultDtlBean> cachedSuppliers = 
													getCachedSuppliersFromMap(zoneId, itemId);

					if (CollectionUtils.isNotEmpty(qualifiedSuppliers)) {
						for (ScQtEvaluationResultDtl qualSupplier 
												: qualifiedSuppliers) {
							if (selectedItem != null) {
								calculateOfferedPrice(qualSupplier, selectedItem);
							}
							if (CollectionUtils.isNotEmpty(cachedSuppliers)) {
								QtEvaluationResultDtlBean recommendationBean = getCachedBeanFromList(
											cachedSuppliers, qualSupplier.getEvaluationResultDtlId());
								if (recommendationBean != null) {
									qualSupplier.setDeskOfficerRank(
											recommendationBean.getDeskOfficerRank());
									qualSupplier.setDeskOfficerRemark(
											recommendationBean.getDeskOfficerRemark());
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
	
	private List<QtEvaluationResultDtlBean> getCachedSuppliersFromMap(Long zoneId, Long itemId) {
		if (zoneId == null && itemId == null) {
			return null;
		}
		
		List<QtEvaluationResultDtlBean> cachedSuppliersList = null;
		Map<MapKey, List<QtEvaluationResultDtlBean>> cachedSupplierBeans = 
								getManagedBean().getQualifiedSuppliersCache();
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
	
	private QtEvaluationResultDtlBean getCachedBeanFromList(List<QtEvaluationResultDtlBean> beanList,
			   										  Long evaluationResultDtlId) {
		if (CollectionUtils.isNotEmpty(beanList)) {
			QtEvaluationResultDtlBean requiredBean = new QtEvaluationResultDtlBean();
			requiredBean.setQtEvalResDtlId(evaluationResultDtlId);
			
			if (beanList.contains(requiredBean)) {
				return beanList.get(beanList.indexOf(requiredBean));
			}
		}
		
		return null;
	}
	
}
