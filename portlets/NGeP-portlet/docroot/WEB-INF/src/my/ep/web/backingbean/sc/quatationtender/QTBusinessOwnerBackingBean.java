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
import my.ep.persistence.entity.sc.quationtender.ScQtStatus;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.UserCommitteeSearchBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
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

	private boolean flag;

	public QTBusinessOwnerBackingBean() {

	}

	@PostConstruct
	public void init() {
		if (this.getManagedBean().getQt().getMinistrySpecSecrataryId() == null) {
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

	private class UserBOPagingDataModel extends PagingDataModel<PmUser> {
		private static final long serialVersionUID = 1L;

		private UserCommitteeSearchBean userBusinessOwnerSearchBean;

		public UserBOPagingDataModel(String componentId,
				UserCommitteeSearchBean userBusinessOwnerSearchBean) {
			super(componentId);
			this.userBusinessOwnerSearchBean = userBusinessOwnerSearchBean;
		}

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
	 * @param userBusinessOwnerSearchBean
	 * @param searchBean
	 */
	private void populateUserSearchCriteria(
			UserCommitteeSearchBean userBusinessOwnerSearchBean,
			ISearchBean<PmUser> userSearchBean) {
		if (userBusinessOwnerSearchBean != null) {
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String name = userBusinessOwnerSearchBean.getName();
			if (!StringUtils.isBlank(name)) {
				userSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(QTConstants.USER_NAME,
								SearchOperator.OPERATOR_LIKE.value(), name,
								true));
			}
			String idno = userBusinessOwnerSearchBean.getIdentificationNo();
			if (!StringUtils.isBlank(idno)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.IDENTIFICATION_NO);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(idno);
				searchFilterBean.add(bean);
			}
			String idtype = userBusinessOwnerSearchBean.getIdentificationType();
			if (!StringUtils.isBlank(idtype)) {
				userSearchBean.getDataFilterBeans().add(new SearchFilterBean("identificationCode.parameterCode", SearchOperator.OPERATOR_EQUALS.value(), idtype));
			}
			if (null != this.getManagedBean().getQt().getPmOrgProfile()) {
			    SearchFilterBean bean = new SearchFilterBean();
			    bean.setSearchField("pmUserOrgs.pmOrgProfile.orgProfileId");
			    bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			    bean.setSearchValue(this.getManagedBean().getQt().getPmOrgProfile().getOrgProfileId());
			    searchFilterBean.add(bean);
			}
			// Adding search filterbeans to searchbean.
			userSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}

	public void searchUserAction() {
		getManagedBean().getUserSearchBusinessOwnerDataModel().reloadData();
	}

	private class BOPagingDataModel extends PagingDataModel<ScQt> {
		private static final long serialVersionUID = 1L;

		private QtnTenderSearchBean cloneBusinessOwnerSearchBean;

		public BOPagingDataModel(String componentId,
				QtnTenderSearchBean cloneBusinessOwnerSearchBean) {
			super(componentId);
			this.cloneBusinessOwnerSearchBean = cloneBusinessOwnerSearchBean;
		}

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
				QuationTenderResDTO resDTO = detailSO.getQuationTenders(reqDTO);
				return resDTO.getQtResultBean();
			} finally {
				detailSO = null;
			}
		}
	}

	/**
	 * @param cloneBusinessOwnerSearchBean
	 * @param searchBean
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
			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}

	public void cloneSearchBOAction() {
		getManagedBean().getCloneBusinessOwnerDataModel().reloadData();
	}

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
		getManagedBean().getCommitteMember().setUserSelection("E");
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
		reqDTO.setCommitteeType(ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER);
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
			this.getManagedBean().setCloneCommitteMemebers(commMembers);
			log.info("List Size : "
					+ getManagedBean().getCloneCommitteMemebers().size());
		} finally {
			committeSO = null;
		}
	}

	public void consolidateSelect(SelectEvent event) {
		ScCommitteeMember member = (ScCommitteeMember) event.getObject();
		System.out.println("*******" + member.isConsolidate());
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

			dto = detailSO.getQuationTenders(reqDTO);
		} finally {
			detailSO = null;
		}

		return null != dto.getQtResultBean().getResultList() ? dto
				.getQtResultBean().getResultList() : new ArrayList<ScQt>();
	}

	public void addCloneCommitteeMembersAction() {
		debugLogger("addCloneCommitteeMembersAction");
		ScCommittee scCommittee = this.getManagedBean().getScQtCommittee().getScCommittee();
		scCommittee.setCommitteStatus(setPmStatus(QTStatusConst.CM_COMPLETE));		
		
		List<ScCommitteeMember> memberList = getManagedBean().getScCommitteeMemberList();
		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}
		List<String> itemIds = new ArrayList<String>();
		List<ScCommitteeMember> members = getManagedBean().getSelectedCommitteMemebers();
		if (!members.isEmpty()) {
			if (getManagedBean().getScCommitteeMemberList().isEmpty()) {
				for (ScCommitteeMember member : members) {
					ScCommitteeMember member1 = null;
					try {
						member1 = (ScCommitteeMember) BeanUtils.cloneBean(member);
					} catch (IllegalAccessException | InstantiationException
							| InvocationTargetException | NoSuchMethodException e) {
						e.printStackTrace();
					}
					if (null != member1) {
						member1.setCommitteeMemberId(null);
						setUserDetails(member1);
						member1.setChangedDate(null);
						if (member1.isConsolidate()) {
							this.getManagedBean()
									.setSelectScCommitteMember(member1);
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
				for (ScCommitteeMember committees : getManagedBean()
						.getSelectedCommitteMemebers()) {
					if (itemIds.size() > 0) {
						if (itemIds.contains(committees.getIcPassport())) {
							throw new EpException("User already existed");
						} else {
							committees.setCommitteeMemberId(null);
							setUserDetails(committees);
							committees.setChangedDate(null);
							if (committees.isConsolidate()) {
								this.getManagedBean().setSelectScCommitteMember(
										committees);
							}
							committees.setScCommittee(scCommittee);
							memberList.add(committees);
							this.getManagedBean().setScCommitteeMemberList(
									memberList);
						}
					}
				}
			}
		} else {
			throw new EpException("Please select a record.");
		}
		scCommittee.setScCommitteeMembers(memberList);
		
		ScQtCommittee qtCommittee = getManagedBean().getScQtCommittee();
		qtCommittee.setScCommittee(scCommittee);
				
		// Clone Business Owner validation
		RequestContext.getCurrentInstance().execute("cloneBusinessOwner.hide()");
	}

	public void cloneBusinessOwnerPopupAction() {
		debugLogger("cloneBusinessOwnerPopupAction");
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
	}

	public void okBusinessOwnerAction() {
		debugLogger("okBusinessOwnerAction");
		UserReqDTO userReqDTO = new UserReqDTO();
		ScCommittee scCommittee = this.getManagedBean().getScQtCommittee().getScCommittee();
		scCommittee.setCommitteStatus(setPmStatus(QTStatusConst.CM_COMPLETE));
		
		// Setting the pm status for New User
		PmStatus status = setPmStatus(60730L);
		// scQt.setQtStatus(status);
		
		ParameterReqDTO parameterReqDTO1 = new ParameterReqDTO();
		parameterReqDTO1.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
		parameterReqDTO1.setParameterCode(ParameterConst.COMMITTEE_ROLE_MEMBER);
		ParameterResDTO parameterResDTO1 = getParameterSO()
				.retrieveParameterByTypeAndCode(parameterReqDTO1);
		List<ScCommitteeMember> memberList = getManagedBean().getScCommitteeMemberList();
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
			UserResDTO resDTO = getUserSO().saveUser(userReqDTO);
			member.setPmUser(resDTO.getPmUser());
			member.setScCommittee(scCommittee);
			
			memberList.add(member);
			getManagedBean().setScCommitteeMemberList(memberList);
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
					committeeMember.setPmOrgProfile(this.getManagedBean().getQt()
							.getPmOrgProfile());
					committeeMember.setMemberRole(parameterResDTO1.getPmParameter());
					committeeMember.setAccessRight(QTConstants.ACCESS_RIGHT);
					committeeMember.setScCommittee(scCommittee);
					
					if (this.getManagedBean().getScCommitteeMemberList().isEmpty()) {
						this.getManagedBean().getScCommitteeMemberList()
								.add(committeeMember);
					} else {
						for (ScCommitteeMember committee : this.getManagedBean()
								.getScCommitteeMemberList()) {
							itemIds.add(committee.getIcPassport());
						}
						if (itemIds.size() > 0) {
							if (itemIds.contains(user.getUserId())) {
								throw new EpException("User already existed");
							} else {
								memberList.add(committeeMember);
								this.getManagedBean().setScCommitteeMemberList(
										memberList);
							}
						}
					}
				}
			} else {
				throw new EpException("Please select a record.");
			}
		}
		scCommittee.setScCommitteeMembers(memberList);
		
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
		
		ScQtCommittee qtCommittee = getManagedBean().getScQtCommittee();
		ScCommittee scCommittee = qtCommittee.getScCommittee();
		scCommittee.setScCommitteeMembers(committeeMemberList);
	}

	public void reset() {
		debugLogger("reset");
		this.getManagedBean().getUserBusineeOwnerSearchBean().setName(null);
		this.getManagedBean().getUserBusineeOwnerSearchBean()
				.setIdentificationType(null);
		this.getManagedBean().getUserBusineeOwnerSearchBean()
				.setIdentificationNo(null);
	}

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
	}

	public void processChecked(AjaxBehaviorEvent event) {
		Boolean id = (Boolean) event.getComponent().getAttributes()
				.get("value");
		log.info("ID : " + id);
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
			ScQt scQt = prepareBOInfo();
			if (!scQt.isBrandNew()) {
				scQt.setScQtStatus(null);
			}
			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			quationTenderReqDTO.setQtdetail(scQt);
			quationTenderReqDTO.setDocType(QTConstants.DOCTYPE_QT);
			detailSO.validateateBODetails(quationTenderReqDTO);
			QuationTenderResDTO quationTenderResDTO = detailSO.saveQTDtl(quationTenderReqDTO);
			if (quationTenderResDTO.isSuccessful()) {
				// TODO Need to think to move to finally block...
				ScQt qt = quationTenderResDTO.getQtDetail();
				
				// To renew the session..
				renewSesionObject();
				getMinistrySpecificationSecretaryList();
				QTDetailBackingBean detailBackingBean = new QTDetailBackingBean();
				detailBackingBean.editAction(qt);
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
																"",
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
	private ScQt prepareBOInfo() {
		ScQt scQt = this.getManagedBean().getQt();
		if (!scQt.isSpecApprovalReq()) {
			scQt.setMinistrySpecSecrataryId(null);
		}
		// No need to set the below fields automatically set by listener
		setUserDetails(scQt);
		// Code to set the QtCommitte N Memembers to ScQt
		List<ScQtCommittee> qtCommittees = null;
		if (null != this.getManagedBean().getScQtCommittee()) {
			qtCommittees = new ArrayList<ScQtCommittee>();
			qtCommittees.add(this.getManagedBean().getScQtCommittee());
		}
		scQt.setScQtCommittees(qtCommittees);
		scQt.setScQtBriefings(null);
		// Constructing reqDto
		/*
		 * QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		 * quationTenderReqDTO.setQtdetail(scQt);
		 */
		return scQt;
	}

	/**
	 * Will called submit Bo details..
	 * 
	 * @return
	 */
	public String submitBusinessOwnerAction() {
		IQTDetailSO detailSO;
		QuationTenderResDTO quationTenderResDTO = null;
		try {
			detailSO = getDetailSO();
			ScQt scQt = prepareBOInfo();

			scQt.setScQtStatus(null);
			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			quationTenderReqDTO.setQtdetail(scQt);
			FacesScopes facesScopes = new FacesScopes();
			String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
			quationTenderReqDTO.setToken(ctxToken);
			quationTenderReqDTO.setTaskId(this.getManagedBean().getTaskId());
			quationTenderReqDTO.setPayLoad(this.getManagedBean()
					.getQuotationTenderDataType());
			detailSO.validateateBODetails(quationTenderReqDTO);
			quationTenderResDTO = detailSO.submitQT(quationTenderReqDTO);
			// To renew the session object..
			renewSesionObject();
			if (quationTenderResDTO.isSuccessful()) {
				scQt = quationTenderResDTO.getQtDetail();
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
																"",
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
		committeStatus.setStatusId(QTStatusConst.CM_PENDING_SUBMISSION);
		committee.setCommitteStatus(committeStatus);
		committee.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		parameterReqDTO.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
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

		scQt.setQtNo(dto.getQtNo());
		ScQtStatus qtStatus = new ScQtStatus();
		qtStatus.setScQt(scQt);
		qtStatus.setPmStatus(status);
		qtStatus.setCurrent(true);
		qtStatus.setDocType(QTConstants.DOCTYPE_QT);
		List<ScQtStatus> qtStatuses = new ArrayList<ScQtStatus>();
		qtStatuses.add(qtStatus);
		scQt.setScQtStatus(qtStatuses);
		this.getManagedBean().setQt(scQt);
		return "/xhtml/sc/quatationtender/businessowner/businessownercreation.xhtml"
				+ "?faces-redirect=true";
	}

	/**
	 * To cancle the QT. need to add logic for cancel update bpm
	 * 
	 * @return
	 */
	public String cancelQTDetails() {
		ScQt qt = prepareBOInfo();
		ScQtStatus qtStatus = new ScQtStatus();
		qtStatus.setScQt(qt);
		PmStatus status = new PmStatus();
		status.setStatusId(QTStatusConst.QT_CANCELLED);
		qtStatus.setPmStatus(status);
		qtStatus.setCurrent(true);
		qtStatus.setDocType(QTConstants.DOCTYPE_QT);
		List<ScQtStatus> qtStatuses = new ArrayList<ScQtStatus>();
		qtStatuses.add(qtStatus);
		qt.setScQtStatus(qtStatuses);
		QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		quationTenderReqDTO.setQtdetail(qt);

		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderResDTO resDTO = detailSO.saveQTDtl(quationTenderReqDTO);
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

		return detailBackingBean.editAction(resDTO.getQtDetail());
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

	public void getConsolidateSelectAll() {
		int selectedCount = 0;
		List<ScCommitteeMember> scCommitteeMemberList = getManagedBean()
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

	public void selectAll(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<ScCommitteeMember> scCommitteeMemberList = getManagedBean()
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

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

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
	
	private IQTDetailSO getDetailSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
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
	
	private PmStatus setPmStatus(Long statusId){
		PmStatus status = new PmStatus();
		status.setStatusId(statusId);
		return status;
	}

}
