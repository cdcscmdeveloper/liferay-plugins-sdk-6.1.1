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
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.ConsolidationReportManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
		getManagedBean().setHasErrors(Boolean.FALSE);
		
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
			getManagedBean().setHasErrors(Boolean.TRUE);
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
				qualifiedSuppliers = qualifiedSupppliersByZone
										.get(selectedZoneId);
			} else {
				qualifiedSuppliers = null;
			}
			if (qualifiedSuppliers != null) {
				getManagedBean().setMaxRank(
						String.valueOf(qualifiedSuppliers.size()));		
			}
		} else {
			populateScQtRecommendsByZone();
		}
		
		getManagedBean().setQualifiedSuppliers(qualifiedSuppliers);
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
		if (CollectionUtils.isNotEmpty(qualifiedSuppliers)) {
			for (ScQtEvaluationResultDtl qualSupp 
						: qualifiedSuppliers) {
				calculateOfferedPrice(qualSupp, scQtRecommend);
			}
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
	
	public void submitAction() {
		// Validate Desk officer ranking
		validateConsolidationReport();
		
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
		List<TplSpecZoneDtl> specZoneDtls = this.getManagedBean().getZoneDetails();
		if (zoneDtl != null && 
				CollectionUtils.isNotEmpty(specZoneDtls)) {
			zoneDtl = getManagedBean().getZoneMap().get(zoneDtl.getSpecZoneId());
			getManagedBean().setSelectedZone(zoneDtl);
			getManagedBean().setZoneActiveIndex(
						zoneDtl.getZoneTabIndex());
			
			if (!isItemized) {
				Map<Long, List<ScQtEvaluationResultDtl>> qualifiedSupppliersByZone 
								= getManagedBean().getQualifiedSupppliersByZone();

				if (MapUtils.isNotEmpty(qualifiedSupppliersByZone)) {
					getManagedBean().setQualifiedSuppliers(
							qualifiedSupppliersByZone.get(zoneDtl.getSpecZoneId()));
				}				
			}
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

		onTabMenuChangeAction(activeIndex);
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
											} else if (report1.getChangedDate().getTime() == 
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
			validateRanking(packageQualifiedSuppliers, 
							  false, null, specZoneDtl);
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
					validateRanking(itemQualifiedSuppliers, 
									  true, item, specZoneDtl);
				}
			}
		}
	}
	
	private void validateRanking(List<ScQtEvaluationResultDtl> qualifiedSuppliers, 
								   boolean isItemized, ScQtRecommend item, 
								   TplSpecZoneDtl specZoneDtl) {
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
		EpException epException = null;
		String expMsgForItem = StringUtils.EMPTY;
		
		if (isItemized) {
			expMsgForItem = "- Item : " + item.getRequestItem().getItemName();
		}
		
		for (ScQtEvaluationResultDtl qualifiedSupp 
								: qualifiedSuppliers) {
			BigDecimal deskOfficerRank = qualifiedSupp.getDeskOfficerRank();
			if (deskOfficerRank != null) {
				int doRank = deskOfficerRank.intValue();
				if (doRank == currentRank) { 
					epException = new EpException(MessageConst.ERROR_760381, 
								new String[] {String.valueOf(currentRank), expMsgForItem});
				} else if (doRank > totalQualifiedSuppliers) {
					epException = new EpException(MessageConst.ERROR_760382, 
								new String[] {String.valueOf(totalQualifiedSuppliers), expMsgForItem});
				} else if (doRank != (currentRank + 1)) {
					epException = new EpException(MessageConst.ERROR_760383, 
										new String[] {String.valueOf(currentRank), expMsgForItem});
				}
				if (epException != null) {
					setZoneAndTabIndexOnException(isItemized, specZoneDtl);
					throw epException;
				}
				
				currentRank = doRank;
				++totalRanksCount;
			}
		}
		
		if (totalRanksCount < minRankCount) {
			setZoneAndTabIndexOnException(isItemized, specZoneDtl);
			throw new EpException(MessageConst.ERROR_760384, 
						new String[] {String.valueOf(minRankCount), expMsgForItem});
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
				debugLogger("No document type is exists with the key "
						+ docDefinitionKey);
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
