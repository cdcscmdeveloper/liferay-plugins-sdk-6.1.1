package my.ep.web.backingbean.sc.quatationtender;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
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
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.CommitteType;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.bean.sc.quatationtender.UserCommitteeSearchBean;
import my.ep.web.bean.tree.TreeNodeBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.tabview.TabView;
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
		if (null == this.getManagedBean().getCommitteMember()) {
			this.getManagedBean().setCommitteMember(new ScCommitteeMember());
		}

		IPagingDataModel<ScQt> cloneSearchQTDataModel = getManagedBean()
				.getCloneSearchQTDataModel();

		if (cloneSearchQTDataModel == null) {
			getManagedBean().setCloneSearchQTDataModel(
					new QTPagingDataModel("clonesearchCommitteid",
							getManagedBean().getCloneQtnTenderSearchBean()));
		}

		IPagingDataModel<PmUser> userSearCommitteeDataModel = getManagedBean()
				.getUserSearchCommitteeDataModel();
		if (userSearCommitteeDataModel == null) {
			getManagedBean().setUserSearchCommitteeDataModel(
					new UserCommitteePagingDataModel(
							"existingcommitteesearchid", getManagedBean()
									.getUserCommitteeSearchBean()));
		}
	}

	private class UserCommitteePagingDataModel extends PagingDataModel<PmUser> {
		private static final long serialVersionUID = 1L;

		private UserCommitteeSearchBean userCommitteeSearchBean;

		public UserCommitteePagingDataModel(String componentId,
				UserCommitteeSearchBean userCommitteeSearchBean) {
			super(componentId);
			this.userCommitteeSearchBean = userCommitteeSearchBean;
		}

		@Override
		protected ISearchResultBean<PmUser> pageChangeListener(
				ISearchBean<PmUser> searchBean) {
			if (logger.isDebugEnabled()) {
				logger.debug("pageChangeListener");
			}

			IUserSO userSO;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserReqDTO userReqDTO = new UserReqDTO();

				populateUserSearchCriteria(userCommitteeSearchBean, searchBean);
				userReqDTO.setUserSearchBean(searchBean);
				UserResDTO resDto = userSO.searchUserProfile(userReqDTO);

				if (null != resDto.getUserResultBean().getResultList()) {
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
				userSO = null;
			}
		}
	}

	/**
	 * @param userCommitteeSearchBean
	 * @param searchBean
	 */
	private void populateUserSearchCriteria(
			UserCommitteeSearchBean userCommitteeSearchBean,
			ISearchBean<PmUser> userSearchBean) {
		if (userCommitteeSearchBean != null) {

			String name = userCommitteeSearchBean.getName();
			if (!StringUtils.isBlank(name)) {
				userSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.USER_NAME,
								SearchOperator.OPERATOR_LIKE.value(), name,
								true));
			}
			String idno = userCommitteeSearchBean.getIdentificationNo();
			if (!StringUtils.isBlank(idno)) {
				userSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.IDENTIFICATION_NO,
								SearchOperator.OPERATOR_LIKE.value(), idno,
								true));
			}
			String idtype = userCommitteeSearchBean.getIdentificationType();
			if (!StringUtils.isBlank(idtype)) {
				userSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"identificationCode.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(), idtype,
								true));
			}
			// Adding search filterbeans to searchbean.
			// userSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}

	public void searchUserAction() {
		getManagedBean().getUserSearchCommitteeDataModel().reloadData();
	}

	private class QTPagingDataModel extends PagingDataModel<ScQt> {
		private static final long serialVersionUID = 1L;

		private QtnTenderSearchBean cloneQtnTenderSearchBean;

		public QTPagingDataModel(String componentId,
				QtnTenderSearchBean cloneQtnTenderSearchBean) {
			super(componentId);
			this.cloneQtnTenderSearchBean = cloneQtnTenderSearchBean;
		}

		@Override
		public ISearchResultBean<ScQt> pageChangeListener(
				ISearchBean<ScQt> searchBean) {
			if (logger.isDebugEnabled()) {
				logger.debug("pageChangeListener");
			}

			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();

				populateCloneQTSearchCriteria(cloneQtnTenderSearchBean,
						searchBean);
				reqDTO.setQtSearchBean(searchBean);

				QuationTenderResDTO resDTO = detailSO.getQuationTenders(reqDTO);

				return resDTO.getQtResultBean();
			} finally {
				detailSO = null;
			}
		}
	}

	/**
	 * @param cloneQtnTenderSearchBean
	 * @param searchBean
	 */
	private void populateCloneQTSearchCriteria(
			QtnTenderSearchBean cloneQtnTenderSearchBean,
			ISearchBean<ScQt> qtSearchBean) {
		if (cloneQtnTenderSearchBean != null) {
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String qtTitle = cloneQtnTenderSearchBean.getQtTitle();
			if (!StringUtils.isBlank(qtTitle)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.QT_TITILE,
								SearchOperator.OPERATOR_LIKE.value(), qtTitle,
								true));
			}
			String qtNumber = cloneQtnTenderSearchBean.getQtNo();
			if (!StringUtils.isBlank(qtNumber)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_NUMBER);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtNumber);
				searchFilterBean.add(bean);
			}
			String procurementMode = cloneQtnTenderSearchBean
					.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("procurementMode.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								procurementMode));
			}
			/*Date fromCreatedDate = cloneQtnTenderSearchBean
					.getFromCreatedDate();
			Date toCreatedDate = cloneQtnTenderSearchBean.getToCreatedDate();
			if (null != fromCreatedDate || toCreatedDate != null) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromCreatedDate, toCreatedDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}*/
			Date fromCreatedDate = cloneQtnTenderSearchBean.getFromCreatedDate();
			Date toCreatedDate = cloneQtnTenderSearchBean.getToCreatedDate();
			if (null != fromCreatedDate || toCreatedDate != null) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(fromCreatedDate);
				Calendar fromDate = Calendar.getInstance();
				fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
						c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				fromDate.set(Calendar.MILLISECOND, 0);
				fromCreatedDate = fromDate.getTime();
				if (toCreatedDate != null) {
					Calendar c2 = Calendar.getInstance();
					c2.setTime(toCreatedDate);
					Calendar toDate = Calendar.getInstance();
					toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
							c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
					toDate.set(Calendar.MILLISECOND, 0);
					toCreatedDate = toDate.getTime();
				}
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromCreatedDate, toCreatedDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			String status = cloneQtnTenderSearchBean.getQtStatus();
			if (!StringUtils.isBlank(status)) {
				qtSearchBean
						.getDataFilterBeans()
						.add(new SearchFilterBean(
								"scQtStatus.pmStatus.statusId",
								SearchOperator.OPERATOR_EQUALS.value(), status));
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtStatus.current",
								SearchOperator.OPERATOR_EQUALS.value(), true));
			}
			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}

	public void cloneSearchQtAction() {
		getManagedBean().getCloneSearchQTDataModel().reloadData();
	}

	public void onTabChangeAction(TabChangeEvent event) {

		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext()
				.getRequestParameterMap();
		TabView tabView = (TabView) event.getComponent();
		String activeIndexValue = params.get(tabView.getClientId(context)
				+ "_tabindex");

		this.getManagedBean()
				.setSubTabIndex(Integer.parseInt(activeIndexValue));
		System.out.println("Index : " + getManagedBean().getSubTabIndex());
		String tabTitle = ((TabBean) event.getData()).getTitleName();

		/*
		 * ScQtCommittee scQtCommittee = ((TabBean)
		 * event.getData()).getScQtCommittee();
		 * this.getManagedBean().setScQtCommittee(scQtCommittee);
		 */
		getManagedBean().setTabTitle(tabTitle);
	}

	/**
	 * @param committeeRole
	 * @param types
	 * @return
	 */
	public List<PmParameterDesc> getRoleTypeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));

		return retrieveParametersBySearchBean(
				ParameterTypeConst.COMMITTEE_ROLE, searchBean);

	}

	public void cancelCommitteAction() {
		this.getManagedBean().getScCommitteeMemberList()
				.remove(this.getManagedBean().getCommitteMember());
	}

	public void onRowCloneAction(SelectEvent event) {
		debugLogger("onRowCloneAction");
		ScQt scQt = (ScQt) event.getObject();

		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType(getManagedBean().getCommType());
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
		debugLogger("populateCommitteeTreeNode");

		for (Entry<ScCommittee, List<ScCommitteeMember>> committeeEntry : committeeMap
				.entrySet()) {
			ScCommittee committee = committeeEntry.getKey();

			List<ScCommitteeMember> committeeMembersList = committeeEntry
					.getValue();

			committee.setScCommitteeMembers(committeeMembersList);

			TreeNode committeeNode = new DefaultTreeNode(
					new TreeNodeBean<ScCommittee>("committee", committee),
					searchcommitteeNode);

			for (ScCommitteeMember scCommitteeMember : committeeMembersList) {
				new DefaultTreeNode(new TreeNodeBean<ScCommitteeMember>(
						"scCommitteeMember", scCommitteeMember), committeeNode);
			}
		}
	}

	// To clone multiple committes.. or single committe..
	public void addCloneDtlToTabs() {
		debugLogger("addCloneDtlToTabs");
		ScQt scQt = this.getManagedBean().getQt();
		TreeNode[] treeNodes = getManagedBean().getSelectSearchcommitteeNode();
		List<ScCommittee> committeesList = new ArrayList<ScCommittee>();
		if (treeNodes.length > 0) {
			for (TreeNode treeNode : treeNodes) {
				ScCommittee committee = (ScCommittee) ((TreeNodeBean) treeNode
						.getData()).getObject();
				List<ScCommitteeMember> memberList = committee
						.getScCommitteeMembers();
				this.getManagedBean().setScCommitteeMemberList(memberList);
				committee.setScCommitteeMembers(memberList);
				committeesList.add(committee);
			}
		} else {
			throw new EpException(MessageConst.ERROR_760103);
		}

		// this.getManagedBean().setScCommittee(committee);
		List<ScCommitteeMember> committeeMemberList = new ArrayList<ScCommitteeMember>();
		checkCloneNeedRNot(scQt.getEvaluationType(), scQt.getQtStatus(),
				committeesList);
		List<TabBean> tabs = this.getManagedBean()
				.getScCommitteeMemberTabList();
		if (!committeesList.isEmpty()) {
			for (ScCommittee committe : committeesList) {
				for (TabBean tabBean : tabs) {
					if (tabBean.getTitleName().equalsIgnoreCase(
							CommitteType.getValue(committe.getCommitteeType()
									.getParameterCode()))) {
						ScCommittee cloneCommitte = tabBean.getScQtCommittee()
								.getScCommittee();
						for (ScCommitteeMember members : committe
								.getScCommitteeMembers()) {
							ScCommitteeMember member1 = null;
							try {
								member1 = (ScCommitteeMember) BeanUtils
										.cloneBean(members);
							} catch (IllegalAccessException
									| InstantiationException
									| InvocationTargetException
									| NoSuchMethodException e) {
								e.printStackTrace();
							}
							if (null != member1) {
								member1.setCommitteeMemberId(null);
								setUserDetails(member1);
								member1.setChangedDate(null);
								committeeMemberList.add(member1);
							}
							member1.setScCommittee(cloneCommitte);
						}
						committe.setScCommitteeMembers(committeeMemberList);
						if (null != tabBean.getScQtCommittee().getScCommittee()
								.getScCommitteeMembers()) {
							tabBean.getScQtCommittee().getScCommittee()
									.getScCommitteeMembers()
									.addAll(committe.getScCommitteeMembers());
						} else {
							tabBean.getScQtCommittee()
									.getScCommittee()
									.setScCommitteeMembers(
											committe.getScCommitteeMembers());
						}
					}
				}
			}
		}
	}

	public void cloneCommitteePopupAction() {
		debugLogger("cloneCommitteePopupAction");
		getManagedBean().getCloneSearchQTDataModel().reloadData();
		getManagedBean().getCloneQtnTenderSearchBean().setQtTitle(null);
		getManagedBean().getCloneQtnTenderSearchBean().setQtNo(null);
		getManagedBean().getCloneQtnTenderSearchBean().setProcurementMode(null);
		getManagedBean().getCloneQtnTenderSearchBean().setFromCreatedDate(null);
		getManagedBean().getCloneQtnTenderSearchBean().setToCreatedDate(null);
		getManagedBean().getCloneQtnTenderSearchBean().setQtStatus(null);
		getManagedBean().setCloneTenderList(null);
		getManagedBean().setSearchcommitteeNode(null);
	}

	public void committeeclonePopupAction(ScQtCommittee committee) {
		debugLogger("committeeclonePopupAction");
		getManagedBean().getCloneSearchQTDataModel().reloadData();
		getManagedBean().getCloneQtnTenderSearchBean().setQtTitle(null);
		getManagedBean().getCloneQtnTenderSearchBean().setQtNo(null);
		getManagedBean().getCloneQtnTenderSearchBean().setProcurementMode(null);
		getManagedBean().getCloneQtnTenderSearchBean().setFromCreatedDate(null);
		getManagedBean().getCloneQtnTenderSearchBean().setToCreatedDate(null);
		getManagedBean().getCloneQtnTenderSearchBean().setQtStatus(null);
		getManagedBean().setCloneTenderList(null);
		getManagedBean().setSearchcommitteeNode(null);
		String commType = committee.getScCommittee().getCommitteeType()
				.getParameterCode();
		this.getManagedBean().setCommType(commType);
		log.info("Committee Type : " + getManagedBean().getCommType());
	}

	public void addCommitteMember(ScQtCommittee committee, String titleName) {
		debugLogger("addCommitteMember");
		getManagedBean().getUserSearchCommitteeDataModel().reloadData();
		getManagedBean().getUserCommitteeSearchBean().setName(null);
		getManagedBean().getUserCommitteeSearchBean().setIdentificationType(
				null);
		getManagedBean().getUserCommitteeSearchBean().setIdentificationNo(null);
		getManagedBean().setSelectedPmUser(null);
		this.getManagedBean().setCommitteMember(null);
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		committeeMember.setUserSelection(QTConstants.USER_SELECTION);
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
			// getManagedBean().getCommitteMember().setExternalUser(true);

		} else if (SelectionType.New.value().equalsIgnoreCase(
				getManagedBean().getCommitteMember().getUserSelection())) {
			getManagedBean().getCommitteMember().setMemberRole(
					new PmParameter());

		}
	}

	public void addCloneToTecDtl() {
		debugLogger("addCloneToTecDtl");
		//TabBean bean = getManagedBean().getSelectedTabBean();
		List<ScCommitteeMember> selectedList = this.getManagedBean()
				.getToBeDelCommitteMemebers();
		if (null != selectedList && !selectedList.isEmpty()) {
			List<TabBean> tabs = this.getManagedBean()
					.getScCommitteeMemberTabList();
			for (TabBean tabBean : tabs) {
				log.debug("Title Name : " + tabBean.getTitleName());
				if (tabBean
						.getTitleName()
						.equalsIgnoreCase(
								ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE)) {
					List<ScCommitteeMember> committeeMembersList = new ArrayList<ScCommitteeMember>();
					for (ScCommitteeMember member : selectedList) {
						ScCommitteeMember member1 = new ScCommitteeMember();
						try {
							member1 = (ScCommitteeMember) BeanUtils
									.cloneBean(member);
						} catch (IllegalAccessException
								| InstantiationException
								| InvocationTargetException
								| NoSuchMethodException e) {
							e.printStackTrace();
						}
						if (null != member1) {
							// member1.setScCommittee(null);
							// scCommittee = member1.getScCommittee();
							// member1.setScCommittee(tabBean.getScQtCommittee().getScCommittee());
							committeeMembersList.add(member1);
						}
					}
					if (null != tabBean.getScQtCommittee().getScCommittee()
							.getScCommitteeMembers()) {
						tabBean.getScQtCommittee().getScCommittee()
								.getScCommitteeMembers()
								.addAll(committeeMembersList);
					} else {
						tabBean.getScQtCommittee().getScCommittee()
								.setScCommitteeMembers(committeeMembersList);
					}
					break;
				}
			}
		} else {
			throw new EpException("No members to clone to TEC");
		}
	}

	public void okCommitteeAction() {
		debugLogger("okCommitteeAction");

		UserReqDTO userReqDTO = new UserReqDTO();
		int committeTabIndex = this.getManagedBean().getSelectedTabIndex();
		TabBean selectedCommitteTabBean = this.getManagedBean()
				.getScCommitteeMemberTabList().get(committeTabIndex);
		ScCommittee scCommittee = selectedCommitteTabBean.getScQtCommittee()
				.getScCommittee();
		ScQt scQt = this.getManagedBean().getQt();
		List<ScCommitteeMember> memberList = scCommittee
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
			member.setMemberRole(getManagedBean().getCommitteMember()
					.getMemberRole());
			member.setAccessRight(QTConstants.ACCESS_RIGHT);
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
			parameterReqDTO.setParameterCode(getManagedBean()
					.getCommitteMember().getIdType());
			PmParameter identificationCode = getParameterSO()
					.retrieveParameterByTypeAndCode(parameterReqDTO)
					.getPmParameter();
			user.setIdentificationCode(identificationCode);

			// Nationality
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
			UserResDTO resDTO = new UserResDTO();
			try {
				resDTO = getUserSO().saveUser(userReqDTO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			member.setPmUser(resDTO.getPmUser());
			setUserDetails(member);
			memberList.add(member);
		} else {
			List<String> itemIds = new ArrayList<String>();
			if (!this.getManagedBean().getSelectedPmUser().isEmpty()) {
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
					if (user.isPMP() == true) {
						ParameterReqDTO reqDTO = new ParameterReqDTO();
						reqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
						reqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
						ParameterResDTO resDTO = getParameterSO()
								.retrieveParameterByTypeAndCode(reqDTO);
						committeeMember.setMemberRole(resDTO.getPmParameter());
					} else {
						List<PmParameterDesc> roles = getManagedBean()
								.getRoleTypes();
						for (PmParameterDesc parameter : roles) {
							if (parameter
									.getPmParameter()
									.getParameterId()
									.equals(user.getMemberRole()
											.getParameterId())) {
								ParameterReqDTO reqDTO = new ParameterReqDTO();
								reqDTO.setParameterId(user.getMemberRole()
										.getParameterId());
								ParameterResDTO resDTO = getParameterSO()
										.retrieveParameterById(reqDTO);
								committeeMember.setMemberRole(resDTO
										.getPmParameter());
								break;
							} else {
								committeeMember.setMemberRole(user
										.getMemberRole());
							}
						}
					}
					committeeMember.setAccessRight(QTConstants.ACCESS_RIGHT);
					committeeMember.setScCommittee(scCommittee);
					setUserDetails(committeeMember);
					// memberList.add(committeeMember);
					if (scCommittee.getScCommitteeMembers().isEmpty()) {
						// scCommittee.getScCommitteeMembers().add(committeeMember);
						memberList.add(committeeMember);
					} else {
						for (ScCommitteeMember committeeMember2 : scCommittee
								.getScCommitteeMembers()) {
							itemIds.add(committeeMember2.getIcPassport());
						}
						if (itemIds.size() > 0) {
							if (itemIds.contains(user.getIdentificationNo())) {
								throw new EpException(MessageConst.ERROR_760120);
							} else {
								memberList.add(committeeMember);
							}
						}
					}
				}
			} else {
				throw new EpException(MessageConst.ERROR_760121);
			}

		}
		scCommittee.setScCommitteeMembers(memberList);
		selectedCommitteTabBean.getScQtCommittee().setScCommittee(scCommittee);

		// Specification Committee validation
		RequestContext.getCurrentInstance().execute(
				"newspecificationcommittee.hide()");
	}

	public void reset() {
		debugLogger("reset");
		this.getManagedBean().getUserCommitteeSearchBean().setName(null);
		this.getManagedBean().getUserCommitteeSearchBean()
				.setIdentificationType(null);
		this.getManagedBean().getUserCommitteeSearchBean()
				.setIdentificationNo(null);
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
		this.getManagedBean().setSearchcommitteeNode(null);
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

	public void preRenderView() {
		/*
		 * renewSesionObject(); HttpServletRequest servletRequest = PortalUtil
		 * .getHttpServletRequest(new FacesScopes().getRequest());
		 * HttpServletRequest originalServletRequest = PortalUtil
		 * .getOriginalServletRequest(servletRequest); String taskId =
		 * originalServletRequest.getParameter("taskId");
		 * this.getManagedBean().setTaskId(taskId); log.info("taskId : " +
		 * taskId); IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
		 * .getServiceObject(IBpmSO.class); // TODO FacesScopes facesScopes =
		 * new FacesScopes(); String ctx =
		 * facesScopes.getBPMAuthenticationCtxToken(); // String ctx =
		 * bpmSO.authenticate("roslan", "welcome1"); BpmRequestDTO reqDTO = new
		 * BpmRequestDTO(); reqDTO.setCtxToken(ctx); reqDTO.setTaskId(taskId);
		 * BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO); Object obj =
		 * res.getPojo(); this.getManagedBean().setOutComes(res.getAction());
		 * SCQuotationTenderDataType quotationTenderDataType =
		 * (SCQuotationTenderDataType) obj;
		 * retrieveCommitteeAppointmentDetails(new Long(
		 * quotationTenderDataType.getQtId()));
		 * this.getManagedBean().setQuotationTenderDataType(
		 * quotationTenderDataType);
		 * this.getManagedBean().setProcessName(res.getProcessName());
		 * FacesContext fc = FacesContext.getCurrentInstance();
		 * fc.getApplication().getNavigationHandler() .handleNavigation(fc,
		 * null, QTConstants.MAINTANANCE_PAGE); }
		 * 
		 * public void retrieveCommitteeAppointmentDetails(Long qtId) {
		 * debugLogger("retrieveCommitteeAppointmentTabDtl"); //
		 * this.getManagedBean().setActiveIndex(1);
		 * 
		 * IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
		 * .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
		 * IQTDetailSO.class); QuationTenderReqDTO tenderReqDTO = new
		 * QuationTenderReqDTO();
		 * tenderReqDTO.setDocType(QTConstants.DOCTYPE_QT);
		 * tenderReqDTO.setQtId(qtId); QuationTenderResDTO tenderResDTO = new
		 * QuationTenderResDTO(); tenderResDTO =
		 * QtDetailsSO.retriveQTDetails(tenderReqDTO); if
		 * (tenderResDTO.getQtDetail() != null) {
		 * getManagedBean().setQt(tenderResDTO.getQtDetail()); } List<TabBean>
		 * committeTabs = this.getManagedBean() .getScCommitteeMemberTabList();
		 * // qtId = getManagedBean().getQt().getQtId(); if (null != qtId &&
		 * null == committeTabs) { List<ScCommitteeMember> members=null; String
		 * evaluationType = this.getManagedBean().getQt() .getEvaluationType();
		 * committeTabs = new ArrayList<TabBean>(); // BO and specification
		 * committe is common for both evaluations.. committeTabs.add(new
		 * TabBean(QTConstants.BUSINESS_OWNER, getQtCommiteDtlsByQtIdNType("BO",
		 * qtId),null)); committeTabs.add(new TabBean(
		 * QTConstants.SPECIFICATION_COMMITTEE_TAB, getQtCommiteDtlsByQtIdNType(
		 * CommitteType.getValue("SpecificationCommittee"), qtId),null)); //
		 * evaluation type is one tier only oc is added.. if
		 * ("1".equals(evaluationType)) { committeTabs.add(new TabBean(
		 * QTConstants.EVALUATION_COMMITTEE_TAB, getQtCommiteDtlsByQtIdNType(
		 * CommitteType.getValue("EvaluationCommittee"), qtId),null)); } else {
		 * committeTabs .add(new TabBean(QTConstants.OPENING_COMMITTEE_TAB,
		 * getQtCommiteDtlsByQtIdNType(CommitteType
		 * .getValue("OpeningCommittee"), qtId),null)); committeTabs.add(new
		 * TabBean("TEC", getQtCommiteDtlsByQtIdNType("TEC", qtId),null));
		 * committeTabs.add(new TabBean("FEC",
		 * getQtCommiteDtlsByQtIdNType("FEC", qtId),null)); } List<PmParameter>
		 * finalizeList = retrieveFinalizationSecratryDtl(qtId);
		 * log.info("Finalization Secratory Size : " + finalizeList.size());
		 * this.getManagedBean().setFinalizationSecratary(finalizeList); String
		 * pmCode = null; for (PmParameter pmParameter : finalizeList) { pmCode
		 * = pmParameter.getParameterCode(); } if (null !=
		 * getManagedBean().getQt().getEstimatedAmount() &&
		 * getManagedBean().getQt().getEstimatedAmount().intValue() >
		 * SourcingConstants.ESTMT_FIVE_HUNDERED_THOUSAND) {
		 * committeTabs.add(new TabBean( QTConstants.FINALIZATION_SECRETARY_TAB,
		 * getFinalizationCommitteeTypeDtl(pmCode, null),null)); } else { //
		 * retrieveFinalizationSecratryDtl(qtId); committeTabs.add(new TabBean(
		 * QTConstants.FINALIZATION_SECRETARY_TAB,
		 * getFinalizationCommitteeTypeDtl(pmCode, null),null)); }
		 * this.getManagedBean().setScCommitteeMemberTabList(committeTabs); }
		 */
	}

	/**
	 * @param qtId
	 * @return
	 */
	/*
	 * private List<PmParameter> retrieveFinalizationSecratryDtl(Long qtId) {
	 * ICommitteSO committeSO; CommitteReqDTO reqDTO = new CommitteReqDTO();
	 * reqDTO.setQtId(qtId); CommitteResDTO resDTO = null; try { committeSO =
	 * BusinessDelegate.getInstance().getServiceObject(
	 * ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class); resDTO =
	 * committeSO.retrieveFinalizationSecratryDtls(reqDTO); } finally {
	 * committeSO = null; } return resDTO.getFinalizationSecratry(); }
	 * 
	 * private ScQtCommittee getQtCommiteDtlsByQtIdNType(String commType, Long
	 * qtId) { IParameterSO parameterSO = BusinessDelegate.getInstance()
	 * .getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
	 * IParameterSO.class); ParameterReqDTO parameterReqDTO = new
	 * ParameterReqDTO(); ParameterResDTO parameterResDTO = null; CommitteReqDTO
	 * reqDTO = new CommitteReqDTO(); reqDTO.setCommitteeType(commType);
	 * reqDTO.setQtId(qtId); CommitteResDTO resDTO = null; ScQtCommittee
	 * qtCommitte = null; ICommitteSO committeSO; try { committeSO =
	 * BusinessDelegate.getInstance().getServiceObject(
	 * ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class); resDTO =
	 * committeSO.retrieveCommitteByQtIdNCommType(reqDTO); qtCommitte =
	 * resDTO.getScQtCommittee(); if
	 * (ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER
	 * .equalsIgnoreCase(commType)) { parameterReqDTO = new ParameterReqDTO();
	 * parameterReqDTO .setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
	 * parameterReqDTO
	 * .setParameterCode(ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER);
	 * parameterResDTO = parameterSO
	 * .retrieveParameterByTypeAndCode(parameterReqDTO); } else if
	 * (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
	 * .equalsIgnoreCase(commType)) { parameterReqDTO = new ParameterReqDTO();
	 * parameterReqDTO .setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
	 * parameterReqDTO
	 * .setParameterCode(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
	 * parameterResDTO = parameterSO
	 * .retrieveParameterByTypeAndCode(parameterReqDTO); } else if
	 * (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
	 * .equalsIgnoreCase(commType)) { parameterReqDTO = new ParameterReqDTO();
	 * parameterReqDTO .setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
	 * parameterReqDTO
	 * .setParameterCode(ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE);
	 * parameterResDTO = parameterSO
	 * .retrieveParameterByTypeAndCode(parameterReqDTO); } else if
	 * (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
	 * .equalsIgnoreCase(commType)) { parameterReqDTO = new ParameterReqDTO();
	 * parameterReqDTO .setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
	 * parameterReqDTO
	 * .setParameterCode(ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE);
	 * parameterResDTO = parameterSO
	 * .retrieveParameterByTypeAndCode(parameterReqDTO); } else if
	 * (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
	 * .equalsIgnoreCase(commType)) { parameterReqDTO = new ParameterReqDTO();
	 * parameterReqDTO .setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
	 * parameterReqDTO .setParameterCode(ParameterConst.
	 * COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE); parameterResDTO =
	 * parameterSO .retrieveParameterByTypeAndCode(parameterReqDTO); } else if
	 * (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
	 * .equalsIgnoreCase(commType)) { parameterReqDTO = new ParameterReqDTO();
	 * parameterReqDTO .setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
	 * parameterReqDTO .setParameterCode(ParameterConst.
	 * COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE); parameterResDTO =
	 * parameterSO .retrieveParameterByTypeAndCode(parameterReqDTO); } if (null
	 * == qtCommitte) { qtCommitte = new ScQtCommittee();
	 * qtCommitte.setScQt(this.getManagedBean().getQt());
	 * setUserDetails(qtCommitte); qtCommitte.setCommitteeActivities(null);
	 * qtCommitte.setQtCommitteRemarks(null); qtCommitte.setScQtMeetings(null);
	 * ScCommittee committee = new ScCommittee();
	 * committee.setCommitteeType(parameterResDTO.getPmParameter());
	 * setUserDetails(committee); PmStatus committeStatus = new PmStatus();
	 * committeStatus.setStatusId(60200l);
	 * committee.setCommitteStatus(committeStatus); committee
	 * .setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
	 * qtCommitte.setScCommittee(committee); } } finally { committeSO = null; }
	 * return qtCommitte; }
	 */

	public void roleChangeListner(ScCommitteeMember member) {
		List<ScCommitteeMember> members = this.getManagedBean()
				.getSelectedTabBean().getScQtCommittee().getScCommittee()
				.getScCommitteeMembers();
		for (ScCommitteeMember committeeMember : members) {
			if (committeeMember.getIcPassport().equalsIgnoreCase(
					member.getIcPassport())) {
				List<PmParameterDesc> roles = getManagedBean().getRoleTypes();
				for (PmParameterDesc parameter : roles) {
					if (parameter.getPmParameter().getParameterId()
							.equals(member.getMemberRole().getParameterId())) {
						member.setMemberRole(parameter.getPmParameter());
						break;
					}
				}
			}
		}

	}

	// To check whether committe needs to be clone or not..
	private void checkCloneNeedRNot(String evalStg, PmStatus status,
			List<ScCommittee> committes) {
		for (ScCommittee committee : committes) {
			if (ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER
					.equalsIgnoreCase(committee.getCommitteeType()
							.getParameterCode())) {
				throw new EpException(MessageConst.ERROR_760104,
						new String[] { committee.getCommitteeType()
								.getParameterCode() });
			}
		}
		if (!status.getStatusId().equals(
				QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING)
				&& status.getStatusId() > QTStatusConst.QT_PENDING_QT_SUBMISSION) {
			for (ScCommittee committee : committes) {
				if (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
						.equalsIgnoreCase(committee.getCommitteeType()
								.getParameterCode())) {
					throw new EpException(MessageConst.ERROR_760104,
							new String[] { committee.getCommitteeType()
									.getParameterCode() });
				}
			}
		}

		if (SourcingConstants.ONE_TIER.equals(evalStg)
				&& status.getStatusId() < QTStatusConst.QT_SUPPLIER_FINALIZATION) {
			for (ScCommittee committee : committes) {
				if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
						.equalsIgnoreCase(committee.getCommitteeType()
								.getParameterCode())
						|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
								.equalsIgnoreCase(committee.getCommitteeType()
										.getParameterCode())
						|| ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
								.equalsIgnoreCase(committee.getCommitteeType()
										.getParameterCode())) {
					throw new EpException(MessageConst.ERROR_760104,
							new String[] { committee.getCommitteeType()
									.getParameterCode() });
				}
			}
		} else if (SourcingConstants.TWO_TIER.equals(evalStg)
				&& status.getStatusId() < QTStatusConst.QT_SUPPLIER_FINALIZATION) {
			for (ScCommittee committee : committes) {
				if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(committee.getCommitteeType()
								.getParameterCode())) {
					throw new EpException(MessageConst.ERROR_760104,
							new String[] { committee.getCommitteeType()
									.getParameterCode() });
				}
			}
		}
	}

	public void deleteSpecificationCommittee() {
		debugLogger("deleteSelected Committe Mmeners");
		TabBean bean = this.getManagedBean().getSelectedTabBean();
		List<ScCommitteeMember> selectedList = this.getManagedBean()
				.getToBeDelCommitteMemebers();
		List<ScCommitteeMember> members = new ArrayList<ScCommitteeMember>();
		for (int i = 0; i < selectedList.size(); i++) {
			ScCommitteeMember committeeMember = selectedList.get(i);
			bean.getScQtCommittee().getScCommittee().getScCommitteeMembers()
					.remove(selectedList.get(i));
			if (null != committeeMember.getCommitteeMemberId()) {
				selectedList.get(i).setDeleted(true);
				members.add(selectedList.get(i));
			} else {
				selectedList.remove(i);
			}
		}
		bean.setMembers(members);
		this.getManagedBean().getScCommitteeMemberTabList()
				.remove(this.getManagedBean().getSelectedTabIndex());
		this.getManagedBean().getScCommitteeMemberTabList()
				.add(this.getManagedBean().getSelectedTabIndex(), bean);

	}
}
