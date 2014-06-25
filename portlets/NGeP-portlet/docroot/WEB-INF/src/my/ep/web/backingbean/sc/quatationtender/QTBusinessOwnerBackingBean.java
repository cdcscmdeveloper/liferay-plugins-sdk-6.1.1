package my.ep.web.backingbean.sc.quatationtender;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
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
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
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

		if (this.getManagedBean().getQt().getMinistrySpecSecrataryId() == null) {
			this.getManagedBean().getQt()
					.setMinistrySpecSecrataryId(new PmUser());
		}

		getMinistrySpecificationSecretaryList();
	}

	private void getMinistrySpecificationSecretaryList() {
		IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		userReqDto.setRoleCode(RoleCode.MSC_COMMITTEE.value());
		UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
		List<PmUser> ministrySpecification = userResDTO.getUserList();
		getManagedBean().setComitteeUserName(ministrySpecification);
	}

	public void addBusinessOwner() {
		debugLogger("addBusinessOwner");
		/*
		 * List<ScCommitteeMember> committeMemebersList = this.getManagedBean()
		 * .getScCommitteeMemberList(); if (committeMemebersList == null) {
		 * committeMemebersList = new ArrayList<ScCommitteeMember>(); }
		 */
		getManagedBean().setExternalUsers(null);
		getManagedBean().setSelectedPmUser(null);
		if (getManagedBean().getCommitteMember() == null) {
			this.getManagedBean().setCommitteMember(new ScCommitteeMember());
		}
		// ScCommitteeMember committeeMember = new ScCommitteeMember();
		getManagedBean().getCommitteMember().setUserSelection("E");
		// this.getManagedBean().setCommitteMember(committeeMember);
		getSelectionTypes();
		// getManagedBean().setScCommitteeMemberList(committeMemebersList);
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
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		this.getManagedBean().getScCommitteeMemberList()
				.remove(this.getManagedBean().getCommitteMember());
		committeeMember.setUserSelection(userSelection);
		this.getManagedBean().setCommitteMember(committeeMember);

		if (this.getManagedBean().getCommitteMember().getUserSelection()
				.equalsIgnoreCase(SelectionType.Existing.value())) {

		} else if (this.getManagedBean().getCommitteMember().getUserSelection()
				.equalsIgnoreCase(SelectionType.New.value())) {

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

		// ScQtCommittee scQtCommittee = (ScQtCommittee) event.getObject();
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType("BO");
		reqDTO.setQtId(scQt.getQtId());
		CommitteResDTO resDTO = null;
		ICommitteSO committeSO;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);

			resDTO = committeSO
					.retrieveCommitteMembersByQtIdAndCommType(reqDTO);

			List<ScCommitteeMember> commMembers = resDTO
					.getCommMemberResultBean().getResultList();
			for (ScCommitteeMember committeeMember : commMembers) {
				System.out.println(committeeMember.getMemberName() + "*****"
						+ committeeMember.getMemberRole() + " $$$"
						+ committeeMember.getScCommittee().getCommitteeType()
						+ " consolidate : "
						+ committeeMember.getConsolidateValue() + " P&P : "
						+ committeeMember.isPNP() + " Email : "
						+ committeeMember.getEmail());
			}
			this.getManagedBean().setCloneCommitteMemebers(commMembers);
			System.out.println("======== list size==== : "
					+ getManagedBean().getCloneCommitteMemebers().size());
		} finally {
			committeSO = null;
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

		if (getManagedBean().getIdentificationType() != null
				&& !"".equalsIgnoreCase(getManagedBean()
						.getIdentificationType())) {
			SearchFilterBean idtypeSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			idtypeSearchBean.setSearchField("identificationCode.parameterCode");
			idtypeSearchBean.setSearchValue(getManagedBean()
					.getIdentificationType());
			searchBean.getDataFilterBeans().add(idtypeSearchBean);
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
		IUserSO userSO;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDto = userSO.searchUserProfile(userReqDTO);
			if (resDto != null) {
				this.getManagedBean().setExternalUsers(
						resDto.getUserResultBean().getResultList());
			}
		} finally {
			userSO = null;
		}
	}

	public void cloneSearchCommitteAction() {
		QtnTenderSearchBean qtnTenderSearchBean = this.getManagedBean()
				.getCloneQtnTenderSearchBean();
		List<ScQt> cloneSearchQt = retriveQtCommittes(qtnTenderSearchBean);

		this.getManagedBean().setCloneBusinessTenderList(cloneSearchQt);
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
				bean.setSearchField("scQt.procurementMode.parameterCode");
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
		} finally {
			detailSO = null;
		}

		return null != dto.getQtResultBean().getResultList() ? dto
				.getQtResultBean().getResultList() : new ArrayList<ScQt>();
	}

	public void addCloneCommitteeMembersAction() {
		debugLogger("addCloneCommitteeMembersAction");
		List<String> itemIds = new ArrayList<String>();
		if (getManagedBean().getScCommitteeMemberList().isEmpty()) {
			getManagedBean().setScCommitteeMemberList(
					getManagedBean().getSelectedCommitteMemebers());
		} else {
			for (ScCommitteeMember committee : this.getManagedBean()
					.getScCommitteeMemberList()) {
				itemIds.add(committee.getIcPassport());
			}
			for (ScCommitteeMember committees : getManagedBean()
					.getSelectedCommitteMemebers()) {
				if (itemIds.size() > 0) {
					if (itemIds.contains(committees.getIcPassport())) {

					} else {
						getManagedBean().getScCommitteeMemberList().addAll(
								getManagedBean().getSelectedCommitteMemebers());
					}
				}
			}

		}
	}

	public void cloneBusinessOwnerPopupAction() {
		debugLogger("cloneBusinessOwnerPopupAction");

		// getManagedBean().setSelectedCloneBusinessTender(new ScQt());
		// getManagedBean().setCloneCommitteMemebers(new
		// ArrayList<ScCommitteeMember>());
		getManagedBean().setSelectedCommitteMemebers(
				new ArrayList<ScCommitteeMember>());
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
		ScCommittee scCommittee = this.getManagedBean().getScQtCommittee()
				.getScCommittee();

		ScQt scQt = this.getManagedBean().getQt();

		// BO
		ParameterResDTO parameterResDTO;
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		parameterReqDTO.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
		parameterReqDTO
				.setParameterCode(ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER);
		parameterResDTO = parameterSO
				.retrieveParameterByTypeAndCode(parameterReqDTO);

		// Setting the pm status for New User
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(60730L);
		StatusResDTO statusResDTO = statusSO
				.retrieveStatusByStatusId(statusReqDTO);
		PmStatus status = statusResDTO.getPmStatus();
		scQt.setQtStatus(status);

		List<ScCommitteeMember> memberList = getManagedBean()
				.getScCommitteeMemberList();

		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}

		if (getManagedBean().getCommitteMember().getUserSelection().equals("N")) {
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
			user.setDesignation(member.getDesignation());
			user.setGrade(member.getGrade());
			user.setPMP(member.isPNP());
			user.setEmail(member.getEmail());

			// Identification Code
			// ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO
					.setParameterType(ParameterTypeConst.IDENTIFICATION_TYPES);
			parameterReqDTO
					.setParameterCode(ParameterConst.IDENTIFICATION_TYPES_IC);
			PmParameter identificationCode = parameterSO
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
			UserResDTO resDTO = iUserSO.saveUser(userReqDTO);
			System.out.println("done");
			member.setPmUser(resDTO.getPmUser());

			memberList.add(member);

			getManagedBean().setScCommitteeMemberList(memberList);
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
				committeeMember.setPmOrgProfile(scQt.getPmOrgProfile());
				/*
				 * committeeMember.setPmOrgProfile(user.getPmUserOrgs().get(0)
				 * .getPmOrgProfile());
				 */
				committeeMember.setMemberRole("M");
				committeeMember.setAccessRight("E");

				if (this.getManagedBean().getScCommitteeMemberList().isEmpty()) {
					this.getManagedBean().getScCommitteeMemberList()
							.add(committeeMember);
				} else {
					for (ScCommitteeMember committee : this.getManagedBean()
							.getScCommitteeMemberList()) {
						itemIds.add(committee.getIcPassport());
					}
					if (itemIds.size() > 0) {
						if (itemIds.contains(user.getIdentificationNo())) {

						} else {
							memberList.add(committeeMember);
							this.getManagedBean().setScCommitteeMemberList(
									memberList);
						}
					}
				}
			}
		}
		scCommittee.setScCommitteeMembers(memberList);
		PmStatus committeStatus = new PmStatus();
		committeStatus.setStatusId(60202l);
		scCommittee.setCommitteStatus(committeStatus);
		for (ScCommitteeMember scCommitteeMember : memberList) {
			scCommitteeMember.setScCommittee(scCommittee);
		}
		scCommittee.setCommitteeType(parameterResDTO.getPmParameter());
		ScQtCommittee scQtCommittee = this.getManagedBean().getScQtCommittee();
		scQtCommittee.setScCommittee(scCommittee);
		this.getManagedBean().setScQtCommittee(scQtCommittee);

		// BO validation
		RequestContext.getCurrentInstance().execute("newbusinessowner.hide()");
	}

	public void deleteBusinessOwner() {
		debugLogger("deleteBusinessOwner");
		List<ScCommitteeMember> committeeMemberList = getManagedBean()
				.getScCommitteeMemberList();
		List<ScCommitteeMember> selectedList = new ArrayList<ScCommitteeMember>();
		for (ScCommitteeMember committeeMember : committeeMemberList) {
			if (committeeMember.isSelected()) {
				selectedList.add(committeeMember);
			}
		}

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
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtTitle(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtNo(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setProcurementMode(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setFromCreatedDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setToCreatedDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean().setQtStatus(null);
	}

	public void processChecked(AjaxBehaviorEvent event) {
		System.out.println("====enter process checked===="
				+ getManagedBean().getSelectedBoolean1());
		Boolean id = (Boolean) event.getComponent().getAttributes()
				.get("value");
		System.out.println("=======get id value=====" + id);
	}

	/**
	 * Method to save the BO
	 * 
	 * @return
	 */
	public String saveBusinessOwnerAction() {
		IQTDetailSO detailSO;

		try {
			detailSO = getDetailSO();

			QuationTenderReqDTO quationTenderReqDTO = prepareBOInfo();
			QuationTenderResDTO quationTenderResDTO = new QuationTenderResDTO();

			quationTenderResDTO = (QuationTenderResDTO) detailSO
					.saveQTDtl(quationTenderReqDTO);
			this.getManagedBean().setQt(quationTenderResDTO.getQtDetail());

			if (quationTenderResDTO.isSuccessful()) {
				ScQt scQt = quationTenderResDTO.getQtDetail();
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
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_QUOTATION),
																scQt.getQtNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
			}
		} finally {
			detailSO = null;
		}
		return null;
	}

	/**
	 * Method to construct bo details for save or submit
	 * 
	 * @return
	 */
	private QuationTenderReqDTO prepareBOInfo() {
		ScQt scQt = this.getManagedBean().getQt();

		if (!scQt.isSpecApprovalReq()) {
			scQt.setMinistrySpecSecrataryId(null);
		}
		// No need to set the below fields automatically set by listener
		setUserDetails(scQt);

		// Setting the Business Owner details to Qtdetails
		if (null != this.getManagedBean().getScCommitteeMemberList()
				&& !this.getManagedBean().getScCommitteeMemberList().isEmpty()) {

			if (!isConsolidate(this.getManagedBean().getScCommitteeMemberList())) {

				throw new EpException(MessageConst.ERROR_100022,
						new String[] { "Consolidate", });

			}

		} else {
			throw new EpException(MessageConst.ERROR_100022,
					new String[] { "Business Owner", });

		}

		// Constructing reqDto
		QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		quationTenderReqDTO.setQtdetail(scQt);

		return quationTenderReqDTO;
	}

	/**
	 * @param scCommitteeMemberList
	 * @return
	 */
	private boolean isConsolidate(List<ScCommitteeMember> scCommitteeMemberList) {
		boolean flag = false;
		for (ScCommitteeMember committeeMember : scCommitteeMemberList) {
			if (committeeMember.isConsolidate()) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public String submitBusinessOwnerAction() {
		IQTDetailSO detailSO;
		try {
			detailSO = getDetailSO();

			QuationTenderReqDTO quationTenderReqDTO = prepareBOInfo();
			QuationTenderResDTO quationTenderResDTO = new QuationTenderResDTO();

			List<FacesMessage> messages = new ArrayList<FacesMessage>();
			for (Iterator<FacesMessage> iter = FacesContext
					.getCurrentInstance().getMessages(); iter.hasNext();) {
				messages.add(iter.next());
				// iter.remove();
			}
			if (messages.size() == 0) {
				FacesScopes facesScopes = new FacesScopes();
				String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
				quationTenderReqDTO.setToken(ctxToken);
				quationTenderReqDTO
						.setTaskId(this.getManagedBean().getTaskId());
				quationTenderReqDTO.setPayLoad(this.getManagedBean()
						.getQuotationTenderDataType());

				quationTenderResDTO = detailSO.submitQT(quationTenderReqDTO);
			}

			if (quationTenderResDTO.isSuccessful()) {
				ScQt scQt = quationTenderResDTO.getQtDetail();

				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_700026,
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_QUOTATION),
																scQt.getQtNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}

		} finally {
			detailSO = null;
		}
		return null;
	}

	public String createBusinessOwner() {
		ScQt scQt = new ScQt();
		// ScQt scQt = this.getManagedBean().getQt();

		scQt.setPmOrgProfile(getManagedBean().getOrgProfile());
		scQt.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		scQt.setMinistrySpecSecrataryId(new PmUser());
		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		QuationTenderResDTO dto = detailSO
				.retriveQTDetails(new QuationTenderReqDTO());
		ScQtCommittee qtCommitte = new ScQtCommittee();
		qtCommitte.setScQt(scQt);
		setUserDetails(qtCommitte);
		qtCommitte.setCommitteeActivities(null);
		qtCommitte.setQtCommitteRemarks(null);
		qtCommitte.setScQtMeetings(null);
		ScCommittee committee = new ScCommittee();
		PmStatus committeStatus = new PmStatus();
		committeStatus.setStatusId(60200l);
		committee.setCommitteStatus(committeStatus);
		committee.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
		qtCommitte.setScCommittee(committee);
		this.getManagedBean().setScQtCommittee(qtCommitte);
		this.getManagedBean().setScCommitteeMemberList(
				qtCommitte.getScCommittee().getScCommitteeMembers());
		this.getManagedBean().setCommitteMember(new ScCommitteeMember());
		PmStatus qtStatus = new PmStatus();
		qtStatus.setStatusId(QTStatusConst.QT_PENDING_APPOINT_BO);
		scQt.setQtStatus(qtStatus);
		scQt.setQtNo(dto.getQtNo());
		this.getManagedBean().setQt(scQt);

		return "/xhtml/sc/quatationtender/businessowner/businessownercreation.xhtml"
				+ "?faces-redirect=true";
	}

	private IQTDetailSO getDetailSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
	}

	public String cancelQTDetails() {
		FacesContext
				.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_INFO,
								null,
								MessageUtils
										.getMessage(
												MessageConst.INFO_700010,
												new String[] {
														getParameterDesc(
																ParameterTypeConst.DOCUMENT_TYPES,
																ParameterConst.DOCTYPE_QUOTATION),
														this.getManagedBean()
																.getQt()
																.getQtNo() },
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes()
														.getUserLocale())));
		return null;

	}

	public void consolidateRowSelectRadio(SelectEvent event) {
		ScCommitteeMember selCommitteeMember = (ScCommitteeMember) event
				.getObject();

		for (ScCommitteeMember committeeMember : getManagedBean()
				.getScCommitteeMemberList()) {
			if (committeeMember.equals(selCommitteeMember)) {
				committeeMember.setConsolidate(true);
			} else {
				committeeMember.setConsolidate(false);
			}
		}
	}

	public boolean getConsolidateSelectAll() {
		int selectedCount = 0;
		List<ScCommitteeMember> scCommitteeMemberList = getManagedBean()
				.getScCommitteeMemberList();

		if (scCommitteeMemberList != null) {
			for (ScCommitteeMember committeeMember : scCommitteeMemberList) {
				if (committeeMember.isSelected()) {
					selectedCount++;
				}
			}

			return scCommitteeMemberList.size() == selectedCount;
		} else {
			return false;
		}
	}
}
