package my.ep.web.backingbean.sc.quatationtender;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.type.aut.SearchOperator;
import my.ep.type.sc.CommitteType;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.bean.tree.TreeNodeBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "qTCommitteBackingBean")
@RequestScoped
public class QTCommitteBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	private static final Logger logger = EpLogger
			.getLogger(QTCommitteBackingBean.class);

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}

	public QTCommitteBackingBean() {
		/*if (this.getManagedBean().getCommitteMember() == null) {
			this.getManagedBean().setCommitteMember(new ScCommitteeMember());
		}*/

		getManagedBean().setRoleTypes(
				getRoleTypeDropdownValues(ParameterTypeConst.COMMITTEE_ROLE));
	}

	public void onTabChangeAction(TabChangeEvent event) {
		String tabTitle = ((TabBean) event.getData()).getTitleName();

		ScCommittee committee = ((TabBean) event.getData()).getScCommittee();
		//System.out.println("$$$$$$$$$$$$$$$$$$$"+committee.getScCommitteeMembers().size());
		getManagedBean().setScCommittee(committee);
		getManagedBean().setTabTitle(tabTitle);
	}

	/**
	 * @param committeeRole
	 * @param types
	 * @return
	 */
	private SelectItem[] getRoleTypeDropdownValues(String committeeRole) {

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
		ScQt scQt = (ScQt) event.getObject();
		// ScQtCommittee scQtCommittee = (ScQtCommittee) event.getObject();

		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setQtId(scQt.getQtId());
		// reqDTO.getsetCommitteId(scQtCommittee.getScCommittee().getCommitteeId());
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
		try {
			QtnTenderSearchBean qtnTenderSearchBean = this.getManagedBean()
					.getCloneQtnTenderSearchBean();
			List<ScQt> cloneSearchQt = retriveQtCommittes(qtnTenderSearchBean);

			this.getManagedBean().setCloneTenderList(cloneSearchQt);
		} catch (Exception e) {

		}
		// List<ScQt> qtCommittee = retriveQtCommittes();
		// getManagedBean().setCloneSearchCommittes(qtCommittee);
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

		/*
		 * ISearchBean<ScQt> searchBean = new SearchBean<ScQt>();
		 * QuationTenderResDTO resDTO = null;
		 * 
		 * 
		 * CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		 * ISearchBean<ScQtCommittee> searchBean = new
		 * SearchBean<ScQtCommittee>(); CommitteResDTO resDto = null;
		 * 
		 * 
		 * if (getManagedBean().getCommitteNo() != null &&
		 * !"".equalsIgnoreCase(getManagedBean().getCommitteNo())) {
		 * SearchFilterBean committeSearchBean = new SearchFilterBean(
		 * SearchOperator.OPERATOR_EQUALS.value());
		 * committeSearchBean.setSearchField("scCommittee.committeeId");
		 * committeSearchBean.setSearchValue(getManagedBean().getCommitteNo());
		 * searchBean.getDataFilterBeans().add(committeSearchBean);
		 * 
		 * }
		 * 
		 * if (getManagedBean().getSelectedCommitte() != null &&
		 * !"".equalsIgnoreCase(getManagedBean().getSelectedCommitte())) {
		 * SearchFilterBean committeTypeSearchBean = new SearchFilterBean(
		 * SearchOperator.OPERATOR_EQUALS.value());
		 * committeTypeSearchBean.setSearchField("scCommittee.committeeType");
		 * committeTypeSearchBean.setSearchValue(getManagedBean()
		 * .getSelectedCommitte());
		 * searchBean.getDataFilterBeans().add(committeTypeSearchBean); }
		 * 
		 * if (getManagedBean().getCreatedDt() != null) { SearchFilterBean
		 * creatDtSearchBean = new SearchFilterBean(
		 * SearchOperator.OPERATOR_EQUALS.value());
		 * creatDtSearchBean.setSearchField("scCommittee.createdDate");
		 * creatDtSearchBean.setSearchValue(getManagedBean().getCreatedDt());
		 * searchBean.getDataFilterBeans().add(creatDtSearchBean); }
		 * 
		 * if (getManagedBean().getQtNo() != null &&
		 * !"".equalsIgnoreCase(getManagedBean().getQtNo())) { SearchFilterBean
		 * qtnoSearchBean = new SearchFilterBean(
		 * SearchOperator.OPERATOR_EQUALS.value());
		 * qtnoSearchBean.setSearchField("scQt.qtNo");
		 * qtnoSearchBean.setSearchValue(getManagedBean().getQtNo());
		 * 
		 * searchBean.getDataFilterBeans().add(qtnoSearchBean); } if
		 * (getManagedBean().getStatus() != null &&
		 * !"".equalsIgnoreCase(getManagedBean().getStatus())) {
		 * 
		 * SearchFilterBean statusSearchBean = new SearchFilterBean(
		 * SearchOperator.OPERATOR_EQUALS.value());
		 * statusSearchBean.setSearchField("scQt.qtNo");
		 * statusSearchBean.setSearchValue(getManagedBean().getQtNo());
		 * 
		 * searchBean.getDataFilterBeans().add(statusSearchBean);
		 * 
		 * } if (getManagedBean().getEvaluationType() != null &&
		 * !"".equalsIgnoreCase(getManagedBean().getEvaluationType())) {
		 * SearchFilterBean evaluationSearchBean = new SearchFilterBean(
		 * SearchOperator.OPERATOR_EQUALS.value());
		 * evaluationSearchBean.setSearchField("scQt.scQtSpec.evaluationType");
		 * evaluationSearchBean.setSearchValue(getManagedBean()
		 * .getEvaluationType());
		 * 
		 * searchBean.getDataFilterBeans().add(evaluationSearchBean); }
		 * reqDTO.setQtSearchBean(searchBean); //
		 * committeReqDTO.setCommitteSearchBean(searchBean);
		 * 
		 * try { IQTDetailSO detailSO = BusinessDelegate.getInstance()
		 * .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
		 * IQTDetailSO.class);
		 * 
		 * ICommitteSO committeSO = BusinessDelegate.getInstance()
		 * .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
		 * ICommitteSO.class);
		 * 
		 * resDTO = detailSO.getQuationTenders(reqDTO); // resDto =
		 * committeSO.searchQTCommittees(committeReqDTO); } catch (Exception ex)
		 * { ex.printStackTrace(); }
		 * 
		 * return resDTO.getQtResultBean();
		 */
	}

	@SuppressWarnings("unchecked")
	public void addCloneDtlToTabs() {
		TreeNode searchCommitteeTreeNode = getManagedBean()
				.getSearchcommitteeNode();
		List<ScCommitteeMember> committeeMemberList = new ArrayList<ScCommitteeMember>();
		List<ScCommittee> treeBeans = new ArrayList();
		if (searchCommitteeTreeNode != null) {
			ScCommittee committee = null;
			for (TreeNode searchTreeNode : searchCommitteeTreeNode
					.getChildren()) {

				// ScCommittee
				ScCommittee treeBean = (ScCommittee) ((TreeNodeBean) searchTreeNode
						.getData()).getObject();
				treeBeans.add(treeBean);
			}
		}

		List<TabBean> tabs = this.getManagedBean()
				.getScCommitteeMemberTabList();
		for (TabBean tabBean : tabs) {
			for (ScCommittee scCommittee : treeBeans) {
				if (tabBean.getTitleName().equalsIgnoreCase(
						CommitteType.getValue(scCommittee.getCommitteeType()))) {
					tabBean.setScCommittee(treeBeans.get(0));
					break;
				}
			}
			/*
			 * for (ScCommittee committee2 :treeBeans) { if
			 * (tabBean.getTitleName().equalsIgnoreCase(
			 * committee2.getCommitteeType())) {
			 * tabBean.setScCommittee(this.getManagedBean().getScCommittee()); }
			 * }
			 */

			// }
		}
	}

	public void addCommitteMember(ScCommittee committee) {
		debugLogger("addCommitteMember");

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
		userSelection = this.getManagedBean().getCommitteMember()
				.getUserSelection();
		System.out.println("------------- : " + userSelection);
		this.getManagedBean().setExternalUsers(null);
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		committeeMember.setUserSelection(userSelection);
		this.getManagedBean().setCommitteMember(committeeMember);
		if (SelectionType.EXISITING.value().equalsIgnoreCase(
				getManagedBean().getCommitteMember().getUserSelection())) {
			getManagedBean().getCommitteMember().setExternalUser(true);

		} else if (SelectionType.New.value().equalsIgnoreCase(
				getManagedBean().getCommitteMember().getUserSelection())) {
			getManagedBean().getCommitteMember().setExternalUser(true);

		}
	}

	/*
	 * public void deleteSpecificationCommittee() {
	 * debugLogger("deleteSpecificationCommittee"); List<ScCommitteeMember>
	 * committeeMembersList = getManagedBean() .getSpecificationCommitteList();
	 * List<ScCommitteeMember> selectCommitteeMembersList = getManagedBean()
	 * .getSelectSpecificationCommitteeList(); if (committeeMembersList.size()
	 * == selectCommitteeMembersList.size()) { committeeMembersList.clear(); }
	 * else { for (ScCommitteeMember scCommitteeMember :
	 * selectCommitteeMembersList) {
	 * committeeMembersList.remove(scCommitteeMember); } } }
	 */
	/*
	 * public void addCloneToTecDtl() { List<ScCommitteeMember> list =
	 * getManagedBean() .getTechEvaluationCommitteList(); if (null == list) {
	 * list = new ArrayList<ScCommitteeMember>(); } for (ScCommitteeMember
	 * scCommitteeMember : getManagedBean()
	 * .getSelectSpecificationCommitteeList()) { list.add(scCommitteeMember); }
	 * getManagedBean().setTechEvaluationCommitteList(list);
	 * System.out.println("TEC Dtl : " +
	 * getManagedBean().getTechEvaluationCommitteList().size()); }
	 */
	public void okCommitteeAction() {
		debugLogger("okCommitteeAction");

		List<ScCommitteeMember> memberList = getManagedBean().getScCommittee()
				.getScCommitteeMembers();
		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}

		ScCommitteeMember member = getManagedBean().getCommitteMember();

		if (getManagedBean().getCommitteMember().getUserSelection().equals("N")) {
			System.out.println("******** New User *********");
			memberList.add(member);
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
				committeeMember.setPNP(user.isPMP());
				memberList.add(committeeMember);
			}
		}

		getManagedBean().getScCommittee().setScCommitteeMembers(memberList);

		List<TabBean> tabs = this.getManagedBean()
				.getScCommitteeMemberTabList();
		for (TabBean tabBean : tabs) {
			if (tabBean.getTitleName().equalsIgnoreCase(
					this.getManagedBean().getTabTitle())) {
				tabBean.setScCommittee(this.getManagedBean().getScCommittee());
				break;
			}
		}
	}

	/*
	 * public void okOpeningCommitteeAction() {
	 * debugLogger("okOpeningCommitteeAction"); List<ScCommitteeMember>
	 * memberList = getManagedBean() .getOpeningCommitteList(); if (null ==
	 * memberList) { memberList = new ArrayList<ScCommitteeMember>(); } if
	 * (getManagedBean().getCommitteMember().getUserSelection().equals("N")) {
	 * System.out.println("******** New User *********"); ScCommitteeMember
	 * member = getManagedBean().getCommitteMember(); memberList.add(member);
	 * this.getManagedBean().setOpeningCommitteList(memberList);
	 * System.out.println(" IC Passport : " + member.getIcPassport()); } else {
	 * System.out.println("******** Existing User *********"); for (PmUser user
	 * : getManagedBean().getSelectedPmUser()) { ScCommitteeMember
	 * committeeMember = new ScCommitteeMember();
	 * committeeMember.setMemberName(user.getUserName());
	 * committeeMember.setIcPassport(user.getIdentificationNo());
	 * committeeMember.setDesignation(user.getDesignation());
	 * committeeMember.setEmail(user.getPmAddressTypes().get(0)
	 * .getPmAddress().getEmail()); committeeMember.setGrade(user.getGrade());
	 * committeeMember.setPNP(user.isPMP()); memberList.add(committeeMember); }
	 * getManagedBean().setOpeningCommitteList(memberList);
	 * System.out.println("Passport : " +
	 * getManagedBean().getOpeningCommitteList().get(0) .getIcPassport()); } }
	 * 
	 * public void deleteOpeningCommittee() {
	 * debugLogger("deleteOpeningCommittee"); List<ScCommitteeMember>
	 * committeeMembersList = getManagedBean() .getOpeningCommitteList();
	 * 
	 * List<ScCommitteeMember> selectCommitteeMembersList = getManagedBean()
	 * .getSelectOpeningCommitteeList();
	 * 
	 * if (committeeMembersList.size() == selectCommitteeMembersList.size()) {
	 * committeeMembersList.clear(); } else { for (ScCommitteeMember
	 * scCommitteeMember : selectCommitteeMembersList) {
	 * committeeMembersList.remove(scCommitteeMember); } } }
	 * 
	 * public void okTechnicalEvaluationCommitteeAction() {
	 * debugLogger("okTechnicalEvaluationCommitteeAction");
	 * List<ScCommitteeMember> memberList = getManagedBean()
	 * .getTechEvaluationCommitteList(); if (null == memberList) { memberList =
	 * new ArrayList<ScCommitteeMember>(); } if
	 * (getManagedBean().getCommitteMember().getUserSelection().equals("N")) {
	 * System.out.println("******** New User *********"); ScCommitteeMember
	 * member = getManagedBean().getCommitteMember(); memberList.add(member);
	 * this.getManagedBean().setTechEvaluationCommitteList(memberList);
	 * System.out.println(" IC Passport : " + member.getIcPassport()); } else {
	 * System.out.println("******** Existing User *********"); for (PmUser user
	 * : getManagedBean().getSelectedPmUser()) { ScCommitteeMember
	 * committeeMember = new ScCommitteeMember();
	 * committeeMember.setMemberName(user.getUserName());
	 * committeeMember.setIcPassport(user.getIdentificationNo());
	 * committeeMember.setDesignation(user.getDesignation());
	 * committeeMember.setGrade(user.getGrade());
	 * committeeMember.setPNP(user.isPMP()); memberList.add(committeeMember); }
	 * getManagedBean().setTechEvaluationCommitteList(memberList);
	 * System.out.println("Passport : " +
	 * getManagedBean().getTechEvaluationCommitteList().get(0)
	 * .getIcPassport()); } }
	 * 
	 * public void deleteTechnicalEvaluationCommittee() {
	 * debugLogger("deleteTechnicalEvaluationCommittee");
	 * List<ScCommitteeMember> committeeMembersList = getManagedBean()
	 * .getTechEvaluationCommitteList(); List<ScCommitteeMember>
	 * selectCommitteeMembersList = getManagedBean()
	 * .getSelectTechEvaluationCommitteeList(); if (committeeMembersList.size()
	 * == selectCommitteeMembersList.size()) { committeeMembersList.clear(); }
	 * else { for (ScCommitteeMember scCommitteeMember :
	 * selectCommitteeMembersList) {
	 * committeeMembersList.remove(scCommitteeMember); } } }
	 * 
	 * public void okFinancailCommitteeAction() {
	 * debugLogger("okFinancailCommitteeAction"); List<ScCommitteeMember>
	 * memberList = getManagedBean() .getFinEvaluationCommitteList(); if (null
	 * == memberList) { memberList = new ArrayList<ScCommitteeMember>(); } if
	 * (getManagedBean().getCommitteMember().getUserSelection().equals("N")) {
	 * System.out.println("******** New User *********"); ScCommitteeMember
	 * member = getManagedBean().getCommitteMember(); memberList.add(member);
	 * this.getManagedBean().setFinEvaluationCommitteList(memberList);
	 * System.out.println(" IC Passport : " + member.getIcPassport()); } else {
	 * System.out.println("******** Existing User *********"); for (PmUser user
	 * : getManagedBean().getSelectedPmUser()) { ScCommitteeMember
	 * committeeMember = new ScCommitteeMember();
	 * committeeMember.setMemberName(user.getUserName());
	 * committeeMember.setIcPassport(user.getIdentificationNo());
	 * committeeMember.setDesignation(user.getDesignation());
	 * committeeMember.setGrade(user.getGrade());
	 * committeeMember.setPNP(user.isPMP()); memberList.add(committeeMember); }
	 * getManagedBean().setFinEvaluationCommitteList(memberList);
	 * System.out.println("Passport : " +
	 * getManagedBean().getFinEvaluationCommitteList().get(0) .getIcPassport());
	 * } }
	 * 
	 * public void deleteFinancialEvaluationCommittee() {
	 * debugLogger("deleteFinancialEvaluationCommittee");
	 * List<ScCommitteeMember> committeeMembersList = getManagedBean()
	 * .getFinEvaluationCommitteList(); List<ScCommitteeMember>
	 * selectCommitteeMembersList = getManagedBean()
	 * .getSelectFinEvaluationCommitteeList(); if (committeeMembersList.size()
	 * == selectCommitteeMembersList.size()) { committeeMembersList.clear(); }
	 * else { for (ScCommitteeMember scCommitteeMember :
	 * selectCommitteeMembersList) {
	 * committeeMembersList.remove(scCommitteeMember); } } }
	 * 
	 * public void okEvaluationCommitteeAction() {
	 * debugLogger("okEvaluationCommitteeAction");
	 * 
	 * List<ScCommitteeMember> memberList = getManagedBean()
	 * .getEvaluationCommitteList();
	 * 
	 * if (null == memberList) { memberList = new
	 * ArrayList<ScCommitteeMember>(); }
	 * 
	 * if (getManagedBean().getCommitteMember().getUserSelection().equals("N"))
	 * {
	 * 
	 * System.out.println("******** New User *********"); ScCommitteeMember
	 * member = getManagedBean().getCommitteMember();
	 * 
	 * memberList.add(member);
	 * this.getManagedBean().setEvaluationCommitteList(memberList);
	 * System.out.println(" IC Passport : " + member.getIcPassport()); } else {
	 * System.out.println("******** Existing User *********");
	 * 
	 * for (PmUser user : getManagedBean().getSelectedPmUser()) {
	 * ScCommitteeMember committeeMember = new ScCommitteeMember();
	 * committeeMember.setMemberName(user.getUserName());
	 * committeeMember.setIcPassport(user.getIdentificationNo());
	 * committeeMember.setDesignation(user.getDesignation());
	 * committeeMember.setGrade(user.getGrade());
	 * committeeMember.setPNP(user.isPMP()); memberList.add(committeeMember); }
	 * 
	 * getManagedBean().setEvaluationCommitteList(memberList);
	 * System.out.println("Passport : " +
	 * getManagedBean().getEvaluationCommitteList().get(0) .getIcPassport()); }
	 * }
	 * 
	 * public void deleteEvaluationCommittee() {
	 * debugLogger("deleteEvaluationCommittee"); List<ScCommitteeMember>
	 * committeeMembersList = getManagedBean() .getEvaluationCommitteList();
	 * 
	 * List<ScCommitteeMember> selectCommitteeMembersList = getManagedBean()
	 * .getSelectEvaluationCommitteeList();
	 * 
	 * if (committeeMembersList.size() == selectCommitteeMembersList.size()) {
	 * committeeMembersList.clear(); } else { for (ScCommitteeMember
	 * scCommitteeMember : selectCommitteeMembersList) {
	 * committeeMembersList.remove(scCommitteeMember); } } }
	 */

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
