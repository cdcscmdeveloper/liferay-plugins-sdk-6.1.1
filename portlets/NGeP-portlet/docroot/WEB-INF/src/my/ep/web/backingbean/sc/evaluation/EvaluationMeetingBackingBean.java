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
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRole;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserNotification;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserPreference;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.managedbean.sc.evaluation.EvaluationMeetingManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.primefaces.event.SelectEvent;

/**
 * @author ACER
 * 
 */

@ManagedBean(name = "evaluationMeetingBackingBean")
@RequestScoped
public class EvaluationMeetingBackingBean extends
		SourcingBaseBackingBean<EvaluationMeetingManagedBean> {

	public EvaluationMeetingBackingBean() {
		/*
		 * getManagedBean().setRoleTypes(
		 * getRoleTypeDropdownValues(ParameterTypeConst.COMMITTEE_ROLE));
		 */
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
		retriveMeetingDetails();
	}

	/**
	 * @param committeeRole
	 * @param types
	 * @return
	 */

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

				items[i++] = new SelectItem(priceTypeCode, getParameterDesc(
						committeeRole, priceTypeCode));
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return items;
		// return getDropdownCodeValues(committeeRole);
	}

	public void retriveMeetingDetails() {
		IQTMeetingSO meetingSO;
		try {

			MeetingReqDTO reqDTO = new MeetingReqDTO();
			MeetingResDTO resDTO = new MeetingResDTO();

			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			if (this.getManagedBean().getScQtMeetings() == null) {
				reqDTO.setQtId(146L);
				reqDTO.setCommitteType("SC");
				resDTO = meetingSO.retrieveMeetingDls(reqDTO);

				if (resDTO.getMeetingDtls() != null) {

					this.getManagedBean().setScQtMeetings(
							resDTO.getMeetingDtls());

				}
				if (resDTO.getCommitteMembers() != null && !resDTO.getCommitteMembers().isEmpty()) {
					this.getManagedBean().setCommitteeMemberActivity(
							resDTO.getCommitteMembers());
					
				}
				

				List<ScQtCommitteRemarks> qtRemarkList = this.getManagedBean()
						.getQtCommitteeRemarks();
				if (qtRemarkList == null) {
					qtRemarkList = new ArrayList<ScQtCommitteRemarks>();
				}
				ScQtCommittee committee = resDTO.getQtCommittee();
				if (null == committee) {
					committee = new ScQtCommittee();
				}

				this.getManagedBean().setScQtCommittee(committee);

				if (committee.getQtCommitteRemarks() != null) {
					for (ScRemark remark : resDTO.getQtCommittee()
							.getQtCommitteRemarks()) {
						ScQtCommitteRemarks qtRemark = (ScQtCommitteRemarks) remark;
						qtRemarkList.add(qtRemark);
					}
					this.getManagedBean().setQtCommitteeRemarks(qtRemarkList);
				}
				initializeRemarks();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			meetingSO = null;
		}

	}

	public void initializeRemarks() {
		FacesScopes facesScopes = new FacesScopes();
		IStatusSO iStatusSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		ScQtCommitteRemarks remark = null;
		if(this.getManagedBean().getQtCommitteeRemarks()==null){
			this.getManagedBean().setQtCommitteeRemarks(new ArrayList<ScQtCommitteRemarks>());
		}

		remark = new ScQtCommitteRemarks();
		List<ScQtRemark> qtRemarkList = new ArrayList<ScQtRemark>();
		 setUserDetails(remark);
		remark.setCreatedUser(facesScopes.getUserSessionBean());
		remark.setScQtCommittee(this.getManagedBean().getScQtCommittee());
		PmStatus status = new PmStatus();
		status.setStatusId(1L);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		if (status != null) {
			statusReqDTO.setStatusId(status.getStatusId());
			StatusResDTO statusResDTO = iStatusSO
					.retrieveStatusByStatusId(statusReqDTO);
			remark.setPmStatus(statusResDTO.getPmStatus());

		}
		// qtRemarkList.add(remark);
		this.getManagedBean().getQtCommitteeRemarks().add(remark);
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
			
			IStatusSO statussO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IStatusSO.class);

			StatusReqDTO statusreq = new StatusReqDTO();
			statusreq.setStatusId(1L);
			StatusResDTO statusres = statussO
					.retrieveStatusByStatusId(statusreq);

			meeting.setMeetingStatus(statusres.getPmStatus());
			this.getManagedBean().setScQtMeetings(meetingList);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addCommitteeDetails() {
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
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		committeeMember.setUserSelection(userSelection);
		this.getManagedBean().setCommitteMember(committeeMember);
		if (SelectionType.Existing.value().equalsIgnoreCase(
				getManagedBean().getCommitteMember().getUserSelection())) {
			// getManagedBean().getCommitteMember().setExternalUser(true);

		} else if (SelectionType.New.value().equalsIgnoreCase(
				getManagedBean().getCommitteMember().getUserSelection())) {
			// getManagedBean().getCommitteMember().setExternalUser(true);

		}
	}

	public void okCommitteeAction() {
		// debugLogger("okCommitteeAction");
		IUserSO iUserSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		IParameterSO parameterSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();

		// ScCommittee scCommittee = this.getManagedBean().getScCommittee();
		// ScQt scQt = this.getManagedBean().getQt();
		List<ScCommitteeMember> memberList = getManagedBean()
				.getCommitteeMembersList();
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
			member.setMemberRole("M");
			member.setAccessRight("E");
			member.setScCommittee(this.getManagedBean().getScQtCommittee()
					.getScCommittee());
			setUserDetails(member);
			//member.setCreatedBy(1L);
			//member.setChangedBy(1L);
			//member.setCreatedDate(new Timestamp(System.currentTimeMillis()));
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
			//user.setChangedBy(1L);
			//user.setCreatedBy(1L);
			//user.setCreatedDate(new Timestamp(System.currentTimeMillis()));

			// Identification Code
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO
					.setParameterType(ParameterTypeConst.IDENTIFICATION_TYPES);
			parameterReqDTO
					.setParameterCode(ParameterConst.IDENTIFICATION_TYPES_IC);
			PmParameter identificationCode = parameterSO
					.retrieveParameterByTypeAndCode(parameterReqDTO)
					.getPmParameter();
			user.setIdentificationCode(identificationCode);

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
			PmOrgProfile pmOrgProfile = orgProfileSO
					.retrieveOrgProfileListByOrgType(orgProfileReqDTO)
					.getOrgProfileList().iterator().next();
			userOrg.setPmOrgProfile(pmOrgProfile);
			userOrg.setEffDate(new Timestamp(System.currentTimeMillis()));
			Timestamp expDate = new Timestamp(System.currentTimeMillis());
			expDate.setYear(1000);
			userOrg.setExpDate(expDate);
			List<PmUserRole> pmUserRoles = new ArrayList<PmUserRole>();
			PmUserRole pmUserRole = new PmUserRole();
			pmUserRole.setPmUserOrg(userOrg);
			PmRole pmRole = new PmRole();
			pmRole.setRoleCode(RoleCode.PLAN_OFFICER.value());
			pmUserRole.setPmRole(pmRole);
			pmUserRoles.add(pmUserRole);
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

			userReqDTO.setOrgType(OrganizationType.EXTERNAL.value());

			userReqDTO.setPmUser(user);
			UserResDTO resDTO = iUserSO.saveUser(userReqDTO);
			System.out.println("done");
			member.setPmUser(resDTO.getPmUser());
			ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
			activity.setScQtCommittee(this.getManagedBean().getScQtCommittee());
			activity.setScCommitteeMember(member);
			committeeActivity.add(activity);
			// memberList.add(member);
			this.getManagedBean().setCommitteeMemberActivity(committeeActivity);
		} else {

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
				PmOrgProfile orgProfile=new PmOrgProfile();
				orgProfile.setOrgProfileId(1L);
				committeeMember.setPmOrgProfile(orgProfile);
				// committeeMember.setMemberRole(getManagedBean().getRoleName());
				committeeMember.setMemberRole(user.getMemberRole());
				committeeMember.setAccessRight("E");
				committeeMember.setScCommittee(this.getManagedBean()
						.getScQtCommittee().getScCommittee());
				committeeMember.setCreatedBy(1L);
				committeeMember.setChangedBy(1L);
				committeeMember.setCreatedDate(new Timestamp(System
						.currentTimeMillis()));
				// committeeMember.setScCommittee(scCommittee);
				// memberList.add(committeeMember);
				ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
				activity.setScQtCommittee(this.getManagedBean().getScQtCommittee());
				activity.setScCommitteeMember(committeeMember);
				committeeActivity.add(activity);
				// memberList.add(member);

			}
			this.getManagedBean().setCommitteeMemberActivity(committeeActivity);
		}

	}

	public void saveCommitteeAction() {
		try {
			MeetingReqDTO metReqDtO = new MeetingReqDTO();
          
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
				qtCommittee.setCommitteeActivities(scQtCommitteeActivity);
			}

			
			
			
			List<ScQtCommitteRemarks> remarksList=	this.getManagedBean()
					.getQtCommitteeRemarks();
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
			qtCommittee.setQtCommitteRemarks(committeeRemarks);
			qtCommittee.setMeetingAttendances(null);
			IQTMeetingSO meetingso = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTMeetingSO.class);
			metReqDtO.setScQtCommitte(qtCommittee);
		

			meetingso.saveQTDtl(metReqDtO);
			

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Meeting Saved Successfully",
							""));
		} catch (Exception e) {
			System.out.println("this is error");
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sample error message",
							"PrimeFaces makes no mistakes"));
		} finally {

		}
	}

	public void existingCommitteeSearchAction() {
		// debugLogger("existingCommitteeSearchAction");
		UserReqDTO userReqDTO = new UserReqDTO();
		SearchBean<PmUser> searchBean = new SearchBean<PmUser>();
		UserResDTO resDto = null;
		if (getManagedBean().getName() != null
				&& !"".equalsIgnoreCase(getManagedBean().getName())) {
			SearchFilterBean usernameSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			usernameSearchBean.setSearchField("userName");
			usernameSearchBean.setSearchValue(getManagedBean().getName());
			searchBean.getDataFilterBeans().add(usernameSearchBean);
		}
		if (getManagedBean().getIdentificationNo() != null
				&& !"".equalsIgnoreCase(getManagedBean().getIdentificationNo())) {
			SearchFilterBean idNoSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			idNoSearchBean.setSearchField("identificationNo");
			idNoSearchBean.setSearchValue(getManagedBean()
					.getIdentificationNo());
			searchBean.getDataFilterBeans().add(idNoSearchBean);
		}
		userReqDTO.setUserSearchBean(searchBean);
		try {
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDto = userSO.searchUserProfile(userReqDTO);
			if (resDto != null) {
				this.getManagedBean().setExternalUsers(
						resDto.getUserResultBean().getResultList());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addCloneCommittee() {
		this.getManagedBean().setCloneQtnTenderSearchBean(
				new QtnTenderSearchBean());
	}

	public void cloneSearchCommitteAction() {
		try {
			QtnTenderSearchBean qtnTenderSearchBean = this.getManagedBean()
					.getCloneQtnTenderSearchBean();
			List<ScQt> cloneSearchQt = retriveQtCommittes(qtnTenderSearchBean);

			this.getManagedBean().setCloneTenderList(cloneSearchQt);
		} catch (Exception e) {

		}
	}

	/**
	 * @param qtnTenderSearchBean
	 * @return
	 */
	private List<ScQt> retriveQtCommittes(
			QtnTenderSearchBean qtnTenderSearchBean) {

		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		if (qtnTenderSearchBean != null) {
			SearchBean<ScQt> qtSearchBean = new SearchBean<ScQt>();
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
			Date fromDate = qtnTenderSearchBean.getFromCreatedDate();
			if (fromDate != null) {
				SearchFilterBean creatDtSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_EQUALS.value());
				creatDtSearchBean.setSearchField("QTConstants.CREATED_DATE");
				creatDtSearchBean.setSearchValue(fromDate);
				searchFilterBean.add(creatDtSearchBean);

			}

			Date toDate = qtnTenderSearchBean.getToCreatedDate();
			if (toDate != null) {
				SearchFilterBean creatDtSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_EQUALS.value());
				creatDtSearchBean.setSearchField("QTConstants.CLOSED_DATE");
				creatDtSearchBean.setSearchValue(toDate);
				searchFilterBean.add(creatDtSearchBean);

			}

			String procurementMode = qtnTenderSearchBean.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				SearchFilterBean creatDtSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_EQUALS.value());
				creatDtSearchBean
						.setSearchField("QTConstants.PROCUREMENT_MODE");
				creatDtSearchBean.setSearchValue(procurementMode);
				searchFilterBean.add(creatDtSearchBean);

			}

			String status = qtnTenderSearchBean.getQtStatus();
			if (!StringUtils.isBlank(status)) {
				SearchFilterBean creatDtSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_EQUALS.value());
				creatDtSearchBean
						.setSearchField("QTConstants.PROCUREMENT_MODE");
				creatDtSearchBean.setSearchValue(status);
				searchFilterBean.add(creatDtSearchBean);

			}

			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
			reqDTO.setQtSearchBean(qtSearchBean);

		}
		QuationTenderResDTO dto = null;
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			dto = detailSO.getQuationTenders(reqDTO);
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							ex.toString(), ex.getMessage()));

		} finally {
			detailSO = null;
		}

		return null != dto.getQtResultBean().getResultList() ? dto
				.getQtResultBean().getResultList() : new ArrayList<ScQt>();

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

		} catch (Exception ex) {
			ex.printStackTrace();
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
		// qtnTenderSearchBean.setFromCreatedDate("");
		// qtnTenderSearchBean.setCreatedDtTo(null);
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
		for (ScCommitteeMember scCommitteeMember : cloneCommitteeList) {
			scCommitteeMember.setScCommittee(this.getManagedBean()
					.getScQtCommittee().getScCommittee());
			scCommitteeMember.setCreatedBy(1L);
			scCommitteeMember.setChangedBy(1L);
			scCommitteeMember.setCreatedDate(new Timestamp(System
					.currentTimeMillis()));
			ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
			activity.setScQtCommittee(this.getManagedBean().getScQtCommittee());
			activity.setScCommitteeMember(scCommitteeMember);
			localList.add(activity);
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
		} catch (Exception e) {

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
		} catch (Exception e) {

		}
	}

}
