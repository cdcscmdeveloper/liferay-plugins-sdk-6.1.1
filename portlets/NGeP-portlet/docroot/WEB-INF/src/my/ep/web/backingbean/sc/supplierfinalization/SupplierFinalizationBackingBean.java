/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.supplierfinalization;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.sc.SupplierQualificationBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
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
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
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
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalAmount;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalItem;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleItem;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleLable;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.supplierfinalization.SupplierFinalizationManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;
import org.primefaces.model.StreamedContent;

/**
 * Implementation for supplier finalization
 * 
 * @author Faradiana
 * @author Praneeth
 * 
 * @version 1.1
 * @since 29/01/2014
 */
@ManagedBean(name = "suppFinalizationBackingBean")
@RequestScoped
public class SupplierFinalizationBackingBean extends
		SourcingBaseBackingBean<SupplierFinalizationManagedBean> {
	private static final Logger log = EpLogger
			.getLogger(SupplierFinalizationBackingBean.class);

	private int currentTab;
	private String textBeforeSign;

	private static final String SPACE = " ";
	
	private static final int PREPARE_ACTIVE_INDEX_MEETING = 0;
	private static final int PREPARE_ACTIVE_INDEX_COMMITTEE = 1;
	private static final int PREPARE_ACTIVE_INDEX_JOIN_IN = 2;
	
	private static final Locale myLocale = new Locale(SourcingConstants.MALAY_LANGUAGE_CODE,
			   										  SourcingConstants.MALAYSIA_COUNTRY_CODE);

	/**
	 * Default constructor
	 */
	public SupplierFinalizationBackingBean() {

	}

	/**
	 * Initialize the data required when the page is initially rendered (based
	 * on the task-id of the selected quotation/tender) and sets the data in
	 * managed bean associated with the current user session
	 */
	public void preRenderView() {
		debugLogger("preRenderView");

		try {
			SCEvaluationDataType dataType = getBpmTaskDetails(SCEvaluationDataType.class, null);
			Long qtId = Long.valueOf(dataType.getQtId());
			getManagedBean().setPayLoad(dataType);
			getManagedBean().setActiveIndex(0);
			
			Long loggedInUserId = getFacesScopes().getUserSessionBean()
									.getUserId();
			getManagedBean().setLoggedInUserId(loggedInUserId);

			loadInitialData(qtId);
			checkIfLoggedinUserIsSecretary();

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							QTConstants.FC_SUPPLIER_FINALIZATION_PAGE);
		} catch (Exception e) {
			handleException(e);
		}
	}

	private void loadInitialData(Long qtId) {
		SCEvaluationDataType dataType = (SCEvaluationDataType) 
				this.getManagedBean().getPayLoad();
		getManagedBean().setCommitteeType(dataType.getCommitteeType());
		
		retrieveQTDetails(qtId);
		
		if (ParameterConst.DECISION_FORWARD_TO_HIGHER_COMMITTEES
				.equals(dataType.getFcDecision())) {
			getManagedBean().setInitialDecisionFwdHighAuth(true);
		} else if ((ParameterConst.DECISION_FORWARD_TO_BPK
						.equals(dataType.getFcDecision()) || isQTRefinalized()) && 
					QTStatusConst.SF_PENDING_ESCALATION_TO_BPK
							.equals(getManagedBean().getCurrentStatusId())) {
			getManagedBean().setCommitteeType(StringUtils.EMPTY);
			getManagedBean().setInitialDecisionFwdToBPK(true);
		}
		
		String committeeType = getManagedBean().getCommitteeType();
		if (StringUtils.isNotBlank(committeeType) && 
				!StringUtils.equalsIgnoreCase(committeeType, "null")) {
			loadPreparationTabDetails();
		}
		loadSupplierFinalizationTabDetails();
		retrieveBriefingPaper();
		
		int tabIndex = 0;
		if (getManagedBean().isInitialDecisionFwdToBPK()) {
			getManagedBean().getScQtFinalization()
					.setLoiLoaBy(ParameterConst.LOILOA_PREPARED_BY_DO);
			getManagedBean().setPreparationTabIndex(-1);
			getManagedBean().setReportTabIndex(tabIndex++);
			getManagedBean().setFinalizationTabIndex(tabIndex++);
			getManagedBean().setEndorsementReportActiveIndex(-1);
			getManagedBean().setCommEndorsementTabIndex(-1);
		} else {
			getManagedBean().setPreparationTabIndex(tabIndex++);
			getManagedBean().setReportTabIndex(tabIndex++);
			getManagedBean().setFinalizationTabIndex(tabIndex++);
			getManagedBean().setEndorsementReportActiveIndex(tabIndex++);
			getManagedBean().setCommEndorsementTabIndex(tabIndex++);		
		}
		
		// Dynamically creating main tab menu
		createMainMenuModel();
		
		// Disable selection if group task and is not yet claimed by any user
		if (getManagedBean().isReadOnly()) {
			getManagedBean().setInitialDecisionFwdToBPK(false);
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
	public void retrieveQTDetails(Long qtId) {
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

			boolean isMedicalProcCategory = isMedicalProcurementCategory(
												scQt.getPmProcurementCategory());
			getManagedBean().setProcCategoryMedicine(isMedicalProcCategory);

			List<TplSpecZoneDtl> zoneDetails = resDTO.getZoneDetails();
			getManagedBean().setZoneDetails(zoneDetails);
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	/**
	 * To navigate to the selected tab
	 */
	public void onTabMenuChangeAction() {
		Integer selectedTabIndex = Integer.valueOf(FacesContext
				.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("selectedTabIndex"));
		
		switch (selectedTabIndex) {
			case 0:
				navigateToPreparationTab();
				break;
			case 1:
				navigateToBriefingPaperTab();
				break;
			case 2:
				navigateToSupplierFinalizationTab();
				break;
			case 3:
				navigateToEndorsementReportTab();	
				break;
			case 4:
				navigateToCommitteeEndorsementTab();
				break;
		}
		
//		List<Integer> endorsementReportTabIndexes 
//				= getManagedBean().getEndorsementReportTabIndexes();
//		if (CollectionUtils.isNotEmpty(
//				endorsementReportTabIndexes) && 
//					endorsementReportTabIndexes.contains(selectedTabIndex)) {
//			getManagedBean().setEndorsementReportActiveIndex(selectedTabIndex);
//		}
	}

	private void navigateToPreparationTab() {
		this.getManagedBean().setActiveIndex(
				this.getManagedBean().getPreparationTabIndex());
		this.getManagedBean().setPrepareActiveIndex(
				PREPARE_ACTIVE_INDEX_MEETING);
	}

	private void loadPreparationTabDetails() {
		Long qtId = getManagedBean().getScQt().getQtId();
		String committeeType = getManagedBean().getCommitteeType();
		if (StringUtils.isNotBlank(committeeType)) {
			retrievePreparationDtls(qtId, committeeType);
		}
	}

	private void navigateToBriefingPaperTab() {
		this.getManagedBean().setActiveIndex(
				this.getManagedBean().getReportTabIndex());
	}

	private void navigateToSupplierFinalizationTab() {
		this.getManagedBean().setActiveIndex(
				this.getManagedBean().getFinalizationTabIndex());
	}

	private void loadSupplierFinalizationTabDetails() {
		ScQt scQt = getManagedBean().getScQt();
		retrieveSupplierFinalizationDtls(scQt);
		ScQtFinalization scQtFinalization = 
					this.getManagedBean().getScQtFinalization();
		if (scQt.isZonal()) {
			createZoneMenuModel();
			populateScQtRecommendsByZone();
		} else {
			this.getManagedBean().setItemList(
					scQtFinalization.getScQtRecommends());
		}
		
		decisionEvent(null);
		populateRecommendTypeEventDetails(false);
		fwdHigherAuthorityEvent();
		finalizationMethodEvent(null);
		
		if (this.getManagedBean().isItemize()) {
			this.getManagedBean().setShortlistedSupplierList(null);
		}

		initializeCommitteeRemarks();
		initializeFinalizationRemarks();
		
		if (this.getManagedBean().getResetSupplierList() == null) {
			this.getManagedBean().setResetSupplierList(
					new HashSet<ScQtEvaluationResultDtl>());
		}		

	}
	
	private void initializeCommitteeRemarks() {
		ScQtCommittee scQtCommittee = this.getManagedBean().getScQtCommittee();
		if (scQtCommittee != null) {
			
			// Retrieve committee remarks
			List<ScQtCommitteRemarks> remarks = initializeRemarks(scQtCommittee,
					null, scQtCommittee.getQtCommitteRemarks(),
					ScQtCommitteRemarks.class);
			scQtCommittee.setQtCommitteRemarks(remarks);
	
			// Committee Remarks list
			this.getManagedBean().setCommitteeRemarkList(remarks);
		}		
	}
	
	private void initializeFinalizationRemarks() {
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();

		// Retrieve Finalization remarks
		List<ScQtFinalizationRemarks> remarks = initializeRemarks(scQtFinalization,
				null, scQtFinalization.getScQtFinalizationRemarks(), ScQtFinalizationRemarks.class);
		scQtFinalization.setScQtFinalizationRemarks(remarks);

		// Finalization Remarks list
		getManagedBean().setFinalizationRemarks(remarks);		
	}	

	private void navigateToCommitteeEndorsementTab() {
		this.getManagedBean().setActiveIndex(
				this.getManagedBean().getCommEndorsementTabIndex());
		retrieveEndorsementCommitteeDetails();
	}
	
	private void navigateToEndorsementReportTab() {
		this.getManagedBean().setActiveIndex(
				this.getManagedBean().getEndorsementReportActiveIndex());
	}	

	/*
	 * private void onTabMenuValidationChangeAction(Integer previousIndex){
	 * IQTSuppFinalizationSO qtSuppFinalizationSO = null; try {
	 * qtSuppFinalizationSO =
	 * BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst
	 * .SUPPLIER_SELECTION, IQTSuppFinalizationSO.class); SuppFinalizationReqDTO
	 * reqDTO = new SuppFinalizationReqDTO();
	 * 
	 * switch (previousIndex) { case 0: //Preparation Tab ScQtCommittee
	 * scQtCommittee = preparationDtlsInfo();
	 * reqDTO.setScQtCommittee(scQtCommittee);
	 * 
	 * qtSuppFinalizationSO.validatePreparation(reqDTO); break; case 1: break;
	 * case 2: break; case 3: break; } }finally{ } }
	 */

	private void retrievePreparationDtls(Long qtId, String committeeType) {
		debugLogger("..In retrievePreparationDtls()..");
		IQTMeetingSO meetingSO = null;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteType(committeeType);

			MeetingResDTO resDTO = meetingSO.retrieveMeetingDls(reqDTO);
			if (resDTO.isSuccessful()) {
				// Meeting Details
				List<ScQtMeeting> meetingList = resDTO.getMeetingDtls();
				if (CollectionUtils.isNotEmpty(meetingList)) {
					for (ScQtMeeting scQtMeeting : meetingList) {
						Long meetingStatusId = scQtMeeting.getMeetingStatus() != null ? scQtMeeting
								.getMeetingStatus().getStatusId() : null;
						scQtMeeting.setMeetingStatusStr(meetingStatusId);
						if (scQtMeeting.getRecordStatusFlg().equals(
									DbValueConst.RECORD_STATUS_SUSPENDED)) {
							this.getManagedBean().setHasPreviousMeeting(true);
						}
					}
				}

				this.getManagedBean().setMeetingList(meetingList);

				ScQtCommittee scQtCommittee = resDTO.getQtCommittee();
				this.getManagedBean().setScQtCommittee(scQtCommittee);

				// Committee Details
				if (scQtCommittee != null) {
					if (CollectionUtils.isNotEmpty(
							scQtCommittee.getCommitteeActivities())) {
						Collections.sort(scQtCommittee
								.getCommitteeActivities());
						this.getManagedBean().setCommitteeList(
								scQtCommittee.getCommitteeActivities());
						
						List<ScQtCommitteeActivity> committeeEndorsementList 
								= this.getManagedBean().getCommitteeEndorsementList();
						if (committeeEndorsementList == null) {
							committeeEndorsementList = new ArrayList<ScQtCommitteeActivity>();
							this.getManagedBean().setCommitteeEndorsementList(
									committeeEndorsementList);
						}
						for (ScQtCommitteeActivity commActivity 
									: scQtCommittee.getCommitteeActivities()) {
							committeeEndorsementList.add(commActivity);
						}
					} else {
						List<ScQtCommitteeActivity> localList 
										= new ArrayList<ScQtCommitteeActivity>();
						for (ScCommitteeMember scCommitteeMember : scQtCommittee
								.getScCommittee().getScCommitteeMembers()) {
							ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
							activity.setScQtCommittee(scQtCommittee);
							activity.setScCommitteeMember(scCommitteeMember);
							setUserDetails(activity);
							localList.add(activity);
						}
						Collections.sort(localList);
						this.getManagedBean().setCommitteeList(localList);
					}
				}

				// Meeting Attendance Details
				if (CollectionUtils.isNotEmpty(scQtCommittee
						.getMeetingAttendances())) {
					this.getManagedBean().setMeetingAttendanceList(
							scQtCommittee.getMeetingAttendances());
				}
			}
			
			this.getManagedBean().setCommitteeTabLabel(
					getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE, committeeType));
		} finally {
			meetingSO = null;
		}
	}
	
	/**
	 * To retrieve finalization committee endorsement list
	 */
	private void retrieveEndorsementCommitteeDetails() {
		List<ScQtCommitteeActivity> committeeList 
					= this.getManagedBean().getCommitteeList();
		
		if (CollectionUtils.isNotEmpty(committeeList)) {
			List<ScQtCommitteeActivity> committeeEndorsementList 
							= new ArrayList<ScQtCommitteeActivity>();
			for (ScQtCommitteeActivity commActivity 
							: committeeList) {
				if (commActivity.isAttended()) {
					committeeEndorsementList.add(commActivity);
				}
			}
			this.getManagedBean().setCommitteeEndorsementList(
					committeeEndorsementList);			
		}		
	}

	/**
	 * Sets the meeting details corresponding to the selected quotation/tender
	 * in the managed bean associate with the current user session
	 */
	public void retrieveMeetingDtls() {
		debugLogger("..In retrieveMeetingDtls()..");
		try {
			this.getManagedBean().setPrepareActiveIndex(
					PREPARE_ACTIVE_INDEX_MEETING);

			// Meeting Details
			List<ScQtMeeting> meetingList = getManagedBean().getMeetingList();
			getManagedBean().setMeetingList(meetingList);
		} finally {
		}
	}

	/**
	 * Event to handle addition of finalization committee meeting(s)
	 */
	public void addMeetingDtls() {
		debugLogger("..In addMeetingDtls()..");
		try {
			List<ScQtMeeting> meetingList = getManagedBean().getMeetingList();
			if (null == meetingList) {
				meetingList = new ArrayList<ScQtMeeting>();
			}

			ScQtMeeting meeting = new ScQtMeeting();
			// meeting.setPmStatus(new PmStatus());
			meeting.setQtCommittee(this.getManagedBean().getScQtCommittee());
			setUserDetails(meeting);

			IStatusSO statussO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IStatusSO.class);
			StatusReqDTO statusreq = new StatusReqDTO();
			statusreq.setStatusId(QTStatusConst.MS_PENDING);
			StatusResDTO statusres = statussO
					.retrieveStatusByStatusId(statusreq);
			meeting.setMeetingStatus(statusres.getPmStatus());

			meetingList.add(meeting);
			this.getManagedBean().setMeetingList(meetingList);
		} finally {

		}
	}

	/**
	 * Event to handle deletion of selected list of finalization committee
	 * meetings
	 * 
	 * @param selectedMeetingList
	 *            the selected list of ScQtMeeting entities
	 */
	public void deleteMeetingDetails() {

		try {
			List<ScQtMeeting> meetingList = getManagedBean().getMeetingList();
			List<ScQtMeeting> deletedMeetingList = getManagedBean().getDeletedMeetingList();
			if (deletedMeetingList == null){
				getManagedBean().setDeletedMeetingList(new ArrayList<ScQtMeeting>());
			}
			
			List<ScQtMeeting> tempDeletedList = new ArrayList<ScQtMeeting>();
			for (ScQtMeeting meeting : meetingList){
				if (this.getManagedBean().isSelectAll() || meeting.isSelected()) {
					tempDeletedList.add(meeting);
					
					if (!meeting.isBrandNew()) {
						meeting.setDeleted(true);
						getManagedBean().getDeletedMeetingList().add(meeting);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(tempDeletedList)) {
				meetingList.removeAll(tempDeletedList);
				getManagedBean().getScQtCommittee().setScQtMeetings(meetingList);
			}			
		} finally {
		}		
	}

	/**
	 * Event to handle deletion of the selected list of committee meeting
	 * attendance
	 * 
	 * @param selectedMeetingAttendanceList
	 *            the selected list of ScQtMeetingAttendance entities
	 */
	public void deleteMeetingAttendanceDetails() {
		try {
			List<ScQtMeetingAttendance> selectedMeetingAttdList 
					= getManagedBean().getSelectedMeetingAttList();			
			List<ScQtMeetingAttendance> meetingAttendanceList 
					= getManagedBean().getMeetingAttendanceList();
			if (CollectionUtils.isNotEmpty(
						selectedMeetingAttdList)) {
				List<ScQtMeetingAttendance> deletedMeetingAttendanceList 
						= this.getManagedBean().getDeletedMeetingAttendanceList();
				if (deletedMeetingAttendanceList == null) {
					deletedMeetingAttendanceList 
							= new ArrayList<ScQtMeetingAttendance>();
					this.getManagedBean().setDeletedMeetingAttendanceList(
							deletedMeetingAttendanceList);			
				}
				for (ScQtMeetingAttendance meetingAttendance
									: selectedMeetingAttdList) {
					if (!meetingAttendance.isBrandNew()) {
						meetingAttendance.setDeleted(true);
						deletedMeetingAttendanceList
								.add(meetingAttendance);
					}
				}
			}			
			deleteRowsAction(meetingAttendanceList,
							 selectedMeetingAttdList);
			this.getManagedBean().setSelectedMeetingAttList(null);
		} finally {
			
		}
	}

	/**
	 * Event to update the status of the selected meeting
	 * 
	 * @param scQtMeeting
	 *            the selected ScQtMeeting entity
	 */
	public void changeMeetingStatus(ScQtMeeting scQtMeeting) {
		IStatusSO statusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(scQtMeeting.getMeetingStatusStr());

		StatusResDTO statusResDTO = statusSO
				.retrieveStatusByStatusId(statusReqDTO);
		scQtMeeting.setMeetingStatus(statusResDTO.getPmStatus());
	}

	/**
	 * To retrieve committee details
	 */
	public void retrieveCommitteeDtls() {
		debugLogger("..In retrieveCommitteeDtls()..");
		this.getManagedBean().setPrepareActiveIndex(
				PREPARE_ACTIVE_INDEX_COMMITTEE);
	}

	/**
	 * To retrieve meeting attendance details corresponding to the selected
	 * quotation/tender
	 */
	public void retrieveMeetingAttendanceDtls() {
		debugLogger("..In retrieveMeetingAttendanceDtls()..");
		this.getManagedBean().setPrepareActiveIndex(
				PREPARE_ACTIVE_INDEX_JOIN_IN);
	}

	/**
	 * Event to handle addition of committee meeting attendance records
	 */
	public void addMeetingAttendance() {
		debugLogger("..In addMeetingAttendance()..");
		try {
			List<ScQtMeetingAttendance> meetingAttendanceList 
					= getManagedBean().getMeetingAttendanceList();
			if (null == meetingAttendanceList) {
				meetingAttendanceList = new ArrayList<ScQtMeetingAttendance>();
				this.getManagedBean().setMeetingAttendanceList(
						meetingAttendanceList);				
			}

			ScQtMeetingAttendance meetingAttendance = new ScQtMeetingAttendance();
			meetingAttendance.setQtCommittee(this.getManagedBean().getScQtCommittee());
			setUserDetails(meetingAttendance);
			meetingAttendanceList.add(meetingAttendance);
		} finally {

		}
	}
	
	/**
	 * Set Evaluation Stage Id
	 * 
	 * @param evaluationStageId
	 */
	public void setEvaluationStageId() {

	}
	
	/**
	 * Retrieve Qualified suppliers by evaluation stage
	 */
	public void retrieveQualifiedSuppliersInfoByEvalStage() {
		String evalStageId = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("evaluationStageId");
		int evaluationStageId = 0;
		if (evalStageId != null) {
			evaluationStageId = Integer.parseInt(evalStageId);
		}
		getManagedBean().setEvaluationStageId(evaluationStageId);
		
		Long selectedZoneId = null;
		String evaluationStage = null;
		String evaluationHeader = null;
		
		switch (evaluationStageId) {
			case 1 : if (getManagedBean().getScQt().isDemoRequired()) {
						 evaluationStage = SourcingConstants.EVAL_STAGE_PRESENTATION_DEMO;
						 evaluationHeader = "lbl.sc.eva.qualified.supplier.list.presentation.demo.stage";
					 } else {
						 evaluationStage = SourcingConstants.EVAL_STAGE_TECHNICAL;
						 evaluationHeader = "lbl.sc.eva.qualified.supplier.list.tec.evaluation.stage";
					 }
					 break;
					 
			case 2 : if (getManagedBean().isItemize()) {
						 evaluationStage = SourcingConstants.EVAL_STAGE_FINANCIAL_ITM;
					 } else {
						 evaluationStage = SourcingConstants.EVAL_STAGE_FINANCIAL_PKG;
					 }
					 if (getManagedBean().getCurrentZone() != null) {
						 selectedZoneId = getManagedBean().getCurrentZone().getSpecZoneId();
					 }
					 evaluationHeader = "lbl.sc.eva.qualified.supplier.list.fec.evaluation.stage";
			 		 break;
		}
		getManagedBean().setEvaluationHeader(evaluationHeader);
		
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(getManagedBean().getScQt().getQtId());
			reqDTO.setEvaluationStage(evaluationStage);
			reqDTO.setQtZoneCriteriaId(selectedZoneId);			

			if (getManagedBean().isItemRowSelect() && 
					getManagedBean().getSelectedItem() != null) {
				reqDTO.setRequestItemId(getManagedBean().getSelectedItem()
						.getRequestItem().getRequestNoteItemId());
			}
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
						.retrieveQualifiedSuppliersInfoByEvalStage(reqDTO);
			if (resDTO.isSuccessful()) {
				getManagedBean().setQualifiedSuppliersByEvalStage(
						 resDTO.getQualifiedSuppliersByEvalStage());
			}
			
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	/**
	 * Validate, save supplier finalization details and sets the updated data in
	 * managed bean associated with the current user session
	 */
	public void saveSupplierFinalization() {
		debugLogger("..In saveSupplierFinalization()..");

		this.getManagedBean().setSubmit(false);

		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		int activeIndex = this.getManagedBean().getActiveIndex();

		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			
			SuppFinalizationReqDTO reqDTO = null;
			
			// Save Preparation and Supplier finalization tab(s) data 
			ScQtCommittee scQtCommittee = null;
			if (activeIndex == 0 && 
					!getManagedBean().isInitialDecisionFwdToBPK()) {
				reqDTO = validateInfo(activeIndex);
				scQtCommittee = reqDTO.getScQtCommittee();
				reqDTO = validateInfo(2);
			} else {
				if (!getManagedBean().isInitialDecisionFwdToBPK()) {
					scQtCommittee = preparationDtlsInfo();
				}
				reqDTO = validateInfo(2);
			}
			
			validateFinalizationReports();
			
			if (!getManagedBean().isInitialDecisionFwdToBPK()) {
				reqDTO.setDeletedMeetingsList(
						this.getManagedBean().getDeletedMeetingList());
				scQtCommittee.setQtCommitteRemarks(
						reqDTO.getScQtCommittee().getQtCommitteRemarks());
				reqDTO.setScQtCommittee(scQtCommittee);
			}
			
			reqDTO.setInitialDecisionFwdHighAuth(
					getManagedBean().isInitialDecisionFwdHighAuth());
			reqDTO.setRefinalize(isQTRefinalized());
			
			saveAndReloadFinalizationDetails(qtSuppFinalizationSO, reqDTO);

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
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	private void validateFinalizationReports() {
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		List<ScQtFinalizationReport> finalizationReports = 
					scQtFinalization.getScQtFinalizationReports();
		
		if (CollectionUtils.isNotEmpty(finalizationReports)) {
			for (ScQtFinalizationReport report 
							: finalizationReports) {
				if (ParameterConst.DOCUMENT_CATEGORY_ENDORSEMENT_REPORT
						.equals(report.getDocCategory()) && 
						(report.getScQtFinalizationReportAttachment() == null ||
							report.getScQtFinalizationReportAttachment().getFileName() == null)) {
					navigateToBriefingPaperTab();
					throw new EpException(MessageConst.ERROR_760386);
				}
			}
		}
	}
	
	private void reloadData(SuppFinalizationResDTO resDTO) {
		int activeIndex = this.getManagedBean().getActiveIndex();
		int prepareActiveIndex = this.getManagedBean().getPrepareActiveIndex();

		String taskId = this.getManagedBean().getTaskId();
		String[] outComes = this.getManagedBean().getOutComes();
		String processName = this.getManagedBean().getProcessName();
		Long currentStatusId = this.getManagedBean().getCurrentStatusId();
		
		renewSessionAndRetrieve(activeIndex, resDTO);
		setTaskDetails(activeIndex, prepareActiveIndex, taskId, outComes,
				processName, currentStatusId);		
	}

	private SuppFinalizationReqDTO validateInfo(int index) {
		debugLogger("..In validateInfo()..");

		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			ScQtFinalization scQtFinalization = getManagedBean()
					.getScQtFinalization();

			switch (index) {
				case 0:
					// Preparation Tab
					ScQtCommittee scQtCommittee = preparationDtlsInfo();
					reqDTO.setDeletedMeetingsList(getManagedBean()
							.getDeletedMeetingList());
					reqDTO.setCommitteeType(this.getManagedBean().getCommitteeType());
					reqDTO.setScQtCommittee(scQtCommittee);
					reqDTO.setSubmit(this.getManagedBean().isSubmit());
					
					qtSuppFinalizationSO.validatePreparation(reqDTO);
					reqDTO.setScQtFinalization(scQtFinalization);
					break;
	
				case 1:
					// Briefing Paper Tab
					// no validation
					// scQtFinalization.set attachment
					scQtFinalization = supplierFinalizationInfo(scQtFinalization);
					reqDTO.setScQtFinalization(scQtFinalization);
					break;
	
				case 2:
					// Supplier Finalization Tab
					scQtFinalization = supplierFinalizationInfo(scQtFinalization);
					String decisionTypeCode = scQtFinalization.getDecision()
							.getParameterCode();
					if (ParameterConst.DECISION_RECOMMEND_SUPPLIER
							.equals(decisionTypeCode) ||
								ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY
									.equals(decisionTypeCode)) {
						
						if (ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY
								.equals(decisionTypeCode) && 
								(scQtFinalization.getReason() == null || 
									scQtFinalization.getReason().getReasonId() == null)) {
							navigateToSupplierFinalizationTab();
							throw new EpException(MessageConst.ERROR_760385);
						}						
						String recommendTypeCode = scQtFinalization
								.getRecommendedTypeId().getParameterCode();
						if (ParameterConst.RECOMMEND_TYPE_PACKAGE
								.equals(recommendTypeCode)) {
							if (this.getManagedBean().getScQt().isZonal()) {
								saveByPackageByZone(scQtFinalization);
							} else {
								saveByPackage(scQtFinalization);
							}
						} else if (ParameterConst.RECOMMEND_TYPE_ITEM
											.equals(recommendTypeCode)) {
							saveByItem(scQtFinalization);
						}
					} else {
						if (ParameterConst.RECOMMEND_TYPE_ITEM.equals(
								scQtFinalization.getRecommendedTypeId().getParameterCode())) {
							resetItemWiseSelectionDetails(this.getManagedBean().getScQt().isZonal(), 
									scQtFinalization);	
						} else {
							resetPackageWiseSelectionDetails(this.getManagedBean().getScQt().isZonal(), 
									scQtFinalization);						
						}
					}
	
					ScQtCommittee _scQtCommittee = this.getManagedBean()
							.getScQtCommittee();
					if (_scQtCommittee != null) {
						List<ScQtCommitteRemarks> remarksList = finalizeRemarks(this
								.getManagedBean().getCommitteeRemarkList());
						_scQtCommittee.setQtCommitteRemarks(remarksList);
						setUserDetails(_scQtCommittee);
					}
					
					List<ScQtFinalizationRemarks> finalizationRemarks 
							= finalizeRemarks(getManagedBean().getFinalizationRemarks());
					scQtFinalization.setScQtFinalizationRemarks(finalizationRemarks);
					setUserDetails(scQtFinalization);
	
					reqDTO.setScQtCommittee(_scQtCommittee);
					reqDTO.setScQtFinalization(scQtFinalization);
					reqDTO.setResetSuppliers(
							this.getManagedBean().getResetSupplierList());

					break;
	
				case 3:
					// Committee Endorsement Tab
					reqDTO.setScQtFinalization(scQtFinalization);
					break;
			}
			return reqDTO;
		} finally {

		}
	}

	private void renewSessionAndRetrieve(int activeIndex,
			SuppFinalizationResDTO resDTO) {
		Long qtId = getManagedBean().getScQt().getQtId();
		String committeeType = getManagedBean().getCommitteeType();
		ScQtCommittee scQtCommittee = resDTO.getScQtCommittee() != null ? resDTO
				.getScQtCommittee() : getManagedBean().getScQtCommittee();
		SCEvaluationDataType dataType = (SCEvaluationDataType) getManagedBean()
				.getPayLoad();
		Long currentStatusId = getManagedBean().getCurrentStatusId();
		TplSpecZoneDtl selectedZone = getManagedBean().getCurrentZone();
		boolean isSecretaryUser = this.getManagedBean().isSecretaryUser();
		

		renewSesionObject();

		getManagedBean().setCommitteeType(committeeType);
		getManagedBean().setScQtCommittee(scQtCommittee);
		getManagedBean().setCurrentZone(selectedZone);
		getManagedBean().setPayLoad(dataType);
		getManagedBean().setCurrentStatusId(currentStatusId);
		getManagedBean().setZoneActiveIndex(
				selectedZone != null ? selectedZone.getZoneTabIndex() : 0);
		getManagedBean().setSecretaryUser(isSecretaryUser);

		loadInitialData(qtId);

		switch (activeIndex) {
		case 0:
			navigateToPreparationTab();
			break;
		case 1:
			navigateToBriefingPaperTab();
			break;
		case 2:
			navigateToSupplierFinalizationTab();
			break;
		case 3:
			navigateToCommitteeEndorsementTab();
			break;
		}
	}

	private void setTaskDetails(Integer activeIndex,
			Integer prepareActiveIndex, String taskId, String[] outComes,
			String processName, Long currentStatusId) {
		if (activeIndex != null) {
			this.getManagedBean().setActiveIndex(activeIndex);
		}
		if (prepareActiveIndex != null) {
			this.getManagedBean().setPrepareActiveIndex(prepareActiveIndex);
		}
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setOutComes(outComes);
		this.getManagedBean().setProcessName(processName);
		this.getManagedBean().setCurrentStatusId(currentStatusId);
	}

	private ScQtCommittee preparationDtlsInfo() {
		debugLogger("..In preparationDtlsInfo()..");

		ScQtCommittee scQtCommittee = getManagedBean().getScQtCommittee();
		if (scQtCommittee != null) {
			setUserDetails(scQtCommittee);

			// Meeting Details
			List<ScQtMeeting> meetingList = getManagedBean().getMeetingList();
			if (CollectionUtils.isNotEmpty(meetingList)) {
				scQtCommittee.setScQtMeetings(meetingList);
			}

			// Committee Details
			if (CollectionUtils.isNotEmpty(
					getManagedBean().getCommitteeList())) {
				scQtCommittee.setCommitteeActivities(
						getManagedBean().getCommitteeList());
			}

			List<ScQtCommitteRemarks> remarksList = finalizeRemarks(this
					.getManagedBean().getCommitteeRemarkList());
			scQtCommittee.setQtCommitteRemarks(remarksList);

			// Meeting Attendance Details
			List<ScQtMeetingAttendance> meetingAttendanceList = getManagedBean()
					.getMeetingAttendanceList();
			if (CollectionUtils.isNotEmpty(meetingAttendanceList)) {
				if (meetingAttendanceList.size() > 1) {
					List<String> userIdentificationNos = new ArrayList<String>();
					for (ScQtMeetingAttendance meetingAttendance 
									: meetingAttendanceList) {
						if (userIdentificationNos.contains(
								meetingAttendance.getIcPassport())) {
							this.getManagedBean().setActiveIndex(0);
							this.getManagedBean().setPrepareActiveIndex(2);
							throw new EpException(MessageConst.ERROR_760320);
						}
						userIdentificationNos.add(meetingAttendance.getIcPassport());
					}
				}
				scQtCommittee.setMeetingAttendances(meetingAttendanceList);
			}
		}

		return scQtCommittee;
	}

	private ScQtFinalization supplierFinalizationInfo(
			ScQtFinalization scQtFinalization) {
		debugLogger("..In supplierFinalizationInfo()..");

		if (scQtFinalization == null) {
			scQtFinalization = new ScQtFinalization();
		}
		setUserDetails(scQtFinalization);
		scQtFinalization.setScQt(getManagedBean().getScQt());

		// reset the value base on the decision before save
		String decision = scQtFinalization.getDecision().getParameterCode();
		if (decision == null) {
			resetRecommendSupplier(scQtFinalization);
			resetReEvaluate(scQtFinalization);
			resetRepublish(scQtFinalization);
			resetCancel(scQtFinalization);
			resetForwardToHigherAuthority(scQtFinalization);

		} else if (decision.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)) {
			resetReEvaluate(scQtFinalization);
			resetRepublish(scQtFinalization);
			resetCancel(scQtFinalization);
			if (!this.getManagedBean().isInitialDecisionFwdHighAuth()) {
				resetForwardToHigherAuthority(scQtFinalization);
			}
		} else if (decision
				.equals(ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY)) {
			resetReEvaluate(scQtFinalization);
			resetRepublish(scQtFinalization);
			resetCancel(scQtFinalization);

		} else if (decision.equals(ParameterConst.DECISION_RE_EVALUATION)) {
			resetRecommendSupplier(scQtFinalization);
			resetRepublish(scQtFinalization);
			resetCancel(scQtFinalization);
			resetForwardToHigherAuthority(scQtFinalization);

		} else if (decision.equals(ParameterConst.DECISION_RE_PUBLISH)) {
			resetRecommendSupplier(scQtFinalization);
			resetReEvaluate(scQtFinalization);
			resetCancel(scQtFinalization);
			resetForwardToHigherAuthority(scQtFinalization);

		} else if (decision.equals(ParameterConst.DECISION_CANCEL)) {
			resetRecommendSupplier(scQtFinalization);
			resetReEvaluate(scQtFinalization);
			resetRepublish(scQtFinalization);
			resetForwardToHigherAuthority(scQtFinalization);
		}
		return scQtFinalization;
	}

	private void saveByPackage(ScQtFinalization scQtFinalization) {
		if (StringUtils.isBlank(
				getManagedBean().getFinalizationMethod())) {
			setZoneAndTabIndexOnException(null);
			throw new EpException(MessageConst.ERROR_760211);
		}
		
		List<ScQtRecommend> scQtRecommendList = new ArrayList<ScQtRecommend>();
		scQtRecommendList.addAll(scQtFinalization.getScQtRecommends());
		ScQtEvaluationResultDtl recommendedSupplier 
					= this.getManagedBean().getRecommendedSupplier();
		
		if (recommendedSupplier != null) {
			recommendedSupplier.setAwareded(true);
			recommendedSupplier.setShortListed(false);

			// Award
			for (ScQtRecommend scQtRecommend : scQtRecommendList) {
				scQtRecommend.setShortListedSupplierList(
						this.getManagedBean().getShortlistedSupplierList());
				if (scQtRecommend.getRecommendDtls() == null) {
					scQtRecommend.setRecommendDtls(
							new ArrayList<ScQtRecommendDtl>());
				}
				setItemHigherAuthorityReqStatus(scQtRecommend);
				resetShortlistedSupplierData(
						getManagedBean().getShortlistedSupplierList(), recommendedSupplier);
				Set<ScQtRecommendDtl> toBeAddedSet = new HashSet<ScQtRecommendDtl>();
				List<ScQtEvaluationResultDtl> recommendedSuppliers 
								= new ArrayList<ScQtEvaluationResultDtl>();
				recommendedSuppliers.add(recommendedSupplier);
				filterRecommendedSuppliersList(toBeAddedSet, recommendedSuppliers, scQtRecommend);
			}
		} else {
			// multiple LOI or Online Bidding

			// to set the recommend details for each recommend
			for (ScQtRecommend scQtRecommend : scQtRecommendList) {
				scQtRecommend.setShortListedSupplierList(
						this.getManagedBean().getShortlistedSupplierList());
				
				if (scQtRecommend.getRecommendDtls() == null) {
					scQtRecommend.setRecommendDtls(
							new ArrayList<ScQtRecommendDtl>());
				}
				setItemHigherAuthorityReqStatus(scQtRecommend);		
				List<ScQtEvaluationResultDtl> recommendedSupplierList 
							= getManagedBean().getRecommendedSupplierList();
				if (CollectionUtils.isNotEmpty(recommendedSupplierList)) {
					Set<ScQtRecommendDtl> toBeAddedSet = new HashSet<ScQtRecommendDtl>();
					filterRecommendedSuppliersList(toBeAddedSet, 
								recommendedSupplierList, scQtRecommend);
				} else if (CollectionUtils.isNotEmpty(
								scQtRecommend.getRecommendDtls())) {
					if (scQtRecommend.getRemoveSupplierList() == null) {
						scQtRecommend.setRemoveSupplierList(
											new ArrayList<ScQtRecommendDtl>());
					}
					
					Set<ScQtRecommendDtl> toBeAddedSet = new HashSet<ScQtRecommendDtl>();
					for (ScQtRecommendDtl recommendDtl 
								: scQtRecommend.getRecommendDtls()) {
						if (!recommendDtl.getRecordStatusFlg()
								.equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
							toBeAddedSet.add(recommendDtl);
						}	
						
						if (!recommendDtl.isBrandNew()
								&& !toBeAddedSet.contains(recommendDtl)) {
							recommendDtl.setDeleted(true);
							scQtRecommend.getRemoveSupplierList().add(
									recommendDtl);
						}
					}
					scQtRecommend.getRecommendDtls().clear();
					scQtRecommend.getRecommendDtls().addAll(toBeAddedSet);
				}
			}
		}
		scQtFinalization.setScQtRecommends(scQtRecommendList);
	}
	
	private void setItemHigherAuthorityReqStatus(ScQtRecommend scQtRecommend) {
		Long loggedInUserId = getFacesScopes().getUserSessionBean()
									  		  .getUserId();
		if (scQtRecommend.getCreatedBy().equals(loggedInUserId)) {
			if (!this.getManagedBean().isForwardToHigherAuthority() ||  
					!this.getManagedBean().isItemize()) {
				scQtRecommend.setHigherAuthReq(false);
			}
		}
	}
	
	private void saveByPackageByZone(ScQtFinalization scQtFinalization) {
		boolean toBeValidated = false;
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		boolean isSubmit = this.getManagedBean().isSubmit();
		String decisionTypeCode = scQtFinalization.getDecision().getParameterCode();

		if (ParameterConst.DECISION_RECOMMEND_SUPPLIER
					.equals(decisionTypeCode) && isSubmit) {
			toBeValidated = true;
		}
		
		PmParameter finalizationMethod = scQtFinalization.getFinalizationMethod();
		TplSpecZoneDtl currentZone = this.getManagedBean().getCurrentZone();
		List<TplSpecZoneDtl> tplSpecZoneDtls = this.getManagedBean().getZoneDetails();
		Map<Long, List<ScQtEvaluationResultDtl>> recommendedSuppliersByZone 
						= this.getManagedBean().getRecommendedSupppliersByZone();
		
		boolean isAward = true;
		String finalizationMethodStr = null;
		if (finalizationMethod != null) {
			finalizationMethodStr = finalizationMethod.getParameterCode();
			
			if (!ParameterConst.RECOMMEND_BY_AWARD.equals(
					finalizationMethodStr) && !isQTRefinalized()) {
				isAward = false;
			}
		} else {
			setZoneAndTabIndexOnException(currentZone);
			throw new EpException(MessageConst.ERROR_760211);
		}		
		
		if (MapUtils.isEmpty(recommendedSuppliersByZone)) {
			recommendedSuppliersByZone =
				new LinkedHashMap<Long, List<ScQtEvaluationResultDtl>>();
			this.getManagedBean().setRecommendedSupppliersByZone(
				recommendedSuppliersByZone);
		}		

		List<ScQtRecommend> toBeValidatedRecommendList = null;
		if (toBeValidated) {
			toBeValidatedRecommendList = new ArrayList<ScQtRecommend>();
		} 

		boolean isSingleAward = this.getManagedBean().isSingleAward();
		
		// If the current zone recommend by is Award, 
		// then have to add the recommended supplier to the recommendedSupppliersByZone map
		// before saving the data
		if (isSingleAward) {
			resetPreviouslyAwardedSuppliersByZone(currentZone);
			
			ScQtEvaluationResultDtl awardedSupplier = this.getManagedBean().getRecommendedSupplier();
			if (this.getManagedBean().getRecommendedSupplier() != null) {
				awardedSupplier.setAwareded(true);
				awardedSupplier.setShortListed(false);
				
				List<ScQtEvaluationResultDtl> awardedSupplierList = 
									new ArrayList<ScQtEvaluationResultDtl>();
				awardedSupplierList.add(awardedSupplier);
				recommendedSuppliersByZone.put(
						currentZone.getSpecZoneId(), awardedSupplierList);
				this.getManagedBean().getResetSupplierList()
									 .remove(awardedSupplier);					
			}
		} else if (!isSingleAward) {
			// If no supplier selected in the current zone, 
			// remove the zone from recommended zones map 
			// which holds the list of recommended suppliers for that zone
			if (CollectionUtils.isEmpty(
					this.getManagedBean().getRecommendedSupplierList())) {
				recommendedSuppliersByZone.remove(currentZone.getSpecZoneId());
			} else {
				this.getManagedBean().getResetSupplierList()
					.removeAll(this.getManagedBean().getRecommendedSupplierList());			
			}
		}
		
		List<ScQtRecommend> scQtRecommendList = new ArrayList<ScQtRecommend>();
		if (CollectionUtils.isNotEmpty(tplSpecZoneDtls)) {
			for (TplSpecZoneDtl zoneDtl : tplSpecZoneDtls) {
				List<ScQtRecommend> recommendedItemListByZone = null;
				if (MapUtils.isNotEmpty(
						scQtFinalization.getScQtRecommendsByZone())) {
					recommendedItemListByZone = 
							scQtFinalization.getScQtRecommendsByZone()
								.get(zoneDtl.getSpecZoneId());
				}
				if (CollectionUtils.isNotEmpty(recommendedItemListByZone)) {
					scQtRecommendList.addAll(recommendedItemListByZone);
				} else {
					continue;
				}
				for (ScQtRecommend scQtRecommend : recommendedItemListByZone) {
					List<ScQtEvaluationResultDtl> shortlistedSuppliersForCurrentZone 
								= getShortlistedSuppliersByZone(zoneDtl);
					scQtRecommend.setShortListedSupplierList(shortlistedSuppliersForCurrentZone);
					
					if (toBeValidated) {
						toBeValidatedRecommendList.add(scQtRecommend);
					}
					setItemHigherAuthorityReqStatus(scQtRecommend);
					if (scQtRecommend.getRecommendDtls() == null) {
						scQtRecommend.setRecommendDtls(
								new ArrayList<ScQtRecommendDtl>());
					}					

					
					if (MapUtils.isNotEmpty(recommendedSuppliersByZone)) {
						if (recommendedSuppliersByZone.containsKey(
								zoneDtl.getSpecZoneId())) {
							Set<ScQtRecommendDtl> toBeAddedSet = new HashSet<ScQtRecommendDtl>();
							List<ScQtEvaluationResultDtl> recommendedSuppList = 
									recommendedSuppliersByZone.get(zoneDtl.getSpecZoneId());
							List<ScQtEvaluationResultDtl> recommendedSuppliers 
											= new ArrayList<ScQtEvaluationResultDtl>();
							if (isAward) {
								if (CollectionUtils.isNotEmpty(recommendedSuppList)) {
									recommendedSuppliers.add(recommendedSuppList.get(0));
									filterRecommendedSuppliersList(toBeAddedSet, recommendedSuppliers, 
											scQtRecommend);	
								} 
							} else {
								if (CollectionUtils.isNotEmpty(recommendedSuppList)) {
									filterRecommendedSuppliersList(toBeAddedSet, 
												recommendedSuppList, scQtRecommend);
								}
							}							
						} else {
							prepareToDeleteRecommendDtls(scQtRecommend);
						}
						
					} else {
						prepareToDeleteRecommendDtls(scQtRecommend);
					}
				}
			}
		}
		
		if (toBeValidated && isZonal && 
				CollectionUtils.isNotEmpty(toBeValidatedRecommendList)) {
			for (ScQtRecommend scQtRecommend : scQtRecommendList) {
				
				if (!(scQtRecommend.isAwardedItem() || 
							scQtRecommend.isCancelled()) ||
									finalizationMethod == null) {
					validateByPackageByZone(scQtRecommend);
				}
			}			
		}
		scQtFinalization.setScQtRecommends(scQtRecommendList);		
	}	
	
	
	private void filterRecommendedSuppliersList(Set<ScQtRecommendDtl> toBeAddedSet, 
												List<ScQtEvaluationResultDtl> recommendedSuppliers, 
												ScQtRecommend scQtRecommend) {
		ScQtRecommendDtl selectedRecommendDtl = null;
		List<ScQtRecommendDtl> removedList 
						= scQtRecommend.getRemoveSupplierList();
		for (ScQtEvaluationResultDtl recommendedSupplier 
								: recommendedSuppliers) {
			boolean alreadyExists = false;
			
			if (CollectionUtils.isNotEmpty(
					scQtRecommend.getRecommendDtls())) {
				Long recommendedSupplierId 
						= recommendedSupplier.getQtEvaluationResult()
							.getScQtProposal().getScQtSupplier()
							.getSmSupplier().getSupplierId();
				
				for (ScQtRecommendDtl detail 
								: scQtRecommend.getRecommendDtls()) {
					if ((detail.getSupplier().getSupplierId())
									.equals(recommendedSupplierId)) {
						alreadyExists = true;
						
						if (!(detail.getRecordStatusFlg().equals(
								DbValueConst.RECORD_STATUS_REJECTED))) {
							detail.setRecordStatus(true);
							detail.setRecordStatusFlg(
									DbValueConst.RECORD_STATUS_ACTIVE);
						}
						
						checkAndDeleteFromRemovedList(
									removedList, detail);
						
						if (!detail.getRecordStatusFlg().equals(
								DbValueConst.RECORD_STATUS_ACTIVE)) {
							recommendedSupplier.setAwareded(false);
							recommendedSupplier.setShortListed(false);
						}
						detail.setScQtProposalAmount(
								recommendedSupplier.getProposalAmount());
						ScQtProposalItem qtProposalItem = getQtProposalItem(
											scQtRecommend, recommendedSupplier);
						
						BigDecimal awardQty = null;
						if (qtProposalItem != null) {
							awardQty = qtProposalItem.getOfferQuantity();
						} else {
							awardQty = scQtRecommend.getRequestItem().getQuantity();
						}
						detail.setAwardQty(awardQty);
						detail.setScQtProposalItem(qtProposalItem);
						toBeAddedSet.add(detail);
						
						break;
					}
				}
			}
			if (!alreadyExists) {
				selectedRecommendDtl = setSelectedSupplier(
						recommendedSupplier, scQtRecommend, removedList);
				setUserDetails(selectedRecommendDtl);
				toBeAddedSet.add(selectedRecommendDtl);
			}
		}
		
		if (scQtRecommend.getRemoveSupplierList() == null) {
			scQtRecommend.setRemoveSupplierList(
					new ArrayList<ScQtRecommendDtl>());
		}
		
		processAndSetDeletedRecommendDtls(toBeAddedSet, scQtRecommend, false);
		scQtRecommend.getRecommendDtls().clear();
		scQtRecommend.getRecommendDtls().addAll(toBeAddedSet);	
	}
	
	private void processAndSetDeletedRecommendDtls(Set<ScQtRecommendDtl> toBeAddedSet, 
											ScQtRecommend scQtRecommend, boolean isReset) {
		
		Set<ScQtRecommendDtl> removedRecommendDtlSet = new HashSet<ScQtRecommendDtl>();
		for (ScQtRecommendDtl recommendDtl 
								: scQtRecommend.getRecommendDtls()) {
			
			// Skip the deletion of rejected suppliers and 
			// suppliers with LOI or LOA as these records should not be deleted
			if (!recommendDtl.getRecordStatusFlg().equals(
					DbValueConst.RECORD_STATUS_ACTIVE)
						|| recommendDtl.getLoiLoaCount() > 0) {
				if (isReset && (!recommendDtl.getRecordStatusFlg()
						.equals(DbValueConst.RECORD_STATUS_REJECTED) || 
							!recommendDtl.getRecordStatusFlg()
								.equals(DbValueConst.RECORD_STATUS_CANCELLED))) {
					recommendDtl.setRecordStatus(false);
					recommendDtl.setRecordStatusFlg(
							DbValueConst.RECORD_STATUS_SUSPENDED);
				}
				toBeAddedSet.add(recommendDtl);
			}
			//End
			
			if (!recommendDtl.isBrandNew()
					&& !toBeAddedSet.contains(recommendDtl)) {
				recommendDtl.setDeleted(true);
				removedRecommendDtlSet.add(recommendDtl);
			}
		}
		scQtRecommend.getRemoveSupplierList()
					 .addAll(removedRecommendDtlSet);
	}

	private ScQtRecommendDtl setSelectedSupplier(
			ScQtEvaluationResultDtl recommendedSupplier,
			ScQtRecommend scQtRecommend, List<ScQtRecommendDtl> removedList) {
		
		ScQtRecommendDtl recommendDtl = null;
		SmSupplier supplier = recommendedSupplier.getQtEvaluationResult()
				.getScQtProposal().getScQtSupplier().getSmSupplier();
		if (CollectionUtils.isNotEmpty(removedList)) {
			for (ScQtRecommendDtl removedDetail : removedList) {
				if ((supplier.getSupplierId()).equals(
						removedDetail.getSupplier().getSupplierId())) {
					removedDetail.setDeleted(false);
					removedList.remove(removedDetail);
					removedDetail.setShortlistedSupplier(
							recommendedSupplier);
					removedDetail.setScQtProposalAmount(
							recommendedSupplier.getProposalAmount());
					recommendDtl = removedDetail;
					
					break;
				}
			}
		}
		
		if (recommendDtl == null) {
			recommendDtl = new ScQtRecommendDtl();
			recommendDtl.setSupplier(supplier);
			recommendDtl.setShortlistedSupplier(recommendedSupplier);
			recommendDtl.setScQtRecommend(scQtRecommend);
			
			ScQtProposalAmount scQtProposalAmount 
						= recommendedSupplier.getProposalAmount();
			scQtProposalAmount.setScQtProposal(
					recommendedSupplier.getQtEvaluationResult().getScQtProposal());
			recommendDtl.setScQtProposalAmount(scQtProposalAmount);
		}
		
		ScQtProposalItem qtProposalItem 
				= getQtProposalItem(scQtRecommend, recommendedSupplier);
		
		BigDecimal awardQty = null;
		if (qtProposalItem != null) {
			awardQty = qtProposalItem.getOfferQuantity();
		} else {
			awardQty = scQtRecommend.getRequestItem().getQuantity();
		}
		recommendDtl.setAwardQty(awardQty);
		recommendDtl.setScQtProposalItem(qtProposalItem);

		return recommendDtl;
	}
	
	private ScQtProposalItem getQtProposalItem(ScQtRecommend scQtRecommend, 
								ScQtEvaluationResultDtl recommendedSupplier) {
		ScQtProposalItem qtProposalItem = null;
		if (!this.getManagedBean().isItemize()) {
			qtProposalItem = fetchQtProposalItem(scQtRecommend, recommendedSupplier);
		} else {
			qtProposalItem = recommendedSupplier.getQtEvaluationResult()
								.getScQtProposalItem();
		}
		
		return qtProposalItem;
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

	private void checkAndDeleteFromRemovedList(
			List<ScQtRecommendDtl> removedList, ScQtRecommendDtl recommendDtl) {
		if (CollectionUtils.isNotEmpty(removedList)) {
			List<ScQtRecommendDtl> excludedList = new ArrayList<ScQtRecommendDtl>();
			for (ScQtRecommendDtl detail : removedList) {
				Long supplierId = recommendDtl.getSupplier().getSupplierId();
				if (supplierId.equals(detail.getSupplier().getSupplierId())) {
					detail.setDeleted(false);
					recommendDtl = detail;
					excludedList.add(detail);
				}
			}
			if (CollectionUtils.isNotEmpty(excludedList)) {
				removedList.removeAll(excludedList);
			}
		}
	}

	private void saveByItem(ScQtFinalization scQtFinalization) {
		int cancelledItemsCount = 0;
		String decisionTypeCode = scQtFinalization.getDecision()
									.getParameterCode();
		boolean isSubmit = this.getManagedBean().isSubmit();
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		TplSpecZoneDtl selectedZone = this.getManagedBean().getCurrentZone();
		List<ScQtRecommend> scQtRecommendList = new ArrayList<ScQtRecommend>();
		scQtRecommendList.addAll(scQtFinalization.getScQtRecommends());
		
		// to set the recommend details for each recommend
		for (ScQtRecommend scQtRecommend : scQtRecommendList) {
			if (scQtRecommend.isCancelled()) {
				++cancelledItemsCount;
			}
			if (isZonal && !(scQtRecommend.getTplSpecZoneDtl().getSpecZoneId()
								.equals(selectedZone.getSpecZoneId())) && !isSubmit) {
				continue;
			}
			setItemHigherAuthorityReqStatus(scQtRecommend);
			
			if (ParameterConst.DECISION_RECOMMEND_SUPPLIER
							.equals(decisionTypeCode)) {
				if (!(scQtRecommend.isAwardedItem() ||
							scQtRecommend.isCancelled())) {
					validateAwardOfItem(scQtRecommend);
				}
			}

			List<ScQtRecommendDtl> removedList 
				= scQtRecommend.getRemoveSupplierList();
			List<ScQtRecommendDtl> recommendDtls 
				= scQtRecommend.getRecommendDtls();
			if (CollectionUtils.isNotEmpty(recommendDtls)) {
				for (ScQtRecommendDtl scQtRecommendDtl : recommendDtls) {
					checkAndDeleteFromRemovedList(removedList, scQtRecommendDtl);
					setUserDetails(scQtRecommendDtl);
				}
			}
		}
		if (cancelledItemsCount == scQtRecommendList.size()) {
			throw new EpException(MessageConst.ERROR_760341);
		}
		
		scQtFinalization.setScQtRecommends(scQtRecommendList);
	}

	/**
	 * Validate, save the selected quotation/tender supplier finalization
	 * details and update to BPM
	 * 
	 * @param outcome
	 *            the task to be performed by BPM
	 */
	public void submitSuppFinalization(String outcome) {
		debugLogger("submitSuppFinalization");
		this.getManagedBean().setAction(outcome);
		
		Long currentStatusId = getManagedBean().getCurrentStatusId();
		if (QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION
					.equals(currentStatusId) || 
				QTStatusConst.SF_PENDING_ESCALATION_TO_BPK.equals(currentStatusId)) {
			this.getManagedBean().setSubmit(true);
		}

		SuppFinalizationReqDTO reqDTO = preSubmitValidation();
		// TODO To be removed - merge supplier finalization method doc types to cater old QTs
		if (QTStatusConst.SF_PENDING_SIGN_OFF.equals(currentStatusId) || QTStatusConst.DA_PENDING_SIGN_OFF.equals(currentStatusId)) {
			if (CollectionUtils.isNotEmpty(
					this.getManagedBean().getCommitteeList())) {
				int totalMembersEndorsed = 0;
				int totalMembersAttended = 0;
				boolean isChairperson = false;
				for (ScQtCommitteeActivity scQtCommitteeActivity 
									: getManagedBean().getCommitteeList()) {
					if (scQtCommitteeActivity.getScCommitteeMember().getPmUser().getUserId()
							.equals(getFacesScopes().getUserSessionBean().getUserId())) {
						if (scQtCommitteeActivity.getScCommitteeMember()
								.getMemberRole().getParameterCode().equals(
										ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
							isChairperson = true;
						}
					}
					if (scQtCommitteeActivity.isAttended()) {
						++totalMembersAttended;
					}
					if (scQtCommitteeActivity.isSignedOff() && 
							!scQtCommitteeActivity.getScCommitteeMember()
							.getMemberRole().getParameterCode().equals(
									ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
						++totalMembersEndorsed;
					}
				}
				
				if (isChairperson && 
						totalMembersEndorsed != 
								(totalMembersAttended - 1)) {
					throw new EpException(MessageConst.ERROR_760265);
				}
			}
			
			String committeeType = getManagedBean().getCommitteeType();
			if (StringUtils.isNotBlank(committeeType) && 
					isDigiSigningRequired(committeeType + QTConstants.CHAIRPERSON,
											this.getManagedBean().getCommitteeList())) {
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			} else {
				updateBPMTask(outcome, null, reqDTO);
			}
		} else {
			signOff();
			updateBPMTask(outcome, null, reqDTO);
		}
	}
	
	private SuppFinalizationReqDTO preSubmitValidation() {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		SuppFinalizationReqDTO reqDTO = null;

		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		String decisionTypeCode = scQtFinalization.getDecision()
				.getParameterCode();
		String finalizationMethod = getManagedBean().getFinalizationMethod();
		boolean decisionRecommendSupplier = ParameterConst.DECISION_RECOMMEND_SUPPLIER
				.equals(decisionTypeCode);	
		boolean decisionFwdToHigherAuth = ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY
				.equals(decisionTypeCode);		
		List<ScQtMeeting> meetingList = this.getManagedBean().getMeetingList();

		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
		} finally {
		}

		ScQtCommittee scQtCommittee = null;
		EpException epException = null;
		
		if (!getManagedBean().isInitialDecisionFwdToBPK()) {
			if (QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION
					.equals(getManagedBean().getCurrentStatusId())) {
				
				validateFinalizationReports();
				
				if (CollectionUtils.isNotEmpty(meetingList)) {
					boolean atleastOneMeetingIsDone = false;
					for (ScQtMeeting meeting : meetingList) {
						if ((DbValueConst.RECORD_STATUS_ACTIVE
								== meeting.getRecordStatusFlg()) && 
										QTConstants.SC_MEETING_DONE_STATUS_ID
											.equals(meeting.getMeetingStatus()
													   .getStatusId())) {
							atleastOneMeetingIsDone = true;
						}
					}
					if (!atleastOneMeetingIsDone) {
						epException = new EpException(MessageConst.ERROR_760292);
					}
				} else {
					epException = new EpException(MessageConst.ERROR_760291);
				}
				
				if (epException == null) {
				
					// Validate Preparation tab details for committee members attendance
					try {
						validateInfo(0);
					} catch (EpException epExcep) {
						epException = epExcep;
					} catch (Exception e) {
						navigateToPreparationTab();
						saveAndReloadFinalizationDetails(
								qtSuppFinalizationSO, reqDTO);					
						throw e;
					} finally {
						this.getManagedBean().setPrepareActiveIndex(1);
					}
				}
			}
			
			scQtCommittee = preparationDtlsInfo();
		}
		
		// Save Preparation and Supplier finalization tab(s) data 
		// before throwing exception
		reqDTO = validateInfo(2);
		reqDTO.setDeletedMeetingsList(
				this.getManagedBean().getDeletedMeetingList());
		if (scQtCommittee != null) {
			scQtCommittee.setQtCommitteRemarks(
					reqDTO.getScQtCommittee().getQtCommitteRemarks());
		}
		reqDTO.setScQtCommittee(scQtCommittee);
		
		if (epException != null) {
			saveAndReloadFinalizationDetails(
						qtSuppFinalizationSO, reqDTO);
			
			navigateToPreparationTab();
			qtSuppFinalizationSO = null;
			
			throw epException;
		}

		if (decisionRecommendSupplier || 
						decisionFwdToHigherAuth) {
			boolean supplierSelected = true;
			boolean minSuppliersExist = true;
			String recommendTypeCode = scQtFinalization.getRecommendedTypeId()
					.getParameterCode();
			
//			if (decisionRecommendSupplier || 
//					this.getManagedBean().isForwardToBpk()) {
//				scQtFinalization.setHigherCommitte(null);
//				scQtFinalization.setHigherCommitteStr(null);
//			}
			if (decisionRecommendSupplier) {
				scQtFinalization.setHigherCommitte(null);
				scQtFinalization.setHigherCommitteStr(null);
				scQtFinalization.setHigherSecretary(null);
				scQtFinalization.setHigherSecretaryStr(null);
				scQtFinalization.setReason(null);
				scQtFinalization.setReasonStr(null);
			}			

			boolean zonalPackageItemsCancelled = getManagedBean().isItemCancelled();
			
			// Check if supplier has been selected before submission
			if (!zonalPackageItemsCancelled && 
					ParameterConst.RECOMMEND_TYPE_PACKAGE
						.equals(recommendTypeCode) && 
					!this.getManagedBean().isDisableSupplierSelection()) {
				TplSpecZoneDtl zoneDtl = this.getManagedBean().getCurrentZone();
				
				if (StringUtils.isBlank(finalizationMethod)) {
					setZoneAndTabIndexOnException(zoneDtl);
					throw new EpException(MessageConst.ERROR_760211);
				}
				if (QTConstants.SF_SINGLE.equals(getManagedBean()
						.getSelectionMode())
						&& getManagedBean().getRecommendedSupplier() == null) {
					supplierSelected = false;
				} else if (QTConstants.SF_MULTIPLE.equals(
								getManagedBean().getSelectionMode())) {
					if (CollectionUtils.isEmpty(
							getManagedBean().getRecommendedSupplierList())) {
						supplierSelected = false;
						minSuppliersExist = false;
					} else if (getManagedBean().getRecommendedSupplierList()
									.size() < 2) {
						supplierSelected = false;
						minSuppliersExist = false;
					}
				}
			}

			if (!supplierSelected) {
				setZoneAndTabIndexOnException(getManagedBean().getCurrentZone());
				qtSuppFinalizationSO = null;
				PmParameter _finalizationMethod = getPmParameterByTypeAndCode(
						ParameterTypeConst.FINALIZATION_RECOMMEND_BY,
						finalizationMethod);
				
				if (!minSuppliersExist) {
					throw new EpException(MessageConst.ERROR_760243,
							new String[] { getParameterDesc(_finalizationMethod) });
				} else {
					throw new EpException(MessageConst.ERROR_760206);
				}
			}
		} else if (ParameterConst.DECISION_CANCEL
							.equals(decisionTypeCode) && 
						CollectionUtils.isEmpty(
								scQtFinalization.getScQtFinalizationRemarks())) {
			navigateToSupplierFinalizationTab();
			initializeFinalizationRemarks();
			
			// Remarks are mandatory for QT cancellation
			throw new EpException(MessageConst.ERROR_760360);
		}
		
		return reqDTO;
	}
	
	// Save and reload suppplier finalization data
	private void saveAndReloadFinalizationDetails(IQTSuppFinalizationSO qtSuppFinalizationSO,
												  SuppFinalizationReqDTO reqDTO) {
		try {
			if (qtSuppFinalizationSO == null) {
				qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);
			}	
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
									.saveSupplierFinalization(reqDTO);
			reloadData(resDTO);				
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	private void updateBPMTask(String outcome, PmDigiSign digiSign, SuppFinalizationReqDTO reqDTO) {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		int activeIndex = this.getManagedBean().getActiveIndex();
		String taskId = this.getManagedBean().getTaskId();
		SuppFinalizationResDTO resDTO = null;
		
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			if (reqDTO == null) {
				reqDTO = validateInfo(activeIndex);
			}			
			reqDTO.setScQt(getManagedBean().getScQt());
			reqDTO.setRefinalize(isQTRefinalized());
			reqDTO.setOutcome(outcome);
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(taskId);
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setRoleCode(getManagedBean().isInitialDecisionFwdToBPK() ? 
										StringUtils.EMPTY : getCommitteeRoleCode());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setCurrentFinalizationStatus(
					getManagedBean().getCurrentStatusId());
			reqDTO.setInitialDecisionFwdHighAuth(
					getManagedBean().isInitialDecisionFwdHighAuth());
			
			boolean hasPendingLOA = false;
			resDTO = qtSuppFinalizationSO.retrievePendingLOA(reqDTO);
			hasPendingLOA = resDTO.isHasPendingLOA();
			
			reqDTO.setHasPendingLoa(hasPendingLOA);		
//			reqDTO.setScQtCommitteeActivity(
//					this.getManagedBean().getSignedOffCommitteeActivity());
			
			reqDTO.setDigiSignData(digiSign);

			ScQtCommittee scQtCommittee = null;
			if (reqDTO.getScQtCommittee() == null && 
					!getManagedBean().isInitialDecisionFwdToBPK() && 
						digiSign != null) {
				scQtCommittee = preparationDtlsInfo();
				reqDTO.setScQtCommittee(scQtCommittee);
			}
			if (null != digiSign) {
				updatePmDigiSignData(digiSign,
						ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION,
						getManagedBean().getScQt().getQtId(), getManagedBean()
								.getScQt().getQtNo());
				signOff();
			}	

			Long currentStatusId = getManagedBean().getCurrentStatusId();
			String decisionTypeCode = getManagedBean().getScQtFinalization()
										   .getDecision().getParameterCode();
			if ( (QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION
						.equals(currentStatusId) || 
					QTStatusConst.SF_PENDING_ESCALATION_TO_BPK
						.equals(currentStatusId)) && 
							ParameterConst.DECISION_RECOMMEND_SUPPLIER
								.equals(decisionTypeCode) ) {
				try {
					debugLogger("Generating all reports...");
					downloadAllReports();	
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			resDTO = qtSuppFinalizationSO
					.submitSupplierFinalization(reqDTO);

			if (resDTO.isSuccessful()) {
				String variable1 = getParameterDesc(
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION)
						+ " " + getManagedBean().getScQt().getQtNo();
				String variable2 = getMessageCommon("lbl.sc.supplier.finalization");

				commonMessageAcknowledmentPage(MessageConst.INFO_700026,
						variable1, variable2);
			}

		} finally {
			qtSuppFinalizationSO = null;
		}

		/*
		 * ScQtFinalization scQtFinalization =
		 * getManagedBean().getScQtFinalization(); String decisionType =
		 * scQtFinalization.getDecision().getParameterCode();
		 * 
		 * if(decisionType.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)){
		 * submitRecommendSupplier(scQtFinalization);
		 * 
		 * }else if(decisionType.equals(ParameterConst.DECISION_RE_EVALUATION)){
		 * submitReEvaluateQT(scQtFinalization);
		 * 
		 * }else if(decisionType.equals(ParameterConst.DECISION_RE_PUBLISH)){
		 * submitRePublishQT(scQtFinalization);
		 * 
		 * }else if(decisionType.equals(ParameterConst.DECISION_CANCEL)){ //TODO
		 * - called Sagar API for Cancel QT }
		 */

	}

	private void submitRecommendSupplier(ScQtFinalization scQtFinalization) {
		// TODO - BPM
		String recommendType = scQtFinalization.getRecommendedTypeId()
				.getParameterCode();

		if (scQtFinalization.isHigherAuthReq()) {
			// BPM Send task to DO/commitee for the re-finalize by higher
			// authority
		} else {
			if (recommendType.equals(ParameterConst.RECOMMEND_TYPE_PACKAGE)) {
				// BPM send task for award/online bidding/multiple LOI by
				// package
			} else if (recommendType.equals(ParameterConst.RECOMMEND_TYPE_ITEM)) {
				// BPM send task for award/online bidding/multiple LOI by items
			}
		}
	}

	private void submitReEvaluateQT(ScQtFinalization scQtFinalization) {
		// TODO - BPM
		/*
		 * String reEvaluateBy = scQtFinalization.getReEvaluateCommitte();
		 * boolean reEvaluateTech = getManagedBean().isReEvaluateReqForTech();
		 * boolean reEvaluateFin = getManagedBean().isReEvaluateReqForFin();
		 * 
		 * if(reEvaluateBy.equals(SelectionType.New)){ //BPM send task to
		 * maintain committee } if(reEvaluateTech && reEvaluateFin){ //BPM send
		 * task to reEvaluate technical and financial
		 * 
		 * }else if(reEvaluateTech && !reEvaluateFin){ //BPM send task to
		 * reEvaluate technical only
		 * 
		 * }else if(!reEvaluateTech && reEvaluateFin){ //BPM send task to
		 * reEvaluate financial only }
		 */
	}

	private void submitRePublishQT(ScQtFinalization scQtFinalization) {
		// TODO - BPM
		boolean specChanges = scQtFinalization.isSpecChangeReq();

		if (specChanges) {
			// BPM send task to whole SC
			// BPM notify DO, and publication approver.
		} else {
			// BPM send task to DO which before publication stage and required
			// to re-schedule.
			// (not required to obtained like budget, bpk approval)
			// (not allow change info which related all approval - budget, bpk
			// etc)
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
			
			int endorsementReportCount = 0;
			ScQtFinalizationReport coverReport = null;
			List<ScQtFinalizationReport> endorsementReports = 
								new ArrayList<ScQtFinalizationReport>();
			for (ScQtFinalizationReport report : finalizationReports) {
				String docCategory = report.getDocCategory();
				String docDefinitionKey = null;
				if (report.getPmDocument() != null) {
					docDefinitionKey = report.getPmDocument()
											 .getDocDefinitionKey();
				}
				if (SourcingConstants.DOC_DEF_KEY_COVER
						.equals(docDefinitionKey)) {
					coverReport = report;
				} else if (ParameterConst.DOCUMENT_CATEGORY_ENDORSEMENT_REPORT
								.equals(docCategory)) {
					endorsementReports.add(report);
					++endorsementReportCount;
				}
			}
			if (coverReport != null && 
					getManagedBean().isInitialDecisionFwdToBPK()) {
				finalizationReports.remove(coverReport);
			}
			scQtFinalization.setScQtFinalizationReports(
									finalizationReports);
			
			if (!getManagedBean().isInitialDecisionFwdToBPK()) {
				int totalFinalizationRounds = findFinalizationCount();
				if (endorsementReportCount < totalFinalizationRounds) {
					String reportTitle = ResourceBundleUtils.getMessage("lbl.sc.committee.endorsement.report", 
				 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, myLocale) 
				 			+ SPACE + totalFinalizationRounds;
					addFinalizationReport(reportTitle);
				} else {
					finalizationReports.removeAll(endorsementReports);
					
					// Sort the reports as per the last updated date
					Collections.sort(endorsementReports, 
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
					
					if (endorsementReports.size() > 0) {
						endorsementReports.get(endorsementReports.size() - 1)
										  .setSelected(true);
					}
					
					finalizationReports.addAll(endorsementReports);
				}
			}
			
			if (finalizationReports != null) {
				getManagedBean().setDocumentList(finalizationReports);
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	/**
	 * Add ScQtFinalizationReport
	 */
	private void addFinalizationReport(String reportTitle) {
		debugLogger("addScQtFinalizationReport");

		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		
		List<ScQtFinalizationReport> scQtFinalizationReports 
						= scQtFinalization.getScQtFinalizationReports();	
		
		ScQtFinalizationReport report = new ScQtFinalizationReport();
		report.setTitle(reportTitle);
		report.setScQtFinalization(scQtFinalization);
		report.setScQtFinalizationReportAttachment(
					new ScQtFinalizationReportAttachment());
		report.setSelected(true);
		
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
					ParameterConst.DOCUMENT_CATEGORY_ENDORSEMENT_REPORT); // DR
		} finally {
			parameterSO = null;
		}

		scQtFinalizationReports.add(report);
	}	

	/**
	 * To handle file upload event in Briefing Paper/Kertas Taklimat tab
	 * 
	 * @param event
	 *            file upload event object
	 * @throws FileNotFoundException
	 *             exception that would be thrown if the file being updated does
	 *             not exist
	 * @throws IOException
	 *             exception that would be thrown if an I/O error is encountered
	 *             while uploading the file
	 */
	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;

			ScQtFinalization scQtFinalization = this.getManagedBean()
					.getScQtFinalization();
			ScQtFinalizationAttachment attachment = scQtFinalization
					.getScQtFinalizationAttachment();
			if (null == attachment) {
				attachment = new ScQtFinalizationAttachment();
				attachment.setScQtFinalization(scQtFinalization);

				scQtFinalization.setScQtFinalizationAttachment(attachment);
			}

			attachment
					.setDocNo(scQtFinalization.getScQt().getQtNo());
			scAttachment = attachment;
			uploadAttachment(event.getFile(), scAttachment);
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
	public void handleReportFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		debugLogger("handleFileUpload");
		
		if (event.getFile() != null) {
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
		
		ScQtFinalizationReportAttachment reportAttachment = scQtFinalizationReport
				.getScQtFinalizationReportAttachment();
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
					QTStatusConst.SF_PENDING_ESCALATION_TO_BPK
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
						QTStatusConst.SF_PENDING_ESCALATION_TO_BPK
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
	
	 
    public StreamedContent getSuppFinalizationEndorsementReportStreamContent() throws Exception {
		debugLogger("getSuppFinalizationEndorsementReportStreamContent");
		ScQtFinalizationReportAttachment reportAttachment = new ScQtFinalizationReportAttachment();
		reportAttachment.setDocNo(getManagedBean().getScQt().getQtNo());

		generateEndorsementReportFromVelocityToPdf(getManagedBean()
				.getScQtFinalization(), (ScAttachment) reportAttachment);
		StreamedContent streamContent = getAttachmentStreamContent(
				reportAttachment, CommonConstants.FILE_CONTENT_TYPE_PDF);

		return streamContent;
    }	

	public ScQtFinalizationReportAttachment generateDocumentAttachment(
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

			} else if (SourcingConstants.DOC_DEF_KEY_OPENNING_REPORT
					.equals(docDefinitionKey)) {
				// FIXME - get from Opening report

			} else if (SourcingConstants.DOC_DEF_KEY_TECHNICAL_EVAALUATION_REPORT
					.equals(docDefinitionKey)) {
				// FIXME - get from Technical Evaluation report

			} else if (SourcingConstants.DOC_DEF_KEY_FINANCIAL_EVAALUATION_REPORT
					.equals(docDefinitionKey)) {
				// FIXME - get from Financial evaluation report
			}
			scQtFinalizationReport
					.setScQtFinalizationReportAttachment(reportAttachment);
		} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
						.equals(definitionType)) {
			reportAttachment = scQtFinalizationReport
									.getScQtFinalizationReportAttachment();
		}
		
		return reportAttachment;
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
		// context.put("fileNo", scQt.getFileNo());
		
//		String departmentOrgName = null;
//		if (scQt.getDepartmentOrgProfile() != null) {
//			departmentOrgName = scQt.getDepartmentOrgProfile().getOrgName();
//		} else {
//			departmentOrgName = StringUtils.EMPTY;
//		}
//		context.put("jabatan", departmentOrgName);
		
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

		List<ScQtMeeting> scQtMeetings = new ArrayList<ScQtMeeting>();
		IQTMeetingSO meetingSO = null;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setCommitteType(committeeType);
			MeetingResDTO resDTO = meetingSO.retrieveMeetingDls(reqDTO);
			scQtMeetings = resDTO.getMeetingDtls();
		} finally {
			meetingSO = null;
		}
		for (ScQtMeeting meeting : scQtMeetings) {
			meeting.setMeetDateStr(dateSDF.format(meeting.getMeetingDate()));
			DateFormat formatter = DateFormat.getInstance();
			formatter = DateFormat.getTimeInstance(); // time only
			meeting.setMeetTimeStr(formatter.format(meeting.getMeetingDate()));
		}
		context.put("scQtMeetings", scQtMeetings);
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
		
//		String departmentOrgName = null;
//		if (scQt.getDepartmentOrgProfile() != null) {
//			departmentOrgName = scQt.getDepartmentOrgProfile().getOrgName();
//		} else {
//			departmentOrgName = StringUtils.EMPTY;
//		}
//		context.put("jabatan", departmentOrgName);	
		
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
		
		boolean isInitialDecisionFwdToBPK = this.getManagedBean().isInitialDecisionFwdToBPK();
		context.put("initialDecisionFwdToBPK", isInitialDecisionFwdToBPK);

		if (!isInitialDecisionFwdToBPK) {
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
					
					if (CollectionUtils.isNotEmpty(committeeActivitiesList)) {
						for (ScQtCommitteeActivity committeeActivity : committeeActivitiesList) {
							setDefaultValueForCommitteeActivityIfNull(committeeActivity);
							committeeActivities.add(committeeActivity);
						}
					} else {
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
				}
			} finally {
				meetingSO = null;
			}
			context.put("committeeActivities", committeeActivities);
		}
		
		context.put("bean", this);
		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_FZ_OFFERED_QT_DTLS, context,
				new FileOutputStream(getActualFilePath(attachment)));
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
		
//		String departmentOrgName = null;
//		if (scQt.getDepartmentOrgProfile() != null) {
//			departmentOrgName = scQt.getDepartmentOrgProfile().getOrgName();
//		} else {
//			departmentOrgName = StringUtils.EMPTY;
//		}
//		context.put("jabatan", departmentOrgName);
		
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
		
//		List<ScQtCategory> categoryResults = new ArrayList<ScQtCategory>();
		IQTDetailSO qtDetailsSO;
		try {
			qtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			tenderReqDTO.setQtdetail(this.getManagedBean().getScQt());
			QuationTenderResDTO tenderResDTO = qtDetailsSO
					.retriveQTSuppQulCriDetails(tenderReqDTO);
//			categoryResults = tenderResDTO.getCategoryResultBean()
//					.getResultList();
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
				// reqDTO.setQtId(getManagedBean().getQt());
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

		/*
		 * List<BriefingZoneBean> bsvZoneBeans = new
		 * ArrayList<BriefingZoneBean>(); IQTNoticeBoardSO qtNoticeBoardSO =
		 * null; NoticeBoardReqDTO noticeBoardReqDTO = new NoticeBoardReqDTO();
		 * NoticeBoardResDTO noticeBoardResDTO = new NoticeBoardResDTO();
		 * 
		 * if (!scQt.isZonal()) { if (null !=
		 * noticeBoardResDTO.getLocCriteria()) { List<PmState> states = new
		 * ArrayList<PmState>(); for (TplSpecLoc locCriteria :
		 * noticeBoardResDTO.getLocCriteria()) { PmState pmState =
		 * locCriteria.getPmState(); TreeNode root = null; if
		 * (!states.contains(pmState)) { root = new DefaultTreeNode("state",
		 * pmState, null); root.setExpanded(true); locations.add(root); }
		 * PmDivision division = locCriteria.getPmDivision(); TreeNode
		 * divisonNode = null; if (division != null) { divisonNode = new
		 * DefaultTreeNode("division", division, root);
		 * divisonNode.setExpanded(true); } PmDistrict pmDistrict =
		 * locCriteria.getPmDistrict(); TreeNode districtNode = null; if
		 * (pmDistrict != null) { if (divisonNode == null) { districtNode = new
		 * DefaultTreeNode("district", pmDistrict, root);
		 * districtNode.setExpanded(true); } else { districtNode = new
		 * DefaultTreeNode("district", pmDistrict, divisonNode);
		 * districtNode.setExpanded(true); } } states.add(pmState); } } ScQtBsv
		 * scQtBsv = noticeBoardResDTO.getScQtBsv();
		 * 
		 * briefingZoneBean = new BriefingZoneBean(null,
		 * noticeBoardResDTO.getBriefingResultBean(),
		 * noticeBoardResDTO.getSiteVisitResultBean(), scQtBsv); } else if
		 * (scQt.isZonal()) { List<TplSpecZoneDtl> zoneDtls =
		 * noticeBoardResDTO.getZones(); if (null != zoneDtls) { isZone = true;
		 * for (TplSpecZoneDtl specZoneDtl : zoneDtls) { BriefingReqDTO
		 * briefingReqDTO = new BriefingReqDTO();
		 * briefingReqDTO.setQtId(scQt.getQtId());
		 * briefingReqDTO.setSpecZoneId(specZoneDtl .getSpecZoneId());
		 * IQTBriefingSO briefingSO = EpEjbBusinessDelegate
		 * .getInstance().getServiceObject( ModuleCodeConst.SUPPLIER_SELECTION,
		 * IQTBriefingSO.class); BriefingResDTO briefingResDTO = briefingSO
		 * .retrieveBriefingSiteVisitDtls(briefingReqDTO); ScQtBsv scQtBsv =
		 * briefingResDTO.getScQtBsv(); briefingZoneBean = new BriefingZoneBean(
		 * specZoneDtl, briefingResDTO.getBriefingList(),
		 * briefingResDTO.getSiteVisitList(), scQtBsv);
		 * bsvZoneBeans.add(briefingZoneBean); } } }
		 * 
		 * context.put("locations", locations); context.put("isZone", isZone);
		 * context.put("bsvZoneBeansList", bsvZoneBeans);
		 */

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
//			this.getManagedBean().setScQt(resDTO.getScQt());
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

	private void generateEndorsementReportFromVelocityToPdf(
			ScQtFinalization scQtFinalization, ScAttachment attachment)
			throws Exception {

		debugLogger("generateEndorsementReportFromVelocityToPdf");
		populateAttachmentDetails(attachment);

		VelocityContext context = new VelocityContext();
		
		ScQt scQt = getManagedBean().getScQt();
		boolean isZonal = scQt.isZonal();
		boolean isSupplierFinalization = false;
		boolean isForwardToHigherAuthority = false;
		boolean isRepublish = false;
		boolean isReEvaluate = false;
		boolean isCancel = false;
		
		String decisionTypeCode = scQtFinalization.getDecision()
									.getParameterCode();
		if (ParameterConst.DECISION_RECOMMEND_SUPPLIER
				.equals(decisionTypeCode)) {
			isSupplierFinalization = true;
		} else if (ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY
						.equals(decisionTypeCode)) {
			isForwardToHigherAuthority = true;
		} else if (ParameterConst.DECISION_RE_PUBLISH
						.equals(decisionTypeCode)) {
			isRepublish = true;
			context.put("requireSpecChanges", scQtFinalization.isSpecChangeReq());
		} else if (ParameterConst.DECISION_RE_EVALUATION
						.equals(decisionTypeCode)) {
			isReEvaluate = true;
			context.put("documentCompliance", scQtFinalization.isReEvaluateCompReq());
			context.put("financial", scQtFinalization.isReEvaluateFinReq());
			
			if ((ParameterConst.EVALUATION_TYPES_ONE_TIER
					.equals(scQt.getEvaluationType()) && 
						scQt.isTechEvaluationReq()) || 
					ParameterConst.EVALUATION_TYPES_TWO_TIER
						.equals(scQt.getEvaluationType())) {
				context.put("hasTechnical", true);
				context.put("technical", scQtFinalization.isReEvaluateTechReq());
			} else {
				context.put("hasTechnical", false);
			}
		} else if (ParameterConst.DECISION_CANCEL
						.equals(decisionTypeCode)) {
			isCancel = true;
		}
		
		context.put("isSupplierFinalization", 
					 isSupplierFinalization);
		context.put("isForwardToHigherAuthority", 
					 isForwardToHigherAuthority);
		context.put("isReEvaluate", isReEvaluate);	
		context.put("isRepublish", isRepublish);
		context.put("isCancel", isCancel);

		if (scQtFinalization.getDecision() != null) {
			context.put("meetingDecision", getParameterDesc(
					scQtFinalization.getDecision(), myLocale));
		}

		if (scQtFinalization.getRecommendedTypeId() != null) {
			context.put("selectionBasedOn", getParameterDesc(
					scQtFinalization.getRecommendedTypeId(), myLocale));
		}
		
		if (scQtFinalization.getFinalizationMethod() != null) {
			context.put("finalizationMethod", getParameterDesc(
					scQtFinalization.getFinalizationMethod(), myLocale));
		}	
		
		if (scQtFinalization.getFinalizationMethod() != null) {
			String loiLoaPreparedBy = null;
			
			if (StringUtils.isNotBlank(
					scQtFinalization.getLoiLoaBy())) {
				PmParameter param = getPmParameterByTypeAndCode(
						ParameterTypeConst.FINALIZATION_LOILOA_PREPARED_BY, 
						scQtFinalization.getLoiLoaBy());
				if (param != null) {
					loiLoaPreparedBy = getParameterDesc(param, myLocale);
				}
			}
			context.put("loiLoaPreparedBy", loiLoaPreparedBy);
		}	
		
		if (isForwardToHigherAuthority) {
			if (scQtFinalization.getReason() != null) {
				context.put("reason", getReasonDesc(scQtFinalization.getReason()));
			}
			if (scQtFinalization.getHigherCommitte() != null) {
				context.put("higherAuthority", getParameterDesc(
							scQtFinalization.getHigherCommitte(), myLocale));
			}
		}
		
		ScQtMeeting meeting = null;
		ScQtCommittee committee = getManagedBean().getScQtCommittee();
		if (CollectionUtils.isNotEmpty(
				committee.getScQtMeetings())) {
			for (ScQtMeeting scQtMeeting 
					: committee.getScQtMeetings()) {
				if (scQtMeeting.getRecordStatusFlg()
						.equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
					meeting = scQtMeeting;
					break;
				}
			}
		}
		
		String meetingDate = null;
		String meetingLocation = null;
		if (meeting != null) {
			meetingDate = new SimpleDateFormat("yyyy/MM/dd HH:mm")
								 .format(meeting.getMeetingDate());
			meetingLocation = meeting.getMeetingLocation();
		}
		context.put("meetingDate", meetingDate);
		context.put("meetingLocation", meetingLocation);

		String loiloaPrepared = "";
		if (scQtFinalization.getLoiLoaBy() != null) {
			loiloaPrepared = scQtFinalization.getLoiLoaBy();
		}
		context.put("loiloaPrepared", loiloaPrepared);
		
		attachment.setFileName("Kertas_Keputusan.pdf");
		populateAttachmentDetails(attachment);
		List<ScQtRecommend> recommendedItems = scQtFinalization.getScQtRecommends();
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat amountDF = new DecimalFormat(
						commonUtils.getDisplayNumberFormat(
								CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		context.put("numberFormatter", amountDF);
		
		boolean isQTRefinalized = isQTRefinalized();
		FinalizationEndorsementReportDTO reportDTO = null;
		Map<TplSpecZoneDtl,FinalizationEndorsementReportDTO> zoneDetailsMap = null;
		
		Map<Long,TplSpecZoneDtl> zoneMap = getManagedBean().getZoneMap();
		
		if (recommendedItems != null) {
			if (!getManagedBean().isItemize()) {
				List<ScQtEvaluationResultDtl> recommendedSuppliers = null;
				ScQtEvaluationResultDtl awardedSupplier = 
							getManagedBean().getRecommendedSupplier();
				if (isZonal) {
					if (awardedSupplier != null && 
							!awardedSupplier.isAwareded()) {
						saveByPackageByZone(scQtFinalization);
					}					
					
					zoneDetailsMap = new HashMap<TplSpecZoneDtl,FinalizationEndorsementReportDTO>();
					Map<Long,List<ScQtRecommend>> scQtRecommendsByZone
							= scQtFinalization.getScQtRecommendsByZone();
					
					for (Long zoneId : zoneMap.keySet()) {
						reportDTO = new FinalizationEndorsementReportDTO();
						
						List<ScQtRecommend> items = scQtRecommendsByZone.get(zoneId);
						List<ScQtEvaluationResultDtl> shortlistedSuppliers = null;
						if (MapUtils.isNotEmpty(
								getManagedBean().getPkgShortlistedSupppliersByZone())) {
							shortlistedSuppliers = getManagedBean()
									.getPkgShortlistedSupppliersByZone().get(zoneId);
						}
						
						if (!items.get(0).isCancelled() && 
								CollectionUtils.isNotEmpty(shortlistedSuppliers)) {
							items.get(0).setShortListedSupplierList(
												shortlistedSuppliers);
							recommendedSuppliers = new ArrayList<ScQtEvaluationResultDtl>();	
							
							for (ScQtEvaluationResultDtl evalResDtl
											: shortlistedSuppliers) {
								if (isQTRefinalized()) {
									if (evalResDtl.isAwareded()) {
										recommendedSuppliers.add(evalResDtl);
									}
								} else if (evalResDtl.isAwareded() || 
												evalResDtl.isShortListed()) {
									recommendedSuppliers.add(evalResDtl);
								}	
							}
						}
						
						if (CollectionUtils.isEmpty(
									recommendedSuppliers)) {
							reportDTO.setEmpty(true);
						}
						
						reportDTO.setRecommendedSuppliers(
								new ArrayList<ScQtEvaluationResultDtl>(recommendedSuppliers));
						reportDTO.setRecommendedItems(items);
						zoneDetailsMap.put(zoneMap.get(zoneId),reportDTO);
					}
				} else {
					if (awardedSupplier != null && 
							!awardedSupplier.isAwareded()) {
						saveByPackage(scQtFinalization);
					}						
					
					reportDTO = new FinalizationEndorsementReportDTO();
					reportDTO.setEmpty(true);
					recommendedSuppliers = new ArrayList<ScQtEvaluationResultDtl>();
					List<ScQtEvaluationResultDtl> shortlistedSuppliers 
								= getManagedBean().getShortlistedPackageList();
					if (!recommendedItems.get(0).isCancelled() && 
							CollectionUtils.isNotEmpty(shortlistedSuppliers)) {
						for (ScQtEvaluationResultDtl evalResDtl
											: shortlistedSuppliers) {
							if (isQTRefinalized()) {
								if (evalResDtl.isAwareded()) {
									recommendedSuppliers.add(evalResDtl);
								}
							} else if (evalResDtl.isAwareded() || 
											evalResDtl.isShortListed()) {
								recommendedSuppliers.add(evalResDtl);
							}							
						}
					}			
					
					if (CollectionUtils.isNotEmpty(
							  recommendedSuppliers)) {
						reportDTO.setEmpty(false);
						reportDTO.setRecommendedSuppliers(
										recommendedSuppliers);
						reportDTO.setRecommendedItems(recommendedItems);
					}
				}
			} else {
				if (isZonal) {
					if (zoneDetailsMap == null) {
						zoneDetailsMap = new HashMap<TplSpecZoneDtl,FinalizationEndorsementReportDTO>();
					}
					Map<Long,List<ScQtRecommend>> scQtRecommendsByZone
								= scQtFinalization.getScQtRecommendsByZone();
					
					for (Long zoneId : zoneMap.keySet()) {
						reportDTO = new FinalizationEndorsementReportDTO();

						List<ScQtRecommend> validItemsByZone = prepareRecommendedSuppliersByItems(
																scQtRecommendsByZone.get(zoneId));
						reportDTO.setRecommendedItems(validItemsByZone);
						zoneDetailsMap.put(zoneMap.get(zoneId),reportDTO);
					}
				} else {
					reportDTO = new FinalizationEndorsementReportDTO();
					
					List<ScQtRecommend> validItems = 
							prepareRecommendedSuppliersByItems(recommendedItems);
					reportDTO.setRecommendedItems(validItems);
				}
			}
		}	
		
		if (isZonal) {
			for (TplSpecZoneDtl specZone
						: getManagedBean().getZoneMap().values()) {
				if (!zoneDetailsMap.containsKey(
						specZone)) {
					FinalizationEndorsementReportDTO tempDTO
							= new FinalizationEndorsementReportDTO();
					tempDTO.setEmpty(true);
					zoneDetailsMap.put(specZone,tempDTO);
				}
			}
		}
		
		String chairPersonName = null;
		String secretaryName = null;
		String memberName = null;
		
		for (ScQtCommitteeActivity scQtCommitteeActivity 
					: getManagedBean().getCommitteeList()) {
			if (scQtCommitteeActivity.getScCommitteeMember().getPmUser().getUserId()
					.equals(getFacesScopes().getUserSessionBean().getUserId())) {
				ScCommitteeMember commMember = scQtCommitteeActivity.getScCommitteeMember();
				if (commMember.getMemberRole().getParameterCode()
						.equals(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
					chairPersonName = commMember.getMemberName().toUpperCase();
				} else if (commMember.getMemberRole().getParameterCode()
						.equals(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
					secretaryName = commMember.getMemberName().toUpperCase();
				} else if (commMember.getMemberRole().getParameterCode()
						.equals(ParameterConst.COMMITTEE_ROLE_MEMBER)) {
					memberName = commMember.getMemberName().toUpperCase();
				}
			}
		}
		
		context.put("chairPerson", chairPersonName);
		context.put("secretary", secretaryName);
		context.put("member", memberName);
		
		context.put("reportDTO", reportDTO);
		context.put("zoneDetailsMap", zoneDetailsMap);
		context.put("numberFormatter", new DecimalFormat("#,###,##0.00"));
		context.put("itemized", getManagedBean().isItemize());
		context.put("isZonal", isZonal);		

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_FZ_ENDORSEMENT_REPORT, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}	
	
	private List<ScQtRecommend> prepareRecommendedSuppliersByItems(
						   List<ScQtRecommend> items) {
		List<ScQtRecommend> validItems = new ArrayList<ScQtRecommend>();
		if (CollectionUtils.isNotEmpty(items)) {
			List<ScQtEvaluationResultDtl> recommendedSuppliers = null;
			
			for (ScQtRecommend item : items) {
				if (!item.isCancelled()) {
					validItems.add(item);
					
					recommendedSuppliers = new ArrayList<ScQtEvaluationResultDtl>();
					List<ScQtEvaluationResultDtl> shortlistedSuppliers 
									= item.getShortListedSupplierList();
					
					if (CollectionUtils.isEmpty(
							shortlistedSuppliers)) {
						SuppFinalizationResDTO resDTO 
							= retrieveShortListedSuppliersByItem(item);
						shortlistedSuppliers = resDTO.getQualifiedByItemSuppliers();
						item.setShortListedSupplierList(shortlistedSuppliers);
					}
					if (CollectionUtils.isNotEmpty(
							  shortlistedSuppliers)) {
						for (ScQtEvaluationResultDtl evalResDtl 
									: shortlistedSuppliers) {
							if (isQTRefinalized()) {
								if (evalResDtl.isAwareded()) {
									recommendedSuppliers.add(evalResDtl);
									calculateOfferedPrice(evalResDtl, item);
								}
							} else if (evalResDtl.isAwareded() || 
											evalResDtl.isShortListed()) {
								recommendedSuppliers.add(evalResDtl);
								calculateOfferedPrice(evalResDtl, item);
							}
						}
					}
					item.setAwardSupplierList(recommendedSuppliers);
				}
			}
		}
		
		return validItems;
	}

	private void setDefaultValueForCommitteeActivityIfNull(ScQtCommitteeActivity committeeActivity) {
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
	
	// Kertas Taklimat
	/*
	 * private ScQtDocReport retrieveBriefingPaperByDocId(PmDocument pmDocument)
	 * { IQTSuppFinalizationSO qtSuppFinalizationSO; ScQtDocReport scQtReport =
	 * null; try { qtSuppFinalizationSO =
	 * BusinessDelegate.getInstance().getServiceObject
	 * (ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
	 * 
	 * SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
	 * reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
	 * reqDTO.setDocumentCategory
	 * (ParameterConst.DOCUMENT_CATEGORY_BRIEFING_PAPER);
	 * reqDTO.setDocumentId(pmDocument.getDocumentId());
	 * 
	 * SuppFinalizationResDTO resDTO =
	 * qtSuppFinalizationSO.retrieveReport(reqDTO); if (resDTO.getScQtReport()
	 * != null) { scQtReport = resDTO.getScQtReport(); } } finally {
	 * qtSuppFinalizationSO = null; } return scQtReport; }
	 */

	private void retrieveSupplierFinalizationDtls(ScQt scQt) {
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

			List<TplSpecZoneDtl> zoneDetails = 
					getManagedBean().getZoneDetails();
			reqDTO.setZoneDetails(zoneDetails);
			reqDTO.setRefinalize(isQTRefinalized());

			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.retrieveSupplierFinalizationDtls(reqDTO);
			ScQtFinalization scQtFinalization = resDTO.getScQtFinalization();

			if (CollectionUtils.isNotEmpty(zoneDetails)
					&& this.getManagedBean().getCurrentZone() == null) {
				this.getManagedBean().setCurrentZone(zoneDetails.get(0));
			}
			// this.getManagedBean().setShortlistedItemList(resDTO.getQualifiedByItemSuppliers());
			// this.getManagedBean().setShortlistedItemsByZone(resDTO.getQualifiedByItemMap());

			if (scQtFinalization != null) {
				this.getManagedBean().setScQtFinalization(scQtFinalization);
				
				List<String> rejectedRecommendDtlIds = getRejectedRecommendDtlIds();
				if (CollectionUtils.isNotEmpty(
								rejectedRecommendDtlIds)) {
					SuppFinalizationReqDTO _reqDTO = new SuppFinalizationReqDTO();
					_reqDTO.setRecommendDtlIds(rejectedRecommendDtlIds);
					
					SuppFinalizationResDTO _resDTO = qtSuppFinalizationSO.retrieveLoiLoaByRecommendDtlIds(_reqDTO);
					if (_resDTO.isSuccessful()) {
						List<ScLoiLoaItem> scLoiLoaItems = _resDTO.getScLoiLoaItemList();
						
						if (CollectionUtils.isNotEmpty(scLoiLoaItems)) {
							for (ScLoiLoaItem scLoiLoaItem 
												: scLoiLoaItems) {
								PmParameter recommendType = null;
								ScQtRecommend scQtRecommend = null;
								
								if (scLoiLoaItem.getScLoi() != null) {
									recommendType = scLoiLoaItem.getScLoi().getScLoiLoa()
														.getRecommendTypeCode();
								} else if (scLoiLoaItem.getScLoa() != null) {
									recommendType = scLoiLoaItem.getScLoa().getScLoiLoa()
														.getRecommendTypeCode();
								}
								scQtRecommend = scLoiLoaItem.getScQtRecommendDtl()
														.getScQtRecommend();								
								if (recommendType != null && 
										ParameterConst.RECOMMEND_TYPE_ITEM
											.equals(recommendType.getParameterCode())) {
									this.getManagedBean().setInitialSelectionItemized(true);
								}	
								
								List<Long> rejectedScQtRecommendIds 
										= getManagedBean().getRejectedScQtRecommendIds(); 
								if (rejectedScQtRecommendIds == null) {
									rejectedScQtRecommendIds = new ArrayList<Long>();
									getManagedBean().setRejectedScQtRecommendIds(
											rejectedScQtRecommendIds);
								}
								if ((scLoiLoaItem.getScQtRecommendDtl().getRecordStatusFlg()
										.equals(DbValueConst.RECORD_STATUS_REJECTED)) && 
											scQtRecommend != null &&
												!rejectedScQtRecommendIds.contains(
													scQtRecommend.getQtRecommendId())) {
									rejectedScQtRecommendIds.add(
											scQtRecommend.getQtRecommendId());
								}
							}
						}
					}
				}
				
				if (isZonal) {
					Map<Long, List<ScQtEvaluationResultDtl>> 
							shortlistedSupppliersByZone = resDTO.getQualifiedByPackageMap();
					
					// disabled supplier selection by zone.
					for (Long specZoneId : shortlistedSupppliersByZone.keySet()) {
						processShortlistedSupplierByPackage(
								scQtFinalization.getScQtRecommendsByZone().get(specZoneId), 
								shortlistedSupppliersByZone.get(specZoneId));
					}					

					this.getManagedBean().setPkgShortlistedSupppliersByZone(
							shortlistedSupppliersByZone);
				} else {
					List<ScQtEvaluationResultDtl> shortlistedPackageList 
							= resDTO.getQualifiedByPackageSuppliers();

					// disabled supplier selection.
					processShortlistedSupplierByPackage(
							scQtFinalization.getScQtRecommends(),
							shortlistedPackageList);

					this.getManagedBean().setShortlistedPackageList(
							shortlistedPackageList);
				}
				getManagedBean().setRefinalizedAndBiddingAward(false);
				
				if (isQTRefinalized()) {
					disableAwardedItems(scQtFinalization.getScQtRecommends());
					
					// Check if the any of the suppliers has awarded LOA's
					reqDTO = new SuppFinalizationReqDTO();
					reqDTO.setQtId(getManagedBean().getScQt().getQtId());
					resDTO = qtSuppFinalizationSO.hasLOAAwarded(reqDTO);
					if (resDTO.isSuccessful()) {
						this.getManagedBean().setHasAwardedLOA(resDTO.isLoaAwarded());
					}
				}
				
				String decisionTypeCode = scQtFinalization.getDecision()
						.getParameterCode();
				if (decisionTypeCode
						.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)
						|| decisionTypeCode
								.equals(ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY)) {
					retrieveSupplierDetailsByRecommendType(scQtFinalization);
					hasTierPriceInMixtureOfItems(scQtFinalization);
				} else if (decisionTypeCode
						.equals(ParameterConst.DECISION_RE_EVALUATION)) {
					// nothing to set
				} else if (decisionTypeCode
						.equals(ParameterConst.DECISION_RE_PUBLISH)) {
					// nothing to set
				} else if (decisionTypeCode
						.equals(ParameterConst.DECISION_CANCEL)) {
					// nothing to set
				}
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	private void setRefinalizedAndBiddingAwardStatus(
						ScQtFinalization scQtFinalization, Long specZoneId) {
		boolean isRefinalizedAndBiddingAward = false;
		
		if (isQTRefinalized() && scQtFinalization.getFinalizationMethod() != null &&
				ParameterConst.RECOMMEND_BY_ONLINE_BIDDING.equals(
						scQtFinalization.getFinalizationMethod().getParameterCode())) {
			boolean hasCancelledBidItems = false;
			
			List<Long> cancelledBidItems = scQtFinalization
							.getCancelledBidItemIdsMap().get(specZoneId);
			
			if (CollectionUtils.isNotEmpty(
					cancelledBidItems)) {
				hasCancelledBidItems = true;
			}
			
			if (!hasCancelledBidItems) {
				isRefinalizedAndBiddingAward = true;
			}					
		}	
		getManagedBean().setRefinalizedAndBiddingAward(
							isRefinalizedAndBiddingAward);		
	}

	private void processShortlistedSupplierByPackage(List<ScQtRecommend> recommendedItems,
								List<ScQtEvaluationResultDtl> shortlistedSuppliers) {
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		
		if (CollectionUtils.isNotEmpty(recommendedItems) && 
								shortlistedSuppliers != null) {
			ScQtRecommend scQtRecommend = recommendedItems.get(0);

			Long specZoneId = scQtRecommend.getTplSpecZoneDtl() != null ? 
					scQtRecommend.getTplSpecZoneDtl().getSpecZoneId() : null;
			for (ScQtEvaluationResultDtl evalResDtl 
									: shortlistedSuppliers) {
				
				// Disable selection for rejected supplier.
				if (scQtRecommend.getRecommendDtls() != null) {
					Long evaZoneId = evalResDtl.getTplSpecZoneDtl() != null ? 
							evalResDtl.getTplSpecZoneDtl().getSpecZoneId() : null;

					for (ScQtRecommendDtl recommendDtl 
									: scQtRecommend.getRecommendDtls()) {
						Long recommendDtlId = recommendDtl.getRecommendDtlId();
						Long supplierId = recommendDtl.getSupplier().getSupplierId();

						if (recommendDtlId != null
								&& supplierId != null
								&& supplierId == evalResDtl
										.getQtEvaluationResult()
										.getScQtProposal().getScQtSupplier()
										.getSmSupplier().getSupplierId()
								&& specZoneId == evaZoneId
								&& (recommendDtl.getRecordStatusFlg().equals(
											DbValueConst.RECORD_STATUS_REJECTED))) {

							resetSupplierSelectionAndDisable(evalResDtl);
							break;
						}
					}
				}
				evalResDtl.setAmountInWords(convertNumberToWord(
						evalResDtl.getProposalAmount().getProposalAmt()));
				
				setRefinalizedAndBiddingAwardStatus(
							scQtFinalization, specZoneId);				
				
				// If QT is refinalized and finalization method is bidding award 
				// then calculate the total bid price
				if (getManagedBean().isRefinalizedAndBiddingAward() &&
						evalResDtl.getBidRank() != null) {
					calculateBidPrice(recommendedItems, evalResDtl);
				}
			}
		}
	}

	private void disableSuppliersByItem(ScQtRecommend recommend,
						List<ScQtEvaluationResultDtl> shortlistedItemSupplierList) {
		if (recommend != null && 
				shortlistedItemSupplierList != null) {
			for (ScQtEvaluationResultDtl evalResultDtl 
								: shortlistedItemSupplierList) {
				if (recommend.isAwardedItem()) {
					
					// disabled selection for awarded supplier.
					evalResultDtl.setSuppSelectionDisabled(true);
				} else {
					
					// disabled selection for rejected supplier.
					if (recommend.getRecommendDtls() != null) {
						Long evaZoneId = null;
						Long specZoneId = null;
						if (evalResultDtl.getTplSpecZoneDtl() != null) {
							evaZoneId = evalResultDtl.getTplSpecZoneDtl().getSpecZoneId();
						}
						if (recommend.getTplSpecZoneDtl() != null) {
							specZoneId = recommend.getTplSpecZoneDtl().getSpecZoneId();
						}
						
						Long currentSupplierId = evalResultDtl.getQtEvaluationResult()
													.getScQtProposal().getScQtSupplier()
													.getSmSupplier().getSupplierId();
						for (ScQtRecommendDtl recommendDtl 
										: recommend.getRecommendDtls()) {
							Long recommendDtlId = recommendDtl
									.getRecommendDtlId();
							Long supplierId = recommendDtl.getSupplier()
									.getSupplierId();

							if (recommendDtlId != null
									&& supplierId != null
									&& supplierId == currentSupplierId
									&& specZoneId == evaZoneId
									&& (recommendDtl.getRecordStatusFlg().equals(
												DbValueConst.RECORD_STATUS_REJECTED))) {

								resetSupplierSelectionAndDisable(evalResultDtl);
								break;
							}
						}
					}
				}
			}
		}
	}
	
	private void resetSupplierSelectionAndDisable(
					ScQtEvaluationResultDtl evalResultDtl) {
		evalResultDtl.setSuppSelectionDisabled(true);
		evalResultDtl.setAwareded(false);
		evalResultDtl.setShortListed(false);
	}

	private void disableAwardedItems(List<ScQtRecommend> scQtRecommends) {
		List<Long> rejectedRecommendIds 
							= getManagedBean().getRejectedScQtRecommendIds();
		
		if (CollectionUtils.isNotEmpty(
							scQtRecommends)) {
			for (ScQtRecommend recommend : scQtRecommends) {
				if (recommend.getRecommendDtls() != null) {
					boolean isAwardedItem = false;
					int activeSupplierCount = 0;
					for (ScQtRecommendDtl recommendDtl 
											: recommend.getRecommendDtls()) {
						Long recommendDtlId = recommendDtl.getRecommendDtlId();

						// If LOI awarded and no decision made on LOA (or) 
						// if LOI awarded and LOA awarded,
						// then consider the item as awarded
						if (recommendDtlId != null) {
							if ( (BooleanUtils.isTrue(
									recommendDtl.getLoiAwarded()) && 
										recommendDtl.getLoaAwarded() == null && 
											recommendDtl.getRecordStatusFlg().equals(
														DbValueConst.RECORD_STATUS_ACTIVE) && 
												(rejectedRecommendIds != null && 
													!rejectedRecommendIds.contains(
															recommend.getQtRecommendId())) ) ||
											(BooleanUtils.isTrue(
												recommendDtl.getLoiAwarded()) && 
													BooleanUtils.isTrue(
														recommendDtl.getLoaAwarded())) ) {
								isAwardedItem = true;
							}
						}
						if (recommendDtl.getRecordStatusFlg()
								.equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
							++activeSupplierCount;
						}
					}
					
					if (isAwardedItem) {
						ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
						if (scQtFinalization.getAwardedItemIds() == null) {
							scQtFinalization.setAwardedItemIds(new ArrayList<Long>());
						}
						scQtFinalization.getAwardedItemIds()
								.add(recommend.getQtRecommendId());
					}
					recommend.setAwardedItem(isAwardedItem);
					recommend.setActiveSupplierCount(activeSupplierCount);
				}
			}			
		}
	}
	
	private boolean isQTRefinalized() {
		Boolean isRefinalized = getManagedBean().getQtRefinalized();
		
		if (isRefinalized == null) {
			IQTDetailSO qtDetailSO = null;
			
			try {
				qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
				tenderReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());	
				List<Long> statusIds = new ArrayList<Long>();
				statusIds.add(QTStatusConst.QT_RE_FINALIZATION);
				statusIds.add(QTStatusConst.QT_RE_EVALUATION);
				tenderReqDTO.setStatusIds(statusIds);
				
				List<ScWorkFlowStatus> statusList = qtDetailSO.retrieveWorkFlowStatus(tenderReqDTO);
				if (CollectionUtils.isNotEmpty(statusList)) {
					ScWorkFlowStatus reEvalWrkFlowStatus = null;
					ScWorkFlowStatus reFinalizationWrkFlowStatus = null;
					for (ScWorkFlowStatus wrkFlowStatus 
												: statusList) {
						if (QTStatusConst.QT_RE_EVALUATION.equals(
								wrkFlowStatus.getPmStatus().getStatusId())) {
							reEvalWrkFlowStatus = wrkFlowStatus;
						} else if (QTStatusConst.QT_RE_FINALIZATION.equals(
										wrkFlowStatus.getPmStatus().getStatusId())) {
							reFinalizationWrkFlowStatus = wrkFlowStatus;
						}
					}
					if (reFinalizationWrkFlowStatus == null) {
						isRefinalized = Boolean.FALSE;
					} else if (reEvalWrkFlowStatus == null) {
						isRefinalized = Boolean.TRUE;
					} else if (reEvalWrkFlowStatus != null && 
									reFinalizationWrkFlowStatus != null) {
						if (reEvalWrkFlowStatus.getChangedDate().after(
								reFinalizationWrkFlowStatus.getChangedDate())) {
							isRefinalized = Boolean.FALSE; 
						} else {
							isRefinalized = Boolean.TRUE;
						}
					}
				} else {
					isRefinalized = Boolean.FALSE;
				}
			} finally {
				qtDetailSO = null;
			}
			
			getManagedBean().setQtRefinalized(isRefinalized);
		}
		
		return isRefinalized;
	}
	
	private int findFinalizationCount() {
		IQTDetailSO qtDetailSO = null;
		
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());	
			List<Long> statusIds = new ArrayList<Long>();
			statusIds.add(QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION);
//			statusIds.add(QTStatusConst.QT_RE_FINALIZATION);
//			statusIds.add(QTStatusConst.QT_RE_EVALUATION);
			tenderReqDTO.setStatusIds(statusIds);
			
			List<ScWorkFlowStatus> statusList = qtDetailSO
							.retrieveWorkFlowStatus(tenderReqDTO);
			if (statusList != null) {
				return statusList.size();
			} 
		} finally {
			qtDetailSO = null;
		}	
		
		return 0;
	}

	private List<String> getRejectedRecommendDtlIds() {
		List<String> rejectedRecommendDtlIds = null;

		if (getManagedBean().getPayLoad() instanceof SCEvaluationDataType) {
			SCEvaluationDataType dataType = (SCEvaluationDataType) getManagedBean()
					.getPayLoad();

			rejectedRecommendDtlIds = dataType.getGroupkeyAll();

			for (int i = 0; i < rejectedRecommendDtlIds.size(); i++) {
				String rejectedRecommendDtlId = rejectedRecommendDtlIds.get(0);
				if (StringUtils.isBlank(rejectedRecommendDtlId)) {
					rejectedRecommendDtlIds.remove(i--);
				}
			}
		}

		return rejectedRecommendDtlIds;
	}

	private void retrieveSupplierDetailsByRecommendType(
							ScQtFinalization scQtFinalization) {
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		String recommendType = scQtFinalization
				.getRecommendedTypeId().getParameterCode();
		if (StringUtils.isBlank(scQtFinalization.getLoiLoaBy())) {
			scQtFinalization.setLoiLoaBy(
					SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER);
		}
		PmParameter finalizationMethod = scQtFinalization.getFinalizationMethod();
		getManagedBean().setFinalizationMethod(
					finalizationMethod.getParameterCode());	
		
		if (ParameterConst.RECOMMEND_TYPE_PACKAGE.equals(recommendType)) {
			if (getManagedBean().getShortlistedSupplierList() == null) {
				getManagedBean().setShortlistedSupplierList(
						new ArrayList<ScQtEvaluationResultDtl>());
				if (!isZonal && getManagedBean()
						.getShortlistedPackageList() != null) {
					getManagedBean().getShortlistedSupplierList()
						.addAll(getManagedBean().getShortlistedPackageList());
				} else {
					Map<Long, List<ScQtEvaluationResultDtl>> shortlistedSupppliersByZone 
									= this.getManagedBean().getPkgShortlistedSupppliersByZone();
					if (MapUtils.isNotEmpty(shortlistedSupppliersByZone)) {
						Long defaultSelectedZoneId = (Long) (shortlistedSupppliersByZone
								.keySet().toArray())[0];
						this.getManagedBean().setShortlistedSupplierList(
								shortlistedSupppliersByZone.get(defaultSelectedZoneId));
					}
				}
			}

			List<ScQtEvaluationResultDtl> shortlistedSuppliers = null;
			if (CollectionUtils.isNotEmpty(
					scQtFinalization.getScQtRecommends())) {
				ScQtRecommend scQtRecommend = null;
				if (isZonal) {
					TplSpecZoneDtl selectedZone = this.getManagedBean()
							.getCurrentZone();
					scQtRecommend = scQtFinalization.getScQtRecommendsByZone()
										.get(selectedZone.getSpecZoneId()).get(0);
					shortlistedSuppliers = getShortlistedSuppliersByZone(
												scQtRecommend.getTplSpecZoneDtl());
				} else {
					shortlistedSuppliers = getManagedBean().getShortlistedSupplierList();
					scQtRecommend = scQtFinalization.getScQtRecommends().get(0);
				}

				// set the checkbox or radio button of supplier selected
				if (CollectionUtils.isNotEmpty(
						scQtRecommend.getRecommendDtls())) {
					if (isQTRefinalized() || finalizationMethod.getParameterCode()
							.equals(ParameterConst.RECOMMEND_BY_AWARD)) {
						
						ScQtRecommendDtl activeRecommendDtl = null;
						for (ScQtRecommendDtl recommendDtl 
									: scQtRecommend.getRecommendDtls()) {
							if (recommendDtl.getRecordStatusFlg()
									.equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
								activeRecommendDtl = recommendDtl;
								break;
							}
						}

						// Condition to exclude the rejected suppliers
						if (activeRecommendDtl != null) {
							
							// Award
							Long selectedSupplierId = activeRecommendDtl.getSupplier()
														.getSupplierId();
							for (ScQtEvaluationResultDtl scQtEvaluationResultDtl 
																: shortlistedSuppliers) {
								Long supplierId = scQtEvaluationResultDtl
										.getQtEvaluationResult().getScQtProposal()
										.getScQtSupplier().getSmSupplier()
										.getSupplierId();
								if (supplierId == selectedSupplierId) {
									scQtEvaluationResultDtl.setSelected(true);
									getManagedBean().setRecommendedSupplier(
											scQtEvaluationResultDtl);
									break;
								}
							}
						}
					} else {
						getManagedBean().setRecommendedSupplierList(
								new ArrayList<ScQtEvaluationResultDtl>());
					} 
					
				} // End of if
			}
		} else {
			if (getManagedBean().getShortlistedSupplierList() == null) {
				getManagedBean().setShortlistedSupplierList(
						new ArrayList<ScQtEvaluationResultDtl>());
			}

			boolean isInitialDecisionFwdToBPK 
						= this.getManagedBean().isInitialDecisionFwdToBPK();
			boolean isInitialDecisionFwdToHighAuth 
						= this.getManagedBean().isInitialDecisionFwdHighAuth();	
			if (isInitialDecisionFwdToBPK || 
					isInitialDecisionFwdToHighAuth) {
				disableItemsNotRecommendedToHigherAuthority();
			}
		}
	}
	
	private void disableItemsNotRecommendedToHigherAuthority() {
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		ScQtFinalization scQtFinalization = this.getManagedBean().getScQtFinalization();
		List<ScQtRecommend> recommendList = scQtFinalization.getScQtRecommends();
		
		if (CollectionUtils.isNotEmpty(recommendList)) {
			Long loggedInUserId = getFacesScopes().getUserSessionBean()
					.getUserId();
			for (ScQtRecommend scQtRecommend : recommendList) {
				if (scQtRecommend.isHigherAuthReq() && 
						!(scQtRecommend.getCreatedBy().equals(loggedInUserId))) {
					this.getManagedBean().setItemFwdToHighAuthority(true);
				}
				
				if (!scQtRecommend.isHigherAuthReq() && 
						getManagedBean().isInitialDecisionFwdToBPK() && 
							isQTRefinalized()) {
					scQtRecommend.setDisabled(false);
				} else if (scQtRecommend.isHigherAuthReq() && 
							(getManagedBean().isInitialDecisionFwdToBPK() || 
								(getManagedBean().isInitialDecisionFwdHighAuth() &&
									(scQtFinalization.getHigherSecretary() != null &&
										scQtFinalization.getHigherSecretary().getUserId()
											.equals(loggedInUserId))))) {
					scQtRecommend.setDisabled(false);
				} else if (scQtRecommend.getCreatedBy()
										.equals(loggedInUserId)) {
					scQtRecommend.setDisabled(false);
				} else if (!scQtRecommend.isHigherAuthReq() && 
								CollectionUtils.isEmpty(
										scQtRecommend.getRecommendDtls())) {
					scQtRecommend.setDisabled(false);
				} else {
					scQtRecommend.setDisabled(true);
				}
				
				if (isZonal) {
					TplSpecZoneDtl selectedZone = this.getManagedBean()
							.getCurrentZone();
					if (!(scQtRecommend.getTplSpecZoneDtl().getSpecZoneId())
							.equals(selectedZone.getSpecZoneId())) {
						continue;
					}
				}

				List<ScQtEvaluationResultDtl> shortlistedSupplierList = null;
				if (CollectionUtils.isEmpty(
						scQtRecommend.getShortListedSupplierList())) {
					SuppFinalizationResDTO resDTO = this.retrieveShortListedSuppliersByItem(scQtRecommend);
					shortlistedSupplierList = resDTO.getQualifiedByItemSuppliers();
					scQtRecommend.setShortListedSupplierList(shortlistedSupplierList);
					
					// disabled supplier selection.
					disableSuppliersByItem(scQtRecommend,
										shortlistedSupplierList);
				}
				 
				if (CollectionUtils.isNotEmpty(shortlistedSupplierList)) {
					List<ScQtEvaluationResultDtl> recommendedSupplierList 
								= this.processAndSetRecommendedSupplierList(
										scQtRecommend, shortlistedSupplierList);
					scQtRecommend.setAwardSupplierList(recommendedSupplierList);
					this.getManagedBean().setRecommendedSupplierList(recommendedSupplierList);
				}
			}
		}		
	}
	
	private List<ScQtEvaluationResultDtl> processAndSetRecommendedSupplierList(
				ScQtRecommend scQtRecommend, List<ScQtEvaluationResultDtl> shortlistedSupplierList) {
		
		// Multiple LOI or Online Bidding
		if (scQtRecommend.getRecommendDtls() == null) {
			scQtRecommend.setRecommendDtls(
					new ArrayList<ScQtRecommendDtl>());
		}

		List<Long> supplierIdList = new ArrayList<Long>();
		for (ScQtRecommendDtl scQtRecommendDtl 
									: scQtRecommend.getRecommendDtls()) {
			if (scQtRecommendDtl.getRecordStatusFlg()
					.equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
				supplierIdList.add(scQtRecommendDtl
						.getSupplier().getSupplierId());
			}
		}

		List<ScQtEvaluationResultDtl> recommendedSupplierList 
							= new ArrayList<ScQtEvaluationResultDtl>();
		for (ScQtEvaluationResultDtl scQtEvaluationResultDtl : 
												shortlistedSupplierList) {
			Long supplierId = scQtEvaluationResultDtl
					.getQtEvaluationResult().getScQtProposal()
					.getScQtSupplier().getSmSupplier().getSupplierId();
			if (supplierIdList.contains(supplierId)) {
				scQtEvaluationResultDtl.setSelected(true);
				recommendedSupplierList.add(scQtEvaluationResultDtl);
			}
		}	
		
		return recommendedSupplierList;
	}

	/**
	 * Method to retrieve Board of Directors Details pop up in supplier finalization
	 */
	public void showBoardofDirectors(SmSupplier selectedSupplier) {
		debugLogger("showBoardofDirectors");

		if (selectedSupplier == null) {
			return;
		}

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

	private void resetCompanyInfo() {
		this.getManagedBean().setSmSupplier(null);
		this.getManagedBean().setSmMofAccount(null);
		this.getManagedBean().setSmCompanyBasic(null);
		this.getManagedBean().setSmPersonnel(null);
		this.getManagedBean().setSupplierBranch(null);
		this.getManagedBean().setSupplierAddress(null);
	}

	/**
	 * To show reminder to the committee members corresponding to the selected
	 * quotation/tender
	 */
	public void showReminder() {
		debugLogger("showReminder");

		this.getManagedBean().setReminderList(
				new ArrayList<ScQtCommitteeActivity>());

		List<ScQtCommitteeActivity> chairpersonList = new ArrayList<ScQtCommitteeActivity>();
		List<ScQtCommitteeActivity> memberList = new ArrayList<ScQtCommitteeActivity>();

		for (ScQtCommitteeActivity committee : getManagedBean()
				.getCommitteeList()) {
			String memberRole = committee.getScCommitteeMember()
					.getMemberRole().getParameterCode();
			if (memberRole.equals(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
				chairpersonList.add(committee);
			} else {
				if (!committee.isSignedOff()) {
					memberList.add(committee);
				}
			}
		}
		if (memberList.size() == 0) {
			// send reminder to Chairperson
			this.getManagedBean().getReminderList().addAll(chairpersonList);
		} else {
			// send reminder to Members
			this.getManagedBean().getReminderList().addAll(memberList);
		}
	}

	private void checkIfLoggedinUserIsSecretary() {
		Long userId = getFacesScopes().getUserSessionBean()
						.getUserId();
		List<ScQtCommitteeActivity> committeeMembers = 
					this.getManagedBean().getCommitteeList();
		
		if (CollectionUtils.isNotEmpty(committeeMembers)) {
			for (ScQtCommitteeActivity committeeActivity 
							: committeeMembers) {
				if (userId.equals(committeeActivity.getScCommitteeMember()
						.getPmUser().getUserId())) {
					String userRole = committeeActivity.getScCommitteeMember().
							getMemberRole().getParameterCode();
					if (StringUtils.equals(userRole, 
							ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
						this.getManagedBean().setSecretaryUser(true);
					}
					break;
				}
			}
		}
		
	}

	/**
	 * To send a notification by email to the committee members corresponding to
	 * the selected quotation/tender
	 */
	public void sendReminder() {
		String errorFieldLabel = null;
		String subject = getManagedBean().getSubject();
		String message = getManagedBean().getMessage();
		
		if (StringUtils.isBlank(subject)) {
			errorFieldLabel = "lbl.sc.subject";
		} else if (StringUtils.isBlank(message)) {
			errorFieldLabel = "lbl.sc.message";
		}
		if (errorFieldLabel != null) {
			throw new EpException(MessageConst.ERROR_100022, new String[]{
					ResourceBundleUtils.getMessage(errorFieldLabel, 
				 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
			});
		}
		resetNotificationFields();
		
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId()
					.toString());
			reqDTO.setCommitteMembers(getManagedBean()
					.getCommitteeList());
			reqDTO.setSubject(subject);
			reqDTO.setMessage(message);

			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.sendNotifications(reqDTO);
			if (resDTO.isSuccessful()) {
				resetNotificationFields();
				
				String variable1 = getParameterDesc(
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION);
				String variable2 = getManagedBean().getScQt().getQtNo();
//				commonMessageAcknowledmentPage(MessageConst.INFO_700047,
//						variable1, variable2);
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
								.getMessage(MessageConst.INFO_700047, new String[] { variable1,
										variable2 },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										getFacesScopes().getUserLocale())));
				
			} else {
				throw new EpException("Failed.");
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	/**
	 * Action performed when committee members endorsed the Supplier
	 * Finalization or chairperson sign off the Supplier Finalization
	 */
	public void signOff() {
		if (StringUtils.isNotBlank(
				getManagedBean().getCommitteeType())) {
			for (ScQtCommitteeActivity scQtCommitteeActivity 
						: getManagedBean().getCommitteeList()) {
				if (scQtCommitteeActivity.getScCommitteeMember().getPmUser().getUserId()
						.equals(getFacesScopes().getUserSessionBean().getUserId())) {
					scQtCommitteeActivity.setSignedOff(true);
					break;
				}
			}
		}
	}

	private void commonMessageAcknowledmentPage(String messageId,
			String variable1, String variable2) {
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(messageId, new String[] { variable1,
								variable2 },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								getFacesScopes().getUserLocale())));
		// clear data
		super.renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
	}

	/**
	 * Populate meeting status values in a drop down under Preparation tab
	 * 
	 * @return an array of meeting status values
	 */
	public SelectItem[] getMeetingStatusDropDown() {
		SelectItem[] items = getStatusDropdownValues("SC",
				QTConstants.DOCTYPE_MEETING);
		return items;
	}

	// public SelectItem[] getRoleTypeDropdownValues(String committeeRole) {
	// ISearchBean<PmParameterDesc> searchBean = new
	// SearchBean<PmParameterDesc>();
	// List<String> recommendList = new ArrayList<String>();
	// recommendList.add(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
	//
	// searchBean.getDataFilterBeans().add(
	// new SearchFilterBean("pmParameter.parameterCode",
	// SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
	//
	// SelectItem[] items =
	// getDropdownValuesBySearchBean(ParameterTypeConst.COMMITTEE_ROLE,
	// searchBean);
	// return items;
	// }

	/**
	 * Populate decision type(s) in Decision drop down under Supplier
	 * Finalization tab
	 * 
	 * @return an array of decision values
	 */
	public SelectItem[] getDecisionDropDown() {
		SelectItem[] items = null;
		ISearchBean<PmParameterDesc> searchBean = null;
		List<String> excludedDecisionList = null;
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();

		// 1. If re-finalized and (is zonal or isItemized) exclude the defined decision type(s)
		// 2. For Zonal package also exclude the defined decision type(s)
		if (CollectionUtils.isNotEmpty(
				scQtFinalization.getAwardedItemIds())) {
			excludedDecisionList = new ArrayList<String>();
			excludedDecisionList.add(
					ParameterConst.DECISION_CANCEL);
			excludedDecisionList.add(
					ParameterConst.DECISION_RE_PUBLISH);
			excludedDecisionList.add(
					ParameterConst.DECISION_RE_EVALUATION);	
		}
		
		if (getManagedBean().isInitialDecisionFwdToBPK() || 
				getManagedBean().isDisableFwdToHighAuthorityOption()) {
			if (excludedDecisionList == null) {
				excludedDecisionList = new ArrayList<String>();
			}
			excludedDecisionList.add(
					ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY);
		}
		
		if (CollectionUtils.isNotEmpty(
					excludedDecisionList)) {
			searchBean = new SearchBean<PmParameterDesc>();
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_IN.value(),
							excludedDecisionList));
		}
		
		items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.FINALIZATION_DECISION_TYPE, searchBean);
		
		return items;		
	}

	/**
	 * Populate values for Recommend Type radio button(s) under Supplier
	 * Finalization tab
	 * 
	 * @return an array of recommend type(s)
	 */
	public SelectItem[] getRecommendTypeRadioBtn() {
		SelectItem[] items = getRadioButtonCodeValues(ParameterTypeConst.SC_RECOMMEND_TYPE);
		return items;
	}

	/**
	 * Populate values for Online Bidding Endorsement radio button(s) under
	 * Supplier Finalization tab
	 * 
	 * @return an array of bidding endorsement type(s)
	 */
	public SelectItem[] getBiddingEndorsementRadioBtn() {
		SelectItem[] items = getRadioButtonCodeValues(ParameterTypeConst.FINALIZATION_BIDDING_ENDORSEMENT);
		return items;
	}

	/**
	 * Populate values for LOI/LOA Prepared By radio button(s) under Supplier
	 * Finalization tab
	 * 
	 * @return an array of LOI/LOA prepared by user type(s)
	 */
	public SelectItem[] getLoiLoaPreparedByRadioBtn() {
		SelectItem[] items = getRadioButtonCodeValues(ParameterTypeConst.FINALIZATION_LOILOA_PREPARED_BY);
		return items;
	}

	/**
	 * Populate values in Finalization Committee drop down, when the decision is
	 * set to Forward to Higher Authority under Supplier Finalization tab
	 * 
	 * @return an array of finalization committee type(s)
	 */
	public SelectItem[] getFinalizationCommitteeDropDown() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		
		String[] higherCommittees = null;
		List<String> finalizationCommitteeList = new ArrayList<String>();
		
		higherCommittees = retrieveHigherCommittees(
								scQtFinalization.getReason());
		
		if (ArrayUtils.isNotEmpty(
					higherCommittees)) {
			for (String committee : higherCommittees) {
				finalizationCommitteeList.add(committee);
			}
		}
		
//		if (committeeType
//				.equals(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B)) {
//			// defaulted QCA
//			searchBean
//					.getDataFilterBeans()
//					.add(new SearchFilterBean("pmParameter.parameterCode",
//							SearchOperator.OPERATOR_EQUALS.value(),
//							ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A));
//
//		} else if (committeeType
//				.equals(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A)) {
//			// dropdown (PBB/PBA)
//
//			List<String> finalizationCommiteeList = new ArrayList<String>();
//			Long userId = getFacesScopes().getUserSessionBean().getUserId();
//			String orgType = null;
//			IUserSO userSO = null;
//			try {
//				userSO = BusinessDelegate.getInstance().getServiceObject(
//						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
//
//				UserReqDTO reqDTO = new UserReqDTO();
//				reqDTO.setUserId(userId);
//				UserResDTO resDTO = userSO.retrieveUserDtlById(reqDTO);
//				if (resDTO != null) {
//					orgType = resDTO.getPmUser().getOrgTypeCode()
//							.getParameterCode();
//				}
//			} finally {
//				userSO = null;
//			}
//
//			if (StringUtils.equals(orgType, ParameterConst.ORG_TYPE_PMS_SABAH)
//					|| StringUtils.equals(orgType,
//							ParameterConst.ORG_TYPE_PMS_SARAWAK)) {// FIXME - to
//																	// filter by
//																	// PTJ
//																	// profile
//				finalizationCommiteeList
//						.add(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH);
//				finalizationCommiteeList
//						.add(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK);
//			} else {
//				finalizationCommiteeList
//						.add(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A);
//				finalizationCommiteeList
//						.add(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B);
//			}
//			searchBean.getDataFilterBeans().add(
//					new SearchFilterBean("pmParameter.parameterCode",
//							SearchOperator.OPERATOR_IN.value(),
//							finalizationCommiteeList));
//
//		} else if (committeeType
//				.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)
//				|| committeeType
//						.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)
//				|| committeeType
//						.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)
//				|| committeeType
//						.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
//
//			// hide Finalization Committee and display
//			// "Notes: Quotation/Tender will be forward to BPK"
//			getManagedBean().setForwardToBpk(true);
//		}

		SelectItem[] items = null;
		if (CollectionUtils.isNotEmpty(
				finalizationCommitteeList)) {
			if (!finalizationCommitteeList.contains(
					SourcingConstants.FC_COMMITTEE_TYPE_MOF)) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmParameter.parameterCode",
								SearchOperator.OPERATOR_IN.value(),
								finalizationCommitteeList));
				items = getDropdownValuesBySearchBean(
							ParameterTypeConst.SC_COMMITTEE_TYPE, searchBean);
			} else {
				items = new SelectItem[2];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
								"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								(new FacesScopes()).getUserLocale()));					
				items[1] = new SelectItem(SourcingConstants.FC_COMMITTEE_TYPE_MOF, SourcingConstants.FC_COMMITTEE_TYPE_MOF);
			}
		} else {
			items = new SelectItem[1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
							"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							(new FacesScopes()).getUserLocale()));
		}
		
		return items;
	}

	/**
	 * Get list of secretary users by committee type
	 * 
	 * @param commType
	 *            the committee type
	 * @return the list of PmUser instance
	 */
	public List<PmUser> retrieveCommitteeSecretary(String commType) {
		ICommitteSO committeSO = null;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);

			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setPmOrgProfile(getManagedBean().getScQt().getPmOrgProfile());
			reqDTO.setCommitteeType(commType);

			CommitteResDTO resDTO = committeSO
					.retrieveFcSecretaryUsersByCommType(reqDTO);

			return resDTO.getUserList();

		} finally {
			committeSO = null;
		}
	}

	/**
	 * Populate values for Reason radio button(s), when the decision is set to
	 * Forward to Higher Authority under Supplier Finalization tab
	 * 
	 * @return an array of reason type(s)
	 */
	public SelectItem[] getReasonRadioBtn() {
		SelectItem[] items = null;
		Map<Long, String> reasonDescMap = new HashMap<Long, String>();
		IReasonSO reasonSO = null;
		try {
			reasonSO = (IReasonSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IReasonSO.class);
			ReasonReqDTO reqDTO = new ReasonReqDTO();
			reqDTO.setReasonCategory(ParameterConst.FINALIZATION_REASON);
			reqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
			reqDTO.setSortBy("reasonDescId");
			ReasonResDTO resDTO = reasonSO
					.retrieveReasonDescListByCategory(reqDTO);

			List<PmReasonDesc> reasonList = resDTO.getReasonDescList();
			items = new SelectItem[reasonList.size()];
			int i = 0;
			for (PmReasonDesc reasonDesc : reasonList) {
				items[i++] = new SelectItem(reasonDesc.getPmReason()
						.getReasonId(), reasonDesc.getReasonDesc());
				reasonDescMap.put(reasonDesc.getPmReason().getReasonId(),
						reasonDesc.getReasonDesc());
			}
		} finally {
			reasonSO = null;
		}
		return items;
	}
	
	/**
	 * Event to trigger change of reason for Forward to Higher Authority
	 */
	public void changeReasonEvent(AjaxBehaviorEvent ajaxEvent) {
		ScQtFinalization scQtFinalization 
				= getManagedBean().getScQtFinalization();
		
		PmReason reason = scQtFinalization.getReason();
		String[] higherCommittees = retrieveHigherCommittees(reason);

		boolean isForwardToBPK = false;
		if (ArrayUtils.isNotEmpty(
						higherCommittees)) {
			boolean hasMOF = ArrayUtils.contains(higherCommittees, 
									SourcingConstants.FC_COMMITTEE_TYPE_MOF);
			if (hasMOF) {
				isForwardToBPK = true;
			}
		}
		
		if (ajaxEvent != null) {
			if (isForwardToBPK) {
				scQtFinalization.setHigherCommitte(null);
				scQtFinalization.setHigherCommitteStr(null);
				scQtFinalization.setHigherSecretary(null);
				scQtFinalization.setHigherSecretaryStr(null);
			} else {
				scQtFinalization.setHigherCommitte(new PmParameter());
				scQtFinalization.setHigherCommitteStr(StringUtils.EMPTY);
				scQtFinalization.setHigherSecretary(new PmUser());
				scQtFinalization.setHigherSecretaryStr(StringUtils.EMPTY);
			}
		}
		getManagedBean().setForwardToBpk(isForwardToBPK);
	}	
		
	private String[] retrieveHigherCommittees(PmReason reason) {
		String[] higherCommittees = null;
		
		if (reason != null && 
				reason.getReasonId() != null) {
			IReasonSO reasonSO = null;
			
			try {
				ReasonReqDTO reqDTO = new ReasonReqDTO();
				reqDTO.setReasonId(reason.getReasonId());
				
				reasonSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);
				reason = reasonSO.retrieveReasonById(reqDTO).getPmReason();
			} finally {
				reasonSO = null;
			}
			
			if (reason != null) {
				String committeeType = getManagedBean().getCommitteeType();
				Map<String,String[]> higherAuthoritiesMap 
							= getManagedBean().getHigherAuthoritiesMap();
				
				// 1 - Non Unanimous decision
				// 2 - Awarded price exceeded committee authority
				if ("1".equals(reason.getReasonCode())) {
					higherCommittees = higherAuthoritiesMap.get(committeeType + "_NU");
				} else if ("2".equals(reason.getReasonCode())) {
					higherCommittees = higherAuthoritiesMap.get(committeeType + "_EX");
				}
			}
		}
		
		return higherCommittees;
	}
	
	/**
	 * Populate values for Re-Evaluation by radio button(s), when the decision
	 * is set to Re-Evaluation under Supplier Finalization tab
	 * 
	 * @return an array of user type(s) for re-evaluation
	 */
	public SelectItem[] getReEvaluateByRadioBtn() {
		SelectItem[] items = getRadioButtonCodeValues(ParameterTypeConst.REEVALUATION_COMMITTEE_MEMBERS);
		return items;
	}

	/**
	 * Populate values for Finalization method drop down
	 * 
	 * @return an array of finalization method values
	 */
	public SelectItem[] getFinalizationMethodDropDown() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> excludedList = new ArrayList<String>();
		excludedList.add(ParameterConst.RECOMMEND_BY_CANCEL);
		excludedList.add(ParameterConst.RECOMMEND_BY_SINGLE_AWARD);
		excludedList.add(ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD);
		excludedList.add(ParameterConst.RECOMMEND_BY_BIDDING_NEXT_RANK);
		
		boolean hasItemWithPriceTypeTier 
						= getManagedBean().isHasItemWithPriceTypeTier();
		
		if (hasItemWithPriceTypeTier) {
			excludedList.add(ParameterConst.RECOMMEND_BY_ONLINE_BIDDING);
		}
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), excludedList));

		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.FINALIZATION_RECOMMEND_BY, searchBean);
		
		return items;
	}

	/**
	 * Populate values for cancellation of items 
	 * under Supplier Finalization tab
	 * 
	 * @return an array of Yes/No values
	 */
	public SelectItem[] getItemCancellationDropDown() {
		SelectItem[] items = getDropdownCodeValues(
						ParameterTypeConst.YES_NO_FLAG);
		return items;
	}

	/**
	 * Populate the list of supplier(s) in Award Supplier drop down under
	 * Supplier Finalization tab
	 * 
	 * @return an array of suppliers shortlisted for the selected item
	 */
	public SelectItem[] getAwardSupplierListDropDown() {
		if (this.getManagedBean().isItemRowSelect()) {
			return null;
		}
		List<ScQtRecommend> selectedItemList 
				= this.getManagedBean().getSelectedItemList();

		if (CollectionUtils.isNotEmpty(selectedItemList)) {
			// Remove duplicate items from selected list [which happens due to
			// row selection and checkbox selection]
			Set<ScQtRecommend> selectedItemSet = new HashSet<ScQtRecommend>();
			selectedItemSet.addAll(selectedItemList);
			selectedItemList.clear();
			selectedItemList.addAll(selectedItemSet);
			selectedItemSet = null;
		} else {
			return null;
		}
		Set<Long> commonSupplierIdSet = new HashSet<Long>();		
		List<SmSupplier> supplierList = new ArrayList<SmSupplier>();
		Map<Long,Integer> supplierNos = new HashMap<Long, Integer>();
		Map<Long, Long> commonSupplierMap = new HashMap<Long, Long>();
		Set<Long> rejectedSupplierIds = new HashSet<Long>();

		if (selectedItemList.size() == 1 && 
				CollectionUtils.isNotEmpty(
						selectedItemList.get(0).getShortListedSupplierList()) && 
						!selectedItemList.get(0).isDisabled() && 
						!selectedItemList.get(0).isAwardedItem()) {
			for (ScQtEvaluationResultDtl resultDtl 
							: selectedItemList.get(0).getShortListedSupplierList()) {
				SmSupplier smSupplier = resultDtl.getQtEvaluationResult()
						.getScQtProposal().getScQtSupplier().getSmSupplier();
				supplierList.add(smSupplier);
				supplierNos.put(smSupplier.getSupplierId(), 
								resultDtl.getQtEvaluationResult()
									.getScQtProposal().getSupplierNo());
			}
			if (CollectionUtils.isNotEmpty(
					selectedItemList.get(0).getRecommendDtls())) {
				for (ScQtRecommendDtl recommendDtl 
							: selectedItemList.get(0).getRecommendDtls()) {
					if (recommendDtl.getRecordStatusFlg()
							== DbValueConst.RECORD_STATUS_REJECTED) {
						rejectedSupplierIds.add(recommendDtl.getSupplier().getSupplierId());
					}
				}
			}
		} else {
			
			for (ScQtRecommend scQtRecommend : selectedItemList) {
				if (CollectionUtils.isNotEmpty(
						scQtRecommend.getShortListedSupplierList()) && 
						!scQtRecommend.isDisabled() && 
						!scQtRecommend.isAwardedItem()) {
					if (CollectionUtils.isNotEmpty(
							scQtRecommend.getRecommendDtls())) {
						for (ScQtRecommendDtl recommendDtl 
									: scQtRecommend.getRecommendDtls()) {
							if (recommendDtl.getRecordStatusFlg()
									== DbValueConst.RECORD_STATUS_REJECTED) {
								rejectedSupplierIds.add(recommendDtl.getSupplier().getSupplierId());
							}
						}
					}
					for (ScQtRecommend otherScQtRecommend : selectedItemList) {
						if (otherScQtRecommend.equals(scQtRecommend)) {
							continue;
						}
						for (ScQtEvaluationResultDtl resultDtl 
									: scQtRecommend.getShortListedSupplierList()) {
							SmSupplier supplier = resultDtl
									.getQtEvaluationResult().getScQtProposal()
									.getScQtSupplier().getSmSupplier();
							if (CollectionUtils.isNotEmpty(otherScQtRecommend
									.getShortListedSupplierList()) && 
										!resultDtl.isSuppSelectionDisabled()) {
								for (ScQtEvaluationResultDtl otherResultDtl 
											: otherScQtRecommend.getShortListedSupplierList()) {
									if (resultDtl.equals(otherResultDtl)) {
										continue;
									}
									SmSupplier otherSupplier = otherResultDtl
											.getQtEvaluationResult()
											.getScQtProposal()
											.getScQtSupplier().getSmSupplier();
									if ((supplier.getSupplierId())
											.equals(otherSupplier
													.getSupplierId())) {
										if (commonSupplierMap
												.containsKey(supplier
														.getSupplierId())) {
											Long repeatCount = commonSupplierMap
													.get(supplier
															.getSupplierId());
											++repeatCount;
											if (repeatCount.intValue() == selectedItemList
													.size()
													&& !commonSupplierIdSet
															.contains(supplier
																	.getSupplierId())) {
												commonSupplierIdSet
														.add(supplier
																.getSupplierId());
												supplierList.add(supplier);
												supplierNos.put(supplier.getSupplierId(), 
														otherResultDtl.getQtEvaluationResult()
															.getScQtProposal().getSupplierNo());
											} else {
												commonSupplierMap.put(supplier
														.getSupplierId(),
														repeatCount);
											}
										} else {
											commonSupplierMap.put(
													supplier.getSupplierId(),
													1L);
										}
										break;

									} // end if

								} // end for

							} // end if

						} // end for

					} // end for
					
				} else {
					break;
				}
				
			} // end for
		}

		commonSupplierIdSet = null;
		commonSupplierMap = null;

		List<SmSupplier> commonSupplierList = new ArrayList<SmSupplier>();
		for (SmSupplier supplier : supplierList) {
			if (rejectedSupplierIds.contains(
						supplier.getSupplierId())) {
				continue;
			}
			commonSupplierList.add(supplier);
		}
		this.getManagedBean().setAwardSupplierList(supplierList);
		
		int i = 0;
		SelectItem[] items = null;
		if (CollectionUtils.isNotEmpty(commonSupplierList)) {
			items = new SelectItem[commonSupplierList.size()];			
			for (SmSupplier supplier : commonSupplierList) {
				if (rejectedSupplierIds.contains(
							supplier.getSupplierId())) {
					continue;
				}
				items[i++] = new SelectItem(supplier.getSupplierId().toString(),
								 	supplierNos.get(supplier.getSupplierId()).toString());
			}			
		}
		
		return items;
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
				this.getManagedBean().getZoneActiveIndex()) {
			return;
		}
		
		TplSpecZoneDtl currentZone = this.getManagedBean().getCurrentZone();
		if (!this.getManagedBean().isItemize()) {
			
			// Check if the recommend type has changed
			finalizationMethodEvent(null);
			
			List<ScQtEvaluationResultDtl> currentZoneRecommendedSupplierList 
							= this.getManagedBean().getRecommendedSupplierList();
			Map<Long, List<ScQtEvaluationResultDtl>> recommendedSuppliersByZone 
							= this.getManagedBean().getRecommendedSupppliersByZone();
			if (recommendedSuppliersByZone == null) {
				recommendedSuppliersByZone =
						new LinkedHashMap<Long, List<ScQtEvaluationResultDtl>>();
				this.getManagedBean().setRecommendedSupppliersByZone(
						recommendedSuppliersByZone);
			}
			if (currentZone != null) {
				filterRecommendedSuppliersByZone(currentZone, 
								currentZoneRecommendedSupplierList);
			}
		}
		onZoneTabChangeAction(selectedZoneTabIndex);
	}
	
	private void onZoneTabChangeAction(Integer zoneTabIndex) {
		debugLogger("onZoneTabChangeAction");

		this.getManagedBean().setRecommendedSupplier(null);
		this.getManagedBean().setRecommendedSupplierList(null);
		this.getManagedBean().setShortlistedSupplierList(null);
		this.getManagedBean().setDisableSupplierSelection(false);
		this.getManagedBean().setItemCancelled(false);

		this.getManagedBean().setZoneActiveIndex(zoneTabIndex);
		TplSpecZoneDtl selectedZone = this.getManagedBean().getZoneDetails()
												.get(zoneTabIndex);
		
		this.getManagedBean().setCurrentZone(selectedZone);
		if (!this.getManagedBean().isItemize()) {
			debugLogger("Zone - Packagedwise supplier selection");
			
			List<ScQtEvaluationResultDtl> shortlistedSuppliers 
						= getShortlistedSuppliersByZone(selectedZone);
			this.getManagedBean().setShortlistedSupplierList(shortlistedSuppliers);
		} else {
			
			// To populate items specific to the selected zone
			populateScQtRecommendsByZone();
		}
		
		this.getManagedBean().setZoneTabChange(true);
		decisionEvent(null);
		this.getManagedBean().setZoneTabChange(false);
		
		if (!this.getManagedBean().isItemize()) {
			setRecommendedSupppliersByZone(
					this.getManagedBean().getCurrentZone());
		}		
	}

	private void filterRecommendedSuppliersByZone(TplSpecZoneDtl specZoneDtl,
			List<ScQtEvaluationResultDtl> currentZoneRecommendedSupplierList) {
		boolean isAward = this.getManagedBean().isSingleAward();
		Map<Long, List<ScQtEvaluationResultDtl>> recommendedSuppliersByZone
						= getManagedBean().getRecommendedSupppliersByZone();

		if (!isAward) {
			if (CollectionUtils.isEmpty(currentZoneRecommendedSupplierList)) {
				recommendedSuppliersByZone.remove(specZoneDtl.getSpecZoneId());
			} else {
				recommendedSuppliersByZone.put(specZoneDtl.getSpecZoneId(),
						currentZoneRecommendedSupplierList);
				this.getManagedBean().getResetSupplierList()
									 .removeAll(currentZoneRecommendedSupplierList);
			}
		} else {
			resetPreviouslyAwardedSuppliersByZone(specZoneDtl);
			
			if (this.getManagedBean().getRecommendedSupplier() != null) {
				List<ScQtEvaluationResultDtl> awardedSuppliers 
								= new ArrayList<ScQtEvaluationResultDtl>();
				ScQtEvaluationResultDtl recommendedSupplier 
							= this.getManagedBean().getRecommendedSupplier();
				recommendedSupplier.setAwareded(true);
				recommendedSupplier.setShortListed(false);
				awardedSuppliers.add(recommendedSupplier);
				recommendedSuppliersByZone.put(
						specZoneDtl.getSpecZoneId(), awardedSuppliers);
				this.getManagedBean().getResetSupplierList()
									 .remove(recommendedSupplier);
			}
		}
	}	
	
	/**
	 * Event for selecting decision for Recommend Supplier, Re-Evaluation,
	 * Re-Publish or Cancel in Supplier Finalization
	 */
	public void decisionEvent(AjaxBehaviorEvent ajaxEvent) {
		debugLogger("Decision Event ");
		
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		ScQtEvaluationResultDtl recommendedSupplier = 
					this.getManagedBean().getRecommendedSupplier();
		
		this.getManagedBean().setRecommendSupplier(false);
		this.getManagedBean().setReEvaluate(false);
		this.getManagedBean().setRePublish(false);
		this.getManagedBean().setForwardToHigherAuthority(false);
		this.getManagedBean().setRecommendedSupplier(null);

		boolean disableFwdToHighAuthorityOption = false;
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		boolean isInitialDecisionFwdToBPK 
					= this.getManagedBean().isInitialDecisionFwdToBPK();
		boolean isInitialDecisionFwdToHighAuth 
					= this.getManagedBean().isInitialDecisionFwdHighAuth();
		String committeeType = getManagedBean().getCommitteeType();
		
		scQtFinalization.setHigherAuthReq(false);
		
		PmParameter finalizationMethod = scQtFinalization.getFinalizationMethod();
		getManagedBean().setFinalizationMethod(
					finalizationMethod.getParameterCode());
		
		Map<String,String[]> higherAuthoritiesMap 
					= getManagedBean().getHigherAuthoritiesMap();
		
		if (!isInitialDecisionFwdToBPK) {
			if (MapUtils.isEmpty(higherAuthoritiesMap)) {
				
				ICommitteSO committeeSO = null;
				try {
					committeeSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
		
					CommitteReqDTO reqDTO = new CommitteReqDTO();
					reqDTO.setSupplierFinalization(true);
					reqDTO.setQtId(getManagedBean().getScQt().getQtId());
					CommitteResDTO resDTO = committeeSO.retrieveFinalizationCommitteeType(reqDTO);
					if (resDTO != null) {
						higherAuthoritiesMap = resDTO.getHigherAuthoritiesMap();
						
						getManagedBean().setHigherAuthoritiesMap(higherAuthoritiesMap);
					}
				} finally {
					committeeSO = null;
				}
			}
			if (MapUtils.isNotEmpty(
					higherAuthoritiesMap) && 
						!higherAuthoritiesMap
							.containsKey(committeeType + "_EX")) {
				disableFwdToHighAuthorityOption = true;
				getManagedBean().setDisableFwdToHighAuthorityOption(
									disableFwdToHighAuthorityOption);
			}
		}
		
		String decision = null;
		if (scQtFinalization.getDecision() != null) {
			decision = scQtFinalization.getDecision().getParameterCode();
			boolean isDecisionFwdToHighAuthority = false;
			
			if (ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY
					.equals(decision)) {
				isDecisionFwdToHighAuthority = true;
				
				if (scQtFinalization.getHigherCommitte() != null && 
						committeeType.equals(scQtFinalization
								.getHigherCommitte().getParameterCode())) {
					resetForwardToHigherAuthorityParams(scQtFinalization);
					scQtFinalization.setHigherCommitte(new PmParameter());
					scQtFinalization.setHigherSecretary(new PmUser());
					scQtFinalization.setReason(new PmReason());
				}
				
				if (!isInitialDecisionFwdToBPK && 
						scQtFinalization.getReason() != null) {
					changeReasonEvent(null);
				}
			}
			
			if (isInitialDecisionFwdToBPK || 
					disableFwdToHighAuthorityOption) {
				if (isDecisionFwdToHighAuthority) {
					PmParameter recommendSupplier = getParameterByTypeAndCode(
							ParameterTypeConst.FINALIZATION_DECISION_TYPE, 
							ParameterConst.DECISION_RECOMMEND_SUPPLIER);
					decision = recommendSupplier.getParameterCode();
					scQtFinalization.setDecision(recommendSupplier);
				}
			}
		}
		
		if (decision == null) {
			resetFlag();
		} else if (decision.equals(
					ParameterConst.DECISION_RECOMMEND_SUPPLIER) ||
						decision.equals(
							ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY)) {
			resetReEvaluate(scQtFinalization);
			resetRepublish(scQtFinalization);
			
			if (ajaxEvent != null && 
					(isInitialDecisionFwdToHighAuth || 
							isInitialDecisionFwdToBPK)) {
				if (this.getManagedBean().isItemize()) {
					disableItemsNotRecommendedToHigherAuthority();
				}
			}
			
			if (decision.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)) {
				this.getManagedBean().setRecommendSupplier(true);
				if (isInitialDecisionFwdToHighAuth) {
					resetForwardToHigherAuthority(scQtFinalization);
				}
			} else {
				this.getManagedBean().setForwardToHigherAuthority(true);
				scQtFinalization.setHigherAuthReq(true);
				fwdHigherAuthorityEvent();
			}
			if (StringUtils.isBlank(scQtFinalization.getLoiLoaBy())) {
				scQtFinalization.setLoiLoaBy(
						SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER);
			}
			
			String recommendType = scQtFinalization.getRecommendedTypeId()
												   .getParameterCode();
			if (ParameterConst.RECOMMEND_TYPE_PACKAGE
									.equals(recommendType)) {
				if (isZonal) {
					if (this.getManagedBean().getRecommendedSupppliersByZone() == null) {
						this.getManagedBean().setRecommendedSupppliersByZone(
								new LinkedHashMap<Long, List<ScQtEvaluationResultDtl>>());
					}
				} else {
					this.getManagedBean().setShortlistedSupplierList(
							getManagedBean().getShortlistedPackageList());					
				}
				
				if (scQtFinalization.getScQtRecommends().size() > 0) {
					ScQtRecommend scQtRecommend = null;
					List<TplSpecZoneDtl> zoneDetails = getManagedBean().getZoneDetails();
					TplSpecZoneDtl selectedZone = null;
					Long selectedZoneId = null;
					List<TplSpecZoneDtl> zoneDtls = getManagedBean().getZoneDetails();
					
					if (isZonal && CollectionUtils.isNotEmpty(zoneDetails)) {
						selectedZone = getManagedBean().getCurrentZone();
						selectedZoneId = selectedZone.getSpecZoneId();
						scQtRecommend = scQtFinalization.getScQtRecommendsByZone()
											.get(selectedZoneId).get(0);
					} else {
						scQtRecommend = scQtFinalization.getScQtRecommends().get(0);
					}
					
					setRefinalizedAndBiddingAwardStatus(scQtFinalization, 
														selectedZoneId);

					if (isZonal) {
						List<ScQtEvaluationResultDtl> shortListedSuppliers = 
								this.getManagedBean().getShortlistedSupplierList();
						
						if (!this.getManagedBean().isZoneTabChange()) {
							
							// Retrieving the items and corresponding recommended suppliers by zone
							if (CollectionUtils.isNotEmpty(zoneDtls)) {
								for (TplSpecZoneDtl zoneDtl : zoneDtls) {
									List<ScQtRecommend> itemListByZone = scQtFinalization
															.getScQtRecommendsByZone().get(zoneDtl.getSpecZoneId());
									if (CollectionUtils.isNotEmpty(itemListByZone)) {
										ScQtRecommend qtRecommend = itemListByZone.get(0);
										List<ScQtEvaluationResultDtl> shortListedSupplierList = 
															this.getShortlistedSuppliersByZone(zoneDtl);
										
										// Skip for first item (scQtRecommend) while loading data initially 
										// as the shortlisted supplier(s) of it would be the default to be displayed
										if (qtRecommend.equals(scQtRecommend)) {
											shortListedSuppliers = shortListedSupplierList;
											continue;
										}
										setRecommendedSuppliersByRecommendType(
												qtRecommend, zoneDtl, shortListedSupplierList);
									}
								}
							}
						} 
						
						if (scQtRecommend.isCancelled()) {
							this.getManagedBean().setItemCancelled(true);
						}
						if (scQtRecommend.isAwardedItem()) {
							this.getManagedBean().setDisableSupplierSelection(true);
						}
						
						// Catered for default and currently selected zone
						setRecommendedSuppliersByRecommendType(scQtRecommend, selectedZone, 
								shortListedSuppliers);
					} else {
						if (recommendedSupplier == null) {
							setRecommendedSuppliersByRecommendType(scQtRecommend, selectedZone, 
									this.getManagedBean().getShortlistedSupplierList());
						} else {
							this.getManagedBean().setRecommendedSupplier(recommendedSupplier);
						}
					}
					
					String selectionMode = setSelectionMode(finalizationMethod.getParameterCode());
					this.getManagedBean().setSelectionMode(selectionMode);
				}
			} else {
				// getManagedBean().setShortlistedSupplierList(getManagedBean().getShortlistedItemList());
				List<ScQtRecommend> shortListedItemList 
										= this.getManagedBean().getItemList();
				if (CollectionUtils.isNotEmpty(shortListedItemList)) {
					for (ScQtRecommend scQtRecommend : shortListedItemList) {
						if (isZonal) {
							TplSpecZoneDtl selectedZone 
								= this.getManagedBean().getCurrentZone();
							if (!(scQtRecommend.getTplSpecZoneDtl()
									.getSpecZoneId()).equals(selectedZone
									.getSpecZoneId())) {
								continue;
							}
						}

						// set the checkbox or radio button of supplier selected
						if (CollectionUtils.isNotEmpty(scQtRecommend
								.getRecommendDtls())) {
							
							// Multiple LOI or Online Bidding
							List<Long> supplierIdList = new ArrayList<Long>();
							for (ScQtRecommendDtl scQtRecommendDtl 
										: scQtRecommend.getRecommendDtls()) {
								if (scQtRecommendDtl.getRecordStatusFlg()
										.equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
									supplierIdList.add(scQtRecommendDtl
											.getSupplier().getSupplierId());
								}
							}
							scQtRecommend.setAwardSupplierList(
									new ArrayList<ScQtEvaluationResultDtl>());
							getManagedBean().setRecommendedSupplierList(
									new ArrayList<ScQtEvaluationResultDtl>());
							
							if (CollectionUtils.isNotEmpty(
									this.getManagedBean().getShortlistedSupplierList())) {
								for (ScQtEvaluationResultDtl scQtEvaluationResultDtl : 
												this.getManagedBean().getShortlistedSupplierList()) {
									Long supplierId = scQtEvaluationResultDtl
											.getQtEvaluationResult()
											.getScQtProposal().getScQtSupplier()
											.getSmSupplier().getSupplierId();
									if (supplierIdList.contains(supplierId)) {
										scQtEvaluationResultDtl.setSelected(true);
										
										for (ScQtRecommendDtl dtl : scQtRecommend
												.getRecommendDtls()) {
											if (supplierId == dtl.getSupplier()
													.getSupplierId()) {
												scQtEvaluationResultDtl
														.setAwardValue(dtl
																.getAwardQty());
											}
										}
	
										scQtRecommend.getAwardSupplierList()
												.add(scQtEvaluationResultDtl);
										getManagedBean().getRecommendedSupplierList()
												.add(scQtEvaluationResultDtl);
									}
								}
							}
						}
					}
				}
			}

		} else if (decision.equals(ParameterConst.DECISION_RE_EVALUATION)) {
			this.getManagedBean().setReEvaluate(true);
		} else if (decision.equals(ParameterConst.DECISION_RE_PUBLISH)) {
			this.getManagedBean().setRePublish(true);
		} else if (decision.equals(ParameterConst.DECISION_CANCEL)) {
			this.getManagedBean().setCancel(true);
		}
	}
	
	private void setRecommendedSuppliersByRecommendType(ScQtRecommend scQtRecommend, 
				 	TplSpecZoneDtl specZoneDtl, List<ScQtEvaluationResultDtl> shortListedSuppliers) {
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		
		// set the checkbox or radio button of supplier selected
		if (CollectionUtils.isNotEmpty(
				scQtRecommend.getRecommendDtls()) && 
					CollectionUtils.isNotEmpty(
								shortListedSuppliers)) {
			boolean isRecommendByAward = ParameterConst.RECOMMEND_BY_AWARD.equals(
					scQtFinalization.getFinalizationMethod().getParameterCode());
			
			if (isQTRefinalized() || isRecommendByAward) {
				
				// If recommend by award, then set the recommended supplier from scQtRecommendDtl 
				// only during initial loading of data
				if (this.getManagedBean().isZoneTabChange()) {
					return;
				}
				
				for (ScQtEvaluationResultDtl scQtEvalResDtl : shortListedSuppliers) {
					if (scQtEvalResDtl.isAwareded()) {
						scQtEvalResDtl.setSelected(true);
						scQtEvalResDtl.setShortListed(false);
						
						getManagedBean().setRecommendedSupplier(
												scQtEvalResDtl);
						if (isZonal) {
							List<ScQtEvaluationResultDtl> 
									recommendedSupplierList = new ArrayList<ScQtEvaluationResultDtl>();
							recommendedSupplierList.add(scQtEvalResDtl);
							if (this.getManagedBean().getRecommendedSupppliersByZone()
									.get(specZoneDtl.getSpecZoneId()) == null) {
								this.getManagedBean().getRecommendedSupppliersByZone().put(
										specZoneDtl.getSpecZoneId(), recommendedSupplierList);
							}
						}
						break;
					}
				}				
			} else {
				if (this.getManagedBean().isItemize()
						|| !this.getManagedBean().isZoneTabChange()) {
					retrieveAndSetRecommendedSuppliers(scQtRecommend,shortListedSuppliers);
				} 
				if (isZonal && !this.getManagedBean().isZoneTabChange()) {
					List<ScQtEvaluationResultDtl> recommendedSupplierList = 
							processAndSetRecommendedSupplierList(scQtRecommend,shortListedSuppliers);
					this.getManagedBean().getRecommendedSupppliersByZone()
						.put(specZoneDtl.getSpecZoneId(), recommendedSupplierList);
				} 
			}
		}		
	}
	
	private void retrieveAndSetRecommendedSuppliers(ScQtRecommend scQtRecommend, 
						List<ScQtEvaluationResultDtl> shortListedSuppliers) {
		
		// Multiple LOI or Online Bidding
		List<Long> supplierIdList = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(
					scQtRecommend.getRecommendDtls())) {
			for (ScQtRecommendDtl scQtRecommendDtl 
						: scQtRecommend.getRecommendDtls()) {
				if (scQtRecommendDtl.getRecordStatusFlg()
						.equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
					supplierIdList.add(scQtRecommendDtl
							.getSupplier().getSupplierId());
				}
			}
		}
		getManagedBean().setRecommendedSupplierList(
				new ArrayList<ScQtEvaluationResultDtl>());
		
		int count = 0;
		for (ScQtEvaluationResultDtl scQtEvaluationResultDtl : shortListedSuppliers) {
			if (count == supplierIdList.size()) {
				break;
			}
			Long supplierId = scQtEvaluationResultDtl
					.getQtEvaluationResult()
					.getScQtProposal().getScQtSupplier()
					.getSmSupplier().getSupplierId();
			if (supplierIdList.contains(supplierId)) {
				scQtEvaluationResultDtl.setSelected(true);
				scQtEvaluationResultDtl.setAwareded(false);
				scQtEvaluationResultDtl.setShortListed(true);
				
				getManagedBean().getRecommendedSupplierList()
								.add(scQtEvaluationResultDtl);
				++count;
			}
		}		
	}

	private void resetRecommendSupplier(ScQtFinalization scQtFinalization) {
		scQtFinalization.setHigherCommitte(null);
		scQtFinalization.setHigherSecretary(null);
		scQtFinalization.setLoiLoaBy(null);
		scQtFinalization.setReason(null);

		resetFlag();
	}

	private void resetReEvaluate(ScQtFinalization scQtFinalization) {
		scQtFinalization.setReEvaluateCommitte(null);
		scQtFinalization.setReEvaluateFinReq(false);
		scQtFinalization.setReEvaluateTechReq(false);
	}

	private void resetForwardToHigherAuthority(ScQtFinalization scQtFinalization) {
		resetForwardToHigherAuthorityParams(scQtFinalization);
		scQtFinalization.setHigherAuthReq(false);
		this.getManagedBean().setForwardToHigherAuthority(false);
	}
	
	private void resetForwardToHigherAuthorityParams(ScQtFinalization scQtFinalization) {
		scQtFinalization.setReason(null);
		scQtFinalization.setReasonStr(null);		
		scQtFinalization.setHigherSecretary(null);
		scQtFinalization.setHigherSecretaryStr(null);
		scQtFinalization.setHigherCommitte(null);
		scQtFinalization.setHigherCommitteStr(null);
	}

	private void resetRepublish(ScQtFinalization scQtFinalization) {
		scQtFinalization.setSpecChangeReq(false);
	}

	private void resetCancel(ScQtFinalization scQtFinalization) {

	}

	private void resetFlag() {
		resetRecommendFlag();
		
		this.getManagedBean().setForward(false);
		this.getManagedBean().setFinalizationMethod(
						ParameterConst.RECOMMEND_BY_AWARD);
		PmParameter finalizationMethodAward = getParameterByTypeAndCode(
							ParameterTypeConst.FINALIZATION_RECOMMEND_BY, 
							ParameterConst.RECOMMEND_BY_AWARD);
		getManagedBean().getScQtFinalization()
			.setFinalizationMethod(finalizationMethodAward);
		getManagedBean().setSelectionMode(QTConstants.SF_SINGLE);
	}
	
	private void resetRecommendFlag() {
		this.getManagedBean().setOnlineBidding(false);
		this.getManagedBean().setMultipleLoi(false);
		this.getManagedBean().setMultipleAward(false);
	}	
	
	private void resetNotificationFields() {
		this.getManagedBean().setSubject(null);
		this.getManagedBean().setMessage(null);
	}

	private void createMainMenuModel() {
		MenuModel mainMenuModel = new DefaultMenuModel();

		boolean isBPKUser = false;
		if (getManagedBean().isInitialDecisionFwdToBPK() || 
				getManagedBean().isReadOnly()) {
			isBPKUser = true;
		}

		int tabIndex = 0;
		MenuItem menuItem = null;
		String tabMenuChangeAction = "onMainTabMenuChangeAction([ {name : 'selectedTabIndex', value : ";
		
		// Preparation Menu Item
		if (!isBPKUser) {
			menuItem = new MenuItem();
			menuItem.setValue(ResourceBundleUtils.getMessage(
								"lbl.sc.preparation",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale()));
			menuItem.setStyle("width:15%;");
			menuItem.setTransient(true);
			menuItem.setPartialSubmit(true);
			menuItem.setProcess("@form");
			menuItem.setUpdate("@form");
			menuItem.setOncomplete(tabMenuChangeAction
					+ (tabIndex++) + "}]); ");
			mainMenuModel.addMenuItem(menuItem);
		}
		
		
		// Briefing Paper Menu Item
		menuItem = new MenuItem();
		menuItem.setValue(ResourceBundleUtils.getMessage(
							"lbl.sc.briefing.paper",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
		menuItem.setStyle("width:15%;");
		menuItem.setTransient(true);
		menuItem.setPartialSubmit(true);
		menuItem.setProcess("@form");
		menuItem.setUpdate("@form");
		menuItem.setOncomplete(tabMenuChangeAction
				+ (isBPKUser ? 1 : (tabIndex++)) + "}]); ");
		mainMenuModel.addMenuItem(menuItem);
		
		
		// Supplier Finalization Menu Item
		menuItem = new MenuItem();
		menuItem.setValue(ResourceBundleUtils.getMessage(
							"lbl.sc.supplier.finalization",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
		menuItem.setStyle("width:20%;");
		menuItem.setTransient(true);
		menuItem.setPartialSubmit(true);
		menuItem.setProcess("@form");
		menuItem.setUpdate("@form");
		menuItem.setOncomplete(tabMenuChangeAction
				+ (isBPKUser ? 2 : (tabIndex++)) + "}]); ");
		mainMenuModel.addMenuItem(menuItem);

		
		// Supplier Finalization Committee Endorsement Menu Item
		if (!isBPKUser) {
			menuItem = new MenuItem();
			menuItem.setValue("Endorsement Report");
			menuItem.setStyle("width:15%;");
			menuItem.setTransient(true);
			menuItem.setPartialSubmit(true);
			menuItem.setProcess("@form");
			menuItem.setUpdate("@form");
			menuItem.setOncomplete(tabMenuChangeAction
					+ (tabIndex++) + "}]); ");
			mainMenuModel.addMenuItem(menuItem);	
			
			menuItem = new MenuItem();
			menuItem.setValue(ResourceBundleUtils.getMessage(
								"lbl.sc.committee.endorsement.finalization",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale()));
			menuItem.setStyle("width:20%;");
			menuItem.setTransient(true);
			menuItem.setPartialSubmit(true);
			menuItem.setProcess("@form");
			menuItem.setUpdate("@form");
			menuItem.setOncomplete(tabMenuChangeAction
					+ (tabIndex++) + "}]); ");
			mainMenuModel.addMenuItem(menuItem);			
		}
		
		this.getManagedBean().setMainMenuModel(mainMenuModel);
	}	

	private void createZoneMenuModel() {
		List<TplSpecZoneDtl> zoneDtls 
					= this.getManagedBean().getZoneDetails();
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
					zoneItem.setTransient(true);
					zoneItem.setPartialSubmit(true);
					zoneItem.setProcess("@this");
					zoneItem.setUpdate("@this");
					zoneItem.setOnstart("blockWidget.show();");
					zoneItem.setOncomplete("onZoneTabChangeAction([ {name : 'selectedZoneTabIndex', value : "
							+ zoneTabIndex + "}]); blockWidget.hide();");
					zoneMenuModel.addMenuItem(zoneItem);
					++zoneTabIndex;
					zoneMap.put(specZoneDtl.getSpecZoneId(), specZoneDtl);
				}
				this.getManagedBean().setZoneMap(zoneMap);
				
				List<ScQtEvaluationResultDtl> shortlistedSuppliers 
							= getShortlistedSuppliersByZone(this.getManagedBean().getCurrentZone());
				this.getManagedBean().setShortlistedSupplierList(shortlistedSuppliers);
			}
			this.getManagedBean().setZoneMenuModel(zoneMenuModel);
		}
	}

	private void populateScQtRecommendsByZone() {
		ScQtFinalization scQtFinalization = this.getManagedBean()
				.getScQtFinalization();
		TplSpecZoneDtl selectedZone = this.getManagedBean().getCurrentZone();
		if (selectedZone != null) {
			this.getManagedBean().setItemList(
					scQtFinalization.getScQtRecommendsByZone()
							.get(selectedZone.getSpecZoneId()));
		}
	}

	private List<ScQtEvaluationResultDtl> getShortlistedSuppliersByZone(TplSpecZoneDtl specZone) {
		Map<Long, List<ScQtEvaluationResultDtl>> shortlistedSuppliersByZone 
						= this.getManagedBean().getPkgShortlistedSupppliersByZone();
		if (MapUtils.isNotEmpty(shortlistedSuppliersByZone)) {
			Long specZoneId = null;
			if (specZone == null) {
				specZoneId = (Long) (shortlistedSuppliersByZone
						.keySet().toArray())[0];
			} else {
				specZoneId = specZone.getSpecZoneId();
			}
			return shortlistedSuppliersByZone
						.get(specZoneId);
		}
		
		return null;
	}
	
	private void resetItemDetails(List<ScQtRecommend> scQtRecommendList) {
		boolean isRecommSuppOrFwdToHighAuth = false;
					
		String decisionTypeCode = getManagedBean().getScQtFinalization()
									.getDecision().getParameterCode();
		if (ParameterConst.DECISION_RECOMMEND_SUPPLIER
				.equals(decisionTypeCode)
				|| ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY
						.equals(decisionTypeCode)) {
			isRecommSuppOrFwdToHighAuth = true;
		}
		
		if (CollectionUtils.isNotEmpty(scQtRecommendList)) {
			boolean isItemized = this.getManagedBean().isItemize();
			for (ScQtRecommend scQtRecommend 
									: scQtRecommendList) {
				if (scQtRecommend.isDisabled() ||
						scQtRecommend.isAwardedItem()) {
					continue;
				}
				if (isItemized) {
					scQtRecommend.setCancelled(false);
				}
				
				List<ScQtRecommendDtl> recommendDtls = scQtRecommend
						.getRecommendDtls();
				if (CollectionUtils.isNotEmpty(recommendDtls)) {
					prepareToDeleteRecommendDtls(scQtRecommend);
				}
				scQtRecommend.setAwardSupplierList(null);
				
				// To reset the selection of supplier(s) by item
				List<ScQtEvaluationResultDtl> shortlistedSuppliers = null;
				
				if ((isItemized || 
						(!isItemized && !isRecommSuppOrFwdToHighAuth)) &&
							scQtRecommend.getTplSpecZoneDtl() != null) {
					
					// Reset package wise shortlisted suppliers
					shortlistedSuppliers = this.getShortlistedSuppliersByZone(
								scQtRecommend.getTplSpecZoneDtl());					
				} else if (!isItemized || 
								(isItemized && !isRecommSuppOrFwdToHighAuth)) {
					shortlistedSuppliers = scQtRecommend.getShortListedSupplierList();
					
					// Reset itemized shortlisted suppliers
					if (CollectionUtils.isEmpty(
							shortlistedSuppliers)) {
						SuppFinalizationResDTO resDTO 
									= this.retrieveShortListedSuppliersByItem(scQtRecommend);
						shortlistedSuppliers = resDTO.getQualifiedByItemSuppliers();
					}
				}
				scQtRecommend.setShortListedSupplierList(null);
				resetShortlistedSupplierData(shortlistedSuppliers, null);
			}
		}		
	}
	
	private void prepareToDeleteRecommendDtls(ScQtRecommend scQtRecommend) {
		Set<ScQtRecommendDtl> removedRecommendDtlSet 
									= new HashSet<ScQtRecommendDtl>();
		Set<ScQtRecommendDtl> toBeAddedSet = new HashSet<ScQtRecommendDtl>();
		
		if (scQtRecommend.getRemoveSupplierList() == null) {
			scQtRecommend.setRemoveSupplierList(
					new ArrayList<ScQtRecommendDtl>());
		} else {
			removedRecommendDtlSet.addAll(
					scQtRecommend.getRemoveSupplierList());
		}
		if (CollectionUtils.isNotEmpty(
				scQtRecommend.getRecommendDtls())) {
			processAndSetDeletedRecommendDtls(toBeAddedSet, scQtRecommend, true);
			removedRecommendDtlSet.addAll(
					scQtRecommend.getRemoveSupplierList());
			removedRecommendDtlSet.removeAll(toBeAddedSet);
			scQtRecommend.getRemoveSupplierList().clear();
			scQtRecommend.getRemoveSupplierList()
						 .addAll(removedRecommendDtlSet);
		}
		
		scQtRecommend.setRecommendDtls(
				new ArrayList<ScQtRecommendDtl>(toBeAddedSet));
	}
	
	private void resetShortlistedSupplierData(
			List<ScQtEvaluationResultDtl> shortlistedSupplierList, 
			ScQtEvaluationResultDtl skippedSupplier) {
		if (CollectionUtils.isNotEmpty(shortlistedSupplierList)) {
			for (ScQtEvaluationResultDtl shortlistedSupplier 
						: shortlistedSupplierList) {
				if (skippedSupplier == null || 
						!(skippedSupplier.getEvaluationResultDtlId()
							.equals(shortlistedSupplier.getEvaluationResultDtlId()))) {
					shortlistedSupplier.setAwareded(false);
					shortlistedSupplier.setShortListed(false);
				}
			}
			if (this.getManagedBean().getResetSupplierList() == null) {
				this.getManagedBean().setResetSupplierList(
						new HashSet<ScQtEvaluationResultDtl>());
			}
			this.getManagedBean().getResetSupplierList()
				.addAll(shortlistedSupplierList);
		}
	}
	
	private void resetPreviouslyAwardedSuppliersByZone(TplSpecZoneDtl specZoneDtl) {
		Map<Long, List<ScQtEvaluationResultDtl>> recommendedSuppliersByZone
						= getManagedBean().getRecommendedSupppliersByZone();
		
		// Reset award status of previously recommended suppliers
		if (recommendedSuppliersByZone.containsKey(
					specZoneDtl.getSpecZoneId())) {
			List<ScQtEvaluationResultDtl> awardedSuppliers 
					= recommendedSuppliersByZone.get(specZoneDtl.getSpecZoneId());
			if (CollectionUtils.isNotEmpty(awardedSuppliers)) {
				for (ScQtEvaluationResultDtl awardedSupplier
							: awardedSuppliers) {
					awardedSupplier.setAwareded(false);
					awardedSupplier.setShortListed(false);
					this.getManagedBean().getResetSupplierList()
							.add(awardedSupplier);
				}
			}
			
			recommendedSuppliersByZone.remove(specZoneDtl.getSpecZoneId());
		}		
	}	
	
	private void setRecommendedSupppliersByZone(TplSpecZoneDtl zoneDtl) {
		Map<Long, List<ScQtEvaluationResultDtl>> recommendedSuppliersByZone 
						= this.getManagedBean().getRecommendedSupppliersByZone();
		if (MapUtils.isNotEmpty(recommendedSuppliersByZone)) {
			Long specZoneId = null;
			if (zoneDtl == null) {
				specZoneId = (Long) (recommendedSuppliersByZone
						.keySet().toArray())[0];
			} else {
				specZoneId = zoneDtl.getSpecZoneId();
			}
			
			if (recommendedSuppliersByZone.containsKey(specZoneId)) {
				List<ScQtEvaluationResultDtl> recommendedSuppList = 
													recommendedSuppliersByZone.get(specZoneId);
				if (!this.getManagedBean().isSingleAward()) {
					this.getManagedBean().setRecommendedSupplierList(recommendedSuppList);
					this.getManagedBean().setRecommendedSupplier(null);
				} else {
					if (CollectionUtils.isNotEmpty(recommendedSuppList)) {
						this.getManagedBean().setRecommendedSupplierList(null);
						this.getManagedBean().setRecommendedSupplier(recommendedSuppList.get(0));
					}
				}
			}
		}
	}	

	/**
	 * Event for Recommendation Type By Item or By Package 
	 * under Supplier Finalization tab
	 */
	public void recommendTypeEvent() {
		debugLogger("recommendTypeEvent");

		this.getManagedBean().setOnlineBidding(false);
		this.getManagedBean().setMultipleLoi(false);
		this.getManagedBean().setItemCancelled(false);
		this.getManagedBean().setDisableSupplierSelection(false);
		this.getManagedBean().setRecommendedSupplier(null);
		this.getManagedBean().setShortlistedSupplierList(
					new ArrayList<ScQtEvaluationResultDtl>());
		this.getManagedBean().setRecommendedSupplierList(
					new ArrayList<ScQtEvaluationResultDtl>());		
		
		populateRecommendTypeEventDetails(true);
	}

	private void populateRecommendTypeEventDetails(boolean triggeredFromEvent) {
		boolean itemized = false;
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		String recommendType = scQtFinalization.getRecommendedTypeId()
				.getParameterCode();
		String selectionMode = QTConstants.SF_SINGLE;
		String finalizationMethod = getManagedBean().getFinalizationMethod();

		// Disable award by package when procurement category is medicine
		if (getManagedBean().isProcCategoryMedicine()) {
			PmParameter itemRecommendType = getPmParameterByTypeAndCode(
					ParameterTypeConst.SC_RECOMMEND_TYPE,
					ParameterConst.RECOMMEND_TYPE_ITEM);
			scQtFinalization.setRecommendedTypeId(itemRecommendType);
			recommendType = itemRecommendType.getParameterCode();
		}
		
		itemized = ParameterConst.RECOMMEND_TYPE_ITEM
						.equals(recommendType) ? true : false;
		this.getManagedBean().setItemize(itemized);
		
		if (!triggeredFromEvent) {
			selectionMode = setSelectionMode(finalizationMethod);
		}
		
		if (itemized) {
			
			// Reset the awarded supplier list (ScQtRecommendDtl) by item,
			// when recommend type is changed to item
			if (triggeredFromEvent || 
					getManagedBean().isProcCategoryMedicine()) {
				
				if (triggeredFromEvent) {
					resetPackageWiseSelectionDetails(isZonal, scQtFinalization);
				}			
				
				if (isZonal) {
					populateScQtRecommendsByZone();
				} else {
					this.getManagedBean().setItemList(
							scQtFinalization.getScQtRecommends());
				}
				this.getManagedBean().setShortlistedSupplierList(null);
			} 
		} else {
			TplSpecZoneDtl currentZone = this.getManagedBean().getCurrentZone();
			if (isZonal && currentZone != null) {
				if (triggeredFromEvent) {
					this.getManagedBean().setSingleAward(true);
					resetItemWiseSelectionDetails(isZonal, scQtFinalization);
				}
				List<ScQtEvaluationResultDtl> shortlistedSuppliers 
								= getShortlistedSuppliersByZone(currentZone);
				this.getManagedBean().setShortlistedSupplierList(shortlistedSuppliers);
			} else if (!isZonal
					&& CollectionUtils.isNotEmpty(this.getManagedBean()
							.getShortlistedPackageList())) {
				if (triggeredFromEvent || 
						getManagedBean().isProcCategoryMedicine()) {
					if (triggeredFromEvent) {
						this.getManagedBean().setSingleAward(true);
						resetItemWiseSelectionDetails(isZonal, scQtFinalization);
					}
				}
				this.getManagedBean().setShortlistedSupplierList(
						new ArrayList<ScQtEvaluationResultDtl>());
				this.getManagedBean().getShortlistedSupplierList()
						.addAll(this.getManagedBean().getShortlistedPackageList());
			}
		}
		this.getManagedBean().setSelectionMode(selectionMode);
	}
	
	private void resetPackageWiseSelectionDetails(boolean isZonal, 
					ScQtFinalization scQtFinalization) {
		if (isZonal) {
			
			// Reset recommend details [TODO : Test]
//			Set<Long> zonalKeySet = scQtFinalization
//					.getScQtRecommendsByZone().keySet();
//			for (Long specZoneId : zonalKeySet) {
//				resetItemDetails(scQtFinalization
//						.getScQtRecommendsByZone().values());
//			}
			resetItemDetails(scQtFinalization.getScQtRecommends());			
			if (this.getManagedBean().getRecommendedSupppliersByZone() != null) {
				this.getManagedBean().getRecommendedSupppliersByZone().clear();
			}
		} else {
			resetItemDetails(scQtFinalization.getScQtRecommends());	
			resetShortlistedSupplierData(
					this.getManagedBean().getShortlistedPackageList(), null);
		}
		
		scQtFinalization.setOnlineBiddingEndorse(null);
		this.getManagedBean().setRecommendedSupplier(null);
		this.getManagedBean().setShortlistedSupplier(null);		

		if (!isQTRefinalized()) {
			resetFlag();
		}		
	}
	
	private void resetItemWiseSelectionDetails(boolean isZonal, 
			ScQtFinalization scQtFinalization) {
		
		if (isZonal) {
			
			// Reset recommend details [TODO : Test]
//			Set<Long> zonalKeySet = scQtFinalization
//					.getScQtRecommendsByZone().keySet();
//			for (Long specZoneId : zonalKeySet) {
//				resetItemDetails(scQtFinalization
//						.getScQtRecommendsByZone().values());
//			}
			resetItemDetails(scQtFinalization.getScQtRecommends());	
			if (this.getManagedBean().getRecommendedSupppliersByZone() != null) {
				this.getManagedBean().getRecommendedSupppliersByZone().clear();
			}			
		} else {
			resetItemDetails(scQtFinalization.getScQtRecommends());
		}
		scQtFinalization.setOnlineBiddingEndorse(null);
		
		if (!isQTRefinalized()) {
			resetFlag();
		}
	}
	
	private String setSelectionMode(String finalizationMethod) {
		this.getManagedBean().setSingleAward(false);
		this.getManagedBean().setMultipleLoi(false);
		this.getManagedBean().setOnlineBidding(false);

		String selectionMode = null;
		if (isQTRefinalized() || ParameterConst.RECOMMEND_BY_AWARD
							.equals(finalizationMethod)) {
			this.getManagedBean().setSingleAward(true);
			
			selectionMode = QTConstants.SF_SINGLE;
		} else if (ParameterConst.RECOMMEND_BY_ONLINE_BIDDING
					.equals(finalizationMethod)) {
			setOnlineBiddingFlag();
			
			selectionMode = QTConstants.SF_MULTIPLE;
		} else if (ParameterConst.RECOMMEND_BY_MULTIPLE_LOI
						.equals(finalizationMethod)) {
			this.getManagedBean().setMultipleLoi(true);
			
			selectionMode = QTConstants.SF_MULTIPLE;
		}		
		this.getManagedBean().setFinalizationMethod(
									finalizationMethod);
		
		return selectionMode;
	}
	
	/**
	 * Event for Re-evaluate by Document compliance, Technical, Financial
	 */
	public void reEvaluateByOptionEvent(String reEvaluateOption, boolean isSelected) {
		ScQt scQt = getManagedBean().getScQt();
		String evaluationType = scQt.getEvaluationType();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		
		if ("1".equals(reEvaluateOption)) {
			scQtFinalization.setReEvaluateCompReq(isSelected);
			
			if ( ParameterConst.EVALUATION_TYPES_TWO_TIER
					.equals(evaluationType) || 
						(ParameterConst.EVALUATION_TYPES_ONE_TIER
							.equals(evaluationType) && 
								scQt.isTechEvaluationReq()) ) {
				scQtFinalization.setReEvaluateTechReq(isSelected);
			}
			scQtFinalization.setReEvaluateFinReq(isSelected);
		} else if ("2".equals(reEvaluateOption)) {
			scQtFinalization.setReEvaluateTechReq(isSelected);
			scQtFinalization.setReEvaluateFinReq(isSelected);
		} else if ("3".equals(reEvaluateOption)) {
			scQtFinalization.setReEvaluateFinReq(isSelected);
		} 
	}

	/**
	 * Event for Recommend By Award, Online Bidding or Multiple LOI 
	 */
	public void finalizationMethodEvent(AjaxBehaviorEvent ajaxEvent) {
		debugLogger("finalizationMethodEvent ");
		
		boolean qtRefinalized = isQTRefinalized();
		boolean isItemize = getManagedBean().isItemize();
		String finalizationMethodStr = getManagedBean().getFinalizationMethod();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		
		// Checking for Bidding process to have atleast 2 qualified suppliers
		if (ajaxEvent != null && 
				ParameterConst.RECOMMEND_BY_ONLINE_BIDDING
					.equals(finalizationMethodStr)) {
			checkIfAtleastTwoSuppliersExist();
		}
		
		if (StringUtils.isNotBlank(finalizationMethodStr)) {
			this.getManagedBean().setSingleAward(false);
			this.getManagedBean().setOnlineBidding(false);
			this.getManagedBean().setMultipleLoi(false);
			
			if (qtRefinalized || ParameterConst.RECOMMEND_BY_AWARD
						.equals(finalizationMethodStr)) {
				if (!qtRefinalized) {
					scQtFinalization.setOnlineBiddingEndorse(null);
				}
				this.getManagedBean().setSelectionMode(QTConstants.SF_SINGLE);
				this.getManagedBean().setSingleAward(true);
				// this.getManagedBean().setBiddingEndorsement(null);

				if (!qtRefinalized) {
					List<ScQtEvaluationResultDtl> recommendedSupplierList 
								= this.getManagedBean().getRecommendedSupplierList();
					if (CollectionUtils.isNotEmpty(
								recommendedSupplierList)) {
						for (ScQtEvaluationResultDtl evalResDtl 
										: recommendedSupplierList) {
							evalResDtl.setAwareded(false);
							evalResDtl.setShortListed(false);
						}
					}
				}
				
				this.getManagedBean().setRecommendedSupplierList(null);
			} else {
				this.getManagedBean().setSelectionMode(QTConstants.SF_MULTIPLE);
				scQtFinalization.setOnlineBiddingEndorse(null);
				
				ScQtEvaluationResultDtl recommendedSupplier 
							= this.getManagedBean().getRecommendedSupplier();
				if (recommendedSupplier != null) {
					recommendedSupplier.setAwareded(false);
					recommendedSupplier.setShortListed(false);
					this.getManagedBean().setRecommendedSupplier(null);
				}
				
				if (ParameterConst.RECOMMEND_BY_ONLINE_BIDDING
						.equals(finalizationMethodStr)) {
					setOnlineBiddingFlag();
				} else if (ParameterConst.RECOMMEND_BY_MULTIPLE_LOI
							.equals(finalizationMethodStr)) {
					this.getManagedBean().setMultipleLoi(true);
				} else if (ParameterConst.RECOMMEND_BY_CANCEL
									.equals(finalizationMethodStr)) {
					if (CollectionUtils.isNotEmpty(
							getManagedBean().getItemList())) {
						for (ScQtRecommend scQtRecommend 
									: getManagedBean().getItemList()) {
							prepareToDeleteRecommendDtls(scQtRecommend);
						}
					}
					resetShortlistedSupplierData(
							this.getManagedBean().getShortlistedSupplierList(), null);
				}
			}
			PmParameter finalizationMethod = getParameterByTypeAndCode(
					ParameterTypeConst.FINALIZATION_RECOMMEND_BY, finalizationMethodStr);
			getManagedBean().getScQtFinalization().setFinalizationMethod(finalizationMethod);
			
			if (ajaxEvent != null) { 
				this.getManagedBean().setDisableSupplierSelection(false);
				
				if (isItemize) {	
					recommendSuppliersByItem();
				} else {
					if (getManagedBean().getRecommendedSupppliersByZone() != null) {
						Map<Long,List<ScQtEvaluationResultDtl>> recommendedSupppliersByZoneMap 
									= getManagedBean().getRecommendedSupppliersByZone();
						if (MapUtils.isNotEmpty(recommendedSupppliersByZoneMap)) {
							for (Long specZoneId : recommendedSupppliersByZoneMap.keySet()) {
								if (CollectionUtils.isNotEmpty(
										recommendedSupppliersByZoneMap.get(specZoneId))) {
									for (ScQtEvaluationResultDtl evalResultDtl
												: recommendedSupppliersByZoneMap.get(specZoneId)) {
										evalResultDtl.setAwareded(false);
										evalResultDtl.setShortListed(false);
									}
								}
							}
							recommendedSupppliersByZoneMap.clear();
						}
					}					
				}
			}
		} else {
			getManagedBean().setFinalizationMethod(StringUtils.EMPTY);
		}
	}
	
	private void checkIfAtleastTwoSuppliersExist() {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		
		boolean isItemize = getManagedBean().isItemize();
		ScQt scQt = getManagedBean().getScQt();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		List<TplSpecZoneDtl> specZones = getManagedBean().getZoneDetails();
		Map<Long, TplSpecZoneDtl> zoneMap = getManagedBean().getZoneMap();
		SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
		
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);	
			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setZonal(scQt.isZonal());
			reqDTO.setItemize(isItemize);
			
			if (isItemize) {
				List<Long> requestItemIds = new ArrayList<Long>();
				for (ScQtRecommend scQtRecommend 
							: scQtFinalization.getScQtRecommends()) {
					if (!(scQtRecommend.isCancelled() || 
							scQtRecommend.isAwardedItem())) {
						requestItemIds.add(scQtRecommend
								.getRequestItem().getRequestNoteItemId());
					}
				}
				reqDTO.setRequestItemIds(requestItemIds);
			} else if (scQt.isZonal()) {
				List<Long> specZoneDtlIds = new ArrayList<Long>();	
				for (TplSpecZoneDtl specZoneDtl : specZones) {
					specZoneDtlIds.add(specZoneDtl.getSpecZoneId());
				}
				reqDTO.setSpecZoneDtlIds(specZoneDtlIds);
			}
			
			SuppFinalizationResDTO resDTO 
					= qtSuppFinalizationSO.hasAtleastTwoQualifiedSuppliers(reqDTO);
			
			if (!resDTO.isSuccessful()) {
				getManagedBean().setFinalizationMethod(
						scQtFinalization.getFinalizationMethod().getParameterCode());
				
				List<Object[]> qualifiedSupplierCountList 
									= resDTO.getQualifiedSupplierCountList();
				Object[] paramsObj = qualifiedSupplierCountList.get(0);
				
				if (isItemize) {
					if (scQt.isZonal()) {
						String zoneName = zoneMap.get(((BigDecimal) paramsObj[2]).longValue()).getZoneName();
						throw new EpException(MessageConst.ERROR_760370, new String[] { (String) paramsObj[0], zoneName});
					} else {
						throw new EpException(MessageConst.ERROR_760371, new String[] { (String) paramsObj[0]});
					}
				} else {
					if (scQt.isZonal()) {
						String zoneName = zoneMap.get(((BigDecimal) paramsObj[0]).longValue()).getZoneName();
						getManagedBean().setFinalizationMethod(
								scQtFinalization.getFinalizationMethod().getParameterCode());
						throw new EpException(MessageConst.ERROR_760372, new String[] { zoneName });
					} else {
						throw new EpException(MessageConst.ERROR_760373);
					}
				}
			}
		} finally {
			qtSuppFinalizationSO = null;
		}		
	}

	private void recommendSuppliersByItem() {
		debugLogger("recommendSuppliersByItem");

		resetRecommendFlag();
		this.getManagedBean().setShortlistedSupplierList(null);
		this.getManagedBean().setDisableSupplierSelection(false);

		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		PmParameter finalizationMethod = scQtFinalization.getFinalizationMethod();
		
		if (finalizationMethod == null) {
			return;
		}

		for (ScQtRecommend scQtRecommend 
					: scQtFinalization.getScQtRecommends()) {
			
			// clear the RecommendDtl list
			if (scQtRecommend.getRecommendDtls() != null) {
				scQtRecommend.setAwardSupplierList(
						new ArrayList<ScQtEvaluationResultDtl>());
				
				prepareToDeleteRecommendDtls(scQtRecommend);
	
				// reset awarded & short listed flag.
				resetShortlistedSupplierData(
						scQtRecommend.getShortListedSupplierList(), null);			
			}
		}
	}
	
	/**
	 * Event for item cancellation where Recommend Type is Itemized 
	 * under Supplier Finalization tab
	 */
	public void itemCancellationEvent(ScQtRecommend scQtRecommend) {
		debugLogger("itemCancellationEvent");
		
		if (scQtRecommend.isCancelled()) {
			this.getManagedBean().setItemCancelled(true);
			this.getManagedBean().setShortlistedSupplierList(null);
			this.getManagedBean().setDisableSupplierSelection(false);
			
			// clear the RecommendDtl list
			if (scQtRecommend.getRecommendDtls() != null) {
				scQtRecommend.setAwardSupplierList(
						new ArrayList<ScQtEvaluationResultDtl>());
				
				prepareToDeleteRecommendDtls(scQtRecommend);
	
				// reset awarded & short listed flag.
				resetShortlistedSupplierData(
						scQtRecommend.getShortListedSupplierList(), null);			
			}
		} else {
			this.getManagedBean().setItemCancelled(false);
		}
	}
	
	/**
	 * Event for item cancellation where QT is Zonal and Recommend Type is Package  
	 * under Supplier Finalization tab
	 */	
	public void zonalPackageItemsCancellationEvent() {
		debugLogger("zonalPackageItemsCancellationEvent");
		
		this.getManagedBean().setDisableSupplierSelection(false);
		boolean zonalPackageItemsCancelled = getManagedBean().isItemCancelled();
		
		Long currentZoneId = getManagedBean().getCurrentZone().getSpecZoneId();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		Map<Long,List<ScQtRecommend>> zonalItems = scQtFinalization.getScQtRecommendsByZone();
		List<ScQtRecommend> scQtRecommends = null;
		
		if (MapUtils.isNotEmpty(zonalItems) && 
				zonalItems.containsKey(currentZoneId)) {
			scQtRecommends = zonalItems.get(currentZoneId);
		}		
		
		if (CollectionUtils.isNotEmpty(scQtRecommends)) {
			if (zonalPackageItemsCancelled) {
				for (ScQtRecommend scQtRecommend : scQtRecommends) {
					scQtRecommend.setCancelled(true);
					
					// clear the RecommendDtl list
					if (CollectionUtils.isNotEmpty(
							scQtRecommend.getRecommendDtls())) {
						scQtRecommend.setAwardSupplierList(null);
						prepareToDeleteRecommendDtls(scQtRecommend);
					}					
				}
				
				// reset awarded & short listed flag of suppliers in current zone
				resetShortlistedSupplierData(
						getManagedBean().getShortlistedSupplierList(), null);	
			} else {
				for (ScQtRecommend scQtRecommend : scQtRecommends) {
					scQtRecommend.setCancelled(false);
				}			
			}
		}
	}
	
	/**
	 * Event for Forward to Higher Authority under Supplier Finalization tab
	 */
	public void fwdHigherAuthorityEvent() {
		debugLogger("fwdHigherAuthorityEvent");

		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		boolean fwdHigherAuthority = scQtFinalization.isHigherAuthReq();
		// boolean forward = false;

		// if (fwdHigherAuthority.equalsIgnoreCase("true")) {
		// forward = true;
		if (fwdHigherAuthority) {
			String higherCommitteeType = null;
			if (scQtFinalization.getHigherCommitte() != null) {
				higherCommitteeType = scQtFinalization
						.getHigherCommitte().getParameterCode();
			}
//			if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A
//					.equals(committeeType)
//						|| ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B
//								.equals(committeeType)
//						|| ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH
//								.equals(committeeType)
//						|| ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK
//								.equals(committeeType)) {
			if (SourcingConstants.FC_COMMITTEE_TYPE_MOF
								 .equals(higherCommitteeType)) {
				getManagedBean().setForwardToBpk(true);
			} else if (getManagedBean().isSecretaryUser()) {
				// getFinalizationCommitteeDropDown();
				if (getManagedBean().getScQtFinalization().getHigherCommitte() == null) {
					getManagedBean().getScQtFinalization().setHigherCommitte(
							new PmParameter());
				}
				if (getManagedBean().getScQtFinalization().getReason() == null) {
					getManagedBean().getScQtFinalization().setReason(new PmReason());
				}
			}
		} else if (!this.getManagedBean().isInitialDecisionFwdHighAuth()) {
			resetForwardToHigherAuthority(scQtFinalization);
		}
	}

	/**
	 * Event to auto populate selected finalization committee secretary's name
	 */
	public void finalizationCommitteeEvent() {
		debugLogger("finalizationCommitteeEvent");

		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		Long paramId = scQtFinalization.getHigherCommitte().getParameterId();
		PmParameter parameter = ParameterHelper.getInstance()
				.getPmParameterById(paramId);
		scQtFinalization.setHigherCommitte(parameter);
		scQtFinalization.setHigherCommitteStr(parameter.getParameterCode());
		scQtFinalization.setHigherSecretary(new PmUser());
	}

	/**
	 * Event to update offered price by the supplier when the value of quantity
	 * changes, where Recommend Type is Itemized, item Recommend By is Multiple
	 * Award under Supplier Finalization tab
	 * 
	 * @param recommendDtl
	 *            the recommendDtl to set
	 */
	public void updateOfferedPrice(ScQtRecommendDtl recommendDtl) {
		debugLogger("updateOfferedPrice...");

		boolean isItemRowSelected = this.getManagedBean().isItemRowSelect();
		if (isItemRowSelected && recommendDtl != null) {
			SmSupplier currentSupplier = recommendDtl.getSupplier();
			List<ScQtEvaluationResultDtl> shortlistedItemSupplierList = this
					.getManagedBean().getShortlistedSupplierList();
			if (currentSupplier != null
					&& CollectionUtils.isNotEmpty(shortlistedItemSupplierList)) {
				for (ScQtEvaluationResultDtl evalResultDtl : shortlistedItemSupplierList) {
					SmSupplier shortListedSupplier = evalResultDtl
							.getQtEvaluationResult().getScQtProposal()
							.getScQtSupplier().getSmSupplier();
					ScQtProposalItem proposalItem = evalResultDtl
							.getQtEvaluationResult().getScQtProposalItem();
					if (proposalItem != null
							&& (currentSupplier.getSupplierId())
									.equals(shortListedSupplier.getSupplierId())) {
						ScRequestItem requestItem = recommendDtl
								.getScQtRecommend().getRequestItem();
						BigDecimal offeredPrice = TemplateSpecHelper
								.getInstance().calculateTotalAmount(
										requestItem.getTplSpecQuestion(),
										proposalItem
										.getOfferPrice(),
										recommendDtl.getAwardQty());
						evalResultDtl.getQtEvaluationResult()
									 .setOfferedPrice(offeredPrice);
						evalResultDtl.setAmountInWords(
								 convertNumberToWord(offeredPrice));
					}
				}
			}
		}
	}

	/**
	 * Event to select an item where Recommend Type is Itemized under Supplier
	 * Finalization tab
	 * 
	 * @param event
	 *            the event to set
	 */
	public void selectItemEvent(SelectEvent event) {
		debugLogger("selectItemEvent");

		Long specZoneId = null;
		if (getManagedBean().getScQt().isZonal()) {
			specZoneId = getManagedBean().getCurrentZone().getSpecZoneId();
		}
		ScQtFinalization scQtFinalization = 
							getManagedBean().getScQtFinalization();
		ScQtRecommend scQtRecommend = (ScQtRecommend) event.getObject();
		this.getManagedBean().setItemRowSelect(true);

		// Reset selection
		this.getManagedBean().setSelectAll(false);
		this.getManagedBean().setSelectedItemList(null);
		this.getManagedBean().setShortlistedSupplierList(null);
		
		setRefinalizedAndBiddingAwardStatus(
					scQtFinalization, specZoneId);
		
		scQtRecommend.setSelected(true);
		addSelectedItem(scQtRecommend);
		
		if (!scQtRecommend.isCancelled()) {
			getManagedBean().setSelectedItem(scQtRecommend);
		}
		
		super.selectAll(false, this.getManagedBean().getItemList());
	}

	/**
	 * Event to select an item where Recommend Type is Itemized under Supplier
	 * Finalization tab
	 * 
	 * @param scQtRecommend
	 *            the scQtRecommend to set
	 */
	public void selectItemEvent(ScQtRecommend scQtRecommend) {
		getManagedBean().setSelectedItem(null);
		getManagedBean().setItemRowSelect(false);
		addSelectedItem(scQtRecommend);
	}

	private void addSelectedItem(ScQtRecommend scQtRecommend) {
		this.getManagedBean().setSelectedItemAlreadyAwarded(false);
		
		if (scQtRecommend.isCancelled()) {
			this.getManagedBean().setItemCancelled(true);
		} else {
			this.getManagedBean().setItemCancelled(false);
		}
		
		if (scQtRecommend.isDisabled() || 
				scQtRecommend.isAwardedItem()) {
			if (this.getManagedBean().isItemRowSelect()) {
				this.getManagedBean().setDisableSupplierSelection(true);
				if (scQtRecommend.isAwardedItem()) {
					this.getManagedBean().setSelectedItemAlreadyAwarded(true);
				}
			} else {
				return;
			}
		} else {
			this.getManagedBean().setDisableSupplierSelection(false);
		}
		
		if (getManagedBean().getSelectedItemList() == null) {
			getManagedBean().setSelectedItemList(
					new ArrayList<ScQtRecommend>());
		}
		if (scQtRecommend.isSelected()) {
			scQtRecommend.setAwardSupplierList(
					new ArrayList<ScQtEvaluationResultDtl>());
			List<ScQtEvaluationResultDtl> shortlistedItemSupplierList = null;
			boolean isSuccessful = false;
			
			if (CollectionUtils.isEmpty(scQtRecommend
					.getShortListedSupplierList())) {
				// Retrieve the suppliers short listed for the selected item
				SuppFinalizationResDTO resDTO = this
						.retrieveShortListedSuppliersByItem(scQtRecommend);
				isSuccessful = resDTO.isSuccessful();
				shortlistedItemSupplierList = resDTO
						.getQualifiedByItemSuppliers();

				// disabled supplier selection.
				disableSuppliersByItem(scQtRecommend,
						shortlistedItemSupplierList);

			} else {
				isSuccessful = true;
				shortlistedItemSupplierList = scQtRecommend
						.getShortListedSupplierList();
			}
			
			if (isSuccessful) {
				if (CollectionUtils.isNotEmpty(shortlistedItemSupplierList)) {
					boolean hasRecommendedSuppliers = true;
					if (CollectionUtils.isEmpty(
							scQtRecommend.getRecommendDtls())) {
						hasRecommendedSuppliers = false;
					}
					for (ScQtEvaluationResultDtl evalResultDtl 
										: shortlistedItemSupplierList) {
						if (!hasRecommendedSuppliers) {
							evalResultDtl.setAwareded(false);
							evalResultDtl.setShortListed(false);
						}
						
						// Skip offered price or bid price calculation 
						// if item is cancelled
						if (!scQtRecommend.isCancelled()) {
							calculateOfferedPrice(
									evalResultDtl, scQtRecommend);
						} else {
							BigDecimal zero = new BigDecimal(0.0);
							evalResultDtl.setBidPrice(zero);
							evalResultDtl.getQtEvaluationResult().setOfferedPrice(zero);
							evalResultDtl.setAmountInWords(convertNumberToWord(zero));
						}
					}
				}
			}
			
			PmParameter finalizationMethod = getManagedBean().getScQtFinalization()
												.getFinalizationMethod();
			if (this.getManagedBean().isItemRowSelect()) {
				this.getManagedBean().setShortlistedSupplierList(
						shortlistedItemSupplierList);
				this.getManagedBean().setRecommendedSupplierByItem(null);
				this.getManagedBean().setPrevRecommendedSupplierByItem(null);			
			} else {
				this.getManagedBean().setShortlistedSupplierList(null);
			}
			scQtRecommend.setShortListedSupplierList(
							shortlistedItemSupplierList);
			getManagedBean().getSelectedItemList().add(scQtRecommend);

			String finalizationMethodStr = finalizationMethod.getParameterCode();
			List<ScQtEvaluationResultDtl> recommendedSupplierList 
							= new ArrayList<ScQtEvaluationResultDtl>();
			List<ScQtRecommendDtl> recommendDtls 
					= scQtRecommend.getRecommendDtls();
			
			if (CollectionUtils.isNotEmpty(shortlistedItemSupplierList)
					&& CollectionUtils.isNotEmpty(recommendDtls)) {
				for (ScQtRecommendDtl recommendDtl : recommendDtls) {
					for (ScQtEvaluationResultDtl evalResultDtl : shortlistedItemSupplierList) {
						Long recommendedSupplierId = recommendDtl.getSupplier()
								.getSupplierId();
						Long shortlistedSupplierId = evalResultDtl
								.getQtEvaluationResult().getScQtProposal()
								.getScQtSupplier().getSmSupplier()
								.getSupplierId();
						if (shortlistedSupplierId.equals(recommendedSupplierId)) {
							scQtRecommend.getAwardSupplierList()
										 .add(evalResultDtl);
							recommendedSupplierList.add(evalResultDtl);
							
							if (recommendDtl.getRecordStatusFlg()
									.equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
								if (isQTRefinalized() || 
										ParameterConst.RECOMMEND_BY_AWARD
											.equals(finalizationMethodStr)) {
									evalResultDtl.setAwareded(true);
									evalResultDtl.setShortListed(false);

									this.getManagedBean().setRecommendedSupplierByItem(
												evalResultDtl);
									this.getManagedBean().setPrevRecommendedSupplierByItem(
												evalResultDtl);										
								} else {
									evalResultDtl.setAwareded(false);
									evalResultDtl.setShortListed(true);
								}
							}
							
						}
					}
				}
			}
			getManagedBean().setRecommendedSupplierList(
					recommendedSupplierList);
		} else {
			getManagedBean().setShortlistedSupplierList(
					new ArrayList<ScQtEvaluationResultDtl>());
			getManagedBean().getSelectedItemList().remove(scQtRecommend);
			getManagedBean().setSelectAll(false);
		}
	}

	/**
	 * Event to select or unselect all items where Recommend Type is Itemized
	 * under Supplier Finalization tab
	 * 
	 * @param selectAll
	 *            the selectAll to set
	 */
	public void selectAllItems(boolean selectAll) {
		debugLogger("selectAllItemsEvent");

		getManagedBean().setSelectedItem(null);
		getManagedBean().setItemRowSelect(false);
		List<ScQtRecommend> selectedItemsList = new ArrayList<ScQtRecommend>();
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getItemList())) {
			selectedItemsList.addAll(this.getManagedBean().getItemList());
		}
		if (selectAll) {
			if (CollectionUtils.isNotEmpty(selectedItemsList)) {
				List<ScQtRecommend> cancelledItems = new ArrayList<ScQtRecommend>();
				for (ScQtRecommend scQtRecommend : selectedItemsList) {
					if (scQtRecommend.isCancelled() || 
							scQtRecommend.isAwardedItem()) {
						cancelledItems.add(scQtRecommend);
						continue;
					}
					if (CollectionUtils.isEmpty(scQtRecommend
							.getShortListedSupplierList())) {
						scQtRecommend.setSelected(true);
						addSelectedItem(scQtRecommend);
					}
				}
				if (cancelledItems.size() > 0) {
					selectedItemsList.removeAll(cancelledItems);
				}
			}
			this.getManagedBean().setSelectedItemList(selectedItemsList);
		} else {
			this.getManagedBean().setSelectedItemList(null);
		}
		this.getManagedBean().setShortlistedSupplierList(null);
		this.getManagedBean().setSelectAll(selectAll);
		super.selectAll(selectAll, selectedItemsList);
	}
	
	/**
	 * Event to unselect an item where Recommend Type is Itemized under Supplier
	 * Finalization tab
	 * 
	 * @param event
	 *            the event to set
	 */
	public void unselectItemEvent(UnselectEvent event) {
		debugLogger("unselectItemEvent");

		ScQtRecommend scQtRecommend = (ScQtRecommend) event.getObject();
		scQtRecommend.setSelected(false);
		selectItemEvent(scQtRecommend);
	}	
	
	/**
	 * Event to award supplier for an item [for Selection Based On - Itemized]
	 * Finalization of Supplier Selection tab
	 */
	public void recommendItemSupplierByAwardEvent() {
		ScQtEvaluationResultDtl recommendedSupplierByItem 
				= this.getManagedBean().getRecommendedSupplierByItem();
		ScQtEvaluationResultDtl prevRecommendedSupplierByItem 
				= this.getManagedBean().getPrevRecommendedSupplierByItem();
		if (prevRecommendedSupplierByItem != null) {
			prevRecommendedSupplierByItem.setAwareded(false);
		}
		if (this.getManagedBean().getRecommendedSupplierByItem() == null) {
			this.getManagedBean().setRecommendedSupplierList(
									new ArrayList<ScQtEvaluationResultDtl>());
		} else {
			this.getManagedBean().getRecommendedSupplierList().clear();
		}
		this.getManagedBean().setPrevRecommendedSupplierByItem(
				recommendedSupplierByItem);
		recommendedSupplierByItem.setAwareded(true);
		
		selectSupplierEvent(recommendedSupplierByItem, true);
	}

	/**
	 * Event for supplier selection where Recommend Type is Itemized under
	 * Supplier Finalization tab
	 */
	public void selectSupplierEvent(ScQtEvaluationResultDtl evaluationResultDtl, 
									boolean isAwarded) {
		debugLogger("selectSupplierEvent");

		boolean isSelected = false;
		boolean isSecretaryUser = this.getManagedBean().isSecretaryUser();
		boolean isItemized = this.getManagedBean().isItemize();
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		
		if (isAwarded) {
			isSelected = evaluationResultDtl.isAwareded();
			if (isSelected) {
				evaluationResultDtl.setShortListed(false);
			}
		} else {
			isSelected = evaluationResultDtl.isShortListed();
			if (isSelected) {
				evaluationResultDtl.setAwareded(false);
			}
		}
		
		if (isSecretaryUser || 
				this.getManagedBean().isInitialDecisionFwdToBPK()) {
			List<ScQtEvaluationResultDtl> recommendedSupplierList = 
						getManagedBean().getRecommendedSupplierList();
			if (recommendedSupplierList == null) {
				recommendedSupplierList = new ArrayList<ScQtEvaluationResultDtl>();
				getManagedBean().setRecommendedSupplierList(
						recommendedSupplierList);
			}
			
			if (isSelected) {
				recommendedSupplierList.add(evaluationResultDtl);
				if (isItemized) {
					addSelectedSupplier();
				} else if (isZonal) {
					TplSpecZoneDtl currentZone = this.getManagedBean().getCurrentZone();
					Map<Long, List<ScQtEvaluationResultDtl>> recommendedSuppliersByZoneMap =
							this.getManagedBean().getRecommendedSupppliersByZone();
					
					if (recommendedSuppliersByZoneMap == null) {
						recommendedSuppliersByZoneMap =
								new LinkedHashMap<Long, List<ScQtEvaluationResultDtl>>();
						this.getManagedBean().setRecommendedSupppliersByZone(
								recommendedSuppliersByZoneMap);
					}
					
					// Add the recommended suppliers to recommended suppliers zonal map 
					// when the supplier is selected
					filterRecommendedSuppliersByZone(currentZone, 
									recommendedSupplierList);
				}
			} else {
				recommendedSupplierList.remove(evaluationResultDtl);
				evaluationResultDtl.setAwareded(false);
				evaluationResultDtl.setShortListed(false);
				unSelectSupplierEvent(evaluationResultDtl);
			}
		} else {
			
			// Reset to previous selection, if the user is not a secretary
			if (isAwarded) {
				evaluationResultDtl.setAwareded(!isSelected);
			} else {
				evaluationResultDtl.setShortListed(!isSelected);
			}
		}
	
	}
	
	private void setOnlineBiddingFlag() {
		this.getManagedBean().setOnlineBidding(true); 
		
		// If the QT is forwarded to BPK, set the default
		// endorsement type as ESCALATION
		if (getManagedBean().isInitialDecisionFwdToBPK()) {
			getManagedBean().getScQtFinalization()
				.setOnlineBiddingEndorse(
					ParameterConst.ONLINE_BIDDING_ESCALATION);
		} else {
			getManagedBean().getScQtFinalization()
				.setOnlineBiddingEndorse(
						ParameterConst.ONLINE_BIDDING_MEETING);
		}
	}

	private void addSelectedSupplier() {
		ScQtFinalization scQtFinalization 
				= getManagedBean().getScQtFinalization();
		String recommendType = scQtFinalization
				.getRecommendedTypeId().getParameterCode();
		List<ScQtRecommend> selectedItemList 
					= getManagedBean().getSelectedItemList();
		if (CollectionUtils.isNotEmpty(selectedItemList)) {
			List<ScQtEvaluationResultDtl> recomendedSupplierList 
						= getManagedBean().getRecommendedSupplierList();
			for (ScQtRecommend scQtRecommend : selectedItemList) {
				PmParameter finalizationMethod = scQtFinalization.getFinalizationMethod();

				if (finalizationMethod != null) {
					scQtRecommend.setSelected(false);					
				}
				scQtRecommend.setAwardSupplierList(
						new ArrayList<ScQtEvaluationResultDtl>());
				if (scQtRecommend.getRecommendDtls() == null) {
					scQtRecommend.setRecommendDtls(
							new ArrayList<ScQtRecommendDtl>());
				}
				if (CollectionUtils.isNotEmpty(recomendedSupplierList)) {
					Set<ScQtRecommendDtl> toBeAddedSet = new HashSet<ScQtRecommendDtl>();
					if (!this.getManagedBean().isItemRowSelect()) {
						if (scQtRecommend.getRecommendDtls() != null) {
							toBeAddedSet.addAll(scQtRecommend
									.getRecommendDtls());
						}
					}
					filterRecommendedSuppliersList(toBeAddedSet, 
									recomendedSupplierList, scQtRecommend);
				} else {
					scQtRecommend.setAwardSupplierList(
							new ArrayList<ScQtEvaluationResultDtl>());
					if (scQtRecommend.getRemoveSupplierList() == null) {
						scQtRecommend.setRemoveSupplierList(
								new ArrayList<ScQtRecommendDtl>());
					}
					
					Set<ScQtRecommendDtl> toBeAddedSet = new HashSet<ScQtRecommendDtl>();
					processAndSetDeletedRecommendDtls(toBeAddedSet, scQtRecommend, false);
					scQtRecommend.getRecommendDtls().clear();
					
					if (CollectionUtils.isNotEmpty(toBeAddedSet)) {
						scQtRecommend.getRecommendDtls().addAll(toBeAddedSet);
					}
				}
				
				if (CollectionUtils.isNotEmpty(
						scQtRecommend.getRecommendDtls()) && 
							CollectionUtils.isNotEmpty(
								scQtRecommend.getShortListedSupplierList())) {
					for (ScQtRecommendDtl recommendDtl 
									: scQtRecommend.getRecommendDtls()) {
						for (ScQtEvaluationResultDtl shortlistedSupplier 
											: scQtRecommend.getShortListedSupplierList()) {
							Long shortlistedSupplierId = shortlistedSupplier
									.getQtEvaluationResult().getScQtProposal()
									.getScQtSupplier().getSmSupplier().getSupplierId();
							
							if (recommendDtl.getRecordStatusFlg()
									.equals(DbValueConst.RECORD_STATUS_ACTIVE) && 
										shortlistedSupplierId.equals(
												recommendDtl.getSupplier().getSupplierId())) {
								scQtRecommend.getAwardSupplierList()
											 .add(shortlistedSupplier);								
							}
						}
					}
				}

				if (recommendType.equalsIgnoreCase(
						ParameterConst.RECOMMEND_TYPE_ITEM) && 
							scQtRecommend.getAwardSupplierList() != null) {
					scQtRecommend.setSelected(false);
				}
				
			}
		}
		
		this.getManagedBean().setSelectAll(false);
	}
	
	private BigDecimal calculateTotalIntendedPrice(ScQtFinalization scQtFinalization) {
		boolean isZonal = getManagedBean().getScQt().isZonal();
		BigDecimal intendedPrice = null;
		BigDecimal totalIntendedPrice = new BigDecimal(0);
		
		if (getManagedBean().isItemize()) {
			for (ScQtRecommend item 
					: scQtFinalization.getScQtRecommends()) {
				if (!item.isCancelled()) {
					List<ScQtEvaluationResultDtl> shortlistedSuppliers 
							= item.getShortListedSupplierList();
					if (CollectionUtils.isEmpty(
									shortlistedSuppliers)) {
						SuppFinalizationResDTO resDTO 
							= retrieveShortListedSuppliersByItem(item);
						shortlistedSuppliers = resDTO.getQualifiedByItemSuppliers();
						item.setShortListedSupplierList(shortlistedSuppliers);
					}
					if (CollectionUtils.isNotEmpty(
							shortlistedSuppliers)) {
						
					}
				}
			}
		} else {
			if (isZonal) {
				Map<Long,List<ScQtEvaluationResultDtl>> shortlistedSuppliersByZone
							= getManagedBean().getPkgShortlistedSupppliersByZone();
				if (MapUtils.isNotEmpty(
						shortlistedSuppliersByZone)) {
					for (Long specZoneId : shortlistedSuppliersByZone.keySet()) {
						List<ScQtRecommend> items = scQtFinalization
								.getScQtRecommendsByZone().get(specZoneId);
						List<ScQtEvaluationResultDtl> shortlistedSuppliers 
								= shortlistedSuppliersByZone.get(specZoneId);
						intendedPrice = calculateIntendedPrice(
											shortlistedSuppliers,items);
						totalIntendedPrice = totalIntendedPrice.add(intendedPrice);
						totalIntendedPrice = totalIntendedPrice.setScale(2,
												BigDecimal.ROUND_HALF_EVEN);
					}
				}
			} else {
				List<ScQtEvaluationResultDtl> shortlistedSuppliers 
						= getManagedBean().getShortlistedPackageList();
				List<ScQtRecommend> items = scQtFinalization.getScQtRecommends();
				intendedPrice = calculateIntendedPrice(
									shortlistedSuppliers, items);
				totalIntendedPrice = totalIntendedPrice.add(intendedPrice);
				totalIntendedPrice = totalIntendedPrice.setScale(2,
										BigDecimal.ROUND_HALF_EVEN);				
			}
		}
		
		return totalIntendedPrice;
	}
	
	private BigDecimal calculateIntendedPrice(List<ScQtEvaluationResultDtl> shortlistedSuppliers, 
										List<ScQtRecommend> items) {
		BigDecimal intendedPrice = new BigDecimal(0);
		if (CollectionUtils.isNotEmpty(
					shortlistedSuppliers) && 
					!items.get(0).isCancelled()) {
			for (ScQtEvaluationResultDtl shortlistedSupplier
							: shortlistedSuppliers) {
				if (shortlistedSupplier.isAwareded() || 
						shortlistedSupplier.isShortListed()) {
					for (ScQtRecommend item : items) {
						calculateOfferedPrice(shortlistedSupplier, item);
						
						BigDecimal price = null;
						if (getManagedBean().isRefinalizedAndBiddingAward()) {
							price = shortlistedSupplier.getBidPrice();
						} else {
							price = shortlistedSupplier.getQtEvaluationResult()
									.getOfferedPrice();												
						}
						if (price != null) {
							intendedPrice = intendedPrice.add(price);
							intendedPrice = intendedPrice.setScale(2,
													BigDecimal.ROUND_HALF_EVEN);
						}
					}
				}
			}			
		}			
		
		return intendedPrice;
	}
	
	private void calculateOfferedPrice(ScQtEvaluationResultDtl evalResultDtl,
									   ScQtRecommend scQtRecommend) {
		ScRequestItem requestItem = scQtRecommend.getRequestItem();
		ScQtProposalItem proposalItem = getQtProposalItem(scQtRecommend, evalResultDtl);
		if (proposalItem != null) {
			if (getManagedBean().isRefinalizedAndBiddingAward()) {
				BigDecimal bidPrice = TemplateSpecHelper.getInstance()
						.calculateTotalAmount(requestItem.getTplSpecQuestion(),
								proposalItem.getFinalPrice(),
								proposalItem.getOfferQuantity());
				
				evalResultDtl.setBidPrice(bidPrice);
				evalResultDtl.setAmountInWords(convertNumberToWord(bidPrice));
			} else {
				BigDecimal offeredPrice = TemplateSpecHelper.getInstance()
						.calculateTotalAmount(requestItem.getTplSpecQuestion(),
								proposalItem.getOfferPrice(),
								proposalItem.getOfferQuantity());
				
				evalResultDtl.getQtEvaluationResult()
							 .setOfferedPrice(offeredPrice);	
				evalResultDtl.setAmountInWords(convertNumberToWord(offeredPrice));
			}
		}
	}
	
	private String convertNumberToWord(BigDecimal amount) {
		return WordUtils.capitalizeFully(NumberWordUtil.convert(amount.toString()));
	}
	
	private BigDecimal calculateAfterNegoSubTotal(
			List<ScLoiLoaItem> scLoiLoaItems,
			List<ScQtRecommend> recommendedItems) {
		BigDecimal afterNegotiationSubTotal = new BigDecimal(0.0);

		if (CollectionUtils.isNotEmpty(scLoiLoaItems)) {
			for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItems) {
				if (scLoiLoaItem.getAfterNegoSubTotal() != null) {
					BigDecimal itemAfterNegoSubTotal = scLoiLoaItem
							.getAfterNegoSubTotal();
					itemAfterNegoSubTotal = itemAfterNegoSubTotal.setScale(2,
							BigDecimal.ROUND_HALF_EVEN);
					afterNegotiationSubTotal = afterNegotiationSubTotal
							.add(itemAfterNegoSubTotal);
					afterNegotiationSubTotal = afterNegotiationSubTotal
							.setScale(2, BigDecimal.ROUND_HALF_EVEN);
				}
			}
		}

		return afterNegotiationSubTotal;
	}	
	
	private void calculateBidPrice(List<ScQtRecommend> recommendedItems, 
								   ScQtEvaluationResultDtl scQtEvalResDtl) {
		BigDecimal totalBidPrice = new BigDecimal(0.0);
		
		for (ScQtRecommend scQtRecommend 
								: recommendedItems) {
			if (!scQtRecommend.isCancelled()) {
				ScQtProposalItem proposalItem = getQtProposalItem(scQtRecommend, scQtEvalResDtl);
				ScRequestItem requestItem = scQtRecommend.getRequestItem();
				totalBidPrice = totalBidPrice.add(TemplateSpecHelper.getInstance()
						.calculateTotalAmount(requestItem.getTplSpecQuestion(),
								proposalItem.getFinalPrice(),
								proposalItem.getOfferQuantity()));
				totalBidPrice = totalBidPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
			}
		}
		scQtEvalResDtl.setBidPrice(totalBidPrice);
	}

	private SuppFinalizationResDTO retrieveShortListedSuppliersByItem(
			ScQtRecommend scQtRecommend) {
		SuppFinalizationResDTO resDTO = new SuppFinalizationResDTO();
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setScQtFinalization(this.getManagedBean().getScQtFinalization());
			
			if (scQtRecommend.getTplSpecZoneDtl() != null) {
				reqDTO.setQtZoneCriteriaId(scQtRecommend.getTplSpecZoneDtl()
						.getSpecZoneId());
			}
			reqDTO.setRequestItemId(scQtRecommend
					.getRequestItem().getRequestNoteItemId());
			reqDTO.setRecommendType(SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM);
			
			boolean isPriceTypeTier = isItemPriceTypeTariffOrTier(scQtRecommend);
			reqDTO.setTierPricing(isPriceTypeTier);
			reqDTO.setRefinalize(isQTRefinalized());
			
			resDTO = qtSuppFinalizationSO
							.retrieveEvaluationResultsByItem(reqDTO);
		} finally {
			qtSuppFinalizationSO = null;
		}

		return resDTO;
	}

	private QTSupplierResDTO retrieveSupplierDetails(SmSupplier smSupplier) {
		QTSupplierResDTO resDTO = new QTSupplierResDTO();
		IQTSupplierSO qtSupplierSO = null;
		try {
			qtSupplierSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSupplierSO.class);
			QTSupplierReqDTO reqDTO = new QTSupplierReqDTO();
			reqDTO.setSupplierId(smSupplier.getSupplierId());
			resDTO = qtSupplierSO.retrieveSupplierDetails(reqDTO);
		} finally {
			qtSupplierSO = null;
		}
		return resDTO;
	}

	/**
	 * Event to handle unselection of the supplier where recommend type is
	 * Itemized under Supplier Finalization tab
	 * 
	 * @param scQtEvaluationResult
	 *            the scQtEvaluationResult
	 */
	private void unSelectSupplierEvent(
			ScQtEvaluationResultDtl scQtEvaluationResultDtl) {
		debugLogger("unSelectSupplierEvent");

		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		String recommendType = scQtFinalization.getRecommendedTypeId()
				.getParameterCode();
		if (recommendType.equals(ParameterConst.RECOMMEND_TYPE_PACKAGE)) {
			if (getManagedBean().getUnRecommendedSupplierList() == null) {
				getManagedBean().setUnRecommendedSupplierList(
						new ArrayList<ScQtEvaluationResultDtl>());
			}
			getManagedBean().getUnRecommendedSupplierList().add(
					scQtEvaluationResultDtl);
			if (this.getManagedBean().getScQt().isZonal()) {
				TplSpecZoneDtl currentZone = this.getManagedBean().getCurrentZone();
				List<ScQtEvaluationResultDtl> currentZoneRecommendedSupplierList = 
						this.getManagedBean().getRecommendedSupplierList();
				Map<Long, List<ScQtEvaluationResultDtl>> recommendedSuppliersByZone =
						this.getManagedBean().getRecommendedSupppliersByZone();
				
				// Remove the unrecommended suppliers from recommended zonal map 
				// when the supplier is unselected
				if (MapUtils.isNotEmpty(recommendedSuppliersByZone)) {
					filterRecommendedSuppliersByZone(currentZone, 
							currentZoneRecommendedSupplierList);
				}
			} 
		} else {
			addSelectedSupplier();
		}
	}

	private PmParameter getPmParameterByTypeAndCode(String parameterType,
			String parameterCode) {
		IParameterSO parameterSO = null;
		try {
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterCode(parameterCode);
			reqDTO.setParameterType(parameterType);
			parameterSO = (IParameterSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IParameterSO.class);
			ParameterResDTO resDTO = parameterSO
					.retrieveParameterByTypeAndCode(reqDTO);
			return resDTO.getPmParameter();
		} finally {
			parameterSO = null;
		}
	}

	/**
	 * Event to award an item to one or more suppliers where Recommend Type is
	 * Itemized under Supplier Finalization tab
	 */
	public void awardSupplierEvent() {
		debugLogger("awardSupplierEvent");

		boolean qtRefinalized = isQTRefinalized();
		String awardSupplierStr = getManagedBean().getAwardSupplier();
		List<ScQtRecommend> selectedItemList 
				= this.getManagedBean().getSelectedItemList();
		if (StringUtils.isBlank(awardSupplierStr)
				|| CollectionUtils.isEmpty(selectedItemList)) {
			log.info("No supplier has been selected or no item has been selected");
			return;
		}

		// Remove duplicate items from selected list [which happens due to row
		// selection and checkbox selection]
		Set<ScQtRecommend> selectedItemSet = new HashSet<ScQtRecommend>();
		selectedItemSet.addAll(selectedItemList);
		selectedItemList.clear();

		if (CollectionUtils.isNotEmpty(selectedItemSet)) {
			ScQtRecommend removableItem = null; // Remove item added due to row
												// selection followed by
												// checkbox selection

			boolean isAwarded = false;
			boolean isShortlisted = false;
			PmParameter finalizationMethod = getManagedBean().getScQtFinalization()
										.getFinalizationMethod();
			if (qtRefinalized || ParameterConst.RECOMMEND_BY_AWARD.equals(
									finalizationMethod.getParameterCode())) {
				isAwarded = true;
			} else {
				isShortlisted = true;
			}
			
			// get the SmSupplier object by the dropdown selection
			for (ScQtRecommend scQtRecommend : selectedItemSet) {
				if (!scQtRecommend.isSelected()) {
					removableItem = scQtRecommend;
					continue;
				}
				
				List<ScQtEvaluationResultDtl> recommendedItemSupplierList 
								= getManagedBean().getRecommendedSupplierList();
				if (recommendedItemSupplierList == null) {
					recommendedItemSupplierList = new ArrayList<ScQtEvaluationResultDtl>();
				} else {
					recommendedItemSupplierList.clear();
				}
				
				this.getManagedBean().getSelectedItemList().clear();
				this.getManagedBean().getSelectedItemList().add(scQtRecommend);
				
				List<ScQtEvaluationResultDtl> shortListedSupplierList 
								= scQtRecommend.getShortListedSupplierList();
				if (CollectionUtils.isNotEmpty(shortListedSupplierList)) {
					for (ScQtEvaluationResultDtl evalResultDtl 
											: shortListedSupplierList) {
						String suppId = String.valueOf(evalResultDtl
								.getQtEvaluationResult().getScQtProposal()
								.getScQtSupplier().getSmSupplier().getSupplierId());
						if (suppId.equals(awardSupplierStr)) {
							recommendedItemSupplierList.add(evalResultDtl);
							this.getManagedBean().setRecommendedSupplierList(
											recommendedItemSupplierList);
							evalResultDtl.setAwareded(isAwarded);
							evalResultDtl.setShortListed(isShortlisted);
							this.getManagedBean().setRecommendedSupplier(null);
							
							// If finalization method is award, remove
							// the previously recommended supplier details
							if (isAwarded) {
								prepareToDeleteRecommendDtls(scQtRecommend);
							}
							addSelectedSupplier();
							
							break;
						}
					}
				}
			}
			if (removableItem != null) {
				selectedItemSet.remove(removableItem);
			}

			this.getManagedBean().setShortlistedSupplierList(null);
			this.getManagedBean().setRecommendedSupplierList(null);
			this.getManagedBean().setSelectedItemList(null);
			this.getManagedBean().setAwardSupplierList(null);
			this.getManagedBean().setAwardSupplier(null);
		} else {
			log.info("There are no selected items");
			return;
		}
	}

	private void validateByPackageByZone(ScQtRecommend scQtRecommend) {
		boolean supplierSelected = true;
		boolean minSuppliersExist = true;
		boolean isQtRefinalized = isQTRefinalized();
		TplSpecZoneDtl zoneDtl = scQtRecommend.getTplSpecZoneDtl();
		PmParameter finalizationMethod = getManagedBean().getScQtFinalization()
											.getFinalizationMethod();
		if (finalizationMethod == null) {
			setZoneAndTabIndexOnException(zoneDtl);
			throw new EpException(MessageConst.ERROR_760211);
		}
		boolean isAward = ParameterConst.RECOMMEND_BY_AWARD
								.equals(finalizationMethod.getParameterCode());
		if (isQtRefinalized) {
			isAward = true;
		}
		
		int activeSupplierCount = 0;
		if (CollectionUtils.isNotEmpty(
				scQtRecommend.getRecommendDtls())) {
			for (ScQtRecommendDtl recommendDtl 
						: scQtRecommend.getRecommendDtls()) {
				if (recommendDtl.getRecordStatusFlg().equals(
								DbValueConst.RECORD_STATUS_ACTIVE)) {
					++activeSupplierCount;
					if (isAward && activeSupplierCount == 1) {
						break;
					} else if (!isAward && activeSupplierCount == 2) {
						break;
					}
				}
			}
		}
		
		if (isAward && activeSupplierCount == 0) {
			supplierSelected = false;
		} else if (!isAward && 
					 	activeSupplierCount < 2) {
			supplierSelected = false;
			minSuppliersExist = false;
		}	
		
		if (!supplierSelected) {
			String recommendByDesc = getParameterDesc(finalizationMethod);
			setZoneAndTabIndexOnException(zoneDtl);
			
			if (!minSuppliersExist) {
				throw new EpException(MessageConst.ERROR_760243,
						new String[] { recommendByDesc });
			} else {
				throw new EpException(MessageConst.ERROR_760206);
			}
		}		
	}

	private void validateAwardOfItem(ScQtRecommend scQtRecommend) {
		boolean isSubmit = this.getManagedBean().isSubmit();
//		boolean isPriceTypeTier = isItemPriceTypeTariffOrTier(scQtRecommend);
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		PmParameter finalizationMethod = scQtFinalization.getFinalizationMethod();	
		String defaultRecommendByStr = ParameterConst.RECOMMEND_BY_AWARD;
//		if (isPriceTypeTier) {
//			defaultRecommendByStr = ParameterConst.RECOMMEND_BY_AWARD;
//		}
													   
//		if ((scQtRecommend.isHigherAuthReq() && 
//				getManagedBean().isSecretaryUser() && 
//					!getManagedBean().isForwardToBpk()) || !isSubmit) {
//			if (finalizationMethod == null) {
//				scQtFinalization.setFinalizationMethod(
//						getPmParameterByTypeAndCode(
//								ParameterTypeConst.FINALIZATION_RECOMMEND_BY,
//								defaultRecommendByStr)
//						);
//			}
//			return;
//		}
		if (finalizationMethod == null) {
			if (isSubmit) {
				setZoneAndTabIndexOnException(scQtRecommend.getTplSpecZoneDtl());
				throw new EpException(MessageConst.ERROR_760211);
			} else {
				
				// By default set finalization method to Award 
				// if Supplier Finalization method is not selected
				scQtFinalization.setFinalizationMethod(
						getPmParameterByTypeAndCode(ParameterTypeConst.FINALIZATION_RECOMMEND_BY,
						defaultRecommendByStr)
							);	
				return;
			}
		}
		String finalizationMethodStr = null;
		if (isQTRefinalized()) {
			finalizationMethodStr = ParameterConst.RECOMMEND_BY_AWARD;
		} else {
			finalizationMethodStr = finalizationMethod.getParameterCode();
		}
		String recommendByDesc = getParameterDesc(finalizationMethod);
		String itemName = scQtRecommend.getRequestItem().getItemName();
		List<ScQtRecommendDtl> recommendDtlList = scQtRecommend
				.getRecommendDtls();
		
		int activeSupplCount = 0;
		if (CollectionUtils.isNotEmpty(recommendDtlList)) {
			for (ScQtRecommendDtl dtl : recommendDtlList) {
				if (dtl.getRecordStatusFlg().equals(
						DbValueConst.RECORD_STATUS_ACTIVE)) {
					++activeSupplCount;
					if (activeSupplCount == 2) {
						break;
					}
				}
			}
		}		
		if ((StringUtils.equals(ParameterConst.RECOMMEND_BY_AWARD,
						finalizationMethodStr) && activeSupplCount != 1)) {
			setZoneAndTabIndexOnException(scQtRecommend.getTplSpecZoneDtl());
			throw new EpException(MessageConst.ERROR_760213);
		}
		
		// TODO : Remove later [Excluded Multiple award option]
//		if (StringUtils.equals(ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD,
//				finalizationMethodStr)) {
//			if (activeSupplCount < 2) {
//				setZoneAndTabIndexOnException(scQtRecommend.getTplSpecZoneDtl());
//				throw new EpException(MessageConst.ERROR_760223, new String[] {
//						itemName, recommendByDesc });
//			} else {
//				BigDecimal totalQuantity = scQtRecommend.getRequestItem()
//						.getQuantity();
//				BigDecimal tempQuantity = new BigDecimal(0.0);
//				for (ScQtRecommendDtl recommendDtl : recommendDtlList) {
//					if (recommendDtl.getRecordStatusFlg().equals(
//							DbValueConst.RECORD_STATUS_ACTIVE)) {
//						BigDecimal quantityAwarded = recommendDtl.getAwardQty();
//						if (quantityAwarded == null
//								|| quantityAwarded.compareTo(totalQuantity) >= 0) {
//							setZoneAndTabIndexOnException(scQtRecommend
//									.getTplSpecZoneDtl());
//							throw new EpException(MessageConst.ERROR_760214);
//						}
//						tempQuantity = tempQuantity.add(quantityAwarded);
//					}
//				}
//				if (totalQuantity.compareTo(tempQuantity) != 0) {
//					setZoneAndTabIndexOnException(scQtRecommend
//							.getTplSpecZoneDtl());
//					throw new EpException(MessageConst.ERROR_760214);
//				}
//			}
//		}
		if (ParameterConst.RECOMMEND_BY_ONLINE_BIDDING
					.equals(finalizationMethodStr)
				|| ParameterConst.RECOMMEND_BY_MULTIPLE_LOI
						.equals(finalizationMethodStr)) {
			if (activeSupplCount < 2) {
				setZoneAndTabIndexOnException(scQtRecommend.getTplSpecZoneDtl());
				throw new EpException(MessageConst.ERROR_760223, new String[] {
						itemName, recommendByDesc });
			}
		}

	}

	/**
	 * Event to set the awarded supplier in managed bean associated with the
	 * current user session (where recommend type is Itemized)
	 */
	public void selectSupplierDropDownEvent() {
		debugLogger("selectSupplierDropDownEvent");

		String awardSupplier = getManagedBean().getAwardSupplier();
		getManagedBean().setAwardSupplier(awardSupplier);
	}

	private void setZoneAndTabIndexOnException(TplSpecZoneDtl zoneDtl) {
		navigateToSupplierFinalizationTab();
		List<TplSpecZoneDtl> specZoneDtls 
					= this.getManagedBean().getZoneDetails();
		if (zoneDtl != null && 
				CollectionUtils.isNotEmpty(specZoneDtls)) {
			zoneDtl = getManagedBean().getZoneMap().get(zoneDtl.getSpecZoneId());
			this.getManagedBean().setZoneActiveIndex(
					zoneDtl.getZoneTabIndex());
			onZoneTabChangeAction(zoneDtl.getZoneTabIndex());
		}
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
	
	private void hasTierPriceInMixtureOfItems(ScQtFinalization scQtFinalization) {
		boolean isPriceTypeTier = false;
		
		if (CollectionUtils.isNotEmpty(
				scQtFinalization.getScQtRecommends())) {
			for (ScQtRecommend scQtRecommend 
						: scQtFinalization.getScQtRecommends()) {
				isPriceTypeTier = isItemPriceTypeTariffOrTier(scQtRecommend);
				
				if (isPriceTypeTier) {
					this.getManagedBean().setHasItemWithPriceTypeTier(true);
					break;
				}
			}
		}
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
	 * @return the currentTab
	 */
	public int getCurrentTab() {
		return currentTab;
	}

	/**
	 * @param currentTab
	 *            the currentTab to set
	 */
	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	/**
	 * Update supplier finalization details, digital signature of the signed off
	 * user and send the task to BPM
	 * 
	 * @param digiSign
	 *            the digiSign to set
	 */
	public void gpkiSignAction(PmDigiSign digiSign) {
		updateBPMTask(this.getManagedBean().getAction(), digiSign, null);
	}

	/**
	 * @return the textBeforeSign
	 */
	public String getTextBeforeSign() {
		StringBuilder sb = new StringBuilder();
		if (getManagedBean().getScQt() != null) {
			sb.append(getManagedBean().getScQt().getQtNo());
		}
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		textBeforeSign = sb.toString();
		return textBeforeSign;
	}

	/**
	 * @param textBeforeSign
	 *            the textBeforeSign to set
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}
	
	/**
	 * Gets the role code.
	 *
	 * @return the role code
	 */
	private String getCommitteeRoleCode(){
		String committeeRoleCode = null;
		FacesScopes facesScopes = new FacesScopes();
		PmUser curLogedInUser = facesScopes.getUserSessionBean();
		String committeeType = this.getManagedBean()
				.getCommitteeType();
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
		committeReqDTO.setUser(curLogedInUser);
		committeReqDTO.setCommitteeType(committeeType);
		CommitteResDTO CommitteeresDTO = committeSO
				.retrieveCommitteMemberByUserId(committeReqDTO);
		ScCommitteeMember member = CommitteeresDTO.getCommMember();
		String roleCodeChar = member.getMemberRole().getParameterCode();
		
		if (roleCodeChar.equals(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
			if (committeeType.equals(
						ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A)) {
				committeeRoleCode = RoleCode.QCA_SECRETARY.value();
			} else if (committeeType.equals(
						ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B)) {
				committeeRoleCode = RoleCode.QCB_SECRETARY.value();
			} else if (committeeType.equals(
						ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)) {
				committeeRoleCode = RoleCode.PBA_SECRETARY.value();
			} else if (committeeType.equals(
						ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)) {
				committeeRoleCode = RoleCode.PBB_SECRETARY.value();
			} else if (committeeType.equals(
						ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) || 
							committeeType.equals(
								ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
				committeeRoleCode = RoleCode.FPB_SECRETARY.value();
			}
		} 
		
		return committeeRoleCode;
	}

	
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
	 * Inner class FinalizationEndorsementReportDTO for 
	 * Finalization Endorsement Report.
	 */
	public class FinalizationEndorsementReportDTO {
		private List<ScQtEvaluationResultDtl> recommendedSuppliers;
		private List<ScQtRecommend> recommendedItems;
		private boolean empty;
		
		public FinalizationEndorsementReportDTO() {}
		
		/**
		 * @return the recommendedSuppliers
		 */
		public List<ScQtEvaluationResultDtl> getRecommendedSuppliers() {
			return recommendedSuppliers;
		}

		/**
		 * @param recommendedSuppliers
		 *            the recommendedSuppliers to set
		 */
		public void setRecommendedSuppliers(
				List<ScQtEvaluationResultDtl> recommendedSuppliers) {
			this.recommendedSuppliers = recommendedSuppliers;
		}

		/**
		 * @return the recommendedItems
		 */
		public List<ScQtRecommend> getRecommendedItems() {
			return recommendedItems;
		}

		/**
		 * @param recommendedItems
		 *            the recommendedItems to set
		 */
		public void setRecommendedItems(
						List<ScQtRecommend> recommendedItems) {
			this.recommendedItems = recommendedItems;
		}

		/**
		 * @return the empty
		 */
		public boolean isEmpty() {
			return empty;
		}

		/**
		 * @param empty
		 *            the empty to set
		 */
		public void setEmpty(boolean empty) {
			this.empty = empty;
		}
	}	
	
}