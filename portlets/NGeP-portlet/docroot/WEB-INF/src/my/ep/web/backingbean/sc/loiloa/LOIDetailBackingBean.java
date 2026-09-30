/**
 * Copyright � 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.sc.loiloa;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp; //#38151
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date; //38151
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

import my.ep.biz.bean.sc.QtCommitteeMemberBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO; //#38151
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MapKeyEnum;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.QTSearchBeanConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.BpmConstants;
import my.ep.constants.CommonConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SpecificationReqDTO;
import my.ep.dto.sc.quationtender.SpecificationResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO; //#38151
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO; //#38151
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.helper.Paginator;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmAppVersion; //#38151
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.common.ScAddress;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoi;
import my.ep.persistence.entity.sc.loaloi.ScLoiApproval;
import my.ep.persistence.entity.sc.loaloi.ScLoiAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoiForQt;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;
import my.ep.persistence.entity.sc.loaloi.ScLoiNegoLocation;
import my.ep.persistence.entity.sc.loaloi.ScLoiRemark;
import my.ep.persistence.entity.sc.loaloi.ScLoiReport;
import my.ep.persistence.entity.sc.loaloi.ScLoiReportAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoiSupportingDoc;
import my.ep.persistence.entity.sc.loaloi.ScLoiSupportingDocAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoiTerm;
import my.ep.persistence.entity.sc.loaloi.ScLoiWorkFlowStatus;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMemberHistory;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreSetting;//#38151
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalItem;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.ApprovalAction;
import my.ep.type.sc.LoiLoaDocType;
import my.ep.util.CustomUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.sc.supplierfinalization.SupplierFinalizationBiddingBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.LoiLoaBean;
import my.ep.web.bean.sc.quatationtender.UserCommitteeSearchBean;
import my.ep.web.bean.sc.quatationtender.WeightageScoreBean; //#38151
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.constant.sc.SLALogConstant;
import my.ep.web.managedbean.sc.loiloa.LOIDetailManagedBean;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;
import org.primefaces.model.StreamedContent;

// TODO: Auto-generated Javadoc
/**
 * The Class LOIDetailBackingBean.
 * 
 * @author Viswanath
 * 
 * @author Sri
 * 
 * @author Praneeth
 */
@ManagedBean(name = "lOIDetailBackingBean")
@RequestScoped
public class LOIDetailBackingBean extends
		SourcingBaseBackingBean<LOIDetailManagedBean> {
	private static final long serialVersionUID = 1L;

	/** The log. */
	public static Logger log = EpLogger.getLogger(LOIDetailBackingBean.class);

	/** The Constant LOI_DETAILS_MAIN_MENU. */
	public static final String LOI_DETAILS_MAIN_MENU = "/xhtml/sc/loi/loiDetailsMain.xhtml";

	/** The Constant LOI_DETAILS_MENU. */
	public static final String LOI_DETAILS_MENU = "/xhtml/sc/loi/loiDetailsMenu.xhtml";

	/** The Constant LOI_AWARD_DETAIL_MENU. */
	public static final String LOI_AWARD_DETAIL_MENU = "/xhtml/sc/loi/loiAwardDetailsMenu.xhtml";

	/** The Constant LOI_NEGO_DETAIL_MENU. */
	public static final String LOI_NEGO_DETAIL_MENU = "/xhtml/sc/loi/loiNegotiationDetailsMenu.xhtml";

	/** The Constant LOI_APPROVAL_MENU. */
	public static final String LOI_APPROVAL_MENU = "/xhtml/sc/loi/loiApprovalMenu.xhtml";

	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;

	/** The text before sign. */
	private String textBeforeSign;

	/** The flag. */
	private boolean flag;
	
	
	
	
	private List<ScLoi> loiList;
	
	/**
	 * Instantiates a new LOI detail backing bean.
	 */
	public LOIDetailBackingBean() {
		log.info("inside LOIDetailBackingBean constructor");
		log.info("log.isDebugEnabled() : "+log.isDebugEnabled());
		// populateUserMinistryList();
		// initializeRemarks(new ScLoi());
		// getApproverNames();
	}
	
	/**
	 * 
	 *  
	 * @param qtId - set null if provided by SCAwardSupplierDataType 
	 * @param otherArgs - set null if provided by SCAwardSupplierDataType
	 *		isQtMaintenanceScreen - Boolean
	 *		isReadOnly - Boolean
	 *		prevPageUrl - String
	 *		
	 */
	private void preRenderLoiListViewPriv(Long qtId, Map<MapKeyEnum, Object> otherArgs){
		try {
			
			boolean fromBpmTaskList = false;
			SCAwardSupplierDataType awardDataType = null;
			Long sourceId = null;
			String letterCreator = null;
			String currentUser = new FacesScopes().getUserSessionBean().getLoginId();
			if(qtId == null){
				fromBpmTaskList = true;
				awardDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);
				sourceId = Long.valueOf(awardDataType.getSourceId()); 
				letterCreator = awardDataType.getLetterCreator();
				if(StringUtils.isBlank(letterCreator)) {
					if (new FacesScopes()
					.hasRoleUser(RoleCode.DESK_OFFICER.value())) {
						awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER);
						awardDataType.setDeskOfficer(currentUser);
					} else {
						awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_REQUISITIONER);
						awardDataType.setSecretary(currentUser);
					}
				} else {
					if(letterCreator.equals(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER)) {
						awardDataType.setDeskOfficer(currentUser);
					} else {
						awardDataType.setSecretary(currentUser);
					}
				} 
				getManagedBean()
				.setRecommendDtlIds(awardDataType.getGroupkeyList());
		
				String finalizationMethod = getFinalizationMethodByAwardType(awardDataType.getAwardType());
				getManagedBean().setDocType(finalizationMethod);
			}
			else{
				this.getManagedBean().setReadOnly(true);
				fromBpmTaskList = false;
				sourceId = qtId;
				//letterCreator
				//finalizationMethod
			}

			
			  
			
			 
			
			
			
			getManagedBean().setSourceId(sourceId);
			
			
			retrieveLOIList(sourceId);
			
			if(!fromBpmTaskList && !CollectionUtils.isEmpty(getManagedBean().getLoiLoas())){
				for(ScLoiLoa ll: getManagedBean().getLoiLoas()){
					if(!CollectionUtils.isEmpty(ll.getScLois())){
						for(ScLoi l: ll.getScLois()){
							AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
							awardSupplierReqDTO.setLoiId(l.getLoiId());
							AwardSupplierResDTO awardSupplierResDTO = getQtAwardSO()
							.retrieveLOIDtlByLoiId(awardSupplierReqDTO);
							String finalizationMethod = null;
							if(awardSupplierResDTO.isSuccessful()){
								finalizationMethod = getFinalizationMethodByAwardType(awardSupplierResDTO.getScLoi().getRecommendBy().getParameterCode());
								getManagedBean().setDocType(finalizationMethod);
							}														
						}
					}
				}
			}
			
			//QT WIP weightage flag
			checkWeightage35384WIP(); //#38151

			// QT Proposal validity expired
			getQtProposalValidExpiredMessage(getManagedBean().getScQt());
			
			//qtMaintenanceScreen
			if(isQTMaintenanceScreen()){
				// ***25201#35 
				//set status
				if(getManagedBean().getScQt() != null && StringUtils.isBlank(getManagedBean().getStatus())){
					ScWorkFlowStatusReqDTO wsReqDTO = new ScWorkFlowStatusReqDTO();
					wsReqDTO.setDocId(getManagedBean().getScQt().getQtId());
					wsReqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
					ScWorkFlowStatusResDTO wsResDTO = getScWorkflowStatusSO().retrieveCurrentWorkFlowStatus(wsReqDTO);
					if(wsResDTO != null && wsResDTO.isSuccessful()){
						if(!CollectionUtils.isEmpty(wsResDTO.getWorkFlowStatusList())){
							getManagedBean().setStatus(getStatusDescById(wsResDTO.getWorkFlowStatusList().get(0).getPmStatus().getStatusId()));
						}
					}
				}
				
				// ***19287#91
				getManagedBean().setReadOnly(true);
			}
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.LOI_LIST_PAGE);
		} catch (Exception e) {
			handleException(e);
		} 
	}
	
	
	public void preRenderLoiListView(Long qtId, String previousPageUrl){
		if(!StringUtils.isBlank(previousPageUrl)){
			this.getManagedBean().setBackToPreviousPage(previousPageUrl);
		}
		preRenderLoiListViewPriv(qtId, null);
	}

	/**
	 * Pre render page navigation to loi list view page based on task id
	 * 
	 * @return void
	 */
	public void preRenderLoiListView() {
		debugLogger("preRenderLoiListView");

		try {

			SCAwardSupplierDataType awardDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);

			Long sourceId = Long.valueOf(awardDataType.getSourceId());
			
			String letterCreator = awardDataType.getLetterCreator();
			String currentUser = new FacesScopes().getUserSessionBean().getLoginId();
			
			if(StringUtils.isBlank(letterCreator)) {
				if (new FacesScopes()
				.hasRoleUser(RoleCode.DESK_OFFICER.value())) {
					awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER);
					awardDataType.setDeskOfficer(currentUser);
				} else {
					awardDataType.setLetterCreator(SourcingConstants.SC_LETTER_CREATOR_REQUISITIONER);
					awardDataType.setSecretary(currentUser);
				}
			} else {
				if(letterCreator.equals(SourcingConstants.SC_LETTER_CREATOR_DESK_OFFICER)) {
					awardDataType.setDeskOfficer(currentUser);
				} else {
					awardDataType.setSecretary(currentUser);
				}
			} 

			getManagedBean().setSourceId(sourceId);
			getManagedBean()
					.setRecommendDtlIds(awardDataType.getGroupkeyList());
			
			String finalizationMethod = getFinalizationMethodByAwardType(awardDataType.getAwardType());
			getManagedBean().setDocType(finalizationMethod);
			
			retrieveLOIList(sourceId);

			// QT Proposal validity expired
			getQtProposalValidExpiredMessage(getManagedBean().getScQt());
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.LOI_LIST_PAGE);
		} catch (Exception e) {
			handleException(e);
		} 
	}
	
	private String getFinalizationMethodByAwardType(String awardType) {		
		if (SourcingConstants.AWARD_TYPE_BIDDING_AWARD.equals(awardType)) {
			return ParameterConst.RECOMMEND_BY_ONLINE_BIDDING;
		} else if (SourcingConstants.AWARD_TYPE_MULTIPLE_LOI_AWARD.equals(awardType)) {
			return ParameterConst.RECOMMEND_BY_MULTIPLE_LOI;
		} else {
			return ParameterConst.RECOMMEND_BY_AWARD;
		}
	}

	/**
	 * Pre render page navigation to loi page based on task id
	 * 
	 * @return void
	 */
	public void preRenderView() {
		debugLogger("preRenderView");
		preRenderViewPriv(null, null);
		// #29007	Vinod Start
		String message=	ResourceBundleUtils.getMessage("lbl.sc.loiloa.suppliers.proposal.specification.update",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale());
		getManagedBean().setLoiSupplierProposalspec(message);
					ScLoi scLoi = getManagedBean().getScLoi();
					Long statusId =scLoi.getScWorkFlowStatus().getPmStatus().getStatusId();//20230711@Asyrafhaziq-SQUBE Bug Minor #16
		isSupplierEPVStatusRejected(scLoi);// #21692
		getSupplierRejectProposalValidityExtensionNotification(scLoi);// #21692
		
					List<ScLoiTerm> scNegoTerms = scLoi.getScNegoTerms();
					if(null == scNegoTerms) {
						scNegoTerms = new ArrayList<ScLoiTerm>();
					}
						
					int record = 0;
					if (QTStatusConst.LOI_PENDING_NEGOTIATION.equals(statusId)) {
						if(scNegoTerms.size() > 0){
							for(ScLoiTerm scLoiTerm : scNegoTerms){
								if(scLoiTerm.getTermDesc().equals(message)){
									record++;
								}
							}
						}
						if(record > 0){
							this.getManagedBean().setRenderInstruction(true);
						}else{
							this.getManagedBean().setRenderInstruction(false);
						}
					}else if(QTStatusConst.LOI_PENDING_SUBMISSION.equals(statusId)){
						this.getManagedBean().setRenderInstruction(true);
					}else{
						this.getManagedBean().setRenderInstruction(false);
					}
		// #29007	Vinod End
					
					//#30008 - Start	
					if(scLoi.getNegoDecision() != null){
						this.getManagedBean().setDecisionCode(String.valueOf(scLoi.getNegoDecision().getParameterId()));
					}
					//#30008 - End
					
	}
	
	/**
	 * 
	 * @param loiId
	 */
	public void preRenderViewForQtMaintenenceScreen(Long loiId, String prevPageURL){
		Map<MapKeyEnum, Object> otherArgs = new HashMap<MapKeyEnum, Object>();
		otherArgs.put(MapKeyEnum.prevPageURL, prevPageURL);
		otherArgs.put(MapKeyEnum.isQtMaintenanceScreen, Boolean.TRUE);
		otherArgs.put(MapKeyEnum.isReadOnly, Boolean.TRUE);
		otherArgs.put(MapKeyEnum.enableDownloadAllDocuments, Boolean.TRUE);
		preRenderViewPriv(loiId, otherArgs);
	}
	
	
	/**
	 * 
	 *  
	 * @param loiId - set null if provided by SCAwardSupplierDataType 
	 * @param otherArgs - set null if provided by SCAwardSupplierDataType
	 *		isQtMaintenanceScreen - Boolean
	 *		isReadOnly - Boolean
	 *		prevPageUrl - String
	 *		
	 */
	private void preRenderViewPriv(Long loiId, Map<MapKeyEnum, Object> otherArgs){
		//boolean isQtMaintenanceScreen = otherArgs != null && otherArgs.containsKey(MapKeyEnum.isQtMaintenanceScreen) && (Boolean)otherArgs.get(MapKeyEnum.isQtMaintenanceScreen)!=null?((Boolean)otherArgs.get(MapKeyEnum.isQtMaintenanceScreen)).booleanValue():false;
		boolean isQtMaintenanceScreen = isQTMaintenanceScreen();
		try {
			SCAwardSupplierDataType awardDataType = null;
			Long sourceId = null;
			Boolean isEditFlag = null;
			String prevPageUrl = null;
			if(isQtMaintenanceScreen){
				prevPageUrl = new MapKeyEnum.ValueRetriever<String>().getValue(otherArgs, MapKeyEnum.prevPageURL);
				Boolean readOnly = new MapKeyEnum.ValueRetriever<Boolean>().getValue(otherArgs, MapKeyEnum.isReadOnly); 
				isEditFlag = readOnly!=null?!readOnly:Boolean.FALSE;
				getManagedBean().setEnableDownloadAllDocuments(new MapKeyEnum.ValueRetriever<Boolean>().getValue(otherArgs, MapKeyEnum.enableDownloadAllDocuments));
			}
			else{
				awardDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);
				sourceId = Long.valueOf(awardDataType.getSourceId());
				getManagedBean().setRecommendDtlIds(awardDataType.getGroupkeyList());
				loiId = Long.valueOf(awardDataType.getAwardId());
				isEditFlag = Boolean.TRUE;
				
			}
			
			Long currentStatusId = getManagedBean().getCurrentStatusId();

			if(currentStatusId != null){
				getManagedBean().setDocType(getPmStatusByStatusId(currentStatusId).getDocType());
				getManagedBean().setSourceId(sourceId);
			}
			
			if(prevPageUrl != null){
				getManagedBean().setBackToPreviousPage(prevPageUrl);
			}

			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO.setLoiId(loiId);
			AwardSupplierResDTO awardSupplierResDTO = getQtAwardSO()
					.retrieveLOIDtlByLoiId(awardSupplierReqDTO);
			if (awardSupplierResDTO.getScLoi() != null) {
				ScLoi scLoi = awardSupplierResDTO.getScLoi();
				editLOI(scLoi, isEditFlag);
			}
			getManagedBean().setEditFlag(Boolean.FALSE);
			
		} catch (Exception e) {
			handleException(e);
		} finally {
			// qtAwardSO = null;
		}
	}

	/**
	 * Pre render page navigation to suppler response page based on task id
	 * 
	 * @return void
	 */
	public void preRenderSupplierView() {
		debugLogger("preRenderSupplierView");
		
		SCAwardSupplierDataType dpDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);

		Long sourceId = Long.valueOf(dpDataType.getSourceId());
		Long loiId = Long.valueOf(dpDataType.getAwardId());
		
		String redirectUrl = retrieveSupplierLOIDtl(sourceId, loiId, null, false);

		if (StringUtils.isNotBlank(redirectUrl)) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, redirectUrl);	
		}
	}
	
	
	// ***25201
	/**
	 * Edits the supplier loi action.
	 * 
	 * @param sourceId 
	 * 		the source id (qt id / rn id
	 * @param loiId 
	 * 		the loi id
	 * @param previousPageUrl
	 * 		the previous page url.
	 * @param isReadonly
	 * 		the readonly flag
	 * @return the supplier loi page
	 */
	public String retrieveSupplierLOIDtl(Long sourceId, Long loiId, String previousPageUrl, boolean isReadonly){
		return retrieveSupplierLOIDtlPriv(sourceId, loiId, previousPageUrl, isReadonly, null);
	}
	
	/**
	 * Edits the supplier loi action.
	 * 
	 * @param sourceId 
	 * 		the source id (qt id / rn id
	 * @param loiId 
	 * 		the loi id
	 * @param previousPageUrl
	 * 		the previous page url.
	 * @param isReadonly
	 * 		the readonly flag
	 * @param enableDownloadAllDocuments
	 * 		either user can download all attachments/documents or not
	 * 
	 * @return the supplier loi page
	 */
	public String retrieveSupplierLOIDtl(Long sourceId, Long loiId, String previousPageUrl, boolean isReadonly, boolean enableDownloadAllDocuments){
		Map<MapKeyEnum, Object> otherArgs = new HashMap<MapKeyEnum, Object>();
		otherArgs.put(MapKeyEnum.enableDownloadAllDocuments, Boolean.valueOf(enableDownloadAllDocuments));
		return retrieveSupplierLOIDtlPriv(sourceId, loiId, previousPageUrl, isReadonly, otherArgs);
	}
	
	/**
	 * Edits the supplier loi action.
	 * 
	 * @param sourceId 
	 * 		the source id (qt id / rn id
	 * @param loiId 
	 * 		the loi id
	 * @param previousPageUrl
	 * 		the previous page url.
	 * @param isReadonly
	 * 		the readonly flag
	 * @param otherArgs
	 * 		enableDownloadAllDocuments - java.lang.Boolean
	 * 		either user can download all attachments/documents or not
	 * 
	 * @return the supplier loi page
	 */
	private String retrieveSupplierLOIDtlPriv(Long sourceId, Long loiId, String previousPageUrl, boolean isReadonly, Map<MapKeyEnum, Object> otherArgs) {
		ScQt scQt = null;
		
		try { 
			//to avoid otherArgs is null checking next time
			if(otherArgs == null){
				otherArgs = new HashMap<MapKeyEnum, Object>();
			}
			if(otherArgs.containsKey(MapKeyEnum.enableDownloadAllDocuments) && otherArgs.get(MapKeyEnum.enableDownloadAllDocuments) instanceof Boolean){
				Object o = otherArgs.get(MapKeyEnum.enableDownloadAllDocuments);
				if(o instanceof Boolean){	getManagedBean().setEnableDownloadAllDocuments((Boolean)o);	}
			}
			getManagedBean().setSourceId(sourceId);
			if (previousPageUrl != null) {
				getManagedBean().setBackToPreviousPage(previousPageUrl);	
			}
			getManagedBean().setReadOnly(isReadonly);
			

			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO.setLoiId(loiId);
			AwardSupplierResDTO awardSupplierResDTO = getQtAwardSO()
					.retrieveSupplierLOIDtl(awardSupplierReqDTO);
			if (awardSupplierResDTO.getScLoi() != null) {
				ScLoi scLoi = awardSupplierResDTO.getScLoi();
				getManagedBean().setScLoi(scLoi);
				// calc sub total items in acceptance letter.
				calculateOfferedAndFinalPriceByItem(scLoi);

				// contract signer
				if (scLoi.getContractSigner() != null) {
					getManagedBean().setContractSignerUser(
							scLoi.getContractSigner());
					// #30447 start
					if (scLoi.getContractSigner().getSalutationCode() != null) {
						scLoi.getContractSigner().getSalutationCode().getSalutationName();
					}
					// #30447 end
				}
				
				ScQtFinalization finalization  = awardSupplierResDTO.getScQtFinalization();

				if(finalization != null){
					scQt  = finalization.getScQt();
					if(scQt != null) {
						this.getManagedBean().setScQt(scQt);
						if(checkQTCancelRuquested(scQt)) {
							getManagedBean().setQtCancelRequested(true);
							Locale userLocale = new FacesScopes().getUserLocale();

							FacesContext.getCurrentInstance().addMessage(
									null,
									new FacesMessage(FacesMessage.SEVERITY_INFO, null,
											MessageUtils.getMessage(MessageConst.ERROR_760434,
													null,
													MessageUtils.BUNDLE_COMMON_MESSAGES, userLocale)));
						}
					}
				}
				
				//check wheather refinalization bidding or not
				checkReFinalizationBidding(finalization, scQt);
				
				// QT Proposal validity expired
				getQtProposalValidExpiredMessage(scQt);
				
				//generateDeclarationFromVelocity(scLoi, scQt);
				
				// #11134 - start
				// Remarks
				this.getManagedBean().getScLoi().setScLoiRemarks(initializeRemarks(
						scLoi, 
						scLoi.getScWorkFlowStatus(),
						scLoi.getScLoiRemarks(), ScLoiRemark.class));
				// #11134 - end
				
				return QTConstants.SUPPLIER_LOI_PAGE;
			}
		} catch (Exception e) {
			handleException(e);
		}
		
		return null;
	}
	
	// #30447 - upperCase salutation check start
	public String getFormattedContactPerson() {
	    // check object null
	    if (this.getManagedBean() == null 
	        || this.getManagedBean().getScLoi() == null 
	        || this.getManagedBean().getScLoi().getScSupplierAddress() == null) {
	        return "";
	    }
	    
	    String name = StringUtils.trimToEmpty(this.getManagedBean().getScLoi().getScSupplierAddress().getContactPerson());
	    
	    String salutation = "";
	    if (this.getManagedBean().getScLoi().getScSupplierAddress().getPmContactUserID() != null 
	        && this.getManagedBean().getScLoi().getScSupplierAddress().getPmContactUserID().getSalutationCode() != null) {
	        salutation = StringUtils.trimToEmpty(this.getManagedBean().getScLoi().getScSupplierAddress().getPmContactUserID().getSalutationCode().getSalutationName());
	    }

        return getFormattedSalutationName(salutation, name);
	}
	
	// # 30447 - start
	public void setFormattedContactPerson(String value) {
	}
	// # 30447 - end
	
	// # 30447 - start
	public String getFormattedContractSignerUserName() {
		//check null
        if (this.getManagedBean() == null 
            || this.getManagedBean().getContractSignerUser() == null) {
            return "";
        }
        
        String name = StringUtils.trimToEmpty(this.getManagedBean().getContractSignerUser().getUserName());
        
        String salutation = "";
        if (this.getManagedBean().getContractSignerUser().getSalutationCode() != null) {
            salutation = StringUtils.trimToEmpty(this.getManagedBean().getContractSignerUser().getSalutationCode().getSalutationName());
        }
        
        return getFormattedSalutationName(salutation, name);
	}
	// # 30447 - end
	
	// # 30447 - start
	public void setFormattedContractSignerUserName(String value) {
	}
	// # 30447 - end
	
	/**
	 * Pre render page navigation to multiple loi page based on task id
	 * 
	 * @return void
	 */
	public void preRenderMultipleLoiView() {
		debugLogger("preRenderMultipleLoiView");

		FacesContext fc = FacesContext.getCurrentInstance();
		
		try {
			SCEvaluationDataType evaluationDataType = getBpmTaskDetails(SCEvaluationDataType.class, null);

			Long sourceId = Long.valueOf(evaluationDataType.getQtId());
			
			String loggedInUserIdentificationNo = getFacesScopes().getUserSessionBean()
														.getIdentificationNo();
			if (StringUtils.equals(loggedInUserIdentificationNo, 
					evaluationDataType.getFcSecretary())) {
				this.getManagedBean().setSecretaryUser(true);
			}
			//QT WIP weightage flag

			retrieveMultipleLoiDtls(sourceId,
					evaluationDataType.getCommitteeType(),
					evaluationDataType.getGroupkeyMa());
			getManagedBean().setMultipleLoiFlag(Boolean.TRUE);

			// QT Proposal validity expired
			getQtProposalValidExpiredMessage(getManagedBean().getScQt());
			
			// #25638 - Start
			displayReviseInstrn(getManagedBean().getFcActivityList());
			// #25638 - End
			checkSupplierAwardedRejectValidityExtensionPreRender();//#21692
			
			checkWeightage35384WIP(); //#38151			
			populateMeetingPreparationTab();// 34491
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.MULTI_LOI_PAGE);
		} catch (Exception e) {
			handleException(e);
			fc.getApplication().getNavigationHandler()
			  .handleNavigation(fc, null,
					CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);			
		}
	}
	
	/**
	 * Retrieve multiple loi dtls.
	 * 
	 * @param qtId
	 *            the qt id
	 * @param committeeType
	 *            the committee type
	 * @param recommendDtlIds
	 *            the recommend dtl ids
	 */
	private void retrieveMultipleLoiDtls(Long qtId, String committeeType,
										 List<String> recommendDtlIds) {
		try {
			boolean isZonal = false;
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteeType(committeeType);
			reqDTO.setRecommendDtlIdList(recommendDtlIds);

			AwardSupplierResDTO resDTO = getQtAwardSO()
					.retrieveMultipleLoiDtls(reqDTO);
			if (resDTO.isSuccessful()) {
				ScQtFinalization scQtFinalization = resDTO
						.getScQtFinalization();
				List<ScQtRecommend> scQtRecommendList = resDTO
						.getScQtRecommendList();
				isZonal = scQtFinalization.getScQt().isZonal();
				List<ScLoi> scLois = resDTO.getScLois();
				Map<Long,List<ScLoiLoaItem>> loiLoaItemsMap 
							= new HashMap<Long,List<ScLoiLoaItem>>();
				Map<Long,List<ScLoiLoaItem>> supplierLoiLoaItemsMap = null;
				if (CollectionUtils.isNotEmpty(scLois) && 
						CollectionUtils.isNotEmpty(recommendDtlIds)) {
					List<ScLoi> previousLois = null;
					
					for (ScLoi scLoi : scLois) {
						if (CollectionUtils.isNotEmpty(
									scLoi.getScLoiLoaItems())) {
							for (ScLoiLoaItem loiLoaItem : scLoi.getScLoiLoaItems()) {
								Long recommendDtlId = loiLoaItem
										.getScQtRecommendDtl().getRecommendDtlId();
								if (!recommendDtlIds.contains(
										String.valueOf(recommendDtlId))) {
									if (previousLois == null) {
										previousLois = new ArrayList<ScLoi>();
									}
									previousLois.add(scLoi);
									break;
								}
								
								if (isZonal) {
									Long specZoneId = loiLoaItem.getTplSpecZoneDtl().getSpecZoneId();
									if (loiLoaItemsMap.containsKey(specZoneId)) {
										loiLoaItemsMap.get(specZoneId).add(loiLoaItem);
									} else {
										List<ScLoiLoaItem> loiLoaItems = new ArrayList<ScLoiLoaItem>();
										loiLoaItems.add(loiLoaItem);
										loiLoaItemsMap.put(specZoneId, loiLoaItems);
									}
								}
							}
							
							if (!isZonal && ((CollectionUtils.isEmpty(previousLois) || //20230712@asyrafhaziq- SQUBE Bug NullPointer#3
									!previousLois.contains(scLoi)))) {//fixedNullpointerIssueSonarQube
									if (supplierLoiLoaItemsMap == null) {
										supplierLoiLoaItemsMap = new HashMap<>();
									}
									Long supplierId = scLoi.getScLoiLoa().getSmSupplier().getSupplierId();
									supplierLoiLoaItemsMap.put(supplierId, scLoi.getScLoiLoaItems());																
							}
						}
					}
					if (CollectionUtils.isNotEmpty(previousLois)) {
						scLois.removeAll(previousLois);
					}
				}
				
				if (CollectionUtils.isNotEmpty(scLois)) {
					 for (ScLoi scLoi : scLois) {
						 calculateOfferedAndFinalPriceByItem(scLoi);
					 }
				}

				getManagedBean().setScQtFinalization(scQtFinalization);
				getManagedBean().setScQt(scQtFinalization.getScQt());
				getManagedBean().setCommitteeType(committeeType);
				getManagedBean().setLois(scLois);
				getManagedBean().setLoiLoaItemsMap(loiLoaItemsMap);
				getManagedBean().setSupplierLoiLoaItemsMap(supplierLoiLoaItemsMap);
				getManagedBean().setRecommendDtlIds(recommendDtlIds);
				getManagedBean().getScQt().setScQtScoreSettings(this.getManagedBean().getScQt().getScQtScoreSettings()); //#38151
				
				String recommendType = scQtFinalization.getRecommendedTypeId()
													   .getParameterCode();
				if (isZonal) {
					this.getManagedBean().setMultiLOIZoneDetails(
							new ArrayList<TplSpecZoneDtl>());
					this.getManagedBean().setScQtRecommendsByZone(
							new LinkedHashMap<Long, List<ScQtRecommend>>());
					
					TplSpecZoneDtl specZoneDtl = null;
					List<TplSpecZoneDtl> zoneDetails = new ArrayList<TplSpecZoneDtl>();
					Map<Long, List<ScQtRecommend>> scQtRecommendsByZone 
									= new LinkedHashMap<Long, List<ScQtRecommend>>();					
					Map<Long,TplSpecZoneDtl> zoneMap = new HashMap<Long,TplSpecZoneDtl>();
					getManagedBean().setZoneMap(zoneMap);
					
					for (ScQtRecommend scQtRecommend : scQtRecommendList) {
						specZoneDtl = scQtRecommend.getTplSpecZoneDtl();
						
						if (!zoneDetails.contains(specZoneDtl)) {
							zoneDetails.add(specZoneDtl);
							zoneMap.put(specZoneDtl.getSpecZoneId(), specZoneDtl);
						}
						if (scQtRecommendsByZone.containsKey(
										specZoneDtl.getSpecZoneId())) {
							List<ScQtRecommend> scQtRecommends 
									= scQtRecommendsByZone.get(specZoneDtl.getSpecZoneId());
							scQtRecommends.add(scQtRecommend);
						} else {
							List<ScQtRecommend> scQtRecommends 
									= new ArrayList<ScQtRecommend>();
							scQtRecommends.add(scQtRecommend);
							scQtRecommendsByZone.put(
									specZoneDtl.getSpecZoneId(), scQtRecommends);
						}						
					}
					if (CollectionUtils.isNotEmpty(zoneDetails)) {
						this.getManagedBean().setMultiLOIZoneDetails(zoneDetails);
						generateZoneMenuModel();// 33380 - change position 
						this.getManagedBean().setSelectedZone(zoneDetails.get(0));
						this.getManagedBean().setZoneActiveIndex(
									zoneDetails.get(0).getZoneTabIndex());
						this.getManagedBean().setScQtRecommendsByZone(scQtRecommendsByZone);
						processMultiLOIDetails(recommendType, 
								scQtRecommendsByZone.get(zoneDetails.get(0).getSpecZoneId()));					
					}					
				} else {
					processMultiLOIDetails(recommendType, scQtRecommendList);
				}

				// Committee Details
				ScQtCommittee scQtCommittee = resDTO.getScQtCommittee();
				getManagedBean().setEcActivityList(resDTO.getEcActivityList());
				getManagedBean().setTecActivityList(
						resDTO.getTecActivityList());
				getManagedBean().setFecActivityList(
						resDTO.getFecActivityList());
				getManagedBean().setScQtCommittee(scQtCommittee);
				if (scQtCommittee != null) {
					List<ScQtCommitteeActivity> fcActivityList
								= new ArrayList<ScQtCommitteeActivity>();
					for (ScQtCommitteeActivity commActivity 
									: scQtCommittee.getCommitteeActivities()) {
						if (commActivity.isAttended()) {
							fcActivityList.add(commActivity);
						}
					}
					getManagedBean().setFcActivityList(fcActivityList);
					
					scQtCommittee.setQtCommitteRemarks(initializeRemarks(
							scQtCommittee, scQtFinalization.getCurrentStatus(),
							scQtCommittee.getQtCommitteRemarks(),
							ScQtCommitteRemarks.class));
				}
			
				// LOI Reports
				getManagedBean().setRecommendedLoi(resDTO.getScLoi());
				getManagedBean().setScLoi(resDTO.getScLoi());

				// Display the negotiation results of the first supplier
				// in the order of display
				onSupplierSelectEvent();

				// status
				getCurrentStatus(getManagedBean().getCurrentStatusId());
				
				// Breifing paper
				retrieveBriefingPaper();
				Long statusId  = getManagedBean().getCurrentStatusId();//34905-start
				if (QTStatusConst.SF_PENDING_MULTIPLE_LOI_RECOMMENDATION
						.equals(statusId) && ParameterConst.RECOMMEND_TYPE_PACKAGE.equals(recommendType) && !getManagedBean().isZonal()) {
					List<Long> qualifiedSuppliers=new ArrayList<>();
					Long awardedSupplierId=null;
					ScQtRecommend scQtRecommend=scQtRecommendList.get(0);
					for (ScQtRecommendDtl recommendDtl 
							: scQtRecommend.getRecommendDtls()) {
						awardedSupplierId = recommendDtl.getSupplier().getSupplierId();
						if (recommendDtl.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE && !qualifiedSuppliers.contains(awardedSupplierId)) {
							qualifiedSuppliers.add(awardedSupplierId);
							if(qualifiedSuppliers.size()>=2){
								getManagedBean().setSelectedSupplier(null);
								break;
							}
							
						}
					}
					
				}//34905-End
				
				//#30008 - Start
				if (this.getManagedBean().isItemized()) {
					List<ScQtRecommend> scRecomendedList = this.getManagedBean().getRecommendList();
					for (ScLoi scLoi : scLois) {
						if (scLoi.getNegoDecision() != null && ParameterConst.NOT_REACH_AGREEMENT.
								equalsIgnoreCase(String.valueOf(scLoi.getNegoDecision().getParameterId()))) {
							getManagedBean().setDecision(new PmParameter());
							getManagedBean().setDecision(scLoi.getNegoDecision());
							getManagedBean().setJustification(scLoi.getDecisionJustification());
							for (ScQtRecommend recom : scRecomendedList) {
								List<ScQtEvaluationResultDtl> recomSuppList = recom.getAwardSupplierList();
								for (ScQtEvaluationResultDtl evalDtl : recomSuppList) {
									if (scLoi.getScLoiLoa().getSmSupplier().getSupplierId().
											equals(evalDtl.getQtEvaluationResult().getScQtProposal().getScQtSupplier()
													.getSmSupplier().getSupplierId())) {
										recom.setNoReachAgreement(true);
										break;
									}
								}
							}
						}
					}
				} else {		
					List<ScQtRecommend> scRecomendedList = this.getManagedBean().getRecommendList();
					for (ScLoi scLoi : scLois) {
						if (scLoi.getNegoDecision() != null	&& ParameterConst.NOT_REACH_AGREEMENT.
								equalsIgnoreCase(String.valueOf(scLoi.getNegoDecision().getParameterId()))) {
							getManagedBean().setDecision(scLoi.getNegoDecision());
							getManagedBean().setJustification(scLoi.getDecisionJustification());
							for (ScQtRecommend recom : scRecomendedList) {
								List<ScQtEvaluationResultDtl> recomSuppList = recom.getAwardSupplierList();
								for (ScQtEvaluationResultDtl evalDtl : recomSuppList) {
									if (scLoi.getScLoiLoa().getSmSupplier().getSupplierId().
											equals(evalDtl.getQtEvaluationResult().getScQtProposal().getScQtSupplier()
													.getSmSupplier().getSupplierId())) {
										recom.setNoReachAgreement(true);
										break;

									}
								}
							}
						}
					}
				}
				//#30008 - End
			}		

		} finally {
			// qtAwardSO = null;
		}
	}
	
	private void processMultiLOIDetails(String recommendType, 
										List<ScQtRecommend> scQtRecommendList) {
		awardedSupplierRejectEPV(scQtRecommendList);//#21692
		getManagedBean().setRecommendList(scQtRecommendList);
		
			if (ParameterConst.RECOMMEND_TYPE_ITEM.equals(recommendType)) {
				getManagedBean().setItemized(true);
				for (ScQtRecommend scQtRecommend : scQtRecommendList) {				
						
						setRecommendedSupplierList(scQtRecommend);
						updateRecommendBy(scQtRecommend);
				}
				
			disqualifiedSupplierListbyItem(scQtRecommendList); // #28777&#29729
				
			} else {
				getManagedBean().setItemized(false);
				processRecommendedSuppliersByPKG(scQtRecommendList);
				ScQtRecommend scQtRecommend = scQtRecommendList.get(0);
				setDisciplinaryAction(scQtRecommend.getAwardSupplierList());// 26247 - 31061
				ScQtEvaluationResultDtl awardedSupplier = getRecommendedEvaluationResultDtlByPKG(scQtRecommend);
				getManagedBean().setRecommendedSupplierList(scQtRecommend.getAwardSupplierList());	
				getManagedBean().setSelectedSupplier(awardedSupplier);
				
			disqualifiedSupplierListbyPackage(scQtRecommendList);// #28777&#29729
				
			}			
		
		
	}
	
	// #28777 & #29729 - Start
	/**
	 * Method to check had disqualified supplier list by items
	 * 
	 * @param scQtRecommendList
	 **/
	private void disqualifiedSupplierListbyItem(List<ScQtRecommend> scQtRecommendList) {
		ScQt scQt = this.getManagedBean().getScQt();
		List<ScQtRecommend> disqualifiedRecomendedList = new ArrayList<>();
		if (!scQt.isZonal() && checkListDisqualifiedSupplierBranchDeleted(scQt.getQtId())) {
			for (ScQtRecommend scQtRecommend : scQtRecommendList) {
				setRecommendedSupplierList(scQtRecommend);
				updateRecommendBy(scQtRecommend);
				setDisciplinaryAction(scQtRecommend.getNotAwardSupplierList());
				disqualifiedRecomendedList.add(scQtRecommend);
			}
			this.getManagedBean().setDisqualifiedRecommendList(disqualifiedRecomendedList);
		}
		for (ScQtRecommend scQtRecommend : scQtRecommendList) {
			TplSpecZoneDtl tplSpecZoneDtl = scQtRecommend.getTplSpecZoneDtl();
			if (scQt.isZonal() && tplSpecZoneDtl != null
					&& checkListZonesDisqualifiedSupplierBranchDeleted(scQt.getQtId(), tplSpecZoneDtl.getSpecZoneId())) {
				setRecommendedSupplierList(scQtRecommend);
				updateRecommendBy(scQtRecommend);
				setDisciplinaryAction(scQtRecommend.getNotAwardSupplierList());
				disqualifiedRecomendedList.add(scQtRecommend);
				this.getManagedBean().setDisqualifiedRecommendList(disqualifiedRecomendedList);
			}
		}
	}

	// #28777 & #29729 - End
	
	// #28777 & #29729 - Start
	/**
	 * Method to check had disqualified supplier list by packages
	 * 
	 * @param scQtRecommendList
	 **/
	private void disqualifiedSupplierListbyPackage(List<ScQtRecommend> scQtRecommendList) {
		ScQt scQt = this.getManagedBean().getScQt();
		if (!scQt.isZonal() && checkListDisqualifiedSupplierBranchDeleted(scQt.getQtId())) {

			processRecommendedSuppliersByPKG(scQtRecommendList);
			ScQtRecommend scQtRecommend = scQtRecommendList.get(0);
			setDisciplinaryAction(scQtRecommend.getNotAwardSupplierList());
			this.getManagedBean().setDisqualifiedSupplierList(scQtRecommend.getNotAwardSupplierList());

		}
		for (ScQtRecommend scQtRecommend : scQtRecommendList) {
			TplSpecZoneDtl tplSpecZoneDtl = scQtRecommend.getTplSpecZoneDtl();
			if (scQt.isZonal() && tplSpecZoneDtl != null
					&& checkListZonesDisqualifiedSupplierBranchDeleted(scQt.getQtId(), tplSpecZoneDtl.getSpecZoneId())) {

				processRecommendedSuppliersByPKG(scQtRecommendList);
				scQtRecommend = scQtRecommendList.get(0);
				setDisciplinaryAction(scQtRecommend.getNotAwardSupplierList());
				this.getManagedBean().setDisqualifiedSupplierList(scQtRecommend.getNotAwardSupplierList());

			}
		}

	}

	// #28777 & #29729 - End
	
	// 26247 - 31061 - start
	private void setDisciplinaryAction(List<ScQtEvaluationResultDtl> evalResDtl) {
		for (ScQtEvaluationResultDtl qtEvalResDtl : evalResDtl) {
			ScQtProposal qtProposal = qtEvalResDtl.getQtEvaluationResult().getScQtProposal();
			if (null != qtProposal && null != qtProposal.getScQtSupplier()
					&& null != qtProposal.getScQtSupplier().getScQt()) {
				Long qtId = qtProposal.getScQtSupplier().getScQt().getQtId();
				qtProposal.getScQtSupplier().setSmSupplier(
						this.getQTDetailSO().evaluateDisciplinaryActions(qtProposal.getScQtSupplier().getSmSupplier(),
								qtId));
			}
		}
	}	 
	// 26247 - 31061 - end
	
	private void processRecommendedSuppliersByPKG(List<ScQtRecommend> scQtRecommendList) {
		// 26247 - start
		for (ScQtRecommend scQtRecommend : scQtRecommendList) {
			setRecommendedSupplierList(scQtRecommend);
		}
		// 26247 - end
		ScQtRecommend scQtRecommend = scQtRecommendList.get(0);
		
		List<ScQtEvaluationResultDtl> recommendedSuppplierList 
								= scQtRecommend.getAwardSupplierList();
		if (CollectionUtils.isNotEmpty(recommendedSuppplierList)) {			
			for (ScQtEvaluationResultDtl recommendedSupplier 
											: recommendedSuppplierList) {
				Long recommendedSupplierId = recommendedSupplier.getQtEvaluationResult()
						.getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierId();
				BigDecimal totalOfferedPrice = new BigDecimal(0);

				List<ScLoiLoaItem> awardedSupplierLoiLoaItems = new ArrayList<ScLoiLoaItem>();				
				Map<Long,List<ScLoiLoaItem>> loiLoaItemsMap 
							= getManagedBean().getLoiLoaItemsMap();
				
				if (getManagedBean().isZonal()) {
					List<ScLoiLoaItem> loiLoaItems = loiLoaItemsMap.get(
							scQtRecommend.getTplSpecZoneDtl().getSpecZoneId());
					for (ScLoiLoaItem scLoiLoaItem : loiLoaItems) {
						Long itemSupplierId = scLoiLoaItem.getScQtRecommendDtl()
													.getSupplier().getSupplierId();
						if (recommendedSupplierId.equals(itemSupplierId)) {
							awardedSupplierLoiLoaItems.add(scLoiLoaItem);
						}
					}					
				} else {
					
					// Get loiLoaItems corresponding to the supplier
					awardedSupplierLoiLoaItems = getManagedBean().getSupplierLoiLoaItemsMap()
													.get(recommendedSupplierId);
				}
				totalOfferedPrice = calculateAfterNegoSubTotal(
							awardedSupplierLoiLoaItems, scQtRecommendList);
				totalOfferedPrice = totalOfferedPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
				recommendedSupplier.getQtEvaluationResult()
								   .setOfferedPrice(totalOfferedPrice);
			}
		}
		
		//#28777 & #29729 - Start
		List<ScQtEvaluationResultDtl> disqualifiedSuppplierList = scQtRecommend.getNotAwardSupplierList();
		if (CollectionUtils.isNotEmpty(disqualifiedSuppplierList)) {			
			for (ScQtEvaluationResultDtl disqualifiedSupplier 
											: disqualifiedSuppplierList) {
				BigDecimal offeredPrice = new BigDecimal(0);
				offeredPrice = disqualifiedSupplier.getProposalAmount().getProposalAmt();
				offeredPrice = offeredPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
				disqualifiedSupplier.getQtEvaluationResult().setOfferedPrice(offeredPrice);
			}
		}
		//#28777 & #29729 - End
		
	}
	
	private ScQtEvaluationResultDtl getRecommendedEvaluationResultDtlByPKG(ScQtRecommend scQtRecommend) {
		ScQtEvaluationResultDtl awardedSupplier = null;
		List<ScQtEvaluationResultDtl> recommendedSuppplierList = scQtRecommend.getAwardSupplierList();
		// 26247, 31061 - start
		for (ScQtRecommendDtl recommendDtl : scQtRecommend.getRecommendDtls()) {
			if (recommendDtl.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				Long awardedSupplierId = recommendDtl.getSupplier().getSupplierId();
				Map<Object, Object> evaluationResultDtlMap = CustomUtils.listToMap(recommendedSuppplierList,
						"getQtEvaluationResult.getScQtProposal.getScQtSupplier.getSmSupplier.getSupplierId");
				ScQtEvaluationResultDtl evaluationResultDtl = (ScQtEvaluationResultDtl) evaluationResultDtlMap
						.get(awardedSupplierId);
				if (evaluationResultDtl == null) {
					continue;
				}
				SmSupplier supplier = evaluationResultDtl.getQtEvaluationResult().getScQtProposal().getScQtSupplier()
						.getSmSupplier();

				if (!this.disabledQTProcess(supplier)) {
					awardedSupplier = evaluationResultDtl;
				} else {
					recommendDtl.setRecordStatus(false);
					recommendDtl.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
				}
				return awardedSupplier;
			}
		}
		// 26247, 31061 - end
		return awardedSupplier;
	}
	
	private void generateZoneMenuModel() {
		if (CollectionUtils.isNotEmpty(
					this.getManagedBean().getMultiLOIZoneDetails())) {
			List<TplSpecZoneDtl> zoneDtls 
						= this.getManagedBean().getMultiLOIZoneDetails();
			
			// sorting by spec zone id for tab zone order view
			// #32479 - Start
			Collections.sort(zoneDtls, new Comparator<TplSpecZoneDtl>() {
				public int compare(TplSpecZoneDtl o1, TplSpecZoneDtl o2) {
							return Integer.valueOf((int) o1.getSpecZoneId().longValue()).compareTo(
								   Integer.valueOf((int) o2.getSpecZoneId().longValue()));
				}
			}); 
		    // #32479 - End

			// Dynamically generating the zone tabs
			MenuModel zoneMenuModel = new DefaultMenuModel();
			if (CollectionUtils.isNotEmpty(zoneDtls)) {
				int zoneTabIndex = 0;
				for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
					specZoneDtl.setZoneTabIndex(zoneTabIndex);

					MenuItem zoneItem = new MenuItem();
					zoneItem.setValue(specZoneDtl.getZoneName().toUpperCase());
//					zoneItem.setAjax(true);
					zoneItem.setTransient(true);
					zoneItem.setPartialSubmit(true);
					zoneItem.setProcess("@this");
					zoneItem.setUpdate("@this");
					zoneItem.setOncomplete("onZoneTabChangeAction([ {name : 'selectedZoneTabIndex', value : "
							+ zoneTabIndex + "}]);");
					zoneMenuModel.addMenuItem(zoneItem);
					++zoneTabIndex;
				}
			}
			this.getManagedBean().setZoneMenuModel(zoneMenuModel);
		}
	}	
	
	/**
	 * To retrieve the bidding details on selection of a zone tab
	 */
	public void onZoneTabChangeAction() {
		Integer selectedZoneTabIndex = Integer.valueOf(FacesContext
				.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("selectedZoneTabIndex"));
		if (selectedZoneTabIndex == 
				this.getManagedBean().getZoneActiveIndex()) {
			return;
		} 
		
		ScQtFinalization scQtFinalization = this.getManagedBean().getScQtFinalization();
		List<TplSpecZoneDtl> zoneDetails = this.getManagedBean().getMultiLOIZoneDetails();	
		
		TplSpecZoneDtl selectedZone = zoneDetails.get(selectedZoneTabIndex);
		this.getManagedBean().setSelectedZone(selectedZone);
		this.getManagedBean().setZoneActiveIndex(selectedZoneTabIndex);
		processMultiLOIDetails(scQtFinalization.getRecommendedTypeId().getParameterCode(), 
				this.getManagedBean().getScQtRecommendsByZone().get(selectedZone.getSpecZoneId()));
	}	

	/**
	 * Retrieve qt details. get scQt details from the database.
	 * 
	 * @param qtId
	 *            the qt id
	 */
	public void retrieveQTDetails(Long qtId) {
		IQTDetailSO qtDetailSO;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

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

	/**
	 * Retrieve loi list. get loi list from database to show in loi list page
	 * 
	 * @param qtId
	 *            the qt id
	 */
	public void retrieveLOIList(Long qtId) {
		debugLogger("retrieveLOIList");
		try {
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(qtId);
			
			// ***19336#62
			if(isQTMaintenanceScreen()){
				reqDTO.setBySupplierIds(false);
			}
			
			reqDTO.setRecommendDtlIdList(getManagedBean().getRecommendDtlIds());
			
			AwardSupplierResDTO resDTO = getQtAwardSO().retrievePendingLoiList(
					reqDTO);

			getManagedBean().setLoiLoas(resDTO.getLoiLoas());
			getManagedBean().setScQt(resDTO.getScQt());
			getManagedBean().setScQtFinalization(resDTO.getScQtFinalization());

			// 27426 - #29772 - start
			if (CollectionUtils.isNotEmpty(resDTO.getLoiLoas())) {
				for (ScLoiLoa scLoiLoa : resDTO.getLoiLoas()) {
					SmSupplier supplier = scLoiLoa.getSmSupplier();
					supplier = this.getQtSuppFinalizationSO().evaluateDisciplinaryActions(supplier, qtId);
					scLoiLoa.setSmSupplier(supplier);
				} 
				getManagedBean().setRecSupplierList(resDTO.getLoiLoas()); 
				checkIsLoiProcessRequired(resDTO.getLoiLoas()); 
			}  
			// 27426 - #29772 - end
			// status
			getCurrentStatus(getManagedBean().getCurrentStatusId());

			// loiLoaList Title
			getLoiLoaListTitle(null);

		} finally {
		}
	}

	/**
	 * Prepare loi loa list.
	 * 
	 * @param loiloas
	 *            the loiloas
	 * @return the list
	 */
	private List<LoiLoaBean> prepareLoiLoaList(List<ScLoi> loiList, List<ScLoa> loaList) {
		List<LoiLoaBean> loiloaBeans = new ArrayList<LoiLoaBean>();

		for (ScLoa scLoa : loaList) {
			LoiLoaBean loiLoaBean = new LoiLoaBean();
			loiLoaBean.setScLoa(scLoa);
			loiLoaBean
			.setLoiLoaType(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
			if(null!=scLoa.getPmStatus()){
				loiLoaBean.setLoiLoaStatus(getStatusDescById(scLoa
					.getPmStatus().getStatusId()));
				loiLoaBean.setPmStatus(scLoa.getPmStatus());
			}
			else{
				loiLoaBean.setPmStatus(getPmStatusByStatusId(new Long(QTConstants.PM_STATUS_INACTIVE_CODE)));
			}
			
			loiloaBeans.add(loiLoaBean);
		}

		for (ScLoi scLoi : loiList) {
			LoiLoaBean loiLoaBean = new LoiLoaBean();
			loiLoaBean.setScLoi(scLoi);
			loiLoaBean
			.setLoiLoaType(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
			if(null!=scLoi.getScWorkFlowStatus() && null!=scLoi.getScWorkFlowStatus().getPmStatus()){
				loiLoaBean.setLoiLoaStatus(getStatusDescById(scLoi
					.getScWorkFlowStatus().getPmStatus()
					.getStatusId()));
				loiLoaBean.setPmStatus(scLoi.getScWorkFlowStatus()
						.getPmStatus());
			}
			else{
				loiLoaBean.setPmStatus(getPmStatusByStatusId(new Long(QTConstants.PM_STATUS_INACTIVE_CODE)));
			}
			
			loiloaBeans.add(loiLoaBean);
		}
		return loiloaBeans;
	}

	/**
	 * On row rec supplier action. show item details and nego terms by selecting
	 * supplier in award details.
	 * 
	 * @param event
	 *            the event
	 */
	public void onRowRecSupplierAction(SelectEvent event) {
		
		debugLogger("onRowRecSupplierAction");
		if(event != null){			
			debugLogger(" onRowRecSupplierAction : " + event.toString() + "    class : " + event.getClass());
		}
		//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
		ScLoiLoa scLoiLoa = null;
		if(event != null)			
			scLoiLoa = (ScLoiLoa) event.getObject();
		// #32757- Start.
		if (null != scLoiLoa) {
			try {
				AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
				getLoiLoaListTitle(scLoiLoa);
				List<Long> loiLoaList = new ArrayList<Long>();
				loiLoaList.add(scLoiLoa.getLoiLoaId());
				reqDTO.setLoiLoaIds(loiLoaList);
				AwardSupplierResDTO resDTO = getQtAwardSO().getLoiAndLoaByLoiLoaList(reqDTO);
				if(!CollectionUtils.isEmpty(resDTO.getScLoas()) ||
						!CollectionUtils.isEmpty(resDTO.getScLois())) {
					getManagedBean().setLoiLoaBeans(
							prepareLoiLoaList(resDTO.getScLois(), resDTO.getScLoas()));
				}
				else {
					getManagedBean().setLoiLoaBeans(new ArrayList<LoiLoaBean>());
				}
			} finally {
			}
		}
		// #32757- End.
	}

	/**
	 * Tab menu listener. Tab navigation from one tab to another.
	 * 
	 * @param selectedTabIndex
	 *            the selected tab index.
	 */
	public void tabMenuListener(Integer selectedTabIndex) {
		debugLogger("tabMenuListener");
		
		if (selectedTabIndex.equals(0)) {
			getManagedBean().setSelectedTabIndex(0);
		} else if (selectedTabIndex.equals(1)) {
			getManagedBean().setSelectedTabIndex(1);
		} else if (selectedTabIndex.equals(2)) {
			getManagedBean().setSelectedTabIndex(2);
		} else if (selectedTabIndex.equals(3)) {
			getManagedBean().setSelectedTabIndex(3);
		// 34491 - start
		} else if (selectedTabIndex.equals(4)) {
			getManagedBean().setSelectedTabIndex(4);
		}
		// 34491 - end
	}

	/**
	 * viewItemDetails method to view item details in loi listing.
	 * 
	 * @param bean
	 *            the bean
	 */
	public void viewItemDetail(LoiLoaBean bean) {
		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);

			String language = new FacesScopes().getUserLocale().getLanguage();

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			AwardSupplierResDTO resDTO = null;
			reqDTO.setLanguageCode(language);

			if (bean.getScLoa() != null) {
				reqDTO.setScLoa(bean.getScLoa());
				resDTO = qtAwardSO.retrieveLOADtl(reqDTO);
			} else if (bean.getScLoi() != null) {
				reqDTO.setScLoi(bean.getScLoi());
				resDTO = qtAwardSO.retrieveLOIDtl(reqDTO);
			}

			if (resDTO != null) {
				if (resDTO.getScLoa() != null) {
					ScLoa loa = resDTO.getScLoa();
					bean.setScLoa(loa);
					bean.setScLoiLoaItems(loa.getScLoiLoaItems());
					bean.setSupplierName(loa.getScLoiLoa().getSupplierName());
				} else if (resDTO.getScLoi() != null) {
					ScLoi loi = resDTO.getScLoi();
					bean.setScLoi(loi);
					bean.setScLoiLoaItems(loi.getScLoiLoaItems());
					bean.setSupplierName(loi.getScLoiLoa().getSupplierName());
				}
			}

			getManagedBean().setSelectedLoiLoaBean(bean);

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.toString());
		} finally {
			qtAwardSO = null;
		}
	}

	/**
	 * onSupplierSelectEvent method to view item details and nego terms in
	 * multiple LOI.
	 */
	public void onSupplierSelectEvent() {
		ScLoi scLoi = getManagedBean().getScLoi();
		if (!QTStatusConst.LOI_REJECTED.equals(
				scLoi.getScWorkFlowStatus().getPmStatus().getStatusId())) {
			boolean isTermPriceFound = Boolean.FALSE;
			getManagedBean().setItemDetailsFlag(Boolean.TRUE);
	
			isTermPriceFound = checkNegoTermPrice(scLoi);
			
			if (isTermPriceFound) {
				getManagedBean().setHideColumnFlag(Boolean.TRUE);
			}  else {
				getManagedBean().setHideColumnFlag(Boolean.FALSE);
			}
			//calculateOfferedAndFinalPriceByItem(scLoi);
			
		} else {
			scLoi.setScNegoTerms(null);
			scLoi.setScLoiSupportingDocs(null);
			getManagedBean().setItemDetailsFlag(Boolean.FALSE);
			this.getManagedBean().setProgressivePaymentList(null);
		}
		
		//#30008 - Start
		if (scLoi.getNegoDecision() != null) {
			getManagedBean().setDecision(new PmParameter());
			getManagedBean().setDecision(scLoi.getNegoDecision());
			getManagedBean().setJustification(scLoi.getDecisionJustification());
			getManagedBean().setNegoDecisitionReadOnly(true);
		}
		//#30008 - End
		
		getManagedBean().setSelectedScQtDeduction(null);
	}
	
	/**
	 * Calculate offered and final price by item.
	 *
	 * @param scLoi the sc loi
	 */
	private void calculateOfferedAndFinalPriceByItem(ScLoi scLoi) {
		getManagedBean().setScLoi(scLoi);
		List<ScLoiLoaItem> filteredMaxPriceItems  = getManagedBean().getScLoiLoaItems();
		filteredMaxPriceItems  = filterZoneTierOrTierPriceItems(scLoi);
		getManagedBean().setScLoiLoaItems(filteredMaxPriceItems);

		// calculate max price items only.
		boolean isCalculateMaxPriceItem = Boolean.FALSE;
		boolean isZonal = Boolean.FALSE;
		if (scLoi.getScLoiLoaItems() != null
				&& scLoi.getScLoiLoaItems().size() > 0) {
			BigDecimal offerPrice = BigDecimal.valueOf(0d);
			BigDecimal finalPrice = BigDecimal.valueOf(0d);
			BigDecimal offeredPriceSubTotal = BigDecimal.valueOf(0d);
			BigDecimal offeredPriceAmount = BigDecimal.valueOf(0d);

			BigDecimal afterNegoSubTotal = BigDecimal.valueOf(0d);
			BigDecimal afterNegoTotalAmount = BigDecimal.valueOf(0d);
			BigDecimal quantity = BigDecimal.valueOf(0d);

			List<ScLoiLoaItem> scLoiLoaItems = scLoi.getScLoiLoaItems();
			for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItems) {
				
				if(scLoiLoaItem.getTplSpecZoneDtl() != null){
					isZonal = Boolean.TRUE;
				}
				
				quantity = scLoiLoaItem.getQty();
				if(null == scLoiLoaItem.getTplSpecTierQty() ){
					isCalculateMaxPriceItem = Boolean.TRUE;
				} else{
					if(filteredMaxPriceItems.contains(scLoiLoaItem)){
						isCalculateMaxPriceItem = Boolean.TRUE;
					} else{
						isCalculateMaxPriceItem = Boolean.FALSE;
					}
				}

				if(isCalculateMaxPriceItem ){
					if (scLoiLoaItem.getScQtProposalItem() != null) {
						offerPrice = scLoiLoaItem.getScQtProposalItem().getOfferPrice();
						offerPrice = offerPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
						scLoiLoaItem.getScQtProposalItem().setOfferPrice(offerPrice);

						offeredPriceSubTotal = TemplateSpecHelper.getInstance().calculateTotalAmount(
								scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), offerPrice, quantity);

						finalPrice = scLoiLoaItem.getScQtProposalItem().getFinalPrice();
						finalPrice = finalPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
						scLoiLoaItem.getScQtProposalItem().setFinalPrice(finalPrice);	

						// offered price sub total and total
						offeredPriceAmount = offeredPriceAmount.add(offeredPriceSubTotal);
						offeredPriceSubTotal = offeredPriceSubTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN);
						scLoiLoaItem.setOfferedPriceSubTotal(offeredPriceSubTotal);
						offeredPriceAmount = offeredPriceAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
						
						afterNegoSubTotal = TemplateSpecHelper.getInstance().calculateTotalAmount(
								scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), finalPrice, quantity);
						afterNegoSubTotal = afterNegoSubTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN);

						// after nego price sub total and total
						afterNegoTotalAmount = afterNegoTotalAmount.add(afterNegoSubTotal);
						afterNegoTotalAmount = afterNegoTotalAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
						scLoiLoaItem.setAfterNegoSubTotal(afterNegoSubTotal);
						scLoiLoaItem.setBidFinalPriceSubTotal(afterNegoSubTotal);
					}
				}
			}

			scLoi.setScLoiLoaItems(scLoiLoaItems);
			scLoi.setOfferedPriceTotal(offeredPriceAmount); // offered price total
			scLoi.setLoiAmt(afterNegoTotalAmount); // unit price total
			scLoi.setAfterNegoTotal(afterNegoTotalAmount); // after nego total
			scLoi.setBidFinalPriceTotal(afterNegoTotalAmount); 
			getManagedBean().setZonal(isZonal);
		}		
	}
	
	/**
	 * Filter zone tier or tier price items.
	 *
	 * @param loi the loi
	 * @return the list
	 */
	private List<ScLoiLoaItem>  filterZoneTierOrTierPriceItems(ScLoi loi) {
		List<ScLoiLoaItem> scLoiLoaItems = loi.getScLoiLoaItems();
		List<ScLoiLoaItem> groupItems = new ArrayList<ScLoiLoaItem>();
		List<ScLoiLoaItem> filteredMaxPriceItems = new ArrayList<ScLoiLoaItem>();
		
		if(loi.getScLoiLoaItems() != null && loi.getScLoiLoaItems().size()>0){
			ScLoiLoaItem scLoiLoaItem  = null;
			int counter = 0;

			for (ScLoiLoaItem loiLoaItem : scLoiLoaItems) {

				if(scLoiLoaItem == null){
					scLoiLoaItem = loiLoaItem;
					groupItems.add(loiLoaItem);
				} else{

					if(loiLoaItem.getTplSpecZoneDtl() != null){
						if(loiLoaItem.getScRequestItem().equals(scLoiLoaItem.getScRequestItem()) && 
								loiLoaItem.getTplSpecZoneDtl().equals(scLoiLoaItem.getTplSpecZoneDtl())){
							groupItems.add(loiLoaItem);

						} else{
							scLoiLoaItem =  loiLoaItem;
							ScLoiLoaItem maxPriceItem  = findMaxPriceItem(groupItems);
							filteredMaxPriceItems.add(maxPriceItem);
							groupItems.clear();
							groupItems.add(loiLoaItem);
						}
					} else{
						/*
						 * Enhancement #21694
						 */
						if(null!=loiLoaItem.getTplSpecTierQty()){
							if(loiLoaItem.getScRequestItem().equals(scLoiLoaItem.getScRequestItem())){
								groupItems.add(loiLoaItem);

							} else{
								scLoiLoaItem =  loiLoaItem;
								ScLoiLoaItem maxPriceItem  = findMaxPriceItem(groupItems);
								filteredMaxPriceItems.add(maxPriceItem);
								groupItems.clear();
								groupItems.add(loiLoaItem);
							}
						}
						
					}
				}
				++counter;

				if(scLoiLoaItems.size() == counter){
					/*
					 * Enhancement #21694
					 */
					if(null!=groupItems && groupItems.size()>0){
						ScLoiLoaItem maxPriceItem  = findMaxPriceItem(groupItems);
						filteredMaxPriceItems.add(maxPriceItem);
						groupItems.clear();
					}
				}
			}
		}
		return filteredMaxPriceItems;
	}
	
	

	/**
	 * Validate item tier price by item.
	 *
	 * @param scLoiLoaItem the sc loi loa item
	 * @param filteredMaxPriceItems the filtered max price items
	 */
	private void validateItemTierPriceByItem(ScLoiLoaItem scLoiLoaItem, List<ScLoiLoaItem> filteredMaxPriceItems) {
		debugLogger("validateItemTierPriceByItem");
		ScLoiLoaItem currentItem = null;
		ScLoiLoaItem prevItem = null;
		List<ScLoiLoaItem> groupByItems = new ArrayList<ScLoiLoaItem>();
		List<ScLoiLoaItem> scLoiLoaItems = getManagedBean().getScLoi().getScLoiLoaItems();
		for (ScLoiLoaItem loiLoaItem : scLoiLoaItems) {

			// zone tier group items
			if(null !=  loiLoaItem.getTplSpecZoneDtl()){
				if(loiLoaItem.getScRequestItem().getRequestNoteItemId().equals(scLoiLoaItem.getScRequestItem().getRequestNoteItemId()) && 
						loiLoaItem.getTplSpecZoneDtl().getZoneName().equals(scLoiLoaItem.getTplSpecZoneDtl().getZoneName())){
					groupByItems.add(loiLoaItem);
				}
			} else{
				// tier group items
				if(loiLoaItem.getScRequestItem().getRequestNoteItemId().equals(scLoiLoaItem.getScRequestItem().getRequestNoteItemId())){
					groupByItems.add(loiLoaItem);
				}
			}

		}
		findLowestIterPriceItem(groupByItems); 

		//validate tier price items
		for (ScLoiLoaItem item : groupByItems) {
			currentItem = item;

			if(prevItem != null && currentItem != null){
				if(prevItem.getUnitPrice().doubleValue() < currentItem.getUnitPrice().doubleValue()){
					throw new EpException(MessageConst.ERROR_700102, 
							new String[]{
							currentItem.getTplSpecZoneDtl() != null ? currentItem.getTplSpecZoneDtl().getZoneName() : "",
							currentItem.getTplSpecTierQty().getFromQty().toString(), 
							currentItem.getTplSpecTierQty().getToQty().toString(),
							prevItem.getUnitPrice().toString()}); 
				}
			}

			prevItem = currentItem;
		}
	}

	/**
	 * Find max price item.
	 *
	 * @param items the items
	 * @return the sc loi loa item
	 */
	private ScLoiLoaItem findMaxPriceItem(List<ScLoiLoaItem> items) {
		Comparator<ScLoiLoaItem> comparator = new Comparator<ScLoiLoaItem>() {
			@Override
			public int compare(ScLoiLoaItem o1, ScLoiLoaItem o2) {
				return new CompareToBuilder().append(o1.getTplSpecTierQty().getFromQty(),
						o2.getTplSpecTierQty().getFromQty()).toComparison();
			}
		};
		 ScLoiLoaItem  scLoiLoaItem = Collections.min(items, comparator);
		return scLoiLoaItem;
	}
	
	
	/**
	 * Find max price item.
	 *
	 * @param items the items
	 * @return the sc loi loa item
	 */
	private void findLowestIterPriceItem(List<ScLoiLoaItem> items) {
		Comparator<ScLoiLoaItem> comparator = new Comparator<ScLoiLoaItem>() {
			@Override
			public int compare(ScLoiLoaItem o1, ScLoiLoaItem o2) {
				return new CompareToBuilder().append(o1.getTplSpecTierQty().getFromQty(),
						o2.getTplSpecTierQty().getFromQty()).toComparison();
			}
		};
		 Collections.sort(items, comparator);
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

	/**
	 * getting Approver drop down listing in approval details.
	 * 
	 * @return the approver names
	 */
	public List<PmUser> getApproverNames() {
		List<PmUser> approverUsers = this.getManagedBean().getApproverNames();
		// to get Approver
		PmUser user = new FacesScopes().getUserSessionBean();
		if (user != null) {
			/*
			 * IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
			 * ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class); UserReqDTO
			 * reqDTO = new UserReqDTO(); reqDTO.setUserId(user.getUserId());
			 * //UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			 */
			IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IUserSO.class);
			UserReqDTO userReqDto = new UserReqDTO();
			userReqDto.setRoleCode(RoleCode.PUBLICATION_APPROVER.value());

			UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
			getManagedBean().setApproverNames(userResDTO.getUserList());

		}
		return approverUsers;
	}

	/**
	 * To get current status for loi.
	 * 
	 * @param statusId
	 *            the status id
	 * @return the current status
	 */
	public void getCurrentStatus(Long statusId) {
		if (statusId != null) {
			getManagedBean().setStatus(getStatusDescById(statusId));
		}
	}

	/**
	 * Page navigation from loi details page to loi loa list.
	 */
	public void backToPrevious() {
		debugLogger("backToPrevious");
		if(!isQTMaintenanceScreen() || getManagedBean().getSourceId() != null){
			retrieveLOIList(getManagedBean().getSourceId());
		}
		getManagedBean().setEditFlag(false);
		getManagedBean().setButtonFlag(false);
		getManagedBean().setReadOnly(false);// 36293

		FacesContext fc = FacesContext.getCurrentInstance();

		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						getManagedBean().getBackToPreviousPage());
		// #32757- Start.
		getManagedBean().setBackToPreviousPage(null);
		// #32757- End.
	}

	/**
	 * Adds the negotiation location in negotiation details.
	 */
	public void addNegotiationLocation() {
		debugLogger("addNegotiationLocation");
		ScLoiNegoLocation negoLocation = new ScLoiNegoLocation();
		List<ScLoiNegoLocation> negoLocations = getManagedBean().getScLoi()
				.getScNegoLocations();

		if (null == negoLocations) {
			negoLocations = new ArrayList<ScLoiNegoLocation>();
		}
		negoLocation.setScLoi(getManagedBean().getScLoi());
		negoLocations.add(negoLocation);
		getManagedBean().getScLoi().setScNegoLocations(negoLocations);
		this.getManagedBean().setNegoLocationList(negoLocations);
	}

	/**
	 * Adds the negotiation term in negotiation details.
	 */
	public void addNegotiationTerm() {
		debugLogger("addNegotiationTerm");
		ScLoi scLoi = getManagedBean().getScLoi();
		
		Long statusId = scLoi.getScWorkFlowStatus().getPmStatus().getStatusId();//20230711@Asyrafhaziq-SQUBE Bug Minor #16
		
		List<ScLoiTerm> scNegoTerms = scLoi
				.getScNegoTerms();
		if (null == scNegoTerms) {
			scNegoTerms = new ArrayList<ScLoiTerm>();
		}
		ScLoiTerm negoTerm = new ScLoiTerm();
		negoTerm.setPmTerm(null);
		if (QTStatusConst.LOI_PENDING_NEGOTIATION.equals(statusId)) {
			negoTerm.setSelectedTerm(Boolean.FALSE);
			//negoTerm.setSelected(Boolean.FALSE);//#29007 Added Vinod removed 14/02/2023.
		} else{
			negoTerm.setSelectedTerm(Boolean.TRUE);
			negoTerm.setSelected(true);
		}
		negoTerm.setScLoi(scLoi);
		negoTerm.setModified(true);
		scNegoTerms.add(negoTerm);
		scLoi.setScNegoTerms(scNegoTerms);
	}


	/**
	 * Delete nego terms in negotiation details.
	 */
	public void deleteNegoTerm() {
		List<ScLoiTerm> negoTermsList = new ArrayList<ScLoiTerm>();
		boolean flag = true;
		List<ScLoiTerm> scLoiTerms = getManagedBean().getScLoi()
				.getScNegoTerms();
		if (scLoiTerms != null && scLoiTerms.size() > 0) {

			for (ScLoiTerm scLoiTerm : scLoiTerms) {
				if (!scLoiTerm.isSelectTerm()) {
					negoTermsList.add(scLoiTerm);
				} else {
					flag = false;
				}
			}
		}
		if (flag) {
			throw new EpException(MessageConst.ERROR_760158);
		}
		getManagedBean().getScLoi().setScNegoTerms(negoTermsList);
	}

	/**
	 * Delete multiple loi nego term.
	 */
	public void deleteMultipleLoiNegoTerm() {
		ScLoi scLoi = getManagedBean().getScLoi();
		List<ScLoiTerm> scLoiTerms = getManagedBean()
				.getSelectedNegoTermsList();
		if (scLoiTerms != null && scLoiTerms.size() > 0) {
			deleteRowsAction(scLoi.getScNegoTerms(), scLoiTerms);
		} else {
			throw new EpException(MessageConst.ERROR_760158);
		}
	}

	/**
	 * Delete nego locations in negotiation details.
	 */
	public void deleteNegoLocations() {
		List<ScLoiNegoLocation> result = new ArrayList<ScLoiNegoLocation>();
		boolean flag = true;
		List<ScLoiNegoLocation> scLoiNegoLocations = getManagedBean()
				.getScLoi().getScNegoLocations();
		if (scLoiNegoLocations != null && scLoiNegoLocations.size() > 0) {

			for (ScLoiNegoLocation loiNegoLocation : scLoiNegoLocations) {
				if (!loiNegoLocation.isSelected()) {
					result.add(loiNegoLocation);
				} else {
					flag = false;
				}
			}
		}
		if (flag) {
			throw new EpException(MessageConst.ERROR_760158);
		}
		getManagedBean().getScLoi().setScNegoLocations(result);
	}

	/**
	 * Creates the loi. page navigate to loi details page.
	 * 
	 * @param scLoiLoa
	 *            the sc loi loa
	 * @return the string
	 * 
	 */
	public void createLOI(ScLoiLoa scLoiLoa) {
		try {
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			renewSessionWithPreviousValues();
		
			ScLoi loi = new ScLoi();
			loi.setScLoiLoa(scLoiLoa);
			reqDTO.setScLoi(loi);
			reqDTO.setAwardType(getManagedBean().getDocType());
			reqDTO.setRecommendDtlIdList(getManagedBean()
					.getRecommendDtlIds());
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOIDtl(
					reqDTO);

			populateLOIDetails(resDTO);
			getManagedBean().setEditFlag(true);
			getManagedBean().setButtonFlag(true);
			getManagedBean().setSelectedTabIndex(0);
			isSupplierEPVStatusRejected(getManagedBean().getScLoi());//#21692
			FacesContext fc = FacesContext.getCurrentInstance();
			getManagedBean().setBackToPreviousPage(
					fc.getViewRoot().getViewId());

			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, QTConstants.LOI_PAGE);
			// #29007	Vinod Start
			String message = ResourceBundleUtils.getMessage(
					QTConstants.LBL_SC_LOILOA_SUPPLIERS_PROPOSAL_SPECIFICATION_UPDATE,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
			getManagedBean().setLoiSupplierProposalspec(message);
			// #29007	Vinod End
		} finally {
		}
	}

	/**
	 * Edits the loi. page navigate to loi details page.
	 * 
	 * @param scLoi
	 *            the sc loi
	 */
	public void editLOI(ScLoi scLoi, boolean isEditFlag) {
		debugLogger("editLOI");
		FacesContext fc = FacesContext.getCurrentInstance();
		try {
			renewSessionWithPreviousValues();
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoi(scLoi);
			reqDTO.setRecommendDtlIdList(null);
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOIDtl(reqDTO);

			populateLOIDetails(resDTO);

			if (isEditFlag) {
				getManagedBean().setButtonFlag(Boolean.TRUE);
			} else {
				getManagedBean().setButtonFlag(Boolean.FALSE);
			}

			getManagedBean().setEditFlag(Boolean.TRUE);
			
			getManagedBean().setReadOnly(!isEditFlag);

			if (getManagedBean().getBackToPreviousPage() == null
					|| getManagedBean().getBackToPreviousPage().isEmpty()) {
				getManagedBean()
				.setBackToPreviousPage(fc.getViewRoot().getViewId());
			}
			getManagedBean().setSelectedTabIndex(0);
			// QT Proposal validity expired
			getQtProposalValidExpiredMessage(getManagedBean().getScQt());
			// #29007	Vinod Start
			String message = ResourceBundleUtils.getMessage(
					QTConstants.LBL_SC_LOILOA_SUPPLIERS_PROPOSAL_SPECIFICATION_UPDATE,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
			getManagedBean().setLoiSupplierProposalspec(message);
			// #29007	Vinod End
		
		} finally {
			getLOITaskId();
		}

		//#21692 - Start
		if (null != getManagedBean().getRecSupplierList() && !getManagedBean().getRecSupplierList().isEmpty()) {
			isSupplierEPVStatusRejected(scLoi);
			getSupplierRejectProposalValidityExtensionNotification(scLoi);
		}
		//#21692 - End
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, QTConstants.LOI_PAGE);
	}
	
	/*
	 * Enhancement #21694 START
	 * TO GET BPM TASK ID DIRECT WITHOUT USING TASKLIST
	 * PURPOSE: To check whether the LOI is in the SOA or not
	 */
	
	public void getLOITaskId(){
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
		bpmReqDTO.setDocNumber(getManagedBean().getScLoi().getLoiNo());
		bpmReqDTO.setState(BpmConstants.STATE_ASSIGNED);
		
		BpmResponseDTO bpmResDTO = bpmSO.getTasksByDocNumber(bpmReqDTO);
		 if(bpmResDTO != null && CollectionUtils.isNotEmpty(bpmResDTO.getWorklistVO().getWorklistItem())){
			 for(WorklistItemVO currentVO: bpmResDTO.getWorklistVO().getWorklistItem()){
				getManagedBean().setLoiTaskId(currentVO.getTaskId());
			 }
		 }else{
			 getManagedBean().setLoiTaskId(null);
		 }
	}
	/*
	 * Enhancement #21694 END
	 */

	/**
	 * Reaload loi.
	 * 
	 * @param scLoi
	 *            the sc loi
	 */
	private void reloadLOI(ScLoi scLoi) {
		try {
			renewSessionWithPreviousValues();
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoi(scLoi);
			reqDTO.setRecommendDtlIdList(null);
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOIDtl(reqDTO);

			populateLOIDetails(resDTO);
			getManagedBean().setButtonFlag(Boolean.TRUE);
			// #29007	Vinod Start
			String message = ResourceBundleUtils.getMessage(
					QTConstants.LBL_SC_LOILOA_SUPPLIERS_PROPOSAL_SPECIFICATION_UPDATE,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
			getManagedBean().setLoiSupplierProposalspec(message);
			// #29007	Vinod End

		} finally {
		}
	}

	/**
	 * Populate loi details.
	 * 
	 * @param resDTO
	 *            the res dto
	 */
	private void populateLOIDetails(AwardSupplierResDTO resDTO) {
		boolean isCalculateMaxPriceItem = Boolean.FALSE;// calculate max price items only.
		boolean isTermPriceFound = Boolean.FALSE;
		boolean isBiddingAward = Boolean.FALSE;
		ScLoi scLoi = resDTO.getScLoi();
		getManagedBean().setScLoi(scLoi);

		//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
		ScWorkFlowStatus currentStatus = scLoi != null ?scLoi.getScWorkFlowStatus():null;
		Long statusId = currentStatus != null ? currentStatus.getPmStatus().getStatusId():null;//20230711@Asyrafhaziq-SQUBE Bug Minor #16

		// online bidding
		if (scLoi != null && scLoi.getRecommendBy() != null) {//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
			PmParameter recommendedBy = scLoi.getRecommendBy();
			if (ParameterConst.RECOMMEND_BY_ONLINE_BIDDING
					.equals(recommendedBy.getParameterCode())) {
				isBiddingAward = Boolean.TRUE;
				// remove nego price for online bidding.
				removeNegoTermPrice(scLoi);
			} else if (ParameterConst.RECOMMEND_BY_AWARD
					.equals(recommendedBy.getParameterCode())) {
				// remove nego price for Direct Award
				removeNegoTermPrice(scLoi);
			}
		}
		
		// check nego term price
		if (QTStatusConst.LOI_PENDING_NEGOTIATION.equals(statusId)) {
			isTermPriceFound = checkNegoTermPrice(scLoi);
		}
		
		if(!isBiddingAward){
			if (isTermPriceFound) {
				getManagedBean().setHideColumnFlag(Boolean.TRUE);

			} else {
				getManagedBean().setHideColumnFlag(Boolean.FALSE);
			}
		}
	
		List<ScLoiLoaItem> filteredItems  = getManagedBean().getScLoiLoaItems();
		if(null == filteredItems){
			filteredItems  = filterZoneTierOrTierPriceItems(scLoi);
			getManagedBean().setScLoiLoaItems(filteredItems);
		}

		// calc total items in award details
		if (scLoi != null && scLoi.getScLoiLoaItems() != null && !scLoi.getScLoiLoaItems().isEmpty()) { //20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
			BigDecimal offerPrice = BigDecimal.valueOf(0d);
			BigDecimal offeredPriceSubTotal = BigDecimal.valueOf(0d);
			BigDecimal offeredPriceAmount = BigDecimal.valueOf(0d);

			BigDecimal finalPrice = BigDecimal.valueOf(0d);
			BigDecimal finalPriceSubTotal = BigDecimal.valueOf(0d);
			BigDecimal finalPriceAmount = BigDecimal.valueOf(0d);

			BigDecimal afterNegoSubTotal = BigDecimal.valueOf(0d);
			BigDecimal afterNegoTotalAmount = BigDecimal.valueOf(0d);
			
			BigDecimal quantity = BigDecimal.valueOf(0d);

			List<ScLoiLoaItem> scLoiLoaItems = scLoi.getScLoiLoaItems();
			for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItems) {
				quantity = scLoiLoaItem.getQty();
				if(null == scLoiLoaItem.getTplSpecTierQty() ){
					isCalculateMaxPriceItem = Boolean.TRUE;
				} else{

					if(filteredItems.contains(scLoiLoaItem)){
						isCalculateMaxPriceItem = Boolean.TRUE;
					} else{
						isCalculateMaxPriceItem = Boolean.FALSE;
					}
				}

				if(isCalculateMaxPriceItem){
					if (scLoiLoaItem.getScQtProposalItem() != null) {
						// offered price sub total and total
						offerPrice = scLoiLoaItem.getScQtProposalItem().getOfferPrice();
						offeredPriceSubTotal = TemplateSpecHelper.getInstance().calculateTotalAmount(scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), offerPrice, quantity);
						if(offeredPriceSubTotal != null){
							scLoiLoaItem.setOfferedPriceSubTotal(offeredPriceSubTotal);
							offeredPriceAmount = offeredPriceAmount.add(offeredPriceSubTotal).setScale(2, QTConstants.QT_ROUNDING_MODE);	//Bug #35600@Syazani 19/09/2024
						}

						// final price sub total and total - for bidding.
						finalPrice = scLoiLoaItem.getScQtProposalItem().getFinalPrice();
						finalPriceSubTotal = TemplateSpecHelper.getInstance().calculateTotalAmount(scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), finalPrice, quantity);
						if(finalPriceSubTotal != null){
							scLoiLoaItem.setAfterNegoSubTotal(finalPriceSubTotal);
							
							// For bid re finalization final price as bid offer price - subtotal
							scLoiLoaItem.setBidFinalPriceSubTotal(finalPriceSubTotal);
							finalPriceAmount = finalPriceAmount.add(finalPriceSubTotal).setScale(2, QTConstants.QT_ROUNDING_MODE);	//Bug #35600@Syazani 19/09/2024
						}

						// after nego unit price sub total and total
						afterNegoSubTotal = TemplateSpecHelper.getInstance().calculateTotalAmount(scLoiLoaItem.getScRequestItem().getTplSpecQuestion(), scLoiLoaItem.getUnitPrice(), quantity);
						if (afterNegoSubTotal != null) {
							afterNegoTotalAmount = afterNegoTotalAmount.add(afterNegoSubTotal).setScale(2, QTConstants.QT_ROUNDING_MODE);	//Bug #35600@Syazani 19/09/2024
							scLoiLoaItem.setAfterNegoSubTotal(afterNegoSubTotal);

						}
					}
				}
			}
			
			scLoi.setOfferedPriceTotal(offeredPriceAmount); // offered price total
			
			// when bid re finalization final price as bid offer price
			scLoi.setBidFinalPriceTotal(finalPriceAmount); 
			
			if(isBiddingAward){
				scLoi.setLoiAmt(finalPriceAmount); // unit price total
				scLoi.setAfterNegoTotal(finalPriceAmount); // after nego total
			} else{
				scLoi.setLoiAmt(afterNegoTotalAmount); // unit price total
				scLoi.setAfterNegoTotal(afterNegoTotalAmount); // after nego total
			}

			scLoi.setScLoiLoaItems(scLoiLoaItems);
		}
		
		checkReFinalizationBidding(resDTO.getScQtFinalization(), resDTO.getScQt());
		
		// branch list
		if (resDTO.getSmSupplierBranchs() != null
				&& resDTO.getSmSupplierBranchs().size() > 0) {
			getManagedBean().setBranchsList(resDTO.getSmSupplierBranchs());
		}

		// Supplier Branch
		getManagedBean().setBranchName(scLoi != null ? scLoi.getSupplierBranch():null);//20230712@asyrafhaziq- SQUBE Bug NullPointer#3
		getManagedBean()
				.setSupplierHQPersonnel(resDTO.getSupplierHQPersonnel());
		getManagedBean().setScQt(resDTO.getScQt());

		if (scLoi != null && scLoi.isBrandNew()) {//20230712@asyrafhaziq- SQUBE Bug NullPointer#3
			setUserDetails(scLoi);
			handleBranchDetailsChanged();
		}
			
		Integer loaRespPeriod = getDaysByRuleType(RuleTypeConst.LOI_RESPONSE_PERIOD);
		if (scLoi != null){
		// Issue & Acknowledge Date
		if(scLoi.getIssueDate() == null){
			scLoi.setIssueDate(DateUtils.getCurrentDateWithoutTime());
		}
		if(scLoi.getAckDueDate() != scLoi.getIssueDate() && scLoi.getAckDueDate() != null){
			long days = DateUtils.getDaysDifference(scLoi.getIssueDate(), scLoi.getAckDueDate());
			scLoi.setLoiResponseDate((int) days);
		}
		else{
			scLoi.setLoiResponseDate(loaRespPeriod);
		}
		
		scLoi.setAckDueDate(DateUtils.addDays(scLoi.getIssueDate(), scLoi.getLoiResponseDate()));
	 }
		// approvers
		ScLoiApproval scLoiApproval = scLoi != null ? scLoi.getScLoiApproval():null;//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
		PmUser approverUser = null;
		if (scLoiApproval != null) {
			approverUser = scLoiApproval.getApprover();
			//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
			if (scLoi != null && !QTStatusConst.LOI_PENDING_SUBMISSION.equals(scLoi
					.getScWorkFlowStatus().getPmStatus().getStatusId())) {
				getManagedBean().setScLoiApproval(scLoi.getScLoiApproval());
			}
		}
		if (approverUser == null) {
			approverUser = new PmUser();
		}
		getManagedBean().setApproverUser(approverUser);
		if (resDTO.getApproverList() != null
				&& resDTO.getApproverList().size() > 0) {
			getManagedBean().setApproverNames(resDTO.getApproverList());
		}

		getManagedBean().setScLoi(resDTO.getScLoi());

		// APPPROVER HISTORY
		getManagedBean().getLois().add(resDTO.getScLoi());

		// remarks
		if(!QTStatusConst.LOI_COMPLETED.equals(statusId)){
			getManagedBean().getScLoi().setScLoiRemarks(
					initializeRemarks(getManagedBean().getScLoi(), 
							currentStatus,
							getManagedBean().getScLoi().getScLoiRemarks(),
							ScLoiRemark.class));
			// # 11134
			List<ScLoiRemark> supplierRemarks = filterSupplierRemarks(getManagedBean().getScLoi().getScLoiRemarks(), false);
			getManagedBean().setSupplierRemarks(supplierRemarks);
		}
		// status
		getCurrentStatus(currentStatus != null ?currentStatus.getPmStatus() 
				.getStatusId():null);//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 

		// Contract Signer
		PmUser contractSignerUser = scLoi != null ?scLoi.getContractSigner():null;//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
		if (contractSignerUser == null) {
			contractSignerUser = new PmUser();
		}

		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setRoleCode(RoleCode.CONTRACT_SIGNER.value());
		//20210406@asyrafhaziq - Bug #30662 - Start
		//if (scLoi.getLoiAmt() != null) {
		//	userReqDTO.setApprovalLimit(scLoi.getLoiAmt());
		//}
		//userReqDTO.setOrgProfileId(scLoi.getScLoiLoa().getPmOrgProfile()
		//	.getOrgProfileId());
		userReqDTO.setMinisterOrgProfileId(retrieveMinistryOrgProfile(
				scLoi != null ?scLoi.getScLoiLoa().getPmOrgProfile()
						.getOrgProfileId():null).getOrgProfileId());
		
		//@asyrafhaziq #27502 - New DO MOF Role and Process - start
		Long contractType = resDTO.getScQt().getContractType().getParameterId();

		if (contractType != null && contractType.equals(SourcingConstants.CENTRAL_CONTRACT)) {
			PmZone zone = SmCommonUtil.getInstance().getUserZone();
			if (zone != null) {
				userReqDTO.setZoneCode(zone.getZoneCode());
			}
			userReqDTO.setBpk(true);
		}
		//@asyrafhaziq #27502 - New DO MOF Role and Process - End
		//20210406@asyrafhaziq - Bug #30662 - End
		UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);

		getManagedBean().setContractSignerUser(contractSignerUser);
		getManagedBean().setContractSigners(userResDTO.getUserList());
		//20230111@Haziq - #12487
		getManagedBean().setMinistryOrgProfile(retrieveMinistryOrgProfile(
				scLoi != null ?scLoi.getScLoiLoa().getPmOrgProfile()
						.getOrgProfileId():null));
		if (scLoi != null) // 20230712@asyrafhaziq- SQUBE Bug NullPointer#3 s
			scLoi.setFileExtnNo("001");//Sonarqube #83
		
		//#30008 - Start
		if (scLoi.getNegoDecision() != null) {
			this.getManagedBean().setDecisionCode(String.valueOf(scLoi.getNegoDecision().getParameterId()));
			getManagedBean().setJustification(scLoi.getDecisionJustification());
		}
		//#30008 - End
		
		/*
		 * if (QTStatusConst.LOI_PENDING_NEGOTIATION.equals(scLoi
		 * .getScWorkFlowStatus().getPmStatus().getStatusId())) {
		 * 
		 * if (getManagedBean().getScQt() != null) { if
		 * (("P").equals(getManagedBean().getScQt().getScheduleType())) { //
		 * progressive payment. formProgressivePayment(scLoi); } else if
		 * (("F").equals(getManagedBean().getScQt() .getScheduleType())) { //
		 * Fulfilment Schedule formFulfilmentSchedule(scLoi); } } }
		 */
	}

	
	/**
	 * Check re finalization bidding.
	 *
	 * @param scQtFinalization the sc qt finalization
	 * @param scQt the sc qt
	 */
	private void checkReFinalizationBidding(ScQtFinalization scQtFinalization, ScQt scQt){
		if(scQtFinalization != null && scQt != null){
			getManagedBean().setScQt(scQt);
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			boolean isScQtRefinalized = getQtAwardSO().isQtRefinalized(reqDTO).isRefinalized();

			if(isScQtRefinalized){
				PmParameter awardType = scQtFinalization.getFinalizationMethod();
				boolean isBidding = awardType.getParameterCode().equals(ParameterConst.RECOMMEND_BY_ONLINE_BIDDING);
				if(isBidding) {
					getManagedBean().setRefinalizedBidding(Boolean.TRUE);
				} else{
					getManagedBean().setRefinalizedBidding(Boolean.FALSE);
				}	
			}
		}
	}

	/**
	 * Handle branch details changed. shows the supplier address details.
	 */
	public void handleBranchDetailsChanged() {
		debugLogger("handleBranchDetailsChanged");
		ScLoi scLoi = this.getManagedBean().getScLoi();
		String selectedSupplierBranchName = this.getManagedBean()
				.getBranchName();
		   String supplierBranchAsHQ = QTConstants.SUPPLIER_INFO_HQ;
		if (selectedSupplierBranchName == null
				|| supplierBranchAsHQ.equals(selectedSupplierBranchName)) {
			SmPersonnel supplierHQPersonnel = this.getManagedBean()
					.getSupplierHQPersonnel();
			SmCompanyBasic smCompanyBasic = null;
			if (supplierHQPersonnel != null) {
				smCompanyBasic = supplierHQPersonnel.getSmAppl()
						.getSmCompanyBasic();
			}

			if (smCompanyBasic != null) {
				for (SmCompanyAddress smCompanyAddress : smCompanyBasic
						.getSmCompanyAddresses()) {
					if (AddressType.CONTACT_ADDRESS.value().equals(
							smCompanyAddress.getAddressType())) {
						debugLogger("found hq address");

						scLoi.setSupplierBranch(supplierBranchAsHQ);
						
						ScAddress supplierAddress = createOrUpdateScAddress(
														scLoi.getScSupplierAddress(),
														smCompanyAddress.getSmAddress(), 
														supplierHQPersonnel, smCompanyBasic);
						scLoi.setScSupplierAddress(supplierAddress);

						break;
					}
				}
			}
		} else {
			List<SmSupplierBranch> supplierBranchs = this.getManagedBean()
					.getBranchsList();

			for (SmSupplierBranch supplierBranch : supplierBranchs) {
				if (supplierBranch.getBranchName().equals(
						selectedSupplierBranchName)) {
					debugLogger("found branch address");

					scLoi.setSupplierBranch(supplierBranch.getBranchName());
					
					SmCompanyBasic smCompanyBasic = null;
					SmPersonnel smPersonnel = supplierBranch.getSmPersonnel();
					if (smPersonnel != null) {						
						if (smPersonnel.getSmAppl() != null) {
							smCompanyBasic = smPersonnel.getSmAppl()
												.getSmCompanyBasic();
						}
					}					
					ScAddress supplierAddress = createOrUpdateScAddress(
													scLoi.getScSupplierAddress(),
													supplierBranch.getSmAddress(), 
													smPersonnel, smCompanyBasic);
					scLoi.setScSupplierAddress(supplierAddress);

					break;
				}
			}
		}

	}
	
	/**
	 * Enhancement #21694
	 * to be used in Screen From Tasklist Only
	 * @param obj
	 * 		instance of ScQt/ScLoiForQt
	 * @param previousPageUrl
	 * @return
	 */
	public String reverseAction() {
		
		debugLogger("viewReverseStatusDetails");
		FacesContext fc = FacesContext.getCurrentInstance();
		QTDetailBackingBean qtDtl = new QTDetailBackingBean();
		ScLoi scLoi = this.getManagedBean().getScLoi();
		//String docType = getManagedBean().getQtnTenderSearchBean().getResultDocType();
		ScQt scQt = this.getManagedBean().getScQt();
		ScLoiForQt scLoiForQt = new ScLoiForQt();
		
		/**
		 * FINDING QT WORKFLOW-> CASE REVERSAL QT
		 */
		IScWorkFlowStatusSO workFlowStatusSO;
		ScWorkFlowStatusReqDTO scWreqDTO = new ScWorkFlowStatusReqDTO();
		workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);	
		scWreqDTO.setDocId(scQt.getQtId());		
		scWreqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
		ScWorkFlowStatus qtcurrentWorkFlowStatus = workFlowStatusSO.retrieveStatusByTypeAndId(scWreqDTO).getWorkFlowStatus();
		if (!qtcurrentWorkFlowStatus.getPmStatus().getStatusId().equals(QTStatusConst.QT_PENDING_LOI)) {
			throw new EpException(MessageConst.ERROR_760496,new String[]{scQt.getQtNo()}); 
		}
		
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		reqDTO.setLoiId(scLoi.getLoiId());
		AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLoiForQtByLoiId(reqDTO);
		
		scLoiForQt = resDTO.getScLoiForQt();
		
		scWreqDTO = new ScWorkFlowStatusReqDTO();
		
		qtDtl.getManagedBean().setEditable(Boolean.TRUE);		
			
		Map<MapKeyEnum, Object> otherArgs = new HashMap<MapKeyEnum, Object>();
		otherArgs.put(MapKeyEnum.loiLoaSelected, scLoi);
		
		 /* FINDING WORKFLOW STATS*/
		scWreqDTO.setDocId(scLoi.getLoiId());		
		scWreqDTO.setDocType(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
		
		ScWorkFlowStatus currentWorkFlowStatus = workFlowStatusSO.retrieveStatusByTypeAndId(scWreqDTO).getWorkFlowStatus();
		qtDtl.getManagedBean().setTaskId(getManagedBean().getLoiTaskId());
		qtDtl.getManagedBean().setPreviousPage(this.getManagedBean().getBackToPreviousPage());
		qtDtl.getManagedBean().setQt(scQt);
		qtDtl.getManagedBean().setScLoiForQt(scLoiForQt);
		qtDtl.getManagedBean().setStatus(getStatusDescById(currentWorkFlowStatus.getPmStatus().getStatusId()));
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, qtDtl.reverseQtDetails(Boolean.TRUE,Boolean.FALSE,qtDtl));
		
		return null;
	}

	/**
	 * save the LOI details. save - save data into database and initiate task
	 * for LOI details. page navigate to loi details page.
	 */
	public void saveAction() {
		debugLogger("saveAction");
		try {
			ScLoi scLoi = this.getManagedBean().getScLoi();
			ScQt scQt=this.getManagedBean().getScQt();
			String outcome = BpmOutcomeConst.BPM_SAVE;
			
			//#30008 - Start
			if (this.getManagedBean().getDecisionCode() != null) {
				Long parameterId = Long.parseLong(this.getManagedBean().getDecisionCode());

				IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

				ParameterReqDTO reqDTO2 = new ParameterReqDTO();
				reqDTO2.setParameterId(parameterId);
				ParameterResDTO resDTO2 = parameterSO.retrieveParameterById(reqDTO2);
				PmParameter pmParameter = resDTO2.getPmParameter();
				scLoi.setNegoDecision(pmParameter);
				scLoi.setDecisionJustification(this.getManagedBean().getJustification());
				
			}// #30008 - end
			
			parseLOIDetails(scLoi, outcome);
			
			//pending loiloas
			List<ScLoiLoa> pendingloiloas  = getPendingLoiLoas(scLoi, getManagedBean().getRecSupplierList());
			
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoi(scLoi);
			reqDTO.setOutcome(outcome);
			reqDTO.setSourceId(getManagedBean().getSourceId()); // Bug #33120
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setPendingloiloas(pendingloiloas);
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());

			AwardSupplierResDTO resDTO = getQtAwardSO().saveLOIDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoi = resDTO.getScLoi();
				reloadLOI(scLoi);
				this.getManagedBean().setScQt(scQt);
				isSupplierEPVStatusRejected(scLoi);//#21692
				successMessage(MessageConst.INFO_700004, ParameterTypeConst.DOCUMENT_TYPES, 
						ParameterConst.DOCTYPE_LETTER_OF_INTENT, scLoi.getLoiNo(), Boolean.FALSE);
			}

		} finally {
		}
	}
	
	/**
	 * Gets the pending loi loas.
	 *
	 * @param scLoi the sc loi
	 * @param pendingloiloas the pendingloiloas
	 * @return the pending loi loas
	 */
	private List<ScLoiLoa> getPendingLoiLoas(ScLoi scLoi, List<ScLoiLoa> pendingloiloas){
		List<ScLoiLoa> results = new ArrayList<ScLoiLoa>();
		for (ScLoiLoa scLoiLoa : pendingloiloas) {
			if(scLoi != null && null != scLoi.getScLoiLoa()){
				if(!scLoiLoa.getLoiLoaId().equals(scLoi.getScLoiLoa().getLoiLoaId())){
					results.add(scLoiLoa);
				}
			}
		}
		return results;
	}

	/**
	 * submit LOI list details. submit - save data into database and BPM process
	 * will route to the next respective user.
	 * 
	 * @param outcome
	 *            the outcome
	 * @return the string page navigate to acknowledge page.
	 */
	public String submitLoiListAction() {
		debugLogger("submitLoiListAction");

		try {
			ScQt scQt = getManagedBean().getScQtFinalization().getScQt();

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setDocType(getManagedBean().getDocType());
			reqDTO.setLoiLoaList(getManagedBean().getRecSupplierList());
			reqDTO.setOutcome(BpmOutcomeConst.BPM_SUBMIT);

			AwardSupplierResDTO resDTO = getQtAwardSO().completeLoiListBPMTask(
					reqDTO);

			if (resDTO.isSuccessful()) {
				renewSesionObject();
				this.getManagedBean().setScQt(scQt);
				successMessage(MessageConst.INFO_700033, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION, scQt.getQtNo(), Boolean.TRUE);
			}

		} finally {
			// iqtAwardSO = null;
		}

		return null;
	}

	/**
	 * save LOI list details. save - save data into database.
	 * @param outcome
	 *            the outcome
	 * @return the string
	 */
	public String savetLoiListAction() {
		debugLogger("savetLoiListAction");
		
		// #33325 - Start.
		if (QTStatusConst.SF_PENDING_LOI_PREPARATION.equals(getManagedBean().getCurrentStatusId())) {
			if (StringUtils.isNotEmpty(getManagedBean().getDocType()) 
					&& !getManagedBean().getDocType().equals(ParameterConst.RECOMMEND_BY_MULTIPLE_LOI)) {
				if (CollectionUtils.isNotEmpty(getManagedBean().getRecSupplierList())) {
					for (ScLoiLoa scLoiLoa: getManagedBean().getRecSupplierList()) {
						if (null == scLoiLoa.getLoiRequired()) {
							throw new EpException(MessageConst.ERROR_775177, new String[] { scLoiLoa.getSmSupplier().getCompanyName() });
						}
					}
				}
			}
		}
		// #33325 - End.
		
		AwardSupplierResDTO resDTO = new AwardSupplierResDTO();
		try {
			ScQtFinalization scQtFinalization = getManagedBean()
					.getScQtFinalization();
			String docType = getManagedBean().getDocType();
			Long sourceId = getManagedBean().getSourceId();
			List<String> recommendDtlIds = getManagedBean()
					.getRecommendDtlIds();
			Object payLoadData = getManagedBean().getPayLoad();
			boolean isLoiRequired  = getManagedBean().isLoiRequired();
			String currentStatus = getManagedBean().getStatus();
			Long currentStatusId = getManagedBean().getCurrentStatusId();
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setOutcome(BpmOutcomeConst.BPM_SAVE);
			reqDTO.setLoiLoaList(getManagedBean().getRecSupplierList());
			resDTO = getQtAwardSO().saveLoiLoaList(reqDTO);

			if (resDTO.isSuccessful()) {
				renewSesionObject();
				getManagedBean().setScQtFinalization(scQtFinalization);
				getManagedBean().setDocType(docType);
				getManagedBean().setRecSupplierList(resDTO.getLoiLoas());
				getManagedBean().setSourceId(sourceId);
				getManagedBean().setRecommendDtlIds(recommendDtlIds);
				getManagedBean().setPayLoad(payLoadData);
				getManagedBean().setLoiRequired(isLoiRequired);
				getManagedBean().setStatus(currentStatus);
				getManagedBean().setCurrentStatusId(currentStatusId);
				reloadLOIList(sourceId);
				if(null!= scQtFinalization){
					getManagedBean().setScQt(scQtFinalization.getScQt());
				}
				
				// 26247 - #33325 - start
				if (CollectionUtils.isNotEmpty(getManagedBean().getRecSupplierList())) {
					for (ScLoiLoa scLoiLoa : getManagedBean().getRecSupplierList()) {
						SmSupplier supplier = scLoiLoa.getSmSupplier();
						supplier = this.getQtSuppFinalizationSO().evaluateDisciplinaryActions(supplier,
								scLoiLoa.getDocId());
						scLoiLoa.setSmSupplier(supplier);
					}
				} 
				// 26247 - #33325 - end
				
				successMessage(MessageConst.INFO_700025, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION, scQtFinalization != null ?scQtFinalization.getScQt().getQtNo():null, Boolean.FALSE);
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, QTConstants.LOI_LIST_PAGE);
			}

		} finally {
		}
		return null;
	}
	
	/**
	 * Reload loi list.
	 *
	 * @param qtId the qt id
	 */
	private void reloadLOIList(Long qtId) {
		debugLogger("reloadLOIList");
		try {
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setRecommendDtlIdList(getManagedBean().getRecommendDtlIds());
			AwardSupplierResDTO resDTO = getQtAwardSO().retrievePendingLoiList(
					reqDTO);
			// rec supplier in loi list
			List<ScLoiLoa> recSupplierList = resDTO.getLoiLoas();
			getManagedBean().setRecSupplierList(recSupplierList);
			//disableIsLoiRequired(recSupplierList);
		} finally{
		}
	}

	/**
	 * Loi required listener.
	 * 
	 * @param scLoiLoa
	 *            the sc loi loa
	 */
	public void loiRequiredListener(ScLoiLoa scLoiLoa) {
		debugLogger("loiRequiredListener");
		int index = getManagedBean().getRecSupplierList().indexOf(scLoiLoa);
		getManagedBean().getRecSupplierList().set(index, scLoiLoa);
		List<ScLoiLoa> pendingLois  = getManagedBean().getRecSupplierList();

		checkIsLoiProcessRequired(pendingLois);
	}

	/**
	 * submit LOI details. submit - save data into database and BPM process will
	 * route to the next respective user.
	 * 
	 * @param outcome
	 *            the outcome
	 * @return the string page navigate to acknowledge page.
	 */
	public String submitAction() {
		debugLogger("submitAction");
		try {
			SCAwardSupplierDataType awardDataType = (SCAwardSupplierDataType) getManagedBean()
					.getPayLoad();

			ScLoi scLoi = this.getManagedBean().getScLoi();
			String outcome = BpmOutcomeConst.BPM_SUBMIT;
			ScQt scQt=this.getManagedBean().getScQt();
			parseLOIDetails(scLoi, outcome);
			
			scLoi.setScLoiWorkFlowStatuses(null);
			// To avoid insert workflow status when direct submit without save.
			if (scLoi.isBrandNew()) {
				List<ScLoiRemark> remarks = scLoi.getScLoiRemarks();
				if (CollectionUtils.isNotEmpty(remarks)) {
					for (ScLoiRemark remark : remarks) {
						remark.setScWorkFlowStatus(null);
					}
				}	
			}
			
			//pending loiloas
			List<ScLoiLoa> pendingloiloas  = getPendingLoiLoas(scLoi, getManagedBean().getRecSupplierList());
			
			//#30008 - Start
			if (this.getManagedBean().getDecisionCode() != null) {
				Long parameterId = Long.parseLong(this.getManagedBean().getDecisionCode());

				IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

				ParameterReqDTO reqDTO2 = new ParameterReqDTO();
				reqDTO2.setParameterId(parameterId);
				ParameterResDTO resDTO2 = parameterSO.retrieveParameterById(reqDTO2);
				PmParameter pmParameter = resDTO2.getPmParameter();
				scLoi.setNegoDecision(pmParameter);
				scLoi.setDecisionJustification(this.getManagedBean().getJustification());

				if (ParameterConst.NOT_REACH_AGREEMENT.equalsIgnoreCase(String.valueOf(scLoi.getNegoDecision().getParameterId()))) {
					awardDataType.setIsAgreement(false);
				} else {
					awardDataType.setIsAgreement(true);
				}
			}// #30008 - end

			scLoi.setIssueDate(DateUtils.getCurrentDateWithoutTime());
			//Integer loaRespPeriod = getDaysByRuleType(RuleTypeConst.LOI_RESPONSE_PERIOD);
			scLoi.setAckDueDate(DateUtils.addDays(scLoi.getIssueDate(), scLoi.getLoiResponseDate()));

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setScLoi(scLoi);
			reqDTO.setOutcome(outcome);
			reqDTO.setSourceId(getManagedBean().getSourceId());
			reqDTO.setPayLoad(awardDataType);
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setPendingloiloas(pendingloiloas);
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());

			AwardSupplierResDTO resDTO = getQtAwardSO().saveLOIDtl(reqDTO);
			
			//#12487 start
			//LOIDetailManagedBean loiObj = new LOIDetailManagedBean();
			/*Map loiObj = new HashMap<>();
			loiObj.put("language", new FacesScopes().getUserLocale());
			if (getManagedBean().getScQtFinalization() != null) {
				if (getManagedBean().getScQtFinalization().getLoiLoaById() != null) {
					loiObj.put("assignUser", getManagedBean().getScQtFinalization().getLoiLoaById());									
				}
			}			
			PmOrgProfile pmOrgProfile = new PmOrgProfile();
			Long profileId = getManagedBean().getScQt().getPmOrgProfile().getOrgProfileId();
			Date createdDate = this.getManagedBean().getScQtFinalization().getCreatedDate();
			loiObj.put("PTJName", retrievePTJByOrgProfileId(profileId, createdDate));
			loiObj.put("PTJCode", retrievePTJCodeByOrgProfileId(profileId, createdDate));
			pmOrgProfile = retrieveMinistryOrgProfile(profileId);
			if(null != pmOrgProfile){
				if(null != pmOrgProfile.getPmOrgValidity()){
					loiObj.put("ministryName", pmOrgProfile.getPmOrgValidity().getOrgName());
				}
			}			
			getManagedBean().setLoiObj(loiObj);*/
			//#12487 end

			if (resDTO.isSuccessful()) {
				scLoi = resDTO.getScLoi();
				renewSesionObject();
				this.getManagedBean().setScQt(scQt);
				successMessage(MessageConst.INFO_700033, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_LETTER_OF_INTENT, scLoi.getLoiNo(), Boolean.TRUE);
			}

		} finally {
			// qtAwardSO = null;
		}
		return null;
	}

	/**
	 * Update loi list bpm task action. update bpm - save data into database and
	 * BPM process will route to the next respective user.
	 * 
	 * @param outcome
	 *            the outcome
	 * @return the string page navigate to acknowledge page.
	 */
	public void updateLOIListBpmTaskAction(String outcome) {
		debugLogger("updateLOIListBpmTaskAction");
		try {

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setOutcome(outcome);
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setPayLoad(getManagedBean().getPayLoad());

			AwardSupplierResDTO resDTO = getQtAwardSO().updateBPMAction(reqDTO);

			if (resDTO.isSuccessful()) {
				String qtNo = getManagedBean().getScQtFinalization().getScQt()
						.getQtNo();

				renewSesionObject();

				successMessage(MessageConst.INFO_700033,
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_LETTER_OF_INTENT, qtNo, Boolean.TRUE);
			}
		} finally {
			// iqtAwardSO = null;
		}
	}
	
	/**
	 * Update bpm task. update bpm - save data into database and BPM process
	 * will route to the next respective user.
	 * 
	 * @param outcome
	 *            the outcome
	 * @param digiSign
	 *            the digi sign
	 * @return AwardSupplierResDTO
	 */
	private AwardSupplierResDTO updateBPMTask(String outcome,
			PmDigiSign digiSign) {
		debugLogger("updateBPMTask");
		AwardSupplierResDTO awardSupplierResDTO = null;
		try {

			SCAwardSupplierDataType awardDataType = (SCAwardSupplierDataType) getManagedBean()
					.getPayLoad();

			ScLoi scLoi = this.getManagedBean().getScLoi();
			Long statusId = scLoi.getScWorkFlowStatus().getPmStatus().getStatusId();//20230711@Asyrafhaziq-SQUBE Bug Minor #16
			parseLOIDetails(scLoi, outcome);
			
			// remove empty remark - start
			List<ScLoiRemark> scLoiRemarks = scLoi.getScLoiRemarks();
			List<ScLoiRemark> finalScLoiRemarks = new ArrayList<>();
			if(CollectionUtils.isNotEmpty(scLoiRemarks)){
				for(ScLoiRemark each : scLoiRemarks){
					if(!StringUtils.isEmpty(each.getRemark())){
						finalScLoiRemarks.add(each);
					}
				}
			}
			scLoi.setScLoiRemarks(finalScLoiRemarks);
			// remove empty remark - end
			
			//#21692 - Start
			if (QTStatusConst.LOI_PENDING_NEGOTIATION
					.equals(statusId)) {
				awardDataType = addLastDocumentPayload(scLoi, awardDataType);
			}
			//#21692 - End
			
			//#30008 - Start
			Long parameterId = null;
			if (this.getManagedBean().getDecisionCode() != null) {
				parameterId = Long.parseLong(this.getManagedBean().getDecisionCode());
			}

			IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

			ParameterReqDTO reqDTO2 = new ParameterReqDTO();
			if (parameterId != null) {
				reqDTO2.setParameterId(parameterId);

				ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO2);
				PmParameter pmParameter = resDTO.getPmParameter();
				scLoi.setNegoDecision(pmParameter);
				scLoi.setDecisionJustification(this.getManagedBean().getJustification());

				if (ParameterConst.NOT_REACH_AGREEMENT.equalsIgnoreCase(String.valueOf(scLoi.getNegoDecision().getParameterId()))) {
					awardDataType.setIsAgreement(false);
				} else {
					awardDataType.setIsAgreement(true);
				}
			}
			// #30008 - end

			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO
					.setDocType(LoiLoaDocType.DOCTYPE_QUOTATION_TENDER.value());
			awardSupplierReqDTO.setScLoi(scLoi);
			awardSupplierReqDTO.setSourceId(getManagedBean().getSourceId());
			awardSupplierReqDTO.setOutcome(outcome);
			awardSupplierReqDTO.setToken(new FacesScopes()
					.getBPMAuthenticationCtxToken());
			awardSupplierReqDTO.setTaskId(this.getManagedBean().getTaskId());
			awardSupplierReqDTO.setPayLoad(awardDataType);
			awardSupplierReqDTO.setPmUser(new FacesScopes()
					.getUserSessionBean());
			awardSupplierReqDTO.setRoleCode(getUserRoleCode());
			awardSupplierReqDTO.setUserLocale(new FacesScopes().getUserLocale());

			// spki
			if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
				String originalText = prepareOriginalText(scLoi.getScLoiLoa()
						.getSmSupplier());
				byte[] signedData = getManagedBean().getSpkiBackingBean()
						.spkiSignTransaction(originalText);
				digiSign.setSpkiSignedData(signedData);
				if (null != digiSign) {
					updateSpkiDigiSignData(digiSign,
							ParameterConst.DOCTYPE_LETTER_OF_INTENT,
							scLoi.getLoiId(), scLoi.getLoiNo(), originalText);
				}
				
				RequestContext.getCurrentInstance().execute(
						"blockWidget.hide();");
			} else {
				// gpki
				if (null != digiSign) {
					updatePmDigiSignData(digiSign,
							ParameterConst.DOCTYPE_LETTER_OF_INTENT,
							scLoi.getLoiId(), scLoi.getLoiNo());
				}
			}

			awardSupplierReqDTO.setPmDigiSign(digiSign);

			awardSupplierResDTO = getQtAwardSO()
					.saveLOIDtl(awardSupplierReqDTO);

			this.getManagedBean().setScLoi(awardSupplierResDTO.getScLoi());

			if (awardSupplierResDTO.isSuccessful()) {
				scLoi = awardSupplierResDTO.getScLoi();

				renewSesionObject();

				String messageCode = null;

				if (QTStatusConst.LOI_PENDING_APPROVAL.equals(statusId)) {
					if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700007;
					} else if (BpmOutcomeConst.BPM_REJECT
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700008;
					} else if (BpmOutcomeConst.BPM_REVISE
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700009;
					}
				} else if (QTStatusConst.LOI_PENDING_REVISION.equals(statusId)) {
					messageCode = MessageConst.INFO_700026;
				} else if (QTStatusConst.LOI_PENDING_SUPPLIER_ACKNOWLEDGEMENT
						.equals(statusId)) {
					if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700068;
					} else if (BpmOutcomeConst.BPM_REJECT
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700008;
					}
				} else if (QTStatusConst.LOI_PENDING_NEGOTIATION
						.equals(statusId)) {
					messageCode = MessageConst.INFO_700001;
				}
				//#21692 - start
				if (BpmOutcomeConst.BPM_EPV_CANCEL
						.equalsIgnoreCase(outcome)) {
					messageCode = MessageConst.INFO_700031;
				}
				//#21692 - end
				
				if (QTStatusConst.LOI_PENDING_SUPPLIER_ACKNOWLEDGEMENT
						.equals(statusId)) {
					 //#12487
					if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
						prepareLoiNotification();
					}
					supplierSuccessMessage(messageCode, scLoi.getLoiNo(), Boolean.TRUE);
				}
				else{
				successMessage(messageCode, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_LETTER_OF_INTENT,
						scLoi.getLoiNo(), Boolean.TRUE);
				}
			}
			/*#34444 Start*/
			return awardSupplierResDTO;
			/*#34444 End*/
		} finally {
			// iqtAwardSO = null;
		}
	}
	
	/**
	 *Supplier Success message.
	 * 
	 * @param messageCode
	 *            the message code           
	 * @param docNo
	 *            the doc no
	 * @param isAcknowledgeRequired
	 *            the is acknowledge required
	 */
	public void supplierSuccessMessage(String messageCode, 
			 String docNo, boolean isAcknowledgeRequired) {
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(
								messageCode,
								new String[] {
										getMessageCommon("lbl.sc.loi.supplier.response.success.info.message"),
										docNo },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

		if (isAcknowledgeRequired) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
		}
	}

	/**
	 * To get message common
	 * 
	 * @param name
	 * @return String instance
	 */
	public static String getMessageCommon(String name) {
		return ResourceBundleUtils.getMessage(name,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
	}

	/**
	 * Update bpm task action. update bpm - save data into database and BPM
	 * process will route to the next respective user. Authenticating gpki
	 * before update bpm.
	 * 
	 * @param outcome
	 *            the outcome
	 */
	public void updateBpmTaskAction(String outcome) {
		debugLogger("updateBpmTaskAction");
		this.getManagedBean().setAction(outcome);
		if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
			if (isDigiSigningRequired(RoleCode.RN_APPROVER.value())
					|| isDigiSigningRequired(RoleCode.PUBLICATION_APPROVER
							.value())) {
				this.getManagedBean().setAutoUpdate(Boolean.FALSE);
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			}
		} else if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
			getManagedBean().getSpkiBackingBean().getChallengeQuestions();
			RequestContext.getCurrentInstance().execute("spki_dlg.show();");
			RequestContext.getCurrentInstance().execute("blockWidget.hide();");
		} else {
			updateBPMTask(outcome, null);
		}
	}

	/**
	 * Prepare original text.
	 * 
	 * @param smSupplier
	 *            the sm supplier
	 * @return the string
	 */
	private String prepareOriginalText(SmSupplier smSupplier) {
		FacesScopes facesScopes = new FacesScopes();
		StringBuilder sb = new StringBuilder();
		String countryCode = "";
		String orginalText = "";

		if (smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses() != null
				&& smSupplier.getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses().size() > 0) {
			List<SmCompanyAddress> companyAddress = smSupplier.getSmAppl()
					.getSmCompanyBasic().getSmCompanyAddresses();

			for (SmCompanyAddress compaAddress : companyAddress) {
				if (compaAddress.getAddressType().equalsIgnoreCase(
						AddressType.CONTACT_ADDRESS.value())) {
					countryCode = compaAddress.getSmAddress().getPmCountry()
							.getCountryCode();
					break;
				}
			}
		}
		sb.append(getManagedBean().getScLoi().getLoiNo()).append("|")
				.append(facesScopes.getUserSessionBean().getIdentificationNo())
				.append("|").append(smSupplier.getRegNo()).append("|")
				.append(countryCode);
		orginalText = sb.toString();
		return orginalText;
	}

	/**
	 * The method used to verify the login user is supplier.
	 * 
	 * @return true, if login user is supplier false, if login user is not
	 *         supplier
	 */
	public boolean isSupplierRole() {
		return (getFacesScopes().hasRoleUser(
				RoleCode.BASIC_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(
						RoleCode.BASIC_SUPPLIER_USER.value())
				|| getFacesScopes().hasRoleUser(
						RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(
						RoleCode.MOF_SUPPLIER_USER.value()) || getFacesScopes()
				.hasRoleUser(RoleCode.GOVT_SELLER.value()));
	}

	/**
	 * This method used to retrieve login user information.
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	/**
	 * save multiple loi action. save - save data into database and initiate
	 * task for LOI details.
	 * 
	 * page navigate to acknowledge page.
	 */
	public void saveMultipleLoiAction() {
		debugLogger("saveMultipleLoiAction");
		
		try {
			ScQtFinalization scQtFinalization = getManagedBean()
					.getScQtFinalization();

			AwardSupplierReqDTO reqDTO = parseMultipleLoiDtls(false);

			AwardSupplierResDTO resDTO = getQtAwardSO().saveMultipleLOIDtl(
					reqDTO);

			if (resDTO.isSuccessful()) {
				renewSessionWithPreviousValues();
				populateMeetingPreparationTab(); // 34491
				retrieveMultipleLoiDtls(scQtFinalization.getScQt().getQtId(),
						getManagedBean().getCommitteeType(), getManagedBean()
								.getRecommendDtlIds());
				successMessage(MessageConst.INFO_700004, ParameterTypeConst.FINALIZATION_RECOMMEND_BY, ParameterConst.RECOMMEND_BY_MULTIPLE_LOI, 
						scQtFinalization.getScQt().getQtNo(), Boolean.FALSE);
			}

		} finally {
			// qtAwardSO = null;
		}
	}

	// #25638 - Start.
	/**
	 *  To Revise.
	 */
	public void reviseAction() {
        reviseSignOff();
		updateMultiLoiBpmTask(BpmOutcomeConst.BPM_REVISE, null);
	}
	
	private void reviseSignOff() {
	  if (StringUtils.isNotBlank(
          getManagedBean().getCommitteeType())) {
        for (ScQtCommitteeActivity scQtCommitteeActivity 
                            : getManagedBean().getFcActivityList()) {
          scQtCommitteeActivity.setMultiLoiSignedOff(false);
        }
	  }
	}
	// #25638 - End.
	
	/**
	 * Update multi loi bpm task action. update bpm - save data into database
	 * and BPM process will route to the next respective user. Authenticating
	 * gpki before update bpm.
	 * 
	 * @param outcome
	 *            the outcome
	 */
	public void updateMultiLoiBpmTaskAction(String outcome) {
		debugLogger("updateMultiLoiBpmTaskAction");
		
		//25638 - Start
		if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)) {
		    boolean remarksNotFound = true; 
	        if (!CollectionUtils.isEmpty(getManagedBean().getScQtCommittee().getQtCommitteRemarks())) {
	            for (ScQtCommitteRemarks scQtRemark : getManagedBean().getScQtCommittee().getQtCommitteRemarks()) {
	                if (null != scQtRemark.getCreatedUser() && null != scQtRemark.getCreatedUser().getUserId()) {
	                  if (scQtRemark.getCreatedUser().getUserId().longValue() == new FacesScopes().getUserSessionBean().getUserId().longValue()) {
	                    if (StringUtils.isEmpty(scQtRemark.getRemark()) ) {
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
	            throw new EpException(MessageConst.ERR_775152);
	        }
	        /*#34444 - Start*/
	        reviseAction();
	        /*#34444 - End*/
		}
		//25638 - End
		//#21692 - Start
		if (outcome.equals(BpmOutcomeConst.BPM_ENDORSE)|| outcome.equals(BpmOutcomeConst.BPM_SIGNOFF)) {
			checkSupplierAwardedRejectValidityExtension();//#21692
		}
		//#21692 - End
		
		// #28777 & #29729 - Start
		if(checkFlagForSupplierBranchDeleted(outcome)) {
			checkNeedSupplierIfHaveBranchDeleted(outcome);
		}
		// #28777 & #29729 - End
				
		this.getManagedBean().setAction(outcome);
		String committeeType = getManagedBean().getCommitteeType();
		CommitteResDTO committeeResDTO = getCommitteMemberByUserId();
		ScCommitteeMember committeeMember = committeeResDTO.getCommMember();
		
		if (StringUtils.isNotBlank(committeeType) && 
				ParameterConst.COMMITTEE_ROLE_CHAIRPERSON.equalsIgnoreCase(committeeMember.getMemberRole().getParameterCode()) &&
				isDigiSigningRequired(committeeResDTO.getRoleCode(),this.getManagedBean().getFcActivityList())) {
			this.getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
		} else {
			signOff();
			updateMultiLoiBpmTask(outcome, null);
		}
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
		committeReqDTO.setPmUser(curLogedInUser);
		committeReqDTO.setCommitteeType(committeeType);
		
		return committeSO.retrieveCommitteMemberByUserId(
				committeReqDTO);
	}
	
	// #28777 & #29729 - Start
	/**
	 * Method to set flag if have supplier branch deleted
	 **/
	private void setFlagForSupplierBranchDeleted() {

		ScQt scqt = this.getManagedBean().getScQt();
		ScQtFinalization scQtFinalization = this.getManagedBean().getScQtFinalization();
		ScWorkFlowStatus currentStatus = scQtFinalization.getCurrentStatus();
		boolean statusRevision = currentStatus != null
				&& QTStatusConst.SF_PENDING_MULTIPLE_LOI_RECOMMENDATION_REVISION.equals(currentStatus.getPmStatus()
						.getStatusId());
		List<ScQtRecommend> recommendedItems = scQtFinalization.getScQtRecommends();

		if (statusRevision && CollectionUtils.isNotEmpty(recommendedItems)) {
			setDetailForSupplierBranchDeleted(recommendedItems, scqt);
		}
	}

	// #28777 & #29729 - End
	
	// #28777 & #29729 - Start
	/**
	 * Method to check details if have supplier branch deleted
	 * 
	 * @param recommendedItems
	 * @param scQt
	 * 
	 **/
	private void setDetailForSupplierBranchDeleted(List<ScQtRecommend> recommendedItems, ScQt scqt) {
		for (ScQtRecommend recommend : recommendedItems) {
			TplSpecZoneDtl tplSpecZoneDtl = recommend.getTplSpecZoneDtl();
			if (tplSpecZoneDtl != null) {
				Long specZoneId = tplSpecZoneDtl.getSpecZoneId();
				if (checkListZonesDisqualifiedSupplierBranchDeleted(scqt.getQtId(), specZoneId)) {
					this.getManagedBean().setFlagBranchDeleted(true);
				}
			} else {
				if (checkListDisqualifiedSupplierBranchDeleted(scqt.getQtId())) {
					this.getManagedBean().setFlagBranchDeleted(true);
				}
			}
		}
	}

	// #28777 & #29729 - End

	/**
	 * Update multi loi bpm task.
	 * 
	 * @param outcome
	 *            the outcome
	 * @param digiSign
	 *            the digi sign
	 */
	private void updateMultiLoiBpmTask(String outcome, PmDigiSign digiSign) {
		debugLogger("updateMultiLoiBpmTask");
		
		try {
			ScQtFinalization scQtFinalization = getManagedBean()
					.getScQtFinalization();
			SCEvaluationDataType evaluationDataType = (SCEvaluationDataType) getManagedBean()
					.getPayLoad();
			validateMeetingPreparation(outcome);// 34491
			setFlagForSupplierBranchDeleted();//#28777&#29729

			// 26247 - start
			boolean isSubmit = !BpmOutcomeConst.BPM_REVISE.equals(outcome);
			AwardSupplierReqDTO reqDTO = parseMultipleLoiDtls(isSubmit);
			// 26247 - end
			
			reqDTO.setSourceId(getManagedBean().getSourceId());
			reqDTO.setOutcome(outcome);
			if (null != digiSign) {
				signOff();
				updatePmDigiSignData(digiSign,
						ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION, scQtFinalization
								.getScQt().getQtId(), scQtFinalization
								.getScQt().getQtNo());
			}

			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setPayLoad(evaluationDataType);
			reqDTO.setDigiSignData(digiSign);
			
			// #25638 - Start. Remove so that PmUser can get current user data
//			if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)) { 
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
//			}
			// #25638 - End.
			
			reqDTO.setStatusId(getManagedBean().getCurrentStatusId());
			
			//29561 - start
			reqDTO.setBaseURL(SmCommonUtil.getBasePortalUrl());
			reqDTO.setTaskName(this.getManagedBean().getTaskName());
			//29561 - end

			AwardSupplierResDTO resDTO = getQtAwardSO().saveMultipleLOIDtl(
					reqDTO);

			if (resDTO.isSuccessful()) {
				super.renewSesionObject();
				// #25638 - Start.
				if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
					successMessage(MessageConst.INFO_776042, getMessageCommon("lbl.sc.quotation.tender"), scQtFinalization
                        .getScQt().getQtNo(), Boolean.TRUE);
				} else {
					successMessage(MessageConst.INFO_700033,
							ParameterTypeConst.FINALIZATION_RECOMMEND_BY,
							ParameterConst.RECOMMEND_BY_MULTIPLE_LOI, scQtFinalization
							.getScQt().getQtNo(), Boolean.TRUE);
				}
				// #25638 - End.
			}

		}
		// 26247 - start
		catch (EpException e) {
			String taskName = this.getManagedBean().getTaskName();
			if (Arrays.asList(SCProcessNameConst.ENDORSE_FC_SELECTION, SCProcessNameConst.SIGNOFF_FC_SELECTION)
					.contains(taskName)) {
				Locale userLocale = new FacesScopes().getUserLocale();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
								MessageConst.ERROR_760601, null, MessageUtils.BUNDLE_COMMON_MESSAGES, userLocale)));
				this.enableAutoUpdate();
			} else {
				throw e;
			}
		}
		// 26247 - end
	}
	
	/**
	 * Action performed when committee members endorsed the Supplier
	 */
	public void signOff() {
		if (StringUtils.isNotBlank(
				getManagedBean().getCommitteeType())) {
			for (ScQtCommitteeActivity scQtCommitteeActivity 
								: getManagedBean().getFcActivityList()) {
				if (scQtCommitteeActivity.getScCommitteeMember().getPmUser().getUserId()
						.equals(getFacesScopes().getUserSessionBean().getUserId())) {
					scQtCommitteeActivity.setMultiLoiSignedOff(true);
					break;
				}
			}
		}
	}
	
	// #28777 & #29729 - Start
	/**
	 * Method to display validation revision for supplier branch deleted
	 * 
	 * @param specZone
	 * @param scQtRecommend
	 * @param scQtEvaluationResultDtl
	 * 
	 **/
	private void validationRevisionSupplierBranchDeleted(TplSpecZoneDtl specZone, ScQtRecommend scQtRecommend,
			ScQtEvaluationResultDtl scQtEvaluationResultDtl) {
		boolean itemized = this.getManagedBean().isItemized();
		if (this.getManagedBean().isFlagBranchDeleted()) {
			if (itemized) {
				validationRevisionItemized(specZone, scQtRecommend);
			} else {
				validationRevisionPackage(specZone, scQtEvaluationResultDtl);
			}
		}
	}
	
	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to display validation revision for itemized
	 * 
	 * @param specZone
	 * @param scQtRecommend
	 * 
	 **/
	private void validationRevisionItemized(TplSpecZoneDtl specZone, ScQtRecommend scQtRecommend) {
		if (specZone != null) {
			throw new EpException(MessageConst.ERROR_760628, new String[] { specZone.getZoneName(),
					scQtRecommend.getRequestItem().getItemName() });
		} else {
			throw new EpException(MessageConst.ERROR_760629, new String[] { scQtRecommend.getRequestItem()
					.getItemName() });
		}
	}
	
	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to display validation revision for package
	 * 
	 * @param specZone
	 * @param scQtEvaluationResultDtl
	 * 
	 **/
	private void validationRevisionPackage(TplSpecZoneDtl specZone, ScQtEvaluationResultDtl scQtEvaluationResultDtl) {
		if (specZone != null) {
			throw new EpException(MessageConst.ERROR_760626, new String[] { specZone.getZoneName() });
		} else {
			if (scQtEvaluationResultDtl == null) {
				throw new EpException(MessageConst.ERROR_760627);
			}
		}
	}
	// #28777 & #29729 - End

	/**
	 * Parses the multiple loi dtls. prepare mulitple loi details to save or
	 * submit into database.
	 * 
	 * @return the award supplier req dto
	 */
	private AwardSupplierReqDTO parseMultipleLoiDtls(boolean isSubmit) {
		Long statusId  = getManagedBean().getCurrentStatusId();
		// 26247 - start
		if (isSubmit) {
			List<TplSpecZoneDtl> zoneDeDtls = getManagedBean().getMultiLOIZoneDetails();
			if (getManagedBean().isItemized()) {
				if (getManagedBean().getScQt().isZonal() && 
						CollectionUtils.isNotEmpty(zoneDeDtls)) {
					Map<Long,List<ScQtRecommend>> scQtRecommendsByZone 
										= getManagedBean().getScQtRecommendsByZone();
					for (TplSpecZoneDtl zone : zoneDeDtls) {
						validateAwardOfSuppliersByItem(isSubmit, zone,
								scQtRecommendsByZone.get(zone.getSpecZoneId()));
					}
				} else {
					validateAwardOfSuppliersByItem(isSubmit, null,
							getManagedBean().getRecommendList());
				}
			} else {
				if (getManagedBean().getScQt().isZonal() && 
							CollectionUtils.isNotEmpty(zoneDeDtls)) {
					Map<Long,List<ScQtRecommend>> scQtRecommendsByZone 
										= getManagedBean().getScQtRecommendsByZone();
					for (TplSpecZoneDtl zone : zoneDeDtls) {
						validateAwardOfSuppliersByPKG(isSubmit, zone,
								scQtRecommendsByZone.get(zone.getSpecZoneId())
								.get(0));
					}
				} else {
					
					validationRevisionSupplierBranchDeleted(null, null, this.getManagedBean().getSelectedSupplier()); //#28777&#29729
					
					if (this.getManagedBean().getSelectedSupplier() == null) {
						throw new EpException(MessageConst.ERROR_760293);
					}
					awardSelectedSupplierAction();//34905
				}				
			}
			
			ScLoi scLoi = this.getManagedBean().getScLoi();
			if (CollectionUtils.isNotEmpty(
						scLoi.getScLoiReports())) {
				for (ScLoiReport report 
							: scLoi.getScLoiReports()) {
					if (report.getScLoiReportAttachment() == null && 
							ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
								.equals(report.getDefinationType().getParameterCode())) {
						tabMenuListener(3);
						throw new EpException(MessageConst.ERROR_760415, 
											  new String[] { report.getTitle() });
					}
				}
			}
		}
		// 26247 - end
	
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();

		reqDTO.setScLoi(getManagedBean().getRecommendedLoi());
		reqDTO.setScQtFinalization(scQtFinalization);
		reqDTO.setScLois(getManagedBean().getLois());
		reqDTO.setScLoiReports(getManagedBean()
								.getRecommendedLoi().getScLoiReports());
		reqDTO.setDeletedScLoiReports(getManagedBean()
								.getDeletedScLoiReports());

		// Save EC/TEC/FEC activities details.
		List<ScQtCommitteeActivity> activityList = new ArrayList<ScQtCommitteeActivity>();
		if (getManagedBean().getEcActivityList() != null) {
			for (ScQtCommitteeActivity committeeActivity : getManagedBean()
					.getEcActivityList()) {
				activityList.add(committeeActivity);
			}
		}
		if (getManagedBean().getTecActivityList() != null) {
			for (ScQtCommitteeActivity committeeActivity : getManagedBean()
					.getTecActivityList()) {
				activityList.add(committeeActivity);
			}
		}
		if (getManagedBean().getFecActivityList() != null) {
			for (ScQtCommitteeActivity committeeActivity : getManagedBean()
					.getFecActivityList()) {
				activityList.add(committeeActivity);
			}
		}
		reqDTO.setEcActivityList(activityList);
		
		if (QTStatusConst.SF_PENDING_MULTIPLE_LOI_RECOMMENDATION
				.equals(statusId)) {
			boolean multiLoiAttended = false;

			for(ScQtCommitteeActivity committee : activityList){
				if(committee.isMultiLoiAttended() 
						|| CollectionUtils.isNotEmpty(this.getManagedBean().getSelectedMeetingAttList())){
					multiLoiAttended = true;
				}
			}

			if (getManagedBean().getScQtCommittee() != null) {// #27502, Adding Condition only - Bug #36187
				for (ScQtMeetingAttendance meetingAttendance : getManagedBean().getScQtCommittee()
						.getMeetingAttendances()) {
					if (meetingAttendance.isSelected() || meetingAttendance != null) {
						multiLoiAttended = true;
					}
				}
			}

			if(!multiLoiAttended && isSubmit){// 34491
				throw new EpException(MessageConst.ERROR_760462);
			}
		}

//		boolean itemized = this.getManagedBean().isItemized();
//		ScQtEvaluationResultDtl awardedSupplier = this.getManagedBean()
//				.getSelectedSupplier();
//		if (!itemized && awardedSupplier != null) {
//			awardedSupplier.setAwareded(true);
//			reqDTO.setAwardedSupplier(awardedSupplier);
//		}

		ScQtCommittee scQtCommittee = getManagedBean().getScQtCommittee();
		if (scQtCommittee != null) {
			scQtCommittee.setQtCommitteRemarks(finalizeRemarks(scQtCommittee
					.getQtCommitteRemarks()));
			reqDTO.setScQtCommittee(scQtCommittee);
		}
		
		List<ScQtRecommend> scQtRecommends = this.getManagedBean().getRecommendList();
		if (getManagedBean().isZonal()) {
			scQtRecommends = new ArrayList<ScQtRecommend>();
			Map<Long,List<ScQtRecommend>> qtRecommendsByZone 
						= getManagedBean().getScQtRecommendsByZone();
			for (Long specZoneId : qtRecommendsByZone.keySet()) {
				scQtRecommends.addAll(qtRecommendsByZone.get(specZoneId));
			}
		}
		reqDTO.setScQtRecommendList(scQtRecommends);
		reqDTO.setItemized(this.getManagedBean().isItemized());
		reqDTO.setDeletedMeetingAttendanceList(
				getManagedBean().getDeletedMeetingAttendanceList());

		// 34491 - start
		reqDTO.setDeletedCommitteeActivities(this.getManagedBean().getDeletedCommitteeActivities());
		reqDTO.setDeletedCommitteeMembers(this.getManagedBean().getDeletedCommitteeMembers());
		reqDTO.setDeletedQtMeetings(this.getManagedBean().getDeletedQtMeetings());
		reqDTO.setDeletedQtMeetingAttendances(this.getManagedBean().getDeletedQtMeetingAttendances());
		reqDTO.setQtApproval(this.getManagedBean().getCommitteeApproval());
		ScQtCommittee qtCommittee = this.getManagedBean().getFinalizationQtCommittee();
		finalizeRemarks(qtCommittee.getQtCommitteRemarks());
		finalizeRemarks(reqDTO.getQtApproval().getScQtApprovalRemarks());
		reqDTO.setCommitteeModified(this.getManagedBean().isCommitteModified());
		reqDTO.setFinalizationQtCommittee(qtCommittee);
		// 34491 - end
		return reqDTO;
	}
	
	private void validateAwardOfSuppliersByPKG(boolean isSubmit,
			TplSpecZoneDtl specZone, ScQtRecommend recommendedItem) {
		if (CollectionUtils.isNotEmpty(
				recommendedItem.getRecommendDtls()) &&
					isSubmit) {
			int rejectedSupplierCount = 0;
			int recommendedSupplierCount = 0;
			for (ScQtRecommendDtl recommendDtl 
						: recommendedItem.getRecommendDtls()) {
				if (BooleanUtils.isTrue(
						recommendDtl.getLoiAwarded()) && 
							recommendDtl.isRecordStatus() && recommendDtl.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE) {//#28777&#29729
					++recommendedSupplierCount;
					break;
				} else if (BooleanUtils.isFalse(
								recommendDtl.getLoiAwarded()) || 
						   (recommendDtl.getLoaAwarded() != null && 
								   !recommendDtl.getLoaAwarded()) ) {
					++rejectedSupplierCount;
				}
			}
			if (rejectedSupplierCount != 
					recommendedItem.getRecommendDtls().size() && 
					recommendedSupplierCount == 0) {
				this.getManagedBean().setSelectedTabIndex(4);// 34491 //26247 - fix validation sent to wrong tab
				if (specZone != null) {
					this.getManagedBean().setSelectedZone(specZone);
					this.getManagedBean().setZoneActiveIndex(
							specZone.getZoneTabIndex());
				}
				
				validationRevisionSupplierBranchDeleted(specZone, null, null); //#28777&#29729
				
				throw new EpException(MessageConst.ERROR_760293);
			}
		}
	}	
	
	private void validateAwardOfSuppliersByItem(boolean isSubmit, TplSpecZoneDtl specZone,
										  List<ScQtRecommend> recommendedItemList) {
		for (ScQtRecommend scQtRecommend 
								: recommendedItemList) {
			if (scQtRecommend.getActiveSupplierCount() > 1) {
				validateAwardQuantity(scQtRecommend);
			}
			if (CollectionUtils.isNotEmpty(
					scQtRecommend.getRecommendDtls()) && 
						isSubmit) {
				int rejectedSupplierCount = 0;
				int recommendedSupplierCount = 0;
				for (ScQtRecommendDtl recommendDtl 
							: scQtRecommend.getRecommendDtls()) {
					if (BooleanUtils.isTrue(
							recommendDtl.getLoiAwarded()) &&
								recommendDtl.isRecordStatus() && recommendDtl.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE) {//#28777&#29729
						++recommendedSupplierCount;
					} else if (BooleanUtils.isFalse(
									recommendDtl.getLoiAwarded()) || 
							   (recommendDtl.getLoaAwarded() != null && 
									   !recommendDtl.getLoaAwarded())) {
						++rejectedSupplierCount;
					}
				}
				if (rejectedSupplierCount != 
						scQtRecommend.getRecommendDtls().size() &&
							recommendedSupplierCount == 0) {
					this.getManagedBean().setSelectedTabIndex(4);// 34491 //26247 - fix validation sent to wrong tab
					if (specZone != null) {
						this.getManagedBean().setSelectedZone(specZone);
						this.getManagedBean().setZoneActiveIndex(
											specZone.getZoneTabIndex());
					}
					
					validationRevisionSupplierBranchDeleted(specZone, scQtRecommend, null); //#28777&#29729
					
					if (!this.getManagedBean().isItemized()) {
						throw new EpException(MessageConst.ERROR_760317);
					} else {
						throw new EpException(MessageConst.ERROR_760318, 
								new String[] {scQtRecommend.getRequestItem().getItemName()});
					}
				}
			}
		}		
	}
	
	private void validateAwardQuantity(ScQtRecommend scQtRecommend) {
		BigDecimal totalQuantity = scQtRecommend.getOrderQty();
		BigDecimal tempQuantity = new BigDecimal(0.0);
		for (ScQtRecommendDtl recommendDtl : scQtRecommend.getRecommendDtls()) {
			if (BooleanUtils.isTrue(
					recommendDtl.getLoiAwarded()) && 
					recommendDtl.getRecordStatusFlg().equals(
								DbValueConst.RECORD_STATUS_ACTIVE)) {
				BigDecimal quantityAwarded = recommendDtl.getAwardQty();
				if (quantityAwarded == null
						|| quantityAwarded.compareTo(totalQuantity) >= 0) {
					this.getManagedBean().setSelectedTabIndex(3);// 34491
					throw new EpException(MessageConst.ERROR_760214);
				}
				tempQuantity = tempQuantity.add(quantityAwarded);
			}
		}
		if (totalQuantity.compareTo(tempQuantity) != 0) {
			this.getManagedBean().setSelectedTabIndex(3);// 34491
			throw new EpException(MessageConst.ERROR_760214);
		}		
	}

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		StringBuilder msg = new StringBuilder(message.toString());
		/*if (getManagedBean().getScQt() != null) {
			msg.append(" qtId:" + getManagedBean().getScQt().getQtId());
		}*/
		if (log.isDebugEnabled()) {
			log.debug(msg);
		}		
		log.info(msg);
	}

	/**
	 * Gets the loi letter stream content. to preview loi content as a pdf and
	 * can download.
	 * 
	 * @return the loi letter stream content
	 * @throws Exception
	 *             the exception
	 */
	public StreamedContent getLoiLetterStreamContent() throws Exception {
		ScLoi scLoi = getManagedBean().getScLoi();
		ScLoiAttachment attachment = getLoiAttachment(scLoi);
		attachment.setDocNo(scLoi.getLoiNo());
		generateLoiLetterFromVelocityToPdf(scLoi, attachment);
		StreamedContent pbStreamContent = getAttachmentStreamContent(
				attachment, CommonConstants.FILE_CONTENT_TYPE_PDF);
		return pbStreamContent;
	}

	/**
	 * Preview letter. to preview loi cover letter as a pdf and can download.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public StreamedContent previewLetter() throws Exception {
		ScLoi scLoi = getManagedBean().getScLoi();
		ScLoiAttachment attachment = getLoiAttachment(scLoi);
		attachment.setDocNo(scLoi.getLoiNo());
		generateLoiLetterFromVelocityToPdf(scLoi,attachment);
		StreamedContent pbStreamContent = getAttachmentStreamContent(
				attachment, CommonConstants.FILE_CONTENT_TYPE_PDF);
		RequestContext.getCurrentInstance().execute("loiLetterDlg.show();");
		return pbStreamContent;
	}

	/**
	 * Adds the meeting attendance.
	 */
	public void addMeetingAttendance() {
		debugLogger("..In addMeetingAttendance()..");

		try {
			ScQtCommittee scQtCommittee = getManagedBean().getScQtCommittee();
			List<ScQtMeetingAttendance> meetingAttendanceList = scQtCommittee
					.getMeetingAttendances();

			if (null == meetingAttendanceList) {
				meetingAttendanceList = new ArrayList<ScQtMeetingAttendance>();
				scQtCommittee.setMeetingAttendances(meetingAttendanceList);
			}

			ScQtMeetingAttendance meeting = new ScQtMeetingAttendance();
			meeting.setQtCommittee(scQtCommittee);
			setUserDetails(meeting);
			meetingAttendanceList.add(meeting);

		} finally {

		}
	}

	/**
	 * Delete meeting attendance.
	 */
	public void deleteMeetingAttendance() {
		ScQtCommittee scQtCommittee = getManagedBean().getScQtCommittee();
		List<ScQtMeetingAttendance> selectedMeetingAttendances = getManagedBean()
				.getSelectedMeetingAttList();
		if (CollectionUtils.isNotEmpty(
				selectedMeetingAttendances)) {
			deleteRowsAction(scQtCommittee.getMeetingAttendances(),
					selectedMeetingAttendances);
			if (getManagedBean().getDeletedMeetingAttendanceList() == null) {
				getManagedBean().setDeletedMeetingAttendanceList(
							new ArrayList<ScQtMeetingAttendance>());
			}
			for (ScQtMeetingAttendance meetingAttd
							: getManagedBean().getSelectedMeetingAttList()) {
				if (!meetingAttd.isBrandNew()) {
					meetingAttd.setDeleted(true);
					getManagedBean().getDeletedMeetingAttendanceList()
									.add(meetingAttd);
				}
			}
		} else {
			throw new EpException("Please select atleast one record");
		}
		getManagedBean()
		.setSelectedMeetingAttList(null);
	}

	/**
	 * Generate loi letter from velocity to pdf.
	 * 
	 * @param scLoi
	 *            the sc loi
	 * @param attachment
	 *            the attachment
	 * @throws Exception
	 *             the exception
	 */
	private void generateLoiLetterFromVelocityToPdf(ScLoi scLoi,
			ScAttachment attachment){
		attachment.setFileName("Loi Letter.pdf");
		try {
			populateAttachmentDetails(attachment);
			Locale myLocale = new Locale(CommonConstants.LANGUAGE_CODE_MALAY, CommonConstants.DEFAULT_COUNTRY_CODE);
			ScLoiLoa scLoiLoa = scLoi.getScLoiLoa();
			SmSupplier smSupplier = scLoiLoa.getSmSupplier();
			Long supplierId = smSupplier.getSupplierId();
			Long statusId = getManagedBean().getCurrentStatusId();
			CommonUtils commonUtils = new CommonUtils();
			SimpleDateFormat timeSDF = new SimpleDateFormat(
					commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_TIME));
			SimpleDateFormat dateSDF = new SimpleDateFormat(
					commonUtils
					.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
			DecimalFormat numberDF = new DecimalFormat(
					commonUtils
					.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_INTEGER));
			DecimalFormat amountDF = new DecimalFormat(
					commonUtils
					.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
			VelocityContext context = new VelocityContext();

			FacesScopes fs = new FacesScopes();
			String hostUrl = fs.getRequest().getServerName() + ":"
					+ fs.getRequest().getServerPort();
			String userName = fs.getUserSessionBean().getUserName();
			context.put("hostUrl", hostUrl);
			context.put("userName", userName);
			
			Date newDate = new Date();
			String date = "";
			if (newDate != null) {
				date = dateSDF.format(newDate);
			}
			String method = "";
			String category = "";
			ScQt scQt = getManagedBean().getScQt();
			PmParameter procurementMode = scQt.getProcurementMode();
			PmParameter procurementType = scQt.getProcurementTypeCatId();
			if(procurementMode != null 
					&& procurementMode.getParameterCode().equals(ParameterConst.PROCUREMENT_MODE_TENDER)){
				method = "Tender";
			}
			else{
				method = "Sebut Harga";
			}
			if(procurementType != null 
					&& procurementType.getParameterCode().equals(ParameterConst.PROCUREMENT_TYPE_CODE_SERVICE)){
				category = "Perkhidmatan";
			}
			else{
				category = "Bekalan";
			}
			context.put("method", method);
			context.put("category", category);
			
			String issueDate = "______________";
			if (scLoi.getIssueDate() != null) {
				issueDate = getLocalizeDate(scLoi.getIssueDate(), myLocale);
			}
			context.put("issueDate", issueDate);
			context.put("date", date);
			context.put("issueDate", scLoi.getIssueDate());
			context.put("timeSDF", timeSDF);
			context.put("dateSDF", dateSDF);
			context.put("numberDF", numberDF);
			context.put("amountDF", amountDF);

			context.put("fileNo", scLoiLoa.getFileNo());
			
			context.put("qtNo", getManagedBean().getScQt().getQtNo());
			
			SmMofAccount mofAccount = getMofAccountBySupplierId(supplierId);
			if(mofAccount != null){
				context.put("mofNo", mofAccount.getMofNo());
			}
			
			SupplierGstResDTO resDTO = getGSTInfo(supplierId, null);
			if(resDTO.getSmGstSupplier() != null){
				SmGstSupplier gstSupplier = resDTO.getSmGstSupplier();
				context.put("gstNo", gstSupplier.getGstRegNo());
			}
			else{
				context.put("gstNo", "");
			}
			
			String fileExtnNo = scLoi.getFileExtnNo();
			if (fileExtnNo == null) {
				fileExtnNo = "&lt;&lt;3-digits&gt;&gt;";
			}
			context.put("fileExtnNo", fileExtnNo);

			context.put("loiNo", scLoi.getLoiNo());

			String supplierName = scLoiLoa.getSupplierName();
			//supplierName = supplierName.replaceAll("&", "&#38;");
			context.put("supplierName", supplierName);

			String supplierAddress = null;
			if (scLoi.getScSupplierAddress() != null) {
				supplierAddress = getFullAddress(scLoi.getScSupplierAddress());
			} else {
				supplierAddress = "&lt;&lt;Address&gt;&gt;";
			}
			debugLogger("supplierAddress" + supplierAddress);
			//context.put("supplierAddress", supplierAddress);
			StringBuilder supplierAddressTable = null;
			if(StringUtils.isNotBlank(supplierAddress)){
				String[] address = supplierAddress.split(System.getProperty("line.separator"));
				supplierAddressTable = new StringBuilder();
				for (String suppAddr : address) {
					supplierAddressTable.append(suppAddr);
					supplierAddressTable.append("<br />");
				}
			}
			debugLogger("SupplierAddressTable" + supplierAddressTable);
			context.put("supplierAddressTable", supplierAddressTable);

			String title = scLoiLoa.getTitle();
			if (title == null) {
				title = "";
			}

			//replace all special characters
			//title = title.replaceAll("\\W", ""); 
			title=title.replaceAll("\r\n", "<br />");
			/*title=title.replaceAll("&", "&#38;");
			title=title.replaceAll("<", "&#60;");
			title=title.replaceAll(">", "&#62;");*/
			context.put("title", title);

			boolean isZonal = Boolean.FALSE;
			List<ScLoiLoaItem> loiloaItems = new ArrayList<ScLoiLoaItem>();
			if (scLoi.getScLoiLoaItems() != null
					&& scLoi.getScLoiLoaItems().size() > 0) {
				loiloaItems = scLoi.getScLoiLoaItems();

				for (ScLoiLoaItem scLoiLoaItem : loiloaItems) {
					// zonal
					if(scLoiLoaItem.getTplSpecZoneDtl() != null){
						isZonal = Boolean.TRUE;
					}

					if (scLoiLoaItem.getScRequestItem().getItemName() != null
							&& !scLoiLoaItem.getScRequestItem().getItemName()
							.isEmpty()) {
						String itemName = scLoiLoaItem.getScRequestItem()
								.getItemName();
						itemName = itemName.replaceAll(" & ", "&#38;");
						scLoiLoaItem.getScRequestItem().setItemName(itemName);
					}

					if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion() != null
							&& scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
							.getTplSpecMaster() != null) {
						BigDecimal bigDecimal = BigDecimal.valueOf(0d);
						if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
								.getClUomPerDay() == null) {
							scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
							.setClUomPerDay(bigDecimal);
						}

						if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
								.getClUomFrequency() == null) {
							scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
							.setClUomFrequency(bigDecimal);
						}

						if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
								.getClUomPerMonth() == null) {
							scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
							.setClUomPerMonth(bigDecimal);
						}

						if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
								.getClDurationMonth() == null) {
							scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
							.setClDurationMonth(bigDecimal);
						}
						if (scLoiLoaItem.getSubTotal() == null) {
							scLoiLoaItem.setSubTotal(bigDecimal);
						}
					}
				}
				context.put("scLoiLoaItems", loiloaItems);
			}

			context.put("isZonal", isZonal);
			context.put("scLoi", scLoi);
			if (scLoi.getScNegoLocations() != null
					&& scLoi.getScNegoLocations().size() > 0) {
				context.put("scNegoLocations", scLoi.getScNegoLocations());
			} else {
				context.put("scNegoLocations", "null");
			}
			
			
			List<ScLoiTerm> selectedNegoTerms = new ArrayList<ScLoiTerm>();
			if (scLoi.getScNegoTerms() != null && scLoi.getScNegoTerms().size() > 0) {
				for (ScLoiTerm scLoiTerm : scLoi.getScNegoTerms()) {
					if (scLoiTerm.isSelectedTerm()) {
						selectedNegoTerms.add(scLoiTerm);
					}
				}
			}

			String Keputusan = "&lt;&lt;Keputusan&gt;&gt;";
			if (selectedNegoTerms != null && selectedNegoTerms.size() > 0) {
				context.put("scNegoTerms", selectedNegoTerms);
			} else if (scLoi.getScNegoTerms() != null
					&& scLoi.getScNegoTerms().size() > 0) {
				context.put("scNegoTerms", null);//#38142
				context.put("Keputusan", Keputusan);
			}

			String ackDueDays = "5";
			if (scLoi.getIssueDate() != null && scLoi.getAckDueDate() != null) {
				ackDueDays = DateUtils.getDaysDifference(scLoi.getIssueDate(),
						scLoi.getAckDueDate()) + "";
			}
			context.put("ackDueDays", ackDueDays);

			String loiSigner = "";
			if (getManagedBean().getContractSignerUser() != null) {
				if (null != getManagedBean().getContractSignerUser().getUserName() && !getManagedBean().getContractSignerUser().getUserName().isEmpty()) {
					loiSigner =  getManagedBean().getContractSignerUser().getUserName();
				} 
			}
			context.put("loiSigner", loiSigner);
			
			// #30447 start
			String salutation = "";
			PmSalutation salutationCode = getManagedBean().getContractSignerUser().getSalutationCode() != null ? 
					getManagedBean().getContractSignerUser().getSalutationCode() : null;
			if (getManagedBean().getContractSignerUser() != null && salutationCode != null) {
				salutation = getManagedBean().getContractSignerUser().getSalutationCode().getSalutationName();
				if (!loiSigner.isEmpty() && loiSigner.equals(loiSigner.toUpperCase())) {
				    salutation = salutation.toUpperCase();
				}
			}
			context.put("salutation", salutation);
			// #30447 end

			String loiSignerDesignation = "";
			if(null != scLoi.getLoiSignerDesig() && !scLoi.getLoiSignerDesig().isEmpty()){
				loiSignerDesignation = scLoi.getLoiSignerDesig();
			}
			context.put("loiSignerDesignation", loiSignerDesignation);

			context.put("isReFinalizationBidding", true);
			
			boolean disclaimer;
			if(QTStatusConst.LOI_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)){
				disclaimer = true;
			}
			else{
				disclaimer = false;
			}
			context.put("statusId", statusId);
			context.put("hasDisclaimer", disclaimer);
				

			// online bidding
			boolean isBiddingAward = Boolean.FALSE;
			if (scLoi.getRecommendBy() != null) {
				PmParameter recommendedBy = scLoi.getRecommendBy();
				if (ParameterConst.RECOMMEND_BY_ONLINE_BIDDING
						.equals(recommendedBy.getParameterCode())) {
					isBiddingAward = Boolean.TRUE;
				}
			}
			context.put("isBiddingAward", isBiddingAward);

			FileOutputStream fos = null;
			
			try {
				fos = new FileOutputStream(getActualFilePath(attachment));
				DocumentGenerator.getInstance().generatePdfFromVelocity(
						SourcingConstants.LOI_LETTER, context, 
						fos);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (fos != null) {
						fos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the mof account by supplier id.
	 *
	 * @param supplierId the supplier id
	 * @return the mof account by supplier id
	 */
	public SmMofAccount getMofAccountBySupplierId(Long supplierId) {
		IMofAccountSO mofAccountSO = (IMofAccountSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IMofAccountSO.class);
		MofAccountReqDTO accountReqDTO = new MofAccountReqDTO();
		accountReqDTO.setSupplierId(supplierId);
		MofAccountResDTO mofAccountResDTO = mofAccountSO
				.retrieveMofNoBySupplierId(accountReqDTO);
		return mofAccountResDTO.getMofAccount();
	}

	private SupplierGstResDTO getGSTInfo(Long supplierId, Long branchCode) {

		ISupplierGstSO supplierGstSO = null;
		try {
			supplierGstSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierGstSO.class);
			SupplierGstReqDTO gstReqDTO = new SupplierGstReqDTO();
			gstReqDTO.setSupplierId(supplierId);
			gstReqDTO.setBranchCode(branchCode);
			SupplierGstResDTO gstResDTO = supplierGstSO
					.retrieveGstInfoBySupplierIdAndBranchCode(gstReqDTO);
			return gstResDTO;
		} finally {
			supplierGstSO = null;
		}
	}

	/**
	 * Parses the loi details.
	 * 
	 * @param scLoi
	 *            the sc loi
	 * @param outcome
	 *            the outcome
	 */
	private void parseLOIDetails(ScLoi scLoi, String outcome) {
		if (scLoi.isBrandNew()) {
			scLoi.setIssueDate(DateUtils.getCurrentDateWithoutTime());

			ScLoiWorkFlowStatus currentStatus = (ScLoiWorkFlowStatus)scLoi.getScWorkFlowStatus();
			List<ScLoiWorkFlowStatus> workFlowStatuses = new ArrayList<ScLoiWorkFlowStatus>();
			workFlowStatuses.add(currentStatus);
			scLoi.setScLoiWorkFlowStatuses(workFlowStatuses);
			
			// To avoid workflow status reset to null when got error during direct submit without save.
			List<ScLoiRemark> remarks = scLoi.getScLoiRemarks();
			if (CollectionUtils.isNotEmpty(remarks)) {
				for (ScLoiRemark remark : remarks) {
					remark.setScWorkFlowStatus(currentStatus);
				}
			}
		}
		
		// validate
		validateLOIDetails(scLoi, outcome);
		
		Long statusId = scLoi.getScWorkFlowStatus().getPmStatus().getStatusId(); //20230711@Asyrafhaziq-SQUBE Bug Minor #16

		if (scLoi.isApprovalReq()
				&& QTStatusConst.LOI_PENDING_SUBMISSION.equals(statusId)) {
			PmUser approverUser = getManagedBean().getApproverUser();

			if (approverUser.getUserId() != null) {
				ScLoiApproval scLoiApproval = scLoi.getScLoiApproval();

				if (scLoiApproval == null) {
					scLoiApproval = new ScLoiApproval();
					scLoiApproval.setScLoi(scLoi);
					scLoiApproval.setApprover(approverUser);

					scLoi.setScLoiApproval(scLoiApproval);
				} else {
					scLoiApproval.setApprover(approverUser);
				}
			}
		} else if (scLoi.isApprovalReq()
				&& QTStatusConst.LOI_PENDING_APPROVAL.equals(statusId)) {
			ScLoiApproval scLoiApproval = scLoi.getScLoiApproval();
			String action = null;
			if (BpmOutcomeConst.BPM_APPROVE.equals(outcome)) {
				action = ApprovalAction.APPROVAL_APPROVED.value();
			} else if (BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
				action = ApprovalAction.APPROVAL_REJECTED.value();
			} else if (BpmOutcomeConst.BPM_REVISE.equals(outcome)) {
				action = ApprovalAction.APPROVAL_REVISED.value();
			}
			//#21692 - Start
			else if (BpmOutcomeConst.BPM_EPV_CANCEL.equals(outcome)) {
				action = ApprovalAction.APPROVAL_CANCEL.value();
			}
			//#21692 - End

			scLoiApproval.setApproverAction(action);
			scLoiApproval.setApproverActionDate(new Date());
			scLoi.setScLoiApproval(scLoiApproval);
		} else if(!scLoi.isApprovalReq()){
			scLoi.setScLoiApproval(null);
		}

		if (QTStatusConst.LOI_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)) {
			scLoi.setAckDate(new Date());
			scLoi.setAckByUser(new FacesScopes().getUserSessionBean());
		}

		PmUser contractSignerUser = getManagedBean().getContractSignerUser();
		if (contractSignerUser != null
				&& contractSignerUser.getUserId() != null) {
			scLoi.setContractSigner(contractSignerUser);
		}

		if(!(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome))){
			scLoi.setScLoiRemarks(finalizeRemarks(scLoi.getScLoiRemarks()));
		}
		// nego terms
		if (QTStatusConst.LOI_PENDING_SUBMISSION.equals(statusId)
				|| QTStatusConst.LOI_PENDING_REVISION.equals(statusId)) {
			if (scLoi.getScNegoTerms() != null
					&& scLoi.getScNegoTerms().size() > 0) {
				List<ScLoiTerm> selectedTerms = new ArrayList<ScLoiTerm>();
				for (ScLoiTerm loiTerm : scLoi.getScNegoTerms()) {
					if (loiTerm.isSelectedTerm()) {
						selectedTerms.add(loiTerm);
					}
				}
				scLoi.setScNegoTerms(selectedTerms);
			}
		}
	}
	
	/**
	 * Validate loi details. 
	 *
	 * @param scLoi the sc loi
	 * @param outcome the outcome
	 */
	private void validateLOIDetails(ScLoi scLoi, String outcome) {
		Long statusId = scLoi.getScWorkFlowStatus().getPmStatus().getStatusId(); //20230711@Asyrafhaziq-SQUBE Bug Minor #16
		
		if (BpmOutcomeConst.BPM_SUBMIT.equals(outcome) && (QTStatusConst.LOI_PENDING_SUBMISSION.equals(statusId)
				|| QTStatusConst.LOI_PENDING_REVISION.equals(statusId))) {

			// online bidding
			if (scLoi.getRecommendBy() != null) {
				PmParameter recommendedBy = scLoi.getRecommendBy();
				if (ParameterConst.RECOMMEND_BY_ONLINE_BIDDING
						.equals(recommendedBy.getParameterCode())
						|| ParameterConst.RECOMMEND_BY_MULTIPLE_LOI
						.equals(recommendedBy.getParameterCode())) {
					// validate negotiation location
					if (scLoi.getScNegoLocations() == null
							|| scLoi.getScNegoLocations().isEmpty()) {
						throw new EpException(MessageConst.ERROR_760274);
					}
				}
			}
			
			// validate negotiation Term
			boolean isSelected = Boolean.TRUE;
			if (scLoi.getScNegoTerms() != null
					&& scLoi.getScNegoTerms().size() > 0) {
				for (ScLoiTerm scLoiTerm : scLoi.getScNegoTerms()) {
					if (scLoiTerm.isSelectedTerm()) {
						isSelected = Boolean.FALSE;
					}
				}
				if (isSelected) {
					throw new EpException(MessageConst.ERROR_760289);
				}
			}

			if(scLoi.isApprovalReq()){
				if(null == getManagedBean().getApproverUser() || null == getManagedBean().getApproverUser().getUserId()){
					throw new EpException("Approver is required.");
				}
			}
		}

		if (!BpmOutcomeConst.BPM_EPV_CANCEL.equals(outcome) &&
				QTStatusConst.LOI_PENDING_NEGOTIATION.equals(statusId)) {//#21692
			// validate zone tier and tier pricing
			for (ScLoiLoaItem  scLoiLoaItem : scLoi.getScLoiLoaItems()) {
				if(null != scLoiLoaItem.getTplSpecTierQty()){
					List<ScLoiLoaItem> filteredMaxPriceItems  = getManagedBean().getScLoiLoaItems();
					if(null == filteredMaxPriceItems){
						filteredMaxPriceItems  = filterZoneTierOrTierPriceItems(scLoi);
						getManagedBean().setScLoiLoaItems(filteredMaxPriceItems);
					}

					if(filteredMaxPriceItems != null && filteredMaxPriceItems.contains(scLoiLoaItem)){
						validateItemTierPriceByItem(scLoiLoaItem, filteredMaxPriceItems);
					}
				}
			}

			// validate deduction payamment for Others
			validateOthersDeductionPay(scLoi);

			// validate  supporting document
			if(scLoi.getScLoiSupportingDocs()  != null && scLoi.getScLoiSupportingDocs().size()>0){
				for (ScLoiSupportingDoc scLoiSupportingDoc : scLoi.getScLoiSupportingDocs() ) {
					if( null == scLoiSupportingDoc.getTitle() || StringUtils.isBlank(scLoiSupportingDoc.getTitle())){
						throw new EpException("Please enter supporting document title");
					}
					if( null == scLoiSupportingDoc.getScLoiSupportingDocAttachment()){
						throw new EpException("Please attach supporting document");
					}
				}
			}
			
			// validate negotiation location
			if (scLoi.getScNegoLocations() == null
					|| scLoi.getScNegoLocations().isEmpty()) {
				throw new EpException(MessageConst.ERROR_760274);
			}
			
		}

		if (QTStatusConst.LOI_PENDING_SUBMISSION.equals(statusId)
				|| QTStatusConst.LOI_PENDING_REVISION.equals(statusId)) {
			// validate nego location duplicate 
			if(null != scLoi.getScNegoLocations()){
				List<Date> uniqueNegoDates = new ArrayList<Date>();
				for (ScLoiNegoLocation scLoiNegoLocation : scLoi.getScNegoLocations()) {
					if (uniqueNegoDates.contains(scLoiNegoLocation.getNegoDate())) {
						throw new EpException(MessageConst.ERROR_760339);
					}
					uniqueNegoDates.add(scLoiNegoLocation.getNegoDate());
				}
			}

			// validate supplier contract address
			if(null == scLoi.getScSupplierAddress()){
				throw new EpException("No supplier contact address");
			}
		}
		
		// #11134 - Start
		if(QTStatusConst.LOI_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)
				&& BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
			
			boolean remarksEmpty = false;
			
			// check if remark is empty
			List<ScLoiRemark> scLoiRemarks = scLoi.getScLoiRemarks();
			if(CollectionUtils.isNotEmpty(scLoiRemarks)){
				for(ScLoiRemark each : scLoiRemarks){
					if(StringUtils.isEmpty(each.getRemark())){
						remarksEmpty = true;
						break;
					}
				}
			}else {
				remarksEmpty = true;
			}
			
			//throw message
			if(remarksEmpty){
				throw new EpException(
						MessageConst.ERROR_716024);
				 // Remark is mandatory for rejection.
			}
		}
		// #11134 -end
		//#34725 Start Vinod
		if (BpmOutcomeConst.BPM_SUBMIT.equals(outcome) 
				&& QTStatusConst.LOI_PENDING_NEGOTIATION.equals(statusId)
				&& CollectionUtils.isNotEmpty(scLoi.getScNegoTerms())){
			for(ScLoiTerm scLoiTerm : scLoi.getScNegoTerms()){
				if(StringUtils.isEmpty(scLoiTerm.getNegoResult())){
					getManagedBean().setSelectedTabIndex(4);// 34491
					throw new EpException(MessageConst.ERROR_100022, new String[] {getMessageCommon("lbl.sc.loiloa.nego.results")});
				}
			}
		}
		//#34725 End vinod
	}

	/**
	 * Renew session with previous values.
	 */
	private void renewSessionWithPreviousValues() {
		List<ScLoiLoa>  recSupplierList = getManagedBean().getRecSupplierList();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		Object payLoadData = getManagedBean().getPayLoad();
		Long sourceId = getManagedBean().getSourceId();
		String taskId = getManagedBean().getTaskId();
		String taskName = getManagedBean().getTaskName();//33380
		String[] outcomes = getManagedBean().getOutComes();
		Long currentStatusId = getManagedBean().getCurrentStatusId();
		String docType = getManagedBean().getDocType();
		String committeeType = getManagedBean().getCommitteeType();
		ScQtCommittee scQtCommittee = getManagedBean().getScQtCommittee();// 34491
		String backToPreviousPage = getManagedBean().getBackToPreviousPage();
		List<String> recommendDtlIds = getManagedBean().getRecommendDtlIds();
		List<ScLoiTerm> negoTerms = getManagedBean().getNegoTermsList();
		boolean editButton = getManagedBean().isEditFlag();
		boolean buttonFlag = getManagedBean().isButtonFlag();
		int selectedTabIndex = getManagedBean().getSelectedTabIndex();
		boolean isSecretaryUser = getManagedBean().isSecretaryUser();
		boolean enableDownloadAllDocuments = getManagedBean().getEnableDownloadAllDocuments();

		this.renewSesionObject();

		getManagedBean().setSourceId(sourceId);
		getManagedBean().setPayLoad(payLoadData);
		getManagedBean().setTaskId(taskId);
		getManagedBean().setTaskName(taskName);//33380
		getManagedBean().setOutComes(outcomes);
		getManagedBean().setCurrentStatusId(currentStatusId);
		getManagedBean().setBackToPreviousPage(backToPreviousPage);
		if (payLoadData != null) {
			if (payLoadData instanceof SCEvaluationDataType) {
				getManagedBean().setRecommendDtlIds(
						((SCEvaluationDataType) payLoadData).getGroupkeyMa());
			} else if (payLoadData instanceof SCAwardSupplierDataType) {
				getManagedBean().setRecommendDtlIds(
						((SCAwardSupplierDataType) payLoadData)
								.getGroupkeyList());
			}
		}
		getManagedBean().setDocType(docType);
		getManagedBean().setRecommendDtlIds(recommendDtlIds);
		getManagedBean().setCommitteeType(committeeType);
		getManagedBean().setNegoTermsList(negoTerms);
		getManagedBean().setEditFlag(editButton);
		getManagedBean().setButtonFlag(buttonFlag);
		getManagedBean().setScQtFinalization(scQtFinalization);
		getManagedBean().setRecSupplierList(recSupplierList);
		getManagedBean().setSelectedTabIndex(selectedTabIndex);
		getManagedBean().setSecretaryUser(isSecretaryUser);
		getManagedBean().setEnableDownloadAllDocuments(enableDownloadAllDocuments);
		if(null!=scQtFinalization){
		this.getManagedBean().setScQt(scQtFinalization.getScQt());
		}
		// 34491 - start
		getManagedBean().setScQtCommittee(scQtCommittee);
		// 34491 - end
	}

	/**
	 * Gets the deduction item type dropdown list.
	 * 
	 * @return the deduction item type dropdown list
	 */
	public SelectItem[] getDeductionItemTypeDropdownList() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(),
						ParameterConst.DEDUCTION_TYPE_ADVANCE_PAYMENT));
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(),
						ParameterConst.DEDUCTION_TYPE_TRANSACTION_FEE));

		return super.getDropdownValuesBySearchBean(
				ParameterTypeConst.DEDUCTION_TYPE, searchBean);
	}

	/**
	 * Deduction type change. Deduction Clause will show when deduction type
	 * changes
	 * 
	 * @param scQtDeduction
	 *            the sc qt deduction
	 */
	public void deductionTypeChange(ScQtDeduction scQtDeduction) {
		
		debugLogger("deductionTypeChange");
			Long parameterId = scQtDeduction.getDeductionType()
					.getParameterId();
			scQtDeduction.setDeductionType(retrieveParameterById(parameterId));
			validateDuplicateDeductionPay(scQtDeduction);
			getClausesTitle(scQtDeduction);
			scQtDeduction.setScQtDeductionClauses(null);
			getManagedBean().setSelectedScQtDeduction(scQtDeduction);
		
	}

	/**
	 * Validate deduction payment.
	 *
	 * @param scQtDeduction the sc qt deduction
	 */
	private void validateDuplicateDeductionPay(ScQtDeduction scQtDeduction) {
		List<ScQtDeduction> scQtDeductions = getManagedBean().getScLoi().getScQtDeductions();
		if(scQtDeductions != null && scQtDeductions.size()>1){
			for (ScQtDeduction qtDeduction : scQtDeductions) {
				if(!qtDeduction.equals(scQtDeduction)){
					if(qtDeduction.getDeductionType().getParameterId() == scQtDeduction.getDeductionType().getParameterId()){
						throw new EpException(MessageConst.ERROR_700027, new String[] {ResourceBundleUtils.getMessage(getParameterDesc(scQtDeduction.getDeductionType()),
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}
			}
		}

	}

	/**
	 * Gets the clauses title.
	 * 
	 * @param deduction
	 *            the deduction
	 * @return the clauses title
	 */
	private void getClausesTitle(ScQtDeduction deduction) {
		StringBuilder title = new StringBuilder();
		title.append("Title");
		title.append(":");
		title.append(" ");
		if (deduction != null && deduction.getDeductionType() != null) {
			title.append(getParameterDesc(deduction.getDeductionType()));
			title.append(">");
			title.append(" ");
		}

		Locale locale = new FacesScopes().getUserLocale();
		title.append(ResourceBundleUtils.getMessage("lbl.sc.deduction.clauses",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		getManagedBean().setClausesTitle(title.toString());
	}

	/**
	 * Gets the clauses title.
	 * 
	 * @param deduction
	 *            the deduction
	 * @return the clauses title
	 */
	private void getLoiLoaListTitle(ScLoiLoa scLoiLoa) {
		StringBuilder title = new StringBuilder();
		title.append("Title");
		title.append(":");
		title.append(" ");
		
		if(scLoiLoa != null && scLoiLoa.getSmSupplier() != null){
			title.append(scLoiLoa.getSmSupplier().getCompanyName());
			title.append(">");
			title.append(" ");
		}
		Locale locale = new FacesScopes().getUserLocale();
		title.append(ResourceBundleUtils.getMessage("lbl.sc.loiloa.loiloalist",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		getManagedBean().setLoiLoaListTitle(title.toString());
	}

	/**
	 * Adds the deduction payment action.
	 */
	public void addDeductionPaymentAction() {
		debugLogger("addDeductionPaymentAction");

		ScLoi scLoi = this.getManagedBean().getScLoi();
		List<ScQtDeduction> scQtDeductions = scLoi.getScQtDeductions();

		if (scQtDeductions == null) {
			scQtDeductions = new ArrayList<ScQtDeduction>();

			scLoi.setScQtDeductions(scQtDeductions);
		}
		ScQtDeduction scQtDeduction = new ScQtDeduction();
		scQtDeduction.setScQt(getManagedBean().getScQt());
		scQtDeduction.setScLoi(scLoi);
		scQtDeduction.setDeductionType(new PmParameter());

		scQtDeductions.add(scQtDeduction);
	}

	/**
	 * Delete deduction payment.
	 */
	public void deleteDeductionPayment() {
		ScLoi scLoi = getManagedBean().getScLoi();
		ScQtDeduction qtDeduction = getManagedBean().getSelectedScQtDeduction();
		if (qtDeduction != null) {
			deleteRowAction(scLoi.getScQtDeductions(), qtDeduction);
			getManagedBean().setSelectedScQtDeduction(null);
		} else {
			throw new EpException(MessageConst.ERROR_760158);
		}
		getManagedBean().setSelectedScQtDeduction(null);
	}

	/**
	 * Adds the deduction clause action.
	 */
	public void addDeductionClauseAction() {
		debugLogger("addDeductionClauseAction");

		ScQtDeduction selectedScQtDeduction = this.getManagedBean()
				.getSelectedScQtDeduction();
		List<ScQtDeductionClause> scQtDeductionClauses = selectedScQtDeduction
				.getScQtDeductionClauses();

		if (scQtDeductionClauses == null) {
			scQtDeductionClauses = new ArrayList<ScQtDeductionClause>();

			selectedScQtDeduction.setScQtDeductionClauses(scQtDeductionClauses);
		}

		ScQtDeductionClause scQtDeductionClause = new ScQtDeductionClause();
		scQtDeductionClause.setScQtDeduction(selectedScQtDeduction);
		scQtDeductionClauses.add(scQtDeductionClause);
	}

	/**
	 * Delete deduction clause.
	 */
	public void deleteDeductionClause() {
		ScQtDeduction scQtDeduction = getManagedBean()
				.getSelectedScQtDeduction();
		List<ScQtDeductionClause> clauses = getManagedBean()
				.getSelectedDeductionClauses();
		if (clauses != null && clauses.size() > 0) {
			deleteRowsAction(scQtDeduction.getScQtDeductionClauses(), clauses);
		} else {
			throw new EpException(MessageConst.ERROR_760158);
		}
		getManagedBean()
		.setSelectedDeductionClauses(null);
	}

	/**
	 * Contract signer event.
	 */
	public void contractSignerEvent() {
		debugLogger("contractSignerEvent");
		PmUser contractSigner = getManagedBean().getContractSignerUser();
		if (contractSigner != null) {
			if (getManagedBean().getContractSigners() != null
					&& getManagedBean().getContractSigners().size() > 0) {
				List<PmUser> contractSignerList = getManagedBean()
						.getContractSigners();
				for (PmUser user : contractSignerList) {

					if (contractSigner.getUserId().equals(user.getUserId())) {
						getManagedBean().getScLoi().setLoiSignerDesig(
								user.getDesignation());
					}
				}
			}
		}
	}

	/**
	 * Approver required event.
	 */
	public void approverRequiredEvent() {
		debugLogger("approverRequiredEvent");
		ScLoi scLoi = getManagedBean().getScLoi();
		// approvers
		if (scLoi.isApprovalReq()) {
			ScLoiApproval scLoiApproval = scLoi.getScLoiApproval();
			PmUser approverUser = null;
			if (scLoiApproval != null) {
				approverUser = scLoiApproval.getApprover();
			}
			if (approverUser == null) {
				approverUser = new PmUser();
			}
			getManagedBean().setApproverUser(approverUser);
		} else {
			getManagedBean().setApproverUser(new PmUser());
		}
	}

	/**
	 * Calculate start month.
	 * 
	 * @param event
	 *            the event
	 */
	public void calculateStartMonth(AjaxBehaviorEvent event) {
		event.getComponent().getAttributes().get("value");

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
			ScLoiReport selectedScLoiReport = this.getManagedBean()
					.getSelectedScLoiReport();
			ScLoiReportAttachment scLoiReportAttachment = selectedScLoiReport
					.getScLoiReportAttachment();
			if (null == scLoiReportAttachment) {
				scLoiReportAttachment = new ScLoiReportAttachment();
				scLoiReportAttachment.setScLoiReport(selectedScLoiReport);
				selectedScLoiReport
						.setScLoiReportAttachment(scLoiReportAttachment);
			}
			scLoiReportAttachment.setScLoiReport(selectedScLoiReport);
			scAttachment = scLoiReportAttachment;

			uploadAttachment(event.getFile(), scAttachment);
		}
	}

	/**
	 * Checkbox event to select Supplier Finalization Report under Briefing Paper tab
	 * 
	 * @param ScQtFinalizationReport
	 *            the ScQtFinalizationReport to set
	 */
	public void selectReportEvent(ScLoiReport report) {
		debugLogger("selectReportEvent - Checkbox select");

		List<ScLoiReport> selectedLoiReports
					= getManagedBean().getSelectedScLoiReports();
		if (selectedLoiReports == null) {
			selectedLoiReports= new ArrayList<ScLoiReport>();
			getManagedBean().setSelectedScLoiReports(
								   selectedLoiReports);
		}
		if (report.isSelected()) {
			selectedLoiReports.add(report);
		} else {
			selectedLoiReports.remove(report);
		}
	}	
	
	/**
	 * Adds the loi report action.
	 */
	public void addLoiReportAction() {
		debugLogger("addLoiReportAction");

		ScLoi scLoi = this.getManagedBean().getScLoi();
		List<ScLoiReport> scLoiReports = scLoi.getScLoiReports();

		if (scLoiReports == null) {
			scLoiReports = new ArrayList<ScLoiReport>();
			scLoi.setScLoiReports(scLoiReports);
		}

		ScLoiReport scLoiReport = new ScLoiReport();
		scLoiReport.setScLoi(scLoi);
//		scLoiReport.setScLoiReportAttachment(new ScLoiReportAttachment());

		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
			reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
			ParameterResDTO resDTO = parameterSO
					.retrieveParameterByTypeAndCode(reqDTO);
			scLoiReport.setDefinationType(resDTO.getPmParameter());
			scLoiReport.setDocCategory(
					ParameterConst.DOCUMENT_CATEGORY_LOI_NEGOTIATION_RESULT); // NR
		} finally {
			parameterSO = null;
		}

		scLoiReports.add(scLoiReport);
	}

	/**
	 * Delete loi report.
	 */
	public void deleteLoiReport() {
		ScLoi scLoi = this.getManagedBean().getScLoi();
		List<ScLoiReport> scLoiReports = scLoi.getScLoiReports();
		List<ScLoiReport> selectedLoiReports
							= getManagedBean().getSelectedScLoiReports();
		List<ScLoiReport> deletedLoiReports
							= getManagedBean().getDeletedScLoiReports();
		
		if (deletedLoiReports == null) {
			deletedLoiReports = new ArrayList<ScLoiReport>();
			getManagedBean().setDeletedScLoiReports(
								   deletedLoiReports);
		}
		
		if (CollectionUtils.isNotEmpty(selectedLoiReports)) {
			for (ScLoiReport loiReport 
						: selectedLoiReports) {
				if (!loiReport.isBrandNew()) {
					loiReport.setDeleted(true);
					deletedLoiReports.add(loiReport);
				}
				scLoiReports.remove(loiReport);
			}
		}
	}

	/**
	 * Download report.
	 * 
	 * @param scLoiReport
	 *            the sc loi report
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void downloadReport(ScLoiReport scLoiReport) throws IOException {
		debugLogger("downloadReport");
		ScLoiReportAttachment scLoiReportAttachment = scLoiReport
				.getScLoiReportAttachment();
		if (QTStatusConst.SF_PENDING_MULTIPLE_LOI_RECOMMENDATION
				.equals(getManagedBean().getCurrentStatusId())
							|| scLoiReportAttachment == null) {
			scLoiReportAttachment = generateDocumentAttachment(scLoiReport);
			scLoiReport.setScLoiReportAttachment(scLoiReportAttachment);
		}
		downloadFile(scLoiReportAttachment);
	}

	/**
	 * Download all reports.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void downloadAllReports() throws IOException {
		debugLogger("downloadAllReports");
		List<ScLoiReport> scLoiReports = getManagedBean().getScLoi()
				.getScLoiReports();
		List<ScLoiReportAttachment> scAttachments = new ArrayList<ScLoiReportAttachment>();
		for (ScLoiReport scLoiReport : scLoiReports) {
			ScLoiReportAttachment scLoiReportAttachment = scLoiReport
					.getScLoiReportAttachment();
			if (scLoiReportAttachment == null) {
				scLoiReportAttachment = generateDocumentAttachment(scLoiReport);
				scLoiReport.setScLoiReportAttachment(scLoiReportAttachment);
			}
			String prefixName = scLoiReport.getTitle().replaceAll(
					SourcingConstants.REG_EXP_WHITE_SPACE, "");
			scLoiReportAttachment.setTempFileName(prefixName);
			scAttachments.add(scLoiReportAttachment);
		}
		downloadAllFiles("NGeP-LOI-Report", scAttachments);
	}

	/**
	 * Adds the loi supp doc action.
	 */
	public void addLoiSuppDocAction() {
		debugLogger("addLoiSuppDocAction");

		ScLoi scLoi = this.getManagedBean().getScLoi();
		List<ScLoiSupportingDoc> scLoiSuppDocs = scLoi.getScLoiSupportingDocs();

		if (scLoiSuppDocs == null) {
			scLoiSuppDocs = new ArrayList<ScLoiSupportingDoc>();
			scLoi.setScLoiSupportingDocs(scLoiSuppDocs);
		}

		ScLoiSupportingDoc scLoiSuppDoc = new ScLoiSupportingDoc();
		scLoiSuppDoc.setScLoi(scLoi);
		scLoiSuppDoc.setDocId(scLoi.getLoiId().toString());
		scLoiSuppDoc.setDocType(ParameterConst.DOCTYPE_LETTER_OF_INTENT_SUPPORTING_DOC);
		scLoiSuppDoc
				.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOI_SUPPORTING_DOC);
		scLoiSuppDoc.setPmDocument(null);

		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
			reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
			ParameterResDTO resDTO = parameterSO
					.retrieveParameterByTypeAndCode(reqDTO);
			scLoiSuppDoc.setDefinationType(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}

		scLoiSuppDocs.add(scLoiSuppDoc);
	}

	/**
	 * Delete supporing docs in negotiation details.
	 */
	public void deleteSupporingDocs() {
		ScLoi scLoi = getManagedBean().getScLoi();
		List<ScLoiSupportingDoc> selectedScLoiSuppDocs = getManagedBean()
				.getSelectedScLoiSuppDocs();

		if (selectedScLoiSuppDocs != null && selectedScLoiSuppDocs.size() > 0) {
			deleteRowsAction(scLoi.getScLoiSupportingDocs(),
					selectedScLoiSuppDocs);
		} else {
			throw new EpException(MessageConst.ERROR_760158);
		}
	}

	/**
	 * Handle supp doc file upload.
	 * 
	 * @param event
	 *            the event
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void handleSuppDocFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		debugLogger("handleSuppDocFileUpload");
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScLoiSupportingDoc selectedScLoiSuppDoc = this.getManagedBean()
					.getSelectedScLoiSuppDoc();
			ScLoiSupportingDocAttachment scLoiSupportingDocAttachment = selectedScLoiSuppDoc
					.getScLoiSupportingDocAttachment();
			if (null == scLoiSupportingDocAttachment) {
				scLoiSupportingDocAttachment = new ScLoiSupportingDocAttachment();
				scLoiSupportingDocAttachment
						.setScLoiSupportingDoc(selectedScLoiSuppDoc);
				selectedScLoiSuppDoc
						.setScLoiSupportingDocAttachment(scLoiSupportingDocAttachment);
			}
			scLoiSupportingDocAttachment.setDocNo(getManagedBean().getScLoi().getLoiNo());
			scAttachment = scLoiSupportingDocAttachment;

			uploadAttachment(event.getFile(), scAttachment);
		}
	}

	/**
	 * Generate document attachment.
	 * 
	 * @param scLoiReport
	 *            the sc loi report
	 * @return the sc loi report attachment
	 */
	private ScLoiReportAttachment generateDocumentAttachment(
			ScLoiReport scLoiReport) {
		String definitionType = scLoiReport.getDefinationType()
				.getParameterCode();
		ScLoiReportAttachment scLoiReportAttachment = null;
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
				.equals(definitionType)) {
			try {
				String docDefinitionKey = scLoiReport.getPmDocument()
						.getDocDefinitionKey();
				scLoiReportAttachment = new ScLoiReportAttachment();
				scLoiReportAttachment.setScLoiReport(scLoiReport);
				scLoiReportAttachment.setDocNo(scLoiReport.getScLoi()
						.getLoiNo());

				if (SourcingConstants.DOC_DEF_KEY_COVER
						.equals(docDefinitionKey)) {
					generateCoverPdfFromVelocity(
							(ScAttachment) scLoiReportAttachment,
							getManagedBean().getScLoi());
				} else if (SourcingConstants.DOC_DEF_KEY_OFFERED_QT_DTLS
						.equals(docDefinitionKey)) {
					generateOfferedAndQTDtlsPdf(
							(ScAttachment) scLoiReportAttachment,
							getManagedBean().getScLoi());
				} else if (SourcingConstants.DOC_DEF_KEY_NEGOTIATION_LOCATION
						.equals(docDefinitionKey)) {
					generateNegotiationLocationPdf(
							(ScAttachment) scLoiReportAttachment,
							getManagedBean().getScLoi());
				} else if (SourcingConstants.DOC_DEF_KEY_NEGOTIATION_COMMITTEE
						.equals(docDefinitionKey)) {
					generateNegotiationCommitteePdf(
							(ScAttachment) scLoiReportAttachment,
							getManagedBean().getScLoi());
				} else if (SourcingConstants.DOC_DEF_KEY_NEGOTIATION_RESULT
						.equals(docDefinitionKey)) {
					generateNegotiationResultPdf(
							(ScAttachment) scLoiReportAttachment,
							getManagedBean().getScLoi());
				} else if (SourcingConstants.DOC_DEF_KEY_NEGOTIATION_RECOMMENDATION
						.equals(docDefinitionKey)) {
					generateNegotiationRecommendationPdf(
							(ScAttachment) scLoiReportAttachment,
							getManagedBean().getScLoi());
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			}
			scLoiReport.setScLoiReportAttachment(scLoiReportAttachment);
		}
		return scLoiReportAttachment;
	}

	/**
	 * Generate negotiation recommendation pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoi
	 *            the sc loi
	 * @throws Exception
	 *             the exception
	 */
	private void generateNegotiationRecommendationPdf(ScAttachment attachment,
			ScLoi scLoi) throws Exception {
		attachment.setFileName("Negotiation_Recommendation.pdf");
		populateAttachmentDetails(attachment);
		VelocityContext context = new VelocityContext();
		List<ScQtRecommend> recommendedItems = getManagedBean().getRecommendList();
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat amountDF = new DecimalFormat(
						commonUtils.getDisplayNumberFormat(
								CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));		
		
		String afterNegoSubTotal = "";
		List<ScLoi> lois = getManagedBean().getLois();
		NegotiationRecommendationReportDTO reportDTO = null;
		Map<TplSpecZoneDtl,NegotiationRecommendationReportDTO> zoneDetailsMap = null;
		
		if (CollectionUtils.isNotEmpty(lois)) {
			for (ScLoi loi : lois) {
				if (!QTStatusConst.LOI_REJECTED.equals(
						loi.getScWorkFlowStatus().getPmStatus().getStatusId())) {
					Map<Long,TplSpecZoneDtl> zoneMap = getManagedBean().getZoneMap();
					if (recommendedItems != null) {
						Long awardedSupplierId = null;
						
						if (!getManagedBean().isItemized()) {
							Set<ScQtEvaluationResultDtl> awardedSuppliers = null;
							if (getManagedBean().isZonal()) {
								if (zoneDetailsMap == null) {
									zoneDetailsMap = new HashMap<TplSpecZoneDtl,NegotiationRecommendationReportDTO>();
								}
								Map<Long,List<ScQtRecommend>> scQtRecommendsByZone
										= getManagedBean().getScQtRecommendsByZone();		
								
								for (Long zoneId : zoneMap.keySet()) {
									awardedSupplierId = null;
									reportDTO = new NegotiationRecommendationReportDTO();
									List<ScQtRecommend> scQtRecommends = scQtRecommendsByZone.get(zoneId);
									if (CollectionUtils.isNotEmpty(scQtRecommends)) {
										ScQtRecommend scQtRecommend = scQtRecommends.get(0);
										if (CollectionUtils.isEmpty(
												scQtRecommend.getAwardSupplierList())) {
											processRecommendedSuppliersByPKG(scQtRecommends);
										}
										
										if (CollectionUtils.isNotEmpty(scQtRecommend.getAwardSupplierList())) {
											ScQtEvaluationResultDtl awardedSupplier = getRecommendedEvaluationResultDtlByPKG(scQtRecommend);
											if (awardedSupplier != null) {
												awardedSuppliers = new HashSet<ScQtEvaluationResultDtl>();												
												awardedSuppliers.add(awardedSupplier);
												awardedSupplierId = awardedSupplier.getQtEvaluationResult()
														.getScQtProposal().getScQtSupplier()
														.getSmSupplier().getSupplierId();
											}
										}
									}
									
									Long loiSupplierId = loi.getScLoiLoa().getSmSupplier().getSupplierId();
									if (awardedSupplierId != null && 
											loiSupplierId.equals(awardedSupplierId)) {
										Map<Long,List<ScLoiLoaItem>> loiLoaItemsMap 
													= getManagedBean().getLoiLoaItemsMap();
										List<ScLoiLoaItem> loiLoaItems = new ArrayList<ScLoiLoaItem>();
										for (ScLoiLoaItem scLoiLoaItem 
												: loiLoaItemsMap.get(zoneId)) {
											Long itemSupplierId = scLoiLoaItem.getScQtRecommendDtl()
																		.getSupplier().getSupplierId();
											if (awardedSupplierId.equals(itemSupplierId)) {
												loiLoaItems.add(scLoiLoaItem);
											}
										}
							
										BigDecimal afterNegotiationSubTotal = calculateAfterNegoSubTotal(
												loiLoaItems, scQtRecommendsByZone.get(zoneId));
										afterNegoSubTotal = amountDF.format(afterNegotiationSubTotal);
										reportDTO.setAwardedSuppliers(
												new ArrayList<ScQtEvaluationResultDtl>(awardedSuppliers));
										reportDTO.setRecommendedItems(scQtRecommendsByZone.get(zoneId));
										reportDTO.setLoiLoaItems(loiLoaItems);
										reportDTO.setAfterNegoSubTotal(afterNegoSubTotal);
										zoneDetailsMap.put(zoneMap.get(zoneId),reportDTO);
									}
								}
							} else {
								reportDTO = new NegotiationRecommendationReportDTO();
								reportDTO.setEmpty(true);
								Set<ScQtEvaluationResultDtl> recommendedSuppliers 
											= new HashSet<ScQtEvaluationResultDtl>(
														getManagedBean().getRecommendedSupplierList());
								if (CollectionUtils.isNotEmpty(recommendedSuppliers) && CollectionUtils.isNotEmpty(recommendedItems)) {
									ScQtRecommend scQtRecommend = recommendedItems.get(0);
									ScQtEvaluationResultDtl awardedSupplier = getRecommendedEvaluationResultDtlByPKG(scQtRecommend);
									if (awardedSupplier != null) {
										awardedSuppliers = new HashSet<ScQtEvaluationResultDtl>();
										awardedSuppliers.add(awardedSupplier);
										awardedSupplierId = awardedSupplier.getQtEvaluationResult()
												.getScQtProposal().getScQtSupplier()
												.getSmSupplier().getSupplierId();											
										//break;
									}
								}			
								
								Long loiSupplierId = loi.getScLoiLoa().getSmSupplier().getSupplierId();
								if (loiSupplierId.equals(awardedSupplierId)) {
									reportDTO.setEmpty(false);
									BigDecimal afterNegotiationSubTotal = calculateAfterNegoSubTotal(
											loi.getScLoiLoaItems(), recommendedItems);
									afterNegoSubTotal = amountDF.format(afterNegotiationSubTotal);									
									reportDTO.setAwardedSuppliers(
											new ArrayList<ScQtEvaluationResultDtl>(awardedSuppliers));
									reportDTO.setRecommendedItems(recommendedItems);
									reportDTO.setAfterNegoSubTotal(afterNegoSubTotal);
									reportDTO.setLoiLoaItems(loi.getScLoiLoaItems());
									
									break;
								}
							}
						} else {
							if (getManagedBean().isZonal()) {
								if (zoneDetailsMap == null) {
									zoneDetailsMap = new HashMap<TplSpecZoneDtl,NegotiationRecommendationReportDTO>();
								}
								Map<Long,List<ScQtRecommend>> scQtRecommendsByZone
										= getManagedBean().getScQtRecommendsByZone();
								
								for (Long zoneId : zoneMap.keySet()) {
									reportDTO = new NegotiationRecommendationReportDTO();
									Map<Long,List<ScLoiLoaItem>> loiLoaItemsMap 
												= getManagedBean().getLoiLoaItemsMap();

									processMultiLOIDetails(ParameterConst.RECOMMEND_TYPE_ITEM, 
															 scQtRecommendsByZone.get(zoneId));
									
									BigDecimal afterNegotiationSubTotal = calculateAfterNegoSubTotal(
											loiLoaItemsMap.get(zoneId), scQtRecommendsByZone.get(zoneId));
									afterNegoSubTotal = amountDF.format(afterNegotiationSubTotal);								
									
									reportDTO.setRecommendedItems(scQtRecommendsByZone.get(zoneId));
									reportDTO.setAfterNegoSubTotal(afterNegoSubTotal);
									zoneDetailsMap.put(zoneMap.get(zoneId),reportDTO);
								}
							} else {
								reportDTO = new NegotiationRecommendationReportDTO();
								BigDecimal afterNegotiationSubTotal = calculateAfterNegoSubTotal(
										loi.getScLoiLoaItems(), recommendedItems);
								afterNegoSubTotal = amountDF.format(afterNegotiationSubTotal);								
								reportDTO.setRecommendedItems(recommendedItems);
								reportDTO.setAfterNegoSubTotal(afterNegoSubTotal);
							}

						}
					}	
					
				}
			}
		}
		
		if (getManagedBean().isZonal()) {
			for (TplSpecZoneDtl specZone
						: getManagedBean().getZoneMap().values()) {
				if (zoneDetailsMap != null && !zoneDetailsMap.containsKey( //20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
						specZone)) {
					NegotiationRecommendationReportDTO tempDTO
							= new NegotiationRecommendationReportDTO();
					tempDTO.setEmpty(true);
					zoneDetailsMap.put(specZone,tempDTO);
				}
			}
		}
		
		context.put("reportDTO", reportDTO);
		context.put("zoneDetailsMap", zoneDetailsMap);
		context.put("numberFormatter", new DecimalFormat("#,###,##0.00"));
		context.put("itemized", getManagedBean().isItemized());
		context.put("isZonal", getManagedBean().isZonal());
		
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
					"/sc/loi/loi-negotiationRecommendation.vm", context,
					fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private BigDecimal calculateAfterNegoSubTotal(List<ScLoiLoaItem> scLoiLoaItems, 
											  List<ScQtRecommend> recommendedItems) {
		BigDecimal afterNegotiationSubTotal = new BigDecimal(0.0);
		
		if (CollectionUtils.isNotEmpty(scLoiLoaItems)) {
			for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItems) {
				if (scLoiLoaItem.getAfterNegoSubTotal() != null) {
					BigDecimal itemAfterNegoSubTotal = scLoiLoaItem.getAfterNegoSubTotal(); 
					itemAfterNegoSubTotal = itemAfterNegoSubTotal.setScale(2, BigDecimal.ROUND_HALF_EVEN);
					afterNegotiationSubTotal = afterNegotiationSubTotal.add(
														itemAfterNegoSubTotal);
					afterNegotiationSubTotal = afterNegotiationSubTotal
													.setScale(2, BigDecimal.ROUND_HALF_EVEN);
				}
			}
		}
				
		return afterNegotiationSubTotal;
	}	

	/**
	 * Generate negotiation result pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoi
	 *            the sc loi
	 * @throws Exception
	 *             the exception
	 */
	private void generateNegotiationResultPdf(ScAttachment attachment,
			ScLoi scLoi) throws Exception {
		attachment.setFileName("Negotiation_Result.pdf");
		populateAttachmentDetails(attachment);

		VelocityContext context = new VelocityContext();
		List<ScLoi> lois = getManagedBean().getLois();
		/*if (CollectionUtils.isNotEmpty(lois)) {
			for (ScLoi loi : lois) {
				if(!loi.equals(getManagedBean().getScLoi())){
					calculateOfferedAndFinalPriceByItem(loi);	
				}
			}
		}*/
		context.put("isZonal", getManagedBean().isZonal());
		context.put("decimalFormatter", new DecimalFormat("#,###,##0.00"));
		context.put("scLois", lois);
		context.put("status", QTStatusConst.LOI_REJECTED);
		context.put("bean", this);
		
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(SourcingConstants.LOI_NEGOTIONATION_RESULT, context,
					fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Generate negotiation committee pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoi
	 *            the sc loi
	 * @throws Exception
	 *             the exception
	 */
	private void generateNegotiationCommitteePdf(ScAttachment attachment,
			ScLoi scLoi) throws Exception {
		attachment.setFileName("Negotiation_Committee.pdf");
		populateAttachmentDetails(attachment);

		// ScLoiLoa scLoiLoa = scLoi.getScLoiLoa();
		VelocityContext context = new VelocityContext();
		List<ScQtCommitteeActivity> activityList = new ArrayList<ScQtCommitteeActivity>();
		List<ScQtMeetingAttendance> meetingAttendances = new ArrayList<ScQtMeetingAttendance>();
		List<ScQtMeetingAttendance> meetingAttendancesList = new ArrayList<ScQtMeetingAttendance>();

		if (getManagedBean().getEcActivityList() != null) {
			for (ScQtCommitteeActivity ecActivity : getManagedBean()
					.getEcActivityList()) {
				if (ecActivity.isMultiLoiAttended()) {
					activityList.add(ecActivity);
				}
			}
		}
		if (getManagedBean().getTecActivityList() != null) {
			for (ScQtCommitteeActivity tecActivity : getManagedBean()
					.getTecActivityList()) {
				if (tecActivity.isMultiLoiAttended()) {
					activityList.add(tecActivity);
				}
			}
		}
		if (getManagedBean().getFecActivityList() != null) {
			for (ScQtCommitteeActivity fecActivity : getManagedBean()
					.getFecActivityList()) {
				if (fecActivity.isMultiLoiAttended()) {
					activityList.add(fecActivity);
				}
			}
		}
		if (getManagedBean().getScQtCommittee() != null) {
			for (ScQtCommitteeActivity activity : getManagedBean()
					.getScQtCommittee().getCommitteeActivities()) {
				if (activity.isMultiLoiAttended()) {
					if (activity.getScCommitteeMember() != null) {
						if (activity.getScCommitteeMember().getMemberName() == null) {
							activity.getScCommitteeMember().setMemberName("");
						}
					} else {
						activity.setScCommitteeMember(new ScCommitteeMember());
						activity.getScCommitteeMember().setMemberName("");
					}
					if (activity.getScCommitteeMember() != null) {
						if (activity.getScCommitteeMember().getDesignation() == null) {
							activity.getScCommitteeMember().setDesignation("");
						}
					} else {
						activity.setScCommitteeMember(new ScCommitteeMember());
						activity.getScCommitteeMember().setDesignation("");
					}
					if (activity.getScCommitteeMember() != null) {
						if (activity.getScCommitteeMember().getGrade() == null) {
							activity.getScCommitteeMember().setGrade("");
						}
					} else {
						activity.setScCommitteeMember(new ScCommitteeMember());
						activity.getScCommitteeMember().setGrade("");
					}
					if (activity.getScCommitteeMember() != null) {
						if (activity.getScCommitteeMember().getIcPassport() == null) {
							activity.getScCommitteeMember().setIcPassport("");
						}
					} else {
						activity.setScCommitteeMember(new ScCommitteeMember());
						activity.getScCommitteeMember().setIcPassport("");
					}
					activityList.add(activity);
				}
			}
			if (null != getManagedBean().getScQtCommittee().getMeetingAttendances()
					&& !getManagedBean().getScQtCommittee().getMeetingAttendances().isEmpty()) {
				meetingAttendances = getManagedBean().getScQtCommittee().getMeetingAttendances();
				for (ScQtMeetingAttendance attendance : meetingAttendances) {
					if (attendance.getName() == null) {
						attendance.setName("");
					}
					if (attendance.getIcPassport() == null) {
						attendance.setIcPassport("");
					}
					meetingAttendancesList.add(attendance);
				}
			}
		}
		context.put("activityLists", activityList);
		context.put("meetingAttendances", meetingAttendancesList);
		context.put("bean", this);

		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
					"/sc/loi/loi-negotiationCommittee.vm", context,
					fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Generate negotiation location pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoi
	 *            the sc loi
	 * @throws Exception
	 *             the exception
	 */
	private void generateNegotiationLocationPdf(ScAttachment attachment,
			ScLoi scLoi) throws Exception {
		attachment.setFileName("Negotiation_Location.pdf");
		populateAttachmentDetails(attachment);
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
						.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		VelocityContext context = new VelocityContext();
		
		context.put("dateSDF", dateSDF);
		context.put("loiList", getManagedBean().getLois());
		context.put("rejectedStatus", "62406");

		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
					"/sc/loi/loi-negotiationLocation.vm", context,
					fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Generate offered and qt dtls pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoi
	 *            the sc loi
	 * @throws Exception
	 *             the exception
	 */
	private void generateOfferedAndQTDtlsPdf(ScAttachment attachment,
			ScLoi scLoi) throws Exception {
		attachment.setFileName("OfferedAndQTDetails.pdf");
		populateAttachmentDetails(attachment);

		ScLoiLoa scLoiLoa = scLoi.getScLoiLoa();
		PmOrgProfile pmOrgProfile = scLoiLoa.getPmOrgProfile();
		VelocityContext context = new VelocityContext();

		String title=	scLoiLoa.getTitle();
		if (title == null) {
			title = "";
		}
		
		//replace all special characters
		//title = title.replaceAll("\\W", ""); 
		title=title.replaceAll("\r\n", "<br />");
		//title=title.replaceAll("&", "&#38;");
		//title=title.replaceAll("<", "&#60;");
		//title=title.replaceAll(">", "&#62;");
		context.put("title", title);
		/*context.put("fulfilmentType",
				getParameterDesc(scLoiLoa.getFulfilmentTypeCode()));*/
		StringBuilder contractDuration = new StringBuilder();
		if (this.getManagedBean().getScQt().getContractDuration() != null) {
			contractDuration.append(this.getManagedBean().getScQt().getContractDuration() + " " + getMessageCommon("lbl.sc.sp.month")); 
		}
		context.put("contractDuration", contractDuration);
		String ptjName=" ";
		if(null!=this.getManagedBean().getCurrentStatusId()){
			debugLogger("=====if generateOfferedAndQTDtlsPdf"+this.getManagedBean().getCurrentStatusId());
			debugLogger("publish date"+this.getManagedBean().getScQtFinalization().getScQt().getPublishDate());
			debugLogger("current date"+this.getManagedBean().getScQtFinalization().getScQt().getCreatedDate());
		 ptjName=retrievePTJByOrgProfileId(pmOrgProfile.getOrgProfileId(),isQTStatusPublished(this.getManagedBean().getCurrentStatusId())?this.getManagedBean().getScQtFinalization().getScQt().getPublishDate() : this.getManagedBean().getScQtFinalization().getScQt().getCreatedDate());
		}else{
			debugLogger("=====else generateOfferedAndQTDtlsPdf");
			ptjName=pmOrgProfile.getOrgName();
		}
		debugLogger("ptjName generateOfferedAndQTDtlsPdf"+ptjName);
		context.put("ptj", ptjName);
		String totalSuppliers = "";
		if (this.getManagedBean().getLois() != null) {
			totalSuppliers = String.valueOf(this.getManagedBean().getLois()
					.size());
		}
		context.put("supplierSize", totalSuppliers);

		List<ScLoi> localList = new ArrayList<ScLoi>();
		if (this.getManagedBean().getLois() != null) {
			for (ScLoi loi : this.getManagedBean().getLois()) {
				if (QTStatusConst.LOI_COMPLETED.equals(loi.getScWorkFlowStatus().getPmStatus()
						.getStatusId())) {
					localList.add(loi);
				}
			}
		}
		String totalStatusSuppliers = "";
		if (localList != null) {
			totalStatusSuppliers = String.valueOf(localList.size());
		}
		context.put("statusSupplierSize", totalStatusSuppliers);

		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
					"/sc/loi/loi-offeredAndQTDetails.vm", context,
					fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Generate cover pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoi
	 *            the sc loi
	 * @throws Exception
	 *             the exception
	 */
	private void generateCoverPdfFromVelocity(ScAttachment attachment,
			ScLoi scLoi) throws Exception {
		attachment.setFileName("Cover.pdf");
		populateAttachmentDetails(attachment);

		ScLoiLoa scLoiLoa = scLoi.getScLoiLoa();
		PmOrgProfile pmOrgProfile = scLoiLoa.getPmOrgProfile();
		VelocityContext context = new VelocityContext();
		
		FacesScopes fs = new FacesScopes();
		String hostUrl = fs.getRequest().getServerName() +":" + fs.getRequest().getServerPort();
		context.put("hostUrl", hostUrl);

		// context.put("ministry",
		// pmOrgProfile.getParentOrgProfile().getOrgName());
		String ptjName=" ";
		if(null!=this.getManagedBean().getCurrentStatusId()){
			debugLogger("=====if generateCoverPdfFromVelocity"+this.getManagedBean().getCurrentStatusId());
			debugLogger("publish date"+this.getManagedBean().getScQtFinalization().getScQt().getPublishDate());
			debugLogger("current date"+this.getManagedBean().getScQtFinalization().getScQt().getCreatedDate());
		 ptjName=retrievePTJByOrgProfileId(pmOrgProfile.getOrgProfileId(),isQTStatusPublished(this.getManagedBean().getCurrentStatusId())?this.getManagedBean().getScQtFinalization().getScQt().getPublishDate() : this.getManagedBean().getScQtFinalization().getScQt().getCreatedDate());
		}else{
			debugLogger("=====else generateCoverPdfFromVelocity");
			ptjName=pmOrgProfile.getOrgName();
		}
		debugLogger("ptjName generateCoverPdfFromVelocity"+ptjName);
		context.put("agency",ptjName);

		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
					SourcingConstants.LOI_COVER, context,
					fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Select all select all nego terms
	 * 
	 * @param event
	 *            the event
	 */
	public void selectAll(AjaxBehaviorEvent event) {
		boolean isSelectAll = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<ScLoiTerm> scLoiTerms = getManagedBean().getScLoi()
				.getScNegoTerms();
		if (scLoiTerms != null && scLoiTerms.size() > 0) {
			for (ScLoiTerm scLoiTerm : scLoiTerms) {
				if (isSelectAll && scLoiTerm.getPmTerm() == null) {
					scLoiTerm.setSelectTerm(true);
				} else {
					scLoiTerm.setSelectTerm(false);
				}
			}
		}
	}

	/**
	 * Select all select all nego locations
	 * 
	 * @param event
	 *            the event
	 */
	public void selectAllNegoLocations(AjaxBehaviorEvent event) {
		boolean isSelectAll = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<ScLoiNegoLocation> scLoiLocations = getManagedBean().getScLoi()
				.getScNegoLocations();
		if (scLoiLocations != null && scLoiLocations.size() > 0) {
			for (ScLoiNegoLocation scloiLocation : scLoiLocations) {
				if (isSelectAll) {
					scloiLocation.setSelected(true);
				} else {
					scloiLocation.setSelected(false);
				}
			}
		}
	}

	/**
	 * Nego term price event. when price found in terms, shows original offered
	 * price and after nego offered price.
	 */
	public void negoTermPriceEvent() {
		boolean termPriceFound = false;
		ScLoi scLoi = getManagedBean().getScLoi();

		if (scLoi.getScNegoTerms() != null && scLoi.getScNegoTerms().size() > 0) {
			for (ScLoiTerm term : scLoi.getScNegoTerms()) {
				if (term.getTermDesc() != null) {
					if (QTConstants.LOI_NEGO_TERM_PRICE
							.equals(term.getTermDesc())) {
						termPriceFound = true;
					}
				}
			}
		}
		getManagedBean().setHideColumnFlag(termPriceFound);
	}

	/**
	 * Price key in event. when keyin unit price, calculate subtotal after nego.
	 */
	public void priceKeyInEvent() {
		debugLogger("priceKeyInEvent");
		ScLoi scLoi = getManagedBean().getScLoi();

		List<ScLoiLoaItem> filteredMaxPriceItemsbyQty  = getManagedBean().getScLoiLoaItems();
		if(null == filteredMaxPriceItemsbyQty){
			filteredMaxPriceItemsbyQty  = filterZoneTierOrTierPriceItems(scLoi);
			getManagedBean().setScLoiLoaItems(filteredMaxPriceItemsbyQty);
		}

		// calculate max price items only.
		boolean isCalculateMaxPriceItem = Boolean.TRUE;
		BigDecimal quantity = BigDecimal.valueOf(0d);
		// calc after nego total
		if (scLoi.getScLoiLoaItems() != null
				&& scLoi.getScLoiLoaItems().size() > 0) {
			BigDecimal subTotal = BigDecimal.valueOf(0d);
			BigDecimal totalAmount = BigDecimal.valueOf(0d);
			List<ScLoiLoaItem> scLoiLoaItems = scLoi.getScLoiLoaItems();
			for (ScLoiLoaItem scLoiLoaItem : scLoiLoaItems) {
				quantity = scLoiLoaItem.getQty();
				if(null == scLoiLoaItem.getTplSpecTierQty() ){
					// with out Tier quantity
					//quantity = scLoiLoaItem.getQty();
				} else{
					if(filteredMaxPriceItemsbyQty.contains(scLoiLoaItem)){
						isCalculateMaxPriceItem = Boolean.TRUE;
					} else{
						isCalculateMaxPriceItem = Boolean.FALSE;
					}
				}

				if(isCalculateMaxPriceItem){
					subTotal = TemplateSpecHelper.getInstance().calculateTotalAmount(
							scLoiLoaItem.getScRequestItem().getTplSpecQuestion(),
							scLoiLoaItem.getUnitPrice(), quantity);

					scLoiLoaItem.setAfterNegoSubTotal(subTotal);
					totalAmount = totalAmount.add(subTotal);	
				}
			}
			scLoi.setScLoiLoaItems(scLoiLoaItems);
			scLoi.setAfterNegoTotal(totalAmount);
			scLoi.setLoiAmt(totalAmount);
		}
		getManagedBean().setScLoi(scLoi);
	}

	/**
	 * Check nego term price.
	 * 
	 * @param scLoi
	 *            the sc loi
	 * @return true, if successful
	 */
	private boolean checkNegoTermPrice(ScLoi scLoi) {
		boolean termPriceFound = false;
		// ScLoi scLoi = getManagedBean().getScLoi();
		if (scLoi.getScNegoTerms() != null && scLoi.getScNegoTerms().size() > 0) {
			for (ScLoiTerm term : scLoi.getScNegoTerms()) {
				if (QTConstants.LOI_NEGO_TERM_PRICE.equals(term.getTermDesc())) {
					termPriceFound = true;
				}
			}
		}
		return termPriceFound;
	}

	/**
	 * Removes the nego term price.
	 * 
	 * @param scLoi
	 *            the sc loi
	 */
	private void removeNegoTermPrice(ScLoi scLoi) {
		List<ScLoiTerm> scNegoTerms = new ArrayList<ScLoiTerm>();
		if (scLoi.getScNegoTerms() != null && scLoi.getScNegoTerms().size() > 0) {
			for (ScLoiTerm term : scLoi.getScNegoTerms()) {
				if (!QTConstants.LOI_NEGO_TERM_PRICE
						.equals(term.getTermDesc())) {
					scNegoTerms.add(term);
				}
			}
			scLoi.setScNegoTerms(scNegoTerms);
		}
	}

	/**
	 * Gets the award supplier list drop down.
	 * 
	 * @return the award supplier list drop down
	 */
	public List<SelectItem> getAwardSupplierListDropDown() {
		List<ScQtRecommend> selectedRecommendList = getManagedBean()
				.getSelectedRecommendList();
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		if (selectedRecommendList != null) {
			List<Long> supplierIds = new ArrayList<Long>();

			for (ScQtRecommend scQtRecommend : selectedRecommendList) {
				// 26247 - start
				Map<Long, SmSupplier> supplierMap = new HashMap<>();
				for (ScQtEvaluationResultDtl resultDtl : scQtRecommend.getAwardSupplierList()) {
					SmSupplier supplier = resultDtl.getQtEvaluationResult().getScQtProposal().getScQtSupplier()
							.getSmSupplier();
					supplierMap.put(supplier.getSupplierId(), supplier);
				}
				// 26247 - end
				for (ScQtRecommendDtl scQtRecommendDtl 
							: scQtRecommend.getRecommendDtls()) {
					if (BooleanUtils.isTrue(
							scQtRecommendDtl.getLoiAwarded())) {
						SmSupplier smSupplier = scQtRecommendDtl.getSupplier();
						Long supplierId = smSupplier.getSupplierId();
						// 26247 - start
						if (this.disabledQTProcess(supplierMap.get(supplierId))) {
							continue;
						}
						// 26247 - end
						if (!supplierIds.contains(supplierId)
								&& !checkDisqualifiedSupplierBranchDeleted(this.getManagedBean().getScQt().getQtId(),
										supplierId)
										//#21692 - Start
								&& (scQtRecommendDtl.getScQtProposalAmount().getScQtProposal()
										.getIsAgreeQtExtendPeriod() == null
										|| scQtRecommendDtl.getScQtProposalAmount().getScQtProposal()
												.getIsAgreeQtExtendPeriod()
												.equals(SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_ACCEPTED) || scQtRecommendDtl
										.getScQtProposalAmount().getScQtProposal().getIsAgreeQtExtendPeriod()
										.equals(SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_PENDING_RESPONSE))) 
						//#21692 - End
						{// #28777 & #29729
							selectItems.add(new SelectItem(supplierId, scQtRecommendDtl.getScQtProposalAmount()
									.getScQtProposal().getSupplierNo().toString()));
							supplierIds.add(supplierId);
						}
					}
				}
			}
		}

		return selectItems;
	}

	/**
	 * Recommend supplier selected action.
	 * 
	 * @param scQtRecommendDtl
	 *           	the selected supplier
	 */
	public void recommendSupplierSelectedAction(ScQtRecommend scQtRecommend,
												ScQtRecommendDtl scQtRecommendDtl) {
		scQtRecommendDtl.setRecordStatusFlg(
				scQtRecommendDtl.isRecordStatus() ? DbValueConst.RECORD_STATUS_ACTIVE
						: DbValueConst.RECORD_STATUS_SUSPENDED);
		
		PmParameter itemType = scQtRecommend.getRequestItem()
				.getTplSpecQuestion().getTplSpecMaster().getItemTypeCode();
		boolean isItemTypeService = StringUtils.equals(ParameterConst.ITEM_TYPE_SERVICE,
										itemType.getParameterCode());
		
		if (scQtRecommend.getActiveSupplierCount() == 0) {
			scQtRecommendDtl.setAwardQty(
					scQtRecommend.getOrderQty());
		} else {
			if (scQtRecommendDtl.getAwardQty() == null) {
				scQtRecommendDtl.setAwardQty(new BigDecimal(0));
			}
		}
		
		// If item type is service, then disable multiple award
		if (isItemTypeService && 
				scQtRecommendDtl.isRecordStatus()) {
			for (ScQtRecommendDtl recommendDtl 
						: scQtRecommend.getRecommendDtls()) {
				if (!recommendDtl.equals(scQtRecommendDtl) && 
						BooleanUtils.isTrue(
								recommendDtl.getLoiAwarded())) {
					recommendDtl.setRecordStatus(false);
					recommendDtl.setRecordStatusFlg(
							DbValueConst.RECORD_STATUS_SUSPENDED);
				}
			}
		}
		updateRecommendBy(scQtRecommend);
	}

	/**
	 * Award selected supplier action.
	 */
	public void awardSelectedSupplierAction() {
		ScQtEvaluationResultDtl awardedSupplier = this.getManagedBean()
				.getSelectedSupplier();
		Long awardedSupplierId = awardedSupplier.getQtEvaluationResult()
				.getScQtProposal().getScQtSupplier().getSmSupplier()
				.getSupplierId();
		List<ScQtRecommend> recommendedItems 
								= this.getManagedBean().getRecommendList();
		if (CollectionUtils.isNotEmpty(recommendedItems)) {
			for (ScQtRecommend recommendedItem : recommendedItems) {
				for (ScQtRecommendDtl recommendDtl 
						: recommendedItem.getRecommendDtls()) {
					if (BooleanUtils.isTrue(
							recommendDtl.getLoiAwarded())) {
						Long recommendedSupplierId = recommendDtl.getSupplier()
								.getSupplierId();
						if (awardedSupplierId.equals(recommendedSupplierId)) { // suspect this the issue for Bug #25951
							recommendDtl.setRecordStatus(true);
							recommendDtl.setRecordStatusFlg(
									DbValueConst.RECORD_STATUS_ACTIVE);
						} else if (BooleanUtils.isTrue(
										recommendDtl.getLoiAwarded())) {
							recommendDtl.setRecordStatus(false);
							recommendDtl.setRecordStatusFlg(
									DbValueConst.RECORD_STATUS_SUSPENDED);
						} else {
							debugLogger("awardSelectedSupplierAction-->recommendDtlId:" + recommendDtl.getRecommendDtlId() +", status not update: " + recommendDtl.getRecordStatusFlg());
						}
					}
				}
			}
		}
		
	}

	/**
	 * Event to update offered price by the supplier when the value of quantity
	 * changes, where Recommend Type is Itemized, item Recommend By is Multiple
	 * Award under Supplier Finalization tab
	 * 
	 * @param recommendDtl
	 *            the recommendDtl to set
	 */
	public void updateOfferedPrice(ScQtRecommend scQtRecommend, ScQtRecommendDtl recommendDtl) {
		debugLogger("updateOfferedPrice...");

		SmSupplier currentSupplier = recommendDtl.getSupplier();
		List<ScQtEvaluationResultDtl> recommendedSupplierList
											= scQtRecommend.getAwardSupplierList();
		if (currentSupplier != null
				&& CollectionUtils.isNotEmpty(recommendedSupplierList)) {
			for (ScQtEvaluationResultDtl evalResultDtl : recommendedSupplierList) {
				SmSupplier shortListedSupplier = evalResultDtl
						.getQtEvaluationResult().getScQtProposal()
						.getScQtSupplier().getSmSupplier();
				ScQtProposalItem proposalItem = evalResultDtl
						.getQtEvaluationResult().getScQtProposalItem();
				
				if (proposalItem != null
						&& (currentSupplier.getSupplierId())
								.equals(shortListedSupplier.getSupplierId())) {
					BigDecimal offeredQuantity = null;
					if (recommendDtl.getAwardQty() == null) {
						offeredQuantity = new BigDecimal(0);
					} else {
						offeredQuantity = recommendDtl.getAwardQty();
					}
					offeredQuantity = offeredQuantity.setScale(2, BigDecimal.ROUND_HALF_EVEN);
					
					//Bug #36417 - start
					BigDecimal finalPrice = proposalItem.getFinalPrice(); 
					finalPrice = finalPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
					
					ScRequestItem requestItem = recommendDtl
							.getScQtRecommend().getRequestItem();
					
					BigDecimal offeredPrice = TemplateSpecHelper
							.getInstance().calculateTotalAmount(
									requestItem.getTplSpecQuestion(),
									finalPrice, offeredQuantity);
					//Bug #36417 - end
					if (offeredPrice != null) {
						offeredPrice = offeredPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
					}
					evalResultDtl.getQtEvaluationResult()
								 .setOfferedPrice(offeredPrice);
				}
			}
		}
	}	

	/**
	 * Update recommend by.
	 * 
	 * @param scQtRecommend
	 *            the sc qt recommend
	 */
	private void updateRecommendBy(ScQtRecommend scQtRecommend) {
		int activeSupplierCount = 0;
		for (ScQtRecommendDtl recommendDtl 
						: scQtRecommend.getRecommendDtls()) {
			if (BooleanUtils.isTrue(
					recommendDtl.getLoiAwarded()) && 
						recommendDtl.isRecordStatus()) {
				++activeSupplierCount;
			}
		}
		scQtRecommend.setActiveSupplierCount(activeSupplierCount);
//		String awardType = ParameterConst.RECOMMEND_BY_SINGLE_AWARD;
		
//		TplSpecQuestion specQuestion = scQtRecommend.getRequestItem().getTplSpecQuestion();
//		boolean isPriceTypeTier = TemplateSpecHelper.getInstance().isPriceTypeTier(
//				specQuestion.getClPriceTypeCode().getParameterCode());
//		if (isPriceTypeTier || 
//				!this.getManagedBean().isItemized()) {
//			awardType = ParameterConst.RECOMMEND_BY_AWARD;
//		}
//		String recommendBy = activeCount > 1 
//				? ParameterConst.RECOMMEND_BY_MULTIPLE_AWARD : awardType;
//		scQtRecommend.setRecommendByStr(recommendBy);
//		scQtRecommend.setRecommendBy(ParameterHelper.getInstance()
//				.getParameterByTypeAndCode(ParameterTypeConst.FINALIZATION_RECOMMEND_BY, recommendBy));
		
		if (CollectionUtils.isEmpty(
				scQtRecommend.getAwardSupplierList())) {
			setRecommendedSupplierList(scQtRecommend);
		}
	}

	/**
	 * Sets the recommended supplier list.
	 *
	 * @param scQtRecommend the new recommended supplier list
	 */
	private void setRecommendedSupplierList(ScQtRecommend scQtRecommend) {
		boolean isSuccessful = false;
		List<ScQtEvaluationResultDtl> shortlistedSupplierList = null;
		List<ScQtEvaluationResultDtl> disqualifiedSupplierList = new ArrayList<>();// #28777&#29729
		boolean isItemized = this.getManagedBean().isItemized();
		
		if (CollectionUtils.isEmpty(scQtRecommend.getShortListedSupplierList())
				|| CollectionUtils.isEmpty(scQtRecommend.getNotAwardSupplierList())) { // #28777&#29729
			
			// Retrieve the suppliers short listed for the selected item
			SuppFinalizationResDTO resDTO = null;
			if (isItemized) {
				resDTO = this.getShortListedSuppliersByItem(scQtRecommend);	
				shortlistedSupplierList = resDTO.getQualifiedByItemSuppliers();
				disqualifiedSupplierList = resDTO.getDisqualifiedByItemSuppliers();// #28777&#29729
			} else {
				resDTO = this.getShortListedSuppliersByPkg(
									scQtRecommend.getTplSpecZoneDtl());
				shortlistedSupplierList = resDTO.getQualifiedByPackageSuppliers();
				disqualifiedSupplierList = resDTO.getDisqualifiedByPackageSuppliers();// #28777&#29729
			}
			
			isSuccessful = resDTO.isSuccessful();
			scQtRecommend.setShortListedSupplierList(shortlistedSupplierList);
			scQtRecommend.setNotAwardSupplierList(disqualifiedSupplierList);// #28777&#29729
		} else {
			isSuccessful = true;
			shortlistedSupplierList = scQtRecommend
					.getShortListedSupplierList();
			disqualifiedSupplierList = scQtRecommend.getNotAwardSupplierList();// #28777&#29729
		}
		if (isSuccessful) {
			
			// set the checkbox or radio button of supplier selected
			// Multiple LOI or Online Bidding
			List<String> recommendDtlIds = getManagedBean().getRecommendDtlIds();
			Map<Long,ScQtRecommendDtl> recommendDtlsMap = new HashMap<Long,ScQtRecommendDtl>();
			for (ScQtRecommendDtl scQtRecommendDtl 
										: scQtRecommend.getRecommendDtls()) {
				
				// Skip the rejected suppliers
				if (BooleanUtils.isTrue(
						scQtRecommendDtl.getLoiAwarded())) {
					Long supplierId = scQtRecommendDtl.getSupplier().getSupplierId();
					recommendDtlsMap.put(supplierId, scQtRecommendDtl);
				}
			}
			scQtRecommend.setAwardSupplierList(
							new ArrayList<ScQtEvaluationResultDtl>());
			if (CollectionUtils.isNotEmpty(shortlistedSupplierList)) {
				for (ScQtEvaluationResultDtl evaluationResultDtl : shortlistedSupplierList) {
					// 26247 - start
					ScQtProposal qtProposal = evaluationResultDtl.getQtEvaluationResult().getScQtProposal();
					ScQt scQt = qtProposal.getScQtSupplier().getScQt();
					qtProposal.getScQtSupplier().setSmSupplier(
							this.getQTDetailSO().evaluateDisciplinaryActions(qtProposal.getScQtSupplier().getSmSupplier(),
									scQt.getQtId()));
					SmSupplier supplier = qtProposal.getScQtSupplier().getSmSupplier();
					Long supplierId = supplier.getSupplierId();
					// 26247 - end
					if (recommendDtlsMap.containsKey(supplierId)) {
						if (evaluationResultDtl.getScQtRecommendDtl() == null) {
							evaluationResultDtl.setScQtRecommendDtl(
										new ArrayList<ScQtRecommendDtl>());
						}
						ScQtRecommendDtl recommendDtl = recommendDtlsMap.get(supplierId);
						// 26247 - start //#21692 - Start
						if (Integer.valueOf(SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_REJECTED).equals(
								evaluationResultDtl.getQtEvaluationResult().getScQtProposal()
										.getIsAgreeQtExtendPeriod()) || this.disabledQTProcess(supplier)) {
							recommendDtl.setRecordStatus(Boolean.FALSE);
							recommendDtl.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
						}
						// 26247 - end //#21692 - End
						evaluationResultDtl.getScQtRecommendDtl()
							.add(recommendDtl);						
						
						if (recommendDtl != null && recommendDtlIds.contains(String.valueOf(recommendDtl.getRecommendDtlId()))) {
							scQtRecommend.getAwardSupplierList()
							 .add(evaluationResultDtl);
						}						
						
						if (isItemized) {
							calculateOfferedPrice(
									evaluationResultDtl,scQtRecommend.getRequestItem());	
						} 
					}
				}				
			}		
			
			// #28777 & #29729 - Start
			Map<Long,ScQtRecommendDtl> recommendDtlsDisMap = new HashMap<>();
			for (ScQtRecommendDtl scQtRecommendDtl : scQtRecommend.getRecommendDtls()) {
				Long supplierId = scQtRecommendDtl.getSupplier().getSupplierId();
				recommendDtlsDisMap.put(supplierId, scQtRecommendDtl);
			}
			scQtRecommend.setNotAwardSupplierList(new ArrayList<ScQtEvaluationResultDtl>());
			if (CollectionUtils.isNotEmpty(disqualifiedSupplierList)) {
				for (ScQtEvaluationResultDtl evaluationResultDtl : disqualifiedSupplierList) {
					Long supplierId = evaluationResultDtl.getQtEvaluationResult().getScQtProposal().getScQtSupplier()
							.getSmSupplier().getSupplierId();
					if (recommendDtlsDisMap.containsKey(supplierId)) {
						if (evaluationResultDtl.getScQtRecommendDtl() == null) {
							evaluationResultDtl.setScQtRecommendDtl(new ArrayList<ScQtRecommendDtl>());
						}
						ScQtRecommendDtl recommendDtl = recommendDtlsDisMap.get(supplierId);
						evaluationResultDtl.getScQtRecommendDtl().add(recommendDtl);

						if (recommendDtl != null
								&& recommendDtlIds.contains(String.valueOf(recommendDtl.getRecommendDtlId()))) {
							scQtRecommend.getNotAwardSupplierList().add(evaluationResultDtl);
						}

						if (isItemized) {
							if (!scQtRecommend.getNotAwardSupplierList().contains(evaluationResultDtl)) {
							    scQtRecommend.getNotAwardSupplierList().add(evaluationResultDtl);
							}
							calculateOfferedPrice(evaluationResultDtl, scQtRecommend.getRequestItem());
						}
					}
				}
			}
			// #28777 & #29729 - End
			
		}
		
	}

	/**
	 * Calculate offered price.
	 *
	 * @param evalResultDtl the eval result dtl
	 * @param requestItem the request item
	 * @return the big decimal
	 */
	private BigDecimal calculateOfferedPrice(ScQtEvaluationResultDtl evalResultDtl,
			ScRequestItem requestItem) {
		BigDecimal offeredPrice = BigDecimal.valueOf(0d);
		
		ScQtProposalItem proposalItem = null;
		ScQtRecommend recommend = null;
		BigDecimal awardQuantity = null;
		
		List<ScQtRecommendDtl> recommendDtls = evalResultDtl.getScQtRecommendDtl();
		for (ScQtRecommendDtl recommendDtl : recommendDtls) {
			if (recommendDtl.getScQtRecommend().getRequestItem()
					.getItemName().equalsIgnoreCase(requestItem.getItemName())) {
					
				proposalItem = recommendDtl.getScQtProposalItem();
				recommend = recommendDtl.getScQtRecommend();
				awardQuantity = recommendDtl.getAwardQty();
				break;
			}
		}

		if (proposalItem != null && recommend != null) {
			if (awardQuantity == null) {
				awardQuantity = recommend.getOrderQty();
			}			
			awardQuantity = awardQuantity.setScale(2, BigDecimal.ROUND_HALF_EVEN);
			
			BigDecimal finalPrice = proposalItem.getFinalPrice();
			finalPrice = finalPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
			
			offeredPrice = TemplateSpecHelper.getInstance()
					.calculateTotalAmount(requestItem.getTplSpecQuestion(),
							finalPrice, awardQuantity);
			
			if (offeredPrice != null) {
				offeredPrice = offeredPrice.setScale(2, BigDecimal.ROUND_HALF_EVEN);
			}
			evalResultDtl.getQtEvaluationResult().setOfferedPrice(offeredPrice);
		}

		return offeredPrice;
	}

	/**
	 * Gets the short listed suppliers by pkg.
	 *
	 * @param tplSpecZoneDtl the tpl spec zone dtl
	 * @return the short listed suppliers by pkg
	 */
	private SuppFinalizationResDTO getShortListedSuppliersByPkg(TplSpecZoneDtl tplSpecZoneDtl) {
		SuppFinalizationResDTO resDTO = new SuppFinalizationResDTO();
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setScQt(this.getManagedBean().getScQt());
			reqDTO.setScQtFinalization(this.getManagedBean().getScQtFinalization());
			reqDTO.setTplSpecZoneDtl(tplSpecZoneDtl);

			resDTO = qtSuppFinalizationSO
						.retrievePackagedShortlistedSupplierInfo(reqDTO);
		} finally {
			qtSuppFinalizationSO = null;
		}

		return resDTO;
	}
	
	/**
	 * Gets the short listed suppliers by item.
	 *
	 * @param scQtRecommend the sc qt recommend
	 * @return the short listed suppliers by item
	 */
	private SuppFinalizationResDTO getShortListedSuppliersByItem(
			ScQtRecommend scQtRecommend) {
		SuppFinalizationResDTO resDTO = new SuppFinalizationResDTO();
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			ScQt scQt = this.getManagedBean().getScQt();
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setScQtFinalization(scQtFinalization);
			
			if (scQt.isZonal() && 
					scQtRecommend.getTplSpecZoneDtl() != null) {
				reqDTO.setQtZoneCriteriaId(scQtRecommend.getTplSpecZoneDtl()
						.getSpecZoneId());
			}
			reqDTO.setRequestItemId(scQtRecommend.getRequestItem()
					.getRequestNoteItemId());
			reqDTO.setRecommendType(SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM);

			resDTO = qtSuppFinalizationSO
						.retrieveEvaluationResultsByItem(reqDTO);
		} finally {
			qtSuppFinalizationSO = null;
		}

		return resDTO;
	}
	
	/**
	 * Set the selected supplier details in managed bean associated with the
	 * current user session
	 * 
	 * @param scQtRecommend
	 *            the scQtRecommend which holds the item
	 * @param selectedRecommendDtl
	 *            the selectedRecommendDtl while holds the recommended supplier
	 */
	public void showSupplierDetails(ScQtEvaluationResultDtl supplierDtl) {
		debugLogger("showSuppDetails");

		resetCompanyInfo();

		if (supplierDtl != null) {
			SmSupplier smSupplier = supplierDtl
					.getQtEvaluationResult().getScQtProposal()
					.getScQtSupplier().getSmSupplier();
			if (smSupplier != null) {
				QTSupplierResDTO resDTO = this
						.retrieveSupplierDetails(smSupplier);
				smSupplier = resDTO.getSmSupplier();
				getManagedBean().setSmSupplier(smSupplier);
				getManagedBean().setSmMofAccount(
						smSupplier.getSmMofAccounts().get(0));

				SmCompanyBasic smCompanyBasic = smSupplier.getSmAppl()
						.getSmCompanyBasic();
				getManagedBean().setSmCompanyBasic(smCompanyBasic);

				List<SmPersonnel> smPersonnelList = smSupplier.getSmAppl()
						.getSmPersonnels();
				for (SmPersonnel smPersonnel : smPersonnelList) {
					if (smPersonnel.isContactPerson()) {
						getManagedBean().setSmPersonnel(smPersonnel);
						break;
					}
				}
				for (SmCompanyAddress smCompanyAddress : smCompanyBasic
						.getSmCompanyAddresses()) {
					if (AddressType.CONTACT_ADDRESS.value().equals(
							smCompanyAddress.getAddressType())) {
						getManagedBean().setSupplierBranch(
								QTConstants.SUPPLIER_INFO_HQ);
						getManagedBean().setSupplierAddress(
								smCompanyAddress.getSmAddress());
						break;
					}
				}
			}
		}
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
	 * Award supplier event.
	 */
	public void awardSupplierEvent() {
		Long awardSupplierId = getManagedBean().getAwardSupplierId();
		List<ScQtRecommend> selectedRecommendList = getManagedBean()
				.getSelectedRecommendList();

		if (selectedRecommendList != null && awardSupplierId != null) {
			for (ScQtRecommend scQtRecommend : selectedRecommendList) {
				for (ScQtRecommendDtl scQtRecommendDtl 
							: scQtRecommend.getRecommendDtls()) {
					Long supplierId = scQtRecommendDtl.getSupplier()
							.getSupplierId();

					if (awardSupplierId.equals(supplierId)) {
						scQtRecommendDtl.setRecordStatus(true);
						recommendSupplierSelectedAction(scQtRecommend, scQtRecommendDtl);
					}
				}
			}
		}
	}

	/**
	 * Calc sub total price. calculating subtotal for item list in award
	 * details.
	 * 
	 * @param scLoiLoaItem
	 *            the sc loi loa item
	 * @return the big decimal
	 */
	public BigDecimal calcSubTotalPrice(ScLoiLoaItem scLoiLoaItem) {
		BigDecimal total = TemplateSpecHelper.getInstance().calculateTotalAmount(
				scLoiLoaItem.getScRequestItem().getTplSpecQuestion(),
				scLoiLoaItem.getUnitPrice(), scLoiLoaItem.getQty());

		/*
		 * BigDecimal total = scLoiLoaItem.getUnitPrice();
		 * 
		 * if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
		 * .getClUomFrequency() != null) { total =
		 * total.multiply(scLoiLoaItem.getScRequestItem()
		 * .getTplSpecQuestion().getClUomFrequency()); }
		 * 
		 * if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
		 * .getClUomPerDay() != null) { total =
		 * total.multiply(scLoiLoaItem.getScRequestItem()
		 * .getTplSpecQuestion().getClUomPerDay()); }
		 * 
		 * if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
		 * .getClUomPerMonth() != null) { total =
		 * total.multiply(scLoiLoaItem.getScRequestItem()
		 * .getTplSpecQuestion().getClUomPerMonth()); }
		 * 
		 * if (scLoiLoaItem.getScRequestItem().getTplSpecQuestion()
		 * .getClDurationMonth() != null) { total =
		 * total.multiply(scLoiLoaItem.getScRequestItem()
		 * .getTplSpecQuestion().getClDurationMonth()); }
		 * 
		 * total = total.multiply(scLoiLoaItem.getQty());
		 */

		return total;
	}

	/**
	 * Gpki sign action. Authenticating gpki before update bpm.
	 * 
	 * @param digiSign
	 *            the digi sign
	 */
	public void gpkiSignAction(PmDigiSign digiSign) {
		updateBPMTask(this.getManagedBean().getAction(), digiSign);
	}

	/**
	 * Spki sign action.
	 * 
	 * @param digiSign
	 *            the digi sign
	 */
	public void spkiSignAction(PmDigiSign digiSign) {
		updateBPMTask(this.getManagedBean().getAction(), digiSign);
	}

	/**
	 * Gpki sign multi loi action. Authenticating gpki before update bpm.
	 * 
	 * @param digiSign
	 *            the digi sign
	 */
	public void gpkiSignMultiLoiAction(PmDigiSign digiSign) {
		updateMultiLoiBpmTask(this.getManagedBean().getAction(), digiSign);
	}

	/**
	 * Gets the pm digi sign.
	 * 
	 * @return the pm digi sign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	/**
	 * Sets the pm digi sign.
	 * 
	 * @param pmDigiSign
	 *            the new pm digi sign
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * Gets the text before sign. prepare sign in data for gpki authentication.
	 * 
	 * @return the text before sign
	 */
	public String getTextBeforeSign() {
		StringBuilder sb = new StringBuilder();
		if (getManagedBean().getScLoi() != null) {
			sb.append(getManagedBean().getScLoi().getLoiNo());
		}
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		textBeforeSign = sb.toString();
		return textBeforeSign;
	}

	/**
	 * Sets the text before sign.
	 * 
	 * @param textBeforeSign
	 *            the new text before sign
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	/**
	 * Checks if is flag.
	 * 
	 * @return true, if is flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * Sets the flag.
	 * 
	 * @param flag
	 *            the new flag
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	/**
	 * Reset company info.
	 */
	private void resetCompanyInfo() {
		this.getManagedBean().setSmSupplier(null);
		this.getManagedBean().setSmMofAccount(null);
		this.getManagedBean().setSmCompanyBasic(null);
		this.getManagedBean().setSmPersonnel(null);
		this.getManagedBean().setSupplierBranch(null);
		this.getManagedBean().setSupplierAddress(null);
	}
	
	/**
	 * Update pending loi loas.
	 *
	 * @param recSupplierList the rec supplier list
	 * @param docType the doc type
	 * @return true, if successful
	 */
	private boolean updatePendingLoiLoas(List<ScLoiLoa> recSupplierList, String docType){
		boolean isLoiRequired = Boolean.FALSE;
		boolean isLoiLoasUpdate = Boolean.FALSE;
		for (ScLoiLoa scLoiLoa : recSupplierList) {
			if(null == scLoiLoa.getLoiRequired() || !scLoiLoa.isLoiRequiredDisabled()){
				isLoiRequired = Boolean.TRUE;
				break;
			}
		}

		if (!ParameterConst.RECOMMEND_BY_MULTIPLE_LOI.equals(docType) && isLoiRequired) {
			isLoiLoasUpdate = Boolean.TRUE;
		}
		return isLoiLoasUpdate;
	}
	
	/**
	 * Localize date.
	 *
	 * @param date the date
	 * @param locale the locale
	 * @return the string
	 */
	private static String getLocalizeDate(Date date, Locale locale) { 
		String DEFAULT_DATE_FORMAT_WITH_MONTH_NAME = "dd MMMM yyyy";
	    SimpleDateFormat localDateFormate = new SimpleDateFormat(DEFAULT_DATE_FORMAT_WITH_MONTH_NAME, locale);       
	    
	    return localDateFormate.format(date);
	}
	

	/**
	 * Disable is loi required.
	 *
	 * @param recSupplierList the rec supplier list
	 */
	/*
	 * private void disableIsLoiRequired(List<ScLoiLoa> recSupplierList){
	 * boolean isRequiredSubmitBtn = Boolean.TRUE; for (ScLoiLoa scLoiLoa :
	 * recSupplierList) {
	 * 
	 * if(null == scLoiLoa.getLoiRequired()){
	 * scLoiLoa.setLoiRequiredDisabled(Boolean.FALSE); } else{
	 * scLoiLoa.setLoiRequiredDisabled(Boolean.TRUE); }
	 * 
	 * if (null != scLoiLoa.getLoiRequired()) { if(scLoiLoa.getLoiRequired() &&
	 * scLoiLoa.isLoiLoaActive()){ isRequiredSubmitBtn = Boolean.FALSE; }
	 * 
	 * } else{ isRequiredSubmitBtn = Boolean.FALSE; } }
	 * 
	 * if (isRequiredSubmitBtn) { getManagedBean().setLoiRequired(Boolean.TRUE);
	 * } else { getManagedBean().setLoiRequired(Boolean.FALSE); } }
	 */
	
	/**
	 * Validate deduction payment dtls.
	 *
	 * @param scLoi the sc loi
	 */
	public void validateOthersDeductionPay(ScLoi scLoi) {
		
		validateDeductionPaymentDtls(scLoi.getScQtDeductions());
	}
	
	/**
	 * Gets the qt proposal valid expired message.
	 *
	 * @param scQt the sc qt
	 * @return the qt proposal valid expired message
	 */
	private void getQtProposalValidExpiredMessage(ScQt scQt){
		if(scQt != null && scQt.getProposalValidityEndDate() != null){
			if(!checkProposalValidityDateReach(scQt.getProposalValidityEndDate())){
				StringBuilder qtProposalExpiredMsg = new StringBuilder();
				SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
				String proposalValidityEndDate = sdf.format(scQt.getProposalValidityEndDate());
				Locale locale = new FacesScopes().getUserLocale();
				qtProposalExpiredMsg.append(ResourceBundleUtils.getMessage("lbl.sc.qt.proposal.val.expired",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
				qtProposalExpiredMsg.append(" ");
				qtProposalExpiredMsg.append(proposalValidityEndDate);

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
								.getMessage(
										qtProposalExpiredMsg.toString(),
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										locale)));
			}
		}
	}
	
	/**
	 * AckDueDate changed
	 */
	public void dueDateChange(){
		debugLogger("dueDateChange");
		ScLoi scLoi = this.getManagedBean().getScLoi();
		Date today = scLoi.getIssueDate();
		int responseDay = scLoi.getLoiResponseDate();
		Date ackDueDate = DateUtils.addDays(today, responseDay);
		
		scLoi.setAckDueDate(ackDueDate);
		/*IParameterSO parameterSO = null;
		
		try{
			Long parameterId = (long) scLoi.getLoiResponseDate();
			
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			PmParameter pmParameter = resDTO.getPmParameter();
			String responseDayStr = pmParameter.getParameterCode();
			
		} finally{
			parameterSO = null;
		}*/
	}
	
	/**
	 * The Class NegotiationRecommendationReportDTO.
	 */
	public class NegotiationRecommendationReportDTO {
		private List<ScQtEvaluationResultDtl> awardedSuppliers;
		private List<ScQtRecommend> recommendedItems;
		private List<ScLoiLoaItem> loiLoaItems;
		private String afterNegoSubTotal;
		private boolean empty;
		
		public NegotiationRecommendationReportDTO() {}
		
		/**
		 * @return the awardedSuppliers
		 */
		public List<ScQtEvaluationResultDtl> getAwardedSuppliers() {
			return awardedSuppliers;
		}

		/**
		 * @param awardedSuppliers 
		 * 				the awardedSuppliers to set
		 */
		public void setAwardedSuppliers(
						List<ScQtEvaluationResultDtl> awardedSuppliers) {
			this.awardedSuppliers = awardedSuppliers;
		}

		/**
		 * @return the recommendedItems
		 */
		public List<ScQtRecommend> getRecommendedItems() {
			return recommendedItems;
		}
		
		/**
		 * @param recommendedItems 
		 * 				the recommendedItems to set
		 */
		public void setRecommendedItems(List<ScQtRecommend> recommendedItems) {
			this.recommendedItems = recommendedItems;
		}

		/**
		 * @return the loiLoaItems
		 */
		public List<ScLoiLoaItem> getLoiLoaItems() {
			return loiLoaItems;
		}

		/**
		 * @param loiLoaItems 
		 * 				the loiLoaItems to set
		 */
		public void setLoiLoaItems(List<ScLoiLoaItem> loiLoaItems) {
			this.loiLoaItems = loiLoaItems;
		}

		/**
		 * @return the afterNegoSubTotal
		 */
		public String getAfterNegoSubTotal() {
			return afterNegoSubTotal;
		}

		/**
		 * @param afterNegoSubTotal 
		 * 				the afterNegoSubTotal to set
		 */
		public void setAfterNegoSubTotal(String afterNegoSubTotal) {
			this.afterNegoSubTotal = afterNegoSubTotal;
		}

		/**
		 * @return the empty
		 */
		public boolean isEmpty() {
			return empty;
		}

		/**
		 * @param empty the empty to set
		 */
		public void setEmpty(boolean empty) {
			this.empty = empty;
		}
		
	}
	
	
	/**
	 * Deletebutton negotation result disable.
	 *
	 * @param list the list
	 * @return true, if successful
	 */
	public  boolean deletebuttonNegotationResultDisable(
			List<ScLoiTerm> list) {
		if (list != null) {
			for (ScLoiTerm entity : list) {
				if(entity.isSelectTerm()){
					return false;
				}
				
			}
		}
		return true;
	}

	/**
	 * Retrivemessage.
	 *
	 * @param date the date
	 * @return the string
	 */
	public String retrivemessage(Date date){
		String message="";
		if(null!=date){
			long dayBal = DateUtils.getDaysDifference(getManagedBean().getScLoi().getIssueDate(),
					date);
			String[] dayLeftParam = { dayBal + "" };
			 message= getMessage(
					"lbl.sc.loiloa.loi.res.due.date.tooltip", dayLeftParam);
			
		}
		return message;
		
	}
	
	/**
	 * The method to used auto update of request.
	 */
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);
	}
	
	/**
	 * Checks if is loi required disabled.
	 *
	 * @param scLoiLoa the sc loi loa
	 * @return true, if is loi required disabled
	 */
	public boolean isLoiRequiredDisabled(ScLoiLoa scLoiLoa){
		// 36293 - start
		if (scLoiLoa.isLoiRequiredDisabled()) {
			return Boolean.TRUE;
		}
		// 36293 - end

		if(CollectionUtils.isNotEmpty(scLoiLoa.getScLois())){
			ScLoi scLoi =  scLoiLoa.getScLois().get(0);
			if(scLoi.getScWorkFlowStatus() != null){
				Long statusId = scLoi.getScWorkFlowStatus().getPmStatus().getStatusId();//20230711@Asyrafhaziq-SQUBE Bug Minor #16
				
				if(QTStatusConst.LOI_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId) ||
						QTStatusConst.LOI_PENDING_APPROVAL.equals(statusId)){
					return Boolean.TRUE;
				}
			}
		}
		
		if(scLoiLoa.isLoiCreated()){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	private void retrieveBriefingPaper() {
		
		SupplierFinalizationBiddingBackingBean biddingBackingBean = new SupplierFinalizationBiddingBackingBean();
		biddingBackingBean.getManagedBean().setScQtFinalization(
				this.getManagedBean().getScQtFinalization());
		biddingBackingBean.getManagedBean().setScQt(
				this.getManagedBean().getScQt());
		biddingBackingBean.getManagedBean()
				.setBpkUser(
						StringUtils.isNotBlank(this.getManagedBean()
								.getCommitteeType()) ? true : false);
		
		biddingBackingBean.retrieveBriefingPaper();
	}
	
	private ScLoiAttachment getLoiAttachment(ScLoi scLoi) {
		ScLoiAttachment scLoiAttachment = scLoi.getScLoiAttachment();
		if(null == scLoiAttachment){
			scLoiAttachment = new ScLoiAttachment();
			scLoiAttachment.setScLoi(scLoi);
			scLoiAttachment.setDocNo(scLoi.getLoiNo());
			scLoiAttachment.setScLoi(scLoi);
			scLoi.setScLoiAttachment(scLoiAttachment);
		}
		return scLoiAttachment;
	}
	
	//6906 - start - fix for #436
	private void checkIsLoiProcessRequired(List<ScLoiLoa> pendingLoiLoas) {
		this.getManagedBean().setLoiRequired(false);
		boolean isAllLoiRequired = true;

		for (ScLoiLoa loiLoa : pendingLoiLoas) {
			if (!loiLoa.isLoiLoaActive()) {
				continue;
			}
			if (null == loiLoa.getLoiRequired()
					|| (loiLoa.getLoiRequired() && (!checkIfAllLoiCompleted(loiLoa.getScLois()) || !isLoiRequiredDisabled(loiLoa)))) {
				this.getManagedBean().setLoiRequired(false);
				return;
			}
			if (!loiLoa.getLoiRequired()) {
				isAllLoiRequired = false;
			}
		}
		this.getManagedBean().setLoiRequired(!isAllLoiRequired);
	}

	private boolean checkIfAllLoiCompleted(List<ScLoi> lois) {
		if (CollectionUtils.isEmpty(lois)) {
			return false;
		}
		for (ScLoi loi : lois) {
			PmStatus status = null != loi.getScWorkFlowStatus() && null != loi.getScWorkFlowStatus().getPmStatus() ? loi
					.getScWorkFlowStatus().getPmStatus() : new PmStatus();
			Long statusId = null != status.getStatusId() ? status.getStatusId() : 0L;

			if (!QTStatusConst.LOI_COMPLETED.equals(statusId)) {
				return false;
			}
		}
		return true;
	}
	//6906 - end - fix for #436

	//Enhancement #21573 To have confirmation box for reject/acknowledge button at document LOI/LOA		
	/**
	 * Update bpm task action for supplier. update bpm - save data into database and BPM
	 * process will route to the next respective user. Authenticating spki
	 * before update bpm.
	 * 
	 * @param outcome
	 *            the outcome
	 */
	public void updateBpmTaskActionSupplier(String outcome) {
		this.getManagedBean().setAction(outcome);
		
		String loi = getMessage("lbl.sc.loiloa.loi.letter", null);
		String procurementMode = ""; //20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
		String mode = this.getManagedBean().getScQt().getProcurementMode().getParameterCode();
		
		if (mode.equals(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)) {
			procurementMode = getMessage("lbl.sc.sq", null);
		} else if (mode.equals(ParameterConst.PROCUREMENT_MODE_QUOTATION)) {
			procurementMode = getMessage("lbl.rpt.sc.quotation", null);
		} else if (mode.equals(ParameterConst.PROCUREMENT_MODE_TENDER)) {
			procurementMode = getMessage("lbl.rpt.sc.tender", null);
		}		
	
		if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
			this.getManagedBean().setAutoUpdate(false);		
			
			String acceptMsg = getMessage("lbl.sc.loi.supplier.confirm.button.accept", null);
			
			this.getManagedBean().setSuppConfirmMsgAccept(acceptMsg.toString().trim());
			RequestContext.getCurrentInstance().execute("confmAcceptDlg.show()"); 
		} else if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {
			this.getManagedBean().setAutoUpdate(false);
			
			String rejectMsg = getMessage("lbl.sc.loiloa.supplier.confirm.button.reject", new String[] { loi.trim(), procurementMode.trim() });

			this.getManagedBean().setSuppConfirmMsgReject(rejectMsg.toString().trim());
			RequestContext.getCurrentInstance().execute("confmRejectDlg.show()"); 
		}
	}
	
	public void confirmSupplierAccept() {
		this.getManagedBean().setAction(BpmOutcomeConst.BPM_ACCEPT);
		log.info(":::confirmSupplierAccept() to "+ BpmOutcomeConst.BPM_ACCEPT +" LOI.");
		getManagedBean().getSpkiBackingBean().getChallengeQuestions();
		RequestContext.getCurrentInstance().execute("spki_dlg.show();");
		RequestContext.getCurrentInstance().execute("blockWidget.hide();");
	}
	
	/**
	 * BUG 33353 
	 */
	public void confirmSupplierReject() {
		this.getManagedBean().setAction(BpmOutcomeConst.BPM_REJECT);
		log.info(":::confirmSupplierReject() to "+ BpmOutcomeConst.BPM_REJECT +" LOI.");
		updateBPMTask(BpmOutcomeConst.BPM_REJECT, null);
	}
	//end Enhancement #21573

	
	public void setLoiList(List<ScLoi> loiList) {
		this.loiList = loiList;
		
	}

	public List<ScLoi> getLoiList() {
		return loiList;
	}
	
	private boolean isDeskOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.DESK_OFFICER.value());
	}
	
	private boolean isQTMaintenanceScreen() {
		return QTConstants.PORTLET_NAME_QT_MAINTENANCE.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
			|| QTConstants.PORTLET_NAME_QT_SEARCH.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId()) // ***17376#55
			|| QTConstants.PORTLET_NAME_QT_TECHSUPP.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
			;
	}
	
	public boolean getIsDeskOfficer(){
		return isDeskOfficer();
	}
	
	public boolean getIsQtMaintenanceScreen(){
		return isQTMaintenanceScreen();
	}
	
	public boolean getManagedBeanIsReadOnly(){
		return this.getManagedBean().isReadOnly();
	}
	
	// #25638 - Start  
	/**
	 * @return
	 */
	public boolean isPendingNegotiationStatus() {
	  Long statusId  = getManagedBean().getCurrentStatusId();
      if (null != statusId && (QTStatusConst.SF_PENDING_MULTIPLE_LOI_RECOMMENDATION.equals(statusId.longValue()) 
          || QTStatusConst.SF_PENDING_MULTIPLE_LOI_RECOMMENDATION_REVISION.equals(statusId.longValue()))) {
        return true;
	  } else {
	    return false;
	  }
	}
	// #25638 - End 
	//#29007	Vinod Start
	/**
	 * Adds the negotiation term in negotiation details.
	*/
	public void addSuppliersProposalSpecificationUpdate() {
		debugLogger("addSuppliersProposalSpecificationUpdate");
		ScLoi scLoi = getManagedBean().getScLoi();
		Long statusId = scLoi.getScWorkFlowStatus().getPmStatus().getStatusId();// 20230711@Asyrafhaziq-SQUBE Bug Minor #16

		List<ScLoiTerm> scNegoTerms = scLoi.getScNegoTerms();
		if (null == scNegoTerms) {
			scNegoTerms = new ArrayList<>();
		}
		ScLoiTerm negoTerm = new ScLoiTerm();

		String message = ResourceBundleUtils.getMessage(
				QTConstants.LBL_SC_LOILOA_SUPPLIERS_PROPOSAL_SPECIFICATION_UPDATE,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		boolean alredyAdded = false;
		// #37831 - #29007 Start Vinod 23/04/2025 Added to check if any description or
		// remark is not entered and not allowing user to add
		// "Kemas Kini Tawaran Spesifikasi Pembekal" record.
		boolean isDescOrRemarkEmpty = false;
		// #37831 - #29007 End Vinod 23/04/2025
		for (ScLoiTerm scLoiTerm : scNegoTerms) {
			// #37831 - #29007 Start Vinod 23/04/2025
			if(StringUtils.isEmpty(scLoiTerm.getTermDesc()) || StringUtils.isEmpty(scLoiTerm.getNegoResult())){
				isDescOrRemarkEmpty = true;
				break;
			}
			// #37831 - #29007 End Vinod 23/04/2025
			if (StringUtils.isNotEmpty(scLoiTerm.getTermDesc()) && scLoiTerm.getTermDesc().equals(message)) {// 29007
				alredyAdded = true;
				break;
			}
		}
		// #37831 - #29007 Start Vinod 23/04/2025 Added isDescOrRemarkEmpty condition
		if (!isDescOrRemarkEmpty && !alredyAdded) {// sonarQubeIssue7#84
			// #37831 - #29007 End Vinod 23/04/2025
			if (QTStatusConst.LOI_PENDING_NEGOTIATION.equals(statusId)) {
				negoTerm.setSelectedTerm(Boolean.FALSE);
			} else {
				negoTerm.setSelectedTerm(Boolean.TRUE);
				negoTerm.setSelected(true);
			}
			negoTerm.setTermDesc(message);
			negoTerm.setSelectTerm(Boolean.TRUE);// 29007
			negoTerm.setScLoi(scLoi);
			negoTerm.setModified(true);
			scNegoTerms.add(negoTerm);
			scLoi.setScNegoTerms(scNegoTerms);
		}

	}
	// #29007	Vinod End
	
	/**
	 * 29007-start
	* Method to Reset NegotiationTerms
	**/
	public void populateNegoTerms(){
		
		ScLoi scLoi =this. getManagedBean().getScLoi();

		if (CollectionUtils.isNotEmpty(scLoi.getScNegoTerms())) {//sonarQubeIssue7#84
			for (ScLoiTerm term : scLoi.getScNegoTerms()) {
				term.setSelectTerm(Boolean.FALSE);	
			}
	}
	}
	//29007-End

	//#21692 - Start
	private void isSupplierEPVStatusRejected(ScLoi scLoi) {
		boolean supplierStatus = false;

		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTDetailSO.class);
		getManagedBean().setAbleToSubmitLOI(true);
		// Only one supplier, LOI's supplier
		List<Long> suppList = new ArrayList<>();
		suppList.add(scLoi.getScLoiLoa().getSmSupplier().getSupplierId());

		// will return true if the supplier reject EPV
		supplierStatus = detailSO.checkSupplierEPVStatus(getManagedBean().getScQt().getQtId(), suppList);

		// all status stated in FRS for 21692
		List<Long> statusEnabledCancel = new ArrayList<>();
		statusEnabledCancel.add(QTStatusConst.LOI_PENDING_APPROVAL);
		statusEnabledCancel.add(QTStatusConst.LOI_PENDING_REVISION);
		statusEnabledCancel.add(QTStatusConst.LOI_PENDING_NEGOTIATION);
		statusEnabledCancel.add(QTStatusConst.LOI_PENDING_REVISION_APPROVAL);

		// check the validity of BPM version, if version is equal/higher will enable button
		boolean isValidFor21692 = this.validateSupportedTask(this.getManagedBean().getScQt().getQtNo(),
				QTConstants.ALLOW_CANCEL_LOA_LOI_FOR_EPV);

		// will hide submit LOI button if supplier reject EPV (prerendered == false)
		getManagedBean().setAbleToSubmitLOI(!supplierStatus);
		// all condition needed to show cancel button in LOI
		supplierStatus = supplierStatus
				&& isLoiListPortlet()
				&& !this.getManagedBean().getScLoi().isBrandNew() ? true : false;
		// cancel button during Pending LOI Submission only
		getManagedBean().setAbleToCancelLOIWithoutBPM(
				supplierStatus
						&& null != this.getManagedBean().getScLoi().getScWorkFlowStatus()
						&& this.getManagedBean().getScLoi().getScWorkFlowStatus().getPmStatus().getStatusId()
								.equals(QTStatusConst.LOI_PENDING_SUBMISSION));
		if (!isValidFor21692) {
			// cancel button for WIP LOI only
			getManagedBean().setAbleToCancelWIP(
					supplierStatus
							&& null != this.getManagedBean().getScLoi().getScWorkFlowStatus()
							&& statusEnabledCancel.contains(this.getManagedBean().getScLoi().getScWorkFlowStatus()
									.getPmStatus().getStatusId()));
			supplierStatus = false;
		}

		// cancel button will show/hide based on this (prerendered false/true)
		getManagedBean().setAbleToCancelLOI(
				supplierStatus
						&& null != this.getManagedBean().getScLoi().getScWorkFlowStatus()
						&& statusEnabledCancel.contains(this.getManagedBean().getScLoi().getScWorkFlowStatus()
								.getPmStatus().getStatusId()));// #36339, #21692
	}
	//#21692 - End
	
	//#21692 - Start
	private boolean isLoiListPortlet() {
		return QTConstants.PORTLET_NAME_QT_LOI_LIST.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
				|| QTConstants.PORTLET_NAME_QT_LOI_REVIEW.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	//#21692 - End
	
	//#21692 - Start
	/**
	 * Enable LOI without BPM (LOI Submission) to be cancelled for rejecting
	 * proposal validity extension
	 */
	public void cancelLOIWithoutBPM() {
		try {
			ScLoi scLoi = getManagedBean().getScLoi();
			/**
			 * Will update LOI workflow status: Cancelled (Not agree with
			 * extension validity period) and ScQtRecommendDtl: IS_LOI_AWARDED =
			 * FALSE and RECORD_STATUS = Rejected (4). IF all LOIs' status
			 * either Completed, Rejected OR Cancelled (Not agree with extension
			 * validity period), QT will exit and goes to Refinalization.
			 */
			QuationTenderResDTO resDTO = getQTDetailSO().cancelLOIWithoutBPM(scLoi);

			if (resDTO.isSuccessful()) {
				successMessage(MessageConst.INFO_700031, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_LETTER_OF_INTENT, scLoi.getLoiNo(), Boolean.TRUE);
			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError("LOI", "CancelLoiWithoutBPM", getManagedBean().getScLoi().getLoiNo(),
					"cancelLOIWithoutBPM", getUserIdSessionFromLoginId(), e);
		}
	}
	//#21692 - End
	
	//#12487 start
	/**
	 * To send notification for email and dashboard.
	 * 
	 */
	public void prepareLoiNotification(){		
	
		Map<String, Object> loiObj = new HashMap<>();
		ScQt scQt = this.getManagedBean().getScQt();
		try {		
			loiObj.put(CommonConstants.OBJ_USER_LOCALE, new FacesScopes().getUserLocale());
			loiObj.put(CommonConstants.OBJ_LANGUAGE, new FacesScopes().getUserLocale().getLanguage());
			if (getManagedBean().getScQtFinalization() != null &&
				getManagedBean().getScQtFinalization().getLoiLoaById() != null) {
					loiObj.put(CommonConstants.OBJ_ASSIGN_USER, getManagedBean().getScQtFinalization().getLoiLoaById());									
			}			
			
			Date createdDate = this.getManagedBean().getScQtFinalization().getCreatedDate();
			long profileId = scQt.getPmOrgProfile().getOrgProfileId();
			loiObj.put(CommonConstants.OBJ_PTJ_NAME, retrievePTJByOrgProfileId(profileId, createdDate));
			loiObj.put(CommonConstants.OBJ_PTJ_CODE, retrievePTJCodeByOrgProfileId(profileId, createdDate));
			PmOrgProfile pmOrgProfile = retrieveMinistryOrgProfile(profileId);
			if(null != pmOrgProfile && 
			   null != pmOrgProfile.getPmOrgValidity()){
				 loiObj.put(CommonConstants.OBJ_MINISTRY_NAME, pmOrgProfile.getPmOrgValidity().getOrgName());
				
			}			
			getManagedBean().setLoiObj(loiObj);
		} finally {
			getQtAwardSO().sendLoiNotification(loiObj,scQt, CommonConstants.DASHBOARD);
			getQtAwardSO().sendLoiNotification(loiObj, scQt, CommonConstants.EMAIL);
		}		
	}
	//#12487 end

	// #28777 & #29729 - Start
	/**
	 * Method to display list of supplier branch had deleted or not with related
	 * current QT
	 * 
	 * @param supplierId
	 **/
	public void showSupplierBranchList(Long supplierId) {

		ISupplierSearchSO supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
		ISourcingIntegrationSO sourcingIntegrationSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);

		SupplierSearchReqDTO suppSearchDeletedReqDTO = new SupplierSearchReqDTO();
		suppSearchDeletedReqDTO.setSupplierId(supplierId);
		SupplierSearchResDTO suppSearchDeletedResDTO = supplierSearchSO
				.retrieveSmSupplierBranchDeletedBySupplier(suppSearchDeletedReqDTO);
		List<SmSupplierBranch> supplierBranchListDeleted = suppSearchDeletedResDTO.getSmSupplierBranchList();

		for (SmSupplierBranch supplierBranch : supplierBranchListDeleted) {
			int statusFlag = supplierBranch.getRecordStatusFlg();
			if (statusFlag == DbValueConst.RECORD_STATUS_DELETED) {
				supplierBranch.setQtRelatedSupplierBranch(DbValueConst.FLAG_YES);
			} else {
				supplierBranch.setQtRelatedSupplierBranch(DbValueConst.FLAG_NO);
			}
		}

		List<SmSupplierBranch> wipSupplierBranchListDeleted = sourcingIntegrationSO.checkWIPSupplierBranchDeleted(supplierId);
		for (SmSupplierBranch wipSupplierBranch : wipSupplierBranchListDeleted) {
			int statusFlag = wipSupplierBranch.getRecordStatusFlg();
			if (statusFlag == DbValueConst.RECORD_STATUS_ACTIVE) {
				wipSupplierBranch.setQtRelatedSupplierBranch(DbValueConst.FLAG_YES);
			} else {
				wipSupplierBranch.setQtRelatedSupplierBranch(DbValueConst.FLAG_NO);
			}
		}

		List<SmSupplierBranch> supplierBranchListActive = listSupplierBranchList(supplierId);
		if (CollectionUtils.isNotEmpty(supplierBranchListDeleted)) {
			supplierBranchListDeleted.addAll(supplierBranchListActive);
			getManagedBean().setSupplierBranchList(supplierBranchListDeleted);
		} else {
			if (CollectionUtils.isNotEmpty(wipSupplierBranchListDeleted)) {
				wipSupplierBranchListDeleted.addAll(supplierBranchListActive);
				getManagedBean().setSupplierBranchList(wipSupplierBranchListDeleted);
			} else {
				if (CollectionUtils.isNotEmpty(supplierBranchListActive)) {
					getManagedBean().setSupplierBranchList(supplierBranchListActive);
				}
			}
		}
		
	}

	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to display list of supplier branch
	 * 
	 * @param supplierId
	 **/
	private List<SmSupplierBranch> listSupplierBranchList(Long supplierId) {

		ISupplierSearchSO supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);

		SupplierSearchReqDTO suppSearchReqDTO = new SupplierSearchReqDTO();
		suppSearchReqDTO.setSupplierId(supplierId);
		SupplierSearchResDTO suppSearchResDTO = supplierSearchSO.retrieveSmSupplierBranchBySupplier(suppSearchReqDTO);
		return suppSearchResDTO.getSmSupplierBranchList();
	}

	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to display instruction when had supplier deleted branch
	 * 
	 * @param qtId
	 * @return true or false
	 **/
	public Boolean checkInstructionSupplierBranchDeleted(Long qtId) {
		ISourcingIntegrationSO sourcingIntegrationSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
		List<TplSpecZoneDtl> zoneDetails = this.getManagedBean().getMultiLOIZoneDetails();
		int zoneActiveIndex = this.getManagedBean().getZoneActiveIndex();
		boolean isZonal = this.getManagedBean().getScQt().isZonal();

		if (isZonal && CollectionUtils.isNotEmpty(zoneDetails) && zoneActiveIndex != -1
				&& zoneDetails.get(zoneActiveIndex).isBranchDeactivated()) {
			return true;
		}

		if (!isZonal && sourcingIntegrationSO.checkInstructionDisqualifiedSupplierBranchDeleted(qtId)) {
			return true;
		}

		return false;
	}

	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to check if this supplier disqualified
	 * 
	 * @param qtId
	 * @param supplierId
	 * @return true or false
	 **/
	public Boolean checkDisqualifiedSupplierBranchDeleted(Long qtId, Long supplierId) {
		ISourcingIntegrationSO sourcingIntegrationSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
		List<TplSpecZoneDtl> zoneDetails = this.getManagedBean().getMultiLOIZoneDetails();
		int zoneActiveIndex = this.getManagedBean().getZoneActiveIndex();
		boolean isZonal = this.getManagedBean().getScQt().isZonal();

		if (isZonal
				&& CollectionUtils.isNotEmpty(zoneDetails)
				&& zoneActiveIndex != -1
				&& sourcingIntegrationSO.flagZonesDisqualifiedSupplierBranchDeleted(qtId, supplierId,
						zoneDetails.get(zoneActiveIndex).getSpecZoneId())) {
			return true;
		}

		if (!isZonal && sourcingIntegrationSO.flagDisqualifiedSupplierBranchDeleted(qtId, supplierId)) {
			return true;
		}

		return false;
	}

	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to check had disqualified supplier list
	 * 
	 * @param qtId
	 * @return true or false
	 **/
	public Boolean checkListDisqualifiedSupplierBranchDeleted(Long qtId) {
		ISourcingIntegrationSO sourcingIntegrationSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
		List<TplSpecZoneDtl> zoneDetails = this.getManagedBean().getMultiLOIZoneDetails();	
		int zoneActiveIndex = this.getManagedBean().getZoneActiveIndex();
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		
		if (isZonal && CollectionUtils.isNotEmpty(zoneDetails) && zoneActiveIndex != -1
				&& zoneDetails.get(zoneActiveIndex).isBranchDeactivated()) {
			return true;
		}

		if (!isZonal && sourcingIntegrationSO.checkListDisqualifiedSupplierBranchDeleted(qtId)) {
			return true;
		}

		return false;
	}

	// #28777 & #29729 - End
	
	// #28777 & #29729 - Start
	/**
	 * Method to check list had disqualified zones
	 * 
	 * @param qtId
	 * @param specZoneId
	 * @return true or false
	 **/
	public Boolean checkListZonesDisqualifiedSupplierBranchDeleted(Long qtId, Long specZoneId) {
		ISourcingIntegrationSO sourcingIntegrationSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
		return sourcingIntegrationSO.checkListZonesDisqualifiedSupplierBranchDeleted(qtId, specZoneId);
	}

	// #28777 & #29729 - End
	
	// #28777 & #29729 - Start
	/**
	 * Method to check if have supplier branch deleted
	 * 
	 * @param outcome
	 * 
	 **/
	private Boolean checkFlagForSupplierBranchDeleted(String outcome) {

		boolean isEndorseOrSignOff = false;
		if (outcome != null
				&& (outcome.equals(BpmOutcomeConst.BPM_ENDORSE) || outcome.equals(BpmOutcomeConst.BPM_SIGNOFF))) {
			isEndorseOrSignOff = true;
		}

		ScQt scqt = this.getManagedBean().getScQt();
		ScQtFinalization scQtFinalization = this.getManagedBean().getScQtFinalization();
		List<ScQtRecommend> recommendedItems = scQtFinalization.getScQtRecommends();

		if (isEndorseOrSignOff && CollectionUtils.isNotEmpty(recommendedItems)
				&& checkDetailForSupplierBranchDeleted(recommendedItems, scqt)) {
			return true;
		}
		return false;
	}

	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to check details if have supplier branch deleted
	 * 
	 * @param recommendedItems
	 * @param scQt
	 * 
	 **/
	private Boolean checkDetailForSupplierBranchDeleted(List<ScQtRecommend> recommendedItems, ScQt scqt) {

		for (ScQtRecommend recommend : recommendedItems) {
			TplSpecZoneDtl tplSpecZoneDtl = recommend.getTplSpecZoneDtl();
			if (tplSpecZoneDtl != null) {
				Long specZoneId = tplSpecZoneDtl.getSpecZoneId();
				if (checkListZonesDisqualifiedSupplierBranchDeleted(scqt.getQtId(), specZoneId)) {
					return true;
				}
			} else {
				if (checkListDisqualifiedSupplierBranchDeleted(scqt.getQtId())) {
					return true;
				}
			}
		}
		return false;
	}

	// #28777 & #29729 - End
	
	// #28777 & #29729 - Start
	/**
	 * Method to check outcome endorse/sign off and get recommend details
	 * 
	 * @param outcome
	 * 
	 **/
	private void checkNeedSupplierIfHaveBranchDeleted(String outcome) {
	
		boolean isEndorseOrSignOff = false;
		if (outcome != null
				&& (outcome.equals(BpmOutcomeConst.BPM_ENDORSE) || outcome.equals(BpmOutcomeConst.BPM_SIGNOFF))) {
			isEndorseOrSignOff = true;
		}
	
		ScQt scqt = this.getManagedBean().getScQt();
		ScQtFinalization scQtFinalization = this.getManagedBean().getScQtFinalization();
		List<ScQtRecommend> recommendedItems = scQtFinalization.getScQtRecommends();

		if (isEndorseOrSignOff && CollectionUtils.isNotEmpty(recommendedItems)) {
			validationNeedSupplier(recommendedItems, scqt);
		}
	}
	
	// #28777 & #29729 - End
	
	// #28777 & #29729 - Start
	/**
	 * Method to set recommend details to do identifier data either true or
	 * false
	 * 
	 * @param recommendedItems
	 * @param scQt
	 * 
	 **/
	private void validationNeedSupplier(List<ScQtRecommend> recommendedItems, ScQt scQt) {

		Map<Long, ScQtRecommend> recommendMap = new HashMap<>();
		Map<Long, Boolean> identifierMap = new HashMap<>();
		boolean isItemized = this.getManagedBean().isItemized();

		for (ScQtRecommend recommend : recommendedItems) {
			Long recommendId = recommend.getQtRecommendId();
			recommendMap.put(recommendId, recommend);
			TplSpecZoneDtl tplSpecZoneDtl = recommend.getTplSpecZoneDtl();

			if (!identifierMap.containsKey(recommendId)) {
				identifierMap.put(recommendId, false);
			}

			for (ScQtRecommendDtl recommendDtl : recommend.getRecommendDtls()) {
				Long supplierId = recommendDtl.getSupplier().getSupplierId();
				if (checkIdentifierForSupplierBranchDeleted(tplSpecZoneDtl, recommendDtl, scQt, supplierId)
						&& identifierMap.containsKey(recommendId)) {
					identifierMap.put(recommendId, true);
				}
			}
		}

		if (isItemized) {
			validationItemizedSupplier(recommendMap, identifierMap, scQt);
		} else {
			validationPackageSupplier(recommendMap, identifierMap, scQt);
		}
	}

	// #28777 & #29729 - End
	
	// #28777 & #29729 - Start
	/**
	 * Method to check flag identifier if have supplier branch deleted
	 * 
	 * @param tplSpecZoneDtl
	 * @param recommendDtl
	 * @param scQt
	 * @param supplierId
	 * 
	 **/
	private Boolean checkIdentifierForSupplierBranchDeleted(TplSpecZoneDtl tplSpecZoneDtl,
			ScQtRecommendDtl recommendDtl, ScQt scQt, Long supplierId) {
		int recordStatusFlg = recommendDtl.getRecordStatusFlg().intValue();

		if (tplSpecZoneDtl != null) {
			Long specZoneId = tplSpecZoneDtl.getSpecZoneId();
			List<Long> qualifiedZoneSupplierList = retrieveZoneSuppliersIdsByQtIdAndSpecZoneId(true, scQt.getQtId(),
					specZoneId);
			for (Long qualifiedZoneSupplier : qualifiedZoneSupplierList) {
				if (recordStatusFlg == DbValueConst.RECORD_STATUS_ACTIVE && qualifiedZoneSupplier.equals(supplierId)) {
					return true;
				}
			}

		} else {

			List<Long> qualifiedSupplierList = retrieveSuppliersIdsByQtId(true, scQt.getQtId());
			for (Long qualifiedSupplier : qualifiedSupplierList) {
				if (recordStatusFlg == DbValueConst.RECORD_STATUS_ACTIVE && qualifiedSupplier.equals(supplierId)) {
					return true;
				}
			}

		}
		return false;
	}

	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to retrieve qualified supplier to get suppliersId
	 * 
	 * @param isQualified
	 * @param qtId
	 * @return List<Long>
	 **/
	public List<Long> retrieveSuppliersIdsByQtId(boolean isQualified, Long qtId) {
		ISourcingIntegrationSO sourcingIntegrationSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
		return sourcingIntegrationSO.retrieveSuppliersIdsByQtId(isQualified, qtId);
	}

	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to retrieve qualified supplier to get suppliersIds
	 * 
	 * @param isQualified
	 * @param qtId
	 * @param specZoneId
	 * @return List<Long>
	 **/
	public List<Long> retrieveZoneSuppliersIdsByQtIdAndSpecZoneId(boolean isQualified, Long qtId, Long specZoneId) {
		ISourcingIntegrationSO sourcingIntegrationSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
		return sourcingIntegrationSO.retrieveZoneSuppliersIdsByQtIdAndSpecZoneId(isQualified, qtId, specZoneId);
	}

	// #28777 & #29729 - End
	
	// #28777 & #29729 - Start
	/**
	 * Method to display validation if no supplier for itemized
	 * 
	 * @param recommendMap
	 * @param identifierMap
	 * @param scQt
	 * 
	 **/
	private void validationItemizedSupplier(Map<Long, ScQtRecommend> recommendMap, Map<Long, Boolean> identifierMap,
			ScQt scQt) {

		boolean foundFalse = false;
		Long falseKey = null;

		for (Map.Entry<Long, Boolean> entry : identifierMap.entrySet()) {
			if (!entry.getValue()) {
				foundFalse = true;
				falseKey = entry.getKey();
				break;
			}
		}

		if (foundFalse) {
			ScQtRecommend recommend = recommendMap.get(falseKey);
			if (scQt.isZonal()) {
				setZoneAndTabIndexOnException(recommend.getTplSpecZoneDtl());
				throw new EpException(MessageConst.ERROR_760623, new String[] {
						recommend.getTplSpecZoneDtl().getZoneName(), recommend.getRequestItem().getItemName() });
			} else {
				tabMenuListener(3);
				throw new EpException(MessageConst.ERROR_760622, new String[] { recommend.getRequestItem()
						.getItemName() });
			}
		}
	}

	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to display validation if no supplier for package
	 * 
	 * @param recommendMap
	 * @param identifierMap
	 * @param scQt
	 * 
	 **/
	private void validationPackageSupplier(Map<Long, ScQtRecommend> recommendMap, Map<Long, Boolean> identifierMap,
			ScQt scQt) {

		for (Map.Entry<Long, Boolean> entry : identifierMap.entrySet()) {
			if (!entry.getValue()) {
				ScQtRecommend recommend = recommendMap.get(entry.getKey());
				if (scQt.isZonal()) {
					setZoneAndTabIndexOnException(recommend.getTplSpecZoneDtl());
					throw new EpException(MessageConst.ERROR_760625, new String[] { recommend.getTplSpecZoneDtl()
							.getZoneName() });
				} else {
					tabMenuListener(3);
					throw new EpException(MessageConst.ERROR_760624);
				}
			}
		}
	}

	// #28777 & #29729 - End
	
	// #28777 & #29729 - Start
	/**
	 * Method to redirect to any zones tab after validation
	 * 
	 * @param tplSpecZoneDtl
	 **/
	private void setZoneAndTabIndexOnException(TplSpecZoneDtl tplSpecZoneDtl) {
		List<TplSpecZoneDtl> specZoneDtls = this.getManagedBean().getMultiLOIZoneDetails();
		if (tplSpecZoneDtl != null && CollectionUtils.isNotEmpty(specZoneDtls)) {
			TplSpecZoneDtl zoneDtl = getManagedBean().getZoneMap().get(tplSpecZoneDtl.getSpecZoneId());
			this.getManagedBean().setZoneActiveIndex(zoneDtl.getZoneTabIndex());
			onZoneTabChangeAction(getManagedBean().getZoneMap().get(zoneDtl.getSpecZoneId()));
		}
	}

	/**
	 * Method to redirect to tab recommendation after validation
	 * 
	 * @param tplSpecZoneDtl
	 **/
	private void onZoneTabChangeAction(TplSpecZoneDtl tplSpecZoneDtl) {
		this.getManagedBean().setSelectedZone(tplSpecZoneDtl);
		this.getManagedBean().setZoneActiveIndex(tplSpecZoneDtl.getZoneTabIndex());
		tabMenuListener(3);
	}
	
	// #28777 & #29729 - Start
	
	/**
	 * #30008 - Validate LOI Negotiation result before submit
	 * @param bpmAction
	 * @return void
	 */
	public void validateNegoDecisionAction(String outcome) {
		try {
			/*#34444 Start*/
			this.getManagedBean().setAction(outcome);
			log.info("validateNegoDecisionAction(String outcome ::>:: " + outcome);
			/*#34444 End*/
			ScLoi scLoi = getManagedBean().getScLoi();
			Long statusId = scLoi.getScWorkFlowStatus().getPmStatus().getStatusId();
			String bpmAction = outcome;
			

			if (QTStatusConst.LOI_PENDING_NEGOTIATION.equals(statusId)) {
				String decision = this.getManagedBean().getDecisionCode();
				String justification = this.getManagedBean().getJustification();
				bpmAction = BpmOutcomeConst.BPM_SUBMIT;

				if (decision == null) {
					throw new EpException(MessageConst.ERROR_775194);
				} else if (decision.equals(ParameterConst.NOT_REACH_AGREEMENT) && justification == "") {
					throw new EpException(MessageConst.ERROR_775193);
				} else if (decision.equals(ParameterConst.NOT_REACH_AGREEMENT) && justification != "") {
					RequestContext.getCurrentInstance().execute("submitNegoConfirmPopUp.show()");
				} else if (decision.equals(ParameterConst.REACHED_AGREEMENT)) {
					updateBpmTaskAction(bpmAction);
				}
			} else {
				updateBpmTaskAction(bpmAction);
			}

		} finally {
		}

	}// #30008 - end
	
	//20230111@Haziq - #12487
	/**
	 * to set the details for selected PTJ
	 * @param userOrg
	 */
	public void userPtjListener(PmUserOrg userOrg) {
		if (userOrg != null) {
			this.getManagedBean().setSelectedUserPtj(userOrg);
			this.getManagedBean().setContractSignerUser(userOrg.getPmUser());
			
			log.info("signerUserListener");
		
			ScLoi scLoi = getManagedBean().getScLoi();
			scLoi.setContractSigner(userOrg.getPmUser());
			scLoi.setLoiSignerDesig(userOrg.getPmUser().getDesignation());
		
			
			RequestContext.getCurrentInstance().update("userPtjOrg");
		}
	}
	
	// #37831 - #29007 Start Vinod 23/04/2025
	/**
	 * This method will update the selected term for nwe row after adding to the
	 * list.
	 * 
	 * @param
	 * @return
	 **/
	public void addNegoTermsSelected() {
		ScLoi scLoi = this.getManagedBean().getScLoi();

		if (CollectionUtils.isNotEmpty(scLoi.getScNegoTerms())) {
			for (ScLoiTerm term : scLoi.getScNegoTerms()) {
				if (StringUtils.isEmpty(term.getTermDesc())) {
					term.setSelectTerm(Boolean.FALSE);
				}
			}
		}
	}
	// #37831 - #29007 End Vinod 23/04/2025
	
	//#21692 - Start
	private void getSupplierRejectProposalValidityExtensionNotification(ScLoi scLoi) {
		if (scLoi != null && scLoi.getLoiId() != null && checkingForEPVNotification(scLoi)) {
			StringBuilder qtProposalExpiredMsg = new StringBuilder();
			Locale locale = new FacesScopes().getUserLocale();
			qtProposalExpiredMsg.append(ResourceBundleUtils.getMessage(SourcingConstants.LETTER_OF_INTENT,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
			qtProposalExpiredMsg.append(CommonConstants.DEFAULT_ENCRYPT_EMPTY_VALUE);
			qtProposalExpiredMsg.append(ResourceBundleUtils.getMessage(
					SourcingConstants.LOI_LOA_SUPPLIER_REJECT_NOTIFICATION, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					locale));

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
							qtProposalExpiredMsg.toString(), MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
		}
	}
	//#21692 - End
	
	//#21692 - Start
	/**
	 * check supplier response for proposal validity extension
	 * 
	 * @param supplierId
	 * @return TRUE IF supplier Rejected Validity Extension
	 */
	public boolean isSupplierStatusRejectedEPV(Long supplierId) {
		boolean supplierStatus = false;

		List<Long> suppList = new ArrayList<>();
		if (null != supplierId) {
			suppList.add(supplierId);
			supplierStatus = getQTDetailSO().checkSupplierEPVStatus(getManagedBean().getScQt().getQtId(), suppList);
		}

		return supplierStatus;
	}
	//#21692 - End
	
	//#21692 - Start
	/**
	 * check supplier response for proposal validity extension
	 * and BPM validity for the task
	 * 
	 * @param supplierId
	 * @return TRUE IF supplier Rejected Validity Extension
	 */
	public boolean isSupplierStatusRejectEPVAndBPMValid(Long supplierId) {
		boolean supplierStatus = false;

		List<Long> suppList = new ArrayList<>();
		if (null != supplierId) {
			suppList.add(supplierId);
			supplierStatus = getQTDetailSO().checkSupplierEPVStatus(getManagedBean().getScQt().getQtId(), suppList);
		}

		return supplierStatus
				&& this.validateSupportedTask(this.getManagedBean().getScQt().getQtNo(),
						QTConstants.ALLOW_CANCEL_LOA_LOI_FOR_EPV);
	}
	//#21692 - End
	
	//#21692 - Start
	/**
	 * check supplier response for proposal validity extension,
	 * BPM validity for the task and status
	 * 
	 * @return TRUE IF all condition met
	 */
	public boolean isApproverAbleToSave() {
		return !this.getManagedBean().isAbleToSubmitLOI()
				&& (this.getManagedBean().getScLoi().getScWorkFlowStatus().getPmStatus().getStatusId()
						.equals(QTStatusConst.LOI_PENDING_APPROVAL) || this.getManagedBean().getScLoi()
						.getScWorkFlowStatus().getPmStatus().getStatusId()
						.equals(QTStatusConst.LOI_PENDING_REVISION_APPROVAL));
	}
	//#21692 - End
	
	//#21692 - Start
	/**
	 * Do not render cancel button for approver when LOI at Approval
	 * 
	 * @return
	 */
	public boolean disableCancelForApprover() {
		return !Arrays.asList(this.getManagedBean().getOutComes()).contains(BpmOutcomeConst.BPM_APPROVE);
	}
	//#21692 - End
	
	//#21692 - Start
	/**
	 * Must validate html for LOI first
	 * @param isWithBPM
	 */
	public void validateBeforePopup(boolean isWithBPM) {
		if (isWithBPM) {
			RequestContext.getCurrentInstance().execute("cancelPopup.show()");
		} else {
			RequestContext.getCurrentInstance().execute("cancelPopup2.show()");
		}
	}
	//#21692 - End
	
	//#21692 - Start
	/**
	 * Must validate html for LOI first
	 * @param isWithBPM
	 */
	public void validateBeforePopupWIP() {
		RequestContext.getCurrentInstance().execute("cancelPopup3.show()");
	}
	//#21692 - End
	
	//#21692 - Start
	private boolean checkingForEPVNotification(ScLoi scLoi) {
		// List of statuses to enable notification
		List<Long> statusEnabledCancel = new ArrayList<>();
		statusEnabledCancel.add(QTStatusConst.LOI_PENDING_SUBMISSION);
		statusEnabledCancel.add(QTStatusConst.LOI_PENDING_APPROVAL);
		statusEnabledCancel.add(QTStatusConst.LOI_PENDING_REVISION);
		statusEnabledCancel.add(QTStatusConst.LOI_PENDING_NEGOTIATION);
		statusEnabledCancel.add(QTStatusConst.LOI_PENDING_REVISION_APPROVAL);
		
		boolean isStatusEnabled = scLoi.getScWorkFlowStatus() != null
				&& statusEnabledCancel.contains(scLoi.getScWorkFlowStatus().getPmStatus().getStatusId());
		boolean isQTValid = checkProposalValidityDateReach(getManagedBean().getScQt().getProposalValidityEndDate());

		return isSupplierStatusRejectedEPV(scLoi.getScLoiLoa().getSmSupplier().getSupplierId()) && isStatusEnabled
				&& isQTValid && !isQTMaintenanceScreen();
	}
	//#21692 - End
	
	//#21692 - Start
	private void checkSupplierAwardedRejectValidityExtensionPreRender() {
		boolean endorseOrSignOff = false;
		for (String outcome : this.getManagedBean().getOutComes()) {
			if (outcome.equals(BpmOutcomeConst.BPM_ENDORSE) || outcome.equals(BpmOutcomeConst.BPM_SIGNOFF)) {
				endorseOrSignOff = true;
			}
		}
		for (ScQtRecommend scQtRecommend : getManagedBean().getScQtFinalization().getScQtRecommends()) {
			for (ScQtRecommendDtl recommendDtl : scQtRecommend.getRecommendDtls()) {
				if (!endorseOrSignOff
						&& getManagedBean().getSelectedSupplier() != null
						&& getManagedBean().getSelectedSupplier().getScQtRecommendDtl().get(0).getScQtProposalAmount()
								.getScQtProposal().getQtProposalId()
								.equals(recommendDtl.getScQtProposalAmount().getScQtProposal().getQtProposalId())
						&& recommendDtl.getScQtProposalAmount().getScQtProposal().getIsAgreeQtExtendPeriod() != null
						&& (recommendDtl.getScQtProposalAmount().getScQtProposal().getIsAgreeQtExtendPeriod()
								.equals(SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_REJECTED) || recommendDtl
								.getScQtProposalAmount().getScQtProposal().getIsAgreeQtExtendPeriod()
								.equals(SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_NOT_QUALIFIED))) {
					getManagedBean().setSelectedSupplier(null);
				}
			}
		}
	}
	//#21692 - End
	
	//#21692 - Start
	private void checkSupplierAwardedRejectValidityExtension() {
		for (ScQtRecommend scQtRecommend : getManagedBean().getScQtFinalization().getScQtRecommends()) {
			for (ScQtRecommendDtl recommendDtl : scQtRecommend.getRecommendDtls()) {
				if (!recommendDtl.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_REJECTED)
						&& recommendDtl.getScQtProposalAmount().getScQtProposal().getIsAgreeQtExtendPeriod() != null
						&& (recommendDtl.getScQtProposalAmount().getScQtProposal().getIsAgreeQtExtendPeriod()
								.equals(SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_REJECTED) ||
								recommendDtl.getScQtProposalAmount().getScQtProposal().getIsAgreeQtExtendPeriod()
						.equals(SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_NOT_QUALIFIED))) {
					throw new EpException(MessageConst.ERROR_770407);
				}
			}
		}
	}
	//#21692 - End//#21692 - Start
	private void awardedSupplierRejectEPV(List<ScQtRecommend> scQtRecommendList) {
		boolean endorseOrSignOff = false;
		if (ArrayUtils.isEmpty(this.getManagedBean().getOutComes())) {
			return;
		}
		for (String outcome : this.getManagedBean().getOutComes()) {
			if (outcome.equals(BpmOutcomeConst.BPM_ENDORSE) || outcome.equals(BpmOutcomeConst.BPM_SIGNOFF)) {
				endorseOrSignOff = true;
			}
		}
		if (!endorseOrSignOff) {
			for (ScQtRecommend scQtRecommend : scQtRecommendList) {
				for (ScQtRecommendDtl recommendDtl : scQtRecommend.getRecommendDtls()) {
					setRecommendDtlForSupplierRejectEPV(recommendDtl);
				}
			}
		}
	}
	//#21692 - End
	
	//#21692 - Start
	private void setRecommendDtlForSupplierRejectEPV(ScQtRecommendDtl recommendDtl) {
		if (recommendDtl.getScQtProposalAmount().getScQtProposal().getIsAgreeQtExtendPeriod() != null
				&& (recommendDtl.getScQtProposalAmount().getScQtProposal().getIsAgreeQtExtendPeriod()
						.equals(SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_REJECTED) || recommendDtl
						.getScQtProposalAmount().getScQtProposal().getIsAgreeQtExtendPeriod()
						.equals(SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_NOT_QUALIFIED))) {
			recommendDtl.setRecordStatus(Boolean.FALSE);
			recommendDtl.setRecordStatusFlg(DbValueConst.RECORD_STATUS_REJECTED);
		}
	}
	//#21692 - End
	
	//#38151 - Start
	public void qualifiedSupplierListPopupScore(Long supplierId, Long requestItemID) {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;

		qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTSuppFinalizationSO.class);

		BigDecimal totalScoreTecEval = null;
		BigDecimal totalScoreFinEval = null;
		BigDecimal scoresettsTec = null;
		BigDecimal scoresettsFin = null;
		BigDecimal recommendationTechnicalScore = null;
		BigDecimal recommendationFinancialScore = null;
		BigDecimal recommendationTotalScore = null;
		BigDecimal percentage = new BigDecimal(SourcingConstants.IS_ONE_HUNDRED_PERCENT);
		Long qtId = this.getManagedBean().getScQt().getQtId();
		String currentEvalStage = null;
		String evalstageT = null;
		String evalstageF = null;
		Long requestNoteItemId = null;
		Long selectedZoneId = null;

		retrievescoresettings(this.getManagedBean().getScQt());
		//#38679 use new calculation if only has quantity 0 for zonal only	
		requestNoteItemId = (CollectionUtils.isNotEmpty(this.getManagedBean().getRecommendList()))
			    ? this.getManagedBean().getRecommendList().get(0).getRequestItem().getRequestNoteItemId()
			    : requestNoteItemId;
		
		selectedZoneId = (this.getManagedBean().getSelectedZone() != null)
			    ? this.getManagedBean().getSelectedZone().getSpecZoneId()
			    : selectedZoneId;
		
		boolean isQtyNotNull = null != selectedZoneId
				        && qtSuppFinalizationSO.checkValueQtyNullForPopupRec(qtId);
		
		if (null != this.getManagedBean().getRecommendedSupplierList()) {
				if (null == this.getManagedBean().getSelectItemList()) {
					currentEvalStage = SourcingConstants.EVAL_STAGE_RECOMMENDATION_PKG;

				} else {
					currentEvalStage = SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM;
				}
		} else {

			for (ScQtRecommend recommendList : this.getManagedBean().getRecommendList()) {
				if (null == selectedZoneId || recommendList.getTplSpecZoneDtl().getSpecZoneId().equals(selectedZoneId)) {
					currentEvalStage = SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM;

				}

			}
		}

		if (currentEvalStage.equalsIgnoreCase(SourcingConstants.EVAL_STAGE_RECOMMENDATION_PKG)) {
			evalstageT = SourcingConstants.EVAL_STAGE_TECHNICAL;
			evalstageF = SourcingConstants.EVAL_STAGE_FINANCIAL_PKG;
			requestItemID = null;
		} else if (currentEvalStage.equalsIgnoreCase(SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM)) {
			//if WIP cannot use Evalstage TI have to change T, 4serangkai deployment only can use TI
			//evalstageT = SourcingConstants.EVAL_STAGE_TECHNICAL;
			evalstageT = SourcingConstants.EVAL_STAGE_TECHNICAL_ITEM;
			evalstageF = SourcingConstants.EVAL_STAGE_FINANCIAL_ITM;
		}
		
		//#38679 start
		//use new calculation if only has quantity 0 for zonal only
		if(isQtyNotNull && currentEvalStage.equalsIgnoreCase(SourcingConstants.EVAL_STAGE_RECOMMENDATION_PKG)){
			Object[] rows = (Object[]) qtSuppFinalizationSO
			        .getSupplierSpecTotalScoreZonePopupTECPkg(qtId, supplierId, selectedZoneId);		
			if (rows != null) {
				BigDecimal supplierTotalScore = (BigDecimal) rows[1];
				totalScoreTecEval = supplierTotalScore;

			}
		} else {
			List<Object[]> findListT = qtSuppFinalizationSO.retrieveQtEvaluationResultDtlsTFscore(evalstageT, qtId, null,requestItemID);
			for (Object[] row : findListT) {
				BigDecimal suppId = (BigDecimal) row[0];
				//BigDecimal supplierNo = (BigDecimal) row[1];
				BigDecimal supplierTotalScore = (BigDecimal) row[3];
				String evalStage = row[4].toString();
				BigDecimal reqItemId = (BigDecimal) row[6];
				// Handle each row
				if (supplierId == suppId.longValue() && evalStage.equalsIgnoreCase(evalstageT)
						&& (requestNoteItemId == null || reqItemId == null || requestNoteItemId == reqItemId.longValue() || requestItemID == reqItemId.longValue())) {
					totalScoreTecEval = supplierTotalScore;
					break;
				}
			}
		}	
		//#38679 end
		
		for (ScQtScoreSetting scoresett : this.getManagedBean().getScQt().getScQtScoreSettings()) {
			if (currentEvalStage.equalsIgnoreCase(SourcingConstants.EVAL_STAGE_RECOMMENDATION_PKG)
					&& scoresett.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_PACKAGE) {
				scoresettsTec = scoresett.getOverallTechnical();
				scoresettsFin = scoresett.getOverallFin();

				if (null != scoresett.getOverallOtherTechnical() || null != scoresett.getOverallOtherFin()) {
					scoresettsTec = scoresett.getOverallTechnical().add(scoresett.getOverallOtherTechnical());
					scoresettsFin = scoresett.getOverallFin().add(scoresett.getOverallOtherFin());
				}
				break;
			} else if (currentEvalStage.equalsIgnoreCase(SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM)
					&& scoresett.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_ITEM) {
				scoresettsTec = scoresett.getOverallTechnical();
				scoresettsFin = scoresett.getOverallFin();

				if (null != scoresett.getOverallOtherTechnical() || null != scoresett.getOverallOtherFin()) {
					scoresettsTec = scoresett.getOverallTechnical().add(scoresett.getOverallOtherTechnical());
					scoresettsFin = scoresett.getOverallFin().add(scoresett.getOverallOtherFin());
				}
				break;
			}
		}
		this.getManagedBean().setOverallTechnicalRecommendationPercent(scoresettsTec.longValue());
		this.getManagedBean().setOverallFinancialRecommendationPercent(scoresettsFin.longValue());


		List<Object[]> findListF = qtSuppFinalizationSO.retrieveQtEvaluationResultDtlsTFscore(evalstageF, qtId,
				selectedZoneId,requestItemID);

		for (Object[] row : findListF) {
			BigDecimal suppId = (BigDecimal) row[0];
			//BigDecimal supplierNo = (BigDecimal) row[1];
			BigDecimal supplierTotalScore = (BigDecimal) row[3];
			String evalStage = row[4].toString();
			BigDecimal selectedZone = (BigDecimal) row[5];
			BigDecimal reqItemId = (BigDecimal) row[6];
			// Handle each row

			if (supplierId == suppId.longValue() && evalStage.equalsIgnoreCase(evalstageF)
					&& (requestNoteItemId == null || reqItemId == null || requestNoteItemId == reqItemId.longValue() 
					|| requestItemID == reqItemId.longValue() && (null != selectedZoneId && selectedZone.longValue() == selectedZoneId)
					|| null == selectedZoneId)) {

				totalScoreFinEval = supplierTotalScore;
				break;
			}
		}

		recommendationTechnicalScore = totalScoreTecEval.multiply(scoresettsTec).divide(percentage,
				SourcingConstants.DECIMAL_PRECISION, BigDecimal.ROUND_HALF_UP);

		recommendationFinancialScore = totalScoreFinEval.multiply(scoresettsFin).divide(percentage,
				SourcingConstants.DECIMAL_PRECISION, BigDecimal.ROUND_HALF_UP);
		
		recommendationTotalScore = recommendationTechnicalScore.add(recommendationFinancialScore);
		
		WeightageScoreBean bean = new WeightageScoreBean(totalScoreTecEval.doubleValue(),
				totalScoreFinEval.doubleValue(), recommendationTechnicalScore.doubleValue(),
				recommendationFinancialScore.doubleValue(), recommendationTotalScore.doubleValue());

		List<WeightageScoreBean> listlist = new ArrayList<>();
		listlist.add(bean);

		this.getManagedBean().setWeightageScoreBeans(listlist);

		RequestContext.getCurrentInstance().execute("SuppScoreDetailsPopup.show()");

	}
	
	public void retrievescoresettings(ScQt scqt) {
		
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setScQt(scqt);
		SpecificationResDTO resDTO = null;
		IQTSpecSO specSo = null;
		try {
			List<Integer> settinglist = new ArrayList<Integer>();
			settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_PACKAGE);
			settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_ITEM);

			reqDTO.setSettingTypes(settinglist);
			specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
			resDTO = specSo.retrieveScoreSettingDtls(reqDTO);
			this.getManagedBean().getScQt().setScQtScoreSettings(resDTO.getScoreSettingResultBean().getResultList());
		} catch (Exception e) {
			handleException(e);
		}
	}
//#38151 - End
	
	//#38151 Start
		private void checkWeightage35384WIP() {
			IQTDetailSO qtDetailsSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTDetailSO.class);
			ScQt qt = this.getManagedBean().getScQt();
			Date publishDate = qt.getPublishDate();
			Date createdDate = qt.getCreatedDate();
			
			if (null == publishDate) {
				QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
				qtReqDTO.setRedmineNo(QTConstants.APP_VERSION_REDMINE_NO_38151);
				qtReqDTO.setPhase(QTConstants.APP_VERSION_REDMINE_NO_38151_PHASE);

				PmAppVersion appVerDtls = qtDetailsSO.getAppVersionDetailsWithRedmineNo(qtReqDTO);

				Date effDate = appVerDtls.getEffDate();
				Timestamp effTimestamp = new Timestamp(effDate.getTime());
				
				if (createdDate.before(effTimestamp)) {
					this.getManagedBean().setWeightage35384WIP(true);
				}
			}else{
				QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
				qtReqDTO.setRedmineNo(QTConstants.APP_VERSION_REDMINE_NO_38151);
				qtReqDTO.setPhase(QTConstants.APP_VERSION_REDMINE_NO_38151_PHASE);

				PmAppVersion appVerDtls = qtDetailsSO.getAppVersionDetailsWithRedmineNo(qtReqDTO);

				Date effDate = appVerDtls.getEffDate();

				if (publishDate.before(effDate)) {
					this.getManagedBean().setWeightage35384WIP(true);
				}
			}
		}
		//#38151 End

	//#21692 - Start
	/**
	 * Enable LOI WIP to be cancelled for rejecting
	 * proposal validity extension
	 */
	public void cancelLOIWIP() {
		try {
			ScLoi scLoi = getManagedBean().getScLoi();
			/**
			 * Will update LOI workflow status: Cancelled (Not agree with
			 * extension validity period) and ScQtRecommendDtl: IS_LOI_AWARDED =
			 * FALSE and RECORD_STATUS = Rejected (4). IF all LOIs' status
			 * either Completed, Rejected OR Cancelled (Not agree with extension
			 * validity period), QT will exit and goes to Refinalization.
			 */
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(scLoi.getScLoiLoa().getDocId());
			reqDTO.setDocId(scLoi.getLoiId());
			QuationTenderResDTO resDTO = getQTDetailSO().exitEPVLOIOperation(reqDTO);

			if (resDTO.isSuccessful()) {
				successMessage(MessageConst.INFO_700031, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_LETTER_OF_INTENT, scLoi.getLoiNo(), Boolean.TRUE);
			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError("LOI", "cancelLOIWIP", getManagedBean().getScLoi().getLoiNo(),
					"cancelLOIWIP", getUserIdSessionFromLoginId(), e);
		}
	}
	//#21692 - End
	
	//#21692 - Start
	private SCAwardSupplierDataType addLastDocumentPayload(ScLoi scLoi, SCAwardSupplierDataType awardDataType) {
		if (scLoi.getScLoiLoa().getDocType().equals(ParameterConst.DOCTYPE_QUOTATION)) {
			awardDataType.setLastDocument(getQtAwardSO().isLastDocumentLOI(scLoi.getScLoiLoa().getDocId()));
		}		
		return awardDataType;
	}
	//#21692 - End

	// 34491 - start
	/**
	 * Evaluate if task in Recommed MA Supplier
	 */
	public boolean isFinalizationStage() {
		String taskName = this.getManagedBean().getTaskName();
		return SCProcessNameConst.RECOMMEND_MA_SUPPLIER.equals(taskName);
	}

	// 34491 - end

	// 34491 - start
	/**
	 * Populate the information needed to load meeting preparation tab
	 */
	private void populateMeetingPreparationTab() {
		ScQt scQt = this.getManagedBean().getScQtFinalization().getScQt();
		prepareFinalizationQtCommittee(this.getManagedBean().getScQtCommittee());
		prepareMemberUpdate();
		this.getManagedBean().setUserSearchBean(new UserCommitteeSearchBean());
		this.getManagedBean().setDeletedCommitteeActivities(new ArrayList<ScQtCommitteeActivity>());
		this.getManagedBean().setDeletedCommitteeMembers(new ArrayList<ScCommitteeMember>());
		this.getManagedBean().setDeletedQtMeetingAttendances(new ArrayList<ScQtMeetingAttendance>());
		this.getManagedBean().setDeletedQtMeetings(new ArrayList<ScQtMeeting>());
		this.getManagedBean().setAllowGlobalEndorseSignoff(
				validateSupportedTask(scQt.getQtNo(), SourcingConstants.ALLOW_GLOBAL_ENDORSE_SIGNOFF));
	}

	// 34491 - end

	// 34491 - start
	private void prepareFinalizationQtCommittee(ScQtCommittee qtCommittee){
		ScCommittee committee = qtCommittee.getScCommittee();
		this.getManagedBean().setFinalizationQtCommittee(qtCommittee);
		
		prepareFinalizationMeeting(qtCommittee);
		
		PmParameter committeeType = committee.getCommitteeType();
		String committeeTypeCode = committeeType.getParameterCode();
		if (Arrays.asList(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A,
				ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B).contains(committeeTypeCode)) {
			committeeType = new PmParameter();
			committeeType.setParameterCode(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE);
			committeeType.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
		}
		this.getManagedBean().setFinalizationCommitteeDesc(this.getParameterDesc(committee.getCommitteeType()));

		prepareCommitteeActivities(qtCommittee);

		List<ScQtCommitteRemarks> remarks = initializeRemarks(qtCommittee, this.getManagedBean()
				.getScQtFinalization().getCurrentStatus(), qtCommittee.getQtCommitteRemarks(),
				ScQtCommitteRemarks.class);
		
		Collections.sort(remarks, new Comparator<ScQtCommitteRemarks>() {
			@Override
			public int compare(ScQtCommitteRemarks o1, ScQtCommitteRemarks o2) {
				if (o1.getCreatedDate() == null || o2.getCreatedDate() == null)
					return 0;
				return o2.getCreatedDate().compareTo(o1.getCreatedDate());
			}
		});
		qtCommittee.setQtCommitteRemarks(remarks);
	}
	// 34491 - end

	// 34491 - start
	private void prepareFinalizationMeeting(ScQtCommittee qtCommittee){
		for (ScQtMeeting qtMeeting : qtCommittee.getScQtMeetings()) {
			boolean disabledMeeting = !isFinalizationStage()
					|| !qtMeeting.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)
					|| this.getManagedBean().isReadOnly();
			qtMeeting.setDisableRow(disabledMeeting);
			qtMeeting.setSelectable(!disabledMeeting);
			Long meetingStatusId = qtMeeting.getMeetingStatus() != null ? qtMeeting.getMeetingStatus()
					.getStatusId() : null;
			qtMeeting.setMeetingStatusStr(meetingStatusId);
			this.getManagedBean().setHasPreviousMeeting(
					qtMeeting.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_SUSPENDED));

		}
		
		CustomUtils.sort(qtCommittee.getScQtMeetings(), new Comparator<ScQtMeeting>() {
			@Override
			public int compare(ScQtMeeting meeting01, ScQtMeeting meeting02) {
				return meeting01.getCreatedDate().compareTo(meeting02.getCreatedDate());
			}
		});
		if (null == qtCommittee.getMeetingAttendances()) {
			qtCommittee.setMeetingAttendances(new ArrayList<ScQtMeetingAttendance>());
		}
		for (ScQtMeetingAttendance qtMeetingAttendance : qtCommittee.getMeetingAttendances()) {
			boolean disabledAttendance = !isFinalizationStage()
					|| !qtMeetingAttendance.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)
					|| this.getManagedBean().isReadOnly();
			qtMeetingAttendance.setDisableRow(disabledAttendance);
		}
		
	}
	// 34491 - end

	// 34491 - start
	private void prepareCommitteeActivities(ScQtCommittee qtCommittee){
		if (!isFinalizationStage()) {
			return;
		}
		if (null == qtCommittee.getCommitteeActivities()) {
			qtCommittee.setCommitteeActivities(new ArrayList<ScQtCommitteeActivity>());

		}
		Map<Object, Object> activityLookup = CustomUtils.listToMap(qtCommittee.getCommitteeActivities(),
				"getScCommitteeMember.getIcPassport");
		ScCommittee committee = qtCommittee.getScCommittee();
		for (ScCommitteeMember member : committee.getScCommitteeMembers()) {
			if (null != activityLookup.get(member.getIcPassport())) {
				activityLookup.remove(member.getIcPassport());
				continue;
			}

			ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
			activity.setScQtCommittee(qtCommittee);
			activity.setScCommitteeMember(member);
			setUserDetails(activity);
			qtCommittee.getCommitteeActivities().add(activity);
		}
		final List<String> sortOrder = Arrays.asList(SourcingConstants.COMMITTEE_ROLE_SORT_ORDER);
		CustomUtils.sort(committee.getScCommitteeMembers(), new Comparator<ScCommitteeMember>() {
			@Override
			public int compare(ScCommitteeMember o1, ScCommitteeMember o2) {
				return Integer.valueOf(sortOrder.indexOf(o1.getMemberRole().getParameterCode())).compareTo(
						Integer.valueOf(sortOrder.indexOf(o2.getMemberRole().getParameterCode())));
			}
		});

		CustomUtils.sort(qtCommittee.getCommitteeActivities(), new Comparator<ScQtCommitteeActivity>() {
			@Override
			public int compare(ScQtCommitteeActivity o1, ScQtCommitteeActivity o2) {
				return Integer.valueOf(sortOrder.indexOf(o1.getScCommitteeMember().getMemberRole().getParameterCode()))
						.compareTo(
								Integer.valueOf(sortOrder.indexOf(o2.getScCommitteeMember().getMemberRole()
										.getParameterCode())));
			}
		});

		committee.getScCommitteeMembers().removeAll(activityLookup.keySet());
	}
	
	// 34491 - end

	// 34491 - start
	/**
	 * Add new meeting details
	 */
	public void addMeetingDtls() {
		ScQtCommittee finalizationQtCommittee = this.getManagedBean().getFinalizationQtCommittee();
		if (null == finalizationQtCommittee.getScQtMeetings()) {
			finalizationQtCommittee.setScQtMeetings(new ArrayList<ScQtMeeting>());
		}
		List<ScQtMeeting> qtMeetings = finalizationQtCommittee.getScQtMeetings();

		ScQtMeeting meeting = new ScQtMeeting();
		meeting.setQtCommittee(finalizationQtCommittee);
		setUserDetails(meeting);
		meeting.setMeetingStatus(getPmStatusByStatusId(QTStatusConst.MS_PENDING));
		qtMeetings.add(meeting);
	}

	// 34491 - end

	// 34491 - start
	/**
	 * Delete meeting if it was flagged selected
	 */
	public void deleteMeetingDetails() {
		ScQtCommittee finalizationQtCommittee = this.getManagedBean().getFinalizationQtCommittee();
		List<ScQtMeeting> qtMeetings = finalizationQtCommittee.getScQtMeetings();
		if (CollectionUtils.isEmpty(qtMeetings)) {
			return;
		}
		List<ScQtMeeting> deletedQtMeetings = new ArrayList<>();
		for (ScQtMeeting qtMeeting : qtMeetings) {
			if (qtMeeting.isSelected()) {
				qtMeeting.setDeleted(true);
				this.getManagedBean().getDeletedQtMeetings().add(qtMeeting);
				deletedQtMeetings.add(qtMeeting);
			}
		}
		qtMeetings.removeAll(deletedQtMeetings);
	}

	// 34491 - end

	// 34491 - start
	/**
	 * Add new meeting attendees
	 */
	public void addFinalizationMeetingAttendance() {
		ScQtCommittee finalizationQtCommittee = this.getManagedBean().getFinalizationQtCommittee();
		if (null == finalizationQtCommittee.getMeetingAttendances()) {
			finalizationQtCommittee.setMeetingAttendances(new ArrayList<ScQtMeetingAttendance>());
		}
		List<ScQtMeetingAttendance> qtMeetingAttendances = finalizationQtCommittee.getMeetingAttendances();

		ScQtMeetingAttendance qtMeetingAttendance = new ScQtMeetingAttendance();
		qtMeetingAttendance.setQtCommittee(finalizationQtCommittee);
		setUserDetails(qtMeetingAttendance);
		qtMeetingAttendances.add(qtMeetingAttendance);
	}

	// 34491 - end

	// 34491 - start
	/**
	 * Delete meeting attendees if it was flagged selected
	 */
	public void deleteMeetingAttendanceDetails() {
		ScQtCommittee finalizationQtCommittee = this.getManagedBean().getFinalizationQtCommittee();
		List<ScQtMeetingAttendance> qtMeetingAttendances = finalizationQtCommittee.getMeetingAttendances();
		if (CollectionUtils.isEmpty(qtMeetingAttendances)) {
			return;
		}
		List<ScQtMeetingAttendance> deletedQtMeetingAttendances = new ArrayList<>();
		for (ScQtMeetingAttendance qtMeetingAttendance : qtMeetingAttendances) {
			if (qtMeetingAttendance.isSelected()) {
				qtMeetingAttendance.setDeleted(true);
				this.getManagedBean().getDeletedQtMeetingAttendances().add(qtMeetingAttendance);
				deletedQtMeetingAttendances.add(qtMeetingAttendance);
			}
		}
		qtMeetingAttendances.removeAll(deletedQtMeetingAttendances);
	}

	// 34491 - end

	// 34491 - start
	/**
	 * Generic select all method to flag rows as selected if
	 * allowed(selectable=true)
	 * 
	 * @param list
	 * @param isSelectedAll
	 */
	public <T> void selectAll(List<T> list, boolean isSelectedAll) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		for (T item : list) {
			Method isSelectableMethod;
			try {
				isSelectableMethod = item.getClass().getMethod("isSelectable");
				Method setSelectedMethod = item.getClass().getMethod("setSelected", boolean.class);
				Boolean selectable = (Boolean) isSelectableMethod.invoke(item);
				setSelectedMethod.invoke(item, selectable && isSelectedAll);
			} catch (Exception e) {
				ScQt scQt = getManagedBean().getScQt();
				String qtNo = null != scQt ? scQt.getQtNo() : "";
				SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE, SLALogConstant.QT_LOI_DETAIL_BACKINGBEAN,
						qtNo, SLALogConstant.QT_SELECTALL, getUserIdSessionFromLoginId(), e);
			}
		}
	}

	// 34491 - end

	// 34491 - start
	/**
	 * Navigation method to navigate between update committee page. Handle back,
	 * update, update committee button
	 * 
	 * @param action
	 */
	public void navigateToUpdateMember(String action) {
		FacesContext fc = FacesContext.getCurrentInstance();
		String mainPage = "/xhtml/sc/loi/mutipleLoi/loiNegotiationResultMenu.xhtml";
		ScQtCommittee fcQtCommittee = this.getManagedBean().getFinalizationQtCommittee();
		List<ScCommitteeMember> committeeMembers = fcQtCommittee.getScCommittee().getScCommitteeMembers();

		if (CommonConstants.ACTION_BACK.equalsIgnoreCase(action)) {
			committeeMembers.clear();
			committeeMembers.addAll(this.getManagedBean().getPreviousMembers());
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, mainPage);
		} else if (CommonConstants.ACTION_UPDATE.equalsIgnoreCase(action)) {
			this.getManagedBean().setPreviousMembers(new ArrayList<ScCommitteeMember>());
			for (ScCommitteeMember member : committeeMembers) {
				boolean disabled = member.getMemberRole().getParameterCode()
						.equals(ParameterConst.COMMITTEE_ROLE_SECRETARY);
				member.setSelectable(!disabled);
				member.setDisableRow(disabled);
				ScCommitteeMember clonedMember = (ScCommitteeMember) SerializationUtils.clone(member);
				this.getManagedBean().getPreviousMembers().add(clonedMember);
			}
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "/xhtml/sc/loi/mutipleLoi/preparation/memberUpdate.xhtml");
		} else if (CommonConstants.ACTION_CONFIRM.equalsIgnoreCase(action)) {
			validateCommitteeChanges();
			this.getManagedBean().setCommitteModified(true);
			saveMultipleLoiAction();
			retrieveMultipleLoiDtls(this.getManagedBean().getScQt().getQtId(),
					this.getManagedBean().getCommitteeType(), this.getManagedBean().getRecommendDtlIds());
			this.populateMeetingPreparationTab();
			this.getManagedBean().setMeetingPreparationActiveIndex(1);
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, mainPage);
		}
	}

	// 34491 - end

	// 34491 - start
	private ICommitteSO getCommitteeSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
	}

	// 34491 - end

	// 34491 - start
	private void prepareMemberUpdate() {
		ScQtCommittee fcQtCommittee = this.getManagedBean().getFinalizationQtCommittee();
		List<ScCommitteeMemberHistory> histories = getCommitteeSO().getMergedHistory(fcQtCommittee, false);
		List<ScCommitteeMemberHistory> legacyHistories = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(histories)) {
			for (ScCommitteeMemberHistory h : histories) {
				if (h.isLegacy()) {
					legacyHistories.add(h);
				}
			}
			histories.removeAll(legacyHistories);
		}
		this.getManagedBean().setHistoryPaginator(new Paginator(histories));

		ScQt qt = this.getManagedBean().getScQt();
		if (CollectionUtils.isEmpty(qt.getScQtApprovals())) {
			return;
		}

		for (ScQtApproval approval : qt.getScQtApprovals()) {
			if (approval.getApprovalCategory().getParameterCode()
					.equals(ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT)) {
				if (CollectionUtils.isEmpty(approval.getScQtApprovalRemarks())) {
					approval.setScQtApprovalRemarks(new ArrayList<ScQtApprovalRemark>());
				}
				List<ScQtApprovalRemark> remarks = initializeRemarks(approval, qt.getCurrentWorkFlowStatus(),
						approval.getScQtApprovalRemarks(), ScQtApprovalRemark.class, true);

				Collections.sort(remarks, new Comparator<ScQtApprovalRemark>() {
					@Override
					public int compare(ScQtApprovalRemark o1, ScQtApprovalRemark o2) {
						return o2.getCreatedDate().compareTo(o1.getCreatedDate());
					}
				});
				approval.setScQtApprovalRemarks(remarks);
				this.getManagedBean().setCommitteeApproval(approval);
				break;
			}
		}
	}

	// 34491 - end

	// 34491 - start
	/**
	 * Listener method when selecting history to display
	 */
	@SuppressWarnings("unchecked")
	public void onSelectHistory() throws IOException {
		ScCommitteeMemberHistory history = this.getManagedBean().getSelectedCommitteeHistory();
		List<QtCommitteeMemberBean> list = (List<QtCommitteeMemberBean>) (Object) history.deserializeList(history
				.getCommitteeMembers());
		CustomUtils.sortQtCommitteeMemberRole(list);
		this.getManagedBean().setMemberHistoryPaginator(new Paginator(list));
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setScCommitteeId(history.getScCommittee().getCommitteeId());
		CommitteResDTO resDTO = this.getCommitteeSO().retrieveCommitteDetailsByCommitteeId(reqDTO);
		history.setScCommittee(resDTO.getCommittee());
	}

	// 34491 - end

	// 34491 - start
	/**
	 * Delete committtee member
	 */
	public void deleteCommitteeMembers() {
		ScQtCommittee fcQtCommittee = this.getManagedBean().getFinalizationQtCommittee();
		ScCommittee fcCommittee = fcQtCommittee.getScCommittee();
		List<ScCommitteeMember> committeeMembers = fcCommittee.getScCommitteeMembers();
		Map<Object, Object> activityLookup = CustomUtils.listToMap(fcQtCommittee.getCommitteeActivities(),
				"getScCommitteeMember.getIcPassport");
		if (CollectionUtils.isEmpty(committeeMembers)) {
			return;
		}
		List<ScCommitteeMember> deletedCommitteeMembers = new ArrayList<>();
		for (ScCommitteeMember committeeMember : committeeMembers) {
			if (committeeMember.isSelected()) {
				ScQtCommitteeActivity activity = (ScQtCommitteeActivity) activityLookup.get(committeeMember
						.getIcPassport());
				if (null != activity) {
					fcQtCommittee.getCommitteeActivities().remove(activity);
					this.getManagedBean().getDeletedCommitteeActivities().add(activity);
					activity.setDeleted(true);
				}
				committeeMember.setDeleted(true);
				this.getManagedBean().getDeletedCommitteeMembers().add(committeeMember);
				deletedCommitteeMembers.add(committeeMember);
			}
		}
		committeeMembers.removeAll(deletedCommitteeMembers);

	}

	// 34491 - end

	// 34491 - start
	/**
	 * Validate changes in committee members before updating
	 */
	public void validateCommitteeChanges() {
		ScQtCommittee fcQtCommittee = this.getManagedBean().getFinalizationQtCommittee();
		validateCommitteeMembers(fcQtCommittee);

		if (isCommitteeModified(this.getManagedBean().getPreviousMembers(), fcQtCommittee.getScCommittee()
				.getScCommitteeMembers())) {
			List<ScQtApprovalRemark> remarks = this.getManagedBean().getCommitteeApproval().getScQtApprovalRemarks();
			if (StringUtils.isEmpty(remarks.get(0).getRemark())) {
				throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
						"lbl.sc.remark", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
			}
		} else {
			String message = MessageUtils.getMessage(MessageConst.INFO_760506, null,
					MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale());
			throw new EpException(message);
		}
	}

	// 34491 - end

	// 34491 - start
	private void validateCommitteeMembers(ScQtCommittee qtCommittee) {
		List<ScCommitteeMember> committeeMembers = qtCommittee.getScCommittee().getScCommitteeMembers();
		if (CollectionUtils.isEmpty(committeeMembers)) {
			return;
		}

		for (ScCommitteeMember member : committeeMembers) {
			PmParameter memberRole = member.getMemberRole();
			if (ParameterConst.COMMITTEE_ROLE_MEMBER.equals(memberRole.getParameterCode())
					&& (member.getRepresentativeType() == null || StringUtils.isBlank(member.getRepresentativeType()
							.getParameterCode()))) {

				throw new EpException(MessageConst.ERROR_760212, new String[] { ResourceBundleUtils.getMessage(
						"lbl.sc.please.select.representative.type", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()) });
			}
		}
		Long qtId = this.getManagedBean().getScQt().getQtId();
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setQtId(qtId);
		reqDTO.setScQtCommittee(qtCommittee);
		reqDTO.setUserLocale(new FacesScopes().getUserLocale());
		this.getCommitteeSO().validateFcCommitteDetails(reqDTO);
	}

	// 34491 - end

	// 34491 - start
	private boolean isCommitteeModified(List<ScCommitteeMember> previousList, List<ScCommitteeMember> currentList) {
		return !StringUtils.join(getMemberCompareArr(previousList), ",").equalsIgnoreCase(
				StringUtils.join(getMemberCompareArr(currentList), ","));
	}

	// 34491 - end

	// 34491 - start
	private String[] getMemberCompareArr(List<ScCommitteeMember> committeeMembers) {
		String[] memberCompareArr = new String[committeeMembers.size()];
		for (int i = 0; i < memberCompareArr.length; i++) {
			ScCommitteeMember m = committeeMembers.get(i);
			String representativeType = m.getRepresentativeType() == null ? "" : m.getRepresentativeType()
					.getParameterCode();
			memberCompareArr[i] = m.getIcPassport().concat(m.getMemberRole().getParameterCode())
					.concat(representativeType);
		}
		Arrays.sort(memberCompareArr);
		return memberCompareArr;
	}

	// 34491 - end

	// 34491 - start
	/**
	 * Populate the information to display list of available user to select as
	 * member. Prepare information for search window.
	 */
	public void addPopupAction() {
		IPagingDataModel<PmUser> userSearchQCDataModel = getManagedBean().getUserSearchDataModel();
		UserCommitteeSearchBean userCommitteeSearchBean = getManagedBean().getUserSearchBean();
		if (userSearchQCDataModel == null) {
			this.getManagedBean().setUserSearchDataModel(
					new UserQCPagingDataModel(QTConstants.DATATABLE_EXISTINGMEMBERTBL, userCommitteeSearchBean));
		}
		userCommitteeSearchBean.setOrgCat(ParameterConst.ORG_CATEGORY_PTJ);
		resetUserAction();
		this.getManagedBean().setNameRequired(false);
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		committeeMember.setUserSelection(SelectionType.Existing.value());
		getSelectionTypes();
	}

	// 34491 - end

	// 34491 - start
	private void getSelectionTypes() {
		SelectionType[] type = SelectionType.values();
		List<SelectItem> selectionTypes = new ArrayList<>();

		for (SelectionType selectionType : type) {
			selectionTypes.add(new SelectItem(selectionType.value(), selectionType.name()));
		}
		this.getManagedBean().setSelections(selectionTypes);
	}

	// 34491 - end

	// 34491 - start
	private class UserQCPagingDataModel extends PagingDataModel<PmUser> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The user qc search bean. */
		private UserCommitteeSearchBean userSearchBean;

		/**
		 * Instantiates a new user qc paging data model.
		 * 
		 * @param componentId
		 *            the component id
		 * @param userQCSearchBean
		 *            the user qc search bean
		 */
		public UserQCPagingDataModel(String componentId, UserCommitteeSearchBean userQCSearchBean) {
			super(componentId, false);
			this.userSearchBean = userQCSearchBean;
			getManagedBean().setCommitteeSearchBean(userQCSearchBean);// 12471
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		protected ISearchResultBean<PmUser> pageChangeListener(ISearchBean<PmUser> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();

				if (null != userSearchBean) {
					populateUserSearchCriteria(userSearchBean, searchBean);
					populateOrganizationSearchCriteria(userSearchBean, searchBean);
				}
				committeReqDTO.setUserSearchBean(searchBean);
				committeReqDTO.setPtjMinistryAdmin(Boolean.TRUE);
				committeReqDTO.setScQt(getManagedBean().getScQt());

				return committeSO.retrieveUserListByCommType(committeReqDTO).getUserResultBean();
			} catch (Exception e) {
				ScQt scQt = getManagedBean().getScQt();
				String qtNo = null != scQt ? scQt.getQtNo() : "";
				SLAUtils.getInstance().logSLAError(
						SLALogConstant.QT_MODULE,
						SLALogConstant.QT_LOI_DETAIL_BACKINGBEAN,
						qtNo,
						SLALogConstant.QT_USER_QC_PAGING_DATA_MODEL.concat(SLALogConstant.QT_SLA_DOT_SEPARATOR).concat(
								SLALogConstant.QT_PAGE_CHANGE_LISTENER), getUserIdSessionFromLoginId(), e);
			}
			return null;
		}

		private void populateUserSearchCriteria(UserCommitteeSearchBean userSearchBean, ISearchBean<PmUser> searchBean) {
			String name = userSearchBean.getName();
			if (StringUtils.isNotBlank(name)) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.USER_NAME, SearchOperator.OPERATOR_LIKE.value(), name, true));
			}

			String identificationNo = userSearchBean.getIdentificationNo();
			if (StringUtils.isNotBlank(identificationNo)) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.IDENTIFICATION_NO, SearchOperator.OPERATOR_LIKE.value(),
								identificationNo, true));
			}

			String identificationType = userSearchBean.getIdentificationType();
			if (StringUtils.isNotBlank(identificationType)) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTSearchBeanConst.SEARCH_IDENTIFICATIONCODE_PARAMETERCODE,
								SearchOperator.OPERATOR_EQUALS.value(), identificationType, true));
			}
		}

		private void populateOrganizationSearchCriteria(UserCommitteeSearchBean userSearchBean,
				ISearchBean<PmUser> searchBean) {
			String orgCategory = null == userSearchBean.getOrgCat() ? "" : userSearchBean.getOrgCat();
			if (ParameterConst.ORG_CATEGORY_PTJ.equalsIgnoreCase(orgCategory)) {
				String ministry = userSearchBean.getMinistry();
				if (!StringUtils.isBlank(ministry)) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(QTSearchBeanConst.SEARCH_PMUSERORGS_PMORGPROFILE
									.concat(QTSearchBeanConst.SEARCH_DOT_SEPARATOR)
									+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
									SearchOperator.OPERATOR_EQUALS.value(), ministry, true));
				}
				String ptjGroup = userSearchBean.getPtjGroup();
				if (!StringUtils.isBlank(ptjGroup)) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(QTSearchBeanConst.SEARCH_PMUSERORGS_PMORGPROFILE
									.concat(QTSearchBeanConst.SEARCH_DOT_SEPARATOR)
									+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()),
									SearchOperator.OPERATOR_EQUALS.value(), ptjGroup, true));
				}
				String ptj = userSearchBean.getPtj();
				if (!StringUtils.isBlank(ptj)) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(QTSearchBeanConst.SEARCH_PMUSERORGS_PMORGPROFILE
									.concat(QTSearchBeanConst.SEARCH_DOT_SEPARATOR)
									+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.DIVISION.value()),
									SearchOperator.OPERATOR_EQUALS.value(), ptj, true));
				}
			} else if (ParameterConst.ORG_CATEGORY_OTHERS.equalsIgnoreCase(orgCategory)) {
				String orgType = userSearchBean.getOrgType();
				if (!StringUtils.isBlank(orgType)) {
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									QTSearchBeanConst.SEARCH_PMUSERORGS_PMORGPROFILE_ORGTYPECODE_PARAMETERID
											+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.OTHERS.value()),
									SearchOperator.OPERATOR_EQUALS.value(), orgType, true));
				}
				String orgName = userSearchBean.getOrgName();
				if (!StringUtils.isBlank(orgName)) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(QTSearchBeanConst.SEARCH_PMUSERORGS_PMORGPROFILE_ORGPROFILEID
									+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.SUPPORT.value()),
									SearchOperator.OPERATOR_EQUALS.value(), orgName, true));
				}
			}
		}
	}

	// 34491 - end

	// 34491 - start
	/**
	 * Reset user search popup
	 */
	public void resetUserAction() {
		this.resetCommitteeSearchBean(this.getManagedBean().getScQt());
		getManagedBean().setSelectedPmUsers(null);
		getManagedBean().setNameRequired(false);
		getManagedBean().getUserSearchDataModel().resetData();
	}

	// 34491 - end

	// 34491 - start
	/**
	 * Action for search button in add user popup.
	 */
	public void searchUserAction() {
		UserCommitteeSearchBean searchBean = this.getManagedBean().getUserSearchBean();
		if (null == searchBean)
			return;
		if (StringUtils.isBlank(searchBean.getName()) && StringUtils.isBlank(searchBean.getIdentificationNo())) {
			throw new EpException(MessageConst.ERROR_775300);
		}
		this.getManagedBean().getUserSearchDataModel().reloadData();
	}

	// 34491 - end

	// 34491 - start
	/**
	 * Confirm action when users is selected and appointed as a committee
	 * member.
	 */
	public void committeeOkAction() {
		ScQtCommittee fcQtCommittee = this.getManagedBean().getFinalizationQtCommittee();
		ScCommittee fcCommittee = fcQtCommittee.getScCommittee();

		List<ScCommitteeMember> committeeMemberList = fcCommittee.getScCommitteeMembers();

		List<PmUser> selectedPmUserList = getManagedBean().getSelectedPmUsers();
		if (CollectionUtils.isNotEmpty(selectedPmUserList) && CollectionUtils.isNotEmpty(committeeMemberList)) {
			validateUserSelections(committeeMemberList, selectedPmUserList);
		}

		List<ScCommitteeMember> memberList = addExistingUser();
		if (memberList != null) {
			committeeMemberList.addAll(memberList);
		}
		RequestContext.getCurrentInstance().execute("memberWidgetVar.hide()");
	}

	// 34491 - end

	// 34491 - start
	private void validateUserSelections(List<ScCommitteeMember> committeeMemberList,
			List<PmUser> selectedPmUserList) {
		List<String> duplicateUsersList = new ArrayList<>();
		Map<Object, Object> existingUsersMap = CustomUtils.listToMap(committeeMemberList, "getIcPassport");
		for (PmUser user : selectedPmUserList) {
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT);
			ScQtApproval approval = this.getQTDetailSO().retrieveApprovalDtlsByQtIdNType(reqDTO).getQtApproval();

			for (ScQtApproverSettings approvalUser : approval.getApproverSettings()) {
				if (approvalUser.getPmUser().getUserId().equals(user.getUserId())) {
					throw new EpException(MessageConst.ERROR_775301, new String[] { user.getUserName() });
				}
			}
		
			if (null != existingUsersMap.get(user.getIdentificationNo())) {
				duplicateUsersList.add(user.getIdentificationNo());

			}
		}

		if (CollectionUtils.isNotEmpty(duplicateUsersList)) {
			StringBuilder identificationNos = new StringBuilder();
			StringBuilder separator = new StringBuilder(", ");
			for (String identificationNo : duplicateUsersList) {
				identificationNos.append(identificationNo).append(separator);
			}
			identificationNos.replace(identificationNos.lastIndexOf(separator.toString()), identificationNos.length(),
					StringUtils.EMPTY);
			throw new EpException(MessageConst.ERROR_760205, new String[] { identificationNos.toString() });
		}

	}
	
	// 34491 - end

	// 34491 - start
	private List<ScCommitteeMember> addExistingUser() {
		List<ScCommitteeMember> memberList = new ArrayList<>();

		for (PmUser user : getManagedBean().getSelectedPmUsers()) {
			ScCommitteeMember committeeMember = new ScCommitteeMember();
			committeeMember.setMemberName(user.getUserName());
			committeeMember.setIcPassport(user.getIdentificationNo());
			committeeMember.setDesignation(user.getDesignation());
			committeeMember.setEmail(user.getEmail());
			committeeMember.setGrade(user.getGrade());
			committeeMember.setPNP(user.isPMP());
			committeeMember.setPmUser(user);
			setCommitteeMemberOrgProfile(committeeMember); 

			if (user.getMemberRoleId() == null) {
				PmParameter memberRole = ParameterHelper.getInstance().getParameterByTypeAndCode(
						ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_MEMBER);
				user.setMemberRoleId(memberRole.getParameterId());
				committeeMember.setRepresentativeType(new PmParameter());
				committeeMember.setRepresentativeTypeId(null);
			}
			PmParameter userMemberRole = ParameterHelper.getInstance().getPmParameterById(user.getMemberRoleId());
			committeeMember.setMemberRole(userMemberRole);
			if (committeeMember.getMemberRole() != null) {
				committeeMember.setMemberRoleId(committeeMember.getMemberRole().getParameterId());
			}
			committeeMember.setAccessRight(SourcingConstants.ACCESS_RIGHT_EDITABLE);
			committeeMember.setScCommittee(this.getManagedBean().getScQtCommittee().getScCommittee());
			setUserDetails(committeeMember);
			memberList.add(committeeMember);
		}
		return memberList;
	}

	// 34491 - end

	// 34491 - start
	/**
	 * Cancel action on confirmation box before user was added.
	 */
	public void committeeCancelAction() {
		resetUserAction();
		RequestContext.getCurrentInstance().execute("memberWidgetVar.hide()");
	}

	// 34491 - end

	// 34491 - start
	private void validateMeetingPreparation(String outcome) {
		if(!BpmOutcomeConst.BPM_SUBMIT.equals(outcome)){
			return;
		}
		boolean atleastOneMeetingIsDone = false;
		ScQtCommittee fcQtCommittee = this.getManagedBean().getFinalizationQtCommittee();
		ScCommittee committee = fcQtCommittee.getScCommittee();
		for (ScQtMeeting meeting : fcQtCommittee.getScQtMeetings()) {
			if ((DbValueConst.RECORD_STATUS_ACTIVE == meeting.getRecordStatusFlg())
					&& QTConstants.SC_MEETING_DONE_STATUS_ID.equals(meeting.getMeetingStatus().getStatusId())) {
				atleastOneMeetingIsDone = true;
			}
		}
		if (!atleastOneMeetingIsDone) {
			throw new EpException(MessageConst.ERROR_760292);
		}
		SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
		String committeeType = committee.getCommitteeType().getParameterCode();
		reqDTO.setCommitteeType(committeeType);
		reqDTO.setScQtCommittee(fcQtCommittee);
		reqDTO.setSubmit(true);
		getQtSuppFinalizationSO().validatePreparation(reqDTO);
	}
	// 34491 - end
}
