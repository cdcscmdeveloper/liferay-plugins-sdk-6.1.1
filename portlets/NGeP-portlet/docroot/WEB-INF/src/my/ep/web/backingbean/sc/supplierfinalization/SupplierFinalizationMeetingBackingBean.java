/**
 * 
 */
package my.ep.web.backingbean.sc.supplierfinalization;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
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
import my.ep.constant.sc.QTStatusConst;
import my.ep.constants.CommonConstants;
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
import my.ep.factory.EpLogger;
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
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.loa.LOADetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.supplierfinalization.SupplierFinalizationMeetingManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pp.common.CommonMethod;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * @author Faradiana
 * 
 */

@ManagedBean(name = "suppFinalMeetingBackingBean")
@RequestScoped
public class SupplierFinalizationMeetingBackingBean extends SourcingBaseBackingBean<SupplierFinalizationMeetingManagedBean> {
	
	public static Logger log = EpLogger.getLogger(LOADetailBackingBean.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
	
	String committeeType = ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A;
	
	public SupplierFinalizationMeetingBackingBean() {		
		if (this.getManagedBean().getCloneQtnTenderSearchBean() == null) {
			this.getManagedBean().setCloneQtnTenderSearchBean(new QtnTenderSearchBean());
		}
		if (this.getManagedBean().getCommitteMember() == null) {
			this.getManagedBean().setCommitteMember(new ScCommitteeMember());
		}
	}
	
	public void preRenderView() {
		debugLogger("preRenderView");

		try {
			SCEvaluationDataType dataType = getBpmTaskDetails(SCEvaluationDataType.class);

			Long qtId = Long.valueOf(dataType.getQtId());
			
			//Long qtId = 146L;

			retriveQTDetails(qtId);
			retriveMeetingDetails(qtId);

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.FC_COORDINATE_MEETING_PAGE);
		} catch (Exception e) {
			handleException(e);
		}
	}

	//Start Header - QT Details
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
	
	
	//Start Meeting Details
	public void retriveMeetingDetails(Long qtId) {
		IQTMeetingSO qtMeetingSO;
		try {
			qtMeetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setCommitteType(committeeType);
			MeetingResDTO resDTO = qtMeetingSO.retrieveMeetingNCommitteDetiails(reqDTO);

			if (resDTO.getMeetingDtls() != null) {
				getManagedBean().setScQtMeetings(resDTO.getMeetingDtls());
			}
			
			if (resDTO.getCommitteMembers() != null && !resDTO.getCommitteMembers().isEmpty()) {
				getManagedBean().setCommitteeMemberActivity(resDTO.getCommitteMembers());
			}

			List<ScQtCommitteRemarks> qtRemarkList = new ArrayList<ScQtCommitteRemarks>();
			this.getManagedBean().setQtCommitteeRemarks(new ArrayList<ScQtCommitteRemarks>());
			
			ScQtCommittee committee = resDTO.getQtCommittee();
			if (null == committee) {
				committee = new ScQtCommittee();
			}
			getManagedBean().setScQtCommittee(committee);
			
			
			List<ScQtCommitteRemarks> remarks = committee.getQtCommitteRemarks();
			if (remarks != null && !remarks.isEmpty()) {
				for (ScRemark remark : remarks) {
					ScQtCommitteRemarks qtRemark = (ScQtCommitteRemarks) remark;
					qtRemarkList.add(qtRemark);
				}
				this.getManagedBean().setQtCommitteeRemarks(qtRemarkList);
			}
			initializeNewRemarks();
		} finally {
			qtMeetingSO = null;
		}
	}

	private void initializeNewRemarks() {
		FacesScopes facesScopes = new FacesScopes();
		
		ScQtCommitteRemarks remark = new ScQtCommitteRemarks();
		setUserDetails(remark);
		remark.setCreatedUser(facesScopes.getUserSessionBean());
		remark.setScQtCommittee(getManagedBean().getScQtCommittee());
		
		PmStatus status = getRetrieveStatusById(QTStatusConst.ACTIVE_STATUS);
		remark.setPmStatus(status);
		
		if(getManagedBean().getQtCommitteeRemarks() == null){
			getManagedBean().setQtCommitteeRemarks(new ArrayList<ScQtCommitteRemarks>());
		}
		getManagedBean().getQtCommitteeRemarks().add(remark);
	}
	
	public void meetingAddAction() {
		try {
			List<ScQtMeeting> meetingList = getManagedBean().getScQtMeetings();
			if (null == meetingList) {
				getManagedBean().setScQtMeetings(new ArrayList<ScQtMeeting>());
			}

			ScQtMeeting meeting = new ScQtMeeting();
			meeting.setQtCommittee(this.getManagedBean().getScQtCommittee());
			setUserDetails(meeting);

			PmStatus status = getRetrieveStatusById(QTStatusConst.ACTIVE_STATUS);
			meeting.setMeetingStatus(status);
			
			meetingList.add(meeting);
			this.getManagedBean().setScQtMeetings(meetingList);
		} finally{
			
		}
	}
	
	public void selectMeetingEvent(SelectEvent event) {
		ScQtMeeting scQtMeeting = (ScQtMeeting) event.getObject();
		if(getManagedBean().getMeetingMultiSelect() == null){
			getManagedBean().setMeetingMultiSelect(new ArrayList<ScQtMeeting>());
		}
		getManagedBean().getMeetingMultiSelect().add(scQtMeeting);
	}
	
	public void unSelectMeetingEvent(SelectEvent event) {
		ScQtMeeting scQtMeeting = (ScQtMeeting) event.getObject();
		getManagedBean().getMeetingMultiSelect().remove(scQtMeeting);
	}
	
	public void meetingDeleteAction() {
		try {
			List<ScQtMeeting> meetingList = getManagedBean().getScQtMeetings();
			List<ScQtMeeting> selectedMeetingList = getManagedBean().getMeetingMultiSelect();
			if (meetingList.size() == selectedMeetingList.size()) {
				meetingList.clear();
			} else {
				for (ScQtMeeting meeting : selectedMeetingList)
					meetingList.remove(meeting);
			}
		} finally {
		}
	}
//End - Meeting Details
	
	
//Start - Committee Details
	public void addPopupAction() {
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		committeeMember.setUserSelection(SelectionType.Existing.value());
		
		this.getManagedBean().setCommitteMember(committeeMember);
		getSelectionTypes();
	}
	
	public void getSelectionTypes() {
		SelectionType[] type = SelectionType.values();
		List<SelectItem> selectionTypes = new ArrayList<SelectItem>();

		for (SelectionType selectionType : type) {
			selectionTypes.add(new SelectItem(selectionType.value(), selectionType.name()));
		}
		this.getManagedBean().setSelections(selectionTypes);
	}
	
	public SelectItem[] getRoleTypeDropdownValues(String committeeRole) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
		
		searchBean.getDataFilterBeans().add( new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		
		SelectItem[] items = getDropdownValuesBySearchBean(ParameterTypeConst.COMMITTEE_ROLE, searchBean);
		return items;
	}
	
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
			// getManagedBean().getCommitteMember().setExternalUser(true);

		} else if (SelectionType.New.value().equalsIgnoreCase(getManagedBean().getCommitteMember().getUserSelection())) {
			committeeMember.setMemberRole(new PmParameter());
			// getManagedBean().getCommitteMember().setExternalUser(true);
		}
		this.getManagedBean().setCommitteMember(committeeMember);
	}
	
	public void committeeSearchAction() {
		SearchBean<PmUser> searchBean = new SearchBean<PmUser>();
		if (getManagedBean().getName() != null && !"".equalsIgnoreCase(getManagedBean().getName())) {
			SearchFilterBean usernameSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
			usernameSearchBean.setSearchField("userName");
			usernameSearchBean.setSearchValue(getManagedBean().getName());
			searchBean.getDataFilterBeans().add(usernameSearchBean);

		}
		if (getManagedBean().getIdentificationType() != null && !"".equalsIgnoreCase(getManagedBean().getIdentificationType())) {
			SearchFilterBean idtypeSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
			idtypeSearchBean.setSearchField("identificationCode.parameterCode");
			idtypeSearchBean.setSearchValue(getManagedBean().getIdentificationType());
			searchBean.getDataFilterBeans().add(idtypeSearchBean);
		}
		if (getManagedBean().getIdentificationNo() != null && !"".equalsIgnoreCase(getManagedBean().getIdentificationNo())) {
			SearchFilterBean idNoSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
			idNoSearchBean.setSearchField("identificationNo");
			idNoSearchBean.setSearchValue(getManagedBean().getIdentificationNo());
			searchBean.getDataFilterBeans().add(idNoSearchBean);
		}
		
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setUserSearchBean(searchBean);
		IUserSO userSO;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserResDTO resDTO = userSO.searchUserProfile(userReqDTO);
			if (resDTO != null) {
				this.getManagedBean().setExternalUsers(resDTO.getUserResultBean().getResultList());
				for (PmUser user : this.getManagedBean().getExternalUsers()) {
					if (user.getMemberRole() == null) {
						user.setMemberRole(new PmParameter());
					}
					if (user.isPMP()) {
						PmParameter memberRole = getPmParameterByTypeAndCode(ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
						user.setMemberRole(memberRole);
					}
				}
			}
		} finally {
			userSO = null;
		}
	}
	
	public void committeeResetAction() {
		this.getManagedBean().setName(null);
		this.getManagedBean().setIdentificationType(null);
		this.getManagedBean().setIdentificationNo(null);
	}

	public void committeeOkAction() {
		List<ScQtCommitteeActivity> committeeActivity = getManagedBean().getCommitteeMemberActivity();
		if (null == committeeActivity) {
			committeeActivity = new ArrayList<ScQtCommitteeActivity>();
		}

		if (SelectionType.New.value().equalsIgnoreCase(getManagedBean().getCommitteMember().getUserSelection())) {
			ScQtCommitteeActivity activity = addNewUser();
			if(activity != null){
				committeeActivity.add(activity);
				getManagedBean().setCommiteeChanged(true);
			}
		} else {
			List<ScQtCommitteeActivity> activityList = addExistingUser();
			if(activityList != null){
				committeeActivity.addAll(activityList);
				getManagedBean().setCommiteeChanged(true);
			}
		}
		this.getManagedBean().setCommitteeMemberActivity(committeeActivity);
		
		// validation
		RequestContext.getCurrentInstance().execute("newMemberWidgetVar.hide()");
	}
	
	public void committeeDeleteAction() {
		try {
			List<ScQtCommitteeActivity> committeeList = getManagedBean().getCommitteeMemberActivity();
			List<ScQtCommitteeActivity> selectedCommitteeList = getManagedBean().getCommitteeActivitySelect();
			if (committeeList.size() == selectedCommitteeList.size()) {
				committeeList.clear();
			} else {
				for (ScQtCommitteeActivity committeeMember : selectedCommitteeList){
					committeeList.remove(committeeMember);
				}
			}
		} finally {
		}
	}
	
	private ScQtCommitteeActivity addNewUser (){
		//ScCommittee scCommittee = this.getManagedBean().getScCommittee();
		ScQt scQt = this.getManagedBean().getScQt();
		
		ScCommitteeMember member = new ScCommitteeMember();
		member.setMemberName(getManagedBean().getCommitteMember().getMemberName());
		member.setIcPassport(getManagedBean().getCommitteMember().getIcPassport());
		member.setGrade(getManagedBean().getCommitteMember().getGrade());
		member.setDesignation(getManagedBean().getCommitteMember().getDesignation());
		member.setPNP(getManagedBean().getCommitteMember().isPNP());
		member.setEmail(getManagedBean().getCommitteMember().getEmail());
		member.setMemberRole(getPmParameterByTypeAndCode(ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_MEMBER));
		member.setAccessRight("E");
		member.setScCommittee(this.getManagedBean().getScQtCommittee().getScCommittee());
		setUserDetails(member);
		member.setPmOrgProfile(scQt.getPmOrgProfile());
		//member.setScCommittee(scCommittee);

		PmUser user = userDetails(member);
		member.setPmUser(user);
		
		ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
		activity.setScQtCommittee(this.getManagedBean().getScQtCommittee());
		activity.setScCommitteeMember(member);
		
		return activity;
	}
	
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
		// pmUser.setPmAddressTypes(null);
		
		PmCountry pmCountry = new PmCountry();
		pmCountry.setCountryId(1L);//FIXME
		user.setPmCountry(pmCountry);
		
		// User Notification
		List<PmUserNotification> pmUserNotifications = userNotificationDetails(user);
		user.setPmUserNotifications(pmUserNotifications);

		// User Orgs
		List<PmUserOrg> pmUserOrgs = userOrgDetails(user);
		user.setPmUserOrgs(pmUserOrgs);
		user.setOrgTypeCode(pmUserOrgs.get(0).getPmOrgProfile().getOrgTypeCode());

		// User Preference
		PmUserPreference pmUserPreference = userPreferenceDetails(user);
		user.setPmUserPreference(pmUserPreference);

		//Save new Member
		PmUser newUser = saveNewUser(user);
		return newUser;
	}
	
	private List<PmUserNotification> userNotificationDetails(PmUser user){
		List<PmUserNotification> pmUserNotifications = new ArrayList<PmUserNotification>();
		PmUserNotification pmUserNotification = new PmUserNotification();
		pmUserNotification.setPmUser(user);
		pmUserNotification.setNotifyMode("E");
		pmUserNotification.setPreferred(true);
		pmUserNotifications.add(pmUserNotification);
		return pmUserNotifications;
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
	
	private PmUserPreference userPreferenceDetails(PmUser user){
		PmUserPreference pmUserPreference = new PmUserPreference();
		pmUserPreference.setPmUser(user);
		pmUserPreference.setFormatDate(CommonConstants.DEFAULT_DATE_FORMAT);
		pmUserPreference.setFormatTime(CommonConstants.DEFAULT_TIME_FORMAT);
		pmUserPreference.setFormatNumber(CommonConstants.DEFAULT_NUMBER_FORMAT);
		pmUserPreference.setPreferredLanguage(CommonConstants.DEFAULT_LANGUAGE_CODE);
		pmUserPreference.setNotifyRemovalDay(30);
		return pmUserPreference;
	}
	
	private PmUser saveNewUser(PmUser user){
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setOrgType(OrganizationType.OTHERS.value());
		userReqDTO.setPmUser(user);
		
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserResDTO resDTO = userSO.saveUser(userReqDTO);
		return resDTO.getPmUser();
	}
	
	private List<ScQtCommitteeActivity> addExistingUser (){
		List<ScQtCommitteeActivity> activityList = new ArrayList<ScQtCommitteeActivity>();
		ScQt scQt = this.getManagedBean().getScQt();
		
		for (PmUser user : getManagedBean().getSelectedPmUser()) {
			ScCommitteeMember committeeMember = new ScCommitteeMember();
			committeeMember.setMemberName(user.getUserName());
			committeeMember.setIcPassport(user.getIdentificationNo());
			committeeMember.setDesignation(user.getDesignation());
			committeeMember.setEmail(user.getEmail());
			committeeMember.setGrade(user.getGrade());
			committeeMember.setPNP(user.isPMP());
			committeeMember.setPmUser(user);
			committeeMember.setPmOrgProfile(scQt.getPmOrgProfile());
			
			//committeeMember.setMemberRole(getManagedBean().getRoleName());
			/*if(user.getMemberRole() != null){
				PmParameter pmParameter = getPmParameterByTypeAndCode(ParameterTypeConst.COMMITTEE_ROLE, user.getMemberRole().getParameterCode());
				committeeMember.setMemberRole(pmParameter);	
			}*/
			PmParameter pmParameter = getPmParameterByTypeAndCode(ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_MEMBER);
			committeeMember.setMemberRole(pmParameter);
			committeeMember.setAccessRight("E");
			committeeMember.setScCommittee(this.getManagedBean().getScQtCommittee().getScCommittee());
			setUserDetails(committeeMember);
			// committeeMember.setScCommittee(scCommittee);
			
			ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
			activity.setScQtCommittee(this.getManagedBean().getScQtCommittee());
			activity.setScCommitteeMember(committeeMember);
			activityList.add(activity);
		}
		return activityList;
	}
//End - Committee Details
	

//Start - Committee Clone 
	public void clonePopupAction() {
		QtnTenderSearchBean cloneQtnTenderSearchBean = new QtnTenderSearchBean();
		IPagingDataModel<ScQt> cloneSearchQTDataModel = new cloneSearchQTDataModel("cloneCommitteeSearchTbl", cloneQtnTenderSearchBean);
		getManagedBean().setCloneSearchQTDataModel(cloneSearchQTDataModel);
		getManagedBean().setCloneQtnTenderSearchBean(cloneQtnTenderSearchBean);
	}
	
	public void cloneSearchAction() {
		getManagedBean().getCloneSearchQTDataModel().reloadData();
	}
	
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
	
	public void onRowCloneAction(SelectEvent event) {
		ScQt scQt = (ScQt) event.getObject();
		ICommitteSO committeSO = null;
		IQTDetailSO qtDetailSO = null;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			
			Long qtId = getManagedBean().getScQt().getQtId();
			
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(qtId);
			QuationTenderResDTO resDTO = qtDetailSO.retriveQTDetails(reqDTO);
			
			if(resDTO.isSuccessful()){
				committeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				CommitteReqDTO committeeReqDTO = new CommitteReqDTO();
				committeeReqDTO.setQtId(scQt.getQtId());
				committeeReqDTO.setCommitteeType(resDTO.getFinalizationCommittee().getParameterCode());
				
				CommitteResDTO committeeResDTO = committeSO.retrieveCommitteMembersByQtIdAndCommType(committeeReqDTO);
				if (committeeResDTO != null) {
					this.getManagedBean().setCloneCommitteMembers(committeeResDTO.getCommMemberResultBean().getResultList());
				}
			}
		} finally {
			committeSO = null;
		}
	}
	
	public void cloneAction() {
		List<ScQtCommitteeActivity> committeeMemberList = this.getManagedBean().getCommitteeMemberActivity();
		if (committeeMemberList == null) {
			committeeMemberList = new ArrayList<ScQtCommitteeActivity>();
		}
		List<ScCommitteeMember> cloneCommitteeList = this.getManagedBean().getCloneCommitteMembersSelect();
		if (cloneCommitteeList == null) {
			cloneCommitteeList = new ArrayList<ScCommitteeMember>();
		}

		List<ScQtCommitteeActivity> localList = new ArrayList<ScQtCommitteeActivity>();
		List<String> itemIds = new ArrayList<String>();
		if (committeeMemberList.isEmpty()) {
			for (ScCommitteeMember scCommitteeMember : cloneCommitteeList) {
				scCommitteeMember.setScCommittee(this.getManagedBean().getScQtCommittee().getScCommittee());
				setUserDetails(scCommitteeMember);

				ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
				activity.setScQtCommittee(this.getManagedBean().getScQtCommittee());
				activity.setScCommitteeMember(scCommitteeMember);
				localList.add(activity);
				getManagedBean().setCommiteeChanged(true);
			}
		} else {
			for (ScQtCommitteeActivity committee : committeeMemberList) {
				itemIds.add(committee.getScCommitteeMember().getIcPassport());
			}
			for (ScCommitteeMember members : cloneCommitteeList) {
				if (itemIds.size() > 0) {
					if (!itemIds.contains(members.getIcPassport())) {
						members.setCommitteeMemberId(null);
						members.setScCommittee(this.getManagedBean().getScQtCommittee().getScCommittee());
						setUserDetails(members);

						ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
						activity.setScQtCommittee(this.getManagedBean().getScQtCommittee());
						activity.setScCommitteeMember(members);
						localList.add(activity);
						getManagedBean().setCommiteeChanged(true);
					}
				}
			}
		}
		committeeMemberList.addAll(localList);
		this.getManagedBean().setCommitteeMemberActivity(committeeMemberList);
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
				QuationTenderResDTO resDTO = detailSO.getQuationTenders(reqDTO);
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
				qtSearchBean.getDataFilterBeans().add(new SearchFilterBean("scQtStatus.pmStatus.statusId", SearchOperator.OPERATOR_EQUALS.value(), status));
				qtSearchBean.getDataFilterBeans().add(new SearchFilterBean("scQtStatus.current", SearchOperator.OPERATOR_EQUALS.value(), true));
			}
			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}
//End - Committee Clone 
	

	public void saveAction() {
		try {
			ScQtCommittee scQtCommittee = this.getManagedBean().getScQtCommittee();
			List<ScQtCommitteeActivity> scQtCommitteeActivity = this.getManagedBean().getCommitteeMemberActivity();
			
			if(getManagedBean().isCommiteeChanged()){
				//create new committee and suspend old one
				ScCommittee oldScCommittee = scQtCommittee.getScCommittee();
				oldScCommittee.setRecordStatus(false);
				
				ScCommittee newScCommittee = new ScCommittee();
				newScCommittee.setCommitteeType(oldScCommittee.getCommitteeType());
				PmStatus status = new PmStatus();
				status.setStatusId(QTStatusConst.CM_COMPLETE);
				newScCommittee.setCommitteStatus(status);
				setUserDetails(newScCommittee);
				
				scQtCommittee.setScCommittee(newScCommittee);
				
				for(ScQtCommitteeActivity activity : scQtCommitteeActivity){
					ScCommitteeMember member = activity.getScCommitteeMember();
					member.setCommitteeMemberId(null);
					member.setScCommittee(newScCommittee);
				}
			}
			
			if (scQtCommitteeActivity != null && !scQtCommitteeActivity.isEmpty()) {
				scQtCommittee.setCommitteeActivities(scQtCommitteeActivity);
			}
			
			List<ScQtMeeting> meetingList = this.getManagedBean().getScQtMeetings();
			if (meetingList != null && !meetingList.isEmpty()) {
				scQtCommittee.setScQtMeetings(meetingList);
			}
			
			List<ScQtCommitteRemarks> remarksList=	this.getManagedBean().getQtCommitteeRemarks();
			List<ScQtCommitteRemarks> committeeRemarks = null;
			if (isNotNullOREmpty(remarksList)) {
				committeeRemarks = new ArrayList<ScQtCommitteRemarks>();
				for (ScQtCommitteRemarks remarks : remarksList) {
					if (remarks.getRemark() != null && "" != remarks.getRemark()) {
						committeeRemarks.add(remarks);
					}
				}
			}
			scQtCommittee.setQtCommitteRemarks(committeeRemarks);
			scQtCommittee.setMeetingAttendances(null);
			setUserDetails(scQtCommittee);
			
			MeetingReqDTO metReqDtO = new MeetingReqDTO();
			metReqDtO.setScQtCommitte(scQtCommittee);
		
			IQTMeetingSO qtMeetingSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQTMeetingSO.class);
			MeetingResDTO meetingResDTO = qtMeetingSO.saveQTDtl(metReqDtO);
			
			retriveMeetingDetails(meetingResDTO.getQtCommittee().getScQt().getQtId());
			FacesContext.getCurrentInstance().addMessage( null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Supplier FInalization Meeting Saved Successfully", ""));
		} finally {

		}
	}

	public void submitAction() {
		String variable1 = getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION) + " " + getManagedBean().getScQt().getQtNo();
		String variable2 = CommonMethod.getMessageCommon("lbl.sc.meeting.confirmation");
		commonMessageAcknowledmentPage(MessageConst.INFO_700026, variable1, variable2);
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
	
	private PmStatus getRetrieveStatusById(Long statusId){
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
	
	private PmParameter getPmParameterById(Long parameterId) {
		IParameterSO parameterSO = null;
		try {
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			return resDTO.getPmParameter();
		} finally {
			parameterSO = null;
		}
	}
	
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
