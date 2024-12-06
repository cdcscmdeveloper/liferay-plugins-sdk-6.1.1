/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.sc.evaluation;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
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
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTAuditTrailSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
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
import my.ep.exception.EpException;
import my.ep.helper.Paginator;
import my.ep.jaxb.sc.committeedata.SCCommitteeDataType;
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
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
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
import my.ep.web.managedbean.sc.evaluation.EvaluationMeetingManagedBean;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
/**
 * The Class EvaluationMeetingBackingBean.
 * 
 * @author Garikapati Mangaiah.
 */

@ManagedBean(name = "evaluationMeetingBackingBean")
@ViewScoped
public class EvaluationMeetingBackingBean extends
		SourcingBaseBackingBean<EvaluationMeetingManagedBean> implements Serializable{

	/**
	 * The  serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** The search quotation tender. */
	private IPagingDataModel<ScQt> searchQuotatioTender = new clonePagingDataModel();

	/**
	 * Instantiates a new evaluation meeting backing bean.
	 */
	public EvaluationMeetingBackingBean() {

		if (this.getManagedBean().getCloneQtnTenderSearchBean() == null) {
			this.getManagedBean().setCloneQtnTenderSearchBean(
					new QtnTenderSearchBean());
		}
		if (this.getManagedBean().getCommitteMember() == null) {
			this.getManagedBean().setCommitteMember(new ScCommitteeMember());
		}

	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {
		// retriveMeetingDetails();
	}

	/**
	 * Gets the role type dropdown values.display committee
	 * roles(Member/Secretary) in add committee member popup.
	 * 
	 * @param committeeRole
	 *            (Mandatory)
	 * @return SelectItem[]
	 */

	public SelectItem[] getRoleTypeDropdownValues(boolean isChairPerson,
			ScQt scqt, PmUser user) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		boolean isPtj = false;
		if (isChairPerson) {
			isPtj = orgProfileCheckExist(scqt, user, isPtj);

		}
		if ((isChairPerson && isPtj == false) || !isChairPerson) {
			recommendList.add(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_IN.value(),
							recommendList));
		}

		recommendList.add(ParameterConst.COMMITTEE_ROLE_SECRETARY);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.COMMITTEE_ROLE, searchBean);
		return items;
	}

	private boolean orgProfileCheckExist(ScQt scqt, PmUser user, boolean isptj) {
		//PmOrgProfile orgProfile = scqt.getPmOrgProfile();
		PmOrgProfile orgProfile=null;
		try{
			
		orgProfile=retriveMinistryByOrgprofile(scqt
				.getPmOrgProfile().getOrgProfileId());
		if (null != user.getPmUserOrgs() && null!=orgProfile) {
			for (PmUserOrg userorg : user.getPmUserOrgs()) {
				log.debug("===user org record status==="
						+ userorg.isRecordStatus());
				//orgProfile.setOrgProfileId(273481L);
				log.debug("===qt PtjId===" + orgProfile.getOrgProfileId());
				if (userorg.isRecordStatus()
						&& null != userorg.getPmOrgProfile()) {
					PmOrgProfile localorgProfile=retriveMinistryByOrgprofile(userorg.getPmOrgProfile().getOrgProfileId());
					if(null!=localorgProfile && localorgProfile.getOrgProfileId().equals(orgProfile.getOrgProfileId())){
					isptj = true;
					break;
					}
				}

			}
		}
		}finally{
			
		}
		return isptj;
	}

	private PmOrgProfile retriveMinistryByOrgprofile(Long orgProfileId){
		IOrgProfileSO	orgProfileSO=null;
		PmOrgProfile orgProfile=null;
		try{
			
			orgProfileSO= BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			if(null!=orgProfileId){
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileId(orgProfileId);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveMinistryByOrgProfileId(reqDTO);
			if (resDTO.getPmOrgProfile() != null) {
			 orgProfile=resDTO.getPmOrgProfile();
			}
		}
		}finally{
			orgProfileSO=null;	
		}
		return orgProfile;
	}
	/**
	 * Gets the procurement mode dropdown values.display procurement mode
	 * values(Quotation/Tender) in committee member clone popup.
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getProcurementModeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO);
		recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));

		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.SC_PROCUREMENT_MODE, searchBean);
		return items;
	}

	/**
	 * Retrieve meeting details.Event handle to display all the meeting and
	 * committee members and remarks details.
	 * 
	 * @param qtId
	 *            (Mandatory)
	 * @param commType
	 *            (Mandatory)
	 */
	public void retriveMeetingDetails(Long qtId, String commType) {
		IQTMeetingSO meetingSO;
		IQTDetailSO qtDetailSo;
		try {
			List<ScQtMeeting> meetingRemoveList = this.getManagedBean()
					.getMeetingRemoveList();
			List<ScCommitteeMember> committeeRemoveList = this.getManagedBean()
					.getRemoveCommitteeList();
			if (null == meetingRemoveList) {
				this.getManagedBean().setMeetingRemoveList(
						new ArrayList<ScQtMeeting>());
			}

			if (null == committeeRemoveList) {
				this.getManagedBean().setRemoveCommitteeList(
						new ArrayList<ScCommitteeMember>());
			}
			this.getManagedBean().setCommitteeType(commType);
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			MeetingResDTO resDTO = new MeetingResDTO();
			QuationTenderResDTO qtResDTO = new QuationTenderResDTO();
			QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();

			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			qtDetailSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			if (null == this.getManagedBean().getQt()) {
				qtReqDTO.setQtId(qtId);
				qtResDTO = qtDetailSo.retriveQtById(qtReqDTO);

				this.getManagedBean().setQt(qtResDTO.getQtDetail());
			}
			if (this.getManagedBean().getScQtMeetings() == null) {
				reqDTO.setQtId(this.getManagedBean().getQt().getQtId());

				reqDTO.setCommitteType(commType);
				resDTO = meetingSO.retrieveMeetingDls(reqDTO);
				ScQtCommittee qtCommittee = resDTO.getQtCommittee();

				if (null != resDTO.getMeetingDtls() && !resDTO.getMeetingDtls().isEmpty()) {//29473-start
					
					for (ScQtMeeting scQtMeeting : resDTO.getMeetingDtls()) {
						Long meetingStatusId = scQtMeeting.getMeetingStatus() != null ? scQtMeeting
								.getMeetingStatus().getStatusId() : null;
						scQtMeeting.setMeetingStatusStr(meetingStatusId);
						if (scQtMeeting.getRecordStatusFlg().equals(
								DbValueConst.RECORD_STATUS_SUSPENDED)) { 
						this.getManagedBean().setHasPreviousMeeting(true);
					}
					}//29473-End

					this.getManagedBean().setScQtMeetings(
							resDTO.getMeetingDtls());

				}
				if (null != qtCommittee) {
					this.getManagedBean().setScQtCommittee(qtCommittee);

					ScCommittee sccommittee = resDTO.getQtCommittee()
							.getScCommittee();
					List<ScCommitteeMember> localList = new ArrayList<ScCommitteeMember>();
					if (null != sccommittee.getScCommitteeMembers()
							&& !sccommittee.getScCommitteeMembers().isEmpty()) {
						localList.addAll(sccommittee.getScCommitteeMembers());
					}

					Collections.sort(localList);
					this.getManagedBean().setCommitteeMemberActivity(localList);

					// }

					List<ScQtCommitteRemarks> qtRemarkList = this
							.getManagedBean().getQtCommitteeRemarks();
					if (qtRemarkList == null) {
						qtRemarkList = new ArrayList<>();//sonarrQubeIssue6#84
					}

					List<ScQtCommitteRemarks> remarks = qtCommittee.getQtCommitteRemarks();

					if (CollectionUtils.isNotEmpty(remarks)) {//sonarrQubeIssue6#84
						for (ScQtCommitteRemarks remark : remarks) {
							qtRemarkList.add(remark);
						}//sonarrQubeIssue6#84
						this.getManagedBean().setQtCommitteeRemarks(
								qtRemarkList);
					}
					qtRemarkList = initializeRemarks(qtCommittee, 
							qtCommittee.getCurrentStatus(),
							qtRemarkList, 
							ScQtCommitteRemarks.class);
					this.getManagedBean().setQtCommitteeRemarks(qtRemarkList);
					prepareCommitteeHistory(qtCommittee);// 25037
				}

			}
		} finally {
			meetingSO = null;
			qtDetailSo=null;
		}

	}

	/**
	 * Adds the meeting action.add empty row for meeting details datatable.
	 */
	public void addMeetingAction() {

		try {
			List<ScQtMeeting> meetingList = getManagedBean().getScQtMeetings();

			if (null == meetingList) {
				meetingList = new ArrayList<ScQtMeeting>();
			}

			ScQtMeeting meeting = new ScQtMeeting();

			meeting.setQtCommittee(this.getManagedBean().getScQtCommittee());
			setUserDetails(meeting);
			meetingList.add(meeting);

			meeting.setMeetingStatus(getPmStatusByStatusId(QTStatusConst.MS_PENDING));
			this.getManagedBean().setScQtMeetings(meetingList);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Adds the committee details.Event handle to display header information for
	 * committee details add popup.
	 */
	public void addCommitteeDetails() {
		this.getManagedBean().setCommitteeTypeDesc(
				ParameterHelper.getInstance().getParameterDesc(
						ParameterTypeConst.SC_COMMITTEE_TYPE,
						this.getManagedBean().getCommitteeType()));

		// #12471 Start
		IPagingDataModel<PmUser> userSearchCommitteeDataModel = getManagedBean().getUserSearchCommitteeDataModel();
		UserCommitteeSearchBean userCommitteeSearchBean = getManagedBean().getUserCommitteeSearchBean();
		if (null == userSearchCommitteeDataModel) {
			getManagedBean().setUserSearchCommitteeDataModel(
					new ExistingUserPagingDataModel(
							QTConstants.DATATABLE_EXISTINGCOMMITTEESEARCHID, userCommitteeSearchBean));
		}
		userCommitteeSearchBean.setOrgCat(ParameterConst.ORG_CATEGORY_PTJ);
		reset();
		// #12471 End
		getManagedBean().setSelectedPmUser(null);
		this.getManagedBean().setCommitteMember(null);
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		committeeMember.setUserSelection(QTConstants.USER_SELECTION);
		this.getManagedBean().setCommitteMember(committeeMember);
		// #12471
		this.getManagedBean().setNameRequired(false);
		this.getManagedBean().setAutoUpdate(Boolean.FALSE);
		getSelectionTypes();
	}

	/**
	 * Gets the selection types.Event handle to display the User selection
	 * type(Existing/New) radio buttons in add popup.
	 * 
	 * @return the selection types
	 */
	public void getSelectionTypes() {
		SelectionType[] type = SelectionType.values();
		List<SelectItem> selectionTypes = new ArrayList<SelectItem>();

		for (SelectionType selectionType : type) {
			selectionTypes.add(new SelectItem(selectionType.value(),
					selectionType.name()));
		}
		this.getManagedBean().setSelections(selectionTypes);
	}

	/**
	 * User selcetion action.Event handle to get the user selection
	 * type(Existing/New) values.
	 * 
	 * @param event
	 *            (Mandatory)
	 */
	public void userSelcetionAction(AjaxBehaviorEvent event) {
		String userSelection = (String) event.getComponent().getAttributes()
				.get("value");
		userSelection = this.getManagedBean().getCommitteMember()
				.getUserSelection();
		// logger.info("User Selection : " + userSelection);
		ScCommitteeMember committeeMember = this.getManagedBean()
				.getCommitteMember();
		if (null == committeeMember.getPmUser()) {
			committeeMember.setPmUser(new PmUser());
		}
		committeeMember.setUserSelection(userSelection);
		// this.getManagedBean().setCommitteMember(committeeMember);
		if (SelectionType.Existing.value().equalsIgnoreCase(
				getManagedBean().getCommitteMember().getUserSelection())) {
			committeeMember.setMemberRole(null);
			// getManagedBean().getCommitteMember().setExternalUser(true);

		} else if (SelectionType.New.value().equalsIgnoreCase(
				getManagedBean().getCommitteMember().getUserSelection())) {

			committeeMember.setMemberRole(new PmParameter());

			// getManagedBean().getCommitteMember().setExternalUser(true);

		}
		committeeMember.setMemberRoleId(null);
		this.getManagedBean().setCommitteMember(committeeMember);
	}

	/**
	 * add committee action.Event handle to add Existing or New Committee member
	 * to Committee Members List.
	 */
	public void addCommitteeAction() {
		// debugLogger("okCommitteeAction");

		UserReqDTO userReqDTO = new UserReqDTO();

		List<ScCommitteeMember> committeeActivity = getManagedBean()
				.getCommitteeMemberActivity();
		if (null == committeeActivity) {
			committeeActivity = new ArrayList<ScCommitteeMember>();
		}
		List<ScCommitteeMember> localList = new ArrayList<ScCommitteeMember>();
		ScCommitteeMember  scMember = getManagedBean().getCommitteMember();
		if ("N".equalsIgnoreCase(scMember.getUserSelection())) {
				
			ScCommitteeMember member = new ScCommitteeMember();
			member.setMemberName(scMember.getMemberName());
			member.setIcPassport(scMember.getIcPassport());
			member.setGrade(scMember.getGrade());
			member.setDesignation(scMember.getDesignation());
			member.setPNP(scMember.isPNP());
			member.setEmail(scMember.getEmail());
			member.setMemberRole(scMember.getMemberRole());
			if (member.getMemberRole() != null) {
				member.setMemberRoleId(member.getMemberRole().getParameterId());
			}
			member.setAccessRight(SourcingConstants.ACCESS_RIGHT_EDITABLE);
			member.setScCommittee(this.getManagedBean().getScQtCommittee()
					.getScCommittee());
			setUserDetails(member);
			// member.setCreatedBy(1L);
			// member.setChangedBy(1L);
			// member.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			// member.setPmOrgProfile(scQt.getPmOrgProfile());

			// member.setScCommittee(scCommittee);

			PmUser user = new PmUser();
			user.setUserName(member.getMemberName());
			user.setLoginId(member.getMemberName());
			user.setIdentificationNo(member.getIcPassport());
			user.setDesignation(member.getDesignation());
			user.setGrade(member.getGrade());
			user.setPMP(member.isPNP());
			user.setEmail(member.getEmail());
			setUserDetails(user);
			// user.setChangedBy(1L);
			// user.setCreatedBy(1L);
			// user.setCreatedDate(new Timestamp(System.currentTimeMillis()));

			// Identification Code
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO
					.setParameterType(ParameterTypeConst.IDENTIFICATION_TYPES);
			parameterReqDTO
					.setParameterCode(ParameterConst.IDENTIFICATION_TYPES_IC);
			PmParameter identificationCode = getParameterSO()
					.retrieveParameterByTypeAndCode(parameterReqDTO)
					.getPmParameter();
			user.setIdentificationCode(identificationCode);

			PmCountry pmCountry = new PmCountry();
			pmCountry.setCountryId(1L);
			user.setPmCountry(pmCountry);

			// pmUser.setPmAddressTypes(null);

			// User Notification
			// List<PmUserNotification> pmUserNotifications = new
			// ArrayList<PmUserNotification>();
			// pmUserNotification = new PmUserNotification();
			// pmUserNotification.setPmUser(user);
			// pmUserNotification.setNotifyMode(QTConstants.ACCESS_RIGHT);
			// pmUserNotification.setPreferred(true);
			// pmUserNotifications.add(pmUserNotification);
			// user.setPmUserNotifications(pmUserNotifications);

			// User Orgs
			List<PmUserOrg> pmUserOrgs = new ArrayList<PmUserOrg>();
			PmUserOrg userOrg = new PmUserOrg();
			userOrg.setPmUser(user);
			OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
			orgProfileReqDTO.setOrgType(ParameterConst.ORG_TYPE_MINISTRY);
			PmOrgProfile pmOrgProfile = getOrgProfileSO()
					.retrieveOrgProfileListByOrgType(orgProfileReqDTO)
					.getOrgProfileList().iterator().next();
			userOrg.setPmOrgProfile(pmOrgProfile);
			userOrg.setEffDate(new Timestamp(System.currentTimeMillis()));
			Timestamp expDate = new Timestamp(System.currentTimeMillis());
			userOrg.setExpDate(expDate);
			List<PmUserRole> pmUserRoles = new ArrayList<PmUserRole>();
			PmUserRole pmUserRole = new PmUserRole();
			pmUserRole.setPmUserOrg(userOrg);
			pmUserRoles.add(pmUserRole);
			PmRole pmRole = new PmRole();
			pmRole.setRoleCode(RoleCode.PLAN_OFFICER.value());
			pmUserRole.setPmRole(pmRole);
			userOrg.setPmUserRoles(pmUserRoles);
			pmUserOrgs.add(userOrg);
			user.setPmUserOrgs(pmUserOrgs);
			user.setOrgTypeCode(pmOrgProfile.getOrgTypeCode());

			// User Preference
			// PmUserPreference pmUserPreference = new PmUserPreference();
			// pmUserPreference.setPmUser(user);
			// pmUserPreference.setFormatDate("DD-MMM-YYYY");
			// pmUserPreference.setFormatTime("HH:mm:ss");
			// pmUserPreference.setFormatNumber("#,##0");
			// pmUserPreference.setPreferredLanguage("en");
			// pmUserPreference.setNotifyRemovalDay(30);
			// user.setPmUserPreference(pmUserPreference);

			userReqDTO.setOrgType(OrganizationType.OTHERS.value());
			userReqDTO.setPmUser(user);
			UserResDTO resDTO = getUserSO().saveUser(userReqDTO);
			
			member.setPmUser(resDTO.getPmUser());
			setCommitteeMemberOrgProfile(member);
			// ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
			// activity.setScQtCommittee(this.getManagedBean().getScQtCommittee());
			// activity.setScCommitteeMember(member);
			// setUserDetails(activity);
			localList.add(member);
			// committeeActivity.add(activity);
			// memberList.add(member);
			// committeeActivity.addAll(localList);
			// Collections.sort(committeeActivity);
			// this.getManagedBean().setCommitteeMemberActivity(committeeActivity);

		} else {

			List<String> itemIds = new ArrayList<String>();
			if (null == getManagedBean().getSelectedPmUser()
					|| getManagedBean().getSelectedPmUser().isEmpty()) {
				throw new EpException(MessageConst.ERROR_760147);
			} else {
				for (PmUser user : getManagedBean().getSelectedPmUser()) {
					ScCommitteeMember committeeMember = new ScCommitteeMember();
					committeeMember.setMemberName(user.getUserName());
					committeeMember.setIcPassport(user.getIdentificationNo());
					committeeMember.setDesignation(user.getDesignation());

					committeeMember.setEmail(user.getEmail());

					committeeMember.setGrade(user.getGrade());
					committeeMember.setPNP(user.isPMP());
					committeeMember.setPmUser(user);
					/*
					 * committeeMember.setPmOrgProfile(this.getManagedBean().getQt
					 * () .getPmOrgProfile());
					 */

					if (user.isPMP() == true) {
						ParameterReqDTO reqDTO = new ParameterReqDTO();
						if (null == user.getMemberRoleId()) {
							reqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
							reqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
							ParameterResDTO resDTO = getParameterSO()
									.retrieveParameterByTypeAndCode(reqDTO);
							committeeMember.setMemberRole(resDTO
									.getPmParameter());
						} else {
							reqDTO.setParameterId(user.getMemberRoleId());
							ParameterResDTO resDTO = getParameterSO()
									.retrieveParameterById(reqDTO);
							committeeMember.setMemberRole(resDTO
									.getPmParameter());
						}
					} else {
						ParameterReqDTO reqDTO = new ParameterReqDTO();
						if (null == user.getMemberRoleId()) {
							reqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
							reqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_MEMBER);
							ParameterResDTO resDTO = getParameterSO()
									.retrieveParameterByTypeAndCode(reqDTO);
							committeeMember.setMemberRole(resDTO
									.getPmParameter());
						} else {
							reqDTO.setParameterId(user.getMemberRoleId());
							ParameterResDTO resDTO = getParameterSO()
									.retrieveParameterById(reqDTO);
							committeeMember.setMemberRole(resDTO
									.getPmParameter());
						}
					}
					if (committeeMember.getMemberRole() != null) {
						committeeMember.setMemberRoleId(committeeMember
								.getMemberRole().getParameterId());
					}
					committeeMember.setAccessRight(SourcingConstants.ACCESS_RIGHT_EDITABLE);
					committeeMember.setScCommittee(this.getManagedBean()
							.getScQtCommittee().getScCommittee());
					setCommitteeMemberOrgProfile(committeeMember);
					setUserDetails(committeeMember);

					if (committeeActivity.isEmpty()) {

						// ScQtCommitteeActivity activity = new
						// ScQtCommitteeActivity();
						// setUserDetails(activity);
						// activity.setScQtCommittee(this.getManagedBean()
						// .getScQtCommittee());
						// activity.setScCommitteeMember(committeeMember);
						// committeeActivity.add(activity);
						localList.add(committeeMember);
					} else {
						for (ScCommitteeMember committee : committeeActivity) {
							itemIds.add(committee.getIcPassport());
						}
						if (CollectionUtils.isNotEmpty(itemIds)) {// #12471
							if (itemIds.contains(user.getIdentificationNo())) {
								throw new EpException(MessageConst.ERROR_760194);
							} else {
								// #12471 Start
								QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
								reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
								reqDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT);
								ScQtApproval approval = this.getQTDetailSO().retrieveApprovalDtlsByQtIdNType(reqDTO).getQtApproval();
								if (approval != null && approval.getApproverSettings() != null) {
									for (ScQtApproverSettings approvalUser : approval.getApproverSettings()) {
										if (approvalUser.getPmUser().getUserId().equals(committeeMember.getPmUser().getUserId())) {
											throw new EpException(MessageConst.ERROR_775301,
													new String[] { committeeMember.getMemberName() });
										}
									}
								}
								// #12471 End
								localList.add(committeeMember);
							}
						}
					}
				}
			}
		}
		validateMember(this.getManagedBean().getQt(), localList, this.getManagedBean().getCommitteeType());
		committeeActivity.addAll(localList);
		Collections.sort(committeeActivity);
		this.getManagedBean().setCommitteeMemberActivity(committeeActivity);
		this.getManagedBean().setAutoUpdate(Boolean.TRUE);
		this.getManagedBean().setNewCommitteeAuditTrailList(localList); //#28895-35500
		// 25037 - start
		Map<Object, Object> map = CustomUtils.listToMap(this.getManagedBean().getCommitteeMemberActivity(),
				QTSearchBeanConst.SEARCH_GETICPASSPORT);
		List<ScCommitteeMember> listToSync = new ArrayList<>();
		List<ScCommitteeMember> listToReSync = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(this.getManagedBean().getRemoveCommitteeList())) {
			for (ScCommitteeMember m : this.getManagedBean().getRemoveCommitteeList()) {
				ScCommitteeMember em = (ScCommitteeMember) map.get(m.getIcPassport());
				if (em != null) {
					m.setDeleted(false);
					m.setSelected(false);
					listToSync.add(m);
					listToReSync.add(em);
				}
			}
			if (CollectionUtils.isNotEmpty(this.getManagedBean().getCommitteeMemberActivity())) {
				this.getManagedBean().getCommitteeMemberActivity().removeAll(listToReSync);
				this.getManagedBean().getCommitteeMemberActivity().addAll(listToSync);
			}
			this.getManagedBean().getRemoveCommitteeList().removeAll(listToSync);
		}
		// 25037 - end	
		RequestContext.getCurrentInstance().execute(
				"newspecificationcommittee.hide()");
	}

	/**
	 * Check committee formation.Event handle to check committee members(1
	 * Chairperosn,1 Secretary,at least 2 member) exist or not in the list
	 * 
	 * @param metReqDtO
	 *            the met req dt o
	 */
	private void checkCommitteeFormation(MeetingReqDTO metReqDtO) {
		IQTMeetingSO meetingso ;
		try{
			meetingso = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTMeetingSO.class);
		metReqDtO.setUser(new FacesScopes().getUserSessionBean());
		metReqDtO.setUserLocale(new FacesScopes().getUserLocale());
		metReqDtO.setCommitteType(this.getManagedBean().getCommitteeType());
		meetingso.validateCommitteDtls(metReqDtO);
		}finally{
			meetingso =null;
		}
		
	}

	/**
	 * Save committee action.Event handle to save the meeting and committee
	 * members information.
	 */
	public void saveCommitteeAction() {
		IQTMeetingSO meetingso;
		try {
			MeetingReqDTO metReqDtO = prepareMeetingDtls();
			checkCommitteeFormation(metReqDtO);
			 meetingso = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTMeetingSO.class);

			String taskId = this.getManagedBean().getTaskId();
			String[] outComes = this.getManagedBean().getOutComes();
			Long qtId = this.getManagedBean().getQt().getQtId();
			String committeeType = this.getManagedBean().getCommitteeType();
			String taskName = this.getManagedBean().getTaskName();
			Long currentStatusId = this.getManagedBean().getCurrentStatusId();
			Object object = this.getManagedBean().getPayLoad();
			String QtNum = this.getManagedBean().getQt().getQtNo();
			// 25037 - start
			boolean modified = isCommitteeModified(this.getManagedBean().getCommitteeMemberActivity())
					&& this.getManagedBean().isValidFor25037();
			metReqDtO.setCommitteeModified(modified);
			metReqDtO.setTaskId(this.getManagedBean().getTaskId());
			metReqDtO.setTaskName(this.getManagedBean().getTaskName());
			metReqDtO.setOutcome(BpmOutcomeConst.BPM_SAVE);
			// 25037 - end
			MeetingResDTO meetingResDTO = meetingso.saveQTDtl(metReqDtO);
			renewSesionObject();
			this.getManagedBean().setTaskId(taskId);
			this.getManagedBean().setOutComes(outComes);
			this.getManagedBean().setTaskName(taskName);
			this.getManagedBean().setCurrentStatusId(currentStatusId);
			this.getManagedBean().setPayLoad(object);
			this.getManagedBean().setCloneQtnTenderSearchBean(
					new QtnTenderSearchBean());
			// getRoleTypeDropdownValues(ParameterTypeConst.COMMITTEE_ROLE);
			retriveMeetingDetails(qtId, committeeType);
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													MessageConst.INFO_700004,
													new String[] {
															getMessageCommon("lbl.sc.evaluation.meeting"),
															QtNum },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
		} finally {
			meetingso=null;
		}
	}

	/**
	 * Gets the meeting to be remove list.
	 * 
	 * @return the meeting to be remove list
	 */
	private List<ScQtMeeting> getMeetingToBeRemoveList() {
		List<ScQtMeeting> toBeRemoveList = new ArrayList<ScQtMeeting>();

		List<ScQtMeeting> meetingRemoveList = this.getManagedBean()
				.getMeetingRemoveList();
		if (null != meetingRemoveList && meetingRemoveList.size() > 0) {
			toBeRemoveList.addAll(meetingRemoveList);
		}

		return toBeRemoveList;
	}

	/**
	 * Gets the committee to be remove list.
	 * 
	 * @return the committee to be remove list
	 */
	private List<ScCommitteeMember> getCommitteeToBeRemoveList() {
		List<ScCommitteeMember> toBeRemoveList = new ArrayList<ScCommitteeMember>();

		List<ScCommitteeMember> committeeRemoveList = this.getManagedBean()
				.getRemoveCommitteeList();
		if (null != committeeRemoveList && committeeRemoveList.size() > 0) {
			toBeRemoveList.addAll(committeeRemoveList);
		}

		return toBeRemoveList;
	}

	/**
	 * Prepare meeting dtls.prepare data for save or submit buttons
	 * 
	 * @return the meeting req dto
	 */
	private MeetingReqDTO prepareMeetingDtls() {
		MeetingReqDTO metReqDtO = new MeetingReqDTO();
		try {

			ScQtCommittee qtCommittee = this.getManagedBean()
					.getScQtCommittee();
			List<ScQtMeeting> meetingList = this.getManagedBean()
					.getScQtMeetings();
			List<ScQtMeeting> localMeetingList = new ArrayList<ScQtMeeting>();
			if (meetingList != null && !meetingList.isEmpty()) {
				for (ScQtMeeting meeting : meetingList) {
					if(null!=meeting.getMeetingDate()){
					compareMeetingDates(meeting, localMeetingList);
					}else{
						throw new EpException(MessageConst.ERROR_760418);
					}
				}
				qtCommittee.setScQtMeetings(meetingList);
			} else {
				qtCommittee.setScQtMeetings(null);
			}
			List<ScCommitteeMember> scQtCommitteeActivity = this
					.getManagedBean().getCommitteeMemberActivity();
			if (ParameterConst.EVALUATION_TYPES_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getQt().getEvaluationType())) {
				retriveExistOtherCommittee();
//				checkCommitteeMembersExistOtherCommittees(this.getManagedBean()
//						.getOtherCommitteeMembersList(), scQtCommitteeActivity);
			}

			if (scQtCommitteeActivity != null
					&& !scQtCommitteeActivity.isEmpty()) {
				qtCommittee.getScCommittee().setScCommitteeMembers(
						scQtCommitteeActivity);

				// qtCommittee.setCommitteeActivities(scQtCommitteeActivity);
			}

			List<ScQtCommitteRemarks> remarksList = this.getManagedBean()
					.getQtCommitteeRemarks();

			if (isNotNullOREmpty(remarksList)) {
				qtCommittee.setQtCommitteRemarks(finalizeRemarks(remarksList));

			}

			metReqDtO.setDeletedMeetingList(getMeetingToBeRemoveList());

			metReqDtO
					.setDeletedCommitteeActivityList(getCommitteeToBeRemoveList());
			qtCommittee.setCommitteeActivities(null);
			qtCommittee.setMeetingAttendances(null);
			metReqDtO.setScQtCommitte(qtCommittee);
			metReqDtO.setCommitteType(this.getManagedBean().getCommitteeType());
			metReqDtO.setPmUser(new FacesScopes().getUserSessionBean());
			// 25037 - start
			if (CollectionUtils.isNotEmpty(this.getManagedBean().getScQtApprovalRemarks())) {
				ScQtApprovalRemark remark = this.getManagedBean().getScQtApprovalRemarks().get(0);
				if (StringUtils.isNotEmpty(remark.getRemark())) {
					metReqDtO.setScQtApprovalRemark(remark);
				}
			}
			//#25037		
			prepareCommitteeAuditTrail();// 28895 - 35500
			// 25037 - end
		} finally {

		}
		return metReqDtO;
	}

	/**
	 * Existing committee search action.Event handle to search existing
	 * committee members list.
	 */
	public void existingCommitteeSearchAction() {
		// #12471 Start
		UserCommitteeSearchBean searchBean = this.getManagedBean().getCommitteeSearchBean();
		if (null == searchBean)
			return;
		if (StringUtils.isBlank(searchBean.getName()) && StringUtils.isBlank(searchBean.getIdentificationNo())) {
			throw new EpException(MessageConst.ERROR_775300); 
		} 
		getManagedBean().getUserSearchCommitteeDataModel().reloadData();
		// #12471 End
	}

	/**
	 * Adds the clone committee.Event handle to create search bean for clone
	 * popup.
	 */
	public void addCloneCommittee() {
		this.getManagedBean().setCloneSelectQt(null);
		this.getManagedBean().setAutoUpdate(Boolean.FALSE);
		this.getManagedBean().setCloneQtnTenderSearchBean(
				new QtnTenderSearchBean());
		this.getManagedBean().setCloneCommitteMemebers(
				new ArrayList<ScCommitteeMember>());
		this.getManagedBean().setCloneCommitteMemebersSelect(new ArrayList<ScCommitteeMember>());
		searchQuotatioTender.resetData();
	}

	/**
	 * Clone search committee action.Event handle to search clone committee
	 * members.
	 */
	public void cloneSearchCommitteAction() {
		try {
			this.getManagedBean().setCloneSelectQt(null);
			this.getManagedBean().setCloneCommitteMemebers(
					new ArrayList<ScCommitteeMember>());
			// searchQuotatioTender = new clonePagingDataModel();
			searchQuotatioTender.reloadData();

		} finally {

		}
	}

	/**
	 * On row clone action.Event handle to select the scqt object based on scqt
	 * and committee type to display existing committee members in clone popup.
	 * 
	 * @param event
	 *            (Mandatory)
	 */
	public void onRowCloneAction(SelectEvent event) {
		// debugLogger("onRowCloneAction");
		ScQt scQt = (ScQt) event.getObject();
		this.getManagedBean().setCloneSelectQt(scQt);
		this.getManagedBean().setCloneCommitteMemebersSelect(
				new ArrayList<ScCommitteeMember>());
		CommitteReqDTO reqDTO = new CommitteReqDTO();

		reqDTO.setQtId(scQt.getQtId());
		reqDTO.setCommitteeType(this.getManagedBean().getCommitteeType());
		CommitteResDTO resDTO = null;
		ICommitteSO committeSO;
		try {
			 committeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			resDTO = committeSO
					.retrieveCommitteMembersByQtIdAndCommType(reqDTO);
			this.getManagedBean().setCloneCommitteMemebers(
					new ArrayList<ScCommitteeMember>());
			if (null != resDTO
					&& null != resDTO.getCommMemberResultBean()
					&& null != resDTO.getCommMemberResultBean().getResultList()
					&& !resDTO.getCommMemberResultBean().getResultList()
							.isEmpty()) {

				this.getManagedBean().setCloneCommitteMemebers(
						resDTO.getCommMemberResultBean().getResultList());
			}

		} finally {
			committeSO=null;
		}
	}

	/**
	 * Reset.Event handle to reset the values in add popup.
	 */
	public void reset() {
		// #12471 start
		resetCommitteeSearchBean(this.getManagedBean().getQt());
		getManagedBean().getUserSearchCommitteeDataModel().resetData();
		// #12471 end
	}

	/**
	 * Reset clone.Event handle to reset the values in clone popup.
	 */
	public void resetClone() {
		QtnTenderSearchBean qtnTenderSearchBean = this.getManagedBean()
				.getCloneQtnTenderSearchBean();
		qtnTenderSearchBean.setQtTitle(null);
		qtnTenderSearchBean.setQtNo(null);
		qtnTenderSearchBean.setProcurementMode(null);
		qtnTenderSearchBean.setFromCreatedDate(null);
		qtnTenderSearchBean.setToCreatedDate(null);
		qtnTenderSearchBean.setQtStatus(null);
		qtnTenderSearchBean.setFromPublishDate(null);
		qtnTenderSearchBean.setToPublishDate(null);
		qtnTenderSearchBean.setFromClosingDate(null);
		qtnTenderSearchBean.setToClosingDate(null);
		this.getManagedBean().setCloneSelectQt(null);
		this.getManagedBean().setCloneCommitteMemebers(
				new ArrayList<ScCommitteeMember>());
		searchQuotatioTender = new clonePagingDataModel();
		searchQuotatioTender.reloadData();
	}

	/**
	 * Clone committee dtls.Event handle to add the committee member for
	 * Committee members list.
	 */
	public void cloneCommitteeDtls() {
		List<ScCommitteeMember> committeeMemberList = this.getManagedBean()
				.getCommitteeMemberActivity();
		if (committeeMemberList == null) {
			committeeMemberList = new ArrayList<ScCommitteeMember>();
		}
		List<ScCommitteeMember> cloneCommitteeList = this.getManagedBean()
				.getCloneCommitteMemebersSelect();
		if (null == cloneCommitteeList || cloneCommitteeList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_760147);
		}

		List<ScCommitteeMember> localList = new ArrayList<ScCommitteeMember>();
		ScCommitteeMember memberClone = null;
		List<String> itemIds = new ArrayList<String>();

		if (committeeMemberList.isEmpty()) {
			for (ScCommitteeMember scCommitteeMember : cloneCommitteeList) {
				scCommitteeMember.setScCommittee(this.getManagedBean()
						.getScQtCommittee().getScCommittee());
				setUserDetails(scCommitteeMember);

				// ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
				// activity.setScQtCommittee(this.getManagedBean()
				// .getScQtCommittee());
				// activity.setScCommitteeMember(scCommitteeMember);
				// setUserDetails(activity);
				
				//20190625@ for bug 25986/25988/26017 - Start
				memberClone = (ScCommitteeMember) scCommitteeMember.cloneEntity();
				memberClone.setCommitteeMemberId(null);
				localList.add(memberClone);
				//20190625@ for bug 25986/25988/26017 - End
			}
		} else {
			for (ScCommitteeMember committee : committeeMemberList) {
				itemIds.add(committee.getIcPassport());
			}
			for (ScCommitteeMember committees : cloneCommitteeList) {
				if (itemIds.size() > 0) {
					if (itemIds.contains(committees.getIcPassport())) {
						throw new EpException(MessageConst.ERROR_760194);

					} else {
						committees.setScCommittee(this.getManagedBean()
								.getScQtCommittee().getScCommittee());
						setUserDetails(committees);

						// ScQtCommitteeActivity activity = new
						// ScQtCommitteeActivity();
						// activity.setScQtCommittee(this.getManagedBean()
						// .getScQtCommittee());
						if (committees.isPNP() == true) {
							if (null != committees.getMemberRole()
									&& ParameterConst.COMMITTEE_ROLE_SECRETARY
											.equalsIgnoreCase(committees
													.getMemberRole()
													.getParameterCode())) {
								ParameterReqDTO reqDTO = new ParameterReqDTO();
								reqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
								reqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
								ParameterResDTO resDTO = getParameterSO()
										.retrieveParameterByTypeAndCode(reqDTO);
								committees.setMemberRole(resDTO
										.getPmParameter());
							}
						} else {
							if (null != committees.getMemberRole()
									&& ParameterConst.COMMITTEE_ROLE_SECRETARY
											.equalsIgnoreCase(committees
													.getMemberRole()
													.getParameterCode())) {
								ParameterReqDTO reqDTO = new ParameterReqDTO();
								reqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
								reqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_MEMBER);
								ParameterResDTO resDTO = getParameterSO()
										.retrieveParameterByTypeAndCode(reqDTO);
								committees.setMemberRole(resDTO
										.getPmParameter());
							}
						}
						if (committees.getMemberRole() != null) {
							committees.setMemberRoleId(committees
									.getMemberRole().getParameterId());
						}
						// activity.setScCommitteeMember(committees);
						// setUserDetails(activity);
						
						//20190625@ for bug 25986/25988/26017 - Start
						memberClone = (ScCommitteeMember) committees.cloneEntity();
						memberClone.setCommitteeMemberId(null);
						localList.add(memberClone);
						//20190625@ for bug 25986/25988/26017 - End
					}
				}
			}

		}
		validateMember(this.getManagedBean().getQt(), localList, this.getManagedBean().getCommitteeType());
		committeeMemberList.addAll(localList);
		Collections.sort(committeeMemberList);
		this.getManagedBean().setCommitteeMemberActivity(committeeMemberList);
		this.getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().execute("screen2.hide();");

	}

	/**
	 * Gets the user so.
	 * 
	 * @return the user so
	 */
	private IUserSO getUserSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
	}

	/**
	 * Gets the parameter so.
	 * 
	 * @return the parameter so
	 */
	private IParameterSO getParameterSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}

	/**
	 * Gets the org profile so.
	 * 
	 * @return the org profile so
	 */
	private IOrgProfileSO getOrgProfileSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
	}

	/**
	 * Gets the status so.
	 * 
	 * @return the status so
	 */
	private IStatusSO getStatusSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
	}

	/**
	 * The Class clonePagingDataModel.to retrieve all the existing
	 * quotation/Tenders in clone popup.
	 */
	private class clonePagingDataModel extends PagingDataModel<ScQt> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new clone paging data model.
		 */
		public clonePagingDataModel() {

			super("clonesearchCommitteid", false);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		public ISearchResultBean<ScQt> pageChangeListener(
				ISearchBean<ScQt> searchBean) {
			IQTDetailSO detailSO;
			try {
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();

				String qtTitle = getManagedBean().getCloneQtnTenderSearchBean()
						.getQtTitle();
				if (!StringUtils.isBlank(qtTitle)) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(QTConstants.QT_TITILE,
									SearchOperator.OPERATOR_LIKE.value(),
									qtTitle, true));
				}

				String qtNumber = getManagedBean()
						.getCloneQtnTenderSearchBean().getQtNo();
				if (null == qtNumber || "".equalsIgnoreCase(qtNumber)) {
					qtNumber = "QT";
				}
				if (!StringUtils.isBlank(qtNumber)) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(QTConstants.QT_NUMBER,
									SearchOperator.OPERATOR_LIKE.value(),
									qtNumber, true));
				}

				Date fromCreatedDate = getManagedBean()
						.getCloneQtnTenderSearchBean().getFromCreatedDate();
				Date toCreatedDate = getManagedBean()
						.getCloneQtnTenderSearchBean().getToCreatedDate();
				if (null != fromCreatedDate || toCreatedDate != null) {

					Calendar c1 = Calendar.getInstance();
					c1.setTime(fromCreatedDate);
					Calendar fromDate = Calendar.getInstance();
					fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
							c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
					fromDate.set(Calendar.MILLISECOND, 0);
					fromCreatedDate = fromDate.getTime();
					if (toCreatedDate != null) {
						Calendar c2 = Calendar.getInstance();
						c2.setTime(toCreatedDate);
						Calendar toDate = Calendar.getInstance();
						toDate.set(c2.get(Calendar.YEAR),
								c2.get(Calendar.MONTH),
								c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
						toDate.set(Calendar.MILLISECOND, 0);
						toCreatedDate = toDate.getTime();
					}
					SearchFilterBean bean = new SearchFilterBean();
					bean.setSearchField(QTConstants.CREATED_DATE);
					bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN
							.value());
					SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
							fromCreatedDate, toCreatedDate);
					/*
					 * bean.setSearchValue(betweenVal);
					 * searchFilterBean.add(bean); SearchFilterBean bean = new
					 * SearchFilterBean();
					 * bean.setSearchField(QTConstants.CREATED_DATE);
					 * bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN
					 * .value());
					 * 
					 * SearchBetweenValue<Date> betweenVal = new
					 * SearchBetweenValue<Date>( fromCreatedDate,
					 * toCreatedDate);
					 */

					bean.setSearchValue(betweenVal);
					searchBean.getDataFilterBeans().add(bean);
					// searchFilterBean.add(bean);
				}

				Date fromPublishDate = getManagedBean()
						.getCloneQtnTenderSearchBean().getFromPublishDate();
				Date toPublishDate = getManagedBean()
						.getCloneQtnTenderSearchBean().getToPublishDate();
				if (null != fromPublishDate || null != toPublishDate) {
					Calendar c1 = Calendar.getInstance();
					c1.setTime(fromPublishDate);
					Calendar fromDate = Calendar.getInstance();
					fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
							c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
					fromDate.set(Calendar.MILLISECOND, 0);
					fromPublishDate = fromDate.getTime();
					if (toPublishDate != null) {
						Calendar c2 = Calendar.getInstance();
						c2.setTime(toPublishDate);
						Calendar toDate = Calendar.getInstance();
						toDate.set(c2.get(Calendar.YEAR),
								c2.get(Calendar.MONTH),
								c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
						toDate.set(Calendar.MILLISECOND, 0);
						toPublishDate = toDate.getTime();
					}
					SearchFilterBean bean = new SearchFilterBean();
					bean.setSearchField(QTConstants.PUBLISH_DATE);
					bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN
							.value());
					SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
							fromPublishDate, toPublishDate);
					bean.setSearchValue(betweenVal);
					searchBean.getDataFilterBeans().add(bean);
				}

				Date fromClosingDate = getManagedBean()
						.getCloneQtnTenderSearchBean().getFromClosingDate();
				Date toClosingDate = getManagedBean()
						.getCloneQtnTenderSearchBean().getToClosingDate();
				if (null != fromClosingDate || null != toClosingDate) {
					Calendar c1 = Calendar.getInstance();
					c1.setTime(fromClosingDate);
					Calendar fromDate = Calendar.getInstance();
					fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
							c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
					fromDate.set(Calendar.MILLISECOND, 0);
					fromClosingDate = fromDate.getTime();
					if (toClosingDate != null) {
						Calendar c2 = Calendar.getInstance();
						c2.setTime(toClosingDate);
						Calendar toDate = Calendar.getInstance();
						toDate.set(c2.get(Calendar.YEAR),
								c2.get(Calendar.MONTH),
								c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
						toDate.set(Calendar.MILLISECOND, 0);
						toClosingDate = toDate.getTime();
					}
					SearchFilterBean bean = new SearchFilterBean();
					bean.setSearchField(QTConstants.CLOSED_DATE);
					bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN
							.value());
					SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
							fromClosingDate, toClosingDate);
					bean.setSearchValue(betweenVal);
					searchBean.getDataFilterBeans().add(bean);
				}

				String procurementMode = getManagedBean()
						.getCloneQtnTenderSearchBean().getProcurementMode();
				if (!StringUtils.isBlank(procurementMode)) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"procurementMode.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									procurementMode, true));

				} else {
					// Filter By - procurementMode
					List<String> procurementModes = new ArrayList<String>();
					procurementModes.add(ParameterConst.PROCUREMENT_MODE_QUOTATION);
					procurementModes.add(ParameterConst.PROCUREMENT_MODE_TENDER);
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"procurementMode.parameterCode",
									SearchOperator.OPERATOR_IN.value(), 
									procurementModes));
				}

				String status = getManagedBean().getCloneQtnTenderSearchBean()
						.getQtStatus();
				if (!StringUtils.isBlank(status)) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"scQtStatus.pmStatus.statusId",
									SearchOperator.OPERATOR_EQUALS.value(),
									status));
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("scQtStatus.current",
									SearchOperator.OPERATOR_EQUALS.value(),
									true));

				}
				/*
				 * Long ministryId = retriveMinistryIdByPTJ(); if (null !=
				 * ministryId) { String parentString = OrgProfileUtil
				 * .getSearchBeanParentString(OrganizationType.MINISTRY
				 * .value()); searchBean.getDataFilterBeans().add( new
				 * SearchFilterBean( "pmOrgProfile." + parentString,
				 * SearchOperator.OPERATOR_EQUALS.value(), ministryId)); }
				 */

				/*
				 * if (null != getManagedBean().getQt().getPmOrgProfile()
				 * .getParentOrgProfile()) { searchBean .getDataFilterBeans()
				 * .add(new SearchFilterBean(
				 * "pmOrgProfile.parentOrgProfile.orgProfileId",
				 * SearchOperator.OPERATOR_EQUALS.value(),
				 * getManagedBean().getQt().getPmOrgProfile()
				 * .getParentOrgProfile() .getOrgProfileId())); }
				 */

				if (null != getManagedBean().getQt().getPmOrgProfile()) {
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									"pmOrgProfile."
											+ OrgProfileUtil
													.getSearchBeanParentString(OrganizationType.MINISTRY
															.value()),
									SearchOperator.OPERATOR_EQUALS.value(),

									retrieveMinistryOrgProfile(
											getManagedBean().getQt()
													.getPmOrgProfile()
													.getOrgProfileId())
											.getOrgProfileId()

							));
				}

				reqDTO.setQtSearchBean(searchBean);

				QuationTenderResDTO dto = null;
			

				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				dto = detailSO
						.retriveQuationTenderDetailsBySearchCriteria(reqDTO);

				if (null != dto && dto.isSuccessful()) {

					return dto.getQtResultBean();
				}
			} finally {
				detailSO=null;
			}

			return null;
		}
	}

	/**
	 * Chnage member role.Event handle to change member (Secretary/Member) in
	 * committee members list.
	 * 
	 * @param index
	 *            (Mandatory)
	 * @param committeeActivity
	 *            (Mandatory)
	 */
	public void chnageMemberRole(int index,
			List<ScCommitteeMember> committeeActivity) {
		ScCommitteeMember activity = committeeActivity.get(index);

		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.COMMITTEE_ROLE);
		if (null != activity.getMemberRoleId()) {
			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(activity.getMemberRoleId())) {

					activity.setMemberRole(paramDesc.getPmParameter());
					break;
				}
			}
		}
		this.getManagedBean().getCommitteeMemberActivity().remove(index);
		this.getManagedBean().getCommitteeMemberActivity().add(index, activity);

	}

	/**
	 * Retrieve parameter desc by type.
	 * 
	 * @param type
	 *            the type
	 * @return the list
	 */
	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	/**
	 * The Class existingUserPagingDataModel.to retrieve all the existing
	 * committee members information in add popup.
	 */
	private class ExistingUserPagingDataModel extends PagingDataModel<PmUser> {// 12471

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;
		// 12471 - start
		private UserCommitteeSearchBean userCommitteeSearchBean;
		
		public ExistingUserPagingDataModel(String componentId, UserCommitteeSearchBean userCommitteeSearchBean) {
			super(componentId, false);
			this.userCommitteeSearchBean = userCommitteeSearchBean;
			getManagedBean().setCommitteeSearchBean(userCommitteeSearchBean);
		} 
		// 12471 - end
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmUser> pageChangeListener(
				ISearchBean<PmUser> searchBean) {
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();
				
				//12471 start
				if (null != userCommitteeSearchBean) {
					populateUserSearchCriteria(userCommitteeSearchBean, searchBean);
					populateOrganizationSearchCriteria(userCommitteeSearchBean, searchBean);
				}
				//12471 end
				committeReqDTO.setUserSearchBean(searchBean);
				committeReqDTO.setPtjMinistryAdmin(Boolean.TRUE);
				return committeSO.retrieveUserListByCommType(committeReqDTO)
						.getUserResultBean();
			} catch (Exception e) {
				ScQt scQt = getManagedBean().getQt();
				String qtNo = null != scQt ? scQt.getQtNo() : "";
				SLAUtils.getInstance().logSLAError(
						SLALogConstant.QT_MODULE,
						SLALogConstant.QT_EVALUATION_MEETING_BACKINGBEAN, qtNo,
						SLALogConstant.QT_USER_COMMITTEE_PAGING_DATA_MODEL.concat(SLALogConstant.QT_SLA_DOT_SEPARATOR)
								.concat(SLALogConstant.QT_PAGE_CHANGE_LISTENER), getUserIdSessionFromLoginId(), e);
			}
			return null;
		}
		//12471 start
		private void populateUserSearchCriteria(UserCommitteeSearchBean userCommitteeSearchBean,
				ISearchBean<PmUser> searchBean) {
			String name = userCommitteeSearchBean.getName();
			if (StringUtils.isNotBlank(name)) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.USER_NAME, SearchOperator.OPERATOR_LIKE.value(), name, true));
			}
			String identificationNo = userCommitteeSearchBean.getIdentificationNo();
			if (StringUtils.isNotBlank(identificationNo)) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.IDENTIFICATION_NO, SearchOperator.OPERATOR_LIKE.value(),
								identificationNo, true));
			}
			String identificationType = userCommitteeSearchBean.getIdentificationType();
			if (StringUtils.isNotBlank(identificationType)) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTSearchBeanConst.SEARCH_IDENTIFICATIONCODE_PARAMETERCODE,
								SearchOperator.OPERATOR_EQUALS.value(), identificationType, true));
			}
		}
		
		private void populateOrganizationSearchCriteria(UserCommitteeSearchBean userCommitteeSearchBean,
				ISearchBean<PmUser> searchBean) {
			String orgCategory = null == userCommitteeSearchBean.getOrgCat() ? "" : userCommitteeSearchBean.getOrgCat();
			if (ParameterConst.ORG_CATEGORY_PTJ.equalsIgnoreCase(orgCategory)) {
				String ministry = userCommitteeSearchBean.getMinistry();
				if (StringUtils.isNotBlank(ministry)) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(QTSearchBeanConst.SEARCH_PMUSERORGS_PMORGPROFILE
									.concat(QTSearchBeanConst.SEARCH_DOT_SEPARATOR)
									+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
									SearchOperator.OPERATOR_EQUALS.value(), ministry, true));
				}
				String ptjGroup = userCommitteeSearchBean.getPtjGroup();
				if (StringUtils.isNotBlank(ptjGroup)) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(QTSearchBeanConst.SEARCH_PMUSERORGS_PMORGPROFILE
									.concat(QTSearchBeanConst.SEARCH_DOT_SEPARATOR)
									+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()),
									SearchOperator.OPERATOR_EQUALS.value(), ptjGroup, true));
				}
				String ptj = userCommitteeSearchBean.getPtj();
				if (StringUtils.isNotBlank(ptj)) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(QTSearchBeanConst.SEARCH_PMUSERORGS_PMORGPROFILE
									.concat(QTSearchBeanConst.SEARCH_DOT_SEPARATOR)
									+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.DIVISION.value()),
									SearchOperator.OPERATOR_EQUALS.value(), ptj, true));
				}
			} else if (ParameterConst.ORG_CATEGORY_OTHERS.equalsIgnoreCase(orgCategory)) {
				String orgType = userCommitteeSearchBean.getOrgType();
				if (StringUtils.isNotBlank(orgType)) {
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									QTSearchBeanConst.SEARCH_PMUSERORGS_PMORGPROFILE_ORGTYPECODE_PARAMETERID
											+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.OTHERS.value()),
									SearchOperator.OPERATOR_EQUALS.value(), orgType, true));
				}
				String orgName = userCommitteeSearchBean.getOrgName();
				if (StringUtils.isNotBlank(orgName)) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(QTSearchBeanConst.SEARCH_PMUSERORGS_PMORGPROFILE_ORGPROFILEID
									+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.SUPPORT.value()),
									SearchOperator.OPERATOR_EQUALS.value(), orgName, true));
				}
			}
		}
		//12471 end
	}

	/**
	 * Submit metting details.Event handle click on submit button in meeting
	 * details.
	 * 
	 * @param outCome
	 *            (Mandatory)
	 * @return the string(ACKNOWLEDGE_PAGE)
	 */
	public String submitMettingDetails(String outCome) {
		IQTMeetingSO meetingso;
		try {

			FacesScopes facesScopes = new FacesScopes();

			 meetingso = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTMeetingSO.class);
			MeetingReqDTO reqDTO = prepareMeetingDtls();
			checkCommitteeFormation(reqDTO);
			// 25037 - start
			boolean modified = isCommitteeModified(this.getManagedBean().getCommitteeMemberActivity())
					&& this.getManagedBean().isValidFor25037();
			reqDTO.setCommitteeModified(modified);
			// 25037 - end
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setTaskName(this.getManagedBean().getTaskName());// 25037
			reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			reqDTO.setPayLoad(this.getManagedBean().getPayLoad());
			reqDTO.setPmUser(facesScopes.getUserSessionBean());
			reqDTO.setOutcome(outCome);
			meetingso.updateBPMAction(reqDTO);// 25037
			
			resolveAcknowledgement(this.getManagedBean().getQt(), reqDTO); // 25037
			// Code to renew the session Object..
			renewSesionObject();
		
		} finally {
			meetingso=null;
		}

		return CommonUIConst.ACKNOWLEDGE_PAGE;

	}

	/**
	 * Prerender view.click on task list navigate to coordinate meeting details
	 * Screens for(EC/OC/TEC/FEC).
	 */
	public void prerenderView() {
		// 25037 - start
		Object obj = getBpmTaskDetails(Object.class, null);
		Long qtId = null;
		String committeeType = null;
		String taskName = getManagedBean().getTaskName();

		if (obj instanceof SCCommitteeDataType) {
			qtId = ((SCCommitteeDataType) obj).getQtId();
			committeeType = ((SCCommitteeDataType) obj).getCommitteeType();
		} else if (obj instanceof SCEvaluationDataType) {
			qtId = ((SCEvaluationDataType) obj).getQtId();
			committeeType = ((SCEvaluationDataType) obj).getCommitteeType();
		}
		this.getManagedBean().setPayLoad(obj);
		// 25037 - end

		// FacesScopes facesScopes = new FacesScopes();
		FacesContext fc = FacesContext.getCurrentInstance();
		IQTMeetingSO meetingso = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTMeetingSO.class);
		MeetingReqDTO activityReqDTo = new MeetingReqDTO();
		// 25037 - start
		activityReqDTo.setQtId(qtId);
		activityReqDTo.setCommitteType(committeeType);
		// 25037 - end
		MeetingResDTO activityResDTo = meetingso
				.retriveCommitteActivityDtls(activityReqDTo);
		if (null != activityResDTo
				&& !activityResDTo.getCommitteMembers().isEmpty()) {
			MeetingReqDTO deleteActivityDao = new MeetingReqDTO();
			List<ScQtCommitteeActivity> deleteActivityList = new ArrayList<ScQtCommitteeActivity>();
			for (ScQtCommitteeActivity activity : activityResDTo
					.getCommitteMembers()) {
				if (!activity.isBrandNew()) {
					// activity.setScCommitteeMember(null);
					activity.setDeleted(true);
					deleteActivityList.add(activity);
				}
			}
			deleteActivityDao
					.setDeletedCommitteeActivityMembersList(deleteActivityList);
			meetingso.deleteCommitteeActivityDtls(deleteActivityDao);
		}
		// 25037 - start
		retriveMeetingDetails(qtId, committeeType);

		if (Arrays.asList(SCProcessNameConst.EVAL_COMM_PREPARATION_GROUP).contains(taskName)
				|| Arrays.asList(SCProcessNameConst.EVAL_COMM_FINALIZE_GROUP).contains(taskName)) {
		// 25037 - end
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/confirmMeeting/confirmMeetingView.xhtml");
		} else {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/evaluationMain.xhtml");
		}
	}

	/**
	 * Gets the search quotatio tender.
	 * 
	 * @return the search quotatio tender
	 */
	public IPagingDataModel<ScQt> getSearchQuotatioTender() {
		return searchQuotatioTender;
	}

	/**
	 * Sets the search quotatio tender.
	 * 
	 * @param searchQuotatioTender
	 *            the new search quotatio tender
	 */
	public void setSearchQuotatioTender(
			IPagingDataModel<ScQt> searchQuotatioTender) {
		this.searchQuotatioTender = searchQuotatioTender;
	}

	/**
	 * this method is used to check two meeting dates or equals or not.
	 * 
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
			meetingList.add(scqtMeeting);
		}

	}

	
	/**
	 * delete meeting details from existing  meeting list.
	 * 
	 * 
	 */
	public void deleteMeetingDtls() {
		markDeleteSelectedRowsAction(this.getManagedBean().getScQtMeetings(),
				this.getManagedBean().getMeetingRemoveList());

	}

	
	/**
	 * delete committee members from existing committee members list.
	 * 
	 * 
	 */
	public void deleteCommitteemembersDtls() {
		markDeleteSelectedRowsAction(this.getManagedBean()
				.getCommitteeMemberActivity(), this.getManagedBean()
				.getRemoveCommitteeList());

	}

	
	
	/**
	 * retrive existing or otherCommittee information from db.
	 * 
	 * 
	 */
	private void retriveExistOtherCommittee() {
		ICommitteSO committeSO = null;
		CommitteResDTO resDTO = null;
		try {
			if (null == this.getManagedBean().getOtherCommitteeMembersList()
					|| !this.getManagedBean().getOtherCommitteeMembersList()
							.isEmpty()) {
				this.getManagedBean().setOtherCommitteeMembersList(
						new ArrayList<ScCommitteeMember>());
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					this.getManagedBean().setCheckOtherCommitteeType(
							ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE);
					this.getManagedBean()
							.setCheckOtherCommitteeTypes(
									ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE);
				} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					this.getManagedBean()
							.setCheckOtherCommitteeType(
									ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE);
					this.getManagedBean()
							.setCheckOtherCommitteeTypes(
									ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE);
				} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(this.getManagedBean()
								.getCommitteeType())) {
					this.getManagedBean()
							.setCheckOtherCommitteeType(
									ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE);
					this.getManagedBean().setCheckOtherCommitteeTypes(
							ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE);
				}

				CommitteReqDTO type1reqDTO = new CommitteReqDTO();
				type1reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
				type1reqDTO.setCommitteeType(this.getManagedBean()
						.getCheckOtherCommitteeType());
				resDTO = committeSO
						.retrieveCommitteMembersByQtIdAndCommType(type1reqDTO);
				if (null != resDTO
						&& null != resDTO.getCommMemberResultBean()
						&& null != resDTO.getCommMemberResultBean()
								.getResultList()
						&& !resDTO.getCommMemberResultBean().getResultList()
								.isEmpty()) {
					this.getManagedBean()
							.getOtherCommitteeMembersList()
							.addAll(resDTO.getCommMemberResultBean()
									.getResultList());
				}

				CommitteReqDTO type2reqDTO = new CommitteReqDTO();
				type2reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
				type2reqDTO.setCommitteeType(this.getManagedBean()
						.getCheckOtherCommitteeTypes());
				resDTO = committeSO
						.retrieveCommitteMembersByQtIdAndCommType(type2reqDTO);
				if (null != resDTO
						&& null != resDTO.getCommMemberResultBean()
						&& null != resDTO.getCommMemberResultBean()
								.getResultList()
						&& !resDTO.getCommMemberResultBean().getResultList()
								.isEmpty()) {
					this.getManagedBean()
							.getOtherCommitteeMembersList()
							.addAll(resDTO.getCommMemberResultBean()
									.getResultList());
				}
			}

		} finally {
			committeSO=null;
		}

	}

	// check existing committeemebers in otherCommittee in TEC,OC,FEC
	// Commented as the code for committee validation already exists in CommitteValidator class
//	private void checkCommitteeMembersExistOtherCommittees(
//			List<ScCommitteeMember> otherCommitteemembers,
//			List<ScCommitteeMember> existingCommitteemembers) {
//		if (null != otherCommitteemembers && !otherCommitteemembers.isEmpty()
//				&& null != existingCommitteemembers
//				&& !existingCommitteemembers.isEmpty()) {
//			for (ScCommitteeMember currentCommitteeMember : existingCommitteemembers) {
//				for (ScCommitteeMember otherCommitteeMember : otherCommitteemembers) {
//					if (otherCommitteeMember.getIcPassport().equalsIgnoreCase(
//							currentCommitteeMember.getIcPassport())) {
//						throw new EpException(
//								MessageConst.ERROR_760272,
//								new String[] {
//										currentCommitteeMember.getMemberName(),
//										this.getManagedBean()
//												.getCheckOtherCommitteeType(),
//										this.getManagedBean()
//												.getCheckOtherCommitteeTypes() });
//					}
//
//				}
//
//			}
//		}
//
//	}

	/**
	 * user click on cancel button in popup window.
	 */
	public void closeListener() {
		
		this.getManagedBean().setNameRequired(false);
		this.getManagedBean().setAutoUpdate(Boolean.TRUE);
	}

	
	
	/**
	 * Gets the qt commite dtls by qt id n type.
	 * 
	 * @param commType
	 *            the comm type
	 * @param qtId
	 *            the qt id
	 * @return the qt commite dtls by qt id n type
	 */
	private ScQtCommittee getQtCommiteDtlsByQtIdNType(String commType, Long qtId) {
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType(commType);
		reqDTO.setQtId(qtId);
		CommitteResDTO resDTO = null;
		ScQtCommittee qtCommitte = null;
		ICommitteSO committeSO;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
			qtCommitte = resDTO.getScQtCommittee();
			
		} finally {
			committeSO = null;
		}
		return qtCommitte;
	}
	
	private void validateMember(ScQt scQt,
			List<ScCommitteeMember> toBeAddedList, String committeeType) {

	log.debug("validateMembercommitteeType"+committeeType);
		List<ScQtCommittee> qtCommitteList = null;
		ScQtCommittee specCommitte = getQtCommiteDtlsByQtIdNType(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE,
				scQt.getQtId());
		qtCommitteList=new ArrayList<ScQtCommittee>();
		if(null!=specCommitte){
			qtCommitteList.add(specCommitte);	
		}
		qtCommitteList.add(this.getManagedBean().getScQtCommittee());
		if (ParameterConst.EVALUATION_TYPES_TWO_TIER.equalsIgnoreCase(this
				.getManagedBean().getQt().getEvaluationType())) {
			if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(committeeType)) {
				ScQtCommittee openCommitte = getQtCommiteDtlsByQtIdNType(ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE,
						scQt.getQtId());
				ScQtCommittee fecCommitte = getQtCommiteDtlsByQtIdNType(ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE,
						scQt.getQtId());
				if(null!=openCommitte){
					qtCommitteList.add(openCommitte);	
				}
				if(null!=fecCommitte){
					qtCommitteList.add(fecCommitte);	
				}
				
			}
			
			else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
					.equalsIgnoreCase(committeeType)) {
				ScQtCommittee tecCommitte = getQtCommiteDtlsByQtIdNType(ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE,
						scQt.getQtId());
				ScQtCommittee fecCommitte = getQtCommiteDtlsByQtIdNType(ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE,
						scQt.getQtId());
				if(null!=tecCommitte){
					qtCommitteList.add(tecCommitte);	
				}
				if(null!=fecCommitte){
					qtCommitteList.add(fecCommitte);	
				}
			}
			
			else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
					.equalsIgnoreCase(committeeType)) {
				
				ScQtCommittee tecCommitte = getQtCommiteDtlsByQtIdNType(ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE,
						scQt.getQtId());
				ScQtCommittee openCommitte = getQtCommiteDtlsByQtIdNType(ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE,
						scQt.getQtId());
				if(null!=tecCommitte){
					qtCommitteList.add(tecCommitte);	
				}
				if(null!=openCommitte){
					qtCommitteList.add(openCommitte);	
				}
			}
			
		}
		
		ICommitteSO committeSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,ICommitteSO.class);
		try{				
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setScQt(scQt);
		FacesScopes scope = new FacesScopes();
		committeReqDTO.setPmUser(scope.getUserSessionBean());
		committeReqDTO.setUserLocale(scope.getUserLocale());
		committeReqDTO.setQtCommittee(qtCommitteList);
		committeReqDTO.setCommitteeMembers(toBeAddedList);
		committeReqDTO.setCommitteeType(committeeType);
		
		committeSO.validateEvaluationMethodMembers(committeReqDTO);
		}finally{
		committeSO =null;
		qtCommitteList=null;
		}
	}
	
	/*
	 * private Long retriveMinistryIdByPTJ() { IOrgProfileSO orgProfileSO =
	 * null; Long ministryId = null; try { orgProfileSO =
	 * BusinessDelegate.getInstance().getServiceObject(
	 * ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
	 * OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
	 * reqDTO.setOrgProfileId(this.getManagedBean().getQt()
	 * .getPmOrgProfile().getOrgProfileId()); OrgProfileResDTO resDTO =
	 * orgProfileSO .retrieveMinistryByOrgProfileId(reqDTO); if (null != resDTO
	 * && null != resDTO.getPmOrgProfile()) { ministryId =
	 * resDTO.getPmOrgProfile().getOrgProfileId(); } } finally {
	 * 
	 * } return ministryId; }
	 */
	
	// 25037 - start
	/**
	 * Navigate between main screen and update committee member screen. Populate
	 * data based on action type provided.
	 * 
	 * @param action
	 */
	public void navigateToUpdateMember(String action) {
		FacesContext fc = FacesContext.getCurrentInstance();
		EvaluationBackingBean evalBB = this.getManagedBean().getEvalBB();
		String mainPage = evalBB != null ? "/xhtml/sc/evaluation/evaluationMain.xhtml?faces-redirect=true"
				: "/xhtml/sc/confirmMeeting/confirmMeetingView.xhtml?faces-redirect=true";
		if (CommonConstants.ACTION_BACK.equalsIgnoreCase(action)) {
			restoreUnconfirmChanges();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, mainPage);
		} else if (CommonConstants.ACTION_UPDATE.equalsIgnoreCase(action)) {
			updateAction();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "/xhtml/sc/confirmMeeting/memberUpdate.xhtml");
		} else if (CommonConstants.ACTION_CONFIRM.equalsIgnoreCase(action)) {
			this.getManagedBean().setCommitteeModified(true);
			if (evalBB != null) {
				confirmAction();
			}
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, mainPage);
		}
	}
	
	private void updateAction() {
		this.getManagedBean().setTempCommitteeMembers(new ArrayList<ScCommitteeMember>());
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getCommitteeMemberActivity())) {
			for (ScCommitteeMember member : this.getManagedBean().getCommitteeMemberActivity()) {
				member = SerializationUtils.clone(member);
				this.getManagedBean().getTempCommitteeMembers().add(member);
			}
		}
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getScQtApprovalRemarks())) {
			this.getManagedBean().setTempScQtApprovalRemark(
					SerializationUtils.clone(this.getManagedBean().getScQtApprovalRemarks().get(0)));
		}
	}
	
	private void confirmAction() {
		EvaluationBackingBean evalBB = this.getManagedBean().getEvalBB();
		evalBB.getManagedBean().setCommitteModified(true);
		List<ScQtCommitteeActivity> activityRemoveList = new ArrayList<>();
		List<ScQtCommitteeActivity> activityList = evalBB.getManagedBean().getCommitteeMemberList();
		Map<Object, Object> activityMap = CustomUtils.listToMap(activityList, "getScCommitteeMember.getIcPassport");

		List<ScCommitteeMember> evalMembers = evalBB.getManagedBean().getCommitteeMemberActivity();
		Map<Object, Object> evalMemberMap = CustomUtils.listToMap(evalMembers, QTSearchBeanConst.SEARCH_GETICPASSPORT);

		if (null == evalBB.getManagedBean().getRemoveCommitteeList()) {
			evalBB.getManagedBean().setRemoveCommitteeList(new ArrayList<ScCommitteeMember>());
		}
		List<ScCommitteeMember> removeCommitteeList = evalBB.getManagedBean().getRemoveCommitteeList();
		Map<Object, Object> removeCommitteeMap = CustomUtils.listToMap(removeCommitteeList,
				QTSearchBeanConst.SEARCH_GETICPASSPORT);

		for (ScCommitteeMember m : this.getManagedBean().getRemoveCommitteeList()) {
			ScQtCommitteeActivity act = (ScQtCommitteeActivity) activityMap.get(m.getIcPassport());
			if (act != null) {
				activityRemoveList.add(act);
			}

			ScCommitteeMember evalMember = (ScCommitteeMember) evalMemberMap.get(m.getIcPassport());
			if (evalMember != null) {
				evalMember.setDeleted(true);
				evalBB.getManagedBean().getRemoveCommitteeList().add(evalMember);
			}
		}

		for (ScCommitteeMember m : this.getManagedBean().getCommitteeMemberActivity()) {
			ScQtCommitteeActivity act = (ScQtCommitteeActivity) activityMap.get(m.getIcPassport());
			if (act == null) {
				ScQtCommitteeActivity newAct = new ScQtCommitteeActivity();
				newAct.setScCommitteeMember(m);
				newAct.setScQtCommittee(evalBB.getManagedBean().getScQtCommittee());
				activityList.add(newAct);
			} else {
				ScCommitteeMember em = act.getScCommitteeMember();
				em.setMemberRole(m.getMemberRole());
			}
			ScCommitteeMember removedMember = (ScCommitteeMember) removeCommitteeMap.get(m.getIcPassport());
			if (removedMember != null) {
				removeCommitteeList.remove(removedMember);
			}
		}
		activityList.removeAll(activityRemoveList);
		evalMembers.removeAll(evalBB.getManagedBean().getRemoveCommitteeList());

		if (CollectionUtils.isNotEmpty(this.getManagedBean().getScQtApprovalRemarks())) {
			ScQtApprovalRemark remark = this.getManagedBean().getScQtApprovalRemarks().get(0);
			if (StringUtils.isNotEmpty(remark.getRemark())) {
				evalBB.getManagedBean().setScQtApprovalRemark(remark);
			}
		}
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
		Paginator selectedHistoryMember = new Paginator(list);
		this.getManagedBean().setMemberHistoryPaginator(selectedHistoryMember);
	}
	
	private ICommitteSO getCommitteeSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
	}
    
    private void restoreUnconfirmChanges(){
    	if(this.getManagedBean().getCommitteeMemberActivity() == null){
    		this.getManagedBean().setCommitteeMemberActivity(new ArrayList<ScCommitteeMember>());
    	}
    	if(this.getManagedBean().getRemoveCommitteeList() == null){
    		this.getManagedBean().setRemoveCommitteeList(new ArrayList<ScCommitteeMember>());
    	}
    	if(this.getManagedBean().getTempCommitteeMembers() == null){
    		this.getManagedBean().setTempCommitteeMembers(new ArrayList<ScCommitteeMember>());
    	}
    	
    	this.getManagedBean().getCommitteeMemberActivity().clear();
		Map<Object, Object> map = CustomUtils.listToMap(this.getManagedBean().getRemoveCommitteeList(), "getIcPassport");
		List<ScCommitteeMember> remove = new ArrayList<>();
    	for(ScCommitteeMember m: this.getManagedBean().getTempCommitteeMembers()){
			if(map.get(m.getIcPassport())!=null){
				m.setDeleted(false);
				m.setSelected(false);
				remove.add((ScCommitteeMember) map.get(m.getIcPassport()));
			}
			this.getManagedBean().getCommitteeMemberActivity().add(m);
		}
    	if(CollectionUtils.isNotEmpty(this.getManagedBean().getRemoveCommitteeList())){
    		this.getManagedBean().getRemoveCommitteeList().removeAll(remove);
    	} 
		// 25037 - start - note 568
		if (this.getManagedBean().getTempScQtApprovalRemark() == null)
			return;
		String remark = this.getManagedBean().getTempScQtApprovalRemark().getRemark();
		remark = StringUtils.isNotEmpty(remark) ? remark : "";
		this.getManagedBean().getScQtApprovalRemarks().get(0).setRemark(remark);
		// 25037 - end - note 568
    }

	private void prepareCommitteeHistory(ScQtCommittee qtCommittee) {
		List<ScCommitteeMemberHistory> histories = this.getCommitteeSO().getMergedHistory(qtCommittee, false);

		if (CollectionUtils.isNotEmpty(histories)) {
			ScCommitteeMemberHistory history = histories.get(0);
			@SuppressWarnings("unchecked")
			List<QtCommitteeMemberBean> prevCommitteeList = (List<QtCommitteeMemberBean>) (Object) history
					.deserializeList(history.getCommitteeMembers());
			this.getManagedBean().setPrevCommitteeMembers(prevCommitteeList);
		}

		this.getManagedBean().setCurrentCommitteeType(
				this.getParameterDesc(qtCommittee.getScCommittee().getCommitteeType()));
		this.getManagedBean().setHistoryPaginator(new Paginator(histories));
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		reqDTO.setQtId(qtCommittee.getScQt().getQtId());
		reqDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT);
		ScQtApproval approval = this.getQTDetailSO().retrieveApprovalDtlsByQtIdNType(reqDTO).getQtApproval();

		List<ScQtApprovalRemark> remarkList = approval.getScQtApprovalRemarks();
		if (CollectionUtils.isEmpty(remarkList)) {
			remarkList = new ArrayList<>();
		}
		remarkList = initializeRemarks(qtCommittee, qtCommittee.getCurrentStatus(), remarkList,
				ScQtApprovalRemark.class);
		Collections.sort(remarkList, new Comparator<ScQtApprovalRemark>() {
			@Override
			public int compare(ScQtApprovalRemark o1, ScQtApprovalRemark o2) {
				return o2.getCreatedDate().compareTo(o1.getCreatedDate());
			}
		});
		this.getManagedBean().setScQtApprovalRemarks(remarkList);
		boolean isValidFor25037 = this.validateSupportedTask(this.getManagedBean().getQt().getQtNo(),
				QTConstants.ALLOW_COMMITTEE_REASSIGNMENT);
		this.getManagedBean().setValidFor25037(isValidFor25037);
	}
	
	/**
	 * Validate if changes in committee correct.
	 */
	public void validateCommitteeChanges() {
		MeetingReqDTO reqDTO = new MeetingReqDTO();
		ScQtCommittee qtCommittee = this.getManagedBean().getScQtCommittee();
		ScCommittee scCommittee = qtCommittee.getScCommittee();
		reqDTO.setScQtCommitte(qtCommittee);
		scCommittee.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
		scCommittee.getScCommitteeMembers().addAll(this.getManagedBean().getCommitteeMemberActivity());
		this.checkCommitteeFormation(reqDTO);

		if (isCommitteeModified(this.getManagedBean().getCommitteeMemberActivity())) {
			if (StringUtils.isEmpty(this.getManagedBean().getScQtApprovalRemarks().get(0).getRemark())) {
				throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
						"lbl.sc.remark", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
			}
		} else {
			String message = MessageUtils.getMessage(MessageConst.INFO_760506, null,
					MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale());
			throw new EpException(message);
		}
		RequestContext.getCurrentInstance().execute("confirmChangeMemberListPopup.show();");
	}

	/**
	 * Submit method when submitting from coordinate meeting screen. Determine
	 * if next process redirect to approval if committee is updated.
	 */
	public void submitMettingDetails() {
		String outcome = BpmOutcomeConst.BPM_SUBMIT;
		// 25037 - start - note 568
		EvaluationBackingBean evalBB = this.getManagedBean().getEvalBB();
		if (evalBB != null) {
			MeetingReqDTO meetingReqDTO = prepareMeetingDtls();
			checkCommitteeFormation(meetingReqDTO);
			boolean modified = isCommitteeModified(this.getManagedBean().getCommitteeMemberActivity())
					&& this.getManagedBean().isValidFor25037();
			if (!modified) {
				return;
			}
			evalBB.submitCommitteeUpdate();
		} else {
			submitMettingDetails(outcome);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
		}
		// 25037 - end - note 568
	}
	
	/**
	 * Determine the popup that should appear upon submission.
	 */
	public void resolveSubmitMeetingDialog() {
		if (isCommitteeModified(this.getManagedBean().getCommitteeMemberActivity()) && this.getManagedBean().isValidFor25037()) {
			RequestContext.getCurrentInstance().execute("confirmSubmitToApproverPopup.show();");
		} else {
			RequestContext.getCurrentInstance().execute("confirmSubmitMeetingPopup.show();");
		}
	}
	
	private void resolveAcknowledgement(ScQt qt, MeetingReqDTO reqDTO) {
		String taskName = reqDTO.getTaskName();
		String outcome = reqDTO.getOutcome();
		String message = null;

		boolean submitAndAllowCommitteeChanges = outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)
				&& this.getManagedBean().isValidFor25037();
		boolean taskAllowedForCommitteeChanges = Arrays.asList(SCProcessNameConst.EVAL_COMM_PREPARATION_GROUP)
				.contains(taskName) || Arrays.asList(SCProcessNameConst.EVAL_COMM_FINALIZE_GROUP).contains(taskName);
		if (submitAndAllowCommitteeChanges && taskAllowedForCommitteeChanges && reqDTO.isCommitteeModified()) {
			String committeeType = this.getManagedBean().getCommitteeType();
			committeeType = StringUtils.isEmpty(committeeType) ? "" : this.getParameterDesc(
					ParameterConst.COMMITTE_TYPE, committeeType);
			String procumentMode = this.getParameterDesc(qt.getProcurementMode());
			message = MessageUtils.getMessage(MessageConst.INFO_760504,
					new String[] { committeeType, procumentMode, qt.getQtNo() }, MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale());

		}
		if (StringUtils.isEmpty(message)) {
			message = MessageUtils.getMessage(getMessgeConstant("SUBMIT"), new String[] {
					getMessageCommon("lbl.sc.evaluation.meeting"), qt.getQtNo() }, MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale());
		}
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, message));
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
			previousListArr[i] = m.getIdentificationNo().concat(m.getMemberRole());
		}
		for (int i = 0; i < currentListArr.length; i++) {
			ScCommitteeMember m = currentList.get(i);
			currentListArr[i] = m.getIcPassport().concat(m.getMemberRole().getParameterCode());
		}
		Arrays.sort(previousListArr);
		Arrays.sort(currentListArr);
		return !StringUtils.join(previousListArr, ",").equalsIgnoreCase(StringUtils.join(currentListArr, ","));
	}

	//#25037-END
	
	/**
	 * 28895-35500 inserting audit trails for Committee Formation
	 * @param reqDTO
	 */
	private void prepareCommitteeAuditTrail() {
		IQTAuditTrailSO qtAuditTrailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAuditTrailSO.class);
		IAuditTrailSO auditTrailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.AUDIT, IAuditTrailSO.class);
		List<ScCommitteeMember> newCommitteeMembers = this.getManagedBean().getNewCommitteeAuditTrailList() != null
				? this.getManagedBean().getNewCommitteeAuditTrailList() : new ArrayList<ScCommitteeMember>();
		List<ScCommitteeMember> deletedCommitteeMembers = this.getManagedBean().getRemoveCommitteeList() != null
				? this.getManagedBean().getRemoveCommitteeList() : new ArrayList<ScCommitteeMember>();
		
		if ((CollectionUtils.isNotEmpty(newCommitteeMembers) || CollectionUtils.isNotEmpty(deletedCommitteeMembers)) &&
				auditTrailSO.isAuditConfig(QTAuditConstant.QT_CONFIG_ID)) {
			
			AuditTrailResDTO auditResDTO = new AuditTrailResDTO();

			auditResDTO.setDocTypeId(this.getManagedBean().getQt().getProcurementMode().getParameterId());
			auditResDTO.setDocId(this.getManagedBean().getQt().getQtId());
			auditResDTO.setDocNo(this.getManagedBean().getQt().getQtNo());
			auditResDTO.setCreatedBy(getUserIdSessionFromLoginId());
			auditResDTO.setStatusId(QTStatusConst.CMT_PENDING_APPROVAL);

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
	
	/** 28895 - 35500
	 * Method to insert  audit trails list for New/Delete Committee Members
	 * @param auditResDTO
	 * @param reqDTO
	 * @param committeeMembers
	 * @param type
	 * @param qtAuditTrailSO
	 * @return
	 */
	public List<AuditTrailReqDTO> insertCommitteeAuditTrail(AuditTrailResDTO auditResDTO,
			List<ScCommitteeMember> committeeMembers,String type,IQTAuditTrailSO qtAuditTrailSO) {
		List<AuditTrailReqDTO> auditReqList = new ArrayList<>();
		
		for (ScCommitteeMember committeeMember : committeeMembers) {
			auditResDTO.setActionType(type);
			if (this.getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE)) {
				auditReqList.addAll(populateCommitteeAuditTrail(auditResDTO, committeeMember,
						QTAuditConstant.AU_SECTION_SC, QTAuditConstant.AU_FIELD_SC_NAME,
						QTAuditConstant.AU_FIELD_SC_IDENTIFICATION, QTAuditConstant.AU_FIELD_SC_ROLE,qtAuditTrailSO));

			} else if (this.getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE)) {
				auditReqList.addAll( populateCommitteeAuditTrail(auditResDTO, committeeMember,
						QTAuditConstant.AU_SECTION_EC, QTAuditConstant.AU_FIELD_EC_NAME,
						QTAuditConstant.AU_FIELD_EC_IDENTIFICATION, QTAuditConstant.AU_FIELD_EC_ROLE,qtAuditTrailSO));

			} else if (this.getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE)) {
				auditReqList.addAll(populateCommitteeAuditTrail(auditResDTO, committeeMember,
						QTAuditConstant.AU_SECTION_TEC, QTAuditConstant.AU_FIELD_TEC_NAME,
						QTAuditConstant.AU_FIELD_TEC_IDENTIFICATION, QTAuditConstant.AU_FIELD_TEC_ROLE,qtAuditTrailSO));

			} else if (this.getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE)) {
				auditReqList.addAll(populateCommitteeAuditTrail(auditResDTO, committeeMember,
						QTAuditConstant.AU_SECTION_FEC, QTAuditConstant.AU_FIELD_FEC_NAME,
						QTAuditConstant.AU_FIELD_FEC_IDENTIFICATION, QTAuditConstant.AU_FIELD_FEC_ROLE,qtAuditTrailSO));

			} else if (this.getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE)) {
				auditReqList.addAll(populateCommitteeAuditTrail(auditResDTO, committeeMember,
						QTAuditConstant.AU_SECTION_OC, QTAuditConstant.AU_FIELD_OC_NAME,
						QTAuditConstant.AU_FIELD_OC_IDENTIFICATION, QTAuditConstant.AU_FIELD_OC_ROLE,qtAuditTrailSO));
			}						
		}
		return auditReqList;
	}
	
	 /** 28895 - 35500
	 * Method to populate audit trails data for update committee
	 * @param auditResDTO
	 * @param committeeMember
	 * @param groupSectionId
	 * @param typeName
	 * @param fieldIdentification
	 * @param fieldRole
	 * @param qtAuditTrailSO
	 * @return
	 */
	public List<AuditTrailReqDTO> populateCommitteeAuditTrail(AuditTrailResDTO auditResDTO,
			ScCommitteeMember committeeMember, Long groupSectionId, Long typeName, Long fieldIdentification,
			Long fieldRole, IQTAuditTrailSO qtAuditTrailSO) {
		List<AuditTrailReqDTO> auditReqList = new ArrayList<>();
		auditResDTO.setAuGroupSectionId(groupSectionId);

		auditResDTO.setQtFieldLabelId(typeName);
		if (auditResDTO.getActionType().equals(QTAuditConstant.AU_TYPE_CREATE)) {
			auditResDTO.setOldValue(null);
			auditResDTO.setNewValue(committeeMember.getMemberName());
		} else {
			auditResDTO.setNewValue(null);
			auditResDTO.setOldValue(committeeMember.getMemberName());
		}
		AuditTrailReqDTO auditReqDTOName = qtAuditTrailSO.populateAuditReqDTO(auditResDTO);
		auditReqList.add(auditReqDTOName);

		auditResDTO.setQtFieldLabelId(fieldIdentification);
		if (auditResDTO.getActionType().equals(QTAuditConstant.AU_TYPE_CREATE)) {
			auditResDTO.setNewValue(committeeMember.getPmUser().getIdentificationNo());
		} else {
			auditResDTO.setOldValue(committeeMember.getPmUser().getIdentificationNo());
		}
		AuditTrailReqDTO auditReqDTOIdentification = qtAuditTrailSO.populateAuditReqDTO(auditResDTO);
		auditReqList.add(auditReqDTOIdentification);

		auditResDTO.setQtFieldLabelId(fieldRole);
		if (auditResDTO.getActionType().equals(QTAuditConstant.AU_TYPE_CREATE)) {
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

		return auditReqList;
	}
	
	/** 
	 * 28895 - 35500
	 * Get PM_PARAMETER Description By Type and Code
	 * @param parameterType
	 * @param parameterCode
	 * @param languageCode
	 * @return
	 */
	private String getParameterDescByTypeAndCode(String parameterType,
			String parameterCode, String languageCode) {
		IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(
	            ModuleCodeConst.SUPPLIER_SELECTION, IParameterSO.class);;
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterType(parameterType);
		reqDTO.setLanguageCode(languageCode == null || languageCode.isEmpty()?CommonConstants.DEFAULT_LANGUAGE_CODE:languageCode);
		SearchBean<PmParameterDesc> parameterDescSearchBean = new SearchBean<>();
		parameterDescSearchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_EQUALS.value(), parameterCode));
		reqDTO.setParameterDescSearchBean(parameterDescSearchBean);

		List<PmParameterDesc> parameterDescList = parameterSO
				.retrieveParameterDescListByType(reqDTO).getParameterDescList();

		if (parameterDescList != null && !parameterDescList.isEmpty()) {
			return parameterDescList.get(0).getCodeName();
		}

		return null;
	}
	// 25037 - end
}
