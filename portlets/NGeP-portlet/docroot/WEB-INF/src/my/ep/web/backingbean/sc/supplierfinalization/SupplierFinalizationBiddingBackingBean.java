/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.supplierfinalization;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.sc.BidItemBean;
import my.ep.biz.bean.sc.BidPackageBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.biz.service.sc.schedulebidding.IScheduleBiddingSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.comparator.sc.MemberRoleComparator;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.BidStatusConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sc.schedulebidding.ScheduleBiddingReqDTO;
import my.ep.dto.sc.schedulebidding.ScheduleBiddingResDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationEndorsementAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationEndorsementRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.CustomUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.supplierfinalization.SupplierFinalizationBiddingManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;
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
 * Implementation for supplier finalization by bidding
 * 
 */
@ManagedBean(name = "suppFinalBiddingBackingBean")
@RequestScoped
public class SupplierFinalizationBiddingBackingBean extends
		SourcingBaseBackingBean<SupplierFinalizationBiddingManagedBean> {
	private static final Logger log = EpLogger
			.getLogger(SupplierFinalizationBiddingBackingBean.class);

	private int currentTab;
	private String textBeforeSign;

	private static final int ONLINE_BIDDING_FINALIZATION_MEETING_ACTIVE_INDEX = 0;
	private static final int ONLINE_BIDDING_ACTIVE_INDEX_ONLINE_BRIEFING = 1;
	private static final int ONLINE_BIDDING_ACTIVE_INDEX_ONLINE_SUMMARY = 2;
	private static final int ONLINE_BIDDING_ACTIVE_INDEX_ONLINE_ENDORSEMENT = 3;
	
	private static final int ONLINE_BIDDING_ACTIVE_INDEX_ESCLATION_BRIEFING = 0;
	private static final int ONLINE_BIDDING_ACTIVE_INDEX_OFFLINE_SUMMARY = 1;
	private static final int ONLINE_BIDDING_ACTIVE_INDEX_OFFLINE_ENDORSEMENT = 2;
	
	// Bidding finalization meeting tab constants
	private static final int BIDDING_ACTIVE_INDEX_MEETING = 0;
	private static final int BIDDING_ACTIVE_INDEX_COMMITTEE = 1;
	private static final int BIDDING_ACTIVE_INDEX_JOIN_IN = 2;
	private static final String SPACE = " ";
	/**
	 * Default constructor
	 */
	
	// #27502 - Start
	private static boolean mofFinal = false;
	// #27502 - End
	
	public SupplierFinalizationBiddingBackingBean() {

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
			this.getManagedBean().setPayLoad(dataType);
			Long qtId = Long.valueOf(dataType.getQtId());
			String committeeType = null;
			
			if (StringUtils.isNotBlank(
					dataType.getCommitteeType())) {
				committeeType = String.valueOf(dataType.getCommitteeType());
				getManagedBean().setCommitteeType(committeeType);
			}
			getManagedBean().setPayLoad(dataType);
			
			retrieveOnlineBiddingEndorsement(qtId);
			// #25638 - Start
			displayReviseInstrn(getManagedBean().getCommitteeList());
			// #25638 - End
			
			// #27502 - Start			
			if (null != dataType.getIsMofFinalization()) {
				mofFinal = dataType.getIsMofFinalization();
			}
			
			if(!mofFinal){// #27502 - Excluded if MOF
				prepareUpdateCommitteeMember(); // 25037
			}
			checkSupplierAwardedRejectValidityExtensionPreRender();//#21692
			
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							QTConstants.FC_ONLINE_BIDDING_ENDORSEMENT_PAGE);			
			
			
			if (getManagedBean().isOnlineBiddingEscalation()) {		
				navigateToBriefingPaperTab();
			} else if(mofFinal){
				if(this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.SF_PENDING_BIDDING_FINALIZATION)){					
					getManagedBean().setMofSelection(true);	
				}
				getManagedBean().setMofFinalization(mofFinal);					
				navigateToBriefingPaperTab();
			} // #27502 - End
			else {
				navigateToBiddingFinalizationMeetingTab();
			}				
		} catch (Exception e) {
			handleException(e);
			fc.getApplication().getNavigationHandler()
			  .handleNavigation(fc, null,
					CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);			
		}
	}

	// 25037 - start
	/**
	 * Populate finalization preparation data
	 * @param qtId
	 */
	public void retrieveOnlineBiddingEndorsement(Long qtId) {
		// 25037 - end
		retriveQTDetails(qtId);
		if (StringUtils.isNotBlank(
				getManagedBean().getCommitteeType())) {
			retrieveCommitteeAndMeetingDetails(qtId);
		}
		
		retrieveSupplierFinalization(qtId);	
		if (ParameterConst.ONLINE_BIDDING_ESCALATION.equals(
				getManagedBean().getScQtFinalization().getOnlineBiddingEndorse())) {
			getManagedBean().setOnlineBiddingEscalation(true);
		}
//		generateZoneMenuModel();
		retrieveBiddingSummary();
		checkIfLoggedinUserIsSecretary();
		
		if (StringUtils.isNotBlank(
				getManagedBean().getCommitteeType())) {
			this.getManagedBean().setCommitteeTabLabel(
				getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE, 
								 getManagedBean().getCommitteeType()));	
		}
		
	}

	/**
	 * Retrieve quotation/tender details corresponding to the qtId
	 * @param qtId
	 * 			The id of the quotation/tender to be loaded
	 */
	public void retriveQTDetails(Long qtId) {
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
			getManagedBean().setZonal(scQt.isZonal());

		} finally {
			qtDetailSO = null;
		}
	}

	/**
	 * Retrieve supplier finalization details details corresponding to the qtId
	 * @param qtId
	 * 			The id of the quotation/tender 
	 */	
	public void retrieveSupplierFinalization(Long qtId) {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCurrentStatusId(getManagedBean().getCurrentStatusId());
			
			boolean isZonal = this.getManagedBean().getScQt().isZonal();
			reqDTO.setZonal(isZonal);
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
												.retrieveSupplierFinalization(reqDTO);
			ScQtFinalization scQtFinalization = resDTO.getScQtFinalization();
			
			if (ParameterConst.RECOMMEND_TYPE_ITEM.equals(
					scQtFinalization.getRecommendedTypeId().getParameterCode())) {
				getManagedBean().setItemized(true);
			} else {
				getManagedBean().setItemized(false);
			}
			
			if (isZonal) {
				this.getManagedBean().setBiddingZoneDetails(
						new ArrayList<TplSpecZoneDtl>());
				this.getManagedBean().setBidSchedulesByZone(
						new LinkedHashMap<Long, List<ScBidSchedule>>());
				this.getManagedBean().setBidPackageBeansByZone(
						new LinkedHashMap<Long, List<BidPackageBean>>());				
				this.getManagedBean().setBidItemsByZone(
						new LinkedHashMap<Long, List<ScBidItem>>());
				// #28777 & #29729 - Start
				this.getManagedBean().setBidDisSchedulesByZone(new LinkedHashMap<Long, List<ScBidSchedule>>());
				this.getManagedBean().setBidDisPackageBeansByZone(new LinkedHashMap<Long, List<BidPackageBean>>());
				// #28777 & #29729 - End
				Map<Long, List<ScQtRecommend>> scQtRecommendsByZone = 
							new LinkedHashMap<Long, List<ScQtRecommend>>();
				
				TplSpecZoneDtl specZoneDtl = null;
				Map<Long,TplSpecZoneDtl> zoneMap = getManagedBean().getZoneMap();
				if (zoneMap == null) {
					zoneMap = new LinkedHashMap<Long,TplSpecZoneDtl>();
					getManagedBean().setZoneMap(zoneMap);
				}
				
				List<TplSpecZoneDtl> biddingZoneDetails = new ArrayList<TplSpecZoneDtl>();
				
				for (ScQtRecommend scQtRecommend 
								: scQtFinalization.getScQtRecommends()) {
					if (scQtRecommend.isCancelled()) {
						continue;
					}
					
					specZoneDtl = scQtRecommend.getTplSpecZoneDtl();
					if (!biddingZoneDetails.contains(specZoneDtl)) {
						biddingZoneDetails.add(specZoneDtl);
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
						scQtRecommendsByZone.put(specZoneDtl.getSpecZoneId(), scQtRecommends);
					}
				}
				this.getManagedBean().setScQtRecommendsByZone(
										  scQtRecommendsByZone);
				if (CollectionUtils.isNotEmpty(biddingZoneDetails)) {
					this.getManagedBean().setBiddingZoneDetails(biddingZoneDetails);
					this.getManagedBean().setSelectedZone(biddingZoneDetails.get(0));
					generateZoneMenuModel();
				}
			}
			
			getManagedBean().setScQtFinalization(scQtFinalization);

			if (getManagedBean().getScQtCommittee() != null &&
					ParameterConst.ONLINE_BIDDING_MEETING.equals(
							scQtFinalization.getOnlineBiddingEndorse())) {
				retrieveOnlineEndorsement();
			} else {
				if (QTStatusConst.SF_PENDING_BIDDING_ESCALATION
						.equals(getManagedBean().getCurrentStatusId())) {
					getManagedBean().setBpkUser(true);
				}
				retrieveOfflineEndorsement();
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	// 25037 - start
	/**
	 * Populate committee and meeting data
	 * @param qtId
	 */
	public void retrieveCommitteeAndMeetingDetails(Long qtId) {
	// 25037 - end	
		IQTMeetingSO meetingSO = null;
		try {
			meetingSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTMeetingSO.class);

			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteType(getManagedBean().getCommitteeType());
			MeetingResDTO resDTO = meetingSO.retrieveMeetingDls(reqDTO);
			if (resDTO.isSuccessful()) {
				ScQtCommittee scQtCommittee = resDTO.getQtCommittee();
				
				// Meeting Details
				List<ScQtMeeting> meetingList = scQtCommittee.getScQtMeetings();
				List<ScQtMeeting> currentMeetingList 
										= new ArrayList<ScQtMeeting>();
				if (CollectionUtils.isNotEmpty(meetingList)) {
					for (ScQtMeeting scQtMeeting : meetingList) {
						if (DbValueConst.RECORD_STATUS_ACTIVE !=
									scQtMeeting.getRecordStatusFlg()) {
							continue;
						}
						currentMeetingList.add(scQtMeeting);
						Long meetingStatusId = scQtMeeting.getMeetingStatus() != null ? scQtMeeting
								.getMeetingStatus().getStatusId() : null;
						scQtMeeting.setMeetingStatusStr(meetingStatusId);
					}
				}

				this.getManagedBean().setMeetingList(currentMeetingList);
				this.getManagedBean().setScQtCommittee(scQtCommittee);
				this.getManagedBean().setMeetingAttendanceList(
							scQtCommittee.getMeetingAttendances());				
			}
		} finally {
			meetingSO = null;
		}
	}
	
	private void generateZoneMenuModel() {
		if (CollectionUtils.isNotEmpty(
				this.getManagedBean().getBiddingZoneDetails())) {
			List<TplSpecZoneDtl> zoneDtls = this.getManagedBean().getBiddingZoneDetails();

			// Dynamically generating the tabs
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
		
		List<TplSpecZoneDtl> zoneDetails = this.getManagedBean().getBiddingZoneDetails();
		this.getManagedBean().setSelectedZone(zoneDetails.get(selectedZoneTabIndex));
		this.getManagedBean().setZoneActiveIndex(selectedZoneTabIndex);
		retrieveBiddingSummary();
		getManagedBean().setBidItemSuppliers(null);
		getManagedBean().setBidItemSuppliersDisqualified(null); // #28777&#29729
	}
	
	private void onZoneTabChangeAction(TplSpecZoneDtl specZoneDtl) {
		boolean isItemized = getManagedBean().isItemized();
		this.getManagedBean().setSelectedZone(specZoneDtl);
		this.getManagedBean().setZoneActiveIndex(specZoneDtl.getZoneTabIndex());
		
		if (isItemized) {
			getManagedBean().setBidItemSuppliers(null);
			getManagedBean().setBidItemSuppliersDisqualified(null); // #28777&#29729
		} else {
			getManagedBean().setBiddingPackagePriceList(null);
			getManagedBean().setBiddingPackagePriceListDisqualified(null); // #28777&#29729
		}
		
		retrieveBiddingSummary();
	}	

	/**
	 * To retrieve the details of Online Bidding Summary
	 */
	public void retrieveBiddingSummary() {
		navigateToBiddingSummaryTab();
		
		ScQt scQt = getManagedBean().getScQt();
		boolean isZonal = scQt.isZonal();
		String recommendType = getManagedBean().getScQtFinalization()
				.getRecommendedTypeId().getParameterCode();
		ScheduleBiddingReqDTO reqDTO = new ScheduleBiddingReqDTO();
		Long qtId = scQt.getQtId();
		reqDTO.setQtID(qtId);
		
		if (isZonal) {
			reqDTO.setSelectedZoneId(
					this.getManagedBean().getSelectedZone().getSpecZoneId());
		}
		if (recommendType.equals(
				ParameterConst.RECOMMEND_TYPE_PACKAGE)) {
			reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
			retrieveRecommendedSuppliersByPkg(reqDTO);
		} else if (recommendType.equals(
				ParameterConst.RECOMMEND_TYPE_ITEM)) {
			retrieveBiddingItems(reqDTO);
		}
		//25638 - Start.
		initializeFinalizationRemarks();
		//25638 - End.
	}
	
	private void retrieveRecommendedSuppliersByPkg(ScheduleBiddingReqDTO reqDTO) {
		IBiddingSO biddingSO = null;
		IScheduleBiddingSO scheduleBiddingSO = null;
		
		TplSpecZoneDtl selectedZone = null;
		Long selectedZoneId = null;
		List<ScBidSchedule> currentZoneBidSchedules = null;
		List<BidPackageBean> currentZoneBidPackageBeans = null;
		List<ScQtEvaluationResultDtl> shortlistedSuppliers = null;
		ScQtRecommendDtl awardedSupplier = null;
		List<ScQtRecommend> recommendedItems = null;
		
		// #28777 & #29729 - Start
		List<ScQtEvaluationResultDtl> notShortlistedSuppliers = new ArrayList<>();
		Map<Long, List<ScQtEvaluationResultDtl>> pkgNotShortlistedSuppliersByZone = getManagedBean()
				.getPkgNotShortlistedSuppliersByZone();
		List<BidPackageBean> currentDisZoneBidPackageBeans = new ArrayList<>();
		// #28777 & #29729 - End
		
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		Map<Long,List<ScQtEvaluationResultDtl>> pkgShortlistedSuppliersByZone
					= getManagedBean().getPkgShortlistedSuppliersByZone();	
		Map<Long,BidPackageBean> selectedPackageBeans 
				= getManagedBean().getSelectedPackageBeans();
		
		try {			
			if (isZonal) {
				if (selectedPackageBeans == null) {
					selectedPackageBeans = new HashMap<Long,BidPackageBean>();
					getManagedBean().setSelectedPackageBeans(selectedPackageBeans);
				}
				selectedZone = this.getManagedBean().getSelectedZone();
				selectedZoneId = selectedZone.getSpecZoneId();
				recommendedItems = getManagedBean().getScQtRecommendsByZone().get(selectedZoneId);
				
				if (MapUtils.isNotEmpty(this.getManagedBean().getBidSchedulesByZone())
						|| MapUtils.isNotEmpty(this.getManagedBean().getBidDisSchedulesByZone())) {// #28777&#29729
					currentZoneBidSchedules = this.getManagedBean().getBidSchedulesByZone().get(selectedZoneId);
					currentZoneBidPackageBeans = this.getManagedBean().getBidPackageBeansByZone().get(selectedZoneId);
					
					// #28777 & #29729 - Start
					currentDisZoneBidPackageBeans = this.getManagedBean().getBidDisPackageBeansByZone()
							.get(selectedZoneId);
					// #28777 & #29729 - End
				}
				if (pkgShortlistedSuppliersByZone == null || pkgNotShortlistedSuppliersByZone == null) { // #28777&#29729
					pkgShortlistedSuppliersByZone 
								= new HashMap<Long,List<ScQtEvaluationResultDtl>>();
					getManagedBean().setPkgShortlistedSuppliersByZone(
										pkgShortlistedSuppliersByZone);
					
					// #28777 & #29729 - Start
					pkgNotShortlistedSuppliersByZone = new HashMap<Long, List<ScQtEvaluationResultDtl>>();
					getManagedBean().setPkgNotShortlistedSuppliersByZone(pkgNotShortlistedSuppliersByZone);
					// #28777 & #29729 - End
				} else {
					shortlistedSuppliers = getManagedBean().getPkgShortlistedSuppliersByZone()
												.get(selectedZoneId);
					getManagedBean().setShortlistedSuppliers(shortlistedSuppliers);
					
					// #28777 & #29729 - Start
					notShortlistedSuppliers = getManagedBean().getPkgNotShortlistedSuppliersByZone()
							.get(selectedZoneId);
					getManagedBean().setNotShortlistedSuppliers(notShortlistedSuppliers);
					// #28777 & #29729 - End
				}
			} else {
				shortlistedSuppliers = getManagedBean().getShortlistedSuppliers();
				notShortlistedSuppliers = getManagedBean().getNotShortlistedSuppliers();// #28777&#29729
				ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
				recommendedItems = scQtFinalization.getScQtRecommends();
			}
			
			// Get awarded supplier
			if (CollectionUtils.isNotEmpty(recommendedItems)) {
				awardedSupplier = getAwardedSupplier(recommendedItems.get(0).getRecommendDtls());
			}
			
			if (CollectionUtils.isNotEmpty(currentZoneBidSchedules)
					&& (CollectionUtils.isNotEmpty(shortlistedSuppliers) || CollectionUtils
							.isNotEmpty(notShortlistedSuppliers))) {// #28777&#29729
				processPackageBidSchedules(currentZoneBidSchedules, currentZoneBidPackageBeans,
						currentDisZoneBidPackageBeans, awardedSupplier);

			} else {
				scheduleBiddingSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IScheduleBiddingSO.class);
				ScheduleBiddingResDTO resDTO = scheduleBiddingSO
									.retrieveBiddingScheduleByDocId(reqDTO);
				List<ScBidSchedule> bidScheduleList = resDTO
						.getBidSchedules();	
				biddingSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);	
				BiddingReqDTO bidReqDTO = new BiddingReqDTO();
				bidReqDTO.setQtId(getManagedBean().getScQt().getQtId());
				bidReqDTO.setRecommendType(SourcingConstants.EVAL_STAGE_RECOMMENDATION_PKG);
				bidReqDTO.setSpecZoneId(selectedZoneId);
				bidReqDTO.setBidScheduleId(bidScheduleList.get(0).getBidScheduleId());// #28777&#29729
				
				BiddingResDTO bidResDTO = null;
				if (CollectionUtils.isEmpty(shortlistedSuppliers) || CollectionUtils.isEmpty(notShortlistedSuppliers)) {// #28777&#29729
					bidResDTO = biddingSO.retrieveShortlistedSuppliersForBidding(bidReqDTO);
					shortlistedSuppliers = bidResDTO.getShortlistedSuppliers();
					notShortlistedSuppliers = bidResDTO.getNotShortlistedSuppliers(); //#28777&#29729
					if (isZonal) {
						pkgShortlistedSuppliersByZone.put(
								selectedZoneId, shortlistedSuppliers);
						pkgNotShortlistedSuppliersByZone.put(selectedZoneId, notShortlistedSuppliers);// #28777&#29729
					}
				}
				this.getManagedBean().setShortlistedSuppliers(
										shortlistedSuppliers);
				this.getManagedBean().setNotShortlistedSuppliers(notShortlistedSuppliers); // #28777&#29729
				Long bidScheduleId = bidScheduleList.get(0)
										.getBidScheduleId();				
				bidReqDTO.setBidScheduleId(bidScheduleId);				
				bidResDTO = biddingSO.retrieveBidScheduleDtl(bidReqDTO);				

				if (bidResDTO.isSuccessful()) {
					processPackageBidSchedules(bidScheduleList, bidResDTO.getBidPackages(),
							bidResDTO.getBidPackagesDisqualified(), awardedSupplier);
					if (isZonal) {
						this.getManagedBean().getBidSchedulesByZone()
							.put(selectedZoneId, bidScheduleList);
						this.getManagedBean().getBidPackageBeansByZone()
							.put(selectedZoneId, bidResDTO.getBidPackages());	
						this.getManagedBean().getBidDisPackageBeansByZone()
						.put(selectedZoneId, bidResDTO.getBidPackagesDisqualified());// #28777&#29729
					}
				}
			}
		} finally {
			biddingSO = null;
			scheduleBiddingSO = null;
		}
	}
	
	private ScQtRecommendDtl getAwardedSupplier(List<ScQtRecommendDtl> recommendDtls) {
		ScQtRecommendDtl awardedSupplier = null;
		if (CollectionUtils.isNotEmpty(recommendDtls)) {
			for (ScQtRecommendDtl recommendDtl : recommendDtls) {
				if (recommendDtl.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					awardedSupplier = recommendDtl;
					break;
				}
			}	
		}
		return awardedSupplier;
	}
	
	private Map<Long,ScQtEvaluationResultDtl> getSupplierCodes(
					List<ScQtEvaluationResultDtl> shortlistedSuppliers) {
		Map<Long, ScQtEvaluationResultDtl> supplierCodes = null;
		
		if (CollectionUtils.isNotEmpty(shortlistedSuppliers)) {
			ScQtProposal scQtProposal = null;
			supplierCodes = new HashMap<Long, ScQtEvaluationResultDtl>();
			
			for (ScQtEvaluationResultDtl shortlistedSupplier 
						: shortlistedSuppliers) {
				scQtProposal = shortlistedSupplier.getQtEvaluationResult().getScQtProposal();
				supplierCodes.put(scQtProposal.getScQtSupplier().getSmSupplier().getSupplierId(), 
						shortlistedSupplier);
			}
		}
		
		return supplierCodes;
	}
	
	private void processPackageBidSchedules(List<ScBidSchedule> bidScheduleList,
			List<BidPackageBean> biddingPackagePriceList, List<BidPackageBean> biddingPackagePriceListDis,
			ScQtRecommendDtl awardedSupplier) {
		
		disableSupplierSelection(false);
		getManagedBean().setSelectedPackageBean(null);
		Map<Long, BidPackageBean> selectedPackageBeans = getManagedBean().getSelectedPackageBeans();
		BigDecimal estimatedCost = new BigDecimal(0);
		if (CollectionUtils.isNotEmpty(bidScheduleList)) {
			if (BidStatusConst.BID_CANCELLED.equals(bidScheduleList.get(0).getPmStatus().getStatusId())) {
				disableSupplierSelection(true);
			}
			if (CollectionUtils.isNotEmpty(bidScheduleList.get(0).getScBidItems())) {
				for (ScBidItem bidItem : bidScheduleList.get(0).getScBidItems()) {
					if (bidItem.getEstimatedCost() != null) {
						estimatedCost = estimatedCost.add(bidItem.getEstimatedCost());
					}
				}
			}
		}

		List<ScQtEvaluationResultDtl> shortlistedSuppliers = getManagedBean().getShortlistedSuppliers();
		Map<Long, ScQtEvaluationResultDtl> supplierCodes = getSupplierCodes(shortlistedSuppliers);

		// #28777 & #29729 - Start
		List<ScQtEvaluationResultDtl> notShortlistedSuppliers = getManagedBean().getNotShortlistedSuppliers();
		Map<Long, ScQtEvaluationResultDtl> disSupplierCodes = getSupplierCodes(notShortlistedSuppliers);
		// #28777 & #29729 - End

		Long awardedSupplierId = null;
		// Get awarded supplier id
		if (awardedSupplier != null) {
			awardedSupplierId = awardedSupplier.getSupplier().getSupplierId();
		}

		getManagedBean().setScheduledBid(bidScheduleList.get(0));
		getManagedBean().setEstimatedCost(estimatedCost);

		/*
		 * 31101 - Shafful
		 */
		
		settingDisciplinaryAction(biddingPackagePriceList, null, false);// #28777&#29729
		getManagedBean().setBiddingPackagePriceList(biddingPackagePriceList);
		
		// #28777 & #29729 - Start
		if (CollectionUtils.isNotEmpty(biddingPackagePriceListDis)) {
			settingDisciplinaryAction(biddingPackagePriceListDis, null, true);
			getManagedBean().setBiddingPackagePriceListDisqualified(biddingPackagePriceListDis);
		}
		// #28777 & #29729 - End

		BigDecimal bumiSuppLowestOfferedPrice = null;
		BigDecimal lowestNonBumiOfferedPrice = null;

		List<BigDecimal> bumiPriceList = new ArrayList<BigDecimal>();
		List<BigDecimal> nonBumiPriceList = new ArrayList<BigDecimal>();

		// Set lowest non-bumi offered price
		if (CollectionUtils.isNotEmpty(biddingPackagePriceList)) {
			for (BidPackageBean packageBean : biddingPackagePriceList) {
				SmSupplier smSupplier = packageBean.getScBidSupplier().getSmSupplier();

				if (smSupplier.isBumi()) {
					bumiPriceList.add(packageBean.getBidPriceTotal());
				} else {
					nonBumiPriceList.add(packageBean.getBidPriceTotal());
				}
				if (StringUtils.isBlank(packageBean.getSupplierName())) {
					packageBean.setSupplierName(packageBean.getScBidSupplier().getSmSupplier().getCompanyName());
				}

				Long supplierId = smSupplier.getSupplierId();
				if (MapUtils.isNotEmpty(supplierCodes) && supplierCodes.containsKey(supplierId)) {
					ScQtEvaluationResultDtl shortlistedSupplier = supplierCodes.get(supplierId);
					packageBean.setScQtProposal(shortlistedSupplier.getQtEvaluationResult().getScQtProposal());
					packageBean.setShortlistedSupplier(shortlistedSupplier);
				}
				
				if (supplierId.equals(awardedSupplierId)) {
					// 26247, 31101 - start
					SmSupplier supplier = packageBean.getScBidSupplier().getSmSupplier();
					getManagedBean().setSelectedPackageBean(null);
					if(!this.disabledQTProcess(supplier)){
						getManagedBean().setSelectedPackageBean(packageBean);
					}
					// 26247, 31101 - end
					if (getManagedBean().isZonal()) {
						selectedPackageBeans.put(getManagedBean().getSelectedZone().getSpecZoneId(), packageBean);
					}
				}
			}
			
			// #28777 & #29729 - Start
			for (BidPackageBean packageBean : biddingPackagePriceListDis) {
				SmSupplier smSupplier = packageBean.getScBidSupplier().getSmSupplier();

				if (smSupplier.isBumi()) {
					bumiPriceList.add(packageBean.getBidPriceTotal());
				} else {
					nonBumiPriceList.add(packageBean.getBidPriceTotal());
				}
				if (StringUtils.isBlank(packageBean.getSupplierName())) {
					packageBean.setSupplierName(packageBean.getScBidSupplier().getSmSupplier().getCompanyName());
				}

				Long supplierId = smSupplier.getSupplierId();
				if (MapUtils.isNotEmpty(disSupplierCodes) && disSupplierCodes.containsKey(supplierId)) {
					ScQtEvaluationResultDtl notShortlistedSupplier = disSupplierCodes.get(supplierId);
					packageBean.setScQtProposal(notShortlistedSupplier.getQtEvaluationResult().getScQtProposal());
					packageBean.setShortlistedSupplier(notShortlistedSupplier);
				}
			}
			// #28777 & #29729 - End
			if (CollectionUtils.isNotEmpty(bumiPriceList)) {
				Collections.sort(bumiPriceList);
				bumiSuppLowestOfferedPrice = bumiPriceList.get(0);
			}
			if (CollectionUtils.isNotEmpty(nonBumiPriceList)) {
				Collections.sort(nonBumiPriceList);
				lowestNonBumiOfferedPrice = nonBumiPriceList.get(0);
			}
		}

		getManagedBean().setBumiSuppLowestOfferedPrice(bumiSuppLowestOfferedPrice);
		getManagedBean().setLowestNonBumiOfferedPrice(lowestNonBumiOfferedPrice);

		if (CollectionUtils.isNotEmpty(nonBumiPriceList)) {
			getManagedBean().setNonBumiAvailable(true);
		}
	}
	
	// 26247 - start
	private void settingDisciplinaryAction(List<BidPackageBean> biddingPackagePriceList,
			List<BidItemBean> biddingItemPriceList, boolean isDisqualified) {// #28777&#29729
		ScQt scQt = this.getManagedBean().getScQt();
		if (CollectionUtils.isNotEmpty(biddingPackagePriceList)) {
			for (BidPackageBean bidPackage : biddingPackagePriceList) {
				SmSupplier supplier = bidPackage.getScBidSupplier().getSmSupplier();
				getQTDetailSO().evaluateDisciplinaryActions(supplier, scQt.getQtId());
			} 
			
			if (!isDisqualified) {// #28777 & #29729
				getManagedBean().setBiddingPackagePriceList(biddingPackagePriceList);
			}// #28777 & #29729
			
		} else if (CollectionUtils.isNotEmpty(biddingItemPriceList)) {
			for (BidItemBean bidItem : biddingItemPriceList) {
				SmSupplier supplier = bidItem.getSupplier();
				getQTDetailSO().evaluateDisciplinaryActions(supplier, scQt.getQtId());
			} 
		} 
	} 
	// 26247 - end
	
	private void retrieveBiddingItems(ScheduleBiddingReqDTO reqDTO) {
		IScheduleBiddingSO scheduleBiddingSO = null;
		try {
			TplSpecZoneDtl selectedZone = this.getManagedBean().getSelectedZone();
			boolean isZonal = this.getManagedBean().getScQt().isZonal();
			List<ScBidItem> biddingItemList = null;
			if (isZonal) {
				if (MapUtils.isNotEmpty(
						this.getManagedBean().getBidItemsByZone())) {
					biddingItemList = this.getManagedBean().getBidItemsByZone()
						.get(selectedZone.getSpecZoneId());
				}
			}
			if (CollectionUtils.isEmpty(biddingItemList)) {
				scheduleBiddingSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IScheduleBiddingSO.class);
				
				ScheduleBiddingResDTO resDTO = scheduleBiddingSO
						.retrieveBiddingItem(reqDTO);
				if (resDTO.isSuccessful()) {
					biddingItemList = resDTO.getBidItemsList();
				}				
			}
			if (isZonal) {
				this.getManagedBean().getBidItemsByZone()
					.put(selectedZone.getSpecZoneId(), biddingItemList);
			}
			
			getManagedBean().setBiddingItemList(biddingItemList);
		} finally {
			scheduleBiddingSO = null;
		}
	}
	
	/**
	 * To navigate to Meeting tab
	 */
	public void navigateToBiddingFinalizationMeetingTab() {
		this.getManagedBean().setOnlineBiddingActiveIndex(
				ONLINE_BIDDING_FINALIZATION_MEETING_ACTIVE_INDEX);
	}	
	
	/**
	 * To navigate to bidding summary tab
	 */
	public void navigateToBiddingSummaryTab() {
		int activeIndex;
		if (mofFinal) {// #27502 - Start
		  activeIndex = 1;
		} else {
    		if (getManagedBean().isOnlineBiddingEscalation()) {
    			activeIndex = ONLINE_BIDDING_ACTIVE_INDEX_OFFLINE_SUMMARY;
    		} else {
    			activeIndex = ONLINE_BIDDING_ACTIVE_INDEX_ONLINE_SUMMARY;
    		}
		}
		this.getManagedBean().setOnlineBiddingActiveIndex(activeIndex);
	}

	/**
	 * Event to handle selection of bidding item
	 * @param event
	 * 			selection event object
	 */
	public void selectBiddingItem(SelectEvent event) {
		disableSupplierSelection(false);
		getManagedBean().setSelectedBidItemBean(null);
		getManagedBean().setLowestNonBumiOfferedPrice(null);
		
		IBiddingSO biddingSO = null;
		ScBidItem bidItem = (ScBidItem) event.getObject();
		getManagedBean().setSelectedBiddingItem(bidItem);
		
		if (BidStatusConst.BID_CANCELLED.equals(
				bidItem.getScBidSchedule().getPmStatus().getStatusId())) {
			disableSupplierSelection(true);
		}
		
		boolean isZonal = getManagedBean().getScQt().isZonal();
		List<ScQtEvaluationResultDtl> shortlistedSuppliers = null;
		Map<Long,List<ScQtEvaluationResultDtl>> itemShortlistedSuppliers 
					= getManagedBean().getItemShortlistedSuppliers();
		Map<Long,Map<Long,List<ScQtEvaluationResultDtl>>> itemShortlistedSuppliersByZone
					= getManagedBean().getItemShortlistedSuppliersByZone();
		
		// #28777 & #29729 - Start
		List<ScQtEvaluationResultDtl> notShortlistedSuppliers = new ArrayList<>();
		Map<Long, List<ScQtEvaluationResultDtl>> itemNotShortlistedSuppliers = getManagedBean()
				.getItemNotShortlistedSuppliers();
		Map<Long, Map<Long, List<ScQtEvaluationResultDtl>>> itemNotShortlistedSuppliersByZone = getManagedBean()
				.getItemNotShortlistedSuppliersByZone();
		// #28777 & #29729 - End
		
		Map<Long,BidItemBean> selectedItemBeans 
					= getManagedBean().getSelectedItemBeans();
		if (selectedItemBeans == null) {
			selectedItemBeans = new HashMap<Long,BidItemBean>();
			getManagedBean().setSelectedItemBeans(selectedItemBeans);
		}
		
		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			Long selectedZoneId = null;
			List<ScQtRecommend> recommendedItems = null;
			if (isZonal) {
				selectedZoneId = getManagedBean().getSelectedZone().getSpecZoneId();
				
				if (itemShortlistedSuppliersByZone == null) {
					itemShortlistedSuppliersByZone 
						= new HashMap<Long,Map<Long,List<ScQtEvaluationResultDtl>>>();
					getManagedBean().setItemShortlistedSuppliersByZone(
							itemShortlistedSuppliersByZone);
				} else if (itemShortlistedSuppliersByZone
									.containsKey(selectedZoneId)) {
						shortlistedSuppliers = itemShortlistedSuppliersByZone
								.get(selectedZoneId).get(bidItem.getBidItemId());
				}
				
				// #28777 & #29729 - Start
				if (itemNotShortlistedSuppliersByZone == null) {
					itemNotShortlistedSuppliersByZone = new HashMap<Long, Map<Long, List<ScQtEvaluationResultDtl>>>();
					getManagedBean().setItemNotShortlistedSuppliersByZone(itemNotShortlistedSuppliersByZone);
				} else if (itemNotShortlistedSuppliersByZone.containsKey(selectedZoneId)) {
					notShortlistedSuppliers = itemNotShortlistedSuppliersByZone.get(selectedZoneId).get(
							bidItem.getBidItemId());
				}
				// #28777 & #29729 - End
				
				recommendedItems = getManagedBean().getScQtRecommendsByZone().get(selectedZoneId);
			} else {
				if (itemShortlistedSuppliers == null) {
					itemShortlistedSuppliers 
							= new HashMap<Long,List<ScQtEvaluationResultDtl>>();
					getManagedBean().setItemShortlistedSuppliers(
							itemShortlistedSuppliers);
				} else {
					shortlistedSuppliers = itemShortlistedSuppliers
											.get(bidItem.getBidItemId());	
				}
				
				// #28777 & #29729 - Start
				if (itemNotShortlistedSuppliers == null) {
					itemNotShortlistedSuppliers = new HashMap<Long, List<ScQtEvaluationResultDtl>>();
					getManagedBean().setItemNotShortlistedSuppliers(itemNotShortlistedSuppliers);
				} else {
					notShortlistedSuppliers = itemNotShortlistedSuppliers.get(bidItem.getBidItemId());
				}
				// #28777 & #29729 - End
				
				ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
				recommendedItems = scQtFinalization.getScQtRecommends();
			}
			
			BiddingReqDTO reqDTO = new BiddingReqDTO();
			reqDTO.setQtId(getManagedBean().getScQt().getQtId());
			reqDTO.setRecommendType(SourcingConstants.EVAL_STAGE_RECOMMENDATION_ITM);			
			reqDTO.setSpecZoneId(selectedZoneId);
			reqDTO.setScBidSchedule(bidItem.getScBidSchedule());
			reqDTO.setBidItemId(bidItem.getScRequestItem().getRequestNoteItemId());
			reqDTO.setBidScheduleId(bidItem.getScBidSchedule().getBidScheduleId());// #28777&#29729
			
			BiddingResDTO resDTO = null;
			if (CollectionUtils.isEmpty(shortlistedSuppliers) || CollectionUtils.isEmpty(notShortlistedSuppliers)) {// #28777&#29729
				resDTO = biddingSO.retrieveShortlistedSuppliersForBidding(reqDTO);
				shortlistedSuppliers = resDTO.getShortlistedSuppliers();
				notShortlistedSuppliers = resDTO.getNotShortlistedSuppliers(); // #28777&#29729
				
				if (isZonal) {
					Map<Long,List<ScQtEvaluationResultDtl>> shortlistedSuppliersMap = null;
					shortlistedSuppliersMap = itemShortlistedSuppliersByZone
												.get(selectedZoneId);
					if (itemShortlistedSuppliersByZone
								.containsKey(selectedZoneId)) {
						if (MapUtils.isEmpty(shortlistedSuppliersMap)) {
							shortlistedSuppliersMap
								= new HashMap<Long,List<ScQtEvaluationResultDtl>>();
						}
					} else {
						shortlistedSuppliersMap
								= new HashMap<Long,List<ScQtEvaluationResultDtl>>();
					}
					shortlistedSuppliersMap.put(bidItem.getBidItemId(),
												shortlistedSuppliers);
					itemShortlistedSuppliersByZone.put(
							selectedZoneId, shortlistedSuppliersMap);	
					
					// #28777 & #29729 - Start
					Map<Long, List<ScQtEvaluationResultDtl>> notShortlistedSuppliersMap = null;
					notShortlistedSuppliersMap = itemNotShortlistedSuppliersByZone.get(selectedZoneId);
					if (itemNotShortlistedSuppliersByZone.containsKey(selectedZoneId)) {
						if (MapUtils.isEmpty(shortlistedSuppliersMap)) {
							notShortlistedSuppliersMap = new HashMap<Long, List<ScQtEvaluationResultDtl>>();
						}
					} else {
						notShortlistedSuppliersMap = new HashMap<Long, List<ScQtEvaluationResultDtl>>();
					}
					notShortlistedSuppliersMap.put(bidItem.getBidItemId(), notShortlistedSuppliers);
					itemNotShortlistedSuppliersByZone.put(selectedZoneId, notShortlistedSuppliersMap);
					// #28777 & #29729 - End
					
				} else {
					itemShortlistedSuppliers.put(
							bidItem.getBidItemId(), shortlistedSuppliers);
					itemNotShortlistedSuppliers.put(bidItem.getBidItemId(), notShortlistedSuppliers);//#28777 & #29729 
				}
			}

			Long awardedSupplierId = null;
			// Get awarded supplier id
			if (CollectionUtils.isNotEmpty(recommendedItems)) {
				for (ScQtRecommend qtRecommend : recommendedItems) {
					if (!qtRecommend.isCancelled() && 
							qtRecommend.getRequestItem().getRequestNoteItemId().equals(
								bidItem.getScRequestItem().getRequestNoteItemId())) {
						ScQtRecommendDtl awardedSupplier = getAwardedSupplier(qtRecommend.getRecommendDtls());
						if (awardedSupplier != null) {
							awardedSupplierId = awardedSupplier.getSupplier().getSupplierId();
						}
						break;
					}
				}
			}
			
			Map<Long,ScQtEvaluationResultDtl> supplierCodes 
						= getSupplierCodes(shortlistedSuppliers);
			// #28777 & #29729 - Start
			Map<Long, ScQtEvaluationResultDtl> disSupplierCodes = getSupplierCodes(notShortlistedSuppliers);
			// #28777 & #29729 - End
			
			reqDTO.setBidItemId(bidItem.getBidItemId());
			resDTO = biddingSO.retrieveLastBidPricesByItem(reqDTO);
			if (resDTO.isSuccessful()) {
			
				// #28777 & #29729 - Start
				if (CollectionUtils.isNotEmpty(resDTO.getBidPriceItemsDisqualified())) {
					processSelectBiddingItem(resDTO.getBidPriceItemsDisqualified(), disSupplierCodes,
							null, selectedItemBeans, bidItem, true);
				}
				processSelectBiddingItem(resDTO.getBidPriceItems(), supplierCodes, awardedSupplierId,
						selectedItemBeans, bidItem, false);
				// #28777 & #29729 - End
		
			}
			
		} finally {
			biddingSO = null;
		}
	}
	
	// #28777 & #29729 - Start
	/**
	 * Method process select bidding item and set bid suppliers items
	 * 
	 * @param biddingItemPriceList
	 * @param supplierCodes
	 * @param awardedSupplierId
	 * @param selectedItemBeans
	 * @param bidItem
	 * @param isDisqualified
	 **/
	private void processSelectBiddingItem(List<BidItemBean> biddingItemPriceList,
			Map<Long, ScQtEvaluationResultDtl> supplierCodes, Long awardedSupplierId,
			Map<Long, BidItemBean> selectedItemBeans, ScBidItem bidItem, boolean isDisqualified) {

		if (CollectionUtils.isNotEmpty(biddingItemPriceList)) {
			List<BigDecimal> nonBumiPriceList = new ArrayList<BigDecimal>();

			/*
			 * 31101 - WIP Impact , added Disciplinary Action
			 */
			settingDisciplinaryAction(null, biddingItemPriceList, isDisqualified);

			for (BidItemBean itemBean : biddingItemPriceList) {
				SmSupplier smSupplier = itemBean.getSupplier();
				itemBean.setSupplierName(smSupplier.getCompanyName());

				if (!itemBean.isBumi()) {
					nonBumiPriceList.add(itemBean.getBidPriceTotal());
				}
				if (MapUtils.isNotEmpty(supplierCodes) && supplierCodes.containsKey(smSupplier.getSupplierId())) {
					ScQtEvaluationResultDtl shortlistedSupplier = supplierCodes.get(smSupplier.getSupplierId());
					itemBean.setScQtProposal(shortlistedSupplier.getQtEvaluationResult().getScQtProposal());
					itemBean.setShortlistedSupplier(shortlistedSupplier);
				}
				// 26247, 31101 - start
				SmSupplier supplier = itemBean.getSupplier();
				if (smSupplier.getSupplierId().equals(awardedSupplierId) && !this.disabledQTProcess(supplier)) {
				// 26247, 31101 - end
					getManagedBean().setSelectedBidItemBean(itemBean);
					selectedItemBeans.put(bidItem.getBidItemId(), itemBean);
				}
			}

			if (CollectionUtils.isNotEmpty(nonBumiPriceList)) {
				Collections.sort(nonBumiPriceList);
				if (nonBumiPriceList.get(0) != null) {
					getManagedBean().setLowestNonBumiOfferedPrice(nonBumiPriceList.get(0));
				}
			}
		}

		if (isDisqualified) {
			getManagedBean().setBidItemSuppliersDisqualified(biddingItemPriceList);
		} else {
			getManagedBean().setBidItemSuppliers(biddingItemPriceList);
		}
	}

	// #28777 & #29729 - End
	
	/**
	 * Award Supplier event
	 */
	public void awardSupplierEvent(AjaxBehaviorEvent ajaxEvent) {
		boolean isZonal = getManagedBean().getScQt().isZonal();
		boolean isItemized = getManagedBean().isItemized();
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		
		if (isItemized) {
			ScBidItem selectedBidItem 
							= getManagedBean().getSelectedBiddingItem();
			BidItemBean selectedSupplier 
							= getManagedBean().getSelectedBidItemBean();
			Long selectedSupplierId = selectedSupplier.getSupplier().getSupplierId();
			
			Map<Long,BidItemBean> selectedItemBeans
					= getManagedBean().getSelectedItemBeans();
			selectedItemBeans.put(selectedBidItem.getBidItemId(), 
								  selectedSupplier);
			
			List<ScQtRecommend> recommendedItems = null;
			if (isZonal) {
				Long selectedZoneId = getManagedBean().getSelectedZone().getSpecZoneId();
				recommendedItems = getManagedBean().getScQtRecommendsByZone().get(selectedZoneId);
			} else {
				recommendedItems = scQtFinalization.getScQtRecommends();
			}
			
			List<ScQtRecommend> selectedItems = new ArrayList<ScQtRecommend>();
			for (ScQtRecommend qtRecommend : recommendedItems) {
				if (!qtRecommend.isCancelled() && 
						qtRecommend.getRequestItem().getRequestNoteItemId().equals(
							selectedBidItem.getScRequestItem().getRequestNoteItemId())) {
					selectedItems.add(qtRecommend);
					updateRecommendDetails(selectedItems, selectedSupplierId);
					break;
				}
			}			
		} else {
			BidPackageBean selectedPackageBean 
								= getManagedBean().getSelectedPackageBean();
			Long selectedSupplierId = selectedPackageBean.getShortlistedSupplier().getQtEvaluationResult()
										.getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierId();
			
			List<ScQtRecommend> recommendedItems = null;
			if (isZonal) {
				Long selectedZoneId = getManagedBean().getSelectedZone().getSpecZoneId();
				recommendedItems = getManagedBean().getScQtRecommendsByZone().get(selectedZoneId);
				Map<Long,BidPackageBean> selectedPackageBeansByZone
							= getManagedBean().getSelectedPackageBeans();
				selectedPackageBeansByZone.put(selectedZoneId, selectedPackageBean);
			} else {
				recommendedItems = scQtFinalization.getScQtRecommends();
			}
			updateRecommendDetails(recommendedItems, selectedSupplierId);
		}
	}
	
	private void updateRecommendDetails(List<ScQtRecommend> itemList, Long selectedSupplierId) {
		for (ScQtRecommend item : itemList) {
			for (ScQtRecommendDtl detail : item.getRecommendDtls()) {
				if (detail.getSupplier().getSupplierId().equals(selectedSupplierId)) {
					// update record status flag to active.
					if (detail.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
						detail.setRecordStatus(true);
						detail.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);	
					} else if (detail.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE) {
						debugLogger("updateRecommendDetails-->recommendDtlId:" + detail.getRecommendDtlId() +", status not updated to active: " + detail.getRecordStatusFlg());
					}
				} else {
					// update record status flag to suspended.
					if (detail.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
						detail.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);	
					} else if (detail.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_SUSPENDED) {
						debugLogger("updateRecommendDetails-->recommendDtlId:" + detail.getRecommendDtlId() +", status not updated to suspended: " + detail.getRecordStatusFlg());
					}
					detail.setRecordStatus(false);
				}	
			}
		}		
	}
	
	private void disableSupplierSelection(boolean disable) {
		getManagedBean().setDisableSupplierSelection(disable);
	}
	
	/**
	 * To calculated the total estimated cost by item
	 * 
	 * @return totalAmount
	 */
	public BigDecimal getCalculatedTotalAmount(BigDecimal bidItemPrice) {
		ScBidItem bidItem = this.getManagedBean().getSelectedBiddingItem();
		BigDecimal totalAmount = TemplateSpecHelper.getInstance().calcTotalAmount(
												bidItem.getScRequestItem().getTplSpecQuestion(),
												bidItemPrice, bidItem.getQty());
		return totalAmount;
	}

	/**
	 * To retrieve the list of Committee for Endorsement (online endorsement)
	 */
	public void retrieveOnlineEndorsement() {
		this.getManagedBean().setOnlineBiddingActiveIndex(
				ONLINE_BIDDING_ACTIVE_INDEX_ONLINE_ENDORSEMENT);

		//#38540 - Start
		List<Long> statuses = new ArrayList<>();
		statuses.add(QTStatusConst.QT_SUPPLIER_FINALIZATION_REVISION);
		statuses.add(QTStatusConst.SF_PENDING_ENDORSEMENT);
		statuses.add(QTStatusConst.SF_PENDING_SIGN_OFF);

		List<Long> signOffStatus = new ArrayList<>();
		signOffStatus.add(QTStatusConst.SF_PENDING_ENDORSEMENT);
		signOffStatus.add(QTStatusConst.SF_PENDING_SIGN_OFF);
		//#38540 - End
		ScQtCommittee scQtCommittee = getManagedBean().getScQtCommittee();
		List<ScQtCommitteeActivity> committeeActivities = scQtCommittee
				.getCommitteeActivities();
		List<ScCommitteeMember> scCommitteeMembers = scQtCommittee.getScCommittee().getScCommitteeMembers();
		if (CollectionUtils.isEmpty(committeeActivities)
				&& CollectionUtils.isNotEmpty(scCommitteeMembers)) {
			// Sort the members
			Collections.sort(scCommitteeMembers, new MemberRoleComparator());
			committeeActivities = new ArrayList<ScQtCommitteeActivity>();
			for (ScCommitteeMember scCommitteeMember : scCommitteeMembers) {
				ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
				activity.setScQtCommittee(this.getManagedBean()
						.getScQtCommittee());
				activity.setScCommitteeMember(scCommitteeMember);
				setDefaultValueForCommitteeActivityIfNull(activity);
				committeeActivities.add(activity);
			}
			scQtCommittee.setCommitteeActivities(committeeActivities);
		}
		
		if (CollectionUtils.isNotEmpty(committeeActivities)) {
			this.getManagedBean().setCommitteeList(committeeActivities);
			// 25037 - start
			Map<Object, Object> map = CustomUtils.listToMap(this.getManagedBean().getCommitteeList(),
					"getScCommitteeMember.getCommitteeMemberId");
			List<ScQtCommitteeActivity> missingActivities = new ArrayList<>();
			for (ScCommitteeMember member : scQtCommittee.getScCommittee().getScCommitteeMembers()) {
				if (map.get(member.getCommitteeMemberId()) == null) {
					ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
					activity.setScQtCommittee(scQtCommittee);
					activity.setScCommitteeMember(member);
					//#21692 - Start
					boolean isSecretary = activity.getScCommitteeMember().getMemberRole().getParameterCode()
							.equals(ParameterConst.COMMITTEE_ROLE_SECRETARY);
					activity.setSignedOff(!isSecretary ? activity.isSignedOff() : signOffStatus.contains(this
							.getManagedBean().getCurrentStatusId()));
					activity.setAttended(!isSecretary ? activity.isAttended() : statuses.contains(this
							.getManagedBean().getCurrentStatusId()));
					//#21692 - End
					setUserDetails(activity);
					missingActivities.add(activity);
				}
			}
			this.getManagedBean().getCommitteeList().addAll(missingActivities);
			List<ScQtCommitteeActivity> committeeEndorsementList = new ArrayList<>();
			// 25037 - end
			for (ScQtCommitteeActivity commActivity 
					: scQtCommittee.getCommitteeActivities()) {
				if (commActivity.isAttended()) {
					committeeEndorsementList.add(commActivity);
				}
			}
			getManagedBean().setCommitteeEndorsementList(
					committeeEndorsementList);
		}
		
		initializeCommitteeRemarks();
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
			// 25037 - start
			if (CollectionUtils.isNotEmpty(remarks)) {
				for (ScQtCommitteRemarks r : remarks) {
					r.setEditable(false);
				}
				remarks.get(0).setEditable(true);
				Collections.sort(remarks, new Comparator<ScQtCommitteRemarks>() {
					@Override
					public int compare(ScQtCommitteRemarks o1, ScQtCommitteRemarks o2) {
						if (o1.getCreatedDate() == null || o2.getCreatedDate() == null) {
							return 0;
						}
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
	
	//25638 - Start.
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
	// #25638 - End.
	
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
	
	/**
	 * Navigate to committee details
	 */
	public void navigateToMeetingDtls() {
		this.getManagedBean().setMeetingActiveIndex(
				BIDDING_ACTIVE_INDEX_MEETING);
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
	 * Event to update the status of the selected meeting
	 * 
	 * @param scQtMeeting
	 *            the selected ScQtMeeting entity
	 */
	public void changeMeetingStatus(ScQtMeeting scQtMeeting) {
		scQtMeeting.setMeetingStatus(getPmStatusByStatusId(scQtMeeting.getMeetingStatusStr()));
	}	
	
	/**
	 * Event to handle addition of bidding finalization committee meeting(s)
	 */
	public void addMeetingDtls() {
		debugLogger("..In addMeetingDtls()..");
		try {
			List<ScQtMeeting> meetingList = getManagedBean().getMeetingList();
			if (null == meetingList) {
				meetingList = new ArrayList<ScQtMeeting>();
				this.getManagedBean().setMeetingList(meetingList);				
			}

			ScQtMeeting meeting = new ScQtMeeting();
			// meeting.setPmStatus(new PmStatus());
			meeting.setQtCommittee(this.getManagedBean().getScQtCommittee());
			setUserDetails(meeting);
			meeting.setMeetingStatus(getPmStatusByStatusId(QTStatusConst.MS_PENDING));

			meetingList.add(meeting);
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
			ScQtCommittee scQtCommittee = getManagedBean().getScQtCommittee();
			List<ScQtMeeting> meetingList = getManagedBean().getMeetingList();
			List<ScQtMeeting> deletedMeetingList = getManagedBean().getDeletedMeetingList();
			if (deletedMeetingList == null){
				getManagedBean().setDeletedMeetingList(new ArrayList<ScQtMeeting>());
			}
			
			List<ScQtMeeting> tempDeletedList = new ArrayList<ScQtMeeting>();
			for (ScQtMeeting meeting : meetingList){
				if (this.getManagedBean().isSelectAll() || 
						meeting.isSelected()) {
					tempDeletedList.add(meeting);
					
					if (!meeting.isBrandNew()) {
						meeting.setDeleted(true);
						scQtCommittee.getScQtMeetings().remove(meeting);
						getManagedBean().getDeletedMeetingList().add(meeting);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(tempDeletedList)) {
				meetingList.removeAll(tempDeletedList);
				getManagedBean().setMeetingList(meetingList);
			}			
		} finally {
		}		
	}	
	
	/**
	 * Navigate to committee details
	 */
	public void navigateToCommitteeDtls() {
		this.getManagedBean().setMeetingActiveIndex(
				BIDDING_ACTIVE_INDEX_COMMITTEE);
	}
	
	/**
	 * Navigate to Meeting Attendance details
	 */
	public void navigateToMeetingAttendanceDtls() {
		this.getManagedBean().setMeetingActiveIndex(
				BIDDING_ACTIVE_INDEX_JOIN_IN);
	}

	/**
	 * Event to handle addition of committee meeting attendance records
	 */
	public void addMeetingAttendance() {
		debugLogger("..In addMeetingAttendance()..");
		List<ScQtMeetingAttendance> meetingAttendanceList = getManagedBean()
				.getMeetingAttendanceList();
		if (null == meetingAttendanceList) {
			meetingAttendanceList = new ArrayList<ScQtMeetingAttendance>();
			this.getManagedBean().setMeetingAttendanceList(
									 meetingAttendanceList);
		}

		ScQtMeetingAttendance meeting = new ScQtMeetingAttendance();
		meeting.setQtCommittee(this.getManagedBean().getScQtCommittee());
		setUserDetails(meeting);
		meetingAttendanceList.add(meeting);
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
	 * Event to handle deletion of the selected list of committee meeting
	 * attendance
	 * 
	 * @param selectedMeetingAttendanceList
	 *            the selected list of ScQtMeetingAttendance entities
	 */
	public void deleteMeetingAttendanceDetails() {
		try {
			List<ScQtMeetingAttendance> selectedMeetingAttdList 
					 =new ArrayList<ScQtMeetingAttendance>();
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
					}
					if (meetingAttendance.isSelected() && !meetingAttendance.isBrandNew()) {
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
	 * To show item price in a popup
	 * 
	 * @param packageItem
	 * 				the packageItem corresponding to which price is shown in popup
	 */
	public void supplierItemPricePopupAction(BidPackageBean packageItem) {
		getManagedBean().setSupplierCode(
				packageItem.getScQtProposal().getSupplierNo());

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingReqDTO reqDTO = new BiddingReqDTO();
			reqDTO.setSupplierId(packageItem.getScBidSupplier().getSmSupplier()
					.getSupplierId());
			reqDTO.setBidScheduleId(getManagedBean().getScheduledBid()
					.getBidScheduleId());
			BiddingResDTO resDTO = biddingSO
					.retrieveSupplierBidPriceList(reqDTO);
			if (resDTO.isSuccessful()) {
				getManagedBean().setScBidPriceList(resDTO.getBidPrices());
			}
		} finally {
			biddingSO = null;
		}
	}
	
	/**
	 * Set the selected supplier details in managed bean associated with the current user session
	 * 
	 * @param scQtRecommend  
	 * 				the scQtRecommend which holds the item
	 * @param selectedRecommendDtl  
	 * 				the selectedRecommendDtl while holds the recommended supplier
	 */
	public void showSupplierDetails(Long supplierId) {
		debugLogger("showSuppDetails");
		
		resetCompanyInfo();
		
		if (supplierId != null) {
			SmSupplier smSupplier = new SmSupplier();
			smSupplier.setSupplierId(supplierId);
			QTSupplierResDTO resDTO = this.retrieveSupplierDetails(smSupplier);
			smSupplier = resDTO.getSmSupplier();
			getManagedBean().setSmSupplier(smSupplier);
			
			if (CollectionUtils.isNotEmpty(smSupplier.getSmMofAccounts())) {
				getManagedBean().setSmMofAccount(smSupplier.getSmMofAccounts().get(0));
			}
			
			SmCompanyBasic smCompanyBasic = smSupplier.getSmAppl().getSmCompanyBasic();
			getManagedBean().setSmCompanyBasic(smCompanyBasic);
			
			List<SmPersonnel> smPersonnelList = smSupplier.getSmAppl().getSmPersonnels();
			if (CollectionUtils.isNotEmpty(smPersonnelList)) {
				for (SmPersonnel smPersonnel : smPersonnelList) {
					if (smPersonnel.isContactPerson()) {
						getManagedBean().setSmPersonnel(smPersonnel);
						break;
					}
				}
			}
			if (CollectionUtils.isNotEmpty(
							smCompanyBasic.getSmCompanyAddresses())) {
				for (SmCompanyAddress smCompanyAddress 
							: smCompanyBasic.getSmCompanyAddresses()) {
					if (AddressType.CONTACT_ADDRESS.value().equals(smCompanyAddress.getAddressType())) {
						getManagedBean().setSupplierBranch(QTConstants.SUPPLIER_INFO_HQ);
						getManagedBean().setSupplierAddress(smCompanyAddress.getSmAddress());
						break;
					}
				}
			}
		}
	}	
	
	private void resetCompanyInfo() {
		this.getManagedBean().setSmSupplier(null);
		this.getManagedBean().setSmMofAccount(null);
		this.getManagedBean().setSmCompanyBasic(null);
		this.getManagedBean().setSmPersonnel(null);
		this.getManagedBean().setSupplierBranch(null);
		this.getManagedBean().setSupplierAddress(null);
	}	
	
	private String getSupplierNameById(Long supplierId) {
		SmSupplier smSupplier = new SmSupplier();
		smSupplier.setSupplierId(supplierId);
		
		QTSupplierResDTO resDTO = retrieveSupplierDetails(smSupplier);
		if (resDTO.isSuccessful()) {
			return resDTO.getSmSupplier().getCompanyName();
		}
		
		return StringUtils.EMPTY;
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
			resDTO.setSuccessful(true);
		} finally {
			qtSupplierSO = null;
		}	
		return resDTO;
	}
	
	/**
	 * Method to call Supplier Details popup in supplier finalization
	 */
	public void showReminder() {
		debugLogger("showReminder");

		this.getManagedBean().setReminderList(
				new ArrayList<ScQtCommitteeActivity>());

		List<ScQtCommitteeActivity> chairpersonList = new ArrayList<ScQtCommitteeActivity>();
		List<ScQtCommitteeActivity> memberList = new ArrayList<ScQtCommitteeActivity>();

		for (ScQtCommitteeActivity committee : getManagedBean()
				.getCommitteeEndorsementList()) {
			String memberRole = committee.getScCommitteeMember()
					.getMemberRole().getParameterCode();

			if (memberRole.equals(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
				chairpersonList.add(committee);
			} else {
				if (!committee.isBiddingSignedOff()) {
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

	/**
	 * To send reminder to committee members
	 */
	public void sendReminder() {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setUserId(getUserIdSessionFromLoginId().toString()); //#31849 add log key
			reqDTO.setCommitteMembers(getManagedBean()
					.getCommitteeEndorsementList());
			reqDTO.setSubject(getManagedBean().getSubject());
			reqDTO.setMessage(getManagedBean().getMessage());

			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.sendNotifications(reqDTO);
			if (resDTO.isSuccessful()) {
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

//	/**
//	 * For committee members to endorse the Supplier Finalization or chairperson
//	 * sign off the Supplier Finalization
//	 */
//	public void signOff() {
//		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
//		try {
//			qtSuppFinalizationSO = BusinessDelegate.getInstance()
//					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
//							IQTSuppFinalizationSO.class);
//
//			ScQtCommitteeActivity committeeAct = null;
//			boolean isChairperson = false;
//
//			retrieveOnlineEndorsement();
//			for (ScQtCommitteeActivity scQtCommitteeActivity 
//							: getManagedBean().getCommitteeEndorsementList()) {
//				if (scQtCommitteeActivity.getScCommitteeMember().getPmUser()
//						.equals(getFacesScopes().getUserSessionBean())) {
//					if (scQtCommitteeActivity.getScCommitteeMember()
//							.getMemberRole().getParameterCode().equals(
//									SourcingConstants.MEMBER_ROLE_CHAIRPERSON)) {
//						isChairperson = true;
//					}
//
//					scQtCommitteeActivity.setBiddingSignedOff(true);
//					committeeAct = scQtCommitteeActivity;
//					break;
//				}
//			}
//
//			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
//			reqDTO.setScQtCommitteeActivity(committeeAct);
//
//			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
//					.signOff(reqDTO);
//			if (resDTO.isSuccessful()) {
//
//				String variable1 = getParameterDesc(
//						ParameterTypeConst.DOCUMENT_TYPES,
//						ParameterConst.DOCTYPE_QUOTATION);
//				String variable2 = getManagedBean().getScQt().getQtNo();
//				if (isChairperson) {
//					commonMessageAcknowledmentPage(MessageConst.INFO_700049,
//							variable1, variable2);
//				} else {
//					commonMessageAcknowledmentPage(MessageConst.INFO_700048,
//							variable1, variable2);
//				}
//			}
//		} finally {
//			qtSuppFinalizationSO = null;
//		}
//	}
	
	/**
	 * For committee members to endorse the Supplier Finalization or chairperson
	 * sign off the Supplier Finalization
	 */
	private void signOff() {
		if (StringUtils.isNotBlank(
				getManagedBean().getCommitteeType())) {
			for (ScQtCommitteeActivity scQtCommitteeActivity 
								: getManagedBean().getCommitteeEndorsementList()) {
				if (scQtCommitteeActivity.getScCommitteeMember().getPmUser().getUserId()
						.equals(getFacesScopes().getUserSessionBean().getUserId())) {
					scQtCommitteeActivity.setBiddingSignedOff(true);
					break;
				}
			}
		}
	}	

	/**
	 * To retrieve the offline Endorsement (escalation)
	 */
	public void retrieveOfflineEndorsement() {
		this.getManagedBean().setOnlineBiddingActiveIndex(
				ONLINE_BIDDING_ACTIVE_INDEX_OFFLINE_ENDORSEMENT);

		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		
		List<ScQtFinalizationEndorsementRemarks> remarks = initializeRemarks(
				scQtFinalization, 
				scQtFinalization.getCurrentStatus(), 
				scQtFinalization.getScQtFinalizationEndorsementRemarks(), 
				ScQtFinalizationEndorsementRemarks.class);
		
		scQtFinalization.setScQtFinalizationEndorsementRemarks(remarks);
		getManagedBean().setRemarks(remarks);
	}

	/**
	 * To handle file upload event
	 * 
	 * @param event
	 *            the file upload event object
	 * @throws FileNotFoundException
	 *             the exception that would be thrown if the file being updated does
	 *             not exist
	 * @throws IOException
	 *             the exception that would be thrown if an I/O error is encountered
	 *             while uploading the file
	 */
	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		if (event.getFile() != null) {
			ScQtFinalization scQtFinalization = this.getManagedBean().getScQtFinalization();
			ScQtFinalizationEndorsementAttachment attachment = new ScQtFinalizationEndorsementAttachment();
			
			List<ScQtFinalizationEndorsementAttachment> attachments = scQtFinalization
					.getScQtFinalizationEndorsementAttachments();
			if (null == attachments) {
				attachments = new ArrayList<ScQtFinalizationEndorsementAttachment>();
				scQtFinalization.setScQtFinalizationEndorsementAttachments(attachments);
			}
			attachment.setScQtFinalization(scQtFinalization);
			attachment.setDocNo(getManagedBean().getScQt().getQtNo());
			attachments.add(attachment);
			uploadAttachment(event.getFile(), attachment);
		}
	}

	/**
	 * determineIcon for displaying image icon in pid
	 */
	public String determineIcon(ScQtApprovalAttachment attachment) {
		String imageFileName = "icon_file_default.png";
		String ext = "";
		if (attachment != null) {
			String fileName = attachment.getFileName();
			if (fileName != null) {
				int mid = fileName.lastIndexOf(".");
				ext = fileName.substring(mid + 1, fileName.length());
			}
			if ("doc".equals(ext) || "docx".equals(ext)) {
				imageFileName = "icon_doc.png";
			} else if ("pdf".equals(ext)) {
				imageFileName = "icon_pdf.png";
			} else if ("xls".equals(ext) || "xlsx".equals(ext)
					|| "csv".equals(ext)) {
				imageFileName = "icon_exel.png";
			} else if ("jpg".equals(ext) || "bmp".equals(ext)
					|| "png".equals(ext) || "gif".equals(ext)) {
				imageFileName = "icon_image.png";
			} else if ("txt".equals(ext)) {
				imageFileName = "icon_txt.png";
			}
		}
		return imageFileName;
	}
	
	/**
	 * To save online endorsement
	 */
	// 25037 - start
	public void saveOnlineEndorsement() {
		saveOnlineEndorsement(false);
	}

	/**
	 * To save online endorsement, skip validation if required
	 * 
	 * @param skipValidation
	 */
	public void saveOnlineEndorsement(boolean skipValidation) {
		// 25037 - end
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			String taskId = this.getManagedBean().getTaskId();
			String[] outComes = this.getManagedBean().getOutComes();
			String processName = this.getManagedBean().getProcessName();
			ScQt scQt = getManagedBean().getScQt();
			String committeeType = getManagedBean().getCommitteeType();
			Long currentStatusId = this.getManagedBean().getCurrentStatusId();
			int onlineBiddingActiveIndex = this.getManagedBean().getOnlineBiddingActiveIndex();
			int meetingActiveIndex = this.getManagedBean().getMeetingActiveIndex();
			SCEvaluationDataType dataType = (SCEvaluationDataType) this.getManagedBean().getPayLoad();
			
			ScQtFinalization scQtFinalization = prepareFinalizationInfo();
			
			// #25638 - Start.
			if (CollectionUtils.isNotEmpty(getManagedBean().getFinalizationRemarks()) 
					&& getManagedBean().getFinalizationRemarks().size() > 0) {
				List<ScQtFinalizationRemarks> finalizationRemarks = 
						finalizeRemarks(getManagedBean().getFinalizationRemarks());
				this.getManagedBean().getScQtFinalization().setScQtFinalizationRemarks(finalizationRemarks);
			} 
			// #25638 - End.
			
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setScQtFinalization(scQtFinalization);
			
			ScQtCommittee scQtCommittee = prepareEndorsementCommitteeInfo();
			reqDTO.setScQtCommittee(scQtCommittee);
			reqDTO.setDeletedMeetingsList(
					getManagedBean().getDeletedMeetingList());
			reqDTO.setDeletedMeetingAttendanceList(
					getManagedBean().getDeletedMeetingAttendanceList());
			
			List<ScQtEvaluationResultDtl> shortlistedSuppliers = prepareShortlistedSuppliers();
			reqDTO.setShortlistedSuppliersForBidding(shortlistedSuppliers);
			
			qtSuppFinalizationSO.saveEndorsementCommitteeDetails(reqDTO);

//			renewSesionObject();
//			setTaskDetails(taskId, dataType, outComes, processName, currentStatusId, committeeType, 
//						   onlineBiddingActiveIndex, meetingActiveIndex);
//
//			Long qtId = scQt.getQtId();
//			retrieveOnlineBiddingEndorsement(qtId);
			renewSessionAndRetrieve();			
			// 25037 - start
			prepareUpdateCommitteeMember();
			if (!skipValidation) {
				String variable1 = getParameterDesc(
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION)
						+ " " + scQt.getQtNo();
				String variable2 = getMessageCommon("lbl.sc.online.bidding.endorsement");
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(MessageConst.INFO_700004,
										new String[] { variable1, variable2 },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
			// 25037 - end
		} finally {
			qtSuppFinalizationSO = null;
		}		
	}

	/**
	 * To save offline endorsement
	 */
	public void saveOfflineEndorsement() {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			ScQtFinalization scQtFinalization = prepareFinalizationInfo();

			String qtNo = getManagedBean().getScQt().getQtNo();
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setScQtFinalization(scQtFinalization);
			
			List<ScQtEvaluationResultDtl> shortlistedSuppliers = prepareShortlistedSuppliers();
			reqDTO.setShortlistedSuppliersForBidding(shortlistedSuppliers);
			
			qtSuppFinalizationSO.saveFinalizationOfflineEndorsement(reqDTO);

			renewSessionAndRetrieve();

			String variable1 = getParameterDesc(
					ParameterTypeConst.DOCUMENT_TYPES,
					ParameterConst.DOCTYPE_QUOTATION)
					+ " " + qtNo;
			String variable2 = getMessageCommon("lbl.sc.online.bidding.endorsement");
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_700004,
									new String[] { variable1, variable2 },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	private ScQtFinalization prepareFinalizationInfo() {
		List<ScQtMeeting> meetingList=this.getManagedBean().getMeetingList();
		if(CollectionUtils.isNotEmpty(meetingList)){
			List<ScQtMeeting> localMeetingList = new ArrayList<ScQtMeeting>();
			
			for (ScQtMeeting meeting : meetingList) {
				if(null!=meeting.getMeetingDate()){
				compareMeetingDates(meeting, localMeetingList);
				}else{
					throw new EpException(MessageConst.ERROR_760418);
				}
			}
		}
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		
		finalizeRemarks(scQtFinalization.getScQtFinalizationEndorsementRemarks());		
		
		setUserDetails(scQtFinalization);

		return scQtFinalization;
	}
	
	private List<ScQtEvaluationResultDtl> prepareShortlistedSuppliers() {
		boolean isZonal = getManagedBean().isZonal();
		boolean isItemized = getManagedBean().isItemized();
		List<ScQtEvaluationResultDtl> shortlistedSuppliers 
							= new ArrayList<ScQtEvaluationResultDtl>();
		if (isItemized) {
			if (isZonal) {
				if (MapUtils.isNotEmpty(
						getManagedBean().getItemShortlistedSuppliersByZone())) {
					Collection<Map<Long,List<ScQtEvaluationResultDtl>>> itemsShortlistedSuppliersByZone 
									= getManagedBean().getItemShortlistedSuppliersByZone().values();
					for (Map<Long,List<ScQtEvaluationResultDtl>> itemShortlistedSuppliers
								: itemsShortlistedSuppliersByZone) {
						if (MapUtils.isNotEmpty(itemShortlistedSuppliers)) {
							List<ScQtEvaluationResultDtl> tempShortlistedSuppliers 
									= processAndGetShortlistedSuppliers(itemShortlistedSuppliers);
							if (CollectionUtils.isNotEmpty(
									tempShortlistedSuppliers)) {
								shortlistedSuppliers.addAll(tempShortlistedSuppliers);
							}
						}
					}
				}
			} else {
				Map<Long,List<ScQtEvaluationResultDtl>> itemShortlistedSuppliers 
									= getManagedBean().getItemShortlistedSuppliers();
				if (MapUtils.isNotEmpty(itemShortlistedSuppliers)) {
					List<ScQtEvaluationResultDtl> tempShortlistedSuppliers 
							= processAndGetShortlistedSuppliers(itemShortlistedSuppliers);	
					if (CollectionUtils.isNotEmpty(
							tempShortlistedSuppliers)) {
						shortlistedSuppliers.addAll(tempShortlistedSuppliers);
					}					
				}
			}
		} else {
			if (isZonal) {
				Map<Long,List<ScQtEvaluationResultDtl>> pkgShortlistedSuppliers 
								= getManagedBean().getPkgShortlistedSuppliersByZone();
				if (MapUtils.isNotEmpty(pkgShortlistedSuppliers)) {
					List<ScQtEvaluationResultDtl> tempShortlistedSuppliers 
							= processAndGetShortlistedSuppliers(pkgShortlistedSuppliers);	
					if (CollectionUtils.isNotEmpty(
							tempShortlistedSuppliers)) {
						shortlistedSuppliers.addAll(tempShortlistedSuppliers);
					}					
				}				
			} else {
				List<ScQtEvaluationResultDtl> pkgShortlistedSuppliers 
								= getManagedBean().getShortlistedSuppliers();
				if (CollectionUtils.isNotEmpty(
						pkgShortlistedSuppliers)) {
					shortlistedSuppliers.addAll(pkgShortlistedSuppliers);
				}				
			}
		}
		
		return shortlistedSuppliers;
	}
	
	private List<ScQtEvaluationResultDtl> processAndGetShortlistedSuppliers(
										Map<Long,List<ScQtEvaluationResultDtl>> shortlistedSuppliers) {
		List<ScQtEvaluationResultDtl> _shortlistedSuppliers = new ArrayList<ScQtEvaluationResultDtl>();
		for (Long key : shortlistedSuppliers.keySet()) {
			if (CollectionUtils.isNotEmpty(
					shortlistedSuppliers.get(key))) {
				_shortlistedSuppliers.addAll(
						shortlistedSuppliers.get(key));
			}
		}
		
		return _shortlistedSuppliers;
	}
	
	private ScQtCommittee prepareEndorsementCommitteeInfo() {
		ScQtCommittee scQtCommittee = this.getManagedBean().getScQtCommittee();
		
		// Meeting Details
		List<ScQtMeeting> meetingList = getManagedBean().getMeetingList();
		if (CollectionUtils.isNotEmpty(meetingList)) {
			for (ScQtMeeting scQtMeeting : meetingList) {
				if (!scQtCommittee.getScQtMeetings()
						.contains(scQtMeeting)) {
					scQtCommittee.getScQtMeetings()
					 .add(scQtMeeting);
				}
			}
		}
		
		List<ScQtCommitteRemarks> remarksList = finalizeRemarks(this
				.getManagedBean().getCommitteeRemarkList());
		if (CollectionUtils.isNotEmpty(remarksList)) {
			scQtCommittee.setQtCommitteRemarks(remarksList);
		}
		
		// Meeting Attendance Details
		List<ScQtMeetingAttendance> meetingAttendanceList 
					= getManagedBean().getMeetingAttendanceList();
		if (CollectionUtils.isNotEmpty(meetingAttendanceList)) {
			if (meetingAttendanceList.size() > 1) {
				List<String> userIdentificationNos = new ArrayList<String>();
				for (ScQtMeetingAttendance meetingAttendance 
								: meetingAttendanceList) {
					if (userIdentificationNos.contains(
							meetingAttendance.getIcPassport())) {
						this.getManagedBean().setOnlineBiddingActiveIndex(0);
						this.getManagedBean().setMeetingActiveIndex(2);
						throw new EpException(MessageConst.ERROR_760320);
					}
					userIdentificationNos.add(meetingAttendance.getIcPassport());
				}
			}
			scQtCommittee.setMeetingAttendances(meetingAttendanceList);
		}	
		
		return scQtCommittee;
	}
	
	// #25638 - Start.
	/**
	 *  To Revise.
	 */
	public void reviseAction() {
	    reviseSignOff();
		updateBPMTask(BpmOutcomeConst.BPM_REVISE, null);
	}
	
	private void reviseSignOff() {
	  if (StringUtils.isNotBlank(getManagedBean().getCommitteeType())) {
        for (ScQtCommitteeActivity scQtCommitteeActivity 
                            : getManagedBean().getCommitteeEndorsementList()) {
          scQtCommitteeActivity.setBiddingSignedOff(false);
        }
	  }
    }
	// #25638 - End.
	
	/**
	 * To submit bidding endorsement
	 * 
	 * @param outcome
	 *  	 	the outcome
	 */
	public void submitBiddingEndorsement(String outcome) {
		debugLogger("submitBiddingEndorsement");
		
		//25638 - Start.
		if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)) {
			validateRemarks();
			RequestContext.getCurrentInstance().execute("reviseBiddingDlgs.show()");
			return;
		}
		//25638 - End.
		//#21692 - Start
		if (outcome.equals(BpmOutcomeConst.BPM_SIGNOFF)) {
			checkSupplierAwardedRejectValidityExtension();
		}
		//#21692 - End
		
		List<ScQtMeeting> meetingList=this.getManagedBean().getMeetingList();
		if(CollectionUtils.isNotEmpty(meetingList)){
			List<ScQtMeeting> localMeetingList = new ArrayList<ScQtMeeting>();
			
			for (ScQtMeeting meeting : meetingList) {
				if(null!=meeting.getMeetingDate()){
				compareMeetingDates(meeting, localMeetingList);
				}else{
					throw new EpException(MessageConst.ERROR_760418);
				}
			}
		}
		
		//25638 - Start.
		List<ScQtFinalizationRemarks> finalizationRemarks = 
				finalizeRemarks(getManagedBean().getFinalizationRemarks());
		getManagedBean().getScQtFinalization().setScQtFinalizationRemarks(finalizationRemarks);
		//25638 - End.
		
		validateBiddingSubmission();
		
		this.getManagedBean().setAction(outcome);
		String committeeType = getManagedBean().getCommitteeType();
		CommitteResDTO committeeResDTO = getCommitteMemberByUserId();
		ScCommitteeMember committeeMember = committeeResDTO.getCommMember();
		if (StringUtils.isNotBlank(committeeType) && 
				ParameterConst.ONLINE_BIDDING_MEETING.equals(getManagedBean().getScQtFinalization().getOnlineBiddingEndorse()) &&
				ParameterConst.COMMITTEE_ROLE_CHAIRPERSON.equalsIgnoreCase(committeeMember.getMemberRole().getParameterCode()) &&
				isDigiSigningRequired(committeeResDTO.getRoleCode(), 
											getManagedBean().getCommitteeEndorsementList())) {
			this.getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
		} else {
			if (getManagedBean().getScQtCommittee() != null &&
					ParameterConst.ONLINE_BIDDING_MEETING.equals(
							getManagedBean().getScQtFinalization().getOnlineBiddingEndorse())) {			
				signOff();
			}
			updateBPMTask(outcome, null);
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

	private void updateBPMTask(String outCome, PmDigiSign digiSign){
		debugLogger("updateBPMTask");
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		SuppFinalizationReqDTO suppFinalizationReqDTO = new SuppFinalizationReqDTO();
		suppFinalizationReqDTO.setSubmit(true);
		//#21692 - Start
		if (outCome.equals(BpmOutcomeConst.BPM_ENDORSE)) {
			checkSupplierAwardedRejectValidityExtension();
		}
		//#21692 - End
		
		SuppFinalizationResDTO suppFinalizationResDTO = null;
		suppFinalizationReqDTO.setOutcome(outCome);
		suppFinalizationReqDTO.setRoleCode(getUserRoleCode());
		FacesScopes facesScope = new FacesScopes();
		// #25638 - Start 
		if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outCome)
		    || BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outCome)
		    || BpmOutcomeConst.BPM_ENDORSE.equalsIgnoreCase(outCome)) {
		    String memberRole = null;
		    boolean isAllMembersEndorsed = true;
		    
			if (getManagedBean().getCommitteeList() != null) { // #27502, Adding checking only
				for (ScQtCommitteeActivity committeeActivity : getManagedBean().getCommitteeList()) {
					if (null != committeeActivity && null != committeeActivity.getScCommitteeMember()
							&& null != committeeActivity.getScCommitteeMember().getMemberRole()) {

						if (ParameterConst.COMMITTEE_ROLE_SECRETARY.equals(committeeActivity.getScCommitteeMember()
								.getMemberRole().getParameterCode())) {
							suppFinalizationReqDTO.setPmUser(committeeActivity.getScCommitteeMember().getPmUser());
						}

						if (committeeActivity.getScCommitteeMember().getPmUser().getUserId().longValue() == new FacesScopes()
								.getUserSessionBean().getUserId().longValue()) {
							if (ParameterConst.COMMITTEE_ROLE_MEMBER.equals(committeeActivity.getScCommitteeMember()
									.getMemberRole().getParameterCode())) {
								memberRole = RoleCode.QCA_MEMBER.value();
							} else if (ParameterConst.COMMITTEE_ROLE_CHAIRPERSON.equals(committeeActivity
									.getScCommitteeMember().getMemberRole().getParameterCode())) {
								memberRole = RoleCode.QCA_CHAIRPERSON.value();
							} else if (ParameterConst.COMMITTEE_ROLE_SECRETARY.equals(committeeActivity
									.getScCommitteeMember().getMemberRole().getParameterCode())) {
								memberRole = RoleCode.QCA_SECRETARY.value();
							}
						}

						if (isAllMembersEndorsed
								&& ParameterConst.COMMITTEE_ROLE_MEMBER.equals(committeeActivity.getScCommitteeMember()
										.getMemberRole().getParameterCode())) {
							if (!committeeActivity.isBiddingSignedOff()) {
								isAllMembersEndorsed = false;
							}
						}
					}
				}
			}
			
			if (isAllMembersEndorsed || mofFinal) { // #27502
			  suppFinalizationReqDTO.setNextStatusId(QTStatusConst.SF_PENDING_SIGN_OFF);
			} 
            suppFinalizationReqDTO.setScQt(getManagedBean().getScQt());
            suppFinalizationReqDTO.setScQtCommittee(getManagedBean().getScQtCommittee());
            suppFinalizationReqDTO.setChildDocId(getManagedBean().getScQt().getQtId());
            suppFinalizationReqDTO.setChildDocNo(getManagedBean().getScQt().getQtNo());
            suppFinalizationReqDTO.setChildDocType(ParameterConst.DOCTYPE_QUOTATION);
            suppFinalizationReqDTO.setRoleCode(memberRole);
            suppFinalizationReqDTO.setPmUser(new FacesScopes().getUserSessionBean());
            // #25638 - Start.
            suppFinalizationReqDTO.setCurrentStatusId(getManagedBean().getCurrentStatusId());
            // #25638 - End.
		} else {
		    suppFinalizationReqDTO.setCurrentStatusId(getManagedBean().getCurrentStatusId());
			suppFinalizationReqDTO.setPmUser(facesScope.getUserSessionBean());
			if (QTStatusConst.SF_PENDING_ENDORSEMENT.equals(getManagedBean().getCurrentStatusId())) {
			  suppFinalizationReqDTO.setRoleCode(RoleCode.QCA_MEMBER.value());
			}
		}
		// #25638 - End 
		suppFinalizationReqDTO.setUserLocale(facesScope.getUserLocale());
		suppFinalizationReqDTO.setTaskId(this.getManagedBean().getTaskId());
		suppFinalizationReqDTO.setToken(facesScope.getBPMAuthenticationCtxToken());
		
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			ScQt scQt = getManagedBean().getScQt();

			ScQtFinalization scQtFinalization = prepareFinalizationInfo();
			List<ScQtEvaluationResultDtl> shortlistedSuppliers = prepareShortlistedSuppliers();
			suppFinalizationReqDTO.setShortlistedSuppliersForBidding(shortlistedSuppliers);

			suppFinalizationReqDTO.setScQtFinalization(scQtFinalization);
			suppFinalizationReqDTO.setRecommendDtlIds(
					getManagedBean().getAwardedRecommendDtlIds());
			suppFinalizationReqDTO.setPayLoad(
					this.getManagedBean().getPayLoad());
			suppFinalizationReqDTO.setDigiSignData(digiSign);
			
			ScQtCommittee scQtCommittee = null;
			if (ParameterConst.ONLINE_BIDDING_MEETING.equals(
					scQtFinalization.getOnlineBiddingEndorse())) {
				suppFinalizationReqDTO.setCommitteeType(
								getManagedBean().getCommitteeType());
				scQtCommittee = prepareEndorsementCommitteeInfo();
				
				suppFinalizationReqDTO.setScQtCommittee(scQtCommittee);
				suppFinalizationReqDTO.setDeletedMeetingsList(
						getManagedBean().getDeletedMeetingList());				
				suppFinalizationReqDTO.setDeletedMeetingAttendanceList(
						getManagedBean().getDeletedMeetingAttendanceList());
				
				EpException epException = null;
				List<ScQtMeeting> meetingList = getManagedBean().getMeetingList();
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
				} else if(mofFinal){// #27502
					epException = null;
				} else {
					epException = new EpException(MessageConst.ERROR_760291);
				}
				
				if (epException != null) {
					saveMeetingDetailsOnException(qtSuppFinalizationSO, suppFinalizationReqDTO);
					getManagedBean().setMeetingActiveIndex(0);
					throw epException;
				}
				
				if (!mofFinal) {// #27502
					try {
						qtSuppFinalizationSO.validatePreparation(suppFinalizationReqDTO);
					} catch (EpException epExcep) {
						saveMeetingDetailsOnException(qtSuppFinalizationSO, suppFinalizationReqDTO);
						getManagedBean().setMeetingActiveIndex(1);
						throw epExcep;
					} catch (Exception e) {
						saveMeetingDetailsOnException(qtSuppFinalizationSO, suppFinalizationReqDTO);
						getManagedBean().setMeetingActiveIndex(1);
						throw e;
					}
				}
			} else {
				scQtCommittee = getManagedBean().getScQtCommittee();
			}
			suppFinalizationReqDTO.setScQtCommittee(scQtCommittee);
			
			if (ParameterConst.ONLINE_BIDDING_ESCALATION.equals(
						scQtFinalization.getOnlineBiddingEndorse())) {
				qtSuppFinalizationSO.validateOfflineEndorsement(suppFinalizationReqDTO);
			}
			
			if (null != digiSign) {
				updatePmDigiSignData(digiSign, ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION, 
						 scQt.getQtId(), scQt.getQtNo());
				
				// Chairperson signing off
				signOff();
			}
			//29561 -starts
			suppFinalizationReqDTO.setBaseURL(SmCommonUtil.getBasePortalUrl());
			suppFinalizationReqDTO.setTaskName(this.getManagedBean().getTaskName());
			//29561 -end
			suppFinalizationResDTO = qtSuppFinalizationSO.updateBPMAction(suppFinalizationReqDTO);
				
			if (suppFinalizationResDTO.isSuccessful()) {
				// #25638 - Start.
				if (outCome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
					commonMessageAcknowledmentPage(MessageConst.INFO_776042,
							getMessageCommon("lbl.sc.quotation.tender"), getManagedBean().getScQt().getQtNo());
				} else {
					String qtParam = getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_QUOTATION)
							+ " " + scQt.getQtNo();
					String statusParam = getStatusDescById(getManagedBean().getCurrentStatusId());
					commonMessageAcknowledmentPage(MessageConst.INFO_700026,
							qtParam, statusParam);
				}
				// #25638 - End.
				
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	private void validateBiddingSubmission() {
		// 26247, 38591 - start
		ScQtFinalization scQtFinalization = getManagedBean().getScQtFinalization();
		ScheduleBiddingReqDTO reqDTO = new ScheduleBiddingReqDTO();
		reqDTO.setQtID(getManagedBean().getScQt().getQtId());
		reqDTO.setBidScheduleStatusId(BidStatusConst.BID_CANCELLED);
		ScheduleBiddingResDTO resDTO = getScheduleBiddingSO().retrieveItemsByBidScheduleStatus(reqDTO);
		Map<Long, List<Long>> cancelledBidScheduleItemsMap = resDTO.getItemsMap();

		Map<Long, ScQtRecommend> recommendMap = new HashMap<>();
		Map<Long, Boolean> identifierMap = new HashMap<>();
		getManagedBean().setAwardedRecommendDtlIds(new ArrayList<String>());
		for (ScQtRecommend recommend : scQtFinalization.getScQtRecommends()) {
			Long recommendId = recommend.getQtRecommendId();
			recommendMap.put(recommendId, recommend);
			TplSpecZoneDtl specZoneDtl = recommend.getTplSpecZoneDtl();

			boolean bidScheduleCancelledItem = specZoneDtl != null
					&& MapUtils.isNotEmpty(cancelledBidScheduleItemsMap)
					&& cancelledBidScheduleItemsMap.containsKey(specZoneDtl.getSpecZoneId())
					&& cancelledBidScheduleItemsMap.get(specZoneDtl.getSpecZoneId()).contains(
							recommend.getRequestItem().getRequestNoteItemId());

			boolean itemValidForSelection = !recommend.isCancelled() && !bidScheduleCancelledItem
					&& CollectionUtils.isNotEmpty(recommend.getRecommendDtls());

			if (!identifierMap.containsKey(recommendId) && itemValidForSelection) {
				identifierMap.put(recommendId, false);
			}
			if (identifierMap.containsKey(recommendId)) {
				for (ScQtRecommendDtl recommendDtl : recommend.getRecommendDtls()) {
					SmSupplier supplier = recommendDtl.getSupplier();
					if (recommendDtl.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)
							&& !this.disabledQTProcess(supplier)) {
						identifierMap.put(recommendId, true);
						getManagedBean().getAwardedRecommendDtlIds().add(recommendDtl.getRecommendDtlId().toString());
					}
				}
			}
		}
		for (Map.Entry<Long, Boolean> entry : identifierMap.entrySet()) {
			if (!entry.getValue()) {
				ScQtRecommend recommend = recommendMap.get(entry.getKey());
				if (getManagedBean().isZonal()) {
					setZoneAndTabIndexOnException(recommend.getTplSpecZoneDtl());
				}
				if (getManagedBean().isItemized()) {
					throw new EpException(MessageConst.ERROR_760377, new String[] { recommend.getRequestItem()
							.getItemName() });
				}
				throw new EpException(MessageConst.ERROR_760376);
			} 
		} 
		// 26247, 38591 - end
	}

	private void setZoneAndTabIndexOnException(TplSpecZoneDtl zoneDtl) {
		navigateToBiddingSummaryTab();
		List<TplSpecZoneDtl> specZoneDtls 
					= this.getManagedBean().getBiddingZoneDetails();
		if (zoneDtl != null && 
				CollectionUtils.isNotEmpty(specZoneDtls)) {
			zoneDtl = getManagedBean().getZoneMap().get(zoneDtl.getSpecZoneId());
			this.getManagedBean().setZoneActiveIndex(
					zoneDtl.getZoneTabIndex());
			onZoneTabChangeAction(getManagedBean()
					.getZoneMap().get(zoneDtl.getSpecZoneId()));
		}
	}
	
	private void saveMeetingDetailsOnException(IQTSuppFinalizationSO qtSuppFinalizationSO, 
									 SuppFinalizationReqDTO suppFinalizationReqDTO) {
		suppFinalizationReqDTO.setSubmit(false);
		qtSuppFinalizationSO.saveEndorsementCommitteeDetails(suppFinalizationReqDTO);
		renewSessionAndRetrieve();
		navigateToBiddingFinalizationMeetingTab();
	}
	
	private void renewSessionAndRetrieve() {
		String taskId = this.getManagedBean().getTaskId();
		String taskName = this.getManagedBean().getTaskName(); // 25037
		String[] outComes = this.getManagedBean().getOutComes();
		String processName = this.getManagedBean().getProcessName();
		ScQt scQt = getManagedBean().getScQt();
		String committeeType = getManagedBean().getCommitteeType();
		Long currentStatusId = this.getManagedBean().getCurrentStatusId();
		int onlineBiddingActiveIndex = this.getManagedBean().getOnlineBiddingActiveIndex();
		int meetingActiveIndex = this.getManagedBean().getMeetingActiveIndex();			
		SCEvaluationDataType dataType = (SCEvaluationDataType) this.getManagedBean().getPayLoad();	
		
		renewSesionObject();

		Long qtId = scQt.getQtId();
		getManagedBean().setCommitteeType(committeeType);
		retrieveOnlineBiddingEndorsement(qtId);	
		
		setTaskDetails(taskId, dataType, outComes, processName, currentStatusId, committeeType,
				onlineBiddingActiveIndex, meetingActiveIndex, taskName); // 25037	
	}

	private void setTaskDetails(String taskId, SCEvaluationDataType dataType, String[] outComes, String processName,
			Long currentStatusId, String committeeType, int activeIndex, int meetingActiveIndex, String taskName) {// 25037
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setOutComes(outComes);
		this.getManagedBean().setProcessName(processName);
		this.getManagedBean().setPayLoad(dataType);
		this.getManagedBean().setCurrentStatusId(currentStatusId);
		this.getManagedBean().setCommitteeType(committeeType);
		this.getManagedBean().setOnlineBiddingActiveIndex(activeIndex);
		this.getManagedBean().setMeetingActiveIndex(meetingActiveIndex);
		this.getManagedBean().setTaskName(taskName);// 25037
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
	 * To digitally sign using GPK device
	 * 
	 * @param digiSign
	 * 			the digital signature
	 */
	public void gpkiSignAction(PmDigiSign digiSign) {
		
		updateBPMTask(this.getManagedBean().getAction(), digiSign);
	}

	/**
	 * Get the Gpki Sign Data.
	 * @return  the textBeforeSign
	 */
	public String getTextBeforeSign() {
		StringBuilder sb = new StringBuilder();
		if(getManagedBean().getScQt() != null){
			sb.append(getManagedBean().getScQt().getQtNo());
		}
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		textBeforeSign = sb.toString();
		return textBeforeSign;
	}

	/**
	 * Set the Gpki Sign Data.
	 * @param textBeforeSign
	 * 				the textBeforeSign to set
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
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
		log.info(msg);
	}

	/**
	 * Get the CurrentTab.
	 * @return the currentTab
	 */
	public int getCurrentTab() {
		return currentTab;
	}

	/**
	 *  Set the CurrentTab.
	 * @param currentTab
	 *            the currentTab to set
	 */
	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	/**
	 *  Get the OnlineBiddingMeeting ActiveIndex.
	 * @return the onlineBiddingFinalizationMeetingActiveIndex
	 */
	public int getOnlineBiddingFinalizationMeetingActiveIndex() {
		if (getManagedBean().isOnlineBiddingEscalation()) {
			return -1;
		} else if(mofFinal){// #27502 - Start
			return -1;
		} else {
			return ONLINE_BIDDING_FINALIZATION_MEETING_ACTIVE_INDEX;
		}
	}

	/**
	 * Get the OnlineBiddingSummary ActiveIndex.
	 * @return the onlineBiddingActiveIndexSummary
	 */
	public int getOnlineBiddingActiveIndexSummary() {
		int activeIndex;
		if(mofFinal){// #27502 - Start
			activeIndex = 1;
		} else
		if (this.getManagedBean().isOnlineBiddingEscalation()) {
			activeIndex = ONLINE_BIDDING_ACTIVE_INDEX_OFFLINE_SUMMARY;
		} else {
			activeIndex = ONLINE_BIDDING_ACTIVE_INDEX_ONLINE_SUMMARY;
		}
		
		return activeIndex;
	}

	/**
	 * Get the OnlineBiddingOnlineEndorsment ActiveIndex.
	 * @return the onlineBiddingActiveIndexOnlineEndorsement
	 */
	public static int getOnlineBiddingActiveIndexOnlineEndorsement() {
		if (mofFinal) {// #27502 - Start
			return -1;
		} else {
			return ONLINE_BIDDING_ACTIVE_INDEX_ONLINE_ENDORSEMENT;
		}
	}

	/**
	 * Get the OnlineBiddingOfflineEndorsment ActiveIndex.
	 * @return the onlineBiddingActiveIndexOfflineEndorsement
	 */
	public static int getOnlineBiddingActiveIndexOfflineEndorsement() {
		if (mofFinal) {// #27502 - Start
			return -1;
		} else {
			return ONLINE_BIDDING_ACTIVE_INDEX_OFFLINE_ENDORSEMENT;
		}
	}
	
	
	/**
	 * this method checking two meeting dates or equal or not.
	 */
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

					} else {
						duplicateDate = false;
					}

				}
				if (duplicateDate == true) {
					throw new EpException(MessageConst.ERROR_760271);
				} else {
					meetingList.add(scqtMeeting);
				}
			} else {
				//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
				if (meetingList == null)
					meetingList = new ArrayList<ScQtMeeting>();
				meetingList.add(scqtMeeting);
			}

		}
		
		/**
		 * The method to used auto update of request.
		 */
		public void enableAutoUpdate() {
			getManagedBean().setAutoUpdate(Boolean.TRUE);		
		}
	
		/**
		 * Get the BriefingPaper Tab Details.and navigate to briefing paper tab.
		 */
	public void navigateToBriefingPaperTab() {
		if (mofFinal) {// #27502 - Start
			this.getManagedBean().setOnlineBiddingActiveIndex(0);
		} else {
			this.getManagedBean().setOnlineBiddingActiveIndex(
					this.getManagedBean().isOnlineBiddingEscalation() ? ONLINE_BIDDING_ACTIVE_INDEX_ESCLATION_BRIEFING
							: ONLINE_BIDDING_ACTIVE_INDEX_ONLINE_BRIEFING);
		}
		
		retrieveBriefingPaper();
	}
	
	/**
	 * Get the OnlineBidding BriefingPaper ActiveIndex.
	 * @return the onlineBiddingFinalizationMeetingActiveIndex
	 */
	public int getOnlineBiddinBriefingPaperActiveIndex() {
		if (mofFinal) {// #27502 - Start
			return 0;
		} else 
			if (getManagedBean().isOnlineBiddingEscalation()) {
			return ONLINE_BIDDING_ACTIVE_INDEX_ESCLATION_BRIEFING;
		} else {
			return ONLINE_BIDDING_ACTIVE_INDEX_ONLINE_BRIEFING;
		}
	}
	
	/**
	 * Get the  BriefingPaper tab details.
	 * 
	 */
	public void retrieveBriefingPaper() {
		
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
			//#29702					
			List<ScQtFinalizationReport> inputReport = new ArrayList<ScQtFinalizationReport>();
					new ArrayList<ScQtFinalizationReport>();
			for (ScQtFinalizationReport report : finalizationReports) {
				String docCategory = report.getDocCategory();
			   //#29702
				String docDefinitionType = report.getDefinationType().getParameterCode();
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
			//#29702 - start
				 else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_USER_INPUT
							.equals(docDefinitionType)) {
					 inputReport.add(report);
			 }
			//#29702 - end	
			}
			if (coverReport != null && 
					getManagedBean().isBpkUser()) {
				finalizationReports.remove(coverReport);
			}
			scQtFinalization.setScQtFinalizationReports(
									finalizationReports);
			
			if (!getManagedBean().isBpkUser()) {
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
			//#29702 - start
			if (inputReport != null ) {
				finalizationReports.removeAll(inputReport);
			}
			//#29702 - end
			if (finalizationReports != null) {
				getManagedBean().setDocumentList(finalizationReports);
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
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
	 * To download a report under Briefing Paper/Kertas Taklimat tab
	 * 
	 * @param scQtFinalizationReport
	 *            selected report which would be downloaded
	 * @throws Exception
	 *             the exception that would be thrown in case of any error while
	 *             downloading the report
	 */
	public void downloadReport(ScQtFinalizationReport scQtFinalizationReport) throws Exception {
		debugLogger("downloadReport");

		// #30567 - Start.
		ScQtFinalizationReportAttachment reportAttachment = scQtFinalizationReport
				.getScQtFinalizationReportAttachment();
		if (null == reportAttachment
				&& scQtFinalizationReport.getTitle().contains(QTConstants.QT_REPORT_BRIEFING_PAPER)) {
			reportAttachment = generateBriefingPaperAttachment(scQtFinalizationReport);
			scQtFinalizationReport.setScQtFinalizationReportAttachment(reportAttachment);
		}
		// #30567 - End.

		downloadFile(reportAttachment);
	}
	
	// 30567 - start
	private ScQtFinalizationReportAttachment generateBriefingPaperAttachment(
			ScQtFinalizationReport scQtFinalizationReport) throws Exception {
		SupplierFinalizationBackingBean supplierFinalizationBackingBean = new SupplierFinalizationBackingBean();
		supplierFinalizationBackingBean.renewSesionObject();
		supplierFinalizationBackingBean.getManagedBean().setCurrentStatusId(this.getManagedBean().getCurrentStatusId());
		supplierFinalizationBackingBean.getManagedBean().setScQtFinalization(
				this.getManagedBean().getScQtFinalization());
		supplierFinalizationBackingBean.getManagedBean().setScQt(this.getManagedBean().getScQt());
		return supplierFinalizationBackingBean.downloadBriefingPaperReportForBidding(scQtFinalizationReport);
	}

	// 30567 - end
	
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
		// 37623 - start
		List<ScQtFinalizationReportAttachment> scAttachments = new ArrayList<>();
		for (ScQtFinalizationReport report : scQtFinalizationReports) {
			ScQtFinalizationReportAttachment attachment = report.getScQtFinalizationReportAttachment();
			if (attachment == null && report.getTitle().contains(QTConstants.QT_REPORT_BRIEFING_PAPER)) {
				attachment = generateBriefingPaperAttachment(report);
			}
			if (attachment != null) {
				attachment.setTempFileName(report.getTitle());
			}
			scAttachments.add(attachment);
		}
		// 37623 - end
		downloadAllFiles("NGeP-Finalization-Report", scAttachments);

	}
	
	// 25037 - start
	/**
	 * Populate and initialize data for update committee screen.
	 */
	public void prepareUpdateCommitteeMember() {
		if (StringUtils.isBlank(this.getManagedBean().getCommitteeType()))
			return;
		SupplierFinalizationMeetingBackingBean meetingBB = new SupplierFinalizationMeetingBackingBean();
		meetingBB.renewSesionObject();
		this.getManagedBean().setMeetingBB(meetingBB);
		Long qtId = this.getManagedBean().getScQt().getQtId();
		meetingBB.getManagedBean().setCommitteeType(this.getManagedBean().getCommitteeType());
		meetingBB.retriveQTDetails(qtId);
		meetingBB.retriveMeetingDetails(this.getManagedBean().getScQt().getQtId());
		meetingBB.prepareMemberUpdate();
		meetingBB.getManagedBean().setTaskName(this.getManagedBean().getTaskName());
		meetingBB.getManagedBean().setFinalBiddingBB(this);
	}

	/**
	 * Submit action.
	 * 
	 * @param action
	 */
	public void submitAction(String action) {
		this.checkIfLoggedinUserIsSecretary();
		if (this.getManagedBean().isSecretaryUser()
				&& this.getManagedBean().getTaskName().equalsIgnoreCase(SCProcessNameConst.FINALISE_FC_SELECTION)) {
			validateBiddingSubmission();// 26247, 38591
			RequestContext.getCurrentInstance().execute("submissionPopup.show()");
		} else {
			this.submitBiddingEndorsement(action);
		}
	}

	// 25037 - end
	
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

		if (CollectionUtils.isEmpty(supplierBranchListDeleted)) {
			getManagedBean().setSupplierBranchList(listSupplierBranchList(supplierId));
		} else {
			supplierBranchListDeleted.addAll(listSupplierBranchList(supplierId));
			getManagedBean().setSupplierBranchList(supplierBranchListDeleted);
		}
	}

	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to display list of supplier branch
	 * 
	 * @param supplierId
	 * @return List<SmSupplierBranch>
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
		List<TplSpecZoneDtl> zoneDetails = this.getManagedBean().getBiddingZoneDetails();
		int zoneActiveIndex = this.getManagedBean().getZoneActiveIndex();
		boolean isZonal = this.getManagedBean().getScQt().isZonal();

		if (isZonal) {
			if ((CollectionUtils.isNotEmpty(zoneDetails) && zoneActiveIndex != -1 && zoneDetails.get(zoneActiveIndex)
					.isBranchDeactivated()) || checkBiddingSupplierDeactivated(qtId)) {
				return true;
			}
		} else {
			if (sourcingIntegrationSO.checkListDisqualifiedSupplierBranchDeleted(qtId)
					|| checkBiddingSupplierDeactivated(qtId)) {
				return true;
			}
		}

		return false;
	}

	// #28777 & #29729 - End

	// #28777 & #29729 - Start
	/**
	 * Method to check supplier deactivated
	 * 
	 * @param qtId
	 * @return true or false
	 **/
	public Boolean checkBiddingSupplierDeactivated(Long qtId) {
		ISourcingIntegrationSO sourcingIntegrationSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);

		ScBidSchedule scBidSchedule;
		Long bidScheduleId = null;
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getBiddingItemList())) {
			scBidSchedule = this.getManagedBean().getBiddingItemList().get(0).getScBidSchedule();
			bidScheduleId = scBidSchedule.getBidScheduleId();
		}

		if (this.getManagedBean().getScheduledBid() != null) {
			scBidSchedule = this.getManagedBean().getScheduledBid();
			bidScheduleId = scBidSchedule.getBidScheduleId();
		}

		if (bidScheduleId != null && sourcingIntegrationSO.flagBiddingScBidSupplierDeactivated(qtId, bidScheduleId)) {
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
		List<TplSpecZoneDtl> zoneDetails = this.getManagedBean().getBiddingZoneDetails();
		int zoneActiveIndex = this.getManagedBean().getZoneActiveIndex();
		boolean isZonal = this.getManagedBean().getScQt().isZonal();

		if (isZonal) {
			if ((CollectionUtils.isNotEmpty(zoneDetails) && zoneActiveIndex != -1 && zoneDetails.get(zoneActiveIndex)
					.isBranchDeactivated()) || checkBiddingSupplierDeactivated(qtId)) {
				return true;
			}
		} else {
			if (sourcingIntegrationSO.checkListDisqualifiedSupplierBranchDeleted(qtId)
					|| checkBiddingSupplierDeactivated(qtId)) {
				return true;
			}
		}

		return false;
	}

	// #28777 & #29729 - End
	
	//#21692 - Start
	public boolean supplierRejectEPV(ScQtProposal proposal) {
		return proposal.getIsAgreeQtExtendPeriod() == null ? false : proposal
				.getIsAgreeQtExtendPeriod().equals(SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_REJECTED) ||
				proposal.getIsAgreeQtExtendPeriod().equals(SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_NOT_QUALIFIED);
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
				if (!endorseOrSignOff && recommendDtl.getScQtProposalAmount().getScQtProposal().getIsAgreeQtExtendPeriod() != null
						&& (recommendDtl.getScQtProposalAmount().getScQtProposal().getIsAgreeQtExtendPeriod()
								.equals(SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_REJECTED) ||
								recommendDtl.getScQtProposalAmount().getScQtProposal().getIsAgreeQtExtendPeriod()
						.equals(SourcingConstants.EXTEND_PROPOSAL_VALIDITY_PERIOD_NOT_QUALIFIED))) {
					recommendDtl.setRecordStatusFlg(DbValueConst.RECORD_STATUS_REJECTED);
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
	//#21692 - End
	
}

