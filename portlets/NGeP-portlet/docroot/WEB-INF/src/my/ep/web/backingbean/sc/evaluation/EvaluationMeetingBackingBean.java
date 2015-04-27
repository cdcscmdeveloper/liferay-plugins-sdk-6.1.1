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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRole;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserNotification;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserPreference;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.common.ScRemark;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.evaluation.EvaluationMeetingManagedBean;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.commons.lang.StringUtils;
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
		SourcingBaseBackingBean<EvaluationMeetingManagedBean> {

	/** The search quotation tender. */
	private IPagingDataModel<ScQt> searchQuotatioTender = new clonePagingDataModel();

	/** The search existing user. */
	private IPagingDataModel<PmUser> searchExistingUser = new existingUserPagingDataModel();

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

	public SelectItem[] getRoleTypeDropdownValues(String committeeRole) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.COMMITTEE_ROLE_SECRETARY);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.COMMITTEE_ROLE, searchBean);
		return items;
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
		recommendList.add(ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));

		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.SC_PROCUREMENT_MODE, searchBean);
		return items;
	}

	/**
	 * Retrieve meeting details.Event handle to display all the meeting and
	 * committee members details.
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

				if (null != resDTO.getMeetingDtls()) {

					this.getManagedBean().setScQtMeetings(
							resDTO.getMeetingDtls());

				}
				if (null != resDTO.getQtCommittee()) {
					this.getManagedBean().setScQtCommittee(
							resDTO.getQtCommittee());

					/*
					 * if (null != resDTO.getQtCommittee()
					 * .getCommitteeActivities()
					 * 
					 * && !resDTO.getQtCommittee()
					 * .getCommitteeActivities().isEmpty()) {
					 * Collections.sort(resDTO.getQtCommittee()
					 * .getCommitteeActivities());
					 * this.getManagedBean().setCommitteeMemberActivity(
					 * resDTO.getQtCommittee() .getCommitteeActivities());
					 * 
					 * } else {
					 */

					ScCommittee sccommittee = resDTO.getQtCommittee()
							.getScCommittee();
					List<ScCommitteeMember> localList = new ArrayList<ScCommitteeMember>();
					if (null != sccommittee.getScCommitteeMembers()
							&& !sccommittee.getScCommitteeMembers().isEmpty()) {
						/*
						 * for (ScCommitteeMember scCommitteeMember :
						 * sccommittee .getScCommitteeMembers()) { //
						 * scCommitteeMember.setScCommittee(sccommittee); //
						 * setUserDetails(scCommitteeMember);
						 * 
						 * ScQtCommitteeActivity activity = new
						 * ScQtCommitteeActivity();
						 * activity.setScQtCommittee(this.getManagedBean()
						 * .getScQtCommittee());
						 * activity.setScCommitteeMember(scCommitteeMember);
						 * setUserDetails(activity); localList.add(activity); }
						 */
						localList.addAll(sccommittee.getScCommitteeMembers());
					}

					Collections.sort(localList);
					this.getManagedBean().setCommitteeMemberActivity(localList);

					// }

					List<ScQtCommitteRemarks> qtRemarkList = this
							.getManagedBean().getQtCommitteeRemarks();
					if (qtRemarkList == null) {
						qtRemarkList = new ArrayList<ScQtCommitteRemarks>();
					}

					List<ScQtCommitteRemarks> remarks = this.getManagedBean()
							.getScQtCommittee().getQtCommitteRemarks();

					if (null != remarks && !remarks.isEmpty()) {
						for (ScRemark remark : remarks) {
							ScQtCommitteRemarks qtRemark = (ScQtCommitteRemarks) remark;
							qtRemarkList.add(qtRemark);
						}
						this.getManagedBean().setQtCommitteeRemarks(
								qtRemarkList);
					}
					List<ScQtCommitteRemarks> remark = initializeRemarks(this
							.getManagedBean().getScQtCommittee(), null,
							qtRemarkList, ScQtCommitteRemarks.class);
					// qtRemarkList.addAll(remark);
					this.getManagedBean().setQtCommitteeRemarks(qtRemarkList);

				}

			}
		} finally {
			meetingSO = null;
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

			PmStatus status = new PmStatus();
			status.setStatusId(62650L);

			meeting.setMeetingStatus(status);
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

		getManagedBean().setName(null);
		getManagedBean().setIdentificationType(null);
		getManagedBean().setIdentificationNo(null);
		getManagedBean().setExternalUsers(null);
		getManagedBean().setSelectedPmUser(null);
		this.getManagedBean().setCommitteMember(null);
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		committeeMember.setUserSelection("E");
		this.getManagedBean().setCommitteMember(committeeMember);
		getSelectionTypes();
		searchExistingUser.reloadData();

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
		this.getManagedBean().setExternalUsers(null);
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
		if ("N".equalsIgnoreCase(getManagedBean().getCommitteMember()
				.getUserSelection())) {
			ScCommitteeMember member = new ScCommitteeMember();
			member.setMemberName(getManagedBean().getCommitteMember()
					.getMemberName());
			member.setIcPassport(getManagedBean().getCommitteMember()
					.getIcPassport());
			member.setGrade(getManagedBean().getCommitteMember().getGrade());
			member.setDesignation(getManagedBean().getCommitteMember()
					.getDesignation());
			member.setPNP(getManagedBean().getCommitteMember().isPNP());
			member.setEmail(getManagedBean().getCommitteMember().getEmail());
			member.setMemberRole(getManagedBean().getCommitteMember()
					.getMemberRole());
			member.setAccessRight(QTConstants.ACCESS_RIGHT);
			member.setPmOrgProfile(this.getManagedBean().getQt()
					.getPmOrgProfile());

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
			List<PmUserNotification> pmUserNotifications = new ArrayList<PmUserNotification>();
			PmUserNotification pmUserNotification = new PmUserNotification();
			pmUserNotification.setPmUser(user);
			pmUserNotification.setNotifyMode(QTConstants.ACCESS_RIGHT);
			pmUserNotification.setPreferred(true);
			pmUserNotifications.add(pmUserNotification);
			user.setPmUserNotifications(pmUserNotifications);

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
			PmUserPreference pmUserPreference = new PmUserPreference();
			pmUserPreference.setPmUser(user);
			pmUserPreference.setFormatDate("DD-MMM-YYYY");
			pmUserPreference.setFormatTime("HH:mm:ss");
			pmUserPreference.setFormatNumber("#,##0");
			pmUserPreference.setPreferredLanguage("en");
			pmUserPreference.setNotifyRemovalDay(30);
			user.setPmUserPreference(pmUserPreference);

			userReqDTO.setOrgType(OrganizationType.OTHERS.value());
			userReqDTO.setPmUser(user);
			UserResDTO resDTO = getUserSO().saveUser(userReqDTO);

			member.setPmUser(resDTO.getPmUser());

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
						reqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
						reqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
						ParameterResDTO resDTO = getParameterSO()
								.retrieveParameterByTypeAndCode(reqDTO);
						committeeMember.setMemberRole(resDTO.getPmParameter());
					} else {
						ParameterReqDTO reqDTO = new ParameterReqDTO();
						reqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
						reqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_MEMBER);
						ParameterResDTO resDTO = getParameterSO()
								.retrieveParameterByTypeAndCode(reqDTO);
						committeeMember.setMemberRole(resDTO.getPmParameter());
					}

					committeeMember.setAccessRight(QTConstants.ACCESS_RIGHT);
					committeeMember.setScCommittee(this.getManagedBean()
							.getScQtCommittee().getScCommittee());
					committeeMember.setPmOrgProfile(this.getManagedBean()
							.getQt().getPmOrgProfile());
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
						if (itemIds.size() > 0) {
							if (itemIds.contains(user.getIdentificationNo())) {
								throw new EpException(MessageConst.ERROR_760194);
							} else {
								// ScQtCommitteeActivity activity = new
								// ScQtCommitteeActivity();
								// setUserDetails(activity);
								// activity.setScQtCommittee(this.getManagedBean()
								// .getScQtCommittee());
								// activity.setScCommitteeMember(committeeMember);
								// committeeActivity.add(activity);
								localList.add(committeeMember);
							}
						}
					}

				}
			}
			// localList.add(activity);
			// committeeActivity.add(activity);
			// memberList.add(member);

		}
		committeeActivity.addAll(localList);
		Collections.sort(committeeActivity);
		this.getManagedBean().setCommitteeMemberActivity(committeeActivity);
		RequestContext.getCurrentInstance().execute(
				"newspecificationcommittee.hide()");
	}

	/**
	 * Check committee formation.Event handle to check committee members(1
	 * Chairperosn,1 Secretary,at least 1 member) exist or not in the list
	 * 
	 * @param metReqDtO
	 *            the met req dt o
	 */
	private void checkCommitteeFormation(MeetingReqDTO metReqDtO) {
		IQTMeetingSO meetingso = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTMeetingSO.class);
		metReqDtO.setUser(new FacesScopes().getUserSessionBean());
		meetingso.validateCommitteDtls(metReqDtO);
	}

	/**
	 * Save committee action.Event handle to save the meeting and committee
	 * members information.
	 */
	public void saveCommitteeAction() {

		try {
			MeetingReqDTO metReqDtO = prepareMeetingDtls();
			checkCommitteeFormation(metReqDtO);
			IQTMeetingSO meetingso = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTMeetingSO.class);

			String taskId = this.getManagedBean().getTaskId();
			String[] outComes = this.getManagedBean().getOutComes();
			Long qtId = this.getManagedBean().getQt().getQtId();
			String committeeType = this.getManagedBean().getCommitteeType();
			String taskName = this.getManagedBean().getTaskName();
			Long currentStatusId = this.getManagedBean().getCurrentStatusId();
			Object object = this.getManagedBean().getPayLoad();
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

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Meeting Saved Successfully",
							"Evaluation Meeting Saved Successfully"));
		} finally {

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

			if (meetingList != null && !meetingList.isEmpty()) {
				qtCommittee.setScQtMeetings(meetingList);
			} else {
				qtCommittee.setScQtMeetings(null);
			}
			List<ScCommitteeMember> scQtCommitteeActivity = this
					.getManagedBean().getCommitteeMemberActivity();

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

		} finally {

		}
		return metReqDtO;
	}

	/**
	 * Existing committee search action.Event handle to search existing
	 * committee members list.
	 */
	public void existingCommitteeSearchAction() {
		searchExistingUser.reloadData();

	}

	/**
	 * Adds the clone committee.Event handle to create search bean for clone
	 * popup.
	 */
	public void addCloneCommittee() {
		this.getManagedBean().setCloneQtnTenderSearchBean(
				new QtnTenderSearchBean());
		this.getManagedBean().setCloneCommitteMemebers(
				new ArrayList<ScCommitteeMember>());
		searchQuotatioTender.reloadData();
	}

	/**
	 * Clone search committee action.Event handle to search clone committee
	 * members.
	 */
	public void cloneSearchCommitteAction() {
		try {
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

		}
	}

	/**
	 * Reset.Event handle to reset the values in add popup.
	 */
	public void reset() {

		this.getManagedBean().setName(null);
		this.getManagedBean().setIdentificationType(null);
		this.getManagedBean().setIdentificationNo(null);
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
				localList.add(scCommitteeMember);
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
							ParameterReqDTO reqDTO = new ParameterReqDTO();
							reqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
							reqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
							ParameterResDTO resDTO = getParameterSO()
									.retrieveParameterByTypeAndCode(reqDTO);
							committees.setMemberRole(resDTO.getPmParameter());
						} else {
							ParameterReqDTO reqDTO = new ParameterReqDTO();
							reqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
							reqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_MEMBER);
							ParameterResDTO resDTO = getParameterSO()
									.retrieveParameterByTypeAndCode(reqDTO);
							committees.setMemberRole(resDTO.getPmParameter());
						}
						// activity.setScCommitteeMember(committees);
						// setUserDetails(activity);
						localList.add(committees);
					}
				}
			}

		}

		committeeMemberList.addAll(localList);
		Collections.sort(committeeMemberList);
		this.getManagedBean().setCommitteeMemberActivity(committeeMemberList);
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

			super("clonesearchCommitteid");

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

				String procurementMode = getManagedBean()
						.getCloneQtnTenderSearchBean().getProcurementMode();
				if (!StringUtils.isBlank(procurementMode)) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"procurementMode.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									procurementMode, true));

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
				if (null != getManagedBean().getQt().getPmOrgProfile()
						.getParentOrgProfile()) {
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									"pmOrgProfile.parentOrgProfile.orgProfileId",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getQt().getPmOrgProfile()
											.getParentOrgProfile()
											.getOrgProfileId()));
				}

				reqDTO.setQtSearchBean(searchBean);

				QuationTenderResDTO dto = null;
				IQTDetailSO detailSO;

				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				dto = detailSO.getQuationTenders(reqDTO);

				if (dto.isSuccessful()) {

					return dto.getQtResultBean();
				}
			} finally {

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
		if (null != activity.getMemberRole().getParameterId()) {
			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(activity.getMemberRole().getParameterId())) {

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
	private class existingUserPagingDataModel extends PagingDataModel<PmUser> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new existing user paging data model.
		 */
		public existingUserPagingDataModel() {

			super("existingcommitteesearch");

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		public ISearchResultBean<PmUser> pageChangeListener(
				ISearchBean<PmUser> searchBean) {

			try {
				UserReqDTO userReqDTO = new UserReqDTO();

				UserResDTO resDto = null;
				if (null != getManagedBean().getName()
						&& !"".equalsIgnoreCase(getManagedBean().getName())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(QTConstants.USER_NAME,
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getName(), true));
				}
				if (null != getManagedBean().getIdentificationNo()
						&& !"".equalsIgnoreCase(getManagedBean()
								.getIdentificationNo())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(QTConstants.IDENTIFICATION_NO,
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getIdentificationNo(),
									true));
				}

				if (null != getManagedBean().getIdentificationType()
						&& !"".equalsIgnoreCase(getManagedBean()
								.getIdentificationType())) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"identificationCode.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getIdentificationType(),
									true));
				}
				if (null != getManagedBean().getQt().getPmOrgProfile()
						.getParentOrgProfile()) {
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									"pmUserOrgs.pmOrgProfile.parentOrgProfile.orgProfileId",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getQt().getPmOrgProfile()
											.getParentOrgProfile()
											.getOrgProfileId()));
				}
				userReqDTO.setUserSearchBean(searchBean);

				IUserSO userSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IUserSO.class);
				resDto = userSO.searchUserProfile(userReqDTO);
				if (resDto != null) {
					getManagedBean().setExternalUsers(
							resDto.getUserResultBean().getResultList());
					for (PmUser user : getManagedBean().getExternalUsers()) {
						if (user.getMemberRole() == null) {
							user.setMemberRole(new PmParameter());
						}
					}
				}
				return resDto.getUserResultBean();

			} finally {

			}

		}
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
		try {

			FacesScopes facesScopes = new FacesScopes();
			String ctxToken = facesScopes.getBPMAuthenticationCtxToken();

			IQTMeetingSO meetingso = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTMeetingSO.class);
			MeetingReqDTO reqDTO = prepareMeetingDtls();
			checkCommitteeFormation(reqDTO);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setToken(ctxToken);
			reqDTO.setPayLoad(this.getManagedBean().getPayLoad());
			reqDTO.setOutcome(outCome);
			MeetingResDTO resDTO = meetingso.updateBPMAction(reqDTO);

			// Code to renew the session Object..
			renewSesionObject();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "",
							"Evaluation Meeting Submitted Successfully"));
		} finally {

		}

		return CommonUIConst.ACKNOWLEDGE_PAGE;

	}

	/**
	 * Prerender view.click on task list navigate to coordinate meeting details
	 * Screens for(EC/OC/TEC/FEC).
	 */
	public void prerenderView() {

		SCEvaluationDataType evaluationDataType = getBpmTaskDetails(SCEvaluationDataType.class);

		this.getManagedBean().setPayLoad(evaluationDataType);
		// FacesScopes facesScopes = new FacesScopes();
		FacesContext fc = FacesContext.getCurrentInstance();
		retriveMeetingDetails(evaluationDataType.getQtId().longValue(),
				evaluationDataType.getCommitteeType());
		String taskName = this.getManagedBean().getTaskName();
		if (SCProcessNameConst.COORDINATE_EC_MEETING.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.COORDINATE_OC_MEETING
						.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.COORDINATE_TEC_MEETING
						.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.COORDINATE_FEC_MEETING
						.equalsIgnoreCase(taskName)) {

			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/confirmMeeting/confirmMeetingView.xhtml");
		} else {
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sc/evaluation/xhtml/sc/evaluation/evaluationstage1view.xhtml");
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
	 * Gets the search existing user.
	 * 
	 * @return the search existing user
	 */
	public IPagingDataModel<PmUser> getSearchExistingUser() {
		return searchExistingUser;
	}

	/**
	 * Sets the search existing user.
	 * 
	 * @param searchExistingUser
	 *            the new search existing user
	 */
	public void setSearchExistingUser(
			IPagingDataModel<PmUser> searchExistingUser) {
		this.searchExistingUser = searchExistingUser;
	}

}
