/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.supplierfinalization;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
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
import javax.persistence.criteria.JoinType;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
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
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.MemberRole;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.UserCommitteeSearchBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.supplierfinalization.SupplierFinalizationMeetingManagedBean;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Implementation for scheduling meeting for supplier finalization
 * 
 * @author Faradiana
 */

@ManagedBean(name = "suppFinalMeetingBackingBean")
@RequestScoped
public class SupplierFinalizationMeetingBackingBean extends 
						SourcingBaseBackingBean<SupplierFinalizationMeetingManagedBean> {
	
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
		
		getManagedBean().setHasErrors(Boolean.FALSE);

		try {
			SCEvaluationDataType dataType = getBpmTaskDetails(SCEvaluationDataType.class, null);
			Long qtId = Long.valueOf(dataType.getQtId());
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

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, QTConstants.FC_COORDINATE_MEETING_PAGE);
		} catch (Exception e) {
			getManagedBean().setHasErrors(Boolean.TRUE);
			handleException(e);
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
		try {
			qtMeetingSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qtId);
			String committeeType = getManagedBean().getCommitteeType();
			reqDTO.setCommitteType(committeeType);
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
				if (CollectionUtils.isNotEmpty(membersList)) {
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
				List<ScCommitteeMember> commMembers = scCommittee.getScCommitteeMembers();
				if (CollectionUtils.isNotEmpty(commMembers)) {
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
			
			List<ScQtCommitteRemarks> remarks = initializeRemarks(
					committee, null, committee.getQtCommitteRemarks(), ScQtCommitteRemarks.class);
			
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
			}
			
			ScQtMeeting meeting = new ScQtMeeting();
			meeting.setQtCommittee(this.getManagedBean().getScQtCommittee());
			setUserDetails(meeting);

			PmStatus status = getStatusById(QTStatusConst.MS_PENDING);
			meeting.setMeetingStatus(status);
			
			meetingList.add(meeting);
			this.getManagedBean().setScQtMeetings(meetingList);
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

		resetUserAction();
		
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
	 * Populate values into Representative Type drop down in Committee Members table
	 * @return  an array of representative type(s)
	 */
	public SelectItem[] getRepresentativeTypeDropdownValues(){
		SelectItem[] items = null;
		String committeeType = getManagedBean().getCommitteeType();
		if (ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B.equalsIgnoreCase(committeeType) 
				|| ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A.equalsIgnoreCase(committeeType) ) {
			items = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_QC);
		
		} else if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B.equalsIgnoreCase(committeeType)) {
			items = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_PBB);
		
		} else if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A.equalsIgnoreCase(committeeType)) {
			items = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_PBA);
		
		} else if (ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH.equalsIgnoreCase(committeeType)) {
			items = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_FPBH);
		
		} else if (ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK.equalsIgnoreCase(committeeType)) {
			items = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_FPBK);
		}
		return items;
	}
	
	// TODO :: Not in use
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
		this.getManagedBean().getUserSearchDataModel().reloadData();
	}
	
	/**
	 * reset user action.
	 */
	public void resetUserAction() {
		IPagingDataModel<PmUser> userSearhQCDataModel = getManagedBean()
				.getUserSearchDataModel();
		if (userSearhQCDataModel == null) {
			this.getManagedBean().setUserSearchDataModel(
					new UserQCPagingDataModel("existingMemberTbl", getManagedBean()
							.getUserSearchBean()));
		}
		UserCommitteeSearchBean userSearchBean 
									= getManagedBean().getUserSearchBean();
		userSearchBean.setName(null);
		userSearchBean.setIdentificationNo(null);
		userSearchBean.setIdentificationType(null);
		
		getManagedBean().setSelectedPmUsers(null);	
		getManagedBean().getUserSearchDataModel().resetData();		
	}	
	
	private void populateUserSearchCriteria(UserCommitteeSearchBean userSearchBean,
			ISearchBean<PmUser> searchBean) {

		if (userSearchBean != null) {
			String name = userSearchBean.getName();
			if (StringUtils.isNotBlank(name)) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.USER_NAME,
								SearchOperator.OPERATOR_LIKE.value(), 
								name, true));
			}
			
			String idno = userSearchBean.getIdentificationNo();
			if (StringUtils.isNotBlank(idno)) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.IDENTIFICATION_NO,
								SearchOperator.OPERATOR_LIKE.value(), 
								idno, true));
			}
			
			String idtype = userSearchBean.getIdentificationType();
			if (StringUtils.isNotBlank(idtype)) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"identificationCode.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(), 
								idtype, true));
			}
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg", 
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE)					
					);			
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserOrgs.recordStatusFlg", 
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE)					
					);
			
			
			ScQt scQt = this.getManagedBean().getScQt();
			if (null != scQt.getPmOrgProfile()) {
				Long ministryOrgProfile = retrieveMinistryOrgProfile(
						scQt.getPmOrgProfile().getOrgProfileId()).getOrgProfileId();
				String committeeType = getManagedBean().getCommitteeType();
				
				SearchFilterBean ptjOrgProfileFB = new SearchFilterBean(
						"pmUserOrgs.pmOrgProfile."
								+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
						SearchOperator.OPERATOR_EQUALS.value(),
						ministryOrgProfile,
						JoinType.LEFT);
				
				if (ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH.equals(committeeType) ||
						ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK.equals(committeeType)) {
					List<String> orgTypes = new ArrayList<String>();
					orgTypes.add(ParameterConst.ORG_TYPE_SM_PMS_SABAH);
					orgTypes.add(ParameterConst.ORG_TYPE_SM_PMS_SARAWAK);
					orgTypes.add(ParameterConst.ORG_TYPE_OTHERS);
					
					SearchFilterBean otherOrgFB = new SearchFilterBean(
							"pmUserOrgs.pmOrgProfile.orgTypeCode.parameterCode",
							SearchOperator.OPERATOR_IN.value(),
							orgTypes,
							SearchCondition.CONDITION_OR.value());
					searchBean.getDataFilterBeans().add(
							new SearchMultiFilterBean(ptjOrgProfileFB, otherOrgFB));
					
				} else {
					SearchFilterBean otherOrgFB = new SearchFilterBean(
							"pmUserOrgs.pmOrgProfile.orgTypeCode.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							ParameterConst.ORG_TYPE_OTHERS,
							SearchCondition.CONDITION_OR.value());
					
					if (ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A.equals(committeeType) ||
							ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B.equals(committeeType)) {
						SearchFilterBean ministryOrgProfileFB = new SearchFilterBean(
								"pmUserOrgs.pmOrgProfile.orgProfileId",
								SearchOperator.OPERATOR_EQUALS.value(),
								ministryOrgProfile);
						searchBean.getDataFilterBeans().add(
								new SearchMultiFilterBean(ptjOrgProfileFB, 
										new SearchMultiFilterBean(ministryOrgProfileFB, SearchCondition.CONDITION_OR.value(), otherOrgFB)));
					} else {
						searchBean.getDataFilterBeans().add(
								new SearchMultiFilterBean(ptjOrgProfileFB, otherOrgFB));
					}
				} 
			}
		}
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
		
		ICommitteSO committeeSO = null;
		try {
			committeeSO = BusinessDelegate.getInstance()
								.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setUser(pmUser);
			reqDTO.setCommitteeType(committeeType);
			
			PmOrgProfile pmOrgProfile = getManagedBean().getScQt().getPmOrgProfile();
			reqDTO.setPmOrgProfile(pmOrgProfile);
			
			CommitteResDTO resDTO = committeeSO.checkIfUserHasRoleByRoleCodeCommTypeAndOrgProfile(reqDTO);
			if (resDTO.isSuccessful() && 
					resDTO.isHasRequiredCommitteeRole()) {
				PmParameter chairPersonRole = getPmParameterByTypeAndCode(ParameterTypeConst.COMMITTEE_ROLE, 
						ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
				pmUser.setMemberRoleId(chairPersonRole.getParameterId());
				userRole = getParameterDesc(chairPersonRole);
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
			List<ScCommitteeMember> selectedCommitteeMemList 
							= getManagedBean().getSelectedCommitteeMembersList();
			
			if (CollectionUtils.isNotEmpty(currentCommitteeMemList) && 
					CollectionUtils.isNotEmpty(selectedCommitteeMemList)) {
				if (currentCommitteeMemList.size() == 
							selectedCommitteeMemList.size()) {
					currentCommitteeMemList.clear();
				} else {
					for (ScCommitteeMember committeeMember 
									: selectedCommitteeMemList) {
						currentCommitteeMemList.remove(committeeMember);
					}
				}
				prepareToDeleteCommitteeMembers(selectedCommitteeMemList);
				getManagedBean().setSelectedCommitteeMembersList(null);
			}
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
		PmParameter identificationCode = getPmParameterByTypeAndCode(ParameterTypeConst.IDENTIFICATION_TYPES, ParameterConst.IDENTIFICATION_TYPES_IC);
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
			super(componentId);
			this.userSearchBean = userQCSearchBean;
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
			IUserSO userSO;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserReqDTO userReqDTO = new UserReqDTO();
				populateUserSearchCriteria(userSearchBean, searchBean);
				userReqDTO.setUserSearchBean(searchBean);
				UserResDTO resDto = userSO.searchUserProfile(userReqDTO);
				return resDto.getUserResultBean();
			} finally {
				userSO = null;
			}
		}
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
			committeeMember.setPmOrgProfile(scQt.getPmOrgProfile());
			if (user.getMemberRoleId() == null) {
				PmParameter memberRole = getPmParameterByTypeAndCode(
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
			committeeMember.setAccessRight("E");
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
			committeeMember.setPmOrgProfile(scQt.getPmOrgProfile());
			
			if (commMember.getMemberRole() == null || 
					MemberRole.SECRETARY.getMemberRole()
						.equals(commMember.getMemberRole().getParameterCode())) {
				PmParameter memberRole = getPmParameterByTypeAndCode(
											ParameterTypeConst.COMMITTEE_ROLE, 
											ParameterConst.COMMITTEE_ROLE_MEMBER);
				commMember.setMemberRole(memberRole);
				committeeMember.setRepresentativeType(new PmParameter());
			}
			committeeMember.setMemberRole(commMember.getMemberRole());
			
			if (committeeMember.getMemberRole() != null) {
				committeeMember.setMemberRoleId(
						committeeMember.getMemberRole().getParameterId());	
			}
			committeeMember.setAccessRight("E");
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
			if (!StringUtils.isBlank(status) && !status.equals("60000")) {
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
		String[] outComes = this.getManagedBean().getOutComes();
		String processName = this.getManagedBean().getProcessName();
		String committeeType = this.getManagedBean().getCommitteeType();
		Long currentStatusId = this.getManagedBean().getCurrentStatusId();
		SCEvaluationDataType evalDataType = (SCEvaluationDataType) this.getManagedBean().getPayLoad();
		
		renewSesionObject();
		
		this.getManagedBean().setScQt(scQt);
		this.getManagedBean().setCommitteeType(committeeType);
		
		retriveMeetingDetails(scQt.getQtId());
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setOutComes(outComes);
		this.getManagedBean().setProcessName(processName);
		this.getManagedBean().setCurrentStatusId(currentStatusId);
		this.getManagedBean().setPayLoad(evalDataType);
	}

	/**
	 * Action to submit the task to BPM for further processing
	 * @param outCome
	 */
	public void submitAction(String outCome) {
		
		IQTMeetingSO meetingSO = null;
		try {
			FacesScopes facesScopes = new FacesScopes();
			meetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			 
			MeetingReqDTO reqDTO = meetingDtlsInfo();
			reqDTO.setSubmit(true);
			boolean isValidationSuccessful = validateMeetingDtlsInfo(reqDTO);
			if (!isValidationSuccessful) {
				return;
			}
			
			reqDTO.setCommitteType(getManagedBean().getCommitteeType());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setPayLoad(this.getManagedBean().getPayLoad());
			reqDTO.setOutcome(outCome);
			
			MeetingResDTO resDTO = meetingSO.submitFinalizationCommitteeMeeting(reqDTO);
			String qtNo = getManagedBean().getScQt().getQtNo();
			renewSesionObject();
			
			String variable1 = getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION) + " " + qtNo;
			String variable2 = getMessageCommon("lbl.sc.meeting.confirmation");
			commonMessageAcknowledmentPage(MessageConst.INFO_700026, variable1, variable2);
		} finally {
			meetingSO = null;
		}
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
			
			if (CollectionUtils.isNotEmpty(
					getManagedBean().getDeletedCommitteeMembers())) {
				for (ScCommitteeMember deletedCommitteeMember 
											: deletedCommitteeMembers) {
					if (currentCommMemberIcs.containsKey(
							deletedCommitteeMember.getIcPassport())) {
						ScCommitteeMember committeeMember = currentCommMemberIcs.get(
												deletedCommitteeMember.getIcPassport());
						deletedCommitteeMember.setDeleted(false);
						deletedCommitteeMember.setRepresentativeType(
									committeeMember.getRepresentativeType());
						currentCommitteeMembers.remove(committeeMember);
						currentCommitteeMembers.add(deletedCommitteeMember);
						if (deletedCommitteeMember.getRepresentativeType() != null) {
							deletedCommitteeMember.setRepresentativeTypeId(
									deletedCommitteeMember.getRepresentativeType().getParameterId());
						}
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
			
			finalizeRemarks(scQtCommittee.getQtCommitteRemarks());
			
			scQtCommittee.setMeetingAttendances(null);
			setUserDetails(scQtCommittee);
			
			// TODO To be removed - merge supplier finalization method doc types to cater old QTs
			if (QTStatusConst.SF_PENDING_BIDDING_CONFIRMATION_COMMITTEE
					.equals(getManagedBean().getCurrentStatusId()) ||
				QTStatusConst.BI_PENDING_CONFIRMATION_COMMITTEE
					.equals(getManagedBean().getCurrentStatusId())) {
				reqDTO.setBiddingMeeting(true);
			}
			reqDTO.setScQtCommitte(scQtCommittee);
			
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
			PmStatus status = new PmStatus();
			status.setStatusId(QTStatusConst.CMT_COMPLETED);
			newScCommittee.setCommitteStatus(status);
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
					
					throw new EpException(MessageConst.ERROR_100022, new String[]{
							ResourceBundleUtils.getMessage("lbl.sc.representative.type", 
						 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, 
						 			new FacesScopes().getUserLocale())
					});						
				}
			}
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
	
	private PmStatus getStatusById(Long statusId){
		IStatusSO statusSO = null;
		try{
			statusSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(statusId);
			
			StatusResDTO resDTO = statusSO.retrieveStatusByStatusId(reqDTO);
			PmStatus status = resDTO.getPmStatus();
		
			return status;
		} finally {
			statusSO = null;
		}
	}
	
	///to be removed
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


}
