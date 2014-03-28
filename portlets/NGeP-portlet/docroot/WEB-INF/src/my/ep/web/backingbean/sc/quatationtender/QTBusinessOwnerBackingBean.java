package my.ep.web.backingbean.sc.quatationtender;

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

import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
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
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRole;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserNotification;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserOrgRole;
import my.ep.persistence.entity.pm.authorization.PmUserPreference;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "qTBusinessOwnerBackingBean")
@RequestScoped
public class QTBusinessOwnerBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {
	private static final Logger log = EpLogger
			.getLogger(QTBusinessOwnerBackingBean.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public QTBusinessOwnerBackingBean() {

		if (this.getManagedBean().getCommitteMember() == null) {
			this.getManagedBean().setCommitteMember(new ScCommitteeMember());
		}

		if (this.getManagedBean().getQt().getMinistrySpecSecrataryId() == null) {
			this.getManagedBean().getQt()
					.setMinistrySpecSecrataryId(new PmUser());
		}

		getMinistrySpecificationSecretaryList();
	}

	/**
	 * 
	 */
	public List<PmUser> getMinistrySpecificationSecretaryList() {
		List<PmUser> approverUserNames = this.getManagedBean()
				.getComitteeUserName();

		if (approverUserNames == null) {
			IUserSO userSO = null;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

				// Get Approver Users
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setRoleCode(RoleCode.MSC_COMMITTEE.value());

				UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
				approverUserNames = resDTO.getUserList();
				/*
				 * System.out.println("Approver List: " +
				 * approverUserNames.size());
				 */
				this.getManagedBean().setComitteeUserName(approverUserNames);

			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, ex
								.toString(), ex.getMessage()));
			} finally {
				userSO = null;
			}
		}
		return approverUserNames;
		/*
		 * List<SelectItem> selectItems = new ArrayList<SelectItem>();
		 * 
		 * try { IUserSO userSO =
		 * BusinessDelegate.getInstance().getServiceObject(
		 * ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class); List<PmUser>
		 * comitteeUsersUsers = new ArrayList<PmUser>(); if
		 * (getManagedBean().getComitteeUsers() == null) { UserReqDTO reqDTO =
		 * new UserReqDTO(); reqDTO.setRoleCode(RoleCode.MSC_COMMITTEE.value());
		 * UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
		 * comitteeUsersUsers = resDTO.getUserList(); } selectItems.add(new
		 * SelectItem(null, "--Select One--")); for (int i = 0; i <
		 * comitteeUsersUsers.size(); i++) { PmUser user = (PmUser)
		 * comitteeUsersUsers.get(i); selectItems.add(new
		 * SelectItem(user.getUserId(), user .getUserName())); }
		 * this.getManagedBean().setComitteeUsers(selectItems); } catch
		 * (Exception e) { e.printStackTrace(); } return
		 * getManagedBean().getComitteeUsers();
		 */
	}

	public void addBusinessOwner() {
		debugLogger("addBusinessOwner");
		List<ScCommitteeMember> committeMemebersList = this.getManagedBean()
				.getScCommitteeMemberList();
		if (committeMemebersList == null) {
			committeMemebersList = new ArrayList<ScCommitteeMember>();
		}
		// this.getManagedBean().setScCommitteeMemberList(null);
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		// committeeMember.setPmUser(new PmUser());
		committeeMember.setUserSelection("E");
		this.getManagedBean().setCommitteMember(committeeMember);
		// this.getManagedBean().setSelectedPmUser(new ArrayList<PmUser>());

		getSelectionTypes();
		// committeMemebersList.add(getManagedBean().getCommitteMember());
		getManagedBean().setScCommitteeMemberList(committeMemebersList);

		/*
		 * System.out.println("Add Business Owner : " +
		 * getManagedBean().getScCommitteeMemberList().size());
		 */
	}

	/**
	 * User selection types gov external new
	 */
	private void getSelectionTypes() {
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
		/* System.out.println("User Selection : " + userSelection); */

		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		this.getManagedBean().getScCommitteeMemberList()
				.remove(this.getManagedBean().getCommitteMember());
		committeeMember.setUserSelection(userSelection);
		this.getManagedBean().setCommitteMember(committeeMember);
		// this.getManagedBean().getScCommitteeMemberList().add(committeeMember);

		if (this.getManagedBean().getCommitteMember().getUserSelection()
				.equalsIgnoreCase(SelectionType.EXISITING.value())) {

			//getManagedBean().getCommitteMember().setExternalUser(true);
			// retriveExternalUsers();
		} else if (this.getManagedBean().getCommitteMember().getUserSelection()
				.equalsIgnoreCase(SelectionType.New.value())) {

			//getManagedBean().getCommitteMember().setExternalUser(true);
			// retriveAllMinstrys();
		}

	}

	public void cancelCommitteAction() {
		this.getManagedBean().getScCommitteeMemberList()
				.remove(this.getManagedBean().getCommitteMember());
	}

	public void onRowEditAction(RowEditEvent event) {
		ScCommitteeMember committeeMember = (ScCommitteeMember) event
				.getObject();
		getManagedBean().setCommitteMember(committeeMember);

	}

	public void onRowCancel(RowEditEvent event) {
		ScCommitteeMember member = (ScCommitteeMember) event.getObject();
		this.getManagedBean().getScCommitteeMemberList().remove(member);
	}

	public void onRowCloneAction(SelectEvent event) {
		ScQt scQt = (ScQt) event.getObject();
		
		//ScQtCommittee scQtCommittee = (ScQtCommittee) event.getObject();
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType("BO");
		reqDTO.setQtId(scQt.getQtId());
		CommitteResDTO resDTO = null;
		try {
			ICommitteSO committeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);

			resDTO = committeSO
					.retrieveCommitteMembersByQtIdAndCommType(reqDTO);
			
			List<ScCommitteeMember> commMembers = resDTO
					.getCommMemberResultBean().getResultList();
			for (ScCommitteeMember committeeMember : commMembers) {
				System.out.println(committeeMember.getMemberName() + "*****"
						+ committeeMember.getMemberRole() + "$$$"
						+ committeeMember.getScCommittee().getCommitteeType() + "consolidate : "
						+ committeeMember.getConsolidateValue() + "P&P : "
						+ committeeMember.isPNP());
			}
			this.getManagedBean().setCloneCommitteMemebers(commMembers);
			System.out.println("======== list size===="
					+ getManagedBean().getCloneCommitteMemebers().size());
			System.out.println("Email : " + getManagedBean().getCloneCommitteMemebers().get(0).getPmUser().getPmAddressTypes().get(0).getPmAddress().getEmail());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void consolidateSelect(SelectEvent event) {
		ScCommitteeMember member = (ScCommitteeMember) event.getObject();
		System.out.println("*******" + member.isConsolidate());
	}

	/**
	 * @return
	 */
	public void existingBusinessOwnerAction() {
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
			/*
			 * System.out.println("Size : " +
			 * resDto.getUserResultBean().getResultList().size());
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void cloneSearchCommitteAction() {
		try {
			QtnTenderSearchBean qtnTenderSearchBean = this.getManagedBean()
					.getCloneQtnTenderSearchBean();
			List<ScQt> cloneSearchQt = retriveQtCommittes(qtnTenderSearchBean);
			
			this.getManagedBean().setCloneBusinessTenderList(cloneSearchQt);
		} catch (Exception e) {

		}
		/*List<ScQtCommittee> qtCommittee = retriveQtCommittes();
		getManagedBean().setCloneSearchCommittes(qtCommittee);*/
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
			
			String procurementMode = qtnTenderSearchBean.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setAndOr(SearchCondition.CONDITION_OR.value());
				bean.setSearchField(QTConstants.PROCUREMENT_MODE);
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

				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromCreatedDate, toCreatedDate);

				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
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

	public void addCloneCommitteeMembersAction() {

		if (getManagedBean().getScCommitteeMemberList() == null) {
			getManagedBean().setScCommitteeMemberList(
					getManagedBean().getSelectedCommitteMemebers());
		} else {
			getManagedBean().getScCommitteeMemberList().addAll(
					getManagedBean().getSelectedCommitteMemebers());
		}
	}

	public void okBusinessOwnerAction() {
		debugLogger("okBusinessOwnerAction");
		IUserSO iUserSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		IParameterSO parameterSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		IStatusSO statusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		ScCommittee scCommittee = new ScCommittee();
		ScQt scQt = this.getManagedBean().getQt();
		
		// Setting the pm status for LOI details
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(1l);
		//StatusResDTO pmStatus = statusSO.retrieveStatusByStatusId(reqDTO);
		//PmStatus status = pmStatus.getPmStatus();
		StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
		PmStatus status = statusResDTO.getPmStatus();
		scQt.setPmStatus(status);

		List<ScCommitteeMember> memberList = getManagedBean()
				.getScCommitteeMemberList();

		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}

		/*
		 * if (memberList != null) { for (ScCommitteeMember member : memberList)
		 * { PmOrgProfile pmOrgProfile = member.getPmOrgProfile();
		 * 
		 * OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		 * orgProfileReqDTO.setOrgProfileId(1l); OrgProfileResDTO profileResDTO
		 * = orgProfileSO .retrieveOrgProfileByProfileId(orgProfileReqDTO);
		 * pmOrgProfile = profileResDTO.getPmOrgProfile();
		 * member.setPmOrgProfile(pmOrgProfile);
		 * 
		 * PmUser user = member.getPmUser(); if (user != null &&
		 * user.getUserId() != null) {
		 * 
		 * for (PmUser pmUser : getManagedBean().getSelectedPmUser()) {
		 * ScCommitteeMember committeeMember = new ScCommitteeMember();
		 * committeeMember.setMemberName(pmUser.getUserName());
		 * committeeMember.setIcPassport(pmUser.getIdentificationNo());
		 * committeeMember.setDesignation(pmUser.getDesignation());
		 * committeeMember.setGrade(pmUser.getGrade());
		 * committeeMember.setPnp(pmUser.isPMP());
		 * committeeMember.setPmUser(pmUser);
		 * committeeMember.setPmOrgProfile(pmUser.getPmUserOrgs().get(0)
		 * .getPmOrgProfile()); memberList.add(committeeMember); }
		 * this.getManagedBean().setScCommitteeMemberList(memberList);
		 * 
		 * UserReqDTO userReqDTO = new UserReqDTO();
		 * userReqDTO.setUserId(user.getUserId()); UserResDTO userResDTO =
		 * iUserSO .retrieveUserDetails(userReqDTO); user =
		 * userResDTO.getPmUser(); member.setPmUser(user); } else { if
		 * (member.isExternalUser() && (user != null && user.getUserId()
		 * ==null)) { ScCommitteeMember committeeMember = new
		 * ScCommitteeMember();
		 * committeeMember.setMemberName(getManagedBean().getCommitteMember()
		 * .getMemberName());
		 * committeeMember.setIcPassport(getManagedBean().getCommitteMember()
		 * .getIcPassport());
		 * committeeMember.setGrade(getManagedBean().getCommitteMember
		 * ().getGrade());
		 * committeeMember.setDesignation(getManagedBean().getCommitteMember()
		 * .getDesignation());
		 * committeeMember.setPnp(getManagedBean().getCommitteMember().isPnp());
		 * committeeMember
		 * .setEmail(getManagedBean().getCommitteMember().getEmail());
		 * 
		 * PmUser pmUser = new PmUser();
		 * pmUser.setUserName(member.getMemberName());
		 * pmUser.setLoginId(member.getMemberName());
		 * pmUser.setIdentificationNo(member.getIcPassport());
		 * pmUser.setCreatedBy(member.getCreatedBy());
		 * pmUser.setChangedBy(member.getChangedBy());
		 * pmUser.setCreatedDate(member.getCreatedDate());
		 * 
		 * ParameterReqDTO reqDTO = new ParameterReqDTO();
		 * 
		 * reqDTO.setParameterType(ParameterTypeConst.IDENTIFICATION_TYPES);
		 * reqDTO.setParameterCode(ParameterConst.IDENTIFICATION_TYPES_IC);
		 * 
		 * ParameterResDTO coderesDTO = parameterSO
		 * .retrieveParameterByTypeAndCode(reqDTO);
		 * pmUser.setIdentificationCode(coderesDTO .getPmParameter());
		 * pmUser.setOrgTypeCode(coderesDTO.getPmParameter()); UserReqDTO
		 * userReqDTO = new UserReqDTO(); userReqDTO.setPmUser(pmUser);
		 * UserResDTO resDTO = iUserSO .saveUser(userReqDTO); // TODO Need to
		 * write the code for pmuser member.setPmUser(resDTO.getPmUser()); } }
		 * memberList.add(member); } }
		 */

		if (getManagedBean().getCommitteMember().getUserSelection().equals("N")) {
			System.out.println("******** New User *********");
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
			member.setPmOrgProfile(scQt.getPmOrgProfile());

			PmUser user = new PmUser();
			user.setUserName(member.getMemberName());
			user.setLoginId(member.getMemberName());
			user.setIdentificationNo(member.getIcPassport());
			user.setPMP(member.isPNP());
			user.setEmail(member.getEmail());
			/*pmUser.setCreatedBy(member.getCreatedBy());
			pmUser.setChangedBy(member.getChangedBy());
			pmUser.setCreatedDate(member.getCreatedDate());*/

			// Identification Code
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterType(ParameterTypeConst.IDENTIFICATION_TYPES);
			parameterReqDTO.setParameterCode(ParameterConst.IDENTIFICATION_TYPES_IC);
			PmParameter identificationCode = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO).getPmParameter();
			user.setIdentificationCode(identificationCode);
			
			//pmUser.setPmAddressTypes(null);
			
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
			PmOrgProfile pmOrgProfile = orgProfileSO.retrieveOrgProfileListByOrgType(orgProfileReqDTO).getOrgProfileList().iterator().next();
			userOrg.setPmOrgProfile(pmOrgProfile);
			userOrg.setEffDate(new Timestamp(System.currentTimeMillis()));
			Timestamp expDate = new Timestamp(System.currentTimeMillis());
			expDate.setYear(1000);
			userOrg.setExpDate(expDate);
			List<PmUserOrgRole> pmUserOrgRoles = new ArrayList<PmUserOrgRole>();
			PmUserOrgRole userOrgRole = new PmUserOrgRole();
			userOrgRole.setPmUserOrg(userOrg);
			PmUserRole pmUserRole = new PmUserRole();
			pmUserRole.setPmUser(user);
			PmRole pmRole = new PmRole();
			pmRole.setRoleCode(RoleCode.PLAN_OFFICER.value());
			pmUserRole.setPmRole(pmRole);
			userOrgRole.setPmUserRole(pmUserRole);
			pmUserOrgRoles.add(userOrgRole);
			userOrg.setPmUserOrgRoles(pmUserOrgRoles);
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
			
			

			/*
			 * OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
			 * orgProfileReqDTO.setOrgProfileId(1l); OrgProfileResDTO
			 * profileResDTO = orgProfileSO
			 * .retrieveOrgProfileByProfileId(orgProfileReqDTO); pmOrgProfile =
			 * profileResDTO.getPmOrgProfile();
			 * member.setPmOrgProfile(pmOrgProfile);
			 */

			memberList.add(member);

			getManagedBean().setScCommitteeMemberList(memberList);
		} else {
			System.out.println("******** Existing User *********");

			for (PmUser user : getManagedBean().getSelectedPmUser()) {
				ScCommitteeMember committeeMember = new ScCommitteeMember();
				committeeMember.setMemberName(user.getUserName());
				committeeMember.setIcPassport(user.getIdentificationNo());
				committeeMember.setDesignation(user.getDesignation());
				committeeMember.setEmail(user.getPmAddressTypes().get(0).getPmAddress().getEmail());
				committeeMember.setGrade(user.getGrade());
				committeeMember.setPNP(user.isPMP());
				committeeMember.setPmUser(user);
				committeeMember.setPmOrgProfile(scQt.getPmOrgProfile());
				/*committeeMember.setPmOrgProfile(user.getPmUserOrgs().get(0)
						.getPmOrgProfile());*/
				committeeMember.setMemberRole("M");
				committeeMember.setAccessRight("E");
				memberList.add(committeeMember);
			}
			this.getManagedBean().setScCommitteeMemberList(memberList);
		}
		scCommittee.setScCommitteeMembers(memberList);
		for (ScCommitteeMember scCommitteeMember : memberList) {
			scCommitteeMember.setScCommittee(scCommittee);
		}

		scCommittee.setCommitteeType("BO");

		ScQtCommittee scQtCommittee = new ScQtCommittee();
		scQtCommittee.setScCommittee(scCommittee);
		scQtCommittee.setScQt(scQt);
		this.getManagedBean().setScQtCommittee(scQtCommittee);
	}

	public void deleteBusinessOwner() {
		debugLogger("deleteBusinessOwner");
		List<ScCommitteeMember> committeeMemberList = getManagedBean()
				.getScCommitteeMemberList();
		List<ScCommitteeMember> selectedList = getManagedBean()
				.getSelectScCommitteMember();

		if (committeeMemberList.size() == selectedList.size()) {
			committeeMemberList.clear();
		} else {
			for (ScCommitteeMember scCommitteeMember : selectedList) {
				committeeMemberList.remove(scCommitteeMember);
			}
		}
	}

	public void reset() {
		debugLogger("reset");
		this.getManagedBean().setName(null);
		this.getManagedBean().setIdentificationType(null);
		this.getManagedBean().setIdentificationNo(null);
	}

	public void resetClone() {
		debugLogger("resetClone");
		this.getManagedBean().setTitle(null);
		this.getManagedBean().setQtNo(null);
		this.getManagedBean().setProcurementMode(null);
		this.getManagedBean().setCreatedDtFrom(null);
		this.getManagedBean().setCreatedDtTo(null);
		this.getManagedBean().setStatus(null);

	}

	public void processChecked(AjaxBehaviorEvent event) {
		System.out.println("====enter process checked===="
				+ getManagedBean().getSelectedBoolean1());
		Boolean id = (Boolean) event.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
	}
}
