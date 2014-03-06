package my.ep.web.backingbean.sc.quatationtender;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.tree.TreeNodeBean;
import my.ep.web.managedbean.sc.committee.CommitteManagedBean;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "qTCommitteBackingBean")
@RequestScoped
public class QTCommitteBackingBean extends
		SourcingBaseBackingBean<CommitteManagedBean> {

	private static final Logger logger = EpLogger
			.getLogger(QTCommitteBackingBean.class);

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}

	public QTCommitteBackingBean() {
		if (this.getManagedBean().getCommitteMember() == null) {
			this.getManagedBean().setCommitteMember(new ScCommitteeMember());
		}
	}

	public void userSelcetionAction(AjaxBehaviorEvent event) {
		String userSelection = (String) event.getComponent().getAttributes()
				.get("value");
		System.out.println("_____________________+++++++++++++++"
				+ userSelection);
		this.getManagedBean().setExternalUsers(null);
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		this.getManagedBean().getCommittemembers()
				.remove(this.getManagedBean().getCommitteMember());
		committeeMember.setUserSelection(userSelection);
		this.getManagedBean().setCommitteMember(committeeMember);
		this.getManagedBean().getCommittemembers().add(committeeMember);
		if (this.getManagedBean().getCommitteMember().getUserSelection()
				.equalsIgnoreCase(SelectionType.EXISITING.value())) {

			getManagedBean().getCommitteMember().setExternalUser(true);
		} else if (this.getManagedBean().getCommitteMember().getUserSelection()
				.equalsIgnoreCase(SelectionType.New.value())) {
			getManagedBean().getCommitteMember().setExternalUser(true);
		}
	}

	public void existingCommitteeSearchAction() {
		debugLogger("existingCommitteeSearchAction");
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

	public void cancelCommitteAction() {
		this.getManagedBean().getScCommitteeMemberList()
				.remove(this.getManagedBean().getCommitteMember());
	}

	public void onRowCloneAction(SelectEvent event) {
		debugLogger("onRowCloneAction");
		ScQtCommittee scQtCommittee = (ScQtCommittee) event.getObject();

		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteId(scQtCommittee.getScCommittee().getCommitteeId());
		CommitteResDTO resDTO = null;
		try {
			ICommitteSO committeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			resDTO = committeSO.retrieveCommitteMembersById(reqDTO);
			if (resDTO != null) {
				this.getManagedBean().setCloneCommitteMemebers(
						resDTO.getCommitteMembers());
			}
			System.out.println("======== list size==== :"
					+ getManagedBean().getCloneCommitteMemebers().size());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		DefaultTreeNode searchcommitteeNode = null;

		List<ScCommitteeMember> panelSupplierList = getManagedBean()
				.getCloneCommitteMemebers();
		if (panelSupplierList != null && panelSupplierList.size() > 0) {
			Map<ScCommittee, List<ScCommitteeMember>> committeeMap = new LinkedHashMap<ScCommittee, List<ScCommitteeMember>>();

			// Group Committee by Committee Type
			for (ScCommitteeMember scCommitteeMember : panelSupplierList) {
				ScCommittee committee = scCommitteeMember.getScCommittee();
				List<ScCommitteeMember> membersList = committeeMap
						.get(committee);
				if (membersList == null) {
					membersList = new ArrayList<ScCommitteeMember>();
					committeeMap.put(committee, membersList);
				}
				membersList.add(scCommitteeMember);
			}
			searchcommitteeNode = new DefaultTreeNode("Root", null);
			populateCommitteeTreeNode(committeeMap, searchcommitteeNode);
		}
		this.getManagedBean().setSearchcommitteeNode(searchcommitteeNode);
	}

	/**
	 * @param committeeMap
	 * @param searchcommitteeNode
	 */
	private void populateCommitteeTreeNode(
			Map<ScCommittee, List<ScCommitteeMember>> committeeMap,
			TreeNode searchcommitteeNode) {

		for (Entry<ScCommittee, List<ScCommitteeMember>> committeeEntry : committeeMap
				.entrySet()) {
			ScCommittee committee = committeeEntry.getKey();

			List<ScCommitteeMember> committeeMembersList = committeeEntry
					.getValue();

			TreeNode committeeNode = new DefaultTreeNode(
					new TreeNodeBean<ScCommittee>("committee", committee),
					searchcommitteeNode);

			for (ScCommitteeMember scCommitteeMember : committeeMembersList) {
				new DefaultTreeNode(new TreeNodeBean<ScCommitteeMember>(
						"scCommitteeMember", scCommitteeMember), committeeNode);
			}
		}
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
		if (getManagedBean().getEvaluationType() != null
				&& !"".equalsIgnoreCase(getManagedBean().getEvaluationType())) {
			SearchFilterBean evaluationSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			evaluationSearchBean.setSearchField("scQt.scQtSpec.evaluationType");
			evaluationSearchBean.setSearchValue(getManagedBean()
					.getEvaluationType());

			searchBean.getDataFilterBeans().add(evaluationSearchBean);
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

	public void addCloneDtlToTabs() {
		TreeNode searchCommitteeTreeNode = getManagedBean()
				.getSearchcommitteeNode();
		List<ScCommitteeMember> committeeMemberList = new ArrayList<ScCommitteeMember>();

		if (searchCommitteeTreeNode != null) {
			ScCommittee committee = null;
			for (TreeNode searchTreeNode : searchCommitteeTreeNode
					.getChildren()) {
				// ScCommittee
				@SuppressWarnings("unchecked")
				TreeNodeBean<ScCommittee> treeBean = (TreeNodeBean<ScCommittee>) searchTreeNode
						.getData();

				if (treeBean.isSelected()) {
					committee = treeBean.getObject();
					System.out.println("Committee Type : "
							+ committee.getCommitteeType());
				}
				// ScCommitteeMember
				List<TreeNode> childs = searchTreeNode.getChildren();
				for (TreeNode treeNode : childs) {
					@SuppressWarnings("unchecked")
					TreeNodeBean<ScCommitteeMember> scCommitteeMemberBean = (TreeNodeBean<ScCommitteeMember>) treeNode
							.getData();
					if (treeBean.isSelected()) {
						ScCommitteeMember scCommitteeMember = scCommitteeMemberBean
								.getObject();
						committeeMemberList.add(scCommitteeMember);
					}
				}
			}
			if (committee.getCommitteeType().equalsIgnoreCase("SC")) {
				getManagedBean().setSpecificationCommitteList(
						committeeMemberList);
			} else if (committee.getCommitteeType().equalsIgnoreCase("TEC")) {
				getManagedBean().setTechEvaluationCommitteList(
						committeeMemberList);
			} else if (committee.getCommitteeType().equalsIgnoreCase("EC")) {
				getManagedBean().setEvaluationCommitteList(committeeMemberList);
			} else if (committee.getCommitteeType().equalsIgnoreCase("FEC")) {
				getManagedBean().setFinEvaluationCommitteList(
						committeeMemberList);
			} else if (committee.getCommitteeType().equalsIgnoreCase("OC")) {
				getManagedBean().setOpeningCommitteList(committeeMemberList);
			}

		}
	}

	public void addCommitteMember() {
		debugLogger("addCommitteMember");
		List<ScCommitteeMember> committeMemebers = this.getManagedBean()
				.getCommittemembers();
		if (committeMemebers == null) {
			committeMemebers = new ArrayList<ScCommitteeMember>();
		}
		this.getManagedBean().setCommitteMember(null);
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		committeeMember.setUserSelection("E");
		this.getManagedBean().setCommitteMember(committeeMember);
		getSelectionTypes();
		committeMemebers.add(getManagedBean().getCommitteMember());
		getManagedBean().setCommittemembers(committeMemebers);

		System.out.println("inside add committemeber$$$$$$$$$$$$$$$$$$$$$"
				+ getManagedBean().getCommittemembers().size());
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

	public void deleteSpecificationCommittee() {
		debugLogger("deleteSpecificationCommittee");
		List<ScCommitteeMember> committeeMembersList = getManagedBean()
				.getSpecificationCommitteList();
		List<ScCommitteeMember> selectCommitteeMembersList = getManagedBean()
				.getSelectSpecificationCommitteeList();
		if (committeeMembersList.size() == selectCommitteeMembersList.size()) {
			committeeMembersList.clear();
		} else {
			for (ScCommitteeMember scCommitteeMember : selectCommitteeMembersList) {
				committeeMembersList.remove(scCommitteeMember);
			}
		}
	}

	public void addCloneToTecDtl() {
		List<ScCommitteeMember> list = getManagedBean()
				.getTechEvaluationCommitteList();
		if (null == list) {
			list = new ArrayList<ScCommitteeMember>();
		}
		for (ScCommitteeMember scCommitteeMember : getManagedBean()
				.getSelectSpecificationCommitteeList()) {
			list.add(scCommitteeMember);
		}
		getManagedBean().setTechEvaluationCommitteList(list);
		System.out.println("TEC Dtl : "
				+ getManagedBean().getTechEvaluationCommitteList().size());
	}

	public void okAction() {
		debugLogger("okAction");
		List<ScCommitteeMember> memberList = getManagedBean()
				.getSpecificationCommitteList();
		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}
		if (getManagedBean().getCommitteMember().getUserSelection().equals("N")) {
			System.out.println("******** New User *********");
			ScCommitteeMember member = getManagedBean().getCommitteMember();
			memberList.add(member);
			this.getManagedBean().setSpecificationCommitteList(memberList);
			System.out.println(" IC Passport : " + member.getIcPassport());
		} else {
			System.out.println("******** Existing User *********");
			for (PmUser user : getManagedBean().getSelectedPmUser()) {
				ScCommitteeMember committeeMember = new ScCommitteeMember();
				committeeMember.setMemberName(user.getUserName());
				committeeMember.setIcPassport(user.getIdentificationNo());
				committeeMember.setDesignation(user.getDesignation());
				committeeMember.setEmail(user.getPmAddressTypes().get(0)
						.getPmAddress().getEmail());
				committeeMember.setGrade(user.getGrade());
				committeeMember.setPnp(user.isPMP());
				memberList.add(committeeMember);
			}
			getManagedBean().setSpecificationCommitteList(memberList);
			System.out.println("Passport : "
					+ getManagedBean().getSpecificationCommitteList().get(0)
							.getIcPassport());
		}
	}

	public void okOpeningCommitteeAction() {
		debugLogger("okOpeningCommitteeAction");
		List<ScCommitteeMember> memberList = getManagedBean()
				.getOpeningCommitteList();
		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}
		if (getManagedBean().getCommitteMember().getUserSelection().equals("N")) {
			System.out.println("******** New User *********");
			ScCommitteeMember member = getManagedBean().getCommitteMember();
			memberList.add(member);
			this.getManagedBean().setOpeningCommitteList(memberList);
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
			getManagedBean().setOpeningCommitteList(memberList);
			System.out.println("Passport : "
					+ getManagedBean().getOpeningCommitteList().get(0)
							.getIcPassport());
		}
	}

	public void deleteOpeningCommittee() {
		debugLogger("deleteOpeningCommittee");
		List<ScCommitteeMember> committeeMembersList = getManagedBean()
				.getOpeningCommitteList();

		List<ScCommitteeMember> selectCommitteeMembersList = getManagedBean()
				.getSelectOpeningCommitteeList();

		if (committeeMembersList.size() == selectCommitteeMembersList.size()) {
			committeeMembersList.clear();
		} else {
			for (ScCommitteeMember scCommitteeMember : selectCommitteeMembersList) {
				committeeMembersList.remove(scCommitteeMember);
			}
		}
	}

	public void okTechnicalEvaluationCommitteeAction() {
		debugLogger("okTechnicalEvaluationCommitteeAction");
		List<ScCommitteeMember> memberList = getManagedBean()
				.getTechEvaluationCommitteList();
		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}
		if (getManagedBean().getCommitteMember().getUserSelection().equals("N")) {
			System.out.println("******** New User *********");
			ScCommitteeMember member = getManagedBean().getCommitteMember();
			memberList.add(member);
			this.getManagedBean().setTechEvaluationCommitteList(memberList);
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
			getManagedBean().setTechEvaluationCommitteList(memberList);
			System.out.println("Passport : "
					+ getManagedBean().getTechEvaluationCommitteList().get(0)
							.getIcPassport());
		}
	}

	public void deleteTechnicalEvaluationCommittee() {
		debugLogger("deleteTechnicalEvaluationCommittee");
		List<ScCommitteeMember> committeeMembersList = getManagedBean()
				.getTechEvaluationCommitteList();
		List<ScCommitteeMember> selectCommitteeMembersList = getManagedBean()
				.getSelectTechEvaluationCommitteeList();
		if (committeeMembersList.size() == selectCommitteeMembersList.size()) {
			committeeMembersList.clear();
		} else {
			for (ScCommitteeMember scCommitteeMember : selectCommitteeMembersList) {
				committeeMembersList.remove(scCommitteeMember);
			}
		}
	}

	public void okFinancailCommitteeAction() {
		debugLogger("okFinancailCommitteeAction");
		List<ScCommitteeMember> memberList = getManagedBean()
				.getFinEvaluationCommitteList();
		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}
		if (getManagedBean().getCommitteMember().getUserSelection().equals("N")) {
			System.out.println("******** New User *********");
			ScCommitteeMember member = getManagedBean().getCommitteMember();
			memberList.add(member);
			this.getManagedBean().setFinEvaluationCommitteList(memberList);
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
			getManagedBean().setFinEvaluationCommitteList(memberList);
			System.out.println("Passport : "
					+ getManagedBean().getFinEvaluationCommitteList().get(0)
							.getIcPassport());
		}
	}

	public void deleteFinancialEvaluationCommittee() {
		debugLogger("deleteFinancialEvaluationCommittee");
		List<ScCommitteeMember> committeeMembersList = getManagedBean()
				.getFinEvaluationCommitteList();
		List<ScCommitteeMember> selectCommitteeMembersList = getManagedBean()
				.getSelectFinEvaluationCommitteeList();
		if (committeeMembersList.size() == selectCommitteeMembersList.size()) {
			committeeMembersList.clear();
		} else {
			for (ScCommitteeMember scCommitteeMember : selectCommitteeMembersList) {
				committeeMembersList.remove(scCommitteeMember);
			}
		}
	}

	public void okEvaluationCommitteeAction() {
		debugLogger("okEvaluationCommitteeAction");

		List<ScCommitteeMember> memberList = getManagedBean()
				.getEvaluationCommitteList();

		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}

		if (getManagedBean().getCommitteMember().getUserSelection().equals("N")) {

			System.out.println("******** New User *********");
			ScCommitteeMember member = getManagedBean().getCommitteMember();

			memberList.add(member);
			this.getManagedBean().setEvaluationCommitteList(memberList);
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

			getManagedBean().setEvaluationCommitteList(memberList);
			System.out.println("Passport : "
					+ getManagedBean().getEvaluationCommitteList().get(0)
							.getIcPassport());
		}
	}

	public void deleteEvaluationCommittee() {
		debugLogger("deleteEvaluationCommittee");
		List<ScCommitteeMember> committeeMembersList = getManagedBean()
				.getEvaluationCommitteList();

		List<ScCommitteeMember> selectCommitteeMembersList = getManagedBean()
				.getSelectEvaluationCommitteeList();

		if (committeeMembersList.size() == selectCommitteeMembersList.size()) {
			committeeMembersList.clear();
		} else {
			for (ScCommitteeMember scCommitteeMember : selectCommitteeMembersList) {
				committeeMembersList.remove(scCommitteeMember);
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

}
