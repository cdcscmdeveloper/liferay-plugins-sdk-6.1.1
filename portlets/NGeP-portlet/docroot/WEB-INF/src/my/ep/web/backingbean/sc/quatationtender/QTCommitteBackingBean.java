package my.ep.web.backingbean.sc.quatationtender;

import java.sql.Timestamp;
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

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
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
import my.ep.persistence.entity.pm.authorization.PmUserPreference;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
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
import org.primefaces.context.RequestContext;
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
		getManagedBean().setRoleTypes(getRoleTypeDropdownValues());
		if(null == this.getManagedBean().getCommitteMember()){
			this.getManagedBean().setCommitteMember(new ScCommitteeMember());
		}
	}

	public void onTabChangeAction(TabChangeEvent event) {
		String tabTitle = ((TabBean) event.getData()).getTitleName();

		/*ScQtCommittee scQtCommittee = ((TabBean) event.getData()).getScQtCommittee();
		this.getManagedBean().setScQtCommittee(scQtCommittee);*/
		getManagedBean().setTabTitle(tabTitle);
	}

	/**
	 * @param committeeRole
	 * @param types
	 * @return
	 */
	public SelectItem[] getRoleTypeDropdownValues(){
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		
		SelectItem[] items = getDropdownValuesBySearchBean(ParameterTypeConst.COMMITTEE_ROLE, searchBean);
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
				
				for(PmUser user : this.getManagedBean().getExternalUsers()) {
					if (user.getMemberRole() == null ) {
						user.setMemberRole(new PmParameter());
					}
				}
			}
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

		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setQtId(scQt.getQtId());
		CommitteResDTO resDTO = null;
		//ScQtCommittee qtCommitte = null;
		try {
			ICommitteSO committeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			resDTO = committeSO
					.retrieveCommitteMembersByQtIdAndCommType(reqDTO);
			if (resDTO != null) {
				//qtCommitte = resDTO.getScQtCommittee();
				this.getManagedBean().setCloneCommitteMemebers(
						resDTO.getCommMemberResultBean().getResultList());
			}
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
	}
	
	public void cloneCommitteePopupAction() {
		debugLogger("cloneCommitteePopupAction");
		getManagedBean().getCloneQtnTenderSearchBean().setQtTitle(null);
		getManagedBean().getCloneQtnTenderSearchBean().setQtNo(null);
		getManagedBean().getCloneQtnTenderSearchBean().setProcurementMode(null);
		getManagedBean().getCloneQtnTenderSearchBean().setFromCreatedDate(null);
		getManagedBean().getCloneQtnTenderSearchBean().setToCreatedDate(null);
		getManagedBean().getCloneQtnTenderSearchBean().setQtStatus(null);
		getManagedBean().setCloneTenderList(null);
		getManagedBean().setSearchcommitteeNode(null);
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

	}

	public void addCloneDtlToTabs() {
		debugLogger("addCloneDtlToTabs");
		TreeNode searchCommitteeTreeNode = getManagedBean().getSearchcommitteeNode();
		List<ScCommittee> committeesList = new ArrayList<ScCommittee>();
		if (searchCommitteeTreeNode != null) {
			for (TreeNode searchTreeNode : searchCommitteeTreeNode.getChildren()) {
				// ScCommittee
				@SuppressWarnings("rawtypes")
				ScCommittee committee = (ScCommittee) ((TreeNodeBean) searchTreeNode.getData()).getObject();
				//List<ScCommitteeMember> memberList = committee.getScCommitteeMembers();
				//this.getManagedBean().setScCommitteeMemberList(memberList);
				//committee.setScCommitteeMembers(memberList);
				//this.getManagedBean().getScQtCommittee().setScCommittee(committee);
				committeesList.add(committee);
			}
		}
		List<TabBean> tabs = this.getManagedBean().getScCommitteeMemberTabList();
		for (TabBean tabBean : tabs) {
			for (int i = 0; i < committeesList.size(); i++) {
					if (tabBean.getTitleName().equalsIgnoreCase(
							CommitteType.getValue(committeesList.get(i).getCommitteeType().getParameterCode()))) {
						tabBean.setScCommittee(committeesList.get(i));
						//tabBean.setScQtCommittee(committeesList.get(i).getScQtCommittees());
						break;
					}
			}
			
		}
	}

	public void addCommitteMember(ScQtCommittee committee, String titleName) {
		debugLogger("addCommitteMember");
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
		getManagedBean().setScQtCommittee(committee);
		getManagedBean().setTabTitle(titleName);
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
		logger.info("User Selection : " + userSelection);
		this.getManagedBean().setExternalUsers(null);
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		committeeMember.setUserSelection(userSelection);
		this.getManagedBean().setCommitteMember(committeeMember);
		if (SelectionType.Existing.value().equalsIgnoreCase(
				getManagedBean().getCommitteMember().getUserSelection())) {
			//getManagedBean().getCommitteMember().setExternalUser(true);

		} else if (SelectionType.New.value().equalsIgnoreCase(
				getManagedBean().getCommitteMember().getUserSelection())) {
			//getManagedBean().getCommitteMember().setExternalUser(true);

		}
	}

	public void deleteSpecificationCommittee() {
		debugLogger("deleteSpecificationCommittee");
		List<TabBean> beans = getManagedBean().getScCommitteeMemberTabList();
		//List<ScCommitteeMember> selectedList = this.getManagedBean().getSelectedCommitteMemebers();
		
		TabBean bean = beans.get(this.getManagedBean().getSubTabIndex());
		//bean.getScQtCommittee().getScCommittee().getScCommitteeMembers();
		
		/*for(ScCommitteeMember member : selectedList) {
			bean.getScQtCommittee().getScCommittee().getScCommitteeMembers().remove(member);
		}
		getManagedBean().getScCommitteeMemberTabList().add(this.getManagedBean().getSubTabIndex(), bean);*/
		
		for(ScCommitteeMember member : bean.getMembers()) {
			bean.getScQtCommittee().getScCommittee().getScCommitteeMembers().remove(member);
		}
		getManagedBean().getScCommitteeMemberTabList().add(this.getManagedBean().getSubTabIndex(), bean);
	}

	public void addCloneToTecDtl(ScCommittee committee) {
		debugLogger("addCloneToTecDtl");
		this.getManagedBean().setScCommittee(committee);
		this.getManagedBean().getScQtCommittee().setScCommittee(getManagedBean().getScCommittee());
		List<TabBean> tabs = this.getManagedBean()
				.getScCommitteeMemberTabList();
		for (TabBean tabBean : tabs) {
			if (tabBean.getTitleName().equalsIgnoreCase("TEC")) {
				tabBean.setScQtCommittee(this.getManagedBean().getScQtCommittee());
				break;
			}
			/*if (tabBean.getTitleName().equalsIgnoreCase("TEC")) {
				tabBean.setScCommittee(this.getManagedBean().getScCommittee());
				break;
			}*/
		}
	}

	/*
	 * public void addCloneToTecDtl() { debugLogger("addCloneToTecDtl");
	 * List<ScCommitteeMember> list = getManagedBean()
	 * .getTechEvaluationCommitteList(); if (null == list) { list = new
	 * ArrayList<ScCommitteeMember>(); } for (ScCommitteeMember
	 * scCommitteeMember : getManagedBean()
	 * .getSelectSpecificationCommitteeList()) { list.add(scCommitteeMember); }
	 * getManagedBean().setTechEvaluationCommitteList(list);
	 * System.out.println("TEC Dtl : " +
	 * getManagedBean().getTechEvaluationCommitteList().size()); }
	 */

	public void okCommitteeAction() {
		debugLogger("okCommitteeAction");
		IUserSO iUserSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		IParameterSO parameterSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		
		PmParameter parameter = new PmParameter();
		
		ScCommittee scCommittee = this.getManagedBean().getScQtCommittee().getScCommittee();
		ScQt scQt = this.getManagedBean().getQt();
		List<ScCommitteeMember> memberList = getManagedBean().getScQtCommittee().getScCommittee()
				.getScCommitteeMembers();
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
			parameter.setParameterId(585l);
			member.setMemberRole(parameter);
			member.setAccessRight("E");
			member.setPmOrgProfile(scQt.getPmOrgProfile());
			
			member.setScCommittee(scCommittee);
			
			PmUser user = new PmUser();
			user.setUserName(member.getMemberName());
			user.setLoginId(member.getMemberName());
			user.setIdentificationNo(member.getIcPassport());
			user.setDesignation(member.getDesignation());
			user.setGrade(member.getGrade());
			user.setPMP(member.isPNP());
			user.setEmail(member.getEmail());
			
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

			userReqDTO.setOrgType(OrganizationType.OTHERS.value());

			userReqDTO.setPmUser(user);
			UserResDTO resDTO = iUserSO.saveUser(userReqDTO);
			System.out.println("done");
			member.setPmUser(resDTO.getPmUser());
			setUserDetails(member);
			memberList.add(member);
			this.getManagedBean().setScCommitteeMemberList(memberList);
		} else {
			for (PmUser user : getManagedBean().getSelectedPmUser()) {
				ScCommitteeMember committeeMember = new ScCommitteeMember();
				committeeMember.setMemberName(user.getUserName());
				committeeMember.setIcPassport(user.getIdentificationNo());
				committeeMember.setDesignation(user.getDesignation());
				if(null!=user.getPmAddressTypes()){
					for(PmAddressType address:user.getPmAddressTypes()){
						if(null!=address.getPmAddress()){
							committeeMember.setEmail(address.getPmAddress().getEmail());
							break;
						}
					}
				}
				committeeMember.setGrade(user.getGrade());
				committeeMember.setPNP(user.isPMP());
				committeeMember.setPmUser(user);
				committeeMember.setPmOrgProfile(scQt.getPmOrgProfile());
				
				if(committeeMember.isPNP() == true) {
					parameter.setParameterId(583l);
					committeeMember.setMemberRole(parameter);
				} else {
					committeeMember.setMemberRole(user.getMemberRole());
				}
				committeeMember.setAccessRight("E");
				committeeMember.setScCommittee(scCommittee);
				setUserDetails(committeeMember);
				memberList.add(committeeMember);
			}
			this.getManagedBean().setScCommitteeMemberList(memberList);
		}
		
		scCommittee.setScCommitteeMembers(memberList);
		this.getManagedBean().getScQtCommittee().setScCommittee(scCommittee);
		
		//this.getManagedBean().setScQtCommittee(scQtCommittee);
		
		List<TabBean> tabs = this.getManagedBean().getScCommitteeMemberTabList();
		for (TabBean tabBean : tabs) {
			if (tabBean.getTitleName().equalsIgnoreCase(this.getManagedBean().getTabTitle())) {
				tabBean.setScQtCommittee(this.getManagedBean().getScQtCommittee());
				break;
			}
		}
		
		// Specification Committee validation
		RequestContext.getCurrentInstance().execute("newspecificationcommittee.hide()");
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
