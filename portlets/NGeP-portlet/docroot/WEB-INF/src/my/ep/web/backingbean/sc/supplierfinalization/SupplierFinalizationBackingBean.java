package my.ep.web.backingbean.sc.supplierfinalization;

import java.io.File;
import java.io.FileInputStream;
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

import my.ep.biz.service.ct.tpl.IContractTemplateSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.ct.tpl.ContractTemplateReqDTO;
import my.ep.dto.ct.tpl.ContractTemplateResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
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
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.supplierfinalization.SupplierFinalizationManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pp.common.CommonMethod;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.log4j.Logger;
import org.primefaces.event.TabChangeEvent;
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
	
	public static final String NAVIGATE_TO_PREPARATION = "suppFinalizationMenu.xhtml";
	
	//temporary
	Long qtId = 146L;
	String committeeType = ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A;
	
	public SupplierFinalizationBackingBean() {
		if(this.getManagedBean().getMeetingList() == null){
			retriveMeetingDtls();
		}
	}
	
	//Preparation Tab menu
	public void onTabChangeAction(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();
		
		if (tabTitle.equalsIgnoreCase(CommonMethod.getMessageCommon("lbl.sc.meeting.details"))) {
			retriveMeetingDtls();

		} else if (tabTitle.equalsIgnoreCase(CommonMethod.getMessageCommon("lbl.sc.committee.b"))) {
			//FIXME - title is generic
			retriveCommitteeDtls();
		
		} else if (tabTitle.equalsIgnoreCase(CommonMethod.getMessageCommon("lbl.sc.join.in"))) {
			retriveMeetingAttendanceDtls();
		}
	}
	
	//Supplier Finalization Tab Menu
	public void onTabMenuChangeAction(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();
		
		if (tabTitle.equalsIgnoreCase(CommonMethod.getMessageCommon("lbl.sc.preparation"))) {
			retriveMeetingDtls();

		} else if (tabTitle.equalsIgnoreCase(CommonMethod.getMessageCommon("lbl.sc.briefing.paper"))) {
			retrieveBriefingPaper();
		
		} else if (tabTitle.equalsIgnoreCase(CommonMethod.getMessageCommon("lbl.sc.supplier.finalization"))) {
			decisionEvent();
			recommendTypeEvent();
			fwdHigherAuthorityEvent();
			recommendByEvent();
			
			retrieveSuppFinalizationDtls();
	
		} else if (tabTitle.equalsIgnoreCase(CommonMethod.getMessageCommon("lbl.sc.committee.endorsement"))) {
			retrieveCommitteeEndorsement();
		}
	}
	
	public String retriveMeetingDtls() {
		debugLogger("..In retriveMeetingDtls()..");
		IQTMeetingSO meetingSO = null;
		try {
			this.getManagedBean().setActiveIndex(ACTIVE_INDEX_PREPARATION);
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
				
				populateParentOrgProfile(committee.getScQt());
				this.getManagedBean().setScQt(committee.getScQt());
			}
		} finally {
			meetingSO = null;
		}
		return NAVIGATE_TO_PREPARATION;
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
			if (this.getManagedBean().getMeetingList() != null && !this.getManagedBean().getMeetingList().isEmpty()) {
				qtCommittee.setScQtMeetings(this.getManagedBean().getMeetingList());
			}
			metReqDtO.setScQtCommitte(qtCommittee);
			meetingso.saveQTDtl(metReqDtO);
			
			if(this.getManagedBean().getSelectedMeetingList()!= null && !this.getManagedBean().getSelectedMeetingList().isEmpty()){
				metReqDtO = new MeetingReqDTO();
				
			}
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Meeting Details Saved Successfully", "Supplier Finalization"));
		} finally {

		}
	}
	
	public String retriveCommitteeDtls() {
		debugLogger("..In retriveCommitteeDtls()..");
		IQTMeetingSO meetingSO = null;
		try {
			this.getManagedBean().setPrepareActiveIndex(PREPARE_ACTIVE_INDEX_COMMITTEE);
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			MeetingResDTO resDTO = new MeetingResDTO();

			meetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			//FIXME = fara
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteType(committeeType);
			resDTO = meetingSO.retrieveMeetingDls(reqDTO);
			if (resDTO.isSuccessful()) {
				this.getManagedBean().setCommitteeList(resDTO.getCommitteMembers());
			}
			
			ScQtCommittee committee = resDTO.getQtCommittee();
			if (null == committee) {
				committee = new ScQtCommittee();
			}
			List<ScQtCommitteRemarks> remarkList = committee.getQtCommitteRemarks();
			if (remarkList == null) {
				remarkList = new ArrayList<ScQtCommitteRemarks>();
			}
		
			if (null != remarkList && !remarkList.isEmpty()) {
				//for (ScQtCommitteRemarks remark : remarkList) {
					//ScQtCommitteRemarks qtRemark = (ScQtCommitteRemarks) remark;
					//qtRemarkList.add(qtRemark);
				//}
				this.getManagedBean().setCommitteeRemarkList(remarkList);
			}
			initializeRemarks();
		} finally {
			meetingSO = null;
		}
		return NAVIGATE_TO_PREPARATION;
	}
	
	public void initializeRemarks() {
		debugLogger("..In initializeRemarks()..");
		
		FacesScopes facesScopes = new FacesScopes();
		IStatusSO iStatusSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		ScQtCommitteRemarks remark = null;
		if (this.getManagedBean().getCommitteeRemarkList() == null) {
			this.getManagedBean().setCommitteeRemarkList(new ArrayList<ScQtCommitteRemarks>());
		}

		remark = new ScQtCommitteRemarks();
		remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		remark.setChangedBy(remark.getCreatedBy());
		remark.setCreatedUser(facesScopes.getUserSessionBean());
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
	
	public SelectItem[] getRoleTypeDropdownValues(String committeeRole) {
		List<String> types = new ArrayList<String>();
		types.add("M");
		types.add("S");

		SelectItem[] items = null;
		try {
			items = new SelectItem[types.size() + 1];
			items[0] = new SelectItem(null, "- Select One -");
			int i = 1;
			for (String priceTypeCode : types) {
				items[i++] = new SelectItem(priceTypeCode, getParameterDesc(committeeRole, priceTypeCode));
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage( null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
		return items;
		// return getDropdownCodeValues(committeeRole);
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
			metReqDtO.setScQtCommitte(qtCommittee);
			meetingso.saveQTDtl(metReqDtO);
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Committee Details Saved Successfully", "Supplier Finalization"));
		} finally {

		}
	}   
	
	public String retriveMeetingAttendanceDtls() {
		debugLogger("..In retriveMeetingAttendanceDtls()..");
		try {
			this.getManagedBean().setPrepareActiveIndex(PREPARE_ACTIVE_INDEX_JOIN_IN);
			ScQtCommittee scQtCommittee = this.getManagedBean().getScQtCommittee();
			if (scQtCommittee.getMeetingAttendances() != null && !scQtCommittee.getMeetingAttendances().isEmpty()) {
				this.getManagedBean().setMeetingAttendanceList(scQtCommittee.getMeetingAttendances());
			}
		} finally {
		
		}
		return NAVIGATE_TO_PREPARATION;
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
	
	public void saveMeetingAttendanceDtls(){
		debugLogger("..In saveMeetingAttendanceDtls()..");
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
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Turut Hadir Saved Successfully", "Supplier Finalization"));
		} finally {

		}
	}
	
	public void saveSuppFinalization(){
		debugLogger("..In saveSuppFinalization()..");
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			ScQtFinalization scQtFinalization = new ScQtFinalization();
			
			setUserDetails(scQtFinalization);
			scQtFinalization.setScQt(getManagedBean().getScQt());
			scQtFinalization.setDecisionStr(getManagedBean().getDecisionType());
			if(scQtFinalization.getDecisionStr().equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)){
				scQtFinalization.setRecommendedType(getManagedBean().getRecommendType());
				scQtFinalization.setLoiLoaBy(getManagedBean().getLoiLoaPreparedBy());
				scQtFinalization.setOnlineBiddingEndorse(getManagedBean().getBiddingEndorsement());
				
				scQtFinalization.setHigherAuthReq(getManagedBean().getFwdHigherAuthority().equalsIgnoreCase("true")?true:false);
				if(scQtFinalization.isHigherAuthReq()){
					scQtFinalization.setHigherCommitteStr(getManagedBean().getFinalizationCommittee());
					//scQtFinalization.setHigherSecretaryStr(getManagedBean().geth);//FIXME
					scQtFinalization.setReasonStr(getManagedBean().getReason());
				}
				
				if(scQtFinalization.getRecommendedType().equals(ParameterConst.RECOMMEND_TYPE_PACKAGE)){
					
					List<ScQtRecommend> scQtRecommendList = new ArrayList<ScQtRecommend>();
					List<ScQtRecommendDtl> scQtRecommendDtlList = new ArrayList<ScQtRecommendDtl>();
					ScQtEvaluationResult recommendedSupplier = getManagedBean().getRecommendedSupplier();
					
					for(ScQtEvaluationResultDtl dtl :recommendedSupplier.getEvaluationResultDtls()){
						ScQtRecommend scQtRecommend = new ScQtRecommend();
						scQtRecommend.setRecommendByStr(getManagedBean().getRecommendBy());
						scQtRecommend.setTplSpecZoneDtl(getManagedBean().getTplSpecZoneDtl());//FIXME
						scQtRecommend.setRequestItem(dtl.getScRequestItem());
						scQtRecommend.setScQtFinalization(scQtFinalization);
						
						ScQtRecommendDtl scQtRecommendDtl = new ScQtRecommendDtl();
						scQtRecommendDtl.setSupplier(recommendedSupplier.getScQtProposal().getScQtSupplier().getSmSupplier());
						scQtRecommendDtl.setAwardQty(dtl.getScRequestItem().getQuantity());
						scQtRecommendDtl.setScQtRecommend(scQtRecommend);
						
						scQtRecommendDtlList.add(scQtRecommendDtl);
						scQtRecommend.setRecommendDtls(scQtRecommendDtlList);
						scQtRecommendList.add(scQtRecommend);
					}
					scQtFinalization.setScQtRecommends(scQtRecommendList);
					
				}else if(scQtFinalization.getRecommendedType().equals(ParameterConst.RECOMMEND_TYPE_ITEM)){
					//TODO
				}
				
			}else if(scQtFinalization.getDecisionStr().equals(ParameterConst.DECISION_RE_EVALUATION)){
				scQtFinalization.setReEvaluateCommitte(getManagedBean().getReEvaluateBy());
				//scQtFinalization.setReEvaluateTechReq(getManagedBean().getReEvaluateReqFor());
				//scQtFinalization.setReEvaluateFinReq(reEvaluateFinReq);
			
			}else if(scQtFinalization.getDecisionStr().equals(ParameterConst.DECISION_RE_PUBLISH)){
				scQtFinalization.setSpecChangeReq(getManagedBean().getReqSpecChanges().equalsIgnoreCase("true")?true:false);
			}
			
			reqDTO.setScQtFinalization(scQtFinalization);
			qtSuppFinalizationSO.saveSuppFinalization(reqDTO);
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Supplier Finalization Saved Successfully", "Supplier Finalization"));
		} finally {

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
	
	public void uploadFileAttachment(UploadedFile file, PmDocument pmDocument){
		debugLogger("handleFileAttachment");
		
		FacesScopes facesScopes = new FacesScopes();
		
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
		attachment.setUser(facesScopes.getUserSessionBean());
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
				getManagedBean().setDecisionType(scQtFinalization.getDecision().getParameterCode());
				getManagedBean().setRecommendType(scQtFinalization.getRecommendedType());
				getManagedBean().setBiddingEndorsement(scQtFinalization.getOnlineBiddingEndorse());
				getManagedBean().setLoiLoaPreparedBy(scQtFinalization.getLoiLoaBy());
				getManagedBean().setFwdHigherAuthority(scQtFinalization.isHigherAuthReq()? "true" : "false");
				getManagedBean().setFinalizationCommittee(scQtFinalization.getHigherCommitte() != null ? scQtFinalization.getHigherCommitte().getParameterCode(): null);
				//TODO - add secretary id
				getManagedBean().setReason(scQtFinalization.getReason() != null ? scQtFinalization.getReason().getReasonCode(): null);
				//TODO - for reevaluate/publish and cancel
				
				if(scQtFinalization.getRecommendedType().equals(ParameterConst.RECOMMEND_TYPE_PACKAGE)){
					if(scQtFinalization.getScQtRecommends().size() > 0){
						ScQtRecommend scQtRecommend= scQtFinalization.getScQtRecommends().get(0);
						getManagedBean().setRecommendBy(scQtRecommend.getRecommendBy().getParameterCode());
						
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
			reqDTO.setQtId(qtId);
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
	 * Method to call Supplier Details popup in supplier finalization
	 */
	public void showSupplierDetails() {
		debugLogger("showSuppDetails");
		if (getManagedBean().getShortlistedSupplier() != null) {
			this.getManagedBean().setShortlistedSupplier(getManagedBean().getShortlistedSupplier());
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
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteType(committeeType);
			
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
			String memberRole = committee.getScCommitteeMember().getMemberRole();
			
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
		
			PmUser user = new FacesScopes().getUserSessionBean();
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setUserId(user.getUserId().toString());
			reqDTO.setCommitteMembers(getManagedBean().getCommitteeEndorsementList());
			reqDTO.setSubject(getManagedBean().getSubject());
			reqDTO.setMessage(getManagedBean().getMessage());
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.sendNotifications(reqDTO);
			if (resDTO.isSuccessful()) {
				commonMessageAcknowledmentPage(MessageConst.INFO_700047);
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
			
			PmUser user = new FacesScopes().getUserSessionBean();
			ScQtCommitteeActivity committeeAct = null;
			boolean isChairperson = false;
			
			retrieveCommitteeEndorsement();
			for(ScQtCommitteeActivity scQtCommitteeActivity : getManagedBean().getCommitteeEndorsementList()){
				if(scQtCommitteeActivity.getScCommitteeMember().getPmUser().equals(user)){
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
				if(isChairperson){
					commonMessageAcknowledmentPage(MessageConst.INFO_700049);
				}else{
					commonMessageAcknowledmentPage(MessageConst.INFO_700048);
				}
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	private void commonMessageAcknowledmentPage(String messageId){
		String qtNo = getManagedBean().getScQt().getQtNo();
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
			FacesMessage.SEVERITY_INFO, 
			null,
			MessageUtils.getMessage(messageId, 
					new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION), qtNo}, 
					MessageUtils.BUNDLE_COMMON_MESSAGES, 
					new FacesScopes().getUserLocale())));
		 // clear data
		super.renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
	}
	
	public List<SelectItem> getDecisionDropDown(){
    	List<SelectItem> list = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownCodeValues(ParameterTypeConst.FINALIZATION_DECISION_TYPE);
		for(SelectItem item : items) {
		  if(item.getValue()!=null) {
			list.add(item);
		  }
		}
		return list;
    }
	
	public List<SelectItem> getRecommendTypeRadioBtn(){
    	List<SelectItem> list = new ArrayList<SelectItem>();
		SelectItem[] items = getRadioButtonCodeValues(ParameterTypeConst.SC_RECOMMEND_TYPE);
		for(SelectItem item : items) {
		  if(item.getValue()!=null) {
			list.add(item);
		  }
		}
		return list;
    }
	
	public List<SelectItem> getBiddingEndorsementRadioBtn(){
    	List<SelectItem> list = new ArrayList<SelectItem>();
		SelectItem[] items = getRadioButtonCodeValues(ParameterTypeConst.FINALIZATION_BIDDING_ENDORSEMENT);
		for(SelectItem item : items) {
		  if(item.getValue()!=null) {
			list.add(item);
		  }
		}
		return list;
    }
	
	public List<SelectItem> getLoiLoaPreparedByRadioBtn(){
    	List<SelectItem> list = new ArrayList<SelectItem>();
		SelectItem[] items = getRadioButtonCodeValues(ParameterTypeConst.FINALIZATION_LOILOA_PREPARED_BY);
		for(SelectItem item : items) {
		  if(item.getValue()!=null) {
			list.add(item);
		  }
		}
		return list;
    }
	
	public List<SelectItem> getFwdHigherAuthorityRadioBtn(){
    	List<SelectItem> list = new ArrayList<SelectItem>();
		SelectItem[] items = getRadioButtonCodeValues(ParameterTypeConst.YES_NO_FLAG);
		for(SelectItem item : items) {
		  if(item.getValue()!=null) {
			list.add(item);
		  }
		}
		return list;
    }
	
	public List<SelectItem> getFinalizationCommitteeDropDown(){
    	List<SelectItem> list = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownCodeValues(ParameterTypeConst.SC_COMMITTEE_TYPE);
		for(SelectItem item : items) {
		  if(item.getValue()!=null) {//FIXME - add the rules
			list.add(item);
		  }
		}
		return list;
    }
	
	public SelectItem[] getReasonRadioBtn() {
		SelectItem[] items = null;
		Map<Long, String> reasonDescMap = new HashMap<Long, String>();
		IReasonSO reasonSO = null;
		try {
			int i = 0;
			reasonSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);
			ReasonReqDTO reqDTO = new ReasonReqDTO();
			reqDTO.setReasonCategory(ParameterConst.FINALIZATION_REASON);
			reqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
			reqDTO.setSortBy("reasonDescId");
			ReasonResDTO resDTO = reasonSO.retrieveReasonDescListByCategory(reqDTO);

			List<PmReasonDesc> reasonList = resDTO.getReasonDescList();
			items = new SelectItem[reasonList.size()];
			for (PmReasonDesc reasonDesc : reasonList) {
				items[i++] = new SelectItem(reasonDesc.getPmReason().getReasonId(), reasonDesc.getReasonDesc());
				reasonDescMap.put(reasonDesc.getPmReason().getReasonId(),reasonDesc.getReasonDesc());
			}
		} finally {
			reasonSO = null;
		}
		return items;
	}
	
	public List<SelectItem> getReEvaluateByRadioBtn(){
    	List<SelectItem> list = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownCodeValues(ParameterTypeConst.CHECKLIST_TYPE);
		for(SelectItem item : items) {
		  if(item.getValue()!=null) {
			list.add(item);
		  }
		}
		return list;
    }
	
	public List<SelectItem> getReEvaluateReqForCheckBox(){
    	List<SelectItem> list = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownCodeValues(ParameterTypeConst.CHECKLIST_TYPE);
		for(SelectItem item : items) {
		  if(item.getValue()!=null) {
			list.add(item);
		  }
		}
		return list;
    }
	
	public List<SelectItem> getRecommendByDropDown(){
    	List<SelectItem> list = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownCodeValues(ParameterTypeConst.FINALIZATION_RECOMMEND_BY);
		for(SelectItem item : items) {
		  if(item.getValue()!=null) {
			list.add(item);
		  }
		}
		return list;
    }
	
	public List<SelectItem> getAwardSupplierListDropDown(){
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
    	
        List<SelectItem> list = new ArrayList<SelectItem>();
		for(SmSupplier supplier : getManagedBean().getAwardSupplierList()) {
			SelectItem item = new SelectItem(supplier.getSupplierId(),supplier.getCompanyName());
			list.add(item);
		}
		return list;
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
		
		if(decision.equalsIgnoreCase(ParameterConst.DECISION_RECOMMEND_SUPPLIER)){
			this.getManagedBean().setRecommendSupplier(true);
			
		}else if(decision.equalsIgnoreCase(ParameterConst.DECISION_RE_EVALUATION)){
			this.getManagedBean().setReEvaluate(true);
			resetFlag();
			
		}else if(decision.equalsIgnoreCase(ParameterConst.DECISION_RE_PUBLISH)){
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
		
		if (recommendType.equalsIgnoreCase(ParameterConst.RECOMMEND_TYPE_ITEM)) {
			itemize = true;
		}
		this.getManagedBean().setItemize(itemize);
		retrieveItemSupplierList(itemize);
		
	}
	
	/**
	 * Event for Recommend By Award,Online Bidding or Multiple LOI in Supplier Finalization
	 */
	public void recommendByEvent() {
		debugLogger("recommend by Event ");

		String recommendBy = getManagedBean().getRecommendBy();
		this.getManagedBean().setSingleAward(false);
		this.getManagedBean().setOnlineBidding(false);
		this.getManagedBean().setMultipleLoi(false);
		
		if(recommendBy.equalsIgnoreCase(ParameterConst.RECOMMEND_BY_SINGLE_AWARD)){
			this.getManagedBean().setSelectionMode(QTConstants.SF_SINGLE);
			this.getManagedBean().setSingleAward(true);
			
		}else if(recommendBy.equalsIgnoreCase(ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD)){
			this.getManagedBean().setSelectionMode(QTConstants.SF_MULTIPLE);
			this.getManagedBean().setMultipleAward(true);//FIXME
				
		}else if(recommendBy.equalsIgnoreCase(ParameterConst.RECOMMEND_BY_ONLINE_BIDDING)){
			this.getManagedBean().setSelectionMode(QTConstants.SF_MULTIPLE);
			this.getManagedBean().setOnlineBidding(true);
			
		}else if(recommendBy.equalsIgnoreCase(ParameterConst.RECOMMEND_BY_MULTIPLE_LOI)){
			this.getManagedBean().setSelectionMode(QTConstants.SF_MULTIPLE);
			this.getManagedBean().setMultipleLoi(true);
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
		}
		this.getManagedBean().setForward(forward);
	}
	
	/**
	 * Event for Award Supplier in Supplier Finalization
	 * It will add the supplier for the item selected
	 */
	public void awardSupplierEvent() {
		debugLogger("awardSupplierEvent");
		
		String awardSupplier = getManagedBean().getAwardSupplier();
		
		List<ScRequestItem> selectedItemList = getManagedBean().getSelectedItemList();
		if(selectedItemList != null){
			for(ScRequestItem item : selectedItemList){
				if(item.getAwardSupplierList() == null){
					item.setAwardSupplierList(new ArrayList<SmSupplier>());
				}
				//item.getAwardSupplierList().add(awardSupplier);
			}
		}
	}
	
	private void resetFlag(){
		this.getManagedBean().setOnlineBidding(false);
		this.getManagedBean().setForward(false);
		this.getManagedBean().setRecommendBy(ParameterConst.RECOMMEND_BY_SINGLE_AWARD);
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
