package my.ep.web.backingbean.sc.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.lookupservice.BusinessDelegate;

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

		getMinistrySpecificationSecretaryList();
	}

	/**
	 * 
	 */
	public List<SelectItem> getMinistrySpecificationSecretaryList() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			List<PmUser> comitteeUsersUsers = new ArrayList<PmUser>();
			if (getManagedBean().getComitteeUsers() == null) {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setRoleCode(RoleCode.MSC_COMMITTEE.value());
				UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
				comitteeUsersUsers = resDTO.getUserList();
			}
			selectItems.add(new SelectItem(null, "--Select One--"));
			for (int i = 0; i < comitteeUsersUsers.size(); i++) {
				PmUser user = (PmUser) comitteeUsersUsers.get(i);
				selectItems.add(new SelectItem(user.getUserId(), user
						.getUserName()));
			}
			this.getManagedBean().setComitteeUsers(selectItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getManagedBean().getComitteeUsers();
	}

	public void addBusinessOwner() {
		debugLogger("addBusinessOwner");
		/*
		 * List<ScCommitteeMember> committeMemebers = this.getManagedBean()
		 * .getScCommitteeMemberList(); if (committeMemebers == null) {
		 * committeMemebers = new ArrayList<ScCommitteeMember>(); }
		 * this.getManagedBean().setScCommitteeMemberList(null);
		 * ScCommitteeMember committeeMember = new ScCommitteeMember();
		 * committeeMember.setUserSelection("E");
		 * this.getManagedBean().setCommitteMember(committeeMember);
		 * getSelectionTypes();
		 * committeMemebers.add(getManagedBean().getCommitteMember());
		 */
		this.getManagedBean().setSelectedPmUser(new ArrayList<PmUser>());

		this.getManagedBean().getCommitteMember().setUserSelection("E");
		getSelectionTypes();
		// getManagedBean().setScCommitteeMemberList(committeMemebers);
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
		System.out.println("_____________________+++++++++++++++ : "
				+ userSelection);

		/*
		 * this.getManagedBean().setExternalUsers(null); ScCommitteeMember
		 * committeeMember = new ScCommitteeMember();
		 * //committeeMember.setPmUser(new PmUser()); if
		 * (getManagedBean().getScCommitteeMemberList() != null) {
		 * getManagedBean().getScCommitteeMemberList().remove(
		 * this.getManagedBean().getCommitteMember()); }
		 * committeeMember.setUserSelection(userSelection);
		 * getManagedBean().setCommitteMember(committeeMember);
		 * getManagedBean().getScCommitteeMemberList().add(committeeMember);
		 */

		if (getManagedBean().getScCommitteeMemberList() != null) {
			getManagedBean().getScCommitteeMemberList().remove(
					this.getManagedBean().getCommitteMember());
		}

		getManagedBean().getCommitteMember().setUserSelection(userSelection);
		if (this.getManagedBean().getCommitteMember().getUserSelection()
				.equalsIgnoreCase(SelectionType.New.value())) {
			getManagedBean().getCommitteMember().setExternalUser(true);
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
		ScQtCommittee scQtCommittee = (ScQtCommittee) event.getObject();
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType("BO");
		reqDTO.setQtId(scQtCommittee.getScQt().getQtId());
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
			System.out.println("======== list size===="
					+ getManagedBean().getCloneCommitteMemebers().size());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
			System.out.println("Size : "
					+ resDto.getUserResultBean().getResultList().size());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void cloneSearchCommitteAction() {
		List<ScQtCommittee> qtCommittee = retriveQtCommittes();
		getManagedBean().setCloneSearchCommittes(qtCommittee);
	}

	/**
	 * @return
	 */
	private List<ScQtCommittee> retriveQtCommittes() {
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		ISearchBean<ScQtCommittee> searchBean = new SearchBean<ScQtCommittee>();
		CommitteResDTO resDto = null;

		if (getManagedBean().getCreatedDtFrom() != null) {
			SearchFilterBean creatDtSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			creatDtSearchBean.setSearchField("scCommittee.createdDate");
			creatDtSearchBean.setSearchValue(getManagedBean()
					.getCreatedDtFrom());
			searchBean.getDataFilterBeans().add(creatDtSearchBean);
		}

		if (getManagedBean().getCreatedDtTo() != null) {
			SearchFilterBean creatDtSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			creatDtSearchBean.setSearchField("scCommittee.createdDate");
			creatDtSearchBean.setSearchValue(getManagedBean().getCreatedDtTo());
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

		if (getManagedBean().getTitle() != null
				&& !"".equalsIgnoreCase(getManagedBean().getTitle())) {
			SearchFilterBean qtnoSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			qtnoSearchBean.setSearchField("scQt.qtTitle");
			qtnoSearchBean.setSearchValue(getManagedBean().getTitle());

			searchBean.getDataFilterBeans().add(qtnoSearchBean);
		}

		if (getManagedBean().getStatus() != null
				&& !"".equalsIgnoreCase(getManagedBean().getStatus())) {
		}
		committeReqDTO.setCommitteSearchBean(searchBean);

		try {
			ICommitteSO committeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			resDto = committeSO.searchQTCommittees(committeReqDTO);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resDto.getCommitteResultBean().getResultList();
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

	public void okListnere() {
		List<ScCommitteeMember> memberList = getManagedBean()
				.getScCommitteeMemberList();

		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}

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
			member.setPnp(getManagedBean().getCommitteMember().isPnp());
			memberList.add(member);
			this.getManagedBean().setScCommitteeMemberList(memberList);
			System.out.println(" IC Passport : " + member.getIcPassport());
		} else {
			System.out.println("******** Existing User *********");

			for (PmUser user : getManagedBean().getSelectedPmUser()) {
				ScCommitteeMember committeeMember = new ScCommitteeMember();
				committeeMember.setMemberName(user.getUserName());
				committeeMember.setIcPassport(user.getIdentificationNo());
				committeeMember.setDesignation(user.getDesignation());
				committeeMember.setGrade(user.getGrade());
				committeeMember.setPnp(user.isPMP());
				memberList.add(committeeMember);
			}
			this.getManagedBean().setScCommitteeMemberList(memberList);
			System.out.println("Passport : "
					+ getManagedBean().getCommitteMember().getIcPassport());
		}
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
}
