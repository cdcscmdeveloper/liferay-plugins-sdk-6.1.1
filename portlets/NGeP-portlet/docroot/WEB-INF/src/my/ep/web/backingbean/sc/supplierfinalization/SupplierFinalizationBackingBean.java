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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.sc.SupplierQualificationBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
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
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalItem;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.sc.supplierfinalization.SupplierFinalizationManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;

/**
 * @author Faradiana
 * @author Praneeth
 * 
 * Supplier Finalization Backing Bean
 */
@ManagedBean(name = "suppFinalizationBackingBean")
@RequestScoped
public class SupplierFinalizationBackingBean extends SourcingBaseBackingBean<SupplierFinalizationManagedBean> {
	private static final Logger log = EpLogger.getLogger(SupplierFinalizationBackingBean.class);

	private int currentTab;
	private String textBeforeSign;
	
	public static final int ACTIVE_INDEX_PREPARATION = 0;
	public static final int ACTIVE_INDEX_REPORT = 1;
	public static final int ACTIVE_INDEX_SUPP_FINALIZATION = 2;
	public static final int ACTIVE_INDEX_ENDORSEMENT = 3;
	
	public static final int PREPARE_ACTIVE_INDEX_MEETING = 0;
	public static final int PREPARE_ACTIVE_INDEX_COMMITTEE = 1;
	public static final int PREPARE_ACTIVE_INDEX_JOIN_IN = 2;
	
	public static final int ONLINE_BIDDING_ACTIVE_INDEX_SUMMARY = 0;
	public static final int ONLINE_BIDDING_ACTIVE_INDEX_ENDORSEMENT = 1;
	
	public SupplierFinalizationBackingBean() {
		
	}
	
	public void preRenderView() {
		debugLogger("preRenderView");

		try {
			SCEvaluationDataType dataType = getBpmTaskDetails(SCEvaluationDataType.class);
			Long qtId = Long.valueOf(dataType.getQtId());
			String committeeType = String.valueOf(dataType.getCommitteeType());
			getManagedBean().setCommitteeType(committeeType);
			getManagedBean().setPayLoad(dataType);
			getManagedBean().setActiveIndex(0);
			
			//Long qtId = 2923L;
			//String committeeType = ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A;

			//renewSesionObject();
			retrieveQTDetails(qtId);
			retrieveSupplierFinalizationDtls(getManagedBean().getScQt());
			retrievePreparationDtls(qtId, committeeType);

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.FC_SUPPLIER_FINALIZATION_PAGE);
		} catch (Exception e) {
			handleException(e);
		}
	}
	
	public void retrieveQTDetails(Long qtId) {
		IQTSuppFinalizationSO qtSuppFinalizationSO;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(qtId);
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.retrieveQTDtls(reqDTO);
			ScQt scQt = resDTO.getScQt();
			populateParentOrgProfile(scQt);
			getManagedBean().setScQt(scQt);
			
			List<TplSpecZoneDtl> zoneDetails = resDTO.getZoneDetails();
			getManagedBean().setZoneDetails(zoneDetails);
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	//Supplier Finalization Tab Menu
	public void onTabMenuChangeAction(int tabIndex) {
//		log.info("Tab Index :: " + tabIndex);		
		
//		String tabTitle = event.getTab().getTitle();
		//int previousIndex = this.getManagedBean().getActiveIndex();
		//validateInfo(previousIndex, false);
		
		if (tabIndex == 0) {
			preparationRetrival();
		} else if (tabIndex == 1) {
			biefingPaperRetrival();
		} else if (tabIndex == 2) {
			supplierFinalizationRetrival();
		} else if (tabIndex == 3) {
			committeeEndorsementRetrival();
		}		
		
		//Meeting Details
//		List<ScQtMeeting> meetingList = getManagedBean().getMeetingList();
//		getManagedBean().setMeetingList(meetingList);
	}
	
	private void preparationRetrival(){
		this.getManagedBean().setActiveIndex(ACTIVE_INDEX_PREPARATION);
		this.getManagedBean().setPrepareActiveIndex(PREPARE_ACTIVE_INDEX_MEETING);
		
		Long qtId = getManagedBean().getScQt().getQtId();
		String committeeType = getManagedBean().getCommitteeType();
		retrievePreparationDtls(qtId,committeeType);
	}
	
	private void biefingPaperRetrival(){
		this.getManagedBean().setActiveIndex(ACTIVE_INDEX_REPORT);
		retrieveSupplierFinalizationDtls(getManagedBean().getScQt());
		retrieveBriefingPaper();
	}
	
	private void supplierFinalizationRetrival(){
		this.getManagedBean().setActiveIndex(ACTIVE_INDEX_SUPP_FINALIZATION);
		
		ScQt scQt = getManagedBean().getScQt();
		retrieveSupplierFinalizationDtls(scQt);
		if (scQt.isZonal()) {
			populateShortListedSuppliersByZone();
			populateScQtRecommendsByZone();
		} else {
			this.getManagedBean().setItemList(
					this.getManagedBean().getScQtFinalization().getScQtRecommends());
		}
		decisionEvent();
		populateRecommendTypeEventDetails(false);
		fwdHigherAuthorityEvent();
		packageRecommendByEvent();
		
		ScQtCommittee scQtCommittee = this.getManagedBean().getScQtCommittee();
		
		// Retrieve committee remarks
		List<ScQtCommitteRemarks> remarks = initializeRemarks(
				scQtCommittee, null, scQtCommittee.getQtCommitteRemarks(), ScQtCommitteRemarks.class);
		scQtCommittee.setQtCommitteRemarks(remarks);
		
		// Committee Remarks list 
		this.getManagedBean().setCommitteeRemarkList(remarks);
	}
	
	private void committeeEndorsementRetrival(){
		this.getManagedBean().setActiveIndex(ACTIVE_INDEX_ENDORSEMENT);
		retrieveCommitteeEndorsement();
	}
	
	/*private void onTabMenuValidationChangeAction(Integer previousIndex){
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			
			switch (previousIndex) {
			case 0:
				//Preparation Tab
				ScQtCommittee scQtCommittee = preparationDtlsInfo();
				reqDTO.setScQtCommittee(scQtCommittee);
				
				qtSuppFinalizationSO.validatePreparation(reqDTO);
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			}
		}finally{
		}
	}*/
	
	public void retrievePreparationDtls(Long qtId, String committeeType) {
		debugLogger("..In retrievePreparationDtls()..");
		IQTMeetingSO meetingSO = null;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteType(committeeType);
			
			MeetingResDTO resDTO = meetingSO.retrieveMeetingDls(reqDTO);
			if (resDTO.isSuccessful()) {
				//Meeting Details
				List<ScQtMeeting> meetingList = resDTO.getMeetingDtls();
				if (CollectionUtils.isNotEmpty(meetingList)) {
					for (ScQtMeeting scQtMeeting : meetingList) {
						Long meetingStatusId = scQtMeeting.getMeetingStatus() != null ? scQtMeeting.getMeetingStatus().getStatusId() : null;
						scQtMeeting.setMeetingStatusStr(meetingStatusId);
					}
				}
				
				this.getManagedBean().setMeetingList(meetingList);
				
				ScQtCommittee scQtCommittee = new ScQtCommittee();
				scQtCommittee = resDTO.getQtCommittee();
				this.getManagedBean().setScQtCommittee(scQtCommittee);
				
				//Committee Details
				if (scQtCommittee.getCommitteeActivities() != null && !scQtCommittee.getCommitteeActivities().isEmpty()) {
					this.getManagedBean().setCommitteeList(scQtCommittee.getCommitteeActivities());
				}
				
				if (null != resDTO.getQtCommittee().getCommitteeActivities()
							&& !resDTO.getQtCommittee().getCommitteeActivities().isEmpty()) {
					Collections.sort(resDTO.getQtCommittee().getCommitteeActivities());
					this.getManagedBean().setCommitteeList(resDTO.getQtCommittee().getCommitteeActivities());
				} else {
					ScCommittee sccommittee = resDTO.getQtCommittee().getScCommittee();
					List<ScQtCommitteeActivity> localList = new ArrayList<ScQtCommitteeActivity>();
					if (null != sccommittee.getScCommitteeMembers()
							&& !sccommittee.getScCommitteeMembers().isEmpty()) {
						for (ScCommitteeMember scCommitteeMember : sccommittee.getScCommitteeMembers()) {
							ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
							activity.setScQtCommittee(this.getManagedBean().getScQtCommittee());
							activity.setScCommitteeMember(scCommitteeMember);
							setUserDetails(activity);
							localList.add(activity);
						}
					}
					Collections.sort(localList);
					this.getManagedBean().setCommitteeList(localList);
				}
				
				//Meeting Attendance Details
				if (scQtCommittee.getMeetingAttendances() != null && !scQtCommittee.getMeetingAttendances().isEmpty()) {
					this.getManagedBean().setMeetingAttendanceList(scQtCommittee.getMeetingAttendances());
				}

				ScQtCommittee committee = this.getManagedBean().getScQtCommittee();
				getManagedBean().setScQtCommittee(committee);
				
				List<ScQtCommitteRemarks> remarks = initializeRemarks(
						committee, null, committee.getQtCommitteRemarks(), ScQtCommitteRemarks.class);
				committee.setQtCommitteRemarks(remarks);
				
				// Committee Remarks list 
				this.getManagedBean().setCommitteeRemarkList(remarks);
			}
			retrieveCommitteeType(committeeType);
		} finally {
			meetingSO = null;
		}
	}
	
	private void retrieveCommitteeType(String committeeType) {
		try {
			String label = "";
			
			if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A)){
				label = getMessageCommon("lbl.sc.committee.qca");
			
			}else if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B)){
				label = getMessageCommon("lbl.sc.committee.qcb");
			
			}else if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)){
				label = getMessageCommon("lbl.sc.committee.pba");
			
			}else if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)){
				label = getMessageCommon("lbl.sc.committee.pbb");
			
			}else if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)){
				label = getMessageCommon("lbl.sc.committee.fpb.sbh");
			
			}else if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)){
				label = getMessageCommon("lbl.sc.committee.fpb.swk");
			}
			this.getManagedBean().setCommitteeTabLabel(label);

		} finally {
		}
	}
	
	public void retrieveMeetingDtls() {
		debugLogger("..In retrieveMeetingDtls()..");
		try {
			this.getManagedBean().setPrepareActiveIndex(PREPARE_ACTIVE_INDEX_MEETING);
			
			//Meeting Details
			List<ScQtMeeting> meetingList = getManagedBean().getMeetingList();
			getManagedBean().setMeetingList(meetingList);
		} finally {
		}
	}
	
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
			
			IStatusSO statussO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			StatusReqDTO statusreq = new StatusReqDTO();
			statusreq.setStatusId(QTStatusConst.MS_PENDING);
			StatusResDTO statusres = statussO.retrieveStatusByStatusId(statusreq);
			meeting.setMeetingStatus(statusres.getPmStatus());
			
			meetingList.add(meeting);
			this.getManagedBean().setMeetingList(meetingList);
		} finally {
		
		}
	}
	
	public void deleteMeetingDetails(List<ScQtMeeting> selectedMeetingList) {
		try {
			List<ScQtMeeting> meetingList = getManagedBean().getMeetingList();
			List<ScQtMeeting> deletedMeetingList = getManagedBean().getDeletedMeetingList();
			if (deletedMeetingList == null){
				getManagedBean().setDeletedMeetingList(new ArrayList<ScQtMeeting>());
			}
			
			List<ScQtMeeting> tempDeletedList = new ArrayList<ScQtMeeting>();
			for (ScQtMeeting meeting : selectedMeetingList){
				meeting.setDeleted(true);
				tempDeletedList.add(meeting);
			}
			getManagedBean().setDeletedMeetingList(tempDeletedList);
			
			deleteRowsAction(meetingList, tempDeletedList);
		} finally {
		}		
	}
	
	public void deleteMeetingAttendanceDetails(List<ScQtMeetingAttendance> selectedMeetingAttendanceList) {
		try {
			List<ScQtMeetingAttendance> meetingAttendanceList = 
						getManagedBean().getMeetingAttendanceList();
			deleteRowsAction(meetingAttendanceList, selectedMeetingAttendanceList);
			this.getManagedBean().setSelectedMeetingAttList(null);
		} finally {
		}		
	}	
	
	public void changeMeetingStatus(ScQtMeeting scQtMeeting) {
		IStatusSO statusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(scQtMeeting.getMeetingStatusStr());
		
		StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
		scQtMeeting.setMeetingStatus(statusResDTO.getPmStatus());
	}
	
	public void retrieveCommitteeDtls() {
		debugLogger("..In retrieveCommitteeDtls()..");
		try {
			this.getManagedBean().setPrepareActiveIndex(PREPARE_ACTIVE_INDEX_COMMITTEE);
			
			//Committee Details
			List<ScQtCommitteeActivity> committeeList = getManagedBean().getCommitteeList();
			getManagedBean().setCommitteeList(committeeList);
		} finally {
		}
	}
	
	public void retrieveMeetingAttendanceDtls() {
		debugLogger("..In retrieveMeetingAttendanceDtls()..");
		try {
			this.getManagedBean().setPrepareActiveIndex(PREPARE_ACTIVE_INDEX_JOIN_IN);
			
			//Meeting Details
			List<ScQtMeeting> meetingList = getManagedBean().getMeetingList();
			getManagedBean().setMeetingList(meetingList);
		} finally {
		}
	}
	
	public void addMeetingAttendance() {
		debugLogger("..In addMeetingAttendance()..");
		try {
			List<ScQtMeetingAttendance> meetingAttendanceList = getManagedBean().getMeetingAttendanceList();
			if (null == meetingAttendanceList) {
				meetingAttendanceList = new ArrayList<ScQtMeetingAttendance>();
			}
             
			ScQtMeetingAttendance meeting = new ScQtMeetingAttendance();
			meeting.setQtCommittee(this.getManagedBean().getScQtCommittee());
			setUserDetails(meeting);
			meetingAttendanceList.add(meeting);

			this.getManagedBean().setMeetingAttendanceList(meetingAttendanceList);
		} finally {
		
		}
	}
	
	public void saveSupplierFinalization() {
		debugLogger("..In saveSupplierFinalization()..");
		
		this.getManagedBean().setSubmit(false);
		
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		int activeIndex = this.getManagedBean().getActiveIndex();
		int prepareActiveIndex = this.getManagedBean().getPrepareActiveIndex();
		
		String taskId = this.getManagedBean().getTaskId();
		String[] outComes = this.getManagedBean().getOutComes();
		String processName = this.getManagedBean().getProcessName();
		Long currentStatusId = this.getManagedBean().getCurrentStatusId();
		
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			
			SuppFinalizationReqDTO reqDTO = validateInfo(activeIndex);
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.saveSupplierFinalization(reqDTO);
			
			renewSessionAndRetrieve(activeIndex, resDTO);			
			setTaskDetails(activeIndex, prepareActiveIndex, taskId, outComes, processName, currentStatusId);
			
			String qtNo = getManagedBean().getScQt().getQtNo().toString();
			String desc = getParameterDesc( ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_700004, new String[] {desc,qtNo},
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	private SuppFinalizationReqDTO validateInfo(int index){
		debugLogger("..In validateInfo()..");
		
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
			
			switch (index) {
			case 0:
				//Preparation Tab
				ScQtCommittee scQtCommittee = preparationDtlsInfo();
				reqDTO.setDeletedMeetingsList(getManagedBean().getDeletedMeetingList());
				reqDTO.setScQtCommittee(scQtCommittee);
				
				qtSuppFinalizationSO.validatePreparation(reqDTO);
				reqDTO.setScQtFinalization(scQtFinalization);
				break;

			case 1: 
				//Briefing Paper Tab 
				//no validation
				//scQtFinalization.set attachment
				scQtFinalization = supplierFinalizationInfo(scQtFinalization);
				reqDTO.setScQtFinalization(scQtFinalization);
				break;
				
			case 2:
				//Supplier Finalization Tab
				scQtFinalization = supplierFinalizationInfo(scQtFinalization);
				String decisionTypeCode = scQtFinalization.getDecision().getParameterCode();
				if (ParameterConst.DECISION_RECOMMEND_SUPPLIER.equals(decisionTypeCode)) {
					String recommendTypeCode = scQtFinalization.getRecommendedTypeId().getParameterCode();
					if (recommendTypeCode.equals(ParameterConst.RECOMMEND_TYPE_PACKAGE)) {
						saveByPackage(scQtFinalization);
					} else if (recommendTypeCode.equals(ParameterConst.RECOMMEND_TYPE_ITEM)) {
						saveByItem(scQtFinalization);
					}
				} 
				// TODO : To delete recommend details for each item [Uncomment if needed]
				/*				
 				else {
					deleteRecommendDetails(scQtFinalization);
				}*/
				
				ScQtCommittee _scQtCommittee = this.getManagedBean().getScQtCommittee();
				List<ScQtCommitteRemarks> remarksList = finalizeRemarks(this.getManagedBean().getCommitteeRemarkList());
				_scQtCommittee.setQtCommitteRemarks(remarksList);
				setUserDetails(_scQtCommittee);
				
				reqDTO.setScQtCommittee(_scQtCommittee);
				reqDTO.setScQtFinalization(scQtFinalization);
				qtSuppFinalizationSO.validateSupplierFinalization(reqDTO);
				break;
				
			case 3: 
				//Committee Endorsement Tab
				reqDTO.setScQtFinalization(scQtFinalization);
				break;
			}
			return reqDTO;
		} finally {
			
		}
	}
	
	private void renewSessionAndRetrieve(int activeIndex, SuppFinalizationResDTO resDTO) {
		Long qtId = getManagedBean().getScQt().getQtId();
		String committeeType = getManagedBean().getCommitteeType();
		ScQtCommittee scQtCommittee = resDTO.getScQtCommittee() != null 
										? resDTO.getScQtCommittee() : getManagedBean().getScQtCommittee();
		SCEvaluationDataType dataType = (SCEvaluationDataType) getManagedBean().getPayLoad();
		TplSpecZoneDtl selectedZone = getManagedBean().getTplSpecZoneDtl();
		int zoneTabActiveIndex = getManagedBean().getZoneActiveIndex();
		
		renewSesionObject();
		
		getManagedBean().setCommitteeType(committeeType);
		getManagedBean().setScQtCommittee(scQtCommittee);
		getManagedBean().setTplSpecZoneDtl(selectedZone);
		getManagedBean().setPayLoad(dataType);
		getManagedBean().setZoneActiveIndex(zoneTabActiveIndex);
		
		retrieveQTDetails(qtId);
		
		switch (activeIndex) {
			case 0:
				preparationRetrival();
				ScQt scQt = getManagedBean().getScQt();
				retrieveSupplierFinalizationDtls(scQt);
				break;
			case 1:
				biefingPaperRetrival();
				break;
			case 2:
				supplierFinalizationRetrival();
				break;
			case 3:
				committeeEndorsementRetrival();
				break;
		}
	}
	
	private void setTaskDetails(Integer activeIndex, Integer prepareActiveIndex, String taskId, 
								String[] outComes, String processName, Long currentStatusId) {
		if(activeIndex != null){
			this.getManagedBean().setActiveIndex(activeIndex);
		}
		if(prepareActiveIndex != null){
			this.getManagedBean().setPrepareActiveIndex(prepareActiveIndex);
		}
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setOutComes(outComes);
		this.getManagedBean().setProcessName(processName);
		this.getManagedBean().setCurrentStatusId(currentStatusId);
	}
	
	private ScQtCommittee preparationDtlsInfo(){
		debugLogger("..In preparationDtlsInfo()..");
		
		ScQtCommittee scQtCommittee = getManagedBean().getScQtCommittee();
		setUserDetails(scQtCommittee);
		
		//Meeting Details
		List<ScQtMeeting> meetingList = getManagedBean().getMeetingList();
		if (meetingList != null && !meetingList.isEmpty()) {
			scQtCommittee.setScQtMeetings(new ArrayList<ScQtMeeting>());
			scQtCommittee.setScQtMeetings(meetingList);
		}
		
		//Committee Details
		List<ScQtCommitteeActivity> committeeList = getManagedBean().getCommitteeList();
		if (committeeList != null && !committeeList.isEmpty()) {
			scQtCommittee.setCommitteeActivities(new ArrayList<ScQtCommitteeActivity>());
			scQtCommittee.setCommitteeActivities(committeeList);
		}

		List<ScQtCommitteRemarks> remarksList = finalizeRemarks(this.getManagedBean().getCommitteeRemarkList());
		scQtCommittee.setQtCommitteRemarks(remarksList);
		
		//Meeting Attendance Details
		List<ScQtMeetingAttendance> meetingAttendanceList = getManagedBean().getMeetingAttendanceList();
		if (meetingAttendanceList != null && !meetingAttendanceList.isEmpty()) {
			if(scQtCommittee.getMeetingAttendances() == null){
				scQtCommittee.setMeetingAttendances(new ArrayList<ScQtMeetingAttendance>());
			}
			scQtCommittee.setMeetingAttendances(meetingAttendanceList);
		}
		
		return scQtCommittee;
	}
	
	private ScQtFinalization supplierFinalizationInfo(ScQtFinalization scQtFinalization){
		debugLogger("..In supplierFinalizationInfo()..");
		
		if(scQtFinalization == null){
			scQtFinalization = new ScQtFinalization();
		}
		setUserDetails(scQtFinalization);
		scQtFinalization.setScQt(getManagedBean().getScQt());
		
		//reset the value base on the decision before save
		String decision = scQtFinalization.getDecision().getParameterCode();
		if(decision == null){
			resetRecommendSupplier(scQtFinalization);
			resetReEvaluate(scQtFinalization);
			resetRepublish(scQtFinalization);
			resetCancel(scQtFinalization);
			
		}else if(decision.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)){
			resetReEvaluate(scQtFinalization);
			resetRepublish(scQtFinalization);
			resetCancel(scQtFinalization);
			
		}else if(decision.equals(ParameterConst.DECISION_RE_EVALUATION)){
			resetRecommendSupplier(scQtFinalization);
			resetRepublish(scQtFinalization);
			resetCancel(scQtFinalization);
			
		}else if(decision.equals(ParameterConst.DECISION_RE_PUBLISH)){
			resetRecommendSupplier(scQtFinalization);
			resetReEvaluate(scQtFinalization);
			resetCancel(scQtFinalization);
			
		}else if(decision.equals(ParameterConst.DECISION_CANCEL)){
			resetRecommendSupplier(scQtFinalization);
			resetReEvaluate(scQtFinalization);
			resetRepublish(scQtFinalization);
		}
		return scQtFinalization;
	}
	
	private void deleteRecommendDetails(ScQtFinalization scQtFinalization) {
		List<ScQtRecommend> scQtRecommendList = scQtFinalization.getScQtRecommends();
		if (CollectionUtils.isNotEmpty(scQtRecommendList)) {
			for (ScQtRecommend scQtRecommend : scQtRecommendList) {
				List<ScQtRecommendDtl> removedList = scQtRecommend.getRemoveSupplierList();
				List<ScQtRecommendDtl> recommendDtlList = scQtRecommend.getRecommendDtls();
				if (CollectionUtils.isNotEmpty(recommendDtlList)) {
					if (removedList == null) {
						removedList = new ArrayList<ScQtRecommendDtl>();
					}
					for (ScQtRecommendDtl recommendDtl : recommendDtlList) {
						if (!recommendDtl.isBrandNew()) {
							recommendDtl.setDeleted(true);
							removedList.add(recommendDtl);
						}
					}
				}
				scQtRecommend.setRecommendDtls(null);
			}
		}
		
	}
	
	private void recommendSupplier(ScQtFinalization scQtFinalization){
		//PmParameter recommendType = getPmParameterByTypeAndCode(ParameterTypeConst.SC_RECOMMEND_TYPE, getManagedBean().getRecommendType());
		//scQtFinalization.setRecommendedTypeId(recommendType);
		
		//scQtFinalization.setLoiLoaBy(getManagedBean().getLoiLoaPreparedBy());
		//scQtFinalization.setOnlineBiddingEndorse(getManagedBean().getBiddingEndorsement());
		
		//scQtFinalization.setHigherAuthReq(getManagedBean().getFwdHigherAuthority().equalsIgnoreCase("true")?true:false);
		//scQtFinalization.setHigherCommitteStr(getManagedBean().getFinalizationCommittee());
		//scQtFinalization.setHigherSecretary(getManagedBean().getCommitteeSecretary());
		//scQtFinalization.setReasonStr(getManagedBean().getReason());
	}
	
	private void saveByPackage(ScQtFinalization scQtFinalization){
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		boolean isSubmit = this.getManagedBean().isSubmit();
		TplSpecZoneDtl zoneDtl = this.getManagedBean().getTplSpecZoneDtl();
		String recommendByStr = getManagedBean().getRecommendBy();
		if (StringUtils.isBlank(recommendByStr)) {
			setZoneAndTabIndexOnException(zoneDtl);
			throw new EpException(MessageConst.ERROR_760211);
		}
		
		List<ScQtRecommend> toBeValidatedRecommendList = null;
		if (isZonal && isSubmit) {
			toBeValidatedRecommendList = new ArrayList<ScQtRecommend>();
		}
		List<ScQtRecommend> scQtRecommendList = new ArrayList<ScQtRecommend>();
		scQtRecommendList.addAll(scQtFinalization.getScQtRecommends());
		PmParameter recommendBy = getPmParameterByTypeAndCode(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, recommendByStr);
		ScQtEvaluationResult scQtEvaluationResult = this.getManagedBean().getRecommendedSupplier();
		
		if (scQtEvaluationResult != null) {
			
			//award
			SmSupplier smSupplier = scQtEvaluationResult.getScQtProposal().getScQtSupplier().getSmSupplier();
			for (ScQtRecommend scQtRecommend : scQtRecommendList) {
				if (isZonal && 
						!(scQtRecommend.getTplSpecZoneDtl().getSpecZoneId().equals(
								zoneDtl.getSpecZoneId()))) {
					if (isSubmit) {
						toBeValidatedRecommendList.add(scQtRecommend);
					}
					continue;
				}
				boolean alreadyExists = false;
				ScQtRecommendDtl selectedRecommendDtl = null;
				BigDecimal awardQty = scQtRecommend.getRequestItem().getQuantity();
				for (ScQtRecommendDtl recommendDtl : scQtRecommend.getRecommendDtls()) {
					if ((smSupplier.getSupplierId())
							.equals(recommendDtl.getSupplier().getSupplierId())) {
						scQtRecommend.getRecommendDtls().remove(recommendDtl);
						alreadyExists = true;
						selectedRecommendDtl = recommendDtl;
						selectedRecommendDtl.setAwardQty(awardQty);
						break;
					}
				}
				if (scQtRecommend.getRemoveSupplierList() == null) {
					scQtRecommend.setRemoveSupplierList(new ArrayList<ScQtRecommendDtl>());
				}
				for (ScQtRecommendDtl recommendDtl : scQtRecommend.getRecommendDtls()) {
					if (!recommendDtl.isBrandNew()) {
						recommendDtl.setDeleted(true);
						scQtRecommend.getRemoveSupplierList().add(recommendDtl);
					}
				}
				if (!alreadyExists) {
					selectedRecommendDtl = setSelectedSupplier(scQtEvaluationResult, scQtRecommend, awardQty, 
															   scQtRecommend.getRemoveSupplierList());
					setUserDetails(selectedRecommendDtl);
				} 
				scQtRecommend.setRecommendBy(recommendBy);
				scQtRecommend.setRecommendByStr(recommendBy.getParameterCode());
				scQtRecommend.getRecommendDtls().clear();
				scQtRecommend.getRecommendDtls().add(selectedRecommendDtl);	
			}
		} else {
			//multiple LOI or Online Bidding
			
			//to set the recommend details for each recommend
			for (ScQtRecommend scQtRecommend : scQtRecommendList) {
				if (isZonal && 
						!(scQtRecommend.getTplSpecZoneDtl().getSpecZoneId().equals(
								zoneDtl.getSpecZoneId()))) {
					if (isSubmit) {
						toBeValidatedRecommendList.add(scQtRecommend);
					}
					continue;
				}	
				List<ScQtEvaluationResult> recommendedSupplierList = this.getManagedBean().getRecommendedSupplierList();
				if (CollectionUtils.isNotEmpty(recommendedSupplierList)) {
					if (scQtRecommend.getRecommendDtls() == null) {
						scQtRecommend.setRecommendDtls(new ArrayList<ScQtRecommendDtl>());
					}						
					ScQtRecommendDtl selectedRecommendDtl = null;
					BigDecimal awardQty = scQtRecommend.getRequestItem().getQuantity();
					List<ScQtRecommendDtl> toBeAddedList = new ArrayList<ScQtRecommendDtl>();
					for (ScQtEvaluationResult recomendedSupplier : recommendedSupplierList) {
						boolean alreadyExists = false; 
						List<ScQtRecommendDtl> removedList = scQtRecommend.getRemoveSupplierList();
						for (ScQtRecommendDtl detail : scQtRecommend.getRecommendDtls()) {
							if ((detail.getSupplier().getSupplierId())
									.equals(recomendedSupplier.getScQtProposal().getScQtSupplier()
											.getSmSupplier().getSupplierId())) {
								alreadyExists = true;
								checkAndRemoveFromDeletedList(removedList, detail);
								detail.setAwardQty(scQtRecommend.getRequestItem().getQuantity());
								setUserDetails(detail);
								toBeAddedList.add(detail);										
								break;
							}
						}
						if (!alreadyExists) {
							selectedRecommendDtl = setSelectedSupplier(recomendedSupplier, scQtRecommend, awardQty,
																	   scQtRecommend.getRemoveSupplierList());	
							setUserDetails(selectedRecommendDtl);
							toBeAddedList.add(selectedRecommendDtl);
						}
					}
					if (scQtRecommend.getRemoveSupplierList() == null) {
						scQtRecommend.setRemoveSupplierList(new ArrayList<ScQtRecommendDtl>());
					}
					for (ScQtRecommendDtl recommendDtl : scQtRecommend.getRecommendDtls()) {
						if (!recommendDtl.isBrandNew() && 
								!toBeAddedList.contains(recommendDtl)) {
							recommendDtl.setDeleted(true);
							scQtRecommend.getRemoveSupplierList().add(recommendDtl);
						}
					}
					scQtRecommend.getRecommendDtls().clear();
					scQtRecommend.getRecommendDtls().addAll(toBeAddedList);
				}
				scQtRecommend.setRecommendByStr(recommendByStr);
			}
		}
		if (isSubmit && isZonal && 
				CollectionUtils.isNotEmpty(toBeValidatedRecommendList)) {
			validateAwardOfItemsToSuppliersByPackage(scQtRecommendList);
		}
		scQtFinalization.setScQtRecommends(scQtRecommendList);
	}
	
	private ScQtRecommendDtl setSelectedSupplier(ScQtEvaluationResult scQtEvaluationResult, ScQtRecommend scQtRecommend,
												 BigDecimal awardQty, List<ScQtRecommendDtl> removedList) {
		SmSupplier supplier = scQtEvaluationResult.getScQtProposal().getScQtSupplier().getSmSupplier();
		if (CollectionUtils.isNotEmpty(removedList)) {
			for (ScQtRecommendDtl removedDetail : removedList) { 
				if ((supplier.getSupplierId())
						.equals(removedDetail.getSupplier().getSupplierId())) {
					removedDetail.setDeleted(false);
					removedDetail.setAwardQty(awardQty);
					removedList.remove(removedDetail);
					removedDetail.setShortlistedSupplier(scQtEvaluationResult);
					return removedDetail;
				}
			}
		}
		ScQtRecommendDtl recommendDtl = new ScQtRecommendDtl();
		recommendDtl.setAwardQty(awardQty);
		recommendDtl.setSupplier(supplier);
		recommendDtl.setScQtProposalAmount(scQtEvaluationResult.getScQtProposal().getProposalAmount());
		recommendDtl.setShortlistedSupplier(scQtEvaluationResult);
		recommendDtl.setScQtRecommend(scQtRecommend);
		
		return recommendDtl;
	}
	
	private void checkAndRemoveFromDeletedList(List<ScQtRecommendDtl> deletedList, ScQtRecommendDtl recommendDtl) {
		if (CollectionUtils.isNotEmpty(deletedList)) {
			List<ScQtRecommendDtl> excludedList = new ArrayList<ScQtRecommendDtl>();
			for (ScQtRecommendDtl detail : deletedList) {
				Long supplierId = recommendDtl.getSupplier().getSupplierId();
				if (supplierId.equals(detail.getSupplier().getSupplierId())) {
					detail.setDeleted(false);
					recommendDtl = detail;
					excludedList.add(detail);
				}
			}
			if (CollectionUtils.isNotEmpty(excludedList)) {
				deletedList.removeAll(excludedList);
			}
		}
	}
	
	private void saveByItem(ScQtFinalization scQtFinalization){
		boolean isSubmit = this.getManagedBean().isSubmit();
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		TplSpecZoneDtl selectedZone = this.getManagedBean().getTplSpecZoneDtl();
		List<ScQtRecommend> scQtRecommendList = new ArrayList<ScQtRecommend>();
		scQtRecommendList.addAll(scQtFinalization.getScQtRecommends());
		
		//to set the recommend details for each recommend
		for (ScQtRecommend scQtRecommend : scQtRecommendList) {
			if (isZonal && 
					!(scQtRecommend.getTplSpecZoneDtl().getSpecZoneId()
							.equals(selectedZone.getSpecZoneId())) && !isSubmit) {
				continue;
			}
			validateAwardOfItem(scQtRecommend);
			
			List<ScQtRecommendDtl> removedList = scQtRecommend.getRemoveSupplierList();
			for (ScQtRecommendDtl scQtRecommendDtl : scQtRecommend.getRecommendDtls()) {
				if (!ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD
						.equals(scQtRecommend.getRecommendByStr())) {
					scQtRecommendDtl.setAwardQty(scQtRecommend.getRequestItem().getQuantity());
				}
				checkAndRemoveFromDeletedList(removedList, scQtRecommendDtl);
				setUserDetails(scQtRecommendDtl);
			}
		}
		scQtFinalization.setScQtRecommends(scQtRecommendList);		
	}
	
	public void submitSuppFinalization(String outcome) {
		debugLogger("submitSuppFinalization");
		this.getManagedBean().setAction(outcome);
		this.getManagedBean().setSubmit(true);
		
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		
		int activeIndex = this.getManagedBean().getActiveIndex();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		List<ScQtMeeting> meetingList = this.getManagedBean().getMeetingList();
		
		if (activeIndex == 2) {
			try {
				qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, 
						IQTSuppFinalizationSO.class);
			} finally {
			}
		}
		
		if (CollectionUtils.isNotEmpty(meetingList)) {
			for (ScQtMeeting meeting : meetingList) {
				if ( (meeting.getMeetingStatus() == null)
						|| !(QTConstants.SC_MEETING_CANCELLED_STATUS_ID.equals(meeting.getMeetingStatus().getStatusId()) 
								|| QTConstants.SC_MEETING_DONE_STATUS_ID.equals(meeting.getMeetingStatus().getStatusId())) ) {
					
					// Check and navigate to Preparation tab
					if (activeIndex == 2) {
						SuppFinalizationReqDTO reqDTO = validateInfo(activeIndex);
						qtSuppFinalizationSO.saveSupplierFinalization(reqDTO);
					}
					if (activeIndex != 0) {
						preparationRetrival();
						supplierFinalizationRetrival();
						this.getManagedBean().setActiveIndex(ACTIVE_INDEX_PREPARATION);
					}
					qtSuppFinalizationSO = null;
					throw new EpException(MessageConst.ERROR_760208);
				}
			}
		}
		
		if (ParameterConst.DECISION_RECOMMEND_SUPPLIER.equals(scQtFinalization.getDecision().getParameterCode()) 
				&& !scQtFinalization.isHigherAuthReq()) {
			boolean supplierSelected = true;
			String recommendTypeCode = scQtFinalization.getRecommendedTypeId().getParameterCode();
			

/*			if (QTConstants.SF_SINGLE.equals(getManagedBean().getSelectionMode())
					&& getManagedBean().getRecommendedSupplier() == null) {
				supplierSelectionFlag = false;
			} else if (QTConstants.SF_MULTIPLE.equals(getManagedBean().getSelectionMode())) {
				if (recommendTypeCode.equals(ParameterConst.RECOMMEND_TYPE_PACKAGE)
						&& CollectionUtils.isEmpty(getManagedBean().getRecommendedSupplierList())) {
					supplierSelectionFlag = false;
				} else if (recommendTypeCode.equals(ParameterConst.RECOMMEND_TYPE_ITEM)
								&& CollectionUtils.isEmpty(getManagedBean().getRecommendedItemSupplierList())) {
					supplierSelectionFlag = false;
				}
			}*/
			
			// Check if supplier has been selected before submission
			if (ParameterConst.RECOMMEND_TYPE_PACKAGE.equals(recommendTypeCode)) {
				if (QTConstants.SF_SINGLE.equals(getManagedBean().getSelectionMode())
						&& getManagedBean().getRecommendedSupplier() == null) {
					supplierSelected = false;
				} else if (QTConstants.SF_MULTIPLE.equals(getManagedBean().getSelectionMode())
								&& CollectionUtils.isEmpty(getManagedBean().getRecommendedSupplierList())) {
					supplierSelected = false;
				}
			}
			/* else if (recommendTypeCode.equals(ParameterConst.RECOMMEND_TYPE_ITEM)
							&& CollectionUtils.isEmpty(getManagedBean().getRecommendedItemSupplierList())) {
				supplierSelectionFlag = false;
			}*/
			
			if (!supplierSelected) {
				if (activeIndex != 2) {
					supplierFinalizationRetrival();
				}
				qtSuppFinalizationSO = null;
				throw new EpException(MessageConst.ERROR_760206);
			}	
			
			if (activeIndex != 2) {
				// By default validate SupplierFinalization Tab content
				validateInfo(2);
			}
		}
		
		if(isDigiSigningRequired(RoleCode.PBA_CHAIRPERSON.value())) {
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");

		} else if(isDigiSigningRequired(RoleCode.QCA_CHAIRPERSON.value())) {
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");

		} else if(isDigiSigningRequired(RoleCode.QCB_CHAIRPERSON.value())) {
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");

		}else if(isDigiSigningRequired(RoleCode.FPB_CHAIRPERSON.value())) {
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");

		}else if(isDigiSigningRequired(RoleCode.PBB_CHAIRPERSON.value())) {
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
		}
		else {
			updateBPMTask(outcome, null);
		}	
	}
	
	public void updateBPMTask(String outcome, PmDigiSign digiSign){
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		int activeIndex = this.getManagedBean().getActiveIndex();
		String taskId = this.getManagedBean().getTaskId();
		String token = new FacesScopes().getBPMAuthenticationCtxToken();
		
		if (null != digiSign) {
			updatePmDigiSignData(digiSign, ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION, 
					getManagedBean().getScQt().getQtId(), getManagedBean().getScQt().getQtNo());
		}
		
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			
			SuppFinalizationReqDTO reqDTO = validateInfo(activeIndex);
			reqDTO.setOutcome(outcome);
			reqDTO.setToken(token);
			reqDTO.setTaskId(taskId);
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setDigiSignData(digiSign);
			
			ScQtCommittee scQtCommittee = preparationDtlsInfo();
			reqDTO.setScQtCommittee(scQtCommittee);
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.submitSupplierFinalization(reqDTO);
			
			if (resDTO.isSuccessful()) {	
				String variable1 = getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION) + " " + getManagedBean().getScQt().getQtNo();
				String variable2 = getMessageCommon("lbl.sc.supplier.finalization");
				
				commonMessageAcknowledmentPage(MessageConst.INFO_700026, variable1, variable2);
			}

		} finally {
			qtSuppFinalizationSO = null;
		}
		
		/*ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		String decisionType = scQtFinalization.getDecision().getParameterCode();
		
		if(decisionType.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)){
			submitRecommendSupplier(scQtFinalization);
			
		}else if(decisionType.equals(ParameterConst.DECISION_RE_EVALUATION)){
			submitReEvaluateQT(scQtFinalization);
		
		}else if(decisionType.equals(ParameterConst.DECISION_RE_PUBLISH)){
			submitRePublishQT(scQtFinalization);
		
		}else if(decisionType.equals(ParameterConst.DECISION_CANCEL)){
			//TODO - called Sagar API for Cancel QT
		}
		*/
		
	}
	
	private void submitRecommendSupplier(ScQtFinalization scQtFinalization){
		//TODO - BPM
		String recommendType = scQtFinalization.getRecommendedTypeId().getParameterCode();
		
		if(scQtFinalization.isHigherAuthReq()){
			// BPM Send task to DO/commitee for the re-finalize by higher authority
		}else{
			if(recommendType.equals(ParameterConst.RECOMMEND_TYPE_PACKAGE)){
				//BPM send task for award/online bidding/multiple LOI by package
			}else if(recommendType.equals(ParameterConst.RECOMMEND_TYPE_ITEM)){
				//BPM send task for award/online bidding/multiple LOI by items
			}
		}
	}
	
	private void submitReEvaluateQT(ScQtFinalization scQtFinalization){
		//TODO - BPM
		/*String reEvaluateBy = scQtFinalization.getReEvaluateCommitte();
		boolean reEvaluateTech = getManagedBean().isReEvaluateReqForTech();
		boolean reEvaluateFin = getManagedBean().isReEvaluateReqForFin();
		
		if(reEvaluateBy.equals(SelectionType.New)){
			//BPM send task to maintain committee
		}
		if(reEvaluateTech && reEvaluateFin){
			//BPM send task to reEvaluate technical and financial
		
		}else if(reEvaluateTech && !reEvaluateFin){
			//BPM send task to reEvaluate technical only
			
		}else if(!reEvaluateTech && reEvaluateFin){
			//BPM send task to reEvaluate financial only
		}*/
	}
	
	private void submitRePublishQT(ScQtFinalization scQtFinalization){
		//TODO - BPM
		boolean specChanges = scQtFinalization.isSpecChangeReq();
		
		if(specChanges){
			//BPM send task to whole SC 
			//BPM notify DO, and publication approver.
		}else{
			//BPM send task to DO which before publication stage and required to re-schedule. 
			//(not required to obtained like budget, bpk approval) 
			//(not allow change info which related all approval - budget, bpk etc)
		}
	}
	
	
	/**
	 * To retrieve the document list in PMDocument for Briefing Paper/Kertas Taklimat
	 */
	private void retrieveBriefingPaper() {
		IQTSuppFinalizationSO qtSuppFinalizationSO;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			
			Long qtId = getManagedBean().getScQt().getQtId();

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			ScQtFinalization scQtFinalization = this.getManagedBean().getScQtFinalization();
			if(scQtFinalization == null){
				scQtFinalization = new ScQtFinalization();
			}
			reqDTO.setQtId(qtId);
			reqDTO.setScQtFinalization(scQtFinalization);
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.retrieveFinalizationReports(reqDTO);
			scQtFinalization = resDTO.getScQtFinalization();
			
			this.getManagedBean().setScQtFinalization(scQtFinalization);
			if (scQtFinalization.getScQtFinalizationReports() != null) {
				this.getManagedBean().setDocumentList(scQtFinalization.getScQtFinalizationReports());
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	public void handleFileUpload(FileUploadEvent event) throws FileNotFoundException, IOException {
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			
			ScQtFinalization scQtFinalization = this.getManagedBean().getScQtFinalization();
			ScQtFinalizationAttachment attachment = scQtFinalization.getScQtFinalizationAttachment();
			if (null == attachment) {
				attachment = new ScQtFinalizationAttachment();
				attachment.setScQtFinalization(scQtFinalization);
				
				scQtFinalization.setScQtFinalizationAttachment(attachment);
			}
			
			attachment.setDocNo(scQtFinalization.getFinalizationId().toString());
			scAttachment = attachment;
			uploadAttachment(event.getFile(), scAttachment);
		}
	}
	
	public void downloadReport(ScQtFinalizationReport scQtFinalizationReport) throws Exception {
		debugLogger("downloadReport");
		
		ScQtFinalizationReportAttachment reportAttachment = scQtFinalizationReport.getScQtFinalizationReportAttachment();
		if (reportAttachment == null) {
			reportAttachment = generateDocumentAttachment(scQtFinalizationReport);
			scQtFinalizationReport.setScQtFinalizationReportAttachment(reportAttachment);
		}
		downloadFile(reportAttachment);	
	}

	public void downloadAllReports() throws Exception {
		debugLogger("downloadAllReports");

		List<ScQtFinalizationReport> scQtFinalizationReports = getManagedBean().getScQtFinalization().getScQtFinalizationReports();
		List<ScQtFinalizationReportAttachment> scAttachments = new ArrayList<ScQtFinalizationReportAttachment>();
		
		for (ScQtFinalizationReport report : scQtFinalizationReports) {
			ScQtFinalizationReportAttachment attachment = report.getScQtFinalizationReportAttachment();
			if (attachment == null) {
				attachment = generateDocumentAttachment(report);
				report.setScQtFinalizationReportAttachment(attachment);
			}
			scAttachments.add(attachment);
		}
		downloadAllFiles("NGeP-Finalization-Report", scAttachments);
	}

	private ScQtFinalizationReportAttachment generateDocumentAttachment(ScQtFinalizationReport scQtFinalizationReport) throws Exception {
		String definitionType = scQtFinalizationReport.getDefinationType().getParameterCode();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		
		ScQtFinalizationReportAttachment reportAttachment = null;
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED.equals(definitionType)) {
			String docDefinitionKey = scQtFinalizationReport.getPmDocument().getDocDefinitionKey();
			reportAttachment = new ScQtFinalizationReportAttachment();
			reportAttachment.setScQtFinalizationReport(scQtFinalizationReport);
			reportAttachment.setDocNo(scQtFinalization.getScQt().getQtNo());
			ScAttachment attachment = (ScAttachment)reportAttachment;
			StringBuilder filename = new StringBuilder();
			//replace white space due to firefox 
			filename.append(scQtFinalizationReport.getTitle().replaceAll("\\s","_"));
			
			if (SourcingConstants.DOC_DEF_KEY_COVER.equals(docDefinitionKey)) {
				filename.append(".pdf");
				reportAttachment.setFileName(filename.toString());
				generateCoverPdf(attachment, getManagedBean().getScQt());
			
			} else if (SourcingConstants.DOC_DEF_KEY_OFFERED_QT_DTLS.equals(docDefinitionKey)) {
				generateOfferedAndQTDtlsPdf(attachment, getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_ANALYSIS_EVALUATION_REPORT.equals(docDefinitionKey)) {
				filename.append(".pdf");
				reportAttachment.setFileName(filename.toString());
				generateAnalysisEvaluationReportPdf(attachment, getManagedBean().getScQt());
				
			} else if (SourcingConstants.DOC_DEF_KEY_NOTICE_BOARD.equals(docDefinitionKey)) {
				generateNoticeBoardPdf(attachment, getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_PRICE_SCHEDULE.equals(docDefinitionKey)) {
				filename.append(".pdf");
				reportAttachment.setFileName(filename.toString());
				generatePriceSchedulePdf(attachment, getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_EVALUATION_REPORT.equals(docDefinitionKey)) {
				//FIXME - get from evaluation report

			} else if (SourcingConstants.DOC_DEF_KEY_OPENNING_REPORT.equals(docDefinitionKey)) {
				//FIXME - get from Opening report

			} else if (SourcingConstants.DOC_DEF_KEY_TECHNICAL_EVAALUATION_REPORT.equals(docDefinitionKey)) {
				//FIXME - get from Technical Evaluation report

			} else if (SourcingConstants.DOC_DEF_KEY_FINANCIAL_EVAALUATION_REPORT.equals(docDefinitionKey)) {
				//FIXME - get from Financial evaluation report
			}
			scQtFinalizationReport.setScQtFinalizationReportAttachment(reportAttachment);
		}
		return reportAttachment;
	}
	
	private void generateAnalysisEvaluationReportPdf(ScAttachment attachment, ScQt scQt) throws Exception {
		//attachment.setFileName("AnalysisOfEvaluation.pdf");
		populateAttachmentDetails(attachment);
		
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

		generatePdfFromOracle(reportName, paramMap, attachment);
	}

	public void generateCoverPdf(ScAttachment attachment, ScQt scQt) throws Exception {
		debugLogger("generateCoverPdf");
		//attachment.setFileName("finalization-Cover.pdf");
		populateAttachmentDetails(attachment);
		
		//ScQt scQt = scQtFinalization.getScQt();
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
    	VelocityContext context = new VelocityContext();
    	
    	Calendar cal = Calendar.getInstance();
    	int year=cal.get(Calendar.YEAR);
    	log.info("Current Year : " + year );
    	
    	context.put("qtNo", scQt.getQtNo());
    	context.put("year", year);
    	//context.put("fileNo", scQt.getFileNo());
    	context.put("jabatan", scQt.getPmOrgProfile().getParentOrgProfile().getOrgName());
    	context.put("ptj", scQt.getPmOrgProfile().getOrgName());
    	context.put("title", scQt.getQtTitle());
    	
    	context.put("procurementMode", scQt.getProcurementMode()
				.getParameterCode());
    	
    	String committeeType = "";
    	for (ScQtCommittee scQtCommittee : scQt.getScQtCommittees()) {
    		committeeType = scQtCommittee.getScCommittee().getCommitteeType().getParameterCode();
    	}
    	log.info("committeeType : " + committeeType);
    	StringBuilder commType = new StringBuilder();
    	if (commType != null) {
    		commType.append(getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE, committeeType));
    	}
    	context.put("commType", commType);
    	
    	List<ScQtMeeting> scQtMeetings = new ArrayList<ScQtMeeting>();
		IQTMeetingSO meetingSO = null;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
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
    	DocumentGenerator.getInstance().generatePdfFromVelocity(SourcingConstants.DOCPATH_FZ_COVER, context, new FileOutputStream(getActualFilePath(attachment)));
	}
	
	public void generateOfferedAndQTDtlsPdf(ScAttachment attachment, ScQt scQt) throws Exception {
		debugLogger("generateOfferedAndQTDtlsPdf");

		attachment.setFileName("Keterangan_Tawaran_dan_Maklumat_Tender_Sebut_Harga.pdf");
		populateAttachmentDetails(attachment);
		
		//ScQt scQt = scQtFinalization.getScQt();
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		//DecimalFormat amountDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		VelocityContext context = new VelocityContext();
	
		//TODO
		String committeeType = "";
		for (ScQtCommittee scQtCommittee : scQt.getScQtCommittees()) {
			committeeType = scQtCommittee.getScCommittee().getCommitteeType().getParameterCode();
		}
		log.info("committeeType : " + committeeType);
		StringBuilder commType = new StringBuilder();
		if (commType != null) {
			commType.append(getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE, committeeType));
		}
		context.put("commType", commType);
		context.put("ptj", scQt.getPmOrgProfile().getOrgName());
		context.put("jabatan", scQt.getPmOrgProfile().getParentOrgProfile().getOrgName());
		context.put("title", scQt.getQtTitle());
		context.put("estimatedAmount", scQt.getEstimatedAmount());
		
		boolean planned = false;
		if (scQt.isPlanned()) {
			planned = true;
			if (scQt.getScQtGosodoBudgets() != null) {
				context.put("budgetList", scQt.getScQtGosodoBudgets());
			}
		}
		context.put("isPlanned", planned);
		
		StringBuilder fulfilmentType = new StringBuilder();
    	if (fulfilmentType != null) {
    		fulfilmentType.append(getParameterDesc(scQt.getFulfilmentType()));
    	}
    	context.put("fulfilmentType", fulfilmentType);
    	
    	boolean isSample = false;
    	if (scQt.isSampleReq()) {
    		isSample = true;
    	}
    	context.put("isSample", isSample);
		
		String publishDate = "";
		if (scQt.getPublishDate() != null) {
			publishDate = dateSDF.format(scQt.getPublishDate());
		}
		context.put("publishDate", publishDate);
		
		String closingDate = "";
		if (scQt.getChangedDate() != null) {
			closingDate = dateSDF.format(scQt.getChangedDate());
		}
		context.put("closingDate", closingDate);
		
		// get Total Suppliers based on Zone
		boolean isZone = false;
		List<Object[]> list = new ArrayList<Object[]>();
		BriefingZoneBean briefingZoneBean = null;
    	List<BriefingZoneBean> zoneBeans = new ArrayList<BriefingZoneBean>();
		IQTBriefingSO qTBriefingSO;
		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
			String languageCode = new FacesScopes().getUserLocale().getLanguage();
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setLanguageCode(languageCode);
			BriefingResDTO resDTO = qTBriefingSO.retrieveBriefingSiteVisitDtl(reqDTO);
			if (scQt.isZonal()) {
				List<TplSpecZoneDtl> zoneDtls = resDTO.getZonesList();
				if (null != zoneDtls) {
					isZone = true;
					for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
						reqDTO.setSpecZoneId(specZoneDtl.getSpecZoneId());
						BriefingResDTO briefingResDTO = qTBriefingSO.getTotalAttendedSupplier(reqDTO);
						list = briefingResDTO.getTotalAttendedSupplier();
						log.info("Size : " + list.size());
						ScQtBsv scQtBsv = briefingResDTO.getScQtBsv();
						briefingZoneBean = new BriefingZoneBean(specZoneDtl, briefingResDTO.getBriefingList(), briefingResDTO.getSiteVisitList(), scQtBsv);
						zoneBeans.add(briefingZoneBean);
						/*for(Object[] object : list){
							TplSpecZoneDtl tplSpecZoneDtl = (TplSpecZoneDtl) object[0];
							Long totalAttended = (Long) object[1];
							ScQtBsvDtl scQtBsvDtl = (ScQtBsvDtl) object[2];
							log.info("tplSpecZoneDtl.getZoneName() : "+ tplSpecZoneDtl.getZoneName());
							log.info("totalAttended : "+ totalAttended);
							log.info("scQtBsvDtl.getBsvDesc() : "+ scQtBsvDtl.getBsvDesc());
						}*/
					}
				}
			} else {
				
			}
		} finally {
			qTBriefingSO = null;
		}
		context.put("isZone", isZone);
		context.put("zoneBeans", zoneBeans);
		String supplierSize = "";
		if (list != null && !list.isEmpty()) {
			supplierSize = String.valueOf(list.size());
		}
		context.put("supplierSize", supplierSize);
		
		Long totalQualifiedSuppliers = null;
		try {
			qTBriefingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
			String languageCode = new FacesScopes().getUserLocale().getLanguage();
			BriefingReqDTO reqDTO = new BriefingReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setLanguageCode(languageCode);
			BriefingResDTO resDTO = qTBriefingSO.retrieveBriefingSiteVisitDtl(reqDTO);
			if (scQt.isZonal()) {
				List<TplSpecZoneDtl> zoneDtls = resDTO.getZonesList();
				if (null != zoneDtls) {
					for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
						reqDTO.setSpecZoneId(specZoneDtl.getSpecZoneId());
						BriefingResDTO briefingResDTO = qTBriefingSO.getTotalQualifiedSupplier(reqDTO);
						totalQualifiedSuppliers = briefingResDTO.getQualifiedSupp();
					}
				}
			}
		} finally {
			qTBriefingSO = null;
		}
		if (totalQualifiedSuppliers == null) {
			totalQualifiedSuppliers = 0l;
		}
		context.put("totalQualifiedSuppliers", totalQualifiedSuppliers);
		
		//QTProposalSO.retrieveTotalSubmittedProposal
		Long totalSuppliers;
		IQTProposalSO qTProposalSO;
		try {
			qTProposalSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO proposalReqDTO = new ProposalReqDTO();		
			proposalReqDTO.setQtId(scQt.getQtId());
			ProposalResDTO proposalResDTO = qTProposalSO.getTotalSubmittedProposal(proposalReqDTO);
			totalSuppliers = proposalResDTO.getTotalSubmitted();
		} finally {
			qTProposalSO = null;
		}
		context.put("totalSuppliers", totalSuppliers);
		
		context.put("validityDays", scQt.getQtValidityPeriod());
		StringBuilder openType = new StringBuilder();
		if (openType != null) {
			openType.append(getParameterDesc(ParameterTypeConst.QUOTATION_TENDER_TYPE, scQt.getQtType()));
		}
		context.put("openType", openType);
		context.put("openFor", scQt.getOpenFor());
		
		String existingContract = scQt.getExistingContractNo();
		if (scQt.getExistingContractNo() == null) {
			existingContract = "";
		} else {
			IContractSO contractSO;
			try {
				contractSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				ContractReqDTO contractReqDTO = new ContractReqDTO();
				contractReqDTO.setContractNo(scQt.getExistingContractNo());
				ContractResDTO contractResDTO = contractSO.retriveContractInfoByContractNo(contractReqDTO);
				if (contractResDTO.getContract() != null) {
					CtContract contract = contractResDTO.getContract();
					context.put("contract", contract);
				}
			} finally {
				contractSO = null;
			}
		}
		context.put("existingContract", existingContract);

		List<ScQtCommitteeActivity> committeeActivities = new ArrayList<ScQtCommitteeActivity>();
		List<ScQtCommitteeActivity> committeeActivitiesList = new ArrayList<ScQtCommitteeActivity>();
		ScQtCommittee scQtCommittee = new ScQtCommittee();
		IQTMeetingSO meetingSO = null;
		MeetingResDTO resDTO = null;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setCommitteType(committeeType);
			resDTO = meetingSO.retrieveMeetingDls(reqDTO);
			scQtCommittee = resDTO.getQtCommittee();
			if (null == scQtCommittee) {
				scQtCommittee = new ScQtCommittee();
			}
			if (null != scQtCommittee) {
				if (null != scQtCommittee.getCommitteeActivities() && !scQtCommittee.getCommitteeActivities().isEmpty()) {
					committeeActivitiesList = scQtCommittee.getCommitteeActivities();
					for (ScQtCommitteeActivity committeeActivity : committeeActivitiesList) {
						if (committeeActivity.getScCommitteeMember() != null) {
							if (committeeActivity.getScCommitteeMember().getMemberName() == null) {
								committeeActivity.getScCommitteeMember().setMemberName("");
							}
						} else {
							committeeActivity.setScCommitteeMember(new ScCommitteeMember());
							committeeActivity.getScCommitteeMember().setMemberName("");
						}
						if (committeeActivity.getScCommitteeMember() != null) {
							if (committeeActivity.getScCommitteeMember().getDesignation() == null) {
								committeeActivity.getScCommitteeMember().setDesignation("");
							}
						} else {
							committeeActivity.setScCommitteeMember(new ScCommitteeMember());
							committeeActivity.getScCommitteeMember().setDesignation("");
						}
						if (committeeActivity.getScCommitteeMember() != null) {
							if (committeeActivity.getScCommitteeMember().getGrade() == null) {
								committeeActivity.getScCommitteeMember().setGrade("");
							}
						} else {
							committeeActivity.setScCommitteeMember(new ScCommitteeMember());
							committeeActivity.getScCommitteeMember().setGrade("");
						}
						if (committeeActivity.getScCommitteeMember() != null) {
							if (committeeActivity.getScCommitteeMember().getIcPassport() == null) {
								committeeActivity.getScCommitteeMember().setIcPassport("");
							}
						} else {
							committeeActivity.setScCommitteeMember(new ScCommitteeMember());
							committeeActivity.getScCommitteeMember().setIcPassport("");
						}
						committeeActivities.add(committeeActivity);
					}
				}
			}
		} finally {
			meetingSO = null;
		}
		context.put("committeeActivities", committeeActivities);
		context.put("bean", this);
		DocumentGenerator.getInstance().generatePdfFromVelocity(SourcingConstants.DOCPATH_FZ_OFFERED_QT_DTLS, context, new FileOutputStream(getActualFilePath(attachment)));
	}
	
	public void generateNoticeBoardPdf(ScAttachment attachment, ScQt scQt) throws Exception {
		debugLogger("generateNoticeBoardPdf");
		
		attachment.setFileName("Notice_Board.pdf");
		populateAttachmentDetails(attachment);
		
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		DecimalFormat amountDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		VelocityContext context = new VelocityContext();
		
		context.put("title", scQt.getQtTitle());
		context.put("qtNo", scQt.getQtNo());
		context.put("ministry", scQt.getPmOrgProfile().getParentOrgProfile().getOrgName());
		context.put("jabatan", scQt.getPmOrgProfile().getParentOrgProfile().getOrgName());
    	context.put("ptj", scQt.getPmOrgProfile().getOrgName());
    	String publishDate = "";
		if (scQt.getPublishDate() != null) {
			publishDate = dateSDF.format(scQt.getPublishDate());
		}
		context.put("publishDate", publishDate);
		context.put("publishPeriod", scQt.getPublishPeriod());
		String closingDate = "";
		if (scQt.getChangedDate() != null) {
			closingDate = dateSDF.format(scQt.getChangedDate());
		}
		context.put("closingDate", closingDate);
		context.put("validityDays", scQt.getQtValidityPeriod());
    	context.put("tier", scQt.getEvaluationType());
		
    	context.put("openFor", scQt.getOpenFor());
    	
    	String paidupcapital = "";
    	if (scQt.getScQtSupplierCriteria() != null) {
	    	if (scQt.getScQtSupplierCriteria().getMinPaidUpCapital() != null) {
	    		paidupcapital = amountDF.format(scQt.getScQtSupplierCriteria().getMinPaidUpCapital());	
	    	}
    	}
    	context.put("paidupcapital", paidupcapital);
    	
    	context.put("categoryList", scQt.getScQtCategorys());
    	context.put("bean", this);
    	
    	List<ScQtCategory> categoryResults = new ArrayList<ScQtCategory>();
    	IQTDetailSO QtDetailsSO;
		try {
			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			tenderReqDTO.setQtdetail(this.getManagedBean().getScQt());
			QuationTenderResDTO tenderResDTO = QtDetailsSO.retriveQTSuppQulCriDetails(tenderReqDTO);
			categoryResults = tenderResDTO.getCategoryResultBean().getResultList();
			if (null != tenderResDTO.getLocationDetails()) {
				context.put("locality", tenderResDTO.getLocationDetails());
			}
		} finally {
			QtDetailsSO = null;
		}
    	
    	String locationLevel = null;
    	boolean isLocLevel = false;
    	boolean isSubLocLevel = false;
    	if (scQt.getZoneLocationLevel() != null) {
    		if (ParameterConst.LOCATION_LEVEL_STATE.equals(scQt.getZoneLocationLevel().getParameterCode())) {
	    		isLocLevel = true;
	    		locationLevel = "State";
			} else if (ParameterConst.LOCATION_LEVEL_DIVISION.equals(scQt.getZoneLocationLevel().getParameterCode())) {
				isLocLevel = true;
				locationLevel = "Division";
			} else if (ParameterConst.LOCATION_LEVEL_DISTRICT.equals(scQt.getZoneLocationLevel().getParameterCode())) {
				isLocLevel = true;
				locationLevel = "District";
			}
	    	context.put("locLevel", isLocLevel);
    	} else {
    		ScQtSupplierCriteria criteria = scQt.getScQtSupplierCriteria();
    		if (criteria != null) {
	    		if (criteria.getLocationLevel() != null) {
	    			if (ParameterConst.LOCATION_LEVEL_STATE.equals(criteria.getLocationLevel().getParameterCode())) {
	    				isSubLocLevel = true;
	    	    		locationLevel = "State";
	        		} else if (ParameterConst.LOCATION_LEVEL_DISTRICT.equals(criteria.getLocationLevel().getParameterCode())) {
	        			isSubLocLevel = true;
	    				locationLevel = "District";
	    			}
	    			context.put("subLocLevel", isSubLocLevel);
	    		} else {
	    			context.put("subLocLevel", isSubLocLevel);
	    		}
    		}
    		context.put("locLevel", isLocLevel);
    	}
    	context.put("locationLevel", locationLevel);
    	IQTDetailSO qtDtlSO;
 		try {
 			qtDtlSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
			qtReqDTO.setDocType(QTConstants.DOCTYPE_QT);
			qtReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			qtReqDTO.setBdgDtlsReq(true);
			QuationTenderResDTO qtResDTO = new QuationTenderResDTO();
			qtResDTO = qtDtlSO.retriveQTDetails(qtReqDTO);
			if (this.getManagedBean().getScQt().isZonal()) {
				context.put("zoneLocality", qtResDTO.getZoneDetails());
			}
 		} finally {
 			qtDtlSO = null;
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
    			qTBriefingSO = (IQTBriefingSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
    			BriefingReqDTO reqDTO = new BriefingReqDTO();
    			//reqDTO.setQtId(getManagedBean().getQt());
    			reqDTO.setScQt(this.getManagedBean().getScQt());
    			BriefingResDTO resDTO = qTBriefingSO.retrieveBriefingSiteVisitDtl(reqDTO);
    			ScQt qt = resDTO.getScQt();
    			if (qt.isZonal()) {
    				List<TplSpecZoneDtl> zones = resDTO.getZonesList();
    				if (null != zones) {
    					isZone = true;
    					BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
    					briefingReqDTO.setQtId(getManagedBean().getScQt().getQtId());
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
        	context.put("isbsv", isbsv);
        	context.put("isZone", isZone);
    		context.put("zoneBeans", zoneBeans);
    	} else {
    		isbsv = false;
    		context.put("isbsv", isbsv);
    	}
    	
    	/*List<BriefingZoneBean> bsvZoneBeans = new ArrayList<BriefingZoneBean>();
    	IQTNoticeBoardSO qtNoticeBoardSO = null;
    	NoticeBoardReqDTO noticeBoardReqDTO = new NoticeBoardReqDTO();
    	NoticeBoardResDTO noticeBoardResDTO = new NoticeBoardResDTO();
    	
    	if (!scQt.isZonal()) {
    		if (null != noticeBoardResDTO.getLocCriteria()) {
				List<PmState> states = new ArrayList<PmState>();
				for (TplSpecLoc locCriteria : noticeBoardResDTO.getLocCriteria()) {
					PmState pmState = locCriteria.getPmState();
					TreeNode root = null;
					if (!states.contains(pmState)) {
						root = new DefaultTreeNode("state", pmState, null);
						root.setExpanded(true);
						locations.add(root);
					}
					PmDivision division = locCriteria.getPmDivision();
					TreeNode divisonNode = null;
					if (division != null) {
						divisonNode = new DefaultTreeNode("division", division, root);
						divisonNode.setExpanded(true);
					}
					PmDistrict pmDistrict = locCriteria.getPmDistrict();
					TreeNode districtNode = null;
					if (pmDistrict != null) {
						if (divisonNode == null) {
							districtNode = new DefaultTreeNode("district", pmDistrict,
									root);
							districtNode.setExpanded(true);
						} else {
							districtNode = new DefaultTreeNode("district", pmDistrict,
									divisonNode);
							districtNode.setExpanded(true);
						}
					}
					states.add(pmState);
				}
			}
			ScQtBsv scQtBsv = noticeBoardResDTO.getScQtBsv();

			briefingZoneBean = new BriefingZoneBean(null,
					noticeBoardResDTO.getBriefingResultBean(),
					noticeBoardResDTO.getSiteVisitResultBean(), scQtBsv);
    	} else if (scQt.isZonal()) {
    		List<TplSpecZoneDtl> zoneDtls = noticeBoardResDTO.getZones();
    		if (null != zoneDtls) {
    			isZone = true;
    			for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
    				BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
					briefingReqDTO.setQtId(scQt.getQtId());
					briefingReqDTO.setSpecZoneId(specZoneDtl
							.getSpecZoneId());
					IQTBriefingSO briefingSO = EpEjbBusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION,
									IQTBriefingSO.class);
					BriefingResDTO briefingResDTO = briefingSO
							.retrieveBriefingSiteVisitDtls(briefingReqDTO);
					ScQtBsv scQtBsv = briefingResDTO.getScQtBsv();
					briefingZoneBean = new BriefingZoneBean(
							specZoneDtl, briefingResDTO.getBriefingList(),
							briefingResDTO.getSiteVisitList(),
							scQtBsv);
					bsvZoneBeans.add(briefingZoneBean);
    			}
    		}
    	}

    	context.put("locations", locations);
    	context.put("isZone", isZone);
    	context.put("bsvZoneBeansList", bsvZoneBeans);*/
    	
    	List<ScQtContactPerson> contactPersons = new ArrayList<ScQtContactPerson>();
    	List<ScQtContactPerson> contactPersonsList = new ArrayList<ScQtContactPerson>();
    	IQTNoticeBoardSO qtNoticeBoardSO = null;
    	NoticeBoardReqDTO noticeBoardReqDTO = new NoticeBoardReqDTO();
    	NoticeBoardResDTO noticeBoardResDTO = new NoticeBoardResDTO();
    	try {
    		qtNoticeBoardSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);
    		noticeBoardReqDTO.setQtId(scQt.getQtId());
			noticeBoardResDTO = qtNoticeBoardSO.retrieveQtNoticeDtl(noticeBoardReqDTO);
			if (noticeBoardResDTO.getContactPersonResultBean().getResultList() != null) {
				contactPersons = noticeBoardResDTO.getContactPersonResultBean().getResultList();
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
		DocumentGenerator.getInstance().generatePdfFromVelocity(SourcingConstants.DOCPATH_FZ_NOTICE_BOARD, context, new FileOutputStream(getActualFilePath(attachment)));
	}
	
	public void generatePriceSchedulePdf(ScAttachment attachment, ScQt scQt) throws Exception {
		debugLogger("generatePriceSchedulePdf");
		
		//attachment.setFileName("finalization-priceSchedule.pdf");
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
			this.getManagedBean().setScQt(resDTO.getScQt());
			if (resDTO.getScQt().isZonal()) {
				List<TplSpecZoneDtl> zones = resDTO.getZones();
				if (null != zones) {
					isZone = true;
					NoticeBoardReqDTO boardReqDTO = new NoticeBoardReqDTO();
					boardReqDTO.setQtId(scQt.getQtId());
					list.addAll(resDTO.getZones());
					context.put("zones", list);
					for (TplSpecZoneDtl zoneDtl : zones) {
						boardReqDTO.setZoneId(zoneDtl.getSpecZoneId());
						NoticeBoardResDTO boardResDTO = qtNoticeBoardSO.retrieveOfferedPriceDtlsByZone(reqDTO);
						beans.addAll(boardResDTO.getQualificationBean());
					}
				}
			} else {
				beans.addAll(resDTO.getQualificationBean());
			}
		} finally {
			qtNoticeBoardSO = null;
		}
		context.put("isZone", isZone);
		context.put("supplierBeans", beans);
		DocumentGenerator.getInstance().generatePdfFromVelocity(SourcingConstants.DOCPATH_FZ_PRICE_SCHEDULE, context, new FileOutputStream(getActualFilePath(attachment)));
	}
	
	//Kertas Taklimat
	/*private ScQtDocReport retrieveBriefingPaperByDocId(PmDocument pmDocument) {
		IQTSuppFinalizationSO qtSuppFinalizationSO;
		ScQtDocReport scQtReport = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocumentCategory(ParameterConst.DOCUMENT_CATEGORY_BRIEFING_PAPER);
			reqDTO.setDocumentId(pmDocument.getDocumentId());
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.retrieveReport(reqDTO);
			if (resDTO.getScQtReport() != null) {
				scQtReport = resDTO.getScQtReport();
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
		return scQtReport;
	}*/
	
	private void retrieveSupplierFinalizationDtls(ScQt scQt) {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			
			boolean isZonal = scQt.isZonal();
			PmUser user = new FacesScopes().getUserSessionBean();
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setScQt(scQt);
			reqDTO.setLoggedInUserId(user.getUserId());
			
			List<TplSpecZoneDtl> zoneDetails = getManagedBean().getZoneDetails();
			reqDTO.setZoneDetails(zoneDetails);
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.retrieveSupplierFinalizationDtls(reqDTO);
			if (CollectionUtils.isNotEmpty(zoneDetails) && 
					this.getManagedBean().getTplSpecZoneDtl() == null) {
				this.getManagedBean().setTplSpecZoneDtl(zoneDetails.get(0));
			}
//			this.getManagedBean().setShortlistedItemList(resDTO.getQualifiedByItemSuppliers());
//			this.getManagedBean().setShortlistedItemsByZone(resDTO.getQualifiedByItemMap());
			if (isZonal) {
				this.getManagedBean().setShortlistedSupppliersByZone(resDTO.getQualifiedByPackageMap());
			} else {
				this.getManagedBean().setShortlistedPackageList(resDTO.getQualifiedByPackageSuppliers());
			}
			
			if (resDTO.getScQtFinalization() != null) {
				ScQtFinalization scQtFinalization = resDTO.getScQtFinalization();
				String decisionTypeCode = scQtFinalization.getDecision().getParameterCode();
				if (decisionTypeCode.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)) {
					String recommendType = scQtFinalization.getRecommendedTypeId().getParameterCode();
					if (ParameterConst.RECOMMEND_TYPE_PACKAGE.equals(recommendType)) {
						if (getManagedBean().getShortlistedSupplierList() == null) {
							getManagedBean().setShortlistedSupplierList(new ArrayList<ScQtEvaluationResult>());
							if (!isZonal && 
									getManagedBean().getShortlistedPackageList() != null) {
								getManagedBean().getShortlistedSupplierList().addAll(getManagedBean().getShortlistedPackageList());
							} else {
								Map<TplSpecZoneDtl, List<ScQtEvaluationResult>> 
											shortlistedSupppliersByZone = this.getManagedBean().getShortlistedSupppliersByZone();
								if (MapUtils.isNotEmpty(shortlistedSupppliersByZone)) {
									TplSpecZoneDtl defaultSelectedZone = (TplSpecZoneDtl) (shortlistedSupppliersByZone.keySet().toArray())[0];
									this.getManagedBean().setShortlistedSupplierList(shortlistedSupppliersByZone.get(defaultSelectedZone));
								}								
							}
						}
						
						if (CollectionUtils.isNotEmpty(scQtFinalization.getScQtRecommends())) {
							ScQtRecommend scQtRecommend = null;
							if (isZonal) {
								TplSpecZoneDtl selectedZone = this.getManagedBean().getTplSpecZoneDtl();
								Set<TplSpecZoneDtl> zonalKeySet = scQtFinalization.getScQtRecommendsByZone().keySet();
								for (TplSpecZoneDtl zone : zonalKeySet) {
									if ((selectedZone.getSpecZoneId()).equals(zone.getSpecZoneId())) {
										scQtRecommend = scQtFinalization.getScQtRecommendsByZone().get(zone).get(0);
										break;
									}
								}
							} else {
								scQtRecommend = scQtFinalization.getScQtRecommends().get(0);
							}
							PmParameter recommendBy = scQtRecommend.getRecommendBy();
							getManagedBean().setRecommendBy(recommendBy.getParameterCode());
							
							//set the checkbox or radio button of supplier selected
							if (CollectionUtils.isNotEmpty(scQtRecommend.getRecommendDtls())) {
								if (recommendBy.getParameterCode().equals(ParameterConst.RECOMMEND_BY_AWARD)) {
									//Award
									Long selectedSupplierId = scQtRecommend.getRecommendDtls().get(0).getSupplier().getSupplierId();
									for (ScQtEvaluationResult scQtEvaluationResult : 
											getManagedBean().getShortlistedSupplierList()) {
										Long supplierId = scQtEvaluationResult.getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierId();
										if (supplierId == selectedSupplierId) {
											scQtEvaluationResult.setSelected(true);
											getManagedBean().setRecommendedSupplier(scQtEvaluationResult);
										}
									}
								} else {
									//Multiple LOI or Online Bidding
									List<Long> supplierIdList = new ArrayList<Long>();
									for (ScQtRecommendDtl scQtRecommendDtl : scQtRecommend.getRecommendDtls()) {
										supplierIdList.add(scQtRecommendDtl.getSupplier().getSupplierId());
									}
									
									getManagedBean().setRecommendedSupplierList(new ArrayList<ScQtEvaluationResult>());
									for (ScQtEvaluationResult scQtEvaluationResult : 
											getManagedBean().getShortlistedSupplierList()) {
										Long supplierId = scQtEvaluationResult.getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierId();
										if (supplierIdList.contains(supplierId)) {
											scQtEvaluationResult.setSelected(true);
											getManagedBean().getRecommendedSupplierList().add(scQtEvaluationResult);
										}
									}
								}
							}
						}
					} else {
						if (getManagedBean().getShortlistedSupplierList() == null) {
							getManagedBean().setShortlistedSupplierList(new ArrayList<ScQtEvaluationResult>());
//							if (getManagedBean().getShortlistedItemList() != null) {
//								getManagedBean().getShortlistedSupplierList().addAll(getManagedBean().getShortlistedItemList());
//							}
						}
						
						List<ScQtRecommend> recommendList = scQtFinalization.getScQtRecommends();
						if (CollectionUtils.isNotEmpty(recommendList)) {
							for (ScQtRecommend scQtRecommend : scQtFinalization.getScQtRecommends()) {
								if (isZonal) {
									TplSpecZoneDtl selectedZone = this.getManagedBean().getTplSpecZoneDtl();
									if (!(scQtRecommend.getTplSpecZoneDtl().getSpecZoneId()).equals(
											selectedZone.getSpecZoneId())) {
										continue;
									}
								}	
								PmParameter recommendBy = scQtRecommend.getRecommendBy();
								scQtRecommend.setRecommendByStr(recommendBy.getParameterCode());
								
								//set the checkbox or radio button of supplier selected
								if (CollectionUtils.isNotEmpty(scQtRecommend.getRecommendDtls())) {
									//Multiple LOI or Online Bidding
									List<Long> supplierIdList = new ArrayList<Long>();
									for (ScQtRecommendDtl scQtRecommendDtl : scQtRecommend.getRecommendDtls()) {
										supplierIdList.add(scQtRecommendDtl.getSupplier().getSupplierId());
									}
									scQtRecommend.setAwardSupplierList(new ArrayList<ScQtEvaluationResult>());
									getManagedBean().setRecommendedSupplierList(new ArrayList<ScQtEvaluationResult>());
									for (ScQtEvaluationResult scQtEvaluationResult : getManagedBean().getShortlistedSupplierList()) {
										Long supplierId = scQtEvaluationResult.getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierId();
										if (supplierIdList.contains(supplierId)) {
											scQtEvaluationResult.setSelected(true);
											for (ScQtRecommendDtl dtl : scQtRecommend.getRecommendDtls()) {
												if(supplierId == dtl.getSupplier().getSupplierId()){
													scQtEvaluationResult.setQtyAward(dtl.getAwardQty());
												}
											}
											scQtRecommend.getAwardSupplierList().add(scQtEvaluationResult);
											getManagedBean().getRecommendedSupplierList().add(scQtEvaluationResult);
										}
									}
								}
							}
						}
					}
				} else if (decisionTypeCode.equals(ParameterConst.DECISION_RE_EVALUATION)) {
					//nothing to set
				} else if (decisionTypeCode.equals(ParameterConst.DECISION_RE_PUBLISH)) {
					//nothing to set
				} else if (decisionTypeCode.equals(ParameterConst.DECISION_CANCEL)) {
					//nothing to set
				}
				getManagedBean().setScQtFinalization(scQtFinalization);
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	/*private void retrieveItemSupplierList(boolean itemize) {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(qtId);//FIXME
			if(getManagedBean().isItemize()){//itemize
				reqDTO.setItemize(itemize);
			}
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.retrieveItemSupplierDtl(reqDTO);
			
			List<ScQtEvaluationResult> shortlistedSupplierList = resDTO.getQualifiedSuppResultBean().getResultList();
			this.getManagedBean().setShortlistedSupplierList(shortlistedSupplierList);
			
			if(getManagedBean().isItemize()){//itemize
				List<ScRequestItem> itemlist = resDTO.getItemResultBean().getResultList();
				this.getManagedBean().setItemList(itemlist);
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	private void retrieveItemSupplierListByZone(Long qtZoneCriteriaId) {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(qtId);//FIXME
			reqDTO.setQtZoneCriteriaId(qtZoneCriteriaId);
			if(getManagedBean().isItemize()){//itemize
				reqDTO.setItemize(true);
			}
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.retrieveItemSupplierDtl(reqDTO);
			if(getManagedBean().isItemize()){//itemize
				List<ScRequestItem> itemlist = resDTO.getItemResultBean().getResultList();
				this.getManagedBean().setItemList(itemlist);
			}
			
			List<ScQtEvaluationResult> shortlistedSupplierList = resDTO.getQualifiedSuppResultBean().getResultList();
			this.getManagedBean().setShortlistedSupplierList(shortlistedSupplierList);
		} finally {
			qtSuppFinalizationSO = null;
		}
	}*/
	
	/**
	 * Method to call Supplier Details pop up in supplier finalization
	 */
	public void showSupplierDetails(ScQtRecommend scQtRecommend, ScQtRecommendDtl selectedRecommendDtl) {
		debugLogger("showSuppDetails");
		
		if (selectedRecommendDtl != null 
				&& selectedRecommendDtl.getShortlistedSupplier() != null) {
			this.getManagedBean().setShortlistedSupplier(selectedRecommendDtl.getShortlistedSupplier());
		} else if (scQtRecommend != null) {
			List<ScQtEvaluationResultDtl> shortlistedItemSupplierList = null;
			boolean isSuccessful = false;
			SuppFinalizationResDTO resDTO = null;
			if (CollectionUtils.isEmpty(scQtRecommend.getShortListedSupplierList())) {
				resDTO = this.getShortListedSuppliersByItem(scQtRecommend);
				isSuccessful = resDTO.isSuccessful();
				shortlistedItemSupplierList = resDTO.getQualifiedByItemSuppliers();
			} else {
				isSuccessful = true;
				shortlistedItemSupplierList = scQtRecommend.getShortListedSupplierList();
			}
			if (isSuccessful) {
				for (ScQtRecommendDtl scQtRecommendDtl : 
						scQtRecommend.getRecommendDtls()) {
					if (CollectionUtils.isNotEmpty(shortlistedItemSupplierList)) {
						for (ScQtEvaluationResultDtl evalResultDtl :
														shortlistedItemSupplierList) {
							calculateOfferedPrice(evalResultDtl, scQtRecommend.getRequestItem());
							SmSupplier smSupplier = evalResultDtl.getQtEvaluationResult().getScQtProposal()
													.getScQtSupplier().getSmSupplier();
							if ((scQtRecommendDtl.getSupplier().getSupplierId()).equals(
									smSupplier.getSupplierId())) {
								scQtRecommendDtl.setShortlistedSupplier(evalResultDtl.getQtEvaluationResult());
								getManagedBean().setShortlistedSupplier(evalResultDtl.getQtEvaluationResult());
							}
						}
					}
				}
			}
		}
		if (getManagedBean().getShortlistedSupplier() != null) {
			this.getManagedBean().setShortlistedSupplier(getManagedBean().getShortlistedSupplier());
			SmSupplier smSupplier = getManagedBean().getShortlistedSupplier().getScQtProposal().getScQtSupplier().getSmSupplier();
			if (smSupplier != null) {
				QTSupplierResDTO resDTO = this.retrieveSupplierDetails(smSupplier);
				smSupplier = resDTO.getSmSupplier();
				getManagedBean().setSmSupplier(smSupplier);
				getManagedBean().setSmMofAccount(smSupplier.getSmMofAccounts().get(0));
				
				SmCompanyBasic smCompanyBasic = smSupplier.getSmAppl().getSmCompanyBasic();
				getManagedBean().setSmCompanyBasic(smCompanyBasic);
				
				List<SmPersonnel> smPersonnelList = smSupplier.getSmAppl().getSmPersonnels();
				for (SmPersonnel smPersonnel : smPersonnelList) {
					if(smPersonnel.isContactPerson()){
						getManagedBean().setSmPersonnel(smPersonnel);
						break;
					}
				}
				for (SmCompanyAddress smCompanyAddress : smCompanyBasic.getSmCompanyAddresses()) {
					if (AddressType.CONTACT_ADDRESS.value().equals(smCompanyAddress.getAddressType())) {
						getManagedBean().setSupplierBranch(QTConstants.SUPPLIER_INFO_HQ);
						getManagedBean().setSupplierAddress(smCompanyAddress.getSmAddress());
						break;
					}
				}
			}
		}
	}
	
	/**
	 * To retrieve the list of Committee for Endorsement
	 */
	private void retrieveCommitteeEndorsement() {
		IQTMeetingSO meetingSO;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);

			Long qtId = getManagedBean().getScQt().getQtId();
			String committeeType = getManagedBean().getCommitteeType();
			
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteType(committeeType);
			reqDTO.setIsAttended(null);
			
			MeetingResDTO resDTO = meetingSO.retrieveSignOffCommitteeListByCommType(reqDTO);
			if (resDTO.getCommitteMembers() != null) {
				this.getManagedBean().setCommitteeEndorsementList(resDTO.getCommitteMembers());
			}
		} finally {
			meetingSO = null;
		}
	}
	
	/**
	 * Method to call Supplier Details popup in supplier finalization
	 */
	public void showReminder() {
		debugLogger("showReminder");
		
		this.getManagedBean().setReminderList(new ArrayList<ScQtCommitteeActivity>());
		
		List<ScQtCommitteeActivity> chairpersonList = new ArrayList<ScQtCommitteeActivity>();
		List<ScQtCommitteeActivity> memberList = new ArrayList<ScQtCommitteeActivity>();
		
		for(ScQtCommitteeActivity committee : getManagedBean().getCommitteeEndorsementList()){
			String memberRole = committee.getScCommitteeMember().getMemberRole().getParameterCode();
			
			if(memberRole.equals(SourcingConstants.MEMBER_ROLE_CHAIRPERSON)){
				chairpersonList.add(committee);
			}else{
				if(!committee.isSignedOff()){
					memberList.add(committee);
				}
			}
		}
		
		if(memberList.size() == 0){
			//send reminder to Chairperson
			this.getManagedBean().getReminderList().addAll(chairpersonList);
		}else{
			//send reminder to Members
			this.getManagedBean().getReminderList().addAll(memberList);
		}
	}
	
	public void sendReminder(){
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
		
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId().toString());
			reqDTO.setCommitteMembers(getManagedBean().getCommitteeEndorsementList());
			reqDTO.setSubject(getManagedBean().getSubject());
			reqDTO.setMessage(getManagedBean().getMessage());
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.sendNotifications(reqDTO);
			if (resDTO.isSuccessful()) {
				String variable1 = getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION);
				String variable2 = getManagedBean().getScQt().getQtNo();
				commonMessageAcknowledmentPage(MessageConst.INFO_700047, variable1, variable2);
			} else {
				throw new EpException("Failed.");
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	//when committee members endorsed the Supplier Finalization or chairperson sign off the Supplier Finalization
	public void signOff(){
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			
			ScQtCommitteeActivity committeeAct = null;
			boolean isChairperson = false;
			
			retrieveCommitteeEndorsement();
			for(ScQtCommitteeActivity scQtCommitteeActivity : getManagedBean().getCommitteeEndorsementList()){
				if(scQtCommitteeActivity.getScCommitteeMember().getPmUser().equals(getFacesScopes().getUserSessionBean())){
					if(scQtCommitteeActivity.getScCommitteeMember().getMemberRole().equals(SourcingConstants.MEMBER_ROLE_CHAIRPERSON)){
						isChairperson = true;
					}
					
					scQtCommitteeActivity.setSignedOff(true);
					committeeAct = scQtCommitteeActivity;
					break;
				}
			}
			
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setScQtCommitteeActivity(committeeAct);
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.signOff(reqDTO);
			if (resDTO.isSuccessful()) {
				
				String variable1 = getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION);
				String variable2 = getManagedBean().getScQt().getQtNo();
				if(isChairperson){
					commonMessageAcknowledmentPage(MessageConst.INFO_700049, variable1, variable2);
				}else{
					commonMessageAcknowledmentPage(MessageConst.INFO_700048, variable1, variable2);
				}
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	public void retrieveS() {
		this.getManagedBean().setOnlineBiddingActiveIndex(ONLINE_BIDDING_ACTIVE_INDEX_SUMMARY);
		//TODO
		/*IQTMeetingSO meetingSO;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);

			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteType(committeeType);
			reqDTO.setIsAttended(null);
			
			MeetingResDTO resDTO = meetingSO.retrieveSignOffCommitteeListByCommType(reqDTO);
			if (resDTO.getCommitteMembers() != null) {
				this.getManagedBean().setCommitteeEndorsementList(resDTO.getCommitteMembers());
			}
		} finally {
			meetingSO = null;
		}*/
	} 
	
	/**
	 * To retrieve the details of Online Bidding Summary
	 */
	public void retrieveBiddingSummary() {
		this.getManagedBean().setOnlineBiddingActiveIndex(ONLINE_BIDDING_ACTIVE_INDEX_SUMMARY);
		//TODO
		/*IQTMeetingSO meetingSO;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);

			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteType(committeeType);
			reqDTO.setIsAttended(null);
			
			MeetingResDTO resDTO = meetingSO.retrieveSignOffCommitteeListByCommType(reqDTO);
			if (resDTO.getCommitteMembers() != null) {
				this.getManagedBean().setCommitteeEndorsementList(resDTO.getCommitteMembers());
			}
		} finally {
			meetingSO = null;
		}*/
	}
	
	/**
	 * To retrieve the commitee of Online Bidding Endorsement
	 */
	public void retrieveOnlineBiddingEndorsement() {
		this.getManagedBean().setOnlineBiddingActiveIndex(ONLINE_BIDDING_ACTIVE_INDEX_ENDORSEMENT);
		//TODO
		/*IQTMeetingSO meetingSO;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);

			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteType(committeeType);
			reqDTO.setIsAttended(null);
			
			MeetingResDTO resDTO = meetingSO.retrieveSignOffCommitteeListByCommType(reqDTO);
			if (resDTO.getCommitteMembers() != null) {
				this.getManagedBean().setCommitteeEndorsementList(resDTO.getCommitteMembers());
			}
		} finally {
			meetingSO = null;
		}*/
	}
	
	private void commonMessageAcknowledmentPage(String messageId, String variable1, String variable2){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
			FacesMessage.SEVERITY_INFO, 
			null,
			MessageUtils.getMessage(messageId, 
					new String[]{variable1,variable2}, 
					MessageUtils.BUNDLE_COMMON_MESSAGES, 
					getFacesScopes().getUserLocale())));
		 // clear data
		super.renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
	}
	
	public SelectItem[] getMeetingStatusDropDown(){
		SelectItem[] items = getStatusDropdownValues("SC", QTConstants.DOCTYPE_MEETING);
		return items;
    }
	
	public SelectItem[] getRoleTypeDropdownValues(String committeeRole) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		
		SelectItem[] items = getDropdownValuesBySearchBean(ParameterTypeConst.COMMITTEE_ROLE, searchBean);
		return items;
	}
	
	public SelectItem[] getDecisionDropDown(){
		SelectItem[] items = getDropdownCodeValues(ParameterTypeConst.FINALIZATION_DECISION_TYPE);
		return items;
    }
	
	public SelectItem[] getRecommendTypeRadioBtn(){
		SelectItem[] items = getRadioButtonCodeValues(ParameterTypeConst.SC_RECOMMEND_TYPE);
		return items;
    }
	
	public SelectItem[] getBiddingEndorsementRadioBtn(){
		SelectItem[] items = getRadioButtonCodeValues(ParameterTypeConst.FINALIZATION_BIDDING_ENDORSEMENT);
		return items;
    }
	
	public SelectItem[] getLoiLoaPreparedByRadioBtn(){
		SelectItem[] items = getRadioButtonCodeValues(ParameterTypeConst.FINALIZATION_LOILOA_PREPARED_BY);
		return items;
    }
	
	public SelectItem[] getFwdHigherAuthorityRadioBtn(){
		SelectItem[] items = getRadioButtonCodeValues(ParameterTypeConst.YES_NO_FLAG);
		return items;
    }
	
	public SelectItem[] getFinalizationCommitteeDropDown(){
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		String committeeType = getManagedBean().getCommitteeType();
		if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B)){
			//defaulted QCA 
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",SearchOperator.OPERATOR_EQUALS.value(), ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A));
			
		}else if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A)){
			//dropdown (PBB/PBA)
			
			List<String> finalizationCommiteeList = new ArrayList<String>();
			Long userId = getFacesScopes().getUserSessionBean().getUserId();
			String orgType = null; 
			IUserSO userSO = null;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserId(userId);
				UserResDTO resDTO = userSO.retrieveUserDtlById(reqDTO);
				if (resDTO != null) {
					orgType = resDTO.getPmUser().getOrgTypeCode().getParameterCode();
				}
			} finally {
				userSO = null;
			}
			
			if (StringUtils.equals(orgType,ParameterConst.ORG_TYPE_PMS_SABAH) || 
					StringUtils.equals(orgType,ParameterConst.ORG_TYPE_PMS_SARAWAK)) {//FIXME - to filter by PTJ profile
				finalizationCommiteeList.add(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH);
				finalizationCommiteeList.add(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK);
			} else {
				finalizationCommiteeList.add(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A);
				finalizationCommiteeList.add(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B);
			}
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_IN.value(), finalizationCommiteeList));
			
		}else if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A) 
			|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)
			|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)
			|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)){
			
			//hide Finalization Committee and display "Notes: Quotation/Tender will be forward to BPK"
			getManagedBean().setForwardToBpk(true);
		}
		if (getManagedBean().getScQtFinalization().getHigherCommitte() == null) {
			getManagedBean().getScQtFinalization().setHigherCommitte(new PmParameter());
		}
		if (getManagedBean().getScQtFinalization().getReason() == null) {
			getManagedBean().getScQtFinalization().setReason(new PmReason());
		}
		
		SelectItem[] items = getDropdownValuesBySearchBean(ParameterTypeConst.SC_COMMITTEE_TYPE, searchBean);
		return items;
    }
	
	private void retrieveCommitteeSecretary(String commType){
		ICommitteSO committeSO = null;
		try{
			committeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			
			Long qtId = getManagedBean().getScQt().getQtId();
			ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
			
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteeType(commType);
				
			CommitteResDTO resDTO = committeSO.retrieveFcSecretaryUsersByCommType(reqDTO);
			List<PmUser> userList = resDTO.getUserList();
			
			if (userList != null && userList.size() > 0) {
				scQtFinalization.setHigherSecretary(userList.get(0));
			}
			
		}finally{
			committeSO = null;
		}
	}
	
	public SelectItem[] getReasonRadioBtn() {
		SelectItem[] items = null;
		Map<Long, String> reasonDescMap = new HashMap<Long, String>();
		IReasonSO reasonSO = null;
		try {
			reasonSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);
			ReasonReqDTO reqDTO = new ReasonReqDTO();
			reqDTO.setReasonCategory(ParameterConst.FINALIZATION_REASON);
			reqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
			reqDTO.setSortBy("reasonDescId");
			ReasonResDTO resDTO = reasonSO.retrieveReasonDescListByCategory(reqDTO);

			List<PmReasonDesc> reasonList = resDTO.getReasonDescList();
			items = new SelectItem[reasonList.size()];
			int i = 0;
			for (PmReasonDesc reasonDesc : reasonList) {
				items[i++] = new SelectItem(reasonDesc.getPmReason().getReasonId(), reasonDesc.getReasonDesc());
				reasonDescMap.put(reasonDesc.getPmReason().getReasonId(),reasonDesc.getReasonDesc());
			}
		} finally {
			reasonSO = null;
		}
		return items;
	}
	
	public SelectItem[] getReEvaluateByRadioBtn() {
		SelectionType[] type = SelectionType.values();
		SelectItem[] items = null;
		
		items = new SelectItem[type.length];
	    int i = 0;
	    for(SelectionType selectionType : type) {
	    	items[i++] = new SelectItem(selectionType.value(), selectionType.name());
	    }
	    return items;
	}
	
	public SelectItem[] getPackageRecommendByDropDown(){
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.RECOMMEND_BY_SINGLE_AWARD);
		recommendList.add(ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD);
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		
		SelectItem[] items = getDropdownCodeValuesBySearchBean(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, searchBean);
		return items;
    }
	
	public SelectItem[] getItemRecommendByDropDown(PmParameter priceType, ScQtRecommend scQtRecommend) {
		SelectItem[] items = null;
		String priceTypeCode = priceType.getParameterCode();
		List<String> recommendList = new ArrayList<String>();
		if (priceTypeCode.equals(ParameterConst.PRICE_TYPE_TARIFF) || 
				priceTypeCode.equals(ParameterConst.PRICE_TYPE_TIER)) {
			recommendList.add(ParameterConst.RECOMMEND_BY_SINGLE_AWARD);
			recommendList.add(ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD);
			recommendList.add(ParameterConst.RECOMMEND_BY_ONLINE_BIDDING);
			
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
			items = getDropdownCodeValuesBySearchBean(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, searchBean);
		} else {
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
			recommendList.add(ParameterConst.RECOMMEND_BY_AWARD);
			
			PmParameter itemType = scQtRecommend.getRequestItem().getTplSpecQuestion().
					getTplSpecMaster().getItemTypeCode();
			if (!(StringUtils.equals(ParameterConst.PRICE_TYPE_STANDARD, priceTypeCode) ||
					StringUtils.equals(ParameterConst.PRICE_TYPE_ZONAL_STANDARD, priceTypeCode)) ||
						StringUtils.equals(ParameterConst.ITEM_TYPE_SERVICE, itemType.getParameterCode())) {
					recommendList.add(ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD);
			}			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
			items = getDropdownCodeValuesBySearchBean(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, searchBean);
		}
		return items;
	}
	
	public SelectItem[] getAwardSupplierListDropDown(){
		if (this.getManagedBean().isItemRowSelect()) {
			return null;
		}
		List<SmSupplier> supplierList = new ArrayList<SmSupplier>();
		Set<Long> commonSupplierIdSet = new HashSet<Long>();
		List<ScQtRecommend> selectedItemList = this.getManagedBean().getSelectedRecommendList();
		Map<Long,Long> commonSupplierMap = new HashMap<Long,Long>();
		
		if (CollectionUtils.isNotEmpty(selectedItemList)) {
			// Remove duplicate items from selected list [which happens due to row selection and checkbox selection]
			Set<ScQtRecommend> selectedItemSet = new HashSet<ScQtRecommend>();
			selectedItemSet.addAll(selectedItemList);
			selectedItemList.clear();
			selectedItemList.addAll(selectedItemSet);
			selectedItemSet.clear();
		} else {
			return null;
		}
		
		if (selectedItemList.size() == 1) {
			for (ScQtEvaluationResultDtl resultDtl : selectedItemList.get(0).getShortListedSupplierList()) {
				supplierList.add(resultDtl.getQtEvaluationResult().getScQtProposal().getScQtSupplier().getSmSupplier());
			}
		} else {
			for (ScQtRecommend scQtRecommend : selectedItemList) {
				for (ScQtRecommend otherScQtRecommend : selectedItemList) {
					if (otherScQtRecommend.equals(scQtRecommend)) {
						continue;
					}
					if (CollectionUtils.isNotEmpty(scQtRecommend.getShortListedSupplierList())) {
						for (ScQtEvaluationResultDtl resultDtl : scQtRecommend.getShortListedSupplierList()) {
							SmSupplier supplier = resultDtl.getQtEvaluationResult().getScQtProposal().
									getScQtSupplier().getSmSupplier();
							if (CollectionUtils.isNotEmpty(otherScQtRecommend.getShortListedSupplierList())) {
								for (ScQtEvaluationResultDtl otherResultDtl : 
											otherScQtRecommend.getShortListedSupplierList()) {
									if (resultDtl.equals(otherResultDtl)) {
										continue;
									}
									SmSupplier otherSupplier = otherResultDtl.getQtEvaluationResult().getScQtProposal().
											getScQtSupplier().getSmSupplier();
									if ((supplier.getSupplierId()).equals(otherSupplier.getSupplierId())) {
										if (commonSupplierMap.containsKey(supplier.getSupplierId())) {
											Long repeatCount = commonSupplierMap.get(supplier.getSupplierId());
											++repeatCount;
											if (repeatCount.intValue() == selectedItemList.size()
													&& !commonSupplierIdSet.contains(supplier.getSupplierId())) {
												commonSupplierIdSet.add(supplier.getSupplierId());
												supplierList.add(supplier);
											} else {
												commonSupplierMap.put(supplier.getSupplierId(), repeatCount);
											}
										} else {
											commonSupplierMap.put(supplier.getSupplierId(), 1L);
										}
										break;
										
									} // end if
									
								} // end for
								
							} // end if
							
						} // end for
						
					} // end if 
					
				} // end for
				
			} // end for
		}
		
		commonSupplierIdSet = null;
		commonSupplierMap = null;
		
    	this.getManagedBean().setAwardSupplierList(supplierList);  	
    	
		SelectItem[] items = new SelectItem[this.getManagedBean().getAwardSupplierList().size()];
		int i = 0;
		for(SmSupplier supplier : this.getManagedBean().getAwardSupplierList()) {
			items[i++] = new SelectItem(supplier.getSupplierId().toString(),supplier.getCompanyName());
		}
		return items;
    }
	
	/**
	 * For Zonal
	 */
	public void onZoneTabChangeAction(Integer zoneTabIndex) {
		debugLogger("onZoneTabChangeAction");
		
		this.getManagedBean().setZoneActiveIndex(zoneTabIndex);
		List<TplSpecZoneDtl> zoneDtls = this.getManagedBean().getZoneDetails();
		TplSpecZoneDtl selectedZone = zoneDtls.get(zoneTabIndex);
		this.getManagedBean().setTplSpecZoneDtl(selectedZone);
		if (!this.getManagedBean().isItemize()) {
			debugLogger("Zone - Packagedwise supplier selection");
			
			setShortlistedSuppliersByZone();
		} 
		
		// To populate items specific to the selected zone
		populateScQtRecommendsByZone();
		decisionEvent();
	}
	
	/**
	 * Event for selecting decision for Recommend Supplier, Re-Evaluation,
	 * Re-Publish or Cancel in Supplier Finalization
	 */
	public void decisionEvent() {
		debugLogger("Decision Event ");
		
		this.getManagedBean().setRecommendSupplier(false);
		this.getManagedBean().setReEvaluate(false);
		this.getManagedBean().setRePublish(false);
		this.getManagedBean().setRecommendedSupplier(null);
		
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		String decision = scQtFinalization.getDecision().getParameterCode();
		if (decision == null) {
			resetFlag();
		} else if (decision.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)) {
			this.getManagedBean().setRecommendSupplier(true);
			
			//PmParameter pmParameter = getPmParameterByTypeAndCode(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, ParameterConst.RECOMMEND_BY_SINGLE_AWARD);
			//this.getManagedBean().setRecommendBy(pmParameter);

			String recommendType = scQtFinalization.getRecommendedTypeId().getParameterCode();
			if (ParameterConst.RECOMMEND_TYPE_PACKAGE.equals(recommendType)) {
				if (!isZonal) {
					this.getManagedBean().setShortlistedSupplierList(getManagedBean().getShortlistedPackageList());
				}
				
				if (scQtFinalization.getScQtRecommends().size() > 0) {
					ScQtRecommend scQtRecommend = null;
					List<TplSpecZoneDtl> zoneDetails = getManagedBean().getZoneDetails();
					if (isZonal && CollectionUtils.isNotEmpty(zoneDetails)) {
						TplSpecZoneDtl selectedZone = this.getManagedBean().getTplSpecZoneDtl();
						Set<TplSpecZoneDtl> zonalKeySet = scQtFinalization.getScQtRecommendsByZone().keySet();
						for (TplSpecZoneDtl zone : zonalKeySet) {
							if ((selectedZone.getSpecZoneId()).equals(zone.getSpecZoneId())) {
								scQtRecommend = scQtFinalization.getScQtRecommendsByZone().get(zone).get(0);
								break;
							}
						}
					} else {
						scQtRecommend = scQtFinalization.getScQtRecommends().get(0);
					}
					PmParameter recommendBy = scQtRecommend.getRecommendBy();
					getManagedBean().setRecommendBy(recommendBy.getParameterCode());
					
					//set the checkbox or radio button of supplier selected
					if(scQtRecommend.getRecommendDtls() != null && scQtRecommend.getRecommendDtls().size() > 0){
						if(recommendBy.getParameterCode().equals(ParameterConst.RECOMMEND_BY_AWARD)){
							//Award
							Long selectedSupplierId = scQtRecommend.getRecommendDtls().get(0).getSupplier().getSupplierId();
							for(ScQtEvaluationResult scQtEvaluationResult : getManagedBean().getShortlistedSupplierList()){
								Long supplierId = scQtEvaluationResult.getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierId();
								if(supplierId == selectedSupplierId){
									scQtEvaluationResult.setSelected(true);
									getManagedBean().setRecommendedSupplier(scQtEvaluationResult);
								}
							}
						}else{
							//Multiple LOI or Online Bidding
							List<Long> supplierIdList = new ArrayList<Long>();
							for(ScQtRecommendDtl scQtRecommendDtl : scQtRecommend.getRecommendDtls()){
								supplierIdList.add(scQtRecommendDtl.getSupplier().getSupplierId());
							}
							
							getManagedBean().setRecommendedSupplierList(new ArrayList<ScQtEvaluationResult>());
							for(ScQtEvaluationResult scQtEvaluationResult : getManagedBean().getShortlistedSupplierList()){
								Long supplierId = scQtEvaluationResult.getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierId();
								if(supplierIdList.contains(supplierId)){
									scQtEvaluationResult.setSelected(true);
									getManagedBean().getRecommendedSupplierList().add(scQtEvaluationResult);
								}
							}
						}
					}
				}
			} else {
//				getManagedBean().setShortlistedSupplierList(getManagedBean().getShortlistedItemList());
				List<ScQtRecommend> shortListedItemList = this.getManagedBean().getItemList();
				if (CollectionUtils.isNotEmpty(shortListedItemList)) {
					for (ScQtRecommend scQtRecommend : shortListedItemList) {
						if (isZonal) {
							TplSpecZoneDtl selectedZone = this.getManagedBean().getTplSpecZoneDtl();
							if (!(scQtRecommend.getTplSpecZoneDtl().getSpecZoneId()).equals(
									selectedZone.getSpecZoneId())) {
								continue;
							}
						}							
						PmParameter recommendBy = scQtRecommend.getRecommendBy();
						if (recommendBy == null) {
							recommendBy = getPmParameterByTypeAndCode(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, scQtRecommend.getRecommendByStr());
						} else {
							scQtRecommend.setRecommendByStr(recommendBy.getParameterCode());
						}
						
						//set the checkbox or radio button of supplier selected
						if(scQtRecommend.getRecommendDtls() != null && scQtRecommend.getRecommendDtls().size() > 0){
							//Multiple LOI or Online Bidding
							List<Long> supplierIdList = new ArrayList<Long>();
							for(ScQtRecommendDtl scQtRecommendDtl : scQtRecommend.getRecommendDtls()){
								supplierIdList.add(scQtRecommendDtl.getSupplier().getSupplierId());
							}
							scQtRecommend.setAwardSupplierList(new ArrayList<ScQtEvaluationResult>());
							getManagedBean().setRecommendedSupplierList(new ArrayList<ScQtEvaluationResult>());
							for(ScQtEvaluationResult scQtEvaluationResult : getManagedBean().getShortlistedSupplierList()){
								Long supplierId = scQtEvaluationResult.getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierId();
								if(supplierIdList.contains(supplierId)){
									scQtEvaluationResult.setSelected(true);
									for(ScQtRecommendDtl dtl : scQtRecommend.getRecommendDtls()){
										if(supplierId == dtl.getSupplier().getSupplierId()){
											scQtEvaluationResult.setQtyAward(dtl.getAwardQty());
										}
									}
									
									scQtRecommend.getAwardSupplierList().add(scQtEvaluationResult);
									getManagedBean().getRecommendedSupplierList().add(scQtEvaluationResult);
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
	
	private void resetRecommendSupplier(ScQtFinalization scQtFinalization){
		scQtFinalization.setHigherAuthReq(false);
		scQtFinalization.setHigherCommitte(null);
		scQtFinalization.setHigherSecretary(null);
		scQtFinalization.setLoiLoaBy(null);
		scQtFinalization.setReason(null);
		
		resetFlag();
	}
	
	private void resetReEvaluate(ScQtFinalization scQtFinalization){
		scQtFinalization.setReEvaluateCommitte(null);
		scQtFinalization.setReEvaluateFinReq(false);
		scQtFinalization.setReEvaluateTechReq(false);
	}
	
	private void resetRepublish(ScQtFinalization scQtFinalization){
		scQtFinalization.setSpecChangeReq(false);
	}
	
	private void resetCancel(ScQtFinalization scQtFinalization){
		
	}
	
	private void resetFlag(){
		this.getManagedBean().setOnlineBidding(false);
		this.getManagedBean().setForward(false);
		this.getManagedBean().setRecommendBy(ParameterConst.RECOMMEND_BY_SINGLE_AWARD);
	}
	
	private void populateShortListedSuppliersByZone() {

		if (CollectionUtils.isNotEmpty(this.getManagedBean().getZoneDetails())) {
			List<TplSpecZoneDtl> zoneDtls = this.getManagedBean().getZoneDetails();
			
			// Dynamically generating the tabs
			MenuModel zoneMenuModel = new DefaultMenuModel();
			if (CollectionUtils.isNotEmpty(zoneDtls)) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				int zoneTabIndex = 0;
				for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
					specZoneDtl.setZoneTabIndex(zoneTabIndex);
					
					MenuItem zoneItem = new MenuItem();
					zoneItem.setValue(specZoneDtl.getZoneName().toUpperCase());
					zoneItem.setAjax(true);
					zoneItem.setPartialSubmit(true);
					zoneItem.setProcess("@this");
					zoneItem.setUpdate("@([id$=zonalMenu],[id$=itemPanel],[id$=supplierPanel])");
					zoneItem.setActionExpression(FacesScopes
							.createMethodExpression(facesContext,
									"#{suppFinalizationBackingBean.onZoneTabChangeAction("
											+ zoneTabIndex + ")}", Void.class,
									new Class[] { Integer.class }));
					zoneMenuModel.addMenuItem(zoneItem);
					++zoneTabIndex;
				}
				setShortlistedSuppliersByZone();
			}
			this.getManagedBean().setZoneMenuModel(zoneMenuModel);
		}			
	}
	
	private void populateScQtRecommendsByZone() {
		ScQtFinalization scQtFinalization = this.getManagedBean().getScQtFinalization();
		TplSpecZoneDtl selectedZone = this.getManagedBean().getTplSpecZoneDtl();
		Set<TplSpecZoneDtl> zonalKeySet = scQtFinalization.getScQtRecommendsByZone().keySet();
		if (selectedZone != null && CollectionUtils.isNotEmpty(zonalKeySet)) {
			for (TplSpecZoneDtl zone : zonalKeySet) {
				if ((selectedZone.getSpecZoneId()).equals(zone.getSpecZoneId())) {
					this.getManagedBean().setItemList(
							scQtFinalization.getScQtRecommendsByZone().get(zone));
					break;
				}
			}
		}
	}	

	private void setShortlistedSuppliersByZone() {
		Map<TplSpecZoneDtl, List<ScQtEvaluationResult>> 
			shortlistedSupppliersByZone = this.getManagedBean().getShortlistedSupppliersByZone();
		if (MapUtils.isNotEmpty(shortlistedSupppliersByZone)) {
			TplSpecZoneDtl selectedZone = this.getManagedBean().getTplSpecZoneDtl();
			if (selectedZone == null) {
				selectedZone = (TplSpecZoneDtl) (shortlistedSupppliersByZone.keySet().toArray())[0];
			}
			for (TplSpecZoneDtl zoneDtl : shortlistedSupppliersByZone.keySet()) {
				if ((zoneDtl.getSpecZoneId()).equals(selectedZone.getSpecZoneId())) {
					this.getManagedBean().setShortlistedSupplierList(shortlistedSupppliersByZone.get(zoneDtl));
					break;
				}
			}
		}	
	}
	
	/**
	 * Event for Recommendation Type By Item or By Package in Supplier Finalization
	 */
	public void recommendTypeEvent() {
		debugLogger("recommendTypeEvent");
		
		populateRecommendTypeEventDetails(true);
	}

	public void populateRecommendTypeEventDetails(boolean triggeredFromEvent) {
		boolean	itemize = false;
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		String recommendType = scQtFinalization.getRecommendedTypeId().getParameterCode();
		String selectionMode = QTConstants.SF_SINGLE;
		String recommendByCode = getManagedBean().getRecommendBy();
		
		if (recommendType.equals(ParameterConst.RECOMMEND_TYPE_ITEM)) {
			itemize = true;
			selectionMode = QTConstants.SF_MULTIPLE;
			this.getManagedBean().setOnlineBidding(false);
			
			// Reset the awarded supplier list (ScQtRecommendDtl) by item, 
			// when recommend type is changed to item
			if (triggeredFromEvent) {
				List<ScQtRecommend> scQtRecommendList = scQtFinalization.getScQtRecommends();
				if (CollectionUtils.isNotEmpty(scQtFinalization.getScQtRecommends())) {
					for (ScQtRecommend scQtRecommend : scQtRecommendList) {
						List<ScQtRecommendDtl> recommendDtls = scQtRecommend.getRecommendDtls();
						if (CollectionUtils.isNotEmpty(recommendDtls)) {
							if (scQtRecommend.getRemoveSupplierList() == null) {
								scQtRecommend.setRemoveSupplierList(new ArrayList<ScQtRecommendDtl>());
							}
							for (ScQtRecommendDtl recommendDtl : recommendDtls) {
								if (!recommendDtl.isBrandNew()) {
									recommendDtl.setDeleted(true);
									scQtRecommend.getRemoveSupplierList().add(recommendDtl);
								}
							}
							scQtRecommend.getRecommendDtls().clear();
						}
					}
				}
			}
		} else {
			if (StringUtils.isBlank(recommendByCode)
					|| ParameterConst.RECOMMEND_BY_SINGLE_AWARD.equals(recommendByCode)) {
				getManagedBean().setRecommendBy(ParameterConst.RECOMMEND_BY_AWARD);
			} else if (ParameterConst.RECOMMEND_BY_ONLINE_BIDDING.equals(recommendByCode)) {
				this.getManagedBean().setOnlineBidding(true);
				selectionMode = QTConstants.SF_MULTIPLE;
			}
			
			boolean isZonal = this.getManagedBean().getScQt().isZonal();
			if (isZonal && 
					this.getManagedBean().getTplSpecZoneDtl() != null) {
				Long selectedZoneId = this.getManagedBean().getTplSpecZoneDtl().getSpecZoneId();
				Map<TplSpecZoneDtl, List<ScQtEvaluationResult>> 
					shortlistedSupppliersByZone = this.getManagedBean().getShortlistedSupppliersByZone();
				if (MapUtils.isNotEmpty(shortlistedSupppliersByZone)) {
					TplSpecZoneDtl currentZone = null;
					for (TplSpecZoneDtl zone : shortlistedSupppliersByZone.keySet()) {
						if ((zone.getSpecZoneId()).equals(selectedZoneId)) {
							currentZone = zone;
							break;
						}
					}
					if (currentZone == null) {
						this.getManagedBean().setShortlistedSupplierList(new ArrayList<ScQtEvaluationResult>());
					} else {
						this.getManagedBean().setShortlistedSupplierList(shortlistedSupppliersByZone.get(currentZone));
					}
				}				
			} else if (!isZonal &&
						CollectionUtils.isEmpty(this.getManagedBean().getShortlistedSupplierList()) && 
						CollectionUtils.isNotEmpty(this.getManagedBean().getShortlistedPackageList())) {
				this.getManagedBean().setShortlistedSupplierList(new ArrayList<ScQtEvaluationResult>());
				this.getManagedBean().getShortlistedSupplierList().addAll(
							this.getManagedBean().getShortlistedPackageList());
			}
		}
		this.getManagedBean().setItemize(itemize);
		this.getManagedBean().setSelectionMode(selectionMode);
	}
	
	/**
	 * Event for Recommend By Award,Online Bidding or Multiple LOI in Supplier Finalization
	 */
	public void packageRecommendByEvent() {
		debugLogger("packageRecommendByEvent ");

		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		
		String recommendByCode = getManagedBean().getRecommendBy();
		if (recommendByCode != null) {
			this.getManagedBean().setSingleAward(false);
			this.getManagedBean().setOnlineBidding(false);
			this.getManagedBean().setMultipleLoi(false);
			
			if (recommendByCode.equals(ParameterConst.RECOMMEND_BY_AWARD)) {
				this.getManagedBean().setSelectionMode(QTConstants.SF_SINGLE);
				this.getManagedBean().setSingleAward(true);
				//this.getManagedBean().setBiddingEndorsement(null);
				
				scQtFinalization.setOnlineBiddingEndorse(null);
			} else if (recommendByCode.equals(ParameterConst.RECOMMEND_BY_ONLINE_BIDDING)) {
				this.getManagedBean().setSelectionMode(QTConstants.SF_MULTIPLE);
				this.getManagedBean().setOnlineBidding(true);
			} else if (recommendByCode.equals(ParameterConst.RECOMMEND_BY_MULTIPLE_LOI)) {
				this.getManagedBean().setSelectionMode(QTConstants.SF_MULTIPLE);
				this.getManagedBean().setMultipleLoi(true);
				//this.getManagedBean().setBiddingEndorsement(null);
				
				scQtFinalization.setOnlineBiddingEndorse(null);
			}
		} else {
			getManagedBean().setRecommendBy(ParameterConst.RECOMMEND_BY_SINGLE_AWARD);
		}
	}
	
	public void itemRecommendByEvent(ScQtRecommend scQtRecommend) {
		debugLogger("itemRecommendByEvent");
		
		String recommendBy = scQtRecommend.getRecommendByStr();
		if (StringUtils.isBlank(recommendBy)) {
			return;
		}
		if (recommendBy.equals(ParameterConst.RECOMMEND_BY_AWARD) 
				|| recommendBy.equals(ParameterConst.RECOMMEND_BY_SINGLE_AWARD)) {
			//clear the list if more than 1
			if (scQtRecommend.getRecommendDtls() != null) {
				int size = scQtRecommend.getRecommendDtls().size();
				if (size > 1) {
					scQtRecommend.setAwardSupplierList(new ArrayList<ScQtEvaluationResult>());
					if (CollectionUtils.isNotEmpty(scQtRecommend.getRecommendDtls())) {
						List<ScQtRecommendDtl> toBeRemovedList = new ArrayList<ScQtRecommendDtl>();
						for (ScQtRecommendDtl recommendDtl : scQtRecommend.getRecommendDtls()) {
							recommendDtl.setDeleted(true);
							toBeRemovedList.add(recommendDtl);
						}
						scQtRecommend.setRemoveSupplierList(toBeRemovedList);
					}
					scQtRecommend.setRecommendDtls(new ArrayList<ScQtRecommendDtl>());
				}
			}
		}
		
		List<ScQtRecommend> selectedItemList = getManagedBean().getSelectedRecommendList();
		if (scQtRecommend.isSelected()) {
			if (CollectionUtils.isEmpty(selectedItemList)) {
				selectedItemList = new ArrayList<ScQtRecommend>();
				selectedItemList.add(scQtRecommend);
			} else if (!selectedItemList.contains(scQtRecommend)) {
				selectedItemList.add(scQtRecommend);
			}
		}
			
		PmParameter pmParameter = getParameterByTypeAndCode(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, recommendBy);
		scQtRecommend.setRecommendBy(pmParameter);
		scQtRecommend.setRecommendByStr(recommendBy);
	}
	
	/**
	 * Event for Forward to Higher Authority in Supplier Finalization
	 */
	public void fwdHigherAuthorityEvent() {
		debugLogger("fwdHigherAuthorityEvent");
		
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		boolean fwdHigherAuthority = scQtFinalization.isHigherAuthReq();
		//boolean	forward = false;
		
		//if (fwdHigherAuthority.equalsIgnoreCase("true")) {
			//forward = true;
		if (fwdHigherAuthority) {
			String committeeType = getManagedBean().getCommitteeType();
			if (committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A) 
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
				
				getManagedBean().setForwardToBpk(true);
			} else {
				getFinalizationCommitteeDropDown();
			}
		} else {
			//reset the commitee n reason
			//getManagedBean().setFinalizationCommittee(null);
			//getManagedBean().setCommitteeSecretary(null);
			//getManagedBean().setCommitteeSecretaryName(null);
			//getManagedBean().setReason(null);
			
			
			scQtFinalization.setHigherCommitte(null);
			scQtFinalization.setHigherSecretary(null);
			scQtFinalization.setReason(null);
		}
		//this.getManagedBean().setForward(forward);
	}
	
	public void finalizationCommitteeEvent() {
		debugLogger("finalizationCommitteeEvent");
		
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		Long paramId = scQtFinalization.getHigherCommitte().getParameterId();
		PmParameter parameter = ParameterHelper.getInstance().getPmParameterById(paramId);
		scQtFinalization.setHigherCommitte(parameter);
		scQtFinalization.setHigherCommitteStr(parameter.getParameterCode());
		retrieveCommitteeSecretary(parameter.getParameterCode());
	}
	
	public void updateOfferedPrice(ScQtRecommendDtl recommendDtl) {
		debugLogger("updateOfferedPrice...");
		
		boolean isItemRowSelected = this.getManagedBean().isItemRowSelect();
		if (isItemRowSelected && recommendDtl != null) {
			SmSupplier currentSupplier = recommendDtl.getSupplier();
			List<ScQtEvaluationResultDtl> shortlistedItemSupplierList =
					this.getManagedBean().getShortlistedItemSupplierList();
			if (currentSupplier != null && 
					CollectionUtils.isNotEmpty(shortlistedItemSupplierList)) {
				for (ScQtEvaluationResultDtl evalResultDtl : shortlistedItemSupplierList) {
					SmSupplier shortListedSupplier = evalResultDtl.getQtEvaluationResult().getScQtProposal().
							getScQtSupplier().getSmSupplier();
					ScQtProposalItem proposalItem = evalResultDtl.getQtEvaluationResult().getScQtProposalItem();
					if (proposalItem != null && 
							(currentSupplier.getSupplierId()).equals(
							shortListedSupplier.getSupplierId())) {
						ScRequestItem requestItem = recommendDtl.getScQtRecommend().getRequestItem();
						BigDecimal offeredPrice = TemplateSpecHelper.getInstance().calcTotalAmount(requestItem.getTplSpecQuestion(), 
														new BigDecimal(proposalItem.getOfferPrice()), recommendDtl.getAwardQty());
						evalResultDtl.getQtEvaluationResult().setOfferedPrice(offeredPrice);
					}
				}
			}
		}
	}
	
	public void selectItemEvent(SelectEvent event) {
		debugLogger("selectItemEvent");
		
		this.getManagedBean().setItemRowSelect(true);
		
		// Reset selection
		this.getManagedBean().setSelectedRecommendList(null);
		this.getManagedBean().setShortlistedItemSupplierList(null);
		this.getManagedBean().setSelectAll(false);
		ScQtRecommend scQtRecommend = (ScQtRecommend) event.getObject();
		scQtRecommend.setSelected(true);
		addSelectedItem(scQtRecommend);
		super.selectAll(false, this.getManagedBean().getItemList());
	}

	public void unselectItemEvent(UnselectEvent event) {
		debugLogger("unselectItemEvent");

		ScQtRecommend scQtRecommend = (ScQtRecommend) event.getObject();
		scQtRecommend.setSelected(false);
		selectItemEvent(scQtRecommend);		
	}
	
	public void selectItemEvent(ScQtRecommend scQtRecommend) {
		this.getManagedBean().setItemRowSelect(false);
		addSelectedItem(scQtRecommend);
	}
	
	public void addSelectedItem(ScQtRecommend scQtRecommend) {
		
		if (getManagedBean().getSelectedRecommendList() == null) {
			getManagedBean().setSelectedRecommendList(new ArrayList<ScQtRecommend>());
		}
		if (scQtRecommend.isSelected()) {
			scQtRecommend.setAwardSupplierList(new ArrayList<ScQtEvaluationResult>());
			List<ScQtEvaluationResultDtl> shortlistedItemSupplierList = null;
			boolean isSuccessful = false;
			if (CollectionUtils.isEmpty(scQtRecommend.getShortListedSupplierList())) {
				// Retrieve the suppliers short listed for the selected item
				SuppFinalizationResDTO resDTO = this.getShortListedSuppliersByItem(scQtRecommend);
				isSuccessful = resDTO.isSuccessful();
				shortlistedItemSupplierList = resDTO.getQualifiedByItemSuppliers();
			} else {
				isSuccessful = true;
				shortlistedItemSupplierList = scQtRecommend.getShortListedSupplierList();
			}
			if (isSuccessful) {
				if (CollectionUtils.isNotEmpty(shortlistedItemSupplierList)) {
					for (ScQtEvaluationResultDtl evalResultDtl :
													shortlistedItemSupplierList) {
						calculateOfferedPrice(evalResultDtl, scQtRecommend.getRequestItem());
					}
				}
			}
			if (this.getManagedBean().isItemRowSelect()) {
				this.getManagedBean().setShortlistedItemSupplierList(shortlistedItemSupplierList);
			} else {
				this.getManagedBean().setShortlistedItemSupplierList(null);
			}
			scQtRecommend.setShortListedSupplierList(shortlistedItemSupplierList);
			getManagedBean().getSelectedRecommendList().add(scQtRecommend);
			
			List<ScQtEvaluationResultDtl> recommendedSupplierList = new ArrayList<ScQtEvaluationResultDtl>();
			List<ScQtRecommendDtl> recommendDtls = scQtRecommend.getRecommendDtls();
			if (CollectionUtils.isNotEmpty(shortlistedItemSupplierList)
					&& CollectionUtils.isNotEmpty(recommendDtls)) {
				for (ScQtRecommendDtl recommendDtl : recommendDtls) {
					for (ScQtEvaluationResultDtl evalResultDtl : shortlistedItemSupplierList) {
						Long recommendedSupplierId = recommendDtl.getSupplier().getSupplierId();
						Long shortlistedSupplierId = evalResultDtl.getQtEvaluationResult().getScQtProposal()
														.getScQtSupplier().getSmSupplier().getSupplierId();
						if (shortlistedSupplierId.equals(recommendedSupplierId)) {
							scQtRecommend.getAwardSupplierList().add(evalResultDtl.getQtEvaluationResult());
							recommendedSupplierList.add(evalResultDtl);
						}
					}
				}
			}
			getManagedBean().setRecommendedItemSupplierList(recommendedSupplierList);	
		} else {
			getManagedBean().setShortlistedItemSupplierList(new ArrayList<ScQtEvaluationResultDtl>());
			getManagedBean().getSelectedRecommendList().remove(scQtRecommend);
			getManagedBean().setSelectAll(false);
		}		
	}
	
	public void selectAllItems(boolean selectAll) {
		debugLogger("selectAllItemsEvent");
		
		this.getManagedBean().setItemRowSelect(false);
		List<ScQtRecommend> selectedItemsList = this.getManagedBean().getItemList();
		if (selectAll) {
			if (CollectionUtils.isNotEmpty(selectedItemsList)) {
				for (ScQtRecommend scQtRecommend : selectedItemsList) {
					if (CollectionUtils.isEmpty(scQtRecommend.getShortListedSupplierList())) {
						scQtRecommend.setSelected(true);
						addSelectedItem(scQtRecommend);
					}
				}
			}
			this.getManagedBean().setSelectedRecommendList(selectedItemsList);				
		} else {
			this.getManagedBean().setSelectedRecommendList(null);
		}
		this.getManagedBean().setShortlistedItemSupplierList(null);
		this.getManagedBean().setSelectAll(selectAll);
		super.selectAll(selectAll,selectedItemsList);
	}	
	
	public void selectSupplierEvent() {
		debugLogger("selectSupplierEvent");
		
		addSelectedSupplier();
	}
	
	private void addSelectedSupplier() {
		boolean recommendBySelectionFlag = true;
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();		
		String recommendType = scQtFinalization.getRecommendedTypeId().getParameterCode();
		List<ScQtRecommend> selectedItemList = getManagedBean().getSelectedRecommendList();
		if (CollectionUtils.isNotEmpty(selectedItemList)) {
			List<ScQtEvaluationResultDtl> recomendedList = getManagedBean().getRecommendedItemSupplierList();
			for (ScQtRecommend scQtRecommend : selectedItemList) {
				PmParameter pmParameter = scQtRecommend.getRecommendBy();
				
				// Reset selection
				if (StringUtils.isBlank(scQtRecommend.getRecommendByStr())) {
					recommendBySelectionFlag = false;
				} else {
					scQtRecommend.setSelected(false);
				}
				scQtRecommend.setAwardSupplierList(new ArrayList<ScQtEvaluationResult>());
				
				if (CollectionUtils.isNotEmpty(recomendedList)) {
					ScQtRecommendDtl selectedRecommendDtl = null;
					Set<ScQtRecommendDtl> toBeAddedSet = new LinkedHashSet<ScQtRecommendDtl>();
					if (!this.getManagedBean().isItemRowSelect()) {
						if (scQtRecommend.getRecommendDtls() != null) {
							toBeAddedSet.addAll(scQtRecommend.getRecommendDtls());
						}
					} 
					for (ScQtEvaluationResultDtl recomendedSupplier : recomendedList) {
						boolean alreadyExists = false; 
						scQtRecommend.getAwardSupplierList().add(recomendedSupplier.getQtEvaluationResult());
						List<ScQtRecommendDtl> removedList = scQtRecommend.getRemoveSupplierList();
						for (ScQtRecommendDtl detail : scQtRecommend.getRecommendDtls()) {
							if ((detail.getSupplier().getSupplierId())
									.equals(recomendedSupplier.getQtEvaluationResult()
											.getScQtProposal().getScQtSupplier()
											.getSmSupplier().getSupplierId())) {
								alreadyExists = true;
								checkAndRemoveFromDeletedList(removedList, detail);
								setUserDetails(detail);
								toBeAddedSet.add(detail);	
								detail.setShortlistedSupplier(recomendedSupplier.getQtEvaluationResult());
								break;
							}
						}
						if (!alreadyExists) {
							selectedRecommendDtl = setSelectedSupplier(recomendedSupplier.getQtEvaluationResult(), 
									scQtRecommend, null, removedList);	
							setUserDetails(selectedRecommendDtl);
							toBeAddedSet.add(selectedRecommendDtl);
						}
					}
					if (scQtRecommend.getRemoveSupplierList() == null) {
						scQtRecommend.setRemoveSupplierList(new ArrayList<ScQtRecommendDtl>());
					}
					for (ScQtRecommendDtl recommendDtl : scQtRecommend.getRecommendDtls()) {
						if (!recommendDtl.isBrandNew() && 
								!toBeAddedSet.contains(recommendDtl)) {
							recommendDtl.setDeleted(true);
							if (!scQtRecommend.getRemoveSupplierList().contains(recommendDtl)) {
								scQtRecommend.getRemoveSupplierList().add(recommendDtl);
							}
						}
					}
					scQtRecommend.getRecommendDtls().clear();
					scQtRecommend.getRecommendDtls().addAll(toBeAddedSet);
				} else {
					scQtRecommend.setAwardSupplierList(new ArrayList<ScQtEvaluationResult>());
					if (scQtRecommend.getRemoveSupplierList() == null) {
						scQtRecommend.setRemoveSupplierList(new ArrayList<ScQtRecommendDtl>());
					}
					if (CollectionUtils.isNotEmpty(scQtRecommend.getRecommendDtls())) {
						for (ScQtRecommendDtl recommendDtl : scQtRecommend.getRecommendDtls()) {
							if (!recommendDtl.isBrandNew()) {
								if (!scQtRecommend.getRemoveSupplierList().contains(recommendDtl)) {
									recommendDtl.setDeleted(true);
									scQtRecommend.getRemoveSupplierList().add(recommendDtl);
								}
							}
						}
					}
					scQtRecommend.setRecommendDtls(new ArrayList<ScQtRecommendDtl>());
				}
				
				if(recommendType.equalsIgnoreCase(ParameterConst.RECOMMEND_TYPE_ITEM) 
						&& scQtRecommend.getAwardSupplierList() != null){
					ScRequestItem scRequestItem = scQtRecommend.getRequestItem();
					String priceType = scRequestItem.getTplSpecQuestion().getClPriceTypeCode().getParameterCode();
					
					String parameterType = ParameterTypeConst.FINALIZATION_RECOMMEND_BY;
					String recommendBy = "";
					
					if(priceType.equals(ParameterConst.PRICE_TYPE_TARIFF) || priceType.equals(ParameterConst.PRICE_TYPE_TIER)){
						if(scQtRecommend.getAwardSupplierList().size()==0){
							pmParameter = null;
						}else if(scQtRecommend.getAwardSupplierList().size()==1){
							if(pmParameter == null){
								recommendBy = ParameterConst.RECOMMEND_BY_SINGLE_AWARD;
								pmParameter = getPmParameterByTypeAndCode(parameterType,recommendBy);
							}else{
								recommendBy = pmParameter.getParameterCode();
							}
						}else if(scQtRecommend.getAwardSupplierList().size()>1){
							//If its price type tier/tariff, should clear the recommend by, 
							//user need to select multiple LOI or online bidding
							if(pmParameter == null || pmParameter.getParameterCode().equals(ParameterConst.RECOMMEND_BY_SINGLE_AWARD)){
								recommendBy = ParameterConst.RECOMMEND_BY_MULTIPLE_LOI;
								pmParameter = getPmParameterByTypeAndCode(parameterType,recommendBy);
							}else{
								recommendBy = pmParameter.getParameterCode();
							}
						}
					}else{
						if(scQtRecommend.getAwardSupplierList().size()==0){
							pmParameter = null;
						}else if(scQtRecommend.getAwardSupplierList().size()==1){
							if(pmParameter == null || pmParameter.getParameterCode().equals(ParameterConst.RECOMMEND_BY_SINGLE_AWARD)){
								recommendBy = ParameterConst.RECOMMEND_BY_SINGLE_AWARD;
								pmParameter = getPmParameterByTypeAndCode(parameterType,recommendBy);
							}else{
								recommendBy = pmParameter.getParameterCode();
							}
						}else if(scQtRecommend.getAwardSupplierList().size()>1){
							if(pmParameter == null || pmParameter.getParameterCode().equals(ParameterConst.RECOMMEND_BY_SINGLE_AWARD)){
								recommendBy = ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD;
								pmParameter = getPmParameterByTypeAndCode(parameterType,recommendBy);
							}else{
								recommendBy = pmParameter.getParameterCode();
							}
						}
					}
					scQtRecommend.setRecommendBy(pmParameter);
					scQtRecommend.setRecommendByStr(recommendBy);
				}
			}
			if (recommendBySelectionFlag == false) {
				this.getManagedBean().setSelectAll(false);
				throw new EpException(MessageConst.ERROR_760211);
			}
		}		
		this.getManagedBean().setSelectAll(false);
	}
	
	private void calculateOfferedPrice(ScQtEvaluationResultDtl evalResultDtl, ScRequestItem requestItem) {
		ScQtProposalItem proposalItem = evalResultDtl.getQtEvaluationResult().getScQtProposalItem();
		if (proposalItem != null) {
			BigDecimal offeredPrice = TemplateSpecHelper.getInstance().calcTotalAmount(requestItem.getTplSpecQuestion(), 
											new BigDecimal(proposalItem.getOfferPrice()), new BigDecimal(proposalItem.getOfferQuantity()));
			evalResultDtl.getQtEvaluationResult().setOfferedPrice(offeredPrice);
		}
	}
	
	private SuppFinalizationResDTO getShortListedSuppliersByItem(ScQtRecommend scQtRecommend) {
		SuppFinalizationResDTO resDTO = new SuppFinalizationResDTO();
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, 
					IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			if (scQtRecommend.getTplSpecZoneDtl() != null) {
				reqDTO.setQtZoneCriteriaId(scQtRecommend.getTplSpecZoneDtl().getSpecZoneId());
			}
			reqDTO.setRequestItemId(scQtRecommend.getRequestItem().getRequestNoteItemId());
			reqDTO.setRecommendType(SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM);
			
			resDTO = qtSuppFinalizationSO.retrieveEvaluationResultsByItem(reqDTO);
		} finally {
			qtSuppFinalizationSO = null;
		}	
		
		return resDTO;
	}
	
	private QTSupplierResDTO retrieveSupplierDetails(SmSupplier smSupplier) {
		QTSupplierResDTO resDTO = new QTSupplierResDTO();
		IQTSupplierSO qtSupplierSO = null;
		try {
			qtSupplierSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, 
					IQTSupplierSO.class);
			QTSupplierReqDTO reqDTO = new QTSupplierReqDTO();
			reqDTO.setSupplierId(smSupplier.getSupplierId());
			resDTO = qtSupplierSO.retrieveSupplierDetails(reqDTO);
		} finally {
			qtSupplierSO = null;
		}	
		return resDTO;
	}
	
	public void unSelectSupplierEvent(ScQtEvaluationResult scQtEvaluationResult) {
		debugLogger("unSelectSupplierEvent");
		
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		String recommendType = scQtFinalization.getRecommendedTypeId().getParameterCode();
		if(recommendType.equals(ParameterConst.RECOMMEND_TYPE_PACKAGE)){
			scQtEvaluationResult.setDeleted(true);
			if(getManagedBean().getUnRecommendedSupplierList() == null){
				getManagedBean().setUnRecommendedSupplierList(new ArrayList<ScQtEvaluationResult>());
			}
			getManagedBean().getUnRecommendedSupplierList().add(scQtEvaluationResult);
		} else {
			addSelectedSupplier();
		}
	}
	
	public void unSelectSupplierEvent(ScQtEvaluationResultDtl scQtEvaluationResultDtl) {
		debugLogger("unSelectSupplierEvent for recommend type - Itemized");
		
		addSelectedSupplier();
	}	
	
/*	private PmParameter getPmParameterById(Long parameterId) {
		IParameterSO parameterSO = null;
		try {
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			return resDTO.getPmParameter();
		} finally {
			parameterSO = null;
		}
	}*/
	
	private PmParameter getPmParameterByTypeAndCode(String parameterType, String parameterCode) {
		IParameterSO parameterSO = null;
		try {
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterCode(parameterCode);
			reqDTO.setParameterType(parameterType);
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			return resDTO.getPmParameter();
		} finally {
			parameterSO = null;
		}
	}
	
	/**
	 * Event for Award Supplier in Supplier Finalization
	 * It will add the supplier for the item selected
	 */
	public void awardSupplierEvent() {
		debugLogger("awardSupplierEvent");
		
		String awardSupplierStr = getManagedBean().getAwardSupplier();
		List<ScQtRecommend> selectedItemList = this.getManagedBean().getSelectedRecommendList();
		if (StringUtils.isBlank(awardSupplierStr) ||
				CollectionUtils.isEmpty(selectedItemList)) {
			log.info("No supplier has been selected or no item has been selected");
			return;
		}
		
		// Remove duplicate items from selected list [which happens due to row selection and checkbox selection]
		Set<ScQtRecommend> selectedItemSet = new HashSet<ScQtRecommend>();
		selectedItemSet.addAll(selectedItemList);
		selectedItemList.clear();
		selectedItemList.addAll(selectedItemSet);
		selectedItemSet = null;
		
		List<ScQtEvaluationResultDtl> recommendedItemSupplierList = getManagedBean().getRecommendedItemSupplierList();
		if (recommendedItemSupplierList == null) {
			recommendedItemSupplierList = new ArrayList<ScQtEvaluationResultDtl>();
		}
		
		if (CollectionUtils.isNotEmpty(selectedItemList)) {
			boolean supplierFound = false;
			ScQtRecommend removableItem = null; // Remove item added due to row selection followed by checkbox selection
			
			//get the SmSupplier object by the dropdown selection
			for (ScQtRecommend scQtRecommend : selectedItemList) {
				if (!scQtRecommend.isSelected()) {
					removableItem = scQtRecommend;
					continue;
				}
				List<ScQtEvaluationResultDtl> shortListedSupplierList = scQtRecommend.getShortListedSupplierList();
				if (CollectionUtils.isNotEmpty(shortListedSupplierList)) {
					for (ScQtEvaluationResultDtl evalResultDtl : shortListedSupplierList) {
						String suppId = String.valueOf(evalResultDtl.getQtEvaluationResult().getScQtProposal()
														.getScQtSupplier().getSmSupplier().getSupplierId());
						if (suppId.equalsIgnoreCase(awardSupplierStr)) {
							supplierFound = true;
							if (!recommendedItemSupplierList.contains(evalResultDtl)) {
								recommendedItemSupplierList.add(evalResultDtl);
								this.getManagedBean().setRecommendedItemSupplierList(recommendedItemSupplierList);
							}
							this.getManagedBean().setRecommendedSupplier(evalResultDtl.getQtEvaluationResult());
							break;
						}
					}
					if (supplierFound) {
						break;
					}
				}
			}
			if (removableItem != null) {
				selectedItemList.remove(removableItem);
			}
			addSelectedSupplier();
			
			this.getManagedBean().setShortlistedItemSupplierList(null);
			this.getManagedBean().setRecommendedItemSupplierList(null);
			this.getManagedBean().setSelectedRecommendList(null);
			this.getManagedBean().setAwardSupplierList(null); 
			this.getManagedBean().setAwardSupplier(null);
		} else {
			log.info("There are no selected items");
			return;
		}
	}
	
	private void validateAwardOfItemsToSuppliersByPackage(List<ScQtRecommend> scQtRecommendList) {
		for (ScQtRecommend scQtRecommend : scQtRecommendList) {
			if (CollectionUtils.isEmpty(scQtRecommend.getRecommendDtls())) {
				setZoneAndTabIndexOnException(scQtRecommend.getTplSpecZoneDtl());
				throw new EpException(MessageConst.ERROR_760206); 
			}
		}
	}
	
	private void validateAwardOfItem(ScQtRecommend scQtRecommend) {
		if (scQtRecommend.getRecommendBy() == null) {
			setZoneAndTabIndexOnException(scQtRecommend.getTplSpecZoneDtl());
			throw new EpException(MessageConst.ERROR_760211);
		}
		
		String recommendByCode = scQtRecommend.getRecommendBy().getParameterCode();
		String recommendByDesc = getParameterDesc(scQtRecommend.getRecommendBy());
		String itemName = scQtRecommend.getRequestItem().getItemName();
		List<ScQtRecommendDtl> recommendDtlList = scQtRecommend.getRecommendDtls();
		if (StringUtils.equals(ParameterConst.RECOMMEND_BY_SINGLE_AWARD,
				scQtRecommend.getRecommendByStr()) && 
					(recommendDtlList == null ||
						recommendDtlList.size() != 1)) {
			setZoneAndTabIndexOnException(scQtRecommend.getTplSpecZoneDtl());
			throw new EpException(MessageConst.ERROR_760213);
		}		
		if (StringUtils.equals(ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD, 
				recommendByCode)) {
			if (recommendDtlList == null || 
					recommendDtlList.size() < 2) {
				setZoneAndTabIndexOnException(scQtRecommend.getTplSpecZoneDtl());
				throw new EpException(MessageConst.ERROR_760223, new String[]{itemName, recommendByDesc});
			} else {
				BigDecimal totalQuantity = scQtRecommend.getRequestItem().getQuantity();
				BigDecimal tempQuantity = new BigDecimal(0.0);
				for (ScQtRecommendDtl recommendDtl : recommendDtlList) {
					BigDecimal quantityAwarded = recommendDtl.getAwardQty();
					if (quantityAwarded == null 
							|| quantityAwarded.compareTo(totalQuantity) >= 0) {
						setZoneAndTabIndexOnException(scQtRecommend.getTplSpecZoneDtl());
						throw new EpException(MessageConst.ERROR_760214);
					}
					tempQuantity = tempQuantity.add(quantityAwarded);
				}
				if (totalQuantity.compareTo(tempQuantity) != 0) {
					setZoneAndTabIndexOnException(scQtRecommend.getTplSpecZoneDtl());
					throw new EpException(MessageConst.ERROR_760214);
				}
			}
		}	
		if (StringUtils.equals(ParameterConst.RECOMMEND_BY_ONLINE_BIDDING, 
				recommendByCode) ||
				StringUtils.equals(ParameterConst.RECOMMEND_BY_MULTIPLE_LOI, 
						recommendByCode)) {
			if (recommendDtlList == null || 
					recommendDtlList.size() < 2) {
				setZoneAndTabIndexOnException(scQtRecommend.getTplSpecZoneDtl());
				throw new EpException(MessageConst.ERROR_760223, new String[]{itemName, recommendByDesc});
			}
		}
		
	}
	
	public String getMofAccountStatus(SmMofAccount mofAccount) {
		switch (mofAccount.getRecordStatusFlg()) {
			case DbValueConst.RECORD_STATUS_ACTIVE : 
					return "ACTIVE";
			case DbValueConst.RECORD_STATUS_CANCELLED : 
					return "CANCELLED";
			case DbValueConst.RECORD_STATUS_DELETED : 
					return "DELETED";
			case DbValueConst.RECORD_STATUS_EXPIRED : 
					return "EXPIRED";
			case DbValueConst.RECORD_STATUS_PENDING_ACTIVATION : 
					return "PENDING ACTIVATION";
			case DbValueConst.RECORD_STATUS_SUSPENDED : 
					return "SUSPENDED";
		}
		
		return StringUtils.EMPTY;
	}
	
	public void selectSupplierDropDownEvent() {
		debugLogger("selectSupplierDropDownEvent");
		
		String awardSupplier = getManagedBean().getAwardSupplier();
		getManagedBean().setAwardSupplier(awardSupplier);
	}
	
	private void setZoneAndTabIndexOnException(TplSpecZoneDtl zoneDtl) {
		int currentActiveIndex = getManagedBean().getActiveIndex();
		if (currentActiveIndex != 2) {
			supplierFinalizationRetrival();
		}
		this.getManagedBean().setActiveIndex(2);
		List<TplSpecZoneDtl> specZoneDtls = this.getManagedBean().getZoneDetails();
		if (zoneDtl != null && CollectionUtils.isNotEmpty(specZoneDtls)) {
			for (TplSpecZoneDtl specZoneDtl : specZoneDtls) {
				if ((specZoneDtl.getSpecZoneId()).equals(zoneDtl.getSpecZoneId())) {
					this.getManagedBean().setTplSpecZoneDtl(specZoneDtl);
					this.getManagedBean().setZoneActiveIndex(specZoneDtl.getZoneTabIndex());
					onZoneTabChangeAction(specZoneDtl.getZoneTabIndex());
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
        if(log.isDebugEnabled()) {
            log.debug(message);
        }log.info(message);
    }
	
	/**
	 * @return the currentTab
	 */
	public int getCurrentTab() {
		return currentTab;
	}

	/**
	 * @param currentTab the currentTab to set
	 */
	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	public void gpkiSignAction(PmDigiSign digiSign) {
		updateBPMTask(this.getManagedBean().getAction(), digiSign);
	}

	public String getTextBeforeSign() {
		StringBuilder sb = new StringBuilder();
		if(getManagedBean().getScQt() != null){
			sb.append(getManagedBean().getScQt().getQtNo());
		}
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		textBeforeSign = sb.toString();
		return textBeforeSign;
	}

	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}
	
}
