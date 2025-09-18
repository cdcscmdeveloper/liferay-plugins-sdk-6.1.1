/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.supplierfinalization;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.IAuditTrailSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTAuditTrailSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTAuditConstant;
import my.ep.constant.sc.QTSearchBeanConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.AuditTrailReqDTO;
import my.ep.dto.sc.quationtender.AuditTrailResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.helper.Paginator;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRole;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMemberHistory;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.CustomUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.UserCommitteeSearchBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.constant.sc.SLALogConstant;
import my.ep.web.managedbean.sc.supplierfinalization.SupplierFinalizationMeetingManagedBean;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;

/**
 * Implementation for scheduling meeting for supplier finalization
 * 
 * @author Faradiana
 */

@ManagedBean(name = "suppFinalMeetingBackingBean")
@RequestScoped
public class SupplierFinalizationMeetingBackingBean extends 
						SourcingBaseBackingBean<SupplierFinalizationMeetingManagedBean> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Logger log = EpLogger.getLogger(SupplierFinalizationMeetingBackingBean.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
	
	/**
	 * Default constructor
	 */
	public SupplierFinalizationMeetingBackingBean() {		
		if (this.getManagedBean().getCloneQtnTenderSearchBean() == null) {
			this.getManagedBean().setCloneQtnTenderSearchBean(new QtnTenderSearchBean());
		}
		if (this.getManagedBean().getCommitteMember() == null) {
			this.getManagedBean().setCommitteMember(new ScCommitteeMember());
		}
	}
	
	/**
	 * Retrieve the data to be displayed before the page is rendered
	 */
	public void preRenderView() {
		debugLogger("preRenderView");
		
		FacesContext fc = FacesContext.getCurrentInstance();

		try {
			SCEvaluationDataType dataType = getBpmTaskDetails(SCEvaluationDataType.class, null);
			Long qtId = Long.valueOf(dataType.getQtId());
			getCommitteeSO().initializedFCLegacyCommitteeHistory(qtId); // 25037
			String committeeType = String.valueOf(dataType.getCommitteeType());
			if (StringUtils.isBlank(committeeType) && 
					StringUtils.isNotBlank(dataType.getHigherCommitteeType())) {
				committeeType = dataType.getHigherCommitteeType();
				dataType.setCommitteeType(committeeType);
			}
			getManagedBean().setCommitteeType(committeeType);
			getManagedBean().setPayLoad(dataType);
			
			retriveQTDetails(qtId);
			retriveMeetingDetails(qtId);
			prepareMemberUpdate(); // 25037

			fc.getApplication().getNavigationHandler()
			  .handleNavigation(fc, null, 
					QTConstants.FC_COORDINATE_MEETING_PAGE);
		} catch (Exception e) {
			handleException(e);
			fc.getApplication().getNavigationHandler()
			  .handleNavigation(fc, null,
					CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);
		}
	}
	
	/**
	 * Enhancement #21694
	 * VIEW Committee
	 * @param obj
	 * 		instance of ScQtCommittee
	 * @param previousPageUrl
	 * @return
	 */
	public void viewMeetingDetails(Object obj) {		
		debugLogger("viewMeetingDetails");
		String previousPageURL = QTConstants.FC_COORDINATE_MEETING_PAGE;
		FacesContext fc = FacesContext.getCurrentInstance();
		
		ScQtMeeting scQtMeeting = null;
		if(obj instanceof ScQtMeeting ){
			scQtMeeting = (ScQtMeeting)obj;
			preRenderViewForQtMeeting(scQtMeeting,previousPageURL);
			
		}		
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, QTConstants.VIEW_QT_COMMITTEE_PAGE);
	}
	
	/**
	 * Enhancement #21694
	 * VIEW Committee
	 * @param obj
	 * 		instance of ScQtCommittee
	 * @param previousPageUrl
	 * @return
	 */
	public void viewMeetingDetailsViewFinalization(Object obj) {		
		debugLogger("viewMeetingDetailsFinalization");
		String previousPageURL = QTConstants.FC_SUPPLIER_FINALIZATION_PAGE;
		FacesContext fc = FacesContext.getCurrentInstance();
		
		ScQtMeeting scQtMeeting = null;
		if(obj instanceof ScQtMeeting ){
			scQtMeeting = (ScQtMeeting)obj;
			preRenderViewForQtMeeting(scQtMeeting,previousPageURL);
			
		}		
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, QTConstants.VIEW_QT_COMMITTEE_PAGE);
	}
	
	/**
	 * Enhancement #21694 START 
	 * Page navigation from  to qtDetailManageBean.backToPreviousPage
	 */
	public void backToPreviousReverse() {
		debugLogger("backToPreviousReverse");
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						getManagedBean().getBackToPreviousPage());
	}
	
	/**
	 * Enhancement #21694
	 * @param ScQtMeeting
	 */
	public void preRenderViewForQtMeeting(ScQtMeeting scQtMeeting, String prevPageURL){
		IQTMeetingSO qtMeetingSO;
		getManagedBean().setBackToPreviousPage(prevPageURL);
		getManagedBean().setPreviousCommitteeMembers(null);
		try{
			qtMeetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			QuationTenderResDTO resDTO = new QuationTenderResDTO();
			reqDTO.setMeetingId(scQtMeeting.getQtMeetingId());
			if(null!=scQtMeeting.getQtMeetingId() && scQtMeeting.getQtMeetingId()>0L){
				if(null!=reqDTO)
					resDTO = qtMeetingSO.findPreviousCommitteeMembersByMeetingId(reqDTO); 
					if(null!=resDTO && null!=resDTO.getPreviousCommitteeMembers() && !resDTO.getPreviousCommitteeMembers().isEmpty()){
						getManagedBean().setPreviousCommitteeMembers(resDTO.getPreviousCommitteeMembers());
					}
			}
		}finally{
			qtMeetingSO = null;
		}
		
	}
	
	

	/**
	 * Retrieve quotation/tender details corresponding to the qtId
	 * @param qtId  
	 */
	public void retriveQTDetails(Long qtId) {
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
//End Header - QT Details
	
	
	
	
	/**
	 * Retrieve quotation/tender meeting details corresponding to the qtId
	 * @param qtId
	 */
	public void retriveMeetingDetails(Long qtId) {
		IQTMeetingSO qtMeetingSO;
		IParameterSO parameterSO;
		try {
			qtMeetingSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					            ModuleCodeConst.SUPPLIER_SELECTION, IParameterSO.class);
			
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qtId);
			String committeeType = getManagedBean().getCommitteeType();
			reqDTO.setCommitteType(committeeType);
			reqDTO.setDocType(ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION);
			MeetingResDTO resDTO = qtMeetingSO.retrieveMeetingDls(reqDTO);

			if (resDTO.getMeetingDtls() != null) {
				for (ScQtMeeting meeting : resDTO.getMeetingDtls()) {
					if (meeting.getRecordStatusFlg().equals(
							DbValueConst.RECORD_STATUS_SUSPENDED)) {
						getManagedBean().setHasPreviousMeeting(true);
					}
				}
				getManagedBean().setScQtMeetings(resDTO.getMeetingDtls());
			}
			
			ScCommittee scCommittee = resDTO.getQtCommittee().getScCommittee();
			if(scCommittee != null){
				List<ScCommitteeMember> membersList = 
							scCommittee.getScCommitteeMembers();
				// Start #26189:PK2.1 
				if (committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) || 
						committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
					for (ScCommitteeMember member : membersList) {
						if(!ParameterConst.COMMITTEE_ROLE_CHAIRPERSON
								.equals(member.getMemberRole().getParameterCode())){
							if(member.getRepresentativeType() == null && !ParameterConst.COMMITTEE_ROLE_SECRETARY
									.equals(member.getMemberRole().getParameterCode())){
								ParameterReqDTO roleReqDTO = new ParameterReqDTO();
								roleReqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
								roleReqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
								ParameterResDTO roleResDTO = parameterSO.retrieveParameterByTypeAndCode(roleReqDTO);
								member.setMemberRole(roleResDTO.getPmParameter());
								long memberRoleId = 378;
								member.setMemberRoleId(memberRoleId);
							}
						}
					}
				}
				// End #26189:PK2.1
				if (CollectionUtils.isNotEmpty(membersList)) {
					//#31870 - START
					Boolean checkIsExistFCSecretary = Boolean.FALSE;
					for (ScCommitteeMember members : membersList) {
						if(ParameterConst.COMMITTEE_ROLE_SECRETARY.equals(members.getMemberRole().getParameterCode())){
							checkIsExistFCSecretary = Boolean.TRUE;
							break;
						}
					}
					
					FacesScopes facesScopes = new FacesScopes();
					Long loggedInUserId = facesScopes.getUserSessionBean().getUserId();
					if(!checkIsExistFCSecretary){
						log.info("Has missing FC Secretary Data == ");
						log.info("loggedInUserId == "+loggedInUserId);
						ScCommitteeMember member = addFCSecretary(loggedInUserId,scCommittee, ParameterConst.COMMITTEE_ROLE_SECRETARY);
						if(member != null){
							try{
								getCommitteeSO().saveCommitteeMember(member);
								membersList.add(member);//#31870#21
							}catch(Exception e){
								log.info("Exception during save == "+e.getMessage());
							}
						}
						//#31870#21
						retriveMeetingDetails(qtId);
						return;
					}
					//#31870 - END
					Collections.sort(membersList);
					
					getManagedBean().setCommitteeMembersList(
										membersList);
					getManagedBean().setOriginalCommitteeMembersList(
										new ArrayList<ScCommitteeMember>());
					getManagedBean().getOriginalCommitteeMembersList()
									.addAll(membersList);
				}
				/*}else{
					getManagedBean().setCommitteeMembersList(new ArrayList<ScCommitteeMember>());
					
					//auto populate chairperson n secretary
					ScCommitteeMember member = getSecretaryChaipersonList(RoleCode.PBA_CHAIRPERSON.value(), getManagedBean().getScQt(), scCommittee);
					getManagedBean().getCommitteeMembersList().add(member);
					
					ScCommitteeMember member2 = getSecretaryChaipersonList(RoleCode.PBA_SECRETARY.value(), getManagedBean().getScQt(), scCommittee);
					getManagedBean().getCommitteeMembersList().add(member2);
				}*/
			}
			
			ScQtCommittee committee = resDTO.getQtCommittee();
			getManagedBean().setScQtCommittee(committee);
			
			List<ScQtCommitteeActivity> activities = committee.getCommitteeActivities();
			if (CollectionUtils.isNotEmpty(activities)) {
				for (ScQtCommitteeActivity activity : activities) {
					
					// Reset attendance of the committee member
					activity.setAttended(false);
					
					PmParameter memberRole = activity.getScCommitteeMember().getMemberRole();
					if (activity.getScCommitteeMember().getRepresentativeType() == null && 
							ParameterConst.COMMITTEE_ROLE_MEMBER.equals(memberRole.getParameterCode())) {
						activity.getScCommitteeMember().setRepresentativeType(new PmParameter());
						activity.getScCommitteeMember().setRepresentativeTypeId(null);
					}
				}
			} else {
				List<ScCommitteeMember> commMembers = scCommittee != null ? scCommittee.getScCommitteeMembers():null;//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
				if (commMembers != null && CollectionUtils.isNotEmpty(commMembers)) {//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
					for (ScCommitteeMember member : commMembers) {
						PmParameter memberRole = member.getMemberRole();
						if (member.getRepresentativeType() == null && 
								ParameterConst.COMMITTEE_ROLE_MEMBER
									.equals(memberRole.getParameterCode())) {
							member.setRepresentativeType(new PmParameter());
							member.setRepresentativeTypeId(null);
						}
					}
				}
			}
			// 25037 - start - note 284
			ScQt scQt = this.getManagedBean().getScQt();
			this.getManagedBean().getScQtCommittee().setScQt(scQt);
			// 25037 - end - note 284
			List<ScQtCommitteRemarks> remarks = initializeRemarks(
					committee, 
					committee.getCurrentStatus(), 
					committee.getQtCommitteRemarks(), 
					ScQtCommitteRemarks.class);
			
			committee.setQtCommitteRemarks(remarks);
			this.getManagedBean().setQtCommitteeRemarks(remarks);
		} finally {
			qtMeetingSO = null;
		}
	}
	
	/**
	 * Action to add meeting
	 */
	public void meetingAddAction() {
		try {
			List<ScQtMeeting> meetingList = getManagedBean().getScQtMeetings();
			if (null == meetingList) {
				getManagedBean().setScQtMeetings(new ArrayList<ScQtMeeting>());
			}else{//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
			
			ScQtMeeting meeting = new ScQtMeeting();
			meeting.setQtCommittee(this.getManagedBean().getScQtCommittee());
			setUserDetails(meeting);

			meeting.setMeetingStatus(getPmStatusByStatusId(QTStatusConst.MS_PENDING));
			meeting.setRecordStatusFlg(1);
			meetingList.add(meeting);
			this.getManagedBean().setScQtMeetings(meetingList);
			}//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
			
		} finally{
			
		}
	}
	
	/**
	 * Action to delete meeting
	 */
	public void meetingDeleteAction() {
		try {
			
			List<ScQtMeeting> meetingList = getManagedBean().getScQtMeetings();
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
//End - Meeting Details
	
	/**
	 * Event to handle selection of committee members
	 */
	public void selectAllMembers(boolean selectAll) {
		debugLogger("addToSelectedMemberList");
		
		List<ScCommitteeMember> committeeMemberList = 
				this.getManagedBean().getCommitteeMembersList();
		
		if (CollectionUtils.isNotEmpty(committeeMemberList)) {
			selectAll(selectAll, committeeMemberList);
			if (this.getManagedBean()
					.getSelectedCommitteeMembersList() == null) {
				this.getManagedBean().setSelectedCommitteeMembersList(
						new ArrayList<ScCommitteeMember>());
			} 
			if (selectAll) {
				this.getManagedBean()
					.getSelectedCommitteeMembersList().clear();
				this.getManagedBean()
					.getSelectedCommitteeMembersList()
					.addAll(committeeMemberList);
			} else {
				this.getManagedBean()
					.setSelectedCommitteeMembersList(null);
			}
		}
	}
	
	/**
	 * Event to handle selection of committee members
	 */
	public void selectMemberEvent(SelectEvent event) {
		debugLogger("selectMemberEvent");
		
		ScCommitteeMember committeeMember = 
								(ScCommitteeMember) event.getObject();
		if (!ParameterConst.COMMITTEE_ROLE_SECRETARY.equals(
				committeeMember.getMemberRole().getParameterCode())) {
			committeeMember.setSelected(true);
			selectMemberEvent(committeeMember);
		} else {
			return;
		}
	}
	
	/**
	 * check the selected member exist in the current list.if member doesn't exist add and exist remove the member from current list.
	 */
	public void selectMemberEvent(ScCommitteeMember committeeMember) {
		this.getManagedBean().setSelectAllMembers(false);
		List<ScCommitteeMember> selectedCommitteeMembers 
				= this.getManagedBean().getSelectedCommitteeMembersList();
		if (selectedCommitteeMembers == null) {
			selectedCommitteeMembers = new ArrayList<ScCommitteeMember>();
		}
		if (committeeMember.isSelected() && 
				!selectedCommitteeMembers.contains(committeeMember)) {
			selectedCommitteeMembers.add(committeeMember);	
		} else {
			selectedCommitteeMembers.remove(committeeMember);
		}
		this.getManagedBean().setSelectedCommitteeMembersList(
				selectedCommitteeMembers);
	}
	
	/**
	 * Action to add committee members from the popup
	 */
	public void addPopupAction() {
		debugLogger("addCommitteMember");
		// #12471 Start
		IPagingDataModel<PmUser> userSearchQCDataModel = getManagedBean().getUserSearchDataModel();
		UserCommitteeSearchBean userCommitteeSearchBean = getManagedBean().getUserSearchBean();
		if (userSearchQCDataModel == null) {
			this.getManagedBean().setUserSearchDataModel(
					new UserQCPagingDataModel(QTConstants.DATATABLE_EXISTINGMEMBERTBL, userCommitteeSearchBean));
		}
		userCommitteeSearchBean.setOrgCat(ParameterConst.ORG_CATEGORY_PTJ);
		resetUserAction(); 
		// #12471 End
		this.getManagedBean().setNameRequired(false);
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		committeeMember.setUserSelection(SelectionType.Existing.value());
		
		this.getManagedBean().setCommitteMember(committeeMember);
		getSelectionTypes();
	}
	
	private void getSelectionTypes() {
		SelectionType[] type = SelectionType.values();
		List<SelectItem> selectionTypes = new ArrayList<SelectItem>();

		for (SelectionType selectionType : type) {
			selectionTypes.add(new SelectItem(selectionType.value(), selectionType.name()));
		}
		this.getManagedBean().setSelections(selectionTypes);
	}
	
	/**
	 * Populate values into Member Role drop down in Committee Members table
	 * @return  an array of member roles
	 */
	public SelectItem[] getMemberRoleTypeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
		
		searchBean.getDataFilterBeans().add( new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		
		SelectItem[] items = getDropdownCodeValuesBySearchBean(ParameterTypeConst.COMMITTEE_ROLE, searchBean);
		return items;
	}
	
	/**
	 * user change committeeType from existing to new method will render.
	 * 
	 */
	public void userSelectionAction(AjaxBehaviorEvent event) {
		String userSelection = (String) event.getComponent().getAttributes().get("value");
		userSelection = this.getManagedBean().getCommitteMember().getUserSelection();
		this.getManagedBean().setExternalUsers(null);
		
		ScCommitteeMember committeeMember = this.getManagedBean().getCommitteMember();
		if (null == committeeMember.getPmUser()) {
			committeeMember.setPmUser(new PmUser());
		}
		committeeMember.setUserSelection(userSelection);
		
		// this.getManagedBean().setCommitteMember(committeeMember);
		if (SelectionType.Existing.value().equalsIgnoreCase(getManagedBean().getCommitteMember().getUserSelection())) {
			committeeMember.setMemberRole(null);
		} else if (SelectionType.New.value().equalsIgnoreCase(getManagedBean().getCommitteMember().getUserSelection())) {
			committeeMember.setMemberRole(new PmParameter());
		}
		committeeMember.setMemberRoleId(null);
		this.getManagedBean().setCommitteMember(committeeMember);
	}
	
	/**
	 * Search user action.
	 */
	public void searchUserAction() {
		// #12471 Start
		UserCommitteeSearchBean searchBean = this.getManagedBean().getUserSearchBean();
		if (null == searchBean)
			return;
		if (StringUtils.isBlank(searchBean.getName()) && StringUtils.isBlank(searchBean.getIdentificationNo())) {
			throw new EpException(MessageConst.ERROR_775300);
		}
		// #12471 End
		this.getManagedBean().getUserSearchDataModel().reloadData();
	}
	
	/**
	 * reset user action.
	 */
	public void resetUserAction() {
		//12471 - start
		this.resetCommitteeSearchBean(this.getManagedBean().getScQt());
		getManagedBean().setSelectedPmUsers(null);
		getManagedBean().setNameRequired(false);
		getManagedBean().getUserSearchDataModel().resetData();
		//12471 - end
	}	
	
	/*private ScCommitteeMember getSecretaryChaipersonList(String roleCode, ScQt scQt, ScCommittee scCommittee) {
		IUserSO iUserSO = null;
		try {
			iUserSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
			
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setRoleCode(roleCode);
			reqDTO.setOrgProfileId(scQt.getPmOrgProfile().getOrgProfileId());
			
			UserResDTO userResDTO = iUserSO.retrieveUserListByRole(reqDTO);
			PmUser pmUser = userResDTO.getUserList().get(0);
			
			ScCommitteeMember member = new ScCommitteeMember();
			member.setMemberName(pmUser.getUserName());
			member.setIcPassport(pmUser.getIdentificationNo());
			member.setGrade(pmUser.getGrade());
			member.setDesignation(pmUser.getDesignation());
			member.setPNP(pmUser.isPMP());
			member.setEmail(pmUser.getEmail());
			if(roleCode.equals(RoleCode.PBA_CHAIRPERSON.value())){
				roleCode = ParameterConst.COMMITTEE_ROLE_CHAIRPERSON;
			}else{
				roleCode = ParameterConst.COMMITTEE_ROLE_SECRETARY;
			}
			member.setMemberRole(getPmParameterByTypeAndCode(ParameterTypeConst.COMMITTEE_ROLE, roleCode));
			member.setAccessRight("E");
			member.setScCommittee(scCommittee);
			setUserDetails(member);
			member.setPmOrgProfile(scQt.getPmOrgProfile());
			member.setPmUser(pmUser);
			
			return member;
		} finally {
			iUserSO = null;
		}
	}*/
	
	
	/**
	 * Get user role description
	 */
	public String getRoleDesc(PmUser pmUser) {
		String userRole = null;
		String committeeType = getManagedBean().getCommitteeType();
		List<ScCommitteeMember> scCommMember = getManagedBean().getCommitteeMembersList();
		boolean hasChairperson = false;
		
		ICommitteSO committeeSO = null;
		IOrgProfileSO orgProfileSO = null;
		
		try {
			committeeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IOrgProfileSO.class);
			
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setPmUser(pmUser);
			reqDTO.setCommitteeType(committeeType);
			
			PmOrgProfile pmOrgProfile = getManagedBean().getScQt().getPmOrgProfile();
						
			//Bug #25021 Task Reassignment Sabah & Sarawak - Finalization Comm
			log.info(":::getRoleDesc() committeeType"+ committeeType +".");
			
			if (committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) || 
				committeeType.equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) { //FSB/FSW
				OrgProfileReqDTO mofReqDTO = new OrgProfileReqDTO();
				mofReqDTO.setOrgCode("103"); //KEMENTERIAN KEWANGAN MALAYSIA
				
				OrgProfileResDTO mofResDTO = orgProfileSO.retrieveOrgProfileByCode(mofReqDTO);
				pmOrgProfile = mofResDTO.getPmOrgProfile();
				
				log.info(":::getRoleDesc() orgProfileId: "+ pmOrgProfile.getOrgProfileId());
			}
			//end Bug #25021
		
			reqDTO.setPmOrgProfile(pmOrgProfile);
			
			CommitteResDTO resDTO = committeeSO.checkIfUserHasRoleByRoleCodeCommTypeAndOrgProfile(reqDTO);
			
			if (resDTO.isSuccessful() && resDTO.isHasRequiredCommitteeRole()) {
				
				//Bug #24911
				for (ScCommitteeMember commMember : scCommMember) {
					if (commMember.getMemberRole().getParameterCode()
							.equals(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
						hasChairperson = true;
						}
					}
				
				if(hasChairperson){
					PmParameter memberRole = ParameterHelper.getInstance().getParameterByTypeAndCode(
							ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_MEMBER);
					pmUser.setMemberRoleId(memberRole.getParameterId());
					userRole = getParameterDesc(memberRole);
				}else{
					PmParameter chairPersonRole = ParameterHelper.getInstance().getParameterByTypeAndCode(
							ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
					pmUser.setMemberRoleId(chairPersonRole.getParameterId());
					userRole = getParameterDesc(chairPersonRole);
				}
			}
			
		} finally {
			committeeSO = null;
		}
		
		if (userRole == null) {
			userRole = getParameterDesc(ParameterTypeConst.COMMITTEE_ROLE, 
					ParameterConst.COMMITTEE_ROLE_MEMBER);
		}
		
		return userRole;
	}
	
	/**
	 * Action to confirm the addition of selected committee members
	 */
	public void committeeOkAction() {
		List<ScCommitteeMember> committeeMemberList = getManagedBean().getCommitteeMembersList();
		if (null == committeeMemberList) {
			committeeMemberList = new ArrayList<ScCommitteeMember>();
		}
		
		/* Removed the feature to add a new user
		if (SelectionType.New.value().equalsIgnoreCase(getManagedBean().getCommitteMember().getUserSelection())) {
			ScCommitteeMember scCommitteeMember = addNewUser();
			if(scCommitteeMember != null){
				committeeMemberList.add(scCommitteeMember);
			}
		} else {

		}*/
		
		// Check if the selected pmusers already exist in the committee members list
		List<PmUser> selectedPmUserList = getManagedBean().getSelectedPmUsers();
		if (CollectionUtils.isNotEmpty(selectedPmUserList) 
				&& CollectionUtils.isNotEmpty(committeeMemberList)) {
			List<String> duplicateUsersList = new ArrayList<String>();
			for (PmUser user : selectedPmUserList) {
				// #12471 Start
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				reqDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT);
				ScQtApproval approval = this.getQTDetailSO().retrieveApprovalDtlsByQtIdNType(reqDTO).getQtApproval();
				if (approval != null && approval.getApproverSettings() != null) {
					for (ScQtApproverSettings approvalUser : approval.getApproverSettings()) {
						if (approvalUser.getPmUser().getUserId().equals(user.getUserId())) {
							throw new EpException(MessageConst.ERROR_775301,
									new String[] { user.getUserName() });
						}
					}
				}
				// #12471 End
				for (ScCommitteeMember member : committeeMemberList) {
					if (StringUtils.equalsIgnoreCase(
							user.getIdentificationNo(), member.getIcPassport())) {
						duplicateUsersList.add(user.getIdentificationNo());
					}
				}
			}
			
			if (CollectionUtils.isNotEmpty(duplicateUsersList)) {
				StringBuilder identificationNos = new StringBuilder();
				StringBuilder separator = new StringBuilder(", ");
				for (String identificationNo : duplicateUsersList) {
					identificationNos.append(identificationNo).append(separator);
				}
				identificationNos.replace(identificationNos.lastIndexOf(separator.toString()), 
						identificationNos.length(), StringUtils.EMPTY);
				throw new EpException(MessageConst.ERROR_760205, new String[]{identificationNos.toString()});
			}
		}
		
		List<ScCommitteeMember> memberList = addExistingUser();
		if (memberList != null){
			committeeMemberList.addAll(memberList);
			this.getManagedBean().setNewCommitteeAuditTrailList(memberList);//28895-35500
		}
		Collections.sort(committeeMemberList);
		
		this.getManagedBean().setCommitteeMembersList(committeeMemberList);
		
 		// validation
		RequestContext.getCurrentInstance().execute("memberWidgetVar.hide()");
	}
	
	/**
	 * Action to cancel addition of committee members and hide the popup
	 */
	public void committeeCancelAction() {
		resetUserAction();
		RequestContext.getCurrentInstance().execute("memberWidgetVar.hide()");
	}
	
	/**
	 * Action to delete the selected committee members
	 */
	public void committeeDeleteAction() {
		try {
			List<ScCommitteeMember> currentCommitteeMemList 
							= getManagedBean().getCommitteeMembersList();
		
			List<ScCommitteeMember> localList=new ArrayList<ScCommitteeMember>();
			List<ScCommitteeMember> newCommitteeMember = this.getManagedBean().getNewCommitteeAuditTrailList() != null
					? this.getManagedBean().getNewCommitteeAuditTrailList() : new ArrayList<ScCommitteeMember>() ;//28895-35500
			
			if (CollectionUtils.isNotEmpty(currentCommitteeMemList)) {
				
				for(ScCommitteeMember committeemember:currentCommitteeMemList){
					if(committeemember.isSelected()){
						localList.add(committeemember);
						newCommitteeMember.remove(committeemember);//28895-35500
					}				
				}
				if(CollectionUtils.isNotEmpty(localList)){
				
				currentCommitteeMemList.removeAll(localList);
				//28895-35500 Start
				this.getManagedBean().setRemoveCommitteeAuditTrailList(localList);
				this.getManagedBean().setNewCommitteeAuditTrailList(newCommitteeMember);
				//28895-35500 End
				}
			}
				prepareToDeleteCommitteeMembers(localList);
				getManagedBean().setSelectedCommitteeMembersList(null);
			
		} finally {
		}
	}
	
	private void prepareToDeleteCommitteeMembers(
			List<ScCommitteeMember> selectedCommitteeMemList) {
		if (CollectionUtils.isNotEmpty(selectedCommitteeMemList)) {
			if (getManagedBean().getDeletedCommitteeMembers() == null) {
				getManagedBean().setDeletedCommitteeMembers(
									new ArrayList<ScCommitteeMember>());
			}
			for (ScCommitteeMember committeeMember 
								: selectedCommitteeMemList) {
				if (!committeeMember.isBrandNew()) {
					committeeMember.setDeleted(true);
					getManagedBean().getDeletedCommitteeMembers()
											.add(committeeMember);
				}
			}
		}
	}
	
//	private ScCommitteeMember addNewUser() {
//		ScQt scQt = this.getManagedBean().getScQt();
//		
//		ScCommitteeMember member = new ScCommitteeMember();
//		member.setMemberName(getManagedBean().getCommitteMember().getMemberName());
//		member.setIcPassport(getManagedBean().getCommitteMember().getIcPassport());
//		member.setGrade(getManagedBean().getCommitteMember().getGrade());
//		member.setDesignation(getManagedBean().getCommitteMember().getDesignation());
//		member.setPNP(getManagedBean().getCommitteMember().isPNP());
//		member.setEmail(getManagedBean().getCommitteMember().getEmail());
//		member.setMemberRole(getPmParameterByTypeAndCode(ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_MEMBER));
//		member.setAccessRight("E");
//		member.setScCommittee(getManagedBean().getScQtCommittee().getScCommittee());
//		setUserDetails(member);
//		member.setPmOrgProfile(scQt.getPmOrgProfile());
//		PmUser user = userDetails(member);
//		member.setPmUser(user);
//		
//		return member;
//	}
	
	private PmUser userDetails(ScCommitteeMember member){
		PmUser user = new PmUser();
		user.setUserName(member.getMemberName());
		user.setLoginId(member.getMemberName());
		user.setIdentificationNo(member.getIcPassport());
		user.setDesignation(member.getDesignation());
		user.setGrade(member.getGrade());
		user.setPMP(member.isPNP());
		user.setEmail(member.getEmail());
		setUserDetails(user);
		
		// Identification Code
		PmParameter identificationCode = ParameterHelper.getInstance().getParameterByTypeAndCode(
						ParameterTypeConst.IDENTIFICATION_TYPES, ParameterConst.IDENTIFICATION_TYPES_IC);
		user.setIdentificationCode(identificationCode);
		user.setPmAddressTypes(null);
		user.setPmCountry(getDefaultCountry());

		// User Orgs
		List<PmUserOrg> pmUserOrgs = userOrgDetails(user);
		user.setPmUserOrgs(pmUserOrgs);
		user.setOrgTypeCode(pmUserOrgs.get(0).getPmOrgProfile().getOrgTypeCode());

		//Save new Member
		PmUser newUser = saveNewUser(user);
		return newUser;
	}
	
	private PmCountry getDefaultCountry(){
		ICountrySO countrySO = null;
		try {
			countrySO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySO.retrieveCountryByCode(countryReqDTo);

			return countryResDTO.getPmCountry();
		} finally {
			countrySO = null;
		}
	}
	
	private List<PmUserOrg> userOrgDetails(PmUser user){
		PmUserOrg userOrg = new PmUserOrg();
		userOrg.setPmUser(user);
		
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		orgProfileReqDTO.setOrgType(ParameterConst.ORG_TYPE_MINISTRY);
		
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IOrgProfileSO.class);
		PmOrgProfile pmOrgProfile = orgProfileSO.retrieveOrgProfileListByOrgType(orgProfileReqDTO).getOrgProfileList().iterator().next();
		
		userOrg.setPmOrgProfile(pmOrgProfile);
		userOrg.setEffDate(new Timestamp(System.currentTimeMillis()));
		Timestamp expDate = new Timestamp(System.currentTimeMillis());
		expDate.setYear(1000);
		userOrg.setExpDate(expDate);
		
		List<PmUserRole> pmUserRoles = userRolesDetails(userOrg);
		userOrg.setPmUserRoles(pmUserRoles);
		
		List<PmUserOrg> pmUserOrgs = new ArrayList<PmUserOrg>();
		pmUserOrgs.add(userOrg);
		
		return pmUserOrgs;
	}
	
	private List<PmUserRole> userRolesDetails(PmUserOrg userOrg){
		List<PmUserRole> pmUserRoles = new ArrayList<PmUserRole>();
		PmUserRole pmUserRole = new PmUserRole();
		pmUserRole.setPmUserOrg(userOrg);
		PmRole pmRole = new PmRole();
		pmRole.setRoleCode(RoleCode.PLAN_OFFICER.value());
		pmUserRole.setPmRole(pmRole);
		pmUserRoles.add(pmUserRole);
		
		return pmUserRoles;
	}
	
	private PmUser saveNewUser(PmUser user) {
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setOrgType(OrganizationType.OTHERS.value());
		userReqDTO.setPmUser(user);
		
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserResDTO resDTO;
		try {
			resDTO = userSO.saveUser(userReqDTO);
			return resDTO.getPmUser();
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		return null;
	}
	
	/**
	 * The Class UserQCPagingDataModel.
	 */
	private class UserQCPagingDataModel extends PagingDataModel<PmUser> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The user qc search bean. */
		private UserCommitteeSearchBean userSearchBean;

		/**
		 * Instantiates a new user qc paging data model.
		 *
		 * @param componentId the component id
		 * @param userQCSearchBean the user qc search bean
		 */
		public UserQCPagingDataModel(String componentId,
				UserCommitteeSearchBean userQCSearchBean) {
			super(componentId,false);
			this.userSearchBean = userQCSearchBean;
			getManagedBean().setCommitteeSearchBean(userQCSearchBean);//12471
		}

		/* (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		protected ISearchResultBean<PmUser> pageChangeListener(
				ISearchBean<PmUser> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();
				
				//12471 - start
				if (null != userSearchBean) {
					populateUserSearchCriteria(userSearchBean, searchBean);
					populateOrganizationSearchCriteria(userSearchBean, searchBean);
				}
				//12471 - end
				committeReqDTO.setUserSearchBean(searchBean);
				committeReqDTO.setPtjMinistryAdmin(Boolean.TRUE);
				
				//Enhancement #21689
				committeReqDTO.setScQt(getManagedBean().getScQt());
				
				return committeSO.retrieveUserListByCommType(committeReqDTO)
						.getUserResultBean();
			} catch (Exception e) {
				ScQt scQt = getManagedBean().getScQt();
				String qtNo = null != scQt ? scQt.getQtNo() : "";
				SLAUtils.getInstance().logSLAError(
						SLALogConstant.QT_MODULE,
						SLALogConstant.QT_SUPPLIER_FINALIZATION_MEETING_BACKINGBEAN, qtNo,
						SLALogConstant.QT_USER_QC_PAGING_DATA_MODEL.concat(SLALogConstant.QT_SLA_DOT_SEPARATOR)
								.concat(SLALogConstant.QT_PAGE_CHANGE_LISTENER), getUserIdSessionFromLoginId(), e);
			}
			return null;
		}

		// 12471 - start
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
		// 12471 - end
	}
	
	private List<ScCommitteeMember> addExistingUser() {
		List<ScCommitteeMember> memberList = new ArrayList<ScCommitteeMember>();
		ScQt scQt = this.getManagedBean().getScQt();
		
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
			PmParameter userMemberRole = ParameterHelper.getInstance().getPmParameterById(
					user.getMemberRoleId());
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
//End - Committee Details
	
	/**
	 * Action to clone committee members from other QT
	 */
	public void clonePopupAction() {
		QtnTenderSearchBean cloneQtnTenderSearchBean = new QtnTenderSearchBean();
		IPagingDataModel<ScQt> cloneSearchQTDataModel = new cloneSearchQTDataModel("cloneCommitteeSearchTbl", cloneQtnTenderSearchBean);
		cloneSearchQTDataModel.resetData();
		getManagedBean().setCloneSearchQTDataModel(cloneSearchQTDataModel);
		getManagedBean().setCloneQtnTenderSearchBean(cloneQtnTenderSearchBean);
		this.getManagedBean().setCloneCommitteMembersSelect(null);
	}
	
	/**
	 * Action to search for QT based on the user input values to clone committee members from a selected QT
	 */
	public void cloneSearchAction() {
		getManagedBean().getCloneSearchQTDataModel().reloadData();
	}
	
	/**
	 * Action to reset clone search field values
	 */
	public void cloneResetAction() {
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtNo(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setProcurementMode(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setFromCreatedDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setToCreatedDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setFromPublishDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setToPublishDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setFromClosingDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setToClosingDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtStatus(null);
	}
	
	/**
	 * Event to clone committee members of the selected QT
	 * @param event  the row select event to set
	 */
	public void onRowCloneAction(SelectEvent event) {
		ScQt scQt = (ScQt) event.getObject();

		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setQtId(scQt.getQtId());
		reqDTO.setCommitteeType(this.getManagedBean().getCommitteeType());
		CommitteResDTO resDTO = null;
		
		try {
			ICommitteSO committeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			resDTO = committeSO
					.retrieveCommitteMembersByQtIdAndCommType(reqDTO);
			this.getManagedBean().setCloneCommitteMembers(
					new ArrayList<ScCommitteeMember>());
			if (null != resDTO
					&& null != resDTO.getCommMemberResultBean()
					&& null != resDTO.getCommMemberResultBean().getResultList()
					&& !resDTO.getCommMemberResultBean().getResultList()
							.isEmpty()) {

				this.getManagedBean().setCloneCommitteMembers(
						resDTO.getCommMemberResultBean().getResultList());
			}

		} finally {

		}
	
	}
	
	/**
	 * Action to clone committee members of the selected quotation/tender
	 */
	public void cloneAction() {
		List<ScCommitteeMember> committeeMemberList = this.getManagedBean().getCommitteeMembersList();
		if (committeeMemberList == null) {
			committeeMemberList = new ArrayList<ScCommitteeMember>();
		}
		
		List<ScCommitteeMember> clonedCommitteeMembersList = 
					this.getManagedBean().getCloneCommitteMembersSelect();
		if (clonedCommitteeMembersList == null) {
			clonedCommitteeMembersList = new ArrayList<ScCommitteeMember>();
		}

		// Check if the members being cloned already exist
		if (CollectionUtils.isNotEmpty(committeeMemberList) 
				&& CollectionUtils.isNotEmpty(clonedCommitteeMembersList)) {
			List<String> duplicateUsersList = new ArrayList<String>();
			for (ScCommitteeMember committeeMember : committeeMemberList) {
				for (ScCommitteeMember cloneCommitteeMember : clonedCommitteeMembersList) {
					if (StringUtils.equalsIgnoreCase(committeeMember.getIcPassport(), cloneCommitteeMember.getIcPassport())) {
						duplicateUsersList.add(cloneCommitteeMember.getIcPassport());
					}
				}
			}
			
			if (CollectionUtils.isNotEmpty(duplicateUsersList)) {
				StringBuilder identificationNos = new StringBuilder();
				StringBuilder separator = new StringBuilder(", ");
				for (String identificationNo : duplicateUsersList) {
					identificationNos.append(identificationNo).append(separator);
				}
				identificationNos.replace(identificationNos.lastIndexOf(separator.toString()), 
						identificationNos.length(), StringUtils.EMPTY);
				throw new EpException(MessageConst.ERROR_760205, new String[]{identificationNos.toString()});
			}
		}
		
		List<ScCommitteeMember> localList = null;
		if (CollectionUtils.isNotEmpty(
				clonedCommitteeMembersList)) {
			localList = addClonedCommitteeMembers();
			committeeMemberList.addAll(localList);
		}
		Collections.sort(committeeMemberList);
		
		this.getManagedBean().setCommitteeMembersList(committeeMemberList);
		
		// Hide the clone dialog, if there are no errors with the cloning process
		RequestContext.getCurrentInstance().execute("cloneMemberWidgetVar.hide()");
	}
	
	private List<ScCommitteeMember> addClonedCommitteeMembers() {
		List<ScCommitteeMember> memberList = new ArrayList<ScCommitteeMember>();
		ScQt scQt = this.getManagedBean().getScQt();
		
		for (ScCommitteeMember commMember : 
					getManagedBean().getCloneCommitteMembersSelect()) {
			ScCommitteeMember committeeMember = new ScCommitteeMember();
			committeeMember.setMemberName(commMember.getMemberName());
			committeeMember.setIcPassport(commMember.getIcPassport());
			committeeMember.setDesignation(commMember.getDesignation());
			committeeMember.setEmail(commMember.getEmail());
			committeeMember.setGrade(commMember.getGrade());
			committeeMember.setPNP(commMember.isPNP());
			committeeMember.setPmUser(commMember.getPmUser());
			setCommitteeMemberOrgProfile(committeeMember);
			if (commMember.getMemberRole() == null || 
					ParameterConst.COMMITTEE_ROLE_SECRETARY
						.equals(commMember.getMemberRole().getParameterCode())) {
				PmParameter memberRole = ParameterHelper.getInstance().getParameterByTypeAndCode(
							ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_MEMBER);
				commMember.setMemberRole(memberRole);
				committeeMember.setRepresentativeType(new PmParameter());
			}
			committeeMember.setMemberRole(commMember.getMemberRole());
			
			if (committeeMember.getMemberRole() != null) {
				committeeMember.setMemberRoleId(
						committeeMember.getMemberRole().getParameterId());	
			}
			committeeMember.setAccessRight(SourcingConstants.ACCESS_RIGHT_EDITABLE);
			committeeMember.setScCommittee(
					getManagedBean().getScQtCommittee().getScCommittee());
			setUserDetails(committeeMember);
			
			memberList.add(committeeMember);
		}
		return memberList;
	}	
	
	private class cloneSearchQTDataModel extends PagingDataModel<ScQt> {
		private static final long serialVersionUID = 1L;

		private QtnTenderSearchBean qtnTenderSearchBean;
		public cloneSearchQTDataModel(String componentId, QtnTenderSearchBean qtnTenderSearchBean) {
			super(componentId);
			this.qtnTenderSearchBean = qtnTenderSearchBean;
		}

		/**
		 * retrieve list of ScQt by params
		 * 
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQt> pageChangeListener(ISearchBean<ScQt> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				populateQTSearchCriteria(qtnTenderSearchBean, searchBean);
				reqDTO.setQtSearchBean(searchBean);
				QuationTenderResDTO resDTO = detailSO.retriveQuationTenderDetailsBySearchCriteria(reqDTO);
				return resDTO.getQtResultBean();
			} finally {
				detailSO = null;
			}
		}
	}
	
	private void populateQTSearchCriteria(QtnTenderSearchBean qtnTenderSearchBean, ISearchBean<ScQt> qtSearchBean) {
		if (qtnTenderSearchBean != null) {
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String qtTitle = qtnTenderSearchBean.getQtTitle();
			if (!StringUtils.isBlank(qtTitle)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_TITILE);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtTitle);
				searchFilterBean.add(bean);
			}
			
			String qtNumber = qtnTenderSearchBean.getQtNo();
			if (!StringUtils.isBlank(qtNumber)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_NUMBER);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtNumber);
				searchFilterBean.add(bean);
			}
			
			String procurementMode = qtnTenderSearchBean.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setAndOr(SearchCondition.CONDITION_OR.value());
				bean.setSearchField("procurementMode.parameterCode");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(procurementMode);
				searchFilterBean.add(bean);
			} else {
				// Filter By - procurementMode
				List<String> procurementModes = new ArrayList<String>();
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_QUOTATION);
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_TENDER);
				searchFilterBean.add(
						new SearchFilterBean(
								"procurementMode.parameterCode",
								SearchOperator.OPERATOR_IN.value(), 
								procurementModes));
			}
			
			Date fromCreatedDate = qtnTenderSearchBean.getFromCreatedDate();
			Date toCreatedDate = qtnTenderSearchBean.getToCreatedDate();
			if (null != fromCreatedDate || toCreatedDate != null) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(fromCreatedDate, toCreatedDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			
			Date fromPublishDate = qtnTenderSearchBean.getFromPublishDate();
			Date toPublishDate = qtnTenderSearchBean.getToPublishDate();
			if (null != fromPublishDate || null != toPublishDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.PUBLISH_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(fromPublishDate, toPublishDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			
			Date fromClosingDate = qtnTenderSearchBean.getFromClosingDate();
			Date toClosingDate = qtnTenderSearchBean.getToClosingDate();
			if (null != fromClosingDate || null != toClosingDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CLOSED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(fromClosingDate, toClosingDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			
			String status = qtnTenderSearchBean.getQtStatus();
			if (!StringUtils.isBlank(status)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtStatus.pmStatus.statusId", SearchOperator.OPERATOR_EQUALS.value(), status));
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtStatus.current", SearchOperator.OPERATOR_EQUALS.value(), true));
			}
			
			if (null != this.getManagedBean().getScQt().getPmOrgProfile()) {
				qtSearchBean
						.getDataFilterBeans()
						.add(new SearchFilterBean(
								"pmOrgProfile."
										+ OrgProfileUtil
												.getSearchBeanParentString(OrganizationType.MINISTRY
														.value()),
								SearchOperator.OPERATOR_EQUALS.value(),

								retrieveMinistryOrgProfile(
										this.getManagedBean().getScQt()
												.getPmOrgProfile()
												.getOrgProfileId())
										.getOrgProfileId()

						));
			}			
			
			qtSearchBean.getDataFilterBeans().add(new SearchFilterBean("scQtCommittees.scCommittee.committeeType.parameterCode", 
						SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getCommitteeType()));
			
			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}
//End - Committee Clone 
	
	/**
	 * Action to validate and save committee meeting and committee member details
	 */
	public void saveAction() {
		IQTMeetingSO qtMeetingSO = null;
		try {
			FacesScopes facesScopes = new FacesScopes();
			qtMeetingSO = BusinessDelegate.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION,IQTMeetingSO.class);
			
			validateCommitteeMembers();
			
			MeetingReqDTO reqDTO = meetingDtlsInfo();
			boolean isValidationSuccessful = validateMeetingDtlsInfo(reqDTO);
			if (!isValidationSuccessful) {
				return;
			}
			// 25037 - start - note 284
			finalizeRemarks(this.getManagedBean().getScQtCommittee().getQtCommitteRemarks());
			finalizeRemarks(this.getManagedBean().getCmtAppointmentApprovalRemarks());
			// 25037 - end - note 284
			reqDTO.setQtId(getManagedBean().getScQt().getQtId());
			reqDTO.setCommitteType(getManagedBean().getCommitteeType());
			reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			
			qtMeetingSO.saveFinalizationCommitteeDetails(reqDTO);
			
			renewSessionAndRetrieve();
			
			String variable1 = getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, 
								ParameterConst.DOCTYPE_QUOTATION) + " " + getManagedBean().getScQt().getQtNo();
			String variable2 = getMessageCommon("lbl.sc.meeting.confirmation");
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
						MessageUtils.getMessage(MessageConst.INFO_700004, new String[]{variable1, variable2}, 
						MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()))); 				  
		} finally {
			qtMeetingSO = null;
		}
	}
	
	private void renewSessionAndRetrieve() {
		ScQt scQt = getManagedBean().getScQt();
		String taskId = this.getManagedBean().getTaskId();
		// 25037 - start
		String taskName = this.getManagedBean().getTaskName();
		Calendar currentTaskCreatedDate = this.getManagedBean().getCurrentTaskCreatedDate();
		// 25037 - end
		String[] outComes = this.getManagedBean().getOutComes();
		String processName = this.getManagedBean().getProcessName();
		String committeeType = this.getManagedBean().getCommitteeType();
		Long currentStatusId = this.getManagedBean().getCurrentStatusId();
		SCEvaluationDataType evalDataType = (SCEvaluationDataType) this.getManagedBean().getPayLoad();
		
		renewSesionObject();
		
		// 25037 - start - note 284
		this.getManagedBean().setCommitteeType(committeeType);
		this.retriveQTDetails(scQt.getQtId());
		this.retriveMeetingDetails(scQt.getQtId());
		this.prepareMemberUpdate();
		// 25037 - end - note 284
		this.getManagedBean().setTaskId(taskId);
		// 25037 - start
		this.getManagedBean().setTaskName(taskName);
		this.getManagedBean().setCurrentTaskCreatedDate(currentTaskCreatedDate);
		// 25037 - end
		this.getManagedBean().setOutComes(outComes);
		this.getManagedBean().setProcessName(processName);
		this.getManagedBean().setCurrentStatusId(currentStatusId);
		this.getManagedBean().setPayLoad(evalDataType);
	}

	/**
	 * Action to submit the task to BPM for further processing
	 * @param outCome
	 */
	// 25037 - start
	public void submitAction(String outCome) {
		submitAction(outCome, null);
	}
	/**
	 * Action to submit the task to BPM for further processing
	 * @param outCome
	 * @param navigateTo
	 */
	public void submitAction(String outCome, String navigateTo) {
	// 25037 - end
		IQTMeetingSO meetingSO = null;
		try {
			FacesScopes facesScopes = new FacesScopes();
			meetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			 
			MeetingReqDTO reqDTO = meetingDtlsInfo();
			reqDTO.setSubmit(true);
			
			validateCommitteeMembers();
			// 25037 - start
			if (StringUtils.isEmpty(navigateTo)) {
				boolean isValidationSuccessful = validateMeetingDtlsInfo(reqDTO);
				if (!isValidationSuccessful) {
					return;
				}
			} else {
				IQTSuppFinalizationSO qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
				SuppFinalizationReqDTO sfReqDTO = new SuppFinalizationReqDTO();
				sfReqDTO.setCommitteeType(getManagedBean().getCommitteeType());
				sfReqDTO.setScQtCommittee(getManagedBean().getScQtCommittee());
				sfReqDTO.setUserLocale(new FacesScopes().getUserLocale());
				sfReqDTO.setSkipMeetingValidation(true);
				qtSuppFinalizationSO.validateCoordinateMeeting(sfReqDTO);
			}
			String taskName = this.getManagedBean().getTaskName() == null ? "" : this.getManagedBean().getTaskName();
			Paginator historyPaginator = this.getManagedBean().getHistoryPaginator();
			if (null != historyPaginator && null != historyPaginator.getModel()
					&& historyPaginator.getModel().size() > 1) {
				ScCommitteeMemberHistory latestHistory = (ScCommitteeMemberHistory) historyPaginator.getModel().get(0);
				Calendar historyCreatedDate = Calendar.getInstance();
				historyCreatedDate.setTimeInMillis(latestHistory.getCreatedDate().getTime());
				boolean isPrepareMeeting = taskName.equals(SCProcessNameConst.COORDINATE_FC_MEETING);
				reqDTO.setSendDONotification(isPrepareMeeting
						&& historyCreatedDate.compareTo(this.getManagedBean().getCurrentTaskCreatedDate()) > 0);
			}
			reqDTO.setCommitteeModified(this.getManagedBean().isCommitteeModified());
			// 25037 - end
			reqDTO.setCommitteType(getManagedBean().getCommitteeType());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setPayLoad(this.getManagedBean().getPayLoad());
			reqDTO.setOutcome(outCome);
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setRoleCode(getCommitteeRoleCode());
			
			// 25037 - start - note 284
			finalizeRemarks(this.getManagedBean().getScQtCommittee().getQtCommitteRemarks());
			finalizeRemarks(this.getManagedBean().getCmtAppointmentApprovalRemarks());
			meetingSO.submitFinalizationCommitteeMeeting(reqDTO);
			// 25037 - end - note 284
			
			String qtNo = getManagedBean().getScQt().getQtNo();
			updateFinalizationBriefingReportStatus(getManagedBean().getScQt().getQtId());
			
			// 25037 - start
			if (SCProcessNameConst.FINALISE_FC_SELECTION.equals(taskName) && reqDTO.isCommitteeModified()
					&& StringUtils.isNotEmpty(navigateTo)) {
				FacesContext fc = FacesContext.getCurrentInstance();
				if (this.getManagedBean().getFinalBB() != null) {
					getManagedBean().getFinalBB().loadPreparationTabDetails();
					getManagedBean().getFinalBB().initializeCommitteeRemarks();
					getManagedBean().getFinalBB().prepareUpdateCommitteeMember();
				} else {
					getManagedBean().getFinalBiddingBB().retrieveCommitteeAndMeetingDetails(
							getManagedBean().getScQt().getQtId());
					getManagedBean().getFinalBiddingBB().retrieveOnlineEndorsement();
					getManagedBean().getFinalBiddingBB().getManagedBean().setOnlineBiddingActiveIndex(0);
					getManagedBean().getFinalBiddingBB().prepareUpdateCommitteeMember();
				}
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, navigateTo);
			} else {
				String submitMessage = ResourceBundleUtils.getMessage("lbl.sc.sf.meeting.submit.message",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
				String qtParam = getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION)
						+ " " + qtNo;
				commonMessageAcknowledmentPage(MessageConst.INFO_700070, submitMessage, qtParam);
				renewSesionObject();
			}
			// 25037 - end
		} finally {
			meetingSO = null;
		}
	}
	
	private void updateFinalizationBriefingReportStatus(Long qtId){
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try{
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(qtId);
			ScQtFinalization finalization = qtSuppFinalizationSO.retrieveSupplierFinalizationByQtId(reqDTO).getScQtFinalization();
		if(finalization != null){
			finalization.setBriefingPaperUploaded(false);
			reqDTO.setScQtFinalization(finalization);
			boolean isFinalizationUpdated = qtSuppFinalizationSO.updateFinalization(reqDTO).isSuccessful();
			if(isFinalizationUpdated){
				log.info("$$$$$ Finalization Updated Sucessfully");
			}
		}
		}finally{
			qtSuppFinalizationSO = null;
		}
		
	}	
	private String getCommitteeRoleCode(){
		return getCommitteMemberByUserId()
				.getRoleCode();
	}

	private CommitteResDTO getCommitteMemberByUserId() {
		FacesScopes facesScopes = new FacesScopes();
		PmUser curLogedInUser = facesScopes.getUserSessionBean();
		String committeeType = this.getManagedBean()
				.getCommitteeType();
		ICommitteSO committeSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
		committeReqDTO.setPmUser(curLogedInUser);
		committeReqDTO.setCommitteeType(committeeType);
		
		return committeSO.retrieveCommitteMemberByUserId(
				committeReqDTO);
	}
	
	private MeetingReqDTO meetingDtlsInfo(){
		MeetingReqDTO reqDTO = new MeetingReqDTO();
		try {
			ScQtCommittee scQtCommittee = this.getManagedBean().getScQtCommittee(); //isCommitteeChanged();
			
			List<ScQtMeeting> meetingList = this.getManagedBean().getScQtMeetings();
			if (meetingList != null && !meetingList.isEmpty()) {
				scQtCommittee.setScQtMeetings(meetingList);
			}
			
			List<ScQtMeeting> deletedMeetingList = this.getManagedBean().getDeletedMeetingList();
			reqDTO.setDeletedMeetingList(deletedMeetingList);
			

			Map<String, ScCommitteeMember> currentCommMemberIcs = 
										new HashMap<String, ScCommitteeMember>();
			List<ScCommitteeMember> toBeAddedMembers = 
										new ArrayList<ScCommitteeMember>();
			List<ScCommitteeMember> currentCommitteeMembers = 
										getManagedBean().getCommitteeMembersList();
			List<ScCommitteeMember> deletedCommitteeMembers =
										getManagedBean().getDeletedCommitteeMembers();
			
			for (ScCommitteeMember currCommMem : 
				currentCommitteeMembers) {
	currentCommMemberIcs.put(
			currCommMem.getIcPassport(), currCommMem);
}

if (CollectionUtils.isNotEmpty(deletedCommitteeMembers)) {
	for (ScCommitteeMember deletedCommitteeMember 
								: deletedCommitteeMembers) {
		if (currentCommMemberIcs.containsKey(
				deletedCommitteeMember.getIcPassport())) {
			ScCommitteeMember currentCommitteeMember = currentCommMemberIcs
									.get(deletedCommitteeMember.getIcPassport());	
			deletedCommitteeMember=setCommitteeMemberData(deletedCommitteeMember, currentCommitteeMember);
			currentCommitteeMembers.remove(currentCommitteeMember);
			currentCommitteeMembers.add(deletedCommitteeMember);			
			toBeAddedMembers.add(deletedCommitteeMember);
		}
	}
}
if (CollectionUtils.isNotEmpty(
				toBeAddedMembers)) {
	deletedCommitteeMembers.removeAll(
					 toBeAddedMembers);
}
			
			scQtCommittee.getScCommittee().setScCommitteeMembers(
										   currentCommitteeMembers);
			reqDTO.setDeletedCommitteeActivityList(deletedCommitteeMembers);
						
			scQtCommittee.setMeetingAttendances(null);
			setUserDetails(scQtCommittee);
			reqDTO.setScQtCommitte(scQtCommittee);
			
			FacesScopes facesScopes = new FacesScopes();
			reqDTO.setPmUser(facesScopes.getUserSessionBean());
			reqDTO.setUserLocale(facesScopes.getUserLocale());
			
			return reqDTO;
		} finally {

		}
	}
	
	private ScQtCommittee isCommitteeChanged(){
		boolean committeeChanged = false;
		ScQtCommittee scQtCommittee = this.getManagedBean().getScQtCommittee();
		List<ScCommitteeMember> scCommitteeMemberList = this.getManagedBean().getCommitteeMembersList();
		
		//check if got members changes
		List<ScCommitteeMember> originalList = getManagedBean().getOriginalCommitteeMembersList();
		List<PmUser> memberList = new ArrayList<PmUser>();
		if(originalList != null){
			for(ScCommitteeMember member : originalList){
				memberList.add(member.getPmUser());
			}
			for(ScCommitteeMember member : scCommitteeMemberList){
				if (!memberList.contains(member.getPmUser())) {
					committeeChanged = true;
				}
			}
		}else{
			committeeChanged = true;
		}
		
		if(committeeChanged){
			//create new committee and suspend old one
			ScCommittee oldScCommittee = scQtCommittee.getScCommittee();
			oldScCommittee.setRecordStatus(false);
			
			ScCommittee newScCommittee = new ScCommittee();
			newScCommittee.setCommitteeId(null);
			newScCommittee.setCommitteeType(oldScCommittee.getCommitteeType());
			newScCommittee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_COMPLETED));
			setUserDetails(newScCommittee);
			
			for(ScCommitteeMember member : scCommitteeMemberList){
				member.setCommitteeMemberId(null);
				member.setScCommittee(newScCommittee);
			}
			
			newScCommittee.setScCommitteeMembers(scCommitteeMemberList);
			scQtCommittee.setScCommittee(newScCommittee);
		}
		return scQtCommittee;
	}
	
	private boolean validateMeetingDtlsInfo(MeetingReqDTO reqDTO) {
		boolean isValidationSuccessful = false;
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		List<ScQtMeeting> meetingList = getManagedBean().getScQtMeetings();
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
		
		if (reqDTO.isSubmit()) {
			if (CollectionUtils.isNotEmpty(meetingList)) {
				boolean hasAtleastOneActiveMeeting = false;
				for (ScQtMeeting meeting : meetingList) {
					if (DbValueConst.RECORD_STATUS_ACTIVE
							== meeting.getRecordStatusFlg()) {
						hasAtleastOneActiveMeeting = true;
						break;
					}
				}
				if (!hasAtleastOneActiveMeeting) {
					throw new EpException(MessageConst.ERROR_760291);
				}
			} else {
				throw new EpException(MessageConst.ERROR_760291);
			}
		}
		
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
											ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			
			SuppFinalizationReqDTO suppFinalReqDTO = new SuppFinalizationReqDTO();
			String committeeType = getManagedBean().getCommitteeType();
			suppFinalReqDTO.setCommitteeType(committeeType);
			suppFinalReqDTO.setScQtCommittee(reqDTO.getScQtCommitte());
			suppFinalReqDTO.setUserLocale(new FacesScopes().getUserLocale());
			qtSuppFinalizationSO.validateCoordinateMeeting(suppFinalReqDTO);
			
			isValidationSuccessful = true;
		} finally {
			qtSuppFinalizationSO = null;
		}
		
		return isValidationSuccessful;
	}
	
	private void validateCommitteeMembers() {
		List<ScCommitteeMember> scCommitteeMemberList 
					= this.getManagedBean().getCommitteeMembersList();
		if (CollectionUtils.isNotEmpty(scCommitteeMemberList)) {
			for (ScCommitteeMember member : scCommitteeMemberList) {
				PmParameter memberRole = member.getMemberRole();
				if (ParameterConst.COMMITTEE_ROLE_MEMBER
						.equals(memberRole.getParameterCode()) && 
							(member.getRepresentativeType() == null || 
								StringUtils.isBlank(
									member.getRepresentativeType()
										  		.getParameterCode()))) {
					
					throw new EpException(MessageConst.ERROR_760212, new String[]{
							ResourceBundleUtils.getMessage("lbl.sc.please.select.representative.type", 
						 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, 
						 			new FacesScopes().getUserLocale())
					});						
				}
			}
		}
		
		ICommitteSO committeSO = null;
		ScQtCommittee finalizationCommittee = this.getManagedBean().getScQtCommittee();
		
		try {
			Long qtId = this.getManagedBean().getScQt().getQtId();
			
			committeSO = (ICommitteSO) BusinessDelegate.getInstance().getServiceObject(
									   ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setScQtCommittee(finalizationCommittee);
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());
			committeSO.validateFcCommitteDetails(reqDTO);
		} finally {
			committeSO = null;
		}
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
	
	
	// this method checking two meeting dates or equal or not.
	private void compareMeetingDates(ScQtMeeting scqtMeeting,
			List<ScQtMeeting> meetingList) {
		//Sonarqube #83 - start
		if (null == meetingList)
			throw new IllegalArgumentException("Meeting list should not be null.");
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

				} else {
					duplicateDate = false;
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
	// 25037 - start
	private ICommitteSO getCommitteeSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
	}
	/**
	 * Navigate between main screen and update committee member screen. Populate
	 * data based on action type provided.
	 * 
	 * @param action
	 */
	public void navigateToUpdateMember(String action) {
		FacesContext fc = FacesContext.getCurrentInstance();
		SupplierFinalizationBackingBean finalBB = this.getManagedBean().getFinalBB();
		SupplierFinalizationBiddingBackingBean finalBiddingBB = this.getManagedBean().getFinalBiddingBB();
		String mainPage = "/xhtml/sc/supplierFinalization/confirmMeeting/confirmMeeting.xhtml";
		mainPage = finalBB == null ? mainPage : "/xhtml/sc/supplierFinalization/suppFinalizationMenu.xhtml";
		mainPage = finalBiddingBB == null ? mainPage
				: "/xhtml/sc/supplierFinalization/onlineBiddingEndorsement/endorsement.xhtml";

		if (CommonConstants.ACTION_BACK.equalsIgnoreCase(action)) {
			restoreUnconfirmChanges();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, mainPage);
		} else if (CommonConstants.ACTION_UPDATE.equalsIgnoreCase(action)) {
			updateAction();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "/xhtml/sc/supplierFinalization/confirmMeeting/memberUpdate.xhtml");
		} else if (CommonConstants.ACTION_CONFIRM.equalsIgnoreCase(action)) {
			validateCommitteeChanges();
			//28895 - 35500 start
			List<ScCommitteeMember> newCommitteeMembers = this.getManagedBean().getNewCommitteeAuditTrailList() != null
					? this.getManagedBean().getNewCommitteeAuditTrailList() : new ArrayList<ScCommitteeMember>();
			List<ScCommitteeMember> deletedCommitteeMembers = this.getManagedBean().getRemoveCommitteeAuditTrailList() != null
					? this.getManagedBean().getRemoveCommitteeAuditTrailList() : new ArrayList<ScCommitteeMember>();
			//28895 - 35500 end
			if (finalBB != null) {
				submitChanges(finalBB, mainPage);
			} else if (finalBiddingBB != null) {
				submitChanges(finalBiddingBB, mainPage);
			} else {
				saveAction();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, mainPage);
			}

			prepareCommitteeAuditTrail(newCommitteeMembers,deletedCommitteeMembers);//28895 - 35500
		}
	}

	private void updateAction() {
		this.getManagedBean().setTempCommitteeMembers(new ArrayList<ScCommitteeMember>());
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getCommitteeMembersList())) {
			for (ScCommitteeMember member : this.getManagedBean().getCommitteeMembersList()) {
				member = SerializationUtils.clone(member);
				this.getManagedBean().getTempCommitteeMembers().add(member);
			}
		}
		SupplierFinalizationBackingBean finalBB = this.getManagedBean().getFinalBB();
		SupplierFinalizationBiddingBackingBean finalBiddingBB = this.getManagedBean().getFinalBiddingBB();

		if (finalBB != null) {
			finalBB.saveSupplierFinalization(true);
		} else if (finalBiddingBB != null) {
			finalBiddingBB.saveOnlineEndorsement(true);
		}
	}
	
	private void submitChanges(Object backingBean, String mainPage) {
		if (backingBean instanceof SupplierFinalizationBackingBean) {
			SupplierFinalizationBackingBean finalBB = (SupplierFinalizationBackingBean) backingBean;
			finalBB.getManagedBean().setCommitteModified(this.getManagedBean().isCommitteeModified());
			this.getManagedBean().setTaskName(finalBB.getManagedBean().getTaskName());
			this.getManagedBean().setToken(finalBB.getManagedBean().getToken());
			this.getManagedBean().setPayLoad(finalBB.getManagedBean().getPayLoad());
			this.getManagedBean().setTaskId(finalBB.getManagedBean().getTaskId());
			if (this.getManagedBean().getQtCommitteeRemarks() == null) {
				this.getManagedBean().setQtCommitteeRemarks(new ArrayList<ScQtCommitteRemarks>());
			}
			if (finalBB.getManagedBean().getCommitteeRemarkList() == null) {
				finalBB.getManagedBean().setCommitteeRemarkList(new ArrayList<ScQtCommitteRemarks>());
			}
			this.getManagedBean().getQtCommitteeRemarks().clear();
			this.getManagedBean().getQtCommitteeRemarks().addAll(finalBB.getManagedBean().getCommitteeRemarkList());
			finalBB.getManagedBean().getMeetingBB().submitAction(BpmOutcomeConst.BPM_SUBMIT, mainPage);
		} else if (backingBean instanceof SupplierFinalizationBiddingBackingBean) {
			SupplierFinalizationBiddingBackingBean finalBiddingBB = (SupplierFinalizationBiddingBackingBean) backingBean;
			finalBiddingBB.getManagedBean().setCommitteModified(this.getManagedBean().isCommitteeModified());
			this.getManagedBean().setTaskName(finalBiddingBB.getManagedBean().getTaskName());
			this.getManagedBean().setToken(finalBiddingBB.getManagedBean().getToken());
			this.getManagedBean().setPayLoad(finalBiddingBB.getManagedBean().getPayLoad());
			this.getManagedBean().setTaskId(finalBiddingBB.getManagedBean().getTaskId());
			if (this.getManagedBean().getQtCommitteeRemarks() == null) {
				this.getManagedBean().setQtCommitteeRemarks(new ArrayList<ScQtCommitteRemarks>());
			}
			if (finalBiddingBB.getManagedBean().getCommitteeRemarkList() == null) {
				finalBiddingBB.getManagedBean().setCommitteeRemarkList(new ArrayList<ScQtCommitteRemarks>());
			}
			this.getManagedBean().getQtCommitteeRemarks().clear();
			this.getManagedBean().getQtCommitteeRemarks()
					.addAll(finalBiddingBB.getManagedBean().getCommitteeRemarkList());
			finalBiddingBB.getManagedBean().getMeetingBB().submitAction(BpmOutcomeConst.BPM_SUBMIT, mainPage);
		}
	}
	
	private void restoreUnconfirmChanges() {
		if (this.getManagedBean().getCommitteeMembersList() == null) {
			this.getManagedBean().setCommitteeMembersList(new ArrayList<ScCommitteeMember>());
		}
		if (this.getManagedBean().getTempCommitteeMembers() == null) {
			this.getManagedBean().setTempCommitteeMembers(new ArrayList<ScCommitteeMember>());
		}
		if (this.getManagedBean().getDeletedCommitteeMembers() == null) {
			this.getManagedBean().setDeletedCommitteeMembers(new ArrayList<ScCommitteeMember>());
		}
		this.getManagedBean().getCommitteeMembersList().clear();
		Map<Object, Object> map = CustomUtils.listToMap(this.getManagedBean().getDeletedCommitteeMembers(),
				"getIcPassport");
		List<ScCommitteeMember> remove = new ArrayList<>();
		for (ScCommitteeMember m : this.getManagedBean().getTempCommitteeMembers()) {
			if (map.get(m.getIcPassport()) != null) {
				m.setDeleted(false);
				m.setSelected(false);
				remove.add((ScCommitteeMember) map.get(m.getIcPassport()));
			}
			this.getManagedBean().getCommitteeMembersList().add(m);
		}
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getDeletedCommitteeMembers())) {
			this.getManagedBean().getDeletedCommitteeMembers().removeAll(remove);
		}
		// 25037 - start - note 284 
    	if(CollectionUtils.isNotEmpty(this.getManagedBean().getCmtAppointmentApprovalRemarks())){
    		this.getManagedBean().getCmtAppointmentApprovalRemarks().get(0).setRemark(null);
    	}
    	// 25037 - end - note 284 
	}
	
	/**
	 * Populate and prepare update committee screen
	 */
	public void prepareMemberUpdate() {
		ScQtCommittee qtCommittee = this.getManagedBean().getScQtCommittee();
		List<ScCommitteeMemberHistory> histories = this.getCommitteeSO().getMergedHistory(qtCommittee, false);
		List<ScCommitteeMemberHistory> legacyHistories = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(histories)) {
			for (ScCommitteeMemberHistory h : histories) {
				if (h.isLegacy()) {
					legacyHistories.add(h);
				}
			}
			histories.removeAll(legacyHistories);// remove legacy history
		}
		if (CollectionUtils.isNotEmpty(histories)) {
			ScCommitteeMemberHistory history = histories.get(0);
			@SuppressWarnings("unchecked")
			List<QtCommitteeMemberBean> prevCommitteeList = (List<QtCommitteeMemberBean>) (Object) history
					.deserializeList(history.getCommitteeMembers());
			this.getManagedBean().setPrevCommitteeMembers(prevCommitteeList);
		}
		// 25037 - start - note 284
		ScQt qt = this.getManagedBean().getScQt();
		if (CollectionUtils.isNotEmpty(qt.getScQtApprovals())) {
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
					this.getManagedBean().setCmtAppointmentApprovalRemarks(remarks);

				}
			}
		}
		// 25037 - end - note 284

		this.getManagedBean().setCurrentCommitteeType(
				this.getParameterDesc(qtCommittee.getScCommittee().getCommitteeType()));
		this.getManagedBean().setHistoryPaginator(new Paginator(histories));
		boolean isValidFor25037 = this.validateSupportedTask(this.getManagedBean().getScQt().getQtNo(),
				QTConstants.ALLOW_COMMITTEE_REASSIGNMENT);
		this.getManagedBean().setValidFor25037(isValidFor25037);
	}
	
	/**
	 * Populate selected history and display the currently selected member
	 * history.
	 * 
	 * @throws IOException
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
	
	/**
	 * Validate if changes in committee correct.
	 */
	public void validateCommitteeChanges() {
		ScQtCommittee qtCommittee = this.getManagedBean().getScQtCommittee();
		ScCommittee scCommittee = qtCommittee.getScCommittee();
		scCommittee.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
		scCommittee.getScCommitteeMembers().addAll(this.getManagedBean().getCommitteeMembersList());
		validateCommitteeMembers();
		if (isCommitteeModified(this.getManagedBean().getCommitteeMembersList())) {
			this.getManagedBean().setCommitteeModified(true);
			List<ScQtApprovalRemark> remarks = this.getManagedBean().getCmtAppointmentApprovalRemarks();// 25037 - note 284
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
	
	/**
	 * Change member role listener
	 * @param index
	 * @param committeeActivity
	 */
	public void changeMemberRole(int index, List<ScCommitteeMember> committeeActivity) {
		ScCommitteeMember activity = committeeActivity.get(index);

		List<PmParameterDesc> localParamDesc = this.getParameterDescsByTypeAndSearchBean(
				ParameterTypeConst.COMMITTEE_ROLE, null);
		if (null != activity.getMemberRoleId()) {
			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId().equals(activity.getMemberRoleId())) {

					activity.setMemberRole(paramDesc.getPmParameter());
					break;
				}
			}
		}
		if (activity.getMemberRole() != null
				&& activity.getMemberRole().getParameterCode().equals(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
			activity.setRepresentativeType(null);
		}
		this.getManagedBean().getCommitteeMembersList().remove(index);
		this.getManagedBean().getCommitteeMembersList().add(index, activity);
	}
	
	private boolean isCommitteeModified(List<ScCommitteeMember> currList) {
		List<QtCommitteeMemberBean> previousList = this.getManagedBean().getPrevCommitteeMembers();
		if (CollectionUtils.isEmpty(previousList)) {
			previousList = new ArrayList<>();
		}
		List<ScCommitteeMember> currentList = currList;
		if (CollectionUtils.isEmpty(currentList)) {
			currentList = new ArrayList<>();
		}
		String[] previousListArr = new String[previousList.size()];
		String[] currentListArr = new String[currentList.size()];
		for (int i = 0; i < previousListArr.length; i++) {
			QtCommitteeMemberBean m = previousList.get(i);
			String representativeType = m.getRepresentativeType() == null ? "" : m.getRepresentativeType();
			previousListArr[i] = m.getIdentificationNo().concat(m.getMemberRole()).concat(representativeType);
		}
		for (int i = 0; i < currentListArr.length; i++) {
			ScCommitteeMember m = currentList.get(i);
			String representativeType = m.getRepresentativeType() == null ? "" : m.getRepresentativeType()
					.getParameterCode();
			currentListArr[i] = m.getIcPassport().concat(m.getMemberRole().getParameterCode())
					.concat(representativeType);
		}
		Arrays.sort(previousListArr);
		Arrays.sort(currentListArr);

		return !StringUtils.join(previousListArr, ",").equalsIgnoreCase(StringUtils.join(currentListArr, ","));
	}

	// 25037 - end
	
	//#31870 - START
	private ScCommitteeMember addFCSecretary(Long userId,ScCommittee committee, String roleCode){
		IUserSO iUserSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
		IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IParameterSO.class);
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IOrgProfileSO.class);
		ScCommitteeMember secretaryMember = null;
		try{
			secretaryMember = new ScCommitteeMember();
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(userId);
			PmUser user = iUserSO.retrieveUserByUserId(userReqDTO).getPmUser();
			secretaryMember.setPmUser(user);
			secretaryMember.setMemberName(user.getUserName());
			secretaryMember.setIcPassport(user.getIdentificationNo());
			secretaryMember.setGrade(user.getGrade());
			if (null != user.getDesignation()) {
				secretaryMember.setDesignation(user.getDesignation());
			}
			secretaryMember.setPNP(user.isPMP());
			secretaryMember.setEmail(user.getEmail());
			ParameterReqDTO roleReqDTO = new ParameterReqDTO();
			roleReqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_SECRETARY);
			roleReqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
			ParameterResDTO roleResDTO = parameterSO.retrieveParameterByTypeAndCode(roleReqDTO);
			secretaryMember.setMemberRole(roleResDTO.getPmParameter());
			if (secretaryMember.getMemberRole() != null) {
				secretaryMember.setMemberRoleId(secretaryMember.getMemberRole().getParameterId());	
			}
			secretaryMember.setAccessRight(SourcingConstants.ACCESS_RIGHT_EDITABLE);
			PmOrgProfile pmOrgProfile = null;
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setUserId(userId);
			reqDTO.setOrgType(OrganizationType.DIVISION.value());
			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
			List<PmOrgProfile> pmOrgProfiles = resDTO.getOrgProfileList();
			if (pmOrgProfiles != null && pmOrgProfiles.size() > 0) {
				pmOrgProfile = pmOrgProfiles.get(0);
			}
			
			secretaryMember.setPmOrgProfile(pmOrgProfile);
			secretaryMember.setCreatedBy(userId);
			secretaryMember.setCreatedDate(new Timestamp(System.currentTimeMillis()));//#31870#21
			secretaryMember.setChangedBy(userId);
			secretaryMember.setChangedDate(new Timestamp(System.currentTimeMillis()));//#31870#21
			secretaryMember.setScCommittee(committee);
		}catch(Exception e){
			log.info("Has Error == "+e.getMessage());
			secretaryMember = null;
		}	
		return secretaryMember;
	}
	//#31870 - END
	
	/**
	 * 28895-35500 inserting audit trails for Committee Formation
	 * @param reqDTO
	 */
	private void prepareCommitteeAuditTrail(List<ScCommitteeMember> newCommitteeMembers,List<ScCommitteeMember> deletedCommitteeMembers) {
		IQTAuditTrailSO qtAuditTrailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAuditTrailSO.class);
		IAuditTrailSO auditTrailsSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.AUDIT, IAuditTrailSO.class);
		
		if ((CollectionUtils.isNotEmpty(newCommitteeMembers) || CollectionUtils.isNotEmpty(deletedCommitteeMembers)) &&
				auditTrailsSO.isAuditConfig(QTAuditConstant.QT_CONFIG_ID)) {
			
			AuditTrailResDTO auditResDTO = new AuditTrailResDTO();

			auditResDTO.setDocTypeId(this.getManagedBean().getScQt().getProcurementMode().getParameterId());
			auditResDTO.setDocId(this.getManagedBean().getScQt().getQtId());
			auditResDTO.setDocNo(this.getManagedBean().getScQt().getQtNo());
			auditResDTO.setCreatedBy(getUserIdSessionFromLoginId());
			auditResDTO.setStatusId(QTStatusConst.SF_PENDING_SUPPLIER_FINALIZATION);
			auditResDTO.setRoleCode(checkRolesForAuditTrail());
			auditResDTO.setAuGroupSectionId(QTAuditConstant.AU_SECTION_QC);
			if (auditResDTO.getRoleCode().equals(RoleCode.FPB_SECRETARY.value())
					||auditResDTO.getRoleCode().equals(RoleCode.PPB_SECRETARY.value())){
				auditResDTO.setAuGroupSectionId(QTAuditConstant.AU_SECTION_PB);
			}
			
			ArrayList<AuditTrailReqDTO> auditReqList = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(newCommitteeMembers)) {
				List<AuditTrailReqDTO> newCommitteeList = insertCommitteeAuditTrail(auditResDTO,newCommitteeMembers
						,QTAuditConstant.AU_TYPE_CREATE,qtAuditTrailSO);
				auditReqList.addAll(newCommitteeList);
			}
			if (CollectionUtils.isNotEmpty(deletedCommitteeMembers)) {
				List<AuditTrailReqDTO> deleteCommitteeList = insertCommitteeAuditTrail(auditResDTO,deletedCommitteeMembers
						,QTAuditConstant.AU_TYPE_DELETE,qtAuditTrailSO);
				auditReqList.addAll(deleteCommitteeList);
			}

			qtAuditTrailSO.insertAuditTrail(auditReqList);
		}
	}
	
	/**
	 * Method to insert  audit trails list for New/Delete Committee Members
	 * @param auditResDTO
	 * @param reqDTO
	 * @param committeeMembers
	 * @param type
	 * @param qtAuditTrailSO
	 * @return
	 */
	public List<AuditTrailReqDTO> insertCommitteeAuditTrail(AuditTrailResDTO auditResDTO,
			List<ScCommitteeMember> committeeMembers, String type, IQTAuditTrailSO qtAuditTrailSO) {
		List<AuditTrailReqDTO> auditReqList = new ArrayList<>();

		for (ScCommitteeMember committeeMember : committeeMembers) {
			auditResDTO.setActionType(type);
			auditResDTO.setQtFieldLabelId(QTAuditConstant.AU_FIELD_QC_NAME);
			if (type.equals(QTAuditConstant.AU_TYPE_CREATE)) {
				auditResDTO.setOldValue(null);
				auditResDTO.setNewValue(committeeMember.getMemberName());
			} else {
				auditResDTO.setNewValue(null);
				auditResDTO.setOldValue(committeeMember.getMemberName());
			}
			AuditTrailReqDTO auditReqDTOName = qtAuditTrailSO.populateAuditReqDTO(auditResDTO);
			auditReqList.add(auditReqDTOName);

			auditResDTO.setQtFieldLabelId(QTAuditConstant.AU_FIELD_QC_IDENTIFICATION);
			if (type.equals(QTAuditConstant.AU_TYPE_CREATE)) {
				auditResDTO.setNewValue(committeeMember.getPmUser().getIdentificationNo());
			} else {
				auditResDTO.setOldValue(committeeMember.getPmUser().getIdentificationNo());
			}
			AuditTrailReqDTO auditReqDTOIdentification = qtAuditTrailSO.populateAuditReqDTO(auditResDTO);
			auditReqList.add(auditReqDTOIdentification);

			auditResDTO.setQtFieldLabelId(QTAuditConstant.AU_FIELD_QC_ROLE);
			if (type.equals(QTAuditConstant.AU_TYPE_CREATE)) {
				auditResDTO.setNewValue(ParameterHelper.getInstance().getParameterDesc(
						ParameterTypeConst.COMMITTEE_ROLE, committeeMember.getMemberRole().getParameterCode(),
						SourcingConstants.MY_LOCALE));
			} else {
				auditResDTO.setOldValue(ParameterHelper.getInstance().getParameterDesc(
						ParameterTypeConst.COMMITTEE_ROLE, committeeMember.getMemberRole().getParameterCode(),
						SourcingConstants.MY_LOCALE));
			}
			AuditTrailReqDTO auditReqDTORole = qtAuditTrailSO.populateAuditReqDTO(auditResDTO);
			auditReqList.add(auditReqDTORole);

		}
		return auditReqList;
	}
	
	/**
	 * Method to check the session user's role
	 * #288895 - 35500
	 * @param qtId
	 * @return
	 */
	public String checkRolesForAuditTrail() {
		FacesScopes facesScopes = new FacesScopes();
		String role = RoleCode.QCA_SECRETARY.value();

		try {
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setLoginId(facesScopes.getUserSessionBean().getLoginId().toLowerCase());
			userReqDTO.setRoleRequired(true);
			UserResDTO resDTO = userSO.isUserExist(userReqDTO);
			List<String> pmRoles = resDTO.getPmRoleNames();

			String currentCommitteeType = this.getManagedBean().getCommitteeType();
			if (currentCommitteeType.equals("FSB") || currentCommitteeType.equals("FSW")) {
				currentCommitteeType = "FPB";
			}
			for (String userRole : pmRoles) {
				String trimmedRole = userRole.substring(0, 3);
				if (trimmedRole.equals(currentCommitteeType)) {
					return userRole;
				}
			}

		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE,
					SLALogConstant.QT_SUPPLIER_FINALIZATION_MEETING_BACKINGBEAN,
					this.getManagedBean().getScQt().getQtNo(),
					"SupplierFinalizationMeetingBackingBean.checkRolesForAuditTrail", getUserIdSessionFromLoginId(), e);
		}
		return role;
	}
}
