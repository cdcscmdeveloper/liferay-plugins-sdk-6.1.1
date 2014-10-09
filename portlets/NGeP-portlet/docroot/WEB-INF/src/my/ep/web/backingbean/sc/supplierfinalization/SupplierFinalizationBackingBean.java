package my.ep.web.backingbean.sc.supplierfinalization;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
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
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.NoticeBoardReqDTO;
import my.ep.dto.sc.quationtender.NoticeBoardResDTO;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.ProposalResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.supplierfinalization.SupplierFinalizationManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pp.common.CommonMethod;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * @author Faradiana
 * Supplier Finalization Backing Bean
 */
/**
 * @author User
 *
 */
@ManagedBean(name = "suppFinalizationBackingBean")
@RequestScoped
public class SupplierFinalizationBackingBean extends SourcingBaseBackingBean<SupplierFinalizationManagedBean> {
	private static final Logger log = EpLogger.getLogger(SupplierFinalizationBackingBean.class);

	private int currentTab;
	
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
	public void onTabMenuChangeAction(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();
		int previousIndex = this.getManagedBean().getActiveIndex();
		validateInfo(previousIndex, false);
		
		if (tabTitle.equalsIgnoreCase(CommonMethod.getMessageCommon("lbl.sc.preparation"))) {
			preparationRetrival();

		} else if (tabTitle.equalsIgnoreCase(CommonMethod.getMessageCommon("lbl.sc.briefing.paper"))) {
			biefingPaperRetrival();
		
		} else if (tabTitle.equalsIgnoreCase(CommonMethod.getMessageCommon("lbl.sc.supplier.finalization"))) {
			supplierFinalizationRetrival();
	
		} else if (tabTitle.equalsIgnoreCase(CommonMethod.getMessageCommon("lbl.sc.committee.endorsement"))) {
			committeeEndorsementRetrival();
		}
		
		//Meeting Details
		List<ScQtMeeting> meetingList = getManagedBean().getMeetingList();
		getManagedBean().setMeetingList(meetingList);
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
		
		ScQt scQt = getManagedBean().getScQt();
		retrieveSupplierFinalizationDtls(scQt);
		
		retrieveBriefingPaper();
	}
	
	private void supplierFinalizationRetrival(){
		this.getManagedBean().setActiveIndex(ACTIVE_INDEX_SUPP_FINALIZATION);
		
		ScQt scQt = getManagedBean().getScQt();
		retrieveSupplierFinalizationDtls(scQt);
		decisionEvent();
		recommendTypeEvent();
		fwdHigherAuthorityEvent();
		packageRecommendByEvent();
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
				this.getManagedBean().setMeetingList(meetingList);
				
				ScQtCommittee scQtCommittee = new ScQtCommittee();
				scQtCommittee = resDTO.getQtCommittee();
				this.getManagedBean().setScQtCommittee(scQtCommittee);
				
				//Committee Details
				if (scQtCommittee.getCommitteeActivities() != null && !scQtCommittee.getCommitteeActivities().isEmpty()) {
					this.getManagedBean().setCommitteeList(scQtCommittee.getCommitteeActivities());
				}
				
				List<ScQtCommitteeActivity> activityList = scQtCommittee.getCommitteeActivities();
				List<ScCommitteeMember> memberList = scQtCommittee.getScCommittee().getScCommitteeMembers();
				if(activityList != null){
					if(memberList.size() != activityList.size()){
						List<ScQtCommitteeActivity> scQtCommitteeActivitys = new ArrayList<ScQtCommitteeActivity>();
						for(ScCommitteeMember member : memberList){
							ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
							activity.setScQtCommittee(scQtCommittee);
							activity.setScCommitteeMember(member);
							setUserDetails(activity);
							
							scQtCommitteeActivitys.add(activity);
						}
						scQtCommittee.setCommitteeActivities(scQtCommitteeActivitys);
						this.getManagedBean().setCommitteeList(scQtCommitteeActivitys);
					}
				}
				
				//Meeting Attendance Details
				if (scQtCommittee.getMeetingAttendances() != null && !scQtCommittee.getMeetingAttendances().isEmpty()) {
					this.getManagedBean().setMeetingAttendanceList(scQtCommittee.getMeetingAttendances());
				}
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
				label = CommonMethod.getMessageCommon("lbl.sc.committee.qca");
			
			}else if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B)){
				label = CommonMethod.getMessageCommon("lbl.sc.committee.qcb");
			
			}else if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)){
				label = CommonMethod.getMessageCommon("lbl.sc.committee.pba");
			
			}else if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)){
				label = CommonMethod.getMessageCommon("lbl.sc.committee.pbb");
			
			}else if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)){
				label = CommonMethod.getMessageCommon("lbl.sc.committee.fpb.sbh");
			
			}else if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)){
				label = CommonMethod.getMessageCommon("lbl.sc.committee.fpb.swk");
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
			statusreq.setStatusId(1L);//FIXME
			StatusResDTO statusres = statussO.retrieveStatusByStatusId(statusreq);
			meeting.setMeetingStatus(statusres.getPmStatus());
			
			meetingList.add(meeting);
			this.getManagedBean().setMeetingList(meetingList);
		} finally {
		
		}
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
	
	public void initializeRemarks() {
		debugLogger("..In initializeRemarks()..");
		
		IStatusSO iStatusSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		ScQtCommitteRemarks remark = null;
		if (this.getManagedBean().getCommitteeRemarkList() == null) {
			this.getManagedBean().setCommitteeRemarkList(new ArrayList<ScQtCommitteRemarks>());
		}

		remark = new ScQtCommitteRemarks();
		remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
		
		remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		remark.setChangedBy(remark.getCreatedBy());
		remark.setCreatedUser(getFacesScopes().getUserSessionBean());
		remark.setScQtCommittee(this.getManagedBean().getScQtCommittee());
		PmStatus status = new PmStatus();
		status.setStatusId(1L);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		if (status != null) {
			statusReqDTO.setStatusId(status.getStatusId());
			StatusResDTO statusResDTO = iStatusSO.retrieveStatusByStatusId(statusReqDTO);
			remark.setPmStatus(statusResDTO.getPmStatus());
		}
		this.getManagedBean().getCommitteeRemarkList().add(remark);
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
	
	public void saveSupplierFinalization(){
		debugLogger("..In saveSupplierFinalization()..");
		
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		int activeIndex = this.getManagedBean().getActiveIndex();
		int prepareActiveIndex = this.getManagedBean().getPrepareActiveIndex();
		
		String taskId = this.getManagedBean().getTaskId();
		String[] outComes = this.getManagedBean().getOutComes();
		String processName = this.getManagedBean().getProcessName();
		
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			
			SuppFinalizationReqDTO reqDTO = validateInfo(activeIndex, true);
			qtSuppFinalizationSO.saveSupplierFinalization(reqDTO);
			
			renewSessionAndRetrieve(activeIndex);
			setTaskDetails(activeIndex, prepareActiveIndex, taskId, outComes, processName);
			
			String qtNo = getManagedBean().getScQt().getQtNo().toString();
			String desc = getParameterDesc( ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_700004, new String[] {desc,qtNo},
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	private SuppFinalizationReqDTO validateInfo(int index, boolean isSave){
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
				reqDTO.setScQtCommittee(scQtCommittee);
				
				qtSuppFinalizationSO.validatePreparation(reqDTO);
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
				
				if(isSave){
					String decisionTypeCode = scQtFinalization.getDecision().getParameterCode();
					if(decisionTypeCode.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)){
						
						String recommendTypeCode = scQtFinalization.getRecommendedTypeId().getParameterCode();
						if(recommendTypeCode.equals(ParameterConst.RECOMMEND_TYPE_PACKAGE)){
							saveByPackage(scQtFinalization);
						
						}else if(recommendTypeCode.equals(ParameterConst.RECOMMEND_TYPE_ITEM)){
							saveByItem(scQtFinalization);
						}
					}
				}
				reqDTO.setScQtFinalization(scQtFinalization);
				
				qtSuppFinalizationSO.validateSupplierFinalization(reqDTO);
				break;
				
			case 3: 
				//Committee Endorsement Tab
				//no validation
				break;
			}
			return reqDTO;
		} finally {
		}
	}
	
	private void renewSessionAndRetrieve(int activeIndex){
		Long qtId = getManagedBean().getScQt().getQtId();
		String committeeType = getManagedBean().getCommitteeType();
		
		renewSesionObject();
		
		getManagedBean().setCommitteeType(committeeType);
		retrieveQTDetails(qtId);
		
		switch (activeIndex) {
		case 0:
			preparationRetrival();
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
	
	private void setTaskDetails(Integer activeIndex, Integer prepareActiveIndex, String taskId, String[] outComes, String processName){
		if(activeIndex != null){
			this.getManagedBean().setActiveIndex(activeIndex);
		}
		if(prepareActiveIndex != null){
			this.getManagedBean().setPrepareActiveIndex(prepareActiveIndex);
		}
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setOutComes(outComes);
		this.getManagedBean().setProcessName(processName);
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
	
		List<ScQtCommitteRemarks> remarksList=	getManagedBean().getCommitteeRemarkList();
		List<ScQtCommitteRemarks> committeeRemarks = null;
		if (isNotNullOREmpty(remarksList)) {
			committeeRemarks = new ArrayList<ScQtCommitteRemarks>();
			for (ScQtCommitteRemarks remarks : remarksList) {
				if (remarks.getRemark() != null && "" != remarks.getRemark()) {
					committeeRemarks.add(remarks);
				}
			}
		}
		scQtCommittee.setQtCommitteRemarks(committeeRemarks);
		
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
		List<ScQtRecommend> scQtRecommendList = new ArrayList<ScQtRecommend>();
		scQtRecommendList.addAll(scQtFinalization.getScQtRecommends());
		
		String recommendBy = getManagedBean().getRecommendBy();
		
		ScQtEvaluationResult scQtEvaluationResult = getManagedBean().getRecommendedSupplier();
		if(scQtEvaluationResult != null){
			//award
			SmSupplier smSupplier = scQtEvaluationResult.getScQtProposal().getScQtSupplier().getSmSupplier();
			for(ScQtRecommend scQtRecommend : scQtRecommendList){
				Set<ScQtRecommendDtl> listToAdd = new HashSet<ScQtRecommendDtl>();
				List<ScQtRecommendDtl> dbList = scQtRecommend.getRecommendDtls();
				Set<SmSupplier> supplierList = new HashSet<SmSupplier>();
				
				if(dbList != null && dbList.size() > 0){
					for(ScQtRecommendDtl db : dbList){
						supplierList.add(db.getSupplier());
					}
				}
				
				if(!supplierList.contains(smSupplier)){
					ScQtRecommendDtl scQtRecommendDtl = prepareRecommendDtlInfo(scQtRecommend, scQtEvaluationResult);
					listToAdd.add(scQtRecommendDtl);
				}else{
					for(ScQtRecommendDtl db : dbList){
						if(db.getSupplier().equals(smSupplier)){
							listToAdd.add(db);
						}
					}
				}
				scQtRecommend.setRecommendByStr(recommendBy);
				scQtRecommend.setRecommendDtls(new ArrayList<ScQtRecommendDtl>(listToAdd));
			}
		}else{
			//multiple LOI or Online Bidding
			
			//to set the recommend details for each recommend
			for(ScQtRecommend scQtRecommend : scQtRecommendList){
				List<ScQtRecommendDtl> dbList = scQtRecommend.getRecommendDtls();
				Set<ScQtRecommendDtl> listToAdd = new HashSet<ScQtRecommendDtl>();
				Set<ScQtRecommendDtl> listToRemove = new HashSet<ScQtRecommendDtl>();
				Set<SmSupplier> supplierList = new HashSet<SmSupplier>();
				
				if(dbList != null && dbList.size() > 0){
					for(ScQtRecommendDtl db : dbList){
						supplierList.add(db.getSupplier());
					}
				}
				
				List<ScQtEvaluationResult> recommendedSupplierList = getManagedBean().getRecommendedSupplierList();
				if(recommendedSupplierList != null && recommendedSupplierList.size() > 0){
					for(ScQtEvaluationResult evalResult : recommendedSupplierList){
						SmSupplier smSupplier = evalResult.getScQtProposal().getScQtSupplier().getSmSupplier();
						if(!supplierList.contains(smSupplier)){
							ScQtRecommendDtl scQtRecommendDtl = prepareRecommendDtlInfo(scQtRecommend, evalResult);
							listToAdd.add(scQtRecommendDtl);
						}else{
							for(ScQtRecommendDtl db : dbList){
								if(db.getSupplier().equals(smSupplier)){
									listToAdd.add(db);
								}
							}
						}
					}
				}
				
				List<ScQtEvaluationResult> unRecommendedSupplierList = getManagedBean().getUnRecommendedSupplierList();
				if(unRecommendedSupplierList != null && unRecommendedSupplierList.size() > 0){
					for(ScQtEvaluationResult evalResult : unRecommendedSupplierList){
						SmSupplier smSupplier = evalResult.getScQtProposal().getScQtSupplier().getSmSupplier();
						for(ScQtRecommendDtl db : dbList){
							if(db.getSupplier().equals(smSupplier)){
								db.setDeleted(true);
								listToRemove.add(db);
							}
						}
					}
				}
				
				scQtRecommend.setRecommendByStr(recommendBy);
				scQtRecommend.setRecommendDtls(new ArrayList<ScQtRecommendDtl>(listToAdd));
				scQtRecommend.setRemoveSupplierList(new ArrayList<ScQtRecommendDtl>(listToRemove));
			}
		}
		scQtFinalization.setScQtRecommends(scQtRecommendList);
	}
	
	/* private void prepareRecommendInfo(ScQtFinalization scQtFinalization, ScQtRecommend scQtRecommend, ScQtEvaluationResultDtl scQtEvaluationResultDtl){
		PmParameter recommendBy = getPmParameterById(getManagedBean().getRecommendBy());
		scQtRecommend.setRecommendBy(recommendBy);
		scQtRecommend.setTplSpecZoneDtl(getManagedBean().getTplSpecZoneDtl());
		scQtRecommend.setRequestItem(scQtEvaluationResultDtl.getScRequestItem());
		scQtRecommend.setScQtFinalization(scQtFinalization);
		setUserDetails(scQtRecommend);
		
	}*/
	
	private ScQtRecommendDtl prepareRecommendDtlInfo(ScQtRecommend scQtRecommend, ScQtEvaluationResult scQtEvaluationResult){
		ScQtRecommendDtl scQtRecommendDtl = new ScQtRecommendDtl();
		
		SmSupplier smSupplier = scQtEvaluationResult.getScQtProposal().getScQtSupplier().getSmSupplier();
		ScRequestItem scRequestItem = scQtRecommend.getRequestItem();
		
		scQtRecommendDtl.setSupplier(smSupplier);
		scQtRecommendDtl.setAwardQty(scRequestItem.getQuantity());
		scQtRecommendDtl.setScQtRecommend(scQtRecommend);
		setUserDetails(scQtRecommendDtl);
		
		return scQtRecommendDtl;
	}
	
	private void saveByItem(ScQtFinalization scQtFinalization){
		List<ScQtRecommend> scQtRecommendList = new ArrayList<ScQtRecommend>();
		scQtRecommendList.addAll(scQtFinalization.getScQtRecommends());
		
		//to set the recommend details for each recommend
		for(ScQtRecommend scQtRecommend : scQtRecommendList){
			List<ScQtRecommendDtl> dbList = scQtRecommend.getRecommendDtls();
			Set<ScQtRecommendDtl> listToAdd = new HashSet<ScQtRecommendDtl>();
			Set<ScQtRecommendDtl> listToRemove = new HashSet<ScQtRecommendDtl>();
			Set<SmSupplier> supplierList = new HashSet<SmSupplier>();
			Set<SmSupplier> supplierList2 = new HashSet<SmSupplier>();
			 
			if(dbList != null && dbList.size() > 0){
				for(ScQtRecommendDtl db : dbList){
					supplierList.add(db.getSupplier());
				}
			}

			for(ScQtEvaluationResult evalResult : scQtRecommend.getAwardSupplierList()){
				SmSupplier smSupplier = evalResult.getScQtProposal().getScQtSupplier().getSmSupplier();
				
				if(!supplierList.contains(smSupplier)){
					ScQtRecommendDtl scQtRecommendDtl = prepareRecommendDtlInfo(scQtRecommend, evalResult);
					if(scQtRecommend.getRecommendByStr().equals(ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD)){
						scQtRecommendDtl.setAwardQty(evalResult.getQtyAward());
					}else{
						scQtRecommendDtl.setAwardQty(scQtRecommend.getRequestItem().getQuantity());
					}
					listToAdd.add(scQtRecommendDtl);
				}else{
					for(ScQtRecommendDtl db : dbList){
						if(db.getSupplier().equals(smSupplier)){
							if(scQtRecommend.getRecommendByStr().equals(ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD)){
								db.setAwardQty(evalResult.getQtyAward());
							}else{
								db.setAwardQty(scQtRecommend.getRequestItem().getQuantity());
							}
							listToAdd.add(db);
						}
					}
				}
			}
			
			if(listToAdd != null && listToAdd.size() > 0){
				for(ScQtRecommendDtl newDtl : listToAdd){
					supplierList2.add(newDtl.getSupplier());
				}
			}
			for(ScQtRecommendDtl db : dbList){
				SmSupplier smSupplier = db.getSupplier();
				if(!supplierList2.contains(smSupplier)){
					db.setDeleted(true);
					listToRemove.add(db);
				}
			}
			scQtRecommend.setRecommendDtls(new ArrayList<ScQtRecommendDtl>(listToAdd));
			scQtRecommend.setRemoveSupplierList(new ArrayList<ScQtRecommendDtl>(listToRemove));
		}
		scQtFinalization.setScQtRecommends(scQtRecommendList);
	}
	
	public void submitSuppFinalization(String outcome){
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		int activeIndex = this.getManagedBean().getActiveIndex();
		String taskId = this.getManagedBean().getTaskId();
		String token = new FacesScopes().getBPMAuthenticationCtxToken();
		
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			
			SuppFinalizationReqDTO reqDTO = validateInfo(activeIndex, true);
			reqDTO.setOutcome(outcome);
			reqDTO.setToken(token);
			reqDTO.setTaskId(taskId);
			
			ScQtCommittee scQtCommittee = preparationDtlsInfo();
			reqDTO.setScQtCommittee(scQtCommittee);
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.submitSupplierFinalization(reqDTO);
			
			if (resDTO.isSuccessful()) {				
				String variable1 = getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION) + " " + getManagedBean().getScQt().getQtNo();
				String variable2 = CommonMethod.getMessageCommon("lbl.sc.supplier.finalization");
				
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
		}
		downloadFile(reportAttachment);	
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
			
			if (SourcingConstants.DOC_DEF_KEY_COVER.equals(docDefinitionKey)) {
				generateCoverPdf(attachment, getManagedBean().getScQt());
			
			} else if (SourcingConstants.DOC_DEF_KEY_OFFERED_QT_DTLS.equals(docDefinitionKey)) {
				generateOfferedAndQTDtlsPdf(attachment, getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_ANALYSIS_EVALUATION_REPORT.equals(docDefinitionKey)) {
				//FIXME - get from evaluation report

			} else if (SourcingConstants.DOC_DEF_KEY_NOTICE_BOARD.equals(docDefinitionKey)) {
				generateNoticeBoardPdf(attachment, getManagedBean().getScQt());

			} else if (SourcingConstants.DOC_DEF_KEY_PRICE_SCHEDULE.equals(docDefinitionKey)) {
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
	
	public void downloadAllReports() throws Exception {
		debugLogger("downloadAllReports");

		List<ScQtFinalizationReport> scQtFinalizationReports = getManagedBean().getScQtFinalization().getScQtFinalizationReports();
		List<ScQtFinalizationReportAttachment> scAttachments = new ArrayList<ScQtFinalizationReportAttachment>();
		
		for (ScQtFinalizationReport report : scQtFinalizationReports) {
			ScQtFinalizationReportAttachment attachment = report.getScQtFinalizationReportAttachment();
			if (attachment == null) {
				attachment = generateDocumentAttachment(report);
			}
			scAttachments.add(attachment);
		}
		
		downloadAllFiles("NGeP-Finalization-Report", scAttachments);
	}
	
	public void generateCoverPdf(ScAttachment attachment, ScQt scQt) throws Exception {
		debugLogger("generateCoverPdf");
		populateAttachmentDetails(attachment);
		attachment.setFileName("finalization-Cover.pdf");
		
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
		
     	String velocityTemplate = "/sc/supplierfinalization/finalization-cover.vm";
    	DocumentGenerator.getInstance().generatePdfFromVelocity(velocityTemplate, context, new FileOutputStream(getActualFilePath(attachment)));
	}
	
	public void generateOfferedAndQTDtlsPdf(ScAttachment attachment, ScQt scQt) throws Exception {
		debugLogger("generateOfferedAndQTDtlsPdf");
		populateAttachmentDetails(attachment);
		attachment.setFileName("finalization-offeredAndQTDetails.pdf");
		
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
		context.put("budgetList", scQt.getScQtGosodoBudgets());
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
						System.out.println("list: " + list.size());
						for(Object[] object : list){
							TplSpecZoneDtl tplSpecZoneDtl = (TplSpecZoneDtl) object[0];
							Long totalAttended = (Long) object[1];
							ScQtBsvDtl scQtBsvDtl = (ScQtBsvDtl) object[2];
							
							System.out.println(" tplSpecZoneDtl.getZoneName() : "+ tplSpecZoneDtl.getZoneName());
							System.out.println(" totalAttended : "+ totalAttended);
							System.out.println(" scQtBsvDtl.getBsvDesc() : "+ scQtBsvDtl.getBsvDesc());
						}
					}
				}
			}
		} finally {
			qTBriefingSO = null;
		}
		context.put("isZone", isZone);
		context.put("list", list);
		
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
		
		IContractSO contractSO;
		try {
			contractSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			ContractReqDTO contractReqDTO = new ContractReqDTO();
			contractReqDTO.setContractNo(scQt.getExistingContractNo());
			ContractResDTO contractResDTO = contractSO.retriveContractInfoByContractNo(contractReqDTO);
			if (contractResDTO.getContract() != null) {
				CtContract contract = contractResDTO.getContract();
				context.put("contract", contract);
				context.put("sp", " ");
			}
		} finally {
			contractSO = null;
		}
		
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
		
		String velocityTemplate = "/sc/supplierfinalization/finalization-offeredAndQTDetails.vm";
		DocumentGenerator.getInstance().generatePdfFromVelocity(velocityTemplate, context, new FileOutputStream(getActualFilePath(attachment)));
	}
	
	public void generateNoticeBoardPdf(ScAttachment attachment, ScQt scQt) throws Exception {
		debugLogger("generateNoticeBoardPdf");
		populateAttachmentDetails(attachment);
		attachment.setFileName("finalization-noticeBoard.pdf");
		
		//ScQt scQt = scQtFinalization.getScQt();
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		//DecimalFormat amountDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		VelocityContext context = new VelocityContext();
		
		//TODO
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
    	context.put("paidupcapital", scQt.getScQtSupplierCriteria().getMinPaidUpCapital());
    	
    	context.put("categoryList", scQt.getScQtCategorys());
    	context.put("bean", this);
    	
    	boolean isZone = false;
    	List<TreeNode> locations = new ArrayList<TreeNode>();
    	List<ScQtContactPerson> contactPersons = new ArrayList<ScQtContactPerson>();
    	List<ScQtContactPerson> contactPersonsList = new ArrayList<ScQtContactPerson>();
    	BriefingZoneBean briefingZoneBean = null;
    	List<BriefingZoneBean> bsvZoneBeans = new ArrayList<BriefingZoneBean>();
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
    	context.put("bsvZoneBeansList", bsvZoneBeans);
    	
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
    	
		String velocityTemplate = "/sc/supplierfinalization/finalization-noticeBoard.vm";
		DocumentGenerator.getInstance().generatePdfFromVelocity(velocityTemplate, context, new FileOutputStream(getActualFilePath(attachment)));
	}
	
	public void generatePriceSchedulePdf(ScAttachment attachment, ScQt scQt) throws Exception {
		debugLogger("generateNoticeBoardPdf");
		populateAttachmentDetails(attachment);
		attachment.setFileName("finalization-priceSchedule.pdf");
		
		//ScQt scQt = scQtFinalization.getScQt();
		//CommonUtils commonUtils = new CommonUtils();
		//SimpleDateFormat dateSDF = new SimpleDateFormat(commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		//DecimalFormat amountDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		VelocityContext context = new VelocityContext();
		
		//TODO
		
		String velocityTemplate = "/sc/supplierfinalization/finalization-priceSchedule.vm";
		DocumentGenerator.getInstance().generatePdfFromVelocity(velocityTemplate, context, new FileOutputStream(getActualFilePath(attachment)));
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
			
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setScQt(scQt);
			
			List<TplSpecZoneDtl> zoneDetails = getManagedBean().getZoneDetails();
			reqDTO.setZoneDetails(zoneDetails);
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.retrieveSupplierFinalizationDtls(reqDTO);
			getManagedBean().setShortlistedPackageList(resDTO.getQualifiedByPackageSuppliers());
			getManagedBean().setShortlistedItemList(resDTO.getQualifiedByItemSuppliers());
			
			if(resDTO.getScQtFinalization() != null){
				ScQtFinalization scQtFinalization = resDTO.getScQtFinalization();
				if(scQtFinalization.getCreatedBy() == null){
					setUserDetails(scQtFinalization);
				}
				
				String decisionTypeCode = scQtFinalization.getDecision().getParameterCode();
				if(decisionTypeCode.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)){
					
					String recommendType = scQtFinalization.getRecommendedTypeId().getParameterCode();
					if(recommendType.equals(ParameterConst.RECOMMEND_TYPE_PACKAGE)){
						if(getManagedBean().getShortlistedSupplierList() == null){
							getManagedBean().setShortlistedSupplierList(new ArrayList<ScQtEvaluationResult>());
							if(getManagedBean().getShortlistedPackageList() != null){
								getManagedBean().getShortlistedSupplierList().addAll(getManagedBean().getShortlistedPackageList());
							}
						}
						
						if(scQtFinalization.getScQtRecommends().size() > 0){
							ScQtRecommend scQtRecommend = scQtFinalization.getScQtRecommends().get(0);
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
					}else{
						if(getManagedBean().getShortlistedSupplierList() == null){
							getManagedBean().setShortlistedSupplierList(new ArrayList<ScQtEvaluationResult>());
							if(getManagedBean().getShortlistedItemList() != null){
								getManagedBean().getShortlistedSupplierList().addAll(getManagedBean().getShortlistedItemList());
							}
						}
						
						if(scQtFinalization.getScQtRecommends().size() > 0){
							for(ScQtRecommend scQtRecommend :scQtFinalization.getScQtRecommends()){
								PmParameter recommendBy = scQtRecommend.getRecommendBy();
								scQtRecommend.setRecommendByStr(recommendBy.getParameterCode());
								
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
				}else if(decisionTypeCode.equals(ParameterConst.DECISION_RE_EVALUATION)){
					//nothing to set
				}else if(decisionTypeCode.equals(ParameterConst.DECISION_RE_PUBLISH)){
					//nothing to set
				}else if(decisionTypeCode.equals(ParameterConst.DECISION_CANCEL)){
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
	public void showSupplierDetails() {
		debugLogger("showSuppDetails");
		if (getManagedBean().getShortlistedSupplier() != null) {
			this.getManagedBean().setShortlistedSupplier(getManagedBean().getShortlistedSupplier());
			
			SmSupplier smSupplier = getManagedBean().getShortlistedSupplier().getScQtProposal().getScQtSupplier().getSmSupplier();
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
			String orgType = getFacesScopes().getUserSessionBean().getOrgTypeCode().getParameterCode();
			if(orgType.equals(ParameterConst.ORG_TYPE_PMS_SABAH) || orgType.equals(ParameterConst.ORG_TYPE_PMS_SARAWAK)){//FIXME - to filter by PTJ profile
				finalizationCommiteeList.add(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH);
				finalizationCommiteeList.add(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK);
			}else{
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
		
		SelectItem[] items = getDropdownValuesBySearchBean(ParameterTypeConst.SC_COMMITTEE_TYPE, searchBean);
		return items;
    }
	
	private void retrieveCommitteeSecretary(String commType){
		ICommitteSO committeSO = null;
		try{
			committeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			
			Long qtId = getManagedBean().getScQt().getQtId();
			
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setQtId(qtId);
			
			reqDTO.setCommitteeType(commType);
			reqDTO.setFinalizationSecratary(true);
				
			CommitteResDTO resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
			ScQtCommittee scQtCommittee = resDTO.getScQtCommittee();
			
			ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
			
			List<ScCommitteeMember> scCommitteeMembers = scQtCommittee.getScCommittee().getScCommitteeMembers();
			for(ScCommitteeMember scCommitteeMember : scCommitteeMembers){
				String secretaryRole = scCommitteeMember.getMemberRole().getParameterCode();
				if(secretaryRole.equals(ParameterConst.COMMITTEE_ROLE_SECRETARY)){
					scQtFinalization.setHigherSecretary(scCommitteeMember.getPmUser());
					break;
				}
			}
			getManagedBean().setScQtFinalization(scQtFinalization);
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
	
	public SelectItem[] getItemRecommendByDropDown(PmParameter priceType) {
		SelectItem[] items = null;
				
		String priceTypeCode = priceType.getParameterCode();
		if(priceTypeCode.equals(ParameterConst.PRICE_TYPE_TARIFF) || priceTypeCode.equals(ParameterConst.PRICE_TYPE_TIER)){
			List<String> recommendList = new ArrayList<String>();
			recommendList.add(ParameterConst.RECOMMEND_BY_SINGLE_AWARD);
			recommendList.add(ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD);
			recommendList.add(ParameterConst.RECOMMEND_BY_ONLINE_BIDDING);
			
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
			
			items = getDropdownCodeValuesBySearchBean(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, searchBean);
			
		}else{
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_EQUALS.value(), ParameterConst.RECOMMEND_BY_AWARD));
			
			items = getDropdownCodeValuesBySearchBean(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, searchBean);
		}
		return items;
	}
	
	public SelectItem[] getAwardSupplierListDropDown(){
    	if(getManagedBean().getAwardSupplierList() == null){
    		List<SmSupplier> supplierList = new ArrayList<SmSupplier>();
        	List<ScQtEvaluationResult> shortlistedSupplierList = getManagedBean().getShortlistedSupplierList();
        	if(shortlistedSupplierList != null){
        		for(ScQtEvaluationResult shorlistedSupplierList : shortlistedSupplierList){
        			supplierList.add(shorlistedSupplierList.getScQtProposal().getScQtSupplier().getSmSupplier());
        		}
        	}
        	getManagedBean().setAwardSupplierList(supplierList);
    	}
    	
		SelectItem[] items = new SelectItem[getManagedBean().getAwardSupplierList().size()];
		int i = 0;
		for(SmSupplier supplier : getManagedBean().getAwardSupplierList()) {
			items[i++] = new SelectItem(supplier.getSupplierId().toString(),supplier.getCompanyName());
		}
		return items;
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
		
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		String decision = scQtFinalization.getDecision().getParameterCode();
		if(decision == null){
			resetFlag();
			
		}else if(decision.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)){
			this.getManagedBean().setRecommendSupplier(true);
			
			//PmParameter pmParameter = getPmParameterByTypeAndCode(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, ParameterConst.RECOMMEND_BY_SINGLE_AWARD);
			//this.getManagedBean().setRecommendBy(pmParameter);

			String recommendType = scQtFinalization.getRecommendedTypeId().getParameterCode();
			if(recommendType.equals(ParameterConst.RECOMMEND_TYPE_PACKAGE)){
				getManagedBean().setShortlistedSupplierList(getManagedBean().getShortlistedPackageList());
				
				if(scQtFinalization.getScQtRecommends().size() > 0){
					ScQtRecommend scQtRecommend = scQtFinalization.getScQtRecommends().get(0);
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
			}else{
				getManagedBean().setShortlistedSupplierList(getManagedBean().getShortlistedItemList());
				
				if(scQtFinalization.getScQtRecommends().size() > 0){
					for(ScQtRecommend scQtRecommend :scQtFinalization.getScQtRecommends()){
						PmParameter recommendBy = scQtRecommend.getRecommendBy();
						if(recommendBy == null){
							recommendBy = getPmParameterByTypeAndCode(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, scQtRecommend.getRecommendByStr());
						}else{
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
			
		}else if(decision.equals(ParameterConst.DECISION_RE_EVALUATION)){
			this.getManagedBean().setReEvaluate(true);
			
		}else if(decision.equals(ParameterConst.DECISION_RE_PUBLISH)){
			this.getManagedBean().setRePublish(true);
			
		}else if(decision.equals(ParameterConst.DECISION_CANCEL)){
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

	/**
	 * Event for Recommendation Type By Item or By Package in Supplier Finalization
	 */
	public void recommendTypeEvent() {
		debugLogger("recommendTypeEvent");
		
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		String recommendType = scQtFinalization.getRecommendedTypeId().getParameterCode();
		boolean	itemize = false;
		String selectionMode = QTConstants.SF_SINGLE;
		
		if (recommendType.equals(ParameterConst.RECOMMEND_TYPE_ITEM)) {
			itemize = true;
			selectionMode = QTConstants.SF_MULTIPLE;
			
			List<ScQtRecommend> scQtRecommendList = scQtFinalization.getScQtRecommends();
			if(scQtRecommendList != null && scQtRecommendList.size() > 0){
				for(ScQtRecommend scQtRecommend : scQtRecommendList){
					scQtRecommend.setRecommendBy(null);
				}
			}
		}else{
			if(getManagedBean().getRecommendBy() == null){
				getManagedBean().setRecommendBy("");
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
		
		if(getManagedBean().getRecommendBy() != null){
			String recommendByCode = getManagedBean().getRecommendBy();
			if(recommendByCode == null){
				getManagedBean().setRecommendBy(ParameterConst.RECOMMEND_BY_SINGLE_AWARD);
			}
			
			this.getManagedBean().setSingleAward(false);
			this.getManagedBean().setOnlineBidding(false);
			this.getManagedBean().setMultipleLoi(false);
			
			if(recommendByCode.equals(ParameterConst.RECOMMEND_BY_AWARD)){
				this.getManagedBean().setSelectionMode(QTConstants.SF_SINGLE);
				this.getManagedBean().setSingleAward(true);
				//this.getManagedBean().setBiddingEndorsement(null);
				
				scQtFinalization.setOnlineBiddingEndorse(null);
					
			}else if(recommendByCode.equals(ParameterConst.RECOMMEND_BY_ONLINE_BIDDING)){
				this.getManagedBean().setSelectionMode(QTConstants.SF_MULTIPLE);
				this.getManagedBean().setOnlineBidding(true);
				
			}else if(recommendByCode.equals(ParameterConst.RECOMMEND_BY_MULTIPLE_LOI)){
				this.getManagedBean().setSelectionMode(QTConstants.SF_MULTIPLE);
				this.getManagedBean().setMultipleLoi(true);
				//this.getManagedBean().setBiddingEndorsement(null);
				
				scQtFinalization.setOnlineBiddingEndorse(null);
			}
		}
	}
	
	public void itemRecommendByEvent(ScQtRecommend scQtRecommend) {
		debugLogger("itemRecommendByEvent");
		
		String recommendBy = scQtRecommend.getRecommendByStr();
		if(recommendBy.equals(ParameterConst.RECOMMEND_BY_AWARD) || recommendBy.equals(ParameterConst.RECOMMEND_BY_SINGLE_AWARD)){
			//clear the list if more than 1
			if(scQtRecommend.getAwardSupplierList() != null){
				int size = scQtRecommend.getAwardSupplierList().size();
				if(size > 1){
					scQtRecommend.setAwardSupplierList(new ArrayList<ScQtEvaluationResult>());
				}
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
		if (fwdHigherAuthority){
			String committeeType = getManagedBean().getCommitteeType();
			if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A) 
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)
				|| committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)){
				
				getManagedBean().setForwardToBpk(true);
			}else{
				getFinalizationCommitteeDropDown();
			}
		}else{
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
		String finalizationCommittee = scQtFinalization.getHigherCommitte().getParameterCode();
		retrieveCommitteeSecretary(finalizationCommittee);
	}
	
	public void selectItemEvent(SelectEvent event) {
		debugLogger("selectItemEvent");
		
		ScQtRecommend scQtRecommend = (ScQtRecommend) event.getObject();
		getManagedBean().setRecommendedSupplierList(new ArrayList<ScQtEvaluationResult>());
		List<ScQtEvaluationResult> recommendedSupplierList = getManagedBean().getRecommendedSupplierList();
		if(scQtRecommend.getAwardSupplierList() != null){
			recommendedSupplierList.addAll(scQtRecommend.getAwardSupplierList());
		}
	}
	
	public void unselectItemEvent(UnselectEvent event) {
		debugLogger("unselectItemEvent");
		
		ScQtRecommend scQtRecommend = (ScQtRecommend) event.getObject();
		if(getManagedBean().getSelectedRecommendList() == null){
			getManagedBean().setSelectedRecommendList(new ArrayList<ScQtRecommend>());
		}else{
			List<ScQtRecommend> selectedRecommendList = getManagedBean().getSelectedRecommendList();
			for(ScQtRecommend item : selectedRecommendList){
				if(scQtRecommend.equals(item)){
					selectedRecommendList.remove(item);
				}
			}
		}
	}
	
	public void selectSupplierEvent() {
		debugLogger("selectSupplierEvent");
		
		List<ScQtRecommend> selectedItemList = getManagedBean().getSelectedRecommendList();
		if(selectedItemList != null){
			List<ScQtEvaluationResult> recomendedList = getManagedBean().getRecommendedSupplierList();
			for(ScQtRecommend scQtRecommend : selectedItemList){
				scQtRecommend.setAwardSupplierList(new ArrayList<ScQtEvaluationResult>());
				
				for(ScQtEvaluationResult recomendedSupplier : recomendedList){
					if(selectedItemList != null){
						scQtRecommend.getAwardSupplierList().add(recomendedSupplier);
					}
				}
				
				ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
				String recommendType = scQtFinalization.getRecommendedTypeId().getParameterCode();
				
				if(recommendType.equalsIgnoreCase(ParameterConst.RECOMMEND_TYPE_ITEM) && scQtRecommend.getAwardSupplierList() != null){
					PmParameter pmParameter = scQtRecommend.getRecommendBy();
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
		}
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
		}else{
			selectSupplierEvent();
		}
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
		ScQtEvaluationResult awardSupplier = null;
		
		//get the SmSupplier object by the dropdown selection
		for(ScQtEvaluationResult evalResult : getManagedBean().getShortlistedSupplierList()){
			String suppId = evalResult.getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierId().toString();
			if( suppId.equalsIgnoreCase(awardSupplierStr)){
				awardSupplier = evalResult;
				break;
			}
		}
		
		//add the SmSupplier to the item selected
		List<ScQtRecommend> selectedItemList = getManagedBean().getSelectedRecommendList();
		if(selectedItemList != null){
			for(ScQtRecommend scQtRecommend : selectedItemList){
				if(scQtRecommend.getAwardSupplierList() == null){
					scQtRecommend.setAwardSupplierList(new ArrayList<ScQtEvaluationResult>());
				}
				if(!scQtRecommend.getAwardSupplierList().contains(awardSupplier)){
					scQtRecommend.getAwardSupplierList().add(awardSupplier);
					
					List<ScQtEvaluationResult> shortlistedSupplierList = getManagedBean().getShortlistedSupplierList();
					for(ScQtEvaluationResult supplier : shortlistedSupplierList){
						if(supplier.equals(awardSupplier) ){
							if(getManagedBean().getRecommendedSupplierList() == null){
								getManagedBean().setRecommendedSupplierList(new ArrayList<ScQtEvaluationResult>());
							}
							getManagedBean().getRecommendedSupplierList().add(supplier);
						}
					}
					
					String parameterType = ParameterTypeConst.FINALIZATION_RECOMMEND_BY;
					PmParameter pmParameter = null;
					ScRequestItem scRequestItem = scQtRecommend.getRequestItem();
					String priceType = scRequestItem.getTplSpecQuestion().getClPriceTypeCode().getParameterCode();
					
					if(priceType.equals(ParameterConst.PRICE_TYPE_TARIFF) || priceType.equals(ParameterConst.PRICE_TYPE_TIER)){
						if(scQtRecommend.getAwardSupplierList().size()==1){
							pmParameter = getPmParameterByTypeAndCode(parameterType, ParameterConst.RECOMMEND_BY_AWARD);
							scQtRecommend.setRecommendBy(pmParameter);
							//scQtRecommend.setRecommendStr(pmParameter.getParameterCode());
						
						}else if(scQtRecommend.getAwardSupplierList().size()>1){
							//If its price type tuer/tariff, should clear the recommend by, 
							//user need to select multiple LOI or online bidding
							scQtRecommend.setRecommendBy(null);
							//scQtRecommend.setRecommendStr(null);
						}
					}else{
						if(scQtRecommend.getAwardSupplierList().size()==1){
							pmParameter = getPmParameterByTypeAndCode(parameterType, ParameterConst.RECOMMEND_BY_SINGLE_AWARD);
							scQtRecommend.setRecommendBy(pmParameter);
							//scQtRecommend.setRecommendStr(pmParameter.getParameterCode());
						
						}else if(scQtRecommend.getAwardSupplierList().size()>1){
							pmParameter = getPmParameterByTypeAndCode(parameterType, ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD);
							scQtRecommend.setRecommendBy(pmParameter);
							//scQtRecommend.setRecommendStr(pmParameter.getParameterCode());
						}
					}
				}
			}
		}
	}
	
	public void selectSupplierDropDownEvent() {
		debugLogger("selectSupplierDropDownEvent");
		
		String awardSupplier = getManagedBean().getAwardSupplier();
		getManagedBean().setAwardSupplier(awardSupplier);
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
	
}
