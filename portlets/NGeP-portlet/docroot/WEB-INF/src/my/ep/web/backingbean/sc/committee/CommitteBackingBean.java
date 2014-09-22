package my.ep.web.backingbean.sc.committee;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
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
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.committeedata.SCCommitteeDataType;
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
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeRemark;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.CommitteType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.UserCommitteeSearchBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.sc.committee.CommitteManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.type.sc.quationtender.MemberType;
import my.ep.web.type.sc.quationtender.SelectionType;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import com.liferay.portal.util.PortalUtil;

/**
 * @author VidyasagarReddy
 * 
 */
@ManagedBean(name = "committeBackingBean")
@RequestScoped
public class CommitteBackingBean extends
		SourcingBaseBackingBean<CommitteManagedBean> {

	private static final String NAV_TO_COMMITTEE_MAINTENANACE = "/xhtml/sc/committe/committeemaintanancepage.xhtml";
	private static final String NAV_TO_VIEW_HISTORY = "/xhtml/sc/committe/viewHistory.xhtml";

	private static final Logger logger = EpLogger
			.getLogger(CommitteBackingBean.class);

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}

	public CommitteBackingBean() {
		getManagedBean().setRoleTypes(getRoleTypeDropdownValues());

		if (this.getManagedBean().getOutComes() == null) {
			this.getManagedBean().setOutComes(new String[] { BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT });
		}
		
		IPagingDataModel<ScQt> cloneSearchQTDataModel = getManagedBean().getCloneSearchQTDataModel();
		if (cloneSearchQTDataModel == null) {
			getManagedBean().setCloneSearchQTDataModel(new CloneQTPagingDataModel("clonesearchCommitteid", getManagedBean().getCloneQtnTenderSearchBean()));
		}
		
		IPagingDataModel<PmUser> userSearhQCDataModel = getManagedBean().getUserSearchQCDataModel();
		if (userSearhQCDataModel == null) {
			getManagedBean().setUserSearchQCDataModel(new UserQCPagingDataModel("userexistid", getManagedBean().getUserQCSearchBean()));
		}
	}

	private class UserQCPagingDataModel extends PagingDataModel<PmUser> {
		private static final long serialVersionUID = 1L;
		
		private UserCommitteeSearchBean userQCSearchBean;

		public UserQCPagingDataModel(String componentId, UserCommitteeSearchBean userQCSearchBean) {
			super(componentId);
			this.userQCSearchBean = userQCSearchBean;
		}

		@Override
		protected ISearchResultBean<PmUser> pageChangeListener(ISearchBean<PmUser> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IUserSO userSO;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserReqDTO userReqDTO = new UserReqDTO();
				populateUserSearchCriteria(userQCSearchBean, searchBean);
				userReqDTO.setUserSearchBean(searchBean);
				UserResDTO resDto = userSO.searchUserProfile(userReqDTO);
				if (resDto != null) {
					getManagedBean().setExternalUsers(resDto.getUserResultBean().getResultList());
					for(PmUser user : getManagedBean().getExternalUsers()) {
						if (user.getMemberRole() == null ) {
							user.setMemberRole(new PmParameter());
						}
						if (user.getRepresentativesType() == null) {
							user.setRepresentativesType(new PmParameter());
						}
					}
				}
				return resDto.getUserResultBean();
			} finally {
				userSO = null;
			}
		}
	}
	
	private void populateUserSearchCriteria(UserCommitteeSearchBean userQCSearchBean, ISearchBean<PmUser> userSearchBean) {
		if (userQCSearchBean != null) {
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String name = userQCSearchBean.getName();
			if (!StringUtils.isBlank(name)) {
				userSearchBean.getDataFilterBeans().add(new SearchFilterBean(QTConstants.QT_TITILE, SearchOperator.OPERATOR_LIKE.value(), name, true));
			}
			String idno = userQCSearchBean.getIdentificationNo();
			if (!StringUtils.isBlank(idno)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.IDENTIFICATION_NO);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(idno);
				searchFilterBean.add(bean);
			}
			String idtype = userQCSearchBean.getIdentificationType();
			if (!StringUtils.isBlank(idtype)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setAndOr(SearchCondition.CONDITION_OR.value());
				bean.setSearchField("identificationCode.parameterCode");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(idtype);
				searchFilterBean.add(bean);
			}
			userSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}
	
	public void searchUserAction() {
		getManagedBean().getUserSearchQCDataModel().reloadData();
	}
	
	/**
	 * @param committeeRole
	 * @param types
	 * @return
	 */
	public SelectItem[] getRoleTypeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		SelectItem[] items = getDropdownValuesBySearchBean(ParameterTypeConst.COMMITTEE_ROLE, searchBean);
		return items;
	}

	@PostConstruct
	private void init() {
		IPagingDataModel<ScQtCommittee> searchQTDataModel = getManagedBean()
				.getSearchQTDataModel();

		if (searchQTDataModel == null) {
			getManagedBean().setSearchQTDataModel(new QTPagingDataModel("searchQTs", getManagedBean().getQtnTenderSearchBean()));
		}
	}

	private class QTPagingDataModel extends PagingDataModel<ScQtCommittee> {
		private static final long serialVersionUID = 1L;

		private QtnTenderSearchBean qtnTenderSearchBean;

		public QTPagingDataModel(String componentId, QtnTenderSearchBean qtnTenderSearchBean) {
			super(componentId);
			this.qtnTenderSearchBean = qtnTenderSearchBean;
		}

		@Override
		public ISearchResultBean<ScQtCommittee> pageChangeListener(
				ISearchBean<ScQtCommittee> searchBean) {
			if (logger.isDebugEnabled()) {
				logger.debug("pageChangeListener");
			}
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();
				populateQTSearchCriteria(qtnTenderSearchBean, searchBean);
				committeReqDTO.setCommSearchBean(searchBean);
				CommitteResDTO resDTO = committeSO.retriveQtCommitteDtls(committeReqDTO);
				return resDTO.getQtCommitteResultBean();
			} finally {
				committeSO = null;
			}
		}
	}

	private void populateQTSearchCriteria(QtnTenderSearchBean qtnTenderSearchBean, ISearchBean<ScQtCommittee> qtSearchBean) {
		if (qtnTenderSearchBean != null) {
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String qtTitle = qtnTenderSearchBean.getQtTitle();
			if (!StringUtils.isBlank(qtTitle)) {
				qtSearchBean.getDataFilterBeans().add(new SearchFilterBean(QTConstants.QT_TITILE, SearchOperator.OPERATOR_LIKE.value(), qtTitle, true));
			}
			String qtNumber = qtnTenderSearchBean.getQtNo();
			if (!StringUtils.isBlank(qtNumber)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("scQt.qtNo");
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtNumber);
				searchFilterBean.add(bean);
			}
			Date fromCreatedDate = qtnTenderSearchBean.getFromCreatedDate();
			if (null != fromCreatedDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(fromCreatedDate);
				searchFilterBean.add(bean);
			}
			String status = qtnTenderSearchBean.getQtStatus();
			if (!StringUtils.isBlank(status)) {
				qtSearchBean.getDataFilterBeans().add(new SearchFilterBean("scCommittee.committeStatus.statusId", SearchOperator.OPERATOR_EQUALS.value(), status));
			}
			String committeeType = qtnTenderSearchBean.getCommitteeType();
			if (!StringUtils.isBlank(committeeType)) {
				qtSearchBean.getDataFilterBeans().add(new SearchFilterBean("scCommittee.committeeType.parameterCode", SearchOperator.OPERATOR_EQUALS.value(), committeeType));
			}
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
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
		// committeReqDTO.setQtCommittee(this.getManagedBean().getQtCommittee());
		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		committeReqDTO.setToken(ctxToken);
		committeReqDTO.setTaskId(this.getManagedBean().getTaskId());
		committeReqDTO.setOutcome(action);
		ICommitteSO commso = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
		try {
			CommitteResDTO resDTO = commso.updateBPMAction(committeReqDTO);
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
		debugLogger("saveCommitteDetails");
		ICommitteSO committeSO;

		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);

			CommitteReqDTO committeReqDTO = constructSaveCommitte();

			CommitteResDTO resDTO = committeSO.saveQTDtl(committeReqDTO);
			this.getManagedBean().setQtCommittee(resDTO.getScQtCommittee());
			logger.info("QT Committee Id : "
					+ this.getManagedBean().getQtCommittee().getQtCommitteeId());
			logger.info("Committee Id : "
					+ this.getManagedBean().getQtCommittee().getScCommittee()
							.getCommitteeId());
			logger.info("Committee Type : "
					+ this.getManagedBean().getQtCommittee().getScCommittee()
							.getCommitteeType());
			logger.info("Committee Member Id : "
					+ this.getManagedBean().getQtCommittee().getScCommittee()
							.getScCommitteeMembers().get(0)
							.getCommitteeMemberId());
			logger.info("Committee Member Type : "
					+ this.getManagedBean().getQtCommittee().getScCommittee()
							.getScCommitteeMembers().get(0).getMemberType());

			if (resDTO.isSuccessful()) {
				// ScQtCommittee scQtCommittee = resDTO.getScQtCommittee();
				/*
				 * FacesContext.getCurrentInstance().addMessage(null, new
				 * FacesMessage( FacesMessage.SEVERITY_INFO, null,
				 * MessageUtils.getMessage( MessageConst.INFO_700004, new
				 * String[] {
				 * getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES,
				 * ParameterConst.DOCTYPE_QUOTATION), scQt.getQtNo() },
				 * MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes()
				 * .getUserLocale())));
				 */

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.",
								"Committe Type. : "
										+ this.getManagedBean()
												.getScCommittee()
												.getCommitteeType()));
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
		debugLogger("submitCommitteDetails");
		CommitteReqDTO committeReqDTO = constructSaveCommitte();
		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		committeReqDTO.setToken(ctxToken);
		ICommitteSO commso = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
		CommitteResDTO resDTO = null;

		try {
			/*
			 * resDTO = commso.submitCommitteeDetails(committeReqDTO); if
			 * (resDTO.isSuccessful()) {
			 * 
			 * FacesContext.getCurrentInstance().addMessage( null, new
			 * FacesMessage(FacesMessage.SEVERITY_INFO,
			 * "Record saved successfully.", "Committe Type. : " +
			 * this.getManagedBean() .getScCommittee() .getCommitteeType()));
			 * 
			 * return "/xhtml/shared/acknowledge.xhtml";
			 * 
			 * } else { throw new EpException(""); }
			 */
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
		setUserDetails(scQtCommittee);

		// SCcommitteData setting
		ScCommittee committee = this.getManagedBean().getScCommittee();
		setUserDetails(committee);

		// Sccommittemembers info
		List<ScCommitteeMember> committeMembers = this.getManagedBean().getQtCommittee().getScCommittee().getScCommitteeMembers();
		List<ScCommitteeMember> commMembers = new ArrayList<ScCommitteeMember>();
		if (committeMembers != null && !committeMembers.isEmpty()) {
			for (ScCommitteeMember comMember : committeMembers) {
				comMember.setScCommittee(committee);
				setUserDetails(comMember);
				comMember.setMemberType("T");
				commMembers.add(comMember);
			}
		}
		committee.setScCommitteeMembers(commMembers);
		// Need to change this status based on qt status
		// committee.setStatusId(1);

		scQtCommittee.setScCommittee(committee);

		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setToken(ctxToken);

		committeReqDTO.setScQtCommittee(scQtCommittee);

		return committeReqDTO;
	}

	/**
	 * Method to search the quationtenders based on serach date
	 */
	public void searchQTAction() {
		getManagedBean().getSearchQTDataModel().reloadData();
	}

	/**
	 * Method to return the maintance page
	 * 
	 * @return maintanacepage
	 */
	public String editAction(ScQtCommittee scQtCommittee) {
		debugLogger("editAction");
		this.getManagedBean().setHeaderTitle(CommitteType.getValue(scQtCommittee.getScCommittee().getCommitteeType().getParameterCode()));
		log.info("Title : " + getManagedBean().getHeaderTitle());
		this.getManagedBean().setScCommittee(scQtCommittee.getScCommittee());
		String committeeType = getManagedBean().getScCommittee().getCommitteeType().getParameterCode();
		log.info("Committee Type : " + committeeType);
		if (committeeType.equalsIgnoreCase("OC") || committeeType.equalsIgnoreCase("EC") || committeeType.equalsIgnoreCase("TEC") || committeeType.equalsIgnoreCase("FEC")) {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			PmParameter commType = scQtCommittee.getScCommittee().getCommitteeType();
			Long qtId = scQtCommittee.getScQt().getQtId();
			reqDTO.setCommitteeType(commType.getParameterCode());
			reqDTO.setQtId(qtId);
			CommitteResDTO resDTO = null;
			ICommitteSO committeSO;
			ScQtCommittee qtCommitte = null;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
				qtCommitte = resDTO.getScQtCommittee();
				if (null == qtCommitte) {
					qtCommitte = new ScQtCommittee();
					qtCommitte.setScQt(this.getManagedBean().getQt());
					setUserDetails(qtCommitte);
					qtCommitte.setCommitteeActivities(null);
					qtCommitte.setQtCommitteRemarks(null);
					qtCommitte.setScQtMeetings(null);
					ScCommittee committee = new ScCommittee();
					committee.setCommitteeType(commType);
					setUserDetails(committee);
					PmStatus committeStatus = new PmStatus();
					committeStatus.setStatusId(60200l);
					committee.setCommitteStatus(committeStatus);
					committee.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
					qtCommitte.setScCommittee(committee);
				}
				this.getManagedBean().setQtCommittee(qtCommitte);

				ScCommittee committee = resDTO.getCommittee();
				if (null == committee) {
					committee = new ScCommittee();
				}
				List<ScCommitteeRemark> committeeRemarks = committee.getScCommitteeRemarks();
				if (committeeRemarks == null) {
					committeeRemarks = new ArrayList<ScCommitteeRemark>();
				}
				if (null != committeeRemarks && !committeeRemarks.isEmpty()) {
					for (ScCommitteeRemark remark : getManagedBean().getScCommittee().getScCommitteeRemarks()) {
						ScCommitteeRemark qtRemark = (ScCommitteeRemark) remark;
						committeeRemarks.add(qtRemark);
					}
					this.getManagedBean().setScCommitteeRemarksList(committeeRemarks);
				}
				initializeRemarks();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			showQtDetailAction(scQtCommittee.getScQt().getQtId());
			return NAV_TO_COMMITTEE_MAINTENANACE + "?faces-redirect=true";

		} else if (committeeType.equals("QCB") || committeeType.equals("QCA") || committeeType.equals("PBB") || committeeType.equals("PBA") || committeeType.equals("PBS") || committeeType.equals("PBQ")) {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			PmParameter commType = scQtCommittee.getScCommittee().getCommitteeType();
			Long qtId = scQtCommittee.getScQt().getQtId();
			reqDTO.setCommitteeType(commType.getParameterCode());
			reqDTO.setQtId(qtId);
			CommitteResDTO resDTO = null;
			ICommitteSO committeSO;
			ScQtCommittee qtCommitte = null;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
				qtCommitte = resDTO.getScQtCommittee();
				if ("QCB".equalsIgnoreCase(committeeType) || "QCA".equalsIgnoreCase(committeeType) ) {
					SelectItem[] qcItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_QC);
					this.getManagedBean().setRepresentativesTypeslist(Arrays.asList(qcItems));
				} else if ("PBB".equalsIgnoreCase(committeeType)) {
					SelectItem[] pbbItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_PBB);
					this.getManagedBean().setRepresentativesTypeslist(Arrays.asList(pbbItems));
				} else if ("PBA".equalsIgnoreCase(committeeType)) {
					SelectItem[] pbaItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_PBA);
					this.getManagedBean().setRepresentativesTypeslist(Arrays.asList(pbaItems));
				} else if ("PBS".equalsIgnoreCase(committeeType)) {
					SelectItem[] fpbhItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_FPBH);
					this.getManagedBean().setRepresentativesTypeslist(Arrays.asList(fpbhItems));
				} else if ("PBQ".equalsIgnoreCase(committeeType)) {
					SelectItem[] fpbkItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_FPBK);
					this.getManagedBean().setRepresentativesTypeslist(Arrays.asList(fpbkItems));
				}
				if (null == qtCommitte) {
					qtCommitte = new ScQtCommittee();
					qtCommitte.setScQt(this.getManagedBean().getQt());
					setUserDetails(qtCommitte);
					qtCommitte.setCommitteeActivities(null);
					qtCommitte.setQtCommitteRemarks(null);
					qtCommitte.setScQtMeetings(null);
					ScCommittee committee = new ScCommittee();
					committee.setCommitteeType(commType);
					setUserDetails(committee);
					PmStatus committeStatus = new PmStatus();
					committeStatus.setStatusId(60200l);
					committee.setCommitteStatus(committeStatus);
					committee.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
					qtCommitte.setScCommittee(committee);
				}
				for (ScCommitteeMember committeeMember : qtCommitte.getScCommittee().getScCommitteeMembers()) {
					if (committeeMember.getRepresentativeType() == null) {
						committeeMember.setRepresentativeType(new PmParameter());
					}
				}
				this.getManagedBean().setQtCommittee(qtCommitte);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			showQtDetailAction(scQtCommittee.getScQt().getQtId());
			return NAV_TO_COMMITTEE_MAINTENANACE + "?faces-redirect=true";
			
		} else if (committeeType.equals("BO") || committeeType.equals("SC")) {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			PmParameter commType = scQtCommittee.getScCommittee().getCommitteeType();
			Long qtId = scQtCommittee.getScQt().getQtId();
			reqDTO.setCommitteeType(commType.getParameterCode());
			reqDTO.setQtId(qtId);
			CommitteResDTO resDTO = null;
			ICommitteSO committeSO;
			ScQtCommittee qtCommitte = null;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
				qtCommitte = resDTO.getScQtCommittee();
				if (null == qtCommitte) {
					qtCommitte = new ScQtCommittee();
					qtCommitte.setScQt(this.getManagedBean().getQt());
					setUserDetails(qtCommitte);
					qtCommitte.setCommitteeActivities(null);
					qtCommitte.setQtCommitteRemarks(null);
					qtCommitte.setScQtMeetings(null);
					ScCommittee committee = new ScCommittee();
					committee.setCommitteeType(commType);
					setUserDetails(committee);
					PmStatus committeStatus = new PmStatus();
					committeStatus.setStatusId(60200l);
					committee.setCommitteStatus(committeStatus);
					committee.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
					qtCommitte.setScCommittee(committee);
				}
				this.getManagedBean().setQtCommittee(qtCommitte);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			//showQtDetailAction(scQtCommittee.getScQt().getQtId());
			return NAV_TO_COMMITTEE_MAINTENANACE + "?faces-redirect=true";
		}
		return null;
	}

	/**
	 * 
	 */
	private void initializeRemarks() {
		debugLogger("initializeRemarks");
		FacesScopes facesScopes = new FacesScopes();
		IStatusSO iStatusSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		ScCommitteeRemark committeeRemark = null;
		if (this.getManagedBean().getScCommitteeRemarksList() == null) {
			this.getManagedBean().setScCommitteeRemarksList(
					new ArrayList<ScCommitteeRemark>());
		}
		if (null == committeeRemark) {
			committeeRemark = new ScCommitteeRemark();
		}
		committeeRemark.setCreatedBy(facesScopes.getUserSessionBean()
				.getUserId());
		committeeRemark
				.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		committeeRemark.setChangedBy(committeeRemark.getCreatedBy());
		committeeRemark.setCreatedUser(facesScopes.getUserSessionBean());
		PmStatus status = new PmStatus();
		status.setStatusId(QTStatusConst.CM_PENDING_SUBMISSION);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		if (status != null) {
			statusReqDTO.setStatusId(status.getStatusId());
			StatusResDTO statusResDTO = iStatusSO
					.retrieveStatusByStatusId(statusReqDTO);
			committeeRemark.setPmStatus(statusResDTO.getPmStatus());
		}
		this.getManagedBean().getScCommitteeRemarksList().add(committeeRemark);
	}

	public String viewHistoryAction(ScQtCommittee scQtCommittee) {
		debugLogger("viewHistoryAction");
		String committeeType = scQtCommittee.getScCommittee().getCommitteeType().getParameterCode();
		log.info("Committee Type : " + committeeType);
		return NAV_TO_VIEW_HISTORY + "?faces-redirect=true";
	}

	public String viewHistoryBackAction() {
		debugLogger("viewHistoryBackAction");
		return NAV_TO_COMMITTEE_MAINTENANACE + "?faces-redirect=true";
	}
	
	private class CloneQTPagingDataModel extends PagingDataModel<ScQt> {

		private static final long serialVersionUID = 1L;

		private QtnTenderSearchBean cloneQtnTenderSearchBean;

		public CloneQTPagingDataModel(String componentId,
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

				populateCloneQTSearchCriteria(cloneQtnTenderSearchBean, searchBean);
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
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_TITILE);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtTitle);
				searchFilterBean.add(bean);
			}

			String qtNumber = cloneQtnTenderSearchBean.getQtNo();
			if (!StringUtils.isBlank(qtNumber)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_NUMBER);
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(qtNumber);
				searchFilterBean.add(bean);
			}
			
			String procurementMode = cloneQtnTenderSearchBean.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setAndOr(SearchCondition.CONDITION_OR.value());
				bean.setSearchField("procurementMode.parameterCode");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(procurementMode);
				searchFilterBean.add(bean);
			}

			Date fromCreatedDate = cloneQtnTenderSearchBean.getFromCreatedDate();
			Date toCreatedDate = cloneQtnTenderSearchBean.getToCreatedDate();
			if (null != fromCreatedDate || toCreatedDate != null) {
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
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtStatus.pmStatus.statusId",
								SearchOperator.OPERATOR_EQUALS.value(),
								status));				
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtStatus.current",
								SearchOperator.OPERATOR_EQUALS.value(),
								true));
			}
			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}
	
	public void cloneSearchQtAction() {
		getManagedBean().getCloneSearchQTDataModel().reloadData();
	}

	public void onRowCloneAction(SelectEvent event) {
		ScQt scQt = (ScQt) event.getObject();
		System.out.println("Committee Type1 : "
				+ getManagedBean().getScCommittee().getCommitteeType());

		CommitteReqDTO reqDTO = new CommitteReqDTO();
		/*
		 * reqDTO.setCommitteeType(getManagedBean().getScCommittee()
		 * .getCommitteeType());
		 */
		reqDTO.setQtId(scQt.getQtId());
		CommitteResDTO resDTO = null;
		try {
			ICommitteSO committeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
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
						+ committeeMember.getEmail() + "Committee Type : " + committeeMember.getScCommittee().getCommitteeType().getParameterCode());
				this.getManagedBean().setCommTypeTitle(CommitteType.getValue(committeeMember.getScCommittee().getCommitteeType().getParameterCode()));
				logger.info("Committee Type : " + this.getManagedBean().getCommTypeTitle());
			}
			this.getManagedBean().setCloneCommitteMemebers(commMembers);
			System.out.println("======== list size==== : "
					+ getManagedBean().getCloneCommitteMemebers().size());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Method to show the qtdetails
	 * 
	 * @param qtId
	 */
	public void showQtDetailAction(Long qtId) {

		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		reqDTO.setQtId(qtId);
		QuationTenderResDTO tenderResDTO = detailSO.retriveQtById(reqDTO);
		ScQt selectedQt = tenderResDTO.getQtDetail();
		this.getManagedBean().setQt(selectedQt);
		List<ScQt> qtList = new ArrayList<ScQt>();
		qtList.add(selectedQt);
		this.getManagedBean().setScQt(qtList);
		/*System.out.println(" Status : " + this.getManagedBean().getQt().getScQtCommittees().get(0).getScCommittee().getCommitteStatus().getStatusId());*/
		System.out.println("ProcurementMode : "
				+ this.getManagedBean().getQt().getProcurementMode());
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
		// retriveGovUsers();
		this.getManagedBean().setCommitteMember(committeeMember);
		getSelectionTypes();
		getUserTypes();

	}

	/*
	 * public void addCommitteMember() { debugLogger("addCommitteMember");
	 * List<ScCommitteeMember> committeMemebersList = this.getManagedBean()
	 * .getCommitteMembers(); if (committeMemebersList == null) {
	 * committeMemebersList = new ArrayList<ScCommitteeMember>(); }
	 * ScCommitteeMember committeeMember = new ScCommitteeMember();
	 * committeeMember.setUserSelection("E");
	 * this.getManagedBean().setCommitteMember(committeeMember);
	 * getSelectionTypes();
	 * getManagedBean().setScCommitteeMemberList(committeMemebersList); }
	 */

	public void addCommitteMember(String titleName) {
		debugLogger("addCommitteMember");
		getManagedBean().getUserQCSearchBean().setName(null);
		getManagedBean().getUserQCSearchBean().setIdentificationType(null);
		getManagedBean().getUserQCSearchBean().setIdentificationNo(null);
		getManagedBean().setExternalUsers(null);
		getManagedBean().setSelectedPmUser(null);
		this.getManagedBean().setCommitteMember(null);
		ScCommitteeMember committeeMember = new ScCommitteeMember();
		committeeMember.setPmUser(new PmUser());
		committeeMember.setUserSelection("E");
		this.getManagedBean().setCommitteMember(committeeMember);
		getSelectionTypes();
		// getManagedBean().setQtCommittee(qtCommittee);
		getManagedBean().setHeaderTitle(titleName);
		String commType = getManagedBean().getQtCommittee().getScCommittee().getCommitteeType().getParameterCode();
		if ("PBB".equalsIgnoreCase(commType)) {
			SelectItem[] pbbItems = getDropdownValues(ParameterTypeConst.REPRESENTATIVE_TYPE_PBB);
			this.getManagedBean().setAddRepresentativesTypeslist(Arrays.asList(pbbItems));
		}
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
		String userSelection = (String) event.getComponent().getAttributes().get("value");
		userSelection = this.getManagedBean().getCommitteMember().getUserSelection();
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
			getManagedBean().getCommitteMember().setMemberRole(new PmParameter());
			getManagedBean().getCommitteMember().setRepresentativeType(new PmParameter());
		}
	}

	public void deleteCommitteeAction() {
		debugLogger("deleteCommitteeAction");
		List<ScCommitteeMember> committeeMemberList = getManagedBean().getQtCommittee().getScCommittee().getScCommitteeMembers();
		List<ScCommitteeMember> selectedList = getManagedBean().getSelectedCommitteMemebers();

		if (committeeMemberList.size() == selectedList.size()) {
			committeeMemberList.clear();
		} else {
			for (ScCommitteeMember scCommitteeMember : selectedList) {
				committeeMemberList.remove(scCommitteeMember);
			}
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

	public void reset() {
		debugLogger("reset");
		this.getManagedBean().getUserQCSearchBean().setName(null);
		this.getManagedBean().getUserQCSearchBean().setIdentificationType(null);
		this.getManagedBean().getUserQCSearchBean().setIdentificationNo(null);
	}

	public void okCommitteeAction() {
		debugLogger("okCommitteeAction");
		UserReqDTO userReqDTO = new UserReqDTO();
		ScCommittee scCommittee = this.getManagedBean().getQtCommittee().getScCommittee();
		ScQt scQt = this.getManagedBean().getQt();
		List<ScCommitteeMember> memberList = getManagedBean().getQtCommittee().getScCommittee().getScCommitteeMembers();
		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}
		if (getManagedBean().getCommitteMember().getUserSelection().equals("N")) {
			ScCommitteeMember member = new ScCommitteeMember();
			member.setMemberName(getManagedBean().getCommitteMember().getMemberName());
			member.setIcPassport(getManagedBean().getCommitteMember().getIcPassport());
			member.setGrade(getManagedBean().getCommitteMember().getGrade());
			member.setDesignation(getManagedBean().getCommitteMember().getDesignation());
			member.setPNP(getManagedBean().getCommitteMember().isPNP());
			member.setEmail(getManagedBean().getCommitteMember().getEmail());
			member.setMemberRole(getManagedBean().getCommitteMember().getMemberRole());
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
			parameterReqDTO.setParameterType(ParameterTypeConst.IDENTIFICATION_TYPES);
			parameterReqDTO.setParameterCode(getManagedBean().getCommitteMember().getIdType());
			PmParameter identificationCode = getParameterSO().retrieveParameterByTypeAndCode(parameterReqDTO).getPmParameter();
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
			PmOrgProfile pmOrgProfile = getOrgProfileSO().retrieveOrgProfileListByOrgType(orgProfileReqDTO).getOrgProfileList().iterator().next();
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
			UserResDTO resDTO = getUserSO().saveUser(userReqDTO);
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
				if (null != user.getPmAddressTypes()) {
					for (PmAddressType address : user.getPmAddressTypes()) {
						if (null != address.getPmAddress()) {
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
					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
					reqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
					ParameterResDTO resDTO = getParameterSO().retrieveParameterByTypeAndCode(reqDTO);
					committeeMember.setMemberRole(resDTO.getPmParameter());
				} else {
					committeeMember.setMemberRole(user.getMemberRole());
				}
				committeeMember.setAccessRight("E");
				committeeMember.setScCommittee(scCommittee);
				memberList.add(committeeMember);
			}
			this.getManagedBean().setScCommitteeMemberList(memberList);
		}
		scCommittee.setScCommitteeMembers(memberList);
		this.getManagedBean().getQtCommittee().setScCommittee(scCommittee);
	}

	public void okQCCommitteeAction() {
		debugLogger("okCommitteeAction");
		UserReqDTO userReqDTO = new UserReqDTO();
		ScCommittee scCommittee = this.getManagedBean().getQtCommittee().getScCommittee();
		ScQt scQt = this.getManagedBean().getQt();
		List<ScCommitteeMember> memberList = getManagedBean().getQtCommittee().getScCommittee().getScCommitteeMembers();
		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}
		if (getManagedBean().getCommitteMember().getUserSelection().equals("N")) {
			ScCommitteeMember member = new ScCommitteeMember();
			member.setMemberName(getManagedBean().getCommitteMember().getMemberName());
			member.setIcPassport(getManagedBean().getCommitteMember().getIcPassport());
			member.setGrade(getManagedBean().getCommitteMember().getGrade());
			member.setDesignation(getManagedBean().getCommitteMember().getDesignation());
			member.setPNP(getManagedBean().getCommitteMember().isPNP());
			member.setEmail(getManagedBean().getCommitteMember().getEmail());
			member.setMemberRole(getManagedBean().getCommitteMember().getMemberRole());
			member.setRepresentativeType(getManagedBean().getCommitteMember().getRepresentativeType());
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
			parameterReqDTO.setParameterType(ParameterTypeConst.IDENTIFICATION_TYPES);
			parameterReqDTO.setParameterCode(getManagedBean().getCommitteMember().getIdType());
			PmParameter identificationCode = getParameterSO().retrieveParameterByTypeAndCode(parameterReqDTO).getPmParameter();
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
			PmOrgProfile pmOrgProfile = getOrgProfileSO().retrieveOrgProfileListByOrgType(orgProfileReqDTO).getOrgProfileList().iterator().next();
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
			UserResDTO resDTO = getUserSO().saveUser(userReqDTO);
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
				if (null != user.getPmAddressTypes()) {
					for (PmAddressType address : user.getPmAddressTypes()) {
						if (null != address.getPmAddress()) {
							committeeMember.setEmail(address.getPmAddress().getEmail());
							break;
						}
					}
				}
				committeeMember.setGrade(user.getGrade());
				committeeMember.setPNP(user.isPMP());
				committeeMember.setPmUser(user);
				committeeMember.setPmOrgProfile(scQt.getPmOrgProfile());
				if(committeeMember.isPNP() == false) {
					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
					reqDTO.setParameterCode(ParameterConst.COMMITTEE_ROLE_MEMBER);
					ParameterResDTO resDTO = getParameterSO().retrieveParameterByTypeAndCode(reqDTO);
					committeeMember.setMemberRole(resDTO.getPmParameter());
				}
				committeeMember.setAccessRight("E");
				committeeMember.setRepresentativeType(user.getRepresentativesType());
				committeeMember.setScCommittee(scCommittee);
				memberList.add(committeeMember);
			}
			this.getManagedBean().setScCommitteeMemberList(memberList);
		}
		scCommittee.setScCommitteeMembers(memberList);
		this.getManagedBean().getQtCommittee().setScCommittee(scCommittee);
	}
	
	/**
	 * User type lie gov or non gov users
	 */
	private void getUserTypes() {
		MemberType[] type = MemberType.values();
		List<SelectItem> userTypes = new ArrayList<SelectItem>();
		for (MemberType memberType : type) {
			userTypes.add(new SelectItem(memberType.value(), memberType.name()));
		}
		this.getManagedBean().setUserTypes(userTypes);
	}

	public void cloneCommitteMembersAcition(String titleName) {
		debugLogger("cloneCommitteMembersAcition");
		this.getManagedBean().setHeaderTitle(titleName);
		System.out.println("Title Name : " + this.getManagedBean().getHeaderTitle());
	}
	
	public void okCloneCommitteeMembersAction() {
		debugLogger("okCloneCommitteeMembersAction");
		ScCommittee scCommittee = this.getManagedBean().getQtCommittee().getScCommittee();
		List<ScCommitteeMember> memberList = getManagedBean().getQtCommittee().getScCommittee().getScCommitteeMembers();
		if (null == memberList) {
			memberList = new ArrayList<ScCommitteeMember>();
		}
		List<String> itemIds = new ArrayList<String>();
		List<ScCommitteeMember> members = getManagedBean().getSelectedCommitteMemebers();
		if (getManagedBean().getQtCommittee().getScCommittee().getScCommitteeMembers().isEmpty()) {
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
					memberList.add(member1);
				}
			}
			//memberList.addAll(getManagedBean().getSelectedCloneCommitteMemebers());
		} else {
			for (ScCommitteeMember committee : this.getManagedBean().getQtCommittee().getScCommittee().getScCommitteeMembers()) {
				itemIds.add(committee.getIcPassport());
			}
			for (ScCommitteeMember committees : getManagedBean().getSelectedCloneCommitteMemebers()) {
				if (itemIds.size() > 0) {
					if (itemIds.contains(committees.getIcPassport())) {

					} else {
						committees.setCommitteeMemberId(null);
						setUserDetails(committees);
						committees.setChangedDate(null);
						memberList.add(committees);
						this.getManagedBean().getQtCommittee().getScCommittee().setScCommitteeMembers(memberList);
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
	}

	public void retriveUserDetails(Long userid) {

		IUserSO iUserSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(getManagedBean().getCommitteMember().getPmUser()
				.getUserId());
		UserResDTO userResDTO = iUserSO.retrieveUserByUserId(reqDTO);
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

			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
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

			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
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

		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
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
		getManagedBean().setCommitteMembers(null);
	}

	public void onRowEditAction(RowEditEvent event) {
		ScCommitteeMember committeeMember = (ScCommitteeMember) event
				.getObject();
		getManagedBean().setCommitteMember(committeeMember);

	}

	public void onRowCancel(RowEditEvent event) {
		ScCommitteeMember member = (ScCommitteeMember) event.getObject();
		this.getManagedBean().getCommitteMembers().remove(member);
	}

	public void cancelCommitteAction() {
		this.getManagedBean().getCommitteMembers()
				.remove(this.getManagedBean().getCommitteMember());
	}

	/*public void goToMaintainceAction(ScQtCommittee qtCommitte) {
		ScQtCommittee committee = qtCommitte;
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		// committeReqDTO.setCommitteId(committee.getQtCommitteeId());
		// committeReqDTO.setQtCommittee(committee);
		ICommitteSO committeSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		
		 * CommitteResDTO resDTO = committeSO
		 * .retriveQTCommitteDetails(committeReqDTO);
		 
		// this.getManagedBean().setQtCommittee(resDTO.getCommittee());
		
		 * this.getManagedBean().setScCommittee(
		 * resDTO.getCommittee().getScCommittee());
		 
		
		 * this.getManagedBean().setCommittemembers(
		 * resDTO.getCommittee().getScCommittee().getScCommitteeMembers());
		 * this.getManagedBean().setApprovers(resDTO.getCommitteApprovers());
		 
		this.getManagedBean().setCommitteMember(new ScCommitteeMember());
		ConfigurableNavigationHandler handler = (ConfigurableNavigationHandler) FacesContext
				.getCurrentInstance().getApplication().getNavigationHandler();
		handler.performNavigation("committeemaintanancepage");

	}*/

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

		ICommitteSO committeSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);

		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.getScCommittee().setCommitteeId(new Long(committeDataType.getQtId()).longValue());
		/*
		 * committeReqDTO.setCommitteId(new Long(committeDataType.getComId())
		 * .longValue()); CommitteResDTO resDTO = committeSO
		 * .retriveQTCommitteDetails(committeReqDTO);
		 * 
		 * this.getManagedBean().setQtCommittee(resDTO.getCommittee());
		 * this.getManagedBean().setScCommittee(
		 * resDTO.getCommittee().getScCommittee());
		 * this.getManagedBean().setCommittemembers(
		 * resDTO.getCommittee().getScCommittee().getScCommitteeMembers());
		 * this.getManagedBean().setApprovers(resDTO.getCommitteApprovers());
		 */
		this.getManagedBean().setCommitteMember(new ScCommitteeMember());

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						"committeemaintanancepage" + "?faces-redirect=true");
	}

	public void resetSearchDetails() {
		this.getManagedBean().getQtnTenderSearchBean().setQtTitle(null);
		this.getManagedBean().getQtnTenderSearchBean().setQtNo(null);
		this.getManagedBean().getQtnTenderSearchBean().setFromCreatedDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setQtStatus(null);
		this.getManagedBean().getQtnTenderSearchBean().setCommitteeType(null);
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
}
