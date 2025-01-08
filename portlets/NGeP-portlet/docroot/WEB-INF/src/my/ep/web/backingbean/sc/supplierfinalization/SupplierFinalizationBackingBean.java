/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.supplierfinalization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.sc.actor.IScActorSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.comparator.WorklistItemVOComparator;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.sc.actor.ScActorReqDTO;
import my.ep.dto.sc.actor.ScActorResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sc.quationtender.EvaluationReqDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.sc.common.ScActor; //Bug #32682
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRemark;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaForQt;
import my.ep.persistence.entity.sc.loaloi.ScLoaRemark;
import my.ep.persistence.entity.sc.loaloi.ScLoi;
import my.ep.persistence.entity.sc.loaloi.ScLoiForQt;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;
import my.ep.persistence.entity.sc.loaloi.ScLoiRemark;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationEndorsementRecommendationRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationEndorsementRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendationUlasanRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtReverse;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalAmount;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalItem;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.CustomUtils;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.constant.sc.SLALogConstant;
import my.ep.web.key.sc.supplierfinalization.MapKey;
import my.ep.web.managedbean.sc.supplierfinalization.SupplierFinalizationManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;
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
	/**
	 * Default serialization version ID
	 */
	private static final long serialVersionUID = 1L;

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
	private static final String JUSTIFICATION_OTHERS = "OTHERS";
	
	private ScQt scQt;
	
	
	
	/**
	 * Default constructor
	 */
	public SupplierFinalizationBackingBean() {
		log.info("default constructor for SupplierFinalizationBackingBean");
	}

	/**
	 * Initialize the data required when the page is initially rendered (based
	 * on the task-id of the selected quotation/tender) and sets the data in
	 * managed bean associated with the current user session
	 */
	public void preRenderView() {
		log.info("preRenderView");
		FacesContext fc = FacesContext.getCurrentInstance();
		/* Enhancement #21694 START*/
		IQTSuppFinalizationSO qtSuppFinalizationSO;
		SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
		SuppFinalizationResDTO resDTO = new SuppFinalizationResDTO();
		//28834 starts
		ScQtMeeting scQtMeeting= new ScQtMeeting();
		scQtMeeting.setMeetingStatus(getPmStatusByStatusId(scQtMeeting.getMeetingStatusStr()));
		//28834 ends
		qtSuppFinalizationSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSuppFinalizationSO.class);
		/* Enhancement #21694 END */
		try {
			
			SCEvaluationDataType dataType = getBpmTaskDetails(SCEvaluationDataType.class, null);
			getManagedBean().setPayLoad(dataType);
			
			
			Long qtId = Long.valueOf(dataType.getQtId());
			log.info("currentStatusId === "+getManagedBean().getCurrentStatusId());
			
			getCommitteeSO().initializedFCLegacyCommitteeHistory(qtId);// 25037
			
			Long loggedInUserId = getFacesScopes().getUserSessionBean()
									.getUserId();
			getManagedBean().setLoggedInUserId(loggedInUserId);			
			
			/*
			 * Enhancement #21694 START
			 * REVERSAL QT CASE
			 */			
			
			ScQtReverse scQtReverse = new ScQtReverse();
			reqDTO.setQtId(dataType.getQtId());			
			resDTO = qtSuppFinalizationSO.findActiveScQtReverse(reqDTO);
			
			getManagedBean().setScQt(qtSuppFinalizationSO.retrieveQTDtls(reqDTO).getScQt());
			if(null!=resDTO && null!=resDTO.getScQtReverse()){
				scQtReverse = resDTO.getScQtReverse();
				getManagedBean().setScQtReverse(resDTO.getScQtReverse());
				reverseAction(scQtReverse);
				getManagedBean().setActiveIndex(5);
				getManagedBean().setReversed(true);
			}else{
				getManagedBean().setActiveIndex(0);
				getManagedBean().setScQtReverse(null);
				getManagedBean().setReversed(false);
			}
			/*
			 * Enhancement #21694 END
			 */
			
			loadInitialData(qtId);
			// #18685 - Start.
			getManagedBean().setDocNo(getManagedBean().getScQt().getQtNo());
			// #18685 - End.
			 // #27502 -- Start
			QTDetailBackingBean qtBackingBean =   new QTDetailBackingBean();
			qtBackingBean.retrieveQuationTenderDetails(qtId);
			ScQt scQt = qtBackingBean.getManagedBean().getQt();
			boolean isBpkCentralContract = false;
			isBpkCentralContract = isBpkCentralContract(scQt);
			
			//#31632
			getManagedBean().setMofCntralCtc(isBpkCentralContract);
			
			if (!isBpkCentralContract) {
				prepareUpdateCommitteeMember();// 25037
			}
			// #27502 -- End
			//28834 start
			if (getManagedBean().getQtRefinalized() && ArrayUtils.isNotEmpty(this.getManagedBean().getOutComes()) 
					&& this.getManagedBean().getOutComes().length > 0) {
				boolean reviseExists = false;
				for (String outcome : this.getManagedBean().getOutComes()) {
					if (outcome.equals(BpmOutcomeConst.BPM_REVISEBP)) {
						reviseExists = true;
					}
				}
				
				if (reviseExists) {
					String tempOutComes[] = new String[this.getManagedBean().getOutComes().length -1];
					for (int i=0, k=0; i < this.getManagedBean().getOutComes().length; i++) {
						if (this.getManagedBean().getOutComes()[i].equals(BpmOutcomeConst.BPM_REVISEBP)) {
							continue;
						}
						tempOutComes[k++] = this.getManagedBean().getOutComes()[i];
					}
					this.getManagedBean().setOutComes(tempOutComes);
					
				}
				
			}
	//28834 end
			
	//newly added 28834 start
			List<ScQtMeeting> meetingListList = getManagedBean().getMeetingList();
			int pendingCount = 0;
			//START - BUG:#30546,CR:#28834
			//Include checking null due to NullPointer exception for scenario forward to Higher Authority to BPK
			if(meetingListList != null){
				for (ScQtMeeting meeting : meetingListList) {
					if (null != meeting.getMeetingStatus() && null != meeting.getMeetingStatus().getStatusId()
							&& !QTStatusConst.MS_DONE.equals(meeting.getMeetingStatus().getStatusId())
							&& meeting.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
						pendingCount++;
					}
				}
			}//END - BUG:#30546,CR:#28834

			if (pendingCount > 0) {

				this.getManagedBean().setDisableBfButton(false);
			} else {
				// #28834 - Start.
				if (!getManagedBean().isBpkCentralContract()) {
					this.getManagedBean().setDisableBfButton(true);
				}
				// #28834 - End.
			}
			this.evaluateDecisionList();//6906 note 117
			setDisableSFJustification();
			//28834 end
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
	
	/**
	 * Enhancement #21694
	 * to be used in Screen From Tasklist Only
	 * @param ScQtReverse
	 * @param previousPageUrl
	 * @return
	 */
	public String reverseAction(ScQtReverse scQtReverse) {
		
		debugLogger("viewReverseStatusDetails");
		FacesContext fc = FacesContext.getCurrentInstance();
		QTDetailBackingBean qtDtl = new QTDetailBackingBean();
		ScLoiForQt scLoiForQt = new ScLoiForQt();
		ScLoaForQt scLoaForQt = new ScLoaForQt();
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		AwardSupplierResDTO resDTO = new AwardSupplierResDTO();
		
		if(scQtReverse.getDocType().equalsIgnoreCase("LI")){	
			reqDTO = new AwardSupplierReqDTO();
			reqDTO.setLoiId(scQtReverse.getDocId());
			resDTO = getQtAwardSO().retrieveLoiForQtByLoiId(reqDTO);
			scLoiForQt = resDTO.getScLoiForQt();
		}else{
			reqDTO = new AwardSupplierReqDTO();
			reqDTO.setLoaId(scQtReverse.getDocId());
			resDTO = getQtAwardSO().retrieveLoaForQtByLoaId(reqDTO);
			scLoaForQt = resDTO.getScLoaForQt();		
		}
		
		IScWorkFlowStatusSO workFlowStatusSO;
		ScWorkFlowStatusReqDTO scWreqDTO = new ScWorkFlowStatusReqDTO();
		workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);		
		qtDtl.getManagedBean().setEditable(Boolean.TRUE);		
			
		
		/* FINDING WORKFLOW STATS*/
		 
		scWreqDTO.setDocType(scQtReverse.getDocType().equalsIgnoreCase("LI")?ParameterConst.DOCTYPE_LETTER_OF_INTENT:ParameterConst.DOCTYPE_LETTER_OF_AWARD);
		scWreqDTO.setDocId(scQtReverse.getDocId());					
		ScWorkFlowStatus currentWorkFlowStatus = workFlowStatusSO.retrieveStatusByTypeAndId(scWreqDTO).getWorkFlowStatus();
		qtDtl.getManagedBean().setViewFinalization(true);
		qtDtl.getManagedBean().setEditable(false);
		qtDtl.getManagedBean().setPreviousPage(this.getManagedBean().getPreviousPageUrl());
		qtDtl.getManagedBean().setQt(getManagedBean().getScQt());
		qtDtl.getManagedBean().setScLoaForQt(scLoaForQt);
		qtDtl.getManagedBean().setScLoiForQt(scLoiForQt);
		qtDtl.getManagedBean().setStatus(getStatusDescById(currentWorkFlowStatus.getPmStatus().getStatusId()));
		if(scQtReverse.getDocType().equalsIgnoreCase("LI")){
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, qtDtl.reverseQtDetails(Boolean.TRUE,Boolean.FALSE,qtDtl));
		}else{
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, qtDtl.reverseQtDetails(Boolean.FALSE,Boolean.TRUE,qtDtl));
		}
		
		return null;
	}

	/**
	 * To fetch initial data
	 * @param qtId
	 */
	private void loadInitialData(Long qtId) {
		log.info("loadInitialData");
		SCEvaluationDataType dataType = (SCEvaluationDataType) 
				this.getManagedBean().getPayLoad();
		getManagedBean().setCommitteeType(dataType.getCommitteeType());		
		
		retrieveQTDetails(qtId);

		// #27502 -- Adding condition only
		if (dataType.getFcDecision() != null && ParameterConst.DECISION_FORWARD_TO_HIGHER_COMMITTEES
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
		//#17376-start
		if(isQTMaintenanceScreen()){
			if(null==dataType.getCommitteeType() || StringUtils.isEmpty(dataType.getCommitteeType()) || StringUtils.isBlank(dataType.getCommitteeType())){ //17376-165
				// #30451-Start.
				if (null != this.getManagedBean().getScQt().getFinalizationCommitte()) {
					dataType.setCommitteeType(this.getManagedBean().getScQt().getFinalizationCommitte().getParameterCode());
					this.getManagedBean().setCommitteeType(this.getManagedBean().getScQt().getFinalizationCommitte().getParameterCode());
				}
				// #30451-End.
				loadPreparationTabDetails();
				
			} //17376-165
			if(getManagedBean().isInitialDecisionFwdToBPK()){
			getManagedBean().setInitialDecisionFwdToBPK(Boolean.FALSE);
			getManagedBean().setCommitteeType(dataType.getCommitteeType());
			loadPreparationTabDetails();
			}
		} //#17376-End
		
		int tabIndex = 0;
		
		// #27502 -- Start
		QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
		qtBackingBean.retrieveQuationTenderDetails(qtId);
		ScQt scQt = qtBackingBean.getManagedBean().getQt();	
		boolean isBpkCentralContract = false;
		isBpkCentralContract = isBpkCentralContract(scQt);
		// #27502 -- END
		
		if (getManagedBean().isInitialDecisionFwdToBPK()) {
			//Bug #36187 - start
			if (null == getManagedBean().getScQtFinalization().getLoiLoaBy()){
				getManagedBean().getScQtFinalization()
						.setLoiLoaBy(ParameterConst.LOILOA_PREPARED_BY_DO);
			}
			//Bug #36187 - end
			getManagedBean().setPreparationTabIndex(-1);
			getManagedBean().setEvaluationReportIndex(tabIndex++);
			getManagedBean().setReportTabIndex(tabIndex++);
			getManagedBean().setFinalizationTabIndex(tabIndex++);
			getManagedBean().setEndorsementReportActiveIndex(-1);
			getManagedBean().setCommEndorsementTabIndex(-1);
			getManagedBean().setEndorsementReportActiveIndex(-1);	
			
			// #27502 -- Start
		} else if (isBpkCentralContract) {
			getManagedBean().setPreparationTabIndex(-1);
			getManagedBean().setEvaluationReportIndex(-1);
			getManagedBean().setReportTabIndex(tabIndex++);
			getManagedBean().setFinalizationTabIndex(tabIndex++);
			getManagedBean().setCommEndorsementTabIndex(-1);		
			getManagedBean().setEndorsementReportActiveIndex(tabIndex++);
			getManagedBean().setReverseTabIndex(-1);
			getManagedBean().setSecretaryUser(true);	
			getManagedBean().setMofFinalization(true);
			//haziq - button download didn't show after upload - start
			//ScQtFinalizationReport endorsementReport = new ScQtFinalizationReport();
			//getManagedBean().setEndorsementReport(endorsementReport); 
			//haziq - button download didn't show after upload - end
			// #27502 -- END
		} else {
			getManagedBean().setPreparationTabIndex(tabIndex++);
			getManagedBean().setEvaluationReportIndex(tabIndex++);
			getManagedBean().setReportTabIndex(tabIndex++);
			getManagedBean().setFinalizationTabIndex(tabIndex++);
			getManagedBean().setCommEndorsementTabIndex(tabIndex++);	
			
			/*
			 * Enhancement #21694
			 */
			if(null!=getManagedBean().getScQtReverse()){
				getManagedBean().setEndorsementReportActiveIndex(tabIndex++);
				getManagedBean().setReverseTabIndex(tabIndex);
			}else{
				getManagedBean().setEndorsementReportActiveIndex(tabIndex);
				getManagedBean().setReverseTabIndex(-1);
			}
		}
		
		// Dynamically creating main tab menu
		createMainMenuModel(scQt); //#27502
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
		log.info("retrieveQTDetails");
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
			// #27502 -- Adding condition only
			if (scQt.getFinalizationSecratery() != null) {
				getManagedBean().setOriginalFinalizationSecretaryUserId(scQt.getFinalizationSecratery().getUserId());

				boolean isMedicalProcCategory = isMultipleProposalCategory(scQt, null);
				if (isMedicalProcCategory) {
					this.getManagedBean().getScQt().setEvaluateBy(SourcingConstants.EVALUATE_BY__ITEM);
				}
				// getManagedBean().setProcCategoryMedicine(isMedicalProcCategory);

				
			}
			List<TplSpecZoneDtl> zoneDetails = resDTO.getZoneDetails();//31401&27502-start
			getManagedBean().setZoneDetails(zoneDetails);//31401&27502-End
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	/**
	 * To navigate to the selected tab
	 */
	public void onTabMenuChangeAction() {
		log.info("onTabMenuChangeAction");
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
				if (getManagedBean().isInitialDecisionFwdToBPK()) { //#28240-start
					navigateToEvaluationReportTab();	
				}else{
				navigateToPreparationTab();
				}//#28240-End
				break;
			case 1:
				if (getManagedBean().isInitialDecisionFwdToBPK()) {//#28240-start
					navigateToBriefingPaperTab();	
				}else{
				navigateToEvaluationReportTab();
				}//#28240-End
				break;
			case 2:
				if (getManagedBean().isInitialDecisionFwdToBPK()) {//#28240-start
					navigateToSupplierFinalizationTab();	
				}else{
				navigateToBriefingPaperTab();
				}//#28240-End
				break;
			case 3:
				navigateToSupplierFinalizationTab();
				break;
			case 4:
				navigateToCommitteeEndorsementStatusTab();
				break;
			case 5:
				navigateToEndorsementReportTab();	
				break;
			case 6:
				//Enhancement #21694
				navigateToViewReverseTab();
				break;
		}
	}

	private void navigateToPreparationTab() {
		log.info("navigateToPreparationTab");
		this.getManagedBean().setActiveIndex(
				this.getManagedBean().getPreparationTabIndex());
		this.getManagedBean().setPrepareActiveIndex(
				PREPARE_ACTIVE_INDEX_MEETING);
	}

	// 25037 - start
	/**
	 * Populate informations for preparation tab.
	 */
	public void loadPreparationTabDetails() {
	// 25037 - end
		log.info("loadPreparationTabDetails");
		Long qtId = getManagedBean().getScQt().getQtId();
		String committeeType = getManagedBean().getCommitteeType();
		if (StringUtils.isNotBlank(committeeType)) {
			retrievePreparationDtls(qtId, committeeType);
		}
	}

	private void navigateToBriefingPaperTab() {
		log.info("navigateToBriefingPaperTab");
		this.getManagedBean().setActiveIndex(
				this.getManagedBean().getReportTabIndex());
	}
	
	private void navigateToEvaluationReportTab() {
		log.info("navigateToBriefingPaperTab");
		this.getManagedBean().setActiveIndex(
				this.getManagedBean().getEvaluationReportIndex());
	}

	private void navigateToSupplierFinalizationTab() {
		log.info("navigateToSupplierFinalizationTab");
		this.getManagedBean().setActiveIndex(
				this.getManagedBean().getFinalizationTabIndex());
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		String decisionTypeCode = scQtFinalization.getDecision()
												  .getParameterCode();
		//6906 note 117 - start
		if (decisionTypeCode!=null && (decisionTypeCode.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER) ||
				  decisionTypeCode.equals(ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY))) {
		//6906 note 117 - end
			retrieveSupplierDetailsByRecommendType(scQtFinalization);
			hasTierPriceInMixtureOfItems(scQtFinalization);
		}
	}

	private void loadSupplierFinalizationTabDetails() {
		log.info("loadSupplierFinalizationTabDetails");
		ScQt scQt = getManagedBean().getScQt();
		retrieveSupplierFinalizationDtls(scQt);
		ScQtFinalization scQtFinalization = 
					this.getManagedBean().getScQtFinalization();
		if (scQt.isZonal()) {
			createZoneMenuModel();
			populateScQtRecommendsByZone();
		} else {
			//this.getManagedBean().setItemList(
			//		scQtFinalization.getScQtRecommends());
			//#32400 - Start
			this.getManagedBean().setItemList(
					sortingItemizedFollowedOrderByQtId(
							scQtFinalization.getScQtRecommends(),scQt));
			//#32400 - End
		}
		this.getManagedBean().setJustification(
				scQtFinalization.getJustification());
		decisionEvent(null);
		//#26169 #38 - START Added by Haidah
		validateFCByDataTypeRule();
		//checkSFHasQualifiedSupplier();
		//#26169 #38 - END
		
		
		populateRecommendTypeEventDetails(false);
		fwdHigherAuthorityEvent();
		finalizationMethodEvent(null);
		initializeCommitteeRemarks();
		initializeFinalizationRemarks();
		initializeFinalizationEndorsementRemarks();
		initializeFinalizationPengreysonRemarks();
		
		// #18685 - Start
		populatePendingRecRemarks(getManagedBean().getScQtFinalization());
		// #18685 - End
		
		if(this.getManagedBean().isSecretaryUser()){
			log.info("ReevaluationSecretaryUser");
		
		String decision = null;
		if (scQtFinalization.getDecision() != null) {
			decision = scQtFinalization.getDecision().getParameterCode();
		}
		if(null!=decision && ParameterConst.DECISION_RE_EVALUATION.equals(decision)){
			
				if(null==this.getManagedBean().getSelectionMode() || " ".equals(this.getManagedBean().getSelectionMode())){
					log.info("ReevaluationSelectionModeZero");
					this.getManagedBean().setSelectionMode(QTConstants.SF_SINGLE);
				}
			//}
		}
		}
		
		
		if (this.getManagedBean().getResetSupplierList() == null) {
			this.getManagedBean().setResetSupplierList(
					new HashSet<ScQtEvaluationResultDtl>());
		}	
		// #25638 - Start
		displayReviseInstrn(getManagedBean().getCommitteeList());
		// #25638 - End		
		
	}
	
	// #18685 - Start
	/**
	 * @param scQtFinalization
	 */
	private void populatePendingRecRemarks(ScQtFinalization scQtFinalization) {
		if (null != scQtFinalization && CollectionUtils.isNotEmpty(scQtFinalization.getScQtFinalizationRemarks())) {
			Iterator<ScQtFinalizationRemarks> iterator = scQtFinalization.getScQtFinalizationRemarks().iterator();
			while (iterator.hasNext()) {
				ScQtFinalizationRemarks remark = iterator.next();
				if (CommonConstants.ITEMIZED.equals(remark.getSourceName())) {
					// #18685 - Start
					if (null == getManagedBean().getPendingRecItemizedRemark()) {
						List<ScRemark> itemizedRemarks = new ArrayList<>();
						getManagedBean().setPendingRecItemizedRemark(itemizedRemarks);
					}
					getManagedBean().getPendingRecItemizedRemark().add(remark);
					// #18685 - End
				} else if (CommonConstants.PACKAGE_WISE.equals(remark.getSourceName())) {
					// #18685 - Start
					if (null == getManagedBean().getPendingRecPackageRemark()) {
						List<ScRemark> packageRemarks = new ArrayList<>();
						getManagedBean().setPendingRecPackageRemark(packageRemarks);
					}
					getManagedBean().getPendingRecPackageRemark().add(remark);
					// #18685 - End
				} else {
				  // #25638 - Start.
				  if (getManagedBean().getNewSfRemarks() == null) {
				    List<ScRemark> newSfRemarkList = new LinkedList<>();
				    getManagedBean().setNewSfRemarks(newSfRemarkList);
				  }
				    getManagedBean().getNewSfRemarks().add(remark);
				  // #25638 - End.
				}
			}
		}
	}
	// #18685 - End
	
	private void loadRecommededSuppliersIntoCachedData(boolean isItemized) {
		log.info("loadRecommededSuppliersIntoCachedData");
		List<ScQtRecommend> scQtRecommends = getManagedBean().getScQtFinalization().getScQtRecommends();
		if (CollectionUtils.isNotEmpty(scQtRecommends)) {
			Set<String> zoneAndItemIds = new HashSet<String>();
			for (ScQtRecommend recommend : scQtRecommends) {
				if (!recommend.isCancelled() && 
						CollectionUtils.isNotEmpty(recommend.getRecommendDtls())) {
					Long zoneId = recommend.getTplSpecZoneDtl() != null ? recommend.getTplSpecZoneDtl().getSpecZoneId() : DEFAULT_ZONE;
					Long itemId = recommend.getRequestItem() != null ? recommend.getRequestItem().getRequestNoteItemId() : null;
					itemId = isItemized ? itemId : DEFAULT_ITEM;
					String zoneAndItemId = zoneId + "|" + itemId;
					
					//33380 - start
					Map<MapKey, ScQtRecommendDtl> map = new HashMap<MapKey, ScQtRecommendDtl>();
					boolean multipleLOIandRefinalized = ParameterConst.RECOMMEND_BY_MULTIPLE_LOI.equals(getManagedBean().getFinalizationMethod()) 
							&& this.getManagedBean().getQtRefinalized(); 
					if(multipleLOIandRefinalized){
						if(CollectionUtils.isNotEmpty(recommend.getRecommendDtls())){
							for (ScQtRecommendDtl recommendDtl: recommend.getRecommendDtls()) {
								boolean isLoiAwarded = recommendDtl.getLoiAwarded() == null ? false: recommendDtl.getLoiAwarded();
								if(recommendDtl.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE) && isLoiAwarded){
									MapKey mapkey = new MapKey(recommendDtl.getSupplier().getSupplierId(), zoneId, recommend.getRequestItem().getRequestNoteItemId());
									map.put(mapkey, recommendDtl);
								}
							}
						}
					}
					//33380 - end
					// only update cache data for same item and zone (cater for normal and zonal package to run once).
					if (!zoneAndItemIds.contains(zoneAndItemId)) {
						List<ScQtEvaluationResultDtl> recommendedSuppliers = retrieveRecommendedSuppliers(
								zoneId, itemId);
						for (ScQtEvaluationResultDtl recommendedSupplier : recommendedSuppliers) {
							// update isAwarded flag if not shortlisted. 
							//33380 - start
							Boolean isAwarded = null;
							if(multipleLOIandRefinalized && recommend.isAwardedItem()){
								Long suppId = recommendedSupplier.getQtEvaluationResult().getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierId();
								MapKey mapKey = new MapKey(suppId, zoneId, recommend.getRequestItem().getRequestNoteItemId());
								isAwarded =map.get(mapKey)!=null;
							}else{
								isAwarded = !recommendedSupplier.isShortListed() ? true : null;
							}
							//33380 - end
							updateCachedData(recommendedSupplier, 
									zoneId, 
									itemId, 
									isAwarded);//33380
						}
						
						zoneAndItemIds.add(zoneAndItemId);
					}
				}
			}
		}
		
	}
	
	/**
	 * Initialize remarks.
	 */
	public void initializeCommitteeRemarks() { // 25037
		log.info("initializeCommitteeRemarks");
		ScQtCommittee scQtCommittee = this.getManagedBean().getScQtCommittee();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		if (scQtCommittee != null) {
			// Retrieve committee remarks
			List<ScQtCommitteRemarks> remarks = initializeRemarks(scQtCommittee,
					scQtFinalization.getCurrentStatus(), 
					scQtCommittee.getQtCommitteRemarks(),
					ScQtCommitteRemarks.class);
			
			// 25037 - start
			if (CollectionUtils.isNotEmpty(remarks)) {
				for (ScQtCommitteRemarks r : remarks) {
					r.setEditable(false);
				}
				remarks.get(0).setEditable(true);
				Collections.sort(remarks, new Comparator<ScQtCommitteRemarks>() {
					@Override
					public int compare(ScQtCommitteRemarks o1, ScQtCommitteRemarks o2) {
						if (o1.getCreatedDate() == null || o2.getCreatedDate() == null)
							return 0;
						return o2.getCreatedDate().compareTo(o1.getCreatedDate());
					}
				});
			}
			// 25037 - end
			scQtCommittee.setQtCommitteRemarks(remarks);
	
			// Committee Remarks list
			this.getManagedBean().setCommitteeRemarkList(remarks);
		}		
	}
	
	private void initializeFinalizationRemarks() {
		log.info("initializeFinalizationRemarks");
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
		log.info("initializeFinalizationEndorsementRemarks");
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		List<ScQtFinalizationEndorsementRemarks> remarks=null;
		// Retrieve finalization endorsement remarks
		if(this.getManagedBean().isSecretaryUser()){
		 remarks = initializeRemarks(scQtFinalization,
				scQtFinalization.getCurrentStatus(), 
				scQtFinalization.getScQtFinalizationEndorsementRemarks(), 
				ScQtFinalizationEndorsementRemarks.class);
		scQtFinalization.setScQtFinalizationEndorsementRemarks(remarks);
	}else{
		remarks=scQtFinalization.getScQtFinalizationEndorsementRemarks();
	}

		// Finalization Endorsement Remarks list
		getManagedBean().setFinalizationEndorsementRemarks(remarks);		
	}		

	private void initializeFinalizationPengreysonRemarks() {
		log.info("initializeFinalizationPengreysonRemarks");
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		List<ScQtFinalizationEndorsementRecommendationRemarks> remarks=null;
		
		// #27502 - Start
		QTDetailBackingBean qtBackingBean =   new QTDetailBackingBean();
		qtBackingBean.retrieveQuationTenderDetails(this.getManagedBean().getScQt().getQtId());
		ScQt scQt = qtBackingBean.getManagedBean().getQt();
		boolean isBpkCentralContract = false;
		isBpkCentralContract = isBpkCentralContract(scQt);
				
		// Retrieve finalization endorsement remarks
		if(isBpkCentralContract || this.getManagedBean().isSecretaryUser()){
		 remarks = initializeRemarks(scQtFinalization,
				scQtFinalization.getCurrentStatus(), 
				scQtFinalization.getScQtFinalizationEndorsmentRecommendationRemarks(), 
				ScQtFinalizationEndorsementRecommendationRemarks.class);
		scQtFinalization.setScQtFinalizationEndorsmentRecommendationRemarks(remarks);
		}else{
			remarks=scQtFinalization.getScQtFinalizationEndorsmentRecommendationRemarks();
		}

		// Finalization Pengesyoran  Remarks list
		getManagedBean().setFinalizationPengreysonremarks(remarks);		
	}
	
	
	private void navigateToCommitteeEndorsementStatusTab() {
		log.info("navigateToCommitteeEndorsementStatusTab");
		this.getManagedBean().setActiveIndex(
				this.getManagedBean().getCommEndorsementTabIndex());
		retrieveEndorsementCommitteeDetails();
	}
	
   /**
    * Get the Endorsement tab data.
    */
	public void navigateToEndorsementReportTab() {
		log.info("navigateToEndorsementReportTab");
		this.getManagedBean().setActiveIndex(
				this.getManagedBean().getEndorsementReportActiveIndex());
		
		PmReason justificationParam = this.getManagedBean().getJustification();
		if (justificationParam == null) {
			this.getManagedBean().setJustification(new PmReason());
		} else {
			if (justificationParam.getReasonId() != null
					&& JUSTIFICATION_OTHERS.equalsIgnoreCase(justificationParam.getReasonCode())) {
				this.getManagedBean().setRenderjustificationTextbox(true);
			}
		}
		
		// #28834 - Start.
		setDisableSFJustification();
		// #28834 - End.
		
		try {
			this.getManagedBean()
					.setJustDropdown(
							getReasonCodeDropdownValues(ReasonCategory.SUPPLIER_SELECTION_JUSTIFICATION
									.value()));
			//getSuppFinalizationEndorsementReportStreamContent();
		} catch (Exception e) {
			log.error(e);
		}
	}	
	
	// #28834 - Start.
	private void setDisableSFJustification() {
		String decision = getManagedBean().getScQtFinalization().getDecision().getParameterCode();
		if (StringUtils.isNotEmpty(decision) && (decision.equals(ParameterConst.DECISION_RE_EVALUATION)
				|| decision.equals(ParameterConst.DECISION_RE_PUBLISH)
				|| decision.equals(ParameterConst.DECISION_CANCEL)
				|| decision.equals(ParameterConst.DECISION_RBP))) {
			getManagedBean().setDisableJustifications(true);
			if (null != getManagedBean().getJustification()) {
				getManagedBean().getJustification().setReasonCode(null);
			}
		} else {
			getManagedBean().setDisableJustifications(false);
		}
	}
	// #28834 - End.

	/**
	 * Enhancement #21694
    * Get the Reversal tab data.
    */
	public void navigateToViewReverseTab() {
		log.info("navigateToViewReverseTab");
		this.getManagedBean().setActiveIndex(
				this.getManagedBean().getReverseTabIndex());
		
	}

	private void retrievePreparationDtls(Long qtId, String committeeType) {
		log.info("..In retrievePreparationDtls()..");
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
						SimpleDateFormat hourFormat=new SimpleDateFormat(CommonConstants.DEFAULT_HOUR_FORMAT);//32613-start
						SimpleDateFormat minuteFormat=new SimpleDateFormat(CommonConstants.DEFAULT_MINUTE_FORMAT);
						SimpleDateFormat Date=new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
						scQtMeeting.setMeetDateStr(getDateValues(hourFormat.format(scQtMeeting.getMeetingDate())));//32613-start
						scQtMeeting.setMeetTimeStr(getDateValues(minuteFormat.format(scQtMeeting.getMeetingDate())));//32613-End
						scQtMeeting.setMetingDate(scQtMeeting.getMeetingDate());//32613-End
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
					// 25037 - start
					if (CollectionUtils.isNotEmpty(scQtCommittee.getCommitteeActivities())) {
						Collections.sort(scQtCommittee.getCommitteeActivities());

						this.getManagedBean().setCommitteeList(scQtCommittee.getCommitteeActivities());

						Map<Object, Object> map = CustomUtils.listToMap(this.getManagedBean().getCommitteeList(),
								"getScCommitteeMember.getCommitteeMemberId");
						List<ScQtCommitteeActivity> missingActivities = new ArrayList<>();
						for (ScCommitteeMember member : scQtCommittee.getScCommittee().getScCommitteeMembers()) {
							if (map.get(member.getCommitteeMemberId()) == null) {
								ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
								activity.setScQtCommittee(scQtCommittee);
								activity.setScCommitteeMember(member);
								setUserDetails(activity);
								missingActivities.add(activity);
							}
						}
						this.getManagedBean().getCommitteeList().addAll(missingActivities);
					// 25037 - end
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
						//25201#63-2
						//if(isQTMaintenanceScreen()){
							//log.info("pending supplier finalization : "+(getManagedBean().getCurrentStatusId()==
							//		QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION));
							//if(getManagedBean().getCurrentStatusId()!=null && getManagedBean().getCurrentStatusId().longValue()!=QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION.longValue()){
								//for(ScQtCommitteeActivity commActivity: this.getManagedBean().getCommitteeEndorsementList()){
									//log.info("commActivity.isSignedOff() : "+commActivity.isSignedOff());
									//commActivity.setSignedOff(true); //
								//}
							//}
						//}
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
				//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
				if (scQtCommittee != null && CollectionUtils.isNotEmpty(scQtCommittee
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
		log.info("retrieveEndorsementCommitteeDetails");
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
		log.info("deleteMeetingDetails");

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
		log.info("deleteMeetingAttendanceDetails");
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
	//28834 start

	public void changeMeetingStatus(ScQtMeeting scQtMeeting) {

		// boolean done = true;
		scQtMeeting.setMeetingStatus(getPmStatusByStatusId(scQtMeeting.getMeetingStatusStr()));
		List<ScQtMeeting> meetingListList = getManagedBean().getMeetingList();
		int pendingCount = 0;
		for (ScQtMeeting meeting : meetingListList) {
			if (null != meeting.getMeetingStatus() && null != meeting.getMeetingStatus().getStatusId()
					&& !QTStatusConst.MS_DONE.equals(meeting.getMeetingStatus().getStatusId())
					&& meeting.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
				pendingCount++;
			}
		}

		if (pendingCount > 0) {

			this.getManagedBean().setDisableBfButton(false);
		} else {

			this.getManagedBean().setDisableBfButton(true);
		}
	}

	//28834 end

	/**
	 * To retrieve committee details
	 */
	public void retrieveCommitteeDtls() {
		log.info("..In retrieveCommitteeDtls()..");
		this.getManagedBean().setPrepareActiveIndex(
				PREPARE_ACTIVE_INDEX_COMMITTEE);
	}

	/**
	 * To retrieve meeting attendance details corresponding to the selected
	 * quotation/tender
	 */
	public void retrieveMeetingAttendanceDtls() {
		log.info("..In retrieveMeetingAttendanceDtls()..");
		this.getManagedBean().setPrepareActiveIndex(
				PREPARE_ACTIVE_INDEX_JOIN_IN);
	}

	/**
	 * Event to handle addition of committee meeting attendance records
	 */
	public void addMeetingAttendance() {
		log.info("..In addMeetingAttendance()..");

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
		log.info("selectAllMeetingAttendeesEvent");
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
		log.info("selectMeetingAttendeeEvent");
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
		log.info("retrieveQualifiedSuppliersInfoByEvalStage");
		String evalStageId = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("evaluationStageId");
		int evaluationStageId = 0;
		//35087 start
		IQTEvaluationSO evaluationSO=null;
		evaluationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION,
				IQTEvaluationSO.class);
		EvaluationReqDTO req4SerDTO = new EvaluationReqDTO();
		//35087 end
		if (evalStageId != null) {
			evaluationStageId = Integer.parseInt(evalStageId);
		}
		getManagedBean().setEvaluationStageId(evaluationStageId);
		
		Long qtId = getManagedBean().getScQt().getQtId();
		Long selectedZoneId = null;
		Long selectedItemId = null;
		String evaluationStage = null;
		String evaluationHeader = null;
		//35087
		req4SerDTO.setQtId(qtId);
		//35087
		switch (evaluationStageId) {
			case 1 : if (getManagedBean().getScQt().isDemoRequired()) {
						 evaluationStage = SourcingConstants.EVAL_STAGE_PRESENTATION_DEMO;
						 evaluationHeader = "lbl.sc.eva.qualified.supplier.list.presentation.demo.stage";
					 } else {
						//#21695 tec by itemize result pop up farid - start
						 if (getManagedBean().isItemize()) {
								//35087
								int wipstatus = evaluationSO.checkWipScoreSetting(req4SerDTO);

								if (wipstatus <= ParameterConst.FLAG_WIP_STATUS_3SERANGKAI_BEFORE) {
									evaluationStage = SourcingConstants.EVAL_STAGE_TECHNICAL;
									evaluationHeader = SourcingConstants.EVAL_QUALIFIED_SUP_LIST_TEC_EVAL_STAGE;
								} else {
									evaluationStage = SourcingConstants.EVAL_STAGE_TECHNICAL_ITEM;
									evaluationHeader = SourcingConstants.EVAL_QUALIFIED_SUP_LIST_TEC_EVAL_STAGE;
									if (getManagedBean().isItemRowSelect() && getManagedBean().getSelectedItem() != null) {
										selectedItemId = getManagedBean().getSelectedItem().getRequestItem().getRequestNoteItemId();
									} else {
										getManagedBean().setQualifiedSuppliersByEvalStage(null);
										log.debug("None of the item(s) has been row selected");
										return;
									}
								}
								//35087
							}else{
							 evaluationStage = SourcingConstants.EVAL_STAGE_TECHNICAL;
							 evaluationHeader = SourcingConstants.EVAL_QUALIFIED_SUP_LIST_TEC_EVAL_STAGE;
						 }
					 }
					 break;
					//#21695 tec by itemize result pop up farid - end
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
					 evaluationHeader = "lbl.sc.eva.qualified.supplier.list.fec.evaluation.stage";
			 		 break;
		}
		getManagedBean().setEvaluationHeader(evaluationHeader);
		//#21695 tec by itemize result pop up farid - start
		if (getManagedBean().getCurrentZone() != null) { //#26440
		//#21695 tec by itemize result pop up farid - end
			 selectedZoneId = getManagedBean().getCurrentZone().getSpecZoneId();
		 }
		this.getManagedBean().setCheckCategoryCodeQt(this.getManagedBean().getScQt());//#29466
		getManagedBean().setQualifiedSuppliersByEvalStage(
				new QualifiedSuppliersPagingDataModel(
						"evaluationQualifiedSuppliers", qtId,
						selectedZoneId, selectedItemId, evaluationStage));
	}
	
	/**
	 * Validate, save supplier finalization details and sets the updated data in
	 * managed bean associated with the current user session
	 */
	// 25037 - start
	public void saveSupplierFinalization() {
		saveSupplierFinalization(false);
	}

	/**
	 * Validate, save supplier finalization details and sets the updated data in
	 * managed bean associated with the current user session, skip validation if
	 * needed
	 *
	 * @param skipValidation
	 */
	public void saveSupplierFinalization(boolean skipValidation) {
	// 25037 - end
		log.info("..In saveSupplierFinalization()..");

		this.getManagedBean().setSubmit(false);
		this.getManagedBean().setAction(null);//6906 note 117

		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		int activeIndex = this.getManagedBean().getActiveIndex();
		Long qtId = getManagedBean().getScQt().getQtId();
		log.info("..In saveSupplierFinalization().. "+qtId);
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			ScQtFinalization scQtFinalization = this.getManagedBean().getScQtFinalization();
			List<ScQtFinalizationReport> finalizationReports = new ArrayList<ScQtFinalizationReport>();
			finalizationReports.addAll(this.getManagedBean().getDocumentList());
			finalizationReports.addAll(this.getManagedBean().getEndorsementReports());
			finalizationReports = new ArrayList<>(new HashSet<>(finalizationReports));//35381 - Related 6906
			scQtFinalization.setScQtFinalizationReports(finalizationReports);
			
			//6906 - note 117 - start
			String desicion = scQtFinalization.getDecision() ==null ? null: scQtFinalization.getDecision().getParameterCode();
			List<String> decisionList = new ArrayList<>();
			if(this.getManagedBean().getDecisionDropdown() != null && this.getManagedBean().getDecisionDropdown().length>0){
				for(SelectItem item: this.getManagedBean().getDecisionDropdown()){
					if (item.getValue() != null) {
						decisionList.add(item.getValue().toString());
					}
				}
			} 
			if (CollectionUtils.isEmpty(decisionList) || StringUtils.isEmpty(desicion) || !decisionList.contains(desicion)) {
				if(this.getManagedBean().getActiveIndex() == this.getManagedBean().getFinalizationTabIndex()){
					throw new EpException(MessageConst.ERROR_100022,
							new String[] { ResourceBundleUtils.getMessage("lbl.sc.sf.meeting.decision", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}else{
					scQtFinalization.getDecision().setParameterCode(ParameterConst.DECISION_RECOMMEND_SUPPLIER);
				}
			} 
			//6906 - note 117 - end
			SuppFinalizationReqDTO reqDTO = null;
			ScQtCommittee scQtCommittee = null;
			
			//#27502 -Start
			ScQt scQt = this.getManagedBean().getScQt();
			QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
			qtBackingBean.retrieveQuationTenderDetails(scQt.getQtId());
			scQt = qtBackingBean.getManagedBean().getQt();
			boolean isBpkCentralContract = false;
			isBpkCentralContract = isBpkCentralContract(scQt);
			if (isBpkCentralContract) {			
				reqDTO = validateInfo(2, skipValidation);
			} else {	//#27502 - END		
				// Save Preparation and Supplier finalization tab(s) data
				
				if (activeIndex == 0 && !getManagedBean().isInitialDecisionFwdToBPK()) {
					this.getManagedBean().setSubmit(Boolean.FALSE);//32613
					reqDTO = validateInfo(activeIndex, skipValidation); // 25037
					scQtCommittee = reqDTO.getScQtCommittee();
					this.getManagedBean().setSubmit(Boolean.FALSE);//32613
					reqDTO = validateInfo(2, skipValidation); // 25037 
				} else {
					if (!getManagedBean().isInitialDecisionFwdToBPK()) {
						scQtCommittee = preparationDtlsInfo();
					}
					reqDTO = validateInfo(2, skipValidation); // 25037
				}
			}
			
			//Bug #29707 - start
			// Move this code to below
			// #27502- Adding condition only
			/*if (!isBpkCentralContract) {
				// #18685 - Start
				resetUnSavedReportContent(this.getManagedBean().getNewBriefingPaperReportList());
				if (CollectionUtils.isNotEmpty(scQtFinalization.getScQtFinalizationReports())) {
					scQtFinalization.getScQtFinalizationReports().addAll(getManagedBean().getEvaluationReportList());
				}
				if (CollectionUtils.isNotEmpty(getManagedBean().getNewBriefingPaperReportList())
						&& CollectionUtils.isNotEmpty(getManagedBean().getConsolidationBriefingPaperReportList())) {
					getManagedBean().getNewBriefingPaperReportList().addAll(
							getManagedBean().getConsolidationBriefingPaperReportList());
				}
				reqDTO.setAddNewBriefingPaperReportList(getManagedBean().getNewBriefingPaperReportList());
				// #18685 - End
			}*/
			//Bug #29707 - end

			validateFinalizationReports(reqDTO, skipValidation);//25037 - note 31
			
			//validateEvaluationReports(SourcingConstants.SUPPLIER_FINALIZATION_TASK);
			//saveEvaluationReportsDetaills();
 			if (this.getManagedBean().getEndorsementReportActiveIndex() == activeIndex) {
				//#27364 Start
				String decisionTypeCode = scQtFinalization.getDecision().getParameterCode();
				if (ParameterConst.DECISION_CANCEL
						  .equals(decisionTypeCode) || ParameterConst.DECISION_RE_PUBLISH
						  .equals(decisionTypeCode) || ParameterConst.DECISION_RE_EVALUATION
						  .equals(decisionTypeCode)){
					this.getManagedBean().setFinalizationMethod(null);
				}
				//#27364 End
				if (this.getManagedBean().getFinalizationMethod() != null
						&& (this.getManagedBean().getFinalizationMethod()
								.equals(ParameterConst.RECOMMEND_BY_AWARD) || this
								.getManagedBean().getFinalizationMethod()
								.equals(ParameterConst.RECOMMEND_BY_SINGLE_AWARD))
								// #28834 - Start.
								&& !getManagedBean().isDisableJustifications()
								// #28834 - End.
								) {
					if ((this.getManagedBean().getJustification() == null
							|| this.getManagedBean().getJustification()
									.getReasonId() == null) && this.getManagedBean().isSubmit()) {//#35205 & #28834
						navigateToEndorsementReportTab();
						throw new EpException(MessageConst.ERROR_100061,
								new String[] { ResourceBundleUtils.getMessage(
										"lbl.sc.sf.justification.selection",
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale()) });
					}
					
					setNullSelectionForJustification(reqDTO);//#35205 & #28834
				}
			}
 			
 			setNullSelectionForJustification(reqDTO);//#35205 & #28834
						
			if (!getManagedBean().isInitialDecisionFwdToBPK()) {
				reqDTO.setDeletedMeetingsList(this.getManagedBean().getDeletedMeetingList());
				// #27502- Adding condition only
				if (scQtCommittee == null) {// 20230712@asyrafhaziq- SQUBE Bug NullPointer#3
					scQtCommittee = new ScQtCommittee();
					scQtCommittee.setQtCommitteRemarks(reqDTO.getScQtCommittee().getQtCommitteRemarks());
					reqDTO.setScQtCommittee(scQtCommittee);
				}
			}
			
			reqDTO.setInitialDecisionFwdHighAuth(
					getManagedBean().isInitialDecisionFwdHighAuth());
			reqDTO.setRefinalize(isQTRefinalized());
			//29707
			reqDTO.setScQt(scQt);
			//Bug #29707 - start
			// Moved code from top
			// #27502- Adding condition only
			if (!isBpkCentralContract) {
				// #18685 - Start
				resetUnSavedReportContent(this.getManagedBean().getNewBriefingPaperReportList());
				if (CollectionUtils.isNotEmpty(scQtFinalization.getScQtFinalizationReports())) {
					log.info("masuk 1");
					scQtFinalization.getScQtFinalizationReports().addAll(getManagedBean().getEvaluationReportList());
				}
				if (CollectionUtils.isNotEmpty(getManagedBean().getNewBriefingPaperReportList())
						&& CollectionUtils.isNotEmpty(getManagedBean().getConsolidationBriefingPaperReportList())) {
					log.info("masuk 2");
					getManagedBean().getNewBriefingPaperReportList().addAll(
							getManagedBean().getConsolidationBriefingPaperReportList());
				}
				reqDTO.setAddNewBriefingPaperReportList(getManagedBean().getNewBriefingPaperReportList());
				// #18685 - End
			}
			//Bug #29707 - end
			//#27502- Adding condition only			 
//			if (isBpkCentralContract) {					
//				if (CollectionUtils.isNotEmpty(reqDTO.getFinalizationItemizedRemarks())) {
//					if (CollectionUtils.isEmpty(scQtFinalization.getScQtFinalizationRemarks())) {
//						List<ScQtFinalizationRemarks> scQtFinalizationRemarks = new ArrayList<>();
//						scQtFinalization.setScQtFinalizationRemarks(scQtFinalizationRemarks);
//					}
//				}
//			}
			
			saveAndReloadFinalizationDetails(qtSuppFinalizationSO, reqDTO);
			prepareUpdateCommitteeMember();// 25037
			// #28834 - Start.
			checkDisableBfButtonAfterValidation();
			// #28834 - End.
			// 25037 - start
			if (!skipValidation) {
				String qtNo = getManagedBean().getScQt().getQtNo();
				String desc = ResourceBundleUtils.getMessage("lbl.sc.sf.supplier.finalization",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
								MessageConst.INFO_700004, new String[] { desc, qtNo },
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}
			// 25037 - end
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	//#35205 & #28834 - Start
	/**
	 * Allow save data for justification dropdown is empty to null value       
	 */
	public void setNullSelectionForJustification(SuppFinalizationReqDTO reqDTO){
		if(this.getManagedBean().getJustification() != null
				&& this.getManagedBean().getJustification().getReasonId() == null 
				&& !this.getManagedBean().isSubmit()){
			reqDTO.getScQtFinalization().setJustification(null);
		}
	}
	//#35205 & #28834 - End
	
	// 25037 - start - note 31
	private void validateFinalizationReports(SuppFinalizationReqDTO reqDTO, boolean skipValidation) {
	// 25037 - end - note 31
		log.info("validateFinalizationReports");
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		boolean isSubmit = this.getManagedBean().isSubmit();//#35205
		List<ScQtFinalizationReport> finalizationReports = 
					scQtFinalization.getScQtFinalizationReports();
		//Bug #29707
		//finalizationReports.addAll(this.getManagedBean()
		//		.getEndorsementReports());
		//35381 - Related 6906 - start
		finalizationReports = new ArrayList<>(new HashSet<>(finalizationReports));
		//35381 - Related 6906 - end
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
						finalizationReports.removeAll(this.getManagedBean().getEndorsementReports());
						// 25037 - start - note 31
						if (skipValidation) {
							return;
						}
						// 25037 - end - note 31
						navigateToEndorsementReportTab();
						// #31934 - Start.
						//throw new EpException(MessageConst.ERROR_760386);
						throw new EpException(MessageConst.ERROR_770271);
						// #31934 - End.
					}
				}
			}
			
			//#28834 - Start 
			if(CollectionUtils.isNotEmpty(toBeRemovedReports) && isSubmit){//#35205 add isSubmit
				if(skipValidation) return;//25037
				navigateToEndorsementReportTab();
			    throw new EpException(MessageConst.ERROR_770271);
			}
			//#28834 - End
			
			if (toBeRemovedReports.size() > 0) {
				finalizationReports.removeAll(
							toBeRemovedReports);
				reqDTO.setRemovedFinalizationReports(
								   toBeRemovedReports);
			}
		}
	}
	
	private void reloadData(SuppFinalizationResDTO resDTO) {
		log.info("reloadData");
		int activeIndex = this.getManagedBean().getActiveIndex();
		int prepareActiveIndex = this.getManagedBean().getPrepareActiveIndex();

		String taskId = this.getManagedBean().getTaskId();
		String taskName = this.getManagedBean().getTaskName();// 25037
		String[] outComes = this.getManagedBean().getOutComes();
		String processName = this.getManagedBean().getProcessName();
		Long currentStatusId = this.getManagedBean().getCurrentStatusId();
		
		renewSessionAndRetrieve(activeIndex, resDTO);
		// 25037 - start
		setTaskDetails(activeIndex, prepareActiveIndex, taskId, outComes, processName, currentStatusId, taskName);
		// 25037 - end
	}

	// 25037 - start
	private SuppFinalizationReqDTO validateInfo(int index) {
		return validateInfo(index, false);
	}

	private SuppFinalizationReqDTO validateInfo(int index, boolean skipValidation) {
	// 25037 - end
		log.info("..In validateInfo()..");

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
					// 25037 - start
					if (!skipValidation) {
						qtSuppFinalizationSO.validatePreparation(reqDTO);
					}
					// 25037 - end
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
						
					// 25037 - start
					if ((ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY.equals(decisionTypeCode) && (scQtFinalization
							.getReason() == null || scQtFinalization.getReason().getReasonId() == null))
							&& !skipValidation) {
					// 25037 - end
						navigateToSupplierFinalizationTab();
						throw new EpException(MessageConst.ERROR_760385);
					}
						// isBpkFinalizationReq is true for Restricted Tender whose sent for  BPK Approval
					if (!this.getManagedBean().isInitialDecisionFwdToBPK()
							&& this.getManagedBean().getScQt().isBpkFinalizationReq()
							&& !this.getManagedBean().isMofCntralCtc()) {//#31632
							
						if ((!ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY
								.equals(decisionTypeCode)
								|| scQtFinalization.getHigherCommitte() != null) && !skipValidation) {
							navigateToSupplierFinalizationTab();
							throw new EpException(MessageConst.ERROR_760459);
						}
						
						}
					
					//#29966
					if(this.getManagedBean().getAction() != null && this.getManagedBean().getAction().equals(BpmOutcomeConst.BPM_SUBMIT)){
						this.getManagedBean().setSubmit(true);
					}
					
					processSuppliersBeforeSaving();
					// 25037 - start
					if (!skipValidation) {
						if (!getManagedBean().isItemize()) {
							validateByPackage();
						} else {
							validateByItem();
						}
					}
					// 25037 - end

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
					List<ScQtFinalizationEndorsementRecommendationRemarks> finalizationEndorsementRecommendationRemarks = 
							finalizeRemarks(getManagedBean().getFinalizationPengreysonremarks());
					scQtFinalization.setScQtFinalizationEndorsementRemarks(
											 finalizationEndorsementRemarks);
					scQtFinalization.setScQtFinalizationEndorsmentRecommendationRemarks(finalizationEndorsementRecommendationRemarks);
					setUserDetails(scQtFinalization);
	
					reqDTO.setScQtCommittee(_scQtCommittee);
					reqDTO.setScQtFinalization(scQtFinalization);
					reqDTO.setRefinalize(isQTRefinalized());
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
		log.info("renewSessionAndRetrieve");
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
		boolean isRenderjustificationTextbox = this.getManagedBean().isRenderjustificationTextbox();
		
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
		getManagedBean().setRenderjustificationTextbox(isRenderjustificationTextbox);
		
		loadInitialData(qtId);
		// #18685 - Start.
		getManagedBean().setDocNo(getManagedBean().getScQt().getQtNo());
		// #18685 - End.
		//27502, navigate to endorsement tab after save
		QTDetailBackingBean qtBackingBean =   new QTDetailBackingBean();
		qtBackingBean.retrieveQuationTenderDetails(qtId);
		ScQt scQt = qtBackingBean.getManagedBean().getQt();
		boolean isBpkCentralContract = false;
		isBpkCentralContract = isBpkCentralContract(scQt);
		
		if (isBpkCentralContract) {
			this.getManagedBean().setActiveIndex(5);
		}
		
		onTabMenuChangeAction();
	}

	// 25037 - start
	private void setTaskDetails(Integer activeIndex, Integer prepareActiveIndex, String taskId, String[] outComes,
			String processName, Long currentStatusId, String taskName) {
	// 25037 - end
		log.info("setTaskDetails");
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
		this.getManagedBean().setTaskName(taskName);// 25037
	}

	private ScQtCommittee preparationDtlsInfo() {
		log.info("..In preparationDtlsInfo()..");

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
		log.info("..In supplierFinalizationInfo()..");

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

	private void setItemHigherAuthorityReqStatus(ScQtRecommend scQtRecommend) {
		log.info("setItemHigherAuthorityReqStatus");
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
		log.info("validateByPackage");
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
		log.info("filterRecommendedSuppliersList");
		boolean isSingleAward = this.getManagedBean().isSingleAward();
		boolean hasRecommendedSuppliers = false;
		ScQtRecommendDtl selectedRecommendDtl = null;
		List<ScQtRecommendDtl> removedList = scQtRecommend.getRemoveSupplierList();
		//33380 - start
		boolean multipleLOIandRefinalized = ParameterConst.RECOMMEND_BY_MULTIPLE_LOI.equals(getManagedBean().getFinalizationMethod()) 
				&& this.getManagedBean().getQtRefinalized(); 
		//33380 - end
		for (QtEvaluationResultDtlBean recommendedSupplier 
									     : cachedSuppliers) {
			if (isSingleAward) {
				//33380 - start
				if(multipleLOIandRefinalized){
					if(!scQtRecommend.isAwardedItem()){
						recommendedSupplier.setShortlisted(false);
					}
				}else{
					recommendedSupplier.setShortlisted(false);
				}
				//33380 - end
			} else {
				recommendedSupplier.setAwarded(false);
			}
			if ((recommendedSupplier.isAwarded() || 
					recommendedSupplier.isShortlisted()) && 
					!(scQtRecommend.isCancelled()&& !isQTRefinalized())) {
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
								debugLogger("filterRecommendedSuppliersList.recordStatusFlg-->recommendDtlId:" + detail.getRecommendDtlId() +", status: " + detail.getRecordStatusFlg());
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
		log.info("processAndSetDeletedRecommendDtls");
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
					debugLogger("processAndSetDeletedRecommendDtls.recordStatusFlg-->recommendDtlId:" + recommendDtl.getRecommendDtlId() +", status: " + recommendDtl.getRecordStatusFlg());
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
				debugLogger("processAndSetDeletedRecommendDtls.deleted-->recommendDtlId:" + recommendDtl.getRecommendDtlId() +", status: " + recommendDtl.getRecordStatusFlg());
			}
		}
		scQtRecommend.getRemoveSupplierList()
					 .addAll(removedRecommendDtlSet);
	}
	
	private ScQtRecommendDtl createOrRestoreRecommendDtl(
			QtEvaluationResultDtlBean recommendedSupplier,
			ScQtRecommend scQtRecommend, List<ScQtRecommendDtl> removedList) {
		log.info("createOrRestoreRecommendDtl");
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
		log.info("getQtProposalItem");
		ScQtProposalItem qtProposalItem = fetchQtProposalItem(
												scQtRecommend,proposalAmount);
		return qtProposalItem;
	}
	
	private ScQtProposalItem fetchQtProposalItem(ScQtRecommend scQtRecommend, 
										 		 ScQtProposalAmount qtProposalAmount) {
		log.info("fetchQtProposalItem");
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
		log.info("checkAndDeleteFromRemovedList");
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
		log.info("validateByItem");
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
	//25638 - Start
	/**
	 * 
	 */
	private void reviseSignOff() {
	  log.info("signOff");
      if (StringUtils.isNotBlank(
              getManagedBean().getCommitteeType())) {
          for (ScQtCommitteeActivity scQtCommitteeActivity 
                      : getManagedBean().getCommitteeList()) {
                  scQtCommitteeActivity.setSignedOff(false);
          }
      }
    }
	//25638 - End
	
	// #28834 - Start.
	/**
	 * 
	 */
	public void navigateToSubmissionPopup() {
		RequestContext.getCurrentInstance().execute("submissionPopup.show()");
	}
	// #28834 - End.
	
	/**
	 * Validate, save the selected quotation/tender supplier finalization
	 * details and update to BPM
	 * 
	 * @param outcome
	 *            the task to be performed by BPM
	 */
	public void submitSuppFinalization(String... revise) {
		log.info("submitSuppFinalization");
		String outcome = this.getManagedBean().getOutcome();
		this.getManagedBean().setAction(outcome);

		//25638 - Start
		if (revise.length == 0 && BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)) {
			// #25638 - Start
			validateRemarks();
			// #25638 - End
			RequestContext.getCurrentInstance().execute("reviseSuppFinalizationDlgs.show()");
		} else {
		//25638 - End
			validateEvaluationReports(SourcingConstants.SUPPLIER_FINALIZATION_TASK);
		ScQtFinalization scQtFinalization = this.getManagedBean().getScQtFinalization();
		List<ScQtFinalizationReport> finalizationReports = new ArrayList<ScQtFinalizationReport>();
		finalizationReports.addAll(this.getManagedBean().getDocumentList());
		finalizationReports.addAll(this.getManagedBean().getEndorsementReports());
		scQtFinalization.setScQtFinalizationReports(finalizationReports);
		//6906 note 117 - start
		String desicion = scQtFinalization.getDecision() ==null ? null: scQtFinalization.getDecision().getParameterCode();
		List<String> decisionList = new ArrayList<>();
		if(this.getManagedBean().getDecisionDropdown() != null && this.getManagedBean().getDecisionDropdown().length>0){
			for(SelectItem item: this.getManagedBean().getDecisionDropdown()){
					if (item.getValue() != null) {
						decisionList.add(item.getValue().toString());
					} 
			}
		}
		if (CollectionUtils.isEmpty(decisionList) || StringUtils.isEmpty(desicion) || !decisionList.contains(desicion)) {
			navigateToSupplierFinalizationTab();
			throw new EpException(MessageConst.ERROR_100022, 
					new String[] { ResourceBundleUtils.getMessage("lbl.sc.sf.meeting.decision", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		}
		//6906 note 117 - end
		Long currentStatusId = getManagedBean().getCurrentStatusId();
		log.info("currentStatusId === "+currentStatusId);
		if (QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION
					.equals(currentStatusId) || 
				QTStatusConst.SF_PENDING_ESCALATION_TO_BPK.equals(currentStatusId)
				// #25638 - Start. 
				|| QTStatusConst.QT_SUPPLIER_FINALIZATION_REVISION.equals(currentStatusId)
				|| QTStatusConst.SF_PENDING_BIDDING_FINALIZATION_REVISION.equals(currentStatusId)
				|| QTStatusConst.SF_PENDING_MULTIPLE_LOI_RECOMMENDATION_REVISION.equals(currentStatusId)
				// #25638 - End. 
		        ) {

			this.getManagedBean().setSubmit(true);
		}
		
		// Bug #32682 #27502 & #34921 - Start
		setUserLoiLoaPreparedBy(scQtFinalization, currentStatusId);
		// Bug #32682 #27502 & #34921 - End

		SuppFinalizationReqDTO reqDTO = preSubmitValidation();
		// #18685-Start.
		for (ScQtFinalizationReport scQtFinalizationReport : getManagedBean().getNewBriefingPaperReportList()) {
			scQtFinalizationReport.setPreviousReportContent(scQtFinalizationReport.getReportContent());
		}
		// #18685-End.
		reqDTO.setAddNewBriefingPaperReportList(getManagedBean().getNewBriefingPaperReportList());
		// #25638 - Start.
		if (QTStatusConst.SF_PENDING_SIGN_OFF.equals(currentStatusId) || QTStatusConst.SF_PENDING_ENDORSEMENT.equals(currentStatusId)) {
		// #25638 - End.
  			 // #28834 - Start.
			 if (QTStatusConst.SF_PENDING_SIGN_OFF.equals(currentStatusId) && CollectionUtils.isNotEmpty(scQtFinalization.getScQtFinalizationReports())) {
				 String decisionTypeCode = scQtFinalization.getDecision()
						  .getParameterCode();
				  // #28834 - Start.
				  if (ParameterConst.DECISION_RECOMMEND_SUPPLIER.equals(decisionTypeCode)) {
						try {
							debugLogger("Generating all reports...");
							downloadAllReports();	
						} catch(Exception e) {
							  SLAUtils.getInstance().logSLAError("QT", "submitSuppFinalizationTask", "docNo", "submitSuppFinalization", getUserIdSessionFromLoginId(), e);//28834-sonarQubeIssue5#84
						}
				  }
				  // #28834 - End.
				  if ((ParameterConst.DECISION_RBP.equals(decisionTypeCode) || ParameterConst.DECISION_RE_EVALUATION.equals(decisionTypeCode)) && CollectionUtils.isNotEmpty(getManagedBean().getDocumentList()) && getManagedBean().getDocumentList().size() > 0) {
					  // #28834 - Start. 
					  addBriefingPaper(scQtFinalization);
		              // #28834 - End. 
		          }
				 
			  }
			  // #28834 - End.
			if (CollectionUtils.isNotEmpty(
					this.getManagedBean().getCommitteeList())
					// #25638 - Start
					&& !BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)
					// #25638 - End
					) {
				log.info("committeeList is not empty !!");
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
			// #25638 - Start
			PmUser secretaryUser = null;
			if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)) {
				// #25638 - Start
			    reviseSignOff();
				validateRemarks();
				// #25638 - End
				for (ScQtCommitteeActivity committeeActivity : getManagedBean().getCommitteeList()) {
					 if (null != committeeActivity && null != committeeActivity.getScCommitteeMember()
							 && null != committeeActivity.getScCommitteeMember().getMemberRole()
							 && 
							 // #25638 - 31322(Note-#17) -Start.
							 ParameterConst.COMMITTEE_ROLE_SECRETARY.equalsIgnoreCase(committeeActivity.getScCommitteeMember().getMemberRole().getParameterCode())
							 // #25638 - 31322(Note-#17) -Start.
							 ) {
						 secretaryUser = committeeActivity.getScCommitteeMember().getPmUser();
						 reqDTO.setPmUser(secretaryUser);
						 break;
					 }
				 }
			} 
			// #25638 - End
			reqDTO.setScQtReverse(getManagedBean().getScQtReverse());
			String committeeType = getManagedBean().getCommitteeType();
			CommitteResDTO committeeResDTO = getCommitteMemberByUserId();
			ScCommitteeMember committeeMember = committeeResDTO.getCommMember();
			// #25638 - Start.
			if (!BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome) && StringUtils.isNotBlank(committeeType) && 
					ParameterConst.COMMITTEE_ROLE_CHAIRPERSON.equalsIgnoreCase(committeeMember.getMemberRole().getParameterCode()) && 
					isDigiSigningRequired(committeeResDTO.getRoleCode(),
											this.getManagedBean().getCommitteeList())) {
				this.getManagedBean().setAutoUpdate(Boolean.FALSE);
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			} else {
				// #28472 - Start.
				if (!BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome) && QTStatusConst.SF_PENDING_ENDORSEMENT.equals(currentStatusId)) {
					signOff();
				}
				// #28472 - End.
				//25638 - Start
				updateBPMTask(outcome, null, reqDTO);
				//25638 - End
			}
			// #25638 - End.
		} else {
			// #29921 - Start.
			String decisionTypeCode = scQtFinalization.getDecision()
					  .getParameterCode();
			if (ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY.equals(decisionTypeCode)) {
				IQTDetailSO qtDetailSO = null;
				try {
					qtDetailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
					if (null != qtDetailSO) {
						QuationTenderReqDTO qTreqDTO = new QuationTenderReqDTO();
						qTreqDTO.setQtdetail(getManagedBean().getScQt());
						qtDetailSO.updateQTDetails(qTreqDTO); 
					}
				} finally {
					qtDetailSO = null;
				}
			}
			// #29921 - End.
			
			// #28834 - Start.
			if (QTStatusConst.SF_MOF_COMMITTEE.longValue() == getManagedBean().getCurrentStatusId().longValue()
					&& (ParameterConst.DECISION_RBP.equals(getManagedBean().getScQtFinalization().getDecision().getParameterCode()) || ParameterConst.DECISION_RE_EVALUATION.equals(getManagedBean().getScQtFinalization().getDecision().getParameterCode()))
					&& getManagedBean().isBpkCentralContract()
					&& BpmOutcomeConst.BPM_SUBMIT.equals(outcome)) {
				addBriefingPaper(scQtFinalization);
				reqDTO.setCurrentStatusId(getManagedBean().getCurrentStatusId());
			}
			// #28834 - End.
			signOff();
			updateBPMTask(outcome, null, reqDTO);
		}
		//25638 - Start
		}
		//25638 - End
	}

	// #28834 - Start. 
	/**
	 * @param scQtFinalization
	 */
	private void addBriefingPaper(ScQtFinalization scQtFinalization) {
		int count = 0;
		  ScQtFinalizationReport scQtFinalizationReport = null;
		  for (ScQtFinalizationReport report: getManagedBean().getDocumentList()) {
			  if (report.getTitle().contains("Kertas Taklimat")) {
				  scQtFinalizationReport = report;
				  count++;
			  }
		  }
		 
		  ScQtFinalizationReportAttachment reportAttachment = null;
		  // #28834 - Start. 
		  if (null != scQtFinalizationReport && null == scQtFinalizationReport.getScQtFinalizationReportAttachment()) {
		    try {
		      // #28834 - Start. 
		      reportAttachment = generateDocumentAttachment(scQtFinalizationReport, true);
		      // #28834 - End. 
		    } catch (Exception e) {
		      log.error("SupplierFinalizationBackingBean.submitBrief(): Error while genarate briefing paper: " + e.getMessage());
		    }
		    scQtFinalizationReport.setScQtFinalizationReportAttachment(reportAttachment);
		  }
		  // #28834 - End. 
		  
		  // #28834 - Start.
		  scQtFinalizationReport = getManagedBean().getDocumentList().get(0);
		  // #28834 - End.
		  ScQtFinalizationReport report = new ScQtFinalizationReport();
		  report.setScQtFinalization(scQtFinalization);
		  String reportTitle = ResourceBundleUtils.getMessage("lbl.sc.kertas.taklimat", 
				  ResourceBundleUtils.BUNDLE_COMMON_LABELS, SourcingConstants.MY_LOCALE) 
				  + SPACE + "(" + count + ")";
		  report.setTitle(reportTitle);
		  if(scQtFinalizationReport.getPmDocument()!=null){
			  report.setDocCategory(scQtFinalizationReport.getPmDocument().getDocCategoryCode().getParameterCode());
			  report.setPmDocument(scQtFinalizationReport.getPmDocument());
			  report.setDefinationType(scQtFinalizationReport.getPmDocument().getDocDefinitionTypeCode()); 
		  }else{
				IParameterSO parameterSO = null;
				IDocumentSO  documentSO = null;
				try {
					documentSO  = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);
					parameterSO = BusinessDelegate.getInstance().getServiceObject(
					        ModuleCodeConst.PROFILE_MANAGEMENT,IParameterSO.class);
					
					DocumentReqDTO docReqDTO = new DocumentReqDTO();
					docReqDTO.setDocCategoryCode(ParameterConst.DOCUMENT_CATEGORY_BRIEFING_PAPER);
					DocumentResDTO docResDTO = documentSO.retrieveDocumentCheckList(docReqDTO);
					List<PmDocument> pmDocumentList = docResDTO.getDocumentList();
					
					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
					reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED);
					ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
					
					if (CollectionUtils.isNotEmpty(pmDocumentList) && null != docResDTO && null != resDTO) {
						for(PmDocument pmDocumentKT :pmDocumentList){
							if(null != pmDocumentKT.getDocDefinitionKey() && pmDocumentKT.getDocDefinitionKey().equals(SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT)){
								PmDocument pmDocument = pmDocumentKT;
								report.setDocCategory(pmDocument.getDocCategoryCode().getParameterCode());
								report.setPmDocument(pmDocument);
								report.setDefinationType(resDTO.getPmParameter());
							}
						}
					}
					
				} finally {
					documentSO = null;
					parameterSO = null;
				} 
		  }
		  report.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		  scQtFinalization.getScQtFinalizationReports().add(report);
	}
	// #28834 - End. 
	
	// #25638 - Start 
	/**
	 * 
	 */
	private void validateRemarks() {
		if (null != getManagedBean().getCurrentStatusId() 
				&& (getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.SF_PENDING_ENDORSEMENT.longValue()
				|| getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.SF_PENDING_SIGN_OFF.longValue())) {
			boolean remarksNotFound = true;
			if (!CollectionUtils.isEmpty(getManagedBean().getFinalizationRemarks())) {
				for (ScQtFinalizationRemarks finalizationRemark : getManagedBean().getFinalizationRemarks()) {
				  if (null != finalizationRemark.getCreatedUser() && null != finalizationRemark.getCreatedUser().getUserId()) {
                    if (finalizationRemark.getCreatedUser().getUserId().longValue() == new FacesScopes().getUserSessionBean().getUserId().longValue()) {
                      if (StringUtils.isEmpty(finalizationRemark.getRemark()) ) {
                        remarksNotFound = true;
                        break;
                      } else {
                        remarksNotFound = false;
                      }
                    }
                  }
				}
			// #25638 - Start. 
			} else {
				initializeFinalizationRemarks();
			}
			// #25638 - End.
			
			if (remarksNotFound) {
				throw new EpException(MessageConst.ERR_775152);
			}
		}
	}
	// #25638 - End	
	
	/**
	 *  If the LoogedIn user is Secretary, then show pop-up confirmation during the Supplier Finalization submission.
	 */
	//28834 start new 
	public void submitAction() {
		ScQtMeeting scQtMeeting = new ScQtMeeting();
		ScQtFinalization scQtFinalization = this.getManagedBean().getScQtFinalization();
		String decisionTypeCode = scQtFinalization.getDecision()
				  .getParameterCode();
	log.info("decisionTypeCode"+decisionTypeCode);
		this.checkIfLoggedinUserIsSecretary();
		if (this.getManagedBean().isSecretaryUser()) {
			if (this.getManagedBean().getOutcome().equalsIgnoreCase("REVISEBP")) {
				List<ScQtMeeting> meetingListList = getManagedBean().getMeetingList();
				int pendingCount = 0;
				// #28834 - Start.
				if (CollectionUtils.isNotEmpty(meetingListList)) {
					for (ScQtMeeting meeting : meetingListList) {
						if (null != meeting.getMeetingStatus() && null != meeting.getMeetingStatus().getStatusId()
								&& !QTStatusConst.MS_DONE.equals(meeting.getMeetingStatus().getStatusId())
								&& meeting.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
							
							if (!getManagedBean().isBpkCentralContract()) {
								// changeMeetingStatusBrief();
								
								//Boolean remarksNotFound = validationRemarksBrief(meeting.getQtMeetingId());
								if (StringUtils.isEmpty(meeting.getRemark())) {
									
									RequestContext.getCurrentInstance().execute("ReamrksBriefPopup.show()");
								} else {
									
									RequestContext.getCurrentInstance().execute(QTConstants.CONFIRM_BRIEF_POPUP);//sonarQube Issue 2 #91 # Vinod #updated constants
								}
								
							} else {
								
								RequestContext.getCurrentInstance().execute(QTConstants.CONFIRM_BRIEF_POPUP);//sonarQube Issue 2 #91 # Vinod #updated constants
							}
							
						} else {
							
						}
					}
				} else {
					RequestContext.getCurrentInstance().execute(QTConstants.CONFIRM_BRIEF_POPUP);//sonarQube Issue 2 #91 # Vinod #updated constants
				}
				// #28834 - End.
				// changeMeetingStatusBrief();

			} else {
				if (ParameterConst.DECISION_RBP
						.equals(decisionTypeCode)
						)  {
					RequestContext.getCurrentInstance().execute("confirmBriefPopup1.show()");
				}
				else
				{
					RequestContext.getCurrentInstance().execute("submissionPopup.show()");
				}
				
			}
		} else {
			this.submitSuppFinalization();
		}
	}
	//28834 end new
//28834 starts
	public boolean changeMeetingStatusBrief() {
		Boolean Meetingstatus= false;
		int doneList = 0;
		int pendingList = 0;
		List<ScQtMeeting> meetingList = getManagedBean().getMeetingList();
		
		if (CollectionUtils.isNotEmpty(meetingList)) {
            List<ScQtMeeting> localMeetingList = new ArrayList<ScQtMeeting>();
				
//				for (ScQtMeeting meeting : meetingList) {
//					meeting.getMeetingStatus().getStatusId();
//					if(meeting.getMeetingStatus().getStatusId().equals(QTConstants.SC_MEETING_DONE_STATUS_ID))
//					{
//						throw new EpException(MessageConst.ERROR_700091);
//					}
//					else 
//					{
//						Meetingstatus=true;
//					
//					}
//					
//				}
				for (ScQtMeeting meeting : meetingList){
					if(meeting.getMeetingStatus().getStatusId().equals(QTConstants.SC_MEETING_DONE_STATUS_ID)){
						doneList++;
					}else{
						pendingList++;
					}
				}
				if (pendingList==0){
					throw new EpException(MessageConst.ERROR_700091);
				}
				if((pendingList-doneList)>0 && doneList!=0){
					throw new EpException(MessageConst.ERROR_700091);
				}else{Meetingstatus=true;}
			}
		
		
		
return	Meetingstatus;	
		
		
	}
	//28834 ends
	//28834 starts
	public boolean validationRemarksBrief() {
		Boolean remarksNotFound= true;
		
		List<ScQtMeeting> meetingList = getManagedBean().getMeetingList();
		
		if (CollectionUtils.isNotEmpty(meetingList)) {
            List<ScQtMeeting> localMeetingList = new ArrayList<ScQtMeeting>();
				
				for (ScQtMeeting meeting : meetingList) {
					
					if (StringUtils.isEmpty(meeting.getRemark())) {
					
							
								remarksNotFound = false;
								//throw new EpException(MessageConst.ERR_775152);
								//RequestContext.getCurrentInstance().execute("ReamrksBriefPopup.show()");
								
								
							
				
					// #25638 - Start.
					}
					else
					{
						
					}
					
				
					
				}
				
			}
		
		
		
return	remarksNotFound;	
		
		
	}
	//28834  ends
	
	 //28834 start.
	 public boolean checkDisableBfButtonAfterValidation(){
		 
		    ScQt scQt = this.getManagedBean().getScQt();
			QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
			qtBackingBean.retrieveQuationTenderDetails(scQt.getQtId());
			scQt = qtBackingBean.getManagedBean().getQt();
			boolean isBpkCentralContract = false;
			isBpkCentralContract = isBpkCentralContract(scQt);
			boolean hidden = false;

			 if(!isBpkCentralContract){
		         List<ScQtMeeting> meetingListList = getManagedBean().getMeetingList();
		         if (CollectionUtils.isNotEmpty(meetingListList) && meetingListList.size() > 0) {
		        	 for (ScQtMeeting meeting : meetingListList) {
		        		 if (null != meeting.getMeetingStatus() && null != meeting.getMeetingStatus().getStatusId()
		        				 && QTStatusConst.MS_DONE.equals(meeting.getMeetingStatus().getStatusId())
		        				 && meeting.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
		        			 hidden = true;
		        			 this.getManagedBean().setDisableBfButton(true);
		        		 } 
		        	 }
		         }
	         }
	         return hidden;
	 }
	 //28834  end.
	
	//28834 start
	 private SuppFinalizationReqDTO validateInfobreif(int index) {
		boolean skipValidation = false;
		List<ScQtMeeting> meetingList = this.getManagedBean().getMeetingList();
		if (CollectionUtils.isNotEmpty(meetingList) && meetingList.size() > 0) {
			for (ScQtMeeting meeting : meetingList) {
				if (null != meeting.getMeetingStatus() && null != meeting.getMeetingStatus().getStatusId()
						&& !QTStatusConst.MS_DONE.equals(meeting.getMeetingStatus().getStatusId())
						&& meeting.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
					skipValidation = true;
				}
			}
		}
		return validateInfobreif(index, skipValidation);
	}
	//28834 end
		
		
		//28834 start
		private SuppFinalizationReqDTO validateInfobreif(int index, boolean skipValidation) {
			log.info("..In validateInfo()..");

			IQTSuppFinalizationSO qtSuppFinalizationSO = null;
			try {
				qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);
//				boolean isZonal = getManagedBean().getScQt().isZonal();
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
						if(!skipValidation){
							//qtSuppFinalizationSO.validatePreparation(reqDTO);
						}
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
							
							if ((ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY
									.equals(decisionTypeCode) && 
									(scQtFinalization.getReason() == null || 
										scQtFinalization.getReason().getReasonId() == null)) && !skipValidation) {
								navigateToSupplierFinalizationTab();
								throw new EpException(MessageConst.ERROR_760385);
							}
							// isBpkFinalizationReq is true for Restricted Tender whose sent for  BPK Approval
						if (!this.getManagedBean().isInitialDecisionFwdToBPK()
								&& this.getManagedBean().getScQt().isBpkFinalizationReq()
								&& !this.getManagedBean().isMofCntralCtc()) {//#31632
								
							if ((!ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY
									.equals(decisionTypeCode)
									|| scQtFinalization.getHigherCommitte() != null) && !skipValidation) {
								navigateToSupplierFinalizationTab();
								throw new EpException(MessageConst.ERROR_760459);
							}
							
							}
							processSuppliersBeforeSaving();
							if(!skipValidation){
								if (!getManagedBean().isItemize()) {
									//validateByPackage();
								} else {
									validateByItem();
								}
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
						List<ScQtFinalizationEndorsementRecommendationRemarks> finalizationEndorsementRecommendationRemarks = 
								finalizeRemarks(getManagedBean().getFinalizationPengreysonremarks());
						scQtFinalization.setScQtFinalizationEndorsementRemarks(
												 finalizationEndorsementRemarks);
						scQtFinalization.setScQtFinalizationEndorsmentRecommendationRemarks(finalizationEndorsementRecommendationRemarks);
						setUserDetails(scQtFinalization);
		
						reqDTO.setScQtCommittee(_scQtCommittee);
						reqDTO.setScQtFinalization(scQtFinalization);
						reqDTO.setRefinalize(isQTRefinalized());
//						reqDTO.setResetSuppliers(
//								this.getManagedBean().getResetSupplierList());
						
						// #25426 ruzaimah - Start
						// 202407252@haziq #25426
						validateInactiveRole(scQtFinalization,decisionTypeCode);
						// #25426 ruzaimah - End
						
						break;
		
					default :
						reqDTO.setScQtFinalization(scQtFinalization);
				}
				
				return reqDTO;
			} finally {

			}
		}//28834 end
		
		
		//28834 starts//
		private void updateBPMTaskBrief(String outcome, PmDigiSign digiSign, SuppFinalizationReqDTO reqDTO) {
			log.info("updateBPMTask");
			IQTSuppFinalizationSO qtSuppFinalizationSO = null;
			int activeIndex = this.getManagedBean().getActiveIndex();
			String taskId = this.getManagedBean().getTaskId();
			SuppFinalizationResDTO resDTO = null;
			
			try {
				qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);
				if (reqDTO == null) {
					reqDTO = validateInfobreif(activeIndex);
				}			
				
				/* Enhancement #21694 START */
				reqDTO.setScQtReverse(getManagedBean().getScQtReverse());
				/* Enhancement #21694 END */
				
				reqDTO.setScQt(getManagedBean().getScQt());
				reqDTO.setRefinalize(isQTRefinalized());
				reqDTO.setOutcome(outcome);
				reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
				reqDTO.setTaskId(taskId);
				reqDTO.setPayLoad(getManagedBean().getPayLoad());
				reqDTO.setRoleCode(QTStatusConst.SF_PENDING_ESCALATION_TO_BPK
					       .equals(getManagedBean().getCurrentStatusId()) ? 
											RoleCode.BPK_APPROVER.value() : getCommitteeRoleCode());
				// #28472 and #25638 - Start.
				if (null == reqDTO.getPmUser()) {
					reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
				}
				// #28472 and #25638 - End.
				reqDTO.setCurrentFinalizationStatus(
						getManagedBean().getCurrentStatusId());
				reqDTO.setInitialDecisionFwdHighAuth(
						getManagedBean().isInitialDecisionFwdHighAuth());
				
				boolean hasPendingLOA = false;
				resDTO = qtSuppFinalizationSO.retrievePendingLOA(reqDTO);
				hasPendingLOA = resDTO.isHasPendingLOA();
				reqDTO.setHasPendingLoa(hasPendingLOA);		
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
								//Bug #28235 - start
								if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)){
									isCurrentCommitteeAuthorisedToSubmit = true;
								}else{
									for (String committee : committeeSet) {
										isCurrentCommitteeAuthorisedToSubmit = 
												isCurrentCommitteeGreaterThanOrEquals(
															 committeeType, committee);
										if (isCurrentCommitteeAuthorisedToSubmit) {
											break;
										}
									}
								}
								//Bug #28235 - end
							} else {
								isCurrentCommitteeAuthorisedToSubmit = true;
							}
						} else {
							isCurrentCommitteeAuthorisedToSubmit = true;
						}
					} finally {
						committeeSO = null;
					}
					
					//#30324 - Start
					QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
					qtBackingBean.retrieveQuationTenderDetails(reqDTO.getScQt().getQtId());
					ScQt scQt = qtBackingBean.getManagedBean().getQt();
					boolean isBpkCentralContract = false;
					isBpkCentralContract = isBpkCentralContract(scQt);

					if (isBpkCentralContract) {
						this.getManagedBean().setBpkCentralContract(true);
					}
					
					if (!isCurrentCommitteeAuthorisedToSubmit && !getManagedBean().isBpkCentralContract()) {//#30324 - End
						String committeeName = getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE, 
																committeeType);
						
						throw new EpException(MessageConst.ERROR_760394, new String[] { 
												committeeName, String.valueOf(totalIntendedPrice.intValue()) });
					}
				}
				
				if (getManagedBean().isSubmit() && 
						!getManagedBean().isInitialDecisionFwdToBPK() &&
							isRecommendByAward() && 					
								(ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY
									.equals(decisionTypeCode))) {
					String reasonType = getManagedBean().getScQtFinalization().getReason().getReasonCode();
					log.info("Reason Code == "+reasonType);				
					if("2".equalsIgnoreCase(reasonType)){
						boolean isOfferPriceExceed = checkSelectedSupplierOfferPriceIsExceedCommitteeAuthority(reqDTO.getScQtFinalization());
						log.info("== IS SUPPLIER OFFER PRICE == "+isOfferPriceExceed);
						if(!isOfferPriceExceed){
							log.info("Selected supplier offer below committee authority !! ");
							throw new EpException(MessageConst.ERROR_760511);
						}
					}
				}

				Long currentStatusId = getManagedBean().getCurrentStatusId();
				if ( (
						// #28834 - Start.
						/*QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION
							.equals(currentStatusId) || */
						// #28834 - End.
						QTStatusConst.SF_PENDING_ESCALATION_TO_BPK
							.equals(currentStatusId)) && 
								ParameterConst.DECISION_RECOMMEND_SUPPLIER
									.equals(decisionTypeCode) ) {
					try {
						debugLogger("Generating all reports...");
						downloadAllReports();	
					} catch(Exception e) {
						//Sonarqube QT Vulnerability Minor Suresh [9/8/2023] START			 
						SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE, SLALogConstant.QT_SUPPLIER_FINALIZATION_BACKINGBEAN, reqDTO.getScQt().getQtNo(),
								SLALogConstant.QT_UPDATE_BPM_TASKBRIEF, getUserIdSessionFromLoginId(), e);
						//Sonarqube QT Vulnerability Minor Suresh [9/8/2023] END
					}
				}
				
				// #18685 - Start
				resetUnSavedReportContent(this.getManagedBean().getNewBriefingPaperReportList());
				if (CollectionUtils.isNotEmpty(reqDTO.getScQtFinalization().getScQtFinalizationReports())) {
					reqDTO.getScQtFinalization().getScQtFinalizationReports().addAll(getManagedBean().getEvaluationReportList());
				}
				if (CollectionUtils.isNotEmpty(getManagedBean().getNewBriefingPaperReportList())
						&& CollectionUtils.isNotEmpty(getManagedBean().getConsolidationBriefingPaperReportList())) {
					getManagedBean().getNewBriefingPaperReportList().addAll(
							getManagedBean().getConsolidationBriefingPaperReportList());
				}
				reqDTO.setAddNewBriefingPaperReportList(getManagedBean().getNewBriefingPaperReportList());
				// #18685 - End
				
				// #28834- Start.
				if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISEBP)) {
					reqDTO.setRoleCode(QTStatusConst.QT_SF_MOF_COMMIEEE
							.equals(getManagedBean().getCurrentStatusId()) ? 
									RoleCode.MOF_DESK_OFFICER.value() : getCommitteeRoleCode());
				}
				// #28834- End.
				
				resDTO = qtSuppFinalizationSO.submitSupplierFinalizationBrief(reqDTO);

				if (resDTO.isSuccessful()) {
					String msgCode = null;
					// #25638 - Start
					String label = "lbl.sc.sf.supplier.finalization";
					if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
						msgCode = MessageConst.INFO_700067;
					} else if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
						msgCode = MessageConst.INFO_776042;
						label = "lbl.sc.quotation.tender";
					} else {
						msgCode = getMessgeConstant(outcome);
					}
					
					// #28834 - Start.
					if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISEBP)) {
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(msgCode,
										new String[] { getManagedBean().getScQt().getQtNo() },
										MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

						FacesContext fc = FacesContext.getCurrentInstance();
						fc.getApplication().getNavigationHandler()
								.handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);

					} else {
					  successMessage(msgCode, getMessageCommon(label), getManagedBean().getScQt().getQtNo(), Boolean.TRUE);
					}
					// #28834 - End.
					
					super.renewSesionObject();
					// #25638 - End
				}
			} finally {
				qtSuppFinalizationSO = null;
			}
		}
		//#28834 end
	
	
	
	public void reviseSuppFinalization() {
		updateBPMTask(this.getManagedBean().getOutcome(), null, null);
	}
	
	/**
	 * Method to navigate back to previous page
	 * 
	 * @return the previous page
	 */
	public String backAction() {
		String t = getManagedBean().getPreviousPageUrl();
		getManagedBean().setPreviousPageUrl(null);
		return t;
	}
	
	private SuppFinalizationReqDTO preSubmitValidation() {
		log.info("preSubmitValidation");
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
		
		 // #27502  //#29699  //28780#52
		//27502 - Start //28834 starts.
		if ((getManagedBean().isBpkCentralContract() || !getManagedBean().isInitialDecisionFwdToBPK()) && null !=this.getManagedBean().getEndorsementReport()
				&& this.getManagedBean().getEndorsementReport().getScQtFinalizationReportAttachment() == null){
			// #31934 - Start.
			/*throw new EpException(MessageConst.ERROR_775150,new String[] { ResourceBundleUtils.getMessage(
					"lbl.sc.committee.endorsement.report",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()) });*/
			navigateToEndorsementReportTab();
			throw new EpException(MessageConst.ERROR_770271);
			// #31934 - End.
		}
		//27502 - End.
		
		// #28834 - Start.
		if (null != getManagedBean().getCurrentStatusId() 
                && (getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION.longValue()
                || getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.QT_SUPPLIER_FINALIZATION_REVISION.longValue()
                || getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.SF_PENDING_BIDDING_FINALIZATION_REVISION.longValue()
                || getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.SF_PENDING_MULTIPLE_LOI_RECOMMENDATION_REVISION.longValue()
                || getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.QT_PENDING_RECOMMENDATION_REVISE_BRIEFING_PAPER.longValue())) {
			if (ParameterConst.DECISION_CANCEL.equals(decisionTypeCode) || ParameterConst.DECISION_RBP.equals(decisionTypeCode) ) {
				boolean remarksNotFound = true;
				if (!CollectionUtils.isEmpty(getManagedBean().getFinalizationRemarks())) {
					for (ScRemark finalizationRemark : getManagedBean().getFinalizationRemarks()) {
						if (null != finalizationRemark.getCreatedUser() && null != finalizationRemark.getCreatedUser().getUserId()) {
							if (finalizationRemark.getCreatedUser().getUserId().longValue() == new FacesScopes().getUserSessionBean().getUserId().longValue()) {
								if (StringUtils.isEmpty(finalizationRemark.getRemark()) ) {
									remarksNotFound = true;
									break;
								} else {
									remarksNotFound = false;
								}
							}
						}
					}
				} 
				
				if (remarksNotFound) {
					navigateToSupplierFinalizationTab();
					if (ParameterConst.DECISION_CANCEL.equals(decisionTypeCode)) {
						throw new EpException(MessageConst.ERROR_760360);
					} else if (ParameterConst.DECISION_RBP.equals(decisionTypeCode)) {
						throw new EpException(MessageConst.ERROR_700093);
					}
				}
			} 
		}
		// #28834 - End.
		
		if (!getManagedBean().isInitialDecisionFwdToBPK()) {
			//25638 - for 25037 note 284 no validation for committee during sf revision - start
			if (Arrays.asList(new Long[]{
					QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION, 
					QTStatusConst.QT_SUPPLIER_FINALIZATION_REVISION })
					.contains(this.getManagedBean().getCurrentStatusId())) {
			//25638 - for 25037 note 284 no validation for committee during sf revision - end
				// #28834 - #31934 - starts.
				validateFinalizationReports(null, false);
				// #28834 - #31934 - ends.
				
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
			} else {
				// need to set Keputusan report at time of Endorsement and signOff status
				this.getManagedBean().getScQtFinalization()
						.getScQtFinalizationReports()
						.addAll(this.getManagedBean().getEndorsementReports());
			}
			
			// Disabling kertas taklimat as per Bug #16172
			/*if(!scQtFinalization.isBriefingPaperUploaded()){
				navigateToBriefingPaperTab();
				throw new EpException(MessageConst.ERROR_760452);
			}*/
			//#27364 Start
			if (ParameterConst.DECISION_CANCEL
					  .equals(decisionTypeCode) || ParameterConst.DECISION_RE_PUBLISH
					  .equals(decisionTypeCode) || ParameterConst.DECISION_RE_EVALUATION
					  .equals(decisionTypeCode)){
				this.getManagedBean().setFinalizationMethod(null);
			}
			//#27364 End
			if (this.getManagedBean().getFinalizationMethod() != null
					&& (this.getManagedBean().getFinalizationMethod()
							.equals(ParameterConst.RECOMMEND_BY_AWARD) || this
							.getManagedBean().getFinalizationMethod()
							.equals(ParameterConst.RECOMMEND_BY_SINGLE_AWARD)) 
							// #28834 - Start.
							&& !getManagedBean().isDisableJustifications()
							// #28834 - End.
							) {
				if (this.getManagedBean().getJustification() == null
						|| this.getManagedBean().getJustification()
								.getReasonId() == null) {
					navigateToEndorsementReportTab();
					throw new EpException(MessageConst.ERROR_100061,
							new String[] { ResourceBundleUtils.getMessage(
									"lbl.sc.sf.justification.selection",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale()) });
				}
			}
			scQtCommittee = preparationDtlsInfo();
			
		}
		
		//Bug #35129 load
		loadRecommededSuppliersItemize();
		// Save Preparation and Supplier finalization tab(s) data 
		// before throwing exception
		// #25426 ruzaimah - Start
		// 202407252@haziq #25426
		validateInactiveRole(scQtFinalization,decisionTypeCode);
		// #25426 ruzaimah - End
		validateReEvaluation();
		reqDTO = validateInfo(2);
		//#27364 Start
		if (ParameterConst.DECISION_CANCEL
				  .equals(decisionTypeCode) || ParameterConst.DECISION_RE_PUBLISH
				  .equals(decisionTypeCode) || ParameterConst.DECISION_RE_EVALUATION
				  .equals(decisionTypeCode)){
			this.getManagedBean().setFinalizationMethod(null);
		}
		//#27364 End
		
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
		
		// #28834 - Start.
		/*if (ParameterConst.DECISION_CANCEL
						  .equals(decisionTypeCode) && 
				CollectionUtils.isEmpty(
					scQtFinalization.getScQtFinalizationRemarks())) {
			navigateToSupplierFinalizationTab();
			initializeFinalizationRemarks();
			
			// Remarks are mandatory for QT cancellation
			throw new EpException(MessageConst.ERROR_760360);
		}
		
		if (ParameterConst.DECISION_RBP
				  .equals(decisionTypeCode) && 
				  CollectionUtils.isEmpty(
							scQtFinalization.getScQtFinalizationRemarks())) {
			log.info("--->DECISION_RBPDECISION_RBPDECISION_RBPDECISION_RBP");
        	navigateToSupplierFinalizationTab();
        	initializeFinalizationRemarks();
        	
        	
        	// Remarks are mandatory for QT cancellation
        	throw new EpException(MessageConst.ERROR_700093);
        }*/
		
		// #28834 - End.
		
		
		/*
		 * Enhancement #21694
		 * SET MEETING MEMBER ACTIVITY
		 */
		reqDTO.setMeetingMembersActivity(getManagedBean().getCommitteeList());
		return reqDTO;
	}
	
	// Save and reload suppplier finalization data
	private void saveAndReloadFinalizationDetails(IQTSuppFinalizationSO qtSuppFinalizationSO,
												  SuppFinalizationReqDTO reqDTO) {
		log.info("saveAndReloadFinalizationDetails");
		try {
			if (qtSuppFinalizationSO == null) {
				qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);
			}	
			
			// #18685 - Start
			resetUnSavedReportContent(this.getManagedBean().getNewBriefingPaperReportList());
			if (CollectionUtils.isNotEmpty(reqDTO.getScQtFinalization().getScQtFinalizationReports())) {
				reqDTO.getScQtFinalization().getScQtFinalizationReports().addAll(getManagedBean().getEvaluationReportList());
			}
			if (CollectionUtils.isNotEmpty(getManagedBean().getNewBriefingPaperReportList())
					&& CollectionUtils.isNotEmpty(getManagedBean().getConsolidationBriefingPaperReportList())) {
				getManagedBean().getNewBriefingPaperReportList().addAll(
						getManagedBean().getConsolidationBriefingPaperReportList());
			}
			reqDTO.setAddNewBriefingPaperReportList(getManagedBean().getNewBriefingPaperReportList());
			// #18685 - End
						
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
									.saveSupplierFinalization(reqDTO);
			reloadData(resDTO);				
			this.evaluateDecisionList();//6906 note 117
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	private void updateBPMTask(String outcome, PmDigiSign digiSign, SuppFinalizationReqDTO reqDTO) {
		log.info("updateBPMTask");
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
			
			// #28834 - Start.
			if (QTStatusConst.QT_SF_MOF_COMMIEEE.longValue() == getManagedBean().getCurrentStatusId().longValue()
					&& ParameterConst.DECISION_RBP.equals(getManagedBean().getScQtFinalization().getDecision().getParameterCode())
					&& getManagedBean().isBpkCentralContract()
					&& BpmOutcomeConst.BPM_SUBMIT.equals(outcome)) {
				log.info("SupplierFinalizationBackingBean.updateBPMTask(): setting REVISEBP as outcome");
				outcome = BpmOutcomeConst.BPM_REVISEBP;
			}
			// #28834 - End.
			
			/* Enhancement #21694 START */
			reqDTO.setScQtReverse(getManagedBean().getScQtReverse());
			/* Enhancement #21694 END */
			
			reqDTO.setScQt(getManagedBean().getScQt());
			reqDTO.setRefinalize(isQTRefinalized());
			reqDTO.setOutcome(outcome);
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(taskId);
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setRoleCode(QTStatusConst.SF_PENDING_ESCALATION_TO_BPK
				       .equals(getManagedBean().getCurrentStatusId()) ? 
										RoleCode.BPK_APPROVER.value() : getCommitteeRoleCode());
			// #28472 and #25638 - Start. 
			/*if (null == reqDTO.getPmUser()) {*/
				reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			/*	}*/
			// #28472 and #25638 - End. 
			reqDTO.setCurrentFinalizationStatus(
					getManagedBean().getCurrentStatusId());
			reqDTO.setInitialDecisionFwdHighAuth(
					getManagedBean().isInitialDecisionFwdHighAuth());
			
			boolean hasPendingLOA = false;
			resDTO = qtSuppFinalizationSO.retrievePendingLOA(reqDTO);
			hasPendingLOA = resDTO.isHasPendingLOA();
			reqDTO.setHasPendingLoa(hasPendingLOA);		
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
							//Bug #28235 - start
							if(committeeType.equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)){
								isCurrentCommitteeAuthorisedToSubmit = true;
							}else{
								for (String committee : committeeSet) {
									isCurrentCommitteeAuthorisedToSubmit = 
											isCurrentCommitteeGreaterThanOrEquals(
														 committeeType, committee);
									if (isCurrentCommitteeAuthorisedToSubmit) {
										break;
									}
								}
							}
							//Bug #28235 - end
						} else {
							isCurrentCommitteeAuthorisedToSubmit = true;
						}
					} else {
						isCurrentCommitteeAuthorisedToSubmit = true;
					}
				} finally {
					committeeSO = null;
				}
				
				//#30324 - Start
				QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
				qtBackingBean.retrieveQuationTenderDetails(reqDTO.getScQt().getQtId());
				ScQt scQt = qtBackingBean.getManagedBean().getQt();
				boolean isBpkCentralContract = false;
				isBpkCentralContract = isBpkCentralContract(scQt);

				if (isBpkCentralContract) {
					this.getManagedBean().setBpkCentralContract(true);
				}
				
				if (!isCurrentCommitteeAuthorisedToSubmit && !getManagedBean().isBpkCentralContract()) {//#30324 - End
					String committeeName = getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE, 
															committeeType);
					
					throw new EpException(MessageConst.ERROR_760394, new String[] { 
											committeeName, String.valueOf(totalIntendedPrice.intValue()) });
				}
			}
			//#26169 - START
			if (getManagedBean().isSubmit() && 
					!getManagedBean().isInitialDecisionFwdToBPK() &&
						isRecommendByAward() && 					
							(ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY
								.equals(decisionTypeCode))) {
				String reasonType = getManagedBean().getScQtFinalization().getReason().getReasonCode();
				log.info("Reason Code == "+reasonType);
				//#26169 - 21102020 , START
				if(reasonType == null || reasonType == ""){
					IReasonSO reasonSO = null;
					try{
						ReasonReqDTO reasonReqDTO = new ReasonReqDTO();
						reasonReqDTO.setReasonId(getManagedBean().getScQtFinalization().getReason().getReasonId());
						reasonSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);
						reasonType = reasonSO.retrieveReasonById(reasonReqDTO).getPmReason().getReasonCode();
					}finally {
						reasonSO = null;
					}
				}
				log.info("Reason Code [after] == "+reasonType);	
				//#26169 - 21102020 , END
				if("2".equalsIgnoreCase(reasonType)){
					boolean isOfferPriceExceed = checkSelectedSupplierOfferPriceIsExceedCommitteeAuthority(reqDTO.getScQtFinalization());
					log.info("== IS SUPPLIER OFFER PRICE == "+isOfferPriceExceed);
					if(!isOfferPriceExceed){
						log.info("Selected supplier offer below committee authority !! ");
						throw new EpException(MessageConst.ERROR_760511);
					}
				}
			}
			//#26169 - END
			Long currentStatusId = getManagedBean().getCurrentStatusId();
			if ( (
					// #28834 - Start.
					/*QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION
						.equals(currentStatusId) || */
					// #28834 - End.
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
			
			// #28834 - Start
			// To force latest briefing paper updated with the recent content during Endorsement or SignOff status.
			if (getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION.longValue() || 
					getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.QT_SUPPLIER_FINALIZATION_REVISION.longValue()) {
				ScQtFinalizationReport scQtFinalizationReport = null;
				for (ScQtFinalizationReport report: getManagedBean().getDocumentList()) {
					if (report.getTitle().contains("Kertas Taklimat")) {
						scQtFinalizationReport = report; 
					}
				}
				if (null != scQtFinalizationReport && null != scQtFinalizationReport.getScQtFinalizationReportAttachment()
						&& StringUtils.isNotEmpty(scQtFinalizationReport.getScQtFinalizationReportAttachment().getFilePath())) {
					String filePath = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR) 
							+ CommonConstants.DOC_REPO_PATH_SEPARATOR 
							+ scQtFinalizationReport.getScQtFinalizationReportAttachment().getFilePath();
					File file = new File(filePath);
					if (null != file && file.exists()) {
						 boolean deleteFile=file.delete();//#28834-sonarQubeIssue6#54
						 log.info("======FileDeletedSucessFully======"+deleteFile);//#28834-sonarQubeIssue6#54
					}
					scQtFinalizationReport.setScQtFinalizationReportAttachment(null);
				}
			}
			// #28834 - End
			
			// #27502 -- Start
			QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
			qtBackingBean.retrieveQuationTenderDetails(reqDTO.getScQt().getQtId());
			ScQt scQt = qtBackingBean.getManagedBean().getQt();
			//29707
			//boolean isBpkCentralContract = false;
			//isBpkCentralContract = isBpkCentralContract(scQt);

			//if (isBpkCentralContract) {
				reqDTO.setScQt(scQt);
			//}
			
			// #18685 - Start
			resetUnSavedReportContent(this.getManagedBean().getNewBriefingPaperReportList());
			if (CollectionUtils.isNotEmpty(reqDTO.getScQtFinalization().getScQtFinalizationReports())) {
				reqDTO.getScQtFinalization().getScQtFinalizationReports().addAll(getManagedBean().getEvaluationReportList());
			}
			if (CollectionUtils.isNotEmpty(getManagedBean().getNewBriefingPaperReportList())
					&& CollectionUtils.isNotEmpty(getManagedBean().getConsolidationBriefingPaperReportList())) {
				getManagedBean().getNewBriefingPaperReportList().addAll(
						getManagedBean().getConsolidationBriefingPaperReportList());
			}
			reqDTO.setAddNewBriefingPaperReportList(getManagedBean().getNewBriefingPaperReportList());
			// #18685 - End
			//29561 -start
			reqDTO.setBaseURL(SmCommonUtil.getBasePortalUrl());
			reqDTO.setTaskName(this.getManagedBean().getTaskName());
			//29561 -end
			resDTO = qtSuppFinalizationSO.submitSupplierFinalization(reqDTO);

			if (resDTO.isSuccessful()) {
				String msgCode = null;
				// #25638 - Start
				String label = "lbl.sc.sf.supplier.finalization";
				if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
					msgCode = MessageConst.INFO_700067;
				} else if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
					msgCode = MessageConst.INFO_776042;
					label = "lbl.sc.quotation.tender";
				} else {
					msgCode = getMessgeConstant(outcome);
				}
				
				// #28834 - Start
				if (null != getManagedBean().getScQtFinalization().getDecision() 
				    && "RBP".equals(getManagedBean().getScQtFinalization().getDecision().getParameterCode())
				    && outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SIGNOFF)) {
				  msgCode = MessageConst.INFO_700091;
				  outcome = BpmOutcomeConst.BPM_REVISEBP;
				}
				// #28834 - End
				
				// #28834 - Start.
				if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISEBP)) {
					FacesContext.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null, 
									MessageUtils.getMessage(msgCode,
											new String[] { getManagedBean().getScQt().getQtNo() }, 
											MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

					FacesContext fc = FacesContext.getCurrentInstance();
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);

				} else {
					
					//#12487 
					//send notification
					if (!BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)) {
					    if (ParameterConst.RECOMMEND_BY_MULTIPLE_LOI.equals(this.getManagedBean().getFinalizationMethod())) {
					        if (currentStatusId.equals(QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION) || 
								currentStatusId.equals(QTStatusConst.SF_PENDING_ESCALATION_TO_BPK)) {
					        	//send prepare LOI
					            prepareNotification(CommonConstants.LOI);
					        } else if (currentStatusId.equals(QTStatusConst.SF_PENDING_SIGN_OFF) || 
									   currentStatusId.equals(QTStatusConst.SF_PENDING_MULTIPLE_LOI_RECOMMENDATION)) {
					        	//send prepare LOA
					            prepareNotification(CommonConstants.LOA);
					        }
					     //#12487 - start Notification to prepare LOA (without LOI)
					    } else if (currentStatusId.equals(QTStatusConst.SF_PENDING_SIGN_OFF) || 
								   currentStatusId.equals(QTStatusConst.SF_PENDING_ESCALATION_TO_BPK)||
								   currentStatusId.equals(QTStatusConst.SF_PENDING_BIDDING_ESCALATION)) {
					        prepareNotification(CommonConstants.LOILOA);
					    }
					}

					//#12487 - end
					
					successMessage(msgCode, getMessageCommon(label), getManagedBean().getScQt()
							.getQtNo(), Boolean.TRUE);
				}
				// #28834 - End.
				super.renewSesionObject();
				// #25638 - End
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	/**
	 * To retrieve the document list in PMDocument for Briefing Paper/Kertas
	 * Taklimat
	 */
	private void retrieveBriefingPaper() {
		log.info("retrieveBriefingPaper");
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
			//28834 starts
			reqDTO.setCommitteeType(getManagedBean().getCommitteeType());
			//28834 ends
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.retrieveFinalizationReports(reqDTO);
			ScQtFinalization _scQtFinalization = resDTO.getScQtFinalization();
			
			List<ScQtFinalizationReport> finalizationReports 
	        					= new ArrayList<ScQtFinalizationReport>(_scQtFinalization.getScQtFinalizationReports());
			List<String> reportTitleList = new ArrayList<>();
			// 18685 - START.
			reportTitleList.add(SourcingConstants.DOC_DEF_KEY_UDPUPBM_REPORT);
			checkKertasTaklimatReports(finalizationReports, reportTitleList, SourcingConstants.SUPPLIER_FINALIZATION_TASK);
			// 18685 - END.
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
				// set to false at meeting screen, need to verify 
				if (scQtFinalization.isBriefingPaperUploaded()) {
					if (SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT.equals(docDefinitionKey)) {
						report.setTempReportAttachment(report.getScQtFinalizationReportAttachment());
					}
				}
			}
			this.getManagedBean().setEndorsementReports(endorsementReports);
			if (coverReport != null && 
					getManagedBean().isInitialDecisionFwdToBPK()) {
				finalizationReports.remove(coverReport);
			}
			
			 // #27502 -- Start
			QTDetailBackingBean qtBackingBean =   new QTDetailBackingBean();
			qtBackingBean.retrieveQuationTenderDetails(this.getManagedBean().getScQt().getQtId());
			ScQt scQt = qtBackingBean.getManagedBean().getQt();
			boolean isBpkCentralContract = false;
			isBpkCentralContract = isBpkCentralContract(scQt);
			//27502 - Start.
			// #28834 - Start.
			getManagedBean().setBpkCentralContract(isBpkCentralContract);
			// #28834 - End.
			//27502 - End.
			
			// #28834 - Strat.
			if (isBpkCentralContract || !getManagedBean().isInitialDecisionFwdToBPK()) {
				int totalFinalizationRounds = findFinalizationCount();
				// #28834 - Strat.
				
				int meetingStatusOtherThanDonecount = 0;
	            List<ScQtMeeting> meetingListList = getManagedBean().getMeetingList();
	            if (CollectionUtils.isNotEmpty(meetingListList)) {
	              for (ScQtMeeting meeting : meetingListList) {
	            	  // #28834 - Start.
	            	  if (null != meeting.getMeetingStatus() && null != meeting.getMeetingStatus().getStatusId()
	                    && !QTStatusConst.MS_DONE.equals(meeting.getMeetingStatus().getStatusId())
	                    && !meeting.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
	            		  meetingStatusOtherThanDonecount++;
	            	  } 
	            	  // #28834 - End.
	              }
	              totalFinalizationRounds = totalFinalizationRounds - meetingStatusOtherThanDonecount;
	            }
	            
	            if (endorsementReportCount < totalFinalizationRounds) {
					// No need to generate Kertas Keputusan under Briefing Paper Preparation tab.
					String reportTitle = ResourceBundleUtils.getMessage("lbl.sc.committee.endorsement.report", 
				 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, SourcingConstants.MY_LOCALE) 
				 			+ SPACE + "(" + totalFinalizationRounds + ")";
					addEndorsementReport(reportTitle);
					//Sort reports
					sortEndorsementReports(endorsementReports);
				} else {
					sortEndorsementReports(endorsementReports);
					
					//Bug #25829
					if(!CollectionUtils.isEmpty(endorsementReports)){
						this.getManagedBean().setEndorsementReport(endorsementReports.get(0));
					}
				}
				
				// 28834 - end.

				this.getManagedBean().setEndorsementReports(endorsementReports);
			}
			// #28834 - End.
			if (finalizationReports != null) {
				//sortReports(finalizationReports);
				
				populateEvolutionReports(finalizationReports, getManagedBean().getScQt());
				
				getManagedBean().setDocumentList(finalizationReports);
				
				if (CollectionUtils.isEmpty(getManagedBean().getNewBriefingPaperReportList())) {
					saveNewBriefingPaperReportList(reportTitleList, _scQtFinalization, qtId);
				}
				
				// #18685 - START.
				if (CollectionUtils.isNotEmpty(getManagedBean().getNewBriefingPaperReportList()) && getManagedBean().getNewBriefingPaperReportList().size() > 0
				    && getManagedBean().isSecretaryUser()) {
					IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IUserSO.class);
					UserReqDTO userReqDTO = new UserReqDTO();
					userReqDTO.setLoginId(getFacesScopes().getUserScreenName());
					PmUser currentUser = userSO.retrieveUserByLoginId(userReqDTO).getPmUser();
					if (currentUser != null && null != currentUser.getUserId()) {
						Long userLoginId = new LogKey(currentUser.getUserId()).getUserId();
						if (null != userLoginId) {
							getManagedBean().getNewBriefingPaperReportList().get(0).setChangedBy(userLoginId);
						}
					}
				}
				// #18685 - END.
			}
			
			//#28784
			if(getManagedBean().getScQt().isDemoRequired()) {
				this.getManagedBean().setDemoReq(true);
			}
			//retrieveReportTabDtl(getManagedBean().getScQt(), "KT", SourcingConstants.SUPPLIER_FINALIZATION_TASK);
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	/**
	 * Add ScQtFinalizationReport
	 */
	private void addEndorsementReport(String reportTitle) {
		
		log.info("addEndorsementReport");

		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		ScQtFinalizationReport endorsementReport = new ScQtFinalizationReport();
		endorsementReport.setTitle(reportTitle);
		endorsementReport.setScQtFinalization(scQtFinalization);
		endorsementReport.setSelected(true);
		
		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
			reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
			ParameterResDTO resDTO = parameterSO
					.retrieveParameterByTypeAndCode(reqDTO);
			endorsementReport.setDefinationType(resDTO.getPmParameter());
			endorsementReport.setDocCategory(
					ParameterConst.DOCUMENT_CATEGORY_ENDORSEMENT_REPORT); // DR
		} finally {
			parameterSO = null;
		}
        this.getManagedBean().setEndorsementReport(endorsementReport); 
        this.getManagedBean().getEndorsementReports().add(endorsementReport);
		
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
		log.info("handleFileUpload");
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
		log.info("handleFileUpload");
		
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
		log.info("downloadReport");
		
		ScQtFinalizationReportAttachment reportAttachment = scQtFinalizationReport.getScQtFinalizationReportAttachment();

		if (reportAttachment == null) {
			reportAttachment = generateDocumentAttachment(scQtFinalizationReport, false);
			scQtFinalizationReport
					.setScQtFinalizationReportAttachment(reportAttachment);
		}
		downloadFile(reportAttachment);
		
		// Bug #35485@Syazani 08/02/2024 -- Start
		long downFilesSize = reportAttachment.getFileSize() != null ? reportAttachment.getFileSize() : 0;
		String docDefinitionKey = scQtFinalizationReport.getPmDocument()
				.getDocDefinitionKey();
		if (downFilesSize <= 1000 && ArrayUtils.contains(SourcingConstants.DOCUMENT_EVALUATIONS, docDefinitionKey)) {
			regenEvaluationDownload(reportAttachment, true);
		}
		// Bug #35485@Syazani 08/02/2024 -- End
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
		for (ScQtFinalizationReport report: getManagedBean().getDocumentList()) {
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
		ScQtFinalizationReport scQtReport = null;
		ScQtFinalizationReportAttachment attachment = null;
		if (briefingPaperList != null && CollectionUtils.isNotEmpty(briefingPaperList) //20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
				&& briefingPaperList.size() > 0) {
			for (int i = 0; i < briefingPaperList.size(); i++) {
				scQtReport = briefingPaperList.get(i);
				attachment = scQtReport.getScQtFinalizationReportAttachment();
				if (null != getManagedBean().getCurrentStatusId()
						&& (getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION.longValue()
								|| getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.QT_SUPPLIER_FINALIZATION_REVISION.longValue()
								|| getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.SF_PENDING_BIDDING_FINALIZATION_REVISION.longValue()
								|| getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.SF_PENDING_MULTIPLE_LOI_RECOMMENDATION_REVISION.longValue()
								)
						&& scQtReport.getTitle().equals(briefingPaperList.get(briefingPaperList.size() -1 ).getTitle())) {
					attachment = generateDocumentAttachment(scQtReport);
					scQtReport.setScQtFinalizationReportAttachment(attachment);
				    // #28834 - End.
				} else {

					if (attachment == null) {
						attachment = generateDocumentAttachment(scQtReport);
						scQtReport.setScQtFinalizationReportAttachment(attachment);
						// #28834 - Start. 
						if (getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.SF_PENDING_ENDORSEMENT.longValue()) {
							saveGeneraratedReport(scQtReport);
						}
						// #28834 - End. 
					}
				}
				scAttachments.add(attachment);
			}
		}
		
		if (decisionPaperList != null && CollectionUtils.isNotEmpty(decisionPaperList)//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
				&& decisionPaperList.size() > 0) {
			for (ScQtFinalizationReport finalizationReport : decisionPaperList) {
				attachment = finalizationReport.getScQtFinalizationReportAttachment();
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
	    // #28834 - Start.
	    ScQtFinalizationReportAttachment reportAttachment = null;
		boolean latestBriefingPaper = false;
		// #28834 - Start.
		List<ScQtFinalizationReport> briefingPaperList = null;
		for (ScQtFinalizationReport report: getManagedBean().getDocumentList()) {
			  if (report.getTitle().contains("Kertas Taklimat")) {
				  if (briefingPaperList == null) {
					  briefingPaperList = new ArrayList<>();
				  }
				  briefingPaperList.add(report);
			  }
		}
		if (briefingPaperList != null && CollectionUtils.isNotEmpty(briefingPaperList)) {//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
          ScQtFinalizationReport latestScQtFinalizationReport = briefingPaperList.get(briefingPaperList.size() -1);
          if (null != latestScQtFinalizationReport && latestScQtFinalizationReport.getTitle().equals(scQtFinalizationReport.getTitle())) {
            latestBriefingPaper = true;
          }
		}
		// #28834 - End.
		if (null != getManagedBean().getCurrentStatusId() && latestBriefingPaper
				&& (getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION.longValue()
						// 25638 - Start.
						|| getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.QT_SUPPLIER_FINALIZATION_REVISION.longValue()
						|| getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.SF_PENDING_BIDDING_FINALIZATION_REVISION.longValue()
						|| getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.SF_PENDING_MULTIPLE_LOI_RECOMMENDATION_REVISION.longValue()
						// 25638 - End.
						
						// 28834- Start.
						|| QTStatusConst.QT_SF_MOF_COMMIEEE.longValue() == getManagedBean().getCurrentStatusId().longValue()
						// 28834- End.
						)) {
			reportAttachment = generateDocumentAttachment(scQtFinalizationReport, false);
			scQtFinalizationReport.setScQtFinalizationReportAttachment(reportAttachment);
		    // #28834 - End.
		} else {
			reportAttachment = scQtFinalizationReport.getScQtFinalizationReportAttachment();

			if (reportAttachment == null) {
				reportAttachment = generateDocumentAttachment(scQtFinalizationReport, false);
				scQtFinalizationReport.setScQtFinalizationReportAttachment(reportAttachment);
				// #28834 - Start. 
				if (getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.SF_PENDING_ENDORSEMENT.longValue()) {
					saveGeneraratedReport(scQtFinalizationReport);
				}
				// #28834 - End. 
			}
		}
		downloadFile(reportAttachment);
	}
	// #18685 - End
	
	/**
	 * To download all reports under Briefing Paper/Kertas Taklimat tab
	 * 
	 * @throws Exception
	 *             the exception that would be thrown in case of any error while
	 *             downloading the report(s)
	 */
	public void downloadAllReports() throws Exception {
		log.info("downloadAllReports");

		List<ScQtFinalizationReport> scQtFinalizationReports = getManagedBean()
				.getDocumentList();
		List<ScQtFinalizationReportAttachment> scAttachments = new ArrayList<ScQtFinalizationReportAttachment>();

		for (int i = 0; i < scQtFinalizationReports.size(); i++) {
			ScQtFinalizationReportAttachment attachment = scQtFinalizationReports.get(i)
					.getScQtFinalizationReportAttachment();
			ScQtFinalizationReport qtReport = scQtFinalizationReports.get(i);
			if (attachment == null) {
				attachment = generateDocumentAttachment(qtReport, false);
				qtReport.setScQtFinalizationReportAttachment(attachment);
			}
			if(attachment != null) {
				String prefixName = qtReport.getTitle().replaceAll(
						SourcingConstants.REG_EXP_WHITE_SPACE, "");
				attachment.setTempFileName(prefixName);
			}
			scAttachments.add(attachment);
		}
		
		// Generate all the reports before submission
		if (!getManagedBean().isSubmit()) {
			downloadAllFiles("NGeP-Finalization-Report", scAttachments);
		}
	}
	
	/**
	 *Get the Endorsment Report Content.
	 * 
	 * @throws Exception
	 *             the exception that would be thrown in case of any error while
	 *             downloading the report(s)
	 */
    public StreamedContent getSuppFinalizationEndorsementReportStreamContent() throws Exception {
    	log.info("getSuppFinalizationEndorsementReportStreamContent");
		debugLogger("getSuppFinalizationEndorsementReportStreamContent");
		ScQtFinalizationReportAttachment reportAttachment = new ScQtFinalizationReportAttachment();
		reportAttachment.setDocNo(getManagedBean().getScQt().getQtNo());

		generateEndorsementReportFromVelocityToPdf(getManagedBean()
				.getScQtFinalization(), (ScAttachment) reportAttachment);
		StreamedContent streamContent = getAttachmentStreamContent(
				reportAttachment, CommonConstants.FILE_CONTENT_TYPE_PDF);

		return streamContent;
    }	
     
    /**
	 *Get the Documents Report Information.
	 * 
	 *@return the ScQtFinalizationReportAttachment instance.
	 *             
	 */
	public ScQtFinalizationReportAttachment generateDocumentAttachment(
			ScQtFinalizationReport scQtFinalizationReport, boolean isDirectDownload) throws Exception {
		log.info("generateDocumentAttachment");
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
			filename.append(scQtFinalizationReport.getTitle()
					.replaceAll(SourcingConstants.REG_EXP_WHITE_SPACE,SourcingConstants.UNDER_SCORE)
					.replaceAll(SourcingConstants.REG_EXP_BACK_SLASH,SourcingConstants.UNDER_SCORE));
			
			if (SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT.equals(docDefinitionKey)) {
				filename.append(".rtf");
				reportAttachment.setFileName(filename.toString());
				generateKertasTaklimatPdf(scQt, attachment, isDirectDownload);

			} else {
				debugLogger("No document type is exists with the key "
						+ docDefinitionKey);
				reportAttachment = null;
			}

		} 
		
		return reportAttachment;
	}
	
	private void generateKertasTaklimatPdf(ScQt qt, ScAttachment attachment, boolean isDirectDownload) 
			   throws Exception {
		log.info("generateSupplierFinalizationPdf");
		populateAttachmentDetails(attachment);
		//#35015 4ser -Start
		IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

		long qtId = qt.getQtId();
		EvaluationReqDTO req4SerDTO = new EvaluationReqDTO();
		req4SerDTO.setQtId(qtId);
		int wipstatus = evaluationSO.checkWipScoreSetting(req4SerDTO);
		String reportName ="";
		if (wipstatus <= ParameterConst.FLAG_WIP_STATUS_3SERANGKAI_BEFORE) {
			 reportName = ReportConst.SC_SUPPLIER_FINALIZATION_RPT_WIP_JSP;
		} else {
			 reportName = ReportConst.SC_SUPPLIER_FINALIZATION_RPT_JSP;
		}
		//#35015 4ser -End
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
		
		boolean isFromWebService = (null!=FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("hostUrl"));
		PmUser createdUser = isFromWebService ? retrivePmUser(new Long(1)): new FacesScopes().getUserSessionBean();

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_QUOTE_ID_EQ, qtId + "");
		paramMap.put(ReportConst.PARAM_KEY_USER_ID, createdUser.getUserId() + "");
		paramMap.put(ReportConst.PARAM_ESTIMATED_AMOUNT, estimatedAmountStr);
		StringBuilder ulasan=new StringBuilder();
		try {
		List<ScQtRecommendationUlasanRemarks> ulasanRemarks=this.getManagedBean().getScQtFinalization().getScQtRecommendationUlasanRemarks();
		if (CollectionUtils.isNotEmpty(ulasanRemarks)) {
		for(ScQtRecommendationUlasanRemarks remark:ulasanRemarks){
		if(StringUtils.isNotBlank(remark.getRemark())){
		ulasan.append(remark.getRemark() + "\n");
		}
		}
		}
		} catch (Exception e) {
		log.info("Error: " + e.getMessage());
		}
		/*StringBuilder pengesyoran=new StringBuilder();
		List<ScQtRecommendationPengesyoranRemarks> pengerysonRemarks=this.getManagedBean().getScQtFinalization().getScQtRecommendationPengesyoranRemarks();
		if (CollectionUtils.isNotEmpty(pengerysonRemarks)) {
		for(ScQtRecommendationPengesyoranRemarks remark:pengerysonRemarks){
		if(StringUtils.isNotBlank(remark.getRemark())){
		pengesyoran.append(remark.getRemark() + "\n");
		}
		}
		}*/
		paramMap.put(ReportConst.PARAM_REMARK, ulasan.length() == 0 ? 
		StringUtils.EMPTY : ulasan.toString());
		paramMap.put(ReportConst.PARAM_REPORT_OUTPUT_FORMAT, "rtf");
		//paramMap.put(ReportConst.PARAM_RECOM, pengesyoran.length() == 0 ? 
		//StringUtils.EMPTY : pengesyoran.toString());
		// #18685 - Start
		paramMap.put("flowType", "tasklist");
		// #18685 - End
		generatePdfFromOracle(reportName, paramMap, attachment, isDirectDownload);		
	}

	private void generateEndorsementReportFromVelocityToPdf(
			ScQtFinalization scQtFinalization, ScAttachment attachment)
			throws Exception {

		log.info("generateEndorsementReportFromVelocityToPdf");
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
		String title = scQt.getQtTitle();
		if (title == null) {
			title = "";
		}
		
		//replace all special characters
		//title = title.replaceAll("\\W", ""); 
		title=title.replaceAll("\r\n", "<br />");
		title=title.replaceAll("&", "&#38;");
		title=title.replaceAll("<", "&#60;");
		title=title.replaceAll(">", "&#62;");
		context.put("qtTitle", title);
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
								recommendedSuppliers = new ArrayList<QtEvaluationResultDtlBean>();
							}
							
							reportDTO.setRecommendedSuppliers(recommendedSuppliers);
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
							zoneDetailsMap = new HashMap<>();//Sonarqube #83
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
				//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
				if (zoneDetailsMap == null) {
					zoneDetailsMap = new HashMap<>();//Sonarqube #83
				}
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
		String chairPersonMinistry = null;
		List<ScCommitteeMember> commMembers = new ArrayList<ScCommitteeMember>();
		for (ScQtCommitteeActivity scQtCommitteeActivity 
					: getManagedBean().getCommitteeList()) {
			ScCommitteeMember commMember = scQtCommitteeActivity.getScCommitteeMember();
			if (commMember.getMemberRole().getParameterCode()
					.equals(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
				chairPersonName = commMember.getMemberName().toUpperCase();
				IOrgProfileSO orgProfileSO_ = null;
				try {
					orgProfileSO_ = BusinessDelegate.getInstance().getServiceObject(
										ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
					OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
					reqDTO.setOrgProfileId(commMember.getPmOrgProfile().getOrgProfileId());
					OrgProfileResDTO resDTO = orgProfileSO_.retrieveMinistryByOrgProfileId(reqDTO);
					
					if (resDTO.getPmOrgProfile() != null) {
						chairPersonMinistry = resDTO.getPmOrgProfile().getOrgName();
						chairPersonMinistry = chairPersonMinistry.toUpperCase();
					}
				} finally {
					orgProfileSO_ = null;
				}
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
		context.put("chairPersonMinistry", chairPersonMinistry);
		//context.put("secretary", secretaryName);
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
		StringBuilder pengerysonRemarks = new StringBuilder();
		List<ScQtFinalizationEndorsementRecommendationRemarks> endorsmentPengeryson=this.getManagedBean().getFinalizationPengreysonremarks();
		if (CollectionUtils.isNotEmpty(
				endorsmentPengeryson)) {
			for (ScQtFinalizationEndorsementRecommendationRemarks pengeryson
						: endorsmentPengeryson) {
				if (StringUtils.isNotBlank(pengeryson.getRemark())) {
					pengerysonRemarks.append(pengeryson.getRemark() + "\n");
				}
			}
		}
		context.put("endorsementRemarks", endorsementRemarks.length() == 0 ? 
					StringUtils.EMPTY : endorsementRemarks.toString());
		context.put("endorsementPengerysonRemarks", pengerysonRemarks.length() == 0 ? 
				StringUtils.EMPTY : pengerysonRemarks.toString());
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
		log.info("prepareRecommendedSuppliersByItems");
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
		log.info("retrieveSupplierFinalizationDtls");
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
				
				//TODO - Maintenance Screen
				List<String> rejectedRecommendDtlIds;
				if(isQTMaintenanceScreen()){
					rejectedRecommendDtlIds = null;
				}else{
					rejectedRecommendDtlIds = getRejectedRecommendDtlIds();
				}
				
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
		log.info("setRefinalizedAndBiddingAwardStatus");
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
		log.info("processShortlistedSuppliersByPackage");
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
			for (ScQtEvaluationResultDtl evalResDtl 
									: shortlistedSuppliers) {
				//35563 - start - move code to check for all recommend
				for(ScQtRecommend scQtRecommend: recommendedItems){
					// Disable selection for rejected supplier.
					boolean supplierDisabled = false;
					Long specZoneId = scQtRecommend.getTplSpecZoneDtl() != null ? 
							scQtRecommend.getTplSpecZoneDtl().getSpecZoneId() : 0L;
					if (scQtRecommend.getRecommendDtls() != null) {
						Long evaZoneId = evalResDtl.getTplSpecZoneDtl() != null ? 
								evalResDtl.getTplSpecZoneDtl().getSpecZoneId() : 0L;
								
								
						for (ScQtRecommendDtl recommendDtl 
										: scQtRecommend.getRecommendDtls()) {
							Long recommendDtlId = recommendDtl.getRecommendDtlId();
							Long supplierId = recommendDtl.getSupplier().getSupplierId();
							if (recommendDtlId != null
									&& supplierId != null
									&& supplierId.compareTo(evalResDtl
											.getQtEvaluationResult()
											.getScQtProposal().getScQtSupplier()
											.getSmSupplier().getSupplierId()) == 0
									&& specZoneId.equals(evaZoneId)
									&& (recommendDtl.getRecordStatusFlg().equals(
												DbValueConst.RECORD_STATUS_REJECTED))) {
								// 11134 - add new param
								resetSupplierSelectionAndDisable(recommendDtl, evalResDtl);
								supplierDisabled = true;
								break;
							}
						} 
						if(supplierDisabled)
							break; 
					} 
				} 
				//35563 - end - move code to check for all recommend
				
				//#33793 - start
				if (isQTRefinalized()){
					getManagedBean().setHaveSupplierRejection(true);
				}
				//#33793 - end
				evalResDtl.setAmountInWords(convertNumberToWord(
						evalResDtl.getProposalAmount().getProposalAmt()));
			}
		}
	}

	private void disableSuppliersByItem(ScQtRecommend recommend,
						List<ScQtEvaluationResultDtl> shortlistedItemSupplierList) {
		log.info("disableSuppliersByItem");
		if (recommend != null && 
				shortlistedItemSupplierList != null) {
			//33380 - start
			Map<MapKey, ScQtRecommendDtl> map = new HashMap<MapKey, ScQtRecommendDtl>();
			boolean multipleLOIandRefinalized = ParameterConst.RECOMMEND_BY_MULTIPLE_LOI.equals(getManagedBean().getFinalizationMethod()) 
					&& this.getManagedBean().getQtRefinalized(); 
			getManagedBean().setHaveSupplierRejection(false);
			if(multipleLOIandRefinalized){
				if(CollectionUtils.isNotEmpty(recommend.getRecommendDtls())){
					Long zoneDtlId =  recommend.getTplSpecZoneDtl()!=null ? recommend.getTplSpecZoneDtl().getSpecZoneId(): 0L;
					for (ScQtRecommendDtl recommendDtl: recommend.getRecommendDtls()) {
						boolean isLoiAwarded = recommendDtl.getLoiAwarded() == null ? false: recommendDtl.getLoiAwarded();
						if(recommendDtl.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE) && isLoiAwarded){
							MapKey mapkey = new MapKey(recommendDtl.getSupplier().getSupplierId(), zoneDtlId, recommend.getRequestItem().getRequestNoteItemId());
							map.put(mapkey, recommendDtl);
						} 
					}
				}
			}
			for (ScQtEvaluationResultDtl evalResultDtl: shortlistedItemSupplierList) {
				Long suppId = evalResultDtl.getQtEvaluationResult().getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierId();
				Long zoneDtlId =  evalResultDtl.getTplSpecZoneDtl()!=null ? evalResultDtl.getTplSpecZoneDtl().getSpecZoneId(): 0L;
				MapKey mapKey = new MapKey(suppId, zoneDtlId, recommend.getRequestItem().getRequestNoteItemId());
				evalResultDtl.setShortListed(false);
				if(MapUtils.isNotEmpty(map) 
						&& map.get(mapKey)!=null
						&& multipleLOIandRefinalized){
					if(map.size()>0)evalResultDtl.setShortListed(true);
				}
				evalResultDtl.setSuppSelectionDisabled(recommend.isAwardedItem());
				if (recommend.getRecommendDtls() != null) {
					Long evaZoneId = 0L; //11134
					Long specZoneId = 0L; //11134
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
								&& supplierId.equals(currentSupplierId)//#11134 - fix change to object comparator
								&& specZoneId.equals(evaZoneId)//#11134 - fix change to object comparator
								&& (recommendDtl.getRecordStatusFlg().equals(
											DbValueConst.RECORD_STATUS_REJECTED))) {
							//#11134 - add new param
							resetSupplierSelectionAndDisable(recommendDtl, evalResultDtl);
							break;
						} 
				//33380 - end
					}
				}
			}
		}
	}
	
	private void resetSupplierSelectionAndDisable(ScQtRecommendDtl recommendDtl,
					ScQtEvaluationResultDtl evalResultDtl) {
		log.info("resetSupplierSelectionAndDisable");
		evalResultDtl.setSuppSelectionDisabled(true);
		evalResultDtl.setShortListed(false);
		// #11134 - start
		populateSupplierRejectionRemark(recommendDtl, evalResultDtl);
		if(!getManagedBean().isHaveSupplierRejection()){
			boolean remarkExists = evalResultDtl.getSupplierRejectionRemark() != null
					&& StringUtils.isNotBlank(evalResultDtl.getSupplierRejectionRemark().getRemark());
			getManagedBean().setHaveSupplierRejection(remarkExists);
		}
		// #11134 - end
	}
	
	//#11134 - start
	private void populateSupplierRejectionRemark(ScQtRecommendDtl recommendDtl,
			ScQtEvaluationResultDtl evalResultDtl){
		log.info("populateSupplierRejectionRemark");
		
		List<ScLoiLoaItem> loiLoaItems = null;
		
		// 1.  get ScLoiLoaItem from recommendDtl
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			// add param to reqDTO
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			String recommendDtlIdStr = Long.toString(recommendDtl.getRecommendDtlId());
			reqDTO.setRecommendDtlIds(Arrays.asList(recommendDtlIdStr));
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.retrieveLoiLoaByRecommendDtlIds(reqDTO);
			
			if(resDTO != null && resDTO.isSuccessful()){
				loiLoaItems = resDTO.getScLoiLoaItemList();
			}
			
		} finally {
			qtSuppFinalizationSO = null;
		}

		if(loiLoaItems != null && CollectionUtils.isNotEmpty(loiLoaItems)){//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
			
			Comparator<BaseEntity> compByDateDesc =  new Comparator<BaseEntity>(){
				@Override
				public int compare(BaseEntity a, BaseEntity b) {
					
					// handle null for object
					if(a == null && b == null) return 0;
					if(a == null) return 1;
					if(b == null) return -1;
					
					// handle null for variable
					if(a.getCreatedDate() == null && b.getCreatedDate() == null) return 0;
					if(a.getCreatedDate() == null) return 1;
					if(b.getCreatedDate() == null) return -1;
					
					// date compare
					return b.getCreatedDate().compareTo(a.getCreatedDate());
				}
			};
			
			//2.  find LOIs and LOAs, distinct, order by created date desc
			List<ScLoi> loiList = new ArrayList<>();
			List<ScLoa> loaList = new ArrayList<>();
			
			Set<Long> loiUniqueChecker = new HashSet<>();
			Set<Long> loaUniqueChecker = new HashSet<>();
			
			// map and distict
			for(ScLoiLoaItem each : loiLoaItems){
				if(each.getScLoi() != null && loiUniqueChecker.add(each.getScLoi().getLoiId())){
					loiList.add(each.getScLoi());
				}
				if(each.getScLoa() != null && loaUniqueChecker.add(each.getScLoa().getLoaId())){
					loaList.add(each.getScLoa());
				}
			}
			loiUniqueChecker.clear();
			loaUniqueChecker.clear();
			
			// sort
			Collections.sort(loiList, compByDateDesc);
			Collections.sort(loaList, compByDateDesc);
			
			//3.  take Loi if find loi 1st, then take Loa(role Supplier).
			
			ScRemark supplierRejectionRemark = null;
			List<ScRemark> flattenRemarks = new ArrayList<>();
			
			boolean rejectedLoi = recommendDtl.getLoiAwarded()==null ? false: !recommendDtl.getLoiAwarded();//11134
			boolean rejectedLoa = recommendDtl.getLoaAwarded()==null ? false: !recommendDtl.getLoaAwarded();//11134
			
			if(CollectionUtils.isNotEmpty(loiList) && rejectedLoi){//11134
				for(ScLoi each : loiList){
					if(CollectionUtils.isNotEmpty(each.getScLoiRemarks())){
						for(ScLoiRemark item : each.getScLoiRemarks()){
							flattenRemarks.add(item);
						}
					}
					
					//#30008 - Start
					this.getManagedBean().setNotReachAgrm(false);
					if(each.getNegoDecision() != null){
						evalResultDtl.setNotInAgreement(each.getNegoDecision());
						this.getManagedBean().setNotReachAgrm(true);
					}//#30008 - end
				}
			} else if(CollectionUtils.isNotEmpty(loaList) && rejectedLoa){//11134
				for(ScLoa each : loaList){
					if(CollectionUtils.isNotEmpty(each.getScLoaRemarks())){
						for(ScLoaRemark item : each.getScLoaRemarks()){
							flattenRemarks.add(item);
						}
					}
				}
				
			}
			
			if(CollectionUtils.isNotEmpty(flattenRemarks)){
				
				List<Long> userIds = new ArrayList<>();
				for(ScRemark remark : flattenRemarks){
					if(remark != null && remark.getCreatedBy() != null){
						userIds.add(remark.getCreatedBy());
					}
				}
				
				// user id of supplier
				userIds = getUserSupplierListByUserIds(userIds);
				
				for(ScRemark remark : flattenRemarks){
					if(remark != null && remark.getCreatedBy() != null
							&& userIds.contains(remark.getCreatedBy())){
						supplierRejectionRemark = remark;
						break;
					}
				}
			}
			//4.  get ScRemark -> set it to evalResultDtl
			
			evalResultDtl.setSupplierRejectionRemark(supplierRejectionRemark);
		}
			
	}
	
	// #11134 - end

	private void disableAwardedItems(List<ScQtRecommend> scQtRecommends) {
		log.info("disableAwardedItems");
		//33380 - start
		List<Long> rejectedRecommendIds = getManagedBean().getRejectedScQtRecommendIds();
		rejectedRecommendIds = rejectedRecommendIds==null ? new ArrayList<Long>(): rejectedRecommendIds;
		//33380 - end
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
														DbValueConst.RECORD_STATUS_ACTIVE) 
														//33380 - start
//														&& (rejectedRecommendIds != null && 
//														!rejectedRecommendIds.contains(
//															recommend.getQtRecommendId())) 
														//33380 - end
									) ||
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
		log.info("isQTRefinalized");
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
		log.info("isQTRefinalized : "+isRefinalized);
		return isRefinalized;
	}
	
	private int findFinalizationCount() {
		log.info("findFinalizationCount");
		IScWorkFlowStatusSO scWorkFlowStatusSO = null;
		
		try {
			scWorkFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			ScWorkFlowStatusReqDTO tenderReqDTO = new ScWorkFlowStatusReqDTO();
			tenderReqDTO.setDocId(this.getManagedBean().getScQt().getQtId());	
			List<Long> statusIds = new ArrayList<Long>();
			
			//27502
			QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
			qtBackingBean.retrieveQuationTenderDetails(getManagedBean().getScQt().getQtId());
			ScQt scQt = qtBackingBean.getManagedBean().getQt();
			boolean isBpkCentralContract = false;
			isBpkCentralContract = isBpkCentralContract(scQt);

			if (isBpkCentralContract) {
				statusIds.add(QTStatusConst.SF_MOF_COMMITTEE);//#27901
				//statusIds.add(QTStatusConst.QT_SF_MOF_COMMIEEE);//#27901

			} else {
				statusIds.add(QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION);
			}
			//statusIds.add(QTStatusConst.QT_RE_FINALIZATION);
			//statusIds.add(QTStatusConst.QT_RE_EVALUATION);
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
	
    // #28834 - Strat.
	/**
	 * To find Revise Briefing COunt of QT from the Workflow Status.
	 * @return
	 */
	private int findReviseBpCount() {
      log.info("findReviseBpCount");
      IScWorkFlowStatusSO scWorkFlowStatusSO = null;
      
      try {
          scWorkFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
                  ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
          ScWorkFlowStatusReqDTO tenderReqDTO = new ScWorkFlowStatusReqDTO();
          tenderReqDTO.setDocId(this.getManagedBean().getScQt().getQtId()); 
          List<Long> statusIds = new ArrayList<Long>();
          statusIds.add(QTStatusConst.QT_PENDING_RECOMMENDATION_REVISE_BRIEFING_PAPER);
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
    // #28834 - End.
	
	private void retrieveStatusQTMaintenance(Long qtId) {
		IScWorkFlowStatusSO scWorkFlowStatusSO = null;
		
		try {
			scWorkFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			ScWorkFlowStatusReqDTO tenderReqDTO = new ScWorkFlowStatusReqDTO();
			tenderReqDTO.setDocId(qtId);	
			tenderReqDTO.setDocType(ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION);
			
			List<ScWorkFlowStatus> statusList = scWorkFlowStatusSO
							.retrieveWorkFlowStatus(tenderReqDTO)
							.getWorkFlowStatusList();
			if (statusList != null) {
				for(ScWorkFlowStatus statusTempList : statusList){
					if(statusTempList.isCurrent() == true){
						getManagedBean().setCurrentStatusId(statusTempList.getPmStatus().getStatusId());
					}
				}
			} 
		} finally {
			scWorkFlowStatusSO = null;
		}	
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
		if(rejectedRecommendDtlIds != null && !CollectionUtils.isEmpty(rejectedRecommendDtlIds)){//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
			log.info("getRejectedRecommendDtlIds size is : "+ rejectedRecommendDtlIds.size());
		}
		return rejectedRecommendDtlIds;
	}

	private void retrieveSupplierDetailsByRecommendType(
							ScQtFinalization scQtFinalization) {
		log.info("retrieveSupplierDetailsByRecommendType");
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
		log.info("createShortlistedSuppliersDataModel");
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
		if (QTConstants.SF_MULTIPLE.equals(
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
		log.info("disableItemsNotRecommendedToHigherAuthority");
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		//20200824@asyrafhaziq - 28841 - Start
		ScQtFinalization scQtFinalization  = retrieveQtFinalization(getManagedBean().getScQt());
		//20200824@asyrafhaziq - 28841 - End
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
									   loggedInUserId.equals(
											scQtFinalization.getHigherSecretary().getUserId()))))) {
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
	
	//20200824@asyrafhaziq - 28841 - Start
	private ScQtFinalization retrieveQtFinalization(ScQt scQt) {
		log.info("retrieveQtFinalization");
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

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
		if (scQtFinalization != null) {
			return 	scQtFinalization;
		}else {
			return null;
		}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	//20200824@asyrafhaziq - 28841 - End
	
	/**
	 * Method to retrieve Board of Directors Details pop up in supplier finalization
	 */
	public void showBoardofDirectors(SmSupplier selectedSupplier) {
		log.info("showBoardofDirectors");

		if (selectedSupplier == null) {
			return;
		}

		List<SmPersonnel> boardofDirectors = null;
		IPersonnelSO personnelSO = null;
		try {
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setSmSupplierId(selectedSupplier.getSupplierId());
			reqDTO.setSupplierType(ParameterConst.SUPPLIER_TYPE_CONTRACTOR);
			
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
		log.info("showReminder");

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
		log.info("checkIfLoggedinUserIsSecretary");
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
	 * the selected quotation/tender.
	 * 
	 */
	public void sendReminder() {
		log.info("sendReminder");
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
			reqDTO.setUserId(getUserIdSessionFromLoginId().toString()); // #31849 add log key
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
		log.info("signOff");
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
		log.info("getDecisionDropDown");
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
		//START - #26169
		else{
			//If no available/qualified supplier, exclude the defined decision type(s)
			if(!getManagedBean().isHasSupplierQualifiedForCurrentSelection()){	//6906 note 202
				if (excludedDecisionList == null) {
					excludedDecisionList = new ArrayList<String>();
				}
				excludedDecisionList.add(
						ParameterConst.DECISION_RECOMMEND_SUPPLIER);
				excludedDecisionList.add(
						ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY);
				//6906 note 117 - start
				if(CollectionUtils.isEmpty(this.getManagedBean().getDisqualifiedStages())){
					excludedDecisionList.add(
							ParameterConst.DECISION_RE_EVALUATION);
					excludedDecisionList.add(
							ParameterConst.DECISION_RBP);
				}
				//6906 note 117 - end
			}
		}
		//END - #26169
		if (getManagedBean().isInitialDecisionFwdToBPK() || 
				getManagedBean().isDisableFwdToHighAuthorityOption()) {
			if (excludedDecisionList == null) {
				excludedDecisionList = new ArrayList<String>();
			}
			excludedDecisionList.add(
					ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY);
			
		}
		
		//28834 starts
		
		// #28834 - Start.
		/*if (getManagedBean().isBpkCentralContract()) {
			if (excludedDecisionList == null) {
				excludedDecisionList = new ArrayList<String>();
			}
			excludedDecisionList.add(
					ParameterConst.DECISION_RBP);
		}*/
		// #28834 - End.
		
		// #28834 - Start.
		if (getManagedBean().getQtRefinalized() || getManagedBean().isBpkCentralContract()) {
			if (excludedDecisionList == null) {
				excludedDecisionList = new ArrayList<String>();
			}
			excludedDecisionList.add(
					ParameterConst.DECISION_RBP);
		}
		// #28834 - End.
		
		//28834 end
		
		
		
		/*#25729#142 Start
		 	If Desk Officer QCA & indicative price 20-50k
				hide forward to higher authority
		*/	
		/* Comment for CR:#26169 , BUG:#28682 START 
		boolean expectTotalIndPriceBetweenThreshold = expectTotalIndPriceBetweenThreshold(getManagedBean().getScQt());																				
		String CommType = getManagedBean().getCommitteeType();
		if (expectTotalIndPriceBetweenThreshold==true && 
				SourcingConstants.QCA_COMMITTEE.equals(CommType)) {
			if (excludedDecisionList == null) {
				excludedDecisionList = new ArrayList<String>();
			}
			excludedDecisionList.add(
					ParameterConst.DECISION_FORWARD_TO_HIGHER_AUTHORITY);
		}
		Comment for CR:#26169 , BUG:#28682 END  */
		//#25729#142 End
		
		if (excludedDecisionList != null && CollectionUtils.isNotEmpty( //20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
					excludedDecisionList)) {
			searchBean = new SearchBean<PmParameterDesc>();
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_IN.value(),
							excludedDecisionList));
		}
		
		//6906 note 117 - start
		if(this.getManagedBean().isNoProposalSubmitted()){
			//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
			if (excludedDecisionList == null) 
				excludedDecisionList = new ArrayList<>();//Sonarqube #83
			
			excludedDecisionList.add(
					ParameterConst.DECISION_RE_EVALUATION);	
		}
		//6906 note 117 - end
		items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.FINALIZATION_DECISION_TYPE, searchBean);
		return items;		
	}
	
	public SelectItem[] getDecisionDropDownExpiredProposal() {
		log.info("getDecisionDropDownExpiredProposal");

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_IN.value(), Arrays.asList(
						ParameterConst.DECISION_CANCEL, ParameterConst.DECISION_RE_PUBLISH)));

		SelectItem[] items = getDropdownCodeValuesBySearchBean(ParameterTypeConst.FINALIZATION_DECISION_TYPE,
				searchBean);

		return items;
	}
	
	
	public SelectItem[] getDecisionDropDownAll(){
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		SelectItem[] items = getDropdownCodeValuesBySearchBean(ParameterTypeConst.FINALIZATION_DECISION_TYPE,
				searchBean);

		return items;
		
	}
	/**
	 * Populate values for Recommend Type radio button(s) under Supplier
	 * Finalization tab
	 * 
	 * @return an array of recommend type(s)
	 */
	public SelectItem[] getRecommendTypeRadioBtn() {
		log.info("getRecommendTypeRadioBtn");
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
		log.info("getBiddingEndorsementRadioBtn");
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
		log.info("getLoiLoaPreparedByRadioBtn");
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
		log.info("getFinalizationCommitteeDropDown");
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
		log.info(":::retrieveCommitteeSecretary() commType: "+ commType);
		ICommitteSO committeSO = null;
				
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);

			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setPmOrgProfile(getManagedBean().getScQt().getPmOrgProfile());
			reqDTO.setCommitteeType(commType);
			//#26169#109 - START
			if(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A.equals(commType)){
				PmOrgProfile currUserLoginOrgProfile = getPTJOrgProfileByLoginUser();
				if(currUserLoginOrgProfile != null){
					reqDTO.setOrgProfileId(currUserLoginOrgProfile.getOrgProfileId());
					reqDTO.setIshigerCommitte(true);
				}
			}
			//#26169 - END
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
		log.info("getReasonRadioBtn");
		SelectItem[] items = null;
		BigDecimal actualIndicativePrice = this.getManagedBean().getScQt().getEstimatedAmount();//To get QT Actual Total Indicative Price
		Map<Long, String> reasonDescMap = new HashMap<Long, String>();//#26169 - 25082020
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

			/*#25729#142#190 Start
		 		If Desk Officer QCB & indicative price below 20k & offer price>50k
					enable REASON Awarded Price Exceeded Committee Authority
				else
					disable REASON Awarded Price Exceeded Committee Authority
			*/	
			int i = 0;
			items = new SelectItem[reasonList.size()];
			/*
			boolean expectTotalIndPriceBelowThreshold = false;
			String CommType = "";
			expectTotalIndPriceBelowThreshold = expectTotalIndPriceBelowThreshold(getManagedBean().getScQt(),"SF");																				
			CommType = getManagedBean().getCommitteeType();
			*/
			for (PmReasonDesc reasonDesc : reasonList) {
				//Include checking/control for #26169 #38 based on SF Decision, Forward To Higher Authority
				//#26169 - 25082020 Fix START
				if(reasonDesc.getPmReason().getReasonCode().equalsIgnoreCase(ParameterConst.FINALIZATION_REASON_CODE_EXCEED_COMMITTEE_AUTHORITY)){
					if(actualIndicativePrice.compareTo(new BigDecimal(SourcingConstants.QT_BELOW_50MILL)) > 0 ){//20230711@Asyrafhaziq-SQUBE Bug Minor #16
						//For actualIndicativePrice > RM 50,000,000.00
						//Enable & Auto Checked radio button option reason 'Awarded Price Exceeded Committee Authority'
						items[i++] = new SelectItem(reasonDesc.getPmReason().getReasonId(), reasonDesc.getReasonDesc(),"",false);
						this.getManagedBean().getScQtFinalization().getReason().setReasonId(reasonDesc.getPmReason().getReasonId());
					}else{
						//For actualIndicativePrice <= RM 50,000,000.00
						//Enable select option reason for 'Awarded Exceed Committee Authority' only if isSuppOfferPriceExceedCommunityAuthority = true
						if(this.getManagedBean().isSuppOfferPriceExceedCommunityAuthority()){
							//Has supplier offer price exceed committee authority amount.
							items[i++] = new SelectItem(reasonDesc.getPmReason().getReasonId(), reasonDesc.getReasonDesc(),"",false);
							
							/*if (expectTotalIndPriceBelowThreshold==true && 
									SourcingConstants.QCB_COMMITTEE.equals(CommType) &&
									this.getManagedBean().isSuppOfferPriceExceedCommunityAuthority()){
								items[i++] = new SelectItem(reasonDesc.getPmReason()
										.getReasonId(), reasonDesc.getReasonDesc(),"",false);
								this.getManagedBean().getScQtFinalization().getReason().setReasonId(reasonDesc.getPmReason().getReasonId());
							}*/
							
						}else{
							items[i++] = new SelectItem(reasonDesc.getPmReason().getReasonId(), reasonDesc.getReasonDesc(),"",true);
						}						
					}
				}else if(reasonDesc.getPmReason().getReasonCode().equalsIgnoreCase(ParameterConst.FINALIZATION_REASON_CODE_UNANIMOUS)){
					if(actualIndicativePrice.compareTo(new BigDecimal(SourcingConstants.QT_BELOW_50MILL)) > 0){
						//For actualIndicativePrice > RM 50,000,000.00
						//Disable option reason for 'The Decision is Unanimous'
						items[i++] = new SelectItem(reasonDesc.getPmReason().getReasonId(), reasonDesc.getReasonDesc(),"",true);
					}else{
						//For actualIndicativePrice <= RM 50,000,000.00
						//Enable option reason for 'The Decision is Unanimous'
						//Auto Checked Reason only if isSuppOfferPriceExceedCommunityAuthority = false, (No supplier offer price exceed committee authority)
						items[i++] = new SelectItem(reasonDesc.getPmReason().getReasonId(), reasonDesc.getReasonDesc(),"",false);
						if(!this.getManagedBean().isSuppOfferPriceExceedCommunityAuthority()){
							this.getManagedBean().getScQtFinalization().getReason().setReasonId(reasonDesc.getPmReason().getReasonId());
						}
					}
				}else {
					if(this.getManagedBean().isSuppOfferPriceExceedCommunityAuthority()){
						items[i++] = new SelectItem(reasonDesc.getPmReason().getReasonId(), reasonDesc.getReasonDesc(),"",false);
					}else{
						items[i++] = new SelectItem(reasonDesc.getPmReason().getReasonId(), reasonDesc.getReasonDesc(),"",true);
					}
				}
				
				reasonDescMap.put(reasonDesc.getPmReason().getReasonId(),reasonDesc.getReasonDesc());
				//#26169 - 25082020 Fix END
			}
			changeReasonEvent(null);
			//#25729#142#190 End				
		} finally {
			reasonSO = null;
		}
		return items;
	}
	
	/**
	 * Event to trigger change of reason for Forward to Higher Authority
	 */
	public void changeReasonEvent(AjaxBehaviorEvent ajaxEvent) {
		log.info("changeReasonEvent");
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
		log.info("retrieveHigherCommittees");
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
		log.info("getFinalizationMethodDropDown");
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
	 * To navigate to the selected zone tab, under Supplier Finalization tab
	 * 
	 * @param zoneTabIndex
	 *            the selected zone tab index
	 */
	public void onZoneTabChangeAction() {
		log.info("onZoneTabChangeAction");
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
		log.info("onZoneTabChangeAction - zoneTabIndex");

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
		log.info("Decision Event ");
		
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
		//Bug #29707 - start
		QTDetailBackingBean qtBackingBean =   new QTDetailBackingBean();
		qtBackingBean.retrieveQuationTenderDetails(this.getManagedBean().getScQt().getQtId());
		ScQt scQt = qtBackingBean.getManagedBean().getQt();
		boolean isBpkCentralContract = false;
		isBpkCentralContract = isBpkCentralContract(scQt);
		if (isBpkCentralContract ){
			getManagedBean().setFinalizationMethod(null);//#27502, null the 'Supplier Finalization Method' for re-evaluation 
		}
		//Bug #29707 - end
		
		Map<String,String[]> higherAuthoritiesMap 
					= getManagedBean().getHigherAuthoritiesMap();
		
		//#26169 #85 - START
		PmOrgProfile currUserLoginOrgProfile = getPTJOrgProfileByLoginUser();
		//#26169 #85 - END
		
		if (!isInitialDecisionFwdToBPK) {
			if (MapUtils.isEmpty(higherAuthoritiesMap)) {
				
				ICommitteSO committeeSO = null;
				try {
					committeeSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
		
					CommitteReqDTO reqDTO = new CommitteReqDTO();
					reqDTO.setSupplierFinalization(true);
					reqDTO.setQtId(getManagedBean().getScQt().getQtId());
					//#26169 #85 - START
					//#26169 #85 - 09092020- include checking null 
					if(currUserLoginOrgProfile != null)reqDTO.setOrgProfileId(currUserLoginOrgProfile.getOrgProfileId());
					//#26169 #85 - END
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
				
				/* Bug_28157 - Start */
				if (scQtFinalization.getHigherCommitte() != null && 
						committeeType.equals(scQtFinalization.getHigherCommitte().getParameterCode()) &&
							!scQtFinalization.getCurrentStatus().getPmStatus().getStatusId().equals(QTStatusConst.SF_PENDING_ENDORSEMENT) &&
							!scQtFinalization.getCurrentStatus().getPmStatus().getStatusId().equals(QTStatusConst.SF_PENDING_SIGN_OFF)) {	
					resetForwardToHigherAuthorityParams(scQtFinalization);
					scQtFinalization.setHigherCommitte(new PmParameter());
					scQtFinalization.setHigherSecretary(new PmUser());
					scQtFinalization.setReason(new PmReason());
				}
				/* Bug_28157 - End */
				
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
				createShortlistedSuppliersDataModel();//35140
			} 
			
			//#28834 - Start
			if(scQtFinalization.getCurrentStatus().getPmStatus().getStatusId().equals(QTStatusConst.QT_SUPPLIER_FINALIZATION_REVISION)){
				this.getManagedBean().setDisableJustifications(false);
			}
			//#28834 - End

		} else if (decision.equals(ParameterConst.DECISION_RE_EVALUATION)) {
			scQtFinalization.setRePublishOption(null);
			this.getManagedBean().setFinalizationMethod(null);
			this.getManagedBean().setReEvaluate(true);
		} else if (decision.equals(ParameterConst.DECISION_RE_PUBLISH)) {
			resetReEvaluate(scQtFinalization);
			this.getManagedBean().setFinalizationMethod(null);
			if (null == scQtFinalization.getRePublishOption()) {
				scQtFinalization.setRePublishOption(new PmParameter());
			}
			this.getManagedBean().setRePublish(true);
		} else if (decision.equals(ParameterConst.DECISION_CANCEL)) {
			this.getManagedBean().setFinalizationMethod(null);
			resetReEvaluate(scQtFinalization);
			scQtFinalization.setRePublishOption(null);
			this.getManagedBean().setCancel(true);
		}
	}
	
	// #18685 - Start
	/**
	 * @return
	 */
	public List<ScRemark> getPendingRecRemark() {
	  if (CollectionUtils.isEmpty(getManagedBean().getScRemarks())) {
	    List<ScRemark> remarkList = new LinkedList<>(); 
	    // #25638 - Start.
	    remarkList.addAll(getManagedBean().getNewSfRemarks());
	    getManagedBean().setScRemarks(remarkList);
	  } else {
	    getManagedBean().getScRemarks().clear();
	    getManagedBean().getScRemarks().addAll(getManagedBean().getNewSfRemarks());
	  }
        // #25638 - End.
	  String decision = getManagedBean().getScQtFinalization().getDecision().getParameterCode();
	  if (StringUtils.isNotEmpty(decision) && !decision.equals(ParameterConst.DECISION_RECOMMEND_SUPPLIER)) {
	    getManagedBean().getScRemarks().addAll(getManagedBean().getScQtFinalization().getScQtRecommendationUlasanRemarks());
	  } else {
		// #18685 - Start.
	    if (getManagedBean().isItemize()) {
	    	if (CollectionUtils.isNotEmpty(getManagedBean().getPendingRecItemizedRemark())) {
	    		getManagedBean().getScRemarks().addAll(getManagedBean().getPendingRecItemizedRemark());
	    	}
	    } else {
	    	if (CollectionUtils.isNotEmpty(getManagedBean().getPendingRecPackageRemark())) {
	    		getManagedBean().getScRemarks().addAll(getManagedBean().getPendingRecPackageRemark());
	    	}
	    }
	    // #18685 - End.
	  }
	  return getManagedBean().getScRemarks();
	}
	// #18685 - End.
	
	private void resetDecisionRecommendSupplier(ScQtFinalization scQtFinalization) {
		log.info("resetDecisionRecommendSupplier");
		scQtFinalization.setHigherCommitte(null);
		scQtFinalization.setHigherSecretary(null);
		scQtFinalization.setLoiLoaBy(null);
		scQtFinalization.setReason(null);

		resetFlag();
	}

	private void resetReEvaluate(ScQtFinalization scQtFinalization) {
		log.info("resetReEvaluate");
		scQtFinalization.setReEvaluateCommitte(null);
		scQtFinalization.setReEvaluateCompReq(false);
		scQtFinalization.setReEvaluateTechReq(false);
		scQtFinalization.setReEvaluateFinReq(false);
	}

	private void resetDecisionForwardToHigherAuthority(ScQtFinalization scQtFinalization) {
		log.info("resetDecisionForwardToHigherAuthority");
		resetForwardToHigherAuthorityParams(scQtFinalization);
		scQtFinalization.setHigherAuthReq(false);
		this.getManagedBean().setForwardToBpk(false);
		this.getManagedBean().setForwardToHigherAuthority(false);
	}
	
	private void resetForwardToHigherAuthorityParams(ScQtFinalization scQtFinalization) {
		log.info("resetForwardToHigherAuthorityParams");
		scQtFinalization.setReason(null);
		scQtFinalization.setReasonStr(null);		
		scQtFinalization.setHigherSecretary(null);
		scQtFinalization.setHigherSecretaryStr(null);
		scQtFinalization.setHigherCommitte(null);
		scQtFinalization.setHigherCommitteStr(null);
	}

	private void resetRepublish(ScQtFinalization scQtFinalization) {
		log.info("resetRepublish");
		scQtFinalization.setRePublishOption(null);
	}

	private void resetCancel(ScQtFinalization scQtFinalization) {
		log.info("resetCancel");
	}

	private void resetFlag() {
		log.info("resetFlag");
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
		log.info("resetRecommendFlag");
		this.getManagedBean().setOnlineBidding(false);
		this.getManagedBean().setMultipleLoi(false);
		this.getManagedBean().setMultipleAward(false);
	}	
	
	private void resetNotificationFields() {
		log.info("resetNotificationFields");
		this.getManagedBean().setSubject(null);
		this.getManagedBean().setMessage(null);
	}

	private void createMainMenuModel(ScQt scQt) { //#27502
      log.info("createMainMenuModel");
      MenuModel mainMenuModel = new DefaultMenuModel();

      boolean isBPKUser = false;
      if (getManagedBean().isInitialDecisionFwdToBPK()) {
          isBPKUser = true;
      }

      int tabIndex = 0;
      MenuItem menuItem = null;
      String tabMenuChangeCommonAction = "onMainTabMenuChangeAction([ {name : 'selectedTabIndex', value : ";
      
      // #27502 -- Start
      boolean isBpkCentralContract = false;
      isBpkCentralContract = isBpkCentralContract(scQt);
      if (isBpkCentralContract) {

          // Briefing Paper Menu Item
          menuItem = new MenuItem();
          menuItem.setValue(ResourceBundleUtils.getMessage("lbl.sc.briefing.paper.preparation",
                  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
          menuItem.setStyle("width:15%;");
          menuItem.setTransient(true);
          menuItem.setPartialSubmit(true);
          menuItem.setProcess("@form");
          menuItem.setUpdate("@form");
//        menuItem.setOncomplete(tabMenuChangeCommonAction + (tabIndex++) + "}]); ");
          menuItem.setOncomplete(tabMenuChangeCommonAction + (isBPKUser ? 0 : (2)) + "}]); ");
          mainMenuModel.addMenuItem(menuItem);

          // Supplier Finalization Menu Item
          menuItem = new MenuItem();
          menuItem.setValue(ResourceBundleUtils.getMessage("lbl.sc.meeting.decision",
                  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
          menuItem.setStyle("width:20%;");
          // menuItem.setDisabled(isQTMaintenanceScreen());
          menuItem.setTransient(true);
          menuItem.setPartialSubmit(true);
          menuItem.setProcess("@form");
          menuItem.setUpdate("@form");
//        menuItem.setOncomplete(tabMenuChangeCommonAction + (tabIndex++) + "}]); ");
          menuItem.setOncomplete(tabMenuChangeCommonAction + (isBPKUser ? 1 : (3)) + "}]); ");
          mainMenuModel.addMenuItem(menuItem);

          menuItem = new MenuItem();
          menuItem.setValue(ResourceBundleUtils.getMessage("lbl.sc.committee.endorsement.report",
                  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
          menuItem.setStyle("width:20%;");
          menuItem.setTransient(true);
          menuItem.setPartialSubmit(true);
          menuItem.setProcess("@form");
          menuItem.setUpdate("@form");        
          //menuItem.setOncomplete(tabMenuChangeCommonAction + (tabIndex++) + "}]); ");
          menuItem.setOncomplete(tabMenuChangeCommonAction + (isBPKUser ? 2 : (5)) + "}]); ");
          mainMenuModel.addMenuItem(menuItem);
          
          navigateToSupplierFinalizationTab();

          // #27502 -- End

      } else {

          // Preparation Menu Item
          if (!isBPKUser) {
              menuItem = new MenuItem();
              menuItem.setValue(ResourceBundleUtils.getMessage("lbl.sc.preparation",
                      ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
              menuItem.setStyle("width:15%;");
              // menuItem.setDisabled(isQTMaintenanceScreen());
              menuItem.setTransient(true);
              menuItem.setPartialSubmit(true);
              menuItem.setProcess("@form");
              menuItem.setUpdate("@form");
              menuItem.setOncomplete(tabMenuChangeCommonAction + (tabIndex++) + "}]); ");
              mainMenuModel.addMenuItem(menuItem);
          }

          // Evaluation Report
          menuItem = new MenuItem();
          menuItem.setValue(ResourceBundleUtils.getMessage("lbl.sc.evaluation.report",
                  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
          menuItem.setStyle("width:15%;");
          menuItem.setTransient(true);
          menuItem.setPartialSubmit(true);
          menuItem.setProcess("@form");
          menuItem.setUpdate("@form");
          // #28240-issued fixed
          menuItem.setOncomplete(tabMenuChangeCommonAction + (isBPKUser ? 0 : (tabIndex++)) + "}]); ");
          mainMenuModel.addMenuItem(menuItem);

          // Briefing Paper Menu Item
          menuItem = new MenuItem();
          menuItem.setValue(ResourceBundleUtils.getMessage("lbl.sc.briefing.paper.preparation",
                  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
          menuItem.setStyle("width:15%;");
          menuItem.setTransient(true);
          menuItem.setPartialSubmit(true);
          menuItem.setProcess("@form");
          menuItem.setUpdate("@form");
          menuItem.setOncomplete(tabMenuChangeCommonAction + (isBPKUser ? 1 : (tabIndex++)) + "}]); ");
          mainMenuModel.addMenuItem(menuItem);

          // Supplier Finalization Menu Item
          menuItem = new MenuItem();
          menuItem.setValue(ResourceBundleUtils.getMessage("lbl.sc.meeting.decision",
                  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
          menuItem.setStyle("width:20%;");
          // menuItem.setDisabled(isQTMaintenanceScreen());
          menuItem.setTransient(true);
          menuItem.setPartialSubmit(true);
          menuItem.setProcess("@form");
          menuItem.setUpdate("@form");
          menuItem.setOncomplete(tabMenuChangeCommonAction + (isBPKUser ? 2 : (tabIndex++)) + "}]); ");
          mainMenuModel.addMenuItem(menuItem);

          // Supplier Finalization Committee Endorsement Menu Item
          if (!isBPKUser) {
              menuItem = new MenuItem();
              menuItem.setValue(ResourceBundleUtils.getMessage("lbl.sc.committee.endorsement.finalization",
                      ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
              menuItem.setStyle("width:15%;");
              // menuItem.setDisabled(isQTMaintenanceScreen());
              menuItem.setTransient(true);
              menuItem.setPartialSubmit(true);
              menuItem.setOncomplete(tabMenuChangeCommonAction + (tabIndex++) + "}]); ");
              mainMenuModel.addMenuItem(menuItem);

              menuItem = new MenuItem();
              menuItem.setValue(ResourceBundleUtils.getMessage("lbl.sc.committee.endorsement.report",
                      ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
              menuItem.setStyle("width:20%;");
              menuItem.setTransient(true);
              menuItem.setPartialSubmit(true);
              menuItem.setProcess("@form");
              menuItem.setUpdate("@form");
              menuItem.setOncomplete(tabMenuChangeCommonAction + (tabIndex++) + "}]); ");
              mainMenuModel.addMenuItem(menuItem);
          }

          /*
           * Enhancement #21694 Supplier Finalization Menu Item
           */
          if (null != getManagedBean().getScQtReverse()) {
              menuItem = new MenuItem();
              menuItem.setValue(ResourceBundleUtils.getMessage("lbl.sc.committee.reverseStatus",
                      ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
              menuItem.setStyle("width:20%;");
              // menuItem.setDisabled(isQTMaintenanceScreen());
              menuItem.setTransient(true);
              menuItem.setPartialSubmit(true);
              menuItem.setProcess("@form");
              menuItem.setUpdate("@form");
              menuItem.setOncomplete(tabMenuChangeCommonAction + (isBPKUser ? 2 : (tabIndex++)) + "}]); ");
              mainMenuModel.addMenuItem(menuItem);

          }
          /*
           * Enhancement #21694 END
           */
      }
      
      this.getManagedBean().setMainMenuModel(mainMenuModel);
  }   

	private void createZoneMenuModel() {
		log.info("createZoneMenuModel");
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
		log.info("populateScQtRecommendsByZone");
		ScQtFinalization scQtFinalization = this.getManagedBean()
				.getScQtFinalization();
		TplSpecZoneDtl selectedZone = this.getManagedBean().getCurrentZone();
		// #28250 - Start - Added Null check
		if (selectedZone != null && null != scQtFinalization && null != scQtFinalization.getScQtRecommendsByZone()) {
		// #28250 - End
			//this.getManagedBean().setItemList(
			//		scQtFinalization.getScQtRecommendsByZone()
			//				.get(selectedZone.getSpecZoneId()));
			//#32400 - Start
			this.getManagedBean().setItemList(
					sortingItemizedFollowedOrderByQtId(
							scQtFinalization.getScQtRecommendsByZone().get(
									selectedZone.getSpecZoneId()),this.getManagedBean().getScQt()));
			//#32400 - End
		}
	}

	private void resetItemDetails() {
		log.info("resetItemDetails");
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
		log.info("prepareToDeleteRecommendDtls");
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
		log.info("recommendTypeEvent");
		this.getManagedBean().setOnlineBidding(false);
		this.getManagedBean().setMultipleLoi(false);
		this.getManagedBean().setItemCancelled(false);
		this.getManagedBean().setDisableSupplierSelection(false);
		this.getManagedBean().setRecommendedSupplier(null);
		this.getManagedBean().setShortlistedSuppliersByRecommendType(null);
//		this.getManagedBean().setZoneActiveIndex(0);//#35064 let active zone stay at current zone instead of reset
		populateRecommendTypeEventDetails(true);
	}

	private void populateRecommendTypeEventDetails(boolean triggeredFromEvent) {
		log.info("populateRecommendTypeEventDetails");
		this.getManagedBean().setSelectedItem(null);
		boolean itemized = false;
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		String recommendType = scQtFinalization.getRecommendedTypeId()
				.getParameterCode();
		String finalizationMethod = getManagedBean().getFinalizationMethod();
		//29707
		//#33174 disable package if there is no supplier qualified by package #21695 - Start
		disablePacSF(this.getManagedBean().getScQt().getQtId());
		//#33174 disable package if there is no supplier qualified by package #21695 - End
		if ( StringUtils.isBlank(finalizationMethod)){
			finalizationMethod =  scQtFinalization.getFinalizationMethod().getParameterCode();
		}
		// Disable award by package when procurement category is medicine
		//getManagedBean().isProcCategoryMedicine()
		if (SourcingConstants.EVALUATE_BY__ITEM.equalsIgnoreCase(this.getManagedBean().getScQt().getEvaluateBy())) {
			PmParameter itemRecommendType = getPmParameterByTypeAndCode(
					ParameterTypeConst.SC_RECOMMEND_TYPE,
					ParameterConst.RECOMMEND_TYPE_ITEM);
			scQtFinalization.setRecommendedTypeId(itemRecommendType);
			recommendType = itemRecommendType.getParameterCode();
		}
		
		itemized = ParameterConst.RECOMMEND_TYPE_ITEM
						.equals(recommendType) ? true : false;
		this.getManagedBean().setItemize(itemized);
		checkSFHasQualifiedSupplier();
		setSelectionMode(finalizationMethod);
		if (!triggeredFromEvent) {
			loadRecommededSuppliersIntoCachedData(itemized);	
		}
		
		if (itemized) {
			if (isZonal) {
				populateScQtRecommendsByZone();
			} else {
				//this.getManagedBean().setItemList(
				//		scQtFinalization.getScQtRecommends());
				//#32400 - Start
				this.getManagedBean().setItemList(
						sortingItemizedFollowedOrderByQtId(
								scQtFinalization.getScQtRecommends(),this.getManagedBean().getScQt()));
				//#32400 - End
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
		//#26169 #38 #25729#142 - Start 
		checkSupplierOfferPriceByItemIsExceedCommitteeAuthority();
		//#26169 #38 #25729#142 - End
	}

	private void setSelectedSupplierCodesByItem() {
		log.info("setSelectedSupplierCodesByItem");
		boolean isAward = getManagedBean().isSingleAward();
		
		List<ScQtRecommend> items = getManagedBean().getItemList();
		// #28250 - Start - Added Null check
		if (CollectionUtils.isNotEmpty(items)) {
		// #28250 - End
			for (ScQtRecommend item : items) {
				Long specZoneId = null;
				if (item.getTplSpecZoneDtl() != null) {
					specZoneId = item.getTplSpecZoneDtl().getSpecZoneId();
				} else {
					specZoneId = DEFAULT_ZONE;
				}
				
				Long itemId = item.getRequestItem().getRequestNoteItemId();
				item.setSelectedSuppliers(new HashMap<Integer,SmSupplier>());
				
				List<QtEvaluationResultDtlBean> cachedSuppliersList = getCachedSuppliersFromMap(specZoneId, itemId);	
				if (CollectionUtils.isNotEmpty(cachedSuppliersList)) {			
					for (QtEvaluationResultDtlBean recommendedBean : cachedSuppliersList) {
						if (recommendedBean != null) {
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
		// #28250 - Start
		}
		// #28250 - End
		//#26169 Start comment this line
		// #25729#142 & #26169 - Start
		//checkSupplierOfferPriceByItemIsExceedCommitteeAuthority();
		// #25729#142 & #26169 - End
		//#26169 - End comment
	}
	
	private void setSelectionMode(String finalizationMethod) {
		log.info("setSelectionMode");
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
		log.info("reEvaluateByOptionEvent");
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
		log.info("finalizationMethodEvent ");
		
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
		log.info("checkIfAtleastTwoSuppliersExist ");
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
		log.info("itemCancellationEvent ");
		
		if (scQtRecommend.isCancelled()) {
			this.getManagedBean().setItemCancelled(true);
			this.getManagedBean().setDisableSupplierSelection(false);
			
			// clear the RecommendDtl list
			if (scQtRecommend.getRecommendDtls() != null) {
				scQtRecommend.setAwardSupplierList(
						new ArrayList<ScQtEvaluationResultDtl>());
				prepareToDeleteRecommendDtls(scQtRecommend);
				// reset flag value of is Awarded and shortlisted in evalResultDtls
				Long currentZoneId = this.getManagedBean()
						.getCurrentZone() != null ? this.getManagedBean()
								.getCurrentZone().getSpecZoneId():DEFAULT_ZONE;
				MapKey mapKey = new MapKey(currentZoneId, scQtRecommend
						.getRequestItem().getRequestNoteItemId());
				List<QtEvaluationResultDtlBean> cachedSuppliers = 
						getManagedBean().getQualifiedSuppliersCache()
										.get(mapKey);
				if (CollectionUtils.isNotEmpty(cachedSuppliers)) {
					for (QtEvaluationResultDtlBean cachedSupplier 
												: cachedSuppliers) {
						cachedSupplier.setShortlisted(false);
						cachedSupplier.setAwarded(false);
					}
				}
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
		log.info("zonalPackageItemsCancellationEvent");
		
		this.getManagedBean().setDisableSupplierSelection(false);
		boolean zonalPackageItemsCancelled = getManagedBean().isItemCancelled();
		
		List<ScQtRecommend> scQtRecommends = null;
		Long currentZoneId = getManagedBean().getCurrentZone().getSpecZoneId();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		Map<Long,List<ScQtRecommend>> zonalItems = scQtFinalization.getScQtRecommendsByZone();
		
		if (MapUtils.isNotEmpty(zonalItems) && 
				zonalItems.containsKey(currentZoneId)) {
			scQtRecommends = zonalItems.get(currentZoneId);
		}		
		
		if (scQtRecommends != null && CollectionUtils.isNotEmpty(scQtRecommends)) {//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
			if (zonalPackageItemsCancelled) {
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
		log.info("fwdHigherAuthorityEvent");

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
					log.info("Inside issecretaryuser ==");
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
		log.info("finalizationCommitteeEvent");

		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		Long paramId = scQtFinalization.getHigherCommitte().getParameterId();
		PmParameter parameter = ParameterHelper.getInstance()
				.getPmParameterById(paramId);
		scQtFinalization.setHigherCommitte(parameter);
		scQtFinalization.setHigherCommitteStr(parameter.getParameterCode());
		scQtFinalization.setHigherSecretary(new PmUser());
		// #29921 - Start.
		getManagedBean().getScQt().setFinalizationCommitte(parameter);
		// #29921 - End.
	}

	/**
	 * Event to select an item where Recommend Type is Itemized under Supplier
	 * Finalization tab
	 * 
	 * @param event
	 *            the event to set
	 */
	public void selectItemEvent(SelectEvent event) {
		log.info("selectItemEvent - SelectEvent");

		Long specZoneId = null;
		if (getManagedBean().getScQt().isZonal()) {
			specZoneId = getManagedBean().getCurrentZone().getSpecZoneId();
		}
		ScQtFinalization scQtFinalization = 
							getManagedBean().getScQtFinalization();
		//35157 - start
		ScQtRecommend scQtRecommend = null;
		if(null == event && null!=this.getManagedBean().getLastSelectedItem()){
			scQtRecommend = getManagedBean().getLastSelectedItem();
		}else if(null != event){
			scQtRecommend = (ScQtRecommend) event.getObject();
			this.getManagedBean().setLastSelectedItem(scQtRecommend);
		}
		//35157 - end
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
		log.info("selectItemEvent - ScQtRecommend");
		getManagedBean().setSelectedItem(null);
		getManagedBean().setItemRowSelect(false);
		addSelectedItem(scQtRecommend);
		this.getManagedBean().setLastSelectedItem(scQtRecommend);//35157
	}

	private void addSelectedItem(ScQtRecommend scQtRecommend) {
		log.info("addSelectedItem");
		this.getManagedBean().setSelectedItemAlreadyAwarded(false);
		
		if (scQtRecommend.isCancelled()) {
			this.getManagedBean().setItemCancelled(true);
		} else {
			this.getManagedBean().setItemCancelled(false);
		}
		
		if (this.getManagedBean().getFinalizationMethod() == null) {//#27502, Throw error if 'Supplier Finalization Method' == null
			throw new EpException(MessageConst.ERROR_760211);					
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
		//33380 - start
		int recommendCount = 0;
		boolean multipleLOIandRefinalized = ParameterConst.RECOMMEND_BY_MULTIPLE_LOI.equals(getManagedBean().getFinalizationMethod()) 
				&& this.getManagedBean().getQtRefinalized(); 
		if(CollectionUtils.isNotEmpty(scQtRecommend.getRecommendDtls()) && multipleLOIandRefinalized){
			for (ScQtRecommendDtl recommendDtl: scQtRecommend.getRecommendDtls()) {
				boolean isLoiAwarded = recommendDtl.getLoiAwarded() == null ? false: recommendDtl.getLoiAwarded();
				if(recommendDtl.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE) && isLoiAwarded){
					recommendCount++;
				}
			}
			this.getManagedBean().setSelectionMode(recommendCount > 0 ? QTConstants.SELECTION_MODE_MULTIPLE : QTConstants.SELECTION_MODE_SINGLE);
		}
		//33380 - end
		if (scQtRecommend.isSelected()) {
			getManagedBean().setSelectedItem(scQtRecommend);
			getManagedBean().getSelectedItemList().add(scQtRecommend);
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
		log.info("selectAllItemsEvent");

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
						this.getManagedBean().setLastSelectedItem(scQtRecommend);//35157
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
		log.info("unselectItemEvent");

		ScQtRecommend scQtRecommend = (ScQtRecommend) event.getObject();
		scQtRecommend.setSelected(false);
		selectItemEvent(scQtRecommend);
	}	
	
	/**
	 * Event to award supplier for an item [for Selection Based On - Itemized]
	 * Finalization of Supplier Selection tab
	 */
	public void recommendSupplierByAwardEvent(SelectEvent selectEvent) {
		log.info("recommendSupplierByAwardEvent");
		ScQtEvaluationResultDtl recommendedSupplier = 
					this.getManagedBean().getRecommendedSupplier();

		selectSupplierEvent(recommendedSupplier, true);
	}

	/**
	 * Event for supplier selection where Recommend Type is Itemized under
	 * Supplier Finalization tab
	 */
	public void selectSupplierEvent(ScQtEvaluationResultDtl evaluationResultDtl, 
									boolean isAwarded) {
		log.info("selectSupplierEvent");

		boolean isSelected = false;
		boolean isSecretaryUser = this.getManagedBean().isSecretaryUser();
		boolean isItemized = this.getManagedBean().isItemize();
		
		Integer supplierCode = evaluationResultDtl.getQtEvaluationResult()
									    .getScQtProposal().getSupplierNo();
		SmSupplier smSupplier = evaluationResultDtl.getQtEvaluationResult()
									.getScQtProposal().getScQtSupplier().getSmSupplier();
		if (isAwarded) {
			isSelected = isAwarded;
			evaluationResultDtl.setShortListed(false);
		} else {
			isSelected = evaluationResultDtl.isShortListed();
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
			if (isItemized) {
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
			updateCachedData(evaluationResultDtl, selectedZoneId, selectedItemId, isAwarded);
		} 
	}
	
	/**
	 * Update the supplier selection in the cached list
	 */
	private QtEvaluationResultDtlBean updateCachedData(ScQtEvaluationResultDtl selectedSupplier,
								  					   Long specZoneId, Long requestedItemId, Boolean isAwarded) {
		log.info("updateCachedData");
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
		
		if (isAwarded != null) {
			recommendedBean.setAwarded(isAwarded);	
		}
		
		/*
		 * BUG 31459 START
		 * UNCHECK BOOLEANCHECKBOX IN MULTIPLE LOI ITEM
		 */
		if(selectedSupplier.getEvaluationResultDtlId()!=null && selectedSupplier.getQtEvaluationResult().getScQtProposal()!=null){
			if(!selectedSupplier.getQtEvaluationResult().getScQtProposal().getScQtSupplier().getSmSupplier().isDisciplinaryActions()
					// #26247 - #32935 - Start.
					|| selectedSupplier.getQtEvaluationResult().getScQtProposal().getScQtSupplier().getSmSupplier().isWarningDA()
					// #26247 - #32935 - End.
					){
				recommendedBean.setShortlisted(selectedSupplier.isShortListed());				
			}
		}		
		/*
		 * BUG 31459 END
		 */
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
		log.info("setOnlineBiddingFlag");
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
		log.info("calculateTotalIntendedPrice");
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
							//#30492 - start
							intendedPrice = null;
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
								
								//To-Do here
								
								break;
							}
							//#30492 - end
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
							//#30492 - start
							intendedPrice = null;
							for (QtEvaluationResultDtlBean evalResultDtlBean 
														   : cachedSuppliers) {
								if (evalResultDtlBean.isAwarded()) {
									intendedPrice = null;
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
							
							if (intendedPrice != null) {
								totalIntendedPrice = totalIntendedPrice.add(intendedPrice);
								totalIntendedPrice = totalIntendedPrice.setScale(2,
														BigDecimal.ROUND_HALF_EVEN);
							}
							
							//#30492 - end
						}
					}
				}
			}
			
		}
		
		return totalIntendedPrice;
	}
	
	private void calculateOfferedPrice(QtEvaluationResultDtlBean evalResDtlBean,
									   ScQtRecommend scQtRecommend) {
		log.info("calculateOfferedPrice");
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
		log.info("convertNumberToWord");
		String amountInWords = null;
		if (amount != null) {
			amountInWords = WordUtils.capitalizeFully(
							NumberWordUtil.convert(amount.toString()));
		}
		return amountInWords;
	}
	
	private void calculateBidPrice(List<ScQtRecommend> recommendedItems, 
								   QtEvaluationResultDtlBean evalResDtlBean) {
		log.info("calculateBidPrice");
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

	private List<ScQtEvaluationResultDtl> retrieveRecommendedSuppliers(Long specZoneId, Long requestItemId) {
		log.info("retrieveRecommendedSuppliers");
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			
			String evaluationStage = null;
			if (requestItemId != null && !DEFAULT_ITEM.equals(requestItemId))  {
				evaluationStage = SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM;
			} else {
				evaluationStage = SourcingConstants.EVAL_STAGE_RECOMMENDATION_PKG;
			}
			
			ISearchBean<ScQtEvaluationResultDtl> searchBean = 
							new SearchBean<ScQtEvaluationResultDtl>();
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("qtEvaluationResult.evaluationStage",
							SearchOperator.OPERATOR_EQUALS.value(), evaluationStage));
			
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setSpecZoneId(DEFAULT_ZONE.equals(specZoneId) ? null : specZoneId);
			if (null != requestItemId && !DEFAULT_ITEM.equals(requestItemId)) {
				reqDTO.setRequestItemId(requestItemId);
			}
			reqDTO.setFilterByRecommendedSuppliers(true);
			reqDTO.setSearchBean(searchBean);
			
			/*
			 * Development 32311: SLA LOG START
			 */
			long startTime = System.currentTimeMillis();
			List<ScQtEvaluationResultDtl> scQtEvalResultDtlList = new ArrayList<>();
			scQtEvalResultDtlList = qtSuppFinalizationSO.retrieveQualifiedSuppliersInfoByEvalStage(reqDTO).getQualifiedSuppliersByEvalStage().getResultList();
			SLAUtils.getInstance().logSQLPerformanceInfo("QT","32311(E)-"+this.getManagedBean().getScQt().getQtId(),"QTSuppFinalizationSO.retrieveQualifiedSuppliersInfoByEvalStage", startTime,"#32311");
			/*
			 * Development 32311: SLA LOG END
			 */
			
			return scQtEvalResultDtlList;
			
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	private PmParameter getPmParameterByTypeAndCode(String parameterType,
			String parameterCode) {
		log.info("getPmParameterByTypeAndCode");
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
		log.info("awardSupplierEvent");

		boolean qtRefinalized = isQTRefinalized();
		Object[] supplierInfo = getManagedBean().getSelectedCommonSupplier();
		List<ScQtRecommend> selectedItemList 
				= this.getManagedBean().getSelectedItemList();
		if (selectedItemList == null
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
			Long supplierId = ((BigDecimal)supplierInfo[0]).longValue();
			Long qtId = getManagedBean().getScQt().getQtId();
			// get the SmSupplier object by the dropdown selection
			for (ScQtRecommend scQtRecommend : selectedItemSet) {
				if (!scQtRecommend.isSelected()) {
					removableItem = scQtRecommend;
					continue;
				}
				
				this.getManagedBean().getSelectedItemList().clear();
				this.getManagedBean().getSelectedItemList().add(scQtRecommend);
				this.getManagedBean().setSelectedItem(scQtRecommend);
				
				IQTSuppFinalizationSO qtSuppFinalizationSO = null;
				try {
					qtSuppFinalizationSO = getQTSuppFinalizationSO();
					SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
					reqDTO.setQtId(qtId);
					reqDTO.setRecommendType(SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM);
					reqDTO.setRequestItemId(scQtRecommend.getRequestItem()
							.getRequestNoteItemId());
					reqDTO.setScQtFinalization(this.getManagedBean()
							.getScQtFinalization());
					reqDTO.setSupplierId(supplierId);
					TplSpecZoneDtl specZoneDtl = this.getManagedBean().getCurrentZone();
					if(specZoneDtl != null){
						reqDTO.setQtZoneCriteriaId(specZoneDtl.getSpecZoneId());
					}
					SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
							.retrieveEvaluationResultsByItem(reqDTO);
					List<ScQtEvaluationResultDtl> selectedQualSuppliers = resDTO
							.getQualifiedByItemSuppliers();
					if (CollectionUtils.isNotEmpty(selectedQualSuppliers)) {
						for (ScQtEvaluationResultDtl evalResultDtl : selectedQualSuppliers) {
							//35157 - start
							if(!this.evaluateSupplierDisabled(evalResultDtl)){
								evalResultDtl.setShortListed(isShortlisted); 
								selectSupplierEvent(evalResultDtl, isAwarded);
							}
							//35157 - end
						}

					}
				} finally {
					qtSuppFinalizationSO = null;
				}
				
				/*List<ScQtEvaluationResultDtl> shortListedSupplierList 
								= scQtRecommend.getShortListedSupplierList();
				if (CollectionUtils.isNotEmpty(shortListedSupplierList)) {
					for (ScQtEvaluationResultDtl evalResultDtl 
											: shortListedSupplierList) {
						String suppId = String.valueOf(evalResultDtl
								.getQtEvaluationResult().getScQtProposal()
								.getScQtSupplier().getSmSupplier().getSupplierId());
						if (suppId.equals(selectedCommSupplier.getScQtProposal()
								.getScQtSupplier().getSmSupplier().getSupplierId())) {
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
				}*/
			}
			if (removableItem != null) {
				selectedItemSet.remove(removableItem);
			}

			this.getManagedBean().setSelectedItemList(null);
			this.getManagedBean().setAwardSupplierList(null);
			this.getManagedBean().setAwardSupplier(null);
			this.getManagedBean().setSelectedItem(null);
			selectItemEvent((SelectEvent)null);//35157
		} else {
			log.info("There are no selected items");
			return;
		}
	}
	
	private boolean isRecommendByAward() {
		log.info("isRecommendByAward");
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
		log.info("validateByPackage");
		boolean isAward = false;
		boolean supplierSelected = true;
		boolean minSuppliersExist = true;
		int isNotSelectedRadioButton = 0;//#28834
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
				//#21695 allow no records validation start
				if(CollectionUtils.isNotEmpty(
						scQtRecommend.getRecommendDtls())){
					throw new EpException(MessageConst.ERROR_760206);
				}
				//#21695 allow no records validation end
				//#28834 - Start
				isNotSelectedRadioButton++;
				//#28834 - End
			}
		}	
		
		//#28834 - Start
		if(!supplierSelected && (isNotSelectedRadioButton > 0)){ 
		   throw new EpException(MessageConst.ERROR_760206);
		} 
		//#28834 - End
	}

	private void validateAwardOfItem(ScQtRecommend scQtRecommend) {
		log.info("validateAwardOfItem");
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
	 * Get the list of supplier codes.
	 * @return the list of codes.
	 */
	public List<Integer> getSupplierCodes(ScQtRecommend item) {
		log.info("getSupplierCodes");
		return new ArrayList<Integer>(
					item.getSelectedSuppliers().keySet());
	}

	private void setZoneAndTabIndexOnException(TplSpecZoneDtl zoneDtl) {
		log.info("setZoneAndTabIndexOnException");
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
		log.info("isItemPriceTypeTariffOrTier");
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
		log.info("hasTierPriceInMixtureOfItems");
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
		StringBuilder msg = new StringBuilder(message.toString());
		if (getManagedBean().getScQt() != null) {
			msg.append(" qtId:" + getManagedBean().getScQt().getQtId());
		}
		if (log.isDebugEnabled()) {
			log.debug(msg);
		}
		else if(log.isInfoEnabled()){
			log.info(msg);
		}

	}

	/**
	 * Get the currentTab
	 * @return the currentTab
	 */
	public int getCurrentTab() {
		return currentTab;
	}

	/**
	 * Set the currentTab
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
	 * Get the Gpki Singin Data.
	 * @return the textBeforeSign
	 */
	public String getTextBeforeSign() {
		log.info("getTextBeforeSign");
		StringBuilder sb = new StringBuilder();
		if (getManagedBean().getScQt() != null) {
			sb.append(getManagedBean().getScQt().getQtNo());
		}
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		textBeforeSign = sb.toString();
		return textBeforeSign;
	}

	/**
	 * Set the Gpki Singin Data.
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
		log.info("getCommitteMemberByUserId");
		FacesScopes facesScopes = new FacesScopes();
		PmUser curLogedInUser = facesScopes.getUserSessionBean();
		String committeeType = this.getManagedBean()
				.getCommitteeType();
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
		committeReqDTO.setPmUser(curLogedInUser);
		committeReqDTO.setCommitteeType(committeeType);
		
		return committeSO.retrieveCommitteMemberByUserId(
				committeReqDTO);
	}

	
	/**
	 * Inner class SampleLabelDTO for Finalization Endorsement Report.
	 *
	 * 
	 */
	
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
			log.info("QualifiedSuppliersPagingDataModel.pageChangeListener");
			ISearchResultBean<ScQtEvaluationResultDtl> resultBean = null;

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("qtEvaluationResult.evaluationStage",
							SearchOperator.OPERATOR_EQUALS.value(), evaluationStage));		
			
			IQTSuppFinalizationSO qtSuppFinalizationSO = null;
			try {
				qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);
				
				SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
				reqDTO.setQtId(qtId);
				//#21695 tec by itemize result pop up farid - start
				if (null != selectedZoneId && (SourcingConstants.EVAL_STAGE_FINANCIAL_ITM.equalsIgnoreCase(evaluationStage) || SourcingConstants.EVAL_STAGE_FINANCIAL_PKG.equalsIgnoreCase(evaluationStage)
						|| SourcingConstants.EVAL_STAGE_TECHNICAL_ITEM.equalsIgnoreCase(evaluationStage))) {
				//#21695 tec by itemize result pop up farid - start
					reqDTO.setSpecZoneId(selectedZoneId);
				}
				/*
				 * BUG 34212 - FIX SORT ITEMIZE & PACKAGE IN SUPPLIER FINALIZATION START
				 */
				reqDTO.setRequestItemId(selectedItemId); //PUT THIS CODE INTO SIT
				reqDTO.setEvaluationStage(evaluationStage); //PUT THIS CODE INTO SIT
				//#36239 4serangkai -adding to check zonal
				if(null != selectedZoneId){
					reqDTO.setZonal(true);
				}
				/*
				 * BUG 34212 - FIX SORT ITEMIZE & PACKAGE IN SUPPLIER FINALIZATION START
				 */
				reqDTO.setSearchBean(searchBean);
				
				/*
				 * Development 32311: SLA LOG START
				 */
				long startTime = System.currentTimeMillis();
				SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
							.retrieveQualifiedSuppliersInfoByEvalStage(reqDTO);
				SLAUtils.getInstance().logSQLPerformanceInfo("QT","32311(C)-"+qtId,"QTSuppFinalizationSO.retrieveQualifiedSuppliersInfoByEvalStage", startTime,"#32311");
				/*
				 * Development 32311: SLA LOG END
				 */
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
		//Sonarqube #83 - start
		if(null == meetingList)
			throw new IllegalArgumentException("Meeting list cannot be null");
		if (CollectionUtils.isNotEmpty(meetingList)) {
		//Sonarqube #83 - end
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
			if (duplicateDate) {//Sonarqube #83
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
			log.info("ShortlistedSuppliersPagingDataModel.pageChangeListener");
			ISearchResultBean<ScQtEvaluationResultDtl> resultBean = null;
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("qtEvaluationResult.evaluationStage",
							SearchOperator.OPERATOR_EQUALS.value(), evaluationStage));		
			
			IQTSuppFinalizationSO qtSuppFinalizationSO = null;
			try {
				qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);
				
				SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
				reqDTO.setQtId(qtId);
				if (selectedZoneId != null) {
					reqDTO.setSpecZoneId(selectedZoneId);
				}
				
				Long selectedItemId = null;
				if (null != selectedItem) {
					selectedItemId = selectedItem.getRequestItem().getRequestNoteItemId();
					reqDTO.setRequestItemId(selectedItemId);
				} 
				reqDTO.setSearchBean(searchBean);
				
				/*
				 * BUG 34212 - FIX SORT ITEMIZE & PACKAGE IN SUPPLIER FINALIZATION START
				 */
				reqDTO.setEvaluationStage(evaluationStage); //PUT THIS CODE INTO SIT
				/*
				 * BUG 34212 - FIX SORT ITEMIZE & PACKAGE IN SUPPLIER FINALIZATION START
				 */
				
				/*
				 * Development 32311: SLA LOG START
				 */
				//#36239 4serangkai -adding to check zonal
				if(null != selectedZoneId){
					reqDTO.setZonal(true);
				}
				long startTime = System.currentTimeMillis();
				SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
							.retrieveQualifiedSuppliersInfoByEvalStage(reqDTO);
				SLAUtils.getInstance().logSQLPerformanceInfo("QT","32311(D)-"+qtId,"QTSuppFinalizationSO.retrieveQualifiedSuppliersInfoByEvalStage", startTime,"#32311");
				/*
				 * Development 32311: SLA LOG END
				 */
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
							// #26247 - #32687 - Start.						
							if (null !=  qualSupplier.getSmMofAccount() && StringUtils.isNotEmpty( qualSupplier.getSmMofAccount().getMofRegStatus())) {
    						      qualSupplier.getSmMofAccount().setMofRegStatus(ResourceBundleUtils.getMessage(
    						          qualSupplier.getSmMofAccount().getMofRegStatus(), 
    						          ResourceBundleUtils.BUNDLE_COMMON_LABELS,
    						          new FacesScopes().getUserLocale()));
  						    }
							// #26247 - #32687 - End.
							
							QtEvaluationResultDtlBean recommendationBean = null;
							recommendationBean = getCachedBeanFromList(cachedSuppliers, 
									qualSupplier.getEvaluationResultDtlId());
							if (recommendationBean != null) {
								qualSupplier.setShortListed(
										recommendationBean.isShortlisted());
							}
							
							//  Set shortlisted as true by defaultly for qualified suppliers in Bidding  
							if (ParameterConst.RECOMMEND_BY_ONLINE_BIDDING
									.equals(getManagedBean().getFinalizationMethod())
									&& !qualSupplier.isSuppSelectionDisabled()&& !isQTRefinalized()) {
								qualSupplier.setShortListed(true);		
							}
							
							recommendationBean = updateCachedData(qualSupplier, 
									zoneId, selectedItemId, null);
							/*
							 * BUG #31128 - UNSELECT & DIM RADIOBUTTON WHEN THE SAID SUPPLIER IS IMPOSE OF DISCIPLINARY ACTION
							 * SHAFFUL
							 */
							// #32699 - Start.
							if (recommendationBean.isAwarded() && (!recommendationBean.getScQtProposal().getScQtSupplier().getSmSupplier().isDisciplinaryActions()
							    || recommendationBean.getScQtProposal().getScQtSupplier().getSmSupplier().isWarningDA())) {
								getManagedBean().setRecommendedSupplier(qualSupplier);
							}
							// #32699 - End.
							/*
							 * END BUG #31128
							 */
							
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
									qualSupplier.setBidPrice(recommendationBean.getBidPrice());//34760
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
		log.info("getCachedSuppliersFromMap");
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
		log.info("getCachedBeanFromList");
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
		log.info("processSuppliersBeforeSaving");
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
		log.info("processSuppliersByItem");
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
		
		// #28250 - Start - Added Null check
		if (zoneDtl != null && null != scQtFinalization && null != scQtFinalization.getScQtRecommendsByZone()) {
		// 28250 - End
			specZoneId = zoneDtl.getSpecZoneId();
			items = scQtFinalization.getScQtRecommendsByZone()
									.get(specZoneId);
		} else if (scQtFinalization != null && scQtFinalization.getScQtRecommends() != null ){//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
			specZoneId = DEFAULT_ZONE;
			items = scQtFinalization.getScQtRecommends();
		}
		
		if (items != null && CollectionUtils.isNotEmpty(items)) {	//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 		
			// to set the recommend details for each item
			for (ScQtRecommend scQtRecommend : items) {
				Long itemId = scQtRecommend.getRequestItem().getRequestNoteItemId();
				cachedSuppliers = getCachedSuppliersFromMap(specZoneId, itemId);
				
				filterRecommendDtls(scQtRecommend, cachedSuppliers);
			}				
		}		
	}
	
	private void processSuppliersByPackage(TplSpecZoneDtl zoneDtl) {
		log.info("processSuppliersByPackage");
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
		log.info("filterRecommendDtls");
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
	
	/**
	 * Gets the qt award so.
	 * 
	 * @return the qt award so
	 */
	private IQTAwardSO getQtAwardSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
	}
	
	private void checkAndResetPreviouslyAwardedSupplier(
							ScQtEvaluationResultDtl evalResultDtl,
							Long selectedZoneId, Long selectedItemId) {
		log.info("checkAndResetPreviouslyAwardedSupplier");
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
	
	// ***25201#35 
	/**
	 * 
	 * @param scQt
	 * @param previousPageUrl
	 * @param editable
	 * @param justAllowTheseRoles - array of RoleCode to allow view the task
	 * @return
	 */
public String editSupplierFinalizationDetails(ScQt scQt, String previousPageUrl, boolean editable, RoleCode[] justAllowTheseRoles){
		
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		String bpmUserLogin = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.BPM_PRINCIPAL);
				
		Object token = getBpmSO().authenticate(bpmUserLogin);
		reqDTO.setCtxToken(token);
		reqDTO.setDocNumber(scQt.getQtNo());
		SuppFinalizationReqDTO sfReqDTO = new SuppFinalizationReqDTO();
		Set<String> fetchSet = new HashSet();
		fetchSet.add("finalizationCommittee");
		sfReqDTO.setFetchSet(fetchSet);
		sfReqDTO.setQtId(scQt.getQtId());
		SuppFinalizationResDTO sfResDTO = getQtSuppFinalizationSO().retrieveQtById(sfReqDTO);
		
		reqDTO.setDocType(ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION);
		BpmResponseDTO resDTO = getBpmSO().getTasksByDocNumber(reqDTO);
		String qtFinCmt = null;
		if(sfResDTO.isSuccessful()){
			if(sfResDTO.getScQt()!=null && sfResDTO.getScQt().getFinalizationCommitte()!=null){
				qtFinCmt = sfResDTO.getScQt().getFinalizationCommitte().getParameterCode();
			}
		}
		
		List<String> loginIds = new ArrayList<String>();
		String loginId = getFacesScopesGen().getUserSessionBean().getLoginId();
		loginIds.add(loginId);
		
		long userId = getFacesScopesGen().getUserSessionBean().getUserId();
				
		
		String taskId = null;
		String latestAssignee = null;
		String assignee = null;
		SCEvaluationDataType dataType = null;
		if(resDTO != null && resDTO.getWorklistVO() != null && !CollectionUtils.isEmpty(resDTO.getWorklistVO().getWorklistItem())){
			List<WorklistItemVO> tl = resDTO.getWorklistVO().getWorklistItem();
			
			Collections.sort(tl, new WorklistItemVOComparator.SortByUpdateDateAscComparator());
			List<WorklistItemVO> sfl = new ArrayList<WorklistItemVO>();
			for(WorklistItemVO w: tl){
				if(ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION.equalsIgnoreCase(w.getDocType())){
					if(CollectionUtils.containsAny(w.getAssignees(), loginIds)){
						taskId = w.getTaskId();
						assignee=loginIds.get(0);
					}
					sfl.add(w);
				}
			}
			
			IScActorSO scActorSO = null;
			boolean isActor = false;
			try {
				scActorSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IScActorSO.class);
				ScActorReqDTO scActorReqDTO = new ScActorReqDTO();
				scActorReqDTO.setDocTypes(Arrays.asList(ParameterConst.DOCTYPE_QUOTATION, ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION));
				scActorReqDTO.setDocId(scQt.getQtId());
				scActorReqDTO.setUserId(userId);
				ScActorResDTO scActorResDTO = scActorSO.retreiveActorByDocTypesDocIdUserId(scActorReqDTO);
				if(scActorResDTO.isSuccessful()){
					if(scActorResDTO.getScActorList()!=null && !CollectionUtils.isEmpty(scActorResDTO.getScActorList())){
						isActor = true;
					}
				}
				

			} finally {
				scActorSO = null;
			}		
			
			if( isQTMaintenanceScreen() && !CollectionUtils.isEmpty(sfl) ){
				Collections.sort(sfl, new WorklistItemVOComparator.SortByUpdateDateDescComparator());
				int chk = -1;
				for(int i = 0; i<3 ; i++){ //3 types of checking
					//default. task was ever assigned to the logged in user
					if(i==0 && !StringUtils.isBlank(taskId) && !StringUtils.isBlank(assignee)){
						chk = i;
						log.info("chk : "+ chk);
					}
					//isActor but not in assignee					 
					if(i==1 && isActor && StringUtils.isBlank(taskId)){
						chk = i;
						log.info("chk : "+ chk);
					}
					//bpk auditor only. then, pass if login id != assignee
					if(i==2 && justAllowTheseRoles != null && justAllowTheseRoles.length > 0 
							&& userHasRole(justAllowTheseRoles) 
							/*&& ( (StringUtils.isBlank(taskId) && StringUtils.isBlank(assignee)) || (!StringUtils.isBlank(taskId) && !StringUtils.isBlank(assignee) && !assignee.equalsIgnoreCase(loginId)))*/
							){
						chk = i;
						log.info("chk : "+chk);
					}
					
					if(chk == i){						
						for(WorklistItemVO w: sfl){
							if(!CollectionUtils.isEmpty(w.getAssignees())){
								for(String a : w.getAssignees()){
									if(!StringUtils.isEmpty(a)){
										latestAssignee = w.getAssignees().get(0);
									}
								}
								if(latestAssignee != null){
									taskId = w.getTaskId();
								}
								try{
									dataType = getBpmTaskDetails(SCEvaluationDataType.class, null, taskId, !StringUtils.isBlank(latestAssignee)?latestAssignee:null);
									break;
								}catch(ClassCastException ex){
									continue;
								}catch(Exception ex){
									continue;
								}
								
							}
						}
						if(dataType != null){
							break;
						}
					}	
				}
			}		
			// TODO error flash message if user is not allowed to view i.e. never was an assignee/actor
		}		
		
		if(isQTMaintenanceScreen() && !StringUtils.isBlank(taskId)){
			getManagedBean().setActiveIndex(0);
			
			/*if(dataType == null){
				dataType = getBpmTaskDetails(SCEvaluationDataType.class, null, taskId, !StringUtils.isBlank(latestAssignee)?latestAssignee:null);
			}*/
			//as in preRenderView()
			Long qtId = scQt.getQtId();
			getManagedBean().setActiveIndex(0);
			
			
			Long loggedInUserId = getFacesScopes().getUserSessionBean()
									.getUserId();
			getManagedBean().setLoggedInUserId(loggedInUserId);
			
		}
		getManagedBean().setReadOnly(!editable);
		getManagedBean().setScQt(scQt);
		getManagedBean().setPreviousPageUrl(previousPageUrl);
		
		Long loggedInUserId = getFacesScopes().getUserSessionBean()
								.getUserId();
		getManagedBean().setLoggedInUserId(loggedInUserId);

		//loadInitialDataMaintenance(scQt.getQtId());
		
		if(isQTMaintenanceScreen()){
			if(dataType == null || dataType.getCommitteeType() == null){
				dataType = new SCEvaluationDataType();
				CommitteReqDTO cmtReqDTO = new CommitteReqDTO();
				cmtReqDTO.setQtId(scQt.getQtId());
				if(qtFinCmt!=null){
					dataType.setCommitteeType(qtFinCmt);
				}
				else{
					CommitteResDTO cmtResDTO = getCommitteeSO()
							.retrieveFinalizationCommitteeType(cmtReqDTO);
		
					List<PmParameter> finalizationCommTypes = cmtResDTO.getFinalizationSecratry();
					if(CollectionUtils.isNotEmpty(finalizationCommTypes)){
						if(1<finalizationCommTypes.size()){
							log.info("finalizationCommTypes.size() > 1");
						}					
						for(PmParameter prm : finalizationCommTypes){
							log.info(prm.getParameterCode());
							dataType.setCommitteeType(prm.getParameterCode());
						}
					}
				}
			}		
		
			if(dataType == null || dataType.getFcDecision() == null){
				IQTSuppFinalizationSO qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);
				
				sfReqDTO = new SuppFinalizationReqDTO();
				Set<String> fetches = new HashSet<String>();
				fetches.add("decision");
				sfReqDTO.setQtId(scQt.getQtId());
				sfReqDTO.setFetchSet(fetches);
				sfResDTO = qtSuppFinalizationSO.retrieveSupplierFinalizationByQtId(sfReqDTO);
				if(sfResDTO.isSuccessful()){
					dataType.setFcDecision(sfResDTO.getScQtFinalization().getDecision().getParameterCode());
				}
			}
			
			getManagedBean().setPayLoad(dataType);
		}
		
		
		
		
		
		
		
		loadInitialData(scQt.getQtId());
		
		if(isQTMaintenanceScreen()){
			//if(QTStatusConst.SF_PENDING_SIGN_OFF.equals(getManagedBean().getCurrentStatusId()) || getManagedBean().getCurrentStatusId() == null){//17376-165
				IQTSuppFinalizationSO qtSuppFinalizationSO = getQTSuppFinalizationSO();
				sfReqDTO = new SuppFinalizationReqDTO();
				sfReqDTO.setQtId(scQt.getQtId());
				sfResDTO = qtSuppFinalizationSO.retrieveCurrentStatus(sfReqDTO);
				if(sfResDTO.isSuccessful() && sfResDTO.getCurrentStatus() != null){
					getManagedBean().setCurrentStatusId(sfResDTO.getCurrentStatus().getPmStatus().getStatusId());
				}
			//} //17376-165
		}
		this.evaluateDecisionList();//6906 &34812
		return QTConstants.FC_SUPPLIER_FINALIZATION_PAGE;
	}
		
		
	/**
	 * The method to used auto update of request.
	 */
	public void enableAutoUpdate() {
		log.info("enableAutoUpdate");
		getManagedBean().setAutoUpdate(Boolean.TRUE);		
	}
	
	private void validateReEvaluation(){
		log.info("validateReEvaluation");
		
		if (this.getManagedBean().isReEvaluate()) {
			ScQtFinalization finalization = this.getManagedBean().getScQtFinalization();
			if (!(finalization.isReEvaluateCompReq()
					||finalization.isReEvaluateFinReq() || finalization
						.isReEvaluateTechReq())) {
				this.getManagedBean().setActiveIndex(
						this.getManagedBean().getFinalizationTabIndex());
				throw new EpException(MessageConst.ERROR_100022,
						new String[] { ResourceBundleUtils.getMessage(
								"lbl.sc.reevaluation.required.for",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale()) });
			}
		}
	}
	
	
	private class ShortlistedCommonSuppliersPagingDataModel extends
			PagingDataModel<Object[]> {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor for Summary Supplier Paging Data Model
		 */
		public ShortlistedCommonSuppliersPagingDataModel() {
			super("commonSupplierListTbl");
		}

		/**
		 * Listener method for lazy data model change event 
		 */
		@Override
		public ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			log.info("ShortlistedCommonSuppliersPagingDataModel.pageChangeListener");
			//34746 - start
			SuppFinalizationResDTO resDTO = new SuppFinalizationResDTO();
			//34746 - end
			IQTSuppFinalizationSO qtSuppFinalizationSO = null;
			try {
				List<ScQtRecommend> selectedItemList = getManagedBean()
						.getSelectedItemList();
				if (CollectionUtils.isNotEmpty(selectedItemList)) {
					qtSuppFinalizationSO = getQTSuppFinalizationSO();

					SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
					reqDTO.setQtId(getManagedBean().getScQt().getQtId());
					reqDTO.setEvalResultSearchBean(searchBean);
					List<Long> requestItemIds = new ArrayList<Long>();
					for (ScQtRecommend selectedItem : selectedItemList) {
						//35157 - start
						Long requestItemId = selectedItem.getRequestItem().getRequestNoteItemId();
						if(!requestItemIds.contains(requestItemId)){
							requestItemIds.add(requestItemId);
						}
						//35157 - end
					}
					reqDTO.setRequestItemIds(requestItemIds);
					TplSpecZoneDtl specZone = getManagedBean().getCurrentZone();
					if(specZone != null){
						reqDTO.setSpecZoneId(specZone.getSpecZoneId());
					}
					reqDTO.setRefinalize(getManagedBean().getQtRefinalized());
					resDTO = qtSuppFinalizationSO
							.retrieveQualifiedCommonSuppliersByItem(reqDTO);
				}
			} finally {
				qtSuppFinalizationSO = null;
			}

			return resDTO.getQualifiedCommonSuppliersByItem();
		}
	}
	
			/**
			 * The method  used to get the shortListed suppliers list.
			 * @return the list of PagingDataModel objects.
			 */		
	public IPagingDataModel<Object[]> getShortlistedCommomSupplier() {
		log.info("getShortlistedCommomSupplier");
		IPagingDataModel<Object[]> shortlistedCommomSupplier = getManagedBean()
				.getShortlistedCommonSuppliers();
		if (shortlistedCommomSupplier == null) {
			shortlistedCommomSupplier = new ShortlistedCommonSuppliersPagingDataModel();
			getManagedBean().setShortlistedCommonSuppliers(
					shortlistedCommomSupplier);
		}else {
			shortlistedCommomSupplier.reloadData();
		}
		return shortlistedCommomSupplier;
	}
	
	private IQTSuppFinalizationSO getQTSuppFinalizationSO() {
		
		return BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSuppFinalizationSO.class);
	}
	
	private ICommitteSO getCommitteeSO(){
		return BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
	}
	
	/**
	 * @method returns eval Type Item.
	 */
	public String evalTypeItem(){
	return SourcingConstants.EVALUATE_BY__ITEM;	
	}
	
	
	/**
	 * Get the Preference Margin data.
	 * @param scQtEvaluationResultDtls evalresultdtls
	 * @return evaluationResultDtls.
	 */
	public List<ScQtEvaluationResultDtl> getPreferenceMarginList(ScQtEvaluationResultDtl evalResultDtls){
		log.info("getPreferenceMarginList");
	List<ScQtEvaluationResultDtl> evaluationResultDtls=new ArrayList<ScQtEvaluationResultDtl>();
	try{
		if(null!=evalResultDtls){
			evaluationResultDtls.add(evalResultDtls);
		}
	}finally{
		
	}
	return evaluationResultDtls;
	}
	
	/**
	 * Listener method to handle terms boolean check box change event
	 * @param event
	 */
	public void setWithTermValue(AjaxBehaviorEvent event) {
		log.info("setWithTermValue");
		boolean isWithTerms = (boolean) event.getComponent().getAttributes()
				.get("value");
		ScQtFinalization scQtFinalization = this.getManagedBean().getScQtFinalization();
		scQtFinalization.setWithTerms(isWithTerms);
		if(!isWithTerms){
			scQtFinalization.setTermsText(null);
		}
	}

	/**
	 * Listener method to handle justification reason change event
	 * @param event
	 */
	public void justificationChange(AjaxBehaviorEvent event) {
		log.info("justificationChange");
		String reasonCode = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().setRenderjustificationTextbox(false);
		PmReason reason = getReasonByCategoryAndCode(
				ReasonCategory.SUPPLIER_SELECTION_JUSTIFICATION.value(), reasonCode);
		//Bug #29707 - start
		if (reason == null) {
			this.getManagedBean().getScQtFinalization().setJustification(new PmReason());
			this.getManagedBean().setJustification(new PmReason());
		} else {
			this.getManagedBean().getScQtFinalization().setJustification(reason);
			this.getManagedBean().setJustification(reason);
			if (JUSTIFICATION_OTHERS.equalsIgnoreCase(reason.getReasonCode())) {
				this.getManagedBean().setRenderjustificationTextbox(true);
			} else {
				this.getManagedBean().getScQtFinalization().setJustificationText(null);
			}
		}
		//Bug #29707 - end
	}
	
	/**
	 * Listener method to handle endorsement report download event
	 * @param scQtFinalizationReport
	 * @throws Exception
	 */
	public void downloadEndorseReport(
			ScQtFinalizationReport scQtFinalizationReport) throws Exception {
		log.info("downloadEndorseReport");
		downloadFile(scQtFinalizationReport
				.getScQtFinalizationReportAttachment());
	}
	
	
	/**
	 * Sort the reports based on date and time 
	 * @param reports
	 */
	private void sortEndorsementReports(List<ScQtFinalizationReport> reports){
		// 35381 - related to 6906 - start
		Collections.sort(reports, new Comparator<ScQtFinalizationReport>() {
			@Override
			public int compare(ScQtFinalizationReport report1, ScQtFinalizationReport report2) {
				if (report1.getChangedDate() == null || report2.getChangedDate() == null) {
					return -1;
				}
				return report2.getChangedDate().compareTo(report1.getChangedDate());
			} 
		});
		// 35381 - related to 6906 - end
	}
	
	/**
	 * Method to handle endoresement report upload event
	 * @param event
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void handleEndorseReportUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		log.info("handleEndorseReportUpload");
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;

			ScQtFinalizationReport scQtFinalizationReport = this.getManagedBean()
					.getEndorsementReport();
			ScQtFinalizationReportAttachment attachment = scQtFinalizationReport
					.getScQtFinalizationReportAttachment();
			if (null == attachment) {
				attachment = new ScQtFinalizationReportAttachment();
				attachment.setScQtFinalizationReport(scQtFinalizationReport);
				attachment.setDocNo(getManagedBean().getScQt().getQtNo());
				scQtFinalizationReport.setScQtFinalizationReportAttachment(attachment);
				
			}

			attachment.setDocNo(getManagedBean().getScQt().getQtNo());
			scAttachment = attachment;
			uploadAttachment(event.getFile(), scAttachment);
			// #18685 - Start.
			int index = 0;
			if (CollectionUtils.isNotEmpty(getManagedBean().getEndorsementReports())
			    && this.getManagedBean().getEndorsementReports().size() > 0) {
			  for (ScQtFinalizationReport report: getManagedBean().getEndorsementReports()) {
				  if (report.getScQtFinalizationReportAttachment() != null) {
					  index++;
				  }
			  }
			} else {
				index = 1;
			}
			String fileName = scAttachment.getFileName();
			
			if (index >= 0 && fileName != null) {
				scAttachment.setFileName("K" + index + UNDER_SCORE + fileName);
			}
			// #18685 - End.
		}
	}
	
	/**
	 * Method to handle Kertas Taklimat file upload event
	 * @param event
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	//Sonarqube #83 - refactor method - start
	public void handleKertasFileUpload(FileUploadEvent event) throws IOException {
		if (null == event.getFile())
			return;
		log.info("handleKertasFileUpload");
		ScQtFinalizationReport scQtFinalizationReport 
				= this.getManagedBean().getSelectedFinalizationReport();
		if (scQtFinalizationReport == null) {
			List<ScQtFinalizationReport> reports = this.getManagedBean().getDocumentList();
			for (ScQtFinalizationReport report : reports) {
				if (SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT
						.equals(report.getPmDocument().getDocDefinitionKey())) {
					scQtFinalizationReport = report;
				}
			}
		}
		//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
		ScQtFinalizationReportAttachment reportAttachment  = null;			
		if (scQtFinalizationReport != null && scQtFinalizationReport.getScQtFinalizationReportAttachment() != null )
			reportAttachment = scQtFinalizationReport.getScQtFinalizationReportAttachment();
		
		//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
		if (scQtFinalizationReport == null )
			scQtFinalizationReport = new ScQtFinalizationReport();
		
		if(reportAttachment == null ){
			reportAttachment = new ScQtFinalizationReportAttachment();
			reportAttachment.setScQtFinalizationReport(scQtFinalizationReport);
			reportAttachment.setDocNo(getManagedBean().getScQt().getQtNo());
			reportAttachment.setFileName(event.getFile().getFileName());
			scQtFinalizationReport.setScQtFinalizationReportAttachment(reportAttachment);
		}
		reportAttachment.setFileName(event.getFile().getFileName());
		populateAttachmentDetails(reportAttachment);
		ScAttachment scAttachment = reportAttachment;
		uploadAttachment(event.getFile(), scAttachment);
		// again set to true after uploaded at finalization currently disabled
		this.getManagedBean().getScQtFinalization().setBriefingPaperUploaded(true);
		scQtFinalizationReport.setTempReportAttachment(reportAttachment);
	}
	//Sonarqube #83 - refactor method - end
	
	/**
	 * Method to delete attached kertas taklimat attachment
	 * @param delEntity
	 */
	public void deleteKertasAttachment(BaseEntity delEntity) {
		log.info("deleteKertasAttachment");
		ScQtFinalizationReport scQtFinalizationReport = this.getManagedBean()
				.getSelectedFinalizationReport();
		scQtFinalizationReport.setTempReportAttachment(null);
		//set to false whenever KT is deleted, currenly disabled
		this.getManagedBean().getScQtFinalization().setBriefingPaperUploaded(false);
				
	}
	
	private IBpmSO getBpmSO() {
		return (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(
				IBpmSO.class);
	}
	
	private IQTSuppFinalizationSO getQtSuppFinalizationSO(){
		return BusinessDelegate.getInstance().getServiceObject(IQTSuppFinalizationSO.class);
	}
	
	/**
	 * 
	 * @return 
	 */
	public Boolean canShowSupplierProposal(){
		
		IUserSO userSO = null;
		try{
			userSO = BusinessDelegate.getInstance()
					.getServiceObject(IUserSO.class);		
			Long userId = getFacesScopes().getUserSessionBean().getUserId();
			UserReqDTO reqDTO = new UserReqDTO();

			//role permission with no organization restriction
			//add role code as necessary
			List <String> roleToCheck = new ArrayList<String>();
			roleToCheck.add(RoleCode.BPK_AUDITOR.value());
			
			ISearchBean<PmUserRole> searchBean = new SearchBean<PmUserRole>();
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserOrg.pmUser.userId", 
							SearchOperator.OPERATOR_EQUALS.value(), userId));
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmRole.roleCode", 
							SearchOperator.OPERATOR_IN.value(), 
							roleToCheck));
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserOrg.recordStatusFlg", 
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE));
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserOrg.pmUser.recordStatusFlg", 
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE)					
			);
			reqDTO.setUserRoleSearchBean(searchBean);
			UserResDTO resDTO = userSO.retrieveUserRoleBySearchBean(reqDTO);
			if(CollectionUtils.isNotEmpty(resDTO.getUserRoleList())){
				return true;
			}
			/**
			 * 	1. find qt pm_org_profile
			 *  2. find user pm_org_profile
			 *  3. give permission to user within same organization with the role accesibility  
			 *  (cancel)4. by state group (sabah, sarawak, peninsular) within same PTJ
			 */
			
			//1
			ScQt scQt = this.scQt!=null?this.scQt:getManagedBean().getScQt();
			PmOrgProfile qtOrgProfile =  scQt.getPmOrgProfile();
						
			//2 & 3
			reqDTO = new UserReqDTO();
			resDTO = null;
			debugLogger(getFacesScopes().getUserRoleList());
			reqDTO.setUserId(userId);
			reqDTO.setOrgProfileId(qtOrgProfile.getOrgProfileId());
			reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
			reqDTO.setRoleCodes(Arrays.asList(
					RoleCode.DESK_OFFICER.value(),
					RoleCode.FPB_SECRETARY.value(),
					RoleCode.QCA_SECRETARY.value(),
					RoleCode.QCB_SECRETARY.value(),
					RoleCode.PBA_SECRETARY.value(),
					RoleCode.PBB_SECRETARY.value()
					));
			reqDTO.setForceGetAllUserOrgList(false);
			resDTO = userSO.checkAndGetUserInOrgProfile(reqDTO);
			/*reqDTO.setForceGetAllUserOrgList(true);
			UserResDTO allUserOrgResDTO = userSO.checkAndGetUserInOrgProfile(reqDTO);
			reqDTO.setForceGetAllUserOrgList(false);*/
			
			
			if(null != resDTO && resDTO.isSuccessful()){
				debugLogger("checkUserInOrgProfile Successful");
				if(!CollectionUtils.isEmpty(resDTO.getUserOrgList())){
					return true;
				}
			}
			return false;
			
		}
		finally{
			userSO = null;
		}
		
	}

	public ScQt getScQt() {
		return scQt;
	}

	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}

	// ***25201
	public boolean isQTMaintenanceScreen() {
		return QTConstants.PORTLET_NAME_QT_MAINTENANCE.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
				|| QTConstants.PORTLET_NAME_QT_SEARCH.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId()) // ***17376#55
				;
	}
	
	//#25426 ruzaimah - Start
	/**
	 * 202407252@haziq #25426
	 * To checking inactive role
	 * @param scQtFinalization
	 * @param decisionTypeCode
	 */
	public void validateInactiveRole(ScQtFinalization scQtFinalization, String decisionTypeCode) {
		// inactive role checking
		scQt = getManagedBean().getScQt();
		String stageType = null;
		if (ParameterConst.DECISION_RE_PUBLISH.equals(decisionTypeCode)) {
			stageType = CommonConstants.VALUE_ALL;
			checkInactiveCommiteeRole(scQtFinalization, stageType);

		} else if (ParameterConst.DECISION_RE_EVALUATION.equals(decisionTypeCode)) {
			if (ParameterConst.EVALUATION_TYPES_ONE_TIER.equals(scQt.getEvaluationType())
					&& ParameterConst.REEVALUATION_COMMITTEE_MEMBERS_EXISTING.equals(scQtFinalization
							.getReEvaluateCommitte())) {
				// Checking on inactive EC Chairperson/Secretary/Member
				stageType = QTStatusConst.EVALUATION_COMMITTEE_CODE;
				checkInactiveCommiteeRole(scQtFinalization, stageType);
			} else if (ParameterConst.EVALUATION_TYPES_TWO_TIER.equals(scQt.getEvaluationType())
					&& ParameterConst.REEVALUATION_COMMITTEE_MEMBERS_EXISTING.equals(scQtFinalization
							.getReEvaluateCommitte())) {
				if (scQtFinalization.isReEvaluateCompReq()) {
					// Document Compliance Stage, check inactive role Committee
					// (OC/TEC/FEC)
					stageType = QTStatusConst.OPENING_COMMITTEE_CODE + CommonConstants.SPACE_SLASH
							+ QTStatusConst.TECHICAL_EVALUATION_COMMITTEE_CODE + CommonConstants.SPACE_SLASH
							+ QTStatusConst.FINANCIAL_EVALUATION_COMMITTEE_CODE ;
					
				} else if (scQtFinalization.isReEvaluateTechReq()) {
					// Technical Stage, check inactive role Committee (TEC/FEC)
					stageType = QTStatusConst.TECHICAL_EVALUATION_COMMITTEE_CODE + CommonConstants.SPACE_SLASH
							+ QTStatusConst.FINANCIAL_EVALUATION_COMMITTEE_CODE ;

				} else if (scQtFinalization.isReEvaluateFinReq()) {
					// Financial Stage, check inactive role Committee (FEC)
					stageType = QTStatusConst.FINANCIAL_EVALUATION_COMMITTEE_CODE;
				}
				checkInactiveCommiteeRole(scQtFinalization, stageType);

			}
		}
	}
	
	
	/**
	 * 202407252@haziq #25426
	 * To check Inactive Commitee Role
	 * @param scQtFinalization
	 * @param stageType
	 */
	public void checkInactiveCommiteeRole(ScQtFinalization scQtFinalization, String stageType) {
		String decisionTypeCode = scQtFinalization.getDecision().getParameterCode();

		boolean isECNotSelected = false;

		if (null != scQtFinalization.getReEvaluateCommitte()
				|| (!scQtFinalization.isReEvaluateCompReq() && !scQtFinalization.isReEvaluateTechReq() && !scQtFinalization
						.isReEvaluateFinReq())) {// #25426#264 if evaluationcommitee not selected
			isECNotSelected = true;

		}

		if (ParameterConst.DECISION_RE_PUBLISH.equals(decisionTypeCode)) {

			handleRePublishDecision(scQtFinalization, decisionTypeCode, stageType);

		} else if (ParameterConst.DECISION_RE_EVALUATION.equals(decisionTypeCode) && isECNotSelected) {// #25426#264 if evaluationcommitee not selected
			handleReEvaluationDecision(scQtFinalization, stageType, decisionTypeCode);
		}
	}
	
	/**
	 * 202407252@haziq #25426
	 * To handle Re Publish Decision
	 * @param scQtFinalization
	 * @param parameterSO
	 * @param decisionTypeCode
	 * @param stageType
	 */
	private void handleRePublishDecision(ScQtFinalization scQtFinalization, String decisionTypeCode, String stageType) {
		IParameterSO parameterSO = null;
		String parameterCode = null;
		try {

			parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IParameterSO.class);
			Long parameterId = scQtFinalization.getRePublishOption().getParameterId();
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			parameterCode = resDTO.getPmParameter().getParameterCode();

		} catch (Exception e) {
			// Log the exception and handle it as needed
			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE,
					SLALogConstant.QT_SUPPLIER_FINALIZATION_BACKINGBEAN, scQtFinalization.getScQt().getQtNo(),
					"handleRePublishDecision", getUserIdSessionFromLoginId(), e);
		}

		boolean isInactiveCommiteeRole = isInactiveCommiteeRole(scQtFinalization, stageType, decisionTypeCode);

		if ((parameterCode != null && ParameterConst.WITH_OUT_SPEC_CHANGES.equalsIgnoreCase(parameterCode) || ParameterConst.WITH__SPEC_CHANGES
				.equalsIgnoreCase(parameterCode)) && isInactiveCommiteeRole) {// #25426#264
			throw new EpException(MessageConst.ERROR_760486);
		}
	}
	
	/**
	 * 202407252@haziq #25426
	 * To handle Re Evaluation Decision
	 * @param scQtFinalization
	 * @param stageType
	 * @param decisionTypeCode
	 */
	private void handleReEvaluationDecision(ScQtFinalization scQtFinalization, String stageType, String decisionTypeCode) {
		scQt = getManagedBean().getScQt();
		boolean isOneTierEvaluation = ParameterConst.EVALUATION_TYPES_ONE_TIER.equals(scQt.getEvaluationType());
		boolean isTwoTierEvaluation = ParameterConst.EVALUATION_TYPES_TWO_TIER.equals(scQt.getEvaluationType());
		boolean isExistingCommittee = ParameterConst.REEVALUATION_COMMITTEE_MEMBERS_EXISTING.equals(scQtFinalization
				.getReEvaluateCommitte());
		boolean isInactiveCommiteeRole = isInactiveCommiteeRole(scQtFinalization, stageType, decisionTypeCode);
		
		if (((isOneTierEvaluation || isTwoTierEvaluation) && isExistingCommittee)
				&& isInactiveCommiteeRole) {
			String errorMessage = isOneTierEvaluation ? MessageConst.ERROR_760487 : MessageConst.ERROR_760488;
			throw new EpException(errorMessage);
		}
	}
	
	/**
	 * 202407252@haziq #25426
	 * To check is inactive committee role
	 * @param scQtFinalization
	 * @param stageType
	 * @param decisionTypeCode
	 * @return
	 */
	public boolean isInactiveCommiteeRole(ScQtFinalization scQtFinalization, String stageType, String decisionTypeCode) { // #25426#264
		Long qtId = Long.valueOf(scQtFinalization.getScQt().getQtId());
		ICommitteSO committeeSO = null;
		try {
			committeeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					ICommitteSO.class);
			return committeeSO.retrieveInactiveCommittesByQtId(qtId, stageType, decisionTypeCode); // #25426#264

		} catch (Exception e) {
			// Log the exception and handle it as needed
			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE,
					SLALogConstant.QT_SUPPLIER_FINALIZATION_BACKINGBEAN, scQtFinalization.getScQt().getQtNo(),
					"isInactiveCommiteeRole", getUserIdSessionFromLoginId(), e);
			return true;
		}
	}
	// #25426 ruzaimah - End	
	
	
	/* (non-Javadoc)
	 * @see my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean#addDocumentList(my.ep.persistence.entity.sc.quationtender.ScQt)
	 */
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
	//19287 #114
	private ScQtFinalization getCurrentFinalizationMethod(){
		if(this.getManagedBean().getScQt()!=null){
			IQTSuppFinalizationSO qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.retrieveSupplierFinalizationByQtId(reqDTO);
			if(resDTO.getScQtFinalization()!=null){
				return resDTO.getScQtFinalization();
			}
			return null;
		}
		return null;
	}
	public boolean getEndorsementType(ScQtCommitteeActivity act){
		if(this.getManagedBean().getScQtFinalization()==null){
			this.getManagedBean().setScQtFinalization(this.getCurrentFinalizationMethod());
		}
		if(this.getManagedBean().getScQtFinalization()!=null && act!=null){
			String method = this.getManagedBean().getScQtFinalization().getFinalizationMethod().getParameterCode();
			if(method.equalsIgnoreCase("L")){
				return act.isMultiLoiSignedOff();
			}else if(method.equalsIgnoreCase("B")){
				//33764 - start
				if(getManagedBean().getQtRefinalized()){
					return act.isSignedOff();
				}else {
					return act.isBiddingSignedOff();
				}
				//33764 - end
			}else{
				return act.isSignedOff();
			}
		}
		return false;
	}
	//19287 #114
	
	// #18685 - Start
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
			// Bug #35485@Syazani 08/02/2024 -- Start
			ScQtFinalizationReport scQtFinalizationReport = scQtFinalizationReports.get(i);
			ScQtFinalizationReportAttachment attachment = scQtFinalizationReport.getScQtFinalizationReportAttachment();
			String docDefinitionKey = scQtFinalizationReport.getPmDocument() != null ? scQtFinalizationReport
					.getPmDocument().getDocDefinitionKey() : "";
			// Bug #35485@Syazani 08/02/2024 -- End
					
			if (attachment == null) {
				attachment = generateDocumentAttachment(scQtFinalizationReports
						.get(i));
				scQtFinalizationReports.get(i)
						.setScQtFinalizationReportAttachment(attachment);
			}
			
			// Bug #35485@Syazani 08/02/2024 -- Start
			File file = new File(getActualFilePath(attachment));
			if (file.exists() && file.length() <= 1000 && ArrayUtils.contains(SourcingConstants.DOCUMENT_EVALUATIONS, docDefinitionKey)) {
				regenEvaluationDownload(attachment, false);
			}
			// Bug #35485@Syazani 08/02/2024 -- End
			
			attachment.setDocNo(getManagedBean().getScQt().getQtNo());
			scAttachments.add(attachment);
		}

		// Generate all the reports before submission
		if (!getManagedBean().isSubmit()) {
			downloadAllFiles("NGeP-Evaluation-Report", scAttachments);
		}
	}

	/**
	 * @param scQtFinalizationReport
	 * @return
	 * @throws Exception
	 */
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
		//#35015 4ser -Start
		IQTEvaluationSO evaluationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION,
				IQTEvaluationSO.class);
		FacesScopes facesScopes = new FacesScopes();
		EvaluationReqDTO req4SerDTO = new EvaluationReqDTO();
		long qtId = qt.getQtId();
		req4SerDTO.setQtId(qtId);
		int wipstatus = evaluationSO.checkWipScoreSetting(req4SerDTO);
		String reportName ="";
		if (wipstatus <= ParameterConst.FLAG_WIP_STATUS_3SERANGKAI_BEFORE) {
			 reportName = ReportConst.SC_SUPPLIER_FINALIZATION_RPT_WIP_JSP;
		} else {
			 reportName = ReportConst.SC_SUPPLIER_FINALIZATION_RPT_JSP;
		}
		//#35015 4ser -End
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
		List<ScQtRecommendationUlasanRemarks> ulasanRemarks = this.getManagedBean().getScQtFinalization().getScQtRecommendationUlasanRemarks();
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
		generatePdfFromOracle(reportName, paramMap, attachment, false);
	}
	
	// #18685 - End
	
	// 25037 - start
	/**
	 * Populate and initialize data for update committee screen.
	 */
	public void prepareUpdateCommitteeMember() {
		if (StringUtils.isEmpty(this.getManagedBean().getCommitteeType())) {
			return;
		}

		SupplierFinalizationMeetingBackingBean meetingBB = new SupplierFinalizationMeetingBackingBean();
		meetingBB.renewSesionObject();
		this.getManagedBean().setMeetingBB(meetingBB);
		Long qtId = this.getManagedBean().getScQt().getQtId();
		meetingBB.getManagedBean().setCommitteeType(this.getManagedBean().getCommitteeType());
		meetingBB.retriveQTDetails(qtId);
		meetingBB.retriveMeetingDetails(this.getManagedBean().getScQt().getQtId());
		meetingBB.prepareMemberUpdate();
		meetingBB.getManagedBean().setTaskName(this.getManagedBean().getTaskName());
		meetingBB.getManagedBean().setFinalBB(this);
	}
	// 25037 - end
	
	//#25729#142 & #26169 - Start
	public void checkSupplierOfferPriceByItemIsExceedCommitteeAuthority(){
		Boolean isItemize = this.getManagedBean().isItemize();
		log.info("IS ITEMIZE : "+isItemize);
		int countSuppOfferPriceExceedCommitteAuthority = 0;
		BigDecimal actualTotalIntendedPrice = this.getManagedBean().getScQt().getEstimatedAmount();
		BigDecimal committeeAuthorityAmount = checkCommitteeAuthorityAmount(actualTotalIntendedPrice);
		
		// #27502 -- Start
		QTDetailBackingBean qtBackingBean =   new QTDetailBackingBean();
		qtBackingBean.retrieveQuationTenderDetails(this.getManagedBean().getScQt().getQtId());
		ScQt scQt = qtBackingBean.getManagedBean().getQt();
		boolean isBpkCentralContract = false;
		isBpkCentralContract = isBpkCentralContract(scQt);
		
		if (!isBpkCentralContract) {

			if (isItemize) {// Recommend By Item
				List<ScQtRecommend> itemsList = this.getManagedBean().getItemList();
				if (CollectionUtils.isNotEmpty(itemsList)) {
					log.info("SC_QT_RECOMMEND LIST ===" + this.getManagedBean().getItemList().size());
					for (ScQtRecommend qtRecommendRequestItem : itemsList) {
						Long itemId = qtRecommendRequestItem.getRequestItem().getRequestNoteItemId();
						log.info("REQUEST_ITEM_ID === " + itemId);
						ISearchBean<ScQtEvaluationResultDtl> searchBean = new SearchBean<ScQtEvaluationResultDtl>();
						ISearchResultBean<ScQtEvaluationResultDtl> resultBean = null;
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("qtEvaluationResult.evaluationStage",
										SearchOperator.OPERATOR_EQUALS.value(),
										SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM));

						IQTSuppFinalizationSO qtSuppFinalizationSO = null;
						try {
							qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
							SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
							reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
							reqDTO.setEvaluationStage("RI");
							reqDTO.setRequestItemId(itemId);
							reqDTO.setSearchBean(searchBean);
							/*
							 * Development 32311: SLA LOG START
							 */
							long startTime = System.currentTimeMillis();
							SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
									.retrieveQualifiedSuppliersInfoByEvalStage(reqDTO);
							SLAUtils.getInstance().logSQLPerformanceInfo("QT","32311(A)-"+scQt.getQtId(),"QTSuppFinalizationSO.retrieveQualifiedSuppliersInfoByEvalStage", startTime,"#32311");
							/*
							 * Development 32311: SLA LOG END
							 */
							if (resDTO.isSuccessful()) {
								resultBean = resDTO.getQualifiedSuppliersByEvalStage();
								List<ScQtEvaluationResultDtl> qualifiedSuppliers = resultBean.getResultList();
								if (CollectionUtils.isNotEmpty(qualifiedSuppliers)) {
									for (ScQtEvaluationResultDtl qualSupplier : qualifiedSuppliers) {
										// #26247 - #32687 - Start.
										if (null !=  qualSupplier.getSmMofAccount() && StringUtils.isNotEmpty( qualSupplier.getSmMofAccount().getMofRegStatus())) {
										   qualSupplier.getSmMofAccount().setMofRegStatus(ResourceBundleUtils.getMessage(
											   qualSupplier.getSmMofAccount().getMofRegStatus(), 
											   ResourceBundleUtils.BUNDLE_COMMON_LABELS,
											   new FacesScopes().getUserLocale()));
										}
										// #26247 - #32687 - End.
										
										log.info("Inside qualified Supplier by item == ");
										ScRequestItem requestItem = qtRecommendRequestItem.getRequestItem();
										ScQtProposalItem proposalItem = getQtProposalItem(qtRecommendRequestItem,
												qualSupplier.getProposalAmount());
										if (proposalItem != null) {
											log.info("Proposal is not null !!!");
											if (qualSupplier.getBidRank() != null) {
												BigDecimal bidPrice = TemplateSpecHelper.getInstance()
														.calculateTotalAmount(requestItem.getTplSpecQuestion(),
																proposalItem.getFinalPrice(),
																qtRecommendRequestItem.getOrderQty());
												log.info("<<== BIDDING PRICE ==>> " + bidPrice);
												if (bidPrice.compareTo(committeeAuthorityAmount) > 0) {// bidPrice //20230711@Asyrafhaziq-SQUBE Bug Minor #16
																										// >
																										// committeeAuthorityAmount
													countSuppOfferPriceExceedCommitteAuthority++;
												}
											} else {
												BigDecimal offeredPrice = TemplateSpecHelper.getInstance()
														.calculateTotalAmount(requestItem.getTplSpecQuestion(),
																proposalItem.getOfferPrice(),
																qtRecommendRequestItem.getOrderQty());
												log.info("<<== OFFER PRICE ==>> " + offeredPrice);
												if (offeredPrice.compareTo(committeeAuthorityAmount) > 0) {// offerPrice //20230711@Asyrafhaziq-SQUBE Bug Minor #16
																											// >
																											// committeeAuthorityAmount
													countSuppOfferPriceExceedCommitteAuthority++;
												}
											}
										}
									}
								}
							}
						} catch (Exception e) {

						}
					}
				}
			} else {// Recommend By Package
				ISearchBean<ScQtEvaluationResultDtl> searchBean = new SearchBean<ScQtEvaluationResultDtl>();
				ISearchResultBean<ScQtEvaluationResultDtl> resultBean = null;
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("qtEvaluationResult.evaluationStage", SearchOperator.OPERATOR_EQUALS
								.value(), SourcingConstants.EVAL_STAGE_RECOMMENDATION_PKG));
				IQTSuppFinalizationSO qtSuppFinalizationSO = null;
				try {
					qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
					SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
					reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
					reqDTO.setEvaluationStage("RP");
					reqDTO.setRequestItemId(null);
					reqDTO.setSearchBean(searchBean);
					SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
							.retrieveQualifiedSuppliersInfoByEvalStage(reqDTO);
					if (resDTO.isSuccessful()) {
						resultBean = resDTO.getQualifiedSuppliersByEvalStage();
						List<ScQtEvaluationResultDtl> qualifiedSuppliersPackage = resultBean.getResultList();
						if (CollectionUtils.isNotEmpty(qualifiedSuppliersPackage)) {
							for (ScQtEvaluationResultDtl qualSupplierByPackage : qualifiedSuppliersPackage) {
								log.info("Inside qualified Supplier package == ");
								BigDecimal suppOfferPrice = qualSupplierByPackage.getProposalAmount().getProposalAmt();
								log.info("OFFER PRICE BY PACKAGE == " + suppOfferPrice);
								if (actualTotalIntendedPrice
										.compareTo(new BigDecimal(SourcingConstants.QT_BELOW_50MILL)) < 1) {
									if (!getManagedBean().isRefinalizedAndBiddingAward()) {
										if (suppOfferPrice.compareTo(committeeAuthorityAmount) > 0) { //20230711@Asyrafhaziq-SQUBE Bug Minor #16
											countSuppOfferPriceExceedCommitteAuthority++;
										}
									}
								}
							}
						}
					}
				} catch (Exception e) {

				}
			}

			log.info("countSuppOfferPriceExceedCommitteAuthority == " + countSuppOfferPriceExceedCommitteAuthority);
			if (countSuppOfferPriceExceedCommitteAuthority > 0) {
				this.getManagedBean().setSuppOfferPriceExceedCommunityAuthority(true);
			} else {
				this.getManagedBean().setSuppOfferPriceExceedCommunityAuthority(false);
			}
		}// #27502 -- End
		
	}
	
	public BigDecimal checkCommitteeAuthorityAmount(BigDecimal actualTotalIntendedPrice){
		BigDecimal committeeAuthorityAmount = new BigDecimal(0);
		if(actualTotalIntendedPrice.compareTo(new BigDecimal(SourcingConstants.QT_BELOW_50K)) < 1){
			committeeAuthorityAmount = new BigDecimal(SourcingConstants.QT_BELOW_50K);
		}
		//#26169 #38 - Start update condition
		else if(actualTotalIntendedPrice.compareTo(new BigDecimal(SourcingConstants.QT_BELOW_50K)) > 0 //20230711@Asyrafhaziq-SQUBE Bug Minor #16
				&& actualTotalIntendedPrice.compareTo(new BigDecimal(SourcingConstants.QT_BELOW_500K)) < 1){
			committeeAuthorityAmount = new BigDecimal(SourcingConstants.QT_BELOW_500K);
		}else if(actualTotalIntendedPrice.compareTo(new BigDecimal(SourcingConstants.QT_BELOW_500K)) > 0  //20230711@Asyrafhaziq-SQUBE Bug Minor #16
				&& actualTotalIntendedPrice.compareTo(new BigDecimal(SourcingConstants.QT_BELOW_30MILL)) < 1){
			committeeAuthorityAmount = new BigDecimal(SourcingConstants.QT_BELOW_30MILL);
		}else if(actualTotalIntendedPrice.compareTo(new BigDecimal(SourcingConstants.QT_BELOW_30MILL)) > 0 //20230711@Asyrafhaziq-SQUBE Bug Minor #16
				&& actualTotalIntendedPrice.compareTo(new BigDecimal(SourcingConstants.QT_BELOW_50MILL)) < 1){
			committeeAuthorityAmount = new BigDecimal(SourcingConstants.QT_BELOW_50MILL);
		}
		//#26169 #38 - End
		return committeeAuthorityAmount;
	}
	
	//#25729 #142 & #26169 - End
	
	//#26169 #38 - Added by Haidah START 
		public SelectItem[] getDecisionDropDownNoSupplierQualified() {
			log.info("getDecisionDropDownNoSupplierQualified");
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_IN.value(), Arrays.asList(
							ParameterConst.DECISION_RE_EVALUATION, ParameterConst.DECISION_RE_PUBLISH,
							ParameterConst.DECISION_CANCEL)));

			SelectItem[] items = getDropdownCodeValuesBySearchBean(ParameterTypeConst.FINALIZATION_DECISION_TYPE,
					searchBean);
			return items;
		}
		
		public void validateFCByDataTypeRule() {
			// Checking with Rule for Total Intended Price 
			// To show/hide option Meeting Decision for Forward To Higher Authority
			ScQtFinalization scQtFinalization = this.getManagedBean().getScQtFinalization();
			BigDecimal actualIntendedPrice = getManagedBean().getScQt().getEstimatedAmount();
			if(actualIntendedPrice.compareTo(new BigDecimal(SourcingConstants.QT_BELOW_50K)) < 1){//actualIntendedPrice <= 50,000.00				
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A)) {
					getManagedBean().setDisableFwdToHighAuthorityOption(true);
					if(this.getManagedBean().isInitialDecisionFwdHighAuth()){
						resetDecisionForwardToHigherAuthority(scQtFinalization);
						this.getManagedBean().setRecommendSupplier(Boolean.TRUE);
						PmParameter recommendSupplier = getParameterByTypeAndCode(
								ParameterTypeConst.FINALIZATION_DECISION_TYPE, 
								ParameterConst.DECISION_RECOMMEND_SUPPLIER);
						scQtFinalization.setDecision(recommendSupplier);
					}
				}
			}else if(actualIntendedPrice.compareTo(new BigDecimal(SourcingConstants.QT_BELOW_50K)) > 0 &&//20230711@Asyrafhaziq-SQUBE Bug Minor #16
					actualIntendedPrice.compareTo(new BigDecimal(SourcingConstants.QT_BELOW_500K)) < 1){
				//actualIntendedPrice > 50,000.00 && actualIntendedPrice < 500,000.00
				if(getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD) || 
					getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) ||
					getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)){
					getManagedBean().setDisableFwdToHighAuthorityOption(true);
				}
			}else if(actualIntendedPrice.compareTo(new BigDecimal(SourcingConstants.QT_BELOW_500K)) > 0){//20230711@Asyrafhaziq-SQUBE Bug Minor #16
				//actualIntendedPrice > 500,000.00
				if(getManagedBean().isInitialDecisionFwdToBPK()){//If current initialize is BPK_APPROVER
					getManagedBean().setDisableFwdToHighAuthorityOption(true);
				}
			}
		}
		
		public void checkSFHasQualifiedSupplier(){
			String recommendType = getManagedBean().getScQtFinalization().getRecommendedTypeId().getParameterCode();
			
			IQTSuppFinalizationSO qtSuppFinalizationSO = null;
			ScQt scQt = getManagedBean().getScQt();
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			try{
				qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);	
				reqDTO.setQtId(scQt.getQtId());
				reqDTO.setZonal(scQt.isZonal());
				reqDTO.setItemize(getManagedBean().isItemize());
				
				SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.checkHasQualifiedSuppliers(reqDTO);	
				if(resDTO.isSuccessful()) getManagedBean().setHasSupplierQualified(Boolean.TRUE);
				else getManagedBean().setHasSupplierQualified(Boolean.FALSE);
				
				this.getManagedBean().setHasSupplierQualifiedForCurrentSelection(resDTO.isHasSupplierQualifiedForCurrentSelection());//6906 note 202
			}finally {
				qtSuppFinalizationSO = null;
			}	
		}
		
		private boolean checkSelectedSupplierOfferPriceIsExceedCommitteeAuthority(ScQtFinalization scQtFinalization) {
			log.info("checkSelectedSupplierOfferPriceIsExceedCommitteeAuthority");
			BigDecimal intendedPrice = null;
			boolean suppOfferPriceIsExceed = false; 
			BigDecimal committeeAuthority = checkCommitteeAuthorityAmount(getManagedBean().getScQt().getEstimatedAmount());
			
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
							for (QtEvaluationResultDtlBean cachedSupplier : cachedSuppliersByItem) {
								if (cachedSupplier.isAwarded()) {
									calculateOfferedPrice(cachedSupplier, item);
									
									if (cachedSupplier.getBidRank() != null) {
										intendedPrice = cachedSupplier.getBidPrice();
									} else {
										intendedPrice = cachedSupplier.getOfferedPrice();												
									}
									
									if (intendedPrice != null) {
										if(intendedPrice.compareTo(committeeAuthority) > 0){//20230711@Asyrafhaziq-SQUBE Bug Minor #16
											suppOfferPriceIsExceed = Boolean.TRUE;
										}
									}									
									break;
								}
							}
						}
					}
				}
			} else {
				if (MapUtils.isNotEmpty(getManagedBean().getQualifiedSuppliersCache())) {
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
											intendedPrice = evalResultDtlBean.getScQtProposalAmount().getProposalAmt();
										}
										
										if(intendedPrice.compareTo(committeeAuthority) > 0){
											suppOfferPriceIsExceed = Boolean.TRUE;
										}
										break;
									}
								}
							}
						}
					}
				}
			}
			return suppOfferPriceIsExceed;
		}
		
		//#26169 #38- END
		//#28834 start
		public void submitBrief(String revise)
		{
		    log.info("testingssssssssssssssssssssssssssssssssssssssssssssssssssssssss"+revise);
		    
		    this.getManagedBean().setOutcome("REVISEBP");
			String outcome = this.getManagedBean().getOutcome();
			log.info("<<==outcome ==>> "+outcome);
			this.getManagedBean().setAction(outcome);
			

			log.info("submitSuppFinalization");
			

			//25638 - Start
			if (BpmOutcomeConst.BPM_REVISEBP.equalsIgnoreCase(outcome)) {
				// #25638 - Start
				validateRemarks();
				// #25638 - End
				//RequestContext.getCurrentInstance().execute("reviseSuppFinalizationDlgs.show()");
			
			//25638 - End
				//validateEvaluationReports(SourcingConstants.SUPPLIER_FINALIZATION_TASK);
			ScQtFinalization scQtFinalization = this.getManagedBean().getScQtFinalization();
			List<ScQtFinalizationReport> finalizationReports = new ArrayList<ScQtFinalizationReport>();
			finalizationReports.addAll(this.getManagedBean().getDocumentList());
			finalizationReports.addAll(this.getManagedBean().getEndorsementReports());
			// #28834 - Start.
			for(ScQtFinalizationReport  report: finalizationReports){
				if(report.isBrandNew()){//find the correct additional condition
					finalizationReports.remove(report);
					break;
				}
			}
			// #28834 - End.
			scQtFinalization.setScQtFinalizationReports(finalizationReports);
			Long currentStatusId = getManagedBean().getCurrentStatusId();
			log.info("currentStatusId === "+currentStatusId);
			if (QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION
						.equals(currentStatusId) || 
					QTStatusConst.SF_PENDING_ESCALATION_TO_BPK.equals(currentStatusId)) {
				this.getManagedBean().setSubmit(true);
			}

			SuppFinalizationReqDTO reqDTO = preSubmitValidationbreif();
			//SuppFinalizationReqDTO reqDTOBreif = preSubmitValidationbreifButton();
			// #18685-Start.
			for (ScQtFinalizationReport scQtFinalizationReport : getManagedBean().getNewBriefingPaperReportList()) {
				scQtFinalizationReport.setPreviousReportContent(scQtFinalizationReport.getReportContent());
			}
			// #18685-End.
			reqDTO.setAddNewBriefingPaperReportList(getManagedBean().getNewBriefingPaperReportList());
			// #25638 - Start.
			if (QTStatusConst.SF_PENDING_SIGN_OFF.equals(currentStatusId) || QTStatusConst.SF_PENDING_ENDORSEMENT.equals(currentStatusId)) {
			// #25638 - End.
				if (CollectionUtils.isNotEmpty(
						this.getManagedBean().getCommitteeList())
						// #25638 - Start
						&& !BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)
						// #25638 - End
						) {
					log.info("committeeList is not empty !!");
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
				// #25638 - Start
				PmUser secretaryUser = null;
				if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)) {
					// #25638 - Start
					validateRemarks();
					// #25638 - End
					for (ScQtCommitteeActivity committeeActivity : getManagedBean().getCommitteeList()) {
						 if (null != committeeActivity && null != committeeActivity.getScCommitteeMember()
								 && null != committeeActivity.getScCommitteeMember().getMemberRole()
								 && "S".equalsIgnoreCase(committeeActivity.getScCommitteeMember().getMemberRole().getParameterCode())) {
							 secretaryUser = committeeActivity.getScCommitteeMember().getPmUser();
							 reqDTO.setPmUser(secretaryUser);
							 break;
						 }
					 }
				} 
				// #25638 - End
				reqDTO.setScQtReverse(getManagedBean().getScQtReverse());
				String committeeType = getManagedBean().getCommitteeType();
				CommitteResDTO committeeResDTO = getCommitteMemberByUserId();
				ScCommitteeMember committeeMember = committeeResDTO.getCommMember();
				// #25638 - Start.
				if (!BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome) && StringUtils.isNotBlank(committeeType) && 
						ParameterConst.COMMITTEE_ROLE_CHAIRPERSON.equalsIgnoreCase(committeeMember.getMemberRole().getParameterCode()) && 
						isDigiSigningRequired(committeeResDTO.getRoleCode(),
												this.getManagedBean().getCommitteeList())) {
					this.getManagedBean().setAutoUpdate(Boolean.FALSE);
					RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
				} else {
					// #28472 - Start.
					if (!BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome) && QTStatusConst.SF_PENDING_ENDORSEMENT.equals(currentStatusId)) {
						signOff();
					}
					// #28472 - End.
					updateBPMTask(outcome, null, reqDTO);
				}
				// #25638 - End.
			} else {
			   
				signOff();
				updateBPMTaskBrief(outcome, null, reqDTO);
			}
			//25638 - Start
			
			//25638 - End
		
			
		    
		    
		}
	}//#28834 end
		
//28834 starts
		private SuppFinalizationReqDTO preSubmitValidationbreif() {
			log.info("preSubmitValidation");
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
					
					//validateFinalizationReports(null);
					
//					if (CollectionUtils.isNotEmpty(meetingList)) {
//						boolean atleastOneMeetingIsDone = false;
//						for (ScQtMeeting meeting : meetingList) {
//							if ((DbValueConst.RECORD_STATUS_ACTIVE
//									== meeting.getRecordStatusFlg()) && 
//											QTConstants.SC_MEETING_DONE_STATUS_ID
//												.equals(meeting.getMeetingStatus()
//														   .getStatusId())) {
//								atleastOneMeetingIsDone = true;
//							}
//						}
//						if (!atleastOneMeetingIsDone) {
//							epException = new EpException(MessageConst.ERROR_760292);
//						}
//					} else {
//						epException = new EpException(MessageConst.ERROR_760291);
//					}
					
					if (epException == null) {
					
						// Validate Preparation tab details for committee members attendance
						try {
							validateInfobreif(0);
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
				} else {
					// need to set Keputusan report at time of Endorsement and signOff status
					// #28834- Start.
					// #34410 - Start
					QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
					qtBackingBean.retrieveQuationTenderDetails(this.getManagedBean().getScQt().getQtId());
					if (!isBpkCentralContract(qtBackingBean.getManagedBean().getQt())) {// #34410 - End
						this.getManagedBean().getScQtFinalization()
						.getScQtFinalizationReports()
						.addAll(this.getManagedBean().getEndorsementReports());
					}
					// #28834- End.
				}
				
				// Disabling kertas taklimat as per Bug #16172
				/*if(!scQtFinalization.isBriefingPaperUploaded()){
					navigateToBriefingPaperTab();
					throw new EpException(MessageConst.ERROR_760452);
				}*/
				//#27364 Start
				if (ParameterConst.DECISION_CANCEL
						  .equals(decisionTypeCode) || ParameterConst.DECISION_RE_PUBLISH
						  .equals(decisionTypeCode) || ParameterConst.DECISION_RE_EVALUATION
						  .equals(decisionTypeCode)){
					this.getManagedBean().setFinalizationMethod(null);
				}
				//#27364 End
//				if (this.getManagedBean().getFinalizationMethod() != null
//						&& (this.getManagedBean().getFinalizationMethod()
//								.equals(ParameterConst.RECOMMEND_BY_AWARD) || this
//								.getManagedBean().getFinalizationMethod()
//								.equals(ParameterConst.RECOMMEND_BY_SINGLE_AWARD))) {
//					if (this.getManagedBean().getJustification() == null
//							|| this.getManagedBean().getJustification()
//									.getReasonId() == null) {
//						//navigateToEndorsementReportTab();
//						throw new EpException(MessageConst.ERROR_100061,
//								new String[] { ResourceBundleUtils.getMessage(
//										"lbl.sc.sf.justification.selection",
//										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
//										new FacesScopes().getUserLocale()) });
//					}
//				}
				scQtCommittee = preparationDtlsInfo();
				
			}
			
			// Save Preparation and Supplier finalization tab(s) data 
			// before throwing exception
			//validateReEvaluation();
			//#27364 Start
			reqDTO = validateInfobreif(2);
			if (ParameterConst.DECISION_CANCEL
					  .equals(decisionTypeCode) || ParameterConst.DECISION_RE_PUBLISH
					  .equals(decisionTypeCode) || ParameterConst.DECISION_RE_EVALUATION
					  .equals(decisionTypeCode)){
				this.getManagedBean().setFinalizationMethod(null);
			}
			//#27364 End
			
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
			
			if (ParameterConst.DECISION_CANCEL
					  .equals(decisionTypeCode) && 
			CollectionUtils.isEmpty(
				scQtFinalization.getScQtFinalizationRemarks())) {
		navigateToSupplierFinalizationTab();
		initializeFinalizationRemarks();
		
		// Remarks are mandatory for QT cancellation
		throw new EpException(MessageConst.ERROR_760360);
	}
			
			
			
			
			/*
			 * Enhancement #21694
			 * SET MEETING MEMBER ACTIVITY
			 */
			reqDTO.setMeetingMembersActivity(getManagedBean().getCommitteeList());
			
			return reqDTO;
		}
		//28834 ends
		
		//#28777 & #29729 - Start
		/**
		* Method to display list of supplier branch had deleted or not with related current QT
		**/
		public void showSupplierBranchList(Long supplierId) {
			
			List<SmSupplierBranch> supplierBranchListDeleted = new ArrayList<>();
			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
		
		    SupplierSearchReqDTO suppSearchDeletedReqDTO = new SupplierSearchReqDTO();
		    suppSearchDeletedReqDTO.setSupplierId(supplierId);
	    	SupplierSearchResDTO suppSearchDeletedResDTO = supplierSearchSO.retrieveSmSupplierBranchDeletedBySupplier(suppSearchDeletedReqDTO);
			supplierBranchListDeleted = suppSearchDeletedResDTO.getSmSupplierBranchList();

			for(SmSupplierBranch supplierBranch : supplierBranchListDeleted) {
				int statusFlag = supplierBranch.getRecordStatusFlg();
				if(statusFlag == DbValueConst.RECORD_STATUS_DELETED){
					supplierBranch.setQtRelatedSupplierBranch(DbValueConst.FLAG_YES);
				}else{
					supplierBranch.setQtRelatedSupplierBranch(DbValueConst.FLAG_NO);
				}
			}
			
			if(CollectionUtils.isEmpty(supplierBranchListDeleted)){
	    		getManagedBean().setSupplierBranchList(listSupplierBranchList(supplierId));
	    	}else{
	    		supplierBranchListDeleted.addAll(listSupplierBranchList(supplierId));
	    		getManagedBean().setSupplierBranchList(supplierBranchListDeleted);
	    	}
			
		}
		
		private List<SmSupplierBranch> listSupplierBranchList(Long supplierId){
			
			List<SmSupplierBranch> supplierBranchList = new ArrayList<>();

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
			
			SupplierSearchReqDTO suppSearchReqDTO = new SupplierSearchReqDTO();
			suppSearchReqDTO.setSupplierId(supplierId);
	    	SupplierSearchResDTO suppSearchResDTO = supplierSearchSO.retrieveSmSupplierBranchBySupplier(suppSearchReqDTO);
	    	supplierBranchList = suppSearchResDTO.getSmSupplierBranchList();
	    	  
	    	return supplierBranchList;
		}
		//#28777 & #29729 - End
		
		//#32400 - Start
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
		//#32400 - End
		
		// 18685 - START.
		protected void checkKertasTaklimatReports(List<ScQtFinalizationReport> finalizationReports, List<String> reportTitleList, String taskType){
			if(CollectionUtils.isNotEmpty(finalizationReports)){
				int count=0;
				List<ScQtFinalizationReport> localListReport=new ArrayList<ScQtFinalizationReport>();
				Iterator<ScQtFinalizationReport> iterator = finalizationReports.iterator();
				//List<String> localTitle=new ArrayList<String>();
				List<ScQtFinalizationReport> titleList = new ArrayList<>(); // Bug #34720@Syazani 07/06/2022	//Sqube QT Master Bug G85 - Issues 21@Syazani 30/11/2023
				while(iterator.hasNext()){
					ScQtFinalizationReport finalizationReport = iterator.next();
					// Bug #34720@Syazani 07/06/2022 -- Start
					if (duplicateCheckFinalizationReportList(finalizationReport, titleList)) {
						iterator.remove();
						continue;
					}
					titleList.add(finalizationReport);
					// Bug #34720@Syazani 07/06/2022 -- End
					
					if(null!=finalizationReport.getPmDocument()){
						if (SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT.equalsIgnoreCase(finalizationReport.getPmDocument().getDocCategoryCode().getParameterCode())) {
							log.info("=======sourbasebackingBeanTitle===="+finalizationReport.getTitle());
							if(finalizationReport.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED)){
							log.info("=======mangatitlecodesexecutes========"+finalizationReport.getPmDocument().getDocName());	
							count++;
							localListReport.add(finalizationReport);
							continue;
						}
							// #18685 - Start
							if (null == getManagedBean().getNewBriefingPaperReportList()) {
								List<ScQtFinalizationReport> newBriefingPaperReportList = new LinkedList<>();
								getManagedBean().setNewBriefingPaperReportList(newBriefingPaperReportList );
							}
							// #18685 - End
							if (reportTitleList.contains(finalizationReport.getPmDocument().getDocDefinitionKey())) {
								getManagedBean().getNewBriefingPaperReportList().add(finalizationReport);
								// #18685 - Start
								/*if (QTStatusConst.QT_SUPPLIER_FINALIZATION_REVISION.longValue() == getManagedBean().getCurrentStatusId() 
										&& taskType.equals(SourcingConstants.SUPPLIER_FINALIZATION_TASK)) {
									finalizationReport.setPreviousReportContent(finalizationReport.getReportContent());
									saveScQtFinalizationReport(finalizationReport);
								}*/
								// #18685 - End
								iterator.remove();
								continue;
							} 
						} 
						
						if (taskType.equals(SourcingConstants.CONSOLIDATION_TASK) && finalizationReport.getPmDocument().getDocDefinitionKey().equals(SourcingConstants.DOC_DEF_KEY_UDPUPBM_REPORT)) {
							// #18685 - Start
							if (null == getManagedBean().getSfBriefingPaperReportList()) {
								List<ScQtFinalizationReport> sFBriefingPaperReportList = new ArrayList<>();
								getManagedBean().setSfBriefingPaperReportList(sFBriefingPaperReportList );
							}
							getManagedBean().getSfBriefingPaperReportList().add(finalizationReport);
							// #18685 - End
							iterator.remove();
							continue;
						}
						
						if (taskType.equals(SourcingConstants.SUPPLIER_FINALIZATION_TASK) && 
								(finalizationReport.getPmDocument().getDocDefinitionKey().equals(SourcingConstants.DOC_DEF_KEY_LATAR_BELAKANG_REPORT)
										|| finalizationReport.getPmDocument().getDocDefinitionKey().equals(SourcingConstants.DOC_DEF_KEY_TUJUAN_REPORT)
										|| finalizationReport.getPmDocument().getDocDefinitionKey().equals(SourcingConstants.DOC_DEF_KEY_METODOLOGI_PENILAIAN_REPORT)
										|| finalizationReport.getPmDocument().getDocDefinitionKey().equals(SourcingConstants.DOC_DEF_KEY_UDPUP_REPORT))) {
							// #18685 - Start
							if (null == getManagedBean().getConsolidationBriefingPaperReportList()) {
								List<ScQtFinalizationReport> consolidationBriefingPaperReportList = new ArrayList<>();
								getManagedBean().setConsolidationBriefingPaperReportList(consolidationBriefingPaperReportList );
							}
							getManagedBean().getConsolidationBriefingPaperReportList().add(finalizationReport);
							// #18685 - End
							iterator.remove(); 
							continue;
							
						}
						
					} 
					// #18685 - Start.
					if ("UP".equals(finalizationReport.getDefinationType().getParameterCode()) 
							&& !finalizationReport.getTitle().contains("Kertas Keputusan")) {
						// #18685 - #33167 Start.
						if (null == getManagedBean().getNewBriefingPaperReportList()) {
							List<ScQtFinalizationReport> newBriefingPaperReportList = new LinkedList<>();
							getManagedBean().setNewBriefingPaperReportList(newBriefingPaperReportList );
						}
						// #18685 -#33167  End.
						getManagedBean().getNewBriefingPaperReportList().add(finalizationReport);
						iterator.remove();
					}
					// #18685 - End.
				}
				
				// #18685 - Start
				if (taskType.equals(SourcingConstants.CONSOLIDATION_TASK) && CollectionUtils.isNotEmpty(getManagedBean().getNewBriefingPaperReportList())) {
					List<ScQtFinalizationReport>  tempList = new ArrayList<>();
					for (String reportName : reportTitleList) {
						for (ScQtFinalizationReport report : getManagedBean().getNewBriefingPaperReportList()) {
							if (reportName.equals(report.getPmDocument().getDocDefinitionKey())) {
								tempList.add(report);
								break;
							}
						}
					}
					// #18685 - Start.
					for (ScQtFinalizationReport report : getManagedBean().getNewBriefingPaperReportList()) {
						if ("UP".equals(report.getDefinationType().getParameterCode())) {
							tempList.add(report);
						}
					}
					// #18685 - End.
					getManagedBean().getNewBriefingPaperReportList().clear();
					getManagedBean().getNewBriefingPaperReportList().addAll(tempList);
				}
				// #18685 - End
				log.info("=========localListReportsizefirst===="+localListReport.size());
			/*	if(localListReport.size()>1){
					log.info("=========localListReportsize===="+localListReport.size());
					log.info("=========finalizationReportssize===="+finalizationReports.size());
				finalizationReports.addAll(localListReport);
				log.info("=========finalizationReportssize2222222===="+finalizationReports.size());
				localListReport=new ArrayList<ScQtFinalizationReport>();
				List<String> localTitleList=new ArrayList<String>();
				for(ScQtFinalizationReport localfinalReport:finalizationReports){
					if(!localTitleList.contains(localfinalReport.getTitle())){
						localTitleList.add(localfinalReport.getTitle());
						localListReport.add(localfinalReport)	;
					}
				}
				finalizationReports.clear();
				finalizationReports.addAll(localListReport);
				log.info("=========finalizationReportssize111111111===="+finalizationReports.size());
				}*/
				log.info("checkKertasTaklimatReports count"+count);
				if(count==1){
					log.info("enter count1"+count);
					for(ScQtFinalizationReport finalizationReport:finalizationReports){
						if(null!=finalizationReport.getPmDocument() && SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT.equalsIgnoreCase(finalizationReport.getPmDocument().getDocDefinitionKey())){
							finalizationReport.setKertasTaklimatUpload(Boolean.TRUE);	
						}
						
					}	
				}else if(count>1){
					log.info("enter countGreaterThan1"+count);
					 String SPACE = " ";
					  String reportTitle = ResourceBundleUtils.getMessage("lbl.sc.kertas.taklimat", 
					 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, SourcingConstants.MY_LOCALE) 
					 			+ SPACE + "(" + (count-1) + ")";
					  log.info("reportTitle"+reportTitle);
					for(ScQtFinalizationReport finalizationReport:finalizationReports){
						if(null!=finalizationReport.getPmDocument() && SourcingConstants.DOC_DEF_KEY_KERTAS_TAKLIMAT.equalsIgnoreCase(finalizationReport.getPmDocument().getDocDefinitionKey()) && reportTitle.equalsIgnoreCase(finalizationReport.getTitle()) ){
							finalizationReport.setKertasTaklimatUpload(Boolean.TRUE);	
							break;
						}
						
					}	
				}
			}
			
		}
		// 18685 - END.
		
		
	// #30567 - Start.
	/**
	 * @param scQtFinalizationReport
	 * @return
	 * @throws Exception
	 */
	public ScQtFinalizationReportAttachment downloadBriefingPaperReportForBidding(ScQtFinalizationReport scQtFinalizationReport) throws Exception {
	    ScQtFinalizationReportAttachment reportAttachment = null;
		if (null != getManagedBean().getCurrentStatusId() 
				&& (getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.SF_PENDING_BIDDING_FINALIZATION.longValue()
						|| getManagedBean().getCurrentStatusId().longValue() == QTStatusConst.SF_PENDING_BIDDING_FINALIZATION_REVISION.longValue()
						)) {
			reportAttachment = generateDocumentAttachment(scQtFinalizationReport, false);
		} else {
			reportAttachment = scQtFinalizationReport.getScQtFinalizationReportAttachment();

			if (reportAttachment == null) {
				reportAttachment = generateDocumentAttachment(scQtFinalizationReport, false);
			}
		}
		return reportAttachment;
	}
	// #30567 - End.
	
	// #28834 - Start. 
	/**
	 * @param scQtFinalizationReport
	 */
	private void saveGeneraratedReport(ScQtFinalizationReport scQtFinalizationReport) {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			List<ScQtFinalizationReport> briefingPaperReportList = new ArrayList<>();
			briefingPaperReportList.add(scQtFinalizationReport);
			reqDTO.setAddNewBriefingPaperReportList(briefingPaperReportList );
			scQtFinalizationReport = qtSuppFinalizationSO.saveFinalizationReports(reqDTO);
			int index = 0;
			// #18685 - Start.
			for (ScQtFinalizationReport report : getManagedBean().getDocumentList()) {
				if (report.getTitle().equalsIgnoreCase(scQtFinalizationReport.getTitle())) {
					getManagedBean().getDocumentList().set(index, scQtFinalizationReport);
					getManagedBean().setSelectedBriefingReport(scQtFinalizationReport);
					break;
				}
				index++;
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	// #28834 - End. 
	
	//#29562 - START
		/**
		 * Reset search field
		 */
		public void resetSelectedItemSupplierandRemarks() {
			//reset select all item list
			this.getManagedBean().setSelectAll(false);
			
			if (this.getManagedBean().getItemList() != null){
				//reset selected item list
				for(int i = 0; i < this.getManagedBean().getItemList().size();){
					if (this.getManagedBean().getItemList().get(i).isSelected()){
						this.getManagedBean().getItemList().get(i).setSelected(false);
						//reset selected suppliers
						if (this.getManagedBean().getItemList().get(i).getSelectedSuppliers() != null){
							this.getManagedBean().getItemList().get(i).getSelectedSuppliers().clear();
							ScQtRecommend itemList = this.getManagedBean().getItemList().get(i);
							getSupplierCodes(itemList);
						}
						i++;
					}
					else{
						break;
					}
				}
			}
			
			if (	//#29562 - START.
					null != this.getManagedBean().getShortlistedSuppliersByRecommendType()
					&& 
					//#29562 - END.
					this.getManagedBean().getShortlistedSuppliersByRecommendType().getList() != null){
				//reset selected suppliers in supplier list table
				for(int j = 0; j < this.getManagedBean().getShortlistedSuppliersByRecommendType().getList().size();){
					if (this.getManagedBean().getShortlistedSuppliersByRecommendType().getList().get(j).isShortListed()){
						this.getManagedBean().getShortlistedSuppliersByRecommendType().getList().get(j).setShortListed(false);
						j++;
					}
					else{
						break;
					}
				}
			}

			if (this.getManagedBean().getScRemarks() != null){
				//reset remarks
				for(int k = 0; k < this.getManagedBean().getScRemarks().size(); k++){
					if (this.getManagedBean().getScRemarks().get(k).getRemark() != null){
						this.getManagedBean().getScRemarks().get(k).setRemark(null);
					}
					else{
						break;
					}
				}
			}
			
			//reset selected supplier for award
			this.getManagedBean().setRecommendedSupplier(null);
			this.getManagedBean().setFinalizationMethodBefore(getManagedBean().getFinalizationMethod());
		}
		
		/**
		* Reset finalization method
		*/
		public void resetFinalizationMethod() {
			this.getManagedBean().setFinalizationMethod(getManagedBean().getFinalizationMethodBefore());
			finalizationMethodEvent(null);
		}
		//#29562 - END
		
		
		public void DateTimeChange(ScQtMeeting meeting){//32613-start
	  		
	  		List<ScQtMeeting> meetungList=getManagedBean().getMeetingList();
	  		int index=	meetungList.indexOf(meeting);
	  	Date MeetingDate=	meeting.getMetingDate();
	  	String MeetingHour=meeting.getMeetDateStr();
		String MeetingMinute=meeting.getMeetTimeStr();
		if(null!=MeetingDate && null!=MeetingHour && null!=MeetingMinute){
			
			int hourValue = Integer.parseInt(MeetingHour);
			int minuteValue = Integer.parseInt(MeetingMinute);
			
			Calendar c1 = Calendar.getInstance();
			c1.setTime((Date)MeetingDate);
			c1.set(Calendar.HOUR_OF_DAY, hourValue);
			c1.set(Calendar.MINUTE, minuteValue);
			
			//dateValue = c1.getTime();
			meeting.setMeetingDate(c1.getTime());
			
		   meetungList.set(index, meeting);
		  this. getManagedBean().setMeetingList(meetungList);
		 
			
		}
	  	}//32613-End
		
		private String getDateValues(String hourMinute){//32613-start
		log.info("======displaydatevalues======="+hourMinute);	
		String hourMinutes="0";
		if (hourMinute.equals("00")) {
			hourMinutes = "0";
		} else if (hourMinute.equals("01")) {
			hourMinutes = "1";
		} else if (hourMinute.equals("02")) {
			hourMinutes = "2";
		} else if (hourMinute.equals("03")) {
			hourMinutes = "3";
		} else if (hourMinute.equals("04")) {
			hourMinutes = "4";
		} else if (hourMinute.equals("05")) {
			hourMinutes = "5";
		} else if (hourMinute.equals("06")) {
			hourMinutes = "6";
		} else if (hourMinute.equals("07")) {
			hourMinutes = "7";
		} else if (hourMinute.equals("08")) {
			hourMinutes = "8";
		} else if (hourMinute.equals("09")) {
			hourMinutes = "9";
		}else{
			hourMinutes=hourMinute;	
		}
		log.info("====displayhourminuefinal=========="+hourMinutes);
		return hourMinutes;
		}//32613-End
		
		//#33174 disable package if there is no supplier qualified by package #21695 - Start
		private void disablePacSF(Long qtId) {

			IQTSuppFinalizationSO qtSuppFinalizationSO = null;

			try {
				qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);

				SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
				reqDTO.setQtId(qtId);
				boolean isPacDisable = false;

				if (this.getManagedBean().getScQt().getEvaluateBy()
						.equalsIgnoreCase(SourcingConstants.EVALUATE_BY__ITEM)) {
					isPacDisable = true;
				} else if (this
						.getManagedBean()
						.getScQt()
						.getEvaluateBy()
						.equalsIgnoreCase(
								SourcingConstants.EVALUATE_BY_BOTH_PACKAGE_ITEM)) {

					int check = qtSuppFinalizationSO
							.retrieveSuppliersNotQualified(qtId);
					if (check == 0) {
						isPacDisable = true;
						this.getManagedBean().getScQt()
								.setEvaluateBy(SourcingConstants.EVALUATE_BY__ITEM);
					}
				}
				this.getManagedBean().setOfferAll(isPacDisable);
			} finally {
				qtSuppFinalizationSO = null;
			}
		}
			//#33174 disable package if there is no supplier qualified by package #21695 - End
				
	// #21692 - start
	public boolean checkMeetingDoneLessThanValidityEndDate(
			Date proposalValidityEndDate) {
		boolean lessThanValidityEndDate = false;

		List<ScQtMeeting> meetingDetailsList = this.getManagedBean()
				.getMeetingList();
		
		// #28834 - Start.
		if (CollectionUtils.isNotEmpty(meetingDetailsList)) {
			for (ScQtMeeting meeting : meetingDetailsList) {
				//sonarQube Issue 3 #91 # Vinod #Start
				boolean meetingStatusAndDate = null != meeting.getMeetingStatus()
						&& null != meeting.getMeetingStatus().getStatusId()
						&& QTStatusConst.MS_DONE.equals(meeting.getMeetingStatus().getStatusId())
						&& meeting.getMeetingDate().before(proposalValidityEndDate);
				if ((this.getManagedBean().isSecretaryUser() && meetingStatusAndDate && meeting.getRecordStatusFlg()
						.equals(DbValueConst.RECORD_STATUS_ACTIVE)) || meetingStatusAndDate) {
					lessThanValidityEndDate = true;
				} else {
					lessThanValidityEndDate = false;
				}
				//sonarQube Issue 3 #91 # Vinod #End
			}
		}
		// #28834 - End.
		return lessThanValidityEndDate;
	}
	// #21692 - end
	//6906 note 117 - start
	private void evaluateDecisionList() {
		ScQt scQt = this.getManagedBean().getScQt();
		IQTProposalSO proposalSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTProposalSO.class);
		ProposalReqDTO reqDTO = new ProposalReqDTO();
		reqDTO.setQtId(scQt.getQtId());
		Long proposalSubmitted = proposalSO.getTotalSubmittedProposal(reqDTO).getTotalSubmitted();
		this.getManagedBean().setNoProposalSubmitted(proposalSubmitted.compareTo(0L) < 0);
		if (!this.getManagedBean().isNoProposalSubmitted()) {
			List<String> disqualifiedStages = getQTSuppFinalizationSO().retrieveDisqualifiedStages(scQt.getQtId());
			this.getManagedBean().setDisqualifiedStages(disqualifiedStages);
		}
		boolean disabledSelection = !((this.getManagedBean().isInitialDecisionFwdToBPK() && !this.getManagedBean()
				.isReadOnly()) || this.getManagedBean().isSecretaryUser()) || this.isQTMaintenanceScreen();
		// || this.getManagedBean().isReversed();
		this.getManagedBean().setDisabledSelection(disabledSelection);
		boolean proposalValid = this.checkProposalValidityDateReach(scQt.getProposalValidityEndDate());
		boolean meetingDoneBeforeproposalValidityExpired = this.checkMeetingDoneLessThanValidityEndDate(scQt
				.getProposalValidityEndDate());// #21692
		if (disabledSelection) {
			this.getManagedBean().setDecisionDropdown(this.getDecisionDropDownAll());
		} else if (proposalValid || meetingDoneBeforeproposalValidityExpired) {// #21692
			this.getManagedBean().setDecisionDropdown(this.getDecisionDropDown());
		} else if (!proposalValid && !meetingDoneBeforeproposalValidityExpired) {// #21692
			this.getManagedBean().setDecisionDropdown(this.getDecisionDropDownExpiredProposal());
		}
		if (this.getManagedBean().getDecisionDropdown() != null
				&& this.getManagedBean().getDecisionDropdown().length > 0) {
			for (SelectItem item : this.getManagedBean().getDecisionDropdown()) {
				if (item.getValue() != null && ParameterConst.DECISION_RECOMMEND_SUPPLIER.equals(item.getValue())) {
					this.getManagedBean().setSupplierFinalizationReadOnly(false);
					break;
				}
			}
		}
	}
	//6906 note 117 - end
	
	// Bug #32682 #27502 & #34921 - Start
	/**
	 * to set user id for LoiLoaById based on LoiLoaBy
	 * @param scQtFinalization
	 * @param currentStatusId
	 */
	private void setUserLoiLoaPreparedBy(ScQtFinalization scQtFinalization, Long currentStatusId) {
		if (scQtFinalization.getLoiLoaBy() != null
				&& (QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION.equals(currentStatusId)
					|| QTStatusConst.QT_SUPPLIER_FINALIZATION_REVISION.equals(currentStatusId) 
					|| QTStatusConst.SF_MOF_COMMITTEE.equals(currentStatusId))) {
			if (scQtFinalization.getLoiLoaBy().equalsIgnoreCase(SourcingConstants.SC_LETTER_CREATOR_SECRETARY)) {
				scQtFinalization.setLoiLoaById(getUserIdSessionFromLoginId());
			} else if (scQtFinalization.getLoiLoaBy().equalsIgnoreCase(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER)) {
				scQtFinalization.setLoiLoaById(getActorUserIdByQtIdAndRoleCode(scQtFinalization.getScQt().getQtId()));
			}
		}
	}
	// Bug #32682 #27502 & #34921 - end
	
	//Bug #32682 start
	private Long getActorUserIdByQtIdAndRoleCode(Long qtId) {
		Long userId = null;
		IScActorSO actorSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IScActorSO.class);

		ScActorReqDTO reqDTO = new ScActorReqDTO();
		reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
		reqDTO.setDocId(qtId);
		reqDTO.setRoleCode(RoleCode.DESK_OFFICER.value());

		ScActor scActor = actorSO.retrieveActorByTypeIdAndRoleCode(reqDTO).getScActor();

		if (scActor != null) {
			userId = scActor.getUserId();
		}
		return userId;
	}
	//Bug #32682 end
	
	// Bug #34720@Syazani 07/06/2022 -- Start
	/**
	* Check for duplicate report related to UUM Definiation Key Report
	* This issue occur in production Bug #34720
	*/
	private boolean duplicateCheckFinalizationReportList(ScQtFinalizationReport finalizationReport,
			List<ScQtFinalizationReport> titleList) {
		String defKey = finalizationReport.getPmDocument() != null ? finalizationReport.getPmDocument()
				.getDocDefinitionKey() : "";
		if (titleList != null && !titleList.isEmpty()) {	// Sqube QT Master Bug G85 - Issues 22@Syazani 30/11/2023
			for (ScQtFinalizationReport sfReportTemp : titleList) {
				String defKeyTemp = sfReportTemp.getPmDocument() != null ? sfReportTemp.getPmDocument()
						.getDocDefinitionKey() : "";
				if (finalizationReport.getTitle().trim().equalsIgnoreCase(sfReportTemp.getTitle().trim())
						&& defKey.equalsIgnoreCase(defKeyTemp)
						&& defKey.equalsIgnoreCase(SourcingConstants.DOC_DEF_KEY_UDPUPBM_REPORT)) {
					return true;
				}
			}
		}
		return false;
	}
	// Bug #34720@Syazani 07/06/2022 -- End
	
	//#35129 Start
	private void loadRecommededSuppliersItemize() {
 
		boolean itemized = false;
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		String recommendType = scQtFinalization.getRecommendedTypeId().getParameterCode();
		String finalizationMethod = getManagedBean().getFinalizationMethod();

		for (ScQtRecommend checkrecmddtls : scQtFinalization.getScQtRecommends()) {
			if (CollectionUtils.isEmpty(checkrecmddtls.getRecommendDtls())) {
				itemized = ParameterConst.RECOMMEND_TYPE_ITEM.equals(recommendType) ? true : false;
				this.getManagedBean().setItemize(itemized);

				setSelectionMode(finalizationMethod);
				loadRecommededSuppliersIntoCachedData(itemized);
				break;
			}
			break;
		}
	}//#35129 End
	
	//35157 - start
	/**
	* Evaluate if supplier is disabled
	* @param ScQtEvaluationResultDtl
	* @return boolean 
	*/
	public boolean evaluateSupplierDisabled(ScQtEvaluationResultDtl evalResultDtl){
		Boolean isRefinalized = this.getManagedBean().getQtRefinalized();
		isRefinalized = null == isRefinalized ? false : isRefinalized;
		boolean isBidding = ParameterConst.RECOMMEND_BY_ONLINE_BIDDING.equals(this.getManagedBean()
				.getScQtFinalization().getFinalizationMethod().getParameterCode())
				&& !isRefinalized;
		ScQtProposal proposal = evalResultDtl.getQtEvaluationResult().getScQtProposal();
		SmSupplier supplier = proposal.getScQtSupplier().getSmSupplier();
		boolean periodExtensionRejected = proposal.getIsAgreeQtExtendPeriod() == null ? false : proposal
				.getIsAgreeQtExtendPeriod().equals(SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_REJECTED) ||
				proposal.getIsAgreeQtExtendPeriod().equals(SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_NOT_QUALIFIED);//#21692 include not qualified;
		boolean isBPKTaskAndClaimed = this.getManagedBean().isInitialDecisionFwdToBPK()
				&& !this.getManagedBean().isReadOnly(); 
		return Arrays.asList(
				new Boolean[] {
						evalResultDtl.isSuppSelectionDisabled(),
						this.getManagedBean().isDisableSupplierSelection(),
						isBidding,
						(supplier.isDisciplinaryActions() && !supplier.isWarningDA()),
						periodExtensionRejected,this.isQTMaintenanceScreen(),
						!(this.getManagedBean().isSecretaryUser() || isBPKTaskAndClaimed) }).contains(Boolean.TRUE); //#28777 & #29729 
	}
	//35157 - end
	
	//#12487 - start Prepare LOI/LOA Notification
	/**
	 * To send notification for email and dashboards
	 * @param docType
	 */
	public void prepareNotification(String docType){
		FacesScopes facesScopes = new FacesScopes();
		SuppFinalizationReqDTO suppFinalizationReqDTO = new SuppFinalizationReqDTO();
		suppFinalizationReqDTO.setScQt(this.getManagedBean().getScQt());
		suppFinalizationReqDTO.setUserLocale(facesScopes.getUserLocale());
			
		if (getManagedBean().getScQtFinalization() != null && 
			getManagedBean().getScQtFinalization().getLoiLoaById() != null) {
			
			long currentUser = new FacesScopes().getUserSessionBean().getUserId();
			long assignUser	 = getManagedBean().getScQtFinalization().getLoiLoaById();				
			
			Long profileId = suppFinalizationReqDTO.getScQt().getPmOrgProfile().getOrgProfileId();
			Date createdDate = this.getManagedBean().getScQtFinalization().getCreatedDate();
			String ptjName = retrievePTJByOrgProfileId(profileId, createdDate);
			String ptjCode = retrievePTJCodeByOrgProfileId(profileId, createdDate);
			PmOrgProfile pmOrgProfile = retrieveMinistryOrgProfile(profileId);
			String ministryName = pmOrgProfile.getPmOrgValidity().getOrgName();
			log.info(ptjName);
			log.info(ptjCode);
			log.info(ministryName);
		
			getQTSuppFinalizationSO().sendPrepareLOILOANotification(suppFinalizationReqDTO,assignUser,ptjName,ptjCode,ministryName,currentUser,docType,CommonConstants.DASHBOARD);
			getQTSuppFinalizationSO().sendPrepareLOILOANotification(suppFinalizationReqDTO,assignUser,ptjName,ptjCode,ministryName,currentUser,docType,CommonConstants.EMAIL);
		}else{
			SLAUtils.getInstance().logSLAInfo(ModuleCodeConst.SUPPLIER_SELECTION,
					ParameterConst.DOCTYPE_QUOTATION.toUpperCase(),
					"SupplierFinalizationBackingBean.prepareNotification : No notification send", 
					System.currentTimeMillis());
		}	
	}
	//#12487 - end
	
	// Bug #35485@Syazani 08/02/2024 -- Start
	private void regenEvaluationDownload(ScAttachment reportAttachment, boolean downloadable) throws Exception {
		String fileName = StringUtils.isNotBlank(reportAttachment.getFileName()) ? reportAttachment.getFileName() : "";
		fileName = CustomUtils.getInstance().removeFileExtension(fileName);
		this.getManagedBean().setReportDownloadFileName(fileName);
		downloadAllReports(getManagedBean().getScQt(), "evaluation", reportAttachment, downloadable);
	}
	// Bug #35485@Syazani 08/02/2024 -- End	
	
}
// ***19336