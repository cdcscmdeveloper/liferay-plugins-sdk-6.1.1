/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.sc.quatationtender;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.comparator.sc.MemberRoleComparator;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constants.SCProcessNameConst;
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
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRole;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
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
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.bean.sc.quatationtender.UserCommitteeSearchBean;
import my.ep.web.bean.tree.TreeNodeBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * The Class QTCommitteBackingBean.
 * 
 * @author Viswanath
 */
@ManagedBean(name = "qTCommitteBackingBean")
@RequestScoped
public class QTCommitteBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	/** The Constant logger. */
	private static final Logger logger = EpLogger
			.getLogger(QTCommitteBackingBean.class);

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}

	/**
	 * Instantiates a new QT committe backing bean.
	 */
	public QTCommitteBackingBean() {

		if (null == this.getManagedBean().getCommitteMember()) {
			this.getManagedBean().setCommitteMember(new ScCommitteeMember());
		}

	}

	/**
	 * The Class UserCommitteePagingDataModel.
	 */
	private class UserCommitteePagingDataModel extends PagingDataModel<PmUser> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The user committee search bean. */
		private UserCommitteeSearchBean userCommitteeSearchBean;

		/**
		 * Instantiates a new user committee paging data model.
		 * 
		 * @param componentId
		 *            the component id
		 * @param userCommitteeSearchBean
		 *            the user committee search bean
		 */
		public UserCommitteePagingDataModel(String componentId,
				UserCommitteeSearchBean userCommitteeSearchBean) {
			super(componentId, false);
			this.userCommitteeSearchBean = userCommitteeSearchBean;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
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

				return userSO.searchUserProfileDtl(userReqDTO).getUserResultBean();
			} finally {
				userSO = null;
			}
		}
	}

	/**
	 * Method to prepare user search criteria.
	 * 
	 * @param userCommitteeSearchBean
	 *            the user committee search bean
	 * @param userSearchBean
	 *            the user search bean
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
			userSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(), 
							DbValueConst.RECORD_STATUS_ACTIVE));
			
			userSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmUserOrgs.recordStatusFlg", 
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE)					
					);			
			
			/*Removed based on Ministry search for Add Users
			 * if (null != this.getManagedBean().getQt().getPmOrgProfile()) {
				userSearchBean
						.getDataFilterBeans()
						.add(new SearchMultiFilterBean(
								new SearchFilterBean(
										"pmUserOrgs.pmOrgProfile."
												+ OrgProfileUtil
														.getSearchBeanParentString(OrganizationType.MINISTRY
																.value()),
										SearchOperator.OPERATOR_EQUALS.value(),
										retrieveMinistryOrgProfile(
												this.getManagedBean().getQt()
														.getPmOrgProfile()
														.getOrgProfileId())
												.getOrgProfileId()),

								new SearchFilterBean(
										"pmUserOrgs.pmOrgProfile.orgTypeCode.parameterCode",
										SearchOperator.OPERATOR_EQUALS.value(),
										ParameterConst.ORG_TYPE_OTHERS,
										SearchCondition.CONDITION_OR.value())));
			}*/
			// Adding search filterbeans to searchbean.
			// userSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}

	/**
	 * Search user action.
	 */
	public void searchUserAction() {

		getManagedBean().getUserSearchCommitteeDataModel().reloadData();
	}

	/**
	 * The Class QTPagingDataModel.
	 */
	private class QTPagingDataModel extends PagingDataModel<ScQt> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The clone qtn tender search bean. */
		private QtnTenderSearchBean cloneQtnTenderSearchBean;

		/**
		 * Instantiates a new QT paging data model.
		 * 
		 * @param componentId
		 *            the component id
		 * @param cloneQtnTenderSearchBean
		 *            the clone qtn tender search bean
		 */
		public QTPagingDataModel(String componentId,
				QtnTenderSearchBean cloneQtnTenderSearchBean) {
			super(componentId, false);
			this.cloneQtnTenderSearchBean = cloneQtnTenderSearchBean;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
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

				QuationTenderResDTO resDTO = detailSO
						.retriveQuationTenderDetailsBySearchCriteria(reqDTO);

				return resDTO.getQtResultBean();
			} finally {
				detailSO = null;
			}
		}
	}

	/**
	 * Populate clone qt search criteria.
	 * 
	 * @param cloneQtnTenderSearchBean
	 *            the clone qtn tender search bean
	 * @param qtSearchBean
	 *            the qt search bean
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
			/*
			 * Date fromCreatedDate = cloneQtnTenderSearchBean
			 * .getFromCreatedDate(); Date toCreatedDate =
			 * cloneQtnTenderSearchBean.getToCreatedDate(); if (null !=
			 * fromCreatedDate || toCreatedDate != null) { SearchFilterBean bean
			 * = new SearchFilterBean();
			 * bean.setSearchField(QTConstants.CREATED_DATE);
			 * bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
			 * SearchBetweenValue<Date> betweenVal = new
			 * SearchBetweenValue<Date>( fromCreatedDate, toCreatedDate);
			 * bean.setSearchValue(betweenVal); searchFilterBean.add(bean); }
			 */
			Date fromCreatedDate = cloneQtnTenderSearchBean
					.getFromCreatedDate();
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

			Date fromPublishDate = cloneQtnTenderSearchBean
					.getFromPublishDate();
			Date toPublishDate = cloneQtnTenderSearchBean.getToPublishDate();
			if (null != fromPublishDate || null != toPublishDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.PUBLISH_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromPublishDate, toPublishDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}

			Date fromClosingDate = cloneQtnTenderSearchBean
					.getFromClosingDate();
			Date toClosingDate = cloneQtnTenderSearchBean.getToClosingDate();
			if (null != fromClosingDate || null != toClosingDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CLOSED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromClosingDate, toClosingDate);
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
			if (null != this.getManagedBean().getQt().getPmOrgProfile()) {
				qtSearchBean
						.getDataFilterBeans()
						.add(new SearchFilterBean(
								"pmOrgProfile."
										+ OrgProfileUtil
												.getSearchBeanParentString(OrganizationType.MINISTRY
														.value()),
								SearchOperator.OPERATOR_EQUALS.value(),

								retrieveMinistryOrgProfile(
										this.getManagedBean().getQt()
												.getPmOrgProfile()
												.getOrgProfileId())
										.getOrgProfileId()

						));
			}
			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}

	/**
	 * Clone search qt action.
	 */
	public void cloneSearchQtAction() {
		getManagedBean().setCloneCommitteeSelectQt(null);

		getManagedBean().getCloneSearchQTDataModel().reloadData();
	}

	/**
	 * On tab change action.
	 * 
	 * @param event
	 *            the event
	 */
	public void onTabChangeAction(TabChangeEvent event) {

		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext()
				.getRequestParameterMap();
		TabView tabView = (TabView) event.getComponent();
		String activeIndexValue = params.get(tabView.getClientId(context)
				+ "_tabindex");

		this.getManagedBean()
				.setSubTabIndex(Integer.parseInt(activeIndexValue));

		String tabTitle = ((TabBean) event.getData()).getTitleName();

		/*
		 * ScQtCommittee scQtCommittee = ((TabBean)
		 * event.getData()).getScQtCommittee();
		 * this.getManagedBean().setScQtCommittee(scQtCommittee);
		 */
		getManagedBean().setTabTitle(tabTitle);
	}

	/**
	 * Cancel committe action.
	 */
	public void cancelCommitteAction() {
		this.getManagedBean().getScCommitteeMemberList()
				.remove(this.getManagedBean().getCommitteMember());
	}

	public void onRowCloneCommitteeApprovalsAction(SelectEvent event) {
		debugLogger("onRowCloneCommitteeApprovalsAction");
		ScQt scQt = (ScQt) event.getObject();
		this.getManagedBean().setCloneCommitteeSelectQt(scQt);

		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType(this.getManagedBean().getCommType());
		reqDTO.setQtId(scQt.getQtId());
		CommitteResDTO resDTO = new CommitteResDTO();
		try {
			ICommitteSO committeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			resDTO = committeSO
					.retrieveCommitteMembersByQtIdAndCommType(reqDTO);
			this.getManagedBean().setCloneCommitteMemebers(null);
			if (null != resDTO
					&& null != resDTO.getCommMemberResultBean()
					&& null != resDTO.getCommMemberResultBean().getResultList()
					&& !resDTO.getCommMemberResultBean().getResultList()
							.isEmpty()) {
				this.getManagedBean().setCloneCommitteMemebers(
						resDTO.getCommMemberResultBean().getResultList());
			}
		} finally {
		}
	}

	public void addCloneCommitteeMembersApprovalsAction() {
		debugLogger("addCloneCommitteeMembersApprovalsAction");

		int committeTabIndex = this.getManagedBean().getSelectedTabIndex();
		TabBean selectedCommitteTabBean = this.getManagedBean()
				.getScCommitteeMemberTabList().get(committeTabIndex);
		ScCommittee scCommittee = selectedCommitteTabBean.getScQtCommittee()
				.getScCommittee();
		List<ScCommitteeMember> memberList = scCommittee
				.getScCommitteeMembers();
		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}
		List<String> itemIds = new ArrayList<String>();
		List<ScCommitteeMember> selectedCloneMembersList = getManagedBean()
				.getSelectedCommitteMemebers();
		if (!selectedCloneMembersList.isEmpty()) {
			if (scCommittee.getScCommitteeMembers().isEmpty()) {
				for (ScCommitteeMember member : selectedCloneMembersList) {
					ScCommitteeMember member1 = null;
					try {
						member1 = (ScCommitteeMember) BeanUtils
								.cloneBean(member);
					} catch (IllegalAccessException | InstantiationException
							| InvocationTargetException | NoSuchMethodException e) {
						e.printStackTrace();
					}
					if (null != member1) {
						member1.setBrandNew(true);
						member1.setCommitteeMemberId(null);
						setUserDetails(member1);
						member1.setChangedDate(null);
						if (member1.isConsolidate()) {
							this.getManagedBean().setSelectScCommitteMember(
									member1);
						}
						member1.setScCommittee(scCommittee);
						memberList.add(member1);
					}
				}
			} else {
				for (ScCommitteeMember committee : scCommittee
						.getScCommitteeMembers()) {
					itemIds.add(committee.getIcPassport());
				}
				for (ScCommitteeMember committeeMember : selectedCloneMembersList) {
					if (itemIds.size() > 0) {
						if (itemIds.contains(committeeMember.getIcPassport())) {
							throw new EpException(MessageConst.ERROR_760120);
						} else {
							committeeMember.setCommitteeMemberId(null);
							setUserDetails(committeeMember);
							committeeMember.setChangedDate(null);
							committeeMember.setScCommittee(scCommittee);
							memberList.add(committeeMember);
							scCommittee.setScCommitteeMembers(memberList);
						}
					}
				}
			}
		} else {
			throw new EpException(MessageConst.ERROR_760121);
		}
		Collections.sort(memberList,new MemberRoleComparator());
		scCommittee.setScCommitteeMembers(memberList);
		selectedCommitteTabBean.getScQtCommittee().setScCommittee(scCommittee);

		// Clone Committee Approval validation
		RequestContext.getCurrentInstance().execute("screen2.hide()");
		this.getManagedBean().setAutoUpdate(true);
	}

	/**
	 * On row clone action.
	 * 
	 * @param event
	 *            the event
	 */
	public void onRowCloneAction(SelectEvent event) {
		debugLogger("onRowCloneAction");
		this.getManagedBean().setSearchcommitteeNode(new DefaultTreeNode());
		ScQt scQt = (ScQt) event.getObject();
		this.getManagedBean().setCloneCommitteeSelectQt(scQt);

		CommitteReqDTO reqDTO = new CommitteReqDTO();
		// reqDTO.setCommitteeType(getManagedBean().getCommType());
		reqDTO.setQtId(scQt.getQtId());
		reqDTO.setEvaluationType(getManagedBean().getQt().getEvaluationType());
		CommitteResDTO resDTO = null;
		try {
			ICommitteSO committeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			resDTO = committeSO.retrieveCommittesByQtId(reqDTO);
			List<ScCommittee> committes = resDTO.getCommittes();
			if (null != committes) {
				DefaultTreeNode searchcommitteeNode = new DefaultTreeNode(
						"root", null);
				populateCommitteeTreeNode(committes, searchcommitteeNode);

				this.getManagedBean().setSearchcommitteeNode(
						searchcommitteeNode);
			}
		} finally {

		}
	}

	/**
	 * Populate committee tree node.
	 * 
	 * @param committes
	 *            the committes
	 * @param searchcommitteeNode
	 *            the searchcommittee node
	 */
	private void populateCommitteeTreeNode(List<ScCommittee> committes,
			TreeNode searchcommitteeNode) {
		debugLogger("populateCommitteeTreeNode");

		for (ScCommittee committe : committes) {
			TreeNode committeeNode = new DefaultTreeNode(
					new TreeNodeBean<ScCommittee>("committee", committe),
					searchcommitteeNode);
			List<ScCommitteeMember> committeeMembersList = committe
					.getScCommitteeMembers();
			for (ScCommitteeMember scCommitteeMember : committeeMembersList) {
				new DefaultTreeNode(new TreeNodeBean<ScCommitteeMember>(
						"scCommitteeMember", scCommitteeMember), committeeNode);
			}
		}
	}

	// To clone multiple committes.. or single committe..
	/**
	 * Adds the clone dtl to tabs.
	 * 
	 * @param isApprovalFlow
	 *            the is approval flow
	 */
	public void addCloneDtlToTabs(boolean isApprovalFlow) {
		debugLogger("addCloneDtlToTabs");

		ScQt scQt = this.getManagedBean().getQt();
		TreeNode[] treeNodes = getManagedBean().getSelectSearchcommitteeNode();
		Map<String, List<ScCommitteeMember>> clonedCommittes = new HashMap<String, List<ScCommitteeMember>>();

		if (null != treeNodes && treeNodes.length > 0) {
			for (int nodes = 0; nodes < treeNodes.length; nodes++) {
				TreeNodeBean<?> treeNodeBean = (TreeNodeBean<?>) treeNodes[nodes]
						.getData();
				String type = treeNodeBean.getType();

				if ("committee".equalsIgnoreCase(type)) {
					ScCommittee scCommittee = (ScCommittee) ((TreeNodeBean<?>) treeNodes[nodes]
							.getData()).getObject();
					if (!clonedCommittes.containsKey(scCommittee
							.getCommitteeType().getParameterCode())) {
						clonedCommittes.put(scCommittee.getCommitteeType()
								.getParameterCode(), scCommittee
								.getScCommitteeMembers());
					}
				}
				if ("scCommitteeMember".equalsIgnoreCase(type)) {
					ScCommittee scCommittee = (ScCommittee) ((TreeNodeBean<?>) treeNodes[nodes]
							.getParent().getData()).getObject();
					if (!clonedCommittes.containsKey(scCommittee
							.getCommitteeType().getParameterCode())) {
						clonedCommittes.put(scCommittee.getCommitteeType()
								.getParameterCode(),
								removeUnselectedChilds(treeNodes[nodes]
										.getParent()));
					}
				}

				treeNodes[nodes].setSelected(false);
			}
		} else {
			throw new EpException(MessageConst.ERROR_760103);
		}

		Set<String> needToCloneCommittes = clonedCommittes.keySet();
		/**
		 * To check whether the cloning is required for that committe or not..
		 */
		if (!isApprovalFlow) {
			checkCloneNeedRNot(scQt.getEvaluationType(), scQt.getQtStatus(),
					needToCloneCommittes);
		}
		List<TabBean> tabs = this.getManagedBean()
				.getScCommitteeMemberTabList();

		for (String committeType : needToCloneCommittes) {

			for (int beanCount = 0; beanCount < tabs.size(); beanCount++) {
				List<ScCommitteeMember> memberList = new ArrayList<ScCommitteeMember>();
				if (tabs.get(beanCount).getTabType()
						.equalsIgnoreCase(committeType)) {
					if (null == tabs.get(beanCount).getMembers()) {
						tabs.get(beanCount).setMembers(
								new ArrayList<ScCommitteeMember>());
					}
					ScCommittee cloneCommitte = tabs.get(beanCount)
							.getScQtCommittee().getScCommittee();
					List<ScCommitteeMember> members = clonedCommittes
							.get(committeType);

					for (int i = 0; i < members.size(); i++) {
						ScCommitteeMember member1 = null;
						try {
							member1 = (ScCommitteeMember) BeanUtils
									.cloneBean(members.get(i));
						} catch (IllegalAccessException
								| InstantiationException
								| InvocationTargetException
								| NoSuchMethodException e) {
							log.debug("Error in cloning.." + e.getMessage());
						}
						if (null != member1) {
							if (!cloneCommitte.getScCommitteeMembers()
									.contains(member1)) {
								member1.setBrandNew(true);
								member1.setCommitteeMemberId(null);
								setUserDetails(member1);
								member1.setChangedDate(null);
								member1.setScCommittee(cloneCommitte);
								// tabs.get(beanCount).getMembers().add(member1);
								memberList.add(member1);
							} else {
								memberList.clear();
								throw new EpException(
										MessageConst.ERROR_760048,
										new String[] { ParameterHelper
												.getInstance()
												.getParameterDesc(
														ParameterTypeConst.SC_COMMITTEE_TYPE,
														cloneCommitte
																.getCommitteeType()
																.getParameterCode()) });
							}
						}
					}
					cloneCommitte.setApprovalReq(true);
					tabs.get(beanCount).getMembers().addAll(memberList);
				}
			}
		}
		// updating the memebers into sccommitte object if no error..
		for (int i = 0; i < tabs.size(); i++) {
			if (null != tabs.get(i).getMembers()) {
				tabs.get(i).getScQtCommittee().getScCommittee()
						.getScCommitteeMembers()
						.addAll(tabs.get(i).getMembers());
				Collections.sort(tabs.get(i).getScQtCommittee()
						.getScCommittee().getScCommitteeMembers(), new MemberRoleComparator());

				tabs.get(i).getMembers().clear();
			}
		}
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.ERROR_760155,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

		RequestContext.getCurrentInstance().execute("screen2.hide()");
		this.getManagedBean().setAutoUpdate(true);
	}

	/**
	 * Removes the unselected childs.
	 * 
	 * @param parent
	 *            the parent
	 * @return the list
	 */
	private List<ScCommitteeMember> removeUnselectedChilds(TreeNode parent) {
		List<ScCommitteeMember> committeeMembers = new ArrayList<ScCommitteeMember>();
		List<TreeNode> treeNode = parent.getChildren();
		for (int i = 0; i < treeNode.size(); i++) {
			if (treeNode.get(i).isSelected()) {
				committeeMembers
						.add((ScCommitteeMember) ((TreeNodeBean<?>) treeNode
								.get(i).getData()).getObject());
			}
		}
		return committeeMembers;
	}

	/**
	 * Clone committee popup action.
	 */
	public void cloneCommitteePopupAction() {
		debugLogger("cloneCommitteePopupAction");
		// logic to check whether committe will allow for clone or not..
		// checkIsSpecCommitteFormOrNot();
		getManagedBean().setCloneCommitteeSelectQt(null);

		IPagingDataModel<ScQt> cloneSearchQTDataModel = getManagedBean()
				.getCloneSearchQTDataModel();

		if (null == cloneSearchQTDataModel) {
			getManagedBean().setCloneSearchQTDataModel(
					new QTPagingDataModel("clonesearchCommitteid",
							getManagedBean().getCloneQtnTenderSearchBean()));
		}
		getManagedBean().getCloneSearchQTDataModel().resetData();

		getManagedBean().getCloneQtnTenderSearchBean().setQtTitle(null);
		getManagedBean().getCloneQtnTenderSearchBean().setQtNo(null);
		getManagedBean().getCloneQtnTenderSearchBean().setProcurementMode(null);
		getManagedBean().getCloneQtnTenderSearchBean().setFromCreatedDate(null);
		getManagedBean().getCloneQtnTenderSearchBean().setToCreatedDate(null);
		getManagedBean().getCloneQtnTenderSearchBean().setQtStatus(null);
		getManagedBean().setCloneTenderList(null);
		getManagedBean().setSearchcommitteeNode(new DefaultTreeNode());
		RequestContext.getCurrentInstance().execute("screen2.show()");
		getManagedBean().setAutoUpdate(false);
	}

	/**
	 * Committeeclone popup action.
	 * 
	 * @param committee
	 *            the committee
	 */
	public void committeeclonePopupAction(ScQtCommittee committee) {
		debugLogger("committeeclonePopupAction");

		IPagingDataModel<ScQt> cloneSearchQTDataModel = getManagedBean()
				.getCloneSearchQTDataModel();

		if (null == cloneSearchQTDataModel) {
			getManagedBean().setCloneSearchQTDataModel(
					new QTPagingDataModel("clonesearchCommitteid",
							getManagedBean().getCloneQtnTenderSearchBean()));
		}
		getManagedBean().getCloneSearchQTDataModel().resetData();

		// getManagedBean().getCloneSearchQTDataModel().reloadData();
		getManagedBean().getCloneQtnTenderSearchBean().setQtTitle(null);
		getManagedBean().getCloneQtnTenderSearchBean().setQtNo(null);
		getManagedBean().getCloneQtnTenderSearchBean().setProcurementMode(null);
		getManagedBean().getCloneQtnTenderSearchBean().setFromCreatedDate(null);
		getManagedBean().getCloneQtnTenderSearchBean().setToCreatedDate(null);
		getManagedBean().getCloneQtnTenderSearchBean().setQtStatus(null);
		getManagedBean().setCloneTenderList(null);
		getManagedBean().setSearchcommitteeNode(new DefaultTreeNode());
		getManagedBean().setCloneCommitteMemebers(null);
		getManagedBean().setSelectedCommitteMemebers(null);
		String commType = committee.getScCommittee().getCommitteeType()
				.getParameterCode();
		this.getManagedBean().setCommType(commType);
		log.info("Committee Type : " + getManagedBean().getCommType());
		RequestContext.getCurrentInstance().execute("screen2.show()");
		getManagedBean().setAutoUpdate(false);
	}

	/**
	 * Adds the committe member.
	 * 
	 * @param committee
	 *            the committee
	 * @param titleName
	 *            the title name
	 */
	public void addCommitteMember(ScQtCommittee committee, String titleName) {
		debugLogger("addCommitteMember");

		IPagingDataModel<PmUser> userSearCommitteeDataModel = getManagedBean()
				.getUserSearchCommitteeDataModel();
		if (null == userSearCommitteeDataModel) {
			getManagedBean().setUserSearchCommitteeDataModel(
					new UserCommitteePagingDataModel(
							"existingcommitteesearchid", getManagedBean()
									.getUserCommitteeSearchBean()));
		}
		getManagedBean().getUserSearchCommitteeDataModel().resetData();

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
		getManagedBean().setAutoUpdate(false);
	}

	/**
	 * User selection types gov external new.
	 * 
	 * @return the selection types
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
	 * User selcetion action.
	 * 
	 * @param event
	 *            the event
	 */
	public void userSelcetionAction(AjaxBehaviorEvent event) {
		String userSelection = (String) event.getComponent().getAttributes()
				.get("value");
		userSelection = this.getManagedBean().getCommitteMember()
				.getUserSelection();
		logger.info("User Selection : " + userSelection);
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
			getManagedBean().getCommitteMember().setMemberRoleId(null);

		}
	}

	/**
	 * Adds the clone to ec dtl.
	 */
	public void addCloneToEcDtl() {
		debugLogger("addCloneToEcDtl");
		checkIsSpecCommitteFormOrNot();
		// TabBean bean = getManagedBean().getSelectedTabBean();
		List<ScCommitteeMember> selectedList = this.getManagedBean()
				.getCommittemembers();
		if (CollectionUtils.isNotEmpty(selectedList)) {
			List<TabBean> tabs = this.getManagedBean()
					.getScCommitteeMemberTabList();
			for (int i = 0; i < tabs.size(); i++) {
				TabBean tabBean = tabs.get(i);
				log.debug("Title Name : " + tabBean.getTabType());
				if (tabBean.getTabType().equalsIgnoreCase(
						ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE)) {
					List<ScCommitteeMember> existingMembers = tabBean
							.getScQtCommittee().getScCommittee()
							.getScCommitteeMembers();
					if (isMemberExists(existingMembers, selectedList)) {
						throw new EpException(
								MessageConst.ERROR_760048,
								new String[] { ParameterHelper
										.getInstance()
										.getParameterDesc(
												ParameterTypeConst.SC_COMMITTEE_TYPE,
												ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE) });
					}
					List<ScCommitteeMember> committeeMembersList = new ArrayList<ScCommitteeMember>();

					for (int memebersList = 0; memebersList < selectedList
							.size(); memebersList++) {
						ScCommitteeMember member1 = new ScCommitteeMember();
						try {
							member1 = (ScCommitteeMember) BeanUtils
									.cloneBean(selectedList.get(memebersList));

						} catch (IllegalAccessException
								| InstantiationException
								| InvocationTargetException
								| NoSuchMethodException e) {
							e.printStackTrace();
						}
						if (null != member1) {
							member1.setBrandNew(true);
							member1.setCommitteeMemberId(null);
							setUserDetails(member1);
							member1.setChangedDate(null);
							member1.setScCommittee(tabBean.getScQtCommittee()
									.getScCommittee());
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
					this.getManagedBean().setSelectedTabIndex(i);
					this.getManagedBean().setSelectedTabBean(tabBean);
					this.getManagedBean().setCommittemembers(null);
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.ERROR_760150,
															new String[] { ParameterHelper
																	.getInstance()
																	.getParameterDesc(
																			ParameterTypeConst.SC_COMMITTEE_TYPE,
																			ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE) },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
					break;
				}
			}

		} else {
			throw new EpException(
					MessageConst.ERROR_760308,
					new String[] { ParameterHelper
							.getInstance()
							.getParameterDesc(
									ParameterTypeConst.SC_COMMITTEE_TYPE,
									ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE) });
			// No members to clone to EC.
		}
	}

	/**
	 * Adds the clone to tec dtl.
	 */
	public void addCloneToTecDtl() {
		debugLogger("addCloneToTecDtl");
		checkIsSpecCommitteFormOrNot();
		// TabBean bean = getManagedBean().getSelectedTabBean();
		List<ScCommitteeMember> selectedList = this.getManagedBean()
				.getCommittemembers();
		if (null != selectedList && !selectedList.isEmpty()) {
			List<TabBean> tabs = this.getManagedBean()
					.getScCommitteeMemberTabList();
			for (int i = 0; i < tabs.size(); i++) {
				TabBean tabBean = tabs.get(i);
				log.debug("Title Name : " + tabBean.getTabType());
				if (tabBean
						.getTabType()
						.equalsIgnoreCase(
								ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE)) {
					List<ScCommitteeMember> existingMembers = tabBean
							.getScQtCommittee().getScCommittee()
							.getScCommitteeMembers();
					if (isMemberExists(existingMembers, selectedList)) {
						throw new EpException(
								MessageConst.ERROR_760048,
								new String[] { ParameterHelper
										.getInstance()
										.getParameterDesc(
												ParameterTypeConst.SC_COMMITTEE_TYPE,
												ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE) });
					}
					List<ScCommitteeMember> committeeMembersList = new ArrayList<ScCommitteeMember>();

					for (int memebersList = 0; memebersList < selectedList
							.size(); memebersList++) {
						ScCommitteeMember member1 = new ScCommitteeMember();
						try {
							member1 = (ScCommitteeMember) BeanUtils
									.cloneBean(selectedList.get(memebersList));

						} catch (IllegalAccessException
								| InstantiationException
								| InvocationTargetException
								| NoSuchMethodException e) {
							e.printStackTrace();
						}
						if (null != member1) {
							member1.setBrandNew(true);
							member1.setCommitteeMemberId(null);
							setUserDetails(member1);
							member1.setChangedDate(null);
							member1.setScCommittee(tabBean.getScQtCommittee()
									.getScCommittee());
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
					this.getManagedBean().setSelectedTabIndex(i);
					this.getManagedBean().setSelectedTabBean(tabBean);
					this.getManagedBean().setCommitteMember(null);
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.ERROR_760150,
															new String[] { ParameterHelper
																	.getInstance()
																	.getParameterDesc(
																			ParameterTypeConst.SC_COMMITTEE_TYPE,
																			ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE) },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
					break;
				}
			}

		} else {
			throw new EpException(
					MessageConst.ERROR_760308,
					new String[] { ParameterHelper
							.getInstance()
							.getParameterDesc(
									ParameterTypeConst.SC_COMMITTEE_TYPE,
									ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE) });
			// No members to clone to TEC.
		}
	}

	/**
	 * Ok committee action.
	 */
	public void okCommitteeAction() {
		debugLogger("okCommitteeAction");

		UserReqDTO userReqDTO = new UserReqDTO();
		int committeTabIndex = this.getManagedBean().getSelectedTabIndex();
		TabBean selectedCommitteTabBean = this.getManagedBean()
				.getScCommitteeMemberTabList().get(committeTabIndex);
		ScCommittee scCommittee = selectedCommitteTabBean.getScQtCommittee()
				.getScCommittee();
		ScQt scQt = this.getManagedBean().getQt();
		List<ScCommitteeMember> toBeAddedList = new ArrayList<ScCommitteeMember>();

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
			if (member.getMemberRole() != null) {
				member.setMemberRoleId(member.getMemberRole().getParameterId());	
			}
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
			// List<PmUserNotification> pmUserNotifications = new
			// ArrayList<PmUserNotification>();
			// PmUserNotification pmUserNotification = new PmUserNotification();
			// pmUserNotification.setPmUser(user);
			// pmUserNotification.setNotifyMode("E");
			// pmUserNotification.setPreferred(true);
			// pmUserNotifications.add(pmUserNotification);
			// user.setPmUserNotifications(pmUserNotifications);

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
			// PmUserPreference pmUserPreference = new PmUserPreference();
			// pmUserPreference.setPmUser(user);
			// pmUserPreference.setFormatDate("DD-MMM-YYYY");
			// pmUserPreference.setFormatTime("HH:mm:ss");
			// pmUserPreference.setFormatNumber("#,##0");
			// pmUserPreference.setPreferredLanguage("en");
			// pmUserPreference.setNotifyRemovalDay(30);
			// user.setPmUserPreference(pmUserPreference);

			userReqDTO.setOrgType(OrganizationType.OTHERS.value());

			userReqDTO.setPmUser(user);
			UserResDTO resDTO = new UserResDTO();
			try {
				// No Need to save.. Need to check with kenny..
				resDTO = getUserSO().saveUser(userReqDTO);
			} catch (Exception e) {
				debugLogger(e.getMessage());
			}

			member.setPmUser(resDTO.getPmUser());
			setUserDetails(member);
			toBeAddedList.add(member);
		} else {
			// List<String> itemIds = new ArrayList<String>();
			if (!this.getManagedBean().getSelectedPmUser().isEmpty()) {
				List<PmUser> users = getManagedBean().getSelectedPmUser();

				for (int i = 0; i < users.size(); i++) {
					ScCommitteeMember committeeMember = new ScCommitteeMember();
					committeeMember.setMemberName(users.get(i).getUserName());
					committeeMember.setIcPassport(users.get(i)
							.getIdentificationNo());
					committeeMember.setDesignation(users.get(i)
							.getDesignation());
					committeeMember.setEmail(users.get(i).getEmail());
					committeeMember.setGrade(users.get(i).getGrade());
					committeeMember.setPNP(users.get(i).isPMP());
					committeeMember.setPmUser(users.get(i));
					committeeMember.setPmOrgProfile(scQt.getPmOrgProfile());
					if (null != users.get(i).getMemberRoleId()) {
						ParameterReqDTO reqDTO = new ParameterReqDTO();
						reqDTO.setParameterId(users.get(i).getMemberRoleId());
						ParameterResDTO resDTO = getParameterSO()
								.retrieveParameterById(reqDTO);
						committeeMember.setMemberRole(resDTO.getPmParameter());
					} else {
						committeeMember.setMemberRole(new PmParameter());
					}
					if (committeeMember.getMemberRole() != null) {
						committeeMember.setMemberRoleId(committeeMember.getMemberRole().getParameterId());	
					}

					/*
					 * List<PmParameterDesc> roles = getManagedBean()
					 * .getRoleTypes(); for (PmParameterDesc parameter : roles)
					 * { if (parameter .getPmParameter() .getParameterCode()
					 * .equals(users.get(i).getMemberRole()
					 * .getParameterCode())) { ParameterReqDTO reqDTO = new
					 * ParameterReqDTO(); reqDTO.setParameterCode(users.get(i)
					 * .getMemberRole().getParameterCode());
					 * reqDTO.setParameterType(users.get(i)
					 * .getMemberRole().getParameterType()); ParameterResDTO
					 * resDTO = getParameterSO() .retrieveParameterById(reqDTO);
					 * committeeMember.setMemberRole(resDTO .getPmParameter());
					 * break; } else {
					 * committeeMember.setMemberRole(users.get(i)
					 * .getMemberRole()); break; }
					 * 
					 * }
					 */

					committeeMember.setAccessRight(QTConstants.ACCESS_RIGHT);
					committeeMember.setScCommittee(scCommittee);
					setUserDetails(committeeMember);
					toBeAddedList.add(committeeMember);

				}
			} else {
				throw new EpException(MessageConst.ERROR_760121);
			}

		}
		if (isMemberExists(scCommittee.getScCommitteeMembers(), toBeAddedList)) {
			throw new EpException(MessageConst.ERROR_760048,
					new String[] { ParameterHelper.getInstance()
							.getParameterDesc(
									ParameterTypeConst.SC_COMMITTEE_TYPE,
									scCommittee.getCommitteeType()
											.getParameterCode()) });
		}

		if (scCommittee
				.getCommitteeType()
				.getParameterCode()
				.equalsIgnoreCase(
						ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE)) {/*
			List<TabBean> committeTabs = this.getManagedBean()
					.getScCommitteeMemberTabList();
			for (int i = 0; i < committeTabs.size(); i++) {
				TabBean tabBean = committeTabs.get(i);
				if (tabBean
						.getTabType()
						.equalsIgnoreCase(
								ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE)) {
					List<ScCommitteeMember> existingMembers = tabBean
							.getScQtCommittee().getScCommittee()
							.getScCommitteeMembers();
					for (ScCommitteeMember committeeMember : existingMembers) {
						if (toBeAddedList
								.get(0)
								.getIcPassport()
								.equalsIgnoreCase(
										committeeMember.getIcPassport())) {
							throw new EpException(
									MessageConst.ERROR_760307,
									new String[] { ParameterHelper
											.getInstance()
											.getParameterDesc(
													ParameterTypeConst.SC_COMMITTEE_TYPE,
													ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE) });
							// Committee Member already existed in TEC.
						}
					}
				}
				if (tabBean
						.getTabType()
						.equalsIgnoreCase(
								ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE)) {
					List<ScCommitteeMember> existingMembers = tabBean
							.getScQtCommittee().getScCommittee()
							.getScCommitteeMembers();
					for (ScCommitteeMember committeeMember : existingMembers) {
						if (toBeAddedList
								.get(0)
								.getIcPassport()
								.equalsIgnoreCase(
										committeeMember.getIcPassport())) {
							throw new EpException(
									MessageConst.ERROR_760307,
									new String[] { ParameterHelper
											.getInstance()
											.getParameterDesc(
													ParameterTypeConst.SC_COMMITTEE_TYPE,
													ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE) });
							// Committee Member already existed in FEC.
						}
					}
				}
			}
		*/}
		if (scCommittee
				.getCommitteeType()
				.getParameterCode()
				.equalsIgnoreCase(
						ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE)) {/*
			List<TabBean> committeTabs = this.getManagedBean()
					.getScCommitteeMemberTabList();
			for (int i = 0; i < committeTabs.size(); i++) {
				TabBean tabBean = committeTabs.get(i);
				if (tabBean.getTabType().equalsIgnoreCase(
						ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE)) {
					List<ScCommitteeMember> existingMembers = tabBean
							.getScQtCommittee().getScCommittee()
							.getScCommitteeMembers();
					for (ScCommitteeMember committeeMember : existingMembers) {
						if (toBeAddedList
								.get(0)
								.getIcPassport()
								.equalsIgnoreCase(
										committeeMember.getIcPassport())) {
							throw new EpException(
									MessageConst.ERROR_760307,
									new String[] { ParameterHelper
											.getInstance()
											.getParameterDesc(
													ParameterTypeConst.SC_COMMITTEE_TYPE,
													ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE) });
							// Committee Member already existed in OC.
						}
					}
				}
				if (tabBean
						.getTabType()
						.equalsIgnoreCase(
								ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE)) {
					List<ScCommitteeMember> existingMembers = tabBean
							.getScQtCommittee().getScCommittee()
							.getScCommitteeMembers();
					for (ScCommitteeMember committeeMember : existingMembers) {
						if (toBeAddedList
								.get(0)
								.getIcPassport()
								.equalsIgnoreCase(
										committeeMember.getIcPassport())) {
							throw new EpException(
									MessageConst.ERROR_760307,
									new String[] { ParameterHelper
											.getInstance()
											.getParameterDesc(
													ParameterTypeConst.SC_COMMITTEE_TYPE,
													ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE) });
							// Committee Member already existed in FEC.
						}
					}
				}
			}
		*/}
		if (scCommittee
				.getCommitteeType()
				.getParameterCode()
				.equalsIgnoreCase(
						ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE)) {/*
			List<TabBean> committeTabs = this.getManagedBean()
					.getScCommitteeMemberTabList();
			for (int i = 0; i < committeTabs.size(); i++) {
				TabBean tabBean = committeTabs.get(i);
				if (tabBean.getTabType().equalsIgnoreCase(
						ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE)) {
					List<ScCommitteeMember> existingMembers = tabBean
							.getScQtCommittee().getScCommittee()
							.getScCommitteeMembers();
					for (ScCommitteeMember committeeMember : existingMembers) {
						if (toBeAddedList
								.get(0)
								.getIcPassport()
								.equalsIgnoreCase(
										committeeMember.getIcPassport())) {
							throw new EpException(
									MessageConst.ERROR_760307,
									new String[] { ParameterHelper
											.getInstance()
											.getParameterDesc(
													ParameterTypeConst.SC_COMMITTEE_TYPE,
													ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE) });
							// Committee Member already existed in OC.
						}
					}
				}
				if (tabBean
						.getTabType()
						.equalsIgnoreCase(
								ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE)) {
					List<ScCommitteeMember> existingMembers = tabBean
							.getScQtCommittee().getScCommittee()
							.getScCommitteeMembers();
					for (ScCommitteeMember committeeMember : existingMembers) {
						if (toBeAddedList
								.get(0)
								.getIcPassport()
								.equalsIgnoreCase(
										committeeMember.getIcPassport())) {
							throw new EpException(
									MessageConst.ERROR_760307,
									new String[] { ParameterHelper
											.getInstance()
											.getParameterDesc(
													ParameterTypeConst.SC_COMMITTEE_TYPE,
													ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE) });
							// Committee Member already existed in TEC.
						}
					}
				}
			}
		*/}

		scCommittee.getScCommitteeMembers().addAll(toBeAddedList);
		Collections.sort(scCommittee.getScCommitteeMembers());
		selectedCommitteTabBean.getScQtCommittee().setScCommittee(scCommittee);

		// Specification Committee validation
		RequestContext.getCurrentInstance().execute(
				"newspecificationcommittee.hide()");
		this.getManagedBean().setAutoUpdate(true);
	}

	/**
	 * Reset.
	 */
	public void reset() {
		debugLogger("reset");
		this.getManagedBean().getUserCommitteeSearchBean().setName(null);
		this.getManagedBean().getUserCommitteeSearchBean()
				.setIdentificationType(null);
		this.getManagedBean().getUserCommitteeSearchBean()
				.setIdentificationNo(null);
	}

	/**
	 * Reset clone.
	 */
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
		this.getManagedBean().setSearchcommitteeNode(new DefaultTreeNode());
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setFromPublishDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setToPublishDate(null);
		this.getManagedBean().getCloneQtnTenderSearchBean()
				.setFromClosingDate(null);
		getManagedBean().setCloneCommitteeSelectQt(null);
	}

	/**
	 * Gets the user so.
	 * 
	 * @return the user so
	 */
	private IUserSO getUserSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
	}

	/**
	 * Gets the parameter so.
	 * 
	 * @return the parameter so
	 */
	private IParameterSO getParameterSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}

	/**
	 * Gets the org profile so.
	 * 
	 * @return the org profile so
	 */
	private IOrgProfileSO getOrgProfileSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean#
	 * preRenderView()
	 */
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

	// To check whether committe needs to be clone or not..
	/**
	 * Check clone need r not.
	 * 
	 * @param evalStg
	 *            the eval stg
	 * @param status
	 *            the status
	 * @param committeCodes
	 *            the committe codes
	 */
	private void checkCloneNeedRNot(String evalStg, PmStatus status,
			Set<String> committeCodes) {
		for (String committee : committeCodes) {
			if (ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER
					.equalsIgnoreCase(committee)) {
				throw new EpException(MessageConst.ERROR_760104,
						new String[] { committee });
			}
		}
		if (isQTStatusGreaterThan(status.getStatusId(), QTStatusConst.QT_PENDING_QT_SUBMISSION)) {
			for (String committee : committeCodes) {
				if (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
						.equalsIgnoreCase(committee)) {
					throw new EpException(MessageConst.ERROR_760104,
							new String[] { committee });
				}
			}
		}

		if (ParameterConst.EVALUATION_TYPES_ONE_TIER.equals(evalStg)
				&& status.getStatusId() < QTStatusConst.QT_SUPPLIER_FINALIZATION) {
			for (String committee : committeCodes) {
				if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
						.equalsIgnoreCase(committee)
						|| ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
								.equalsIgnoreCase(committee)
						|| ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
								.equalsIgnoreCase(committee)) {
					throw new EpException(MessageConst.ERROR_760104,
							new String[] { committee });
				}
			}
		} else if (ParameterConst.EVALUATION_TYPES_TWO_TIER.equals(evalStg)
				&& status.getStatusId() < QTStatusConst.QT_SUPPLIER_FINALIZATION) {
			for (String committee : committeCodes) {
				if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(committee)) {
					throw new EpException(MessageConst.ERROR_760104,
							new String[] { committee });
				}
			}
		}
	}

	/**
	 * Delete specification committee.
	 */
	public void deleteSpecificationCommittee() {
		debugLogger("deleteSelected Committe Mmeners");

		TabBean bean = this.getManagedBean().getSelectedTabBean();
		List<ScCommitteeMember> selectedList = this.getManagedBean()
				.getCommittemembers();
		if (CollectionUtils.isEmpty(selectedList)) {
			throw new EpException(MessageConst.ERROR_760146);
		}

		List<ScCommitteeMember> committeeMembers = getManagedBean().getToBeDelCommitteMemebers();
		
		if (committeeMembers == null) {
			committeeMembers = new ArrayList<ScCommitteeMember>();	
		}
		
		ScCommittee scCommitte = bean.getScQtCommittee().getScCommittee();
		//scCommitte.getScCommitteeMembers().removeAll(selectedList);
		if (!ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
				.equalsIgnoreCase(scCommitte.getCommitteeType()
						.getParameterCode())
				&& CollectionUtils.isEmpty(scCommitte.getScCommitteeMembers())
				&& !(QTStatusConst.CMT_PENDING_APPROVAL.equals(scCommitte
						.getCommitteStatus().getStatusId()) || QTStatusConst.CMT_PENDING_REVISION_APPROVAL
						.equals(scCommitte.getCommitteStatus().getStatusId()))) {
			bean.getScQtCommittee().getScCommittee().setApprovalReq(false);
		}

		this.getManagedBean().getScCommitteeMemberTabList()
				.remove(this.getManagedBean().getSelectedTabIndex());
		this.getManagedBean().getScCommitteeMemberTabList()
				.add(this.getManagedBean().getSelectedTabIndex(), bean);
		for (int i = 0; i < selectedList.size(); i++) {
			log.info("====delete committee member name==="+selectedList.get(i).getMemberName());
			scCommitte.getScCommitteeMembers().remove(selectedList.get(i));
			if (null != selectedList.get(i).getCommitteeMemberId()) {

				selectedList.get(i).setDeleted(true);
				selectedList.get(i).setRepresentativeType(null);
				selectedList.get(i).setRepresentativeTypeId(null);
				committeeMembers.add(selectedList.get(i));
			}
			
		}

		this.getManagedBean().setToBeDelCommitteMemebers(committeeMembers);
		this.getManagedBean().setCommitteMember(null);

	}

	/**
	 * Checks if is member exists.
	 * 
	 * @param existingMembers
	 *            the existing members
	 * @param newMembers
	 *            the new members
	 * @return true, if is member exists
	 */
	private boolean isMemberExists(List<ScCommitteeMember> existingMembers,
			List<ScCommitteeMember> newMembers) {
		if (!existingMembers.isEmpty()) {
			for (ScCommitteeMember member : newMembers) {
				if (existingMembers.contains(member)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check is spec committe form or not.
	 */
	private void checkIsSpecCommitteFormOrNot() {
		ScCommittee committe = null;
		for (TabBean tabBean : this.getManagedBean()
				.getScCommitteeMemberTabList()) {

			if (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
					.equalsIgnoreCase(tabBean.getTabType())) {
				committe = tabBean.getScQtCommittee().getScCommittee();

				break;
			}
		}
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setScCommittee(committe);
		reqDTO.setScQt(this.getManagedBean().getQt());
		reqDTO.setUser(new FacesScopes().getUserSessionBean());
		committeSO.validateCommitteDtls(reqDTO);

	}

	/**
	 * On node unselect.
	 * 
	 * @param event
	 *            the event
	 */
	public void onNodeUnselect(NodeUnselectEvent event) {
		TreeNodeBean<?> nodeBean = (TreeNodeBean<?>) event.getTreeNode().getData();
		if ("committee".equalsIgnoreCase(nodeBean.getType())) {
			List<TreeNode> childNodes = event.getTreeNode().getChildren();
			for (int i = 0; i < childNodes.size(); i++) {
				childNodes.get(i).setSelected(false);
			}
		} else {
			event.getTreeNode().setSelected(false);
		}

	}

	/**
	 * Checks if is committe info editable.
	 * 
	 * @param committeStatus
	 *            the committe status
	 * @param commType
	 *            the comm type
	 * @return true, if is committe info editable
	 */
	public boolean isCommitteInfoEditable(Long committeStatus, String commType) {
		/*
		 * if (QTStatusConst.QT_EVALUATION_PROPOSAL.compareTo(this
		 * .getManagedBean().getQt().getQtStatus().getStatusId()) <= 0) { return
		 * true; }
		 */
		Long currentStatusId = this
				.getManagedBean().getCurrentStatusId();
		if (this.getManagedBean().isMaintanceFlow()) {
			
			 if (QTStatusConst.QT_PROPOSAL_PREPARATION.equals(currentStatusId)) {
				if (DateUtils.getDaysDifference(new Date(), this
						.getManagedBean().getQt().getClosingDate()) <= 7
						&& (QTStatusConst.CMT_PENDING_SUBMISSION
								.equals(committeStatus) || QTStatusConst.CMT_PENDING_REVISION
								.equals(committeStatus))) {

					return true;
				}
			} else if (isQTStatusGreaterThanOrEquals(currentStatusId, QTStatusConst.QT_CLOSED)) {
				return true;
			} else if (isMaintenanceEligibleStatus(currentStatusId)) {
							
				return true;
			}
			 
			if (!getManagedBean().isDeskOfficer()) {
				return true;
			}
		} else {
			String taskName = this.getManagedBean().getTaskName();
			if ((QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL.equals(this.getManagedBean().getCurrentStatusId())
					|| QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING.equals(this.getManagedBean().getCurrentStatusId()))
					&& !SCProcessNameConst.REVISE_COMMITTEE
							.equalsIgnoreCase(taskName)) {
				return true;
			} else if (isQTStatusAfterScheduleDoc(this.getManagedBean().getCurrentStatusId())
					&& !(SCProcessNameConst.REVISE_COMMITTEE
							.equalsIgnoreCase(taskName)
							|| SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
									.equalsIgnoreCase(taskName) || SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
								.equalsIgnoreCase(taskName))) {
				return true;
			}
		}
		if (QTStatusConst.CMT_PENDING_APPROVAL.equals(committeStatus)
				|| QTStatusConst.CMT_PENDING_REVISION_APPROVAL
						.equals(committeStatus)) {
			return true;
		} else if (QTStatusConst.CMT_COMPLETED.equals(committeStatus)
				&& ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
			return true;
		}
		return false;

	}
	
	private boolean isQTStatusAfterScheduleDoc(Long currentStatusId) {
		
		boolean isQtStatusAfterSchedule = false;
		if(isQTStatusGreaterThan(currentStatusId,
				QTStatusConst.QT_PENDING_SCHEDULE_REVISION)){
				isQtStatusAfterSchedule = true;
		}
		
		return isQtStatusAfterSchedule;
	}

	/**
	 * Checks if is FC editable.
	 * 
	 * @return true, if is FC editable
	 */
	public boolean isFCEditable() {
		Long currentQtStatus = this.getManagedBean().getQt().getQtStatus()
				.getStatusId();

		if (!isQTStatusGreaterThanOrEquals(currentQtStatus, QTStatusConst.QT_EVALUATION_PROPOSAL) || !this.getManagedBean().isMaintanceFlow()) {
			return true;
		} 

		return false;
	}

	public boolean isEnableCloneButton() {
		
		Long currentSatatusId = this.getManagedBean().getCurrentStatusId();
		if (isMaintenanceEligibleStatus(currentSatatusId)) {
			return true;
		} else {
			if ((QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL.equals(currentSatatusId)
					|| QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING.equals(currentSatatusId))
					&& !SCProcessNameConst.REVISE_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getTaskName())) {
				return true;
			} else if (isQTStatusAfterScheduleDoc(this.getManagedBean().getCurrentStatusId())
					&& !SCProcessNameConst.REVISE_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getTaskName())
					&& !SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
							.equalsIgnoreCase(this.getManagedBean()
									.getTaskName())) {
				return true;
			}
		}
		
		return !getManagedBean().isDeskOfficer();
	}

	/**
	 * Method to check wheter committe is formed with at least one chairperson
	 * secratary and memeber
	 */
	public void checkCommitteFormedOrNot() {
		TabBean selectedCommitteTabBean = this.getManagedBean()
				.getSelectedTabBean();
		if (null != selectedCommitteTabBean) {
			ScQtCommittee qtCommittee = selectedCommitteTabBean
					.getScQtCommittee();
			if (qtCommittee != null) {
				ScCommittee committee = qtCommittee.getScCommittee();
				 if(committee.isApprovalReq()){
					if (CollectionUtils.isNotEmpty(committee
							.getScCommitteeMembers())) {
					
						String committeeType = committee.getCommitteeType().getParameterCode();
						isChairpersonRequired(committee.getScCommitteeMembers(),committeeType);
						isSecretaryRequired(committee.getScCommitteeMembers(),committeeType);
						isAtleastOneMemebersReq(committee.getScCommitteeMembers());
					} else {
						committee.setApprovalReq(false);
						int selectedIndex = this.getManagedBean()
								.getSelectedTabIndex();
						this.getManagedBean().getScCommitteeMemberTabList()
								.remove(selectedIndex);
						this.getManagedBean().getScCommitteeMemberTabList()
								.add(selectedIndex, selectedCommitteTabBean);
						throw new EpException(MessageConst.ERROR_760275);
					}
				}
			}
		}
	}

	private void isChairpersonRequired(List<ScCommitteeMember> commMembers,String committeeType) {
		int count = 0;

		for (ScCommitteeMember committeeMember : commMembers) {
			String memberRole = committeeMember.getMemberRole()
					.getParameterCode();
			if (ParameterConst.COMMITTEE_ROLE_CHAIRPERSON
					.equalsIgnoreCase(memberRole)) {
				if (!committeeMember.isPNP()) {
					throw new EpException(MessageConst.ERROR_760264,new String[]{committeeType});
				}
				count++;
				if (count >= 2) {
					throw new EpException(MessageConst.ERROR_760123,new String[]{committeeType});
					// Only required to have 1 chairperson for appoint committee
				}
			}
		}

		if (count == 0) {
			throw new EpException(MessageConst.ERROR_760047,new String[]{committeeType});
			// At least one chairperson are required for appoint committee
		}
	}

	private void isSecretaryRequired(List<ScCommitteeMember> commMembers,String committeeType) {
		int count = 0;
		for (ScCommitteeMember committeeMember : commMembers) {
			String memberRole = committeeMember.getMemberRole()
					.getParameterCode();
			if (ParameterConst.COMMITTEE_ROLE_SECRETARY
					.equalsIgnoreCase(memberRole)) {
				count++;
				if (count >= 2) {
					throw new EpException(MessageConst.ERROR_760124,new String[]{committeeType});
					// Only required to have 1 secretary for appoint committee
				}
			}
		}
		if (count == 0) {
			throw new EpException(MessageConst.ERROR_760046,new String[]{committeeType});
			// At least one secretary are required for appoint committee
		}
	}

	private boolean isAtleastOneMemebersReq(List<ScCommitteeMember> commMembers) {
		int i = 0;
		for (ScCommitteeMember committeeMember : commMembers) {
			String memberRole = committeeMember.getMemberRole()
					.getParameterCode();
			if (ParameterConst.COMMITTEE_ROLE_MEMBER
					.equalsIgnoreCase(memberRole)) {
				i++;
				if (i >= 1) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get parameter code select items by parameter type for radio buttons.
	 * 
	 * @return array of SelectItem
	 */
	public SelectItem[] getProcurementModeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO);
		recommendList.add(ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE);
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.SC_PROCUREMENT_MODE, searchBean);
		return items;
	}

	/**
	 * Method will cut the string based on cutOffIndex value.
	 * 
	 * @param str
	 * @param cutOffIndex
	 * @return formatted string
	 */
	public String chopMemberName(String str, int cutOffIndex) {

		if (str.length() > cutOffIndex) {
			str = StringUtils.substring(str, 0, cutOffIndex) + " ...";
		}
		return str;
	}

	public String splitEmail(String email) {

		if (email != null && email.length() > 19) {
			String[] splitEmail = email.split("@");
			if (splitEmail.length > 1) {
				email = splitEmail[0] + " @ " + splitEmail[1];
			}
		}
		return email;
	}
	
	public boolean renderECOrTECButton(Long committeStatus, String commType) {

		boolean isRenderBtn = false;

		if (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
				.equalsIgnoreCase(commType)
				&& isQTStatusInBetween(this.getManagedBean()
						.getCurrentStatusId(),
						QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT,
						QTStatusConst.QT_PROPOSAL_PREPARATION)
				&& this.getManagedBean().isDeskOfficer()) {
			isRenderBtn = true;
			ScQt scQt = this.getManagedBean().getQt();
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			String fetchCommType = ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE;
			if (ParameterConst.EVALUATION_TYPES_TWO_TIER.equals(scQt
					.getEvaluationType())) {
				fetchCommType = ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE;
			}
			reqDTO.setCommitteeType(fetchCommType);
			reqDTO.setQtId(scQt.getQtId());
			CommitteResDTO resDTO = null;
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
				ScQtCommittee qtCommitte = resDTO.getScQtCommittee();
				if (qtCommitte != null && qtCommitte.getScCommittee() != null) {
					Long committeeStatus = qtCommitte.getScCommittee().getCommitteStatus().getStatusId();
					if ((QTStatusConst.CMT_PENDING_APPROVAL
							.equals(committeeStatus) || QTStatusConst.CMT_PENDING_REVISION_APPROVAL
							.equals(committeeStatus))) {
						isRenderBtn = false;
					}
				}

			} finally {
				committeSO = null;
			}
		}
		return isRenderBtn;

	}
	
	public boolean isMaintancePendingChklistStatus() {

		boolean isPresent = false;
		if (this.getManagedBean().isMaintanceFlow()) {
			Long statusId = this.getManagedBean().getQt().getQtStatus()
					.getStatusId();
			if (QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING.equals(statusId)||
					 QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT.equals(statusId)||
					 QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF.equals(statusId)||
					 QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION.equals(statusId)) {
				isPresent = true;
			}
		}
		return isPresent;
	}
	
	private boolean isMaintenanceEligibleStatus(Long statusId) {

		return (this.getManagedBean().isMaintanceFlow() && 
				(isMaintancePendingChklistStatus() || QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL
				.equals(statusId)));

	}
	
	public boolean isEnableCheckboxes(Long committeStatus, String commType) {

		boolean isEnableCheckboxes = isCommitteInfoEditable(committeStatus,commType);
		if (isEnableCheckboxes) {
			isEnableCheckboxes = !renderECOrTECButton(committeStatus, commType);
		}
		return isEnableCheckboxes;
	}
}
