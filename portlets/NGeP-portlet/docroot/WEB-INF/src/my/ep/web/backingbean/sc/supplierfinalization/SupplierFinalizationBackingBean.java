/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.supplierfinalization;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.bean.sc.QtEvaluationResultDtlBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationEndorsementRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalAmount;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalItem;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.key.sc.supplierfinalization.MapKey;
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
	
	private static final Long DEFAULT_ITEM = 0L;
	private static final Long DEFAULT_ZONE = 0L;
	
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
		
		FacesContext fc = FacesContext.getCurrentInstance();
		
		try {
			SCEvaluationDataType dataType = getBpmTaskDetails(SCEvaluationDataType.class, null);
			Long qtId = Long.valueOf(dataType.getQtId());
			getManagedBean().setPayLoad(dataType);
			getManagedBean().setActiveIndex(0);
			
			Long loggedInUserId = getFacesScopes().getUserSessionBean()
									.getUserId();
			getManagedBean().setLoggedInUserId(loggedInUserId);

			loadInitialData(qtId);

			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							QTConstants.FC_SUPPLIER_FINALIZATION_PAGE);
		} catch (Exception e) {
			handleException(e);
			fc.getApplication().getNavigationHandler()
			  .handleNavigation(fc, null,
					CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);
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
			checkIfLoggedinUserIsSecretary();
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
			getManagedBean().setCommEndorsementTabIndex(tabIndex);		
		}
		
		// Dynamically creating main tab menu
		createMainMenuModel();
	}
	
	/**
	 * Loads ScQt details corresponding to the selected quotation/tender and
	 * sets the data in the managed bean associated with the current user
	 * session
	 * 
	 * @param qtId
	 *            The id of the quotation/tender to be loaded
	 */
	private void retrieveQTDetails(Long qtId) {
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
			getManagedBean().setOriginalFinalizationSecretaryUserId(
						scQt.getFinalizationSecratery().getUserId());

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
		Integer selectedTabIndex = null; 
		String selectedTabIndexStr = FacesContext
				.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("selectedTabIndex");
		
		if (StringUtils.isNotBlank(selectedTabIndexStr)) {
			selectedTabIndex = Integer.valueOf(selectedTabIndexStr);
		} else {
			selectedTabIndex = getManagedBean().getActiveIndex();
		}
		
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
				navigateToCommitteeEndorsementStatusTab();
				break;
		}
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
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		String decisionTypeCode = scQtFinalization.getDecision()
												  .getParameterCode();
		if (decisionTypeCode.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER) ||
				  decisionTypeCode.equals(ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY)) {
			retrieveSupplierDetailsByRecommendType(scQtFinalization);
			hasTierPriceInMixtureOfItems(scQtFinalization);
		}
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
		
//		if (this.getManagedBean().isItemize()) {
//			this.getManagedBean().setShortlistedSupplierList(null);
//		}

		initializeCommitteeRemarks();
		initializeFinalizationRemarks();
		initializeFinalizationEndorsementRemarks();
		
		if (this.getManagedBean().getResetSupplierList() == null) {
			this.getManagedBean().setResetSupplierList(
					new HashSet<ScQtEvaluationResultDtl>());
		}		

	}
	
	private void initializeCommitteeRemarks() {
		ScQtCommittee scQtCommittee = this.getManagedBean().getScQtCommittee();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		if (scQtCommittee != null) {
			// Retrieve committee remarks
			List<ScQtCommitteRemarks> remarks = initializeRemarks(scQtCommittee,
					scQtFinalization.getCurrentStatus(), 
					scQtCommittee.getQtCommitteRemarks(),
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
				scQtFinalization.getCurrentStatus(), 
				scQtFinalization.getScQtFinalizationRemarks(), 
				ScQtFinalizationRemarks.class);
		scQtFinalization.setScQtFinalizationRemarks(remarks);

		// Finalization Remarks list
		getManagedBean().setFinalizationRemarks(remarks);		
	}
	
	private void initializeFinalizationEndorsementRemarks() {
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();

		// Retrieve finalization endorsement remarks
		List<ScQtFinalizationEndorsementRemarks> remarks = initializeRemarks(scQtFinalization,
				scQtFinalization.getCurrentStatus(), 
				scQtFinalization.getScQtFinalizationEndorsementRemarks(), 
				ScQtFinalizationEndorsementRemarks.class);
		scQtFinalization.setScQtFinalizationEndorsementRemarks(remarks);

		// Finalization Endorsement Remarks list
		getManagedBean().setFinalizationEndorsementRemarks(remarks);		
	}		

	private void navigateToCommitteeEndorsementStatusTab() {
		this.getManagedBean().setActiveIndex(
				this.getManagedBean().getCommEndorsementTabIndex());
		retrieveEndorsementCommitteeDetails();
	}
	
	public void navigateToEndorsementReportTab() {
		this.getManagedBean().setActiveIndex(
				this.getManagedBean().getEndorsementReportActiveIndex());
		try {
			getSuppFinalizationEndorsementReportStreamContent();
		} catch (Exception e) {
			log.error(e);
		}
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
			meeting.setMeetingStatus(getPmStatusByStatusId(QTStatusConst.MS_PENDING));

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
			if(null==selectedMeetingAttdList){
				selectedMeetingAttdList=new ArrayList<ScQtMeetingAttendance>();
			}
			List<ScQtMeetingAttendance> meetingAttendanceList 
					= getManagedBean().getMeetingAttendanceList();
			if (CollectionUtils.isNotEmpty(
					meetingAttendanceList)) {
				List<ScQtMeetingAttendance> deletedMeetingAttendanceList 
						= this.getManagedBean().getDeletedMeetingAttendanceList();
				if (deletedMeetingAttendanceList == null) {
					deletedMeetingAttendanceList 
							= new ArrayList<ScQtMeetingAttendance>();
					this.getManagedBean().setDeletedMeetingAttendanceList(
							deletedMeetingAttendanceList);			
				}
				for (ScQtMeetingAttendance meetingAttendance
									: meetingAttendanceList) {
					if(meetingAttendance.isSelected()){
						selectedMeetingAttdList.add(meetingAttendance);
					if (!meetingAttendance.isBrandNew()) {
						meetingAttendance.setDeleted(true);
						deletedMeetingAttendanceList
								.add(meetingAttendance);
					}
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
		scQtMeeting.setMeetingStatus(getPmStatusByStatusId(scQtMeeting.getMeetingStatusStr()));
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

		List<ScQtMeetingAttendance> meetingAttendanceList = 
					getManagedBean().getMeetingAttendanceList();
		if (null == meetingAttendanceList) {
			meetingAttendanceList = new ArrayList<ScQtMeetingAttendance>();
			this.getManagedBean().setMeetingAttendanceList(
									 meetingAttendanceList);
		}

		ScQtMeetingAttendance meetingAttendance = new ScQtMeetingAttendance();
		meetingAttendance.setQtCommittee(
				this.getManagedBean().getScQtCommittee());
		setUserDetails(meetingAttendance);
		meetingAttendanceList.add(meetingAttendance);
	}
	
	/**
	 * Event to handle selection of all committee meeting attendance record(s)
	 */
	public void selectAllMeetingAttendeesEvent() {
		boolean isSelectAllMeetingAttendees = 
					getManagedBean().isSelectAllMeetingAttendees();
		List<ScQtMeetingAttendance> meetingAttendanceList = 
				getManagedBean().getMeetingAttendanceList();
		if (CollectionUtils.isNotEmpty(meetingAttendanceList)) {
			for (ScQtMeetingAttendance meetingAttd 
					: meetingAttendanceList) {
				if (meetingAttd.getRecordStatusFlg() == 
							DbValueConst.RECORD_STATUS_ACTIVE) {
					meetingAttd.setSelected(isSelectAllMeetingAttendees);
					selectMeetingAttendeeEvent(meetingAttd);
				}
			}
		}
	}
	
	/**
	 * Event to handle selection of single committee meeting attendance record
	 */	
	public void selectMeetingAttendeeEvent(
						ScQtMeetingAttendance meetingAttendance) {
		List<ScQtMeetingAttendance> selectedMeetingAttdList = 
					getManagedBean().getSelectedMeetingAttList();
		if (selectedMeetingAttdList == null) {
			selectedMeetingAttdList = new ArrayList<ScQtMeetingAttendance>();
			getManagedBean().setSelectedMeetingAttList(selectedMeetingAttdList);
		}
		if (meetingAttendance.isSelected()) {
			selectedMeetingAttdList.add(meetingAttendance);
		} else {
			selectedMeetingAttdList.remove(meetingAttendance);
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
		
		Long qtId = getManagedBean().getScQt().getQtId();
		Long selectedZoneId = null;
		Long selectedItemId = null;
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
						 
						 if (getManagedBean().isItemRowSelect() && 
								 getManagedBean().getSelectedItem() != null) {
							selectedItemId = getManagedBean().getSelectedItem()
										.getRequestItem().getRequestNoteItemId();
						 } else {
							 getManagedBean().setQualifiedSuppliersByEvalStage(null);
							 log.debug("None of the item(s) has been row selected");
							 return;
						 }
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
		
		getManagedBean().setQualifiedSuppliersByEvalStage(
				new QualifiedSuppliersPagingDataModel(
						"evaluationQualifiedSuppliers", qtId,
						selectedZoneId, selectedItemId, evaluationStage));
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
			validateFinalizationReports(reqDTO);
						
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
			String desc = ResourceBundleUtils.getMessage(
					"lbl.sc.sf.supplier.finalization", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
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
	
	private void validateFinalizationReports(SuppFinalizationReqDTO reqDTO) {
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		List<ScQtFinalizationReport> finalizationReports = 
					scQtFinalization.getScQtFinalizationReports();
		
		List<ScQtFinalizationReport> toBeRemovedReports 
					= new ArrayList<ScQtFinalizationReport>();
		if (CollectionUtils.isNotEmpty(finalizationReports)) {
			for (ScQtFinalizationReport report 
							: finalizationReports) {
				if (ParameterConst.DOCUMENT_CATEGORY_ENDORSEMENT_REPORT
						.equals(report.getDocCategory()) && 
						(report.getScQtFinalizationReportAttachment() == null ||
							report.getScQtFinalizationReportAttachment().getFileName() == null)) {
					
					// Remove report for save action
					if (reqDTO != null) {
						report.setDeleted(true);
						toBeRemovedReports.add(report);
					} else {
						navigateToBriefingPaperTab();
						throw new EpException(MessageConst.ERROR_760386);
					}
				}
			}
			
			if (toBeRemovedReports.size() > 0) {
				finalizationReports.removeAll(
							toBeRemovedReports);
				reqDTO.setRemovedFinalizationReports(
								   toBeRemovedReports);
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
//			boolean isZonal = getManagedBean().getScQt().isZonal();
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
						
						processSuppliersBeforeSaving();
						
						if (!getManagedBean().isItemize()) {
							validateByPackage();
						} else {
							validateByItem();
						}
						
						List<QtEvaluationResultDtlBean> cachedSupplierBeans = null;
						Map<MapKey,List<QtEvaluationResultDtlBean>> cachedSuppliersMap = 
												getManagedBean().getQualifiedSuppliersCache();
						
						if (MapUtils.isNotEmpty(cachedSuppliersMap)) {
							cachedSupplierBeans = new ArrayList<QtEvaluationResultDtlBean>();
							
							for (MapKey mapKey : cachedSuppliersMap.keySet()) {
								if (getManagedBean().isItemize() && 
										!mapKey.getItemId().equals(DEFAULT_ITEM)) {
									cachedSupplierBeans.addAll(
											cachedSuppliersMap.get(mapKey));
								} else if (!getManagedBean().isItemize() && 
												mapKey.getItemId().equals(DEFAULT_ITEM)) {
									cachedSupplierBeans.addAll(
											cachedSuppliersMap.get(mapKey));									
								}
							}
						}
						reqDTO.setCachedSupplierBeans(cachedSupplierBeans);		
						
					} else {
						resetItemDetails();
					}
	
					ScQtCommittee _scQtCommittee = this.getManagedBean()
							.getScQtCommittee();
					if (_scQtCommittee != null) {
						List<ScQtCommitteRemarks> remarksList = finalizeRemarks(this
								.getManagedBean().getCommitteeRemarkList());
						_scQtCommittee.setQtCommitteRemarks(remarksList);
						setUserDetails(_scQtCommittee);
					}
					
					List<ScQtFinalizationRemarks> finalizationRemarks = 
							finalizeRemarks(getManagedBean().getFinalizationRemarks());
					scQtFinalization.setScQtFinalizationRemarks(finalizationRemarks);
					
					List<ScQtFinalizationEndorsementRemarks> finalizationEndorsementRemarks = 
							finalizeRemarks(getManagedBean().getFinalizationEndorsementRemarks());
					scQtFinalization.setScQtFinalizationEndorsementRemarks(
											 finalizationEndorsementRemarks);
					setUserDetails(scQtFinalization);
	
					reqDTO.setScQtCommittee(_scQtCommittee);
					reqDTO.setScQtFinalization(scQtFinalization);
//					reqDTO.setResetSuppliers(
//							this.getManagedBean().getResetSupplierList());
					
					break;
	
				default :
					reqDTO.setScQtFinalization(scQtFinalization);
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
		SCEvaluationDataType dataType = (SCEvaluationDataType) 
										 getManagedBean().getPayLoad();
		Long currentStatusId = getManagedBean().getCurrentStatusId();
		TplSpecZoneDtl selectedZone = getManagedBean().getCurrentZone();
		boolean isSecretaryUser = this.getManagedBean().isSecretaryUser();
		Boolean isInitialSelectionItemized = 
				this.getManagedBean().isInitialSelectionItemized();

		renewSesionObject();

		getManagedBean().setCommitteeType(committeeType);
		getManagedBean().setScQtCommittee(scQtCommittee);
		getManagedBean().setCurrentZone(selectedZone);
		getManagedBean().setPayLoad(dataType);
		getManagedBean().setCurrentStatusId(currentStatusId);
		getManagedBean().setActiveIndex(activeIndex);
		getManagedBean().setZoneActiveIndex(
				selectedZone != null ? selectedZone.getZoneTabIndex() : 0);
		getManagedBean().setSecretaryUser(isSecretaryUser);
		getManagedBean().setInitialSelectionItemized(
						   isInitialSelectionItemized);

		loadInitialData(qtId);

		onTabMenuChangeAction();
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
               List<ScQtMeeting> localMeetingList = new ArrayList<ScQtMeeting>();
				
				for (ScQtMeeting meeting : meetingList) {
					if(null!=meeting.getMeetingDate()){
					compareMeetingDates(meeting, localMeetingList);
					}else{
						throw new EpException(MessageConst.ERROR_760418);
					}
				}
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
			resetDecisionRecommendSupplier(scQtFinalization);
			resetReEvaluate(scQtFinalization);
			resetRepublish(scQtFinalization);
			resetCancel(scQtFinalization);
			resetDecisionForwardToHigherAuthority(scQtFinalization);

		} else if (decision.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)) {
			resetReEvaluate(scQtFinalization);
			resetRepublish(scQtFinalization);
			resetCancel(scQtFinalization);
			if (!this.getManagedBean().isInitialDecisionFwdHighAuth()) {
				resetDecisionForwardToHigherAuthority(scQtFinalization);
			}
		} else if (decision
				.equals(ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY)) {
			resetReEvaluate(scQtFinalization);
			resetRepublish(scQtFinalization);
			resetCancel(scQtFinalization);

		} else if (decision.equals(ParameterConst.DECISION_RE_EVALUATION)) {
			resetDecisionRecommendSupplier(scQtFinalization);
			resetRepublish(scQtFinalization);
			resetCancel(scQtFinalization);
			resetDecisionForwardToHigherAuthority(scQtFinalization);

		} else if (decision.equals(ParameterConst.DECISION_RE_PUBLISH)) {
			resetDecisionRecommendSupplier(scQtFinalization);
			resetReEvaluate(scQtFinalization);
			resetCancel(scQtFinalization);
			resetDecisionForwardToHigherAuthority(scQtFinalization);

		} else if (decision.equals(ParameterConst.DECISION_CANCEL)) {
			resetDecisionRecommendSupplier(scQtFinalization);
			resetReEvaluate(scQtFinalization);
			resetRepublish(scQtFinalization);
			resetDecisionForwardToHigherAuthority(scQtFinalization);
		}
		return scQtFinalization;
	}

	private void saveByPackage(ScQtFinalization scQtFinalization) {
//		if (StringUtils.isBlank(
//				getManagedBean().getFinalizationMethod())) {
//			setZoneAndTabIndexOnException(null);
//			throw new EpException(MessageConst.ERROR_760211);
//		}
//		
//		List<ScQtRecommend> scQtRecommendList = new ArrayList<ScQtRecommend>();
//		scQtRecommendList.addAll(scQtFinalization.getScQtRecommends());
//		ScQtEvaluationResultDtl recommendedSupplier 
//					= this.getManagedBean().getRecommendedSupplier();
		
//		if (recommendedSupplier != null) {
//			recommendedSupplier.setAwareded(true);
//			recommendedSupplier.setShortListed(false);

			// Award
//			for (ScQtRecommend scQtRecommend : scQtRecommendList) {
//				scQtRecommend.setShortListedSupplierList(
//						this.getManagedBean().getShortlistedSupplierList());
//				if (scQtRecommend.getRecommendDtls() == null) {
//					scQtRecommend.setRecommendDtls(
//							new ArrayList<ScQtRecommendDtl>());
//				}
//				setItemHigherAuthorityReqStatus(scQtRecommend);
//				resetShortlistedSupplierData(
//						getManagedBean().getShortlistedSupplierList(), recommendedSupplier);
//				Set<ScQtRecommendDtl> toBeAddedSet = new HashSet<ScQtRecommendDtl>();
//				List<ScQtEvaluationResultDtl> recommendedSuppliers 
//								= new ArrayList<ScQtEvaluationResultDtl>();
//				recommendedSuppliers.add(recommendedSupplier);
//				filterRecommendedSuppliersList(toBeAddedSet, recommendedSuppliers, scQtRecommend);
//			}
//		} else {
//			// multiple LOI or Online Bidding
//
//			// to set the recommend details for each recommend
//			for (ScQtRecommend scQtRecommend : scQtRecommendList) {
//				scQtRecommend.setShortListedSupplierList(
//						this.getManagedBean().getShortlistedSupplierList());
//				
//				if (scQtRecommend.getRecommendDtls() == null) {
//					scQtRecommend.setRecommendDtls(
//							new ArrayList<ScQtRecommendDtl>());
//				}
//				setItemHigherAuthorityReqStatus(scQtRecommend);		
//				List<ScQtEvaluationResultDtl> recommendedSupplierList 
//							= getManagedBean().getRecommendedSupplierList();
//				if (CollectionUtils.isNotEmpty(recommendedSupplierList)) {
//					Set<ScQtRecommendDtl> toBeAddedSet = new HashSet<ScQtRecommendDtl>();
//					filterRecommendedSuppliersList(toBeAddedSet, 
//								recommendedSupplierList, scQtRecommend);
//				} else if (CollectionUtils.isNotEmpty(
//								scQtRecommend.getRecommendDtls())) {
//					if (scQtRecommend.getRemoveSupplierList() == null) {
//						scQtRecommend.setRemoveSupplierList(
//								new ArrayList<ScQtRecommendDtl>());
//					}
//					
//					Set<ScQtRecommendDtl> toBeAddedSet = new HashSet<ScQtRecommendDtl>();
//					for (ScQtRecommendDtl recommendDtl 
//								: scQtRecommend.getRecommendDtls()) {
//						if (!recommendDtl.getRecordStatusFlg()
//								.equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
//							toBeAddedSet.add(recommendDtl);
//						}	
//						
//						if (!recommendDtl.isBrandNew()
//								&& !toBeAddedSet.contains(recommendDtl)) {
//							recommendDtl.setDeleted(true);
//							scQtRecommend.getRemoveSupplierList().add(
//									recommendDtl);
//						}
//					}
//					scQtRecommend.getRecommendDtls().clear();
//					scQtRecommend.getRecommendDtls().addAll(toBeAddedSet);
//				}
//			}
//		}
//		scQtFinalization.setScQtRecommends(scQtRecommendList);
	}
	
	private void setItemHigherAuthorityReqStatus(ScQtRecommend scQtRecommend) {
		Long loggedInUserId = getFacesScopes().getUserSessionBean()
									  		  .getUserId();
		if (loggedInUserId.equals(
				getManagedBean().getOriginalFinalizationSecretaryUserId())) {
			if (!this.getManagedBean().isForwardToHigherAuthority() ||  
					!this.getManagedBean().isItemize()) {
				scQtRecommend.setHigherAuthReq(false);
			}
		}
	}
	
	private void validateByPackage() {
		boolean toBeValidated = false;
		boolean isSubmit = this.getManagedBean().isSubmit();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		String decisionTypeCode = scQtFinalization.getDecision().getParameterCode();

		if ( (ParameterConst.DECISION_RECOMMEND_SUPPLIER
					.equals(decisionTypeCode) || 
				ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY
						.equals(decisionTypeCode) ) && isSubmit) {
			toBeValidated = true;
		}
		
		PmParameter finalizationMethod = scQtFinalization.getFinalizationMethod();
		TplSpecZoneDtl currentZone = this.getManagedBean().getCurrentZone();

		if (finalizationMethod == null) {
			setZoneAndTabIndexOnException(currentZone);
			throw new EpException(MessageConst.ERROR_760211);
		}		
		
		if (toBeValidated) {
			int cancelledItemsCount = 0;
			for (ScQtRecommend scQtRecommend : 
						scQtFinalization.getScQtRecommends()) {
				
				if (scQtRecommend.isCancelled()) {
					++cancelledItemsCount;
					continue;
				}
				if (!scQtRecommend.isAwardedItem()) {
					validateByPackage(scQtRecommend);
				}
			}
			
			if (cancelledItemsCount == 
					scQtFinalization.getScQtRecommends().size()) {
				throw new EpException(MessageConst.ERROR_760341);
			}
		}
	}	
	
	private boolean filterRecommendedSuppliersList(Set<ScQtRecommendDtl> toBeAddedSet, 
							          List<QtEvaluationResultDtlBean> cachedSuppliers, 
							 						      ScQtRecommend scQtRecommend) {
		
		boolean isSingleAward = this.getManagedBean().isSingleAward();
		boolean hasRecommendedSuppliers = false;
		ScQtRecommendDtl selectedRecommendDtl = null;
		List<ScQtRecommendDtl> removedList = scQtRecommend.getRemoveSupplierList();

		for (QtEvaluationResultDtlBean recommendedSupplier 
									     : cachedSuppliers) {
			if (isSingleAward) {
				recommendedSupplier.setShortlisted(false);
			} else {
				recommendedSupplier.setAwarded(false);
			}
			if (recommendedSupplier.isAwarded() || 
					recommendedSupplier.isShortlisted()) {
				hasRecommendedSuppliers = true;
				boolean alreadyExists = false;
				
				if (CollectionUtils.isNotEmpty(
						scQtRecommend.getRecommendDtls())) {
					Long recommendedSupplierId = 
							recommendedSupplier.getSmSupplier().getSupplierId();
					
					for (ScQtRecommendDtl detail : 
									scQtRecommend.getRecommendDtls()) {
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
							detail.setScQtProposalAmount(
									recommendedSupplier.getScQtProposalAmount());
							detail.setAwardQty(scQtRecommend.getOrderQty());
							if(recommendedSupplier.getScQtProposalItem() != null){
								detail.setScQtProposalItem(
										recommendedSupplier.getScQtProposalItem());
							}
							toBeAddedSet.add(detail);
							
							break;
						}
					}
				}
				if (!alreadyExists) {
					selectedRecommendDtl = createOrRestoreRecommendDtl(
							recommendedSupplier, scQtRecommend, removedList);
					setUserDetails(selectedRecommendDtl);
					toBeAddedSet.add(selectedRecommendDtl);
				}
			}
		}
		
		if (scQtRecommend.getRemoveSupplierList() == null) {
			scQtRecommend.setRemoveSupplierList(
					new ArrayList<ScQtRecommendDtl>());
		}
		
		processAndSetDeletedRecommendDtls(toBeAddedSet, scQtRecommend, false);
		scQtRecommend.getRecommendDtls().clear();
		scQtRecommend.getRecommendDtls().addAll(toBeAddedSet);
		
		return hasRecommendedSuppliers;
	}
	
	private void processAndSetDeletedRecommendDtls(Set<ScQtRecommendDtl> toBeAddedSet, 
											ScQtRecommend scQtRecommend, boolean isReset) {
		
		Set<ScQtRecommendDtl> removedRecommendDtlSet = new HashSet<ScQtRecommendDtl>();
		for (ScQtRecommendDtl recommendDtl 
								: scQtRecommend.getRecommendDtls()) {
			
			// Skip the deletion of rejected suppliers and 
			// suppliers with LOI or LOA as these records will have child records
			if (!recommendDtl.getRecordStatusFlg().equals(
					DbValueConst.RECORD_STATUS_ACTIVE)
						|| recommendDtl.getLoiLoaCount() > 0) {
				if (isReset && !(recommendDtl.getRecordStatusFlg()
						.equals(DbValueConst.RECORD_STATUS_REJECTED) || 
							recommendDtl.getRecordStatusFlg()
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
	
	private ScQtRecommendDtl createOrRestoreRecommendDtl(
			QtEvaluationResultDtlBean recommendedSupplier,
			ScQtRecommend scQtRecommend, List<ScQtRecommendDtl> removedList) {
		
		ScQtRecommendDtl recommendDtl = null;
		SmSupplier supplier = recommendedSupplier.getSmSupplier();
		if (CollectionUtils.isNotEmpty(removedList)) {
			for (ScQtRecommendDtl removedDetail : removedList) {
				if ((supplier.getSupplierId()).equals(
						removedDetail.getSupplier().getSupplierId())) {
					removedDetail.setDeleted(false);
					removedList.remove(removedDetail);
					removedDetail.setScQtProposalAmount(
							recommendedSupplier.getScQtProposalAmount());
					recommendDtl = removedDetail;
					
					break;
				}
			}
		}
		
		if (recommendDtl == null) {
			recommendDtl = new ScQtRecommendDtl();
			recommendDtl.setSupplier(supplier);
			recommendDtl.setScQtRecommend(scQtRecommend);
			recommendDtl.setScQtProposalAmount(
					recommendedSupplier.getScQtProposalAmount());
		}
		
		ScQtProposalItem qtProposalItem = fetchQtProposalItem(scQtRecommend, 
			 					recommendedSupplier.getScQtProposalAmount());
		recommendDtl.setScQtProposalItem(qtProposalItem);
		recommendDtl.setAwardQty(scQtRecommend.getOrderQty());

		return recommendDtl;
	}
	
	private ScQtProposalItem getQtProposalItem(ScQtRecommend scQtRecommend, 
										 ScQtProposalAmount proposalAmount) {
		ScQtProposalItem qtProposalItem = fetchQtProposalItem(
												scQtRecommend,proposalAmount);
		return qtProposalItem;
	}
	
	private ScQtProposalItem fetchQtProposalItem(ScQtRecommend scQtRecommend, 
										 		 ScQtProposalAmount qtProposalAmount) {
		ScQtProposalItem qtProposalItem = null;
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			if (scQtRecommend.getTplSpecZoneDtl() != null) {
				reqDTO.setSpecZoneId(scQtRecommend.getTplSpecZoneDtl().getSpecZoneId());
			}
			reqDTO.setScQtProposalAmount(qtProposalAmount);
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

	private void validateByItem() {
		int cancelledItemsCount = 0;
		ScQtFinalization scQtFinalization = 
						 getManagedBean().getScQtFinalization();
		String decisionTypeCode = scQtFinalization.getDecision()
									.getParameterCode();
		boolean isSubmit = this.getManagedBean().isSubmit();
		boolean isDecRecommendSupplier = false;
		boolean isDecFwdToHighAuthority = false;
		
		if (ParameterConst.DECISION_RECOMMEND_SUPPLIER
						  .equals(decisionTypeCode)) {
			isDecRecommendSupplier = true;
		} else if (ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY
								 .equals(decisionTypeCode)) {
			isDecFwdToHighAuthority = true;
		}
		
		// to set the recommend details for each recommend
		for (ScQtRecommend scQtRecommend :
					scQtFinalization.getScQtRecommends()) {
			if (scQtRecommend.isCancelled()) {
				++cancelledItemsCount;
			}
			if (!isSubmit) {
				continue;
			}
			
			if (isSubmit && 
					(isDecRecommendSupplier || 
						isDecFwdToHighAuthority)) {
				if (!(scQtRecommend.isAwardedItem() ||
						scQtRecommend.isCancelled() || 
							scQtRecommend.isDisabled())) {
					validateAwardOfItem(scQtRecommend);
				}
			}
		}
		
		if (cancelledItemsCount == 
				scQtFinalization.getScQtRecommends().size()) {
			throw new EpException(MessageConst.ERROR_760341);
		}
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
		if (QTStatusConst.SF_PENDING_SIGN_OFF.equals(currentStatusId)) {
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
			CommitteResDTO committeeResDTO = getCommitteMemberByUserId();
			ScCommitteeMember committeeMember = committeeResDTO.getCommMember();
			if (StringUtils.isNotBlank(committeeType) && 
					ParameterConst.COMMITTEE_ROLE_CHAIRPERSON.equalsIgnoreCase(committeeMember.getMemberRole().getParameterCode()) && 
					isDigiSigningRequired(committeeResDTO.getRoleCode(),
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

		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		String decisionTypeCode = scQtFinalization.getDecision().getParameterCode();
		
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
				
				validateFinalizationReports(null);
				
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

		if (ParameterConst.DECISION_CANCEL
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
			reqDTO.setRoleCode(QTStatusConst.SF_PENDING_ESCALATION_TO_BPK
				       .equals(getManagedBean().getCurrentStatusId()) ? 
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
			String decisionTypeCode = getManagedBean().getScQtFinalization()
										.getDecision().getParameterCode();
			
			// Check if the current committee is authorized to submit
			// based on the total awarded price
			if (getManagedBean().isSubmit() && 
					!getManagedBean().isInitialDecisionFwdToBPK() &&
						isRecommendByAward() && 					
							(ParameterConst.DECISION_RECOMMEND_SUPPLIER
								.equals(decisionTypeCode))) {
				boolean isCurrentCommitteeAuthorisedToSubmit = false;
				String committeeType = getManagedBean().getCommitteeType();
				BigDecimal totalIntendedPrice = calculateTotalIntendedPrice(
													reqDTO.getScQtFinalization());
				ICommitteSO committeeSO = null;
				try {
					committeeSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
		
					CommitteReqDTO commReqDTO = new CommitteReqDTO();
					commReqDTO.setSupplierFinalization(true);
					commReqDTO.setQtId(getManagedBean().getScQt().getQtId());
					commReqDTO.setTotalAwardedPrice(totalIntendedPrice);
					CommitteResDTO commResDTO = committeeSO.retrieveFinalizationCommitteeType(commReqDTO);
					
					if (commResDTO.getHigherAuthoritiesMap() != null) {
						Map<String,String[]> higherAuthoritiesMap = 
												commResDTO.getHigherAuthoritiesMap();
						Set<String> committeeSet = new HashSet<String>();
						for (String committee : 
										higherAuthoritiesMap.keySet()) {
							committee = committee.replace(SourcingConstants.FORWARD_TO_HIGHER_AUTHORITY_DECISION_OTHERS, 
														  StringUtils.EMPTY);
							committee = committee.replace(SourcingConstants.FORWARD_TO_HIGHER_AUTHORITY_DECISION_NON_UNANIMOUS, 
														  StringUtils.EMPTY);
							committeeSet.add(committee);
						}
						
						if (!committeeSet.contains(committeeType)) {
							for (String committee : committeeSet) {
								isCurrentCommitteeAuthorisedToSubmit = 
										isCurrentCommitteeGreaterThanOrEquals(
													 committeeType, committee);
								if (isCurrentCommitteeAuthorisedToSubmit) {
									break;
								}
							}
						} else {
							isCurrentCommitteeAuthorisedToSubmit = true;
						}
					} else {
						isCurrentCommitteeAuthorisedToSubmit = true;
					}
				} finally {
					committeeSO = null;
				}
				
				if (!isCurrentCommitteeAuthorisedToSubmit) {
					String committeeName = getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE, 
															committeeType);
					
					throw new EpException(MessageConst.ERROR_760394, new String[] { 
											committeeName, String.valueOf(totalIntendedPrice.intValue()) });
				}
			}

			Long currentStatusId = getManagedBean().getCurrentStatusId();
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
			
			resDTO = qtSuppFinalizationSO.submitSupplierFinalization(reqDTO);

			if (resDTO.isSuccessful()) {
				String msgCode = null;
				if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
					msgCode = MessageConst.INFO_700067;
				} else {
					msgCode = getMessgeConstant(outcome);
				}
				commonMessageAcknowledmentPage(msgCode,
						ResourceBundleUtils.getMessage(
								"lbl.sc.sf.supplier.finalization", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale()), 
						getManagedBean().getScQt().getQtNo());
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
				 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, SourcingConstants.MY_LOCALE) 
				 			+ SPACE + "(" + totalFinalizationRounds + ")";
					addFinalizationReport(reportTitle);
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
				} else {
					finalizationReports.removeAll(endorsementReports);
					
					// Sort the reports as per the last updated date (in ascending order)
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
//		report.setScQtFinalizationReportAttachment(
//					new ScQtFinalizationReportAttachment());
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
				attachment.setDocNo(getManagedBean().getScQt().getQtNo());

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
		
		ScQtFinalizationReportAttachment reportAttachment = scQtFinalizationReport.getScQtFinalizationReportAttachment();

		if (reportAttachment == null) {
			reportAttachment = generateDocumentAttachment(scQtFinalizationReport, false);
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
				attachment = generateDocumentAttachment(scQtFinalizationReports.get(i), false);
				scQtFinalizationReports.get(i).setScQtFinalizationReportAttachment(attachment);
			}
			scAttachments.add(attachment);
		}
		
		// Generate all the reports before submission
		if (!getManagedBean().isSubmit()) {
			downloadAllFiles("NGeP-Finalization-Report", scAttachments);
		}
	}
	
	 
    public void getSuppFinalizationEndorsementReportStreamContent() throws Exception {
		debugLogger("getSuppFinalizationEndorsementReportStreamContent");
		ScQtFinalizationReportAttachment reportAttachment = new ScQtFinalizationReportAttachment();
		reportAttachment.setDocNo(getManagedBean().getScQt().getQtNo());

		generateEndorsementReportFromVelocityToPdf(getManagedBean()
				.getScQtFinalization(), (ScAttachment) reportAttachment);
		StreamedContent streamContent = getAttachmentStreamContent(
				reportAttachment, CommonConstants.FILE_CONTENT_TYPE_PDF);

		getManagedBean().setEndorsementReportStream(streamContent);
    }	

	public ScQtFinalizationReportAttachment generateDocumentAttachment(
			ScQtFinalizationReport scQtFinalizationReport, boolean isDirectDownload) throws Exception {
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
			ScAttachment attachment = (ScAttachment) reportAttachment;
			StringBuilder filename = new StringBuilder();
			// replace white space due to firefox
			filename.append(scQtFinalizationReport.getTitle().replaceAll("\\s",
					"_"));
			
			if (SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT.equals(docDefinitionKey)) {
				filename.append(".pdf");
				reportAttachment.setFileName(filename.toString());
				generateKertasTaklimatPdf(scQt, attachment, isDirectDownload);

			} else {
				debugLogger("No document type is exists with the key "
						+ docDefinitionKey);
				reportAttachment = null;
			}

			//Redmine SIR#7782
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
//			} else if (SourcingConstants.DOC_DEF_KEY_OPENNING_REPORT
//					.equals(docDefinitionKey)) {
//				// FIXME - get from Opening report
//
//			} else if (SourcingConstants.DOC_DEF_KEY_TECHNICAL_EVAALUATION_REPORT
//					.equals(docDefinitionKey)) {
//				// FIXME - get from Technical Evaluation report
//
//			} else if (SourcingConstants.DOC_DEF_KEY_FINANCIAL_EVAALUATION_REPORT
//					.equals(docDefinitionKey)) {
//				// FIXME - get from Financial evaluation report
//			}
			
		} 
		
		return reportAttachment;
	}
	
	private void generateKertasTaklimatPdf(ScQt qt, ScAttachment attachment, boolean isDirectDownload) 
										   throws Exception {
		debugLogger("generateSupplierFinalizationPdf");
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

		generatePdfFromOracle(reportName, paramMap, attachment, isDirectDownload);		
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
		String ministryName = null;
		
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
			
			boolean specChanges = false;
			PmParameter rePublishQt=scQtFinalization.getRePublishOption();
			if (null != rePublishQt
					&& ParameterConst.WITH_OUT_SPEC_CHANGES
							.equalsIgnoreCase(rePublishQt.getParameterCode())) {
				specChanges = false;
			} else if (null != rePublishQt
					&& ParameterConst.WITH__SPEC_CHANGES
							.equalsIgnoreCase(rePublishQt.getParameterCode())) {
				specChanges = true;
			} else if (null != rePublishQt
					&& ParameterConst.SPEC_AND_COMMITTEE_CHANGES
							.equalsIgnoreCase(rePublishQt.getParameterCode())) {
				specChanges = true;
			}		
			context.put("requireSpecChanges", specChanges);
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
		
		IOrgProfileSO orgProfileSO = null;
		try {
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileId(scQt.getPmOrgProfile().getOrgProfileId());
			OrgProfileResDTO resDTO = orgProfileSO.retrieveMinistryByOrgProfileId(reqDTO);
			
			if (resDTO.getPmOrgProfile() != null) {
				ministryName = resDTO.getPmOrgProfile().getOrgName();
				ministryName = ministryName.toUpperCase();
			}
		} finally {
			orgProfileSO = null;
		}
		
		context.put("qtTitle", scQt.getQtTitle().toUpperCase());
		String committeeTypeStr = ParameterHelper.getInstance()
				.getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE, 
						getManagedBean().getCommitteeType(), SourcingConstants.MY_LOCALE);
		if (committeeTypeStr != null) {
			committeeTypeStr = committeeTypeStr.toUpperCase();
		}
		context.put("committeeTypeStr", committeeTypeStr);
		context.put("ministryName", ministryName);
		context.put("isSupplierFinalization", 
					 isSupplierFinalization);
		context.put("isForwardToHigherAuthority", 
					 isForwardToHigherAuthority);
		context.put("isReEvaluate", isReEvaluate);	
		context.put("isRepublish", isRepublish);
		context.put("isCancel", isCancel);

		if (scQtFinalization.getDecision() != null) {
			context.put("meetingDecision", getParameterDesc(
					scQtFinalization.getDecision(), SourcingConstants.MY_LOCALE));
		}

		if (scQtFinalization.getRecommendedTypeId() != null) {
			context.put("selectionBasedOn", getParameterDesc(
					scQtFinalization.getRecommendedTypeId(), SourcingConstants.MY_LOCALE));
		}
		
		if (scQtFinalization.getFinalizationMethod() != null) {
			context.put("finalizationMethod", getParameterDesc(
					scQtFinalization.getFinalizationMethod(), SourcingConstants.MY_LOCALE));
		}	
		
		if (scQtFinalization.getFinalizationMethod() != null) {
			String loiLoaPreparedBy = null;
			
			if (StringUtils.isNotBlank(
					scQtFinalization.getLoiLoaBy())) {
				PmParameter param = getPmParameterByTypeAndCode(
						ParameterTypeConst.FINALIZATION_LOILOA_PREPARED_BY, 
						scQtFinalization.getLoiLoaBy());
				if (param != null) {
					loiLoaPreparedBy = getParameterDesc(param, SourcingConstants.MY_LOCALE);
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
							scQtFinalization.getHigherCommitte(), SourcingConstants.MY_LOCALE));
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
		context.put("committeeType", getManagedBean().getCommitteeType());

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
		
		FinalizationEndorsementReportDTO reportDTO = null;
		Map<TplSpecZoneDtl,FinalizationEndorsementReportDTO> zoneDetailsMap = null;
		
		if (isSupplierFinalization || 
				isForwardToHigherAuthority) {
			Map<Long,TplSpecZoneDtl> zoneMap = getManagedBean().getZoneMap();
			
			if (recommendedItems != null) {
				if (!getManagedBean().isItemize()) {
					List<QtEvaluationResultDtlBean> recommendedSuppliers = null;
//					ScQtEvaluationResultDtl awardedSupplier = 
//								getManagedBean().getRecommendedSupplier();
					if (isZonal) {					
						processSuppliersBeforeSaving();
						
						zoneDetailsMap = new HashMap<TplSpecZoneDtl,FinalizationEndorsementReportDTO>();
						Map<Long,List<ScQtRecommend>> scQtRecommendsByZone = 
											scQtFinalization.getScQtRecommendsByZone();
						
						for (Long zoneId : zoneMap.keySet()) {
							reportDTO = new FinalizationEndorsementReportDTO();
							
							List<ScQtRecommend> items = scQtRecommendsByZone.get(zoneId);
							List<QtEvaluationResultDtlBean> cachedSuppliers = null;
//							if (MapUtils.isNotEmpty(
//									getManagedBean().getPkgShortlistedSupppliersByZone())) {
//								shortlistedSuppliers = getManagedBean()
//										.getPkgShortlistedSupppliersByZone().get(zoneId);
//							}
							cachedSuppliers = getCachedSuppliersFromMap(zoneId, DEFAULT_ITEM);
							
							if (!items.get(0).isCancelled() && 
									CollectionUtils.isNotEmpty(cachedSuppliers)) {
								recommendedSuppliers = new ArrayList<QtEvaluationResultDtlBean>();	
								
								for (QtEvaluationResultDtlBean evalResDtlBean
													        : cachedSuppliers) {
									if (getManagedBean().isSingleAward()) {
										if (evalResDtlBean.isAwarded()) {
											recommendedSuppliers.add(evalResDtlBean);
										}
									} else if (evalResDtlBean.isShortlisted()) {
										recommendedSuppliers.add(evalResDtlBean);
									}	
								}
							}
							
							if (CollectionUtils.isEmpty(
										recommendedSuppliers)) {
								reportDTO.setEmpty(true);
							}
							
							reportDTO.setRecommendedSuppliers(
									new ArrayList<QtEvaluationResultDtlBean>(recommendedSuppliers));
							reportDTO.setRecommendedItems(items);
							zoneDetailsMap.put(zoneMap.get(zoneId),reportDTO);
						}
					} else {
						processSuppliersBeforeSaving();
						
						reportDTO = new FinalizationEndorsementReportDTO();
						reportDTO.setEmpty(true);
						recommendedSuppliers = new ArrayList<QtEvaluationResultDtlBean>();
						List<QtEvaluationResultDtlBean> cachedSuppliers = 
									getCachedSuppliersFromMap(DEFAULT_ZONE, DEFAULT_ITEM);
						if (!recommendedItems.get(0).isCancelled() && 
								CollectionUtils.isNotEmpty(cachedSuppliers)) {
							for (QtEvaluationResultDtlBean evalResDtlBean
														: cachedSuppliers) {
								if (getManagedBean().isSingleAward()) {
									if (evalResDtlBean.isAwarded()) {
										recommendedSuppliers.add(evalResDtlBean);
									}
								} else if (evalResDtlBean.isShortlisted()) {
									recommendedSuppliers.add(evalResDtlBean);
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
	
							Map<ScQtRecommend,List<QtEvaluationResultDtlBean>> recommendedSuppliersByItem = 
											prepareRecommendedSuppliersByItems(scQtRecommendsByZone.get(zoneId));
							reportDTO.setRecommendedSuppliersByItem(recommendedSuppliersByItem);
							zoneDetailsMap.put(zoneMap.get(zoneId),reportDTO);
						}
					} else {
						reportDTO = new FinalizationEndorsementReportDTO();
						Map<ScQtRecommend,List<QtEvaluationResultDtlBean>> recommendedSuppliersByItem = 
														prepareRecommendedSuppliersByItems(recommendedItems);
						reportDTO.setRecommendedSuppliersByItem(recommendedSuppliersByItem);
					}
				}
			}	
			
			if (isZonal) {
				for (TplSpecZoneDtl specZone
							: getManagedBean().getZoneMap().values()) {
					if (!zoneDetailsMap.containsKey(specZone)) {
						FinalizationEndorsementReportDTO tempDTO
								= new FinalizationEndorsementReportDTO();
						tempDTO.setEmpty(true);
						zoneDetailsMap.put(specZone,tempDTO);
					}
				}
			}
		}	
		
		String chairPersonName = null;
		//String secretaryName = null;
		String memberName = null;
		
		List<ScCommitteeMember> commMembers = new ArrayList<ScCommitteeMember>();
		for (ScQtCommitteeActivity scQtCommitteeActivity 
					: getManagedBean().getCommitteeList()) {
			ScCommitteeMember commMember = scQtCommitteeActivity.getScCommitteeMember();
			if (commMember.getMemberRole().getParameterCode()
					.equals(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
				chairPersonName = commMember.getMemberName().toUpperCase();
			} /*else if (commMember.getMemberRole().getParameterCode()
					.equals(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
				secretaryName = commMember.getMemberName().toUpperCase();
			}*/ else if (commMember.getMemberRole().getParameterCode()
					.equals(ParameterConst.COMMITTEE_ROLE_MEMBER) && scQtCommitteeActivity.isAttended()) {
				memberName = commMember.getMemberName().toUpperCase();
				commMembers.add(commMember);
			}
		}
		context.put("commMembers", commMembers);
		context.put("chairPerson", chairPersonName);
//		context.put("secretary", secretaryName);
		context.put("member", memberName);
		
		StringBuilder endorsementRemarks = new StringBuilder();
		if (CollectionUtils.isNotEmpty(
				getManagedBean().getFinalizationEndorsementRemarks())) {
			for (ScQtFinalizationEndorsementRemarks remarks
						: getManagedBean().getFinalizationEndorsementRemarks()) {
				if (StringUtils.isNotBlank(remarks.getRemark())) {
					endorsementRemarks.append(remarks.getRemark() + "\n");
				}
			}
		}
		context.put("endorsementRemarks", endorsementRemarks.length() == 0 ? 
					StringUtils.EMPTY : endorsementRemarks.toString());
		context.put("reportDTO", reportDTO);
		context.put("zoneDetailsMap", zoneDetailsMap);
		context.put("numberFormatter", new DecimalFormat("#,###,##0.00"));
		context.put("itemized", getManagedBean().isItemize());
		context.put("isZonal", isZonal);		

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.DOCPATH_FZ_ENDORSEMENT_REPORT, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}	
	
	private Map<ScQtRecommend,List<QtEvaluationResultDtlBean>> prepareRecommendedSuppliersByItems(
						   										         List<ScQtRecommend> items) {
		Map<ScQtRecommend,List<QtEvaluationResultDtlBean>> validItems = 
							new HashMap<ScQtRecommend,List<QtEvaluationResultDtlBean>>();
		
		if (CollectionUtils.isNotEmpty(items)) {
			validItems = new HashMap<ScQtRecommend,List<QtEvaluationResultDtlBean>>();
			List<QtEvaluationResultDtlBean> recommendedSuppliers = null;
			
			for (ScQtRecommend item : items) {
				if (!item.isCancelled()) {
					Long specZoneId = null;
					if (item.getTplSpecZoneDtl() != null) {
						specZoneId = item.getTplSpecZoneDtl().getSpecZoneId();
					} else {
						specZoneId = DEFAULT_ZONE;
					}
					recommendedSuppliers = new ArrayList<QtEvaluationResultDtlBean>();
					List<QtEvaluationResultDtlBean> cachedSuppliers = getCachedSuppliersFromMap(
										specZoneId, item.getRequestItem().getRequestNoteItemId());
					
					if (CollectionUtils.isNotEmpty(cachedSuppliers)) {
						for (QtEvaluationResultDtlBean evalResDtlBean 
											        : cachedSuppliers) {
							if (getManagedBean().isSingleAward()) {
								if (evalResDtlBean.isAwarded()) {
									recommendedSuppliers.add(evalResDtlBean);
									calculateOfferedPrice(evalResDtlBean, item);
								}
							} else if (evalResDtlBean.isShortlisted()) {
								recommendedSuppliers.add(evalResDtlBean);
								calculateOfferedPrice(evalResDtlBean, item);
							}
						}
					}
					validItems.put(item,recommendedSuppliers);
				}
			}
		}
		
		return validItems;
	}
	
	private void retrieveSupplierFinalizationDtls(ScQt scQt) {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

//			boolean isZonal = scQt.isZonal();
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
			if (scQtFinalization != null) {
				this.getManagedBean().setScQtFinalization(scQtFinalization);
				
				List<String> rejectedRecommendDtlIds = getRejectedRecommendDtlIds();
				if (CollectionUtils.isNotEmpty(rejectedRecommendDtlIds)) {
					SuppFinalizationReqDTO _reqDTO = new SuppFinalizationReqDTO();
					_reqDTO.setRecommendDtlIds(rejectedRecommendDtlIds);
					
					SuppFinalizationResDTO _resDTO = qtSuppFinalizationSO.retrieveLoiLoaByRecommendDtlIds(_reqDTO);
					if (_resDTO.isSuccessful()) {
						List<ScLoiLoaItem> rejectedScLoiLoaItems = _resDTO.getScLoiLoaItemList();
						if (CollectionUtils.isNotEmpty(rejectedScLoiLoaItems)) {
							List<Long> rejectedScQtRecommendIds =
								getManagedBean().getRejectedScQtRecommendIds(); 
							
							if (rejectedScQtRecommendIds == null) {
								rejectedScQtRecommendIds = new ArrayList<Long>();
								getManagedBean().setRejectedScQtRecommendIds(
													 rejectedScQtRecommendIds);
							}
							ScQtRecommend scQtRecommend = null;
							for (ScLoiLoaItem scLoiLoaItem 
												: rejectedScLoiLoaItems) {
								PmParameter recommendType = null;
								
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
				
				getManagedBean().setRefinalizedAndBiddingAward(false);
				
				if (isQTRefinalized()) {
					disableAwardedItems(scQtFinalization.getScQtRecommends());
					
					// Check if the any of the suppliers has awarded LOA's TODO : To be removed [Not in use]
					/******Start******/
					reqDTO = new SuppFinalizationReqDTO();
					reqDTO.setQtId(getManagedBean().getScQt().getQtId());
					resDTO = qtSuppFinalizationSO.hasLOAAwarded(reqDTO);
					if (resDTO.isSuccessful()) {
						this.getManagedBean().setHasAwardedLOA(resDTO.isLoaAwarded());
					}
					/******End******/
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

	private void processShortlistedSuppliersByPackage(
				 			List<ScQtEvaluationResultDtl> shortlistedSuppliers) {
		Long currentZoneId = null;
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		
		List<ScQtRecommend> recommendedItems = null;
		if (getManagedBean().getCurrentZone() != null) {
			currentZoneId = getManagedBean().getCurrentZone().getSpecZoneId();
			recommendedItems = scQtFinalization.getScQtRecommendsByZone()
											   .get(currentZoneId);
		} else {
			recommendedItems = scQtFinalization.getScQtRecommends();
		}
		setRefinalizedAndBiddingAwardStatus(
				scQtFinalization, currentZoneId);
		
		if (CollectionUtils.isNotEmpty(recommendedItems) && 
								shortlistedSuppliers != null) {
			ScQtRecommend scQtRecommend = recommendedItems.get(0);

			Long specZoneId = scQtRecommend.getTplSpecZoneDtl() != null ? 
					scQtRecommend.getTplSpecZoneDtl().getSpecZoneId() : 0L;
			for (ScQtEvaluationResultDtl evalResDtl 
									: shortlistedSuppliers) {
				
				// Disable selection for rejected supplier.
				if (scQtRecommend.getRecommendDtls() != null) {
					Long evaZoneId = evalResDtl.getTplSpecZoneDtl() != null ? 
							evalResDtl.getTplSpecZoneDtl().getSpecZoneId() : 0L;

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
								&& specZoneId.equals(evaZoneId)
								&& (recommendDtl.getRecordStatusFlg().equals(
											DbValueConst.RECORD_STATUS_REJECTED))) {

							resetSupplierSelectionAndDisable(evalResDtl);
							break;
						}
					}
				}
				evalResDtl.setAmountInWords(convertNumberToWord(
						evalResDtl.getProposalAmount().getProposalAmt()));
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
		List<Long> rejectedRecommendIds =
					getManagedBean().getRejectedScQtRecommendIds();
		
		if (CollectionUtils.isNotEmpty(scQtRecommends)) {
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
			IQTSuppFinalizationSO qtSuppFinalizationSO = null;
			
			try {
				qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
				SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
				reqDTO.setQtId(getManagedBean().getScQt().getQtId());
				
				SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.checkIfQTRefinalized(reqDTO);
				isRefinalized = resDTO.isQtRefinalized();
			} finally {
				qtSuppFinalizationSO = null;
			}
			
			getManagedBean().setQtRefinalized(isRefinalized);
		}
		
		return isRefinalized;
	}
	
	private int findFinalizationCount() {
		IScWorkFlowStatusSO scWorkFlowStatusSO = null;
		
		try {
			scWorkFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			ScWorkFlowStatusReqDTO tenderReqDTO = new ScWorkFlowStatusReqDTO();
			tenderReqDTO.setDocId(this.getManagedBean().getScQt().getQtId());	
			List<Long> statusIds = new ArrayList<Long>();
			statusIds.add(QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION);
//			statusIds.add(QTStatusConst.QT_RE_FINALIZATION);
//			statusIds.add(QTStatusConst.QT_RE_EVALUATION);
			tenderReqDTO.setStatusIds(statusIds);
			
			List<ScWorkFlowStatus> statusList = scWorkFlowStatusSO
							.retrieveWorkFlowStatus(tenderReqDTO)
							.getWorkFlowStatusList();
			if (statusList != null) {
				return statusList.size();
			} 
		} finally {
			scWorkFlowStatusSO = null;
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
				String rejectedRecommendDtlId = rejectedRecommendDtlIds.get(i);
				if (StringUtils.isBlank(rejectedRecommendDtlId)) {
					rejectedRecommendDtlIds.remove(i--);
				}
			}
		}

		return rejectedRecommendDtlIds;
	}

	private void retrieveSupplierDetailsByRecommendType(
							ScQtFinalization scQtFinalization) {
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
			createShortlistedSuppliersDataModel();
		} else {
			boolean isInitialDecisionFwdToBPK =
						this.getManagedBean().isInitialDecisionFwdToBPK();
			boolean isInitialDecisionFwdToHighAuth = 
						this.getManagedBean().isInitialDecisionFwdHighAuth();	
			if (isInitialDecisionFwdToBPK || 
					isInitialDecisionFwdToHighAuth) {
				disableItemsNotRecommendedToHigherAuthority();
			}
		}
	}
	
	private void createShortlistedSuppliersDataModel() {
		Long selectedZoneId = null;
		String evaluationStage = null;
		ScQtRecommend selectedItem = getManagedBean().getSelectedItem();
		boolean bidRankRequired = getManagedBean().isRefinalizedAndBiddingAward();
		
		if (getManagedBean().isItemize()) {
			evaluationStage = SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM;
		} else {
			evaluationStage = SourcingConstants.EVAL_STAGE_RECOMMENDATION_PKG;
		}
		if (getManagedBean().getCurrentZone() != null) {
			selectedZoneId = getManagedBean().getCurrentZone().getSpecZoneId();
		}
		
		String componentId = "supplierListTblSingle";
		if (QTConstants.SF_SINGLE.equals(
				getManagedBean().getSelectionMode())) {
			componentId = "supplierListTblMultiple";
		}
		getManagedBean().setShortlistedSuppliersByRecommendType(
				new ShortlistedSuppliersPagingDataModel(
						componentId, getManagedBean().getScQt().getQtId(),
						evaluationStage, selectedZoneId, selectedItem, bidRankRequired)
		);			
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
						!(loggedInUserId.equals(
								getManagedBean().getOriginalFinalizationSecretaryUserId()))) {
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
				} else if (loggedInUserId.equals(
								getManagedBean().getOriginalFinalizationSecretaryUserId())) {
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
			}
		}		
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
			if (!isForwardToBPK) {
				scQtFinalization.setHigherCommitte(new PmParameter());
				scQtFinalization.setHigherCommitteStr(StringUtils.EMPTY);
				scQtFinalization.setHigherSecretary(new PmUser());
				scQtFinalization.setHigherSecretaryStr(StringUtils.EMPTY);
			}
		}
		if (isForwardToBPK) {
			scQtFinalization.setHigherCommitte(null);
			scQtFinalization.setHigherCommitteStr(null);
			scQtFinalization.setHigherSecretary(null);
			scQtFinalization.setHigherSecretaryStr(null);
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
				// != 1 - Other decision(s)
				if ("1".equals(reason.getReasonCode())) {
					higherCommittees = higherAuthoritiesMap.get(committeeType + 
							SourcingConstants.FORWARD_TO_HIGHER_AUTHORITY_DECISION_NON_UNANIMOUS);
				} else {
					higherCommittees = higherAuthoritiesMap.get(committeeType + 
							SourcingConstants.FORWARD_TO_HIGHER_AUTHORITY_DECISION_OTHERS);
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
		getManagedBean().setSelectedItem(null);
		
		onZoneTabChangeAction(selectedZoneTabIndex);
	}
	
	private void onZoneTabChangeAction(Integer zoneTabIndex) {
		debugLogger("onZoneTabChangeAction");

		this.getManagedBean().setRecommendedSupplier(null);
		this.getManagedBean().setDisableSupplierSelection(false);
		this.getManagedBean().setItemCancelled(false);

		this.getManagedBean().setZoneActiveIndex(zoneTabIndex);
		TplSpecZoneDtl selectedZone = this.getManagedBean().getZoneDetails()
												.get(zoneTabIndex);
		
		this.getManagedBean().setCurrentZone(selectedZone);
		if (!this.getManagedBean().isItemize()) {
			debugLogger("Zone - Packagedwise supplier selection");
			
			createShortlistedSuppliersDataModel();
		} else {
			this.getManagedBean().setShortlistedSuppliersByRecommendType(null);
			
			// To populate items specific to the selected zone
			populateScQtRecommendsByZone();
			setSelectedSupplierCodesByItem();
		}
		
		this.getManagedBean().setZoneTabChange(true);
		decisionEvent(null);
		this.getManagedBean().setZoneTabChange(false);
	}
	
	/**
	 * Event for selecting decision for Recommend Supplier, Re-Evaluation,
	 * Re-Publish or Cancel in Supplier Finalization
	 */
	public void decisionEvent(AjaxBehaviorEvent ajaxEvent) {
		debugLogger("Decision Event ");
		
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		
		this.getManagedBean().setRecommendSupplier(false);
		this.getManagedBean().setReEvaluate(false);
		this.getManagedBean().setRePublish(false);
		this.getManagedBean().setForwardToHigherAuthority(false);
		
		getManagedBean().setSelectedItem(null);
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
						!higherAuthoritiesMap.containsKey(committeeType + 
							SourcingConstants.FORWARD_TO_HIGHER_AUTHORITY_DECISION_OTHERS)) {
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
					resetDecisionForwardToHigherAuthority(scQtFinalization);
				}
			} else {
				this.getManagedBean().setForwardToHigherAuthority(true);
				scQtFinalization.setHigherAuthReq(true);
				
				if (ajaxEvent != null) {
					fwdHigherAuthorityEvent();
				}
			}
			if (StringUtils.isBlank(scQtFinalization.getLoiLoaBy())) {
				scQtFinalization.setLoiLoaBy(
						SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER);
			}
			
			String recommendType = scQtFinalization.getRecommendedTypeId()
												   .getParameterCode();
			if (ParameterConst.RECOMMEND_TYPE_PACKAGE
									.equals(recommendType)) {
				if (scQtFinalization.getScQtRecommends().size() > 0) {
					ScQtRecommend scQtRecommend = null;
					List<TplSpecZoneDtl> zoneDetails = getManagedBean().getZoneDetails();
					TplSpecZoneDtl selectedZone = null;
					Long selectedZoneId = null;
					
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
						if (scQtRecommend.isCancelled()) {
							this.getManagedBean().setItemCancelled(true);
						}
						if (scQtRecommend.isAwardedItem()) {
							this.getManagedBean().setDisableSupplierSelection(true);
						}
					} 
					
					setSelectionMode(finalizationMethod.getParameterCode());
				}
			} 

		} else if (decision.equals(ParameterConst.DECISION_RE_EVALUATION)) {
			scQtFinalization.setRePublishOption(null);
			this.getManagedBean().setReEvaluate(true);
		} else if (decision.equals(ParameterConst.DECISION_RE_PUBLISH)) {
			resetReEvaluate(scQtFinalization);
			
			if (null == scQtFinalization.getRePublishOption()) {
				scQtFinalization.setRePublishOption(new PmParameter());
			}
			this.getManagedBean().setRePublish(true);
		} else if (decision.equals(ParameterConst.DECISION_CANCEL)) {
			resetReEvaluate(scQtFinalization);
			scQtFinalization.setRePublishOption(null);
			this.getManagedBean().setCancel(true);
		}
	}
	
	private void resetDecisionRecommendSupplier(ScQtFinalization scQtFinalization) {
		scQtFinalization.setHigherCommitte(null);
		scQtFinalization.setHigherSecretary(null);
		scQtFinalization.setLoiLoaBy(null);
		scQtFinalization.setReason(null);

		resetFlag();
	}

	private void resetReEvaluate(ScQtFinalization scQtFinalization) {
		scQtFinalization.setReEvaluateCommitte(null);
		scQtFinalization.setReEvaluateCompReq(false);
		scQtFinalization.setReEvaluateTechReq(false);
		scQtFinalization.setReEvaluateFinReq(false);
	}

	private void resetDecisionForwardToHigherAuthority(ScQtFinalization scQtFinalization) {
		resetForwardToHigherAuthorityParams(scQtFinalization);
		scQtFinalization.setHigherAuthReq(false);
		this.getManagedBean().setForwardToBpk(false);
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
		scQtFinalization.setRePublishOption(null);
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
		if (getManagedBean().isInitialDecisionFwdToBPK()) {
			isBPKUser = true;
		}

		int tabIndex = 0;
		MenuItem menuItem = null;
		String tabMenuChangeCommonAction = "onMainTabMenuChangeAction([ {name : 'selectedTabIndex', value : ";
		
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
			menuItem.setOncomplete(tabMenuChangeCommonAction
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
		menuItem.setOncomplete(tabMenuChangeCommonAction
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
		menuItem.setOncomplete(tabMenuChangeCommonAction
				+ (isBPKUser ? 2 : (tabIndex++)) + "}]); ");
		mainMenuModel.addMenuItem(menuItem);

		
		// Supplier Finalization Committee Endorsement Menu Item
		if (!isBPKUser) {
			menuItem = new MenuItem();
			menuItem.setValue(ResourceBundleUtils.getMessage(
							"lbl.sc.committee.endorsement.report",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
			menuItem.setStyle("width:15%;");
			menuItem.setTransient(true);
			menuItem.setPartialSubmit(true);
			menuItem.setOncomplete(tabMenuChangeCommonAction
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
			menuItem.setOncomplete(tabMenuChangeCommonAction
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

	private void resetItemDetails() {
		ScQtFinalization scQtFinalization = 
				  getManagedBean().getScQtFinalization();
		List<ScQtRecommend> scQtRecommendList = 
				  scQtFinalization.getScQtRecommends();
		
		if (CollectionUtils.isNotEmpty(scQtRecommendList)) {
			boolean isItemized = this.getManagedBean().isItemize();
			for (ScQtRecommend scQtRecommend 
									: scQtRecommendList) {
				if (scQtRecommend.isDisabled() ||
						scQtRecommend.isAwardedItem()) {
					continue;
				}
				if (isItemized) {
					scQtRecommend.setSelected(false);
					scQtRecommend.setCancelled(false);
				}
				
				List<ScQtRecommendDtl> recommendDtls = scQtRecommend
						.getRecommendDtls();
				if (CollectionUtils.isNotEmpty(recommendDtls)) {
					prepareToDeleteRecommendDtls(scQtRecommend);
				}
			}
		}
		
		scQtFinalization.setOnlineBiddingEndorse(null);
		
		if (!isQTRefinalized()) {
			resetFlag();
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
		this.getManagedBean().setShortlistedSuppliersByRecommendType(null);
		
		populateRecommendTypeEventDetails(true);
	}

	private void populateRecommendTypeEventDetails(boolean triggeredFromEvent) {
		this.getManagedBean().setSelectedItem(null);
		
		boolean itemized = false;
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		String recommendType = scQtFinalization.getRecommendedTypeId()
				.getParameterCode();
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
		
		setSelectionMode(finalizationMethod);
		
		if (itemized) {
			if (isZonal) {
				populateScQtRecommendsByZone();
			} else {
				this.getManagedBean().setItemList(
						scQtFinalization.getScQtRecommends());
			}
			setSelectedSupplierCodesByItem();
		} else {
			if (triggeredFromEvent) {
				List<ScQtRecommend> scQtRecommendList = 
						  scQtFinalization.getScQtRecommends();
				for (ScQtRecommend item : scQtRecommendList) {
					item.setCancelled(false);
				}
			}
			
			createShortlistedSuppliersDataModel();
		}
	}
	
	private void setSelectedSupplierCodesByItem() {
		boolean isAward = getManagedBean().isSingleAward();
		
		List<ScQtRecommend> items = getManagedBean().getItemList();
		for (ScQtRecommend item : items) {
			Long specZoneId = null;
			if (item.getTplSpecZoneDtl() != null) {
				specZoneId = item.getTplSpecZoneDtl().getSpecZoneId();
			} else {
				specZoneId = DEFAULT_ZONE;
			}
			
			Long itemId = item.getRequestItem().getRequestNoteItemId();
			SuppFinalizationResDTO resDTO = retrieveQualifiedSuppliers(isAward, !isAward, 
																	  specZoneId, itemId);	
			List<ScQtEvaluationResultDtl> qualifiedSuppliers = 
					  resDTO.getQualifiedSuppliersByEvalStage().getResultList();
			item.setSelectedSuppliers(new HashMap<Integer,SmSupplier>());
			
			if (CollectionUtils.isNotEmpty(qualifiedSuppliers)) {
				List<QtEvaluationResultDtlBean> cachedSuppliersList = 
						  		getCachedSuppliersFromMap(specZoneId, itemId);				
				for (ScQtEvaluationResultDtl evalResDtl 
								   : qualifiedSuppliers) {
					QtEvaluationResultDtlBean recommendedBean = getCachedBeanFromList(
							cachedSuppliersList, evalResDtl.getEvaluationResultDtlId());
					if (recommendedBean == null) {
						recommendedBean = updateCachedData(evalResDtl, specZoneId, itemId);
					}
					if (isAward && recommendedBean.isAwarded()) {
						item.getSelectedSuppliers().put(
								recommendedBean.getScQtProposal().getSupplierNo(),
								recommendedBean.getScQtProposal().getScQtSupplier().getSmSupplier());
					} else if (!isAward && recommendedBean.isShortlisted()) {
						item.getSelectedSuppliers().put(
								recommendedBean.getScQtProposal().getSupplierNo(),
								recommendedBean.getScQtProposal().getScQtSupplier().getSmSupplier());
					}
				}
			}
		}
		
	}
	
	private void setSelectionMode(String finalizationMethod) {
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
		
		this.getManagedBean().setSelectionMode(selectionMode);
	}
	
	/**
	 * Event for Re-evaluate by Document compliance, Technical, Financial
	 */
	public void reEvaluateByOptionEvent(String reEvaluateOption, boolean isSelected) {
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		
		if (StringUtils.isNotBlank(
					reEvaluateOption)) {
			
			// Free fall method
			switch (reEvaluateOption) {
				case "1" : 
					scQtFinalization.setReEvaluateCompReq(isSelected);
				case "2" : 
					scQtFinalization.setReEvaluateTechReq(isSelected);
				case "3" : 
					scQtFinalization.setReEvaluateFinReq(isSelected);
			}
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
			} else {
				this.getManagedBean().setSelectionMode(QTConstants.SF_MULTIPLE);
				scQtFinalization.setOnlineBiddingEndorse(null);
				
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
				}
			}
			
			PmParameter finalizationMethod = getParameterByTypeAndCode(
					ParameterTypeConst.FINALIZATION_RECOMMEND_BY, finalizationMethodStr);
			getManagedBean().getScQtFinalization().setFinalizationMethod(finalizationMethod);
			
			if (ajaxEvent != null) { 
				this.getManagedBean().setDisableSupplierSelection(false);
				
				if (isItemize) {	
					setSelectedSupplierCodesByItem();
				} else {
					createShortlistedSuppliersDataModel();
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
		boolean isZonal = scQt.isZonal();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		Map<Long, TplSpecZoneDtl> zoneMap = getManagedBean().getZoneMap();
		SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
		
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);	
			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setZonal(isZonal);
			reqDTO.setItemize(isItemize);
			
			StringBuilder requestItemAndZoneId = new StringBuilder();
			List<String> requestItemAndZoneIds = new ArrayList<String>();
			for (ScQtRecommend scQtRecommend 
						: scQtFinalization.getScQtRecommends()) {
				if (!(scQtRecommend.isCancelled() || 
						scQtRecommend.isAwardedItem())) {
					requestItemAndZoneId = new StringBuilder();
					
					if (isItemize) {
						requestItemAndZoneId.append(scQtRecommend.getRequestItem().getRequestNoteItemId());
						
						if (isZonal) {
							requestItemAndZoneId.append(CommonConstants.DB_FIELDS_SEPARATOR);	
						}
					}
					
					if (isZonal && scQtRecommend.getTplSpecZoneDtl() != null) {
						requestItemAndZoneId.append(scQtRecommend.getTplSpecZoneDtl().getSpecZoneId());
					}
					
					if (!requestItemAndZoneIds.contains(requestItemAndZoneId.toString())) {
						requestItemAndZoneIds.add(requestItemAndZoneId.toString());	
					}
				}
			}
			if (requestItemAndZoneIds.size() > 0) {
				reqDTO.setRequestItemAndZoneIds(requestItemAndZoneIds);
				
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
			}
		} finally {
			qtSuppFinalizationSO = null;
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
			this.getManagedBean().setDisableSupplierSelection(false);
			
			// clear the RecommendDtl list
			if (scQtRecommend.getRecommendDtls() != null) {
				scQtRecommend.setAwardSupplierList(
						new ArrayList<ScQtEvaluationResultDtl>());
				
				prepareToDeleteRecommendDtls(scQtRecommend);
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
		
		List<ScQtRecommend> scQtRecommends = null;
		Long currentZoneId = getManagedBean().getCurrentZone().getSpecZoneId();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		Map<Long,List<ScQtRecommend>> zonalItems = scQtFinalization.getScQtRecommendsByZone();
		
		SuppFinalizationResDTO resDTO = retrieveQualifiedSuppliers(true, true, currentZoneId, null);
		if (resDTO.isSuccessful()) {
			ISearchResultBean<ScQtEvaluationResultDtl> resultBean = 
								resDTO.getQualifiedSuppliersByEvalStage();
			if (CollectionUtils.isNotEmpty(
					resultBean.getResultList())) {
				for (ScQtEvaluationResultDtl evalResDtl 
								 : resultBean.getResultList()) {
					updateCachedData(evalResDtl, currentZoneId, DEFAULT_ITEM);
				}
			}
		}
		
		if (MapUtils.isNotEmpty(zonalItems) && 
				zonalItems.containsKey(currentZoneId)) {
			scQtRecommends = zonalItems.get(currentZoneId);
		}		
		
		if (CollectionUtils.isNotEmpty(scQtRecommends)) {
			MapKey mapKey = new MapKey(currentZoneId, DEFAULT_ITEM);
			
			List<QtEvaluationResultDtlBean> cachedSuppliers = 
					getManagedBean().getQualifiedSuppliersCache()
									.get(mapKey);
			if (CollectionUtils.isNotEmpty(cachedSuppliers)) {
				for (QtEvaluationResultDtlBean cachedSupplier 
											: cachedSuppliers) {
					cachedSupplier.setAwarded(false);
					cachedSupplier.setShortlisted(false);
				}
			}
			if (zonalPackageItemsCancelled) {
				for (ScQtRecommend scQtRecommend : scQtRecommends) {
					scQtRecommend.setCancelled(true);
					
					// clear the RecommendDtl list
					if (CollectionUtils.isNotEmpty(
							scQtRecommend.getRecommendDtls())) {
						prepareToDeleteRecommendDtls(scQtRecommend);
					}					
				}
				
				getManagedBean().setRecommendedSupplier(null);
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

		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		boolean fwdHigherAuthority = scQtFinalization.isHigherAuthReq();

		if (fwdHigherAuthority) {
			if (!getManagedBean().isForwardToBpk()) {
				String higherCommitteeType = null;
				if (scQtFinalization.getHigherCommitte() != null) {
					higherCommitteeType = scQtFinalization
							.getHigherCommitte().getParameterCode();
				}
				if (SourcingConstants.FC_COMMITTEE_TYPE_MOF
									 .equals(higherCommitteeType)) {
					getManagedBean().setForwardToBpk(true);
				} else if (getManagedBean().isSecretaryUser()) {
					if (getManagedBean().getScQtFinalization().getHigherCommitte() == null) {
						getManagedBean().getScQtFinalization().setHigherCommitte(
								new PmParameter());
					}
					if (getManagedBean().getScQtFinalization().getReason() == null) {
						getManagedBean().getScQtFinalization().setReason(new PmReason());
					}
				}
			}
		} else if (!this.getManagedBean().isInitialDecisionFwdHighAuth()) {
			resetDecisionForwardToHigherAuthority(scQtFinalization);
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
		
		setRefinalizedAndBiddingAwardStatus(
					scQtFinalization, specZoneId);
		if (!scQtRecommend.isCancelled()) {
			getManagedBean().setSelectedItem(scQtRecommend);
		}
		
		scQtRecommend.setSelected(true);
		addSelectedItem(scQtRecommend);
		
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
			createShortlistedSuppliersDataModel();
		} else {
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
	public void recommendSupplierByAwardEvent(SelectEvent selectEvent) {
		ScQtEvaluationResultDtl recommendedSupplier = 
					this.getManagedBean().getRecommendedSupplier();

		recommendedSupplier.setAwareded(true);
		selectSupplierEvent(recommendedSupplier, true);
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
		
		Integer supplierCode = evaluationResultDtl.getQtEvaluationResult()
									    .getScQtProposal().getSupplierNo();
		SmSupplier smSupplier = evaluationResultDtl.getQtEvaluationResult()
									.getScQtProposal().getScQtSupplier().getSmSupplier();
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
			
			if (isSelected) {
				if (isItemized) {
					ScQtRecommend selectedItem = getManagedBean().getSelectedItem();
					if (isAwarded) {
						selectedItem.getSelectedSuppliers().clear();
					}
					selectedItem.getSelectedSuppliers().put(supplierCode, smSupplier);
				}
			} else {
				evaluationResultDtl.setAwareded(false);
				evaluationResultDtl.setShortListed(false);
				
				if (isItemized) {
					ScQtRecommend selectedItem = getManagedBean().getSelectedItem();
					selectedItem.getSelectedSuppliers().remove(supplierCode);
				}
			}
			
			Long selectedZoneId = null;
			if (getManagedBean().getScQt().isZonal()) {
				selectedZoneId = getManagedBean().getCurrentZone().getSpecZoneId();
			} else {
				selectedZoneId = DEFAULT_ZONE;
			}
			
			Long selectedItemId = null;
			if (getManagedBean().isItemize()) {
				selectedItemId = getManagedBean().getSelectedItem()
								 .getRequestItem().getRequestNoteItemId();
			} else {
				selectedItemId = DEFAULT_ITEM;
			}
			
			if (isAwarded) {
				
				// Reset previously awarded suppliers
				checkAndResetPreviouslyAwardedSupplier(
						evaluationResultDtl, selectedZoneId, selectedItemId);
			}
			updateCachedData(evaluationResultDtl, selectedZoneId, selectedItemId);
		} 
	}
	
	/**
	 * Update the supplier selection in the cached list
	 */
	private QtEvaluationResultDtlBean updateCachedData(ScQtEvaluationResultDtl selectedSupplier,
								  					   Long specZoneId, Long requestedItemId) {
		boolean alreadyExists = true;
		
		if (selectedSupplier == null) {
			return null;
		}
		
		List<QtEvaluationResultDtlBean> cachedSuppliersList = 
				getCachedSuppliersFromMap(specZoneId, requestedItemId);
		
		QtEvaluationResultDtlBean recommendedBean = getCachedBeanFromList(
				cachedSuppliersList, selectedSupplier.getEvaluationResultDtlId());
		
		if (recommendedBean == null) {
			recommendedBean = new QtEvaluationResultDtlBean();
			recommendedBean.setQtEvalResDtlId(
					selectedSupplier.getEvaluationResultDtlId());
			alreadyExists = false;
		}
		
		recommendedBean.setAwarded(
				selectedSupplier.isAwareded());
		recommendedBean.setShortlisted(
				selectedSupplier.isShortListed());
		recommendedBean.setBidRank(
				selectedSupplier.getBidRank());
		recommendedBean.setSmSupplier(selectedSupplier.getQtEvaluationResult()
						  .getScQtProposal().getScQtSupplier().getSmSupplier());
		recommendedBean.setAmountInWords(
				selectedSupplier.getAmountInWords());
		ScQtProposal scQtProposal = selectedSupplier.getQtEvaluationResult()
													.getScQtProposal();
		recommendedBean.setScQtProposal(scQtProposal);
		recommendedBean.setScQtProposalItem(
					selectedSupplier.getQtProposalItem());
		ScQtProposalAmount scQtProposalAmount = 
					selectedSupplier.getProposalAmount();
		
		scQtProposalAmount.setScQtProposal(scQtProposal);
		
		recommendedBean.setScQtProposalAmount(scQtProposalAmount);
		
		if (!alreadyExists) {
			cachedSuppliersList.add(recommendedBean);
		}
		
		return recommendedBean;
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

	private BigDecimal calculateTotalIntendedPrice(ScQtFinalization scQtFinalization) {
		BigDecimal intendedPrice = null;
		BigDecimal totalIntendedPrice = new BigDecimal(0);
		
		if (getManagedBean().isItemize()) {
			for (ScQtRecommend item :
					scQtFinalization.getScQtRecommends()) {
				if (!item.isCancelled()) {
					Long specZoneId = null;
					if (item.getTplSpecZoneDtl() != null) {
						specZoneId = item.getTplSpecZoneDtl().getSpecZoneId();
					} else {
						specZoneId = DEFAULT_ITEM;
					}
					Long requestItemId = item.getRequestItem().getRequestNoteItemId();
					
					List<QtEvaluationResultDtlBean> cachedSuppliersByItem = 
							getCachedSuppliersFromMap(specZoneId, requestItemId);
					if (CollectionUtils.isNotEmpty(cachedSuppliersByItem)) {
						for (QtEvaluationResultDtlBean cachedSupplier 
														: cachedSuppliersByItem) {
							if (cachedSupplier.isAwarded()) {
								calculateOfferedPrice(cachedSupplier, item);
								
								if (cachedSupplier.getBidRank() != null) {
									intendedPrice = cachedSupplier.getBidPrice();
								} else {
									intendedPrice = cachedSupplier.getOfferedPrice();												
								}
								if (intendedPrice != null) {
									totalIntendedPrice = totalIntendedPrice.add(intendedPrice);
									totalIntendedPrice = totalIntendedPrice.setScale(2,
															BigDecimal.ROUND_HALF_EVEN);
								}
								break;
							}
						}
					}
				}
			}
		} else {
			if (MapUtils.isNotEmpty(
					getManagedBean().getQualifiedSuppliersCache())) {
				for (MapKey mapKey : getManagedBean().getQualifiedSuppliersCache().keySet()) {
					List<ScQtRecommend> items = null;
					if (DEFAULT_ZONE.equals(mapKey.getZoneId())) {
						items = scQtFinalization.getScQtRecommends();
					} else {
						items = scQtFinalization.getScQtRecommendsByZone()
												.get(mapKey.getZoneId());						
					}
					if (!items.get(0).isCancelled()) {
						List<QtEvaluationResultDtlBean> cachedSuppliers 
								= getManagedBean().getQualifiedSuppliersCache().get(mapKey);
						
						if (CollectionUtils.isNotEmpty(cachedSuppliers)) {
							for (QtEvaluationResultDtlBean evalResultDtlBean 
														   : cachedSuppliers) {
								if (evalResultDtlBean.isAwarded()) {
									if (evalResultDtlBean.getBidRank() != null) {
										calculateBidPrice(items, evalResultDtlBean);
										intendedPrice = evalResultDtlBean.getBidPrice();
									} else {
										intendedPrice = evalResultDtlBean.getScQtProposalAmount()
																		 .getProposalAmt();
									}
									break;
								}
							}
							totalIntendedPrice = totalIntendedPrice.add(intendedPrice);
							totalIntendedPrice = totalIntendedPrice.setScale(2,
													BigDecimal.ROUND_HALF_EVEN);
						}
					}
				}
			}
			
		}
		
		return totalIntendedPrice;
	}
	
	private void calculateOfferedPrice(QtEvaluationResultDtlBean evalResDtlBean,
									   ScQtRecommend scQtRecommend) {
		ScRequestItem requestItem = scQtRecommend.getRequestItem();
		ScQtProposalItem proposalItem = getQtProposalItem(scQtRecommend, 
											evalResDtlBean.getScQtProposalAmount());
		if (proposalItem != null) {
			if (evalResDtlBean.getBidRank() != null) {
				BigDecimal bidPrice = TemplateSpecHelper.getInstance()
						.calculateTotalAmount(requestItem.getTplSpecQuestion(),
								proposalItem.getFinalPrice(),
								scQtRecommend.getOrderQty());
				
				evalResDtlBean.setBidPrice(bidPrice);
				evalResDtlBean.setAmountInWords(
							 	convertNumberToWord(bidPrice));
			} else {
				BigDecimal offeredPrice = TemplateSpecHelper.getInstance()
						.calculateTotalAmount(requestItem.getTplSpecQuestion(),
								proposalItem.getOfferPrice(),
								scQtRecommend.getOrderQty());
				
				evalResDtlBean.setOfferedPrice(offeredPrice);	
				evalResDtlBean.setAmountInWords(
								convertNumberToWord(offeredPrice));
			}
			evalResDtlBean.setScQtProposalItem(proposalItem);
		}
	}
	
	private String convertNumberToWord(BigDecimal amount) {
		String amountInWords = null;
		if (amount != null) {
			amountInWords = WordUtils.capitalizeFully(
							NumberWordUtil.convert(amount.toString()));
		}
		return amountInWords;
	}
	
	private void calculateBidPrice(List<ScQtRecommend> recommendedItems, 
								   QtEvaluationResultDtlBean evalResDtlBean) {
		BigDecimal totalBidPrice = new BigDecimal(0.0);
		
		for (ScQtRecommend scQtRecommend 
								: recommendedItems) {
			if (!scQtRecommend.isCancelled()) {
				ScQtProposalItem proposalItem = getQtProposalItem(scQtRecommend, 
										 evalResDtlBean.getScQtProposalAmount());
				ScRequestItem requestItem = scQtRecommend.getRequestItem();
				totalBidPrice = totalBidPrice.add(TemplateSpecHelper.getInstance()
						.calculateTotalAmount(requestItem.getTplSpecQuestion(),
								proposalItem.getFinalPrice(),
								scQtRecommend.getOrderQty()));
				totalBidPrice = totalBidPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
			}
		}
		evalResDtlBean.setBidPrice(totalBidPrice);
	}

	private SuppFinalizationResDTO retrieveQualifiedSuppliers(boolean isAwarded,  boolean isShortlisted,  
									 						  Long specZoneId, Long requestItemId) {
		SuppFinalizationResDTO resDTO = new SuppFinalizationResDTO();
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			
			String evaluationStage = null;
			if (requestItemId != null)  {
				evaluationStage = SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM;
			} else {
				evaluationStage = SourcingConstants.EVAL_STAGE_RECOMMENDATION_PKG;
			}
			
			ISearchBean<ScQtEvaluationResultDtl> searchBean = 
							new SearchBean<ScQtEvaluationResultDtl>();
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"qtEvaluationResult.scQtProposal.scQtSupplier.scQt.qtId",
					SearchOperator.OPERATOR_EQUALS.value(), 
					this.getManagedBean().getScQt().getQtId()));
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("qtEvaluationResult.evaluationStage",
							SearchOperator.OPERATOR_EQUALS.value(), evaluationStage));		
			
			if (null != specZoneId && !DEFAULT_ZONE.equals(specZoneId)) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("tplSpecZoneDtl.specZoneId",
								SearchOperator.OPERATOR_EQUALS.value(), specZoneId));
			} 
			if (null != requestItemId && !DEFAULT_ITEM.equals(requestItemId)) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("scRequestItem.requestNoteItemId",
								SearchOperator.OPERATOR_EQUALS.value(), requestItemId));				
			} 	
			
			if (isAwarded && isShortlisted) {
				searchBean.getDataFilterBeans().add(
						new SearchMultiFilterBean(
								new SearchFilterBean(
								"isAwareded",
								SearchOperator.OPERATOR_EQUALS.value(), true),
								new SearchFilterBean(
								"isShortListed",
								SearchOperator.OPERATOR_EQUALS.value(), true,
								SearchCondition.CONDITION_OR.value())));
			} else if (isAwarded) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("isAwareded",
								SearchOperator.OPERATOR_EQUALS.value(), true));	
			} else if (isShortlisted) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("isShortListed",
								SearchOperator.OPERATOR_EQUALS.value(), true));					
			}
			
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setSearchBean(searchBean);
			reqDTO.setSpecZoneId(DEFAULT_ZONE.equals(specZoneId) ? null : specZoneId);
			
			resDTO = qtSuppFinalizationSO.retrieveQualifiedSuppliersInfoByEvalStage(reqDTO);
		} finally {
			qtSuppFinalizationSO = null;
		}

		return resDTO;
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
							evalResultDtl.setAwareded(isAwarded);
							evalResultDtl.setShortListed(isShortlisted);
							this.getManagedBean().setRecommendedSupplier(null);
							
							// If finalization method is award, remove
							// the previously recommended supplier details
							if (isAwarded) {
								prepareToDeleteRecommendDtls(scQtRecommend);
							}
							break;
						}
					}
				}
			}
			if (removableItem != null) {
				selectedItemSet.remove(removableItem);
			}

			this.getManagedBean().setSelectedItemList(null);
			this.getManagedBean().setAwardSupplierList(null);
			this.getManagedBean().setAwardSupplier(null);
		} else {
			log.info("There are no selected items");
			return;
		}
	}
	
	private boolean isRecommendByAward() {
		PmParameter finalizationMethod = 
					getManagedBean().getScQtFinalization()
									.getFinalizationMethod();
		if (isQTRefinalized() || 
				ParameterConst.RECOMMEND_BY_AWARD
					.equals(finalizationMethod.getParameterCode())) {
			return true;
		}
		
		return false;
	}

	private void validateByPackage(ScQtRecommend scQtRecommend) {
		boolean isAward = false;
		boolean supplierSelected = true;
		boolean minSuppliersExist = true;
		TplSpecZoneDtl zoneDtl = scQtRecommend.getTplSpecZoneDtl();
		PmParameter finalizationMethod = getManagedBean().getScQtFinalization()
											.getFinalizationMethod();
		isAward = isRecommendByAward();
		
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
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		PmParameter finalizationMethod = scQtFinalization.getFinalizationMethod();	
		String defaultRecommendByStr = ParameterConst.RECOMMEND_BY_AWARD;
		
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
			throw new EpException(MessageConst.ERROR_760213, new String[] {
					scQtRecommend.getRequestItem().getItemName() });
		}
		
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
	
	public List<Integer> getSupplierCodes(ScQtRecommend item) {
		return new ArrayList<Integer>(
					item.getSelectedSuppliers().keySet());
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
		return getCommitteMemberByUserId()
				.getRoleCode();
	}

	private CommitteResDTO getCommitteMemberByUserId() {
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
		
		return committeSO.retrieveCommitteMemberByUserId(
				committeReqDTO);
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
	 * Evaluation Qualified Suppliers Paging Data Model
	 * 
	 * @author Praneeth
	 * @since 17.02.2016
	 */
	private class QualifiedSuppliersPagingDataModel extends
			PagingDataModel<ScQtEvaluationResultDtl> {

		private static final long serialVersionUID = 1L;

		private Long qtId;
		private Long selectedZoneId;
		private Long selectedItemId;
		private String evaluationStage;

		/**
		 * Constructor for Summary Supplier Paging Data Model
		 */
		public QualifiedSuppliersPagingDataModel(String componentId,
					            Long qtId, Long selectedZoneId, 
								Long selectedItemId, String evaluationStage) {
			super(componentId);
			this.qtId = qtId;
			this.selectedZoneId = selectedZoneId; 
			this.selectedItemId = selectedItemId;
			this.evaluationStage = evaluationStage;
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
			if (null != selectedItemId) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("scRequestItem.requestNoteItemId",
								SearchOperator.OPERATOR_EQUALS.value(), selectedItemId));				
			}			
			
			IQTSuppFinalizationSO qtSuppFinalizationSO = null;
			try {
				qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);
				
				SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
				reqDTO.setSearchBean(searchBean);
				
				SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
							.retrieveQualifiedSuppliersInfoByEvalStage(reqDTO);
				if (resDTO.isSuccessful()) {
					resultBean = resDTO.getQualifiedSuppliersByEvalStage();
				}
			} finally {
				qtSuppFinalizationSO = null;
			}
			
			return resultBean;
		}
	}
	
	
	/**
	 * Inner class FinalizationEndorsementReportDTO for 
	 * Finalization Endorsement Report.
	 */
	public class FinalizationEndorsementReportDTO {
		private List<QtEvaluationResultDtlBean> recommendedSuppliers;
		private List<ScQtRecommend> recommendedItems;
		private Map<ScQtRecommend,List<QtEvaluationResultDtlBean>> recommendedSuppliersByItem;
		private boolean empty;
		
		public FinalizationEndorsementReportDTO() {}
		
		/**
		 * @return the recommendedSuppliers
		 */
		public List<QtEvaluationResultDtlBean> getRecommendedSuppliers() {
			return recommendedSuppliers;
		}

		/**
		 * @param recommendedSuppliers
		 *            the recommendedSuppliers to set
		 */
		public void setRecommendedSuppliers(
				List<QtEvaluationResultDtlBean> recommendedSuppliers) {
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
		 * @return the recommendedSuppliersByItem
		 */
		public Map<ScQtRecommend, List<QtEvaluationResultDtlBean>> getRecommendedSuppliersByItem() {
			return recommendedSuppliersByItem;
		}

		/**
		 * @param recommendedSuppliersByItem
		 *            the recommendedSuppliersByItem to set
		 */
		public void setRecommendedSuppliersByItem(
				Map<ScQtRecommend, List<QtEvaluationResultDtlBean>> recommendedSuppliersByItem) {
			this.recommendedSuppliersByItem = recommendedSuppliersByItem;
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
	
	
	// this method checking two meeting dates or equal or not.
	private void compareMeetingDates(ScQtMeeting scqtMeeting,
			List<ScQtMeeting> meetingList) {
		if (null != meetingList && meetingList.size() >= 1) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			boolean duplicateDate = false;
			for (ScQtMeeting meeting : meetingList) {
				String objectDate = sdf.format(scqtMeeting.getMeetingDate());
				String listDate = sdf.format(meeting.getMeetingDate());

				if (objectDate.equals(listDate)) {
					duplicateDate = true;
					break;
				}
			}
			if (duplicateDate == true) {
				throw new EpException(MessageConst.ERROR_760271);
			} else {
				meetingList.add(scqtMeeting);
			}
		} else {
			meetingList.add(scqtMeeting);
		}

	}
	
	
	/**
	 * Qualified Suppliers Paging Data Model
	 * 
	 * @author Praneeth
	 * @since 18.08.2016
	 */
	private class ShortlistedSuppliersPagingDataModel extends
						PagingDataModel<ScQtEvaluationResultDtl> {

		private static final long serialVersionUID = 1L;

		private Long qtId;
		private String evaluationStage;
		private Long selectedZoneId;
		private ScQtRecommend selectedItem;
		private boolean bidRankRequired;

		/**
		 * Constructor for Summary Supplier Paging Data Model
		 */
		public ShortlistedSuppliersPagingDataModel(String componentId,
					            Long qtId, String evaluationStage, 
					            Long selectedZoneId, ScQtRecommend selectedItem,
					            boolean bidRankRequired) {
			super(componentId);
			this.qtId = qtId;
			this.evaluationStage = evaluationStage;
			this.selectedZoneId = selectedZoneId; 
			this.selectedItem = selectedItem;
			this.bidRankRequired = bidRankRequired;
			
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
				
				if (selectedZoneId != null) {
					reqDTO.setSpecZoneId(selectedZoneId);
				}
				
				SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
							.retrieveQualifiedSuppliersInfoByEvalStage(reqDTO);
				if (resDTO.isSuccessful()) {
					resultBean = resDTO.getQualifiedSuppliersByEvalStage();
					List<ScQtEvaluationResultDtl> qualifiedSuppliers = 
												    	resultBean.getResultList();
					Long zoneId = (selectedZoneId == null ? DEFAULT_ZONE : selectedZoneId);
					selectedItemId = (selectedItemId == null ? DEFAULT_ITEM : selectedItemId);
					List<QtEvaluationResultDtlBean> cachedSuppliers = 
							getCachedSuppliersFromMap(zoneId, selectedItemId);
					
					if (CollectionUtils.isNotEmpty(qualifiedSuppliers)) {
						if (selectedItem != null) {
							disableSuppliersByItem(selectedItem, qualifiedSuppliers);
						} else {
							processShortlistedSuppliersByPackage(qualifiedSuppliers);
						}
						
						for (ScQtEvaluationResultDtl qualSupplier 
												: qualifiedSuppliers) {
							QtEvaluationResultDtlBean recommendationBean = null;
							if (CollectionUtils.isNotEmpty(cachedSuppliers)) {
								recommendationBean = getCachedBeanFromList(cachedSuppliers, 
															qualSupplier.getEvaluationResultDtlId());
								if (recommendationBean != null) {
									qualSupplier.setShortListed(
											recommendationBean.isShortlisted());
									qualSupplier.setAwareded(
											recommendationBean.isAwarded());
								}
								
								if (qualSupplier.isAwareded()) {
									getManagedBean().setRecommendedSupplier(qualSupplier);
								} else {
									recommendationBean = updateCachedData(qualSupplier, 
											zoneId, selectedItemId);
								}
							} else if (qualSupplier.isAwareded()) {
								recommendationBean = updateCachedData(qualSupplier, 
										zoneId, selectedItemId);
								getManagedBean().setRecommendedSupplier(qualSupplier);
							} else {
								recommendationBean = updateCachedData(qualSupplier, 
										zoneId, selectedItemId);
							}
							
							if (selectedItem != null) {
								if (!selectedItem.isCancelled()) {
									calculateOfferedPrice(recommendationBean, selectedItem);
									qualSupplier.getQtEvaluationResult().setOfferedPrice(
													 recommendationBean.getOfferedPrice());
								} else {
									
									// Skip offered price or bid price calculation 
									// if item is cancelled
									BigDecimal zero = new BigDecimal(0.0);
									qualSupplier.setBidPrice(zero);
									qualSupplier.getQtEvaluationResult().setOfferedPrice(zero);
									qualSupplier.setAmountInWords(convertNumberToWord(zero));
								}
							} else { 
								
								// If QT is refinalized and finalization method is bidding award 
								// then calculate the total bid price
								if (getManagedBean().isRefinalizedAndBiddingAward() &&
										recommendationBean.getBidRank() != null) {
									
									List<ScQtRecommend> items = null;
									if (DEFAULT_ZONE.equals(zoneId)) {
										items = getManagedBean().getScQtFinalization()
															    .getScQtRecommends();
									} else {
										items = getManagedBean().getScQtFinalization()
											    	.getScQtRecommendsByZone().get(zoneId);									
									}
									calculateBidPrice(items, recommendationBean);
								}
							}
						}						
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
			log.error("zoneId : " + zoneId + "; itemId : " + itemId);
			return null;
		}
		
		List<QtEvaluationResultDtlBean> cachedSuppliers = null;
		Map<MapKey, List<QtEvaluationResultDtlBean>> cachedSupplierBeans = 
								getManagedBean().getQualifiedSuppliersCache();
		MapKey mapKey = new MapKey(zoneId, itemId);

		if (cachedSupplierBeans == null) {
			cachedSupplierBeans = new HashMap<MapKey, List<QtEvaluationResultDtlBean>>();
			cachedSuppliers = new ArrayList<QtEvaluationResultDtlBean>();
			cachedSupplierBeans.put(mapKey, cachedSuppliers);

			getManagedBean().setQualifiedSuppliersCache(cachedSupplierBeans);
		} else if (cachedSupplierBeans.containsKey(mapKey)) {
			cachedSuppliers = cachedSupplierBeans.get(mapKey);
		} else {
			cachedSuppliers = new ArrayList<QtEvaluationResultDtlBean>();
			cachedSupplierBeans.put(mapKey, cachedSuppliers);
		}
		
		return cachedSuppliers;
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
	
	private void processSuppliersBeforeSaving() {
		ScQt scQt = this.getManagedBean().getScQt();
		boolean isZonal = scQt.isZonal(); 
		boolean isItemized = true;
		List<TplSpecZoneDtl> zoneDtls = getManagedBean().getZoneDetails();
		String recommendTypeCode = getManagedBean().getScQtFinalization()
									.getRecommendedTypeId().getParameterCode();
		if (ParameterConst.RECOMMEND_TYPE_PACKAGE
						  .equals(recommendTypeCode)) {
			isItemized = false;
		}
		if (isItemized) {
			if (isZonal) {
				for (TplSpecZoneDtl zoneDtl : zoneDtls) {
					processSuppliersByItem(zoneDtl);
				}
			} else {
				processSuppliersByItem(null);
			}
		} else {
			if (isZonal) {
				for (TplSpecZoneDtl zoneDtl : zoneDtls) {
					processSuppliersByPackage(zoneDtl);
				}				
			} else {
				processSuppliersByPackage(null);
			}
		}
	}
	
	private void processSuppliersByItem(TplSpecZoneDtl zoneDtl) {
//		boolean isAward = true;
		Long specZoneId = null;
//		String finalizationMethodStr = null;
		List<QtEvaluationResultDtlBean> cachedSuppliers = null;
		
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		PmParameter finalizationMethod = getManagedBean().getScQtFinalization()
														 .getFinalizationMethod();
		List<ScQtRecommend> items = null;
//		if (finalizationMethod != null) {
//			finalizationMethodStr = finalizationMethod.getParameterCode();
//			
//			if (!ParameterConst.RECOMMEND_BY_AWARD.equals(
//					finalizationMethodStr) && !isQTRefinalized()) {
//				isAward = false;
//			}
//		} 
		if (finalizationMethod == null) {
			setZoneAndTabIndexOnException(zoneDtl);
			throw new EpException(MessageConst.ERROR_760211);
		}		
		
		if (zoneDtl != null) {
			specZoneId = zoneDtl.getSpecZoneId();
			items = scQtFinalization.getScQtRecommendsByZone()
									.get(specZoneId);
		} else {
			specZoneId = DEFAULT_ZONE;
			items = scQtFinalization.getScQtRecommends();
		}
		
		if (CollectionUtils.isNotEmpty(items)) {
			boolean isAward = getManagedBean().isSingleAward();
			
			// to set the recommend details for each item
			for (ScQtRecommend scQtRecommend : items) {
				Long itemId = scQtRecommend.getRequestItem().getRequestNoteItemId();
				
				List<ScQtEvaluationResultDtl> qualifiedSuppliers = null;
				if (CollectionUtils.isEmpty(cachedSuppliers)) {
					SuppFinalizationResDTO resDTO = retrieveQualifiedSuppliers(true, true, 
																		specZoneId, itemId);	
					qualifiedSuppliers = resDTO.getQualifiedSuppliersByEvalStage().getResultList();
				}
				cachedSuppliers = getCachedSuppliersFromMap(specZoneId, itemId);
				
				if (CollectionUtils.isNotEmpty(qualifiedSuppliers)) {
					for (ScQtEvaluationResultDtl evalResDtl 
									   : qualifiedSuppliers) {
						QtEvaluationResultDtlBean recommendedBean = getCachedBeanFromList(
									cachedSuppliers, evalResDtl.getEvaluationResultDtlId());
						if (recommendedBean == null) {
							if (isAward && evalResDtl.isShortListed()) {
								evalResDtl.setShortListed(false);
							} else if (!isAward && evalResDtl.isAwareded()) {
								evalResDtl.setAwareded(false);
							}
							updateCachedData(evalResDtl, specZoneId, itemId);
						}
					}
				}
				
				filterRecommendDtls(scQtRecommend, cachedSuppliers);
			}				
		}		
	}
	
	private void processSuppliersByPackage(TplSpecZoneDtl zoneDtl) {
		List<QtEvaluationResultDtlBean> cachedSuppliers = null;
		
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		PmParameter finalizationMethod = getManagedBean().getScQtFinalization()
														 .getFinalizationMethod();
		List<ScQtRecommend> items = null;
		if (finalizationMethod == null) {
			setZoneAndTabIndexOnException(zoneDtl);
			throw new EpException(MessageConst.ERROR_760211);
		}		
		
		Long specZoneId = null;
		if (zoneDtl != null) {
			specZoneId = zoneDtl.getSpecZoneId();
			items = scQtFinalization.getScQtRecommendsByZone()
									.get(specZoneId);
		} else {
			specZoneId = DEFAULT_ZONE;
			items = scQtFinalization.getScQtRecommends();
		}

		boolean isAward = getManagedBean().isSingleAward();
		SuppFinalizationResDTO resDTO = retrieveQualifiedSuppliers(
											true, true, specZoneId, null);	
		List<ScQtEvaluationResultDtl> qualifiedSuppliers = 
				resDTO.getQualifiedSuppliersByEvalStage().getResultList();
		
		List<QtEvaluationResultDtlBean> cachedSuppliersList = 
				getCachedSuppliersFromMap(specZoneId, DEFAULT_ITEM);
		
		if (CollectionUtils.isNotEmpty(qualifiedSuppliers)) {
			for (ScQtEvaluationResultDtl evalResDtl 
							   : qualifiedSuppliers) {
				QtEvaluationResultDtlBean recommendedBean = getCachedBeanFromList(
						cachedSuppliersList, evalResDtl.getEvaluationResultDtlId());
				
				if (recommendedBean == null) {
					if (isAward && evalResDtl.isShortListed()) {
						evalResDtl.setShortListed(false);
					} else if (!isAward && evalResDtl.isAwareded()) {
						evalResDtl.setAwareded(false);
					}
					updateCachedData(evalResDtl, specZoneId, DEFAULT_ITEM);
				}
			}
		}
		
		Map<MapKey,List<QtEvaluationResultDtlBean>> qualifiedSuppliersCache = 
									getManagedBean().getQualifiedSuppliersCache();
		if (MapUtils.isNotEmpty(qualifiedSuppliersCache)) {
			cachedSuppliers = qualifiedSuppliersCache.get(
									new MapKey(specZoneId, DEFAULT_ITEM));

			if (CollectionUtils.isNotEmpty(items)) {
				
				// to set the recommend details for each item
				for (ScQtRecommend scQtRecommend : items) {
					filterRecommendDtls(scQtRecommend, cachedSuppliers);
				}				
			}
		}
	}
	
	private void filterRecommendDtls(ScQtRecommend scQtRecommend,
									 List<QtEvaluationResultDtlBean> cachedSuppliers) {
		setItemHigherAuthorityReqStatus(scQtRecommend);	
		
		if (scQtRecommend.getRecommendDtls() == null) {
			scQtRecommend.setRecommendDtls(
					new ArrayList<ScQtRecommendDtl>());
		}
		if (scQtRecommend.getRemoveSupplierList() == null) {
			scQtRecommend.setRemoveSupplierList(
					new ArrayList<ScQtRecommendDtl>());
		}					
		
		boolean hasRecommendedSuppliers = false;
		if (CollectionUtils.isNotEmpty(cachedSuppliers)) {
			Set<ScQtRecommendDtl> toBeAddedSet = new HashSet<ScQtRecommendDtl>();
			hasRecommendedSuppliers = filterRecommendedSuppliersList(toBeAddedSet, 
						 						   cachedSuppliers, scQtRecommend);
		} 
		if (!hasRecommendedSuppliers && 
				CollectionUtils.isNotEmpty(
						scQtRecommend.getRecommendDtls())) {
			if (scQtRecommend.getRemoveSupplierList() == null) {
				scQtRecommend.setRemoveSupplierList(
						new ArrayList<ScQtRecommendDtl>());
			}
			
			Set<ScQtRecommendDtl> toBeAddedSet = new HashSet<ScQtRecommendDtl>();
			for (ScQtRecommendDtl recommendDtl 
						: scQtRecommend.getRecommendDtls()) {
				if (!recommendDtl.getRecordStatusFlg().equals(
							DbValueConst.RECORD_STATUS_ACTIVE)) {
					toBeAddedSet.add(recommendDtl);
				}	
				if (!recommendDtl.isBrandNew() && 
						!toBeAddedSet.contains(recommendDtl)) {
					recommendDtl.setDeleted(true);
					scQtRecommend.getRemoveSupplierList()
								 .add(recommendDtl);
				}
			}
			scQtRecommend.getRecommendDtls().clear();
			scQtRecommend.getRecommendDtls().addAll(toBeAddedSet);
		}			
	}
	
	private void resetSupplierAwardOrShortlistedFlag(Long zoneId, Long ItemId) {
		
	}
	
	private void checkAndResetPreviouslyAwardedSupplier(
							ScQtEvaluationResultDtl evalResultDtl,
							Long selectedZoneId, Long selectedItemId) {
		
		QtEvaluationResultDtlBean awardedSupplier = null;
		
//		Long zoneId = selectedZoneId == DEFAULT_ZONE ? null : selectedZoneId;
//		Long itemId = selectedItemId == DEFAULT_ITEM ? null : selectedItemId;
		
		List<QtEvaluationResultDtlBean> cachedSuppliersList = 
				getCachedSuppliersFromMap(selectedZoneId, selectedItemId);
		if (CollectionUtils.isNotEmpty(
				   cachedSuppliersList)) {
			for (QtEvaluationResultDtlBean evalResDtlBean
									: cachedSuppliersList) {
				if (evalResDtlBean.isAwarded()) {
					awardedSupplier = evalResDtlBean;
					break;
				}
			}
		} 
		
		// If the previously awarded supplier does not exist in the cache,
		// then check if an awarded supplier exists in the database
		if (awardedSupplier == null) {
			SuppFinalizationResDTO resDTO = retrieveQualifiedSuppliers(true, false, 
													selectedZoneId, selectedItemId);
			if (resDTO.isSuccessful()) {
				ISearchResultBean<ScQtEvaluationResultDtl> resultBean = 
									resDTO.getQualifiedSuppliersByEvalStage();
				if (CollectionUtils.isNotEmpty(
						resultBean.getResultList())) {
					ScQtEvaluationResultDtl previouslyAwardedSupplier = 
								resultBean.getResultList().get(0);	
					awardedSupplier = updateCachedData(previouslyAwardedSupplier, 
													   selectedZoneId, selectedItemId);
				}
			}
		}		
		
		QtEvaluationResultDtlBean selectedSupplier = 
						new QtEvaluationResultDtlBean();
		selectedSupplier.setQtEvalResDtlId(
				evalResultDtl.getEvaluationResultDtlId());

		if (awardedSupplier != null && 
				!awardedSupplier.equals(selectedSupplier)) {
			awardedSupplier.setAwarded(false);
			awardedSupplier.setShortlisted(false);
		}		
	}
	
}