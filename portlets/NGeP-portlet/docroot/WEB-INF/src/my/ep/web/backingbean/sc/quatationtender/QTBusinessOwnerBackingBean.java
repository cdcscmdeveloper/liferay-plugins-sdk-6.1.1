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
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
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
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.BpmOutcomeConst;
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
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
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
import my.ep.persistence.entity.sc.quationtender.ScQtStatus;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.UserCommitteeSearchBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * The Class QTBusinessOwnerBackingBean.
 * 
 * @author Viswanath
 */
@ManagedBean(name = "qTBusinessOwnerBackingBean")
@RequestScoped
public class QTBusinessOwnerBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(QTBusinessOwnerBackingBean.class);

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/** The flag. */
	private boolean flag;

	/**
	 * Instantiates a new QT business owner backing bean.
	 */
	public QTBusinessOwnerBackingBean() {

	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {
		if (this.getManagedBean().getQt() != null
				&& this.getManagedBean().getQt().getMinistrySpecSecrataryId() == null) {
			this.getManagedBean().getQt()
					.setMinistrySpecSecrataryId(new PmUser());
		}
		getMinistrySpecificationSecretaryList();

		// PagingDataModel for Quotation Tender List
		IPagingDataModel<ScQt> cloneSearchBODataModel = getManagedBean()
				.getCloneBusinessOwnerDataModel();

		if (cloneSearchBODataModel == null) {
			getManagedBean().setCloneBusinessOwnerDataModel(
					new BOPagingDataModel("clonesearchboid", getManagedBean()
							.getCloneBusinessOwnerSearchBean()));
		}

		IPagingDataModel<PmUser> userSearhBusinessOwnerDataModel = getManagedBean()
				.getUserSearchBusinessOwnerDataModel();
		if (userSearhBusinessOwnerDataModel == null) {
			getManagedBean().setUserSearchBusinessOwnerDataModel(
					new UserBOPagingDataModel("existingsearch",
							getManagedBean().getUserBusineeOwnerSearchBean()));
		}
	}

	/**
	 * user Details paging Data Model.
	 */
	private class UserBOPagingDataModel extends PagingDataModel<PmUser> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The user business owner search bean. */
		private UserCommitteeSearchBean userBusinessOwnerSearchBean;

		/**
		 * Instantiates a new user bo paging data model.
		 * 
		 * @param componentId
		 *            the component id
		 * @param userBusinessOwnerSearchBean
		 *            the user business owner search bean
		 */
		public UserBOPagingDataModel(String componentId,
				UserCommitteeSearchBean userBusinessOwnerSearchBean) {
			super(componentId);
			this.userBusinessOwnerSearchBean = userBusinessOwnerSearchBean;
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
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IUserSO userSO;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

				UserReqDTO userReqDTO = new UserReqDTO();
				populateUserSearchCriteria(userBusinessOwnerSearchBean,
						searchBean);
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
	 * Populate user search criteria.
	 * 
	 * @param userBusinessOwnerSearchBean
	 *            the user business owner search bean
	 * @param userSearchBean
	 *            the user search bean
	 */
	private void populateUserSearchCriteria(
			UserCommitteeSearchBean userBusinessOwnerSearchBean,
			ISearchBean<PmUser> userSearchBean) {
		if (userBusinessOwnerSearchBean != null) {

			String name = userBusinessOwnerSearchBean.getName();
			if (!StringUtils.isBlank(name)) {
				userSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.USER_NAME,
								SearchOperator.OPERATOR_LIKE.value(), name,
								true));
			}
			String idno = userBusinessOwnerSearchBean.getIdentificationNo();
			if (!StringUtils.isBlank(idno)) {
				userSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.IDENTIFICATION_NO,
								SearchOperator.OPERATOR_LIKE.value(), idno,
								true));

			}
			String idtype = userBusinessOwnerSearchBean.getIdentificationType();
			if (!StringUtils.isBlank(idtype)) {
				userSearchBean
						.getDataFilterBeans()
						.add(new SearchFilterBean(
								"identificationCode.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(), idtype));
			}

			PmOrgProfile orgProfile = this.getManagedBean().getQt()
					.getPmOrgProfile();
			if (null != orgProfile && null != orgProfile.getOrgProfileId()) {
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
			}
			// Adding search filterbeans to searchbean.
			// userSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}

	/**
	 * Search user action.
	 */
	public void searchUserAction() {
		getManagedBean().getUserSearchBusinessOwnerDataModel().reloadData();
	}

	/**
	 * Qt Details paging Data Model.
	 */
	private class BOPagingDataModel extends PagingDataModel<ScQt> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The clone business owner search bean. */
		private QtnTenderSearchBean cloneBusinessOwnerSearchBean;

		/**
		 * Instantiates a new BO paging data model.
		 * 
		 * @param componentId
		 *            the component id
		 * @param cloneBusinessOwnerSearchBean
		 *            the clone business owner search bean
		 */
		public BOPagingDataModel(String componentId,
				QtnTenderSearchBean cloneBusinessOwnerSearchBean) {
			super(componentId, false);
			this.cloneBusinessOwnerSearchBean = cloneBusinessOwnerSearchBean;
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
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				populateCloneQTSearchCriteria(cloneBusinessOwnerSearchBean,
						searchBean);
				reqDTO.setQtSearchBean(searchBean);
				QuationTenderResDTO resDTO = detailSO.retriveQuationTenderDetailsBySearchCriteria(reqDTO);
				return resDTO.getQtResultBean();
			} finally {
				detailSO = null;
			}
		}
	}

	/**
	 * Populate clone qt search criteria.
	 * 
	 * @param cloneBusinessOwnerSearchBean
	 *            the clone business owner search bean
	 * @param qtSearchBean
	 *            the qt search bean
	 */
	private void populateCloneQTSearchCriteria(
			QtnTenderSearchBean cloneBusinessOwnerSearchBean,
			ISearchBean<ScQt> qtSearchBean) {
		if (cloneBusinessOwnerSearchBean != null) {
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();

			String qtTitle = cloneBusinessOwnerSearchBean.getQtTitle();
			if (!StringUtils.isBlank(qtTitle)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.QT_TITILE,
								SearchOperator.OPERATOR_LIKE.value(), qtTitle,
								true));
			}

			String qtNumber = cloneBusinessOwnerSearchBean.getQtNo();
			if (!StringUtils.isBlank(qtNumber)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_NUMBER);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtNumber);
				searchFilterBean.add(bean);
			}

			String procurementMode = cloneBusinessOwnerSearchBean
					.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("procurementMode.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								procurementMode));
			} else {
				if (StringUtils.isBlank(cloneBusinessOwnerSearchBean
						.getQtStatus())) {
					List<String> procurementModeList = new ArrayList<String>();
					procurementModeList
							.add(ParameterConst.PROCUREMENT_MODE_QUOTATION);
					procurementModeList
							.add(ParameterConst.PROCUREMENT_MODE_TENDER);
					qtSearchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"procurementMode.parameterCode",
									SearchOperator.OPERATOR_IN.value(),
									procurementModeList));
				}
			}

			Date fromCreatedDate = cloneBusinessOwnerSearchBean
					.getFromCreatedDate();
			Date toCreatedDate = cloneBusinessOwnerSearchBean
					.getToCreatedDate();
			if (null != fromCreatedDate || toCreatedDate != null) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromCreatedDate, toCreatedDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}

			Date fromPublishDate = cloneBusinessOwnerSearchBean
					.getFromPublishDate();
			Date toPublishDate = cloneBusinessOwnerSearchBean
					.getToPublishDate();
			if (null != fromPublishDate || null != toPublishDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.PUBLISH_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromPublishDate, toPublishDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}

			Date fromClosingDate = cloneBusinessOwnerSearchBean
					.getFromClosingDate();
			Date toClosingDate = cloneBusinessOwnerSearchBean
					.getToClosingDate();
			if (null != fromClosingDate || null != toClosingDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CLOSED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromClosingDate, toClosingDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}

			String status = cloneBusinessOwnerSearchBean.getQtStatus();
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
			PmOrgProfile orgProfile = this.getManagedBean().getQt().getPmOrgProfile();
			if (null != orgProfile && null != orgProfile.getOrgProfileId()) {
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
										.getOrgProfileId()));
			}
			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}

	/**
	 * Clone search bo action.
	 */
	public void cloneSearchBOAction() {
		getManagedBean().setCloneBusinessSelectQt(null);
		getManagedBean().getCloneBusinessOwnerDataModel().reloadData();
	}

	/**
	 * Gets the ministry specification secretary list.
	 * 
	 * @return the ministry specification secretary list
	 */
	private void getMinistrySpecificationSecretaryList() {
		IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		userReqDto.setRoleCode(RoleCode.MSC_SECRETARY.value());
		UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
		List<PmUser> ministrySpecification = userResDTO.getUserList();
		getManagedBean().setComitteeUserName(ministrySpecification);
	}

	/**
	 * Adds the Business Owner.Event handle add empty row for data table in
	 */
	public void addBusinessOwner() {
		debugLogger("addBusinessOwner");
		getManagedBean().getUserSearchBusinessOwnerDataModel().reloadData();
		// Search Criteria
		getManagedBean().getUserBusineeOwnerSearchBean().setName(null);
		getManagedBean().getUserBusineeOwnerSearchBean().setIdentificationType(
				null);
		getManagedBean().getUserBusineeOwnerSearchBean().setIdentificationNo(
				null);
		getManagedBean().setSelectedPmUser(null);
		if (getManagedBean().getCommitteMember() == null) {
			this.getManagedBean().setCommitteMember(new ScCommitteeMember());
		}
		getManagedBean().getCommitteMember().setUserSelection(
				QTConstants.USER_SELECTION);
		getSelectionTypes();
		getManagedBean().setAutoUpdate(false);
	}

	/**
	 * User will select the selection types like external or new.
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
	 * User Selection Action. when you change user selection it will display the
	 * existing user or new user details.
	 * 
	 * @param event
	 *            (is Mandatory)
	 * 
	 */
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

	/**
	 * Cancel committe action.
	 */
	public void cancelCommitteAction() {
		this.getManagedBean().getScCommitteeMemberList()
				.remove(this.getManagedBean().getCommitteMember());
	}

	/**
	 * On row edit action.
	 * 
	 * @param event
	 *            the event
	 */
	public void onRowEditAction(RowEditEvent event) {
		ScCommitteeMember committeeMember = (ScCommitteeMember) event
				.getObject();
		getManagedBean().setCommitteMember(committeeMember);

	}

	/**
	 * On row cancel.
	 * 
	 * @param event
	 *            the event
	 */
	public void onRowCancel(RowEditEvent event) {
		ScCommitteeMember member = (ScCommitteeMember) event.getObject();
		this.getManagedBean().getScCommitteeMemberList().remove(member);
	}

	/**
	 * On row clone action.
	 * 
	 * @param event
	 *            the event
	 */
	public void onRowCloneAction(SelectEvent event) {
		ScQt scQt = (ScQt) event.getObject();
		getManagedBean().setCloneBusinessSelectQt(scQt);
		// ScQtCommittee scQtCommittee = (ScQtCommittee) event.getObject();
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType(ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER);
		reqDTO.setQtId(scQt.getQtId());
		CommitteResDTO resDTO = new CommitteResDTO();
		ICommitteSO committeSO;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			resDTO = committeSO
					.retrieveCommitteMembersByQtIdAndCommType(reqDTO);
			this.getManagedBean().setCloneCommitteMemebers(null);
			if (null != resDTO.getCommMemberResultBean()
					&& !resDTO.getCommMemberResultBean().getResultList()
							.isEmpty()) {
				List<ScCommitteeMember> commMembers = resDTO
						.getCommMemberResultBean().getResultList();
				this.getManagedBean().setCloneCommitteMemebers(commMembers);
			}
		} finally {
			committeSO = null;
		}
	}

	/**
	 * Consolidate select.
	 * 
	 * @param event
	 *            the event
	 */
	public void consolidateSelect(SelectEvent event) {
		ScCommitteeMember member = (ScCommitteeMember) event.getObject();
		System.out.println("*******" + member.isConsolidate());
	}

	/**
	 * Clone search committe action.
	 */
	public void cloneSearchCommitteAction() {
		QtnTenderSearchBean qtnTenderSearchBean = this.getManagedBean()
				.getCloneQtnTenderSearchBean();
		List<ScQt> cloneSearchQt = retriveQtCommittes(qtnTenderSearchBean);

		this.getManagedBean().setCloneBusinessTenderList(cloneSearchQt);
	}

	/**
	 * Retrive qt committes.
	 * 
	 * @param qtnTenderSearchBean
	 *            the qtn tender search bean
	 * @return the list
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

				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromCreatedDate, toCreatedDate);

				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}

			String status = qtnTenderSearchBean.getQtStatus();
			if (!StringUtils.isBlank(status)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setAndOr(SearchCondition.CONDITION_OR.value());
				bean.setSearchField("scQtStatus.pmStatus.statusId");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(status);
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

			dto = detailSO.retriveQuationTenderDetailsBySearchCriteria(reqDTO);
		} finally {
			detailSO = null;
		}

		return null != dto.getQtResultBean().getResultList() ? dto
				.getQtResultBean().getResultList() : new ArrayList<ScQt>();
	}

	/**
	 * Adds the clone committee members action.
	 */
	public void addCloneCommitteeMembersAction() {
		debugLogger("addCloneCommitteeMembersAction");
		ScCommittee scCommittee = this.getManagedBean().getScQtCommittee()
				.getScCommittee();
		scCommittee.setCommitteStatus(setPmStatus(QTStatusConst.CMT_COMPLETED));

		List<ScCommitteeMember> memberList = getManagedBean()
				.getScCommitteeMemberList();
		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}
		List<String> itemIds = new ArrayList<String>();
		List<ScCommitteeMember> selectedCloneMembersList = getManagedBean()
				.getSelectedCommitteMemebers();
		if (!selectedCloneMembersList.isEmpty()) {
			if (getManagedBean().getScCommitteeMemberList().isEmpty()) {
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
				// memberList.addAll(getManagedBean().getSelectedCommitteMemebers());
			} else {
				for (ScCommitteeMember committee : this.getManagedBean()
						.getScCommitteeMemberList()) {
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
							if (committeeMember.isConsolidate()) {
								this.getManagedBean()
										.setSelectScCommitteMember(
												committeeMember);
							}
							committeeMember.setScCommittee(scCommittee);
							memberList.add(committeeMember);
							this.getManagedBean().setScCommitteeMemberList(
									memberList);
						}
					}
				}
			}
		} else {
			throw new EpException(MessageConst.ERROR_760121);
		}
		scCommittee.setScCommitteeMembers(memberList);

		ScQtCommittee qtCommittee = getManagedBean().getScQtCommittee();
		qtCommittee.setScCommittee(scCommittee);

		// Clone Business Owner validation
		RequestContext.getCurrentInstance()
				.execute("cloneBusinessOwner.hide()");
	}

	/**
	 * Clone business owner popup action.
	 */
	public void cloneBusinessOwnerPopupAction() {
		debugLogger("cloneBusinessOwnerPopupAction");
		getManagedBean().setCloneBusinessSelectQt(null);
		getManagedBean().getCloneBusinessOwnerDataModel().reloadData();
		getManagedBean().getCloneBusinessOwnerSearchBean().setQtTitle(null);
		getManagedBean().getCloneBusinessOwnerSearchBean().setQtNo(null);
		getManagedBean().getCloneBusinessOwnerSearchBean().setProcurementMode(
				null);
		getManagedBean().getCloneBusinessOwnerSearchBean().setFromCreatedDate(
				null);
		getManagedBean().getCloneBusinessOwnerSearchBean().setToCreatedDate(
				null);
		getManagedBean().getCloneBusinessOwnerSearchBean().setQtStatus(null);
		getManagedBean().setCloneCommitteMemebers(null);
		getManagedBean().setCloneBusinessTenderList(null);
		getManagedBean().setSelectedCommitteMemebers(null);

		IPagingDataModel<ScQt> cloneSearchBODataModel = getManagedBean().getCloneBusinessOwnerDataModel();
		if (cloneSearchBODataModel == null) {
			getManagedBean().setCloneBusinessOwnerDataModel(new BOPagingDataModel("clonesearchboid", getManagedBean().getCloneBusinessOwnerSearchBean()));
		}
		getManagedBean().getCloneBusinessOwnerDataModel().resetData();
		getManagedBean().setAutoUpdate(false);
	}

	/**
	 * After Adding the business owner details, need to check the duplicate.
	 */
	public void okBusinessOwnerAction() {
		debugLogger("okBusinessOwnerAction");
		UserReqDTO userReqDTO = new UserReqDTO();
		ScCommittee scCommittee = this.getManagedBean().getScQtCommittee()
				.getScCommittee();
		scCommittee.setCommitteStatus(setPmStatus(QTStatusConst.CMT_COMPLETED));

		ParameterReqDTO parameterReqDTO1 = new ParameterReqDTO();
		parameterReqDTO1.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
		parameterReqDTO1.setParameterCode(ParameterConst.COMMITTEE_ROLE_MEMBER);
		ParameterResDTO parameterResDTO1 = getParameterSO()
				.retrieveParameterByTypeAndCode(parameterReqDTO1);
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
			member.setMemberRole(parameterResDTO1.getPmParameter());
			// member.setMemberRole("M");
			member.setAccessRight(QTConstants.ACCESS_RIGHT);
			member.setPmOrgProfile(this.getManagedBean().getQt()
					.getPmOrgProfile());
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
			// User Notification
			//List<PmUserNotification> pmUserNotifications = new ArrayList<PmUserNotification>();
			//PmUserNotification pmUserNotification = new PmUserNotification();
			//pmUserNotification.setPmUser(user);
			//pmUserNotification.setNotifyMode("E");
			//pmUserNotification.setPreferred(true);
			//pmUserNotifications.add(pmUserNotification);
			//user.setPmUserNotifications(pmUserNotifications);
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
			pmUserRoles.add(pmUserRole);
			PmRole pmRole = new PmRole();
			pmRole.setRoleCode(RoleCode.PLAN_OFFICER.value());
			pmUserRole.setPmRole(pmRole);
			userOrg.setPmUserRoles(pmUserRoles);
			pmUserOrgs.add(userOrg);
			user.setPmUserOrgs(pmUserOrgs);
			user.setOrgTypeCode(pmOrgProfile.getOrgTypeCode());
			// User Preference
			//PmUserPreference pmUserPreference = new PmUserPreference();
			//pmUserPreference.setPmUser(user);
			//pmUserPreference.setFormatDate("DD-MMM-YYYY");
			//pmUserPreference.setFormatTime("HH:mm:ss");
			//pmUserPreference.setFormatNumber("#,##0");
			//pmUserPreference.setPreferredLanguage("en");
			//pmUserPreference.setNotifyRemovalDay(30);
			//user.setPmUserPreference(pmUserPreference);
			userReqDTO.setOrgType(OrganizationType.OTHERS.value());
			userReqDTO.setPmUser(user);
			UserResDTO resDTO = new UserResDTO();

			resDTO = getUserSO().saveUser(userReqDTO);

			member.setPmUser(resDTO.getPmUser());
			member.setScCommittee(scCommittee);

			memberList.add(member);
			this.getManagedBean().setScCommitteeMemberList(memberList);
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
					committeeMember.setPmOrgProfile(this.getManagedBean()
							.getQt().getPmOrgProfile());
					committeeMember.setMemberRole(parameterResDTO1
							.getPmParameter());
					committeeMember.setAccessRight(QTConstants.ACCESS_RIGHT);
					committeeMember.setScCommittee(scCommittee);

					if (this.getManagedBean().getScCommitteeMemberList()
							.isEmpty()) {
						this.getManagedBean().getScCommitteeMemberList()
								.add(committeeMember);
						this.getManagedBean().setSelectScCommitteMember(null);
					} else {
						for (ScCommitteeMember committee : this
								.getManagedBean().getScCommitteeMemberList()) {
							itemIds.add(committee.getIcPassport());
						}
						if (itemIds.size() > 0) {
							if (itemIds.contains(user.getIdentificationNo())) {
								throw new EpException(MessageConst.ERROR_760120);
							} else {
								memberList.add(committeeMember);
								this.getManagedBean().setScCommitteeMemberList(
										memberList);
							}
						}
					}
				}
			} else {
				throw new EpException(MessageConst.ERROR_760121);
			}
		}
		scCommittee.setScCommitteeMembers(memberList);

		// BO validation
		RequestContext.getCurrentInstance().execute("newbusinessowner.hide()");
		this.getManagedBean().setAutoUpdate(true);
	}

	/**
	 * Delete business owner.
	 */
	public void deleteBusinessOwner() {
		debugLogger("deleteBusinessOwner");
		// this.getManagedBean().setAutoUpdate(true);
		int count = 0;
		List<ScCommitteeMember> committeeMemberList = this.getManagedBean()
				.getScCommitteeMemberList();
		List<ScCommitteeMember> selectedMemebers = new ArrayList<ScCommitteeMember>();
		for (int i = 0; i < committeeMemberList.size(); i++) {
			if (committeeMemberList.get(i).isSelected()) {
				count++;
				committeeMemberList.get(i).setDeleted(true);
				selectedMemebers.add(committeeMemberList.get(i));
			}
		}
		committeeMemberList.removeAll(selectedMemebers);
		if (count < 1) {
			throw new EpException(MessageConst.ERROR_760146);
		}
		ScQtCommittee qtCommittee = getManagedBean().getScQtCommittee();
		ScCommittee scCommittee = qtCommittee.getScCommittee();
		scCommittee.setScCommitteeMembers(committeeMemberList);
		this.getManagedBean().setToBeDelCommitteMemebers(selectedMemebers);
	}

	/**
	 * Reset.
	 * 
	 * clear the key in data in Business owner search.
	 */
	public void reset() {
		debugLogger("reset");
		this.getManagedBean().getUserBusineeOwnerSearchBean().setName(null);
		this.getManagedBean().getUserBusineeOwnerSearchBean()
				.setIdentificationType(null);
		this.getManagedBean().getUserBusineeOwnerSearchBean()
				.setIdentificationNo(null);
	}

	/**
	 * Reset clone.
	 * 
	 * clear the key in data in Clone QT search.
	 */
	public void resetClone() {
		debugLogger("resetClone");
		this.getManagedBean().getCloneBusinessOwnerSearchBean()
				.setQtTitle(null);
		this.getManagedBean().getCloneBusinessOwnerSearchBean().setQtNo(null);
		this.getManagedBean().getCloneBusinessOwnerSearchBean()
				.setProcurementMode(null);
		this.getManagedBean().getCloneBusinessOwnerSearchBean()
				.setFromCreatedDate(null);
		this.getManagedBean().getCloneBusinessOwnerSearchBean()
				.setToCreatedDate(null);
		this.getManagedBean().getCloneBusinessOwnerSearchBean()
				.setQtStatus(null);
		this.getManagedBean().getCloneBusinessOwnerSearchBean()
				.setFromPublishDate(null);
		this.getManagedBean().getCloneBusinessOwnerSearchBean()
				.setToPublishDate(null);
		this.getManagedBean().getCloneBusinessOwnerSearchBean()
				.setFromClosingDate(null);
		this.getManagedBean().getCloneBusinessOwnerSearchBean()
				.setToClosingDate(null);
		getManagedBean().setCloneBusinessSelectQt(null);
	}

	/**
	 * Process checked.
	 * 
	 * @param event
	 *            the event
	 */
	public void processChecked(AjaxBehaviorEvent event) {
		Boolean id = (Boolean) event.getComponent().getAttributes()
				.get("value");
		log.info("ID : " + id);
	}

	/**
	 * save Business Owner details save - save data into database.
	 * 
	 * @return the string
	 */
	public String saveBusinessOwnerAction() {
		IQTDetailSO detailSO;

		try {
			detailSO = getDetailSO();
			ScQt scQt = prepareBOInfo(true);
			if (!scQt.isBrandNew()) {
				scQt.setScQtStatus(null);
			}
			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			quationTenderReqDTO.setQtdetail(scQt);
			quationTenderReqDTO.setDocType(QTConstants.DOCTYPE_QT);
			quationTenderReqDTO.setOutcome(BpmOutcomeConst.BPM_SAVE);
			quationTenderReqDTO.setPmUser(new FacesScopes()
					.getUserSessionBean());
			quationTenderReqDTO.setRoleCode(getUserRoleCode());
			quationTenderReqDTO.setToBeRemovedCommitteMembers(this
					.getManagedBean().getToBeDelCommitteMemebers());
			detailSO.validateateBODetails(quationTenderReqDTO);
			/*
			 * // setting removed committemebers list to committe. if (null !=
			 * this.getManagedBean().getToBeDelCommitteMemebers() &&
			 * !this.getManagedBean().getToBeDelCommitteMemebers() .isEmpty()) {
			 * scQt.getScQtCommittees() .get(0) .getScCommittee()
			 * .getScCommitteeMembers() .addAll(this.getManagedBean()
			 * .getToBeDelCommitteMemebers()); }
			 */
			quationTenderReqDTO.setQtdetail(scQt);
			QuationTenderResDTO quationTenderResDTO = detailSO
					.saveQTDtl(quationTenderReqDTO);
			if (quationTenderResDTO.isSuccessful()) {

				ScQt qt = quationTenderResDTO.getQtDetail();

				// To renew the session..
				renewSesionObject();
				getMinistrySpecificationSecretaryList();
				QTDetailBackingBean detailBackingBean = new QTDetailBackingBean();
				detailBackingBean.editQuationTenderDetails(qt);
				this.getManagedBean().setQt(qt);
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_700004, new String[] {
												"",
												this.getManagedBean().getQt()
														.getQtNo() },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
		} finally {
			detailSO = null;
		}
		return null;
	}

	/**
	 * Method to construct business owner details for save or submit.
	 * 
	 * @return ScQt
	 */
	private ScQt prepareBOInfo(boolean isSaveFlow) {
		ScQt scQt = this.getManagedBean().getQt();
		if (!scQt.getSpecApprovalReq()) {
			scQt.setMinistrySpecSecrataryId(null);
		}
		// No need to set the below fields automatically set by listener
		setUserDetails(scQt);
		// Code to set the QtCommitte N Memembers to ScQt
		List<ScQtCommittee> qtCommittees = null;
		if (null != this.getManagedBean().getScQtCommittee()) {
			qtCommittees = new ArrayList<ScQtCommittee>();
			ScQtCommittee qtCommittee = this.getManagedBean()
					.getScQtCommittee();
			if (!isSaveFlow) {
				PmStatus status = new PmStatus();
				status.setStatusId(QTStatusConst.CMT_COMPLETED);
				qtCommittee.getScCommittee().setCommitteStatus(status);
			}

			qtCommittees.add(qtCommittee);
		}
		scQt.setScQtCommittees(qtCommittees);
		scQt.setScQtBriefings(null);
		if (scQt.getQtTitle().equals("")) {
			//throw new EpException("Procurement Title is mandatory.");
			throw new EpException(MessageConst.ERROR_100022, new String[] {getMessageCommon("lbl.sc.title") + "" });
		}
		// Constructing reqDto
		/*
		 * QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		 * quationTenderReqDTO.setQtdetail(scQt);
		 */
		return scQt;
	}

	/**
	 * submit business owner details. submit - save data into database and BPM
	 * process will route to the next respective user.
	 * 
	 * @return the string page navigate to acknowledge page.
	 */
	public String submitBusinessOwnerAction() {
		IQTDetailSO detailSO;
		QuationTenderResDTO quationTenderResDTO = null;
		try {
			detailSO = getDetailSO();
			ScQt scQt = prepareBOInfo(false);

			scQt.setScQtStatus(null);
			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			quationTenderReqDTO.setQtdetail(scQt);
			FacesScopes facesScopes = new FacesScopes();
			quationTenderReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			quationTenderReqDTO.setTaskId(this.getManagedBean().getTaskId());
			quationTenderReqDTO.setPayLoad(this.getManagedBean()
					.getPayLoad());
			quationTenderReqDTO.setOutcome(BpmOutcomeConst.BPM_SUBMIT);
			quationTenderReqDTO.setPmUser(facesScopes.getUserSessionBean());
			quationTenderReqDTO.setRoleCode(getUserRoleCode());
			quationTenderReqDTO.setToBeRemovedCommitteMembers(this
					.getManagedBean().getToBeDelCommitteMemebers());
			detailSO.validateateBODetails(quationTenderReqDTO);

			quationTenderResDTO = detailSO.submitQT(quationTenderReqDTO);
			// To renew the session object..
			renewSesionObject();
			if (quationTenderResDTO.isSuccessful()) {
				scQt = quationTenderResDTO.getQtDetail();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_700026, new String[] {
												"", scQt.getQtNo() },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
		} finally {
			detailSO = null;
		}
		return null;
	}

	/**
	 * To create the Quatation/Tender and redirect to BO creation screen..
	 * 
	 */
	public void preCreateQuotationTenderRenderView() {
		debugLogger("preCreateQuotationTenderRenderView");

		super.renewSesionObject();
		this.getManagedBean().setEditable(false);

		IQTDetailSO detailSO = null;
		try {

			ScQt scQt = new ScQt();
			scQt.setPmOrgProfile(new PmOrgProfile());
			scQt.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			scQt.setMinistrySpecSecrataryId(new PmUser());
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			/*
			 * QuationTenderReqDTO quationTenderReqDTO = new
			 * QuationTenderReqDTO();
			 * quationTenderReqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
			 * QuationTenderResDTO dto = detailSO
			 * .retriveQTDetails(quationTenderReqDTO);
			 */
			ScQtCommittee qtCommitte = new ScQtCommittee();
			qtCommitte.setScQt(scQt);
			setUserDetails(qtCommitte);
			qtCommitte.setCommitteeActivities(null);
			qtCommitte.setQtCommitteRemarks(null);
			qtCommitte.setScQtMeetings(null);
			ScCommittee committee = new ScCommittee();
			PmStatus committeStatus = new PmStatus();
			committeStatus.setStatusId(QTStatusConst.CMT_PENDING_SUBMISSION);
			committee.setCommitteStatus(committeStatus);
			committee.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO
					.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
			parameterReqDTO
					.setParameterCode(ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER);
			ParameterResDTO parameterResDTO = getParameterSO()
					.retrieveParameterByTypeAndCode(parameterReqDTO);
			committee.setCommitteeType(parameterResDTO.getPmParameter());
			qtCommitte.setScCommittee(committee);
			this.getManagedBean().setScQtCommittee(qtCommitte);
			this.getManagedBean().setScCommitteeMemberList(
					qtCommitte.getScCommittee().getScCommitteeMembers());
			this.getManagedBean().setCommitteMember(new ScCommitteeMember());
			PmStatus status = new PmStatus();
			status.setStatusId(QTStatusConst.QT_PENDING_APPOINT_BO);
			scQt.setQtStatus(status);

			/* scQt.setQtNo(dto.getQtNo()); */
			ScQtStatus qtStatus = new ScQtStatus();
			qtStatus.setScQt(scQt);
			qtStatus.setPmStatus(status);
			qtStatus.setCurrent(Boolean.TRUE);
			qtStatus.setDocType(QTConstants.DOCTYPE_QT);
			List<ScQtStatus> qtStatuses = new ArrayList<ScQtStatus>();
			qtStatuses.add(qtStatus);
			scQt.setScQtStatus(qtStatuses);
			this.getManagedBean().setQt(scQt);
			Long currentStatus = this.getManagedBean().getQt().getQtStatus()
					.getStatusId();
			this.getManagedBean().setCurrentStatusId(currentStatus);

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.BO_MAINTANANCE_PAGE);

		} finally {
			detailSO = null;
		}
	}

	/**
	 * To cancle the QT. need to add logic for cancel update bpm
	 * 
	 * @return the string
	 */
	public String cancelQTDetails() {
		ScQt qt = prepareBOInfo(false);
		PmStatus currStatus = qt.getQtStatus();
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		QuationTenderResDTO resDTO = null;
		quationTenderReqDTO.setOutcome(BpmOutcomeConst.BPM_CANCEL);
		quationTenderReqDTO.setPmUser(new FacesScopes().getUserSessionBean());
		quationTenderReqDTO.setRoleCode(getUserRoleCode());
		/*
		 * if (currStatus.getStatusId() > QTStatusConst.QT_PENDING_APPOINT_BO) {
		 * quationTenderReqDTO.setQtdetail(qt); resDTO =
		 * detailSO.cancleQT(quationTenderReqDTO); } else {
		 */
		ScQtStatus qtStatus = new ScQtStatus();
		qtStatus.setScQt(qt);
		PmStatus pmStatus = new PmStatus();
		pmStatus.setStatusId(QTStatusConst.QT_CANCELLED);
		qtStatus.setPmStatus(pmStatus);
		qtStatus.setCurrent(Boolean.TRUE);
		qtStatus.setDocType(QTConstants.DOCTYPE_QT);

		quationTenderReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
		quationTenderReqDTO.setToBeRemovedCommitteMembers(this.getManagedBean()
				.getToBeDelCommitteMemebers());
		QuationTenderResDTO resDTO1 = detailSO
				.retriveQtById(quationTenderReqDTO);
		ScQt scQt = resDTO1.getQtDetail();
		for (ScQtStatus status : scQt.getScQtStatus()) {
			status.setCurrent(Boolean.FALSE);
		}
		scQt.getScQtStatus().add(qtStatus);
		/*
		 * List<ScQtStatus> qtStatuses = new ArrayList<ScQtStatus>();
		 * qtStatuses.add(qtStatus);
		 */
		qt.setScQtStatus(scQt.getScQtStatus());

		quationTenderReqDTO.setQtdetail(qt);

		resDTO = detailSO.saveQTDtl(quationTenderReqDTO);
		// }
		if (resDTO.isSuccessful()) {
			renewSesionObject();
			getMinistrySpecificationSecretaryList();

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
															resDTO.getQtDetail()
																	.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
		}
		QTDetailBackingBean detailBackingBean = new QTDetailBackingBean();

		return detailBackingBean.editQuationTenderDetails(resDTO.getQtDetail());
	}

	/**
	 * Consolidate row select radio.
	 * 
	 * @param event
	 *            the event
	 */
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

	/**
	 * Gets the consolidate select all.
	 * 
	 * @return the consolidate select all
	 */
	public void getConsolidateSelectAll() {
		int selectedCount = 0;
		List<ScCommitteeMember> scCommitteeMemberList = this.getManagedBean()
				.getScCommitteeMemberList();
		if (scCommitteeMemberList != null) {
			for (ScCommitteeMember committeeMember : scCommitteeMemberList) {
				if (committeeMember.isSelected()) {
					selectedCount++;
				}
			}
			flag = scCommitteeMemberList.size() == selectedCount;
		}
	}

	/**
	 * Select all.
	 * 
	 * @param event
	 *            the event
	 */
	public void selectAll(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<ScCommitteeMember> scCommitteeMemberList = this.getManagedBean()
				.getScCommitteeMemberList();
		if (scCommitteeMemberList != null) {

			for (ScCommitteeMember committeeMember : scCommitteeMemberList) {
				if (flag) {
					committeeMember.setSelected(true);
				} else {
					committeeMember.setSelected(false);
				}
			}
		}
	}

	/**
	 * Checks if is flag.
	 * 
	 * @return true, if is flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * Sets the flag.
	 * 
	 * @param flag
	 *            the new flag
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
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
	 * Gets the detail so.
	 * 
	 * @return the detail so
	 */
	private IQTDetailSO getDetailSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
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

	/**
	 * Sets the pm status.
	 * 
	 * @param statusId
	 *            the status id
	 * @return the pm status
	 */
	private PmStatus setPmStatus(Long statusId) {
		PmStatus status = new PmStatus();
		status.setStatusId(statusId);
		return status;
	}

}
