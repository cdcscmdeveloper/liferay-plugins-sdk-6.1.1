package my.ep.web.backingbean.sc.supplierfinalization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.tpl.IContractTemplateSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.ct.tpl.ContractTemplateReqDTO;
import my.ep.dto.ct.tpl.ContractTemplateResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtReport;
import my.ep.persistence.entity.sc.quationtender.ScQtRptBriefingPaperAttachment;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.supplierfinalization.SupplierFinalizationManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pp.common.CommonMethod;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.UploadedFile;

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
	
	
	//temporary
	Long qtId = 146L;
	String committeeType = ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A;
	
	public SupplierFinalizationBackingBean() {
		if(getManagedBean().getScQt() == null){
			retriveQTDetails();
		}
		
		if(this.getManagedBean().getMeetingList() == null){
			retriveMeetingDtls();
		}
	}
	
	public void retriveQTDetails() {
		IQTDetailSO qtDetailSO;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(qtId);
			
			QuationTenderResDTO resDTO = qtDetailSO.retriveQtById(reqDTO);
			ScQt scQt = resDTO.getQtDetail();
			populateParentOrgProfile(scQt);
			getManagedBean().setScQt(scQt);

		} finally {
			qtDetailSO = null;
		}
	}

	
	//Supplier Finalization Tab Menu
	public void onTabMenuChangeAction(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();
		
		if (tabTitle.equalsIgnoreCase(CommonMethod.getMessageCommon("lbl.sc.preparation"))) {
			this.getManagedBean().setActiveIndex(ACTIVE_INDEX_PREPARATION);
			this.getManagedBean().setPrepareActiveIndex(PREPARE_ACTIVE_INDEX_MEETING);
			retriveMeetingDtls();

		} else if (tabTitle.equalsIgnoreCase(CommonMethod.getMessageCommon("lbl.sc.briefing.paper"))) {
			this.getManagedBean().setActiveIndex(ACTIVE_INDEX_REPORT);
			retrieveBriefingPaper();
		
		} else if (tabTitle.equalsIgnoreCase(CommonMethod.getMessageCommon("lbl.sc.supplier.finalization"))) {
			this.getManagedBean().setActiveIndex(ACTIVE_INDEX_SUPP_FINALIZATION);
			
			decisionEvent();
			recommendTypeEvent();
			fwdHigherAuthorityEvent();
			packageRecommendByEvent();
			retrieveSuppFinalizationDtls();
	
		} else if (tabTitle.equalsIgnoreCase(CommonMethod.getMessageCommon("lbl.sc.committee.endorsement"))) {
			this.getManagedBean().setActiveIndex(ACTIVE_INDEX_ENDORSEMENT);
			retrieveCommitteeEndorsement();
		}
	}
	
	public void retriveMeetingDtls() {
		debugLogger("..In retriveMeetingDtls()..");
		IQTMeetingSO meetingSO = null;
		try {
			this.getManagedBean().setPrepareActiveIndex(PREPARE_ACTIVE_INDEX_MEETING);
			meetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			//FIXME = fara
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteType(committeeType);
			
			MeetingResDTO resDTO = meetingSO.retrieveMeetingDls(reqDTO);
			if (resDTO.isSuccessful()) {
				this.getManagedBean().setMeetingList(resDTO.getMeetingDtls());
				
				ScQtCommittee committee = new ScQtCommittee();
				committee = resDTO.getQtCommittee();
				this.getManagedBean().setScQtCommittee(committee);
			}
		} finally {
			meetingSO = null;
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
	
	public void saveMeetingDtls(){
		debugLogger("..In saveMeetingDtls()..");
		IQTMeetingSO meetingso = null;
		try {
			meetingso = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);

			MeetingReqDTO metReqDtO = new MeetingReqDTO();
			ScQtCommittee qtCommittee = this.getManagedBean().getScQtCommittee();
			setUserDetails(qtCommittee);
			
			if (this.getManagedBean().getMeetingList() != null && !this.getManagedBean().getMeetingList().isEmpty()) {
				if(qtCommittee.getScQtMeetings() == null){
					qtCommittee.setScQtMeetings(new ArrayList<ScQtMeeting>());
				}
				for(ScQtMeeting scQtMeeting : this.getManagedBean().getMeetingList()){
					System.out.println("LOC : "+ scQtMeeting.getMeetingLocation());
					System.out.println("STATUS : "+ scQtMeeting.getMeetingStatus().getStatusId());
				}
				qtCommittee.setScQtMeetings(this.getManagedBean().getMeetingList());
			}
			metReqDtO.setScQtCommitte(qtCommittee);
			meetingso.saveQTDtl(metReqDtO);
			retriveMeetingDtls();
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Meeting Details Saved Successfully", ""));
		} finally {
			meetingso = null;
		}
	}
	
	public void retriveCommitteeDtls() {
		debugLogger("..In retriveCommitteeDtls()..");
		IQTMeetingSO meetingSO;
		try {
			this.getManagedBean().setPrepareActiveIndex(PREPARE_ACTIVE_INDEX_COMMITTEE);
			meetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);

			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteType(committeeType);
			reqDTO.setIsAttended(null);
			
			MeetingResDTO resDTO = meetingSO.retrieveSignOffCommitteeListByCommType(reqDTO);
			if (resDTO.getCommitteMembers() != null) {
				this.getManagedBean().setCommitteeList(resDTO.getCommitteMembers());
			}
		} finally {
			meetingSO = null;
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
	
	public void saveCommitteeDtls(){
		debugLogger("..In saveCommitteeDtls()..");
		IQTMeetingSO meetingso = null;
		try {
			meetingso = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);

			MeetingReqDTO metReqDtO = new MeetingReqDTO();
			ScQtCommittee qtCommittee = this.getManagedBean().getScQtCommittee();
			
			if (this.getManagedBean().getCommitteeList() != null&& !this.getManagedBean().getCommitteeList().isEmpty()) {
				qtCommittee.setCommitteeActivities(this.getManagedBean().getCommitteeList());
			}
		
			List<ScQtCommitteRemarks> remarksList=	this.getManagedBean().getCommitteeRemarkList();
			List<ScQtCommitteRemarks> committeeRemarks = null;
			if (isNotNullOREmpty(remarksList)) {
				committeeRemarks = new ArrayList<ScQtCommitteRemarks>();
				for (ScQtCommitteRemarks remarks : remarksList) {
					if (remarks.getRemark() != null && "" != remarks.getRemark()) {
						committeeRemarks.add(remarks);
					}
				}
			}
			qtCommittee.setQtCommitteRemarks(committeeRemarks);
			setUserDetails(qtCommittee);
			
			metReqDtO.setScQtCommitte(qtCommittee);
			meetingso.saveQTDtl(metReqDtO);
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Committee Details Saved Successfully", ""));
		} finally {

		}
	}   
	
	public void retriveMeetingAttendanceDtls() {
		debugLogger("..In retriveMeetingAttendanceDtls()..");
		try {
			this.getManagedBean().setPrepareActiveIndex(PREPARE_ACTIVE_INDEX_JOIN_IN);
			ScQtCommittee scQtCommittee = this.getManagedBean().getScQtCommittee();
			if (scQtCommittee.getMeetingAttendances() != null && !scQtCommittee.getMeetingAttendances().isEmpty()) {
				this.getManagedBean().setMeetingAttendanceList(scQtCommittee.getMeetingAttendances());
			}
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
	
	public void saveMeetingAttendance(){
		debugLogger("..In saveMeetingAttendance()..");
		IQTMeetingSO meetingso = null;
		try {
			meetingso = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);

			MeetingReqDTO metReqDtO = new MeetingReqDTO();
			ScQtCommittee qtCommittee = this.getManagedBean().getScQtCommittee();
			
			if (getManagedBean().getMeetingAttendanceList() != null && !getManagedBean().getMeetingAttendanceList().isEmpty()) {
				qtCommittee.setMeetingAttendances(getManagedBean().getMeetingAttendanceList());
			}
			
			metReqDtO.setScQtCommitte(qtCommittee);
			meetingso.saveQTDtl(metReqDtO);
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Turut Hadir Saved Successfully", ""));
		} finally {

		}
	}
	
	public void saveSuppFinalization(){
		debugLogger("..In saveSuppFinalization()..");
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			
			ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
			if(scQtFinalization == null){
				scQtFinalization = new ScQtFinalization();
			}
			setUserDetails(scQtFinalization);
			scQtFinalization.setScQt(getManagedBean().getScQt());
			
			PmParameter decisionType = getPmParameterByTypeAndCode(ParameterTypeConst.FINALIZATION_DECISION_TYPE,getManagedBean().getDecisionType());
			String decisionTypeCode = decisionType.getParameterCode();
			scQtFinalization.setDecision(decisionType);
			
			if(decisionTypeCode.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)){
				recommendSupplier(scQtFinalization);
				
			}else if(decisionTypeCode.equals(ParameterConst.DECISION_RE_EVALUATION)){
				String reEvaluateBy = getManagedBean().getReEvaluateBy();
				boolean reEvaluateTech = getManagedBean().isReEvaluateReqForTech();
				boolean reEvaluateFin = getManagedBean().isReEvaluateReqForFin();
				
				scQtFinalization.setReEvaluateCommitte(reEvaluateBy);
				scQtFinalization.setReEvaluateTechReq(reEvaluateTech);
				scQtFinalization.setReEvaluateFinReq(reEvaluateFin);
			
			}else if(decisionTypeCode.equals(ParameterConst.DECISION_RE_PUBLISH)){
				boolean specChanges = getManagedBean().getReqSpecChanges();
				scQtFinalization.setSpecChangeReq(specChanges);
			
			}else if(decisionTypeCode.equals(ParameterConst.DECISION_CANCEL)){
				//nothing to set
			}
			
			reqDTO.setScQtFinalization(scQtFinalization);
			qtSuppFinalizationSO.saveSuppFinalization(reqDTO);
			retrieveSuppFinalizationDtls();
			
			//String docType = ;
			String qtId = getManagedBean().getScQt().getQtId().toString();
			String desc = getParameterDesc( ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_700004, new String[] {desc,qtId},
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

		} finally {

		}
	}
	
	private void recommendSupplier(ScQtFinalization scQtFinalization){
		PmParameter recommendType = getPmParameterByTypeAndCode(ParameterTypeConst.SC_RECOMMEND_TYPE, getManagedBean().getRecommendType());
		String recommendTypeCode = recommendType.getParameterCode();
		scQtFinalization.setRecommendedTypeId(recommendType);
		
		scQtFinalization.setLoiLoaBy(getManagedBean().getLoiLoaPreparedBy());
		scQtFinalization.setOnlineBiddingEndorse(getManagedBean().getBiddingEndorsement());
		
		scQtFinalization.setHigherAuthReq(getManagedBean().getFwdHigherAuthority().equalsIgnoreCase("true")?true:false);
		scQtFinalization.setHigherCommitteStr(getManagedBean().getFinalizationCommittee());
		scQtFinalization.setHigherSecretary(getManagedBean().getCommitteeSecretary());
		scQtFinalization.setReasonStr(getManagedBean().getReason());
		
		if(recommendTypeCode.equals(ParameterConst.RECOMMEND_TYPE_PACKAGE)){
			saveByPackage(scQtFinalization);
		
		}else if(recommendTypeCode.equals(ParameterConst.RECOMMEND_TYPE_ITEM)){
			saveByItem(scQtFinalization);
		}
	}
	
	private void saveByPackage(ScQtFinalization scQtFinalization){
		List<ScQtRecommend> scQtRecommendList = new ArrayList<ScQtRecommend>();
		List<ScQtRecommendDtl> scQtRecommendDtlList = new ArrayList<ScQtRecommendDtl>();
		
		ScQtEvaluationResult scQtEvaluationResult = getManagedBean().getRecommendedSupplier();
		if(scQtEvaluationResult != null){
			//award
			for(ScQtEvaluationResultDtl scQtEvaluationResultDtl : scQtEvaluationResult.getEvaluationResultDtls()){
				ScQtRecommend scQtRecommend = new ScQtRecommend();
				
				prepareRecommendInfo(scQtFinalization, scQtRecommend, scQtEvaluationResultDtl);
				ScQtRecommendDtl scQtRecommendDtl = prepareRecommendDtlInfo(scQtRecommend, scQtEvaluationResult, scQtEvaluationResultDtl);
				
				scQtRecommendDtlList.add(scQtRecommendDtl);
				scQtRecommend.setRecommendDtls(scQtRecommendDtlList);
				scQtRecommendList.add(scQtRecommend);
			}
		}else{
			//multiple LOI or Online Bidding
			List<ScQtEvaluationResult> recommendedSupplierList = getManagedBean().getRecommendedSupplierList();
			
			//To add the recommend entity
			List<ScQtRecommend> tempScQtRecommendList = new ArrayList<ScQtRecommend>();
			ScQtEvaluationResult evaluationResult = recommendedSupplierList.get(0);
			for(ScQtEvaluationResultDtl scQtEvaluationResultDtl: evaluationResult.getEvaluationResultDtls()){
				ScQtRecommend scQtRecommend = new ScQtRecommend();
				prepareRecommendInfo(scQtFinalization, scQtRecommend, scQtEvaluationResultDtl);
				
				tempScQtRecommendList.add(scQtRecommend);
			}
			
			//to set the recommend details for each recommend
			for(ScQtRecommend scQtRecommend : tempScQtRecommendList){
				for(ScQtEvaluationResult evalResult : recommendedSupplierList){
					for(ScQtEvaluationResultDtl evalResultDtl : evalResult.getEvaluationResultDtls()){
						ScQtRecommendDtl scQtRecommendDtl = prepareRecommendDtlInfo(scQtRecommend, evalResult, evalResultDtl);
						
						scQtRecommendDtlList.add(scQtRecommendDtl);
						break;
					}
					scQtRecommend.setRecommendDtls(scQtRecommendDtlList);
					scQtRecommendList.add(scQtRecommend);
				}
			}
		}
		scQtFinalization.setScQtRecommends(scQtRecommendList);
	}
	
	private void prepareRecommendInfo(ScQtFinalization scQtFinalization, ScQtRecommend scQtRecommend, ScQtEvaluationResultDtl scQtEvaluationResultDtl){
		PmParameter recommendBy = getPmParameterById(getManagedBean().getRecommendBy());
		scQtRecommend.setRecommendBy(recommendBy);
		scQtRecommend.setTplSpecZoneDtl(getManagedBean().getTplSpecZoneDtl());
		scQtRecommend.setRequestItem(scQtEvaluationResultDtl.getScRequestItem());
		scQtRecommend.setScQtFinalization(scQtFinalization);
	}
	
	private ScQtRecommendDtl prepareRecommendDtlInfo(ScQtRecommend scQtRecommend, ScQtEvaluationResult scQtEvaluationResult,ScQtEvaluationResultDtl scQtEvaluationResultDtl){
		ScQtRecommendDtl scQtRecommendDtl = new ScQtRecommendDtl();
		scQtRecommendDtl.setSupplier(scQtEvaluationResult.getScQtProposal().getScQtSupplier().getSmSupplier());
		scQtRecommendDtl.setAwardQty(scQtEvaluationResultDtl.getScRequestItem().getQuantity());
		scQtRecommendDtl.setScQtRecommend(scQtRecommend);
		return scQtRecommendDtl;
	}
	
	private void saveByItem(ScQtFinalization scQtFinalization){
		List<ScQtRecommend> scQtRecommendList = new ArrayList<ScQtRecommend>();
		List<ScQtRecommendDtl> scQtRecommendDtlList = new ArrayList<ScQtRecommendDtl>();
		
		List<ScRequestItem> itemList = getManagedBean().getItemList();
		for(ScRequestItem scRequestItem : itemList){
			ScQtRecommend scQtRecommend = new ScQtRecommend();
			
			PmParameter recommendBy = getPmParameterById(scRequestItem.getRecommendBy());
			scQtRecommend.setRecommendBy(recommendBy);
			scQtRecommend.setTplSpecZoneDtl(getManagedBean().getTplSpecZoneDtl());
			scQtRecommend.setRequestItem(scRequestItem);
			scQtRecommend.setScQtFinalization(scQtFinalization);
			
			for(ScQtEvaluationResult evalResult : scRequestItem.getAwardSupplierList()){
				ScQtRecommendDtl scQtRecommendDtl = new ScQtRecommendDtl();
				scQtRecommendDtl.setSupplier(evalResult.getScQtProposal().getScQtSupplier().getSmSupplier());
				
				if(recommendBy.getParameterCode().equals(ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD)){
					scQtRecommendDtl.setAwardQty(evalResult.getQtyAward());
				}else{
					for(ScQtEvaluationResultDtl evalResultDtl : evalResult.getEvaluationResultDtls()){
						scQtRecommendDtl.setAwardQty(evalResultDtl.getScRequestItem().getQuantity());
						break;
					}
				}
				scQtRecommendDtl.setScQtRecommend(scQtRecommend);
				scQtRecommendDtlList.add(scQtRecommendDtl);
			}
			scQtRecommend.setRecommendDtls(scQtRecommendDtlList);
			scQtRecommendList.add(scQtRecommend);
		}
		scQtFinalization.setScQtRecommends(scQtRecommendList);
	}
	
	public void submitSuppFinalization(){
		//TODO - BPM
		saveSuppFinalization();
		
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
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
		
		String variable1 = getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION) + " " + getManagedBean().getScQt().getQtNo();
		String variable2 = CommonMethod.getMessageCommon("lbl.sc.supplier.finalization");
		commonMessageAcknowledmentPage(MessageConst.INFO_700026, variable1, variable2);
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
		String reEvaluateBy = scQtFinalization.getReEvaluateCommitte();
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
		}
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

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setDocumentCategory(ParameterConst.DOCUMENT_CATEGORY_BRIEFING_PAPER);
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.retrieveDocumentsByDocCategory(reqDTO);
			if (resDTO.getDocumentResultBean() != null) {
				this.getManagedBean().setDocumentList(resDTO.getDocumentResultBean());
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	/**
	 * Download Briefing Paper/Kertas Taklimat document list. 
	 * If not generated yet, system will generate from the Template to word document and will save in the ScQtReport and SCAttachment.
	 * Else, will download from ScQtReport and SCAttachment .
	 * 
	 * For the template - Use Contract Letter Template to add in the template (Text Editor)
	 * 
	 * @param pmDocument
	 * @throws IOException
	 */
	public void downloadDocFile(PmDocument pmDocument) throws IOException{
		log.debug("..In downloadDocFile(String parameterCode)..");
		
		ScQtReport scQtReport = retrieveBriefingPaperByDocId(pmDocument);
		if(scQtReport != null){
			//download from DB
			String filePath = scQtReport.getScQtRptBriefingPaperAttachment().getFilePath();
			String fileName = scQtReport.getScQtRptBriefingPaperAttachment().getFileName();
			downloadFile(filePath, fileName);
		}else{
			//system generate, save to DB and download
			generatedAndDownloadDoc(pmDocument);
		}
		log.debug("Exit downloadDocFile(.)");
	}
	
	//Kertas Taklimat
	private ScQtReport retrieveBriefingPaperByDocId(PmDocument pmDocument) {
		IQTSuppFinalizationSO qtSuppFinalizationSO;
		ScQtReport scQtReport = null;
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
	}
	
	private void generatedAndDownloadDoc(PmDocument pmDocument) throws UnsupportedEncodingException{
		IContractTemplateSO contractTemplateSO = null;
		String filePath = "";
		String fileName = "";
		String parameterCode = pmDocument.getDocDefinitionKey();
		try{
			contractTemplateSO = (IContractTemplateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractTemplateSO.class);
			ContractTemplateReqDTO reqDTO = new ContractTemplateReqDTO();
			ContractTemplateResDTO resDTO;
			String content="";
			String tempDir = FacesContext.getCurrentInstance().getExternalContext().getRealPath("\\docDownload");
			
			if(SourcingConstants.DOC_DEF_KEY_KT_COVER.equalsIgnoreCase(parameterCode)){
				reqDTO.setTemplateName(SourcingConstants.TEMPLATE_KT_COVER);
				resDTO = contractTemplateSO.retrieveTemplateByName(reqDTO);
				content = new String(resDTO.getCtTemplate().getContent(), "UTF-8");
				filePath = tempDir + "\\BriefingPaper_CoverPage.docx";
				fileName = "BriefingPaper_CoverPage.docx";
			
			}else if(SourcingConstants.DOC_DEF_KEY_KT_OFFERED_QT_PRICE.equalsIgnoreCase(parameterCode)){
				reqDTO.setTemplateName(SourcingConstants.TEMPLATE_KT_OFFERED_QT_PRICE);
				resDTO = contractTemplateSO.retrieveTemplateByName(reqDTO);
				content = new String(resDTO.getCtTemplate().getContent(), "UTF-8");
				filePath = tempDir + "/BriefingPaper_Offered_and_QT_Info.docx";
				fileName = "BriefingPaper_CoverPage.docx";
			}else{
				//FIXME - more template and the evaluation reports, which suppose alrdy generated
			}
			
			content = getContent(content, parameterCode);
		 	converthtmlToDocFile(content, filePath);
		 	File file = downloadFile(filePath, fileName);
		 	
		 	//convert to Uploaded File
			uploadFileAttachment((UploadedFile) file, pmDocument);
			
		}catch(IOException e) {
			  e.printStackTrace();
		}
	}
	
	public void uploadFileAttachment(UploadedFile file, PmDocument pmDocument) throws FileNotFoundException, IOException {
		debugLogger("handleFileAttachment");
		
		ScQtReport scQtReport = new ScQtReport();
		scQtReport.setScQt(this.getManagedBean().getScQt());
		if(this.getManagedBean().getScQt().getProcurementMode().getParameterCode().equals(ParameterConst.PROCUREMENT_MODE_QUOTATION)){
			scQtReport.setDocType(ParameterConst.DOCTYPE_QUOTATION);
		}else if(this.getManagedBean().getScQt().getProcurementMode().getParameterCode().equals(ParameterConst.PROCUREMENT_MODE_TENDER)){
			scQtReport.setDocType(ParameterConst.DOCTYPE_TENDER);
		}
		scQtReport.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_BRIEFING_PAPER);
		scQtReport.setTitle(pmDocument.getDocName());
		scQtReport.setDefinationType(pmDocument.getDocDefinitionTypeCode());
		scQtReport.setPmDocument(pmDocument);
		
		ScQtRptBriefingPaperAttachment attachment = new ScQtRptBriefingPaperAttachment();
		attachment.setScQtReport(scQtReport);
		attachment.setDocNo(scQtReport.getScQt().getQtNo());
		attachment.setUser(getFacesScopes().getUserSessionBean());
		attachment.setChangedDate(new Timestamp(System.currentTimeMillis()));
		
		uploadAttachment(file, attachment);
		scQtReport.setScQtRptBriefingPaperAttachment(attachment);
		
		IQTSuppFinalizationSO suppFinalizationSO = null;
		try {
			suppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setScQtReport(scQtReport);
			suppFinalizationSO.saveBriefingPaper(reqDTO);
		} finally {
			suppFinalizationSO = null;
		}
	}
	
	public void downloadAll(){
		log.debug("Inside downloadAll()");
		IContractTemplateSO contractTemplateSO = null;
		String filePath1, filePath2, filePath3;
		String content;
		String tempDir = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/docDownload");
		try{
			contractTemplateSO = (IContractTemplateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractTemplateSO.class);
			ContractTemplateReqDTO reqDTO = new ContractTemplateReqDTO();
			
			reqDTO.setTemplateName(SourcingConstants.TEMPLATE_KT_COVER);
			ContractTemplateResDTO resDTO = contractTemplateSO.retrieveTemplateByName(reqDTO);
			content = new String(resDTO.getCtTemplate().getContent(), "UTF-8");
			content = getContent(content, SourcingConstants.TEMPLATE_KT_COVER);
			filePath1 = tempDir + "/BriefingPaper_CoverPage.docx";
			converthtmlToDocFile(content, filePath1);
			
			File[] all = {new File(filePath1)};   
			File zipFile = new File(tempDir + "/BriefingPaper.zip");
			try {
				zipFile.createNewFile();
				byte[] buffer = new byte[1024];   
				ZipOutputStream out = new ZipOutputStream(new   
				FileOutputStream(zipFile));   
				out.setLevel(Deflater.DEFAULT_COMPRESSION);   
				for (int i = 0; i < all.length; i++) {
				  FileInputStream in = new FileInputStream(all[i]);   
				  out.putNextEntry(new ZipEntry(all[i].getName()));   
				  int len;   
				  while ((len = in.read(buffer)) > 0) {
				    out.write(buffer, 0, len);   
				  }   
				  out.closeEntry();   
				  in.close();   
				}  
				out.close();   
			} catch (IOException e) {
				e.printStackTrace();
			}
			downloadFile(tempDir + "/BriefingPaper.zip", "BriefingPaper.zip");
		}catch(Exception exp){
			exp.printStackTrace();
		}
		log.debug("Exit downloadAll()");
	}
	
	private String getContent(String content, String parameterCode){
		log.debug("Inside getContent(..)");
		
		//CommonUtils commonUtils = new CommonUtils();
		//SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		//DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		Calendar calendar = Calendar.getInstance();  
		calendar.setTime(new Date());  

		Map<String, String> valuesMap = new HashMap<String, String>();
		
		if(SourcingConstants.DOC_DEF_KEY_KT_COVER.equalsIgnoreCase(parameterCode)) {
			valuesMap.put("BIL_KERTAS_TAKLIMAT", String.valueOf(1));//FIXME
			valuesMap.put("YEAR", String.valueOf(calendar.get(Calendar.YEAR)));
			valuesMap.put("MINISTRY_DEPARTMENT_PTJ", getManagedBean().getScQt().getPmOrgProfile().getOrgName());
			valuesMap.put("COMMITTEE_TYPE", getManagedBean().getScQt().getPmOrgProfile().getOrgName());//FIXME
			valuesMap.put("TITLE", getManagedBean().getScQt().getQtTitle());
			
			StrSubstitutor sub = new StrSubstitutor(valuesMap);
			content = sub.replace(content);
		}
		//FIXME
		log.debug("Exit getContent(..)");
		return content;
	}
	
	private void retrieveSuppFinalizationDtls() {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(qtId);//FIXME
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.retrieveSuppFinalizationDtls(reqDTO);
			getManagedBean().setScQtFinalization(resDTO.getScQtFinalization());
			
			if(resDTO.getScQtFinalization() != null){
				ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
				
				//set the managed bean
				String decisionTypeCode = scQtFinalization.getDecision().getParameterCode();
				getManagedBean().setDecisionType(decisionTypeCode);
				
				if(decisionTypeCode.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)){
					String recommendType = scQtFinalization.getRecommendedTypeId().getParameterCode();
					getManagedBean().setRecommendType(recommendType);
					getManagedBean().setBiddingEndorsement(scQtFinalization.getOnlineBiddingEndorse());
					getManagedBean().setLoiLoaPreparedBy(scQtFinalization.getLoiLoaBy());
					getManagedBean().setFwdHigherAuthority(scQtFinalization.isHigherAuthReq()? "true" : "false");
					if(getManagedBean().getFwdHigherAuthority().equalsIgnoreCase("true")){
						getManagedBean().setFinalizationCommittee(scQtFinalization.getHigherCommitte() != null ? scQtFinalization.getHigherCommitte().getParameterId().toString(): null);
						getManagedBean().setCommitteeSecretary(scQtFinalization.getHigherSecretary());
						getManagedBean().setCommitteeSecretaryName(scQtFinalization.getHigherSecretary().getUserName());
						getManagedBean().setReason(scQtFinalization.getReason() != null ? scQtFinalization.getReason().getReasonId().toString(): null);
					}
					
					if(recommendType.equals(ParameterConst.RECOMMEND_TYPE_PACKAGE)){
						if(scQtFinalization.getScQtRecommends().size() > 0){
							ScQtRecommend scQtRecommend = scQtFinalization.getScQtRecommends().get(0);
							getManagedBean().setRecommendBy(scQtRecommend.getRecommendBy().getParameterId());
							
							if(scQtRecommend.getRecommendDtls().size() > 0){
								Long selectedSupplierId = scQtRecommend.getRecommendDtls().get(0).getSupplier().getSupplierId();
								for(ScQtEvaluationResult scQtEvaluationResult : getManagedBean().getShortlistedSupplierList()){
									Long suppId = scQtEvaluationResult.getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierId();
									if(suppId == selectedSupplierId){
										scQtEvaluationResult.setSelected(true);
										getManagedBean().setRecommendedSupplier(scQtEvaluationResult);
									}
								}
							}
						}
					}else{
						//TODO
					}
				}else if(decisionTypeCode.equals(ParameterConst.DECISION_RE_EVALUATION)){
					getManagedBean().setReEvaluateBy(scQtFinalization.getReEvaluateCommitte());
					getManagedBean().setReEvaluateReqForTech(scQtFinalization.isReEvaluateTechReq());
					getManagedBean().setReEvaluateReqForFin(scQtFinalization.isReEvaluateFinReq());
					
				}else if(decisionTypeCode.equals(ParameterConst.DECISION_RE_PUBLISH)){
					getManagedBean().setReqSpecChanges(scQtFinalization.isSpecChangeReq());
					
				}else if(decisionTypeCode.equals(ParameterConst.DECISION_CANCEL)){
					//nothing to set
				}
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	private void retrieveItemSupplierList(boolean itemize) {
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
	}
	
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

			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qtId);//FIXME
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
			
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setQtId(qtId);
			
			reqDTO.setCommitteeType(commType);
			reqDTO.setFinalizationSecratary(true);
				
			CommitteResDTO resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
			ScQtCommittee scQtCommittee = resDTO.getScQtCommittee();
			
			List<ScCommitteeMember> scCommitteeMembers = scQtCommittee.getScCommittee().getScCommitteeMembers();
			for(ScCommitteeMember scCommitteeMember : scCommitteeMembers){
				String secretaryRole = scCommitteeMember.getMemberRole().getParameterCode();
				if(secretaryRole.equals(ParameterConst.COMMITTEE_ROLE_SECRETARY)){
					getManagedBean().setCommitteeSecretary(scCommitteeMember.getPmUser());
					getManagedBean().setCommitteeSecretaryName(scCommitteeMember.getMemberName());
				}
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
		
		SelectItem[] items = getDropdownValuesBySearchBean(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, searchBean);
		return items;
    }
	
	public SelectItem[] getItemRecommendByDropDown(PmParameter priceType) {
		SelectItem[] items = null;
				
		String priceTypeCode = priceType.getParameterCode();
		if(priceTypeCode.equals(ParameterConst.PRICE_TYPE_TARIFF) || priceTypeCode.equals(ParameterConst.PRICE_TYPE_TIER)){
			//same as package
			items = getPackageRecommendByDropDown();
			
		}else{
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_EQUALS.value(), ParameterConst.RECOMMEND_BY_AWARD));
			
			items = getDropdownValuesBySearchBean(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, searchBean);
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
		
		String decision = getManagedBean().getDecisionType();
		this.getManagedBean().setRecommendSupplier(false);
		this.getManagedBean().setReEvaluate(false);
		this.getManagedBean().setRePublish(false);
		
		if(decision == null){
			resetFlag();
			
		}else if(decision.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)){
			this.getManagedBean().setRecommendSupplier(true);
			
		}else if(decision.equals(ParameterConst.DECISION_RE_EVALUATION)){
			this.getManagedBean().setReEvaluate(true);
			resetFlag();
			
		}else if(decision.equals(ParameterConst.DECISION_RE_PUBLISH)){
			this.getManagedBean().setRePublish(true);
			resetFlag();
		}
	}

	/**
	 * Event for Recommendation Type By Item or By Package in Supplier Finalization
	 */
	public void recommendTypeEvent() {
		debugLogger("recommendTypeEvent");
		
		String recommendType = getManagedBean().getRecommendType();
		boolean	itemize = false;
		String selectionMode = QTConstants.SF_SINGLE;
		
		if (recommendType.equals(ParameterConst.RECOMMEND_TYPE_ITEM)) {
			itemize = true;
			selectionMode = QTConstants.SF_MULTIPLE;
		}
		this.getManagedBean().setItemize(itemize);
		this.getManagedBean().setSelectionMode(selectionMode);
		retrieveItemSupplierList(itemize);
		retrieveSuppFinalizationDtls();
	}
	
	/**
	 * Event for Recommend By Award,Online Bidding or Multiple LOI in Supplier Finalization
	 */
	public void packageRecommendByEvent() {
		debugLogger("packageRecommendByEvent ");

		Long recommendById = getManagedBean().getRecommendBy();
		PmParameter pmParameter = null;
		if(recommendById == null){
			pmParameter = getPmParameterByTypeAndCode(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, ParameterConst.RECOMMEND_BY_SINGLE_AWARD);
			getManagedBean().setRecommendBy(pmParameter.getParameterId());
		}else{
			pmParameter = getPmParameterById(getManagedBean().getRecommendBy());
		}
		
		String recommendBy = pmParameter.getParameterCode();
		this.getManagedBean().setSingleAward(false);
		this.getManagedBean().setOnlineBidding(false);
		this.getManagedBean().setMultipleLoi(false);
		
		if(recommendBy.equals(ParameterConst.RECOMMEND_BY_AWARD)){
			this.getManagedBean().setSelectionMode(QTConstants.SF_SINGLE);
			this.getManagedBean().setSingleAward(true);
			this.getManagedBean().setBiddingEndorsement(null);
				
		}else if(recommendBy.equals(ParameterConst.RECOMMEND_BY_ONLINE_BIDDING)){
			this.getManagedBean().setSelectionMode(QTConstants.SF_MULTIPLE);
			this.getManagedBean().setOnlineBidding(true);
			
		}else if(recommendBy.equals(ParameterConst.RECOMMEND_BY_MULTIPLE_LOI)){
			this.getManagedBean().setSelectionMode(QTConstants.SF_MULTIPLE);
			this.getManagedBean().setMultipleLoi(true);
			this.getManagedBean().setBiddingEndorsement(null);
		}
	}
	
	public void itemRecommendByEvent(ScRequestItem scRequestItem) {
		debugLogger("itemRecommendByEvent");
		
		Long recommendById = scRequestItem.getRecommendBy();
		PmParameter pmParameter = getPmParameterById(recommendById);
		String recommendBy = pmParameter.getParameterCode();
		
		if(recommendBy.equals(ParameterConst.RECOMMEND_BY_AWARD) || recommendBy.equals(ParameterConst.RECOMMEND_BY_SINGLE_AWARD)){
			//clear the list if more than 1
			if(scRequestItem.getAwardSupplierList() != null){
				int size = scRequestItem.getAwardSupplierList().size();
				if(size > 1){
					scRequestItem.getAwardSupplierList().clear();
					getManagedBean().getShortlistedSupplierList().clear();
				}
			}
		}
	}
	
	/**
	 * Event for Forward to Higher Authority in Supplier Finalization
	 */
	public void fwdHigherAuthorityEvent() {
		debugLogger("fwdHigherAuthorityEvent");
		
		String fwdHigherAuthority = getManagedBean().getFwdHigherAuthority();
		boolean	forward = false;
		
		if (fwdHigherAuthority.equalsIgnoreCase("true")) {
			forward = true;
			
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
			getManagedBean().setFinalizationCommittee(null);
			getManagedBean().setCommitteeSecretary(null);
			getManagedBean().setCommitteeSecretaryName(null);
			getManagedBean().setReason(null);
		}
		this.getManagedBean().setForward(forward);
	}
	
	public void finalizationCommitteeEvent() {
		debugLogger("finalizationCommitteeEvent");
		
		String finalizationCommittee = getManagedBean().getFinalizationCommittee();
		PmParameter pmParameter = getPmParameterById(new Long(finalizationCommittee));
		retrieveCommitteeSecretary(pmParameter.getParameterCode());
	}
	
	public void selectItemEvent(SelectEvent event) {
		debugLogger("selectItemEvent");
		
		ScRequestItem scRequestItem = (ScRequestItem) event.getObject();
		getManagedBean().setRecommendedSupplierList(new ArrayList<ScQtEvaluationResult>());
		List<ScQtEvaluationResult> recommendedSupplierList = getManagedBean().getRecommendedSupplierList();
		if(scRequestItem.getAwardSupplierList() != null){
			recommendedSupplierList.addAll(scRequestItem.getAwardSupplierList());
		}
	}
	
	public void unselectItemEvent(UnselectEvent event) {
		debugLogger("unselectItemEvent");
		
		ScRequestItem scRequestItem = (ScRequestItem) event.getObject();
		if(getManagedBean().getSelectedItemList() == null){
			getManagedBean().setSelectedItemList(new ArrayList<ScRequestItem>());
		}else{
			List<ScRequestItem> selectedItemList = getManagedBean().getSelectedItemList();
			for(ScRequestItem item : selectedItemList){
				if(scRequestItem.equals(item)){
					selectedItemList.remove(item);
				}
			}
		}
	}
	
	public void selectSupplierEvent() {
		debugLogger("selectSupplierEvent");
		
		List<ScRequestItem> selectedItemList = getManagedBean().getSelectedItemList();
		
		if(selectedItemList != null){
			List<ScQtEvaluationResult> recomendedList = getManagedBean().getRecommendedSupplierList();
			for(ScRequestItem item : selectedItemList){
				item.setAwardSupplierList(new ArrayList<ScQtEvaluationResult>());
				for(ScQtEvaluationResult recomendedSupplier : recomendedList){
					if(selectedItemList != null){
						item.getAwardSupplierList().add(recomendedSupplier);
					}
				}
				if(item.getAwardSupplierList() != null){
					String parameterType = ParameterTypeConst.FINALIZATION_RECOMMEND_BY;
					PmParameter pmParameter = null;
					String priceType = item.getTplSpecQuestion().getClPriceTypeCode().getParameterCode();
					
					if(priceType.equals(ParameterConst.PRICE_TYPE_TARIFF) || priceType.equals(ParameterConst.PRICE_TYPE_TIER)){
						if(item.getAwardSupplierList().size()==1){
							pmParameter = getPmParameterByTypeAndCode(parameterType, ParameterConst.RECOMMEND_BY_AWARD);
							item.setRecommendBy(pmParameter.getParameterId());
							item.setRecommendStr(pmParameter.getParameterCode());
						}else if(item.getAwardSupplierList().size()>1){
							//If its price type tuer/tariff, should clear the recommend by, 
							//user need to select multiple LOI or online bidding
							item.setRecommendBy(null);
							item.setRecommendStr(null);
						}
					}else{
						if(item.getAwardSupplierList().size()==1){
							pmParameter = getPmParameterByTypeAndCode(parameterType, ParameterConst.RECOMMEND_BY_SINGLE_AWARD);
							item.setRecommendBy(pmParameter.getParameterId());
							item.setRecommendStr(pmParameter.getParameterCode());
						}else if(item.getAwardSupplierList().size()>1){
							pmParameter = getPmParameterByTypeAndCode(parameterType, ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD);
							item.setRecommendBy(pmParameter.getParameterId());
							item.setRecommendStr(pmParameter.getParameterCode());
						}
					}
				}
			}
		}
	}
	
	private PmParameter getPmParameterById(Long parameterId) {
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
	}
	
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
		List<ScRequestItem> selectedItemList = getManagedBean().getSelectedItemList();
		if(selectedItemList != null){
			for(ScRequestItem item : selectedItemList){
				if(item.getAwardSupplierList() == null){
					item.setAwardSupplierList(new ArrayList<ScQtEvaluationResult>());
				}
				if(!item.getAwardSupplierList().contains(awardSupplier)){
					item.getAwardSupplierList().add(awardSupplier);
					
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
					String priceType = item.getTplSpecQuestion().getClPriceTypeCode().getParameterCode();
					if(priceType.equals(ParameterConst.PRICE_TYPE_TARIFF) || priceType.equals(ParameterConst.PRICE_TYPE_TIER)){
						if(item.getAwardSupplierList().size()==1){
							pmParameter = getPmParameterByTypeAndCode(parameterType, ParameterConst.RECOMMEND_BY_AWARD);
							item.setRecommendBy(pmParameter.getParameterId());
							item.setRecommendStr(pmParameter.getParameterCode());
						}else if(item.getAwardSupplierList().size()>1){
							//If its price type tuer/tariff, should clear the recommend by, 
							//user need to select multiple LOI or online bidding
							item.setRecommendBy(null);
							item.setRecommendStr(null);
						}
					}else{
						if(item.getAwardSupplierList().size()==1){
							pmParameter = getPmParameterByTypeAndCode(parameterType, ParameterConst.RECOMMEND_BY_SINGLE_AWARD);
							item.setRecommendBy(pmParameter.getParameterId());
							item.setRecommendStr(pmParameter.getParameterCode());
						}else if(item.getAwardSupplierList().size()>1){
							pmParameter = getPmParameterByTypeAndCode(parameterType, ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD);
							item.setRecommendBy(pmParameter.getParameterId());
							item.setRecommendStr(pmParameter.getParameterCode());
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
	
	private void resetFlag(){
		this.getManagedBean().setOnlineBidding(false);
		this.getManagedBean().setForward(false);
		
		PmParameter pmParameter = getPmParameterByTypeAndCode(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, ParameterConst.RECOMMEND_BY_SINGLE_AWARD);
		this.getManagedBean().setRecommendBy(pmParameter.getParameterId());
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
