/**
 * 
 */
package my.ep.web.backingbean.sc.evaluation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

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
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
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
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRole;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserNotification;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserPreference;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmAddressType;
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
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.evaluation.EvaluationMeetingManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.liferay.portal.util.PortalUtil;

/**
 * @author ACER
 * 
 */

@ManagedBean(name = "evaluationMeetingBackingBean")
@ViewScoped
public class EvaluationMeetingBackingBean extends
		SourcingBaseBackingBean<EvaluationMeetingManagedBean> {
	private IPagingDataModel<ScQt> searchQuotatioTender = new clonePagingDataModel();
	private IPagingDataModel<PmUser> searchExistingUser = new existingUserPagingDataModel();

	
	
	public EvaluationMeetingBackingBean() {

		if (this.getManagedBean().getCloneQtnTenderSearchBean() == null) {
			this.getManagedBean().setCloneQtnTenderSearchBean(
					new QtnTenderSearchBean());
		}
		if (this.getManagedBean().getCommitteMember() == null) {
			this.getManagedBean().setCommitteMember(new ScCommitteeMember());
		}

	}

	@PostConstruct
	public void init() {
		// retriveMeetingDetails();
	}

	/**
	 * @param committeeRole
	 * @param types
	 * @return
	 */

	public SelectItem[] getRoleTypeDropdownValues(String committeeRole) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.COMMITTEE_ROLE, searchBean);
		return items;
	}

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

	public void retriveMeetingDetails(Long qtId, String commType) {
		IQTMeetingSO meetingSO;
		IQTDetailSO qtDetailSo;
		try {

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

					if (null != resDTO.getQtCommittee()
							.getCommitteeActivities()

							&& !resDTO.getQtCommittee()
									.getCommitteeActivities().isEmpty()) {

						this.getManagedBean().setCommitteeMemberActivity(
								resDTO.getQtCommittee()
										.getCommitteeActivities());

					} else {

						ScCommittee sccommittee = resDTO.getQtCommittee()
								.getScCommittee();
						List<ScQtCommitteeActivity> localList = new ArrayList<ScQtCommitteeActivity>();
						if (null != sccommittee.getScCommitteeMembers()
								&& !sccommittee.getScCommitteeMembers()
										.isEmpty()) {
							for (ScCommitteeMember scCommitteeMember : sccommittee
									.getScCommitteeMembers()) {
								scCommitteeMember.setScCommittee(sccommittee);
								setUserDetails(scCommitteeMember);

								ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
								activity.setScQtCommittee(this.getManagedBean()
										.getScQtCommittee());
								activity.setScCommitteeMember(scCommitteeMember);
								setUserDetails(activity);
								localList.add(activity);
							}
						}
						this.getManagedBean().setCommitteeMemberActivity(
								localList);

					}

					List<ScQtCommitteRemarks> qtRemarkList = this
							.getManagedBean().getQtCommitteeRemarks();
					if (qtRemarkList == null) {
						qtRemarkList = new ArrayList<ScQtCommitteRemarks>();
					}

					List<ScQtCommitteRemarks> remarks = this.getManagedBean()
							.getScQtCommittee().getQtCommitteRemarks();
					if (null == remarks) {
						remarks = new ArrayList<ScQtCommitteRemarks>();
					}

					if (null != remarks && !remarks.isEmpty()) {
						for (ScRemark remark : remarks) {
							ScQtCommitteRemarks qtRemark = (ScQtCommitteRemarks) remark;
							qtRemarkList.add(qtRemark);
						}
						this.getManagedBean().setQtCommitteeRemarks(
								qtRemarkList);
					}
					initializeRemarks(this.getManagedBean().getScQtCommittee(),
							this.getManagedBean().getQtCommitteeRemarks(),
							ScQtCommitteRemarks.class);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			meetingSO = null;
		}

	}

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

	public void addCommitteeDetails() {
		if (QTConstants.DOCTYPE_EVAL_COMMITTE.equalsIgnoreCase(this
				.getManagedBean().getCommitteeType())) {
			this.getManagedBean().setCommitteeTypeDesc(
					QTConstants.EVALUATION_COMMITTEE_TAB);
		}

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

	}

	/**
	 * User selection types gov external new
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

	public void okCommitteeAction() {
		// debugLogger("okCommitteeAction");
		IUserSO iUserSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		IParameterSO parameterSo = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);

		UserReqDTO userReqDTO = new UserReqDTO();

		// ScCommittee scCommittee = this.getManagedBean().getScCommittee();
		// ScQt scQt = this.getManagedBean().getQt();
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(60730L);
		StatusResDTO statusResDTO = getStatusSO().retrieveStatusByStatusId(
				statusReqDTO);
		PmStatus status = statusResDTO.getPmStatus();
		// scQt.setQtStatus(status);

		List<ScCommitteeMember> memberList = getManagedBean()
				.getScCommitteeMemberList();
		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}

		List<ScQtCommitteeActivity> committeeActivity = getManagedBean()
				.getCommitteeMemberActivity();
		if (null == committeeActivity) {
			committeeActivity = new ArrayList<ScQtCommitteeActivity>();
		}

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
			member.setAccessRight("E");
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
			pmUserNotification.setNotifyMode("E");
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

			ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
			activity.setScQtCommittee(this.getManagedBean().getScQtCommittee());
			activity.setScCommitteeMember(member);
			setUserDetails(activity);
			committeeActivity.add(activity);
			// memberList.add(member);
			this.getManagedBean().setCommitteeMemberActivity(committeeActivity);
		} else {

			List<String> itemIds = new ArrayList<String>();

			for (PmUser user : getManagedBean().getSelectedPmUser()) {
				ScCommitteeMember committeeMember = new ScCommitteeMember();
				committeeMember.setMemberName(user.getUserName());
				committeeMember.setIcPassport(user.getIdentificationNo());
				committeeMember.setDesignation(user.getDesignation());
				if (null != user.getPmAddressTypes()) {
					for (PmAddressType address : user.getPmAddressTypes()) {
						if (null != address.getPmAddress()) {
							committeeMember.setEmail(address.getPmAddress()
									.getEmail());
							break;
						}
					}
				}

				committeeMember.setGrade(user.getGrade());
				committeeMember.setPNP(user.isPMP());
				committeeMember.setPmUser(user);
				/*
				 * committeeMember.setPmOrgProfile(this.getManagedBean().getQt()
				 * .getPmOrgProfile());
				 */
				if (committeeMember.isPNP() == true) {

					ParameterReqDTO chairPerson = new ParameterReqDTO();
					chairPerson
							.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
					chairPerson
							.setParameterCode(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
					ParameterResDTO chairPersonresDTO = parameterSo
							.retrieveParameterByTypeAndCode(chairPerson);

					committeeMember.setMemberRole(chairPersonresDTO
							.getPmParameter());
				} else {
					committeeMember.setMemberRole(user.getMemberRole());
				}

				committeeMember.setAccessRight("E");
				committeeMember.setScCommittee(this.getManagedBean()
						.getScQtCommittee().getScCommittee());
				committeeMember.setPmOrgProfile(this.getManagedBean().getQt()
						.getPmOrgProfile());
				setUserDetails(committeeMember);

				if (committeeActivity.isEmpty()) {

					ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
					setUserDetails(activity);
					activity.setScQtCommittee(this.getManagedBean()
							.getScQtCommittee());
					activity.setScCommitteeMember(committeeMember);
					committeeActivity.add(activity);
				} else {
					for (ScQtCommitteeActivity committee : committeeActivity) {
						itemIds.add(committee.getScCommitteeMember()
								.getIcPassport());
					}
					if (itemIds.size() > 0) {
						if (itemIds.contains(user.getIdentificationNo())) {

						} else {
							ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
							setUserDetails(activity);
							activity.setScQtCommittee(this.getManagedBean()
									.getScQtCommittee());
							activity.setScCommitteeMember(committeeMember);
							committeeActivity.add(activity);
						}
					}
				}

			}
			this.getManagedBean().setCommitteeMemberActivity(committeeActivity);
		}
		RequestContext.getCurrentInstance().execute(
				"newspecificationcommittee.hide()");
	}

	public void saveCommitteeAction() {

		try {
			MeetingReqDTO metReqDtO = prepareMeetingDtls();
			IQTMeetingSO meetingso = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTMeetingSO.class);

			meetingso.saveQTDtl(metReqDtO);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Meeting Saved Successfully", ""));
		} finally {

		}
	}

	public MeetingReqDTO prepareMeetingDtls() {
		MeetingReqDTO metReqDtO = new MeetingReqDTO();
		try {

			ScQtCommittee qtCommittee = this.getManagedBean()
					.getScQtCommittee();
			List<ScQtMeeting> meetingList = this.getManagedBean()
					.getScQtMeetings();

			if (meetingList != null && !meetingList.isEmpty()) {
				qtCommittee.setScQtMeetings(meetingList);
			}
			List<ScQtCommitteeActivity> scQtCommitteeActivity = this
					.getManagedBean().getCommitteeMemberActivity();

			if (scQtCommitteeActivity != null
					&& !scQtCommitteeActivity.isEmpty()) {
				for (ScQtCommitteeActivity committeeactivity : scQtCommitteeActivity) {
					if (null != committeeactivity.getScCommitteeMember()
							.getMemberRole()
							&& null == committeeactivity.getScCommitteeMember()
									.getMemberRole().getParameterId()) {
						committeeactivity.getScCommitteeMember().setMemberRole(
								null);
					}
				}
				qtCommittee.setCommitteeActivities(scQtCommitteeActivity);
			}

			List<ScQtCommitteRemarks> remarksList = this.getManagedBean()
					.getQtCommitteeRemarks();

			if (isNotNullOREmpty(remarksList)) {
				qtCommittee.setQtCommitteRemarks(finalizeRemarks(remarksList));

			}

			qtCommittee.setMeetingAttendances(null);
			metReqDtO.setScQtCommitte(qtCommittee);

		} finally {

		}
		return metReqDtO;
	}

	public void existingCommitteeSearchAction() {
		searchExistingUser.reloadData();

	}

	public void addCloneCommittee() {
		this.getManagedBean().setCloneQtnTenderSearchBean(
				new QtnTenderSearchBean());
		this.getManagedBean().setCloneCommitteMemebers(
				new ArrayList<ScCommitteeMember>());
	}

	public void cloneSearchCommitteAction() {
		try {
			searchQuotatioTender.reloadData();

		} finally {

		}
	}

	public void onRowCloneAction(SelectEvent event) {
		// debugLogger("onRowCloneAction");
		ScQt scQt = (ScQt) event.getObject();

		CommitteReqDTO reqDTO = new CommitteReqDTO();

		reqDTO.setQtId(scQt.getQtId());
		CommitteResDTO resDTO = null;
		try {
			ICommitteSO committeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			resDTO = committeSO
					.retrieveCommitteMembersByQtIdAndCommType(reqDTO);
			if (resDTO != null) {

				this.getManagedBean().setCloneCommitteMemebers(
						resDTO.getCommMemberResultBean().getResultList());
			}

		} finally {

		}
	}

	public void reset() {

		this.getManagedBean().setName(null);
		this.getManagedBean().setIdentificationType(null);
		this.getManagedBean().setIdentificationNo(null);
	}

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

	public void cloneCommitteeDtls() {
		List<ScQtCommitteeActivity> committeeMemberList = this.getManagedBean()
				.getCommitteeMemberActivity();
		if (committeeMemberList == null) {
			committeeMemberList = new ArrayList<ScQtCommitteeActivity>();
		}
		List<ScCommitteeMember> cloneCommitteeList = this.getManagedBean()
				.getCloneCommitteMemebersSelect();
		if (cloneCommitteeList == null) {
			cloneCommitteeList = new ArrayList<ScCommitteeMember>();
		}

		List<ScQtCommitteeActivity> localList = new ArrayList<ScQtCommitteeActivity>();

		List<String> itemIds = new ArrayList<String>();

		if (committeeMemberList.isEmpty()) {
			for (ScCommitteeMember scCommitteeMember : cloneCommitteeList) {
				scCommitteeMember.setScCommittee(this.getManagedBean()
						.getScQtCommittee().getScCommittee());
				setUserDetails(scCommitteeMember);

				ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
				activity.setScQtCommittee(this.getManagedBean()
						.getScQtCommittee());
				activity.setScCommitteeMember(scCommitteeMember);
				setUserDetails(activity);
				localList.add(activity);
			}
		} else {
			for (ScQtCommitteeActivity committee : committeeMemberList) {
				itemIds.add(committee.getScCommitteeMember().getIcPassport());
			}
			for (ScCommitteeMember committees : cloneCommitteeList) {
				if (itemIds.size() > 0) {
					if (itemIds.contains(committees.getIcPassport())) {

					} else {
						committees.setScCommittee(this.getManagedBean()
								.getScQtCommittee().getScCommittee());
						setUserDetails(committees);

						ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
						activity.setScQtCommittee(this.getManagedBean()
								.getScQtCommittee());
						activity.setScCommitteeMember(committees);
						setUserDetails(activity);
						localList.add(activity);
					}
				}
			}

		}

		committeeMemberList.addAll(localList);
		this.getManagedBean().setCommitteeMemberActivity(committeeMemberList);

	}

	public void deleteMeetingDetails() {
		try {
			List<ScQtMeeting> meetingList = getManagedBean().getScQtMeetings();
			List<ScQtMeeting> selectMeeting = getManagedBean()
					.getMeetingMultiSelect();

			if (meetingList.size() == selectMeeting.size()) {
				meetingList.clear();
			} else {
				for (ScQtMeeting meeting : selectMeeting)
					meetingList.remove(meeting);
			}
		} finally {

		}
	}

	public void deleteCommitteeMembers() {
		try {
			List<ScQtCommitteeActivity> committeeList = getManagedBean()
					.getCommitteeMemberActivity();
			List<ScQtCommitteeActivity> selectCommitteeList = getManagedBean()
					.getCommitteeActivitySelect();

			if (committeeList.size() == selectCommitteeList.size()) {
				committeeList.clear();
			} else {
				for (ScQtCommitteeActivity committeeMember : selectCommitteeList)
					committeeList.remove(committeeMember);
			}
		} finally {

		}
	}

	private IUserSO getUserSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
	}

	private IParameterSO getParameterSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}

	private IOrgProfileSO getOrgProfileSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
	}

	private IStatusSO getStatusSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
	}

	private class clonePagingDataModel extends PagingDataModel<ScQt> {
		private static final long serialVersionUID = 1L;

		public clonePagingDataModel() {

			super("clonesearchCommitteid");

		}

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
					SearchFilterBean bean = new SearchFilterBean();
					bean.setSearchField(QTConstants.CREATED_DATE);
					bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN
							.value());

					SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
							fromCreatedDate, toCreatedDate);

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

	private class existingUserPagingDataModel extends PagingDataModel<PmUser> {
		private static final long serialVersionUID = 1L;

		public existingUserPagingDataModel() {

			super("existingcommitteesearch");

		}

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
	 * @return the searchQuotatioTender
	 */
	public IPagingDataModel<ScQt> getSearchQuotatioTender() {
		return searchQuotatioTender;
	}

	/**
	 * @param searchQuotatioTender
	 *            the searchQuotatioTender to set
	 */
	public void setSearchQuotatioTender(
			IPagingDataModel<ScQt> searchQuotatioTender) {
		this.searchQuotatioTender = searchQuotatioTender;
	}

	/**
	 * @return the searchExistingUser
	 */
	public IPagingDataModel<PmUser> getSearchExistingUser() {
		return searchExistingUser;
	}

	/**
	 * @param searchExistingUser
	 *            the searchExistingUser to set
	 */
	public void setSearchExistingUser(
			IPagingDataModel<PmUser> searchExistingUser) {
		this.searchExistingUser = searchExistingUser;
	}

	public String submitMettingDetails(String outCome) {
		try {
			FacesScopes facesScopes = new FacesScopes();
			String ctxToken = facesScopes.getBPMAuthenticationCtxToken();

			IQTMeetingSO meetingso = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTMeetingSO.class);
			MeetingReqDTO reqDTO = prepareMeetingDtls();

			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setToken(ctxToken);
			reqDTO.setPayLoad(this.getManagedBean().getPayLoad());
			reqDTO.setOutcome(outCome);
			MeetingResDTO resDTO = meetingso.updateBPMAction(reqDTO);

			// Code to renew the session Object..
			renewSesionObject();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Meeting Submitted Successfully", ""));
		} finally {

		}

		return CommonUIConst.ACKNOWLEDGE_PAGE;

	}

	public void prerenderView() {

		renewSesionObject();
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		this.getManagedBean().setTaskId(taskId);
		log.info("taskId : " + taskId);
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		// TODO
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		// String ctx = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();
		String taskName = res.getTaskName();
		this.getManagedBean().setOutComes(res.getAction());
		SCEvaluationDataType evaluationDataType = (SCEvaluationDataType) obj;
		this.getManagedBean().setPayLoad(evaluationDataType);
		FacesContext fc = FacesContext.getCurrentInstance();
		retriveMeetingDetails(evaluationDataType.getQtId().longValue(),
				evaluationDataType.getCommitteeType());
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

	

}
