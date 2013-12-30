package my.ep.web.backingbean.ss.committee;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.ss.committe.ICommitteSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.ss.committe.CommitteReqDTO;
import my.ep.dto.ss.committe.CommitteResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.committeedata.SCCommitteeDataType;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.ss.quationtender.ScCommittee;
import my.ep.persistence.entity.ss.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCommittee;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.ss.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.ss.committee.CommitteManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.type.ss.quationtender.ApprovalStatus;
import my.ep.web.type.ss.quationtender.CommitteType;
import my.ep.web.type.ss.quationtender.IdentificationType;
import my.ep.web.type.ss.quationtender.MemberRole;
import my.ep.web.type.ss.quationtender.MemberType;
import my.ep.web.type.ss.quationtender.SelectionType;
import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;
import org.primefaces.event.RowEditEvent;

import com.liferay.portal.util.PortalUtil;

/**
 * @author VidyasagarReddy
 * 
 */
@ManagedBean(name = "committeBackingBean")
@RequestScoped
public class CommitteBackingBean extends
		SourcingBaseBackingBean<CommitteManagedBean> {

	private static final Logger logger = EpLogger
			.getLogger(CommitteBackingBean.class);

	public CommitteBackingBean() {
		retriveCommitteTypes();
		if (this.getManagedBean().getOutComes() == null) {
			this.getManagedBean().setOutComes(
					new String[] { BpmOutcomeConst.BPM_SAVE,
							BpmOutcomeConst.BPM_SUBMIT });
		}
	}

	public String invokeCommitteAction(String action) {

		String page = null;
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
			page = saveCommitteDetails();
		} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
			page = submitCommitteDetails();

		} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
			page = updateCommitteBpmTask(action);
		} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
			page = updateCommitteBpmTask(action);
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "/xhtml/shared/acknowledge.xhtml");
		return page;

	}

	private String updateCommitteBpmTask(String action) {

		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtCommittee(this.getManagedBean().getQtCommittee());
		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		committeReqDTO.setToken(ctxToken);
		committeReqDTO.setTaskId(this.getManagedBean().getTaskId());
		committeReqDTO.setOutcome(action);
		ICommitteSO commso = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(QTConstants.SS_MODULE, ICommitteSO.class);
		try {
			commso.updateBPMTask(committeReqDTO);
			if (true) {

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.",
								"Committe Type. : "
										+ this.getManagedBean()
												.getScCommittee()
												.getCommitteeType()));

				return "/xhtml/shared/acknowledge.xhtml";

			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Sorry For the inconvinence", ""));
		}
		return null;

	}

	/**
	 * Method to save the committe details.
	 */
	public String saveCommitteDetails() {
		// TODO Need to get the committe object from managed bean.

		CommitteReqDTO committeReqDTO = constructSaveCommitte();
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(QTConstants.SS_MODULE, ICommitteSO.class);
		try {
			CommitteResDTO resDTO = committeSO
					.saveOrUpdateCommitte(committeReqDTO);
			if (resDTO.isSuccessful()) {

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.",
								"Committe Type. : "
										+ this.getManagedBean()
												.getScCommittee()
												.getCommitteeType()));

				return "/xhtml/shared/acknowledge.xhtml";

			} else {
				throw new EpException("");
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Sorry For the inconvinence", ""));
		}
		return null;
	}

	/**
	 * Method to save and submit the data to BPM
	 */
	public String submitCommitteDetails() {

		CommitteReqDTO committeReqDTO = constructSaveCommitte();
		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		committeReqDTO.setToken(ctxToken);
		ICommitteSO commso = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(QTConstants.SS_MODULE, ICommitteSO.class);
		CommitteResDTO resDTO = null;

		try {
			resDTO = commso.submitCommitteeDetails(committeReqDTO);
			if (resDTO.isSuccessful()) {

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.",
								"Committe Type. : "
										+ this.getManagedBean()
												.getScCommittee()
												.getCommitteeType()));

				return "/xhtml/shared/acknowledge.xhtml";

			} else {
				throw new EpException("");
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Sorry For the inconvinence", ""));
		}
		return null;
	}

	private CommitteReqDTO constructSaveCommitte() {

		Long currentUser = new FacesScopes().getUserSessionBean().getUserId();
		// ScQtcommitte date setting
		ScQtCommittee scQtCommittee = this.getManagedBean().getQtCommittee();
		scQtCommittee.setCreatedBy(currentUser);
		scQtCommittee.setChangedBy(currentUser);
		scQtCommittee.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		scQtCommittee.setRecordStatus(true);

		// SCcommitteData setting
		ScCommittee committee = this.getManagedBean().getScCommittee();
		committee.setCreatedBy(currentUser);
		committee.setChangedBy(currentUser);
		committee.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		committee.setRecordStatus(true);

		// Sccommittemembers info
		List<ScCommitteeMember> committeMembers = this.getManagedBean()
				.getCommittemembers();
		List<ScCommitteeMember> commMembers = new ArrayList<ScCommitteeMember>();
		if (committeMembers != null && !committeMembers.isEmpty()) {
			for (ScCommitteeMember comMember : committeMembers) {
				comMember.setScCommittee(committee);
				comMember.setCreatedBy(currentUser);
				comMember.setChangedBy(currentUser);
				comMember.setCreatedDate(new Timestamp(System
						.currentTimeMillis()));
				comMember.setRecordStatus(true);
				comMember.setMemberType("T");
				commMembers.add(comMember);

			}
		}
		committee.setScCommitteeMembers(commMembers);
		// Need to change this status based on qt status
		committee.setStatusId(1);

		scQtCommittee.setScCommittee(committee);

		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setToken(ctxToken);
		committeReqDTO.setQtCommittee(scQtCommittee);
		/*
		 * ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
		 * .getServiceObject(QTConstants.SS_MODULE, ICommitteSO.class);
		 * CommitteResDTO resDTO =
		 * committeSO.saveOrUpdateCommitte(committeReqDTO);
		 */

		return committeReqDTO;
	}

	/**
	 * To search committes
	 */
	public void sarchQtCommitte() {
		List<ScQtCommittee> qtCommittee = retriveQtCommittes();
		this.getManagedBean().setQtcommittees(qtCommittee);
		this.getManagedBean().setCloneSearchCommittes(null);
	}

	public void cloneSearchCommitteAction() {
		List<ScQtCommittee> qtCommittee = retriveQtCommittes();
		getManagedBean().setCloneSearchCommittes(qtCommittee);
	}

	/**
	 * retrive qtcommittees
	 * 
	 * @return
	 */
	private List<ScQtCommittee> retriveQtCommittes() {
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		ISearchBean<ScQtCommittee> searchBean = new SearchBean<ScQtCommittee>();
		CommitteResDTO resDto = null;

		if (getManagedBean().getCommitteNo() != null
				&& !"".equalsIgnoreCase(getManagedBean().getCommitteNo())) {
			SearchFilterBean committeSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			committeSearchBean.setSearchField("scCommittee.committeeId");
			committeSearchBean.setSearchValue(getManagedBean().getCommitteNo());
			searchBean.getDataFilterBeans().add(committeSearchBean);

		}

		if (getManagedBean().getSelectedCommitte() != null
				&& !"".equalsIgnoreCase(getManagedBean().getSelectedCommitte())) {
			SearchFilterBean committeTypeSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			committeTypeSearchBean.setSearchField("scCommittee.committeeType");
			committeTypeSearchBean.setSearchValue(getManagedBean()
					.getSelectedCommitte());
			searchBean.getDataFilterBeans().add(committeTypeSearchBean);
		}

		if (getManagedBean().getCreatedDt() != null) {
			SearchFilterBean creatDtSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			creatDtSearchBean.setSearchField("scCommittee.createdDate");
			creatDtSearchBean.setSearchValue(getManagedBean().getCreatedDt());
			searchBean.getDataFilterBeans().add(creatDtSearchBean);
		}

		if (getManagedBean().getQtNo() != null
				&& !"".equalsIgnoreCase(getManagedBean().getQtNo())) {
			SearchFilterBean qtnoSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			qtnoSearchBean.setSearchField("scQt.qtNo");
			qtnoSearchBean.setSearchValue(getManagedBean().getQtNo());

			searchBean.getDataFilterBeans().add(qtnoSearchBean);
		}
		if (getManagedBean().getStatus() != null
				&& !"".equalsIgnoreCase(getManagedBean().getStatus())) {
			/*
			 * SearchFilterBean statusSearchBean = new SearchFilterBean(
			 * SearchOperator.OPERATOR_EQUALS.value());
			 * statusSearchBean.setSearchField("scQt.qtNo");
			 * statusSearchBean.setSearchValue(getManagedBean().getQtNo());
			 * 
			 * searchBean.getDataFilterBeans().add(statusSearchBean);
			 */
		}
		committeReqDTO.setCommitteSearchBean(searchBean);

		try {
			ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE, ICommitteSO.class);
			resDto = committeSO.searchQTCommittees(committeReqDTO);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resDto.getCommitteResultBean().getResultList();
	}

	/**
	 * Method to show the qtdetails
	 * 
	 * @param qtId
	 */
	public void showQtDetailAction(Long qtId) {

		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		reqDTO.setQtId(qtId);
		QuationTenderResDTO tenderResDTO = detailSO.retriveQtById(reqDTO);
		List<ScQt> qtList = new ArrayList<ScQt>();
		qtList.add(tenderResDTO.getQtDetail());
		getManagedBean().setScQt(qtList);
	}

	/**
	 * Method to provide nessasary info to commitperson
	 */
	public void showCommitteMember() {
		this.getManagedBean().setCommitteMember(null);
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		committeeMember.setMinistryOrgProfile(new PmOrgProfile());
		committeeMember.setPmOrgProfile(new PmOrgProfile());
		committeeMember.setDepartmentOrgProfile(new PmOrgProfile());
		committeeMember.setUserSelection("P");
		retriveGovUsers();
		this.getManagedBean().setCommitteMember(committeeMember);
		getSelectionTypes();
		getUserTypes();

	}

	public void addCommitteMember() {
		System.out.println("inside add committemeber$$$$$$$$$$$$$$$$$$$$$");
		List<ScCommitteeMember> committeMemebers = this.getManagedBean()
				.getCommittemembers();
		if (committeMemebers == null) {
			committeMemebers = new ArrayList<ScCommitteeMember>();
		}
		this.getManagedBean().setCommitteMember(null);
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		committeeMember.setMinistryOrgProfile(new PmOrgProfile());
		committeeMember.setPmOrgProfile(new PmOrgProfile());
		committeeMember.setDepartmentOrgProfile(new PmOrgProfile());
		committeeMember.setUserSelection("P");
		committeeMember.setGovtUser(true);
		retriveGovUsers();
		this.getManagedBean().setCommitteMember(committeeMember);
		getSelectionTypes();
		getUserTypes();
		committeMemebers.add(getManagedBean().getCommitteMember());
		getManagedBean().setCommittemembers(committeMemebers);

		System.out.println("inside add committemeber$$$$$$$$$$$$$$$$$$$$$"
				+ getManagedBean().getCommittemembers().size());
	}

	/**
	 * Method to navigate to create committepage.
	 * 
	 * @return
	 */
	public String newCommitteeAction() {
		try {
			resetCommitte();

			ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE, ICommitteSO.class);
			CommitteResDTO committeResDTO = committeSO
					.retriveQTCommitteDetails(new CommitteReqDTO());
			ScQtCommittee qtCommittee = committeResDTO.getCommittee();
			ScQt scQt = new ScQt();
			qtCommittee.setScQt(scQt);
			getManagedBean().setQtCommittee(qtCommittee);
			ScCommittee committee = qtCommittee.getScCommittee();
			PmUser pmUser = new PmUser();
			committee.setPmUser(pmUser);
			getManagedBean().setScCommittee(committee);

			if (getManagedBean().getCommitteType() == null) {
				retriveCommitteTypes();
			}

			getManagedBean()
					.setApprovers(committeResDTO.getCommitteApprovers());
			if (getManagedBean().getApprovalStatus() == null) {
				getApprovalStatus();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return QTConstants.CREATE_COMMITTE;
	}

	private void getApprovalStatus() {
		ApprovalStatus status[] = ApprovalStatus.values();
		List<SelectItem> items = new ArrayList<SelectItem>();
		items.add(new SelectItem("", "SelectOne"));
		for (ApprovalStatus iEnumType : status) {
			items.add(new SelectItem(iEnumType.value(), iEnumType.name()));
		}
		getManagedBean().setApprovalStatus(items);
	}

	/**
	 * 
	 */
	private void retriveCommitteTypes() {
		List<SelectItem> committeTypes = new ArrayList<SelectItem>();
		committeTypes.add(new SelectItem("", "SelectOne"));
		for (CommitteType type : CommitteType.values()) {
			committeTypes.add(new SelectItem(type.value(), type.name()));
		}
		this.getManagedBean().setCommitteType(committeTypes);

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

	/**
	 * User type lie gov or non gov users
	 */
	private void getUserTypes() {
		MemberType[] type = MemberType.values();
		List<SelectItem> userTypes = new ArrayList<SelectItem>();

		for (MemberType memberType : type) {
			userTypes
					.add(new SelectItem(memberType.value(), memberType.name()));
		}
		this.getManagedBean().setUserTypes(userTypes);
	}

	/**
	 * Memeber roles like secreatey, chirperson, member
	 */
	private void getMemberRoles() {
		MemberRole[] roles = MemberRole.values();
		List<SelectItem> role = new ArrayList<SelectItem>();
		role.add(new SelectItem("", "SelectOne"));
		for (MemberRole memberRole : roles) {
			role.add(new SelectItem(memberRole.value(), memberRole.name()));
		}
		this.getManagedBean().setRoles(role);
	}

	/**
	 * Id type like ic or passport
	 */
	private void getIdTypes() {
		IdentificationType identificationType[] = IdentificationType.values();
		List<SelectItem> idtypes = new ArrayList<SelectItem>();
		idtypes.add(new SelectItem("", "SelectOne"));
		for (IdentificationType type : identificationType) {
			idtypes.add(new SelectItem(type.value(), type.name()));
		}
		this.getManagedBean().setIdTypes(idtypes);
	}

	/**
	 * Method to retrive govusers
	 */
	public void retriveGovUsers() {
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				"pm", IUserSO.class);
		if (getManagedBean().getGovUsers() == null) {
			UserResDTO resDTO = userSO.retrieveGovUserList();
			List<PmUser> userList = resDTO.getUserList();
			getManagedBean().setGovUsers(userList);
		}

	}

	/**
	 * Method to retrive externalusers
	 */
	public void retriveExternalUsers() {
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				"pp", IUserSO.class);
		if (getManagedBean().getExternalUsers() == null) {
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setOrgType(OrganizationType.EXTERNAL.value());
			UserResDTO resDTO = userSO.retrieveUserListByOrgType(reqDTO);
			List<PmUser> externalUsers = resDTO.getUserList();

			getManagedBean().setExternalUsers(externalUsers);
		}
	}

	public void cloneCommitteMembersAcition(long committeId) {
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(SourcingConst.SOURCING_MODULE,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setCommitteId(committeId);
		CommitteResDTO resDTO = committeSO
				.retrieveCommitteMembersById(committeReqDTO);
		if (getManagedBean().getCloneCommitteMemebers() == null) {
			getManagedBean().setCloneCommitteMemebers(
					new ArrayList<ScCommitteeMember>());
		}

		getManagedBean().getCloneCommitteMemebers().addAll(
				resDTO.getCommitteMembers());

	}

	public void addCloneCommitteeMembersAction() {

		if (getManagedBean().getCommittemembers() == null) {
			getManagedBean().setCommittemembers(
					getManagedBean().getSelectedCommitteMemebers());
		} else {
			getManagedBean().getCommittemembers().addAll(
					getManagedBean().getSelectedCommitteMemebers());
		}
	}

	public void userSelcetionAction(AjaxBehaviorEvent event) {
		String userSelection = (String) event.getComponent().getAttributes()
				.get("value");
		System.out.println("_____________________+++++++++++++++"
				+ userSelection);
		this.getManagedBean().setExternalUsers(null);

		this.getManagedBean().setMinstryList(null);
		this.getManagedBean().setDeptList(null);
		this.getManagedBean().setDivisionList(null);
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		committeeMember.setMinistryOrgProfile(new PmOrgProfile());
		committeeMember.setPmOrgProfile(new PmOrgProfile());
		committeeMember.setDepartmentOrgProfile(new PmOrgProfile());
		this.getManagedBean().getCommittemembers()
				.remove(this.getManagedBean().getCommitteMember());
		committeeMember.setUserSelection(userSelection);
		this.getManagedBean().setCommitteMember(committeeMember);
		this.getManagedBean().getCommittemembers().add(committeeMember);
		if (this.getManagedBean().getCommitteMember().getUserSelection()
				.equalsIgnoreCase(SelectionType.PROFILE.value())) {

			retriveGovUsers();
			getManagedBean().getCommitteMember().setGovtUser(true);
		} else if (this.getManagedBean().getCommitteMember().getUserSelection()
				.equalsIgnoreCase(SelectionType.EXISITING.value())) {

			getManagedBean().getCommitteMember().setExternalUser(true);
			retriveExternalUsers();
		} else if (this.getManagedBean().getCommitteMember().getUserSelection()
				.equalsIgnoreCase(SelectionType.New.value())) {

			getManagedBean().getCommitteMember().setExternalUser(true);
			retriveAllMinstrys();
		}

	}

	public void retriveUserDetails(Long userid) {

		IUserSO iUserSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				QTConstants.PM_MODULE, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(getManagedBean().getCommitteMember().getPmUser()
				.getUserId());
		UserResDTO userResDTO = iUserSO.retrieveUserDetails(reqDTO);
		PmUser pmUser = userResDTO.getPmUser();

		ScCommitteeMember committeeMember = getManagedBean()
				.getCommitteMember();

		committeeMember.setMemberName(pmUser.getUserName());
		committeeMember.setIdType("");
		committeeMember.setIcPassport(pmUser.getIdentificationNo());
		committeeMember.setDesignation(pmUser.getDesignation());
		committeeMember.setGrade(pmUser.getGrade());
		UserResDTO userResDTO1 = iUserSO.retrieveUserOrgListByUserId(reqDTO);
		List<PmUserOrg> userOrgs = userResDTO1.getUserOrgList();
		List<PmOrgProfile> minstryProfiles = new ArrayList<PmOrgProfile>();
		for (PmUserOrg pmUserOrg : userOrgs) {
			PmOrgProfile pmOrgProfile = pmUserOrg.getPmOrgProfile();

			if (pmOrgProfile != null
					&& pmOrgProfile.getParentOrgProfile() != null)
				minstryProfiles.add(pmOrgProfile.getParentOrgProfile()
						.getParentOrgProfile());

		}
		getManagedBean().setMinstryList(minstryProfiles);
		getManagedBean().setCommitteMember(committeeMember);

	}

	/**
	 * Handle Department dropdown changed - reset selected ptj profile and
	 * populate list of department.
	 * */
	public void handleMinistryProfileChanged() {

		Long parentOrgProfileId = this.getManagedBean().getCommitteMember()
				.getMinistryOrgProfile().getOrgProfileId();

		if (parentOrgProfileId != null) {
			Long userId = this.getManagedBean().getCommitteMember().getPmUser()
					.getUserId();

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();

			reqDTO.setUserId(userId);
			reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
			reqDTO.setParentOrgProfileId(parentOrgProfileId);

			IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("pm", IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			this.getManagedBean().setDeptList(resDTO.getOrgProfileList());

		} else {
			this.getManagedBean()
					.setDivisionList(new ArrayList<PmOrgProfile>());
		}

		this.getManagedBean().getCommitteMember().getDepartmentOrgProfile()
				.setOrgProfileId(null);

		handleDepartmentProfileChanged();
	}

	/**
	 * Handle Department dropdown changed - reset selected ptj profile and
	 * populate list of division.
	 */
	public void handleDepartmentProfileChanged() {

		Long parentOrgProfileId = this.getManagedBean().getCommitteMember()
				.getDepartmentOrgProfile().getOrgProfileId();

		if (parentOrgProfileId != null) {
			Long userId = this.getManagedBean().getCommitteMember().getPmUser()
					.getUserId();
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();

			reqDTO.setUserId(userId);
			reqDTO.setOrgType(OrganizationType.DIVISION.value());
			reqDTO.setParentOrgProfileId(parentOrgProfileId);

			IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("pm", IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			this.getManagedBean().setDivisionList(resDTO.getOrgProfileList());

		} else {
			this.getManagedBean()
					.setDivisionList(new ArrayList<PmOrgProfile>());
		}

		this.getManagedBean().getCommitteMember().getPmOrgProfile()
				.setOrgProfileId(null);
	}

	public void retriveAllMinstrys() {

		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();

		reqDTO.setOrgType(OrganizationType.MINISTRY.value());

		IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject("pm", IOrgProfileSO.class);
		OrgProfileResDTO resDTO = orgProfileSO
				.retrieveOrgProfileListByOrgType(reqDTO);
		this.getManagedBean().setMinstryList(resDTO.getOrgProfileList());

	}

	public void resetCommitteDetails() {
		resetCommitte();
	}

	private void resetCommitte() {
		getManagedBean().setCloneCommitteMemebers(null);
		getManagedBean().setCloneSearchCommittes(null);
		getManagedBean().setCommittemembers(null);
	}

	public void onRowEditAction(RowEditEvent event) {
		ScCommitteeMember committeeMember = (ScCommitteeMember) event
				.getObject();
		getManagedBean().setCommitteMember(committeeMember);

	}

	public void onRowCancel(RowEditEvent event) {
		ScCommitteeMember member = (ScCommitteeMember) event.getObject();
		this.getManagedBean().getCommittemembers().remove(member);
	}

	public void cancelCommitteAction() {
		this.getManagedBean().getCommittemembers()
				.remove(this.getManagedBean().getCommitteMember());
	}

	public void goToMaintainceAction(ScQtCommittee qtCommitte) {
		ScQtCommittee committee = qtCommitte;
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setCommitteId(committee.getQtCommitteeId());
		// committeReqDTO.setQtCommittee(committee);
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(QTConstants.SS_MODULE, ICommitteSO.class);
		CommitteResDTO resDTO = committeSO
				.retriveQTCommitteDetails(committeReqDTO);
		this.getManagedBean().setQtCommittee(resDTO.getCommittee());
		this.getManagedBean().setScCommittee(
				resDTO.getCommittee().getScCommittee());
		this.getManagedBean().setCommittemembers(
				resDTO.getCommittee().getScCommittee().getScCommitteeMembers());
		this.getManagedBean().setApprovers(resDTO.getCommitteApprovers());
		this.getManagedBean().setCommitteMember(new ScCommitteeMember());
		ConfigurableNavigationHandler handler = (ConfigurableNavigationHandler) FacesContext
				.getCurrentInstance().getApplication().getNavigationHandler();
		handler.performNavigation("committeemaintanancepage");

	}

	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		this.getManagedBean().setTaskId(taskId);

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
		this.getManagedBean().setOutComes(res.getAction());
		SCCommitteeDataType committeDataType = (SCCommitteeDataType) obj;

		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(QTConstants.SS_MODULE, ICommitteSO.class);

		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setCommitteId(new Long(committeDataType.getComId())
				.longValue());
		CommitteResDTO resDTO = committeSO
				.retriveQTCommitteDetails(committeReqDTO);

		this.getManagedBean().setQtCommittee(resDTO.getCommittee());
		this.getManagedBean().setScCommittee(
				resDTO.getCommittee().getScCommittee());
		this.getManagedBean().setCommittemembers(
				resDTO.getCommittee().getScCommittee().getScCommitteeMembers());
		this.getManagedBean().setApprovers(resDTO.getCommitteApprovers());
		this.getManagedBean().setCommitteMember(new ScCommitteeMember());

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						"committeemaintanancepage" + "?faces-redirect=true");
	}
}
